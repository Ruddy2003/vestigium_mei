package io.github.ruddy2003.vestigium.data

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/** Todas las fechas se manejan en hora local del teléfono, nunca en UTC. */
object Fechas {
    val ES: Locale = Locale.forLanguageTag("es-MX")
    private val ISO_DIA: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val HORA: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun zona(): ZoneId = ZoneId.systemDefault()

    /** Antes de las 5:00 se sigue contando el día anterior. */
    fun diaLogico(ahora: LocalDateTime = LocalDateTime.now()): String {
        val d = if (ahora.hour < 5) ahora.toLocalDate().minusDays(1) else ahora.toLocalDate()
        return d.format(ISO_DIA)
    }

    fun hoyCalendario(): String = LocalDate.now().format(ISO_DIA)
    fun parse(s: String): LocalDate = LocalDate.parse(s.take(10), ISO_DIA)
    fun iso(d: LocalDate): String = d.format(ISO_DIA)
    fun sumar(s: String, dias: Long): String = iso(parse(s).plusDays(dias))
    fun diasEntre(a: String, b: String): Long = java.time.temporal.ChronoUnit.DAYS.between(parse(a), parse(b))

    /** Marca de tiempo con desfase local, por ejemplo 2026-10-16T08:42:10-06:00. */
    fun ahoraIso(): String = OffsetDateTime.now().withNano(0).toString()
    fun isoDeMillis(ms: Long): String = OffsetDateTime.ofInstant(Instant.ofEpochMilli(ms), zona()).withNano(0).toString()
    fun millis(iso: String): Long = try { OffsetDateTime.parse(iso).toInstant().toEpochMilli() } catch (e: Exception) { 0L }
    fun horaDe(iso: String): String = try { OffsetDateTime.parse(iso).atZoneSameInstant(zona()).format(HORA) } catch (e: Exception) { "" }
    fun horaAhora(): String = LocalDateTime.now().format(HORA)
    fun diaDeMillis(ms: Long): String = iso(Instant.ofEpochMilli(ms).atZone(zona()).toLocalDate())
    fun diaLogicoDeMillis(ms: Long): String = diaLogico(LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), zona()))

    /** De dónde a dónde va el día lógico [s]: de las 05:00 de ese día a las 05:00 del siguiente. */
    fun inicioDiaLogico(s: String): Long = parse(s).atTime(5, 0).atZone(zona()).toInstant().toEpochMilli()
    fun finDiaLogico(s: String): Long = parse(s).plusDays(1).atTime(5, 0).atZone(zona()).toInstant().toEpochMilli()
    fun inicioDia(s: String): Long = parse(s).atStartOfDay(zona()).toInstant().toEpochMilli()
    fun finDia(s: String): Long = parse(s).plusDays(1).atStartOfDay(zona()).toInstant().toEpochMilli()

    fun titulo(s: String): String {
        val d = parse(s)
        val dia = d.dayOfWeek.getDisplayName(TextStyle.FULL, ES).replaceFirstChar { it.uppercase() }
        val mes = d.month.getDisplayName(TextStyle.FULL, ES)
        return "$dia, ${d.dayOfMonth} de $mes"
    }
    /** «18 de octubre de 2026». */
    fun larga(s: String): String {
        val d = parse(s)
        return "${d.dayOfMonth} de ${d.month.getDisplayName(TextStyle.FULL, ES)} de ${d.year}"
    }
    fun corto(s: String): String {
        val d = parse(s)
        return d.dayOfWeek.getDisplayName(TextStyle.SHORT, ES).replace(".", "") + " " + d.dayOfMonth + " " +
            d.month.getDisplayName(TextStyle.SHORT, ES).replace(".", "")
    }
    fun mesTitulo(ym: String): String {
        val d = parse("$ym-01")
        return d.month.getDisplayName(TextStyle.FULL, ES).replaceFirstChar { it.uppercase() } + " " + d.year
    }
    fun relativo(s: String): String {
        val dif = java.time.temporal.ChronoUnit.DAYS.between(parse(diaLogico()), parse(s))
        return when {
            dif == 0L -> "hoy"
            dif == -1L -> "ayer"
            dif == 1L -> "mañana"
            dif < 0 -> "hace ${-dif} días"
            else -> "en $dif días"
        }
    }
    /**
     * Días de descanso obligatorio en México (Ley Federal del Trabajo, art. 74): 1 de enero, primer lunes de febrero, tercer lunes
     * de marzo, 1 de mayo, 16 de septiembre, tercer lunes de noviembre, 25 de diciembre y el día de la transmisión del Poder
     * Ejecutivo Federal (cada seis años: el 1 de octubre desde 2024; antes, el 1 de diciembre). Antes de 2006 los de febrero,
     * marzo y noviembre iban en su fecha fija (5 de febrero, 21 de marzo y 20 de noviembre). La jornada electoral se omite
     * (cae en domingo) y Jueves y Viernes Santo no están en la ley (I-1).
     */
    fun esFestivoMx(s: String): Boolean {
        val d = parse(s); val a = d.year; val m = d.monthValue; val dia = d.dayOfMonth
        fun lunes(n: Int) = d.dayOfWeek == DayOfWeek.MONDAY && (dia - 1) / 7 == n - 1
        return when (m) {
            1 -> dia == 1
            2 -> if (a >= 2006) lunes(1) else dia == 5
            3 -> if (a >= 2006) lunes(3) else dia == 21
            5 -> dia == 1
            9 -> dia == 16
            10 -> a >= 2024 && a % 6 == 2 && dia == 1
            11 -> if (a >= 2006) lunes(3) else dia == 20
            12 -> dia == 25 || (a < 2024 && a % 6 == 2 && dia == 1)
            else -> false
        }
    }

    /**
     * Día libre: sábado, domingo o, si vives en México ([mexico]), un festivo de la ley. Es la única regla de la app (ritmo de
     * sueño, panel, exportación): una noche es libre si el día en que despiertas es libre (P-19).
     */
    fun esDiaLibre(s: String, mexico: Boolean): Boolean = parse(s).dayOfWeek.value >= 6 || (mexico && esFestivoMx(s))

    fun lunesDe(s: String): String = iso(parse(s).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    fun mesDe(s: String): String = s.take(7)
}
