package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Arcontes (lote 10; `docs/diseno/temas/arcontes.md`): lo que el tema calcula para sus textos. Todo sale solo de la
 * fecha, así que el mismo día dice siempre lo mismo y no se guarda nada.
 * - **El cielo del día:** Ireneo (*Contra las herejías* 1.24.3–7) cuenta que Basílides enseñaba 365 cielos, e Hipólito
 *   (*Refutación* 7.26), que por eso el año tiene 365 días. Recurso: el día 366 de un año bisiesto se muestra como el
 *   cielo 365.
 * - **El planeta del día:** la semana planetaria antigua.
 * - **La fecha hebrea:** el calendario hebreo calculado (el de hoy). Recurso: en el siglo I el mes empezaba cuando se
 *   veía la luna nueva, así que la fecha real podía correrse uno o dos días; y el día hebreo empieza al ponerse el sol,
 *   pero aquí sigue al día de la app.
 * - **Los números:** con letras hebreas que valen números.
 */
object Arcontes {
    /** El cielo del día, de 1 a 365. */
    fun cielo(d: LocalDate): Int = d.dayOfYear.coerceAtMost(365)

    /** El planeta que da nombre al día en la semana planetaria antigua. */
    fun planeta(d: LocalDate): String = when (d.dayOfWeek) {
        DayOfWeek.MONDAY -> "Luna"
        DayOfWeek.TUESDAY -> "Marte"
        DayOfWeek.WEDNESDAY -> "Mercurio"
        DayOfWeek.THURSDAY -> "Júpiter"
        DayOfWeek.FRIDAY -> "Venus"
        DayOfWeek.SATURDAY -> "Saturno"
        DayOfWeek.SUNDAY -> "Sol"
    }

    /** «Cielo 290 · Saturno · 6 de jeshván». */
    fun fecha(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        val h = hebrea(d)
        "Cielo ${cielo(d)} · ${planeta(d)} · ${h.dia} de ${h.nombreMes}"
    }.getOrDefault(iso)

    // ------------------------------------------------------------------------------------------------------------
    //  Números con letras hebreas
    // ------------------------------------------------------------------------------------------------------------

    private val UNIDADES = listOf("", "א", "ב", "ג", "ד", "ה", "ו", "ז", "ח", "ט")
    private val DECENAS = listOf("", "י", "כ", "ל", "מ", "נ", "ס", "ע", "פ", "צ")
    private val CENTENAS = listOf("", "ק", "ר", "ש", "ת", "תק", "תר", "תש", "תת", "תתק")
    private const val GERESH = "׳"
    private const val GERSHAYIM = "״"

    /**
     * Un número con letras hebreas (de 1 a 9999), como en los calendarios judíos: el gershayim (״) antes de la última
     * letra si hay más de una (כ״ג = 23) y el geresh (׳) detrás si es una sola (ה׳ = 5). El 15 y el 16 se escriben
     * ט״ו y ט״ז, para no formar el comienzo del nombre de Dios. Los miles van con la letra de las unidades y un geresh
     * delante del resto (ה׳תשפ״ז = 5787). Fuera de ese rango, con cifras.
     */
    fun numero(n: Int): String {
        if (n <= 0 || n >= 10000) return n.toString()
        val miles = n / 1000
        val resto = n % 1000
        val cuerpo = letras(resto)
        val conSigno = when {
            cuerpo.isEmpty() -> ""
            cuerpo.length == 1 -> cuerpo + GERESH
            else -> cuerpo.dropLast(1) + GERSHAYIM + cuerpo.last()
        }
        return if (miles > 0) UNIDADES[miles] + GERESH + conSigno else conSigno
    }

    private fun letras(n: Int): String {
        val c = n / 100
        val dyu = n % 100
        val du = when (dyu) {
            15 -> "טו"
            16 -> "טז"
            else -> DECENAS[dyu / 10] + UNIDADES[dyu % 10]
        }
        return CENTENAS[c] + du
    }

    // ------------------------------------------------------------------------------------------------------------
    //  El calendario hebreo (el aritmético de hoy), según Dershowitz y Reingold, *Calendrical Calculations*.
    //  Los meses se numeran desde nisán (1) hasta adar (12) y, en año embolismal, adar II (13); el año empieza en
    //  tishréi (7). Las fechas fijas cuentan días desde el 1 de enero del año 1 (proléptico gregoriano) = 1.
    // ------------------------------------------------------------------------------------------------------------

    /** Una fecha hebrea: año desde la creación, mes (nisán = 1) y día. */
    data class Hebrea(val anio: Int, val mes: Int, val dia: Int) {
        /** El nombre del mes en español; en año embolismal, adar I y adar II. */
        val nombreMes: String get() = when {
            mes == 12 && embolismal(anio) -> "adar I"
            mes == 13 -> "adar II"
            else -> MESES[mes - 1]
        }
    }

    private val MESES = listOf("nisán", "iyar", "siván", "tamuz", "av", "elul", "tishréi", "jeshván", "kislev", "tevet", "shevat", "adar")

    private const val EPOCA = -1373427L          // la fecha fija del 1 de tishréi del año 1
    private const val DIAS_HASTA_1970 = 719163L  // la fecha fija del 1 de enero de 1970

    fun embolismal(a: Int): Boolean = Math.floorMod(7L * a + 1, 19L) < 7

    private fun mesesTranscurridos(a: Int): Long = Math.floorDiv(235L * a - 234, 19L)

    private fun diasTranscurridos(a: Int): Long {
        val m = mesesTranscurridos(a)
        val partes = 12084L + 13753L * m
        val dia = 29L * m + Math.floorDiv(partes, 25920L)
        return if (Math.floorMod(3L * (dia + 1), 7L) < 3) dia + 1 else dia
    }

    private fun correccion(a: Int): Long {
        val n0 = diasTranscurridos(a - 1)
        val n1 = diasTranscurridos(a)
        val n2 = diasTranscurridos(a + 1)
        return when {
            n2 - n1 == 356L -> 2
            n1 - n0 == 382L -> 1
            else -> 0
        }
    }

    private fun anioNuevo(a: Int): Long = EPOCA + diasTranscurridos(a) + correccion(a)

    private fun diasDelAnio(a: Int): Long = anioNuevo(a + 1) - anioNuevo(a)

    private fun ultimoMes(a: Int): Int = if (embolismal(a)) 13 else 12

    private fun diasDelMes(m: Int, a: Int): Int = when {
        m == 2 || m == 4 || m == 6 || m == 10 || m == 13 -> 29
        m == 12 && !embolismal(a) -> 29
        m == 8 && diasDelAnio(a) != 355L && diasDelAnio(a) != 385L -> 29
        m == 9 && (diasDelAnio(a) == 353L || diasDelAnio(a) == 383L) -> 29
        else -> 30
    }

    private fun fija(a: Int, m: Int, d: Int): Long {
        var r = anioNuevo(a) + d - 1
        if (m < 7) {
            for (k in 7..ultimoMes(a)) r += diasDelMes(k, a)
            for (k in 1 until m) r += diasDelMes(k, a)
        } else {
            for (k in 7 until m) r += diasDelMes(k, a)
        }
        return r
    }

    /** La fecha hebrea de un día del calendario gregoriano. */
    fun hebrea(d: LocalDate): Hebrea {
        val f = d.toEpochDay() + DIAS_HASTA_1970
        var a = (Math.floorDiv((f - EPOCA) * 98496L, 35975351L) + 1).toInt() - 1
        while (anioNuevo(a + 1) <= f) a++
        var m = if (f < fija(a, 1, 1)) 7 else 1
        while (f > fija(a, m, diasDelMes(m, a))) m++
        return Hebrea(a, m, (f - fija(a, m, 1) + 1).toInt())
    }
}
