package io.github.ruddy2003.vestigium.ui

import android.content.Context
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo

/**
 * El tema que cambia solo cada día (a las 5:00, cuando empieza el día de la app), entre los que elegiste. Básico
 * ([TemaId.sinDiseno]) nunca entra: es para probar la app, no para que toque un día.
 *
 * Se revisa en cada sitio por donde puede salir un aviso **antes** de publicarlo (el arranque del proceso, el pulso de 15
 * minutos, las alarmas del ánimo, del enfoque y de los recordatorios, el mosaico y las pantallas sueltas). Antes solo lo
 * hacía la app al abrir y el pulso al final, después de avisar: el primer aviso de la mañana podía salir con el tema de ayer.
 */
object RotacionTema {
    /** Los temas que pueden tocar en la rotación (todos menos Básico). */
    val rotables: List<TemaId> get() = TemaId.entries.filterNot { it.sinDiseno }

    fun candidatos(c: Config = Repo.config): List<TemaId> =
        c.temas_rotacion.mapNotNull { id -> rotables.firstOrNull { it.id == id } }.ifEmpty { rotables }

    /** El tema que le toca a [dia]: al azar, pero siempre el mismo para ese día y nunca igual al de ayer. */
    fun deDia(dia: String, actual: String, c: Config = Repo.config): TemaId {
        val l = candidatos(c)
        val opciones = l.filter { it.id != actual }.ifEmpty { l }
        return opciones[Math.floorMod(java.util.Random(dia.hashCode().toLong() * 31 + 7).nextInt(), opciones.size)]
    }

    /** El tema al que hay que cambiar [hoy], o null si no toca (rotación apagada o ya cambiado hoy). No escribe nada. */
    fun tocaRotar(c: Config, hoy: String): TemaId? {
        if (!c.tema_rotar || c.tema_rotado == hoy) return null
        return deDia(hoy, c.tema, c)
    }

    /**
     * Si toca, cambia el tema de hoy. Devuelve true si cambió. Con la base sin abrir bien no escribe: la configuración en
     * memoria sería la de fábrica y pisaría la buena.
     */
    @Synchronized
    fun revisar(): Boolean {
        if (Repo.errorAlAbrir != null) return false
        val hoy = Fechas.diaLogico()
        val t = tocaRotar(Repo.config, hoy) ?: return false
        Repo.cambiarConfig { it.copy(tema = t.id, tema_rotado = hoy) }
        return true
    }

    /**
     * Lo que cambia fuera de la app con el tema: el widget, el ícono, los accesos directos y el fondo del teléfono. Es lo
     * pesado, así que lo hace el pulso (no la alarma de un aviso, que tiene que salir ya). Lo de la app (widget, ícono y
     * accesos) solo se rehace si el tema cambió desde la última vez; el fondo lo decide [FondosTelefono.alDia].
     */
    fun propagar(ctx: Context) {
        val prefs = ctx.getSharedPreferences("rotacion", Context.MODE_PRIVATE)
        val tema = Repo.config.tema
        if (prefs.getString("propagado", null) != tema) {
            runCatching { io.github.ruddy2003.vestigium.widget.Widgets.actualizar(ctx) }
            IconoApp.aplicar(ctx)
            AccesosDirectos.publicar(ctx)
            prefs.edit().putString("propagado", tema).apply()
        }
        runCatching { FondosTelefono.alDia(ctx) }
    }

    /** Para las alarmas y los botones de los avisos: revisa el tema y, si cambió, pide un pulso para lo pesado. */
    fun revisarDesde(ctx: Context) {
        if (runCatching { revisar() }.getOrDefault(false)) runCatching { io.github.ruddy2003.vestigium.avisos.Programador.ahora(ctx) }
    }
}
