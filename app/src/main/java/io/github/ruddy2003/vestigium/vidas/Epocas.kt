package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import java.text.Normalizer

/**
 * Qué acontecimiento de «Hoy en la historia» lleva «en la época de X ›», que abre el cuento de X (plan de historia y
 * escenas, sección 3.6). H2 (13 nov 2026) lo enlazaba con solo caer en su época, y salía demasiado: el 13 de noviembre,
 * 60 de 98 acontecimientos, casi todos a Rosa o a José. **Desde H3 (13 nov 2026) solo sale con lo que pudo afectarle:**
 * lo que pasó en su país o región, y lo mundial de su tiempo. No se copia texto: solo se enlaza.
 *
 * Un acontecimiento lleva el enlace de una vida si cae en su época ([de]: su año ± [MARGEN]) y además:
 * - **(a) Wikidata dice que es de su lugar** ([Motivo.PAIS]). De cada artículo que el acontecimiento enlaza se sabe su
 *   país (`historia/Wikidata.kt`). «Hablan» los artículos que tienen entre 1 y [MAX_PAISES] países: uno con más (el Mar
 *   del Norte, el Cinturón de Fuego del Pacífico) no dice nada de un sitio. Cuenta si el lugar de la vida
 *   ([LugarDeVida.wikidata]) está en al menos la mitad de los que hablan, o en uno solo pero el texto nombra el lugar
 *   (Alvarado sale «de Tenochtitlán»: dos artículos son de España y uno de México).
 * - **(b) Si Wikidata no dice nada**, porque no contestó o porque ningún artículo habla ([Motivo.PALABRAS]): el texto o
 *   los títulos nombran una de las palabras de la vida, sin contar acentos ni las [LugarDeVida.excepto] («Nuevo México»
 *   no es México).
 * - **(c) Es mundial y lo vivió** ([Motivo.MUNDIAL]): uno de [MUNDIALES], dentro de sus fechas, y entre el nacimiento de
 *   la vida y el final de su temporada 5. Esta no pide caer en su época ni en su lugar.
 *
 * Si cumplen varias, gana la que tiene más artículos de su lugar; luego la de año más cercano; luego la del tema; luego
 * la primera de la lista. Lo medido con cuatro fechas enteras (436 acontecimientos) está en el informe de H3.
 *
 * **Sin época** **[Diseño propio]**: las vidas inventadas ([OtrasVidas.Vida.ficticia]), porque su mundo no es el de la
 * historia de Wikipedia y algunas ni cuentan los años como nosotros (Brenna, el año 312 «de los Caminos»), y las de hoy
 * (desde [DESDE_HOY]: Alguien del mundo, Alguien de México, Giulia), cuya época es la tuya.
 */
object Epocas {
    /** Años antes y después del de la vida (el plan: `anio ± 30`). */
    const val MARGEN = 30

    /** Desde este año, una vida es de hoy y no tiene época. */
    const val DESDE_HOY = 2000

    /** Un artículo con más países que estos no dice de dónde es el acontecimiento. */
    const val MAX_PAISES = 3

    /** Las temporadas que dura cada vida después de su año (la 1 es su año; la 5, cuatro después). */
    private const val ANIOS_DE_TEMPORADAS = 4

    /** Por qué un acontecimiento lleva a una vida. */
    enum class Motivo { PAIS, PALABRAS, MUNDIAL }

    /** Un artículo que el acontecimiento enlaza: su título, su identificador de Wikidata y sus países (null: no se sabe). */
    class Articulo(val titulo: String, val wikidata: String?, val paises: List<String>?)

    /** Un acontecimiento: su año (negativo, antes de Cristo), su texto y sus artículos. */
    class Acontecimiento(val anio: Int, val texto: String, val articulos: List<Articulo>)

    /** Un suceso mundial: su artículo de Wikidata, las frases con que lo nombra el texto y sus años (incluidos). */
    class Mundial(val wikidata: String, val frases: List<String>, val desde: Int, val hasta: Int)

    /**
     * Lo mundial, con sus fechas. La Guerra Fría no está: duró 44 años y casi cualquier cosa de entonces la nombra
     * («en el marco de la Guerra Fría, un espía…»), pero no afectaba a todos. Tampoco las guerras napoleónicas ni la de
     * los Siete Años, que no fueron de todo el mundo.
     */
    val MUNDIALES = listOf(
        Mundial("Q361", listOf("Primera Guerra Mundial", "Gran Guerra"), 1914, 1918),
        Mundial("Q362", listOf("Segunda Guerra Mundial"), 1939, 1945),
        Mundial("Q8698", listOf("Gran Depresión"), 1929, 1939),
        Mundial("Q178275", listOf("gripe española", "gripe de 1918"), 1918, 1920),
        Mundial("Q42005", listOf("peste negra", "Peste negra"), 1346, 1353),
        Mundial("Q81068910", listOf("COVID-19"), 2020, 2023),
        Mundial("Q43653", listOf("Apolo 11"), 1969, 1969),
    )

    /** Los años de su época, o null si no tiene (inventada o de hoy). */
    fun de(v: OtrasVidas.Vida): IntRange? = if (v.ficticia || v.anio >= DESDE_HOY) null else (v.anio - MARGEN)..(v.anio + MARGEN)

    /** Los años que vivió hasta el final de su temporada 5, para lo mundial; null si no tiene época. */
    fun vida(v: OtrasVidas.Vida): IntRange? = if (de(v) == null) null else (v.anio - v.edad)..(v.anio + ANIOS_DE_TEMPORADAS)

    /** Si el acontecimiento es uno de [MUNDIALES], dentro de sus fechas. */
    fun esMundial(a: Acontecimiento): Boolean {
        val qs = a.articulos.mapNotNull { it.wikidata }.toSet()
        val texto = todo(a)
        return MUNDIALES.any { m -> a.anio in m.desde..m.hasta && (m.wikidata in qs || m.frases.any { nombra(texto, it) }) }
    }

    /**
     * La vida de [vidas] a la que lleva el acontecimiento, y por qué; o null. [delTema] desempata a favor de la vida del
     * tema. Las vidas sin [LugarDeVida] solo pueden llegar por lo mundial.
     */
    fun vidaDelAcontecimiento(a: Acontecimiento, vidas: List<OtrasVidas.Vida>, delTema: String? = null): Pair<OtrasVidas.Vida, Motivo>? {
        val texto = todo(a)
        val hablan = a.articulos.mapNotNull { art ->
            val ps = art.paises ?: return@mapNotNull null
            if (ps.size in 1..MAX_PAISES) (ps + listOfNotNull(art.wikidata)).toSet() else null
        }
        val mundial = esMundial(a)
        class Candidata(val v: OtrasVidas.Vida, val motivo: Motivo, val peso: Int, val orden: Int)
        val candidatas = vidas.mapIndexedNotNull { i, v ->
            val epoca = de(v) ?: return@mapIndexedNotNull null
            val lugar = LUGARES[v.id]
            var motivo: Motivo? = null; var peso = 0
            if (lugar != null && a.anio in epoca) {
                val limpio = lugar.excepto.fold(texto) { t, x -> t.replace(x, " ") }
                if (hablan.isNotEmpty()) {
                    val n = hablan.count { s -> s.any { it in lugar.wikidata } }
                    if (n > 0 && (2 * n >= hablan.size || lugar.nombres.any { nombra(limpio, it) })) { motivo = Motivo.PAIS; peso = n }
                } else if (lugar.palabras.any { nombra(limpio, it) }) { motivo = Motivo.PALABRAS; peso = 1 }
            }
            if (motivo == null && mundial && a.anio in vida(v)!!) motivo = Motivo.MUNDIAL
            motivo?.let { Candidata(v, it, peso, i) }
        }
        return candidatas.minWithOrNull(compareBy<Candidata>({ -it.peso }, { kotlin.math.abs(it.v.anio - a.anio) },
            { if (it.v.tema != null && it.v.tema == delTema) 0 else 1 }, { it.orden }))?.let { it.v to it.motivo }
    }

    /** El texto y los títulos de los artículos, juntos. */
    private fun todo(a: Acontecimiento) = a.texto + " | " + a.articulos.joinToString(" | ") { it.titulo }

    /**
     * Sin acentos y con los espacios y guiones de Wikipedia que no se parten (`U+00A0`, `U+202F`, `U+2007`, `U+2011`)
     * vueltos normales: Wikipedia escribe «Apolo 11» y «1939‑1945» así (203 espacios en cuatro fechas), y si no, no
     * casarían.
     */
    private fun normal(s: String) = Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
        .replace(Regex("[\u00A0\u202F\u2007]"), " ").replace('\u2011', '-')

    /** Si [texto] nombra [palabra] como palabra entera, sin contar acentos («Tenochtitlán» nombra «Tenochtitlan»). */
    fun nombra(texto: String, palabra: String): Boolean =
        Regex("(?<![\\p{L}\\p{N}])" + Regex.escape(normal(palabra)) + "(?![\\p{L}\\p{N}])").containsMatchIn(normal(texto))
}
