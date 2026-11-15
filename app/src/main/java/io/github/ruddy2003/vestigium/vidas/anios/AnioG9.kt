package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Arco
import io.github.ruddy2003.vestigium.vidas.Calendario
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.DiaEspecial
import io.github.ruddy2003.vestigium.vidas.Estado
import io.github.ruddy2003.vestigium.vidas.TemporadaVida
import io.github.ruddy2003.vestigium.vidas.TipoArco

/*
 * Los calendarios del grupo 9: las otras vidas de los temas del lote 10 (`docs/diseno/PLAN_TEMAS.md`, 10a a 10g). Por
 * ahora, la de Frente exterior (10c), Tadeo, el ladrón de piezas al que recluta la red; la de Arcontes (10d), Eleazar,
 * el salador de pescado de Magdala, y la de Galia (10e), Segomaros, el aprendiz de los druidas de Gergovia. Siguen las
 * reglas de `AnioPiloto.kt`; el de Tadeo, el estilo de los de ficción de `AnioG7.kt`, y los de Eleazar y Segomaros, el de
 * las personas inventadas en mundos reales (Zaynab y Bernat en `AnioG4.kt`).
 *
 * Lo propio de este grupo:
 *
 * - **Frente exterior es ficción** (`ficticia = true`): todo es inventado, también las fiestas, y nada lleva enlace.
 *   Toma el género de *Andor* (espionaje, resistencia, mundos ocupados, ciencia ficción de los años 70 hecha a mano),
 *   no la saga: ni sus planetas, ni su gente, ni sus naves, ni sus soldados, ni sus palabras. El régimen es el
 *   Directorio y la red, el Frente, que trabaja en células con una letra y un número.
 * - **Arcontes es una persona inventada en un mundo real** (10a): `ficticia = false`, y su calendario lleva días
 *   históricos y aproximados con su fuente, y lo de su vida como inventado. Su historia **avanza**: cada temporada es un
 *   año de su vida, del 28 al 32 d. C., y nada se repite (por eso cada día lleva su año en el id, también las fiestas,
 *   que cada año se cuentan de otra manera). Tiene el doble de días especiales que los demás (PLAN_TEMAS, 10d).
 * - **Galia también es una persona inventada en un mundo real**, con unos 20 días por temporada, como la mayoría. Su
 *   historia avanza del 54 al 50 a. C.: la escuela, la Galia inquieta, el levantamiento, las últimas resistencias y la
 *   Galia conquistada. Lo que vuelve casi cada año (el muérdago, la siembra, la asamblea de los druidas, la siega y
 *   Samonios) comparte su id y su texto; lo que cambia con la guerra lleva otro id.
 * - Los otros dos temas del lote (Art nouveau y Psicodelia) también son personas inventadas en mundos reales:
 *   cuando lleguen, sus calendarios podrán tener días históricos con su fuente.
 */
internal object AnioG9 {
    val TODOS: List<Calendario> by lazy { listOf(AGENTE, INICIADO, APRENDIZ) }

    /**
     * Días «históricos» sin enlace porque su fecha sale de un cálculo: en la ficción no hay ninguno, y en el calendario de
     * Eleazar lo calculado (las fiestas judías y la semana de la Pascua del 30) va como «aproximado», con su enlace.
     */
    val CALCULADOS = emptySet<String>()

    private val I = INVENTADO
    private val W = "https://en.wikipedia.org/wiki/"

    // =====================================================================
    //  Tadeo, ladrón de piezas y agente de la red. El puerto de Halde, años 212 a 216 del Directorio.
    // =====================================================================
    //
    // El mundo (todo inventado): Halde es un mundo minero que el Directorio exprime desde hace dos siglos. Su puerto es
    // de ladrillo: almacenes, grúas, un muelle de cargueros de mineral, el desguace donde se cortan las naves viejas
    // del Directorio, el mercado y el barrio de ladrillo donde vive la gente del puerto. Al norte lo cierra una sierra,
    // la Cresta; al este, en la montaña, la mina de la Compañía y, más abajo, el Hondo, una mina penal. Manda la
    // Prefectura, con su fachada de mármol blanco y bronce, que es lo único del Directorio que brilla. La Guardia lleva
    // abrigo gris y casco de acero con visera; la Oficina de Seguridad, abrigo azul oscuro. Las naves patrulla son un
    // bloque largo con un anillo, nunca un triángulo. Se paga con vales de la Compañía.
    //
    // La red (el Frente) trabaja en células que no se conocen entre sí. La de Tadeo es la K-7, y transmite cada noche
    // en una ventana de pocos minutos por una radio de maleta, con la consola escondida donde toque esa semana. Su
    // nombre en clave, desde la temporada 1, es Cobre.
    //
    // Su gente (toda inventada, y ninguna de ninguna saga): Odila, la relojera del mercado, que le compra piezas, lo
    // recluta y manda la célula; la Oficina quema su tienda y se la lleva a la mina penal (temporada 3), sale en la fuga
    // (4) y se va del planeta, enferma, en un carguero. Gaspar, un minero viejo de la célula, al que detienen en la 3 y
    // que muere en el Hondo ese invierno (se sabe en la 4). Lis, la radiooperadora, que llega en la 2. Sela, su hermana
    // menor, que trabaja en el lavadero de mineral; su hija Tula nace en la 2. Brun, el dueño del desguace, que paga
    // poco y no pregunta. Yusef, el del Pozo, el bar de abajo donde se pasan los mensajes. Abel, el del taller de
    // motores, que le alquila el cuarto de arriba. El inspector Morell, de la Oficina, que llega al puerto en la 3 y se
    // va en la 5.
    //
    // La historia de la red, de temporada en temporada: el reclutamiento (1), las primeras misiones (2), la caída de la
    // célula (3), la fuga de los presos del Hondo (4) y el golpe, la noche en que se ciega la estación de radar de la
    // Cresta y se para el puerto (5). Tadeo no muere ni se va de Halde.
    //
    // Claves de escena: fiesta, lamparas (los mineros bajan con sus lámparas), desfile (la Guardia formada en el muelle),
    // convoy (el muelle sin cargueros), luto, hogueras, casco (llega un casco nuevo al desguace), patrulla (una patrullera
    // baja sobre el puerto), pintadas (la chispa en los muros), humo, toque (calles vacías y reflectores), redada
    // (camiones de la Guardia y puertas abiertas), carteles (las fotos de los buscados) y golpe (la estación de radar
    // apagada y el puerto parado).
    //
    // Arcos: casco (lo que queda del casco del año en el desguace: llega entero cada 10 de marzo y se va comiendo),
    // ocupacion (cuánta Guardia hay en el puerto), crucero (1 = el crucero de anillo del Directorio está sobre el
    // puerto: de la fuga al golpe), relojeria (0 = la tienda de Odila entera; 1 = quemada; 0.3 = abierta otra vez como
    // taller de radios), chispa (cuántas chispas pintadas hay en los muros) y radar (0 = sin estación en la Cresta;
    // 1 = la estación encendida; 0.5 = ciega desde el golpe). Estados: «cresta» (temporada 2, cuatro días escondido junto
    // a la estación de radar; temporada 5, los tres días antes del golpe) y «escondido» (temporada 3, cuatro semanas en
    // las galerías viejas de la mina después de la redada).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento (el cuadro va después de la tanda S19): el
    // puerto de ladrillo con sus grúas y el desguace, con el casco según `casco`; la Guardia formada en el muelle en el
    // `desfile`; patrulleras de forma propia (un bloque largo con un anillo) y más Guardia según `ocupacion`; el crucero
    // de anillo, enorme, colgado sobre el puerto según `crucero`; la relojería del mercado entera, quemada o reabierta
    // según `relojeria`; la chispa pintada en los muros según `chispa`; y en la Cresta, al fondo, la estación de radar
    // según `radar`, con su luz que gira, y ciega desde el golpe. La ventana del cuarto de Tadeo, sobre el taller,
    // encendida en la ventana de transmisión; a oscuras y con el sello de la Oficina en la puerta durante «escondido»; y
    // a oscuras durante «cresta», con la luz de la estación que parpadea en la sierra.

    /** Lo que vuelve cada año en el puerto. En la temporada [t] 3 el censo lo pilla escondido, y lleva otro id. */
    private fun anioDeHalde(t: Int): List<DiaEspecial> = listOf(
        fiesta("cambio_anio", "01-01", "Cambia el año del Directorio: todas las sirenas del puerto suenan a la vez", certeza = I),
        rito("cuota", "02-05", "La Prefectura anuncia la cuota de mineral del año, más alta que la del anterior", certeza = I),
        rito("casco_nuevo", "03-10", "Llega remolcado al desguace el casco de una nave vieja del Directorio", certeza = I, escena = "casco"),
        fiesta("lamparas", "04-18", "La Noche de las Lámparas: los mineros bajan al puerto con la lámpara encendida", certeza = I, escena = "lamparas"),
        fiesta("concordia", "06-12", "El Día de la Concordia: la Guardia del Directorio forma en el muelle", certeza = I, escena = "desfile"),
        mundo("convoy", "07-05", "Sale el convoy de mineral hacia la Capital, y el puerto se queda sin cargueros", certeza = I, escena = "convoy"),
        fiesta("feria", "08-14", "La feria de la chatarra: tres días de puestos entre los cascos del desguace", 3, I),
        rito("caidos", "09-21", "El día de los caídos de la mina: una sirena larga y un minuto sin trabajo", certeza = I, escena = "luto"),
        if (t == 3) rito("censo_escondido", "10-15", "Es el día del censo, y él no está: su nombre se queda en blanco en la lista", certeza = I)
        else rito("censo", "10-15", "El censo del Directorio: cola en la Prefectura con la credencial en la mano", certeza = I),
        rito("carbon", "11-18", "La Prefectura reparte los vales de carbón para el invierno", certeza = I),
        fiesta("noche_larga", "12-21", "La Noche Larga: hogueras en los patios del barrio de ladrillo", certeza = I, escena = "hogueras"),
    )

    val AGENTE = Calendario(
        vida = "agente_red",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 3, nubes = 12, lluvia = 4, nieve = 6, niebla = 5, viento = 2), mes(despejado = 3, nubes = 11, lluvia = 4, nieve = 5, niebla = 5, viento = 3),
            mes(despejado = 4, nubes = 11, lluvia = 7, nieve = 2, niebla = 4, viento = 4, polvo = 1), mes(despejado = 6, nubes = 10, lluvia = 8, niebla = 3, viento = 3, polvo = 2),
            mes(despejado = 8, nubes = 9, lluvia = 7, tormenta = 1, niebla = 2, viento = 2, polvo = 3), mes(despejado = 10, nubes = 8, lluvia = 5, tormenta = 2, niebla = 1, viento = 2, polvo = 4),
            mes(despejado = 11, nubes = 7, lluvia = 4, tormenta = 3, viento = 2, polvo = 5), mes(despejado = 10, nubes = 7, lluvia = 5, tormenta = 3, viento = 2, polvo = 5),
            mes(despejado = 7, nubes = 10, lluvia = 7, tormenta = 1, niebla = 3, viento = 3, polvo = 2), mes(despejado = 5, nubes = 11, lluvia = 8, niebla = 5, viento = 3, polvo = 1),
            mes(despejado = 3, nubes = 12, lluvia = 7, nieve = 2, niebla = 6, viento = 3), mes(despejado = 3, nubes = 12, lluvia = 4, nieve = 6, niebla = 5, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("año 212 del Directorio", "cuota",
                "Tadeo saca de una patrullera desguazada una caja de navegación y se la vende a Odila, la relojera del mercado, que lo recluta para la red. Lleva su primer recado, conoce a Gaspar y oye su primera ventana de transmisión. Un hombre de la Oficina lo sigue por el mercado. Una explosión en la mina mata a doce. Aparece la chispa pintada en el muelle. Sela espera un hijo. En diciembre le dan su nombre en clave en la célula K-7: Cobre.",
                anioDeHalde(1) + listOf(
                    suceso("la_caja", "01-18", "Saca del casco de una patrullera desguazada una caja de navegación sellada"),
                    suceso("relojera", "01-30", "Odila, la relojera del mercado, le compra la caja sin preguntar de dónde salió"),
                    suceso("recluta", "02-24", "Odila le dice para quién compra lo que compra: para la red"),
                    suceso("primer_recado", "03-28", "Su primer recado para la red: una caja de válvulas que no son válvulas"),
                    mundo("patrullera_nueva", "05-08", "Llega al puerto una patrullera nueva, más larga que las otras, con el anillo encendido", certeza = I, escena = "patrulla"),
                    suceso("primera_ventana", "05-26", "Oye su primera ventana de transmisión en el cuarto de atrás de la relojería"),
                    suceso("seguido", "07-22", "Un hombre de la Oficina lo sigue por el mercado, y lo pierde entre los puestos"),
                    mundo("explosion", "08-30", "Una explosión en la galería nueva de la mina: mueren doce mineros", certeza = I, escena = "luto"),
                    suceso("sela_espera", "09-08", "Sela, su hermana, le cuenta que espera un hijo"),
                    mundo("chispa", "10-28", "Amanece pintada en el muro del muelle una chispa dentro de un círculo roto", certeza = I, escena = "pintadas"),
                    suceso("juramento", "12-06", "Le dan su nombre en clave en la célula K-7: Cobre"),
                )),
            TemporadaVida("año 213 del Directorio", "patrullas",
                "Nace Tula, la hija de Sela. Llega a la célula Lis, una radiooperadora de diecinueve años. El Directorio enciende una estación de radar en la Cresta. Tadeo copia el horario de las patrullas en la torre del puerto y sube con Gaspar cuatro días a la Cresta a copiar las frecuencias de la estación. La célula descarrila un tren de mineral vacío, sin muertos, y la Prefectura pone tres semanas de toque de queda. Odila le enseña a cifrar. La Guardia registra el Pozo.",
                anioDeHalde(2) + listOf(
                    suceso("nace_tula", "02-14", "Nace Tula, la hija de Sela, en el cuarto de encima del lavadero"),
                    suceso("llega_lis", "03-22", "Llega a la célula Lis, una radiooperadora de diecinueve años"),
                    mundo("radar", "04-30", "El Directorio enciende una estación de radar en la Cresta, sobre el puerto", certeza = I),
                    suceso("torre", "05-19", "Copia el horario de las patrullas en la torre de control del puerto"),
                    suceso("sube_cresta", "07-14", "Sube con Gaspar a la Cresta a copiar las frecuencias de la estación de radar", cadena = "cresta"),
                    suceso("baja_cresta", "07-18", "Baja de la Cresta con las frecuencias en un microfilm, dentro del tacón", cadena = "cresta"),
                    guerra("tren", "09-03", "La célula descarrila un tren de mineral vacío: no muere nadie", certeza = I, escena = "humo"),
                    mundo("toque", "09-04", "Toque de queda en el barrio de ladrillo: nadie en la calle después de las diez", 21, I, escena = "toque"),
                    suceso("cifrar", "10-24", "Odila le enseña a cifrar con la llave de cuarenta columnas"),
                    mundo("redada_pozo", "11-26", "La Guardia registra el Pozo y se lleva a dos estibadores", certeza = I, escena = "redada"),
                )),
            TemporadaVida("año 214 del Directorio", "caida",
                "Llega al puerto el inspector Morell, de la Oficina de Seguridad, y pone controles en el mercado. Tula da sus primeros pasos. Detienen a Gaspar en el muelle con una radio en el carro. La Oficina quema la relojería y se lleva a Odila. Tadeo pasa cuatro semanas escondido en las galerías viejas de la mina; mientras, interrogan a Sela y la sueltan. Salen en el muelle las fotos de los buscados, y la suya no está. En diciembre llega un mensaje: Odila está viva en la mina penal del Hondo.",
                anioDeHalde(3) + listOf(
                    mundo("llega_morell", "02-27", "Llega al puerto el inspector Morell, de la Oficina de Seguridad", certeza = I, escena = "patrulla"),
                    suceso("tula_camina", "03-30", "Tula da sus primeros pasos en el patio, agarrada a su dedo"),
                    mundo("controles", "05-03", "La Oficina pone controles de credenciales en todas las salidas del mercado", certeza = I, escena = "redada"),
                    guerra("cae_gaspar", "06-24", "Detienen a Gaspar en el muelle, con una radio de maleta en el carro", certeza = I, escena = "redada"),
                    suceso("quema_papeles", "07-02", "Quema en la estufa todo lo que Gaspar podría nombrar"),
                    guerra("redada", "10-02", "La Oficina entra en la relojería, le prende fuego y se lleva a Odila", certeza = I, escena = "humo"),
                    suceso("sela_interrogada", "10-09", "Interrogan a Sela un día entero en la Prefectura, y la sueltan"),
                    suceso("sale_tuneles", "10-30", "Sale de las galerías de la mina, flaco y con la barba crecida"),
                    mundo("buscados", "11-20", "La Oficina pega en el muelle las fotos de los buscados: la suya no está", certeza = I, escena = "carteles"),
                    suceso("noticia_odila", "12-12", "Un mensaje cifrado: Odila está viva, en la mina penal del Hondo"),
                )),
            TemporadaVida("año 215 del Directorio", "ocupacion",
                "Tadeo vuelve a juntar a la célula en el sótano del Pozo: son tres. Se entera de que Gaspar murió en el Hondo. Encuentra en las galerías viejas el pozo de ventilación que da a la mina penal, y lo mide de noche. La noche de la fuga salen por él cuarenta presos, Odila entre ellos. El Directorio manda un crucero de anillo que se queda sobre el puerto, y la Guardia registra el barrio casa por casa. Odila, enferma, se va del planeta en un carguero. Morell lo interroga seis horas y lo suelta. La Prefectura pone precio al agente al que llaman Cobre.",
                anioDeHalde(4) + listOf(
                    suceso("rearma", "01-25", "Vuelve a juntar a la célula en el sótano del Pozo: son tres"),
                    suceso("noticia_gaspar", "03-04", "Se entera de que Gaspar murió en el invierno, en la mina penal", escena = "luto"),
                    suceso("pozo_ventilacion", "04-05", "Encuentra en las galerías viejas el pozo de ventilación que da al Hondo"),
                    suceso("medir_pozo", "05-20", "Baja de noche a medir el pozo de ventilación con una cuerda y una linterna"),
                    suceso("tula_habla", "06-02", "Tula le dice «tío» por primera vez"),
                    guerra("fuga", "07-09", "La noche de la fuga: cuarenta presos salen del Hondo por el pozo de ventilación", 2, I, escena = "toque"),
                    mundo("crucero", "07-12", "Llega el crucero de anillo del Directorio y se queda sobre el puerto", certeza = I, escena = "patrulla"),
                    mundo("registros", "07-13", "La Guardia registra el barrio de ladrillo casa por casa", 10, I, escena = "redada"),
                    suceso("odila_parte", "08-25", "Odila, enferma, se va del planeta escondida en un carguero de mineral"),
                    suceso("interrogatorio", "10-06", "Morell lo interroga seis horas en la Prefectura, y lo deja ir"),
                    mundo("precio", "11-10", "La Prefectura pone precio al agente del Frente al que llaman Cobre", certeza = I, escena = "carteles"),
                )),
            TemporadaVida("año 216 del Directorio", "golpe",
                "Llega un enlace de otra célula con una carta estelar y una fecha: el Frente prepara un golpe en muchos mundos a la vez, y a la K-7 le toca cegar la estación de radar de la Cresta. Tula empieza la escuela. Los estibadores votan una huelga. Tadeo sube con Lis y pasan tres días escondidos junto a la estación. La noche del golpe se apaga el radar y se para el puerto; el crucero de anillo se va. Cambian al prefecto, y Morell se va. Lis abre en la relojería quemada un taller de radios. Sela le pide que deje la red, y él no la deja. Le llega una carta de Odila.",
                anioDeHalde(5) + listOf(
                    suceso("enlace", "02-17", "Llega un enlace de otra célula con una carta estelar y una fecha"),
                    suceso("tula_escuela", "03-16", "Tula empieza la escuela de la Prefectura, y él la lleva el primer día"),
                    mundo("huelga_aviso", "05-12", "Los estibadores votan en secreto una huelga para el día que diga la red", certeza = I),
                    guerra("sube_golpe", "06-26", "Sube con Lis a la Cresta: tres días escondidos junto a la estación de radar", certeza = I),
                    guerra("golpe", "06-29", "La noche del golpe: se apaga la estación de radar y se para el puerto", 2, I, escena = "golpe"),
                    mundo("se_va_crucero", "07-03", "El crucero de anillo enciende los motores y se va del cielo del puerto", certeza = I, escena = "fiesta"),
                    mundo("prefecto_nuevo", "07-20", "Cambian al prefecto, y el nuevo suelta a los estibadores de la huelga", certeza = I),
                    mundo("se_va_morell", "08-05", "El inspector Morell se va del puerto: lo llaman a la Capital", certeza = I),
                    mundo("relojeria_abre", "09-10", "Lis abre en la relojería quemada un taller de radios", certeza = I),
                    suceso("sela_pide", "10-20", "Sela le pide que deje la red, y él le dice que no"),
                    suceso("carta_odila", "11-24", "Le llega una carta de Odila desde otro mundo, sin firma"),
                )),
        ),
        arcos = listOf(
            // El casco del año: llega entero cada 10 de marzo y el desguace se lo va comiendo hasta el siguiente.
            Arco.cadaAnio("casco", TipoArco.OBRA, "01-01" to 0.12, "03-09" to 0.0, "03-10" to 1.0, "12-31" to 0.12, saltos = setOf("03-10")),
            Arco("ocupacion", TipoArco.OTRO, listOf(
                p(1, "01-01", 0.3), p(3, "02-26", 0.35), p(3, "02-27", 0.42, salto = true), p(3, "05-02", 0.44), p(3, "05-03", 0.55, salto = true),
                p(3, "10-02", 0.7, salto = true), p(4, "07-11", 0.7), p(4, "07-12", 1.0, salto = true), p(5, "07-02", 1.0), p(5, "07-03", 0.25, salto = true), p(5, "12-31", 0.2),
            )),
            Arco("crucero", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(4, "07-12", 1.0, salto = true), p(5, "07-03", 0.0, salto = true))),
            Arco("relojeria", TipoArco.DANOS, listOf(p(1, "01-01", 0.0), p(3, "10-02", 1.0, salto = true), p(5, "09-10", 0.3, salto = true), p(5, "12-31", 0.3))),
            Arco("chispa", TipoArco.ADORNOS, listOf(
                p(1, "01-01", 0.0), p(1, "10-28", 0.1, salto = true), p(3, "02-27", 0.5), p(3, "10-02", 0.2, salto = true), p(4, "07-09", 0.3), p(4, "12-31", 0.6),
                p(5, "07-02", 0.6), p(5, "07-03", 1.0, salto = true), p(5, "12-31", 0.9),
            )),
            Arco("radar", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(2, "04-30", 1.0, salto = true), p(5, "06-29", 0.5, salto = true), p(5, "12-31", 0.5))),
        ),
        objetos = listOf("cartucho", "radio", "llave_cifrado", "credencial", "auricular", "microfilm", "carta_estelar", "soplete", "linterna", "chaqueta", "valvula", "ficha"),
        estados = listOf(Estado("cresta", "sube_cresta", "baja_cresta"), Estado("escondido", "redada", "sale_tuneles"), Estado("cresta", "sube_golpe", "golpe")),
    )

    // =====================================================================
    //  Eleazar, salador de pescado e iniciado de un círculo escondido. Magdala, en el lago de Galilea, del 28 al 32 d. C.
    // =====================================================================
    //
    // La ficha es la de PLAN_TEMAS, 10d («La historia del iniciado: los eventos especiales»), con las correcciones de
    // `docs/diseno/temas/fuentes_arcontes.md` (sección 1). Lo que allí va como «[sin verificar]» no se afirma aquí.
    //
    // Su gente (toda inventada): Mara, su mujer; Natán, su hijo (unos seis años en la temporada 1); Débora, la hija que
    // nace en la 2; Shifra, su madre, viuda, que vive con ellos y muere en el invierno de la 4. El viejo Ezequías, dueño
    // del secadero de la orilla donde trabaja, que lo hace su socio en la 4. Los del círculo, «los que saben»: Ismael, el
    // viejo que vino de Alejandría y enseña (se va quedando ciego desde la 3), Judit, una viuda que teje lino y trae hojas
    // copiadas de Tiro, y Tobías, el barquero. La gente real que cruza su vida no se dibuja nunca: Juan el Bautista, María
    // de Magdala (su vecina), Pedro, Felipe, Esteban, Saulo y Simón de Samaria. Lo que hacen ellos sale de los evangelios y
    // de Hechos, con su cita; lo que hacen con él (lo que María le cuenta, lo que habla con los de Pedro) es inventado.
    //
    // Lo real: el año judío (Purim, Pésaj y la primera gavilla, Shavuot, Rosh Hashaná, Yom Kipur, Sucot y Janucá), la vida
    // de Jesús según los evangelios y lo que vino después según Hechos, Herodes Antipas, Pilato y la caída de Sejano. Las
    // creencias del círculo son las de los textos gnósticos (siglos II a IV) puestas a propósito en el siglo I, como decidió
    // el autor; el cuento las cuenta como lo que él cree.
    //
    // Las fechas:
    // - Van en juliano, el calendario de entonces, escritas como «MM-DD» (como las de Zaynab). Cada fiesta empieza al
    //   ponerse el sol del día anterior; la fecha es la de su parte de luz.
    // - Las fiestas salen de la tabla de `fuentes_arcontes.md` (sección 6), calculada con la luna nueva y la primera luna
    //   visible en Jerusalén: van como «aproximado», porque el mes empezaba cuando se veía la luna (un día de margen, y un
    //   mes si los sacerdotes añadían otro adar). Hay adar II en el 29 y en el 32. El Purim del 28 cayó el 29 de febrero,
    //   que el calendario no puede escribir: va el 1 de marzo, el 15 de adar, el Purim de Jerusalén. Shavuot va con la
    //   cuenta de los fariseos (el 6 de siván), salvo en el 30, en que las dos cuentas dan el domingo 28 de mayo.
    // - El año quince de Tiberio (Lucas 3:1), con la cuenta romana, empieza en el otoño del 28: Juan bautiza desde
    //   entonces, y el bautismo de Jesús, la llamada a los pescadores y Cafarnaúm van a finales del 28. Marcos (1:14) pone
    //   la llamada después de que encierran a Juan, así que la cárcel de Juan va en diciembre del 28 y su muerte, antes de
    //   los panes de la Pascua del 29, como en Marcos 6 (los estudiosos dan del 29 al 32).
    // - La semana de la Pascua del 30 sigue la cronología de Juan: la crucifixión, el viernes 14 de nisán, 7 de abril (una
    //   de las dos fechas más defendidas; según Riesner, al parecer la de la mayoría); la entrada, el domingo 2; los
    //   mercaderes del Templo, el lunes 3 (en el 30, no en el 28); la cena, el jueves 6; el sepulcro vacío, el domingo 9.
    //   Pentecostés, el domingo 28 de mayo. La ascensión, cuarenta días después del 9 de abril (Hechos 1:3).
    // - Lo que los evangelios y Hechos no fechan va como «aproximado», en el orden de Marcos y de Hechos y con su cita.
    //   **Esteban va adelantado**: lo habitual es ponerlo hacia el 34 (su muerte, entre el 33 y el 36); aquí va en junio
    //   del 32, como pide la ficha. La noticia de Sejano (Roma, 18 de octubre del 31) llega entre finales de noviembre y
    //   diciembre. Helena no está en Samaria: en el 32 Eleazar solo oye a Simón y su «gran poder» (corrección 9).
    // - Algunos hechos los ve sin saber qué son: en `luz_monte_29` ve una luz en lo alto de un monte, la noche de la
    //   transfiguración (Marcos 9:2–8, que no dice qué monte), y no sabrá lo que fue hasta que alguien se lo cuente.
    // - Sus viajes van por el valle del Jordán (cuatro días de camino), salvo los de la temporada 5, por Samaria (tres).
    // - La huida (temporada 5) sale de Jerusalén la noche del viernes 6 de junio, después del entierro de Esteban, y llega
    //   a la ciudad de Samaria el lunes 9 (unos dos días a pie, con un día escondidos cerca de Siquem). Los tres días del
    //   camino son especiales (`dispersion_32`, `saulo_casas_32` y `samaria_32`), sin días normales en medio, porque las
    //   variantes de «huida» del cuento son las de la ciudad.
    //
    // Claves de escena: fiesta, lamparas (las luces de Janucá), sucot (las cabañas de ramas), luto, tormenta, humo,
    // secadero (los cañizos llenos de pescado abierto), cosecha, grieta (las noches del círculo en que se abre la luz),
    // camino (los días de camino a Jerusalén y de vuelta), jordan (el vado del Jordán), oscuridad (el cielo que se apaga la
    // tarde de la cruz), luz_monte (una luz en lo alto de un monte, de noche), huida y **figura_luz** (decisión 18): los días
    // en que Eleazar ve a Jesús, que en el cuadro es una figura de luz de espaldas, sin rasgos, blanca con borde violeta,
    // lejos, en la orilla, sobre el agua o entre la gente (que no se dibuja).
    //
    // Arcos: secadero (cuánto pescado hay tendido al sol: más en invierno y primavera, menos en lo peor del verano;
    // **[Aproximación]**), barcas (las barcas y las hogueras de la gente que sigue a Jesús por la orilla de Magdala: nada
    // hasta diciembre del 28, lo más en la Pascua del 29, y nada desde la cruz; no se dibuja a la gente, solo sus barcas y
    // sus fuegos), codice (las hojas que el círculo va copiando, que crecen poco a poco), cueva (0 = el círculo se reúne en
    // la casa de Ismael; 1 = en una cueva del Arbel, desde noviembre del 31) y jarra (1 = las hojas, selladas en una jarra en
    // lo hondo de la cueva, desde octubre del 32).
    //
    // Estados: «jerusalen» (sus subidas a Jerusalén: Sucot del 28 y del 29, Pésaj y Shavuot del 30, Pésaj y Sucot del 31,
    // Pésaj y Shavuot del 32; del día en que sale de Magdala a la víspera del día en que vuelve, con los días de camino) y
    // «huida» (temporada 5: de la persecución que sigue a Esteban a la víspera de Rosh Hashaná, escondido en la ciudad de
    // Samaria). En la temporada 5, que desde 2032 se repite, los días que abren y cierran los estados son ritos, fiestas y
    // sucesos del mundo, que no se mueven; sus sucesos personales van en enero y febrero o desde mediados de noviembre, lejos
    // de los viajes, para que al sortearse de nuevo no caigan en Jerusalén ni en Samaria.
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento (el cuadro de Arcontes va después de la tanda
    // S19): la orilla de Magdala al atardecer, con su torre, las barcas varadas, el secadero con sus cañizos según
    // `secadero`, las ánforas, el Arbel detrás y, en los días claros, el Hermón al norte; las barcas y las hogueras de la
    // gente según `barcas`; la figura de luz, de espaldas, los días `figura_luz`; las cabañas de ramas en los tejados en
    // Sucot y las lámparas en Janucá. De noche, a la hora de la reunión (19 a 21 h), el sitio del círculo con la lámpara de
    // aceite, las hojas del códice según `codice` y la grieta de luz: el cuarto de Ismael y, desde `cueva`, la cueva del
    // Arbel; con `jarra`, la jarra sellada en el fondo de la cueva. Durante «jerusalen», Jerusalén (lugar propio, porque
    // vuelve cada año: decisión 21): el Templo al fondo, el monte de los Olivos con los fuegos de los peregrinos y, los días
    // `camino`, el camino del valle del Jordán. Durante «huida», la ciudad de Samaria y sus montes, con las eras y las viñas
    // (lugar propio: dura casi cuatro meses).

    private val F_PURIM = "Purim" to "${W}Purim"
    private val F_PESAJ = "Pésaj" to "${W}Passover"
    private val F_SHAVUOT = "Shavuot" to "${W}Shavuot"
    private val F_ROSH = "Rosh Hashaná" to "${W}Rosh_Hashanah"
    private val F_KIPUR = "Yom Kipur" to "${W}Yom_Kippur"
    private val F_SUCOT = "Sucot" to "${W}Sukkot"
    private val F_JANUCA = "Janucá" to "${W}Hanukkah"
    private val F_CRONOLOGIA = "La cronología de Jesús" to "${W}Chronology_of_Jesus"
    private val F_JUAN_BAUTISTA = "Juan el Bautista" to "${W}John_the_Baptist"
    private val F_MAQUERONTE = "Maqueronte" to "${W}Machaerus"
    private val F_MARCOS_1 = "Marcos 1" to "${W}Mark_1"
    private val F_MARCOS_4 = "Marcos 4" to "${W}Mark_4"
    private val F_MARCOS_5 = "Marcos 5" to "${W}Mark_5"
    private val F_MARCOS_6 = "Marcos 6" to "${W}Mark_6"
    private val F_MARCOS_8 = "Marcos 8" to "${W}Mark_8"
    private val F_MARCOS_10 = "Marcos 10" to "${W}Mark_10"
    private val F_MARCOS_11 = "Marcos 11" to "${W}Mark_11"
    private val F_LUCAS_8 = "Lucas 8" to "${W}Luke_8"
    private val F_MATEO_11 = "Mateo 11" to "${W}Matthew_11"
    private val F_JUAN_6 = "Juan 6" to "${W}John_6"
    private val F_JUAN_7 = "Juan 7" to "${W}John_7"
    private val F_JUAN_10 = "Juan 10" to "${W}John_10"
    private val F_JUAN_11 = "Juan 11" to "${W}John_11"
    private val F_JUAN_20 = "Juan 20" to "${W}John_20"
    private val F_JUAN_21 = "Juan 21" to "${W}John_21"
    private val F_TEMPESTAD = "La tempestad calmada" to "${W}Calming_the_storm"
    private val F_PANES = "Los panes y los peces" to "${W}Feeding_the_multitude"
    private val F_AGUA = "Jesús camina sobre el agua" to "${W}Jesus_walking_on_water"
    private val F_TRANSFIGURACION = "La transfiguración" to "${W}Transfiguration_of_Jesus"
    private val F_PILATO = "Poncio Pilato" to "${W}Pontius_Pilate"
    private val F_ENTRADA = "La entrada en Jerusalén" to "${W}Triumphal_entry_into_Jerusalem"
    private val F_MERCADERES = "Los mercaderes del Templo" to "${W}Cleansing_of_the_Temple"
    private val F_CENA = "La última cena" to "${W}Last_Supper"
    private val F_CRUZ = "La crucifixión de Jesús" to "${W}Crucifixion_of_Jesus"
    private val F_HECHOS_1 = "Hechos 1" to "${W}Acts_1"
    private val F_PENTECOSTES = "Pentecostés" to "${W}Pentecost"
    private val F_HECHOS_2 = "Hechos 2" to "${W}Acts_2"
    private val F_HECHOS_3 = "Hechos 3" to "${W}Acts_3"
    private val F_HECHOS_4 = "Hechos 4" to "${W}Acts_4"
    private val F_HECHOS_5 = "Hechos 5" to "${W}Acts_5"
    private val F_HECHOS_6 = "Hechos 6" to "${W}Acts_6"
    private val F_HECHOS_8 = "Hechos 8" to "${W}Acts_8"
    private val F_HECHOS_9 = "Hechos 9" to "${W}Acts_9"
    private val F_GAMALIEL = "Gamaliel" to "${W}Gamaliel"
    private val F_SIETE = "Los siete" to "${W}Seven_Deacons"
    private val F_SEJANO = "Sejano" to "${W}Sejanus"
    private val F_ESTEBAN = "Esteban" to "${W}Saint_Stephen"
    private val F_FELIPE = "Felipe el evangelista" to "${W}Philip_the_Evangelist"
    private val F_SIMON = "Simón de Samaria" to "${W}Simon_Magus"

    private val A = APROXIMADO
    private val INV = INVENTADO

    val INICIADO = Calendario(
        vida = "iniciado",
        estaciones = Calendario.TEMPLADO_NORTE,
        // La orilla del lago, a más de doscientos metros bajo el nivel del mar: lluvias de noviembre a marzo, con tormentas
        // que bajan de golpe de los montes; un verano largo, seco y caliente; y en primavera y otoño, el viento del desierto.
        clima = listOf(
            mes(despejado = 9, nubes = 9, lluvia = 9, tormenta = 2, niebla = 1, viento = 1), mes(despejado = 10, nubes = 9, lluvia = 8, tormenta = 2, niebla = 1, viento = 1),
            mes(despejado = 14, nubes = 8, lluvia = 6, tormenta = 1, viento = 2, polvo = 1), mes(despejado = 18, nubes = 6, lluvia = 3, viento = 2, polvo = 2),
            mes(despejado = 24, nubes = 3, lluvia = 1, viento = 2, polvo = 2), mes(despejado = 27, nubes = 1, viento = 3),
            mes(despejado = 28, nubes = 1, viento = 3), mes(despejado = 28, nubes = 1, viento = 2),
            mes(despejado = 25, nubes = 2, viento = 2, polvo = 2), mes(despejado = 20, nubes = 5, lluvia = 3, tormenta = 1, viento = 2, polvo = 1),
            mes(despejado = 13, nubes = 8, lluvia = 6, tormenta = 2, viento = 1), mes(despejado = 10, nubes = 9, lluvia = 8, tormenta = 2, niebla = 1, viento = 1),
        ),
        temporadas = listOf(
            TemporadaVida("28 d. C.", "antipas",
                "Eleazar sala pescado en el secadero de Ezequías y va de noche al círculo de Ismael, que en primavera le enseña las palabras de las siete puertas. Su vecina María oye voces. En el otoño llega la noticia de Juan, que bautiza en el Jordán: Eleazar sube a Jerusalén para Yom Kipur y Sucot y, a la vuelta, lo ve en el vado. En noviembre vuelve al Jordán con Tobías y ve bautizar a un hombre de Nazaret. Herodes encierra a Juan. El de Nazaret vuelve a Galilea, llama a cuatro pescadores en Cafarnaúm, y Eleazar lo ve de lejos, de espaldas. Mara espera un hijo.",
                listOf(
                    suceso("hermon_28", "01-09", "Ismael le enseña el Hermón nevado: allí, dice, bajaron los Vigilantes"),
                    mundo("tormenta_28", "01-27", "Una tormenta baja de golpe de los montes y vuelca dos barcas en el lago", certeza = INV, escena = "tormenta", clima = Clima.TORMENTA),
                    cosecha("banco_28", "02-14", "Un banco de peces llena las redes: tres días abriendo pescado sin parar", 3, INV, escena = "secadero"),
                    fiesta("purim_28", "03-01", "Segundo día de Purim, el que se guarda en Jerusalén: platos de casa en casa", certeza = A, fuente = F_PURIM),
                    suceso("fiebre_28", "03-12", "Shifra, su madre, cae con fiebre y no se levanta de la estera", cadena = "madre_28"),
                    suceso("sana_28", "03-20", "Shifra se levanta de la estera y pide pescado asado", cadena = "madre_28"),
                    fiesta("pesaj_28", "03-30", "Pésaj: este año no suben a Jerusalén, y la noche de los ácimos es en casa", certeza = A, fuente = F_PESAJ),
                    cosecha("cebada_28", "04-01", "Empieza la siega de la cebada, y en el Templo se ofrece la primera gavilla", 1, A, F_SHAVUOT),
                    rito("sello_28", "04-14", "La noche del sello: Ismael le enseña las palabras de las siete puertas", certeza = INV, escena = "grieta"),
                    mundo("caravana_28", "05-06", "La caravana de Damasco se lleva cien ánforas de pescado del secadero", certeza = INV),
                    fiesta("shavuot_28", "05-20", "Shavuot, la fiesta de las semanas: el trigo nuevo", certeza = A, fuente = F_SHAVUOT),
                    suceso("esferas_28", "06-08", "Ismael dibuja en la arena los siete círculos del cielo y la serpiente de fuera"),
                    mundo("sharav_28", "06-25", "Sopla tres días el viento caliente del desierto, y el pescado se seca de más", 3, INV, clima = Clima.POLVO),
                    mundo("cobradores_28", "07-15", "Los cobradores de Herodes Antipas cuentan una por una las ánforas del secadero", certeza = INV),
                    suceso("voces_28", "07-30", "De noche se oye a María, la vecina, hablar con voces que no son la suya"),
                    suceso("natan_28", "08-20", "Natán abre su primer pescado con el cuchillo de su abuelo"),
                    mundo("rumor_juan_28", "09-06", "Llega a Magdala la noticia de un profeta que bautiza en el Jordán", certeza = A, fuente = F_CRONOLOGIA),
                    fiesta("rosh_28", "09-09", "Rosh Hashaná, el año nuevo: suena el cuerno en la sinagoga", certeza = A, fuente = F_ROSH),
                    rito("sube_sucot_28", "09-12", "Sube a Jerusalén con los peregrinos de Magdala, para Yom Kipur y Sucot", 4, INV, escena = "camino"),
                    rito("kipur_28", "09-18", "Yom Kipur en el Templo: ayuno, y el macho cabrío que se va al desierto", certeza = A, fuente = F_KIPUR),
                    fiesta("sucot_28", "09-23", "Sucot en Jerusalén: siete días en una cabaña de ramas", 7, A, F_SUCOT, escena = "sucot"),
                    rito("baja_sucot_28", "10-01", "Baja de Jerusalén por Jericó y el valle del Jordán", 4, INV, escena = "camino"),
                    mundo("juan_vado_28", "10-03", "En un vado del Jordán ve a Juan, el que bautiza, metido en el agua", certeza = A, fuente = F_JUAN_BAUTISTA, escena = "jordan"),
                    rito("vuelve_sucot_28", "10-05", "Vuelve a Magdala con el polvo del camino y con Juan en la cabeza", certeza = INV),
                    suceso("agua_28", "10-20", "En el círculo discuten el agua de Juan: Ismael dice que es agua de abajo"),
                    suceso("baja_jordan_28", "11-04", "Baja otra vez al Jordán con Tobías, a oír a Juan", 2, cadena = "jordan_28", escena = "jordan"),
                    mundo("bautismo_28", "11-06", "Juan bautiza a un hombre de Nazaret, y la gente dice que el cielo se abrió", certeza = A, fuente = F_MARCOS_1, escena = "figura_luz"),
                    suceso("vuelve_jordan_28", "11-07", "Vuelve del Jordán con Tobías sin hablar en todo el camino", cadena = "jordan_28"),
                    suceso("dos_ensenanzas_28", "11-12", "Ismael dice que el Cristo bajó sobre él como paloma; Judit, que su cuerpo solo parece"),
                    suceso("mara_espera_28", "11-20", "Mara le dice que espera otro hijo para el verano"),
                    mundo("desierto_28", "11-26", "Dicen que el de Nazaret se fue al desierto y no ha vuelto", certeza = A, fuente = F_MARCOS_1),
                    fiesta("januca_28", "12-01", "Janucá, la fiesta de la Dedicación: ocho días de luces", 8, A, F_JANUCA, escena = "lamparas"),
                    mundo("carcel_juan_28", "12-10", "Herodes Antipas manda encerrar a Juan en la fortaleza de Maqueronte", certeza = A, fuente = F_MAQUERONTE),
                    mundo("galilea_28", "12-16", "El de Nazaret ha vuelto a Galilea: dice que el tiempo se ha cumplido", certeza = A, fuente = F_MARCOS_1),
                    mundo("pescadores_28", "12-21", "En la orilla de Cafarnaúm llama a cuatro pescadores, y lo dejan todo", certeza = A, fuente = F_MARCOS_1, escena = "figura_luz"),
                    mundo("cafarnaum_28", "12-26", "Noticias de Cafarnaúm: el sábado sacó un espíritu en la sinagoga y sanó a muchos", certeza = A, fuente = F_MARCOS_1),
                    suceso("lo_ve_28", "12-29", "Va a los pueblos de la orilla y lo ve de lejos, de espaldas, entre la gente", escena = "figura_luz"),
                )),
            TemporadaVida("29 d. C.", "maestro",
                "El de Nazaret enseña por la orilla del lago, y la gente lo sigue en barcas. María, la vecina, sana y se va con él. Eleazar lo oye enseñar desde una barca y esa noche cruza el lago en la de Tobías, detrás de la suya, en plena tormenta. Herodes mata a Juan. Cerca de la Pascua, Eleazar ve los panes y, de madrugada, una figura que camina sobre el agua; en Cafarnaúm muchos lo dejan. Ezequías se lastima la espalda. El de Nazaret llega a la comarca de Magdala. Nace Débora. Una noche ve una luz en lo alto de un monte. Judit le enseña las letras. En Sucot lo ve en el Templo.",
                listOf(
                    mundo("granizo_29", "01-08", "Cae granizo sobre el lago y rompe las tejas del secadero", certeza = INV, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("leviatan_29", "01-20", "Ismael le enseña que alrededor del mundo se enrosca Leviatán, la serpiente de fuera"),
                    mundo("maria_sigue_29", "02-08", "María, la vecina, está sana: se va con el de Nazaret y con otras mujeres", certeza = A, fuente = F_LUCAS_8),
                    mundo("sembrador_29", "02-21", "Enseña desde una barca a la gente de la orilla, con historias de semillas", certeza = A, fuente = F_MARCOS_4, escena = "figura_luz"),
                    mundo("tempestad_29", "02-22", "Cruza el lago de noche en plena tormenta, y la tormenta se calla", certeza = A, fuente = F_TEMPESTAD, escena = "figura_luz", clima = Clima.TORMENTA),
                    mundo("gerasa_29", "02-25", "Al otro lado del lago, dicen, una piara entera se echó al agua", certeza = A, fuente = F_MARCOS_5),
                    mundo("jairo_29", "03-05", "Dicen que levantó de la cama a la hija de Jairo, que ya estaba muerta", certeza = A, fuente = F_MARCOS_5),
                    mundo("nazaret_29", "03-12", "En Nazaret, su pueblo, no le creen, y casi no hace nada allí", certeza = A, fuente = F_MARCOS_6),
                    mundo("mayor_29", "03-16", "Los de Juan vienen desde la cárcel a preguntarle si es él el que tenía que venir", certeza = A, fuente = F_MATEO_11),
                    fiesta("purim_29", "03-19", "Purim, en el segundo mes de adar: se lee el rollo de Ester", certeza = A, fuente = F_PURIM),
                    mundo("envio_29", "03-26", "Manda a los doce de dos en dos por los pueblos de Galilea", certeza = A, fuente = F_MARCOS_6),
                    mundo("muerte_juan_29", "04-02", "Llega la noticia: Herodes Antipas mandó matar a Juan en Maqueronte", certeza = A, fuente = F_MARCOS_6, escena = "luto"),
                    mundo("panes_29", "04-11", "En un descampado de la orilla da de comer a miles con cinco panes y dos peces", certeza = A, fuente = F_JUAN_6, escena = "figura_luz"),
                    mundo("agua_29", "04-12", "De madrugada, desde la orilla, ve una figura que camina sobre el agua", certeza = A, fuente = F_AGUA, escena = "figura_luz"),
                    mundo("pan_vida_29", "04-14", "En la sinagoga de Cafarnaúm habla del pan de vida, y muchos lo dejan", certeza = A, fuente = F_JUAN_6),
                    fiesta("pesaj_29", "04-18", "Pésaj: este año tampoco suben, y la noche de los ácimos es en casa", certeza = A, fuente = F_PESAJ),
                    cosecha("cebada_29", "04-20", "La siega de la cebada: en el Templo se ofrece la primera gavilla", 1, A, F_SHAVUOT),
                    mundo("sal_29", "05-04", "Llega por el camino del Jordán la sal del mar Muerto, en sacos, a lomo de burro", certeza = INV),
                    suceso("espalda_29", "05-12", "Ezequías se lastima la espalda y deja el secadero en sus manos", cadena = "ezequias_29"),
                    suceso("vuelve_ezequias_29", "06-02", "Ezequías vuelve al secadero con bastón, gritando como siempre", cadena = "ezequias_29"),
                    fiesta("shavuot_29", "06-08", "Shavuot, la fiesta de las semanas: el trigo nuevo", certeza = A, fuente = F_SHAVUOT),
                    mundo("magadan_29", "06-20", "Llega en barca a la comarca de Magdala, y los fariseos le piden una señal", certeza = A, fuente = F_PANES, escena = "figura_luz"),
                    mundo("pedro_29", "07-08", "Dicen que, camino de Cesarea de Filipo, Pedro le dijo que es el Mesías", certeza = A, fuente = F_MARCOS_8),
                    mundo("luz_monte_29", "07-17", "Ve de noche una luz blanca en lo alto de un monte, y no sabe qué es", certeza = A, fuente = F_TRANSFIGURACION, escena = "luz_monte"),
                    suceso("nace_debora_29", "07-27", "Nace Débora, su hija, con el pelo negro de su abuela"),
                    mundo("incendio_29", "08-26", "Se quema un secadero de la orilla, y el humo tapa Magdala un día entero", certeza = INV, escena = "humo"),
                    suceso("letras_29", "09-10", "Judit empieza a enseñarle las letras, con un palo, en la arena del patio"),
                    fiesta("rosh_29", "09-28", "Rosh Hashaná, el año nuevo: suena el cuerno en la sinagoga", certeza = A, fuente = F_ROSH),
                    rito("sube_sucot_29", "10-02", "Sube otra vez a Jerusalén con los de Magdala, para Yom Kipur y Sucot", 4, INV, escena = "camino"),
                    rito("kipur_29", "10-07", "Yom Kipur en el Templo: todo Jerusalén ayuna", certeza = A, fuente = F_KIPUR),
                    fiesta("sucot_29", "10-12", "Sucot en Jerusalén: las cabañas de ramas en los tejados y en los caminos", 7, A, F_SUCOT, escena = "sucot"),
                    mundo("ensena_29", "10-15", "En medio de la fiesta aparece en el Templo y se pone a enseñar", certeza = A, fuente = F_JUAN_7, escena = "figura_luz"),
                    mundo("agua_viva_29", "10-18", "El último día de la fiesta, de pie, grita que quien tenga sed vaya a él", certeza = A, fuente = F_JUAN_7, escena = "figura_luz"),
                    rito("baja_sucot_29", "10-20", "Baja de Jerusalén por el valle del Jordán", 4, INV, escena = "camino"),
                    rito("vuelve_sucot_29", "10-24", "Vuelve a Magdala, y la orilla está casi vacía de barcas de fuera", certeza = INV),
                    suceso("natan_29", "11-10", "Natán sale de noche por primera vez en la barca de Tobías"),
                    suceso("hojas_29", "11-28", "Judit trae de Tiro unas hojas copiadas, y Ismael las lee en la reunión"),
                    fiesta("januca_29", "12-20", "Janucá, la fiesta de la Dedicación: ocho días de luces", 8, A, F_JANUCA, escena = "lamparas"),
                    mundo("dedicacion_29", "12-30", "Llega la noticia: en la fiesta de la Dedicación quisieron apedrearlo en el Templo", certeza = A, fuente = F_JUAN_10),
                )),
            TemporadaVida("30 d. C.", "pilato",
                "El de Nazaret anda al otro lado del Jordán y en Judea. A Ismael se le nublan los ojos, y Eleazar, que ya lee, lee por él. Pilato mata a unos galileos en el Templo. Eleazar sube a Jerusalén para Pésaj por Jericó; ve la entrada, los mercaderes y la higuera seca; la noche del jueves lo prenden. El viernes 14 de nisán lo crucifican, y él mira de lejos. El domingo, María dice que el sepulcro está vacío y que lo ha visto. De vuelta en Magdala, lo ve al amanecer en la orilla. Sube otra vez en Shavuot. Ismael dice que el que sufrió no era él, y que él se reía. María le cuenta lo que vio.",
                listOf(
                    mundo("mas_alla_30", "01-10", "Dicen que se fue al otro lado del Jordán, adonde bautizaba Juan", certeza = A, fuente = F_JUAN_10),
                    suceso("ojos_30", "01-25", "A Ismael se le nublan los ojos, y le pide que lea él en la reunión"),
                    mundo("galileos_30", "02-14", "Llega la noticia: Pilato mató a unos galileos en el Templo, junto a sus sacrificios", certeza = A, fuente = F_PILATO),
                    suceso("lee_30", "03-01", "Lee solo, por primera vez, una hoja entera de las que trajo Judit"),
                    mundo("lazaro_30", "03-05", "Dicen que en Betania sacó vivo del sepulcro a un muerto, Lázaro", certeza = A, fuente = F_JUAN_11),
                    fiesta("purim_30", "03-08", "Purim: se lee el rollo de Ester, y las casas se mandan platos unas a otras", certeza = A, fuente = F_PURIM),
                    rito("sube_pesaj_30", "03-26", "Sube a Jerusalén para Pésaj por Jericó; dicen que el de Nazaret también va", 4, INV, escena = "camino"),
                    mundo("jerico_30", "03-28", "En Jericó, a la salida, un ciego del camino le grita, y se va detrás de él viendo", certeza = A, fuente = F_MARCOS_10, escena = "figura_luz"),
                    mundo("entrada_30", "04-02", "Entra en Jerusalén en un pollino, y la gente le tiende mantos y ramas", certeza = A, fuente = F_ENTRADA, escena = "figura_luz"),
                    mundo("mercaderes_30", "04-03", "En el Templo vuelca las mesas de los cambistas y los puestos de las palomas", certeza = A, fuente = F_MERCADERES, escena = "figura_luz"),
                    mundo("higuera_30", "04-04", "En el camino de Betania hay una higuera seca de raíz, la que él maldijo", certeza = A, fuente = F_MARCOS_11),
                    mundo("cena_30", "04-06", "La última noche: cena con los doce y, después de la cena, lo prenden", certeza = A, fuente = F_CENA),
                    mundo("cruz_30", "04-07", "Lo crucifican bajo Poncio Pilato, la víspera de la Pascua, y él mira de lejos", certeza = A, fuente = F_CRUZ, escena = "oscuridad"),
                    fiesta("pesaj_30", "04-08", "Pésaj cae en sábado, y la ciudad está callada", certeza = A, fuente = F_PESAJ, escena = "luto"),
                    mundo("sepulcro_30", "04-09", "María, la vecina, dice que el sepulcro está vacío y que lo ha visto", certeza = A, fuente = F_JUAN_20),
                    rito("baja_pesaj_30", "04-15", "Baja de Jerusalén con los de Galilea, que no hablan de otra cosa", 4, INV, escena = "camino"),
                    rito("vuelve_pesaj_30", "04-19", "Vuelve a Magdala, y el secadero le parece de otro", certeza = INV),
                    mundo("orilla_30", "04-28", "Al amanecer, desde el secadero, ve a un hombre de pie en la orilla, y nadie sabe quién es", certeza = A, fuente = F_JUAN_21, escena = "figura_luz"),
                    suceso("circulo_30", "05-05", "La noche en que el círculo por fin habla de Jerusalén: nadie sabe qué decir"),
                    mundo("cuarenta_30", "05-18", "Dicen los de Pedro que en el monte de los Olivos lo vieron subir, y una nube lo tapó", certeza = A, fuente = F_HECHOS_1),
                    rito("sube_shavuot_30", "05-22", "Sube otra vez a Jerusalén, para Shavuot", 4, INV, escena = "camino"),
                    fiesta("shavuot_30", "05-28", "Shavuot: los de Galilea hablan, y cada forastero los oye en su lengua", certeza = A, fuente = F_PENTECOSTES),
                    rito("baja_shavuot_30", "06-01", "Baja de Jerusalén: los de Pedro se quedan allí", 4, INV, escena = "camino"),
                    rito("vuelve_shavuot_30", "06-05", "Vuelve a Magdala con el verano encima", certeza = INV),
                    suceso("risa_30", "06-20", "Ismael dice que el que sufrió en el madero no era él, y que él se reía"),
                    suceso("maria_vuelve_30", "07-20", "María vuelve unos días a su casa de Magdala, y abre la puerta"),
                    suceso("debora_camina_30", "08-05", "Débora da sus primeros pasos en el patio, hacia la higuera"),
                    suceso("vision_30", "08-14", "María le cuenta que lo vio después, y que lo vio con el espíritu"),
                    suceso("barca_nueva_30", "09-05", "Tobías estrena una barca nueva, y la empujan al agua entre todos"),
                    fiesta("rosh_30", "09-18", "Rosh Hashaná, el año nuevo: suena el cuerno en la sinagoga", certeza = A, fuente = F_ROSH),
                    rito("kipur_30", "09-27", "Yom Kipur en Magdala: un día entero de ayuno en la sinagoga", certeza = A, fuente = F_KIPUR),
                    fiesta("sucot_30", "10-02", "Sucot en Magdala: cabañas de ramas en los tejados", 7, A, F_SUCOT, escena = "sucot"),
                    mundo("caravana_30", "10-25", "Los de la caravana de Damasco cuentan que allí también hay de los del Nazareno", certeza = INV),
                    suceso("natan_30", "11-12", "Natán aprende a remendar la red con la aguja de hueso"),
                    fiesta("januca_30", "12-10", "Janucá, la fiesta de la Dedicación: ocho días de luces", 8, A, F_JANUCA, escena = "lamparas"),
                    suceso("cielos_30", "12-22", "Ismael, casi ciego, le pide que cuente los cielos por él"),
                )),
            TemporadaVida("31 d. C.", "comunidad",
                "Muere Shifra, su madre. Eleazar sube a Jerusalén para Pésaj a ver a los del Nazareno, que lo tienen todo en común; ve a Pedro y a Juan levantar en el Templo a un hombre que no podía andar, y cómo los prenden y el consejo los suelta con amenazas. Habla con los de Pedro de lo que enseña María, y lo echan. Llega la noticia de Ananías y Safira. Ezequías lo hace su socio. En Sucot ve a los del Nazareno en el pórtico de Salomón; los meten presos, los azotan, y Gamaliel pide que los dejen. Eligen a los siete. En Roma cae Sejano, y la noticia llega en diciembre. Los ancianos de la sinagoga echan a Ismael, y el círculo se esconde en las cuevas del Arbel.",
                listOf(
                    mundo("tormenta_31", "01-05", "Una tormenta de invierno arranca los cañizos del secadero", certeza = INV, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("muere_shifra_31", "01-18", "Muere Shifra, su madre, una noche de lluvia", escena = "luto"),
                    rito("duelo_31", "01-25", "Se levantan del suelo al acabar los siete días de duelo", certeza = INV),
                    cosecha("banco_31", "02-12", "Llega otro banco de peces, y el secadero no da abasto", 3, INV, escena = "secadero"),
                    fiesta("purim_31", "02-25", "Purim: se lee el rollo de Ester en la sinagoga", certeza = A, fuente = F_PURIM),
                    rito("sube_pesaj_31", "03-19", "Sube a Jerusalén para Pésaj, a ver a los del Nazareno", 4, INV, escena = "camino"),
                    mundo("comunidad_31", "03-23", "Ve en Jerusalén a los del Nazareno: comen juntos y lo tienen todo en común", certeza = A, fuente = F_HECHOS_2),
                    fiesta("pesaj_31", "03-27", "Pésaj en Jerusalén: el cordero, el pan sin levadura y las hierbas amargas", certeza = A, fuente = F_PESAJ),
                    mundo("cojo_31", "03-29", "En el Templo, Pedro y Juan levantan a un hombre que no podía andar", certeza = A, fuente = F_HECHOS_3),
                    mundo("presos_31", "03-30", "Prenden a Pedro y a Juan en el pórtico de Salomón y los llevan ante el consejo", certeza = A, fuente = F_HECHOS_4),
                    mundo("consejo_31", "03-31", "El consejo suelta a Pedro y a Juan con la orden de no hablar más de ese nombre", certeza = A, fuente = F_HECHOS_4),
                    suceso("choque_31", "04-01", "Habla en casa de los de Pedro de lo que enseña María, y lo echan"),
                    mundo("bernabe_31", "04-02", "Un levita de Chipre vende su campo y pone el dinero a los pies de los apóstoles", certeza = A, fuente = F_HECHOS_4),
                    rito("baja_pesaj_31", "04-04", "Baja de Jerusalén sin despedirse de nadie", 4, INV, escena = "camino"),
                    rito("vuelve_pesaj_31", "04-08", "Vuelve a Magdala y no le cuenta a nadie lo de la casa de Pedro", certeza = INV),
                    mundo("ananias_31", "05-02", "Llega la noticia: Ananías y Safira cayeron muertos delante de Pedro", certeza = A, fuente = F_HECHOS_5),
                    fiesta("shavuot_31", "05-17", "Shavuot, la fiesta de las semanas: el trigo nuevo", certeza = A, fuente = F_SHAVUOT),
                    suceso("socio_31", "06-10", "Ezequías lo hace socio del secadero, a medias en la ganancia"),
                    suceso("guardianes_31", "06-28", "En la reunión aprenden lo que el alma responde a los guardianes del camino"),
                    suceso("debora_habla_31", "07-14", "Débora dice su nombre, y luego el de su abuela, que ya no está"),
                    suceso("natan_pesca_31", "08-03", "Natán sale a pescar con Tobías una noche entera"),
                    suceso("maria_31", "08-20", "María pasa por Magdala camino de Jerusalén y deja un recado para el círculo"),
                    fiesta("rosh_31", "09-07", "Rosh Hashaná, el año nuevo: suena el cuerno en la sinagoga", certeza = A, fuente = F_ROSH),
                    rito("sube_sucot_31", "09-12", "Sube a Jerusalén para Yom Kipur y Sucot", 4, INV, escena = "camino"),
                    rito("kipur_31", "09-16", "Yom Kipur en el Templo: el sumo sacerdote entra solo detrás del velo", certeza = A, fuente = F_KIPUR),
                    fiesta("sucot_31", "09-21", "Sucot en Jerusalén: siete días en una cabaña de ramas", 7, A, F_SUCOT, escena = "sucot"),
                    mundo("carcel_31", "09-22", "Meten a los apóstoles en la cárcel, y dicen que de noche salieron de ella", certeza = A, fuente = F_HECHOS_5),
                    mundo("apostoles_31", "09-23", "Azotan a los apóstoles, y un fariseo, Gamaliel, pide que los dejen en paz", certeza = A, fuente = F_GAMALIEL),
                    mundo("portico_31", "09-25", "Ve a los del Nazareno reunidos en el pórtico de Salomón, a la vista de todos", certeza = A, fuente = F_HECHOS_5),
                    rito("baja_sucot_31", "09-29", "Baja de Jerusalén con las espaldas de los apóstoles en la cabeza", 4, INV, escena = "camino"),
                    rito("vuelve_sucot_31", "10-03", "Vuelve a Magdala con las primeras lluvias", certeza = INV),
                    mundo("siete_31", "10-12", "Los de Jerusalén eligen a siete, todos con nombre griego, para repartir el pan", certeza = A, fuente = F_SIETE),
                    mundo("sejano_31", "10-18", "En Roma, el Senado condena a muerte a Sejano; aquí nadie lo sabe todavía", certeza = HISTORICO, fuente = F_SEJANO),
                    mundo("esteban_31", "10-30", "Se habla en Jerusalén de Esteban, uno de los siete, y de lo que hace entre la gente", certeza = A, fuente = F_HECHOS_6),
                    suceso("cueva_31", "11-04", "Los ancianos de la sinagoga echan a Ismael, y el círculo se va a las cuevas del Arbel", escena = "grieta"),
                    fiesta("januca_31", "11-30", "Janucá, la fiesta de la Dedicación: ocho días de luces", 8, A, F_JANUCA, escena = "lamparas"),
                    mundo("noticia_sejano_31", "12-12", "Llega la noticia: en Roma cayó Sejano, el que mandaba en nombre de Tiberio", certeza = A, fuente = F_SEJANO),
                    suceso("guardia_31", "12-20", "Un hombre de la sinagoga le pregunta qué luces se ven de noche en el Arbel"),
                )),
            TemporadaVida("32 d. C.", "sanedrin",
                "En Pésaj, Eleazar oye a Esteban discutir con los de la sinagoga de los libertos. En Shavuot lo apedrean fuera de la ciudad, y un joven, Saulo, guarda los mantos; unos hombres piadosos lo entierran. Empieza la persecución, Saulo entra en las casas, y Eleazar huye con otros a la ciudad de Samaria, donde predica Felipe y donde la gente llama a Simón el gran poder de Dios. Simón se bautiza; llegan Pedro y Juan, y Simón les ofrece dinero. Eleazar trabaja en las eras y en la vendimia y vuelve a Magdala para Rosh Hashaná. El círculo sella sus hojas en una jarra, en el fondo de la cueva. Saulo pide cartas para Damasco. Un mercader dice que Simón anda por la costa.",
                listOf(
                    suceso("natan_32", "01-12", "Natán carga solo, por primera vez, un ánfora hasta la caravana"),
                    mundo("tormenta_32", "01-20", "Llueve una semana entera y el lago se come la orilla del secadero", certeza = INV, escena = "tormenta", clima = Clima.LLUVIA),
                    suceso("debora_32", "01-29", "Débora se pierde en la orilla, y la encuentran dormida entre las redes"),
                    suceso("llave_32", "02-10", "Ismael le da a guardar la llave del arca de las hojas"),
                    mundo("nieve_hermon_32", "02-24", "Nieva hasta la mitad del Hermón, y desde Magdala se ve como un muro blanco", certeza = INV, clima = Clima.DESPEJADO),
                    fiesta("purim_32", "03-15", "Purim, en el segundo mes de adar: se lee el rollo de Ester", certeza = A, fuente = F_PURIM),
                    rito("sube_pesaj_32", "04-05", "Sube a Jerusalén para Pésaj por el camino de Samaria", 3, INV, escena = "camino"),
                    mundo("libertos_32", "04-10", "Oye a Esteban discutir con los de la sinagoga de los libertos", certeza = A, fuente = F_HECHOS_6),
                    fiesta("pesaj_32", "04-13", "Pésaj en Jerusalén, con miedo en las casas de los del Nazareno", certeza = A, fuente = F_PESAJ),
                    rito("baja_pesaj_32", "04-21", "Baja de Jerusalén por el camino de Samaria", 3, INV, escena = "camino"),
                    rito("vuelve_pesaj_32", "04-24", "Vuelve a Magdala con la cebada ya segada", certeza = INV),
                    mundo("sequia_32", "05-12", "No llueve en todo el mes, y el lago baja hasta enseñar piedras que nadie había visto", certeza = INV, clima = Clima.DESPEJADO),
                    rito("sube_shavuot_32", "05-28", "Sube a Jerusalén para Shavuot", 3, INV, escena = "camino"),
                    fiesta("shavuot_32", "06-03", "Shavuot en Jerusalén: el trigo nuevo y los dos panes en el Templo", certeza = A, fuente = F_SHAVUOT),
                    mundo("esteban_32", "06-05", "Apedrean a Esteban fuera de la ciudad; un joven, Saulo, guarda los mantos", certeza = A, fuente = F_ESTEBAN, escena = "luto"),
                    rito("entierro_esteban_32", "06-06", "Unos hombres piadosos entierran a Esteban y lloran por él", certeza = A, fuente = F_HECHOS_8, escena = "luto"),
                    mundo("dispersion_32", "06-07", "Empieza la persecución: los del Nazareno huyen de Jerusalén", certeza = A, fuente = F_HECHOS_8, escena = "huida", clima = Clima.DESPEJADO),
                    mundo("saulo_casas_32", "06-08", "Llega la noticia: Saulo entra en las casas y se lleva presos a hombres y mujeres", certeza = A, fuente = F_HECHOS_8),
                    mundo("samaria_32", "06-09", "Llega a la ciudad de Samaria con otros que huyen, y no se atreve a volver a casa", certeza = INV),
                    mundo("felipe_32", "06-25", "En la ciudad de Samaria predica Felipe, uno de los siete, y la gente lo sigue", certeza = A, fuente = F_FELIPE),
                    mundo("simon_32", "07-06", "Oye hablar de Simón, al que los de Samaria llaman el gran poder de Dios", certeza = A, fuente = F_SIMON),
                    mundo("bautizo_simon_32", "07-20", "Simón se hace bautizar por Felipe, y ya no se separa de él", certeza = A, fuente = F_SIMON),
                    mundo("mensaje_32", "07-29", "Un barquero de Magdala le trae un recado de Mara: están bien", certeza = INV),
                    mundo("pedro_juan_32", "08-08", "Llegan de Jerusalén Pedro y Juan, e imponen las manos a los bautizados", certeza = A, fuente = F_SIMON),
                    mundo("dinero_32", "08-10", "Simón les ofrece dinero por ese poder, y Pedro lo echa con palabras duras", certeza = A, fuente = F_SIMON),
                    mundo("noche_simon_32", "08-20", "De noche, en una casa de Samaria, oye hablar a Simón, y no sabe qué pensar", certeza = INV),
                    mundo("eunuco_32", "08-30", "Dicen que Felipe bautizó a un etíope en el camino de Jerusalén a Gaza", certeza = A, fuente = F_FELIPE),
                    cosecha("vendimia_32", "09-06", "La vendimia en los montes de Samaria: pisa uva por un jornal", 3, INV),
                    fiesta("rosh_32", "09-26", "Rosh Hashaná: está otra vez en Magdala para el año nuevo", certeza = A, fuente = F_ROSH),
                    rito("kipur_32", "10-05", "Yom Kipur en Magdala: un día entero de ayuno en la sinagoga", certeza = A, fuente = F_KIPUR),
                    fiesta("sucot_32", "10-10", "Sucot en Magdala: este año nadie del círculo sube a Jerusalén", 7, A, F_SUCOT, escena = "sucot"),
                    rito("jarra_32", "10-28", "Sellan las hojas del círculo en una jarra, en lo hondo de la cueva", certeza = INV, escena = "grieta"),
                    mundo("saulo_32", "11-15", "Dicen que Saulo pidió cartas para ir a buscar a los del Nazareno a Damasco", certeza = A, fuente = F_HECHOS_9),
                    suceso("mara_32", "11-22", "Mara le pregunta si piensa seguir subiendo a la cueva, y él no contesta"),
                    suceso("natan_cueva_32", "12-04", "Natán le pregunta qué hay en la cueva del Arbel"),
                    fiesta("januca_32", "12-18", "Janucá, la fiesta de la Dedicación: ocho días de luces", 8, A, F_JANUCA, escena = "lamparas"),
                    mundo("tiro_32", "12-28", "Un mercader de Tiro dice que Simón anda ahora por la costa", certeza = INV),
                )),
        ),
        arcos = listOf(
            // El pescado tendido al sol: más con los bancos de invierno y primavera, menos en lo peor del verano. [Aproximación]
            Arco.cadaAnio("secadero", TipoArco.OTRO, "01-01" to 0.7, "02-20" to 1.0, "04-30" to 0.75, "07-15" to 0.3, "09-30" to 0.35, "11-30" to 0.6, "12-31" to 0.7),
            // Las barcas y las hogueras de la gente que sigue a Jesús por la orilla de Magdala (la gente no se dibuja).
            Arco("barcas", TipoArco.OTRO, listOf(
                p(1, "01-01", 0.0), p(1, "12-15", 0.0), p(1, "12-31", 0.25), p(2, "02-20", 0.6), p(2, "04-11", 1.0), p(2, "04-16", 0.55),
                p(2, "07-01", 0.5), p(2, "10-01", 0.1), p(3, "04-01", 0.05), p(3, "04-07", 0.0),
            )),
            // Las hojas que el círculo copia y guarda.
            Arco("codice", TipoArco.OBRA, listOf(p(1, "01-01", 0.1), p(5, "10-27", 0.9), p(5, "12-31", 0.95))),
            // Dónde se reúne el círculo: la casa de Ismael (0) o una cueva del Arbel (1).
            Arco("cueva", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(4, "11-04", 1.0, salto = true), p(5, "12-31", 1.0))),
            // Las hojas, selladas en una jarra en el fondo de la cueva.
            Arco("jarra", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(5, "10-28", 1.0, salto = true), p(5, "12-31", 1.0))),
        ),
        objetos = listOf("lampara", "anfora", "sal", "cuchillo", "red", "pesa", "aguja", "cesto", "jarra", "hojas", "barca", "amuleto"),
        estados = listOf(
            Estado("jerusalen", "sube_sucot_28", "vuelve_sucot_28"), Estado("jerusalen", "sube_sucot_29", "vuelve_sucot_29"),
            Estado("jerusalen", "sube_pesaj_30", "vuelve_pesaj_30"), Estado("jerusalen", "sube_shavuot_30", "vuelve_shavuot_30"),
            Estado("jerusalen", "sube_pesaj_31", "vuelve_pesaj_31"), Estado("jerusalen", "sube_sucot_31", "vuelve_sucot_31"),
            Estado("jerusalen", "sube_pesaj_32", "vuelve_pesaj_32"), Estado("jerusalen", "sube_shavuot_32", "dispersion_32"),
            Estado("huida", "dispersion_32", "rosh_32"),
        ),
    )

    // =====================================================================
    //  Segomaros, aprendiz de los druidas. Gergovia, el oppidum de los arvernos, del 54 al 50 a. C.
    // =====================================================================
    //
    // La ficha es la de PLAN_TEMAS, 10e («Otra vida»), con las correcciones de `docs/diseno/temas/fuentes_galia.md`
    // (sección 1). Lo que allí va como «[sin verificar]» no se afirma aquí. Es una persona inventada en un mundo real
    // (10a): sus días históricos y aproximados llevan su fuente; lo de su vida es inventado.
    //
    // Su gente (toda inventada): su padre, un caballero arverno con su gente; su madre; su hermana, más chica, a la que
    // prometen en la 2, que se casa en la montaña en la 4 y tiene un hijo en la 5. Vebrumaros, el druida viejo que enseña
    // en el robledal al pie de la meseta (el nombre es el de la dedicatoria galo-griega a Taranis de Orgon, ΟΥΗΒΡΟΥΜΑΡΟΣ;
    // `fuentes_galia.md`, sección 10). Korisios, el otro aprendiz, que no deja la escuela (el nombre, de la espada grabada
    // de Port). Esunertos, un guerrero de la gente de su padre que le enseña armas y caballos, y que no vuelve de Alesia (el
    // nombre es de una inscripción latina de época romana, Esunertus: **[Aproximación]**). La gente real (Vercingétorix,
    // Gobanición, Lucterio, Epasnacto, Acón, Tasgecio, Commio, César) sale de lejos y como la cuentan las fuentes.
    //
    // Las temporadas, del 54 al 50 a. C.:
    // 1. **54 a. C., la escuela.** Los versos de memoria en el robledal, el muérdago, la asamblea de los druidas en tierra
    //    de los carnutos (César 6.13; el maestro va y él se queda), la siega corta de un año sin lluvia (5.24), y las
    //    noticias de fuera: César en Britania, la muerte de Tasgecio entre los carnutos y la legión que los eburones de
    //    Ambiórix destruyen en su invernada.
    // 2. **53 a. C., la Galia inquieta.** Las Cortes de Lutecia (6.3), los senones de Acón que piden perdón, el segundo
    //    puente sobre el Rin, la tierra de los eburones saqueada, y la muerte de Acón en Durocortorum (6.44), de la que se
    //    quejan los nobles al empezar el 52 (7.1). Lleva sus primeros mensajes; prometen a su hermana.
    // 3. **52 a. C., el levantamiento.** El grito de Cenabum, Vercingétorix echado de Gergovia y vuelto rey (7.3–7.4); él
    //    deja la escuela, porque los druidas no van a la guerra (6.14); César en las Cevenas; la tierra quemada y Avárico;
    //    César al pie de Gergovia y la victoria gala; la asamblea de Bibracte (sin los remos, los lingones ni los tréveros,
    //    7.63); la batalla de caballería, en la que lo hieren; Alesia, la caballería que sale de noche (Esunertos se lleva
    //    su yegua), los mandubios, el socorro, la última batalla y la entrega; el regreso de los cautivos arvernos (7.90).
    // 4. **51 a. C., las últimas resistencias.** El invierno de los carnutos, los belóvacos, Lucterio, el gutuater,
    //    Uxellodunum (Aulo Hircio, libro 8; comprobado en Wikipedia: «Siege of Uxellodunum» y «Lucterius»), Lucterio
    //    entregado por el arverno Epasnacto y la paz de Commio. Quiere irse con Lucterio y no se va; decide volver.
    // 5. **50 a. C., la Galia conquistada.** Vuelve a la escuela y a los versos que no se escriben; Vercingétorix preso
    //    en Roma; un mercader que quiere los versos escritos; César se lleva legiones hacia Italia («Gallic Wars»).
    //
    // Las fechas:
    // - Van en el calendario juliano proléptico, «MM-DD». **Casi todo es aproximado:** César da estaciones y cuentas de
    //   días, no fechas; los meses son los de la cronología de `fuentes_galia.md` (sección 3), y el día se eligió dentro.
    // - **Cenabum, el 23 de enero** del 52, la fecha de Carcopino que da la Wikipedia francesa, con la duda de la
    //   corrección 17 (César pone el levantamiento después de saberse la muerte de Clodio, el 18 de enero romano). El grito
    //   llega a la frontera arverna «antes de tres horas de noche» (7.3), antes de acabar la primera guardia, y a Gergovia
    //   esa misma noche. La victoria de Gergovia va el 20 de mayo (la cronología propone mayo), la batalla de caballería a
    //   mediados de agosto, el socorro a finales de septiembre y **la entrega el 27 de septiembre** (Brunaux).
    // - **El muérdago,** el sexto día de la luna (Plinio 16.249–251). La luna nueva se calculó en la sesión (Meeus,
    //   *Astronomical Algorithms*, cap. 49, con ΔT de unas 3 horas; el día 1 de la luna es el de la conjunción): enero del
    //   54 (el 11), febrero del 53 (el 30 de enero), enero del 51 (el 7) y enero del 50 (el 26). **El mes lo eligió el
    //   calendario** (Plinio no lo dice): en invierno, con los robles sin hojas, se ve el muérdago. Va «aproximado».
    //   En el 52 la luna cae el mismo 23 de enero de Cenabum: ese día no va aparte, lo cuenta el día del grito.
    // - **Las tres noches de Samonios** (TRINVX SAMONI, en el bronce de Coligny) van del 17 al 19 de noviembre porque el
    //   tema pone Samonios en noviembre (recurso: `ui/Galia.kt`). Que la fiesta fuera así en el 52 a. C. es una
    //   reconstrucción **[Aproximación]**: el bronce es de finales del siglo II d. C.
    // - **La asamblea de los druidas** (6.13): César dice «en cierta estación del año», sin día ni mes; aquí, en mayo.
    //   Va «aproximado» y sin bosque: César dice «un lugar sagrado» (la página «Carnutes» advierte que lo del bosque de los
    //   carnutos es moderno).
    //
    // Claves de escena: muerdago (el druida en el roble, con la hoz y el manto blanco; sin los toros), nieve, cosecha,
    // tormenta, samonios (las hogueras de las tres noches), luto, grito (antorchas que corren de noche por la llanura),
    // rey (estandartes y hogueras en la meseta), humo (granjas que arden lejos), romanos (los campamentos de César en la
    // llanura, de día; sus hogueras, de noche), victoria, socorro (las colinas de fuera de Alesia llenas de fuegos),
    // batalla, entrega (las armas en el suelo, delante del campamento) y camino. Nunca personas.
    //
    // Arcos: trigo (el trigo de los campos de la llanura: sembrado en otoño, verde en primavera, segado en julio;
    // **[Aproximación]**), romanos (1 = los campamentos de César al pie de Gergovia, del 5 al 22 de mayo del 52), danos
    // (las granjas quemadas cuando César cruza las Cevenas, y más cuando llega a Gergovia; se van reconstruyendo hasta el
    // 50), muralla (la muralla de la meseta, de madera, piedra y tierra, que se refuerza en la primavera del 52) y comercio
    // (los carros y las ánforas de los mercaderes de Italia en la puerta: los hay hasta Cenabum, ninguno en el 52, y
    // vuelven en la 4).
    //
    // Estados: «campana» (temporada 3: con los jinetes de Vercingétorix, de que se une a que vuelve a Gergovia, y de que
    // sale hacia Bibracte a que empieza el cerco de Alesia), «alesia» (del cerco a la entrega) y «cautivo» (de la entrega
    // a que César devuelve a los cautivos arvernos). En la temporada 5, que desde 2032 se repite, los días que no deben
    // moverse son ritos, fiestas y sucesos del mundo; sus sucesos personales no forman cadenas.
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento (el cuadro de Galia va después de la tanda
    // S19; la ficha del tema, PLAN_TEMAS 10e, pide el oppidum, el manantial y Alesia):
    // - **Gergovia (lugar principal):** la meseta con su muralla según `muralla`, las casas de paja y el humo, con niebla
    //   de mañana; abajo, la llanura con el trigo según `trigo`, las granjas quemadas según `danos` y, según `comercio`,
    //   los carros de los mercaderes en la puerta. Al pie, el robledal de la escuela con el manantial y sus ofrendas de
    //   madera (de época romana: es recurso del cuadro). Del 5 al 22 de mayo del 52, según `romanos`, los campamentos de
    //   César en la llanura (de noche, sus hogueras: «Roma, al fondo»).
    // - **El campamento de la campaña (temporada 3, unas 17 semanas en dos tramos: lugar propio):** tiendas y fuegos
    //   galos en un bosque, caballos atados, el humo de la tierra quemada en el horizonte (`humo`).
    // - **Alesia (temporada 3, unas 6 semanas: lugar propio):** el oppidum en su colina entre dos ríos, de noche, con las
    //   dos líneas del cerco romano iluminadas; con `socorro`, las colinas de fuera llenas de fuegos; con `entrega`, las
    //   armas en el suelo. Durante «cautivo», el mismo sitio con el cerco abierto y sin los fuegos galos (variante).

    private val F_DRUIDAS = "Los druidas y su asamblea (César 6.13–6.14)" to "${W}Druid"
    private val F_MUERDAGO = "El rito del roble y el muérdago (Plinio 16.249–251)" to "${W}Ritual_of_oak_and_mistletoe"
    private val F_COLIGNY = "El calendario de Coligny" to "${W}Coligny_calendar"
    private val F_CARNUTOS = "Los carnutos" to "${W}Carnutes"
    private val F_BRITANIA = "Las expediciones de César a Britania" to "${W}Julius_Caesar%27s_invasions_of_Britain"
    private val F_AMBIORIX = "Ambiórix y los eburones" to "${W}Ambiorix"
    private val F_LUTECIA = "Lutecia" to "${W}Lutetia"
    private val F_ACON = "Acón, de los senones" to "https://fr.wikipedia.org/wiki/Acco"
    private val F_RIN = "Los puentes de César sobre el Rin" to "${W}Caesar%27s_Rhine_bridges"
    private val F_VERCINGETORIX = "Vercingétorix" to "${W}Vercingetorix"
    private val F_GUERRE = "La guerra de las Galias (Wikipedia en francés)" to "https://fr.wikipedia.org/wiki/Guerre_des_Gaules"
    private val F_AVARICO = "El cerco de Avárico" to "https://fr.wikipedia.org/wiki/Si%C3%A8ge_d%27Avaricum"
    private val F_GERGOVIA = "La batalla de Gergovia" to "${W}Battle_of_Gergovia"
    private val F_ALESIA = "El cerco de Alesia" to "${W}Battle_of_Alesia"
    private val F_BELOVACOS = "Los belóvacos" to "${W}Bellovaci"
    private val F_LUCTERIO = "Lucterio, de los cadurcos" to "${W}Lucterius"
    private val F_UXELLODUNUM = "El cerco de Uxellodunum" to "${W}Siege_of_Uxellodunum"
    private val F_COMMIO = "Commio, de los atrébates" to "${W}Commius"
    private val F_GUERRA = "La guerra de las Galias" to "${W}Gallic_Wars"

    /** Lo que vuelve casi cada año en la escuela y en los campos: los mismos ids, el mismo texto. */
    private fun muerdago(fecha: String) = rito("muerdago", fecha, "El sexto día de la luna: el druida sube al roble y corta el muérdago con la hoz de oro", certeza = A, fuente = F_MUERDAGO, escena = "muerdago")
    private fun siembra(fecha: String) = cosecha("siembra", fecha, "La siembra de primavera en los campos de la llanura", 4, INV)
    private fun parteMaestro(fecha: String) = rito("parte_maestro", fecha, "El maestro sale con otros druidas hacia la asamblea, en tierra de los carnutos", certeza = INV)
    private fun asamblea(fecha: String) = rito("asamblea", fecha, "Los druidas se reúnen en un lugar sagrado de la tierra de los carnutos", 3, A, F_DRUIDAS)
    private fun vuelveMaestro(fecha: String) = rito("vuelve_maestro", fecha, "Vuelve el maestro de la asamblea con noticias de toda la Galia", certeza = INV)
    private fun siega(fecha: String) = cosecha("siega", fecha, "La siega del trigo en los campos de la llanura", 8, INV)
    private val SAMONIOS = fiesta("samonios", "11-17", "Las tres noches de Samonios", 3, A, F_COLIGNY, escena = "samonios")
    private fun nieve(fecha: String) = mundo("nieve_escuela", fecha, "Nieva sobre la meseta, y la escuela se hace junto al fuego", certeza = INV, escena = "nieve", clima = Clima.NIEVE)
    private val ANIVERSARIO_GERGOVIA = rito("aniversario_gergovia", "05-20", "Sube al borde de la meseta, donde los romanos se volvieron atrás", certeza = INV)
    private val ANIVERSARIO_ALESIA = rito("aniversario_alesia", "09-27", "El día de la entrega: no habla con nadie hasta la noche", certeza = INV)

    val APRENDIZ = Calendario(
        vida = "aprendiz_druida",
        estaciones = Calendario.TEMPLADO_NORTE,
        // La meseta de Gergovia, a unos 700 m, en el Macizo Central: inviernos fríos, con nieve y nieblas en los valles;
        // primaveras de lluvia; veranos calientes con tormentas por la tarde; otoños de niebla. [Aproximación]
        clima = listOf(
            mes(despejado = 5, nubes = 10, lluvia = 4, nieve = 6, niebla = 5, viento = 1), mes(despejado = 5, nubes = 10, lluvia = 5, nieve = 5, niebla = 4, viento = 2),
            mes(despejado = 7, nubes = 10, lluvia = 7, nieve = 2, niebla = 2, viento = 3), mes(despejado = 8, nubes = 10, lluvia = 9, tormenta = 1, niebla = 1, viento = 2),
            mes(despejado = 10, nubes = 9, lluvia = 8, tormenta = 3, viento = 1), mes(despejado = 13, nubes = 7, lluvia = 5, tormenta = 4, viento = 1),
            mes(despejado = 16, nubes = 5, lluvia = 3, tormenta = 5, viento = 1), mes(despejado = 15, nubes = 6, lluvia = 4, tormenta = 5),
            mes(despejado = 12, nubes = 8, lluvia = 6, tormenta = 2, niebla = 2), mes(despejado = 8, nubes = 9, lluvia = 8, niebla = 5, viento = 2),
            mes(despejado = 5, nubes = 11, lluvia = 8, nieve = 1, niebla = 6, viento = 2), mes(despejado = 4, nubes = 11, lluvia = 5, nieve = 5, niebla = 6, viento = 1),
        ),
        temporadas = listOf(
            TemporadaVida("54 a. C.", "escuela",
                "Segomaros aprende versos de memoria en la escuela del robledal, con el viejo Vebrumaros y con Korisios. Ve cortar el muérdago el sexto día de la luna. Su padre le da una yegua. El maestro va a la asamblea de los druidas en tierra de los carnutos y vuelve con noticias. La siega sale corta, porque no llueve. Llegan noticias de César en Britania, de la muerte de Tasgecio entre los carnutos y de la legión que destruyen los eburones de Ambiórix. En diciembre el maestro le dice que empezará los versos de los juicios.",
                listOf(
                    muerdago("01-16"),
                    nieve("02-08"),
                    suceso("mil_versos", "02-25", "Dice de corrido ante el maestro, sin equivocarse, los versos que le costaron un invierno"),
                    suceso("yegua", "03-15", "Su padre le da una yegua torda: un caballero, le dice, no va a pie"),
                    siembra("03-28"),
                    suceso("celtilo", "04-18", "Un viejo le cuenta junto al fuego cómo mataron a Celtilo por querer ser rey"),
                    parteMaestro("05-12"),
                    asamblea("05-24"),
                    vuelveMaestro("06-06"),
                    suceso("letras", "06-20", "En el mercado ve a un mercader llevar sus cuentas con letras griegas"),
                    mundo("tormenta", "07-01", "Una tormenta de verano: un rayo cae en el robledal de la otra ladera", certeza = INV, escena = "tormenta", clima = Clima.TORMENTA),
                    cosecha("siega_seca", "07-12", "La siega del trigo, corta y rala: este año no ha llovido", 8, A, F_AMBIORIX),
                    mundo("britania", "08-02", "Dicen los mercaderes que César ha vuelto a cruzar el mar hacia Britania", certeza = A, fuente = F_BRITANIA),
                    suceso("banquete", "08-22", "En el banquete, un bardo canta las alabanzas de su abuelo y las burlas de un vecino"),
                    suceso("estrellas", "09-12", "Una noche clara, el maestro le habla de los astros y de lo grande que es la tierra"),
                    suceso("manto", "10-04", "Su madre termina de tejerle un manto de lana a cuadros para el invierno"),
                    mundo("tasgecio", "10-20", "Llega la noticia: los carnutos han matado a Tasgecio, el rey que les puso César", certeza = A, fuente = F_CARNUTOS),
                    SAMONIOS,
                    mundo("atuatuca", "11-28", "Corre la noticia: los eburones de Ambiórix acaban con una legión y media en su invernada", certeza = A, fuente = F_AMBIORIX),
                    suceso("versos_juicios", "12-16", "El maestro le dice que en primavera empezará los versos de los juicios"),
                )),
            TemporadaVida("53 a. C.", "inquietud",
                "Bajan los lobos en el invierno. César llama a la Galia a Cortes y las lleva a Lutecia; los senones de Acón piden perdón. Segomaros lleva su primer mensaje a otro oppidum. El maestro vuelve a la asamblea de los carnutos. César echa otro puente sobre el Rin y llama a los galos a saquear la tierra de los eburones. Segomaros ve al maestro juzgar un pleito de lindes. Prometen a su hermana. En otoño, César hace matar a Acón ante la asamblea de la Galia en Durocortorum. En el bosque, un noble joven habla de recobrar la libertad.",
                listOf(
                    mundo("lobos", "01-20", "En lo peor del invierno bajan los lobos hasta los rediles de la meseta", certeza = INV, escena = "nieve", clima = Clima.NIEVE),
                    muerdago("02-04"),
                    mundo("cortes", "03-18", "César convoca a la Galia en Lutecia: no van los senones, los carnutos ni los tréveros", certeza = A, fuente = F_LUTECIA),
                    siembra("03-26"),
                    mundo("senones", "03-30", "Los senones de Acón piden perdón a César y le dan cien rehenes", certeza = A, fuente = F_ACON),
                    suceso("primer_mensaje", "04-12", "Lleva su primer mensaje, de parte de su padre, a otro oppidum de los arvernos"),
                    parteMaestro("05-08"),
                    asamblea("05-20"),
                    vuelveMaestro("06-02"),
                    mundo("rin", "06-25", "Dicen que César ha vuelto a echar un puente sobre el Rin", certeza = A, fuente = F_RIN),
                    siega("07-10"),
                    suceso("juicio", "07-28", "Ve al maestro juzgar un pleito de lindes entre dos familias"),
                    guerra("eburones", "08-20", "César llama a los galos a saquear la tierra de los eburones", certeza = A, fuente = F_AMBIORIX),
                    suceso("promesa", "09-06", "Prometen a su hermana con un joven arverno de la montaña"),
                    mundo("acon", "10-18", "Llega la noticia de Durocortorum: César ha hecho matar a Acón ante la asamblea de la Galia", certeza = A, fuente = F_ACON, escena = "luto"),
                    SAMONIOS,
                    mundo("arenga", "11-30", "En una reunión en el bosque oye a un noble joven hablar de recobrar la libertad", certeza = INV),
                    suceso("yegua_coja", "12-12", "Su yegua se lastima una mano en el hielo, y la cuida noche y día"),
                )),
            TemporadaVida("52 a. C.", "levantamiento",
                "El grito de Cenabum llega a Gergovia en la noche. Vercingétorix, echado de Gergovia por su tío, vuelve con la gente del campo y lo proclaman rey. Segomaros deja la escuela, porque los druidas no van a la guerra, y se une a los jinetes. César cruza las Cevenas en la nieve. La tierra quemada; Avárico cae. César llega al pie de Gergovia y se va vencido. En Bibracte toda la Galia que acude quiere a Vercingétorix por general. En la batalla de caballería lo hieren. En Alesia, la caballería sale de noche con su yegua; echan a los mandubios; llega el socorro y se pierde. Vercingétorix se entrega. César devuelve los cautivos arvernos. Esunertos no vuelve.",
                listOf(
                    guerra("grito", "01-23", "Corre el grito de campo en campo: en Cenabum los carnutos han matado a los romanos", certeza = A, fuente = F_GUERRE, escena = "grito"),
                    mundo("echado", "02-05", "Vercingétorix llama a las armas, y su tío Gobanición y los nobles lo echan de Gergovia", certeza = A, fuente = F_VERCINGETORIX),
                    mundo("rey", "02-12", "Vercingétorix vuelve con la gente del campo, echa a los nobles y lo proclaman rey", certeza = A, fuente = F_VERCINGETORIX, escena = "rey"),
                    suceso("deja_escuela", "02-16", "Deja la escuela del robledal: los druidas no van a la guerra, y él sí"),
                    guerra("cevenas", "02-24", "César cruza las Cevenas por la nieve y entra en tierra de los arvernos", certeza = A, fuente = F_GUERRE, escena = "humo", clima = Clima.NIEVE),
                    suceso("se_une", "03-02", "Se une con su yegua a los jinetes de Vercingétorix"),
                    guerra("tierra_quemada", "03-14", "La tierra quemada: los bitúriges prenden fuego a sus propias ciudades", certeza = A, fuente = F_VERCINGETORIX, escena = "humo"),
                    guerra("avarico", "03-22", "César pone cerco a Avárico, la ciudad de los bitúriges", certeza = A, fuente = F_AVARICO),
                    guerra("avarico_cae", "04-14", "Cae Avárico: de toda su gente se salvan unos pocos cientos", certeza = A, fuente = F_AVARICO, escena = "luto"),
                    suceso("vuelve_gergovia", "04-30", "Vuelve a Gergovia con el ejército: César baja por el Allier"),
                    guerra("cesar_gergovia", "05-05", "César llega al pie de Gergovia y levanta sus campamentos en la llanura", certeza = A, fuente = F_GERGOVIA, escena = "romanos"),
                    guerra("victoria", "05-20", "Los romanos suben al asalto de Gergovia y se tienen que volver: la victoria es gala", certeza = A, fuente = F_GERGOVIA, escena = "victoria"),
                    suceso("a_bibracte", "06-14", "Sale otra vez con los jinetes, hacia Bibracte"),
                    mundo("bibracte", "06-24", "En la asamblea de Bibracte, todos los que han ido quieren a Vercingétorix por general", certeza = A, fuente = F_VERCINGETORIX),
                    guerra("caballeria", "08-14", "La batalla de caballería: los jinetes galos se rompen, y a él lo hieren en el brazo", certeza = A, fuente = F_GUERRE),
                    guerra("alesia", "08-17", "Vercingétorix se encierra en Alesia, y César empieza a cavar alrededor", certeza = A, fuente = F_ALESIA),
                    guerra("sale_caballeria", "08-25", "De noche sale de Alesia toda la caballería, a pedir ayuda: Esunertos se lleva su yegua", certeza = A, fuente = F_ALESIA),
                    mundo("mandubios", "09-10", "Echan de Alesia a los mandubios con sus mujeres y sus hijos, y César no los deja pasar", certeza = A, fuente = F_ALESIA),
                    guerra("socorro", "09-22", "Llega el ejército de socorro de toda la Galia y llena las colinas de fuera", certeza = A, fuente = F_ALESIA, escena = "socorro"),
                    guerra("ultima_batalla", "09-26", "La última batalla: el socorro ataca las líneas romanas y no las rompe", certeza = A, fuente = F_ALESIA, escena = "batalla"),
                    guerra("entrega", "09-27", "Vercingétorix se entrega a César, y los galos tiran las armas a sus pies", certeza = A, fuente = F_ALESIA, escena = "entrega"),
                    mundo("liberados", "10-08", "César devuelve a los arvernos sus cautivos: vuelve a pie a Gergovia", 5, A, F_ALESIA, escena = "camino"),
                    suceso("esunertos", "10-24", "Sabe que Esunertos cayó en la última batalla, fuera de Alesia"),
                    fiesta("samonios_52", "11-17", "Las tres noches de Samonios, el año de Alesia", 3, A, F_COLIGNY, escena = "samonios"),
                    suceso("visita_maestro", "12-12", "Sube al robledal a ver al maestro, y el maestro no le pregunta nada"),
                )),
            TemporadaVida("51 a. C.", "resistencia",
                "Segomaros vive en Gergovia con la marca de la herida en el brazo, lleva mensajes y ya no va a la escuela. César cae en invierno sobre los carnutos. Su padre le da un potro. Su hermana se casa en la montaña. Los belóvacos se levantan; vuelven los mercaderes de Italia. Lucterio, el de los cadurcos, sigue la guerra: él quiere irse y no se va. César hace matar al gutuater de los carnutos. Uxellodunum cae porque le cortan el agua. El arverno Epasnacto entrega a Lucterio a los romanos. El maestro le manda decir que hay sitio para él; Commio hace la paz; él decide volver.",
                listOf(
                    rito("muerdago_lejos", "01-12", "El sexto día de la luna: mira desde lejos cómo cortan el muérdago", certeza = A, fuente = F_MUERDAGO, escena = "muerdago"),
                    guerra("carnutos_invierno", "01-25", "César cae en pleno invierno sobre los carnutos", certeza = A, fuente = F_CARNUTOS),
                    suceso("potro", "02-20", "Su padre le da un potro bayo que nadie ha montado"),
                    siembra("03-26"),
                    suceso("boda", "04-15", "La boda de su hermana, que se va a vivir a la montaña"),
                    guerra("belovacos", "05-06", "Los belóvacos se levantan contra César, y su jefe Correo muere peleando", certeza = A, fuente = F_BELOVACOS),
                    ANIVERSARIO_GERGOVIA,
                    mundo("mercaderes", "06-03", "Vuelven al mercado de Gergovia los mercaderes de Italia, con su vino", certeza = INV),
                    guerra("lucterio", "06-12", "Lucterio, el de los cadurcos, junta gente para seguir la guerra", certeza = A, fuente = F_LUCTERIO),
                    suceso("no_va", "06-18", "Quiere irse con Lucterio, y no se va"),
                    mundo("gutuater", "07-03", "César hace matar al gutuater de los carnutos, al que llama autor de la guerra", certeza = A, fuente = F_CARNUTOS),
                    siega("07-10"),
                    guerra("uxellodunum", "07-24", "Los romanos cercan Uxellodunum, el oppidum de los cadurcos", certeza = A, fuente = F_UXELLODUNUM),
                    guerra("uxellodunum_cae", "08-26", "Uxellodunum se rinde: los romanos le han cortado el agua", certeza = A, fuente = F_UXELLODUNUM, escena = "luto"),
                    mundo("lucterio_entregado", "09-12", "Epasnacto, un jefe arverno, entrega a Lucterio a los romanos", certeza = A, fuente = F_LUCTERIO),
                    ANIVERSARIO_ALESIA,
                    suceso("maestro_llama", "10-20", "El maestro le manda decir que en la escuela todavía hay sitio para él"),
                    SAMONIOS,
                    mundo("commio", "12-06", "Dicen que Commio, el de los atrébates, ha hecho la paz con los romanos", certeza = A, fuente = F_COMMIO),
                    suceso("decide", "12-20", "Decide volver a la escuela del robledal cuando pase el invierno"),
                )),
            TemporadaVida("50 a. C.", "conquista",
                "Segomaros vuelve a la escuela del robledal y a los versos; Korisios va ya muy por delante. Un mercader cuenta que Vercingétorix sigue preso en Roma. El maestro vuelve a la asamblea de los carnutos, la primera de la Galia vencida. En un templo ve la espada que dicen que fue de César. Su hermana tiene un hijo. El maestro le deja decir los versos en un juicio. Un mercader de Italia le ofrece plata por los versos escritos, y él no los escribe. César se lleva legiones hacia Italia: en Roma se prepara otra guerra.",
                listOf(
                    rito("vuelve_escuela", "01-10", "Vuelve a la escuela del robledal, a los versos", certeza = INV),
                    muerdago("01-31"),
                    suceso("korisios", "02-18", "Korisios ya sabe versos que él todavía no ha empezado"),
                    mundo("preso", "03-10", "Un mercader dice que Vercingétorix sigue preso en Roma", certeza = A, fuente = F_VERCINGETORIX),
                    siembra("03-26"),
                    parteMaestro("05-10"),
                    ANIVERSARIO_GERGOVIA,
                    asamblea("05-22"),
                    vuelveMaestro("06-04"),
                    suceso("espada", "06-20", "En un templo de los arvernos ve colgada la espada corta que dicen que fue de César"),
                    siega("07-10"),
                    suceso("sobrino", "08-14", "Su hermana tiene un hijo en la montaña"),
                    suceso("juicio_versos", "09-05", "En un juicio, el maestro le deja decir a él los versos de la ley"),
                    ANIVERSARIO_ALESIA,
                    suceso("no_escribe", "10-15", "Un mercader de Italia le ofrece plata por los versos escritos, y no los escribe"),
                    SAMONIOS,
                    mundo("legiones", "12-02", "Dicen que César saca legiones de la Galia: en Roma se prepara otra guerra", certeza = A, fuente = F_GUERRA),
                    nieve("12-14"),
                )),
        ),
        arcos = listOf(
            // El trigo de la llanura: sembrado en otoño, verde en primavera, alto en junio y segado en julio. [Aproximación]
            Arco.cadaAnio("trigo", TipoArco.CULTIVO, "01-01" to 0.15, "03-25" to 0.25, "06-01" to 0.75, "07-09" to 1.0, "07-10" to 0.0, "10-20" to 0.0, "11-10" to 0.1, "12-31" to 0.15, saltos = setOf("07-10")),
            // Los campamentos de César al pie de Gergovia: del día en que llega al día en que cruza el Allier hacia los eduos (7.53).
            Arco("romanos", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(3, "05-05", 1.0, salto = true), p(3, "05-23", 0.0, salto = true), p(5, "12-31", 0.0))),
            // Las granjas quemadas de la llanura: cuando César cruza las Cevenas, y más cuando llega a Gergovia; se rehacen despacio.
            Arco("danos", TipoArco.DANOS, listOf(p(1, "01-01", 0.0), p(3, "02-24", 0.5, salto = true), p(3, "05-04", 0.45), p(3, "05-05", 0.6, salto = true), p(4, "06-01", 0.3), p(5, "12-31", 0.1))),
            // La muralla de la meseta, de madera, piedra y tierra: se refuerza en la primavera del 52.
            Arco("muralla", TipoArco.OBRA, listOf(p(1, "01-01", 0.6), p(3, "03-01", 0.6), p(3, "04-30", 1.0), p(5, "12-31", 1.0))),
            // Los carros y las ánforas de los mercaderes de Italia en la puerta: se van con Cenabum y vuelven en la 4.
            Arco("comercio", TipoArco.OTRO, listOf(p(1, "01-01", 0.4), p(3, "01-23", 0.0, salto = true), p(4, "06-03", 0.35, salto = true), p(5, "12-31", 0.6))),
        ),
        objetos = listOf("torques", "moneda", "hoz", "tablilla", "espada", "lanza", "escudo", "manto", "fibula", "caldero", "cuerno", "carnyx"),
        estados = listOf(
            Estado("campana", "se_une", "vuelve_gergovia"), Estado("campana", "a_bibracte", "alesia"),
            Estado("alesia", "alesia", "entrega"), Estado("cautivo", "entrega", "liberados"),
        ),
    )
}
