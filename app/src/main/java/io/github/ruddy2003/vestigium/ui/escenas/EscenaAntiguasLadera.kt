package io.github.ruddy2003.vestigium.ui.escenas

import io.github.ruddy2003.vestigium.vidas.Azar
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

    /**
     * Una arista (espolón, `true`) o una canal que baja de la cresta, y la normal de la cara que queda a su derecha. Las
     * caras se alternan: la que sigue a un espolón baja hacia la derecha (mira a la derecha) y la que sigue a una canal sube
     * hacia el espolón siguiente (mira a la izquierda): así la montaña se pliega y cada hora enciende unas y apaga otras.
     * [nieve]: cuánto junta la canal (sus neveros).
     */
    private class Linea(val puntos: List<Offset>, val espolon: Boolean, val cara: V3, val nieve: Float = 1f)

    private val DERECHA = V3(0.64f, 0.4f, -0.66f)
    private val IZQUIERDA = V3(-0.68f, 0.38f, -0.63f)
    private val FRENTE_DER = V3(0.32f, 0.42f, -0.85f)
    private val FRENTE_IZQ = V3(-0.36f, 0.42f, -0.83f)

    private val LINEAS = listOf(
        Linea(pts(-6f, 74f, -6f, 240f), false, FRENTE_IZQ),
        Linea(pts(60f, 50.6f, 64f, 76f, 69f, 104f, 74f, 134f), true, FRENTE_DER, 0.6f),
        Linea(pts(88f, 43.6f, 93f, 70f, 98f, 98f, 102f, 128f), false, IZQUIERDA, 1f),
        Linea(pts(124f, 27.2f, 127f, 54f, 130f, 82f, 133f, 110f, 136f, 138f), true, FRENTE_IZQ, 0.7f),
        Linea(pts(150f, 20.2f, 152f, 46f, 150f, 76f, 146f, 106f, 142f, 138f), true, DERECHA, 0.5f),
        Linea(pts(172f, 30.4f, 176f, 56f, 179f, 84f, 181f, 112f, 183f, 140f), false, IZQUIERDA, 1.3f),
        Linea(pts(190f, 39.8f, 195f, 62f, 199f, 88f, 202f, 114f), true, DERECHA, 0.6f),
        Linea(pts(212f, 44.2f, 216f, 64f, 221f, 88f, 225f, 112f), false, FRENTE_IZQ, 1.2f),
        Linea(pts(221f, 43.6f, 230f, 64f, 239f, 88f, 246f, 112f), true, DERECHA, 0.6f),
        Linea(pts(250f, 63.6f, 259f, 82f, 268f, 100f, 274f, 114f), false, IZQUIERDA, 1.1f),
        Linea(pts(266f, 75.6f, 276f, 92f, 284f, 106f, 289f, 113f), true, FRENTE_DER, 0.7f),
        Linea(pts(284f, 91.6f, 292f, 102f, 297f, 112f), false, IZQUIERDA, 0.9f),
        Linea(pts(305f, 108.6f, 306f, 118f), true, IZQUIERDA, 0.8f),
        Linea(pts(320f, 97.6f, 317f, 108f, 315f, 120f), false, DERECHA, 1.1f),
        Linea(pts(340f, 77f, 335f, 98f, 331f, 122f), true, IZQUIERDA, 0.7f),
        Linea(pts(368f, 57f, 360f, 84f, 354f, 112f), false, DERECHA, 1.1f),
        Linea(pts(388f, 48f, 392f, 240f), false, V3(0f, 1f, 0f)),
    )

    /**
     * Las bandas de roca: tramos tendidos que cruzan las caras, cada una con su repisa (donde se queda la nieve) y su
     * escarpe en sombra debajo; rotas a mano, de distinto largo, alguna partida en dos.
     */
    private val BANDAS = listOf(
        pts(100f, 64f, 112f, 67f, 124f, 70f), pts(130f, 72f, 140f, 75f), pts(156f, 50f, 166f, 54f, 178f, 59f), pts(168f, 86f, 182f, 90f, 196f, 95f),
        pts(204f, 99f, 216f, 103f, 226f, 108f), pts(206f, 60f, 220f, 66f, 234f, 73f), pts(240f, 86f, 252f, 92f, 262f, 98f), pts(110f, 98f, 124f, 101f, 138f, 106f),
        pts(26f, 84f, 42f, 87f, 56f, 91f), pts(68f, 62f, 82f, 64f, 92f, 68f), pts(256f, 104f, 268f, 109f), pts(322f, 100f, 334f, 96f, 344f, 90f),
        pts(150f, 112f, 162f, 116f, 172f, 120f),
    )

    fun DrawScope.cumbres(v: VistaAntiguas) {
        val p = v.p; val l = v.l
        val az = fijo("cumbres")
        val roca = lerp(lerp(C.ROCA, C.ESCORIA, 0.3f), C.TIERRA, 0.18f)
        val forma = curva(CRESTA, 240f)
        val lejos = 0.22f
        drawPath(forma, p.medio(roca, lejos))
        val rotas = LINEAS.map { li -> if (li.puntos.size > 2) rota(li.puntos, az, 1.6f, 2) else li.puntos }
        clipPath(forma) {
            caras(v, rotas, roca, lejos, az)
            bandas(v, roca, lejos, az)
            canales(v, rotas, roca, lejos)
            nieve(v, rotas, lejos, az)
            refugioDeLaCabra(v)
            // El aire entre la montaña y la ladera del paso: la bruma del valle sube por el pie y la despega.
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.5f to lerp(l.bruma, l.bajo, 0.35f).copy(alpha = 0.18f * (1f - 0.5f * l.noche)), 1f to lerp(l.bruma, l.bajo, 0.35f).copy(alpha = 0.26f * (1f - 0.5f * l.noche)), startY = 84f, endY = 150f), Offset(0f, 84f), Size(ANCHO, 156f))
            // La garganta, delante del aire: honda y en sombra, con su bosque.
            pieDeLaCara(v, roca, az)
        }
        // La cresta recortada contra el cielo: un filo de luz o de luna.
        val filo = if (l.noche > 0.4f) C.LUNA.copy(alpha = 0.45f * v.luna + 0.08f) else p.luz(lerp(roca, C.NIEVE, 0.6f), lejos).copy(alpha = 0.55f)
        drawPath(curva(CRESTA), filo, style = Stroke(0.8f))
        sombrero(v)
    }

    /** La luz de una cara con la normal [n], con el contraste estirado: la que da la espalda al sol se va de verdad a la sombra azul. */
    private fun luzDeCara(v: VistaAntiguas, n: V3): Float {
        // Media luz del sol de verdad y media de un sol que siempre viene algo de lado (del lado donde está), para que a
        // mediodía las paredes en sombra y las caras al sol no se igualen: el modelado no se pierde **[Diseño propio]**.
        val lado = if (v.r.lat >= 0f) 1f else -1f
        val lx = lado * maxOf(kotlin.math.abs(v.r.lat), 0.55f)
        val largo = kotlin.math.sqrt(lx * lx + 0.74f)
        val propia = ((n.x * lx + n.y * 0.7f - n.z * 0.5f) / largo - 0.55f) * 2.6f
        val real = (v.sol.t(n) - 0.22f) * 2.3f
        val fuerza = (v.r.fuerza / 0.72f).coerceIn(0f, 1f)
        return ((0.5f * real + 0.5f * propia) * (0.3f + 0.7f * fuerza)).coerceIn(-1f, 1f)
    }

    /** El color de la roca de una cara con luz [t]: al sol, cálida; en sombra, azul violeta (las paredes en sombra). */
    private fun colorDeCara(v: VistaAntiguas, roca: Color, t: Float, lejos: Float): Color {
        val p = v.p
        val base = p.por(lerp(roca, C.ORO, 0.1f * t.coerceAtLeast(0f)), if (t > 0f) t * 0.72f else t, lejos)
        return if (t >= 0f) base else lerp(base, p.sombra(lerp(roca, C.SOMBRA, 0.42f), lejos), (-t * 0.8f).coerceAtMost(0.75f))
    }

    /**
     * Las caras entre dos líneas, cada una con su luz, de la arista a la canal; por dentro, la roca con sus manchas (más
     * oscura en las paredes, con vetas de color) y las grietas cortas, rotas, que no son rayas iguales.
     */
    private fun DrawScope.caras(v: VistaAntiguas, rotas: List<List<Offset>>, roca: Color, lejos: Float, az: Azar) {
        val p = v.p
        for (i in 0 until rotas.size - 1) {
            val a = larga(rotas[i]); val b = larga(rotas[i + 1])
            val arriba = CRESTA.filter { it.x > a.first().x && it.x < b.first().x }
            val cara = poligono(a + b.reversed() + arriba.reversed())
            val t = luzDeCara(v, LINEAS[i].cara)
            // Junto a la arista, la cara más clara (le da más el aire); junto a la canal, más honda.
            val claro = if (LINEAS[i].espolon) 0.22f else -0.22f
            drawPath(cara, Brush.linearGradient(0f to colorDeCara(v, roca, t + claro, lejos), 1f to colorDeCara(v, roca, t - claro, lejos), start = en(a, 0.3f), end = en(b, 0.3f)))
            clipPath(cara) {
                // Manchas de roca más oscura y más rojiza, tendidas, a mano.
                repeat(5) {
                    val q = en(a, az.entre(0.15f, 0.85f)) + (en(b, 0.5f) - en(a, 0.5f)) * az.entre(0.2f, 0.8f)
                    drawPath(borron(q, az.entre(4f, 10f), az.entre(1.6f, 3.4f), az, 8, 0.4f), colorDeCara(v, lerp(roca, if (it % 2 == 0) C.ROJIZA else C.ESCORIA, 0.3f), t - 0.15f, lejos).copy(alpha = 0.45f))
                }
                // Grietas cortas y quebradas, sueltas: en sombra, con su filo de luz del lado del sol.
                val lado = if (p.solALaDerecha) 0.6f else -0.6f
                repeat(9) {
                    val f = az.entre(0.15f, 0.85f); val h = az.entre(0.1f, 0.9f)
                    val q = en(a, h) + (en(b, h) - en(a, h)) * f
                    val r = rota(listOf(q, q + Offset(az.entre(-2f, 2f), az.entre(5f, 11f))), az, 1.2f, 2)
                    drawPath(curva(r), p.sombra(lerp(roca, C.ESCORIA, 0.35f), lejos).copy(alpha = 0.5f), style = Stroke(az.entre(0.6f, 1.1f), cap = StrokeCap.Round))
                    drawPath(curva(r.map { it + Offset(lado, -0.2f) }), p.luz(lerp(roca, C.NIEVE, 0.2f), lejos).copy(alpha = 0.25f * p.r.dia), style = Stroke(0.45f, cap = StrokeCap.Round))
                }
            }
        }
    }

    private fun larga(r: List<Offset>): List<Offset> { val d = r.last() - r[r.size - 2]; return r + (r.last() + d / d.getDistance().coerceAtLeast(0.01f) * 120f) }

    /** El pie de la cara norte, en la garganta: un canchal gris y el bosque oscuro de pinos que sube por ella. */
    private fun DrawScope.pieDeLaCara(v: VistaAntiguas, roca: Color, az: Azar) {
        val p = v.p
        // La garganta: una cuña honda y en sombra entre las dos laderas, oscura abajo.
        val hondo = TrazosValle.cerrada(pts(150f, 150f, 162f, 132f, 176f, 136f, 186f, 160f, 180f, 214f, 150f, 214f))
        drawPath(hondo, Brush.verticalGradient(listOf(p.sombra(lerp(roca, C.SOMBRA, 0.3f), 0.18f).copy(alpha = 0.5f), p.sombra(lerp(C.PINO, C.ESCORIA, 0.4f), 0.12f)), 136f, 214f))
        val verde = lerp(C.PINO, C.ESCORIA, 0.15f)
        repeat(90) {
            val x = az.entre(148f, 186f); val y = az.entre(158f, 216f) + (x - 150f) * 0.05f
            val h = 4f + (y - 150f) * 0.09f
            drawPath(poligono(Offset(x, y - h), Offset(x + h * 0.32f, y), Offset(x - h * 0.32f, y)), p.por(verde, if (az.real() < 0.5f) 0.1f else -0.3f, 0.2f))
        }
    }

    /** Las bandas de roca: cada una, una repisa clara arriba (con su nieve) y el escarpe oscuro debajo, roto. */
    private fun DrawScope.bandas(v: VistaAntiguas, roca: Color, lejos: Float, az: Azar) {
        val pared = colorDeCara(v, roca, -0.6f, lejos)
        for (b in BANDAS) {
            val r = rota(b, az, 1f, 3)
            val alto = az.entre(2.6f, 5f)
            val abajo = r.reversed().map { it + Offset(az.entre(-0.8f, 0.8f), alto * az.entre(0.6f, 1.2f)) }
            drawPath(poligono(r + abajo), pared.copy(alpha = 0.8f))
            drawPath(curva(r), colorDeCara(v, roca, 0.7f, lejos).copy(alpha = 0.7f), style = Stroke(0.9f))
        }
    }

    /** Las canales: el pliegue hondo, en sombra del lado contrario al sol; los espolones, un filo de luz. */
    private fun DrawScope.canales(v: VistaAntiguas, rotas: List<List<Offset>>, roca: Color, lejos: Float) {
        val p = v.p; val hacia = if (p.solALaDerecha) -1f else 1f
        val hondo = colorDeCara(v, lerp(roca, C.ESCORIA, 0.3f), -0.8f, lejos)
        for ((i, li) in LINEAS.withIndex()) {
            if (li.espolon || li.puntos.size < 3) continue
            val r = rotas[i]
            val pliegue = poligono(r + r.reversed().mapIndexed { k, q -> q + Offset(hacia * 3f * (0.4f + 0.6f * (r.size - 1 - k) / r.size), 1f) })
            drawPath(pliegue, hondo.copy(alpha = 0.8f))
        }
        for ((i, li) in LINEAS.withIndex()) {
            if (!li.espolon || li.puntos.size < 3) continue
            val r = rotas[i]
            drawPath(Path().apply { moveTo(r[0]); for (q in r.drop(1)) lineTo(q) }, p.luz(lerp(roca, C.NIEVE, 0.35f), lejos).copy(alpha = 0.3f + 0.35f * p.r.dia), style = Stroke(1f, cap = StrokeCap.Round))
        }
    }

    /**
     * La nieve de las cumbres ([VistaAntiguas.nieveArriba], al menos 0.3): se queda donde la roca la deja: el casquete de
     * lo alto, que baja más por las caras que dan la espalda al sol y por las canales; los neveros de las canales, cintas que
     * se ensanchan hacia abajo y crecen con la nieve; las repisas de las bandas. Con mucha nieve, las aristas y los
     * escarpes de las bandas siguen asomando negros: la montaña no se vuelve un cono liso. De noche, la luna la platea.
     */
    private fun DrawScope.nieve(v: VistaAntiguas, rotas: List<List<Offset>>, lejos: Float, az: Azar) {
        val n = v.nieveArriba; val p = v.p
        if (n <= 0.02f) return
        // El casquete: de la cresta baja un trecho que crece con la nieve y con la altura, distinto en cada cara.
        val capas = ArrayList<Path>()
        for (i in 0 until rotas.size - 1) {
            val a = larga(rotas[i]); val b = larga(rotas[i + 1])
            val arriba = CRESTA.filter { it.x > a.first().x && it.x < b.first().x }
            val sombra = LINEAS[i].cara.x < 0f
            // Con poca nieve, el casquete queda en manchas: solo en algunas caras, y más en las de sombra.
            val queda = az.real() < 0.35f + 0.9f * n
            val baja = if (!queda) 0f else (2f + 60f * (n - 0.2f).coerceAtLeast(0f) * (0.4f + n)) * (if (sombra) 1.4f else 0.6f) * (0.7f + 0.6f * az.real())
            if (baja < 1.5f) continue
            val borde = (0..8).map { k -> val f = k / 8f; val q = a.first() + (b.first() - a.first()) * f; val y0 = alturaEn(CRESTA, q.x); Offset(q.x, y0 + baja * ((200f - y0) / 176f).coerceIn(0.2f, 1f) * (0.7f + 0.5f * sin(f * 3.1416f)) + az.entre(-2f, 2f)) }
            val capa = poligono(listOf(Offset(a.first().x, a.first().y - 3f)) + arriba + listOf(Offset(b.first().x, b.first().y - 3f)) + borde.reversed())
            val t = luzDeCara(v, LINEAS[i].cara)
            clipPath(poligono(a + b.reversed() + arriba.reversed())) { drawPath(capa, p.por(C.NIEVE, t + 0.15f, lejos * 0.8f)) }
            drawPath(curva(borde), p.sombra(C.NIEVE_SOMBRA, lejos).copy(alpha = 0.3f), style = Stroke(0.6f))
            capas += capa
        }
        // Los neveros de las canales: cintas que se ensanchan hacia abajo, más largas cuanta más nieve; partidas.
        val hacia = if (p.solALaDerecha) -1f else 1f
        for ((i, li) in LINEAS.withIndex()) {
            if (li.espolon || li.puntos.size < 3) continue
            val r = rotas[i]; val f1 = (0.3f + 0.6f * n * li.nieve).coerceAtMost(0.98f)
            for ((f0, fa) in listOf(0.04f to f1 * 0.55f, f1 * 0.62f to f1)) {
                if (fa - f0 < 0.06f) continue
                val eje = (0..6).map { k -> en(r, f0 + (fa - f0) * k / 6f) + Offset(hacia * 0.8f, 0.4f) }
                val anchos = (0..6).map { k -> (1.1f + 2f * n) * li.nieve * (0.45f + 0.7f * k / 6f) * (0.75f + 0.35f * sin(k * 1.7f + i)) }
                drawPath(cinta(eje, anchos), p.por(C.NIEVE, v.sol.t(V3(-hacia * 0.3f, 0.6f, -0.75f)) + 0.1f, lejos))
            }
        }
        // Las repisas de las bandas, con su nieve.
        for (b in BANDAS) for (q in b) if (az.real() < 0.35f + 0.6f * n) drawPath(borron(q + Offset(0f, -0.8f), az.entre(2.4f, 5.5f) * (0.5f + n), 1f, az, 7, 0.35f), p.por(C.NIEVE, 0.4f, lejos).copy(alpha = (0.55f + 0.45f * n).coerceAtMost(1f)))
        // Con mucha nieve, las aristas y los escarpes asoman negros: cintas de roca que la nieve no tapa.
        if (n > 0.45f) {
            val k = ((n - 0.45f) / 0.4f).coerceIn(0f, 1f)
            for ((i, li) in LINEAS.withIndex()) {
                if (!li.espolon || li.puntos.size < 3) continue
                val r = rotas[i]
                val eje = (0..6).map { j -> en(r, 0.05f + 0.9f * j / 6f) + Offset(-hacia * 1.2f, 0f) }
                val anchos = (0..6).map { j -> (0.8f + 2.2f * k) * (0.5f + 0.5f * sin(j * 1.3f + i)).coerceAtLeast(0.25f) }
                drawPath(cinta(eje, anchos), colorDeCara(v, C.ROCA, -0.5f, lejos).copy(alpha = 0.85f))
            }
            for (b in BANDAS) { val r = rota(b, az, 1f, 3); drawPath(curva(r.map { it + Offset(0f, 2.4f) }), colorDeCara(v, C.ROCA, -0.6f, lejos).copy(alpha = 0.75f * k), style = Stroke(2.4f, cap = StrokeCap.Round)) }
        }
        // De noche, la luna platea la nieve de lo alto (la noche a dos luces: el frío arriba).
        if (v.luna > 0.02f) for (c in capas) drawPath(c, C.LUNA.copy(alpha = 0.18f * v.luna), blendMode = BlendMode.Plus)
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
    fun DrawScope.alud(v: VistaAntiguas) {
        if (EscenaAntiguas.Senal.ALUD !in v.senales) return
        val p = v.p; val az = fijo("alud")
        val eje = pts(CABRA.x, CABRA.y + 2f, CABRA.x - 2f, 104f, CABRA.x - 7f, 124f, CABRA.x - 13f, 142f, CABRA.x - 19f, 160f)
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
