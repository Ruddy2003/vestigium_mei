package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El color de una cosa de la azotea por su lado de luz, por su lado de sombra y de frente, con el sol [r] de este
 * momento. [calida], de 0 a 1: de noche, cuánto la alumbra además la luz de la casa (la puerta del casetón, el farol),
 * que es cálida y viene de la izquierda.
 */
internal class SolUrbe(val l: Luces, val r: Rayo, val calida: Float = 0f) {
    private fun deCasa(c: Color) = Color(c.red, c.green * 0.74f, c.blue * 0.44f, c.alpha)
    private fun con(base: Color, c: Color, k: Float) = if (calida <= 0f) base else lerp(base, deCasa(c), (calida * k).coerceIn(0f, 1f))
    fun luz(c: Color, lejos: Float = 0f) = con(l.tono(lerp(c, Color(0xFFFFF2D0), 0.34f * r.contraste), lejos), c, 0.8f)
    fun sombra(c: Color, lejos: Float = 0f) = l.tono(mult(c, r.color, 0.85f * r.contraste), lejos)
    fun frente(c: Color, lejos: Float = 0f) = con(if (r.lat < 0f) l.tono(lerp(c, Color(0xFFFFF2D0), 0.14f * r.contraste), lejos) else l.tono(mult(c, r.color, 0.4f * r.contraste), lejos), c, 0.4f)
    fun lado(c: Color, lejos: Float = 0f) = if (r.lat < 0f) sombra(c, lejos) else luz(c, lejos)
}

/**
 * Lo verde de Roma en agosto: los pinos piñoneros (el grande que enmarca la azotea y los lejanos), y las hojas del
 * limonero, los geranios y la adelfa. Nada de bolas lisas: cada copa es una masa de borde irregular, con su sombra
 * honda debajo, su luz encima y el fleco de las agujas; cada hoja se dibuja una por una.
 *
 * **[Aproximación]**: el pino piñonero (el «pino de sombrilla» de Roma) se dibuja como lo describe cualquier guía de
 * árboles: tronco alto y desnudo, de corteza en placas rojizas, que se abre arriba en un abanico de ramas, y una copa
 * ancha y aplastada, en pisos. No se comprobó contra ninguna fuente en esta sesión.
 */
internal object VerdeUrbe {
    private val C = ColorUrbe

    /** Una masa de follaje de borde irregular alrededor de [c]. [plano]: cuánto se aplasta por debajo. */
    fun masa(az: Azar, c: Offset, rx: Float, ry: Float, n: Int = 18, quebrado: Float = 0.22f, plano: Float = 0.7f): Path = Path().apply {
        for (i in 0 until n) {
            val t = 6.2832f * i / n; val k = 1f + az.entre(-quebrado, quebrado)
            val q = c + Offset(cos(t) * rx * k, sin(t) * ry * k * (if (sin(t) > 0f) plano else 1f))
            if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
        }
        close()
    }

    /** Una hoja en punta, con centro en [c], vuelta hacia [ang]. */
    fun hoja(c: Offset, ang: Float, largo: Float, ancho: Float): Path {
        val d = Offset(cos(ang), sin(ang)) * (largo / 2f); val n = Offset(-sin(ang), cos(ang)) * (ancho / 2f)
        return Path().apply { moveTo(c.x - d.x, c.y - d.y); lineTo(c.x - d.x * 0.2f + n.x, c.y - d.y * 0.2f + n.y); lineTo(c.x + d.x, c.y + d.y); lineTo(c.x - d.x * 0.2f - n.x, c.y - d.y * 0.2f - n.y); close() }
    }

    /**
     * Un piso de la copa de un pino piñonero, visto desde abajo: la sombra honda de debajo, el cuerpo, los mechones de
     * luz de arriba y, si es [fino], las agujas: mechones claros en el borde de arriba y flecos oscuros que cuelgan.
     */
    fun DrawScope.piso(s: SolUrbe, az: Azar, c: Offset, rx: Float, ry: Float, lejos: Float, fino: Boolean) {
        val honda = s.sombra(lerp(C.PINO, C.HOLLIN, 0.32f), lejos); val lado = if (s.r.lat > 0f) 1f else -1f
        drawPath(masa(az, c + Offset(0f, ry * 0.3f), rx, ry * 0.8f, if (fino) 22 else 12, 0.2f, 0.5f), honda)
        drawPath(masa(az, c, rx * 0.97f, ry, if (fino) 24 else 12, 0.2f, 0.6f), s.l.tono(C.PINO, lejos))
        repeat(if (fino) 7 else 3) {
            val u = az.entre(-0.74f, 0.74f); val cc = c + Offset(u * rx + lado * rx * 0.08f, -ry * (0.3f + 0.42f * (1f - u * u)))
            drawPath(masa(az, cc, rx * az.entre(0.2f, 0.34f), ry * az.entre(0.3f, 0.46f), 10, 0.3f, 0.5f), s.luz(lerp(C.PINO_LUZ, C.PINO, 0.25f), lejos))
        }
        if (!fino) return
        val claro = s.luz(C.PINO_LUZ, lejos)
        repeat(30) { val u = az.entre(-1f, 1f); val p = c + Offset(u * rx, -ry * sqrt(1f - u * u) * az.entre(0.85f, 1.1f)); for (d in -1..1) drawLine(claro, p, p + Offset(d * 1.7f + u * 1.4f, -2.2f - az.real() * 1.6f), 0.5f) }
        repeat(34) { val u = az.entre(-1f, 1f); val p = c + Offset(u * rx, ry * (0.42f + 0.32f * sqrt(1f - u * u))); for (d in -1..1) drawLine(honda, p, p + Offset(d * 1.6f + u * 1.3f, 2.4f + az.real() * 2f), 0.5f) }
    }

    /**
     * Un pino piñonero lejano, que pisa en [pie]. [forma]: 0, derecho; 1, inclinado; 2, de dos copas; 3, alto y de
     * copa corta; 4, achaparrado y ancho. Ninguno es igual al de al lado.
     */
    fun DrawScope.pinoLejano(s: SolUrbe, az: Azar, pie: Offset, alto: Float, ancho: Float, lejos: Float, forma: Int) {
        val l = s.l; val corteza = l.tono(lerp(C.MADERA_OSC, C.ROSA_ROMA, 0.4f), lejos); val g = (alto * 0.055f).coerceIn(0.6f, 2.2f)
        val cae = when (forma) { 1 -> alto * 0.28f; 2 -> -alto * 0.1f; else -> alto * az.entre(-0.06f, 0.06f) }
        val w = when (forma) { 3 -> ancho * 0.7f; 4 -> ancho * 1.25f; else -> ancho }; val h = when (forma) { 4 -> alto * 0.8f; else -> alto }
        val horca = pie + Offset(cae * 0.7f, -h * 0.72f); val copa = pie + Offset(cae, -h * 0.9f)
        drawPath(Path().apply { moveTo(pie.x, pie.y); cubicTo(pie.x + cae * 0.1f, pie.y - h * 0.3f, horca.x - cae * 0.2f, horca.y + h * 0.2f, horca.x, horca.y) }, corteza, style = Stroke(g, cap = StrokeCap.Round))
        if (forma == 2) {
            // La segunda copa, más baja, del otro lado.
            val otra = pie + Offset(w * 0.5f, -h * 0.66f)
            drawLine(corteza, pie + Offset(cae * 0.3f, -h * 0.42f), otra + Offset(0f, 1f), g * 0.7f, StrokeCap.Round)
            piso(s, az, otra, w * 0.3f, h * 0.085f, lejos, false)
        }
        // El abanico de ramas bajo la copa.
        for (k in -2..2) drawLine(corteza, horca, copa + Offset(k * w * 0.19f, h * 0.02f), g * 0.5f, StrokeCap.Round)
        piso(s, az, copa + Offset(-w * 0.14f, h * 0.02f), w * 0.36f, h * 0.1f, lejos, false)
        piso(s, az, copa + Offset(w * 0.16f, -h * 0.02f), w * 0.4f, h * 0.11f, lejos, false)
        if (forma != 4) piso(s, az, copa + Offset(0f, -h * 0.08f), w * 0.3f, h * 0.085f, lejos, false)
    }

    /**
     * El pino grande, que sube del patio de al lado y enmarca la azotea por la derecha: el tronco de corteza en placas,
     * el abanico de ramas y la copa en tres pisos, con claros por donde se ve el cielo. No tapa el sol de las 7:00 ni
     * la luna de las 21:00.
     */
    fun DrawScope.pinoGrande(s: SolUrbe, pretil: Float) {
        val l = s.l; val r = s.r; val az = Azar(Semilla.de("urbe", "pino grande"))
        val corteza = lerp(C.MADERA_OSC, C.ROSA_ROMA, 0.4f); val rama = l.tono(corteza); val ramaLuz = s.luz(lerp(corteza, C.ROSA_ROMA, 0.4f))
        fun ramas(lista: List<Triple<Offset, Offset, Offset>>, gruesa: Float) {
            for ((a, c, b) in lista) {
                val medio = Offset((a.x + 2f * c.x + b.x) / 4f, (a.y + 2f * c.y + b.y) / 4f)
                drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(c.x, c.y, c.x, c.y, b.x, b.y) }, rama, style = Stroke(gruesa * 0.45f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(a.x, a.y); cubicTo((a.x + c.x) / 2f, (a.y + c.y) / 2f, (a.x + c.x) / 2f, (a.y + c.y) / 2f, medio.x, medio.y) }, rama, style = Stroke(gruesa, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(a.x, a.y - gruesa * 0.3f); cubicTo((a.x + c.x) / 2f, (a.y + c.y) / 2f - gruesa * 0.3f, (a.x + c.x) / 2f, (a.y + c.y) / 2f - gruesa * 0.3f, medio.x, medio.y - gruesa * 0.3f) }, ramaLuz.copy(alpha = 0.6f), style = Stroke(0.6f, cap = StrokeCap.Round))
                // Las ramillas, que se meten en la copa.
                for (k in -1..1) drawLine(rama, b, b + Offset(k * 5f + (b.x - a.x) * 0.06f, -5f - az.real() * 3f), 0.7f, StrokeCap.Round)
            }
        }
        // El piso de arriba, y sus ramas.
        piso(s, az, Offset(300f, 13f), 30f, 8f, 0f, true); piso(s, az, Offset(350f, 10f), 34f, 9f, 0f, true); piso(s, az, Offset(386f, 15f), 14f, 8f, 0f, true)
        ramas(listOf(Triple(Offset(361f, 62f), Offset(354f, 36f), Offset(350f, 15f)), Triple(Offset(338f, 37f), Offset(318f, 28f), Offset(300f, 18f))), 3f)
        // El de en medio.
        piso(s, az, Offset(270f, 24f), 22f, 6.5f, 0f, true); piso(s, az, Offset(322f, 24f), 28f, 8.5f, 0f, true); piso(s, az, Offset(369f, 28f), 24f, 8.5f, 0f, true)
        ramas(listOf(Triple(Offset(361f, 64f), Offset(346f, 46f), Offset(322f, 29f)), Triple(Offset(332f, 46f), Offset(300f, 44f), Offset(272f, 28f)), Triple(Offset(363f, 62f), Offset(368f, 46f), Offset(370f, 32f))), 3.4f)
        // El de abajo, que deja un claro en medio.
        piso(s, az, Offset(312f, 33f), 22f, 6.5f, 0f, true); piso(s, az, Offset(385f, 47f), 16f, 6.5f, 0f, true)
        ramas(listOf(Triple(Offset(360f, 70f), Offset(340f, 64f), Offset(313f, 38f)), Triple(Offset(364f, 68f), Offset(376f, 62f), Offset(384f, 50f))), 3.8f)
        // Unas piñas, bajo la copa.
        for (q in listOf(Offset(319f, 43.6f), Offset(331f, 33f), Offset(379f, 55f), Offset(291f, 34f), Offset(357f, 21f), Offset(304f, 41f))) {
            drawOval(s.sombra(lerp(C.MADERA_OSC, C.OCRE_ROMA, 0.3f)), Offset(q.x - 1.5f, q.y - 2f), Size(3f, 4f)); drawOval(s.luz(lerp(C.MADERA_OSC, C.OCRE_ROMA, 0.6f)), Offset(q.x - 1.2f + r.lat * 0.4f, q.y - 1.9f), Size(1.7f, 2.4f))
        }
        // El tronco: la corteza en placas rojizas, separadas por grietas oscuras; un lado a la luz y el otro en sombra.
        val tronco = Path().apply { moveTo(357f, pretil); cubicTo(355f, 150f, 364f, 104f, 356f, 66f); lineTo(367f, 60f); cubicTo(377f, 104f, 371f, 150f, 375f, pretil); close() }
        volumen(l, r, tronco, 355f, 376f, lerp(corteza, C.HOLLIN, 0.3f))
        clipPath(tronco) {
            val rojiza = lerp(C.TEJA, C.ROSA_ROMA, 0.45f)
            var y = 60f
            while (y < pretil) {
                val alto = az.entre(10f, 19f); var x = 353f + az.entre(0f, 2f)
                while (x < 377f) {
                    val w = az.entre(2.6f, 5f); val t = ((x + w / 2f - 356f) / 19f).coerceIn(0f, 1f); val k = (if (r.lat > 0f) t else 1f - t) * (0.6f + 0.4f * az.real())
                    val placa = Path().apply { moveTo(x + 0.4f, y + az.entre(0f, 1.6f)); lineTo(x + w - 0.5f, y + az.entre(0f, 1.6f)); lineTo(x + w - az.entre(0.4f, 1.2f), y + alto - az.entre(0.6f, 2f)); lineTo(x + az.entre(0.3f, 1f), y + alto - az.entre(0.6f, 2f)); close() }
                    drawPath(placa, lerp(s.sombra(lerp(corteza, rojiza, 0.4f)), s.luz(lerp(rojiza, corteza, 0.3f)), k))
                    if (k > 0.55f) drawLine(s.luz(lerp(rojiza, C.CAL, 0.3f)).copy(alpha = 0.6f), Offset(x + (if (r.lat > 0f) w - 0.9f else 0.9f), y + 2f), Offset(x + (if (r.lat > 0f) w - 1.1f else 1.1f), y + alto - 3f), 0.5f)
                    x += w + az.entre(0.5f, 1.1f)
                }
                y += alto - az.entre(0f, 1.4f)
            }
        }
    }
}
