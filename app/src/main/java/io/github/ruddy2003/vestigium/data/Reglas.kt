package io.github.ruddy2003.vestigium.data

/** Reglas pequeñas del guardado de un día, aparte para poder probarlas sin teléfono. */
object Reglas {
    /** La hora de la valencia (P-13): ahora si pasó de vacía a llena o cambió; la que ya tenía si no; ninguna si se borró. */
    fun tsValencia(d: Dia, previo: Dia?, ahora: String): String? = when {
        d.valencia == null -> null
        d.valencia != previo?.valencia -> ahora
        else -> d.ts_valencia ?: previo?.ts_valencia
    }

    /**
     * Retroactivo = la valencia se llenó otro día lógico, después (sesgo de recuerdo). Sin hora de la valencia (días de
     * antes de la v13, o sin valencia), se conserva lo que ya tenía el día o, si es nuevo, si se creó después de su fecha.
     */
    fun retroactivo(fecha: String, tsValencia: String?, previo: Boolean?, hoy: String): Boolean =
        tsValencia?.let { Fechas.diaLogicoDeMillis(Fechas.millis(it)) > fecha } ?: previo ?: (fecha < hoy)
}
