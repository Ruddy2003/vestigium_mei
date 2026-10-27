package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ciencia.Campanas
import io.github.ruddy2003.vestigium.data.AvisosAnimo
import io.github.ruddy2003.vestigium.data.AvisosDia
import io.github.ruddy2003.vestigium.data.Campana
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Especiales
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Experimentos
import io.github.ruddy2003.vestigium.data.FechaPropia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Perfil
import io.github.ruddy2003.vestigium.data.Persona
import io.github.ruddy2003.vestigium.data.Reglas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Lo que la entrega 10b empezó a guardar (v13): avisos, campañas, experimentos, la hora de la valencia y los días especiales. */
class DatosNuevosPrueba {
    @Test fun avisosRespondidos() {
        val a = AvisosDia("2026-10-27", planeados = 4, vencidos = listOf("t1", "t2", "t3"), mostrados = listOf("t1", "t2"))
        val m = listOf(
            Momento("1", "x", "2026-10-27", "muestreo", aviso_ts = "t1"),            // sin valencia también cuenta
            Momento("2", "x", "2026-10-27", "muestreo", valencia = 5, aviso_ts = "t3"), // respondió uno que no se mostró
            Momento("3", "x", "2026-10-27", "animo", valencia = 4))                   // registro propio: no es respuesta
        val c = AvisosAnimo.cifras(a, AvisosAnimo.respondidos(m))
        assertEquals(4.0, c["avisos_planeados"]); assertEquals(2.0, c["avisos_mostrados"]); assertEquals(1.0, c["avisos_respondidos"])
        assertEquals(0.5, c["tasa_respuesta"]!!, 1e-9)
        assertNull(AvisosAnimo.cifras(AvisosDia("2026-10-28", planeados = 3), emptySet())["tasa_respuesta"])
    }

    @Test fun campanasPorDia() {
        val l = listOf(
            Campana("a", "2026-10-19", "sueno7", 5),                                        // de antes: cuenta la semana entera
            Campana("b", "2026-10-26", "pasos", 5, aceptada = "2026-10-28"),                // aceptada el miércoles
            Campana("c", "2026-10-26", "habito:leer", 5, aceptada = "2026-10-26", abandonada = "2026-10-27"))
        val d = Campanas.diasActivos(l, hoy = "2026-10-30")
        assertEquals(setOf("sueno7"), d["2026-10-25"])
        assertEquals(setOf("habito:leer"), d["2026-10-27"])
        assertNull(d["2026-10-31"])                       // después de hoy no se cuenta
        assertEquals(setOf("pasos"), d["2026-10-30"])
        assertFalse(d.containsKey("2026-10-18"))
        assertEquals("campana_hab_leer", Campanas.variable("habito:leer"))
        assertTrue(Campanas.obvia("campana_pasos", "pasos")); assertTrue(Campanas.obvia("campana_hab_leer", "hab_leer"))
        assertFalse(Campanas.obvia("campana_pasos", "valencia")); assertTrue(Campanas.obvia("campanas_n", "campana_pasos"))
    }

    @Test fun experimentoTerminadoYCumplimiento() {
        // Lunes 7 de septiembre; semanas SÍ, no, SÍ, no; terminado el domingo de la semana 2.
        val ini = "2026-09-07"
        val hecho = (0 until 7).associate { Fechas.sumar(ini, it.toLong()) to (it < 5) }.filterKeys { it != Fechas.sumar(ini, 6) } +   // SÍ: 5 sí, 1 no, 1 sin respuesta
            (7 until 14).associate { Fechas.sumar(ini, it.toLong()) to (it == 7) }.filterKeys { it != Fechas.sumar(ini, 13) }            // no: 1 sí, 5 no, 1 sin respuesta
        val e = Experimento("e", "Prueba", "acción", "valencia", ini, 4, listOf(true, false, true, false), fin = Fechas.sumar(ini, 13), hecho = hecho)
        val panel = (0 until 28).associate { i -> Fechas.sumar(ini, i.toLong()) to mapOf<String, Double?>("valencia" to (if ((i / 7) % 2 == 0) 5.0 else 3.0) + (i % 2) * 0.5) }
        val r = Experimentos.resultado(e, panel, hoy = "2026-10-30")
        assertEquals(7, r.con.size); assertEquals(7, r.sin.size)     // las semanas 3 y 4 no cuentan: se terminó antes
        val c = r.cumplimiento!!
        assertEquals(5, c.hechoSi); assertEquals(6, c.respSi); assertEquals(1, c.hechoNo); assertEquals(6, c.respNo); assertEquals(2, c.sinRespuesta)
        assertEquals(r.dif!!.first / (5.0 / 6 - 1.0 / 6), r.cociente!!, 1e-9)
        assertEquals(Fechas.sumar(ini, 13), Experimentos.ultimoDia(e))
        assertFalse(Experimentos.enCurso(e, "2026-09-15"))
    }

    @Test fun valenciaRetroactiva() {
        // Horas locales, para que la prueba no dependa de la zona del equipo donde corre.
        fun local(d: Int, h: Int, m: Int = 0) = java.time.LocalDateTime.of(2026, 10, d, h, m).atZone(Fechas.zona()).toOffsetDateTime().toString()
        val hoyMas1 = local(28, 10)
        // El día lo creó otra cosa (un hábito) el mismo día; la valencia llegó al día siguiente: es retroactiva.
        val previo = Dia("2026-10-27", habitos = mapOf("leer" to true), retroactivo = false)
        val d = previo.copy(valencia = 5)
        val ts = Reglas.tsValencia(d, previo, hoyMas1)
        assertEquals(hoyMas1, ts)
        assertTrue(Reglas.retroactivo("2026-10-27", ts, previo.retroactivo, "2026-10-28"))
        // La misma valencia, guardada de nuevo, conserva su hora.
        assertEquals(hoyMas1, Reglas.tsValencia(d.copy(ts_valencia = hoyMas1), d.copy(ts_valencia = hoyMas1), local(30, 10)))
        // Llenada el mismo día lógico (a la 1 de la mañana sigue siendo el día anterior): no es retroactiva.
        assertFalse(Reglas.retroactivo("2026-10-27", local(28, 1, 30), null, "2026-10-28"))
        // Días de antes de la v13 sin hora: conservan lo que tenían.
        assertTrue(Reglas.retroactivo("2026-10-01", null, true, "2026-10-28"))
    }

    @Test fun diasEspeciales() {
        assertEquals("1962-03-15", Especiales.leer("15/03/1962")); assertEquals("--03-15", Especiales.leer("15/3"))
        assertEquals("", Especiales.leer("  ")); assertNull(Especiales.leer("31/02")); assertEquals("--02-29", Especiales.leer("29/02"))
        assertEquals(64, Especiales.edad("1962-03-15", "2026-10-27")); assertEquals(63, Especiales.edad("1962-11-15", "2026-10-27"))
        assertNull(Especiales.edad("--03-15", "2026-10-27"))
        // 29 de febrero en un año que no es bisiesto: cae el 28.
        assertTrue(Especiales.cae("--02-29", "2027-02-28")); assertFalse(Especiales.cae("--02-29", "2028-02-28"))
        val c = Config(perfil = Perfil(nacimiento = "1996-10-27"),
            personas = listOf(Persona("ana", "familia", nacimiento = "--10-27"), Persona("luis", "familia", nacimiento = "1940-01-02", fallecimiento = "2020-10-27")),
            fechas_propias = listOf(FechaPropia("f", "Llegué a la ciudad", "2016-10-27", "aniversario")))
        val l = Especiales.de("2026-10-27", c)
        assertEquals(listOf("mi_cumpleanos", "cumpleanos", "fallecimiento", "aniversario"), l.map { it.tipo })
        assertEquals(30, l[0].anios); assertNull(l[1].anios); assertEquals(6, l[2].anios); assertEquals(10, l[3].anios)
        assertEquals(80, Especiales.edadDe(c.personas[1]))   // la que tenía al morir
    }
}
