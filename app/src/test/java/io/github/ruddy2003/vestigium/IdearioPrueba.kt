package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Idea
import io.github.ruddy2003.vestigium.data.Ideario
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Nota
import io.github.ruddy2003.vestigium.data.Obra
import io.github.ruddy2003.vestigium.data.Respaldo
import io.github.ruddy2003.vestigium.data.RevisionIdea
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** El Ideario (esquema 16): estanterías, voces, «Una idea de antes», lo compartido y los respaldos. Datos inventados. */
class IdearioPrueba {
    private fun idea(id: String, fecha: String, texto: String = "algo", revisiones: List<RevisionIdea> = emptyList(), quien: List<String> = emptyList()) =
        Idea(id, "${fecha}T10:00:00-06:00", fecha, texto, quien = quien, revisiones = revisiones)

    @Test fun estanteriasSoloConTemasDeIdeas() {
        val l = listOf(idea("a", "2026-01-01", "uno #fisica #filosofia"), idea("b", "2026-01-02", "dos #fisica #fisica"), idea("c", "2026-01-03", "sin tema"))
        assertEquals(listOf("fisica" to 2, "filosofia" to 1), Ideario.temas(l))
        assertEquals(listOf("b", "a"), Ideario.deTema(l, "fisica").map { it.id })
        assertEquals(listOf("c"), Ideario.deTema(l, null).map { it.id })
    }

    @Test fun vocesSinRepetir() {
        val l = listOf(idea("a", "2026-01-01", quien = listOf("Autora Uno")), idea("b", "2026-01-02", quien = listOf("Autora Uno", "Invitado Dos")))
        val obras = listOf(Obra("o", "libro", "Un libro", autor = "autora uno, Tercer Autor"))
        assertEquals(listOf("Autora Uno", "Invitado Dos", "Tercer Autor"), Ideario.voces(l, obras))
    }

    @Test fun resurgirPrefiereAniversariosYDescansa() {
        val hoy = "2026-11-02"
        val joven = idea("j", "2026-10-20")                  // 13 días: todavía no
        val mes = idea("m", "2026-10-02")                    // 31 días: aniversario de un mes
        val suelta = idea("s", "2026-08-15")                 // 79 días: sin aniversario
        assertEquals("m", Ideario.resurgir(listOf(joven, mes, suelta), hoy)?.id)
        // Si no hay aniversario, cualquiera de las de más de 30 días; y siempre la misma para el mismo día.
        val sinAniv = listOf(joven, suelta, idea("t", "2026-07-01"))
        val r = Ideario.resurgir(sinAniv, hoy)
        assertNotNull(r); assertEquals(r, Ideario.resurgir(sinAniv, hoy))
        // Respondida hace menos de 30 días: descansa.
        val respondida = mes.copy(revisiones = listOf(RevisionIdea("2026-10-20", "sigue")))
        assertNull(Ideario.resurgir(listOf(joven, respondida), hoy))
        assertNull(Ideario.resurgir(listOf(joven), hoy))
        assertEquals("m", Ideario.respondidaHoy(listOf(mes.copy(revisiones = listOf(RevisionIdea(hoy, "cambio")))), hoy)?.id)
    }

    @Test fun haceCuantoEnPalabras() {
        assertEquals("hace un mes", Ideario.haceCuanto("2026-10-02", "2026-11-02"))
        assertEquals("hace un año", Ideario.haceCuanto("2025-11-01", "2026-11-02"))
        assertEquals("hace 2 años", Ideario.haceCuanto("2024-11-02", "2026-11-02"))
        assertEquals("hace 79 días", Ideario.haceCuanto("2026-08-15", "2026-11-02"))
    }

    @Test fun loCompartido() {
        val v = Ideario.deCompartido("https://www.youtube.com/watch?v=abc123", "Un video de ejemplo", "2026-11-02T10:00:00-06:00", "2026-11-02", "x")
        assertEquals("https://www.youtube.com/watch?v=abc123", v.enlace); assertEquals("Un video de ejemplo", v.fuente); assertEquals("visto", v.origen); assertEquals("", v.texto)
        val largo = "«" + "palabra ".repeat(30).trim() + "» de Un Libro https://a.co/d/xyz"
        val k = Ideario.deCompartido(largo, null, "2026-11-02T10:00:00-06:00", "2026-11-02", "y")
        assertEquals("leido", k.origen); assertEquals("https://a.co/d/xyz", k.enlace); assertTrue(k.texto.startsWith("«palabra")); assertEquals("", k.fuente)
        val sitio = Ideario.deCompartido("Un artículo https://ejemplo.org/nota.", null, "t", "2026-11-02", "z")
        assertEquals("https://ejemplo.org/nota", sitio.enlace); assertEquals("Un artículo", sitio.fuente); assertEquals("", sitio.origen)
    }

    @Test fun notaAlIdearioConservaSuHora() {
        val n = Nota("n", "2026-05-01T22:10:00-06:00", "2026-05-01", "un pensamiento #tiempo", tipo = "pensamiento", fijada = true)
        val i = Ideario.deNota(n, "i")
        assertEquals(n.creada, i.creada); assertEquals(n.fecha, i.fecha); assertEquals(n.texto, i.texto); assertEquals("propia", i.origen); assertTrue(i.fijada)
    }

    @Test fun respaldoConYSinIdeas() {
        val r = Respaldo("2026-11-02T10:00:00-06:00", 16, ideas = listOf(idea("a", "2026-11-01", "#x", listOf(RevisionIdea("2026-11-02", "sigue")))))
        val vuelta = JSON.decodeFromString(Respaldo.serializer(), JSON.encodeToString(Respaldo.serializer(), r))
        assertEquals(r.ideas, vuelta.ideas)
        // Un respaldo de antes del esquema 16 no trae ideas: se lee igual.
        assertEquals(emptyList<Idea>(), JSON.decodeFromString(Respaldo.serializer(), """{"exportado":"2026-01-01T00:00:00Z","schema_v":15}""").ideas)
    }
}
