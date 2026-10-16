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
            "latencia_min" to it.aviso_ts?.let { a -> (Fechas.millis(it.ts) - Fechas.millis(a)) / 60000.0 })
    }

    fun filasGastos() = Repo.gastos.map {
        linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "hora" to it.hora, "monto" to it.monto, "categoria" to it.categoria,
            "imprevisto" to it.imprevisto, "nota" to it.nota, "lat" to it.lat, "lon" to it.lon)
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
            "fc_reposo" to it.fc_reposo, "hrv_rmssd" to it.hrv_rmssd, "pasos" to it.pasos, "spo2" to it.spo2)
    }

    fun filasPantalla() = Repo.pantalla.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "total_min" to it.total_min, "desbloqueos" to it.desbloqueos,
            "primer_uso" to it.primer_uso, "ultimo_uso" to it.ultimo_uso,
            "top_apps" to it.por_app_min.entries.take(10).map { e -> "${e.key}:${e.value}" })
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
            "luz_min" to it.luz_min, "sol_min" to it.sol_min, "pm25" to it.pm25, "pm10" to it.pm10, "aqi_us" to it.aqi_us,
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

    fun filasMusica() = Repo.escuchas("0000-01-01", "9999-12-31").map {
        linkedMapOf<String, Any?>("ts" to it.ts, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "titulo" to it.titulo, "artista" to it.artista,
            "album" to it.album, "app" to it.app, "duracion_s" to it.duracion_s)
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
            "dias" to if (it.inicio != null && it.fin != null) java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(it.inicio), Fechas.parse(it.fin)) + 1 else null)
    }

    /** Una fila por día con todo lo que se sabe de él: el archivo más cómodo para empezar en R. */
    fun filasPanel(): List<Map<String, Any?>> {
        val fechas = (Repo.dias.keys + Repo.salud.keys + Repo.pantalla.keys + Repo.entorno.keys + Repo.momentos.map { it.fecha } + Repo.gastos.map { it.fecha }).toSortedSet()
        val mu = Repo.momentos.filter { it.tipo == "muestreo" && it.valencia != null }.groupBy { it.fecha }
        val mo = Repo.momentos.filter { it.tipo != "muestreo" }.groupBy { it.fecha }
        val ga = Repo.gastos.groupBy { it.fecha }
        val ej = Repo.sesiones.groupBy { it.fecha }
        val es = Repo.ascensos.groupBy { it.fecha }
        val km = runCatching { io.github.ruddy2003.vestigium.ciencia.Movilidad.kmPorDia() }.getOrDefault(emptyMap())
        return fechas.map { f ->
            val d = Repo.dias[f]; val s = Repo.salud[f]; val p = Repo.pantalla[f]; val e = Repo.entorno[f]
            val ft = Repo.fotos[f]; val ag = Repo.agenda[f]; val nt = Repo.notifs[f]; val ac = Repo.actividad[f]
            val r = linkedMapOf<String, Any?>("fecha" to f, "dia_semana" to Fechas.parse(f).dayOfWeek.value, "con_diario" to (d != null),
                "valencia" to d?.valencia, "activacion" to d?.activacion, "dolor" to d?.dolor, "sueno_calidad" to d?.sueno_calidad,
                "valencia_muestreo" to mu[f]?.mapNotNull { it.valencia }?.average(), "n_muestreos" to (mu[f]?.size ?: 0),
                "n_momentos" to (mo[f]?.size ?: 0))
            Catalogo.MOMENTOS.forEach { t -> r["m_${t.id}"] = mo[f]?.count { it.tipo == t.id } ?: 0 }
            r["gasto_total"] = ga[f]?.sumOf { it.monto } ?: 0.0
            r["palabras"] = d?.let { Texto.palabras(it.nota) }
            r["sueno_min"] = s?.sueno_min; r["sueno_profundo_min"] = s?.sueno_profundo_min; r["sueno_rem_min"] = s?.sueno_rem_min
            r["fc_reposo"] = s?.fc_reposo; r["hrv_rmssd"] = s?.hrv_rmssd; r["pasos"] = s?.pasos
            r["pantalla_min"] = p?.total_min; r["desbloqueos"] = p?.desbloqueos; r["primer_uso"] = p?.primer_uso?.let { Fechas.horaDe(it) }; r["ultimo_uso"] = p?.ultimo_uso?.let { Fechas.horaDe(it) }
            r["t_max"] = e?.t_max; r["t_min"] = e?.t_min; r["precip_mm"] = e?.precip_mm; r["nubes_pct"] = e?.nubes_pct; r["luz_min"] = e?.luz_min
            r["sol_min"] = e?.sol_min; r["aqi_us"] = e?.aqi_us; r["pm25"] = e?.pm25; r["luna_ilum"] = e?.luna_ilum ?: Luna.iluminacion(Luna.fase(f))
            r["fotos"] = ft?.fotos; r["eventos"] = ag?.eventos; r["min_ocupado"] = ag?.min_ocupado; r["notificaciones"] = nt?.total
            r["a_pie_min"] = ac?.minutos?.get("a_pie"); r["vehiculo_min"] = ac?.minutos?.get("vehiculo")
            r["ejercicio_min"] = ej[f]?.sumOf { it.min }; r["escalada"] = es[f] != null
            r["km"] = km[f]
            r
        }
    }

    fun csvUbicaciones(): String {
        val sb = StringBuilder("﻿ts,lat,lon,precision_m,fuente\n")
        Repo.db().ubicaciones().forEach { sb.append(Fechas.isoDeMillis(it.ms)).append(',').append(it.lat).append(',').append(it.lon).append(',')
            .append(it.precision ?: "").append(',').append(it.fuente ?: "").append('\n') }
        return sb.toString()
    }

    fun respaldoJson(): String = JSON.encodeToString(Respaldo.serializer(), Respaldo(
        exportado = Fechas.ahoraIso(), schema_v = SCHEMA_V, dias = Repo.dias.values.sortedBy { it.fecha }, momentos = Repo.momentos,
        gastos = Repo.gastos, respuestas = Repo.respuestas, reacciones = Repo.reacciones, lugares = Repo.lugares, visitas = Repo.visitas,
        salud = Repo.salud.values.toList(), pantalla = Repo.pantalla.values.toList(), cartas = Repo.cartas, experimentos = Repo.experimentos,
        config = Repo.config.copy(disparos = emptyMap(), plan_muestreo = emptyMap()),
        entorno = Repo.entorno.values.sortedBy { it.fecha }, fotos = Repo.fotos.values.sortedBy { it.fecha }, agenda = Repo.agenda.values.sortedBy { it.fecha },
        notifs = Repo.notifs.values.sortedBy { it.fecha }, actividad = Repo.actividad.values.sortedBy { it.fecha }, sesiones = Repo.sesiones,
        ascensos = Repo.ascensos, obras = Repo.obras, escuchas = Repo.escuchas("0000-01-01", "9999-12-31")))

    fun archivos(): List<Pair<String, String>> = listOf(
        "vm_panel.csv" to csv(filasPanel()),
        "vm_dias.csv" to csv(filasDias()),
        "vm_momentos.csv" to csv(filasMomentos()),
        "vm_gastos.csv" to csv(filasGastos()),
        "vm_escalas.csv" to csv(filasEscalas()),
        "vm_reaccion.csv" to csv(filasReaccion()),
        "vm_salud.csv" to csv(filasSalud()),
        "vm_pantalla.csv" to csv(filasPantalla()),
        "vm_visitas.csv" to csv(filasVisitas()),
        "vm_ubicaciones.csv" to csvUbicaciones(),
        "vm_entorno.csv" to csv(filasEntorno()),
        "vm_fotos.csv" to csv(filasFotos()),
        "vm_agenda.csv" to csv(filasAgenda()),
        "vm_notificaciones.csv" to csv(filasNotifs()),
        "vm_musica.csv" to csv(filasMusica()),
        "vm_actividad.csv" to csv(filasActividad()),
        "vm_ejercicio.csv" to csv(filasEjercicio()),
        "vm_escalada_sesiones.csv" to csv(filasEscaladaSesiones()),
        "vm_escalada_bloques.csv" to csv(filasEscaladaBloques()),
        "vm_biblioteca.csv" to csv(filasBiblioteca()),
        "vm_respaldo.json" to respaldoJson(),
        "LEEME.txt" to leeme(),
    )

    private fun leeme() = """
        Vestigium Mei · exportación ${Fechas.ahoraIso()}
        Esquema de datos versión $SCHEMA_V.

        En R:
          library(tidyverse)
          panel <- read_csv("vm_panel.csv")        # una fila por día con todo junto
          dias <- read_csv("vm_dias.csv")          # diario completo
          momentos <- read_csv("vm_momentos.csv")
          salud <- read_csv("vm_salud.csv")

        Archivos: panel (resumen diario), dias, momentos, gastos, escalas, reaccion, salud, pantalla, visitas,
        ubicaciones, entorno (clima, luz, aire y luna), fotos, agenda, notificaciones (solo conteos), musica,
        actividad (minutos a pie, en vehículo...), ejercicio (sesiones del reloj), escalada_sesiones,
        escalada_bloques (grado_num: V0 = 0, V1 = 1...; Fontainebleau convertido a V) y biblioteca.

        Convenciones: fechas AAAA-MM-DD en hora local; los días empiezan a las 5:00;
        booleanos 0/1; listas separadas por «|»; celda vacía = dato que no existe (nunca se rellenó con promedios).
        Escalas: ${Escalas.TODAS.joinToString("; ") { it.nombre + " — " + it.adaptacion }}
    """.trimIndent()

    fun zip(ctx: Context): File {
        val dir = File(ctx.cacheDir, "export").apply { mkdirs() }
        dir.listFiles()?.forEach { it.delete() }
        val f = File(dir, "vestigium_${Fechas.hoyCalendario()}.zip")
        ZipOutputStream(f.outputStream()).use { z ->
            archivos().forEach { (nombre, cont) -> z.putNextEntry(ZipEntry(nombre)); z.write(cont.toByteArray()); z.closeEntry() }
        }
        return f
    }

    fun compartir(ctx: Context) {
        val f = java.io.File(java.io.File(ctx.cacheDir, "export"), "vestigium_${Fechas.hoyCalendario()}.zip").takeIf { it.exists() } ?: zip(ctx)
        val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".archivos", f)
        val i = Intent(Intent.ACTION_SEND).setType("application/zip").putExtra(Intent.EXTRA_STREAM, uri)
            .putExtra(Intent.EXTRA_SUBJECT, f.name).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(Intent.createChooser(i, "Guardar o enviar los datos").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Escribe todos los archivos en la carpeta elegida por el usuario (se reemplazan cada día). */
    fun respaldoAutomatico(ctx: Context): Boolean {
        val uriTxt = Repo.config.carpeta_respaldo ?: return false
        val dir = DocumentFile.fromTreeUri(ctx, Uri.parse(uriTxt)) ?: return false
        if (!dir.canWrite()) return false
        archivos().forEach { (nombre, cont) ->
            val mime = when { nombre.endsWith(".csv") -> "text/csv"; nombre.endsWith(".json") -> "application/json"; else -> "text/plain" }
            val f = dir.findFile(nombre) ?: dir.createFile(mime, nombre) ?: return@forEach
            ctx.contentResolver.openOutputStream(f.uri, "wt")?.use { it.write(cont.toByteArray()) }
        }
        return true
    }

}
