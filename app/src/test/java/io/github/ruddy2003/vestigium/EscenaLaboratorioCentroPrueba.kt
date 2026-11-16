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
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.CamCentro
import io.github.ruddy2003.vestigium.ui.escenas.CentroLab
import io.github.ruddy2003.vestigium.ui.escenas.CentroLab.Mesa
import io.github.ruddy2003.vestigium.ui.escenas.CuadroCentro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Jardines
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosLaboratorio
import io.github.ruddy2003.vestigium.ui.escenas.PantallasCentro
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.V3
import io.github.ruddy2003.vestigium.ui.escenas.VerdeCentro
import io.github.ruddy2003.vestigium.ui.escenas.VistaCentro
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * El segundo cuadro de Laboratorio (tanda S17): el centro del Claustro, el hall con el comedor grande. Se vigila cuándo
 * aparece (a las horas de comer y en los días de lo común, nunca en la cuarentena, la campana ni el cierre; arriba, a las
 * horas de comer), que la mancha del pozo camina con la hora, lo que hay en las mesas, las luces de los días, la cámara
 * (las elipses y la fuga de las verticales), el gato y que el cuadro se dibuja sin fallas con cada objeto.
 */
class EscenaLaboratorioCentroPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "cientifica")!!, hora)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    // ---------------------------------------------------------------- Cuándo aparece

    /** La regla de la composición, escrita aparte para compararla con [EscenaLaboratorio.cuadro]. */
    private fun esperado(m: Momento): Cuadro {
        val lugar = when (m.ficha.estado) { "cuarentena" -> Lugar.CUARENTENA; "experimento" -> Lugar.CAMPANA; "cierre" -> Lugar.CIERRE; "arriba" -> Lugar.ARRIBA; else -> Lugar.LABORATORIO }
        if (lugar == Lugar.CUARENTENA || lugar == Lugar.CAMPANA || lugar == Lugar.CIERRE) return Cuadro.LABORATORIO
        if (m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE) return Cuadro.CENTRO
        if (lugar == Lugar.LABORATORIO) {
            if (hoyEs(m, "censo") && m.hora >= 7.5 && m.hora < 10.0) return Cuadro.CENTRO
            if ((hoyEs(m, "anio_nuevo") || hoyEs(m, "fundacion")) && m.hora >= 12.5 && m.hora < 21.0) return Cuadro.CENTRO
        }
        return Cuadro.LABORATORIO
    }

    @Test fun apareceALasHorasDeComerYEnLosDiasDeLoComun() {
        var d = LocalDate.of(2026, 1, 1); val centro = HashMap<Int, Int>()
        while (d.year <= 2032) {
            for (h in listOf(2.0, 6.0, 7.0, 8.0, 10.0, 12.0, 13.0, 15.0, 19.0, 20.0, 20.5, 21.0, 22.0)) {
                val m = momento(d.toString(), h)
                assertEquals("$d $h", esperado(m), EscenaLaboratorio.cuadro(m))
                if (EscenaLaboratorio.cuadro(m) == Cuadro.CENTRO) centro[d.year] = (centro[d.year] ?: 0) + 1
            }
            d = d.plusDays(1)
        }
        // Las tres horas de las capturas de un día normal: el desayuno y la comida en el centro; a las 21:00, el laboratorio.
        val dia = "2027-05-05"
        assertEquals(listOf(Cuadro.CENTRO, Cuadro.CENTRO, Cuadro.CENTRO, Cuadro.LABORATORIO, Cuadro.LABORATORIO, Cuadro.LABORATORIO),
            listOf(7.0, 13.0, 20.0, 21.0, 10.0, 16.0).map { EscenaLaboratorio.cuadro(momento(dia, it)) })
        // Los días de lo común.
        assertEquals(Cuadro.CENTRO, EscenaLaboratorio.cuadro(momento("2027-02-12", 8.0))); assertEquals(Cuadro.LABORATORIO, EscenaLaboratorio.cuadro(momento("2027-02-12", 10.0)))
        assertEquals(Cuadro.LABORATORIO, EscenaLaboratorio.cuadro(momento("2027-02-11", 8.0)))
        assertEquals(Cuadro.CENTRO, EscenaLaboratorio.cuadro(momento("2027-01-01", 15.0))); assertEquals(Cuadro.CENTRO, EscenaLaboratorio.cuadro(momento("2027-03-18", 15.0)))
        assertEquals(Cuadro.LABORATORIO, EscenaLaboratorio.cuadro(momento("2027-03-17", 15.0)))
        // Nunca en la cuarentena, la campana ni el cierre; arriba, a las horas de comer.
        for (dd in listOf("2028-03-20", "2029-05-10", "2030-10-05")) for (h in listOf(7.0, 13.0, 20.0)) assertEquals("$dd $h", Cuadro.LABORATORIO, EscenaLaboratorio.cuadro(momento(dd, h)))
        assertEquals(Cuadro.CENTRO, EscenaLaboratorio.cuadro(momento("2031-05-10", 13.0))); assertEquals(Cuadro.LABORATORIO, EscenaLaboratorio.cuadro(momento("2031-05-10", 15.0)))
        // Cada año, unos cuatro de cada trece momentos del muestreo caen en el centro (el desayuno, la comida y la cena).
        for (a in 2026..2032) assertTrue("$a: ${centro[a]}", (centro[a] ?: 0) in 1300..1600)
    }

    // ---------------------------------------------------------------- La luz del pozo

    @Test fun laManchaDelPozoCaminaConLaHora() {
        val alba = CentroLab.mancha(7f); val medio = CentroLab.mancha(13f); val tarde = CentroLab.mancha(20f)
        // Sesgada hacia la izquierda del cuadro en el desayuno, a plomo a mediodía, sesgada hacia la cabecera en la cena.
        assertTrue(alba.x < -1f && alba.y > 4f); assertTrue(sqrt(medio.x * medio.x + medio.y * medio.y) < 0.5f); assertTrue(tarde.x > 6.5f)
        assertTrue(CamCentro.p(V3(alba.x, 0f, alba.y)).x < CamCentro.p(V3(medio.x, 0f, medio.y)).x)
        assertTrue(CamCentro.p(V3(medio.x, 0f, medio.y)).x < CamCentro.p(V3(tarde.x, 0f, tarde.y)).x)
        // Las tres manchas caen dentro del cuadro.
        for (mm in listOf(alba, medio, tarde)) { val q = CamCentro.p(V3(mm.x, 0f, mm.y)); assertTrue("$q", q.x in 0f..ANCHO && q.y in 0f..ALTO) }
        val d = "2027-05-05"
        val p7 = CentroLab.pozo(momento(d, 7.0)); val p13 = CentroLab.pozo(momento(d, 13.0)); val p20 = CentroLab.pozo(momento(d, 20.0))
        assertTrue(p13.d.y < -0.99f); assertTrue("sesgada al alba", p7.d.y > -0.95f); assertTrue("sesgada en la cena", p20.d.y > -0.9f)
        // Cálida al alba, blanca a mediodía, dorada en la cena.
        assertTrue(p7.color.red - p7.color.blue > p13.color.red - p13.color.blue)
        assertTrue(p20.color.red - p20.color.blue > p7.color.red - p7.color.blue)
        // El haz le da al centro de su mancha y no al otro lado del hall.
        for (h in listOf(7.0, 13.0, 20.0)) {
            val v = VistaCentro(momento(d, h)); val mm = v.pozo.mancha
            assertTrue("$h", v.haz(V3(mm.x, 0.01f, mm.y)) > 0.85f)
            assertEquals("$h", 0f, v.haz(V3(-mm.x * 0.2f - 6f * kotlin.math.sign(mm.x + 0.01f), 0.01f, -mm.y)), 0f)
        }
        // Por la mañana la pasarela le hace sombra a la mancha; a mediodía, no.
        val v7 = VistaCentro(momento(d, 7.0)); var sombra7 = 0
        for (i in -10..10) for (j in -10..10) { if (i * i + j * j > 100) continue; val w = V3(v7.pozo.mancha.x + i * 0.3f, 0.01f, v7.pozo.mancha.y + j * 0.3f); if (CentroLab.sombraDeLaPasarela(w, v7.pozo.d) > 0.5f) sombra7++ }
        assertTrue("la pasarela tapa la mancha de la mañana", sombra7 > 5)
        val v13 = VistaCentro(momento(d, 13.0))
        assertEquals(0f, CentroLab.sombraDeLaPasarela(V3(v13.pozo.mancha.x, 0.01f, v13.pozo.mancha.y), v13.pozo.d), 0f)
    }

    @Test fun lasLucesDeLosDias() {
        assertEquals(0f, CentroLab.penumbra(momento("2027-05-05", 13.0)), 0f)
        assertTrue(CentroLab.penumbra(momento("2027-12-21", 20.0)) > 0.5f)                 // la noche larga, a la luz de las velas
        assertTrue(CentroLab.velas(momento("2027-12-21", 20.0))); assertFalse(CentroLab.velas(momento("2027-12-20", 20.0)))
        assertTrue(CentroLab.penumbra(momento("2027-07-08", 13.0)) in 0.2f..0.8f)          // el apagón, a media luz
        assertFalse(CentroLab.ventilador(momento("2027-07-08", 13.0))); assertTrue(CentroLab.ventilador(momento("2027-07-07", 13.0)))
        assertTrue(CentroLab.pozo(momento("2027-07-08", 13.0)).fuerza < 0.5f)
        // La cena del Consejo, con sus lámparas bajas; a mediodía, apagadas; la noche larga, velas.
        assertTrue(CentroLab.lamparasDelConsejo(momento("2027-05-05", 20.0)) > 0.5f); assertEquals(0f, CentroLab.lamparasDelConsejo(momento("2027-05-05", 13.0)), 0f)
        assertEquals(0f, CentroLab.lamparasDelConsejo(momento("2027-12-21", 20.0)), 0f)
        // El destello apagado en su revisión; la boca de Botánica, a oscuras la noche larga.
        assertEquals(0f, CentroLab.destello(momento("2027-08-18", 13.0)), 0f); assertTrue(CentroLab.destello(momento("2027-08-17", 13.0)) > 0f)
        assertTrue(CentroLab.luzDeBotanica(momento("2027-12-21", 20.0)) < CentroLab.luzDeBotanica(momento("2027-12-20", 20.0)))
        // Las pantallas: apagadas el tercer día de las jornadas a la hora de trabajo (no se pinta el centro), encendidas a la comida.
        assertTrue(CentroLab.brilloDeLasPantallas(momento("2027-06-04", 13.0)) > 0f)
    }

    @Test fun loQueHayEnLasMesas() {
        val d = "2027-05-05"
        assertEquals(listOf(Mesa.DESAYUNO, Mesa.COMIDA, Mesa.CENA), listOf(7.0, 13.0, 20.0).map { CentroLab.mesa(momento(d, it)) })
        assertEquals(Mesa.CENSO, CentroLab.mesa(momento("2027-02-12", 8.0)))
        assertEquals(Mesa.FIESTA, CentroLab.mesa(momento("2027-01-01", 15.0))); assertEquals(Mesa.FIESTA, CentroLab.mesa(momento("2027-03-18", 13.0)))
        assertTrue(CentroLab.fresas(momento("2027-01-01", 15.0))); assertFalse(CentroLab.fresas(momento("2027-01-02", 13.0)))
        assertTrue(CentroLab.banderas(momento("2027-03-18", 13.0))); assertFalse(CentroLab.banderas(momento("2027-03-17", 13.0)))
        assertTrue(CentroLab.jornadas(momento("2027-06-02", 13.0)) && CentroLab.jornadas(momento("2027-06-04", 13.0))); assertFalse(CentroLab.jornadas(momento("2027-06-05", 13.0)))
        assertTrue(CentroLab.sillasALaBarandilla(momento("2031-05-10", 13.0))); assertFalse(CentroLab.sillasALaBarandilla(momento("2031-06-10", 13.0)))
        // El verde sube por los hilos con los jardines.
        val subidas = Jardines.entries.map { VerdeCentro.subida(it) }
        assertEquals(subidas.sorted(), subidas); assertEquals(0f, subidas.first(), 0f); assertEquals(1f, subidas.last(), 0f)
        // La pared de pantallas: la mancha verde en una de abajo; el humo, en una de arriba a la derecha.
        assertTrue(PantallasCentro.HUMO_COL >= PantallasCentro.COLUMNAS / 2 && PantallasCentro.HUMO_FILA < PantallasCentro.FILAS / 2)
        assertTrue(PantallasCentro.VERDE_COL in 1 until PantallasCentro.COLUMNAS - 1)
    }

    // ---------------------------------------------------------------- La cámara

    @Test fun laCamaraHaceElipsesYFugaLasVerticales() {
        // El horizonte, alto (≈ y 97): se ve mucho suelo del comedor.
        assertTrue(CamCentro.HORIZONTE in 88f..104f)
        // El borde del vacío en el suelo es una elipse convexa que rodea el centro del comedor.
        val q = (0 until 72).map { CamCentro.p(GeoCentro.aro(GeoCentro.RV, 0f, it * 5f)) }
        val c = CamCentro.p(V3(0f, 0f, 0f))
        var signo = 0
        for (i in q.indices) { val a = q[i]; val b = q[(i + 1) % q.size]; val cruz = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x); val s = if (cruz > 0) 1 else -1; if (signo == 0) signo = s; assertEquals("el borde del vacío no es convexo alrededor del centro ($i)", signo, s) }
        // Lo de enfrente, más lejos, sale más arriba y más chico.
        assertTrue(CamCentro.p(GeoCentro.aro(GeoCentro.RV, 0f, 90f)).y < c.y); assertTrue(CamCentro.k(GeoCentro.aro(GeoCentro.RV, 0f, 90f)) < CamCentro.k(V3(0f, 0f, 0f)))
        // Las verticales fugan un poco hacia abajo: a la izquierda del centro, el pie de una costilla queda más cerca del eje que su cabeza.
        val pie = CamCentro.p(GeoCentro.aro(GeoCentro.RC, 0f, 140f)); val cabeza = CamCentro.p(GeoCentro.aro(GeoCentro.RC, 9f, 140f))
        assertTrue(abs(pie.x - CamCentro.CX) < abs(cabeza.x - CamCentro.CX))
        // Lo de detrás de los ojos se recorta: un polígono que cruza el plano de cerca da puntos delante.
        val recortado = CamCentro.puntos(listOf(CamCentro.OJO + V3(0f, -1f, -2f), CamCentro.OJO + V3(1f, -1f, 3f), CamCentro.OJO + V3(-1f, -1f, 3f)))
        assertTrue(recortado.size >= 3 && recortado.all { it.x.isFinite() && it.y.isFinite() })
        // El rayo de un punto del lienzo y su punto del suelo vuelven al mismo sitio.
        val p = Offset(200f, 270f); val w = CamCentro.enPlano(p, GeoCentro.G1)!!; val vuelta = CamCentro.p(w)
        assertEquals(p.x, vuelta.x, 0.05f); assertEquals(p.y, vuelta.y, 0.05f)
    }

    // ---------------------------------------------------------------- El gato

    @Test fun elGatoTieneSueloLibreLuzYSusSitios() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaLaboratorio.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla
        for (dia in listOf("2027-05-05", "2027-02-12", "2027-12-21", "2031-05-10", "2027-03-18")) for (h in listOf(7.0, 8.0, 13.0, 20.0)) {
            val base = momento(dia, h); if (EscenaLaboratorio.cuadro(base) != Cuadro.CENTRO) continue
            for (o in ObjetosLaboratorio.TODOS + listOf(null)) {
                val m = Momento(base.ficha.copy(objeto = o), h)
                val anclas = EscenaLaboratorio.anclas(m); val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertEquals(CuadroCentro.PISO_GATO, suelo.y, 0f)
                assertTrue(suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$dia $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$dia $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo == TipoAncla.OBJETO || a.tipo == TipoAncla.ABRIGO) assertTrue("$dia $h ${a.tipo} ${a.x}", a.x in suelo.x..suelo.hasta) }
                val estorbos = EscenaLaboratorio.estorbos(m)
                val cortes = (listOf(suelo.x, suelo.hasta) + estorbos + anclas.filter { it.tipo == TipoAncla.OBJETO }.map { it.x }).sorted()
                assertTrue("$dia $h $o: tramo libre $cortes", cortes.zipWithNext().maxOf { (a, b) -> b - a } >= 110f)
                val quieto = gatoQuieto(suelo, anclas, talla, estorbos)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                    val luz = EscenaLaboratorio.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue(valido(luz.tono(c)))
                    assertTrue(luz.rayo.fuerza in 0f..1f && luz.noche in 0f..1f && valido(luz.brillo))
                }
            }
        }
    }

    @Test fun elMismoMomentoDaLoMismo() {
        val a = VistaCentro(momento("2027-05-05", 13.0)); val b = VistaCentro(momento("2027-05-05", 13.0))
        assertEquals(a.luces.map { it.w to it.fuerza }, b.luces.map { it.w to it.fuerza })
        assertEquals(a.pozo.mancha, b.pozo.mancha)
        assertTrue(EscenaLaboratorio.seMueve(momento("2027-05-05", 13.0)))
    }

    // ---------------------------------------------------------------- El dibujo

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }
    private fun sinObjeto(m: Momento) = Momento(m.ficha.copy(objeto = null), m.hora)

    private fun pixeles(m: Momento): IntArray {
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val img = ImageBitmap(ancho, alto)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ancho.toFloat(), alto.toFloat())) { lienzo { with(EscenaLaboratorio) { quieta(m) } } }
        return img.toPixelMap().buffer
    }

    @Test fun losDiasDelCuentoSeVenEnElCentro() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        // La Fundación (las banderas), la noche larga (las velas), el apagón y las jornadas, contra la víspera a la misma hora.
        for ((dia, h) in listOf("2027-03-18" to 13.0, "2027-12-21" to 20.0, "2027-07-08" to 13.0, "2027-06-02" to 13.0)) {
            val antes = LocalDate.parse(dia).minusDays(1).toString()
            assertEquals(Cuadro.CENTRO, EscenaLaboratorio.cuadro(momento(dia, h)))
            assertNotEquals(dia, pixeles(sinObjeto(momento(antes, h))).toList(), pixeles(sinObjeto(momento(dia, h))).toList())
        }
        // Las tres horas dan tres cuadros distintos.
        val tres = listOf(7.0, 13.0, 20.0).map { pixeles(sinObjeto(momento("2027-05-05", it))).toList() }
        assertNotEquals(tres[0], tres[1]); assertNotEquals(tres[1], tres[2])
    }

    @Test fun elCentroSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2027-05-05", "2027-01-01", "2027-02-12", "2027-03-18", "2027-06-02", "2027-07-08", "2027-08-18", "2027-09-10", "2027-12-21", "2030-12-20", "2031-04-26", "2031-04-27", "2031-05-13", "2032-04-01")
        for (dia in dias) for (h in listOf(7.0, 8.0, 13.0, 15.0, 20.0, 20.7)) {
            val base = momento(dia, h); if (EscenaLaboratorio.cuadro(base) != Cuadro.CENTRO) continue
            for (o in ObjetosLaboratorio.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima, Clima.LLUVIA)) {
                if (clima != base.ficha.clima && o != null) continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaLaboratorio) { quieta(m) } }
                        lienzo { with(EscenaLaboratorio) { viva(m, 3.7f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
    }
}
