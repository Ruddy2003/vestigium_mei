package io.github.ruddy2003.vestigium.data

/**
 * Quién es cada @persona: qué es para ti, qué tan cerca (círculos de Aron, 1 a 7) y desde cuándo.
 * Las relaciones cambian (pareja → expareja): cada cambio guarda hasta cuándo valió lo anterior, y los análisis
 * usan la relación que había ese día.
 */
object Personas {
    /**
     * Las relaciones que se ofrecen (esquema 17). «Amistad cercana» ya no se ofrece: la cercanía la dan los círculos 1 a 7;
     * quien la tenía se muestra como «Amistad» y conserva su id (los análisis ya la juntaban con amistad).
     */
    val RELACIONES = listOf(
        Opcion("familia", "Familia"), Opcion("pareja", "Pareja"), Opcion("expareja", "Expareja"),
        Opcion("amistad", "Amistad"), Opcion("colega", "Colega o compañero de trabajo"), Opcion("companero", "Compañero de escuela"),
        Opcion("mentor", "Profesor, jefe o mentor"), Opcion("roomie", "Roomie (vivimos juntos)"), Opcion("vecino", "Vecino"),
        Opcion("salud", "Profesional de salud"), Opcion("conocido", "Conocido"), Opcion("lejana", "Solo en línea o de lejos"),
        Opcion("mascota", "Mascota"), Opcion("otra", "Otra"),
    )
    /** La relación como se ve en la lista (la retirada «amistad_cercana» se ve como «amistad»). */
    fun relacionVisible(id: String?): String? = if (id == "amistad_cercana") "amistad" else id
    fun nombreRelacion(id: String?) = RELACIONES.firstOrNull { it.id == relacionVisible(id) }?.nombre ?: Perfiles.RELACIONES.firstOrNull { it.id == id }?.nombre ?: id.orEmpty()

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
        // Una cercanía vacía en el historial es «sin cercanía» en ese tramo (P-25), no la de ahora.
        val tramo = p.historial.sortedBy { it.hasta }.firstOrNull { fecha < it.hasta } ?: return p.cercania
        return tramo.cercania
    }

    /**
     * Guarda o cambia a alguien. Si cambia la relación o la cercanía (también de «sin cercanía» a un valor, o al revés: [cercania]
     * null la borra), lo de antes queda en el historial hasta hoy (P-25).
     * [nacimiento] y [fallecimiento] en la forma de [Especiales] ("" = borrar; null = dejar como estaba).
     */
    fun guardar(nombre: String, relacion: String, cercania: Int?, detalle: String = "", hoy: String = Fechas.diaLogico(),
                nacimiento: String? = null, fallecimiento: String? = null) {
        Repo.cambiarConfig { c ->
            val previo = de(nombre, c)
            val n = if (previo == null) Persona(nombre.lowercase().removePrefix("@"), relacion, detalle, cercania, hoy,
                nacimiento = nacimiento.orEmpty(), fallecimiento = fallecimiento.orEmpty())
            else {
                val cambio = previo.relacion != relacion || previo.cercania != cercania
                previo.copy(relacion = relacion, cercania = cercania, detalle = detalle.ifBlank { previo.detalle },
                    historial = if (cambio) previo.historial + CambioRelacion(hoy, previo.relacion, previo.cercania) else previo.historial,
                    nacimiento = nacimiento ?: previo.nacimiento, fallecimiento = fallecimiento ?: previo.fallecimiento)
            }
            c.copy(personas = c.personas.filter { it !== previo } + n, personas_omitidas = c.personas_omitidas - n.nombre)
        }
    }

    fun omitir(nombre: String) = Repo.cambiarConfig { c -> c.copy(personas_omitidas = (c.personas_omitidas + nombre).distinct()) }
    fun posponer(hoy: String = Fechas.diaLogico()) = Repo.cambiarConfig { c -> c.copy(personas_pospuesto = hoy) }

    /**
     * En qué días aparece cada persona: diario, momentos (su texto y su «con quién»), sueños, notas del cronómetro y cartas.
     * Un día cuenta una vez por persona, la nombres las veces que la nombres. Con [ini] y [fin] (incluidos), solo esos días.
     * Es la única cuenta de «días» por persona: la usan Tu gente, «Tu año» y los reportes ([io.github.ruddy2003.vestigium.ciencia.Periodo]).
     */
    fun diasCon(ini: String? = null, fin: String? = null): Map<String, Set<String>> {
        val eq = Repo.equivalencias()
        val dias = HashMap<String, MutableSet<String>>()
        fun en(f: String) = (ini == null || f >= ini) && (fin == null || f <= fin)
        fun sumar(f: String, t: String?) { Texto.personas(t).forEach { dias.getOrPut(Texto.canon(it, eq)) { HashSet() }.add(f) } }
        Repo.dias.values.forEach { d -> if (en(d.fecha)) { sumar(d.fecha, d.nota); sumar(d.fecha, d.sueno_texto); d.sueno_temas.filter { it.startsWith("@") }.forEach { t -> sumar(d.fecha, t) } } }
        Repo.momentos.forEach { m -> if (!m.privado && en(m.fecha)) { sumar(m.fecha, m.texto); m.personas.forEach { p -> dias.getOrPut(Texto.canon(p.lowercase(), eq)) { HashSet() }.add(m.fecha) } } }
        Repo.tramos.forEach { t -> if (en(t.fecha)) sumar(t.fecha, t.nota) }
        Repo.cartas.forEach { c -> if (c.para.isNotBlank() && en(c.escrita)) dias.getOrPut(Texto.canon(c.para.lowercase(), eq)) { HashSet() }.add(c.escrita) }
        return dias
    }

    /** Quién aparece, en cuántos días y cuál fue el último ([diasCon], sin acotar). */
    fun menciones(): Map<String, Pair<Int, String>> = diasCon().mapValues { (_, s) -> s.size to (s.maxOrNull() ?: "") }

    /**
     * Todas las personas que la app conoce, sin tope: las de Tu gente, las de «Conocerte» y las mencionadas ([menc], de
     * [menciones]). Cada una con el nombre con que se cuenta (las uniones ya aplicadas) y una sola vez, de la que aparece en
     * más días a la que menos y, a igual número, por orden alfabético.
     */
    fun todas(menc: Map<String, Pair<Int, String>> = menciones(), c: Config = Repo.config): List<String> {
        val eq = c.equivalencias
        return (c.personas.map { it.nombre } + c.perfil.cercanas.map { it.nombre } + c.perfil.convivientes + menc.keys)
            .map { Texto.canon(it.trim().lowercase().removePrefix("@"), eq) }.filter { it.isNotBlank() }.distinct()
            .sortedWith(compareByDescending<String> { menc[it]?.first ?: 0 }.thenBy { it })
    }

    /** Cuántas veces y la última fecha de intimidad con alguien (esquema 21). Solo con el seguimiento privado encendido; si no, null. */
    fun intimidadCon(nombre: String, c: Config = Repo.config): Pair<Int, String>? {
        if (!c.seguimiento_privado) return null
        val k = canon(nombre)
        val l = Repo.momentos.filter { it.tipo == "intimidad" && it.personas.any { p -> canon(p) == k } }
        if (l.isEmpty()) return null
        return l.size to l.maxOf { it.fecha }
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
