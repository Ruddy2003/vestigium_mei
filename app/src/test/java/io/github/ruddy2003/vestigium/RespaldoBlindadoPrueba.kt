package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Respaldo
import io.github.ruddy2003.vestigium.data.Respaldos
import io.github.ruddy2003.vestigium.data.SCHEMA_V
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/** El respaldo blindado (9 nov 2026): copias con fecha, semáforo y la revisión contra el manifiesto. */
class RespaldoBlindadoPrueba {
    @Test fun rotacionConservaLasMasRecientes() {
        val dias = (1..10).map { "vestigium_dia_2026-11-%02d.vmc".format(it) } + listOf("vestigium_respaldo.vmc", "vestigium_semana_2026-11-02.vmc")
        val sobran = Respaldos.sobran(dias, Regex("vestigium_dia_\\d{4}-\\d{2}-\\d{2}\\.vmc"), 7)
        assertEquals(listOf("vestigium_dia_2026-11-03.vmc", "vestigium_dia_2026-11-02.vmc", "vestigium_dia_2026-11-01.vmc"), sobran)
        assertTrue(Respaldos.sobran(listOf("vestigium_mes_2026-10.zip"), Regex("vestigium_mes_\\d{4}-\\d{2}\\.zip"), 12).isEmpty())
    }

    @Test fun semaforo() {
        val ahora = Fechas.millis("2026-11-09T12:00:00-06:00")
        assertEquals(Respaldos.Luz.VERDE, Respaldos.luz("2026-11-09T06:10:00-06:00", ahora))
        assertEquals(Respaldos.Luz.AMARILLO, Respaldos.luz("2026-11-07T20:00:00-06:00", ahora))
        assertEquals(Respaldos.Luz.ROJO, Respaldos.luz("2026-11-06T06:00:00-06:00", ahora))
        assertEquals(Respaldos.Luz.ROJO, Respaldos.luz(null, ahora))
    }

    private fun respaldo() = Respaldo("2026-11-09T06:00:00-06:00", SCHEMA_V,
        dias = (1..25).map { Dia("2026-10-%02d".format(it), valencia = 4, nota = "día inventado $it") },
        momentos = (1..5).map { Momento("m$it", "2026-10-0${it}T10:00:00-06:00", "2026-10-0$it", "animo", valencia = 5) })

    /** Un zip como el de la app: entradas, y al final el manifiesto con sus huellas. */
    private fun zip(f: File, r: Respaldo, alterar: Boolean = false, sinManifiesto: Boolean = false) {
        val json = JSON.encodeToString(Respaldo.serializer(), r).toByteArray()
        val csv = "ts,lat,lon\n2026-10-01T10:00:00-06:00,22.1,-100.9\n".toByteArray()
        val huellas = mapOf("vm_respaldo.json" to json, "vm_ubicaciones.csv" to csv).mapValues { (_, b) ->
            Respaldos.EntradaManifiesto(Respaldos.huella(b.inputStream()).first, b.size.toLong())
        }
        val m = Respaldos.Manifiesto("2026-11-09T06:00:00-06:00", SCHEMA_V, "prueba", Respaldos.conteoDe(r), huellas)
        ZipOutputStream(f.outputStream()).use { z ->
            z.putNextEntry(ZipEntry("vm_respaldo.json")); z.write(json); z.closeEntry()
            z.putNextEntry(ZipEntry("vm_ubicaciones.csv")); z.write(if (alterar) csv.copyOf().also { it[5] = 'X'.code.toByte() } else csv); z.closeEntry()
            if (!sinManifiesto) { z.putNextEntry(ZipEntry(Respaldos.MANIFIESTO)); z.write(JSON.encodeToString(Respaldos.Manifiesto.serializer(), m).toByteArray()); z.closeEntry() }
        }
    }

    @Test fun manifiestoCoincide() {
        val f = File.createTempFile("resp", ".zip")
        try { zip(f, respaldo()); assertNull(Respaldos.verificarZip(f)) } finally { f.delete() }
    }

    @Test fun unByteDistintoSeNota() {
        val f = File.createTempFile("resp", ".zip")
        try {
            zip(f, respaldo(), alterar = true)
            val p = Respaldos.verificarZip(f)
            assertNotNull(p); assertTrue(p!!, p.contains("vm_ubicaciones.csv"))
        } finally { f.delete() }
    }

    @Test fun sinManifiesto() {
        val f = File.createTempFile("resp", ".zip")
        try {
            zip(f, respaldo(), sinManifiesto = true)
            assertEquals("no trae manifiesto", Respaldos.verificarZip(f))
            assertNull(Respaldos.verificarZip(f, estricto = false))
        } finally { f.delete() }
    }

    @Test fun empobrecidoNoReemplaza() {
        assertTrue(Respaldos.empobrecido(mapOf("dias" to 300, "momentos" to 900), mapOf("dias" to 100, "momentos" to 900)))
        assertTrue(!Respaldos.empobrecido(mapOf("dias" to 300, "momentos" to 900), mapOf("dias" to 301, "momentos" to 905)))
    }
}
