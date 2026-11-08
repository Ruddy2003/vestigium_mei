package io.github.ruddy2003.vestigium.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import io.github.ruddy2003.vestigium.ui.QuickActivity

/** Botones del panel de ajustes rápidos (donde están el WiFi y la linterna). */
abstract class MosaicoBase(private val modo: String, private val req: Int) : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        qsTile?.let { it.state = Tile.STATE_INACTIVE; it.updateTile() }
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()
        val i = Intent(this, QuickActivity::class.java)
            .putExtra(QuickActivity.MODO, modo).putExtra(QuickActivity.ORIGEN, "panel")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (Build.VERSION.SDK_INT >= 34) {
            startActivityAndCollapse(PendingIntent.getActivity(this, req, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(i)
        }
    }
}

class MosaicoMomento : MosaicoBase(QuickActivity.M_MENU, 3401)
class MosaicoGasto : MosaicoBase(QuickActivity.M_GASTO, 3402)
class MosaicoAnimo : MosaicoBase(QuickActivity.M_ANIMO, 3403)

/** El cronómetro de enfoque: encendido mientras corre un bloque o una siesta. */
class MosaicoEnfoque : MosaicoBase(QuickActivity.M_ENFOQUE, 3404) {
    override fun onStartListening() {
        super.onStartListening()
        val corriendo = runCatching { io.github.ruddy2003.vestigium.data.Repo.init(this); io.github.ruddy2003.vestigium.data.Enfoque.enCurso() }.getOrNull()
        qsTile?.let {
            it.state = if (corriendo != null) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= 29) it.subtitle = corriendo?.let { c -> io.github.ruddy2003.vestigium.data.Catalogo.nombreTramo(c.first) } ?: ""
            it.updateTile()
        }
    }
}

/**
 * Suplementos: un toque anota la dosis habitual de los que les falta una toma hoy; si ya no falta ninguno, suma otra toma
 * de cada uno (para quien toma algo más veces que las que puso). Encendido cuando a ninguno le falta.
 */
class MosaicoSuplemento : TileService() {
    /** Los activos que se toman (sin los de «solo porciones»). */
    private fun tomables(): List<io.github.ruddy2003.vestigium.data.Suplemento> = runCatching {
        io.github.ruddy2003.vestigium.data.Repo.init(this)
        io.github.ruddy2003.vestigium.data.Suplementos.activos().filter { it.modelo != "exposicion" }
    }.getOrDefault(emptyList())

    /** Cuántas tomas faltan hoy, sumando las de todos (esquema 21: un suplemento puede ir más de una vez al día). */
    private fun faltantes(): Int = runCatching {
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        tomables().sumOf { s -> (s.tomas_dia.coerceAtLeast(1) - io.github.ruddy2003.vestigium.data.Suplementos.tomasDe(s.id, hoy).size).coerceAtLeast(0) }
    }.getOrDefault(0)

    private fun pintar() {
        val n = faltantes()
        qsTile?.let {
            it.state = if (n == 0) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= 29) it.subtitle = if (n == 0) "hoy ya" else "falta $n"
            it.updateTile()
        }
    }

    override fun onStartListening() { super.onStartListening(); pintar() }

    override fun onClick() {
        super.onClick()
        val l = runCatching {
            val todos = tomables()
            val faltan = io.github.ruddy2003.vestigium.data.Suplementos.registrarFaltantes()
            // Si no faltaba ninguno, otra toma de cada uno.
            if (faltan.isEmpty()) todos.map { io.github.ruddy2003.vestigium.data.Suplementos.registrar(it) } else faltan
        }.getOrDefault(emptyList())
        // Avisa qué anotó, con «deshacer».
        if (l.isNotEmpty()) runCatching { io.github.ruddy2003.vestigium.avisos.Avisos.tomasAnotadas(this, l) }
        // El guardado en la base es inmediato; el estado en memoria se actualiza en el hilo principal.
        android.os.Handler(mainLooper).postDelayed({ pintar() }, 300)
    }
}
