package io.github.ruddy2003.vestigium.data

/** Los avisos de ánimo: cuáles quedan sin contestar hoy y las cifras de cada día para el panel (P-10). */
object AvisosAnimo {
    /** Las horas de los avisos de hoy que ya pasaron y no tienen respuesta, de la más antigua a la más reciente. */
    fun sinContestar(ms: Long = System.currentTimeMillis()): List<Long> {
        val resp = respondidos(Repo.momentos)
        return Repo.config.plan_muestreo[Fechas.hoyCalendario()].orEmpty().filter { it <= ms && Fechas.isoDeMillis(it) !in resp }.sorted()
    }

    /** Las horas de aviso que ya tienen respuesta: un registro «muestreo» con ese aviso, tenga o no valencia. */
    fun respondidos(momentos: List<Momento>): Set<String> = momentos.filter { it.tipo == "muestreo" }.mapNotNullTo(HashSet()) { it.aviso_ts }

    /**
     * Las cifras de un día: planeados, mostrados, respondidos (de los mostrados) y la tasa de respuesta
     * (respondidos ÷ mostrados; sin dato si no se mostró ninguno).
     */
    fun cifras(a: AvisosDia, respondidos: Set<String>): Map<String, Double?> {
        val r = a.mostrados.count { it in respondidos }
        return mapOf("avisos_planeados" to a.planeados.toDouble(), "avisos_mostrados" to a.mostrados.size.toDouble(), "avisos_respondidos" to r.toDouble(),
            "tasa_respuesta" to if (a.mostrados.isEmpty()) null else r.toDouble() / a.mostrados.size)
    }
}
