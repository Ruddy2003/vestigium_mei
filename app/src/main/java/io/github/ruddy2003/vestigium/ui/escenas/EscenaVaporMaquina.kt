package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaVapor.Fuego
import io.github.ruddy2003.vestigium.ui.escenas.EscenaVapor.Maquina
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.corona
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La máquina de Albert, de costado y mirando a la derecha: «una seis ruedas acopladas, verde con filetes negros, con su
 * número de bronce en la chimenea y su ténder cargado de carbón y de agua». **[Validado]** el tipo (Wikipedia, «0-6-0»,
 * leída el 14 nov 2026): la máquina de ténder de tres ejes acoplados y cilindros interiores fue «extremely common in
 * Britain», sobre todo para mercancías. **[Aproximación]** sus formas y medidas: no copia un modelo ni la librea de
 * ninguna compañía. Sin letras: la placa de bronce es lisa.
 *
 * Se mide desde la trasera de la cabina ([X0]) y desde la cabeza del carril: `q(x, y)` lleva un punto de la máquina al
 * lienzo (x crece hacia el frente; y negativa, hacia arriba).
 */
internal object MaquinaVapor {
    private val C = ColorVapor
    private const val RIEL = EscenaVapor.RIEL

    /** La trasera de la cabina. La máquina va de [X0] − 76 (los topes del ténder) a [X0] + 127 (los de delante). */
    const val X0 = 176f

    /** Cuánto se agranda al pintarla: es lo principal del cuadro. */
    const val K = 1.2f
    const val TRASERA = X0 - 76f * K
    const val FRENTE = X0 + 127f * K

    /** La boca de la chimenea y las válvulas de seguridad: de ahí salen el humo y el vapor. */
    val CHIMENEA = ext(108f, -73f)
    val VALVULAS = ext(30f, -61f)

    private fun q(x: Float, y: Float) = Offset(X0 + x, RIEL + y)

    /** Un punto de la máquina, ya agrandada, en el lienzo. */
    fun ext(x: Float, y: Float) = Offset(X0 + x * K, RIEL + y * K)
    private val RUEDAS = listOf(30f, 58f, 90f)
    private const val R = 12.5f
    private val RUEDAS_TENDER = listOf(-20f, -40f, -60f)
    private const val RT = 9f

    /** La silueta de la máquina y su ténder, para la sombra que arrojan. */
    fun silueta(): Path = poligono(listOf(ext(-76f, 0f), ext(-76f, -24f), ext(-70f, -47f), ext(-6f, -50f), ext(-4f, -58f), ext(24f, -58f), ext(24f, -54f), ext(60f, -54f), ext(62f, -62f), ext(70f, -62f), ext(72f, -53f), ext(103f, -53f), ext(104f, -72f), ext(112f, -72f), ext(113f, -53f), ext(118f, -50f), ext(127f, -24f), ext(127f, 0f)))

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.maquina(m: Momento, p: PincelVapor) = scale(K, K, Offset(X0, RIEL)) { pintar(m, p) }

    private fun DrawScope.pintar(m: Momento, p: PincelVapor) {
        val vieja = EscenaVapor.maquina(m) == Maquina.VIEJA
        val chapa = if (vieja) lerp(C.HOLLIN, C.COBRE, 0.2f) else C.VERDE
        val negro = lerp(C.HOLLIN, C.HIERRO, 0.2f)
        val acero = lerp(C.HIERRO, C.HUESO, 0.35f)
        val nieve = EscenaVapor.nevado(m)
        val lumbre = EscenaVapor.lumbre(m)

        // La sombra de debajo: entre las ruedas no llega la luz.
        drawRect(p.umbra(q(20f, 0f), C.HOLLIN).copy(alpha = 0.75f), q(-74f, -10f), Size(198f, 12f))
        mancha(q(26f, 2f), 108f, 5f, p.umbra(q(20f, 0f), C.HOLLIN), 0.55f)

        tender(m, p, chapa, negro, acero, nieve)

        // ---- Las ruedas acopladas, con sus radios, y detrás los frenos.
        for (x in RUEDAS) drawRect(p.umbra(q(x, -12f), negro), q(x + R - 1f, -22f), Size(3f, 16f))
        val fase = 0.6f
        for (x in RUEDAS) rueda(p, q(x, -R), R, if (vieja) lerp(chapa, C.HOLLIN, 0.3f) else lerp(C.VERDE, C.HOLLIN, 0.25f), negro, acero, 12, fase)
        // Las bielas de acoplamiento, de muñón a muñón.
        val mu = Offset(cos(fase) * 5.2f, sin(fase) * 5.2f)
        drawLine(p.umbra(q(58f, -R) + mu, acero), q(RUEDAS[0], -R) + mu + Offset(0f, 1f), q(RUEDAS[2], -R) + mu + Offset(0f, 1f), 3.4f, StrokeCap.Round)
        drawLine(p.cara(q(58f, -R) + mu, acero), q(RUEDAS[0], -R) + mu, q(RUEDAS[2], -R) + mu, 2.6f, StrokeCap.Round)
        drawLine(p.arriba(q(58f, -R) + mu, lerp(acero, Color.White, 0.3f)), q(RUEDAS[0] + 2f, -R) + mu + Offset(0f, -0.7f), q(RUEDAS[2] - 2f, -R) + mu + Offset(0f, -0.7f), 0.6f)
        for (x in RUEDAS) { drawCircle(p.cara(q(x, -R) + mu, acero), 2.6f, q(x, -R) + mu); drawCircle(p.umbra(q(x, -R) + mu, negro), 1f, q(x, -R) + mu) }

        // ---- El bastidor, el estribo y la pasarela con sus guardabarros.
        drawRect(p.umbra(q(60f, -20f), negro), q(-2f, -23f), Size(120f, 5f))
        drawRect(p.cara(q(60f, -24f), negro), q(-4f, -25f), Size(124f, 2.4f))
        drawRect(p.arriba(q(60f, -25f), lerp(negro, C.HUESO, 0.25f)), q(-4f, -25f), Size(124f, 0.7f))
        for (x in RUEDAS) {
            val arco = Path().apply { moveTo(q(x - 14.5f, -25f).x, q(x, -25f).y); cubicTo(q(x - 13f, -33f).x, q(x, -33f).y, q(x + 13f, -33f).x, q(x, -33f).y, q(x + 14.5f, -25f).x, q(x, -25f).y); close() }
            drawPath(arco, p.cara(q(x, -28f), chapa)); drawPath(arco, p.umbra(q(x, -28f), negro), style = Stroke(0.9f))
            if (!vieja && x == RUEDAS[1]) drawOval(p.cara(q(x, -28f), C.LATON), q(x - 4.2f, -30.4f), Size(8.4f, 3.6f))      // la placa del constructor, lisa
        }
        // Los peldaños de la cabina y el cajón de la arena.
        for (y in listOf(-8f, -16f)) drawRect(p.cara(q(-1f, y), negro), q(-5f, y), Size(8f, 1.4f)); drawLine(p.umbra(q(-1f, -12f), negro), q(-1f, -23f), q(-1f, -7f), 1f)
        drawRect(p.cara(q(46f, -29f), chapa), q(42f, -31f), Size(8f, 6f)); drawRect(p.arriba(q(46f, -31f), lerp(chapa, C.HUESO, 0.2f)), q(42f, -31f), Size(8f, 0.8f))

        // ---- El hogar y la caldera, con sus aros negros (los filetes).
        val caldera = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(q(34f, -53f), q(102f, -27f)), CornerRadius(2f))) }
        fun cilindro(forma: Path, y0: Float, y1: Float, propio: Color, en: Offset) {
            val luz = p.arriba(en, lerp(propio, Color.White, if (vieja) 0.06f else 0.2f)); val medio = p.cara(en, propio); val sombra = p.umbra(en, propio)
            drawPath(forma, Brush.verticalGradient(0f to medio, 0.16f to luz, 0.42f to medio, 0.84f to sombra, 1f to lerp(sombra, medio, 0.45f), startY = RIEL + y0, endY = RIEL + y1))
        }
        val hogar = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(q(20f, -55f), q(36f, -25f)), CornerRadius(2.4f, 2.4f))) }
        cilindro(hogar, -55f, -25f, chapa, q(28f, -40f))
        cilindro(caldera, -53f, -27f, chapa, q(68f, -40f))
        for (x in listOf(36f, 52f, 78f, 99f)) { drawRect(p.umbra(q(x, -40f), C.HOLLIN), q(x - 1.1f, -53f), Size(2.2f, 26f)); if (!vieja) drawRect(p.arriba(q(x, -50f), lerp(C.HOLLIN, C.HUESO, 0.4f)).copy(alpha = 0.7f), q(x - 1.1f, -50.6f), Size(2.2f, 1.6f)) }
        if (vieja) { val oxido = p.cara(q(60f, -36f), lerp(C.COBRE, C.HOLLIN, 0.3f)); for ((x, y, w) in listOf(Triple(44f, -34f, 7f), Triple(70f, -31f, 9f), Triple(88f, -36f, 5f), Triple(26f, -33f, 5f))) mancha(q(x, y), w, 2.6f, oxido, 0.5f) }
        // El pasamanos, a lo largo de la caldera.
        drawLine(p.arriba(q(66f, -46f), acero), q(24f, -46f), q(114f, -46f), 0.8f); for (x in listOf(40f, 64f, 88f, 106f)) drawCircle(p.cara(q(x, -46f), acero), 0.9f, q(x, -46f))
        // El tubo de cobre nuevo, donde reventó el grifo.
        if (EscenaVapor.tuboNuevo(m)) { drawLine(p.arriba(q(27f, -30f), lerp(C.COBRE, Color.White, 0.25f)), q(22f, -30f), q(33f, -30f), 1.3f, StrokeCap.Round); drawLine(p.arriba(q(33f, -28f), lerp(C.COBRE, Color.White, 0.25f)), q(33f, -30f), q(33f, -26f), 1.3f, StrokeCap.Round) }

        // ---- La caja de humos, negra, con su puerta, y la chimenea.
        val humos = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(q(100f, -54f), q(117f, -25f)), CornerRadius(2f))) }
        cilindro(humos, -54f, -25f, negro, q(108f, -40f))
        drawPath(Path().apply { moveTo(q(117f, -53f).x, q(0f, -53f).y); cubicTo(q(121.5f, -48f).x, q(0f, -48f).y, q(121.5f, -32f).x, q(0f, -32f).y, q(117f, -26f).x, q(0f, -26f).y); close() }, p.cara(q(119f, -40f), negro, 1f))
        drawLine(p.arriba(q(120f, -40f), acero), q(120.2f, -42f), q(120.2f, -37f), 0.9f, StrokeCap.Round)
        val boca = if (vieja) -78f else -73f
        val tubo = poligono(listOf(q(103.2f, -53f), q(104.6f, boca + 3f), q(104f, boca), q(112f, boca), q(111.4f, boca + 3f), q(112.8f, -53f)))
        drawPath(tubo, Brush.horizontalGradient(0f to p.umbra(q(104f, -62f), negro), 0.3f to p.cara(q(107f, -62f), negro), 0.62f to p.arriba(q(109f, -62f), lerp(negro, C.HUESO, 0.18f)), 1f to p.cara(q(112f, -62f), negro), startX = q(103f, 0f).x, endX = q(113f, 0f).x))
        drawRect(p.cara(q(108f, -54f), negro), q(102f, -55f), Size(12f, 2.4f))
        if (!vieja) {
            drawRoundRect(Brush.horizontalGradient(0f to p.umbra(q(104f, boca), C.COBRE), 0.6f to p.arriba(q(109f, boca), lerp(C.COBRE, Color.White, 0.3f)), 1f to p.cara(q(112f, boca), C.COBRE), startX = q(103f, 0f).x, endX = q(113f, 0f).x), q(103.2f, boca - 0.6f), Size(9.6f, 4f), CornerRadius(1.2f))
            drawRect(p.cara(q(108f, -64f), C.LATON), q(106.2f, -66f), Size(3.6f, 2.6f))                                  // «su número de bronce en la chimenea»: una placa lisa
        }
        drawOval(p.umbra(q(108f, boca), C.HOLLIN), q(104.6f, boca - 1f), Size(6.8f, 2f))

        // ---- El domo y las válvulas de seguridad.
        val domo = Path().apply { moveTo(q(60.4f, -53f).x, q(0f, -53f).y); lineTo(q(60.8f, -57f).x, q(0f, -57f).y); cubicTo(q(61f, -64.6f).x, q(0f, -64.6f).y, q(71f, -64.6f).x, q(0f, -64.6f).y, q(71.2f, -57f).x, q(0f, -57f).y); lineTo(q(71.6f, -53f).x, q(0f, -53f).y); close() }
        val metal = if (vieja) chapa else C.LATON
        drawPath(domo, Brush.horizontalGradient(0f to p.umbra(q(61f, -58f), metal), 0.34f to p.cara(q(64f, -58f), metal), 0.64f to p.arriba(q(67f, -58f), lerp(metal, Color.White, if (vieja) 0.1f else 0.5f)), 1f to p.cara(q(71f, -58f), metal), startX = q(60f, 0f).x, endX = q(72f, 0f).x))
        for (x in listOf(27f, 31.4f)) { drawRect(p.cara(q(x, -58f), metal), q(x - 1.2f, -61f), Size(2.4f, 6.4f)); drawRect(p.arriba(q(x, -61f), lerp(metal, Color.White, 0.4f)), q(x - 1.5f, -61.6f), Size(3f, 1f)) }
        drawLine(p.cara(q(29f, -62f), acero), q(26f, -62.4f), q(33f, -62.4f), 0.8f)

        // ---- La cabina.
        if (vieja) {
            // Las máquinas viejas van casi al descubierto: un parabrisas de chapa con su ojo de cristal y unos costados bajos.
            drawRect(p.cara(q(9f, -32f), chapa), q(-3f, -39f), Size(25f, 16f)); drawRect(p.arriba(q(9f, -39f), lerp(chapa, C.HUESO, 0.2f)), q(-3f, -39.6f), Size(25f, 1f))
            drawPath(poligono(listOf(q(19f, -25f), q(19f, -57f), q(15f, -61f), q(23f, -61f), q(23f, -25f))), p.cara(q(21f, -45f), chapa, 1f))
            drawCircle(p.umbra(q(21f, -52f), C.HOLLIN), 2.4f, q(21f, -52f)); drawCircle(p.cara(q(21f, -52f), C.LATON), 2.4f, q(21f, -52f), style = Stroke(0.6f))
            if (lumbre > 0f) resplandor(q(12f, -40f), 12f, 0.5f * lumbre * (0.3f + 0.7f * p.l.noche), C.FUEGO)
        } else {
            val costado = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(q(-3f, -52f), q(22f, -23f)), CornerRadius(1f))) }
            drawPath(costado, Brush.verticalGradient(0f to p.arriba(q(9f, -50f), lerp(chapa, Color.White, 0.12f)), 0.3f to p.cara(q(9f, -40f), chapa), 1f to p.umbra(q(9f, -26f), chapa), startY = RIEL - 52f, endY = RIEL - 23f))
            // El hueco de la cabina: dentro, la puerta del hogar y sus bronces.
            val hueco = Path().apply { moveTo(q(4f, -32f).x, q(0f, -32f).y); lineTo(q(4f, -42f).x, q(0f, -42f).y); cubicTo(q(4f, -48.6f).x, q(0f, -48.6f).y, q(18.6f, -48.6f).x, q(0f, -48.6f).y, q(18.6f, -42f).x, q(0f, -42f).y); lineTo(q(18.6f, -32f).x, q(0f, -32f).y); close() }
            drawPath(hueco, p.umbra(q(11f, -40f), lerp(C.HOLLIN, chapa, 0.2f)))
            clipPath(hueco) {
                if (lumbre > 0f) {
                    val k = lumbre * (0.45f + 0.55f * p.l.noche)
                    drawOval(lerp(C.FUEGO, Color(0xFFFFE0A0), 0.4f).copy(alpha = (0.5f + 0.5f * k).coerceIn(0f, 1f)), q(12f, -38.6f), Size(5.6f, 6f))
                    resplandor(q(14.8f, -36f), 13f, 0.9f * k, C.FUEGO)
                }
                for ((x, y) in listOf(8f to -45f, 12.4f to -46f, 16f to -44.6f)) drawCircle(p.cara(q(x, y), C.LATON), 0.9f, q(x, y))
                drawLine(p.cara(q(6f, -40f), C.COBRE), q(6f, -47f), q(6f, -33f), 0.6f)
                // Maquinista: su gorra cuelga del otro lado, junto al regulador.
                if (EscenaVapor.maquinista(m)) { drawOval(p.cara(q(8.6f, -40f), C.AZUL_PANA), q(6f, -41.6f), Size(5.6f, 3.2f)); drawRect(p.umbra(q(8.6f, -38f), C.HOLLIN), q(5.2f, -39f), Size(4f, 0.9f)) }
            }
            drawPath(hueco, p.arriba(q(11f, -40f), lerp(C.HOLLIN, C.HUESO, 0.1f)), style = Stroke(0.9f))
            // Los filetes negros del costado y el techo.
            drawRect(Color.Transparent, q(-3f, -52f), Size(25f, 29f)); drawPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(q(-1f, -30f), q(20f, -25.4f)), CornerRadius(0.8f))) }, p.umbra(q(9f, -28f), C.HOLLIN), style = Stroke(0.7f))
            val techo = Path().apply { moveTo(q(-6f, -53f).x, q(0f, -53f).y); cubicTo(q(2f, -59f).x, q(0f, -59f).y, q(18f, -59f).x, q(0f, -59f).y, q(26f, -54f).x, q(0f, -54f).y); lineTo(q(26f, -52f).x, q(0f, -52f).y); lineTo(q(-6f, -51f).x, q(0f, -51f).y); close() }
            drawPath(techo, p.arriba(q(10f, -56f), if (nieve) lerp(negro, C.NIEVE, 0.85f) else lerp(negro, C.HUESO, 0.12f)))
            drawLine(p.umbra(q(10f, -52f), C.HOLLIN), q(-6f, -51.4f), q(26f, -52.4f), 0.8f)
            drawLine(p.arriba(q(-4.4f, -40f), acero), q(-4.4f, -50f), q(-4.4f, -26f), 0.8f)
        }

        // ---- La viga de los topes, roja, con su gancho y el farol.
        drawRect(p.cara(q(120f, -20f), C.ROJO, 1f), q(118f, -27f), Size(3.4f, 11f)); drawRect(p.arriba(q(120f, -27f), lerp(C.ROJO, C.HUESO, 0.3f)), q(118f, -27f), Size(3.4f, 0.8f))
        drawRect(p.cara(q(124f, -21f), acero), q(121.4f, -22.6f), Size(4f, 2.6f)); drawOval(p.cara(q(127f, -21f), acero, 1f), q(125.2f, -25f), Size(2.4f, 7.4f))
        drawLine(p.umbra(q(122f, -17f), negro), q(121.4f, -17.6f), q(124f, -15f), 1f, StrokeCap.Round)
        val farol = q(115.6f, -29.4f)
        drawRect(p.cara(farol, negro), farol + Offset(-2.2f, -3.2f), Size(4.4f, 5f)); drawRect(p.cara(farol, negro), farol + Offset(-1f, -4.6f), Size(2f, 1.6f))
        val alumbra = EscenaVapor.turno(m) && p.l.noche > 0.3f
        drawCircle(if (alumbra) Color(0xFFFFF0C0) else p.cara(farol, lerp(C.HUESO, C.HIERRO, 0.4f), 1f), 1.5f, farol + Offset(1.4f, -0.8f))
        if (alumbra) resplandor(farol + Offset(2f, -0.8f), 12f, 0.7f * p.l.noche, C.GAS)

        adornos(m, p)
    }

    /** Una rueda con su llanta de acero, su cubo y sus radios. */
    private fun DrawScope.rueda(p: PincelVapor, c: Offset, r: Float, propio: Color, negro: Color, acero: Color, radios: Int, fase: Float) {
        drawCircle(p.umbra(c, negro), r, c)
        drawCircle(p.cara(c, acero), r, c, style = Stroke(1.5f))
        drawCircle(p.cara(c, propio), r - 1.6f, c, style = Stroke(1.6f))
        for (i in 0 until radios) { val a = fase + i * (2f * PI.toFloat() / radios); drawLine(p.cara(c, propio), c + Offset(cos(a), sin(a)) * 2.4f, c + Offset(cos(a), sin(a)) * (r - 2.2f), 1f) }
        drawCircle(p.cara(c, propio), 3f, c); drawCircle(p.arriba(c, lerp(acero, Color.White, 0.2f)), 1.2f, c)
        // El filo de luz de la llanta, arriba.
        drawArc(p.arriba(c, lerp(acero, Color.White, 0.5f)), 200f, 100f, false, c - Offset(r, r), Size(2f * r, 2f * r), style = Stroke(0.7f))
    }

    /** El ténder: el depósito del agua, con su borde vuelto, y encima el carbón. */
    private fun DrawScope.tender(m: Momento, p: PincelVapor, chapa: Color, negro: Color, acero: Color, nieve: Boolean) {
        val vieja = EscenaVapor.maquina(m) == Maquina.VIEJA
        for (x in RUEDAS_TENDER) rueda(p, q(x, -RT), RT, lerp(chapa, C.HOLLIN, 0.3f), negro, acero, 10, 0.2f + x * 0.03f)
        // El bastidor, con las cajas de grasa y las ballestas.
        drawRect(p.umbra(q(-40f, -17f), negro), q(-72f, -23f), Size(68f, 9f))
        for (x in RUEDAS_TENDER) { drawRect(p.cara(q(x, -10f), negro), q(x - 2.6f, -13f), Size(5.2f, 6f)); drawPath(Path().apply { moveTo(q(x - 7f, -16f).x, q(0f, -16f).y); cubicTo(q(x - 3f, -13f).x, q(0f, -13f).y, q(x + 3f, -13f).x, q(0f, -13f).y, q(x + 7f, -16f).x, q(0f, -16f).y) }, p.cara(q(x, -15f), acero), style = Stroke(1.3f)) }
        // El carbón, que asoma por encima del borde.
        val carbon = Path().apply { moveTo(q(-66f, -46f).x, q(0f, -46f).y); cubicTo(q(-56f, -55f).x, q(0f, -55f).y, q(-44f, -51f).x, q(0f, -51f).y, q(-34f, -57f).x, q(0f, -57f).y); cubicTo(q(-24f, -60f).x, q(0f, -60f).y, q(-14f, -54f).x, q(0f, -54f).y, q(-9f, -46f).x, q(0f, -46f).y); close() }
        drawPath(carbon, p.umbra(q(-36f, -50f), C.HOLLIN))
        val az = io.github.ruddy2003.vestigium.vidas.Azar(io.github.ruddy2003.vestigium.vidas.Semilla.de("vapor", "carbon"))
        clipPath(carbon) {
            repeat(46) { val x = -64f + az.entero(54); val y = -58f + az.entero(12); val s = 1.6f + az.entero(3)
                drawPath(poligono(q(x, y), q(x + s, y - s * 0.7f), q(x + s * 1.8f, y + s * 0.3f)), p.arriba(q(x, y), lerp(C.HOLLIN, C.HIERRO, 0.5f + 0.1f * az.entero(4))).copy(alpha = 0.85f)) }
            if (nieve) drawRect(p.arriba(q(-36f, -56f), C.NIEVE).copy(alpha = 0.7f), q(-68f, -62f), Size(62f, 9f))
        }
        // El depósito y su borde vuelto.
        val caja = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(q(-72f, -45f), q(-5f, -22f)), CornerRadius(1.4f))) }
        drawPath(caja, Brush.verticalGradient(0f to p.arriba(q(-38f, -44f), lerp(chapa, Color.White, if (vieja) 0.04f else 0.14f)), 0.25f to p.cara(q(-38f, -36f), chapa), 1f to p.umbra(q(-38f, -24f), chapa), startY = RIEL - 45f, endY = RIEL - 22f))
        drawPath(poligono(listOf(q(-73.6f, -48.6f), q(-4f, -48.6f), q(-5f, -45f), q(-72f, -45f))), p.cara(q(-38f, -47f), lerp(chapa, C.HOLLIN, 0.25f)))
        drawLine(p.arriba(q(-38f, -48.6f), lerp(chapa, C.HUESO, 0.45f)), q(-73.6f, -48.6f), q(-4f, -48.6f), 0.8f)
        if (!vieja) for (rect in listOf(Rect(q(-68f, -42f), q(-40f, -26f)), Rect(q(-37f, -42f), q(-9f, -26f)))) drawRoundRect(p.umbra(rect.center, C.HOLLIN), rect.topLeft, rect.size, CornerRadius(2f), style = Stroke(0.7f))
        else for ((x, y, w) in listOf(Triple(-56f, -30f, 8f), Triple(-28f, -27f, 10f), Triple(-16f, -38f, 5f))) mancha(q(x, y), w, 3f, p.cara(q(x, y), lerp(C.COBRE, C.HOLLIN, 0.3f)), 0.5f)
        // Los remaches del borde de abajo, el pasamanos y los topes de atrás.
        for (i in 0..16) drawCircle(p.umbra(q(-70f + i * 4f, -23.6f), C.HOLLIN).copy(alpha = 0.6f), 0.4f, q(-70f + i * 4f, -23.6f))
        drawLine(p.arriba(q(-6f, -40f), acero), q(-6.4f, -50f), q(-6.4f, -24f), 0.8f)
        drawRect(p.cara(q(-74f, -20f), C.ROJO, -1f), q(-75f, -26f), Size(3f, 10f)); drawOval(p.cara(q(-77f, -21f), acero, -1f), q(-78.4f, -24.6f), Size(2.4f, 7.4f))
        // El cajón de las herramientas y la pala, apoyada.
        drawRect(p.cara(q(-14f, -50f), lerp(C.MADERA, C.HOLLIN, 0.3f)), q(-20f, -52.4f), Size(12f, 4f))
    }

    /** Lo que la máquina lleva puesto los días señalados, y lo de la limpieza. */
    private fun DrawScope.adornos(m: Momento, p: PincelVapor) {
        val l = p.l
        if (EscenaVapor.limpieza(m)) {
            // La escalera apoyada en la caldera y el trapo de estopa sobre el pasamanos.
            val a = q(78f, 0f); val b = q(70f, -50f)
            for (d in listOf(0f, 6f)) drawLine(p.cara(a, C.MADERA), a + Offset(d, 0f), b + Offset(d, 0f), 1.1f)
            for (i in 1..7) { val t = i / 8f; drawLine(p.cara(a, C.MADERA), a + (b - a) * t, a + (b - a) * t + Offset(6f, 0f), 0.8f) }
            drawPath(poligono(q(90f, -46.4f), q(96f, -46.4f), q(95f, -40f), q(91.4f, -41.4f)), p.cara(q(93f, -44f), C.HUESO))
        }
        if (EscenaVapor.excursion(m) || EscenaVapor.fiesta(m)) {
            // Cintas en el pasamanos y, en las excursiones, dos banderas en los hierros del farol.
            for ((x, c) in listOf(30f to C.HUESO, 54f to C.ROJO, 78f to C.HUESO, 100f to C.ROJO)) { drawLine(p.cara(q(x, -44f), c), q(x, -46f), q(x - 1.6f, -40f), 1f, StrokeCap.Round); drawLine(p.cara(q(x, -44f), c), q(x, -46f), q(x + 1.8f, -40.6f), 1f, StrokeCap.Round) }
            if (EscenaVapor.excursion(m)) for ((x, c) in listOf(113f to C.ROJO, 118.6f to C.AZUL_PANA)) {
                drawLine(p.cara(q(x, -40f), C.MADERA), q(x, -30f), q(x, -47f), 0.6f)
                drawPath(poligono(q(x, -47f), q(x + 8f, -45.6f), q(x, -42.4f)), p.cara(q(x + 3f, -45f), c)); drawLine(p.cara(q(x + 3f, -45f), C.HUESO), q(x + 0.6f, -45.4f), q(x + 5f, -45f), 0.5f)
            }
        }
        if (EscenaVapor.navidad(m)) corona(l, q(119.4f, -40f), 4.4f, cinta = C.ROJO)
        if (EscenaVapor.luto(m)) {
            // Un crespón negro en el farol.
            val f = q(115.6f, -33f)
            drawPath(poligono(f, f + Offset(-3.4f, -2.4f), f + Offset(-3f, 1.6f)), p.umbra(f, C.HOLLIN)); drawPath(poligono(f, f + Offset(3.4f, -2.4f), f + Offset(3f, 1.6f)), p.umbra(f, C.HOLLIN))
            drawLine(p.umbra(f, C.HOLLIN), f, f + Offset(-1.6f, 6f), 0.9f); drawLine(p.umbra(f, C.HOLLIN), f, f + Offset(1.4f, 6.6f), 0.9f)
        }
    }

    // ---------------------------------------------------------------- La parte viva

    /**
     * El humo gris de la chimenea, a borbotones; el vapor blanco de las válvulas de seguridad y el de los purgadores, a
     * ras de suelo, que se deshacen enseguida; y el resplandor del hogar, que de noche tiñe el vapor que tiene cerca.
     */
    fun DrawScope.maquinaViva(m: Momento, l: Luces, r: Rayo, reloj: Float) {
        val viento = EscenaVapor.soplo(m)
        val boca = if (EscenaVapor.maquina(m) == Maquina.VIEJA) CHIMENEA + Offset(0f, -5f) else CHIMENEA
        val lumbre = EscenaVapor.lumbre(m); val roja = (0.55f * lumbre * l.noche).coerceIn(0f, 0.6f)
        with(HumoVapor) {
            when (EscenaVapor.fuego(m)) {
                Fuego.DORMIDO -> {
                    // Con el fuego dormido, un hilo de humo; y el vapor que se le escapa por debajo de la cabina, que el hogar tiñe.
                    bocanadas(l, r, boca, 62f, 22f, reloj, viento, 0.5f, 1, 6, fuerza = 0.75f, ritmo = 0.04f)
                    vapor(l, r, ext(6f, -8f), Offset(-0.8f, -0.6f), 30f, 20f, reloj * 0.6f, viento, 5, n = 5, fuerza = 0.7f, tinte = C.FUEGO, cuanto = roja)
                    vapor(l, r, VALVULAS, Offset(0.08f, -0.997f), 20f, 10f, reloj * 0.7f, viento, 4, n = 4, fuerza = 0.5f, tinte = C.FUEGO, cuanto = roja)
                }
                Fuego.ENCENDIENDO -> bocanadas(l, r, boca, 114f, 56f, reloj, viento, 0.9f, 1, 12, ritmo = 0.06f)
                Fuego.A_PRESION -> {
                    bocanadas(l, r, boca, 112f, 46f, reloj, viento, 0.52f, 1, 11, ritmo = 0.07f)
                    vapor(l, r, VALVULAS, Offset(0.08f, -0.997f), 46f, 26f, reloj, viento, 4, n = 7, tinte = C.FUEGO, cuanto = roja)
                    vapor(l, r, ext(116f, -6f), Offset(0.99f, -0.12f), 46f, 27f, reloj, viento, 7, n = 7)
                    vapor(l, r, ext(96f, -7f), Offset(-0.93f, -0.36f), 34f, 22f, reloj * 1.2f, viento, 8, n = 5, fuerza = 0.9f)
                }
            }
            if (EscenaVapor.grifoRoto(m)) vapor(l, r, ext(22f, -30f), Offset(-0.9f, -0.44f), 52f, 30f, reloj * 1.4f, viento, 9, n = 8, tinte = C.FUEGO, cuanto = roja)
        }
        if (lumbre > 0f && l.noche > 0.3f) { val late = 0.86f + 0.14f * sin(reloj * 5.3f); resplandor(EscenaVapor.HOGAR, 46f, 0.75f * lumbre * l.noche * late, C.FUEGO); resplandor(EscenaVapor.HOGAR + Offset(-8f, 46f), 40f, 0.4f * lumbre * l.noche * late, C.FUEGO) }
    }
}
