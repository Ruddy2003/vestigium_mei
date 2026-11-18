package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Presencias
import io.github.ruddy2003.vestigium.data.Repo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Esquema 28: «llamada» aparte de «mensajes» en las presencias (con datos inventados). Lo que ya existía tiene que dar lo
 * mismo con una llamada que con un mensaje (hasta el esquema 27 las llamadas se marcaban como mensaje); lo nuevo,
 * `personas_llamadas`, cuenta las llamadas y queda vacío antes del primer día con una.
 */
class LlamadaPrueba {
    private val f = "2026-11-18"
    private val conMensaje = Dia(f, valencia = 5, nota = "Hablé con @ana y comí con @luis.", presencias = mapOf("ana" to Presencias.MENSAJES))
    private val conLlamada = conMensaje.copy(presencias = mapOf("ana" to Presencias.LLAMADA))

    @Test fun laLlamadaValeComoMensajeEnLoQueYaExistia() {
        val m = Presencias.delDia(f, emptyMap(), conMensaje, emptyList())
        val l = Presencias.delDia(f, emptyMap(), conLlamada, emptyList())
        assertEquals(Presencias.LLAMADA, l["ana"])
        assertEquals(m.count { Presencias.aDistancia(it.value) }, l.count { Presencias.aDistancia(it.value) })
        // La columna «presencias» de la exportación la escribe como antes.
        assertEquals(m.mapValues { Presencias.comoAntes(it.value) }, l.mapValues { Presencias.comoAntes(it.value) })
        assertEquals(Presencias.ESTUVE, Presencias.comoAntes(Presencias.ESTUVE))
    }

    @Test fun elToquePasaPorLaLlamada() {
        val vuelta = generateSequence(Presencias.ESTUVE) { Presencias.siguiente(it) }.take(5).toList()
        assertEquals(listOf(Presencias.ESTUVE, Presencias.PENSE, Presencias.MENSAJES, Presencias.LLAMADA, Presencias.ESTUVE), vuelta)
        assertEquals("llamada", Presencias.nombre(Presencias.LLAMADA))
        assertEquals("mensaje", Presencias.nombre(Presencias.MENSAJES))
    }

    @Test fun elPanelCuentaLaLlamadaComoMensajeYAparte() {
        val antes = Repo.dias
        try {
            Repo.diasEnMemoria(mapOf(f to conMensaje))
            val pm = Estadistica.panel(dinamicas = false)[f]!!
            Repo.diasEnMemoria(mapOf(f to conLlamada))
            val pl = Estadistica.panel(dinamicas = false)[f]!!
            assertEquals(1.0, pm["personas_mensajes"]); assertEquals(1.0, pl["personas_mensajes"])
            assertEquals(pm["personas_vistas"], pl["personas_vistas"]); assertEquals(pm["personas_pensadas"], pl["personas_pensadas"])
            assertNull(pm["personas_llamadas"]); assertEquals(1.0, pl["personas_llamadas"])
            // Antes del primer día con una llamada, la columna nueva queda vacía; lo de siempre, igual.
            val ayer = "2026-11-17"
            Repo.diasEnMemoria(mapOf(ayer to conMensaje.copy(fecha = ayer), f to conLlamada))
            val p = Estadistica.panel(dinamicas = false)
            assertNull(p[ayer]!!["personas_llamadas"]); assertEquals(1.0, p[ayer]!!["personas_mensajes"])
            assertEquals(1.0, p[f]!!["personas_llamadas"]); assertEquals(1.0, p[f]!!["personas_mensajes"])
        } finally {
            Repo.diasEnMemoria(antes)
        }
    }
}
