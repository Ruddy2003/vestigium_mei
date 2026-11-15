package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Arco
import io.github.ruddy2003.vestigium.vidas.Calendario
import io.github.ruddy2003.vestigium.vidas.DiaEspecial
import io.github.ruddy2003.vestigium.vidas.Estado
import io.github.ruddy2003.vestigium.vidas.TemporadaVida
import io.github.ruddy2003.vestigium.vidas.TipoArco

/*
 * Los calendarios del grupo 9: las otras vidas de los temas del lote 10 (`docs/diseno/PLAN_TEMAS.md`, 10a a 10g). Por
 * ahora, la de Frente exterior (10c): Tadeo, el ladrón de piezas al que recluta la red. Siguen las reglas de
 * `AnioPiloto.kt` y el estilo de los de ficción de `AnioG7.kt`.
 *
 * Lo propio de este grupo:
 *
 * - **Frente exterior es ficción** (`ficticia = true`): todo es inventado, también las fiestas, y nada lleva enlace.
 *   Toma el género de *Andor* (espionaje, resistencia, mundos ocupados, ciencia ficción de los años 70 hecha a mano),
 *   no la saga: ni sus planetas, ni su gente, ni sus naves, ni sus soldados, ni sus palabras. El régimen es el
 *   Directorio y la red, el Frente, que trabaja en células con una letra y un número.
 * - Los otros cuatro temas del lote (Arcontes, Galia, Art nouveau y Psicodelia) son personas inventadas en mundos
 *   reales (10a): cuando lleguen, sus calendarios podrán tener días históricos con su fuente.
 */
internal object AnioG9 {
    val TODOS: List<Calendario> by lazy { listOf(AGENTE) }

    /** Días «históricos» sin enlace porque su fecha sale de un cálculo: en la ficción no hay ninguno. */
    val CALCULADOS = emptySet<String>()

    private val I = INVENTADO

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
}
