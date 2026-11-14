package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import io.github.ruddy2003.vestigium.ui.escenas.CapillaUrbe
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrbe
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Estancia
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Lado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cae
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.solQueEntra
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La estancia de la biblioteca (`paisaje/Estancia.kt`), la pieza de los interiores con ventana, sale de la capilla de
 * Roma, que el autor aprobó. Aquí se comprueba que, con las medidas de la capilla, da los mismos puntos y el mismo sol
 * que ella: así los interiores nuevos heredan lo aprobado sin copiarlo.
 */
class EstanciaPrueba {
    /** La capilla, escrita como estancia: sus medidas están en `CapillaUrbe`. */
    private val capilla = Estancia(CapillaUrbe.FUGA, 300f, CapillaUrbe.X0, CapillaUrbe.X1, CapillaUrbe.ARRANQUE, CapillaUrbe.SUELO, CapillaUrbe.CERCA)
    private fun momento(hora: Double) = Momento(FichaDelDia.de("2027-05-06", "restauradora")!!.copy(clima = Clima.DESPEJADO), hora)
    private fun igual(a: Offset, b: Offset) { assertEquals(a.x, b.x, 0.001f); assertEquals(a.y, b.y, 0.001f) }

    @Test fun llevaLosPuntosAlLienzoComoLaCapilla() {
        for (x in listOf(100f, 150f, 182f, 264f)) for (y in listOf(60f, 120f, 200f)) for (t in listOf(0f, 30f, 110f, 196f)) {
            igual(CapillaUrbe.p(x, y, t), capilla.p(x, y, t)); assertEquals(CapillaUrbe.s(t), capilla.s(t), 0.0001f)
        }
        // En la pared del fondo, una unidad de la estancia es una del lienzo.
        igual(Offset(140f, 90f), capilla.p(140f, 90f))
        // Lo que está más cerca de quien mira se ve más grande.
        assertTrue(capilla.s(150f) > capilla.s(20f))
    }

    @Test fun elSolEntraComoEnLaCapilla() {
        for (hora in listOf(7.0, 9.5, 13.0, 17.0, 19.0, 23.0)) {
            val m = momento(hora); val suyo = EscenaUrbe.solQueEntra(m); val nuestro = solQueEntra(m.luces())
            if (suyo == null) { assertNull("$hora", nuestro); continue }
            assertNotNull("$hora", nuestro)
            assertEquals(suyo.e, nuestro!!.e, 0.0001f); assertEquals(suyo.a, nuestro.a, 0.0001f); assertEquals(suyo.fuerza, nuestro.fuerza, 0.0001f)
            // El centro de la mancha cae en el mismo sitio.
            igual(CapillaUrbe.centroDeLaMancha(suyo), capilla.cae(80f, 120f, nuestro))
        }
    }

    @Test fun laManchaSeMueveConLaHoraYPorLaOtraParedSaleEnEspejo() {
        val manana = solQueEntra(momento(7.0).luces())!!; val mediodia = solQueEntra(momento(13.0).luces())!!
        // Con el sol bajo el rayo cruza más estancia antes de tocar el suelo que a mediodía.
        assertTrue(mediodia.e > manana.e)
        val izq = capilla.cae(80f, 120f, mediodia, Lado.IZQUIERDA); val der = capilla.cae(80f, 120f, mediodia, Lado.DERECHA)
        // Entrando por la derecha, la mancha cae a la misma distancia de su pared y a la misma altura y hondura.
        assertEquals(izq.y, der.y, 0.001f)
        assertTrue(izq.x > capilla.p(capilla.x0, 120f, 80f).x && der.x < capilla.p(capilla.x1, 120f, 80f).x)
        // De noche, con luna, entra una mancha fría.
        val luna = solQueEntra(momento(23.0).luces())
        if (luna != null) assertTrue(luna.deLuna)
    }
}
