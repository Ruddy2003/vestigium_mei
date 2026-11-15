package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Nouveau
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/** Lo que calcula el tema Art nouveau: la página del almanaque, la hora de París, el mes y los números. */
class NouveauPrueba {
    @Test fun fecha() {
        assertEquals("samedi 17 octobre · Taminier — soyez mon appui", Nouveau.fecha("2026-10-17"))
        assertEquals("vendredi 1 janvier · Absinthe — absence · 9 h 41 du matin, heure de Paris", Nouveau.fecha("2027-01-01", LocalTime.of(9, 41)))
    }

    @Test fun flores() {
        assertEquals(275, Nouveau.FLORES.size)
        assertEquals(275, Nouveau.FLORES.map { it.nombre }.toSet().size)
        // Las 275 salen una vez en los primeros 275 días del año.
        val dias = (0 until 275).map { Nouveau.flor(LocalDate.of(2027, 1, 1).plusDays(it.toLong())) }.toSet()
        assertEquals(275, dias.size)
        assertTrue(Nouveau.FLORES.all { it.sentido.first().isLowerCase() })
        assertEquals("Pivoine", Nouveau.flor(LocalDate.of(2028, 12, 31)).nombre)
    }

    @Test fun hora() {
        assertEquals("9 h 41 du matin", Nouveau.hora(9, 41))
        assertEquals("12 h 05 de l'après-midi", Nouveau.hora(12, 5))
        assertEquals("11 h 30 du soir", Nouveau.hora(23, 30))
        assertEquals("12 h 10 de la nuit", Nouveau.hora(0, 10))
    }

    @Test fun mesesYNumeros() {
        assertEquals("Almanach de juillet", Nouveau.mes(7))
        assertEquals("Almanach d'avril 2026", Nouveau.mes(4, 2026))
        assertEquals("Almanach d'octobre", Nouveau.mes(10))
        assertEquals("– 17 –", Nouveau.numero(17))
        assertEquals("2026", Nouveau.numero(2026))
    }
}
