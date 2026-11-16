package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.ChicosAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.EntornoObjeto
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada
import io.github.ruddy2003.vestigium.ui.escenas.LuzObjeto
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.PuertaAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.Sitio
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.V3
import io.github.ruddy2003.vestigium.ui.escenas.VistaAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
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
import kotlin.math.abs

/**
 * Los doce objetos del día de Brenna (`ObjetosAntiguas`, tanda S18): cuáles son grandes (de pie en las losas, junto al
 * banco del zaguán) y cuáles chicos (sobre el poyo), que ninguno sale dos veces (el bastón, la montura y la cuerda los
 * dibuja el contrato y el cuadro quita los suyos) y que todos los días el objeto está a la vista, que los grandes devuelven
 * su `Cosa`, que de noche llegan las dos luces, y que se dibujan sueltos y en el cuadro en todos sus estados y tiempos.
 */
class EscenaAntiguasObjetosPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "guia_caminos")!!, hora)
    private fun conObjeto(dia: String, hora: Double, o: String?, clima: Clima? = null) = momento(dia, hora).let { Momento(it.ficha.copy(objeto = o, clima = clima ?: it.ficha.clima), hora) }
    private fun cadaDia(hacer: (LocalDate, String) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, d.toString()); d = d.plusDays(1) } }

    /** Los mismos puntos y escalas con que los llama el cuadro (`CercaAntiguas.primerPlano`). */
    private val pie = FachadaPosada.p(1.82f, 0f, 0.46f)
    private val apoyo = FachadaPosada.p(1.82f, 1.0f, 0.02f)
    private val escalaGrande = FachadaPosada.escala(1.82f, 0.46f) / 48f
    private val sobre = PuertaAntiguas.SOBRE_EL_POYO
    private val escalaChico = FachadaPosada.escala(-0.62f, 0.25f) / 48f
    private fun pincelGrande(m: Momento): Pincel { val v = VistaAntiguas(m); return v.pincelEn(pie, lumbre = v.encendido.sala * v.noche * 0.5f) }
    private fun pincelChico(m: Momento): Pincel { val v = VistaAntiguas(m); return v.pincelEn(sobre, lumbre = v.encendido.sala * v.noche * 0.6f) }

    /** Un día de cada estado: la posada con el paso abierto, el paso cerrado, la travesía y la crianza. */
    private val ESTADOS = listOf("2027-05-05", "2027-01-15", "2029-07-30", "2031-04-20")

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, d -> momento(d).ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosAntiguas.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosAntiguas.TODOS.size)
    }

    /** Cinco grandes (el bastón, la cuerda, la capa, la montura y las botas) y siete chicos; cada día el cuadro sabe cuál le toca. */
    @Test fun cadaObjetoEsGrandeOChico() {
        assertEquals(setOf("baston", "cuerda", "capa", "montura", "botas"), ObjetosAntiguas.GRANDES)
        assertEquals(setOf("linterna", "cuerno", "libro", "mapa", "cecina", "jarra", "pedernal"), ObjetosAntiguas.TODOS.filter { !ObjetosAntiguas.esGrande(it) }.toSet())
        assertFalse(ObjetosAntiguas.esGrande(null as String?))
        cadaDia { d, dia -> val m = momento(dia); val o = m.ficha.objeto ?: return@cadaDia; assertEquals("$d: $o", o in ObjetosAntiguas.GRANDES, ObjetosAntiguas.esGrande(m)) }
    }

    /**
     * Ninguno sale dos veces y todos los días hay objeto a la vista, de 2026 a 2032, a las 7:00, las 13:00 y las 21:00: el
     * contrato dibuja las tres piezas del cuadro (el bastón, la montura, la cuerda) y el día que tocan el cuadro no pinta la
     * suya; el bastón de Brenna junto a la puerta no sale el día del bastón (tampoco a las 21:00, cuando vuelve del camino);
     * un grande da su `Cosa` y un chico no; y todos los días tienen objeto.
     */
    @Test fun ningunoSaleDosVecesYSiempreSeVe() {
        assertEquals(ObjetosAntiguas.YA_EN_EL_CUADRO, ObjetosAntiguas.DIBUJADOS)
        assertTrue(ObjetosAntiguas.GRANDES.containsAll(ObjetosAntiguas.DIBUJADOS))
        var dias = 0
        cadaDia { d, dia ->
            for (h in listOf(7.0, 13.0, 21.0)) {
                val m = momento(dia, h); val o = m.ficha.objeto
                assertNotNull("$d: un día sin objeto", o); o!!
                for (pieza in ObjetosAntiguas.YA_EN_EL_CUADRO) assertEquals("$d $h: $o y la pieza $pieza", o != pieza, ObjetosAntiguas.elCuadroPinta(m, pieza))
                // El bastón de Brenna junto a la puerta y la montura del caballete, con las condiciones del cuadro.
                val bastonDelCuadro = EscenaAntiguas.bastonEnLaPuerta(m) && o != "baston" && ObjetosAntiguas.elCuadroPinta(m, "baston")
                if (o == "baston") assertFalse("$d $h", bastonDelCuadro)
                val caballete = ObjetosAntiguas.elCuadroPinta(m, "montura") && o != "montura"
                assertEquals("$d $h", o != "montura", caballete)
                val g = ObjetosAntiguas.grande(m, pincelGrande(m), pie, apoyo, escalaGrande)
                assertEquals("$d $h: $o", o in ObjetosAntiguas.GRANDES, g != null)
            }
            dias++
        }
        assertTrue(dias > 2500)
    }

    /**
     * Cada grande devuelve su `Cosa`, en los cuatro estados y a tres horas, con el pie, el apoyo y la escala del cuadro: con
     * el pie que se le dio, la silueta dentro del lienzo, que llega al suelo, sube por el muro, queda a la derecha de la
     * cabeza de las mulas (no se mete bajo ella) y es la misma dos veces. Un chico, o ninguno, no devuelve nada.
     */
    @Test fun cadaGrandeDevuelveSuCosa() {
        for (dia in ESTADOS) for (h in listOf(7.0, 13.0, 21.0)) for (o in ObjetosAntiguas.TODOS + listOf<String?>(null)) {
            val m = conObjeto(dia, h, o); val p = pincelGrande(m)
            val g = ObjetosAntiguas.grande(m, p, pie, apoyo, escalaGrande)
            if (o == null || o !in ObjetosAntiguas.GRANDES) { assertNull("$dia $h $o", g); continue }
            assertNotNull("$dia $h $o", g); g!!
            assertEquals(o, pie, g.pie)
            val caja = g.forma.getBounds()
            assertTrue("$o: dentro del lienzo ($caja)", caja.left >= 0f && caja.right <= ANCHO && caja.top >= 0f && caja.bottom <= ALTO)
            assertTrue("$o: llega al suelo (${caja.bottom})", caja.bottom >= pie.y - 8f && caja.bottom <= pie.y + 14f)
            assertTrue("$o: sube (${caja.top})", caja.top < pie.y - 20f)
            assertTrue("$o: a la derecha de la cabeza de las mulas (${caja.left}..${caja.right})", caja.left >= pie.x - 25f && caja.right <= pie.x + 60f)
            assertEquals(o, caja, ObjetosAntiguas.grande(m, p, pie, apoyo, escalaGrande)!!.forma.getBounds())
        }
        // El bastón es lo más alto: «de su altura y un palmo más», pasa del apoyo.
        val b = ObjetosAntiguas.grande(conObjeto("2027-05-05", 13.0, "baston"), pincelGrande(momento("2027-05-05")), pie, apoyo, escalaGrande)!!
        assertTrue(b.forma.getBounds().top < apoyo.y - 30f)
    }

    /**
     * De noche, a dos luces: a las 21:00 con la sala encendida, la cara de una cosa que mira a la puerta es más cálida que
     * la que le da la espalda, y con luna, la que mira a la luna es más fría que la de la lumbre. La linterna de mina se
     * enciende de noche y con niebla, y no de día.
     */
    @Test fun deNocheDosLucesYLaLinternaEncendida() {
        val m = momento("2027-05-05", 21.0); val p = pincelGrande(m)
        assertTrue("la lumbre llega a los grandes", p.calida > 0.05f)
        val l = LuzObjeto(Sitio(SitioAntiguas.MIRADA, pie, escalaGrande * 48f, LuzObjeto.GIRO_MURO), p, V3(0.79f, 0.15f, -0.6f), EntornoObjeto())
        val color = io.github.ruddy2003.vestigium.ui.escenas.ColorAntiguas.LANA
        val calida = l.cw(color, LuzObjeto.unidad(V3(0.79f, 0.15f, -0.6f))); val fria = l.cw(color, LuzObjeto.unidad(V3(-0.79f, 0.15f, -0.6f)))
        assertTrue("del lado de la puerta, más cálido ($calida / $fria)", calida.red / calida.blue.coerceAtLeast(0.01f) > fria.red / fria.blue.coerceAtLeast(0.01f) + 0.05f)
        assertTrue(ChicosAntiguas.encendida(VistaAntiguas(m).noche, EntornoObjeto()) > 0.9f)
        assertTrue(ChicosAntiguas.encendida(VistaAntiguas(momento("2027-05-05", 13.0)).noche, EntornoObjeto()) < 0.05f)
        assertTrue(ChicosAntiguas.encendida(VistaAntiguas(momento("2027-05-05", 13.0)).noche, EntornoObjeto(niebla = true)) > 0.9f)
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    private fun cajaDelChico(m: Momento): IntArray? {
        val img = ImageBitmap(ANCHO.toInt(), ALTO.toInt())
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ANCHO, ALTO)) {
            with(ObjetosAntiguas) { sobreElSoporte(m, pincelChico(m), sobre, escalaChico) }
        }
        val px = img.toPixelMap()
        var x0 = Int.MAX_VALUE; var y0 = Int.MAX_VALUE; var x1 = -1; var y1 = -1
        for (y in 0 until px.height) for (x in 0 until px.width) if (px[x, y].alpha > 0.3f) { x0 = minOf(x0, x); y0 = minOf(y0, y); x1 = maxOf(x1, x); y1 = maxOf(y1, y) }
        return if (x1 < 0) null else intArrayOf(x0, y0, x1, y1)
    }

    /** El poyo solo lleva lo que toca: un chico se pinta a la vista, sobre la losa y de un tamaño que se lee; un grande o ningún objeto, nada. */
    @Test fun elPoyoSoloLlevaLoQueToca() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        for (dia in listOf("2027-05-05", "2027-01-15")) for (o in ObjetosAntiguas.TODOS + listOf<String?>(null)) for (h in listOf(13.0, 21.0)) {
            val caja = cajaDelChico(conObjeto(dia, h, o))
            if (o == null || o in ObjetosAntiguas.GRANDES) { assertNull("$dia $o $h", caja); continue }
            assertNotNull("$dia $o $h: no se pinta", caja); caja!!
            val ancho = caja[2] - caja[0]; val alto = caja[3] - caja[1]
            assertTrue("$o $h: es muy chico para leerse ($ancho × $alto)", ancho >= 14 && alto >= 12 && ancho * alto >= 350)
            assertTrue("$o $h: no está en el poyo (${caja.toList()})", abs((caja[0] + caja[2]) / 2f - sobre.x) < 25f && caja[1] < sobre.y - 10f && caja[3] > sobre.y - 4f && caja[0] >= 325)
        }
    }

    /** Cada objeto se dibuja suelto: en los cuatro estados, a cuatro horas, con y sin nieve, nevando y con niebla, con y sin lumbre, a dos escalas, en su sitio y en otro. */
    @Test fun cadaObjetoSeDibujaSuelto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val lienzoImg = Canvas(ImageBitmap(380, 285))
        val entornos = listOf(EntornoObjeto(), EntornoObjeto(nieve = 1f, nevando = true, dia = 7), EntornoObjeto(nieve = 0.4f, niebla = true, dia = 999))
        for (dia in ESTADOS) for (h in listOf(3.0, 7.0, 13.0, 21.0)) {
            val m = momento(dia, h)
            for (p in listOf(pincelChico(m), pincelGrande(m).con(0.8f))) for (e in entornos) for (escala in listOf(1f, 1.3f)) for (o in ObjetosAntiguas.TODOS) {
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(380f, 285f)) {
                        with(ObjetosAntiguas) { chico(o, p, sobre, escala * escalaChico, e) }
                        ObjetosAntiguas.grande(o, p, pie, apoyo, escala * escalaGrande, e)?.pintar?.invoke(this)
                        // En otro sitio y con otro apoyo (el muro no sale de él: se toma el del cuadro).
                        with(ObjetosAntiguas) { chico(o, p, Offset(120f, 230f), escala, e) }
                        ObjetosAntiguas.grande(o, p, Offset(80f, 270f), Offset(110f, 190f), escala, e)?.pintar?.invoke(this)
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» a escala $escala: $x", x)
                }
            }
        }
    }

    /**
     * El cuadro entero (la parte quieta y la viva) se dibuja con cada objeto: en los cuatro estados, con el paso cerrado
     * nevando, en los días del cuento que tocan a la puerta (el año nuevo, las linternas, la noche del arpa, el alud, el
     * nacimiento) y a las 7:00, las 13:00 y las 21:00, y con las mulas en la argolla.
     */
    @Test fun elCuadroSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val lugares = HashSet<Lugar>()
        for (dia in ESTADOS + listOf("2027-01-01", "2027-02-03", "2027-12-21", "2028-02-25", "2031-04-12", "2027-11-10")) for (h in listOf(7.0, 13.0, 21.0)) {
            val base = momento(dia, h); lugares += EscenaAntiguas.lugar(base)
            for (o in ObjetosAntiguas.TODOS) for (clima in listOf(base.ficha.clima, Clima.NIEVE)) {
                if (clima == Clima.NIEVE && dia != "2027-01-15" && dia != "2027-05-05") continue
                val m = conObjeto(dia, h, o, clima)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaAntiguas) { quieta(m) } }
                        lienzo { with(EscenaAntiguas) { viva(m, 2.3f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
        assertEquals(Lugar.entries.toSet(), lugares)
    }
}
