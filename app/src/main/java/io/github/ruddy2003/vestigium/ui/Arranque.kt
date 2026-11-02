package io.github.ruddy2003.vestigium.ui

import android.app.Activity
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Repo

/**
 * La pantalla de arranque de cada tema (`Theme.Vestigium.Arranque.<Id>` en `res/values/themes.xml`, con su `.Oscuro`).
 *
 * Un alias del ícono no puede llevar tema propio, así que el arranque se elige desde la app con
 * `SplashScreen.setSplashScreenTheme` (Android 13 o más): Android lo guarda y lo usa en el SIGUIENTE arranque. Por eso se
 * vuelve a elegir cada vez que la app sabe el tema y el modo (al abrir y al cambiar de tema o de claro a oscuro).
 * En Android 11 y 12 no hay forma de elegirlo: queda el neutro oscuro de `Theme.Vestigium`.
 */
object ArranqueTema {
    /** El estilo de arranque del tema, claro u oscuro (el que solo existe en oscuro tiene uno solo). */
    fun estilo(t: TemaId, oscuro: Boolean): Int = when (t) {
        TemaId.ROMANO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Romano_Oscuro else R.style.Theme_Vestigium_Arranque_Romano
        TemaId.URBE -> if (oscuro) R.style.Theme_Vestigium_Arranque_Urbe_Oscuro else R.style.Theme_Vestigium_Arranque_Urbe
        TemaId.CIBER -> R.style.Theme_Vestigium_Arranque_Ciber
        TemaId.NEON -> R.style.Theme_Vestigium_Arranque_Neon
        TemaId.HELENO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Heleno_Oscuro else R.style.Theme_Vestigium_Arranque_Heleno
        TemaId.EGIPTO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Egipto_Oscuro else R.style.Theme_Vestigium_Arranque_Egipto
        TemaId.MEDIEVAL -> if (oscuro) R.style.Theme_Vestigium_Arranque_Medieval_Oscuro else R.style.Theme_Vestigium_Arranque_Medieval
        TemaId.TEMPLARIO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Templario_Oscuro else R.style.Theme_Vestigium_Arranque_Templario
        TemaId.DISTOPIA -> if (oscuro) R.style.Theme_Vestigium_Arranque_Distopia_Oscuro else R.style.Theme_Vestigium_Arranque_Distopia
        TemaId.REFUGIO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Refugio_Oscuro else R.style.Theme_Vestigium_Arranque_Refugio
        TemaId.ORDEN -> if (oscuro) R.style.Theme_Vestigium_Arranque_Orden_Oscuro else R.style.Theme_Vestigium_Arranque_Orden
        TemaId.VAPOR -> if (oscuro) R.style.Theme_Vestigium_Arranque_Vapor_Oscuro else R.style.Theme_Vestigium_Arranque_Vapor
        TemaId.LABORATORIO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Laboratorio_Oscuro else R.style.Theme_Vestigium_Arranque_Laboratorio
        TemaId.COLONOS -> if (oscuro) R.style.Theme_Vestigium_Arranque_Colonos_Oscuro else R.style.Theme_Vestigium_Arranque_Colonos
        TemaId.NAPOLEON -> if (oscuro) R.style.Theme_Vestigium_Arranque_Napoleon_Oscuro else R.style.Theme_Vestigium_Arranque_Napoleon
        TemaId.BOTANICO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Botanico_Oscuro else R.style.Theme_Vestigium_Arranque_Botanico
        TemaId.RUINAS -> if (oscuro) R.style.Theme_Vestigium_Arranque_Ruinas_Oscuro else R.style.Theme_Vestigium_Arranque_Ruinas
        TemaId.NAVIDAD -> if (oscuro) R.style.Theme_Vestigium_Arranque_Navidad_Oscuro else R.style.Theme_Vestigium_Arranque_Navidad
        TemaId.SEDA -> if (oscuro) R.style.Theme_Vestigium_Arranque_Seda_Oscuro else R.style.Theme_Vestigium_Arranque_Seda
        TemaId.EDO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Edo_Oscuro else R.style.Theme_Vestigium_Arranque_Edo
        TemaId.URSS -> if (oscuro) R.style.Theme_Vestigium_Arranque_Urss_Oscuro else R.style.Theme_Vestigium_Arranque_Urss
        TemaId.GOTICO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Gotico_Oscuro else R.style.Theme_Vestigium_Arranque_Gotico
        TemaId.ARCANO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Arcano_Oscuro else R.style.Theme_Vestigium_Arranque_Arcano
        TemaId.ESPACIO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Espacio_Oscuro else R.style.Theme_Vestigium_Arranque_Espacio
        TemaId.ABISMO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Abismo_Oscuro else R.style.Theme_Vestigium_Arranque_Abismo
        TemaId.ANDALUS -> if (oscuro) R.style.Theme_Vestigium_Arranque_Andalus_Oscuro else R.style.Theme_Vestigium_Arranque_Andalus
        TemaId.MESO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Meso_Oscuro else R.style.Theme_Vestigium_Arranque_Meso
        TemaId.MONGOL -> if (oscuro) R.style.Theme_Vestigium_Arranque_Mongol_Oscuro else R.style.Theme_Vestigium_Arranque_Mongol
        TemaId.RENACIMIENTO -> if (oscuro) R.style.Theme_Vestigium_Arranque_Renacimiento_Oscuro else R.style.Theme_Vestigium_Arranque_Renacimiento
        TemaId.PIRATA -> if (oscuro) R.style.Theme_Vestigium_Arranque_Pirata_Oscuro else R.style.Theme_Vestigium_Arranque_Pirata
        TemaId.REVOLUCION -> if (oscuro) R.style.Theme_Vestigium_Arranque_Revolucion_Oscuro else R.style.Theme_Vestigium_Arranque_Revolucion
        TemaId.ANTIGUAS -> if (oscuro) R.style.Theme_Vestigium_Arranque_Antiguas_Oscuro else R.style.Theme_Vestigium_Arranque_Antiguas
        TemaId.CUEVA -> if (oscuro) R.style.Theme_Vestigium_Arranque_Cueva_Oscuro else R.style.Theme_Vestigium_Arranque_Cueva
    }

    /** ¿Va en oscuro? Lo mismo que [modoOscuro], pero fuera de Compose (antes de dibujar el primer cuadro). */
    fun oscuro(a: Activity, t: TemaId): Boolean = t.soloOscuro || when (Repo.config.modo) {
        "claro" -> false
        "oscuro" -> true
        else -> (a.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Pone el fondo del tema en la ventana (para que entre el arranque y el primer cuadro no asome el neutro) y deja
     * elegido el arranque del tema para la próxima vez que se abra la app.
     */
    fun aplicar(a: Activity, t: TemaId, oscuro: Boolean) {
        a.window?.setBackgroundDrawable(ColorDrawable(esquemaDe(t, oscuro).background.toArgb()))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try { a.splashScreen.setSplashScreenTheme(estilo(t, oscuro)) } catch (_: RuntimeException) { }
        }
    }
}
