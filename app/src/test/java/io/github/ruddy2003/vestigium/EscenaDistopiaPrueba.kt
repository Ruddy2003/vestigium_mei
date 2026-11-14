package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDistopia
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDistopia.Furgon
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDistopia.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDistopia.Torre
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDistopia.Vigilancia
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * El cuadro de Distopía (tanda S3) cuenta lo mismo que el cuento del funcionario: dónde está (en la capital o, dos días,
 * en el pueblo de su hermana), si hay campaña, cuántas cámaras y cuántos sacos hay, cómo va la torre nueva y si el
 * vecino de enfrente sigue en su casa salen de la ficha del día, y aquí se comparan con su calendario.
 */
class EscenaDistopiaPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "funcionario")!!, hora)

    @Test fun laEscenaEsLaDelFuncionario() {
        assertEquals(listOf("funcionario"), EscenaDistopia.vidas)
        assertEquals("funcionario", EscenaDistopia.vida("2027-05-10"))
    }

    @Test fun estaDondeDiceElCuento() {
        // Temporada 4 (2030): sale hacia el pueblo de su hermana el 28 de junio y vuelve el 30, en el tren de la noche.
        assertEquals(Lugar.CAPITAL, EscenaDistopia.lugar(momento("2030-06-27")))
        for (dia in listOf("2030-06-28", "2030-06-29")) assertEquals(dia, Lugar.PROVINCIA, EscenaDistopia.lugar(momento(dia)))
        assertEquals(Lugar.CAPITAL, EscenaDistopia.lugar(momento("2030-06-30")))
        // Los otros años no sale de la capital.
        for (anio in listOf(2027, 2028, 2029, 2031)) assertEquals(Lugar.CAPITAL, EscenaDistopia.lugar(momento("$anio-06-28")))
        // En el pueblo no hay cámaras, ni farolas, ni pilotos, ni bidón.
        val pueblo = momento("2030-06-28", 21.4)
        assertEquals(0, EscenaDistopia.camaras(pueblo)); assertFalse(EscenaDistopia.farolas(pueblo)); assertFalse(EscenaDistopia.pilotos(pueblo))
        assertTrue(EscenaDistopia.estorbos(pueblo).isEmpty())
    }

    @Test fun enCampanaElMinisterioNoApagaLasLuces() {
        // Temporada 2 (2028): del 1 al 21 de abril. Temporada 4 (2030): del 20 de marzo al 9 de abril.
        assertFalse(EscenaDistopia.enCampana(momento("2028-03-31")))
        for (dia in listOf("2028-04-01", "2028-04-10", "2028-04-21", "2030-03-20", "2030-04-09")) assertTrue(dia, EscenaDistopia.enCampana(momento(dia)))
        assertFalse(EscenaDistopia.enCampana(momento("2028-04-22"))); assertFalse(EscenaDistopia.enCampana(momento("2030-04-10")))
        for (anio in listOf(2027, 2029, 2031)) assertFalse(EscenaDistopia.enCampana(momento("$anio-04-05")))
        // De madrugada, en campaña, casi todas las rendijas tienen luz; un día cualquiera, casi ninguna.
        assertTrue(EscenaDistopia.rendijas(momento("2028-04-10", 2.0)) > 0.9f)
        assertTrue(EscenaDistopia.rendijas(momento("2028-05-10", 2.0)) < 0.1f)
        // De día no hay ninguna encendida.
        assertEquals(0f, EscenaDistopia.rendijas(momento("2028-04-10", 13.0)), 0f)
    }

    @Test fun cadaAnioHayMasCamarasYMenosRacion() {
        // El arco «camaras» sube de 0.4 a 0.9: de 5 cámaras a 9. El arco «racion» baja de 0.6 a 0.35: de 6 sacos a 4.
        assertEquals(5, EscenaDistopia.camaras(momento("2027-01-02"))); assertEquals(9, EscenaDistopia.camaras(momento("2031-12-30")))
        assertEquals(Vigilancia.ALGUNAS, EscenaDistopia.vigilancia(momento("2027-01-02"))); assertEquals(Vigilancia.TODAS, EscenaDistopia.vigilancia(momento("2031-12-30")))
        assertEquals(6, EscenaDistopia.sacos(momento("2027-01-02"))); assertEquals(4, EscenaDistopia.sacos(momento("2031-12-30")))
        var antes = 0; var sacos = 9
        for (anio in 2027..2031) for (mes in listOf("01-15", "07-15", "12-15")) {
            val m = momento("$anio-$mes")
            assertTrue("$anio-$mes: las cámaras no bajan", EscenaDistopia.camaras(m) >= antes); antes = EscenaDistopia.camaras(m)
            assertTrue("$anio-$mes: los sacos no suben", EscenaDistopia.sacos(m) <= sacos); sacos = EscenaDistopia.sacos(m)
        }
        // Los pilotos rojos llegan con las cámaras «en todas partes» (el corte de 0.75 del cuento), y solo se apagan sin corriente.
        assertFalse(EscenaDistopia.pilotos(momento("2027-06-01"))); assertTrue(EscenaDistopia.pilotos(momento("2031-06-01")))
    }

    @Test fun laTorreNuevaSubeYSeInauguraElDiaQueDiceElCalendario() {
        assertEquals(Torre.CIMIENTOS, EscenaDistopia.torre(momento("2027-02-01")))
        assertEquals(Torre.ESTRUCTURA, EscenaDistopia.torre(momento("2028-06-01"))); assertEquals(Torre.ESTRUCTURA, EscenaDistopia.torre(momento("2029-12-01")))
        assertEquals(Torre.REMATE, EscenaDistopia.torre(momento("2031-03-01")))
        // Se inaugura el 15 de mayo de la temporada 5 (2031): la víspera todavía tiene la grúa.
        assertEquals(Torre.REMATE, EscenaDistopia.torre(momento("2031-05-14")))
        assertEquals(Torre.TERMINADA, EscenaDistopia.torre(momento("2031-05-15"))); assertTrue(EscenaDistopia.desfile(momento("2031-05-15")))
        assertTrue(momento("2031-05-15").ficha.especiales.any { it.dia.id == "torre" })
        assertEquals(Torre.TERMINADA, EscenaDistopia.torre(momento("2031-12-01"))); assertEquals(Torre.TERMINADA, EscenaDistopia.torre(momento("2032-02-01")))
        // Nunca baja.
        var antes = 0f; var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2032) { val a = EscenaDistopia.alturaDeLaTorre(momento(d.toString())); assertTrue("$d: la torre no baja", a >= antes - 0.0001f); antes = a; d = d.plusDays(7) }
    }

    @Test fun alVecinoDeEnfrenteSeLoLlevanElDiaQueDiceElCalendario() {
        assertTrue(EscenaDistopia.vecinoDeEnfrente(momento("2027-08-01"))); assertTrue(EscenaDistopia.vecinoDeEnfrente(momento("2028-06-24")))
        assertTrue(momento("2028-06-25").ficha.especiales.any { it.dia.id == "vecino" })
        assertFalse(EscenaDistopia.vecinoDeEnfrente(momento("2028-06-25"))); assertFalse(EscenaDistopia.vecinoDeEnfrente(momento("2029-01-01"))); assertFalse(EscenaDistopia.vecinoDeEnfrente(momento("2032-01-01")))
        // Ese día, antes de que amanezca, el furgón está en su portal; después, aparcado como siempre. Con desfile no hay coches.
        assertEquals(Furgon.EN_EL_PORTAL, EscenaDistopia.furgon(momento("2028-06-25", 6.0))); assertEquals(Furgon.APARCADO, EscenaDistopia.furgon(momento("2028-06-25", 13.0)))
        assertEquals(Furgon.APARCADO, EscenaDistopia.furgon(momento("2028-06-24", 6.0))); assertEquals(Furgon.NINGUNO, EscenaDistopia.furgon(momento("2028-05-01", 10.0))); assertEquals(Furgon.NINGUNO, EscenaDistopia.furgon(momento("2030-06-28", 10.0)))
    }

    @Test fun losDiasSenaladosSeVen() {
        // El Desfile de la Unidad, el Día del Fundador y el de la Victoria: desfile. Año nuevo y la última noche: fiesta.
        for (dia in listOf("2027-05-01", "2028-03-14", "2029-09-09")) assertTrue(dia, EscenaDistopia.desfile(momento(dia)))
        assertTrue(EscenaDistopia.fiesta(momento("2027-01-01"))); assertTrue(EscenaDistopia.fiesta(momento("2029-12-31")))
        // El apagón de tres días (temporada 1): ni farolas, ni pilotos, ni rendijas; solo unas velas y el bidón, de noche.
        for (dia in listOf("2027-10-05", "2027-10-06", "2027-10-07")) {
            val m = momento(dia, 21.4)
            assertTrue(dia, EscenaDistopia.apagon(m)); assertFalse(EscenaDistopia.farolas(m)); assertEquals(0f, EscenaDistopia.rendijas(m), 0f)
            assertTrue("$dia: velas", EscenaDistopia.ventanas(m) in 0.01f..0.1f)
            if (m.ficha.clima != Clima.TORMENTA) assertTrue("$dia: el bidón", EscenaDistopia.bidon(m))
        }
        assertFalse(EscenaDistopia.apagon(momento("2027-10-08"))); assertTrue(EscenaDistopia.farolas(momento("2027-10-08", 21.4)))
        // Los cohetes (temporada 3).
        assertTrue(EscenaDistopia.cohetes(momento("2029-10-15"))); assertFalse(EscenaDistopia.cohetes(momento("2029-10-16")))
    }

    @Test fun elTramoDelDiaSeNotaEnLaPlaza() {
        // Los tres primeros días del mes, la escalera de quien pega el cartel sigue al pie.
        assertTrue(EscenaDistopia.pegandoCarteles(momento("2027-06-02"))); assertFalse(EscenaDistopia.pegandoCarteles(momento("2027-06-04")))
        // Come de 12:30 a 13:00 (la caseta abierta); las actividades obligatorias, de 18 a 20 (el bidón y los taburetes); duerme desde las 23:30.
        assertTrue(EscenaDistopia.comiendo(momento("2027-05-06", 12.6))); assertFalse(EscenaDistopia.comiendo(momento("2027-05-06", 13.0)))
        assertTrue(EscenaDistopia.conGente(momento("2027-05-06", 19.0))); assertTrue(EscenaDistopia.bidon(momento("2027-05-06", 19.0)))
        assertEquals(3, EscenaDistopia.estorbos(momento("2027-05-06", 19.0)).size); assertEquals(1, EscenaDistopia.estorbos(momento("2027-05-06", 10.0)).size)
        // Al anochecer hay luz en las casas; después del toque de queda, en ninguna.
        assertTrue(EscenaDistopia.ventanas(momento("2027-05-06", 21.4)) > 0.4f)
        assertTrue(EscenaDistopia.toqueDeQueda(momento("2027-05-06", 2.0))); assertEquals(0f, EscenaDistopia.ventanas(momento("2027-05-06", 2.0)), 0f)
        // Las farolas, de noche; de día, no.
        assertTrue(EscenaDistopia.farolas(momento("2027-05-06", 22.0))); assertFalse(EscenaDistopia.farolas(momento("2027-05-06", 13.0)))
    }

    @Test fun elCartelCambiaCadaMes() {
        val vistos = HashSet<Int>()
        for (mes in 1..12) {
            val a = EscenaDistopia.cartelDelMes(momento("2027-%02d-03".format(mes))); val b = EscenaDistopia.cartelDelMes(momento("2027-%02d-27".format(mes)))
            assertEquals("el mismo todo el mes", a, b); assertTrue(a in 0..5); vistos += a
            if (mes < 12) assertFalse("cambia de un mes al siguiente", a == EscenaDistopia.cartelDelMes(momento("2027-%02d-03".format(mes + 1))))
        }
        assertEquals(6, vistos.size)
    }

    @Test fun ningunDiaElCuadroContradiceALaFicha() {
        var d = LocalDate.of(2026, 1, 1)
        var enElPueblo = 0; var enCampana = 0; var conTranvia = 0; var dias = 0
        while (d.year <= 2032) {
            val f = FichaDelDia.de(d.toString(), "funcionario")!!
            for (hora in listOf(2.0, 5.0, 7.0, 8.4, 10.0, 12.6, 13.0, 17.0, 19.0, 20.4, 21.0, 23.0, 23.8)) {
                val m = Momento(f, hora)
                val lugar = EscenaDistopia.lugar(m)
                assertEquals("$d: en el pueblo si y solo si el estado es provincia", f.estado == "provincia", lugar == Lugar.PROVINCIA)
                assertEquals("$d: en campaña si y solo si el estado es campana", f.estado == "campana", EscenaDistopia.enCampana(m))
                val camaras = (f.arcos["camaras"] ?: 0.0).toFloat(); val racion = (f.arcos["racion"] ?: 0.0).toFloat()
                assertEquals("$d: la vigilancia sigue al arco", if (camaras < 0.55f) Vigilancia.ALGUNAS else if (camaras < 0.75f) Vigilancia.MUCHAS else Vigilancia.TODAS, EscenaDistopia.vigilancia(m))
                assertEquals("$d: las cámaras siguen al arco", if (lugar == Lugar.PROVINCIA) 0 else (1f + 9f * camaras).roundToInt().coerceIn(0, 9), EscenaDistopia.camaras(m))
                assertEquals("$d: los sacos siguen al arco", (racion * 10f).roundToInt().coerceIn(0, 8), EscenaDistopia.sacos(m))
                val a = EscenaDistopia.alturaDeLaTorre(m)
                assertTrue("$d: la torre, de 0 a 1", a in 0f..1f)
                assertEquals("$d: terminada si y solo si llegó a 1", a >= 1f, EscenaDistopia.torre(m) == Torre.TERMINADA)
                if (f.temporada < 5) assertFalse("$d: no se inaugura antes de la temporada 5", EscenaDistopia.torre(m) == Torre.TERMINADA)
                if (f.especiales.any { it.dia.id == "torre" }) assertEquals("$d: el día de la inauguración ya está terminada", Torre.TERMINADA, EscenaDistopia.torre(m))
                // Sin corriente no hay farolas, ni pilotos, ni rendijas.
                if (m.con("apagon")) { assertFalse("$d: farolas en el apagón", EscenaDistopia.farolas(m)); assertFalse("$d: pilotos en el apagón", EscenaDistopia.pilotos(m)); assertEquals(0f, EscenaDistopia.rendijas(m), 0f) }
                if (m.hacer == Hacer.DORMIR) assertEquals("$d $hora h: después del toque de queda no hay luz en las casas", 0f, EscenaDistopia.ventanas(m), 0f)
                assertTrue("$d: las ventanas, de 0 a 1", EscenaDistopia.ventanas(m) in 0f..1f); assertTrue("$d: las rendijas, de 0 a 1", EscenaDistopia.rendijas(m) in 0f..1f)
                assertTrue("$d: el cartel, de 0 a 5", EscenaDistopia.cartelDelMes(m) in 0..5)
                if (lugar == Lugar.PROVINCIA) { assertFalse("$d: no hay desfile en el pueblo", EscenaDistopia.desfile(m)); assertFalse("$d: no hay cohetes en el pueblo", EscenaDistopia.cohetes(m)) }
                val furgon = EscenaDistopia.furgon(m)
                assertEquals("$d $hora h: el furgón está en el portal solo el día del vecino, antes de las 8", f.especiales.any { it.dia.id == "vecino" } && hora < 8.0 && lugar == Lugar.CAPITAL && !m.con("desfile"), furgon == Furgon.EN_EL_PORTAL)
                if (lugar == Lugar.PROVINCIA || m.con("desfile")) assertEquals("$d: sin coches", Furgon.NINGUNO, furgon)
                assertEquals("$d: la escalera, los tres primeros días del mes", lugar == Lugar.CAPITAL && d.dayOfMonth <= 3, EscenaDistopia.pegandoCarteles(m))
                // El tranvía: nunca sin corriente, ni después del toque de queda, ni con desfile, ni en el pueblo; y el mismo día, a todas las horas en que puede estar.
                if (EscenaDistopia.tranvia(m)) { assertEquals("$d: tranvía solo en la capital", Lugar.CAPITAL, lugar); assertFalse("$d: tranvía en el apagón", m.con("apagon")); assertFalse("$d $hora h: tranvía tras el toque de queda", m.hacer == Hacer.DORMIR); assertFalse("$d: tranvía con desfile", m.con("desfile")) }
                if (hora == 13.0 && EscenaDistopia.tranvia(m)) conTranvia++
                if (m.hacer != Hacer.DORMIR) assertEquals("$d $hora h: el tranvía no aparece y desaparece durante el día", EscenaDistopia.tranvia(Momento(f, 13.0)), EscenaDistopia.tranvia(m))
                // Las anclas del gato caben en el cuadro, y nunca faltan el suelo, el mirador, el objeto y el abrigo.
                val anclas = EscenaDistopia.anclas(m)
                for (an in anclas) assertTrue("$d $hora h: ancla ${an.tipo} fuera", an.x in 0f..ANCHO && an.y in 0f..ALTO && an.hasta in 0f..ANCHO)
                for (t in listOf(TipoAncla.SUELO, TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: falta $t", anclas.any { it.tipo == t })
                val suelo = anclas.first { it.tipo == TipoAncla.SUELO }
                assertTrue("$d: el suelo del gato mide al menos 110", suelo.hasta - suelo.x >= 110f)
                for (t in listOf(TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: $t dentro del suelo", anclas.first { it.tipo == t }.x in suelo.x..suelo.hasta)
                for (x in EscenaDistopia.estorbos(m)) assertTrue("$d: estorbo fuera del suelo", x in suelo.x..suelo.hasta)
                val fuego = lugar == Lugar.CAPITAL && EscenaDistopia.bidon(m)
                assertEquals("$d $hora h: hay fuego para el gato si y solo si arde el bidón", fuego, anclas.any { it.tipo == TipoAncla.FUEGO })
                if (fuego) { val an = anclas.first { it.tipo == TipoAncla.FUEGO }; assertTrue("$d: el gato se echa a un lado de la lumbre", kotlin.math.abs(an.x - an.hasta) >= 20f && an.x in suelo.x..suelo.hasta) }
                if (f.clima == Clima.TORMENTA) assertFalse("$d: con tormenta no arde el bidón", EscenaDistopia.bidon(m))
                assertEquals("$d: hay adorno si y solo si es fiesta o desfile", m.con("fiesta") || (m.con("desfile") && lugar == Lugar.CAPITAL), anclas.any { it.tipo == TipoAncla.ADORNO })
                assertTrue("$d: siempre hay algo que se mueve", EscenaDistopia.seMueve(m))
                EscenaDistopia.focos(m); EscenaDistopia.luzDelGato(m, androidx.compose.ui.geometry.Offset(suelo.x + 40f, suelo.y))
            }
            if (f.estado == "provincia") enElPueblo++; if (f.estado == "campana") enCampana++
            dias++
            d = d.plusDays(1)
        }
        assertEquals("dos días en el pueblo, solo en la temporada 4", 2, enElPueblo)
        assertEquals("veintiún días de campaña en las temporadas 2 y 4", 42, enCampana)
        assertTrue("hay tranvía unos días sí y otros no (dos de cada cinco): $conTranvia de $dias", conTranvia in (dias * 3 / 10)..(dias / 2))
    }
}
