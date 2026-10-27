package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Privado
import io.github.ruddy2003.vestigium.data.privado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** La regla de lo privado: qué momentos y qué variables solo se ven en la pantalla Privado. */
class PrivadoPrueba {
    private fun momento(tipo: String) = Momento(id = tipo, ts = "2026-10-27T12:00:00-06:00", fecha = "2026-10-27", tipo = tipo)

    @Test fun momentosPrivados() {
        listOf("impulso", "recaida", "libido").forEach { assertTrue(it, momento(it).privado) }
        listOf("animo", "muestreo", "alegria", "idea", "pensando_en").forEach { assertFalse(it, momento(it).privado) }
    }

    @Test fun variablesPrivadas() {
        val privadas = listOf("impulso", "recaida", "n_recaida", "recaida_min", "libido_max", "n_libido", "n_impulso", "medicamentos_n",
            "med_paracetamol", "det_estres")
        privadas.forEach { assertTrue(it, Privado.esVariable(it, null)) }
        listOf("valencia", "sueno_horas", "hab_leer", "per_ana", "tema_tesis", "medicina", "detalle").forEach { assertFalse(it, Privado.esVariable(it, null)) }
    }

    @Test fun habitoLigado() {
        assertTrue(Privado.esVariable("hab_sin_x", "sin_x"))
        assertFalse(Privado.esVariable("hab_leer", "sin_x"))
        assertEquals(false, Privado.esVariable("hab_sin_x", null))
    }
}
