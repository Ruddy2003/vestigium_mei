package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import java.time.LocalDate
import java.time.LocalTime

/**
 * Art nouveau (lote 10; `docs/diseno/temas/nouveau.md`): lo que el tema calcula para sus textos. Todo sale de la fecha
 * (y, el día de hoy, de la hora), así que el mismo día dice siempre lo mismo y no se guarda nada.
 * - **La página del almanaque:** el día en francés con su flor y lo que significa, de *Le Langage des fleurs* (1819),
 *   firmado «Charlotte de La Tour» (seudónimo). Recurso: el libro no trae una flor para cada día; la app las reparte por
 *   el año ([flor]). La lista y su fuente, en `docs/diseno/temas/fuentes_nouveau.md`.
 * - **La hora de París:** la hora legal de Francia de 1891 a 1911. Recurso: la app pone la hora del teléfono.
 * - **Los números:** el folio de una revista ilustrada, entre rayas: «– 17 –».
 */
object Nouveau {
    private val DIAS = listOf("lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche")
    private val MESES = listOf("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre")

    /** El día como en la página del almanaque: «samedi 17 octobre». */
    fun dia(d: LocalDate): String = "${DIAS[d.dayOfWeek.value - 1]} ${d.dayOfMonth} ${MESES[d.monthValue - 1]}"

    /**
     * La hora a la francesa de 1900, con la parte del día: «9 h 41 du matin», «2 h 05 de l'après-midi», «11 h 30 du
     * soir». De las 0 a las 5, de la noche; de las 6 a las 11, de la mañana; de las 12 a las 17, de la tarde; lo demás, de
     * la noche que empieza.
     */
    fun hora(h: Int, m: Int): String {
        val h12 = if (h % 12 == 0) 12 else h % 12
        val parte = when (h) {
            in 0..5 -> "de la nuit"
            in 6..11 -> "du matin"
            in 12..17 -> "de l'après-midi"
            else -> "du soir"
        }
        return "%d h %02d %s".format(h12, m, parte)
    }

    /** «samedi 17 octobre · Bruyère : solitude»; el día de hoy, con la hora: «… · 9 h 41 du matin, heure de Paris». */
    fun fecha(iso: String, ahora: LocalTime? = null): String = runCatching {
        val d = Fechas.parse(iso)
        val f = flor(d)
        val base = "${dia(d)} · ${f.nombre} : ${f.sentido}"
        val hoy = ahora ?: if (iso == Fechas.diaLogico()) LocalTime.now() else null
        if (hoy != null) "$base · ${hora(hoy.hour, hoy.minute)}, heure de Paris" else base
    }.getOrDefault(iso)

    /** El mes como el almanaque: «Almanach de juillet», «Almanach d'avril 2026». */
    fun mes(n: Int, anio: Int? = null): String {
        val m = MESES[(n - 1).coerceIn(0, 11)]
        val de = if (m.first() in "aeiou") "d'" else "de "
        return "Almanach $de$m" + (anio?.let { " $it" } ?: "")
    }

    /** El folio de una revista ilustrada, entre rayas cortas: «– 17 –». Los años y lo que pasa de 999, tal cual. */
    fun numero(n: Int): String = if (n in 1..999) "– $n –" else n.toString()

    // ------------------------------------------------------------------------------------------------------------
    //  La flor del día
    // ------------------------------------------------------------------------------------------------------------

    /** Una flor del libro de La Tour: su nombre y lo que significa, como los escribe el libro. */
    data class Flor(val nombre: String, val sentido: String)
}
