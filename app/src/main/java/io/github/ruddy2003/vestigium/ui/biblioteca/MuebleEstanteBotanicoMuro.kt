package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La pared del gabinete en la corona del estante de Botánico ([MuebleEstanteBotanico]): el mismo enlucido de verde salvia del
 * Ideario, con la luz de arriba a la izquierda (de fuera del cuadro), sus pinceladas, unas grietas finas y los agujeros de
 * clavos viejos, y arriba la moldura de nogal para colgar cuadros. De noche, el halo del quinqué y el verde de su pantalla; al
 * ocaso, el último sol que entra bajo desde la izquierda; con la luna, su parche frío arriba a la izquierda.
 */
internal object MuroEstanteBotanico {
    private val P = PaletaBotanico

    /** Dónde corre la moldura de colgar cuadros, en anchos desde arriba, y su alto. */
    const val MOLDURA_Y = 0.045f
    const val MOLDURA_ALTO = 0.016f

    /**
     * El parche de luz que entra de la izquierda en la pared, en el ocaso o con la luna: el rectángulo de una ventana que queda
     * fuera del cuadro, tendido por el ángulo de la luz, sin montantes (el Ideario no la enseña). En un lienzo de ancho [w];
     * [crece] lo agranda (la penumbra).
     */
    fun parche(w: Float, a: AireBotanico, crece: Float = 0f): Path? = when {
        a.oscuro && a.noche -> poligonoBotanico(
            w * (-0.05f - crece), w * (0.1f - crece), w * (0.3f + crece), w * (0.17f - crece),
            w * (0.38f + crece), w * (0.52f + crece), w * (-0.05f - crece), w * (0.48f + crece))
        !a.oscuro && a.noche -> poligonoBotanico(
            w * (-0.05f - crece), w * (0.2f - crece), w * (0.62f + crece), w * (0.27f - crece),
            w * (0.7f + crece), w * (0.5f + crece), w * (-0.05f - crece), w * (0.52f + crece))
        else -> null
    }

    /** La pared de la corona, de arriba hasta donde pisan las cosas ([apoyo]) y un poco más (la tapa la cubre). */
    fun DrawScope.pared(s: SitioBotanico, apoyo: Float) {
        val w = size.width; val u = density
        val a = s.a
        val abajo = apoyo + w * 0.03f
        val r = Rect(0f, 0f, w, abajo)
        if (!a.oscuro && !a.noche) {
            // De día, como en el Ideario: más clara arriba a la izquierda, hacia la sombra a la derecha y abajo.
            drawRect(Brush.horizontalGradient(listOf(mezcla(P.SALVIA, LUZ_MUEBLE, 0.25f), P.SALVIA, mezcla(P.SALVIA, P.SALVIA_OSC, 0.55f)), 0f, w), r.topLeft, r.size)
            mancha(Offset(w * 0.05f, 0f), w * 0.78f, luzMueble(0.42f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.14f)), apoyo * 0.45f, abajo), Offset(0f, apoyo * 0.45f), Size(w, abajo - apoyo * 0.45f))
        } else if (!a.oscuro) {
            // Al ocaso: la pared baja de tono hacia la derecha y abajo; el sol bajo la pinta en su parche.
            val t0 = s.t(Offset(w * 0.9f, apoyo))
            val t1 = s.t(Offset(w * 0.1f, w * 0.05f))
            drawRect(Brush.linearGradient(listOf(t1.c(P.SALVIA, 0.04f), t0.c(P.SALVIA, -0.1f)), Offset(0f, 0f), Offset(w, abajo)), r.topLeft, r.size)
        } else {
            // De noche: el halo del quinqué en la pared (con su verde arriba de la pantalla) y lo lejano hundido.
            val l = Offset(MuebleEstanteBotanico.LAMP_X * w, MuebleEstanteBotanico.LAMP_Y * w)
            fun k(d: Float) = s.t(l + Offset(-d * w, d * w * 0.25f)).c(P.SALVIA)
            drawRect(Brush.radialGradient(0f to k(0.02f), 0.1f to k(0.1f), 0.22f to k(0.22f), 0.4f to k(0.4f), 0.65f to k(0.66f), 1f to k(1.1f),
                center = l + Offset(0f, w * 0.04f), radius = w * 1.1f), r.topLeft, r.size)
        }
        // El parche del ocaso o de la luna: la misma pared con esa luz, con su penumbra.
        parche(w, a)?.let { pa ->
            val lit = s.a.en(0.1f, 0.3f, 0.15f).let { t -> if (a.oscuro) t.c(P.SALVIA) else t.c(P.SALVIA, 0.06f) }
            parche(w, a, 0.03f)?.let { drawPath(it, lit.copy(alpha = 0.3f)) }
            parche(w, a, 0.012f)?.let { drawPath(it, lit.copy(alpha = 0.45f)) }
            drawPath(pa, lit)
        }
        pinceladas(r, if (a.oscuro) P.NOCHE else P.SALVIA_OSC, 9001, 46, u, 0.1f)
        pinceladas(r, if (a.oscuro) P.NOCHE else mezcla(P.SALVIA, LUZ_MUEBLE, 0.4f), 9002, 22, u, 0.1f)
        // Grietas finas del enlucido y clavos viejos.
        listOf(Offset(0.31f, 0.33f), Offset(0.68f, 0.06f), Offset(0.95f, 0.36f)).forEachIndexed { i, f ->
            val p0 = Offset(w * f.x, w * f.y)
            val t = s.t(p0)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 5) { q += Offset(w * (0.008f + 0.012f * azar(9010 + i, j)) * (if (j % 2 == 0) 1f else -0.45f), w * (0.011f + 0.01f * azar(9020 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, t.s(0.24f), style = Stroke(0.7f * u))
            translate(0.8f * u, 0.8f * u) { drawPath(g, t.b(0.25f), style = Stroke(0.5f * u)) }
        }
        listOf(Offset(0.12f, 0.27f), Offset(0.41f, 0.13f), Offset(0.58f, 0.36f), Offset(0.97f, 0.16f)).forEach { f ->
            val p = Offset(w * f.x, w * f.y)
            val t = s.t(p)
            drawCircle(t.s(0.38f), 1.1f * u, p)
            drawCircle(t.b(0.3f), 0.7f * u, p + Offset(0.6f * u, 0.6f * u))
        }
        moldura(s)
    }

    /** La moldura de nogal para colgar cuadros: su perfil (el bocel con su luz) y la sombra que deja en la pared. */
    private fun DrawScope.moldura(s: SitioBotanico) {
        val w = size.width; val u = density
        val y0 = w * MOLDURA_Y; val y1 = y0 + w * MOLDURA_ALTO
        val n = if (s.a.lumbre) 14 else 1
        for (i in 0 until n) {
            val x0 = w * i / n; val x1 = w * (i + 1) / n + 1f
            val t = s.t(Offset((x0 + x1) / 2, y0))
            drawRect(t.s(0.3f), Offset(x0, y1), Size(x1 - x0, 3.2f * u))
            drawRect(t.s(0.14f), Offset(x0, y1 + 3.2f * u), Size(x1 - x0, 4f * u))
            drawRect(t.c(P.NOGAL, 0.08f), Offset(x0, y0), Size(x1 - x0, y1 - y0))
            drawRect(t.c(mezcla(P.NOGAL, P.ROBLE, 0.4f), 0.1f), Offset(x0, y0 + (y1 - y0) * 0.18f), Size(x1 - x0, (y1 - y0) * 0.36f))
            drawRect(t.c(P.NOGAL_OSC), Offset(x0, y1 - (y1 - y0) * 0.28f), Size(x1 - x0, (y1 - y0) * 0.28f))
        }
        val t = s.t(Offset(w * 0.3f, y0))
        vetas(Rect(0f, y0, w, y1), t.c(P.NOGAL_OSC).copy(alpha = 0.4f), 9030, 0.8f, grosor = 0.5f)
        drawRect(t.b(0.5f), Offset(0f, y0 + (y1 - y0) * 0.2f), Size(w, 0.9f * u))
        drawRect(t.s(0.3f), Offset(0f, y0), Size(w, 0.8f * u))
    }

    /**
     * La luz de la noche por encima de lo que tiene cerca: el halo cálido del quinqué y el verde de su pantalla arriba (como
     * en el Ideario). En los Sueños oscuros, más bajo.
     */
    fun DrawScope.luzDeNoche(s: SitioBotanico, ch: Float) {
        val a = s.a
        if (!a.oscuro) return
        val w = size.width
        val l = Offset(MuebleEstanteBotanico.LAMP_X * w, MuebleEstanteBotanico.LAMP_Y * w)
        val f = if (a.noche) 0.55f else 1f
        mancha(l + Offset(0f, w * 0.06f), w * 0.4f, P.LLAMA.copy(alpha = 0.1f * f))
        mancha(l + Offset(0f, w * 0.05f), w * 0.15f, P.LLAMA.copy(alpha = 0.2f * f))
        mancha(l - Offset(0f, w * 0.09f), w * 0.13f, P.VIDRIO_VERDE.copy(alpha = 0.13f * f))
    }
}
