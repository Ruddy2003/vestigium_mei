package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TemaId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El rótulo de un mes («mes del tema · mes común», `Estilo.mesConComun`): si el nombre del tema es el mismo mes común, el mes
 * va una sola vez («noviembre de 2026», no «noviembre · noviembre de 2026»), en cualquier tema.
 */
class MesConComunPrueba {
    @Test fun elMesNoSeRepite() {
        Pendientes.conTema(TemaId.MEDIEVAL) {
            assertEquals("noviembre de 2026", Estilo.mesConComun(11, 2026))
            assertEquals("otubre · octubre de 2026", Estilo.mesConComun(10, 2026))
        }
        val repetidos = TemaId.entries.flatMap { t ->
            Pendientes.conTema(t) {
                (1..12).map { n -> Estilo.mesConComun(n, 2026) }.filter { r ->
                    val partes = r.split(" · ")
                    partes.size == 2 && partes[0].trim().equals(partes[1].removeSuffix(" de 2026").trim(), ignoreCase = true)
                }.map { "${t.id}: $it" }
            }
        }
        assertTrue("Rótulos con el mes repetido: $repetidos", repetidos.isEmpty())
    }
}
