package io.github.ruddy2003.vestigium.data

/**
 * Fechas señaladas (esquema 21): lo que quieres recordar de un día, cada una con su tipo (primera vez, viaje, logro, cambio de
 * vida, pérdida, encuentro, celebración u otra) y una frase. Sustituyen a los hitos, que eran etiquetas sueltas: los de antes
 * se leen como fechas señaladas de tipo «otro», con el texto sin guiones bajos. Lo que te pasa con fuerza (una crisis, un
 * llanto, una pelea, una migraña) no va aquí: es un episodio, un momento con hora (Catalogo.EPISODIO).
 */
object Senaladas {
    /** Las del día: las nuevas y, si quedan, los hitos de antes (sin repetir las que ya se pasaron). */
    fun de(d: Dia?): List<Senalada> {
        d ?: return emptyList()
        val ya = d.senaladas.map { normal(it.texto) }.toSet()
        return d.senaladas + d.hitos.map { Senalada("otro", texto(it)) }.filter { normal(it.texto) !in ya }
    }

    /** El texto de un hito de antes, legible: «primer_vuelo» → «primer vuelo». */
    fun texto(hito: String) = hito.replace('_', ' ').trim().replace(Regex("\\s+"), " ")

    /** Para comparar: sin acentos, en minúsculas y sin guiones bajos. */
    fun normal(t: String) = Texto.slug(t.replace('_', ' '))

    /** Cómo se muestra en todas partes: «✈ Viaje a la costa»; sin frase, el nombre del tipo. */
    fun etiqueta(s: Senalada): String {
        val o = Catalogo.senalada(s.tipo)
        return "${o.detalle} ${s.texto.trim().ifBlank { o.nombre }}"
    }

    fun etiquetas(d: Dia?): List<String> = de(d).map { etiqueta(it) }

    /** El día con sus hitos de antes pasados a fechas señaladas (lo que hace la migración del esquema 21 y la importación). */
    fun migrar(d: Dia): Dia = if (d.hitos.isEmpty()) d else d.copy(senaladas = de(d), hitos = emptyList())

    /** Días con fechas señaladas, en orden. */
    fun dias(): List<Pair<String, List<Senalada>>> = Repo.dias.values.map { it.fecha to de(it) }.filter { it.second.isNotEmpty() }.sortedBy { it.first }
}
