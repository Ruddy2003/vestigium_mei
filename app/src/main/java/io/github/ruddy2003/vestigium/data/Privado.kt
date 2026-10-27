package io.github.ruddy2003.vestigium.data

/**
 * Lo privado, en un solo lugar: impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado a las recaídas.
 * Se ve solo en la pantalla Privado. No sale en ninguna otra pantalla, reporte, widget, logro, meta, campaña, mapa
 * ni análisis de la app; sí va en la exportación para R y en el respaldo (que, con datos privados, exige contraseña).
 */
object Privado {
    /** Tipos de momento privados (ver `Momento.privado`). */
    val TIPOS = setOf("recaida", "libido", "impulso")

    /** Variables del panel que solo existen para R. */
    val VARIABLES = setOf("impulso", "recaida", "n_recaida", "recaida_min", "libido_max", "n_libido", "n_impulso", "medicamentos_n")

    /** Familias dinámicas privadas: medicamentos (med_) y detonantes (det_). */
    val PREFIJOS = listOf("med_", "det_")

    /** El hábito que una recaída rompe. Con el seguimiento privado, el elegido o el que lo dice por su nombre; sin él, solo el elegido. */
    fun habito(c: Config = Repo.config): Habito? =
        if (c.seguimiento_privado) Recaidas.habito(c)
        else c.habito_recaida?.takeIf { it.isNotBlank() }?.let { id -> c.habitos.firstOrNull { it.id == id } }

    /** Los hábitos que pueden verse fuera de Privado: todos menos el ligado a las recaídas. */
    fun habitosVisibles(c: Config = Repo.config): List<Habito> {
        val h = habito(c) ?: return c.habitos
        return c.habitos.filter { it.id != h.id }
    }

    fun esVariable(id: String, c: Config = Repo.config): Boolean = esVariable(id, habito(c)?.id)

    /** La regla sin leer la configuración (para probarla sin la app). */
    fun esVariable(id: String, habitoLigado: String?): Boolean =
        id in VARIABLES || PREFIJOS.any { id.startsWith(it) } || (habitoLigado != null && id == "hab_$habitoLigado")

    /** Si hay algo privado guardado: entonces ningún respaldo sale del teléfono sin contraseña. */
    fun hayDatos(): Boolean = Repo.momentos.any { it.privado } ||
        Repo.dias.values.any { it.impulso != null || it.recaida != null || it.detonantes.isNotEmpty() || it.medicamentos.isNotEmpty() }
}
