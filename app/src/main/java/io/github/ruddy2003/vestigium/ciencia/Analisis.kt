package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.Exportar
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import java.text.Normalizer
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Herramientas de análisis que corren en el teléfono. Son descriptivas: sirven para encontrar pistas
 * que después vale la pena revisar en R con modelos que consideren que los días vecinos se parecen.
 */
object Analisis {
    // ---------- Utilidades estadísticas ----------
    /** Función de distribución normal estándar (aproximación de Abramowitz y Stegun 7.1.26). */
    fun phi(z: Double): Double {
        val t = 1 / (1 + 0.3275911 * abs(z) / sqrt(2.0))
        val y = 1 - (((((1.061405429 * t - 1.453152027) * t) + 1.421413741) * t - 0.284496736) * t + 0.254829592) * t * exp(-z * z / 2)
        return if (z >= 0) 0.5 * (1 + y) else 0.5 * (1 - y)
    }
    fun pBilateral(z: Double): Double = (2 * (1 - phi(abs(z)))).coerceIn(0.0, 1.0)

    /** Corrección de Benjamini y Hochberg: controla la proporción de falsos descubrimientos al revisar muchas cosas a la vez. */
    fun bh(p: List<Double>): List<Double> {
        val m = p.size; if (m == 0) return emptyList()
        val orden = p.indices.sortedBy { p[it] }
        val q = DoubleArray(m)
        var minimo = 1.0
        for (k in m - 1 downTo 0) { val i = orden[k]; minimo = minOf(minimo, p[i] * m / (k + 1)); q[i] = minimo }
        return q.toList()
    }

    fun sd(x: List<Double>): Double? { if (x.size < 2) return null; val m = x.average(); return sqrt(x.sumOf { (it - m) * (it - m) } / (x.size - 1)) }

    // ---------- Series ----------
    fun serie(panel: Map<String, Map<String, Double?>>, v: String): List<Pair<String, Double>> =
        panel.entries.mapNotNull { (f, r) -> r[v]?.let { f to it } }.sortedBy { it.first }

    /** Media móvil por días de calendario (no por registros): cada punto promedia los días que existan en la ventana. */
    fun movil(s: List<Pair<String, Double>>, ventana: Int): List<Pair<String, Double>> {
        if (s.isEmpty()) return emptyList()
        val porFecha = s.toMap()
        val ini = Fechas.parse(s.first().first); val fin = Fechas.parse(s.last().first)
        val out = ArrayList<Pair<String, Double>>()
        var d = ini
        while (!d.isAfter(fin)) {
            val vals = (0 until ventana).mapNotNull { porFecha[Fechas.iso(d.minusDays(it.toLong()))] }
            if (vals.size >= maxOf(2, ventana / 3)) out.add(Fechas.iso(d) to vals.average())
            d = d.plusDays(1)
        }
        return out
    }

    data class Cambio(val reciente: Double, val anterior: Double, val dif: Double, val lo: Double, val hi: Double, val nA: Int, val nB: Int)

    fun cambio(s: List<Pair<String, Double>>, dias: Int = 30): Cambio? {
        val hoy = Fechas.diaLogico()
        val a = s.filter { it.first > Fechas.sumar(hoy, -dias.toLong()) }.map { it.second }
        val b = s.filter { it.first <= Fechas.sumar(hoy, -dias.toLong()) && it.first > Fechas.sumar(hoy, -2L * dias) }.map { it.second }
        val d = Estadistica.diferencia(a, b) ?: return null
        return Cambio(a.average(), b.average(), d.first, d.second, d.third, a.size, b.size)
    }

    data class Grupo(val media: Double, val lo: Double, val hi: Double, val n: Int)

    fun porDiaSemana(s: List<Pair<String, Double>>): List<Grupo?> = (1..7).map { dow ->
        val v = s.filter { Fechas.parse(it.first).dayOfWeek.value == dow }.map { it.second }
        if (v.size < 2) null else { val m = v.average(); val e = 1.96 * (sd(v) ?: 0.0) / sqrt(v.size.toDouble()); Grupo(m, m - e, m + e, v.size) }
    }

    // ---------- Qué acompaña tus mejores días ----------
    data class Factor(val v: Variable, val binaria: Boolean, val efecto: Double, val lo: Double, val hi: Double, val n: Int, val nCon: Int, val nSin: Int, val p: Double, var q: Double = 1.0)

    fun factores(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, objetivo: String, desfase: Boolean): List<Factor> {
        val out = ArrayList<Factor>()
        for (v in vars) {
            if (v.id == objetivo) continue
            // La misma medida por otra vía (valencia del día y en avisos) no es un «factor».
            if (Estadistica.FAMILIAS.any { v.id in it && objetivo in it }) continue
            if (vars.firstOrNull { it.id == objetivo }?.let { Estadistica.obvia(v, it) } == true) continue
            val ps = Estadistica.pares(panel, v.id, objetivo, desfase)
            if (ps.size < 10) continue
            val bin = ps.all { it.x == 0.0 || it.x == 1.0 }
            if (bin) {
                val con = ps.filter { it.x == 1.0 }.map { it.y }; val sin = ps.filter { it.x == 0.0 }.map { it.y }
                if (con.size < 5 || sin.size < 5) continue
                val d = Estadistica.diferencia(con, sin) ?: continue
                val se = (d.third - d.second) / (2 * 1.96)
                if (se <= 0) continue
                out.add(Factor(v, true, d.first, d.second, d.third, ps.size, con.size, sin.size, pBilateral(d.first / se)))
            } else {
                if (ps.size < 14) continue
                val r = Estadistica.spearman(ps.map { it.x }, ps.map { it.y }) ?: continue
                val ic = Estadistica.ic95(r, ps.size) ?: continue
                val z = 0.5 * ln((1 + r.coerceIn(-0.999, 0.999)) / (1 - r.coerceIn(-0.999, 0.999))) * sqrt((ps.size - 3).toDouble())
                out.add(Factor(v, false, r, ic.first, ic.second, ps.size, 0, 0, pBilateral(z)))
            }
        }
        val q = bh(out.map { it.p })
        out.forEachIndexed { i, f -> f.q = q[i] }
        return out.sortedBy { it.p }
    }

    // ---------- Regresión múltiple ----------
    data class Modelo(val nombres: List<String>, val beta: List<Double>, val lo: List<Double>, val hi: List<Double>, val r2: Double, val n: Int)

    /** Mínimos cuadrados con variables estandarizadas: los coeficientes dicen cuánto cambia el objetivo (en desviaciones estándar) por cada desviación estándar del predictor. */
    fun modelo(panel: Map<String, Map<String, Double?>>, objetivo: String, predictores: List<String>, conAyer: Boolean): Modelo? {
        val nombres = predictores + if (conAyer) listOf("ayer") else emptyList()
        val filas = ArrayList<DoubleArray>(); val ys = ArrayList<Double>()
        for ((f, r) in panel) {
            val y = r[objetivo] ?: continue
            val xs = predictores.map { r[it] ?: Double.NaN }.toMutableList()
            if (conAyer) xs.add(panel[Fechas.sumar(f, -1)]?.get(objetivo) ?: Double.NaN)
            if (xs.any { it.isNaN() }) continue
            filas.add(xs.toDoubleArray()); ys.add(y)
        }
        val k = nombres.size; val n = ys.size
        if (k == 0 || n < k + 10) return null
        fun z(v: List<Double>): List<Double>? { val m = v.average(); val s = sd(v) ?: return null; if (s == 0.0) return null; return v.map { (it - m) / s } }
        val y = z(ys) ?: return null
        val cols = (0 until k).map { j -> z(filas.map { it[j] }) ?: return null }
        // Matriz X'X y X'y (sin intercepto porque todo está centrado).
        val a = Array(k) { i -> DoubleArray(k) { j -> (0 until n).sumOf { cols[i][it] * cols[j][it] } } }
        val b = DoubleArray(k) { i -> (0 until n).sumOf { cols[i][it] * y[it] } }
        val inv = invertir(a) ?: return null
        val beta = DoubleArray(k) { i -> (0 until k).sumOf { inv[i][it] * b[it] } }
        val pred = (0 until n).map { r -> (0 until k).sumOf { beta[it] * cols[it][r] } }
        val sse = (0 until n).sumOf { (y[it] - pred[it]).let { e -> e * e } }
        val sst = y.sumOf { it * it }
        val s2 = sse / (n - k - 1).coerceAtLeast(1)
        val se = (0 until k).map { sqrt(s2 * inv[it][it]) }
        return Modelo(nombres, beta.toList(), beta.indices.map { beta[it] - 1.96 * se[it] }, beta.indices.map { beta[it] + 1.96 * se[it] }, 1 - sse / sst, n)
    }

    private fun invertir(m: Array<DoubleArray>): Array<DoubleArray>? {
        val n = m.size
        val a = Array(n) { i -> DoubleArray(2 * n) { j -> if (j < n) m[i][j] else if (j - n == i) 1.0 else 0.0 } }
        for (c in 0 until n) {
            val piv = (c until n).maxByOrNull { abs(a[it][c]) } ?: return null
            if (abs(a[piv][c]) < 1e-10) return null
            val t = a[c]; a[c] = a[piv]; a[piv] = t
            val d = a[c][c]
            for (j in 0 until 2 * n) a[c][j] /= d
            for (i in 0 until n) if (i != c) { val f = a[i][c]; if (f != 0.0) for (j in 0 until 2 * n) a[i][j] -= f * a[c][j] }
        }
        return Array(n) { i -> DoubleArray(n) { j -> a[i][j + n] } }
    }

    // ---------- Dinámica emocional ----------
    data class Mes(val mes: String, val media: Double, val sd: Double?, val mssd: Double?, val n: Int)
    data class Dinamica(val media: Double, val sd: Double?, val mssd: Double?, val inercia: Double?, val n: Int, val dentroDelDia: Double?,
                        val dificiles: Double, val rachaBuena: Int, val rachaDificil: Int, val meses: List<Mes>)

    /**
     * Medidas clásicas de la investigación del afecto: variabilidad (DE), inestabilidad (MSSD, Jahng et al. 2008)
     * e inercia (autocorrelación de un día al siguiente, Kuppens et al. 2010).
     */
    fun dinamica(): Dinamica? {
        val s = Repo.dias.values.mapNotNull { d -> d.valencia?.let { d.fecha to it.toDouble() } }.sortedBy { it.first }
        if (s.size < 7) return null
        val mapa = s.toMap()
        val consecutivos = s.mapNotNull { (f, v) -> mapa[Fechas.sumar(f, 1)]?.let { v to it } }
        val mssd = if (consecutivos.size >= 3) consecutivos.map { (a, b) -> (b - a) * (b - a) }.average() else null
        val inercia = if (consecutivos.size >= 10) Estadistica.pearson(consecutivos.map { it.first }, consecutivos.map { it.second }) else null
        val mu = Repo.momentos.filter { it.tipo == "muestreo" && it.valencia != null }.groupBy { it.fecha }.values.mapNotNull { l -> if (l.size >= 2) sd(l.map { it.valencia!!.toDouble() }) else null }
        var buena = 0; var mala = 0; var rb = 0; var rm = 0; var prev: String? = null
        for ((f, v) in s) {
            val seguido = prev != null && Fechas.sumar(prev, 1) == f
            rb = if (v >= 5) (if (seguido) rb + 1 else 1) else 0
            rm = if (v <= 3) (if (seguido) rm + 1 else 1) else 0
            buena = maxOf(buena, rb); mala = maxOf(mala, rm); prev = f
        }
        val meses = s.groupBy { it.first.take(7) }.map { (m, l) ->
            val mp = l.toMap()
            val c = l.mapNotNull { (f, v) -> mp[Fechas.sumar(f, 1)]?.let { (it - v) * (it - v) } }
            Mes(m, l.map { it.second }.average(), sd(l.map { it.second }), if (c.size >= 3) c.average() else null, l.size)
        }.sortedBy { it.mes }
        return Dinamica(s.map { it.second }.average(), sd(s.map { it.second }), mssd, inercia, s.size,
            if (mu.isEmpty()) null else mu.average(), s.count { it.second <= 2 } * 100.0 / s.size, buena, mala, meses)
    }

    // ---------- Ritmo y sueño ----------
    data class Noche(val fecha: String, val inicio: Double, val fin: Double, val reloj: Boolean)
    data class Ritmo(val noches: List<Noche>, val dormirMedio: Double, val despertarMedio: Double, val duracionLaboral: Double?, val duracionLibre: Double?,
                     val msfLaboral: Double?, val msfLibre: Double?, val jetLag: Double?, val sdDormir: Double?, val fuente: String)

    /**
     * Ritmo de sueño: horas medias, regularidad y «jet lag social» (diferencia del punto medio del sueño entre días libres y laborales;
     * Wittmann et al. 2006). Si no hay reloj, se aproxima con el último y el primer uso del teléfono.
     */
    fun ritmo(): Ritmo? {
        val reloj = Repo.salud.values.mapNotNull { s ->
            val i = s.sueno_inicio?.let { Exportar.horaDecimal(it, true) } ?: return@mapNotNull null
            val f = s.sueno_fin?.let { Exportar.horaDecimal(it, false) } ?: return@mapNotNull null
            Noche(s.fecha, i, f + 24, true)
        }
        val telefono = Repo.pantalla.values.mapNotNull { p ->
            val ult = p.ultimo_uso?.let { Exportar.horaDecimal(it, true) } ?: return@mapNotNull null
            val sig = Repo.pantalla[Fechas.sumar(p.fecha, 1)]?.primer_uso?.let { Exportar.horaDecimal(it, false) } ?: return@mapNotNull null
            if (ult < 18 || sig < 4) return@mapNotNull null
            Noche(Fechas.sumar(p.fecha, 1), ult, sig + 24, false)
        }
        val noches = (if (reloj.size >= 7) reloj else (reloj + telefono.filter { t -> reloj.none { it.fecha == t.fecha } })).sortedBy { it.fecha }
            .filter { it.fin - it.inicio in 2.0..16.0 }
        if (noches.size < 5) return null
        fun libre(f: String) = Fechas.parse(f).dayOfWeek.value >= 6
        val lab = noches.filter { !libre(it.fecha) }; val lib = noches.filter { libre(it.fecha) }
        fun msf(l: List<Noche>) = if (l.size < 2) null else l.map { (it.inicio + it.fin) / 2 }.average()
        val a = msf(lab); val b = msf(lib)
        return Ritmo(noches.takeLast(30), noches.map { it.inicio }.average(), noches.map { it.fin - 24 }.average(),
            lab.takeIf { it.size >= 2 }?.map { it.fin - it.inicio }?.average(), lib.takeIf { it.size >= 2 }?.map { it.fin - it.inicio }?.average(),
            a, b, if (a != null && b != null) abs(b - a) else null, sd(noches.map { it.inicio }), if (reloj.size >= 7) "reloj" else "teléfono")
    }

    // ---------- Palabras ----------
    private val VACIAS = ("a al algo algunas algunos ante antes aquel aquella aquello aqui aquí asi así aun aún bien cada casi como cómo con contra cual cuando cuándo de del desde " +
        "donde dónde dos el él ella ellas ellos en entre era eran es esa esas ese eso esos esta está estaba estaban estar este esto estos estoy fue fueron fui ha habia había han hasta hay " +
        "hice hizo la las le les lo los mas más me mi mí mis mucho muy nada ni no nos nosotros o otra otras otro otros para pero poco por porque que qué quien quién se sea ser si sí " +
        "sido sin sobre solo sólo son su sus también tan tanto te tengo tenia tenía ti tiene tienen todo todos tu tú tus un una unas uno unos usted ya yo hoy dia día luego despues después " +
        "creo pues igual ahora vez veces cosa cosas hacer hace estuve estuvo tuve tuvo voy iba ir fui fue pude puedo quiero queria quería siento senti sentí").split(" ").toSet()

    internal val VACIAS_N by lazy { VACIAS.map { Normalizer.normalize(it, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "") }.toSet() }

    data class Palabra(val palabra: String, val dias: Int, val media: Double, val dif: Double)

    internal fun normal(s: String) = Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

    fun palabras(minDias: Int = 5): Triple<List<Palabra>, List<Palabra>, List<Pair<String, Int>>> {
        val dias = Repo.dias.values.filter { it.nota.isNotBlank() }
        val porPalabra = HashMap<String, MutableSet<String>>()
        val frec = HashMap<String, Int>()
        for (d in dias) {
            val toks = Regex("[\\p{L}]{3,}").findAll(d.nota.lowercase()).map { it.value }.filter { normal(it) !in VACIAS_N }.toList()
            toks.forEach { frec[it] = (frec[it] ?: 0) + 1 }
            toks.toSet().forEach { porPalabra.getOrPut(it) { HashSet() }.add(d.fecha) }
        }
        val conVal = dias.filter { it.valencia != null }.associate { it.fecha to it.valencia!!.toDouble() }
        val global = if (conVal.isEmpty()) 0.0 else conVal.values.average()
        val lista = porPalabra.mapNotNull { (w, fs) ->
            val v = fs.mapNotNull { conVal[it] }
            if (v.size < minDias) null else Palabra(w, v.size, v.average(), v.average() - global)
        }
        val frecuentes = frec.entries.sortedByDescending { it.value }.take(25).map { it.key to it.value }
        return Triple(lista.sortedByDescending { it.dif }.take(15), lista.sortedBy { it.dif }.take(15), frecuentes)
    }

    // ---------- Días atípicos ----------
    data class Atipico(val fecha: String, val razones: List<String>, val z: Double)

    /** Días que se salen de lo normal en alguna variable, con un z robusto (mediana y desviación absoluta mediana). */
    fun atipicos(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, umbral: Double = 3.0): List<Atipico> {
        val ids = listOf("valencia", "sueno_horas", "pasos", "pantalla_min", "desbloqueos", "gasto_total", "notificaciones", "fotos", "km",
            "min_hablado", "wa_mios", "musica_min", "fc_reposo", "hrv", "palabras")
        val nombres = vars.associate { it.id to it.nombre }
        val porDia = HashMap<String, MutableList<Pair<String, Double>>>()
        for (id in ids) {
            val s = serie(panel, id)
            if (s.size < 20) continue
            val x = s.map { it.second }.sorted()
            val med = x[x.size / 2]
            val mad = x.map { abs(it - med) }.sorted()[x.size / 2] * 1.4826
            if (mad <= 0) continue
            for ((f, v) in s) {
                val z = (v - med) / mad
                if (abs(z) >= umbral) porDia.getOrPut(f) { ArrayList() }.add("${nombres[id] ?: id} ${if (z > 0) "muy alto" else "muy bajo"} (%s)".format(if (abs(v) >= 100) "%,.0f".format(v) else "%.1f".format(v)) to abs(z))
            }
        }
        val desde = Fechas.sumar(Fechas.diaLogico(), -365)
        return porDia.filterKeys { it >= desde }.map { (f, l) -> Atipico(f, l.sortedByDescending { it.second }.map { it.first }, l.maxOf { it.second }) }
            .sortedByDescending { it.fecha }
    }
}
