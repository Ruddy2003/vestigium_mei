package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import io.github.ruddy2003.vestigium.vidas.piezas.TodasLasPiezas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** El rango de épocas de «Hoy en la historia» (tanda H2): qué acontecimiento lleva a qué cuento. */
class EpocasPrueba {
    private fun vida(id: String) = OtrasVidas.porId(id)!!
    private fun todas() = OtrasVidas.BASE + OtrasVidas.DE_TEMAS
    private fun conCuento(tema: String) = OtrasVidas.visibles(tema).filter { Calendarios.de(it.id) != null && TodasLasPiezas.de(it.id) != null }

    @Test fun laEpocaEsElAnioMasMenosTreinta() {
        assertEquals(-461..-401, Epocas.de(vida("remero")))
        assertEquals(1875..1935, Epocas.de(vida("hacienda")))
        assertEquals(80..140, Epocas.de(vida("roma")))
    }

    @Test fun lasVidasInventadasNoTienenEpoca() {
        // Brenna cuenta los años «de los Caminos» y la recolectora «tras el silencio»: no son años nuestros.
        todas().filter { it.ficticia }.forEach { assertNull(it.id, Epocas.de(it)) }
        assertNull(Epocas.vidaDelAnio(312, listOf(vida("guia_caminos"))))
        assertNull(Epocas.vidaDelAnio(40, listOf(vida("semillera"))))
        assertNull("la astronauta es del futuro", Epocas.vidaDelAnio(2024, listOf(vida("astronauta"))))
    }

    @Test fun lasVidasDeHoyTampoco() {
        val deHoy = todas().filter { !it.ficticia && Epocas.de(it) == null }.map { it.id }.toSet()
        assertEquals(setOf("mundo2025", "mexico2025", "restauradora"), deHoy)
        assertNull(Epocas.vidaDelAnio(2015, OtrasVidas.BASE))
    }

    @Test fun cadaAcontecimientoVaALaVidaMasCercana() {
        val base = OtrasVidas.BASE
        assertEquals("hacienda", Epocas.vidaDelAnio(1918, base)?.id)
        assertEquals("tenochtitlan", Epocas.vidaDelAnio(1492, base)?.id)
        assertEquals("roma", Epocas.vidaDelAnio(117, base)?.id)
        assertNull("el 7 a. C. queda lejos de todas", Epocas.vidaDelAnio(-7, base))
        assertNull(Epocas.vidaDelAnio(619, base))
        // Los bordes cuentan.
        assertEquals("hacienda", Epocas.vidaDelAnio(1935, listOf(vida("hacienda")))?.id)
        // En 1935 Rosa (1960) está más cerca que José (1905).
        assertEquals("cdmx1960", Epocas.vidaDelAnio(1935, base)?.id)
        assertNull(Epocas.vidaDelAnio(1936, listOf(vida("hacienda"))))
    }

    @Test fun antesDeCristoYConElTema() {
        assertEquals("remero", Epocas.vidaDelAnio(-430, conCuento("heleno"))?.id)
        // Cayo (Romano) y Lucio (de todos) son del año 110: gana el del tema.
        assertEquals("legionario", Epocas.vidaDelAnio(105, conCuento("romano"), delTema = "romano")?.id)
    }

    @Test fun soloEnlazaVidasConCuento() {
        // Lo que ve la pantalla son las vidas del tema con cuento: Tom y Carmen no tienen.
        listOf("escribiente", "operadora").forEach { id -> assertTrue(id, conCuento(vida(id).tema!!).none { it.id == id }) }
    }
}
