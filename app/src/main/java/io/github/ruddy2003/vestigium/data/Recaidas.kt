package io.github.ruddy2003.vestigium.data

/**
 * Seguimiento privado de recaídas: cada una se guarda como un momento con hora y lugar, y además marca el día
 * (recaída = sí) y deja en 0 el hábito ligado. Todo esto se ve solo en la pantalla Privado (ver `Privado`).
 */
object Recaidas {
    /** Lo que había en el día antes de registrar la recaída, para poder deshacerla tal cual. */
    class Previo(val fecha: String, val recaida: Boolean?, val habito: String?, val habitoHecho: Boolean)

    private val PALABRAS = listOf("adult", "porn", "porno", "contenido", "nsfw", "xxx")

    /** El hábito que una recaída rompe: el elegido en Privacidad o, si no hay, uno cuyo nombre lo diga. */
    fun habito(c: Config = Repo.config): Habito? {
        if (c.habito_recaida == "") return null
        c.habito_recaida?.let { id -> c.habitos.firstOrNull { it.id == id }?.let { return it } }
        return c.habitos.filter { it.activo }.firstOrNull { h -> val n = Texto.slug(h.nombre); PALABRAS.any { n.contains(it) } }
    }

    /** Marca el día y el hábito; devuelve cómo estaban para poder deshacerlo. */
    fun registrar(m: Momento): Previo {
        val d = Repo.dia(m.fecha)
        val h = habito()
        val previo = Previo(m.fecha, d.recaida, h?.id, h != null && d.habitos[h.id] == true)
        Repo.guardarDia(d.copy(recaida = true, habitos = if (h != null) d.habitos + (h.id to false) else d.habitos))
        return previo
    }

    fun deshacer(p: Previo) {
        val d = Repo.dia(p.fecha)
        val habitos = if (p.habito != null && p.habitoHecho) d.habitos + (p.habito to true) else d.habitos
        Repo.guardarDia(d.copy(recaida = p.recaida, habitos = habitos))
    }

    /**
     * El mismo momento a otra hora: su día lógico y la ubicación que había a esa hora (la muestra más cercana a ±30 min;
     * si no hay, queda sin ubicación para no guardar el lugar de ahora). La luz y el ruido medidos al registrar se descartan.
     */
    fun aOtraHora(m: Momento, ms: Long): Momento {
        val p = runCatching { Repo.db().ubicaciones(ms - 30 * 60_000L, ms + 30 * 60_000L).minByOrNull { kotlin.math.abs(it.ms - ms) } }.getOrNull()
        return m.copy(ts = Fechas.isoDeMillis(ms), fecha = Fechas.diaLogicoDeMillis(ms), lat = p?.lat, lon = p?.lon, precision_m = p?.precision?.toFloat(),
            lugar = p?.let { Repo.lugarCercano(it.lat, it.lon)?.id }, luz_lux = null, ruido_db = null)
    }

    /** Cambia la hora de una recaída ya guardada. Si cae en otro día, la marca del día se mueve con ella. */
    fun moverHora(m: Momento, ms: Long): Momento {
        val n = aOtraHora(m, ms)
        Repo.guardarMomento(n)
        if (n.fecha != m.fecha) {
            val viejo = Repo.dia(m.fecha)
            if (viejo.recaida == true && Repo.momentos.none { it.tipo == "recaida" && it.fecha == m.fecha && it.id != m.id }) Repo.guardarDia(viejo.copy(recaida = null))
            registrar(n)
        }
        return n
    }

    /**
     * Al borrar una recaída: si era la única de su día, el día deja de estar marcado (queda sin respuesta, porque no se sabe
     * qué había antes). El hábito ligado se queda sin marcar, como lo dejó la recaída.
     */
    fun alBorrar(m: Momento) {
        if (m.tipo != "recaida") return
        val d = Repo.dias[m.fecha] ?: return
        if (d.recaida == true && Repo.momentos.none { it.tipo == "recaida" && it.fecha == m.fecha && it.id != m.id }) Repo.guardarDia(d.copy(recaida = null))
    }

    /** Días con recaída: los marcados en el día y los que tienen una recaída registrada con hora. */
    fun dias(): Set<String> = Repo.dias.values.filter { it.recaida == true }.map { it.fecha }.toSet() +
        Repo.momentos.filter { it.tipo == "recaida" }.map { it.fecha }

    /** Días con el seguimiento contestado (sí o no), para poder hablar de «días limpios». */
    fun diasConRespuesta(): Set<String> = Repo.dias.values.filter { it.recaida != null }.map { it.fecha }.toSet() + dias()

    data class Resumen(
        val impulsosHora: IntArray, val recaidasHora: IntArray, val recaidasSemana: IntArray,
        val lugares: List<Pair<String, Int>>, val detonantes: List<Pair<String, Int>>, val antes: List<Pair<String, Int>>, val aparatos: List<Pair<String, Int>>,
        /** Minutos entre un impulso y la recaída que le siguió (dentro de 24 h). */
        val minutosImpulsoARecaida: List<Double>,
        /** Impulsos registrados con hora, y cuántos pasaron sin recaída en las 3 horas siguientes. */
        val impulsos: Int, val impulsosSuperados: Int,
        val diasDesdeUltima: Int?, val rachaMaxima: Int, val rachaActual: Int, val totalDias: Int, val diasConRespuesta: Int,
        /** Duraciones anotadas (minutos). */
        val duraciones: List<Int> = emptyList(),
        /** Días sin respuesta desde la última recaída: no cuentan como limpios ni rompen la racha (P-27). */
        val sinRespuestaRacha: Int = 0,
    )

    /** Rachas sin recaída: la más larga, la actual y los días sin respuesta dentro de la actual. */
    class Racha(val mejor: Int, val actual: Int, val sinRespuesta: Int)

    /**
     * Las rachas, como función pura (P-27). Un día cuenta como limpio solo si respondiste «no» ese día ([limpios]); un día con
     * recaída ([recaidas]) la reinicia; un día sin respuesta no suma ni reinicia, y se cuenta aparte (hoy, mientras no
     * respondas, no cuenta como sin respuesta). Antes, todo día sin recaída contaba como limpio, aunque no lo hubieras contestado.
     */
    fun rachas(recaidas: Set<String>, limpios: Set<String>, hoy: String): Racha {
        val primero = (recaidas + limpios).minOrNull() ?: return Racha(0, 0, 0)
        var mejor = 0; var actual = 0; var sin = 0
        var f = primero
        while (f <= hoy) {
            when {
                f in recaidas -> { actual = 0; sin = 0 }
                f in limpios -> { actual++; if (actual > mejor) mejor = actual }
                f < hoy -> sin++
            }
            f = Fechas.sumar(f, 1)
        }
        return Racha(mejor, actual, sin)
    }

    fun resumen(hoy: String = Fechas.diaLogico()): Resumen {
        val imp = Repo.momentos.filter { it.tipo == "impulso" }
        val rec = Repo.momentos.filter { it.tipo == "recaida" }
        fun hora(m: Momento) = Fechas.horaDe(m.ts).take(2).toIntOrNull() ?: 0
        val ih = IntArray(24); imp.forEach { ih[hora(it)]++ }
        val rh = IntArray(24); rec.forEach { rh[hora(it)]++ }
        val dias = dias()
        val sem = IntArray(7); dias.forEach { f -> sem[Fechas.parse(f).dayOfWeek.value - 1]++ }
        val lugares = rec.groupingBy { m -> Repo.lugares.firstOrNull { it.id == m.lugar }?.nombre ?: if (m.lat != null) "otro lugar" else "sin ubicación" }.eachCount()
            .entries.sortedByDescending { it.value }.map { it.key to it.value }
        val det = (Repo.dias.values.flatMap { it.detonantes } + rec.flatMap { it.detonantes }).groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }.map { it.key to it.value }
        val antes = rec.mapNotNull { it.antes }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.map { it.key to it.value }
        val aparatos = rec.mapNotNull { it.dispositivo }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.map { it.key to it.value }
        val recMs = rec.map { Fechas.millis(it.ts) }.sorted()
        val impMs = imp.map { Fechas.millis(it.ts) }.sorted()
        val entre = recMs.mapNotNull { r -> impMs.lastOrNull { it <= r && r - it <= 24 * 3_600_000L }?.let { (r - it) / 60_000.0 } }
        val superados = impMs.count { i -> recMs.none { it >= i && it - i <= 3 * 3_600_000L } }
        val ultima = dias.maxOrNull()
        val respondidos = diasConRespuesta()
        val limpios = Repo.dias.values.filter { it.recaida == false }.map { it.fecha }.toSet() - dias
        val racha = rachas(dias, limpios, hoy)
        return Resumen(ih, rh, sem, lugares, det, antes, aparatos, entre, impMs.size, superados,
            ultima?.let { Fechas.diasEntre(it, hoy).toInt() }, racha.mejor, racha.actual, dias.size, respondidos.size,
            rec.mapNotNull { it.duracion_min }.filter { it > 0 }, racha.sinRespuesta)
    }
}
