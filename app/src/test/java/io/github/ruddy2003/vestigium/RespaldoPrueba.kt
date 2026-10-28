package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Carta
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Consumo
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.Habito
import io.github.ruddy2003.vestigium.data.Importar
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Lectura
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Persona
import io.github.ruddy2003.vestigium.data.Respaldo
import io.github.ruddy2003.vestigium.data.Respaldos
import io.github.ruddy2003.vestigium.data.SCHEMA_V
import io.github.ruddy2003.vestigium.data.Tramo
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * El respaldo de ida y vuelta, sin teléfono: se arma un respaldo con datos de todo tipo, se mete en un .zip,
 * se cifra con contraseña, se descifra y se vuelve a leer. Todo debe quedar exactamente igual.
 */
class RespaldoPrueba {
    private fun ejemplo(): Respaldo {
        val dias = (1..40).map { i ->
            val f = "2026-09-%02d".format(((i - 1) % 30) + 1).let { if (i > 30) "2026-10-%02d".format(i - 30) else it }
            Dia(fecha = f, valencia = 1 + i % 7, activacion = 1 + (i * 3) % 7, nota = "Día $i con @ana y #tesis, ñandú y acentos: canción, café.",
                sueno_texto = if (i % 3 == 0) "Soñé con @luis en una ciudad imposible" else "", sueno_recordado = i % 3 == 0,
                hitos = if (i == 7) listOf("primer_vuelo") else emptyList(), habitos = mapOf("leer" to (i % 2 == 0)),
                presencias = if (i % 5 == 0) mapOf("ana" to "pense") else emptyMap(), presencias_ok = i % 5 == 0)
        }
        val momentos = (1..25).map { i ->
            Momento(id = "m$i", ts = "2026-10-0${1 + i % 9}T1${i % 10}:0${i % 6}:00-06:00", fecha = "2026-10-0${1 + i % 9}", tipo = if (i % 4 == 0) "muestreo" else "animo",
                valencia = 1 + i % 7, activacion = 1 + i % 5, emociones = listOf("alegria", "calma").take(1 + i % 2), texto = "momento $i con @ana", con_quien = listOf("solo"))
        }
        return Respaldo(
            exportado = "2026-10-25T13:40:00-06:00", schema_v = SCHEMA_V, dias = dias, momentos = momentos,
            gastos = listOf(Gasto("g1", "2026-10-01", "09:15", 120.5, "Comida", nota = "tacos"), Gasto("g2", "2026-10-02", "03:10", 40.0, "Transporte", imprevisto = true)),
            cartas = listOf(Carta("c1", "2026-10-01", "2027-10-01", "Querido yo del futuro…"), Carta("c2", "2026-10-02", "2026-10-02", "Para @mama", para = "mama")),
            tramos = listOf(Tramo("t1", "2026-10-01", "tesis", "2026-10-01T10:00:00-06:00", "2026-10-01T11:30:00-06:00", 90, "capítulo dos")),
            consumos = listOf(Consumo("k1", "2026-10-03", "alcohol", inicio = "2026-10-03T21:00:00-06:00", niveles = listOf(Lectura("2026-10-03T22:00:00-06:00", 4)), cantidad = 3.0, cruda = 2)),
            config = Config(habitos = listOf(Habito("leer", "Leer")), equivalencias = mapOf("tésis" to "tesis"), personas = listOf(Persona("ana", "amistad_cercana", cercania = 6)),
                tema = "revolucion", temas_distintos = listOf("examen|examenes")),
        )
    }

    private fun zip(json: String, ubicaciones: Int): ByteArray {
        val b = ByteArrayOutputStream()
        ZipOutputStream(b).use { z ->
            z.putNextEntry(ZipEntry("vm_respaldo.json")); z.write(json.toByteArray()); z.closeEntry()
            z.putNextEntry(ZipEntry("vm_ubicaciones.csv"))
            z.write(("ts,lat,lon,precision_m,fuente,ms\n" + (1..ubicaciones).joinToString("") { "2026-10-01T10:00:00-06:00,22.15,-100.98,10,fondo,$it\n" }).toByteArray())
            z.closeEntry()
            z.putNextEntry(ZipEntry("portadas/obra1.jpg")); z.write(byteArrayOf(1, 2, 3)); z.closeEntry()
        }
        return b.toByteArray()
    }

    @Test fun jsonIdaYVuelta() {
        val r = ejemplo()
        val texto = JSON.encodeToString(Respaldo.serializer(), r)
        assertEquals(r, JSON.decodeFromString(Respaldo.serializer(), texto))
    }

    @Test fun zipCifradoIdaYVuelta() {
        val r = ejemplo()
        val zip = zip(JSON.encodeToString(Respaldo.serializer(), r), 120)
        val cifrado = Respaldos.cifrar(zip, "una contraseña larga".toCharArray())
        assertTrue(Respaldos.estaCifrado(cifrado))
        // Con otra contraseña no abre.
        assertNull(Respaldos.descifrar(cifrado, "otra".toCharArray()))
        val abierto = Respaldos.descifrar(cifrado, "una contraseña larga".toCharArray())
        assertNotNull(abierto)
        assertArrayEquals(zip, abierto)
        val c = Respaldos.contenido(abierto!!)
        assertNotNull(c)
        assertEquals(r, c!!.r)
        assertEquals(120, c.ubicaciones)
        assertEquals(1, c.portadas)
        assertTrue(Respaldos.informe(c).isNotEmpty())
    }

    /** El formato 2 guarda en el encabezado las iteraciones (600 000, en 4 bytes big-endian después de la marca). */
    @Test fun formatoNuevoGuardaIteraciones() {
        val cifrado = Respaldos.cifrar("hola".toByteArray(), "una contraseña larga".toCharArray())
        val marca = "VESTIGIUM-MEI-CIFRADO-2".toByteArray()
        assertArrayEquals(marca, cifrado.copyOfRange(0, marca.size))
        assertEquals(600_000, ByteBuffer.wrap(cifrado, marca.size, 4).int)
    }

    /**
     * Un .vmc del formato 1 (el de antes: marca, sal, iv y cuerpo, a 210 000 iteraciones) se sigue abriendo.
     * Se arma aquí a mano, como lo hacía la app, para no depender del código que se prueba.
     */
    private fun cifrarFormato1(datos: ByteArray, contrasena: CharArray): ByteArray {
        val magia = "VESTIGIUM-MEI-CIFRADO-1".toByteArray()
        val r = SecureRandom()
        val sal = ByteArray(16).also { r.nextBytes(it) }
        val iv = ByteArray(12).also { r.nextBytes(it) }
        val f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val llave = SecretKeySpec(f.generateSecret(PBEKeySpec(contrasena, sal, 210_000, 256)).encoded, "AES")
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, llave, GCMParameterSpec(128, iv))
        c.updateAAD(magia)
        return magia + sal + iv + c.doFinal(datos)
    }

    @Test fun formatoViejoSeAbre() {
        val r = ejemplo()
        val zip = zip(JSON.encodeToString(Respaldo.serializer(), r), 30)
        val viejo = cifrarFormato1(zip, "una contraseña larga".toCharArray())
        assertTrue(Respaldos.estaCifrado(viejo))
        assertNull(Respaldos.descifrar(viejo, "otra contraseña".toCharArray()))
        val abierto = Respaldos.descifrar(viejo, "una contraseña larga".toCharArray())
        assertArrayEquals(zip, abierto)
        assertEquals(r, Respaldos.contenido(abierto!!)!!.r)
    }

    /** Un encabezado con iteraciones fuera de 100 000 a 10 000 000 es un archivo dañado: no se intenta derivar la llave. */
    @Test fun iteracionesFueraDeRangoNoAbren() {
        val bueno = Respaldos.cifrar("hola".toByteArray(), "una contraseña larga".toCharArray())
        val o = "VESTIGIUM-MEI-CIFRADO-2".length
        for (n in listOf(Int.MAX_VALUE, -1, 0, 99_999, 10_000_001)) {
            val malo = bueno.copyOf()
            ByteBuffer.wrap(malo, o, 4).putInt(n)
            assertTrue(Respaldos.estaCifrado(malo))
            assertNull(Respaldos.descifrar(malo, "una contraseña larga".toCharArray()))
        }
        // Lo que no lleva ninguna marca no está cifrado; y truncado tras la marca, tampoco se abre.
        assertFalse(Respaldos.estaCifrado("PK hola".toByteArray()))
        assertNull(Respaldos.descifrar(bueno.copyOfRange(0, o + 10), "una contraseña larga".toCharArray()))
    }

    @Test fun jsonSueltoTambienSeLee() {
        val r = ejemplo()
        val c = Respaldos.contenido(JSON.encodeToString(Respaldo.serializer(), r).toByteArray())
        assertEquals(r, c!!.r)
    }

    @Test fun noEsRespaldo() {
        assertNull(Respaldos.contenido("{\"hola\": 1}".toByteArray()))
    }

    /** Un respaldo de una versión vieja (sin los campos nuevos) se sigue leyendo, con los valores por omisión. */
    @Test fun respaldoViejoSeLee() {
        val viejo = """{"exportado":"2026-01-01T00:00:00-06:00","schema_v":5,"dias":[{"fecha":"2026-01-01","valencia":5,"nota":"hola @ana"}],"momentos":[],"campo_que_ya_no_existe":3}"""
        val c = Respaldos.contenido(viejo.toByteArray())
        assertNotNull(c)
        assertEquals(1, c!!.r.dias.size)
        assertEquals(emptyMap<String, String>(), c.r.dias[0].presencias)
        assertEquals(false, c.r.dias[0].presencias_ok)
    }

    /** Restaurar en un teléfono con datos combina los ajustes sin perder nada de lo de aquí. */
    @Test fun ajustesSeCombinan() {
        val aqui = Config(habitos = listOf(Habito("meditar", "Meditar")), equivalencias = mapOf("ana_m" to "ana"), temas_distintos = listOf("a|b"))
        val delRespaldo = ejemplo().config
        val c = Importar.combinarConfig(aqui, delRespaldo, nuevo = false)
        assertEquals(setOf("meditar", "leer"), c.habitos.map { it.id }.toSet())
        assertEquals("ana", c.equivalencias["ana_m"]); assertEquals("tesis", c.equivalencias["tésis"])
        assertEquals(setOf("a|b", "examen|examenes"), c.temas_distintos.toSet())
    }
}
