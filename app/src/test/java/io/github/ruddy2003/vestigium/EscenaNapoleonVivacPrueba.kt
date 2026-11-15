package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Compania
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fuego
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Marmita
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Moscu
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.VistaVivac
import io.github.ruddy2003.vestigium.ui.escenas.VivacCampo
import io.github.ruddy2003.vestigium.ui.escenas.VivacCerca
import io.github.ruddy2003.vestigium.ui.escenas.VivacLuz
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.Campo
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.Fondo
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

/**
 * El vivac de Napoleónico (tanda S14, cuadro B): una base que no se mueve (el campamento de la compañía) y un paisaje
 * que cambia por tramos de la campaña. Aquí se comprueba, contra el calendario de Jean-Baptiste (`AnioG5.kt`) y los días
 * de 2026 a 2032, que el tramo, los lugares provisionales, la compañía y sus fuegos, el horario, Moscú y Smolensk y la
 * luz cuentan lo mismo que el cuento, y que el cuadro se dibuja entero sin lanzar nada.
 */
class EscenaNapoleonVivacPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "granadero")!!, hora)
    private fun conClima(dia: String, hora: Double, clima: Clima) = Momento(FichaDelDia.de(dia, "granadero")!!.copy(clima = clima), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun elTramoSigueAlCalendario() {
        // Los días que lo deciden, con lo que dice el cuento de cada uno.
        val esperado = mapOf(
            "2027-03-20" to Fondo.FRANCIA, "2027-04-02" to Fondo.ALEMANIA, "2027-05-10" to Fondo.POLONIA, "2027-06-23" to Fondo.POLONIA,
            "2027-06-24" to Fondo.NIEMEN, "2027-06-25" to Fondo.RUSIA, "2027-08-17" to Fondo.SMOLENSK, "2027-08-18" to Fondo.SMOLENSK,
            "2027-08-19" to Fondo.RUSIA, "2027-09-07" to Fondo.BORODINO, "2027-09-13" to Fondo.RUSIA, "2027-09-14" to Fondo.MOSCU,
            "2027-12-14" to Fondo.NIEMEN, "2027-12-20" to Fondo.PRUSIA, "2028-01-05" to Fondo.PRUSIA, "2028-02-01" to Fondo.HOSPITAL,
            "2028-03-20" to Fondo.ALEMANIA, "2028-10-16" to Fondo.ALEMANIA, "2028-11-08" to Fondo.RIN, "2029-02-11" to Fondo.FRANCIA,
            "2029-06-20" to Fondo.PUEBLO, "2030-04-05" to Fondo.CIEN_DIAS, "2030-06-18" to Fondo.CIEN_DIAS, "2027-02-01" to Fondo.CUARTEL,
        )
        for ((dia, fondo) in esperado) assertEquals(dia, fondo, VivacNapoleon.fondo(momento(dia)))
        cadaDia { d, m ->
            val f = VivacNapoleon.fondo(m)
            // El lugar manda: Moscú, el cuartel, el hospital y el pueblo tienen su fondo; la retirada no es de este cuadro.
            when (EscenaNapoleon.lugar(m)) {
                Lugar.MOSCU -> assertEquals("$d", Fondo.MOSCU, f)
                Lugar.CUARTEL -> assertEquals("$d", Fondo.CUARTEL, f)
                Lugar.HOSPITAL -> assertEquals("$d", Fondo.HOSPITAL, f)
                Lugar.PUEBLO -> assertEquals("$d", Fondo.PUEBLO, f)
                Lugar.VIVAC, Lugar.RETIRADA -> assertTrue("$d: $f", f !in setOf(Fondo.MOSCU, Fondo.CUARTEL, Fondo.HOSPITAL, Fondo.PUEBLO))
            }
            // Smolensk y Borodino solo sus días; el Niemen, el día que lo cruza (a la ida y a la vuelta).
            assertEquals("$d", hoyEs(m, "smolensk") && EscenaNapoleon.lugar(m) == Lugar.VIVAC, f == Fondo.SMOLENSK)
            assertEquals("$d", hoyEs(m, "borodino"), f == Fondo.BORODINO)
            assertEquals("$d", hoyEs(m, "niemen") || hoyEs(m, "niemen_vuelta"), f == Fondo.NIEMEN)
            // Rusia solo en 1812 (temporada 1), y Alemania y el Rin en 1812 y 1813; Francia en 1812 y 1814; los Cien Días en 1815.
            if (f == Fondo.RUSIA || f == Fondo.POLONIA) assertEquals("$d", 1, m.ficha.temporada)
            if (f == Fondo.RIN) assertEquals("$d", 2, m.ficha.temporada)
            if (f == Fondo.CIEN_DIAS) assertEquals("$d", 4, m.ficha.temporada)
            assertEquals("$d", f in setOf(Fondo.CUARTEL, Fondo.HOSPITAL, Fondo.PUEBLO), VivacNapoleon.provisional(m))
        }
    }

    @Test fun elCampoEsElDelTramoYDeLaEstacion() {
        assertEquals(Campo.TRIGO, VivacNapoleon.campo(conClima("2027-07-10", 13.0, Clima.DESPEJADO)))
        assertEquals(Campo.ARENA, VivacNapoleon.campo(conClima("2027-05-25", 13.0, Clima.DESPEJADO)))
        assertEquals(Campo.VINA, VivacNapoleon.campo(conClima("2029-09-01", 13.0, Clima.DESPEJADO)))
        assertEquals(Campo.RASTROJO, VivacNapoleon.campo(conClima("2027-09-20", 13.0, Clima.DESPEJADO)))
        // Cada tierra con lo suyo: Alemania de prado, Francia de viñas, Rusia de trigo.
        assertEquals(Campo.HIERBA, VivacNapoleon.campo(conClima("2028-06-12", 13.0, Clima.DESPEJADO)))
        assertEquals(Campo.HIERBA, VivacNapoleon.campo(conClima("2028-10-01", 13.0, Clima.DESPEJADO)))
        assertEquals(Campo.VINA, VivacNapoleon.campo(conClima("2027-03-20", 13.0, Clima.DESPEJADO)))
        cadaDia { d, m -> assertEquals("$d", EscenaNapoleon.nieve(m) >= 0.5f, VivacNapoleon.campo(m) == Campo.NIEVE) }
    }

    @Test fun laBaseNoSeMueve() {
        // El suelo del gato, su mirador, el fuego y la tienda están siempre en el mismo sitio, sea el día que sea.
        val base = VivacNapoleon.anclas(momento("2027-07-10", 13.0)).filter { it.tipo != TipoAncla.FUEGO }.map { Triple(it.tipo, it.x, it.y) }
        for (dia in listOf("2026-05-01", "2027-03-20", "2027-06-24", "2027-08-17", "2027-09-16", "2027-12-20", "2028-07-10", "2029-02-11", "2030-06-18", "2031-06-01"))
            for (h in listOf(3.0, 7.0, 13.0, 21.0)) {
                val m = momento(dia, h); if (EscenaNapoleon.lugar(m) == Lugar.RETIRADA) continue
                assertEquals("$dia $h", base, VivacNapoleon.anclas(m).filter { it.tipo != TipoAncla.FUEGO }.map { Triple(it.tipo, it.x, it.y) })
                assertEquals("$dia $h", listOf(VivacCerca.FUEGO.x), VivacNapoleon.estorbos(m))
            }
        // El objeto del día está a la vista: el soporte y el pie del grande, dentro del cuadro y a la derecha del fuego.
        assertTrue(VivacCerca.SOPORTE.x in VivacCerca.FUEGO.x..ANCHO && VivacCerca.SOPORTE.y in 0f..ALTO)
        assertTrue(VivacCerca.PIE_GRANDE.x in VivacCerca.FUEGO.x..ANCHO)
        // La fila de pabellones es la diagonal grande: sale de abajo a la izquierda y se aleja hacia el centro.
        val fila = VivacCerca.FILA.map { VivacNapoleon.w(it.first, it.second) }
        assertTrue(fila.first().x < ANCHO * 0.3f && fila.first().y > ALTO * 0.85f)
        assertTrue(fila.zipWithNext().all { (a, b) -> b.x > a.x && b.y < a.y })
        assertTrue(fila.last().y < ALTO * 0.6f)
    }

    @Test fun losPabellonesYLosFuegosSiguenALaCompania() {
        cadaDia { d, m ->
            if (EscenaNapoleon.lugar(m) == Lugar.RETIRADA) return@cadaDia
            val sin = EscenaNapoleon.lugar(m) == Lugar.HOSPITAL || EscenaNapoleon.lugar(m) == Lugar.PUEBLO
            assertEquals("$d", sin, VivacNapoleon.sinCompania(m))
            // Dos pabellones por rancho, a todas horas (la base); ninguno donde no está la compañía.
            for (h in listOf(3.0, 7.0, 12.5, 21.0)) {
                val mh = momento(d.toString(), h)
                assertEquals("$d $h", if (sin) 0 else 2 * EscenaNapoleon.fuegos(mh), VivacNapoleon.pabellones(mh))
                assertEquals("$d $h", !sin, VivacNapoleon.aguila(mh))
            }
            // «Caben todos alrededor de un solo fuego»: con pocos, dos pabellones; con la compañía entera, la fila llena.
            when (EscenaNapoleon.cuantos(m)) {
                Compania.POCOS -> if (!sin) assertEquals("$d", 2, VivacNapoleon.pabellones(m))
                Compania.ENTERA -> assertTrue("$d", VivacNapoleon.pabellones(m) >= 28)
                Compania.MERMADA -> assertTrue("$d", VivacNapoleon.pabellones(m) in 16..26)
            }
            // Los otros ranchos de la compañía, a lo largo de la fila: uno menos que los fuegos (el nuestro va en la base).
            assertTrue("$d", VivacCampo.ranchos(m).size <= (VivacNapoleon.fuegosDeLaCompania(m) - 1).coerceAtLeast(0))
        }
        // Los fuegos del ejército, hasta el horizonte: solo de noche, y menos cuanto menos queda de la compañía.
        val noche = momento("2027-07-10", 21.0); val dia = momento("2027-07-10", 13.0)
        assertTrue(VivacNapoleon.fuegosDelEjercito(noche) > 200); assertEquals(0, VivacNapoleon.fuegosDelEjercito(dia))
        val mermada = momento("2027-10-01", 21.0); val pocos = momento("2029-03-01", 21.0)
        assertEquals(Compania.POCOS, EscenaNapoleon.cuantos(pocos))
        assertTrue(VivacNapoleon.fuegosDelEjercito(noche) > VivacNapoleon.fuegosDelEjercito(pocos))
        assertTrue(VivacCampo.fuegosDeHoy(noche).size >= VivacCampo.fuegosDeHoy(pocos).size)
        assertEquals(0, VivacCampo.fuegosDeHoy(momento("2029-09-01", 21.0)).size)                       // el pueblo: no hay campamento
        assertTrue(EscenaNapoleon.lugar(mermada) == Lugar.MOSCU)
    }

    @Test fun elHorarioDaTresCuadros() {
        val dia = "2027-07-10"
        val siete = momento(dia, 7.0); val una = momento(dia, 13.0); val alto = momento(dia, 12.5); val nueve = momento(dia, 21.0); val tres = momento(dia, 3.0)
        assertEquals(Fase.MARCHA, EscenaNapoleon.fase(siete)); assertEquals(Fase.MARCHA, EscenaNapoleon.fase(una))
        assertEquals(Fase.VIVAC, EscenaNapoleon.fase(nueve)); assertEquals(Fase.SUENO, EscenaNapoleon.fase(tres))
        // El fuego: rescoldo por la mañana, ceniza fría por la tarde, encendido de noche, bajo de madrugada.
        assertEquals(Fuego.RESCOLDO, EscenaNapoleon.estadoDelFuego(siete)); assertEquals(Fuego.APAGADO, EscenaNapoleon.estadoDelFuego(una))
        assertTrue(VivacNapoleon.fuego(nueve) >= 0.99f); assertTrue(VivacNapoleon.fuego(tres) in 0.02f..0.5f)
        assertEquals(Marmita.AL_FUEGO, EscenaNapoleon.marmita(alto))
        // El polvo de la columna por el camino, de día; de noche, los fuegos y el farol de la tienda.
        val claro = conClima(dia, 13.0, Clima.DESPEJADO)
        assertTrue(VivacNapoleon.polvo(claro) > 0.5f); assertEquals(0f, VivacNapoleon.polvo(nueve), 0f)
        assertEquals(0f, VivacNapoleon.polvo(conClima(dia, 13.0, Clima.LLUVIA)), 0f)
        assertEquals(0f, VivacNapoleon.farol(una), 0f); assertTrue(VivacNapoleon.farol(nueve) > 0.5f)
        // Las anclas del gato: el fuego, solo si está encendido.
        assertFalse(VivacNapoleon.anclas(una).any { it.tipo == TipoAncla.FUEGO }); assertTrue(VivacNapoleon.anclas(nueve).any { it.tipo == TipoAncla.FUEGO })
        // San Napoleón, de noche, el fuego más grande; los días de fiesta, la bandera desplegada (lo vigila el dibujo).
        assertTrue(VivacNapoleon.fuego(momento("2027-08-15", 20.0)) >= 0.99f)
    }

    @Test fun moscuEnteraArdeYQuemadaYSmolenskArde() {
        assertEquals(Moscu.ENTERA, EscenaNapoleon.moscu(momento("2027-09-14")))
        assertEquals(0f, VivacNapoleon.incendio(momento("2027-09-14", 21.0)), 0f)
        for (d in listOf("2027-09-15", "2027-09-16", "2027-09-17", "2027-09-18")) {
            val m = momento(d, 21.0)
            assertEquals(d, Moscu.ARDE, EscenaNapoleon.moscu(m)); assertTrue(d, VivacNapoleon.incendio(m) > 0.3f)
            // De noche la ciudad que arde lo alumbra todo: el cielo bajo se pone rojo.
            val l = VivacNapoleon.luces(m); val normal = EscenaNapoleon.luces(m)
            assertTrue(d, l.bajo.red - l.bajo.blue > normal.bajo.red - normal.bajo.blue)
        }
        for (d in listOf("2027-09-19", "2027-10-10", "2027-10-18")) { assertEquals(d, Moscu.QUEMADA, EscenaNapoleon.moscu(momento(d))); assertEquals(d, 0f, VivacNapoleon.incendio(momento(d, 21.0)), 0f) }
        assertTrue(VivacNapoleon.incendio(momento("2027-08-17", 21.0)) > 0.3f)
        assertEquals(0f, VivacNapoleon.incendio(momento("2027-08-19", 21.0)), 0f)
    }

    @Test fun laLuzCaminaConLaHoraYDeNocheHayDosLuces() {
        // Las sombras de las nubes del día caminan con la hora; de noche y con el cielo cubierto no hay.
        val dia = (1..28).map { "2027-07-%02d".format(it) }.first { VivacLuz.nubesDelDia(conClima(it, 9.0, Clima.NUBES)).isNotEmpty() }
        val nueve = VistaVivac(conClima(dia, 9.0, Clima.NUBES)); val cuatro = VistaVivac(conClima(dia, 16.0, Clima.NUBES))
        assertTrue(nueve.nubes.isNotEmpty() && cuatro.nubes.isNotEmpty())
        assertNotEquals(nueve.nubes.first().centro, cuatro.nubes.first().centro)
        assertTrue(VistaVivac(conClima(dia, 23.0, Clima.NUBES)).nubes.isEmpty())
        assertTrue(VistaVivac(conClima(dia, 13.0, Clima.LLUVIA)).nubes.isEmpty())
        // El sol cambia de lado entre la mañana y la tarde (y con él las sombras de la tienda y de los pabellones).
        assertTrue(VistaVivac(momento("2027-07-10", 8.0)).r.lat * VistaVivac(momento("2027-07-10", 17.0)).r.lat < 0f)
        // De noche, despejado y con luna: el fuego del rancho alumbra y la luna tumba sus sombras.
        val noche = (1..28).map { VistaVivac(conClima("2027-07-%02d".format(it), 22.0, Clima.DESPEJADO)) }.first { it.luna > 0.3f }
        assertTrue(noche.lumbre > 0.2f); assertTrue(noche.rLuna != null)
        // «La última luz del día» en los cañones de los fusiles, al atardecer; no a mediodía.
        val tarde = (0 until 15).maxOf { VivacLuz.ultimaLuz(VistaVivac(conClima("2027-07-10", 17.0 + it / 3.0, Clima.DESPEJADO)).l) }
        assertTrue(tarde > 0.2f); assertEquals(0f, VivacLuz.ultimaLuz(VistaVivac(conClima("2027-07-10", 13.0, Clima.DESPEJADO)).l), 0f)
        // Tres horas, tres cuadros: a las siete las sombras son largas (y con tope), a mediodía cortas; el relente es de
        // la mañana y no de la tarde ni de los días de lluvia.
        val siete = VistaVivac(conClima("2027-07-10", 7.0, Clima.DESPEJADO)).r; val una = VistaVivac(conClima("2027-07-10", 13.0, Clima.DESPEJADO)).r
        assertTrue("${siete.cot} ${una.cot}", siete.cot > 3f * una.cot && siete.cot <= 5.5f && una.cot < 1f)
        assertTrue(VivacLuz.relente(conClima("2027-07-10", 6.5, Clima.DESPEJADO), VistaVivac(conClima("2027-07-10", 6.5, Clima.DESPEJADO)).l) > 0.5f)
        assertEquals(0f, VivacLuz.relente(conClima("2027-07-10", 13.0, Clima.DESPEJADO), VistaVivac(conClima("2027-07-10", 13.0, Clima.DESPEJADO)).l), 0f)
        assertEquals(0f, VivacLuz.relente(conClima("2027-07-10", 6.5, Clima.LLUVIA), VistaVivac(conClima("2027-07-10", 6.5, Clima.LLUVIA)).l), 0f)
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /**
     * El vivac se dibuja entero (la parte quieta y la viva) en cada tramo y lugar, a las horas de cada fase, con cada
     * objeto y sin objeto, con nieve, lluvia y niebla, sin lanzar nada.
     */
    @Test fun elVivacSeDibujaEnTodosSusTramos() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2027-03-20", "2027-04-20", "2027-05-25", "2027-06-24", "2027-07-10", "2027-08-15", "2027-08-17", "2027-09-07", "2027-09-14", "2027-09-16", "2027-10-01",
            "2027-12-14", "2027-12-20", "2027-02-01", "2028-02-01", "2028-11-20", "2028-12-05", "2029-02-11", "2029-09-01", "2030-06-18", "2031-01-15")
        val vistos = HashSet<Fondo>()
        for (dia in dias) for (h in listOf(4.5, 7.0, 12.5, 19.0, 23.0)) {
            val base = momento(dia, h); vistos += VivacNapoleon.fondo(base)
            for (o in ObjetosNapoleon.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima, Clima.NIEVE, Clima.LLUVIA, Clima.NIEBLA)) {
                if (clima != base.ficha.clima && o != null && o != "capote") continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaNapoleon) { quieta(m) } }
                        lienzo { with(EscenaNapoleon) { viva(m, 2.9f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
        assertEquals(Fondo.entries.toSet(), vistos)
    }
}
