package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.BarcoAbismo
import io.github.ruddy2003.vestigium.ui.escenas.ColorAbismo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Flota
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.MuelleAbismo
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
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
 * Los doce objetos del día de Nikos (`ObjetosAbismo`, tanda S16): que son los del calendario, cuáles son grandes, que
 * ninguno sale dos veces (el cuadro quita el suyo, o el objeto ya es una pieza que se ve y no se pinta otro), que los
 * grandes devuelven su cosa con la sombra en el lienzo, y que los dos cuadros se dibujan con cada uno, de día y de noche.
 */
class EscenaAbismoObjetosPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "buzo_esponjas")!!, hora)
    private fun con(m: Momento, objeto: String?) = Momento(m.ficha.copy(objeto = objeto), m.hora)
    private fun cadaDia(hora: Double = 13.0, hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString(), hora)); d = d.plusDays(1) } }

    /** El pincel de la luz de fuera de ese momento (el sol, o la luna); con `con(…)`, el de una lumbre cerca. */
    @Suppress("UNUSED_PARAMETER")
    private fun pincel(m: Momento, en: Offset): Pincel { val l = EscenaAbismo.luces(m); return Pincel(l, EscenaAbismo.rayo(l), 0f, ColorAbismo.LUMBRE) }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosAbismo.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosAbismo.TODOS.size)
    }

    /** Cuatro grandes (el traje, las botas, la cuerda y la red); las listas del contrato, dentro de los doce y sin cruzarse. */
    @Test fun lasListasDelContrato() {
        assertEquals(setOf("escafandra", "botas", "cuerda", "red"), ObjetosAbismo.GRANDES)
        assertEquals(setOf("bomba", "icono", "barco"), ObjetosAbismo.EN_EL_CUADRO)
        assertEquals(setOf("casco", "escafandra", "botas"), ObjetosAbismo.QUITA_DEL_CUADRO)
        assertTrue(ObjetosAbismo.TODOS.containsAll(ObjetosAbismo.GRANDES + ObjetosAbismo.EN_EL_CUADRO + ObjetosAbismo.QUITA_DEL_CUADRO))
        assertTrue((ObjetosAbismo.EN_EL_CUADRO intersect (ObjetosAbismo.GRANDES + ObjetosAbismo.QUITA_DEL_CUADRO)).isEmpty())
        assertFalse(ObjetosAbismo.esGrande(null as String?))
        cadaDia { d, m -> val o = m.ficha.objeto ?: return@cadaDia; assertEquals("$d: $o", o in ObjetosAbismo.GRANDES, ObjetosAbismo.esGrande(m)) }
    }

    /**
     * Ninguno sale dos veces. El día del casco, del traje o de las botas, el cuadro no pinta el suyo; la bomba solo es pieza
     * del barco (el muelle no la tiene: ese día la pinta el contrato, desarmada); el icono y el barco, donde está el barco de
     * Nikos (en el cuarto de 2030, ninguno). Lo vigila cada día de 2026 a 2032.
     */
    @Test fun ningunoSaleDosVeces() {
        for (pieza in ObjetosAbismo.QUITA_DEL_CUADRO) for (otra in ObjetosAbismo.TODOS) {
            val m = con(momento("2027-04-03"), otra)
            assertEquals("$pieza el día de $otra", otra != pieza, ObjetosAbismo.elCuadroPinta(m, pieza))
        }
        assertTrue(ObjetosAbismo.estaEnElCuadro("bomba", Cuadro.BARCO, null)); assertFalse(ObjetosAbismo.estaEnElCuadro("bomba", Cuadro.MUELLE, Flota.LLENO))
        for (o in listOf("icono", "barco")) {
            assertTrue(ObjetosAbismo.estaEnElCuadro(o, Cuadro.BARCO, null))
            for (f in Flota.entries) assertEquals("$o $f", f != Flota.VACIO, ObjetosAbismo.estaEnElCuadro(o, Cuadro.MUELLE, f))
        }
        for (o in ObjetosAbismo.TODOS - ObjetosAbismo.EN_EL_CUADRO) for (c in Cuadro.entries) assertFalse(o, ObjetosAbismo.estaEnElCuadro(o, c, null))
        val pie = Offset(150f, 260f); val apoyo = Offset(160f, 200f)
        cadaDia { d, m ->
            val o = m.ficha.objeto ?: return@cadaDia
            val p = pincel(m, pie)
            val g = ObjetosAbismo.grande(m, p, pie, apoyo, 1.3f)
            val esperado = o in ObjetosAbismo.GRANDES || (o == "bomba" && !ObjetosAbismo.estaEnElCuadro(m))
            assertEquals("$d: el grande de «$o»", esperado, g != null)
            if (ObjetosAbismo.estaEnElCuadro(m)) assertNull("$d: «$o» ya está en el cuadro", g)
        }
    }

    /**
     * Cada grande (y la rueda de la bomba, en el muelle) devuelve su cosa con el pie que se le dio, y su forma es su sombra
     * en el suelo: cae en el lienzo y nace al pie de la cosa. Lo mismo dos veces da lo mismo.
     */
    @Test fun losGrandesDevuelvenSuCosaYSuSombraCaeEnElLienzo() {
        // Donde los ponen los dos cuadros (si su pie cae en el lienzo) y dos sitios como esos, por si los cuadros se mueven.
        val sitios = listOf(Triple("2027-04-03", Offset(97f, 275f) to Offset(103f, 204f), 1.36f), Triple("2027-01-12", Offset(232f, 250f) to Offset(256f, 214f), 1.4f)) +
            listOf(Triple("2027-04-03", BarcoAbismo.PIE_GRANDE to BarcoAbismo.APOYO_GRANDE, 1.36f), Triple("2027-01-12", MuelleAbismo.PIE_GRANDE to MuelleAbismo.APOYO_GRANDE, 1.4f)).filter { it.second.first.y <= ALTO }
        for ((dia, sitio, escala) in sitios) for (h in listOf(7.0, 13.0, 17.0, 21.0)) {
            val (pie, apoyo) = sitio
            val base = momento(dia, h); val tierra = EscenaAbismo.cuadro(base) == Cuadro.MUELLE
            for (o in ObjetosAbismo.TODOS) {
                val m = con(base, o); val p = pincel(m, pie)
                val g = ObjetosAbismo.grande(m, p, pie, apoyo, escala)
                if (o !in ObjetosAbismo.GRANDES && !(o == "bomba" && tierra)) { assertNull("$dia $h $o", g); continue }
                assertNotNull("$dia $h $o", g); g!!
                assertEquals("$dia $h $o", pie, g.pie)
                val caja = g.forma.getBounds()
                assertFalse("$dia $h $o: la sombra está vacía", caja.isEmpty)
                assertTrue("$dia $h $o: la sombra cae en el lienzo ($caja)", caja.center.x in 0f..ANCHO && caja.center.y in 0f..ALTO + 20f && caja.right > 0f && caja.left < ANCHO && caja.top < ALTO)
                assertTrue("$dia $h $o: la sombra nace al pie ($caja, pie $pie)", caja.bottom >= pie.y - 0.4f * 60f * escala && caja.bottom <= pie.y + 0.25f * 60f * escala)
                assertTrue("$dia $h $o: la sombra no se va lejos ($caja)", caja.width < 2.2f * 60f * escala && caja.height < 1.6f * 60f * escala)
                assertEquals("$dia $h $o", caja, ObjetosAbismo.grande(m, p, pie, apoyo, escala)!!.forma.getBounds())
            }
        }
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /**
     * El soporte: un día de chico se pinta algo encima; un día de grande, o de algo que ya está en el cuadro, el soporte
     * queda como estaba (no sale dos veces). En el muelle, la bomba (desarmada) sí se pinta; en el cuarto, también el icono.
     */
    @Test fun elSoporteSoloLlevaLoQueToca() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val sobre = Offset(190f, 150f)
        for ((dia, h) in listOf("2027-04-03" to 13.0, "2027-01-12" to 13.0, "2030-06-20" to 13.0, "2027-04-03" to 21.0)) for (o in ObjetosAbismo.TODOS + listOf(null)) {
            val m = con(momento(dia, h), o)
            val img = ImageBitmap(380, 285)
            CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(380f, 285f)) { with(ObjetosAbismo) { sobreElSoporte(m, pincel(m, sobre), sobre, 1.1f) } }
            val px = img.toPixelMap(); var pintado = 0
            for (x in 0 until 380 step 2) for (y in 0 until 285 step 2) if (px[x, y].alpha > 0.05f) pintado++
            val toca = o != null && o !in ObjetosAbismo.GRANDES && !ObjetosAbismo.estaEnElCuadro(m) && o != "barco"
            assertEquals("$dia $h «$o»: ${if (toca) "no se pintó" else "se pintó y no tocaba"} ($pintado)", toca, pintado > 20)
        }
    }

    /** Cada objeto se dibuja suelto, a las escalas de los dos cuadros, de día y de noche y con una lumbre cerca. */
    @Test fun cadaObjetoSeDibujaSuelto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val lienzoImg = Canvas(ImageBitmap(380, 285))
        for (dia in listOf("2027-04-03", "2027-01-12")) for (h in listOf(7.0, 13.0, 21.0)) {
            val m = momento(dia, h); val base = pincel(m, Offset(190f, 200f))
            for (p in listOf(base, base.con(0.8f))) for (escala in listOf(0.8f, 1.1f, 1.9f)) for (o in ObjetosAbismo.TODOS) for (tierra in listOf(false, true)) {
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(380f, 285f)) {
                        with(ObjetosAbismo) { chico(o, p, Offset(120f, 170f), escala, if (tierra) 0f else 1f) }
                        ObjetosAbismo.grande(o, p, Offset(150f, 270f), Offset(160f, 200f), escala, tierra, 0.8f)?.pintar?.invoke(this)
                        ObjetosAbismo.grande(o, p, Offset(300f, 250f), Offset(280f, 210f), escala, tierra, 0f)?.pintar?.invoke(this)
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» a escala $escala: $x", x)
                }
            }
        }
    }

    /**
     * Los dos cuadros (el barco y el muelle, y el cuarto de 2030 con el muelle vacío) se dibujan enteros, la parte quieta y
     * la viva, con cada objeto, a la hora de bajar, a la de las esponjas, a la del corro con el farol y de noche.
     */
    @Test fun losDosCuadrosSeDibujanConCadaObjetoDeDiaYDeNoche() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val vistos = HashSet<Cuadro>()
        for (dia in listOf("2027-04-03", "2027-01-12", "2030-06-20")) for (h in listOf(7.0, 13.0, 19.0, 21.0)) {
            val base = momento(dia, h); vistos += EscenaAbismo.cuadro(base)
            for (o in ObjetosAbismo.TODOS) {
                val m = con(base, o)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaAbismo) { quieta(m) } }
                        lienzo { with(EscenaAbismo) { viva(m, 2.3f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o»: $x", x)
                }
            }
        }
        assertEquals(setOf(Cuadro.BARCO, Cuadro.MUELLE), vistos)
    }
}
