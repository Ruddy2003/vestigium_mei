package io.github.ruddy2003.vestigium

import android.app.Application
import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.avisos.Programador
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Geocercas

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Repo.init(this)
        io.github.ruddy2003.vestigium.data.Mundo.app = this
        Avisos.crearCanales(this)
        Programador.asegurar(this)
        // Solo si cambiaron los lugares o pasó un día: el proceso arranca muy seguido (pulsos, widgets, geocercas).
        runCatching { Geocercas.registrar(this, soloSiCambio = true) }
        runCatching { io.github.ruddy2003.vestigium.sensores.Actividad.registrar(this) }
        org.osmdroid.config.Configuration.getInstance().apply {
            userAgentValue = io.github.ruddy2003.vestigium.data.Red.AGENTE
            osmdroidBasePath = java.io.File(cacheDir, "osmdroid")
            osmdroidTileCache = java.io.File(cacheDir, "osmdroid/mosaicos")
        }
    }
}
