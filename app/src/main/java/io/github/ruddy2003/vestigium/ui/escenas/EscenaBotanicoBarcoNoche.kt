package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.EJE
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.FAROL
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.H
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.LUZ_BITACORA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.LUZ_CLARABOYA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.PALO_MAYOR
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.REGALA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_CLARABOYA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_TIMON
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.Vista
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.estribor
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.k
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * La noche del bergantín, a dos luces de color contrario, como el taller de Renacimiento (`mejores_referencias.md`):
 *
 * - **La cálida, de dentro:** la cabina encendida (el diario, de 19 a 22) sube por la claraboya en un haz tibio que
 *   enciende por debajo la botavara, el pie de la cangreja y el palo, con la reja de sombra de los barrotes; la
 *   lámpara de la bitácora alumbra la rueda y la cubierta de alrededor; el farol de popa tiñe la amurada, los cajones y
 *   el agua. Cada una deja su charco en las tablas.
 * - **La fría, de fuera:** la luna platea la cara de las velas que la mira (lo hace [VelasBarco] con el mismo rayo), deja
 *   su brillo en la cubierta mojada de relente, su estela en el mar y un filo en los cantos (la regala, la botavara); y
 *   tumba las sombras de todo en la capa de sombras de la cubierta (`EscenaBotanico.rayo` da de noche la luna).
 *
 * En la parte viva: los destellos del mar, el reflejo tembloroso del farol y, de noche, el mar que brilla en la espuma
 * de la marcha («el mar brilla de noche»).
 */
internal object NocheBarco {
    private val C = ColorBotanico

    fun DrawScope.lumbres(v: Vista) {
        val lu = v.lu; val l = v.l
        if (l.noche < 0.15f) return
        // ---- La cabina, por la claraboya.
        if (lu.claraboya > 0.02f) {
            val f = lu.claraboya
            val boca = LUZ_CLARABOYA
            // El charco en la cubierta alrededor de la claraboya, y el que sube por el palo.
            clipPath(CubiertaBarco.formaDeLaCubierta()) { charcoDeLuz(Foco(boca, C.LUMBRE, f, 60f), SITIO_CLARABOYA.p(0f, 0f, 0f), k(8.6f) * 1.6f, k(8.6f) * 0.5f, C.TECA, 0.9f) }
            // El haz: el aire húmedo de encima de la claraboya, encendido, que sube hasta la botavara y se abre.
            val (b0, b1) = VelasBarco.botavaraEnLienzo(v)
            val arriba = b0 + (b1 - b0) * 0.12f
            val w0 = k(8.6f) * 0.55f; val w1 = w0 * 2.4f
            drawPath(poligono(boca + Offset(-w0, 0f), boca + Offset(w0, 0f), arriba + Offset(w1 * 0.6f, 0f), arriba + Offset(-w1, 0f)),
                Brush.verticalGradient(listOf(C.LUMBRE.copy(alpha = 0.03f * f), C.LUMBRE.copy(alpha = 0.16f * f)), arriba.y, boca.y), blendMode = BlendMode.Plus)
            val az = Azar(Semilla.de("botanico", "polvo claraboya"))
            repeat(26) { val t = az.real(); val q = boca + (arriba - boca) * t + Offset(az.entre(-1f, 1f) * (w0 + (w1 - w0) * t) * 0.7f, 0f); drawCircle(C.LUMBRE.copy(alpha = (0.5f * f * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.7f), q) }
            // La botavara y el palo, encendidos por debajo.
            resplandor(arriba, 34f, 0.5f * f, C.LUMBRE)
            resplandor(SITIO_CLARABOYA.p(0f, 2.2f, 1.8f), 30f, 0.35f * f, C.LUMBRE)
            if (VelasBarco.cangrejaLarga(v)) clipPath(VelasBarco.formaDeLaCangreja(v)) {
                // La reja de sombra de los barrotes de la claraboya, abierta en abanico sobre el pie de la vela.
                val pie = VelasBarco.pieDeLaCangreja(v)
                resplandor(arriba + Offset(-14f, -26f), 110f, 0.85f * f, C.LUMBRE)
                resplandor(pie, 70f, 0.5f * f, C.LUMBRE)
                for (i in -3..3) { val dir = Offset(i * 0.2f, -1f); drawLine(Color(0xFF3A3050).copy(alpha = 0.22f * f), boca + dir * 40f, boca + dir * 200f + Offset(pie.x - boca.x, 0f) * 0.3f, 2.2f, blendMode = BlendMode.Multiply) }
            }
            haloDeBruma(boca + Offset(0f, -6f), k(8.6f) * 0.9f, k(8.6f) * 0.5f, C.LUMBRE, 0.6f * f)
        }
        // ---- La lámpara de la bitácora en la rueda y en la cubierta.
        if (lu.bitacora > 0.02f) {
            val f = lu.bitacora
            clipPath(CubiertaBarco.formaDeLaCubierta()) { charcoDeLuz(Foco(LUZ_BITACORA, C.LUMBRE, f, 50f), BarcoBotanico.SITIO_BITACORA.p(0f, 0f, -0.3f), k(5.5f) * 1.1f, k(5.5f) * 0.35f, C.TECA, 0.8f) }
            resplandor(SITIO_TIMON.p(0.5f, 1.2f, 0f), 46f, 0.32f * f, C.LUMBRE)
        }
        // ---- El farol de popa: la amurada, los cajones y la cubierta de la aleta.
        if (lu.farol > 0.02f) {
            val f = lu.farol
            clipPath(CubiertaBarco.caraDeEstribor()) { charcoDeLuz(Foco(FAROL, C.LUMBRE, f, 60f), pc(estribor(4.2f), 0.6f, 4.2f), 60f, 40f, C.AMURADA, 0.8f) }
            clipPath(CubiertaBarco.formaDeLaCubierta()) { charcoDeLuz(Foco(FAROL, C.LUMBRE, f, 60f), pc(0.9f, 0f, 3.2f), 70f, 22f, C.TECA, 0.7f) }
            haloDeBruma(FAROL, 26f, 22f, C.LUMBRE, 0.7f * f)
        }
        // ---- La luna: el relente de la cubierta, que brilla hacia ella, y el filo de plata de la regala y la botavara.
        if (v.luna > 0.05f) {
            val lunaX = l.lunaX * ANCHO
            clipPath(CubiertaBarco.formaDeLaCubierta()) {
                mancha(Offset(150f + (lunaX - 150f) * 0.4f, H + 26f), 120f, 16f, EscenaBotanico.LUZ_DE_LUNA, 0.16f * v.luna)
                mancha(Offset(150f + (lunaX - 150f) * 0.25f, H + 60f), 90f, 18f, EscenaBotanico.LUZ_DE_LUNA, 0.08f * v.luna)
            }
            val zs = (0..30).map { 1.6f + 24f * it / 30f }
            drawPoints(zs.map { pc(estribor(it) + 0.1f, REGALA + 0.04f, it) }, androidx.compose.ui.graphics.PointMode.Polygon, EscenaBotanico.LUZ_DE_LUNA.copy(alpha = 0.45f * v.luna), 0.8f, StrokeCap.Round)
            val (b0, b1) = VelasBarco.botavaraEnLienzo(v)
            drawLine(EscenaBotanico.LUZ_DE_LUNA.copy(alpha = 0.4f * v.luna), b0 + Offset(0f, -2f), b1 + Offset(0f, -5f), 1f, StrokeCap.Round)
            drawLine(EscenaBotanico.LUZ_DE_LUNA.copy(alpha = 0.35f * v.luna), pc(EJE + 0.2f, 0.3f, PALO_MAYOR), pc(EJE + 0.2f, 6f, PALO_MAYOR), 1f)
        }
    }

    // ================================================================ Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaBotanico.luces(m)
        val g = EscenaBotanico.inclinacion(m)
        val zona = Rect(150f, H + 1f, ANCHO, 230f)
        if (g == 0f) destellos(l, zona, reloj, m.semilla("destellos"), 22)
        else withTransform({ rotate(g, Offset(ANCHO / 2f, H)); scale(1.18f, 1.18f, Offset(ANCHO / 2f, H)) }) { destellos(l, zona, reloj, m.semilla("destellos"), 26) }
        val lu = BarcoBotanico.lumbres(m, l)
        if (lu.farol > 0.05f) reflejo(Offset(FAROL.x + 18f, 214f), 30f, C.LUMBRE, 0.5f * lu.farol, reloj, 3)
        // De noche, el mar brilla en la espuma del costado: chispas verdes que se encienden y se apagan.
        if (l.noche > 0.6f && !EscenaBotanico.fondeado(m)) {
            val az = Azar(Semilla.de("botanico", "mar que brilla"))
            repeat(24) {
                val z = az.entre(4f, 24f); val q = pc(estribor(z) + az.entre(0.2f, 1.6f) * (1f + (26f - z) / 10f), -2.6f, z)
                val a = (0.5f + 0.5f * sin(reloj * az.entre(1.2f, 2.6f) + az.entre(0f, 6.28f))).let { it * it * it }
                drawCircle(Color(0xFF8FE6D2).copy(alpha = (0.75f * a * l.noche).coerceIn(0f, 1f)), az.entre(0.4f, 1f) * (k(z) / 30f).coerceIn(0.5f, 1.8f), q, blendMode = BlendMode.Plus)
            }
        }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }
}
