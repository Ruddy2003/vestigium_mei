package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.esAnimo
import android.content.Context
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Macrofactor
import io.github.ruddy2003.vestigium.data.FraseGuardada
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Obra
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import java.time.temporal.ChronoUnit

/**
 * Todo lo de un periodo (una semana, un mes, un año) ya contado: lo usan los reportes y «Tu año».
 * Nunca toca lo privado (impulsos, detonantes, medicamentos) ni el texto de las cartas.
 */
object Periodo {
    class Datos(
        val ini: String, val fin: String, val fechas: List<String>,
        // ---- diario y ánimo
        val diasDiario: Int, val palabras: Int, val valencia: List<Double?>, val activacion: List<Double?>,
        val valMedia: Double?, val actMedia: Double?, val mejorDia: String?, val peorDia: String?, val mejorMomento: String?,
        val valPorMes: Map<Int, Double>, val valPorSemana: Map<Int, Double>,
        val suenosRecordados: Int, val suenoVivido: String?, val hitos: List<String>, val momentos: List<Pair<String, Int>>,
        val personas: List<Pair<String, Int>>, val temas: List<Pair<String, Int>>, val personaNueva: String?,
        val habitosPct: Double?, val habitos: List<Pair<String, Int>>, val rachaDiario: Int,
        // ---- cuerpo
        val suenoH: Double?, val suenoTotalH: Double, val dormirH: Double?, val despertarH: Double?,
        val nocheLarga: Pair<String, Int>?, val nocheCorta: Pair<String, Int>?, val suenoPorDia: List<Double?>,
        val pasos: Double?, val pasosTotal: Long, val km: Double, val ejercicioMin: Int,
        val entrenos: Int, val volumenKg: Double, val series: Int, val pr: Pair<String, Double>?,
        val pesoIni: Double?, val pesoFin: Double?, val fcReposo: Double?,
        val kcal: Double?, val proteina: Double?, val tazasCafe: Double, val comidaTop: List<Pair<String, Int>>,
        // ---- mente
        val reaccionMs: Double?, val reacciones: Int, val cuestionarios: List<String>,
        // ---- gente
        val waMios: Int, val waOtros: Int, val waTop: List<Pair<String, Int>>, val llamadas: Int, val cartas: Int,
        // ---- mundo
        val ciudades: List<Pair<String, String>>, val ciudadesNuevas: List<Pair<String, String>>, val paises: List<String>, val paisesNuevos: List<String>,
        val sitios: List<String>, val fotos: Int, val tempMax: Double?, val diasLluvia: Int, val luzH: Double?,
        // ---- cultura
        val obras: List<Obra>, val mejorObra: Obra?, val generoTop: String?, val frases: Int, val frase: FraseGuardada?,
        val musicaH: Double, val canciones: Int, val artistas: List<Pair<String, Double>>, val cancionTop: Pair<String, Int>?, val artistaNuevo: String?,
        val ytVideos: Int, val ytCanales: List<Pair<String, Int>>, val ytMadrugada: Int, val ytMegusta: Int, val subrayados: Int,
        // ---- dinero
        val gasto: Double, val gastoImprevisto: Double, val gastoCategorias: List<Pair<String, Double>>, val ingresos: Double,
        // ---- teléfono
        val pantallaMin: Double?, val pantallaTotalMin: Long, val desbloqueos: Double?, val desbloqueosTotal: Long, val notifs: Long,
        val apps: List<Pair<String, Long>>,
        // ---- comparación con el periodo anterior (misma duración)
        val valAnterior: Double?, val suenoAnterior: Double?, val pasosAnterior: Double?, val pantallaAnterior: Double?,
        /** Días con ánimo solo en los avisos (sin el del diario): no cuentan en [valMedia]. */
        val diasSoloAvisos: Int = 0,
        /** Error típico de cada diferencia con el periodo anterior ([errorDif]): «valencia», «sueno», «pasos», «pantalla». */
        val errorAnterior: Map<String, Double> = emptyMap(),
        // ---- lo que dicen los registros de ánimo (esquema 17): emociones con su intensidad, pensamientos y con quién
        /** Las emociones nombradas, de más a menos, con cuántas veces dijiste «mucho». «No sé» no cuenta; «otro» va con lo que escribiste. */
        val emociones: List<EmocionPeriodo> = emptyList(),
        /** En qué pensabas, de más a menos (un registro con varios cuenta en cada uno). */
        val pensamientos: List<Pair<String, Int>> = emptyList(),
        /** Con quién estabas: cuántos registros con cada compañía, y de cuántos registros con «con quién» respondido. */
        val conQuien: List<Pair<String, Int>> = emptyList(), val conQuienRegistros: Int = 0,
        /** Registros de ánimo del periodo (de un aviso o abiertos por ti). */
        val registrosAnimo: Int = 0,
    ) {
        val n: Int get() = fechas.size
    }

    /** Una emoción del periodo: su nombre, cuántos registros la nombran y en cuántos fue «mucho». */
    class EmocionPeriodo(val nombre: String, val n: Int, val mucho: Int)

    /**
     * Lo que dicen los registros de ánimo de un periodo, con la misma lógica de las pantallas: cada emoción cuenta una vez por
     * registro, «no sé» se deja fuera (salvo con [conNoSe], como en el reporte para terapia, que siempre lo contó), «otro» se
     * nombra con lo que escribiste y «mucho» es la intensidad 3. Solo informativo: no entra a ningún promedio ni al panel.
     */
    fun animoDe(registros: List<io.github.ruddy2003.vestigium.data.Momento>, k: Int = 6, conNoSe: Boolean = false): Triple<List<EmocionPeriodo>, List<Pair<String, Int>>, Pair<List<Pair<String, Int>>, Int>> {
        val reg = registros.filter { Catalogo.esRegistro(it) }
        val pares = reg.flatMap { m -> Catalogo.emocionesDe(m).distinct().filter { conNoSe || it != Catalogo.NO_SE }.map { id -> m to id } }
        val emociones = pares.groupBy { (m, id) -> if (id == Catalogo.OTRO) m.emocion_otra?.trim()?.lowercase()?.ifBlank { null } ?: "otro" else Catalogo.nombreSentimiento(id) }
            .map { (nombre, l) -> EmocionPeriodo(nombre.replaceFirstChar { it.uppercase() }, l.size, l.count { (m, id) -> m.intensidades[id] == 3 }) }
            .sortedWith(compareByDescending<EmocionPeriodo> { it.n }.thenBy { it.nombre }).take(k)
        val pens = top(reg.flatMap { Catalogo.pensamientosDe(it).distinct() }, k).map { Catalogo.nombrePensamiento(it.first) to it.second }
        val conL = reg.filter { it.con_quien.isNotEmpty() }
        val con = top(conL.flatMap { it.con_quien.distinct() }, 9).map { (q, n) -> (Catalogo.CON_QUIEN.firstOrNull { it.id == q }?.nombre?.lowercase() ?: q) to n }
        return Triple(emociones, pens, con to conL.size)
    }

    private fun <T> top(l: List<T>, k: Int = 6): List<Pair<T, Int>> = l.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(k).map { it.key to it.value }

    /** Hora decimal de un ISO, la misma del panel y de R ([Exportar.horaDecimal]: de noche, antes de las 12 suma 24) (P-29). */
    private fun horaDec(iso: String?, noche: Boolean): Double? = iso?.let { io.github.ruddy2003.vestigium.data.Exportar.horaDecimal(it, noche) }

    /** La valencia del día: la del diario, como `valencia` en el panel y en R (P-29). Los días con solo avisos no cuentan aquí. */
    private fun valDe(f: String): Double? = Repo.dias[f]?.valencia?.toDouble()

    /** Días con valencia en los avisos pero sin la del diario: no entran en el ánimo medio, y el reporte dice cuántos son. */
    private fun soloAvisos(f: String): Boolean = Repo.dias[f]?.valencia == null && Repo.momentos.any { it.fecha == f && it.esAnimo && it.valencia != null }

    /**
     * Error típico de la diferencia de dos promedios de días, √(s₁²/n₁ + s₂²/n₂) (H-8-11): una diferencia menor no se distingue
     * de lo que varían tus días. No descuenta que los días seguidos se parecen, así que se queda corto. Null con menos de 3 días.
     */
    fun errorDif(a: List<Double>, b: List<Double>): Double? {
        if (a.size < 3 || b.size < 3) return null
        fun v(x: List<Double>) = x.average().let { m -> x.sumOf { (it - m) * (it - m) } / (x.size - 1) }
        return kotlin.math.sqrt(v(a) / a.size + v(b) / b.size)
    }

    fun calcular(ctx: Context?, ini: String, fin: String): Datos {
        val n = (ChronoUnit.DAYS.between(Fechas.parse(ini), Fechas.parse(fin)).toInt() + 1).coerceAtLeast(1)
        val fechas = (0 until n).map { Fechas.sumar(ini, it.toLong()) }
        val en = fechas.toHashSet()
        val dias = fechas.mapNotNull { Repo.dias[it] }
        val mu = Repo.momentos.filter { it.fecha in en && !it.privado }
        val eq = Repo.equivalencias()

        // ---- ánimo
        val vals = fechas.map { valDe(it) }
        val acts = fechas.map { f -> Repo.dias[f]?.activacion?.toDouble() ?: mu.filter { it.fecha == f && it.esAnimo }.mapNotNull { it.activacion?.toDouble() }.takeIf { it.isNotEmpty() }?.average() }
        val conVal = fechas.zip(vals).filter { it.second != null }
        val mejor = conVal.maxByOrNull { it.second!! }?.first
        val peor = conVal.minByOrNull { it.second!! }?.first
        val porMes = conVal.groupBy { it.first.substring(5, 7).toInt() }.mapValues { (_, l) -> l.map { it.second!! }.average() }
        val porSem = conVal.groupBy { Fechas.parse(it.first).dayOfWeek.value }.mapValues { (_, l) -> l.map { it.second!! }.average() }
        val personasL = dias.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct() } + mu.flatMap { Texto.personas(it.texto).map { p -> Texto.canon(p, eq) } }
        val antes = Repo.dias.values.filter { it.fecha < ini }.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) } }.toHashSet()
        val nueva = dias.sortedBy { it.fecha }.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) } }.firstOrNull { it !in antes && Repo.dias.isNotEmpty() && Repo.dias.keys.min() < ini }
        val temasL = dias.flatMap { d -> Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct() } + mu.flatMap { Texto.temas(it.texto).map { t -> Texto.canon(t, eq) } }
        val activos = io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().filter { it.activo }
        val habPct = if (activos.isEmpty() || dias.isEmpty()) null else dias.sumOf { d -> activos.count { d.habitos[it.id] == true } }.toDouble() / (activos.size * dias.size)
        val habitos = activos.map { h -> h.nombre to dias.count { it.habitos[h.id] == true } }.filter { it.second > 0 }.sortedByDescending { it.second }
        var racha = 0; var r = 0
        fechas.forEach { f -> val d = Repo.dias[f]; if (d != null && (d.nota.isNotBlank() || d.valencia != null)) { r++; racha = maxOf(racha, r) } else r = 0 }
        val vividos = dias.filter { it.sueno_recordado }
        val vivido = vividos.maxByOrNull { (if (it.sueno_vividez == "muy_vivido" || it.sueno_vividez == "vivido") 1000 else 0) + Texto.palabras(it.sueno_texto) }?.fecha

        // ---- cuerpo
        val sal = fechas.mapNotNull { Repo.salud[it] }
        val sue = sal.filter { (it.sueno_min ?: 0) > 120 }
        val suenoPorDia = fechas.map { f -> Repo.salud[f]?.sueno_min?.takeIf { it > 120 }?.div(60.0) }
        val pasosL = sal.mapNotNull { it.pasos?.toDouble() }.filter { it > 0 }
        val km = sal.sumOf { it.distancia_km ?: 0.0 }.takeIf { it > 0 } ?: pasosL.sum() * 0.00075
        val ses = Repo.sesiones.filter { it.fecha in en }
        val ent = fechas.mapNotNull { Repo.entrenos[it] }.filter { it.series > 0 }
        val series = runCatching { Repo.series(ini, fin) }.getOrDefault(emptyList())
        // La misma 1RM estimada que la exportación, Fuerza y R: Epley con 1 a 12 repeticiones, sin calentamientos (P-22).
        val pr = series.mapNotNull { s -> Macrofactor.e1rm(s)?.let { s.ejercicio to it } }.maxByOrNull { it.second }
        val pesos = fechas.mapNotNull { f -> (Repo.salud[f]?.peso_kg ?: Repo.nutricion[f]?.valores?.get("peso_tendencia_kg"))?.let { f to it } }
        val nut = fechas.mapNotNull { Repo.nutricion[it] }.filter { (it.valores["kcal"] ?: 0.0) > 300 }
        val comidas = runCatching { Repo.comidas(ini, fin) }.getOrDefault(emptyList())

        // ---- mente
        val reac = Repo.reacciones.filter { it.fecha in en }
        val cuest = Repo.respuestas.filter { it.fecha in en }.map { rr ->
            val inst = Escalas.por(rr.instrumento)
            (inst?.nombre ?: rr.instrumento) + ": " + rr.puntajes.entries.joinToString(" · ") { "${it.key} ${"%.0f".format(it.value)}" }
        }

        // ---- gente
        val chats = Repo.chats.filter { it.fecha in en }
        val com = fechas.mapNotNull { Repo.comunicacion[it] }

        // ---- mundo
        val mundo = ctx?.let { c -> runCatching { Mundo.diario(c) }.getOrNull() }
        val dm = fechas.mapNotNull { f -> mundo?.get(Fechas.parse(f).toEpochDay().toInt()) }
        val ciudades = dm.flatMap { d -> d.ciudades.map { it.first } }.groupBy { it.clave }.values.map { l -> l.first() to l.size }.sortedByDescending { it.second }.map { it.first.nombre to it.first.pais }
        val ciuNuevas = dm.flatMap { it.ciudadesNuevas }.distinctBy { it.clave }.map { it.nombre to it.pais }
        val paises = dm.flatMap { d -> d.paises.map { it.first.pais } }.distinct()
        val paisesNuevos = dm.flatMap { it.paisesNuevos }.map { it.pais }.distinct()
        val sitios = dm.flatMap { it.sitios }.distinctBy { it.i }.map { it.n }
        val entorno = fechas.mapNotNull { Repo.entorno[it] }

        // ---- cultura
        val obras = Repo.obras.filter { it.estado == "terminado" && it.fin != null && it.fin in en }
        val mejorObra = obras.filter { it.calificacion != null }.maxByOrNull { it.calificacion!! }
        val genero = obras.flatMap { it.etiquetas }.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        val frases = Repo.config.frases_guardadas.filter { it.fecha in en }
        val esc = runCatching { Repo.escuchas(ini, fin) }.getOrDefault(emptyList())
        fun minutos(e: io.github.ruddy2003.vestigium.data.Escucha) = (e.ms?.toDouble() ?: e.duracion_s?.times(1000.0) ?: 180_000.0) / 60000
        val artistas = esc.filter { it.artista.isNotBlank() }.groupBy { it.artista }.mapValues { (_, l) -> l.sumOf { minutos(it) } }.entries.sortedByDescending { it.value }.take(8).map { it.key to it.value }
        val cancion = esc.filter { it.titulo.isNotBlank() && (it.ms ?: 30_000) >= 30_000 }.groupingBy { it.titulo + if (it.artista.isNotBlank()) " — ${it.artista}" else "" }.eachCount().maxByOrNull { it.value }?.toPair()
        val previos = runCatching { Repo.escuchas("0000-01-01", Fechas.sumar(ini, -1)).map { it.artista }.toHashSet() }.getOrDefault(hashSetOf())
        val artistaNuevo = if (previos.isEmpty()) null else artistas.firstOrNull { it.first !in previos }?.first
        val yt = runCatching { Repo.huellasDe(ini, fin) }.getOrDefault(emptyList()).filter { it.producto == "youtube" && it.accion == "visto" }
        val ytCanales = yt.filter { it.canal.isNotBlank() }.groupingBy { it.canal }.eachCount().entries.sortedByDescending { it.value }.take(5).map { it.key to it.value }
        val madrugada = yt.count { v -> v.ts.substring(11, 13).toIntOrNull()?.let { it < 5 } == true }

        // ---- dinero
        val gastos = Repo.gastos.filter { it.dia in en }
        val cats = gastos.groupBy { it.categoria }.mapValues { (_, l) -> l.sumOf { it.monto } }.entries.sortedByDescending { it.value }.map { it.key to it.value }

        // ---- teléfono
        val pant = fechas.mapNotNull { Repo.pantalla[it] }.filter { it.total_min > 0 }
        val apps = HashMap<String, Long>(); pant.forEach { p -> p.por_app_min.forEach { (a, m) -> apps.merge(a, m.toLong(), Long::plus) } }
        val ntf = fechas.mapNotNull { Repo.notifs[it] }

        // ---- el periodo anterior de la misma duración
        val pIni = Fechas.sumar(ini, -n.toLong()); val pFin = Fechas.sumar(ini, -1)
        val pf = (0 until n).map { Fechas.sumar(pIni, it.toLong()) }
        val pv = pf.mapNotNull { valDe(it) }
        val ps = pf.mapNotNull { Repo.salud[it]?.sueno_min?.takeIf { m -> m > 120 }?.div(60.0) }
        val pp = pf.mapNotNull { Repo.salud[it]?.pasos?.toDouble()?.takeIf { x -> x > 0 } }
        val ppa = pf.mapNotNull { Repo.pantalla[it]?.total_min?.toDouble()?.takeIf { x -> x > 0 } }

        val animo = animoDe(mu)
        return Datos(
            ini, fin, fechas,
            diasDiario = dias.count { it.nota.isNotBlank() || it.valencia != null }, palabras = dias.sumOf { Texto.palabras(it.nota) },
            valencia = vals, activacion = acts, valMedia = vals.filterNotNull().takeIf { it.isNotEmpty() }?.average(),
            actMedia = acts.filterNotNull().takeIf { it.isNotEmpty() }?.average(), mejorDia = mejor, peorDia = peor,
            mejorMomento = mejor?.let { Repo.dias[it]?.mejor_momento?.takeIf { m -> m.isNotBlank() } },
            valPorMes = porMes, valPorSemana = porSem,
            suenosRecordados = vividos.size, suenoVivido = vivido, hitos = dias.flatMap { it.hitos }.distinct(),
            emociones = animo.first, pensamientos = animo.second, conQuien = animo.third.first, conQuienRegistros = animo.third.second,
            registrosAnimo = mu.count { Catalogo.esRegistro(it) },
            momentos = mu.filter { it.tipo != "muestreo" }.groupingBy { it.tipo }.eachCount().entries.sortedByDescending { it.value }.map { Catalogo.nombreTipo(it.key) to it.value },
            personas = top(personasL), temas = top(temasL), personaNueva = nueva,
            habitosPct = habPct, habitos = habitos, rachaDiario = racha,
            suenoH = sue.mapNotNull { it.sueno_min }.takeIf { it.isNotEmpty() }?.average()?.div(60), suenoTotalH = sue.sumOf { it.sueno_min ?: 0 } / 60.0,
            dormirH = sue.mapNotNull { horaDec(it.sueno_inicio, true) }.takeIf { it.isNotEmpty() }?.average(),
            despertarH = sue.mapNotNull { horaDec(it.sueno_fin, false) }.takeIf { it.isNotEmpty() }?.average(),
            nocheLarga = sue.maxByOrNull { it.sueno_min!! }?.let { it.fecha to it.sueno_min!! }, nocheCorta = sue.minByOrNull { it.sueno_min!! }?.let { it.fecha to it.sueno_min!! },
            suenoPorDia = suenoPorDia,
            pasos = pasosL.takeIf { it.isNotEmpty() }?.average(), pasosTotal = pasosL.sum().toLong(), km = km,
            ejercicioMin = ses.sumOf { it.min }, entrenos = ent.size, volumenKg = ent.sumOf { it.volumen_kg }, series = ent.sumOf { it.series }, pr = pr,
            pesoIni = pesos.firstOrNull()?.second, pesoFin = pesos.lastOrNull()?.second, fcReposo = sal.mapNotNull { it.fc_reposo }.takeIf { it.isNotEmpty() }?.average(),
            kcal = nut.mapNotNull { it.valores["kcal"] }.takeIf { it.isNotEmpty() }?.average(), proteina = nut.mapNotNull { it.valores["proteina_g"] }.takeIf { it.isNotEmpty() }?.average(),
            tazasCafe = fechas.sumOf { Repo.nutricion[it]?.valores?.get("cafeina_mg") ?: 0.0 } / 95.0,
            comidaTop = comidas.groupingBy { it.alimento.trim() }.eachCount().entries.sortedByDescending { it.value }.take(5).map { it.key to it.value },
            reaccionMs = reac.mapNotNull { it.mediana_ms }.takeIf { it.isNotEmpty() }?.average(), reacciones = reac.size, cuestionarios = cuest,
            waMios = chats.sumOf { it.mios }, waOtros = chats.sumOf { it.otros },
            waTop = chats.groupBy { it.chat }.mapValues { (_, l) -> l.sumOf { it.mios + it.otros } }.entries.sortedByDescending { it.value }.take(5).map { it.key to it.value },
            llamadas = com.sumOf { it.entrantes + it.salientes }, cartas = Repo.cartas.count { it.escrita in en },
            ciudades = ciudades, ciudadesNuevas = ciuNuevas, paises = paises, paisesNuevos = paisesNuevos, sitios = sitios,
            fotos = fechas.sumOf { Repo.fotos[it]?.fotos ?: 0 }, tempMax = entorno.mapNotNull { it.t_max }.takeIf { it.isNotEmpty() }?.average(),
            diasLluvia = entorno.count { (it.precip_mm ?: 0.0) >= 1.0 }, luzH = entorno.mapNotNull { it.luz_min?.toDouble() }.takeIf { it.isNotEmpty() }?.average()?.div(60),
            obras = obras, mejorObra = mejorObra, generoTop = genero, frases = frases.size, frase = frases.lastOrNull(),
            musicaH = esc.sumOf { minutos(it) } / 60, canciones = esc.count { (it.ms ?: 30_000) >= 30_000 }, artistas = artistas, cancionTop = cancion, artistaNuevo = artistaNuevo,
            ytVideos = yt.size, ytCanales = ytCanales, ytMadrugada = madrugada,
            ytMegusta = Repo.youtube.count { it.tipo == "megusta" && it.fecha in en }, subrayados = Repo.subrayados.count { it.fecha in en },
            gasto = gastos.sumOf { it.monto }, gastoImprevisto = gastos.filter { it.imprevisto }.sumOf { it.monto }, gastoCategorias = cats,
            // Por el día del diario, como los gastos: un ingreso de madrugada cuenta para el día anterior (P-29).
            ingresos = Repo.ingresos.filter { it.dia in en }.sumOf { it.monto },
            pantallaMin = pant.map { it.total_min.toDouble() }.takeIf { it.isNotEmpty() }?.average(), pantallaTotalMin = pant.sumOf { it.total_min.toLong() },
            desbloqueos = pant.map { it.desbloqueos.toDouble() }.takeIf { it.isNotEmpty() }?.average(), desbloqueosTotal = pant.sumOf { it.desbloqueos.toLong() },
            notifs = ntf.sumOf { it.total.toLong() }, apps = apps.entries.sortedByDescending { it.value }.take(6).map { it.key to it.value },
            valAnterior = pv.takeIf { it.size >= 3 }?.average(), suenoAnterior = ps.takeIf { it.size >= 3 }?.average(),
            pasosAnterior = pp.takeIf { it.size >= 3 }?.average(), pantallaAnterior = ppa.takeIf { it.size >= 3 }?.average(),
            diasSoloAvisos = fechas.count { soloAvisos(it) },
            errorAnterior = listOfNotNull(
                errorDif(vals.filterNotNull(), pv)?.let { "valencia" to it },
                errorDif(sue.mapNotNull { it.sueno_min?.div(60.0) }, ps)?.let { "sueno" to it },
                errorDif(pasosL, pp)?.let { "pasos" to it },
                errorDif(pant.map { it.total_min.toDouble() }, ppa)?.let { "pantalla" to it },
            ).toMap(),
        )
    }
}
