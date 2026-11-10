package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.porNumero

import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Grados
import io.github.ruddy2003.vestigium.data.Macrofactor
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import java.text.Normalizer
import java.time.format.TextStyle
import java.util.Locale
import java.util.TreeMap

/**
 * Totales acumulados de un periodo (kilómetros, horas de sueño, calorías, mensajes…) y un índice buscable:
 * cada alimento, persona, artista, lugar o ejercicio con cuánto, cuántas veces y desde cuándo.
 * Cada cifra dice desde qué fecha hay datos, porque cada fuente empezó en un momento distinto.
 */
object Totales {
    data class Cifra(val nombre: String, val valor: String, val desde: String?, val nota: String? = null)
    data class Grupo(val titulo: String, val latin: String, val cifras: List<Cifra>)

    class Entrada(val nombre: String, val unidad: String) {
        var total = 0.0; var veces = 0
        val dias = HashSet<String>()
        var primera = ""; var ultima = ""
        val porMes = TreeMap<String, Double>()
        /** Segunda medida: kcal de un alimento, minutos de un artista, volumen de un ejercicio… */
        var extra = 0.0
        /** Máximo: mejor 1RM estimada de un ejercicio. */
        var mejor = 0.0
        fun sumar(fecha: String, cantidad: Double, n: Int = 1) {
            total += cantidad; veces += n; dias.add(fecha)
            if (primera.isEmpty() || fecha < primera) primera = fecha
            if (fecha > ultima) ultima = fecha
            porMes.merge(fecha.take(7), cantidad, Double::plus)
        }
    }

    /** [linea] describe una entrada en una línea; [buscaSinonimos] activa los sinónimos en español para nombres en inglés. */
    class Indice(val id: String, val titulo: String, val entradas: List<Entrada>, val buscaSinonimos: Boolean = false, val linea: (Entrada) -> String)

    data class Resultado(val desde: String, val hasta: String, val grupos: List<Grupo>, val indices: List<Indice>)

    // ---------- Formato ----------
    fun n(v: Double, dec: Int = 0): String = if (dec == 0) "%,.0f".format(v) else "%,.${dec}f".format(v)
    fun masa(g: Double) = if (g >= 1000) "${n(g / 1000, 1)} kg" else "${n(g)} g"
    fun liquido(ml: Double) = if (ml >= 1000) "${n(ml / 1000, 1)} L" else "${n(ml)} ml"
    fun tiempo(min: Double) = when {
        min < 90 -> "${n(min)} min"
        min < 60 * 24 * 3 -> "${n(min / 60, if (min < 600) 1 else 0)} h"
        else -> "${n(min / 60)} h (${porNumero(min / 60 / 24, "1 día", "${n(min / 60 / 24, 1)} días")})"
    }
    fun dinero(v: Double) = "$" + n(v)
    fun formato(v: Double, unidad: String): String = when (unidad) {
        "g" -> masa(v); "ml" -> liquido(v); "min" -> tiempo(v); "$" -> dinero(v)
        else -> "${n(v, if (v < 10 && v % 1.0 != 0.0) 1 else 0)} $unidad"
    }
    fun cantidad(e: Entrada): String = formato(e.total, e.unidad)
    private val ES = Locale.forLanguageTag("es")
    fun fecha(s: String): String = runCatching {
        val d = Fechas.parse(s); "${d.dayOfMonth} ${d.month.getDisplayName(TextStyle.SHORT, ES).replace(".", "")} ${d.year}"
    }.getOrDefault(s)

    fun normal(s: String): String = Normalizer.normalize(s.lowercase().trim(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

    /** MacroFactor nombra los alimentos en inglés: «plátano» también busca «banana». */
    private val SINONIMOS = mapOf(
        "platano" to listOf("banana", "plantain"), "manzana" to listOf("apple"), "huevo" to listOf("egg"), "leche" to listOf("milk", "lala"),
        "avena" to listOf("oat"), "arroz" to listOf("rice"), "pollo" to listOf("chicken"), "pan" to listOf("bread", "bun", "bagel"),
        "cafe" to listOf("coffee"), "azucar" to listOf("sugar"), "nuez" to listOf("walnut", "pecan"), "frijol" to listOf("bean"),
        "atun" to listOf("tuna"), "queso" to listOf("cheese"), "yogur" to listOf("yogurt", "yoghurt"), "cerveza" to listOf("beer"),
        "aguacate" to listOf("avocado"), "mani" to listOf("peanut"), "cacahuate" to listOf("peanut"), "fresa" to listOf("strawberr"),
        "naranja" to listOf("orange"), "uva" to listOf("grape"), "res" to listOf("beef"), "carne" to listOf("beef", "meat", "steak"),
        "cerdo" to listOf("pork"), "pescado" to listOf("fish"), "miel" to listOf("honey"), "te" to listOf("tea"), "galleta" to listOf("cookie", "cracker"),
        "papa" to listOf("potato"), "tomate" to listOf("tomato"), "jitomate" to listOf("tomato"), "zanahoria" to listOf("carrot"),
        "brocoli" to listOf("broccoli"), "espinaca" to listOf("spinach"), "lenteja" to listOf("lentil"), "garbanzo" to listOf("chickpea"),
        "almendra" to listOf("almond"), "salmon" to listOf("salmon"), "pina" to listOf("pineapple"), "sandia" to listOf("watermelon"),
        "limon" to listOf("lemon", "lime"), "pepino" to listOf("cucumber"), "cebolla" to listOf("onion"), "ajo" to listOf("garlic"),
        "vino" to listOf("wine"), "mantequilla" to listOf("butter"), "aceite" to listOf("oil"), "chia" to listOf("chia"),
        "semilla" to listOf("seed"), "pasta" to listOf("pasta", "spaghetti"), "jamon" to listOf("ham"), "agua" to listOf("water"),
        "refresco" to listOf("soda", "cola"), "jugo" to listOf("juice"), "helado" to listOf("ice cream"), "pera" to listOf("pear"),
        "durazno" to listOf("peach"), "mango" to listOf("mango"), "papaya" to listOf("papaya"), "maiz" to listOf("corn"),
        "proteina" to listOf("protein", "whey"), "crema" to listOf("cream"), "tocino" to listOf("bacon"), "camaron" to listOf("shrimp"),
    )

    fun coincide(nombre: String, consulta: String, sinonimos: Boolean): Boolean {
        val q = normal(consulta).removePrefix("@").removePrefix("#")
        if (q.isEmpty()) return false
        val n = normal(nombre)
        if (q in n) return true
        if (!sinonimos) return false
        return SINONIMOS.entries.any { (es, en) -> (q == es || (q.length >= 4 && es.startsWith(q))) && en.any { it in n } }
    }

    // ---------- Sumas ----------
    private class S {
        var v = 0.0; var n = 0; var desde: String? = null
        var max = Double.NEGATIVE_INFINITY; var fMax: String? = null
        var min = Double.POSITIVE_INFINITY; var fMin: String? = null
        fun add(f: String, x: Number?) {
            if (x == null) return
            val d = x.toDouble(); if (d.isNaN()) return
            v += d; n++
            if (desde == null || f < desde!!) desde = f
            if (d > max) { max = d; fMax = f }
            if (d < min) { min = d; fMin = f }
        }
    }

    private fun <T> suma(l: Iterable<T>, f: (T) -> String, x: (T) -> Number?): S { val s = S(); l.forEach { s.add(f(it), x(it)) }; return s }

    private fun MutableList<Cifra>.poner(nombre: String, s: S, valor: (Double) -> String, nota: ((S) -> String?)? = null) {
        if (s.n > 0) add(Cifra(nombre, valor(s.v), s.desde, nota?.invoke(s)))
    }

    private fun conteo(nombre: String, n: Int, desde: String?, nota: String? = null) = if (n > 0) Cifra(nombre, n(n.toDouble()), desde, nota) else null

    fun calcular(desde: String, hasta: String): Resultado {
        fun en(f: String?) = f != null && f.length >= 10 && f.take(10) >= desde && f.take(10) <= hasta
        val eq = Repo.equivalencias()
        val dias = Repo.dias.values.filter { en(it.fecha) }
        val momentos = Repo.momentos.filter { en(it.fecha) && !it.privado }
        val salud = Repo.salud.values.filter { en(it.fecha) }
        val pantalla = Repo.pantalla.values.filter { en(it.fecha) }
        val actividad = Repo.actividad.values.filter { en(it.fecha) }
        val nutricion = Repo.nutricion.values.filter { en(it.fecha) }
        val entrenos = Repo.entrenos.values.filter { en(it.fecha) && it.series > 0 }
        // Por el día del diario (de 5:00 a 5:00), como en el panel (P-29).
        val gastos = Repo.gastos.filter { en(it.dia) }
        val ingresos = Repo.ingresos.filter { en(it.dia) }
        val visitas = Repo.visitas.filter { en(Fechas.diaLogicoDeMillis(Fechas.millis(it.entrada))) }
        val comidas = runCatching { Repo.comidas(desde, hasta) }.getOrDefault(emptyList())
        val series = runCatching { Repo.series(desde, hasta) }.getOrDefault(emptyList())
        val escuchas = runCatching { Repo.escuchas(desde, hasta) }.getOrDefault(emptyList()).filter { (it.ms ?: 30_000) >= 30_000 }
        val huellas = runCatching { Repo.huellasDe(desde, hasta) }.getOrDefault(emptyList())
        val grupos = ArrayList<Grupo>()
        /** En qué días del periodo aparece cada persona: la cuenta única ([io.github.ruddy2003.vestigium.data.Personas.diasCon]). */
        val personasDias = io.github.ruddy2003.vestigium.data.Personas.diasCon(desde, hasta)

        // ----- Movimiento -----
        run {
            val c = ArrayList<Cifra>()
            val km = S(); runCatching { Movilidad.kmEntre(desde, hasta) }.getOrDefault(emptyMap()).forEach { (f, v) -> km.add(f, v) }
            c.poner("Kilómetros recorridos (ubicación)", km, { "${n(it, 1)} km" }) { s -> "récord: ${n(s.max, 1)} km el ${fecha(s.fMax!!)}" }
            c.poner("Kilómetros según el reloj", suma(salud, { it.fecha }, { it.distancia_km }), { "${n(it, 1)} km" })
            // Como en los reportes (Periodo): un día con cero pasos es un día sin medir y no entra en la media «al día».
            c.poner("Pasos", suma(salud, { it.fecha }, { it.pasos?.takeIf { p -> p > 0 } }), { n(it) }) { s -> "${n(s.v / s.n)} al día · récord ${n(s.max)} el ${fecha(s.fMax!!)}" }
            listOf("a_pie" to "A pie", "corriendo" to "Corriendo", "bici" to "En bici", "vehiculo" to "En vehículo").forEach { (k, t) ->
                c.poner(t, suma(actividad, { it.fecha }, { it.minutos[k] }), ::tiempo)
            }
            c.poner("Pisos subidos", suma(salud, { it.fecha }, { it.pisos }), { n(it) })
            if (visitas.isNotEmpty()) c.add(Cifra("Visitas a tus lugares", "${visitas.size} a ${visitas.map { it.lugar }.distinct().size} lugares",
                visitas.minOf { it.entrada }.take(10)))
            if (c.isNotEmpty()) grupos.add(Grupo("Movimiento", "MOTVS", c))
        }
        // ----- Cuerpo -----
        run {
            val c = ArrayList<Cifra>()
            // Como en los reportes (Periodo): solo las noches de más de 120 min (menos es una lectura rota o una siesta).
            c.poner("Horas dormidas", suma(salud, { it.fecha }, { it.sueno_min?.takeIf { m -> m > 120 } }), ::tiempo) { s -> "${n(s.v / s.n / 60, 1)} h por noche en ${porNumero(s.n, "1 noche", "${s.n} noches")}" }
            val ses = Repo.sesiones.filter { en(it.fecha) }
            if (ses.isNotEmpty()) c.add(Cifra("Sesiones de ejercicio (reloj)", "${ses.size} · ${tiempo(ses.sumOf { it.min }.toDouble())}", ses.minOf { it.fecha }))
            if (entrenos.isNotEmpty()) {
                val d0 = entrenos.minOf { it.fecha }
                c.add(Cifra("Entrenamientos de gimnasio", n(entrenos.size.toDouble()), d0, "${n(entrenos.sumOf { it.series }.toDouble())} series · ${n(entrenos.sumOf { it.reps }.toDouble())} repeticiones"))
                c.add(Cifra("Peso total levantado", "${n(entrenos.sumOf { it.volumen_kg } / 1000, 1)} toneladas", d0))
            }
            val asc = Repo.ascensos.filter { en(it.fecha) }
            if (asc.isNotEmpty()) c.add(Cifra("Sesiones de escalada", n(asc.size.toDouble()), asc.minOf { it.fecha },
                "${asc.sumOf { a -> a.bloques.count { Grados.encadenado(it.estilo) } }} bloques encadenados"))
            c.poner("Calorías activas", suma(salud, { it.fecha }, { it.cal_activas }), { "${n(it)} kcal" })
            c.poner("Agua (reloj)", suma(salud, { it.fecha }, { it.agua_ml }), ::liquido)
            if (c.isNotEmpty()) grupos.add(Grupo("Cuerpo", "CORPVS", c))
        }
        // ----- Comida -----
        run {
            val c = ArrayList<Cifra>()
            c.poner("Calorías consumidas", suma(nutricion, { it.fecha }, { it.valores["kcal"] }), { "${n(it)} kcal" }) { s -> "${n(s.v / s.n)} al día en ${porNumero(s.n, "1 día", "${s.n} días")}" }
            listOf("proteina_g" to "Proteína", "carbohidratos_g" to "Carbohidratos", "grasa_g" to "Grasa", "fibra_g" to "Fibra", "azucares_g" to "Azúcares", "alcohol_g" to "Alcohol").forEach { (k, t) ->
                c.poner(t, suma(nutricion, { it.fecha }, { it.valores[k] }), ::masa)
            }
            // En mg hasta 10 g: en gramos sin decimales, 400 mg salían como «0 g».
            c.poner("Cafeína", suma(nutricion, { it.fecha }, { it.valores["cafeina_mg"] }), { mg -> if (mg < 10_000) "${n(mg)} mg" else masa(mg / 1000) })
            if (comidas.isNotEmpty()) c.add(Cifra("Alimentos registrados", n(comidas.size.toDouble()), comidas.minOf { it.fecha },
                "${comidas.map { normal(it.alimento) }.distinct().size} distintos"))
            if (c.isNotEmpty()) grupos.add(Grupo("Comida", "CIBVS", c))
        }
        // ----- Diario y mente -----
        run {
            val c = ArrayList<Cifra>()
            // «Día con diario» = con nota o valencia, como en los reportes (Periodo.conDiario); no cualquier día guardado.
            val conDiario = dias.filter { it.nota.isNotBlank() || it.valencia != null }
            val d0 = conDiario.minOfOrNull { it.fecha }
            conteo("Días con diario", conDiario.size, d0)?.let(c::add)
            c.poner("Palabras escritas", suma(dias, { it.fecha }, { Texto.palabras(it.nota) }), { n(it) })
            val mo = momentos.filter { it.tipo != "muestreo" }
            conteo("Momentos registrados", mo.size, mo.minOfOrNull { it.fecha })?.let(c::add)
            val mu = momentos.filter { it.tipo == "muestreo" }
            conteo("Avisos respondidos", mu.size, mu.minOfOrNull { it.fecha })?.let(c::add)
            // Las fechas señaladas (con los hitos de antes) y los episodios (esquema 21; los momentos privados ya no están en [momentos]).
            conteo("Fechas señaladas", dias.sumOf { io.github.ruddy2003.vestigium.data.Senaladas.de(it).size },
                dias.filter { io.github.ruddy2003.vestigium.data.Senaladas.de(it).isNotEmpty() }.minOfOrNull { it.fecha })?.let(c::add)
            val ep = momentos.filter { it.tipo == "episodio" }
            conteo("Episodios", ep.size, ep.minOfOrNull { it.fecha })?.let(c::add)
            conteo("Sueños recordados", dias.count { it.sueno_recordado }, dias.filter { it.sueno_recordado }.minOfOrNull { it.fecha })?.let(c::add)
            val re = Repo.respuestas.filter { en(it.fecha) }
            conteo("Cuestionarios respondidos", re.size, re.minOfOrNull { it.fecha })?.let(c::add)
            val rx = Repo.reacciones.filter { en(it.fecha) }
            conteo("Pruebas de reacción", rx.size, rx.minOfOrNull { it.fecha })?.let(c::add)
            if (c.isNotEmpty()) grupos.add(Grupo("Diario y mente", "ANIMVS", c))
        }
        // ----- Dinero -----
        if (gastos.isNotEmpty() || ingresos.isNotEmpty()) {
            val c = ArrayList<Cifra>()
            if (gastos.isNotEmpty()) {
                // Por día lógico del gasto (Gasto.dia), como el filtro de arriba y los reportes.
                val g0 = gastos.minOf { it.dia }
                val porDia = gastos.groupBy { it.dia }.mapValues { e -> e.value.sumOf { it.monto } }
                val top = porDia.maxByOrNull { it.value }!!
                c.add(Cifra("Gasto total", dinero(gastos.sumOf { it.monto }), g0, "${porNumero(gastos.size, "1 gasto", "${gastos.size} gastos")} · día más caro: ${dinero(top.value)} el ${fecha(top.key)}"))
                val imp = gastos.filter { it.imprevisto }
                if (imp.isNotEmpty()) c.add(Cifra("Imprevistos", dinero(imp.sumOf { it.monto }), imp.minOf { it.dia }, "${porNumero(imp.size, "1 gasto", "${imp.size} gastos")}"))
            }
            if (ingresos.isNotEmpty()) {
                val total = ingresos.sumOf { it.monto }
                val principal = ingresos.groupBy { it.categoria }.mapValues { e -> e.value.sumOf { it.monto } }.maxBy { it.value }.key
                c.add(Cifra("Ingresos", dinero(total), ingresos.minOf { it.fecha }, "${ingresos.size} ingresos · lo que más aporta: ${principal.lowercase()}"))
                val saldo = total - gastos.sumOf { it.monto }
                c.add(Cifra("Saldo", (if (saldo < 0) "−" else "") + dinero(kotlin.math.abs(saldo)), ingresos.minOf { it.fecha }, "ingresos menos gastos"))
            }
            grupos.add(Grupo("Dinero", "PECVNIA", c))
        }
        // ----- Teléfono -----
        run {
            val c = ArrayList<Cifra>()
            c.poner("Pantalla", suma(pantalla, { it.fecha }, { it.total_min }), ::tiempo) { s -> "${tiempo(s.v / s.n)} al día" }
            c.poner("Desbloqueos", suma(pantalla, { it.fecha }, { it.desbloqueos }), { n(it) })
            c.poner("Notificaciones", suma(Repo.notifs.values.filter { en(it.fecha) }, { it.fecha }, { it.total }), { n(it) })
            val fo = Repo.fotos.values.filter { en(it.fecha) }
            c.poner("Fotos", suma(fo, { it.fecha }, { it.fotos }), { n(it) }) { s -> "récord: ${n(s.max)} el ${fecha(s.fMax!!)}" }
            c.poner("Videos grabados", suma(fo, { it.fecha }, { it.videos }), { n(it) })
            c.poner("Capturas de pantalla", suma(fo, { it.fecha }, { it.capturas }), { n(it) })
            if (escuchas.isNotEmpty()) {
                val min = escuchas.sumOf { ((it.ms?.toDouble() ?: it.duracion_s?.times(1000.0) ?: 0.0)) / 60000 }
                c.add(Cifra("Canciones escuchadas", n(escuchas.size.toDouble()), escuchas.minOf { it.fecha },
                    "${tiempo(min)} de música · ${escuchas.map { normal(it.artista) }.distinct().size} artistas"))
            }
            c.poner("Con audífonos", suma(Repo.conexiones.values.filter { en(it.fecha) }, { it.fecha }, { it.audio_min["audifonos"] }), ::tiempo)
            val hd = Repo.huellas.values.filter { en(it.fecha) }
            c.poner("Videos de YouTube", suma(hd, { it.fecha }, { it.yt_vistos }), { n(it) })
            c.poner("Búsquedas en Google", suma(hd, { it.fecha }, { it.busquedas + it.yt_busquedas }), { n(it) })
            if (c.isNotEmpty()) grupos.add(Grupo("Teléfono", "TELEPHONVM", c))
        }
        // ----- Social -----
        run {
            val c = ArrayList<Cifra>()
            val co = Repo.comunicacion.values.filter { en(it.fecha) }
            c.poner("Llamadas", suma(co, { it.fecha }, { it.entrantes + it.salientes }), { n(it) })
            c.poner("Al teléfono", suma(co, { it.fecha }, { it.min_hablado }), ::tiempo)
            c.poner("SMS", suma(co, { it.fecha }, { it.sms_enviados + it.sms_recibidos }), { n(it) })
            val wa = Repo.chats.filter { en(it.fecha) }
            c.poner("WhatsApp enviados", suma(wa, { it.fecha }, { it.mios }), { n(it) })
            c.poner("WhatsApp recibidos", suma(wa, { it.fecha }, { it.otros }), { n(it) })
            // Con la cuenta única de Tu gente y de los reportes (Personas.diasCon).
            if (personasDias.isNotEmpty()) c.add(Cifra("Personas mencionadas", n(personasDias.size.toDouble()), personasDias.values.mapNotNull { it.minOrNull() }.minOrNull()))
            if (c.isNotEmpty()) grupos.add(Grupo("Social", "SOCIETAS", c))
        }
        // ----- Entorno -----
        run {
            val c = ArrayList<Cifra>()
            val en0 = Repo.entorno.values.filter { en(it.fecha) }
            c.poner("Lluvia", suma(en0, { it.fecha }, { it.precip_mm }), { "${n(it)} mm" }) { _ -> "${en0.count { (it.precip_mm ?: 0.0) >= 1 }} días con lluvia" }
            c.poner("Sol directo", suma(en0, { it.fecha }, { it.sol_min }), ::tiempo)
            val tmax = suma(en0, { it.fecha }, { it.t_max }); val tmin = suma(en0, { it.fecha }, { it.t_min })
            if (tmax.n > 0) c.add(Cifra("Día más caluroso", "${n(tmax.max, 1)} °C", tmax.desde, fecha(tmax.fMax!!)))
            if (tmin.n > 0) c.add(Cifra("Noche más fría", "${n(tmin.min, 1)} °C", tmin.desde, fecha(tmin.fMin!!)))
            if (c.isNotEmpty()) grupos.add(Grupo("Entorno", "AMBITVS", c))
        }
        // ----- Cultura -----
        run {
            val c = ArrayList<Cifra>()
            val term = Repo.obras.filter { it.estado == "terminado" && en(it.fin) }
            term.groupBy { it.tipo }.forEach { (t, l) -> c.add(Cifra("Terminados: ${t.replace('_', ' ')}", n(l.size.toDouble()), l.mapNotNull { it.fin }.minOrNull())) }
            val sub = Repo.subrayados.filter { en(it.fecha) }
            conteo("Subrayados de Kindle", sub.size, sub.mapNotNull { it.fecha }.minOrNull(), "${sub.map { it.libro }.distinct().size} libros")?.let(c::add)
            if (c.isNotEmpty()) grupos.add(Grupo("Cultura", "CVLTVRA", c))
        }

        // ---------- Índices ----------
        val indices = ArrayList<Indice>()

        // Alimentos: «g» si la porción está en gramos o tiene peso; «ml» si se registró en mililitros.
        val alimentosOk = run {
            val m = LinkedHashMap<String, Entrada>()
            for (c in comidas) {
                val ml = c.porcion.equals("ml", true)
                val cant = when {
                    ml -> c.cantidad
                    c.porcion.equals("g", true) -> c.cantidad
                    c.cantidad != null && c.peso_porcion_g != null -> c.cantidad * c.peso_porcion_g
                    else -> c.peso_porcion_g
                } ?: 0.0
                val e = m.getOrPut(normal(c.alimento) + if (ml) "|ml" else "") { Entrada(c.alimento, if (ml) "ml" else "g") }
                e.sumar(c.fecha, cant); e.extra += c.valores["kcal"] ?: 0.0
            }
            m.values.sortedByDescending { it.veces * 1e6 + it.total }
        }
        if (alimentosOk.isNotEmpty()) indices.add(Indice("alimentos", "Alimentos", alimentosOk, buscaSinonimos = true) { e ->
            "${cantidad(e)} · ${e.veces} ${if (e.veces == 1) "vez" else "veces"} · ≈ ${n(e.extra)} kcal"
        })

        fun texto(id: String, titulo: String, extraer: (String?) -> List<String>) {
            val m = LinkedHashMap<String, Entrada>()
            dias.forEach { d -> extraer(d.nota).map { Texto.canon(it, eq) }.forEach { p -> m.getOrPut(p) { Entrada(p, "menciones") }.sumar(d.fecha, 1.0) } }
            momentos.forEach { mo -> extraer(mo.texto).map { Texto.canon(it, eq) }.forEach { p -> m.getOrPut(p) { Entrada(p, "menciones") }.sumar(mo.fecha, 1.0) } }
            if (m.isNotEmpty()) indices.add(Indice(id, titulo, m.values.sortedByDescending { it.dias.size * 1e6 + it.total }) { e ->
                "${e.dias.size} ${if (e.dias.size == 1) "día" else "días"} · ${n(e.total)} menciones"
            })
        }
        // Las personas, por días en que aparecen (Personas.diasCon): un día cuenta una vez, se nombre las veces que se nombre.
        run {
            val m = personasDias.map { (p, fs) -> Entrada(p, "días").also { e -> fs.sorted().forEach { f -> e.sumar(f, 1.0) } } }
            if (m.isNotEmpty()) indices.add(Indice("personas", "Personas", m.sortedWith(compareByDescending<Entrada> { it.dias.size }.thenBy { it.nombre })) { e ->
                porNumero(e.dias.size, "1 día", "${e.dias.size} días")
            })
        }
        texto("temas", "Temas") { Texto.temas(it) }

        run {
            val m = LinkedHashMap<String, Entrada>()
            // Las fechas señaladas (con los hitos de antes), como en todas las vistas: «✈ Viaje a la costa».
            dias.forEach { d -> io.github.ruddy2003.vestigium.data.Senaladas.etiquetas(d).distinct().forEach { h -> m.getOrPut(normal(h)) { Entrada(h, "días") }.sumar(d.fecha, 1.0) } }
            if (m.isNotEmpty()) indices.add(Indice("senaladas", "Fechas señaladas", m.values.sortedByDescending { it.total }) { e -> "${porNumero(e.total, "1 día", "${n(e.total)} días")}" })
        }

        if (escuchas.isNotEmpty()) {
            val art = LinkedHashMap<String, Entrada>(); val can = LinkedHashMap<String, Entrada>()
            for (es in escuchas) {
                val min = (es.ms?.toDouble() ?: es.duracion_s?.times(1000.0) ?: 0.0) / 60000
                es.artista.split(", ").map { it.trim() }.filter { it.isNotEmpty() }.distinct().forEach { a ->
                    art.getOrPut(normal(a)) { Entrada(a, "reproducciones") }.apply { sumar(es.fecha, 1.0); extra += min }
                }
                val a0 = es.artista.substringBefore(", ")
                can.getOrPut(normal(es.titulo) + "|" + normal(a0)) { Entrada(if (a0.isBlank()) es.titulo else "${es.titulo} — $a0", "reproducciones") }
                    .apply { sumar(es.fecha, 1.0); extra += min }
            }
            indices.add(Indice("artistas", "Artistas", art.values.sortedByDescending { it.total }) { e -> "${n(e.total)} reproducciones · ${tiempo(e.extra)}" })
            indices.add(Indice("canciones", "Canciones", can.values.sortedByDescending { it.total }) { e -> "${porNumero(e.total, "1 vez", "${n(e.total)} veces")} · ${tiempo(e.extra)}" })
        }

        if (visitas.isNotEmpty()) {
            val m = LinkedHashMap<String, Entrada>()
            visitas.forEach { v ->
                val nombre = Repo.lugares.firstOrNull { it.id == v.lugar }?.nombre ?: return@forEach
                val min = v.salida?.let { (Fechas.millis(it) - Fechas.millis(v.entrada)) / 60000.0 }?.takeIf { it in 0.0..(60.0 * 24 * 3) } ?: 0.0
                m.getOrPut(v.lugar) { Entrada(nombre, "min") }.sumar(v.entrada.take(10), min)
            }
            if (m.isNotEmpty()) indices.add(Indice("lugares", "Lugares", m.values.sortedByDescending { it.total }) { e -> "${tiempo(e.total)} · ${porNumero(e.veces, "1 visita", "${e.veces} visitas")}" })
        }

        if (series.isNotEmpty()) {
            val m = LinkedHashMap<String, Entrada>()
            series.filter { !it.tipo.contains("warm", true) }.forEach { s ->
                val e = m.getOrPut(normal(s.ejercicio)) { Entrada(s.ejercicio, "series") }
                e.sumar(s.fecha, 1.0)
                e.extra += (s.peso_kg ?: 0.0) * (s.reps ?: 0)
                Macrofactor.e1rm(s)?.let { if (it > e.mejor) e.mejor = it }
            }
            indices.add(Indice("ejercicios", "Ejercicios de gimnasio", m.values.sortedByDescending { it.total }) { e ->
                "${porNumero(e.total, "1 serie", "${n(e.total)} series")} · ${porNumero(e.dias.size, "1 día", "${e.dias.size} días")} · ${n(e.extra)} kg" + if (e.mejor > 0) " · 1RM ≈ ${n(e.mejor, 1)} kg" else ""
            })
        }

        Repo.sesiones.filter { en(it.fecha) }.takeIf { it.isNotEmpty() }?.let { l ->
            val m = LinkedHashMap<String, Entrada>()
            l.forEach { s -> m.getOrPut(normal(s.tipo_nombre)) { Entrada(s.tipo_nombre, "min") }.sumar(s.fecha, s.min.toDouble()) }
            indices.add(Indice("deportes", "Ejercicio del reloj", m.values.sortedByDescending { it.total }) { e -> "${tiempo(e.total)} · ${porNumero(e.veces, "1 sesión", "${e.veces} sesiones")}" })
        }

        Repo.ascensos.filter { en(it.fecha) }.takeIf { it.isNotEmpty() }?.let { l ->
            val m = LinkedHashMap<String, Entrada>()
            l.forEach { a -> a.bloques.filter { Grados.encadenado(it.estilo) }.forEach { b -> m.getOrPut(b.grado) { Entrada(b.grado, "bloques") }.sumar(a.fecha, 1.0) } }
            if (m.isNotEmpty()) indices.add(Indice("grados", "Grados encadenados", m.values.sortedByDescending { Grados.valor(it.nombre) ?: 0.0 }) { e -> "${porNumero(e.total, "1 bloque", "${n(e.total)} bloques")} · ${porNumero(e.dias.size, "1 sesión", "${e.dias.size} sesiones")}" })
        }

        if (pantalla.isNotEmpty()) {
            val m = LinkedHashMap<String, Entrada>()
            pantalla.forEach { p -> p.por_app_min.forEach { (app, min) -> m.getOrPut(app) { Entrada(app, "min") }.sumar(p.fecha, min.toDouble()) } }
            if (m.isNotEmpty()) indices.add(Indice("apps", "Apps", m.values.sortedByDescending { it.total }) { e -> "${tiempo(e.total)} · ${porNumero(e.dias.size, "1 día", "${e.dias.size} días")}" })
        }

        if (huellas.isNotEmpty()) {
            val canales = LinkedHashMap<String, Entrada>(); val busq = LinkedHashMap<String, Entrada>()
            huellas.forEach { h ->
                if (h.producto == "youtube" && h.accion == "visto" && h.canal.isNotBlank()) canales.getOrPut(normal(h.canal)) { Entrada(h.canal, "videos") }.sumar(h.fecha, 1.0)
                if (h.accion == "buscado" && h.titulo.isNotBlank()) busq.getOrPut(normal(h.titulo)) { Entrada(h.titulo, "veces") }.sumar(h.fecha, 1.0)
            }
            if (canales.isNotEmpty()) indices.add(Indice("canales", "Canales de YouTube", canales.values.sortedByDescending { it.total }) { e -> "${n(e.total)} videos · ${porNumero(e.dias.size, "1 día", "${e.dias.size} días")}" })
            if (busq.isNotEmpty()) indices.add(Indice("busquedas", "Búsquedas", busq.values.sortedByDescending { it.total }) { e -> "${n(e.total)} ${if (e.total == 1.0) "vez" else "veces"}" })
        }

        if (gastos.isNotEmpty()) {
            val m = LinkedHashMap<String, Entrada>()
            gastos.forEach { g -> m.getOrPut(normal(g.categoria)) { Entrada(g.categoria, "$") }.sumar(g.dia, g.monto) }
            indices.add(Indice("gastos", "Gastos por categoría", m.values.sortedByDescending { it.total }) { e -> "${dinero(e.total)} · ${porNumero(e.veces, "1 gasto", "${e.veces} gastos")}" })
        }

        run {
            val m = LinkedHashMap<String, Entrada>()
            io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().forEach { h -> dias.filter { it.habitos[h.id] == true }.forEach { d -> m.getOrPut(h.id) { Entrada(h.nombre, "días") }.sumar(d.fecha, 1.0) } }
            if (m.isNotEmpty()) indices.add(Indice("habitos", "Hábitos cumplidos", m.values.sortedByDescending { it.total }) { e -> "${porNumero(e.total, "1 día", "${n(e.total)} días")}" })
        }
        run {
            val m = LinkedHashMap<String, Entrada>()
            momentos.filter { it.tipo != "muestreo" }.forEach { mo -> m.getOrPut(mo.tipo) { Entrada(Catalogo.nombreTipo(mo.tipo), "veces") }.sumar(mo.fecha, 1.0) }
            if (m.isNotEmpty()) indices.add(Indice("momentos", "Momentos", m.values.sortedByDescending { it.total }) { e -> "${porNumero(e.total, "1 vez", "${n(e.total)} veces")} · ${porNumero(e.dias.size, "1 día", "${e.dias.size} días")}" })
        }
        run {
            val m = LinkedHashMap<String, Entrada>()
            Catalogo.CONTEXTOS.forEach { cx -> dias.filter { it.ctx[cx.id] == true }.forEach { d -> m.getOrPut(cx.id) { Entrada(cx.nombre, "días") }.sumar(d.fecha, 1.0) } }
            if (m.isNotEmpty()) indices.add(Indice("contextos", "Contextos", m.values.sortedByDescending { it.total }) { e -> "${porNumero(e.total, "1 día", "${n(e.total)} días")}" })
        }
        run {
            val m = LinkedHashMap<String, Entrada>()
            Repo.agenda.values.filter { en(it.fecha) }.forEach { a -> a.titulos.forEach { t -> m.getOrPut(normal(t)) { Entrada(t, "eventos") }.sumar(a.fecha, 1.0) } }
            if (m.isNotEmpty()) indices.add(Indice("agenda", "Eventos de agenda", m.values.sortedByDescending { it.total }) { e -> "${n(e.total)} eventos" })
        }
        run {
            val m = LinkedHashMap<String, Entrada>()
            Repo.chats.filter { en(it.fecha) }.forEach { ch -> m.getOrPut(ch.chat) { Entrada(ch.chat, "mensajes") }.apply { sumar(ch.fecha, (ch.mios + ch.otros).toDouble()); extra += ch.mios } }
            if (m.isNotEmpty()) indices.add(Indice("chats", "Chats de WhatsApp", m.values.sortedByDescending { it.total }) { e -> "${porNumero(e.total, "1 mensaje", "${n(e.total)} mensajes")} (${n(e.extra)} tuyos) · ${porNumero(e.dias.size, "1 día", "${e.dias.size} días")}" })
        }
        run {
            val m = LinkedHashMap<String, Entrada>()
            Repo.subrayados.filter { en(it.fecha) }.forEach { s -> m.getOrPut(normal(s.libro)) { Entrada(s.libro, "subrayados") }.sumar(s.fecha!!, 1.0) }
            if (m.isNotEmpty()) indices.add(Indice("libros", "Libros subrayados", m.values.sortedByDescending { it.total }) { e -> "${porNumero(e.total, "1 subrayado", "${n(e.total)} subrayados")}" })
        }
        return Resultado(desde, hasta, grupos, indices)
    }
}
