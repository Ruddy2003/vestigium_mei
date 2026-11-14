package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.DepositoVapor
import io.github.ruddy2003.vestigium.ui.escenas.EscenaVapor
import io.github.ruddy2003.vestigium.ui.escenas.EscenaVapor.Fuego
import io.github.ruddy2003.vestigium.ui.escenas.EscenaVapor.Maquina
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
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
 * El cuadro de Vapor victoriano (tanda S8) cuenta lo mismo que el cuento de Albert: cuál es su máquina, cómo va el
 * viaducto, si está de baja o ya es maquinista y lo que pasa cada día señalado salen de la ficha del día, y aquí se
 * comparan con su calendario (`AnioG6.kt`).
 */
class EscenaVaporPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "fogonero")!!, hora)
    private fun cadaDia(desde: Int = 2026, hacer: (LocalDate, FichaDelDia) -> Unit) { var d = LocalDate.of(desde, 1, 1); while (d.year <= 2032) { hacer(d, FichaDelDia.de(d.toString(), "fogonero")!!); d = d.plusDays(1) } }
    private fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f

    @Test fun laEscenaEsLaDelFogonero() {
        assertEquals(listOf("fogonero"), EscenaVapor.vidas)
        assertEquals("fogonero", EscenaVapor.vida("2027-05-10"))
    }

    @Test fun llevaLaMaquinaViejaHastaElDiaQueLesDanLaNueva() {
        // Temporada 1 (2027, y 2026 como avance): el 2 de febrero les dan «una máquina recién salida del taller, verde y brillante».
        assertTrue(momento("2027-02-02").ficha.especiales.any { it.dia.id == "maquina" })
        for (dia in listOf("2026-01-15", "2027-01-01", "2027-02-01")) assertEquals(dia, Maquina.VIEJA, EscenaVapor.maquina(momento(dia)))
        for (dia in listOf("2026-03-01", "2027-02-02", "2027-12-31", "2029-06-01", "2032-01-01")) assertEquals(dia, Maquina.VERDE, EscenaVapor.maquina(momento(dia)))
    }

    @Test fun elViaductoSubeConSuArcoYConLosCortesDelCuento() {
        // Antes del 3 de mayo de la temporada 1 no hay obra; el primer arco se cierra el 20 de agosto de la 2; se termina el 31 de mayo de la 4.
        assertFalse(EscenaVapor.hayObra(momento("2027-05-02"))); assertEquals(0f, EscenaVapor.pilar(momento("2027-05-02"), 0), 0f)
        assertTrue(EscenaVapor.hayObra(momento("2027-07-01"))); assertTrue(EscenaVapor.pilar(momento("2027-07-01"), 0) > 0f)
        assertEquals(0, EscenaVapor.arcosCerrados(momento("2028-08-19"))); assertEquals(1, EscenaVapor.arcosCerrados(momento("2028-08-20")))
        // Al cerrarse el primer arco ya están todas las pilas arriba.
        for (i in 0 until EscenaVapor.APOYOS) assertEquals(1f, EscenaVapor.pilar(momento("2028-08-20"), i), 0.001f)
        assertEquals(8, EscenaVapor.arcosCerrados(momento("2030-05-31"))); assertTrue(EscenaVapor.viaductoTerminado(momento("2030-05-31"))); assertTrue(EscenaVapor.viaductoTerminado(momento("2032-06-01")))
        assertFalse(EscenaVapor.viaductoTerminado(momento("2030-03-01")))
        // El día del primer tren, su máquina lo cruza: se ve durante el turno.
        assertTrue(momento("2030-06-01").ficha.especiales.any { it.dia.id == "primer_tren" }); assertTrue(EscenaVapor.trenEnElViaducto(momento("2030-06-01", 10.0)))
        // De 2027 a 2032 la obra nunca va a menos, y el cuadro usa los mismos cortes que el cuento (0.02 y 0.99).
        var antes = 0; var pilas = 0f
        cadaDia(2027) { d, f ->
            val m = Momento(f, 13.0); val a = f.arcos.getValue("viaducto")
            assertEquals("$d", a >= 0.02, EscenaVapor.hayObra(m)); assertEquals("$d", a >= 0.99, EscenaVapor.viaductoTerminado(m))
            val n = EscenaVapor.arcosCerrados(m); val suma = (0 until EscenaVapor.APOYOS).sumOf { EscenaVapor.pilar(m, it).toDouble() }.toFloat()
            assertTrue("$d: los arcos no se abren", n >= antes); assertTrue("$d: las pilas no bajan", suma >= pilas - 0.001f)
            if (!EscenaVapor.viaductoTerminado(m)) assertFalse("$d: sin viaducto no cruza ningún tren", EscenaVapor.trenEnElViaducto(m))
            antes = n; pilas = suma
        }
        assertEquals(8, antes)
    }

    @Test fun estaDeBajaUnMesYAlFinalEsMaquinista() {
        // Temporada 3 (2029): se quema el 20 de junio y vuelve el 20 de julio. Temporada 5 (2031): aprueba el 10 de septiembre.
        var baja = 0; var maquinista = 0
        cadaDia { d, f ->
            val m = Momento(f, 10.0)
            assertEquals("$d", f.estado == "baja", EscenaVapor.deBaja(m)); assertEquals("$d", f.estado == "maquinista", EscenaVapor.maquinista(m))
            if (EscenaVapor.deBaja(m)) { baja++; assertEquals("$d: la baja es de la temporada 3", 2029, d.year); assertFalse("$d: de baja, sus cosas no están en el arca", EscenaVapor.susCosas(m)) }
            else assertTrue("$d: en su turno, sus cosas están en el arca", EscenaVapor.susCosas(m))
            if (EscenaVapor.maquinista(m)) { maquinista++; assertTrue("$d", d >= LocalDate.of(2031, 9, 10)) }
            assertFalse("$d: de noche no hay nada suyo en el arca", EscenaVapor.susCosas(Momento(f, 21.0)))
        }
        assertEquals("los días de baja", 30, baja)
        assertTrue("maquinista desde que aprueba hasta fin de año (113 días), y otra vez cuando la temporada se repite: $maquinista", maquinista >= 113)
        assertTrue(EscenaVapor.deBaja(momento("2029-06-20"))); assertTrue(EscenaVapor.deBaja(momento("2029-07-19"))); assertFalse(EscenaVapor.deBaja(momento("2029-07-20")))
        assertFalse(EscenaVapor.maquinista(momento("2031-09-09"))); assertTrue(EscenaVapor.maquinista(momento("2031-09-10"))); assertTrue(EscenaVapor.maquinista(momento("2031-12-31")))
        // El día de la quemadura el grifo roto echa vapor; desde que vuelve, la máquina lleva el tubo nuevo.
        assertTrue(EscenaVapor.grifoRoto(momento("2029-06-20", 10.0))); assertFalse(EscenaVapor.grifoRoto(momento("2029-06-21", 10.0)))
        assertFalse(EscenaVapor.tuboNuevo(momento("2029-07-19"))); assertTrue(EscenaVapor.tuboNuevo(momento("2029-07-20"))); assertTrue(EscenaVapor.tuboNuevo(momento("2032-01-01")))
    }

    @Test fun elFuegoYLaFaenaSiguenElTurnoDeDoceHoras() {
        // La hora del cuadro va en pasos de veinte minutos: el turno, de 5:30 a 17:30, se ve desde las 5:40 hasta las 17:20.
        val dia = "2027-05-05"
        assertEquals(Fuego.DORMIDO, EscenaVapor.fuego(momento(dia, 2.0))); assertEquals(Fuego.ENCENDIENDO, EscenaVapor.fuego(momento(dia, 4.0)))
        for (h in listOf(5.7, 7.0, 13.0, 17.0)) assertEquals("$h", Fuego.A_PRESION, EscenaVapor.fuego(momento(dia, h)))
        for (h in listOf(17.7, 19.0, 21.0, 23.5)) assertEquals("$h", Fuego.DORMIDO, EscenaVapor.fuego(momento(dia, h)))
        assertTrue(EscenaVapor.aguada(momento(dia, 6.0))); assertTrue(EscenaVapor.aguada(momento(dia, 12.34))); assertFalse(EscenaVapor.aguada(momento(dia, 9.0)))
        assertTrue(EscenaVapor.limpieza(momento(dia, 17.0))); assertFalse(EscenaVapor.limpieza(momento(dia, 13.0)))
        // El hogar da más luz mientras lo encienden que dormido, y las señales solo dan vía libre durante el turno.
        assertTrue(EscenaVapor.lumbre(momento(dia, 4.0)) > EscenaVapor.lumbre(momento(dia, 13.0))); assertTrue(EscenaVapor.lumbre(momento(dia, 13.0)) > EscenaVapor.lumbre(momento(dia, 23.0)))
        for (h in listOf(0.0, 3.0, 18.0, 22.0)) for (i in 0..1) assertFalse("$h", DepositoVapor.viaLibre(momento(dia, h), i))
        // La taberna tiene luz al salir del turno.
        assertTrue(EscenaVapor.taberna(momento(dia, 20.0))); assertFalse(EscenaVapor.taberna(momento(dia, 13.0))); assertFalse(EscenaVapor.taberna(momento(dia, 23.5)))
    }

    @Test fun laNocheDeLaNevadaLaMaquinaNoEstaEnElDeposito() {
        // Temporada 1: el 8 de diciembre «una nevada deja su tren atascado toda la noche en una trinchera de la línea».
        assertTrue(momento("2027-12-08").ficha.especiales.any { it.dia.id == "nieve" }); assertEquals(Clima.NIEVE, momento("2027-12-08").ficha.clima)
        assertTrue(EscenaVapor.enElDeposito(momento("2027-12-08", 10.0)))
        for (h in listOf(15.0, 21.0, 2.0)) { val m = momento("2027-12-08", h); assertFalse("$h", EscenaVapor.enElDeposito(m)); assertEquals("$h: sin máquina no hay lumbre", 0f, EscenaVapor.lumbre(m), 0f) }
        var faltas = 0
        cadaDia { _, f -> if (!EscenaVapor.enElDeposito(Momento(f, 21.0))) faltas++ }
        assertEquals("solo falta esa noche (y su repetición en el año de avance)", 2, faltas)
    }

    @Test fun losDiasSenaladosSeVen() {
        // Las cuatro fiestas de los bancos de cada año son de excursión; la gala de los mineros lleva además sus estandartes.
        for (dia in listOf("2027-03-29", "2027-05-17", "2027-07-10", "2027-08-02", "2029-04-10")) assertTrue(dia, EscenaVapor.excursion(momento(dia)))
        assertTrue(EscenaVapor.gala(momento("2027-07-10"))); assertFalse(EscenaVapor.gala(momento("2027-08-02")))
        assertTrue(EscenaVapor.hogueras(momento("2027-11-05"))); assertTrue(EscenaVapor.fiesta(momento("2027-11-05"))); assertTrue(EscenaVapor.navidad(momento("2028-12-25"))); assertTrue(EscenaVapor.navidad(momento("2028-12-26")))
        assertTrue("la boda", EscenaVapor.fiesta(momento("2029-01-28")))
        assertTrue(EscenaVapor.luto(momento("2030-06-17"))); assertTrue(EscenaVapor.luto(momento("2031-07-17"))); assertFalse(EscenaVapor.luto(momento("2030-06-18")))
        assertTrue(EscenaVapor.vendaval(momento("2028-10-14"))); assertTrue(EscenaVapor.helada(momento("2030-01-20"))); assertTrue(EscenaVapor.descarrilado(momento("2028-11-22"))); assertTrue(EscenaVapor.elecciones(momento("2027-04-03")))
        // Los atardeceres rojos: desde el 28 de noviembre de la temporada 4 y un mes.
        assertFalse(EscenaVapor.krakatoa(momento("2030-11-27"))); assertTrue(EscenaVapor.krakatoa(momento("2030-11-28"))); assertTrue(EscenaVapor.krakatoa(momento("2030-12-28"))); assertFalse(EscenaVapor.krakatoa(momento("2030-12-29"))); assertFalse(EscenaVapor.krakatoa(momento("2031-11-28")))
        // Su casa de la calle de los ferroviarios, desde que se mudan (14 de abril de la temporada 4).
        assertFalse(EscenaVapor.suCasa(momento("2030-04-13"))); assertTrue(EscenaVapor.suCasa(momento("2030-04-14"))); assertTrue(EscenaVapor.suCasa(momento("2032-01-01")))
        // El día señalado no cambia lo demás: sigue siendo su máquina y sigue el viaducto.
        assertEquals(Maquina.VERDE, EscenaVapor.maquina(momento("2027-07-10")))
    }

    @Test fun laLuzDelLugarEsValidaATodasHorasConTodoTiempo() {
        val muestras = listOf(Color.White, Color.Black, Color(0xFF2F6A46), Color(0xFF9A4E3A))
        for (dia in listOf("2027-05-05", "2030-12-05", "2027-11-05")) for (clima in Clima.entries) for (paso in 0 until 72) {
            val m = Momento(FichaDelDia.de(dia, "fogonero")!!.copy(clima = clima), paso / 3.0)
            // Si un color se saliera de 0 a 1, `Color(...)` lanzaría aquí mismo.
            val l = EscenaVapor.luces(m); val r = EscenaVapor.rayo(l)
            (listOf(l.alto, l.medio, l.bajo, l.ambiente, l.bruma, l.solColor) + muestras.flatMap { listOf(l.tono(it), l.tono(it, 0.6f)) }).forEach { assertTrue("$dia $clima $paso", valido(it)) }
            assertTrue(r.fuerza in 0f..1f && r.contraste in 0f..1f && l.brumaFuerza in 0f..1f)
            val focos = EscenaVapor.focos(m)
            assertEquals("$dia $clima $paso: los faroles se encienden de noche", l.noche > 0.2f, focos.isNotEmpty())
            focos.forEach { assertTrue(it.fuerza in 0f..1.2f && it.alcance > 0f && it.p.x in 0f..ANCHO && it.p.y in 0f..ALTO) }
        }
        // De noche hay al menos las dos farolas de gas; con el hogar encendiéndose, también su lumbre.
        assertTrue(EscenaVapor.focos(momento("2027-05-05", 23.0)).size >= 3); assertTrue(EscenaVapor.focos(momento("2027-05-05", 13.0)).isEmpty())
    }

    @Test fun elGatoTieneSueloLuzYSusAnclasATodasHoras() {
        // Lo mismo que `GatoCuadroPrueba.cadaEscenaLeDaSueloYLuz` comprueba para las escenas registradas, para esta.
        val escena = EscenaVapor
        assertTrue("la talla del gato", escena.tallaDelGato in 0.8f..1.8f)
        for (dia in listOf("2027-01-20", "2027-05-05", "2028-08-21", "2029-03-18", "2029-06-25", "2030-11-10", "2027-12-08", "2027-07-10", "2031-11-05")) for (hora in listOf(3.0, 4.7, 7.0, 13.0, 14.7, 17.0, 20.5, 23.0)) {
            val m = momento(dia, hora); val anclas = escena.anclas(m)
            val suelo = anclas.firstOrNull { it.tipo == TipoAncla.SUELO }
            assertTrue("$dia $hora: no hay suelo para el gato", suelo != null)
            assertTrue("el suelo es muy corto para caminar", suelo!!.hasta - suelo.x >= 2 * Gatuno.MEDIO_LARGO * escena.tallaDelGato + 60f)
            assertTrue("el gato no cabe de alto en el cuadro", suelo.y - Gatuno.ALTO * escena.tallaDelGato > 150f && suelo.y <= 285f)
            anclas.forEach { a -> assertTrue("$dia $hora: ancla ${a.tipo} fuera del cuadro", a.x in 0f..ANCHO && a.hasta in 0f..ANCHO && a.y in 0f..ALTO) }
            assertEquals("$dia $hora: el gato solo se arrima al brasero si arde", EscenaVapor.brasero(m), anclas.any { it.tipo == TipoAncla.FUEGO })
            assertEquals("$dia $hora: el adorno, los días de fiesta y de excursión", EscenaVapor.fiesta(m) || EscenaVapor.excursion(m), anclas.any { it.tipo == TipoAncla.ADORNO })
            assertEquals(1, anclas.count { it.tipo == TipoAncla.MIRADOR }); assertEquals(1, anclas.count { it.tipo == TipoAncla.OBJETO }); assertEquals(1, anclas.count { it.tipo == TipoAncla.ABRIGO })
            val plantado = escena.estorbos(m)
            val quieto = gatoQuieto(suelo, anclas, escena.tallaDelGato, plantado)
            plantado.forEach { x -> assertTrue("$dia $hora: lo plantado no está en el suelo del gato", x in suelo.x..suelo.hasta) }
            plantado.forEach { x -> assertTrue("$dia $hora: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= Gatuno.MEDIO_LARGO * escena.tallaDelGato) }
            assertEquals(AnimGato.SENTADO, quieto.anim)
            assertTrue("$dia $hora: quieto se sale de su tramo", quieto.x - Gatuno.MEDIO_LARGO * escena.tallaDelGato >= suelo.x - 0.01f && quieto.x + Gatuno.MEDIO_LARGO * escena.tallaDelGato <= suelo.hasta + 0.01f)
            anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$dia $hora: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= Gatuno.MEDIO_LARGO * escena.tallaDelGato) }
            for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                val luz = escena.luzDelGato(m, Offset(x, suelo.y))
                for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$dia $hora: un color del gato no es válido", valido(luz.tono(c)))
                assertTrue("$dia $hora: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
            }
        }
    }

    @Test fun ningunDiaElCuadroContradiceALaFicha() {
        var conTren = 0; var dias = 0; var conBrasero = 0
        cadaDia { d, f ->
            for (hora in listOf(3.0, 7.0, 10.0, 13.0, 17.0, 19.0, 21.0)) {
                val m = Momento(f, hora)
                assertEquals("$d $hora", f.temporada > 1 || d.toString().substring(5) >= "02-02", EscenaVapor.maquina(m) == Maquina.VERDE)
                assertEquals("$d $hora: el turno va de 5:30 a 17:30", hora in 5.5..17.49, EscenaVapor.turno(m))
                assertEquals("$d $hora", f.especiales.any { it.dia.escena == "excursion" }, EscenaVapor.excursion(m))
                assertEquals("$d $hora", f.especiales.any { it.dia.escena == "luto" }, EscenaVapor.luto(m))
                if (EscenaVapor.grifoRoto(m)) assertTrue("$d: el grifo solo revienta el día de la quemadura", f.especiales.any { it.dia.id == "quemadura" })
                if (f.clima == Clima.TORMENTA) assertFalse("$d $hora: con tormenta el brasero no arde", EscenaVapor.brasero(m))
                assertTrue("$d $hora", EscenaVapor.viento(m) in 0.2f..1f)
                assertEquals(1, EscenaVapor.estorbos(m).size)
            }
            dias++; if (EscenaVapor.trenEnElViaducto(Momento(f, 13.0))) conTren++; if (EscenaVapor.brasero(Momento(f, 7.0))) conBrasero++
            // El mismo día y la misma hora dan siempre lo mismo.
            assertEquals(EscenaVapor.trenEnElViaducto(Momento(f, 13.0)), EscenaVapor.trenEnElViaducto(Momento(f, 13.2)))
        }
        assertTrue("pasan trenes por el viaducto, pero no siempre: $conTren de $dias", conTren in 150..500)
        assertTrue("el brasero arde las mañanas frías, no todo el año: $conBrasero de $dias", conBrasero in 400..(dias - 400))
    }

    @Test fun elVientoSoplaHaciaLaIzquierdaYElVendavalSeNota() {
        // El humo se va hacia la izquierda, como las ráfagas de `tiempoVivo`; el día del vendaval (14 de octubre de la temporada 2) sopla de verdad.
        var ventarrones = 0
        cadaDia { d, f -> val m = Momento(f, 13.0)
            assertEquals("$d", -EscenaVapor.viento(m), EscenaVapor.soplo(m), 0f)
            if (f.clima == Clima.VIENTO || f.especiales.any { it.dia.escena == "viento" }) assertTrue("$d: con viento, ventarrón", EscenaVapor.ventarron(m))
            if (f.clima == Clima.DESPEJADO && f.especiales.none { it.dia.escena == "viento" }) assertFalse("$d: un día despejado no es de ventarrón", EscenaVapor.ventarron(m))
            if (EscenaVapor.ventarron(m)) ventarrones++ }
        assertTrue(EscenaVapor.ventarron(momento("2028-10-14"))); assertTrue("$ventarrones", ventarrones in 1..1200)
        // El hogar da más luz al encenderse que a presión, y más a presión que dormido; nunca tanta que queme el cuadro.
        val dia = "2027-05-05"
        assertTrue(EscenaVapor.lumbre(momento(dia, 4.0)) <= 1f && EscenaVapor.lumbre(momento(dia, 23.0)) >= 0.4f)
    }

    @Test fun elCuadroQuietoSoloCambiaConElSelloYLaParteVivaSiempreSeMueve() {
        assertTrue(EscenaVapor.seMueve(momento("2027-05-05", 3.0)))
        assertEquals(momento("2027-05-05", 13.0).sello, momento("2027-05-05", 13.2).sello)
        // Tres horas, tres cuadros: el fuego, las luces y lo que hay en el arca cambian.
        val a = momento("2027-05-05", 7.0); val b = momento("2027-05-05", 13.0); val c = momento("2027-05-05", 21.0)
        assertTrue(EscenaVapor.focos(a).isEmpty() && EscenaVapor.focos(b).isEmpty() && EscenaVapor.focos(c).isNotEmpty())
        assertTrue(EscenaVapor.fuego(a) == Fuego.A_PRESION && EscenaVapor.fuego(c) == Fuego.DORMIDO)
        assertTrue(EscenaVapor.susCosas(b) && !EscenaVapor.susCosas(c))
    }
}
