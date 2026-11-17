package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDeHiguera
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * **El marco del pueblo de Borgoña (provisional)**: en el pueblo Jean-Baptiste ya no es soldado y una tienda de oficial
 * no pinta nada, así que el marco de la izquierda es la parra de su puerta («una parra en la puerta», dice el cuento):
 * el poste de madera, la viga que se sale por arriba, la cepa retorcida que sube por el poste y la masa de hojas (la
 * hoja de cinco lóbulos de la biblioteca), con los racimos de septiembre y octubre; en invierno, la cepa desnuda. Echa
 * su sombra de follaje en el suelo. **[Diseño propio]**, provisional hasta que el pueblo tenga su cuadro.
 */
internal object VivacParra {
    private val C = ColorNapoleon
    private val HOJA_PARRA = lerp(C.PINO, C.HOJA, 0.5f)
    private val UVA = lerp(C.AZUL, C.ROJO, 0.45f)
    private val CEPA = lerp(C.MADERA_OSC, C.CAPOTE, 0.25f)

    /** El poste: su pie y su cabeza (cortada por arriba). */
    private val POSTE = Offset(96f, 252f) to Offset(90f, -6f)

    /** Cuánta hoja tiene la parra hoy, de 0 (invierno: la cepa desnuda) a 1. */
    fun hojas(m: Momento): Float = when (m.ficha.dia.substring(5, 7).toInt()) { 12, 1, 2, 3 -> 0f; 4 -> 0.35f; 5 -> 0.75f; 11 -> 0.3f; else -> 1f }

    /** Si hay racimos: maduros en septiembre y octubre (la vendimia), verdes en agosto. */
    fun uvas(m: Momento): Float = when (m.ficha.dia.substring(5, 7).toInt()) { 8 -> 0.4f; 9, 10 -> 1f; else -> 0f }

    /** La sombra del follaje de la parra en el suelo de delante (una capa de sombra). */
    fun DrawScope.sombra(v: VistaVivac) {
        val k = hojas(v.m); if (k <= 0.05f) return
        val r = VivacLuz.rayo(v.l)
        capaSombra(r, 0.8f * k) { c -> sombraDeFollaje(Azar(Semilla.de("napoleon", "vivac", "parra", "sombra")), Rect(-40f + r.ax * 30f, 228f, 190f + r.ax * 30f, 290f), r, c, 40, 110) }
    }

    fun DrawScope.parra(v: VistaVivac) {
        val a = v.en(POSTE.first); val p = v.p; val az = Azar(Semilla.de("napoleon", "vivac", "parra"))
        val lado = if (p.solALaDerecha) 1f else -1f
        val (pie, cabeza) = POSTE
        // El poste y la viga, de madera vieja, con su lado de luz.
        val g = 7f
        drawPath(poligono(pie + Offset(-g / 2f, 0f), pie + Offset(g / 2f, 0f), cabeza + Offset(g / 2.4f, 0f), cabeza + Offset(-g / 2.4f, 0f)), a.plano(C.MADERA_GRIS, pie, 0f))
        drawLine(a.plano(C.MADERA_GRIS, pie, lado), pie + Offset(lado * g * 0.3f, 0f), cabeza + Offset(lado * g * 0.25f, 0f), g * 0.3f)
        for (j in 0 until 6) { val y = 40f + j * 34f; drawLine(mult(a.plano(C.MADERA_GRIS, pie, 0f), C.SOMBRA, 0.4f), Offset(pie.x - 2f + j % 2, y), Offset(pie.x - 1f, y + 12f), 0.5f) }
        val viga = poligono(Offset(-10f, -4f), Offset(cabeza.x + 10f, 8f), Offset(cabeza.x + 10f, 13f), Offset(-10f, 2f))
        if (hojas(v.m) > 0.05f) drawPath(viga, a.plano(C.MADERA_GRIS, Offset(40f, 22f), 0f, 0f, -0.4f))
        // La cepa: tronco retorcido que sube enroscado al poste y se abre por la viga.
        val cepa = Path().apply { moveTo(Offset(pie.x + 10f, pie.y)); cubicTo(Offset(pie.x + 18f, pie.y - 60f), Offset(pie.x - 14f, pie.y - 110f), Offset(pie.x + 6f, pie.y - 160f)); cubicTo(Offset(pie.x + 16f, pie.y - 200f), Offset(pie.x - 8f, 50f), Offset(pie.x - 30f, 30f)) }
        drawPath(cepa, a.plano(CEPA, pie, 0f), style = Stroke(5f, cap = StrokeCap.Round))
        drawPath(cepa, a.plano(lerp(CEPA, C.LONA, 0.2f), pie, lado), style = Stroke(1.4f, cap = StrokeCap.Round))
        // Los sarmientos por la viga.
        for (j in 0 until 7) { val x0 = az.entre(-10f, cabeza.x + 10f); val q0 = Offset(x0, 24f); val q1 = q0 + Offset(az.entre(-30f, 30f), az.entre(20f, 60f))
            drawLine(a.plano(CEPA, q0, 0f), q0, q1, 1.2f, StrokeCap.Round) }
        // Las hojas: una masa oscura detrás y las hojas una por una delante, más claras las que reciben el sol (regla 2).
        val k = hojas(v.m)
        if (k > 0.05f) {
            val otono = v.m.ficha.dia.substring(5, 7).toInt() >= 10
            val verde = if (otono) lerp(HOJA_PARRA, C.LATON, 0.5f) else HOJA_PARRA
            // La masa de detrás, que cuelga de la viga y baja por el poste.
            repeat((60 * k).toInt()) { val t = az.real(); val c = Offset(az.entre(-14f, 175f - 90f * t), -6f + 110f * t * az.real()); mancha(c, az.entre(10f, 20f), az.entre(7f, 12f), a.plano(lerp(verde, C.PINO_OSC, 0.55f), c, 0f), 0.9f) }
            // Las hojas una por una: más grandes cerca del poste, las de arriba al sol.
            repeat((190 * k).toInt()) {
                val t = az.real(); val c = Offset(az.entre(-14f, 180f - 95f * t), -8f + 120f * t * az.real())
                val rad = az.entre(5f, 9.5f); val hoja = hojaDeHiguera(c, rad, az.entre(-0.7f, 0.7f))
                val mira = ((c.x - 80f) / 90f).coerceIn(-1f, 1f) * lado
                drawPath(hoja, a.plano(if (az.real() < 0.25f) lerp(verde, C.LONA, 0.15f) else verde, c, mira, 0f, 0.2f + 0.6f * az.real()))
                drawLine(mult(a.plano(verde, c, 0f), C.SOMBRA, 0.45f), c, c + Offset(0f, rad * 0.8f), 0.5f)
            }
            // Los racimos.
            val u = uvas(v.m)
            if (u > 0.05f) repeat(7) {
                val c = Offset(az.entre(10f, 150f), az.entre(26f, 78f)); val uva = lerp(lerp(C.PINO_LUZ, C.HOJA, 0.3f), UVA, u)
                for (j in 0 until 14) { val f = j / 14f; val q = c + Offset(az.entre(-4f, 4f) * (1f - f), f * 13f); drawCircle(a.plano(uva, q, if (j % 3 == 0) lado else 0f), 2.1f * (1f - 0.4f * f), q) }
            }
        }
    }
}
