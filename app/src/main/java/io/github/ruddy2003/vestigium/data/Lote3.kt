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

    /** Anota una toma. [ts] es cuándo la tomaste (ahora, o antes si la anotas tarde); el día lógico sale de esa hora. */
    fun registrar(s: Suplemento, cantidad: Double = s.dosis, ts: String = Fechas.ahoraIso()): TomaSuplemento {
        val fecha = Fechas.diaLogicoDeMillis(Fechas.millis(ts).takeIf { it > 0 } ?: System.currentTimeMillis())
        val t = TomaSuplemento(Repo.nuevoId(), s.id, ts, fecha, cantidad, ts_creado = Fechas.ahoraIso())
        Repo.guardarToma(t)
        return t
    }

    /** Las de los activos que faltan hoy, con su dosis habitual (lo que hacen «Ya los tomé» y el mosaico). */
    fun registrarFaltantes(): List<TomaSuplemento> {
        val hoy = Fechas.diaLogico()
        return activos().filter { it.modelo != "exposicion" && !tomadoHoy(it.id, hoy) }.map { registrar(it) }
    }

    /** «Creatina 5 g · Vitamina D 2000 UI»: para decir qué se anotó. */
    fun resumen(tomas: List<TomaSuplemento>): String = tomas.joinToString(" · ") { t ->
        val s = de(t.suplemento)
        "${s?.nombre ?: t.suplemento} ${if (t.cantidad % 1.0 == 0.0) t.cantidad.toLong().toString() else t.cantidad.toString()} ${s?.unidad ?: ""}".trim()
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

    /**
     * La dosis con la que la reserva llega al 95 % en [Suplemento.carga_dias]. En la creatina es 3 g, la del estudio del que salen
     * los 28 días (Hultman et al., 1996): con 5 g se llena antes (H-4-6). En los demás, la dosis que pusiste.
     */
    fun dosisReferencia(s: Suplemento): Double = if (s.id == "creatina") 3.0 else s.dosis

    /** Reserva (0 a 100 %) al final de cada día, desde la primera toma hasta [hasta]. */
    fun carga(s: Suplemento, hasta: String = Fechas.diaLogico()): Map<String, Double> {
        val ref = dosisReferencia(s).takeIf { it > 0 } ?: 1.0
        val dosis = dosisPorDia(s.id)
        val inicio = dosis.keys.minOrNull() ?: return emptyMap()
        val sube = 1 - Math.pow(0.05, 1.0 / s.carga_dias.coerceAtLeast(1))
        val baja = 1 - Math.pow(0.05, 1.0 / s.lavado_dias.coerceAtLeast(1))
        val out = LinkedHashMap<String, Double>()
        var nivel = 0.0; var f = inicio
        while (f <= hasta) {
            val d = dosis[f] ?: 0.0
            nivel = if (d > 0) nivel + (1 - Math.pow(1 - sube, d / ref)) * (100 - nivel) else nivel * (1 - baja)
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

    /** Vida media mínima (horas) para calcular el nivel al final del día: con menos, a las 5:00 ya no queda casi nada. */
    const val VIDA_MEDIA_MIN_NIVEL = 6.0

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
                    // Con vida media corta (melatonina, 1 h), lo que queda a las 5:00 es casi siempre 0: no se exporta (H-4-7).
                    "vida_media" -> if (s.vida_media_h >= VIDA_MEDIA_MIN_NIVEL) put(f, "supl_${s.id}_nivel", Math.round(cantidadEn(s, Fechas.finDia(f) + 5 * 3_600_000L - 1) * 100) / 100.0)
                    else -> {
                        val e = estimados[f] ?: 0.0
                        put(f, "supl_${s.id}_porciones", d + e)
                        // Lo leído en la bitácora de comidas solo se sabe los días que tienen bitácora (P-15).
                        if (s.id == "probioticos") { put(f, "probioticos_porciones", d + e); if (f in Repo.nutricion || f in estimados) put(f, "probioticos_estimado", e) }
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
    private val OCIO = listOf("steam", "minecraft", "epic", "netflix", "youtube", "twitch", "spotify", "primevideo", "disney", "max.com", "crunchyroll", "epicgames", "roblox", "league", "leagueoflegends")
    private val SOCIAL = listOf("whatsapp", "telegram", "discord", "instagram", "facebook", "twitter", "x.com", "reddit", "tiktok", "messenger", "linkedin", "bsky")

    fun nombre(raw: String, sitio: Boolean): String {
        val n = raw.trim().lowercase()
        if (n.startsWith("all other")) return if (sitio) "otros sitios" else "otras apps"
        return if (sitio) n.removePrefix("www.") else n.removeSuffix(".exe").removeSuffix(".windows")
    }

    fun categoria(n: String, c: Config = Repo.config): String = c.laptop_categorias[n] ?: when {
        n.startsWith("otr") -> "otro"
        PRODUCTIVO.any { coincide(n, it) } -> "productivo"
        OCIO.any { coincide(n, it) } -> "ocio"
        SOCIAL.any { coincide(n, it) } -> "social"
        else -> "otro"
    }

    /**
     * Si el sitio o la app [n] es de la lista [patron] (P-33). En un sitio (lleva punto) el patrón tiene que ocupar partes completas
     * del dominio: «x.com» es x.com o m.x.com, pero no dropbox.com; «youtube» es youtube.com o m.youtube.com. En una app basta
     * con que el nombre lo contenga (rstudio, leagueclient).
     */
    fun coincide(n: String, patron: String): Boolean =
        if ('.' in n) ".$n.".contains(".$patron.") else n.contains(patron)

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

    /** Los vínculos que dijiste, con los nombres unidos (P-25): si unes @anita con @ana, lo dicho de @anita vale para @ana. */
    fun dichos(c: Config = Repo.config): List<Vinculo> {
        val eq = c.equivalencias
        return c.vinculos.mapNotNull { v ->
            val (x, y) = par(Texto.canon(v.a, eq), Texto.canon(v.b, eq))
            if (x == y) null else Vinculo(x, y, v.tipo)
        }.reversed().distinctBy { it.a to it.b }.reversed()
    }

    fun tipo(a: String, b: String, c: Config = Repo.config): String? {
        val eq = c.equivalencias; val (x, y) = par(Texto.canon(a, eq), Texto.canon(b, eq))
        return dichos(c).firstOrNull { it.a == x && it.b == y }?.tipo
    }

    fun poner(a: String, b: String, tipo: String?) {
        Repo.cambiarConfig { c ->
            val eq = c.equivalencias
            val (x, y) = par(Texto.canon(a, eq), Texto.canon(b, eq))
            if (x == y) c
            // Se quita también lo dicho con otro nombre de las mismas dos personas.
            else c.copy(vinculos = c.vinculos.filterNot { par(Texto.canon(it.a, eq), Texto.canon(it.b, eq)) == (x to y) } + listOfNotNull(tipo?.let { Vinculo(x, y, it) }))
        }
    }

    /** El siguiente tipo al tocar la ficha: nada → se conocen → familia → pareja → colegas → no se conocen → nada. */
    fun siguiente(actual: String?): String? = if (actual == null) TIPOS[0].id else TIPOS.indexOfFirst { it.id == actual }.let { i -> TIPOS.getOrNull(i + 1)?.id }

    /** [relacion] es la de Tu gente (o «Conocerte»); [grupo], la que da el color y la composición: familia, pareja, amistad, trabajo… */
    class Nodo(val nombre: String, val dias: Int, val relacion: String?, val cercania: Int?, var grado: Int = 0, var x: Float = 0f, var y: Float = 0f,
               val grupo: String? = Personas.grupo(relacion))
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
        val dichos = dichos()
        val nombres = (dias.entries.sortedByDescending { it.value }.take(max).map { it.key } + dichos.flatMap { listOf(it.a, it.b) }).distinct()
        val set = nombres.toSet()
        val aristas = ArrayList<Arista>()
        val claves = (juntos.filterValues { it >= minJuntos }.keys + dichos.map { it.a to it.b }).toSet()
        claves.filter { it.first in set && it.second in set }.forEach { k ->
            aristas += Arista(k.first, k.second, dichos.firstOrNull { it.a == k.first && it.b == k.second }?.tipo, juntos[k] ?: 0)
        }
        val nodos = nombres.map { n -> val p = Personas.de(n); Nodo(n, dias[n] ?: 0, p?.relacion ?: Perfiles.relacion(n), p?.cercania) }
        grados(nodos, aristas)
        return nodos to aristas
    }

    /** Con cuántas personas está unida cada una, contando solo [aristas] (al filtrar «solo lo que dije», se recalcula; P-25). */
    fun grados(nodos: List<Nodo>, aristas: List<Arista>) {
        val conectadas = aristas.filter { it.tipo != "no_se_conocen" }
        nodos.forEach { nd -> nd.grado = conectadas.count { it.a == nd.nombre || it.b == nd.nombre } }
    }

    /** Cuántos grupos separados hay con estas [aristas] (sin contar a quienes no tienen ninguna línea). */
    fun componentes(nodos: List<Nodo>, aristas: List<Arista>): Int {
        val conectadas = aristas.filter { it.tipo != "no_se_conocen" }
        val padre = HashMap<String, String>()
        fun raiz(x: String): String { var r = x; while (padre[r] != null && padre[r] != r) r = padre[r]!!; return r }
        val unidos = conectadas.flatMap { listOf(it.a, it.b) }.toSet()
        nodos.filter { it.nombre in unidos }.forEach { padre[it.nombre] = it.nombre }
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
        val k = (0.5 / sqrt(nodos.size.toDouble())).toFloat()
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
                dx[i] += (0.5f - nodos[i].x) * 0.12f; dy[i] += (0.5f - nodos[i].y) * 0.12f
                val d = sqrt(dx[i] * dx[i] + dy[i] * dy[i]).coerceAtLeast(1e-6f)
                nodos[i].x = (nodos[i].x + dx[i] / d * minOf(d, temp)).coerceIn(0.04f, 0.96f)
                nodos[i].y = (nodos[i].y + dy[i] / d * minOf(d, temp)).coerceIn(0.04f, 0.96f)
            }
            temp = (temp * 0.985f).coerceAtLeast(0.002f)
        }
        // Al final se estira para usar todo el cuadro (con margen para los nombres).
        val x0 = nodos.minOf { it.x }; val x1 = nodos.maxOf { it.x }; val y0 = nodos.minOf { it.y }; val y1 = nodos.maxOf { it.y }
        nodos.forEach { n ->
            n.x = if (x1 - x0 < 1e-3f) 0.5f else 0.1f + 0.8f * (n.x - x0) / (x1 - x0)
            n.y = if (y1 - y0 < 1e-3f) 0.5f else 0.1f + 0.8f * (n.y - y0) / (y1 - y0)
        }
    }
}

/** Experimentos guiados: el resultado respetando los días de lavado, el tamaño del efecto y si hay tendencia. */
object Experimentos {
    class Plantilla(val id: String, val nombre: String, val pregunta: String, val accion: String, val resultado: String, val diseno: String, val semanas: Int, val lavado: Int, val nota: String)

    val PLANTILLAS = listOf(
        // 12 semanas: con 28 días de lavado quedan 14 días con la acción y 42 sin ella (con 8 no quedaba ninguno con la acción).
        Plantilla("creatina", "Creatina", "¿La creatina cambia mi fuerza o mi energía?", "Tomar 5 g de creatina al día", "fuerza_ref_kg", "antes_despues", 12, 28,
            "Antes y después: la reserva del músculo tarda semanas en llenarse y en vaciarse, así que alternar semanas no sirve. Se mide con un " +
                "ejercicio fijo, el press inclinado con mancuernas (la 1RM estimada de tu mejor serie), para que no dependa de qué entrenaste ese " +
                "día; solo cuentan los días en que lo hiciste. Si vas subiendo de peso, la cifra sube sola: una mejora no prueba que fue la creatina."),
        Plantilla("hora_dormir", "Hora de dormir", "¿Dormirme antes de las 00:00 mejora cómo me siento?", "Estar en la cama antes de las 00:00", "valencia", "alterno", 6, 1,
            "Semanas alternas sorteadas; el primer día de cada cambio no cuenta."),
        Plantilla("pantalla_noche", "Pantalla antes de dormir", "¿Soltar el teléfono una hora antes de dormir mejora mi sueño?", "Sin pantalla en la última hora antes de dormir", "sueno_calidad", "alterno", 6, 1,
            "Semanas alternas sorteadas."),
        Plantilla("cafeina_tarde", "Cafeína en la tarde", "¿Dejar la cafeína después de las 14:00 me hace dormir más?", "Nada de cafeína después de las 14:00", "sueno_horas", "alterno", 6, 2,
            "Semanas alternas; dos días de lavado por la cafeína que queda en el cuerpo."),
    )

    /** El último día que cuenta: el día en que lo terminaste o, si no, el último del plan. */
    fun ultimoDia(e: Experimento): String {
        val plan = Fechas.sumar(e.inicio, e.semanas * 7L - 1)
        return e.fin?.let { minOf(it, plan) } ?: plan
    }

    /** Si [f] está dentro del experimento (del inicio a su último día). */
    fun cubre(e: Experimento, f: String): Boolean = f >= e.inicio && f <= ultimoDia(e)

    /** En curso: no lo terminaste y el plan no ha vencido (los que aún no empiezan también cuentan). */
    fun enCurso(e: Experimento, hoy: String = Fechas.diaLogico()): Boolean = e.activo && e.fin == null && hoy <= ultimoDia(e)

    /** Días mínimos que deben contar en cada condición para poder sellar un experimento (P-18). */
    const val MIN_DIAS = 7

    /**
     * El plan de semanas. Antes y después: la primera mitad sin la acción y la segunda con ella. Alterno: sorteado por pares de
     * semanas, una «SÍ» y una «no» en cada par en orden al azar (P-18), para que el azar no dé «SÍ SÍ SÍ no no no», que en la
     * práctica es un antes y después. Con un número impar de semanas, la última se sortea sola.
     */
    fun plan(diseno: String, semanas: Int, azar: kotlin.random.Random = kotlin.random.Random(System.currentTimeMillis())): List<Boolean> =
        if (diseno == "antes_despues") List(semanas / 2) { false } + List(semanas - semanas / 2) { true }
        else (0 until semanas / 2).flatMap { if (azar.nextBoolean()) listOf(true, false) else listOf(false, true) } +
            if (semanas % 2 == 1) listOf(azar.nextBoolean()) else emptyList()

    /** Días que cuentan (sin lavado) con la acción y sin ella en el plan de [e], hasta [hasta] (su último día). */
    fun diasPorCondicion(e: Experimento, hasta: String = ultimoDia(e)): Pair<Int, Int> {
        var si = 0; var no = 0
        for (i in 0 until e.semanas * 7) {
            val f = Fechas.sumar(e.inicio, i.toLong())
            if (f > hasta) break
            val t = toca(e, f) ?: continue
            if (enLavado(e, f)) continue
            if (t) si++ else no++
        }
        return si to no
    }

    /**
     * Lo menos que podría quedar en una condición con ese diseño, semanas y lavado, sea cual sea el sorteo (se prueban todos
     * los planes por pares). Si queda menos de [MIN_DIAS], no se deja sellar: el experimento nunca daría resultado.
     */
    fun minimoPosible(diseno: String, semanas: Int, lavado: Int): Int {
        val base = Experimento("x", "", "", "", "2026-01-05", semanas, emptyList(), lavado_dias = lavado)
        val planes = if (diseno == "antes_despues") listOf(plan(diseno, semanas)) else {
            val pares = semanas / 2; val impar = semanas % 2 == 1
            (0 until (1 shl pares)).flatMap { m ->
                val p = (0 until pares).flatMap { if ((m shr it) and 1 == 1) listOf(true, false) else listOf(false, true) }
                if (impar) listOf(p + true, p + false) else listOf(p)
            }
        }
        return planes.minOf { p -> diasPorCondicion(base.copy(plan = p)).let { minOf(it.first, it.second) } }
    }

    /** El plan real quedó en bloque (un solo cambio de condición): se lee como un antes y después, expuesto a tendencias. */
    fun enBloque(e: Experimento): Boolean = e.diseno == "antes_despues" || (1 until e.plan.size).count { e.plan[it] != e.plan[it - 1] } <= 1

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

    /**
     * Cuánto cumpliste, según «¿Lo hiciste hoy?» (I-3): en los días que tocaba la acción y en los que no
     * (sin contar los de lavado). Los días sin respuesta no son «no».
     */
    class Cumplimiento(val diasSi: Int, val respSi: Int, val hechoSi: Int, val diasNo: Int, val respNo: Int, val hechoNo: Int) {
        /** Proporción de días con la acción hecha entre los respondidos (null sin respuestas). */
        val pSi: Double? get() = if (respSi > 0) hechoSi.toDouble() / respSi else null
        val pNo: Double? get() = if (respNo > 0) hechoNo.toDouble() / respNo else null
        val sinRespuesta: Int get() = diasSi - respSi + diasNo - respNo
    }

    /**
     * [provisional]: el plan sigue en curso; el resultado puede cambiar y no debe leerse como señal (detenerse en la primera
     * «señal» multiplica las falsas alarmas). [posibleSi] y [posibleNo]: días que cuentan en todo el plan, se midan o no.
     */
    class Resultado(val con: List<Double>, val sin: List<Double>, val dif: Triple<Double, Double, Double>?, val d: Double?, val tendencia: Double?, val tendenciaClara: Boolean,
                    val cumplimiento: Cumplimiento? = null, val provisional: Boolean = false, val posibleSi: Int = 0, val posibleNo: Int = 0) {
        /**
         * Efecto estimado de hacerlo de verdad: la diferencia según el plan entre la diferencia de cumplimiento
         * (SÍ − no). Supone que saber la condición no cambia nada por sí solo. null si el cumplimiento casi no difiere.
         */
        val cociente: Double? get() {
            val c = cumplimiento ?: return null; val a = c.pSi ?: return null; val b = c.pNo ?: return null
            val dd = dif?.first ?: return null
            return if (a - b >= 0.2) dd / (a - b) else null
        }
    }

    /**
     * El resultado según el plan: los días que tocaba la acción contra los que no, sin los de lavado. Hoy no entra (aún no
     * termina: pantalla, pasos y enfoque estarían a medias), ni lo posterior a terminarlo. La diferencia usa t de Welch y el
     * error estándar corregido porque los días seguidos se parecen (P-17). La tendencia se mide dentro de cada condición, con
     * el modelo valor ~ día + condición, para que el propio efecto no parezca una tendencia (P-18).
     */
    fun resultado(e: Experimento, panel: Map<String, Map<String, Double?>>, hoy: String = Fechas.diaLogico()): Resultado {
        val con = ArrayList<Pair<String, Double>>(); val sin = ArrayList<Pair<String, Double>>()
        val serie = ArrayList<Triple<Double, Double, Double>>()
        var dSi = 0; var rSi = 0; var hSi = 0; var dNo = 0; var rNo = 0; var hNo = 0
        for (i in 0 until e.semanas * 7) {
            val f = Fechas.sumar(e.inicio, i.toLong())
            // Después de terminarlo ya no cuenta: las semanas «SÍ» que quedaban no tuvieron la acción.
            if (f > hoy || (e.fin != null && f > e.fin)) break
            val t = toca(e, f) ?: continue
            if (enLavado(e, f)) continue
            // En la plantilla «Creatina», sin respuesta a «¿Lo hiciste hoy?» cuenta lo que dice el registro de suplementos (H-9-26).
            val h = e.hecho[f] ?: if (e.plantilla == "creatina") panel[f]?.get("supl_creatina_tomado")?.let { it > 0 } else null
            if (t) { dSi++; if (h != null) { rSi++; if (h) hSi++ } } else { dNo++; if (h != null) { rNo++; if (h) hNo++ } }
            if (f == hoy) continue
            val v = panel[f]?.get(e.resultado) ?: continue
            if (t) con.add(f to v) else sin.add(f to v)
            serie.add(Triple(i.toDouble(), if (t) 1.0 else 0.0, v))
        }
        val dif = io.github.ruddy2003.vestigium.ciencia.Estadistica.diferenciaEnDias(con, sin)?.let { Triple(it.d, it.lo, it.hi) }
        val cv = con.map { it.second }; val sv = sin.map { it.second }
        val d = if (cv.size >= 3 && sv.size >= 3) {
            val va = cv.sumOf { (it - cv.average()) * (it - cv.average()) } / (cv.size - 1)
            val vb = sv.sumOf { (it - sv.average()) * (it - sv.average()) } / (sv.size - 1)
            val sp = sqrt(((cv.size - 1) * va + (sv.size - 1) * vb) / (cv.size + sv.size - 2))
            // g de Hedges (H-7-7): la d de Cohen por 1 − 3 / (4·(n₁ + n₂) − 9), que quita lo que se infla con pocos días.
            if (sp > 0) (cv.average() - sv.average()) / sp * (1 - 3.0 / (4 * (cv.size + sv.size) - 9)) else null
        } else null
        val (pend, clara) = tendencia(serie)
        val cum = if (e.hecho.isEmpty() && rSi + rNo == 0) null else Cumplimiento(dSi, rSi, hSi, dNo, rNo, hNo)
        val (pSi, pNo) = diasPorCondicion(e)
        return Resultado(cv, sv, dif, d, pend, clara, cum, enCurso(e, hoy), pSi, pNo)
    }

    /**
     * Pendiente por día dentro de cada condición: mínimos cuadrados de valor ~ día + condición (filas día, condición 0/1, valor).
     * «Clara» si |t| supera el cuantil de la t con n − 3 grados. Null con menos de 8 días.
     */
    fun tendencia(serie: List<Triple<Double, Double, Double>>): Pair<Double?, Boolean> {
        if (serie.size < 8) return null to false
        val mt = serie.map { it.first }.average(); val mc = serie.map { it.second }.average(); val mv = serie.map { it.third }.average()
        var stt = 0.0; var scc = 0.0; var stc = 0.0; var stv = 0.0; var scv = 0.0
        for ((t, c, v) in serie) { val a = t - mt; val b = c - mc; val y = v - mv; stt += a * a; scc += b * b; stc += a * b; stv += a * y; scv += b * y }
        val det = stt * scc - stc * stc
        // Sin variación en la condición (todo SÍ o todo no), queda la pendiente simple.
        val (bt, bc) = if (scc == 0.0 || abs(det) < 1e-9) (if (stt > 0) stv / stt else return null to false) to 0.0
            else (stv * scc - scv * stc) / det to (scv * stt - stv * stc) / det
        val k = if (scc == 0.0 || abs(det) < 1e-9) 2 else 3
        val sse = serie.sumOf { (t, c, v) -> val p = mv + bt * (t - mt) + bc * (c - mc); (v - p) * (v - p) }
        val s2 = sse / (serie.size - k)
        val varBt = if (k == 3) s2 * scc / det else s2 / stt
        if (varBt <= 0) return bt to false
        val tt = bt / sqrt(varBt)
        return bt to (abs(tt) > io.github.ruddy2003.vestigium.ciencia.Estadistica.cuantilT(0.975, (serie.size - k).toDouble()))
    }

    /**
     * Lo que queda sellado antes de empezar (H-7-10), en un orden fijo: pregunta, acción, medida, hipótesis, criterio (umbral y
     * sentido), diseño, inicio, semanas, plan, lavado y la hora del sello. La huella es su SHA-256: si alguno cambia después
     * (una edición o un respaldo que lo reescribe), la huella ya no coincide y la app lo dice.
     */
    fun textoSellado(e: Experimento): String = listOf(e.pregunta, e.accion, e.resultado, e.hipotesis, e.umbral?.toString().orEmpty(), e.sentido?.toString().orEmpty(),
        e.diseno, e.inicio, e.semanas.toString(), e.plan.joinToString("") { if (it) "1" else "0" }, e.lavado_dias.toString(), e.sellada.orEmpty()).joinToString("\u001F")

    fun huella(e: Experimento): String =
        java.security.MessageDigest.getInstance("SHA-256").digest(textoSellado(e).toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

    /** El experimento con su huella puesta. Se llama al sellar. */
    fun sellar(e: Experimento): Experimento = e.copy(huella = huella(e))

    /** true = lo sellado no cambió; false = cambió después del sello; null = no tiene huella (de antes del esquema 15). */
    fun intacto(e: Experimento): Boolean? = e.huella?.let { it == huella(e) }

    /**
     * El criterio sellado aplicado al resultado final (H-7-10): cuenta como efecto si la diferencia va en el sentido que dijiste,
     * mide al menos el umbral (0 si no pusiste uno) y su intervalo de 95 % no toca el cero. Null si no fijaste un sentido, si
     * no hay resultado o si el plan sigue en curso (el criterio se decide al terminar, no a medio camino).
     */
    fun criterio(e: Experimento, r: Resultado): Boolean? {
        val s = e.sentido ?: return null
        val (d, lo, hi) = r.dif ?: return null
        if (r.provisional) return null
        return d * s >= (e.umbral ?: 0.0) && (if (s > 0) lo > 0 else hi < 0)
    }

    fun tamano(d: Double): String = when { abs(d) < 0.2 -> "muy pequeño"; abs(d) < 0.5 -> "pequeño"; abs(d) < 0.8 -> "mediano"; else -> "grande" }
}
