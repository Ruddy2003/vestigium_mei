package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.CamDeco
import io.github.ruddy2003.vestigium.ui.escenas.CosasDeco
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosDeco
import io.github.ruddy2003.vestigium.ui.escenas.VistaDeco
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
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
 * Los doce objetos del día de Lou (`ObjetosDeco`, tanda S15): cuáles son grandes (van en el suelo, al pie de la mesa
 * cercana) y cuáles chicos (sobre la mesa), que cada día el cuadro sabe cuál le toca, que los grandes devuelven su
 * silueta, que el piano no se dibuja dos veces, y que el cuadro se dibuja con cada uno en el club, en el club nuevo y en
 * los dos lugares provisionales (la gira y sin club).
 */
class EscenaDecoObjetosPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "pianista")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosDeco.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosDeco.TODOS.size)
    }

    /** Dos grandes (la radio y los zapatos), nueve chicos y el piano, que no es ni lo uno ni lo otro: lo señala el ramo. */
    @Test fun cadaObjetoEsGrandeOChico() {
        assertEquals(setOf("radio", "zapatos"), ObjetosDeco.GRANDES)
        assertTrue(ObjetosDeco.TODOS.containsAll(ObjetosDeco.GRANDES))
        assertEquals(setOf("piano", "partitura", "sombrero", "ficha", "taza", "disco", "collar", "panuelo", "cigarrillo", "metronomo"), ObjetosDeco.TODOS.filter { !ObjetosDeco.esGrande(it) }.toSet())
        assertFalse(ObjetosDeco.esGrande(null as String?))
        cadaDia { d, m -> val o = m.ficha.objeto ?: return@cadaDia; assertEquals("$d: $o", o in ObjetosDeco.GRANDES, ObjetosDeco.esGrande(m)) }
    }

    /** Un grande devuelve su cosa con el pie que se le dio y una silueta que sube del suelo (los zapatos, un palmo más cerca, bajo la mesa); un chico, o ninguno, nada. */
    @Test fun losGrandesPisanJuntoALaMesa() {
        val m = momento("2028-10-12", 13.0); val v = VistaDeco(m)
        val pie = CamDeco.p(CosasDeco.PIE_GRANDE); val apoyo = CamDeco.p(CosasDeco.APOYO_GRANDE); val escala = CamDeco.k(CosasDeco.PIE_GRANDE.z) / 120f
        val p = v.pincel(pie)
        for (o in ObjetosDeco.TODOS + listOf(null)) {
            val g = ObjetosDeco.grande(v, o, p, pie, apoyo, escala)
            if (o == null || o !in ObjetosDeco.GRANDES) { assertNull("$o", g); continue }
            assertNotNull(o, g); g!!
            assertEquals(o, pie, g.pie)
            val caja = g.forma.getBounds()
            assertTrue("$o: la silueta llega al suelo (${caja.bottom})", caja.bottom >= pie.y - 12f && caja.bottom <= pie.y + 18f)
            assertTrue("$o: la silueta sube (${caja.top})", caja.top < pie.y - 6f)
            assertTrue("$o: no se va de lado (${caja.left}..${caja.right})", caja.left > pie.x - 60f && caja.right < pie.x + 50f)
            assertEquals(o, caja, ObjetosDeco.grande(v, o, p, pie, apoyo, escala)!!.forma.getBounds())
        }
        // La radio es lo más alto: pasa de la mitad del canto de la mesa.
        assertTrue(ObjetosDeco.grande(v, "radio", p, pie, apoyo, escala)!!.forma.getBounds().top < (pie.y + apoyo.y) / 2f)
        cadaDia { d, mm -> if (d.dayOfMonth == 1) assertEquals("$d", ObjetosDeco.esGrande(mm), ObjetosDeco.grande(mm, p, pie, apoyo, escala) != null) }
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** Cada objeto se dibuja suelto, a dos escalas, en las fases del día, en la mesa puesta y en la desnuda, y en sitios cualesquiera. */
    @Test fun cadaObjetoSeDibujaSuelto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val img = Canvas(ImageBitmap(380, 285))
        for (h in listOf(7.0, 13.0, 15.5, 21.0, 4.5)) {
            val v = VistaDeco(momento("2028-10-12", h))
            for (escala in listOf(1f, 1.1f, 0.6f)) for (o in ObjetosDeco.TODOS + listOf(null, "otro")) {
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, img, Size(380f, 285f)) {
                        with(ObjetosDeco) { chico(v, o, v.pincel(Offset(259f, 211f)), Offset(259f, 211f), escala) }
                        with(ObjetosDeco) { chico(v, o, v.pincel(Offset(60f, 250f)), Offset(60f, 250f), escala) }
                        ObjetosDeco.grande(v, o, v.pincel(Offset(288f, 262f)), Offset(288f, 262f), Offset(280f, 204f), escala)?.pintar?.invoke(this)
                        ObjetosDeco.grande(v, o, v.pincel(Offset(80f, 270f)), Offset(80f, 270f), Offset(110f, 200f), escala)?.pintar?.invoke(this)
                    }
                } catch (x: Exception) {
                    throw AssertionError("$h con «$o» a escala $escala: $x", x)
                }
            }
        }
    }

    /**
     * El cuadro se dibuja con cada objeto a las tres horas de las capturas y a las 15:30 y las 4:30, en el club de Sully,
     * el día de la madre (con su cloché), en la gira y sin club (los lugares provisionales, con el piano bajo su funda), en
     * el club nuevo y con nieve.
     */
    @Test fun elCuadroSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val img = Canvas(ImageBitmap(ancho, alto))
        val vistos = HashSet<Lugar>()
        for (dia in listOf("2028-10-12", "2027-11-06", "2029-07-10", "2031-03-01", "2031-06-01", "2031-01-20")) for (h in listOf(7.0, 13.0, 15.5, 21.0, 4.5)) {
            val base = momento(dia, h); vistos += EscenaDeco.lugar(base)
            for (o in ObjetosDeco.TODOS) for (clima in listOf(base.ficha.clima, Clima.NIEVE)) {
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, img, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaDeco) { quieta(m) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
        assertEquals(setOf(Lugar.CLUB, Lugar.GIRA, Lugar.SIN_CLUB), vistos)
    }
}
