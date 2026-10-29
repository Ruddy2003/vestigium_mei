package io.github.ruddy2003.vestigium.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Icon
import android.os.PersistableBundle
import androidx.compose.ui.graphics.toArgb
import io.github.ruddy2003.vestigium.R

/**
 * Los accesos directos (mantener presionado el ícono): se publican desde el código con el color del tema, en lugar de
 * los fijos del manifiesto. El fondo del ícono es el primario del tema y el dibujo, su color «sobre primario».
 * Se vuelven a publicar solo si cambió el tema, el modo claro u oscuro del teléfono o el ícono activo: Android limita
 * cuántas veces por día puede hacerlo una app en segundo plano.
 */
object AccesosDirectos {
    private class Acceso(val id: String, val corto: Int, val largo: Int, val dibujo: Int, val intent: (Context) -> Intent)

    private val ACCESOS = listOf(
        Acceso("tema_momento", R.string.sc_momento, R.string.sc_momento_largo, R.drawable.ic_sc_momento) { rapido(it, QuickActivity.M_MENU) },
        Acceso("tema_gasto", R.string.sc_gasto, R.string.sc_gasto, R.drawable.ic_sc_gasto) { rapido(it, QuickActivity.M_GASTO) },
        Acceso("tema_diario", R.string.sc_diario, R.string.sc_diario, R.drawable.ic_sc_diario) {
            Intent(Intent.ACTION_VIEW).setClass(it, MainActivity::class.java).putExtra(MainActivity.PANTALLA, "diario")
        },
        Acceso("tema_animo", R.string.sc_animo, R.string.sc_animo, R.drawable.ic_sc_animo) { rapido(it, QuickActivity.M_ANIMO) },
        Acceso("tema_sueno", R.string.sc_sueno, R.string.sc_sueno, R.drawable.ic_sc_sueno) { rapido(it, QuickActivity.M_SUENO) },
    )

    private const val CLAVE = "vestigium_clave"

    private fun rapido(ctx: Context, modo: String) = Intent(Intent.ACTION_VIEW).setClass(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, modo)

    /** El alias del ícono que está encendido (los accesos se cuelgan de él); el romano viene encendido de fábrica. */
    private fun aliasActivo(ctx: Context): ComponentName? {
        val pm = ctx.packageManager
        return TemaId.entries.map { t -> t to ComponentName(ctx.packageName, "io.github.ruddy2003.vestigium.Icono" + t.id.replaceFirstChar { it.uppercase() }) }
            .firstOrNull { (t, c) ->
                val e = runCatching { pm.getComponentEnabledSetting(c) }.getOrDefault(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
                e == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || (t.id == "romano" && e == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
            }?.second
    }

    /** El ícono adaptable: fondo del color del tema y el dibujo en el centro (dentro de la zona que ningún lanzador recorta). */
    private fun icono(ctx: Context, dibujo: Int, fondo: Int, trazo: Int): Icon {
        val lado = (108 * ctx.resources.displayMetrics.density).toInt().coerceAtLeast(108)
        val bmp = Bitmap.createBitmap(lado, lado, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(fondo)
        ctx.getDrawable(dibujo)?.mutate()?.let { d ->
            d.setTint(trazo)
            val m = lado / 6
            d.setBounds(m, m, lado - m, lado - m)
            d.draw(c)
        }
        return Icon.createWithAdaptiveBitmap(bmp)
    }

    /** Publica (o deja como están) los accesos con el color del tema. Seguro de llamar seguido: casi siempre no hace nada. */
    fun publicar(ctx: Context) = runCatching {
        val sm = ctx.getSystemService(ShortcutManager::class.java) ?: return@runCatching
        val t = Estilo.tema
        val noche = (ctx.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        val alias = aliasActivo(ctx)
        val clave = t.id + "|" + noche + "|" + alias?.className
        val hoy = sm.dynamicShortcuts
        if (hoy.size == ACCESOS.size && hoy.all { it.extras?.getString(CLAVE) == clave }) return@runCatching
        val esquema = esquemaDe(t, t.soloOscuro || noche)
        val fondo = esquema.primary.toArgb()
        val trazo = esquema.onPrimary.toArgb()
        val extras = PersistableBundle().apply { putString(CLAVE, clave) }
        val lista = ACCESOS.mapIndexed { i, a ->
            ShortcutInfo.Builder(ctx, a.id)
                .setShortLabel(ctx.getString(a.corto))
                .setLongLabel(ctx.getString(a.largo))
                .setIcon(icono(ctx, a.dibujo, fondo, trazo))
                .setIntent(a.intent(ctx))
                .setRank(i)
                .setExtras(extras)
                .apply { if (alias != null) setActivity(alias) }
                .build()
        }
        sm.setDynamicShortcuts(lista)
        Unit
    }
}
