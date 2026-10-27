package io.github.ruddy2003.vestigium.data

/**
 * Subcategorías de gasto, su monto habitual y los frecuentes (N-29): la mayoría de los gastos se anotan con dos o tres
 * toques, sin escribir.
 */
object Dinero {
    /** Un gasto que se registra con un toque: categoría, subcategoría y su monto habitual. */
    data class Frecuente(val categoria: String, val sub: String, val monto: Double)

    fun subcategorias(cat: String, c: Config = Repo.config): List<Subcategoria> = c.subcategorias[cat].orEmpty()

    /**
     * El monto habitual de una subcategoría: el que fijaste a mano; si no, el que más se repite en tus últimos 10 gastos
     * de esa subcategoría (si empatan, el más reciente). Null si no hay ni uno ni otro.
     */
    fun montoHabitual(cat: String, sub: String, gastos: List<Gasto> = Repo.gastos, c: Config = Repo.config): Double? {
        c.subcategorias[cat]?.firstOrNull { it.nombre == sub }?.monto?.let { return it }
        val ultimos = gastos.filter { it.categoria == cat && it.subcategoria == sub }.sortedByDescending { it.fecha + it.hora }.take(10)
        if (ultimos.isEmpty()) return null
        val cuenta = ultimos.groupingBy { it.monto }.eachCount()
        val max = cuenta.values.max()
        return ultimos.first { cuenta[it.monto] == max }.monto
    }

    /**
     * Tus [n] subcategorías más usadas en los últimos 90 días, con su monto habitual; después, las que tienen un monto
     * fijado aunque aún no las uses. Las que no tienen monto habitual no entran: no se podrían registrar con un toque.
     */
    fun frecuentes(n: Int = 6, hoy: String = Fechas.hoyCalendario(), gastos: List<Gasto> = Repo.gastos, c: Config = Repo.config): List<Frecuente> {
        val desde = Fechas.sumar(hoy, -90)
        val usadas = gastos.filter { it.subcategoria.isNotBlank() && it.fecha >= desde }.groupingBy { it.categoria to it.subcategoria }.eachCount()
            .entries.sortedByDescending { it.value }.map { it.key }
        val fijas = c.subcategorias.flatMap { (cat, l) -> l.filter { it.monto != null }.map { cat to it.nombre } }
        return (usadas + fijas).distinct().mapNotNull { (cat, sub) -> montoHabitual(cat, sub, gastos, c)?.let { Frecuente(cat, sub, it) } }.take(n)
    }

    /** El mismo gasto de un toque registrado hace menos de 2 s: un segundo toque no lo repite. */
    fun repetido(cat: String, sub: String, monto: Double, ahoraMs: Long = System.currentTimeMillis(), gastos: List<Gasto> = Repo.gastos): Gasto? =
        gastos.lastOrNull { g ->
            g.categoria == cat && g.subcategoria == sub && g.monto == monto &&
                g.ts_creado?.let { ahoraMs - Fechas.millis(it) in 0..1999 } == true
        }

    /** Registra un frecuente ahora (o devuelve el de hace un instante, si fue un toque doble). */
    fun registrarFrecuente(cat: String, sub: String, monto: Double): Pair<Gasto, Boolean> {
        repetido(cat, sub, monto)?.let { return it to false }
        val g = Gasto(Repo.nuevoId(), Fechas.hoyCalendario(), Fechas.horaAhora(), monto, cat, subcategoria = sub, ts_creado = Fechas.ahoraIso())
        Repo.guardarGasto(g)
        return g to true
    }

    fun agregarSub(cat: String, nombre: String) = Repo.cambiarConfig { c ->
        val l = c.subcategorias[cat].orEmpty()
        if (nombre.isBlank() || l.any { it.nombre.equals(nombre, ignoreCase = true) }) c else c.copy(subcategorias = c.subcategorias + (cat to l + Subcategoria(nombre.trim())))
    }

    fun quitarSub(cat: String, nombre: String) = Repo.cambiarConfig { c ->
        c.copy(subcategorias = c.subcategorias + (cat to c.subcategorias[cat].orEmpty().filter { it.nombre != nombre }))
    }

    /** Fija (o, con null, deja automático) el monto habitual de una subcategoría. */
    fun fijarMonto(cat: String, nombre: String, monto: Double?) = Repo.cambiarConfig { c ->
        c.copy(subcategorias = c.subcategorias + (cat to c.subcategorias[cat].orEmpty().map { if (it.nombre == nombre) it.copy(monto = monto) else it }))
    }

    /** Cuántos gastos guardados llevan esa categoría (y subcategoría, si se da): para preguntar antes de renombrarlos. */
    fun cuantos(cat: String, sub: String? = null): Int = Repo.gastos.count { it.categoria == cat && (sub == null || it.subcategoria == sub) }

    /** Cambia el nombre de una categoría en tus ajustes y, si [tambienGastos], en los gastos que ya la tienen. */
    fun renombrarCategoria(viejo: String, nuevo: String, tambienGastos: Boolean) {
        val n = nuevo.trim()
        if (n.isEmpty() || n == viejo || n in Repo.config.categorias) return
        Repo.cambiarConfig { c ->
            c.copy(categorias = c.categorias.map { if (it == viejo) n else it },
                subcategorias = c.subcategorias[viejo]?.let { l -> c.subcategorias - viejo + (n to l) } ?: c.subcategorias)
        }
        if (tambienGastos) Repo.gastos.filter { it.categoria == viejo }.forEach { Repo.guardarGasto(it.copy(categoria = n, ts_editado = Fechas.ahoraIso())) }
    }

    /** Cambia el nombre de una subcategoría y, si [tambienGastos], en los gastos que ya la tienen. */
    fun renombrarSub(cat: String, viejo: String, nuevo: String, tambienGastos: Boolean) {
        val n = nuevo.trim()
        if (n.isEmpty() || n == viejo || subcategorias(cat).any { it.nombre == n }) return
        Repo.cambiarConfig { c -> c.copy(subcategorias = c.subcategorias + (cat to c.subcategorias[cat].orEmpty().map { if (it.nombre == viejo) it.copy(nombre = n) else it })) }
        if (tambienGastos) Repo.gastos.filter { it.categoria == cat && it.subcategoria == viejo }.forEach { Repo.guardarGasto(it.copy(subcategoria = n, ts_editado = Fechas.ahoraIso())) }
    }

    /** Sube una categoría un lugar en la lista. */
    fun subir(cat: String) = Repo.cambiarConfig { c ->
        val i = c.categorias.indexOf(cat)
        if (i <= 0) c else c.copy(categorias = c.categorias.toMutableList().apply { removeAt(i); add(i - 1, cat) })
    }

    /** «1,234.50» sin ceros de más, para llenar el campo del monto. */
    fun texto(v: Double): String = if (v % 1.0 == 0.0) v.toLong().toString() else "%.2f".format(java.util.Locale.US, v).trimEnd('0').trimEnd('.')
}
