package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ciencia.Analisis
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.ciencia.Variable
import io.github.ruddy2003.vestigium.data.ColdTurkey
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Exportar
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.sensores.Percepcion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

/**
 * La entrega 10d (P-30 a P-33): R y la app dan lo mismo. Los números esperados salieron de correr vestigium.R
 * (vm_factores y vm_relaciones) con los mismos datos inventados; si una de las dos cambia, esta prueba lo avisa.
 * Los p que salen de la normal se comparan con 2e-6: la app usa la aproximación de Abramowitz y Stegun (7.1.26, error
 * máximo 1.5e-7) y R, la normal exacta (pnorm); q multiplica esa diferencia por hasta el número de pruebas.
 */
class ExportacionPrueba {
    private val P = 2e-6
    private val vars = listOf("y", "x1", "x2", "b").map { Variable(it, it, "Prueba") }

    /** 80 días con fórmulas fijas (sin azar), iguales a las del script de R. x1 falta cada 9 días. */
    private val panel: Map<String, Map<String, Double?>> = (0 until 80).associate { i ->
        Fechas.sumar("2026-03-01", i.toLong()) to mapOf(
            "y" to sin(i / 5.0) + 0.3 * cos(i * 1.7) + if (i % 7 == 0) 0.5 else 0.0,
            "x1" to if (i % 9 == 0) null else cos(i / 4.0) + 0.2 * sin(i * 2.3),
            "x2" to ((i * 37) % 11) / 10.0,
            "b" to if ((i * 13) % 5 < 2) 1.0 else 0.0)
    }

    private fun revisa(f: List<Analisis.Factor>, id: String, efecto: Double, lo: Double, hi: Double, n: Int, p: Double) {
        val x = f.first { it.v.id == id }
        assertEquals("$id efecto", efecto, x.efecto, 1e-9); assertEquals("$id lo", lo, x.lo, 1e-9); assertEquals("$id hi", hi, x.hi, 1e-9)
        assertEquals("$id n", n, x.n); assertEquals("$id p", p, x.p, P)
    }

    // ---- P-17 en R: vm_factores() es «Todo lo que se mueve con algo».
    @Test fun factoresComoR() {
        val a = Analisis.factores(panel, vars, "y", desfase = false, sinTendencia = true)
        revisa(a, "x1", -0.4034934343749698, -0.694999479938390, 0.00193264386639378, 71, 0.051034977249207)
        revisa(a, "x2", -0.0199092324446691, -0.238586345409335, 0.20068948055265140, 80, 0.861295020338990)
        revisa(a, "b", 0.0326740851880019, -0.537943083485518, 0.60329125386152160, 80, 0.903286493051766)
        assertEquals(0.153104931747621, a.first { it.v.id == "x1" }.q, P)
        val b = Analisis.factores(panel, vars, "y", desfase = true, sinTendencia = true)
        revisa(b, "x1", -0.4590620857627961, -0.730302352492540, -0.0627869207780215, 70, 0.024805578531723)
        revisa(b, "b", -0.0183898582173502, -0.530241580408015, 0.4934618639733142, 79, 0.939951813469952)
        val c = Analisis.factores(panel, vars, "y", desfase = false, sinTendencia = false)
        revisa(c, "x1", -0.3531127461201858, -0.790373621946548, 0.322500594944538, 71, 0.303880210831132)
        revisa(c, "b", 0.0338219160966475, -2.531367110615542, 2.599010942808837, 80, 0.965627907451630)
        val d = Analisis.factores(panel, vars, "y", desfase = true, sinTendencia = false)
        revisa(d, "x2", -0.09182621273706609, -0.306713014689235, 0.13196760061433, 79, 0.422099688996208)
        revisa(d, "b", -0.00533547867641854, -2.410057453388386, 2.39938649603555, 79, 0.994405188217994)
    }

    // ---- P-30: vm_relaciones() es «Las relaciones más marcadas».
    @Test fun relacionesComoR() {
        val l = Estadistica.relaciones(panel, vars, true).lista
        assertEquals(6, l.size)
        fun par(a: String, b: String) = l.first { setOf(it.a.id, it.b.id) == setOf(a, b) }
        val yx1 = par("y", "x1")
        assertEquals(-0.4034934343749698, yx1.r, 1e-9); assertEquals(71, yx1.n)
        assertEquals(-0.694999479938390, yx1.ic!!.first, 1e-9); assertEquals(0.00193264386639378, yx1.ic!!.second, 1e-9)
        assertEquals(0.051034977249207, yx1.p, P); assertEquals(0.306209863495242, yx1.q, P)
        val x2b = par("x2", "b")
        assertEquals(-0.0386309377125217, x2b.r, 1e-9); assertEquals(0.782468955969985, x2b.p, P)
        assertEquals(listOf("y" to "x1", "x1" to "x2", "x1" to "b"), l.take(3).map { it.a.id to it.b.id })
    }

    // ---- P-31: vm_familias.csv esconde exactamente las mismas parejas que obvia().
    @Test fun familiasIgualQueObvia() {
        val v = listOf(
            "valencia" to "Ánimo", "muestreo_valencia" to "Ánimo", "animo_valencia" to "Ánimo", "palabras" to "Texto", "personas" to "Texto",
            "cercanos" to "Personas", "registros_animo" to "Momentos", "n_madrugada" to "Momentos", "n_alegria" to "Sentimientos",
            "n_tristeza" to "Sentimientos", "sueno_pesadilla" to "Sueños", "sueno_vuelo" to "Sueños", "sueno_horas" to "Sueño",
            "sueno_recordado" to "Sueño", "flow_alto" to "Lectura del día", "flow_bajo" to "Lectura del día", "horas_tipo_casa" to "Lugares",
            "lug_gym" to "Lugares", "supl_creatina_dosis" to "Suplementos", "supl_creatina_tomado" to "Suplementos", "supl_omega_dosis" to "Suplementos",
            "campana_sueno7" to "Campañas", "campana_hab_leer" to "Campañas", "hab_leer" to "Hábitos", "campanas_n" to "Campañas",
            "per_ana" to "Personas", "pen_ana" to "Personas", "per_beto" to "Personas", "tema_viaje" to "Temas", "nut_hierro" to "Nutrición",
            "ali_pan" to "Nutrición", "ciu_cdmx" to "Mundo", "pais_mx" to "Mundo", "esc_who5_total" to "Escalas", "esc_who5_item1" to "Escalas",
            "esc_pss4_total" to "Escalas", "app_instagram" to "Apps", "pantalla_min" to "Teléfono", "desbloqueos" to "Teléfono", "kcal" to "Nutrición",
            "proteina_g" to "Nutrición", "pasos" to "Cuerpo", "distancia_km" to "Movilidad", "gasto_total" to "Dinero", "otra" to "Otra",
        ).map { (id, g) -> Variable(id, id, g) }
        val fam = Estadistica.familias(v)
        for (i in v.indices) for (j in v.indices) if (i != j) {
            val a = v[i]; val b = v[j]
            assertEquals("${a.id} con ${b.id}", Estadistica.obvia(a, b), Estadistica.obviaEnFamilias(fam, a.id, b.id))
        }
        // Con los nombres de vm_panel.csv: km va con distancia_km en sus familias, y los dos son la misma medida.
        val conAlias = Estadistica.familias(v, mapOf("distancia_km" to listOf("km")))
        assertTrue(Estadistica.obviaEnFamilias(conAlias, "km", "pasos")); assertTrue(Estadistica.obviaEnFamilias(conAlias, "km", "distancia_km"))
        assertFalse(Estadistica.obviaEnFamilias(conAlias, "km", "valencia"))
        assertEquals(11, Exportar.EQUIVALENTES.size)
    }

    // ---- P-32: vm_palabras usa las mismas palabras vacías que la app.
    @Test fun palabrasVaciasComoR() {
        val f = listOf("src/main/assets/vestigium.R", "app/src/main/assets/vestigium.R").map { java.io.File(it) }.first { it.exists() }
        val cuerpo = Regex("vacias <- c\\(([\\s\\S]*?)\\)\\n").find(f.readText())!!.groupValues[1]
        val deR = Regex("\"([^\"]*)\"").findAll(cuerpo).map { Analisis.normal(it.groupValues[1]) }.toSet()
        assertEquals(Analisis.VACIAS_N, deR)
    }

    // ---- P-33: dominios completos en Cold Turkey y el rango del barómetro.
    @Test fun coldTurkeyPorDominio() {
        val c = Config()
        assertEquals("social", ColdTurkey.categoria("x.com", c)); assertEquals("social", ColdTurkey.categoria("m.x.com", c))
        assertEquals("otro", ColdTurkey.categoria("dropbox.com", c)); assertEquals("otro", ColdTurkey.categoria("wix.com", c))
        assertEquals("ocio", ColdTurkey.categoria("m.youtube.com", c)); assertEquals("otro", ColdTurkey.categoria("hbomax.com", c))
        assertEquals("productivo", ColdTurkey.categoria("gemini.google.com", c)); assertEquals("productivo", ColdTurkey.categoria("rstudio", c))
        assertEquals("ocio", ColdTurkey.categoria("leagueoflegends.com", c))
        assertTrue(780.0 in Percepcion.RANGO_HPA); assertTrue(1013.25 in Percepcion.RANGO_HPA); assertFalse(1200.0 in Percepcion.RANGO_HPA)
    }
}
