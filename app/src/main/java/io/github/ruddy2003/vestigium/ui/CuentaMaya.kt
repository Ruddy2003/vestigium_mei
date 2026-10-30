package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import java.time.LocalDate

/**
 * Mesoamérica: la cuenta maya de los días. Tres cuentas que corren juntas:
 * - la **cuenta larga** (baktún.katún.tun.uinal.kin), los días desde el 13.0.0.0.0, 4 Ajaw 8 Kumk'u (11 de agosto del
 *   3114 a. C., proléptico gregoriano);
 * - el **tzolk'in**, de 260 días: un número del 1 al 13 junto a uno de 20 nombres de día;
 * - el **haab**, de 365: 18 meses de 20 días más los 5 de Wayeb, sin año bisiesto.
 *
 * Se usa la correlación GMT (día juliano 584283), la más aceptada; con ella el 21 de diciembre de 2012 es 13.0.0.0.0
 * 4 Ajaw 3 K'ank'in. Los nombres van en la ortografía de la ALMG sin la b' (Ak'bal, K'ib, Kaban). La cuenta maya no tenía
 * semana de siete días: la semana del tema se nombra con los números del maya yucateco de hoy (jun, ka', óox…).
 */
object Maya {
    /** Día juliano del 13.0.0.0.0 (correlación GMT). */
    const val CORRELACION = 584283L

    val TZOLKIN = listOf("Imix", "Ik'", "Ak'bal", "K'an", "Chikchan", "Kimi", "Manik'", "Lamat", "Muluk", "Ok",
        "Chuwen", "Eb", "Ben", "Ix", "Men", "K'ib", "Kaban", "Etz'nab", "Kawak", "Ajaw")
    val HAAB = listOf("Pop", "Wo'", "Sip", "Sotz'", "Sek", "Xul", "Yaxk'in", "Mol", "Ch'en", "Yax",
        "Sak'", "Keh", "Mak", "K'ank'in", "Muwan", "Pax", "K'ayab", "Kumk'u", "Wayeb")
    /** De lunes a domingo, los números del uno al siete en maya yucateco: «primer día», «segundo día»… */
    val SEMANA = listOf("Jun", "Ka'", "Óox", "Kan", "Jo'", "Wak", "Wuk")
    private val MESES_CORTOS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

    /** Días transcurridos desde el 13.0.0.0.0. */
    fun dias(d: LocalDate): Long = d.toEpochDay() + 2440588L - CORRELACION

    /** Las cinco cifras de la cuenta larga: «13.0.14.0.8». */
    fun cuentaLarga(d: LocalDate): String {
        var x = dias(d)
        if (x < 0) return d.toString()
        val r = LongArray(5)
        longArrayOf(144000, 7200, 360, 20, 1).forEachIndexed { i, b -> r[i] = x / b; x %= b }
        return r.joinToString(".")
    }

    /** Número (1 a 13) y nombre (índice 0 a 19) del día en el tzolk'in. */
    fun tzolkinPartes(d: LocalDate): Pair<Int, Int> {
        val n = dias(d)
        return (Math.floorMod(n + 3, 13L) + 1).toInt() to Math.floorMod(n + 19, 20L).toInt()
    }

    /** «8 Lamat». */
    fun tzolkin(d: LocalDate): String = tzolkinPartes(d).let { (n, i) -> "$n ${TZOLKIN[i]}" }

    /** Día (0 a 19; el 0 es el «asiento» del mes) y mes (0 a 18) del haab. */
    fun haabPartes(d: LocalDate): Pair<Int, Int> {
        val p = Math.floorMod(dias(d) + 348, 365L).toInt()
        return p % 20 to p / 20
    }

    /** «1 Sak'». */
    fun haab(d: LocalDate): String = haabPartes(d).let { (dia, mes) -> "$dia ${HAAB[mes]}" }

    /** La fecha completa del tema: «8 Lamat 1 Sak' · 13.0.14.0.8». */
    fun fecha(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        "${tzolkin(d)} ${haab(d)} · ${cuentaLarga(d)}"
    }.getOrDefault(iso)

    /**
     * Los meses del haab que cubren el mes gregoriano [n] (del primer al último día), con el mes nuestro entre paréntesis:
     * «Yax–Sak' (oct.)». Como el haab no tiene bisiesto, se corre un día cada cuatro años: depende del año ([anio], o el
     * actual si no se da).
     */
    fun mes(n: Int, anio: Int? = null): String {
        val m = n.coerceIn(1, 12)
        val a = anio ?: LocalDate.now().year
        val ini = LocalDate.of(a, m, 1); val fin = ini.plusMonths(1).minusDays(1)
        val h1 = HAAB[haabPartes(ini).second]; val h2 = HAAB[haabPartes(fin).second]
        return "$h1–$h2 (${MESES_CORTOS[m - 1]})" + (anio?.let { " $it" } ?: "")
    }

    /**
     * Las fechas que marca la propia cuenta: el año nuevo del haab (0 Pop), los cinco días sin nombre del Wayeb, el
     * comienzo de un tzolk'in (1 Imix) y el fin de cada tun (cada 360 días de la cuenta larga). Null si no hay nada.
     */
    fun marca(iso: String): String? = runCatching {
        val d = Fechas.parse(iso)
        val (dia, mes) = haabPartes(d)
        when {
            mes == 0 && dia == 0 -> "0 Pop"
            mes == 18 -> "Wayeb"
            tzolkinPartes(d) == (1 to 0) -> "1 Imix"
            Math.floorMod(dias(d), 360L) == 0L -> "Fin de tun"
            else -> null
        }
    }.getOrNull()

    /**
     * El número en puntos y barras, con los numerales mayas de Unicode (U+1D2E0 a U+1D2F3): cada cifra es un signo de
     * 0 a 19 y las posiciones van de mayor a menor, separadas por un espacio fino (los mayas las apilaban de arriba abajo).
     * Las letras del tema los traen (ver `herramientas/fuentes.py`); Android no tiene una letra propia para ellos, así que
     * de 400 en adelante (los años, sobre todo, que también salen en el widget) se escriben con cifras.
     */
    fun numeral(n: Int): String {
        if (n < 0 || n >= 400) return n.toString()
        val cifras = if (n < 20) listOf(n) else listOf(n / 20, n % 20)
        return cifras.joinToString(" ") { String(Character.toChars(0x1D2E0 + it)) }
    }
}
