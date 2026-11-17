package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.AdarveOrden
import io.github.ruddy2003.vestigium.ui.escenas.CamOrden
import io.github.ruddy2003.vestigium.ui.escenas.CuboOrden
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden
import io.github.ruddy2003.vestigium.ui.escenas.LuzObjetoOrden
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosOrden
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosOrden.Sitio
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden
import io.github.ruddy2003.vestigium.ui.escenas.V3
import io.github.ruddy2003.vestigium.ui.escenas.VistaOrden
import io.github.ruddy2003.vestigium.ui.escenas.envolvente
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
 * Los doce objetos del día de la escriba (`ObjetosOrden`, tanda S18): cuáles son grandes y cuáles chicos, que ninguno sale
 * dos veces (el día del candil el cuadro deja vacía la hornacina de la almena de delante y el candil sale en el soporte),
 * que cada grande devuelve su `Cosa` junto a la almena, que de noche les llegan las dos luces (el candil, cálido; la luna,
 * fría), que lo que queda detrás de la almena no se pinta encima de ella, y que todos se dibujan sin fallar de día y de
 * noche, en los cuatro estados (Peñaseca, la expedición, el encierro y la herida) y en los días del cuento.
 */
class EscenaOrdenObjetosPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "escriba")!!, hora)
    private fun conObjeto(dia: String, hora: Double, o: String?, clima: Clima? = null): Momento {
        val f = FichaDelDia.de(dia, "escriba")!!.copy(objeto = o)
        return Momento(if (clima != null) f.copy(clima = clima) else f, hora)
    }
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    /** Un día de cada estado: Peñaseca de siempre, la expedición, el encierro y la herida. */
    private val ESTADOS = listOf("2027-05-05" to EscenaOrden.Lugar.PENASECA, "2028-05-10" to EscenaOrden.Lugar.EXPEDICION, "2029-02-20" to EscenaOrden.Lugar.ENCIERRO, "2030-05-10" to EscenaOrden.Lugar.HERIDA)
    private val pie: Offset get() = CamOrden.p(AdarveOrden.PIE_GRANDE)
    private val apoyo: Offset get() = CamOrden.p(AdarveOrden.APOYO_GRANDE)
    private val escalaGrande: Float get() = AdarveOrden.escalaEn(AdarveOrden.PIE_GRANDE)
    private val sobre: Offset get() = CamOrden.p(AdarveOrden.SOPORTE)
    private val escalaChico: Float get() = AdarveOrden.escalaEn(AdarveOrden.SOPORTE)

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosOrden.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosOrden.TODOS.size)
        assertTrue(ObjetosOrden.TODOS.containsAll(ObjetosOrden.GRANDES + ObjetosOrden.QUITA_DEL_CUADRO))
        assertTrue(ObjetosOrden.EN_EL_CUADRO.isEmpty())
    }

    /** Cinco grandes (la radio, el manual, la ración, la cantimplora y el soldador) y siete chicos; cada día el cuadro sabe cuál le toca. */
    @Test fun cadaObjetoEsGrandeOChico() {
        assertEquals(setOf("radio", "manual", "racion", "cantimplora", "soldador"), ObjetosOrden.GRANDES)
        assertEquals(setOf("pluma", "cuaderno", "lupa", "destornillador", "candil", "brujula", "codigo"), ObjetosOrden.TODOS.filter { !ObjetosOrden.esGrande(it) }.toSet())
        assertFalse(ObjetosOrden.esGrande(null as String?))
        cadaDia { d, m ->
            val o = m.ficha.objeto
            assertEquals("$d: $o", o in ObjetosOrden.GRANDES, ObjetosOrden.esGrande(m))
            val esperado = when { o == null -> Sitio.NINGUNO; o in ObjetosOrden.GRANDES -> Sitio.GRANDE; else -> Sitio.SOPORTE }
            assertEquals("$d: $o", esperado, ObjetosOrden.sitio(m))
        }
    }

    /**
     * Ninguno sale dos veces, cada día de 2026 a 2032: el cuadro pinta el candil de la almena de delante todos los días
     * menos el del candil; ese día el candil es chico y sale en el soporte, una sola vez; el grande sale solo los días de
     * grande.
     */
    @Test fun ningunoSaleDosVeces() {
        assertFalse("el candil que quita el cuadro no puede ser de los grandes", ObjetosOrden.QUITA_DEL_CUADRO.any { it in ObjetosOrden.GRANDES })
        var diasDeCandil = 0
        cadaDia { d, m ->
            val o = m.ficha.objeto
            assertEquals("$d $o", o != "candil", ObjetosOrden.elCuadroPinta(m, "candil"))
            if (o == "candil") { diasDeCandil++; assertEquals(Sitio.SOPORTE, ObjetosOrden.sitio(m)) }
            for (otra in listOf("radio", "manual", "farol", "cantaro")) assertTrue("$d $otra", ObjetosOrden.elCuadroPinta(m, otra))
            if (d.dayOfMonth % 4 == 0) {
                val v = VistaOrden(m)
                assertEquals("$d $o: el grande", ObjetosOrden.sitio(m) == Sitio.GRANDE, ObjetosOrden.grande(m, v.p, pie, apoyo, escalaGrande) != null)
            }
        }
        assertTrue("hay días de candil ($diasDeCandil)", diasDeCandil > 100)
    }

    /**
     * Cada grande devuelve su `Cosa` con el pie que se le dio, y su silueta está a la vista, junto a la almena: dentro del
     * lienzo, alrededor del pie, subiendo por encima de él y con tamaño; dos veces, la misma. Un chico, o ninguno, nada.
     */
    @Test fun cadaGrandeDevuelveSuCosa() {
        for ((dia, _) in ESTADOS) for (h in listOf(7.0, 13.0, 21.0)) for (o in ObjetosOrden.TODOS + listOf<String?>(null)) {
            val m = conObjeto(dia, h, o); val v = VistaOrden(m)
            val g = ObjetosOrden.grande(m, v.p, pie, apoyo, escalaGrande)
            if (o == null || o !in ObjetosOrden.GRANDES) { assertNull("$o", g); continue }
            assertNotNull(o, g); g!!
            assertEquals(o, pie, g.pie)
            val caja = g.forma.getBounds()
            assertFalse("$o: la silueta está vacía", caja.isEmpty)
            assertTrue("$o: la silueta sube por encima del pie (${caja.top})", caja.top < pie.y - 18f)
            assertTrue("$o: la silueta está alrededor del pie (${caja.left}..${caja.right})", caja.left < pie.x + 5f && caja.right > pie.x - 5f && caja.left > pie.x - 90f && caja.right < ANCHO + 20f)
            assertTrue("$o: la silueta no baja del lienzo (${caja.bottom})", caja.bottom < ALTO + 10f)
            assertTrue("$o: la silueta tiene tamaño (${caja.width} × ${caja.height})", caja.width > 20f && caja.height > 25f)
            assertEquals(o, caja, ObjetosOrden.grande(m, v.p, pie, apoyo, escalaGrande)!!.forma.getBounds())
        }
    }

    /**
     * El candil del día arde con la corona de candiles (de noche y al caer la tarde), no a mediodía ni el día de luto; de
     * noche, en el soporte, les llegan dos luces: la cálida de los candiles cercanos, por el lado que los mira, y la fría
     * de la luna.
     */
    @Test fun deNocheDosLuces() {
        assertTrue(ObjetosOrden.candilEncendido(conObjeto("2027-05-05", 21.0, "candil")))
        assertFalse(ObjetosOrden.candilEncendido(conObjeto("2027-05-05", 13.0, "candil")))
        assertFalse("de luto, apagado", ObjetosOrden.candilEncendido(conObjeto("2027-09-20", 21.0, "candil")))
        val gris = Color(0xFFB0B0B0)
        // Una noche con luna, a las 23:00.
        var d = LocalDate.of(2027, 1, 1); var m = momento(d.toString(), 23.0)
        while (EscenaOrden.luna(EscenaOrden.luces(m)) < 0.3f && d.year == 2027) { d = d.plusDays(1); m = momento(d.toString(), 23.0) }
        assertTrue("hay una noche con luna en 2027", EscenaOrden.luna(EscenaOrden.luces(m)) >= 0.3f)
        val lo = LuzObjetoOrden(m, VistaOrden(m).p)
        val en = TrazoOrden.aCamara(AdarveOrden.SOPORTE) + V3(0f, 0.1f, 0f)
        val candil = TrazoOrden.aCamara(CuboOrden.CANDIL)
        val haciaElCandil = TrazoOrden.unidad(candil - en)
        assertTrue("$d: el lado que mira al candil se calienta (${lo.calor(en, haciaElCandil)})", lo.calor(en, haciaElCandil) > 0.12f)
        assertTrue("$d: el de enfrente, menos", lo.calor(en, haciaElCandil * -1f) < lo.calor(en, haciaElCandil) * 0.7f)
        val calido = lo.color(gris, en, haciaElCandil)
        assertTrue("$d: el lado del candil es cálido ($calido)", calido.red > calido.blue + 0.05f)
        // El lado de la luna: hacia donde viene su luz (al revés de donde caen las sombras).
        val c = lo.lz.corre; val nLuna = TrazoOrden.aCamara(TrazoOrden.unidad(V3(-c.x, 1f, -c.y)))
        val frio = lo.lz.color(gris, TrazoOrden.normalCiudadela(nLuna))
        assertTrue("$d: el lado de la luna es frío ($frio)", frio.blue > frio.red + 0.02f)
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** Lo que pinta [dibujo] solo, en un lienzo vacío: los píxeles. */
    private fun pintado(dibujo: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit): androidx.compose.ui.graphics.PixelMap {
        val img = ImageBitmap(ANCHO.toInt(), ALTO.toInt())
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ANCHO, ALTO)) { dibujo() }
        return img.toPixelMap()
    }

    /** La caja de lo pintado, o null si no se pintó nada. */
    private fun caja(px: androidx.compose.ui.graphics.PixelMap): IntArray? {
        var x0 = Int.MAX_VALUE; var y0 = Int.MAX_VALUE; var x1 = -1; var y1 = -1
        for (y in 0 until px.height) for (x in 0 until px.width) if (px[x, y].alpha > 0.3f) { x0 = minOf(x0, x); y0 = minOf(y0, y); x1 = maxOf(x1, x); y1 = maxOf(y1, y) }
        return if (x1 < 0) null else intArrayOf(x0, y0, x1, y1)
    }

    /**
     * El soporte solo lleva lo que toca: un chico pinta algo, un grande o ningún objeto, nada. Y cada chico, en un sitio
     * libre del lienzo, a la escala del soporte, se lee: al menos 18 × 12 unidades, alrededor de su punto.
     */
    @Test fun elSoporteSoloLlevaLoQueToca() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val libre = Offset(120f, 200f)
        for (o in ObjetosOrden.TODOS + listOf<String?>(null)) for (h in listOf(13.0, 21.0)) {
            val m = conObjeto("2027-05-05", h, o)
            val enElSoporte = caja(pintado { with(ObjetosOrden) { sobreElSoporte(m, VistaOrden(m).p, sobre, escalaChico) } })
            if (o == null || o in ObjetosOrden.GRANDES) { assertNull("$o $h", enElSoporte); continue }
            assertNotNull("$o $h: no se pinta", enElSoporte)
            val suelto = caja(pintado { with(ObjetosOrden) { chico(LuzObjetoOrden(m, VistaOrden(m).p), o, libre, escalaChico) } })!!
            val ancho = suelto[2] - suelto[0]; val alto = suelto[3] - suelto[1]
            assertTrue("$o $h: es muy chico para leerse ($ancho × $alto)", ancho >= 18 && alto >= 12)
            assertTrue("$o $h: no está en su sitio (${suelto.toList()})", kotlin.math.abs((suelto[0] + suelto[2]) / 2f - libre.x) < 30f && suelto[1] < libre.y && suelto[3] > libre.y - 10f)
        }
    }

    /** Lo que queda detrás de la almena de delante (en la tronera, más allá de su esquina) no se pinta encima de ella. */
    @Test fun loDeDetrasDeLaAlmenaQuedaTapado() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val almena = envolvente(AdarveOrden.cuerpoDeLaAlmena(1).map { CamOrden.p(it) })
        val dentro = { q: Offset -> var signo = 0; var fuera = false
            for (i in almena.indices) { val a = almena[i]; val b = almena[(i + 1) % almena.size]; val c = (b.x - a.x) * (q.y - a.y) - (b.y - a.y) * (q.x - a.x); val s = if (c > 0f) 1 else -1; if (signo == 0) signo = s else if (s != signo) fuera = true }
            !fuera }
        val punto = sobre
        for (o in listOf("candil", "codigo")) {
            val m = conObjeto("2027-05-05", 13.0, o)
            val tras = TrazoOrden.aCiudadela(ObjetosOrden.enLaCamara(sobre, escalaChico))
            if (!(tras.x > CamOrden.PARAPETO && tras.z > AdarveOrden.almena(1) + AdarveOrden.ALMENA - 0.05f)) continue
            val px = pintado { with(ObjetosOrden) { sobreElSoporte(m, VistaOrden(m).p, sobre, escalaChico) } }
            for (dx in -3..3) for (dy in -6..0) {
                val q = Offset(punto.x + dx * 2f, punto.y + dy * 2f)
                if (q.x < 0f || q.x >= ANCHO || q.y < 0f || q.y >= ALTO || !dentro(q)) continue
                assertTrue("$o: pintado encima de la almena en $q", px[q.x.toInt(), q.y.toInt()].alpha < 0.05f)
            }
        }
    }

    /** Cada objeto se dibuja suelto, chico o grande, a tres escalas, en los cuatro estados, de día y de noche, con nieve y con polvo, en su sitio y en otros. */
    @Test fun cadaObjetoSeDibujaSuelto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val img = Canvas(ImageBitmap(380, 285))
        for ((dia, _) in ESTADOS) for (h in listOf(3.0, 7.0, 13.0, 21.0)) for (clima in listOf<Clima?>(null, Clima.NIEVE, Clima.POLVO)) {
            for (escala in listOf(0.5f, 0.66f, 1.2f)) for (o in ObjetosOrden.TODOS + listOf(null, "otro")) {
                val m = conObjeto(dia, h, o, clima); val lo = LuzObjetoOrden(m, VistaOrden(m).p)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, img, Size(380f, 285f)) {
                        with(ObjetosOrden) { chico(lo, o, sobre, escala) }
                        with(ObjetosOrden) { chico(lo, o, Offset(70f, 240f), escala) }
                        ObjetosOrden.grande(lo, o, pie, apoyo, escala)?.pintar?.invoke(this)
                        ObjetosOrden.grande(lo, o, Offset(100f, 270f), Offset(130f, 200f), escala)?.pintar?.invoke(this)
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h $clima con «$o» a escala $escala: $x", x)
                }
            }
        }
    }

    /**
     * El cuadro se dibuja con cada objeto (la parte quieta y la viva) en los cuatro estados, a las 7:00, las 13:00 y las
     * 21:00, y en los días del cuento que tocan al adarve: el año nuevo (candiles de día), la tormenta de polvo, los Caídos
     * (luto: candiles apagados), la nevada y la Vigilia.
     */
    @Test fun elCuadroSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val img = Canvas(ImageBitmap(ancho, alto))
        val lugares = HashSet<EscenaOrden.Lugar>()
        val dias = ESTADOS.map { it.first } + listOf("2027-01-01", "2027-04-02", "2027-09-20", "2030-01-15", "2027-06-21")
        for (dia in dias) for (h in listOf(7.0, 13.0, 21.0)) {
            lugares += EscenaOrden.lugar(momento(dia, h))
            for (o in ObjetosOrden.TODOS) {
                val m = conObjeto(dia, h, o)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, img, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaOrden) { quieta(m) } }
                        lienzo { with(EscenaOrden) { viva(m, 1.5f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o»: $x", x)
                }
            }
        }
        assertEquals(EscenaOrden.Lugar.entries.toSet(), lugares)
    }
}
