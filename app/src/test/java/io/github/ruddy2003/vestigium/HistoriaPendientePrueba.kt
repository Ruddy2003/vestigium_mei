package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.avisos.HistoriaPendiente
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.CuentosLeidos
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.Seguir
import io.github.ruddy2003.vestigium.ui.seguir.SeguirDatos
import io.github.ruddy2003.vestigium.vidas.DiasEspeciales
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Parte 8, encargo A: qué avisar de los días especiales de quien sigues (`avisos/HistoriaPendiente.kt`) y qué muestran
 * el botón, las banderitas de Hoy y la lista de días pasados (`ui/seguir/SeguirDatos.kt`). Con los calendarios de
 * verdad; las fechas y los instantes son inventados.
 */
class HistoriaPendientePrueba {
    /** Un día especial de 2027 de [vida] que dure al menos [dias] días, con su fecha de inicio. */
    private fun especial(vida: String, dias: Int = 1) =
        DiasEspeciales.pasados(vida, "2027-12-31", "2027-01-01").firstOrNull { it.dia.duracion >= dias }

    /** Un personaje y un día especial suyo de varios días (una cosecha, una fiesta larga). */
    private val largo by lazy {
        Calendarios.TODOS.firstNotNullOf { cal -> especial(cal.vida, 3)?.takeIf { it.inicio.plusDays(2).year == 2027 } }
    }

    private val ts = "2027-05-01T10:00:00-06:00"

    private fun siguiendo(vararg vidas: String, hoy: String = "2027-01-01") = vidas.fold(Config()) { c, v -> Seguir.seguir(c, v, hoy) }

    @Test fun avisaUnaVezPorDiaEspecialAunqueDureVariosDias() {
        val e = largo
        var c = siguiendo(e.vida)
        val primero = e.inicio.toString()
        val a = HistoriaPendiente.decidir(c, primero, 10)
        assertNotNull(a)
        assertTrue(a!!.claves.contains(Seguir.claveAviso(e.vida, e.id, primero)))
        c = HistoriaPendiente.anotar(c, a, ts, primero)
        // El mismo día, en el pulso siguiente: nada.
        assertNull(HistoriaPendiente.decidir(c, primero, 11))
        // Al día siguiente sigue siendo el mismo día especial: no vuelve a avisar (si no empieza otro).
        val segundo = e.inicio.plusDays(1).toString()
        val otro = HistoriaPendiente.decidir(c, segundo, 10)
        assertTrue(otro == null || otro.pendientes.none { p -> p.especiales.any { it.dia.id == e.id && it.inicio == e.inicio } })
    }

    @Test fun siEmpiezasASeguirloAMitadDeUnoTeAvisaEseDia() {
        val e = largo
        val mitad = e.inicio.plusDays(1).toString()
        val c = siguiendo(e.vida, hoy = mitad)
        val a = HistoriaPendiente.decidir(c, mitad, 12)!!
        assertTrue(a.claves.contains(Seguir.claveAviso(e.vida, e.id, e.inicio.toString())))
    }

    @Test fun soloEnSuHorarioYConLosInterruptores() {
        val e = especial(Calendarios.TODOS.first().vida)!!
        val dia = e.inicio.toString()
        val c = siguiendo(e.vida).copy(hora_sueno = 8)
        assertNull(HistoriaPendiente.decidir(c, dia, 7))
        assertNotNull(HistoriaPendiente.decidir(c, dia, 8))
        assertNotNull(HistoriaPendiente.decidir(c, dia, 20))
        assertNull(HistoriaPendiente.decidir(c, dia, 21))
        assertNull(HistoriaPendiente.decidir(c.copy(avisos = false), dia, 10))
        assertNull(HistoriaPendiente.decidir(c.copy(avisos_historia = false), dia, 10))
        // Sin seguir a nadie, nada; al dejar de seguirlo, tampoco.
        assertNull(HistoriaPendiente.decidir(Config(), dia, 10))
        assertNull(HistoriaPendiente.decidir(Seguir.dejar(c, e.vida, dia), dia, 10))
    }

    @Test fun unSoloAvisoJuntaATodosYSoloLlevaNombreYTitulo() {
        // Un día en que al menos dos personajes con calendario tienen día especial.
        val (dia, dos) = (0 until 365).asSequence().map { LocalDate.parse("2027-01-01").plusDays(it.toLong()).toString() }
            .map { d -> d to DiasEspeciales.conEspecialHoy(Calendarios.TODOS.map { it.vida }, d).map { it.first } }
            .first { it.second.size >= 2 }
        val c = siguiendo(dos[0], dos[1])
        val a = HistoriaPendiente.decidir(c, dia, 10)!!
        assertEquals(dos.take(2), a.pendientes.map { it.vida })
        // Varios: abre Hoy; el texto es «Hoy <nombre>: <título>. <nombre>: <título>».
        assertEquals("dia", a.destino)
        val esperado = "Hoy " + dos.take(2).joinToString(". ") { v ->
            OtrasVidas.porId(v)!!.nombre + ": " + DiasEspeciales.hoy(v, dia).joinToString(" y ") { it.dia.titulo }
        }
        assertEquals(esperado, a.texto)
        // Uno solo: abre su cuento.
        val uno = HistoriaPendiente.decidir(siguiendo(dos[0]), dia, 10)!!
        assertEquals("cuento?vida=${dos[0]}", uno.destino)
        // Avisado uno, el otro sigue pendiente, y solo ese sale.
        val c2 = HistoriaPendiente.anotar(c, uno, ts, dia)
        assertEquals(listOf(dos[1]), HistoriaPendiente.decidir(c2, dia, 10)!!.pendientes.map { it.vida })
    }

    @Test fun elAvisoSeOlvidaALos400Dias() {
        val e = especial(Calendarios.TODOS.first().vida)!!
        val dia = e.inicio.toString()
        val c = HistoriaPendiente.anotar(siguiendo(e.vida), HistoriaPendiente.decidir(siguiendo(e.vida), dia, 10)!!, ts, dia)
        assertTrue(c.historia_avisada.isNotEmpty())
        val despues = e.inicio.plusDays(Seguir.DIAS_AVISO + 1).toString()
        assertTrue(Seguir.podarAvisos(c, despues).historia_avisada.isEmpty())
    }

    // ---------------------------------------------------------------- Lo que muestran las pantallas

    @Test fun elBotonDiceLoQuePuedeHacer() {
        val vidas = SeguirDatos.seguibles().map { it.id }
        assertTrue(vidas.size > Seguir.TOPE)
        var c = Config()
        assertEquals(SeguirDatos.Estado.SEGUIR, SeguirDatos.estado(c, vidas[0]))
        vidas.take(Seguir.TOPE).forEach { c = Seguir.seguir(c, it, "2027-01-01") }
        assertEquals(SeguirDatos.Estado.SIGUES, SeguirDatos.estado(c, vidas[0]))
        assertEquals(SeguirDatos.Estado.LLENO, SeguirDatos.estado(c, vidas[Seguir.TOPE]))
        c = Seguir.dejar(c, vidas[0], "2027-01-02")
        assertEquals(SeguirDatos.Estado.SEGUIR, SeguirDatos.estado(c, vidas[Seguir.TOPE]))
    }

    @Test fun seSiguenTodasLasVidasConCalendarioNoSoloLasDelTema() {
        val l = SeguirDatos.seguibles()
        assertEquals(Calendarios.TODOS.map { it.vida }.toSet(), l.map { it.id }.toSet())
        // Las doce comunes (sin tema) y las de los temas.
        assertTrue(l.any { it.tema == null })
        assertTrue(l.any { it.tema != null })
        assertEquals(l.sortedBy { it.anio }.map { it.anio }, l.map { it.anio })
    }

    @Test fun lasBanderitasDeHoyYSiYaLoLeiste() {
        val e = especial(Calendarios.TODOS.first().vida)!!
        val dia = e.inicio.toString()
        var c = siguiendo(e.vida)
        val m = SeguirDatos.marcasDeHoy(c, dia)
        assertEquals(1, m.size)
        assertEquals(DiasEspeciales.hoy(e.vida, dia).first().dia.titulo, m[0].titulo)
        assertEquals(OtrasVidas.porId(e.vida)!!.tema, m[0].tema)
        assertFalse(m[0].leido)
        c = CuentosLeidos.marcar(c, e.vida, dia, dia)
        assertTrue(SeguirDatos.marcasDeHoy(c, dia)[0].leido)
        // Sin seguir a nadie, ninguna.
        assertTrue(SeguirDatos.marcasDeHoy(Config(), dia).isEmpty())
    }

    @Test fun losPasadosPorAnioSinFuturosYConSuLectura() {
        val vida = Calendarios.TODOS.first().vida
        val hoy = "2028-03-15"
        val l = SeguirDatos.pasadosPorAnio(Config(), vida, hoy)
        assertEquals(l.map { it.first }.sortedDescending(), l.map { it.first })
        assertTrue(l.all { (anio, xs) -> xs.all { it.especial.inicio.year == anio && !it.especial.inicio.isAfter(LocalDate.parse(hoy)) } })
        assertEquals(DiasEspeciales.pasados(vida, hoy).size, l.sumOf { it.second.size })
        assertTrue(l.flatMap { it.second }.none { it.leido })
        // Leído (aunque sea después), sale marcado.
        val uno = l.first().second.first().especial
        val c = CuentosLeidos.marcar(Config(), vida, uno.diaDelCuento, hoy)
        assertTrue(SeguirDatos.pasadosPorAnio(c, vida, hoy).first().second.first().leido)
        assertEquals("cuento?vida=$vida&dia=${uno.diaDelCuento}", SeguirDatos.rutaCuento(vida, uno.diaDelCuento))
    }
}
