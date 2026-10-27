package io.github.ruddy2003.vestigium.data

/**
 * Quién es cada @persona: qué es para ti, qué tan cerca (círculos de Aron, 1 a 7) y desde cuándo.
 * Las relaciones cambian (pareja → expareja): cada cambio guarda hasta cuándo valió lo anterior, y los análisis
 * usan la relación que había ese día.
 */
object Personas {
    val RELACIONES = listOf(
        Opcion("familia", "Familia"), Opcion("pareja", "Pareja"), Opcion("expareja", "Expareja"), Opcion("amistad_cercana", "Amistad cercana"),
        Opcion("amistad", "Amistad"), Opcion("colega", "Colega o compañero de trabajo"), Opcion("companero", "Compañero de escuela"),
        Opcion("mentor", "Profesor, jefe o mentor"), Opcion("conocido", "Conocido"), Opcion("lejana", "No la conozco en persona"),
        Opcion("mascota", "Mascota"), Opcion("otra", "Otra"),
    )
    fun nombreRelacion(id: String?) = RELACIONES.firstOrNull { it.id == id }?.nombre ?: Perfiles.RELACIONES.firstOrNull { it.id == id }?.nombre ?: id.orEmpty()

    /** Para los análisis de siempre: familia, pareja, amistad (las de «Conocerte» usaban estas tres). */
    fun grupo(relacion: String?): String? = when (relacion) {
        "familia", "casa" -> "familia"; "pareja" -> "pareja"; "amistad", "amistad_cercana" -> "amistad"
        "trabajo", "colega", "companero", "mentor" -> "trabajo"; null -> null; else -> relacion
    }

    private fun canon(n: String) = Texto.canon(n.lowercase(), Repo.equivalencias())

    fun de(nombre: String, c: Config = Repo.config): Persona? { val k = canon(nombre); return c.personas.firstOrNull { canon(it.nombre) == k } }

    /** La relación que había en [fecha] (vacío = la de ahora). */
    fun relacionEn(nombre: String, fecha: String? = null, c: Config = Repo.config): String? {
        val p = de(nombre, c) ?: return null
        if (fecha == null) return p.relacion
        return p.historial.sortedBy { it.hasta }.firstOrNull { fecha < it.hasta }?.relacion ?: p.relacion
    }
    fun cercaniaEn(nombre: String, fecha: String? = null, c: Config = Repo.config): Int? {
        val p = de(nombre, c) ?: return null
        if (fecha == null) return p.cercania
        return p.historial.sortedBy { it.hasta }.firstOrNull { fecha < it.hasta }?.let { it.cercania ?: p.cercania } ?: p.cercania
    }

    /** Guarda o cambia a alguien. Si cambia la relación o la cercanía, lo de antes queda en el historial hasta hoy. */
    fun guardar(nombre: String, relacion: String, cercania: Int?, detalle: String = "", hoy: String = Fechas.diaLogico()) {
        Repo.cambiarConfig { c ->
            val previo = de(nombre, c)
            val n = if (previo == null) Persona(nombre.lowercase().removePrefix("@"), relacion, detalle, cercania, hoy)
            else {
                val cambio = previo.relacion != relacion || (previo.cercania != null && cercania != null && previo.cercania != cercania)
                previo.copy(relacion = relacion, cercania = cercania ?: previo.cercania, detalle = detalle.ifBlank { previo.detalle },
                    historial = if (cambio) previo.historial + CambioRelacion(hoy, previo.relacion, previo.cercania) else previo.historial)
            }
            c.copy(personas = c.personas.filter { it !== previo } + n, personas_omitidas = c.personas_omitidas - n.nombre)
        }
    }

    fun omitir(nombre: String) = Repo.cambiarConfig { c -> c.copy(personas_omitidas = (c.personas_omitidas + nombre).distinct()) }
    fun posponer(hoy: String = Fechas.diaLogico()) = Repo.cambiarConfig { c -> c.copy(personas_pospuesto = hoy) }

    /** Quién aparece y cuántos días: diario, momentos, sueños, notas del cronómetro y cartas. */
    fun menciones(): Map<String, Pair<Int, String>> {
        val eq = Repo.equivalencias()
        val dias = HashMap<String, MutableSet<String>>()
        fun sumar(f: String, t: String?) { Texto.personas(t).forEach { dias.getOrPut(Texto.canon(it, eq)) { HashSet() }.add(f) } }
        Repo.dias.values.forEach { d -> sumar(d.fecha, d.nota); sumar(d.fecha, d.sueno_texto); d.sueno_temas.filter { it.startsWith("@") }.forEach { t -> sumar(d.fecha, t) } }
        Repo.momentos.filter { !it.privado }.forEach { m -> sumar(m.fecha, m.texto) }
        Repo.tramos.forEach { t -> sumar(t.fecha, t.nota) }
        Repo.cartas.filter { it.para.isNotBlank() }.forEach { c -> dias.getOrPut(Texto.canon(c.para.lowercase(), eq)) { HashSet() }.add(c.escrita) }
        return dias.mapValues { (_, s) -> s.size to (s.maxOrNull() ?: "") }
    }

    /** Las que faltan por clasificar, de la más mencionada a la menos (dos días o más). */
    fun pendientes(c: Config = Repo.config): List<Pair<String, Int>> {
        val ya = c.personas.map { canon(it.nombre) }.toSet() + c.perfil.cercanas.map { canon(it.nombre) } + c.perfil.convivientes.map { canon(it) }
        return menciones().filter { (n, v) -> v.first >= 2 && canon(n) !in ya && n !in c.personas_omitidas }.entries
            .sortedByDescending { it.value.first }.map { it.key to it.value.first }
    }

    /** Nombres que parecen la misma persona escrita distinto (@ana y @anita, @luis_m y @luism). */
    fun duplicados(c: Config = Repo.config): List<Pair<String, String>> {
        val n = menciones().keys.sorted()
        val out = ArrayList<Pair<String, String>>()
        for (i in n.indices) for (j in i + 1 until n.size) {
            val a = n[i]; val b = n[j]
            if ("$a|$b" in c.personas_distintas) continue
            val parecido = Texto.parecidos(a, b) || (a.length >= 3 && b.length >= 3 && (b.startsWith(a) || a.startsWith(b)) && kotlin.math.abs(a.length - b.length) <= 3)
            if (parecido) out += a to b
        }
        return out.take(20)
    }

    /** Une dos nombres: [de] pasa a contarse como [a]. */
    fun unir(de: String, a: String) = Repo.cambiarConfig { c -> c.copy(equivalencias = c.equivalencias + (de to a)) }
    fun distintas(a: String, b: String) = Repo.cambiarConfig { c -> c.copy(personas_distintas = (c.personas_distintas + "$a|$b").distinct()) }
}
