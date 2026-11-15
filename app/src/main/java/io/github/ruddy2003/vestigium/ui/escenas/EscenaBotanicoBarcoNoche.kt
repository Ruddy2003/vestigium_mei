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

    /** El punto de la botavara que queda encima de la claraboya, en el lienzo: ahí llega el haz de la cabina. */
    private fun sobreLaClaraboya(v: Vista): Offset = VelasBarco.botavaraEn(v, 8.6f)

    /**
     * Lo que las lumbres y la luna dejan en la cubierta y en el aire de detrás de lo cercano (va antes del taller, que se
     * pinta delante): los charcos de la claraboya, de la bitácora y del farol en las tablas y en la amurada, el haz de la
     * cabina que sube hasta la botavara con su polvo, y el relente de la cubierta que brilla hacia la luna.
     */
    fun DrawScope.lumbresEnCubierta(v: Vista) {
        val lu = v.lu; val l = v.l
        if (l.noche < 0.15f) return
        val cubierta = CubiertaBarco.formaDeLaCubierta()
        if (lu.claraboya > 0.02f) {
            val f = lu.claraboya; val boca = LUZ_CLARABOYA
            clipPath(cubierta) { charcoDeLuz(Foco(boca, C.LUMBRE, f, 60f), SITIO_CLARABOYA.p(0f, 0f, 0f), k(8.6f) * 1.7f, k(8.6f) * 0.55f, C.TECA, 1f) }
            // El haz: el aire húmedo de encima de la claraboya, encendido, que sube hasta la botavara y se abre.
            val arriba = sobreLaClaraboya(v)
            val w0 = k(8.6f) * 0.5f; val w1 = w0 * 1.9f
            drawPath(poligono(boca + Offset(-w0, 0f), boca + Offset(w0, 0f), arriba + Offset(w1, 0f), arriba + Offset(-w1, 0f)),
                Brush.verticalGradient(listOf(C.LUMBRE.copy(alpha = 0.04f * f), C.LUMBRE.copy(alpha = 0.2f * f)), arriba.y, boca.y), blendMode = BlendMode.Plus)
            val az = Azar(Semilla.de("botanico", "polvo claraboya"))
            repeat(30) { val t = az.real(); val q = boca + (arriba - boca) * t + Offset(az.entre(-1f, 1f) * (w0 + (w1 - w0) * t) * 0.7f, 0f); drawCircle(C.LUMBRE.copy(alpha = (0.55f * f * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.7f), q) }
            // El palo mayor, encendido por el lado de la claraboya.
            resplandor(SITIO_CLARABOYA.p(0f, 2.2f, 2.2f), 30f, 0.4f * f, C.LUMBRE)
            haloDeBruma(boca + Offset(0f, -4f), k(8.6f) * 0.9f, k(8.6f) * 0.5f, C.LUMBRE, 0.7f * f)
        }
        if (lu.bitacora > 0.02f) {
            val f = lu.bitacora
            clipPath(cubierta) { charcoDeLuz(Foco(LUZ_BITACORA, C.LUMBRE, f, 50f), BarcoBotanico.SITIO_BITACORA.p(0f, 0f, -0.3f), k(5.6f) * 1.2f, k(5.6f) * 0.38f, C.TECA, 0.9f) }
        }
        if (lu.farol > 0.02f) {
            val f = lu.farol
            clipPath(CubiertaBarco.caraDeEstribor()) { charcoDeLuz(Foco(FAROL, C.LUMBRE, f, 60f), pc(estribor(4.2f), 0.6f, 4.2f), 64f, 42f, lerp(C.ARENA, C.CUERO, 0.24f), 0.85f) }
            clipPath(cubierta) { charcoDeLuz(Foco(FAROL, C.LUMBRE, f, 60f), pc(0.9f, 0f, 3.2f), 74f, 24f, C.TECA, 0.75f) }
        }
        if (v.luna > 0.05f) {
            // El relente: la cubierta, húmeda, brilla hacia la luna en una franja larga, y las costuras la parten.
            val lunaX = l.lunaX * ANCHO
            clipPath(cubierta) {
                mancha(Offset(150f + (lunaX - 150f) * 0.4f, H + 26f), 130f, 16f, EscenaBotanico.LUZ_DE_LUNA, 0.22f * v.luna)
                mancha(Offset(150f + (lunaX - 150f) * 0.25f, H + 62f), 100f, 20f, EscenaBotanico.LUZ_DE_LUNA, 0.12f * v.luna)
            }
        }
    }

    /**
     * Lo que las lumbres y la luna dejan en lo alto y en lo cercano (va después de las velas y del taller): el pie de la
     * cangreja y la botavara encendidos por debajo, con la reja de sombra de los barrotes de la claraboya; la rueda, por la
     * lámpara de la bitácora; el halo del farol; y el filo de plata de la luna en la regala, la botavara y el palo.
     */
    fun DrawScope.lumbresEnLoAlto(v: Vista) {
        val lu = v.lu; val l = v.l
        if (l.noche < 0.15f) return
        if (lu.claraboya > 0.02f) {
            val f = lu.claraboya; val boca = LUZ_CLARABOYA; val arriba = sobreLaClaraboya(v)
            resplandor(arriba, 30f, 0.55f * f, C.LUMBRE)
            if (VelasBarco.cangrejaLarga(v)) clipPath(VelasBarco.formaDeLaCangreja(v)) {
                resplandor(arriba + Offset(-10f, -22f), 100f, 0.9f * f, C.LUMBRE)
                // La reja de sombra de los barrotes, abierta en abanico desde la claraboya.
                for (i in -4..4) { val dir = Offset(i * 0.16f, -1f); drawLine(Color(0xFF3A3050).copy(alpha = 0.26f * f), boca + dir * 50f, boca + dir * 220f, 2.4f, blendMode = BlendMode.Multiply) }
            }
        }
        if (lu.bitacora > 0.02f) resplandor(SITIO_TIMON.p(0.5f, 1.2f, 0f), 46f, 0.32f * lu.bitacora, C.LUMBRE)
        if (lu.farol > 0.02f) haloDeBruma(FAROL, 26f, 22f, C.LUMBRE, 0.7f * lu.farol)
        if (v.luna > 0.05f) {
            val zs = (0..30).map { 1.6f + 24f * it / 30f }
            drawPoints(zs.map { pc(estribor(it) + 0.1f, REGALA + 0.04f, it) }, androidx.compose.ui.graphics.PointMode.Polygon, EscenaBotanico.LUZ_DE_LUNA.copy(alpha = 0.5f * v.luna), 0.9f, StrokeCap.Round)
            val (b0, b1) = VelasBarco.botavaraEnLienzo(v)
            drawLine(EscenaBotanico.LUZ_DE_LUNA.copy(alpha = 0.45f * v.luna), b0 + Offset(0f, -2f), b1 + Offset(0f, -5f), 1f, StrokeCap.Round)
            drawLine(EscenaBotanico.LUZ_DE_LUNA.copy(alpha = 0.4f * v.luna), pc(EJE + 0.2f, 0.3f, PALO_MAYOR), pc(EJE + 0.2f, 6f, PALO_MAYOR), 1f)
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
                val z = az.entre(4f, 24f); val q = pc(estribor(z) + az.entre(0.2f, 1f) * (1f + (26f - z) * 0.5f), -2.6f, z)
                val a = (0.5f + 0.5f * sin(reloj * az.entre(1.2f, 2.6f) + az.entre(0f, 6.28f))).let { it * it * it }
                drawCircle(Color(0xFF8FE6D2).copy(alpha = (0.75f * a * l.noche).coerceIn(0f, 1f)), az.entre(0.4f, 1f) * (k(z) / 30f).coerceIn(0.5f, 1.8f), q, blendMode = BlendMode.Plus)
            }
        }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }
}
