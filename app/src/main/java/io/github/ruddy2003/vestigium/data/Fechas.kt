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

    /** Marca de tiempo con desfase local, por ejemplo 2026-10-16T08:42:10-06:00. */
    fun ahoraIso(): String = OffsetDateTime.now().withNano(0).toString()
    fun isoDeMillis(ms: Long): String = OffsetDateTime.ofInstant(Instant.ofEpochMilli(ms), zona()).withNano(0).toString()
    fun millis(iso: String): Long = try { OffsetDateTime.parse(iso).toInstant().toEpochMilli() } catch (e: Exception) { 0L }
    fun horaDe(iso: String): String = try { OffsetDateTime.parse(iso).atZoneSameInstant(zona()).format(HORA) } catch (e: Exception) { "" }
    fun horaAhora(): String = LocalDateTime.now().format(HORA)
    fun diaDeMillis(ms: Long): String = iso(Instant.ofEpochMilli(ms).atZone(zona()).toLocalDate())
    fun diaLogicoDeMillis(ms: Long): String = diaLogico(LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), zona()))

    fun inicioDia(s: String): Long = parse(s).atStartOfDay(zona()).toInstant().toEpochMilli()
    fun finDia(s: String): Long = parse(s).plusDays(1).atStartOfDay(zona()).toInstant().toEpochMilli()

    fun titulo(s: String): String {
        val d = parse(s)
        val dia = d.dayOfWeek.getDisplayName(TextStyle.FULL, ES).replaceFirstChar { it.uppercase() }
        val mes = d.month.getDisplayName(TextStyle.FULL, ES)
        return "$dia, ${d.dayOfMonth} de $mes"
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
    fun lunesDe(s: String): String = iso(parse(s).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    fun mesDe(s: String): String = s.take(7)
}
