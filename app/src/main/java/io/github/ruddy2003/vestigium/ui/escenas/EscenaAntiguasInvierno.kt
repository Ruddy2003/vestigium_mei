package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.CUMBRE
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.H
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * El mal tiempo de la montaña (solo uno de cada cuatro días es despejado) y lo último de la parte quieta.
 *
 * - **La niebla y la bruma por capas** ([capa]), como en Horror cósmico: un banco entre cada plano (detrás de la ladera
 *   del paso, de la de las vetas, del valle y del primer plano), cada uno más espeso cuanto más lejos y más arriba; con
 *   niebla cerrada, «no se ven ni las runas»: las cumbres y el collado se pierden, la ladera se adivina, la aldea queda
 *   gris y la puerta, el pino y la posada siguen ahí. Con nubes bajas o lluvia, las cumbres se meten en las nubes. Las
 *   mañanas claras, la bruma del torrente.
 * - **La lluvia y la nieve que caen** y el relámpago, en la parte viva; el velo de la lluvia, en la quieta.
 * - **El grano**, al final.
 */
internal object InviernoAntiguas {
    private val C = ColorAntiguas

    /** Cuánta bruma hay entre los planos con cada nivel de niebla (0 a 3), detrás de cada uno de los cuatro planos. */
    private val ESPESOR = listOf(
        listOf(0f, 0.38f, 0.6f, 0.9f),
        listOf(0f, 0.22f, 0.42f, 0.8f),
        listOf(0f, 0.12f, 0.3f, 0.6f),
        listOf(0f, 0.05f, 0.16f, 0.3f),
    )

    /** De dónde a dónde va el banco de cada plano. */
    private val BANDAS = listOf(40f to 205f, 100f to 214f, 150f to 226f, 196f to 258f)

    /**
     * El banco de bruma de detrás del plano [nivel] (0: detrás de las cumbres, antes de la ladera del paso; 1: antes de la
     * ladera de las vetas; 2: antes del valle; 3: antes del primer plano).
     */
    fun DrawScope.capa(v: VistaAntiguas, nivel: Int) {
        val k = ESPESOR[nivel][v.niebla]
        val (y0, y1) = BANDAS[nivel]
        if (k > 0.01f) {
            val color = lerp(v.l.bruma, lerp(v.l.bajo, Color.White, 0.2f), 0.35f)
            drawRect(Brush.verticalGradient(0f to color.copy(alpha = 0.55f * k), 0.6f to color.copy(alpha = k), 1f to color.copy(alpha = 0.2f * k), startY = y0, endY = y1), Offset(0f, y0), Size(ANCHO, y1 - y0))
            val az = v.m.azar("bancos", nivel)
            var x = -30f
            while (x < ANCHO + 30f) { val w = az.entre(50f, 100f); mancha(Offset(x, az.entre(y0 + 0.3f * (y1 - y0), y1 - 6f)), w, (y1 - y0) * 0.16f, color, (0.6f * k).coerceIn(0f, 1f)); x += w * az.entre(0.55f, 0.85f) }
        }
        // Las nubes bajas que se comen las cumbres con lluvia, nieve o nubes cargadas.
        if (nivel == 0 && (v.clima == Clima.LLUVIA || v.clima == Clima.TORMENTA || v.clima == Clima.NIEVE || v.clima == Clima.NUBES && v.niebla > 0)) nubesBajas(v)
        // La bruma del torrente, al alba de los días claros.
        if (nivel == 2 && v.niebla == 0 && v.m.hora >= 5.5 && v.m.hora < 9.5 && v.clima != Clima.VIENTO) {
            val f = ((9.5 - v.m.hora) / 2.0).toFloat().coerceIn(0f, 1f) * (1f - 0.5f * v.l.noche)
            val color = lerp(v.l.bajo, Color.White, 0.3f)
            for ((x, y) in listOf(80f to 214f, 160f to 208f, 240f to 200f, 310f to 196f)) mancha(Offset(x, y), 70f, 6f, color, 0.4f * f)
        }
    }

    /** Las nubes que se sientan en las cumbres: bancos tendidos, de base plana, que tapan lo alto. */
    private fun DrawScope.nubesBajas(v: VistaAntiguas) {
        val az = v.m.azar("nubes bajas")
        val claro = lerp(v.l.medio, Color.White, 0.25f); val oscuro = lerp(v.l.medio, lerp(C.SOMBRA, C.ROCA, 0.5f), 0.4f)
        val y = az.entre(36f, 66f)
        var x = -20f
        while (x < ANCHO + 20f) {
            val w = az.entre(40f, 80f)
            mancha(Offset(x, y + az.entre(-8f, 8f)), w, az.entre(10f, 16f), oscuro, 0.6f)
            mancha(Offset(x, y - 6f + az.entre(-6f, 4f)), w * 0.8f, az.entre(7f, 11f), claro, 0.55f)
            x += w * az.entre(0.6f, 0.9f)
        }
    }

    /** El tiempo quieto de la biblioteca (el velo de la lluvia, la claridad de la nieve); la niebla no: va por capas. */
    fun DrawScope.tiempo(v: VistaAntiguas) {
        if (v.clima != Clima.NIEBLA) tiempoQuieto(v.l, v.clima, H, v.m.semilla("tiempo"))
    }

    fun DrawScope.grano(v: VistaAntiguas) {
        grano(Azar(Semilla.de("antiguas", "grano")), 0.6f + 0.4f * v.r.dia)
    }

    /** Lo que se mueve del tiempo: la lluvia, la nieve, el relámpago y, con viento, la nieve que el viento arranca de la cumbre. */
    fun DrawScope.vivo(v: VistaAntiguas, reloj: Float) {
        tiempoVivo(v.l, v.clima, reloj, H, v.m.semilla("tiempo"))
        if (v.clima == Clima.VIENTO && v.nieveArriba > 0.3f) {
            val c = lerp(v.l.bajo, Color.White, 0.55f)
            for (k in 0 until 6) {
                val t = ((reloj * 0.08f + k / 6f) % 1f)
                mancha(CUMBRE + Offset(6f + 60f * t, -2f - 10f * t + 2f * sin(reloj + k)), 6f + 14f * t, 2f + 3f * t, c, 0.35f * (1f - t))
            }
        }
    }
}
