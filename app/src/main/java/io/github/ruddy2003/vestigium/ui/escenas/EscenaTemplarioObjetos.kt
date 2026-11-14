package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los doce objetos del día de Bernat (los del cuento: `PiezasBernat.OBJETOS`). Casi todos van sobre el poyo; la lanza
 * y el escudo, grandes, apoyados en él; la campana es la de la espadaña, y ese día su cuerda baja hasta el suelo
 * ([CastilloTemplario]). Todos tienen señales de uso.
 */
internal object ObjetosTemplario {
    private val C = ColorTemplario

    /** Los ids que sabe dibujar: los mismos del calendario. */
    val TODOS = listOf("espada", "lanza", "escudo", "cota", "manto", "silla", "herradura", "almohaza", "regla", "escudilla", "vela", "campana")

    /** Los que van apoyados contra el poyo, con su propia sombra: la lanza y el escudo. */
    fun grande(m: Momento, p: Pincel, sobre: Offset): Cosa? = when (m.ficha.objeto) {
        "lanza" -> {
            val pie = Offset(sobre.x + 26f, 275f); val punta = Offset(sobre.x - 8f, 128f)
            val forma = poligono(pie + Offset(-1.6f, 0f), pie + Offset(1.6f, 0f), punta + Offset(1.6f, 0f), punta + Offset(-1.6f, 0f))
            Cosa(forma, pie) {
                drawLine(p.lado(C.MADERA), pie, punta, 2.2f, StrokeCap.Round)
                drawLine(p.luz(C.MADERA).copy(alpha = 0.5f), pie + Offset(-0.6f, 0f), punta + Offset(-0.6f, 0f), 0.5f)
                val d = (punta - pie) / (punta - pie).getDistance(); val n = Offset(-d.y, d.x)
                drawPath(poligono(punta + n * 2f, punta - n * 2f, punta + d * 13f), p.lado(C.HIERRO))
                drawLine(p.luz(lerp(C.HIERRO, Color.White, 0.5f)), punta + n * 0.4f, punta + d * 11f, 0.6f)
                drawLine(p.medio(C.CUERO), pie + (punta - pie) * 0.42f, pie + (punta - pie) * 0.48f, 3f)              // el puño de cuero
                drawLine(p.medio(C.HIERRO), pie, pie + d * 4f, 2.6f)                                                    // el regatón
            }
        }
        "escudo" -> {
            val cuelga = Offset(sobre.x - 22f, 232f)
            val forma = Path().apply { moveTo(cuelga.x - 11f, cuelga.y + 4f); lineTo(cuelga.x + 11f, cuelga.y + 4f); lineTo(cuelga.x + 11f, cuelga.y + 22f); lineTo(cuelga.x, cuelga.y + 42f); lineTo(cuelga.x - 11f, cuelga.y + 22f); close() }
            Cosa(forma, Offset(cuelga.x, 275f)) {
                rotate(-8f, Offset(cuelga.x, cuelga.y + 42f)) { with(CastilloTemplario) { escudo(p, cuelga, 2.2f, true) } }
            }
        }
        else -> null
    }

    /** Lo que va encima del poyo, centrado en [sobre] (el centro del tablero). */
    fun DrawScope.sobreElPoyo(m: Momento, p: Pincel, sobre: Offset) {
        when (m.ficha.objeto) {
            "espada" -> espada(p, sobre)
            "cota" -> cota(p, sobre)
            "manto" -> manto(p, sobre)
            "silla" -> silla(p, sobre)
            "herradura" -> herraduras(p, sobre)
            "almohaza" -> almohaza(p, sobre)
            "regla" -> regla(p, sobre)
            "escudilla" -> escudilla(p, sobre)
            "vela" -> vela(m, p, sobre)
            else -> Unit                                                                     // la lanza, el escudo y la campana van aparte
        }
    }

    /** La espada sin adornos, de hoja ancha, con su vaina de cuero al lado, la piedra de afilar y la aceitera. */
    private fun DrawScope.espada(p: Pincel, s: Offset) {
        val a = Offset(s.x - 30f, s.y - 1f); val b = Offset(s.x + 18f, s.y - 3f)
        drawLine(p.sombra(C.HIERRO), a + Offset(0f, 1f), b + Offset(0f, 1f), 3.6f, StrokeCap.Round)
        drawLine(p.medio(lerp(C.HIERRO, Color.White, 0.25f)), a, b, 3f, StrokeCap.Round)
        drawLine(p.luz(lerp(C.HIERRO, Color.White, 0.6f)), a + Offset(2f, -0.6f), b + Offset(-2f, -0.6f), 0.6f)                // el filo recién afilado
        drawLine(p.sombra(C.HIERRO), a + Offset(4f, 0f), b + Offset(-8f, 0f), 0.4f)                                            // la canaladura
        // La cruz, el puño de cuero y el pomo.
        drawLine(p.medio(C.HIERRO), Offset(b.x, b.y - 6f), Offset(b.x + 1f, b.y + 5f), 2.2f, StrokeCap.Round)
        drawLine(p.medio(C.CUERO), Offset(b.x + 1f, b.y), Offset(b.x + 10f, b.y - 0.4f), 2.6f, StrokeCap.Round)
        drawCircle(p.lado(C.HIERRO), 2.6f, Offset(b.x + 12f, b.y - 0.5f))
        // La vaina, detrás, y la piedra de afilar con su agua.
        drawLine(p.medio(lerp(C.CUERO, C.MADERA_OSC, 0.3f)), Offset(s.x - 32f, s.y - 5f), Offset(s.x + 10f, s.y - 6.4f), 3.2f, StrokeCap.Round)
        drawRoundRect(p.lado(C.BASALTO), Offset(s.x + 24f, s.y - 5f), Size(10f, 4f), CornerRadius(1.6f))
        drawOval(p.medio(lerp(C.LAGO, Color.White, 0.3f)).copy(alpha = 0.5f), Offset(s.x + 22f, s.y - 1f), Size(14f, 2.4f))
    }

    /** La cota de malla, amontonada, con su textura de anillos, y el saco de arena para limpiarla. */
    private fun DrawScope.cota(p: Pincel, s: Offset) {
        val forma = Path().apply { moveTo(s.x - 26f, s.y); cubicTo(s.x - 26f, s.y - 9f, s.x - 14f, s.y - 14f, s.x - 2f, s.y - 13f); cubicTo(s.x + 10f, s.y - 15f, s.x + 22f, s.y - 9f, s.x + 22f, s.y); close() }
        drawPath(forma, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.HIERRO), p.medio(C.HIERRO), p.luz(lerp(C.HIERRO, Color.White, 0.3f))) else listOf(p.luz(lerp(C.HIERRO, Color.White, 0.3f)), p.medio(C.HIERRO), p.sombra(C.HIERRO)), s.x - 26f, s.x + 22f))
        clipPath(forma) {
            var y = s.y - 15f; var fila = 0
            while (y < s.y) { var x = s.x - 26f + (fila % 2) * 0.9f; while (x < s.x + 22f) { drawCircle(p.luz(lerp(C.HIERRO, Color.White, 0.45f)).copy(alpha = 0.45f), 0.6f, Offset(x, y), style = Stroke(0.3f)); x += 1.8f }; y += 1.4f; fila++ }
            drawLine(p.sombra(C.NEGRO).copy(alpha = 0.6f), Offset(s.x - 8f, s.y - 12f), Offset(s.x - 4f, s.y), 0.8f)
            drawLine(p.medio(Color(0xFF8E5A3A)).copy(alpha = 0.5f), Offset(s.x + 6f, s.y - 10f), Offset(s.x + 12f, s.y - 4f), 1.4f)          // una mancha de óxido
        }
        // El saco de arena, abierto, a un lado.
        val saco = Path().apply { moveTo(s.x + 24f, s.y); lineTo(s.x + 26f, s.y - 12f); cubicTo(s.x + 28f, s.y - 15f, s.x + 34f, s.y - 15f, s.x + 36f, s.y - 12f); lineTo(s.x + 38f, s.y); close() }
        drawPath(saco, Brush.horizontalGradient(listOf(p.lado(C.PAJA, alReves = true), p.lado(C.PAJA)), s.x + 24f, s.x + 38f))
        drawOval(p.arriba(C.CALIZA), Offset(s.x + 27f, s.y - 14.4f), Size(8f, 2.4f))
    }

    /** El manto pardo de sargento, doblado sobre el poyo y caído por delante, con la cruz roja cosida. */
    private fun DrawScope.manto(p: Pincel, s: Offset) {
        val forma = Path().apply { moveTo(s.x - 28f, s.y - 5f); lineTo(s.x + 18f, s.y - 6f); lineTo(s.x + 20f, s.y + 2f); lineTo(s.x + 16f, s.y + 22f); cubicTo(s.x + 6f, s.y + 24f, s.x - 6f, s.y + 21f, s.x - 14f, s.y + 23f); lineTo(s.x - 18f, s.y + 2f); close() }
        drawPath(forma, Brush.verticalGradient(listOf(p.arriba(C.PARDO), p.medio(C.PARDO), p.frente(C.PARDO)), s.y - 6f, s.y + 24f))
        clipPath(forma) {
            for ((x, c) in listOf(s.x - 8f to true, s.x + 4f to false, s.x + 12f to true)) drawLine((if (c) p.sombra(C.PARDO) else p.luz(C.PARDO)).copy(alpha = 0.6f), Offset(x, s.y + 2f), Offset(x - 1f, s.y + 24f), 1.4f)
            // La cruz roja, cosida, que asoma en la caída.
            drawRect(p.medio(C.CRUZ), Offset(s.x - 4f, s.y + 4f), Size(3.2f, 14f)); drawRect(p.medio(C.CRUZ), Offset(s.x - 9f, s.y + 8.6f), Size(13f, 3.2f))
        }
        drawLine(p.luz(C.PARDO), Offset(s.x - 28f, s.y - 5f), Offset(s.x + 18f, s.y - 6f), 0.7f)
    }

    /** La silla de guerra, de arzones altos, con los estribos colgando por delante del poyo. */
    private fun DrawScope.silla(p: Pincel, s: Offset) {
        // La manta de debajo.
        drawPath(poligono(Offset(s.x - 24f, s.y - 2f), Offset(s.x + 22f, s.y - 2f), Offset(s.x + 24f, s.y + 8f), Offset(s.x - 26f, s.y + 8f)), p.medio(lerp(C.LINO, C.PAJA, 0.4f)))
        drawLine(p.medio(C.CRUZ), Offset(s.x - 26f, s.y + 6.6f), Offset(s.x + 24f, s.y + 6.6f), 1f)
        val cuerpo = Path().apply {
            moveTo(s.x - 18f, s.y); lineTo(s.x - 20f, s.y - 22f); cubicTo(s.x - 19f, s.y - 26f, s.x - 14f, s.y - 26f, s.x - 13f, s.y - 20f)
            cubicTo(s.x - 8f, s.y - 10f, s.x + 6f, s.y - 10f, s.x + 11f, s.y - 16f); lineTo(s.x + 14f, s.y - 19f); cubicTo(s.x + 17f, s.y - 20f, s.x + 19f, s.y - 16f, s.x + 17f, s.y - 12f); lineTo(s.x + 18f, s.y); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.CUERO), p.medio(C.CUERO), p.luz(C.CUERO)) else listOf(p.luz(C.CUERO), p.medio(C.CUERO), p.sombra(C.CUERO)), s.x - 20f, s.x + 18f))
        clipPath(cuerpo) {
            drawLine(p.sombra(C.MADERA_OSC).copy(alpha = 0.6f), Offset(s.x - 16f, s.y - 5f), Offset(s.x + 16f, s.y - 5f), 0.6f)
            for (x in listOf(-16f, -11f, 12f, 15f)) drawCircle(p.luz(Color(0xFFC8A050)), 0.6f, Offset(s.x + x, s.y - 3f))
            drawLine(p.luz(C.CUERO).copy(alpha = 0.6f), Offset(s.x - 18f, s.y - 21f), Offset(s.x - 14.6f, s.y - 22f), 1f)
        }
        // La cincha y los estribos, que cuelgan por delante.
        for (x in listOf(s.x - 6f, s.x + 6f)) {
            drawLine(p.medio(C.CUERO), Offset(x, s.y - 4f), Offset(x + 1f, s.y + 16f), 1f)
            drawPath(Path().apply { moveTo(x - 2.6f, s.y + 16f); cubicTo(x - 3f, s.y + 22f, x + 5f, s.y + 22f, x + 4.6f, s.y + 16f) }, p.medio(C.HIERRO), style = Stroke(1f))
            drawLine(p.medio(C.HIERRO), Offset(x - 2.8f, s.y + 20f), Offset(x + 4.8f, s.y + 20f), 1.4f)
        }
    }

    /** Tres herraduras, una nueva y dos gastadas, el martillo y un puñado de clavos. */
    private fun DrawScope.herraduras(p: Pincel, s: Offset) {
        val az = Azar(Semilla.de("templario", "clavos"))
        for ((i, c) in listOf(Offset(s.x - 20f, s.y - 3f), Offset(s.x - 6f, s.y - 2f), Offset(s.x + 6f, s.y - 6f)).withIndex()) {
            val nueva = i == 2; val hierro = if (nueva) lerp(C.HIERRO, Color.White, 0.35f) else lerp(C.HIERRO, Color(0xFF8E5A3A), 0.3f)
            if (nueva) { drawArc(p.medio(hierro), 200f, 320f, false, Offset(c.x - 6f, c.y - 7f), Size(12f, 12f), style = Stroke(2.6f, cap = StrokeCap.Round)); for (k in 0..5) { val t = Math.toRadians(220.0 + k * 50.0); drawCircle(p.sombra(C.NEGRO), 0.4f, c + Offset(cos(t).toFloat() * 6f, sin(t).toFloat() * 6f - 1f)) } }
            else drawArc(p.medio(hierro), 200f, 320f, false, Offset(c.x - 6f, c.y - 3f), Size(12f, 5f), style = Stroke(2.2f, cap = StrokeCap.Round))
        }
        // El martillo de herrar.
        drawLine(p.lado(C.MADERA), Offset(s.x + 14f, s.y - 1f), Offset(s.x + 34f, s.y - 3f), 1.6f, StrokeCap.Round)
        drawRoundRect(p.lado(C.HIERRO), Offset(s.x + 11f, s.y - 6f), Size(4f, 8f), CornerRadius(0.8f))
        repeat(9) { val q = Offset(s.x + az.entre(-30f, 32f), s.y - az.entre(0f, 2f)); drawLine(p.medio(C.HIERRO), q, q + Offset(2.4f, 0.3f), 0.5f) }
    }

    /** La almohaza de hierro con su asa de madera y el cepillo de cerdas, con pelo del caballo enredado. */
    private fun DrawScope.almohaza(p: Pincel, s: Offset) {
        val c = Offset(s.x - 10f, s.y - 2f)
        drawRoundRect(p.lado(C.HIERRO), Offset(c.x - 9f, c.y - 7f), Size(18f, 7f), CornerRadius(1f))
        for (i in 0..5) drawLine(p.sombra(C.NEGRO), Offset(c.x - 8f + i * 3.2f, c.y - 6f), Offset(c.x - 8f + i * 3.2f, c.y - 0.6f), 0.5f)
        drawLine(p.lado(C.MADERA), Offset(c.x + 8f, c.y - 4f), Offset(c.x + 22f, c.y - 6f), 2.2f, StrokeCap.Round)
        // El cepillo.
        val b = Offset(s.x + 18f, s.y - 1f)
        drawRoundRect(p.lado(C.MADERA), Offset(b.x - 8f, b.y - 7f), Size(16f, 4f), CornerRadius(1.6f))
        for (i in 0..10) drawLine(p.medio(C.MADERA_OSC), Offset(b.x - 7.4f + i * 1.5f, b.y - 3f), Offset(b.x - 7.4f + i * 1.5f, b.y), 0.5f)
        val az = Azar(Semilla.de("templario", "pelo"))
        repeat(8) { val q = Offset(c.x + az.entre(-12f, 12f), c.y + az.entre(-1f, 1f)); drawLine(p.medio(Color(0xFF5A3A26)), q, q + Offset(az.entre(-3f, 3f), az.entre(-1f, 0.4f)), 0.4f) }
    }

    /** El libro de la Regla: tapas de tabla forradas de cuero, cantoneras y dos broches, con una cinta. */
    private fun DrawScope.regla(p: Pincel, s: Offset) {
        val x0 = s.x - 18f; val y0 = s.y - 9f
        drawPath(poligono(Offset(x0, y0 + 6f), Offset(x0 + 30f, y0 + 6f), Offset(x0 + 30f, y0 + 9f), Offset(x0, y0 + 9f)), p.medio(lerp(C.LINO, C.PAJA, 0.4f)))      // el canto de las hojas
        for (k in 0..3) drawLine(p.sombra(C.PAJA).copy(alpha = 0.5f), Offset(x0, y0 + 6.6f + k * 0.6f), Offset(x0 + 30f, y0 + 6.6f + k * 0.6f), 0.3f)
        val tapa = poligono(Offset(x0 + 2f, y0), Offset(x0 + 32f, y0 + 0.4f), Offset(x0 + 30f, y0 + 6f), Offset(x0, y0 + 6f))
        drawPath(tapa, p.arriba(lerp(C.CUERO, C.CRUZ, 0.25f)))
        drawPath(tapa, p.sombra(C.MADERA_OSC), style = Stroke(0.6f))
        for (q in listOf(Offset(x0 + 2f, y0), Offset(x0 + 30f, y0 + 0.4f), Offset(x0, y0 + 6f), Offset(x0 + 28f, y0 + 6f))) drawCircle(p.luz(Color(0xFFC8A050)), 1f, q + Offset(1f, 0.6f))
        drawLine(p.medio(Color(0xFFC8A050)), Offset(x0 + 31f, y0 + 2f), Offset(x0 + 34f, y0 + 2.4f), 1f); drawLine(p.medio(Color(0xFFC8A050)), Offset(x0 + 30.6f, y0 + 4.4f), Offset(x0 + 33.6f, y0 + 4.8f), 1f)
        drawLine(p.medio(C.CRUZ), Offset(x0 + 14f, y0 + 9f), Offset(x0 + 16f, y0 + 16f), 0.9f)
        // Una tablilla de cera y su punzón al lado: lo que el capellán lee y lo que apunta.
        drawRoundRect(p.lado(C.MADERA), Offset(s.x + 18f, s.y - 5f), Size(14f, 4f), CornerRadius(0.6f)); drawRect(p.medio(lerp(C.PAJA, C.MADERA_OSC, 0.4f)), Offset(s.x + 19f, s.y - 4.4f), Size(12f, 2.4f))
    }

    /** La escudilla de madera que comparten dos hermanos, con habas, el pan y una cuchara. */
    private fun DrawScope.escudilla(p: Pincel, s: Offset) {
        val c = Offset(s.x - 4f, s.y)
        val cuenco = Path().apply { moveTo(c.x - 14f, c.y - 8f); cubicTo(c.x - 12f, c.y - 1f, c.x + 12f, c.y - 1f, c.x + 14f, c.y - 8f); close() }
        drawPath(cuenco, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.MADERA), p.medio(C.MADERA), p.luz(C.MADERA)) else listOf(p.luz(C.MADERA), p.medio(C.MADERA), p.sombra(C.MADERA)), c.x - 14f, c.x + 14f))
        drawOval(p.arriba(C.MADERA_OSC), Offset(c.x - 14f, c.y - 10f), Size(28f, 4f))
        val az = Azar(Semilla.de("templario", "habas"))
        repeat(16) { val q = Offset(c.x + az.entre(-11f, 11f), c.y - 8.6f + az.entre(-1f, 1f)); drawOval(p.medio(Color(0xFF9A8A4A)), Offset(q.x - 1f, q.y - 0.6f), Size(2f, 1.3f)) }
        drawLine(p.lado(C.MADERA), Offset(c.x + 4f, c.y - 9f), Offset(c.x + 18f, c.y - 16f), 1.2f, StrokeCap.Round)
        // El pan, partido.
        val pan = Offset(s.x + 22f, s.y)
        drawPath(Path().apply { moveTo(pan.x - 8f, pan.y); cubicTo(pan.x - 8f, pan.y - 8f, pan.x + 8f, pan.y - 8f, pan.x + 8f, pan.y); close() }, Brush.verticalGradient(listOf(p.luz(Color(0xFFD8A866)), p.medio(Color(0xFFB07A44))), pan.y - 7f, pan.y))
        drawLine(p.sombra(Color(0xFF7A522A)), Offset(pan.x - 3f, pan.y - 5f), Offset(pan.x + 3f, pan.y - 5f), 0.6f)
    }

    /** La vela de cera de la capilla en su candelero de pincho, con chorreras; encendida de noche. */
    private fun DrawScope.vela(m: Momento, p: Pincel, s: Offset) {
        val c = Offset(s.x, s.y - 1f)
        // El candelero: el plato y sus tres patas.
        drawOval(p.lado(C.HIERRO), Offset(c.x - 7f, c.y - 3f), Size(14f, 3f))
        for (dx in listOf(-5f, 0f, 5f)) drawLine(p.medio(C.HIERRO), Offset(c.x + dx * 0.6f, c.y - 1.6f), Offset(c.x + dx, c.y + 0.6f), 0.8f)
        val cuerpo = Path().apply { moveTo(c.x - 2.6f, c.y - 3f); lineTo(c.x - 2.4f, c.y - 24f); lineTo(c.x + 2.4f, c.y - 25f); lineTo(c.x + 2.6f, c.y - 3f); close() }
        drawPath(cuerpo, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.CERA), p.luz(C.CERA)) else listOf(p.luz(C.CERA), p.sombra(C.CERA)), c.x - 2.6f, c.x + 2.6f))
        for ((dx, h) in listOf(-1.8f to 8f, 1.4f to 5f)) drawLine(p.luz(C.CERA), Offset(c.x + dx, c.y - 24f), Offset(c.x + dx, c.y - 24f + h), 1f, StrokeCap.Round)
        drawLine(p.sombra(C.NEGRO), Offset(c.x, c.y - 25f), Offset(c.x, c.y - 27f), 0.5f)
        val encendida = EscenaTemplario.luces(m).noche > 0.3f
        if (encendida) { drawOval(Color(0xFFFFE6A8), Offset(c.x - 1.2f, c.y - 31f), Size(2.4f, 4.6f)); resplandor(Offset(c.x, c.y - 28f), 20f, 0.5f) }
        // Un cabo viejo de vela, al lado.
        drawRect(p.lado(C.CERA), Offset(c.x + 14f, c.y - 6f), Size(3f, 5f))
        val az = Azar(Semilla.de("templario", "cera"))
        repeat(5) { drawCircle(p.luz(C.CERA), az.entre(0.4f, 0.9f), Offset(c.x + az.entre(-10f, 10f), c.y - az.entre(0f, 1.4f))) }
    }
}
