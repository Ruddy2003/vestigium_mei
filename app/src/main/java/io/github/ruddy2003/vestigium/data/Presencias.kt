package io.github.ruddy2003.vestigium.data

/**
 * Pensar en alguien no es verlo. Cada @persona del día tiene un estado: estuviste con ella (lo normal), solo pensaste en
 * ella, o hablaron por mensaje o llamada. El diario manda (lo marcas con un toque debajo del texto); si alguien solo
 * aparece en momentos, cuenta como «pensé» cuando ese momento era «pensando en…» o un recuerdo y estabas solo.
 */
object Presencias {
    const val ESTUVE = "estuve"
    const val PENSE = "pense"
    const val MENSAJES = "mensajes"

    val OPCIONES = listOf(Opcion(ESTUVE, "estuve con", "La viste en persona"), Opcion(PENSE, "pensé en", "No la viste: pensaste en ella o la recordaste"),
        Opcion(MENSAJES, "mensajes", "Hablaron por mensaje o llamada, sin verse"))

    fun nombre(id: String): String = OPCIONES.firstOrNull { it.id == id }?.nombre ?: OPCIONES[0].nombre

    /** El que sigue al tocar el chip: estuve → pensé → mensajes → estuve. */
    fun siguiente(id: String): String = when (id) { ESTUVE -> PENSE; PENSE -> MENSAJES; else -> ESTUVE }

    /** Cómo estuviste con [persona] (sin @, en minúsculas) según lo que marcaste en el diario de [d]. */
    fun enDiario(d: Dia?, persona: String, eq: Map<String, String> = Repo.equivalencias()): String {
        val p = d?.presencias ?: return ESTUVE
        p[persona]?.let { return it }
        val k = Texto.canon(persona, eq)
        return p.entries.firstOrNull { Texto.canon(it.key, eq) == k }?.value ?: ESTUVE
    }

    /** Guarda el estado de una persona en el día (estuve = sin entrada). Con [revisado], marca además el día como revisado. */
    fun con(d: Dia, persona: String, estado: String, revisado: Boolean = true): Dia {
        val k = persona.lowercase().removePrefix("@")
        val m = if (estado == ESTUVE) d.presencias - k else d.presencias + (k to estado)
        return d.copy(presencias = m, presencias_ok = d.presencias_ok || revisado)
    }

    private val PENSAR = setOf("pensando_en", "recuerdo")

    /** En un momento: si era «pensando en…» o un recuerdo y no estabas con nadie, la gente que nombras es en quien pensabas. */
    fun deMomento(m: Momento): String =
        if (Catalogo.pensamientosDe(m).any { it in PENSAR } && m.con_quien.all { it == "solo" || it == "mensaje" }) PENSE else ESTUVE

    /**
     * Todas las personas de un día (nombre canónico) con su estado: lo que marcaste en el diario manda; si alguien solo
     * aparece en momentos, cuenta como presente si en alguno estabas con ella.
     */
    fun delDia(fecha: String, eq: Map<String, String> = Repo.equivalencias(), dia: Dia? = Repo.dias[fecha],
               momentos: List<Momento> = Repo.momentos.filter { it.fecha == fecha }): Map<String, String> {
        val out = LinkedHashMap<String, String>()
        dia?.let { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct().forEach { p -> out[p] = enDiario(d, p, eq) } }
        momentos.filter { !it.privado }.forEach { m ->
            val e = deMomento(m)
            Texto.personas(m.texto).map { Texto.canon(it, eq) }.distinct().forEach { p ->
                if (dia != null && Texto.personas(dia.nota).any { Texto.canon(it, eq) == p }) return@forEach
                val antes = out[p]
                out[p] = if (antes == ESTUVE || e == ESTUVE) ESTUVE else e
            }
        }
        return out
    }

    /** Días con personas en el diario que todavía no revisaste (de los más nuevos a los más viejos). */
    fun porRevisar(): List<Dia> = Repo.dias.values.filter { !it.presencias_ok && Texto.personas(it.nota).isNotEmpty() }.sortedByDescending { it.fecha }

    /** Todos los días con personas en el diario, revisados o no. */
    fun conPersonas(): List<Dia> = Repo.dias.values.filter { Texto.personas(it.nota).isNotEmpty() }.sortedByDescending { it.fecha }

    /**
     * Hasta v81, tocar un nombre en «¿Estuviste o pensaste?» daba el día por revisado y lo quitaba de la lista. Una sola vez,
     * esos días (los que tienen algún cambio marcado) vuelven a «por revisar», con lo que ya marcaste.
     */
    fun paraReabrir(): List<Dia> = if (Repo.config.presencias_reabiertas) emptyList()
        else Repo.dias.values.filter { it.presencias_ok && it.presencias.isNotEmpty() && Texto.personas(it.nota).isNotEmpty() }

    fun reabrirUnaVez() {
        if (Repo.config.presencias_reabiertas) return
        val l = paraReabrir().map { it.copy(presencias_ok = false) }
        if (l.isNotEmpty()) Repo.importarDias(l, sobrescribir = true)
        Repo.cambiarConfig { it.copy(presencias_reabiertas = true) }
    }
}
