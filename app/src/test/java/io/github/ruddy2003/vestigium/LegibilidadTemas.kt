package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Textos
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Plan de diseño, principio 7: lo que hay que leer para entender un dato (cuánto dormiste, qué día es, cómo se llama una
 * pestaña) se entiende sin saber otra lengua. El latín, el italiano, el francés y el castellano antiguo se parecen al
 * español y valen; el griego, el árabe, el cirílico, el mongol y el chino solo van de adorno o junto a su traducción.
 */
class LegibilidadTemas {
    /** Letras que no se leen sin saber la lengua: griego, cirílico, árabe, mongol y chino. */
    private val AJENAS = Regex("[\\u0370-\\u03FF\\u1F00-\\u1FFF\\u0400-\\u052F\\u0600-\\u06FF\\u1800-\\u18AF\\u3000-\\u303F\\u4E00-\\u9FFF]")
    private val LATINAS = Regex("[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]")

    /** Las claves de las líneas «clave = valor» que se ven en Día, en el widget y en los reportes. */
    private val CLAVES = listOf("horologium", "somnus", "stadia", "cor", "passus", "motus", "iter", "exercitatio", "loca", "telephonum", "usus",
        "monita", "musica", "imagines", "fasti", "caelum", "lux", "aer", "corpus", "nutritio", "cibus", "robur", "musculi", "voces", "soni",
        "retia", "lumen", "vis", "lectio", "strepitus", "luna", "animus", "momenta", "svmptvs", "opera", "iudicium", "citatio", "sententia",
        "orbis", "nova", "mirabilia", "somnium", "epistulae", "aperta", "consuetudo", "series", "palmae", "reditus")

    @Test fun loQueSeLeeSeEntiende() {
        val errores = mutableListOf<String>()
        TemaId.entries.forEach { t ->
            Pendientes.conTema(t) {
                fun sinAjenas(que: String, valor: String) { if (AJENAS.containsMatchIn(valor)) errores += "${t.id} · $que: «$valor»" }
                CLAVES.forEach { sinAjenas("clave $it", Estilo.clave(it)) }
                Textos.CLAVES.forEach { sinAjenas("texto $it", Estilo.texto(it)) }
                CoberturaTemas.CLAVES_WIDGET.forEach { sinAjenas("widget $it", Estilo.widget(it)) }
                Estilo.semana.forEach { sinAjenas("semana", it) }
                Estilo.semanaCorta.forEach { sinAjenas("semana corta", it) }
                (1..12).forEach { sinAjenas("mes $it", Estilo.mes(it)); sinAjenas("mes $it con año", Estilo.mes(it, 2026)) }
                (listOf(1, 2, 7, 10, 17, 31, 120, 2026)).forEach { sinAjenas("numeral $it", Estilo.numeral(it)) }
                listOf("2026-01-01", "2026-10-17", "2026-12-25").forEach { sinAjenas("fecha $it", Estilo.fecha(it)) }
                sinAjenas("racha", Estilo.racha(5))
                sinAjenas("atrás", Estilo.atras)
                // Los nombres de pantalla pueden ir en su lengua, pero cuando van solos llevan su español al lado.
                UnicidadTemas.PANTALLAS.forEach { p ->
                    val n = Estilo.pantallaLegible(p, null)
                    if (!LATINAS.containsMatchIn(n)) errores += "${t.id} · pantalla $p: «$n» no tiene nada que se lea"
                }
            }
        }
        assertTrue("Textos que no se leen:\n" + errores.joinToString("\n"), errores.isEmpty())
    }
}
