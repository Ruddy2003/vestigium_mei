package io.github.ruddy2003.vestigium.sensores

import android.content.Context
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.widget.Widgets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Trae ahora mismo todo lo automático: reloj, pantalla, galería, agenda y clima. */
object Sincronizar {
    suspend fun todo(ctx: Context, dias: Int = 3): String = withContext(Dispatchers.IO) {
        val hechos = ArrayList<String>()
        runCatching { if (Salud.sincronizar(ctx, dias) > 0) hechos += "reloj" }
        runCatching { if (Pantalla.tienePermiso(ctx)) { Pantalla.sincronizar(ctx, dias); hechos += "pantalla" } }
        runCatching { if (Galeria.tienePermiso(ctx)) { Galeria.sincronizar(ctx, dias); hechos += "fotos" } }
        runCatching { if (Agenda.tienePermiso(ctx)) { Agenda.sincronizar(ctx); hechos += "agenda" } }
        runCatching { if (Comunicacion.tienePermiso(ctx)) { Comunicacion.sincronizar(ctx, dias); hechos += "llamadas y SMS" } }
        runCatching { Percepcion.muestrear(ctx) }
        runCatching { if (Entorno.sincronizar(ctx) > 0) hechos += "clima" }
        runCatching { Actividad.registrar(ctx) }
        runCatching { Widgets.actualizar(ctx) }
        Repo.cambiarConfig { it.copy(ultimo_sync = Fechas.ahoraIso()) }
        if (hechos.isEmpty()) "No hubo nada nuevo que traer (revisa los permisos)." else "Actualizado: ${hechos.joinToString(", ")}."
    }
}
