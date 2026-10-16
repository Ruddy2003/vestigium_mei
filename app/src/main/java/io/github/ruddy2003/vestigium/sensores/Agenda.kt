package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import io.github.ruddy2003.vestigium.data.AgendaDia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import java.time.Instant
import java.time.ZoneOffset

/** Eventos de tu calendario: cuántos, cuánto tiempo ocupado y a qué hora empezó y terminó tu día agendado. */
object Agenda {
    fun tienePermiso(ctx: Context) = ctx.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    data class Evento(val titulo: String, val inicio: Long, val fin: Long, val todoElDia: Boolean, val calendario: String)

    fun eventos(ctx: Context, fecha: String): List<Evento> {
        if (!tienePermiso(ctx)) return emptyList()
        val a = Fechas.inicioDia(fecha); val b = Fechas.finDia(fecha)
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().also {
            ContentUris.appendId(it, a - 86_400_000L); ContentUris.appendId(it, b + 86_400_000L)
        }.build()
        val proy = arrayOf(CalendarContract.Instances.TITLE, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.CALENDAR_DISPLAY_NAME, CalendarContract.Instances.STATUS,
            CalendarContract.Instances.VISIBLE)
        val out = ArrayList<Evento>()
        runCatching {
            ctx.contentResolver.query(uri, proy, null, null, "${CalendarContract.Instances.BEGIN} ASC")?.use { c ->
                while (c.moveToNext()) {
                    if (!c.isNull(5) && c.getInt(5) == CalendarContract.Instances.STATUS_CANCELED) continue
                    if (!c.isNull(6) && c.getInt(6) == 0) continue
                    val todo = c.getInt(3) == 1
                    val ini = c.getLong(1); val fin = c.getLong(2)
                    if (todo) {
                        // Los eventos de todo el día se guardan en UTC.
                        val d = Fechas.iso(Instant.ofEpochMilli(ini).atZone(ZoneOffset.UTC).toLocalDate())
                        val dFin = Fechas.iso(Instant.ofEpochMilli(fin - 1).atZone(ZoneOffset.UTC).toLocalDate())
                        if (fecha < d || fecha > dFin) continue
                    } else if (fin <= a || ini >= b) continue
                    out.add(Evento(c.getString(0) ?: "", ini, fin, todo, c.getString(4) ?: ""))
                }
            }
        }
        return out
    }

    fun calcular(ctx: Context, fecha: String): AgendaDia? {
        if (!tienePermiso(ctx)) return null
        val ev = eventos(ctx, fecha)
        val a = Fechas.inicioDia(fecha); val b = Fechas.finDia(fecha)
        val normales = ev.filter { !it.todoElDia }
        // Une los tramos que se enciman para no contar dos veces el mismo tiempo.
        val tramos = normales.map { maxOf(it.inicio, a) to minOf(it.fin, b) }.filter { it.second > it.first }.sortedBy { it.first }
        var total = 0L; var ini = -1L; var fin = -1L
        for ((x, y) in tramos) {
            if (x > fin) { if (fin > ini) total += fin - ini; ini = x; fin = y } else fin = maxOf(fin, y)
        }
        if (fin > ini && ini >= 0) total += fin - ini
        return AgendaDia(fecha = fecha, eventos = normales.size, todo_el_dia = ev.count { it.todoElDia }, min_ocupado = (total / 60_000).toInt(),
            primero = normales.minOfOrNull { it.inicio }?.let { Fechas.isoDeMillis(it) }, ultimo = normales.maxOfOrNull { it.fin }?.let { Fechas.isoDeMillis(it) },
            titulos = ev.map { it.titulo }.filter { it.isNotBlank() }.take(30))
    }

    fun sincronizar(ctx: Context) {
        if (!tienePermiso(ctx)) return
        for (i in -1..2) calcular(ctx, Fechas.sumar(Fechas.hoyCalendario(), -i.toLong()))?.let { Repo.guardarAgenda(it) }
    }

    fun historia(ctx: Context, dias: Int = 365) {
        if (!tienePermiso(ctx)) return
        for (i in 3 until dias) {
            val f = Fechas.sumar(Fechas.hoyCalendario(), -i.toLong())
            if (f !in Repo.agenda) calcular(ctx, f)?.let { Repo.guardarAgenda(it) }
        }
    }
}
