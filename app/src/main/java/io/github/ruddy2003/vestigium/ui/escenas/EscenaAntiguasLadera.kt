package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.CABRA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.CRESTA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.CUMBRE
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.H
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.alturaEn
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.cinta
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.en
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Clima
import kotlin.math.sin

/**
 * El cielo y las cumbres: lo de más lejos. La cumbre alta a la izquierda, con su cara norte que baja larga hasta el
 * collado, «una muesca entre dos cumbres por donde el cielo se asoma al otro lado», y la otra cumbre, empinada, que sale
 * por encima del alero de la posada. Hechas de caras (de cada espolón baja una arista; entre dos, una canal), cada una con
 * su luz; bandas de roca, la nieve según el arco (una capa en lo alto que baja más por las canales, cintas en las canales,
 * manchas en las repisas) y, colgado de la cara norte, el refugio de la Cabra hasta el alud.
 */
internal object LaderaAntiguas {
    private val C = ColorAntiguas

    // ---------------------------------------------------------------- El cielo

    fun DrawScope.cielo(v: VistaAntiguas) {
        val l = v.l; val semilla = v.m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla); nubes(l, H, v.clima, semilla)
    }

    // ---------------------------------------------------------------- Las cumbres

    /** Una arista (espolón, `true`) o una canal que baja de la cresta, y la normal de la cara que queda a su derecha. */
    private class Linea(val puntos: List<Offset>, val espolon: Boolean, val cara: V3, val nieve: Float = 1f)

    private val DERECHA = V3(0.62f, 0.4f, -0.68f)
    private val IZQUIERDA = V3(-0.66f, 0.38f, -0.65f)
    private val FRENTE = V3(-0.08f, 0.45f, -0.89f)
    private val FRENTE_DER = V3(0.32f, 0.42f, -0.85f)

    private val LINEAS = listOf(
        Linea(pts(-6f, 98f, -6f, 240f), false, IZQUIERDA),
        Linea(pts(66f, 70.2f, 72f, 96f, 80f, 120f, 86f, 150f), true, FRENTE_DER, 0.6f),
        Linea(pts(98f, 60.2f, 104f, 82f, 112f, 104f, 118f, 132f), false, IZQUIERDA, 1f),
        Linea(pts(146f, 40.6f, 150f, 62f, 156f, 86f, 160f, 112f, 162f, 140f), true, FRENTE, 0.7f),
        Linea(pts(178f, 28.6f, 176f, 52f, 172f, 78f, 168f, 104f, 166f, 132f, 164f, 170f), false, IZQUIERDA, 1.2f),
        Linea(pts(196f, 23.8f, 199f, 46f, 197f, 72f, 192f, 100f, 186f, 130f, 178f, 170f), true, DERECHA, 0.5f),
        Linea(pts(212f, 29.6f, 220f, 52f, 226f, 76f, 228f, 102f, 224f, 128f, 216f, 154f), false, FRENTE, 1.4f),
        Linea(pts(232f, 41.8f, 240f, 64f, 246f, 88f, 250f, 112f, 252f, 132f), true, DERECHA, 0.6f),
        Linea(pts(252f, 56.4f, 260f, 76f, 266f, 96f, 270f, 116f), false, FRENTE, 1.2f),
        Linea(pts(270f, 71.6f, 279f, 90f, 286f, 106f), true, DERECHA, 0.7f),
        Linea(pts(287f, 90f, 294f, 102f, 299f, 110f), false, FRENTE, 1f),
        Linea(pts(305f, 108.6f, 306f, 118f), true, IZQUIERDA, 0.8f),
        Linea(pts(324f, 89.6f, 320f, 106f, 318f, 120f), false, IZQUIERDA, 1.2f),
        Linea(pts(344f, 64.6f, 338f, 90f, 334f, 118f), true, IZQUIERDA, 0.7f),
        Linea(pts(365f, 44.4f, 358f, 74f, 352f, 104f), false, IZQUIERDA, 1.2f),
        Linea(pts(388f, 32f, 392f, 240f), false, V3(0f, 1f, 0f)),
    )

    /** Las bandas de roca de la cara norte: tramos tendidos y algo caídos hacia la derecha, de distinto largo. */
    private val BANDAS = listOf(
        pts(150f, 70f, 166f, 72f, 182f, 75f), pts(200f, 52f, 214f, 56f, 230f, 62f), pts(204f, 84f, 222f, 88f, 240f, 95f, 256f, 102f),
        pts(170f, 110f, 186f, 112f, 200f, 116f), pts(232f, 118f, 248f, 122f, 262f, 124f), pts(112f, 92f, 128f, 94f, 140f, 99f),
        pts(40f, 100f, 56f, 104f, 70f, 108f), pts(276f, 98f, 290f, 103f), pts(332f, 96f, 346f, 92f, 358f, 86f),
    )

    fun DrawScope.cumbres(v: VistaAntiguas) {
        val p = v.p; val sol = v.sol; val l = v.l
        val az = fijo("cumbres")
        val roca = lerp(lerp(C.ROCA, C.ESCORIA, 0.2f), C.TIERRA, 0.12f)
        val forma = curva(CRESTA, 240f)
        val lejos = 0.26f
        // El relleno: la roca gris, que abajo se calienta y se pierde en la bruma del valle.
        drawPath(forma, Brush.verticalGradient(0f to p.medio(roca, lejos), 0.6f to p.medio(lerp(roca, C.TIERRA, 0.2f), lejos * 0.8f), 1f to lerp(p.medio(lerp(roca, C.PINO, 0.35f), lejos * 0.6f), l.bruma, 0.2f), startY = 24f, endY = 215f))
        val rotas = LINEAS.map { li -> if (li.puntos.size > 2) rota(li.puntos, az, 1.3f, 2) else li.puntos }
        clipPath(forma) {
            caras(v, rotas, roca, lejos)
            estrias(v, rotas, roca, lejos, az)
            bandas(v, roca, lejos, az)
            canales(v, rotas, roca, lejos)
            pieDeLaCara(v, roca, az)
            nieve(v, rotas, lejos, az)
            refugioDeLaCabra(v)
            alud(v)
            // La bruma del valle al pie: despega las cumbres de las laderas de delante.
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(l.bruma, l.bajo, 0.4f).copy(alpha = 0.32f * (1f - 0.5f * l.noche)), startY = 140f, endY = 208f), Offset(0f, 140f), Size(ANCHO, 70f))
        }
        // La cresta recortada contra el cielo: un filo de luz o de luna.
        val filo = if (l.noche > 0.4f) C.LUNA.copy(alpha = 0.45f * v.luna + 0.08f) else p.luz(lerp(roca, C.NIEVE, 0.6f), lejos).copy(alpha = 0.55f)
        drawPath(curva(CRESTA), filo, style = Stroke(0.8f))
        sombrero(v)
    }

    /** Cada cara entre dos líneas, con su luz: la que da la espalda al sol se va a la sombra azul. */
    private fun DrawScope.caras(v: VistaAntiguas, rotas: List<List<Offset>>, roca: Color, lejos: Float) {
        val p = v.p
        for (i in 0 until rotas.size - 1) {
            val a = larga(rotas[i]); val b = larga(rotas[i + 1])
            val arriba = CRESTA.filter { it.x > a.first().x && it.x < b.first().x }
            val cara = poligono(a + b.reversed() + arriba.reversed())
            val t = ((v.sol.t(LINEAS[i].cara) - 0.25f) * 2f).coerceIn(-1f, 1f)
            val claro = if (LINEAS[i].espolon) 0.3f else -0.3f
            drawPath(cara, Brush.linearGradient(0f to p.por(roca, t + claro, lejos), 1f to p.por(roca, t - claro, lejos), start = en(a, 0.3f), end = en(b, 0.3f)))
        }
    }

    private fun larga(r: List<Offset>): List<Offset> { val d = r.last() - r[r.size - 2]; return r + (r.last() + d / d.getDistance().coerceAtLeast(0.01f) * 120f) }

    /**
     * Las estrías de la roca: las líneas de caída de cada cara (las que deja el agua y la piedra que baja), trazos finos
     * entre una arista y la siguiente, oscuros y con huecos; con su filo de luz del lado del sol. Dan a la roca su materia.
     */
    private fun DrawScope.estrias(v: VistaAntiguas, rotas: List<List<Offset>>, roca: Color, lejos: Float, az: io.github.ruddy2003.vestigium.vidas.Azar) {
        val p = v.p; val oscura = p.sombra(lerp(roca, C.ESCORIA, 0.35f), lejos); val clara = p.luz(lerp(roca, C.NIEVE, 0.2f), lejos)
        val lado = if (p.solALaDerecha) 0.6f else -0.6f
        for (i in 0 until rotas.size - 1) {
            val a = rotas[i]; val b = rotas[i + 1]
            val n = ((b.first().x - a.first().x) / 5f).toInt().coerceIn(2, 12)
            for (k in 1 until n) {
                val f = k / n.toFloat() + az.entre(-0.04f, 0.04f)
                val t0 = az.entre(0.02f, 0.3f); val t1 = (t0 + az.entre(0.25f, 0.6f)).coerceAtMost(1f)
                val linea = (0..5).map { j -> val t = t0 + (t1 - t0) * j / 5f; en(a, t) + (en(b, t) - en(a, t)) * f + Offset(az.entre(-0.8f, 0.8f), 0f) }
                drawPath(curva(linea), oscura.copy(alpha = 0.42f), style = Stroke(az.entre(0.5f, 1.1f), cap = StrokeCap.Round))
                drawPath(curva(linea.map { it + Offset(lado, -0.2f) }), clara.copy(alpha = 0.22f * p.r.dia), style = Stroke(0.4f, cap = StrokeCap.Round))
            }
        }
    }

    /** El pie de la cara norte, entre la ladera de las vetas y la del paso: canchales grises y un bosque oscuro de pinos que sube por la garganta. */
    private fun DrawScope.pieDeLaCara(v: VistaAntiguas, roca: Color, az: io.github.ruddy2003.vestigium.vidas.Azar) {
        val p = v.p
        val canchal = TrazosValle.cerrada(pts(150f, 150f, 176f, 134f, 204f, 140f, 232f, 132f, 254f, 140f, 250f, 170f, 214f, 196f, 170f, 206f, 140f, 190f))
        drawPath(canchal, p.por(lerp(roca, C.NIEVE, 0.18f), 0.1f, 0.2f).copy(alpha = 0.75f))
        clipPath(canchal) { drawPoints((0 until 160).map { Offset(az.entre(140f, 256f), az.entre(132f, 206f)) }, PointMode.Points, p.sombra(roca, 0.2f).copy(alpha = 0.55f), 0.9f, StrokeCap.Round) }
        // El bosque de la garganta: pinos oscuros, en masa, con la punta de cada uno.
        val verde = lerp(C.PINO, C.ESCORIA, 0.15f)
        repeat(70) {
            val x = az.entre(150f, 240f); val y = az.entre(160f, 210f) + (x - 150f) * 0.05f
            val h = 4f + (y - 150f) * 0.09f
            drawPath(poligono(Offset(x, y - h), Offset(x + h * 0.32f, y), Offset(x - h * 0.32f, y)), p.por(verde, if (az.real() < 0.5f) 0.1f else -0.3f, 0.2f))
        }
    }

    /** Las bandas de roca: cada una, una repisa clara arriba y el escarpe oscuro debajo. */
    private fun DrawScope.bandas(v: VistaAntiguas, roca: Color, lejos: Float, az: io.github.ruddy2003.vestigium.vidas.Azar) {
        val p = v.p
        for (b in BANDAS) {
            val r = rota(b, az, 0.8f, 3)
            val alto = az.entre(2.4f, 4.6f)
            drawPath(poligono(r + r.reversed().map { it + Offset(az.entre(-0.6f, 0.6f), alto) }), p.sombra(lerp(roca, C.ESCORIA, 0.25f), lejos).copy(alpha = 0.55f))
            drawPath(curva(r), p.luz(lerp(roca, C.NIEVE, 0.25f), lejos).copy(alpha = 0.45f), style = Stroke(0.7f))
        }
        // Grietas y piedras: trazos cortos caídos por la cara.
        val trazos = ArrayList<Offset>()
        repeat(240) { val x = az.entre(20f, 372f); val y0 = alturaEn(CRESTA, x) + 4f; val y = az.entre(y0, 196f); trazos += Offset(x, y) }
        drawPoints(trazos, PointMode.Points, p.sombra(lerp(roca, C.ESCORIA, 0.4f), lejos).copy(alpha = 0.45f), 0.9f, StrokeCap.Round)
        drawPoints(trazos.map { it + Offset(if (p.solALaDerecha) 0.5f else -0.5f, -0.4f) }, PointMode.Points, p.luz(roca, lejos).copy(alpha = 0.35f * p.r.dia), 0.7f, StrokeCap.Round)
    }

    /** Las canales: el pliegue hondo, en sombra, con una rama que se abre abajo. */
    private fun DrawScope.canales(v: VistaAntiguas, rotas: List<List<Offset>>, roca: Color, lejos: Float) {
        val p = v.p; val hacia = if (p.solALaDerecha) -1f else 1f
        for ((i, li) in LINEAS.withIndex()) {
            if (li.espolon || li.puntos.size < 3) continue
            val r = rotas[i]
            val pliegue = poligono(r + r.reversed().mapIndexed { k, q -> q + Offset(hacia * 2.4f * (0.4f + 0.6f * (r.size - 1 - k) / r.size), 1f) })
            drawPath(pliegue, p.sombra(lerp(roca, C.ESCORIA, 0.3f), lejos).copy(alpha = 0.75f))
        }
        for ((i, li) in LINEAS.withIndex()) {
            if (!li.espolon || li.puntos.size < 3) continue
            val r = rotas[i]
            drawPath(Path().apply { moveTo(r[0]); for (q in r.drop(1)) lineTo(q) }, p.luz(lerp(roca, C.NIEVE, 0.35f), lejos).copy(alpha = 0.25f + 0.35f * p.r.dia), style = Stroke(0.9f, cap = StrokeCap.Round))
        }
    }

    /**
     * La nieve de las cumbres ([VistaAntiguas.nieveArriba]): una capa blanda en lo alto que baja más por las canales y menos
     * por los espolones, con su lado en sombra; cintas de nieve dentro de las canales, más largas cuanta más nieve; manchas
     * en las repisas de las bandas. De noche, la luna la platea.
     */
    private fun DrawScope.nieve(v: VistaAntiguas, rotas: List<List<Offset>>, lejos: Float, az: io.github.ruddy2003.vestigium.vidas.Azar) {
        val n = v.nieveArriba; val p = v.p; val sol = v.sol
        if (n <= 0.02f) return
        // La capa: baja de la cresta un trecho que crece con la nieve y con la altura de la cresta (lo alto, más).
        val baja = CRESTA.map { q -> val alto = ((200f - q.y) / 176f).coerceIn(0f, 1f); q + Offset(0f, (6f + 70f * n * n) * (0.3f + 0.7f * alto) * (0.8f + 0.28f * sin(q.x * 0.19f) + 0.12f * sin(q.x * 0.53f))) }
        val capa = curva(CRESTA.map { it + Offset(0f, -2f) }).apply { for (q in baja.reversed()) lineTo(q); close() }
        clipPath(capa) {
            fun larga(r: List<Offset>): List<Offset> { val d = r.last() - r[r.size - 2]; return r + (r.last() + d / d.getDistance().coerceAtLeast(0.01f) * 120f) }
            for (i in 0 until rotas.size - 1) {
                val a = larga(rotas[i]); val b = larga(rotas[i + 1])
                val arriba = CRESTA.filter { it.x > a.first().x && it.x < b.first().x }
                val t = ((sol.t(LINEAS[i].cara) - 0.3f) * 1.6f).coerceIn(-1f, 1f)
                drawPath(poligono(a + b.reversed() + arriba.reversed()), p.por(C.NIEVE, t + 0.2f, lejos * 0.8f))
            }
            // Los espolones asoman negros en la nieve.
            for ((i, li) in LINEAS.withIndex()) if (li.espolon && li.puntos.size > 2) drawPath(curva(rotas[i]), p.sombra(lerp(C.ROCA, C.ESCORIA, 0.3f), lejos).copy(alpha = 0.6f * (1f - 0.4f * n)), style = Stroke(1.6f, cap = StrokeCap.Round))
        }
        drawPath(curva(baja), p.sombra(C.NIEVE_SOMBRA, lejos).copy(alpha = 0.35f), style = Stroke(0.6f))
        // Las cintas de nieve en las canales.
        val hacia = if (p.solALaDerecha) -1f else 1f
        for ((i, li) in LINEAS.withIndex()) {
            if (li.espolon || li.puntos.size < 3) continue
            val r = rotas[i]; val f1 = (0.35f + 0.6f * n * li.nieve).coerceAtMost(0.98f)
            val eje = (0..7).map { k -> en(r, 0.04f + (f1 - 0.04f) * k / 7f) + Offset(hacia * 0.6f, 0.4f) }
            val anchos = (0..7).map { k -> (1f + 1.6f * n) * li.nieve * (0.5f + 0.7f * k / 7f) * (0.75f + 0.35f * sin(k * 1.7f + i)) }
            drawPath(cinta(eje, anchos), p.por(C.NIEVE, sol.t(V3(-hacia * 0.3f, 0.6f, -0.75f)) + 0.1f, lejos))
        }
        // Las repisas de las bandas, con su nieve.
        for (b in BANDAS) for (q in b) if (az.real() < 0.3f + 0.6f * n) drawPath(borron(q + Offset(0f, -0.6f), az.entre(2f, 5f) * (0.5f + n), 0.9f, az, 7, 0.35f), p.por(C.NIEVE, 0.4f, lejos).copy(alpha = (0.5f + 0.5f * n).coerceAtMost(1f)))
        // De noche, la luna platea la nieve de lo alto (la noche a dos luces: el frío arriba).
        if (v.luna > 0.02f) clipPath(capa) { drawRect(Brush.horizontalGradient(listOf(C.LUNA.copy(alpha = 0.06f * v.luna), C.LUNA.copy(alpha = 0.26f * v.luna)), 120f, 380f), Offset(0f, 0f), Size(ANCHO, 200f), blendMode = BlendMode.Plus) }
    }

    /**
     * El refugio de la Cabra, «colgado de la cara norte como un nido de golondrina»: una caseta de piedra pegada a la roca
     * en una repisa, con su tejado de una sola agua y su puerta oscura. Hasta el alud de 2028.
     */
    private fun DrawScope.refugioDeLaCabra(v: VistaAntiguas) {
        val q = CABRA; val p = v.p; val lejos = 0.36f
        if (!EscenaAntiguas.refugioDeLaCabra(v.m)) {
            // Donde estuvo: la repisa vacía y unas piedras sueltas.
            drawPath(poligono(q + Offset(-6f, 1.5f), q + Offset(6f, 0.5f), q + Offset(5f, 2.6f), q + Offset(-5f, 3.4f)), p.por(C.PIEDRA, 0.3f, lejos))
            repeat(4) { drawCircle(p.por(C.PIEDRA, -0.2f, lejos), 0.6f, q + Offset(-4f + it * 2.5f, 1f + (it % 2) * 0.6f)) }
            return
        }
        drawPath(poligono(q + Offset(-7f, 2f), q + Offset(7f, 0.6f), q + Offset(6f, 3.2f), q + Offset(-6f, 4.2f)), p.por(C.PIEDRA, 0.2f, lejos))
        val frente = poligono(q + Offset(-4.2f, 1.6f), q + Offset(2.4f, 1.2f), q + Offset(2.4f, -3.6f), q + Offset(-4.2f, -3.2f))
        val lado = poligono(q + Offset(2.4f, 1.2f), q + Offset(4.4f, 0.4f), q + Offset(4.4f, -3.8f), q + Offset(2.4f, -3.6f))
        drawPath(frente, p.por(C.PIEDRA, v.sol.t(V3(-0.1f, 0f, -1f)), lejos))
        drawPath(lado, p.por(C.PIEDRA, v.sol.t(V3(1f, 0f, -0.2f)), lejos))
        drawPath(poligono(q + Offset(-5f, -3f), q + Offset(5f, -4.4f), q + Offset(4.4f, -6.2f), q + Offset(-4f, -5f)), p.por(C.LOSA, v.sol.t(V3(0f, 0.8f, -0.5f)), lejos))
        drawRect(p.medio(C.ESCORIA, lejos), q + Offset(-2.4f, -1.8f), Size(1.6f, 3f))
        if (v.nieveArriba > 0.3f) drawPath(poligono(q + Offset(-5.4f, -4.6f), q + Offset(5.2f, -6f), q + Offset(4.6f, -7f), q + Offset(-4.6f, -5.6f)), p.por(C.NIEVE, 0.5f, lejos))
    }

    /** El día del alud: la lengua de nieve sucia que baja por la cara norte desde donde estuvo el refugio de la Cabra. */
    private fun DrawScope.alud(v: VistaAntiguas) {
        if (EscenaAntiguas.Senal.ALUD !in v.senales) return
        val p = v.p; val az = fijo("alud")
        val eje = pts(CABRA.x, CABRA.y + 2f, CABRA.x - 2f, 110f, CABRA.x - 8f, 140f, CABRA.x - 16f, 168f, CABRA.x - 22f, 190f)
        val anchos = listOf(3f, 6f, 9f, 13f, 18f)
        val lengua = cinta(eje, anchos)
        drawPath(lengua, p.por(lerp(C.NIEVE, C.PIEDRA, 0.18f), 0.3f, 0.3f))
        clipPath(lengua) {
            repeat(60) { val t = az.real(); val q = en(eje, t) + Offset(az.entre(-1f, 1f) * anchos[(t * 4).toInt().coerceAtMost(4)], 0f); drawCircle(p.por(C.PIEDRA, -0.3f, 0.3f).copy(alpha = 0.7f), az.entre(0.4f, 1.1f), q) }
            for (k in 0..3) drawPath(curva(eje.map { it + Offset((k - 1.5f) * 3f, 0f) }), p.sombra(C.NIEVE_SOMBRA, 0.3f).copy(alpha = 0.4f), style = Stroke(0.7f))
        }
    }

    /**
     * «Si [la cumbre] tiene una nube encima, como un sombrero, habrá que darse prisa»: algunos días, una nube lenticular
     * sentada en la cumbre alta, con su base plana y su lado de sombra.
     */
    private fun DrawScope.sombrero(v: VistaAntiguas) {
        if (v.clima != Clima.DESPEJADO && v.clima != Clima.VIENTO && v.clima != Clima.NUBES) return
        if (v.m.azar("sombrero").real() > 0.22f) return
        val l = v.l; val q = CUMBRE + Offset(4f, -5f)
        val claro = lerp(l.bajo, Color.White, 0.55f); val sombra = lerp(l.medio, lerp(C.SOMBRA, Color.White, 0.4f), 0.5f)
        val nube = TrazosValle.cerrada(listOf(q + Offset(-30f, 2f), q + Offset(-18f, -4.6f), q + Offset(0f, -7.4f), q + Offset(18f, -5.4f), q + Offset(32f, 1.6f), q + Offset(10f, 3.4f), q + Offset(-12f, 3.6f)))
        drawPath(nube, Brush.verticalGradient(listOf(lerp(claro, l.solColor, 0.25f * (1f - l.noche)).copy(alpha = 0.85f), sombra.copy(alpha = 0.85f)), q.y - 8f, q.y + 4f))
        mancha(q + Offset(0f, -8f), 22f, 2.4f, claro, 0.35f)
    }
}
