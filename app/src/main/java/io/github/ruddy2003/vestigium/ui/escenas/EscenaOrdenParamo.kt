package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima

/**
 * El páramo: lo que se ve por encima del parapeto, de lejos a cerca. **[Diseño propio]** a partir del cuento: «mesetas,
 * torres caídas y nada más»; «el horizonte se ve entero, recto, como trazado con regla»; el camino de las patrullas, el
 * pozo del pie de la meseta, una gasolinera vieja, una casa hundida en la arena.
 *
 * - **Las mesetas**, en tres filas que se enciman, cada una con su cara de luz y su cara de sombra, su talud y la calima
 *   entre ellas; en dos, los muñones de torres caídas de antes del Fuego, inclinados.
 * - **El llano**, ocre, con las rachas del viento, las sombras de las nubes «como rebaños», el camino que sale de la
 *   ensenada y se pierde, la gasolinera y la casa hundida.
 * - **La otra pared de la meseta**, al otro lado de una ensenada que se mete en la meseta (a la derecha, cortada por el
 *   borde): la roca roja a pico, con sus estratos, sus columnas y sus grietas, el talud de la base y el pozo con su brocal.
 *   Es la «pared que cae a pico»: desde el adarve no se ve la de debajo, así que se ve la de enfrente **[Aproximación]**.
 */
internal object ParamoOrden {
    private val C = ColorOrden
    private val H = CamOrden.H

    // ---------------------------------------------------------------- El cielo

    fun DrawScope.cielo(v: VistaOrden) {
        cielo(v.l, H, v.m.semilla("cielo"))
        astros(v.l, H, v.m.semilla("astros"))
        if (v.m.ficha.clima != Clima.POLVO) nubes(v.l, H, v.m.ficha.clima, v.m.semilla("nubes"))
        if (v.m.ficha.clima == Clima.POLVO) paredDePolvo(v)
    }

    /**
     * El polvo en el cielo: bancos ocres que tapan el sol, más espesos abajo. El día de la tormenta de polvo, «una pared
     * de polvo más alta que la meseta, ocre por abajo y negra por arriba».
     */
    private fun DrawScope.paredDePolvo(v: VistaOrden) {
        val az = v.m.azar("polvo")
        val ocre = lerp(v.l.tono(C.ARENA), v.l.bruma, 0.4f); val negro = v.l.tono(lerp(C.ROCA, C.HOLLIN, 0.6f))
        val fuerte = EscenaOrden.hoyEs(v.m, "tormenta_polvo") || EscenaOrden.sequia(v.m)
        repeat(if (fuerte) 16 else 9) {
            val y = az.entre(H * 0.35f, H + 6f); val w = az.entre(70f, 160f)
            mancha(Offset(az.entre(-20f, ANCHO + 20f), y), w, az.entre(10f, 22f), if (fuerte && y < H * 0.6f) lerp(ocre, negro, 0.5f) else ocre, if (fuerte) 0.5f else 0.32f)
        }
    }

    // ---------------------------------------------------------------- Lo lejano

    /** Las tres filas de mesetas: dónde empieza y acaba cada una, la altura de su mesa sobre el horizonte y lo lejos que está. */
    private class Meseta(val x0: Float, val x1: Float, val alto: Float, val lejos: Float, val torre: Float = -1f)

    private val MESETAS = listOf(
        Meseta(150f, 214f, 5.5f, 0.92f), Meseta(236f, 300f, 7f, 0.9f, torre = 262f), Meseta(318f, 390f, 4.5f, 0.92f),
        Meseta(170f, 232f, 9f, 0.78f, torre = 214f), Meseta(286f, 344f, 11f, 0.76f),
        Meseta(194f, 252f, 6f, 0.6f),
    )

    /**
     * Lo lejano, de atrás adelante: las mesetas con sus torres caídas, el llano con el camino y lo que hay en él, la
     * ensenada y la otra pared de la meseta. Va todo antes del patio y del adarve, que lo tapan.
     */
    fun DrawScope.lejos(v: VistaOrden) {
        val l = v.l; val az = PiezasOrden.fijo("mesetas")
        val polvo = v.polvo
        // El llano: del horizonte hacia acá, más cálido y más oscuro cerca.
        val llano = Path().apply { moveTo(0f, H); lineTo(ANCHO, H); lineTo(ANCHO, 285f); lineTo(0f, 285f); close() }
        drawPath(llano, Brush.verticalGradient(0f to l.tono(C.PARAMO, 0.85f), 0.25f to l.tono(C.PARAMO, 0.55f), 1f to l.tono(lerp(C.PARAMO, C.ROCA, 0.25f), 0.2f), startY = H, endY = 240f))
        // Las mesetas, de la fila de atrás a la de delante.
        for (me in MESETAS) meseta(v, me, az)
        rachas(v, az)
        caminoYRuinas(v)
        if (polvo > 0.01f) veloDePolvo(v, H - 10f, H + 30f, 0.55f * polvo)
        pared(v)
    }

    /** Una meseta lejana: la mesa casi plana, la cara a pico con su lado de luz y de sombra, y el talud que baja al llano. */
    private fun DrawScope.meseta(v: VistaOrden, me: Meseta, az: Azar) {
        val l = v.l; val y0 = H - me.alto; val pie = H + 2.5f * (1f - me.lejos) + 0.6f
        val w = me.x1 - me.x0
        val cima = listOf(Offset(me.x0 + w * 0.06f, y0 + 1.2f), Offset(me.x0 + w * 0.18f, y0 + az.entre(-0.4f, 0.4f)), Offset(me.x0 + w * 0.5f, y0 + az.entre(-0.6f, 0.3f)),
            Offset(me.x1 - w * 0.2f, y0 + az.entre(-0.3f, 0.4f)), Offset(me.x1 - w * 0.08f, y0 + 1f))
        val forma = Path().apply {
            moveTo(me.x0 - w * 0.08f, pie); lineTo(me.x0, y0 + me.alto * 0.45f); lineTo(cima[0].x, cima[0].y)
            for (q in cima.drop(1)) lineTo(q.x, q.y)
            lineTo(me.x1, y0 + me.alto * 0.5f); lineTo(me.x1 + w * 0.1f, pie); close()
        }
        val luz = v.lz.color(C.MESETA, V3(1f, 0.2f, -0.6f), me.lejos); val sombra = v.lz.color(C.MESETA, V3(-1f, 0.2f, -0.6f), me.lejos)
        val derecha = v.p.solALaDerecha
        drawPath(forma, Brush.horizontalGradient(0f to (if (derecha) sombra else luz), 0.55f to lerp(luz, sombra, 0.5f), 1f to (if (derecha) luz else sombra), startX = me.x0, endX = me.x1))
        clipPath(forma) {
            // La mesa, más clara; los estratos, en rayas tendidas.
            drawRect(l.tono(lerp(C.MESETA, C.ARENA, 0.4f), me.lejos), Offset(me.x0 - 4f, y0 - 2f), Size(w + 8f, 1.4f))
            for (i in 1..3) drawLine(sombra.copy(alpha = 0.35f), Offset(me.x0 - 4f, y0 + me.alto * i / 4.2f), Offset(me.x1 + 4f, y0 + me.alto * i / 4.2f + az.entre(-0.4f, 0.4f)), 0.5f)
            // El talud de la base, con el color del llano.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.PARAMO, me.lejos)), y0 + me.alto * 0.6f, pie), Offset(me.x0 - w * 0.1f, y0 + me.alto * 0.6f), Size(w * 1.2f, pie - y0 - me.alto * 0.6f + 1f))
        }
        if (me.torre > 0f) torreCaida(v, me.torre, y0, me.lejos, az)
    }

    /** Un muñón de torre de antes del Fuego sobre una meseta: un prisma flaco, roto arriba y un poco inclinado, con sus huecos. */
    private fun DrawScope.torreCaida(v: VistaOrden, x: Float, y0: Float, lejos: Float, az: Azar) {
        val alto = az.entre(8f, 13f); val ancho = az.entre(3.2f, 4.6f); val ladeo = az.entre(-2.4f, 2.4f)
        val forma = poligono(listOf(Offset(x, y0 + 0.5f), Offset(x + ancho, y0 + 0.5f), Offset(x + ancho + ladeo, y0 - alto * 0.8f), Offset(x + ancho * 0.6f + ladeo, y0 - alto * 0.62f), Offset(x + ladeo + 0.4f, y0 - alto)))
        drawPath(forma, v.lz.color(lerp(C.HIERRO, C.MESETA, 0.5f), V3(if (v.p.solALaDerecha) 1f else -1f, 0f, -0.5f), lejos))
        clipPath(forma) { for (i in 1..4) drawLine(v.l.tono(C.HOLLIN, lejos).copy(alpha = 0.4f), Offset(x - 2f, y0 - alto * i / 5f), Offset(x + ancho + 3f, y0 - alto * i / 5f + ladeo * 0.1f), 0.5f) }
    }

    /** Las rachas del viento en el llano: lenguas de arena más clara, tendidas, cada vez más finas hacia el horizonte. */
    private fun DrawScope.rachas(v: VistaOrden, az: Azar) {
        val claro = v.l.tono(C.ARENA, 0.4f); val oscuro = v.l.tono(lerp(C.PARAMO, C.ROCA, 0.4f), 0.4f)
        repeat(26) {
            val y = H + 2f + 70f * az.real() * az.real(); val k = (y - H) / 70f
            val w = 10f + 70f * k * az.real(); val x = az.entre(130f, ANCHO)
            mancha(Offset(x, y), w, 0.6f + 2.2f * k, if (it % 3 == 0) oscuro else claro, 0.32f)
        }
    }

    /** Dónde sale el camino de la ensenada y cómo se pierde en el llano (puntos del lienzo, de cerca a lejos). */
    private val CAMINO = listOf(Offset(296f, 196f), Offset(270f, 176f), Offset(252f, 158f), Offset(238f, 146f), Offset(210f, 138f), Offset(196f, 131f), Offset(205f, 126f), Offset(186f, 122f), Offset(172f, 120f))

    /** El camino de las patrullas, la gasolinera vieja y la casa hundida. */
    private fun DrawScope.caminoYRuinas(v: VistaOrden) {
        val l = v.l
        drawPath(curva(CAMINO), l.tono(lerp(C.ARENA, Color.White, 0.15f), 0.45f).copy(alpha = 0.75f), style = Stroke(1.1f, cap = StrokeCap.Round))
        drawPath(curva(CAMINO), l.tono(lerp(C.PARAMO, C.ROCA, 0.35f), 0.45f).copy(alpha = 0.4f), style = Stroke(0.35f, cap = StrokeCap.Round))
        // La gasolinera vieja: la marquesina sobre dos postes y la caseta, junto al camino.
        val g = Offset(224f, 141f)
        drawRect(l.tono(lerp(C.PAPEL, C.PARAMO, 0.5f), 0.5f), Offset(g.x - 5.5f, g.y - 5.2f), Size(11f, 1.3f))
        drawLine(l.tono(C.HIERRO, 0.5f), Offset(g.x - 4f, g.y - 4f), Offset(g.x - 4.2f, g.y), 0.6f); drawLine(l.tono(C.HIERRO, 0.5f), Offset(g.x + 3.6f, g.y - 4f), Offset(g.x + 3.8f, g.y), 0.6f)
        drawPath(poligono(Offset(g.x + 6f, g.y), Offset(g.x + 12f, g.y), Offset(g.x + 12f, g.y - 3.8f), Offset(g.x + 6f, g.y - 3.2f)), v.lz.color(C.PIEDRA, V3(-1f, 0f, -0.4f), 0.5f))
        sombraCorta(v.r, g + Offset(1f, 0.4f), 11f, 4f)
        // La casa hundida en la arena: un tejado roto a ras del suelo.
        val h = Offset(262f, 166f)
        drawPath(poligono(Offset(h.x - 7f, h.y), Offset(h.x + 6f, h.y + 0.5f), Offset(h.x + 3f, h.y - 3.5f), Offset(h.x - 4f, h.y - 3f)), v.lz.color(lerp(C.HIERRO, C.PARAMO, 0.4f), V3(0f, 1f, -0.3f), 0.35f))
        drawLine(v.l.tono(C.HOLLIN, 0.35f), Offset(h.x - 2f, h.y - 1.6f), Offset(h.x + 1f, h.y - 1.4f), 1.1f)
        mancha(Offset(h.x + 2f, h.y + 1.2f), 12f, 2f, v.l.tono(C.ARENA, 0.35f), 0.6f)
    }

    // ---------------------------------------------------------------- La otra pared de la meseta

    /** El borde de arriba de la pared de enfrente (de izquierda a derecha) y su pie, en el lienzo. */
    internal val BORDE_PARED = listOf(Offset(286f, 133f), Offset(294f, 130.5f), Offset(305f, 131.5f), Offset(318f, 128f), Offset(336f, 129f), Offset(350f, 125.5f), Offset(368f, 126.5f), Offset(386f, 124f))
    internal val PIE_PARED = listOf(Offset(276f, 200f), Offset(300f, 196f), Offset(330f, 199f), Offset(360f, 195f), Offset(386f, 197f))

    /** La silueta de la pared: del borde al pie. */
    fun formaDeLaPared(): Path = poligono(BORDE_PARED + PIE_PARED.reversed() + listOf(Offset(272f, 160f)))

    /**
     * La otra pared de la meseta, a pico: la cara de roca roja, que mira hacia el adarve y un poco a la izquierda, se
     * enciende por la tarde y queda en sombra por la mañana; los estratos tendidos, las columnas, las grietas, la mesa de
     * arriba más clara y el talud de abajo, con el pozo y su brocal al pie.
     */
    private fun DrawScope.pared(v: VistaOrden) {
        val l = v.l; val forma = formaDeLaPared(); val az = PiezasOrden.fijo("pared")
        val n = V3(-0.55f, 0f, -0.83f); val lejos = 0.32f
        val roca = v.lz.color(C.ROCA, n, lejos); val honda = v.lz.color(C.ROCA_HONDA, n, lejos)
        drawPath(forma, Brush.verticalGradient(0f to lerp(roca, v.l.tono(C.PIEDRA, lejos), 0.25f), 0.55f to roca, 1f to honda, startY = 124f, endY = 200f))
        clipPath(forma) {
            // La roca se parte en columnas de anchos distintos, cada una con su cara de luz (del lado del sol) y la de
            // sombra, y su remate en escalones; los estratos las cortan de través.
            val claro = lerp(roca, v.l.tono(lerp(C.PIEDRA, C.ARENA, 0.3f), lejos), 0.45f)
            var x = 268f
            while (x < 392f) {
                val w = az.entre(5f, 14f); val y0 = 126f + az.entre(0f, 18f) * az.real()
                val luzIzq = !v.p.solALaDerecha
                drawRect(honda.copy(alpha = 0.55f), Offset(if (luzIzq) x + w * 0.62f else x, y0), Size(w * 0.38f, 90f))
                drawRect(claro.copy(alpha = 0.5f), Offset(if (luzIzq) x else x + w * 0.7f, y0 + 2f), Size(w * 0.3f, 90f))
                if (az.real() < 0.5f) drawLine(honda.copy(alpha = 0.7f), Offset(x, y0 + az.entre(6f, 30f)), Offset(x + w, y0 + az.entre(6f, 30f)), 0.8f)
                x += w
            }
            // Los estratos: bandas tendidas, onduladas, de grueso distinto, más juntas arriba.
            for (i in 0 until 7) {
                val y = 132f + i * i * 1.2f + az.entre(-1.5f, 1.5f)
                val banda = (0..8).map { k -> Offset(266f + k * 16f, y + az.entre(-1.2f, 1.2f) - k * 0.35f) }
                drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(banda), honda.copy(alpha = 0.5f), style = Stroke(0.6f + 0.25f * i))
                drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(banda.map { it + Offset(0f, -1.2f) }), claro.copy(alpha = 0.35f), style = Stroke(0.5f))
            }
            // Las grietas.
            repeat(7) { val x0 = az.entre(285f, 380f); val y0 = az.entre(128f, 150f); drawLine(v.l.tono(C.HOLLIN, lejos).copy(alpha = 0.45f), Offset(x0, y0), Offset(x0 + az.entre(-4f, 4f), y0 + az.entre(15f, 45f)), 0.6f) }
            // La mesa de arriba, más clara, y el talud de abajo: un pedregal del color del llano.
            drawPath(curva(BORDE_PARED.map { it + Offset(0f, 1.4f) }), v.l.tono(lerp(C.ARENA, C.PIEDRA, 0.4f), lejos), style = Stroke(2.2f))
            val talud = Path().apply { moveTo(266f, 205f); lineTo(266f, 186f); for (k in 0..10) lineTo(270f + k * 12f, 178f + az.entre(-5f, 4f)); lineTo(396f, 205f); close() }
            drawPath(talud, Brush.verticalGradient(listOf(lerp(roca, v.l.tono(C.PARAMO, lejos), 0.6f), v.l.tono(C.PARAMO, lejos * 0.7f)), 176f, 200f))
            repeat(30) { val q = Offset(az.entre(268f, 392f), az.entre(182f, 200f)); drawOval(honda.copy(alpha = 0.5f), Offset(q.x - 1.2f, q.y - 0.4f), Size(2.4f, 0.9f)) }
        }
        // El pozo al pie de la pared: el brocal redondo con su armazón y el cubo, y las huellas que llegan a él.
        val pozo = Offset(306f, 194f)
        drawOval(v.lz.color(C.PIEDRA, V3(0f, 1f, -0.4f), lejos), Offset(pozo.x - 4f, pozo.y - 1.6f), Size(8f, 3.2f))
        drawOval(v.l.tono(C.HOLLIN, lejos), Offset(pozo.x - 2.6f, pozo.y - 1.1f), Size(5.2f, 1.8f))
        drawLine(v.l.tono(C.MADERA, lejos), pozo + Offset(-3f, 0f), pozo + Offset(-2.6f, -6f), 0.6f); drawLine(v.l.tono(C.MADERA, lejos), pozo + Offset(3f, 0f), pozo + Offset(2.6f, -6f), 0.6f)
        drawLine(v.l.tono(C.MADERA, lejos), pozo + Offset(-3.2f, -6f), pozo + Offset(3.2f, -6f), 0.7f)
    }

    /** Un velo de polvo entre dos planos, de [y0] a [y1]: más espeso en medio, del ocre del aire. */
    fun DrawScope.veloDePolvo(v: VistaOrden, y0: Float, y1: Float, k: Float) {
        if (k <= 0.01f) return
        val c = lerp(v.l.bruma, v.l.tono(C.ARENA), 0.4f)
        drawRect(Brush.verticalGradient(0f to c.copy(alpha = 0f), 0.55f to c.copy(alpha = (0.85f * k).coerceIn(0f, 1f)), 1f to c.copy(alpha = (0.4f * k).coerceIn(0f, 1f)), startY = y0, endY = y1), Offset(0f, y0), Size(ANCHO, y1 - y0))
    }

    /** Las sombras de las nubes sobre el llano, «como rebaños»: los días nublados y de viento, manchas que cruzan el páramo. */
    fun DrawScope.sombrasDeNubes(v: VistaOrden) {
        val clima = v.m.ficha.clima
        if (clima != Clima.NUBES && clima != Clima.VIENTO && clima != Clima.DESPEJADO) return
        val sol = EscenaOrden.sol(v.l) + (if (clima == Clima.NUBES) 0.6f * (1f - v.l.noche) else 0f)
        if (sol <= 0.05f) return
        val az = v.m.azar("rebanos"); val n = when (clima) { Clima.NUBES -> 9; Clima.VIENTO -> 5; else -> if (az.real() < 0.5f) 0 else 3 }
        if (n == 0) return
        capaSombra(v.r, 0.7f * (if (clima == Clima.NUBES) 0.8f else 1f)) { c ->
            repeat(n) {
                val y = H + 3f + 60f * az.real(); val k = (y - H) / 60f
                mancha(Offset(az.entre(150f, ANCHO + 20f), y), 14f + 50f * k, 1.4f + 6f * k, c, 0.9f)
            }
        }
    }

    /** El resplandor de la hora azul en el horizonte, detrás de las mesetas: las recorta. */
    fun DrawScope.horaAzul(v: VistaOrden) {
        val h = v.m.hora.toFloat(); val k = when { h < 20.2f || h > 22.4f -> 0f; h < 20.8f -> (h - 20.2f) / 0.6f; else -> (22.4f - h) / 1.6f } * (1f - v.l.nublado)
        if (k <= 0.02f) return
        val c = lerp(v.l.bajo, Color(0xFFFFA070), 0.35f)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, c.copy(alpha = 0.35f * k), Color.Transparent), H - 14f, H + 4f), Offset(0f, H - 14f), Size(ANCHO, 18f), blendMode = BlendMode.Plus)
    }
}
