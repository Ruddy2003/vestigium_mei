package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Dinero
import io.github.ruddy2003.vestigium.data.Idea
import io.github.ruddy2003.vestigium.data.Ideario
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Momento
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Las pantallas de captura del esquema 17: lo que cambia en los datos y cómo se leen los registros de antes. Datos inventados. */
class CapturaManualPrueba {
    @Test fun montoConComa() {
        assertEquals("12.50", Dinero.limpiarMonto("12,50"))
        assertEquals("12.5", Dinero.limpiarMonto("12.5"))
        // Un segundo separador se ignora (antes «1.2.3» pasaba el filtro y fallaba al guardar).
        assertEquals("1.23", Dinero.limpiarMonto("1.2.3"))
        assertEquals("1234", Dinero.limpiarMonto("$ 1 234"))
    }

    @Test fun suenoDeAntesSeTraduce() {
        val d = Dia("2025-05-01", sueno_recordado = true, sueno_tipo = "recurrente", sueno_categorias = listOf("per_familia"))
        assertEquals(listOf("per_familia", "est_recurrente"), Catalogo.categoriasSuenoDe(d))
        assertNull(Catalogo.rarezaDe(d)); assertNull(Catalogo.pesadillaDe(d))
        assertEquals("realista", Catalogo.rarezaDe(Dia("2025-05-02", sueno_tipo = "mundano")))
        assertEquals("imposible", Catalogo.rarezaDe(Dia("2025-05-03", sueno_tipo = "bizarro_surreal")))
        assertEquals(true, Catalogo.pesadillaDe(Dia("2025-05-04", sueno_tipo = "ansioso_pesadilla")))
        // Lo que se marca con el esquema 17 manda sobre el tipo de antes.
        assertEquals(false, Catalogo.pesadillaDe(Dia("2025-05-05", sueno_tipo = "ansioso_pesadilla", sueno_pesadilla = false)))
    }

    @Test fun diaViejoSinCamposNuevos() {
        val d = JSON.decodeFromString(Dia.serializer(), """{"fecha":"2025-05-01","sueno_tipo":"mundano","habitos":{"leer":true}}""")
        assertNull(d.sueno_rareza); assertNull(d.sueno_pesadilla); assertFalse(d.habitos_tri); assertEquals("", d.ctx_otro)
    }

    @Test fun pensamientosDeAntesYDeAhora() {
        val viejo = Momento("m1", "2025-05-01T10:00:00-06:00", "2025-05-01", "animo", pensamiento = "plan")
        assertEquals(listOf("plan"), Catalogo.pensamientosDe(viejo))
        val tipoViejo = Momento("m2", "2025-05-01T11:00:00-06:00", "2025-05-01", "recuerdo")
        assertEquals(listOf("recuerdo"), Catalogo.pensamientosDe(tipoViejo))
        val nuevo = viejo.copy(pensamientos = listOf("plan", "vueltas"))
        assertEquals(listOf("plan", "vueltas"), Catalogo.pensamientosDe(nuevo))
    }

    @Test fun conQuienSoloYMensaje() {
        assertEquals(listOf("solo"), Catalogo.alternarConQuien(listOf("amigos"), "solo"))
        assertEquals(listOf("solo", "mensaje"), Catalogo.alternarConQuien(listOf("solo"), "mensaje"))
        assertEquals(listOf("mensaje", "amigos"), Catalogo.alternarConQuien(listOf("solo", "mensaje"), "amigos"))
    }

    @Test fun temasDeIdeaCampoYTexto() {
        val i = Idea("a", "2026-01-01T10:00:00-06:00", "2026-01-01", "algo #fisica", temas = listOf("Filosofía", "fisica"))
        assertEquals(listOf("filosofía", "fisica"), Ideario.temasDe(i))
        val sinTema = Idea("b", "2026-01-02T10:00:00-06:00", "2026-01-02", "nada")
        assertEquals(listOf("fisica", "filosofía"), Ideario.temas(listOf(i, sinTema)).map { it.first }.sortedDescending())
        assertEquals(listOf("b"), Ideario.deTema(listOf(i, sinTema), null).map { it.id })
    }

    @Test fun emocionesNuevasSinChoqueDeIds() {
        val ids = Catalogo.SENTIMIENTOS.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
        assertEquals(48, ids.size)
        val letras = Catalogo.SENTIMIENTOS.map { it.discreto }
        assertEquals(letras.size, letras.distinct().size)
        assertTrue(Catalogo.SUENO_EJES.flatMap { e -> e.opciones.map { it.id } }.let { it.size == it.distinct().size })
    }
}
