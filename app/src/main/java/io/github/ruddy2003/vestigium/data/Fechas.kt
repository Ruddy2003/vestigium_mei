package io.github.ruddy2003.vestigium.data

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * Todas las fechas se manejan en hora local, nunca en UTC. La hora local de un instante es la del lugar donde estabas entonces
 * (H-1-7): la app guarda cada cambio de zona del teléfono ([CambioZona]) y convierte cada instante con la zona que tenía en ese
 * momento, no con la de hoy. Así un viaje o un cambio de horario no mueve lo que ya pasó a otra hora ni a otro día.
 */
object Fechas {
    val ES: Locale = Locale.forLanguageTag("es-MX")
    private val ISO_DIA: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val HORA: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    /** La zona del teléfono ahora. Para un instante pasado se usa [zonaEn]. */
    fun zona(): ZoneId = ZoneId.systemDefault()

    // El historial de zonas, ordenado: desde qué instante (ms) rige cada una.
    @Volatile private var desdes: LongArray = LongArray(0)
    @Volatile private var zonas: Array<ZoneId> = emptyArray()

    /** Toma el historial de la configuración. Vacío = siempre la zona de ahora (como en las pruebas y las capturas). */
    fun usarHistorial(l: List<CambioZona>) {
        val p = l.mapNotNull { c -> runCatching { millisEstricto(c.desde) to ZoneId.of(c.zona) }.getOrNull() }.sortedBy { it.first }
        zonas = p.map { it.second }.toTypedArray(); desdes = p.map { it.first }.toLongArray()
    }

    /** La zona que tenía el teléfono en el instante [ms]. Antes del primer cambio guardado, la primera zona del historial. */
    fun zonaEn(ms: Long): ZoneId {
        val d = desdes; val z = zonas
        if (d.isEmpty() || z.size != d.size) return zona()
        var lo = 0; var hi = d.size - 1
        if (ms < d[0]) return z[0]
        while (lo < hi) { val m = (lo + hi + 1) / 2; if (d[m] <= ms) lo = m else hi = m - 1 }
        return z[lo]
    }

    /** Hora local de un instante, en la zona que tenía el teléfono entonces. */
    fun local(ms: Long): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), zonaEn(ms))

    /**
     * Hora local de una marca ISO: la de su propio desfase, que es la del lugar donde se tomó la nota. Las marcas en UTC (Z), que
     * vienen de importaciones, se pasan a la zona que tenía el teléfono en ese instante.
     */
    fun localDe(iso: String): LocalDateTime {
        val t = OffsetDateTime.parse(iso)
        return if (t.offset == ZoneOffset.UTC) local(t.toInstant().toEpochMilli()) else t.toLocalDateTime()
    }

    /** El instante de una hora local: con la zona que regía ese día (si el cambio fue justo ese día, la de esa hora). */
    fun millisDe(t: LocalDateTime): Long {
        if (desdes.size <= 1) return t.atZone(zonaEn(0)).toInstant().toEpochMilli()
        val z0 = zonaEn(t.toInstant(ZoneOffset.UTC).toEpochMilli())
        val ms0 = t.atZone(z0).toInstant().toEpochMilli()
        val z1 = zonaEn(ms0)
        return if (z1 == z0) ms0 else t.atZone(z1).toInstant().toEpochMilli()
    }

    /** Horas de diferencia con UTC en el instante [ms] (−6 en la Ciudad de México). */
    fun desfaseH(ms: Long): Double = zonaEn(ms).rules.getOffset(Instant.ofEpochMilli(ms)).totalSeconds / 3600.0

    /**
     * Si en el día lógico [s] cambió la hora del teléfono (horario de verano o un viaje a otra zona): el desfase con UTC al empezar
     * y al terminar el día. Null si no cambió.
     */
    fun cambioDeHora(s: String): Pair<Double, Double>? {
        val a = desfaseH(inicioDiaLogico(s)); val b = desfaseH(finDiaLogico(s) - 1)
        return if (a != b) a to b else null
    }

    private fun millisEstricto(iso: String): Long = OffsetDateTime.parse(iso).toInstant().toEpochMilli()

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
    /**
     * Fecha y hora con segundos siempre (2026-10-16T18:32:00-06:00). `OffsetDateTime.toString()` los quitaba cuando eran
     * :00 (2026-10-16T18:32-06:00), y Excel o algunos lectores de Python no leían esas filas. Lo ya guardado se queda como
     * está: los dos formatos son ISO 8601 y la app y vestigium.R leen ambos.
     */
    private val ISO_SEGUNDOS: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssXXX", Locale.ROOT)
    fun ahoraIso(): String = OffsetDateTime.now().withNano(0).format(ISO_SEGUNDOS)
    fun isoDeMillis(ms: Long): String = OffsetDateTime.ofInstant(Instant.ofEpochMilli(ms), zonaEn(ms)).withNano(0).format(ISO_SEGUNDOS)
    fun millis(iso: String): Long = try { OffsetDateTime.parse(iso).toInstant().toEpochMilli() } catch (e: Exception) { 0L }
    fun horaDe(iso: String): String = try { localDe(iso).format(HORA) } catch (e: Exception) { "" }
    fun horaAhora(): String = LocalDateTime.now().format(HORA)
    fun diaDeMillis(ms: Long): String = iso(local(ms).toLocalDate())
    fun diaLogicoDeMillis(ms: Long): String = diaLogico(local(ms))

    /**
     * De dónde a dónde va el día lógico [s]: de las 05:00 de ese día a las 05:00 del siguiente, cada una en la hora local de
     * entonces (un día con cambio de horario dura 23 o 25 horas).
     */
    fun inicioDiaLogico(s: String): Long = millisDe(parse(s).atTime(5, 0))
    fun finDiaLogico(s: String): Long = millisDe(parse(s).plusDays(1).atTime(5, 0))
    fun inicioDia(s: String): Long = millisDe(parse(s).atStartOfDay())
    fun finDia(s: String): Long = millisDe(parse(s).plusDays(1).atStartOfDay())

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
            dif < 0 -> "hace ${porNumero(-dif, "1 día", "${-dif} días")}"
            else -> "en ${porNumero(dif, "1 día", "$dif días")}"
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
