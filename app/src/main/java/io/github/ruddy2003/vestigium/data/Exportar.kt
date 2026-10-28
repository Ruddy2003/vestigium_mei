package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import io.github.ruddy2003.vestigium.ciencia.Escalas
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Serializable
data class Respaldo(
    val exportado: String,
    val schema_v: Int,
    val dias: List<Dia> = emptyList(),
    val momentos: List<Momento> = emptyList(),
    val gastos: List<Gasto> = emptyList(),
    val respuestas: List<Respuesta> = emptyList(),
    val reacciones: List<Reaccion> = emptyList(),
    val lugares: List<Lugar> = emptyList(),
    val visitas: List<Visita> = emptyList(),
    val salud: List<SaludDia> = emptyList(),
    val pantalla: List<PantallaDia> = emptyList(),
    val cartas: List<Carta> = emptyList(),
    val experimentos: List<Experimento> = emptyList(),
    val config: Config = Config(),
    val entorno: List<EntornoDia> = emptyList(),
    val fotos: List<FotosDia> = emptyList(),
    val agenda: List<AgendaDia> = emptyList(),
    val notifs: List<NotifDia> = emptyList(),
    val actividad: List<ActividadDia> = emptyList(),
    val sesiones: List<Sesion> = emptyList(),
    val ascensos: List<Ascenso> = emptyList(),
    val obras: List<Obra> = emptyList(),
    val escuchas: List<Escucha> = emptyList(),
    val comunicacion: List<ComunicacionDia> = emptyList(),
    val conexiones: List<ConexionDia> = emptyList(),
    val subrayados: List<Subrayado> = emptyList(),
    val huellas: List<Huella> = emptyList(),
    val huellas_dia: List<HuellasDia> = emptyList(),
    val chats: List<ChatDia> = emptyList(),
    val nutricion: List<NutricionDia> = emptyList(),
    val comidas: List<Comida> = emptyList(),
    val series: List<Serie> = emptyList(),
    val entrenos: List<EntrenoDia> = emptyList(),
    val ingresos: List<Ingreso> = emptyList(),
    val youtube: List<YtEvento> = emptyList(),
    val tramos: List<Tramo> = emptyList(),
    val consumos: List<Consumo> = emptyList(),
    val notas: List<Nota> = emptyList(),
    val metas: List<Meta> = emptyList(),
    val tomas: List<TomaSuplemento> = emptyList(),
    val laptop: List<LaptopDia> = emptyList(),
    val avisos: List<AvisosDia> = emptyList(),
)

/** CSV listos para R: fechas AAAA-MM-DD, booleanos como 0/1, listas separadas por «|», vacío = dato faltante. */
object Exportar {
    private fun celda(v: Any?): String {
        if (v == null) return ""
        val s = when (v) {
            is Boolean -> if (v) "1" else "0"
            is Double -> if (v.isNaN()) "" else v.toString()
            is List<*> -> v.joinToString("|")
            else -> v.toString()
        }
        return if (s.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) "\"" + s.replace("\"", "\"\"") + "\"" else s
    }

    fun csv(filas: List<Map<String, Any?>>): String {
        val cols = LinkedHashSet<String>()
        filas.forEach { cols.addAll(it.keys) }
        val sb = StringBuilder("﻿")
        sb.append(cols.joinToString(",")).append('\n')
        filas.forEach { f -> sb.append(cols.joinToString(",") { celda(f[it]) }).append('\n') }
        return sb.toString()
    }

    fun filasDias(): List<Map<String, Any?>> {
        val eq = Repo.equivalencias()
        return Repo.dias.values.sortedBy { it.fecha }.map { d ->
            val r = linkedMapOf<String, Any?>(
                "fecha" to d.fecha, "schema_v" to d.schema_v, "origen" to d.origen, "retroactivo" to d.retroactivo, "ts_valencia" to d.ts_valencia,
                "dia_dificil" to d.dia_dificil, "ts_creado" to d.ts_creado, "ts_editado" to d.ts_editado, "version_noche" to d.version_noche,
                "valencia" to d.valencia, "activacion" to d.activacion, "dolor" to d.dolor,
            )
            Catalogo.CONTEXTOS.forEach { r["ctx_${it.id}"] = d.ctx[it.id] == true }
            r["hitos"] = d.hitos
            r["flow"] = d.flow; r["sdt_necesidad"] = d.sdt_necesidad; r["apreciacion"] = d.apreciacion
            r["sueno_recordado"] = d.sueno_recordado; r["sueno_valencia"] = d.sueno_valencia; r["sueno_lucidez"] = d.sueno_lucidez
            r["sueno_vividez"] = d.sueno_vividez; r["sueno_tipo"] = d.sueno_tipo; r["sueno_temas"] = d.sueno_temas
            r["sueno_calidad"] = d.sueno_calidad; r["sueno_categorias"] = d.sueno_categorias; r["sueno_texto"] = d.sueno_texto
            r["mejor_momento"] = d.mejor_momento; r["peor_momento"] = d.peor_momento
            r["impulso"] = d.impulso; r["recaida"] = d.recaida; r["detonantes"] = d.detonantes
            r["medicamentos"] = d.medicamentos; r["prediccion_manana"] = d.prediccion_manana
            // Antes de que existiera el hábito no se sabe (P-15).
            Repo.config.habitos.forEach { h -> r["hab_${h.id}"] = if (d.habitos[h.id] == true) true else if (h.desde.isNotBlank() && d.fecha < h.desde) null else false }
            r["personas"] = Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct()
            // Cómo estuviste con cada persona: «ana:estuve|luis:pense|mama:mensajes».
            r["presencias"] = Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct().map { "$it:" + Presencias.enDiario(d, it, eq) }
            r["presencias_revisado"] = d.presencias_ok
            r["temas"] = Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct()
            r["palabras"] = Texto.palabras(d.nota)
            r["nota"] = d.nota
            r
        }
    }

    /** Nivel subjetivo (0 a 10) de una sustancia en un instante: el mayor de sus episodios; null antes del primer registro de esa sustancia. */
    private fun nivelSustancia(id: String, ms: Long, lista: List<Consumo>): Double? {
        val l = lista.filter { it.sustancia == id }
        val p0 = l.minOfOrNull { Fechas.millis(it.inicio) } ?: return null
        if (ms < p0) return null
        return l.mapNotNull { c -> if (ms >= Fechas.millis(c.inicio) && ms <= Sustancias.finEfectivo(c)) Sustancias.nivelEn(c, ms) else null }.maxOrNull() ?: 0.0
    }

    fun filasMomentos(): List<Map<String, Any?>> {
        val cons = Repo.consumos.filter { Catalogo.sustancia(it.sustancia)?.conNivel == true }
        return Repo.momentos.map {
        val ms = Fechas.millis(it.ts)
        linkedMapOf<String, Any?>("id" to it.id, "ts" to it.ts, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "tipo" to it.tipo,
            "es_animo" to Catalogo.esRegistro(it), "emociones" to Catalogo.emocionesDe(it), "emocion_otra" to it.emocion_otra, "pensamiento" to Catalogo.pensamientoDe(it),
            "intensidad" to it.intensidad, "valencia" to it.valencia, "activacion" to it.activacion, "texto" to it.texto,
            "personas" to Texto.personas(it.texto), "temas" to Texto.temas(it.texto),
            "lat" to it.lat, "lon" to it.lon, "precision_m" to it.precision_m, "lugar" to Repo.lugares.firstOrNull { l -> l.id == it.lugar }?.nombre,
            "origen" to it.origen, "aviso_ts" to it.aviso_ts,
            "latencia_min" to it.aviso_ts?.let { a -> (Fechas.millis(it.ts) - Fechas.millis(a)) / 60000.0 },
            "ruido_db" to it.ruido_db, "luz_lux" to it.luz_lux,
            "con_quien" to it.con_quien, "detonantes" to it.detonantes, "dispositivo" to it.dispositivo, "antes" to it.antes, "duracion_min" to it.duracion_min, "foco" to it.foco, "cierre" to it.esCierre,
            "tipo_lugar" to Repo.lugares.firstOrNull { l -> l.id == it.lugar }?.tipo,
            "alcohol_nivel" to nivelSustancia("alcohol", ms, cons), "cannabis_nivel" to nivelSustancia("cannabis", ms, cons), "otra_nivel" to nivelSustancia("otra", ms, cons),
            "ts_creado" to it.ts_creado, "ts_editado" to it.ts_editado)
        }
    }

    /** Cada consumo de una sustancia, con lo de la mañana siguiente y el alcohol en sangre estimado. */
    fun filasSustancias(): List<Map<String, Any?>> {
        val l = Repo.consumos.sortedBy { it.inicio }
        val alc = if (l.any { it.sustancia == "alcohol" }) runCatching { Sustancias.dosis(l.first().fecha, Fechas.diaLogico()).second }.getOrDefault(emptyList()) else emptyList()
        val ocultas = Repo.config.sustancias_ocultas
        return l.map { c ->
            linkedMapOf<String, Any?>("id" to c.id, "fecha" to c.fecha, "sustancia" to c.sustancia, "otra_nombre" to c.otra_nombre.ifBlank { null },
                "inicio" to c.inicio, "fin" to c.fin, "hora_inicio" to Fechas.horaDe(c.inicio), "hora_fin" to c.fin?.let { Fechas.horaDe(it) },
                "duracion_h" to Sustancias.horas(c)?.let { Math.round(it * 100) / 100.0 }, "nivel_max" to Sustancias.maximo(c),
                "lecturas" to c.niveles.map { "${Fechas.horaDe(it.ts)}=${it.nivel}" }, "cantidad" to c.cantidad, "unidad" to Catalogo.sustancia(c.sustancia)?.unidad,
                "via" to c.via, "con_quien" to c.con_quien, "lat" to c.lat, "lon" to c.lon, "lugar" to (Repo.lugares.firstOrNull { it.id == c.lugar }?.nombre ?: c.lugar),
                "cruda" to c.cruda, "sintomas" to c.sintomas, "nota" to c.nota, "personas" to Texto.personas(c.nota), "temas" to Texto.temas(c.nota),
                "alcohol_estimado_max_gl" to Sustancias.alcoholMaximo(c, alc)?.let { Math.round(it * 1000) / 1000.0 },
                "completo" to c.completo, "oculta_en_pantallas" to (c.sustancia in ocultas), "origen" to c.origen, "ts_creado" to c.ts_creado)
        }
    }

    /**
     * Una fila por hora (fecha de calendario y hora de 0 a 23) con lo que pasó en ella: dormido, dónde, pantalla,
     * notificaciones, luz, música, ánimo, cafeína y alcohol en el cuerpo, nivel de cada sustancia, enfoque, siestas,
     * gastos y comidas. Para ver el día por dentro en R (por ejemplo, el ánimo según la cafeína de esa hora).
     */
    fun filasHoras(): List<Map<String, Any?>> {
        val H = 3_600_000L
        val zona = Fechas.zona()
        fun clave(ms: Long): String { val t = java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(ms), zona); return "${t.toLocalDate()}|${t.hour}" }
        fun inicioHora(f: String, h: Int): Long = java.time.LocalDateTime.of(Fechas.parse(f), java.time.LocalTime.of(h, 0)).atZone(zona).toInstant().toEpochMilli()
        /** Reparte el intervalo [a, b) entre las horas que toca, en minutos. */
        fun repartir(a: Long, b: Long, suma: HashMap<String, Double>) {
            if (b <= a || b - a > 40L * 24 * H) return
            var t = a
            while (t < b) {
                val ldt = java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(t), zona).withMinute(0).withSecond(0).withNano(0)
                val sig = ldt.plusHours(1).atZone(zona).toInstant().toEpochMilli()
                val y = minOf(b, sig)
                val k = "${ldt.toLocalDate()}|${ldt.hour}"
                suma[k] = (suma[k] ?: 0.0) + (y - t) / 60_000.0
                t = y
            }
        }
        val fechas = sortedSetOf<String>()
        val dormido = HashMap<String, Double>(); val siesta = HashMap<String, Double>(); val enfoque = HashMap<String, Double>()
        Repo.salud.values.forEach { x ->
            val a = x.sueno_inicio?.let { Fechas.millis(it) } ?: return@forEach; val b = x.sueno_fin?.let { Fechas.millis(it) } ?: return@forEach
            if (a > 0 && b > a) repartir(a, b, dormido)
        }
        Repo.tramos.forEach { t -> val a = Fechas.millis(t.inicio); repartir(a, a + t.min * 60_000L, if (t.tipo == "siesta") siesta else enfoque) }
        // Lugares: los minutos en cada lugar por hora; se queda el de más minutos.
        val porLugar = HashMap<String, HashMap<String, Double>>()
        val casas = Repo.lugares.filter { it.tipo == "casa" }.map { it.id }.toSet()
        val enCasa = HashMap<String, Double>()
        val ahora = System.currentTimeMillis()
        Repo.visitas.forEach { v ->
            val a = Fechas.millis(v.entrada); val b = v.salida?.let { Fechas.millis(it) } ?: ahora
            if (a <= 0) return@forEach
            val m = porLugar.getOrPut(v.lugar) { HashMap() }
            repartir(a, b, m)
            if (v.lugar in casas) repartir(a, b, enCasa)
        }
        val nombres = Repo.lugares.associate { it.id to it.nombre }
        val lugarHora = HashMap<String, Pair<String, Double>>()
        porLugar.forEach { (id, m) -> m.forEach { (k, min) -> if (min > (lugarHora[k]?.second ?: 0.0)) lugarHora[k] = (nombres[id] ?: id) to min } }
        // Lo que va por día con 24 horas: notificaciones, luz y la pantalla desde la v13 van por día de la app (de 0 a 4 es la madrugada siguiente).
        val pantalla = HashMap<String, Int>(); val notifs = HashMap<String, Int>(); val luz = HashMap<String, Double>()
        fun calendario(f: String, h: Int) = if (h < 5) Fechas.sumar(f, 1) else f
        // La pantalla de antes de la v13 va por día de calendario; la de después, por día lógico (de 0 a 4, la madrugada siguiente).
        Repo.pantalla.values.forEach { p -> p.por_hora_min.forEachIndexed { h, m -> pantalla["${if (p.ventana == "logica") calendario(p.fecha, h) else p.fecha}|$h"] = m }; fechas += p.fecha }
        Repo.notifs.values.forEach { n -> n.por_hora.forEachIndexed { h, k -> notifs["${calendario(n.fecha, h)}|$h"] = k } }
        Repo.conexiones.values.forEach { c -> c.luz_n.forEachIndexed { h, k -> if (k > 0) luz["${calendario(c.fecha, h)}|$h"] = Math.round((c.luz_suma.getOrNull(h) ?: 0.0) / k * 10) / 10.0 } }
        val canciones = HashMap<String, Int>()
        runCatching { Repo.escuchas("0000-01-01", "9999-12-31").forEach { e -> val k = clave(Fechas.millis(e.ts)); canciones[k] = (canciones[k] ?: 0) + 1 } }
        // Ánimo al momento.
        val animo = Repo.momentos.filter { Catalogo.esRegistro(it) }.groupBy { clave(Fechas.millis(it.ts)) }
        // Gastos y comidas con hora.
        val gastos = HashMap<String, Double>()
        Repo.gastos.forEach { g -> runCatching { val h = g.hora.take(2).toInt(); val k = "${g.fecha}|$h"; gastos[k] = (gastos[k] ?: 0.0) + g.monto } }
        val comidas = runCatching { Repo.comidas("0000-01-01", "9999-12-31") }.getOrDefault(emptyList())
        val comidasN = HashMap<String, Int>(); val kcal = HashMap<String, Double>()
        comidas.forEach { c -> val h = c.hora.take(2).toIntOrNull() ?: return@forEach; val k = "${c.fecha}|$h"; comidasN[k] = (comidasN[k] ?: 0) + 1; c.valores["kcal"]?.let { kcal[k] = (kcal[k] ?: 0.0) + it } }
        // Sustancias: el nivel que dijiste a la mitad de cada hora, y la cafeína y el alcohol estimados.
        val cons = Repo.consumos
        val niveles = HashMap<String, HashMap<String, Double>>()
        cons.filter { Catalogo.sustancia(it.sustancia)?.conNivel == true }.forEach { c ->
            val a = Fechas.millis(c.inicio); val b = Sustancias.finEfectivo(c)
            if (b - a > 2 * 24 * H) return@forEach
            var t = java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(a), zona).withMinute(0).withSecond(0).withNano(0)
            while (t.atZone(zona).toInstant().toEpochMilli() < b) {
                val mitad = t.atZone(zona).toInstant().toEpochMilli() + 30 * 60_000L
                val n = Sustancias.nivelEn(c, mitad)
                if (n != null) { val m = niveles.getOrPut(c.sustancia) { HashMap() }; val k = "${t.toLocalDate()}|${t.hour}"; m[k] = maxOf(m[k] ?: 0.0, n) }
                t = t.plusHours(1)
            }
        }
        val tabaco = HashMap<String, Double>()
        cons.filter { it.sustancia == "tabaco" }.forEach { c -> val k = clave(Fechas.millis(c.inicio)); tabaco[k] = (tabaco[k] ?: 0.0) + (c.cantidad ?: 1.0) }
        val primerConsumo = cons.minOfOrNull { Fechas.millis(it.inicio) }
        Repo.momentos.forEach { fechas += clave(Fechas.millis(it.ts)).substringBefore('|') }
        (dormido.keys + enfoque.keys + siesta.keys + lugarHora.keys + notifs.keys + luz.keys + gastos.keys + comidasN.keys + tabaco.keys + niveles.values.flatMap { it.keys })
            .forEach { fechas += it.substringBefore('|') }
        cons.forEach { fechas += clave(Fechas.millis(it.inicio)).substringBefore('|') }
        val hoyCal = Fechas.hoyCalendario()
        val lista = fechas.filter { it <= hoyCal }
        if (lista.isEmpty()) return emptyList()
        val (caf, alc) = runCatching { Sustancias.dosis(Fechas.sumar(lista.first(), -1), Fechas.diaLogico()) }.getOrDefault(emptyList<Sustancias.Dosis>() to emptyList<Sustancias.Dosis>())
        // Cafeína: suma de las dosis de las últimas 36 horas en cada mitad de hora (ventana deslizante).
        val vm = Repo.config.cafeina_vida_media_h
        val cafeina = HashMap<String, Double>()
        if (caf.isNotEmpty()) {
            var i0 = 0
            for (f in lista) for (h in 0..23) {
                val t = inicioHora(f, h) + 30 * 60_000L
                while (i0 < caf.size && caf[i0].ms < t - 36 * H) i0++
                var s0 = 0.0; var j = i0
                while (j < caf.size && caf[j].ms <= t) {
                    val dt = (t - caf[j].ms).toDouble()
                    s0 += caf[j].cantidad * (dt / (45.0 * 60_000)).coerceAtMost(1.0) * Math.pow(0.5, dt / (vm * H)); j++
                }
                cafeina["$f|$h"] = Math.round(s0 * 10) / 10.0
            }
        }
        // Alcohol en sangre: una simulación por día con alcohol (Widmark).
        val alcohol = HashMap<String, Double>()
        alc.groupBy { Fechas.diaLogicoDeMillis(it.ms) }.forEach { (_, l) ->
            val a = l.minOf { it.ms }
            val inst = (0 until 20).map { k -> java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(a), zona).withMinute(30).withSecond(0).withNano(0).plusHours(k.toLong()).atZone(zona).toInstant().toEpochMilli() }
            Sustancias.alcoholEn(l, inst).forEachIndexed { k, g -> if (g > 0) alcohol[clave(inst[k])] = Math.round(g * 1000) / 1000.0 }
        }
        val primerCaf = caf.firstOrNull()?.ms; val primerAlc = alc.firstOrNull()?.ms
        val out = ArrayList<Map<String, Any?>>(lista.size * 24)
        for (f in lista) for (h in 0..23) {
            val k = "$f|$h"
            val t0 = inicioHora(f, h)
            val an = animo[k].orEmpty()
            out += linkedMapOf<String, Any?>(
                "fecha" to f, "hora" to h, "dia_logico" to (if (h < 5) Fechas.sumar(f, -1) else f), "dia_semana" to Fechas.parse(f).dayOfWeek.value,
                "dormido_min" to (dormido[k]?.let { Math.round(it).toInt() } ?: 0), "siesta_min" to (siesta[k]?.let { Math.round(it).toInt() } ?: 0),
                "enfoque_min" to (enfoque[k]?.let { Math.round(it).toInt() } ?: 0),
                "lugar" to lugarHora[k]?.takeIf { it.second >= 15 }?.first, "en_casa_min" to (if (casas.isEmpty()) null else (enCasa[k]?.let { Math.round(it).toInt() } ?: 0)),
                "pantalla_min" to pantalla[k], "notificaciones" to notifs[k], "luz_lux" to luz[k], "canciones" to (canciones[k] ?: 0),
                "n_registros" to an.size, "valencia" to an.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average(),
                "activacion" to an.mapNotNull { it.activacion }.takeIf { it.isNotEmpty() }?.average(),
                "emociones" to an.flatMap { Catalogo.emocionesDe(it) }.distinct(),
                "cafeina_mg" to (if (primerCaf != null && t0 + H > primerCaf) cafeina[k] ?: 0.0 else null),
                "alcohol_gl" to (if (primerAlc != null && t0 + H > primerAlc) alcohol[k] ?: 0.0 else null),
                "alcohol_nivel" to (if (primerConsumo != null && cons.any { it.sustancia == "alcohol" }) niveles["alcohol"]?.get(k) ?: 0.0 else null),
                "cannabis_nivel" to (if (cons.any { it.sustancia == "cannabis" }) niveles["cannabis"]?.get(k) ?: 0.0 else null),
                "otra_nivel" to (if (cons.any { it.sustancia == "otra" }) niveles["otra"]?.get(k) ?: 0.0 else null),
                "tabaco_n" to (if (cons.any { it.sustancia == "tabaco" }) tabaco[k] ?: 0.0 else null),
                "gasto" to (gastos[k] ?: 0.0), "comidas_n" to (comidasN[k] ?: 0), "kcal" to kcal[k]?.let { Math.round(it).toDouble() },
            )
        }
        return out
    }

    fun filasTramos() = Repo.tramos.map {
        linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "tipo" to it.tipo, "es_siesta" to (it.tipo == "siesta"), "inicio" to it.inicio, "fin" to it.fin,
            "hora" to Fechas.horaDe(it.inicio), "min" to it.min, "nota" to it.nota, "personas" to Texto.personas(it.nota), "temas" to Texto.temas(it.nota), "origen" to it.origen,
            "ts_creado" to it.ts_creado)
    }

    fun filasSuplementos(): List<Map<String, Any?>> {
        val nombres = Repo.config.suplementos.associate { it.id to it }
        val manual = Repo.tomas.map { linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "suplemento" to it.suplemento,
            "nombre" to (nombres[it.suplemento]?.nombre ?: it.suplemento), "cantidad" to it.cantidad, "unidad" to nombres[it.suplemento]?.unidad, "estimado" to false,
            "ts" to it.ts, "ts_creado" to it.ts_creado) }
        val estimadas = if (nombres.containsKey("probioticos")) Suplementos.probioticosEstimados().map { (f, n) ->
            linkedMapOf<String, Any?>("id" to "est_$f", "fecha" to f, "hora" to null, "suplemento" to "probioticos", "nombre" to "Probióticos", "cantidad" to n, "unidad" to "porción", "estimado" to true, "ts" to null, "ts_creado" to null)
        } else emptyList()
        return (manual + estimadas).sortedBy { (it["fecha"] as String) + (it["hora"] ?: "") }
    }

    fun filasLaptop() = Repo.laptop.values.sortedBy { it.fecha }.flatMap { d ->
        d.apps.map { (n, m) -> linkedMapOf<String, Any?>("fecha" to d.fecha, "tipo" to "app", "nombre" to n, "minutos" to m, "categoria" to ColdTurkey.categoria(n)) } +
            d.sitios.map { (n, m) -> linkedMapOf<String, Any?>("fecha" to d.fecha, "tipo" to "sitio", "nombre" to n, "minutos" to m, "categoria" to ColdTurkey.categoria(n)) }
    }

    fun filasRed(): Pair<List<Map<String, Any?>>, List<Map<String, Any?>>> {
        val (nodos, aristas) = RedPersonas.calcular(minJuntos = 1, max = 400)
        return nodos.map { linkedMapOf<String, Any?>("persona" to it.nombre, "relacion" to it.relacion, "grupo" to it.grupo, "cercania" to it.cercania, "dias_mencion" to it.dias, "grado" to it.grado) } to
            aristas.map { linkedMapOf<String, Any?>("a" to it.a, "b" to it.b, "tipo_declarado" to it.tipo, "dias_juntos" to it.juntos) }
    }

    /** Tu gente (I-7): quién es cada persona, desde cuándo, sus fechas y su edad (hoy, o la que tenía al morir). */
    fun filasPersonas() = Repo.config.personas.sortedBy { it.nombre }.map {
        linkedMapOf<String, Any?>("persona" to it.nombre, "relacion" to it.relacion, "detalle" to it.detalle.ifBlank { null }, "cercania" to it.cercania,
            "desde" to it.desde.ifBlank { null }, "nacimiento" to it.nacimiento.ifBlank { null }, "fallecimiento" to it.fallecimiento.ifBlank { null },
            "edad" to Especiales.edadDe(it), "historial" to it.historial.sortedBy { h -> h.hasta }.map { h -> "${h.relacion}:${h.hasta}" })
    }

    /** Fechas propias (I-6): las que se repiten cada año y no son de nadie de Tu gente. */
    fun filasFechasPropias() = Repo.config.fechas_propias.sortedBy { Especiales.mesDia(it.fecha) }.map {
        linkedMapOf<String, Any?>("nombre" to it.nombre, "fecha" to it.fecha, "mes_dia" to Especiales.mesDia(it.fecha), "anio" to Especiales.anio(it.fecha), "tipo" to it.tipo)
    }

    fun filasExperimentos() = Repo.experimentos.sortedBy { it.inicio }.map {
        linkedMapOf<String, Any?>("id" to it.id, "nombre" to it.nombre, "pregunta" to it.pregunta, "hipotesis" to it.hipotesis, "sellada" to it.sellada, "accion" to it.accion,
            "resultado" to it.resultado, "diseno" to it.diseno, "inicio" to it.inicio, "semanas" to it.semanas, "lavado_dias" to it.lavado_dias,
            "plan" to it.plan.map { p -> if (p) 1 else 0 }, "activo" to it.activo, "plantilla" to it.plantilla, "fin" to it.fin,
            "hecho" to it.hecho.entries.sortedBy { e -> e.key }.map { (f, h) -> "$f:${if (h) 1 else 0}" })
    }

    fun filasNotas() = Repo.notas.sortedBy { it.creada }.map {
        linkedMapOf<String, Any?>("id" to it.id, "creada" to it.creada, "fecha" to it.fecha, "tipo" to it.tipo, "texto" to it.texto,
            "personas" to Texto.personas(it.texto), "temas" to Texto.temas(it.texto), "fijada" to it.fijada, "recordar_el" to it.recordar_el,
            "hecha" to it.hecha, "editada" to it.editada)
    }

    fun filasMetas(): List<Map<String, Any?>> {
        val panel = if (Repo.metas.any { it.medida_tipo == "variable" }) runCatching { io.github.ruddy2003.vestigium.ciencia.Estadistica.panel() }.getOrNull() else null
        return Repo.metas.sortedBy { it.creada }.map { m ->
            linkedMapOf<String, Any?>("id" to m.id, "nombre" to m.nombre, "detalle" to m.detalle, "horizonte" to m.horizonte, "creada" to m.creada,
                "fecha_objetivo" to m.fecha_objetivo, "estado" to m.estado, "medida_tipo" to m.medida_tipo, "medida_clave" to m.medida_clave,
                "medida_modo" to m.medida_modo, "umbral" to m.umbral, "objetivo" to m.objetivo, "valor_actual" to Metas.valor(m, panel = panel),
                "progreso" to Metas.progreso(m, panel = panel)?.let { Math.round(it * 1000) / 1000.0 }, "revision" to m.revision,
                "ultima_revision" to m.ultima_revision, "anio_proposito" to m.anio_proposito,
                "historial" to m.historial.map { "${it.fecha}:${it.campo}:${it.antes}>${it.despues}" })
        }
    }

    fun filasGastos() = Repo.gastos.map {
        linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "hora" to it.hora, "dia_logico" to it.dia, "monto" to it.monto, "categoria" to it.categoria,
            "subcategoria" to it.subcategoria.ifBlank { null }, "imprevisto" to it.imprevisto, "nota" to it.nota, "lat" to it.lat, "lon" to it.lon,
            "ts_creado" to it.ts_creado, "ts_editado" to it.ts_editado)
    }

    /** Lo marcado a mano en «Mundo»: ciudades añadidas, países rascados o tapados y sitios del Patrimonio Mundial. */
    fun filasMundo(): List<Map<String, Any?>> {
        val c = Repo.config
        return c.ciudades_visitadas.map { m ->
            linkedMapOf<String, Any?>("tipo" to "ciudad", "id" to m.id, "nombre" to m.nombre, "pais" to m.pais, "lat" to m.lat, "lon" to m.lon,
                "desde" to m.desde, "hasta" to m.hasta, "dias" to m.dias, "nota" to m.nota.ifBlank { null })
        } + c.paises_rascados.map { linkedMapOf<String, Any?>("tipo" to "pais_rascado", "id" to it, "nombre" to null, "pais" to it, "lat" to null, "lon" to null, "desde" to null, "hasta" to null, "dias" to null, "nota" to null) } +
            c.paises_tapados.map { linkedMapOf<String, Any?>("tipo" to "pais_tapado", "id" to it, "nombre" to null, "pais" to it, "lat" to null, "lon" to null, "desde" to null, "hasta" to null, "dias" to null, "nota" to null) } +
            c.unesco_marcados.map { linkedMapOf<String, Any?>("tipo" to "unesco", "id" to it.id.toString(), "nombre" to null, "pais" to null, "lat" to null, "lon" to null, "desde" to it.fecha, "hasta" to null, "dias" to null, "nota" to null) } +
            c.unesco_descartados.map { linkedMapOf<String, Any?>("tipo" to "unesco_no_cuenta", "id" to it.toString(), "nombre" to null, "pais" to null, "lat" to null, "lon" to null, "desde" to null, "hasta" to null, "dias" to null, "nota" to null) }
    }

    /** Las cartas. Las que siguen selladas van sin texto: no se pueden leer antes de su fecha. */
    fun filasCartas(): List<Map<String, Any?>> = Repo.cartas.sortedBy { it.escrita + it.hora }.map { c ->
        linkedMapOf<String, Any?>("id" to c.id, "escrita" to c.escrita, "hora" to c.hora.ifBlank { null }, "para" to c.para.ifBlank { null }, "abrir_en" to c.abrir_en,
            "abierta" to c.abierta, "abierta_el" to c.abierta_el, "lugar" to c.lugar.ifBlank { null }, "tema" to c.tema.ifBlank { null },
            "palabras" to Texto.palabras(c.texto), "texto" to if (c.legible) c.texto else null)
    }

    fun filasPerfil(): List<Map<String, Any?>> {
        val hoy = Fechas.diaLogico()
        return Perfiles.campos(Repo.config.perfil).map { (k, v) -> linkedMapOf<String, Any?>("tipo" to "actual", "fecha" to hoy, "campo" to k, "valor" to v.ifBlank { null }, "antes" to null, "despues" to null) } +
            Repo.config.perfil_historial.map { c -> linkedMapOf<String, Any?>("tipo" to "cambio", "fecha" to c.fecha, "campo" to c.campo, "valor" to null, "antes" to c.antes.ifBlank { null }, "despues" to c.despues.ifBlank { null }) }
    }

    fun filasPolitica(): List<Map<String, Any?>> = Repo.config.politica.sortedBy { it.fecha }.map { t ->
        val r = linkedMapOf<String, Any?>("fecha" to t.fecha)
        Politica.EJES.forEach { e -> r[e.id] = t.ejes[e.id] }
        Politica.AFIRMACIONES.forEach { a -> r["r_${a.id}"] = t.respuestas[a.id] }
        r
    }

    fun filasCitas() = Repo.obras.flatMap { o -> o.citas.map { c ->
        linkedMapOf<String, Any?>("id" to c.id, "obra_id" to o.id, "tipo" to o.tipo, "titulo" to o.titulo, "autor" to o.autor, "texto" to c.texto,
            "pagina" to c.pagina.ifBlank { null }, "fecha" to c.fecha.ifBlank { null }, "favorita" to c.favorita)
    } } + Repo.config.frases_guardadas.map { f ->
        linkedMapOf<String, Any?>("id" to null, "obra_id" to null, "tipo" to if (f.tema == "kindle") "kindle" else "frase_del_dia", "titulo" to null, "autor" to f.autor,
            "texto" to f.texto, "pagina" to null, "fecha" to f.fecha, "favorita" to true)
    }

    fun filasIngresos() = Repo.ingresos.map {
        linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "hora" to it.hora, "dia_logico" to it.dia, "monto" to it.monto, "categoria" to it.categoria,
            "fijo" to it.fijo, "nota" to it.nota, "ts_creado" to it.ts_creado, "ts_editado" to it.ts_editado)
    }

    fun filasEscalas() = Repo.respuestas.map { r ->
        val m = linkedMapOf<String, Any?>("id" to r.id, "fecha" to r.fecha, "ts" to r.ts, "instrumento" to r.instrumento)
        r.items.toSortedMap().forEach { (k, v) -> m["item_$k"] = v }
        r.puntajes.forEach { (k, v) -> m["puntaje_$k"] = v }
        m
    }

    fun filasReaccion() = Repo.reacciones.map {
        linkedMapOf<String, Any?>("id" to it.id, "ts" to it.ts, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "media_ms" to it.media_ms,
            "mediana_ms" to it.mediana_ms, "lapsos" to it.lapsos, "anticipaciones" to it.anticipaciones, "ensayos_ms" to it.ensayos_ms)
    }

    fun filasSalud() = Repo.salud.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "sueno_min" to it.sueno_min, "sueno_profundo_min" to it.sueno_profundo_min,
            "sueno_rem_min" to it.sueno_rem_min, "sueno_ligero_min" to it.sueno_ligero_min, "sueno_despierto_min" to it.sueno_despierto_min,
            "sueno_inicio" to it.sueno_inicio, "sueno_fin" to it.sueno_fin, "fc_media" to it.fc_media, "fc_min" to it.fc_min, "fc_max" to it.fc_max,
            "fc_reposo" to it.fc_reposo, "hrv_rmssd" to it.hrv_rmssd, "pasos" to it.pasos, "spo2" to it.spo2,
            "peso_kg" to it.peso_kg, "grasa_pct" to it.grasa_pct, "resp_rpm" to it.resp_rpm, "temp_c" to it.temp_c, "temp_piel_delta" to it.temp_piel_delta,
            "pa_sistolica" to it.pa_sistolica, "pa_diastolica" to it.pa_diastolica, "distancia_km" to it.distancia_km, "cal_activas" to it.cal_activas,
            "cal_totales" to it.cal_totales, "pisos" to it.pisos, "vo2max" to it.vo2max, "agua_ml" to it.agua_ml,
            "spo2_min" to it.spo2_min, "fc_sueno" to it.fc_sueno, "siesta_reloj_min" to it.siesta_reloj_min, "sueno_manual" to it.sueno_manual,
            "sueno_fuentes" to it.sueno_fuentes, "ventana_pasos" to it.ventana_pasos, "sueno_descartadas" to it.sueno_descartadas)
    }

    fun filasPantalla() = Repo.pantalla.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "total_min" to it.total_min, "desbloqueos" to it.desbloqueos,
            "primer_uso" to it.primer_uso, "ultimo_uso" to it.ultimo_uso,
            "top_apps" to it.por_app_min.entries.take(10).map { e -> "${e.key}:${e.value}" },
            "cama_min" to it.cama_min, "dormir_tras_tel_min" to it.dormir_tras_tel_min, "tel_tras_despertar_min" to it.tel_tras_despertar_min,
            "por_hora_min" to it.por_hora_min, "ventana" to it.ventana)
    }

    fun filasVisitas() = Repo.visitas.map { v ->
        val l = Repo.lugares.firstOrNull { it.id == v.lugar }
        linkedMapOf<String, Any?>("lugar" to l?.nombre, "tipo" to l?.tipo, "entrada" to v.entrada, "salida" to v.salida,
            "minutos" to v.salida?.let { (Fechas.millis(it) - Fechas.millis(v.entrada)) / 60000 })
    }

    fun filasEntorno() = Repo.entorno.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "t_max" to it.t_max, "t_min" to it.t_min, "t_media" to it.t_media, "sensacion_max" to it.sensacion_max,
            "precip_mm" to it.precip_mm, "horas_lluvia" to it.horas_lluvia, "nubes_pct" to it.nubes_pct, "humedad_pct" to it.humedad_pct,
            "presion_hpa" to it.presion_hpa, "viento_max" to it.viento_max, "uv_max" to it.uv_max, "codigo_wmo" to it.codigo,
            "cielo" to it.codigo?.let { c -> Clima.descripcion(c) }, "amanecer" to it.amanecer, "atardecer" to it.atardecer,
            "luz_dia_min" to it.luz_min, "sol_min" to it.sol_min, "pm25" to it.pm25, "pm10" to it.pm10, "aqi_us" to it.aqi_us,
            "luna_fase" to it.luna_fase, "luna_ilum" to it.luna_ilum, "lat_aprox" to it.lat, "lon_aprox" to it.lon)
    }

    fun filasFotos() = Repo.fotos.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "fotos" to it.fotos, "capturas" to it.capturas, "otras" to it.otras, "videos" to it.videos,
            "primera" to it.primera, "ultima" to it.ultima)
    }

    fun filasAgenda() = Repo.agenda.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "eventos" to it.eventos, "todo_el_dia" to it.todo_el_dia, "min_ocupado" to it.min_ocupado,
            "primero" to it.primero, "ultimo" to it.ultimo, "titulos" to it.titulos)
    }

    fun filasNotifs(): List<Map<String, Any?>> {
        val apps = Repo.notifs.values.flatMap { it.por_app.entries }.groupBy({ it.key }, { it.value }).mapValues { it.value.sum() }
            .entries.sortedByDescending { it.value }.take(12).map { it.key }
        return Repo.notifs.values.sortedBy { it.fecha }.map { n ->
            val m = linkedMapOf<String, Any?>("fecha" to n.fecha, "total" to n.total)
            n.por_hora.forEachIndexed { h, v -> m["h%02d".format(h)] = v }
            apps.forEach { a -> m["app_" + Texto.slug(a)] = n.por_app[a] ?: 0 }
            m["por_app"] = n.por_app.entries.sortedByDescending { it.value }.map { "${it.key}:${it.value}" }
            m
        }
    }

    fun filasMusica() = Repo.escuchas("0000-01-01", "9999-12-31", todas = true).map {
        linkedMapOf<String, Any?>("ts" to it.ts, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "titulo" to it.titulo, "artista" to it.artista,
            "album" to it.album, "app" to it.app, "duracion_s" to it.duracion_s, "ms_escuchado" to it.ms, "saltada" to it.saltada, "fuente" to it.fuente, "contexto" to it.contexto, "es_musica" to it.esMusica)
    }

    fun filasComunicacion() = Repo.comunicacion.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "entrantes" to it.entrantes, "salientes" to it.salientes, "perdidas" to it.perdidas,
            "rechazadas" to it.rechazadas, "min_hablado" to it.min_hablado, "llamada_max_min" to it.llamada_max_min,
            "sms_recibidos" to it.sms_recibidos, "sms_enviados" to it.sms_enviados, "contactos" to it.contactos, "primera" to it.primera, "ultima" to it.ultima)
    }

    fun filasConexiones(): List<Map<String, Any?>> {
        val redes = Repo.conexiones.values.flatMap { it.wifi_min.entries }.groupBy({ it.key }, { it.value }).mapValues { it.value.sum() }
            .entries.sortedByDescending { it.value }.take(6).map { it.key }
        return Repo.conexiones.values.sortedBy { it.fecha }.map { c ->
            val m = linkedMapOf<String, Any?>("fecha" to c.fecha, "muestras" to c.muestras, "sin_wifi_min" to c.sin_wifi_min,
                "wifi_total_min" to c.wifi_min.values.sum())
            redes.forEach { r -> m["wifi_" + Texto.slug(r).ifEmpty { "red" }] = c.wifi_min[r] ?: 0 }
            m["audifonos_min"] = c.audio_min["audifonos"] ?: 0; m["coche_min"] = c.audio_min["coche"] ?: 0
            m["bocina_min"] = c.audio_min["bocina"] ?: 0; m["bluetooth_otro_min"] = c.audio_min["bluetooth"] ?: 0
            m["musica_min"] = c.musica_min; m["cargando_min"] = c.cargando_min
            m["carga_noche_inicio"] = c.carga_noche_inicio; m["carga_noche_fin"] = c.carga_noche_fin
            m["bateria_media"] = if (c.muestras > 0 && c.bateria_suma > 0) Math.round(c.bateria_suma / c.muestras * 10) / 10.0 else null
            m["bateria_min"] = c.bateria_min
            val n = c.luz_n.sum()
            m["luz_media"] = if (n > 0) Math.round(c.luz_suma.sum() / n * 10) / 10.0 else null
            m["luz_max"] = c.luz_max; m["luz_alta_min"] = c.luz_alta_min
            m["presion_media"] = if (c.presion_n > 0) Math.round(c.presion_suma / c.presion_n * 100) / 100.0 else null
            m["presion_min"] = c.presion_min; m["presion_max"] = c.presion_max; m["cambio_presion_3h"] = c.cambio_presion_3h
            for (h in 0 until 24) m["luz_h%02d".format(h)] = c.luz_n.getOrNull(h)?.takeIf { it > 0 }?.let { k -> Math.round((c.luz_suma.getOrNull(h) ?: 0.0) / k * 10) / 10.0 }
            m["dispositivos"] = c.dispositivos_min.entries.sortedByDescending { it.value }.map { "${it.key}:${it.value}" }
            m["redes"] = c.wifi_min.entries.sortedByDescending { it.value }.map { "${it.key}:${it.value}" }
            m
        }
    }

    fun filasSubrayados() = Repo.subrayados.map {
        linkedMapOf<String, Any?>("id" to it.id, "libro" to it.libro, "autor" to it.autor, "tipo" to it.tipo, "ubicacion" to it.ubicacion,
            "pagina" to it.pagina, "ts" to it.ts, "fecha" to it.fecha, "palabras" to Texto.palabras(it.texto), "texto" to it.texto)
    }

    fun filasGoogleDia() = Repo.huellas.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "yt_vistos" to it.yt_vistos, "yt_busquedas" to it.yt_busquedas, "busquedas" to it.busquedas,
            "visitas" to it.visitas, "otras" to it.otras, "primera" to it.primera, "ultima" to it.ultima,
            "canales" to it.canales.entries.map { e -> "${e.key}:${e.value}" })
    }

    fun filasYoutube() = Repo.youtube.sortedWith(compareBy({ it.tipo }, { it.ts })).map {
        linkedMapOf<String, Any?>("tipo" to it.tipo, "ts" to it.ts.ifBlank { null }, "fecha" to it.fecha.ifBlank { null }, "hora" to it.ts.takeIf { t -> t.isNotBlank() }?.let { t -> Fechas.horaDe(t) },
            "video_id" to it.video.ifBlank { null }, "canal" to it.canal.ifBlank { null }, "lista" to it.lista.ifBlank { null })
    }

    fun filasGoogle() = Repo.huellasDe("0000-01-01", "9999-12-31").sortedBy { it.ts }.map {
        linkedMapOf<String, Any?>("ts" to it.ts, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "producto" to it.producto, "accion" to it.accion,
            "titulo" to it.titulo, "canal" to it.canal)
    }

    fun filasNutricion(): List<Map<String, Any?>> {
        val cols = Repo.nutricion.values.flatMap { it.valores.keys }.toSortedSet(compareBy<String> { orden(it) }.thenBy { it })
        return Repo.nutricion.values.sortedBy { it.fecha }.map { n ->
            val m = linkedMapOf<String, Any?>("fecha" to n.fecha)
            cols.forEach { m[it] = n.valores[it] }
            m["comidas"] = n.comidas; m["primera_comida"] = n.primera; m["ultima_comida"] = n.ultima; m["cafeina_ultima"] = n.cafeina_ultima
            m
        }
    }
    // Las columnas principales primero y los micronutrientes después.
    private val PRINCIPALES = listOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "cafeina_mg", "azucares_g", "sodio_mg", "agua_g",
        "gasto_kcal", "peso_mf_kg", "peso_tendencia_kg", "pasos_mf", "objetivo_kcal", "objetivo_proteina_g", "objetivo_carbohidratos_g", "objetivo_grasa_g")
    private fun orden(k: String) = PRINCIPALES.indexOf(k).let { if (it < 0) 999 else it }

    /** Gramos de un alimento: la cantidad si la porción es «g»; si no, cantidad × peso de la porción. Null si se registró en ml. */
    fun gramos(c: Comida): Double? = when {
        c.porcion.equals("ml", true) -> null
        c.porcion.equals("g", true) -> c.cantidad
        c.cantidad != null && c.peso_porcion_g != null -> Math.round(c.cantidad * c.peso_porcion_g * 10) / 10.0
        else -> c.peso_porcion_g
    }

    fun filasComidas(): List<Map<String, Any?>> {
        val l = Repo.comidas("0000-01-01", "9999-12-31")
        val cols = l.flatMap { it.valores.keys }.toSortedSet(compareBy<String> { orden(it) }.thenBy { it })
        return l.map { c ->
            val m = linkedMapOf<String, Any?>("fecha" to c.fecha, "hora" to c.hora.takeIf { it.matches(Regex("\\d{2}:\\d{2}")) }, "alimento" to c.alimento,
                "porcion" to c.porcion, "cantidad" to c.cantidad, "peso_porcion_g" to c.peso_porcion_g,
                "gramos" to gramos(c), "mililitros" to if (c.porcion.equals("ml", true)) c.cantidad else null)
            cols.forEach { m[it] = c.valores[it] }
            m
        }
    }

    fun filasEntrenos(): List<Map<String, Any?>> {
        val mus = Repo.entrenos.values.flatMap { it.series_musculo.keys + it.volumen_musculo.keys }.toSortedSet()
        return Repo.entrenos.values.sortedBy { it.fecha }.map { e ->
            val m = linkedMapOf<String, Any?>("fecha" to e.fecha, "entrenos" to e.entrenos, "minutos" to e.minutos, "series" to e.series,
                "series_efectivas" to e.series_efectivas, "reps" to e.reps, "volumen_kg" to e.volumen_kg)
            mus.forEach { g -> m["series_$g"] = e.series_musculo[g] }
            mus.forEach { g -> m["volumen_${g}_kg"] = e.volumen_musculo[g] }
            m
        }
    }

    fun filasSeries() = Repo.series("0000-01-01", "9999-12-31").map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "orden" to it.orden, "entreno" to it.entreno, "entreno_min" to it.entreno_s?.let { s -> Math.round(s / 6.0) / 10.0 },
            "ejercicio" to it.ejercicio, "tipo_serie" to it.tipo, "calentamiento" to it.tipo.contains("Warm", true), "peso_kg" to it.peso_kg, "reps" to it.reps,
            "rir" to it.rir, "e1rm_kg" to Macrofactor.e1rm(it), "duracion_s" to it.duracion_s, "distancia_m" to it.distancia_m, "peso_base_kg" to it.peso_base_kg)
    }

    fun filasWhatsapp() = Repo.chats.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "chat" to it.chat, "grupo" to it.grupo, "mios" to it.mios, "otros" to it.otros,
            "palabras_mias" to it.palabras_mias, "palabras_otros" to it.palabras_otros, "multimedia" to it.multimedia, "primero" to it.primero,
            "ultimo" to it.ultimo, "resp_mia_min" to it.resp_mia_min, "resp_otros_min" to it.resp_otros_min, "inicie" to it.inicie, "conversaciones" to it.conversaciones)
    }

    fun filasActividad() = Repo.actividad.values.sortedBy { it.fecha }.map { a ->
        linkedMapOf<String, Any?>("fecha" to a.fecha, "a_pie_min" to (a.minutos["a_pie"] ?: 0), "corriendo_min" to (a.minutos["corriendo"] ?: 0),
            "bici_min" to (a.minutos["bici"] ?: 0), "vehiculo_min" to (a.minutos["vehiculo"] ?: 0), "quieto_min" to (a.minutos["quieto"] ?: 0))
    }

    fun filasEjercicio() = Repo.sesiones.map {
        linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "inicio" to it.inicio, "fin" to it.fin, "tipo" to it.tipo_nombre,
            "tipo_hc" to it.tipo, "titulo" to it.titulo, "min" to it.min, "fc_media" to it.fc_media, "fc_max" to it.fc_max)
    }

    fun filasEscaladaSesiones() = Repo.ascensos.map { a ->
        val g = a.bloques.filter { Grados.encadenado(it.estilo) }.mapNotNull { Grados.valor(it.grado) }
        linkedMapOf<String, Any?>("id" to a.id, "fecha" to a.fecha, "lugar" to a.lugar, "modalidad" to a.modalidad, "inicio" to a.inicio, "min" to a.min,
            "bloques" to a.bloques.size, "encadenados" to a.bloques.count { Grados.encadenado(it.estilo) },
            "flashes" to a.bloques.count { it.estilo == "flash" || it.estilo == "a_vista" }, "intentos" to a.bloques.sumOf { it.intentos },
            "grado_max" to g.maxOrNull()?.let { Grados.nombre(it, a.bloques.firstOrNull()?.grado) }, "grado_max_num" to g.maxOrNull(),
            "grado_medio_num" to if (g.isEmpty()) null else g.average(), "energia" to a.energia, "piel" to a.piel, "esfuerzo" to a.esfuerzo,
            "satisfaccion" to a.satisfaccion, "dolor" to a.dolor, "nota" to a.nota, "sesion_reloj" to a.sesion_reloj)
    }

    fun filasEscaladaBloques() = Repo.ascensos.flatMap { a ->
        a.bloques.mapIndexed { i, b ->
            linkedMapOf<String, Any?>("sesion" to a.id, "fecha" to a.fecha, "lugar" to a.lugar, "orden" to i + 1, "grado" to b.grado,
                "grado_num" to Grados.valor(b.grado), "escala" to Grados.escala(b.grado), "estilo" to b.estilo, "encadenado" to Grados.encadenado(b.estilo),
                "intentos" to b.intentos, "angulo" to b.angulo, "rasgos" to b.rasgos, "nota" to b.nota)
        }
    }

    fun filasBiblioteca() = Repo.obras.sortedBy { it.agregado }.map {
        linkedMapOf<String, Any?>("id" to it.id, "tipo" to it.tipo, "titulo" to it.titulo, "autor" to it.autor, "anio" to it.anio, "estado" to it.estado,
            "inicio" to it.inicio, "fin" to it.fin, "calificacion" to it.calificacion, "progreso" to it.progreso, "total" to it.total,
            "veces" to it.veces, "etiquetas" to it.etiquetas, "nota" to it.nota, "agregado" to it.agregado,
            "revista" to it.revista.ifBlank { null }, "doi" to it.doi.ifBlank { null }, "fuente" to it.fuente, "dias_marcados" to it.dias.size, "fechas_marcadas" to it.dias,
            "citas" to it.citas.size,
            "dias" to if (it.inicio != null && it.fin != null) java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(it.inicio), Fechas.parse(it.fin)) + 1 else null)
    }

    /** Una fila por día con todo lo que se sabe de él: el archivo más cómodo para empezar en R. */
    fun filasPanel(): List<Map<String, Any?>> {
        val fechas = (Repo.dias.keys + Repo.salud.keys + Repo.pantalla.keys + Repo.entorno.keys + Repo.momentos.map { it.fecha } + Repo.gastos.map { it.fecha } + Repo.ingresos.map { it.dia } +
            Repo.nutricion.keys + Repo.entrenos.keys).toSortedSet()
        // Respondido = hay un registro del aviso, tenga o no valencia (la misma regla en el diccionario, Explorar y los widgets).
        val mu = Repo.momentos.filter { it.tipo == "muestreo" }.groupBy { it.fecha }
        val mo = Repo.momentos.filter { it.tipo != "muestreo" }.groupBy { it.fecha }
        val ga = Repo.gastos.groupBy { it.dia }
        val ing = Repo.ingresos.groupBy { it.dia }
        val ej = Repo.sesiones.groupBy { it.fecha }
        val es = Repo.ascensos.groupBy { it.fecha }
        val km = runCatching { io.github.ruddy2003.vestigium.ciencia.Movilidad.kmPorDia() }.getOrDefault(emptyMap())
        val wa0 = Repo.chats.groupBy { it.fecha }
        val sub0 = Repo.subrayados.filter { it.fecha != null }.groupingBy { it.fecha!! }.eachCount()
        val can0 = runCatching { Repo.escuchas("0000-01-01", "9999-12-31").filter { (it.ms ?: 30_000) >= 30_000 }.groupingBy { it.fecha }.eachCount() }.getOrDefault(emptyMap())
        // Cultura, mundo, cartas y cercanos: se calculan igual que en los análisis.
        val nuevas = listOf("obras_dia", "lectura", "vi_pelicula_serie", "jugue", "obra_terminada", "calificacion_obra", "citas_dia", "ciudades_dia", "fuera_de_casa",
            "extranjero", "ciudad_nueva", "unesco", "carta_escrita", "sueno_palabras", "cercanos", "vi_familia", "vi_pareja", "vi_amistades", "vi_convivientes",
            "vi_expareja", "vi_trabajo", "cercania_media", "personas_vistas", "personas_pensadas", "personas_mensajes",
            "penso_en_pareja", "penso_en_expareja", "penso_en_familia", "penso_en_amistades", "animo_valencia", "animo_activacion", "registros_animo",
            "alcohol", "alcohol_bebidas", "alcohol_nivel_max", "alcohol_horas", "alcohol_estimado_max", "alcohol_al_dormir", "cannabis", "cannabis_nivel_max", "cannabis_horas",
            "tabaco_n", "otra_sustancia", "cruda", "cafeina_total_mg", "cafeina_al_dormir_mg",
            "animo_cierre_valencia", "animo_cierre_activacion", "presion_local_hpa", "cambio_presion_3h",
            "avisos_planeados", "avisos_mostrados", "avisos_respondidos", "tasa_respuesta") +
            Repo.config.campanas.map { io.github.ruddy2003.vestigium.ciencia.Campanas.variable(it.reto) }.distinct().sorted() +
            (if (Repo.config.campanas.isNotEmpty()) listOf("campanas_n") else emptyList()) +
            Repo.lugares.map { "horas_tipo_${it.tipo}" }.distinct().sorted()
        // Lo que sentías y pensabas (una columna por sentimiento o pensamiento que hayas registrado alguna vez).
        val emociones = (Repo.momentos.flatMap { Catalogo.emocionesDe(it) }.filter { it != Catalogo.OTRO } + Repo.momentos.mapNotNull { Catalogo.pensamientoDe(it) }).distinct().sorted()
        val est = runCatching { io.github.ruddy2003.vestigium.ciencia.Estadistica.panel() }.getOrDefault(emptyMap())
        val hayEspeciales = Especiales.hay()
        // Festivos solo si vives en México (I-1); el día libre es la misma regla del ritmo de sueño (P-19), y R la lee de aquí.
        val mexico = Repo.config.perfil.pais.equals("MX", ignoreCase = true)
        val filas = fechas.map { f ->
            val d = Repo.dias[f]; val s = Repo.salud[f]; val p = Repo.pantalla[f]; val e = Repo.entorno[f]
            val ft = Repo.fotos[f]; val ag = Repo.agenda[f]; val nt = Repo.notifs[f]; val ac = Repo.actividad[f]
            val r = linkedMapOf<String, Any?>("fecha" to f, "dia_semana" to Fechas.parse(f).dayOfWeek.value,
                "festivo" to (if (mexico) Fechas.esFestivoMx(f) else null), "dia_libre" to Fechas.esDiaLibre(f, mexico), "con_diario" to (d != null),
                "valencia" to d?.valencia, "activacion" to d?.activacion, "dolor" to d?.dolor, "sueno_calidad" to d?.sueno_calidad, "version_noche" to d?.version_noche,
                "valencia_muestreo" to mu[f]?.mapNotNull { it.valencia }?.average(), "n_muestreos" to (mu[f]?.size ?: 0),
                "n_momentos" to (mo[f]?.size ?: 0))
            Catalogo.MOMENTOS.forEach { t -> r["m_${t.id}"] = mo[f]?.count { it.tipo == t.id } ?: 0 }
            r["gasto_total"] = ga[f]?.sumOf { it.monto } ?: 0.0
            r["ingreso_total"] = ing[f]?.sumOf { it.monto } ?: 0.0
            r["palabras"] = d?.let { Texto.palabras(it.nota) }
            r["sueno_min"] = s?.sueno_min; r["sueno_profundo_min"] = s?.sueno_profundo_min; r["sueno_rem_min"] = s?.sueno_rem_min
            r["fc_reposo"] = s?.fc_reposo; r["hrv_rmssd"] = s?.hrv_rmssd; r["pasos"] = s?.pasos
            r["pantalla_min"] = p?.total_min; r["desbloqueos"] = p?.desbloqueos; r["ventana_pantalla"] = p?.ventana; r["ventana_pasos"] = s?.takeIf { it.pasos != null }?.ventana_pasos; r["primer_uso"] = p?.primer_uso?.let { Fechas.horaDe(it) }; r["ultimo_uso"] = p?.ultimo_uso?.let { Fechas.horaDe(it) }
            r["t_max"] = e?.t_max; r["t_min"] = e?.t_min; r["precip_mm"] = e?.precip_mm; r["nubes_pct"] = e?.nubes_pct; r["luz_dia_min"] = e?.luz_min
            r["sol_min"] = e?.sol_min; r["aqi_us"] = e?.aqi_us; r["pm25"] = e?.pm25; r["luna_ilum"] = e?.luna_ilum ?: Luna.iluminacion(Luna.fase(f))
            r["fotos"] = ft?.fotos; r["eventos"] = ag?.eventos; r["min_ocupado"] = ag?.min_ocupado; r["notificaciones"] = nt?.total
            r["a_pie_min"] = ac?.minutos?.get("a_pie"); r["vehiculo_min"] = ac?.minutos?.get("vehiculo")
            r["ejercicio_min"] = ej[f]?.sumOf { it.min }; r["escalada"] = es[f] != null
            r["km"] = km[f]
            r["primer_uso_h"] = p?.primer_uso?.let { horaDecimal(it, false) }; r["ultimo_uso_h"] = p?.ultimo_uso?.let { horaDecimal(it, true) }
            r["sueno_inicio_h"] = s?.sueno_inicio?.let { horaDecimal(it, true) }; r["sueno_fin_h"] = s?.sueno_fin?.let { horaDecimal(it, false) }
            r["peso_kg"] = s?.peso_kg; r["temp_piel_delta"] = s?.temp_piel_delta; r["resp_rpm"] = s?.resp_rpm; r["cal_activas"] = s?.cal_activas
            val co = Repo.comunicacion[f]
            r["llamadas"] = co?.let { it.entrantes + it.salientes }; r["min_hablado"] = co?.min_hablado; r["sms"] = co?.let { it.sms_enviados + it.sms_recibidos }
            r["contactos"] = co?.contactos
            val wa = wa0[f]
            r["wa_mios"] = wa?.sumOf { it.mios }; r["wa_otros"] = wa?.sumOf { it.otros }; r["wa_chats"] = wa?.size
            val cx = Repo.conexiones[f]
            r["audifonos_min"] = cx?.audio_min?.get("audifonos"); r["coche_min"] = cx?.audio_min?.get("coche"); r["musica_min"] = cx?.musica_min
            r["sin_wifi_min"] = cx?.sin_wifi_min; r["luz_alta_min"] = cx?.luz_alta_min; r["carga_noche_inicio_h"] = cx?.carga_noche_inicio?.let { horaDecimal(it, true) }
            val hu = Repo.huellas[f]
            r["yt_vistos"] = hu?.yt_vistos; r["busquedas"] = hu?.let { it.busquedas + it.yt_busquedas }
            r["subrayados"] = sub0[f]
            r["canciones"] = can0[f]
            r["ruido_db_medio"] = mo[f]?.mapNotNull { it.ruido_db }?.takeIf { it.isNotEmpty() }?.average()
            val nu = Repo.nutricion[f]; val en = Repo.entrenos[f]
            listOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "cafeina_mg", "gasto_kcal", "peso_tendencia_kg").forEach { k -> r[k] = nu?.valores?.get(k) }
            r["comidas"] = nu?.comidas?.takeIf { it > 0 }
            r["primera_comida_h"] = nu?.primera?.let { horaTexto(it) }; r["ultima_comida_h"] = nu?.ultima?.let { horaTexto(it) }
            r["cafeina_ultima_h"] = nu?.cafeina_ultima?.let { horaTexto(it) }
            r["gimnasio"] = en != null && en.series > 0; r["gimnasio_min"] = en?.minutos; r["series_gimnasio"] = en?.series_efectivas; r["volumen_kg"] = en?.volumen_kg
            val x = est[f]
            nuevas.forEach { k -> r[k] = x?.get(k) }
            // Días especiales (I-6): si cae alguno y de qué tipo (mi_cumpleanos, cumpleanos, fallecimiento, aniversario, otra).
            if (hayEspeciales) Especiales.de(f).let { l -> r["dia_especial"] = l.isNotEmpty(); r["dia_especial_tipo"] = l.map { it.tipo }.distinct() }
            emociones.forEach { k -> r["n_$k"] = x?.get("n_$k") }
            r
        }
        if (est.isNotEmpty()) comoLaApp(filas, est)
        return filas
    }

    /**
     * Las columnas de vm_panel.csv que en el panel de la app tienen otro nombre (P-30): exportada → (de la app, factor).
     * No se renombran, para no romper scripts viejos; el diccionario y vm_familias.csv dicen cuál es cuál.
     */
    val EQUIVALENTES: Map<String, Pair<String, Double>> = linkedMapOf(
        "sueno_min" to ("sueno_horas" to 60.0), "sueno_profundo_min" to ("sueno_profundo" to 1.0), "sueno_rem_min" to ("sueno_rem" to 1.0),
        "hrv_rmssd" to ("hrv" to 1.0), "km" to ("distancia_km" to 1.0), "valencia_muestreo" to ("muestreo_valencia" to 1.0),
        "luz_dia_min" to ("luz_horas" to 60.0), "sol_min" to ("sol_horas" to 60.0), "carga_noche_inicio_h" to ("carga_noche_h" to 1.0),
        "temp_piel_delta" to ("temp_piel" to 1.0), "ruido_db_medio" to ("ruido_db" to 1.0))

    /**
     * R analiza el mismo panel que la app (P-30): en cada columna que comparte nombre con el panel de la app (o es su
     * equivalente directo, ver [EQUIVALENTES]), el valor es el de la app, con sus reglas de cero y vacío (P-15) y su hora
     * de último uso (+24 solo antes de las 5:00, P-16). Se conserva el tipo de la columna (0/1, entero o número) para no
     * cambiar cómo la lee R. Las equivalencias con otra unidad (minutos frente a horas) se dejan como estaban: salen de la
     * misma fuente.
     */
    private fun comoLaApp(filas: List<MutableMap<String, Any?>>, est: Map<String, Map<String, Double?>>) {
        val deLaApp = HashSet<String>()
        est.values.forEach { deLaApp.addAll(it.keys) }
        io.github.ruddy2003.vestigium.ciencia.Estadistica.variables(dinamicas = false, privadas = true).forEach { deLaApp.add(it.id) }
        val cols = LinkedHashSet<String>(); filas.forEach { cols.addAll(it.keys) }
        val fuente = cols.filter { it != "fecha" }.mapNotNull { c ->
            when {
                c in deLaApp -> c to c
                EQUIVALENTES[c]?.second == 1.0 -> c to EQUIVALENTES[c]!!.first
                else -> null
            }
        }.toMap()
        for ((c, k) in fuente) {
            val antes = filas.firstNotNullOfOrNull { it[c] }
            for (r in filas) {
                val v = est[r["fecha"] as String]?.get(k)
                r[c] = when {
                    v == null -> null
                    antes is Boolean -> v == 1.0
                    (antes is Int || antes is Long) && v == Math.rint(v) && kotlin.math.abs(v) < Int.MAX_VALUE -> v.toInt()
                    else -> v
                }
            }
        }
    }

    /**
     * El panel extendido: todo lo del panel, más una columna por cada persona, tema, lugar, app, categoría, artista, canal,
     * obra, ciudad, país, músculo, alimento y nutriente (vitaminas y minerales incluidos), los cuestionarios en los días que
     * cubren, las etapas de vida, la brújula política, y todas las columnas de las tablas diarias (reloj, clima, pantalla,
     * notificaciones, conexiones, Google, nutrición, gimnasio…). Las columnas repetidas se dejan una sola vez.
     */
    fun filasPanelExtendido(): List<Map<String, Any?>> {
        val filas = java.util.TreeMap<String, LinkedHashMap<String, Any?>>()
        filasPanel().forEach { r -> filas[r["fecha"] as String] = LinkedHashMap(r) }
        val est = runCatching { io.github.ruddy2003.vestigium.ciencia.Estadistica.panel(dinamicas = true, extendido = true) }.getOrDefault(emptyMap())
        for ((f, r) in est) {
            val fila = filas.getOrPut(f) { linkedMapOf<String, Any?>("fecha" to f) }
            // Todas las del panel de la app, con su nombre: R cruza las mismas (vm_variables.csv). Los nutrientes, los minutos por
            // app y por movimiento salen además completos de sus tablas (abajo), con su propio nombre.
            r.forEach { (k, v) -> if (k !in fila) fila[k] = v }
        }
        fun igual(a: Any?, b: Any?): Boolean = when {
            a == null || b == null -> a == b
            a is Number && b is Number -> kotlin.math.abs(a.toDouble() - b.toDouble()) < 1e-6
            a is Boolean && b is Number -> (if (a) 1.0 else 0.0) == b.toDouble()
            b is Boolean && a is Number -> (if (b) 1.0 else 0.0) == a.toDouble()
            else -> a.toString() == b.toString()
        }
        val tablas = listOf("salud_" to filasSalud(), "pant_" to filasPantalla(), "clima_" to filasEntorno(), "fotos_" to filasFotos(), "agenda_" to filasAgenda(),
            "notif_" to filasNotifs(), "mov_" to filasActividad(), "com_" to filasComunicacion(), "cx_" to filasConexiones(), "google_" to filasGoogleDia(),
            "nutr_" to filasNutricion(), "gym_" to filasEntrenos())
        for ((pre, t) in tablas) {
            if (t.isEmpty()) continue
            val usados = HashSet<String>(); filas.values.forEach { usados.addAll(it.keys) }
            val cols = LinkedHashSet<String>(); t.forEach { cols.addAll(it.keys) }; cols.remove("fecha")
            for (c in cols) {
                // Ya está con el mismo nombre y los mismos valores: no se repite.
                if (c in usados && t.all { fr -> igual(filas[fr["fecha"] as String]?.get(c), fr[c]) }) continue
                val forzar = pre == "notif_" && (c.startsWith("app_") || Regex("^h\\d{2}$").matches(c))
                val nombre = if (forzar || c in usados || c in setOf("total", "primera", "ultima", "primero", "ultimo", "otras", "visitas", "muestras", "series", "reps", "minutos")) pre + c else c
                for (fr in t) {
                    val f = fr["fecha"] as String
                    filas.getOrPut(f) { linkedMapOf<String, Any?>("fecha" to f) }[nombre] = fr[c]
                }
            }
        }
        // Minutos en cada app (todas las que pasan de una hora en total), además de las principales.
        val apps = Repo.pantalla.values.flatMap { it.por_app_min.entries }.groupBy({ it.key }, { it.value }).mapValues { it.value.sum() }.filter { it.value >= 60 }.keys
        for ((f, p) in Repo.pantalla) {
            val fila = filas.getOrPut(f) { linkedMapOf<String, Any?>("fecha" to f) }
            apps.forEach { a -> val k = "app_min_" + Texto.slug(a).ifEmpty { "app" }; if (k !in fila) fila[k] = p.por_app_min[a] ?: 0 }
        }
        return filas.values.toList()
    }

    /**
     * vm_familias.csv (P-31): las parejas que la app esconde por obvias, escritas como familias (ver [Estadistica.familias]) con los
     * nombres del panel extendido. vm_obvia en R las lee: dos variables son obvias si comparten una familia con lado 0, o con lados
     * distintos (1 y 2).
     */
    fun filasFamilias(): List<Map<String, Any?>> {
        val vars = (io.github.ruddy2003.vestigium.ciencia.Estadistica.variables(dinamicas = false, privadas = true) +
            runCatching { io.github.ruddy2003.vestigium.ciencia.Dinamicas.calcular(true).variables }.getOrDefault(emptyList())).distinctBy { it.id }
        val alias = EQUIVALENTES.entries.groupBy({ it.value.first }, { it.key })
        return io.github.ruddy2003.vestigium.ciencia.Estadistica.familias(vars, alias).flatMap { f ->
            f.lados.entries.sortedBy { it.value }.map { (v, lado) -> linkedMapOf<String, Any?>("familia" to f.nombre, "variable" to v, "lado" to lado) }
        }
    }

    /**
     * vm_variables.csv (P-30): las variables que la app cruza en «Las relaciones más marcadas» y en «Todo lo que se mueve», con su
     * nombre y su grupo. Las privadas van marcadas: la app nunca las cruza; en R se cruzan solo si se pide (privadas = TRUE).
     */
    fun filasVariables(): List<Map<String, Any?>> {
        val ligado = Privado.habito()?.id
        return io.github.ruddy2003.vestigium.ciencia.Estadistica.variables(dinamicas = true, privadas = true).distinctBy { it.id }.map {
            linkedMapOf<String, Any?>("variable" to it.id, "nombre" to it.nombre, "grupo" to it.grupo, "privada" to Privado.esVariable(it.id, ligado))
        }
    }

    /** Hora como número (7.5 = 7:30). Para horas de dormir, la madrugada cuenta como 24+ (1:00 = 25). */
    fun horaDecimal(iso: String, noche: Boolean): Double? = runCatching {
        val t = java.time.OffsetDateTime.parse(iso).atZoneSameInstant(Fechas.zona())
        val h = t.hour + t.minute / 60.0
        Math.round((if (noche && h < 12) h + 24 else h) * 100) / 100.0
    }.getOrNull()

    fun horaTexto(hhmm: String): Double? = runCatching { val (h, m) = hhmm.split(":").map { it.toInt() }; Math.round((h + m / 60.0) * 100) / 100.0 }.getOrNull()

    fun csvUbicaciones(): String {
        val sb = StringBuilder("﻿ts,lat,lon,precision_m,fuente,ms\n")
        Repo.db().ubicaciones().forEach { sb.append(Fechas.isoDeMillis(it.ms)).append(',').append(it.lat).append(',').append(it.lon).append(',')
            .append(it.precision ?: "").append(',').append(it.fuente ?: "").append(',').append(it.ms).append('\n') }
        return sb.toString()
    }

    /** El catálogo de fotos con lugar (solo hora, coordenadas y de qué aparato; nunca la foto). Sirve para restaurar en otro teléfono. */
    fun csvCatalogoFotos(): String {
        val sb = StringBuilder("﻿clave,ms,ts,lat,lon,aparato,origen,ms_archivo,fuente\n")
        Repo.db().recorrerFotos { f ->
            sb.append(celda(f.clave)).append(',').append(f.ms).append(',').append(Fechas.isoDeMillis(f.ms)).append(',').append(f.lat).append(',').append(f.lon).append(',')
                .append(celda(f.aparato)).append(',').append(celda(f.origen)).append(',').append(f.msArchivo).append(',').append(celda(f.fuente)).append('\n')
        }
        return sb.toString()
    }

    fun respaldoJson(): String = JSON.encodeToString(Respaldo.serializer(), Respaldo(
        exportado = Fechas.ahoraIso(), schema_v = SCHEMA_V, dias = Repo.dias.values.sortedBy { it.fecha }, momentos = Repo.momentos,
        gastos = Repo.gastos, respuestas = Repo.respuestas, reacciones = Repo.reacciones, lugares = Repo.lugares, visitas = Repo.visitas,
        salud = Repo.salud.values.toList(), pantalla = Repo.pantalla.values.toList(), cartas = Repo.cartas, experimentos = Repo.experimentos,
        config = Repo.config.copy(disparos = emptyMap(), plan_muestreo = emptyMap()),
        entorno = Repo.entorno.values.sortedBy { it.fecha }, fotos = Repo.fotos.values.sortedBy { it.fecha }, agenda = Repo.agenda.values.sortedBy { it.fecha },
        notifs = Repo.notifs.values.sortedBy { it.fecha }, actividad = Repo.actividad.values.sortedBy { it.fecha }, sesiones = Repo.sesiones,
        ascensos = Repo.ascensos, obras = Repo.obras, escuchas = Repo.escuchas("0000-01-01", "9999-12-31", todas = true),
        comunicacion = Repo.comunicacion.values.sortedBy { it.fecha }, conexiones = Repo.conexiones.values.sortedBy { it.fecha },
        subrayados = Repo.subrayados, huellas = Repo.huellasDe("0000-01-01", "9999-12-31"), huellas_dia = Repo.huellas.values.sortedBy { it.fecha },
        chats = Repo.chats, nutricion = Repo.nutricion.values.sortedBy { it.fecha }, comidas = Repo.comidas("0000-01-01", "9999-12-31"),
        series = Repo.series("0000-01-01", "9999-12-31"), entrenos = Repo.entrenos.values.sortedBy { it.fecha }, ingresos = Repo.ingresos,
        youtube = Repo.youtube, tramos = Repo.tramos, consumos = Repo.consumos, notas = Repo.notas, metas = Repo.metas,
        tomas = Repo.tomas, laptop = Repo.laptop.values.sortedBy { it.fecha }, avisos = Repo.avisos.values.sortedBy { it.fecha }))

    class Tabla(val archivo: String, val descripcion: String, val filas: List<Map<String, Any?>>)

    fun tablas(): List<Tabla> = listOf(
        Tabla("vm_panel.csv", "Una fila por día con lo principal de todas las fuentes", filasPanel()),
        Tabla("vm_panel_extendido.csv", "Una fila por día con todas las variables: cada persona, lugar, app, categoría, artista, obra, ciudad, nutriente y más", filasPanelExtendido()),
        Tabla("vm_variables.csv", "Las variables que la app cruza entre sí, con su nombre y su grupo (las privadas, marcadas)", filasVariables()),
        Tabla("vm_familias.csv", "Parejas de variables que la app esconde por obvias (la misma medida, una parte y su total…), en familias", filasFamilias()),
        Tabla("vm_dias.csv", "Diario: lo que llenaste cada día", filasDias()),
        Tabla("vm_momentos.csv", "Momentos y avisos de ánimo, uno por fila", filasMomentos()),
        Tabla("vm_tramos.csv", "Cronómetro: bloques de enfoque (estudio, lectura, idiomas, tesis…) y siestas, uno por fila", filasTramos()),
        Tabla("vm_sustancias.csv", "Sustancias: cada consumo (alcohol, cannabis, tabaco, cafeína, otra) con su nivel, duración y cruda", filasSustancias()),
        Tabla("vm_horas.csv", "Una fila por hora: dormido, lugar, pantalla, notificaciones, luz, música, ánimo, cafeína y alcohol en el cuerpo, sustancias, enfoque, gastos y comidas", filasHoras()),
        Tabla("vm_gastos.csv", "Gastos, uno por fila", filasGastos()),
        Tabla("vm_notas.csv", "Notas sueltas: ideas, pensamientos, propósitos y recordatorios", filasNotas()),
        Tabla("vm_suplementos.csv", "Cada toma de suplemento (y las porciones de probióticos estimadas de tu bitácora de comidas)", filasSuplementos()),
        Tabla("vm_laptop.csv", "Minutos por app y por sitio en la laptop, por día (Cold Turkey)", filasLaptop()),
        filasRed().let { (n, v) -> Tabla("vm_red_nodos.csv", "Personas de tu red: relación, cercanía, días mencionada y grado", n) },
        Tabla("vm_red_vinculos.csv", "Pares de personas: cómo se conocen según tú y cuántos días aparecen juntas", filasRed().second),
        Tabla("vm_personas.csv", "Tu gente: relación, cercanía, desde cuándo, nacimiento, fallecimiento y edad", filasPersonas()),
        Tabla("vm_fechas_propias.csv", "Fechas propias que se repiten cada año (aniversarios y otras)", filasFechasPropias()),
        Tabla("vm_experimentos.csv", "Experimentos personales: pregunta, hipótesis sellada, diseño, lavado y plan por semana (1 = con la acción)", filasExperimentos()),
        Tabla("vm_metas.csv", "Metas: horizonte, fecha objetivo, estado, medida, progreso e historial de cambios", filasMetas()),
        Tabla("vm_ingresos.csv", "Ingresos, uno por fila", filasIngresos()),
        Tabla("vm_escalas.csv", "Cuestionarios validados (WHO-5, PSS-4, UCLA-3, TIPI)", filasEscalas()),
        Tabla("vm_reaccion.csv", "Pruebas de reacción", filasReaccion()),
        Tabla("vm_salud.csv", "Health Connect por día (reloj y báscula)", filasSalud()),
        Tabla("vm_pantalla.csv", "Tiempo en pantalla por día", filasPantalla()),
        Tabla("vm_visitas.csv", "Llegadas y salidas de tus lugares", filasVisitas()),
        Tabla("vm_entorno.csv", "Clima, luz, aire y luna por día", filasEntorno()),
        Tabla("vm_fotos.csv", "Fotos tomadas por día", filasFotos()),
        Tabla("vm_agenda.csv", "Agenda por día", filasAgenda()),
        Tabla("vm_notificaciones.csv", "Notificaciones por día (solo conteos)", filasNotifs()),
        Tabla("vm_musica.csv", "Canciones y podcasts, uno por fila", filasMusica()),
        Tabla("vm_actividad.csv", "Minutos por tipo de movimiento", filasActividad()),
        Tabla("vm_ejercicio.csv", "Sesiones de ejercicio del reloj", filasEjercicio()),
        Tabla("vm_escalada_sesiones.csv", "Sesiones de escalada", filasEscaladaSesiones()),
        Tabla("vm_escalada_bloques.csv", "Bloques o vías de cada sesión", filasEscaladaBloques()),
        Tabla("vm_biblioteca.csv", "Libros, películas, series, juegos y artículos", filasBiblioteca()),
        Tabla("vm_citas.csv", "Citas de tus obras y frases guardadas con ♥", filasCitas()),
        Tabla("vm_mundo.csv", "Ciudades añadidas a mano, países rascados y sitios del Patrimonio Mundial marcados", filasMundo()),
        Tabla("vm_cartas.csv", "Cartas: a tu yo del futuro y a otras personas (las selladas, sin su texto)", filasCartas()),
        Tabla("vm_perfil.csv", "Conocerte: cómo está ahora y cada cambio con su fecha", filasPerfil()),
        Tabla("vm_politica.csv", "Brújula política: un test por año, ejes y respuestas", filasPolitica()),
        Tabla("vm_comunicacion.csv", "Llamadas y SMS por día (solo conteos)", filasComunicacion()),
        Tabla("vm_conexiones.csv", "Wifi, audio, carga, batería y luz por día", filasConexiones()),
        Tabla("vm_subrayados.csv", "Subrayados y notas de Kindle", filasSubrayados()),
        Tabla("vm_google_dia.csv", "YouTube y búsquedas de Google por día", filasGoogleDia()),
        Tabla("vm_google.csv", "YouTube y búsquedas de Google, una por fila", filasGoogle()),
        Tabla("vm_youtube.csv", "YouTube (CSV de Takeout): suscripciones, «me gusta», videos en listas, comentarios y chats (sin texto)", filasYoutube()),
        Tabla("vm_whatsapp.csv", "WhatsApp: un chat por día por fila (solo conteos)", filasWhatsapp()),
        Tabla("vm_nutricion.csv", "MacroFactor: nutrición, gasto energético y peso por día", filasNutricion()),
        Tabla("vm_comidas.csv", "MacroFactor: cada alimento registrado", filasComidas()),
        Tabla("vm_entrenos.csv", "MacroFactor Workouts: resumen por día y por grupo muscular", filasEntrenos()),
        Tabla("vm_series.csv", "MacroFactor Workouts: cada serie", filasSeries()),
    )

    fun archivos(ctx: Context? = null, ts: List<Tabla> = tablas()): List<Pair<String, String>> {
        val out = ArrayList<Pair<String, String>>()
        ts.forEach { out.add(it.archivo to csv(it.filas)) }
        out.add("vm_ubicaciones.csv" to csvUbicaciones())
        out.add("vm_fotos_catalogo.csv" to csvCatalogoFotos())
        out.add("vm_diccionario.csv" to Diccionario.csv(ts))
        out.add("vm_respaldo.json" to respaldoJson())
        out.add("LEEME.txt" to leeme())
        ctx?.let { c -> runCatching { c.assets.open("vestigium.R").use { it.readBytes().toString(Charsets.UTF_8) } }.getOrNull()?.let { out.add("vestigium.R" to it) } }
        return out
    }

    private fun leeme() = """
        Vestigium Mei · exportación ${Fechas.ahoraIso()}
        Esquema de datos versión $SCHEMA_V.

        Para empezar en R, abre vestigium.R (viene en esta misma carpeta) y córrelo completo:
          source("vestigium.R")
        Lee todas las tablas con el tipo correcto de cada columna (según vm_diccionario.csv), arma el panel diario,
        y deja listas funciones de análisis: tendencias, día de la semana, desfase de un día, dinámica emocional,
        ritmo de sueño y comparaciones de días con y sin algo.

        Si prefieres hacerlo a mano:
          library(tidyverse)
          panel <- read_csv("vm_panel.csv")        # una fila por día con lo principal de todo
          ext   <- read_csv("vm_panel_extendido.csv")  # una fila por día con TODAS las variables (personas, apps, nutrientes…)

        vm_diccionario.csv describe cada columna de cada archivo: tipo (fecha, fecha_hora, hora, entero, numero,
        logico, texto, lista), unidad y significado.

        La exportación completa trae también vm_datos.sqlite: las mismas tablas en una sola base SQLite (formato abierto),
        más «ubicaciones» y «diccionario». En R: vm_leer_sqlite() o DBI::dbConnect(RSQLite::SQLite(), "vm_datos.sqlite").

        Convenciones: texto en UTF-8; fechas AAAA-MM-DD en hora local; los días empiezan a las 5:00 (lo de la madrugada
        cuenta para el día anterior), salvo lo que va por día de calendario (0:00 a 24:00): corazón, oxígeno, calorías
        activas, temperatura de la piel, respiración y peso del reloj, la agenda, el clima, la luna y las ciudades y países
        (el diccionario lo dice en cada una), y la pantalla y los pasos de antes de la v13 (columnas ventana_pantalla y
        ventana_pasos). El sueño va en el día en que despiertas. fecha_hora en ISO 8601 con desfase horario (2026-10-16T08:42:10-06:00);
        horas HH:MM; booleanos 0/1; listas separadas por «|»; celda vacía = dato que no existe (nunca se rellenó con promedios).
        Horas decimales (*_h): 7.5 = 7:30; en horas de dormir la madrugada suma 24 (1:00 = 25) para que el promedio tenga sentido;
        en ultimo_uso_h suma 24 solo antes de las 5:00, como el día lógico.

        El panel de R es el de la app (desde la entrega 10d): vestigium.R analiza vm_panel_extendido.csv, y en las columnas de
        vm_panel.csv que comparten nombre con la app el valor es el de la app, con sus mismas reglas de 0 y vacío (por ejemplo,
        gasto_total, escalada y gimnasio valen 0 desde el primer registro y en días con diario; antes, vacío). Once columnas de
        vm_panel.csv se llaman distinto en la app (km = distancia_km, sueno_min = sueno_horas en minutos…): el diccionario dice
        cuál es cuál. vm_variables.csv lista las variables que la app cruza y vm_familias.csv, las parejas que esconde por obvias.
        Escalas: ${Escalas.TODAS.joinToString("; ") { it.nombre + " — " + it.adaptacion }}
    """.trimIndent()

    /**
     * Exportación resumida: solo el panel diario con lo principal, su diccionario, el LEEME y el script de R.
     * Para abrirlo rápido y empezar.
     */
    fun zipResumido(ctx: Context): File {
        val dir = File(ctx.cacheDir, "export").apply { mkdirs() }
        dir.listFiles()?.forEach { it.delete() }
        val f = File(dir, "vestigium_resumido_${Fechas.hoyCalendario()}.zip")
        val panel = Tabla("vm_panel.csv", "Una fila por día con lo principal de todas las fuentes", filasPanel())
        ZipOutputStream(f.outputStream()).use { z ->
            val archivos = listOfNotNull("vm_panel.csv" to csv(panel.filas), "vm_diccionario.csv" to Diccionario.csv(listOf(panel)), "LEEME.txt" to leeme(),
                runCatching { ctx.assets.open("vestigium.R").use { it.readBytes().toString(Charsets.UTF_8) } }.getOrNull()?.let { "vestigium.R" to it })
            archivos.forEach { (nombre, cont) -> z.putNextEntry(ZipEntry(nombre)); z.write(cont.toByteArray()); z.closeEntry() }
        }
        return f
    }

    fun zip(ctx: Context): File {
        val dir = File(ctx.cacheDir, "export").apply { mkdirs() }
        dir.listFiles()?.forEach { it.delete() }
        val f = File(dir, "vestigium_${Fechas.hoyCalendario()}.zip")
        val ts = tablas()
        ZipOutputStream(f.outputStream()).use { z ->
            archivos(ctx, ts).forEach { (nombre, cont) -> z.putNextEntry(ZipEntry(nombre)); z.write(cont.toByteArray()); z.closeEntry() }
            // Todo también en una sola base SQLite (formato abierto que se lee igual dentro de veinte años): vm_datos.sqlite.
            runCatching { sqlite(ctx, ts) }.getOrNull()?.let { db ->
                z.putNextEntry(ZipEntry("vm_datos.sqlite")); db.inputStream().use { it.copyTo(z) }; z.closeEntry(); db.delete()
            }
            // Las portadas también van, para no perder las fotos que pusiste a mano.
            Portadas.carpeta(ctx).listFiles()?.forEach { p -> z.putNextEntry(ZipEntry("portadas/" + p.name)); p.inputStream().use { it.copyTo(z) }; z.closeEntry() }
        }
        return f
    }

    /**
     * Las mismas tablas de la exportación en una base SQLite (una tabla por archivo, sin el «vm_»; más «ubicaciones» y
     * «diccionario»). Números como números, lógicos como 0/1 y listas como texto separado por «|».
     */
    fun sqlite(ctx: Context, ts: List<Tabla> = tablas()): File {
        val dir = File(ctx.cacheDir, "export_sqlite").apply { mkdirs() }
        val f = File(dir, "vm_datos.sqlite")
        f.delete(); File(f.path + "-journal").delete()
        val db = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(f, null)
        fun q(n: String) = "\"" + n.replace("\"", "") + "\""
        fun tipoSql(valores: List<Any?>): String {
            val v = valores.filterNotNull()
            return when {
                v.isEmpty() -> "TEXT"
                v.all { it is Boolean || it is Int || it is Long || it is Short } -> "INTEGER"
                v.all { it is Number || it is Boolean } -> "REAL"
                else -> "TEXT"
            }
        }
        fun tabla(nombre: String, filas: List<Map<String, Any?>>) {
            val cols = LinkedHashSet<String>(); filas.forEach { cols.addAll(it.keys) }
            if (cols.isEmpty()) return
            // SQLite no distingue mayúsculas en los nombres de columna: si dos chocan, la segunda lleva «_2».
            val vistos = HashSet<String>()
            val nombres = cols.map { c -> var n = c; var k = 2; while (!vistos.add(n.lowercase())) { n = "${c}_$k"; k++ }; n }
            db.execSQL("CREATE TABLE ${q(nombre)} (" + cols.mapIndexed { i, c -> q(nombres[i]) + " " + tipoSql(filas.map { it[c] }) }.joinToString(", ") + ")")
            val st = db.compileStatement("INSERT INTO ${q(nombre)} VALUES (" + cols.joinToString(", ") { "?" } + ")")
            for (fila in filas) {
                st.clearBindings()
                cols.forEachIndexed { i, c ->
                    val k = i + 1
                    when (val v = fila[c]) {
                        null -> st.bindNull(k)
                        is Boolean -> st.bindLong(k, if (v) 1L else 0L)
                        is Int -> st.bindLong(k, v.toLong())
                        is Long -> st.bindLong(k, v)
                        is Short -> st.bindLong(k, v.toLong())
                        is Double -> if (v.isNaN()) st.bindNull(k) else st.bindDouble(k, v)
                        is Float -> if (v.isNaN()) st.bindNull(k) else st.bindDouble(k, v.toDouble())
                        is Number -> st.bindDouble(k, v.toDouble())
                        is List<*> -> st.bindString(k, v.joinToString("|"))
                        else -> st.bindString(k, v.toString())
                    }
                }
                st.executeInsert()
            }
            st.close()
        }
        try {
            db.beginTransaction()
            ts.forEach { t -> tabla(t.archivo.removeSuffix(".csv").removePrefix("vm_"), t.filas) }
            tabla("ubicaciones", Repo.db().ubicaciones().map { linkedMapOf<String, Any?>("ts" to Fechas.isoDeMillis(it.ms), "lat" to it.lat, "lon" to it.lon, "precision_m" to it.precision, "fuente" to it.fuente, "ms" to it.ms) })
            tabla("diccionario", Diccionario.filas(ts))
            db.setTransactionSuccessful()
        } finally { db.endTransaction(); db.close() }
        return f
    }

    fun compartir(ctx: Context, archivo: File? = null, tipo: String = "application/zip") {
        val f = archivo ?: java.io.File(java.io.File(ctx.cacheDir, "export"), "vestigium_${Fechas.hoyCalendario()}.zip").takeIf { it.exists() } ?: zip(ctx)
        val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".archivos", f)
        val i = Intent(Intent.ACTION_SEND).setType(tipo).putExtra(Intent.EXTRA_STREAM, uri)
            .putExtra(Intent.EXTRA_SUBJECT, f.name).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(Intent.createChooser(i, "Guardar o enviar los datos").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Respaldo diario en la carpeta elegida: los CSV para R y el respaldo completo (o solo el cifrado, si pusiste contraseña). */
    fun respaldoAutomatico(ctx: Context): Boolean = Respaldos.aCarpeta(ctx)

}
