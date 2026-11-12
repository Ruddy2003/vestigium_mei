package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Arco
import io.github.ruddy2003.vestigium.vidas.Calendario
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.DiaEspecial
import io.github.ruddy2003.vestigium.vidas.Estado
import io.github.ruddy2003.vestigium.vidas.RegimenLuz
import io.github.ruddy2003.vestigium.vidas.TemporadaVida
import io.github.ruddy2003.vestigium.vidas.TipoArco

/*
 * Los calendarios del grupo 7 (tanda A7): las cinco temporadas (2027 a 2031) de los personajes de ficción de la
 * astronauta (Espacio), la recolectora de semillas (Ciudad verde), el funcionario (Distopía), la residente (Refugio) y el
 * archivista (Horror cósmico). Siguen las reglas de `AnioPiloto.kt`.
 *
 * Lo propio de este grupo:
 *
 * - **Todo es inventado**, también las fiestas: en un personaje de ficción no hay nada «histórico» ni con enlace. Sus
 *   mundos, sus fiestas y su gente son de cada historia, sin nombres, lugares ni sucesos de ninguna obra.
 * - **La astronauta va rumbo a Alfa Centauri, solo de ida** (su vida se rehízo en esta misma tanda). Sus cinco
 *   temporadas son los años 15 a 19 del viaje. Lo único que sale de la física de verdad es cuándo la estrella de delante
 *   empieza a brillar más que el Sol (ver `docs/historia/fuentes.md`).
 * - **La nave y el refugio no tienen cielo ni estaciones:** su clima es «sin clima» y su luz, la de dos estados
 *   (encendida de día y tenue de noche). Un día especial puede traer clima de verdad: la lluvia, cuando se abre la puerta.
 * - Los nombres de las temporadas no son años en todos: «año 15 del viaje», «año 40 tras el silencio».
 */
internal object AnioG7 {
    val TODOS: List<Calendario> by lazy { listOf(ASTRONAUTA, SEMILLERA, FUNCIONARIO, RESIDENTE, ARCHIVISTA) }

    /** Días «históricos» sin enlace porque su fecha sale de un cálculo: en la ficción no hay ninguno. */
    val CALCULADOS = emptySet<String>()

    private val I = INVENTADO
    private val SIN_CLIMA = List(12) { mapOf(Clima.SIN_CLIMA to 1) }

    // =====================================================================
    //  La astronauta, ingeniera de vuelo. Una nave rumbo a Alfa Centauri, años 15 a 19 del viaje.
    // =====================================================================
    //
    // Su gente (toda inventada): Ade, el botánico de la granja de a bordo, con quien vive desde la temporada 4. Sana, su
    // aprendiz, que subió a la nave con cinco años (temporada 1), aprueba en la 2 y tiene una hija en la 5. La comandante
    // Varga, que deja el mando en la 4. Bao, el médico, cuyo hijo Nilo nace en la 3. Su madre, en la Tierra, que muere
    // antes de la temporada 2 (la noticia llega dos años tarde).
    //
    // El viaje: salió hace quince años, va a una décima parte de la velocidad de la luz y tardará más de cuarenta. Los
    // mensajes de la Tierra llegan con casi dos años de retraso. En la temporada 4 la estrella de delante empieza a
    // brillar más que el Sol, que queda detrás.
    //
    // Claves de escena: fiesta, luto, alarma, fuera (sale al casco), cosecha. Arcos: viaje (la parte del camino hecha) y
    // granja (lo que ha crecido en los cultivos de a bordo). Estados: «parada» (cada octubre, diez días con el reactor
    // parado y media luz) e «ingravidez» (temporada 3: tres semanas con el anillo parado, sin peso).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la nave por dentro con las plantas de la
    // granja según `granja`; todo más oscuro durante «parada»; y las cosas flotando, sueltas, durante «ingravidez».

    /** Lo que vuelve cada año en la nave. */
    private val anioDeLaNave: List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo de a bordo: se brinda con el vino de la granja", certeza = I),
        rito("revision", "02-10", "La revisión médica del año: huesos, ojos y sangre", certeza = I),
        rito("correo", "03-15", "Llega el paquete de mensajes de la Tierra, que salió hace casi dos años", certeza = I),
        fiesta("partida", "04-22", "El aniversario de la partida: se apagan las luces y se busca el Sol", certeza = I),
        rito("siembra", "05-05", "Se siembra el trigo enano en la granja", certeza = I),
        fiesta("juegos", "07-01", "Los juegos del anillo: carreras por el pasillo que da la vuelta", certeza = I),
        cosecha("cosecha", "08-20", "La cosecha del trigo enano de la granja", 4, certeza = I),
        rito("correo_otono", "09-15", "Llega el segundo paquete de mensajes del año", certeza = I),
        rito("parada", "10-05", "Se para el reactor para su revisión: diez días con media luz", certeza = I, escena = "alarma"),
        rito("fin_parada", "10-15", "Se enciende otra vez el reactor, y vuelve la luz entera", certeza = I),
        rito("asamblea", "11-15", "La asamblea del año: qué se planta, qué se arregla, quién tiene hijos", certeza = I),
        fiesta("luces", "12-21", "La fiesta de las luces: una lámpara por cada uno que se quedó en la Tierra", certeza = I),
    )

    val ASTRONAUTA = Calendario(
        vida = "astronauta",
        estaciones = Calendario.SIN_ESTACIONES,
        clima = SIN_CLIMA,
        luz = RegimenLuz.DOS_ESTADOS,
        temporadas = listOf(
            TemporadaVida("año 15 del viaje", "crucero",
                "Un grano de polvo golpea el escudo. La astronauta ve el mensaje de su madre, grabado hace dos años. Sale por primera vez al casco. Le dan una aprendiz, Sana. Una fuga de agua. El telescopio de proa ve un planeta junto a la estrella de delante. Nace un niño a bordo.",
                anioDeLaNave + listOf(
                    mundo("polvo", "02-25", "Un grano de polvo choca contra el escudo y la nave suena como una campana", certeza = I, escena = "alarma"),
                    suceso("video_madre", "03-17", "Ve el mensaje de su madre, grabado hace casi dos años"),
                    suceso("casco", "05-20", "Sale por primera vez al casco de la nave a reparar una antena", escena = "fuera"),
                    suceso("aprendiz", "06-10", "Le dan una aprendiz: Sana, que subió a la nave con cinco años"),
                    mundo("fuga", "07-20", "Una fuga en un tanque de agua: todos a la sala de máquinas", certeza = I, escena = "alarma"),
                    mundo("planeta", "09-02", "En una foto del telescopio de proa aparece un planeta junto a la estrella de delante", certeza = I),
                    mundo("nace_nino", "11-28", "Nace un niño a bordo, el séptimo del viaje", certeza = I, escena = "fiesta"),
                )),
            TemporadaVida("año 16 del viaje", "noticia",
                "Llega en el correo la noticia de que la madre de la astronauta murió hace casi dos años. Le graba un mensaje igual. Se rompe una bomba del reactor. Una lluvia de rayos cósmicos los manda al refugio del centro. Sana aprueba el examen de ingeniera. Florece el primer manzano de la nave.",
                anioDeLaNave + listOf(
                    suceso("noticia", "03-16", "Llega la noticia: su madre murió en la Tierra hace casi dos años", escena = "luto"),
                    suceso("responde", "03-26", "Graba un mensaje para su madre, aunque ya no esté"),
                    mundo("bomba", "06-18", "Se rompe una bomba del circuito que enfría el reactor", certeza = I, escena = "alarma"),
                    mundo("rayos", "07-28", "Una lluvia de rayos cósmicos: tres días todos en el refugio del centro", 3, certeza = I, escena = "alarma"),
                    suceso("sana_aprueba", "09-28", "Sana aprueba el examen de ingeniera de vuelo"),
                    mundo("manzano", "10-25", "Florece el primer manzano de la granja de a bordo", certeza = I),
                )),
            TemporadaVida("año 17 del viaje", "anillo",
                "Nace Nilo, el hijo de Bao, y la astronauta es su madrina. El anillo se para tres semanas para cambiar un cojinete, y nadie pesa nada. Sana sale al casco por primera vez. Dejan atrás los últimos cometas del Sol. Ade le pide que vivan juntos.",
                anioDeLaNave + listOf(
                    suceso("nilo", "02-20", "Nace Nilo, el hijo de Bao, y ella es su madrina", escena = "fiesta"),
                    mundo("para_anillo", "05-10", "Se para el anillo para cambiar un cojinete: tres semanas sin peso", certeza = I, escena = "alarma"),
                    mundo("gira_anillo", "06-01", "El anillo vuelve a girar y todos vuelven a pesar", certeza = I),
                    suceso("sana_casco", "08-12", "Sana sale al casco por primera vez, y ella le lleva la cuerda", escena = "fuera"),
                    mundo("cometas", "10-25", "Los astrónomos de a bordo dicen que han dejado atrás los últimos cometas del Sol", certeza = I),
                    suceso("ade", "12-05", "Ade le pide que vivan juntos en el mismo camarote"),
                )),
            TemporadaVida("año 18 del viaje", "delante",
                "La astronauta se muda al camarote de Ade. Una gripe deja a media nave en cama. Le escribe una sobrina que no conoce. La comandante Varga deja el mando. La estrella de delante ya brilla más que el Sol. Se agrieta una placa del escudo. La nombran jefa de ingenieros.",
                anioDeLaNave + listOf(
                    suceso("camarote", "02-14", "Se muda al camarote de Ade"),
                    mundo("gripe", "02-26", "Una gripe que nadie sabe de dónde salió deja a media nave en cama", certeza = I),
                    suceso("sobrina", "03-17", "Le llega un mensaje de una sobrina que nació después de la partida"),
                    mundo("comandante", "06-12", "La comandante Varga deja el mando, y la asamblea elige a otra", certeza = I),
                    mundo("brillante", "07-15", "La estrella de delante ya brilla más que el Sol, que queda detrás", certeza = I, escena = "fiesta"),
                    mundo("escudo", "09-25", "Se agrieta una placa del escudo de proa y hay que cambiarla desde fuera", certeza = I, escena = "fuera"),
                    suceso("jefa", "11-02", "La nombran jefa de ingenieros de la nave"),
                )),
            TemporadaVida("año 19 del viaje", "nacimientos",
                "Sana tiene una hija. A Ade le encuentran un bulto en el pulmón y lo operan, y sale bien. Un hongo se come una parte del trigo. La astronauta empieza a dar clase a los niños nacidos a bordo. El telescopio ve una aurora en el planeta de delante.",
                anioDeLaNave + listOf(
                    suceso("hija_sana", "04-10", "Sana tiene una hija, y le pide que sea la primera en tenerla en brazos", escena = "fiesta"),
                    suceso("ade_bulto", "06-20", "A Ade le encuentran un bulto en el pulmón", cadena = "ade"),
                    suceso("ade_opera", "07-05", "Operan a Ade en la enfermería, y sale bien", cadena = "ade"),
                    mundo("hongo", "08-10", "Un hongo se come una parte del trigo antes de la cosecha", certeza = I),
                    suceso("clases", "09-08", "Empieza a dar clase de motores a los niños nacidos a bordo"),
                    mundo("aurora", "10-20", "El telescopio de proa ve una aurora en el planeta de delante", certeza = I),
                )),
        ),
        arcos = listOf(
            Arco("viaje", TipoArco.OTRO, listOf(p(1, "01-01", 0.34), p(5, "12-31", 0.45))),
            Arco.cadaAnio("granja", TipoArco.OTRO, "01-01" to 0.45, "08-19" to 1.0, "08-20" to 0.1, "12-31" to 0.44, saltos = setOf("08-20")),
        ),
        objetos = listOf("tableta", "llave", "botella", "foto", "arnes", "lampara", "auriculares", "semilla", "traje", "taza", "cuaderno", "planta"),
        estados = listOf(Estado("parada", "parada", "fin_parada"), Estado("ingravidez", "para_anillo", "gira_anillo")),
    )

    // =====================================================================
    //  La recolectora de semillas. Una ciudad que el bosque recuperó, años 40 a 44 tras el silencio.
    // =====================================================================
    //
    // Su gente (toda inventada): unas sesenta personas en un edificio de oficinas con huertos en las azoteas. La vieja
    // Amalia, la última que se acuerda de antes del silencio, que muere en la temporada 4. Teo, el carpintero, con quien
    // vive desde la 2; su hija nace al final de la 4. Lía, una niña de nueve años a la que enseña a guardar semillas
    // desde la 1. La gente del río, otro campamento, con la que se cambian cosas.
    //
    // Claves de escena: fiesta, nieve, humo, luto, cosecha. Arcos: huerto (lo que ha crecido en las azoteas), bosque
    // (cuánto se ha comido el bosque la ciudad) y puente (el puente viejo que se arregla de la temporada 3 a la 4).
    // Estado: «expedicion» (temporada 2, al jardín botánico viejo; temporada 5, a la costa).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la azotea con el huerto según `huerto`, la
    // ciudad tragada por el bosque según `bosque` y el puente según `puente`; y durante «expedicion», un camino entre
    // ruinas lejos de casa.

    private fun anioDeLaCiudad(): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "El primer día del año: se cuenta en voz alta a los que quedan", certeza = I),
        rito("deshielo", "03-01", "Se rompe el hielo del río, y empieza el año de las semillas", certeza = I),
        fiesta("reparto", "03-20", "La fiesta del reparto: las semillas del año para cada azotea", certeza = I),
        rito("silencio", "05-12", "El día del silencio: un minuto sin hablar por los de antes", certeza = I),
        fiesta("trueque", "06-21", "La feria del trueque con la gente del río", 2, certeza = I),
        cosecha("cerezas", "07-25", "Se recogen las cerezas de los árboles de la avenida", 3, certeza = I),
        cosecha("cosecha", "09-05", "La cosecha grande de las azoteas", 5, certeza = I),
        rito("latas", "10-01", "Se sellan las latas de semillas del año en el sótano", certeza = I),
        rito("tapar", "11-01", "Se tapan los huertos con paja para el invierno", certeza = I),
        fiesta("noche_larga", "12-21", "La noche más larga: fogata grande en la azotea más alta", certeza = I),
    )

    val SEMILLERA = Calendario(
        vida = "semillera",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 6, nubes = 12, lluvia = 4, nieve = 7, niebla = 2), mes(despejado = 7, nubes = 11, lluvia = 4, nieve = 6),
            mes(despejado = 9, nubes = 11, lluvia = 7, nieve = 3, viento = 1), mes(despejado = 10, nubes = 10, lluvia = 9, viento = 1),
            mes(despejado = 13, nubes = 9, lluvia = 8, tormenta = 1), mes(despejado = 14, nubes = 8, lluvia = 6, tormenta = 2),
            mes(despejado = 16, nubes = 7, lluvia = 5, tormenta = 3), mes(despejado = 15, nubes = 7, lluvia = 6, tormenta = 3),
            mes(despejado = 12, nubes = 9, lluvia = 7, niebla = 2), mes(despejado = 9, nubes = 11, lluvia = 8, niebla = 3),
            mes(despejado = 6, nubes = 13, lluvia = 8, nieve = 1, niebla = 2), mes(despejado = 5, nubes = 13, lluvia = 5, nieve = 6, niebla = 2),
        ),
        temporadas = listOf(
            TemporadaVida("año 40 tras el silencio", "hiedra",
                "Se oyen lobos en la avenida. Se cae la torre de cristal de la plaza. La recolectora empieza a enseñarle a Lía a guardar semillas. Un rayo quema el edificio de enfrente. Encuentra en la biblioteca un libro de plantas con dibujos. Pasan ciervos por la calle del mercado. Amalia le cuenta cómo era antes.",
                anioDeLaCiudad() + listOf(
                    mundo("lobos", "01-25", "Se oyen lobos en la avenida grande, por primera vez desde que ella nació", certeza = I, escena = "nieve"),
                    mundo("torre", "04-14", "Se cae la torre de cristal de la plaza, que llevaba años inclinada", certeza = I, escena = "humo"),
                    suceso("lia", "05-25", "Empieza a enseñarle a Lía, de nueve años, a guardar semillas"),
                    mundo("rayo", "07-10", "Un rayo cae en el edificio de enfrente y lo quema entero", certeza = I, escena = "humo", clima = Clima.TORMENTA),
                    suceso("libro", "08-08", "Encuentra en la biblioteca vieja un libro de plantas con dibujos"),
                    mundo("ciervos", "10-18", "Una manada de ciervos pasa por la calle del mercado", certeza = I),
                    suceso("amalia_cuenta", "11-20", "La vieja Amalia le cuenta cómo era la ciudad antes del silencio"),
                )),
            TemporadaVida("año 41 tras el silencio", "jardin",
                "Una nevada encierra a todos una semana. El río entra en los sótanos y salvan las latas de semillas. La recolectora va con Teo al jardín botánico viejo, al otro lado del río, y vuelve con semillas raras. Llegan cuatro forasteros del sur. Teo le pide que vivan juntos.",
                anioDeLaCiudad() + listOf(
                    mundo("nevada", "01-28", "Una nevada tan grande que nadie sale del edificio en una semana", certeza = I, escena = "nieve", clima = Clima.NIEVE),
                    mundo("crecida", "03-10", "El río crece y entra en los sótanos: hay que salvar las latas de semillas", certeza = I, clima = Clima.LLUVIA),
                    suceso("sale_jardin", "07-01", "Sale con Teo hacia el jardín botánico viejo, al otro lado del río"),
                    suceso("invernadero", "07-10", "Encuentra el invernadero del jardín, roto, con plantas que nadie conoce"),
                    suceso("vuelve_jardin", "07-22", "Vuelve a casa con una mochila llena de semillas raras"),
                    mundo("forasteros", "10-12", "Llegan cuatro forasteros del sur, flacos y con un perro", certeza = I),
                    suceso("teo", "12-02", "Teo le pide que vivan juntos en la oficina del piso nueve"),
                )),
            TemporadaVida("año 42 tras el silencio", "puente",
                "Empiezan a arreglar el puente viejo con la gente del río. Germina una de las semillas raras del jardín. Amalia se cae en la escalera y ya no sube a la azotea. Arde el parque del norte. Lía lee sola una etiqueta. Alguien enciende una radio vieja y solo se oye ruido.",
                anioDeLaCiudad() + listOf(
                    mundo("puente_empieza", "04-01", "Empiezan a arreglar el puente viejo con la gente del río", certeza = I),
                    suceso("germina", "05-30", "Germina por primera vez una de las semillas raras del jardín"),
                    suceso("amalia_cae", "08-15", "La vieja Amalia se cae en la escalera y ya no sube a la azotea"),
                    mundo("incendio", "08-28", "Arde el parque del norte, y el humo tapa el sol tres días", 3, certeza = I, escena = "humo", clima = Clima.DESPEJADO),
                    suceso("lia_lee", "11-15", "Lía lee sola, por primera vez, la etiqueta de una lata"),
                    mundo("radio", "12-10", "Alguien enciende una radio vieja con una dinamo, y solo se oye ruido", certeza = I),
                )),
            TemporadaVida("año 43 tras el silencio", "despedida",
                "Muere la vieja Amalia. La recolectora le dice a Teo que espera un hijo. Una granizada destroza los huertos. Se termina el puente, y la feria del trueque se hace encima. Los lobos se llevan una cabra. Nace su hija en la azotea.",
                anioDeLaCiudad() + listOf(
                    suceso("amalia_muere", "02-08", "Muere la vieja Amalia, la última que se acordaba de antes", escena = "luto"),
                    suceso("espera", "04-18", "Le dice a Teo que espera un hijo"),
                    mundo("granizo", "05-22", "Una granizada destroza los huertos de las azoteas", certeza = I, clima = Clima.TORMENTA),
                    mundo("puente_listo", "06-20", "Se termina el puente viejo: se puede cruzar el río a pie", certeza = I, escena = "fiesta"),
                    mundo("cabra", "10-28", "Los lobos se llevan una cabra del patio", certeza = I),
                    suceso("nace", "12-01", "Nace su hija en la azotea del piso doce, junto a la fogata", escena = "fiesta"),
                )),
            TemporadaVida("año 44 tras el silencio", "costa",
                "Lía enseña a los pequeños a guardar semillas. La recolectora va con la gente del río hasta la costa y ve el mar por primera vez. Su hija dice su primera palabra. Por la radio vieja se oye, por fin, una voz.",
                anioDeLaCiudad() + listOf(
                    suceso("lia_ensena", "04-05", "Lía enseña a los más pequeños a guardar semillas"),
                    suceso("sale_costa", "05-25", "Sale con la gente del río hacia la costa, a buscar sal y semillas de las dunas", cadena = "costa"),
                    suceso("mar", "06-02", "Ve el mar por primera vez", cadena = "costa"),
                    suceso("vuelve_costa", "06-15", "Vuelve a casa con sal, conchas y semillas de las dunas", cadena = "costa"),
                    suceso("palabra", "09-20", "Su hija dice su primera palabra"),
                    mundo("voz", "10-30", "Por la radio vieja se oye, por fin, una voz que habla", certeza = I),
                )),
        ),
        arcos = listOf(
            Arco.cadaAnio("huerto", TipoArco.OTRO, "01-01" to 0.0, "03-19" to 0.0, "09-09" to 1.0, "09-10" to 0.15, "11-01" to 0.05, "12-31" to 0.0, saltos = setOf("09-10")),
            Arco("bosque", TipoArco.OTRO, listOf(p(1, "01-01", 0.7), p(5, "12-31", 0.8))),
            Arco("puente", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(3, "04-01", 0.0), p(4, "06-20", 1.0), p(5, "12-31", 1.0))),
        ),
        objetos = listOf("lata", "lapiz", "mochila", "cuchillo", "tarro", "mapa", "libro", "botas", "cuerda", "silbato", "manta", "semilla"),
        estados = listOf(Estado("expedicion", "sale_jardin", "vuelve_jardin"), Estado("expedicion", "sale_costa", "vuelve_costa")),
    )

    // =====================================================================
    //  El funcionario, empleado de un ministerio. La capital de un Estado que todo lo vigila, de 1984 a 1988.
    // =====================================================================
    //
    // Su gente (toda inventada, y ninguna de ninguna novela): Anselm, su compañero de mesa, que desaparece en la
    // temporada 1. Vera, su hermana, en la provincia, que le escribe cartas con líneas tachadas y a la que visita en la 4.
    // Mira, la del archivo de abajo, con quien habla desde la 2 hasta que la trasladan en la 3. El niño de los vecinos,
    // de la Guardia Joven. El viejo del mercado negro.
    //
    // El Estado y sus fiestas son inventados: el Día del Fundador, el Desfile de la Unidad, la Semana de la Gratitud, el
    // Día de la Victoria Permanente, el Vocabulario Oficial, el examen de lealtad.
    //
    // Claves de escena: fiesta, desfile, apagon, cohetes, luto. Arcos: racion (lo que dan de comer: baja despacio) y
    // camaras (cuántas cámaras hay en las calles: suben). Estados: «campana» (temporadas 2 y 4, tres semanas de noventa
    // horas en el ministerio) y «provincia» (temporada 4, dos días en casa de su hermana).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la calle gris con carteles y cámaras según
    // `camaras`; las ventanas del ministerio encendidas de noche durante «campana»; y un pueblo de provincia, más vacío y
    // menos vigilado, durante «provincia».

    private fun anioDelEstado(): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo: el discurso de la pantalla dura tres horas", certeza = I),
        rito("racion", "02-01", "Se anuncia la ración del trimestre, que según la pantalla ha subido", certeza = I),
        fiesta("fundador", "03-14", "El Día del Fundador: un retrato en cada ventana", certeza = I, escena = "desfile"),
        rito("inspeccion", "04-15", "La inspección de las viviendas: todo abierto, todo contado", certeza = I),
        fiesta("desfile", "05-01", "El Desfile de la Unidad: ocho horas de marcha por la avenida", certeza = I, escena = "desfile"),
        rito("gratitud", "06-10", "La Semana de la Gratitud: cada tarde se dan las gracias en voz alta", 7, certeza = I),
        rito("vocabulario", "08-01", "Sale la nueva edición del Vocabulario Oficial, con trescientas palabras menos", certeza = I),
        fiesta("victoria", "09-09", "El Día de la Victoria Permanente", certeza = I, escena = "desfile"),
        mundo("cifras", "10-20", "La pantalla anuncia que la cosecha ha superado el plan otra vez", certeza = I),
        rito("lealtad", "11-20", "El examen de lealtad de todos los funcionarios del ministerio", certeza = I),
        rito("calefaccion", "12-15", "Se corta la calefacción por las noches, por el bien común", certeza = I),
        fiesta("fin_anio", "12-31", "La última noche del año: brindis obligatorio en la sala comunal", certeza = I),
    )

    val FUNCIONARIO = Calendario(
        vida = "funcionario",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 3, nubes = 15, lluvia = 8, nieve = 3, niebla = 2), mes(despejado = 4, nubes = 14, lluvia = 7, nieve = 2, niebla = 1),
            mes(despejado = 5, nubes = 14, lluvia = 9, viento = 3), mes(despejado = 6, nubes = 13, lluvia = 10, viento = 1),
            mes(despejado = 8, nubes = 12, lluvia = 10, tormenta = 1), mes(despejado = 9, nubes = 11, lluvia = 9, tormenta = 1),
            mes(despejado = 10, nubes = 11, lluvia = 8, tormenta = 2), mes(despejado = 9, nubes = 11, lluvia = 9, tormenta = 2),
            mes(despejado = 7, nubes = 12, lluvia = 10, niebla = 1), mes(despejado = 5, nubes = 14, lluvia = 10, niebla = 2),
            mes(despejado = 4, nubes = 15, lluvia = 9, niebla = 2), mes(despejado = 3, nubes = 15, lluvia = 8, nieve = 3, niebla = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1984", "frente",
                "Anselm, el compañero de mesa del funcionario, no vuelve, y su nombre desaparece de las listas. El funcionario encuentra una foto que no debería existir. Le llega una carta de su hermana con dos líneas tachadas. Un apagón de tres días. La pantalla anuncia una gran victoria en el frente del este.",
                anioDelEstado() + listOf(
                    suceso("anselm", "02-20", "Su compañero de mesa, Anselm, no viene a trabajar, y su nombre desaparece de las listas"),
                    suceso("foto", "05-20", "Encuentra, en un expediente que corrige, una foto que no debería existir"),
                    suceso("carta_vera", "07-12", "Le llega una carta de su hermana, con dos líneas tachadas"),
                    mundo("apagon", "10-05", "Un apagón de tres días en todo el barrio", 3, certeza = I, escena = "apagon"),
                    mundo("victoria_este", "11-02", "La pantalla anuncia una gran victoria en el frente del este", certeza = I),
                )),
            TemporadaVida("1985", "campana",
                "El funcionario conoce a Mira, la del archivo de abajo. Tres semanas de campaña: noventa horas a la semana, durmiendo en el ministerio. Se llevan al vecino de enfrente. Compra café de verdad en el mercado negro. La ración de pan baja, y la pantalla dice que sube.",
                anioDelEstado() + listOf(
                    suceso("mira", "03-05", "Conoce a Mira, la del archivo de abajo, que se ríe sin mirar la pantalla"),
                    mundo("campana", "04-01", "Empieza una campaña: noventa horas a la semana, y se duerme en el ministerio", certeza = I),
                    mundo("fin_campana", "04-22", "Se termina la campaña, y se vuelve a casa", certeza = I),
                    suceso("vecino", "06-25", "Se llevan al vecino de enfrente antes de que amanezca"),
                    suceso("cafe", "09-25", "Compra café de verdad al viejo del mercado negro"),
                    mundo("pan", "11-05", "La ración de pan baja, y la pantalla dice que sube", certeza = I),
                )),
            TemporadaVida("1986", "cohetes",
                "El niño de los vecinos, de la Guardia Joven, le pregunta al funcionario qué escribe. Empieza un diario escondido. A Mira la trasladan sin despedirse. Caen cohetes en el barrio de los obreros. Le cambian la mesa al lado de la ventana.",
                anioDelEstado() + listOf(
                    suceso("nino", "02-25", "El niño de los vecinos, de la Guardia Joven, le pregunta qué escribe"),
                    suceso("diario", "05-10", "Empieza a escribir un diario en un cuaderno escondido detrás del zócalo"),
                    suceso("mira_traslado", "08-20", "A Mira la trasladan a otro ministerio sin despedirse"),
                    mundo("cohetes", "10-15", "Caen cohetes en el barrio de los obreros: dicen que son del enemigo", certeza = I, escena = "cohetes"),
                    mundo("ventana", "12-04", "Le cambian la mesa a una al lado de la ventana, sin explicación", certeza = I),
                )),
            TemporadaVida("1987", "himno",
                "Otra campaña de tres semanas. Al funcionario le dan permiso para visitar a su hermana dos días. Cambia una estrofa del himno y hay que olvidar la vieja. Quema su diario en la estufa.",
                anioDelEstado() + listOf(
                    mundo("campana", "03-20", "Empieza una campaña: noventa horas a la semana, y se duerme en el ministerio", certeza = I),
                    mundo("fin_campana", "04-10", "Se termina la campaña, y se vuelve a casa", certeza = I),
                    suceso("viaje_vera", "06-28", "Le dan permiso para visitar a su hermana en la provincia"),
                    suceso("vuelve_vera", "06-30", "Vuelve de la provincia en el tren de la noche"),
                    mundo("himno", "08-15", "Cambia una estrofa del himno, y hay que olvidar la vieja", certeza = I),
                    suceso("quema", "11-10", "Quema su diario en la estufa, página por página"),
                )),
            TemporadaVida("1988", "torre",
                "Ascienden al funcionario: ahora corrige libros de texto. Se inaugura la torre nueva del ministerio. Ve a Mira en el metro, y ella no lo reconoce, o hace como que no. Lo llaman a la oficina del cuarto piso.",
                anioDelEstado() + listOf(
                    suceso("ascenso", "03-10", "Lo ascienden: ahora corrige los libros de texto de las escuelas"),
                    mundo("torre", "05-15", "Se inaugura la torre nueva del ministerio, la más alta de la capital", certeza = I, escena = "desfile"),
                    suceso("mira_metro", "06-05", "Ve a Mira en el metro; ella no lo reconoce, o hace como que no"),
                    suceso("cuarto_piso", "09-15", "Lo llaman a la oficina del cuarto piso, y vuelve tres horas después sin decir nada"),
                )),
        ),
        arcos = listOf(
            Arco("racion", TipoArco.OTRO, listOf(p(1, "01-01", 0.6), p(2, "11-05", 0.5), p(5, "12-31", 0.35))),
            Arco("camaras", TipoArco.OTRO, listOf(p(1, "01-01", 0.4), p(5, "12-31", 0.9))),
        ),
        objetos = listOf("pantalla", "cartilla", "lapiz", "cuaderno", "ginebra", "abrigo", "pase", "tubo", "cigarrillo", "gafas", "periodico", "llave"),
        estados = listOf(Estado("campana", "campana", "fin_campana"), Estado("provincia", "viaje_vera", "vuelve_vera")),
    )

    // =====================================================================
    //  La residente, técnica de purificación de agua. Un refugio bajo tierra, de 2105 a 2109.
    // =====================================================================
    //
    // Su gente (toda inventada): su madre, cocinera del comedor. Lina, su amiga de siempre, que tiene un hijo en la
    // temporada 2. Ezra, el de los cultivos, con quien vive desde la 3 y que sale afuera con la expedición de la 4. La
    // vieja Dorotea, que se acuerda de las historias de afuera y muere en la 3. El supervisor, que cambia en la 3.
    //
    // Claves de escena: fiesta, alarma, luto, cielo, cosecha. Arcos: reserva (el agua de la cisterna grande) y cultivo
    // (lo que ha crecido en los tanques). Estados: «racionamiento» (temporada 2, cinco semanas con la cisterna rota),
    // «cuarentena» (temporada 3, el sector cerrado por una fiebre) y «abierto» (temporada 5, desde que la Puerta se abre).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el refugio con su luz de día y de noche; las
    // cisternas y los indicadores bajos durante «racionamiento»; compuertas cerradas y luces de aviso durante
    // «cuarentena»; y la Puerta abierta, con luz de verdad entrando, durante «abierto».

    private fun anioDelRefugio(): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo en el refugio: el altavoz cuenta hacia atrás", certeza = I),
        rito("sellado", "01-15", "El aniversario del sellado: un minuto de silencio delante de la Puerta", certeza = I),
        fiesta("fundadores", "03-01", "El Día de los Fundadores: tarta de lata y discos en el comedor", certeza = I),
        rito("filtros", "04-10", "La revisión de los filtros grandes: tres días de trabajo doble", 3, certeza = I),
        cosecha("cosecha", "05-20", "La cosecha grande de los tanques de cultivo", 3, certeza = I),
        rito("prueba", "06-15", "La prueba de aptitudes de los que cumplen dieciséis", certeza = I),
        fiesta("sol_falso", "07-20", "La fiesta del sol falso: las luces del comedor, al máximo todo el día", certeza = I),
        rito("recuento", "09-01", "El recuento del supervisor: cuántos somos, cuánta agua, cuánta comida", certeza = I),
        rito("permisos", "10-10", "Se anuncian los permisos de hijos del año", certeza = I),
        rito("simulacro", "11-11", "Simulacro de cierre: todas las compuertas bajan a la vez", certeza = I, escena = "alarma"),
        fiesta("nochebuena", "12-24", "La Nochebuena del refugio: un árbol de cable y luces", certeza = I),
    )

    val RESIDENTE = Calendario(
        vida = "residente",
        estaciones = Calendario.SIN_ESTACIONES,
        clima = SIN_CLIMA,
        luz = RegimenLuz.DOS_ESTADOS,
        temporadas = listOf(
            TemporadaVida("2105", "sensor",
                "Se rompe la bomba tres y el agua sale marrón. Ezra le regala a la residente un disco que no tiene nadie. El sensor de la entrada marca menos radiación que nunca. A Lina le dan permiso para tener un hijo. La vieja Dorotea le cuenta cómo era la lluvia.",
                anioDelRefugio() + listOf(
                    mundo("bomba_tres", "02-12", "Se rompe la bomba tres, y el agua sale marrón un día entero", certeza = I, escena = "alarma"),
                    suceso("disco", "04-25", "Ezra, el de los cultivos, le regala un disco que no tiene nadie"),
                    mundo("sensor", "08-10", "El sensor de la entrada marca menos radiación fuera que nunca", certeza = I),
                    suceso("lina", "10-12", "A Lina le dan permiso para tener un hijo"),
                    suceso("lluvia", "12-02", "La vieja Dorotea le cuenta cómo era la lluvia"),
                )),
            TemporadaVida("2106", "cisterna",
                "Se agrieta la cisterna grande y hay cinco semanas de racionamiento. Nombran a la residente encargada del turno de las bombas. Nace el hijo de Lina. Se rompe el tocadiscos del comedor. Ezra le pide que pidan un cuarto doble.",
                anioDelRefugio() + listOf(
                    mundo("grieta", "03-14", "Se agrieta la cisterna grande", certeza = I, escena = "alarma"),
                    mundo("racion", "03-15", "Empieza el racionamiento: dos litros de agua por persona al día", certeza = I),
                    mundo("fin_racion", "04-21", "Se termina el racionamiento: la cisterna está arreglada", certeza = I),
                    suceso("encargada", "06-30", "La nombran encargada del turno de las bombas"),
                    suceso("hijo_lina", "07-08", "Nace el hijo de Lina, y ella lo tiene en brazos la primera", escena = "fiesta"),
                    mundo("tocadiscos", "09-20", "Se rompe el tocadiscos del comedor, y el refugio se queda sin música", certeza = I),
                    suceso("ezra", "11-28", "Ezra le pide que pidan juntos un cuarto doble"),
                )),
            TemporadaVida("2107", "senal",
                "Una fiebre en el sector B: cierran las compuertas casi un mes. La residente y Ezra se mudan a un cuarto doble. La radio de la entrada capta una señal de fuera. Arregla sola la bomba grande. Cambian al supervisor. Muere la vieja Dorotea.",
                anioDelRefugio() + listOf(
                    mundo("fiebre", "02-01", "Una fiebre en el sector B: cierran las compuertas", certeza = I, escena = "alarma"),
                    mundo("abre_sector", "02-26", "Se abren otra vez las compuertas del sector B", certeza = I),
                    suceso("cuarto_doble", "05-02", "Se muda con Ezra a un cuarto doble"),
                    mundo("senal", "08-30", "La radio de la entrada capta por primera vez una señal de fuera", certeza = I),
                    suceso("bomba_grande", "09-14", "Arregla sola la bomba grande, la que nadie sabía arreglar"),
                    mundo("supervisor", "11-25", "Cambian al supervisor: el nuevo habla de abrir algún día", certeza = I),
                    suceso("dorotea", "12-10", "Muere la vieja Dorotea", escena = "luto"),
                )),
            TemporadaVida("2108", "expedicion",
                "La asamblea vota mandar a cinco personas afuera. Eligen a Ezra. Se abre la Puerta un minuto y salen los cinco. Vuelven tres, con tierra en las botas y una flor, y Ezra le da la flor a la residente. Les dan permiso para tener un hijo.",
                anioDelRefugio() + listOf(
                    mundo("voto", "03-20", "La asamblea vota mandar a cinco personas afuera", certeza = I),
                    suceso("eligen", "04-02", "Eligen a Ezra para salir afuera"),
                    mundo("salen", "05-10", "Se abre la Puerta un minuto: salen los cinco", certeza = I, escena = "alarma"),
                    mundo("vuelven", "07-28", "Vuelven tres de los cinco, con tierra en las botas y una flor", certeza = I),
                    suceso("flor", "07-29", "Ezra le da la flor: la trajo de afuera para ella"),
                    suceso("permiso", "10-12", "Les dan permiso para tener un hijo"),
                )),
            TemporadaVida("2109", "puerta",
                "La residente espera un hijo. Empiezan a coser trajes para salir todos. El niño se mueve por primera vez. Eligen un nombre de afuera. Se abre la Puerta para siempre y todos ven el cielo. Llueve, y ella sale a mojarse.",
                anioDelRefugio() + listOf(
                    suceso("espera", "03-08", "Le dice a Ezra que espera un hijo", cadena = "hijo"),
                    mundo("trajes", "06-01", "Empiezan a coser trajes para salir todos", certeza = I),
                    suceso("patada", "06-20", "El niño se mueve por primera vez", cadena = "hijo"),
                    suceso("nombre", "08-10", "Ezra y ella eligen el nombre: uno de afuera", cadena = "hijo"),
                    mundo("abre", "09-20", "Se abre la Puerta para siempre: todos ven el cielo por primera vez", certeza = I, escena = "cielo"),
                    mundo("llueve", "10-15", "Llueve, y sale a mojarse", certeza = I, escena = "cielo", clima = Clima.LLUVIA),
                    fiesta("fin_anio", "12-31", "La última noche del año, con la Puerta abierta", certeza = I),
                )),
        ),
        arcos = listOf(
            Arco("reserva", TipoArco.OTRO, listOf(p(1, "01-01", 0.8), p(2, "03-13", 0.8), p(2, "03-14", 0.2, salto = true), p(2, "04-21", 0.45), p(3, "04-21", 0.8), p(5, "12-31", 0.85))),
            Arco.cadaAnio("cultivo", TipoArco.OTRO, "01-01" to 0.6, "05-19" to 1.0, "05-20" to 0.1, "12-31" to 0.58, saltos = setOf("05-20")),
        ),
        objetos = listOf("llave", "disco", "linterna", "mono", "contador", "racion", "cuaderno", "pastilla", "manta", "foto", "tocadiscos", "flor"),
        estados = listOf(
            Estado("racionamiento", "racion", "fin_racion"), Estado("cuarentena", "fiebre", "abre_sector"),
            Estado("abierto", "abre", "fin_anio"), Estado("abierto", "fin_anio", null),
        ),
    )

    // =====================================================================
    //  El archivista de un pueblo costero. Un pueblo de pescadores en la niebla, de 1926 a 1930.
    // =====================================================================
    //
    // Su gente (toda inventada): la posadera, doña Inés, en cuya posada vive. Baltasar, el torrero del faro, viejo, que
    // enferma en la temporada 4 y muere ese otoño; un torrero nuevo llega en la 5. La niña que le lleva el café al archivo.
    // El párroco. Su antiguo profesor de la universidad, que no le contesta hasta la 5. Nada de criaturas ni nombres de
    // ninguna obra: lo raro se insinúa (un signo, un nombre que se repite, una isla que no existe) y nunca se explica.
    //
    // Claves de escena: fiesta, niebla, viento, luto, tormenta. Arcos: archivo (lo catalogado: se termina en la temporada
    // 5) y signo (cuántas veces aparece el signo en sus cuadernos). Estados: «fiebre» (temporada 3, tres semanas en cama)
    // y «faro» (temporada 4, tres semanas cuidando el faro).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el archivo con las cajas por abrir según
    // `archivo`, y la niebla y el faro de la costa; su cuarto de la posada durante «fiebre»; y el faro por dentro, con la
    // lámpara, durante «faro».

    private fun anioDelPueblo(): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo: las campanas suenan a medianoche y nadie sale a la calle", certeza = I),
        fiesta("candelaria", "02-02", "La Candelaria: una vela en cada ventana que da al mar", certeza = I),
        rito("aceite", "03-21", "El torrero baja al pueblo a comprar el aceite del faro para todo el año", certeza = I),
        rito("bendicion", "05-01", "La bendición de las barcas en el muelle", certeza = I),
        fiesta("san_pedro", "06-29", "San Pedro, el de los pescadores: procesión por el muelle", certeza = I),
        fiesta("carmen", "07-16", "La Virgen del Carmen sale en barca a dar la vuelta a la bahía", certeza = I),
        rito("marea_baja", "08-23", "La noche de la marea más baja: nadie baja a la playa", certeza = I, escena = "niebla"),
        rito("varada", "10-15", "Se acaba la temporada de la sardina: las barcas se varan en la arena", certeza = I),
        rito("difuntos", "11-02", "Día de difuntos: el pueblo entero en el cementerio de las rocas", certeza = I, escena = "luto"),
        fiesta("navidad", "12-25", "Navidad", certeza = I),
    )

    val ARCHIVISTA = Calendario(
        vida = "archivista",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 4, nubes = 12, lluvia = 8, niebla = 5, viento = 2), mes(despejado = 4, nubes = 11, lluvia = 7, niebla = 5, viento = 2),
            mes(despejado = 5, nubes = 11, lluvia = 8, niebla = 5, viento = 3), mes(despejado = 6, nubes = 10, lluvia = 7, niebla = 6, viento = 1),
            mes(despejado = 7, nubes = 9, lluvia = 5, niebla = 9), mes(despejado = 8, nubes = 8, lluvia = 4, niebla = 10),
            mes(despejado = 9, nubes = 7, lluvia = 3, niebla = 11, tormenta = 1), mes(despejado = 9, nubes = 7, lluvia = 3, niebla = 11, tormenta = 1),
            mes(despejado = 7, nubes = 9, lluvia = 6, niebla = 7, tormenta = 1), mes(despejado = 5, nubes = 10, lluvia = 9, niebla = 5, viento = 2),
            mes(despejado = 4, nubes = 11, lluvia = 9, niebla = 4, viento = 2), mes(despejado = 4, nubes = 12, lluvia = 8, niebla = 4, viento = 3),
        ),
        temporadas = listOf(
            TemporadaVida("1926", "niebla",
                "El archivista encuentra un legajo de hace dos siglos que nadie había abierto. Un temporal echa una barca contra las rocas. Ve el mismo nombre en un bautizo de hace cien años y en uno de este mes. Las redes salen llenas de un pez que nadie sabe nombrar. Ve por primera vez el signo en su cuaderno. Cinco días de niebla.",
                anioDelPueblo() + listOf(
                    suceso("legajo", "02-20", "Encuentra un legajo de hace dos siglos que nadie había abierto"),
                    mundo("barca", "03-12", "Un temporal echa una barca contra las rocas: se salvan todos", certeza = I, escena = "viento", clima = Clima.VIENTO),
                    suceso("nombre", "05-18", "Ve el mismo nombre en un bautizo de hace cien años y en uno de este mes"),
                    mundo("pez", "07-30", "Las redes salen llenas de un pez que nadie sabe nombrar", certeza = I),
                    suceso("signo", "09-14", "Ve por primera vez el signo en el margen de su cuaderno"),
                    mundo("niebla", "11-20", "Cinco días de niebla tan espesa que la sirena del faro no para", 5, certeza = I, escena = "niebla", clima = Clima.NIEBLA),
                )),
            TemporadaVida("1927", "campanas",
                "El archivista le escribe a su antiguo profesor, que no contesta. El mar devuelve una barca perdida hace diez años, vacía y seca. Las campanas suenan solas a medianoche. La posadera le dice que no pregunte más por la familia del acantilado. Encuentra un mapa con una isla que no existe. El faro se apaga una noche.",
                anioDelPueblo() + listOf(
                    suceso("profesor", "02-10", "Le escribe a su antiguo profesor de la universidad, y no contesta"),
                    mundo("barca_seca", "04-05", "El mar devuelve una barca que se perdió hace diez años, vacía y seca", certeza = I),
                    mundo("campanas", "06-06", "Las campanas de la iglesia suenan solas a medianoche", certeza = I),
                    suceso("posadera", "08-02", "La posadera le dice que no pregunte más por la familia del acantilado"),
                    suceso("mapa", "10-28", "Encuentra en una caja un mapa de la costa con una isla que no existe"),
                    mundo("faro_apagado", "12-12", "El faro se apaga una noche entera, y nadie sabe por qué", certeza = I, escena = "niebla"),
                )),
            TemporadaVida("1928", "fiebre",
                "El archivista cae enfermo con una fiebre que el médico no entiende, y pasa tres semanas en cama. Una ballena muerta llega a la playa. La niña del café le pregunta qué dibuja. Un temporal arranca medio muelle. Termina el índice de los naufragios: hay demasiados.",
                anioDelPueblo() + listOf(
                    suceso("cae", "03-01", "Cae enfermo con una fiebre que el médico no entiende"),
                    suceso("levanta", "03-25", "Se levanta de la cama, más flaco, y vuelve al archivo"),
                    mundo("ballena", "05-20", "Una ballena muerta llega a la playa", certeza = I),
                    suceso("nina", "07-07", "La niña que le lleva el café le pregunta qué dibuja"),
                    mundo("temporal", "09-30", "Un temporal de tres días arranca medio muelle", 3, certeza = I, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("indice", "11-28", "Termina el índice de los naufragios: hay demasiados"),
                )),
            TemporadaVida("1929", "faro",
                "El torrero, enfermo, le pide al archivista que lo ayude con el faro, y pasa tres semanas allí. Llega un forastero que pregunta por el archivo. Hay páginas de su diario que no recuerda haber escrito. Una mañana no hay ni un pájaro en el pueblo. Muere el torrero.",
                anioDelPueblo() + listOf(
                    suceso("sube_faro", "01-20", "El torrero, enfermo, le pide que lo ayude a cuidar el faro"),
                    suceso("baja_faro", "02-10", "El torrero se levanta, y él baja del faro"),
                    mundo("forastero", "05-05", "Llega un forastero que pregunta por el archivo, y se va al día siguiente", certeza = I),
                    suceso("paginas", "06-14", "Hay páginas de su diario que no recuerda haber escrito"),
                    mundo("pajaros", "08-08", "Una mañana no hay ni un pájaro en todo el pueblo", certeza = I),
                    suceso("torrero", "10-22", "Muere Baltasar, el torrero", escena = "luto"),
                )),
            TemporadaVida("1930", "marea",
                "Llega un torrero nuevo que no habla. El profesor contesta, por fin: una sola línea. El mar amanece rojo. El archivista abre la última caja, termina el catálogo y decide quedarse en el pueblo.",
                anioDelPueblo() + listOf(
                    mundo("torrero_nuevo", "03-05", "Llega un torrero nuevo, joven, que no habla", certeza = I),
                    suceso("contesta", "04-15", "Por fin le contesta el profesor: una sola línea"),
                    mundo("mar_rojo", "06-20", "El mar amanece rojo de punta a punta de la bahía", certeza = I),
                    suceso("ultima_caja", "09-10", "Abre la última caja del archivo", cadena = "final"),
                    suceso("catalogo", "10-20", "Termina el catálogo del archivo", cadena = "final"),
                    suceso("quedarse", "11-10", "Decide quedarse en el pueblo", cadena = "final"),
                )),
        ),
        arcos = listOf(
            Arco("archivo", TipoArco.OBRA, listOf(p(1, "01-01", 0.05), p(3, "03-01", 0.45), p(3, "03-25", 0.45), p(5, "10-20", 1.0), p(5, "12-31", 1.0))),
            Arco("signo", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(1, "09-13", 0.0), p(1, "09-14", 0.05), p(5, "12-31", 0.9))),
        ),
        objetos = listOf("legajo", "pluma", "lampara", "lupa", "diario", "llave", "cafe", "abrigo", "reloj", "carta", "mapa", "vela"),
        estados = listOf(Estado("fiebre", "cae", "levanta"), Estado("faro", "sube_faro", "baja_faro")),
    )
}
