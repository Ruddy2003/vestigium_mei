package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Enfoque
import io.github.ruddy2003.vestigium.ui.RotacionTema
import io.github.ruddy2003.vestigium.ui.TemaId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los avisos salen con el tema del día: el sonido de cada canal va por nombre, cada canal nuevo lleva un id que nunca se
 * usó, y el pulso rota el tema antes de avisar. Lo que solo se ve en el teléfono (que el aviso pendiente no vuelva a sonar
 * al mudarlo de canal) está en la lista de qué probar del informe.
 */
class AvisosTemaPrueba {
    @Test fun elSonidoVaPorNombreYElArchivoExiste() {
        TemaId.entries.filterNot { it.sinDiseno }.forEach { t ->
            listOf(Avisos.CANAL_ANIMO, Avisos.CANAL_DIARIO, Avisos.CANAL_ESCALAS, Enfoque.CANAL_AVISO).forEach { base ->
                val nombre = Avisos.nombreSonido(base, t.id)
                assertTrue("${t.id}/$base sin sonido", nombre != null)
                assertTrue("falta res/raw/$nombre.ogg", Pendientes.main("res/raw/$nombre.ogg").exists())
                // El mismo archivo que el `when` de los recursos (lo que suena en la prueba de sonido de Ajustes).
                assertEquals(nombre, nombreDeRecurso(Avisos.sonido(base, t.id)!!))
            }
        }
        // Básico no tiene sonido propio, y los reportes nunca suenan.
        assertNull(Avisos.nombreSonido(Avisos.CANAL_ANIMO, TemaId.BASICO.id))
        assertNull(Avisos.nombreSonido(Avisos.CANAL_REPORTES, TemaId.ROMANO.id))
        assertTrue(Pendientes.main("res/raw/n_lugar.ogg").exists())
    }

    @Test fun cadaCanalNuevoTieneUnIdQueNuncaSeUso() {
        val a = Avisos.idCanalNuevo("animo", "romano", 1_000_000L)
        val b = Avisos.idCanalNuevo("animo", "romano", 1_000_001L)
        assertNotEquals(a, b)
        assertTrue(a.startsWith("animo_romano_"))
    }

    @Test fun elVigenteEsElMasNuevoDeSuTema() {
        val viejo = Avisos.idCanalNuevo("animo", "romano", 1_000L)
        val nuevo = Avisos.idCanalNuevo("animo", "romano", 9_000_000L)
        val ids = listOf("animo_romano", viejo, nuevo, Avisos.idCanalNuevo("animo", "heleno", 5L), "diario_romano_x1", "reportes")
        assertEquals(nuevo, Avisos.canalVigente(ids, "animo", "romano"))
        // El canal de antes («animo_romano», sin serie) no cuenta como vigente: se crea uno nuevo y él se borra.
        assertNull(Avisos.canalVigente(listOf("animo_romano"), "animo", "romano"))
        assertNull(Avisos.canalVigente(ids, "animo", "neon"))
        // «romano» no confunde a un tema cuyo id empiece igual.
        assertNull(Avisos.canalVigente(listOf("animo_romanox_1"), "animo", "romano"))
    }

    @Test fun losQueSobranSonSoloDeSuFamilia() {
        val nuevo = Avisos.idCanalNuevo(Enfoque.CANAL_AVISO, "gotico", 77L)
        val ids = listOf("enfoque", "enfoque_aviso", "enfoque_aviso_romano", Avisos.idCanalNuevo(Enfoque.CANAL_AVISO, "heleno", 3L), nuevo,
            "animo_romano", "reportes", "recordatorios", "lugares_especiales_2")
        val sobran = Avisos.canalesQueSobran(ids, Enfoque.CANAL_AVISO, nuevo)
        assertEquals(setOf("enfoque_aviso", "enfoque_aviso_romano", Avisos.idCanalNuevo(Enfoque.CANAL_AVISO, "heleno", 3L)), sobran.toSet())
        // El canal fijo del cronómetro («enfoque») no es de la familia del aviso de fin de bloque.
        assertFalse("enfoque" in sobran)
        assertEquals(listOf("animo_romano"), Avisos.canalesQueSobran(ids, Avisos.CANAL_ANIMO, "x"))
    }

    @Test fun elPlazoQueLeQuedaAUnAviso() {
        assertNull(Avisos.plazoRestante(0L, 1_000L, 5_000L))
        assertEquals(60 * 60_000L - 10 * 60_000L, Avisos.plazoRestante(60 * 60_000L, 0L, 10 * 60_000L))
        assertTrue(Avisos.plazoRestante(60_000L, 0L, 61_000L)!! <= 0L)
    }

    @Test fun laRotacionNoEscribeNiRepite() {
        val c = Config(tema = "romano", tema_rotar = true, tema_rotado = "2027-03-01")
        // Ya rotó hoy: nada. Con la rotación apagada: nada.
        assertNull(RotacionTema.tocaRotar(c, "2027-03-01"))
        assertNull(RotacionTema.tocaRotar(c.copy(tema_rotar = false), "2027-03-02"))
        // Toca: siempre el mismo para ese día y nunca el de ayer.
        val t = RotacionTema.tocaRotar(c, "2027-03-02")!!
        assertEquals(t, RotacionTema.tocaRotar(c, "2027-03-02"))
        assertNotEquals("romano", t.id)
        assertFalse(t.sinDiseno)
    }

    @Test fun elPulsoRotaElTemaAntesDeAvisar() {
        val s = Pendientes.main("java/io/github/ruddy2003/vestigium/avisos/Trabajo.kt").readText()
        val pulso = s.substring(s.indexOf("override suspend fun doWork()"))
        val rota = pulso.indexOf("RotacionTema.revisar()")
        assertTrue("el pulso no rota el tema", rota >= 0)
        listOf("avisos(ctx)", "diario(ctx)", "Avisos.lugar(", "Enfoque.avanzar(ctx)").forEach {
            val i = pulso.indexOf(it)
            assertTrue("no encontré $it", i >= 0)
            assertTrue("$it va antes de rotar el tema", rota < i)
        }
    }

    @Test fun cadaAlarmaQueAvisaRevisaElTemaPrimero() {
        // Los receptores que publican avisos: revisan el tema antes de hacer nada más.
        listOf("avisos/Muestreo.kt" to "class MuestreoReceiver", "data/Enfoque.kt" to "class EnfoqueReceiver",
            "avisos/Recordatorios.kt" to "class RecordatorioReceiver").forEach { (archivo, clase) ->
            val s = Pendientes.main("java/io/github/ruddy2003/vestigium/$archivo").readText()
            val cuerpo = s.substring(s.indexOf(clase)).let { it.substring(it.indexOf("onReceive")) }.take(400)
            assertTrue("$clase no revisa el tema", "RotacionTema.revisarDesde(" in cuerpo)
        }
    }

    /** El nombre del recurso de sonido, sacado de R.raw por reflexión (las pruebas no tienen `Resources`). */
    private fun nombreDeRecurso(id: Int): String =
        io.github.ruddy2003.vestigium.R.raw::class.java.fields.first { it.getInt(null) == id }.name
}
