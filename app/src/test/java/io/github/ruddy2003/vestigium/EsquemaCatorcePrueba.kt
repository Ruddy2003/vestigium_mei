package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Borrado
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.SCHEMA_V
import io.github.ruddy2003.vestigium.data.SaludDia
import io.github.ruddy2003.vestigium.data.Subcategoria
import io.github.ruddy2003.vestigium.data.TomaSuplemento
import io.github.ruddy2003.vestigium.data.VERSION_NOCHE
import io.github.ruddy2003.vestigium.data.cierrePermitido
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Lo que el esquema 14 empezó a guardar (revisión de navegación): los registros viejos se siguen leyendo igual. */
class EsquemaCatorcePrueba {
    // El esquema 15 (10f) agregó el historial de zonas, la fuente del clima, el wifi sin dato, el criterio sellado y la fuerza de referencia;
    // el 16, el Ideario (ver IdearioPrueba); el 17, la rareza y la pesadilla del sueño, los temas de las ideas y más campos de captura;
    // el 18, los deseados (prioridad, quién lo recomendó y dónde) y las listas libres (ver DeseadosPrueba); el 19, cómo se
    // supo la salida de cada visita (ver VisitasPrueba); el 20, la hora real en que salió cada aviso de ánimo (AvisosDia.reales);
    // el 21, masturbación e intimidad, episodios, fechas señaladas, varias tomas y la extra del enfoque (ver EsquemaVeintiunoPrueba);
    // el 22, el respaldo blindado (estado del respaldo en Config, fecha en Borrado) y la copia de la foto del día (Dia.foto_archivo).
    @Test fun esquema() = assertEquals(22, SCHEMA_V)

    @Test fun esquema22SeLeeSinLoNuevo() {
        // Un día y unos ajustes de antes del esquema 22: la foto sin copia y el respaldo sin estado.
        val d = JSON.decodeFromString(io.github.ruddy2003.vestigium.data.Dia.serializer(), """{"fecha":"2026-10-01","foto":"content://x/1"}""")
        assertEquals(null, d.foto_archivo)
        val c = JSON.decodeFromString(io.github.ruddy2003.vestigium.data.Config.serializer(), """{"respaldo_ultimo":"2026-10-01T06:00:00-06:00"}""")
        assertEquals(null, c.respaldo_verificado); assertEquals(true, c.bd_revision_ok); assertEquals(emptyMap<String, Int>(), c.respaldo_conteo)
        val b = JSON.decodeFromString(io.github.ruddy2003.vestigium.data.Borrado.serializer(), """{"col":"gastos","id":"g1","json":"{}","borrado":"2026-10-01T06:00:00-06:00"}""")
        assertEquals(null, b.fecha)
    }

    @Test fun avisosSinHoraReal() {
        // Un día de avisos de antes del esquema 20 no trae la hora real: queda vacía (y su latencia_min, sin dato).
        val a = JSON.decodeFromString(io.github.ruddy2003.vestigium.data.AvisosDia.serializer(), """{"fecha":"2026-03-02","planeados":4,"vencidos":["2026-03-02T11:23:00-06:00"],"mostrados":["2026-03-02T11:23:00-06:00"]}""")
        assertTrue(a.reales.isEmpty())
    }

    @Test fun registrosViejosSinMarca() {
        // Un momento, un gasto, una toma y un día guardados antes del esquema 14 no traen los campos nuevos.
        val m = JSON.decodeFromString(Momento.serializer(), """{"id":"m1","ts":"2026-10-20T09:00:00-06:00","fecha":"2026-10-20","tipo":"animo","valencia":5}""")
        assertNull(m.ts_creado); assertNull(m.ts_editado)
        val g = JSON.decodeFromString(Gasto.serializer(), """{"id":"g1","fecha":"2026-10-20","hora":"08:15","monto":55.0,"categoria":"Comida"}""")
        assertEquals("", g.subcategoria); assertNull(g.ts_creado)
        val t = JSON.decodeFromString(TomaSuplemento.serializer(), """{"id":"t1","suplemento":"creatina","ts":"2026-10-20T08:30:00-06:00","fecha":"2026-10-20","cantidad":5.0}""")
        assertNull(t.ts_creado)
        val d = JSON.decodeFromString(Dia.serializer(), """{"fecha":"2026-10-20","valencia":4}""")
        assertNull(d.version_noche)
        val s = JSON.decodeFromString(SaludDia.serializer(), """{"fecha":"2026-10-20","sueno_min":420}""")
        assertTrue(s.sueno_descartadas.isEmpty())
        assertTrue(JSON.decodeFromString(Config.serializer(), "{}").subcategorias.isEmpty())
    }

    @Test fun idaYVuelta() {
        val g = Gasto("g2", "2026-10-21", "08:15", 55.0, "Comida", subcategoria = "Café", ts_creado = "2026-10-21T08:16:00-06:00")
        assertEquals(g, JSON.decodeFromString(Gasto.serializer(), JSON.encodeToString(Gasto.serializer(), g)))
        val c = Config(subcategorias = mapOf("Comida" to listOf(Subcategoria("Café", 55.0), Subcategoria("Súper"))))
        assertEquals(c.subcategorias, JSON.decodeFromString(Config.serializer(), JSON.encodeToString(Config.serializer(), c)).subcategorias)
        val b = Borrado("gastos", "g2", JSON.encodeToString(Gasto.serializer(), g), "2026-10-22T10:00:00-06:00")
        val b2 = JSON.decodeFromString(Borrado.serializer(), JSON.encodeToString(Borrado.serializer(), b))
        assertEquals(g, JSON.decodeFromString(Gasto.serializer(), b2.json))
    }

    @Test fun nocheConElOrdenNuevo() = assertEquals(2, VERSION_NOCHE)

    @Test fun cierreSoloHoy() {
        // El «cómo estás ahora» de la noche solo va en la hoja de hoy; en un día pasado no se ofrece (H-2-8).
        assertTrue(cierrePermitido("2026-10-27", hoy = "2026-10-27"))
        assertFalse(cierrePermitido("2026-10-26", hoy = "2026-10-27"))
        assertFalse(cierrePermitido("2026-10-28", hoy = "2026-10-27"))
    }
}
