package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.GrandesAntiguas.silueta
import io.github.ruddy2003.vestigium.ui.escenas.GrandesAntiguas.sombraGrande
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.contacto
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.empolvado
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.hoyo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.labio
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.punto
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.trazo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.tubo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los dos objetos grandes de tela y cáñamo, junto al banco del zaguán: la cuerda adujada, colgada de una estaquilla del
 * muro sobre el banco (algo vuelta hacia quien mira, para que no se vea de canto **[Diseño propio]**), con el chicote que
 * baja por el banco hasta las losas; y la capa de lana, echada sobre el banco, con su forro de piel y su capucha.
 */
internal object CuerdaYCapa {
    private val C = ColorAntiguas
    private val O = ColorObjetos

    /** Cuánto se vuelve hacia quien mira lo que cuelga de la estaquilla (radianes, sobre el muro). */
    private const val VUELTA = 0.35f

    /** Los ejes de lo que cuelga: el centro [o], lo horizontal [ex], lo vertical y lo que sale hacia quien mira [nr]. */
    private class Plano(val o: V3) {
        val ex = V3(cos(VUELTA), 0f, sin(VUELTA)); val nr = V3(sin(VUELTA), 0f, -cos(VUELTA))
        fun p(x: Float, y: Float, d: Float = 0f): V3 = o + ex * x + V3(0f, y, 0f) + nr * d
    }

    /** La estaquilla de madera clavada en el muro, en [x], a [y] de alto. */
    private fun DrawScope.estaquilla(lm: LuzObjeto, x: Float, y: Float, zm: Float) {
        tubo(lm, listOf(V3(x, y, zm), V3(x + 0.01f, y + 0.012f, zm - 0.11f)), listOf(0.017f, 0.015f), lerp(C.MADERA, C.FRESNO, 0.3f), 0.5f)
    }

    // ---------------------------------------------------------------- La cuerda

    /** Las vueltas de la aduja: dónde va cada una en el grueso del rollo (de dentro a fuera) y cuánto sale hacia quien mira. */
    private val VUELTAS = listOf(0.15f to 0.012f, 0.5f to 0.016f, 0.85f to 0.01f, 0.0f to -0.024f, 0.33f to -0.03f, 0.66f to -0.028f, 1.0f to -0.018f)

    /**
     * La cuerda de cáñamo, «trenzado, de treinta brazas»: adujada en un rollo grande y colgada de una estaquilla sobre el
     * banco. Las vueltas se juntan arriba, donde las abraza el amarre de cuatro vueltas, y se abren abajo; cada una con su
     * torcido. El chicote sale del amarre, baja por delante del rollo, pasa por el canto del banco y se queda en las losas
     * en un par de vueltas flojas: por ahí pisa la cosa. 1,2 veces más grande que una de verdad.
     */
    fun cuerda(lm: LuzObjeto, zm: Float): Cosa {
        val pl = Plano(V3(0.2f, 1.18f, zm - 0.14f))
        val chicote = listOf(pl.p(0.05f, 0.27f, -0.05f), pl.p(0.14f, 0.12f, -0.07f), pl.p(0.17f, -0.12f, -0.06f), V3(0.33f, 0.72f, 0.14f), V3(0.3f, 0.48f, 0.07f), V3(0.27f, 0.28f, 0.02f), V3(0.2f, 0.02f, -0.07f),
            V3(0.06f, 0.012f, -0.16f), V3(-0.1f, 0.012f, -0.12f), V3(-0.08f, 0.012f, -0.02f), V3(0.08f, 0.012f, -0.03f), V3(0.16f, 0.012f, -0.1f), V3(0.04f, 0.012f, -0.2f))
        val env = (0 until 12).map { j -> val a = 2f * PI.toFloat() * j / 12f; pl.p(0.26f * cos(a), 0.32f * sin(a)) } + chicote
        return Cosa(silueta(lm, env), lm.s.pie) {
            hoyo(lm, 0.03f, -0.1f, 0.3f)
            contacto(lm, 0.03f, -0.1f, 0.32f, 0.12f)
            sombraGrande(lm, zm, env)
            estaquilla(lm, 0.2f, 1.47f, zm)
            for ((f, d) in VUELTAS.filter { it.second > 0f }) vuelta(lm, pl, f, d)
            for ((f, d) in VUELTAS.filter { it.second <= 0f }) vuelta(lm, pl, f, d)
            amarre(lm, pl)
            tubo(lm, chicote, chicote.indices.map { if (it == chicote.size - 1) 0.011f else 0.012f }, O.CANAMO, 0.5f)
            torcido(lm, chicote, 0.012f)
            punto(lm.q(chicote.last()), 2.2f, 1.6f, lm.c(lerp(O.CANAMO, C.CUERO, 0.5f), V3(0f, 1f, -0.4f)))
            empolvado(lm, (3..9).map { j -> val a = PI.toFloat() * j / 12f; lm.q(pl.p(0.27f * cos(a), 0.33f * sin(a) + 0.01f, -0.01f)) }, 1.2f)
            labio(lm, 0.03f, -0.1f, 0.34f)
        }
    }

    /** Una vuelta de la aduja: un lazo casi redondo que se cierra arriba, en el amarre, con su torcido. */
    private fun DrawScope.vuelta(lm: LuzObjeto, pl: Plano, f: Float, d: Float) {
        val eje = (0..30).map { j ->
            val a = PI.toFloat() / 2f + 2f * PI.toFloat() * j / 30f
            val junta = sin(a).coerceAtLeast(0f).let { it * it * it * it }
            val r = 0.26f + 0.06f * f * (1f - 0.75f * junta)
            pl.p(r * cos(a) * 0.86f, r * sin(a) * 1.08f - 0.02f * f, d * (1f - 0.6f * junta))
        }
        tubo(lm, eje, eje.map { 0.0125f }, lerp(O.CANAMO, C.LANA, 0.15f * f), 0.45f)
        if (d <= 0f) torcido(lm, eje, 0.0125f)
    }

    /** El torcido del cáñamo: rayitas en sesgo a lo largo de un cabo. */
    private fun DrawScope.torcido(lm: LuzObjeto, eje: List<V3>, radio: Float) {
        val c = lm.c(lerp(O.CANAMO, C.ESCORIA, 0.35f), V3(0f, 0.3f, -1f)).copy(alpha = 0.45f)
        for (i in 1 until eje.size) {
            val a = lm.q(eje[i - 1]); val b = lm.q(eje[i]); val d = b - a; val l = d.getDistance(); if (l < 0.5f) continue
            val t = d / l; val n = Offset(-t.y, t.x); val r = radio * lm.k(eje[i].x, eje[i].z)
            val pasos = (l / (r * 1.4f)).toInt().coerceIn(1, 8)
            for (j in 0 until pasos) { val m = a + d * ((j + 0.5f) / pasos); drawLine(c, m - n * r * 0.8f - t * r * 0.45f, m + n * r * 0.8f + t * r * 0.45f, 0.4f) }
        }
    }

    /** El amarre de arriba: cuatro vueltas apretadas que abrazan el rollo bajo la estaquilla. */
    private fun DrawScope.amarre(lm: LuzObjeto, pl: Plano) {
        for (j in 0 until 4) {
            val a = PI.toFloat() / 2f + (j - 1.5f) * 0.075f
            val c = pl.p(0.29f * cos(a) * 0.86f, 0.29f * sin(a) * 1.08f)
            val rad = V3(cos(a) * 0.86f, sin(a) * 1.08f, 0f)
            tubo(lm, listOf(c - pl.ex * (rad.x * 0.06f) - V3(0f, rad.y * 0.06f, 0f) + pl.nr * 0.03f, c + pl.nr * 0.045f, c + pl.ex * (rad.x * 0.06f) + V3(0f, rad.y * 0.06f, 0f) + pl.nr * 0.03f), listOf(0.011f, 0.012f, 0.011f), lerp(O.CANAMO, C.CUERO, 0.2f), 0.5f)
        }
    }

    // ---------------------------------------------------------------- La capa

    /**
     * La capa de lana, «de lana de oveja sin lavar…, con capucha y un forro de piel en los hombros»: echada sobre el banco,
     * doblada, como la deja quien llega empapado: el bulto doblado en el asiento; por el canto, el doblez de los hombros, que
     * enseña el forro de piel; por delante, la caída en pliegues hasta las losas, donde se arremansa con el bajo sucio de
     * barro; la capucha chafada colgando a la izquierda y el botón de madera con su cordón. Nada cuelga en alto ni tiene forma
     * de cuerpo (una capa colgada de un clavo se leía como una figura con capucha) **[Diseño propio]**. 1,2 veces más grande.
     */
    fun capa(lm: LuzObjeto, zm: Float): Cosa {
        val zf = zm - 0.34f; val y = 0.46f; val x0 = -0.24f; val x1 = 0.36f
        val env = listOf(V3(x0 - 0.04f, y + 0.12f, zf + 0.1f), V3(x1 + 0.03f, y + 0.12f, zf + 0.1f), V3(x0 - 0.03f, 0.01f, zf - 0.13f), V3(x1 + 0.05f, 0.01f, zf - 0.11f), V3(-0.15f, y + 0.16f, zf + 0.16f))
        return Cosa(silueta(lm, env), lm.s.pie) {
            hoyo(lm, 0.06f, zf - 0.08f, 0.62f)
            contacto(lm, 0.06f, zf - 0.07f, 0.66f, 0.12f, fuerza = 0.3f)
            sombraGrande(lm, zm, env)
            bulto(lm, zf, y, x0, x1)
            caida(lm, zf, y, x0, x1)
            forro(lm, zf, y, x0, x1)
            capucha(lm, zf, y)
            boton(lm, zf, y)
            empolvado(lm, listOf(lm.q(x0, y + 0.1f, zf + 0.1f), lm.q(-0.05f, y + 0.135f, zf + 0.12f), lm.q(0.15f, y + 0.13f, zf + 0.12f), lm.q(x1 - 0.02f, y + 0.1f, zf + 0.1f)), 1.2f)
            labio(lm, 0.06f, zf - 0.12f, 0.66f)
        }
    }

    /** Lo doblado sobre el asiento, detrás del doblez: un bulto blando de lana. */
    private fun DrawScope.bulto(lm: LuzObjeto, zf: Float, y: Float, x0: Float, x1: Float) {
        val pts = listOf(V3(x0 - 0.02f, y + 0.04f, zf + 0.02f), V3(x1 + 0.01f, y + 0.04f, zf + 0.02f), V3(x1 - 0.01f, y + 0.11f, zf + 0.11f), V3(x1 - 0.06f, y + 0.09f, zf + 0.26f),
            V3(0.05f, y + 0.13f, zf + 0.22f), V3(x0 + 0.04f, y + 0.1f, zf + 0.26f), V3(x0 - 0.03f, y + 0.11f, zf + 0.12f))
        drawPath(poligono(pts.map { lm.q(it) }), Brush.verticalGradient(listOf(lm.c(O.LANA_CAPA, V3(0f, 1f, -0.2f), 0.1f), lm.c(O.LANA_CAPA, V3(0f, 0.3f, -1f), -0.1f)), lm.q(0f, y + 0.13f, zf + 0.2f).y, lm.q(0f, y + 0.04f, zf).y))
        for (j in 0 until 3) trazo(listOf(lm.q(x0 + 0.08f + j * 0.17f, y + 0.12f, zf + 0.2f), lm.q(x0 + 0.12f + j * 0.17f, y + 0.08f, zf + 0.08f)), lm.c(O.LANA_CAPA, V3(0f, 0.6f, -0.6f), -0.6f).copy(alpha = 0.4f), 0.5f)
    }

    /** La caída por delante del banco, en pliegues que se abren hacia abajo, y el bajo arremansado en las losas, sucio de barro. */
    private fun DrawScope.caida(lm: LuzObjeto, zf: Float, y: Float, x0: Float, x1: Float) {
        val n = 8; val az = SitioAntiguas.fijo("capa")
        val arriba = (0..n).map { i -> x0 + (x1 - x0) * i / n }
        val abajo = (0..n).map { i -> x0 - 0.03f + (x1 - x0 + 0.08f) * i / n + if (i in 1 until n) az.entre(-0.015f, 0.015f) else 0f }
        val hondo = (0..n).map { i -> if (i % 2 == 0) -0.035f else 0.01f }
        // El bajo arremansado en el suelo, por delante.
        val charco = abajo.indices.map { i -> V3(abajo[i], 0.03f, zf - 0.04f + hondo[i]) } + abajo.indices.reversed().map { i -> V3(abajo[i] + 0.01f, 0.012f, zf - 0.11f - 0.025f * sin(i * 1.9f)) }
        drawPath(poligono(charco.map { lm.q(it) }), lm.c(lerp(O.LANA_CAPA, C.TIERRA, 0.35f), V3(0f, 1f, -0.4f), -0.1f))
        for (i in 0 until n) {
            val forma = poligono(lm.q(arriba[i], y + 0.02f, zf - 0.012f), lm.q(arriba[i + 1], y + 0.02f, zf - 0.012f), lm.q(abajo[i + 1], 0.03f, zf - 0.04f + hondo[i + 1]), lm.q(abajo[i], 0.03f, zf - 0.04f + hondo[i]))
            val sube = hondo[i + 1] > hondo[i]
            val nA = LuzObjeto.unidad(V3(if (sube) -0.65f else 0.55f, 0.05f, -1f)); val nB = LuzObjeto.unidad(V3(if (sube) 0.25f else -0.3f, 0.05f, -1f))
            drawPath(forma, Brush.horizontalGradient(listOf(lm.c(O.LANA_CAPA, nA), lm.c(O.LANA_CAPA, nB, 0.05f)), lm.q(abajo[i], 0.2f, zf).x, lm.q(abajo[i + 1], 0.2f, zf).x))
            clipPath(forma) {
                for (j in 0 until 7) { val h = 0.06f + j * 0.06f; drawLine(lm.c(O.LANA_CAPA, nA, -0.4f).copy(alpha = 0.16f), lm.q(abajo[i], h, zf - 0.04f), lm.q(abajo[i + 1], h + 0.008f, zf - 0.04f), 0.4f) }
                drawLine(lm.c(lerp(O.LANA_CAPA, C.TIERRA, 0.6f), nA, -0.2f).copy(alpha = 0.7f), lm.q(abajo[i], 0.05f, zf - 0.04f + hondo[i]), lm.q(abajo[i + 1], 0.05f, zf - 0.04f + hondo[i + 1]), 0.05f * lm.k())
            }
        }
    }

    /** El forro de piel de los hombros, que asoma en el doblez del canto del asiento: un rollo claro y lanudo, con sus mechones. */
    private fun DrawScope.forro(lm: LuzObjeto, zf: Float, y: Float, x0: Float, x1: Float) {
        val n = 12; val az = SitioAntiguas.fijo("forro")
        val arriba = (0..n).map { i -> lm.q(x0 - 0.02f + (x1 - x0 + 0.04f) * i / n, y + 0.07f + az.entre(-0.008f, 0.012f), zf + 0.005f) }
        val abajo = (0..n).reversed().map { i -> lm.q(x0 - 0.01f + (x1 - x0 + 0.02f) * i / n, y - 0.015f + az.entre(-0.01f, 0.006f), zf - 0.02f) }
        val forma = poligono(arriba + abajo)
        drawPath(forma, Brush.verticalGradient(listOf(lm.c(O.PELLEJO, V3(0f, 0.8f, -0.6f), 0.15f), lm.c(O.PELLEJO, V3(0f, -0.3f, -1f), -0.2f)), arriba.minOf { it.y }, abajo.maxOf { it.y }))
        val rizo = lm.c(lerp(O.PELLEJO, C.CUERO, 0.35f), V3(0f, 0f, -1f), -0.3f).copy(alpha = 0.55f)
        clipPath(forma) { repeat(30) { val q = lm.q(az.entre(x0, x1), y + az.entre(-0.01f, 0.07f), zf - 0.02f); drawArc(rizo, 20f, 200f, false, q, androidx.compose.ui.geometry.Size(2f, 1.5f), style = Stroke(0.45f)) } }
        for (i in 0 until n) { val q = abajo[i]; drawLine(lm.c(O.PELLEJO, V3(0f, 0f, -1f), 0.05f), q, q + Offset(0.3f * (i % 3 - 1), 1.8f), 1f, androidx.compose.ui.graphics.StrokeCap.Round) }
    }

    /** La capucha, chafada, que cuelga por delante a la izquierda: un pico de lana con su costura y el borde de la cara forrado. */
    private fun DrawScope.capucha(lm: LuzObjeto, zf: Float, y: Float) {
        val pts = listOf(V3(-0.23f, y + 0.06f, zf - 0.03f), V3(0.0f, y + 0.05f, zf - 0.035f), V3(-0.03f, y - 0.16f, zf - 0.065f), V3(-0.13f, y - 0.36f, zf - 0.085f), V3(-0.24f, y - 0.14f, zf - 0.065f))
        val forma = poligono(pts.map { lm.q(it) })
        val lana = lerp(O.LANA_CAPA, C.ESCORIA, 0.12f)
        drawPath(forma, Brush.horizontalGradient(listOf(lm.c(lana, V3(-0.5f, 0.1f, -1f), 0.1f), lm.c(lana, V3(0.6f, 0f, -1f), -0.15f)), lm.q(pts[4]).x, lm.q(pts[1]).x))
        drawPath(forma, lm.c(lerp(lana, C.ESCORIA, 0.4f), V3(0f, 0f, -1f)).copy(alpha = 0.6f), style = Stroke(0.6f))
        trazo(listOf(lm.q(V3(-0.12f, y + 0.04f, zf - 0.04f)), lm.q(V3(-0.13f, y - 0.12f, zf - 0.07f)), lm.q(pts[3])), lm.c(O.LANA_CAPA, V3(0f, 0f, -1f), -0.7f).copy(alpha = 0.55f), 0.5f)
        trazo(listOf(lm.q(pts[1]), lm.q(pts[2]), lm.q(pts[3])), lm.c(O.PELLEJO, V3(0.4f, 0f, -1f), 0.1f), 1.8f)
    }

    /** El botón de madera del cuello, con su cordón, que cuelga del doblez. */
    private fun DrawScope.boton(lm: LuzObjeto, zf: Float, y: Float) {
        val a = V3(0.12f, y + 0.0f, zf - 0.03f); val b = V3(0.14f, y - 0.17f, zf - 0.06f)
        trazo(listOf(lm.q(a), lm.q(V3(0.15f, y - 0.08f, zf - 0.05f)), lm.q(b)), lm.c(C.CUERO, V3(0f, 0f, -1f), -0.1f), 0.6f)
        tubo(lm, listOf(b + V3(0f, 0.012f, 0f), b + V3(0.004f, -0.05f, -0.004f)), listOf(0.013f, 0.013f), lerp(C.MADERA, C.FRESNO, 0.4f), 0.6f)
    }
}
