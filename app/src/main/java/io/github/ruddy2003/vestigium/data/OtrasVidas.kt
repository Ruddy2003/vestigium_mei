package io.github.ruddy2003.vestigium.data

/**
 * Otras vidas: personas promedio de otras épocas y lugares, con lo que comían, cuánto se movían, cuánto dormían, a cuánta gente veían…
 * Cada número dice qué tan seguro es: medido (estudios y censos), estimado (registros de la época), reconstruido (esqueletos,
 * restos y comparaciones) o inventado (los mundos de ficción de algunos temas: estimaciones razonables, no datos).
 *
 * Doce vidas reales para todos, y una o dos más por tema que solo aparecen dentro de ese tema.
 */
object OtrasVidas {
    enum class Certeza(val nombre: String, val explica: String) {
        MEDIDO("medido", "Medido en estudios, encuestas o censos."),
        ESTIMADO("estimado", "Calculado con registros de la época (raciones, salarios, cuentas, leyes)."),
        RECONSTRUIDO("reconstruido", "Inferido de esqueletos, restos o pueblos parecidos de hoy: con bastante incertidumbre."),
        INVENTADO("inventado", "Viene del mundo de ficción del tema: es una estimación razonable, no un dato."),
    }

    class Dato(val v: Double, val c: Certeza, val nota: String = "")

    enum class Hacer(val nombre: String) { DORMIR("dormir"), TRABAJO("trabajo"), COMER("comer"), CAMINO("en camino"), LIBRE("tiempo propio"), GENTE("con otros"), CUIDAR("casa y cuidados"), REZAR("rezo y ritual") }

    /** Un tramo del día, de [desde] a [hasta] en horas (si [hasta] es menor, cruza la medianoche). */
    class Tramo(val desde: Double, val hasta: Double, val que: Hacer, val texto: String = "")

    /** Lo que se compara. [tuyo]: con qué número tuyo (de TusNumeros) se compara, si hay. [forma]: cómo se escribe. */
    class Medida(val id: String, val nombre: String, val unidad: String, val tuyo: String?, val forma: String = "entero", val explica: String = "")

    class Vida(
        val id: String, val nombre: String, val rol: String, val lugar: String, val cuando: String, val anio: Int,
        val dibujo: String, val tema: String? = null, val edad: Int, val historia: List<String>, val dia: List<Tramo>,
        val datos: Map<String, Dato>, val fuentes: List<Pair<String, String>>, val ficticia: Boolean = false, val inspiracion: String = "",
    )

    val MEDIDAS = listOf(
        Medida("pasos", "Pasos al día", "pasos", "pasos", explica = "A pie, en un día normal."),
        Medida("km_anio", "Kilómetros al año", "km", "km_anio", explica = "Todo lo que recorre en un año: a pie, a caballo, en barco o en vehículo."),
        Medida("kcal", "Calorías al día", "kcal", "kcal"),
        Medida("proteina", "Proteína al día", "g", "proteina"),
        Medida("azucar", "Azúcar libre al día", "g", "azucar", explica = "Azúcar añadida, miel y jarabes. La OMS recomienda menos de 50 g."),
        Medida("fibra", "Fibra al día", "g", "fibra", explica = "La OMS recomienda 25 g o más."),
        Medida("alcohol", "Alcohol al día", "g", "alcohol", "decimal", "Gramos de alcohol puro: 10 g son una bebida estándar."),
        Medida("sueno", "Horas de sueño", "h", "sueno", "decimal"),
        Medida("dormir", "Hora de dormir", "", "dormir", "hora"),
        Medida("trabajo", "Trabajo a la semana", "h", null),
        Medida("aire_libre", "Horas al aire libre", "h al día", "aire_libre", "decimal", "Tu dato: minutos con luz de exterior que mide el teléfono."),
        Medida("pantalla", "Horas frente a una pantalla", "h al día", "pantalla", "decimal"),
        Medida("notificaciones", "Interrupciones al día", "", "notificaciones",
            explica = "Cuántas veces algo le interrumpe para avisarle de algo: un mensajero, una campana, un grito desde la calle. Tu dato: las notificaciones que recibe tu teléfono al día."),
        Medida("musica", "Música al día", "h", "musica", "decimal", "Antes de la grabación, solo en vivo: cantos, fiestas, iglesia."),
        Medida("personas_dia", "Personas con las que trata en un día", "", "personas_dia", explica = "Tu dato: lo más alto entre la gente que nombras en el diario, tus llamadas y tus chats del día."),
        Medida("conocidas", "Personas que conoce", "", "conocidas", explica = "En toda su vida. Tu dato: personas distintas que has nombrado con @ en tu diario."),
        Medida("poblacion", "Gente en su pueblo o ciudad", "", "poblacion"),
        Medida("lejos", "Lo más lejos de casa", "km", "lejos"),
        Medida("estatura", "Estatura", "cm", null),
        Medida("vida", "Esperanza de vida al nacer", "años", null, explica = "Baja sobre todo porque muchos bebés y niños morían; quien llegaba a adulto solía vivir bastante más."),
        Medida("vida_adulto", "Si llegaba a adulto, vivía hasta", "años", null),
        Medida("hijos", "Hijos por mujer", "", null, "decimal"),
        Medida("alfabetizacion", "Gente que sabe leer", "%", null),
        Medida("lectura", "Libros al año", "", "lectura"),
    )

    /** Las que sirven para decir a quién te pareces (comportamiento diario, no circunstancias). */
    val PARECIDO = listOf("pasos", "kcal", "azucar", "fibra", "alcohol", "sueno", "dormir", "aire_libre", "pantalla", "notificaciones", "musica", "personas_dia", "km_anio")

    private fun m(v: Number, nota: String = "") = Dato(v.toDouble(), Certeza.MEDIDO, nota)
    private fun e(v: Number, nota: String = "") = Dato(v.toDouble(), Certeza.ESTIMADO, nota)
    private fun r(v: Number, nota: String = "") = Dato(v.toDouble(), Certeza.RECONSTRUIDO, nota)
    private fun i(v: Number, nota: String = "") = Dato(v.toDouble(), Certeza.INVENTADO, nota)
    private fun t(desde: Number, hasta: Number, que: Hacer, texto: String = "") = Tramo(desde.toDouble(), hasta.toDouble(), que, texto)
    private val W = "https://en.wikipedia.org/wiki/"

    // =====================================================================
    //  Las doce vidas de todos
    // =====================================================================

    val BASE: List<Vida> = listOf(
        Vida("paleolitico", "Naa", "Cazadora y recolectora", "Sabana de África oriental", "hace unos 15,000 años", -13000, "mano", edad = 28,
            historia = listOf(
                "Naa vive en un campamento de unas treinta personas que se muda cada pocas semanas. Sale temprano con otras mujeres a sacar tubérculos con un palo de cavar, juntar bayas y frutos de baobab; los hombres caminan el doble de lejos, siguiendo animales o colmenas.",
                "No hay horario: se trabaja unas horas, se descansa a la sombra, se reparte lo que se trae. La miel silvestre es su único dulce y, en temporada, casi una fiesta. Nadie bebe alcohol. Duerme poco más de seis horas, y despierta con el frío del amanecer.",
                "Conocerá en toda su vida a unos cientos de personas. La mitad de los niños no llega a los quince años; pero si ella sobrevive a la infancia, puede pasar de los sesenta. Lo que sabemos de su día sale de pueblos que todavía viven así, como los hadza de Tanzania.",
            ),
            dia = listOf(t(21.8, 6.3, Hacer.DORMIR), t(6.3, 7.5, Hacer.CUIDAR, "fuego y niños"), t(7.5, 11.5, Hacer.TRABAJO, "recolectar"), t(11.5, 15.5, Hacer.LIBRE, "sombra y charla"),
                t(15.5, 17.5, Hacer.TRABAJO, "procesar comida"), t(17.5, 19.0, Hacer.COMER), t(19.0, 21.8, Hacer.GENTE, "fuego, cantos, historias")),
            datos = mapOf(
                "pasos" to r(9000, "Las mujeres hadza caminan unos 6 km al día; los hombres, más de 11."), "km_anio" to m(2100, "Unos 5.8 km al día, medidos con GPS en mujeres hadza."),
                "kcal" to m(1900, "Gasto medido con agua doblemente marcada en mujeres hadza: unas 1,880 kcal."), "proteina" to e(70), "azucar" to e(40, "Miel silvestre: entre los hadza es casi el 15% de las calorías del año, sobre todo de los hombres."),
                "fibra" to e(90, "Tubérculos, baobab y frutos: estimaciones de 80 a 150 g al día."), "alcohol" to r(0), "sueno" to m(6.4, "Entre 5.7 y 7.1 h en hadza, san y tsimane."),
                "dormir" to m(21.8, "Unas 3 h 20 min después de la puesta de sol."), "trabajo" to e(35, "Buscar, cargar y preparar comida."), "aire_libre" to e(11),
                "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(1, "Cantos y danzas al anochecer."), "personas_dia" to e(25), "conocidas" to e(500),
                "poblacion" to e(30, "La banda; el pueblo entero, unos cientos dispersos."), "lejos" to e(100), "estatura" to r(158), "vida" to m(31, "Promedio de pueblos cazadores-recolectores estudiados."),
                "vida_adulto" to m(54, "Quien llega a los 15 vive en promedio unos 39 años más; la edad más común de muerte en adultos es cerca de los 70."),
                "hijos" to m(5.5, "6.2 entre los hadza; 4.7 entre los !kung."), "alfabetizacion" to r(0), "lectura" to r(0),
            ),
            fuentes = listOf("Pontzer y otros (2012), energética de los hadza" to "https://pmc.ncbi.nlm.nih.gov/articles/PMC3405064",
                "Yetish y otros (2015), el sueño en sociedades preindustriales" to "https://www.uclahealth.org/news/release/our-ancestors-probably-didnt-get-8-hours-a-night-either",
                "Gurven y Kaplan (2007), longevidad en cazadores-recolectores" to "https://gurven.anth.ucsb.edu/sites/secure.lsit.ucsb.edu.anth.d7_gurven/files/sitefiles/papers/GurvenKaplan2007pdr.pdf",
                "Marlowe y otros (2014), la miel y los hadza" to "https://onwork.edu.au/bibitem/2014-Marlowe,Frank+W-Berbesque,J+Colette-etal-Honey,Hadza,hunter-gatherers,and+human+evolution/")),

        Vida("neolitico", "Ata", "Agricultor", "Çatalhöyük, Anatolia", "hacia el 7000 a. C.", -7000, "espiga", edad = 30,
            historia = listOf(
                "Ata vive en uno de los primeros pueblos grandes del mundo: miles de casas de adobe pegadas unas a otras, sin calles. Se entra por el techo, con una escalera. Adentro hay un horno, plataformas para dormir y, bajo el piso, los huesos de los abuelos.",
                "Cultiva trigo y cebada, lentejas, cría ovejas y cabras. Con los siglos los campos quedaron cada vez más lejos y caminar hasta ellos se volvió parte del trabajo. Comer granos le dio más hijos que a sus antepasados, pero también caries, más infecciones y una estatura menor.",
                "Casi todo lo que sabemos sale de sus esqueletos: músculos marcados por cargar y moler, dientes gastados por la piedra del molino, huesos que muestran hambre en la infancia.",
            ),
            dia = listOf(t(21.0, 5.5, Hacer.DORMIR), t(5.5, 6.5, Hacer.CUIDAR, "horno y animales"), t(6.5, 8.0, Hacer.CAMINO, "a los campos"), t(8.0, 13.0, Hacer.TRABAJO, "campo"), t(13.0, 14.0, Hacer.COMER),
                t(14.0, 17.0, Hacer.TRABAJO, "moler, tejer, construir"), t(17.0, 18.5, Hacer.CAMINO), t(18.5, 21.0, Hacer.GENTE, "en el techo con los vecinos")),
            datos = mapOf(
                "pasos" to r(13000), "km_anio" to e(3500), "kcal" to e(2600), "proteina" to e(70), "azucar" to e(5), "fibra" to e(60), "alcohol" to r(0),
                "sueno" to e(7.5), "dormir" to e(21.0), "trabajo" to r(50, "Los esqueletos muestran más carga de trabajo que en los cazadores."), "aire_libre" to e(8),
                "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(0.5), "personas_dia" to e(60), "conocidas" to e(1500), "poblacion" to e(5000, "Entre 3,500 y 8,000 personas en su mejor época."),
                "lejos" to e(60), "estatura" to r(166), "vida" to r(28), "vida_adulto" to r(42), "hijos" to r(7, "Los primeros agricultores tenían más hijos que los cazadores."),
                "alfabetizacion" to r(0), "lectura" to r(0),
            ),
            fuentes = listOf("Larsen y otros (2019), bioarqueología de Çatalhöyük" to "https://pmc.ncbi.nlm.nih.gov/articles/PMC6601270", "Çatalhöyük" to "${W}%C3%87atalh%C3%B6y%C3%BCk")),

        Vida("egipto", "Ipy", "Campesino", "Tebas, Egipto", "hacia el 1250 a. C.", -1250, "piramide", edad = 26,
            historia = listOf(
                "La vida de Ipy la marca el Nilo: cuatro meses de inundación, cuatro de siembra, cuatro de cosecha. Cuando el río cubre los campos, el faraón llama a los campesinos a trabajar en obras de piedra a cambio de raciones.",
                "Su pago es grano: trigo emmer para el pan y cebada para la cerveza, espesa y poco alcohólica, que se toma todos los días como comida. Come cebolla, lentejas, pescado del río, dátiles; carne, solo en fiestas.",
                "Casi nadie a su alrededor sabe leer: los escribas son quizá uno de cada cien. Muchos de sus hermanos murieron de niños. Si llega a viejo, tendrá la espalda y las rodillas gastadas por la azada.",
            ),
            dia = listOf(t(21.0, 5.0, Hacer.DORMIR), t(5.0, 6.0, Hacer.COMER, "pan y cerveza"), t(6.0, 11.0, Hacer.TRABAJO, "campo o canal"), t(11.0, 14.0, Hacer.LIBRE, "el calor del mediodía"),
                t(14.0, 18.0, Hacer.TRABAJO), t(18.0, 19.0, Hacer.COMER), t(19.0, 21.0, Hacer.GENTE)),
            datos = mapOf(
                "pasos" to e(12000), "km_anio" to e(3000), "kcal" to e(2900, "Pan como base; la ración mensual de un obrero alcanzaba para su familia."), "proteina" to e(70), "azucar" to e(15, "Dátiles, miel en fiestas."),
                "fibra" to e(50), "alcohol" to e(40, "Unos dos litros de cerveza ligera al día."), "sueno" to e(7.5), "dormir" to e(21.0), "trabajo" to e(50, "En la obra real: dos turnos de 4 horas, ocho días de cada diez."),
                "aire_libre" to e(9), "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(0.5), "personas_dia" to e(40), "conocidas" to e(1000), "poblacion" to e(500),
                "lejos" to e(100), "estatura" to r(165), "vida" to e(25), "vida_adulto" to e(50), "hijos" to e(6), "alfabetizacion" to e(1, "Solo los escribas."), "lectura" to r(0),
            ),
            fuentes = listOf("Las huelgas de Deir el-Medina: raciones y turnos" to "${W}Deir_el-Medina_strikes", "Hilary Wilson: pan, cerveza y economía" to "https://the-past.com/feature/hilary-wilson-on-bread-beer-and-economics-part-2/")),

        Vida("roma", "Lucio", "Zapatero", "Roma", "hacia el 110 d. C.", 110, "anfora", edad = 32,
            historia = listOf(
                "Lucio vive en el tercer piso de una ínsula, un edificio de departamentos donde no se puede cocinar por el riesgo de incendio: come pan, gachas, legumbres y salsa de pescado en los puestos de la calle. Cada mes recoge del Estado cinco modios de trigo, unos 33 kilos.",
                "Trabaja de la mañana a la media tarde en su taller abierto a la calle. Después, las termas: por una moneda pequeña, baño caliente, frío, ejercicio y conversación. Hay más de cien días de fiesta al año, con carreras y juegos.",
                "Roma tiene cerca de un millón de habitantes: nunca se está solo. Toma vino rebajado con agua todos los días. Quizá uno de cada diez sabe leer bien. Muchos niños mueren en los veranos de fiebre.",
            ),
            dia = listOf(t(21.5, 5.5, Hacer.DORMIR), t(5.5, 6.0, Hacer.COMER), t(6.0, 13.0, Hacer.TRABAJO, "taller"), t(13.0, 14.0, Hacer.COMER), t(14.0, 16.5, Hacer.GENTE, "termas"),
                t(16.5, 19.0, Hacer.LIBRE, "foro y calles"), t(19.0, 21.5, Hacer.COMER, "cena")),
            datos = mapOf(
                "pasos" to e(12000), "km_anio" to e(3000), "kcal" to e(2800, "El trigo del Estado daba para unas 3,000 kcal al día: «suficiente para dos personas», dicen las cuentas."), "proteina" to e(90),
                "azucar" to e(20, "Miel y jarabe de uva."), "fibra" to e(40), "alcohol" to e(45, "Vino aguado, cerca de un litro al día en los hombres."), "sueno" to e(7.0), "dormir" to e(21.5),
                "trabajo" to e(40), "aire_libre" to e(6), "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(0.5), "personas_dia" to e(150), "conocidas" to e(3000),
                "poblacion" to e(1000000), "lejos" to e(30), "estatura" to r(164, "Varones de la Italia romana: unos 164 cm."), "vida" to e(25), "vida_adulto" to e(50), "hijos" to e(5),
                "alfabetizacion" to e(10), "lectura" to r(0),
            ),
            fuentes = listOf("La cura annonae: el trigo para la plebe" to "${W}Cura_annonae", "Giannecchini y Moggi-Cecchi (2008), estatura en la Italia antigua" to "https://flore.unifi.it/bitstream/2158/253691/1/Giannecchini%20MoggiCecchi%20%20AJPA%202008.pdf")),

        Vida("medieval", "Alice", "Campesina", "Una aldea de Inglaterra", "hacia 1300", 1300, "castillo", edad = 30,
            historia = listOf(
                "Alice vive en una casa de un solo cuarto con su familia y, en invierno, con una vaca. Su aldea tiene unas trescientas personas, una iglesia y un molino del señor, donde hay que pagar por moler.",
                "Come pan oscuro, potaje de chícharos y verduras, queso, a veces tocino; bebe cerveza ligera todo el día, más segura que el agua del pozo. En la cosecha trabaja de sol a sol; en invierno, la noche es larga: quizá duerme en dos tandas, con un rato despierta a la mitad.",
                "Rara vez pasará del mercado del pueblo vecino, a unos veinte kilómetros. Dentro de pocos años vendrá una gran hambruna, y en 1348 la peste matará a uno de cada tres de sus vecinos.",
            ),
            dia = listOf(t(20.5, 5.0, Hacer.DORMIR, "en dos tandas"), t(5.0, 6.0, Hacer.CUIDAR, "fuego y animales"), t(6.0, 12.0, Hacer.TRABAJO, "campo"), t(12.0, 13.0, Hacer.COMER),
                t(13.0, 17.0, Hacer.TRABAJO, "hilar, huerto"), t(17.0, 18.0, Hacer.COMER), t(18.0, 20.5, Hacer.GENTE)),
            datos = mapOf(
                "pasos" to e(13000), "km_anio" to e(2500), "kcal" to e(2800), "proteina" to e(80), "azucar" to e(5, "El azúcar era un lujo de señores; la miel, escasa."), "fibra" to e(50),
                "alcohol" to e(30, "Cerveza ligera (ale) en lugar de agua."), "sueno" to e(8.5, "Noches largas; el «primer y segundo sueño» aparece en textos de la época."), "dormir" to e(20.5),
                "trabajo" to e(45, "Mucho en la cosecha, poco en invierno; unos 40 días de fiesta religiosa."), "aire_libre" to e(8), "pantalla" to r(0), "notificaciones" to r(0),
                "musica" to e(0.5), "personas_dia" to e(30), "conocidas" to e(500), "poblacion" to e(300), "lejos" to e(25), "estatura" to r(158), "vida" to e(30), "vida_adulto" to e(55),
                "hijos" to e(4.5), "alfabetizacion" to e(5), "lectura" to r(0),
            ),
            fuentes = listOf("Dyer: la dieta de los trabajadores de la cosecha" to "https://www.medievalists.net/2010/10/16/changes-in-diet-in-the-late-middle-ages-the-case-of-harvest-workers/",
                "Cuántos días al año se trabajaba en Inglaterra" to "https://ehs.org.uk/how-many-days-a-year-did-people-work-in-england-before-the-industrial-revolution/")),

        Vida("tenochtitlan", "Citlali", "Macehual que cultiva una chinampa", "México-Tenochtitlan", "hacia 1500", 1500, "maiz", edad = 24,
            historia = listOf(
                "Citlali vive en una ciudad sobre el lago, más grande que casi cualquiera de Europa: unas doscientas mil personas, canales llenos de canoas y calzadas hacia tierra firme. Cultiva maíz, frijol, chile y flores en una chinampa, una isla de lodo fértil.",
                "Muele maíz nixtamalizado cada madrugada para las tortillas. Come frijoles, chía, amaranto, quelites, insectos, acociles y el alga del lago que llaman tecuitlatl. El pulque está reservado para los viejos y las fiestas: emborracharse es mal visto y castigado.",
                "En su ciudad todos los muchachos van a la escuela, algo raro en el mundo de entonces, y al anochecer los jóvenes aprenden cantos y danzas en la casa del canto. Al mercado de Tlatelolco llegan decenas de miles de personas.",
            ),
            dia = listOf(t(21.0, 4.5, Hacer.DORMIR), t(4.5, 6.5, Hacer.CUIDAR, "moler y tortear"), t(6.5, 12.0, Hacer.TRABAJO, "chinampa"), t(12.0, 13.0, Hacer.COMER),
                t(13.0, 16.0, Hacer.CAMINO, "mercado en canoa"), t(16.0, 18.0, Hacer.CUIDAR), t(18.0, 21.0, Hacer.GENTE, "cantos y familia")),
            datos = mapOf(
                "pasos" to e(10000, "Mucho se movía en canoa."), "km_anio" to e(2500), "kcal" to e(2200, "El maíz daba quizá tres cuartas partes de la energía."), "proteina" to e(65),
                "azucar" to e(10, "Aguamiel y miel de maguey."), "fibra" to e(60), "alcohol" to e(5, "Pulque solo para mayores y fiestas."), "sueno" to e(7.5), "dormir" to e(21.0),
                "trabajo" to e(45, "Más el trabajo por turnos para obras públicas."), "aire_libre" to e(8), "pantalla" to r(0), "notificaciones" to r(0),
                "musica" to e(1.5, "Cantos y danzas casi todas las noches para los jóvenes."), "personas_dia" to e(100), "conocidas" to e(2000), "poblacion" to e(200000, "Entre 150,000 y 250,000."),
                "lejos" to e(40), "estatura" to r(150), "vida" to r(30), "vida_adulto" to r(55), "hijos" to e(5), "alfabetizacion" to e(5, "La escritura de imágenes era de los tlacuilos."), "lectura" to r(0),
            ),
            fuentes = listOf("Tenochtitlan" to "${W}Tenochtitlan", "La cocina mexica" to "${W}Aztec_cuisine")),

        Vida("edo", "Kichizō", "Carpintero", "Edo (hoy Tokio)", "hacia 1750", 1750, "abanico", edad = 30,
            historia = listOf(
                "Kichizō vive en una casa de vecindad de madera, en un callejón, con baño público a la vuelta: en Edo casi todos van al sentō cada día. Su ciudad, con más de un millón de personas, es quizá la más grande del mundo.",
                "Come mucho arroz blanco pulido, unos cinco gō al día, con sopa de miso, verduras en salmuera, tofu y, a veces, pescado. Tanto arroz blanco y tan pocas otras cosas causan la «enfermedad de Edo», que hoy sabemos que es falta de vitamina B1.",
                "Sabe leer los silabarios, como la mayoría de los hombres de la ciudad, y alquila libros ilustrados a un vendedor que pasa por su casa. Ha caminado, como tantos, en peregrinación a algún santuario lejano: el viaje de su vida.",
            ),
            dia = listOf(t(21.5, 5.5, Hacer.DORMIR), t(5.5, 6.5, Hacer.COMER), t(6.5, 8.0, Hacer.CAMINO, "a la obra"), t(8.0, 12.0, Hacer.TRABAJO), t(12.0, 13.0, Hacer.COMER),
                t(13.0, 16.0, Hacer.TRABAJO), t(16.0, 17.5, Hacer.GENTE, "baño público"), t(17.5, 21.5, Hacer.LIBRE, "sake, libros, vecinos")),
            datos = mapOf(
                "pasos" to e(15000, "Todo se hacía a pie."), "km_anio" to e(4000), "kcal" to e(2700, "Cinco gō de arroz son unos 750 g: casi toda su energía."), "proteina" to e(60),
                "azucar" to e(5), "fibra" to e(15, "Arroz pulido: muy poca fibra."), "alcohol" to e(20, "Un gō de sake."), "sueno" to e(7.0), "dormir" to e(21.5, "El aceite de las lámparas era caro."),
                "trabajo" to e(45), "aire_libre" to e(7), "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(0.5), "personas_dia" to e(150), "conocidas" to e(3000),
                "poblacion" to e(1100000), "lejos" to e(60), "estatura" to r(156, "La estatura más baja de la historia de Japón."), "vida" to e(35), "vida_adulto" to e(60), "hijos" to e(3.5),
                "alfabetizacion" to e(70, "Hombres de la ciudad; en el campo, mucho menos."), "lectura" to e(6, "Cientos de librerías de alquiler en la ciudad."),
            ),
            fuentes = listOf("El arroz en Edo y el beriberi" to "https://assets.atlasobscura.com/articles/rice-disease-mystery-edo-tokyo-navy-beriberi",
                "La estatura de los japoneses a lo largo del tiempo" to "https://web.kahaku.go.jp/research/publication/anthropology/download/33/BNMNS_D3303.pdf",
                "La comida en el periodo Edo (Kikkoman)" to "https://www.kikkoman.com/jp/kiifc/english/journal/pdf/no14_e_8.pdf")),

        Vida("manchester", "Mary", "Hiladora en una fábrica de algodón", "Manchester, Inglaterra", "1840", 1840, "fabrica", edad = 19,
            historia = listOf(
                "La campana de la fábrica suena antes de las seis. Mary trabaja doce horas entre máquinas de hilar, de pie, con el ruido y la pelusa del algodón en el aire; el sábado, nueve. Su ciudad es la primera ciudad industrial del mundo, con el cielo negro de humo.",
                "Come pan, papas, té con azúcar y, si alcanza, un poco de tocino. El té dulce le da energía barata para el turno. Vive con sus padres y hermanos en un cuarto sin agua corriente; el baño se comparte con decenas de vecinos.",
                "Un informe de 1842 calculó que los obreros de Manchester morían, en promedio, a los 17 años, contando a los muchos niños que no pasaban de los cinco. Sabe firmar su nombre, como la mitad de las mujeres de su edad.",
            ),
            dia = listOf(t(22.0, 5.0, Hacer.DORMIR), t(5.0, 5.75, Hacer.CAMINO), t(5.75, 8.0, Hacer.TRABAJO, "telares"), t(8.0, 8.5, Hacer.COMER), t(8.5, 12.5, Hacer.TRABAJO), t(12.5, 13.5, Hacer.COMER),
                t(13.5, 19.0, Hacer.TRABAJO), t(19.0, 22.0, Hacer.CUIDAR, "casa y hermanos")),
            datos = mapOf(
                "pasos" to e(12000, "De pie todo el turno, atendiendo las máquinas."), "km_anio" to e(3000), "kcal" to e(2300), "proteina" to e(55), "azucar" to e(22, "En 1840 cada británico comía unos 8 kg de azúcar al año."),
                "fibra" to e(25), "alcohol" to e(20), "sueno" to e(6.5), "dormir" to e(22.0), "trabajo" to e(69, "Doce horas de lunes a viernes y nueve el sábado."), "aire_libre" to e(1),
                "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(0.3), "personas_dia" to e(200), "conocidas" to e(2000), "poblacion" to m(240000, "Censo de 1841."), "lejos" to e(60),
                "estatura" to r(155), "vida" to e(26, "Contando la altísima mortalidad infantil."), "vida_adulto" to e(55), "hijos" to e(5), "alfabetizacion" to e(50), "lectura" to e(1),
            ),
            fuentes = listOf("Las leyes de fábricas (1833)" to "${W}Factory_Acts", "Informe Chadwick sobre las condiciones de los trabajadores (1842)" to "${W}Report_on_the_Sanitary_Condition_of_the_Labouring_Population_of_Great_Britain")),

        Vida("hacienda", "José", "Peón acasillado", "Una hacienda pulquera de Hidalgo", "1905", 1905, "maguey", edad = 28,
            historia = listOf(
                "José vive dentro de la hacienda, en un cuarto de adobe que le presta el patrón. Raspa magueyes para sacar aguamiel de sol a sol, seis días a la semana; el domingo, misa y mercado. Le pagan unos centavos al día, buena parte en vales para la tienda de raya.",
                "Come tortillas, frijoles, chile y nopales, y bebe pulque, la bebida de la hacienda, con la comida y después del trabajo. Carne, casi nunca. Las deudas de la tienda pasan de padres a hijos: muchos no pueden irse aunque quieran.",
                "Siete de cada diez mexicanos no saben leer. Le quedan unos años para que estalle la Revolución.",
            ),
            dia = listOf(t(20.5, 4.5, Hacer.DORMIR), t(4.5, 5.0, Hacer.REZAR, "el alabado"), t(5.0, 12.0, Hacer.TRABAJO, "raspar magueyes"), t(12.0, 13.0, Hacer.COMER), t(13.0, 18.0, Hacer.TRABAJO),
                t(18.0, 19.0, Hacer.COMER), t(19.0, 20.5, Hacer.GENTE)),
            datos = mapOf(
                "pasos" to e(15000), "km_anio" to e(4000), "kcal" to e(2500), "proteina" to e(70), "azucar" to e(15, "Piloncillo."), "fibra" to e(55), "alcohol" to e(45, "Litro y medio de pulque al día no era raro."),
                "sueno" to e(7.0), "dormir" to e(20.5), "trabajo" to e(66, "De sol a sol, seis días."), "aire_libre" to e(10), "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(0.3),
                "personas_dia" to e(40), "conocidas" to e(600), "poblacion" to e(500), "lejos" to e(50), "estatura" to r(160), "vida" to e(30), "vida_adulto" to e(52), "hijos" to e(6),
                "alfabetizacion" to m(27, "Censo de 1910: más del 70% no sabía leer."), "lectura" to r(0),
            ),
            fuentes = listOf("La Revolución mexicana en números (SEP)" to "https://nuevaescuelamexicana.sep.gob.mx/contenido/coleccion/la-revolucion-mexicana-en-numeros/", "Tienda de raya" to "https://es.wikipedia.org/wiki/Tienda_de_raya")),

        Vida("cdmx1960", "Rosa", "Secretaria", "Ciudad de México", "1960", 1960, "television", edad = 26,
            historia = listOf(
                "Rosa toma el tranvía para llegar a una oficina del centro, donde escribe a máquina ocho horas. La ciudad tiene unos cinco millones de habitantes y crece todos los días con gente que llega de los pueblos; la Torre Latinoamericana es el edificio más alto.",
                "Come en casa de su mamá: sopa, guisado, frijoles y tortillas. El refresco y el pan dulce ya son de todos los días. En la noche escucha radionovelas; en su vecindad hay una sola televisión y los niños se juntan a verla.",
                "Las mujeres de su generación tendrán, en promedio, casi siete hijos; sus hijas, tres; sus nietas, menos de dos. Ella es de las pocas de su familia que terminó la secundaria. Lee historietas y fotonovelas cada semana.",
            ),
            dia = listOf(t(22.5, 6.0, Hacer.DORMIR), t(6.0, 7.0, Hacer.CUIDAR), t(7.0, 8.0, Hacer.CAMINO, "tranvía"), t(8.0, 14.0, Hacer.TRABAJO, "oficina"), t(14.0, 16.0, Hacer.COMER, "comida en casa"),
                t(16.0, 18.0, Hacer.TRABAJO), t(18.0, 19.0, Hacer.CAMINO), t(19.0, 22.5, Hacer.LIBRE, "radio, novio, familia")),
            datos = mapOf(
                "pasos" to e(9000), "km_anio" to e(6000), "kcal" to e(2400), "proteina" to e(65), "azucar" to e(70, "En los 60, el consumo de azúcar en México ya rondaba los 30 kg por persona al año."),
                "fibra" to e(35), "alcohol" to e(5), "sueno" to e(7.5), "dormir" to e(22.5), "trabajo" to e(45), "aire_libre" to e(2), "pantalla" to e(0.6, "Algo de tele y el cine los domingos."),
                "notificaciones" to r(0), "musica" to e(2, "La radio sonaba todo el día."), "personas_dia" to e(100), "conocidas" to e(2500), "poblacion" to m(5000000),
                "lejos" to e(400), "estatura" to r(155), "vida" to m(58), "vida_adulto" to e(68), "hijos" to m(6.8), "alfabetizacion" to e(85, "En la capital; en todo el país, cerca de dos tercios."),
                "lectura" to e(2, "Más historietas que libros."),
            ),
            fuentes = listOf("IMCO: 100 años de datos de México" to "https://imco.org.mx/wp-content/uploads/2026/03/Presentacion_100-anos-de-datos.pdf", "Ciudad de México en el siglo XX" to "${W}Mexico_City")),

        Vida("mundo2025", "Alguien del mundo", "Una persona promedio del mundo", "Una ciudad de Asia, probablemente", "2025", 2025, "globo", edad = 31,
            historia = listOf(
                "La persona promedio del mundo tiene unos treinta años, vive en una ciudad y lleva un teléfono en el bolsillo. Pasa casi siete horas al día conectada a internet y escucha unas tres horas de música.",
                "Da menos de cinco mil pasos al día: menos que cualquier otra persona de esta lista. Hay más comida que nunca: en el mundo se producen casi tres mil kilocalorías por persona, y también más azúcar.",
                "Vivirá en promedio 73 años, más del doble que casi todos aquí. Tendrá dos hijos. Reconoce unas cinco mil caras, la mayoría de gente que nunca le hablará.",
            ),
            dia = listOf(t(23.5, 6.5, Hacer.DORMIR), t(6.5, 8.0, Hacer.CUIDAR), t(8.0, 9.0, Hacer.CAMINO), t(9.0, 13.0, Hacer.TRABAJO), t(13.0, 14.0, Hacer.COMER), t(14.0, 18.0, Hacer.TRABAJO),
                t(18.0, 19.0, Hacer.CAMINO), t(19.0, 20.0, Hacer.COMER), t(20.0, 23.5, Hacer.LIBRE, "pantallas")),
            datos = mapOf(
                "pasos" to m(4961, "Promedio de 717,000 personas medido con sus teléfonos en 111 países."), "km_anio" to e(7000), "kcal" to e(2400, "Se producen unas 2,950 por persona; se come menos."),
                "proteina" to e(80), "azucar" to e(55), "fibra" to e(20), "alcohol" to m(12, "5.5 litros de alcohol puro al año por adulto; la mitad no bebe."), "sueno" to e(7.0), "dormir" to e(23.5),
                "trabajo" to e(43), "aire_libre" to e(2), "pantalla" to m(6.6, "Tiempo diario en internet de quien lo usa."), "notificaciones" to e(60), "musica" to m(3.0, "20.7 horas por semana."),
                "personas_dia" to e(30), "conocidas" to m(5000, "Caras que una persona reconoce."), "poblacion" to e(500000), "lejos" to e(1000), "estatura" to m(165),
                "vida" to m(73.3), "vida_adulto" to e(77), "hijos" to m(2.25), "alfabetizacion" to m(87), "lectura" to e(4),
            ),
            fuentes = listOf("Naciones Unidas, World Population Prospects 2024" to "https://population.un.org/WPP/Publications/Files/WPP2024_Key-Messages.pdf",
                "Althoff y otros (2017): pasos en 111 países" to "https://www.livescience.com/59782-worldwide-step-count.html",
                "IFPI (2023): cuánta música escuchamos" to "https://www.ifpi.org/ifpis-global-study-finds-were-listening-to-more-music-in-more-ways-than-ever",
                "DataReportal (2025): el mundo conectado" to "https://datareportal.com/reports/digital-2025-sub-section-ever-more-connected")),

        Vida("mexico2025", "Alguien de México", "Una persona promedio de México", "Una ciudad de México", "2025", 2025, "telefono", edad = 30,
            historia = listOf(
                "La persona promedio de México tiene unos treinta años y vive en una ciudad. Ocho de cada diez usan internet, y lo hacen muchas horas: México está entre los países que más tiempo pasan en el teléfono y en redes.",
                "Trabaja más horas al año que casi cualquier otro país de la OCDE y da menos de cinco mil pasos al día. Toma más refresco que casi cualquier país del mundo; el azúcar añadida es más de una décima parte de lo que come.",
                "Vivirá en promedio 75 años y medio: las mujeres, seis más que los hombres. Tendrá menos de dos hijos. Casi todos saben leer, aunque se leen pocos libros al año.",
            ),
            dia = listOf(t(23.5, 6.0, Hacer.DORMIR), t(6.0, 7.0, Hacer.CUIDAR), t(7.0, 8.5, Hacer.CAMINO, "traslado"), t(8.5, 14.0, Hacer.TRABAJO), t(14.0, 15.0, Hacer.COMER), t(15.0, 18.5, Hacer.TRABAJO),
                t(18.5, 20.0, Hacer.CAMINO), t(20.0, 23.5, Hacer.LIBRE, "pantallas y familia")),
            datos = mapOf(
                "pasos" to m(4700), "km_anio" to e(9000), "kcal" to e(2500), "proteina" to e(80), "azucar" to e(70, "El azúcar añadida es cerca del 12% de la energía de los adultos."), "fibra" to e(24),
                "alcohol" to e(12), "sueno" to e(7.0), "dormir" to e(23.5), "trabajo" to e(44, "Unas 2,200 horas al año."), "aire_libre" to e(2), "pantalla" to e(7.5), "notificaciones" to e(70),
                "musica" to e(3.0), "personas_dia" to e(30), "conocidas" to e(5000), "poblacion" to e(1000000), "lejos" to e(800), "estatura" to m(164, "170 cm hombres, 157 mujeres."),
                "vida" to m(75.5, "72.6 hombres, 79 mujeres."), "vida_adulto" to e(78), "hijos" to e(1.6), "alfabetizacion" to m(95), "lectura" to e(3.4),
            ),
            fuentes = listOf("Esperanza de vida en México (INEGI)" to "https://www.nmas.com.mx/sociedad/cual-es-esperanza-vida-mexico-2025-estos-anos-viven-mexicanos-promedio/",
                "DataReportal (2025): México" to "https://datareportal.com/reports/digital-2025-mexico",
                "Pasos de los mexicanos (Althoff 2017)" to "https://www.eluniversal.com.mx/articulo/english/2017/07/20/mexico-among-most-active-countries-world/")),
    )

    // =====================================================================
    //  Una vida por tema (solo se ve dentro de ese tema)
    // =====================================================================

    val DE_TEMAS: List<Vida> = listOf(
        Vida("legionario", "Cayo", "Legionario", "Un campamento en el Danubio", "hacia el 110 d. C.", 110, "tema", "romano", edad = 25,
            historia = listOf(
                "Cayo se alistó a los dieciocho por veinticinco años. Vive en un barracón con los siete compañeros de su tienda, el contubernio: comen juntos lo que muelen y hornean ellos mismos con su ración de grano.",
                "Tres veces al mes marcha veinte millas romanas, unos treinta kilómetros, con su equipo a cuestas, en cinco horas. Construye caminos, puentes y murallas tanto como pelea. Bebe posca, vino agrio con agua.",
                "No puede casarse mientras sirva, aunque muchos tienen familia en el pueblo junto al campamento. Si llega al retiro, recibirá tierras o dinero. Ha cruzado medio imperio desde Hispania.",
            ),
            dia = listOf(t(21.5, 4.5, Hacer.DORMIR, "con guardias de noche"), t(4.5, 5.5, Hacer.COMER), t(5.5, 12.0, Hacer.TRABAJO, "marcha o instrucción"), t(12.0, 13.0, Hacer.COMER),
                t(13.0, 18.0, Hacer.TRABAJO, "obras y guardia"), t(18.0, 19.5, Hacer.COMER, "con el contubernio"), t(19.5, 21.5, Hacer.GENTE)),
            datos = mapOf("pasos" to e(18000, "En días de marcha, más de 35,000."), "km_anio" to e(6000), "kcal" to e(3300, "Cerca de un kilo de grano al día, más tocino y queso."), "proteina" to e(110),
                "azucar" to e(5), "fibra" to e(45), "alcohol" to e(20), "sueno" to e(6.5), "dormir" to e(21.5), "trabajo" to e(60), "aire_libre" to e(10), "pantalla" to r(0), "notificaciones" to e(20, "Toques de corneta."),
                "musica" to e(0.2), "personas_dia" to e(80, "Una centuria."), "conocidas" to e(5000, "Una legión."), "poblacion" to e(5500), "lejos" to e(2000), "estatura" to r(168),
                "vida" to e(25), "vida_adulto" to e(50), "hijos" to e(1), "alfabetizacion" to e(30), "lectura" to r(0)),
            fuentes = listOf("La legión romana" to "${W}Roman_legion", "El ejército romano: la vida diaria" to "${W}Roman_army")),

        // La vida moderna más parecida a la tuya, pero en otra ciudad: misma época, mismas pantallas, otro ritmo de calle.
        Vida("restauradora", "Giulia", "Restauradora de frescos", "Roma", "2025", 2025, "tema", "urbe", edad = 38,
            historia = listOf(
                "Giulia sale en motoneta antes de las ocho y cruza el tráfico del centro hasta una iglesia donde trabaja sobre un andamio, a diez metros del suelo, limpiando un fresco centímetro a centímetro con hisopos y una lámpara. Un metro cuadrado puede llevarle semanas.",
                "A la una baja a comer algo rápido en un bar; en la tarde vuelve al andamio hasta las seis. Después camina por su barrio, toma un café de pie en la barra y pasa por el mercado. Cena tarde, como casi todos en Roma, y a veces se queda en la plaza con amigos.",
                "Su ciudad tiene unos dos millones setecientos cincuenta mil habitantes y lleva casi tres mil años de capas encima. Ella vivirá, en promedio, más de ochenta y cinco años y tendrá uno o ningún hijo: en Italia nacen menos niños que nunca. Lleva el teléfono en el bolsillo del overol, como tú.",
            ),
            dia = listOf(t(23.5, 7.0, Hacer.DORMIR), t(7.0, 8.0, Hacer.CUIDAR), t(8.0, 8.5, Hacer.CAMINO, "en motoneta"), t(8.5, 13.0, Hacer.TRABAJO, "andamio en una iglesia"), t(13.0, 14.0, Hacer.COMER),
                t(14.0, 18.0, Hacer.TRABAJO), t(18.0, 19.5, Hacer.LIBRE, "paseo de la tarde"), t(19.5, 20.5, Hacer.CUIDAR), t(20.5, 22.0, Hacer.COMER, "cena tardía"), t(22.0, 23.5, Hacer.GENTE)),
            datos = mapOf("pasos" to e(7000, "De pie en el andamio y a pie en el barrio; la motoneta ahorra el resto."), "km_anio" to e(8000), "kcal" to e(2100), "proteina" to e(75),
                "azucar" to e(35, "El cornetto y el café con azúcar de la mañana."), "fibra" to e(22), "alcohol" to e(10, "Una copa de vino en la cena."), "sueno" to e(7.5), "dormir" to e(23.5),
                "trabajo" to e(40), "aire_libre" to e(2), "pantalla" to e(4.5, "Poca pantalla en el trabajo, mucha en la noche."), "notificaciones" to e(60), "musica" to e(1.5),
                "personas_dia" to e(25), "conocidas" to e(3000), "poblacion" to m(2745062, "Roma Capitale al 1 de enero de 2026, según el Istat."), "lejos" to e(9000),
                "estatura" to e(163, "Promedio aproximado de las italianas de su edad."), "vida" to m(85.5, "Mujeres en Italia, 2024 (Istat); 81.4 los hombres."), "vida_adulto" to e(87),
                "hijos" to m(1.18, "Italia, 2024: el mínimo de su historia (Istat)."), "alfabetizacion" to e(99), "lectura" to e(10, "En Italia, el 57% de los mayores de seis años leyó al menos un libro en 2024 (Istat).")),
            fuentes = listOf("Istat: indicadores demográficos 2024" to "https://www.istat.it/comunicato-stampa/indicatori-demografici-anno-2024/",
                "Istat: libros y bibliotecas, 2024 y 2025" to "https://www.istat.it/comunicato-stampa/libri-e-biblioteche-abitudini-di-lettura-e-frequentazione-anni-2024-e-2025/",
                "Población de Roma (datos del Istat)" to "https://www.quantitalia.it/demografia/comune/roma",
                "El fresco y su restauración" to "${W}Fresco")),

        Vida("remero", "Demeas", "Remero de un trirreme", "Atenas", "431 a. C.", -431, "tema", "heleno", edad = 30,
            historia = listOf(
                "Demeas es un ciudadano pobre de Atenas: no tiene armadura de hoplita, así que sirve en la flota. En el trirreme son ciento setenta remeros en tres filas, y un flautista marca el ritmo para que nadie choque.",
                "Le pagan una dracma al día, lo de un buen jornal. En tierra va a la asamblea, donde puede hablar y votar como cualquier otro ciudadano, y al teatro en las fiestas de Dioniso.",
                "Come pan de cebada, aceitunas, higos, queso y pescado salado; bebe vino mezclado con agua. Este año empieza la guerra con Esparta, y pronto vendrá la peste.",
            ),
            dia = listOf(t(21.5, 5.0, Hacer.DORMIR), t(5.0, 6.0, Hacer.COMER), t(6.0, 12.0, Hacer.TRABAJO, "remar"), t(12.0, 13.5, Hacer.COMER, "en la playa"), t(13.5, 17.0, Hacer.TRABAJO),
                t(17.0, 21.5, Hacer.GENTE, "ágora, banquete")),
            datos = mapOf("pasos" to e(12000), "km_anio" to e(4000), "kcal" to e(3200, "Remar exigía mucha energía."), "proteina" to e(80), "azucar" to e(20), "fibra" to e(45), "alcohol" to e(30),
                "sueno" to e(7.0), "dormir" to e(21.5), "trabajo" to e(40), "aire_libre" to e(9), "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(1, "El aulós marcaba el remo."),
                "personas_dia" to e(170), "conocidas" to e(3000), "poblacion" to e(250000, "Ática entera."), "lejos" to e(800), "estatura" to r(168), "vida" to e(28), "vida_adulto" to e(55),
                "hijos" to e(4), "alfabetizacion" to e(10), "lectura" to r(0)),
            fuentes = listOf("El trirreme" to "${W}Trireme", "La democracia ateniense" to "${W}Athenian_democracy")),

        // La vida del tema Egipto (su id de tema, «egipto», es el mismo texto que el id de la vida de Ipy en BASE: son
        // espacios de nombres distintos, el de los temas y el de las vidas, y no chocan).
        Vida("escriba_tumba", "Pay", "Escriba de la tumba", "Deir el-Medina, Tebas oeste", "hacia el 1200 a. C.", -1200, "tema", "egipto", edad = 34,
            historia = listOf(
                "Pay es uno de los escribas de «la Tumba», la cuadrilla que excava y decora las tumbas de los reyes en el Valle. Vive en la aldea de los obreros: unas setenta casas de adobe apretadas entre los cerros del desierto, sin agua propia; el agua se la suben en burro desde el río.",
                "Su oficio es llevar la cuenta de todo en óstraca y papiros: cuánto grano llega, cuántas mechas y cinceles de cobre se reparten, quién faltó y por qué («enfermo», «picado por un escorpión»). La semana dura diez días; se trabaja ocho, en dos turnos de unas cuatro horas, y a veces se duerme en las chozas del paso de la montaña.",
                "Le pagan cada mes en grano, más que a un obrero: trigo para el pan y cebada para la cerveza, con pescado, verduras y leña. En la aldea leen bastantes; en todo Egipto, quizá uno de cada cien. Pay es un personaje inventado, con un nombre común en la aldea; la aldea y sus registros son reales.",
            ),
            dia = listOf(t(20.0, 5.0, Hacer.DORMIR), t(5.0, 6.0, Hacer.COMER), t(6.0, 6.5, Hacer.CAMINO, "subir al valle"), t(6.5, 11.0, Hacer.TRABAJO, "anotar raciones, herramientas, ausencias"),
                t(11.0, 12.0, Hacer.COMER, "y la siesta"), t(12.0, 16.0, Hacer.TRABAJO), t(16.0, 17.0, Hacer.CAMINO), t(17.0, 18.0, Hacer.CUIDAR), t(18.0, 20.0, Hacer.GENTE)),
            datos = mapOf("pasos" to r(10000, "La subida por el paso de la montaña, ida y vuelta, cada día de trabajo."), "km_anio" to r(2500), "kcal" to r(2800, "Pan, cerveza, cebolla, lentejas y pescado; su ración de grano alcanzaba para la familia."),
                "proteina" to r(70), "azucar" to r(10, "Dátiles y algo de miel."), "fibra" to r(45), "alcohol" to r(35, "Cerveza espesa y poco alcohólica, a diario."), "sueno" to r(9.0, "Se acuesta con la noche; además, la siesta del mediodía."),
                "dormir" to r(20.0), "trabajo" to e(48, "Ocho días de cada diez, en dos turnos de unas cuatro horas."), "aire_libre" to r(4, "El camino y la obra al sol; dentro de la tumba, a la luz de mechas de aceite."), "pantalla" to r(0),
                "notificaciones" to r(3), "musica" to r(0.5, "Cantos de trabajo y fiestas de la aldea."), "personas_dia" to e(60, "La cuadrilla tenía entre unos cuarenta y ciento veinte hombres, según el reinado."), "conocidas" to r(800),
                "poblacion" to e(350, "Entre 200 y 500 personas, según la época."), "lejos" to r(30, "Hasta los templos de la otra orilla."), "estatura" to r(165), "vida" to r(25), "vida_adulto" to r(50), "hijos" to r(5),
                "alfabetizacion" to e(1, "En todo Egipto, quizá uno de cada cien; en la aldea, muchos más: allí se han hallado miles de óstraca escritos."), "lectura" to r(4, "Copias de enseñanzas y poemas, que también aparecen en la aldea.")),
            fuentes = listOf("Deir el-Medina, la aldea de los obreros" to "${W}Deir_el-Medina", "Las huelgas de Deir el-Medina: raciones y turnos" to "${W}Deir_el-Medina_strikes",
                "Óstracon con el registro de ausencias (British Museum EA5634)" to "https://www.britishmuseum.org/collection/object/Y_EA5634")),

        Vida("copista", "Hermano Odo", "Monje copista", "Una abadía benedictina", "hacia 1150", 1150, "tema", "medieval", edad = 35,
            historia = listOf(
                "El hermano Odo se acuesta al anochecer, después de completas, y lo despiertan a la madrugada para maitines. Reza en el coro siete veces al día: unas cuatro horas de canto llano diario.",
                "En el scriptorium copia con pluma de ganso sobre pergamino solo con luz de día: un libro le lleva meses. Cada Cuaresma la Regla le asigna un libro que debe leer entero.",
                "En algunas abadías ricas los monjes comían más de cinco mil kilocalorías los días sin ayuno, con un galón de cerveza; en otras, mucho menos. No tendrá hijos, y casi todo el mundo que conocerá cabe en su monasterio.",
            ),
            dia = listOf(t(19.5, 2.0, Hacer.DORMIR), t(2.0, 3.5, Hacer.REZAR, "maitines y laudes"), t(3.5, 5.5, Hacer.DORMIR), t(5.5, 7.0, Hacer.REZAR, "prima"), t(7.0, 11.0, Hacer.TRABAJO, "copiar"),
                t(11.0, 12.0, Hacer.REZAR, "sexta"), t(12.0, 13.0, Hacer.COMER, "en silencio, con lectura"), t(13.0, 16.5, Hacer.TRABAJO), t(16.5, 18.0, Hacer.REZAR, "vísperas"), t(18.0, 19.5, Hacer.COMER)),
            datos = mapOf("pasos" to e(6000), "km_anio" to e(1500), "kcal" to e(4500, "Barbara Harvey calculó hasta 6,000 en Westminster en días de fiesta."), "proteina" to e(110), "azucar" to e(10),
                "fibra" to e(40), "alcohol" to e(50, "Cerveza o la hemina de vino de la Regla."), "sueno" to e(6.5, "En dos partes."), "dormir" to e(19.5), "trabajo" to e(35), "aire_libre" to e(2),
                "pantalla" to r(0), "notificaciones" to e(8, "Las campanas de las horas."), "musica" to e(3.5, "El oficio cantado."), "personas_dia" to e(40), "conocidas" to e(300), "poblacion" to e(80),
                "lejos" to e(100), "estatura" to r(170), "vida_adulto" to e(55), "hijos" to e(0), "alfabetizacion" to e(100, "Dentro del monasterio."), "lectura" to e(1, "El libro de Cuaresma.")),
            fuentes = listOf("Barbara Harvey, Living and Dying in England 1100–1540" to "https://fivebooks.com/book/living-and-dying-in-england-1100-1540-the-monastic-experience-by-barbara-harvey/", "Scriptorium" to "${W}Scriptorium")),

        Vida("sargento", "Hermano Bernat", "Sargento de una orden militar", "Castillo de frontera en el Levante", "hacia 1250", 1250, "tema", "templario", edad = 29,
            historia = listOf(
                "El hermano Bernat no es caballero: es sargento, un freire de la orden que no viene de linaje noble. Viste de pardo y no de blanco, pelea a caballo o a pie detrás de los caballeros y cuida sus monturas y sus armas. Ha hecho los votos de pobreza, castidad y obediencia.",
                "La Regla le ordena todo: a qué hora se levanta para maitines en plena noche, cómo come (en silencio, mientras un hermano lee en voz alta), cuántos caballos puede tener y qué castigo le toca si pierde el equipo. No puede salir del castillo ni recibir una carta sin permiso del maestre.",
                "Desde la muralla mira la arena y los caminos de los peregrinos. Escolta caravanas de un castillo a otro, monta guardia y repara la muralla. No tendrá hijos, y casi toda la gente que conocerá cabe en unos pocos castillos.",
            ),
            dia = listOf(t(20.0, 0.0, Hacer.DORMIR), t(0.0, 1.5, Hacer.REZAR, "maitines"), t(1.5, 5.5, Hacer.DORMIR), t(5.5, 6.0, Hacer.REZAR, "prima"), t(6.0, 11.0, Hacer.TRABAJO, "caballos, armas, guardia"),
                t(11.0, 12.0, Hacer.COMER, "en silencio, con lectura"), t(12.0, 17.0, Hacer.TRABAJO, "instrucción y muralla"), t(17.0, 18.0, Hacer.REZAR, "vísperas"), t(18.0, 19.0, Hacer.COMER),
                t(19.0, 20.0, Hacer.REZAR, "completas")),
            datos = mapOf("pasos" to r(11000, "Mucho del día va a caballo o en la muralla."), "km_anio" to r(4000, "Escoltas y cabalgadas entre castillos."),
                "kcal" to r(3500, "La Regla daba carne unos tres días a la semana."), "proteina" to r(100, "Los domingos, los sargentos tenían una comida de carne y los caballeros, dos."), "azucar" to r(10), "fibra" to r(40),
                "alcohol" to r(30, "Vino en la mesa, repartido por parejas de hermanos."), "sueno" to r(6.0, "En dos partes, cortado por maitines."), "dormir" to r(20.0, "Después de completas."),
                "trabajo" to r(60), "aire_libre" to r(9), "pantalla" to r(0), "notificaciones" to r(10, "La campana de la capilla y los toques de la guardia."),
                "musica" to r(1, "Oír el oficio; quien no sabía leer rezaba padrenuestros."), "personas_dia" to r(70, "La guarnición de un castillo mediano."), "conocidas" to r(800),
                "poblacion" to r(400, "Freires, soldados a sueldo, criados y peregrinos de paso."), "lejos" to r(3500, "Desde su tierra natal, al otro lado del mar."), "estatura" to r(170),
                "vida" to r(30), "vida_adulto" to r(50, "Si no lo mataba la guerra."), "hijos" to e(0, "Por el voto de castidad."), "alfabetizacion" to r(10, "Entre los freires sargentos, pocos."), "lectura" to r(0)),
            fuentes = listOf("Los templarios" to "${W}Knights_Templar", "La Regla latina del Temple" to "${W}Latin_Rule",
                "Las órdenes militares" to "${W}Military_order_(religious_society)", "Un castillo templario: Atlit" to "${W}Castle_Pilgrim"),
            inspiracion = "Persona inventada. Su día sigue la Regla del Temple (la latina de 1129 y los artículos franceses que se le añadieron); las cifras son reconstrucciones, no registros."),

        Vida("cantero", "Pierre", "Cantero", "La obra de la catedral de Amiens", "1240", 1240, "tema", "gotico", edad = 34,
            historia = listOf(
                "Pierre talla piedra para una catedral que no verá terminada. En verano trabaja de sol a sol, doce horas; en invierno, ocho, y se paga menos. Los canteros viajan de obra en obra: ha trabajado en tres ciudades.",
                "Sube andamios de madera a decenas de metros y marca cada piedra con su signo para que le paguen la suya. En la logia, la cabaña de los canteros, se come, se discuten los planos y se guardan los secretos del oficio.",
                "Parte del pago es en vino o cerveza. Una caída o un derrumbe pueden acabar con todo; si no, verá subir las bóvedas año con año.",
            ),
            dia = listOf(t(21.0, 4.5, Hacer.DORMIR), t(4.5, 5.0, Hacer.COMER), t(5.0, 11.5, Hacer.TRABAJO, "tallar y subir"), t(11.5, 12.5, Hacer.COMER, "en la logia"), t(12.5, 18.5, Hacer.TRABAJO),
                t(18.5, 21.0, Hacer.GENTE)),
            datos = mapOf("pasos" to e(10000), "km_anio" to e(2500), "kcal" to e(3500), "proteina" to e(90), "azucar" to e(5), "fibra" to e(50), "alcohol" to e(40), "sueno" to e(7.5), "dormir" to e(21.0),
                "trabajo" to e(60, "En verano; menos en invierno."), "aire_libre" to e(10), "pantalla" to r(0), "notificaciones" to e(6, "Campanas."), "musica" to e(0.5), "personas_dia" to e(60),
                "conocidas" to e(2000), "poblacion" to e(20000), "lejos" to e(600), "estatura" to r(170), "vida" to e(30), "vida_adulto" to e(52), "hijos" to e(4), "alfabetizacion" to e(10), "lectura" to r(0)),
            fuentes = listOf("Catedral de Amiens" to "${W}Amiens_Cathedral", "Canteros medievales" to "${W}Stonemasonry")),

        Vida("aprendiz", "Iacopo", "Aprendiz de pintor", "Un taller de Florencia", "1490", 1490, "tema", "renacimiento", edad = 19,
            historia = listOf(
                "Iacopo entró al taller a los trece. Muele pigmentos, prepara tablas con yeso, copia dibujos del maestro y, ahora, pinta fondos y ropajes de los encargos. Duerme en el taller y come en la mesa del maestro.",
                "Florencia tiene unas sesenta mil personas, bancos, gremios y más niños en la escuela que casi cualquier ciudad de Europa. Los libros impresos acaban de llegar y ya no cuestan una fortuna.",
                "Toma vino en cada comida, come pan, frijoles, verduras, y carne los días de fiesta, que son muchos. Si tiene talento, un día abrirá su propio taller.",
            ),
            dia = listOf(t(21.5, 5.0, Hacer.DORMIR), t(5.0, 6.0, Hacer.COMER), t(6.0, 12.0, Hacer.TRABAJO, "pigmentos y tablas"), t(12.0, 13.0, Hacer.COMER), t(13.0, 18.5, Hacer.TRABAJO, "pintar"),
                t(18.5, 21.5, Hacer.GENTE, "plaza y taberna")),
            datos = mapOf("pasos" to e(11000), "km_anio" to e(2500), "kcal" to e(3000), "proteina" to e(80), "azucar" to e(10), "fibra" to e(40), "alcohol" to e(40), "sueno" to e(7.0), "dormir" to e(21.5),
                "trabajo" to e(60), "aire_libre" to e(4), "pantalla" to r(0), "notificaciones" to e(6, "Campanas."), "musica" to e(0.7), "personas_dia" to e(100), "conocidas" to e(3000), "poblacion" to e(60000),
                "lejos" to e(80), "estatura" to r(167), "vida" to e(30), "vida_adulto" to e(55), "hijos" to e(5), "alfabetizacion" to e(30), "lectura" to e(1)),
            fuentes = listOf("Florencia en el Renacimiento" to "${W}Republic_of_Florence", "El taller de Verrocchio" to "${W}Andrea_del_Verrocchio")),
        Vida("grumete", "Tomás", "Grumete", "Un balandro pirata en el Caribe", "1718", 1718, "tema", "pirata", edad = 14,
            historia = listOf(
                "Tomás subió a bordo de un mercante a los doce, como paje, y cuando los piratas tomaron el barco se quedó con ellos: en un balandro le dan su parte del botín, aunque sea media. Baldea la cubierta, sube a la jarcia a soltar o aferrar las velas, ceba los cañones y sirve el rancho.",
                "El día se cuenta por guardias de cuatro horas y la campana suena cada media hora. Duerme a pedazos, en un coy o donde caiga, entre setenta y tantos hombres que ve todos los días: los mismos, durante meses. Casi todo el día está al aire libre, al sol y al viento.",
                "Come galleta, carne salada, frijoles y lo que se saque de las presas; bebe ron rebajado con agua (el grog) todos los días. Ese año el rey ofrece el indulto a quien se rinda y en noviembre cae Barbanegra: la mayoría de las tripulaciones no duran más de un par de años.",
            ),
            dia = listOf(t(0.0, 4.0, Hacer.DORMIR), t(4.0, 8.0, Hacer.TRABAJO, "baldear la cubierta en la guardia del alba"), t(8.0, 9.0, Hacer.COMER),
                t(9.0, 12.0, Hacer.TRABAJO, "remendar velas y cabos"), t(12.0, 13.0, Hacer.COMER, "galleta y grog"), t(13.0, 16.0, Hacer.DORMIR),
                t(16.0, 20.0, Hacer.TRABAJO, "guardias cortas de la tarde"), t(20.0, 24.0, Hacer.GENTE, "canto y dados")),
            datos = mapOf("pasos" to r(9000, "Cubierta, jarcia y escalas; casi nada en tierra."), "km_anio" to r(12000, "Lo que navega el balandro en un año de corso."),
                "kcal" to r(4000, "Las raciones de la marina de entonces se calculan entre 3,300 y 5,000 kcal; los piratas comían a rachas."),
                "proteina" to e(110, "Carne salada y frijoles."), "azucar" to e(5), "fibra" to e(20), "alcohol" to e(45, "Su ración de ron rebajado; los adultos bebían más."),
                "sueno" to e(7.0, "Partido en dos: de noche y en la tarde."), "dormir" to e(0.0), "trabajo" to e(77), "aire_libre" to e(14), "pantalla" to r(0),
                "notificaciones" to e(36, "La campana de a bordo, cada media hora de guardia."), "musica" to e(1.5, "Salomas para el trabajo y canto por la noche."),
                "personas_dia" to e(75, "Toda la tripulación, todos los días."), "conocidas" to e(400), "poblacion" to e(75, "Hacia 1720 navegaban unos 2,400 piratas en unos 32 barcos."),
                "lejos" to r(2500), "estatura" to r(158), "vida" to r(35), "vida_adulto" to r(45), "hijos" to r(5), "alfabetizacion" to r(30), "lectura" to r(0)),
            fuentes = listOf("La edad de oro de la piratería" to "${W}Golden_Age_of_Piracy", "La campana de a bordo y las guardias" to "${W}Ship%27s_bell",
                "Quiénes eran los piratas del Caribe (Smithsonian)" to "https://www.smithsonianmag.com/history/who-were-the-real-pirates-of-the-caribbean-180984343/",
                "La galleta de a bordo (Royal Museums Greenwich)" to "https://www.rmg.co.uk/stories/topics/ships-biscuit")),

        Vida("cordobesa", "Zaynab", "Tejedora de seda", "Córdoba", "hacia el 970", 970, "tema", "andalus", edad = 27,
            historia = listOf(
                "Zaynab teje seda en un taller de la ciudad más grande de Europa occidental en su tiempo, capital de un califato. Las calles tienen faroles y hay baños públicos en cada barrio.",
                "Come pan, aceite de oliva, garbanzos, berenjena, frutas; el azúcar de caña, traído de Oriente, empieza a endulzar postres que antes solo llevaban miel. Vive alrededor de un patio con agua y plantas.",
                "La biblioteca del califa, dicen, tiene cientos de miles de libros. Por las tardes se oye el laúd, que un músico venido de Bagdad puso de moda generaciones atrás.",
            ),
            dia = listOf(t(21.5, 5.0, Hacer.DORMIR), t(5.0, 5.5, Hacer.REZAR), t(5.5, 6.5, Hacer.COMER), t(6.5, 12.5, Hacer.TRABAJO, "telar"), t(12.5, 14.0, Hacer.COMER), t(14.0, 17.5, Hacer.TRABAJO),
                t(17.5, 19.0, Hacer.GENTE, "baño y patio"), t(19.0, 21.5, Hacer.CUIDAR)),
            datos = mapOf("pasos" to e(9000), "km_anio" to e(2000), "kcal" to e(2300), "proteina" to e(70), "azucar" to e(20), "fibra" to e(40), "alcohol" to e(2), "sueno" to e(7.0), "dormir" to e(21.5),
                "trabajo" to e(45), "aire_libre" to e(4), "pantalla" to r(0), "notificaciones" to e(5, "Los cinco llamados a la oración."), "musica" to e(1), "personas_dia" to e(100), "conocidas" to e(3000),
                "poblacion" to e(100000, "Las estimaciones van de 100,000 a más de 400,000."), "lejos" to e(150), "estatura" to r(156), "vida" to e(35), "vida_adulto" to e(55), "hijos" to e(5),
                "alfabetizacion" to e(15), "lectura" to e(0.5)),
            fuentes = listOf("El califato de Córdoba" to "${W}Caliphate_of_C%C3%B3rdoba")),

        // La vida del tema Mesoamérica: un escriba maya del Clásico tardío, de las tierras bajas (no repite a Citlali, la
        // macehual de Tenochtitlan de las vidas de todos: otra región, otro pueblo y siete siglos antes).
        Vida("escriba_maya", "K'an", "Escriba y pintor de códices", "Una ciudad maya de las tierras bajas", "hacia el 750", 750, "tema", "meso", edad = 30,
            historia = listOf(
                "K'an es un aj tz'ib, «el de la escritura»: uno de los pocos de su ciudad que saben leer y pintar los glifos. Trabaja en una casa de piedra junto a la plaza, con pinceles de pelo y tinta negra y roja guardada en conchas cortadas, sobre tiras de papel amate cubiertas de estuco blanco que luego se doblan como un biombo.",
                "Su oficio es llevar la cuenta del tiempo: qué día del tzolk'in de 260 días cae con qué día del haab de 365, cuándo se cierra un katún, qué dice el almanaque para la siembra y para los ritos. También pinta escenas y textos en vasos de cerámica para beber cacao, que su señor regala a otros señores; algunos escribas firmaban esos vasos.",
                "Come sobre todo maíz: atole por la mañana, tamales y tortillas, con frijol, calabaza y chile; el cacao espumoso es bebida de la corte y de las fiestas, y la carne de venado o de pavo llega pocas veces. Su ciudad vive su mejor siglo; en las generaciones siguientes, muchas de estas ciudades se irán vaciando. K'an es un personaje inventado; su oficio, sus herramientas y sus cuentas son reales.",
            ),
            dia = listOf(t(21.0, 5.0, Hacer.DORMIR), t(5.0, 6.0, Hacer.COMER, "atole"), t(6.0, 12.0, Hacer.TRABAJO, "pintar glifos en amate"), t(12.0, 13.0, Hacer.COMER),
                t(13.0, 16.0, Hacer.TRABAJO, "cuentas del calendario"), t(16.0, 18.0, Hacer.REZAR, "ritual en el templo"), t(18.0, 19.0, Hacer.COMER, "tamales y cacao"), t(19.0, 21.0, Hacer.GENTE)),
            datos = mapOf("pasos" to r(8000, "Casi todo el día sentado ante el papel; camina a la plaza, al templo y a su casa."), "km_anio" to r(1500),
                "kcal" to r(2500, "El maíz daba quizá dos tercios o más de la energía."), "proteina" to r(60, "Frijol y maíz; carne de venado, pavo o pescado de vez en cuando."),
                "azucar" to r(8, "Miel de abejas sin aguijón, que se criaban en troncos huecos."), "fibra" to r(55), "alcohol" to r(5, "Balché, una bebida de miel fermentada con corteza, sobre todo en los ritos."),
                "sueno" to r(8.0), "dormir" to r(21.0), "trabajo" to r(48), "aire_libre" to r(4, "Pinta a la sombra, con la luz del día; el resto, en la plaza y en el templo."), "pantalla" to r(0),
                "notificaciones" to r(3, "La caracola y los tambores que llaman a los ritos."), "musica" to r(0.5, "Flautas, caracolas, tambores y sonajas en las ceremonias."),
                "personas_dia" to r(50), "conocidas" to r(1500), "poblacion" to r(40000, "Una ciudad mediana del Clásico tardío; las mayores, como Tikal, pasaban de 50,000."),
                "lejos" to r(150, "Hasta una ciudad aliada, por los caminos blancos de piedra (sacbeob)."), "estatura" to r(158, "Promedio aproximado de los hombres mayas del Clásico, según sus esqueletos."),
                "vida" to r(28), "vida_adulto" to r(50), "hijos" to r(5), "alfabetizacion" to r(1, "Leer y escribir era cosa de una minoría de la corte y los sacerdotes."),
                "lectura" to r(3, "Los códices de almanaques y profecías de su ciudad, que copia y consulta.")),
            fuentes = listOf("La escritura maya" to "${W}Maya_script", "Los códices mayas" to "${W}Maya_codices", "El calendario maya" to "${W}Maya_calendar",
                "La cocina maya" to "${W}Maya_cuisine"),
            inspiracion = "Persona inventada. Su oficio, sus herramientas y su calendario siguen lo que se sabe de los escribas mayas del Clásico; las cifras son reconstrucciones, no registros."),

        Vida("pastor_mongol", "Bat", "Pastor nómada", "La estepa mongola", "hacia 1220", 1220, "tema", "mongol", edad = 30,
            historia = listOf(
                "Bat casi no camina: pasa el día a caballo cuidando ovejas, cabras, yaks y caballos. Su familia muda el ger, la tienda de fieltro, cuatro veces al año, siguiendo los pastos.",
                "Come carne y leche en todas sus formas: queso seco, mantequilla, yogur, y bebe airag, leche de yegua fermentada, a litros en el verano. Casi no prueba vegetales. Su campamento son unas pocas familias.",
                "Hace poco cabalgó con el ejército hacia el oeste, miles de kilómetros, hasta ciudades que no imaginaba. Canta con la garganta y toca el violín de cabeza de caballo.",
            ),
            dia = listOf(t(22.0, 5.0, Hacer.DORMIR), t(5.0, 6.0, Hacer.CUIDAR, "ordeñar"), t(6.0, 12.0, Hacer.TRABAJO, "pastorear a caballo"), t(12.0, 13.0, Hacer.COMER), t(13.0, 19.0, Hacer.TRABAJO),
                t(19.0, 22.0, Hacer.GENTE, "airag y cantos")),
            datos = mapOf("pasos" to e(5000, "Todo se hace a caballo."), "km_anio" to e(8000), "kcal" to e(3500), "proteina" to e(150), "azucar" to e(0), "fibra" to e(5, "Casi sin plantas."),
                "alcohol" to e(30, "El airag tiene 1 o 2% de alcohol."), "sueno" to e(7.0), "dormir" to e(22.0), "trabajo" to e(50), "aire_libre" to e(12), "pantalla" to r(0), "notificaciones" to r(0),
                "musica" to e(1), "personas_dia" to e(15), "conocidas" to e(1000), "poblacion" to e(30), "lejos" to e(3000, "Con el ejército, hacia el oeste."), "estatura" to r(165), "vida" to e(30),
                "vida_adulto" to e(55), "hijos" to e(5), "alfabetizacion" to e(1), "lectura" to r(0)),
            fuentes = listOf("La cocina mongola" to "${W}Mongolian_cuisine", "El imperio mongol" to "${W}Mongol_Empire")),

        Vida("mercader_sogdiano", "Nanai-vandak", "Mercader de caravana", "Entre Samarcanda y Chang'an", "hacia el 750", 750, "tema", "seda", edad = 38,
            historia = listOf(
                "Nanai-vandak camina junto a sus camellos unos treinta kilómetros al día y duerme en caravasares. Ir de Samarcanda a Chang'an le lleva medio año: ese tramo es más de lo que la mayoría de la gente de su tiempo recorre en toda la vida.",
                "Habla sogdiano, persa, turco y algo de chino. Compra seda, vende plata, almizcle, vidrio y vino, del que su pueblo tiene fama de beber mucho. Escribe cartas a casa que quizá tarden un año en llegar.",
                "Cuando las ciudades de la ruta están en guerra, espera meses. Conoce a más gente distinta que casi nadie en esta lista.",
            ),
            dia = listOf(t(21.0, 4.0, Hacer.DORMIR, "caravasar"), t(4.0, 5.0, Hacer.COMER), t(5.0, 13.0, Hacer.CAMINO, "la ruta"), t(13.0, 14.0, Hacer.COMER), t(14.0, 17.0, Hacer.CAMINO),
                t(17.0, 21.0, Hacer.GENTE, "negocios y noticias")),
            datos = mapOf("pasos" to e(15000), "km_anio" to e(4000), "kcal" to e(3000), "proteina" to e(90), "azucar" to e(15), "fibra" to e(40), "alcohol" to e(30), "sueno" to e(6.5), "dormir" to e(21.0),
                "trabajo" to e(70), "aire_libre" to e(11), "pantalla" to r(0), "notificaciones" to r(0), "musica" to e(0.5), "personas_dia" to e(50), "conocidas" to e(5000), "poblacion" to e(200, "La caravana."),
                "lejos" to e(4500), "estatura" to r(168), "vida" to e(32), "vida_adulto" to e(55), "hijos" to e(4), "alfabetizacion" to e(10), "lectura" to r(0)),
            fuentes = listOf("Los sogdianos" to "${W}Sogdia", "La Ruta de la Seda" to "${W}Silk_Road")),

        // Su tema es «edo» (TemaId.EDO). Ojo: «edo» también es el id de la vida de Kichizō en BASE; son espacios de nombres distintos (tema frente a id de vida).
        Vida("posadera", "Ochiyo", "Sirvienta de una casa de té en una posta", "Una posta del camino real entre Edo y Kioto", "1830", 1830, "tema", "edo", edad = 22,
            historia = listOf(
                "Ochiyo sirve té, dulces y arroz a los viajeros que pasan por la posta: mensajeros, peregrinos, mercaderes, samuráis con su séquito. El camino real, el Tōkaidō, tiene 53 postas entre Edo y Kioto, y por la suya pasa gente todo el día.",
                "Entró a servir de niña. Duerme en un cuarto con las otras sirvientas, se levanta antes del alba para encender el fuego y barrer la entrada, y solo tiene dos días libres al año, en el primer y el séptimo mes, para visitar a su familia.",
                "Ve cientos de caras al día y casi ninguna vuelve: es lo contrario de un pueblo, donde todos se conocen. Nunca ha ido más allá de la posta siguiente, aunque sabe de memoria lo que cuentan los viajeros de Edo, de Kioto y del monte Fuji.",
            ),
            dia = listOf(t(22.0, 5.0, Hacer.DORMIR, "en el cuarto de las sirvientas"), t(5.0, 7.0, Hacer.CUIDAR, "fuego y barrer"), t(7.0, 8.0, Hacer.COMER), t(8.0, 13.0, Hacer.TRABAJO, "servir té a los viajeros"),
                t(13.0, 14.0, Hacer.COMER), t(14.0, 21.0, Hacer.TRABAJO, "recibir viajeros, baños y cenas"), t(21.0, 22.0, Hacer.LIBRE)),
            datos = mapOf(
                "pasos" to e(14000, "Todo el día de pie: servir, barrer, ir por agua."), "km_anio" to e(3000, "Casi todo dentro de la posta."), "kcal" to e(2200), "proteina" to e(55),
                "azucar" to e(10, "Algún dulce de la casa de té."), "fibra" to e(15, "Arroz blanco: poca fibra."), "alcohol" to e(5), "sueno" to e(7.0), "dormir" to e(22.0),
                "trabajo" to e(84, "Doce horas al día, casi sin días libres."), "aire_libre" to e(4), "pantalla" to r(0),
                "notificaciones" to e(40, "Viajeros que llegan, las voces que llaman a los clientes desde la puerta, las campanas del templo."), "musica" to e(0.5, "El shamisen de algún huésped por la noche."),
                "personas_dia" to e(200, "Cientos de caras de paso."), "conocidas" to e(2000), "poblacion" to e(2000, "En el censo de 1843, las postas del Tōkaidō iban de unos 500 a más de 2,000 habitantes."),
                "lejos" to e(30, "La posta siguiente."), "estatura" to r(145), "vida" to e(35), "vida_adulto" to e(60), "hijos" to e(4),
                "alfabetizacion" to r(30, "Leer los silabarios era común en las ciudades; mucho menos entre las mujeres del campo."), "lectura" to r(1),
            ),
            fuentes = listOf("El Tōkaidō" to "${W}T%C5%8Dkaid%C5%8D_(road)", "Las postas (shukuba)" to "${W}Shukuba", "Hiratsuka-juku y el censo de 1843" to "${W}Hiratsuka-juku")),

        Vida("granadero", "Jean-Baptiste", "Soldado de infantería", "La campaña de Rusia", "1812", 1812, "tema", "napoleon", edad = 22,
            historia = listOf(
                "Jean-Baptiste fue llamado por la conscripción y cruzó el río Niemen con más de medio millón de hombres. Marcha veinticinco o treinta kilómetros al día, a veces más, con unos veinte kilos encima.",
                "En el papel le tocan 750 gramos de pan, carne y un cuarto de vino al día; en la práctica, lo que se encuentra. Duerme al raso, junto al fuego de su compañía.",
                "Llegará a Moscú y la encontrará en llamas. De los que cruzaron con él, la mayoría no volverá: el hambre, el tifus y el invierno matan más que las balas.",
            ),
            dia = listOf(t(22.0, 3.5, Hacer.DORMIR, "al raso"), t(3.5, 4.0, Hacer.COMER), t(4.0, 12.0, Hacer.CAMINO, "marcha"), t(12.0, 13.0, Hacer.COMER), t(13.0, 18.0, Hacer.CAMINO),
                t(18.0, 22.0, Hacer.CUIDAR, "vivac, buscar comida")),
            datos = mapOf("pasos" to e(35000), "km_anio" to e(5000), "kcal" to e(2000, "La ración teórica era de 3,000; la real, mucho menos."), "proteina" to e(80), "azucar" to e(0), "fibra" to e(30),
                "alcohol" to e(25), "sueno" to e(5.5), "dormir" to e(22.0), "trabajo" to e(80), "aire_libre" to e(16), "pantalla" to r(0), "notificaciones" to e(15, "Tambores y cornetas."),
                "musica" to e(0.5), "personas_dia" to e(150), "conocidas" to e(3000), "poblacion" to e(600000, "El ejército que cruzó el Niemen."), "lejos" to e(2500), "estatura" to e(164),
                "vida" to e(37), "vida_adulto" to e(60), "hijos" to e(4.4), "alfabetizacion" to e(45), "lectura" to r(0)),
            fuentes = listOf("Lo que comía el ejército de Napoleón (NPR)" to "https://www.npr.org/sections/thesalt/2015/06/18/414614705/appetite-for-war-what-napoleon-and-his-men-ate-on-the-march",
                "La infantería en la campaña de 1812" to "https://www.napoleonguide.com/campaign_russ_1812inf.htm")),

        // La vida del tema Art déco. Lou es inventada; las cifras de la ciudad y del país sí salen de los registros de la época.
        Vida("pianista", "Lou", "Pianista de un club de jazz", "Nueva York", "1926", 1926, "tema", "deco", edad = 29,
            historia = listOf(
                "Lou se levanta pasado el mediodía en un cuarto de alquiler de Harlem. En la tarde ensaya en el piano de una casa de vecinos o en el del club vacío: la mano izquierda salta entre el bajo y el acorde, como en el piano «stride», y la derecha inventa encima.",
                "A las nueve de la noche toma el metro hacia el club. Es plena Ley Seca: el alcohol está prohibido desde 1920, pero en el sótano se sirve igual, en tazas de café si hace falta. Lou toca siete horas casi seguidas, entre el humo, el charlestón y el ruido de la gente.",
                "Cuando el club cierra, a las cuatro, empieza lo que más le gusta: la sesión de después, en otro sótano o en una fiesta de alquiler, donde los músicos tocan para ellos mismos hasta que sale el sol. Duerme de día, como casi todos los músicos de la ciudad.",
            ),
            dia = listOf(t(6.0, 13.0, Hacer.DORMIR), t(13.0, 14.0, Hacer.COMER), t(14.0, 17.0, Hacer.LIBRE, "ensayar"), t(17.0, 19.0, Hacer.CUIDAR), t(19.0, 20.0, Hacer.COMER),
                t(20.0, 21.0, Hacer.CAMINO, "en metro"), t(21.0, 4.0, Hacer.TRABAJO, "tocar en el club"), t(4.0, 6.0, Hacer.GENTE, "la sesión de después")),
            datos = mapOf("pasos" to r(6000, "Calles y escaleras del metro; casi todo el resto, sentado al piano."), "km_anio" to r(5000), "kcal" to r(2500), "proteina" to r(80),
                "azucar" to r(70, "Refrescos, café endulzado y los dulces de las cafeterías que abren de noche."), "fibra" to r(15), "alcohol" to r(40, "Prohibido por la Ley Seca, pero servido en los clubes."),
                "sueno" to r(7.0), "dormir" to r(6.0), "trabajo" to r(49, "Siete noches por semana, siete horas cada una."), "aire_libre" to r(1, "Casi no ve el sol: duerme de día."),
                "pantalla" to r(0.3, "Alguna función de cine mudo en la tarde."), "notificaciones" to r(10, "El timbre del teléfono del club, los recados, la puerta."), "musica" to r(9, "Siete horas en el club y la sesión de después."),
                "personas_dia" to r(200, "El público del club."), "conocidas" to r(3000), "poblacion" to e(6300000, "Entre los censos de 1920 (5.6 millones) y 1930 (6.9 millones)."), "lejos" to r(1500),
                "estatura" to r(170), "vida" to m(56.7, "Estados Unidos, 1926, en los estados que registraban las defunciones (NCHS)."), "vida_adulto" to r(65),
                "hijos" to r(2.9, "Aproximado para el país a mediados de los años veinte; no está comprobado."), "alfabetizacion" to e(94, "En el censo de 1920, cerca del 6% no sabía escribir."),
                "lectura" to r(6, "Periódicos y revistas a diario; algún libro.")),
            fuentes = listOf("La Ley Seca en Estados Unidos" to "${W}Prohibition_in_the_United_States", "El piano stride" to "${W}Stride_(music)",
                "El Renacimiento de Harlem" to "${W}Harlem_Renaissance", "Población de Nueva York por censo" to "${W}Demographic_history_of_New_York_City",
                "NCHS: esperanza de vida al nacer desde 1900" to "https://data.cdc.gov/NCHS/NCHS-Death-rates-and-life-expectancy-at-birth/w9j2-ggv5"),
            inspiracion = "Inspirado en los pianistas de stride de Harlem de los años veinte, que tocaban en clubes y fiestas de alquiler hasta el amanecer."),

        Vida("lavandera", "Marie-Jeanne", "Lavandera", "Un barco-lavadero en el Sena, París", "1789", 1789, "tema", "revolucion", edad = 28,
            historia = listOf(
                "Marie-Jeanne lava ropa ajena en uno de los barcos-lavaderos amarrados en el Sena: de rodillas, con las manos en agua fría, golpeando la ropa con una paleta de madera, desde que amanece hasta que se va la luz.",
                "Casi todo lo que gana se va en pan. En el verano de 1789 una hogaza de cuatro libras llega a costar catorce sueldos y medio, cuando el jornal de un obrero da entre veinte y treinta, y el de una mujer, menos. La cola de la panadería empieza antes del alba.",
                "En octubre caminará con miles de mujeres de los mercados y los barrios los veinte kilómetros hasta Versalles para pedir pan. No sabe leer, pero en la plaza alguien lee en voz alta los periódicos y los carteles pegados en los muros.",
            ),
            dia = listOf(t(21.5, 4.5, Hacer.DORMIR), t(4.5, 5.5, Hacer.CUIDAR, "la cola del pan"), t(5.5, 12.0, Hacer.TRABAJO, "lavar en el barco"), t(12.0, 12.5, Hacer.COMER),
                t(12.5, 18.5, Hacer.TRABAJO, "tender y entregar"), t(18.5, 19.5, Hacer.COMER, "sopa y pan"), t(19.5, 21.5, Hacer.GENTE, "la plaza y la sección")),
            datos = mapOf("pasos" to e(12000), "km_anio" to e(1500), "kcal" to e(2000, "Casi todo, pan."), "proteina" to e(55), "azucar" to e(5), "fibra" to e(30),
                "alcohol" to e(15, "Vino barato de las tabernas de fuera de la muralla."), "sueno" to e(7.0), "dormir" to e(21.5), "trabajo" to e(78), "aire_libre" to e(10, "Sobre el río, bajo un techo abierto."),
                "pantalla" to r(0), "notificaciones" to e(8, "Campanas, pregoneros y el tambor de la guardia."), "musica" to e(0.5, "Canciones de la calle."), "personas_dia" to e(150),
                "conocidas" to e(2000), "poblacion" to e(600000, "París hacia 1789."), "lejos" to e(20, "Versalles."), "estatura" to e(151), "vida" to e(28), "vida_adulto" to e(60),
                "hijos" to e(5), "alfabetizacion" to e(37, "Firmaban su acta de matrimonio cerca de la mitad de los hombres y una cuarta parte de las mujeres; en París, muchos más."), "lectura" to r(0)),
            fuentes = listOf("La marcha sobre Versalles" to "${W}Women%27s_March_on_Versailles", "Los lavaderos" to "${W}Lavoir"),
            inspiracion = "Inspirado en las mujeres de los mercados de París que marcharon sobre Versalles el 5 de octubre de 1789."),

        Vida("naturalista", "Un joven naturalista", "Naturalista a bordo de un bergantín", "La vuelta al mundo", "1832", 1832, "tema", "botanico", edad = 23,
            historia = listOf(
                "Tiene veintitrés años y se embarcó en un bergantín de la marina de 27 metros, con unas setenta personas a bordo, para un viaje que iba a durar dos años y durará cinco. Se marea todo el tiempo en el mar.",
                "En tierra es otra persona: cabalga con gauchos, sube a los Andes, colecciona escarabajos, fósiles y aves, y lo anota todo en libretas de campo. En el barco come galleta, carne salada y, como remedio contra el escorbuto, jugo de limón.",
                "Cajones de especímenes viajan de regreso a Inglaterra. Lo que ve en unas islas volcánicas del Pacífico, con sus tortugas y pinzones distintos en cada isla, le dará vueltas en la cabeza el resto de su vida.",
            ),
            dia = listOf(t(22.0, 5.5, Hacer.DORMIR, "hamaca"), t(5.5, 6.5, Hacer.COMER), t(6.5, 13.0, Hacer.TRABAJO, "colectar en tierra"), t(13.0, 14.0, Hacer.COMER), t(14.0, 18.0, Hacer.TRABAJO, "disecar y etiquetar"),
                t(18.0, 19.0, Hacer.COMER, "mesa del capitán"), t(19.0, 22.0, Hacer.LIBRE, "escribir el diario")),
            datos = mapOf("pasos" to e(9000, "Pocos en el barco; muchísimos en tierra."), "km_anio" to e(13000), "kcal" to e(3000), "proteina" to e(100), "azucar" to e(40, "Azúcar en la ración de la marina."),
                "fibra" to e(15), "alcohol" to e(15, "A los marineros se les daba grog de ron cada día."), "sueno" to e(7.0), "dormir" to e(22.0), "trabajo" to e(70), "aire_libre" to e(7),
                "pantalla" to r(0), "notificaciones" to e(10, "La campana de las guardias."), "musica" to e(0.3), "personas_dia" to e(70, "La tripulación."), "conocidas" to e(2000), "poblacion" to e(74),
                "lejos" to e(17000, "Del otro lado del mundo."), "estatura" to e(175), "vida" to e(40), "vida_adulto" to e(65), "hijos" to e(5), "alfabetizacion" to e(60), "lectura" to e(25)),
            fuentes = listOf("El viaje del Beagle" to "${W}Second_voyage_of_HMS_Beagle", "HMS Beagle" to "${W}HMS_Beagle"),
            inspiracion = "Inspirado en el viaje de Charles Darwin (1831–1836)."),

        Vida("semillera", "Una recolectora", "Recolectora de semillas", "Una ciudad que el bosque recuperó", "año 40 tras el silencio", 40, "tema", "ruinas", edad = 33, ficticia = true,
            inspiracion = "Inventada para la Ciudad verde. La idea de cómo la vegetación recupera una ciudad vacía viene del libro de Alan Weisman y de lo que creció en Prípiat después de 1986.",
            historia = listOf(
                "Nació siete años después del silencio, cuando la ciudad ya llevaba tiempo vacía. Vive con unas sesenta personas en un edificio de oficinas cuyas azoteas se volvieron huertos. Por las avenidas crecen abedules y la hiedra cubre los semáforos.",
                "Su trabajo es guardar semillas: cada mañana cuida el huerto de la azotea, y a mediodía recorre las calles con una mochila para juntar las de los árboles, las hierbas y las hortalizas que se asilvestraron en los jardines. Por la tarde las seca y las guarda en latas con su etiqueta a lápiz.",
                "Camina muchísimo y no ve una pantalla jamás: la última se apagó antes de que naciera. Duerme cuando se va la luz y despierta con el sol. Aprendió a leer con los libros que se salvaron de la biblioteca, y de noche, junto a la fogata, alguien canta.",
            ),
            dia = listOf(t(21.0, 5.5, Hacer.DORMIR), t(5.5, 6.0, Hacer.COMER), t(6.0, 11.0, Hacer.TRABAJO, "huerto en la azotea"), t(11.0, 13.0, Hacer.CAMINO, "calles con hiedra"),
                t(13.0, 14.0, Hacer.COMER), t(14.0, 17.0, Hacer.TRABAJO, "secar y guardar semillas"), t(17.0, 19.0, Hacer.CUIDAR, "agua, leña y remiendos"), t(19.0, 21.0, Hacer.GENTE, "fogata")),
            datos = mapOf("pasos" to i(24000, "Va a pie a todas partes: no hay coches ni gasolina."), "km_anio" to i(3500), "kcal" to i(2600), "proteina" to i(60), "azucar" to i(5, "Miel silvestre de vez en cuando."),
                "fibra" to i(45, "Huerto, semillas y frutos del monte."), "alcohol" to i(3), "sueno" to i(8.0, "Duerme con el sol: no hay luz eléctrica."), "dormir" to i(21.0), "trabajo" to i(48),
                "aire_libre" to i(10), "pantalla" to i(0, "La última pantalla se apagó antes de que naciera."), "notificaciones" to i(2, "Un silbido desde otra azotea."), "musica" to i(1, "Canciones junto a la fogata."),
                "personas_dia" to i(15), "conocidas" to i(120), "poblacion" to i(60, "El campamento, en una ciudad donde antes vivían cientos de miles."), "lejos" to i(60), "estatura" to i(166),
                "vida" to i(50), "vida_adulto" to i(62), "hijos" to i(2.5), "alfabetizacion" to i(70, "Los que crecieron con los libros que se salvaron."), "lectura" to i(6, "Libros rescatados de la biblioteca.")),
            fuentes = listOf("El mundo sin nosotros (Alan Weisman, 2007)" to "${W}The_World_Without_Us", "La zona de exclusión de Chernóbil" to "${W}Chernobyl_Exclusion_Zone",
                "Prípiat, la ciudad vacía desde 1986" to "${W}Pripyat", "Guardar semillas" to "${W}Seed_saving")),

        Vida("escribiente", "Tom", "Escribiente", "Una oficina de Londres", "Navidad de 1843", 1843, "tema", "navidad", edad = 35,
            historia = listOf(
                "Tom copia cartas y lleva cuentas en una oficina helada, de las ocho de la mañana a las ocho de la noche, seis días a la semana. Camina una hora para llegar y otra para volver, entre la niebla y el humo de carbón.",
                "Gana poco, tiene muchos hijos y uno está enfermo. El día de Navidad es el único que tiene libre en todo el año: habrá ganso si alcanza, pudín y ponche caliente, y villancicos en la calle.",
                "Londres es la ciudad más grande del mundo, con casi dos millones de personas. Él sabe leer y escribir: lee por entregas las novelas que todos comentan.",
            ),
            dia = listOf(t(22.5, 6.0, Hacer.DORMIR), t(6.0, 7.0, Hacer.COMER), t(7.0, 8.0, Hacer.CAMINO), t(8.0, 13.0, Hacer.TRABAJO), t(13.0, 13.5, Hacer.COMER), t(13.5, 20.0, Hacer.TRABAJO),
                t(20.0, 21.0, Hacer.CAMINO), t(21.0, 22.5, Hacer.CUIDAR, "familia")),
            datos = mapOf("pasos" to e(12000), "km_anio" to e(3500), "kcal" to e(2400), "proteina" to e(70), "azucar" to e(25), "fibra" to e(25), "alcohol" to e(25), "sueno" to e(7.0), "dormir" to e(22.5),
                "trabajo" to e(66), "aire_libre" to e(2), "pantalla" to r(0), "notificaciones" to e(6), "musica" to e(0.5), "personas_dia" to e(100), "conocidas" to e(3000), "poblacion" to m(1950000, "Censo de 1841."),
                "lejos" to e(100), "estatura" to r(166), "vida" to e(37), "vida_adulto" to e(60), "hijos" to e(5), "alfabetizacion" to e(60), "lectura" to e(6)),
            fuentes = listOf("Londres en la época victoriana" to "${W}Victorian_era", "Cuento de Navidad (1843)" to "${W}A_Christmas_Carol"),
            inspiracion = "Inspirado en el Londres de Cuento de Navidad, de Charles Dickens."),

        Vida("obrera_moscu", "Liudmila", "Obrera textil", "Moscú", "1975", 1975, "tema", "urss", edad = 34,
            historia = listOf(
                "Liudmila trabaja en una fábrica de tejidos: cinco días, 41 horas a la semana. Va y viene en el metro de Moscú, con sus estaciones como palacios. Vive con su esposo, su hija y su suegra en un departamento de dos cuartos.",
                "Después del trabajo, las filas: pan, leche, a veces salchichas; puede pasar más de una hora al día formada. En la Unión Soviética se come mucho pan, papa y azúcar, más que en casi cualquier país del mundo.",
                "Lee mucho, como casi todos: los libros son baratos y los buenos se agotan. En el verano, la fábrica le da un viaje a un sanatorio en el mar Negro. Los hombres de su edad viven, en promedio, doce años menos que ella.",
            ),
            dia = listOf(t(23.0, 6.0, Hacer.DORMIR), t(6.0, 7.0, Hacer.CUIDAR), t(7.0, 7.75, Hacer.CAMINO, "metro"), t(7.75, 12.0, Hacer.TRABAJO), t(12.0, 13.0, Hacer.COMER, "comedor"), t(13.0, 16.0, Hacer.TRABAJO),
                t(16.0, 18.0, Hacer.CAMINO, "filas"), t(18.0, 20.0, Hacer.CUIDAR), t(20.0, 23.0, Hacer.LIBRE, "televisión y libros")),
            datos = mapOf("pasos" to e(9000), "km_anio" to e(6000), "kcal" to e(2800), "proteina" to e(90), "azucar" to e(100, "Unos 40 kg de azúcar por persona al año."), "fibra" to e(30),
                "alcohol" to e(12, "Los hombres, mucho más: el país bebía más de 10 litros de alcohol puro por persona."), "sueno" to e(7.0), "dormir" to e(23.0), "trabajo" to m(41), "aire_libre" to e(2),
                "pantalla" to e(1.5, "La televisión de la tarde."), "notificaciones" to r(0), "musica" to e(1), "personas_dia" to e(100), "conocidas" to e(3000), "poblacion" to m(7600000), "lejos" to e(1500),
                "estatura" to r(160), "vida" to m(74, "Mujeres; los hombres, 62."), "vida_adulto" to e(75), "hijos" to e(1.5), "alfabetizacion" to m(99), "lectura" to e(10)),
            fuentes = listOf("La crisis de salud en la URSS (1981)" to "https://www.nybooks.com/articles/1981/02/19/the-health-crisis-in-the-ussr/", "Moscú en los años setenta" to "${W}Moscow")),

        Vida("astronauta", "Una astronauta", "Ingeniera de vuelo", "Una nave rumbo a Alfa Centauri", "año 15 del viaje", 2172, "tema", "espacio", edad = 45, ficticia = true,
            inspiracion = "Inventada para el tema Espacio: un viaje solo de ida a la estrella más cercana. La distancia, la velocidad y lo que tardan la luz y los mensajes son de la física de verdad; la nave, su gente y su vida, no.",
            historia = listOf(
                "Va en una nave que salió de la órbita de la Tierra hace quince años rumbo a Alfa Centauri, el sistema de estrellas más cercano al Sol, a 4.37 años luz. Viaja a una décima parte de la velocidad de la luz, unos 30,000 kilómetros cada segundo, y aun así el viaje dura más de cuarenta años. Es solo de ida: si llega, tendrá más de setenta.",
                "Es ingeniera de vuelo: cuida el reactor, los motores, el escudo que va delante y el aire y el agua, que se reciclan una y otra vez. La nave gira para que en su anillo haya algo de peso; en el centro, sin peso, están los talleres. Come lo que da la granja de a bordo: trigo enano, papas, soya, algas y algún huevo.",
                "Vive con unas ciento sesenta personas y no conocerá a nadie más en su vida, salvo los niños que nazcan a bordo. Los mensajes de la Tierra tardan casi dos años en llegar, y cada año un poco más. Desde la ventana, el Sol es solo la estrella más brillante del cielo; dentro de unos años, la más brillante será la de delante.",
            ),
            dia = listOf(t(22.5, 6.0, Hacer.DORMIR, "en su litera del anillo"), t(6.0, 7.0, Hacer.CUIDAR, "aseo y revisión médica"), t(7.0, 12.0, Hacer.TRABAJO, "sala de máquinas"), t(12.0, 13.0, Hacer.COMER),
                t(13.0, 14.5, Hacer.LIBRE, "ejercicio en el anillo"), t(14.5, 18.0, Hacer.TRABAJO), t(18.0, 19.0, Hacer.COMER, "comedor común"), t(19.0, 21.0, Hacer.GENTE, "la sala común"),
                t(21.0, 22.5, Hacer.LIBRE, "la ventana")),
            datos = mapOf("pasos" to i(7000, "Los pasillos y las escaleras del anillo, y la cinta del ejercicio."), "km_anio" to i(946000000000, "Una décima parte de la velocidad de la luz: unos 30,000 km cada segundo."),
                "kcal" to i(2300), "proteina" to i(70, "Soya, huevos y algas."), "azucar" to i(10, "Lo poco que da la remolacha de la granja."), "fibra" to i(40, "Casi todo lo que come sale de la granja."),
                "alcohol" to i(3, "Un vino de la granja en las fiestas."), "sueno" to i(7.0), "dormir" to i(22.5), "trabajo" to i(45), "aire_libre" to i(0, "No hay afuera."), "pantalla" to i(8),
                "notificaciones" to i(50, "Alarmas y avisos de los sistemas de la nave."), "musica" to i(1.5), "personas_dia" to i(35), "conocidas" to i(160, "Todos los de a bordo, y nadie más."),
                "poblacion" to i(160), "lejos" to i(14000000000000, "Año y medio luz desde la Tierra, y cada día más."), "estatura" to i(172), "vida" to i(85), "vida_adulto" to i(86),
                "hijos" to i(0.8, "Los nacimientos se planean: el aire y la comida alcanzan para un número de personas."), "alfabetizacion" to i(100), "lectura" to i(15, "La biblioteca de a bordo, con todos los libros de la Tierra.")),
            fuentes = listOf("Alfa Centauri" to "${W}Alpha_Centauri", "Viajar a otra estrella" to "${W}Interstellar_travel", "La nave de varias generaciones" to "${W}Generation_ship")),

        Vida("operadora", "Carmen", "Operadora de computadora central, turno de noche", "El centro de cómputo de un banco, Ciudad de México", "1978", 1978, "tema", "ciber", edad = 27,
            historia = listOf(
                "Carmen entra a las diez de la noche a un cuarto frío, con piso falso y aire acondicionado todo el año, donde zumba la computadora central del banco. Su trabajo es que la máquina no se detenga: montar y desmontar cintas magnéticas, cargar los paquetes de tarjetas, cambiar el papel continuo de la impresora y vigilar la consola.",
                "De noche corren los procesos largos: los saldos de todas las cuentas, la nómina, los estados de cuenta que saldrán por la mañana. Cuando algo falla, la consola escribe un mensaje en mayúsculas y hay que buscar en el manual qué significa y qué cinta pide.",
                "Sale a las seis, cuando la ciudad empieza a moverse, y duerme de día con las cortinas cerradas. Sus amigas del turno de día casi no la ven; a cambio, entre cinta y cinta, lee novelas enteras.",
            ),
            dia = listOf(t(8.0, 15.0, Hacer.DORMIR), t(15.0, 17.0, Hacer.CUIDAR, "la casa y el mandado"), t(17.0, 18.0, Hacer.LIBRE), t(18.0, 21.5, Hacer.GENTE, "comida con la familia"),
                t(21.5, 22.0, Hacer.CAMINO, "en camión"), t(22.0, 6.0, Hacer.TRABAJO, "cargar cintas, vigilar la consola, imprimir listados"), t(6.0, 6.5, Hacer.COMER, "un café y una torta"),
                t(6.5, 8.0, Hacer.CAMINO, "el tráfico de la mañana")),
            datos = mapOf("pasos" to e(6000), "km_anio" to e(9000, "Ida y vuelta en camión, todos los días."), "kcal" to e(2000), "proteina" to e(65), "azucar" to e(60, "Refrescos para aguantar la noche."),
                "fibra" to e(25), "alcohol" to e(3), "sueno" to e(6.5, "De día, con ruido de la calle."), "dormir" to e(8.0), "trabajo" to e(48, "Seis turnos de ocho horas."), "aire_libre" to e(1.5),
                "pantalla" to e(7, "La consola de la computadora, toda la noche."), "notificaciones" to e(40, "Mensajes de la consola y el timbre de la impresora."), "musica" to e(1.5, "La radio de la madrugada."),
                "personas_dia" to e(15), "conocidas" to e(1500), "poblacion" to r(13000000, "La zona metropolitana, hacia 1980."), "lejos" to e(400), "estatura" to e(155),
                "vida" to r(65), "vida_adulto" to r(72), "hijos" to r(5.0), "alfabetizacion" to r(83), "lectura" to e(20, "Entre cinta y cinta.")),
            fuentes = listOf("Operador de computadoras (Wikipedia)" to "${W}Computer_operator", "IBM System/370 (Wikipedia)" to "${W}IBM_System/370",
                "Cintas magnéticas de datos (Wikipedia)" to "${W}Magnetic-tape_data_storage", "La población de México (Wikipedia)" to "${W}Demographics_of_Mexico")),
        Vida("buzo_esponjas", "Nikos", "Buzo esponjero de escafandra", "Un barco esponjero frente a la costa de Florida", "1910", 1910, "tema", "abismo", edad = 26,
            historia = listOf(
                "Nikos llegó de una isla del Dodecaneso con los buzos griegos que, desde 1905, sacan esponjas del golfo de México con escafandra: un casco de cobre atornillado a un traje de lona, botas de plomo y una manguera por la que dos hombres, a mano, le bombean aire desde cubierta. Baja una y otra vez durante la mañana; arriba, su ayudante sostiene la cuerda y entiende sus tirones.",
                "Cada buzo sabe de alguien a quien el fondo dejó paralítico o mató al subir: es la enfermedad de los buzos, que entonces nadie sabía evitar bien. Por la tarde, las esponjas se limpian en cubierta, se pisan para quitarles la piel negra, se enjuagan y se ensartan en cuerdas para secarlas al sol.",
                "Pasa meses en el mar con la misma decena de hombres. Come pan duro que se ablanda en agua, aceitunas, legumbres y el pescado que caiga; duerme en cubierta, bajo las estrellas, y antes de dormir se cantan las canciones de la isla.",
            ),
            dia = listOf(t(20.0, 4.5, Hacer.DORMIR, "en cubierta"), t(4.5, 5.0, Hacer.COMER), t(5.0, 11.0, Hacer.TRABAJO, "inmersiones con casco de cobre"), t(11.0, 12.5, Hacer.COMER),
                t(12.5, 17.0, Hacer.TRABAJO, "limpiar y ensartar esponjas"), t(17.0, 18.0, Hacer.COMER), t(18.0, 20.0, Hacer.GENTE, "canciones de la isla")),
            datos = mapOf("pasos" to r(3000, "Entre la cubierta y el fondo, con botas de plomo."), "km_anio" to r(4000, "Ir y volver de los bancos de esponjas del golfo."), "kcal" to r(3500, "Trabajo pesado en agua fría."),
                "proteina" to r(100), "azucar" to r(15), "fibra" to r(30, "Pan duro, legumbres y aceitunas."), "alcohol" to r(20, "Algo de vino o de licor de anís por la noche."),
                "sueno" to r(8.5), "dormir" to r(20.0), "trabajo" to r(70, "Seis días a la semana en la temporada."), "aire_libre" to r(13, "Casi todo el día en cubierta o bajo el agua."), "pantalla" to e(0),
                "notificaciones" to r(20, "Los tirones de la cuerda de señales."), "musica" to r(2, "Canciones de la isla."), "personas_dia" to r(10, "La tripulación del barco."), "conocidas" to r(400),
                "poblacion" to e(2212, "Tarpon Springs, el puerto de los esponjeros griegos, en el censo de 1910."), "lejos" to r(9600, "Desde su isla del Egeo hasta Florida."), "estatura" to r(167),
                "vida" to r(40), "vida_adulto" to r(60, "Menos para los buzos: la enfermedad de la descompresión mataba o paralizaba a muchos."), "hijos" to r(5),
                "alfabetizacion" to r(40), "lectura" to r(0)),
            fuentes = listOf("La historia de los buzos de esponjas (Divers Alert Network)" to "https://dan.org/alert-diver/article/the-story-of-sponge-divers/",
                "Tarpon Springs" to "${W}Tarpon_Springs,_Florida", "Escafandra" to "${W}Standard_diving_dress")),

        // ------------------------------------------------ mundos de ficción: estimaciones inventadas, con sentido
        Vida("repartidora", "Kei", "Repartidora en bicicleta eléctrica", "Una megaciudad de neón", "2077", 2077, "tema", "neon", edad = 26, ficticia = true,
            inspiracion = "Un mundo cyberpunk: megaciudades, implantes y corporaciones.",
            historia = listOf(
                "Kei reparte de noche entre torres de cien pisos, guiada por flechas que solo ella ve: un implante en el ojo le pone el mapa, la música y los anuncios encima del mundo. La aplicación le cobra si llega tarde.",
                "Come fideos sintéticos, barras de proteína de tanque y bebidas energéticas; casi nada que haya crecido de la tierra. Duerme de día, con cortinas que no detienen del todo el brillo de la ciudad.",
                "Tiene veinte mil contactos y cuatro amigos. Ve a cientos de personas por noche y no conoce a ninguna. Nunca ha salido de la ciudad: afuera, dicen, no hay nada.",
            ),
            dia = listOf(t(6.0, 12.0, Hacer.DORMIR), t(12.0, 13.0, Hacer.COMER), t(13.0, 16.0, Hacer.LIBRE, "pantallas"), t(16.0, 21.0, Hacer.TRABAJO, "repartos"), t(21.0, 21.5, Hacer.COMER),
                t(21.5, 4.0, Hacer.TRABAJO, "turno de noche"), t(4.0, 6.0, Hacer.GENTE, "bar del barrio")),
            datos = mapOf("pasos" to i(8000), "km_anio" to i(30000), "kcal" to i(2300), "proteina" to i(120), "azucar" to i(120, "Bebidas energéticas."), "fibra" to i(8), "alcohol" to i(20),
                "sueno" to i(5.0), "dormir" to i(30.0), "trabajo" to i(72), "aire_libre" to i(6), "pantalla" to i(16, "El implante nunca se apaga."), "notificaciones" to i(2500, "Anuncios en el ojo."),
                "musica" to i(8), "personas_dia" to i(400), "conocidas" to i(20000), "poblacion" to i(30000000), "lejos" to i(50), "estatura" to i(166), "vida" to i(65), "vida_adulto" to i(70),
                "hijos" to i(0.9), "alfabetizacion" to i(99), "lectura" to i(0)),
            fuentes = emptyList()),

        Vida("fideos", "Amaru", "Vendedor de fideos en un puesto de callejón", "Los niveles bajos de una megaciudad", "2077", 2077, "tema", "neon", edad = 47, ficticia = true,
            inspiracion = "Los puestos de comida callejera que abren de noche bajo los letreros, llevados a una megaciudad del futuro.",
            historia = listOf(
                "Amaru abre su puesto cuando se encienden los letreros, en un callejón del nivel −3 donde nunca da el sol: seis bancos de plástico, una olla de caldo que lleva hirviendo desde la tarde y un letrero magenta con un tazón que parpadea.",
                "Por la barra le pasan cientos de caras cada noche: repartidores, guardias que salen de turno, gente que no puede dormir. Conoce el pedido de muchos y el nombre de pocos. Su mejor amigo es el de la farmacia de guardia de enfrente.",
                "Duerme de día, en el cuarto de arriba del puesto, con el zumbido del transformador del letrero metido en el sueño. Hace años que no sube a la superficie; dice que allá arriba la comida no sabe a nada.",
            ),
            dia = listOf(t(10.0, 17.0, Hacer.DORMIR), t(17.0, 18.0, Hacer.CUIDAR, "compra en el mercado flotante"), t(18.0, 4.0, Hacer.TRABAJO, "el puesto, entre vapor y letreros"),
                t(4.0, 5.0, Hacer.COMER, "con los repartidores"), t(5.0, 10.0, Hacer.LIBRE)),
            datos = mapOf("pasos" to i(4000, "Tres pasos entre la olla y la barra, miles de veces."), "km_anio" to i(300), "kcal" to i(2600), "proteina" to i(90), "azucar" to i(40), "fibra" to i(12),
                "alcohol" to i(25, "Un trago con los últimos clientes."), "sueno" to i(6.0), "dormir" to i(10.0), "trabajo" to i(70), "aire_libre" to i(0.5, "Si el callejón cuenta como aire libre."),
                "pantalla" to i(3), "notificaciones" to i(600, "Pedidos, pagos y los letreros del callejón."), "musica" to i(10, "La radio del puesto, toda la noche."), "personas_dia" to i(350),
                "conocidas" to i(15000), "poblacion" to i(30000000), "lejos" to i(5), "estatura" to i(168), "vida" to i(70), "vida_adulto" to i(74), "hijos" to i(0.9),
                "alfabetizacion" to i(99), "lectura" to i(1)),
            fuentes = emptyList()),

        Vida("funcionario", "Un funcionario", "Empleado de un ministerio", "La capital de un Estado que todo lo vigila", "1984", 1984, "tema", "distopia", edad = 39, ficticia = true,
            inspiracion = "Inspirado en el mundo de 1984, de George Orwell.",
            historia = listOf(
                "A las siete y cuarto, la pantalla de su cuarto lo despierta para la gimnasia obligatoria, y lo mira mientras la hace. No se puede apagar: nunca. Trabaja en un ministerio corrigiendo periódicos viejos para que el pasado diga lo que hoy conviene.",
                "Come guisado gris en el comedor, pan oscuro, café que no es café y una ginebra que quema; la ración de chocolate, le anuncian como un logro, vuelve a bajar. Las calles están llenas de carteles con un rostro que lo sigue con la mirada.",
                "Conoce a cientos de personas y no confía en ninguna. Cada año, el diccionario oficial tiene menos palabras. Escribir un diario, como este, sería un crimen.",
            ),
            dia = listOf(t(23.5, 7.0, Hacer.DORMIR), t(7.0, 7.5, Hacer.TRABAJO, "gimnasia frente a la pantalla"), t(7.5, 8.5, Hacer.CAMINO), t(8.5, 12.5, Hacer.TRABAJO, "ministerio"), t(12.5, 13.0, Hacer.COMER, "comedor"),
                t(13.0, 18.0, Hacer.TRABAJO), t(18.0, 20.0, Hacer.GENTE, "actividades obligatorias"), t(20.0, 23.5, Hacer.LIBRE, "vigilado")),
            datos = mapOf("pasos" to i(6000), "km_anio" to i(1500), "kcal" to i(1700, "Racionamiento."), "proteina" to i(50), "azucar" to i(0, "Sacarina."), "fibra" to i(15), "alcohol" to i(40, "Ginebra barata, todos los días."),
                "sueno" to i(6.0), "dormir" to i(23.5), "trabajo" to i(60, "Noventa en las semanas de campaña."), "aire_libre" to i(0.5), "pantalla" to i(17, "La que no se apaga."),
                "notificaciones" to i(200, "Boletines de victorias y cifras de producción."), "musica" to i(2, "Marchas."), "personas_dia" to i(60), "conocidas" to i(500), "poblacion" to i(8000000),
                "lejos" to i(30), "estatura" to i(165), "vida" to i(60), "vida_adulto" to i(62), "hijos" to i(1.5), "alfabetizacion" to i(100), "lectura" to i(0, "Los libros se reescriben.")),
            fuentes = emptyList()),

        Vida("residente", "Una residente", "Técnica de purificación de agua", "Un refugio bajo tierra", "2105", 2105, "tema", "refugio", edad = 22, ficticia = true,
            inspiracion = "Inspirado en los refugios antiatómicos de la ficción retrofuturista.",
            historia = listOf(
                "Nació en el refugio y nunca ha visto el cielo. Las luces bajan a las diez de la noche y suben a las seis de la mañana; el día lo marca un altavoz que siempre suena optimista.",
                "Le asignaron su trabajo a los dieciséis, después de una prueba de aptitudes: mantener las bombas de agua. Come raciones de los cultivos hidropónicos y, los domingos, algo de una lata de antes de la guerra.",
                "Todas las personas que conocerá en su vida caben en el comedor: unas quinientas. Escucha los mismos discos de los años cincuenta una y otra vez. Hay una puerta enorme que nadie abre.",
            ),
            dia = listOf(t(22.0, 6.0, Hacer.DORMIR), t(6.0, 7.0, Hacer.COMER, "comedor"), t(7.0, 12.0, Hacer.TRABAJO, "bombas de agua"), t(12.0, 13.0, Hacer.COMER), t(13.0, 17.0, Hacer.TRABAJO),
                t(17.0, 19.0, Hacer.LIBRE, "terminal y discos"), t(19.0, 22.0, Hacer.GENTE)),
            datos = mapOf("pasos" to i(5000), "km_anio" to i(800), "kcal" to i(2000), "proteina" to i(60), "azucar" to i(10), "fibra" to i(25), "alcohol" to i(5), "sueno" to i(8.0), "dormir" to i(22.0),
                "trabajo" to i(40), "aire_libre" to i(0), "pantalla" to i(6), "notificaciones" to i(40, "Anuncios del supervisor."), "musica" to i(3), "personas_dia" to i(80), "conocidas" to i(500),
                "poblacion" to i(500), "lejos" to i(1), "estatura" to i(163), "vida" to i(72), "vida_adulto" to i(74), "hijos" to i(2, "Con permiso del supervisor."), "alfabetizacion" to i(100), "lectura" to i(12)),
            fuentes = emptyList()),

        Vida("escriba", "Una escriba", "Escriba de una orden de caballeros", "Una ciudadela en el páramo", "2287", 2287, "tema", "orden", edad = 28, ficticia = true,
            inspiracion = "Inspirado en las órdenes de caballeros que guardan la tecnología en la ficción posapocalíptica.",
            historia = listOf(
                "Su orden recorre el páramo buscando la tecnología de antes de la guerra para guardarla, estudiarla y que nadie más la use mal. Ella no pelea en primera línea: cataloga, repara y descifra manuales.",
                "Patrulla con los caballeros, bajo armaduras que pesan más que una persona, y come raciones de campaña y lo que se caza. Las guardias de noche se rotan; casi nunca duerme seis horas seguidas.",
                "Dentro de la orden todos leen; afuera, casi nadie. Ha cruzado el continente en la nave de la orden, más lejos que cualquiera de su pueblo natal.",
            ),
            dia = listOf(t(23.0, 5.0, Hacer.DORMIR, "con guardias"), t(5.0, 6.0, Hacer.COMER), t(6.0, 12.0, Hacer.TRABAJO, "patrulla"), t(12.0, 13.0, Hacer.COMER), t(13.0, 19.0, Hacer.TRABAJO, "taller y archivo"),
                t(19.0, 20.0, Hacer.COMER), t(20.0, 23.0, Hacer.REZAR, "el código de la orden")),
            datos = mapOf("pasos" to i(14000), "km_anio" to i(4000), "kcal" to i(3800), "proteina" to i(140), "azucar" to i(20), "fibra" to i(15), "alcohol" to i(10), "sueno" to i(6.0), "dormir" to i(23.0),
                "trabajo" to i(70), "aire_libre" to i(6), "pantalla" to i(4, "Terminales."), "notificaciones" to i(20, "Radio."), "musica" to i(0.2), "personas_dia" to i(50), "conocidas" to i(800),
                "poblacion" to i(300), "lejos" to i(3000), "estatura" to i(175), "vida" to i(45), "vida_adulto" to i(55), "hijos" to i(1), "alfabetizacion" to i(90, "Dentro de la orden; afuera, uno de cada cinco."),
                "lectura" to i(15, "Manuales técnicos.")),
            fuentes = emptyList()),

        Vida("fogonero", "Albert", "Fogonero de locomotora", "Una línea del norte de Inglaterra", "1880", 1880, "tema", "vapor", edad = 24,
            historia = listOf(
                "Albert empezó de muchacho limpiando máquinas en el depósito, y hace unos años lo subieron a la plataforma. Su oficio es el fuego: echar carbón al hogar en paladas cortas y bien repartidas, vigilar el agua de la caldera y tener la presión que le pide el maquinista antes de cada cuesta.",
                "La plataforma casi no tiene techo: de frente le pega el calor del hogar abierto y de espaldas el viento y la lluvia del norte. En un turno recorre cientos de kilómetros sin dar casi un paso, de pie, girando con la pala entre el ténder y la puerta del fuego. Los turnos de doce horas o más son normales; hasta 1893 ninguna ley se mete con las horas de los ferroviarios, aunque las tripulaciones cansadas causan accidentes.",
                "Para aguantar come como pocos: pan, queso, tocino que fríe sobre la pala limpia, papas y té muy dulce en una lata. Al salir, una o dos pintas en la taberna con los del depósito, que son casi toda su gente. Sabe leer y firmar, y su meta es cambiar de lado en la plataforma: dejar la pala y tomar el regulador, ser maquinista.",
            ),
            dia = listOf(t(22.0, 4.5, Hacer.DORMIR), t(4.5, 5.0, Hacer.COMER), t(5.0, 5.5, Hacer.CAMINO), t(5.5, 17.5, Hacer.TRABAJO, "palear carbón, turno de 12 h"), t(17.5, 18.0, Hacer.CAMINO),
                t(18.0, 19.0, Hacer.COMER), t(19.0, 22.0, Hacer.GENTE, "la taberna")),
            datos = mapOf("pasos" to e(6000, "Casi todo el día de pie en la plataforma: pocos pasos y mucha pala."), "km_anio" to e(60000, "Unos 200 km por turno, seis días a la semana, siempre sobre la máquina."),
                "kcal" to e(4500, "Lo más alto que se calcula para los obreros de su época; palear carbón doce horas es de los trabajos más pesados."), "proteina" to e(110),
                "azucar" to e(70, "Hacia 1880 Gran Bretaña consumía unos 26 kg de azúcar por persona al año, buena parte en el té."), "fibra" to e(25), "alcohol" to e(30, "Una o dos pintas de cerveza floja al día."),
                "sueno" to e(6.5), "dormir" to e(22.0), "trabajo" to e(72, "Doce horas, seis días a la semana."), "aire_libre" to e(12, "La plataforma va casi al descubierto."), "pantalla" to e(0),
                "notificaciones" to e(60, "Silbatos, señales de la vía y las órdenes del maquinista."), "musica" to e(0.5, "Canciones en la taberna."), "personas_dia" to e(40), "conocidas" to e(1500),
                "poblacion" to e(30000, "Un pueblo ferroviario del norte, con su depósito de máquinas."), "lejos" to e(250, "El otro extremo de la línea."), "estatura" to e(166),
                "vida" to e(43, "En Inglaterra y Gales eran unos 40 años al nacer en 1841, y algo más hacia 1880."), "vida_adulto" to e(62), "hijos" to e(4.8),
                "alfabetizacion" to e(86, "Hombres que firmaron su acta de matrimonio hacia 1880."), "lectura" to e(1)),
            fuentes = listOf("Fogonero de máquina de vapor" to "${W}Fireman_(steam_engine)",
                "Clayton y Rowbotham (2008): la dieta de los obreros victorianos" to "https://doi.org/10.1258/jrsm.2008.080112",
                "Las horas de los ferroviarios y la ley de 1893 (Railway Work, Life & Death)" to "https://www.railwayaccidents.port.ac.uk/working-9-5-not-in-1915-long-hours-1")),

        Vida("cientifica", "La doctora", "Científica del instituto subterráneo", "Un laboratorio bajo una ciudad en ruinas", "2287", 2287, "tema", "laboratorio", edad = 41, ficticia = true,
            inspiracion = "Inspirado en el instituto científico escondido bajo tierra de la ficción posapocalíptica.",
            historia = listOf(
                "No ha visto el sol en veinte años. Su mundo son pasillos blancos, jardines hidropónicos bajo luces que imitan el día y elevadores silenciosos. Para salir no camina: se transporta en un destello.",
                "Come la dieta más perfecta de esta lista, calculada al gramo: verduras de cultivo, proteína exacta, cero azúcar. Duerme sus ocho horas. Trabaja sesenta a la semana porque su investigación es lo único que le importa.",
                "Todas las personas que conoce viven bajo esa misma ciudad, unas trescientas; las demás le han enseñado a temerlas. Le ayudan asistentes que parecen humanos y que nadie cuenta como personas.",
            ),
            dia = listOf(t(22.5, 6.5, Hacer.DORMIR), t(6.5, 7.5, Hacer.COMER), t(7.5, 12.5, Hacer.TRABAJO, "laboratorio"), t(12.5, 13.5, Hacer.COMER), t(13.5, 19.5, Hacer.TRABAJO),
                t(19.5, 21.0, Hacer.GENTE, "cena del consejo"), t(21.0, 22.5, Hacer.LIBRE, "música clásica")),
            datos = mapOf("pasos" to i(3500), "km_anio" to i(500), "kcal" to i(2100), "proteina" to i(90), "azucar" to i(5), "fibra" to i(35), "alcohol" to i(5), "sueno" to i(8.0), "dormir" to i(22.5),
                "trabajo" to i(60), "aire_libre" to i(0), "pantalla" to i(11), "notificaciones" to i(50, "Memorandos de la dirección."), "musica" to i(1), "personas_dia" to i(40), "conocidas" to i(300),
                "poblacion" to i(300), "lejos" to i(2), "estatura" to i(168), "vida" to i(90), "vida_adulto" to i(92), "hijos" to i(1, "Con autorización."), "alfabetizacion" to i(100), "lectura" to i(40)),
            fuentes = emptyList()),

        Vida("colona", "Ruth", "Colona", "Un pueblo que se reconstruye", "2288", 2288, "tema", "colonos", edad = 31, ficticia = true,
            inspiracion = "Inspirado en los pueblos que se reconstruyen en la ficción posapocalíptica.",
            historia = listOf(
                "Ruth y otras veinte personas levantaron muros con tablas, láminas y lo que encontraron. Siembran maíz y calabazas, purifican agua, montan guardia. Cuando la radio dice que otro pueblo necesita ayuda, alguien tiene que ir.",
                "Come lo que da la tierra, caza, y de vez en cuando una lata o una caja de cereal de antes de la guerra, todavía dulcísimo. De noche solo hay luz donde llega el generador.",
                "Sabe leer porque su madre le enseñó con revistas viejas. La estación de radio pone canciones de hace dos siglos; a todos les gustan.",
            ),
            dia = listOf(t(21.0, 5.0, Hacer.DORMIR), t(5.0, 6.0, Hacer.COMER), t(6.0, 12.0, Hacer.TRABAJO, "campo y muros"), t(12.0, 13.0, Hacer.COMER), t(13.0, 18.0, Hacer.TRABAJO, "guardia y reparaciones"),
                t(18.0, 21.0, Hacer.GENTE, "fogata y radio")),
            datos = mapOf("pasos" to i(16000), "km_anio" to i(5000), "kcal" to i(2400), "proteina" to i(70), "azucar" to i(20, "Cereal de antes de la guerra."), "fibra" to i(35), "alcohol" to i(15, "Destilado casero."),
                "sueno" to i(6.5), "dormir" to i(21.0), "trabajo" to i(70), "aire_libre" to i(11), "pantalla" to i(0.5), "notificaciones" to i(3, "Llamadas de auxilio por radio."), "musica" to i(2),
                "personas_dia" to i(12), "conocidas" to i(200), "poblacion" to i(25), "lejos" to i(150), "estatura" to i(165), "vida" to i(45), "vida_adulto" to i(55), "hijos" to i(3),
                "alfabetizacion" to i(30), "lectura" to i(2)),
            fuentes = emptyList()),
        Vida("guia_caminos", "Brenna", "Guía de caminos", "Una posada al pie de un paso de montaña", "año 312 de los Caminos", 312, "tema", "antiguas", edad = 29, ficticia = true,
            inspiracion = "Inventada para Tierras Antiguas: un mundo de caminos, minas y compañías de viaje, sin nombres de ninguna obra.",
            historia = listOf(
                "Brenna lleva compañías de viajeros por el paso: mercaderes, mineros que suben a las vetas de hierro, peregrinos que van a las ruinas del otro lado. Conoce cada piedra marcada con las runas del camino y cada refugio donde se puede pasar la noche.",
                "Camina casi todo el día con su bastón y su linterna de mina. Come pan de centeno, queso duro, cecina y lo que se recoge en el monte; en la posada, guiso y cerveza oscura. Dibuja mapas a mano en el libro de crónicas de la compañía.",
                "Sabe leer las runas y un poco de letra; casi nadie en la aldea sabe. De noche, en la posada, alguien toca el arpa y los viajeros cuentan lo que vieron del otro lado de las montañas.",
            ),
            dia = listOf(t(22.0, 5.0, Hacer.DORMIR), t(5.0, 6.0, Hacer.COMER), t(6.0, 12.0, Hacer.CAMINO, "guiar a la compañía"), t(12.0, 13.0, Hacer.COMER, "pan, queso y cecina"),
                t(13.0, 18.0, Hacer.CAMINO, "el paso y las marcas del camino"), t(18.0, 19.0, Hacer.CUIDAR, "mapas y monturas"), t(19.0, 22.0, Hacer.GENTE, "la posada, el arpa y las historias")),
            datos = mapOf("pasos" to i(22000), "km_anio" to i(6000), "kcal" to i(3000), "proteina" to i(90), "azucar" to i(15, "Miel de monte."), "fibra" to i(40), "alcohol" to i(25, "Cerveza oscura de la posada."),
                "sueno" to i(7.0), "dormir" to i(22.0), "trabajo" to i(60), "aire_libre" to i(11), "pantalla" to i(0), "notificaciones" to i(4, "Los cuernos del paso y la campana de la posada."), "musica" to i(1.5, "El arpa de la posada."),
                "personas_dia" to i(20), "conocidas" to i(600), "poblacion" to i(150, "La aldea del paso."), "lejos" to i(900), "estatura" to i(168), "vida" to i(40), "vida_adulto" to i(60), "hijos" to i(2),
                "alfabetizacion" to i(15, "Las runas del camino las leen más; la letra, pocos."), "lectura" to i(1, "Mapas y el libro de crónicas.")),
            fuentes = emptyList()),
        Vida("pintora_cueva", "Ama", "Pintora de cuevas", "Un valle del suroeste de Europa", "hace unos 17,000 años", -15000, "tema", "cueva", edad = 26,
            historia = listOf(
                "Ama vive al final de la última glaciación, en un valle de caliza donde el invierno dura casi medio año. Su grupo, de unas veinticinco personas, pasa el frío bajo un abrigo de roca, junto al río, y se mueve con las estaciones detrás de los renos y los caballos; en otoño, cuando bajan las manadas, se caza más que nunca.",
                "Come sobre todo carne y grasa: reno, caballo, salmón del río, médula de los huesos; en verano, bayas y raíces. Cose su ropa de piel con agujas de hueso. Por las tardes muele ocre y carbón en una piedra plana y lo mezcla con agua o grasa para hacer pintura.",
                "Al caer el día entra con otros a lo hondo de la cueva, con lámparas de piedra que queman grasa, y pinta bisontes, caballos y puntos en la pared. Apoya la mano en la roca y sopla el ocre alrededor: queda su silueta. Medir esas manos sugiere que muchas las hicieron mujeres. Ama es inventada; su mundo, en lo que se sabe, no.",
            ),
            dia = listOf(t(20.0, 6.0, Hacer.DORMIR, "junto al fuego"), t(6.0, 7.0, Hacer.COMER), t(7.0, 12.0, Hacer.TRABAJO, "cazar y recolectar"), t(12.0, 14.0, Hacer.LIBRE),
                t(14.0, 16.0, Hacer.TRABAJO, "moler ocre y carbón"), t(16.0, 18.0, Hacer.REZAR, "pintar en lo hondo de la cueva, con lámparas de grasa"), t(18.0, 20.0, Hacer.GENTE)),
            datos = mapOf(
                "pasos" to r(14000, "Seguir manadas por la estepa fría; nadie midió pasos en el Paleolítico."), "km_anio" to r(4000, "Varios campamentos al año, siguiendo las estaciones."),
                "kcal" to r(2700, "El frío de la glaciación sube el gasto del cuerpo."), "proteina" to r(130, "Carne de reno y caballo, y pescado del río."), "azucar" to r(5, "Bayas en verano; casi nada el resto del año."),
                "fibra" to r(25, "Pocas plantas comestibles en la estepa helada."), "alcohol" to r(0), "sueno" to r(8.0, "Noches largas de invierno junto al fuego."), "dormir" to r(20.0),
                "trabajo" to r(40, "Cazar, recolectar, curtir pieles, coser y moler pigmentos."), "aire_libre" to r(8), "pantalla" to r(0), "notificaciones" to r(0),
                "musica" to r(1, "Del Paleolítico superior quedan flautas de hueso; no se sabe cuánto se tocaba."), "personas_dia" to r(25), "conocidas" to r(400, "Su grupo y los que se juntan en las cacerías grandes."),
                "poblacion" to r(25, "El grupo que comparte el abrigo de roca."), "lejos" to r(250, "Hay conchas de la costa en sitios del interior: viajaban o se intercambiaban."),
                "estatura" to r(154, "Las mujeres del Paleolítico superior tardío medían en promedio unos 153.5 cm (Formicola y Holt, 2007)."),
                "vida" to r(30, "Muchos niños morían pequeños."), "vida_adulto" to r(55), "hijos" to r(4.5), "alfabetizacion" to r(0), "lectura" to r(0),
            ),
            fuentes = listOf("Snow (2013), las manos de las cuevas, casi todas de mujeres" to "https://doi.org/10.7183/0002-7316.78.4.746",
                "Formicola y Holt (2007), la estatura en el Paleolítico superior" to "https://works.bepress.com/brigitte_holt/1",
                "Lascaux" to "${W}Lascaux", "Magdaleniense" to "${W}Magdalenian")),
        Vida("archivista", "El archivista", "Archivista de un pueblo costero", "Un pueblo de pescadores en la niebla", "1926", 1926, "tema", "arcano", edad = 44, ficticia = true,
            inspiracion = "Inventada para Horror cósmico: un archivo de pueblo, una costa con faro y algo que no se nombra, sin personajes, lugares ni criaturas de ninguna obra.",
            historia = listOf(
                "Llegó al pueblo para poner en orden el archivo municipal: cajas de actas, registros de bautizos y de naufragios, cartas que nadie reclamó. Pasa el día entre legajos húmedos, copiando a mano lo que el salitre está a punto de borrar, y anota en su diario los nombres que se repiten donde no deberían.",
                "Come poco y a deshoras: pescado salado, pan duro, sopa de la posada y mucho café. Casi no habla con nadie; los pescadores lo saludan de lejos y cambian de acera. Al caer la tarde camina por el muelle hasta donde se ve la luz del faro.",
                "De noche lee lo que encuentra en las cajas del fondo, a la luz de una lámpara de aceite, hasta que el cansancio lo vence de madrugada. Duerme mal y poco. En el margen de sus cuadernos hay un mismo signo, dibujado una y otra vez, que no recuerda haber trazado.",
            ),
            dia = listOf(t(3.0, 9.0, Hacer.DORMIR, "mal"), t(9.0, 10.0, Hacer.COMER), t(10.0, 17.0, Hacer.TRABAJO, "catalogar legajos"), t(17.0, 18.0, Hacer.CAMINO, "por el muelle"),
                t(18.0, 19.0, Hacer.COMER), t(19.0, 3.0, Hacer.LIBRE, "lee lo que no debería")),
            datos = mapOf("pasos" to i(5000), "km_anio" to i(1200, "El tren que lo trajo y el muelle cada tarde."), "kcal" to i(1800), "proteina" to i(70, "Pescado salado."), "azucar" to i(25, "En el café."), "fibra" to i(18),
                "alcohol" to i(15, "Una copa de licor para dormir."), "sueno" to i(5.0, "Y mal."), "dormir" to i(3.0), "trabajo" to i(50), "aire_libre" to i(1.5, "La niebla casi no deja ver el sol."), "pantalla" to i(0),
                "notificaciones" to i(3, "La sirena del faro y la campana de la iglesia."), "musica" to i(0.1), "personas_dia" to i(3), "conocidas" to i(150), "poblacion" to i(900),
                "lejos" to i(600, "La ciudad donde estudió."), "estatura" to i(172), "vida" to i(55), "vida_adulto" to i(65), "hijos" to i(0), "alfabetizacion" to i(70, "En el pueblo; él lee latín y letra antigua."),
                "lectura" to i(120, "Legajos, cartas y los libros del fondo del archivo.")),
            fuentes = emptyList()),
    )

    /** Las vidas que se ven con el tema [tema]: las doce de todos y la del tema (primero). */
    fun visibles(tema: String): List<Vida> = DE_TEMAS.filter { it.tema == tema } + BASE

    fun porId(id: String): Vida? = (BASE + DE_TEMAS).firstOrNull { it.id == id }

    /** Escala para comparar: diferencias en logaritmo (1 = el doble) salvo la hora de dormir, en horas. */
    private val SUAVE = mapOf("pasos" to 1000.0, "kcal" to 200.0, "azucar" to 10.0, "fibra" to 5.0, "alcohol" to 5.0, "sueno" to 1.0, "aire_libre" to 0.5, "pantalla" to 0.5,
        "notificaciones" to 5.0, "musica" to 0.3, "personas_dia" to 5.0, "km_anio" to 500.0)

    /** Qué tan distinta es una vida de la tuya (0 = igual), con cuántas cosas en común se comparó. */
    fun distancia(v: Vida, tuyo: Map<String, Double>): Pair<Double, Int>? {
        var s = 0.0; var n = 0
        for (k in PARECIDO) {
            val a = tuyo[k] ?: continue; val b = v.datos[k]?.v ?: continue
            s += if (k == "dormir") kotlin.math.abs(a - b) / 2.0 else kotlin.math.abs(kotlin.math.ln((a + SUAVE[k]!!) / (b + SUAVE[k]!!)))
            n++
        }
        return if (n >= 4) (s / n) to n else null
    }
}
