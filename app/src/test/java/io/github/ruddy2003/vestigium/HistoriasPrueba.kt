package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.CuadrosGuardados
import io.github.ruddy2003.vestigium.data.CuentoLeido
import io.github.ruddy2003.vestigium.data.CuentosLeidos
import io.github.ruddy2003.vestigium.data.EspecialLeido
import io.github.ruddy2003.vestigium.data.Seguido
import io.github.ruddy2003.vestigium.data.Seguir
import io.github.ruddy2003.vestigium.data.UsoDias
import io.github.ruddy2003.vestigium.data.combinarHistorias
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las funciones de `data/Historias.kt` (esquema 27): seguir personajes con su tope, no repetir un aviso, marcar un cuento
 * leído (nunca uno futuro), los días de uso y la galería de cuadros; y cómo se juntan al importar un respaldo. Los ids de
 * personaje son los de `OtrasVidas` (ninguno es una persona real); fechas e instantes, inventados.
 */
class HistoriasPrueba {
    /** Siete personajes que existen en `OtrasVidas`. */
    private val siete = listOf("paleolitico", "neolitico", "egipto", "roma", "medieval", "tenochtitlan", "edo")

    // ---------------------------------------------------------------- Seguir

    @Test fun seguirConTopeDeSeis() {
        var c = Config()
        siete.forEach { c = Seguir.seguir(c, it, "2027-01-03") }
        assertEquals(siete.take(6), Seguir.activos(c))
        assertEquals(Seguir.TOPE, c.seguidos.size)
        // El séptimo no entra: la configuración no cambia.
        assertFalse(Seguir.puedeSeguir(c, "edo"))
        assertSame(c, Seguir.seguir(c, "edo", "2027-01-03"))
        // Dejando a uno, cabe otro.
        c = Seguir.dejar(c, "roma", "2027-01-09")
        assertTrue(Seguir.puedeSeguir(c, "edo"))
        c = Seguir.seguir(c, "edo", "2027-01-10")
        assertEquals(listOf("paleolitico", "neolitico", "egipto", "medieval", "tenochtitlan", "edo"), Seguir.activos(c))
    }

    @Test fun dejarDeSeguirGuardaLaFecha() {
        var c = Seguir.seguir(Config(), "roma", "2027-01-03")
        c = Seguir.dejar(c, "roma", "2027-02-01")
        assertEquals(listOf(Seguido("roma", "2027-01-03", "2027-02-01")), c.seguidos)
        assertFalse(Seguir.sigue(c, "roma"))
        // Volver a seguirlo abre otra entrada: la historia queda.
        c = Seguir.seguir(c, "roma", "2027-03-01")
        assertEquals(2, c.seguidos.size)
        assertTrue(Seguir.sigue(c, "roma"))
        // Dejar a quien no sigues no cambia nada.
        assertSame(c, Seguir.dejar(c, "edo", "2027-03-02"))
    }

    @Test fun noSeSigueLoQueNoExisteNiDosVeces() {
        val c = Seguir.seguir(Config(), "roma", "2027-01-03")
        assertSame(c, Seguir.seguir(c, "roma", "2027-01-04"))
        assertSame(c, Seguir.seguir(c, "no_existe", "2027-01-04"))
        assertSame(c, Seguir.seguir(c, "edo", "no es fecha"))
    }

    @Test fun unAvisoNoSeRepiteYSeOlvidaAlAnio() {
        val clave = Seguir.claveAviso("roma", "saturnales", "2027-12-17")
        assertEquals("roma|saturnales|2027-12-17", clave)
        var c = Seguir.anotarAviso(Config(), clave, "2027-12-17T09:00:00-06:00", "2027-12-17")
        assertTrue(Seguir.avisado(c, clave))
        // Al otro día del mismo día especial ya está avisado.
        assertTrue(Seguir.avisado(Seguir.podarAvisos(c, "2027-12-18"), clave))
        // A los 400 días se recuerda todavía; después, no.
        assertTrue(Seguir.avisado(Seguir.podarAvisos(c, "2029-01-20"), clave))
        assertFalse(Seguir.avisado(Seguir.podarAvisos(c, "2029-01-21"), clave))
        // Una clave rara no se borra.
        c = c.copy(historia_avisada = c.historia_avisada + ("rara" to "x"))
        assertTrue("rara" in Seguir.podarAvisos(c, "2035-01-01").historia_avisada)
    }

    // ---------------------------------------------------------------- Cuentos leídos

    @Test fun soloHoyOElPasado() {
        assertTrue(CuentosLeidos.sePuedeLeer("2027-03-10", "2027-03-10"))
        assertTrue(CuentosLeidos.sePuedeLeer("2026-12-31", "2027-03-10"))
        assertFalse(CuentosLeidos.sePuedeLeer("2027-03-11", "2027-03-10"))
        assertFalse(CuentosLeidos.sePuedeLeer("no", "2027-03-10"))
        // Uno futuro no se marca: la configuración no cambia.
        val c = Config()
        assertSame(c, CuentosLeidos.marcar(c, "roma", "2027-03-11", "2027-03-10"))
        assertSame(c, CuentosLeidos.marcar(c, "", "2027-03-10", "2027-03-10"))
    }

    @Test fun seQuedaLaPrimeraLectura() {
        val esp = listOf(EspecialLeido("saturnales", "2027-12-17"), EspecialLeido("saturnales", "2027-12-17"))
        var c = CuentosLeidos.marcar(Config(), "roma", "2027-12-17", "2027-12-17", "2027-12-17T23:00:00-06:00", esp)
        val l = CuentosLeidos.leido(c, "roma", "2027-12-17")
        assertNotNull(l)
        assertTrue(CuentosLeidos.mismoDia(l!!))
        assertEquals(listOf(EspecialLeido("saturnales", "2027-12-17")), l.especiales)
        // Leerlo otra vez después no cambia la primera lectura.
        assertSame(c, CuentosLeidos.marcar(c, "roma", "2027-12-17", "2027-12-20"))
        // Un día pasado leído después: no es el mismo día.
        c = CuentosLeidos.marcar(c, "roma", "2027-12-01", "2027-12-20")
        assertFalse(CuentosLeidos.mismoDia(CuentosLeidos.leido(c, "roma", "2027-12-01")!!))
        assertEquals(2, c.cuentos_leidos.size)
        assertNull(CuentosLeidos.leido(c, "edo", "2027-12-01"))
    }

    // ---------------------------------------------------------------- Días de uso

    @Test fun diasDeUsoSinRepetirYEnOrden() {
        var c = UsoDias.anotar(Config(), UsoDias.HISTORIA_HOY, "2027-01-05")
        c = UsoDias.anotar(c, UsoDias.HISTORIA_HOY, "2027-01-03")
        c = UsoDias.anotar(c, UsoDias.HISTORIA_HOY, "2027-01-05")
        assertEquals(listOf("2027-01-03", "2027-01-05"), UsoDias.dias(c, UsoDias.HISTORIA_HOY))
        c = UsoDias.anotar(c, UsoDias.tema("heleno"), "2027-01-05")
        assertEquals(listOf("2027-01-05"), UsoDias.dias(c, "tema:heleno"))
    }

    @Test fun soloLasClavesDeLaLista() {
        // Ni el gato (D.190), ni algo privado o de sustancias, ni una clave inventada, ni un día que no es fecha.
        val c = Config()
        listOf("gato", "gato:heleno", "recaida", "cannabis", "tema:", "tema:Heleno", "otra").forEach { k ->
            assertFalse(k, UsoDias.valida(k))
            assertSame(k, c, UsoDias.anotar(c, k, "2027-01-05"))
        }
        assertSame(c, UsoDias.anotar(c, UsoDias.HISTORIA_HOY, "ayer"))
        UsoDias.FIJAS.forEach { assertTrue(it, UsoDias.valida(it)) }
    }

    // ---------------------------------------------------------------- Cuadros guardados

    private fun cuadro(id: String, hora: Double = 13.5, guardado: String = "2027-01-03T13:31:00-06:00") =
        CuadrosGuardados.nuevo(id, "heleno", "remero", "2027-01-03", hora, guardado, listOf("fiesta", "antorchas", "fiesta"))!!

    @Test fun armarUnCuadro() {
        val g = cuadro("a1")
        assertEquals("a1.jpg", g.archivo)
        assertEquals(listOf("antorchas", "fiesta"), g.claves)
        assertNull(CuadrosGuardados.nuevo("con/barra", "heleno", "remero", "2027-01-03", 13.5, "x"))
        assertNull(CuadrosGuardados.nuevo("a2", "heleno", "remero", "2027-13-03", 13.5, "x"))
        assertNull(CuadrosGuardados.nuevo("a2", "heleno", "remero", "2027-01-03", 24.0, "x"))
        assertNull(CuadrosGuardados.nuevo("a2", "", "remero", "2027-01-03", 3.0, "x"))
    }

    @Test fun guardarYQuitar() {
        var c = CuadrosGuardados.guardar(Config(), cuadro("a1"))
        c = CuadrosGuardados.guardar(c, cuadro("b2", hora = 20.0, guardado = "2027-01-04T20:05:00-06:00"))
        // El mismo id no entra dos veces.
        assertSame(c, CuadrosGuardados.guardar(c, cuadro("a1")))
        assertEquals(listOf("b2", "a1"), CuadrosGuardados.todos(c).map { it.id })
        // El «me gusta» se ve puesto en todo el tramo de veinte minutos del cuadro guardado (13:20 a 13:40).
        assertEquals("a1", CuadrosGuardados.yaGuardado(c, "heleno", "remero", "2027-01-03", 13.34)?.id)
        assertNull(CuadrosGuardados.yaGuardado(c, "heleno", "remero", "2027-01-03", 13.7))
        assertNull(CuadrosGuardados.yaGuardado(c, "gotico", "remero", "2027-01-03", 13.5))
        c = CuadrosGuardados.quitar(c, "a1")
        assertEquals(listOf("b2"), c.cuadros_guardados.map { it.id })
        assertSame(c, CuadrosGuardados.quitar(c, "no_esta"))
        // Lo que sobra en la carpeta: los archivos que ningún cuadro usa.
        assertEquals(listOf("a1.jpg", "b2.jpg.nuevo"), CuadrosGuardados.sobrantes(c, listOf("a1.jpg", "b2.jpg", "b2.jpg.nuevo")))
    }

    // ---------------------------------------------------------------- Importar un respaldo

    @Test fun combinarSinPerderNada() {
        val aqui = Config(
            seguidos = listOf(Seguido("roma", "2027-01-03"), Seguido("edo", "2027-01-03", "2027-01-20")),
            avisos_historia = false,
            historia_avisada = mapOf("roma|a|2027-01-05" to "aqui"),
            cuentos_leidos = listOf(CuentoLeido("roma", "2027-01-05", "2027-01-07")),
            uso_dias = mapOf(UsoDias.HISTORIA_HOY to listOf("2027-01-05")),
            cuadros_guardados = listOf(cuadro("a1")),
        )
        val respaldo = Config(
            seguidos = listOf(Seguido("roma", "2027-01-03"), Seguido("roma", "2026-12-01"), Seguido("egipto", "2027-01-02")),
            historia_avisada = mapOf("roma|a|2027-01-05" to "respaldo", "egipto|b|2027-01-02" to "respaldo"),
            cuentos_leidos = listOf(CuentoLeido("roma", "2027-01-05", "2027-01-05"), CuentoLeido("egipto", "2027-01-02", "2027-01-02")),
            uso_dias = mapOf(UsoDias.HISTORIA_HOY to listOf("2027-01-04", "2027-01-05"), "gato" to listOf("2027-01-04")),
            cuadros_guardados = listOf(cuadro("a1"), cuadro("c3")),
        )
        val c = combinarHistorias(aqui, respaldo)
        // Roma ya se sigue aquí: la entrada activa del respaldo con otro inicio no entra; egipto sí.
        assertEquals(listOf(Seguido("roma", "2027-01-03"), Seguido("edo", "2027-01-03", "2027-01-20"), Seguido("egipto", "2027-01-02")), c.seguidos)
        // El cuento de roma del 5: la primera lectura, la del respaldo (el mismo día).
        assertEquals("2027-01-05", c.cuentos_leidos.first { it.vida == "roma" }.leido)
        assertEquals(2, c.cuentos_leidos.size)
        assertEquals(listOf("2027-01-04", "2027-01-05"), c.uso_dias[UsoDias.HISTORIA_HOY])
        assertFalse("gato" in c.uso_dias)
        assertEquals(listOf("a1", "c3"), c.cuadros_guardados.map { it.id })
        assertEquals("aqui", c.historia_avisada["roma|a|2027-01-05"])
        assertEquals("respaldo", c.historia_avisada["egipto|b|2027-01-02"])
        assertFalse(c.avisos_historia)
    }

    @Test fun combinarRespetaElTope() {
        val aqui = Config(seguidos = siete.take(6).map { Seguido(it, "2027-01-03") })
        val respaldo = Config(seguidos = listOf(Seguido("edo", "2027-01-01"), Seguido("edo", "2026-11-01", "2026-12-01")))
        val c = combinarHistorias(aqui, respaldo)
        assertEquals(Seguir.TOPE, Seguir.activos(c).size)
        // La entrada cerrada sí entra: es historia, no ocupa lugar.
        assertTrue(Seguido("edo", "2026-11-01", "2026-12-01") in c.seguidos)
        assertFalse(Seguir.sigue(c, "edo"))
    }

    @Test fun combinarDosVacios() = assertEquals(Config(), combinarHistorias(Config(), Config()))
}
