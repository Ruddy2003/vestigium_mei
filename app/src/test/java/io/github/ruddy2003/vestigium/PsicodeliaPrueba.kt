package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Psicodelia
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

/** Lo que calcula el tema Psicodelia: el disco del día, el lado y la pista, el álbum del mes y los números. */
class PsicodeliaPrueba {
    @Test fun fecha() {
        assertEquals("Disco 290 · sábado 17 de octubre", Psicodelia.fecha("2026-10-17"))
        assertEquals("Disco 1 · viernes 1 de enero · lado B, pista 10", Psicodelia.fecha("2027-01-01", LocalTime.of(21, 5)))
        assertEquals("Disco 1 · viernes 1 de enero · lado A, pista 1", Psicodelia.fecha("2027-01-01", LocalTime.of(0, 30)))
    }

    @Test fun ladoYPista() {
        assertEquals("A", Psicodelia.lado(11)); assertEquals("B", Psicodelia.lado(12))
        assertEquals(12, Psicodelia.pista(11)); assertEquals(1, Psicodelia.pista(12)); assertEquals(12, Psicodelia.pista(23))
    }

    @Test fun mesYNumeros() {
        assertEquals("Álbum de octubre", Psicodelia.mes(10))
        assertEquals("Álbum de junio de 1967", Psicodelia.mes(6, 1967))
        assertEquals("~17~", Psicodelia.numero(17))
        assertEquals("2026", Psicodelia.numero(2026))
    }
}
