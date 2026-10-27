package io.github.ruddy2003.vestigium.data

import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Una fecha que se repite cada año y que no es de nadie de Tu gente: el aniversario de algo, el cumpleaños de alguien
 * que no mencionas, un día que te importa (v13). [fecha] es AAAA-MM-DD, o --MM-DD si no sabes el año.
 * [tipo]: cumpleanos, aniversario u otra.
 */
@Serializable
data class FechaPropia(val id: String, val nombre: String, val fecha: String, val tipo: String = "otra")

/**
 * Días especiales (I-6) y edades de las personas (I-7): cumpleaños (el tuyo, de Conocerte, y los de Tu gente), aniversarios
 * de fallecimiento y fechas propias. Las fechas se guardan como AAAA-MM-DD o, sin año, --MM-DD. El 29 de febrero, en un año
 * que no es bisiesto, cae el 28.
 */
object Especiales {
    /** Lo que cae un día: de qué tipo es, de quién o qué, y cuántos años cumple (si se sabe el año). */
    class Especial(val tipo: String, val nombre: String, val anios: Int?)

    val TIPOS_PROPIOS = listOf(Opcion("cumpleanos", "Cumpleaños"), Opcion("aniversario", "Aniversario"), Opcion("otra", "Otra fecha"))

    /** AAAA-MM-DD, o --MM-DD sin año (la forma de ISO 8601 para una fecha que se repite). */
    private val FORMA = Regex("""^(?:(\d{4})-|--)(\d{2})-(\d{2})$""")

    /** El mes y día («03-15») de una fecha guardada; null si no es válida. */
    fun mesDia(f: String): String? {
        val m = FORMA.matchEntire(f) ?: return null
        val (a, mes, dia) = m.destructured
        val anio = a.toIntOrNull() ?: 2024   // 2024 es bisiesto: sin año, el 29 de febrero vale
        return runCatching { LocalDate.of(anio, mes.toInt(), dia.toInt()); "$mes-$dia" }.getOrNull()
    }

    fun anio(f: String): Int? = FORMA.matchEntire(f)?.groupValues?.get(1)?.toIntOrNull()

    /**
     * Lee lo que escribiste: «15/03/1962», «15/03», «15-3-1962» o la forma guardada. Devuelve la forma guardada
     * (AAAA-MM-DD o --MM-DD), "" si quedó vacío, o null si no se entiende.
     */
    fun leer(texto: String): String? {
        val t = texto.trim()
        if (t.isEmpty()) return ""
        if (mesDia(t) != null) return t
        val m = Regex("""^(\d{1,2})[/.\- ](\d{1,2})(?:[/.\- ](\d{4}))?$""").matchEntire(t) ?: return null
        val (d, mes, a) = m.destructured
        val f = (if (a.isEmpty()) "--" else "$a-") + "%02d-%02d".format(mes.toInt(), d.toInt())
        return f.takeIf { mesDia(it) != null }
    }

    /** Cómo se escribe para editarla: «15/03/1962» o «15/03». */
    fun paraEditar(f: String): String {
        val md = mesDia(f) ?: return ""
        return md.substring(3) + "/" + md.substring(0, 2) + (anio(f)?.let { "/$it" } ?: "")
    }

    /** «15 de marzo» (y el año, si se sabe). */
    fun texto(f: String, conAnio: Boolean = true): String {
        val md = mesDia(f) ?: return ""
        val mes = java.time.Month.of(md.take(2).toInt()).getDisplayName(java.time.format.TextStyle.FULL, Fechas.ES)
        return "${md.substring(3).toInt()} de $mes" + (if (conAnio) anio(f)?.let { " de $it" } ?: "" else "")
    }

    /** Si la fecha guardada [f] se celebra el día [dia] (AAAA-MM-DD). */
    fun cae(f: String, dia: String): Boolean {
        val md = mesDia(f) ?: return false
        val d = Fechas.parse(dia)
        if (md == "02-29" && !d.isLeapYear) return d.monthValue == 2 && d.dayOfMonth == 28
        return dia.substring(5) == md
    }

    /** Años cumplidos el día [en]: null si no se sabe el año. */
    fun edad(nacimiento: String, en: String): Int? {
        val a = anio(nacimiento) ?: return null
        val md = mesDia(nacimiento) ?: return null
        val d = Fechas.parse(en)
        val yaCumplio = d.toString().substring(5) >= md || (md == "02-29" && !d.isLeapYear && d.toString().substring(5) >= "02-28")
        return (d.year - a - if (yaCumplio) 0 else 1).takeIf { it >= 0 }
    }

    /** La edad de alguien de Tu gente hoy, o la que tenía al morir. */
    fun edadDe(p: Persona, hoy: String = Fechas.diaLogico()): Int? {
        if (p.nacimiento.isBlank()) return null
        // Si murió y no sabes el año, no se puede saber qué edad tenía.
        if (p.fallecimiento.isNotBlank() && anio(p.fallecimiento) == null) return null
        val hasta = p.fallecimiento.takeIf { it.isNotBlank() } ?: hoy
        return edad(p.nacimiento, hasta)
    }

    /** Todo lo que cae el día [dia]. */
    fun de(dia: String, c: Config = Repo.config): List<Especial> {
        val l = ArrayList<Especial>()
        val yo = c.perfil.nacimiento
        if (yo.isNotBlank() && cae(yo, dia)) l += Especial("mi_cumpleanos", "tu cumpleaños", anio(yo)?.let { Fechas.parse(dia).year - it })
        for (p in c.personas) {
            if (p.nacimiento.isNotBlank() && cae(p.nacimiento, dia)) {
                // De quien ya murió, el cumpleaños se recuerda pero no se le cuentan años.
                val anios = if (p.fallecimiento.isBlank()) anio(p.nacimiento)?.let { Fechas.parse(dia).year - it } else null
                l += Especial("cumpleanos", "@${p.nombre}", anios)
            }
            if (p.fallecimiento.isNotBlank() && cae(p.fallecimiento, dia))
                l += Especial("fallecimiento", "@${p.nombre}", anio(p.fallecimiento)?.let { Fechas.parse(dia).year - it }?.takeIf { it > 0 })
        }
        for (f in c.fechas_propias) if (cae(f.fecha, dia)) l += Especial(f.tipo, f.nombre, anio(f.fecha)?.let { Fechas.parse(dia).year - it }?.takeIf { it > 0 })
        return l
    }

    /** Si hay alguna fecha especial guardada. */
    fun hay(c: Config = Repo.config): Boolean =
        c.perfil.nacimiento.isNotBlank() || c.fechas_propias.isNotEmpty() || c.personas.any { it.nacimiento.isNotBlank() || it.fallecimiento.isNotBlank() }

    /** Una línea para el aviso o el Día: «cumpleaños de @ana (30)», «aniversario de @luis (5 años)». */
    fun frase(e: Especial): String = when (e.tipo) {
        "mi_cumpleanos" -> "tu cumpleaños" + (e.anios?.let { " ($it)" } ?: "")
        "cumpleanos" -> "cumpleaños de ${e.nombre}" + (e.anios?.let { " ($it)" } ?: "")
        "fallecimiento" -> "aniversario de ${e.nombre}" + (e.anios?.let { " (${if (it == 1) "1 año" else "$it años"})" } ?: "")
        "aniversario" -> "aniversario: ${e.nombre}" + (e.anios?.let { " (${if (it == 1) "1 año" else "$it años"})" } ?: "")
        else -> e.nombre
    }
}
