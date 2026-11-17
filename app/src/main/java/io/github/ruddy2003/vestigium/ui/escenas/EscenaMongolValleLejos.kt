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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
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

    /**
     * La cadena del fondo, puesta a mano: macizos anchos y romos, de altura y ancho distintos (montes de estepa, no
     * picos alpinos), con un collado justo debajo del sol de las 7:00.
     */
    private val SIERRA_ALTA = pts(
        220f, 117f, 232f, 111f, 244f, 104.4f, 254f, 99.6f, 263f, 97f, 272f, 96.4f, 281f, 97.8f, 289f, 100.6f, 297f, 103.6f, 305f, 104.6f,
        313f, 103.2f, 321f, 99f, 330f, 94f, 339f, 90.6f, 348f, 89.4f, 357f, 90.2f, 365f, 92.6f, 372f, 95.4f, 380f, 95.8f, 387f, 94f, 396f, 92.6f,
    )

    /** La cadena de delante, más baja, más ancha y de otro azul, más oscuro. */
    private val SIERRA_BAJA = pts(
        244f, 117.6f, 256f, 112.8f, 268f, 109.6f, 280f, 108.4f, 291f, 109.6f, 301f, 108.8f, 312f, 105.8f, 324f, 104f, 336f, 105.2f,
        346f, 107.4f, 356f, 106f, 367f, 102.6f, 378f, 101.4f, 388f, 102.4f, 396f, 103.6f,
    )

    /** La loma de la otra orilla, la más cercana: un escarpe que baja al río. */
    val LOMA_CERCA = pts(308f, 134f, 314f, 131.2f, 321f, 129f, 328f, 127.6f, 335f, 127f, 342f, 124.6f, 349f, 125.2f, 356f, 123f, 363f, 123.8f, 371f, 121.6f, 379f, 122.8f, 386f, 121.8f, 394f, 122.4f)

    /** Dónde suben los humos de los otros campamentos (la parte viva los pinta). */
    val HUMOS = listOf(Offset(351f, 113.4f), Offset(366f, 113.8f), Offset(322f, 119f))

    fun DrawScope.lejos(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, m.ficha.clima, semilla)
        // Las dos sierras, con la bruma entre una y otra: la del fondo, nevada todo el año; la de delante, según el invierno.
        sierra(p, SIERRA_ALTA, H + 2f, lerp(C.MONTE, C.SOMBRA, 0.25f), 0.72f, 1f, fijo("sierra", 0))
        bruma(p, 90f, H + 2f, 0.5f)
        sierra(p, SIERRA_BAJA, H + 2f, lerp(lerp(C.MONTE, C.SOMBRA, 0.4f), C.HOLLIN, 0.32f), 0.52f, 0.1f + 0.55f * nieve, fijo("sierra", 1))
        bruma(p, 98f, H + 3f, 0.42f)
        lomas(m, p, sol, nieve)
        // Por la tarde, con la sombra del cerro sobre el valle, la lejanía del otro lado del río queda dorada.
        if (p.l.solX <= 0.6f) with(ValleLuz) { luzCalida(poligono(Offset(292f, 88f), Offset(392f, 86f), Offset(392f, 150f), Offset(300f, 136f)), p.l, 1.4f, 88f, 150f) }
    }

    private fun DrawScope.bruma(p: Pincel, y0: Float, y1: Float, alfa: Float) {
        val l = p.l
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(l.bruma, l.bajo, 0.5f).copy(alpha = alfa * (1f - 0.5f * l.noche)), startY = y0, endY = y1), Offset(200f, y0), Size(200f, y1 - y0))
    }

    /**
     * Una cadena de macizos romos: el relleno con su degradado (más claro arriba, la bruma abajo); la cara de cada macizo
     * que no ve el sol, en sombra azul que se apaga hacia el pie; la nieve en lo alto como una capa blanda que baja más
     * por las vaguadas, con su mitad en sombra; y, de noche, el filo de la luna. Sin aristas en diente de sierra.
     */
    private fun DrawScope.sierra(p: Pincel, cresta: List<Offset>, base: Float, roca: Color, lejos: Float, nieve: Float, az: Azar) {
        val l = p.l; val forma = curva(cresta, base); val sombraHacia = if (p.solALaDerecha) -1f else 1f
        val arriba = cresta.minOf { it.y }
        drawPath(forma, Brush.verticalGradient(0f to p.medio(lerp(roca, C.FIELTRO, 0.1f), lejos), 0.5f to p.medio(roca, lejos), 1f to lerp(p.medio(roca, lejos), l.bruma, 0.3f), startY = arriba, endY = base))
        // Las cumbres (los mínimos de y) y, a cada lado, la ladera hasta el collado.
        val cumbres = (1 until cresta.size - 1).filter { cresta[it].y <= cresta[it - 1].y && cresta[it].y < cresta[it + 1].y }
        clipPath(forma) {
            for (i in cumbres) {
                var j = i; while (j + sombraHacia.toInt() in cresta.indices && cresta[j + sombraHacia.toInt()].y >= cresta[j].y) j += sombraHacia.toInt()
                val ladera = (if (j > i) (i..j) else (j..i).reversed()).map { cresta[it] }
                val cara = poligono(ladera + listOf(Offset(ladera.last().x, base), Offset(ladera.first().x, base)))
                drawPath(cara, Brush.verticalGradient(0f to p.sombra(roca, lejos).copy(alpha = 0.6f), 1f to p.sombra(roca, lejos).copy(alpha = 0.1f), startY = arriba, endY = base))
                // Unas vaguadas blandas que bajan por la cara de luz.
                repeat(2) { val x = cresta[i].x - sombraHacia * az.entre(4f, 14f); val y0 = TrazosValle.alturaEn(cresta, x) + 1f
                    drawLine(p.sombra(roca, lejos).copy(alpha = 0.22f), Offset(x, y0), Offset(x - sombraHacia * az.entre(1f, 4f), base - 4f), az.entre(1.5f, 3f), StrokeCap.Round) }
            }
            // La nieve: una capa blanda en lo alto, más honda en las vaguadas (los collados) y con su lado en sombra.
            if (nieve > 0.05f) {
                val hondo = (base - arriba)
                val bajo = cresta.map { q -> val h = base - q.y; q + Offset(0f, (h * (0.12f + 0.2f * nieve) * (0.8f + 0.25f * kotlin.math.sin(q.x * 0.21f)) - (q.y - arriba) * 0.45f).coerceAtLeast(0f)) }
                val capa = curva(cresta.map { it + Offset(0f, -2f) }).apply { for (q in bajo.reversed()) lineTo(q); close() }
                clipPath(capa) {
                    drawPath(forma, p.luz(C.NIEVE, lejos * 0.8f))
                    for (i in cumbres) {
                        var j = i; while (j + sombraHacia.toInt() in cresta.indices && cresta[j + sombraHacia.toInt()].y >= cresta[j].y) j += sombraHacia.toInt()
                        val ladera = (if (j > i) (i..j) else (j..i).reversed()).map { cresta[it] }
                        drawPath(poligono(ladera + listOf(Offset(ladera.last().x, base), Offset(ladera.first().x, base))), p.sombra(lerp(C.NIEVE, C.HIELO, 0.4f), lejos * 0.8f).copy(alpha = 0.85f))
                    }
                }
                drawPath(curva(bajo), p.medio(roca, lejos).copy(alpha = 0.25f * hondo / hondo), style = Stroke(0.6f))
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
                val y0 = TrazosValle.alturaEn(LOMA_CERCA, x) + az.entre(3f, 9f); val largo = az.entre(4f, 16f); val w = az.entre(1.2f, 3.4f); val sesgo = az.entre(-12f, 4f)
                val canal = rota(listOf(Offset(x, y0), Offset(x + sesgo * 0.5f, y0 + largo * 0.5f), Offset(x + sesgo, y0 + largo)), az, 1.2f, 2)
                drawPath(poligono(canal + canal.reversed().map { it + Offset(w * az.entre(0.6f, 1.2f), 1f) }), p.por(roca, cara - 0.5f, lejos).copy(alpha = 0.38f))
                if (az.real() < 0.35f) { val q = canal[canal.size / 2]; drawLine(p.por(roca, cara - 0.6f, lejos).copy(alpha = 0.5f), q, q + Offset(az.entre(3f, 7f), az.entre(4f, 9f)), w * 0.6f, StrokeCap.Round) }
                if (nieve > 0.1f) drawPath(curva(canal.map { it + Offset(w * 0.4f, 0.8f) }), p.por(C.NIEVE, cara + 0.2f, lejos).copy(alpha = nieve), style = Stroke(w * 0.5f, cap = StrokeCap.Round))
                x += az.entre(7f, 24f)
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
    }

    /**
     * El carro cubierto del monje chino que cruza el Kerulen (temporada 2, 25 de abril), por la otra orilla, con sus
     * bueyes y los caballos de su escolta **[Aproximación]**: el cuento solo dice que pasa. Va después del río.
     */
    fun DrawScope.monjeEnLaOrilla(m: Momento, p: Pincel, sol: Sol3) {
        if (!EscenaMongol.monjeDePaso(m)) return
        val pie = Offset(352f, 149.4f); val k = 1.9f; val lejos = 0.2f
        fun q(x: Float, y: Float) = pie + Offset(x * k, y * k)
        val madera = lerp(C.MADERA, C.HOLLIN, 0.2f); val toldo = lerp(C.ROJO, C.MADERA, 0.3f)
        drawOval(sol.r.color.copy(alpha = 0.5f * sol.r.fuerza), Offset(q(-10f, 0f).x, pie.y - 0.6f), Size(26f * k, 1.6f * k), blendMode = BlendMode.Multiply)
        for (dx in listOf(-1.2f, 2.6f)) { drawCircle(p.medio(madera, lejos), 1.4f * k, q(dx, -1.4f), style = Stroke(0.4f * k)); drawLine(p.medio(madera, lejos), q(dx - 1.2f, -1.4f), q(dx + 1.2f, -1.4f), 0.2f * k) }
        drawRect(p.por(madera, 0.2f, lejos), q(-2.4f, -3.4f), Size(6.4f * k, 1.2f * k))
        drawPath(poligono(q(-2.6f, -3.4f), q(4.2f, -3.4f), q(3.6f, -6.6f), q(1f, -7.6f), q(-2f, -6.6f)), p.por(toldo, if (p.solALaDerecha) 0.4f else -0.2f, lejos))
        drawLine(p.luz(C.ORO, lejos), q(-2.2f, -6.4f), q(3.4f, -6.4f), 0.3f * k)
        // Los bueyes de la vara, y los caballos de la escolta detrás.
        for (dx in listOf(-6f, -9f)) { drawOval(p.por(lerp(C.CUERO, C.HOLLIN, 0.4f), 0.1f, lejos), Offset(q(dx - 1.3f, -2.8f).x, q(0f, -2.8f).y), Size(2.8f * k, 1.5f * k)); drawLine(p.medio(C.HOLLIN, lejos), q(dx - 1f, -1.4f), q(dx - 1f, 0f), 0.3f * k); drawLine(p.medio(C.HOLLIN, lejos), q(dx + 1f, -1.4f), q(dx + 1f, 0f), 0.3f * k) }
        drawLine(p.medio(madera, lejos), q(-2.4f, -2.6f), q(-8f, -2.2f), 0.25f * k)
        for (dx in listOf(8f, 10.8f, 13.4f)) { drawOval(p.por(lerp(C.CUERO, C.ORO, 0.2f), 0.2f, lejos), Offset(q(dx - 1f, -2.4f).x, q(0f, -2.4f).y), Size(2.2f * k, 1.1f * k)); drawLine(p.medio(C.CUERO, lejos), q(dx + 0.9f, -2.2f), q(dx + 1.5f, -3.6f), 0.5f * k); for (pp in listOf(-0.6f, 0.8f)) drawLine(p.medio(C.CUERO, lejos), q(dx + pp, -1.4f), q(dx + pp, 0f), 0.25f * k) }
    }
}
