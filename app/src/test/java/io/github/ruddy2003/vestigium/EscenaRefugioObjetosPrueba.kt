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
import io.github.ruddy2003.vestigium.ui.escenas.CamRefugio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosRefugio
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosRefugio.Sitio
import io.github.ruddy2003.vestigium.ui.escenas.SalaRefugio
import io.github.ruddy2003.vestigium.ui.escenas.V3
import io.github.ruddy2003.vestigium.ui.escenas.VistaRefugio
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
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
 * Los doce objetos del día de la residente (`ObjetosRefugio`, tanda S17): cuáles son grandes y cuáles chicos, que ninguno
 * sale dos veces (la sala no pinta su pieza el día que esa pieza es el objeto, y el día que se rompe el tocadiscos del
 * comedor el objeto es el que ya está abierto en la bancada), que cada grande devuelve su `Cosa` junto a la punta de la
 * bancada, que cada chico se pinta sobre su soporte y a la vista, que de noche les llegan dos luces, y que todos se
 * dibujan sin fallar de día y de noche, en los cuatro estados del refugio y en los días del cuento.
 */
class EscenaRefugioObjetosPrueba {
    private val K = CamRefugio
    private val S = SalaRefugio
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "residente")!!, hora)
    private fun conObjeto(dia: String, hora: Double, o: String?) = Momento(FichaDelDia.de(dia, "residente")!!.copy(objeto = o), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    /** Un día de cada estado del refugio: el cerrado de siempre, el racionamiento, la cuarentena y el «abierto». */
    private val ESTADOS = listOf("2027-05-05" to EscenaRefugio.Estado.CERRADO, "2028-03-25" to EscenaRefugio.Estado.RACIONAMIENTO, "2029-02-10" to EscenaRefugio.Estado.CUARENTENA, "2031-10-01" to EscenaRefugio.Estado.ABIERTO)
    private val pie: Offset get() = K.p(S.PIE_GRANDE)
    private val apoyo: Offset get() = K.p(S.APOYO_GRANDE)
    private val escalaGrande: Float get() = K.k(S.PIE_GRANDE) / 120f
    private val sobre: Offset get() = K.p(S.SOPORTE)
    private val escalaChico: Float get() = K.k(S.SOPORTE) / 120f

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosRefugio.TODOS.toSortedSet(), delCalendario)
        assertEquals(12, ObjetosRefugio.TODOS.size)
        // Los de la sala son todos objetos, y el contrato los dibuja todos.
        assertEquals(EscenaRefugio.DE_LA_SALA, ObjetosRefugio.YA_EN_LA_SALA)
        assertEquals(EscenaRefugio.DE_LA_SALA, ObjetosRefugio.DIBUJADOS)
        assertTrue(ObjetosRefugio.TODOS.containsAll(ObjetosRefugio.DIBUJADOS))
    }

    /** Cuatro grandes (la llave, el mono, la manta y el tocadiscos del comedor) y ocho chicos; cada día el cuadro sabe cuál le toca. */
    @Test fun cadaObjetoEsGrandeOChico() {
        assertEquals(setOf("llave", "mono", "manta", "tocadiscos"), ObjetosRefugio.GRANDES)
        assertEquals(setOf("disco", "linterna", "contador", "racion", "cuaderno", "pastilla", "foto", "flor"), ObjetosRefugio.TODOS.filter { !ObjetosRefugio.esGrande(it) }.toSet())
        assertFalse(ObjetosRefugio.esGrande(null as String?))
        cadaDia { d, m -> val o = m.ficha.objeto ?: return@cadaDia; assertEquals("$d: $o", o in ObjetosRefugio.GRANDES, ObjetosRefugio.esGrande(m)) }
    }

    /**
     * Ninguno sale dos veces, cada día de 2026 a 2032: la sala no pinta su pieza el día que es el objeto (y los demás días
     * sí); el objeto sale en un solo sitio (el soporte o junto a la bancada) y, el día que se rompe el tocadiscos del
     * comedor, si además es el objeto, no se pinta otro: ya está abierto en la bancada. La flor de papel y el disco del
     * objeto no quitan nada: son otros que la flor de verdad y el disco de la sala.
     */
    @Test fun ningunoSaleDosVeces() {
        cadaDia { d, m ->
            val o = m.ficha.objeto
            for (p in EscenaRefugio.DE_LA_SALA) assertEquals("$d $p", o != p, EscenaRefugio.laSalaPinta(m, p))
            val sitio = ObjetosRefugio.sitio(m)
            val esperado = when {
                o == null -> Sitio.NINGUNO
                o == "tocadiscos" && EscenaRefugio.tocadiscosRoto(m) -> Sitio.EN_LA_SALA
                o in ObjetosRefugio.GRANDES -> Sitio.GRANDE
                else -> Sitio.SOPORTE
            }
            assertEquals("$d $o", esperado, sitio)
            if (d.dayOfMonth % 4 == 0) {
                val v = VistaRefugio(m)
                assertEquals("$d $o: el grande", sitio == Sitio.GRANDE, ObjetosRefugio.grande(m, v.pincel(S.PIE_GRANDE), pie, apoyo, escalaGrande) != null)
            }
        }
        // El día que se rompe el tocadiscos del comedor, con él de objeto: la sala lo tiene abierto y el contrato no pinta otro.
        val roto = conObjeto("2028-09-20", 13.0, "tocadiscos")
        assertTrue(EscenaRefugio.tocadiscosRoto(roto))
        assertEquals(Sitio.EN_LA_SALA, ObjetosRefugio.sitio(roto))
        assertNull(ObjetosRefugio.grande(roto, VistaRefugio(roto).pincel(S.PIE_GRANDE), pie, apoyo, escalaGrande))
        // La flor de papel con la flor de verdad en su frasco (desde que vuelven): las dos a la vez, cada una en lo suyo.
        val flor = conObjeto("2030-08-10", 13.0, "flor")
        assertTrue(EscenaRefugio.florDeVerdad(flor)); assertEquals(Sitio.SOPORTE, ObjetosRefugio.sitio(flor))
        assertFalse("flor" in EscenaRefugio.DE_LA_SALA); assertFalse("disco" in EscenaRefugio.DE_LA_SALA)
    }

    /**
     * Cada grande devuelve su `Cosa` con el pie que se le dio, y su silueta está a la vista, junto a la punta de la bancada:
     * dentro del lienzo, a la derecha de la bancada, subiendo por encima del canto. Un chico, o ninguno, no devuelve nada.
     */
    @Test fun cadaGrandeDevuelveSuCosa() {
        for ((dia, _) in ESTADOS) for (h in listOf(7.0, 13.0, 21.0)) for (o in ObjetosRefugio.TODOS + listOf<String?>(null)) {
            val m = conObjeto(dia, h, o); val v = VistaRefugio(m)
            val g = ObjetosRefugio.grande(m, v.pincel(S.PIE_GRANDE), pie, apoyo, escalaGrande)
            if (o == null || o !in ObjetosRefugio.GRANDES) { assertNull("$o", g); continue }
            assertNotNull(o, g); g!!
            assertEquals(o, pie, g.pie)
            val caja = g.forma.getBounds()
            assertFalse("$o: la silueta está vacía", caja.isEmpty)
            assertTrue("$o: la silueta sube por encima del canto (${caja.top})", caja.top < apoyo.y + 5f)
            assertTrue("$o: la silueta no baja del lienzo más de la cuenta (${caja.bottom})", caja.bottom < ALTO + 25f)
            assertTrue("$o: la silueta está junto a la punta (${caja.left}..${caja.right})", caja.left > sobre.x - 50f && caja.right < ANCHO + 5f && caja.right > apoyo.x - 10f)
            assertTrue("$o: la silueta tiene tamaño (${caja.width} × ${caja.height})", caja.width > 15f && caja.height > 30f)
            assertEquals(o, caja, ObjetosRefugio.grande(m, v.pincel(S.PIE_GRANDE), pie, apoyo, escalaGrande)!!.forma.getBounds())
        }
    }

    /**
     * De noche les llegan las dos luces del cuadro: con la sala sola, la cálida de los pilotos por la izquierda y la violeta
     * del portón por detrás; en el turno de más (el racionamiento y la cuarentena), la lámpara de la bancada por arriba, más
     * cálida que el lado de atrás.
     */
    @Test fun deNocheDosLucesEnElSoporte() {
        for ((dia, _) in ESTADOS) {
            val m = momento(dia, 21.0); val v = VistaRefugio(m)
            val gris = Color(0xFFB0B0B0)
            val izq = v.c(S.SOPORTE, K.unidad(V3(-1f, 0.2f, -0.3f)), gris)
            val atras = v.c(S.SOPORTE, K.unidad(V3(0f, 0.3f, 1f)), gris)
            if (EscenaRefugio.lamparas(m).isEmpty()) {
                assertTrue("$dia: el lado de la izquierda es cálido ($izq)", izq.red > izq.blue + 0.03f)
                assertTrue("$dia: el lado de atrás es frío ($atras)", atras.blue > atras.red + 0.03f)
            } else {
                val arriba = v.c(S.SOPORTE, K.ARRIBA, gris); val detras = v.c(S.SOPORTE, K.FONDO, gris)
                assertTrue("$dia: la lámpara calienta lo de arriba ($arriba)", arriba.red > arriba.blue + 0.03f)
                assertTrue("$dia: lo de atrás, menos cálido ($detras)", arriba.red - arriba.blue > detras.red - detras.blue + 0.02f)
            }
        }
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** Lo que pinta el chico de [m] sobre el soporte, solo, en un lienzo vacío: la caja de lo pintado, o null si no pinta nada. */
    private fun cajaDelChico(m: Momento): IntArray? {
        val img = ImageBitmap(ANCHO.toInt(), ALTO.toInt())
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ANCHO, ALTO)) {
            with(ObjetosRefugio) { sobreElSoporte(m, VistaRefugio(m).pincel(S.SOPORTE), sobre, escalaChico) }
        }
        val px = img.toPixelMap()
        var x0 = Int.MAX_VALUE; var y0 = Int.MAX_VALUE; var x1 = -1; var y1 = -1
        for (y in 0 until px.height) for (x in 0 until px.width) if (px[x, y].alpha > 0.3f) { x0 = minOf(x0, x); y0 = minOf(y0, y); x1 = maxOf(x1, x); y1 = maxOf(y1, y) }
        return if (x1 < 0) null else intArrayOf(x0, y0, x1, y1)
    }

    /** El soporte solo lleva lo que toca: un chico se pinta a la vista, alrededor del soporte; un grande o ningún objeto, nada. */
    @Test fun elSoporteSoloLlevaLoQueToca() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        for (o in ObjetosRefugio.TODOS + listOf<String?>(null)) for (h in listOf(13.0, 21.0)) {
            val caja = cajaDelChico(conObjeto("2027-05-05", h, o))
            if (o == null || o in ObjetosRefugio.GRANDES) { assertNull("$o $h", caja); continue }
            assertNotNull("$o $h: no se pinta", caja); caja!!
            val ancho = caja[2] - caja[0]; val alto = caja[3] - caja[1]
            assertTrue("$o $h: es muy chico para leerse ($ancho × $alto)", ancho >= 14 && alto >= 8)
            assertTrue("$o $h: no está en el soporte (${caja.toList()})", abs((caja[0] + caja[2]) / 2f - sobre.x) < 40f && caja[1] < sobre.y + 5f && caja[3] > sobre.y - 15f)
        }
    }

    /** Cada objeto se dibuja suelto, chico o grande, a tres escalas, en los cuatro estados, de día y de noche, y en sitios cualesquiera. */
    @Test fun cadaObjetoSeDibujaSuelto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val img = Canvas(ImageBitmap(380, 285))
        for ((dia, _) in ESTADOS) for (h in listOf(3.0, 7.0, 13.0, 21.0)) {
            val v = VistaRefugio(momento(dia, h))
            for (escala in listOf(0.6f, 1f, 1.5f)) for (o in ObjetosRefugio.TODOS + listOf(null, "otro")) {
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, img, Size(380f, 285f)) {
                        with(ObjetosRefugio) { chico(v, o, sobre, escala) }
                        with(ObjetosRefugio) { chico(v, o, Offset(70f, 240f), escala) }
                        ObjetosRefugio.grande(v, o, pie, apoyo, escala)?.pintar?.invoke(this)
                        ObjetosRefugio.grande(v, o, Offset(100f, 270f), Offset(130f, 200f), escala)?.pintar?.invoke(this)
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» a escala $escala: $x", x)
                }
            }
        }
    }

    /**
     * El cuadro se dibuja con cada objeto (la parte quieta y la viva) en los cuatro estados del refugio, a las 7:00, las
     * 13:00, las 17:30 y las 21:00, y en los días del cuento que tocan a la bancada: el tocadiscos roto, la flor de verdad,
     * el sellado (sus flores de papel), la Nochebuena, la cuarentena con sus herramientas y el año nuevo con sus latas.
     */
    @Test fun elCuadroSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val img = Canvas(ImageBitmap(ancho, alto))
        val estados = HashSet<EscenaRefugio.Estado>()
        val dias = ESTADOS.map { it.first } + listOf("2028-09-20", "2030-08-10", "2027-01-15", "2027-12-24", "2027-01-01")
        for (dia in dias) for (h in listOf(7.0, 13.0, 17.5, 21.0)) {
            estados += EscenaRefugio.estado(momento(dia, h))
            for (o in ObjetosRefugio.TODOS) {
                val m = conObjeto(dia, h, o)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, img, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaRefugio) { quieta(m) } }
                        lienzo { with(EscenaRefugio) { viva(m, 1.5f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o»: $x", x)
                }
            }
        }
        assertEquals(EscenaRefugio.Estado.entries.toSet(), estados)
    }
}
