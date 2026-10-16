package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import kotlin.math.abs
import kotlin.math.atanh
import kotlin.math.sqrt
import kotlin.math.tanh

data class Variable(val id: String, val nombre: String, val grupo: String)
data class Par(val x: Double, val y: Double, val fecha: String)
data class Correlacion(val a: Variable, val b: Variable, val r: Double, val n: Int, val ic: Pair<Double, Double>?)

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

    /** Diferencia de medias con intervalo aproximado de 95% (Welch). */
    fun diferencia(a: List<Double>, b: List<Double>): Triple<Double, Double, Double>? {
        if (a.size < 3 || b.size < 3) return null
        val ma = a.average(); val mb = b.average()
        val va = a.sumOf { (it - ma) * (it - ma) } / (a.size - 1)
        val vb = b.sumOf { (it - mb) * (it - mb) } / (b.size - 1)
        val se = sqrt(va / a.size + vb / b.size)
        val d = ma - mb
        return Triple(d, d - 1.96 * se, d + 1.96 * se)
    }

    // ---------- Variables diarias para cruzar ----------
    fun variables(): List<Variable> {
        val v = mutableListOf(
            Variable("valencia", "Valencia del día", "Ánimo"), Variable("activacion", "Activación del día", "Ánimo"),
            Variable("muestreo_valencia", "Valencia promedio en avisos", "Ánimo"), Variable("muestreo_activacion", "Activación promedio en avisos", "Ánimo"),
            Variable("dolor", "Dolor físico", "Cuerpo"), Variable("sueno_calidad", "Calidad de sueño percibida", "Sueño"),
            Variable("sueno_horas", "Horas de sueño (reloj)", "Sueño"), Variable("sueno_profundo", "Minutos de sueño profundo", "Sueño"),
            Variable("sueno_rem", "Minutos de sueño REM", "Sueño"), Variable("sueno_recordado", "Recordé un sueño (0/1)", "Sueño"),
            Variable("sueno_valencia", "Valencia del sueño", "Sueño"),
            Variable("hrv", "Variabilidad cardiaca (HRV)", "Cuerpo"), Variable("fc_reposo", "Frecuencia cardiaca en reposo", "Cuerpo"),
            Variable("pasos", "Pasos", "Cuerpo"), Variable("pantalla_min", "Minutos de pantalla", "Teléfono"),
            Variable("desbloqueos", "Desbloqueos del teléfono", "Teléfono"), Variable("gasto_total", "Gasto del día", "Dinero"),
            Variable("gasto_imprevisto", "Hubo gasto imprevisto (0/1)", "Dinero"), Variable("palabras", "Palabras en el diario", "Texto"),
            Variable("personas", "Personas distintas mencionadas", "Social"), Variable("hitos_n", "Número de hitos", "Texto"),
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
            Variable("yt_vistos", "Videos de YouTube vistos", "Teléfono"), Variable("busquedas", "Búsquedas en Google", "Teléfono"),
            Variable("subrayados", "Subrayados de Kindle", "Lectura"), Variable("ruido_db", "Ruido medio al registrar (dB)", "Entorno"),
            Variable("sueno_inicio_h", "Hora de dormir (reloj)", "Sueño"), Variable("sueno_fin_h", "Hora de despertar (reloj)", "Sueño"),
        )
        if (Repo.config.seguimiento_privado) {
            v += Variable("impulso", "Intensidad del impulso", "Privado"); v += Variable("recaida", "Recaída (0/1)", "Privado")
        }
        Catalogo.CONTEXTOS.forEach { v += Variable("ctx_${it.id}", "Contexto: ${it.nombre} (0/1)", "Contexto") }
        Catalogo.CIENTIFICAS.forEach { p -> p.opciones.filter { it.id != "no_aplica" }.forEach { o -> v += Variable("${p.clave}_${o.id}", "${o.nombre} (0/1)", "Lectura del día") } }
        Catalogo.MOMENTOS.forEach { v += Variable("n_${it.id}", "Momentos de ${it.nombre.lowercase()}", "Momentos") }
        Repo.config.habitos.forEach { v += Variable("hab_${it.id}", "Hábito: ${it.nombre} (0/1)", "Hábitos") }
        return v
    }

    /** Una fila por día con todas las variables numéricas. Lo que no existe queda en null. */
    fun panel(): Map<String, Map<String, Double?>> {
        val eq = Repo.equivalencias()
        val filas = sortedMapOf<String, MutableMap<String, Double?>>()
        fun fila(f: String) = filas.getOrPut(f) { mutableMapOf() }
        val b = { x: Boolean -> if (x) 1.0 else 0.0 }

        for ((f, d) in Repo.dias) {
            val r = fila(f)
            r["valencia"] = d.valencia?.toDouble(); r["activacion"] = d.activacion?.toDouble(); r["dolor"] = d.dolor?.toDouble()
            r["sueno_calidad"] = d.sueno_calidad?.toDouble(); r["sueno_recordado"] = b(d.sueno_recordado)
            r["sueno_valencia"] = if (d.sueno_recordado) d.sueno_valencia?.toDouble() else null
            r["palabras"] = Texto.palabras(d.nota).toDouble()
            r["personas"] = Texto.personas(d.nota).map { Texto.canon(it, eq) }.toSet().size.toDouble()
            r["hitos_n"] = d.hitos.size.toDouble()
            r["impulso"] = d.impulso?.toDouble(); r["recaida"] = d.recaida?.let(b)
            Catalogo.CONTEXTOS.forEach { r["ctx_${it.id}"] = b(d.ctx[it.id] == true) }
            Catalogo.CIENTIFICAS.forEach { p ->
                val valor = when (p.clave) { "flow" -> d.flow; "sdt_necesidad" -> d.sdt_necesidad; else -> d.apreciacion }
                p.opciones.filter { it.id != "no_aplica" }.forEach { o -> r["${p.clave}_${o.id}"] = valor?.let { b(it == o.id) } }
            }
            Repo.config.habitos.forEach { r["hab_${it.id}"] = b(d.habitos[it.id] == true) }
            r["gasto_total"] = 0.0; r["gasto_imprevisto"] = 0.0
            Catalogo.MOMENTOS.forEach { r["n_${it.id}"] = 0.0 }
        }
        Repo.momentos.groupBy { it.fecha }.forEach { (f, lista) ->
            val r = fila(f)
            val mu = lista.filter { it.tipo == "muestreo" }
            r["muestreo_valencia"] = media(mu.mapNotNull { it.valencia?.toDouble() })
            r["muestreo_activacion"] = media(mu.mapNotNull { it.activacion?.toDouble() })
            Catalogo.MOMENTOS.forEach { t -> r["n_${t.id}"] = lista.count { it.tipo == t.id }.toDouble() }
        }
        Repo.gastos.groupBy { it.fecha }.forEach { (f, lista) ->
            val r = fila(f); r["gasto_total"] = lista.sumOf { it.monto }; r["gasto_imprevisto"] = b(lista.any { it.imprevisto })
        }
        Repo.salud.forEach { (f, s) ->
            val r = fila(f)
            r["sueno_horas"] = s.sueno_min?.let { it / 60.0 }; r["sueno_profundo"] = s.sueno_profundo_min?.toDouble()
            r["sueno_rem"] = s.sueno_rem_min?.toDouble(); r["hrv"] = s.hrv_rmssd; r["fc_reposo"] = s.fc_reposo; r["pasos"] = s.pasos?.toDouble()
        }
        Repo.pantalla.forEach { (f, p) -> val r = fila(f); r["pantalla_min"] = p.total_min.toDouble(); r["desbloqueos"] = p.desbloqueos.toDouble() }
        Repo.reacciones.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["rt_media"] = media(l.mapNotNull { it.media_ms }) }
        Repo.visitas.groupBy { it.entrada.take(10) }.forEach { (f, l) -> fila(f)["lugares_distintos"] = l.map { it.lugar }.toSet().size.toDouble() }
        Movilidad.kmPorDia().forEach { (f, km) -> fila(f)["distancia_km"] = km }
        Repo.entorno.forEach { (f, e) ->
            val r = fila(f)
            r["t_max"] = e.t_max; r["t_min"] = e.t_min; r["precip_mm"] = e.precip_mm; r["nubes_pct"] = e.nubes_pct
            r["luz_horas"] = e.luz_min?.let { it / 60.0 }; r["sol_horas"] = e.sol_min?.let { it / 60.0 }; r["presion_hpa"] = e.presion_hpa
            r["aqi_us"] = e.aqi_us?.toDouble(); r["pm25"] = e.pm25
        }
        filas.keys.toList().forEach { f -> fila(f)["luna_ilum"] = io.github.ruddy2003.vestigium.data.Luna.iluminacion(io.github.ruddy2003.vestigium.data.Luna.fase(f)) }
        Repo.fotos.forEach { (f, x) -> fila(f)["fotos"] = x.fotos.toDouble() }
        Repo.notifs.forEach { (f, x) -> fila(f)["notificaciones"] = x.total.toDouble() }
        Repo.agenda.forEach { (f, x) -> val r = fila(f); r["eventos"] = x.eventos.toDouble(); r["min_ocupado"] = x.min_ocupado.toDouble() }
        Repo.actividad.forEach { (f, x) -> val r = fila(f); r["a_pie_min"] = (x.minutos["a_pie"] ?: 0).toDouble(); r["vehiculo_min"] = (x.minutos["vehiculo"] ?: 0).toDouble() }
        Repo.sesiones.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["ejercicio_min"] = l.sumOf { it.min }.toDouble() }
        Repo.pantalla.forEach { (f, p) ->
            val r = fila(f)
            fun h(iso: String?): Double? = iso?.let { val t = java.time.OffsetDateTime.parse(it).atZoneSameInstant(java.time.ZoneId.systemDefault()); t.hour + t.minute / 60.0 }
            r["primer_uso_h"] = runCatching { h(p.primer_uso) }.getOrNull()
            r["ultimo_uso_h"] = runCatching { h(p.ultimo_uso)?.let { if (it < 5) it + 24 else it } }.getOrNull()
        }
        val diasEsc = Repo.ascensos.groupBy { it.fecha }
        for (f in Repo.dias.keys) { fila(f)["escalada"] = if (diasEsc.containsKey(f)) 1.0 else 0.0 }
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
        Repo.huellas.forEach { (f, h) -> val r = fila(f); r["yt_vistos"] = h.yt_vistos.toDouble(); r["busquedas"] = (h.busquedas + h.yt_busquedas).toDouble() }
        Repo.subrayados.filter { it.fecha != null }.groupBy { it.fecha!! }.forEach { (f, l) -> fila(f)["subrayados"] = l.size.toDouble() }
        Repo.momentos.filter { it.ruido_db != null }.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["ruido_db"] = l.mapNotNull { it.ruido_db }.average() }
        runCatching {
            val desde = filas.keys.firstOrNull() ?: return@runCatching
            Repo.escuchas(desde, "9999-12-31").groupBy { it.fecha }.forEach { (f, l) -> fila(f)["canciones"] = l.size.toDouble() }
        }
        return filas
    }

    fun pares(panel: Map<String, Map<String, Double?>>, x: String, y: String, desfase: Boolean): List<Par> {
        val out = ArrayList<Par>()
        for ((f, r) in panel) {
            val ry = if (desfase) panel[Fechas.sumar(f, 1)] else r
            val a = r[x] ?: continue
            val bb = ry?.get(y) ?: continue
            out.add(Par(a, bb, f))
        }
        return out
    }

    fun mejores(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, minN: Int = 14): Pair<List<Correlacion>, Int> {
        val res = ArrayList<Correlacion>()
        for (i in vars.indices) for (j in i + 1 until vars.size) {
            val a = vars[i]; val b = vars[j]
            if (a.grupo == b.grupo && (a.grupo == "Lectura del día" || a.grupo == "Momentos")) continue
            val ps = pares(panel, a.id, b.id, false)
            if (ps.size < minN) continue
            val r = pearson(ps.map { it.x }, ps.map { it.y }) ?: continue
            res.add(Correlacion(a, b, r, ps.size, ic95(r, ps.size)))
        }
        return res.sortedByDescending { abs(it.r) } to res.size
    }
}

object Movilidad {
    /** Suma de distancias entre muestras consecutivas del mismo día, ignorando saltos imposibles. */
    fun kmDe(fecha: String): Double? {
        val a = Fechas.inicioDia(fecha) + 5 * 3_600_000L
        return kmPorDia(Repo.db().ubicaciones(a, a + 86_400_000L))[fecha]
    }

    fun kmPorDia(puntos: List<io.github.ruddy2003.vestigium.data.Db.Punto> = Repo.db().ubicaciones()): Map<String, Double> {
        val out = HashMap<String, Double>()
        var prev: io.github.ruddy2003.vestigium.data.Db.Punto? = null
        for (p in puntos) {
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
