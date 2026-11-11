package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.Bici
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.Letrero
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.Puesto
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lucesDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import io.github.ruddy2003.vestigium.vidas.RegimenLuz
import io.github.ruddy2003.vestigium.vidas.luzDe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * El cuadro de Ciudad neón (plan de historia y escenas, tanda P4b): lo que cuenta el callejón, sin dibujarlo. El
 * puesto, el letrero y la bici de Kei se deciden en funciones aparte del dibujo, y aquí se comparan con el calendario.
 */
class EscenaNeonPrueba {
    private val KEI = "repartidora"
    private val AMARU = "fideos"
    private fun m(vida: String, dia: String, hora: Double) = Momento(FichaDelDia.de(dia, vida)!!, hora)
    private fun cadaDia(bloque: (String) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { bloque(d.toString()); d = d.plusDays(1) } }
    private val horas = (0 until 72).map { it / 3.0 }

    @Test fun losDosCompartenElCallejonUnDiaCadaUno() {
        assertEquals(listOf(KEI, AMARU), EscenaNeon.vidas)
        val a = EscenaNeon.vida("2027-05-05"); val b = EscenaNeon.vida("2027-05-06")
        assertTrue("Un día de cada uno", a != b && EscenaNeon.vida("2027-05-07") == a)
    }

    @Test fun elPuestoSigueElHorarioDeAmaru() {
        // El mismo horario vale los días de Kei: el callejón es el mismo.
        for (dia in listOf("2027-05-05", "2028-10-10", "2031-01-15")) for (h in horas) {
            val deAmaru = m(AMARU, dia, h)
            val espera = when (deAmaru.hacer) { Hacer.TRABAJO -> Puesto.ABIERTO; Hacer.COMER -> Puesto.SOBREMESA; Hacer.LIBRE -> Puesto.RECOGIDO; Hacer.DORMIR -> Puesto.CERRADO; Hacer.CUIDAR -> Puesto.PREPARANDO; else -> null }
            assertEquals("$dia a las $h", espera, EscenaNeon.puesto(deAmaru))
            assertEquals("$dia a las $h, en un día de Kei", espera, EscenaNeon.puesto(m(KEI, dia, h)))
        }
        // Con el toque de queda cierra a las nueve.
        assertEquals(Puesto.ABIERTO, EscenaNeon.puesto(m(AMARU, "2029-03-18", 20.0)))
        assertEquals(Puesto.CERRADO, EscenaNeon.puesto(m(AMARU, "2029-03-18", 21.0)))
        assertEquals(Puesto.CERRADO, EscenaNeon.puesto(m(AMARU, "2029-03-18", 2.0)))
    }

    @Test fun elLetreroEstaComoDiceElCuento() {
        fun letrero(dia: String, hora: Double = 21.0) = EscenaNeon.letrero(m(AMARU, dia, hora))
        assertEquals(Letrero.ENCENDIDO, letrero("2027-05-05"))
        assertEquals("Con el puesto cerrado no luce", Letrero.APAGADO, letrero("2027-05-05", 13.0))
        assertEquals(Letrero.ENCENDIDO, letrero("2028-11-11"))
        assertEquals("Se descompone el 12 de noviembre de la temporada 2", Letrero.ROTO, letrero("2028-11-12"))
        assertEquals(Letrero.ROTO, letrero("2028-11-28"))
        assertEquals("Lo arregla con una pieza del mercado flotante", Letrero.REMENDADO, letrero("2028-11-29"))
        assertEquals(Letrero.REMENDADO, letrero("2030-06-14"))
        assertEquals("Sami lo deja como nuevo", Letrero.ENCENDIDO, letrero("2030-06-15"))
        assertEquals("La Hora Muda", Letrero.APAGADO, letrero("2027-08-08"))
        for (dia in listOf("2028-08-20", "2028-08-21", "2028-08-22")) assertEquals("El apagón", Letrero.APAGADO, letrero(dia))
        assertEquals(Letrero.ENCENDIDO, letrero("2028-08-23"))
        // El mismo letrero los días de Kei.
        assertEquals(Letrero.ROTO, EscenaNeon.letrero(m(KEI, "2028-11-20", 22.0)))
    }

    @Test fun laBiciDeKeiEstaDondeDiceElCuento() {
        fun bici(dia: String, hora: Double) = EscenaNeon.bici(m(KEI, dia, hora))
        assertEquals("Duerme: la bici está en su pasillo", Bici.FUERA, bici("2027-05-06", 7.0))
        assertEquals(Bici.ESPERANDO, bici("2027-05-06", 17.0))
        assertEquals("Cena en el puesto, con la bici enchufada", Bici.ENCHUFADA, bici("2027-05-06", 21.0))
        assertEquals(Bici.ESPERANDO, bici("2027-05-06", 23.0))
        assertEquals(Bici.ESPERANDO, bici("2028-03-21", 17.0))
        // Del choque al día en que vuelve a rodar, no está a ninguna hora.
        for (dia in listOf("2028-03-22", "2028-03-30", "2028-04-07")) for (h in horas) assertEquals("$dia a las $h", Bici.EN_EL_TALLER, bici(dia, h))
        assertEquals(Bici.ESPERANDO, bici("2028-04-08", 17.0))
        assertTrue("Vuelve con el cuadro soldado", EscenaNeon.soldada(m(KEI, "2028-04-08", 17.0)))
        assertFalse(EscenaNeon.soldada(m(KEI, "2028-03-21", 17.0)))
        assertFalse("En el taller no se ve", EscenaNeon.soldada(m(KEI, "2028-03-30", 17.0)))
        assertTrue(EscenaNeon.soldada(m(KEI, "2031-02-13", 17.0)))
        assertFalse("La pinta de azul otra vez", EscenaNeon.soldada(m(KEI, "2031-02-14", 17.0)))
        // Los días de Amaru no sale.
        assertEquals(Bici.NO_ES_SU_DIA, EscenaNeon.bici(m(AMARU, "2027-05-05", 21.0)))
        assertFalse(EscenaNeon.soldada(m(AMARU, "2029-05-05", 21.0)))
    }

    @Test fun enNingunDiaElCuadroContradiceALaFicha() {
        cadaDia { dia ->
            val fk = FichaDelDia.de(dia, KEI)!!; val fa = FichaDelDia.de(dia, AMARU)!!
            for (h in listOf(2.0, 7.0, 13.0, 17.0, 21.0, 23.0)) {
                val mk = Momento(fk, h); val ma = Momento(fa, h)
                val bici = EscenaNeon.bici(mk)
                assertEquals("$dia $h: sin bici", fk.estado == "sin_bici", bici == Bici.EN_EL_TALLER)
                if (bici == Bici.ESPERANDO) assertEquals("$dia $h", Hacer.TRABAJO, mk.hacer)
                if (bici == Bici.ENCHUFADA) assertTrue("$dia $h", mk.hacer == Hacer.COMER && EscenaNeon.puesto(mk) == Puesto.ABIERTO)
                for (mm in listOf(mk, ma)) {
                    val le = EscenaNeon.letrero(mm); val abierto = EscenaNeon.puesto(mm) == Puesto.ABIERTO
                    if (!abierto || mm.con("apagon")) assertEquals("$dia $h", Letrero.APAGADO, le)
                    else assertEquals("$dia $h", when { mm.arco("letrero") >= 0.5f -> Letrero.ROTO; mm.arco("letrero") > 0.05f -> Letrero.REMENDADO; else -> Letrero.ENCENDIDO }, le)
                    assertEquals("$dia $h: las máquinas", mm.arco("obra") >= 0.5f || mm.con("maquinas"), EscenaNeon.maquinas(mm))
                    if (mm.con("apagon")) assertFalse("$dia $h: en el apagón la farmacia no tiene luz", EscenaNeon.farmaciaAbierta(mm))
                }
                assertEquals("$dia $h: el letrero es el mismo para los dos", EscenaNeon.letrero(mk), EscenaNeon.letrero(ma))
            }
        }
    }

    @Test fun lasMaquinasYElMuralLleganCuandoDiceElCalendario() {
        assertFalse(EscenaNeon.maquinas(m(AMARU, "2030-08-31", 21.0)))
        assertTrue("Llegan el 1 de septiembre de la temporada 4", EscenaNeon.maquinas(m(AMARU, "2030-09-01", 21.0)))
        assertTrue(EscenaNeon.maquinas(m(KEI, "2031-08-29", 21.0)))
        assertFalse("Se van el 30 de agosto de la temporada 5", EscenaNeon.maquinas(m(KEI, "2031-08-30", 21.0)))
        assertFalse(EscenaNeon.mural(m(AMARU, "2030-06-14", 13.0)))
        assertTrue("Sami pinta la cortina el 15 de junio de la temporada 4", EscenaNeon.mural(m(AMARU, "2030-06-15", 13.0)))
        assertTrue(EscenaNeon.mural(m(KEI, "2032-01-10", 13.0)))
        assertFalse(EscenaNeon.mural(m(KEI, "2027-06-20", 13.0)))
    }

    @Test fun lasTresHorasDeLasCapturasDanTresCuadrosDistintos() {
        // Abajo siempre es de noche, así que el cuadro cambia por lo que está abierto y encendido.
        for (vida in listOf(KEI, AMARU)) {
            val estados = listOf(7.0, 13.0, 21.0).map { h -> val mm = m(vida, "2027-05-05", h); Triple(EscenaNeon.puesto(mm), EscenaNeon.letrero(mm), EscenaNeon.farmaciaAbierta(mm)) }
            assertEquals("$vida: $estados", 3, estados.toSet().size)
            assertEquals(Triple(Puesto.RECOGIDO, Letrero.APAGADO, false), estados[0])
            assertEquals(Triple(Puesto.CERRADO, Letrero.APAGADO, true), estados[1])
            assertEquals(Triple(Puesto.ABIERTO, Letrero.ENCENDIDO, false), estados[2])
        }
    }

    @Test fun lasAnclasDelGatoCabenEnElCallejon() {
        cadaDia { dia ->
            if (dia.endsWith("-01") || dia.endsWith("-15")) for (vida in listOf(KEI, AMARU)) for (h in listOf(3.0, 7.0, 13.0, 21.0)) {
                val mm = m(vida, dia, h); val anclas = EscenaNeon.anclas(mm)
                anclas.forEach { a -> assertTrue("$dia $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO && a.hasta in 0f..ANCHO) }
                assertTrue(anclas.any { it.tipo == TipoAncla.SUELO } && anclas.any { it.tipo == TipoAncla.MIRADOR } && anclas.any { it.tipo == TipoAncla.OBJETO } && anclas.any { it.tipo == TipoAncla.ABRIGO })
                assertEquals("$dia $h: el calor de la olla, solo con el puesto abierto", EscenaNeon.puesto(mm) == Puesto.ABIERTO, anclas.any { it.tipo == TipoAncla.FUEGO })
            }
        }
    }

    @Test fun unFocoAlumbraLoCercanoYTumbaLasSombrasLejosDeEl() {
        val noche = lucesDe(luzDe(22.0, RegimenLuz.SIEMPRE_NOCHE), Clima.DESPEJADO)
        val foco = Foco(Offset(100f, 100f), Color(0xFFFF3E9A), 1f, 60f, suelo = 200f)
        val a = Alumbrado(noche, listOf(foco)); val gris = Color(0xFF808080)
        fun claridad(c: Color) = 0.299f * c.red + 0.587f * c.green + 0.114f * c.blue
        val cerca = a.en(Offset(110f, 110f), gris); val lejos = a.en(Offset(370f, 280f), gris)
        assertTrue("Cerca del foco hay más luz", claridad(cerca) > claridad(lejos) + 0.1f)
        assertTrue("Y es de su color", cerca.red > cerca.green)
        assertTrue("La cara que le da la espalda queda en sombra", claridad(a.en(Offset(160f, 100f), gris, 1f)) < claridad(a.en(Offset(160f, 100f), gris, -1f)))
        for (x in 0..38) for (y in 0..28) { val c = a.en(Offset(x * 10f, y * 10f), Color.White); assertTrue(c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f) }
        // Lo que está a la derecha del foco echa la sombra a la derecha; lo que está delante, hacia quien mira.
        assertTrue(rayoDeFoco(foco, Offset(200f, 230f), 1f, gris).ax > 0f)
        assertTrue(rayoDeFoco(foco, Offset(30f, 230f), 1f, gris).ax < 0f)
        assertTrue(rayoDeFoco(foco, Offset(100f, 260f), 1f, gris).ay > 0f)
    }
}
