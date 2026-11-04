package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ciencia.Periodo
import io.github.ruddy2003.vestigium.ciencia.Terapia
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.ElementoLista
import io.github.ruddy2003.vestigium.data.Exportar
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Lista
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Obra
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Respaldo
import io.github.ruddy2003.vestigium.data.SCHEMA_V
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Esquema 18: los deseados, las listas libres y lo nuevo de los momentos en pantallas y reportes. Datos inventados. */
class DeseadosPrueba {
    @Test fun obraDeAntesSinCamposNuevos() {
        val o = JSON.decodeFromString(Obra.serializer(), """{"id":"o1","tipo":"libro","titulo":"Un libro","estado":"quiero","agregado":"2026-01-01"}""")
        assertEquals("", o.prioridad); assertEquals(emptyList<String>(), o.recomendo); assertEquals("", o.donde)
    }

    @Test fun listasEnElRespaldo() {
        val l = Lista("l1", "Lugares por visitar", "2026-10-01T10:00:00-06:00",
            elementos = listOf(ElementoLista("a", "Un museo", prioridad = "pronto", agregado = "2026-10-01"), ElementoLista("b", "Un mirador", agregado = "2026-10-02", hecho = "2026-10-12")))
        val r = Respaldo(exportado = "2026-10-25T13:40:00-06:00", schema_v = SCHEMA_V, listas = listOf(l))
        val vuelta = JSON.decodeFromString(Respaldo.serializer(), JSON.encodeToString(Respaldo.serializer(), r))
        assertEquals(listOf(l), vuelta.listas)
        // Un respaldo de antes del esquema 18 no trae listas.
        assertEquals(emptyList<Lista>(), JSON.decodeFromString(Respaldo.serializer(), """{"exportado":"2026-01-01T00:00:00Z","schema_v":17}""").listas)
    }

    @Test fun filasDeListas() {
        Repo.listasEnMemoria(listOf(Lista("l1", "Cosas por comprar", "2026-10-01T10:00:00-06:00",
            elementos = listOf(ElementoLista("a", "Una lámpara", agregado = "2026-10-01", hecho = "2026-10-11"), ElementoLista("b", "Un cuaderno", agregado = "2026-10-03")))))
        val f = Exportar.filasListas()
        assertEquals(2, f.size)
        assertEquals(10L, f.first { it["id"] == "a" }["dias_hasta_hecho"])
        assertEquals(null, f.first { it["id"] == "b" }["dias_hasta_hecho"])
        assertEquals("Cosas por comprar", f.first()["lista"])
        Repo.listasEnMemoria(emptyList())
    }

    private fun m(id: String, vararg e: String, intens: Map<String, Int> = emptyMap(), pens: List<String> = emptyList(), con: List<String> = emptyList(), otra: String? = null) =
        Momento(id, "2026-10-20T12:00:00-06:00", "2026-10-20", "animo", emociones = e.toList(), intensidades = intens, pensamientos = pens, con_quien = con, emocion_otra = otra)

    @Test fun tituloConIntensidadYConQuien() {
        val x = m("1", "tristeza", "cansancio", intens = mapOf("tristeza" to 3), pens = listOf("vueltas"), con = listOf("solo", "mensaje"))
        assertEquals("Tristeza (mucho) · Cansancio", Catalogo.tituloDetallado(x, false))
        assertEquals("solo, por mensaje o llamada", Catalogo.conQuienTexto(x))
        assertEquals("pensando: dándole vueltas · con solo, por mensaje o llamada", Catalogo.detalleMomento(x, false))
        // En modo discreto: letras, un punto por nivel y sin detalle.
        assertEquals("M··· · V", Catalogo.tituloDetallado(x, true))
        assertEquals("", Catalogo.detalleMomento(x, true))
        // Un registro de antes (sin intensidades ni lista de pensamientos) se ve igual que con [Catalogo.titulo].
        val viejo = Momento("v", "2026-01-01T12:00:00-06:00", "2026-01-01", "tristeza")
        assertEquals(Catalogo.titulo(viejo, false), Catalogo.tituloDetallado(viejo, false))
    }

    @Test fun animoDelPeriodo() {
        val l = listOf(m("1", "ansiedad", intens = mapOf("ansiedad" to 3), pens = listOf("preocupacion"), con = listOf("pareja")),
            m("2", "ansiedad", "otro", otra = "Nervios", con = listOf("solo")), m("3", "no_se"))
        val (emo, pens, con) = Periodo.animoDe(l)
        assertEquals("Ansiedad", emo.first().nombre); assertEquals(2, emo.first().n); assertEquals(1, emo.first().mucho)
        assertTrue(emo.any { it.nombre == "Nervios" })
        assertTrue(emo.none { it.nombre == "No sé" })
        assertEquals(listOf("Preocupación" to 1), pens)
        assertEquals(2, con.second)
        // El reporte para terapia sigue contando «no sé», y anota las veces que fue «mucho».
        val (top, _) = Terapia.emociones(l)
        assertEquals("Ansiedad (1 «mucho»)" to 2, top.first())
        assertTrue(top.any { it.first == "No sé" })
    }
}
