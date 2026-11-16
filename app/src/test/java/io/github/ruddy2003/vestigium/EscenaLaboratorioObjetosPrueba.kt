package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.CamLab
import io.github.ruddy2003.vestigium.ui.escenas.CosasLab
import io.github.ruddy2003.vestigium.ui.escenas.CuadroLab
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.MesaLab
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosLaboratorio
import io.github.ruddy2003.vestigium.ui.escenas.SalaLab
import io.github.ruddy2003.vestigium.ui.escenas.SitioObjeto
import io.github.ruddy2003.vestigium.ui.escenas.V3
import io.github.ruddy2003.vestigium.ui.escenas.VistaLab
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate

/**
 * Los doce objetos del día de la doctora (`ObjetosLaboratorio`, tanda S17): cuáles son grandes y cuáles chicos, que la sala
 * no pinta su pieza el día en que esa pieza es el objeto (para que ninguno salga dos veces), que cada grande devuelve su
 * cosa, que la luz de cada objeto es la de las lámparas del laboratorio (y la de otro cuadro, si es otro sitio), y que cada
 * uno se dibuja de día y de noche, en cada estado, en el laboratorio y en un sitio cualquiera.
 */
class EscenaLaboratorioObjetosPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "cientifica")!!, hora)
    private fun conObjeto(dia: String, hora: Double, o: String?) = Momento(FichaDelDia.de(dia, "cientifica")!!.copy(objeto = o), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun pincel(m: Momento): Pincel { val l = EscenaLaboratorio.luces(m); return Pincel(l, Rayo(l)) }

    /** Los días de muestra: el laboratorio de siempre, la cuarentena, la campana, el cierre, arriba, el apagón y la noche larga. */
    private val dias = listOf("2027-05-05", "2028-03-20", "2029-05-12", "2030-10-05", "2031-05-10", "2027-07-08", "2027-12-21", "2032-04-01")
    private val horas = listOf(2.0, 7.0, 10.0, 13.0, 17.5, 21.5)

    /** El pie y el apoyo del grande que da el cuadro del laboratorio ese momento. */
    private fun apoyo(m: Momento) = CamLab.p(CosasLab.taburete(m) + V3(0f, 0.62f, 0f))
    private val pie = CamLab.p(CuadroLab.PIE_GRANDE)
    private val escalaPie = CamLab.k(CuadroLab.PIE_GRANDE) / ObjetosLaboratorio.METRO
    private val claro = CamLab.p(MesaLab.CLARO)
    private val escalaClaro = CamLab.k(MesaLab.CLARO) / ObjetosLaboratorio.METRO

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosLaboratorio.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosLaboratorio.TODOS.size)
    }

    /** Tres grandes (la bata, los guantes y la maceta) y nueve chicos; cada día el cuadro sabe cuál le toca. */
    @Test fun cadaObjetoEsGrandeOChico() {
        assertEquals(setOf("bata", "maceta", "guantes"), ObjetosLaboratorio.GRANDES)
        assertEquals(setOf("tableta", "pipeta", "microscopio", "semilla", "tarjeta", "auriculares", "cuaderno", "piedra", "taza"), ObjetosLaboratorio.TODOS.filter { !ObjetosLaboratorio.esGrande(it) }.toSet())
        assertFalse(ObjetosLaboratorio.esGrande(null as String?))
        cadaDia { d, m -> val o = m.ficha.objeto ?: return@cadaDia; assertEquals("$d: $o", o in ObjetosLaboratorio.GRANDES, ObjetosLaboratorio.esGrande(m)) }
    }

    /**
     * Ninguno sale dos veces: las piezas de la sala que también son objetos las dibuja el contrato el día que tocan, y ese día
     * la sala no pinta la suya; los demás días, sí. La tarjeta y la piedra no están en la sala, y la maceta del día es otra que
     * las del estante.
     */
    @Test fun ningunoSaleDosVeces() {
        assertEquals(setOf("microscopio", "taza", "tableta", "cuaderno", "semilla", "pipeta", "bata", "guantes", "auriculares"), ObjetosLaboratorio.DE_LA_SALA)
        assertEquals(ObjetosLaboratorio.DE_LA_SALA, ObjetosLaboratorio.DIBUJADOS)
        for (o in listOf("tarjeta", "piedra", "maceta")) assertFalse(o, o in ObjetosLaboratorio.DE_LA_SALA)
        cadaDia { d, m -> for (p in ObjetosLaboratorio.DE_LA_SALA) assertEquals("$d $p", m.ficha.objeto != p, SalaLab.laSalaPinta(m, p)) }
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        // Y la sala de verdad lo quita: la mesa (a las 10:00, trabajando) y la pared con la percha (a las 7:00, con la bata
        // colgada) cambian el día de su pieza respecto de un día con otro objeto (la piedra, que no es de la sala).
        for ((pieza, hora) in listOf("microscopio" to 10.0, "taza" to 10.0, "tableta" to 10.0, "cuaderno" to 10.0, "semilla" to 10.0, "auriculares" to 10.0)) {
            val con = pintado { with(MesaLab) { pintar(VistaLab(conObjeto("2027-05-05", hora, pieza))) } }
            val sin = pintado { with(MesaLab) { pintar(VistaLab(conObjeto("2027-05-05", hora, "piedra"))) } }
            assertFalse("la mesa no quita «$pieza»", con.contentEquals(sin))
        }
        for (pieza in listOf("bata", "guantes")) {
            val con = pintado { with(SalaLab) { paredDelFondo(VistaLab(conObjeto("2027-05-05", 7.0, pieza))) } }
            val sin = pintado { with(SalaLab) { paredDelFondo(VistaLab(conObjeto("2027-05-05", 7.0, "piedra"))) } }
            assertFalse("la percha no quita «$pieza»", con.contentEquals(sin))
        }
    }

    /**
     * Cada grande devuelve su cosa con el pie que se le dio y una silueta que sube desde el suelo (la bata y los guantes,
     * desde el taburete; la maceta, desde el suelo), en el laboratorio a todas horas y en cada estado, y en un sitio
     * cualquiera; un chico, o ninguno, nada.
     */
    @Test fun cadaGrandeDevuelveSuCosa() {
        for (dia in dias) for (h in horas) for (o in ObjetosLaboratorio.TODOS + listOf(null)) {
            val m = conObjeto(dia, h, o); val v = VistaLab(m)
            val g = ObjetosLaboratorio.grande(m, v.pincel(CuadroLab.PIE_GRANDE), pie, apoyo(m), escalaPie)
            val otro = ObjetosLaboratorio.grande(m, pincel(m), Offset(120f, 250f), Offset(110f, 200f), 0.8f)
            if (o == null || o !in ObjetosLaboratorio.GRANDES) { assertNull("$dia $h $o", g); assertNull("$dia $h $o", otro); continue }
            assertNotNull("$dia $h $o", g); g!!; assertNotNull("$dia $h $o", otro)
            assertEquals(o, pie, g.pie); assertEquals(o, Offset(120f, 250f), otro!!.pie)
            val caja = g.forma.getBounds(); val a = apoyo(m)
            if (o == "maceta") {
                assertTrue("$dia $h $o: la maceta pisa el suelo (${caja.bottom})", caja.bottom > pie.y - 15f && caja.bottom < pie.y + 25f)
                assertTrue("$dia $h $o: y sube con su grama (${caja.top})", caja.top < pie.y - 40f)
            } else {
                assertTrue("$dia $h $o: arranca del asiento (${caja.top}, ${a.y})", caja.top < a.y + 12f && caja.top > a.y - 40f)
                assertTrue("$dia $h $o: y cuelga hacia el suelo (${caja.bottom})", caja.bottom > a.y + 25f && caja.bottom < pie.y + 30f)
            }
            assertTrue("$dia $h $o: está junto al taburete (${caja.left}..${caja.right})", caja.left > 120f && caja.right < 320f)
            assertEquals(o, caja, ObjetosLaboratorio.grande(m, v.pincel(CuadroLab.PIE_GRANDE), pie, apoyo(m), escalaPie)!!.forma.getBounds())
        }
    }

    /**
     * La luz de cada objeto es la del laboratorio: en el claro de la mesa y en el pie del taburete, la cuenta de la sala
     * (`VistaLab.c`), más la de las lámparas con pantalla que alumbran de verdad lo que tienen debajo (la sala mira su cono
     * al revés: ver `SitioObjeto.delLaboratorio`); lo que no mira a ninguna lámpara queda igual que en la sala. De noche, la
     * cara que mira a la lámpara de su mesa es más cálida que la que mira a la puerta del violeta; de día la lámpara de su
     * mesa tumba la sombra del objeto, y de noche, en el suelo, la pantalla. En otro sitio, la luz se arma con el pincel.
     */
    @Test fun laLuzEsLaDelCuadro() {
        for (h in listOf(7.0, 10.0, 13.0, 21.5)) {
            val m = conObjeto("2027-05-05", h, "taza"); val v = VistaLab(m)
            val s = ObjetosLaboratorio.sitioDelClaro(m, pincel(m), claro, escalaClaro)
            assertEquals(MesaLab.CLARO, s.o)
            for (n in listOf(V3(0f, -1f, 0f), V3(0.2f, -1f, 0.3f))) assertEquals("$h $n", v.c(MesaLab.CLARO, Color.White, n), s.color(MesaLab.CLARO, Color.White, n))
            for (n in listOf(V3(0f, 1f, 0f), V3(1f, 0f, -0.3f), V3(-1f, 0.2f, 0.4f))) { val a = v.c(MesaLab.CLARO, Color.White, n); val b = s.color(MesaLab.CLARO, Color.White, n)
                assertTrue("$h $n: $a $b", b.red >= a.red - 1e-4f && b.green >= a.green - 1e-4f && b.blue >= a.blue - 1e-4f) }
            val g = ObjetosLaboratorio.sitioDelPie(conObjeto("2027-05-05", h, "bata"), pincel(m), pie, apoyo(m), escalaPie)
            assertEquals(CuadroLab.PIE_GRANDE, g.o); assertNotNull(g.apoyo)
        }
        val noche = conObjeto("2027-05-05", 21.5, "taza")
        val s = ObjetosLaboratorio.sitioDelClaro(noche, pincel(noche), claro, escalaClaro)
        val aLampara = s.color(MesaLab.CLARO, Color.White, MesaLab.LAMPARA_MESA - MesaLab.CLARO)
        val aPuerta = s.color(MesaLab.CLARO, Color.White, V3(-0.22f, 1.0f, SalaLab.ZF - 0.25f) - MesaLab.CLARO)
        assertTrue("la lámpara calienta su lado ($aLampara, $aPuerta)", aLampara.red - aLampara.blue > aPuerta.red - aPuerta.blue + 0.05f)
        assertTrue("de día, la lámpara de su mesa tumba la sombra del objeto", ObjetosLaboratorio.sitioDelClaro(momento("2027-05-05", 10.0), pincel(noche), claro, escalaClaro).sombras.isNotEmpty())
        val suelo = ObjetosLaboratorio.sitioDelPie(conObjeto("2027-05-05", 21.5, "maceta"), pincel(noche), pie, apoyo(noche), escalaPie)
        assertTrue("de noche, en el suelo tumba sombra la pantalla (${suelo.sombras.size})", suelo.sombras.isNotEmpty())
        // En otro sitio, otra cámara y la luz del pincel.
        val otro = ObjetosLaboratorio.sitioDelClaro(noche, pincel(noche), Offset(80f, 200f), 1f)
        assertNotEquals(V3(0f, CamLab.OJO, 0f), otro.ojo)
        assertEquals(Offset(80f, 200f), otro.alLienzo(otro.o))
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** Lo que deja [dibujar] en un lienzo transparente de 380 × 285, como píxeles. */
    private fun pintado(dibujar: DrawScope.() -> Unit): IntArray {
        val img = ImageBitmap(ANCHO.toInt(), ALTO.toInt())
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ANCHO, ALTO)) { dibujar() }
        return img.toPixelMap().buffer
    }

    /** Un sitio de otro cuadro, como el del centro del Claustro: otra cámara (más alta, que mira hacia abajo) y otra luz. */
    private fun sitioDeOtroCuadro(apoyo: Boolean): SitioObjeto {
        val ojo = V3(-1.2f, 2.6f, -1.4f)
        val camara: (V3) -> Offset = { w -> val d = w - ojo; val z = (d.x * 0.45f + d.z * 0.8f - d.y * 0.4f).coerceAtLeast(0.1f); Offset(190f + 260f * (d.x * 0.85f - d.z * 0.5f) / z, 120f - 260f * (d.y * 0.9f + (d.x * 0.45f + d.z * 0.8f) * 0.3f) / z) }
        val luz: (V3, Color, V3) -> Color = { _, c, n -> val k = (0.5f + 0.4f * n.y.coerceIn(-1f, 1f) + 0.1f * n.x.coerceIn(-1f, 1f)).coerceIn(0f, 1f); Color(c.red * k, c.green * k, c.blue * k, c.alpha) }
        return SitioObjeto(V3(0.3f, 0.75f, 1.2f), V3(1f, 0f, 0.2f), ojo, camara, luz, listOf(V3(0.5f, 3f, 1.6f) to 0.6f), null, 0f, if (apoyo) V3(0.3f, 0.72f, 1.0f) else null)
    }

    /**
     * Cada objeto se dibuja suelto, sin lanzar nada y dejando algo en el lienzo: en el laboratorio (por las firmas del cuadro)
     * a cada hora y en cada estado, en un sitio cualquiera con la luz del pincel y en el sitio de otro cuadro con su cámara.
     */
    @Test fun cadaObjetoSeDibujaSuelto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        for (dia in dias) for (h in horas) {
            val base = momento(dia, h)
            for (o in ObjetosLaboratorio.TODOS) {
                val m = conObjeto(dia, h, o); val v = VistaLab(m)
                val px = try {
                    pintado {
                        with(ObjetosLaboratorio) { sobreElSoporte(m, v.pincel(MesaLab.CLARO), claro, escalaClaro) }
                        ObjetosLaboratorio.grande(m, v.pincel(CuadroLab.PIE_GRANDE), pie, apoyo(m), escalaPie)?.pintar?.invoke(this)
                    }
                } catch (x: Exception) { throw AssertionError("$dia $h con «$o» (${EscenaLaboratorio.lugar(base)}): $x", x) }
                assertTrue("$dia $h: «$o» no se ve", px.count { (it ushr 24) > 40 } > 60)
            }
        }
        for (o in ObjetosLaboratorio.TODOS) for (h in listOf(10.0, 21.5)) {
            val m = conObjeto("2027-05-05", h, o)
            for (escala in listOf(0.6f, 1f, 1.4f)) {
                val px = pintado {
                    with(ObjetosLaboratorio) { sobreElSoporte(m, pincel(m), Offset(190f, 200f), escala) }
                    ObjetosLaboratorio.grande(m, pincel(m), Offset(190f, 260f), Offset(170f, 200f), escala)?.pintar?.invoke(this)
                }
                assertTrue("«$o» a escala $escala no se ve", px.count { (it ushr 24) > 40 } > 30)
            }
            val px = pintado {
                with(ObjetosLaboratorio) { sobreElSoporte(m, sitioDeOtroCuadro(false)) }
                ObjetosLaboratorio.grande(m, sitioDeOtroCuadro(true))?.pintar?.invoke(this)
            }
            assertTrue("«$o» en otro cuadro no se ve", px.count { (it ushr 24) > 40 } > 30)
        }
    }

    /**
     * El objeto del día se ve en el cuadro, de día y de noche: el cuadro con el objeto cambia, cerca de su sitio, respecto del
     * mismo cuadro sin objeto. Y el cuadro entero se dibuja con cada uno en cada estado.
     */
    @Test fun seVeEnElCuadro() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        fun cuadro(m: Momento) = pintado { lienzo { with(EscenaLaboratorio) { quieta(m) } } }
        val vistos = HashSet<Lugar>()
        for (h in listOf(10.0, 21.5)) {
            val nada = cuadro(conObjeto("2027-05-05", h, null))
            for (o in ObjetosLaboratorio.TODOS) {
                val con = cuadro(conObjeto("2027-05-05", h, o))
                val sitio = if (o in ObjetosLaboratorio.GRANDES) pie else claro
                var distintos = 0
                for (y in (sitio.y - 90f).toInt().coerceAtLeast(0) until (sitio.y + 15f).toInt().coerceAtMost(ALTO.toInt())) for (x in (sitio.x - 60f).toInt() until (sitio.x + 60f).toInt()) {
                    val i = y * ANCHO.toInt() + x; if (con[i] != nada[i]) distintos++ }
                assertTrue("«$o» a las $h: solo $distintos píxeles cambian", distintos > 250)
            }
        }
        for (dia in dias) for (h in listOf(7.0, 13.0, 21.5)) for (o in ObjetosLaboratorio.TODOS) {
            val m = conObjeto(dia, h, o); vistos += EscenaLaboratorio.lugar(m)
            try { pintado { lienzo { with(EscenaLaboratorio) { quieta(m) } }; lienzo { with(EscenaLaboratorio) { viva(m, 2.3f) } } } }
            catch (x: Exception) { throw AssertionError("$dia $h con «$o»: $x", x) }
        }
        assertEquals(Lugar.entries.toSet(), vistos)
    }
}
