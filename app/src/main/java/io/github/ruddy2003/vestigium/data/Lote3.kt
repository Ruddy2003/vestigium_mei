package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.net.Uri
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Suplementos: catálogo, tomas y tres modelos simplificados (no es consejo médico; los parámetros se cambian en la app).
 * - Vida media (vitamina D, magnesio, melatonina, omega-3): cantidad estimada en el cuerpo, suma de dosis que decaen.
 * - Carga (creatina): reserva del músculo de 0 a 100 %. Cada día con dosis llena una parte de lo que falta (más rápido con
 *   dosis de carga) y cada día sin dosis se vacía otra parte, hasta casi cero tras [Suplemento.lavado_dias].
 * - Exposición (probióticos): porciones por día; las de tu bitácora de MacroFactor van aparte, como estimadas.
 */
object Suplementos {
    val MODELOS = listOf(Opcion("carga", "Carga acumulada"), Opcion("vida_media", "Vida media"), Opcion("exposicion", "Solo porciones"))

    val CATALOGO = listOf(
        Suplemento("creatina", "Creatina", "g", 5.0, "carga", carga_dias = 28, lavado_dias = 35),
        Suplemento("vitamina_d", "Vitamina D", "UI", 2000.0, "vida_media", vida_media_h = 360.0),
        Suplemento("magnesio", "Magnesio", "mg", 300.0, "vida_media", vida_media_h = 12.0),
        Suplemento("melatonina", "Melatonina", "mg", 1.0, "vida_media", vida_media_h = 1.0),
        Suplemento("omega3", "Omega-3", "mg", 1000.0, "vida_media", vida_media_h = 60.0),
        Suplemento("probioticos", "Probióticos", "porción", 1.0, "exposicion"),
    )
    val INICIALES = listOf(CATALOGO[0])

    fun activos(c: Config = Repo.config) = c.suplementos.filter { it.activo }
    fun de(id: String, c: Config = Repo.config) = c.suplementos.firstOrNull { it.id == id }

    fun registrar(s: Suplemento, cantidad: Double = s.dosis, ts: String = Fechas.ahoraIso()) {
        val fecha = Fechas.diaLogicoDeMillis(Fechas.millis(ts).takeIf { it > 0 } ?: System.currentTimeMillis())
        Repo.guardarToma(TomaSuplemento(Repo.nuevoId(), s.id, ts, fecha, cantidad))
    }

    fun tomadoHoy(id: String, hoy: String = Fechas.diaLogico()) = Repo.tomas.any { it.suplemento == id && it.fecha == hoy }

    /** Porciones de probióticos que se leen en tu bitácora de comidas (estimadas, por día). */
    fun probioticosEstimados(c: Config = Repo.config): Map<String, Double> {
        val desde = Repo.nutricion.keys.minOrNull() ?: return emptyMap()
        val palabras = c.probioticos_palabras.map { Texto.slug(it) }.filter { it.isNotBlank() }
        if (palabras.isEmpty()) return emptyMap()
        val out = HashMap<String, Double>()
        runCatching { Repo.comidas(desde, "9999-12-31") }.getOrDefault(emptyList()).forEach { cm ->
            val n = Texto.slug(cm.alimento)
            if (palabras.any { n.contains(it) }) out[cm.fecha] = (out[cm.fecha] ?: 0.0) + 1.0
        }
        return out
    }

    /** Dosis por día de un suplemento (lo anotado a mano). */
    fun dosisPorDia(id: String): Map<String, Double> = Repo.tomas.filter { it.suplemento == id && !it.estimado }.groupBy { it.fecha }.mapValues { (_, l) -> l.sumOf { it.cantidad } }

    /** Reserva (0 a 100 %) al final de cada día, desde la primera toma hasta [hasta]. */
    fun carga(s: Suplemento, hasta: String = Fechas.diaLogico()): Map<String, Double> {
        val dosis = dosisPorDia(s.id)
        val inicio = dosis.keys.minOrNull() ?: return emptyMap()
        val sube = 1 - Math.pow(0.05, 1.0 / s.carga_dias.coerceAtLeast(1))
        val baja = 1 - Math.pow(0.05, 1.0 / s.lavado_dias.coerceAtLeast(1))
        val out = LinkedHashMap<String, Double>()
        var nivel = 0.0; var f = inicio
        while (f <= hasta) {
            val d = dosis[f] ?: 0.0
            nivel = if (d > 0) nivel + (1 - Math.pow(1 - sube, d / s.dosis)) * (100 - nivel) else nivel * (1 - baja)
            out[f] = Math.round(nivel * 10) / 10.0
            f = Fechas.sumar(f, 1)
        }
        return out
    }

    /** Cantidad estimada en el cuerpo en un instante (modelo de vida media). */
    fun cantidadEn(s: Suplemento, ms: Long): Double = Repo.tomas.filter { it.suplemento == s.id }.sumOf { t ->
        val dt = ms - Fechas.millis(t.ts)
        if (dt < 0) 0.0 else t.cantidad * Math.pow(0.5, dt / (s.vida_media_h * 3_600_000.0))
    }

    /** Variables del día: dosis, si se tomó y el nivel según el modelo (para el panel y la exportación). */
    fun panel(c: Config = Repo.config, hoy: String = Fechas.diaLogico()): Map<String, Map<String, Double>> {
        val out = HashMap<String, HashMap<String, Double>>()
        fun put(f: String, k: String, v: Double) { out.getOrPut(f) { HashMap() }[k] = v }
        c.suplementos.forEach { s ->
            val dosis = dosisPorDia(s.id)
            val estimados = if (s.id == "probioticos") probioticosEstimados(c) else emptyMap()
            val inicio = (dosis.keys + estimados.keys).minOrNull() ?: return@forEach
            var f = inicio
            val niveles = if (s.modelo == "carga") carga(s, hoy) else emptyMap()
            while (f <= hoy) {
                val d = dosis[f] ?: 0.0
                put(f, "supl_${s.id}_dosis", d); put(f, "supl_${s.id}_tomado", if (d > 0) 1.0 else 0.0)
                when (s.modelo) {
                    "carga" -> niveles[f]?.let { put(f, "supl_${s.id}_nivel", it); if (s.id == "creatina") put(f, "creatina_saturacion_pct", it) }
                    "vida_media" -> put(f, "supl_${s.id}_nivel", Math.round(cantidadEn(s, Fechas.finDia(f) + 5 * 3_600_000L - 1) * 100) / 100.0)
                    else -> {
                        val e = estimados[f] ?: 0.0
                        put(f, "supl_${s.id}_porciones", d + e)
                        if (s.id == "probioticos") { put(f, "probioticos_porciones", d + e); put(f, "probioticos_estimado", e) }
                    }
                }
                f = Fechas.sumar(f, 1)
            }
        }
        return out
    }
}

/**
 * Cold Turkey Blocker: sus estadísticas salen en dos CSV («App Filename» o «Website Domain», «Date (year-month-day)»,
 * «Minutes Used»). Se reconoce cada uno por su encabezado (no por el nombre del archivo). Reimportar un día lo reemplaza.
 * Los días antes del primer minuto registrado del archivo no cuentan (Cold Turkey todavía no medía) ni los días futuros.
 */
object ColdTurkey {
    val CATEGORIAS = listOf(Opcion("productivo", "Productivo"), Opcion("ocio", "Ocio"), Opcion("social", "Social"), Opcion("otro", "Otro"))
    /** Los navegadores: sus minutos ya se reparten entre los sitios, así que no cuentan en las categorías. */
    val NAVEGADORES = setOf("chrome", "msedge", "firefox", "opera", "brave", "vivaldi", "safari", "arc")
    private val PRODUCTIVO = listOf("winword", "excel", "powerpnt", "onenote", "outlook", "rstudio", "rsession", "code", "pycharm", "notion", "obsidian", "zotero", "acrobat",
        "claude", "chatgpt", "gemini.google", "keep.google", "meet.google", "docs.google", "drive.google", "classroom", "scholar", "overleaf", "github", "deepl", "duolingo", "teams", "zoom")
    private val OCIO = listOf("steam", "minecraft", "epic", "netflix", "youtube", "twitch", "spotify", "primevideo", "disney", "max.com", "crunchyroll", "epicgames", "roblox", "league")
    private val SOCIAL = listOf("whatsapp", "telegram", "discord", "instagram", "facebook", "twitter", "x.com", "reddit", "tiktok", "messenger", "linkedin", "bsky")

    fun nombre(raw: String, sitio: Boolean): String {
        val n = raw.trim().lowercase()
        if (n.startsWith("all other")) return if (sitio) "otros sitios" else "otras apps"
        return if (sitio) n.removePrefix("www.") else n.removeSuffix(".exe").removeSuffix(".windows")
    }

    fun categoria(n: String, c: Config = Repo.config): String = c.laptop_categorias[n] ?: when {
        n.startsWith("otr") -> "otro"
        PRODUCTIVO.any { n.contains(it) } -> "productivo"
        OCIO.any { n.contains(it) } -> "ocio"
        SOCIAL.any { n.contains(it) } -> "social"
        else -> "otro"
    }

    class Leido(val sitios: Boolean, val dias: Map<String, Map<String, Double>>)

    fun leer(texto: String, hoy: String = Fechas.diaLogico()): Leido? {
        val lineas = texto.removePrefix("﻿").lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lineas.size < 2) return null
        val cab = lineas[0].lowercase()
        val sitios = when { cab.contains("website") || cab.contains("domain") -> true; cab.contains("app") -> false; else -> return null }
        val crudo = HashMap<String, HashMap<String, Double>>()
        lineas.drop(1).forEach { l ->
            val c = l.split(',')
            if (c.size < 3) return@forEach
            val min = c.last().trim().toDoubleOrNull() ?: return@forEach
            val p = c[c.size - 2].trim().split('-')
            if (p.size != 3) return@forEach
            val f = runCatching { "%04d-%02d-%02d".format(p[0].toInt(), p[1].toInt(), p[2].toInt()) }.getOrNull() ?: return@forEach
            val n = nombre(c.dropLast(2).joinToString(","), sitios)
            crudo.getOrPut(f) { HashMap() }.merge(n, min, Double::plus)
        }
        val primero = crudo.filterValues { m -> m.values.sum() > 0 }.keys.minOrNull() ?: return Leido(sitios, emptyMap())
        val dias = crudo.filterKeys { it in primero..hoy }.mapValues { (_, m) -> m.filterValues { it > 0 }.mapValues { Math.round(it.value * 100) / 100.0 } }
        return Leido(sitios, dias)
    }

    fun importar(ctx: Context, uris: List<Uri>): String {
        val leidos = uris.mapNotNull { u -> ctx.contentResolver.openInputStream(u)?.use { it.readBytes().toString(Charsets.UTF_8) }?.let { leer(it) } }
        if (leidos.isEmpty()) return "No reconocí el archivo. En Cold Turkey: Statistics → Export, y elige el CSV de apps o de sitios."
        val porDia = HashMap<String, LaptopDia>()
        leidos.forEach { l ->
            l.dias.forEach { (f, m) ->
                val previo = porDia[f] ?: Repo.laptop[f] ?: LaptopDia(f)
                porDia[f] = if (l.sitios) previo.copy(sitios = m) else previo.copy(apps = m)
            }
        }
        Repo.guardarLaptop(porDia.values.toList())
        io.github.ruddy2003.vestigium.avisos.Recordatorios.importado("coldturkey")
        val apps = leidos.count { !it.sitios }; val sitios = leidos.count { it.sitios }
        return "Cold Turkey: ${porDia.size} días (" + listOfNotNull(apps.takeIf { it > 0 }?.let { "apps" }, sitios.takeIf { it > 0 }?.let { "sitios" }).joinToString(" y ") + ")."
    }

    /** Minutos totales, en sitios y por categoría de un día. */
    fun resumen(d: LaptopDia, c: Config = Repo.config): Map<String, Double> {
        val r = HashMap<String, Double>()
        r["laptop_min"] = d.apps.values.sum().takeIf { d.apps.isNotEmpty() } ?: d.sitios.values.sum()
        if (d.sitios.isNotEmpty()) r["laptop_web_min"] = d.sitios.values.sum()
        val porCat = HashMap<String, Double>()
        d.apps.filterKeys { it !in NAVEGADORES }.forEach { (n, m) -> porCat.merge(categoria(n, c), m, Double::plus) }
        d.sitios.forEach { (n, m) -> porCat.merge(categoria(n, c), m, Double::plus) }
        CATEGORIAS.forEach { o -> r["laptop_${o.id}_min"] = Math.round((porCat[o.id] ?: 0.0) * 100) / 100.0 }
        return r
    }

    /** Todos los nombres vistos (apps y sitios) con sus minutos totales, del más usado al menos. */
    fun nombres(): List<Pair<String, Double>> {
        val t = HashMap<String, Double>()
        Repo.laptop.values.forEach { d -> (d.apps + d.sitios).forEach { (n, m) -> t.merge(n, m, Double::plus) } }
        return t.toList().sortedByDescending { it.second }
    }
}

/**
 * La red de tu gente: quién conoce a quién (lo que dices) y quiénes aparecen juntos (lo que se deriva del diario y los
 * momentos, sin lo privado). En la app solo métricas simples; densidad, restricción de Burt y composición se calculan en R.
 */
object RedPersonas {
    val TIPOS = listOf(Opcion("se_conocen", "Se conocen"), Opcion("familia", "Familia entre sí"), Opcion("pareja", "Pareja"), Opcion("colegas", "Colegas"),
        Opcion("no_se_conocen", "No se conocen"))
    fun nombreTipo(id: String?) = TIPOS.firstOrNull { it.id == id }?.nombre ?: ""

    private fun par(a: String, b: String) = if (a < b) a to b else b to a

    fun tipo(a: String, b: String, c: Config = Repo.config): String? { val (x, y) = par(a, b); return c.vinculos.firstOrNull { it.a == x && it.b == y }?.tipo }

    fun poner(a: String, b: String, tipo: String?) {
        if (a == b) return
        val (x, y) = par(a, b)
        Repo.cambiarConfig { c -> c.copy(vinculos = c.vinculos.filterNot { it.a == x && it.b == y } + listOfNotNull(tipo?.let { Vinculo(x, y, it) })) }
    }

    /** El siguiente tipo al tocar la ficha: nada → se conocen → familia → pareja → colegas → no se conocen → nada. */
    fun siguiente(actual: String?): String? = if (actual == null) TIPOS[0].id else TIPOS.indexOfFirst { it.id == actual }.let { i -> TIPOS.getOrNull(i + 1)?.id }

    class Nodo(val nombre: String, val dias: Int, val relacion: String?, val cercania: Int?, var grado: Int = 0, var x: Float = 0f, var y: Float = 0f)
    class Arista(val a: String, val b: String, val tipo: String?, val juntos: Int)

    /** Nodos (las personas más mencionadas y las que tienen vínculos dichos) y aristas: dichas o con [minJuntos] días juntas. */
    fun calcular(minJuntos: Int = 2, max: Int = 45): Pair<List<Nodo>, List<Arista>> {
        val eq = Repo.equivalencias()
        val porDia = HashMap<String, MutableSet<String>>()
        fun sumar(f: String, t: String) { Texto.personas(t).forEach { porDia.getOrPut(f) { HashSet() }.add(Texto.canon(it, eq)) } }
        Repo.dias.values.forEach { d -> sumar(d.fecha, d.nota) }
        Repo.momentos.filter { !it.privado }.forEach { m -> sumar(m.fecha, m.texto) }
        val dias = HashMap<String, Int>(); val juntos = HashMap<Pair<String, String>, Int>()
        porDia.values.forEach { g ->
            g.forEach { dias[it] = (dias[it] ?: 0) + 1 }
            val l = g.sorted(); for (i in l.indices) for (j in i + 1 until l.size) juntos.merge(l[i] to l[j], 1, Int::plus)
        }
        val dichos = Repo.config.vinculos
        val nombres = (dias.entries.sortedByDescending { it.value }.take(max).map { it.key } + dichos.flatMap { listOf(it.a, it.b) }).distinct()
        val set = nombres.toSet()
        val aristas = ArrayList<Arista>()
        val claves = (juntos.filterValues { it >= minJuntos }.keys + dichos.map { it.a to it.b }).toSet()
        claves.filter { it.first in set && it.second in set }.forEach { k ->
            aristas += Arista(k.first, k.second, dichos.firstOrNull { it.a == k.first && it.b == k.second }?.tipo, juntos[k] ?: 0)
        }
        val conectadas = aristas.filter { it.tipo != "no_se_conocen" }
        val nodos = nombres.map { n -> val p = Personas.de(n); Nodo(n, dias[n] ?: 0, p?.relacion ?: Perfiles.relacion(n), p?.cercania) }
        nodos.forEach { nd -> nd.grado = conectadas.count { it.a == nd.nombre || it.b == nd.nombre } }
        return nodos to aristas
    }

    /** Cuántos grupos separados hay (sin contar a quienes no tienen ninguna línea). */
    fun componentes(nodos: List<Nodo>, aristas: List<Arista>): Int {
        val conectadas = aristas.filter { it.tipo != "no_se_conocen" }
        val padre = HashMap<String, String>()
        fun raiz(x: String): String { var r = x; while (padre[r] != null && padre[r] != r) r = padre[r]!!; return r }
        nodos.filter { it.grado > 0 }.forEach { padre[it.nombre] = it.nombre }
        conectadas.forEach { val ra = raiz(it.a); val rb = raiz(it.b); if (ra != rb) padre[ra] = rb }
        return padre.keys.map { raiz(it) }.toSet().size
    }

    /** Coloca los nodos con un modelo de fuerzas (resortes entre los unidos, repulsión entre todos). Coordenadas de 0 a 1. */
    fun acomodar(nodos: List<Nodo>, aristas: List<Arista>, vueltas: Int = 300) {
        if (nodos.isEmpty()) return
        val r = java.util.Random(7)
        nodos.forEach { it.x = 0.2f + 0.6f * r.nextFloat(); it.y = 0.2f + 0.6f * r.nextFloat() }
        val idx = nodos.withIndex().associate { it.value.nombre to it.index }
        val lazos = aristas.filter { it.tipo != "no_se_conocen" }.mapNotNull { a -> val i = idx[a.a]; val j = idx[a.b]; if (i != null && j != null) Triple(i, j, a) else null }
        val k = (0.9 / sqrt(nodos.size.toDouble())).toFloat()
        var temp = 0.1f
        repeat(vueltas) {
            val dx = FloatArray(nodos.size); val dy = FloatArray(nodos.size)
            for (i in nodos.indices) for (j in i + 1 until nodos.size) {
                val ex = nodos[i].x - nodos[j].x; val ey = nodos[i].y - nodos[j].y
                val d2 = (ex * ex + ey * ey).coerceAtLeast(1e-4f); val d = sqrt(d2)
                val f = k * k / d
                dx[i] += ex / d * f; dy[i] += ey / d * f; dx[j] -= ex / d * f; dy[j] -= ey / d * f
            }
            lazos.forEach { (i, j, a) ->
                val ex = nodos[i].x - nodos[j].x; val ey = nodos[i].y - nodos[j].y
                val d = sqrt(ex * ex + ey * ey).coerceAtLeast(1e-3f)
                val peso = if (a.tipo != null) 1.4f else (0.5f + a.juntos * 0.05f).coerceAtMost(1.2f)
                val f = d * d / k * peso
                dx[i] -= ex / d * f; dy[i] -= ey / d * f; dx[j] += ex / d * f; dy[j] += ey / d * f
            }
            for (i in nodos.indices) {
                // Una leve atracción al centro para que los sueltos no se vayan a las orillas.
                dx[i] += (0.5f - nodos[i].x) * 0.05f; dy[i] += (0.5f - nodos[i].y) * 0.05f
                val d = sqrt(dx[i] * dx[i] + dy[i] * dy[i]).coerceAtLeast(1e-6f)
                nodos[i].x = (nodos[i].x + dx[i] / d * minOf(d, temp)).coerceIn(0.04f, 0.96f)
                nodos[i].y = (nodos[i].y + dy[i] / d * minOf(d, temp)).coerceIn(0.04f, 0.96f)
            }
            temp = (temp * 0.985f).coerceAtLeast(0.002f)
        }
    }
}

/** Experimentos guiados: el resultado respetando los días de lavado, el tamaño del efecto y si hay tendencia. */
object Experimentos {
    class Plantilla(val id: String, val nombre: String, val pregunta: String, val accion: String, val resultado: String, val diseno: String, val semanas: Int, val lavado: Int, val nota: String)

    val PLANTILLAS = listOf(
        Plantilla("creatina", "Creatina", "¿La creatina cambia mi fuerza o mi energía?", "Tomar 5 g de creatina al día", "volumen_kg", "antes_despues", 8, 28,
            "Antes y después: la reserva del músculo tarda semanas en llenarse y en vaciarse, así que alternar semanas no sirve."),
        Plantilla("hora_dormir", "Hora de dormir", "¿Dormirme antes de las 00:00 mejora cómo me siento?", "Estar en la cama antes de las 00:00", "valencia", "alterno", 6, 1,
            "Semanas alternas sorteadas; el primer día de cada cambio no cuenta."),
        Plantilla("pantalla_noche", "Pantalla antes de dormir", "¿Soltar el teléfono una hora antes de dormir mejora mi sueño?", "Sin pantalla en la última hora antes de dormir", "sueno_calidad", "alterno", 6, 1,
            "Semanas alternas sorteadas."),
        Plantilla("cafeina_tarde", "Cafeína en la tarde", "¿Dejar la cafeína después de las 14:00 me hace dormir más?", "Nada de cafeína después de las 14:00", "sueno_horas", "alterno", 6, 2,
            "Semanas alternas; dos días de lavado por la cafeína que queda en el cuerpo."),
    )

    /** Si toca la acción ese día (null si el día no está en el experimento). */
    fun toca(e: Experimento, f: String): Boolean? {
        val d = Fechas.diasEntre(e.inicio, f)
        if (d < 0) return null
        val s = (d / 7).toInt()
        return e.plan.getOrNull(s)
    }

    /** Días que no cuentan: los primeros [Experimento.lavado_dias] tras cada cambio de condición (y tras empezar con la acción). */
    fun enLavado(e: Experimento, f: String): Boolean {
        if (e.lavado_dias <= 0) return false
        val d = Fechas.diasEntre(e.inicio, f).toInt()
        val cambios = (if (e.plan.firstOrNull() == true) listOf(0) else emptyList()) + (1 until e.plan.size).filter { e.plan[it] != e.plan[it - 1] }.map { it * 7 }
        return cambios.any { d - it in 0 until e.lavado_dias }
    }

    class Resultado(val con: List<Double>, val sin: List<Double>, val dif: Triple<Double, Double, Double>?, val d: Double?, val tendencia: Double?, val tendenciaClara: Boolean)

    fun resultado(e: Experimento, panel: Map<String, Map<String, Double?>>, hoy: String = Fechas.diaLogico()): Resultado {
        val con = ArrayList<Double>(); val sin = ArrayList<Double>()
        val serie = ArrayList<Pair<Double, Double>>()
        for (i in 0 until e.semanas * 7) {
            val f = Fechas.sumar(e.inicio, i.toLong())
            if (f > hoy) break
            val t = toca(e, f) ?: continue
            if (enLavado(e, f)) continue
            val v = panel[f]?.get(e.resultado) ?: continue
            if (t) con.add(v) else sin.add(v)
            serie.add(i.toDouble() to v)
        }
        val dif = io.github.ruddy2003.vestigium.ciencia.Estadistica.diferencia(con, sin)
        val d = if (con.size >= 3 && sin.size >= 3) {
            val va = con.sumOf { (it - con.average()) * (it - con.average()) } / (con.size - 1)
            val vb = sin.sumOf { (it - sin.average()) * (it - sin.average()) } / (sin.size - 1)
            val sp = sqrt(((con.size - 1) * va + (sin.size - 1) * vb) / (con.size + sin.size - 2))
            if (sp > 0) (con.average() - sin.average()) / sp else null
        } else null
        // Tendencia: pendiente del resultado contra el día; si es clara, la diferencia puede deberse al paso del tiempo.
        var pend: Double? = null; var clara = false
        if (serie.size >= 8) {
            val mx = serie.map { it.first }.average(); val my = serie.map { it.second }.average()
            val sxx = serie.sumOf { (it.first - mx) * (it.first - mx) }
            if (sxx > 0) {
                val b = serie.sumOf { (it.first - mx) * (it.second - my) } / sxx
                val res = serie.sumOf { val p = my + b * (it.first - mx); (it.second - p) * (it.second - p) } / (serie.size - 2)
                val se = sqrt(res / sxx)
                pend = b; clara = se > 0 && abs(b / se) > 2.0
            }
        }
        return Resultado(con, sin, dif, d, pend, clara)
    }

    fun tamano(d: Double): String = when { abs(d) < 0.2 -> "muy pequeño"; abs(d) < 0.5 -> "pequeño"; abs(d) < 0.8 -> "mediano"; else -> "grande" }
}
