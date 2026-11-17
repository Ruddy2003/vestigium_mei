package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.ClaveFondo
import io.github.ruddy2003.vestigium.ui.MS_ENTRE_CAMBIOS_DE_LUZ
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.escenas.PROPORCION_CUADRO
import io.github.ruddy2003.vestigium.ui.escenas.escenaDe
import io.github.ruddy2003.vestigium.ui.escenas.momentoDelFondo
import io.github.ruddy2003.vestigium.ui.escenas.recorteFranja
import io.github.ruddy2003.vestigium.ui.tocaCambiarFondo
import io.github.ruddy2003.vestigium.vidas.Franja
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * Los fondos del teléfono (parte 6): cuándo toca cambiar el fondo y dónde cae la franja del cuadro. Lo que solo se ve en
 * el teléfono (que `WallpaperManager` lo ponga, que el pulso llegue a tiempo) queda para probarlo ahí.
 */
class FondoTelefonoPrueba {
    private val papel = ClaveFondo("heleno", oscuro = false)
    private val manana = ClaveFondo("heleno", oscuro = false, cuadro = true, dia = "2027-04-12", luz = Franja.MANANA.name)
    private val largo = MS_ENTRE_CAMBIOS_DE_LUZ + 1

    // ---------------------------------------------------------------- la clave

    @Test fun laClaveSeLeeComoSeGuardo() {
        listOf(papel, manana, ClaveFondo("psicodelia", true, "intensa"), manana.copy(oscuro = true, luz = Franja.NOCHE.name)).forEach {
            assertEquals(it, ClaveFondo.leer(it.texto()))
        }
    }

    @Test fun lasClavesDeAntesNoSeLeen() {
        // «romano/false» era la clave de antes: no se lee, y entonces el fondo se pone una vez con la clave nueva.
        listOf(null, "", "romano/false", "psicodelia/true/intensa", "f2|romano", "f2|romano|quizas|||||", "f2||false||papel||", "f2|romano|false||otro||")
            .forEach { assertNull(it, ClaveFondo.leer(it)) }
    }

    // ---------------------------------------------------------------- cuándo cambia

    @Test fun sinNadaPuestoSePone() {
        assertTrue(tocaCambiarFondo(null, papel, 0))
        assertTrue(tocaCambiarFondo(null, manana, 0))
    }

    @Test fun elMismoNoSeVuelveAPoner() {
        assertFalse(tocaCambiarFondo(papel, papel, 0))
        assertFalse(tocaCambiarFondo(papel, papel, largo * 100))
        assertFalse(tocaCambiarFondo(manana, manana.copy(), largo))
    }

    @Test fun otroTemaModoCaraOEstiloSePoneEnElMomento() {
        assertTrue(tocaCambiarFondo(papel, papel.copy(tema = "gotico"), 0))
        assertTrue(tocaCambiarFondo(papel, papel.copy(oscuro = true), 0))
        assertTrue(tocaCambiarFondo(ClaveFondo("psicodelia", false, "base"), ClaveFondo("psicodelia", false, "intensa"), 0))
        assertTrue(tocaCambiarFondo(papel, manana, 0))   // del papel al cuadro
        assertTrue(tocaCambiarFondo(manana, papel, 0))   // del cuadro al papel
        assertTrue(tocaCambiarFondo(manana, manana.copy(tema = "gotico"), 0))
        assertTrue(tocaCambiarFondo(manana, manana.copy(oscuro = true), 0))
    }

    @Test fun otraLuzSeEsperaAlMenosDiezMinutos() {
        val mediodia = manana.copy(luz = Franja.MEDIODIA.name)
        assertFalse(tocaCambiarFondo(manana, mediodia, 0))
        assertFalse(tocaCambiarFondo(manana, mediodia, MS_ENTRE_CAMBIOS_DE_LUZ - 1))
        assertTrue(tocaCambiarFondo(manana, mediodia, MS_ENTRE_CAMBIOS_DE_LUZ))
        // Otro día, igual (a las 5:00 cambian a la vez el día y la luz).
        assertTrue(tocaCambiarFondo(manana.copy(luz = Franja.NOCHE.name), manana.copy(dia = "2027-04-13", luz = Franja.AMANECER.name), largo))
        assertFalse(tocaCambiarFondo(manana, manana.copy(dia = "2027-04-13"), 60_000))
        // Un reloj que va hacia atrás no lo deja trabado.
        assertTrue(tocaCambiarFondo(manana, mediodia, -5_000))
    }

    @Test fun conElPulsoDe15MinutosCadaTramoNuevoSePone() {
        // Un día entero de pulsos cada 15 minutos con el cuadro de seis franjas: el fondo cambia una vez por tramo, nunca más.
        val e = escenaDe(TemaId.HELENO)!!
        var ultimo: ClaveFondo? = null; var hace = 0L; var cambios = 0
        var minuto = 5 * 60
        while (minuto < 29 * 60) {
            val hora = (minuto / 60.0) % 24
            val dia = "2027-04-12"   // de 5:00 a 5:00 es el mismo día lógico
            val m = momentoDelFondo(e, dia, hora)!!
            val k = manana.copy(dia = dia, luz = m.luz.franja.name)
            if (tocaCambiarFondo(ultimo, k, hace)) { ultimo = k; hace = 0; cambios++ }
            hace += 15 * 60_000L; minuto += 15
        }
        assertEquals("amanecer, mañana, mediodía, tarde, atardecer y noche", 6, cambios)
    }

    // ---------------------------------------------------------------- la franja

    @Test fun laFranjaNuncaSeSaleDelCuadro() {
        val pantallas = listOf(1080 to 2424, 1080 to 2400, 1440 to 3120, 720 to 1600, 1080 to 1080, 2424 to 1080, 1 to 1, 1 to 5000)
        for ((w, h) in pantallas) for (k in 0..40) {
            val c = k / 20f - 0.5f   // de -0.5 a 1.5: también fuera de 0..1
            val r = recorteFranja(w, h, c)
            assertEquals(w, r.ancho); assertEquals(h, r.alto)
            assertTrue("$w×$h c=$c: $r", r.izquierda >= 0 && r.izquierda + r.ancho <= r.anchoCuadro)
            assertTrue("$w×$h c=$c: $r", r.arriba >= 0 && r.arriba + r.alto <= r.altoCuadro)
        }
        val r = recorteFranja(1080, 2424, Float.NaN)
        assertEquals(recorteFranja(1080, 2424, 0.5f), r)
    }

    @Test fun elCuadroGuardaSuProporcionYCubreLaPantalla() {
        for ((w, h) in listOf(1080 to 2424, 1440 to 3120, 720 to 1600, 2424 to 1080, 1080 to 1080)) {
            val r = recorteFranja(w, h, 0.5f)
            assertTrue(r.anchoCuadro >= w && r.altoCuadro >= h)
            // Sin deformar: la proporción del cuadro, salvo el redondeo a pixeles.
            assertTrue("$r", abs(r.anchoCuadro.toFloat() / r.altoCuadro - PROPORCION_CUADRO) < 2f / r.altoCuadro)
            // Y sin sobrar de los dos lados a la vez: cubre justo (a lo alto o a lo ancho).
            assertTrue("$r", r.altoCuadro == h || r.anchoCuadro <= w + 1)
        }
    }

    @Test fun enElPixelLaFranjaEsUnTercioDelCuadro() {
        val r = recorteFranja(1080, 2424, 0.5f)
        assertEquals(2424, r.altoCuadro); assertEquals(3232, r.anchoCuadro)
        assertEquals(0, r.arriba)
        assertEquals((3232 - 1080) / 2, r.izquierda)
        assertEquals(0, recorteFranja(1080, 2424, 0f).izquierda)
        assertEquals(3232 - 1080, recorteFranja(1080, 2424, 1f).izquierda)
        // El centro pedido queda en el centro de la franja cuando cabe.
        val g = recorteFranja(1080, 2424, 0.4f)
        assertTrue(abs(g.izquierda + 540 - 0.4f * 3232) <= 1f)
    }

    @Test fun cadaCuadroTieneSuFranjaDentroDelCuadro() {
        TemaId.entries.mapNotNull { escenaDe(it) }.forEach { e ->
            assertTrue("${e.vidas}: ${e.franjaFondo}", e.franjaFondo in 0f..1f)
        }
        // Y al menos un cuadro no va al centro: cada uno tiene su sitio.
        assertNotEquals(0, TemaId.entries.mapNotNull { escenaDe(it) }.count { it.franjaFondo != 0.5f })
    }
}
