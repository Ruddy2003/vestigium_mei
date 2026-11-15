package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosMongol
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.ValleCerro
import io.github.ruddy2003.vestigium.ui.escenas.ValleMongol
import io.github.ruddy2003.vestigium.ui.escenas.ValleRio
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate

/**
 * Lo que cuenta solo el valle de invierno de Mongol (tanda S12, cuadro A), además de lo que ya vigila
 * `EscenaMongolPrueba`: dónde está el rebaño a cada hora, la parición con sus corderos y sus antorchas, la carne de la
 * matanza, la piel del ciervo, el cuenco del mes blanco, las visitas de las fiestas, los bueyes de la mudanza, los
 * animales muertos del invierno malo y el potro blanco el día que nace. Todos los días de 2026 a 2032.
 */
class EscenaMongolVallePrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "pastor_mongol")!!, hora)
    private fun cadaDia(hacer: (LocalDate, String) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, d.toString()); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun elRebanoPaceEnLaLaderaDeDiaYDuermeEnElCorral() {
        cadaDia { d, dia ->
            for (h in listOf(3.0, 5.5, 7.0, 12.5, 15.0, 20.0, 23.0)) {
                val m = momento(dia, h)
                assertEquals("$d $h: las del corral y las de la ladera son el rebaño", EscenaMongol.ovejas(m), ValleMongol.ovejasEnLaLadera(m) + ValleMongol.ovejasEnElCorral(m))
                val fuera = m.hacer == Hacer.TRABAJO || m.hacer == Hacer.COMER
                if (!fuera) assertEquals("$d $h: de noche y al ordeñar, todas en el corral", 0, ValleMongol.ovejasEnLaLadera(m))
                if (fuera && EscenaMongol.faena(m) == EscenaMongol.Faena.NINGUNA && !EscenaMongol.partida(m)) assertTrue("$d $h: de día, casi todas en la ladera", ValleMongol.ovejasEnLaLadera(m) > ValleMongol.ovejasEnElCorral(m))
            }
        }
    }

    @Test fun lasFaenasDejanSuRastroSoloSusDias() {
        cadaDia { d, dia ->
            val m = momento(dia); val noche = momento(dia, 21.0)
            // La parición (del 25 de marzo al 13 de abril): corderos en el corral y junto a la puerta; antorchas de noche, no de día.
            val paricion = md(d) >= "03-25" && md(d) <= "04-13"
            assertEquals("$d: corderos", paricion, ValleMongol.corderos(m) > 0)
            assertEquals("$d: antorchas de noche", paricion, ValleMongol.antorchas(noche))
            assertFalse("$d: antorchas de día", ValleMongol.antorchas(m))
            // La matanza (del 28 al 30 de noviembre): la carne colgada de las varas de la carreta.
            assertEquals("$d: la carne", md(d) >= "11-28" && md(d) <= "11-30", ValleMongol.carneColgada(m))
            // La piel del ciervo, desde el cuarto día de la gran cacería (13 de noviembre) hasta que acaba (19).
            assertEquals("$d: la piel del ciervo", md(d) >= "11-13" && md(d) <= "11-19", ValleMongol.pielDeCiervo(m))
            // Todo eso pasa en el valle.
            if (paricion || ValleMongol.carneColgada(m) || ValleMongol.pielDeCiervo(m)) assertEquals("$d", Lugar.VALLE, EscenaMongol.lugar(m))
        }
    }

    @Test fun losDiasSenaladosSeVen() {
        var cuencos = 0; var potros = 0
        cadaDia { d, dia ->
            val m = momento(dia, 13.0); val noche = momento(dia, 23.0)
            // El mes blanco: el cuenco de leche fuera, en la nieve.
            assertEquals("$d: el cuenco", hoyEs(m, "mes_blanco"), ValleMongol.cuencoEnLaNieve(m)); if (ValleMongol.cuencoEnLaNieve(m)) cuencos++
            // Las fiestas: caballos ensillados de visita (de día y a la hora de la gente, no de noche) y cintas azules.
            assertEquals("$d: las visitas", EscenaMongol.fiesta(m), ValleMongol.caballosDeVisita(m) > 0)
            assertEquals("$d: nadie de visita de noche", 0, ValleMongol.caballosDeVisita(noche))
            assertEquals("$d: las cintas", EscenaMongol.fiesta(m) && !EscenaMongol.luto(m), ValleMongol.cintasAzules(m))
            // La mudanza: los bueyes esperan junto a las carretas cargadas.
            assertEquals("$d: los bueyes", m.con("partida"), ValleMongol.bueyes(m))
            if (ValleMongol.bueyes(m)) assertTrue("$d: con los bueyes, las carretas cargadas", EscenaMongol.carretasCargadas(m))
            // El potro blanco, con la yegua baya en el rincón del corral, el día que nace (en el valle).
            assertEquals("$d: el potro en el rincón", hoyEs(m, "potro"), ValleMongol.potroEnElRincon(m)); if (ValleMongol.potroEnElRincon(m)) { potros++; assertEquals(Lugar.VALLE, EscenaMongol.lugar(m)) }
            // El invierno malo (temporada 4): los muertos en la nieve dura, de la costra de hielo al fondo del rebaño.
            val malo = d.year == 2030 && md(d) >= "02-15" && md(d) <= "03-20"
            assertEquals("$d: los muertos del invierno malo", malo, ValleMongol.muertosEnLaNieve(m) > 0)
            assertEquals("$d: la costra de hielo", d.toString() == "2030-02-15", ValleMongol.costra(m))
            // La tienda de la madre: el día que muere, la ceniza fresca; después, la tierra quemada, cada vez menos.
            assertEquals("$d: la ceniza fresca", d.toString() == "2030-03-08", ValleMongol.cenizaFresca(m))
            val quemado = ValleMongol.quemado(m)
            if (EscenaMongol.sitioDeLaMadre(m)) assertTrue("$d", quemado > 0f) else assertEquals("$d", 0f, quemado, 0f)
            if (d.year >= 2031) assertTrue("$d: con los años se borra", quemado <= 0.3f)
        }
        assertEquals("un mes blanco de dos días por año", 2 * 7, cuencos)
        assertTrue("el potro nace al menos una vez", potros >= 1)
    }

    @Test fun elFuegoDeFueraYSuRescoldo() {
        cadaDia { d, dia ->
            for (h in listOf(7.0, 13.0, 21.0, 2.0)) {
                val m = momento(dia, h)
                if (EscenaMongol.lugar(m) != Lugar.VALLE) continue
                // O arden las llamas o queda el rescoldo, nunca las dos cosas.
                if (EscenaMongol.fuegoFuera(m) > 0.05f) assertEquals("$d $h", 0f, ValleMongol.rescoldo(m), 0f) else assertTrue("$d $h", ValleMongol.rescoldo(m) > 0f)
                // El gato se echa junto al hogar solo si arde.
                assertEquals("$d $h", EscenaMongol.fuegoFuera(m) >= 0.45f, EscenaMongol.anclas(m).any { it.tipo == TipoAncla.FUEGO })
            }
        }
    }

    @Test fun elRioSaleDeDetrasDelCerroYSeEnsanchaHaciaQuienMira() {
        val cortes = ValleRio.CORTES
        // De lejos a cerca: cada corte más abajo que el anterior y más ancho (con la perspectiva).
        for (i in 1 until cortes.size) {
            val a = cortes[i - 1]; val b = cortes[i]
            assertTrue("corte $i baja", (b.first.y + b.second.y) > (a.first.y + a.second.y))
            if (i >= 8) assertTrue("corte $i se ensancha", (b.second - b.first).getDistance() >= (a.second - a.first).getDistance() - 0.5f)
        }
        // Nace junto al final del pie del cerro, cerca del horizonte, y sale por el borde derecho.
        val finDelCerro = ValleCerro.PIE.last()
        assertTrue((cortes.first().first - finDelCerro).getDistance() < 6f)
        assertTrue(cortes.first().second.y < ValleMongol.H + 4f)
        assertTrue(cortes.last().first.x > 380f && cortes.last().second.x > 380f)
        // La cresta y el pie del cerro acaban en el mismo punto, y el hombro en el pie.
        assertEquals(ValleCerro.CRESTA.last(), ValleCerro.PIE.last())
        assertTrue((ValleCerro.HOMBRO.last() - ValleCerro.PIE.first { it.x >= ValleCerro.HOMBRO.last().x - 1f }).getDistance() < 3f)
    }

    /** Si esta JVM puede dibujar: en el arnés de escritorio sí; en la de Android sin gráficos, no (y la prueba se salta). */
    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /**
     * El valle se dibuja entero (la parte quieta y la viva) con cada uno de los doce objetos y sin objeto, con nieve,
     * sin nieve y nevando, de día y de noche, sin lanzar nada. Los grandes llevan su sombra arrojada con un recorte que
     * tiene que caber en el lienzo (un suelo por debajo de [ALTO] lo deja del revés y la sombra lanza una excepción).
     */
    @Test fun elValleSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        for (dia in listOf("2027-01-12", "2027-10-20", "2027-11-03")) for (h in listOf(9.0, 13.0, 21.0)) {
            val base = momento(dia, h)
            assertEquals("$dia: es el valle", Lugar.VALLE, EscenaMongol.lugar(base))
            for (o in ObjetosMongol.TODOS + listOf(null)) {
                val m = Momento(base.ficha.copy(objeto = o), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaMongol) { quieta(m) } }
                        lienzo { with(EscenaMongol) { viva(m, 3.7f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o»: $x", x)
                }
            }
        }
    }
}
