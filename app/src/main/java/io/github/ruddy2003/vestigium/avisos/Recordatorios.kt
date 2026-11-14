package io.github.ruddy2003.vestigium.avisos

import io.github.ruddy2003.vestigium.data.porNumero

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Metas
import io.github.ruddy2003.vestigium.data.Repo
import java.time.LocalDateTime

/**
 * El motor de recordatorios: un solo aviso a la semana con las importaciones que tocan (MacroFactor cada 7 días,
 * Cold Turkey cada 7, Takeout cada mes) y, cada día a la hora elegida, las metas que toca revisar y las notas
 * de recordatorio que llegaron a su fecha; y los suplementos, a cada hora elegida mientras falte alguna toma.
 * Cada fuente se puede posponer a mañana, a una fecha o apagar.
 */
object Recordatorios {
    class Fuente(val id: String, val nombre: String, val cada: Int, val activa: Boolean, val que: String)

    val FUENTES = listOf(
        Fuente("macrofactor", "MacroFactor", 7, true, "nutrición y Workouts"),
        Fuente("coldturkey", "Cold Turkey", 7, true, "minutos por app y por sitio en la laptop"),
        Fuente("takeout", "Google Takeout", 30, true, "YouTube, búsquedas y Mi actividad"),
    )

    fun fuente(id: String) = FUENTES.firstOrNull { it.id == id }

    /** Cuándo importaste por última vez (AAAA-MM-DD): lo anotado o, si no, el último día con datos de esa fuente. */
    fun ultima(id: String): String? = Repo.config.importado[id]?.take(10) ?: when (id) {
        "macrofactor" -> Repo.nutricion.keys.maxOrNull()
        "takeout" -> Repo.huellas.keys.maxOrNull()
        else -> null
    }

    fun hace(id: String, hoy: String = Fechas.diaLogico()): Long? = ultima(id)?.let { runCatching { Fechas.diasEntre(it, hoy) }.getOrNull() }

    fun apagada(id: String): Boolean = Repo.config.importar_pospuesto[id] == "apagado" || fuente(id)?.activa != true

    /** El día al que la pospusiste (si es futuro). */
    fun pospuesta(id: String, hoy: String = Fechas.diaLogico()): String? = Repo.config.importar_pospuesto[id]?.takeIf { it != "apagado" && it > hoy }

    fun toca(f: Fuente, hoy: String = Fechas.diaLogico()): Boolean =
        !apagada(f.id) && pospuesta(f.id, hoy) == null && (hace(f.id, hoy)?.let { it >= f.cada } ?: true)

    fun pendientes(hoy: String = Fechas.diaLogico()): List<Fuente> = FUENTES.filter { toca(it, hoy) }

    /** Después de importar: se anota la fecha y se quita lo pospuesto. */
    fun importado(id: String) {
        Repo.cambiarConfig { c ->
            c.copy(importado = c.importado + (id to Fechas.ahoraIso()),
                importar_pospuesto = if (c.importar_pospuesto[id] == "apagado") c.importar_pospuesto else c.importar_pospuesto - id)
        }
    }

    fun posponer(id: String, dia: String) { Repo.cambiarConfig { c -> c.copy(importar_pospuesto = c.importar_pospuesto + (id to dia)) } }
    fun apagar(id: String, apagado: Boolean) {
        Repo.cambiarConfig { c -> c.copy(importar_pospuesto = if (apagado) c.importar_pospuesto + (id to "apagado") else c.importar_pospuesto - id) }
    }

    /** Notas de recordatorio cuya fecha ya llegó. */
    fun notasVencidas(hoy: String = Fechas.diaLogico()) = Repo.notas.filter { it.tipo == "recordatorio" && !it.hecha && it.recordar_el != null && it.recordar_el <= hoy }

    /** Desde el pulso de 15 minutos. */
    fun revisar(ctx: Context, ahora: LocalDateTime, yaFue: (String) -> Boolean, marcar: (String) -> Unit) {
        val c = Repo.config
        val hoy = Fechas.hoyCalendario()
        if (ahora.hour >= c.importar_hora && !yaFue("importar:$hoy")) {
            val pend = pendientes(hoy)
            // El día elegido de la semana, o el día al que pospusiste alguna.
            val tocaHoy = ahora.dayOfWeek.value == c.importar_dia || FUENTES.any { c.importar_pospuesto[it.id] == hoy }
            if (tocaHoy) {
                marcar("importar:$hoy")
                if (pend.isNotEmpty()) Avisos.importar(ctx, pend.map { it.nombre + (hace(it.id, hoy)?.let { d -> " (hace ${porNumero(d, "1 día", "$d días")})" } ?: " (nunca)") })
            }
        }
        // Suplementos (esquema 21): a cada hora elegida (o una hora después de «en una hora»), solo si a alguno le faltan tomas.
        // Cada hora tiene su marca («supl:<día>:<hora>»); si el pulso llega tarde a varias, sale un solo aviso y se marcan todas.
        val horasSupl = io.github.ruddy2003.vestigium.data.Suplementos.horas(c)
        if (horasSupl.isNotEmpty()) {
            val hoyL = Fechas.diaLogico()
            val pospuesto = ctx.getSharedPreferences("recordatorios", Context.MODE_PRIVATE).getLong("supl_hasta", 0L)
            val vencidas = horasSupl.filter { h -> ahora.hour >= h && !yaFue("supl:$hoy:$h") }
            if (vencidas.isNotEmpty() && System.currentTimeMillis() >= pospuesto) {
                val faltan = io.github.ruddy2003.vestigium.data.Suplementos.activos(c).filter { it.modelo != "exposicion" && io.github.ruddy2003.vestigium.data.Suplementos.faltan(it, hoyL) }
                vencidas.forEach { h -> marcar("supl:$hoy:$h") }
                if (faltan.isNotEmpty()) Avisos.suplementos(ctx, faltan.map { nombreConToma(it, hoyL) })
            }
        }
        if (ahora.hour >= c.metas_hora && !yaFue("metas:$hoy")) {
            val metas = Metas.porRevisar(hoy); val notas = notasVencidas(hoy)
            if (metas.isNotEmpty() || notas.isNotEmpty()) { marcar("metas:$hoy"); Avisos.metas(ctx, metas.map { it.nombre }, notas.map { it.texto.lineSequence().first().take(60) }) }
        }
    }

    /** «Creatina (1 de 2)»: el nombre con la toma que toca, si se toma más de una vez al día; si no, solo el nombre. */
    fun nombreConToma(s: io.github.ruddy2003.vestigium.data.Suplemento, dia: String = Fechas.diaLogico()): String {
        val n = s.tomas_dia.coerceAtLeast(1)
        if (n <= 1) return s.nombre
        val lleva = io.github.ruddy2003.vestigium.data.Suplementos.tomasDe(s.id, dia).size
        return "${s.nombre} (${(lleva + 1).coerceAtMost(n)} de $n)"
    }

    /** Borra la marca «ya se avisó hoy» para que el pulso vuelva a avisar. */
    fun desmarcar(ctx: Context, clave: String) { Repo.cambiarConfig { c -> c.copy(disparos = c.disparos - clave) } }

    const val ACCION = "accion"
    const val MANANA = "manana"
    const val TOME = "tome"
    const val UNA_HORA = "una_hora"
    /** Quitar las tomas que acaba de anotar «Ya los tomé» o el mosaico (sus ids van en [TOMAS]). */
    const val DESHACER_TOMAS = "deshacer_tomas"
    const val TOMAS = "tomas"
}

/** «Recordar mañana» desde el aviso de importaciones: pospone todo lo pendiente un día. */
class RecordatorioReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        io.github.ruddy2003.vestigium.ui.RotacionTema.revisarDesde(context)
        when (intent.getStringExtra(Recordatorios.ACCION)) {
            Recordatorios.MANANA -> {
                val manana = Fechas.sumar(Fechas.hoyCalendario(), 1)
                Recordatorios.pendientes(Fechas.hoyCalendario()).forEach { Recordatorios.posponer(it.id, manana) }
                NotificationManagerCompat.from(context).cancel(Avisos.ID_IMPORTAR)
            }
            Recordatorios.TOME -> {
                val l = io.github.ruddy2003.vestigium.data.Suplementos.registrarFaltantes()
                NotificationManagerCompat.from(context).cancel(Avisos.ID_SUPLEMENTOS)
                // Dice qué anotó y deja deshacerlo un rato.
                if (l.isNotEmpty()) Avisos.tomasAnotadas(context, l)
            }
            Recordatorios.DESHACER_TOMAS -> {
                intent.getStringArrayExtra(Recordatorios.TOMAS)?.forEach { Repo.borrarToma(it) }
                NotificationManagerCompat.from(context).cancel(Avisos.ID_SUPLEMENTOS)
            }
            Recordatorios.UNA_HORA -> {
                // Se vuelve a preguntar en una hora: se borra la marca de la última hora que ya pasó y se guarda desde cuándo.
                context.getSharedPreferences("recordatorios", Context.MODE_PRIVATE).edit().putLong("supl_hasta", System.currentTimeMillis() + 3_600_000L).apply()
                val hora = LocalDateTime.now().hour
                io.github.ruddy2003.vestigium.data.Suplementos.horas().filter { it <= hora }.maxOrNull()
                    ?.let { Recordatorios.desmarcar(context, "supl:" + Fechas.hoyCalendario() + ":" + it) }
                NotificationManagerCompat.from(context).cancel(Avisos.ID_SUPLEMENTOS)
            }
        }
    }
}
