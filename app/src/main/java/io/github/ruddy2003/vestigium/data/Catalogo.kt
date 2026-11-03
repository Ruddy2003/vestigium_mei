package io.github.ruddy2003.vestigium.data

data class Opcion(val id: String, val nombre: String, val detalle: String = "")
data class TipoMomento(val id: String, val nombre: String, val discreto: String, val pregunta: String)
data class Pregunta(val clave: String, val pregunta: String, val autor: String, val guia: String, val opciones: List<Opcion>)

object Catalogo {
    val CONTEXTOS = listOf(
        Opcion("trabajo", "Trabajo"), Opcion("gym", "Gym"), Opcion("viaje", "Viaje"), Opcion("enfermo", "Enfermo"),
        Opcion("academico", "Académico"), Opcion("ocio_inmersivo", "Ocio inmersivo"),
        Opcion("naturaleza", "Naturaleza"), Opcion("amigos", "Amigos"),
        Opcion("romantico", "Romántico"), Opcion("familia", "Familia"),
    )

    val ANCLAS: Map<String, Map<Int, String>> = mapOf(
        "valencia" to mapOf(
            1 to "Muy desagradable: angustia, desesperanza", 2 to "Desagradable: tristeza o frustración marcada",
            3 to "Algo desagradable: incomodidad leve", 4 to "Neutral: plano", 5 to "Algo agradable: contento",
            6 to "Agradable: disfrute claro", 7 to "Muy agradable: entusiasmo pleno"),
        "activacion" to mapOf(
            1 to "Muy baja: agotado, apagado", 2 to "Baja: cansado, lento", 3 to "Algo baja: relajado, en calma",
            4 to "Neutral", 5 to "Algo alta: alerta, despierto", 6 to "Alta: activo, con impulso", 7 to "Muy alta: acelerado, inquieto"),
        "dolor" to mapOf(
            0 to "Nada", 1 to "Lo noto, no interfiere", 2 to "Molesta, pero no cambia lo que hago",
            3 to "Modifica cómo me muevo", 4 to "Limita qué actividades hago", 5 to "No puedo funcionar normal"),
        "sueno_valencia" to mapOf(
            1 to "Muy desagradable", 2 to "Desagradable", 3 to "Algo desagradable", 4 to "Neutral",
            5 to "Algo agradable", 6 to "Agradable", 7 to "Muy agradable"),
        "sueno_calidad" to mapOf(
            1 to "Muy mala: casi no descansé", 2 to "Mala", 3 to "Regular", 4 to "Buena", 5 to "Muy buena: desperté descansado"),
        "impulso" to mapOf(
            0 to "Ninguno", 1 to "Leve, pasó solo", 2 to "Lo noté varias veces", 3 to "Fuerte, me costó",
            4 to "Muy fuerte, casi todo el día", 5 to "Incontrolable"),
        "libido" to mapOf(
            0 to "Nada", 1 to "Leve", 2 to "Presente", 3 to "Notable", 4 to "Fuerte", 5 to "Dominante"),
        "prediccion_manana" to mapOf(
            1 to "Muy desagradable", 2 to "Desagradable", 3 to "Algo desagradable", 4 to "Neutral",
            5 to "Algo agradable", 6 to "Agradable", 7 to "Muy agradable"),
    )

    val CIENTIFICAS = listOf(
        Pregunta("flow", "¿El reto igualó tu habilidad?", "Csikszentmihalyi",
            "Piensa en la actividad más exigente del día y compara el tamaño del reto con qué tan preparado te sentías.",
            listOf(Opcion("flujo", "Flujo", "Concentración total, perdiste la noción del tiempo"),
                Opcion("ansiedad", "Ansiedad", "El reto te rebasó"),
                Opcion("aburrimiento", "Aburrimiento", "Muy por debajo de tu capacidad"),
                Opcion("apatia", "Apatía", "Ni reto ni interés"),
                Opcion("no_aplica", "No aplica", "No hubo actividad que evaluar así"))),
        Pregunta("sdt_necesidad", "¿Qué necesidad se satisfizo más?", "Deci y Ryan",
            "Piensa en el momento del día en que te sentiste más pleno y en qué lo explicó.",
            listOf(Opcion("autonomia", "Autonomía", "Las decisiones fueron tuyas"),
                Opcion("competencia", "Competencia", "Algo se te dio bien"),
                Opcion("relacion", "Relación", "Te sentiste conectado con alguien"),
                Opcion("ninguna", "Ninguna", "El día no tocó ninguna"),
                Opcion("las_tres", "Las tres", "Las tres a la vez"))),
        Pregunta("apreciacion", "¿Cómo evaluaste la exigencia del día?", "Lazarus",
            "No es cuánta carga tuviste, sino cómo la interpretaste. La misma carga puede sentirse como reto o como amenaza.",
            listOf(Opcion("reto", "Reto", "Exigente, pero te motivó"),
                Opcion("amenaza", "Amenaza", "Exigente, y temiste fallar"),
                Opcion("perdida", "Pérdida", "Algo ya salió mal"),
                Opcion("rutina", "Rutina", "Baja exigencia"))),
    )

    val SUENO_LUCIDEZ = listOf(Opcion("no", "No"), Opcion("parcial", "Parcial"), Opcion("total", "Total"))
    /** Qué tan claro lo recuerdas (los ids de antes: vago, moderado, muy_vivido). */
    val SUENO_VIVIDEZ = listOf(Opcion("vago", "Solo fragmentos"), Opcion("moderado", "Partes claras"), Opcion("muy_vivido", "Casi completo y nítido"))
    /**
     * El «tipo» de antes (hasta el esquema 16). Ya no se pregunta: se partió en [SUENO_RAREZA] y en «¿fue pesadilla?», y
     * «recurrente» quedó en la estructura. Se conserva para leer los días viejos (ver [rarezaDe], [pesadillaDe], [categoriasSuenoDe]).
     */
    val SUENO_TIPO = listOf(Opcion("mundano", "Mundano"), Opcion("ansioso_pesadilla", "Ansioso o pesadilla"),
        Opcion("bizarro_surreal", "Bizarro o surreal"), Opcion("recurrente", "Recurrente"))
    /** Qué tan raro fue el sueño (esquema 17): de lo que podría pasar despierto a lo imposible. */
    val SUENO_RAREZA = listOf(Opcion("realista", "Realista"), Opcion("raro", "Raro pero posible"), Opcion("imposible", "Imposible o surreal"))
    /** La rareza del sueño, o la que se deduce del tipo de antes (mundano = realista, bizarro = imposible). */
    fun rarezaDe(d: Dia): String? = d.sueno_rareza ?: when (d.sueno_tipo) { "mundano" -> "realista"; "bizarro_surreal" -> "imposible"; else -> null }
    /** Si fue pesadilla: lo que marcaste o, en los días de antes, el tipo «ansioso o pesadilla». */
    fun pesadillaDe(d: Dia): Boolean? = d.sueno_pesadilla ?: if (d.sueno_tipo == "ansioso_pesadilla") true else null
    /** Las categorías del sueño, con el «recurrente» del tipo de antes pasado a «se repite». */
    fun categoriasSuenoDe(d: Dia): List<String> =
        if (d.sueno_tipo == "recurrente" && "est_recurrente" !in d.sueno_categorias) d.sueno_categorias + "est_recurrente" else d.sueno_categorias

    val MOMENTOS = listOf(
        TipoMomento("pensando_en", "Pensando en", "A", "¿En qué o en quién?"),
        TipoMomento("preocupacion", "Preocupación", "B", "¿Qué te preocupa?"),
        TipoMomento("tristeza", "Tristeza", "C", "¿Qué la provocó?"),
        TipoMomento("ansiedad", "Ansiedad", "D", "¿Qué la está provocando?"),
        TipoMomento("enojo", "Enojo", "E", "¿Con qué o con quién?"),
        TipoMomento("alegria", "Alegría", "F", "¿Qué pasó?"),
        TipoMomento("gratitud", "Gratitud", "G", "¿Qué agradeces?"),
        TipoMomento("impulso", "Impulso", "H", "¿Qué lo detonó?"),
        TipoMomento("idea", "Idea", "I", "Anótala"),
        TipoMomento("recuerdo", "Recuerdo", "J", "¿Qué recordaste?"),
    )
    val MOMENTOS_WIDGET = listOf("pensando_en", "preocupacion", "tristeza", "ansiedad", "alegria", "gratitud", "impulso", "idea")
    /** Recaída: solo aparece con el seguimiento privado activado y nunca sale de la app (salvo en tu exportación a R). */
    val RECAIDA = TipoMomento("recaida", "Recaída", "K", "¿Qué pasó antes?")
    /** Libido: solo con el seguimiento privado; distinta del impulso. Sin texto, sin logros, rachas ni widget. */
    val LIBIDO = TipoMomento("libido", "Libido", "L", "¿En qué está el foco?")
    val FOCOS = listOf(Opcion("pensando", "Pensando"), Opcion("viendo", "Viendo"), Opcion("conversando", "Conversando"), Opcion("nada", "Nada en particular"), Opcion("otro", "Otro"))
    fun tipo(id: String): TipoMomento? = MOMENTOS.firstOrNull { it.id == id } ?: RECAIDA.takeIf { it.id == id } ?: LIBIDO.takeIf { it.id == id }
    fun nombreTipo(id: String): String = if (id == "muestreo" || id == "animo") "Ánimo" else tipo(id)?.nombre ?: id

    // ------------------------------------------------------------------ ánimo: sentimientos y pensamientos

    /** Un sentimiento: nombre, si suele ser agradable (+1), desagradable (−1) o mixto (0), y su letra en modo discreto. */
    data class Sentimiento(val id: String, val nombre: String, val tono: Int, val discreto: String)

    val SENTIMIENTOS = listOf(
        Sentimiento("alegria", "Alegría", 1, "A"), Sentimiento("calma", "Calma", 1, "B"), Sentimiento("gratitud", "Gratitud", 1, "C"),
        Sentimiento("entusiasmo", "Entusiasmo", 1, "D"), Sentimiento("orgullo", "Orgullo", 1, "E"), Sentimiento("ternura", "Cariño", 1, "F"),
        Sentimiento("alivio", "Alivio", 1, "G"), Sentimiento("diversion", "Diversión", 1, "H"), Sentimiento("esperanza", "Esperanza", 1, "I"),
        Sentimiento("estres", "Estrés", -1, "J"), Sentimiento("ansiedad", "Ansiedad", -1, "K"), Sentimiento("frustracion", "Frustración", -1, "L"),
        Sentimiento("tristeza", "Tristeza", -1, "M"), Sentimiento("enojo", "Enojo", -1, "N"), Sentimiento("miedo", "Miedo", -1, "O"),
        Sentimiento("soledad", "Soledad", -1, "P"), Sentimiento("aburrimiento", "Aburrimiento", -1, "Q"), Sentimiento("culpa", "Culpa", -1, "R"),
        Sentimiento("verguenza", "Vergüenza", -1, "S"), Sentimiento("celos", "Celos", -1, "T"), Sentimiento("abrumado", "Abrumado", -1, "U"),
        Sentimiento("cansancio", "Cansancio", -1, "V"), Sentimiento("nostalgia", "Nostalgia", 0, "W"), Sentimiento("confusion", "Confusión", 0, "X"),
        Sentimiento("sorpresa", "Sorpresa", 0, "Y"),
    )
    /** «No sé» también es un dato: no lo sabías nombrar. «Otro» lleva su nombre escrito. */
    val NO_SE = "no_se"
    val OTRO = "otro"
    fun sentimiento(id: String) = SENTIMIENTOS.firstOrNull { it.id == id }
    fun nombreSentimiento(id: String, discreto: Boolean = false): String = when (id) {
        NO_SE -> if (discreto) "?" else "No sé"; OTRO -> if (discreto) "Ω" else "Otro"
        else -> sentimiento(id)?.let { if (discreto) it.discreto else it.nombre } ?: id
    }

    /** En qué pensabas (opcional). */
    val PENSAMIENTOS = listOf(
        Opcion("pensando_en", "En alguien o algo"), Opcion("preocupacion", "Preocupación"), Opcion("idea", "Idea"), Opcion("recuerdo", "Recuerdo"),
        Opcion("plan", "Plan o pendiente"), Opcion("vueltas", "Dándole vueltas"), Opcion("autocritica", "Autocrítica"), Opcion("deseo", "Deseo o antojo"),
    )

    /** Los tipos de momento de antes, traducidos al registro de ánimo nuevo: qué sentimiento o qué pensamiento eran. */
    private val VIEJO_SENTIMIENTO = setOf("tristeza", "ansiedad", "enojo", "alegria", "gratitud")
    private val VIEJO_PENSAMIENTO = setOf("pensando_en", "preocupacion", "idea", "recuerdo")
    fun emocionesDe(m: Momento): List<String> = m.emociones.ifEmpty { if (m.tipo in VIEJO_SENTIMIENTO) listOf(m.tipo) else emptyList() }
    fun pensamientoDe(m: Momento): String? = m.pensamiento ?: m.tipo.takeIf { it in VIEJO_PENSAMIENTO }
    /** Un registro de ánimo o un momento de antes (no impulsos, recaídas ni sustancias). */
    fun esRegistro(m: Momento) = m.esAnimo || m.tipo in VIEJO_SENTIMIENTO || m.tipo in VIEJO_PENSAMIENTO

    /** Cómo se nombra un registro: sus sentimientos o, si no hay, su pensamiento o «Ánimo». */
    fun titulo(m: Momento, discreto: Boolean): String {
        if (m.tipo == "impulso" || m.tipo == "recaida" || m.tipo == "libido") return nombreTipo(m.tipo).let { n -> if (discreto) tipo(m.tipo)?.discreto ?: n else n }
        val e = emocionesDe(m).map { if (it == OTRO && !m.emocion_otra.isNullOrBlank() && !discreto) m.emocion_otra else nombreSentimiento(it, discreto) }
        if (e.isNotEmpty()) return e.joinToString(" · ")
        pensamientoDe(m)?.let { p -> return if (discreto) "·" else PENSAMIENTOS.firstOrNull { it.id == p }?.nombre ?: p }
        return if (discreto) "Ā" else "Ánimo"
    }

    /** Las ocho del widget de momentos: un toque y se guarda ese sentimiento. */
    val SENTIMIENTOS_WIDGET = listOf("alegria", "calma", "gratitud", "estres", "ansiedad", "frustracion", "tristeza", "enojo")

    // ------------------------------------------------------------------ sustancias

    data class Sustancia(val id: String, val nombre: String, val unidad: String, val conNivel: Boolean, val vias: List<Opcion> = emptyList())

    val SUSTANCIAS = listOf(
        Sustancia("alcohol", "Alcohol", "bebidas", true),
        Sustancia("cannabis", "Cannabis", "veces", true, listOf(Opcion("fumado", "Fumado"), Opcion("vape", "Vape"), Opcion("comestible", "Comestible"))),
        Sustancia("tabaco", "Tabaco o vape", "cigarros o sesiones", false, listOf(Opcion("cigarro", "Cigarro"), Opcion("vape", "Vape"))),
        Sustancia("cafeina", "Cafeína", "mg", false),
        Sustancia("otra", "Otra", "dosis", true),
    )
    fun sustancia(id: String) = SUSTANCIAS.firstOrNull { it.id == id }
    fun nombreSustancia(c: Consumo) = if (c.sustancia == "otra" && c.otra_nombre.isNotBlank()) c.otra_nombre else sustancia(c.sustancia)?.nombre ?: c.sustancia

    /** Escalas de 0 a 10 con anclas en 2.5, 5, 7.5 y 10, para que el número signifique lo mismo cada vez. */
    val NIVEL_ALCOHOL = mapOf(
        0 to "Sobrio", 1 to "Apenas lo noto", 2 to "Relajado, un poco suelto", 3 to "Tipsy: más platicador", 4 to "Bien tipsy",
        5 to "Entre tipsy y pedo", 6 to "Pedo agradable", 7 to "Pedo, ya pierdo algo de control", 8 to "Pedo desagradable: mareo",
        9 to "Muy mal, náusea fuerte", 10 to "Vómito y mucho mareo")
    val NIVEL_CANNABIS = mapOf(
        0 to "Nada", 1 to "Apenas lo noto", 2 to "Relajado", 3 to "Leve, agradable", 4 to "Se nota bien", 5 to "Claramente high",
        6 to "Muy high, agradable", 7 to "Pensamientos rápidos, lento al hablar", 8 to "Demasiado: incómodo", 9 to "Ansiedad o paranoia", 10 to "Abrumador, náusea o pánico")
    val NIVEL_OTRA = mapOf(0 to "Nada", 2 to "Leve", 5 to "Claro", 7 to "Fuerte", 8 to "Fuerte e incómodo", 10 to "Abrumador")
    fun anclasNivel(sustancia: String) = when (sustancia) { "alcohol" -> NIVEL_ALCOHOL; "cannabis" -> NIVEL_CANNABIS; else -> NIVEL_OTRA }

    val CRUDA = mapOf(0 to "Nada", 1 to "Leve: lo noto", 2 to "Molesta", 3 to "Moderada: me cuesta arrancar", 4 to "Fuerte: el día se pierde en parte", 5 to "No puedo funcionar")
    val SINTOMAS = listOf(Opcion("cabeza", "Dolor de cabeza"), Opcion("nausea", "Náusea"), Opcion("cansancio", "Cansancio"), Opcion("ansiedad", "Ansiedad"),
        Opcion("sed", "Sed o boca seca"), Opcion("lagunas", "Lagunas de memoria"), Opcion("mal_sueno", "Dormí mal"), Opcion("animo_bajo", "Ánimo bajo"))
    /** Bebidas para contar rápido: equivalencia en bebidas estándar (14 g de alcohol). */
    val BEBIDAS = listOf("Cerveza 355 ml" to 1.0, "Caguama 940 ml" to 2.6, "Copa de vino" to 1.0, "Caballito" to 1.0, "Cuba o trago" to 1.0, "Mezcal o tequila doble" to 2.0)
    /** Cafeína para anotar a mano (mg). */
    val CAFES = listOf("Espresso" to 64.0, "Café de taza" to 95.0, "Café grande" to 190.0, "Té negro" to 47.0, "Mate" to 80.0, "Energética" to 80.0, "Refresco de cola" to 35.0)

    // ------------------------------------------------------------------ sueños por ejes

    data class Eje(val id: String, val nombre: String, val ayuda: String, val opciones: List<Opcion>)

    /**
     * Categorías de sueños en ejes separados (para que no se enciman: un sueño puede ser «familia» y «niñez» a la vez).
     * Se basan en el sistema de Hall y Van de Castle y en los cuestionarios de sueños típicos.
     */
    val SUENO_EJES = listOf(
        Eje("per", "Quiénes aparecen", "Personajes", listOf(Opcion("per_familia", "Familia"), Opcion("per_pareja", "Pareja"), Opcion("per_expareja", "Expareja"),
            Opcion("per_amistades", "Amistades"), Opcion("per_pasado", "Gente de antes"), Opcion("per_trabajo", "Gente del trabajo o la escuela"),
            Opcion("per_desconocidos", "Desconocidos"), Opcion("per_famosos", "Figuras públicas"), Opcion("per_ninos", "Niños"),
            Opcion("per_animales", "Animales"), Opcion("per_criaturas", "Criaturas o seres extraños"), Opcion("per_fallecidos", "Alguien que ya murió"),
            Opcion("per_yo_otro", "Yo, con otra edad o cuerpo"))),
        Eje("esc", "Dónde pasa", "Escenario", listOf(Opcion("esc_casa", "Mi casa de ahora"), Opcion("esc_infancia", "Un lugar de mi pasado (casa vieja, escuela)"),
            Opcion("esc_escuela", "Escuela o trabajo"), Opcion("esc_conocido_cambiado", "Un lugar conocido pero cambiado"), Opcion("esc_desconocido", "Lugar desconocido"),
            Opcion("esc_extranjero", "Otro país o ciudad"), Opcion("esc_transporte", "Transporte (coche, avión, tren)"), Opcion("esc_imposible", "Lugar imposible"),
            Opcion("esc_naturaleza", "Naturaleza"))),
        Eje("int", "Qué pasa entre personas", "Interacción", listOf(Opcion("int_romantica", "Romántico: ligar, noviazgo"), Opcion("int_sexual", "Sexual"),
            Opcion("int_amistosa", "Amistosa"), Opcion("int_conflicto", "Conflicto o pelea"), Opcion("int_ayuda", "Ayudar o que me ayuden"),
            Opcion("int_cuidado", "Me cuidaban o yo cuidaba"), Opcion("int_rechazo", "Rechazo o abandono"), Opcion("int_traicion", "Traición o engaño"),
            Opcion("int_reconciliacion", "Reconciliación"), Opcion("int_despedida", "Despedida"))),
        Eje("tema", "Temas típicos", "Temas", listOf(Opcion("tema_persecucion", "Persecución"), Opcion("tema_caida", "Caer"), Opcion("tema_volar", "Volar"),
            Opcion("tema_examen", "Examen o llegar tarde"), Opcion("tema_perder", "Perder algo"), Opcion("tema_buscar", "Buscar algo o a alguien"),
            Opcion("tema_atrapado", "Atrapado o encerrado"), Opcion("tema_paralisis", "No poder moverme, gritar o correr"), Opcion("tema_muerte", "Muerte o enfermedad"),
            Opcion("tema_desnudez", "Desnudez en público"), Opcion("tema_dientes", "Dientes"), Opcion("tema_viaje", "Viaje"), Opcion("tema_agua", "Agua (inundación, ahogarse)"),
            Opcion("tema_desastre", "Desastre"), Opcion("tema_aparato", "Un aparato que no funciona"), Opcion("tema_embarazo", "Embarazo o bebés"),
            Opcion("tema_fiesta", "Fiesta o celebración"), Opcion("tema_regreso", "Volver a otra época o edad"), Opcion("tema_poderes", "Poderes o magia"))),
        Eje("res", "Cómo me fue en el sueño", "Cómo me fue", listOf(Opcion("res_logro", "Logré algo"), Opcion("res_fallo", "Fallé o no pude"),
            Opcion("res_suerte", "Tuve suerte"), Opcion("res_desgracia", "Me pasó algo malo"), Opcion("res_peligro", "Estaba en peligro"))),
        Eje("sen", "Lo que sentía el cuerpo", "Sensaciones", listOf(Opcion("sen_colores", "Colores vivos"), Opcion("sen_sonidos", "Sonidos o voces"),
            Opcion("sen_tacto", "Tacto o dolor"), Opcion("sen_olores", "Olores o sabores"), Opcion("sen_movimiento", "Caer, flotar o volar en el cuerpo"))),
        Eje("est", "Cómo es el sueño", "Estructura", listOf(Opcion("est_conexion", "Una persona en el centro de todo el sueño"), Opcion("est_restos", "Restos del día de ayer"),
            Opcion("est_recurrente", "Se repite"), Opcion("est_continua", "Sigue un sueño anterior"), Opcion("est_fuera", "Me veía desde fuera"),
            Opcion("est_otra_persona", "Era otra persona"), Opcion("est_saltos", "Cambios bruscos de escena"), Opcion("est_interrumpido", "Desperté a la mitad"))),
        Eje("emo", "Emociones del sueño (varias)", "Emociones", listOf(Opcion("emo_alegria", "Alegría"), Opcion("emo_paz", "Paz"), Opcion("emo_euforia", "Euforia"),
            Opcion("emo_ternura", "Ternura"), Opcion("emo_alivio", "Alivio"), Opcion("emo_asombro", "Asombro"), Opcion("emo_deseo", "Deseo"),
            Opcion("emo_miedo", "Miedo"), Opcion("emo_ansiedad", "Ansiedad"), Opcion("emo_tristeza", "Tristeza"), Opcion("emo_soledad", "Soledad"),
            Opcion("emo_enojo", "Enojo"), Opcion("emo_impotencia", "Impotencia"), Opcion("emo_culpa", "Culpa"), Opcion("emo_verguenza", "Vergüenza"),
            Opcion("emo_celos", "Celos"), Opcion("emo_asco", "Asco"), Opcion("emo_confusion", "Confusión"), Opcion("emo_nostalgia", "Nostalgia"))),
        Eje("desp", "Cómo desperté", "Al despertar", listOf(Opcion("desp_alivio", "Aliviado de que no fuera real"), Opcion("desp_volver", "Con ganas de volver al sueño"),
            Opcion("desp_emocion", "Con la emoción todavía encima"), Opcion("desp_persona", "Pensando en alguien del sueño"))),
    )
    /** Categorías que ya no se ofrecen pero pueden estar en días viejos: «Solo fragmentos» pasó a la claridad del recuerdo. */
    private val SUENO_RETIRADAS = listOf(Opcion("est_fragmentos", "Solo fragmentos"))
    /** Las de siempre, para cuando todavía no hay suficientes sueños marcados. */
    private val SUENO_RAPIDAS_BASE = listOf("int_romantica", "tema_regreso", "est_conexion", "per_familia", "per_expareja", "tema_persecucion", "est_restos", "est_recurrente")
    /**
     * Las que más usas, a la vista sin abrir los ejes: las 8 categorías más marcadas en tus sueños de los últimos 180 días;
     * si no llegan a 8, se completan con las de siempre.
     */
    fun suenoRapidas(dias: Collection<Dia>, hoy: String = Fechas.diaLogico()): List<String> {
        val desde = Fechas.sumar(hoy, -180)
        val vigentes = SUENO_EJES.flatMap { e -> e.opciones.map { it.id } }.toSet()
        val usadas = dias.filter { it.fecha >= desde }.flatMap { categoriasSuenoDe(it) }.filter { it in vigentes }
            .groupingBy { it }.eachCount().entries.sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key }).map { it.key }
        return (usadas + SUENO_RAPIDAS_BASE).distinct().take(8)
    }
    fun opcionSueno(id: String) = (SUENO_EJES.flatMap { it.opciones } + SUENO_RETIRADAS).firstOrNull { it.id == id }

    /** «¿Con quién estás?» en los avisos de ánimo. */
    val CON_QUIEN = listOf(Opcion("solo", "Solo"), Opcion("pareja", "Pareja"), Opcion("amigos", "Amigos"), Opcion("familia", "Familia"),
        Opcion("companeros", "Compañeros"), Opcion("desconocidos", "Desconocidos"))
    /** Recaída: en qué aparato y qué hacías justo antes. */
    val DISPOSITIVOS = listOf(Opcion("telefono", "Teléfono"), Opcion("computadora", "Computadora"), Opcion("tableta", "Tableta"), Opcion("tele", "Tele"), Opcion("otro", "Otro"))
    val ANTES = listOf(Opcion("redes", "En redes"), Opcion("cama", "En la cama sin dormir"), Opcion("estudiando", "Estudiando o trabajando"),
        Opcion("solo_casa", "Solo en casa"), Opcion("sin_plan", "Aburrido, sin plan"), Opcion("tras_discusion", "Después de algo difícil"), Opcion("otro", "Otra cosa"))
    /** El cronómetro: bloques de enfoque y siestas. */
    val ENFOQUE = listOf(Opcion("estudio", "Estudio"), Opcion("lectura", "Lectura"), Opcion("idiomas", "Idiomas"), Opcion("tesis", "Tesis"),
        Opcion("trabajo", "Trabajo"), Opcion("creativo", "Creativo"))
    val SIESTA = Opcion("siesta", "Siesta")
    fun nombreTramo(id: String): String = (ENFOQUE + SIESTA).firstOrNull { it.id == id }?.nombre ?: id

    val CATEGORIAS = listOf("Comida", "Transporte", "Salud", "Ocio", "Social", "Suscripciones", "Escuela", "Ropa", "Regalos", "Otro")
    val CATEGORIAS_INGRESO = listOf("Sueldo", "Beca", "Trabajo extra", "Mesada", "Regalo", "Venta", "Reembolso", "Intereses", "Otro")
    val DETONANTES = listOf("aburrimiento", "soledad", "estres", "tristeza", "ansiedad", "noche_insomnio", "redes_sociales", "cansancio", "otro")
    val HABITOS_SUGERIDOS = listOf("Meditar", "Leer", "Estudiar chino", "Escribir a mano", "Dormir antes de 00:00", "Sin pantalla al despertar")
    val TIPOS_LUGAR = listOf(Opcion("casa", "Casa"), Opcion("familia", "Familia"), Opcion("trabajo", "Trabajo"),
        Opcion("escuela", "Escuela"), Opcion("ejercicio", "Ejercicio"), Opcion("social", "Social"), Opcion("bar", "Bar"), Opcion("ocio", "Ocio"),
        Opcion("salud", "Salud"), Opcion("naturaleza", "Naturaleza"), Opcion("viaje", "Viaje"), Opcion("otro", "Otro"))
    fun nombreTipoLugar(id: String) = TIPOS_LUGAR.firstOrNull { it.id == id }?.nombre ?: id

    /** Notas sueltas y metas. */
    val TIPOS_NOTA = listOf(Opcion("idea", "Idea"), Opcion("pensamiento", "Pensamiento"), Opcion("proposito", "Propósito"), Opcion("recordatorio", "Recordatorio"))
    /** Ideario: de dónde viene la idea, cómo la tienes y qué respondiste al volver a verla. */
    val ORIGENES_IDEA = listOf(Opcion("leido", "Lo leí"), Opcion("visto", "Lo vi o escuché"), Opcion("contado", "Me lo contaron"), Opcion("propia", "Se me ocurrió"))
    val POSTURAS_IDEA = listOf(Opcion("convence", "Me convence"), Opcion("piensa", "Me hace pensar"), Opcion("duda", "La dudo"), Opcion("desacuerdo", "No estoy de acuerdo"))
    val RESPUESTAS_IDEA = listOf(Opcion("sigue", "Sigo pensando igual"), Opcion("cambio", "Ya no"), Opcion("ignorar", "Ahora no"))
    val HORIZONTES = listOf(Opcion("corto", "Corto", "hasta 3 meses"), Opcion("mediano", "Mediano", "hasta un año"), Opcion("largo", "Largo", "más de un año"))
    val ESTADOS_META = listOf(Opcion("activa", "Activa"), Opcion("pausada", "Pausada"), Opcion("lograda", "Lograda"), Opcion("abandonada", "Abandonada"))
    val REVISIONES = listOf(Opcion("semanal", "Cada semana"), Opcion("mensual", "Cada mes"), Opcion("fecha", "En una fecha"), Opcion("ninguna", "Sin revisión"))
}
