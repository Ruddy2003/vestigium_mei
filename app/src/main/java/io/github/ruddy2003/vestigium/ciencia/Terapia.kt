package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Exportar
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.data.privado
import kotlin.math.sqrt

/**
 * El reporte para terapia: los últimos 7 o 14 días, solo con lo que sirve para hablar de la semana con quien te atiende.
 * Describe, no interpreta: no trae correlaciones, valores p ni experimentos. Nunca lleva el diario, los sueños escritos,
 * las cartas, las notas, la agenda, los chats, los lugares ni nombres (los @nombres de los textos salen como iniciales).
 *
 * Lo privado (impulsos, recaídas, detonantes y medicamentos) solo se lee aquí con [Opciones.privado], una casilla que se
 * marca al generar cada reporte y no se guarda. Es el único reporte de la app que puede llevarlo.
 */
object Terapia {
    /**
     * Lo que se elige al generar el reporte: el periodo y qué secciones salen. Nada se guarda: cada vez empieza con estos
     * valores (todo encendido, salvo lo mejor y lo peor de cada día y lo privado). La cobertura de datos sale siempre.
     */
    data class Opciones(
        val dias: Int = 7,
        val fin: String = Fechas.sumar(Fechas.diaLogico(), -1),
        val animo: Boolean = true,
        val cuestionarios: Boolean = true,
        val sueno: Boolean = true,
        /** Actividad, rutina y contacto con otros. */
        val rutina: Boolean = true,
        /** Gimnasio y alimentación. */
        val cuerpo: Boolean = true,
        val sustancias: Boolean = true,
        /** Lo mejor del mejor día y lo peor del peor día. */
        val momentosPeriodo: Boolean = true,
        /** Lo mejor y lo peor de cada día (dentro de la sección de momentos). */
        val momentosDia: Boolean = false,
        val privado: Boolean = false,
    ) {
        val ini: String get() = Fechas.sumar(fin, -(dias - 1).toLong())
        /** Todas las secciones encendidas. */
        val todo: Boolean get() = animo && cuestionarios && sueno && rutina && cuerpo && sustancias && momentosPeriodo && momentosDia && privado
        /** Al menos una sección, además de la cobertura. */
        val alguna: Boolean get() = animo || cuestionarios || sueno || rutina || cuerpo || sustancias || momentosPeriodo || momentosDia || privado
        /** Enciende o apaga todas las secciones a la vez (el periodo no cambia). */
        fun conTodo(v: Boolean) = copy(animo = v, cuestionarios = v, sueno = v, rutina = v, cuerpo = v, sustancias = v, momentosPeriodo = v, momentosDia = v, privado = v)
    }

    /** Un día de la tabla. [impulso] y [recaida] solo se llenan con la casilla de lo privado. */
    class Fila(
        val fecha: String, val valencia: Int?, val activacion: Double?, val dificil: Boolean, val suenoH: Double?, val calidad: Int?,
        val kcal: Double?, val objetivoKcal: Double?, val gimnasioMin: Double?, val gimnasio: Boolean,
        val mejor: String, val peor: String, val impulso: Int?, val recaida: Boolean?,
    )

    /** Un valor contra su objetivo, día por día: la diferencia (valor − objetivo) de cada día con registro y objetivo. */
    class Objetivo(
        val diferencias: List<Pair<String, Double>>, val diferenciaMedia: Double, val porcentajeMedio: Double,
        val dentro: Int, val encima: Int, val debajo: Int, val valorMedio: Double, val objetivoMedio: Double,
    )

    class Nutricion(
        val diasConRegistro: Int, val diasSinRegistro: Int, val kcal: Objetivo?, val proteina: Objetivo?,
        val carbohidratos: Pair<Double, Double?>?, val grasa: Pair<Double, Double?>?,
        /** Nombre, promedio diario y unidad. */
        val nutrientes: List<Triple<String, Double, String>>,
        val cafeinaUltima: Pair<Double, Double?>?, val comidasPorDia: Double?, val primera: Pair<Double, Double?>?, val ultima: Pair<Double, Double?>?,
    )

    class Cuestionario(val nombre: String, val enPeriodo: List<Pair<String, String>>, val lectura: String?, val anteriores: List<Pair<String, String>>)

    class Sustancia(val nombre: String, val unidad: String, val dias: Int, val cantidad: Double?, val nivelMax: Int?, val crudaMedia: Double?, val sintomas: List<String>)

    class Privados(
        val diasConRecaida: Int, val diasRespondidos: Int, val impulsos: Int, val superados: Int, val impulsoMedio: Double?,
        val detonantes: List<Pair<String, Int>>, val medicamentos: List<Pair<String, Int>>,
    )

    class Datos(
        val op: Opciones, val fechas: List<String>, val filas: List<Fila>,
        // ---- cobertura
        val diasDiario: Int, val avisosPlaneados: Int, val avisosMostrados: Int, val avisosRespondidos: Int, val diasSoloAvisos: Int,
        // ---- ánimo
        val valMedia: Double?, val valAnterior: Double?, val errorValencia: Double?, val actMedia: Double?,
        val registrosAnimo: Int, val emociones: List<Pair<String, Int>>, val negativasPct: Double?, val pensamientos: List<Pair<String, Int>>,
        val acompanadoPct: Double?, val valSolo: Double?, val valAcompanado: Double?, val apreciacion: List<Pair<String, Int>>,
        val errorPrediccion: Double?, val nPrediccion: Int,
        // ---- cuestionarios
        val cuestionarios: List<Cuestionario>,
        // ---- sueño
        val suenoH: Double?, val suenoAnterior: Double?, val errorSueno: Double?, val dormir: Pair<Double, Double?>?, val despertar: Pair<Double, Double?>?,
        val calidad: Double?, val pesadillas: Int, val camaMin: Double?,
        // ---- conducta
        val pasos: Double?, val pasosAnterior: Double?, val errorPasos: Double?, val ejercicioMin: Int, val aireLibreMin: Double?, val enCasaMin: Double?,
        val habitosPct: Double?, val dolorMedio: Double?, val dolorMax: Int?, val contextos: List<Pair<String, Int>>,
        val pantallaMin: Double?, val pantallaAnterior: Double?, val errorPantalla: Double?, val desbloqueos: Double?,
        // ---- contacto
        val diasSociales: Int, val vio: List<Pair<String, Int>>, val penso: List<Pair<String, Int>>, val llamadas: Int,
        // ---- gimnasio y ejercicio
        val gimnasio: List<Pair<String, Double?>>, val otrasSesiones: List<Triple<String, Int, Int>>,
        // ---- nutrición
        val nutricion: Nutricion?,
        // ---- casillas
        val sustancias: List<Sustancia>?, val mejorMomento: Pair<String, String>?, val peorMomento: Pair<String, String>?, val privados: Privados?,
    ) {
        val n: Int get() = fechas.size
    }

    /** Sesiones del reloj que son el gimnasio mismo (para no contarlas dos veces). */
    private val FUERZA = setOf("fuerza", "pesas")

    /** Las kcal de un día con menos de esto no son un día registrado (la misma regla de [Periodo]). */
    const val MINIMO_KCAL = 300.0

    // ------------------------------------------------------------------ piezas sin la app (se prueban solas)

    private val RX_PERSONA = Regex("@([\\p{L}\\p{N}_]+)")

    /** «@ana_luisa» → «A.L.»: en este reporte los nombres nunca salen completos. */
    fun sinNombres(t: String): String = RX_PERSONA.replace(t) { m ->
        m.groupValues[1].split('_', '-').filter { it.isNotBlank() }.joinToString("") { it.take(1).uppercase() + "." }
    }

    fun media(x: List<Double>): Double? = x.takeIf { it.isNotEmpty() }?.average()

    /** Desviación estándar muestral (n − 1); null con menos de 2 valores. */
    fun desviacion(x: List<Double>): Double? {
        if (x.size < 2) return null
        val m = x.average()
        return sqrt(x.sumOf { (it - m) * (it - m) } / (x.size - 1))
    }

    /** Media y desviación juntas; null sin valores. */
    fun mediaYDesviacion(x: List<Double>): Pair<Double, Double?>? = media(x)?.let { it to desviacion(x) }

    /**
     * Cada día (fecha, valor, objetivo) contra su objetivo. Cuentan solo los días con valor y objetivo; [minimo] descarta los
     * días sin registro real (menos de 300 kcal). «Dentro» es a ±[tolerancia] del objetivo (10 %).
     */
    fun contraObjetivo(dias: List<Triple<String, Double?, Double?>>, minimo: Double = 0.0, tolerancia: Double = 0.10): Objetivo? {
        val validos = dias.filter { (_, v, o) -> v != null && v >= minimo && o != null && o > 0 }.map { Triple(it.first, it.second!!, it.third!!) }
        if (validos.isEmpty()) return null
        val dif = validos.map { it.first to it.second - it.third }
        val pct = validos.map { (it.second - it.third) / it.third }
        return Objetivo(
            diferencias = dif, diferenciaMedia = dif.map { it.second }.average(), porcentajeMedio = pct.average(),
            dentro = pct.count { kotlin.math.abs(it) <= tolerancia }, encima = pct.count { it > tolerancia }, debajo = pct.count { it < -tolerancia },
            valorMedio = validos.map { it.second }.average(), objetivoMedio = validos.map { it.third }.average(),
        )
    }

    /**
     * Las [k] emociones más nombradas en los registros de ánimo, y qué parte de las nombradas (con tono conocido) son desagradables.
     * «Otro» se nombra con lo que escribiste (esquema 17), y si alguna vez dijiste «mucho», se anota al lado: «Ansiedad (2 «mucho»)».
     */
    fun emociones(registros: List<Momento>, k: Int = 5): Pair<List<Pair<String, Int>>, Double?> {
        val todas = registros.flatMap { Catalogo.emocionesDe(it) }
        val periodo = Periodo.animoDe(registros, k, conNoSe = true)
        val top = periodo.first.map { e -> (if (e.mucho > 0) "${e.nombre} (${e.mucho} «mucho»)" else e.nombre) to e.n }
        val conTono = todas.mapNotNull { Catalogo.sentimiento(it) }
        val neg = if (conTono.isEmpty()) null else conTono.count { it.tono < 0 }.toDouble() / conTono.size
        return top to neg
    }

    /** Impulsos sin una recaída en las 3 horas siguientes (la regla de la pantalla Privado, `Recaidas.resumen`). */
    fun superados(impulsosMs: List<Long>, recaidasMs: List<Long>): Int =
        impulsosMs.count { i -> recaidasMs.none { it >= i && it - i <= 3 * 3_600_000L } }

    private fun <T> conteo(l: List<T>, nombre: (T) -> String): List<Pair<String, Int>> =
        l.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.map { nombre(it.key) to it.value }

    private fun legible(id: String) = id.replace('_', ' ').replaceFirstChar { it.uppercase() }

    /** Los detonantes de un día o de una recaída, con «otro» cambiado por lo que escribiste (esquema 17). */
    private fun detonantesCon(l: List<String>, otro: String?): List<String> =
        l.map { if (it == "otro" && !otro.isNullOrBlank()) otro.trim().lowercase() else it }

    /** Los detonantes de [Catalogo.DETONANTES] con acentos; los escritos a mano salen como se anotaron. */
    private fun detonante(id: String) = Catalogo.DETONANTES.firstOrNull { it.id == id }?.nombre ?: legible(id)

    // ------------------------------------------------------------------ el reporte

    fun calcular(op: Opciones): Datos {
        val ini = op.ini; val fin = op.fin
        val p = Periodo.calcular(null, ini, fin)
        val fechas = p.fechas
        val en = fechas.toHashSet()
        val dias = fechas.mapNotNull { Repo.dias[it] }
        val mu = Repo.momentos.filter { it.fecha in en && !it.privado }
        // El panel diario da lo que ya se calcula para R: con quién estuviste, en casa, al aire libre. Sin él, esas cifras faltan.
        val panel = runCatching { Estadistica.panel(dinamicas = false) }.getOrDefault(emptyMap())
        fun delPanel(k: String) = fechas.mapNotNull { panel[it]?.get(k) }

        // ---- cobertura
        val respAv = io.github.ruddy2003.vestigium.data.AvisosAnimo.respondidos(Repo.momentos)
        val avisos = fechas.mapNotNull { Repo.avisos[it] }.map { io.github.ruddy2003.vestigium.data.AvisosAnimo.cifras(it, respAv) }
        fun sumaAvisos(k: String) = avisos.sumOf { it[k] ?: 0.0 }.toInt()

        // ---- ánimo
        val registros = mu.filter { Catalogo.esRegistro(it) && !Estadistica.cierreFuera(it) }
        val (emo, neg) = emociones(registros)
        val pens = conteo(registros.flatMap { Catalogo.pensamientosDe(it) }) { id -> Catalogo.PENSAMIENTOS.firstOrNull { it.id == id }?.nombre ?: id }
        val con = mu.filter { it.tipo == "muestreo" && it.con_quien.isNotEmpty() }
        val solos = con.filter { "solo" in it.con_quien }; val acomp = con.filter { "solo" !in it.con_quien }
        val apre = conteo(dias.mapNotNull { it.apreciacion?.takeIf { a -> a != "no_aplica" } }) { id -> Catalogo.CIENTIFICAS.first { it.clave == "apreciacion" }.opciones.firstOrNull { it.id == id }?.nombre ?: id }
        val errores = fechas.mapNotNull { f -> Repo.dias[Fechas.sumar(f, -1)]?.prediccion_manana?.let { pr -> Repo.dias[f]?.valencia?.let { (it - pr).toDouble() } } }

        // ---- cuestionarios: los semanales y el de soledad; la personalidad no cambia en una semana.
        val cuest = listOf(Escalas.WHO5 to "porcentaje", Escalas.PSS4 to "total", Escalas.UCLA3 to "total").map { (inst, clave) ->
            val todas = Repo.respuestas.filter { it.instrumento == inst.id }.sortedBy { it.ts }
            fun punt(r: io.github.ruddy2003.vestigium.data.Respuesta) = r.puntajes[clave]?.let { "%.0f".format(it) } ?: "—"
            val dentro = todas.filter { it.fecha in en }
            Cuestionario(
                inst.nombre, dentro.map { it.fecha to punt(it) }, dentro.lastOrNull()?.let { inst.lectura(it.puntajes) },
                todas.filter { it.fecha < ini }.takeLast(3).map { it.fecha to punt(it) },
            )
        }

        // ---- sueño
        val noches = fechas.mapNotNull { Repo.salud[it] }.filter { (it.sueno_min ?: 0) > 120 }
        val dormir = noches.mapNotNull { it.sueno_inicio?.let { s -> Exportar.horaDecimal(s, true) } }
        val despertar = noches.mapNotNull { it.sueno_fin?.let { s -> Exportar.horaDecimal(s, false) } }

        // ---- gimnasio: los días con series o con el contexto «gym»; la duración, la de MacroFactor o la de la sesión de fuerza del reloj.
        val sesiones = Repo.sesiones.filter { it.fecha in en }
        val gimnasio = fechas.filter { f -> (Repo.entrenos[f]?.series ?: 0) > 0 || Repo.dias[f]?.ctx?.get("gym") == true }.map { f ->
            f to (Repo.entrenos[f]?.minutos ?: sesiones.filter { it.fecha == f && it.tipo_nombre in FUERZA }.sumOf { it.min }.takeIf { it > 0 }?.toDouble())
        }
        val diasGym = gimnasio.map { it.first }.toSet()
        val otras = sesiones.filterNot { it.fecha in diasGym && it.tipo_nombre in FUERZA }.groupBy { it.tipo_nombre }
            .map { (t, l) -> Triple(legible(t), l.size, l.sumOf { it.min }) }.sortedByDescending { it.third }

        // ---- nutrición (MacroFactor)
        val nut = fechas.mapNotNull { Repo.nutricion[it] }
        val nutReg = nut.filter { (it.valores["kcal"] ?: 0.0) >= MINIMO_KCAL }
        val nutricion = if (nutReg.isEmpty()) null else {
            fun prom(k: String) = media(nutReg.mapNotNull { it.valores[k] })
            fun contra(k: String, o: String) = media(nutReg.mapNotNull { it.valores[k] })?.let { it to media(nutReg.mapNotNull { n -> n.valores[o] }) }
            Nutricion(
                diasConRegistro = nutReg.size, diasSinRegistro = fechas.size - nutReg.size,
                kcal = contraObjetivo(nut.map { Triple(it.fecha, it.valores["kcal"], it.valores["objetivo_kcal"]) }, MINIMO_KCAL),
                proteina = contraObjetivo(nutReg.map { Triple(it.fecha, it.valores["proteina_g"], it.valores["objetivo_proteina_g"]) }),
                carbohidratos = contra("carbohidratos_g", "objetivo_carbohidratos_g"), grasa = contra("grasa_g", "objetivo_grasa_g"),
                nutrientes = listOf("cafeina_mg" to ("Cafeína" to "mg"), "alcohol_g" to ("Alcohol" to "g"), "azucares_anadidos_g" to ("Azúcares añadidos" to "g"),
                    "fibra_g" to ("Fibra" to "g"), "omega_3_g" to ("Omega-3" to "g"), "agua_g" to ("Agua" to "ml"))
                    .mapNotNull { (k, nu) -> prom(k)?.let { Triple(nu.first, it, nu.second) } },
                cafeinaUltima = mediaYDesviacion(nutReg.mapNotNull { it.cafeina_ultima?.let(Exportar::horaTexto) }),
                comidasPorDia = media(nutReg.filter { it.comidas > 0 }.map { it.comidas.toDouble() }),
                primera = mediaYDesviacion(nutReg.mapNotNull { it.primera?.let(Exportar::horaTexto) }),
                ultima = mediaYDesviacion(nutReg.mapNotNull { it.ultima?.let(Exportar::horaTexto) }),
            )
        }

        // ---- sustancias (casilla): las que no ocultaste de las pantallas.
        val sustancias = if (!op.sustancias) null else Repo.consumos.filter { it.fecha in en && Sustancias.visible(it) }
            .groupBy { Catalogo.nombreSustancia(it) }.map { (nombre, l) ->
                Sustancia(
                    nombre, Catalogo.sustancia(l.first().sustancia)?.unidad ?: "", l.map { it.fecha }.distinct().size,
                    l.mapNotNull { it.cantidad }.takeIf { it.isNotEmpty() }?.sum(), l.mapNotNull { Sustancias.maximo(it) }.maxOrNull(),
                    media(l.mapNotNull { it.cruda?.toDouble() }),
                    conteo(l.flatMap { it.sintomas }) { id -> Catalogo.SINTOMAS.firstOrNull { it.id == id }?.nombre ?: id }.take(3).map { it.first },
                )
            }.sortedByDescending { it.dias }

        // ---- momentos del periodo (casilla): lo mejor del mejor día y lo peor del peor.
        fun texto(f: String?, campo: (io.github.ruddy2003.vestigium.data.Dia) -> String): Pair<String, String>? {
            if (f == null) return null
            val t = Repo.dias[f]?.let(campo)?.takeIf { it.isNotBlank() } ?: return null
            return f to sinNombres(t.trim())
        }
        val mejor = if (op.momentosPeriodo) texto(p.mejorDia) { it.mejor_momento } else null
        val peor = if (op.momentosPeriodo) texto(p.peorDia) { it.peor_momento } else null

        // ---- lo privado (casilla)
        val privados = if (!op.privado) null else {
            val imp = Repo.momentos.filter { it.tipo == "impulso" && it.fecha in en }
            val rec = Repo.momentos.filter { it.tipo == "recaida" }
            val diasRec = fechas.filter { f -> Repo.dias[f]?.recaida == true || rec.any { it.fecha == f } }
            Privados(
                diasConRecaida = diasRec.size, diasRespondidos = fechas.count { f -> Repo.dias[f]?.recaida != null || f in diasRec },
                impulsos = imp.size, superados = superados(imp.map { Fechas.millis(it.ts) }, rec.map { Fechas.millis(it.ts) }),
                impulsoMedio = media(dias.mapNotNull { it.impulso?.toDouble() }),
                detonantes = conteo(dias.flatMap { detonantesCon(it.detonantes, it.detonante_otro) } +
                    rec.filter { it.fecha in en }.flatMap { detonantesCon(it.detonantes, it.detonante_otro) }, ::detonante).take(5),
                medicamentos = conteo(dias.flatMap { d -> d.medicamentos.map { it.trim() }.filter { it.isNotBlank() }.distinct() }) { it },
            )
        }

        val filas = fechas.mapIndexed { i, f ->
            val d = Repo.dias[f]; val n = Repo.nutricion[f]
            Fila(
                fecha = f, valencia = d?.valencia, activacion = p.activacion.getOrNull(i), dificil = d?.dia_dificil == true,
                suenoH = p.suenoPorDia.getOrNull(i), calidad = d?.sueno_calidad,
                kcal = n?.valores?.get("kcal")?.takeIf { it >= MINIMO_KCAL }, objetivoKcal = n?.valores?.get("objetivo_kcal"),
                gimnasioMin = gimnasio.firstOrNull { it.first == f }?.second, gimnasio = f in diasGym,
                mejor = if (op.momentosDia) sinNombres(d?.mejor_momento.orEmpty().trim()) else "",
                peor = if (op.momentosDia) sinNombres(d?.peor_momento.orEmpty().trim()) else "",
                impulso = if (op.privado) d?.impulso else null, recaida = if (!op.privado) null else if (Repo.momentos.any { it.tipo == "recaida" && it.fecha == f }) true else d?.recaida,
            )
        }

        val sociales = fechas.filter { panel[it]?.get("vi_familia") != null }
        fun diasCon(k: String) = sociales.count { panel[it]?.get(k) == 1.0 }
        return Datos(
            op = op, fechas = fechas, filas = filas,
            diasDiario = p.diasDiario, avisosPlaneados = sumaAvisos("avisos_planeados"), avisosMostrados = sumaAvisos("avisos_mostrados"),
            avisosRespondidos = sumaAvisos("avisos_respondidos"), diasSoloAvisos = p.diasSoloAvisos,
            valMedia = p.valMedia, valAnterior = p.valAnterior, errorValencia = p.errorAnterior["valencia"], actMedia = p.actMedia,
            registrosAnimo = registros.size, emociones = emo, negativasPct = neg, pensamientos = pens,
            acompanadoPct = if (con.isEmpty()) null else acomp.size.toDouble() / con.size,
            valSolo = media(solos.mapNotNull { it.valencia?.toDouble() }), valAcompanado = media(acomp.mapNotNull { it.valencia?.toDouble() }),
            apreciacion = apre, errorPrediccion = media(errores), nPrediccion = errores.size,
            cuestionarios = cuest,
            suenoH = p.suenoH, suenoAnterior = p.suenoAnterior, errorSueno = p.errorAnterior["sueno"],
            dormir = mediaYDesviacion(dormir), despertar = mediaYDesviacion(despertar),
            calidad = media(dias.mapNotNull { it.sueno_calidad?.toDouble() }),
            pesadillas = dias.count { it.sueno_recordado && Catalogo.pesadillaDe(it) == true },
            camaMin = media(fechas.mapNotNull { Repo.pantalla[it]?.cama_min?.toDouble() }),
            pasos = p.pasos, pasosAnterior = p.pasosAnterior, errorPasos = p.errorAnterior["pasos"], ejercicioMin = p.ejercicioMin,
            aireLibreMin = media(delPanel("aire_libre_min")), enCasaMin = media(delPanel("en_casa_min")),
            habitosPct = p.habitosPct, dolorMedio = media(dias.mapNotNull { it.dolor?.toDouble() }), dolorMax = dias.mapNotNull { it.dolor }.maxOrNull(),
            contextos = listOf("enfermo", "trabajo", "academico").map { c -> (Catalogo.CONTEXTOS.first { it.id == c }.nombre) to dias.count { it.ctx[c] == true } },
            pantallaMin = p.pantallaMin, pantallaAnterior = p.pantallaAnterior, errorPantalla = p.errorAnterior["pantalla"], desbloqueos = p.desbloqueos,
            diasSociales = sociales.size,
            vio = listOf("Familia" to "vi_familia", "Pareja" to "vi_pareja", "Amistades" to "vi_amistades", "Expareja" to "vi_expareja").map { it.first to diasCon(it.second) },
            penso = listOf("Familia" to "penso_en_familia", "Pareja" to "penso_en_pareja", "Amistades" to "penso_en_amistades", "Expareja" to "penso_en_expareja").map { it.first to diasCon(it.second) },
            llamadas = p.llamadas,
            gimnasio = gimnasio, otrasSesiones = otras,
            nutricion = nutricion,
            sustancias = sustancias, mejorMomento = mejor, peorMomento = peor, privados = privados,
        )
    }
}
