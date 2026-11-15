package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.CocinaUrss
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrss
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosUrss
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate

/**
 * Los doce objetos del día de Liudmila (`ObjetosUrss`, tanda S15): cuáles son grandes y cuáles chicos, que la cocina no
 * pinta su pieza el día en que esa pieza es el objeto (para que no salga dos veces) y que el cuadro se dibuja con cada
 * uno, en la casa vieja, en la nueva y en el sanatorio.
 */
class EscenaUrssObjetosPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "obrera_moscu")!!, hora)
    private fun conObjeto(dia: String, hora: Double, o: String?) = Momento(FichaDelDia.de(dia, "obrera_moscu")!!.copy(objeto = o), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun pincel(m: Momento): Pincel { val l = EscenaUrss.luces(m); return Pincel(l, Rayo(l)) }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosUrss.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosUrss.TODOS.size)
    }

    /** Cuatro grandes (el televisor, el abrigo, las botas y el chal) y ocho chicos; cada día el cuadro sabe cuál le toca. */
    @Test fun cadaObjetoEsGrandeOChico() {
        assertEquals(setOf("televisor", "abrigo", "botas", "chal"), ObjetosUrss.GRANDES)
        assertEquals(setOf("bolsa", "moneda", "libro", "radio", "samovar", "termo", "pase", "postal"), ObjetosUrss.TODOS.filter { !ObjetosUrss.esGrande(it) }.toSet())
        assertFalse(ObjetosUrss.esGrande(null as String?))
        cadaDia { d, m -> val o = m.ficha.objeto ?: return@cadaDia; assertEquals("$d: $o", o in ObjetosUrss.GRANDES, ObjetosUrss.esGrande(m)) }
        // Un grande devuelve su cosa; un chico, o ningún objeto, nada. Los chicos dan su silueta para la sombra en el hule.
        for (o in ObjetosUrss.TODOS + listOf<String?>(null)) {
            val m = conObjeto("2027-01-20", 13.0, o)
            val g = ObjetosUrss.grande(m, pincel(m), CocinaUrss.PIE_GRANDE, CocinaUrss.APOYO_GRANDE, CocinaUrss.K.s(70f))
            if (o != null && o in ObjetosUrss.GRANDES) { assertNotNull(o, g); assertEquals(o, CocinaUrss.PIE_GRANDE, g!!.pie) } else assertNull("$o", g)
            val s = ObjetosUrss.silueta(m, CocinaUrss.SOBRE_LA_MESA, CocinaUrss.K.s(60f)).getBounds()
            assertEquals("$o", o != null && o !in ObjetosUrss.GRANDES, !s.isEmpty)
        }
    }

    /**
     * Las piezas de la cocina que también son objetos (el samovar, el televisor, la radio, el abrigo, el chal, las botas y
     * la bolsa) las dibuja el contrato el día que tocan, y ese día la cocina no pinta la suya; los demás días, sí.
     */
    @Test fun laCocinaNoPintaElSuyoElDiaQueEsElObjeto() {
        assertEquals(setOf("samovar", "televisor", "radio", "abrigo", "chal", "botas", "bolsa"), EscenaUrss.DE_LA_COCINA)
        assertEquals(EscenaUrss.DE_LA_COCINA, ObjetosUrss.DIBUJADOS)
        cadaDia { d, m ->
            for (p in EscenaUrss.DE_LA_COCINA) assertEquals("$d $p", m.ficha.objeto != p, EscenaUrss.laCocinaPinta(m, p))
        }
        // El día del samovar, el de la mesa no echa vaho (el del objeto lleva el suyo), pero el té sigue servido.
        val noche = conObjeto("2027-01-20", 21.0, "samovar")
        assertTrue(EscenaUrss.samovarEnchufado(noche)); assertFalse(EscenaUrss.samovar(noche)); assertTrue(EscenaUrss.te(noche))
        assertTrue(EscenaUrss.samovar(conObjeto("2027-01-20", 21.0, "termo")))
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** El cuadro se dibuja con cada objeto a las tres horas, en invierno y en verano, en la casa vieja, en la nueva y en el sanatorio. */
    @Test fun elCuadroSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val casas = HashSet<EscenaUrss.Casa>(); var sanatorio = false
        for (dia in listOf("2027-01-20", "2027-07-15", "2030-01-15", "2030-07-15", "2028-07-15")) for (h in listOf(7.0, 13.0, 21.0)) {
            val base = momento(dia, h); casas += EscenaUrss.casa(base); sanatorio = sanatorio || EscenaUrss.enElSanatorio(base)
            for (o in ObjetosUrss.TODOS) {
                val m = conObjeto(dia, h, o)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaUrss) { quieta(m) } }
                        lienzo { with(EscenaUrss) { viva(m, 1.5f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o»: $x", x)
                }
            }
        }
        assertEquals(setOf(EscenaUrss.Casa.VIEJA, EscenaUrss.Casa.NUEVA), casas)
        assertTrue(sanatorio)
        // Y cada objeto suelto, a otra escala, en otro sitio.
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(ImageBitmap(380, 285)), Size(380f, 285f)) {
            for (o in ObjetosUrss.TODOS) {
                val m = conObjeto("2027-03-21", 13.0, o)
                with(ObjetosUrss) { sobreElSoporte(m, pincel(m), Offset(200f, 180f), 1f) }
                ObjetosUrss.grande(m, pincel(m), Offset(150f, 260f), Offset(170f, 190f), 1f)?.pintar?.invoke(this)
            }
        }
    }
}
