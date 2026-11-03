package io.github.ruddy2003.vestigium.data

/**
 * Lo que se calcula sobre el Ideario (esquema 16): sus estanterías de temas, las voces que ya citaste, qué idea vuelve a salir
 * en «Una idea de antes» y cómo se arma una idea a partir de lo que otra app comparte.
 */
object Ideario {
    /** Cuántos días mínimo antes de que una idea pueda volver a salir, y cuánto descansa después de responderla. */
    const val DIAS_MINIMO = 30
    /** Aniversarios que se prefieren (± [HOLGURA] días): un mes, seis meses y cada año hasta diez. */
    val ANIVERSARIOS: List<Long> = listOf(30L, 180L) + (1..10).map { 365L * it }
    const val HOLGURA = 3L

    /**
     * Las estanterías: cada #tema escrito en el texto de alguna idea, con cuántas ideas lo tienen, de más a menos (empate:
     * alfabético). Solo cuentan las ideas: los temas del diario o de los momentos no aparecen aquí.
     */
    fun temas(ideas: List<Idea>): List<Pair<String, Int>> {
        val n = HashMap<String, Int>()
        ideas.forEach { i -> Texto.temas(i.texto).distinct().forEach { n.merge(it, 1, Int::plus) } }
        return n.entries.sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key }).map { it.key to it.value }
    }

    /** Las ideas de una estantería (o, con null, las que no tienen ningún tema), de la más reciente a la más antigua. */
    fun deTema(ideas: List<Idea>, tema: String?): List<Idea> = ideas.filter { i ->
        val t = Texto.temas(i.texto)
        if (tema == null) t.isEmpty() else tema in t
    }.sortedByDescending { it.creada }

    /** Quién lo dice: los nombres ya usados en el Ideario (de más a menos) y después los autores de tus obras, sin repetir. */
    fun voces(ideas: List<Idea>, obras: List<Obra>): List<String> {
        val n = LinkedHashMap<String, Int>()
        ideas.forEach { i -> i.quien.forEach { q -> n.merge(q.trim(), 1, Int::plus) } }
        val usados = n.entries.filter { it.key.isNotBlank() }.sortedByDescending { it.value }.map { it.key }
        val autores = obras.flatMap { o -> o.autor.split(",", ";", " y ").map { it.trim() } }.filter { it.isNotBlank() }
        val vistos = HashSet<String>()
        return (usados + autores).filter { vistos.add(it.lowercase()) }
    }

    /**
     * La idea que vuelve a salir hoy en «Una idea de antes»: entre las que tienen al menos [DIAS_MINIMO] días y no respondiste
     * en los últimos [DIAS_MINIMO], primero las que cumplen un aniversario de [ANIVERSARIOS] (± [HOLGURA] días); si ninguna,
     * cualquiera de ellas. La elección depende solo del día y del id, así que no cambia al volver a abrir la pantalla.
     * null si no hay ninguna.
     */
    fun resurgir(ideas: List<Idea>, hoy: String): Idea? {
        val candidatas = ideas.filter { i ->
            val edad = runCatching { Fechas.diasEntre(i.fecha, hoy) }.getOrDefault(-1L)
            edad >= DIAS_MINIMO && i.revisiones.none { r -> runCatching { Fechas.diasEntre(r.fecha, hoy) }.getOrDefault(0L) < DIAS_MINIMO }
        }
        if (candidatas.isEmpty()) return null
        val aniversario = candidatas.filter { i ->
            val edad = Fechas.diasEntre(i.fecha, hoy)
            ANIVERSARIOS.any { kotlin.math.abs(edad - it) <= HOLGURA }
        }
        return aniversario.ifEmpty { candidatas }.minBy { (hoy + ":" + it.id).hashCode() }
    }

    /** Cuánto hace, en palabras: «hace un mes», «hace seis meses», «hace 2 años» cerca de los aniversarios; si no, en días. */
    fun haceCuanto(fecha: String, hoy: String): String {
        val d = Fechas.diasEntre(fecha, hoy)
        return when {
            kotlin.math.abs(d - 30) <= HOLGURA -> "hace un mes"
            kotlin.math.abs(d - 180) <= HOLGURA -> "hace seis meses"
            d >= 365 - HOLGURA && kotlin.math.abs(d - 365 * ((d + HOLGURA) / 365)) <= HOLGURA -> ((d + HOLGURA) / 365).let { if (it == 1L) "hace un año" else "hace $it años" }
            else -> "hace $d días"
        }
    }

    /** La idea que ya respondiste hoy en «Una idea de antes» (para no sacar otra el mismo día). */
    fun respondidaHoy(ideas: List<Idea>, hoy: String): Idea? = ideas.firstOrNull { i -> i.revisiones.any { it.fecha == hoy } }

    private val RX_URL = Regex("https?://\\S+")

    /**
     * Arma una idea nueva con lo que compartió otra app: el primer enlace va a [Idea.enlace]; el asunto (título del video o de
     * la página), a [Idea.fuente]. Lo que queda del texto, si es corto (hasta 120 caracteres), también es la fuente; si es
     * largo (un subrayado de Kindle, un párrafo), queda en el texto para que lo reescribas. YouTube y los podcasts dejan el
     * origen en «visto»; Kindle, Goodreads y Amazon, en «leido».
     */
    fun deCompartido(texto: String?, asunto: String?, ahora: String, dia: String, id: String): Idea {
        val t = texto.orEmpty().trim()
        val enlace = RX_URL.find(t)?.value?.trimEnd('.', ',', ')', '»', '"').orEmpty()
        val resto = t.replace(RX_URL, "").trim().trim('-', '—', '·').trim()
        val corto = resto.length <= 120
        val fuente = asunto?.trim()?.takeIf { it.isNotBlank() } ?: if (corto) resto else ""
        val cuerpo = if (!corto) resto else ""
        val todo = (t + " " + asunto.orEmpty()).lowercase()
        val origen = when {
            listOf("youtube.com", "youtu.be", "spotify.com", "podcasts.apple", "podcasts.google", "vimeo.com", "twitch.tv").any { it in todo } -> "visto"
            listOf("kindle", "goodreads", "a.co/", "amazon.").any { it in todo } -> "leido"
            else -> ""
        }
        return Idea(id = id, creada = ahora, fecha = dia, texto = cuerpo, origen = origen, fuente = fuente.take(200), enlace = enlace)
    }

    /** Una idea nueva con la entrada del diario de [dia] (o el fragmento que quieras conservar): origen propio, del mismo día. */
    fun deDiario(texto: String, dia: String, ahora: String, id: String): Idea =
        Idea(id = id, creada = ahora, fecha = dia, texto = texto.trim(), origen = "propia")

    /** Una nota pasada al Ideario: conserva su texto, cuándo se creó y su día; origen propio. */
    fun deNota(n: Nota, id: String): Idea =
        Idea(id = id, creada = n.creada, fecha = n.fecha, texto = n.texto, origen = "propia", fijada = n.fijada, editada = n.editada)

    /** Lo que se busca de una idea: título, texto, quién lo dice, la fuente y el título de su obra. */
    fun textoBuscable(i: Idea, obras: List<Obra>): String =
        listOf(i.titulo, i.texto, i.quien.joinToString(" "), i.fuente, i.obra_id?.let { o -> obras.firstOrNull { it.id == o }?.titulo }.orEmpty()).joinToString(" ")
}
