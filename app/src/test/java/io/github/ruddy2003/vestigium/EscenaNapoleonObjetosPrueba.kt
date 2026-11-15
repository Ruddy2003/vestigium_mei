package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
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
 * Los doce objetos del día de Jean-Baptiste (`ObjetosNapoleon`, tanda S14): cuáles son grandes (van de pie, junto al
 * pabellón) y cuáles chicos (van sobre la mochila de pie), que cada día del calendario el cuadro sabe cuál le toca, que los
 * grandes devuelven su silueta y su pie, y que los dos cuadros se dibujan con cada uno.
 */
class EscenaNapoleonObjetosPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "granadero")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun pincel(m: Momento): Pincel { val l = EscenaNapoleon.luces(m); return Pincel(l, Rayo(l)) }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosNapoleon.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosNapoleon.TODOS.size)
    }

    /** Cinco grandes (el fusil, la mochila, el capote, las botas y la manta) y siete chicos; cada día el cuadro sabe cuál le toca. */
    @Test fun cadaObjetoEsGrandeOChico() {
        assertEquals(setOf("fusil", "mochila", "capote", "botas", "manta"), ObjetosNapoleon.GRANDES)
        assertTrue(ObjetosNapoleon.TODOS.containsAll(ObjetosNapoleon.GRANDES))
        assertEquals(setOf("bayoneta", "chaco", "cantimplora", "galleta", "pipa", "carta", "cuchara"), ObjetosNapoleon.TODOS.filter { !ObjetosNapoleon.esGrande(it) }.toSet())
        assertFalse(ObjetosNapoleon.esGrande(null as String?))
        cadaDia { d, m ->
            val o = m.ficha.objeto ?: return@cadaDia
            assertEquals("$d: $o", o in ObjetosNapoleon.GRANDES, ObjetosNapoleon.esGrande(m))
        }
    }

    /**
     * Un grande devuelve su cosa con el pie que se le dio y una silueta que sube desde él; un chico, o ningún objeto, no
     * devuelve nada. Lo mismo dos veces da lo mismo. Son los puntos y la escala con que lo llama la retirada.
     */
    @Test fun losGrandesPisanDondeSeLesDiceYSeApoyan() {
        val m = momento("2027-11-21", 13.0); val p = pincel(m)
        val pie = Offset(282f, 252f); val apoyo = Offset(270f, 176f)
        for (o in ObjetosNapoleon.TODOS + listOf(null)) {
            val g = ObjetosNapoleon.grande(o, p, pie, apoyo, 1.1f)
            if (o == null || o !in ObjetosNapoleon.GRANDES) { assertNull("$o", g); continue }
            assertNotNull(o, g); g!!
            assertEquals(o, pie, g.pie)
            val caja = g.forma.getBounds()
            assertTrue("$o: la silueta llega al suelo (${caja.bottom})", caja.bottom >= pie.y - 6f && caja.bottom <= pie.y + 8f)
            assertTrue("$o: la silueta sube (${caja.top})", caja.top < pie.y - 25f)
            assertTrue("$o: la silueta no se va de lado (${caja.left}..${caja.right})", caja.left > pie.x - 70f && caja.right < pie.x + 60f)
            assertEquals(o, caja, ObjetosNapoleon.grande(o, p, pie, apoyo, 1.1f)!!.forma.getBounds())
        }
        // El fusil y la horquilla de la ropa son lo más alto: pasan del apoyo.
        for (o in listOf("fusil", "capote")) assertTrue(o, ObjetosNapoleon.grande(o, p, pie, apoyo, 1.1f)!!.forma.getBounds().top < apoyo.y)
        // Con el momento: lo que toca hoy.
        cadaDia { d, mm -> if (d.dayOfMonth == 1) assertEquals("$d", ObjetosNapoleon.esGrande(mm), ObjetosNapoleon.grande(mm, pincel(mm), pie, apoyo) != null) }
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** Cada objeto se dibuja suelto, a dos escalas, de día y de noche, con una lumbre cerca, con nieve y sin ella, y apoyado a los dos lados. */
    @Test fun cadaObjetoSeDibujaSuelto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val lienzoImg = Canvas(ImageBitmap(380, 285))
        for (h in listOf(7.0, 13.0, 21.0)) {
            val m = momento("2027-11-21", h); val base = pincel(m)
            for (p in listOf(base, base.con(0.8f))) for (escala in listOf(1f, 1.1f)) for (nieve in listOf(0f, 0.9f)) for (o in ObjetosNapoleon.TODOS) {
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(380f, 285f)) {
                        with(ObjetosNapoleon) { chico(o, p, Offset(236f, 225f), escala, nieve) }
                        ObjetosNapoleon.grande(o, p, Offset(282f, 252f), Offset(270f, 176f), escala, nieve)?.pintar?.invoke(this)
                        // Lejos del fuego (en la nieve, no en el barro) y apoyado hacia el otro lado.
                        ObjetosNapoleon.grande(o, p, Offset(80f, 270f), Offset(110f, 190f), escala, nieve)?.pintar?.invoke(this)
                    }
                } catch (x: Exception) {
                    throw AssertionError("$h con «$o» a escala $escala y nieve $nieve: $x", x)
                }
            }
        }
    }

    /** Los dos cuadros (la retirada y el vivac) se dibujan con cada objeto a las tres horas de las capturas, con nieve y sin ella. */
    @Test fun losDosCuadrosSeDibujanConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val vistos = HashSet<Cuadro>()
        for (dia in listOf("2027-10-22", "2027-11-21", "2027-07-10")) for (h in listOf(7.0, 13.0, 21.0)) {
            val base = momento(dia, h); vistos += EscenaNapoleon.cuadro(base)
            for (o in ObjetosNapoleon.TODOS) for (clima in listOf(base.ficha.clima, Clima.NIEVE)) {
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaNapoleon) { quieta(m) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
        assertEquals(setOf(Cuadro.VIVAC, Cuadro.RETIRADA), vistos)
    }
}
