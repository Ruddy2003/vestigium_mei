package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.Privado
import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Personas
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.data.esAnimo
import io.github.ruddy2003.vestigium.data.esCierre
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import kotlin.math.abs
import kotlin.math.atanh
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tanh

data class Variable(val id: String, val nombre: String, val grupo: String)
data class Par(val x: Double, val y: Double, val fecha: String)
/**
 * [r] es la correlación que se muestra (sin tendencia o bruta, según el modo); [bruta] la de los valores tal cual.
 * [nEf] son los días «independientes» una vez descontado que un día se parece al anterior; el intervalo y [q] se calculan con ellos.
 */
data class Correlacion(
    val a: Variable, val b: Variable, val r: Double, val n: Int, val ic: Pair<Double, Double>?,
    val bruta: Double = r, val nEf: Double = n.toDouble(), val p: Double = 1.0,
    var q: Double = 1.0, var qConObvias: Double = 1.0, val obvia: Boolean = false,
)

/** Resultado de revisar todos los pares. [lentas]: variables que casi solo cambian con las semanas (se omiten al quitar tendencias). */
data class Relaciones(val lista: List<Correlacion>, val obvias: Int, val lentas: List<Variable>, val sinTendencia: Boolean) {
    val probadas get() = lista.size - obvias
}

object Estadistica {

    fun pearson(xs: List<Double>, ys: List<Double>): Double? {
        val n = xs.size
        if (n < 3) return null
        val mx = xs.average(); val my = ys.average()
        var sxy = 0.0; var sxx = 0.0; var syy = 0.0
        for (i in 0 until n) { val dx = xs[i] - mx; val dy = ys[i] - my; sxy += dx * dy; sxx += dx * dx; syy += dy * dy }
        if (sxx == 0.0 || syy == 0.0) return null
        return sxy / sqrt(sxx * syy)
    }

    fun rangos(a: List<Double>): List<Double> {
        val idx = a.withIndex().sortedBy { it.value }
        val r = DoubleArray(a.size)
        var i = 0
        while (i < idx.size) {
            var j = i
            while (j + 1 < idx.size && idx[j + 1].value == idx[i].value) j++
            val prom = (i + j) / 2.0 + 1
            for (k in i..j) r[idx[k].index] = prom
            i = j + 1
        }
        return r.toList()
    }

    fun spearman(xs: List<Double>, ys: List<Double>): Double? = pearson(rangos(xs), rangos(ys))

    /** Intervalo de confianza de 95% por la transformación de Fisher. */
    fun ic95(r: Double?, n: Int): Pair<Double, Double>? {
        if (r == null || n < 5) return null
        val z = atanh(r.coerceIn(-0.9999, 0.9999)); val se = 1 / sqrt(n - 3.0)
        return tanh(z - 1.96 * se) to tanh(z + 1.96 * se)
    }

    fun fuerza(r: Double): String = abs(r).let { if (it < .1) "prácticamente nula" else if (it < .3) "débil" else if (it < .5) "moderada" else "fuerte" }

    fun media(v: List<Double>) = if (v.isEmpty()) null else v.average()

    // ---------- La t de Student y los días que se parecen a sus vecinos (P-17) ----------

    /** Logaritmo de la función gamma (aproximación de Lanczos, g = 7). */
    private fun lnGamma(x: Double): Double {
        val c = doubleArrayOf(0.99999999999980993, 676.5203681218851, -1259.1392167224028, 771.32342877765313, -176.61502916214059,
            12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7)
        if (x < 0.5) return ln(Math.PI / abs(sin(Math.PI * x))) - lnGamma(1 - x)
        val z = x - 1; var a = c[0]; val t = z + 7.5
        for (i in 1 until 9) a += c[i] / (z + i)
        return 0.5 * ln(2 * Math.PI) + (z + 0.5) * ln(t) - t + ln(a)
    }

    /** Beta incompleta regularizada I_x(a, b), por fracción continua (Lentz). */
    private fun betaInc(a: Double, b: Double, x: Double): Double {
        if (x <= 0.0) return 0.0
        if (x >= 1.0) return 1.0
        val frente = exp(lnGamma(a + b) - lnGamma(a) - lnGamma(b) + a * ln(x) + b * ln(1 - x))
        if (x > (a + 1) / (a + b + 2)) return 1 - betaInc(b, a, 1 - x)
        val diminuto = 1e-300
        var f = 1.0; var c = 1.0; var d = 0.0
        for (i in 0..400) {
            val m = i / 2
            val num = when {
                i == 0 -> 1.0
                i % 2 == 0 -> m * (b - m) * x / ((a + 2 * m - 1) * (a + 2 * m))
                else -> -((a + m) * (a + b + m) * x) / ((a + 2 * m) * (a + 2 * m + 1))
            }
            d = 1 + num * d; if (abs(d) < diminuto) d = diminuto; d = 1 / d
            c = 1 + num / c; if (abs(c) < diminuto) c = diminuto
            val cd = c * d; f *= cd
            if (abs(1 - cd) < 1e-12) break
        }
        return frente * (f - 1) / a
    }

    /** p de dos colas de un valor t con [gl] grados de libertad (admite grados no enteros, como los de Welch). */
    fun pT(t: Double, gl: Double): Double {
        if (t.isNaN() || gl <= 0) return 1.0
        val g = gl.coerceAtMost(1e6)
        return betaInc(g / 2, 0.5, g / (g + t * t)).coerceIn(0.0, 1.0)
    }

    /**
     * El cuantil [p] (por ejemplo 0.975) de la t de Student con [gl] grados de libertad: el número que reemplaza a 1.96 cuando
     * hay pocos datos (con 2 grados, 4.30; con 30, 2.04). Se busca por bisección sobre la distribución exacta.
     */
    fun cuantilT(p: Double, gl: Double): Double {
        if (p == 0.5) return 0.0
        if (p < 0.5) return -cuantilT(1 - p, gl)
        val cola = 2 * (1 - p)
        var lo = 0.0; var hi = 1.0
        while (pT(hi, gl) > cola && hi < 1e7) hi *= 2
        repeat(100) { val m = (lo + hi) / 2; if (pT(m, gl) > cola) lo = m else hi = m }
        return (lo + hi) / 2
    }

    /**
     * Autocorrelación de orden 1 de una serie con fechas: qué tanto un día se parece al siguiente, contando solo días seguidos
     * (o semanas seguidas, con [paso] = 7).
     * Null si hay menos de 10 pares de días seguidos (con menos, la estimación es puro ruido).
     */
    fun ar1(s: List<Pair<String, Double>>, paso: Long = 1L): Double? {
        val m = s.toMap()
        val pares = s.mapNotNull { (f, v) -> m[Fechas.sumar(f, paso)]?.let { v to it } }
        if (pares.size < 10) return null
        return pearson(pares.map { it.first }, pares.map { it.second })
    }

    /** Cuánto se ensancha el error estándar si cada día se parece al anterior con autocorrelación [rho]: √((1 + ρ)/(1 − ρ)). */
    fun factorAR1(rho: Double): Double = sqrt((1 + rho) / (1 - rho))

    /** Diferencia de medias: estimación, intervalo de 95 %, error estándar, grados de libertad (Welch) y p de dos colas. */
    data class Diferencia(val d: Double, val lo: Double, val hi: Double, val se: Double, val gl: Double, val p: Double)

    /**
     * Diferencia de medias con *t* de Welch. Si los días de cada grupo se parecen a sus vecinos ([rhoA], [rhoB], autocorrelación
     * de orden 1), el error estándar de ese grupo se multiplica por [factorAR1] y sus grados de libertad se calculan con su
     * n efectivo, n·(1 − ρ)/(1 + ρ). Solo se corrige hacia arriba: ρ se acota entre 0 y 0.9.
     */
    fun diferenciaCompleta(a: List<Double>, b: List<Double>, rhoA: Double = 0.0, rhoB: Double = 0.0): Diferencia? {
        if (a.size < 3 || b.size < 3) return null
        val ma = a.average(); val mb = b.average()
        val va = a.sumOf { (it - ma) * (it - ma) } / (a.size - 1)
        val vb = b.sumOf { (it - mb) * (it - mb) } / (b.size - 1)
        val ra = rhoA.coerceIn(0.0, 0.9); val rb = rhoB.coerceIn(0.0, 0.9)
        val nA = (a.size * (1 - ra) / (1 + ra)).coerceAtLeast(2.0); val nB = (b.size * (1 - rb) / (1 + rb)).coerceAtLeast(2.0)
        val sa = va / a.size * (1 + ra) / (1 - ra); val sb = vb / b.size * (1 + rb) / (1 - rb)
        val se = sqrt(sa + sb)
        val d = ma - mb
        if (se == 0.0) return Diferencia(d, d, d, 0.0, nA + nB - 2, if (d == 0.0) 1.0 else 0.0)
        val gl = ((sa + sb) * (sa + sb) / (sa * sa / (nA - 1) + sb * sb / (nB - 1))).coerceIn(1.0, nA + nB - 2)
        val t = cuantilT(0.975, gl)
        return Diferencia(d, d - t * se, d + t * se, se, gl, pT(d / se, gl))
    }

    /** Diferencia de medias con intervalo de 95 % (t de Welch; ver [diferenciaCompleta]). */
    fun diferencia(a: List<Double>, b: List<Double>, rhoA: Double = 0.0, rhoB: Double = 0.0): Triple<Double, Double, Double>? =
        diferenciaCompleta(a, b, rhoA, rhoB)?.let { Triple(it.d, it.lo, it.hi) }

    /**
     * La autocorrelación de cada grupo, para [diferenciaCompleta]: la del grupo si tiene 10 pares de días seguidos; si no, la de
     * los dos grupos juntos (cada uno centrado en su media); si tampoco, 0. En series cortas la autocorrelación estimada sale
     * más baja que la real, así que se corrige: ρ + (1 + 4ρ)/n (Marriott y Pope, 1954). Sin esa corrección, con 30 + 30 días y
     * ρ = 0.5, el intervalo de 95 % cubre el 92 %; con ella, el 95 %.
     */
    fun ar1Grupos(a: List<Pair<String, Double>>, b: List<Pair<String, Double>>): Pair<Double, Double> {
        fun corregida(s: List<Pair<String, Double>>) = ar1(s)?.let { it + (1 + 4 * it) / s.size }
        val ma = a.map { it.second }.average(); val mb = b.map { it.second }.average()
        val juntos = (a.map { it.first to it.second - ma } + b.map { it.first to it.second - mb }).sortedBy { it.first }.distinctBy { it.first }
        val comun = corregida(juntos) ?: 0.0
        return (corregida(a) ?: comun) to (corregida(b) ?: comun)
    }

    /** [diferenciaCompleta] de dos grupos de días con fecha, con su autocorrelación (ver [ar1Grupos]). */
    fun diferenciaEnDias(a: List<Pair<String, Double>>, b: List<Pair<String, Double>>): Diferencia? {
        if (a.size < 3 || b.size < 3) return null
        val (ra, rb) = ar1Grupos(a.sortedBy { it.first }, b.sortedBy { it.first })
        return diferenciaCompleta(a.map { it.second }, b.map { it.second }, ra, rb)
    }

    /**
     * La serie sin tendencia: cada día menos el promedio de sus ±[ventana] días de calendario (si hay al menos 3), como en
     * «Las relaciones más marcadas». Queda lo que ese día tuvo de distinto a sus semanas.
     */
    fun sinTendencia(s: List<Pair<String, Double>>, ventana: Int = VENTANA): List<Pair<String, Double>> {
        val l = s.sortedBy { it.first }
        val dia = l.map { Fechas.parse(it.first).toEpochDay() }
        val out = ArrayList<Pair<String, Double>>()
        var lo = 0; var hi = 0; var suma = 0.0; var cnt = 0
        for (i in l.indices) {
            while (hi < l.size && dia[hi] <= dia[i] + ventana) { suma += l[hi].second; cnt++; hi++ }
            while (dia[lo] < dia[i] - ventana) { suma -= l[lo].second; cnt--; lo++ }
            if (cnt >= 3) out.add(l[i].first to l[i].second - suma / cnt)
        }
        return out
    }

    /**
     * Correlación entre dos series con fecha, con el n efectivo de [relaciones]: r de Pearson, intervalo de 95 % (Fisher con nEf)
     * y p. [ra] y [rb] son las autocorrelaciones de cada serie. Null con menos de [minN] días en común.
     */
    fun correlacionEf(xs: List<Double>, ys: List<Double>, ra: Double, rb: Double, minN: Int = 5): Triple<Double, Pair<Double, Double>, Pair<Double, Double>>? {
        val n = xs.size
        if (n < minN) return null
        val r = pearson(xs, ys) ?: return null
        val a = ra.coerceIn(-0.5, 0.97); val b = rb.coerceIn(-0.5, 0.97)
        val nEf = (n * (1 - a * b) / (1 + a * b)).coerceIn(minOf(4.0, n.toDouble()), n.toDouble())
        if (nEf <= 3.0) return null
        val z = atanh(r.coerceIn(-0.9999, 0.9999)); val se = 1 / sqrt(nEf - 3)
        return Triple(r, tanh(z - 1.96 * se) to tanh(z + 1.96 * se), nEf to Analisis.pBilateral(z / se))
    }

    // ---------- Variables diarias para cruzar ----------
    /**
     * Todas las variables. Con [dinamicas], también una por persona, lugar, app, categoría, artista, nutriente… (ver [Dinamicas]).
     * Las privadas (ver [Privado]) solo salen con [privadas]: las pantallas de la app nunca las piden; el diccionario de la exportación sí.
     */
    fun variables(dinamicas: Boolean = true, privadas: Boolean = false): List<Variable> {
        val v = mutableListOf(
            Variable("valencia", "Valencia del día", "Ánimo"), Variable("activacion", "Activación del día", "Ánimo"),
            Variable("muestreo_valencia", "Valencia promedio en avisos", "Ánimo"), Variable("muestreo_activacion", "Activación promedio en avisos", "Ánimo"),
            Variable("avisos_planeados", "Avisos de ánimo planeados", "Ánimo"), Variable("avisos_mostrados", "Avisos de ánimo mostrados", "Ánimo"),
            Variable("avisos_respondidos", "Avisos de ánimo respondidos", "Ánimo"), Variable("tasa_respuesta", "Proporción de avisos respondidos", "Ánimo"),
            Variable("dolor", "Dolor físico", "Cuerpo"), Variable("sueno_calidad", "Calidad de sueño percibida", "Sueño"),
            Variable("sueno_horas", "Horas de sueño (reloj)", "Sueño"), Variable("sueno_profundo", "Minutos de sueño profundo", "Sueño"),
            Variable("sueno_rem", "Minutos de sueño REM", "Sueño"), Variable("sueno_recordado", "Recordé un sueño (0/1)", "Sueño"),
            Variable("sueno_valencia", "Valencia del sueño", "Sueño"),
            Variable("hrv", "Variabilidad cardiaca (HRV)", "Cuerpo"), Variable("fc_reposo", "Frecuencia cardiaca en reposo", "Cuerpo"),
            Variable("fc_media", "Frecuencia cardiaca media del día", "Cuerpo"), Variable("fc_sueno", "Frecuencia cardiaca dormido", "Cuerpo"),
            Variable("spo2", "Saturación de oxígeno media (%)", "Cuerpo"), Variable("spo2_min", "Oxígeno mínimo de noche (%)", "Cuerpo"),
            Variable("pasos", "Pasos", "Cuerpo"), Variable("pantalla_min", "Minutos de pantalla", "Teléfono"),
            Variable("desbloqueos", "Desbloqueos del teléfono", "Teléfono"), Variable("gasto_total", "Gasto del día", "Dinero"),
            Variable("gasto_imprevisto", "Hubo gasto imprevisto (0/1)", "Dinero"), Variable("gasto_imprevisto_monto", "Gasto imprevisto (monto)", "Dinero"),
            Variable("palabras", "Palabras en el diario", "Texto"),
            Variable("personas", "Personas distintas mencionadas", "Social"), Variable("hitos_n", "Fechas señaladas del día", "Texto"),
            Variable("presencias_revisado", "Revisaste quién estuvo y en quién solo pensaste (0/1)", "Social"),
            Variable("rt_media", "Tiempo de reacción (ms)", "Mente"), Variable("lugares_distintos", "Lugares distintos visitados", "Movilidad"),
            Variable("distancia_km", "Kilómetros recorridos (aprox.)", "Movilidad"),
            Variable("t_max", "Temperatura máxima (°C)", "Entorno"), Variable("t_min", "Temperatura mínima (°C)", "Entorno"),
            Variable("precip_mm", "Lluvia (mm)", "Entorno"), Variable("nubes_pct", "Nubosidad (%)", "Entorno"),
            Variable("luz_horas", "Horas de luz del día", "Entorno"), Variable("sol_horas", "Horas de sol directo", "Entorno"),
            Variable("presion_hpa", "Presión atmosférica (hPa)", "Entorno"), Variable("aqi_us", "Índice de calidad del aire", "Entorno"),
            Variable("pm25", "Partículas PM2.5", "Entorno"), Variable("luna_ilum", "Luna iluminada (0 a 1)", "Entorno"),
            Variable("fotos", "Fotos tomadas", "Teléfono"), Variable("notificaciones", "Notificaciones recibidas", "Teléfono"),
            Variable("canciones", "Canciones escuchadas", "Teléfono"), Variable("primer_uso_h", "Hora del primer uso del teléfono", "Teléfono"),
            Variable("ultimo_uso_h", "Hora del último uso del teléfono", "Teléfono"),
            Variable("eventos", "Eventos en la agenda", "Agenda"), Variable("min_ocupado", "Minutos ocupados en agenda", "Agenda"),
            Variable("a_pie_min", "Minutos a pie", "Movilidad"), Variable("vehiculo_min", "Minutos en vehículo", "Movilidad"),
            Variable("ejercicio_min", "Minutos de ejercicio (reloj)", "Cuerpo"), Variable("escalada", "Escalé (0/1)", "Cuerpo"),
            Variable("escalada_grado", "Grado máximo encadenado", "Cuerpo"),
            Variable("llamadas", "Llamadas (entrantes y salientes)", "Social"), Variable("min_hablado", "Minutos al teléfono", "Social"),
            Variable("contactos", "Personas distintas por llamada o SMS", "Social"), Variable("wa_mios", "Mensajes de WhatsApp enviados", "Social"),
            Variable("wa_otros", "Mensajes de WhatsApp recibidos", "Social"), Variable("wa_chats", "Chats de WhatsApp activos", "Social"),
            Variable("audifonos_min", "Minutos con audífonos", "Teléfono"), Variable("coche_min", "Minutos en el coche (Bluetooth)", "Movilidad"),
            Variable("musica_min", "Minutos con audio sonando", "Teléfono"), Variable("sin_wifi_min", "Minutos sin wifi (fuera)", "Movilidad"),
            Variable("luz_alta_min", "Minutos con luz de exterior", "Entorno"), Variable("carga_noche_h", "Hora de conectar el teléfono de noche", "Sueño"),
            Variable("peso_kg", "Peso (kg)", "Cuerpo"), Variable("temp_piel", "Variación de temperatura de la piel", "Cuerpo"),
            Variable("resp_rpm", "Frecuencia respiratoria", "Cuerpo"), Variable("cal_activas", "Calorías activas", "Cuerpo"),
            Variable("yt_vistos", "Videos de YouTube vistos", "Teléfono"), Variable("yt_megusta", "«Me gusta» en YouTube", "Teléfono"),
            Variable("yt_comentarios", "Comentarios en YouTube", "Teléfono"), Variable("busquedas", "Búsquedas en Google", "Teléfono"),
            Variable("subrayados", "Subrayados de Kindle", "Lectura"), Variable("ruido_db", "Ruido medio al registrar (dB)", "Entorno"),
            Variable("sueno_inicio_h", "Hora de dormir (reloj)", "Sueño"), Variable("sueno_fin_h", "Hora de despertar (reloj)", "Sueño"),
            Variable("kcal", "Calorías consumidas", "Nutrición"), Variable("proteina_g", "Proteína (g)", "Nutrición"),
            Variable("carbohidratos_g", "Carbohidratos (g)", "Nutrición"), Variable("grasa_g", "Grasa (g)", "Nutrición"), Variable("fibra_g", "Fibra (g)", "Nutrición"),
            Variable("alcohol_g", "Alcohol (g)", "Nutrición"), Variable("cafeina_mg", "Cafeína (mg)", "Nutrición"), Variable("azucares_g", "Azúcares (g)", "Nutrición"),
            Variable("balance_kcal", "Calorías menos gasto estimado", "Nutrición"), Variable("peso_tendencia_kg", "Peso de tendencia (MacroFactor)", "Cuerpo"),
            Variable("primera_comida_h", "Hora del primer alimento", "Nutrición"), Variable("ultima_comida_h", "Hora del último alimento", "Nutrición"),
            Variable("cafeina_ultima_h", "Hora de la última cafeína", "Nutrición"), Variable("gimnasio", "Entrené en el gimnasio (0/1)", "Cuerpo"),
            Variable("volumen_kg", "Volumen del entrenamiento (kg)", "Cuerpo"), Variable("series_gimnasio", "Series efectivas de gimnasio", "Cuerpo"),
            Variable("fuerza_ref_kg", "Fuerza de referencia: 1RM estimada en press inclinado con mancuernas (kg)", "Cuerpo"),
            Variable("obras_dia", "Obras a las que les dediqué tiempo", "Cultura"), Variable("lectura", "Leí un libro o artículo (0/1)", "Cultura"),
            Variable("vi_pelicula_serie", "Vi una película o serie (0/1)", "Cultura"), Variable("jugue", "Jugué un videojuego (0/1)", "Cultura"),
            Variable("obra_terminada", "Terminé un libro, película, serie o juego (0/1)", "Cultura"), Variable("calificacion_obra", "Calificación de lo que terminé (1 a 10)", "Cultura"),
            Variable("citas_dia", "Citas y frases guardadas", "Cultura"),
            Variable("ciudades_dia", "Ciudades distintas en el día", "Mundo"), Variable("fuera_de_casa", "Fuera de mi ciudad (0/1)", "Mundo"),
            Variable("extranjero", "En otro país (0/1)", "Mundo"), Variable("ciudad_nueva", "Primera vez en una ciudad (0/1)", "Mundo"),
            Variable("unesco", "Pasé por un sitio del Patrimonio Mundial (0/1)", "Mundo"),
            Variable("carta_escrita", "Escribí una carta (0/1)", "Texto"), Variable("sueno_palabras", "Palabras del sueño escrito", "Sueño"),
            Variable("cartas_otros", "Cartas escritas a otras personas", "Social"),
            Variable("ingreso_total", "Ingresos del día", "Dinero"), Variable("balance_dinero", "Ingresos menos gastos del día", "Dinero"),
            Variable("dia_dificil", "Día difícil (0/1)", "Ánimo"), Variable("prediccion", "Predicción para mañana (1 a 7)", "Ánimo"),
            Variable("error_prediccion", "Cuánto falló la predicción de anoche", "Ánimo"),
            Variable("error_prediccion_signo", "El día contra la predicción de anoche (+ mejor, − peor)", "Ánimo"), Variable("medicamentos_n", "Medicamentos tomados", "Salud"),
            Variable("sueno_lucidez", "Lucidez del sueño (0 no, 1 parcial, 2 total)", "Sueño"), Variable("sueno_vividez", "Vividez del sueño (0 vago, 1 moderado, 2 muy vívido)", "Sueño"),
            Variable("n_madrugada", "Momentos de madrugada (antes de las 5)", "Momentos"), Variable("n_manana", "Momentos en la mañana (5 a 12)", "Momentos"),
            Variable("n_tarde", "Momentos en la tarde (12 a 19)", "Momentos"), Variable("n_noche", "Momentos en la noche (19 a 24)", "Momentos"),
            Variable("momentos_intensidad", "Intensidad media de los momentos (1 a 5)", "Momentos"), Variable("momentos_valencia", "Valencia media de los momentos", "Momentos"),
        )
        if (Repo.config.perfil.cercanas.isNotEmpty() || Repo.config.perfil.convivientes.isNotEmpty() || Repo.config.personas.isNotEmpty()) {
            v += Variable("cercanos", "Personas cercanas con las que estuve", "Social"); v += Variable("vi_familia", "Estuve con mi familia (0/1)", "Social")
            v += Variable("vi_pareja", "Estuve con mi pareja (0/1)", "Social"); v += Variable("vi_amistades", "Estuve con una amistad (0/1)", "Social")
            v += Variable("vi_convivientes", "Estuve con quien vive conmigo (0/1)", "Social")
        }
        // «Tu gente»: con la relación que había cada día.
        val hayEx = Repo.config.personas.any { it.relacion == "expareja" || it.historial.any { h -> h.relacion == "expareja" } }
        if (hayEx) v += Variable("vi_expareja", "Estuve con una expareja (0/1)", "Social")
        if (Repo.config.personas.any { Personas.grupo(it.relacion) == "trabajo" }) v += Variable("vi_trabajo", "Estuve con un colega, compañero o mentor (0/1)", "Social")
        // Ver a alguien no es lo mismo que pensar en esa persona o escribirse.
        if (Repo.dias.values.any { it.presencias.isNotEmpty() } || Repo.momentos.any { "pensando_en" in Catalogo.pensamientosDe(it) && Texto.personas(it.texto).isNotEmpty() }) {
            v += Variable("personas_vistas", "Personas con las que estuve", "Social"); v += Variable("personas_pensadas", "Personas en las que pensé sin verlas", "Social")
            v += Variable("personas_mensajes", "Personas con las que hablé por mensaje o llamada", "Social")
            if (Repo.config.personas.any { it.relacion == "pareja" } || Repo.config.perfil.pareja.isNotBlank()) v += Variable("penso_en_pareja", "Pensé en mi pareja sin verla (0/1)", "Social")
            if (hayEx) v += Variable("penso_en_expareja", "Pensé en una expareja sin verla (0/1)", "Social")
            v += Variable("penso_en_familia", "Pensé en mi familia sin verla (0/1)", "Social"); v += Variable("penso_en_amistades", "Pensé en una amistad sin verla (0/1)", "Social")
        }
        if (Repo.config.personas.any { it.cercania != null }) v += Variable("cercania_media", "Cercanía media de quien mencioné (1 a 7)", "Social")
        // El ánimo de todos los registros (avisos y los que haces tú), y lo que sentías y pensabas.
        if (Repo.momentos.any { it.esAnimo }) {
            v += Variable("animo_valencia", "Valencia promedio al momento (todos los registros)", "Ánimo")
            v += Variable("animo_activacion", "Activación promedio al momento (todos los registros)", "Ánimo")
            v += Variable("registros_animo", "Registros de ánimo en el día", "Momentos")
        }
        emocionesUsadas().forEach { id -> v += Variable("n_$id", "Registros con ${Catalogo.nombreSentimiento(id).lowercase()}", "Sentimientos") }
        pensamientosUsados().forEach { id -> v += Variable("n_$id", "Registros pensando: ${(Catalogo.PENSAMIENTOS.firstOrNull { it.id == id }?.nombre ?: id).lowercase()}", "Momentos") }
        // Sustancias: un dato normal (también las que ocultas de las pantallas).
        val sus = Repo.consumos.map { it.sustancia }.toSet()
        if ("alcohol" in sus || Repo.nutricion.values.any { (it.valores["alcohol_g"] ?: 0.0) > 0 }) {
            v += Variable("alcohol", "Tomé alcohol (0/1)", "Sustancias"); v += Variable("alcohol_bebidas", "Bebidas estándar", "Sustancias")
            v += Variable("alcohol_estimado_max", "Alcohol en sangre máximo estimado (g/L)", "Sustancias")
            v += Variable("alcohol_al_dormir", "Alcohol en sangre estimado al dormirme (g/L)", "Sustancias")
        }
        if ("alcohol" in sus) { v += Variable("alcohol_nivel_max", "Qué tan pedo llegué (0 a 10)", "Sustancias"); v += Variable("alcohol_horas", "Horas con efecto del alcohol", "Sustancias") }
        if ("cannabis" in sus) {
            v += Variable("cannabis", "Cannabis (0/1)", "Sustancias"); v += Variable("cannabis_nivel_max", "Nivel máximo con cannabis (0 a 10)", "Sustancias")
            v += Variable("cannabis_horas", "Horas con efecto del cannabis", "Sustancias")
        }
        if ("tabaco" in sus) v += Variable("tabaco_n", "Cigarros o sesiones de vape", "Sustancias")
        if ("otra" in sus) v += Variable("otra_sustancia", "Otra sustancia (0/1)", "Sustancias")
        if (Repo.consumos.any { it.cruda != null }) v += Variable("cruda", "Cruda o bajón (0 a 5, del día anterior)", "Sustancias")
        if ("cafeina" in sus || Repo.nutricion.values.any { (it.valores["cafeina_mg"] ?: 0.0) > 0 }) {
            v += Variable("cafeina_total_mg", "Cafeína del día (MacroFactor o anotada, mg)", "Sustancias")
            v += Variable("cafeina_al_dormir_mg", "Cafeína en el cuerpo al dormirme (mg, estimada)", "Sustancias")
        }
        // De qué fueron tus sueños.
        categoriasSuenoUsadas().forEach { id -> v += Variable("sueno_$id", "Sueño: ${Catalogo.opcionSueno(id)?.nombre?.lowercase() ?: id} (0/1)", "Sueños") }
        v += Variable("impulso", "Intensidad del impulso", "Privado"); v += Variable("recaida", "Recaída (0/1)", "Privado")
        v += Variable("n_recaida", "Recaídas registradas con hora", "Privado")
        if (Repo.momentos.any { it.tipo == "recaida" && it.duracion_min != null }) v += Variable("recaida_min", "Minutos de recaída", "Privado")
        // Enfoque, siestas y lo que se deriva del teléfono y el reloj.
        if (Repo.tramos.isNotEmpty()) {
            v += Variable("enfoque_min", "Minutos de enfoque (cronómetro)", "Enfoque")
            Repo.tramos.map { it.tipo }.filter { it != "siesta" }.distinct().forEach { t -> v += Variable("enfoque_${t}_min", "Enfoque: ${Catalogo.nombreTramo(t).lowercase()} (min)", "Enfoque") }
            if (Repo.tramos.any { it.tipo == "siesta" }) { v += Variable("siesta", "Tomé siesta (0/1)", "Sueño"); v += Variable("siesta_min", "Minutos de siesta", "Sueño") }
        }
        if (Repo.pantalla.values.any { it.cama_min != null }) {
            v += Variable("cama_min", "Pantalla en la última hora antes de dormir (min)", "Sueño")
            v += Variable("dormir_tras_tel_min", "Minutos entre soltar el teléfono y dormirte", "Sueño")
        }
        if (Repo.pantalla.values.any { it.tel_tras_despertar_min != null }) v += Variable("tel_tras_despertar_min", "Minutos entre despertar y tomar el teléfono", "Sueño")
        v += Variable("aire_libre_min", "Minutos al aire libre (estimado)", "Entorno")
        if (Repo.lugares.any { it.tipo == "casa" }) v += Variable("en_casa_min", "Minutos en casa", "Movilidad")
        // Horas en cada tipo de lugar (casa, trabajo, bar, naturaleza…).
        Repo.lugares.map { it.tipo }.distinct().sorted().forEach { t -> v += Variable("horas_tipo_$t", "Horas en lugares de tipo ${Catalogo.nombreTipoLugar(t).lowercase()}", "Lugares") }
        // El «Cómo estás ahora» al cerrar el día: la misma hora todos los días.
        if (Repo.momentos.any { it.esCierre }) {
            v += Variable("animo_cierre_valencia", "Valencia al cerrar el día", "Ánimo"); v += Variable("animo_cierre_activacion", "Activación al cerrar el día", "Ánimo")
        }
        // El barómetro del teléfono.
        if (Repo.conexiones.values.any { it.presion_n > 0 }) {
            v += Variable("presion_local_hpa", "Presión del barómetro del teléfono (hPa)", "Entorno"); v += Variable("cambio_presion_3h", "Mayor cambio de presión en 3 horas (hPa)", "Entorno")
        }
        if (Repo.momentos.any { it.tipo == "libido" }) {
            v += Variable("libido_max", "Libido máxima del día (0 a 5)", "Privado"); v += Variable("n_libido", "Registros de libido", "Privado")
        }
        // Esquema 21: masturbación e intimidad (privadas) y episodios.
        if (Repo.momentos.any { it.tipo == "masturbacion" }) {
            v += Variable("n_masturbacion", "Masturbaciones registradas", "Privado"); v += Variable("masturbacion_min", "Minutos de masturbación", "Privado")
            v += Variable("masturbacion_satisfaccion", "Satisfacción con la masturbación (1 a 5, promedio)", "Privado")
        }
        if (Repo.momentos.any { it.tipo == "intimidad" }) {
            v += Variable("n_intimidad", "Encuentros íntimos registrados", "Privado")
            v += Variable("intimidad_nivel", "Intimidad: nivel más alto del día (0 a distancia a 4 coito)", "Privado")
            v += Variable("intimidad_satisfaccion", "Intimidad: satisfacción (1 a 5, promedio)", "Privado")
            v += Variable("intimidad_comodidad", "Intimidad: comodidad (1 a 5, promedio)", "Privado")
            v += Variable("n_coito", "Encuentros con coito", "Privado")
            v += Variable("intimidad_distancia", "Intimidad solo a distancia (0/1)", "Privado")
        }
        if (Repo.momentos.any { it.tipo == "episodio" }) {
            v += Variable("n_episodio", "Episodios del día (crisis, llanto, pelea, salud…)", "Episodios")
            v += Variable("episodio_intensidad_max", "Intensidad del episodio más fuerte (1 a 5)", "Episodios")
            listOf("crisis" to "Crisis de ansiedad o pánico", "llanto" to "Llanto o bajón fuerte", "pelea" to "Pelea o discusión", "salud" to "Migraña, enfermedad o accidente")
                .forEach { (k, n) -> v += Variable("epi_$k", "$n (0/1)", "Episodios") }
        }
        // Suplementos: dosis, si se tomó y el nivel del modelo (o porciones).
        val sup = runCatching { io.github.ruddy2003.vestigium.data.Suplementos.panel() }.getOrDefault(emptyMap())
        val clavesSup = sup.values.flatMap { it.keys }.toSet()
        Repo.config.suplementos.forEach { s ->
            val u = s.unidad
            listOf("supl_${s.id}_dosis" to "${s.nombre}: dosis del día ($u)", "supl_${s.id}_tomado" to "${s.nombre}: lo tomaste (0/1)",
                "supl_${s.id}_n" to "${s.nombre}: tomas del día",
                "supl_${s.id}_nivel" to (if (s.modelo == "carga") "${s.nombre}: reserva estimada (0 a 100 %)" else "${s.nombre}: cantidad estimada en el cuerpo al final del día ($u)"),
                "supl_${s.id}_porciones" to "${s.nombre}: porciones del día").forEach { (k, n) -> if (k in clavesSup) v += Variable(k, n, "Suplementos") }
        }
        if ("creatina_saturacion_pct" in clavesSup) v += Variable("creatina_saturacion_pct", "Saturación de creatina estimada (%)", "Suplementos")
        if ("probioticos_porciones" in clavesSup) { v += Variable("probioticos_porciones", "Porciones de probióticos (anotadas y estimadas)", "Suplementos"); v += Variable("probioticos_estimado", "Porciones de probióticos leídas en tu bitácora de comidas", "Suplementos") }
        // La laptop (Cold Turkey).
        if (Repo.laptop.isNotEmpty()) {
            v += Variable("laptop_min", "Minutos en la laptop", "Laptop")
            if (Repo.laptop.values.any { it.sitios.isNotEmpty() }) v += Variable("laptop_web_min", "Minutos en sitios web (laptop)", "Laptop")
            io.github.ruddy2003.vestigium.data.ColdTurkey.CATEGORIAS.forEach { o -> v += Variable("laptop_${o.id}_min", "Minutos en la laptop: ${o.nombre.lowercase()}", "Laptop") }
        }
        if (Repo.momentos.any { it.con_quien.isNotEmpty() }) {
            v += Variable("acompanado_pct", "Avisos de ánimo con alguien (0 a 1)", "Social")
            Catalogo.CON_QUIEN.filter { it.id != "solo" }.forEach { o -> v += Variable("con_${o.id}", "Avisos con ${o.nombre.lowercase()} (0 a 1)", "Social") }
            v += Variable("valencia_acompanado", "Valencia en avisos con alguien", "Ánimo"); v += Variable("valencia_solo", "Valencia en avisos estando solo", "Ánimo")
        }
        Catalogo.CONTEXTOS.forEach { v += Variable("ctx_${it.id}", "Contexto: ${it.nombre} (0/1)", "Contexto") }
        Catalogo.CIENTIFICAS.forEach { p -> p.opciones.filter { it.id != "no_aplica" }.forEach { o -> v += Variable("${p.clave}_${o.id}", "${o.nombre} (0/1)", "Lectura del día") } }
        // Lo de antes (tipos de momento) que no es un sentimiento ni un pensamiento: el impulso.
        Catalogo.MOMENTOS.filter { it.id == "impulso" }.forEach { v += Variable("n_${it.id}", "Momentos de ${it.nombre.lowercase()}", "Privado") }
        Repo.config.habitos.forEach { v += Variable("hab_${it.id}", "Hábito: ${it.nombre} (0/1)", "Hábitos") }
        if (io.github.ruddy2003.vestigium.data.Especiales.hay()) v += Variable("dia_especial", "Día especial: cumpleaños, aniversario o fecha propia (0/1)", "Calendario")
        // Campañas (P-11): una por reto que hayas tenido, y cuántas tenías ese día.
        val camp = Repo.config.campanas
        if (camp.isNotEmpty()) {
            camp.map { it.reto }.distinct().forEach { r -> v += Variable(Campanas.variable(r), "En campaña: ${Campanas.reto(r)?.nombre ?: r} (0/1)", "Campañas") }
            v += Variable("campanas_n", "Campañas que tenías ese día", "Campañas")
        }
        if (dinamicas) v += Dinamicas.calcular(false).variables
        if (privadas) return v
        val ligado = Privado.habito()?.id
        return v.filter { !Privado.esVariable(it.id, ligado) }
    }

    private fun emocionesUsadas(): List<String> = Repo.momentos.flatMap { Catalogo.emocionesDe(it) }.filter { it != Catalogo.OTRO }.distinct().sorted()
    private fun pensamientosUsados(): List<String> = Repo.momentos.flatMap { Catalogo.pensamientosDe(it) }.distinct().sorted()
    private fun categoriasSuenoUsadas(): List<String> = Repo.dias.values.flatMap { Catalogo.categoriasSuenoDe(it) }.distinct().sorted()

    /**
     * Un «Cómo estás ahora» guardado otro día que el que cierra (H-2-8, P-26): no es el ánimo de esa noche, así que no cuenta
     * en el panel. Corrige también los cierres viejos, de antes de que solo se pudiera cerrar el día de hoy.
     */
    fun cierreFuera(m: io.github.ruddy2003.vestigium.data.Momento): Boolean =
        m.esCierre && Fechas.millis(m.ts).let { it > 0 && Fechas.diaLogicoDeMillis(it) != m.fecha }

    /**
     * Los días de [base] desde el primer registro de la fuente ([fuente]): en ellos «no hubo» vale 0; antes, sin dato (P-15).
     * Una sola regla para no confundir «no anoté» con «fue cero».
     */
    fun ceroDesde(fuente: Collection<String>, base: Collection<String>): List<String> {
        val p0 = fuente.minOrNull() ?: return emptyList()
        return base.filter { it >= p0 }
    }

    /**
     * Una fila por día con todas las variables numéricas. Lo que no existe queda en null. Con [dinamicas] incluye las de
     * cada persona, lugar, app…; con [extendido], todas las que aparecen al menos dos días (para la exportación).
     */
    fun panel(dinamicas: Boolean = true, extendido: Boolean = false): Map<String, Map<String, Double?>> {
        val eq = Repo.equivalencias()
        val filas = sortedMapOf<String, MutableMap<String, Double?>>()
        fun fila(f: String) = filas.getOrPut(f) { mutableMapOf() }
        val b = { x: Boolean -> if (x) 1.0 else 0.0 }
        // Las variables sociales solo valen 0 los días en que se pudo nombrar a alguien (P-15).
        val sociales = Dinamicas.diasSociales()

        for ((f, d) in Repo.dias) {
            val r = fila(f)
            r["valencia"] = d.valencia?.toDouble(); r["activacion"] = d.activacion?.toDouble(); r["dolor"] = d.dolor?.toDouble()
            r["sueno_calidad"] = d.sueno_calidad?.toDouble()
            // «No recordé un sueño» solo si ese día dijiste algo del sueño; si no, no se sabe.
            r["sueno_recordado"] = if (d.sueno_recordado) 1.0 else if (d.sueno_calidad != null || d.sueno_texto.isNotBlank()) 0.0 else null
            r["sueno_valencia"] = if (d.sueno_recordado) d.sueno_valencia?.toDouble() else null
            r["palabras"] = Texto.palabras(d.nota).toDouble()
            r["hitos_n"] = io.github.ruddy2003.vestigium.data.Senaladas.de(d).size.toDouble()
            r["impulso"] = d.impulso?.toDouble(); r["recaida"] = d.recaida?.let(b)
            Catalogo.CONTEXTOS.forEach { r["ctx_${it.id}"] = b(d.ctx[it.id] == true) }
            Catalogo.CIENTIFICAS.forEach { p ->
                val valor = when (p.clave) { "flow" -> d.flow; "sdt_necesidad" -> d.sdt_necesidad; else -> d.apreciacion }
                // «Las tres» también cuenta como autonomía, competencia y relación.
                p.opciones.filter { it.id != "no_aplica" }.forEach { o -> r["${p.clave}_${o.id}"] = valor?.let { b(it == o.id || p.clave == "sdt_necesidad" && it == "las_tres" && o.id in setOf("autonomia", "competencia", "relacion")) } }
            }
            // Un hábito vale 0 desde que existe; antes, sin dato (salvo que lo hayas marcado).
            // Con «sí / no / sin contestar» (esquema 17), sin respuesta queda vacío; en los días de antes, sin marca es 0.
            Repo.config.habitos.forEach { h -> r["hab_${h.id}"] = when { d.habitos[h.id] == true -> 1.0; h.desde.isNotBlank() && f < h.desde -> null; d.habitos_tri && h.id !in d.habitos -> null; else -> 0.0 } }
            if (d.presencias_ok || Texto.personas(d.nota).isNotEmpty()) r["presencias_revisado"] = b(d.presencias_ok)
        }
        // Personas distintas nombradas con @ en el diario y en los momentos no privados (H-14-2): los mismos días en que vale 0.
        val personasMomentos = Repo.momentos.filter { !it.privado }.groupBy { it.fecha }.mapValues { (_, l) -> l.flatMap { Texto.personas(it.texto) + it.personas } }
        for (f in sociales) fila(f)["personas"] = (Texto.personas(Repo.dias[f]?.nota.orEmpty()) + personasMomentos[f].orEmpty()).map { Texto.canon(it, eq) }.toSet().size.toDouble()
        // «No hubo» (0) desde el primer registro de cada fuente y solo en días con diario; antes, sin dato (P-15).
        ceroDesde(Repo.gastos.map { it.dia }, Repo.dias.keys).forEach { f -> val r = fila(f); r["gasto_total"] = 0.0; r["gasto_imprevisto"] = 0.0; r["gasto_imprevisto_monto"] = 0.0 }
        val primerImpulso = Repo.momentos.filter { it.tipo == "impulso" }.minOfOrNull { it.fecha }
        ceroDesde(listOfNotNull(primerImpulso), Repo.dias.keys).forEach { f -> fila(f)["n_impulso"] = 0.0 }
        val emoUsadas = emocionesUsadas(); val penUsados = pensamientosUsados()
        // Desde el primer registro de ánimo, los días sin registros cuentan como 0.
        Repo.momentos.filter { Catalogo.esRegistro(it) }.minOfOrNull { it.fecha }?.let { p0 ->
            Repo.dias.keys.filter { it >= p0 }.forEach { f -> val r = fila(f); emoUsadas.forEach { r["n_$it"] = 0.0 }; penUsados.forEach { r["n_$it"] = 0.0 }; r["registros_animo"] = 0.0 }
        }
        Repo.momentos.filter { !cierreFuera(it) }.groupBy { it.fecha }.forEach { (f, lista) ->
            val r = fila(f)
            val mu = lista.filter { it.tipo == "muestreo" }
            r["muestreo_valencia"] = media(mu.mapNotNull { it.valencia?.toDouble() })
            r["muestreo_activacion"] = media(mu.mapNotNull { it.activacion?.toDouble() })
            val an = lista.filter { it.esAnimo }
            if (an.isNotEmpty()) { r["animo_valencia"] = media(an.mapNotNull { it.valencia?.toDouble() }); r["animo_activacion"] = media(an.mapNotNull { it.activacion?.toDouble() }) }
            val regs = lista.filter { Catalogo.esRegistro(it) }
            r["registros_animo"] = regs.size.toDouble()
            emoUsadas.forEach { e -> r["n_$e"] = regs.count { e in Catalogo.emocionesDe(it) }.toDouble() }
            penUsados.forEach { t -> r["n_$t"] = regs.count { t in Catalogo.pensamientosDe(it) }.toDouble() }
            if (primerImpulso != null && f >= primerImpulso) r["n_impulso"] = lista.count { it.tipo == "impulso" }.toDouble()
            // Con quién: la proporción de avisos de ánimo con cada compañía, y el ánimo solo o acompañado.
            val con = mu.filter { it.con_quien.isNotEmpty() }
            if (con.isNotEmpty()) {
                val acomp = con.filter { "solo" !in it.con_quien }
                r["acompanado_pct"] = acomp.size.toDouble() / con.size
                Catalogo.CON_QUIEN.filter { it.id != "solo" }.forEach { o -> r["con_${o.id}"] = con.count { o.id in it.con_quien }.toDouble() / con.size }
                r["valencia_acompanado"] = media(acomp.mapNotNull { it.valencia?.toDouble() })
                r["valencia_solo"] = media(con.filter { "solo" in it.con_quien }.mapNotNull { it.valencia?.toDouble() })
            }
        }
        // Avisos de ánimo (v13): planeados, mostrados, respondidos y tasa de respuesta. Los días sin registro quedan sin dato.
        val respAv = io.github.ruddy2003.vestigium.data.AvisosAnimo.respondidos(Repo.momentos)
        Repo.avisos.values.forEach { a -> fila(a.fecha).putAll(io.github.ruddy2003.vestigium.data.AvisosAnimo.cifras(a, respAv)) }
        // Campañas (P-11): 1 los días que tenías aceptado ese reto, 0 los demás desde tu primera campaña; antes, sin dato.
        val camp = Repo.config.campanas
        if (camp.isNotEmpty()) {
            val hoyL = Fechas.diaLogico()
            val act = Campanas.diasActivos(camp, hoyL)
            val vars = camp.map { Campanas.variable(it.reto) }.distinct()
            val p0 = camp.minOf { Campanas.tramo(it).first }
            (Repo.dias.keys.filter { it in p0..hoyL } + act.keys).toSet().forEach { f ->
                val r = fila(f); val hoyActivas = act[f].orEmpty().mapTo(HashSet()) { Campanas.variable(it) }
                vars.forEach { k -> r[k] = if (k in hoyActivas) 1.0 else 0.0 }
                r["campanas_n"] = hoyActivas.size.toDouble()
            }
        }
        // Lo privado se calcula siempre: no sale en las pantallas (ver [variables]), pero sí en la exportación para R.
        run {
            val rec = Repo.momentos.filter { it.tipo == "recaida" }.groupingBy { it.fecha }.eachCount()
            rec.keys.minOrNull()?.let { p0 -> Repo.dias.keys.filter { it >= p0 }.forEach { f -> fila(f)["n_recaida"] = (rec[f] ?: 0).toDouble() } }
            rec.forEach { (f, n) -> fila(f)["n_recaida"] = n.toDouble() }
            Repo.momentos.filter { it.tipo == "recaida" && it.duracion_min != null }.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["recaida_min"] = l.sumOf { it.duracion_min ?: 0 }.toDouble() }
        }
        // Cronómetro: desde el primer tramo, los días sin nada cuentan como 0.
        if (Repo.tramos.isNotEmpty()) {
            val porDia = Repo.tramos.groupBy { it.fecha }
            val tipos = Repo.tramos.map { it.tipo }.filter { it != "siesta" }.distinct()
            val p0 = porDia.keys.min()
            (Repo.dias.keys.filter { it >= p0 } + porDia.keys).toSet().forEach { f ->
                val l = porDia[f].orEmpty(); val r = fila(f)
                r["enfoque_min"] = l.filter { it.tipo != "siesta" }.sumOf { it.min }.toDouble()
                tipos.forEach { t -> r["enfoque_${t}_min"] = l.filter { it.tipo == t }.sumOf { it.min }.toDouble() }
                if (Repo.tramos.any { it.tipo == "siesta" }) { val sm = l.filter { it.tipo == "siesta" }.sumOf { it.min }; r["siesta_min"] = sm.toDouble(); r["siesta"] = b(sm > 0) }
            }
        }
        Repo.gastos.groupBy { it.dia }.forEach { (f, lista) ->
            val r = fila(f); r["gasto_total"] = lista.sumOf { it.monto }; r["gasto_imprevisto"] = b(lista.any { it.imprevisto })
            // El monto aparte (H-1-5): gasto_imprevisto solo dice si hubo alguno.
            r["gasto_imprevisto_monto"] = lista.filter { it.imprevisto }.sumOf { it.monto }
        }
        Repo.salud.forEach { (f, s) ->
            val r = fila(f)
            r["sueno_horas"] = s.sueno_min?.let { it / 60.0 }; r["sueno_profundo"] = s.sueno_profundo_min?.toDouble()
            r["sueno_rem"] = s.sueno_rem_min?.toDouble(); r["hrv"] = s.hrv_rmssd; r["fc_reposo"] = s.fc_reposo; r["pasos"] = s.pasos?.toDouble()
            r["fc_media"] = s.fc_media; r["fc_sueno"] = s.fc_sueno; r["spo2"] = s.spo2; r["spo2_min"] = s.spo2_min
        }
        Repo.pantalla.forEach { (f, p) ->
            val r = fila(f); r["pantalla_min"] = p.total_min.toDouble(); r["desbloqueos"] = p.desbloqueos.toDouble()
            r["cama_min"] = p.cama_min?.toDouble(); r["dormir_tras_tel_min"] = p.dormir_tras_tel_min?.toDouble(); r["tel_tras_despertar_min"] = p.tel_tras_despertar_min?.toDouble()
        }
        Repo.reacciones.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["rt_media"] = media(l.mapNotNull { it.media_ms }) }
        // Por día lógico, como las horas en cada lugar: una visita que empieza a la 1:00 cuenta para el día anterior (P-16).
        Repo.visitas.groupBy { Fechas.diaLogicoDeMillis(Fechas.millis(it.entrada)) }.forEach { (f, l) -> fila(f)["lugares_distintos"] = l.map { it.lugar }.toSet().size.toDouble() }
        runCatching { Movilidad.kmPorDia() }.getOrDefault(emptyMap()).forEach { (f, km) -> fila(f)["distancia_km"] = km }
        Repo.entorno.forEach { (f, e) ->
            val r = fila(f)
            r["t_max"] = e.t_max; r["t_min"] = e.t_min; r["precip_mm"] = e.precip_mm; r["nubes_pct"] = e.nubes_pct
            r["luz_horas"] = e.luz_min?.let { it / 60.0 }; r["sol_horas"] = e.sol_min?.let { it / 60.0 }; r["presion_hpa"] = e.presion_hpa
            r["aqi_us"] = e.aqi_us?.toDouble(); r["pm25"] = e.pm25
        }
        Repo.fotos.forEach { (f, x) -> fila(f)["fotos"] = x.fotos.toDouble() }
        Repo.notifs.forEach { (f, x) -> fila(f)["notificaciones"] = x.total.toDouble() }
        Repo.agenda.forEach { (f, x) -> val r = fila(f); r["eventos"] = x.eventos.toDouble(); r["min_ocupado"] = x.min_ocupado.toDouble() }
        Repo.actividad.forEach { (f, x) -> val r = fila(f); r["a_pie_min"] = (x.minutos["a_pie"] ?: 0).toDouble(); r["vehiculo_min"] = (x.minutos["vehiculo"] ?: 0).toDouble() }
        // Al aire libre: la luz de exterior que mide el teléfono o, si es más, el tiempo caminando, corriendo o en bici.
        (Repo.conexiones.keys + Repo.actividad.keys).forEach { f ->
            val luz = Repo.conexiones[f]?.luz_alta_min
            val mov = Repo.actividad[f]?.minutos?.let { (it["a_pie"] ?: 0) + (it["corriendo"] ?: 0) + (it["bici"] ?: 0) }
            if (luz != null || mov != null) fila(f)["aire_libre_min"] = maxOf(luz ?: 0, mov ?: 0).toDouble()
        }
        // En casa: el tiempo dentro de los lugares que marcaste como casa, recortado a cada día.
        val casas = Repo.lugares.filter { it.tipo == "casa" }.map { it.id }.toSet()
        if (casas.isNotEmpty()) {
            val enCasa = HashMap<String, Double>()
            val ahoraMs = System.currentTimeMillis()
            Repo.visitas.filter { it.lugar in casas }.forEach { v ->
                var a = Fechas.millis(v.entrada); val z = v.salida?.let { Fechas.millis(it) } ?: ahoraMs
                if (a <= 0 || z <= a || z - a > 40L * 86_400_000L) return@forEach
                while (a < z) {
                    val f = Fechas.diaLogicoDeMillis(a)
                    // El fin del día lógico con su zona (antes, medianoche + 5 h de reloj: la noche en que se atrasa el
                    // reloj caía a las 04:00, del mismo día lógico, y el ciclo no terminaba nunca).
                    val finDia = Fechas.finDiaLogico(f)
                    val y = minOf(z, finDia)
                    if (y <= a) break
                    enCasa[f] = (enCasa[f] ?: 0.0) + (y - a) / 60_000.0
                    a = y
                }
            }
            enCasa.forEach { (f, m) -> fila(f)["en_casa_min"] = Math.round(minOf(m, 1440.0)).toDouble() }
        }
        // Horas por tipo de lugar: desde el primer día con visitas, los días sin nada cuentan como 0.
        if (Repo.lugares.isNotEmpty()) {
            val porTipo = io.github.ruddy2003.vestigium.data.Lugares.minutosPorTipo()
            val tipos = Repo.lugares.map { it.tipo }.toSet()
            porTipo.keys.minOrNull()?.let { p0 ->
                (Repo.dias.keys.filter { it >= p0 } + porTipo.keys).toSet().forEach { f ->
                    val r = fila(f); tipos.forEach { t -> r["horas_tipo_$t"] = Math.round((porTipo[f]?.get(t) ?: 0.0) / 60.0 * 100) / 100.0 }
                }
            }
        }
        Repo.momentos.filter { it.esCierre && !cierreFuera(it) }.groupBy { it.fecha }.forEach { (f, l) ->
            val m = l.maxBy { it.ts }; val r = fila(f); r["animo_cierre_valencia"] = m.valencia?.toDouble(); r["animo_cierre_activacion"] = m.activacion?.toDouble()
        }
        val ajusteBar = io.github.ruddy2003.vestigium.data.Barometro.ajuste()
        Repo.conexiones.forEach { (f, c) -> if (c.presion_n > 0) { val r = fila(f); r["presion_local_hpa"] = io.github.ruddy2003.vestigium.data.Barometro.local(c, ajusteBar); r["cambio_presion_3h"] = c.cambio_presion_3h } }
        Repo.momentos.filter { it.tipo == "libido" }.groupBy { it.fecha }.forEach { (f, l) ->
            val r = fila(f); r["libido_max"] = l.mapNotNull { it.intensidad }.maxOrNull()?.toDouble(); r["n_libido"] = l.size.toDouble()
        }
        // Esquema 21. Masturbación e intimidad: desde el primer registro, los días con diario sin nada cuentan como 0 (P-15).
        fun ceroTipo(tipo: String, claves: List<String>) {
            val p0 = Repo.momentos.filter { it.tipo == tipo }.minOfOrNull { it.fecha } ?: return
            ceroDesde(listOf(p0), Repo.dias.keys).forEach { f -> val r = fila(f); claves.forEach { r[it] = 0.0 } }
        }
        ceroTipo("masturbacion", listOf("n_masturbacion")); ceroTipo("intimidad", listOf("n_intimidad", "n_coito")); ceroTipo("episodio", listOf("n_episodio", "epi_crisis", "epi_llanto", "epi_pelea", "epi_salud"))
        Repo.momentos.filter { it.tipo == "masturbacion" }.groupBy { it.fecha }.forEach { (f, l) ->
            val r = fila(f); r["n_masturbacion"] = l.size.toDouble()
            l.mapNotNull { it.duracion_min }.takeIf { it.isNotEmpty() }?.let { r["masturbacion_min"] = it.sum().toDouble() }
            r["masturbacion_satisfaccion"] = media(l.mapNotNull { it.satisfaccion?.toDouble() })
        }
        Repo.momentos.filter { it.tipo == "intimidad" }.groupBy { it.fecha }.forEach { (f, l) ->
            val r = fila(f); r["n_intimidad"] = l.size.toDouble()
            r["intimidad_nivel"] = l.mapNotNull { it.nivel ?: Catalogo.nivelIntimidad(it.practicas) }.maxOrNull()?.toDouble()
            r["intimidad_satisfaccion"] = media(l.mapNotNull { it.satisfaccion?.toDouble() })
            r["intimidad_comodidad"] = media(l.mapNotNull { it.comodidad?.toDouble() })
            r["n_coito"] = l.count { m -> m.practicas.any { it.startsWith("coito_") } }.toDouble()
            val niveles = l.flatMap { m -> m.practicas.mapNotNull { Catalogo.practica(it)?.nivel } }
            r["intimidad_distancia"] = if (niveles.isEmpty()) null else if (niveles.all { it == 0 }) 1.0 else 0.0
        }
        Repo.momentos.filter { it.tipo == "episodio" }.groupBy { it.fecha }.forEach { (f, l) ->
            val r = fila(f); r["n_episodio"] = l.size.toDouble()
            r["episodio_intensidad_max"] = l.mapNotNull { it.intensidad }.maxOrNull()?.toDouble()
            val grupos = l.mapNotNull { m -> Catalogo.EPISODIOS.firstOrNull { it.id == m.episodio }?.detalle?.takeIf { it.isNotBlank() } }.toSet()
            listOf("crisis", "llanto", "pelea", "salud").forEach { k -> r["epi_$k"] = if (k in grupos) 1.0 else 0.0 }
        }
        runCatching { io.github.ruddy2003.vestigium.data.Suplementos.panel().forEach { (f, m) -> val r = fila(f); m.forEach { (k, x) -> r[k] = x } } }
        Repo.laptop.forEach { (f, d) -> val r = fila(f); io.github.ruddy2003.vestigium.data.ColdTurkey.resumen(d).forEach { (k, x) -> r[k] = x } }
        // Ejercicio: 0 los días con datos del reloj y sin sesión, desde la primera sesión.
        val sesiones = Repo.sesiones.groupBy { it.fecha }
        ceroDesde(sesiones.keys, Repo.salud.filterValues { it.pasos != null || it.fc_media != null || it.fc_reposo != null }.keys).forEach { f -> fila(f)["ejercicio_min"] = 0.0 }
        sesiones.forEach { (f, l) -> fila(f)["ejercicio_min"] = l.sumOf { it.min }.toDouble() }
        Repo.pantalla.forEach { (f, p) ->
            val r = fila(f)
            fun h(iso: String?): Double? = iso?.let { val t = Fechas.localDe(it); t.hour + t.minute / 60.0 }
            r["primer_uso_h"] = runCatching { h(p.primer_uso) }.getOrNull()
            r["ultimo_uso_h"] = runCatching { h(p.ultimo_uso)?.let { if (it < 5) it + 24 else it } }.getOrNull()
        }
        val diasEsc = Repo.ascensos.groupBy { it.fecha }
        ceroDesde(diasEsc.keys, Repo.dias.keys).forEach { f -> fila(f)["escalada"] = if (diasEsc.containsKey(f)) 1.0 else 0.0 }
        diasEsc.forEach { (f, l) ->
            val r = fila(f); r["escalada"] = 1.0
            r["escalada_grado"] = l.flatMap { it.bloques }.filter { io.github.ruddy2003.vestigium.data.Grados.encadenado(it.estilo) }.mapNotNull { io.github.ruddy2003.vestigium.data.Grados.valor(it.grado) }.maxOrNull()
        }
        fun hd(iso: String?, noche: Boolean) = iso?.let { io.github.ruddy2003.vestigium.data.Exportar.horaDecimal(it, noche) }
        Repo.comunicacion.forEach { (f, c) -> val r = fila(f); r["llamadas"] = (c.entrantes + c.salientes).toDouble(); r["min_hablado"] = c.min_hablado; r["contactos"] = c.contactos.toDouble() }
        Repo.chats.groupBy { it.fecha }.forEach { (f, l) -> val r = fila(f); r["wa_mios"] = l.sumOf { it.mios }.toDouble(); r["wa_otros"] = l.sumOf { it.otros }.toDouble(); r["wa_chats"] = l.size.toDouble() }
        Repo.conexiones.forEach { (f, c) ->
            val r = fila(f)
            r["audifonos_min"] = (c.audio_min["audifonos"] ?: 0).toDouble(); r["coche_min"] = (c.audio_min["coche"] ?: 0).toDouble()
            r["musica_min"] = c.musica_min.toDouble(); r["sin_wifi_min"] = c.sin_wifi_min.toDouble(); r["luz_alta_min"] = c.luz_alta_min.toDouble()
            r["carga_noche_h"] = hd(c.carga_noche_inicio, true)
        }
        Repo.salud.forEach { (f, x) ->
            val r = fila(f)
            r["peso_kg"] = x.peso_kg; r["temp_piel"] = x.temp_piel_delta; r["resp_rpm"] = x.resp_rpm; r["cal_activas"] = x.cal_activas
            r["sueno_inicio_h"] = hd(x.sueno_inicio, true); r["sueno_fin_h"] = hd(x.sueno_fin, false)
        }
        Repo.nutricion.forEach { (f, n) ->
            val r = fila(f)
            listOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "cafeina_mg", "azucares_g", "peso_tendencia_kg").forEach { k -> r[k] = n.valores[k] }
            val k = n.valores["kcal"]; val g = n.valores["gasto_kcal"]
            r["balance_kcal"] = if (k != null && g != null) k - g else null
            r["primera_comida_h"] = n.primera?.let { io.github.ruddy2003.vestigium.data.Exportar.horaTexto(it) }
            r["ultima_comida_h"] = n.ultima?.let { io.github.ruddy2003.vestigium.data.Exportar.horaNoche(it) }
            r["cafeina_ultima_h"] = n.cafeina_ultima?.let { io.github.ruddy2003.vestigium.data.Exportar.horaTexto(it) }
        }
        Repo.entrenos.forEach { (f, e) -> val r = fila(f); r["volumen_kg"] = e.volumen_kg; r["series_gimnasio"] = e.series_efectivas.toDouble(); e.fuerza_ref_kg?.let { r["fuerza_ref_kg"] = it } }
        (ceroDesde(Repo.entrenos.keys, Repo.dias.keys) + Repo.entrenos.keys).toSet().forEach { f -> fila(f)["gimnasio"] = if ((Repo.entrenos[f]?.series ?: 0) > 0) 1.0 else 0.0 }
        Repo.huellas.forEach { (f, h) -> val r = fila(f); r["yt_vistos"] = h.yt_vistos.toDouble(); r["busquedas"] = (h.busquedas + h.yt_busquedas).toDouble() }
        // «Me gusta» y comentarios: cuentan desde el primero que hay (antes no se sabe), en días con historial de YouTube.
        if (Repo.youtube.isNotEmpty()) {
            val mg = Repo.youtube.filter { it.tipo == "megusta" && it.fecha.isNotBlank() }.groupingBy { it.fecha }.eachCount()
            val co = Repo.youtube.filter { (it.tipo == "comentario" || it.tipo == "chat") && it.fecha.isNotBlank() }.groupingBy { it.fecha }.eachCount()
            val d0 = mg.keys.minOrNull(); val c0 = co.keys.minOrNull()
            Repo.huellas.keys.forEach { f ->
                if (d0 != null && f >= d0) fila(f)["yt_megusta"] = (mg[f] ?: 0).toDouble()
                if (c0 != null && f >= c0) fila(f)["yt_comentarios"] = (co[f] ?: 0).toDouble()
            }
        }
        Repo.subrayados.filter { it.fecha != null }.groupBy { it.fecha!! }.forEach { (f, l) -> fila(f)["subrayados"] = l.size.toDouble() }
        Repo.momentos.filter { it.ruido_db != null }.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["ruido_db"] = l.mapNotNull { it.ruido_db }.average() }
        runCatching {
            val desde = filas.keys.firstOrNull() ?: return@runCatching
            // Canción = escuchada 30 s o más (lo que dice el diccionario; P-30). Sin la duración escuchada, cuenta.
            Repo.escuchas(desde, "9999-12-31").filter { (it.ms ?: 30_000) >= 30_000 }.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["canciones"] = l.size.toDouble() }
        }
        // Lo que leíste, viste y jugaste: desde el primer día con una obra marcada, los días sin nada cuentan como 0.
        val obrasPorDia = HashMap<String, MutableList<io.github.ruddy2003.vestigium.data.Obra>>()
        Repo.obras.forEach { o -> o.dias.forEach { f -> obrasPorDia.getOrPut(f) { ArrayList() }.add(o) } }
        val terminadas = Repo.obras.filter { it.estado == "terminado" && it.fin != null }.groupBy { it.fin!! }
        val primeraObra = (obrasPorDia.keys + terminadas.keys).minOrNull()
        if (primeraObra != null) {
            (Repo.dias.keys.filter { it >= primeraObra } + obrasPorDia.keys + terminadas.keys).toSet().forEach { f ->
                val l = obrasPorDia[f].orEmpty(); val t = terminadas[f].orEmpty()
                val r = fila(f)
                r["obras_dia"] = l.size.toDouble()
                r["lectura"] = b(l.any { it.tipo == "libro" || it.tipo == "articulo" } || t.any { it.tipo == "libro" || it.tipo == "articulo" })
                r["vi_pelicula_serie"] = b(l.any { it.tipo == "pelicula" || it.tipo == "serie" } || t.any { it.tipo == "pelicula" || it.tipo == "serie" })
                r["jugue"] = b(l.any { it.tipo == "juego" } || t.any { it.tipo == "juego" })
                r["obra_terminada"] = b(t.isNotEmpty())
                r["calificacion_obra"] = media(t.mapNotNull { it.calificacion?.toDouble() })
            }
        }
        val citas = HashMap<String, Int>()
        Repo.obras.forEach { o -> o.citas.forEach { c -> if (c.fecha.isNotBlank()) citas.merge(c.fecha, 1, Int::plus) } }
        Repo.config.frases_guardadas.forEach { citas.merge(it.fecha, 1, Int::plus) }
        citas.forEach { (f, n) -> fila(f)["citas_dia"] = n.toDouble() }
        // Cartas y sueños escritos.
        val cartas = Repo.cartas.groupingBy { it.escrita }.eachCount()
        val primeraCarta = cartas.keys.minOrNull()
        if (primeraCarta != null) Repo.dias.keys.filter { it >= primeraCarta }.forEach { f -> fila(f)["carta_escrita"] = b((cartas[f] ?: 0) > 0) }
        cartas.forEach { (f, _) -> fila(f)["carta_escrita"] = 1.0 }
        for ((f, d) in Repo.dias) if (d.sueno_recordado || d.sueno_texto.isNotBlank()) fila(f)["sueno_palabras"] = Texto.palabras(d.sueno_texto).toDouble()
        // Las personas del diario y los momentos: con quién estuviste, en quién solo pensaste (y su relación ese día).
        val perfil = Repo.config.perfil
        run {
            val momentosPorDia = Repo.momentos.groupBy { it.fecha }
            // Con el nombre unido (P-25), como los nombres del día.
            val convivientes = perfil.convivientes.map { Texto.canon(it.lowercase().removePrefix("@"), eq) }.toSet()
            for (f in (Repo.dias.keys + momentosPorDia.keys).toSet()) {
                if (f !in sociales) continue
                // Quién estuvo de verdad, en quién solo pensaste y con quién hablaste por mensaje (el diario manda).
                val estados = io.github.ruddy2003.vestigium.data.Presencias.delDia(f, eq, Repo.dias[f], momentosPorDia[f].orEmpty())
                val nombres = estados.keys.toList()
                fun rel(lista: Collection<String>) = lista.mapNotNull { io.github.ruddy2003.vestigium.data.Perfiles.relacion(it, perfil, f)?.let { r -> it to r } }
                // La relación que había ese día (pareja hasta marzo, expareja después).
                val vistos = rel(estados.filterValues { it == io.github.ruddy2003.vestigium.data.Presencias.ESTUVE }.keys)
                val pensados = rel(estados.filterValues { it == io.github.ruddy2003.vestigium.data.Presencias.PENSE }.keys)
                val r = fila(f)
                r["cercanos"] = vistos.count { it.second in setOf("familia", "pareja", "amistad", "casa") }.toDouble()
                r["vi_familia"] = b(vistos.any { it.second == "familia" }); r["vi_pareja"] = b(vistos.any { it.second == "pareja" })
                r["vi_amistades"] = b(vistos.any { it.second == "amistad" }); r["vi_convivientes"] = b(vistos.any { it.first in convivientes })
                r["vi_expareja"] = b(vistos.any { it.second == "expareja" }); r["vi_trabajo"] = b(vistos.any { it.second == "trabajo" })
                r["cercania_media"] = media(nombres.mapNotNull { Personas.cercaniaEn(it, f)?.toDouble() })
                r["personas_vistas"] = estados.count { it.value == io.github.ruddy2003.vestigium.data.Presencias.ESTUVE }.toDouble()
                r["personas_pensadas"] = estados.count { it.value == io.github.ruddy2003.vestigium.data.Presencias.PENSE }.toDouble()
                r["personas_mensajes"] = estados.count { it.value == io.github.ruddy2003.vestigium.data.Presencias.MENSAJES }.toDouble()
                r["penso_en_pareja"] = b(pensados.any { it.second == "pareja" }); r["penso_en_expareja"] = b(pensados.any { it.second == "expareja" })
                r["penso_en_familia"] = b(pensados.any { it.second == "familia" }); r["penso_en_amistades"] = b(pensados.any { it.second == "amistad" })
            }
        }
        // Dinero que entra, y el balance del día.
        val ingresos = Repo.ingresos.groupBy { it.dia }.mapValues { (_, l) -> l.sumOf { it.monto } }
        val primerIngreso = ingresos.keys.minOrNull()
        if (primerIngreso != null) {
            (Repo.dias.keys.filter { it >= primerIngreso } + ingresos.keys).toSet().forEach { f ->
                val r = fila(f); val i = ingresos[f] ?: 0.0
                r["ingreso_total"] = i; r["balance_dinero"] = i - (r["gasto_total"] ?: Repo.gastos.filter { it.dia == f }.sumOf { it.monto })
            }
        }
        // Lo demás del diario: día difícil, predicción y su error, medicamentos, detalle del sueño.
        val conMedicamentos = Repo.dias.values.filter { it.medicamentos.isNotEmpty() }.minOfOrNull { it.fecha }
        for ((f, d) in Repo.dias) {
            val r = fila(f)
            r["dia_dificil"] = b(d.dia_dificil)
            r["prediccion"] = d.prediccion_manana?.toDouble()
            // Con signo (H-2-10): positivo si el día salió mejor de lo que predijiste; el absoluto pierde la dirección.
            Repo.dias[Fechas.sumar(f, -1)]?.prediccion_manana?.let { p -> d.valencia?.let { r["error_prediccion"] = abs(p - it).toDouble(); r["error_prediccion_signo"] = (it - p).toDouble() } }
            if (conMedicamentos != null && f >= conMedicamentos) r["medicamentos_n"] = d.medicamentos.size.toDouble()
            if (d.sueno_recordado) {
                r["sueno_lucidez"] = when (d.sueno_lucidez) { "no" -> 0.0; "parcial" -> 1.0; "total" -> 2.0; else -> null }
                r["sueno_vividez"] = when (d.sueno_vividez) { "vago" -> 0.0; "moderado" -> 1.0; "muy_vivido" -> 2.0; else -> null }
            }
            listOf("n_madrugada", "n_manana", "n_tarde", "n_noche").forEach { r[it] = 0.0 }
        }
        // Momentos según la hora, su intensidad y su valencia.
        Repo.momentos.filter { it.tipo != "muestreo" && !it.privado }.groupBy { it.fecha }.forEach { (f, l) ->
            val r = fila(f)
            val horas = l.mapNotNull { Fechas.horaDe(it.ts).take(2).toIntOrNull() }
            r["n_madrugada"] = horas.count { it < 5 }.toDouble(); r["n_manana"] = horas.count { it in 5..11 }.toDouble()
            r["n_tarde"] = horas.count { it in 12..18 }.toDouble(); r["n_noche"] = horas.count { it >= 19 }.toDouble()
            r["momentos_intensidad"] = media(l.mapNotNull { it.intensidad?.toDouble() })
            r["momentos_valencia"] = media(l.mapNotNull { it.valencia?.toDouble() })
        }
        // De qué fueron tus sueños: solo en los días en que marcaste alguna categoría.
        val catSueno = categoriasSuenoUsadas()
        if (catSueno.isNotEmpty()) for ((f, d) in Repo.dias) { val cats = Catalogo.categoriasSuenoDe(d); if (cats.isNotEmpty()) {
            val r = fila(f); catSueno.forEach { k -> r["sueno_$k"] = b(k in cats) } }
        }
        runCatching { sustancias(filas.keys.firstOrNull(), b) { f -> fila(f) } }
        // Cartas a otras personas.
        val aOtros = Repo.cartas.filter { it.para.isNotBlank() }.groupingBy { it.escrita }.eachCount()
        aOtros.keys.minOrNull()?.let { a -> Repo.dias.keys.filter { it >= a }.forEach { f -> fila(f)["cartas_otros"] = (aOtros[f] ?: 0).toDouble() } }
        aOtros.forEach { (f, n) -> fila(f)["cartas_otros"] = n.toDouble() }
        // Una variable por cada persona, tema, lugar, app, categoría, artista, obra, ciudad, nutriente, cuestionario, etapa de vida…
        if (dinamicas) runCatching { Dinamicas.calcular(extendido).valores.forEach { (f, m) -> val r = fila(f); m.forEach { (k, x) -> r[k] = x } } }
        // Dónde estuviste: ciudades, fuera de casa, otro país, primera vez y Patrimonio Mundial (de tus ubicaciones).
        runCatching {
            val ctx = io.github.ruddy2003.vestigium.data.Mundo.app ?: return@runCatching
            // En el hilo principal solo se usa lo ya calculado: recorrer todas las ubicaciones tarda.
            val enMain = android.os.Looper.myLooper() == android.os.Looper.getMainLooper()
            val diario = (if (enMain) io.github.ruddy2003.vestigium.data.Mundo.diarioListo() else io.github.ruddy2003.vestigium.data.Mundo.diario(ctx)) ?: return@runCatching
            if (diario.isEmpty()) return@runCatching
            val r0 = io.github.ruddy2003.vestigium.data.Mundo.resumenFijo ?: (if (enMain) null else io.github.ruddy2003.vestigium.data.Mundo.resumen(ctx))
            val casa = io.github.ruddy2003.vestigium.data.Perfiles.casa(perfil) ?: r0?.ciudades?.firstOrNull()?.let { it.lat to it.lon }
            val paisCasa = perfil.pais.ifBlank { r0?.paises?.firstOrNull()?.clave.orEmpty() }
            for ((d, m) in diario) {
                val f = Fechas.iso(java.time.LocalDate.ofEpochDay(d.toLong()))
                val ciudades = m.ciudades.filter { it.second >= 0.5 || it.second == 0.0 }.map { it.first }
                val paises = m.paises.filter { it.second >= 0.5 || it.second == 0.0 }.map { it.first }
                val r = fila(f)
                r["ciudades_dia"] = ciudades.size.toDouble()
                if (casa != null && ciudades.isNotEmpty()) r["fuera_de_casa"] = b(ciudades.none { io.github.ruddy2003.vestigium.data.Geo.distanciaM(it.lat, it.lon, casa.first, casa.second) < 40_000 })
                if (paisCasa.isNotBlank() && paises.isNotEmpty()) r["extranjero"] = b(paises.any { it.clave != paisCasa })
                r["ciudad_nueva"] = b(m.ciudadesNuevas.isNotEmpty())
                r["unesco"] = b(m.sitios.isNotEmpty())
            }
        }
        // La luna de todos los días del panel, también los que solo tienen nutrición o entreno (y así en vm_panel.csv).
        filas.forEach { (f, r) -> r["luna_ilum"] = io.github.ruddy2003.vestigium.data.Luna.iluminacion(io.github.ruddy2003.vestigium.data.Luna.fase(f)) }
        // Días especiales (I-6): 1 si ese día cae un cumpleaños, un aniversario o una fecha propia. Es del calendario:
        // vale para todos los días del panel.
        if (io.github.ruddy2003.vestigium.data.Especiales.hay()) filas.forEach { (f, r) -> r["dia_especial"] = b(io.github.ruddy2003.vestigium.data.Especiales.de(f).isNotEmpty()) }
        return filas
    }

    /**
     * Las variables de sustancias. Desde el primer registro de cada una, los días sin nada cuentan como 0. La cafeína y el
     * alcohol en el cuerpo se estiman con sus dosis y su hora (MacroFactor o lo anotado); «al dormirme» va en el día del sueño.
     */
    @Volatile private var dosisCache: Pair<String, Pair<List<Sustancias.Dosis>, List<Sustancias.Dosis>>>? = null

    private fun sustancias(desde: String?, b: (Boolean) -> Double, fila: (String) -> MutableMap<String, Double?>) {
        val todos = Repo.consumos
        val primero = (todos.map { it.fecha } + listOfNotNull(Repo.nutricion.keys.minOrNull())).minOrNull() ?: return
        val ini = listOfNotNull(desde, primero).minOrNull() ?: primero
        val hoy = Fechas.diaLogico()
        val dias = Repo.dias.keys + todos.map { it.fecha }
        val porDia = todos.groupBy { it.fecha }
        fun desdePrimero(id: String, k: (String, List<io.github.ruddy2003.vestigium.data.Consumo>) -> Unit) {
            val p0 = todos.filter { it.sustancia == id }.minOfOrNull { it.fecha } ?: return
            dias.filter { it >= p0 }.forEach { f -> k(f, porDia[f].orEmpty().filter { it.sustancia == id }) }
        }
        desdePrimero("alcohol") { f, l ->
            val r = fila(f)
            // Una sola regla de día (P-20): bebiste el día lógico de cada dosis. Aquí solo cuentan los episodios sin cantidad
            // (no tienen dosis); los demás los marca el día de sus dosis, abajo.
            r["alcohol"] = b(l.any { (it.cantidad ?: 0.0) <= 0.0 })
            r["alcohol_nivel_max"] = if (l.isEmpty()) 0.0 else l.mapNotNull { Sustancias.maximo(it) }.maxOrNull()?.toDouble()
            r["alcohol_horas"] = if (l.isEmpty()) 0.0 else l.mapNotNull { Sustancias.horas(it) }.takeIf { it.isNotEmpty() }?.sum()
        }
        desdePrimero("cannabis") { f, l ->
            val r = fila(f)
            r["cannabis"] = b(l.isNotEmpty())
            r["cannabis_nivel_max"] = if (l.isEmpty()) 0.0 else l.mapNotNull { Sustancias.maximo(it) }.maxOrNull()?.toDouble()
            r["cannabis_horas"] = if (l.isEmpty()) 0.0 else l.mapNotNull { Sustancias.horas(it) }.takeIf { it.isNotEmpty() }?.sum()
        }
        desdePrimero("tabaco") { f, l -> fila(f)["tabaco_n"] = l.sumOf { it.cantidad ?: 1.0 } }
        desdePrimero("otra") { f, l -> fila(f)["otra_sustancia"] = b(l.isNotEmpty()) }
        // La cruda se siente al día siguiente: va en ese día.
        todos.filter { it.cruda != null }.groupBy { Fechas.sumar(it.fecha, 1) }.forEach { (f, l) -> fila(f)["cruda"] = l.maxOf { it.cruda!! }.toDouble() }
        // Leer todos los alimentos tarda: en el hilo principal se usa lo último calculado o solo lo anotado aquí.
        // La clave cambia con cada importación de MacroFactor (el mapa se reemplaza), no solo cuando cambia el número de días (P-21).
        val clave = "${todos.hashCode()}|${System.identityHashCode(Repo.nutricion)}|${Repo.nutricion.size}|$ini|$hoy"
        val enMain = android.os.Looper.myLooper() == android.os.Looper.getMainLooper()
        // En el hilo principal sin lo ya calculado, lo que depende de MacroFactor queda vacío: con solo lo anotado aquí saldría
        // otra cifra que en las demás pantallas (P-21). También «bebiste»: sin MacroFactor no se sabe si un día sin episodios
        // fue 0 (H-14-4); los días con un episodio anotado sí bebiste.
        val enCache = dosisCache?.takeIf { it.first == clave }?.second
        if (enCache == null && enMain && Repo.nutricion.isNotEmpty()) {
            desdePrimero("alcohol") { f, l -> fila(f)["alcohol"] = if (l.isEmpty()) null else 1.0 }
            // La última cafeína también sale de las dosis (H-4-12): sin ellas, vacía, no la de MacroFactor por día de calendario.
            Repo.nutricion.keys.forEach { f -> fila(f)["cafeina_ultima_h"] = null }
            return
        }
        val (caf, alc) = enCache ?: Sustancias.dosis(ini, hoy, if (enMain) emptyList() else null).also { if (!enMain) dosisCache = clave to it }
        val gramos = HashMap<String, Double>()
        alc.forEach { d -> val f = Fechas.diaLogicoDeMillis(d.ms); gramos[f] = (gramos[f] ?: 0.0) + d.cantidad }
        val mg = HashMap<String, Double>()
        caf.forEach { d -> val f = Fechas.diaLogicoDeMillis(d.ms); mg[f] = (mg[f] ?: 0.0) + d.cantidad }
        val alcDesde = (alc.minOfOrNull { it.ms }?.let { Fechas.diaLogicoDeMillis(it) })
        if (alcDesde != null) dias.filter { it >= alcDesde }.forEach { f ->
            val r = fila(f); val g = gramos[f] ?: 0.0
            r["alcohol_bebidas"] = g / Sustancias.GRAMOS_BEBIDA
            if (g == 0.0) {
                r["alcohol_estimado_max"] = 0.0
                if (r["alcohol"] == null) r["alcohol"] = 0.0
            } else {
                r["alcohol"] = 1.0
            }
        }
        // Máximo estimado: cada día con alcohol mira de su primera dosis a 14 horas después (o hasta la primera dosis del siguiente
        // día con alcohol, para no contar un pico en dos días). La simulación incluye las dosis de las 24 horas previas: lo que
        // seguía en la sangre de la noche anterior cuenta (H-4-5). Muestras cada 5 minutos, como la lista de episodios (H-14-5).
        val alcPorDia = alc.groupBy { Fechas.diaLogicoDeMillis(it.ms) }.toSortedMap()
        val inicios = alcPorDia.values.map { l -> l.minOf { it.ms } }
        alcPorDia.keys.forEachIndexed { k, f ->
            val a = inicios[k]
            val fin = minOf(a + 14 * 3_600_000L, (inicios.getOrNull(k + 1) ?: Long.MAX_VALUE) - 1)
            val previas = alc.filter { it.ms in (a - 24 * 3_600_000L)..fin }
            val instantes = (a..fin step 5 * 60_000L).toList()
            fila(f)["alcohol_estimado_max"] = Sustancias.alcoholEn(previas, instantes).maxOrNull()
        }
        // La última cafeína del día lógico sale de las mismas dosis que los totales (MacroFactor y lo anotado aquí; H-4-12), no solo de
        // MacroFactor por día de calendario. Antes de las 5:00 suma 24, como ultimo_uso_h.
        val ultimaCaf = caf.groupBy { Fechas.diaLogicoDeMillis(it.ms) }.mapValues { (_, l) ->
            val t = Fechas.local(l.maxOf { it.ms })
            val h = t.hour + t.minute / 60.0
            Math.round((if (h < 5) h + 24 else h) * 100) / 100.0
        }
        Repo.nutricion.keys.forEach { f -> if (f !in ultimaCaf) fila(f)["cafeina_ultima_h"] = null }
        ultimaCaf.forEach { (f, h) -> fila(f)["cafeina_ultima_h"] = h }
        val cafDesde = caf.minOfOrNull { it.ms }?.let { Fechas.diaLogicoDeMillis(it) }
        if (cafDesde != null) dias.filter { it >= cafDesde }.forEach { f -> fila(f)["cafeina_total_mg"] = mg[f] ?: 0.0 }
        // Al dormirme: en el día del sueño (el reloj guarda la noche en el día en que despiertas).
        Repo.salud.forEach { (f, x) ->
            val t = x.sueno_inicio?.let { Fechas.millis(it) }?.takeIf { it > 0 } ?: return@forEach
            if (cafDesde != null && f >= cafDesde) {
                val previas = caf.filter { it.ms in (t - 36 * 3_600_000L)..t }
                fila(f)["cafeina_al_dormir_mg"] = Sustancias.cafeinaEn(previas, listOf(t)).first()
            }
            if (alcDesde != null && f >= alcDesde) {
                val previas = alc.filter { it.ms in (t - 18 * 3_600_000L)..t }
                fila(f)["alcohol_al_dormir"] = if (previas.isEmpty()) 0.0 else Sustancias.alcoholEn(previas, listOf(t)).first()
            }
        }
    }

    /**
     * El panel por semanas (de lunes a domingo): cada variable es el promedio de los días que tienen dato (en las 0/1, la
     * proporción de días). Una semana entra si tiene al menos [minDias] días con dato de esa variable.
     */
    fun semanal(panel: Map<String, Map<String, Double?>>, minDias: Int = 3): Map<String, Map<String, Double?>> {
        val suma = sortedMapOf<String, HashMap<String, DoubleArray>>()
        for ((f, r) in panel) {
            val lunes = Fechas.lunesDe(f)
            val m = suma.getOrPut(lunes) { HashMap() }
            for ((k, v) in r) if (v != null && !v.isNaN()) { val a = m.getOrPut(k) { DoubleArray(2) }; a[0] += v; a[1] += 1.0 }
        }
        val out = sortedMapOf<String, Map<String, Double?>>()
        for ((l, m) in suma) out[l] = m.filterValues { it[1] >= minDias }.mapValues { it.value[0] / it.value[1] }
        return out
    }

    fun pares(panel: Map<String, Map<String, Double?>>, x: String, y: String, desfase: Boolean, paso: Long = 1L): List<Par> {
        val out = ArrayList<Par>()
        for ((f, r) in panel) {
            val ry = if (desfase) panel[Fechas.sumar(f, paso)] else r
            val a = r[x] ?: continue
            val bb = ry?.get(y) ?: continue
            out.add(Par(a, bb, f))
        }
        return out
    }

    // ---------- Relaciones entre todas las variables ----------

    /**
     * Variables que salen de la misma fuente o se calculan una de otra. Entre ellas la correlación es casi segura
     * y no dice nada de ti (las calorías suben con la proteína; los eventos, con los minutos de agenda).
     * Una variable puede estar en varias familias.
     */
    val FAMILIAS: List<Set<String>> = listOf(
        setOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "azucares_g", "cafeina_mg", "balance_kcal", "primera_comida_h", "ultima_comida_h", "cafeina_ultima_h"),
        setOf("peso_kg", "peso_tendencia_kg"),
        setOf("eventos", "min_ocupado"),
        setOf("sueno_horas", "sueno_profundo", "sueno_rem", "sueno_inicio_h", "sueno_fin_h"),
        setOf("sueno_recordado", "sueno_valencia", "sueno_lucidez", "sueno_vividez", "sueno_palabras"),
        setOf("ingreso_total", "balance_dinero", "gasto_total"), setOf("prediccion", "error_prediccion", "error_prediccion_signo"), setOf("valencia", "error_prediccion", "error_prediccion_signo"),
        setOf("valencia", "muestreo_valencia"), setOf("activacion", "muestreo_activacion"),
        setOf("avisos_planeados", "avisos_mostrados", "avisos_respondidos", "tasa_respuesta", "muestreo_valencia", "muestreo_activacion", "n_muestreos"),
        setOf("hrv", "fc_reposo", "fc_media", "fc_sueno"), setOf("spo2", "spo2_min"),
        setOf("pasos", "a_pie_min", "cal_activas", "ejercicio_min", "distancia_km"),
        setOf("distancia_km", "vehiculo_min", "coche_min", "sin_wifi_min", "lugares_distintos"),
        setOf("gimnasio", "volumen_kg", "series_gimnasio", "fuerza_ref_kg", "ejercicio_min", "cal_activas"),
        setOf("escalada", "escalada_grado", "ejercicio_min", "cal_activas"),
        setOf("pantalla_min", "desbloqueos", "notificaciones", "primer_uso_h", "ultimo_uso_h"),
        setOf("ultimo_uso_h", "carga_noche_h", "sueno_inicio_h"),
        setOf("primer_uso_h", "sueno_fin_h"),
        setOf("llamadas", "min_hablado", "contactos"),
        setOf("wa_mios", "wa_otros", "wa_chats"),
        setOf("canciones", "musica_min", "audifonos_min", "coche_min"),
        setOf("palabras", "personas", "hitos_n"),
        setOf("yt_vistos", "busquedas", "yt_megusta", "yt_comentarios"),
        setOf("t_max", "t_min", "precip_mm", "nubes_pct", "luz_horas", "sol_horas", "presion_hpa"),
        setOf("aqi_us", "pm25"),
        setOf("gasto_total", "gasto_imprevisto", "gasto_imprevisto_monto", "balance_dinero"),
        setOf("impulso", "recaida", "n_recaida", "n_impulso", "recaida_min", "libido_max", "n_libido", "n_masturbacion", "masturbacion_min"),
        // Esquema 21: las de la intimidad se mueven juntas (una cuenta y sus partes); la masturbación con pornografía crea una recaída.
        setOf("n_masturbacion", "masturbacion_min", "masturbacion_satisfaccion"),
        setOf("n_intimidad", "intimidad_nivel", "intimidad_satisfaccion", "intimidad_comodidad", "n_coito", "intimidad_distancia"),
        setOf("n_episodio", "episodio_intensidad_max", "epi_crisis", "epi_llanto", "epi_pelea", "epi_salud"),
        setOf("presion_local_hpa", "presion_hpa", "cambio_presion_3h"),
        setOf("laptop_min", "laptop_web_min", "laptop_productivo_min", "laptop_ocio_min", "laptop_social_min", "laptop_otro_min"),
        setOf("laptop_productivo_min", "enfoque_min", "enfoque_estudio_min", "enfoque_tesis_min", "enfoque_trabajo_min"),
        setOf("creatina_saturacion_pct", "supl_creatina_dosis", "supl_creatina_tomado", "supl_creatina_nivel"),
        setOf("probioticos_porciones", "probioticos_estimado", "supl_probioticos_porciones", "supl_probioticos_dosis", "supl_probioticos_tomado"),
        setOf("animo_cierre_valencia", "valencia", "animo_valencia"), setOf("animo_cierre_activacion", "activacion", "animo_activacion"),
        setOf("horas_tipo_casa", "en_casa_min", "sin_wifi_min", "fuera_de_casa"),
        setOf("enfoque_min", "enfoque_estudio_min", "enfoque_lectura_min", "enfoque_idiomas_min", "enfoque_tesis_min", "enfoque_trabajo_min", "enfoque_creativo_min"),
        setOf("siesta", "siesta_min"),
        setOf("cama_min", "dormir_tras_tel_min", "ultimo_uso_h", "sueno_inicio_h", "carga_noche_h"),
        setOf("tel_tras_despertar_min", "primer_uso_h", "sueno_fin_h"),
        setOf("aire_libre_min", "luz_alta_min", "a_pie_min", "pasos"),
        setOf("en_casa_min", "sin_wifi_min", "lugares_distintos", "distancia_km", "fuera_de_casa"),
        setOf("acompanado_pct", "con_pareja", "con_amigos", "con_familia", "con_companeros", "con_desconocidos", "valencia_acompanado", "valencia_solo", "muestreo_valencia"),
        setOf("obras_dia", "lectura", "vi_pelicula_serie", "jugue", "obra_terminada", "calificacion_obra"),
        setOf("ciudades_dia", "fuera_de_casa", "extranjero", "ciudad_nueva", "unesco", "distancia_km", "vehiculo_min"),
        setOf("cercanos", "vi_familia", "vi_pareja", "vi_amistades", "vi_convivientes", "personas", "palabras"),
        setOf("sueno_palabras", "sueno_recordado", "sueno_valencia"),
        setOf("carta_escrita", "palabras"),
        setOf("animo_valencia", "valencia", "muestreo_valencia", "momentos_valencia", "valencia_acompanado", "valencia_solo"),
        setOf("animo_activacion", "activacion", "muestreo_activacion"),
        setOf("registros_animo", "n_madrugada", "n_manana", "n_tarde", "n_noche"),
        setOf("alcohol", "alcohol_g", "alcohol_bebidas", "alcohol_nivel_max", "alcohol_horas", "alcohol_estimado_max", "alcohol_al_dormir"),
        setOf("cannabis", "cannabis_nivel_max", "cannabis_horas"),
        setOf("cafeina_mg", "cafeina_total_mg", "cafeina_ultima_h", "cafeina_al_dormir_mg"),
        setOf("cercanos", "vi_familia", "vi_pareja", "vi_amistades", "vi_expareja", "vi_trabajo", "cercania_media", "personas"),
        setOf("personas", "personas_vistas", "personas_pensadas", "personas_mensajes", "cercanos"),
        // Haber revisado las presencias solo existe en días con personas: se mueve con ellas por construcción.
        setOf("presencias_revisado", "personas", "personas_vistas", "personas_pensadas", "personas_mensajes", "cercanos", "palabras"),
        setOf("personas_pensadas", "penso_en_pareja", "penso_en_expareja", "penso_en_familia", "penso_en_amistades"),
    )

    fun obvia(a: Variable, b: Variable): Boolean {
        // Una campaña y lo que ese reto busca mover (la elegiste para eso).
        if (Campanas.obvia(a.id, b.id) || Campanas.obvia(b.id, a.id)) return true
        if ((a.grupo == b.grupo && (a.grupo == "Lectura del día" || a.grupo == "Momentos" || a.grupo == "Sentimientos" || a.grupo == "Sueños")) || FAMILIAS.any { a.id in it && b.id in it }) return true
        // Las horas por tipo de lugar se reparten el mismo día entre sí y con cada lugar.
        val lugA = a.id.startsWith("horas_tipo_") || a.id.startsWith("lug_"); val lugB = b.id.startsWith("horas_tipo_") || b.id.startsWith("lug_")
        if (lugA && lugB) return true
        // Las variables del mismo suplemento (dosis, si se tomó, nivel) se mueven juntas por construcción.
        fun supl(id: String) = if (id.startsWith("supl_")) id.removePrefix("supl_").substringBeforeLast('_') else null
        if (supl(a.id) != null && supl(a.id) == supl(b.id)) return true
        // Lo que sentías con el número de registros del día, y cada categoría del sueño con haber recordado un sueño.
        if ((a.grupo == "Sentimientos" && b.id == "registros_animo") || (b.grupo == "Sentimientos" && a.id == "registros_animo")) return true
        if ((a.grupo == "Sueños" && b.id.startsWith("sueno_")) || (b.grupo == "Sueños" && a.id.startsWith("sueno_"))) return true
        // Las variables de cada persona, app o categoría con su total (minutos en una app y minutos de pantalla).
        val pa = Dinamicas.prefijo(a.id); val pb = Dinamicas.prefijo(b.id)
        if (pa != null && Dinamicas.AGREGADOS[pa]?.contains(b.id) == true) return true
        if (pb != null && Dinamicas.AGREGADOS[pb]?.contains(a.id) == true) return true
        if (pa != null && pb != null && Dinamicas.ENTRE_SI.any { pa in it && pb in it }) return true
        // La misma persona: «aparece @ana» y «pensé en @ana sin verla».
        if (setOf(pa, pb) == setOf("per_", "pen_") && a.id.substringAfter('_') == b.id.substringAfter('_')) return true
        // Dos puntajes del mismo cuestionario salen de las mismas respuestas.
        if (pa == "esc_" && pb == "esc_" && a.id.split('_').getOrNull(1) == b.id.split('_').getOrNull(1)) return true
        return false
    }

    /**
     * Una familia de pares obvios para vm_familias.csv (P-31). Con lado 0, todas sus variables son obvias entre sí; con lados 1 y 2,
     * cada variable del lado 1 lo es con cada una del lado 2, pero no las del mismo lado entre ellas (una campaña y lo que busca mover).
     */
    class Familia(val nombre: String, val lados: Map<String, Int>)

    /** Si [a] y [b] comparten una familia en la que cuentan como obvias (la misma regla que lee vm_obvia en R). */
    fun obviaEnFamilias(familias: List<Familia>, a: String, b: String): Boolean = familias.any { f ->
        val la = f.lados[a]; val lb = f.lados[b]
        la != null && lb != null && (la == 0 && lb == 0 || la != 0 && lb != 0 && la != lb)
    }

    /**
     * Las reglas de [obvia] escritas como familias, para que R esconda las mismas parejas que la app (P-31). Para cualquier par de
     * [vars], [obviaEnFamilias] da lo mismo que [obvia]. [alias] agrega, junto a cada variable de la app, el nombre con que va en
     * vm_panel.csv (km junto a distancia_km), en las mismas familias, y una familia más con los dos.
     */
    fun familias(vars: List<Variable>, alias: Map<String, List<String>> = emptyMap()): List<Familia> {
        val ids = vars.map { it.id }.distinct()
        val hay = ids.toSet()
        val out = ArrayList<Familia>()
        fun todas(nombre: String, l: Collection<String>) { val x = l.filter { it in hay }.distinct(); if (x.size >= 2) out += Familia(nombre, x.associateWith { 0 }) }
        fun dos(nombre: String, a: Collection<String>, b: Collection<String>) {
            val x = a.filter { it in hay }.distinct(); val y = b.filter { it in hay && it !in x }.distinct()
            if (x.isNotEmpty() && y.isNotEmpty()) out += Familia(nombre, x.associateWith { 1 } + y.associateWith { 2 })
        }
        // Campañas: el conteo con cada una, y cada una con lo que busca mover (o con su hábito).
        val campanas = ids.filter { it.startsWith("campana_") }
        dos("campanas_n", listOf("campanas_n"), campanas)
        campanas.forEach { c -> val r = c.removePrefix("campana_"); dos(c, listOf(c), if (r.startsWith("hab_")) listOf(r) else Campanas.MUEVE[r].orEmpty()) }
        // Grupos que se reparten el mismo registro.
        listOf("Lectura del día", "Momentos", "Sentimientos", "Sueños").forEach { g -> todas("grupo:$g", vars.filter { it.grupo == g }.map { it.id }) }
        FAMILIAS.forEachIndexed { i, f -> todas("familia_${i + 1}", f) }
        todas("lugares", ids.filter { it.startsWith("horas_tipo_") || it.startsWith("lug_") })
        ids.filter { it.startsWith("supl_") }.groupBy { it.removePrefix("supl_").substringBeforeLast('_') }.forEach { (s, l) -> todas("supl:$s", l) }
        dos("sentimientos", listOf("registros_animo"), vars.filter { it.grupo == "Sentimientos" }.map { it.id })
        val suenos = vars.filter { it.grupo == "Sueños" }.map { it.id }
        dos("suenos", suenos, ids.filter { it.startsWith("sueno_") })
        // Cada persona, app, categoría… con sus totales; y las familias que, entre ellas, también son obvias.
        val porPrefijo = ids.groupBy { Dinamicas.prefijo(it) }
        Dinamicas.AGREGADOS.forEach { (p, agregados) -> dos("total:$p", porPrefijo[p].orEmpty(), agregados) }
        Dinamicas.ENTRE_SI.forEach { e -> todas("entre_si:" + e.joinToString("+"), e.flatMap { porPrefijo[it].orEmpty() }) }
        porPrefijo["per_"].orEmpty().forEach { a -> val b = "pen_" + a.removePrefix("per_"); todas("persona:" + a.removePrefix("per_"), listOf(a, b)) }
        porPrefijo["esc_"].orEmpty().groupBy { it.split('_').getOrNull(1) }.forEach { (q, l) -> todas("escala:$q", l) }
        if (alias.isEmpty()) return out
        // Los nombres de vm_panel.csv van con su variable de la app, y cada pareja es la misma medida.
        val conAlias = out.map { f -> Familia(f.nombre, f.lados + f.lados.flatMap { (v, lado) -> alias[v].orEmpty().map { it to lado } }) }
        return conAlias + alias.filterKeys { it in hay }.map { (v, l) -> Familia("igual:$v", (listOf(v) + l).associateWith { 0 }) }
    }

    /** Días hacia cada lado con los que se compara cada día al quitar la tendencia. */
    const val VENTANA = 10

    /** Una variable alineada a las fechas del panel (NaN = sin dato), con su versión sin tendencia y su autocorrelación. */
    private class Serie(val crudo: DoubleArray, val resto: DoubleArray, val r1Crudo: Double, val r1Resto: Double, val lenta: Boolean, val binaria: Boolean)

    /**
     * La autocorrelación que se supone cuando no se puede estimar (menos de 10 pares de días seguidos): 0.3, un valor prudente,
     * en vez de 0, que daría tantos días efectivos como días y una relación más segura de lo que es (H-5-13).
     */
    const val AR1_SIN_DATO = 0.3

    private fun autocorr(x: DoubleArray, dia: LongArray, paso: Long = 1L): Double {
        val a = ArrayList<Double>(); val b = ArrayList<Double>()
        for (i in 0 until x.size - 1) if (!x[i].isNaN() && !x[i + 1].isNaN() && dia[i + 1] - dia[i] == paso) { a.add(x[i]); b.add(x[i + 1]) }
        if (a.size < 10) return AR1_SIN_DATO
        return pearson(a, b) ?: AR1_SIN_DATO
    }

    private fun varianza(v: DoubleArray): Double {
        val l = v.filter { !it.isNaN() }; if (l.size < 2) return 0.0
        val m = l.average(); return l.sumOf { (it - m) * (it - m) } / (l.size - 1)
    }

    private fun series(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, paso: Long = 1L, ventana: Int = VENTANA): Map<String, Serie> {
        val fechas = panel.keys.sorted()
        val dia = LongArray(fechas.size) { Fechas.parse(fechas[it]).toEpochDay() }
        val out = HashMap<String, Serie>()
        for (v in vars) {
            val x = DoubleArray(fechas.size) { panel[fechas[it]]?.get(v.id) ?: Double.NaN }
            val idx = x.indices.filter { !x[it].isNaN() }
            if (idx.size < 3) continue
            // Cada día menos el promedio de sus vecinos (±VENTANA días de calendario): queda lo que ese día tuvo de distinto.
            val resto = DoubleArray(x.size) { Double.NaN }
            var lo = 0; var hi = 0; var suma = 0.0; var cnt = 0
            for (i in idx) {
                while (hi < idx.size && dia[idx[hi]] <= dia[i] + ventana) { suma += x[idx[hi]]; cnt++; hi++ }
                while (dia[idx[lo]] < dia[i] - ventana) { suma -= x[idx[lo]]; cnt--; lo++ }
                if (cnt >= 3) resto[i] = x[i] - suma / cnt
            }
            val vc = varianza(x); val vr = varianza(resto)
            val binaria = idx.all { x[it] == 0.0 || x[it] == 1.0 }
            // «Lenta»: casi toda su variación es de semanas a meses (horas de luz, peso suavizado). Las constantes no cuentan.
            val lenta = vc > 0.0 && resto.count { !it.isNaN() } >= (if (paso > 1) 8 else 14) && vr / vc < 0.12
            out[v.id] = Serie(x, resto, autocorr(x, dia, paso), autocorr(resto, dia, paso), lenta, binaria)
        }
        return out
    }

    /**
     * Revisa todos los pares de variables. Con [sinTendencia], cada día se compara con sus semanas cercanas, así que
     * dos cosas que solo cambiaron con el tiempo (días más cortos mientras bajabas de peso) dejan de parecer relacionadas.
     * El intervalo usa días efectivos: si hoy se parece a ayer, dos días seguidos no cuentan como dos pruebas.
     * [q] es el valor corregido por comparaciones múltiples (Benjamini-Hochberg) entre los pares no obvios.
     */
    fun relaciones(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, sinTendencia: Boolean, minN: Int = 14,
                   paso: Long = 1L, ventana: Int = VENTANA): Relaciones {
        val s = series(panel, vars, paso, ventana)
        val usables = vars.filter { it.id in s }
        val lentas = if (sinTendencia) usables.filter { s[it.id]!!.lenta } else emptyList()
        val res = ArrayList<Correlacion>()
        val largo = s.values.firstOrNull()?.crudo?.size ?: 0
        val xs = DoubleArray(largo); val ys = DoubleArray(largo); val cx = DoubleArray(largo); val cy = DoubleArray(largo)
        fun r(a: DoubleArray, b: DoubleArray, n: Int): Double? {
            var ma = 0.0; var mb = 0.0
            for (i in 0 until n) { ma += a[i]; mb += b[i] }
            ma /= n; mb /= n
            var sab = 0.0; var saa = 0.0; var sbb = 0.0
            for (i in 0 until n) { val da = a[i] - ma; val db = b[i] - mb; sab += da * db; saa += da * da; sbb += db * db }
            return if (saa == 0.0 || sbb == 0.0) null else sab / sqrt(saa * sbb)
        }
        for (i in usables.indices) for (j in i + 1 until usables.size) {
            val a = usables[i]; val b = usables[j]
            val sa = s[a.id]!!; val sb = s[b.id]!!
            if (sinTendencia && (sa.lenta || sb.lenta)) continue
            val va = if (sinTendencia) sa.resto else sa.crudo; val vb = if (sinTendencia) sb.resto else sb.crudo
            var n = 0
            for (k in va.indices) { val p = va[k]; val q = vb[k]; if (!p.isNaN() && !q.isNaN()) { xs[n] = p; ys[n] = q; cx[n] = sa.crudo[k]; cy[n] = sb.crudo[k]; n++ } }
            if (n < minN) continue
            // Una variable 0/1 necesita algunos días de cada tipo para decir algo.
            if (sa.binaria) { var unos = 0; for (k in 0 until n) if (cx[k] == 1.0) unos++; if (minOf(unos, n - unos) < 4) continue }
            if (sb.binaria) { var unos = 0; for (k in 0 until n) if (cy[k] == 1.0) unos++; if (minOf(unos, n - unos) < 4) continue }
            val rr = r(xs, ys, n) ?: continue
            val bruta = r(cx, cy, n) ?: rr
            val ra = (if (sinTendencia) sa.r1Resto else sa.r1Crudo).coerceIn(-0.5, 0.97)
            val rb = (if (sinTendencia) sb.r1Resto else sb.r1Crudo).coerceIn(-0.5, 0.97)
            val nEf = (n * (1 - ra * rb) / (1 + ra * rb)).coerceIn(minOf(4.0, n.toDouble()), n.toDouble())
            if (nEf <= 3.0) continue
            val z = atanh(rr.coerceIn(-0.9999, 0.9999)); val se = 1 / sqrt(nEf - 3)
            val p = Analisis.pBilateral(z / se)
            res.add(Correlacion(a, b, rr, n, tanh(z - 1.96 * se) to tanh(z + 1.96 * se), bruta, nEf, p, obvia = obvia(a, b)))
        }
        val limpias = res.filter { !it.obvia }
        Analisis.bh(limpias.map { it.p }).forEachIndexed { i, q -> limpias[i].q = q }
        Analisis.bh(res.map { it.p }).forEachIndexed { i, q -> res[i].qConObvias = q }
        // Primero lo que se sostiene aun en el peor caso del intervalo; luego, por fuerza.
        fun seguro(c: Correlacion) = c.ic?.let { if (it.first > 0) it.first else if (it.second < 0) -it.second else 0.0 } ?: 0.0
        val orden = res.sortedWith(compareByDescending<Correlacion> { seguro(it) }.thenByDescending { abs(it.r) })
        return Relaciones(orden, res.count { it.obvia }, lentas, sinTendencia)
    }
}

object Movilidad {
    /** Suma de distancias entre muestras consecutivas del mismo día, ignorando saltos imposibles. */
    fun kmDe(fecha: String): Double? {
        val a = Fechas.inicioDiaLogico(fecha)
        return kmPorDia(Repo.db().ubicaciones(a, a + 86_400_000L))[fecha]
    }

    /** Lo mismo que [kmPorDia] entre dos días lógicos, recorriendo la base sin cargar todos los puntos a memoria. */
    fun kmEntre(desde: String, hasta: String): Map<String, Double> {
        val out = HashMap<String, Double>()
        var pMs = -1L; var pLat = 0.0; var pLon = 0.0
        val ini = if (desde <= "1971-01-01") 0L else Fechas.inicioDia(desde)
        val fin = if (hasta >= "9999-01-01") Long.MAX_VALUE else Fechas.finDiaLogico(hasta)
        Repo.db().recorrerUbicaciones(ini, fin) { ms, lat, lon, _ ->
            if (pMs >= 0) {
                val dt = (ms - pMs) / 1000.0
                val d = io.github.ruddy2003.vestigium.data.Geo.distanciaM(pLat, pLon, lat, lon)
                if (dt in 1.0..7200.0 && d / dt < 70 && d > 30) {
                    val f = Fechas.diaLogicoDeMillis(ms)
                    if (f in desde..hasta) out[f] = (out[f] ?: 0.0) + d / 1000
                }
            }
            pMs = ms; pLat = lat; pLon = lon
        }
        return out
    }

    fun kmPorDia(puntos: List<io.github.ruddy2003.vestigium.data.Db.Punto> = Repo.db().ubicaciones()): Map<String, Double> {
        val out = HashMap<String, Double>()
        var prev: io.github.ruddy2003.vestigium.data.Db.Punto? = null
        // Un día con ubicaciones y sin movimiento vale 0 km, no «sin dato» (P-15).
        var desde = Long.MAX_VALUE; var hasta = Long.MIN_VALUE
        for (p in puntos) {
            if (p.ms < desde || p.ms >= hasta) {
                val f = Fechas.diaLogicoDeMillis(p.ms); out.putIfAbsent(f, 0.0)
                desde = Fechas.inicioDiaLogico(f); hasta = Fechas.finDiaLogico(f)
            }
            val pr = prev
            if (pr != null) {
                val dt = (p.ms - pr.ms) / 1000.0
                val d = io.github.ruddy2003.vestigium.data.Geo.distanciaM(pr.lat, pr.lon, p.lat, p.lon)
                if (dt in 1.0..7200.0 && d / dt < 70 && d > 30) {
                    val f = Fechas.diaLogicoDeMillis(p.ms)
                    out[f] = (out[f] ?: 0.0) + d / 1000
                }
            }
            prev = p
        }
        return out
    }
}
