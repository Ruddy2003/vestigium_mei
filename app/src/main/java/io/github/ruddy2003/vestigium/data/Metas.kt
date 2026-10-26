package io.github.ruddy2003.vestigium.data

/**
 * Metas: horizonte, fecha objetivo, estado e historial de cambios. Si están ligadas a un hábito o a una variable del día,
 * el progreso se calcula solo desde que se crearon; si no, lo marcas tú. Cada meta activa pide una revisión (cada semana,
 * cada mes o en una fecha); los propósitos de año nuevo se revisan en marzo, junio y septiembre.
 */
object Metas {
    fun activas(): List<Meta> = Repo.metas.filter { it.estado == "activa" }

    /** Los valores diarios de la medida, desde que se creó la meta hasta hoy (o la fecha objetivo). */
    private fun valores(m: Meta, hoy: String, panel: Map<String, Map<String, Double?>>?): List<Double> {
        val desde = m.creada.take(10)
        val hasta = listOfNotNull(hoy, m.fecha_objetivo).min()
        val clave = m.medida_clave ?: return emptyList()
        return when (m.medida_tipo) {
            "habito" -> Repo.dias.filterKeys { it in desde..hasta }.values.map { if (it.habitos[clave] == true) 1.0 else 0.0 }
            "variable" -> (panel ?: emptyMap()).filterKeys { it in desde..hasta }.values.mapNotNull { it[clave] }
            else -> emptyList()
        }
    }

    /** Lo que lleva la medida: días que cumplen, la suma o la media (null si no hay medida o datos). */
    fun valor(m: Meta, hoy: String = Fechas.diaLogico(), panel: Map<String, Map<String, Double?>>? = null): Double? {
        if (m.medida_tipo == null) return null
        val v = valores(m, hoy, panel)
        if (v.isEmpty()) return 0.0
        return when (m.medida_modo) {
            "suma" -> v.sum()
            "media" -> v.average()
            else -> v.count { if (m.medida_tipo == "habito") it >= 1.0 else it >= m.umbral }.toDouble()
        }
    }

    /** Progreso de 0 a 1: la medida contra el objetivo o, sin medida, lo que marcaste. */
    fun progreso(m: Meta, hoy: String = Fechas.diaLogico(), panel: Map<String, Map<String, Double?>>? = null): Double? {
        if (m.estado == "lograda") return 1.0
        if (m.medida_tipo == null) return m.progreso_manual?.let { it / 100.0 }
        val obj = m.objetivo?.takeIf { it > 0 } ?: return null
        return ((valor(m, hoy, panel) ?: 0.0) / obj).coerceIn(0.0, 1.0)
    }

    /** Cuándo toca la siguiente revisión (null si no toca ninguna). */
    fun proximaRevision(m: Meta): String? {
        if (m.estado != "activa") return null
        val base = m.ultima_revision ?: m.creada.take(10)
        if (m.revisiones.isNotEmpty()) return m.revisiones.sorted().firstOrNull { it > base }
        return when (m.revision) {
            "semanal" -> Fechas.sumar(base, 7)
            "mensual" -> Fechas.iso(Fechas.parse(base).plusMonths(1))
            "fecha" -> m.revisar_el?.takeIf { it > base }
            else -> null
        }
    }

    fun porRevisar(hoy: String = Fechas.diaLogico()): List<Meta> = activas().filter { proximaRevision(it)?.let { f -> f <= hoy } == true }

    /** Marca la revisión de hoy (y deja una línea en el historial). */
    fun revisar(m: Meta, nota: String = ""): Meta {
        val hoy = Fechas.diaLogico()
        val n = m.copy(ultima_revision = hoy, historial = m.historial + CambioMeta(hoy, "revision", m.ultima_revision ?: "", nota))
        Repo.guardarMeta(n); return n
    }

    /** Guarda los cambios dejando en el historial qué cambió (estado, fecha, horizonte, nombre, progreso). */
    fun cambiar(antes: Meta?, despues: Meta): Meta {
        if (antes == null) { Repo.guardarMeta(despues); return despues }
        val hoy = Fechas.diaLogico()
        val cambios = listOfNotNull(
            if (antes.estado != despues.estado) CambioMeta(hoy, "estado", antes.estado, despues.estado) else null,
            if (antes.fecha_objetivo != despues.fecha_objetivo) CambioMeta(hoy, "fecha_objetivo", antes.fecha_objetivo ?: "", despues.fecha_objetivo ?: "") else null,
            if (antes.horizonte != despues.horizonte) CambioMeta(hoy, "horizonte", antes.horizonte, despues.horizonte) else null,
            if (antes.nombre != despues.nombre) CambioMeta(hoy, "nombre", antes.nombre, despues.nombre) else null,
            if (antes.progreso_manual != despues.progreso_manual) CambioMeta(hoy, "progreso", "${antes.progreso_manual ?: ""}", "${despues.progreso_manual ?: ""}") else null,
        )
        val n = despues.copy(historial = antes.historial + cambios)
        Repo.guardarMeta(n); return n
    }

    /** Horizonte que corresponde a una fecha objetivo (hasta 3 meses, hasta un año o más). */
    fun horizonteDe(fecha: String?, hoy: String = Fechas.diaLogico()): String {
        val d = fecha?.let { runCatching { Fechas.diasEntre(hoy, it) }.getOrNull() } ?: return "mediano"
        return when { d <= 92 -> "corto"; d <= 366 -> "mediano"; else -> "largo" }
    }

    /** En diciembre: la plantilla de propósitos del año siguiente, con revisiones en marzo, junio y septiembre. */
    fun toPlantilla(hoy: String = Fechas.diaLogico()): Int? {
        val f = Fechas.parse(hoy)
        val anio = if (f.monthValue == 12) f.year + 1 else if (f.monthValue == 1 && f.dayOfMonth <= 15) f.year else return null
        return anio.takeIf { a -> Repo.metas.none { it.anio_proposito == a } }
    }

    fun proposito(nombre: String, anio: Int): Meta = Meta(
        id = Repo.nuevoId(), nombre = nombre.trim(), horizonte = "mediano", creada = Fechas.ahoraIso(), fecha_objetivo = "$anio-12-31",
        revision = "fecha", revisiones = listOf("$anio-03-31", "$anio-06-30", "$anio-09-30", "$anio-12-31"), anio_proposito = anio,
    )

    /** Ideas para empezar la lista de propósitos (se eligen y se editan). */
    val IDEAS_PROPOSITOS = listOf("Dormir 7 horas la mayoría de las noches", "Leer 12 libros", "Escribir el diario casi todos los días",
        "Una hora de enfoque al día", "Ahorrar cada mes", "Entrenar tres veces por semana", "Aprender un idioma", "Ver más a mis amigos")
}
