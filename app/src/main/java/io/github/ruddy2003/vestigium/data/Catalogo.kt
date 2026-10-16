package io.github.ruddy2003.vestigium.data

data class Opcion(val id: String, val nombre: String, val detalle: String = "")
data class TipoMomento(val id: String, val nombre: String, val discreto: String, val pregunta: String)
data class Pregunta(val clave: String, val pregunta: String, val autor: String, val guia: String, val opciones: List<Opcion>)

object Catalogo {
    val CONTEXTOS = listOf(
        Opcion("gym", "Gym"), Opcion("viaje", "Viaje"), Opcion("enfermo", "Enfermo"),
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
    val SUENO_VIVIDEZ = listOf(Opcion("vago", "Vago"), Opcion("moderado", "Moderado"), Opcion("muy_vivido", "Muy vívido"))
    val SUENO_TIPO = listOf(Opcion("mundano", "Mundano"), Opcion("ansioso_pesadilla", "Ansioso o pesadilla"),
        Opcion("bizarro_surreal", "Bizarro o surreal"), Opcion("recurrente", "Recurrente"))

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
    fun tipo(id: String): TipoMomento? = MOMENTOS.firstOrNull { it.id == id }
    fun nombreTipo(id: String): String = if (id == "muestreo") "Ánimo" else tipo(id)?.nombre ?: id

    val CATEGORIAS = listOf("Comida", "Transporte", "Salud", "Ocio", "Social", "Suscripciones", "Escuela", "Ropa", "Regalos", "Otro")
    val DETONANTES = listOf("aburrimiento", "soledad", "estres", "tristeza", "ansiedad", "noche_insomnio", "redes_sociales", "cansancio", "otro")
    val HABITOS_SUGERIDOS = listOf("Meditar", "Leer", "Estudiar chino", "Escribir a mano", "Dormir antes de 00:00", "Sin pantalla al despertar")
    val TIPOS_LUGAR = listOf(Opcion("casa", "Casa"), Opcion("familia", "Familia"), Opcion("trabajo", "Trabajo"),
        Opcion("escuela", "Escuela"), Opcion("ejercicio", "Ejercicio"), Opcion("social", "Social"), Opcion("otro", "Otro"))
}
