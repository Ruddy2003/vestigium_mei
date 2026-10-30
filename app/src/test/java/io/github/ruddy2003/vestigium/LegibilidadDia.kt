package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TemaId
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Plan de diseño, principio 7: en el reporte del día (y en sus capturas) lo que hay que leer para entender un dato se
 * entiende. La palabra del tema no se quita: cuando va en griego, cirílico, árabe, mongol o chino, lleva su español al lado.
 */
class LegibilidadDia {
    /** Las claves de las líneas «clave = valor» y de las fichas del reporte del día. */
    private val CLAVES = listOf("horologium", "somnus", "stadia", "cor", "passus", "motus", "iter", "exercitatio", "loca", "telephonum", "usus",
        "monita", "musica", "imagines", "fasti", "caelum", "lux", "aer", "corpus", "nutritio", "cibus", "robur", "musculi", "voces", "soni",
        "retia", "lumen", "vis", "lectio", "strepitus", "luna", "opera", "iudicium", "citatio", "sententia", "orbis", "nova", "mirabilia",
        "somnium", "epistulae", "aperta", "oxygenium", "avisos", "google", "whatsapp")

    @Test fun lasClavesDelDiaSeEntienden() {
        val errores = mutableListOf<String>()
        TemaId.entries.forEach { t ->
            Pendientes.conTema(t) {
                CLAVES.forEach { k ->
                    val es = Estilo.claveEspanol(k)
                    val lee = es ?: Estilo.clave(k)
                    if (Estilo.ajeno(lee)) errores += "${t.id} · $k: «$lee» no se lee"
                    if (es != null && !Estilo.ajeno(Estilo.clave(k))) errores += "${t.id} · $k: lleva español sin hacer falta"
                }
                val llenar = Estilo.conEspanol(Estilo.seccion("Llenar el día", null).first, "Llenar el día")
                if (!Regex("[A-Za-zÁÉÍÓÚÑáéíóúñ]").containsMatchIn(llenar)) errores += "${t.id} · Llenar el día: «$llenar»"
            }
        }
        assertTrue("Claves del día que no se leen:\n" + errores.joinToString("\n"), errores.isEmpty())
    }
}
