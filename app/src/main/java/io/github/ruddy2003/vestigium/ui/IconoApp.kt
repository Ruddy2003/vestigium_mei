package io.github.ruddy2003.vestigium.ui

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import io.github.ruddy2003.vestigium.data.Repo

/**
 * El ícono del teléfono es un «alias» por tema; solo uno está activo a la vez. Se cambia al salir de la app,
 * porque algunos lanzadores tardan unos segundos en redibujar el ícono (y pueden quitarlo de la pantalla de inicio).
 */
object IconoApp {
    private fun alias(ctx: Context, t: TemaId) = ComponentName(ctx.packageName, "io.github.ruddy2003.vestigium.Icono" + t.id.replaceFirstChar { it.uppercase() })

    /** Alias de temas que ya no existen (Día de Muertos): se conservan para que la app no se quede sin ícono al actualizar, y se apagan. */
    private val VIEJOS = listOf("IconoMuertos")

    fun deseado(): TemaId = if (Repo.config.icono_tema) Estilo.tema else TemaId.ROMANO

    fun aplicar(ctx: Context) = runCatching {
        val pm = ctx.packageManager
        val quiero = deseado()
        val activo = pm.getComponentEnabledSetting(alias(ctx, quiero))
        val yaEsta = activo == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
            (quiero == TemaId.ROMANO && activo == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        val otrosApagados = TemaId.entries.filter { it != quiero }.all { t ->
            val e = pm.getComponentEnabledSetting(alias(ctx, t))
            e == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || (t != TemaId.ROMANO && e == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        }
        val viejos = VIEJOS.map { ComponentName(ctx.packageName, "io.github.ruddy2003.vestigium.$it") }
        val viejosApagados = viejos.all { pm.getComponentEnabledSetting(it) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED }
        if (yaEsta && otrosApagados && viejosApagados) return@runCatching
        // Primero se enciende el nuevo y luego se apagan los demás: nunca queda la app sin ícono.
        pm.setComponentEnabledSetting(alias(ctx, quiero), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        TemaId.entries.filter { it != quiero }.forEach { t ->
            pm.setComponentEnabledSetting(alias(ctx, t), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        }
        viejos.forEach { runCatching { pm.setComponentEnabledSetting(it, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP) } }
    }
}
