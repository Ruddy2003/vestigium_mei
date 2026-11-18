package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.CuadroGuardado
import io.github.ruddy2003.vestigium.data.CuentoLeido
import io.github.ruddy2003.vestigium.data.EspecialLeido
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.SCHEMA_V
import io.github.ruddy2003.vestigium.data.Seguido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El esquema 27 (17 nov 2026): los cuadros y las historias de «Otras vidas» en `Config`. Una configuración guardada antes se
 * sigue leyendo igual, con los campos nuevos vacíos; y lo nuevo va y vuelve sin perder nada. Todo con datos inventados.
 */
class EsquemaVeintisietePrueba {
    // El esquema que trae esto es el 27; el número vigente lo comprueba EsquemaCatorcePrueba (desde el 28, «llamada»).
    @Test fun esquema() = assertTrue(SCHEMA_V >= 27)

    @Test fun configDeAntesSeLeeConLoNuevoVacio() {
        // Una configuración del esquema 26: trae lo suyo y nada de lo nuevo.
        val vieja = JSON.decodeFromString(Config.serializer(), """{"tema":"heleno","modo":"hora","gato_estilo":"pixel","frases_guardadas":[]}""")
        assertTrue(vieja.seguidos.isEmpty())
        assertTrue(vieja.avisos_historia)
        assertTrue(vieja.historia_avisada.isEmpty())
        assertTrue(vieja.cuentos_leidos.isEmpty())
        assertTrue(vieja.uso_dias.isEmpty())
        assertTrue(vieja.cuadros_guardados.isEmpty())
        assertTrue(vieja.ambiente_cuadro)
        // Lo que ya tenía no cambia.
        assertEquals("heleno", vieja.tema); assertEquals("hora", vieja.modo); assertEquals("pixel", vieja.gato_estilo)
        // Y una vacía, igual.
        val vacia = JSON.decodeFromString(Config.serializer(), "{}")
        assertEquals(Config(), vacia)
    }

    @Test fun registrosNuevosSinLoOpcional() {
        // Lo mínimo de cada clase nueva: lo opcional toma su valor por omisión.
        val s = JSON.decodeFromString(Seguido.serializer(), """{"vida":"remero","desde":"2027-01-03"}""")
        assertNull(s.hasta); assertTrue(s.activo)
        val l = JSON.decodeFromString(CuentoLeido.serializer(), """{"vida":"remero","dia":"2027-01-03","leido":"2027-01-03"}""")
        assertNull(l.ts); assertTrue(l.especiales.isEmpty())
        val g = JSON.decodeFromString(CuadroGuardado.serializer(),
            """{"id":"abc123","tema":"heleno","vida":"remero","dia":"2027-01-03","hora":13.5,"guardado":"2027-01-03T13:31:00-06:00","archivo":"abc123.jpg"}""")
        assertTrue(g.claves.isEmpty())
    }

    @Test fun idaYVuelta() {
        val c = Config(
            seguidos = listOf(Seguido("remero", "2027-01-03"), Seguido("cantero", "2027-01-04", "2027-02-01")),
            avisos_historia = false,
            historia_avisada = mapOf("remero|fiesta_x|2027-01-10" to "2027-01-10T09:00:00-06:00"),
            cuentos_leidos = listOf(CuentoLeido("remero", "2027-01-10", "2027-01-12", "2027-01-12T22:00:00-06:00", listOf(EspecialLeido("fiesta_x", "2027-01-10")))),
            uso_dias = mapOf("historia_hoy" to listOf("2027-01-03", "2027-01-05")),
            cuadros_guardados = listOf(CuadroGuardado("abc123", "heleno", "remero", "2027-01-03", 13.5, "2027-01-03T13:31:00-06:00", "abc123.jpg", listOf("fiesta"))),
            ambiente_cuadro = false,
        )
        val vuelta = JSON.decodeFromString(Config.serializer(), JSON.encodeToString(Config.serializer(), c))
        assertEquals(c, vuelta)
        // «activo» se calcula: no se guarda.
        assertFalse(JSON.encodeToString(Seguido.serializer(), Seguido("remero", "2027-01-03")).contains("activo"))
    }
}
