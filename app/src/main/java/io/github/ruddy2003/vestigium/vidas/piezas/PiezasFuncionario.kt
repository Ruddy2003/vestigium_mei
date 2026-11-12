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
 * El cuento del funcionario, empleado de un ministerio en la capital de un Estado que todo lo vigila, de 1984 a 1988.
 * Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su ficha se llama «Un funcionario»: el cuento escribe «el funcionario» a mano y no usa el hueco del nombre.
 * - Su ficha se inspira en el mundo de *1984*, pero nada sale de la novela: ni sus personas, ni sus palabras, ni sus
 *   oficinas. El Estado, sus fiestas y su vocabulario son de esta historia.
 * - Su trabajo sale tres veces al día (la gimnasia ante la pantalla y los dos tramos del ministerio): esos textos valen
 *   a cualquiera de sus horas y no dicen qué comida toca.
 * - Dos estados: «campana» (temporadas 2 y 4, tres semanas de noventa horas durmiendo en el ministerio) y «provincia»
 *   (temporada 4, dos días en casa de su hermana).
 * - El pueblo lee el arco «camaras» (cuántas hay en las calles).
 * - No repite lo que ya dice «Su vida»: la gimnasia de las siete y cuarto, los periódicos viejos que corrige, el guisado
 *   gris, el café que no es café, la ración de chocolate, los carteles con un rostro, el diccionario que adelgaza.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG7.kt`):
 * - Anselm, su compañero de mesa, hasta que desaparece (`anselm`, temporada 1).
 * - Mira, la del archivo, desde `mira` (temporada 2) hasta que la trasladan (`mira_traslado`, 3).
 * - El vecino de enfrente, hasta que se lo llevan (`vecino`, temporada 2).
 * - El diario escondido, desde `diario` (temporada 3) hasta que lo quema (`quema`, 4).
 * - Desde el ascenso (`ascenso`, temporada 5) corrige libros de texto.
 * - Las fiestas que vuelven cada año no nombran a Anselm ni a Mira.
 */
internal object PiezasFuncionario {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(12, 1, 2)

    private val MANANA = listOf(
        v("Al funcionario lo despierta la pantalla de su cuarto, que se enciende sola con un himno y una voz alegre que da los buenos días a la nación entera. No se puede apagar. Se levanta antes de que la voz diga su número de vivienda, que es lo que pasa si tarda. Hoy no tarda."),
        v("El funcionario se despierta un minuto antes que la pantalla, como siempre, y se queda quieto en la cama con los ojos cerrados, para que la cámara crea que todavía duerme. Es el único minuto del día que es suyo. Lo aprovecha. No piensa en nada. Luego, el himno."),
        v("El funcionario se despierta con frío, porque la calefacción vuelve a funcionar solo a ratos, y hace la gimnasia ante la pantalla con el abrigo puesto. La instructora de la pantalla le dice que se lo quite. Se lo quita. Tiene la piel de gallina. Sigue doblándose."),
        v("Lo primero que hace el funcionario al levantarse es mirarse en el espejo del lavabo, que está rajado, y comprobar que tiene la cara de siempre: neutra, un poco cansada, sin nada. Es la cara que hay que llevar. Se la pone como se pone la corbata. Sale."),
        v("El funcionario se despierta con el ruido de las botas en la escalera: los de la patrulla del bloque, que suben a alguna parte. Se queda quieto, escuchando. Pasan de largo. No es su piso. Se levanta. No pregunta luego a nadie de quién era."),
        v("El funcionario se despierta con la boca seca de la ginebra de anoche y el estómago vacío. El agua del grifo sale marrón los primeros segundos. La deja correr. Se lava, se peina, se pone la camisa de ayer, que es la de mañana también. Hay jabón para una semana más.", meses = INVIERNO),
    )

    /** Tres semanas de campaña, noventa horas a la semana, durmiendo en el ministerio (estado «campana»). */
    private fun ca(texto: String, hacer: Hacer? = null) = v(texto, estado = "campana", hacer = hacer)

    private val CAMPANA_MANANA = listOf(
        ca("El funcionario se despierta en el catre plegable de su oficina, entre las mesas, con los demás de la sección roncando alrededor, y no sabe qué hora es: en la campaña las luces no se apagan nunca. Se levanta, se lava en el lavabo del pasillo, vuelve a su mesa. Ya hay papeles."),
        ca("Al funcionario lo despierta el carrito del café, que pasa por las oficinas tres veces cada noche durante la campaña, con un líquido negro y caliente que no sabe a nada y que mantiene despierto. Se toma dos vasos. Se frota los ojos. Vuelve a los expedientes."),
        ca("El funcionario se despierta con la cara pegada a la mesa, encima de un expediente, con la marca del papel en la mejilla. Se ha dormido trabajando. Nadie lo ha visto, o todos lo han visto y nadie dice nada, porque todos se duermen así. Sigue donde se quedó."),
    )

    private val CAMPANA_TRABAJO = listOf(
        ca("A esta hora corrige expedientes sin parar, uno detrás de otro, con los ojos que ya no ven bien las letras, porque la campaña exige que todo lo de este año diga lo mismo antes del día señalado. Las cifras cambian. Los nombres cambian. Él firma. El montón no baja.", Hacer.TRABAJO),
        ca("A esta hora lleva noventa horas a la semana en la misma silla, y su espalda lo sabe. Se levanta, da dos vueltas a la oficina, se sienta. Un jefe pasa por detrás mirando las pantallas. Nadie levanta la cabeza. El ruido de las teclas es como una lluvia que no para.", Hacer.TRABAJO),
        ca("A esta hora va del ministerio al ministerio: de su oficina al archivo, del archivo a la oficina, por los pasillos que en la campaña nadie apaga, con carpetas en los brazos. No sale a la calle. No ha salido en diez días. Ha olvidado de qué color es el cielo esta semana.", Hacer.CAMINO),
        ca("A esta hora come en el comedor del ministerio, abierto día y noche durante la campaña, un plato de lo mismo que ayer y que mañana, de pie, en cinco minutos. Los de la campaña tienen ración doble de pan. Es lo único bueno de la campaña. Se lo come entero.", Hacer.COMER),
        ca("A esta hora duerme cuatro horas en el catre plegable de la oficina, con la luz encendida y la pantalla de la pared hablando bajito de los logros de la campaña. Duerme igual. El cansancio es más fuerte que la luz. Sueña con columnas de números.", Hacer.DORMIR),
        ca("A esta hora está en la reunión de la sección, de pie, mientras el jefe lee los avances de la campaña y todos aplauden cuando hay que aplaudir. Aplaude. Le duelen las manos de no hacer otra cosa que escribir. Le duelen de aplaudir.", Hacer.GENTE),
        ca("A esta hora no tiene rato propio: en la campaña no hay tardes libres. Escribe a su hermana una postal con las frases permitidas, la mete en el buzón del pasillo y vuelve a su mesa. La postal dice que está bien. Es lo que dicen todas las postales.", Hacer.LIBRE),
    )

    private val CAMPANA_PUEBLO = listOf(
        ca("El ministerio, durante la campaña, no duerme: las ventanas encendidas toda la noche, los ascensores subiendo y bajando, los mensajeros corriendo por los pasillos con carpetas, los catres en las oficinas. Visto desde la calle, parece un barco iluminado en un mar negro."),
        ca("En la campaña la ciudad sigue fuera, pero el funcionario no la ve. Su mundo son tres pasillos, una oficina, el comedor, el lavabo y el ascensor. Por la ventana se ve un trozo de cielo y una cámara en la esquina del edificio de enfrente."),
    )

    private val CAMPANA_ANOCHECER = listOf(
        ca("No hay anochecer en la campaña: las luces de la oficina son las mismas a todas horas. Solo por la ventana se ve que fuera oscurece, que se encienden las farolas, que vuelve la gente a casa. El funcionario lo mira un momento. Luego, otro expediente."),
        ca("Cuando oscurece, en la campaña, el jefe de la sección pasa con la lista de lo que falta, y lo que falta es siempre más que lo hecho. Nadie se queja. Alguien bosteza y lo disimula con una tos. El funcionario coge otra carpeta del montón."),
    )

    /** Dos días en casa de su hermana, en la provincia, en la temporada 4 (estado «provincia»). */
    private fun pr(texto: String, hacer: Hacer? = null) = v(texto, estado = "provincia", hacer = hacer)

    private val PROVINCIA_MANANA = listOf(
        pr("El funcionario se despierta en la casa de su hermana, en el pueblo, en la cama que fue de sus padres, y lo primero que nota es que no hay pantalla en el cuarto. Hay una cruz vieja en la pared, tapada con un calendario. Se queda tumbado mirando el techo un largo rato."),
        pr("Al funcionario lo despierta un gallo, por primera vez en años, y luego la voz de su hermana en la cocina, hablando con alguien. Huele a pan de verdad. Se queda en la cama, quieto, con miedo de moverse, como si se pudiera romper algo."),
    )

    private val PROVINCIA_TRABAJO = listOf(
        pr("A esta hora no trabaja: ayuda a su hermana en el huerto de detrás de la casa, cavando con una azada que no sabe usar, mientras ella se ríe de él. El sol le da en la nuca. Suda. Hace años que no sudaba por algo que no fuera miedo.", Hacer.TRABAJO),
        pr("A esta hora pasea con su hermana por el camino del río, como cuando eran niños, y ella le habla en voz baja de los del pueblo: quién se fue, a quién se llevaron, quién vigila. Aquí también vigilan. Pero menos. Aquí todos se conocen.", Hacer.CAMINO),
        pr("A esta hora come en la cocina de su hermana sopa de verdad, con verduras del huerto, pan del horno del pueblo y un trozo de tocino guardado para la visita. Come despacio. Se le saltan las lágrimas. Le dice a su hermana que es el humo de la cocina.", Hacer.COMER),
        pr("A esta hora duerme como no duerme en la capital: hondo, sin sueños, con la ventana abierta y el ruido del campo, que es un ruido que no vigila a nadie. Se despierta una vez, no sabe dónde está, y se vuelve a dormir sonriendo.", Hacer.DORMIR),
        pr("A esta hora está sentado con su hermana en el banco de la puerta, mirando cómo se va la luz detrás de los campos, sin hablar. Pasan unos vecinos. Saludan. Uno se para y le pregunta por la capital. Él dice que bien. El vecino asiente. Sabe que no.", Hacer.GENTE),
        pr("A esta hora lee en voz alta a los niños de su hermana un libro de cuentos viejo, de los de antes de todo, que ella guarda debajo de una tabla del suelo. Los niños escuchan con la boca abierta. Él lee con cuidado, mirando la ventana de vez en cuando.", Hacer.LIBRE),
    )

    private val PROVINCIA_PUEBLO = listOf(
        pr("El pueblo de su hermana está a seis horas de tren de la capital: una calle, una plaza con un altavoz en un poste, una cooperativa, un campo de remolacha hasta el horizonte. Hay un cartel del Fundador en la plaza, descolorido por el sol. Nadie lo mira."),
        pr("En el pueblo hay una sola cámara, en la estación, y casi siempre está rota. La gente habla en la calle sin bajar la voz. El funcionario tarda un día en dejar de mirar por encima del hombro. Al segundo día, ya se tiene que ir."),
    )

    private val PROVINCIA_ANOCHECER = listOf(
        pr("Al caer la tarde el cielo del pueblo se pone de un color que el funcionario no recordaba, naranja y luego violeta, sin edificios que lo corten. Su hermana dice que siempre es así. Él dice que en la capital no hay cielo. Ella dice que sí lo hay. Que él no lo mira."),
        pr("Anochece en el pueblo, y no se encienden farolas, porque no hay. Solo las ventanas de las casas, amarillas. Se oyen grillos, un perro, una radio lejos con música de baile. El funcionario se queda en la puerta hasta que su hermana lo llama. No quiere entrar."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} La pantalla dice cada mañana que hará buen tiempo. Lo dice también cuando llueve."),
        v("El funcionario mira el cielo un momento por una ventana, entre dos bloques. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El parte del tiempo es la única noticia de la pantalla que se puede comprobar."),
        v("{cielo} En {estacion} la capital cambia poco: es gris en todas las estaciones, con más o menos agua."),
        v("{de_estacion} {cielo} Hablar del tiempo es lo único seguro que se puede hablar con un desconocido."),
        v("En la capital el cielo es una franja entre los bloques, y casi nadie la mira. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un azul pálido que no parece de aquí.",
            "Hace un día claro, raro, y el sol pega en las fachadas grises y las vuelve blancas.",
            "No hay nubes, y las cámaras de las esquinas brillan al sol.",
        ),
        Clima.NUBES to listOf(
            "El cielo está gris y bajo, del mismo color que los bloques.",
            "Pasan nubes pesadas, y la luz es la de siempre: ninguna.",
            "Está nublado, como casi todos los días.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fina y sucia que deja manchas negras en la ropa.",
            "Cae un agua fría que entra por las suelas de los zapatos, todas rotas.",
            "Llueve, y la gente camina deprisa con la cabeza baja, como siempre.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, y la luz se va dos veces en todo el barrio.",
            "Truena, y por un momento todo el mundo mira al cielo a la vez.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y la nieve tapa por un día lo gris, hasta que se pone gris también.",
            "Cae nieve, y los barrenderos del Estado la apilan en montones negros.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, y las cámaras no ven nada, y la gente camina un poco más tranquila.",
            "La niebla tapa la torre del ministerio hasta la mitad.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento frío que arranca los carteles viejos y los lleva por la calle.",
            "Hace viento, y las banderas de los edificios oficiales restallan como látigos.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en los parques del Estado brotan los árboles sin pedir permiso.",
            "En primavera se cambian los carteles de las fachadas por otros iguales, más nuevos.",
            "Es el tiempo de las campañas de limpieza y de los desfiles.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el calor se queda pegado a todo, de día y de noche.",
            "En verano los días son largos, y el sol no perdona a nadie.",
            "Es el tiempo de las vacaciones obligatorias en los campamentos del sindicato.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando la calefacción todavía no se enciende y el frío ya ha llegado.",
            "En otoño las hojas de los parques se barren antes de que toquen el suelo.",
            "Es el tiempo de las cifras de la cosecha en la pantalla.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando se oscurece antes de salir del ministerio.",
            "En invierno las colas para el carbón dan la vuelta a la manzana.",
            "Es el tiempo de los abrigos que no abrigan y de las estufas que no calientan.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora corrige un expediente viejo en su mesa del ministerio, cambiando una cifra por otra, un nombre por otro, una fecha por otra, con una letra que imita la del original. Luego firma con su número. Lo de antes ya no ha pasado. Lo de ahora siempre pasó así.", hacer = Hacer.TRABAJO),
        v("A esta hora busca en el archivo de su sección todos los papeles donde aparece un nombre que le han mandado borrar: actas, fotos, listas de premiados, felicitaciones. Son cuarenta y tres. Los saca uno por uno. Mañana el nombre no habrá existido nunca.", hacer = Hacer.TRABAJO),
        v("A esta hora espera delante del tubo de su mesa a que llegue la carpeta siguiente, que cae con un golpe seco desde algún piso de arriba. No sabe quién la manda. No sabe adónde va lo que él devuelve. Abre la carpeta. Dentro hay una instrucción de una línea.", hacer = Hacer.TRABAJO),
        v("A esta hora hace la gimnasia de la mañana delante de la pantalla, con la instructora que le dice que doble más, que respire mejor, que sonría. Él dobla, respira, sonríe. La cámara de la pantalla lo mira. Él mira el suelo. Es la única manera de no mirarla.", hacer = Hacer.TRABAJO),
        v("A esta hora escribe el informe de la semana de su sección, que dice que todo va bien, como todos los informes. Pone las cifras que le han dado. Las cifras no se parecen a lo que él ha visto. No importa. El informe es para arriba. Lo de abajo no lo lee nadie.", hacer = Hacer.TRABAJO),
        v("A esta hora pasa a máquina una lista de nombres para un acto del Estado: los que hablarán, los que aplaudirán, los que irán en primera fila. Ve el nombre de un vecino. No dice nada. Pasa a máquina el siguiente. La máquina hace ruido. Es un ruido que tapa otros.", hacer = Hacer.TRABAJO),

        v("A esta hora come en el comedor del ministerio, en una bandeja de metal, un guiso de algo con patatas, un pan y un vaso de agua, sentado en una mesa larga entre gente que no habla. Se come rápido y mirando el plato. Hablar en el comedor es peligroso. Masticar, no.", hacer = Hacer.COMER),
        v("A esta hora come de pie en la cantina del ministerio, junto a la ventana, un bocadillo de algo rosa que dicen que es carne, con una taza de té aguado. Por la ventana se ve el patio, donde unos funcionarios fuman en silencio. Termina. Vuelve.", hacer = Hacer.COMER),
        v("A esta hora come lo que le dan con la cartilla: sopa de col, un trozo de pan oscuro, una cucharada de mermelada que sabe a nabo. Se lo come todo. No sabe cuándo será la próxima vez que haya mermelada. Nadie lo sabe.", hacer = Hacer.COMER),

        v("A esta hora va en el metro, de pie, apretado, entre gente que no se mira, con la cartera contra el pecho. En el vagón hay una pantalla pequeña que pone noticias de la producción. Nadie la mira. Todos la oyen. El tren va despacio. Nadie protesta.", hacer = Hacer.CAMINO),
        v("A esta hora camina por la avenida grande hacia el ministerio, entre miles de funcionarios que van al mismo sitio, con la misma ropa gris, el mismo paso, la misma cara. Si se parara, lo arrastrarían. No se para. Pasa debajo de tres cámaras. Las cuenta.", hacer = Hacer.CAMINO),
        v("A esta hora espera el autobús en una parada donde hay siempre más gente que sitio, bajo la lluvia o el sol, sin hablar. El autobús llega lleno. Sube igual. Va pegado a la puerta. Ve pasar la ciudad por un cristal sucio: bloques, carteles, cámaras, bloques.", hacer = Hacer.CAMINO),

        v("A esta hora duerme en su cama estrecha, en su cuarto de nueve metros, con la luz de la pantalla bajada al mínimo, que nunca llega a apagarse del todo. Duerme de lado, de cara a la pared. Así la pantalla le ve la espalda. Es lo único que le enseña.", hacer = Hacer.DORMIR),
        v("A esta hora duerme mal, despertándose con cada ruido de la escalera, de la calle, del piso de arriba. Cada coche que se para delante del bloque lo despierta. Escucha. El coche se va. Se vuelve a dormir. Así tres o cuatro veces cada noche.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con cosas que no puede recordar al despertar, solo la sensación: algo de colores, una voz que le hablaba bajito, un sitio con árboles. Por la mañana se esfuerza en olvidarlo del todo. Los sueños también se pueden notar en la cara.", hacer = Hacer.DORMIR),

        v("A esta hora está en las actividades obligatorias del bloque, en la sala comunal: una charla sobre los logros del plan, una canción en grupo, un ejercicio de aplausos. Se apunta en una lista quién viene. Él viene siempre. Aplaude a tiempo. Canta en voz media.", hacer = Hacer.GENTE),
        v("A esta hora está en la reunión de vecinos de su planta, donde cada uno dice qué ha visto de los demás esta semana. Él dice que nada. Todos dicen que nada. El responsable de planta apunta nada. Es la mejor reunión posible. Dura una hora.", hacer = Hacer.GENTE),
        v("A esta hora está en la sala comunal viendo con los vecinos el programa de la tarde en la pantalla grande: un concurso de preguntas sobre la historia del Estado, con premios de cartillas extra. Todos saben las respuestas. Las respuestas cambian cada año. Las saben igual.", hacer = Hacer.GENTE),

        v("A esta hora está en su cuarto, sentado en la única silla, bebiendo un vaso de ginebra barata que quema como el aceite, con la pantalla hablando de fondo. No hace nada. No se puede hacer nada. Hacer algo sería sospechoso. Mira la pared.", hacer = Hacer.LIBRE),
        v("A esta hora lee el periódico del día, entero, como hay que hacer, y subraya con lápiz las frases importantes, por si un día le preguntan. Lo hace despacio. Sabe que la mitad de lo que lee lo corrigió él mismo, o alguien como él, hace un año. Lo lee igual.", hacer = Hacer.LIBRE),
        v("A esta hora pasea por el parque del barrio, de los paseos permitidos, por el camino de grava entre árboles podados todos a la misma altura. Se sienta en un banco. Mira a los niños de la Guardia Joven, que marchan en filas. Se levanta antes de que lo miren a él.", hacer = Hacer.LIBRE),
    )

    private val PUEBLO = listOf(
        v("{entorno} El ministerio es una torre de hormigón blanco de treinta pisos, con ventanas estrechas como rendijas y una bandera enorme en la azotea. Por dentro, kilómetros de pasillos iguales, puertas iguales, oficinas iguales. El funcionario lleva diez años y no conoce más que cinco pisos."),
        v("El bloque donde vive es uno de cien bloques iguales en una avenida que no se acaba: doce pisos de hormigón, ventanas iguales, balcones vacíos, una pantalla grande en la fachada que da las noticias a la calle. En la puerta, una cámara. En la escalera, otra. {entorno}"),
        v("{entorno} La capital tiene dos caras: la de las avenidas anchas, los monumentos y los desfiles, limpia y vacía; y la de detrás, la de los bloques, las colas, los patios con ropa tendida y niños flacos. El funcionario vive en la segunda y trabaja en la primera."),
        v("En todas las paredes de la capital hay carteles: el Fundador mirando a lo lejos, obreros sonrientes con herramientas, cifras de la producción, la frase del año en letras rojas. Los carteles se cambian cada mes, y nadie se acuerda de los del mes pasado. {entorno}"),
        v("{entorno} El metro de la capital es hondo, limpio, rápido y silencioso: nadie habla en los vagones, y en cada estación hay un retrato del Fundador y un altavoz que dice el nombre de la estación con una voz de mujer muy tranquila. Es lo mejor que funciona en el Estado."),
        v("En el mercado del barrio, en un patio detrás de los bloques, se cambia de todo sin cartilla: medias, café de verdad, jabón, cuchillas, libros viejos. Está prohibido. Todo el mundo va. La policía también, a comprar. {entorno}"),
    )

    /** Las cámaras de las calles (arco «camaras»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("camaras", 0.0, 0.55, frases = listOf(
            "En las esquinas hay cámaras, una aquí, otra allá, y todavía quedan calles donde no llega ninguna.",
            "Las cámaras de la calle son grises, del color de las paredes, y a veces uno se olvida de que están.",
        )),
        Entorno("camaras", 0.55, 0.75, frases = listOf(
            "Este año han puesto cámaras nuevas en los portales, en las paradas, en los bancos de los parques.",
            "Ya casi no queda un trozo de calle sin una cámara que lo mire, y la gente camina más deprisa.",
        )),
        Entorno("camaras", 0.75, 1.0, frases = listOf(
            "Hay cámaras en todas partes: en cada farola, en cada puerta, en cada ascensor, en cada árbol del parque.",
            "Las cámaras nuevas giran solas, siguiendo a la gente, con una lucecita roja que no se apaga nunca.",
        )),
    )

    private val GENTE = listOf(
        v("Vera, su hermana, vive en un pueblo de la provincia, a seis horas de tren, con su marido y sus dos hijos, y trabaja en la cooperativa. Le escribe cartas de una página, una al mes, que llegan abiertas y con alguna línea tachada en negro. Él las guarda en una caja."),
        v("El niño de los vecinos de al lado tiene doce años y es de la Guardia Joven: pañuelo rojo, gorra, un cuaderno donde apunta cosas. Saluda al funcionario en la escalera con mucha educación. El funcionario le devuelve el saludo con más educación todavía."),
        v("El jefe de su sección es un hombre gordo y amable que nunca levanta la voz y que ha mandado a otros sitios a cuatro funcionarios en dos años. Nadie sabe adónde. El jefe sonríe mucho. Cuando sonríe, el funcionario se pone a trabajar más rápido."),
        v("Los de su sección son veinte funcionarios en una oficina con veinte mesas iguales, cada uno con su tubo, su máquina de escribir y su pantalla. Se conocen desde hace años y no saben nada unos de otros. Se dan los buenos días. Se dan las buenas tardes. Nada más."),
        v("El viejo del mercado del barrio, que vende cosas de antes en una mesa de tijera, tiene una mano de menos y unos ojos que lo ven todo. Al funcionario le vende a veces una cuchilla buena, un jabón, un lápiz de verdad. Nunca le pregunta en qué trabaja. Lo sabe."),
        v("Anselm, su compañero de la mesa de al lado, es un hombre bajito con gafas que se ríe por la nariz y que corrige expedientes más rápido que nadie. A veces, al pasarle una carpeta, le guiña un ojo. El funcionario no sabe qué quiere decir. No se lo pregunta.", hasta = "anselm"),
        v("La mesa de al lado, la que era de Anselm, la tiene ahora un muchacho nuevo que no habla. Del nombre de Anselm no queda nada: ni en las listas, ni en las fotos de la sección, ni en la memoria de nadie, por lo que dicen. El funcionario se acuerda. No lo dice.", desde = "anselm"),
        v("El vecino de enfrente es un viejo profesor jubilado que toca el violín muy bajito por las noches, con sordina, para no molestar. El funcionario lo oye a través de la puerta. No sabe qué toca. No es ninguna de las marchas. Le gusta más.", hasta = "vecino"),
        v("La puerta de enfrente, la del viejo del violín, tiene desde hace tiempo una cinta de papel pegada con un sello del Estado. Nadie ha vuelto a entrar. Nadie habla de él. Por las noches, el funcionario escucha a través de la puerta. No se oye nada.", desde = "vecino"),
        v("Mira trabaja en el archivo de abajo, el de los periódicos, una mujer delgada con el pelo corto que se ríe sin mirar la pantalla, como si no existiera. El funcionario baja al archivo más de lo que necesita. Hablan de nada. Es lo más peligroso que ha hecho en años.", desde = "mira", hasta = "mira_traslado"),
        v("A Mira la trasladaron a otro ministerio sin despedirse, un lunes, y su mesa del archivo la tiene ahora un hombre que tose. El funcionario ya no baja al archivo más de lo necesario. A veces, en el metro, le parece verla. Nunca es ella.", desde = "mira_traslado"),
    )

    private val OBJETO = listOf(
        v("En el Estado cada cosa tiene un número, un dueño y un sitio. {dato_objeto}"),
        v("{dato_objeto} El funcionario lo sabe desde siempre, como lo sabe todo el mundo."),
        v("Hoy el funcionario se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} La pantalla no habla nunca de eso. Por eso es verdad."),
        v("{dato_objeto} Todo el mundo lo sabe, y por eso nadie lo dice en voz alta."),
        v("Las cosas de la capital son pocas y feas, pero cada una cuenta algo. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("pantalla", "la pantalla", "una pantalla", "La pantalla",
            "La pantalla de cada cuarto es un rectángulo gris en la pared que habla, enseña y mira a la vez: tiene una cámara detrás del cristal; se puede bajar el volumen, pero no apagarla; quien la tapa con algo recibe una visita al día siguiente."),
        ObjetoCuento("cartilla", "la cartilla", "una cartilla de racionamiento", "La cartilla",
            "La cartilla es una libreta de cupones de colores, uno para cada cosa: pan, azúcar, jabón, tabaco, ropa; se sella en la tienda; si se pierde, se pasa un mes sin nada; hay quien la lleva cosida por dentro del abrigo."),
        ObjetoCuento("lapiz", "el lápiz", "un lápiz", "El lápiz",
            "Los lápices del ministerio son de madera blanda, con una mina gris que se rompe al menor apretón y un número de serie grabado; cada funcionario tiene cuatro al mes; los lápices buenos, los de mina negra, solo se encuentran en el mercado."),
        ObjetoCuento("cuaderno", "el cuaderno", "un cuaderno", "El cuaderno",
            "Los cuadernos se compran en la papelería del Estado y tienen todas las hojas numeradas, para que se note si falta alguna; los cuadernos sin numerar, de antes, se venden en el mercado, caros, y quien tiene uno lo esconde."),
        ObjetoCuento("ginebra", "la ginebra", "una botella de ginebra", "La ginebra",
            "La ginebra del Estado es incolora, huele a aceite y se vende en botellas sin etiqueta con un tapón de cera; es lo único que no está racionado; quema la garganta y por un rato también los pensamientos."),
        ObjetoCuento("abrigo", "el abrigo", "un abrigo gris", "El abrigo",
            "El abrigo de los funcionarios es gris, de una lana que pica y no abriga, con un número en la etiqueta del cuello; se da uno cada cinco años; en invierno, debajo, se llevan periódicos doblados contra el frío."),
        ObjetoCuento("pase", "el pase", "un pase del ministerio", "El pase",
            "El pase del ministerio es una tarjeta con la foto, el número y una banda de colores que dice a qué pisos se puede entrar; se enseña en cada puerta, en cada ascensor, en cada pasillo; sin él, un funcionario no existe."),
        ObjetoCuento("tubo", "el tubo", "un tubo de carpetas", "El tubo",
            "Cada mesa del ministerio tiene un tubo de metal que sube por la pared hasta pisos que nadie ha visto: por él llegan las carpetas con un golpe y por él se devuelven; nadie sabe quién está al otro lado; se dice que nadie."),
        ObjetoCuento("cigarrillo", "el cigarrillo", "un cigarrillo", "El cigarrillo",
            "Los cigarrillos del Estado vienen en paquetes de cartón blando, sin marca, y se deshacen si no se fuman con cuidado; el tabaco se cae de la punta; los funcionarios fuman en el patio, en silencio, cinco minutos, mirando las paredes."),
        ObjetoCuento("gafas", "las gafas", "unas gafas", "Las gafas",
            "Las gafas se piden en la clínica del barrio y llegan a los seis meses, con una montura negra igual para todos y unos cristales que casi nunca son los de la receta; el funcionario lleva las suyas pegadas con cinta por la patilla."),
        ObjetoCuento("periodico", "el periódico", "un periódico", "El periódico",
            "El periódico del Estado sale cada mañana con cuatro hojas: los logros, los discursos, las cifras, los enemigos; se lee entero, porque luego se pregunta; las hojas viejas sirven para encender estufas, envolver pescado, tapar ventanas."),
        ObjetoCuento("llave", "la llave", "una llave", "La llave",
            "Las llaves de las viviendas son todas iguales, con un número grabado; el responsable de cada planta tiene copia de todas; el funcionario cierra su puerta cada noche con dos vueltas, aunque sabe que no sirve de nada."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} El funcionario oye eso y no piensa nada, o piensa que no piensa nada, que es lo que hay que hacer. A sus {edad} años ha aprendido que lo que se piensa se nota en la cara, y que la cara es pública. Piensa en el tiempo. En el tiempo se puede pensar."),
        v("Piensa en su infancia, en el pueblo, antes de que todo fuera así, o eso cree, porque a lo mejor siempre fue así y se lo ha inventado. Se acuerda de un río, de una bicicleta, de su padre riéndose. No se acuerda de la cara de su padre. Se la han borrado, o se le ha borrado. Ya no sabe."),
        v("{mundo} El funcionario piensa que el pasado es lo que él escribe cada día en el ministerio. Piensa que entonces el pasado lo escribe alguien como él. Piensa que eso tendría que darle poder. Le da miedo. Piensa que todo lo que sabe del mundo podría haberlo corregido otro."),
        v("{mundo} A veces, en el metro, el funcionario mira a la gente y piensa que todos piensan lo mismo que él y que ninguno lo dirá nunca. Piensa que si todos lo dijeran a la vez, no podrían llevárselos a todos. Luego piensa que sí. Luego deja de pensar."),
        v("Piensa en Vera, su hermana, en el pueblo, y en sus hijos, que crecen sin saber nada de cómo eran las cosas. Piensa que a lo mejor es mejor así. Piensa que él tampoco sabe cómo eran. Piensa que eso es lo que querían. Que lo han conseguido."),
        v("{mundo} A sus {edad} años, el funcionario sabe que no será nada más que esto: un número en una mesa, corrigiendo papeles hasta que lo corrijan a él. No le duele. Hace tiempo que no le duele nada. Eso es lo que más le asusta, cuando se acuerda de asustarse."),
    )

    private val MUNDOS = mapOf(
        "frente" to listOf(
            "La pantalla dice que el frente del este avanza, como dijo la semana pasada y la anterior.",
            "Se anuncia que la producción de botas ha superado el plan, y en las tiendas no hay botas.",
            "La pantalla habla de un complot de enemigos descubierto a tiempo, con nombres que nadie conoce.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde las farolas de la avenida se encienden todas a la vez, con un zumbido, y las pantallas de las fachadas suben el volumen para el boletín de la noche. La gente vuelve a sus bloques sin hablar. El funcionario se mezcla con ellos. Es fácil. Se parece a todos."),
        v("Anochece, y en el bloque se encienden las ventanas, todas con la misma luz azul de las pantallas. Desde la calle parecen peceras. En cada una, alguien solo, o dos personas que no se hablan. El funcionario sube los doce pisos a pie: el ascensor no funciona."),
        v("Al caer la tarde el funcionario sale del ministerio con los demás, por la puerta grande, enseñando el pase, y baja la escalinata entre miles de abrigos grises. Fuera, la avenida, la lluvia, la pantalla gigante de la plaza con la cara del Fundador. No levanta la vista."),
        v("De noche, desde la ventana de su cuarto, el funcionario ve la torre del ministerio iluminada a lo lejos, con sus ventanas encendidas, como un edificio que no duerme. Piensa que dentro hay alguien corrigiendo algo ahora mismo. Corre la cortina. La pantalla, detrás, lo mira igual."),
        v("Anochece, y en el patio del bloque unos niños juegan a la Guardia Joven: uno es el enemigo, los demás lo persiguen y lo atrapan. Se ríen. El enemigo también se ríe. El funcionario los mira desde la ventana. Cierra la ventana."),
        v("La última luz del día se queda en lo alto de la bandera del ministerio, que flamea roja contra un cielo gris cuando la calle ya está a oscuras. Suena el toque de la noche en todos los altavoces. A partir de ahora, solo se puede estar en casa.", meses = INVIERNO),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y anoche, a las doce, toda la ciudad escuchó el discurso del año en las pantallas: tres horas de logros, cifras, enemigos derrotados y promesas. Luego se brindó con una copa de vino dulce del Estado, el que se da por cartilla una vez al año.",
            Parrafo.TRABAJO to "Hoy el funcionario no trabaja: es fiesta. Se queda en su cuarto con la pantalla, que repite el discurso de anoche dos veces más. Duerme un poco. Lee el periódico especial del año nuevo, de ocho hojas en vez de cuatro. Las cuatro de más son fotos del Fundador.",
            Parrafo.PENSAMIENTO to "El funcionario piensa en el año que empieza y no le pide nada, porque pedir es esperar, y esperar es peligroso. Piensa solo en llegar al próximo año nuevo sin que pase nada. Es lo único que se puede desear sin que se note.",
        ),
        "racion" to unDia(
            Parrafo.MANANA to "Hoy la pantalla anuncia la ración del trimestre con una música de fiesta: la de pan sube, la de azúcar sube, la de jabón se mantiene. La gente aplaude en los portales. El funcionario aplaude. Luego mira su cartilla: los cupones nuevos dan menos que los viejos.",
            Parrafo.GENTE to "Esta tarde, en la cola de la tienda del bloque, nadie habla de la ración, pero todos miran lo que se llevan los demás. Una mujer cuenta sus cupones tres veces. Un viejo dice que antes daban más. Todos hacen como que no lo han oído. El viejo también.",
            Parrafo.PENSAMIENTO to "El funcionario piensa que la ración sube cada trimestre en la pantalla y baja cada trimestre en el plato, y que las dos cosas son verdad a la vez. Piensa que ya no le cuesta creerse las dos. Piensa que eso es lo que hace el ministerio. Que lo hace bien.",
        ),
        "fundador" to unDia(
            Parrafo.MANANA to "Hoy es el Día del Fundador, y en cada ventana de la capital hay un retrato suyo, igual, que se reparte en los portales la semana antes: un hombre con bigote mirando a lo lejos, hacia algo que nadie ve. Se comprueba quién no lo pone. Todo el mundo lo pone.",
            Parrafo.TRABAJO to "Hoy el funcionario no trabaja: es fiesta. Pone el retrato en la ventana, bien derecho, y sale a la calle a ver los de los demás, como hay que hacer. Cien mil retratos iguales mirándolo desde todas las ventanas. Ninguno lo mira a él. Todos miran lo mismo, a lo lejos.",
            Parrafo.PENSAMIENTO to "El funcionario piensa en el Fundador, que murió hace muchos años, o eso dicen, o a lo mejor no murió. Piensa que nadie sabe ya cómo era de verdad, porque todas sus fotos se han corregido tantas veces. Piensa que el retrato de la ventana es una corrección de una corrección.",
        ),
        "inspeccion" to unDia(
            Parrafo.MANANA to "Hoy es la inspección anual de las viviendas, y desde muy temprano unos funcionarios de otro ministerio recorren el bloque piso por piso, con carpetas, abriendo armarios, contando sábanas, mirando debajo de las camas. Hay que estar en casa. Hay que tener todo abierto.",
            Parrafo.TRABAJO to "A esta hora espera de pie en su cuarto mientras dos inspectores abren sus cajones, cuentan sus camisas, hojean sus libros, golpean las paredes con los nudillos. Uno encuentra la caja de las cartas de su hermana. Las lee por encima. La cierra. Apunta algo.",
            Parrafo.PENSAMIENTO to "El funcionario piensa, cuando se van, en lo que apuntó el inspector, y en que no lo sabrá nunca. Piensa que da igual: todo lo que tiene está en la lista de lo que se puede tener. Piensa que eso es lo que le salva. Que no tiene nada.",
        ),
        "desfile" to unDia(
            Parrafo.MANANA to "Hoy es el Desfile de la Unidad, y desde el amanecer la avenida grande está llena de columnas: soldados, obreros, la Guardia Joven, los ministerios con sus estandartes. Ocho horas de marcha delante de una tribuna donde nadie sabe quién está. Cada ministerio tiene su sitio.",
            Parrafo.TRABAJO to "Hoy el funcionario no trabaja: desfila con su ministerio, en la fila catorce, llevando una pancarta con una cifra que él mismo corrigió el mes pasado. Marcha. Grita cuando hay que gritar. Al pasar por la tribuna, mira hacia arriba como todos. Ve unos sombreros. Nada más.",
            Parrafo.PENSAMIENTO to "El funcionario piensa, marchando, que en la avenida hay medio millón de personas gritando lo mismo, y que si alguien gritara otra cosa nadie lo oiría. Piensa que el desfile no es para que se vea. Es para que se oiga que todos dicen lo mismo.",
        ),
        "gratitud" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la Semana de la Gratitud, siete días en los que cada tarde, en todas las salas comunales, todos dan las gracias en voz alta al Estado por algo: la vivienda, el trabajo, la ración, la paz. Se apunta quién da las gracias y por qué. Hay que preparar qué decir.",
                Parrafo.GENTE to "Esta tarde, en la sala comunal del bloque, el funcionario se levanta cuando le toca y da las gracias por su trabajo en el ministerio, que le permite servir. Lo dice bien, con la voz justa. Se sienta. Le aplauden. El niño de la Guardia Joven apunta algo en su cuaderno.",
                Parrafo.PENSAMIENTO to "El funcionario piensa que dar las gracias por obligación es la cosa más rara del mundo, y que después de unos años uno ya no nota la diferencia. Piensa que a lo mejor él ya no la nota. Lo piensa y le da un escalofrío. Luego se le pasa.",
            )),
            sigue = mapOf(Parrafo.GENTE to listOf(
                "Esta tarde, en la Semana de la Gratitud, una vecina da las gracias por la medicina de su hijo, y se le quiebra la voz. Nadie sabe si es de gratitud o de otra cosa. Le aplauden más fuerte que a nadie.",
                "Esta tarde, en la sala comunal, el responsable de la planta lee la lista de los que no han venido a dar las gracias. Son dos. Nadie los mira cuando vuelven. Mañana vendrán.",
                "Esta tarde, en la Semana de la Gratitud, el funcionario da las gracias por la paz, como el año pasado. Nadie se acuerda de lo que dijo el año pasado. Él tampoco. Por si acaso, dice lo mismo.",
            )),
        ),
        "vocabulario" to unDia(
            Parrafo.MANANA to "Hoy sale la nueva edición del Vocabulario Oficial, un libro gordo de tapas rojas que se reparte en el ministerio, con trescientas palabras menos que la del año pasado. Hay que aprenderse cuáles ya no existen. Hay que dejar de usarlas desde hoy.",
            Parrafo.TRABAJO to "A esta hora repasa con su sección la lista de las palabras que se han quitado este año, y busca en sus expedientes dónde aparecen, para cambiarlas. Entre ellas hay palabras que él usaba de niño: una que quería decir la luz del atardecer, otra que era un tipo de pena. Las tacha.",
            Parrafo.PENSAMIENTO to "El funcionario piensa que con cada palabra que se quita se quita también lo que nombraba, y que dentro de unos años nadie sabrá que existía esa luz del atardecer, ni esa pena. Piensa que él la seguirá viendo. Piensa que no podrá decirlo.",
        ),
        "victoria" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Victoria Permanente, la fiesta grande del otoño, y en la plaza del ministerio hay un acto con cañonazos, banderas y un discurso sobre la guerra que se ganó, o que se gana, o que se ganará: el discurso no lo deja claro. Todos los ministerios forman en la plaza.",
            Parrafo.TRABAJO to "Hoy el funcionario no trabaja: está en la plaza, de pie, con su sección, cuatro horas, escuchando el discurso y los cañonazos, que hacen temblar los cristales de los edificios. Al final se canta el himno. Él lo canta entero, todas las estrofas. Hay quien se equivoca. Él, no.",
            Parrafo.PENSAMIENTO to "El funcionario piensa en la guerra, que no ha visto nunca, y que lleva toda la vida oyendo. Piensa que no sabe contra quién es, porque el enemigo cambia de nombre a veces. Piensa que a lo mejor no hay guerra. Piensa que eso no se puede pensar. Deja de pensarlo.",
        ),
        "cifras" to unDia(
            Parrafo.MANANA to "Hoy la pantalla anuncia, con una música triunfal, que la cosecha de este año ha superado el plan en un porcentaje que nadie entiende, y enseña campos dorados, tractores, campesinos sonrientes. En la tienda del bloque, esta mañana, no había patatas.",
            Parrafo.TRABAJO to "A esta hora corrige en el ministerio las cifras de la cosecha del año pasado, para que la de este año sea mejor: baja un número, sube otro, cambia una gráfica. Lo hace todos los años. El año que viene alguien corregirá las de este año. Así se crece.",
            Parrafo.PENSAMIENTO to "El funcionario piensa que las cifras de la cosecha son un idioma que solo hablan las pantallas, y que el estómago habla otro. Piensa que él sabe los dos. Piensa que eso es saber demasiado. Esa noche se toma una sopa de col sin patatas.",
        ),
        "lealtad" to unDia(
            Parrafo.MANANA to "Hoy es el examen de lealtad de todos los funcionarios del ministerio, que se hace cada año, uno por uno, en una sala del piso veinte con tres examinadores y una pantalla. Se pregunta de todo: de la historia, del Vocabulario, de los vecinos, de uno mismo.",
            Parrafo.TRABAJO to "A esta hora está sentado delante de los tres examinadores, contestando preguntas con la cara que hay que poner. Le preguntan por su hermana. Contesta que trabaja en la cooperativa. Le preguntan qué piensa de la última campaña. Contesta lo que dice el periódico. Apuntan algo.",
            Parrafo.PENSAMIENTO to "El funcionario piensa, al salir, que ha aprobado, porque aprueba todo el mundo menos los que no aprueban, y que esos no lo saben hasta que dejan de venir. Piensa en las caras de los examinadores. No se acuerda de ninguna. Eso es lo que quieren.",
        ),
        "calefaccion" to unDia(
            Parrafo.MANANA to "Hoy la pantalla anuncia que, por el bien común y para ahorrar carbón para la industria, la calefacción de las viviendas se cortará por las noches hasta la primavera. Lo anuncia una mujer sonriente con un jersey de lana. Hace tres grados en el cuarto.",
            Parrafo.GENTE to "Esta tarde, en la sala comunal, se reparten mantas del Estado a quien las pida: una por vivienda, delgada, gris, con un número. Se forma una cola. El funcionario coge la suya. La vecina de abajo, que tiene tres niños, coge una también. Una.",
            Parrafo.PENSAMIENTO to "El funcionario piensa que el frío es lo más democrático del Estado: entra en todas las casas por igual, menos en las de los que mandan, que tienen otra calefacción. Piensa que eso no es democrático. Se tapa con la manta hasta la nariz.",
        ),
        "fin_anio" to unDia(
            Parrafo.MANANA to "Hoy es el último día del año, y en todos los bloques se prepara la sala comunal para el brindis obligatorio de la medianoche: mesas con mantel, el retrato del Fundador con una guirnalda, la pantalla grande encendida para el discurso. Hay que estar todos.",
            Parrafo.ANOCHECER to "Esta noche, en la sala comunal, los vecinos esperan la medianoche de pie, con una copa en la mano, escuchando el discurso. A las doce se brinda por el Estado, por el Fundador, por la victoria. Se canta el himno. Luego cada uno sube a su casa. Nadie se queda.",
            Parrafo.PENSAMIENTO to "El funcionario piensa, con la copa en la mano, en el año que se va y en todo lo que corrigió en él: miles de papeles, cientos de nombres, decenas de cifras. Piensa que el año que se va ya no será el año que fue. Piensa que mañana alguien empezará a corregirlo.",
        ),

        // ---------- Los de la temporada 1 ----------
        "anselm" to unDia(
            Parrafo.MANANA to "Hoy Anselm, su compañero de la mesa de al lado, no ha venido a trabajar. Su silla está vacía, su máquina tapada, su tubo cerrado. A media mañana ha pasado alguien del personal y se ha llevado sus cosas en una caja. Nadie ha preguntado nada.",
            Parrafo.TRABAJO to "A esta hora le llega por el tubo una carpeta con una instrucción: buscar el nombre de Anselm en los registros de la sección y quitarlo. Lo encuentra en cuarenta sitios: listas, actas, la foto de la fiesta del año pasado. Lo quita de todos. En la foto, ahora, hay un hueco con una planta.",
            Parrafo.PENSAMIENTO to "El funcionario piensa en Anselm, que le guiñaba un ojo al pasarle las carpetas, y en que no sabrá nunca qué quería decir. Piensa que mañana nadie se acordará de él. Piensa que él sí. Piensa que eso es un crimen. Lo piensa muy bajito, por dentro.",
        ),
        "foto" to unDia(
            Parrafo.MANANA to "Hoy, corrigiendo un expediente de hace veinte años, el funcionario ha encontrado una foto que no debería existir: un grupo de hombres en una tribuna, y entre ellos uno que, según todos los libros, no estuvo nunca allí, porque fue un traidor desde siempre. Está sonriendo.",
            Parrafo.TRABAJO to "A esta hora tiene la foto en la mano, debajo de la mesa, mirándola, mientras el tubo trae otra carpeta. Tendría que devolverla para que la corrijan. Tendría que denunciarla. La mira un minuto más. Luego la mete en el sobre de las correcciones y la manda arriba.",
            Parrafo.PENSAMIENTO to "El funcionario piensa que esa foto era la prueba de que el pasado fue de otra manera, y que la tuvo un minuto en la mano y la soltó. Piensa que no podía hacer otra cosa. Piensa que sí podía. Piensa en eso toda la noche, con la pantalla mirándolo.",
        ),
        "carta_vera" to unDia(
            Parrafo.MANANA to "Hoy le ha llegado una carta de Vera, su hermana, la del mes, en el sobre gris del correo del Estado, abierto y vuelto a cerrar con una cinta. Una página. Dos líneas tachadas en negro, en el medio, tan gruesas que no se ve nada a través.",
            Parrafo.GENTE to "Esta tarde lee la carta de Vera tres veces en su cuarto: que los niños están bien, que la cosecha fue buena, que el marido trabaja mucho. Luego las dos líneas negras. Luego que lo echa de menos y que se abrigue. Intenta adivinar qué había debajo del negro.",
            Parrafo.PENSAMIENTO to "El funcionario piensa que lo más importante de la carta son las dos líneas que no puede leer, y que Vera lo sabía al escribirlas. Piensa que su hermana le ha mandado un mensaje que no es lo que dice, sino lo que no le han dejado decir. Lo ha entendido. Más o menos.",
        ),
        "apagon" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy, de madrugada, se ha ido la luz en todo el barrio, y no ha vuelto. Las pantallas están negras, por primera vez desde que el funcionario recuerda. Los ascensores, parados. El silencio en el bloque es tan grande que se oye llorar a un niño en el piso de abajo.",
                Parrafo.TRABAJO to "A esta hora está en el ministerio, que tiene su propia luz, corrigiendo expedientes como cualquier día, pero con la cabeza en su cuarto, donde la pantalla está apagada. Esta noche dormirá sin que nadie lo mire. Lo piensa todo el día. Le da casi miedo.",
                Parrafo.PENSAMIENTO to "El funcionario piensa, en la oscuridad de su cuarto, que hace años que no está solo de verdad, y que no sabe qué hacer con eso. Se sienta en la cama. Habla en voz alta, bajito, una frase cualquiera. Se asusta de su propia voz. Se calla.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "Anochece, y el barrio sigue a oscuras, con velas en algunas ventanas y nada en las demás. La gente sale a los portales a hablar, porque no hay nada que hacer dentro. Hablan de cosas que no se hablan. Bajito. Por si vuelve la luz de golpe.",
                "Anochece, y en el bloque a oscuras alguien toca una armónica en la escalera, una canción que no es ninguna marcha. Los vecinos escuchan desde sus puertas. Nadie dice nada. Cuando vuelva la luz, nadie se acordará de quién era.",
            )),
        ),
        "victoria_este" to unDia(
            Parrafo.MANANA to "Hoy la pantalla ha interrumpido todo a media mañana, con una fanfarria, para anunciar una gran victoria en el frente del este: miles de enemigos capturados, un río cruzado, una ciudad tomada cuyo nombre nadie había oído nunca. En el ministerio, todos de pie, aplaudiendo.",
            Parrafo.GENTE to "Esta tarde, en la sala comunal, se celebra la victoria con un vaso de ginebra por cabeza y canciones del frente. El responsable de planta dice que pronto se acabará la guerra. El funcionario recuerda que dijo lo mismo el año pasado. No lo recuerda en voz alta.",
            Parrafo.PENSAMIENTO to "El funcionario piensa que mañana le llegará por el tubo la orden de corregir los mapas de la sección, para que esa ciudad del este siempre haya sido del Estado. Piensa que en unos meses a lo mejor le llega la contraria. Piensa que guardará el mismo lápiz para las dos.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "funcionario", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + CAMPANA_MANANA + PROVINCIA_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + CAMPANA_TRABAJO + PROVINCIA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + CAMPANA_PUEBLO + PROVINCIA_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + CAMPANA_ANOCHECER + PROVINCIA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
