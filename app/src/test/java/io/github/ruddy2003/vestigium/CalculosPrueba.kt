package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ciencia.Analisis
import io.github.ruddy2003.vestigium.ciencia.Dinamicas
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Experimentos
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Macrofactor
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Recaidas
import io.github.ruddy2003.vestigium.data.RedPersonas
import io.github.ruddy2003.vestigium.data.Serie
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.data.Texto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Los cálculos que corrigió la entrega 10c (P-14 a P-29, I-1 e I-2), con datos inventados. */
class CalculosPrueba {
    private fun dia(i: Int, desde: String = "2026-01-01") = Fechas.sumar(desde, i.toLong())

    // ---- P-14: cada día de un cuestionario, con la primera respuesta que lo cubre.
    @Test fun cuestionarioPorSemana() {
        val r = listOf("2026-10-01" to 4.0, "2026-10-08" to 6.0, "2026-10-15" to 9.0, "2026-10-22" to 5.0, "2026-10-29" to 7.0)
        val m = Dinamicas.porDiaCuestionario(r, 7, "2026-10-30")
        assertEquals(4.0, m["2026-10-01"]!!, 0.0); assertEquals(4.0, m["2026-09-25"]!!, 0.0); assertNull(m["2026-09-24"])
        assertEquals(6.0, m["2026-10-05"]!!, 0.0); assertEquals(9.0, m["2026-10-12"]!!, 0.0); assertEquals(5.0, m["2026-10-20"]!!, 0.0)
        assertEquals(7.0, m["2026-10-29"]!!, 0.0); assertNull(m["2026-10-30"])
        // Con la ventana de un mes, un día cubierto por varias respuestas se queda con la primera, no con la última.
        assertEquals(6.0, Dinamicas.porDiaCuestionario(r, 30, "2026-10-30")["2026-10-05"]!!, 0.0)
        assertEquals(7L, Dinamicas.ventanaCuestionario("pss4")); assertEquals(30L, Dinamicas.ventanaCuestionario("ucla3"))
    }

    // ---- P-15: «no hubo» solo desde el primer registro.
    @Test fun ceroDesdeElPrimero() {
        assertEquals(listOf("2026-10-05", "2026-10-09"), Estadistica.ceroDesde(listOf("2026-10-07", "2026-10-05"), listOf("2026-10-01", "2026-10-05", "2026-10-09")))
        assertTrue(Estadistica.ceroDesde(emptyList(), listOf("2026-10-01")).isEmpty())
    }

    // ---- P-17: la t de Student y la diferencia de medias.
    @Test fun cuantilesDeT() {
        assertEquals(12.706, Estadistica.cuantilT(0.975, 1.0), 0.001)
        assertEquals(4.303, Estadistica.cuantilT(0.975, 2.0), 0.001)
        assertEquals(2.571, Estadistica.cuantilT(0.975, 5.0), 0.001)
        assertEquals(2.042, Estadistica.cuantilT(0.975, 30.0), 0.001)
        assertEquals(-2.571, Estadistica.cuantilT(0.025, 5.0), 0.001)
        assertEquals(0.0734, Estadistica.pT(2.0, 10.0), 0.0005)
    }

    @Test fun diferenciaConWelch() {
        // Medias 2 y 5, varianzas 1: error estándar 0.816, 4 grados de libertad, t = 2.776.
        val d = Estadistica.diferenciaCompleta(listOf(1.0, 2.0, 3.0), listOf(4.0, 5.0, 6.0))!!
        assertEquals(-3.0, d.d, 1e-9); assertEquals(4.0, d.gl, 1e-6)
        assertEquals(-5.267, d.lo, 0.002); assertEquals(-0.733, d.hi, 0.002)
        // Con autocorrelación, el intervalo se ensancha.
        val c = Estadistica.diferenciaCompleta(listOf(1.0, 2.0, 3.0, 2.0), listOf(4.0, 5.0, 6.0, 5.0), 0.5, 0.5)!!
        val s = Estadistica.diferenciaCompleta(listOf(1.0, 2.0, 3.0, 2.0), listOf(4.0, 5.0, 6.0, 5.0))!!
        assertTrue(c.hi - c.lo > s.hi - s.lo)
    }

    @Test fun coberturaConDiasParecidos() {
        // 30 días contra los 30 anteriores, sin cambio real y con autocorrelación 0.5: el intervalo de 95 % debe cubrir el cero
        // entre el 93 y el 97 % de las veces (sin la corrección, alrededor del 74 %).
        val azar = java.util.Random(2026)
        val fechas = (0 until 60).map { dia(it) }
        var cubre = 0; val n = 2000
        repeat(n) {
            var v = azar.nextGaussian() / kotlin.math.sqrt(1 - 0.25)
            val x = (0 until 60).map { v = 0.5 * v + azar.nextGaussian(); v }
            val b = (0 until 30).map { fechas[it] to x[it] }; val a = (30 until 60).map { fechas[it] to x[it] }
            val d = Estadistica.diferenciaEnDias(a, b)!!
            if (d.lo <= 0 && d.hi >= 0) cubre++
        }
        val p = cubre.toDouble() / n
        assertTrue("cobertura $p", p in 0.93..0.97)
    }

    // ---- P-18: experimentos.
    private fun exp(plan: List<Boolean>, lavado: Int = 0, diseno: String = "alterno", inicio: String = "2026-10-05") =
        Experimento("e", "x", "acción", "valencia", inicio, plan.size, plan, diseno = diseno, lavado_dias = lavado)

    @Test fun creatinaDaResultado() {
        val e = exp(Experimentos.plan("antes_despues", 12), 28, "antes_despues")
        assertEquals(14 to 42, Experimentos.diasPorCondicion(e))
        // Con 8 semanas y 28 días de lavado no quedaba ningún día con la acción.
        assertEquals(0, Experimentos.minimoPosible("antes_despues", 8, 28))
        assertTrue(Experimentos.minimoPosible("antes_despues", 12, 28) >= Experimentos.MIN_DIAS)
        assertTrue(Experimentos.minimoPosible("alterno", 6, 1) >= Experimentos.MIN_DIAS)
        assertTrue(Experimentos.minimoPosible("alterno", 6, 7) < Experimentos.MIN_DIAS)
    }

    @Test fun sorteoPorPares() {
        repeat(200) { s ->
            val p = Experimentos.plan("alterno", 12, kotlin.random.Random(s))
            assertEquals(12, p.size)
            p.chunked(2).forEach { par -> assertEquals(1, par.count { it }) }
            // Nunca más de dos semanas iguales seguidas.
            assertFalse(p.windowed(3).any { w -> w.all { it } || w.none { it } })
        }
    }

    @Test fun hoyNoEntraYMientrasDuraEsProvisional() {
        val e = exp(listOf(true, false, true, false))
        val hoy = "2026-10-20"
        val panel = (0 until 28).associate { i -> dia(i, "2026-10-05").let { f -> f to mapOf<String, Double?>("valencia" to if (f == hoy) 100.0 else 4.0 + (i % 7) * 0.1) } }
        val r = Experimentos.resultado(e, panel, hoy)
        assertTrue(r.con.none { it == 100.0 })
        assertEquals(7 + 1, r.con.size)
        assertTrue(r.provisional)
        assertFalse(Experimentos.resultado(e.copy(fin = "2026-10-18", activo = false), panel, hoy).provisional)
    }

    @Test fun tendenciaDentroDeCadaCondicion() {
        // Con efecto (+1 con la acción) y sin tendencia, en un plan en bloque: la pendiente común de valor ~ día + condición es casi 0.
        val serie = (0 until 28).map { i -> val c = if (i < 14) 1.0 else 0.0; Triple(i.toDouble(), c, 5 + c + 0.2 * kotlin.math.sin(i * 1.7)) }
        val (b, clara) = Experimentos.tendencia(serie)
        assertTrue(kotlin.math.abs(b!!) < 0.05); assertFalse(clara)
        // Con una tendencia real, sí.
        val conTendencia = serie.map { (t, c, v) -> Triple(t, c, v + 0.1 * t) }
        assertTrue(Experimentos.tendencia(conTendencia).second)
    }

    @Test fun planEnBloque() {
        assertTrue(Experimentos.enBloque(exp(listOf(true, true, true, false, false, false))))
        assertFalse(Experimentos.enBloque(exp(listOf(true, false, false, true, true, false))))
        assertTrue(Experimentos.enBloque(exp(listOf(false, false, true, true), diseno = "antes_despues")))
    }

    // ---- P-19: el ritmo con las noches recientes.
    @Test fun ritmoConLasNochesRecientes() {
        fun noches(desde: String, n: Int, finde: Double) = (0 until n).map { i ->
            val f = dia(i, desde); val libre = Fechas.esDiaLibre(f, false)
            // Laborales: de 23:00 a 7:00 (punto medio 3:00). Libres: el punto medio se mueve [finde] horas.
            Analisis.Noche(f, 23.0 + if (libre) finde else 0.0, 31.0 + if (libre) finde else 0.0, true)
        }
        val todas = noches("2026-05-01", 60, 3.0) + noches("2026-08-31", 60, 1.0)
        val hoy = "2026-10-29"
        assertEquals(2.0, Analisis.ritmoDe(todas, emptyList(), false)!!.jetLag!!, 0.3)
        assertEquals(1.0, Analisis.ritmoDe(Analisis.recientes(todas, hoy, 56), emptyList(), false)!!.jetLag!!, 1e-9)
        assertEquals("mezcla", Analisis.ritmoDe(noches("2026-10-01", 3, 1.0), noches("2026-10-10", 10, 1.0).map { it.copy(reloj = false) }, false)!!.fuente)
    }

    // ---- I-1: festivos de México.
    @Test fun festivos() {
        listOf("2025-01-01", "2025-02-03", "2025-03-17", "2025-05-01", "2025-09-16", "2025-11-17", "2025-12-25", "2024-10-01", "2018-12-01",
            "2027-02-01", "2005-02-05", "2005-03-21", "2005-11-20").forEach { assertTrue(it, Fechas.esFestivoMx(it)) }
        listOf("2025-02-05", "2025-03-21", "2025-04-17", "2025-04-18", "2024-12-01", "2025-10-01", "2018-10-01").forEach { assertFalse(it, Fechas.esFestivoMx(it)) }
        assertTrue(Fechas.esDiaLibre("2025-02-03", true)); assertFalse(Fechas.esDiaLibre("2025-02-03", false))
        assertTrue(Fechas.esDiaLibre("2025-02-08", false))
    }

    // ---- P-20 e I-2: el peso del alcohol y la r de Watson.
    @Test fun pesoMasReciente() {
        val p = listOf("2024-01-01" to 80.0, "2026-01-01" to 70.0)
        assertEquals(70.0, Sustancias.pesoDe("2026-02-01", p)!!, 0.0)
        assertEquals(80.0, Sustancias.pesoDe("2025-06-01", p)!!, 0.0)
        // Antes del primero, el primero posterior (no 70 kg inventados).
        assertEquals(80.0, Sustancias.pesoDe("2023-06-01", p)!!, 0.0)
        assertNull(Sustancias.pesoDe("2026-02-01", emptyList()))
    }

    @Test fun watson() {
        assertEquals(0.749, Sustancias.watsonR("hombre", 30, 175, 70.0)!!, 0.001)
        assertEquals(0.777, Sustancias.watsonR("hombre", 30, 190, 70.0)!!, 0.001)
        assertEquals(0.624, Sustancias.watsonR("hombre", 55, 170, 95.0)!!, 0.001)
        assertEquals(0.621, Sustancias.watsonR("mujer", 30, 160, 60.0)!!, 0.001)
        assertNull(Sustancias.watsonR("hombre", null, 175, 70.0))
        assertNull(Sustancias.watsonR("hombre", 30, 90, 70.0))
        assertNull(Sustancias.watsonR("", 30, 175, 70.0))
    }

    // ---- P-22: una sola 1RM estimada.
    @Test fun unaSolaRepeticionMaxima() {
        val s = listOf(Serie("1", "2026-10-01", ejercicio = "Sentadilla", peso_kg = 100.0, reps = 15),
            Serie("2", "2026-10-01", ejercicio = "Sentadilla", peso_kg = 120.0, reps = 5),
            Serie("3", "2026-10-01", ejercicio = "Sentadilla", tipo = "Warm-up", peso_kg = 130.0, reps = 8))
        assertEquals(140.0, s.mapNotNull { Macrofactor.e1rm(it) }.max(), 1e-9)
    }

    // ---- P-24: los días atípicos revisan la distancia.
    @Test fun viajeAtipico() {
        val hoy = Fechas.diaLogico()
        val panel = (0 until 40).associate { i -> Fechas.sumar(hoy, -i.toLong()) to mapOf<String, Double?>("distancia_km" to if (i == 3) 300.0 else 5.0 + (i % 5)) }
        val l = Analisis.atipicos(panel, emptyList())
        assertTrue(l.any { it.fecha == Fechas.sumar(hoy, -3) })
    }

    // ---- P-25: nombres unidos y la red.
    @Test fun nombresEncadenados() {
        assertEquals("c", Texto.canon("a", mapOf("a" to "b", "b" to "c")))
        val ciclo = mapOf("x" to "y", "y" to "x")
        assertEquals("x", Texto.canon("x", ciclo)); assertEquals("x", Texto.canon("y", ciclo))
        assertEquals("z", Texto.canon("z", ciclo))
    }

    @Test fun redConLineasFiltradas() {
        val nodos = listOf("a", "b", "c", "d").map { RedPersonas.Nodo(it, 3, "amistad_cercana", null) }
        val aristas = listOf(RedPersonas.Arista("a", "b", "se_conocen", 0), RedPersonas.Arista("c", "d", null, 4))
        assertEquals(2, RedPersonas.componentes(nodos, aristas))
        val dichas = aristas.filter { it.tipo != null }
        RedPersonas.grados(nodos, dichas)
        assertEquals(1, RedPersonas.componentes(nodos, dichas))
        assertEquals(0, nodos.first { it.nombre == "c" }.grado)
        assertEquals("amistad", nodos.first().grupo)
    }

    // ---- P-26: un cierre guardado otro día no cuenta.
    @Test fun cierreDeOtroDia() {
        val fuera = Momento("m1", "2026-10-21T09:00:00-06:00", "2026-10-20", "animo", valencia = 6, origen = "cierre")
        val ese = Momento("m2", "2026-10-20T21:00:00-06:00", "2026-10-20", "animo", valencia = 6, origen = "cierre")
        assertTrue(Estadistica.cierreFuera(fuera)); assertFalse(Estadistica.cierreFuera(ese))
        assertFalse(Estadistica.cierreFuera(fuera.copy(origen = "app")))
    }

    // ---- P-27: rachas con los días respondidos.
    @Test fun rachasSoloConRespuesta() {
        // sí, no, sin respuesta, no.
        val r = Recaidas.rachas(setOf("2026-10-01"), setOf("2026-10-02", "2026-10-04"), "2026-10-04")
        assertEquals(2, r.actual); assertEquals(2, r.mejor); assertEquals(1, r.sinRespuesta)
    }

    // ---- P-28: el diccionario no repite claves con descripciones distintas.
    @Test fun diccionarioSinClavesRepetidas() {
        val f = listOf("src/main/java/io/github/ruddy2003/vestigium/data/Diccionario.kt", "app/src/main/java/io/github/ruddy2003/vestigium/data/Diccionario.kt")
            .map { java.io.File(it) }.first { it.exists() }
        val s = f.readText()
        val cuerpo = s.substring(s.indexOf("val D: Map<String, String> = mapOf("), s.indexOf("/** De qué tabla salió"))
        val pares = Regex("\"((?:[^\"\\\\]|\\\\.)*)\"\\s+to\\s+\"((?:[^\"\\\\]|\\\\.)*)\"").findAll(cuerpo).map { it.groupValues[1] to it.groupValues[2] }.toList()
        val repetidas = pares.groupBy({ it.first }, { it.second }).filterValues { it.toSet().size > 1 }.keys
        assertTrue("claves repetidas: $repetidas", repetidas.isEmpty())
    }
}
