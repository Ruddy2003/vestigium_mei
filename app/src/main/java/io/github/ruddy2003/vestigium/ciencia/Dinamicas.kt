package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Perfiles
import io.github.ruddy2003.vestigium.data.Politica
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.privado

/**
 * Variables que dependen de lo que registraste: una por cada persona, tema, hito, lugar, app, categoría de gasto,
 * artista, canal, obra, género, medicamento, ciudad, país, músculo, nutriente, alimento… Así todo se puede cruzar con todo.
 *
 * En los análisis entran las más frecuentes de cada familia (para no ahogar los resultados en casos raros);
 * en la exportación extendida entran todas las que aparecen al menos dos días.
 */
object Dinamicas {
    class Resultado(val variables: List<Variable>, val valores: Map<String, Map<String, Double>>)

    /** Prefijo de cada familia → variables fijas que la resumen (pares obvios: «minutos en Instagram» con «minutos de pantalla»). */
    val AGREGADOS: Map<String, Set<String>> = mapOf(
        "per_" to setOf("personas", "cercanos", "vi_familia", "vi_pareja", "vi_amistades", "vi_convivientes", "palabras", "cartas_otros",
            "personas_vistas", "personas_pensadas", "personas_mensajes", "vi_expareja", "vi_trabajo", "cercania_media", "presencias_revisado",
            "penso_en_pareja", "penso_en_expareja", "penso_en_familia", "penso_en_amistades"),
        "pen_" to setOf("personas", "personas_pensadas", "penso_en_pareja", "penso_en_expareja", "penso_en_familia", "penso_en_amistades"),
        "tema_" to setOf("palabras"), "hito_" to setOf("hitos_n"),
        "lug_" to setOf("lugares_distintos", "distancia_km"),
        "app_" to setOf("pantalla_min", "desbloqueos"), "ntf_" to setOf("notificaciones"),
        "gcat_" to setOf("gasto_total", "gasto_imprevisto", "gasto_imprevisto_monto", "balance_dinero"), "icat_" to setOf("ingreso_total", "balance_dinero"),
        "art_" to setOf("canciones", "musica_min", "audifonos_min"), "ytc_" to setOf("yt_vistos", "busquedas"),
        "gen_" to setOf("obras_dia", "lectura", "vi_pelicula_serie", "jugue", "obra_terminada"), "obr_" to setOf("obras_dia", "lectura", "vi_pelicula_serie", "jugue", "obra_terminada", "calificacion_obra"),
        "med_" to setOf("medicamentos_n"), "det_" to setOf("impulso", "recaida"),
        "ciu_" to setOf("ciudades_dia", "fuera_de_casa", "extranjero", "ciudad_nueva", "distancia_km", "vehiculo_min", "unesco"),
        "pais_" to setOf("ciudades_dia", "fuera_de_casa", "extranjero", "ciudad_nueva", "distancia_km", "vehiculo_min", "unesco"),
        "musc_" to setOf("gimnasio", "volumen_kg", "series_gimnasio", "fuerza_ref_kg", "ejercicio_min", "cal_activas"),
        "act_" to setOf("ejercicio_min", "pasos", "cal_activas", "a_pie_min", "distancia_km"),
        "nut_" to setOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "azucares_g", "cafeina_mg", "balance_kcal"),
        "ali_" to setOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "azucares_g", "cafeina_mg", "comidas"),
        "lap_" to setOf("laptop_min", "laptop_productivo_min", "laptop_ocio_min", "laptop_social_min", "laptop_otro_min"),
        "web_" to setOf("laptop_min", "laptop_web_min", "laptop_productivo_min", "laptop_ocio_min", "laptop_social_min", "laptop_otro_min"),
        "suenot_" to setOf("sueno_recordado", "sueno_valencia", "sueno_palabras", "sueno_lucidez", "sueno_vividez"),
    )

    /** Familias cuyas variables, entre ellas, también son obvias (dos nutrientes del mismo plato, una ciudad y su país). */
    val ENTRE_SI: List<Set<String>> = listOf(setOf("nut_", "ali_"), setOf("ciu_", "pais_"), setOf("gen_", "obr_"), setOf("pol_"), setOf("vida_"), setOf("suenot_"),
        // Dos músculos del mismo entrenamiento: con un split empuje/tirón van juntos (espalda alta y dorsales) o se excluyen (tríceps y bíceps) por diseño.
        setOf("musc_", "act_"), setOf("art_"), setOf("lap_", "web_"))

    fun prefijo(id: String): String? = (AGREGADOS.keys + listOf("esc_", "pol_", "vida_")).firstOrNull { id.startsWith(it) }

    /** Lo que significa cada columna dinámica, para el diccionario de la exportación. */
    @Volatile var nombres: Map<String, String> = emptyMap(); private set

    @Volatile private var cache: Pair<List<Any?>, Resultado>? = null
    @Volatile private var cacheExt: Pair<List<Any?>, Resultado>? = null

    private fun clave(): List<Any?> = listOf(Repo.dias, Repo.momentos, Repo.obras, Repo.cartas, Repo.gastos, Repo.ingresos, Repo.pantalla, Repo.notifs,
        Repo.huellas, Repo.nutricion, Repo.entrenos, Repo.actividad, Repo.visitas, Repo.lugares, Repo.respuestas, Repo.config, Mundo.version, Fechas.diaLogico(), Repo.laptop)

    private fun igual(a: List<Any?>, b: List<Any?>) = a.size == b.size && a.indices.all { a[it] === b[it] || a[it] == b[it] && a[it] !is Collection<*> && a[it] !is Map<*, *> }

    fun calcular(extendido: Boolean = false): Resultado {
        val k = clave()
        (if (extendido) cacheExt else cache)?.let { if (igual(it.first, k)) return it.second }
        val r = construir(extendido)
        if (extendido) cacheExt = k to r else cache = k to r
        nombres = nombres + r.variables.associate { it.id to it.nombre }
        return r
    }

    private fun slug(s: String): String = Texto.slug(s).take(40).ifEmpty { "x" + (s.hashCode() and 0xffff).toString(16) }

    private fun construir(ext: Boolean): Resultado {
        val hoy = Fechas.diaLogico()
        val vars = ArrayList<Variable>()
        val out = HashMap<String, HashMap<String, Double>>()
        val usados = HashSet<String>()
        fun pon(f: String, id: String, v: Double) { out.getOrPut(f) { HashMap() }[id] = v }

        /**
         * Una familia: [porDia] es fecha → (cosa → valor). En los días de [base] lo que no aparece vale 0.
         * Se quedan las cosas que aparecen al menos [minDias] días, ordenadas por días (o por [peso]), hasta [max].
         */
        fun familia(prefijo: String, grupo: String, porDia: Map<String, Map<String, Double>>, base: Collection<String>, max: Int, minDias: Int,
                    nombre: (String) -> String, porPeso: Boolean = false) {
            val dias = HashMap<String, Int>(); val peso = HashMap<String, Double>()
            porDia.values.forEach { m -> m.forEach { (c, v) -> if (v != 0.0) { dias.merge(c, 1, Int::plus); peso.merge(c, v, Double::plus) } } }
            val minimo = if (ext) 2 else minDias
            val elegidas = dias.filter { it.value >= minimo }.keys
                .sortedWith(if (porPeso) compareByDescending<String> { peso[it] ?: 0.0 } else compareByDescending<String> { dias[it] ?: 0 }.thenByDescending { peso[it] ?: 0.0 })
                .take(if (ext) 400 else max)
            val fechas = (base + porDia.keys).toSet()
            for (c in elegidas) {
                var id = prefijo + slug(c); var n = 2
                while (id in usados) { id = prefijo + slug(c) + "_" + n; n++ }
                usados += id
                vars += Variable(id, nombre(c), grupo)
                for (f in fechas) pon(f, id, porDia[f]?.get(c) ?: 0.0)
            }
        }
        fun cobertura(fechas: Collection<String>): List<String> {
            val a = fechas.minOrNull() ?: return emptyList(); val z = fechas.maxOrNull() ?: return emptyList()
            return Repo.dias.keys.filter { it in a..z } + fechas
        }
        val eq = Repo.equivalencias()
        // Lo privado no cuenta: ni sus @menciones ni sus #temas, ni sus días como «días con texto».
        val publicos = Repo.momentos.filter { !it.privado }
        // «No apareció» (0) solo en los días en que pudo aparecer (P-15): con diario escrito o con un momento que nombra a alguien
        // (en los temas, con algún #tema). Un día sin nada escrito queda sin dato.
        val diasPersonas = diasSociales()
        val diasTemas = Repo.dias.filterValues { it.nota.isNotBlank() }.keys + publicos.filter { Texto.temas(it.texto).isNotEmpty() }.map { it.fecha }

        // ---- Personas: el diario, los momentos, los sueños y las cartas que les escribiste.
        val personas = HashMap<String, HashMap<String, Double>>(); val temas = HashMap<String, HashMap<String, Double>>()
        fun texto(f: String, t: String) {
            Texto.personas(t).forEach { personas.getOrPut(f) { HashMap() }[Texto.canon(it, eq)] = 1.0 }
            Texto.temas(t).forEach { temas.getOrPut(f) { HashMap() }[Texto.canon(it, eq)] = 1.0 }
        }
        Repo.dias.forEach { (f, d) ->
            texto(f, d.nota); texto(f, d.sueno_texto); texto(f, d.mejor_momento + " " + d.peor_momento)
            // En el sueño, solo lo que lleva @ es una persona y lo que lleva # un tema; una etiqueta sin símbolo no cuenta (P-25).
            d.sueno_temas.forEach { t ->
                val x = Texto.etiqueta(t)
                when { t.trim().startsWith("#") -> temas.getOrPut(f) { HashMap() }[Texto.canon(x, eq)] = 1.0; t.trim().startsWith("@") -> personas.getOrPut(f) { HashMap() }[Texto.canon(x, eq)] = 1.0 }
            }
        }
        publicos.forEach { m -> texto(m.fecha, m.texto) }
        Repo.cartas.filter { it.para.isNotBlank() }.forEach { c -> personas.getOrPut(c.escrita) { HashMap() }[Texto.canon(c.para, eq)] = 1.0; texto(c.escrita, c.texto) }
        familia("per_", "Personas", personas, diasPersonas, 15, 5, { "Aparece @$it (diario, mejor y peor momento, momentos, sueños o cartas) (0/1)" })
        // ---- En quién pensaste sin verla (lo marcas en el diario; en los momentos, «pensando en…» estando solo).
        val pensadas = HashMap<String, HashMap<String, Double>>()
        val momentosPorDia = Repo.momentos.groupBy { it.fecha }
        (Repo.dias.keys + momentosPorDia.keys).toSet().forEach { f ->
            io.github.ruddy2003.vestigium.data.Presencias.delDia(f, eq, Repo.dias[f], momentosPorDia[f].orEmpty())
                .filterValues { it == io.github.ruddy2003.vestigium.data.Presencias.PENSE }.keys.forEach { p -> pensadas.getOrPut(f) { HashMap() }[p] = 1.0 }
        }
        if (pensadas.isNotEmpty()) familia("pen_", "Personas", pensadas, diasPersonas, 10, 3, { "Pensé en @$it sin verla (0/1)" })
        familia("tema_", "Temas", temas, diasTemas, 12, 5, { "Aparece #$it (0/1)" })
        familia("hito_", "Hitos", Repo.dias.mapValues { (_, d) -> d.hitos.associateWith { 1.0 } }, Repo.dias.keys, 10, 3, { "Hito: ${it.replace('_', ' ')} (0/1)" })

        // ---- Lugares marcados: horas en cada uno.
        // Cada visita se reparte entre los días lógicos que cruza, como horas_tipo_* (Lugares.minutosPorTipo). Antes iba entera
        // (con tope de 24 h) al día en que llegaste: dos noches en casa contaban 24 h el primer día y 0 los siguientes.
        val lugares = HashMap<String, HashMap<String, Double>>()
        val ahoraMs = System.currentTimeMillis()
        Repo.visitas.forEach { v ->
            val l = Repo.lugares.firstOrNull { it.id == v.lugar } ?: return@forEach
            var a = Fechas.millis(v.entrada); val z = v.salida?.let { Fechas.millis(it) } ?: ahoraMs
            if (a <= 0 || z <= a || z - a > 40L * 86_400_000L) return@forEach
            while (a < z) {
                val f = Fechas.diaLogicoDeMillis(a)
                val y = minOf(z, Fechas.finDiaLogico(f))
                if (y <= a) break
                lugares.getOrPut(f) { HashMap() }.merge(l.nombre, (y - a) / 3_600_000.0, Double::plus)
                a = y
            }
        }
        lugares.values.forEach { m -> m.replaceAll { _, h -> minOf(h, 24.0) } }
        familia("lug_", "Lugares", lugares, cobertura(lugares.keys), 15, 3, { "Horas en «$it»" }, porPeso = true)

        // ---- Teléfono: minutos por app, notificaciones por app, YouTube por canal.
        familia("app_", "Apps", Repo.pantalla.mapValues { (_, p) -> p.por_app_min.mapValues { it.value.toDouble() } }, Repo.pantalla.keys, 12, 5, { "Minutos en $it" }, porPeso = true)
        familia("ntf_", "Notificaciones", Repo.notifs.mapValues { (_, n) -> n.por_app.mapValues { it.value.toDouble() } }, Repo.notifs.keys, 8, 5, { "Notificaciones de $it" }, porPeso = true)
        familia("lap_", "Laptop", Repo.laptop.mapValues { (_, d) -> d.apps }, Repo.laptop.keys, 10, 4, { "Minutos en $it (laptop)" }, porPeso = true)
        familia("web_", "Laptop", Repo.laptop.mapValues { (_, d) -> d.sitios }, Repo.laptop.keys, 10, 4, { "Minutos en $it (navegador de la laptop)" }, porPeso = true)
        familia("ytc_", "YouTube", Repo.huellas.mapValues { (_, h) -> h.canales.mapValues { it.value.toDouble() } }, Repo.huellas.keys, 8, 5, { "Videos de $it en YouTube" })

        // ---- Dinero por categoría.
        val gcat = HashMap<String, HashMap<String, Double>>(); Repo.gastos.forEach { g -> gcat.getOrPut(g.dia) { HashMap() }.merge(g.categoria, g.monto, Double::plus) }
        familia("gcat_", "Dinero", gcat, cobertura(gcat.keys), 15, 5, { "Gasto en ${it.lowercase()}" }, porPeso = true)
        val icat = HashMap<String, HashMap<String, Double>>(); Repo.ingresos.forEach { g -> icat.getOrPut(g.dia) { HashMap() }.merge(g.categoria, g.monto, Double::plus) }
        familia("icat_", "Dinero", icat, cobertura(icat.keys), 8, 3, { "Ingreso de ${it.lowercase()}" }, porPeso = true)

        // ---- Música: canciones por artista.
        runCatching {
            val desde = Repo.dias.keys.minOrNull() ?: "0000-01-01"
            val art = HashMap<String, HashMap<String, Double>>()
            Repo.escuchas(desde, "9999-12-31").forEach { e -> if (e.artista.isNotBlank() && (e.ms ?: 30_000) >= 30_000) art.getOrPut(e.fecha) { HashMap() }.merge(e.artista, 1.0, Double::plus) }
            familia("art_", "Música", art, cobertura(art.keys), 10, 5, { "Canciones de $it" })
        }

        // ---- Lo que leíste, viste y jugaste: cada obra y cada género (etiqueta).
        val obras = HashMap<String, HashMap<String, Double>>(); val generos = HashMap<String, HashMap<String, Double>>()
        Repo.obras.filter { it.titulo.isNotBlank() }.forEach { o ->
            (o.dias + listOfNotNull(o.fin.takeIf { o.estado == "terminado" })).distinct().forEach { f ->
                obras.getOrPut(f) { HashMap() }[o.titulo] = 1.0
                o.etiquetas.forEach { g -> generos.getOrPut(f) { HashMap() }[g] = 1.0 }
            }
        }
        familia("obr_", "Cultura", obras, cobertura(obras.keys), 10, 5, { "Le dediqué tiempo a «$it» (0/1)" })
        familia("gen_", "Cultura", generos, cobertura(generos.keys), 10, 5, { "Leí, vi o jugué algo de ${it.replace('_', ' ')} (0/1)" })

        // ---- Medicamentos y detonantes.
        familia("med_", "Privado", Repo.dias.mapValues { (_, d) -> d.medicamentos.associateWith { 1.0 } }, cobertura(Repo.dias.filterValues { it.medicamentos.isNotEmpty() }.keys), 15, 3, { "Tomé $it (0/1)" })
        familia("det_", "Privado", Repo.dias.mapValues { (_, d) -> d.detonantes.associateWith { 1.0 } }, cobertura(Repo.dias.filterValues { it.detonantes.isNotEmpty() }.keys), 10, 3, { "Detonante: ${it.replace('_', ' ')} (0/1)" })

        // ---- El sueño de la noche: qué tan raro fue y si fue pesadilla (esquema 17; los días de antes, desde su «tipo»).
        val catS = io.github.ruddy2003.vestigium.data.Catalogo
        familia("suenot_", "Sueño", Repo.dias.filterValues { it.sueno_recordado }.mapValues { (_, d) ->
                (listOfNotNull(catS.rarezaDe(d)) + listOfNotNull("pesadilla".takeIf { catS.pesadillaDe(d) == true })).associateWith { 1.0 } },
            Repo.dias.filterValues { it.sueno_recordado }.keys, 6, 3,
            { if (it == "pesadilla") "Sueño: pesadilla (0/1)" else "Sueño ${catS.SUENO_RAREZA.firstOrNull { o -> o.id == it }?.nombre?.lowercase() ?: it} (0/1)" })

        // ---- Mundo: horas en cada ciudad y país.
        runCatching {
            val ctx = Mundo.app ?: return@runCatching
            val enMain = android.os.Looper.myLooper() == android.os.Looper.getMainLooper()
            val diario = (if (enMain) Mundo.diarioListo() else Mundo.diario(ctx)) ?: return@runCatching
            val ciu = HashMap<String, HashMap<String, Double>>(); val pai = HashMap<String, HashMap<String, Double>>()
            diario.forEach { (d, m) ->
                val f = Fechas.iso(java.time.LocalDate.ofEpochDay(d.toLong()))
                m.ciudades.forEach { (e, h) -> ciu.getOrPut(f) { HashMap() }[e.nombre + " (" + e.pais + ")"] = if (h == 0.0) 1.0 else h }
                m.paises.forEach { (e, h) -> pai.getOrPut(f) { HashMap() }[e.nombre] = if (h == 0.0) 1.0 else h }
            }
            familia("ciu_", "Mundo", ciu, ciu.keys, 8, 3, { "Horas en $it" })
            familia("pais_", "Mundo", pai, pai.keys, 6, 3, { "Horas en $it" })
        }

        // ---- Cuerpo: series por grupo muscular y minutos por tipo de movimiento.
        familia("musc_", "Cuerpo", Repo.entrenos.mapValues { (_, e) -> e.series_musculo }, cobertura(Repo.entrenos.keys), 12, 3, { "Series de ${it.replace('_', ' ')}" })
        familia("act_", "Cuerpo", Repo.actividad.mapValues { (_, a) -> a.minutos.filterKeys { it !in setOf("a_pie", "vehiculo", "quieto") }.mapValues { it.value.toDouble() } },
            Repo.actividad.keys, 6, 3, { "Minutos ${it.replace('_', ' ')}" })

        // ---- Nutrición: todos los nutrientes de MacroFactor que no son ya variables fijas (vitaminas, minerales…), y los alimentos más frecuentes.
        val fijas = setOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "cafeina_mg", "azucares_g", "gasto_kcal", "peso_tendencia_kg")
        // Pasos y peso de MacroFactor repiten los del teléfono, y los objetivos los pone la app, no tú: fuera.
        familia("nut_", "Nutrición", Repo.nutricion.mapValues { (_, n) -> n.valores.filterKeys { it !in fijas && it != "pasos_mf" && it != "peso_mf_kg" && !it.startsWith("objetivo") && !it.startsWith("target") } }, Repo.nutricion.keys, 60, 14, { nutriente(it) })
        runCatching {
            val desde = Repo.nutricion.keys.minOrNull() ?: return@runCatching
            val ali = HashMap<String, HashMap<String, Double>>()
            Repo.comidas(desde, "9999-12-31").forEach { c -> ali.getOrPut(c.fecha) { HashMap() }[c.alimento.trim()] = 1.0 }
            familia("ali_", "Nutrición", ali, Repo.nutricion.keys, 12, 5, { "Comí $it (0/1)" })
        }

        // ---- Cuestionarios: cada puntaje vale para los días que pregunta (los 7 o los 30 anteriores a responder) o, en la personalidad,
        // hasta el test siguiente. Si dos respuestas cubren el mismo día, se queda la primera (la más cercana después de ese día).
        Escalas.TODAS.forEach { ins ->
            val resp = Repo.respuestas.filter { it.instrumento == ins.id }.sortedBy { it.fecha }
            if (resp.isEmpty()) return@forEach
            val ventana = ventanaCuestionario(ins.id)
            val claves = resp.flatMap { it.puntajes.keys }.distinct()
            claves.forEach { p ->
                val id = "esc_${ins.id}_${slug(p)}"
                if (id in usados) return@forEach
                usados += id
                vars += Variable(id, "${ins.nombre}: ${p.replace('_', ' ')}", "Cuestionarios")
                porDiaCuestionario(resp.mapNotNull { r -> r.puntajes[p]?.let { r.fecha to it } }, ventana, hoy).forEach { (f, v) -> pon(f, id, v) }
            }
        }

        // ---- Etapas de vida (de «Conocerte» y sus cambios): cada día sabe si vivías con tu pareja, si trabajabas, etc.
        val perfil = Repo.config.perfil
        if (!Perfiles.vacio(perfil)) {
            val actual = Perfiles.campos(perfil)
            val hist = Repo.config.perfil_historial.sortedBy { it.fecha }
            // Antes del primer guardado (si quedó en el historial) un campo que se llenó ese día no se sabía: sin dato (P-26).
            val primero = Perfiles.primerGuardado(hist)
            val desdePrimero = hist.filter { it.fecha == primero }.map { it.campo }.toSet()
            fun valorEn(campo: String, f: String): String? {
                if (primero != null && f < primero && campo in desdePrimero) return null
                return hist.lastOrNull { it.campo == campo && it.fecha <= f }?.despues ?: hist.firstOrNull { it.campo == campo && it.fecha > f }?.antes ?: actual[campo].orEmpty()
            }
            val etapas = listOf<Triple<String, String, (String) -> Double?>>(
                Triple("vida_en_pareja", "Etapa: en pareja (0/1)", { f: String -> valorEn("pareja", f)?.takeIf { it.isNotBlank() && it != "Prefiero no decir" }?.let { if (it in setOf("En pareja", "Casado o en unión", "Saliendo con alguien")) 1.0 else 0.0 } }),
                Triple("vida_vive_solo", "Etapa: vivía solo (0/1)", { f: String -> valorEn("vive con", f)?.takeIf { it.isNotBlank() }?.let { if ("solo" in it) 1.0 else 0.0 } }),
                Triple("vida_vive_con_pareja", "Etapa: vivía con mi pareja (0/1)", { f: String -> valorEn("vive con", f)?.takeIf { it.isNotBlank() }?.let { if ("pareja" in it) 1.0 else 0.0 } }),
                Triple("vida_vive_con_familia", "Etapa: vivía con familia (0/1)", { f: String -> valorEn("vive con", f)?.takeIf { it.isNotBlank() }?.let { v -> if (listOf("mamá", "hermanos", "hijos", "abuelos", "familiares").any { it in v }) 1.0 else 0.0 } }),
                Triple("vida_trabaja", "Etapa: trabajaba (0/1)", { f: String -> valorEn("a qué te dedicas", f)?.takeIf { it.isNotBlank() }?.let { if ("trabajo" in it || "por mi cuenta" in it) 1.0 else 0.0 } }),
                Triple("vida_estudia", "Etapa: estudiaba (0/1)", { f: String -> valorEn("a qué te dedicas", f)?.takeIf { it.isNotBlank() }?.let { if ("estudio" in it) 1.0 else 0.0 } }),
                Triple("vida_mascota", "Etapa: tenía mascota (0/1)", { f: String -> if (actual["mascotas"].isNullOrBlank() && hist.none { it.campo == "mascotas" }) null else valorEn("mascotas", f)?.let { if (it.isNotBlank()) 1.0 else 0.0 } }),
                Triple("vida_desde_casa", "Etapa: trabajaba o estudiaba desde casa, total o en parte (0/1)", { f: String -> valorEn("modalidad", f)?.takeIf { it.isNotBlank() }?.let { if (it == "Presencial") 0.0 else 1.0 } }),
                Triple("vida_horas_semana", "Etapa: horas a la semana de trabajo o escuela", { f: String -> valorEn("horas a la semana", f)?.toDoubleOrNull() }),
                Triple("vida_auto", "Etapa: me movía en auto o moto (0/1)", { f: String -> valorEn("transporte", f)?.takeIf { it.isNotBlank() }?.let { if (it == "Auto" || it == "Moto") 1.0 else 0.0 } }),
                Triple("vida_sin_carne", "Etapa: comía sin carne (vegetariana o vegana) (0/1)", { f: String -> valorEn("dieta", f)?.takeIf { it.isNotBlank() }?.let { if (it == "Vegetariana" || it == "Vegana") 1.0 else 0.0 } }),
                Triple("vida_hijos", "Etapa: número de hijos", { f: String -> valorEn("hijos", f)?.toDoubleOrNull() }),
                Triple("vida_idiomas", "Etapa: idiomas que aprendía", { f: String -> if (actual["idiomas que aprendes"].isNullOrBlank() && hist.none { it.campo == "idiomas que aprendes" }) null else valorEn("idiomas que aprendes", f)?.split(",")?.count { it.isNotBlank() }?.toDouble() }),
                Triple("vida_estudios", "Etapa: nivel de estudios (1 secundaria … 6 doctorado)", { f: String -> Perfiles.ESTUDIOS.indexOfFirst { it.nombre == valorEn("estudios", f) }.takeIf { it >= 0 }?.let { it + 1.0 } }),
            )
            val fechas = Repo.dias.keys
            etapas.forEach { (id, nombre, fn) ->
                var alguno = false
                fechas.forEach { f -> fn(f)?.let { pon(f, id, it); alguno = true } }
                if (alguno) { vars += Variable(id, nombre, "Etapas de vida"); usados += id }
            }
            // La ciudad donde vivías, si cambió.
            val ciudades = (hist.filter { it.campo == "ciudad" }.flatMap { listOf(it.antes, it.despues) } + listOfNotNull(actual["ciudad"])).filter { it.isNotBlank() }.distinct()
            if (ciudades.size >= 2) ciudades.forEach { c ->
                val id = "vida_ciudad_" + slug(c)
                if (id in usados) return@forEach
                usados += id; vars += Variable(id, "Etapa: vivía en $c (0/1)", "Etapas de vida")
                fechas.forEach { f -> valorEn("ciudad", f)?.let { v -> pon(f, id, if (v == c) 1.0 else 0.0) } }
            }
        }

        // ---- Brújula política: la posición de cada año vale hasta el test siguiente.
        val tests = Repo.config.politica.sortedBy { it.fecha }
        if (tests.isNotEmpty()) {
            Politica.EJES.forEach { e ->
                val id = "pol_${e.id}"
                vars += Variable(id, "Brújula: ${e.nombre.lowercase()} (−100 ${e.menos}, +100 ${e.mas})", "Política"); usados += id
                tests.forEachIndexed { i, t ->
                    val v = t.ejes[e.id] ?: return@forEachIndexed
                    val hasta = minOf(tests.getOrNull(i + 1)?.fecha ?: Fechas.sumar(t.fecha, 400), Fechas.sumar(hoy, 1))
                    var f = t.fecha; var g = 0
                    while (f < hasta && g < 1200) { pon(f, id, v); f = Fechas.sumar(f, 1); g++ }
                }
            }
        }
        return Resultado(vars, out)
    }

    /**
     * Fecha → puntaje de un cuestionario, a partir de sus respuestas (fecha, puntaje) de la más vieja a la más nueva. Con [ventana] > 0,
     * cada respuesta cubre ese día y los [ventana] − 1 anteriores, y un día cubierto por dos respuestas se queda con la primera.
     * Con 0, cada puntaje vale desde su fecha hasta el test siguiente (como mucho un año, y no más allá de hoy).
     */
    fun porDiaCuestionario(resp: List<Pair<String, Double>>, ventana: Long, hoy: String): Map<String, Double> {
        val m = HashMap<String, Double>()
        resp.forEachIndexed { i, (fecha, v) ->
            if (ventana > 0) for (k in 0 until ventana) m.putIfAbsent(Fechas.sumar(fecha, -k), v)
            else {
                val hasta = minOf(resp.getOrNull(i + 1)?.first ?: Fechas.sumar(fecha, 365), Fechas.sumar(hoy, 1))
                var f = fecha; var g = 0
                while (f < hasta && g < 800) { m[f] = v; f = Fechas.sumar(f, 1); g++ }
            }
        }
        return m
    }

    /** Días en que se pudo nombrar a alguien: con diario escrito o con un momento no privado que nombra a alguien con @ (P-15). */
    fun diasSociales(): Set<String> =
        Repo.dias.filterValues { it.nota.isNotBlank() }.keys + Repo.momentos.filter { !it.privado && Texto.personas(it.texto).isNotEmpty() }.map { it.fecha }

    /** Cuántos días cubre una respuesta (los que pregunta, igual a `frecuenciaDias`); 0 = vale hasta el test siguiente (personalidad). */
    fun ventanaCuestionario(instrumento: String): Long = when (instrumento) { "who5", "pss4" -> 7L; "ucla3" -> 30L; else -> 0L }

    /** Nombres en español de las columnas de MacroFactor que llegan en inglés (el orden importa: «isoleucine» antes que «leucine»). */
    private val NUTRIENTES_ES = listOf(
        "thiamin" to "Vitamina B1 (tiamina)", "riboflavin" to "Vitamina B2 (riboflavina)", "niacin" to "Vitamina B3 (niacina)",
        "pantothen" to "Vitamina B5 (ácido pantoténico)", "pyridox" to "Vitamina B6", "cobalamin" to "Vitamina B12", "b12" to "Vitamina B12",
        "b6" to "Vitamina B6", "b5" to "Vitamina B5", "b3" to "Vitamina B3", "b2" to "Vitamina B2", "b1" to "Vitamina B1", "folate" to "Folato",
        "folic" to "Ácido fólico", "biotin" to "Biotina", "vitamin_a" to "Vitamina A", "vitamin_c" to "Vitamina C", "vitamin_d" to "Vitamina D",
        "vitamin_e" to "Vitamina E", "vitamin_k" to "Vitamina K", "retinol" to "Retinol", "beta_carotene" to "Betacaroteno", "choline" to "Colina",
        "calcium" to "Calcio", "copper" to "Cobre", "iron" to "Hierro", "magnesium" to "Magnesio", "manganese" to "Manganeso", "phosphorus" to "Fósforo",
        "potassium" to "Potasio", "selenium" to "Selenio", "sodium" to "Sodio", "zinc" to "Zinc", "iodine" to "Yodo", "chromium" to "Cromo",
        "molybdenum" to "Molibdeno", "fluoride" to "Flúor", "chloride" to "Cloruro", "monounsaturated" to "Grasa monoinsaturada",
        "polyunsaturated" to "Grasa poliinsaturada", "trans" to "Grasa trans", "saturated" to "Grasa saturada", "omega_3" to "Omega 3",
        "omega_6" to "Omega 6", "cholesterol" to "Colesterol", "water" to "Agua", "sugars_added" to "Azúcares añadidos", "added_sugar" to "Azúcares añadidos",
        "sugar_alcohol" to "Polialcoholes", "starch" to "Almidón", "net_carb" to "Carbohidratos netos", "cysteine" to "Cisteína", "histidine" to "Histidina",
        "isoleucine" to "Isoleucina", "leucine" to "Leucina", "lysine" to "Lisina", "methionine" to "Metionina", "phenylalanine" to "Fenilalanina",
        "threonine" to "Treonina", "tryptophan" to "Triptófano", "tyrosine" to "Tirosina", "valine" to "Valina", "alanine" to "Alanina",
        "arginine" to "Arginina", "glycine" to "Glicina", "proline" to "Prolina", "serine" to "Serina", "glutamic" to "Ácido glutámico",
        "aspartic" to "Ácido aspártico", "caffeine" to "Cafeína", "alcohol" to "Alcohol", "fiber" to "Fibra", "sugars" to "Azúcares",
        "protein" to "Proteína", "carbs" to "Carbohidratos", "fat" to "Grasa", "calories" to "Calorías", "steps" to "Pasos (MacroFactor)",
        "weight" to "Peso (MacroFactor)", "expenditure" to "Gasto calórico",
        "azucares_anadidos" to "Azúcares añadidos", "grasa_saturada" to "Grasa saturada", "sodio" to "Sodio", "agua" to "Agua",
        "colesterol" to "Colesterol", "potasio" to "Potasio", "calcio" to "Calcio", "hierro" to "Hierro", "magnesio" to "Magnesio", "omega" to "Omega 3",
    )

    fun nutriente(k: String): String {
        val unidades = listOf("mcg", "mg", "kcal", "g", "iu", "ml", "kg", "ug")
        val partes = k.split('_')
        val u = partes.lastOrNull()?.takeIf { it in unidades }?.let { if (it == "ug") "mcg" else it }
        val base = (if (u != null) partes.dropLast(1) else partes).joinToString("_")
        val es = NUTRIENTES_ES.firstOrNull { (en, _) -> base == en || base.startsWith(en + "_") || base.contains("_" + en) || (en.length >= 5 && base.contains(en)) }?.second
        val n = es ?: base.replace('_', ' ').replaceFirstChar { it.uppercase() }
        val pre = if (base.startsWith("objetivo") || base.startsWith("target")) "Objetivo: " else ""
        return if (u != null) "$pre$n (${if (u == "iu") "UI" else u})" else "$pre$n"
    }
}
