package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.en
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.ValleMongol.H
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * El suelo del valle y el río, la contradiagonal del cuadro: sale de detrás del pie del cerro, cerca del horizonte, hace
 * un meandro y se ensancha hasta salir por la derecha. La regla 4 (la superficie grande con profundidad) la cumplen los
 * dos: la nieve del valle, azulada lejos y cálida cerca, con sus rachas, sus ventisqueros y la sombra de cada cosa; y el
 * río, helado la mitad del año (hielo de varios azules, ventisqueros, grietas, el agujero del agua con su cubo) o
 * abierto, con sus reflejos y la luna encima.
 */
internal object ValleRio {
    private val C = ColorMongol

    /** El río por cortes, de lejos a cerca: cada corte es el punto de la orilla de acá y el de la de allá. Puestos a mano. */
    val CORTES: List<Pair<Offset, Offset>> = listOf(
        Offset(315f, 121.6f) to Offset(318f, 120.8f), Offset(319f, 122.6f) to Offset(325f, 121.6f), Offset(321f, 124f) to Offset(332f, 123f),
        Offset(317f, 125.8f) to Offset(335f, 125f), Offset(310f, 128f) to Offset(330f, 127.4f), Offset(304f, 130.8f) to Offset(322f, 130.2f),
        Offset(301f, 134.4f) to Offset(316f, 133.6f), Offset(302f, 138.6f) to Offset(317f, 137.4f), Offset(307f, 143.2f) to Offset(323f, 141.2f),
        Offset(314f, 148f) to Offset(332f, 145f), Offset(322f, 153.2f) to Offset(342f, 148.8f), Offset(331f, 158.8f) to Offset(352f, 152.6f),
        Offset(340f, 164.8f) to Offset(362f, 156.4f), Offset(349f, 171f) to Offset(371f, 160.2f), Offset(358f, 177.6f) to Offset(380f, 164f),
        Offset(366f, 184.2f) to Offset(388f, 167.6f), Offset(374f, 191f) to Offset(396f, 171f), Offset(382f, 198f) to Offset(404f, 174.4f),
        Offset(392f, 205.6f) to Offset(412f, 178f),
    )
    private val ACA = rota(CORTES.map { it.first }, fijo("orilla", 0), 0.7f, 2)
    private val ALLA = rota(CORTES.map { it.second }, fijo("orilla", 1), 0.6f, 2)

    /** El agujero del agua, junto a la orilla de acá, adonde llega la senda del campamento. */
    val AGUJERO = Offset(347f, 166.4f)

    /** La forma del río (entre las dos orillas). */
    fun forma(): Path = poligono(ACA + ALLA.reversed())

    // ---------------------------------------------------------------- El suelo del valle

    /** De qué color es el suelo del valle sin nieve: la hierba seca, con verde en las hondonadas cuando brota. */
    fun hierba(m: Momento): Color = lerp(lerp(C.ESTEPA, C.TIERRA, 0.28f), lerp(C.VERDE, C.ESTEPA, 0.3f), 0.75f * EscenaMongol.pasto(m))

    fun DrawScope.suelo(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val az = fijo("suelo")
        val seco = hierba(m); val blanco = C.NIEVE
        val manto = nieve > 0.62f
        val base = if (manto) blanco else seco
        val forma = poligono(listOf(Offset(-6f, 124f), Offset(300f, 120.6f)) + ACA + listOf(Offset(420f, ALTO + 4f), Offset(-6f, ALTO + 4f)))
        // La nieve refleja el cielo: lejos se va al azul; cerca, la luz cálida del sol.
        drawPath(forma, Brush.verticalGradient(0f to lerp(p.arriba(base, 0.4f), l.bajo, 0.25f), 0.3f to p.arriba(base, 0.22f), 0.7f to p.arriba(base, 0.08f), 1f to p.por(base, 0.2f), startY = H, endY = 225f))
        clipPath(forma) {
            // Lo que falta del otro material, a manchas que siguen el suelo: la nieve que queda en primavera, o la hierba que el viento destapa.
            val otro = if (manto) seco else blanco
            val cuantas = if (manto) ((1f - nieve) * 30f).toInt() + 4 else (nieve * 55f).toInt()
            repeat(cuantas) {
                val y = az.entre(H + 4f, 222f); val k = ((y - H) / 100f).coerceIn(0f, 1f); val c = Offset(az.entre(-10f, 400f), y)
                val forma2 = borron(c, az.entre(6f, 22f) * (0.4f + k), az.entre(1f, 3.4f) * (0.4f + k), az, 9, 0.38f)
                drawPath(forma2, p.arriba(otro, 0.3f * (1f - k)))
                if (!manto || it % 2 == 0) drawPath(forma2, p.sombra(otro, 0.3f * (1f - k)).copy(alpha = 0.4f), style = Stroke(0.4f + 0.5f * k))
            }
            if (nieve > 0.15f) {
                // Las rachas del viento en la nieve (sastrugi): lomitos con su cara de luz arriba y su sombra azul debajo.
                repeat(46) {
                    val y = az.entre(H + 5f, 222f); val k = ((y - H) / 100f).coerceIn(0f, 1f); val x = az.entre(-10f, 400f); val w = az.entre(5f, 16f) * (0.35f + k)
                    val c = Path().apply { moveTo(Offset(x, y)); cubicTo(Offset(x + w * 0.3f, y - 0.9f * (0.4f + k)), Offset(x + w * 0.7f, y - 0.6f * (0.4f + k)), Offset(x + w, y + 0.3f)) }
                    drawPath(c, p.sombra(C.NIEVE, 0.2f * (1f - k)).copy(alpha = 0.55f * nieve), style = Stroke(0.35f + 0.6f * k, cap = StrokeCap.Round))
                    drawPath(Path().apply { moveTo(Offset(x + 0.4f, y - 0.6f)); cubicTo(Offset(x + w * 0.3f, y - 1.4f * (0.4f + k)), Offset(x + w * 0.7f, y - 1.1f * (0.4f + k)), Offset(x + w - 0.6f, y - 0.3f)) }, p.luz(C.NIEVE).copy(alpha = 0.6f * nieve * (0.4f + 0.6f * p.r.dia)), style = Stroke(0.3f + 0.4f * k, cap = StrokeCap.Round))
                }
                // El día de la costra de hielo: la nieve dura y lisa, con brillos largos de cielo, como vidriada.
                if (ValleMongol.costra(m)) repeat(40) {
                    val y = az.entre(H + 5f, 224f); val k = ((y - H) / 100f).coerceIn(0f, 1f); val x = az.entre(-10f, 400f); val w = az.entre(6f, 22f) * (0.35f + k)
                    drawLine(lerp(p.l.medio, Color.White, 0.55f).copy(alpha = 0.55f), Offset(x, y), Offset(x + w, y + 0.2f), 0.4f + 0.7f * k, StrokeCap.Round)
                }
                // Los ventisqueros del pie del cerro: lomos blandos de nieve arrimada.
                for ((c, w) in listOf(Offset(268f, 150f) to 22f, Offset(294f, 136.4f) to 14f, Offset(214f, 186f) to 26f, Offset(166f, 206f) to 18f)) {
                    drawPath(borron(c, w, w * 0.16f, az, 10, 0.18f, 0.4f), p.sombra(C.NIEVE, 0.18f).copy(alpha = 0.5f * nieve))
                    drawPath(borron(c + Offset(0f, -w * 0.06f), w * 0.86f, w * 0.1f, az, 10, 0.18f, 0.4f), p.luz(C.NIEVE, 0.18f).copy(alpha = 0.75f * nieve))
                }
            }
            if (nieve < 0.62f) {
                // Las manchas de la hierba: rubia en los lomitos, parda y (cuando brota) verde en las hondonadas.
                val verde = lerp(C.VERDE, C.ESTEPA, 0.25f); val pasto = EscenaMongol.pasto(m)
                repeat(34) {
                    val y = az.entre(H + 4f, 222f); val k = ((y - H) / 100f).coerceIn(0f, 1f); val c = Offset(az.entre(-10f, 400f), y)
                    // El pasto que brota: verde primero en las hondonadas (las manchas de este tercio), cuando las lomas aún son rubias.
                    val tinta = when (it % 3) { 0 -> p.luz(lerp(seco, C.ESTEPA, 0.6f), 0.3f * (1f - k)); 1 -> p.sombra(lerp(seco, C.CUERO, 0.3f), 0.3f * (1f - k)); else -> p.por(lerp(seco, verde, (pasto * 2.5f).coerceAtMost(1f)), 0f, 0.3f * (1f - k)) }
                    drawPath(borron(c, az.entre(8f, 26f) * (0.4f + k), az.entre(1f, 3.4f) * (0.4f + k), az, 9, 0.38f), tinta.copy(alpha = if (it % 3 == 2) 0.45f + 0.35f * pasto else 0.45f))
                }
                // Las matas de hierba, con su bulto y su sombra corta: cada vez más grandes hacia quien mira.
                repeat(60) {
                    val y = az.entre(H + 8f, 222f); val k = ((y - H) / 100f).coerceIn(0f, 1f); val c = Offset(az.entre(-10f, 400f), y); val w = 0.6f + 2.4f * k
                    drawOval(p.r.color.copy(alpha = 0.45f * p.r.fuerza), Offset(c.x - w + p.r.ax.coerceIn(-2f, 2f) * w * 0.5f, c.y - w * 0.15f), Size(w * 2.4f, w * 0.45f), blendMode = BlendMode.Multiply)
                    drawPath(borron(c + Offset(0f, -w * 0.3f), w, w * 0.45f, az, 7, 0.3f, 0.9f), p.por(lerp(seco, C.CUERO, 0.2f), if (p.solALaDerecha) 0.2f else -0.2f, 0.3f * (1f - k)))
                }
                // La hierba seca, en pinceladas cortas, cada vez más grandes hacia quien mira; la escarcha de las mañanas.
                val escarcha = EscenaMongol.helada(m)
                if (escarcha) drawPath(forma, Brush.verticalGradient(0f to Color.Transparent, 0.4f to p.luz(C.NIEVE).copy(alpha = 0.22f), 1f to p.luz(C.NIEVE).copy(alpha = 0.3f), startY = H, endY = 225f))
                repeat(150) {
                    val y = az.entre(H + 5f, 222f); val k = ((y - H) / 100f).coerceIn(0f, 1f); val q = Offset(az.entre(-10f, 400f), y)
                    val c = p.por(lerp(seco, if (it % 3 == 0) C.CUERO else C.ESTEPA, 0.3f), if (it % 3 == 0) 0.6f else -0.4f, 0.3f * (1f - k))
                    drawLine(c, q, q + Offset(az.entre(-0.6f, 0.8f), -(0.8f + 2.8f * k)), 0.35f + 0.4f * k)
                    if (escarcha && it % 2 == 0) drawLine(p.luz(C.NIEVE, 0.3f * (1f - k)).copy(alpha = 0.6f), q + Offset(0f, -0.2f), q + Offset(az.entre(-0.4f, 0.4f), -(0.4f + 1.4f * k)), 0.3f + 0.3f * k)
                }
            }
        }
    }

    // ---------------------------------------------------------------- El río

    fun DrawScope.rio(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val az = fijo("rio"); val hielo = EscenaMongol.hielo(m)
        val forma = forma()
        // La franja de paso de cada orilla: grava y tierra oscura, más ancha cerca, con la nieve que se queda encima.
        val tierra = lerp(C.TIERRA, C.HOLLIN, 0.3f)
        for ((orilla, abajo) in listOf(ALLA to -1f, ACA to 1f)) {
            val banda = poligono(orilla.mapIndexed { i, q -> q } + orilla.reversed().mapIndexed { i, q -> val k = ((q.y - H) / 90f).coerceIn(0f, 1f); q + Offset(-abajo * 0.6f * k, abajo * (1.2f + 4.5f * k)) })
            drawPath(banda, p.por(tierra, if (abajo > 0f) -0.2f else 0.1f, 0.2f))
            if (nieve > 0.2f) drawPath(curva(orilla.map { q -> val k = ((q.y - H) / 90f).coerceIn(0f, 1f); q + Offset(0f, abajo * (2f + 3.6f * k)) }), p.luz(C.NIEVE, 0.2f).copy(alpha = 0.7f * nieve), style = Stroke(1.6f, cap = StrokeCap.Round))
        }
        // El agua honda y oscura lejos, más clara y verde cerca (siempre debajo: se ve en los agujeros y entre las placas).
        drawPath(forma, Brush.verticalGradient(0f to lerp(p.medio(C.RIO_HONDO, 0.5f), l.bajo, 0.45f), 0.35f to p.medio(C.RIO_HONDO, 0.25f), 1f to p.medio(lerp(C.RIO, C.RIO_HONDO, 0.3f), 0.05f), startY = H, endY = 206f))
        clipPath(forma) {
            if (hielo < 1f) agua(m, p, az, 1f - hielo)
            if (hielo > 0f) hielo(m, p, sol, az, hielo, nieve)
            // La luna o el sol bajo, en el hielo pulido o en el agua.
            estela(l, Rect(296f, H + 2f, 412f, 206f), m.azar("estela"))
        }
        // Las piedras y las matas secas de la orilla de acá: la franja de paso entre el río y la nieve del valle.
        for (i in 5 until CORTES.size) {
            val q = CORTES[i].first; val k = ((q.y - H) / 90f).coerceIn(0f, 1f)
            repeat(2) { j ->
                val c = q + Offset(az.entre(-3f, 3f) * (0.5f + k), 1f + az.entre(0.5f, 3f) * k); val rr = 0.7f + 2.2f * k * az.entre(0.6f, 1.2f)
                drawOval(p.por(C.PIEDRA, -0.5f, 0.15f), Offset(c.x - rr, c.y - rr * 0.5f), Size(rr * 2f, rr))
                drawOval(p.por(lerp(C.PIEDRA, C.NIEVE, nieve * 0.8f), 0.6f, 0.15f), Offset(c.x - rr * 0.8f + (if (p.solALaDerecha) rr * 0.2f else -rr * 0.2f), c.y - rr * 0.62f), Size(rr * 1.5f, rr * 0.55f))
            }
            if (i % 2 == 0) mata(p, q + Offset(az.entre(-4f, 2f), 2.5f + 3f * k), 2f + 5f * k, az, 0.15f)
        }
    }

    /** Una mata seca de la orilla (sauce enano sin hojas): varas rojizas que salen de un punto. */
    fun DrawScope.mata(p: Pincel, pie: Offset, alto: Float, az: Azar, lejos: Float) {
        val vara = lerp(C.CUERO, C.ROJO, 0.25f)
        for (j in 0..6) {
            val punta = pie + Offset(az.entre(-0.55f, 0.55f) * alto, -alto * az.entre(0.6f, 1.05f))
            drawLine(p.por(vara, if (j % 2 == 0) 0.4f else -0.5f, lejos), pie, punta, (0.06f * alto).coerceIn(0.3f, 0.9f), StrokeCap.Round)
        }
        drawOval(p.sombra(C.NIEVE, lejos).copy(alpha = 0.4f * p.r.fuerza), Offset(pie.x - alto * 0.5f, pie.y - alto * 0.06f), Size(alto, alto * 0.14f))
    }

    /** El agua abierta: refleja el cielo, la loma de enfrente y la orilla; rizos tendidos. */
    private fun DrawScope.agua(m: Momento, p: Pincel, az: Azar, k: Float) {
        val l = p.l
        // Las calmas reflejan el cielo; las rachas, el agua honda.
        drawRect(Brush.verticalGradient(0f to l.bajo.copy(alpha = 0.55f * k), 0.5f to l.medio.copy(alpha = 0.25f * k), 1f to Color.Transparent, startY = H, endY = 206f), Offset(296f, H), Size(120f, 90f))
        // La loma de la otra orilla, al revés, rota.
        reflejoRoto(312f, 412f, 136f, 26f, p.sombra(lerp(C.ESTEPA, C.TIERRA, 0.4f), 0.2f), 0.35f * k, az, 1.4f)
        repeat(70) {
            val i = az.entero(CORTES.size - 1); val t = az.real(); val a = CORTES[i]; val b = CORTES[i + 1]
            val q = (a.first + (a.second - a.first) * t) + ((b.first + (b.second - b.first) * t) - (a.first + (a.second - a.first) * t)) * az.real()
            val kk = ((q.y - H) / 88f).coerceIn(0f, 1f)
            drawLine(lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.4f * k), q, q + Offset(1.5f + 7f * kk, 0.6f * kk), 0.4f + 0.6f * kk, StrokeCap.Round)
        }
    }

    /**
     * El hielo: placas de varios azules (el hielo negro donde el viento lo barrió, el blanco donde se heló con nieve),
     * los ventisqueros que corren con el río, las grietas y el agujero del agua con su cubo de cuero. Con [hielo] menor
     * que 1, el hielo crece desde las orillas (noviembre) o se rompe en placas que bajan con el agua (el deshielo).
     */
    private fun DrawScope.hielo(m: Momento, p: Pincel, sol: Sol3, az: Azar, hielo: Float, nieve: Float) {
        val l = p.l; val deshielo = EscenaMongol.deshielo(m)
        val azul = lerp(C.HIELO, C.RIO, 0.35f); val negro = lerp(C.RIO_HONDO, C.HIELO, 0.25f); val blanco = lerp(C.HIELO, C.NIEVE, 0.5f)
        val centro = CORTES.map { (it.first + it.second) / 2f }
        // Dónde hay hielo: todo, o una banda desde cada orilla que llega hasta [hielo] del camino al medio.
        val placa = if (hielo >= 0.999f) forma() else Path().apply {
            for (orilla in listOf(CORTES.map { it.first }, CORTES.map { it.second })) addPath(poligono(orilla + orilla.indices.reversed().map { i -> orilla[i] + (centro[i] - orilla[i]) * (hielo * 0.9f + 0.1f * az.real()) }))
        }
        clipPath(placa) {
            drawPath(forma(), Brush.verticalGradient(0f to lerp(p.luz(azul, 0.5f), l.bajo, 0.45f), 0.4f to p.arriba(azul, 0.25f), 1f to p.por(lerp(azul, C.RIO_HONDO, 0.15f), -0.1f), startY = H, endY = 206f))
            // Las placas de hielo negro y de hielo blanco, alargadas con el río.
            for (i in 2 until CORTES.size - 1) repeat(2) { j ->
                val a = CORTES[i]; val b = CORTES[i + 1]; val t = az.entre(0.15f, 0.85f)
                val c = (a.first + (a.second - a.first) * t + b.first + (b.second - b.first) * t) / 2f; val w = (a.second - a.first).getDistance()
                val forma2 = borron(c, w * az.entre(0.2f, 0.4f) + 2f, w * 0.08f + 0.5f, az, 8, 0.35f)
                drawPath(forma2, if (j == 0) p.medio(negro, 0.2f).copy(alpha = 0.55f) else p.arriba(blanco, 0.2f).copy(alpha = 0.6f))
                if (j == 0 && w > 10f) repeat(5) { val q = c + Offset(az.entre(-w, w) * 0.2f, az.entre(-1f, 1f) * w * 0.05f); drawCircle(p.luz(C.NIEVE, 0.1f).copy(alpha = 0.5f), 0.25f + w * 0.012f, q) }   // burbujas en el hielo negro
            }
            // El cielo en el hielo pulido, en bandas a lo largo del río.
            for (i in 4 until CORTES.size - 1) { val c = (centro[i] + centro[i + 1]) / 2f; val w = (CORTES[i].second - CORTES[i].first).getDistance(); mancha(c + Offset(0f, -w * 0.06f), w * 0.55f, w * 0.06f + 0.6f, lerp(l.medio, Color.White, 0.3f), 0.32f * (1f - 0.5f * l.noche)) }
            // Los ventisqueros: lenguas de nieve que el viento tiende a lo largo del hielo, con su lado de sombra.
            repeat(9) {
                val i = 2 + az.entero(CORTES.size - 4); val t = az.entre(0.1f, 0.9f); val a = CORTES[i]; val b = CORTES[i + 1 + az.entero(2)]
                val q0 = a.first + (a.second - a.first) * t; val q1 = b.first + (b.second - b.first) * (t + az.entre(-0.15f, 0.15f)).coerceIn(0.05f, 0.95f)
                val w = (a.second - a.first).getDistance()
                val lengua = Path().apply { moveTo(q0); cubicTo(q0 + (q1 - q0) * 0.3f + Offset(0f, -w * 0.06f), q0 + (q1 - q0) * 0.7f + Offset(0f, -w * 0.05f), q1); cubicTo(q0 + (q1 - q0) * 0.7f + Offset(0f, w * 0.05f), q0 + (q1 - q0) * 0.3f + Offset(0f, w * 0.06f), q0); close() }
                drawPath(lengua, p.arriba(C.NIEVE, 0.15f).copy(alpha = 0.85f * (0.35f + 0.65f * nieve)))
                drawPath(Path().apply { moveTo(q0); cubicTo(q0 + (q1 - q0) * 0.3f + Offset(0f, w * 0.06f), q0 + (q1 - q0) * 0.7f + Offset(0f, w * 0.05f), q1) }, p.sombra(C.NIEVE, 0.15f).copy(alpha = 0.6f * (0.35f + 0.65f * nieve)), style = Stroke(0.4f + w * 0.02f))
            }
            // Las grietas: quebradas, que cruzan el río y se bifurcan, con un filo claro de un lado.
            repeat(6) {
                // Cada grieta arranca en un punto suelto del hielo y corre sesgada, río abajo, sin cruzarlo entero.
                val i = 5 + az.entero(CORTES.size - 6); val a = CORTES[i]; val b = CORTES[i + 1]; val w = (a.second - a.first).getDistance()
                val t0 = az.entre(0.1f, 0.6f); val q0 = a.first + (a.second - a.first) * t0; val q2 = b.first + (b.second - b.first) * (t0 + az.entre(0.2f, 0.4f))
                val linea = rota(listOf(q0, (q0 + q2) / 2f + Offset(az.entre(-2f, 2f), az.entre(-1f, 1f)), q2), az, w * 0.04f, 3)
                drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.sombra(C.RIO_HONDO, 0.1f).copy(alpha = 0.6f), style = Stroke(0.3f + w * 0.014f))
                drawPath(Path().apply { moveTo(linea[0] + Offset(0f, -0.5f)); for (q in linea.drop(1)) lineTo(q + Offset(0f, -0.5f)) }, p.luz(C.HIELO, 0.1f).copy(alpha = 0.5f), style = Stroke(0.25f + w * 0.008f))
                val rama = en(linea, az.entre(0.3f, 0.7f)); drawLine(p.sombra(C.RIO_HONDO, 0.1f).copy(alpha = 0.45f), rama, rama + Offset(az.entre(-1f, 1f) * w * 0.15f, w * 0.12f), 0.3f + w * 0.01f)
            }
        }
        if (hielo < 0.999f) {
            // El borde del hielo que crece o se rompe: un filo claro con su sombra en el agua.
            for (orilla in listOf(CORTES.map { it.first }, CORTES.map { it.second })) {
                val borde = orilla.indices.map { i -> orilla[i] + (centro[i] - orilla[i]) * hielo * 0.9f }
                drawPath(curva(borde), p.luz(blanco, 0.15f).copy(alpha = 0.8f), style = Stroke(0.7f))
                drawPath(curva(borde.map { it + Offset(0f, 0.8f) }), p.sombra(C.RIO_HONDO, 0.15f).copy(alpha = 0.5f), style = Stroke(0.6f))
            }
            // En el deshielo, las placas sueltas bajan por el canal abierto.
            if (deshielo) repeat(12) {
                val i = 3 + az.entero(CORTES.size - 4); val c = centro[i] + (centro[i + 1] - centro[i]) * az.real(); val w = (CORTES[i].second - CORTES[i].first).getDistance()
                val pl = borron(c, w * az.entre(0.08f, 0.2f) + 1f, w * 0.04f + 0.5f, az, 6, 0.4f, 0.8f)
                drawPath(pl, p.sombra(C.RIO_HONDO, 0.1f).copy(alpha = 0.5f), style = Stroke(0.8f)); drawPath(pl, p.arriba(blanco, 0.1f))
                drawPath(pl, p.sombra(blanco, 0.1f).copy(alpha = 0.6f), style = Stroke(0.4f))
            }
        }
        // El agujero del agua, picado en el hielo junto a la orilla de acá, con su cubo de cuero y el hielo roto alrededor.
        if (hielo > 0.6f) {
            val c = AGUJERO
            repeat(6) { val q = c + Offset(az.entre(-10f, 10f), az.entre(-1.6f, 2.2f)); drawPath(borron(q, az.entre(1f, 2.4f), az.entre(0.5f, 0.9f), az, 5, 0.3f), p.arriba(blanco, 0.1f)); drawPath(borron(q, az.entre(1f, 2.4f), az.entre(0.5f, 0.9f), az, 5, 0.3f), p.sombra(blanco, 0.1f).copy(alpha = 0.6f), style = Stroke(0.3f)) }
            drawOval(p.por(C.NIEVE, -0.4f, 0.1f), Offset(c.x - 7.5f, c.y - 2.4f), Size(15f, 5f))
            drawOval(p.medio(lerp(C.RIO_HONDO, C.HOLLIN, 0.4f), 0.05f), Offset(c.x - 5.6f, c.y - 1.6f), Size(11.2f, 3.4f))
            drawOval(lerp(p.medio(C.RIO, 0.05f), p.l.bajo, 0.35f).copy(alpha = 0.55f), Offset(c.x - 3.6f, c.y - 1.1f), Size(6.6f, 1.2f))
            val cubo = c + Offset(11.5f, 2f)
            drawOval(sol.r.color.copy(alpha = 0.5f * sol.r.fuerza), Offset(cubo.x - 3f + sol.r.ax.coerceIn(-3f, 3f) * 1.5f, cubo.y - 0.6f), Size(7f, 1.6f), blendMode = BlendMode.Multiply)
            drawPath(poligono(cubo + Offset(-2.8f, -5.6f), cubo + Offset(3f, -5.6f), cubo + Offset(2.4f, 0f), cubo + Offset(-2.3f, 0f)), Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.CUERO, 0.05f), p.luz(C.CUERO, 0.05f)) else listOf(p.luz(C.CUERO, 0.05f), p.sombra(C.CUERO, 0.05f)), cubo.x - 3f, cubo.x + 3f))
            drawOval(p.medio(C.HOLLIN, 0.05f), Offset(cubo.x - 3f, cubo.y - 6.5f), Size(6f, 1.8f)); drawOval(p.luz(C.HIELO, 0.05f).copy(alpha = 0.6f), Offset(cubo.x - 2.2f, cubo.y - 6.2f), Size(4.4f, 1f))
            drawLine(p.medio(C.CRIN, 0.05f), cubo + Offset(-2.8f, -5.6f), cubo + Offset(0f, -9.6f), 0.5f); drawLine(p.medio(C.CRIN, 0.05f), cubo + Offset(3f, -5.6f), cubo + Offset(0f, -9.6f), 0.5f)
            drawLine(p.sombra(C.CUERO, 0.05f).copy(alpha = 0.6f), cubo + Offset(-2.5f, -2.6f), cubo + Offset(2.6f, -2.6f), 0.4f)
        }
    }
}
