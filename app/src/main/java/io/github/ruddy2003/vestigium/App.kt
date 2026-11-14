package io.github.ruddy2003.vestigium

import android.app.Application
import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.avisos.Programador
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Geocercas

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Repo.init ya no lanza (deja el error en Repo.errorAlAbrir); por si acaso, un fallo aquí tampoco cierra la app:
        // sin esto, un error al abrir la base la dejaba cerrándose una y otra vez, sin poder ni exportar.
        runCatching { Repo.init(this) }
        io.github.ruddy2003.vestigium.data.Mundo.app = this
        // El tema del día antes que los canales: si el proceso arranca por una alarma de aviso después de las 5:00, el aviso
        // ya sale con el tema de hoy (con su voz y su sonido).
        runCatching { io.github.ruddy2003.vestigium.ui.RotacionTema.revisar() }
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
