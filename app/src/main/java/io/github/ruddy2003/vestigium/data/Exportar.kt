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
                "fecha" to d.fecha, "schema_v" to d.schema_v, "origen" to d.origen, "retroactivo" to d.retroactivo,
                "dia_dificil" to d.dia_dificil, "ts_creado" to d.ts_creado, "ts_editado" to d.ts_editado,
                "valencia" to d.valencia, "activacion" to d.activacion, "dolor" to d.dolor,
            )
            Catalogo.CONTEXTOS.forEach { r["ctx_${it.id}"] = d.ctx[it.id] == true }
            r["hitos"] = d.hitos
            r["flow"] = d.flow; r["sdt_necesidad"] = d.sdt_necesidad; r["apreciacion"] = d.apreciacion
            r["sueno_recordado"] = d.sueno_recordado; r["sueno_valencia"] = d.sueno_valencia; r["sueno_lucidez"] = d.sueno_lucidez
            r["sueno_vividez"] = d.sueno_vividez; r["sueno_tipo"] = d.sueno_tipo; r["sueno_temas"] = d.sueno_temas
            r["sueno_calidad"] = d.sueno_calidad; r["sueno_texto"] = d.sueno_texto
            r["mejor_momento"] = d.mejor_momento; r["peor_momento"] = d.peor_momento
            r["impulso"] = d.impulso; r["recaida"] = d.recaida; r["detonantes"] = d.detonantes
            r["medicamentos"] = d.medicamentos; r["prediccion_manana"] = d.prediccion_manana
            Repo.config.habitos.forEach { r["hab_${it.id}"] = d.habitos[it.id] == true }
            r["personas"] = Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct()
            r["temas"] = Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct()
            r["palabras"] = Texto.palabras(d.nota)
            r["nota"] = d.nota
            r
        }
    }

    fun filasMomentos() = Repo.momentos.map {
        linkedMapOf<String, Any?>("id" to it.id, "ts" to it.ts, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "tipo" to it.tipo,
            "intensidad" to it.intensidad, "valencia" to it.valencia, "activacion" to it.activacion, "texto" to it.texto,
            "personas" to Texto.personas(it.texto), "temas" to Texto.temas(it.texto),
            "lat" to it.lat, "lon" to it.lon, "precision_m" to it.precision_m, "lugar" to Repo.lugares.firstOrNull { l -> l.id == it.lugar }?.nombre,
            "origen" to it.origen, "aviso_ts" to it.aviso_ts,
            "latencia_min" to it.aviso_ts?.let { a -> (Fechas.millis(it.ts) - Fechas.millis(a)) / 60000.0 },
            "ruido_db" to it.ruido_db, "luz_lux" to it.luz_lux,
            "con_quien" to it.con_quien, "detonantes" to it.detonantes, "dispositivo" to it.dispositivo, "antes" to it.antes, "duracion_min" to it.duracion_min)
    }

    fun filasTramos() = Repo.tramos.map {
        linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "tipo" to it.tipo, "es_siesta" to (it.tipo == "siesta"), "inicio" to it.inicio, "fin" to it.fin,
            "hora" to Fechas.horaDe(it.inicio), "min" to it.min, "nota" to it.nota, "personas" to Texto.personas(it.nota), "temas" to Texto.temas(it.nota), "origen" to it.origen)
    }

    fun filasGastos() = Repo.gastos.map {
        linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "hora" to it.hora, "dia_logico" to it.dia, "monto" to it.monto, "categoria" to it.categoria,
            "imprevisto" to it.imprevisto, "nota" to it.nota, "lat" to it.lat, "lon" to it.lon)
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
            "fijo" to it.fijo, "nota" to it.nota)
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
            "cal_totales" to it.cal_totales, "pisos" to it.pisos, "vo2max" to it.vo2max, "agua_ml" to it.agua_ml)
    }

    fun filasPantalla() = Repo.pantalla.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "total_min" to it.total_min, "desbloqueos" to it.desbloqueos,
            "primer_uso" to it.primer_uso, "ultimo_uso" to it.ultimo_uso,
            "top_apps" to it.por_app_min.entries.take(10).map { e -> "${e.key}:${e.value}" },
            "cama_min" to it.cama_min, "dormir_tras_tel_min" to it.dormir_tras_tel_min, "tel_tras_despertar_min" to it.tel_tras_despertar_min,
            "por_hora_min" to it.por_hora_min)
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
        val mu = Repo.momentos.filter { it.tipo == "muestreo" && it.valencia != null }.groupBy { it.fecha }
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
            "extranjero", "ciudad_nueva", "unesco", "carta_escrita", "sueno_palabras", "cercanos", "vi_familia", "vi_pareja", "vi_amistades", "vi_convivientes")
        val est = runCatching { io.github.ruddy2003.vestigium.ciencia.Estadistica.panel() }.getOrDefault(emptyMap())
        return fechas.map { f ->
            val d = Repo.dias[f]; val s = Repo.salud[f]; val p = Repo.pantalla[f]; val e = Repo.entorno[f]
            val ft = Repo.fotos[f]; val ag = Repo.agenda[f]; val nt = Repo.notifs[f]; val ac = Repo.actividad[f]
            val r = linkedMapOf<String, Any?>("fecha" to f, "dia_semana" to Fechas.parse(f).dayOfWeek.value, "con_diario" to (d != null),
                "valencia" to d?.valencia, "activacion" to d?.activacion, "dolor" to d?.dolor, "sueno_calidad" to d?.sueno_calidad,
                "valencia_muestreo" to mu[f]?.mapNotNull { it.valencia }?.average(), "n_muestreos" to (mu[f]?.size ?: 0),
                "n_momentos" to (mo[f]?.size ?: 0))
            Catalogo.MOMENTOS.forEach { t -> r["m_${t.id}"] = mo[f]?.count { it.tipo == t.id } ?: 0 }
            r["gasto_total"] = ga[f]?.sumOf { it.monto } ?: 0.0
            r["ingreso_total"] = ing[f]?.sumOf { it.monto } ?: 0.0
            r["palabras"] = d?.let { Texto.palabras(it.nota) }
            r["sueno_min"] = s?.sueno_min; r["sueno_profundo_min"] = s?.sueno_profundo_min; r["sueno_rem_min"] = s?.sueno_rem_min
            r["fc_reposo"] = s?.fc_reposo; r["hrv_rmssd"] = s?.hrv_rmssd; r["pasos"] = s?.pasos
            r["pantalla_min"] = p?.total_min; r["desbloqueos"] = p?.desbloqueos; r["primer_uso"] = p?.primer_uso?.let { Fechas.horaDe(it) }; r["ultimo_uso"] = p?.ultimo_uso?.let { Fechas.horaDe(it) }
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
            r
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
            // Los nutrientes, los minutos por app y por movimiento vienen completos de sus tablas (abajo).
            r.forEach { (k, v) -> if (k !in fila && !k.startsWith("nut_") && !k.startsWith("act_") && !k.startsWith("app_")) fila[k] = v }
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
        youtube = Repo.youtube, tramos = Repo.tramos))

    class Tabla(val archivo: String, val descripcion: String, val filas: List<Map<String, Any?>>)

    fun tablas(): List<Tabla> = listOf(
        Tabla("vm_panel.csv", "Una fila por día con lo principal de todas las fuentes", filasPanel()),
        Tabla("vm_panel_extendido.csv", "Una fila por día con todas las variables: cada persona, lugar, app, categoría, artista, obra, ciudad, nutriente y más", filasPanelExtendido()),
        Tabla("vm_dias.csv", "Diario: lo que llenaste cada día", filasDias()),
        Tabla("vm_momentos.csv", "Momentos y avisos de ánimo, uno por fila", filasMomentos()),
        Tabla("vm_tramos.csv", "Cronómetro: bloques de enfoque (estudio, lectura, idiomas, tesis…) y siestas, uno por fila", filasTramos()),
        Tabla("vm_gastos.csv", "Gastos, uno por fila", filasGastos()),
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

    fun archivos(ctx: Context? = null): List<Pair<String, String>> {
        val ts = tablas()
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

        Convenciones: texto en UTF-8; fechas AAAA-MM-DD en hora local; los días empiezan a las 5:00 (lo de la madrugada
        cuenta para el día anterior); fecha_hora en ISO 8601 con desfase horario (2026-10-16T08:42:10-06:00);
        horas HH:MM; booleanos 0/1; listas separadas por «|»; celda vacía = dato que no existe (nunca se rellenó con promedios).
        Horas decimales (*_h): 7.5 = 7:30; en horas de dormir la madrugada suma 24 (1:00 = 25) para que el promedio tenga sentido.
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
        ZipOutputStream(f.outputStream()).use { z ->
            archivos(ctx).forEach { (nombre, cont) -> z.putNextEntry(ZipEntry(nombre)); z.write(cont.toByteArray()); z.closeEntry() }
            // Las portadas también van, para no perder las fotos que pusiste a mano.
            Portadas.carpeta(ctx).listFiles()?.forEach { p -> z.putNextEntry(ZipEntry("portadas/" + p.name)); p.inputStream().use { it.copyTo(z) }; z.closeEntry() }
        }
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
