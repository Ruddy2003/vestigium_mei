package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.AzoteaEgipto
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEgipto
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.LejosEgipto
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosEgipto
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El cuadro de Egipto (tanda S9) cuenta lo mismo que el cuento de Pay: cuánta agua de la crecida hay en el valle, cómo
 * va la tumba del rey, si la cuadrilla duerme en el paso y lo que le va pasando a su casa salen de la ficha del día, y
 * aquí se comparan con su calendario. Antes y después de cada suceso el cuadro tiene que ser distinto.
 */
class EscenaEgiptoPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "escriba_tumba")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun hoy(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun laEscenaEsLaDePay() {
        assertEquals(listOf("escriba_tumba"), EscenaEgipto.vidas)
        assertEquals("escriba_tumba", EscenaEgipto.vida("2027-05-10"))
        assertTrue(EscenaEgipto.tallaDelGato in 0.8f..1.8f)
    }

    @Test fun elRioCreceYSeVaComoDiceElCalendario() {
        val maximo = mutableMapOf<Int, Float>(); val minimo = mutableMapOf<Int, Float>()
        cadaDia { d, m ->
            val rio = EscenaEgipto.rio(m)
            assertEquals("$d", m.ficha.arcos["rio"]!!.toFloat().coerceIn(0f, 1f), rio, 0.0001f)
            for (x in listOf(rio, EscenaEgipto.verdor(m), EscenaEgipto.limo(m), EscenaEgipto.maduro(m))) assertTrue("$d", x in 0f..1f)
            maximo[d.year] = maxOf(maximo[d.year] ?: 0f, rio); minimo[d.year] = minOf(minimo[d.year] ?: 1f, rio)
            // El agua llega más lejos cuanta más crecida hay, y nunca pasa del pie de los templos.
            assertTrue("$d", LejosEgipto.hastaDondeLlegaElAgua(rio) in LejosEgipto.hastaDondeLlegaElAgua(0f)..LejosEgipto.hastaDondeLlegaElAgua(1f))
            // El campo no puede estar verde y en espiga del todo a la vez, ni verde bajo el agua alta.
            assertTrue("$d", EscenaEgipto.verdor(m) + EscenaEgipto.maduro(m) <= 1.5f)
        }
        for (anio in 2026..2032) { assertTrue("$anio: no hay crecida", maximo[anio]!! >= 0.95f); assertTrue("$anio: el río no vuelve a su cauce", minimo[anio]!! <= 0.05f) }
        assertTrue(LejosEgipto.hastaDondeLlegaElAgua(1f) - LejosEgipto.hastaDondeLlegaElAgua(0f) > 30f)
        // El valle es una cuña que se abre hacia la derecha: el borde del desierto baja, y el agua de la crecida con él, sin pasarlo nunca.
        assertTrue(LejosEgipto.pieDelDesierto(380f) - LejosEgipto.pieDelDesierto(228f) > 18f); assertEquals(LejosEgipto.pieDelDesierto(100f), LejosEgipto.pieDelDesierto(228f), 0f)
        for (x in listOf(228f, 260f, 300f, 340f, 380f)) { assertTrue("$x", LejosEgipto.hastaDondeLlegaElAgua(1f, x) < LejosEgipto.pieDelDesierto(x)); assertTrue("$x", LejosEgipto.hastaDondeLlegaElAgua(0.5f, x) < LejosEgipto.hastaDondeLlegaElAgua(1f, x)); assertTrue("$x", LejosEgipto.pieDelDesierto(x) - LejosEgipto.H > 38f) }
        assertTrue(LejosEgipto.hastaDondeLlegaElAgua(1f, 380f) > LejosEgipto.hastaDondeLlegaElAgua(1f, 228f) + 15f)
        for (anio in 2027..2031) {
            // En junio el río está en su cauce y el campo seco; a mediados de septiembre el valle es un lago; en noviembre queda el limo; en enero verdea y en abril está en espiga.
            assertTrue("$anio", EscenaEgipto.rio(momento("$anio-06-15")) < 0.05f); assertTrue("$anio", EscenaEgipto.verdor(momento("$anio-06-15")) < 0.05f); assertEquals("$anio", 0f, EscenaEgipto.maduro(momento("$anio-06-15")), 0.01f)
            assertTrue("$anio", EscenaEgipto.rio(momento("$anio-09-15")) > 0.95f)
            assertTrue("$anio", EscenaEgipto.rio(momento("$anio-08-20")) > EscenaEgipto.rio(momento("$anio-07-26"))); assertTrue("$anio", EscenaEgipto.rio(momento("$anio-11-20")) < EscenaEgipto.rio(momento("$anio-10-01")))
            assertTrue("$anio", EscenaEgipto.limo(momento("$anio-11-05")) > 0.3f); assertTrue("$anio", EscenaEgipto.limo(momento("$anio-06-15")) < 0.01f)
            assertTrue("$anio", EscenaEgipto.verdor(momento("$anio-01-15")) > 0.9f); assertTrue("$anio", EscenaEgipto.verdor(momento("$anio-12-25")) > 0.9f)
            assertTrue("$anio", EscenaEgipto.maduro(momento("$anio-04-01")) > 0.6f); assertTrue("$anio", EscenaEgipto.maduro(momento("$anio-01-15")) < 0.01f)
        }
    }

    @Test fun laTumbaAvanzaSeSellaYSeEmpiezaOtra() {
        cadaDia { d, m ->
            assertEquals("$d", m.ficha.arcos["tumba"]!!.toFloat().coerceIn(0f, 1f), EscenaEgipto.tumba(m), 0.0001f)
            assertTrue("$d", EscenaEgipto.montones(m) in 1..5)
            // Sellada la vieja y sin empezar la nueva, no hay andamio ni obra; con la nueva, la vieja sigue sellada.
            if (EscenaEgipto.tumbaNueva(m)) assertTrue("$d", EscenaEgipto.sellada(m))
            if (EscenaEgipto.sellada(m) && !EscenaEgipto.tumbaNueva(m)) { assertFalse("$d", EscenaEgipto.andamio(m)); for (h in listOf(7.0, 13.0, 21.0)) assertFalse("$d $h", EscenaEgipto.enObra(momento(d.toString(), h))) }
            if (!EscenaEgipto.sellada(m)) assertEquals("$d", !EscenaEgipto.entierro(m), EscenaEgipto.andamio(m))
        }
        // La de Seti II: tres montones al empezar la temporada 1 y cinco al acabarla; el andamio, hasta que la sellan.
        assertEquals(3, EscenaEgipto.montones(momento("2027-01-01"))); assertEquals(5, EscenaEgipto.montones(momento("2030-05-10")))
        assertTrue(EscenaEgipto.montones(momento("2029-06-01")) >= EscenaEgipto.montones(momento("2027-06-01")))
        assertFalse(EscenaEgipto.sellada(momento("2030-05-11"))); assertTrue(EscenaEgipto.sellada(momento("2030-05-12")))
        assertTrue(EscenaEgipto.andamio(momento("2030-05-10"))); assertFalse(EscenaEgipto.andamio(momento("2030-05-11"))); assertFalse(EscenaEgipto.andamio(momento("2030-05-12"))); assertFalse(EscenaEgipto.andamio(momento("2030-06-24")))
        // La de Siptah: empieza de cero el 25 de junio de la temporada 4, con un solo montón y sin andamio; después crece.
        assertFalse(EscenaEgipto.tumbaNueva(momento("2030-06-24"))); assertTrue(EscenaEgipto.tumbaNueva(momento("2030-06-25")))
        assertEquals(1, EscenaEgipto.montones(momento("2030-06-25"))); assertFalse(EscenaEgipto.andamio(momento("2030-06-25"))); assertTrue(EscenaEgipto.tumba(momento("2030-06-25")) < 0.02f)
        assertTrue(EscenaEgipto.andamio(momento("2031-06-01"))); assertTrue(EscenaEgipto.montones(momento("2031-12-30")) >= 2); assertTrue(EscenaEgipto.tumba(momento("2031-12-30")) > EscenaEgipto.tumba(momento("2030-12-30")))
        assertTrue(EscenaEgipto.enObra(momento("2030-07-10", 10.0))); assertTrue(EscenaEgipto.enObra(momento("2027-11-05", 10.0))); assertFalse(EscenaEgipto.enObra(momento("2027-11-05", 21.0)))
        // El entierro del rey y las dos visitas a la obra.
        assertTrue(EscenaEgipto.entierro(momento("2030-05-11"))); assertFalse(EscenaEgipto.entierro(momento("2030-05-10"))); assertFalse(EscenaEgipto.enObra(momento("2030-05-11", 10.0)))
        assertTrue(EscenaEgipto.visita(momento("2027-12-10"))); assertTrue(EscenaEgipto.visita(momento("2031-04-25"))); assertFalse(EscenaEgipto.visita(momento("2027-12-11")))
    }

    @Test fun mientrasLaCuadrillaDuermeEnElPasoLaAldeaQuedaSinLosHombres() {
        var dias = 0
        cadaDia { d, m ->
            assertEquals("$d", m.ficha.estado == "valle", EscenaEgipto.enElPaso(m))
            if (EscenaEgipto.enElPaso(m)) {
                dias++
                assertEquals("$d: solo pasa en la temporada 4", 4, m.ficha.temporada); assertFalse("$d", EscenaEgipto.sellada(m)); assertTrue("$d", EscenaEgipto.lumbreDelPaso(m))
                for (h in listOf(3.0, 7.0, 13.0, 19.0, 21.0)) { val a = momento(d.toString(), h)
                    assertFalse("$d $h: está en el paso, no en casa", EscenaEgipto.enCasa(a))
                    if (a.luces().noche > 0.3f) { assertTrue("$d $h", EscenaEgipto.lamparaDelPretil(a)); assertTrue("$d $h", EscenaEgipto.antorchasDeLaObra(a)) } else { assertFalse("$d $h", EscenaEgipto.lamparaDelPretil(a)); assertFalse("$d $h", EscenaEgipto.antorchasDeLaObra(a)) }
                    assertTrue("$d $h", LejosEgipto.encendidas(a) <= 0.5f)
                }
            } else for (h in listOf(7.0, 21.0)) { val a = momento(d.toString(), h); assertFalse("$d $h", EscenaEgipto.lamparaDelPretil(a)); assertFalse("$d $h", EscenaEgipto.antorchasDeLaObra(a)); assertFalse("$d $h", EscenaEgipto.lumbreDelPaso(a)) }
        }
        // Setenta días: de la muerte del rey (2 de marzo de la temporada 4) a su entierro (11 de mayo). Diez semanas: es una variante del mismo sitio.
        assertTrue("$dias días en el paso", dias in 68..72)
        assertFalse(EscenaEgipto.enElPaso(momento("2030-03-01"))); assertTrue(EscenaEgipto.enElPaso(momento("2030-03-02"))); assertTrue(EscenaEgipto.enElPaso(momento("2030-05-10"))); assertFalse(EscenaEgipto.enElPaso(momento("2030-05-12")))
        // Un día cualquiera, de noche, él está en casa y no hay lámpara en el pretil; en el paso, al revés, y hay menos luces en la aldea.
        assertTrue(EscenaEgipto.enCasa(momento("2030-02-20", 21.0))); assertFalse(EscenaEgipto.enCasa(momento("2030-04-01", 21.0)))
        assertTrue(LejosEgipto.encendidas(momento("2030-04-01", 21.0)) < LejosEgipto.encendidas(momento("2030-02-20", 21.0)))
    }

    @Test fun loQueElCuentoCambiaSeVeDesdeSuDia() {
        // Cada suceso de la casa: el día antes no, y desde su día sí.
        for ((antes, desde, que) in listOf<Triple<String, String, (Momento) -> Boolean>>(
            Triple("2027-04-14", "2027-04-15", EscenaEgipto::huyEscribe), Triple("2029-02-04", "2029-02-05", EscenaEgipto::tumbaDeTiy),
            Triple("2029-04-24", "2029-04-25", EscenaEgipto::juguete), Triple("2030-10-24", "2030-10-25", EscenaEgipto::telar), Triple("2031-08-24", "2031-08-25", EscenaEgipto::arca),
        )) { assertFalse(antes, que(momento(antes))); assertTrue(desde, que(momento(desde))); assertTrue(desde, que(momento("2031-12-30"))) }
        // La cuna se quita cuando Meryt camina: nunca están a la vez la cuna y el juguete.
        assertFalse(EscenaEgipto.cuna(momento("2028-05-04"))); assertTrue(EscenaEgipto.cuna(momento("2028-05-05"))); assertTrue(EscenaEgipto.cuna(momento("2029-04-24"))); assertFalse(EscenaEgipto.cuna(momento("2029-04-25")))
        cadaDia { d, m -> assertFalse("$d", EscenaEgipto.cuna(m) && EscenaEgipto.juguete(m)) }
        // Los días del escorpión no sube al Valle: a mediodía está en casa.
        assertFalse(EscenaEgipto.picado(momento("2027-05-24"))); assertTrue(EscenaEgipto.picado(momento("2027-05-25"))); assertTrue(EscenaEgipto.picado(momento("2027-05-31"))); assertFalse(EscenaEgipto.picado(momento("2027-06-01")))
        assertTrue(EscenaEgipto.enCasa(momento("2027-05-27", 13.0))); assertFalse(EscenaEgipto.enCasa(momento("2027-06-02", 13.0)))
        // Los sacos de la ración: cuatro desde que llega y dos semanas más; ninguno el día que el grano no llega; si no, uno o dos.
        val vistos = HashSet<Int>()
        cadaDia { d, m -> val n = EscenaEgipto.sacos(m); vistos += n; val md = d.toString().substring(5)
            if (hoy(m, "grano_tarde")) assertEquals("$d", 0, n) else if (md >= "11-28" && md <= "12-11") assertEquals("$d", 4, n) else assertTrue("$d", n in 1..2) }
        assertEquals(setOf(0, 1, 2, 4), vistos); assertEquals(0, EscenaEgipto.sacos(momento("2028-12-12"))); assertEquals(4, EscenaEgipto.sacos(momento("2027-11-28")))
    }

    @Test fun losDiasEspecialesSonLosDelCalendario() {
        val vistos = mutableMapOf<String, Int>()
        fun cuenta(que: String, si: Boolean) { if (si) vistos[que] = (vistos[que] ?: 0) + 1 }
        cadaDia { d, m ->
            assertEquals("$d", m.con("procesion"), EscenaEgipto.procesion(m)); assertEquals("$d", m.con("luto"), EscenaEgipto.luto(m))
            assertEquals("$d", m.con("fiesta") || m.con("procesion"), EscenaEgipto.fiesta(m))
            assertEquals("$d", m.con("polvo") || m.ficha.clima == Clima.POLVO, EscenaEgipto.polvo(m))
            if (EscenaEgipto.engalanada(m)) { assertTrue("$d", EscenaEgipto.fiesta(m)); assertFalse("$d", EscenaEgipto.entierro(m)) }
            // Los días de descanso nadie sube al Valle; con el viento del desierto no hay lumbre, ni toldo, ni obra.
            for (h in listOf(7.0, 13.0, 19.0, 21.0)) { val a = momento(d.toString(), h)
                if (EscenaEgipto.descanso(a)) assertFalse("$d $h", EscenaEgipto.enObra(a))
                if (EscenaEgipto.polvo(a)) { assertEquals("$d $h", 0f, EscenaEgipto.lumbre(a), 0f); assertFalse("$d $h", AzoteaEgipto.toldoTendido(a)); assertFalse("$d $h", EscenaEgipto.enObra(a)); assertFalse("$d $h", AzoteaEgipto.humoDeCocinas(a)) } else assertTrue("$d $h", AzoteaEgipto.toldoTendido(a))
                assertEquals("$d $h", EscenaEgipto.engalanada(a), EscenaEgipto.anclas(a).any { it.tipo == TipoAncla.ADORNO })
                assertTrue("$d $h", EscenaEgipto.lumbre(a) in 0f..1f); assertTrue("$d $h", LejosEgipto.encendidas(a) in 0f..1f)
            }
            cuenta("procesion", EscenaEgipto.procesion(m)); cuenta("luto", EscenaEgipto.luto(m)); cuenta("polvo", EscenaEgipto.polvo(m)); cuenta("torrente", EscenaEgipto.torrente(m)); cuenta("engalanada", EscenaEgipto.engalanada(m))
            cuenta("barca", EscenaEgipto.barcaDeAmon(m)); cuenta("amenhotep", EscenaEgipto.amenhotep(m)); cuenta("opet", EscenaEgipto.lucesDeTebas(m)); cuenta("lamparas", EscenaEgipto.lamparasDeLasTumbas(m))
        }
        for (que in listOf("procesion", "luto", "polvo", "torrente", "engalanada", "barca", "amenhotep", "opet", "lamparas")) assertTrue("nunca pasa: $que", (vistos[que] ?: 0) > 0)
        // Cada uno, en su día y no el de antes.
        assertTrue(EscenaEgipto.amenhotep(momento("2027-01-10"))); assertFalse(EscenaEgipto.amenhotep(momento("2027-01-09"))); assertTrue(EscenaEgipto.engalanada(momento("2027-01-10")))
        assertTrue(EscenaEgipto.barcaDeAmon(momento("2027-03-20"))); assertFalse(EscenaEgipto.barcaDeAmon(momento("2027-03-19"))); assertTrue(EscenaEgipto.lamparasDeLasTumbas(momento("2027-03-20")))
        // En la temporada 4 la Fiesta del Valle cae con la cuadrilla en el paso: hay lámparas en las tumbas, pero ni barca ni guirnaldas.
        assertTrue(EscenaEgipto.lamparasDeLasTumbas(momento("2030-03-17"))); assertFalse(EscenaEgipto.barcaDeAmon(momento("2030-03-17"))); assertFalse(EscenaEgipto.engalanada(momento("2030-03-17")))
        assertTrue(EscenaEgipto.polvo(momento("2027-05-10"))); assertTrue(EscenaEgipto.lucesDeTebas(momento("2027-08-02"))); assertFalse(EscenaEgipto.lucesDeTebas(momento("2027-08-01")))
        assertTrue(EscenaEgipto.lamparasDeLasTumbas(momento("2027-10-04"))); assertFalse(EscenaEgipto.engalanada(momento("2027-10-04"))); assertFalse(EscenaEgipto.lamparasDeLasTumbas(momento("2027-10-03")))
        assertTrue(EscenaEgipto.luto(momento("2029-02-05"))); assertFalse(EscenaEgipto.luto(momento("2029-02-04"))); assertTrue(EscenaEgipto.luto(momento("2030-03-02")))
        assertTrue(EscenaEgipto.torrente(momento("2031-11-20"))); assertFalse(EscenaEgipto.torrente(momento("2031-11-19")))
    }

    @Test fun lasSieteLasTreceYLasVeintiunaSonTresCuadros() {
        // Un día corriente, sin fiesta ni polvo: a las 7 y a las 13 él está en la obra (y hay polvo de cincel en la tumba); a las 21, en casa.
        val dia = "2027-11-05"; val m7 = momento(dia, 7.0); val m13 = momento(dia, 13.0); val m19 = momento(dia, 19.0); val m21 = momento(dia, 21.0)
        assertFalse(EscenaEgipto.polvo(m7)); assertFalse(EscenaEgipto.fiesta(m7))
        assertEquals(Hacer.TRABAJO, m7.hacer); assertEquals(Hacer.TRABAJO, m13.hacer); assertEquals(Hacer.GENTE, m19.hacer); assertEquals(Hacer.DORMIR, m21.hacer)
        assertTrue(EscenaEgipto.enObra(m7)); assertTrue(EscenaEgipto.enObra(m13)); assertFalse(EscenaEgipto.enObra(m21))
        assertFalse(EscenaEgipto.enCasa(m7)); assertFalse(EscenaEgipto.enCasa(m13)); assertTrue(EscenaEgipto.enCasa(m19)); assertTrue(EscenaEgipto.enCasa(m21))
        // El sol sale por el este (a la derecha, sobre el valle) y se pone por la montaña (a la izquierda); a las 21 es de noche.
        assertTrue(m7.luces().solX > 0.6f); assertTrue(m13.luces().solX < m7.luces().solX); assertTrue(m21.luces().noche > 0.6f); assertTrue(m7.luces().noche < 0.3f)
        // La lumbre arde a la hora de la cena y queda el rescoldo recién acostados; las lámparas, de noche mientras hay alguien despierto.
        assertEquals(0f, EscenaEgipto.lumbre(m7), 0f); assertEquals(0f, EscenaEgipto.lumbre(m13), 0f); assertEquals(1f, EscenaEgipto.lumbre(m19), 0f); assertTrue(EscenaEgipto.lumbre(m21) in 0.2f..0.5f); assertEquals(0f, EscenaEgipto.lumbre(momento(dia, 3.0)), 0f)
        assertFalse(EscenaEgipto.lamparas(m13)); assertTrue(EscenaEgipto.lamparas(m21)); assertFalse(EscenaEgipto.lamparas(momento(dia, 3.0)))
        assertTrue(LejosEgipto.encendidas(m21) > LejosEgipto.encendidas(momento(dia, 3.0))); assertEquals(0f, LejosEgipto.encendidas(m13), 0f)
        assertTrue(AzoteaEgipto.humoDeCocinas(momento(dia, 6.0))); assertFalse(AzoteaEgipto.humoDeCocinas(momento(dia, 15.0)))
        // Las noches de calor se duerme en la azotea; en invierno, no.
        assertTrue(EscenaEgipto.duermenArriba(momento("2027-06-20", 21.0)) || EscenaEgipto.polvo(momento("2027-06-20"))); assertFalse(EscenaEgipto.duermenArriba(momento("2027-01-20", 21.0))); assertFalse(EscenaEgipto.duermenArriba(momento("2027-06-20", 13.0)))
        var noches = 0; cadaDia { d, _ -> if (EscenaEgipto.duermenArriba(momento(d.toString(), 23.0))) { noches++; assertTrue("$d", d.monthValue in 4..10) } }
        assertTrue("casi nunca duermen arriba: $noches", noches > 700)
    }

    /**
     * Lo mismo que comprueba `GatoCuadroPrueba.cadaEscenaLeDaSueloYLuz` para las escenas registradas en `escenaDe`
     * (esta todavía no lo está) y lo de `EscenasPrueba.kt`: suelo a todas horas y bastante largo, lo plantado dentro
     * del tramo, el gato quieto fuera de lo plantado, del fuego y del objeto, y una luz válida. Aquí, todos los días.
     */
    @Test fun elGatoTieneSueloLuzYSusSitios() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaEgipto.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla
        cadaDia { d, _ ->
            for (h in listOf(3.0, 7.0, 13.0, 14.7, 17.0, 19.0, 20.5, 21.0, 23.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaEgipto.anclas(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo != TipoAncla.SUELO && a.tipo != TipoAncla.MIRADOR && a.tipo != TipoAncla.ADORNO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                val plantado = EscenaEgipto.estorbos(m)
                for (x in plantado) assertTrue("$d $h: lo plantado en $x no está en el suelo del gato", x in suelo.x..suelo.hasta)
                assertEquals((suelo.x + suelo.hasta) / 2f, gatoQuieto(suelo).x, 0.01f)
                val quieto = gatoQuieto(suelo, anclas, talla, plantado)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in plantado) assertTrue("$d $h: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= medio)
                assertTrue("$d $h: quieto se sale", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
                anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$d $h: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= medio) }
                // Solo se echa junto al hornillo encendido.
                assertEquals("$d $h", EscenaEgipto.lumbre(m) > 0f, anclas.any { it.tipo == TipoAncla.FUEGO })
                for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                    val luz = EscenaEgipto.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
                assertTrue("$d $h", AzoteaEgipto.calor(m, Offset(quieto.x, suelo.y)) in 0f..1f)
            }
        }
        // De día la lumbre no tiñe al gato; de noche, junto al hornillo encendido, sí.
        assertEquals(0f, AzoteaEgipto.calor(momento("2027-11-05", 13.0), AzoteaEgipto.LUMBRE), 0f); assertTrue(AzoteaEgipto.calor(momento("2027-11-05", 21.0), AzoteaEgipto.LUMBRE) > 0.15f)
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosEgipto.TODOS.toSortedSet(), delCalendario)
    }

    @Test fun algoSeMueveYElMismoMomentoDaLoMismo() {
        assertTrue(EscenaEgipto.seMueve(momento("2027-11-05", 19.0))); assertTrue(EscenaEgipto.seMueve(momento("2027-11-05", 21.0))); assertTrue(EscenaEgipto.seMueve(momento("2030-04-01", 23.0))); assertTrue(EscenaEgipto.seMueve(momento("2027-11-05", 10.0)))
        // De madrugada, un día corriente, nada se mueve: no hay que pedir cuadros.
        assertFalse(EscenaEgipto.seMueve(momento("2027-11-05", 3.0)))
        val a = momento("2027-12-20", 13.0); val b = momento("2027-12-20", 13.0)
        assertEquals(EscenaEgipto.rio(a), EscenaEgipto.rio(b), 0f); assertEquals(EscenaEgipto.sacos(a), EscenaEgipto.sacos(b)); assertEquals(EscenaEgipto.anclas(a).map { it.x to it.y }, EscenaEgipto.anclas(b).map { it.x to it.y }); assertEquals(EscenaEgipto.estorbos(a), EscenaEgipto.estorbos(b))
    }
}
