package io.github.ruddy2003.vestigium.ciencia

/**
 * Instrumentos validados. Los textos siguen las versiones publicadas en español;
 * cuando se adapta el periodo de referencia para uso semanal, se indica en `adaptacion`.
 */
data class Item(val clave: String, val texto: String, val inverso: Boolean = false)

data class Instrumento(
    val id: String,
    val nombre: String,
    val mide: String,
    val frecuenciaDias: Int,
    val instruccion: String,
    val items: List<Item>,
    val opciones: List<Pair<Double, String>>,
    val referencia: String,
    val adaptacion: String,
    val interpretar: (Map<String, Double>) -> Map<String, Double>,
    val lectura: (Map<String, Double>) -> String,
)

object Escalas {
    private val frecuenciaWho = listOf(5.0 to "Todo el tiempo", 4.0 to "La mayor parte del tiempo", 3.0 to "Más de la mitad del tiempo",
        2.0 to "Menos de la mitad del tiempo", 1.0 to "De vez en cuando", 0.0 to "Nunca")

    val WHO5 = Instrumento(
        id = "who5", nombre = "Bienestar (WHO-5)", mide = "Bienestar general", frecuenciaDias = 7,
        instruccion = "Durante los últimos 7 días…",
        items = listOf(
            Item("w1", "Me he sentido alegre y de buen humor"),
            Item("w2", "Me he sentido tranquilo y relajado"),
            Item("w3", "Me he sentido activo y enérgico"),
            Item("w4", "Me he despertado fresco y descansado"),
            Item("w5", "Mi vida diaria ha estado llena de cosas que me interesan"),
        ),
        opciones = frecuenciaWho,
        referencia = "Topp, Østergaard, Søndergaard y Bech (2015). The WHO-5 Well-Being Index: a systematic review of the literature. Psychotherapy and Psychosomatics, 84(3), 167-176.",
        adaptacion = "El original pregunta por las últimas 2 semanas; aquí se usa 1 semana para seguimiento semanal.",
        interpretar = { r -> val bruto = (1..5).sumOf { r["w$it"] ?: 0.0 }; mapOf("bruto" to bruto, "porcentaje" to bruto * 4) },
        lectura = { p ->
            val v = p["porcentaje"] ?: 0.0
            "Puntaje ${v.toInt()} de 100. " + if (v <= 28) "Es un valor bajo. Si se repite varias semanas, vale la pena hablarlo con un profesional de salud."
            else if (v <= 50) "Bienestar por debajo de lo habitual en población general." else "Bienestar en rango habitual o alto."
        },
    )

    private val frecuenciaPss = listOf(0.0 to "Nunca", 1.0 to "Casi nunca", 2.0 to "De vez en cuando", 3.0 to "A menudo", 4.0 to "Muy a menudo")

    val PSS4 = Instrumento(
        id = "pss4", nombre = "Estrés percibido (PSS-4)", mide = "Qué tan impredecible y desbordada sentiste tu vida", frecuenciaDias = 7,
        instruccion = "En los últimos 7 días, ¿con qué frecuencia…",
        items = listOf(
            Item("p1", "te has sentido incapaz de controlar las cosas importantes de tu vida?"),
            Item("p2", "te has sentido seguro de tu capacidad para manejar tus problemas personales?", inverso = true),
            Item("p3", "has sentido que las cosas te van bien?", inverso = true),
            Item("p4", "has sentido que las dificultades se acumulan tanto que no puedes superarlas?"),
        ),
        opciones = frecuenciaPss,
        referencia = "Cohen, Kamarck y Mermelstein (1983). A global measure of perceived stress. Journal of Health and Social Behavior, 24(4), 385-396. Versión en español: Remor (2006), Spanish Journal of Psychology, 9(1), 86-93.",
        adaptacion = "El original pregunta por el último mes; aquí se usa 1 semana.",
        interpretar = { r ->
            val total = listOf("p1", "p4").sumOf { r[it] ?: 0.0 } + listOf("p2", "p3").sumOf { 4.0 - (r[it] ?: 0.0) }
            mapOf("total" to total)
        },
        lectura = { p -> "Puntaje ${(p["total"] ?: 0.0).toInt()} de 16. Más alto significa más estrés percibido. Lo útil es comparar contigo mismo semana a semana." },
    )

    val UCLA3 = Instrumento(
        id = "ucla3", nombre = "Soledad (UCLA-3)", mide = "Sensación de soledad", frecuenciaDias = 30,
        instruccion = "Pensando en el último mes, ¿con qué frecuencia…",
        items = listOf(
            Item("u1", "sientes que te falta compañía?"),
            Item("u2", "te sientes excluido o dejado de lado?"),
            Item("u3", "te sientes aislado de los demás?"),
        ),
        opciones = listOf(1.0 to "Casi nunca", 2.0 to "A veces", 3.0 to "A menudo"),
        referencia = "Hughes, Waite, Hawkley y Cacioppo (2004). A short scale for measuring loneliness in large surveys. Research on Aging, 26(6), 655-672.",
        adaptacion = "Se aplica una vez al mes.",
        interpretar = { r -> mapOf("total" to (1..3).sumOf { r["u$it"] ?: 0.0 }) },
        lectura = { p -> val t = (p["total"] ?: 0.0).toInt(); "Puntaje $t de 9. " + if (t >= 6) "Valores de 6 o más suelen usarse como indicador de soledad." else "Rango bajo de soledad." },
    )

    private val acuerdo = listOf(1.0 to "Muy en desacuerdo", 2.0 to "Moderadamente en desacuerdo", 3.0 to "Algo en desacuerdo",
        4.0 to "Ni de acuerdo ni en desacuerdo", 5.0 to "Algo de acuerdo", 6.0 to "Moderadamente de acuerdo", 7.0 to "Muy de acuerdo")

    val TIPI = Instrumento(
        id = "tipi", nombre = "Personalidad (TIPI)", mide = "Los cinco grandes rasgos de personalidad", frecuenciaDias = 365,
        instruccion = "Me veo a mí mismo como una persona…",
        items = listOf(
            Item("t1", "extravertida, entusiasta"), Item("t2", "crítica, conflictiva"),
            Item("t3", "confiable, autodisciplinada"), Item("t4", "ansiosa, que se altera fácilmente"),
            Item("t5", "abierta a nuevas experiencias, compleja"), Item("t6", "reservada, callada"),
            Item("t7", "comprensiva, afectuosa"), Item("t8", "desorganizada, descuidada"),
            Item("t9", "calmada, emocionalmente estable"), Item("t10", "convencional, poco creativa"),
        ),
        opciones = acuerdo,
        referencia = "Gosling, Rentfrow y Swann (2003). A very brief measure of the Big-Five personality domains. Journal of Research in Personality, 37, 504-528. Versión en español: Romero, Villar, Gómez-Fraguela y López-Romero (2012), Personality and Individual Differences, 53(3), 289-293.",
        adaptacion = "Se aplica una vez al año para ver cambios a largo plazo.",
        interpretar = { r ->
            fun v(k: String) = r[k] ?: 4.0
            fun inv(k: String) = 8.0 - v(k)
            mapOf(
                "extraversion" to (v("t1") + inv("t6")) / 2, "amabilidad" to (inv("t2") + v("t7")) / 2,
                "responsabilidad" to (v("t3") + inv("t8")) / 2, "estabilidad" to (inv("t4") + v("t9")) / 2,
                "apertura" to (v("t5") + inv("t10")) / 2,
            )
        },
        lectura = { p -> p.entries.joinToString(" · ") { "${it.key.replaceFirstChar { c -> c.uppercase() }} ${"%.1f".format(it.value)}" } + " (escala 1 a 7)." },
    )

    val TODAS = listOf(WHO5, PSS4, UCLA3, TIPI)
    fun por(id: String) = TODAS.firstOrNull { it.id == id }

    /** Fecha en que toca responder: la última respuesta más la frecuencia; si nunca se ha respondido, hoy. */
    fun proxima(i: Instrumento): String {
        val u = io.github.ruddy2003.vestigium.data.Repo.ultimaRespuesta(i.id) ?: return io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        return io.github.ruddy2003.vestigium.data.Fechas.sumar(u.fecha, i.frecuenciaDias.toLong())
    }

    /** Días de atraso (0 = toca hoy; negativo = todavía no toca). */
    fun atraso(i: Instrumento): Long = java.time.temporal.ChronoUnit.DAYS.between(
        io.github.ruddy2003.vestigium.data.Fechas.parse(proxima(i)), io.github.ruddy2003.vestigium.data.Fechas.parse(io.github.ruddy2003.vestigium.data.Fechas.diaLogico()))

    fun pendientes(): List<Pair<Instrumento, Long>> = TODAS.map { it to atraso(it) }.filter { it.second >= 0 }

    /**
     * Horas a las que se recuerda según el atraso: el día que toca, una vez; con 1 a 3 días de atraso, dos veces;
     * con 4 o más, tres veces al día hasta que lo respondas.
     */
    fun horasAviso(atraso: Long, base: Int): List<Int> = when {
        atraso <= 0 -> listOf(base)
        atraso <= 3 -> listOf(12, base).distinct().sorted()
        else -> listOf(10, 15, maxOf(base, 20)).distinct().sorted()
    }
}

/** Referencias que sostienen el diseño de la app. */
object Fuentes {
    val LISTA = listOf(
        "Ánimo en dos ejes" to "Russell (1980). A circumplex model of affect. Journal of Personality and Social Psychology, 39(6), 1161-1178.",
        "Flujo" to "Csikszentmihalyi (1990). Flow: The Psychology of Optimal Experience. Harper & Row.",
        "Necesidades psicológicas" to "Deci y Ryan (2000). The \"what\" and \"why\" of goal pursuits. Psychological Inquiry, 11(4), 227-268.",
        "Evaluación del estrés" to "Lazarus y Folkman (1984). Stress, Appraisal, and Coping. Springer.",
        "Muestreo durante el día" to "Shiffman, Stone y Hufford (2008). Ecological momentary assessment. Annual Review of Clinical Psychology, 4, 1-32.",
        "Análisis de datos diarios" to "Bolger y Laurenceau (2013). Intensive Longitudinal Methods. Guilford Press.",
        "Prueba de reacción" to "Basner y Dinges (2011). Maximizing sensitivity of the Psychomotor Vigilance Test (PVT) to sleep loss. Sleep, 34(5), 581-591.",
        "Movilidad y ánimo" to "Saeb y colaboradores (2015). Mobile phone sensor correlates of depressive symptom severity in daily-life behavior. Journal of Medical Internet Research, 17(7), e175.",
        "Continuidad de los sueños" to "Domhoff (2003). The Scientific Study of Dreams. American Psychological Association.",
    )
}
