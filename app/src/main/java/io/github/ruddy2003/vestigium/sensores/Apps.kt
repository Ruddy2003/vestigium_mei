package io.github.ruddy2003.vestigium.sensores

import android.content.Context

/** Nombre visible de una app a partir de su paquete, con memoria para no preguntarle al sistema cada vez. */
object Apps {
    private val cache = HashMap<String, String>()

    @Suppress("DEPRECATION")
    @Synchronized
    fun nombre(ctx: Context, pkg: String): String = cache.getOrPut(pkg) {
        try {
            val pm = ctx.packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
        } catch (e: Exception) { pkg }
    }
}
