package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosBotanico
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
 * Los doce objetos del día del naturalista (`ObjetosBotanico`, tanda S13): cuáles son grandes (van de pie, apoyados) y
 * cuáles chicos (van sobre la mesa), que cada día del calendario el cuadro sabe cuál le toca, que los grandes devuelven
 * su silueta y su pie, y que los dos cuadros se dibujan con cada uno.
 */
class EscenaBotanicoObjetosPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "naturalista")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun pincel(m: Momento): Pincel { val l = EscenaBotanico.luces(m); return Pincel(l, Rayo(l)) }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosBotanico.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosBotanico.TODOS.size)
    }

    /** Cuatro grandes (la hamaca, la red, la escopeta y el cajón) y ocho chicos; cada día el cuadro sabe cuál le toca. */
    @Test fun cadaObjetoEsGrandeOChico() {
        assertEquals(setOf("hamaca", "red", "escopeta", "caja"), ObjetosBotanico.GRANDES)
        assertTrue(ObjetosBotanico.TODOS.containsAll(ObjetosBotanico.GRANDES))
        assertEquals(setOf("libreta", "lupa", "frasco", "martillo", "brujula", "carta", "limon", "galleta"), ObjetosBotanico.TODOS.filter { !ObjetosBotanico.esGrande(it) }.toSet())
        assertFalse(ObjetosBotanico.esGrande(null as String?))
        cadaDia { d, m ->
            val o = m.ficha.objeto ?: return@cadaDia
            assertEquals("$d: $o", o in ObjetosBotanico.GRANDES, ObjetosBotanico.esGrande(m))
        }
    }

    /**
     * Un grande devuelve su cosa con el pie que se le dio y una silueta que sube desde él (lo que pisa queda abajo y lo
     * apoyado, arriba); un chico, o ningún objeto, no devuelve nada. Lo mismo dos veces da lo mismo.
     */
    @Test fun losGrandesPisanDondeSeLesDiceYSeApoyan() {
        val m = momento("2027-03-21", 13.0); val p = pincel(m)
        val pie = Offset(197f, 266f); val apoyo = Offset(187f, 181f)
        for (o in ObjetosBotanico.TODOS + listOf(null)) {
            val g = ObjetosBotanico.grande(o, p, pie, apoyo, 1.56f)
            if (o == null || o !in ObjetosBotanico.GRANDES) { assertNull("$o", g); continue }
            assertNotNull(o, g); g!!
            assertEquals(o, pie, g.pie)
            val caja = g.forma.getBounds()
            assertTrue("$o: la silueta llega al suelo (${caja.bottom})", caja.bottom >= pie.y - 6f && caja.bottom <= pie.y + 6f)
            assertTrue("$o: la silueta sube por encima del apoyo o cerca (${caja.top})", caja.top < pie.y - 40f)
            assertTrue("$o: la silueta no se va de lado (${caja.left}..${caja.right})", caja.left > pie.x - 120f && caja.right < pie.x + 120f)
            val otra = ObjetosBotanico.grande(o, p, pie, apoyo, 1.56f)!!.forma.getBounds()
            assertEquals(o, caja, otra)
        }
        // Con el momento: lo que toca hoy.
        cadaDia { d, mm -> if (d.dayOfMonth == 1) assertEquals("$d", ObjetosBotanico.esGrande(mm), ObjetosBotanico.grande(mm, pincel(mm), pie, apoyo) != null) }
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** Cada objeto se dibuja suelto, a las escalas de los dos cuadros (1 en la galería, ~1.44 en la mesa de cubierta), de día y de noche y con una lumbre cerca. */
    @Test fun cadaObjetoSeDibujaSuelto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val lienzoImg = Canvas(ImageBitmap(380, 285))
        for (h in listOf(7.0, 13.0, 21.0)) {
            val m = momento("2027-03-21", h); val base = pincel(m)
            for (p in listOf(base, base.con(0.8f))) for (escala in listOf(1f, 1.44f)) for (o in ObjetosBotanico.TODOS) {
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(380f, 285f)) {
                        with(ObjetosBotanico) { chico(o, p, Offset(120f, 170f), escala) }
                        ObjetosBotanico.grande(o, p, Offset(197f, 266f), Offset(187f, 181f), escala)?.pintar?.invoke(this)
                        // Apoyado hacia el otro lado y a la izquierda del cuadro (el cajón enseña su otro costado).
                        ObjetosBotanico.grande(o, p, Offset(80f, 270f), Offset(110f, 190f), escala)?.pintar?.invoke(this)
                    }
                } catch (x: Exception) {
                    throw AssertionError("$h con «$o» a escala $escala: $x", x)
                }
            }
        }
    }

    /** Los dos cuadros (el bergantín y la selva) se dibujan con cada objeto a las tres horas de las capturas. */
    @Test fun losDosCuadrosSeDibujanConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val vistos = HashSet<Cuadro>()
        for (dia in listOf("2027-03-21", "2027-06-12")) for (h in listOf(7.0, 13.0, 21.0)) {
            val base = momento(dia, h); vistos += EscenaBotanico.cuadro(base)
            for (o in ObjetosBotanico.TODOS) {
                val m = Momento(base.ficha.copy(objeto = o), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaBotanico) { quieta(m) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o»: $x", x)
                }
            }
        }
        assertEquals(setOf(Cuadro.BARCO, Cuadro.SELVA), vistos)
    }
}
