package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.Musica
import io.github.ruddy2003.vestigium.ui.RelojModo
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.caraSegun
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los sonidos de los temas con caras (Psicodelia v2, tanda 7; `docs/diseno/temas/psicodelia.md`, «Sonido»): un juego por
 * cara en lo que suena dentro de la app, que sigue la cara **de la hora** y nunca la intensidad (un consumo no debe oírse).
 * Los avisos y los tonos del teléfono no cambian con la cara (los vigilan `AvisosTemaPrueba` y `CoberturaTemas`).
 */
class SonidosCaraPrueba {
    private val eventos = listOf("toque", "guardar", "logro", "pasar", "elegir", "abrir", "tema", "error", "cambio")

    /** El nombre del recurso de sonido, sacado de R.raw por reflexión (las pruebas no tienen `Resources`). */
    private fun nombre(id: Int): String = R.raw::class.java.fields.first { it.getInt(null) == id }.name
    private fun bytes(nombre: String): ByteArray = Pendientes.main("res/raw/$nombre.ogg").readBytes()

    @Test fun cadaCaraTieneSuJuegoConSusNombres() {
        val conCaras = TemaId.entries.filter { it.conCaras }
        assertTrue("ningún tema con caras", conCaras.isNotEmpty())
        conCaras.forEach { t ->
            val base = Sentidos.SONIDOS.getValue(t.id).map(::nombre)
            val intensa = Sentidos.SONIDOS_INTENSA.getValue(t.id).map(::nombre)
            assertEquals(eventos.map { "s_${t.id}_$it" } + listOf("s_tecla_${t.id}", "s_rascar_${t.id}"), base)
            // El rascado es el mismo en las dos caras: es la misma moneda.
            assertEquals(eventos.map { "s_${t.id}_tinta_$it" } + listOf("s_tecla_${t.id}_tinta", "s_rascar_${t.id}"), intensa)
            intensa.forEach { assertTrue("falta res/raw/$it.ogg", Pendientes.main("res/raw/$it.ogg").exists()) }
            // Cada uno de los otros diez es otro archivo y no es una copia del de la ciudad.
            base.zip(intensa).dropLast(1).forEach { (b, i) -> assertFalse("$i es una copia de $b", bytes(b).contentEquals(bytes(i))) }
            assertEquals("s_${t.id}_presentacion", nombre(Musica.presentacion(t, Cara.BASE)))
            assertEquals("s_${t.id}_tinta_presentacion", nombre(Musica.presentacion(t, Cara.INTENSA)))
            assertEquals("s_${t.id}_anual", nombre(Musica.anual(t, Cara.BASE)))
            assertEquals("s_${t.id}_tinta_anual", nombre(Musica.anual(t, Cara.INTENSA)))
            assertTrue(Sentidos.sonidosDe(t, Cara.INTENSA).contentEquals(Sentidos.SONIDOS_INTENSA[t.id]))
            assertTrue(Sentidos.sonidosDe(t, Cara.BASE).contentEquals(Sentidos.SONIDOS[t.id]))
        }
    }

    @Test fun losTemasSinCarasSuenanIgualEnLasDos() {
        TemaId.entries.filterNot { it.conCaras }.forEach { t ->
            assertFalse(t.id, t.id in Sentidos.SONIDOS_INTENSA)
            assertTrue(t.id, Sentidos.sonidosDe(t, Cara.INTENSA).contentEquals(Sentidos.SONIDOS[t.id]))
            assertEquals(t.id, Musica.presentacion(t, Cara.BASE), Musica.presentacion(t, Cara.INTENSA))
            assertEquals(t.id, Musica.anual(t, Cara.BASE), Musica.anual(t, Cara.INTENSA))
        }
    }

    @Test fun elSonidoSigueLaHoraYNoLaIntensidad() {
        try {
            EstadoTema.forzar(null, null)
            EstadoTema.poner(1f, 1f)   // un consumo en curso, al máximo
            // La imagen pasa a la cara intensa a cualquier hora...
            assertEquals(Cara.INTENSA, EstadoTema.caraDe(TemaId.PSICODELIA))
            TemaId.entries.filter { it.conCaras }.forEach { t ->
                // ...pero el sonido sigue en la cara de la hora, como si no hubiera ningún consumo.
                val deLaHora = caraSegun(t, RelojModo.noche, 0f)
                assertEquals(t.id, deLaHora, Sentidos.caraDelSonido(t))
                assertEquals(t.id, Musica.presentacion(t, deLaHora), Musica.presentacion(t))
                assertEquals(t.id, Musica.anual(t, deLaHora), Musica.anual(t))
            }
            // Y la cara forzada (capturas, arnés) manda también sobre el sonido.
            EstadoTema.forzar(Cara.INTENSA, 0f)
            assertEquals(Cara.INTENSA, Sentidos.caraDelSonido(TemaId.PSICODELIA))
            EstadoTema.forzar(Cara.BASE, 1f)
            assertEquals(Cara.BASE, Sentidos.caraDelSonido(TemaId.PSICODELIA))
        } finally {
            EstadoTema.forzar(null, null)
            EstadoTema.poner(0f, 0f)
        }
    }
}
