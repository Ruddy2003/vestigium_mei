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
        // Si lo de aquí se leyó después que lo del respaldo, manda lo de aquí (un respaldo viejo no pisa una lectura más nueva);
        // lo que aquí falte y el respaldo sí traiga, se completa. Sin las dos horas de lectura, sigue mandando el respaldo.
        if (masNuevo(aqui.ts, respaldo.ts)) return completar(aqui, respaldo).copy(sueno_descartadas = (respaldo.sueno_descartadas + aqui.sueno_descartadas).distinct())
        // Lo que dijiste que no cuenta se respeta venga de donde venga (N-04).
        val descartadas = (respaldo.sueno_descartadas + aqui.sueno_descartadas).distinct()
        if (!aqui.sueno_manual || respaldo.sueno_manual) return respaldo.copy(sueno_descartadas = descartadas)
        return respaldo.copy(sueno_min = aqui.sueno_min, sueno_inicio = aqui.sueno_inicio, sueno_fin = aqui.sueno_fin,
            sueno_despierto_min = aqui.sueno_despierto_min, sueno_profundo_min = aqui.sueno_profundo_min, sueno_rem_min = aqui.sueno_rem_min,
            sueno_ligero_min = aqui.sueno_ligero_min, sueno_manual = true, sueno_descartadas = descartadas)
    }

    /** Si la marca de tiempo [a] es posterior a [b]. Sin alguna de las dos (registros viejos), no se puede saber: false. */
    private fun masNuevo(a: String?, b: String?): Boolean {
        val x = a?.let { Fechas.millis(it) } ?: return false
        val y = b?.let { Fechas.millis(it) } ?: return false
        return x > 0 && y > 0 && x > y
    }

    /**
     * [gana] con los huecos rellenados con lo que [otro] sí trae. El sueño de la noche va en bloque (minutos, horas y etapas
     * son de una misma lectura): solo se toma de [otro] si [gana] no la corregiste a mano y, o bien no tiene noche, o bien
     * la de [otro] sí está corregida a mano (una corrección tuya nunca la pisa una lectura del reloj, por nueva que sea).
     */
    private fun completar(gana: SaludDia, otro: SaludDia): SaludDia {
        val g = if (!gana.sueno_manual && (otro.sueno_manual || (gana.sueno_min == null && otro.sueno_min != null))) gana.copy(
            sueno_min = otro.sueno_min, sueno_profundo_min = otro.sueno_profundo_min, sueno_rem_min = otro.sueno_rem_min, sueno_ligero_min = otro.sueno_ligero_min,
            sueno_despierto_min = otro.sueno_despierto_min, sueno_inicio = otro.sueno_inicio, sueno_fin = otro.sueno_fin, sueno_manual = otro.sueno_manual,
            sueno_fuentes = gana.sueno_fuentes.ifEmpty { otro.sueno_fuentes }, siesta_reloj_min = gana.siesta_reloj_min ?: otro.siesta_reloj_min) else gana
        val conPasos = if (g.pasos == null && otro.pasos != null) g.copy(pasos = otro.pasos, ventana_pasos = otro.ventana_pasos) else g
        return conPasos.copy(
            fc_media = g.fc_media ?: otro.fc_media, fc_min = g.fc_min ?: otro.fc_min, fc_max = g.fc_max ?: otro.fc_max, fc_reposo = g.fc_reposo ?: otro.fc_reposo,
            hrv_rmssd = g.hrv_rmssd ?: otro.hrv_rmssd, spo2 = g.spo2 ?: otro.spo2, peso_kg = g.peso_kg ?: otro.peso_kg, grasa_pct = g.grasa_pct ?: otro.grasa_pct,
            resp_rpm = g.resp_rpm ?: otro.resp_rpm, temp_c = g.temp_c ?: otro.temp_c, temp_piel_delta = g.temp_piel_delta ?: otro.temp_piel_delta,
            pa_sistolica = g.pa_sistolica ?: otro.pa_sistolica, pa_diastolica = g.pa_diastolica ?: otro.pa_diastolica, distancia_km = g.distancia_km ?: otro.distancia_km,
            cal_activas = g.cal_activas ?: otro.cal_activas, cal_totales = g.cal_totales ?: otro.cal_totales, pisos = g.pisos ?: otro.pisos, vo2max = g.vo2max ?: otro.vo2max,
            agua_ml = g.agua_ml ?: otro.agua_ml, spo2_min = g.spo2_min ?: otro.spo2_min, fc_sueno = g.fc_sueno ?: otro.fc_sueno)
    }

    /**
     * Lo que se guarda al terminar de leer el reloj. [base] es lo que había al empezar, [actual] lo que hay ahora y [leido] lo
     * recién leído (armado sobre [base]). Si nada cambió entretanto, vale lo leído. Si mientras tanto tocaste ese día (la
     * lectura tarda), tu cambio se respeta: lo que marcaste «no cuenta», y la noche corregida a mano o vuelta al reloj.
     */
    fun saludLeida(base: SaludDia, actual: SaludDia, leido: SaludDia): SaludDia {
        if (actual == base) return leido
        val n = leido.copy(sueno_descartadas = actual.sueno_descartadas, sueno_manual = actual.sueno_manual)
        if (!actual.sueno_manual) return n
        return n.copy(sueno_min = actual.sueno_min, sueno_inicio = actual.sueno_inicio, sueno_fin = actual.sueno_fin, sueno_despierto_min = actual.sueno_despierto_min,
            sueno_profundo_min = actual.sueno_profundo_min, sueno_rem_min = actual.sueno_rem_min, sueno_ligero_min = actual.sueno_ligero_min)
    }
}
