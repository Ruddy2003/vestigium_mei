package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import java.time.LocalDate
import java.time.LocalTime

/**
 * Psicodelia (lote 10; `docs/diseno/temas/psicodelia.md`): lo que el tema calcula para sus textos. Todo sale de la fecha
 * (y, el día de hoy, de la hora), así que el mismo día dice siempre lo mismo y no se guarda nada.
 * - **El día como disco** (recurso): cada día del año es un disco, «Disco 290 · sábado 17 de octubre».
 * - **El lado y la pista** (recurso): el lado A va de las 0 a las 11 y el lado B de las 12 a las 23; la pista es la hora
 *   dentro de su lado, de la 1 a la 12.
 * - **El mes:** «Álbum de octubre».
 * - **Los números** (versión 2): entre paréntesis, como en un horario o en la lista de canciones de un disco, «(17)»; iguales
 *   en las dos caras, para que un dato no cambie de aspecto con la hora.
 */
object Psicodelia {
    private val DIAS = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo")
    private val MESES = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")

    /** El número del disco: el día del año, de 1 a 366. */
    fun disco(d: LocalDate): Int = d.dayOfYear

    /** El lado del disco a una hora: «A» de las 0 a las 11, «B» de las 12 a las 23. */
    fun lado(h: Int): String = if (h < 12) "A" else "B"

    /** La pista: la hora dentro de su lado, de la 1 a la 12 (a las 0 y a las 12, la pista 1). */
    fun pista(h: Int): Int = h % 12 + 1

    /** «Disco 290 · sábado 17 de octubre»; el día de hoy, con el lado y la pista: «… · lado B, pista 10». */
    fun fecha(iso: String, ahora: LocalTime? = null): String = runCatching {
        val d = Fechas.parse(iso)
        val base = "Disco ${disco(d)} · ${DIAS[d.dayOfWeek.value - 1]} ${d.dayOfMonth} de ${MESES[d.monthValue - 1]}"
        val hoy = ahora ?: if (iso == Fechas.diaLogico()) LocalTime.now() else null
        if (hoy != null) "$base · lado ${lado(hoy.hour)}, pista ${pista(hoy.hour)}" else base
    }.getOrDefault(iso)

    /** El mes como un álbum: «Álbum de octubre», «Álbum de octubre de 2026». */
    fun mes(n: Int, anio: Int? = null): String = "Álbum de " + MESES[(n - 1).coerceIn(0, 11)] + (anio?.let { " de $it" } ?: "")

    /** Un número entre paréntesis, como en un horario: «(17)». Los años y lo que pasa de 999, tal cual. */
    fun numero(n: Int): String = if (n in 1..999) "($n)" else n.toString()
}
