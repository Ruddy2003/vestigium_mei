package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Rect
import io.github.ruddy2003.vestigium.ui.galeria.TextosGaleria
import io.github.ruddy2003.vestigium.ui.vida.MuebleGaleriaComun
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/** La galería de cuadros (parte 8, encargo C): dónde caen los huecos del mueble y los textos de la pantalla. */
class GaleriaPrueba {
    /** El nicho por omisión de `DisenoVida.areaGaleria`. */
    private val nicho = Rect(0.06f, 0.16f, 0.94f, 0.82f)

    @Test fun losHuecosSonCuatroDeCuatroATresDentroDelNichoYSinTocarse() {
        // Un teléfono angosto, uno normal, uno ancho y una tableta, a 2.625 px por dp; 190 dp de alto.
        listOf(296f, 328f, 380f, 568f).forEach { dp ->
            val w = dp * 2.625f; val h = 190f * 2.625f
            val c = MuebleGaleriaComun.colgados(w, h, nicho)
            assertEquals(MuebleGaleriaComun.CUANTOS, c.size)
            val n = Rect(nicho.left * w, nicho.top * h, nicho.right * w, nicho.bottom * h)
            c.forEach { k ->
                assertEquals("4:3 a $dp dp", 4f / 3f, k.hueco.width / k.hueco.height, 0.01f)
                assertTrue("el marco cabe en el nicho a $dp dp", k.marco.left >= n.left - 0.5f && k.marco.right <= n.right + 0.5f && k.marco.top >= n.top - 0.5f && k.marco.bottom <= n.bottom + 0.5f)
                assertTrue(k.hueco.left > k.marco.left && k.hueco.right < k.marco.right)
            }
            for (i in c.indices) for (j in i + 1 until c.size) assertTrue("los marcos $i y $j no se tocan a $dp dp", !c[i].marco.overlaps(c[j].marco))
            // El primero (el más nuevo) es el más grande.
            assertTrue(c.drop(1).all { it.hueco.width < c[0].hueco.width })
        }
    }

    @Test fun sinSitioNoHayHuecosNiSeCuelga() {
        assertTrue(MuebleGaleriaComun.colgados(0f, 0f, nicho).isEmpty())
        assertTrue(MuebleGaleriaComun.colgados(0.5f, 500f, nicho).isEmpty())
        assertTrue(MuebleGaleriaComun.colgados(500f, 0f, nicho).isEmpty())
        assertTrue(MuebleGaleriaComun.colgados(Float.NaN, 500f, nicho).isEmpty())
        assertTrue(MuebleGaleriaComun.colgados(20f, 10f, nicho).isEmpty())
        // Muy angosto para su alto: los marcos se achican, pero siguen siendo 4:3 y caben.
        val c = MuebleGaleriaComun.colgados(300f, 900f, nicho)
        c.forEach { assertTrue(abs(it.hueco.width / it.hueco.height - 4f / 3f) < 0.01f); assertTrue(it.marco.right <= 300f * 0.94f + 0.5f) }
    }

    @Test fun laHoraNuncaPasaDeCincuentaYNueveMinutos() {
        assertEquals("13:40", TextosGaleria.hora(13 + 40 / 60.0))
        assertEquals("00:00", TextosGaleria.hora(0.0))
        assertEquals("23:59", TextosGaleria.hora(23.9999999))
        assertEquals("01:00", TextosGaleria.hora(25.0))
        assertEquals("—", TextosGaleria.hora(Double.NaN))
    }

    @Test fun losTextos() {
        assertEquals(TextosGaleria.INVITACION, TextosGaleria.detalle(0, null))
        assertEquals("1 cuadro guardado · el último, del 21 de marzo de 2027", TextosGaleria.detalle(1, "2027-03-21"))
        assertEquals("5 cuadros guardados", TextosGaleria.detalle(5, "no es un día"))
        assertEquals("17 de noviembre de 2026, 21:04", TextosGaleria.guardado("2026-11-17T21:04:33-06:00"))
        assertEquals("ayer", TextosGaleria.guardado("ayer"))
    }
}
