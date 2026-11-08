package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.PlanEnfoque
import io.github.ruddy2003.vestigium.data.Senalada
import io.github.ruddy2003.vestigium.data.Senaladas
import io.github.ruddy2003.vestigium.data.Suplemento
import io.github.ruddy2003.vestigium.data.Suplementos
import io.github.ruddy2003.vestigium.data.Tramo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** El esquema 21: lo de antes se sigue leyendo igual, y los hitos pasan a fechas señaladas. Todo con datos inventados. */
class EsquemaVeintiunoPrueba {
    @Test fun registrosViejos() {
        val m = JSON.decodeFromString(Momento.serializer(), """{"id":"m1","ts":"2026-10-20T09:00:00-06:00","fecha":"2026-10-20","tipo":"recaida","duracion_min":10}""")
        assertTrue(m.practicas.isEmpty()); assertTrue(m.personas.isEmpty()); assertNull(m.estimulo); assertNull(m.vinculo); assertNull(m.nivel)
        val s = JSON.decodeFromString(Suplemento.serializer(), """{"id":"creatina","nombre":"Creatina","unidad":"g","dosis":5.0,"modelo":"carga"}""")
        assertEquals(1, s.tomas_dia)
        val t = JSON.decodeFromString(Tramo.serializer(), """{"id":"enf1_0","fecha":"2026-10-20","tipo":"tesis","inicio":"2026-10-20T09:00:00-06:00","min":30}""")
        assertNull(t.plan_min)
        val p = JSON.decodeFromString(PlanEnfoque.serializer(), """{"tipo":"tesis","meta_min":60,"esperando":true}""")
        assertFalse(p.avisado)
        val c = JSON.decodeFromString(Config.serializer(), """{"suplementos_hora":9}""")
        assertEquals(listOf(9), Suplementos.horas(c))
        assertEquals(listOf(8, 20), Suplementos.horas(c.copy(suplementos_horas = listOf(20, 8))))
        assertTrue(Suplementos.horas(JSON.decodeFromString(Config.serializer(), "{}")).isEmpty())
    }

    @Test fun hitosAFechasSenaladas() {
        val d = JSON.decodeFromString(Dia.serializer(), """{"fecha":"2026-10-20","hitos":["primer_vuelo","Mudanza"]}""")
        assertEquals(listOf(Senalada("otro", "primer vuelo"), Senalada("otro", "Mudanza")), Senaladas.de(d))
        val n = Senaladas.migrar(d)
        assertTrue(n.hitos.isEmpty()); assertEquals(2, n.senaladas.size)
        assertEquals(n.senaladas, Senaladas.de(n))
        // Sin repetir: un hito que ya se pasó no sale dos veces.
        assertEquals(1, Senaladas.de(Dia("2026-10-21", hitos = listOf("viaje_corto"), senaladas = listOf(Senalada("viaje", "Viaje corto")))).size)
        assertEquals("✈ Viaje corto", Senaladas.etiqueta(Senalada("viaje", "Viaje corto")))
        assertEquals("★ Logro", Senaladas.etiqueta(Senalada("logro")))
        assertEquals("• algo", Senaladas.etiqueta(Senalada("no_existe", "algo")))
    }

    @Test fun nivelDeIntimidad() {
        assertEquals(4, Catalogo.nivelIntimidad(listOf("beso_profundo", "coito_vaginal")))
        assertEquals(0, Catalogo.nivelIntimidad(listOf("mensajes_eroticos")))
        assertNull(Catalogo.nivelIntimidad(listOf("juguetes", "otra")))
        assertEquals(2, Catalogo.nivelIntimidad(listOf("juguetes", "caricias_ropa")))
        val ids = Catalogo.PRACTICAS.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test fun tiposNuevos() {
        listOf("masturbacion", "intimidad", "episodio").forEach { assertEquals(it, Catalogo.tipo(it)?.id) }
        val letras = (Catalogo.MOMENTOS + listOf(Catalogo.RECAIDA, Catalogo.LIBIDO, Catalogo.MASTURBACION, Catalogo.INTIMIDAD, Catalogo.EPISODIO)).map { it.discreto }
        assertEquals(letras.size, letras.distinct().size)
        val e = Momento("e1", "2026-10-20T09:00:00-06:00", "2026-10-20", "episodio", episodio = "otro", episodio_otro = "Un susto")
        assertEquals("Un susto", Catalogo.titulo(e, false))
        assertEquals("Crisis de ansiedad o pánico", Catalogo.titulo(e.copy(episodio = "crisis"), false))
    }
}
