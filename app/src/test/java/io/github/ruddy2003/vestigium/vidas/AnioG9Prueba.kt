package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG9
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Los calendarios del grupo 9, los de los temas del lote 10. Por ahora, el de Tadeo, el agente de la red de Frente
 * exterior, que es ficción. Las pruebas generales (forma, certeza, continuidad) están en [AnioPilotoPrueba] y corren
 * solas sobre todo lo registrado en [Calendarios]; aquí va lo propio de su historia y lo que su cuadro va a leer.
 */
class AnioG9Prueba {
    private val c = AnioG9.AGENTE
    private fun ficha(dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun arco(dia: String, id: String) = ficha(dia).arcos.getValue(id)
    private fun estadoEntre(clave: String, desde: String, hasta: String) {
        val a = LocalDate.parse(desde); val b = LocalDate.parse(hasta)
        assertTrue("el día antes de $desde", ficha(a.minusDays(1).toString()).estado != clave)
        var d = a
        while (!d.isAfter(b)) { assertEquals("$d", clave, ficha(d.toString()).estado); d = d.plusDays(1) }
        assertTrue("el día después de $hasta", ficha(b.plusDays(1).toString()).estado != clave)
    }
    /** Cuántos días del año [anio] está en el estado [clave]. */
    private fun diasEn(clave: String, anio: Int) =
        generateSequence(LocalDate.of(anio, 1, 1)) { it.plusDays(1) }.takeWhile { it.year == anio }.count { ficha(it.toString()).estado == clave }

    @Test fun estaTadeoConSuTemaYEsFiccion() {
        assertEquals(listOf("agente_red"), AnioG9.TODOS.map { it.vida })
        assertTrue("No está en el registro", Calendarios.de(c.vida) === c)
        val q = OtrasVidas.porId(c.vida)!!
        assertEquals("frente", q.tema); assertEquals("tema", q.dibujo); assertEquals("Tadeo", q.nombre); assertEquals(32, q.edad)
        assertTrue(q.ficticia)
        assertTrue(q.inspiracion.startsWith("Inspirado en Andor, de Star Wars"))
        // Sus números son de ficción: todos inventados, y están los 24.
        assertEquals(OtrasVidas.MEDIDAS.map { it.id }.toSet(), q.datos.keys)
        assertTrue(q.datos.values.all { it.c == OtrasVidas.Certeza.INVENTADO })
        assertEquals(3, q.historia.size); assertTrue(q.fuentes.isEmpty())
        // Su agenda es la de la ficha del tema (PLAN_TEMAS, 10c): duerme de 2 a 9 y la ventana es de 23 a 2.
        val f = ficha("2027-04-04")
        assertEquals(OtrasVidas.Hacer.DORMIR, f.tramo(5.0)!!.que); assertEquals(OtrasVidas.Hacer.TRABAJO, f.tramo(0.5)!!.que)
        assertEquals("la ventana de transmisión y la misión de la noche", f.tramo(23.5)!!.texto)
        assertEquals(OtrasVidas.Hacer.GENTE, f.tramo(21.0)!!.que)
        // Sin época: es de otro mundo, y sus años son los del Directorio.
        assertNull(Epocas.de(q))
        assertEquals("año 212 del Directorio", ficha("2027-03-03").cuando); assertEquals("año 216 del Directorio", ficha("2031-03-03").cuando)
        assertEquals(36, ficha("2031-03-03").edad)
    }

    @Test fun nadaSaleDeLaSaga() {
        // Ni nombres de la saga ni las palabras que la voz del tema no dice nunca (docs/diseno/temas/frente.md).
        val prohibidas = listOf("imperio", "imperial", "rebelde", "rebelión", "alianza", "la fuerza", "jedi", "sith", "galaxia muy lejana", "andor", "ferrix", "coruscant",
            "aldhani", "kenari", "narkina", "ghorman", "cassian", "luthen", "mon mothma", "bix", "syril", "dedra", "maarva", "soldado de asalto", "bláster", "droide", "hiperespacio")
        val q = OtrasVidas.porId(c.vida)!!
        val textos = c.temporadas.flatMap { t -> listOf(t.cuando, t.resumen) + t.dias.map { it.titulo } } + q.historia + listOf(q.nombre, q.rol, q.lugar, q.cuando) + q.dia.map { it.texto } + q.datos.values.map { it.nota }
        textos.forEach { s -> prohibidas.forEach { assertFalse("«$it» en «${s.take(60)}…»", Regex("""\b$it\b""").containsMatchIn(s.lowercase())) } }
    }

    @Test fun laRedAvanzaDeTemporadaEnTemporada() {
        assertEquals(listOf("cuota", "patrullas", "caida", "ocupacion", "golpe"), c.temporadas.map { it.mundo })
        // El reclutamiento, las primeras misiones, la caída, la fuga y el golpe, cada uno en su año.
        assertEquals(listOf("02-24", null, null, null, null), fechas("recluta"))
        assertEquals(listOf("12-06", null, null, null, null), fechas("juramento"))
        assertEquals(listOf(null, "07-14", null, null, null), fechas("sube_cresta"))
        assertEquals(listOf(null, null, "10-02", null, null), fechas("redada"))
        assertEquals(listOf(null, null, null, "07-09", null), fechas("fuga"))
        assertEquals(listOf(null, null, null, null, "06-29"), fechas("golpe"))
        // Lo que vuelve cada año está en las cinco; en la temporada 3 el censo lo pilla escondido y lleva otro id.
        listOf("cambio_anio", "cuota", "casco_nuevo", "lamparas", "concordia", "convoy", "feria", "caidos", "carbon", "noche_larga").forEach { id ->
            assertTrue(id, fechas(id).all { it != null })
        }
        assertEquals(listOf("10-15", "10-15", null, "10-15", "10-15"), fechas("censo"))
        assertEquals(listOf(null, null, "10-15", null, null), fechas("censo_escondido"))
        // En la temporada 5 los sucesos de su vida no cuelgan de los días del golpe, que no se mueven: no hay cadenas.
        assertTrue(c.temporadas[4].dias.none { it.cadena != null })
        assertTrue(c.temporadas[4].dias.filter { it.id in setOf("sube_golpe", "golpe") }.all { it.tipo == TipoDia.GUERRA })
    }

    @Test fun subeALaCrestaDosVecesYSeEscondeEnLaMina() {
        estadoEntre("cresta", "2028-07-14", "2028-07-17")
        estadoEntre("escondido", "2029-10-02", "2029-10-29")
        estadoEntre("cresta", "2031-06-26", "2031-06-28")
        assertEquals(0, diasEn("cresta", 2027)); assertEquals(0, diasEn("escondido", 2027)); assertEquals(0, diasEn("escondido", 2030))
        // El censo de la temporada 3 cae mientras está escondido.
        assertEquals("escondido", ficha("2029-10-15").estado); assertEquals("censo_escondido", ficha("2029-10-15").principal!!.dia.id)
        // Cuando la temporada 5 se repite, los días del golpe no se mueven: siempre son tres días en la Cresta.
        (2032..2036).forEach { y -> assertEquals("$y", 3, diasEn("cresta", y)); assertEquals("cresta", ficha("$y-06-27").estado) }
    }

    @Test fun elCuadroPuedeContarLoMismoQueElCuento() {
        // El casco nuevo llega entero cada 10 de marzo y el desguace se lo come a lo largo del año.
        (2027..2031).forEach { y ->
            assertTrue(arco("$y-03-09", "casco") < 0.05); assertEquals(1.0, arco("$y-03-10", "casco"), 0.0)
            assertTrue(arco("$y-09-01", "casco") < arco("$y-04-01", "casco"))
            assertEquals("casco", ficha("$y-03-10").escena)
        }
        // La estación de radar se enciende en la temporada 2 y se queda ciega con el golpe.
        assertEquals(0.0, arco("2028-04-29", "radar"), 0.0); assertEquals(1.0, arco("2028-04-30", "radar"), 0.0)
        assertEquals(1.0, arco("2031-06-28", "radar"), 0.0); assertEquals(0.5, arco("2031-06-29", "radar"), 0.0)
        // La relojería, entera hasta la redada, quemada hasta que Lis la abre, y reabierta.
        assertEquals(0.0, arco("2029-10-01", "relojeria"), 0.0); assertEquals(1.0, arco("2029-10-02", "relojeria"), 0.0)
        assertEquals(1.0, arco("2031-09-09", "relojeria"), 0.0); assertEquals(0.3, arco("2031-09-10", "relojeria"), 1e-9)
        assertEquals("humo", ficha("2029-10-02").escena)
        // El crucero de anillo, sobre el puerto de la fuga al golpe.
        assertEquals(0.0, arco("2030-07-11", "crucero"), 0.0); assertEquals(1.0, arco("2030-07-12", "crucero"), 0.0)
        assertEquals(1.0, arco("2031-07-02", "crucero"), 0.0); assertEquals(0.0, arco("2031-07-03", "crucero"), 0.0)
        assertEquals(0.0, arco("2027-12-31", "crucero"), 0.0); assertEquals(0.0, arco("2031-12-31", "crucero"), 0.0)
        // La ocupación sube con Morell, con los controles, con la redada y con el crucero, y baja después del golpe.
        val ocupa = listOf("2027-06-01", "2029-03-15", "2029-06-01", "2029-12-01", "2030-08-01").map { arco(it, "ocupacion") }
        assertEquals("La ocupación no sube", ocupa.sorted(), ocupa)
        assertTrue(arco("2031-08-01", "ocupacion") < arco("2027-06-01", "ocupacion"))
        // La chispa aparece pintada en el muelle en la temporada 1, la Oficina la borra con la redada y vuelve más que nunca con el golpe.
        assertEquals(0.0, arco("2027-10-27", "chispa"), 0.0); assertTrue(arco("2027-10-28", "chispa") > 0.0); assertEquals("pintadas", ficha("2027-10-28").escena)
        assertTrue(arco("2029-10-02", "chispa") < arco("2029-10-01", "chispa"))
        assertEquals(1.0, arco("2031-07-03", "chispa"), 0.0)
        // Los días que cambian lo que se ve.
        assertEquals("desfile", ficha("2027-06-12").escena); assertEquals("golpe", ficha("2031-06-29").escena)
        assertEquals("toque", ficha("2028-09-10").escena); assertEquals("redada", ficha("2030-07-15").escena)
    }
}
