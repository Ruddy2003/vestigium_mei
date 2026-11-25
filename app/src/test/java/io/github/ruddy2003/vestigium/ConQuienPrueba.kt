package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Catalogo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Esquema 29 (D.201): «con quién» cuenta como acompañado solo si hay alguien en persona. «Por mensaje o llamada» no cuenta,
 * vaya solo o con «solo»; la mascota sí, igual que antes. Decisión del autor del 25 nov 2026 (H-18-2).
 */
class ConQuienPrueba {
    @Test fun enPersona() {
        assertTrue(Catalogo.acompanado(listOf("amigos")))
        assertTrue(Catalogo.acompanado(listOf("pareja", "mensaje")))
        assertTrue(Catalogo.acompanado(listOf("mascota")))
    }

    @Test fun soloOPorMensaje() {
        assertFalse(Catalogo.acompanado(listOf("solo")))
        assertFalse(Catalogo.acompanado(listOf("mensaje")))
        assertFalse(Catalogo.acompanado(listOf("solo", "mensaje")))
        assertFalse(Catalogo.acompanado(emptyList()))
    }

    @Test fun todasLasOpcionesSalvoSoloYMensajeSonEnPersona() {
        Catalogo.CON_QUIEN.forEach { o -> assertTrue(o.id, Catalogo.acompanado(listOf(o.id)) == (o.id != "solo" && o.id != "mensaje")) }
    }
}
