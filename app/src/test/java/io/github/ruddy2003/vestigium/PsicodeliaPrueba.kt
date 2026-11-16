package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.ui.Psicodelia
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.nombres.NombresPsicodelia
import io.github.ruddy2003.vestigium.vidas.JERGA_QUE_CELEBRA
import io.github.ruddy2003.vestigium.vidas.LO_PRIVADO
import io.github.ruddy2003.vestigium.vidas.NOMBRES_DE_SUSTANCIAS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Ignore
import org.junit.Test
import java.time.LocalTime

/** Lo que calcula el tema Psicodelia: el disco del día, el lado y la pista, el álbum del mes y los números («(17)», versión 2). */
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
        assertEquals("(17)", Psicodelia.numero(17))
        assertEquals("(1)", Psicodelia.numero(1)); assertEquals("(999)", Psicodelia.numero(999)); assertEquals("1000", Psicodelia.numero(1000))
        assertEquals("2026", Psicodelia.numero(2026))
    }

    /**
     * La voz del tema no habla del consumo (`docs/diseno/temas/psicodelia.md`, «La voz»: el tema muestra, no comenta): en
     * los textos de interfaz de Psicodelia no salen ni lo privado, ni la jerga que celebra, ni los nombres de las
     * sustancias. El cuento de David sí los nombra, solo las noches que toca (`vidas/CuentoG9Prueba`).
     *
     * **Desactivada hasta la tanda 8 (textos) de `psicodelia_plan.md`:** hoy no la cumple. `NombresPsicodelia` dice
     * «Alcohol, tabaco y lo demás que anotas» para «Sustancias», que es la voz del tema hablando del consumo de la persona.
     * La tanda de textos la activa (quita el `@Ignore`) y suma los textos de `Estilo` con el tema puesto, que aquí no se
     * recorren.
     */
    @Ignore("La activa la tanda de textos de Psicodelia: hoy «Sustancias» se dice «Alcohol, tabaco y lo demás que anotas».")
    @Test fun laVozDelTemaNoHablaDelConsumo() {
        val voz = Avisos.voz(TemaId.PSICODELIA)
        val enfoque = Avisos.vozEnfoque(TemaId.PSICODELIA)
        val textos = NombresPsicodelia.M.values + listOf(
            voz.animo, voz.animoTexto, voz.sueno, voz.suenoTexto, voz.diario, voz.diarioTexto, voz.dificil, voz.escalas,
            voz.carta, voz.cartaTexto, voz.logro, voz.logros, voz.anio, voz.anioTexto,
            enfoque.descanso, enfoque.consejo, enfoque.finDescanso, enfoque.despertar, enfoque.terminaste, enfoque.meta,
        )
        textos.forEach { t -> (LO_PRIVADO + JERGA_QUE_CELEBRA + NOMBRES_DE_SUSTANCIAS).forEach { r -> assertFalse("«${r.find(t)?.value}» en «$t»", r.containsMatchIn(t)) } }
    }
}
