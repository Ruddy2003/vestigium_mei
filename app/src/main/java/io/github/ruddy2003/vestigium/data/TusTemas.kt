package io.github.ruddy2003.vestigium.data

/**
 * Los #temas, ordenados y limpios: cuántos días aparece cada uno, cuáles parecen el mismo escrito distinto (#tesis y
 * #tésis, #examen y #examenes), cuáles aparecen un solo día (quizá eran hitos) y cuáles son sentimientos que conviene
 * marcar con los botones. Se pueden unir (solo para los análisis), renombrar en todos tus textos o pasar a hitos.
 */
object TusTemas {
    class Tema(val nombre: String, val dias: Int, val primera: String, val ultima: String)

    /** Un texto donde puede haber #temas, con cómo cambiarlo. */
    private class Lugar(val fecha: String, val texto: String, val cambiar: (String) -> Unit)

    private fun lugares(): List<Lugar> {
        val out = ArrayList<Lugar>()
        Repo.dias.values.forEach { d ->
            out += Lugar(d.fecha, d.nota) { t -> Repo.importarDias(listOf(Repo.dia(d.fecha).copy(nota = t)), sobrescribir = true) }
            if (d.sueno_texto.isNotBlank()) out += Lugar(d.fecha, d.sueno_texto) { t -> Repo.importarDias(listOf(Repo.dia(d.fecha).copy(sueno_texto = t)), sobrescribir = true) }
            if (d.mejor_momento.isNotBlank()) out += Lugar(d.fecha, d.mejor_momento) { t -> Repo.importarDias(listOf(Repo.dia(d.fecha).copy(mejor_momento = t)), sobrescribir = true) }
            if (d.peor_momento.isNotBlank()) out += Lugar(d.fecha, d.peor_momento) { t -> Repo.importarDias(listOf(Repo.dia(d.fecha).copy(peor_momento = t)), sobrescribir = true) }
        }
        Repo.momentos.filter { it.texto.isNotBlank() }.forEach { m -> out += Lugar(m.fecha, m.texto) { t -> Repo.momento(m.id)?.let { Repo.guardarMomento(it.copy(texto = t)) } } }
        Repo.tramos.filter { it.nota.isNotBlank() }.forEach { tr -> out += Lugar(tr.fecha, tr.nota) { t -> Repo.guardarTramo(tr.copy(nota = t)) } }
        Repo.consumos.filter { it.nota.isNotBlank() }.forEach { c -> out += Lugar(c.fecha, c.nota) { t -> Repo.guardarConsumo(c.copy(nota = t)) } }
        // Las cartas selladas no se tocan: siguen cerradas hasta su fecha.
        Repo.cartas.filter { !it.sellada() && it.texto.isNotBlank() }.forEach { c -> out += Lugar(c.escrita, c.texto) { t -> Repo.guardarCarta(c.copy(texto = t)) } }
        return out
    }

    /** Cada #tema (como lo escribiste, sin unir) con los días en que aparece. */
    fun crudos(): Map<String, Set<String>> {
        val m = HashMap<String, HashSet<String>>()
        lugares().forEach { l -> Texto.temas(l.texto).forEach { t -> m.getOrPut(t) { HashSet() }.add(l.fecha) } }
        Repo.dias.values.forEach { d -> d.sueno_temas.filter { it.trim().startsWith("#") }.forEach { t -> m.getOrPut(Texto.etiqueta(t)) { HashSet() }.add(d.fecha) } }
        return m
    }

    /** Los temas ya unidos (cada uno con su nombre final), del más usado al menos. */
    fun lista(): List<Tema> {
        val eq = Repo.equivalencias()
        val m = HashMap<String, HashSet<String>>()
        crudos().forEach { (t, f) -> m.getOrPut(Texto.canon(t, eq)) { HashSet() }.addAll(f) }
        return m.map { (t, f) -> Tema(t, f.size, f.min(), f.max()) }.sortedWith(compareByDescending<Tema> { it.dias }.thenBy { it.nombre })
    }

    private fun sinAcentos(s: String) = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

    /** Singular aproximado en español: examenes → examen, tesis → tesis, amigos → amigo, viajes → viaje. */
    private fun raiz(s: String): String {
        val x = sinAcentos(s).replace("_", "")
        return when {
            x.length > 4 && x.endsWith("es") && !x.endsWith("ies") -> x.dropLast(2)
            x.length > 3 && x.endsWith("s") && !x.endsWith("is") -> x.dropLast(1)
            else -> x
        }
    }

    /** Pares que parecen el mismo tema escrito distinto (y que no dijiste que son distintos). */
    fun parecidos(): List<Pair<String, String>> {
        val eq = Repo.equivalencias()
        val l = lista()
        val dias = l.associate { it.nombre to it.dias }
        val nombres = l.map { it.nombre }
        val out = ArrayList<Pair<String, String>>()
        for (i in nombres.indices) for (j in i + 1 until nombres.size) {
            val a = nombres[i]; val b = nombres[j]
            if ("$a|$b" in Repo.config.temas_distintos || "$b|$a" in Repo.config.temas_distintos) continue
            if (Texto.canon(a, eq) == Texto.canon(b, eq)) continue
            val parecido = Texto.parecidos(a, b) || raiz(a) == raiz(b) || sinAcentos(a) == sinAcentos(b)
            // El más usado primero: es el nombre que se propone conservar.
            if (parecido) out += if ((dias[a] ?: 0) >= (dias[b] ?: 0)) a to b else b to a
        }
        return out.take(30)
    }

    /** [de] pasa a contarse como [a] en los análisis, reportes y la exportación. Tus textos no cambian. */
    fun unir(de: String, a: String) = Repo.cambiarConfig { c -> c.copy(equivalencias = c.equivalencias + (de to a)) }

    fun distintos(a: String, b: String) = Repo.cambiarConfig { c -> c.copy(temas_distintos = (c.temas_distintos + "$a|$b").distinct()) }

    /** Cuántos textos cambiarían al renombrar #[de], y un ejemplo (antes, después). */
    fun vistaPrevia(de: String, a: String): Pair<Int, Pair<String, String>?> {
        val l = lugares().filter { l -> Texto.temas(l.texto).contains(de.lowercase()) }
        val ej = l.firstOrNull()?.let { x ->
            val i = x.texto.lowercase().indexOf("#" + de.lowercase()).coerceAtLeast(0)
            val desde = (i - 50).coerceAtLeast(0); val hasta = (i + de.length + 60).coerceAtMost(x.texto.length)
            val trozo = x.texto.substring(desde, hasta)
            trozo to Texto.reemplazar(trozo, '#', de, a)
        }
        return l.size to ej
    }

    /** Copia de lo que había antes de un cambio masivo, para deshacerlo. */
    class Antes(val dias: List<Dia>, val momentos: List<Momento>, val tramos: List<Tramo>, val consumos: List<Consumo>, val cartas: List<Carta>)

    private fun foto(): Antes = Antes(Repo.dias.values.toList(), Repo.momentos, Repo.tramos, Repo.consumos, Repo.cartas)

    fun restaurar(a: Antes) {
        Repo.importarDias(a.dias, sobrescribir = true)
        a.momentos.forEach { m -> if (Repo.momento(m.id)?.texto != m.texto) Repo.guardarMomento(m) }
        val tr = Repo.tramos.associateBy { it.id }; a.tramos.forEach { t -> if (tr[t.id]?.nota != t.nota) Repo.guardarTramo(t) }
        val co = Repo.consumos.associateBy { it.id }; a.consumos.forEach { c -> if (co[c.id]?.nota != c.nota) Repo.guardarConsumo(c) }
        val ca = Repo.cartas.associateBy { it.id }; a.cartas.forEach { c -> if (ca[c.id]?.texto != c.texto) Repo.guardarCarta(c) }
    }

    /** Cambia cada #[de] por #[a] en todos tus textos. Devuelve cuántos textos cambió y cómo estaba todo antes. */
    fun renombrar(de: String, a: String): Pair<Int, Antes> {
        val antes = foto()
        val d = de.lowercase().removePrefix("#"); val n = Texto.etiqueta(a)
        var k = 0
        lugares().forEach { l ->
            if (d !in Texto.temas(l.texto)) return@forEach
            val t = Texto.reemplazar(l.texto, '#', d, n)
            if (t != l.texto) { l.cambiar(t); k++ }
        }
        // Las etiquetas del sueño («#tema» en la lista).
        Repo.dias.values.filter { dd -> dd.sueno_temas.any { it.trim().equals("#$d", true) } }.forEach { dd ->
            Repo.importarDias(listOf(Repo.dia(dd.fecha).let { it.copy(sueno_temas = it.sueno_temas.map { t -> if (t.trim().equals("#$d", true)) "#$n" else t }) }), sobrescribir = true); k++
        }
        // Si había una unión que apuntaba al nombre viejo, ahora apunta al nuevo.
        Repo.cambiarConfig { c -> c.copy(equivalencias = c.equivalencias.mapValues { (_, v) -> if (v == d) n else v } - n) }
        return k to antes
    }

    /** Quita el # (el texto se queda): «#sentimiento_frustrado» → «frustrado»; sin el prefijo, los guiones bajos pasan a espacios. */
    fun quitarAlmohadilla(tema: String): Pair<Int, Antes> {
        val antes = foto()
        val d = tema.lowercase().removePrefix("#")
        val limpio = d.removePrefix("sentimiento_").removePrefix("sentimientos_").replace('_', ' ')
        val rx = Regex("#" + Regex.escape(d) + "(?![\\p{L}\\p{N}_])", RegexOption.IGNORE_CASE)
        var k = 0
        lugares().forEach { l -> if (rx.containsMatchIn(l.texto)) { l.cambiar(rx.replace(l.texto, limpio)); k++ } }
        return k to antes
    }

    /** Los que empiezan con «sentimiento_»: son lo que sentiste, que va mejor en los botones de «¿Cómo estás ahora?». */
    fun deSentimientos(): List<Tema> = lista().filter { it.nombre.startsWith("sentimiento") }

    /** Temas que solo aparecieron un día y hace más de dos semanas: quizá eran un hito. */
    fun deUnDia(): List<Tema> {
        val limite = Fechas.sumar(Fechas.diaLogico(), -14)
        return lista().filter { it.dias == 1 && it.ultima < limite && !it.nombre.startsWith("sentimiento") }
    }

    /** Pasa un #tema de un solo día a los hitos de ese día y le quita el # en el texto. */
    fun aHito(t: Tema): Pair<Int, Antes> {
        val antes = foto()
        val d = Repo.dia(t.primera)
        val hito = Texto.slug(t.nombre)
        if (hito.isNotBlank() && hito !in d.hitos) Repo.importarDias(listOf(d.copy(hitos = d.hitos + hito)), sobrescribir = true)
        val (k, _) = quitarAlmohadilla(t.nombre)
        return k to antes
    }
}
