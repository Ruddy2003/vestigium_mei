package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * `DiasEspeciales` (parte 8): los días especiales de hoy de los personajes que sigues y los ya pasados, para leerlos
 * después. Con los calendarios de verdad: los futuros nunca salen y lo de hoy es lo mismo que dice la ficha.
 */
class DiasEspecialesPrueba {
    @Test fun unAnioEnteroTraeTodosLosDeSuTemporada() {
        // En 2027 (temporada 1, sin sorteo) cada día especial de la temporada empieza una vez, dentro del año.
        Calendarios.TODOS.forEach { cal ->
            val l = DiasEspeciales.pasados(cal.vida, "2027-12-31", "2027-01-01")
            assertEquals(cal.vida, cal.temporadas[0].dias.map { it.id }.sorted(), l.map { it.id }.sorted())
            assertTrue(cal.vida, l.all { it.inicio.year == 2027 })
        }
    }

    @Test fun nuncaLosFuturos() {
        val hoy = LocalDate.parse("2028-06-30")
        Calendarios.TODOS.forEach { cal ->
            val l = DiasEspeciales.pasados(cal.vida, hoy.toString())
            assertTrue(cal.vida, l.all { !it.inicio.isAfter(hoy) && !it.inicio.isBefore(LocalDate.parse(DiasEspeciales.PRIMER_DIA)) })
            // Del más reciente al más viejo, sin repetir.
            assertEquals(cal.vida, l.sortedByDescending { it.inicio }.map { it.inicio }, l.map { it.inicio })
            assertEquals(cal.vida, l.size, l.map { it.id to it.inicio }.toSet().size)
        }
    }

    @Test fun elQueEmpiezaHoyYaSale() {
        val cal = Calendarios.TODOS.first()
        val primero = DiasEspeciales.pasados(cal.vida, "2027-12-31", "2027-01-01").minByOrNull { it.inicio }!!
        val dia = primero.inicio.toString()
        assertTrue(DiasEspeciales.pasados(cal.vida, dia, "2027-01-01").any { it.id == primero.id && it.inicio == primero.inicio })
        assertFalse(DiasEspeciales.pasados(cal.vida, primero.inicio.minusDays(1).toString(), "2027-01-01").any { it.id == primero.id && it.inicio == primero.inicio })
        assertEquals(dia, primero.diaDelCuento)
    }

    @Test fun hoyEsLoDeLaFicha() {
        val cal = Calendarios.TODOS.first()
        val e = DiasEspeciales.pasados(cal.vida, "2027-12-31", "2027-01-01").last()
        val dia = e.inicio.toString()
        val deHoy = DiasEspeciales.hoy(cal.vida, dia)
        assertEquals(FichaDelDia.de(dia, cal.vida)!!.especiales, deHoy)
        assertTrue(deHoy.any { it.dia.id == e.id && it.inicio == e.inicio })
        // Entre varios, solo los que hoy tienen día especial, en el orden dado y sin repetir.
        val otro = Calendarios.TODOS.map { it.vida }.firstOrNull { DiasEspeciales.hoy(it, dia).isEmpty() }
        val vidas = listOfNotNull(otro, cal.vida, cal.vida)
        assertEquals(listOf(cal.vida), DiasEspeciales.conEspecialHoy(vidas, dia).map { it.first })
    }

    @Test fun fechasQueNoValen() {
        val v = Calendarios.TODOS.first().vida
        assertTrue(DiasEspeciales.pasados(v, "no").isEmpty())
        assertTrue(DiasEspeciales.pasados(v, "2027-01-01", "2027-02-01").isEmpty())
        assertTrue(DiasEspeciales.pasados("no_existe", "2027-12-31").isEmpty())
        assertTrue(DiasEspeciales.hoy(v, "no").isEmpty())
    }
}
