package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import android.provider.Telephony
import io.github.ruddy2003.vestigium.data.ComunicacionDia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo

/**
 * Llamadas y SMS del registro del teléfono. Solo se cuentan: cuántas, de qué tipo, cuántos minutos
 * y con cuántas personas distintas. Los números se usan en memoria para contar y se descartan.
 */
object Comunicacion {
    fun permisos() = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS)
    fun llamadas(ctx: Context) = ctx.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
    fun sms(ctx: Context) = ctx.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    fun tienePermiso(ctx: Context) = llamadas(ctx) || sms(ctx)

    private fun ini(f: String) = Fechas.inicioDiaLogico(f)
    private fun fin(f: String) = Fechas.finDiaLogico(f)
    private fun clave(numero: String?): String? = numero?.filter { it.isDigit() }?.takeLast(8)?.takeIf { it.length >= 6 }

    fun calcular(ctx: Context, fecha: String): ComunicacionDia? {
        if (!tienePermiso(ctx)) return null
        val a = ini(fecha); val b = fin(fecha)
        var c = ComunicacionDia(fecha = fecha)
        val personas = HashSet<String>()
        val tiempos = ArrayList<Long>()
        if (llamadas(ctx)) runCatching {
            ctx.contentResolver.query(CallLog.Calls.CONTENT_URI, arrayOf(CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.NUMBER),
                "${CallLog.Calls.DATE} >= ? AND ${CallLog.Calls.DATE} < ?", arrayOf(a.toString(), b.toString()), null)?.use { k ->
                while (k.moveToNext()) {
                    val dur = k.getLong(2) / 60.0
                    tiempos.add(k.getLong(1))
                    clave(k.getString(3))?.let { personas.add(it) }
                    c = when (k.getInt(0)) {
                        CallLog.Calls.INCOMING_TYPE -> c.copy(entrantes = c.entrantes + 1, min_hablado = c.min_hablado + dur, llamada_max_min = maxOf(c.llamada_max_min, dur))
                        CallLog.Calls.OUTGOING_TYPE -> c.copy(salientes = c.salientes + 1, min_hablado = c.min_hablado + dur, llamada_max_min = maxOf(c.llamada_max_min, dur))
                        CallLog.Calls.MISSED_TYPE -> c.copy(perdidas = c.perdidas + 1)
                        CallLog.Calls.REJECTED_TYPE, CallLog.Calls.BLOCKED_TYPE -> c.copy(rechazadas = c.rechazadas + 1)
                        else -> c
                    }
                }
            }
        }
        if (sms(ctx)) runCatching {
            ctx.contentResolver.query(Telephony.Sms.CONTENT_URI, arrayOf(Telephony.Sms.TYPE, Telephony.Sms.DATE, Telephony.Sms.ADDRESS),
                "${Telephony.Sms.DATE} >= ? AND ${Telephony.Sms.DATE} < ?", arrayOf(a.toString(), b.toString()), null)?.use { k ->
                while (k.moveToNext()) {
                    val dir = clave(k.getString(2))
                    // Los SMS de bancos y servicios llegan de números cortos: no cuentan como personas.
                    if (dir != null) personas.add(dir)
                    tiempos.add(k.getLong(1))
                    c = when (k.getInt(0)) {
                        Telephony.Sms.MESSAGE_TYPE_INBOX -> c.copy(sms_recibidos = c.sms_recibidos + 1)
                        Telephony.Sms.MESSAGE_TYPE_SENT -> c.copy(sms_enviados = c.sms_enviados + 1)
                        else -> c
                    }
                }
            }
        }
        val r2 = { x: Double -> Math.round(x * 10) / 10.0 }
        return c.copy(min_hablado = r2(c.min_hablado), llamada_max_min = r2(c.llamada_max_min), contactos = personas.size,
            primera = tiempos.minOrNull()?.let { Fechas.isoDeMillis(it) }, ultima = tiempos.maxOrNull()?.let { Fechas.isoDeMillis(it) })
    }

    fun sincronizar(ctx: Context, dias: Int = 3) {
        if (!tienePermiso(ctx)) return
        for (i in 0 until dias) calcular(ctx, Fechas.sumar(Fechas.diaLogico(), -i.toLong()))?.let { Repo.guardarComunicacion(it) }
    }

    /** El registro de llamadas guarda meses de historia: se rellena una vez. */
    fun historia(ctx: Context, dias: Int = 365) {
        if (!tienePermiso(ctx)) return
        for (i in 3 until dias) {
            val f = Fechas.sumar(Fechas.diaLogico(), -i.toLong())
            if (f in Repo.comunicacion) continue
            val c = calcular(ctx, f) ?: continue
            if (c.entrantes + c.salientes + c.perdidas + c.rechazadas + c.sms_enviados + c.sms_recibidos > 0) Repo.guardarComunicacion(c)
        }
    }
}
