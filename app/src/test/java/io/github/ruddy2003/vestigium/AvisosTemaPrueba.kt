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
            Avisos.FAMILIAS.forEach { base ->
                val nombre = Avisos.nombreSonido(base, t.id)
                assertTrue("${t.id}/$base sin sonido", nombre != null)
                assertTrue("falta res/raw/$nombre.ogg", Pendientes.main("res/raw/$nombre.ogg").exists())
                // El mismo archivo que el `when` de los recursos (lo que suena en la prueba de sonido de Ajustes).
                assertEquals(nombre, nombreDeRecurso(Avisos.sonido(base, t.id)!!))
            }
        }
        // Básico no tiene sonido propio (suena el del teléfono), y el cronómetro fijo del enfoque no tiene canal por tema.
        assertNull(Avisos.nombreSonido(Avisos.CANAL_ANIMO, TemaId.BASICO.id))
        assertNull(Avisos.nombreSonido(Enfoque.CANAL, TemaId.ROMANO.id))
    }

    @Test fun cadaFamiliaLlevaElSonidoQueLeToca() {
        // Lo breve y frecuente, el corto; lo ceremonioso o de fin de día, el solemne.
        listOf(Avisos.CANAL_ANIMO, Enfoque.CANAL_AVISO, Avisos.CANAL_RECORDATORIOS, Avisos.CANAL_LUGARES, Avisos.CANAL_RESPALDOS)
            .forEach { assertEquals(it, "n_romano_aviso", Avisos.nombreSonido(it, "romano")) }
        listOf(Avisos.CANAL_DIARIO, Avisos.CANAL_ESCALAS, Avisos.CANAL_REPORTES)
            .forEach { assertEquals(it, "n_romano_solemne", Avisos.nombreSonido(it, "romano")) }
        // Ninguna familia nace con importancia baja (en Android, baja es sin sonido): «reportes» nacía así.
        Avisos.FAMILIAS.forEach { assertTrue(it, Avisos.importancia(it) >= android.app.NotificationManager.IMPORTANCE_DEFAULT) }
        // Las familias no se pisan: ninguna es el principio de otra («enfoque» no es familia; «enfoque_aviso» sí).
        Avisos.FAMILIAS.forEach { a -> Avisos.FAMILIAS.filter { it != a }.forEach { b -> assertFalse("$a/$b", b.startsWith(a + "_")) } }
    }

    @Test fun ningunAvisoSaleDeUnCanalFijo() {
        // El origen del aviso mudo: avisos que salían por un canal fijo (sin el sonido del tema o con importancia baja).
        // Todos los de Avisos pasan por `canal()`; el único fijo es el cronómetro de enfoque, en silencio a propósito.
        val s = Pendientes.main("java/io/github/ruddy2003/vestigium/avisos/Avisos.kt").readText()
        val usos = Regex("""NotificationCompat\.Builder\(ctx, ([^\n]{0,40})""").findAll(s).map { it.groupValues[1] }.toList()
        assertTrue(usos.size >= 20)
        usos.forEach { assertTrue("aviso por un canal fijo: $it", it.startsWith("canal(ctx, ")) }
        val e = Pendientes.main("java/io/github/ruddy2003/vestigium/data/Enfoque.kt").readText()
        assertEquals(1, Regex("""NotificationCompat\.Builder\(ctx, CANAL\)""").findAll(e).count())
        assertTrue("el aviso de fin de bloque no pasa por el canal por tema", "Avisos.canal(ctx, CANAL_AVISO)" in e)
    }

    @Test fun elPrevioEsElMasReciente() {
        val viejo = Avisos.idCanalNuevo("animo", "romano", 1_000_000L)
        val medio = Avisos.idCanalNuevo("animo", "heleno", 5_000_000L)
        val nuevo = Avisos.idCanalNuevo("animo", "sistema", 9_000_000L)
        // En cualquier orden (Android no los da ordenados), y los de antes de la serie al final.
        listOf(listOf(nuevo, viejo, medio, "animo", "animo_romano"), listOf("animo_romano", medio, viejo, nuevo, "animo")).forEach {
            assertEquals(nuevo, Avisos.masReciente(it, "animo"))
        }
        assertEquals("animo_romano", Avisos.masReciente(listOf("animo", "animo_romano"), "animo"))
        // Los de lugares de antes: «especiales» no es un tema y ese «2» no es una serie.
        assertNull(Avisos.serialDe("lugares_especiales_2", Avisos.CANAL_LUGARES))
        assertNull(Avisos.serialDe("lugares_especiales", Avisos.CANAL_LUGARES))
        assertNull(Avisos.serialDe("recordatorios", Avisos.CANAL_RECORDATORIOS))
        assertEquals(1_000_000L, Avisos.serialDe(viejo, "animo"))
        assertEquals("lugares_especiales_2", Avisos.masReciente(listOf("lugares_especiales", "lugares_especiales_2"), Avisos.CANAL_LUGARES))
        val lug = Avisos.idCanalNuevo(Avisos.CANAL_LUGARES, "galia", 3L)
        assertEquals(lug, Avisos.masReciente(listOf("lugares_especiales_2", lug, "lugares_especiales"), Avisos.CANAL_LUGARES))
        assertNull(Avisos.masReciente(emptyList(), "animo"))
    }

    @Test fun losCanalesFijosDeAntesSeMudanASuFamilia() {
        val ids = listOf("reportes", "recordatorios", "lugares_especiales", "lugares_especiales_2", "enfoque", "animo_romano_x")
        assertEquals(listOf("reportes"), Avisos.canalesQueSobran(ids, Avisos.CANAL_REPORTES, ""))
        assertEquals(listOf("recordatorios"), Avisos.canalesQueSobran(ids, Avisos.CANAL_RECORDATORIOS, ""))
        assertEquals(setOf("lugares_especiales", "lugares_especiales_2"), Avisos.canalesQueSobran(ids, Avisos.CANAL_LUGARES, "").toSet())
        assertTrue(Avisos.canalesQueSobran(ids, Avisos.CANAL_RESPALDOS, "").isEmpty())
    }

    @Test fun elSilencioSoloSeHeredaSiEsTuyo() {
        // Lo silenciaste tú en ese canal: sigue en silencio y se recuerda.
        assertTrue(Avisos.naceEnSilencio(sinSonido = true, loElegisteTu = true, recordado = false))
        assertEquals(true, Avisos.recordarSilencio(true, true))
        // Lo silenciaste en uno anterior (este nació en silencio por herencia): sigue.
        assertTrue(Avisos.naceEnSilencio(true, false, recordado = true))
        // Mudo sin que lo pidieras (la herencia de un error): vuelve el sonido del tema, y no se recuerda nada.
        assertFalse(Avisos.naceEnSilencio(true, false, recordado = false))
        assertNull(Avisos.recordarSilencio(true, false))
        // Suena: el siguiente también, aunque antes lo hubieras silenciado (le devolviste un sonido), y se olvida el silencio.
        assertFalse(Avisos.naceEnSilencio(false, true, recordado = true))
        assertFalse(Avisos.naceEnSilencio(false, null, recordado = true))
        assertEquals(false, Avisos.recordarSilencio(false, true))
        assertEquals(false, Avisos.recordarSilencio(false, false))
        // Android 11 no dice quién lo silenció: se respeta.
        assertTrue(Avisos.naceEnSilencio(true, null, recordado = false))
    }

    @Test fun laImportanciaDeUnCanalFijoSoloPasaSiLaCambiasteTu() {
        val baja = android.app.NotificationManager.IMPORTANCE_LOW
        val normal = android.app.NotificationManager.IMPORTANCE_DEFAULT
        // «reportes» nació con importancia baja (sin sonido): el canal por tema nace con la normal.
        assertEquals(normal, Avisos.importanciaNueva(baja, deLaSerie = false, laCambiasteTu = false, deFabrica = normal))
        // Si la bajaste tú, se queda baja.
        assertEquals(baja, Avisos.importanciaNueva(baja, deLaSerie = false, laCambiasteTu = true, deFabrica = normal))
        // Dentro de la serie se hereda, como antes (todas nacen con la de fábrica: lo distinto lo decidiste tú).
        assertEquals(baja, Avisos.importanciaNueva(baja, deLaSerie = true, laCambiasteTu = false, deFabrica = normal))
        assertEquals(normal, Avisos.importanciaNueva(null, false, false, normal))
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
