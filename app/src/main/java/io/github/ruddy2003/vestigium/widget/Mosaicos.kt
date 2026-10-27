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

/** Suplementos: un toque anota la dosis habitual de los que faltan hoy; encendido cuando ya están todos. */
class MosaicoSuplemento : TileService() {
    private fun pendientes(): List<io.github.ruddy2003.vestigium.data.Suplemento> = runCatching {
        io.github.ruddy2003.vestigium.data.Repo.init(this)
        val hoy = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
        io.github.ruddy2003.vestigium.data.Suplementos.activos().filter { it.modelo != "exposicion" && !io.github.ruddy2003.vestigium.data.Suplementos.tomadoHoy(it.id, hoy) }
    }.getOrDefault(emptyList())

    private fun pintar() {
        val faltan = pendientes()
        qsTile?.let {
            it.state = if (faltan.isEmpty()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= 29) it.subtitle = if (faltan.isEmpty()) "hoy ya" else faltan.joinToString(", ") { s -> s.nombre.lowercase() }
            it.updateTile()
        }
    }

    override fun onStartListening() { super.onStartListening(); pintar() }

    override fun onClick() {
        super.onClick()
        val l = pendientes().map { io.github.ruddy2003.vestigium.data.Suplementos.registrar(it) }
        // Avisa qué anotó, con «deshacer».
        if (l.isNotEmpty()) runCatching { io.github.ruddy2003.vestigium.avisos.Avisos.tomasAnotadas(this, l) }
        // El guardado en la base es inmediato; el estado en memoria se actualiza en el hilo principal.
        android.os.Handler(mainLooper).postDelayed({ pintar() }, 300)
    }
}
