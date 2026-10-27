package io.github.ruddy2003.vestigium.data

/** Reglas para juntar lo que trae un respaldo con lo que ya hay en el teléfono, sin perder nada de lo de aquí. */
object Fusion {
    /** Los elementos de [lista] cuyo id no está en [existentes] (ni repetido dentro de la misma lista). */
    fun <T> nuevosPorId(existentes: List<T>, lista: List<T>, id: (T) -> String): List<T> {
        val ids = existentes.mapTo(HashSet()) { id(it) }
        return lista.filter { ids.add(id(it)) }
    }

    /**
     * Un experimento del respaldo contra el de aquí. Lo de aquí manda, pero no se pierde lo que solo trae el respaldo:
     * la fecha de fin (un respaldo viejo no reactiva un experimento terminado, ni uno nuevo se queda activo)
     * y las respuestas de «¿Lo hiciste hoy?». null = no hay nada que guardar.
     */
    fun experimento(aqui: Experimento?, respaldo: Experimento): Experimento? {
        if (aqui == null) return respaldo
        val fin = aqui.fin ?: respaldo.fin
        val n = aqui.copy(fin = fin, activo = aqui.activo && fin == null && respaldo.activo, hecho = respaldo.hecho + aqui.hecho)
        return n.takeIf { it != aqui }
    }

    /**
     * El registro de salud de un día del respaldo contra el de aquí. El respaldo manda (trae lo que midió el reloj), salvo
     * el sueño que corregiste aquí a mano: ese no lo pisa un respaldo que no lo tenía corregido.
     */
    fun salud(aqui: SaludDia?, respaldo: SaludDia): SaludDia {
        if (aqui == null) return respaldo
        // Lo que dijiste que no cuenta se respeta venga de donde venga (N-04).
        val descartadas = (respaldo.sueno_descartadas + aqui.sueno_descartadas).distinct()
        if (!aqui.sueno_manual || respaldo.sueno_manual) return respaldo.copy(sueno_descartadas = descartadas)
        return respaldo.copy(sueno_min = aqui.sueno_min, sueno_inicio = aqui.sueno_inicio, sueno_fin = aqui.sueno_fin,
            sueno_despierto_min = aqui.sueno_despierto_min, sueno_profundo_min = aqui.sueno_profundo_min, sueno_rem_min = aqui.sueno_rem_min,
            sueno_ligero_min = aqui.sueno_ligero_min, sueno_manual = true, sueno_descartadas = descartadas)
    }
}
