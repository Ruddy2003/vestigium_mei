package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.CuentosLeidos
import io.github.ruddy2003.vestigium.data.EspecialLeido
import io.github.ruddy2003.vestigium.ui.escenas.LecturaCuento
import io.github.ruddy2003.vestigium.ui.escenas.ReglasAmbiente
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import io.github.ruddy2003.vestigium.vidas.Franja
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las reglas puras del cuento con su cuadro (parte 8, encargo B; `ui/escenas/CuentoReglas.kt`): cuándo suena el ambiente y
 * el ronroneo, el fundido, qué archivo toca de noche, cuándo un cuento cuenta como leído y cómo se llama un día pasado.
 */
class CuentoReglasPrueba {

    @Test fun elAmbienteNoDependeDelAjusteDeSonidos() {
        // Decisión del director: solo su interruptor y el modo del teléfono. No hay parámetro de `Config.sonidos`, y con
        // la configuración de fábrica (sonidos apagados, ambiente encendido) el ambiente puede sonar.
        val c = Config()
        assertFalse("los sonidos vienen apagados", c.sonidos)
        assertTrue("el ambiente viene encendido", c.ambiente_cuadro)
        assertTrue(ReglasAmbiente.puedeEmpezar(c.ambiente_cuadro, timbreNormal = true, llamada = false, hayMusica = false))
        // Sin su interruptor, en silencio o vibración, durante una llamada o con música de otra app, no empieza.
        assertFalse(ReglasAmbiente.puedeEmpezar(false, true, false, false))
        assertFalse(ReglasAmbiente.puedeEmpezar(true, false, false, false))
        assertFalse(ReglasAmbiente.puedeEmpezar(true, true, true, false))
        assertFalse(ReglasAmbiente.puedeEmpezar(true, true, false, true))
        // Ya sonando, la música (que sería él mismo) no lo calla; lo demás sí.
        assertTrue(ReglasAmbiente.puedeSeguir(true, true, false))
        assertFalse(ReglasAmbiente.puedeSeguir(false, true, false))
        assertFalse(ReglasAmbiente.puedeSeguir(true, false, false))
        assertFalse(ReglasAmbiente.puedeSeguir(true, true, true))
    }

    @Test fun elRonroneoSigueElAjusteDeSonidos() {
        assertTrue(ReglasAmbiente.puedeRonronear(sonidos = true, timbreNormal = true, llamada = false))
        assertFalse(ReglasAmbiente.puedeRonronear(sonidos = false, timbreNormal = true, llamada = false))
        assertFalse(ReglasAmbiente.puedeRonronear(sonidos = true, timbreNormal = false, llamada = false))
        assertFalse(ReglasAmbiente.puedeRonronear(sonidos = true, timbreNormal = true, llamada = true))
    }

    @Test fun elFundido() {
        val v = ReglasAmbiente.VOLUMEN
        assertTrue("suave pero no tenue (0,35 a 0,5)", v in 0.35f..0.5f)
        assertEquals(2000L, ReglasAmbiente.ENTRADA_MS)
        assertEquals(1500L, ReglasAmbiente.SALIDA_MS)
        // Entrada: empieza en 0, termina en el volumen, sube sin bajar nunca y pasa por la mitad a la mitad del tiempo.
        assertEquals(0f, ReglasAmbiente.rampa(0f, v, 0L, 2000L), 1e-6f)
        assertEquals(v, ReglasAmbiente.rampa(0f, v, 2000L, 2000L), 1e-6f)
        assertEquals(v, ReglasAmbiente.rampa(0f, v, 9000L, 2000L), 1e-6f)
        assertEquals(v / 2, ReglasAmbiente.rampa(0f, v, 1000L, 2000L), 1e-5f)
        var antes = -1f
        for (ms in 0L..2000L step 40L) { val x = ReglasAmbiente.rampa(0f, v, ms, 2000L); assertTrue(x >= antes); antes = x }
        // Arranca despacio: a los primeros 40 ms apenas se oye.
        assertTrue(ReglasAmbiente.rampa(0f, v, 40L, 2000L) < v * 0.01f)
        // Salida: desde donde esté hasta 0, y nunca negativo.
        assertEquals(0f, ReglasAmbiente.rampa(0.3f, 0f, 1500L, 1500L), 1e-6f)
        for (ms in 0L..1600L step 50L) assertTrue(ReglasAmbiente.rampa(0.3f, 0f, ms, 1500L) in 0f..0.3f)
        // Sin duración, salta a la meta.
        assertEquals(0.2f, ReglasAmbiente.rampa(0.9f, 0.2f, 0L, 0L), 1e-6f)
        assertEquals(0.9f, ReglasAmbiente.rampa(0.9f, 0.2f, -5L, 100L), 1e-6f)
    }

    @Test fun elArchivoDeNoche() {
        assertTrue(ReglasAmbiente.esNoche(Franja.NOCHE))
        assertTrue("la luz tenue de la nave cuenta como noche", ReglasAmbiente.esNoche(Franja.TENUE))
        for (f in listOf(Franja.AMANECER, Franja.MANANA, Franja.MEDIODIA, Franja.TARDE, Franja.ATARDECER, Franja.ENCENDIDO)) assertFalse(f.name, ReglasAmbiente.esNoche(f))
        val hay = setOf("a_heleno_ambiente", "a_heleno_ambiente_noche", "a_neon_ambiente")
        assertEquals("a_heleno_ambiente_noche", ReglasAmbiente.elegir("heleno", true) { it in hay })
        assertEquals("a_heleno_ambiente", ReglasAmbiente.elegir("heleno", false) { it in hay })
        // Sin versión de noche (Ciudad neón), la de siempre.
        assertEquals("a_neon_ambiente", ReglasAmbiente.elegir("neon", true) { it in hay })
        // Un tema sin ambiente todavía: nada.
        assertNull(ReglasAmbiente.elegir("egipto", false) { it in hay })
    }

    @Test fun losAmbientesQueHayEnElRepositorio() {
        // Cada tema con escena tiene su ambiente (y el ronroneo existe). Se mira la carpeta res/raw.
        val raw = listOf(java.io.File("src/main/res/raw"), java.io.File("app/src/main/res/raw")).firstOrNull { it.isDirectory } ?: return
        val temas = listOf("romano", "urbe", "neon", "heleno", "medieval", "distopia", "vapor", "ruinas", "seda", "gotico", "arcano", "espacio",
            "andalus", "renacimiento", "pirata", "revolucion")
        for (t in temas) {
            assertTrue("falta a_${t}_ambiente.ogg", raw.resolve("a_${t}_ambiente.ogg").isFile)
            assertEquals(t, "a_${t}_ambiente", ReglasAmbiente.elegir(t, true) { raw.resolve("$it.ogg").isFile }?.removeSuffix("_noche"))
        }
        assertTrue(raw.resolve("${ReglasAmbiente.RONRONEO}.ogg").isFile)
    }

    @Test fun leerEsLlegarAlFinal() {
        // El marcador de 1 dp (unos 3 px) a la vista, dentro de una ventana de 2400 px.
        assertTrue(LecturaCuento.alaVista(2300f, 2303f, 2400f))
        assertTrue(LecturaCuento.alaVista(0f, 3f, 2400f))
        // Recortado a nada (fuera de la vista: el rectángulo queda vacío) o fuera de la ventana: todavía no.
        assertFalse(LecturaCuento.alaVista(2400f, 2400f, 2400f))
        assertFalse(LecturaCuento.alaVista(0f, 0f, 2400f))
        assertFalse(LecturaCuento.alaVista(2500f, 2503f, 2400f))
        assertFalse(LecturaCuento.alaVista(-10f, -7f, 2400f))
    }

    @Test fun loLeidoGuardaSusDiasEspeciales() {
        // El 18 de marzo de 2027 Demeas está en una fiesta (las capturas del cuento usan ese día).
        val ficha = FichaDelDia.de("2027-03-18", "remero")!!
        val esp = LecturaCuento.especiales(ficha)
        assertEquals(ficha.especiales.size, esp.size)
        assertTrue("es un día especial", esp.isNotEmpty())
        esp.forEach { e -> assertTrue(e.inicio.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) }
        // Marcarlo leído con lo que da: se queda una vez, la primera, con sus especiales.
        val c = CuentosLeidos.marcar(Config(), "remero", "2027-03-18", "2027-03-18", "2027-03-18T21:00:00-06:00", esp)
        val c2 = CuentosLeidos.marcar(c, "remero", "2027-03-18", "2027-03-19", null, emptyList())
        assertEquals(1, c2.cuentos_leidos.size)
        assertEquals(esp, c2.cuentos_leidos.single().especiales)
        assertTrue(CuentosLeidos.mismoDia(c2.cuentos_leidos.single()))
        // Un día normal no lleva especiales.
        val normal = FichaDelDia.de("2027-02-03", "remero")!!
        assertEquals(emptyList<EspecialLeido>(), LecturaCuento.especiales(normal))
    }

    @Test fun elTituloDeUnDiaPasado() {
        assertEquals("Hoy, en su vida", LecturaCuento.tituloSeccion("2027-03-21", "2027-03-21"))
        assertEquals("Ayer, en su vida", LecturaCuento.tituloSeccion("2027-03-20", "2027-03-21"))
        assertEquals("El 2 de marzo, en su vida", LecturaCuento.tituloSeccion("2027-03-02", "2027-03-21"))
        assertEquals("El 21 de diciembre de 2026, en su vida", LecturaCuento.tituloSeccion("2026-12-21", "2027-03-21"))
        // Ayer, aunque cambie el año.
        assertEquals("Ayer, en su vida", LecturaCuento.tituloSeccion("2026-12-31", "2027-01-01"))
        assertEquals("El cuento de hoy", LecturaCuento.tituloPantalla("2027-03-21", "2027-03-21"))
        assertEquals("El cuento del 2 de marzo", LecturaCuento.tituloPantalla("2027-03-02", "2027-03-21"))
        // Lo que no es fecha no rompe nada.
        assertEquals("El x, en su vida", LecturaCuento.tituloSeccion("x", "2027-03-21"))
    }
}
