package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import java.time.LocalDate

/**
 * Los días especiales de los personajes, para seguirlos (parte 8 del plan de mejoras, `docs/diseno/CUADROS_PLAN.md`): cuáles
 * tocan hoy y cuáles ya pasaron, para leerlos después. Kotlin puro: sale de la fecha y del calendario, como la ficha, y no
 * lee ni guarda nada tuyo (qué personajes sigues lo da quien llama, con `Seguir.activos`).
 */
object DiasEspeciales {
    /**
     * Desde qué día se ofrecen los días especiales pasados: el principio del año del avance (2026). **[Diseño propio]**.
     * Quien llama puede pedir otro con `desde`.
     */
    const val PRIMER_DIA = "2026-01-01"

    /** Un día especial de [vida] con su fecha real: empezó el [inicio] y dura `dia.duracion` días. */
    data class Especial(val vida: String, val dia: DiaEspecial, val inicio: LocalDate) {
        val id: String get() = dia.id
        val titulo: String get() = dia.titulo
        /** El último día que dura. */
        val fin: LocalDate get() = inicio.plusDays(dia.duracion - 1L)
        /** El día del cuento que se abre para leerlo: el primero, el que tiene su texto propio. */
        val diaDelCuento: String get() = inicio.toString()
    }

    private fun fecha(s: String): LocalDate? = runCatching { LocalDate.parse(s) }.getOrNull()

    /** Los días especiales que tocan a [vida] el día lógico [hoy], el que manda primero (los mismos de su ficha); vacío si ninguno. */
    fun hoy(vida: String, hoy: String): List<EspecialDelDia> = if (fecha(hoy) == null) emptyList() else FichaDelDia.de(hoy, vida)?.especiales.orEmpty()

    /**
     * De [vidas] (los que sigues), los que hoy tienen un día especial, en el mismo orden, con sus días especiales. Es lo que
     * decide la banderita de Hoy y la notificación.
     */
    fun conEspecialHoy(vidas: List<String>, hoy: String): List<Pair<String, List<EspecialDelDia>>> =
        vidas.distinct().mapNotNull { v -> hoy(v, hoy).takeIf { it.isNotEmpty() }?.let { v to it } }

    /**
     * Los días especiales de [vida] que ya empezaron: desde [desde] hasta el día lógico [hoy] (también el que empieza hoy),
     * del más reciente al más viejo. Los futuros no salen nunca. Vacío si el personaje no tiene calendario o las fechas no
     * valen. Desde 2032, con los sucesos sorteados de nuevo, salen en su fecha sorteada (la misma que usa la ficha).
     */
    fun pasados(vida: String, hoy: String, desde: String = PRIMER_DIA): List<Especial> {
        val cal = Calendarios.de(vida) ?: return emptyList()
        val h = fecha(hoy) ?: return emptyList()
        val d = fecha(desde) ?: return emptyList()
        if (d.isAfter(h)) return emptyList()
        return (d.year..h.year).flatMap { anio -> FichaDelDia.inicios(cal, anio).map { (dia, inicio) -> Especial(vida, dia, inicio) } }
            .filter { !it.inicio.isBefore(d) && !it.inicio.isAfter(h) }
            .distinctBy { it.id to it.inicio }
            .sortedWith(compareByDescending<Especial> { it.inicio }.thenBy { it.id })
    }
}
