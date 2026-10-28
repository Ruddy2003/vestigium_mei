package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ciencia.Analisis
import io.github.ruddy2003.vestigium.ciencia.Periodo
import io.github.ruddy2003.vestigium.data.Comida
import io.github.ruddy2003.vestigium.data.Consumo
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Experimentos
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Sustancias
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Los hallazgos que se cerraron después del paper (H-4-2, H-7-7, H-8-11, H-9-26, H-14-3), con datos inventados. */
class CierrePrueba {
    // Horas locales, para que la prueba no dependa de la zona del equipo donde corre.
    private fun iso(f: String, h: Int, m: Int = 0) = java.time.LocalDate.parse(f).atTime(h, m).atZone(Fechas.zona()).toOffsetDateTime().toString()

    // ---- H-7-7: el tamaño del efecto es la g de Hedges.
    @Test fun gDeHedges() {
        val ini = "2026-09-07"
        val e = Experimento("e", "x", "acción", "valencia", ini, 2, listOf(true, false))
        val panel = (0 until 14).associate { i -> Fechas.sumar(ini, i.toLong()) to mapOf<String, Double?>("valencia" to (if (i < 7) 5.0 else 4.0) + (i % 3) * 0.5) }
        val r = Experimentos.resultado(e, panel, hoy = "2026-10-30")
        val a = r.con; val b = r.sin
        fun v(x: List<Double>) = x.sumOf { (it - x.average()) * (it - x.average()) } / (x.size - 1)
        val sp = kotlin.math.sqrt(((a.size - 1) * v(a) + (b.size - 1) * v(b)) / (a.size + b.size - 2))
        val d = (a.average() - b.average()) / sp
        assertEquals(d * (1 - 3.0 / (4 * (a.size + b.size) - 9)), r.d!!, 1e-9)
    }

    // ---- H-9-26: en «Creatina», el registro de suplementos completa «¿Lo hiciste hoy?».
    @Test fun creatinaUsaLasTomas() {
        val ini = "2026-09-07"
        val e = Experimento("e", "Creatina", "creatina", "valencia", ini, 2, listOf(true, false), plantilla = "creatina", fin = Fechas.sumar(ini, 13))
        val panel = (0 until 14).associate { i ->
            Fechas.sumar(ini, i.toLong()) to mapOf<String, Double?>("valencia" to 4.0 + (i % 2), "supl_creatina_tomado" to if (i < 7 && i != 3) 1.0 else 0.0)
        }
        val c = Experimentos.resultado(e, panel, hoy = "2026-10-30").cumplimiento!!
        assertEquals(7, c.respSi); assertEquals(6, c.hechoSi); assertEquals(7, c.respNo); assertEquals(0, c.hechoNo)
        // Sin plantilla, no se inventan respuestas.
        assertNull(Experimentos.resultado(e.copy(plantilla = null), panel, hoy = "2026-10-30").cumplimiento)
    }

    // ---- H-14-3: «Tu año» puede pedir días de hace más de un año.
    @Test fun atipicosDeOtroAnio() {
        val hoy = Fechas.diaLogico()
        val panel = (0 until 40).associate { i -> Fechas.sumar(hoy, -(500 + i).toLong()) to mapOf<String, Double?>("distancia_km" to if (i == 3) 300.0 else 5.0 + (i % 5)) }
        assertTrue(Analisis.atipicos(panel, emptyList()).isEmpty())
        assertTrue(Analisis.atipicos(panel, emptyList(), desde = null).any { it.fecha == Fechas.sumar(hoy, -503) })
    }

    // ---- H-8-11: el error típico de la diferencia con el periodo anterior.
    @Test fun errorDeLaDiferencia() {
        val a = listOf(4.0, 5.0, 6.0); val b = listOf(3.0, 5.0, 7.0)
        assertEquals(kotlin.math.sqrt(1.0 / 3 + 4.0 / 3), Periodo.errorDif(a, b)!!, 1e-12)
        assertNull(Periodo.errorDif(listOf(1.0, 2.0), b))
    }

    // ---- H-4-2: lo anotado aquí y en MacroFactor se cuenta una vez, toma por toma.
    @Test fun dosisTomaPorToma() {
        val f = "2026-10-20"
        val antes = Repo.consumos
        try {
            Repo.consumosEnMemoria(listOf(
                // La misma taza en los dos lados (MacroFactor la registró 40 min después) y otra solo aquí.
                Consumo("c1", f, "cafeina", inicio = iso(f, 8, 0), fin = iso(f, 8, 0), cantidad = 95.0, completo = true),
                Consumo("c2", f, "cafeina", inicio = iso(f, 15, 0), fin = iso(f, 15, 0), cantidad = 64.0, completo = true),
                // Cuatro bebidas aquí; MacroFactor solo tiene una de ellas: gana lo de aquí, que tiene más.
                Consumo("c3", f, "alcohol", inicio = iso(f, 21, 0), fin = iso(f, 23, 0), cantidad = 4.0),
            ))
            val comidas = listOf(
                Comida("m1", f, "08:40", "Café", valores = mapOf("cafeina_mg" to 90.0)),
                Comida("m2", f, "12:00", "Refresco de cola", valores = mapOf("cafeina_mg" to 30.0)),
                Comida("m3", f, "22:00", "Cerveza", valores = mapOf("alcohol_g" to 14.0)),
            )
            val (caf, alc) = Sustancias.dosis(f, f, comidas)
            assertEquals(listOf(95.0, 30.0, 64.0), caf.map { it.cantidad })
            assertEquals(Fechas.millis(iso(f, 8, 0)), caf.first().ms)
            assertEquals(4 * Sustancias.GRAMOS_BEBIDA, alc.sumOf { it.cantidad }, 1e-9)
            // Si MacroFactor tiene más, gana MacroFactor y lo de aquí no se suma.
            val (_, alc2) = Sustancias.dosis(f, f, comidas + Comida("m4", f, "21:30", "Vino", valores = mapOf("alcohol_g" to 60.0)))
            assertEquals(74.0, alc2.sumOf { it.cantidad }, 1e-9)
        } finally {
            Repo.consumosEnMemoria(antes)
        }
    }
}
