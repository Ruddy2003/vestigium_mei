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
 * El cuento de Pierre, cantero de la catedral de Amiens. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el
 * autor aprobó el 11 nov 2026.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioPiloto.kt`):
 * - Del 27 de mayo al 24 de agosto de la temporada 2 está «herido»: no talla ni sube. Sus variantes del trabajo, de la
 *   mañana y del sueño son otras.
 * - Al andamio solo sube de abril a julio (`meses`): en agosto de la temporada 2 vuelve con piedra blanda, desde
 *   septiembre de la temporada 1 la obra está parada y de noviembre a marzo los muros duermen tapados con paja.
 * - Colin entra de aprendiz el 2 de julio de la temporada 1; Agnès nace en la 2; Jehanne se casa en la 4; maese Thomas
 *   deja la obra a su hijo Renaud en la 4. Las variantes que los nombran llevan `desde` o `hasta`.
 * - Gautier se va con la hueste de abril a septiembre de la temporada 3: sus variantes solo salen de octubre a marzo.
 * - Las fiestas que vuelven cada año no nombran a Agnès ni a Jehanne, no lo ponen a tallar ni a subir (en la
 *   temporada 2 caen con el hombro roto) y no nombran a Gautier entre abril y septiembre.
 */
internal object PiezasPierre {
    /** El estado «herido» del calendario: desde que cede el andamio hasta que vuelve a tallar. */
    private const val HERIDO = "herido"

    /** De abril a julio se sube al andamio. */
    private val CON_ANDAMIO = (4..7).toSet()

    /** Los meses en que Gautier está en la logia todos los años: en la temporada 3 pasa el verano en la hueste. */
    private val CON_GAUTIER = setOf(1, 2, 3, 10, 11, 12)

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta antes que la campana de la obra, que es lo mismo que despertarse antes que el día. Se viste a oscuras, a tientas: las calzas, la saya, la capucha. Mahaut ya ha soplado el fuego y le pone delante un cuenco de gachas que quema."),
        v("La primera luz entra por las rendijas de los postigos, que en su casa no tienen vidrio: el vidrio es para la catedral. {nombre} se moja la cara en el cubo del patio y sale con el pan en la mano, masticando por el camino."),
        v("Lo despiertan los gallos de Saint-Leu, que cantan como si les pagaran por pieza. Desde la puerta se ve la catedral, o la parte que hay: una montaña de piedra clara, vestida de andamios o de paja según la estación. Cada mañana la mira, por costumbre, para ver si sigue ahí."),
        v("En la calle huele a río y a pan. Las panaderas ya han encendido el horno, y los tintoreros de la orilla echan al agua lo que les sobró ayer, que baja azul un buen trecho. {nombre} cruza el puente con otros que van a la obra, todos con la misma cara de sueño."),
        v("Antes de salir, {nombre} se santigua delante de la Virgen que tienen clavada junto a la puerta, una tabla pintada que compró en una feria. La Virgen tiene la cara un poco torcida, y en casa la quieren así. Fuera, el cielo empieza a clarear por el lado de Corbie."),
        v("El camino a la obra es corto y él lo hace largo: se para en la fuente, saluda al herrero, que ya está golpeando, y mira cómo amanece sobre los tejados de paja. La campana de la obra lo alcanza siempre en la misma esquina, y entonces aprieta el paso."),
        v("Colin duerme con la boca abierta, atravesado en el jergón de los chicos. {nombre} le tapa los pies al pasar, sin despertarlo. En la mesa hay pan de ayer y media cebolla; se lleva las dos cosas, y deja la puerta entornada para que no chirríe.", hasta = "colin_aprendiz"),
        v("Colin baja con él a la obra, medio dormido y orgulloso, con su delantal de cuero que todavía cruje. Por el camino le pregunta cosas del oficio que ya le preguntó ayer. {nombre} le contesta igual que ayer, porque así se aprende.", desde = "colin_aprendiz"),

        v("{nombre} se despierta con el hombro antes que con los ojos: lo tiene atado contra el pecho con un paño, y cada vez que se da la vuelta en el jergón se lo recuerda. Mahaut le ayuda a vestirse sin decir nada, que es su manera de no regañarlo.", HERIDO),
        v("Hoy tampoco sube a la obra a la hora de siempre. Oye la campana desde la cama, se levanta despacio y desayuna sentado, con una sola mano, como un señor. La otra la tiene quieta, como le mandó el barbero, y le pesa más que si trabajara.", HERIDO),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la obra el tiempo no es conversación: es jornal. Con helada no se asienta piedra, y con lluvia fuerte no se sube."),
        v("{nombre} mira el cielo como lo mira un cantero, pensando en la cal. {cielo} Si el mortero no fragua, lo de hoy hay que deshacerlo mañana, y eso no lo paga nadie."),
        v("{de_estacion} {cielo} Los viejos de la logia dicen que el tiempo ya no es el de antes, y lo dicen todos los años."),
        v("{cielo} En Picardía dicen que {estacion} es así y que quien no lo aguante, que se vaya a Provenza. {nombre} no ha estado nunca en Provenza, pero se la imagina con mucho sol y poca piedra buena."),
        v("Lo primero que hace al salir es mirar hacia la catedral: si se le ve la cabecera, no hay niebla. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En el río los barqueros dicen lo mismo con otras palabras, y se ríen de los que trabajan en seco."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un azul que los vidrieros quisieran para sí.",
            "Hace un día claro, y la piedra recién labrada brilla tanto que cansa los ojos.",
            "No hay una nube, y desde lo alto de la obra se ve hasta el bosque de la otra orilla.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, de un gris que no promete nada.",
            "Hay nubes bajas que pasan rozando los andamios.",
            "Está nublado, y la piedra tiene el color de la ceniza.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia menuda de las que no paran en todo el día.",
            "Cae agua desde la madrugada, y las calles son de barro hasta el tobillo.",
            "Llueve, y por las gárgolas que ya están puestas sale el agua a chorros, como debe ser.",
        ),
        Clima.NIEVE to listOf(
            "Ha nevado, y los muros a medio hacer tienen un sombrero blanco.",
            "Cae una nieve fina, y en la obra no se mueve nadie.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla del río, tan espesa que la catedral desaparece a veinte pasos.",
            "La niebla del Somme lo tapa todo, y las campanas suenan como si vinieran de debajo del agua.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del oeste, el que viene del mar y silba entre los andamios.",
            "Hay viento, y en lo alto de la obra hay que agarrarse con una mano y trabajar con la otra.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: truena sobre los campos y nadie sube a los andamios.",
            "El cielo se ha puesto negro, y el trueno hace temblar las cuerdas de la grúa.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, el tiempo de destapar los muros y volver a subir.",
            "En primavera la obra se llena de gente nueva, que llega de otras ciudades buscando jornal.",
            "Es primavera, y los campos de waide empiezan a verdear fuera de las murallas.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, cuando se trabaja de sol a sol y el sol no tiene prisa.",
            "En verano el polvo de la piedra se pega al sudor, y los canteros parecen de harina.",
            "Es pleno verano, y en las eras ya huele a paja.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño: los días se acortan, y el jornal con ellos.",
            "En otoño se empieza a juntar paja para tapar los muros antes de las heladas.",
            "El otoño trae lluvia, nueces y las primeras nieblas del río.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando la obra duerme tapada con paja y los canteros tallan bajo techo.",
            "En invierno se trabaja ocho horas y se cobra menos, y nadie sabe si la culpa es del maestro o del sol.",
            "Es invierno, y anochece cuando uno apenas ha empezado a entrar en calor.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora talla. Tiene delante un bloque de piedra blanca y la plantilla de madera que le dieron en la sala de trazas, y va quitando lo que sobra, golpe a golpe. La piedra de aquí es blanda al salir de la cantera y se endurece con el aire, así que conviene no distraerse.", hacer = Hacer.TRABAJO),
        v("A esta hora está en la logia, la cabaña de tablas pegada a la obra donde los canteros trabajan a cubierto. Se oye un golpeteo de mazos que no van a compás, y el polvo flota en la luz como harina. Cada uno tiene su banco, y nadie se sienta en el de otro.", hacer = Hacer.TRABAJO),
        v("A esta hora afila. Un cantero pasa casi tanto tiempo con la piedra de amolar como con la de tallar: un cincel romo estropea más de lo que hace. El herrero de la obra le cobra los arreglos grandes; los pequeños los hace él, escupiendo en la piedra.", hacer = Hacer.TRABAJO),
        v("A esta hora marca. Cada piedra que termina lleva su signo, una flecha cruzada por dos rayas, para que el maestro sepa de quién es y se la pague. Hay piedras que nadie verá nunca, enterradas en el muro, y él las marca igual de bien.", hacer = Hacer.TRABAJO),
        v("A esta hora está en el andamio, a la altura de los pájaros. Ayuda a asentar una dovela que sube colgada de la grúa, la gran rueda que mueven dos hombres caminando por dentro como ardillas. Abajo, la gente es del tamaño de un pulgar, y él procura no mirarla.", hacer = Hacer.TRABAJO, meses = CON_ANDAMIO),
        v("A esta hora sube piedra. Las cuerdas crujen, la rueda chirría, y el bloque se balancea en el aire hasta que cuatro manos lo agarran y lo llevan a su sitio. Ahí se le pone el mortero, se golpea con el mazo de madera y se mira con la plomada. Si no está a plomo, se vuelve a empezar.", hacer = Hacer.TRABAJO, meses = CON_ANDAMIO),
        v("A esta hora trabaja en el dintel de Enguerrand, el de la waide, en un rincón del patio de su casa. Le ha pedido unas hojas y un escudo con tres bolas, que son los panes de waide de los que vive. {nombre} talla despacio: es el único jornal entero que tiene este otoño.", hacer = Hacer.TRABAJO, desde = "encargo_dintel", hasta = "entrega_dintel"),
        v("A esta hora pasa por la logia maese Thomas, el maestro de la obra, con el compás en la mano y los planos en la cabeza. Mira las piezas de cada uno sin decir nada, y a veces pasa el dedo por una arista. Si se detiene, malo; si sigue de largo, bueno.", hacer = Hacer.TRABAJO, hasta = "maese_renaud"),
        v("A esta hora pasa por la logia maese Renaud, que es el maestro ahora que su padre se ha retirado. Es más joven que la mitad de los canteros y lo sabe: habla poco y mira mucho. {nombre} lo conoció cuando todavía le cargaba la escuadra a su padre.", hacer = Hacer.TRABAJO, desde = "maese_renaud"),

        v("A esta hora no talla: tiene el hombro atado y el brazo quieto. Pasa el día en la logia, en un banco, separando los cinceles buenos de los que hay que llevar al herrero. Es trabajo de aprendiz, y lo sabe. Lo hace con mucho cuidado, para que dure.", HERIDO, Hacer.TRABAJO),
        v("A esta hora mira trabajar a los demás, que es el peor de los trabajos. Desde abajo ve subir las piedras y adivina dónde van antes de que lleguen. Cuando alguien se equivoca, se muerde la lengua. A veces no se la muerde.", HERIDO, Hacer.TRABAJO),
        v("A esta hora está en casa, con el brazo en cabestrillo, ayudando a Mahaut en lo que se puede hacer con una mano: desgranar, enhebrar, espantar las gallinas. Ella dice que nunca la había ayudado tanto. Él no sabe si es un elogio.", HERIDO, Hacer.TRABAJO),

        v("A esta hora come: pan moreno, un trozo de queso y una cebolla cruda que se pela con los dientes. Se bebe cerveza floja, que da más sed que la que quita. {nombre} come como comen los canteros, deprisa y en silencio, porque la boca está ocupada y la cabeza también.", hacer = Hacer.COMER),
        v("A esta hora come gachas de avena con un poco de tocino, que es lo que hay casi todos los días que no son de vigilia. Rebaña el cuenco con el pan hasta dejarlo limpio. Mahaut dice que así no hay que fregarlo, y lo dice como si el mérito fuera suyo.", hacer = Hacer.COMER),
        v("A esta hora come lo que lleva en el hatillo: pan, un puñado de nueces, un trozo de arenque que pica. Parte del jornal se paga en vino, y el de esta semana está agrio. Se lo bebe igual y dice que ha probado peores, lo cual es verdad.", hacer = Hacer.COMER),
        v("A esta hora come con Colin, que ya come como un aprendiz: el doble que en casa y la mitad de despacio. El chico le enseña una ampolla de la mano como quien enseña una medalla. {nombre} le enseña las suyas, que son callos, y el chico se calla.", hacer = Hacer.COMER, desde = "colin_aprendiz"),

        v("A esta hora está en la taberna de la plaza del mercado, la de la rama de enebro colgada en la puerta. Los canteros beben en un rincón y los carpinteros en otro, y de rincón a rincón se insultan con cariño. {nombre} paga una ronda cuando le toca y ni una antes.", hacer = Hacer.GENTE),
        v("A esta hora está sentado en el poyo de la puerta con los vecinos, mientras queda luz. Se habla de quién se casa, de quién debe y de lo que dijo el cura el domingo. Los chicos juegan a la pelota en el barro, y nadie los riñe hasta que rompen algo.", hacer = Hacer.GENTE),
        v("A esta hora está en la cofradía, que se reúne en una sala detrás de la iglesia de Saint-Firmin. Se reza un poco, se bebe otro poco y se habla de quién está enfermo: la cofradía paga el entierro de los que no pueden. {nombre} pone su moneda en la caja y espera no necesitarla.", hacer = Hacer.GENTE),
        v("A esta hora se sienta junto al fuego y arregla lo que se rompió durante el día: el mango de un mazo, la suela de un zapato, la pata de un taburete. Mahaut hila a su lado. No hablan mucho; con el fuego encendido, no hace falta.", hacer = Hacer.GENTE),
        v("A esta hora juega a los dados con Gautier y otros dos de la logia, detrás de la casa del deán, donde no los ve el deán. Se apuesta poco, porque hay poco. Gautier pierde siempre y siempre dice que los dados son de otro, aunque sean suyos.", hacer = Hacer.GENTE, meses = CON_GAUTIER),

        v("A esta hora duerme. Los canteros duermen pronto y hondo: la piedra se cobra el día entero y no deja nada para la noche. Ni las campanas de completas lo despiertan, y eso que la catedral está a cuatro calles.", hacer = Hacer.DORMIR),
        v("A esta hora duerme de lado, con las manos metidas entre las rodillas, que es como se le quedan después del cincel. Mahaut dice que hasta dormido da golpecitos, y que un día le va a tallar el jergón.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y la casa entera duerme con él, apretada en una sola cama grande bajo una manta de lana: así se pasa menos frío. Lo último que se oye en la calle es la ronda, que pasa con su farol y su lanza, despacio.", hacer = Hacer.DORMIR),

        v("A esta hora duerme, o lo intenta: boca arriba, que de lado el hombro no lo deja. Cada vez que se mueve se despierta, y cada vez que se despierta oye a Mahaut, que también se despierta, aunque diga que no.", HERIDO, Hacer.DORMIR),
        v("A esta hora duerme mal. Sueña que sube al andamio y que el andamio no se acaba nunca. Se despierta sudando y se queda mirando el techo de paja, contando las vigas, hasta que vuelve a dormirse sin darse cuenta.", HERIDO, Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("Amiens está hecha de agua: el Somme se parte en canales, y los canales pasan por detrás de las casas, que tienen la puerta de atrás sobre el río. En Saint-Leu se va a ver al vecino en barca. {entorno}"),
        v("{entorno} En el mercado lo que más se vende es la waide, en bolas secas del tamaño de un puño. De ella sale el azul, y del azul, la ciudad. Los que la venden tienen casa de piedra; los demás, de barro."),
        v("La catedral es tan grande que la ciudad, a su lado, parece que se ha quedado a vivir en sus faldas. Dicen que cabrán dentro todos los vecinos de Amiens, y aún sobrará sitio. {entorno}"),
        v("Fuera de las murallas empiezan los huertos del agua: parcelas entre canales donde se cultivan puerros, coles y nabos, y adonde se llega en barca chata. Los hortelanos venden de barca a barca, sin pisar tierra. {entorno}"),
        v("{entorno} En la calle de los tintoreros el agua sale azul de las tinas y huele a orines, que es como se fija el tinte. Los tintoreros tienen los brazos azules hasta el codo, y en misa se les reconoce de lejos."),
        v("Las campanas de Amiens dicen la hora a todos: la de la obra, las de las parroquias, la del campanario de la comuna, que es de los vecinos y no de Dios. Cuando suenan todas juntas, o es fiesta o es fuego. {entorno}"),
    )

    /** Lo que se ve alrededor según cómo va la obra, el campo y el río. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("agua", 0.5, frases = listOf(
            "Las calles bajas todavía están con agua, y la gente pasa por tablones puestos sobre cubos.",
            "El río ha dejado barro en las paredes de Saint-Leu, a la altura de la rodilla.",
        )),
        Entorno("guede", 0.7, frases = listOf(
            "En los campos de fuera la waide está alta, verde y amarga.",
            "Desde las murallas se ven los campos de waide, de un verde que no hace pensar en el azul.",
        )),
        Entorno("andamios", 0.0, 0.3, frases = listOf(
            "Los muros del coro, a medio subir, esperan tapados con paja y estiércol, como un enfermo con mantas.",
            "En la obra apenas hay nadie: unos pocos tallan en la logia, y los muros están tapados con paja.",
        )),
        Entorno("andamios", 0.9, frases = listOf(
            "En la obra hay andamios en todos los muros, y en los andamios, gente como hormigas en un tronco.",
            "Desde la plaza se oye la obra entera: mazos, poleas, gritos de un andamio a otro.",
        )),
        Entorno("andamios", 0.3, 0.9, frases = listOf(
            "En la obra hay andamios en una parte de la cabecera y en la otra no: el maestro dice que se va por partes.",
            "La obra trabaja a medias: se oyen mazos, pero todavía no todos.",
        )),
    )

    private val GENTE = listOf(
        v("Mahaut lleva la casa y las cuentas, y sabe cuánto cuesta cada cosa en el mercado y cuánto costaba el año pasado. Cuando {nombre} llega con la paga, ella la cuenta delante de él, moneda a moneda, y le devuelve una para la taberna. Una sola."),
        v("Los vecinos de al lado son una familia de tintoreros, y tienen las manos azules todo el año. Su perro también es un poco azul, de tanto meterse en las tinas. Se prestan sal, fuego y noticias, y cuando hay pelea se oye todo, porque la pared es de barro y paja."),
        v("En la logia cada uno tiene su marca y su manera, y {nombre} reconoce el golpe de cada compañero sin mirar. Hay uno de Laon que canta, uno de Reims que se queja y uno de aquí que no dice nada en todo el día y talla mejor que todos."),
        v("El cura de la parroquia es primo de Mahaut, y se nota: en casa se reza más de lo necesario y el diezmo se paga puntual. A cambio, el cura les guarda sitio en la misa del domingo, cerca del brasero, que en invierno vale más que una indulgencia."),
        v("La panadera de la esquina le fía el pan cuando se acaba el mes, y apunta lo que le debe con muescas en un palo partido: una mitad la guarda ella y la otra él. Cuando paga, juntan las dos mitades y las muescas tienen que coincidir. Nunca han discutido."),
        v("Colin quiere ser cantero como su padre, y se pasa las tardes tallando trozos de yeso con un clavo. Le salen caras, perros, una catedral entera del tamaño de un pan. {nombre} le dice que están torcidas, y luego las guarda en el arcón.", hasta = "colin_aprendiz"),
        v("Colin ya es aprendiz en la logia, y en casa no habla de otra cosa: de los cinceles, de las marcas, de un cantero de Reims que dice palabrotas en latín. Mahaut lo escucha como si entendiera. {nombre} lo escucha y se acuerda de cuando él decía lo mismo.", desde = "colin_aprendiz"),
        v("Jehanne ya teje mejor que su madre, aunque nadie se lo diga a su madre. Las vecinas empiezan a preguntar por ella con una sonrisa que {nombre} conoce, y él hace como que no entiende la pregunta. Lo hace tan mal que todos entienden.", hasta = "boda_jehanne"),
        v("Agnès, la pequeña, es la que manda ahora en la casa: todos bajan la voz cuando duerme y todos se callan cuando habla. {nombre} la mira al volver de la obra y le parece el trabajo mejor hecho de la familia.", desde = "nace_agnes"),
        v("Gautier, el del mortero, es su amigo desde que llegó a la obra. Tiene los brazos blancos de cal hasta el codo y una risa que se oye desde los andamios. En los meses fríos, cuando hay menos trabajo, se sientan juntos en la logia a contarse mentiras de otras obras.", meses = CON_GAUTIER),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del oficio: con las manos antes que con la cabeza."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En la logia se dice que un cantero vale lo que sus herramientas; lo dicen los que las tienen malas."),
        v("{dato_objeto} Su padre no sabía nada de piedra, pero le enseñó a cuidar las cosas, y él se lo enseña a Colin, que no escucha."),
        v("Un cantero tiene pocas cosas, y cada una tiene su historia. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("mazo", "el mazo", "un mazo", "El mazo",
            "Su mazo es de madera de fresno, con la cabeza gastada de un lado más que del otro, porque él golpea siempre un poco torcido; un mazo nuevo tarda un año en aprender la mano de su dueño."),
        ObjetoCuento("cincel", "el cincel dentado", "un cincel dentado", "El cincel",
            "El cincel dentado tiene la boca como un peine y deja en la piedra unas rayas finas, todas iguales; los canteros viejos tallaban con hacha, y los jóvenes dicen que por eso sus piedras parecen mordidas."),
        ObjetoCuento("escuadra", "la escuadra", "una escuadra", "La escuadra",
            "La escuadra es de hierro y la hizo el herrero de la obra a su medida; con ella se comprueba cada esquina, y una piedra que no cuadra no sube, aunque la haya tallado el maestro."),
        ObjetoCuento("compas", "el compás", "un compás", "El compás",
            "El compás de la logia es de hierro y largo como un brazo; con él se trazan sobre un suelo de yeso los arcos a su tamaño de verdad, y en ese suelo, lleno de rayas, solo los maestros entienden algo."),
        ObjetoCuento("plomada", "la plomada", "una plomada", "La plomada",
            "La plomada es una bola de plomo colgada de un cordel; no se equivoca nunca, y por eso nadie discute con ella, ni siquiera el maestro cuando un muro le ha salido inclinado."),
        ObjetoCuento("marca", "la marca de cantero", "una marca de cantero", "La marca",
            "Su marca es una flecha cruzada por dos rayas, y la pone en cada piedra que talla para que se la paguen; no la heredó de nadie, la eligió él, y la tiene grabada también en el mango del mazo."),
        ObjetoCuento("cuerda", "la cuerda de nudos", "una cuerda de nudos", "La cuerda de nudos",
            "La cuerda de nudos tiene trece nudos a la misma distancia; con ella se miden los largos y se saca un ángulo recto sin escuadra, tendiendo un triángulo de tres, cuatro y cinco tramos."),
        ObjetoCuento("jarra", "la jarra de cerveza", "una jarra", "La jarra",
            "La jarra de la casa es de barro vidriado de verde, con una mella en el pico; la cerveza que se sirve en ella es de cebada y avena, espesa, y se bebe pronto, antes de que se agríe."),
        ObjetoCuento("pan", "el pan", "un pan", "El pan",
            "El pan de todos los días es de centeno y cebada, oscuro y pesado; el blanco, de trigo, es para los canónigos y, en las fiestas, para los demás, que por un día se sienten canónigos."),
        ObjetoCuento("plantilla", "la plantilla", "una plantilla de madera", "La plantilla",
            "La plantilla es una tabla fina recortada con el perfil de la moldura; la da el maestro, se apoya en la piedra y se sigue con la punta, y así cien canteros que no se conocen tallan la misma pieza."),
        ObjetoCuento("capucha", "la capucha", "una capucha", "La capucha",
            "La capucha es de lana basta y le cubre la cabeza y los hombros; con el polvo de la piedra se vuelve blanca, y Mahaut la sacude cada noche en la puerta, que es la única manera de saber de qué color era."),
        ObjetoCuento("vela", "la vela de sebo", "una vela de sebo", "La vela",
            "La vela de la casa es de sebo, humea y huele a carnero; las de cera son para la iglesia, y en la Candelaria cada familia lleva la suya a bendecir y la guarda para las tormentas."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} no verá la catedral terminada, y lo sabe. Ni su hijo, seguramente. Le da igual: las piedras que talla van a estar ahí cuando nadie se acuerde de su nombre, y su marca, que no lee nadie, también."),
        v("Piensa a veces en lo raro de su oficio: pasarse la vida quitándole cosas a una piedra hasta que se parece a lo que alguien dibujó. Si lo piensa mucho le parece un trabajo de escultor, y entonces se acuerda de lo que cobra y se le pasa."),
        v("{mundo} Él piensa en cosas más cercanas: en el techo de paja, que gotea; en si Mahaut tendrá bastante leña; en la piedra de mañana, que tiene una veta mala. Las cosas grandes, se dice, son para los canónigos."),
        v("{mundo} A sus {edad} años, {nombre} ha trabajado en tres obras y ha visto caer a dos hombres de un andamio. No habla de eso. Cuando sube, no mira abajo, y cuando baja, da gracias a la Virgen sin mover los labios."),
        v("No tiene tierra ni la quiere: la tierra ata, y un cantero va donde hay obra. Tiene un mazo, una marca y un sitio en la logia, y hay días en que eso le parece mucho. Su padre era labrador, y a su padre le parecía poco."),
        v("{mundo} Un cantero no decide qué se construye, pero sin él no se construye nada. {nombre} le da vueltas a eso los domingos, en misa, mirando las columnas. No se lo ha dicho a nadie: en una iglesia, pensar en el orgullo ya es pecado."),
    )

    private val MUNDOS = mapOf(
        "escasez" to listOf(
            "En la logia se dice que la fábrica anda corta de dinero.",
            "Dicen que la catedral come más que un ejército.",
            "Se dice que las limosnas ya no llegan como antes.",
        ),
        "miedo" to listOf(
            "Corren rumores de cosas malas en tierras lejanas.",
            "La gente anda inquieta, y el cura confiesa más que nunca.",
        ),
        "guerra" to listOf(
            "Se habla del rey, de los ingleses y de quién irá a la hueste.",
            "Cuando pasa un mensajero por la plaza, todos se paran a mirarlo.",
        ),
        "tregua" to listOf(
            "Las cosas parecen ir más tranquilas este año.",
            "En la plaza se habla menos de guerras y más de bodas.",
        ),
        "cruzada" to listOf(
            "En las iglesias se predica sobre Tierra Santa.",
            "Se habla mucho de Jerusalén, que casi nadie sabe dónde está.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando oscurece, Mahaut enciende la vela de sebo y la casa se llena de un humo que huele a carnero. Se cena sopa de coles con el pan dentro, y se apaga pronto: el sebo cuesta, y mañana la campana de la obra no espera a nadie."),
        v("Al caer la tarde la obra se queda en silencio, y los andamios parecen un esqueleto contra el cielo. {nombre} vuelve a casa por la calle de los tintoreros, despacio, con el día entero en las piernas. En la puerta lo recibe el olor de la sopa, que reconoce desde la esquina."),
        v("Anochece temprano sobre Amiens, y las campanas tocan a completas una tras otra, como si se pasaran un recado. {nombre} se sienta en el umbral a mirar cómo se encienden las pocas luces de la calle. Pasa un vecino, se sienta, y entre los dos no dicen diez palabras."),
        v("De noche se cierran las puertas de la ciudad y Amiens se queda a solas con su río. {nombre} se acuesta con el cuerpo molido y las manos calientes. Lo último que oye es el agua, que pasa por detrás de la casa de al lado y no se calla nunca."),
        v("Se cena en silencio, porque la boca está ocupada. Después Mahaut cuenta lo que ha pasado en el mercado y {nombre} lo que ha pasado en la obra, y los dos fingen interesarse. Luego se cubre el fuego con ceniza para que amanezca vivo, y se reza un padrenuestro, corto."),
        v("La última luz se queda un rato en lo más alto de la catedral, en la piedra nueva, cuando la calle ya está a oscuras. {nombre} la mira desde la puerta. Algo de esa piedra es suyo, y a esa hora le parece que se nota."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "candelaria" to unDia(
            Parrafo.MANANA to "Hoy es la Candelaria, cuarenta días después de Navidad. Cada familia lleva una vela a la iglesia para que la bendigan, y vuelve con ella encendida, tapándola con la mano para que no se apague. Si llega encendida a casa, dicen, el año será bueno.",
            Parrafo.GENTE to "Mahaut guarda la vela bendita en el arcón, envuelta en un trapo, para encenderla cuando haya tormenta o cuando alguien se esté muriendo. Hoy se hacen tortas finas en la sartén, y Colin se quema los dedos queriendo darle la vuelta a la primera.",
            Parrafo.ANOCHECER to "En la logia se dice que después de la Candelaria la obra ya no tarda: se alargan los días, se destapan los muros y vuelven los que se fueron a pasar el invierno a otra parte. {nombre} echa la cuenta de lo que debe y de lo que va a ganar, y le sale justo, como siempre.",
        ),
        "carnaval" to unDia(
            Parrafo.MANANA to "Hoy es martes de carnaval, el último día de carne antes de la cuaresma. Desde temprano huele a tocino frito en toda la calle, porque lo que no se coma hoy hay que guardarlo cuarenta días. Los chicos salen con la cara pintada de hollín a asustar a las viejas.",
            Parrafo.TRABAJO to "A esta hora en la logia nadie trabaja como debe: alguien ha traído una morcilla, otro un jarro de vino, y hasta el aparejador hace como que no ve. Un cantero joven se ha puesto la capucha del revés y hace de obispo, bendiciendo piedras con un cepillo.",
            Parrafo.ANOCHECER to "Por la noche se baila en la plaza alrededor de una hoguera, y se come como si mañana no hubiera comida, que es casi verdad. {nombre} se acuesta con la barriga llena y la conciencia regular. Mañana es miércoles de ceniza, y el cura no perdona.",
        ),
        "pascua" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es Pascua. Las campanas, que llevaban calladas desde el jueves santo, vuelven a sonar todas a la vez, y en la ciudad no queda nadie dormido. {nombre} se pone la saya buena, la que solo sale tres veces al año, y Mahaut le peina el pelo con agua.",
                Parrafo.PUEBLO to "La catedral está llena hasta la puerta, y la gente que no cabe oye la misa desde la plaza, sin enterarse de nada y contenta igual. En la obra no se mueve una piedra: hoy los andamios están vacíos y la grúa, quieta, parece que también descansa.",
                Parrafo.ANOCHECER to "Después de cuarenta días sin carne se come cordero, huevos y pan blanco, y a media tarde ya a nadie le cabe nada más. Los chicos se pelean por los huevos teñidos con piel de cebolla. {nombre} se queda dormido sentado, con la jarra en la mano y una sonrisa de canónigo.",
            ),
            dia(
                Parrafo.GENTE to "Segundo día de Pascua, y tampoco se trabaja. Se va de visita: a la hermana de Mahaut, a los vecinos, al cura, y en cada casa hay que comer algo para no ofender. {nombre} vuelve más lleno que ayer, jurando que no prueba otro huevo hasta el año que viene.",
                Parrafo.PUEBLO to "En el prado de fuera de la muralla los jóvenes juegan a la pelota, una parroquia contra otra, sin más regla que no matar a nadie. Siempre hay alguno que vuelve cojeando y otro que vuelve sin un diente, y todos dicen que fue el mejor partido que recuerdan.",
            ),
        )),
        "rogativas" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empiezan las rogativas: tres días de procesión antes de la Ascensión, para pedir que el año dé trigo y no granizo. La procesión sale de la catedral al amanecer, con las cruces por delante y los estandartes de las cofradías detrás. Se ayuna hasta la tarde.",
                Parrafo.PUEBLO to "La procesión sale por la puerta de la ciudad y va por los caminos entre los campos, parándose en cada cruz. El cura bendice la tierra con agua, y los labradores miran el trigo, que ya está verde, como si lo vieran por primera vez.",
                Parrafo.PENSAMIENTO to "{nombre} camina detrás de la cruz de su parroquia, con los demás canteros. Él no tiene campos, pero el pan sale de ellos y el jornal también, así que reza por el trigo como el que más. De paso reza por el andamio, que nunca está de más.",
            ),
            dia(
                Parrafo.GENTE to "Segundo día de rogativas. Hoy la procesión va hacia el río, y los barqueros la esperan con las barcas adornadas de ramas. Mahaut va descalza, por una promesa que no le ha contado. {nombre} no pregunta: con las promesas de las mujeres, mejor no saber.",
                Parrafo.ANOCHECER to "Por la tarde, al romper el ayuno, se come en la calle lo que cada uno trae, y se comparte. Hay pan, cebollas, queso y algún vino escondido que todos ven. Los niños corren entre las mesas como si fuera fiesta, que en el fondo lo es.",
            ),
            dia(
                Parrafo.PUEBLO to "Último día de rogativas. La procesión rodea la ciudad por fuera de la muralla, y en cada puerta se canta una letanía que nadie entiende del todo, porque es en latín. Los que van delante cantan bien; los de atrás, lo que les llega.",
                Parrafo.ANOCHECER to "Al volver, los pies le duelen más que después de un día de andamio. {nombre} los mete en un balde de agua fría, y Mahaut se ríe de él. Mañana es la Ascensión, y después se vuelve a la obra, con los campos bendecidos y el granizo, si Dios quiere, lejos.",
            ),
        )),
        "guede" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la cogida de la waide. Antes del alba salen de la ciudad carros vacíos y cuadrillas de mujeres y muchachos con cuchillos cortos, porque las hojas se cortan a mano, una por una. Los mercaderes van detrás a caballo, mirando el cielo.",
                Parrafo.PUEBLO to "En los campos de fuera la waide está alta y amarga. Las hojas se cortan, se lavan en el río y se llevan a los molinos, donde unas ruedas de piedra las aplastan hasta hacerlas pasta. Toda la orilla huele a col podrida, que es como huele el dinero en Amiens.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que media catedral está hecha con esta hierba fea. Los mercaderes de waide pagan ventanas, altares y capillas, y quieren que se sepa. Él no tiene nada contra eso: el dinero de una col podrida, puesto en piedra, ya no huele a nada.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días los molinos de waide no paran, y las ruedas de piedra giran sobre la pasta verde de la mañana a la noche. Con la pasta se hacen bolas del tamaño de un puño, que se ponen a secar en zarzos. Desde la calle parecen ejércitos de panes negros.",
                "Por el camino entran los carros de hojas, tan cargados que dejan un rastro verde. Los niños de la calle los siguen y se pelean por las hojas que caen, que no sirven para nada. Lo que importa es ganar.",
                "En la plaza los mercaderes ya discuten el precio de las bolas, que no estarán secas hasta dentro de semanas. Unos dicen que el año viene bueno y otros que viene malo, según quieran comprar o vender. El precio, al final, lo pone el que tiene más paciencia.",
            )),
        ),
        "san_juan" to unDia(
            Parrafo.MANANA to "Hoy es San Juan, el día más largo del año, y en Amiens es fiesta doble: la catedral guarda la cabeza del Bautista, traída de Constantinopla. Desde temprano llegan peregrinos de toda Picardía a verla, con su bastón y su cara de haber andado mucho.",
            Parrafo.PUEBLO to "La cabeza está en un relicario de oro, y los peregrinos pasan delante en fila, tocando lo que pueden. Fuera, en la plaza, se venden velas, medallas de plomo con la cara del santo y empanadas, y no siempre se sabe cuál de las tres cosas deja más ganancia.",
            Parrafo.ANOCHECER to "Al caer la tarde, que hoy cae tardísimo, se encienden las hogueras de San Juan en las plazas y en los campos. Los jóvenes saltan por encima del fuego para tener suerte, y los viejos los miran pensando en lo que se va a quemar. Las brasas se llevan a casa en una teja, contra el rayo.",
        ),
        "siega" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Ha empezado la siega del trigo. Antes del alba los segadores salen por las puertas con la hoz al hombro, y la ciudad se queda medio vacía: hasta el barbero tiene primos en el campo. En la obra se nota, porque este mes faltan brazos y nadie los pide.",
                Parrafo.PUEBLO to "Desde lo alto de la muralla se ven los campos dorados hasta el bosque, y en ellos los segadores, en fila, avanzando como un ejército que no tiene prisa. Detrás van las mujeres atando gavillas y, detrás de ellas, los pobres, que tienen derecho a lo que quede en el suelo.",
                Parrafo.ANOCHECER to "Al anochecer los carros de gavillas entran por la puerta, tan cargados que el carretero va sentado encima. {nombre} se aparta para dejarlos pasar y se le queda una brizna de paja en la capucha. Mahaut se la quita en casa y la guarda: dice que trae suerte.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días todas las eras de fuera de la muralla están llenas: se trilla a golpe de mayal, y el ruido llega hasta la ciudad como un tambor torpe. El polvo de la paja se mete en todas partes, hasta en el pan.",
                "En el mercado ya hay trigo nuevo, y el precio del pan baja un poco, que es la mejor noticia del año. La panadera de la esquina dice que no baja, que es una impresión. Lo dice todos los años.",
                "Los diezmeros del cabildo andan por los campos contando gavillas: una de cada diez es para la Iglesia, y con eso, en parte, se pagan las piedras de la catedral. Los labradores cuentan también, por si acaso, y casi nunca les sale la misma cuenta.",
            )),
        ),
        "asuncion" to unDia(
            Parrafo.MANANA to "Hoy es la Asunción, la fiesta de Nuestra Señora, que es la dueña de la catedral y, según el obispo, de la ciudad entera. No se trabaja. A media mañana sale la procesión con la imagen de la Virgen en andas, vestida de azul, del azul de la waide.",
            Parrafo.PUEBLO to "La procesión da la vuelta a la obra entre andamios y montones de piedra, y el obispo bendice los muros a medio hacer, que es una forma de pedirle a la Virgen que ayude a pagarlos. Los canteros miran desde un lado, con la capucha bajada y el ojo en las juntas.",
            Parrafo.GENTE to "Mahaut ha ido a misa con su mejor toca, y Colin con la cara lavada a la fuerza. En la puerta de la catedral se reparte pan a los pobres, y hay quien hace dos veces la cola. {nombre} los ve y no dice nada: en la fiesta de la Virgen, contar panes es de mal cristiano.",
        ),
        "san_firmin" to unDia(
            Parrafo.MANANA to "Hoy es San Fermín, el primer obispo de Amiens, al que le cortaron la cabeza los romanos. Es el patrón de la ciudad, y desde temprano las calles están adornadas con ramas y con paños colgados de las ventanas. Las campanas no se callan en toda la mañana.",
            Parrafo.PUEBLO to "Sacan el arca de plata con los huesos del santo, a hombros de los canónigos, y la pasean por las calles. Dicen que cuando pasa el arca los enfermos sanan y los sanos no enferman. Hay quien la sigue de rodillas un buen trecho, y quien la sigue para ver quién la sigue de rodillas.",
            Parrafo.PENSAMIENTO to "{nombre} se quita la capucha al pasar el arca y piensa en el santo, que vino de lejos a una ciudad que no era la suya, como los canteros. Le pide jornal para el invierno, que siempre viene corto. Luego le da un poco de vergüenza pedirle a un mártir algo tan pequeño.",
        ),
        "todos_santos" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es Todos los Santos. La catedral tiene encendidas todas sus velas, y las campanas tocan desde la víspera con un toque lento que se mete en los huesos. No se trabaja, y nadie lo celebra con alegría: es fiesta, pero de las serias.",
                Parrafo.PUEBLO to "Esta noche las campanas tocan por los muertos, toda la noche, y los campaneros se turnan con vino caliente para aguantar. En las casas se deja algo de comida en la mesa, por si alguno de la familia vuelve a ver cómo están. Nadie lo dice en voz alta, pero nadie la recoge.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en los dos hombres que vio caer de un andamio, en otra obra, hace años. No sabe si alguien reza por ellos, así que reza él, por si acaso, y les pone la cara que recuerda. Una de las dos ya se le ha borrado.",
            ),
            dia(
                Parrafo.GENTE to "Hoy es el día de los difuntos. Mahaut y {nombre} van al cementerio de la parroquia con los chicos y dejan una vela sobre la tumba de los padres de ella. Los de él están enterrados lejos, en su pueblo, y él los recuerda en la iglesia, que le queda más cerca.",
                Parrafo.ANOCHECER to "Por la tarde se da limosna a los pobres que pasan por las casas pidiendo por las almas: un pan, una moneda, un poco de sopa. Mahaut no deja que nadie se vaya con las manos vacías. Dice que nunca se sabe a quién se le está dando de comer.",
            ),
        )),
        "cuatro_coronados" to unDia(
            Parrafo.MANANA to "Hoy son los Cuatro Coronados, cuatro canteros que, según se cuenta, se negaron a tallar un ídolo para un emperador y acabaron metidos vivos en cajas de plomo. Son los patronos del oficio. Los canteros de Amiens van juntos a misa, con el delantal limpio.",
            Parrafo.TRABAJO to "A esta hora no se trabaja: la logia está cerrada y en la puerta cuelga una rama. Dentro, sobre el banco del maestro, arde una vela delante de una tabla con los cuatro santos pintados, cada uno con su herramienta. El pintor les puso la cara de cuatro canteros de la obra, y todo el mundo sabe quiénes son.",
            Parrafo.ANOCHECER to "Por la noche hay cena de cofradía, con pescado, vino y un canto que se sabe todo el oficio. Los viejos cuentan historias de obras que se cayeron y de maestros que se arruinaron. Colin escucha con los ojos muy abiertos; mañana, en la logia, las contará como si las hubiera vivido.",
        ),
        "san_martin" to unDia(
            Parrafo.MANANA to "Hoy es San Martín, el soldado que partió su capa con un pobre. Es el día en que se cierran las cuentas: se pagan las rentas, se contratan los criados y, en la obra, se acaba la temporada larga y empiezan las jornadas cortas del invierno.",
            Parrafo.PUEBLO to "En la obra se cubren los muros con paja y estiércol para que la helada no los parta. Es trabajo de todos y no lo quiere nadie: huele mal y pica. Los muros acaban parecidos a una cabaña de pastores, y así se quedarán hasta la primavera.",
            Parrafo.ANOCHECER to "Esta noche se come ganso, quien puede, y se prueba el vino nuevo, que todavía no ha terminado de ser vino. {nombre} paga una ronda en la taberna, porque hoy es costumbre, y vuelve a casa cantando una canción de soldados que no sabe de dónde ha sacado.",
        ),
        "cabeza_bautista" to unDia(
            Parrafo.MANANA to "Hoy se recuerda el día en que llegó a Amiens la cabeza de san Juan Bautista, que un canónigo de Picardía trajo de Constantinopla después de la gran cruzada. Se cuenta que la traía escondida bajo la capa, y que el barco no se hundió en una tormenta gracias a ella.",
            Parrafo.PUEBLO to "La procesión sale de la catedral con el relicario y da la vuelta por el barrio de los canónigos, que son los que más lo celebran. Hace frío, las calles están de barro y la gente canta igual, con las manos metidas en las mangas.",
            Parrafo.GENTE to "Dicen que fue la cabeza la que trajo a los peregrinos, los peregrinos el dinero y el dinero la catedral nueva. {nombre} se lo cuenta así a los chicos, como una cadena. Colin dice que entonces la catedral es del Bautista. Mahaut le dice que no diga eso delante del cura.",
        ),
        "navidad" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es Navidad. Se va a la misa de medianoche con faroles, por calles heladas, y la catedral está tan llena que la gente se calienta con el aliento de los demás. Al salir, ya de madrugada, los vecinos se desean buenas fiestas echando vaho por la boca.",
                Parrafo.GENTE to "Hoy se come carne y pan blanco, y Mahaut ha hecho un pastel con lo que llevaba semanas guardando. Los chicos se pelean por el trozo más grande, que siempre es el mismo. {nombre} se come el suyo despacio, para que dure, y se acuerda de las navidades de su pueblo, que eran más pobres y le parecían mejores.",
                Parrafo.ANOCHECER to "Empiezan los doce días de Navidad, en que se trabaja poco y se visita mucho. Esta noche {nombre} se queda dormido junto al fuego, con la jarra vacía y los pies calientes. Fuera, las campanas tocan todavía, porque hoy ninguna quiere ser la primera en callarse.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días la ciudad anda despacio. Los canónigos cantan en el coro, los niños eligen a un obispo de juguete que manda un día entero, y la gente se pasa la tarde de casa en casa, comiendo lo que le dan. En la logia solo hay un par de hombres, afilando por no aburrirse.",
                "Hay escarcha en los tejados y hielo en los bordes del río. Los chicos se deslizan por los charcos helados, caen de culo y se levantan riéndose. Los barqueros miran el hielo con mala cara: si se cierra el río, no se trabaja.",
                "En las casas ricas hay música: un juglar con una viola, un cantor que va de puerta en puerta por un plato de comida. En las pobres se canta igual, más fuerte y peor. En la de {nombre}, el que canta más fuerte es Colin.",
                "Para el día de Año Nuevo se hacen regalos pequeños: una manzana, una cinta, un pan con miel. Hasta los mendigos de la puerta de la catedral reciben más que de costumbre, y lo saben. Hay quien dice que los mismos mendigos piden en dos puertas a la vez.",
            )),
        ),
        "primera_piedra" to unDia(
            Parrafo.MANANA to "Hoy se destapan los muros. Se quita la paja del invierno, podrida y helada, y debajo aparecen las piedras del año pasado, enteras, como si nada. Los canteros dan una vuelta a la obra mirando las juntas, igual que un pastor cuenta las ovejas al salir del corral.",
            Parrafo.TRABAJO to "A esta hora le toca tallar la primera piedra de la temporada, y la talla despacio, como quien estrena zapatos. Cuando termina, pone su marca en una cara que quedará escondida dentro del muro. La sube la grúa a media tarde, y los demás le dan palmadas en la espalda.",
            Parrafo.PENSAMIENTO to "{nombre} no sabe dónde irá su primera piedra cuando la catedral esté hecha: en un pilar, en un arco, en un muro que nadie mirará. Le gusta no saberlo. Algún día alguien la tocará sin saber que fue la primera, y eso, de algún modo, también es suyo.",
        ),
        "reliquias_honorato" to unDia(
            Parrafo.MANANA to "Hoy sacan de la catedral las reliquias de san Honorato, el que fue obispo de Amiens, para llevarlas de pueblo en pueblo pidiendo para la obra. Los canónigos que van con ellas llevan cajas para las monedas y un libro para apuntar los nombres de los que dan.",
            Parrafo.PUEBLO to "La comitiva sale por la puerta de la ciudad con el arca a hombros, una campanilla delante y dos mulas detrás, cargadas de comida para el camino. En cada pueblo habrá misa, se enseñarán los huesos y se pedirá. El que da, dicen, tendrá su nombre escrito y un trozo de cielo.",
            Parrafo.GENTE to "En la logia se habla de lo mismo todo el día: si la fábrica tiene que salir a pedir, es que no hay dinero. Gautier dice que con lo que den los pueblos alcanzará para dos piedras. El maestro lo oye y, por primera vez, no le manda callar.",
        ),
        "colin_aprendiz" to unDia(
            Parrafo.MANANA to "Hoy Colin entra de aprendiz en la logia. Se ha levantado antes que nadie, se ha lavado sin que se lo pidan, y espera a su padre en la puerta con un hatillo y la cara muy seria. Mahaut le ha cosido un delantal de cuero que le llega a los tobillos.",
            Parrafo.TRABAJO to "A esta hora el maestro le da a Colin su primer trabajo: barrer el polvo de piedra de la logia y llevar el agua para el mortero. El chico lo hace como si le hubieran dado el coro entero. {nombre} lo mira de reojo desde su banco y talla peor que de costumbre.",
            Parrafo.ANOCHECER to "Al volver a casa, Colin le cuenta a su madre todo lo que ha visto, con palabras que aprendió esta mañana y que todavía pronuncia mal. Se duerme antes de terminar la sopa. {nombre} lo lleva a la cama y piensa que el chico tiene, con suerte, cincuenta años de piedra por delante.",
        ),
        "sin_dinero" to unDia(
            Parrafo.MANANA to "Hoy el maestro reúne a todos en la logia y les dice lo que ya sabían: se ha acabado el dinero de la fábrica. Desde mañana no se sube más piedra al coro. Los que tienen otra obra adonde ir ya están haciendo el hatillo, y los que no, miran al suelo.",
            Parrafo.PUEBLO to "Esta tarde se desmontan los andamios altos y se baja la grúa. Los muros del coro se quedan a medio subir, como un hombre al que han dejado a mitad de una frase. La gente que pasa por la plaza se para a mirar, y nadie dice nada, que es peor que si dijeran.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en lo que tiene ahorrado, que es poco, y en lo que cuesta el invierno, que es mucho. Piensa también que la catedral no tiene prisa: ya esperará. La que tiene prisa es su familia. Esa noche no dice nada en casa; Mahaut, que lo sabe igual, tampoco.",
        ),
        "medio_jornal" to unDia(
            Parrafo.MANANA to "Hoy le dicen que se puede quedar, pero a medio jornal: la fábrica guarda a unos pocos canteros para tallar en la logia la piedra que ya estaba pagada. Es mejor que nada, y eso es exactamente lo que vale: un poco más que nada.",
            Parrafo.TRABAJO to "A esta hora talla en la logia, que se ha quedado grande. Donde había veinte bancos ahora hay seis, y el golpeteo de los mazos suena hueco, como en una iglesia vacía. Los que quedan trabajan más despacio, para que la piedra dure.",
            Parrafo.GENTE to "Mahaut no dice nada cuando él le cuenta lo del medio jornal. Al día siguiente empieza a hilar para una tejedora de la calle de abajo, por las noches, a la luz del fuego. {nombre} no le pide que no lo haga: lo necesitan, y los dos lo saben.",
        ),
        "encargo_dintel" to unDia(
            Parrafo.MANANA to "Hoy viene a buscarlo a la logia Enguerrand, un mercader de waide de los que tienen casa de piedra. Quiere un dintel tallado para la puerta de su casa nueva, con hojas, con su escudo y con algo que se vea desde la calle. Pregunta por el mejor cantero que quede, y le dicen su nombre.",
            Parrafo.TRABAJO to "A esta hora está en el patio del mercader, midiendo la puerta con su cuerda de nudos. Enguerrand le explica lo que quiere tres veces, y cada vez quiere algo distinto. {nombre} dice que sí a todo y apunta en la cabeza lo único que importa: cuánto, y para cuándo.",
            Parrafo.PENSAMIENTO to "Es raro tallar para un hombre y no para Dios. {nombre} se dice que la piedra es la misma y el cincel también, y que un dintel bien hecho también es una oración. Lo que no se dice es que este paga mejor, y al contado.",
        ),
        "entrega_dintel" to unDia(
            Parrafo.MANANA to "Hoy se coloca el dintel en la casa de Enguerrand. Lo suben entre cuatro con cuerdas y cuñas, despacio, y el mercader mira desde abajo con las manos juntas, como si estuviera en misa. Cuando la piedra se asienta, alguien de la calle aplaude.",
            Parrafo.GENTE to "Enguerrand le paga una parte en monedas y otra en paño teñido de azul, de su propia waide, que vale más que el dinero y se come peor. Mahaut lo toca y le cambia la cara: hacía años que no tenía un vestido nuevo. Lo mide sobre los chicos antes que sobre ella.",
            Parrafo.ANOCHECER to "Esta noche {nombre} pasa por delante de la casa del mercader, aunque no le queda de camino. Con la poca luz que hay, las hojas del dintel parecen moverse. Se queda un rato mirándolas, y luego sigue, deprisa, antes de que alguien lo vea mirando su propia obra.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "cantero", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
