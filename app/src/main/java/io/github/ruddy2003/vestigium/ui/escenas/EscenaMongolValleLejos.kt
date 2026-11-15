package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ger
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.ValleMongol.H
import io.github.ruddy2003.vestigium.ui.escenas.ValleMongol.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Lo lejano del valle de invierno, a la derecha del cerro: el cielo, dos sierras azules con su nieve y, al otro lado del
 * río, la estepa en cuatro lomas que se enciman, cada una más cerca, más grande y más cálida; en una, otro campamento
 * con su humo y su rebaño. Todo con lado de luz y lado de sombra, y la bruma entre un plano y otro (regla 10).
 */
internal object ValleLejos {
    private val C = ColorMongol

    /** La sierra del fondo, puesta a mano: picos y collados de distinta altura, nunca por encima del sol de las 7:00. */
    private val SIERRA_ALTA = pts(
        226f, 116f, 234f, 108.6f, 241f, 103f, 247f, 99.6f, 253f, 95.4f, 258f, 97.8f, 264f, 92.4f, 270f, 89.8f, 275f, 92.6f, 281f, 91.2f,
        288f, 95f, 294f, 92.6f, 300f, 89.6f, 306f, 91.6f, 312f, 90.2f, 319f, 86f, 324f, 88.2f, 330f, 84.2f, 337f, 81.6f, 342f, 83.4f,
        349f, 79f, 355f, 80.8f, 361f, 84.4f, 367f, 82.2f, 373f, 79.4f, 380f, 81.8f, 386f, 85.2f, 394f, 83.6f,
    )

    /** La sierra de delante, más baja y más oscura. */
    private val SIERRA_BAJA = pts(
        250f, 116f, 258f, 111.4f, 266f, 108.2f, 274f, 105.6f, 281f, 107.4f, 288f, 103.2f, 295f, 104.8f, 302f, 100.6f, 309f, 102.4f, 317f, 99.2f,
        324f, 101.8f, 332f, 98.6f, 340f, 100.8f, 347f, 97.2f, 355f, 99.4f, 362f, 96.8f, 370f, 100.2f, 378f, 98f, 386f, 101.4f, 394f, 100f,
    )

    /** La loma de la otra orilla, la más cercana: un escarpe que baja al río. */
    val LOMA_CERCA = pts(308f, 134f, 314f, 131.2f, 321f, 129f, 328f, 127.6f, 335f, 127f, 342f, 124.6f, 349f, 125.2f, 356f, 123f, 363f, 123.8f, 371f, 121.6f, 379f, 122.8f, 386f, 121.8f, 394f, 122.4f)

    /** Dónde suben los humos de los otros campamentos (la parte viva los pinta). */
    val HUMOS = listOf(Offset(351f, 113.4f), Offset(366f, 113.8f), Offset(322f, 119f))

    fun DrawScope.lejos(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        // Las dos sierras, con la bruma entre una y otra: la del fondo, nevada todo el año; la de delante, según el invierno.
        sierra(p, SIERRA_ALTA, H + 2f, lerp(C.MONTE, C.SOMBRA, 0.12f), 0.86f, 1f, fijo("sierra", 0))
        bruma(p, 84f, H + 2f, 0.5f)
        sierra(p, SIERRA_BAJA, H + 2f, lerp(lerp(C.MONTE, C.SOMBRA, 0.3f), C.HOLLIN, 0.3f), 0.62f, 0.2f + 0.5f * nieve, fijo("sierra", 1))
        bruma(p, 98f, H + 3f, 0.42f)
        lomas(m, p, sol, nieve)
    }

    private fun DrawScope.bruma(p: Pincel, y0: Float, y1: Float, alfa: Float) {
        val l = p.l
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(l.bruma, l.bajo, 0.5f).copy(alpha = alfa * (1f - 0.5f * l.noche)), startY = y0, endY = y1), Offset(200f, y0), Size(200f, y1 - y0))
    }

    /**
     * Una sierra: de cada pico baja una arista quebrada que separa la cara de luz de la cara en sombra (cambian de lado
     * con la hora); en los picos altos, la nieve, de borde roto, con sus lenguas por las canales y su mitad en sombra azul.
     * De noche, la luna le saca el filo. Es la idea de la sierra de Prehistoria, con la nieve de la estepa.
     */
    private fun DrawScope.sierra(p: Pincel, cresta: List<Offset>, base: Float, roca: Color, lejos: Float, nieve: Float, az: Azar) {
        val l = p.l; val forma = curva(cresta, base); val hacia = if (p.solALaDerecha) -1f else 1f
        monte(l, cresta, base, roca, lejos)
        val alta = base - cresta.minOf { it.y }
        clipPath(forma) {
            for (i in 1 until cresta.size - 1) {
                val q = cresta[i]; if (q.y > cresta[i - 1].y || q.y > cresta[i + 1].y) continue
                val h = base - q.y
                val arista = (0..4).map { j -> Offset(q.x + (if (j == 0) 0f else az.entre(-0.1f, 0.22f) * h * hacia * j * 0.45f), q.y + h * j / 4f) }
                drawPath(poligono(arista + listOf(Offset(q.x + hacia * h * 1.2f, base), Offset(q.x + hacia * h * 0.7f, q.y + h * 0.4f))), p.sombra(roca, lejos).copy(alpha = 0.55f))
                drawPath(poligono(arista + listOf(Offset(q.x - hacia * h * 1.0f, base), Offset(q.x - hacia * h * 0.5f, q.y + h * 0.45f))), p.luz(lerp(roca, C.FIELTRO, 0.3f), lejos).copy(alpha = 0.3f * p.r.dia + 0.06f))
                if (nieve > 0.05f && h > alta * 0.35f) {
                    val n = h * (0.12f + 0.2f * nieve) * az.entre(0.75f, 1.15f)
                    val borde = (0..10).map { j -> val t = j / 10f; val lengua = az.real().let { 0.3f + 1.8f * it * it }; Offset(q.x + (1.6f - 3.2f * t) * n + az.entre(-0.12f, 0.12f) * n, q.y + n * lengua * (1f - 0.5f * kotlin.math.abs(2f * t - 1f))) }
                    val nevero = poligono(listOf(Offset(q.x - 1.8f * n, q.y - 3f), Offset(q.x + 1.8f * n, q.y - 3f)) + borde)
                    drawPath(nevero, p.luz(C.NIEVE, lejos * 0.75f).copy(alpha = 0.95f))
                    clipPath(nevero) { drawPath(poligono(arista.take(4) + listOf(Offset(q.x + hacia * n * 2.6f, q.y + n * 2.4f), Offset(q.x + hacia * n * 2.6f, q.y - 4f))), p.sombra(lerp(C.HIELO, C.NIEVE, 0.3f), lejos * 0.75f).copy(alpha = 0.92f)) }
                }
            }
        }
        if (l.noche > 0.4f) drawPath(curva(cresta), C.LUNA.copy(alpha = 0.22f * l.noche * (1f - l.nublado)), style = Stroke(0.6f))
        else drawPath(curva(cresta), p.luz(lerp(roca, C.NIEVE, 0.5f), lejos).copy(alpha = 0.3f), style = Stroke(0.5f))
    }

    /** Una loma de la estepa: [cresta] (de izquierda a derecha), su relleno hasta [fondo], su color y lo lejos que está. */
    private class Loma(val cresta: List<Offset>, val fondo: Float, val lejos: Float, val calida: Float)

    private val LOMAS = listOf(
        Loma(pts(292f, 117.6f, 304f, 115.8f, 316f, 114.2f, 328f, 115f, 340f, 112.8f, 352f, 113.4f, 364f, 111.6f, 376f, 112.4f, 394f, 111f), 124f, 0.6f, 0f),
        Loma(pts(300f, 120.4f, 312f, 118.6f, 322f, 119.2f, 333f, 116.8f, 345f, 117.4f, 357f, 115.6f, 369f, 116.2f, 380f, 114.8f, 394f, 115.6f), 128f, 0.5f, 0.15f),
        Loma(pts(304f, 124.6f, 314f, 122.4f, 324f, 121.2f, 336f, 121.8f, 348f, 119.6f, 360f, 120.8f, 372f, 118.6f, 384f, 119.4f, 394f, 118.6f), 134f, 0.4f, 0.3f),
    )

    /** Las lomas del otro lado del río, de atrás adelante, y lo que hay en ellas. */
    private fun DrawScope.lomas(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val az = fijo("lomas"); val pasto = EscenaMongol.pasto(m)
        val hacia = if (p.solALaDerecha) -1f else 1f
        val seca = lerp(lerp(C.ESTEPA, C.TIERRA, 0.2f), C.VERDE, 0.5f * pasto)
        for ((j, lo) in (LOMAS + Loma(LOMA_CERCA, 210f, 0.3f, 0.45f)).withIndex()) {
            val cresta = rota(lo.cresta, az, 0.35f + 0.15f * j, 2)
            val propio = lerp(lerp(seca, C.CUERO, 0.12f * lo.calida), C.NIEVE, nieve * 0.82f)
            val forma = curva(cresta, lo.fondo)
            drawPath(forma, Brush.verticalGradient(0f to p.luz(propio, lo.lejos), 0.35f to p.medio(propio, lo.lejos), 1f to p.medio(lerp(propio, C.MONTE, 0.25f), lo.lejos), startY = cresta.minOf { it.y }, endY = lo.fondo))
            clipPath(forma) {
                // Cada lomo tiene su cara de sombra, tendida del lado contrario al sol, y su cara de luz.
                for (i in 1 until cresta.size - 1) {
                    val q = cresta[i]; if (q.y > cresta[i - 1].y || q.y > cresta[i + 1].y) continue
                    val h = (lo.fondo - q.y).coerceAtMost(26f)
                    drawPath(poligono(q, q + Offset(hacia * h * 2.4f, h), q + Offset(hacia * h * 0.25f, h)), p.sombra(propio, lo.lejos).copy(alpha = 0.55f))
                    drawPath(poligono(q, q + Offset(-hacia * h * 1.6f, h * 0.8f), q + Offset(-hacia * h * 0.2f, h)), p.luz(propio, lo.lejos).copy(alpha = 0.3f))
                }
                // La nieve se va de los lomos con el viento: la hierba seca asoma en la cresta y en franjas.
                if (nieve > 0.1f) for (i in 0 until cresta.size - 1) { val c = (cresta[i] + cresta[i + 1]) / 2f + Offset(0f, 1.2f + j * 0.4f); drawPath(borron(c, az.entre(3f, 7f) + j, 0.8f + 0.4f * j, az, 7, 0.4f), p.por(lerp(seca, C.CUERO, 0.2f), 0.2f, lo.lejos).copy(alpha = 0.6f * nieve)) }
                // Rachas: lomos claros y hondonadas, más marcadas cuanto más cerca.
                repeat(4 + 2 * j) { val c = Offset(az.entre(300f, 392f), cresta.minOf { it.y } + az.entre(2f, 8f + 10f * j)); mancha(c, az.entre(8f, 20f), 0.8f + 0.5f * j, if (it % 2 == 0) p.luz(lerp(propio, Color.White, 0.3f), lo.lejos) else p.sombra(propio, lo.lejos), 0.35f) }
            }
            drawPath(curva(cresta), p.luz(lerp(propio, C.FIELTRO, 0.4f), lo.lejos).copy(alpha = 0.5f * (1f - 0.6f * l.noche)), style = Stroke(0.5f))
            if (j == 1) campamentoLejano(m, p, sol, nieve)
            if (j == 2) rebanoLejano(m, p, sol)
        }
        escarpe(m, p, sol, nieve)
    }

    /** La loma de la otra orilla, de cerca: su escarpe roto, en sombra o al sol según la hora, con peñas y la nieve en las canales. */
    private fun DrawScope.escarpe(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val az = fijo("escarpe"); val lejos = 0.28f
        val forma = curva(LOMA_CERCA, 210f)
        val cara = sol.t(V3(-0.55f, 0.45f, -0.7f))                              // mira al río y a la izquierda
        val roca = lerp(C.PIEDRA, C.HOLLIN, 0.3f)
        clipPath(forma) {
            // La banda de roca del borde, rota, que asoma bajo la hierba de arriba.
            val borde = LOMA_CERCA.map { it + Offset(0f, 2.2f) }
            for (i in 0 until borde.size - 1) if (az.real() < 0.7f) { val a = borde[i]; val b = borde[i + 1]; val h = az.entre(1.6f, 3.4f)
                drawPath(poligono(a, b, b + Offset(-0.6f, h), a + Offset(0.4f, h * az.entre(0.6f, 1f))), p.por(roca, cara - 0.2f, lejos)); drawLine(p.luz(lerp(roca, C.PIEDRA, 0.6f), lejos).copy(alpha = 0.6f * p.r.dia), a, b, 0.5f) }
            // Las canales del escarpe: de largo, ancho, sesgo y separación desiguales; algunas se abren en dos. Con su lado de
            // sombra y la nieve dentro.
            var x = 313f
            while (x < 392f) {
                val y0 = TrazosValle.alturaEn(LOMA_CERCA, x) + az.entre(3f, 5f); val largo = az.entre(5f, 22f); val w = az.entre(1.4f, 4f); val sesgo = az.entre(-6f, 2f)
                val canal = rota(listOf(Offset(x, y0), Offset(x + sesgo * 0.5f, y0 + largo * 0.5f), Offset(x + sesgo, y0 + largo)), az, 0.9f, 2)
                drawPath(poligono(canal + canal.reversed().map { it + Offset(w * az.entre(0.6f, 1.2f), 1f) }), p.por(roca, cara - 0.6f, lejos).copy(alpha = 0.6f))
                if (az.real() < 0.35f) { val q = canal[canal.size / 2]; drawLine(p.por(roca, cara - 0.6f, lejos).copy(alpha = 0.5f), q, q + Offset(az.entre(3f, 7f), az.entre(4f, 9f)), w * 0.6f, StrokeCap.Round) }
                if (nieve > 0.1f) drawPath(curva(canal.map { it + Offset(w * 0.4f, 0.8f) }), p.por(C.NIEVE, cara + 0.2f, lejos).copy(alpha = nieve), style = Stroke(w * 0.5f, cap = StrokeCap.Round))
                x += az.entre(5f, 19f)
            }
            // Peñas sueltas al borde.
            repeat(7) { val q = Offset(az.entre(314f, 390f), 0f).let { Offset(it.x, TrazosValle.alturaEn(LOMA_CERCA, it.x) + az.entre(2f, 6f)) }; val w = az.entre(2f, 4f)
                drawPath(borron(q, w, w * 0.6f, az, 6, 0.3f, 0.9f), p.por(roca, cara, lejos)); drawPath(borron(q + Offset(-hacia(p) * w * 0.3f, -w * 0.2f), w * 0.6f, w * 0.3f, az, 6, 0.3f), p.luz(lerp(roca, C.PIEDRA, 0.5f), lejos).copy(alpha = 0.6f * p.r.dia)) }
        }
    }

    private fun hacia(p: Pincel) = if (p.solALaDerecha) 1f else -1f

    /** Otro campamento del valle, en una loma: tres tiendas con su sombra corta y, de noche, la luz por el aro. */
    private fun DrawScope.campamentoLejano(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val dentro = EscenaMongol.fuegoDentro(m)
        for ((j, x) in listOf(347f, 355f, 364f).withIndex()) {
            val pie = Offset(x, 116.4f + (j % 2) * 0.6f)
            sombraCorta(sol.r, pie, 5f, 2.4f)
            ger(Ger(Sitio(MIRADA, pie, 1.25f, 0.2f * j), 900L + j, 0.6f, dentro = dentro, nieve = nieve, lejos = 0.5f, detalle = false), p, sol)
            if (p.l.noche > 0.3f) resplandor(pie + Offset(0f, -3.4f), 4f, 0.5f * p.l.noche * dentro, C.LUMBRE)
        }
        // El corral de ese campamento: un trazo oscuro con el rebaño apretado.
        drawPath(curva(pts(368f, 118.6f, 374f, 118f, 380f, 118.4f)), p.medio(C.ESTIERCOL, 0.5f).copy(alpha = 0.8f), style = Stroke(0.8f))
    }

    /** Un rebaño de otro campamento, en la loma de delante: puntos claros con su sombra; de noche no se ve. */
    private fun DrawScope.rebanoLejano(m: Momento, p: Pincel, sol: Sol3) {
        if (p.l.noche > 0.5f) return
        val az = Azar(Semilla.de("mongol", "valle", "rebano lejano", m.ficha.dia))
        val c = Offset(az.entre(338f, 370f), 125.4f)
        repeat(16) {
            val q = c + Offset(az.entre(-9f, 9f), az.entre(-1.2f, 1.6f))
            drawOval(sol.r.color.copy(alpha = 0.5f * sol.r.fuerza), Offset(q.x - 0.4f + sol.r.ax.coerceIn(-1.5f, 1.5f), q.y + 0.2f), Size(1.6f, 0.5f), blendMode = BlendMode.Multiply)
            drawOval(p.por(C.LANA, 0.3f, 0.36f), Offset(q.x - 0.7f, q.y - 0.6f), Size(1.4f, 0.8f))
        }
        // Y unos caballos, más allá.
        repeat(4) { val q = Offset(az.entre(372f, 388f), 121f + az.entre(-0.5f, 0.6f)); drawOval(p.medio(lerp(C.CUERO, C.HOLLIN, 0.3f), 0.42f), Offset(q.x - 1f, q.y - 1f), Size(2f, 0.9f)); drawLine(p.medio(lerp(C.CUERO, C.HOLLIN, 0.3f), 0.42f), q + Offset(-0.6f, 0f), q + Offset(-0.6f, 0.6f), 0.3f) }
        // El carro del monje que va a ver al kan, en el vado, lejos.
        if (EscenaMongol.monjeDePaso(m)) monje(p, Offset(330f, 125.8f))
    }

    /** El carro cubierto del monje chino que cruza el Kerulen, con sus bueyes y su séquito de caballos **[Aproximación]**. */
    private fun DrawScope.monje(p: Pincel, pie: Offset) {
        val madera = lerp(C.MADERA, C.HOLLIN, 0.2f); val toldo = lerp(C.ROJO, C.MADERA, 0.3f)
        drawCircle(p.medio(madera, 0.35f), 1.4f, pie + Offset(-1.2f, -1.4f), style = Stroke(0.35f)); drawCircle(p.medio(madera, 0.35f), 1.4f, pie + Offset(2.6f, -1.4f), style = Stroke(0.35f))
        drawRect(p.medio(madera, 0.35f), pie + Offset(-2.4f, -3.4f), Size(6.4f, 1.2f))
        drawPath(poligono(pie + Offset(-2.6f, -3.4f), pie + Offset(4.2f, -3.4f), pie + Offset(3.6f, -6.6f), pie + Offset(1f, -7.4f), pie + Offset(-2f, -6.6f)), p.por(toldo, 0.3f, 0.35f))
        for (dx in listOf(-6f, -8.6f)) { drawOval(p.medio(lerp(C.CUERO, C.HOLLIN, 0.4f), 0.35f), Offset(pie.x + dx - 1.2f, pie.y - 2.6f), Size(2.6f, 1.4f)) }
        for (dx in listOf(8f, 10.6f, 13f)) { drawOval(p.medio(lerp(C.CUERO, C.ORO, 0.2f), 0.35f), Offset(pie.x + dx - 1f, pie.y - 2.2f), Size(2f, 1f)); drawLine(p.medio(C.CUERO, 0.35f), Offset(pie.x + dx + 0.8f, pie.y - 2f), Offset(pie.x + dx + 1.4f, pie.y - 3.2f), 0.5f) }
    }
}
