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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Colgado
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosMongol.sobreElSoporte
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.argal
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.carreta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.luzDePuerta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.odre
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.sombraDeCarreta
import io.github.ruddy2003.vestigium.ui.escenas.CampamentoVerano.montonDeLana
import io.github.ruddy2003.vestigium.ui.escenas.VeranoMongol.DeLaCuerda
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Postura
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.parejaMamando
import io.github.ruddy2003.vestigium.ui.escenas.LuzVerano.filoDeLuna
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano del campamento de verano ([VeranoMongol]) y el marco:
 * - [suelo]: el llano de este lado del río, del agua a quien mira: más verde y oscuro junto al río, pisado en el
 *   pasillo, en la cuerda de las yeguas y delante de la puerta; la hierba (a matas, más alta y más vencida cuanto más
 *   cerca) y las flores amarillas y moradas en manchas, según el pasto del día.
 * - [primerPlano]: la tienda de Bat con lo que cuelga de su pared y lo que se arrima a ella (los arreos, la soga, la
 *   silla en su caballete, el objeto grande del día), el odre con su mancha de airag, el fieltro de sentarse con el
 *   objeto del día, el montón de argal, las yeguas y los potros de la cuerda larga, y la carreta del marco.
 * - [marco]: la vara atada a la carreta y la cuerda de crin que cruza el cielo con lo que cuelga cada día.
 */
internal object CercaVerano {
    private val C = ColorMongol
    private const val H = VeranoMongol.H
    private val V = VeranoMongol

    // ---------------------------------------------------------------- El suelo

    /** La hierba, en trazos juntados por tono y por distancia: unos pocos caminos largos en vez de miles de líneas sueltas (pesa mucho menos). */
    private class Hierbas {
        val caminos = Array(9) { Path() }
        fun brizna(tono: Int, banda: Int, pie: Offset, punta: Offset, dobla: Offset) {
            val c = caminos[tono * 3 + banda]
            c.moveTo(pie); c.cubicTo(pie + (punta - pie) * 0.45f, punta + dobla * 0.5f - (punta - pie) * 0.15f, punta + dobla)
        }
    }

    fun DrawScope.suelo(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "verano", "suelo")); val verde = V.hierba(m)
        val pasto = EscenaMongol.pasto(m)
        val borde = LejosVerano.PLAYA
        val forma = poligono(borde.map { it + Offset(0f, -0.6f) } + listOf(Offset(-14f, ALTO + 2f), Offset(ANCHO + 14f, ALTO + 2f)))
        val rio = lerp(verde, C.MONTE, 0.28f); val seco = lerp(verde, C.ESTEPA, 0.45f)
        drawPath(forma, Brush.verticalGradient(0f to p.medio(rio, 0.3f), 0.12f to p.medio(lerp(rio, verde, 0.5f), 0.22f), 0.32f to p.luz(seco, 0.12f), 0.6f to p.medio(verde, 0.04f), 1f to p.por(lerp(verde, C.MONTE, 0.15f), -0.3f), startY = 140f, endY = ALTO))
        val pisado = lerp(lerp(verde, C.TIERRA, 0.55f), C.ESTEPA, 0.2f)
        clipPath(forma) {
            // Las ondulaciones del llano: una loma suave donde está el campamento y la hondonada más verde junto al río.
            mancha(Offset(80f, 152f), 140f, 6f, p.sombra(rio, 0.25f), 0.45f)
            mancha(Offset(260f, 147f), 120f, 4f, p.sombra(rio, 0.25f), 0.35f)
            mancha(Offset(150f, 176f), 130f, 10f, p.luz(seco, 0.1f), 0.35f * (0.4f + 0.6f * sol.r.dia))
            // Las rachas del viento: lomos claros que la vuelven del revés, y surcos oscuros entre ellos.
            repeat(18) {
                val c = Offset(az.entre(-20f, ANCHO + 20f), az.entre(150f, 280f)); val k = ((c.y - 150f) / 130f).coerceIn(0f, 1f)
                mancha(c, az.entre(30f, 70f) * (0.5f + k), 1.6f + 3.4f * k, if (it % 2 == 0) p.luz(lerp(seco, C.FIELTRO, 0.15f), 0.2f * (1f - k)) else p.sombra(lerp(verde, C.MONTE, 0.2f), 0.2f * (1f - k)), 0.24f)
            }
            // Lo pisado: el pasillo de la puerta al río, la franja de la cuerda de las yeguas, delante de la puerta y el campamento.
            val senda = listOf(Offset(252f, 252f), Offset(236f, 232f), Offset(222f, 212f), Offset(214f, 194f), Offset(206f, 176f), Offset(200f, 162f), Offset(196f, 150f))
            for (i in 0 until senda.size - 1) { val k = 1f - i / senda.size.toFloat(); drawLine(p.medio(pisado, 0.15f * (1f - k)).copy(alpha = 0.5f), senda[i], senda[i + 1], 3f + 13f * k, StrokeCap.Round) }
            drawLine(p.medio(pisado, 0.1f).copy(alpha = 0.42f), V.ESTACA + Offset(-14f, 2f), V.FONDO_CUERDA + Offset(-8f, 1f), 18f, StrokeCap.Round)
            mancha(Offset(212f, 252f), 52f, 13f, p.medio(pisado, 0f), 0.45f)
            mancha(Offset(120f, 178f), 60f, 9f, p.medio(pisado, 0.1f), 0.4f)
            // Las boñigas de los caballos a lo largo de la cuerda, cada una con su sombrita.
            repeat(16) {
                val t = az.real(); val q = V.ESTACA + (V.FONDO_CUERDA - V.ESTACA) * t + Offset(az.entre(-16f, 6f), az.entre(-2f, 4f)); val w = 1.6f * (1f - 0.55f * t)
                drawOval(p.sombra(C.HOLLIN, 0.1f).copy(alpha = 0.3f), Offset(q.x - w * 0.6f, q.y - w * 0.1f), Size(w * 2.2f, w * 0.6f))
                drawOval(p.por(C.ESTIERCOL, if (p.solALaDerecha) 0.2f else -0.2f, 0.1f), Offset(q.x - w, q.y - w * 0.5f), Size(w * 2f, w))
            }
            // La hierba: matas más altas y más juntas cerca, vencidas hacia donde sopla; donde se pisa, rala y corta.
            val hierbas = Hierbas(); val tumba = 1.4f + 4f * m.viento; val alta = 0.45f + 0.75f * pasto
            for (i in 0 until 820) {
                val u = az.real(); val q = Offset(az.entre(-6f, ANCHO + 6f), 145f + (ALTO - 140f) * u * (0.3f + 0.7f * u))
                if (q.y < LejosVerano.orillaDeAca(q.x) + 3f) continue
                val k = ((q.y - 140f) / (ALTO - 140f)).coerceIn(0f, 1f); val banda = if (k < 0.25f) 0 else if (k < 0.6f) 1 else 2
                val enPisado = (abs(q.x - (180f + (q.y - 230f) * -0.7f)) < 12f && q.y < 250f) || ((q - Offset(212f, 252f)).getDistance() < 30f)
                val h = (1.2f + 9f * k) * az.entre(0.6f, 1.4f) * alta * (if (enPisado) 0.4f else 1f)
                val n = 3 + az.entero(3)
                for (j in 0 until n) {
                    val t = j / (n - 1f) - 0.5f; val pie = Offset(q.x + t * 1.6f * k, q.y)
                    val punta = pie + Offset(t * 3f * (0.3f + k) + tumba * k * az.entre(0.3f, 1.1f) * 0.5f, -h * az.entre(0.6f, 1.1f))
                    hierbas.brizna(if (j % 3 == 0) 0 else if (j % 3 == 1) 2 else 1, banda, pie, punta, Offset(tumba * k * 0.5f, h * 0.06f))
                }
            }
            val tonos = listOf(lerp(seco, C.FIELTRO, 0.12f), verde, lerp(verde, C.MONTE, 0.22f))
            for (tono in 0 until 3) for (banda in 0 until 3) {
                val lejos = listOf(0.2f, 0.08f, 0f)[banda]
                val color = when (tono) { 0 -> p.luz(tonos[0], lejos); 1 -> p.medio(tonos[1], lejos); else -> p.sombra(tonos[2], lejos) }
                drawPath(hierbas.caminos[tono * 3 + banda], color.copy(alpha = listOf(0.55f, 0.75f, 0.9f)[banda]), style = Stroke(listOf(0.4f, 0.7f, 1.05f)[banda], cap = StrokeCap.Round))
            }
            // Las flores, en manchas: botones amarillos y ásteres morados, más hacia los bordes de lo pisado.
            flores(m, p, az)
            // Con lluvia, la tierra pisada se oscurece y se encharca: los charcos devuelven el cielo gris.
            if (V.rioTurbio(m)) charcos(p, az)
            // Las mañanas de los últimos días de septiembre, «las primeras heladas blanquean la hierba» (EscenaMongol.helada):
            // la escarcha cubre el llano, más espesa lejos de lo pisado, y se queda en la sombra.
            if (EscenaMongol.helada(m)) {
                drawRect(Brush.verticalGradient(listOf(p.luz(C.NIEVE, 0.2f).copy(alpha = 0.35f), p.medio(C.NIEVE_SOMBRA).copy(alpha = 0.45f)), 140f, ALTO), Offset(-14f, 140f), Size(ANCHO + 28f, ALTO - 138f))
                drawPoints((0 until 900).map { Offset(az.entre(-4f, ANCHO + 4f), az.entre(145f, ALTO)) }, PointMode.Points, p.luz(C.NIEVE).copy(alpha = 0.55f), 0.9f, StrokeCap.Round)
                mancha(Offset(212f, 252f), 50f, 12f, p.medio(lerp(C.TIERRA, C.ESTEPA, 0.3f)), 0.35f)
            }
        }
    }

    /** Los charcos de la lluvia en lo pisado: delante de la puerta, en el pasillo y junto a la cuerda; el borde de barro y el cielo reflejado. */
    private fun DrawScope.charcos(p: Pincel, az: Azar) {
        val l = p.l
        mancha(Offset(212f, 250f), 60f, 15f, p.sombra(lerp(C.TIERRA, C.HOLLIN, 0.3f)), 0.4f)
        mancha(Offset(196f, 200f), 30f, 30f, p.sombra(lerp(C.TIERRA, C.HOLLIN, 0.3f)), 0.3f)
        for ((c, w) in listOf(Offset(232f, 258f) to 16f, Offset(196f, 247f) to 9f, Offset(214f, 206f) to 7f, Offset(170f, 226f) to 8f, Offset(204f, 176f) to 5f, Offset(262f, 266f) to 11f)) {
            val h = w * (0.14f + (c.y - 150f) / 1400f)
            val borde = (0 until 12).map { j -> val a = j / 12f * 2f * PI.toFloat(); c + Offset(cos(a) * w * az.entre(0.8f, 1.1f), sin(a) * h * az.entre(0.8f, 1.1f)) }
            drawPath(poligono(borde.map { it + Offset(0f, 0.6f) }), p.sombra(lerp(C.TIERRA, C.HOLLIN, 0.4f)).copy(alpha = 0.7f))
            val barro = p.sombra(lerp(C.TIERRA, C.HOLLIN, 0.3f))
            drawPath(poligono(borde), Brush.verticalGradient(listOf(lerp(lerp(l.medio, l.alto, 0.4f), barro, 0.45f), lerp(lerp(l.bajo, l.medio, 0.3f), barro, 0.3f)), c.y - h, c.y + h))
            drawLine(lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.4f), c + Offset(-w * 0.5f, -h * 0.3f), c + Offset(w * 0.2f, -h * 0.3f), 0.5f)
        }
    }

    private fun DrawScope.flores(m: Momento, p: Pincel, az: Azar) {
        val cuantas = EscenaMongol.flores(m); if (cuantas <= 0.02f) return
        val amarilla = C.ORO; val morada = lerp(C.SOMBRA, C.ROJO, 0.32f)
        val manchas = listOf(
            Triple(Offset(36f, 270f), 30f, 0), Triple(Offset(110f, 278f), 26f, 1), Triple(Offset(300f, 272f), 34f, 0), Triple(Offset(346f, 248f), 22f, 1),
            Triple(Offset(168f, 186f), 22f, 0), Triple(Offset(244f, 170f), 18f, 1), Triple(Offset(64f, 196f), 20f, 1), Triple(Offset(132f, 156f), 26f, 0),
            Triple(Offset(222f, 150f), 20f, 0), Triple(Offset(270f, 262f), 16f, 1), Triple(Offset(150f, 246f), 14f, 0), Triple(Offset(18f, 240f), 18f, 0),
        )
        val petalos = Array(6) { ArrayList<Offset>() }; val centros = ArrayList<Offset>()
        for ((c, w, cual) in manchas) {
            val k = ((c.y - 140f) / 145f).coerceIn(0f, 1f); val banda = if (k < 0.4f) 0 else if (k < 0.75f) 1 else 2
            val n = (w * (0.6f + 0.8f * k) * cuantas).toInt()
            repeat(n) {
                val u = az.real(); val a = az.entre(0f, 6.28f)
                val q = c + Offset(cos(a) * w * u, sin(a) * w * u * (0.18f + 0.1f * k)) + Offset(0f, -az.entre(1f, 4f + 10f * k) * EscenaMongol.pasto(m))
                val otra = if (az.real() < 0.15f) 1 - cual else cual
                if (banda == 2) { val r = 0.9f; for (j in 0 until 4) { val b = j * 1.57f + a; petalos[otra * 3 + banda] += q + Offset(cos(b) * r, sin(b) * r * 0.7f) }; centros += q }
                else petalos[otra * 3 + banda] += q
            }
        }
        for (cual in 0 until 2) for (banda in 0 until 3) {
            val color = if (cual == 0) amarilla else morada
            drawPoints(petalos[cual * 3 + banda], PointMode.Points, p.luz(color, listOf(0.2f, 0.08f, 0f)[banda]), listOf(1.1f, 1.6f, 1.5f)[banda], StrokeCap.Round)
        }
        drawPoints(centros, PointMode.Points, p.medio(lerp(C.ORO, C.CUERO, 0.5f)), 0.9f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- El primer plano

    private val BAT = Ger(VeranoMongol.SITIO_BAT, Semilla.de("mongol", "tienda", "BAT"))

    /** Dónde toca el suelo la pared de la tienda de Bat a la altura [x] del lienzo (y hasta dónde sube), para lo que se arrima a ella. */
    private fun pared(g: Ger, x: Float): Pair<Float, Float> {
        val (a, b) = g.visible(); var mejor = a; var d = Float.MAX_VALUE
        for (i in 0..60) { val t = a + (b - a) * i / 60f; val q = g.pared(t, 0f); if (abs(q.x - x) < d) { d = abs(q.x - x); mejor = t } }
        return g.pared(mejor, 0f).y to g.pared(mejor, g.pared).y
    }

    /** El ángulo de la pared de la tienda que cae en la [x] del lienzo. */
    private fun angulo(g: Ger, x: Float): Float {
        val (a, b) = g.visible(); var mejor = a; var d = Float.MAX_VALUE
        for (i in 0..60) { val t = a + (b - a) * i / 60f; val q = g.pared(t, 0f); if (abs(q.x - x) < d) { d = abs(q.x - x); mejor = t } }
        return mejor
    }

    private fun alto(t: Float) = 36f * (1f - 0.6f * t)
    private fun enLaCuerda(t: Float) = V.ESTACA + (V.FONDO_CUERDA - V.ESTACA) * t
    private fun pieDe(d: DeLaCuerda): Offset = if (d.potro) enLaCuerda(d.t) + Offset(5f * (1f - d.t) + 2f, 1f) else enLaCuerda(d.t) + Offset(-12f * (1f - d.t) - 3f, 3f * (1f - d.t))
    private fun tallaDe(d: DeLaCuerda) = alto(d.t) * (if (d.potro) 0.62f else 1f) * d.talla

    /**
     * Un caballo de la cuerda con su postura (la de la pieza común). La yegua que tiene potro que mama se pinta con él
     * ([parejaMamando]): ella vuelve la cabeza hacia el potro. De noche, el filo cálido sale del lado de la puerta.
     */
    private fun DrawScope.caballoDeLaCuerda(p: Pincel, d: DeLaCuerda, cintas: Boolean, viento: Float, lumbreX: Float) {
        val pie = pieDe(d); val lejos = 0.12f * d.t
        val cria = d.cria
        if (cria != null) parejaMamando(p, pie, tallaDe(d), d.mira, d.pelo, cria, lejos, vuelve = d.postura == Postura.MIRANDO_ATRAS, viento = viento)
        else caballo(p, pie, tallaDe(d), d.mira, d.pelo, potro = d.potro, cintas = cintas && (!d.potro || d.blanco), lejos = lejos, postura = d.postura, viento = viento, lumbreX = lumbreX)
    }

    /** La silueta de un caballo de la cuerda (y la de su potro, si mama), para su sombra. */
    private fun siluetaDe(d: DeLaCuerda): Path {
        val pie = pieDe(d); val alto = tallaDe(d)
        val forma = PiezasMongol.siluetaDeCaballo(pie, alto, d.mira, postura = d.postura, potro = d.potro)
        if (d.cria != null) { val (pp, ap) = PiezasMongol.potroDe(pie, alto, d.mira); forma.addPath(PiezasMongol.siluetaDeCaballo(pp, ap, -d.mira, postura = Postura.MAMANDO, potro = true)) }
        return forma
    }

    fun DrawScope.primerPlano(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "verano", "cerca"))
        val dentro = EscenaMongol.fuegoDentro(m)
        val bat = Ger(VeranoMongol.SITIO_BAT, Semilla.de("mongol", "tienda", "BAT"), 0.35f, EscenaMongol.puerta(m), dentro, 0f, EscenaMongol.armado(m))
        val carreta = Carreta(VeranoMongol.SITIO_CARRETA, Semilla.de("mongol", "carreta", "verano"), true, EscenaMongol.cuna(m))
        // De noche la puerta abierta alumbra lo que tiene delante: cuánto, según lo lejos que esté.
        val puerta = bat.umbral + Offset(-6f, 4f)
        val abierta = bat.puerta != EscenaMongol.Puerta.CERRADA
        fun calida(q: Offset) = if (!abierta) 0f else (l.noche * maxOf(dentro, 0.5f) * (1f - (q - puerta).getDistance() / 110f)).coerceIn(0f, 0.8f)

        val yeguada = V.yeguada(m)
        val cintas = EscenaMongol.fiesta(m) || V.carrera(m) || EscenaMongol.diaDelOvoo(m)
        val negro = EscenaMongol.negroAtado(m); val forastero = V.caballosDeFuera(m) > 0
        val grande = ObjetosMongol.grande(m, p.con(calida(V.PIE_GRANDE)), V.PIE_GRANDE, V.APOYO_GRANDE)
        val (baseCab, _) = pared(bat, V.CABALLETE.x)
        val silla = V.sillaEnElCaballete(m); val hayCaballete = V.caballete(m)
        val lumbreX = puerta.x

        // ---- 1. Las sombras en el suelo: la tienda, la carreta, los caballos, el odre, el caballete, el objeto grande, la
        // cuerda con lo colgado y la vara. De día, las del sol; de noche, las de la luna, frías y flojas (la misma capa).
        val rs = LuzVerano.rayoDeSombras(l); val ss = Sol3(rs)
        capaSombra(rs) { c ->
            sombra(PiezasMongol.sombraDeGer(bat, ss), c)
            sombraDeCarreta(carreta, ss, c)
            for (d in yeguada) { val q = pieDe(d); sombraArrojada(rs, siluetaDe(d), q, c, 150f, 150f) }
            if (negro) sombraArrojada(rs, PiezasMongol.siluetaDeCaballo(V.NEGRO, 34f, 1f, postura = Postura.DESCANSO), V.NEGRO, c, 150f, 150f)
            if (forastero) sombraArrojada(rs, PiezasMongol.siluetaDeCaballo(V.FORASTERO, 33f, -1f), V.FORASTERO, c, 150f, 150f)
            if (EscenaMongol.odre(m)) sombraArrojada(rs, PiezasMongol.siluetaDeOdre(V.ODRE, 54f), V.ODRE, c, 150f, 150f)
            if (hayCaballete) sombraArrojada(rs, siluetaDeCaballete(V.CABALLETE, silla), V.CABALLETE, c, 150f, 150f)
            if (grande != null) sombraArrojada(rs, grande.forma, grande.pie, c, 150f, 150f)
            sombraDeLaCuerda(m, ss, c)
        }
        // La luz de la puerta abierta en la hierba.
        luzDePuerta(bat, maxOf(l.noche - 0.1f, 0f) * maxOf(bat.dentro, 0.6f), 5f)
        // La mancha de airag que rezuma del odre y los cubos, oscura y húmeda, con su brillo.
        if (EscenaMongol.odre(m)) {
            mancha(V.ODRE + Offset(-12f, 1.4f), 30f, 5.4f, p.sombra(lerp(C.TIERRA, C.CUERO, 0.5f)), 0.7f)
            mancha(V.ODRE + Offset(-16f, 1.8f), 12f, 1.8f, p.luz(C.FIELTRO), 0.3f * (1f - 0.7f * l.noche))
            drawPoints((0 until 14).map { V.ODRE + Offset(az.entre(-34f, 8f), az.entre(-1f, 4f)) }, PointMode.Points, p.luz(lerp(C.FIELTRO, C.ESTEPA, 0.2f)).copy(alpha = 0.7f), 1.1f, StrokeCap.Round)
        }

        // ---- 2. Las cosas, de atrás adelante.
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        // La cuerda de las yeguas: de estaca en estaca, baja, con sus nudos; con la cinta azul de la primera leche.
        cosas += parte(V.FONDO_CUERDA.y - 1f) { cuerdaDeLasYeguas(m, p) }
        // Los del fondo de la cuerda pueden quedar bajo la sombra de una nube: entonces, con el pincel de la sombra. De
        // noche, la luna les deja el filo plata en el lomo.
        val nubes = LuzVerano.sombrasDeNube(m)
        for (d in yeguada) cosas += parte(pieDe(d).y) { caballoDeLaCuerda(LuzVerano.enSombra(p.con(calida(pieDe(d))), LuzVerano.enSombraDeNube(nubes, pieDe(d))), d, cintas, m.viento, lumbreX); filoDeLuna(siluetaDe(d), l) }
        // El Negro, atado de noche junto a la puerta: en descanso, la cabeza baja y una pata doblada, alumbrado por la puerta.
        if (negro) cosas += parte(V.NEGRO.y) { caballo(p.con(calida(V.NEGRO) + 0.15f * l.noche), V.NEGRO, 34f, 1f, PiezasMongol.NEGRO, postura = Postura.DESCANSO, viento = m.viento, lumbreX = lumbreX); filoDeLuna(PiezasMongol.siluetaDeCaballo(V.NEGRO, 34f, 1f, postura = Postura.DESCANSO), l, 1.4f) }
        // El caballo de fuera (el del correo, el del que trae las noticias, el de un invitado), ensillado y atado a la carreta.
        if (forastero) cosas += parte(V.FORASTERO.y) {
            caballo(p.con(calida(V.FORASTERO)), V.FORASTERO, 33f, -1f, PiezasMongol.TORDO, silla = true, cintas = EscenaMongol.fiesta(m) || V.carrera(m), viento = m.viento, lumbreX = lumbreX)
            filoDeLuna(PiezasMongol.siluetaDeCaballo(V.FORASTERO, 33f, -1f), l, 1.4f)
            drawPath(Path().apply { moveTo(V.FORASTERO + Offset(-28f, -24f)); cubicTo(V.FORASTERO + Offset(-32f, -14f), V.FORASTERO + Offset(-38f, -18f), V.FORASTERO + Offset(-42f, -30f)) }, p.medio(C.CRIN), style = Stroke(0.8f))
        }
        // El ordeño del alba y la primera leche: el cubo de cuero lleno junto a la primera yegua; la cuchara de nueve agujeros.
        if (V.ordenio(m) || V.primeraLeche(m)) cosas += parte(229f) { cuboDeLeche(p, Offset(160f, 229f), V.primeraLeche(m)) }
        if (V.cuboVolcado(m)) cosas += parte(234f) { cuboVolcado(p, Offset(186f, 234f)) }
        if (V.primeraLeche(m)) cosas += parte(150f) { lecheEnLaHierba(p, az) }
        // La tienda de Bat, con su uso, y lo que cuelga de su pared.
        cosas += parte(214f) {
            ger(bat, p, sol)
            usoDeLaPared(p, bat, az, V.hierba(m))
            if (EscenaMongol.colgado(m) != Colgado.NADA) quesoEnElTecho(p, sol, bat, az)
            arreos(p.con(calida(Offset(284f, 220f))), sol, bat)
            if (EscenaMongol.liebre(m)) liebre(p.con(calida(Offset(278f, 200f))), Offset(278f, 186f))
        }
        // Las sombras sobre la pared: la de la silla y la del objeto grande, que se arriman a ella.
        cosas += parte(214.5f) {
            val (suelo, arriba) = pared(bat, V.PIE_GRANDE.x)
            capaSombra(rs, 0.85f) { c ->
                clipPath(PiezasMongol.silueta(bat)) {
                    if (grande != null) sombraArrojada(rs, grande.forma, grande.pie, c, suelo, arriba, 200f, ANCHO)
                    if (hayCaballete) sombraArrojada(rs, siluetaDeCaballete(V.CABALLETE, silla), V.CABALLETE, c, baseCab, arriba, 200f, ANCHO)
                }
            }
        }
        if (EscenaMongol.odre(m)) cosas += parte(V.ODRE.y) { odre(p.con(calida(V.ODRE)), V.ODRE, 54f, Azar(Semilla.de("mongol", "odre")), batiendo = V.batido(m)); filoDeLuna(PiezasMongol.siluetaDeOdre(V.ODRE, 54f), l, 1.6f) }
        // El día que llegan, mientras se arma la tienda, los rollos de fieltro esperan en la hierba donde irá el odre.
        if (V.llegada(m) && EscenaMongol.armado(m) < 1f) for ((c, largo) in listOf(Offset(226f, 246f) to 38f, Offset(258f, 252f) to 32f, Offset(240f, 238f) to 30f).sortedBy { it.first.y }) cosas += parte(c.y) { rolloDeFieltro(p, sol, c, largo, 6.4f) }
        if (grande != null) cosas += parte(grande.pie.y) { grande.pintar(this) }
        if (hayCaballete) cosas += parte(V.CABALLETE.y) { caballete(p.con(calida(V.CABALLETE)), sol, V.CABALLETE, silla); filoDeLuna(siluetaDeCaballete(V.CABALLETE, silla), l, 1.2f) }
        // El fieltro de sentarse, con el objeto del día encima; los días de objeto grande, la tabla del queso con su cuchara.
        cosas += parte(V.FIELTRO_OBJETO.y) {
            val pp = p.con(calida(V.FIELTRO_OBJETO))
            fieltroDeSentarse(pp, V.FIELTRO_OBJETO, V.hierba(m), Azar(Semilla.de("mongol", "fieltro", "sentarse")))
            if (ObjetosMongol.esGrande(m) || m.ficha.objeto == null) tablaDeQueso(pp, V.FIELTRO_OBJETO + Offset(2f, -1f))
            else with(ObjetosMongol) { sobreElSoporte(m, pp, V.FIELTRO_OBJETO + Offset(0f, -1f), 1.35f) }
        }
        if (V.pielParaDormir(m)) cosas += parte(262f) { pielDeOveja(p.con(calida(Offset(250f, 262f))), Offset(250f, 263f), az) }
        if (V.lanaEnElSuelo(m)) cosas += parte(262f) { montonDeLana(p, sol, Offset(132f, 262f), 24f, Azar(Semilla.de("mongol", "lana", "cerca")), 0f); tijeras(p, Offset(128f, 250f)) }
        cosas += parte(V.ARGAL.y) { argal(p.con(calida(V.ARGAL)), V.ARGAL, 58f, Azar(Semilla.de("mongol", "argal", "verano"))) }
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()

        // ---- 3. La carreta del marco, delante de todo lo de la izquierda.
        carreta(carreta, p.con(calida(Offset(110f, 230f)) * 0.7f), sol)

        // ---- 4. La artesa de los potros, delante de la rueda, cortada por los bordes; luego las matas altas de delante, con flores.
        artesa(p, sol)
        matasDeDelante(m, p, az)
    }

    /**
     * La artesa de madera donde beben los potros atados, delante de la rueda de la carreta, cortada por el borde de la
     * izquierda y el de abajo **[Diseño propio]**: el cuento lleva las ovejas a beber al río y no dice dónde beben los
     * potros. El agua devuelve el cielo; la madera, vieja y mojada abajo, tiene sus duelas y sus clavos.
     */
    private fun DrawScope.artesa(p: Pincel, sol: Sol3) {
        val l = p.l
        val lejos = listOf(Offset(-12f, 266f), Offset(70f, 259f)); val cerca = listOf(Offset(-8f, 278f), Offset(80f, 268.5f))
        val madera = lerp(C.MADERA, C.TIERRA, 0.35f)
        // La sombra en la hierba.
        drawPath(poligono(cerca[0] + Offset(0f, 12f), cerca[1] + Offset(0f, 12f), cerca[1] + Offset(sol.r.ax * 8f + 4f, 14f), cerca[0] + Offset(sol.r.ax * 8f, 16f)), sol.r.color.copy(alpha = 0.7f * sol.r.fuerza), blendMode = BlendMode.Multiply)
        // La cara de dentro de la duela de allá, el agua y el canto de arriba.
        val boca = poligono(lejos[0], lejos[1], cerca[1], cerca[0])
        drawPath(boca, p.sombra(lerp(madera, C.HOLLIN, 0.2f)))
        val agua = poligono(lejos[0] + Offset(2f, 3.4f), lejos[1] + Offset(-3f, 3f), cerca[1] + Offset(-3f, -1.4f), cerca[0] + Offset(2f, -1.2f))
        drawPath(agua, Brush.verticalGradient(listOf(lerp(l.alto, C.RIO_HONDO, 0.3f), lerp(l.medio, l.bajo, 0.4f)), 262f, 277f))
        drawLine(lerp(l.bajo, Color.White, 0.5f).copy(alpha = 0.6f * (1f - 0.5f * l.noche)), Offset(18f, 268f), Offset(50f, 265f), 0.8f, StrokeCap.Round)
        drawPath(poligono(lejos[0], lejos[1], lejos[1] + Offset(0f, 1.6f), lejos[0] + Offset(0f, 1.6f)), p.arriba(madera))
        // La duela de delante, con su luz, la humedad de abajo y los clavos.
        val frente = poligono(cerca[0], cerca[1], cerca[1] + Offset(0f, 13f), cerca[0] + Offset(0f, 13f))
        drawPath(frente, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.medio(madera), p.luz(madera)) else listOf(p.luz(madera), p.medio(madera)), -8f, 80f))
        clipPath(frente) {
            for (k in 1..2) drawLine(p.sombra(madera).copy(alpha = 0.6f), cerca[0] + Offset(0f, k * 4.4f), cerca[1] + Offset(0f, k * 4.4f), 0.5f)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(lerp(madera, C.HOLLIN, 0.3f)).copy(alpha = 0.7f)), 276f, 290f), Offset(-10f, 270f), Size(92f, 22f))
            for (x in listOf(10f, 40f, 70f)) for (k in 0..2) drawCircle(p.medio(C.HOLLIN), 0.5f, Offset(x, cerca[0].y + (cerca[1].y - cerca[0].y) * (x + 8f) / 88f + 2f + k * 4.4f))
        }
        drawPath(poligono(cerca[0] + Offset(0f, -0.8f), cerca[1] + Offset(0f, -0.8f), cerca[1] + Offset(0f, 0.8f), cerca[0] + Offset(0f, 0.8f)), p.luz(lerp(madera, C.FIELTRO, 0.2f)))
    }

    /** Un rollo de fieltro tendido en la hierba, en tres cuartos: el cuerpo con su luz, la cabeza con la espiral y las cuerdas de crin. */
    private fun DrawScope.rolloDeFieltro(p: Pincel, sol: Sol3, c: Offset, largo: Float, r: Float) {
        val inclina = -0.22f; val a = c + Offset(-largo / 2f, -largo / 2f * inclina); val b = c + Offset(largo / 2f, largo / 2f * inclina)
        drawOval(sol.r.color.copy(alpha = 0.5f * sol.r.fuerza), Offset(c.x - largo * 0.55f + sol.r.ax * r, c.y - r * 0.3f), Size(largo * 1.1f, r * 0.9f), blendMode = BlendMode.Multiply)
        val n = Offset(0f, -r)
        val cuerpo = poligono(a, b, b + n * 2f, a + n * 2f)
        drawPath(cuerpo, Brush.verticalGradient(listOf(p.luz(C.FIELTRO), p.medio(C.FIELTRO), p.sombra(C.FIELTRO)), c.y - 2f * r - largo * 0.12f, c.y + largo * 0.12f))
        for (u in listOf(0.22f, 0.78f)) { val q = a + (b - a) * u; drawLine(p.sombra(C.CRIN), q, q + n * 2f, 0.9f) }
        val cabeza = b + n
        drawOval(p.por(lerp(C.FIELTRO, C.TIERRA, 0.15f), if (p.solALaDerecha) 0.4f else -0.3f), Offset(cabeza.x - r * 0.55f, cabeza.y - r), Size(r * 1.1f, r * 2f))
        val espiral = (0..30).map { val t = it * 0.55f; val rr = r * (0.1f + 0.85f * it / 30f); cabeza + Offset(kotlin.math.cos(t) * rr * 0.5f, kotlin.math.sin(t) * rr) }
        drawPath(Path().apply { moveTo(espiral[0]); for (q in espiral.drop(1)) lineTo(q) }, p.sombra(C.FIELTRO).copy(alpha = 0.7f), style = Stroke(0.4f))
    }

    /** La cuerda larga de las yeguas: baja entre sus estacas y se ve cada nudo; la de la primera leche lleva su cinta azul. */
    private fun DrawScope.cuerdaDeLasYeguas(m: Momento, p: Pincel) {
        val linea = cuerda(V.ESTACA + Offset(0f, -7f), V.FONDO_CUERDA + Offset(0f, -3.4f), 3.2f, 18)
        drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.medio(C.CRIN), style = Stroke(1f))
        for ((t, alto) in listOf(0f to 11f, 0.5f to 7f, 1f to 4.4f)) {
            val q = enLaCuerda(t) + Offset(0f, 1f)
            drawLine(p.sombra(C.MADERA), q, q + Offset(0f, -alto), 2.2f * (1f - 0.5f * t), StrokeCap.Round)
            drawLine(p.luz(C.MADERA), q + Offset(if (p.solALaDerecha) 0.5f else -0.5f, 0f), q + Offset(if (p.solALaDerecha) 0.5f else -0.5f, -alto), 0.7f * (1f - 0.4f * t), StrokeCap.Round)
        }
        // Los ramales cortos con que se atan los potros.
        for (t in listOf(0.2f, 0.56f, 0.86f)) { val a = linea[(t * 18).toInt().coerceIn(0, 18)]; drawLine(p.medio(C.CRIN), a, a + Offset(5f * (1f - t) + 1f, 4f * (1f - t) + 1f), 0.6f) }
        if (V.primeraLeche(m)) { val q = V.ESTACA + Offset(0f, -10f); drawPath(Path().apply { moveTo(q); cubicTo(q + Offset(3f, 2f), q + Offset(3f + 3f * m.viento, 7f), q + Offset(2f + 4f * m.viento, 12f)) }, p.por(C.AZUL, 0.3f), style = Stroke(1.6f, cap = StrokeCap.Round)) }
    }

    /** La sombra de la cuerda de crin y de lo que cuelga de ella, tumbada en el llano: se mueve con la hora. */
    private fun DrawScope.sombraDeLaCuerda(m: Momento, sol: Sol3, c: Color) {
        val r = sol.r; val linea = lineaDeLaCuerda(m)
        fun enElSuelo(q: Offset, t: Float, baja: Float = 0f): Offset {
            val suelo = 250f + (218f - 250f) * t; val h = suelo - q.y - baja
            return Offset(q.x + r.ax * h, suelo + r.ay * h)
        }
        val n = linea.size - 1
        val sombraLinea = linea.mapIndexed { i, q -> enElSuelo(q, i / n.toFloat()) }
        clipRect(0f, 146f, ANCHO, ALTO) {
            // La vara, de pie junto a la carreta: su sombra sale de donde pisa y se tiende en el llano.
            val pieVara = Offset(V.VARA_PIE.x, 252f)
            fun deLaVara(q: Offset): Offset { val h = pieVara.y - q.y; return Offset(q.x + r.ax * h, pieVara.y + r.ay * h) }
            drawLine(c, deLaVara(V.VARA_PIE), deLaVara(V.VARA_PUNTA), 2.2f, StrokeCap.Round)
            drawPath(Path().apply { moveTo(sombraLinea[0]); for (q in sombraLinea.drop(1)) lineTo(q) }, c, style = Stroke(1.2f))
            for (cosa in colgados(m, linea)) { val q = enElSuelo(cosa.q, cosa.t, cosa.alto * 0.5f); drawOval(c, Offset(q.x - cosa.ancho * 0.5f, q.y - cosa.alto * 0.25f), Size(cosa.ancho, cosa.alto * 0.5f)) }
        }
    }

    /** El fieltro de sentarse, tendido en la hierba: grueso, con su ribete rojo, el pespunte y una esquina doblada. */
    /**
     * El fieltro de sentarse, tendido en la hierba delante de la puerta: grueso, de fieltro pardo claro acolchado, con el
     * ribete de fieltro rojo cosido alrededor y su pespunte claro; dentro, el dibujo acolchado de hilo oscuro (un rombo con
     * cuernos de carnero en las puntas y la greca del martillo por dentro del ribete) **[Aproximación]** a los fieltros de
     * sentarse acolchados; las arrugas y el rodal gastado del uso; la esquina de delante doblada, que enseña el envés y
     * el canto; y la hierba que le sube por los bordes.
     */
    private fun DrawScope.fieltroDeSentarse(p: Pincel, c: Offset, verde: Color, az: Azar) {
        val s = Sitio(VeranoMongol.MIRADA, c + Offset(0f, 1f), 60f, 0.12f)
        val w = 0.7f; val d = 0.47f; val alto = 0.045f; val doblez = 0.22f; val b = 0.085f
        fun q(x: Float, z: Float, y: Float = alto) = s.p(x, y, z)
        val tela = lerp(lerp(C.FIELTRO_VIEJO, C.ESTEPA, 0.25f), C.LANA, 0.2f)
        val ribete = lerp(C.ROJO, C.CUERO, 0.32f)
        val hilo = p.sombra(lerp(C.CRIN, C.CUERO, 0.3f)).copy(alpha = 0.7f)
        // La sombra en la hierba y el canto grueso, de delante y del lado derecho.
        drawPath(poligono(listOf(q(-w * 1.03f, -d * 1.03f, 0f), q(w * 1.05f, -d * 1.03f, 0f), q(w * 1.06f, d * 1.1f, 0f), q(-w * 1.03f, d * 1.1f, 0f))), p.sombra(C.TIERRA).copy(alpha = 0.5f))
        drawPath(poligono(listOf(q(-w, d), q(w - doblez, d), q(w - doblez, d, 0f), q(-w, d, 0f))), LuzVerano.pintado(p, ribete, -0.7f))
        drawPath(poligono(listOf(q(w, -d), q(w, d - doblez), q(w, d - doblez, 0f), q(w, -d, 0f))), LuzVerano.pintado(p, ribete, if (p.solALaDerecha) 0.3f else -0.5f))
        // La cara de arriba: el ribete rojo y, dentro, el campo acolchado.
        val cara = poligono(listOf(q(-w, -d), q(w, -d), q(w, d - doblez), q(w - doblez, d), q(-w, d)))
        drawPath(cara, Brush.verticalGradient(listOf(LuzVerano.pintado(p, ribete, 0.6f), LuzVerano.pintado(p, ribete, 0.3f)), q(0f, -d).y, q(0f, d).y))
        val campo = poligono(listOf(q(-w + b, -d + b), q(w - b, -d + b), q(w - b, d - doblez - b * 0.3f), q(w - doblez - b * 0.3f, d - b), q(-w + b, d - b)))
        drawPath(campo, Brush.verticalGradient(listOf(p.arriba(tela), p.medio(tela)), q(0f, -d).y, q(0f, d).y))
        clipPath(cara) {
            // El pespunte claro que cose el ribete, por dentro.
            val cosido = ArrayList<Offset>()
            for (k in 0..40) { val u = k / 40f; cosido += q(-w + b * 0.9f + (2f * w - 2f * b) * u, -d + b * 0.9f); cosido += q(-w + b * 0.9f + (2f * w - 2f * b - doblez) * u, d - b * 0.9f) }
            for (k in 0..24) { val u = k / 24f; cosido += q(-w + b * 0.9f, -d + b + (2f * d - 2f * b) * u); cosido += q(w - b * 0.9f, -d + b + (2f * d - 2f * b - doblez) * u) }
            drawPoints(cosido, PointMode.Points, p.luz(lerp(C.FIELTRO, C.ORO, 0.2f)).copy(alpha = 0.8f), 0.7f, StrokeCap.Round)
            // La greca del martillo, por dentro del ribete, delante y detrás.
            for (z0 in listOf(-d + b + 0.06f, d - b - 0.06f)) {
                val greca = Path(); var x = -w + b + 0.06f; var arriba = false; greca.moveTo(q(x, z0))
                while (x < w - b - 0.08f - (if (z0 > 0f) doblez else 0f)) { val z1 = if (arriba) z0 else z0 + (if (z0 < 0f) 0.05f else -0.05f); greca.lineTo(q(x, z1)); x += 0.06f; greca.lineTo(q(x, z1)); arriba = !arriba }
                drawPath(greca, hilo, style = Stroke(0.55f))
            }
            // El rombo del centro con los cuernos de carnero en sus cuatro puntas.
            val rombo = listOf(q(0f, -d * 0.48f), q(w * 0.42f, 0f), q(0f, d * 0.42f), q(-w * 0.42f, 0f))
            drawPath(poligono(rombo), hilo, style = Stroke(0.65f))
            drawPath(poligono(listOf(q(0f, -d * 0.3f), q(w * 0.26f, 0f), q(0f, d * 0.26f), q(-w * 0.26f, 0f))), hilo.copy(alpha = 0.5f), style = Stroke(0.45f))
            for ((cx, cz) in listOf(0f to -d * 0.48f, w * 0.42f to 0f, 0f to d * 0.42f, -w * 0.42f to 0f)) for (sentido in listOf(-1f, 1f)) {
                val espiral = (0..12).map { k -> val a = k * 0.55f; val rr = 0.07f * (1f - k / 15f); q(cx + sentido * (0.07f - cos(a) * rr), cz - sin(a) * rr * 0.9f) }
                drawPath(Path().apply { moveTo(espiral[0]); for (e in espiral.drop(1)) lineTo(e) }, hilo, style = Stroke(0.5f))
            }
            // Las arrugas del fieltro y lo gastado del uso: un rodal donde se sienta, una mancha de leche.
            for (k in 0 until 3) { val x = -w * 0.6f + k * w * 0.5f + az.entre(-0.1f, 0.1f); mancha(q(x, az.entre(-0.2f, 0.2f)), 9f, 2.2f, p.sombra(tela), 0.22f); mancha(q(x + 0.05f, az.entre(-0.2f, 0.2f)), 7f, 1.4f, p.luz(tela), 0.25f) }
            mancha(q(-0.3f, 0.08f), 13f, 3.6f, p.sombra(lerp(tela, C.TIERRA, 0.3f)), 0.3f)
            mancha(q(0.38f, -0.18f), 4f, 1.4f, p.luz(C.FIELTRO), 0.35f)
        }
        drawPath(cara, p.sombra(lerp(ribete, C.HOLLIN, 0.3f)).copy(alpha = 0.6f), style = Stroke(0.5f))
        // La esquina de delante, doblada hacia arriba: enseña el envés, más gris, y el canto rojo.
        val punta = q(w - doblez * 0.42f, d - doblez * 0.42f, alto + 0.13f)
        drawPath(poligono(q(w - doblez, d), q(w, d - doblez), punta), p.por(lerp(tela, C.PIEDRA, 0.35f), if (p.solALaDerecha) 0.5f else -0.2f))
        drawPath(Path().apply { moveTo(q(w - doblez, d)); lineTo(punta); lineTo(q(w, d - doblez)) }, p.medio(ribete), style = Stroke(1.2f, cap = StrokeCap.Round))
        drawLine(p.sombra(tela).copy(alpha = 0.6f), q(w - doblez, d), q(w, d - doblez), 0.6f)
        // La hierba que le sube por los bordes y se le echa encima.
        val briznas = Path()
        for (k in 0 until 46) {
            val u = az.real(); val borde = az.entero(3)
            val base = when (borde) { 0 -> q(-w + 2f * w * u, -d - 0.01f, 0f); 1 -> q(-w - 0.01f, -d + 2f * d * u, 0f); else -> q(-w + (2f * w - doblez) * u, d + 0.02f, 0f) }
            val h = az.entre(3f, 7f) * (if (borde == 2) 1.2f else 0.8f); val lado = az.entre(-2f, 2.5f)
            briznas.moveTo(base); briznas.cubicTo(base + Offset(lado * 0.2f, -h * 0.5f), base + Offset(lado * 0.7f, -h * 0.9f), base + Offset(lado, -h))
        }
        drawPath(briznas, p.por(verde, 0.1f), style = Stroke(0.75f, cap = StrokeCap.Round))
    }

    /**
     * Lo que hay en el fieltro de sentarse los días en que el objeto del día es de los grandes: la tabla de madera con el
     * queso seco cortado en trozos y la cuchara de madera **[Diseño propio]**: no es ninguno de los doce objetos.
     */
    private fun DrawScope.tablaDeQueso(p: Pincel, c: Offset) {
        val madera = lerp(C.MADERA, C.CUERO, 0.25f)
        drawOval(p.sombra(C.TIERRA).copy(alpha = 0.45f), Offset(c.x - 15f, c.y - 1f), Size(32f, 8f))
        val tabla = poligono(c + Offset(-15f, -4.2f), c + Offset(11f, -5.6f), c + Offset(16f, 1.8f), c + Offset(-11f, 3.8f))
        drawPath(poligono(c + Offset(-11f, 3.8f), c + Offset(16f, 1.8f), c + Offset(16f, 4f), c + Offset(-11f, 6f)), p.sombra(madera))
        drawPath(tabla, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.medio(madera), p.luz(madera)) else listOf(p.luz(madera), p.medio(madera)), c.x - 15f, c.x + 16f))
        drawPath(tabla, p.luz(lerp(madera, C.FIELTRO, 0.3f)).copy(alpha = 0.6f), style = Stroke(0.6f))
        for (k in 1..3) drawLine(p.sombra(madera).copy(alpha = 0.4f), c + Offset(-13f + k * 1.2f, -3.4f + k * 1.7f), c + Offset(11f + k * 1.2f, -4.8f + k * 1.6f), 0.35f)
        val queso = lerp(C.FIELTRO, C.ESTEPA, 0.15f)
        for (i in 0 until 5) for (j in 0 until 2) {
            val q = c + Offset(-9f + i * 4.6f + j * 2.2f, -2.2f + j * 2.8f - i * 0.3f); val w = 1.5f
            drawRect(p.sombra(queso), Offset(q.x - w, q.y - w * 0.4f), Size(w * 2f, w * 1.1f))
            drawRect(p.luz(queso), Offset(if (p.solALaDerecha) q.x - w * 0.2f else q.x - w, q.y - w * 0.9f), Size(w * 1.2f, w * 0.8f))
        }
        // La cuchara, de madera, con el mango hacia quien mira.
        drawLine(p.medio(C.MADERA_OSC), c + Offset(18f, 3f), c + Offset(30f, 7f), 1.3f, StrokeCap.Round)
        drawOval(p.por(C.MADERA, 0.3f), Offset(c.x + 14f, c.y), Size(6f, 3.6f))
        drawOval(p.sombra(C.MADERA_OSC), Offset(c.x + 15f, c.y + 0.6f), Size(4f, 2f))
    }

    private const val U_CABALLETE = 0.5f

    /**
     * El caballete de la silla, visto de tres cuartos: un borriquete de dos aspas y un travesaño; encima, la manta de
     * fieltro con su ribete, que cae a los dos lados, y la silla de madera «alta delante y detrás», pintada, con sus
     * clavos de cobre y el estribo corto colgando.
     */
    private fun DrawScope.caballete(p: Pincel, sol: Sol3, pie: Offset, silla: Boolean) {
        val u = U_CABALLETE; fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        val madera = lerp(C.MADERA, C.TIERRA, 0.25f); val luzX = if (p.solALaDerecha) 0.7f else -0.7f
        fun pata(a: Offset, b: Offset, cerca: Boolean) { drawLine(if (cerca) p.medio(madera) else p.sombra(madera), a, b, 3.6f * u, StrokeCap.Round); if (cerca) drawLine(p.luz(madera), a + Offset(luzX, 0f), b + Offset(luzX, 0f), 1.2f * u, StrokeCap.Round) }
        // Las aspas de detrás, más oscuras; luego el travesaño; luego las de delante.
        pata(q(30f, -6f), q(40f, -78f), false); pata(q(52f, -8f), q(40f, -78f), false)
        drawLine(p.sombra(madera), q(-42f, -76f), q(44f, -80f), 6f * u, StrokeCap.Round)
        drawLine(p.luz(madera), q(-42f, -79f), q(44f, -83f), 1.6f * u, StrokeCap.Round)
        pata(q(-50f, 0f), q(-38f, -76f), true); pata(q(-26f, 2f), q(-38f, -76f), true)
        // La manta de fieltro: cae a este lado del travesaño, con pliegues y el ribete rojo con su greca azul.
        val manta = Path().apply { moveTo(q(-36f, -80f)); cubicTo(q(-12f, -84f), q(14f, -86f), q(38f, -84f)); cubicTo(q(40f, -70f), q(42f, -56f), q(40f, -46f)); cubicTo(q(18f, -42f), q(-10f, -40f), q(-36f, -42f)); cubicTo(q(-38f, -54f), q(-38f, -68f), q(-36f, -80f)); close() }
        val tela = lerp(C.FIELTRO, C.ESTEPA, 0.18f)
        drawPath(manta, Brush.verticalGradient(listOf(p.luz(tela), p.medio(tela), p.sombra(tela)), q(0f, -86f).y, q(0f, -40f).y))
        clipPath(manta) {
            drawRect(Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(tela).copy(alpha = 0.6f), Color.Transparent) else listOf(Color.Transparent, p.sombra(tela).copy(alpha = 0.6f)), q(-36f, 0f).x, q(40f, 0f).x), q(-40f, -90f), Size(84f * u, 52f * u))
            for (k in 0..4) drawLine(p.sombra(tela).copy(alpha = 0.4f), q(-26f + k * 14f, -82f), q(-28f + k * 15f, -42f), 0.9f * u)
            drawRect(LuzVerano.pintado(p, C.ROJO, 0.35f), q(-40f, -52f), Size(84f * u, 8f * u))
            for (k in 0 until 9) drawPath(poligono(q(-34f + k * 8.6f, -48f), q(-30f + k * 8.6f, -51f), q(-26f + k * 8.6f, -48f), q(-30f + k * 8.6f, -45f)), LuzVerano.pintado(p, C.AZUL, 0.35f))
        }
        drawPath(manta, p.sombra(lerp(tela, C.CRIN, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        if (!silla) return
        // La silla: el borrén de delante, más alto, y el de detrás, el asiento entre los dos y el faldón.
        val pintada = lerp(C.MADERA, C.ROJO, 0.55f)
        val faldon = Path().apply { moveTo(q(-24f, -84f)); cubicTo(q(-8f, -80f), q(10f, -80f), q(26f, -86f)); lineTo(q(28f, -62f)); cubicTo(q(10f, -58f), q(-8f, -58f), q(-24f, -62f)); close() }
        drawPath(faldon, p.por(lerp(C.CUERO, C.HOLLIN, 0.2f), if (p.solALaDerecha) 0.2f else -0.1f))
        for (x in listOf(-18f, -6f, 6f, 18f)) drawCircle(p.luz(C.ORO), 1.3f * u, q(x, -66f))
        val asiento = Path().apply {
            moveTo(q(-34f, -84f)); cubicTo(q(-40f, -106f), q(-34f, -122f), q(-24f, -118f)); cubicTo(q(-18f, -112f), q(-20f, -98f), q(-12f, -92f))
            cubicTo(q(0f, -88f), q(12f, -90f), q(20f, -98f)); cubicTo(q(24f, -110f), q(34f, -114f), q(38f, -104f)); cubicTo(q(40f, -96f), q(38f, -88f), q(34f, -84f))
            cubicTo(q(14f, -80f), q(-14f, -80f), q(-34f, -84f)); close()
        }
        val sl = LuzVerano.pintado(p, pintada, 0.9f); val sm = LuzVerano.pintado(p, pintada, 0.15f); val ss = LuzVerano.pintado(p, pintada, -0.8f)
        drawPath(asiento, Brush.horizontalGradient(if (p.solALaDerecha) listOf(ss, sm, sl) else listOf(sl, sm, ss), q(-40f, 0f).x, q(40f, 0f).x))
        clipPath(asiento) { drawRect(Brush.verticalGradient(listOf(p.luz(lerp(pintada, C.ORO, 0.4f)).copy(alpha = 0.6f), Color.Transparent), q(0f, -122f).y, q(0f, -100f).y), q(-40f, -124f), Size(80f * u, 26f * u)) }
        drawPath(asiento, p.sombra(lerp(pintada, C.HOLLIN, 0.4f)).copy(alpha = 0.7f), style = Stroke(0.8f * u))
        // Las placas de cobre de los borrenes y el borde gastado del asiento.
        for (c in listOf(q(-28f, -108f), q(30f, -102f))) { drawCircle(p.luz(C.ORO), 3.4f * u, c); drawCircle(p.sombra(C.ORO), 3.4f * u, c, style = Stroke(0.6f * u)); drawCircle(p.medio(lerp(C.ORO, C.CUERO, 0.4f)), 1.2f * u, c) }
        drawPath(Path().apply { moveTo(q(-12f, -92f)); cubicTo(q(0f, -88f), q(12f, -90f), q(20f, -98f)) }, p.luz(lerp(pintada, C.FIELTRO, 0.4f)).copy(alpha = 0.7f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // El estribo corto, colgando de su correa.
        drawLine(p.medio(C.CUERO), q(2f, -82f), q(0f, -40f), 1.6f * u)
        drawPath(Path().apply { moveTo(q(-6f, -40f)); cubicTo(q(-8f, -30f), q(8f, -30f), q(6f, -40f)); close() }, p.medio(lerp(C.HOLLIN, C.PIEDRA, 0.35f)), style = Stroke(1.6f * u))
        drawLine(p.luz(lerp(C.HOLLIN, C.PIEDRA, 0.5f)), q(-7f, -31.6f), q(7f, -31.6f), 2f * u, StrokeCap.Round)
    }

    private fun siluetaDeCaballete(pie: Offset, silla: Boolean): Path {
        val u = U_CABALLETE; fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        return Path().apply {
            addPath(poligono(listOf(q(-52f, 0f), q(-46f, 0f), q(-38f, -60f), q(-30f, 2f), q(-24f, 2f), q(-36f, -76f), q(-40f, -76f))))
            addPath(poligono(listOf(q(28f, -6f), q(34f, -6f), q(40f, -60f), q(48f, -8f), q(54f, -8f), q(42f, -78f), q(38f, -78f))))
            addPath(poligono(q(-38f, -84f), q(40f, -86f), q(42f, -44f), q(-38f, -40f)))
            if (silla) addPath(poligono(q(-36f, -84f), q(-30f, -120f), q(0f, -92f), q(32f, -112f), q(38f, -84f)))
        }
    }

    /** Lo que cuelga de la pared de la tienda: el ronzal y las riendas en una estaca de la celosía, y la soga de crin enrollada, cada uno con su sombra en el fieltro. */
    private fun DrawScope.arreos(p: Pincel, sol: Sol3, g: Ger) {
        val r = sol.r; val corre = Offset((r.ax * 3f).coerceIn(-5f, 5f), 1.6f)
        val t1 = angulo(g, 284f); val clavo = g.pared(t1, 1.25f, g.radio + 0.02f)
        // El ronzal: un lazo de crin y cuero con su argolla, y las riendas que caen en curva.
        val ronzal = Path().apply { moveTo(clavo); cubicTo(clavo + Offset(-5f, 6f), clavo + Offset(-4f, 16f), clavo + Offset(1f, 18f)); cubicTo(clavo + Offset(6f, 16f), clavo + Offset(6f, 6f), clavo) }
        val riendas = Path().apply { moveTo(clavo); cubicTo(clavo + Offset(3f, 10f), clavo + Offset(9f, 22f), clavo + Offset(5f, 30f)) }
        if (r.fuerza > 0.05f) { drawPath(ronzal.apply { translate(corre) }, r.color.copy(alpha = 0.35f * r.fuerza), style = Stroke(1.4f), blendMode = BlendMode.Multiply); ronzal.translate(-corre) }
        drawPath(ronzal, p.medio(C.CUERO), style = Stroke(1.3f, cap = StrokeCap.Round))
        drawPath(riendas, p.medio(C.CRIN), style = Stroke(0.9f, cap = StrokeCap.Round))
        drawCircle(p.luz(lerp(C.PIEDRA, C.FIELTRO, 0.3f)), 1.6f, clavo + Offset(1f, 18f), style = Stroke(0.7f))
        drawCircle(p.medio(C.MADERA_OSC), 1f, clavo)
        // La soga de crin, enrollada y colgada de otra estaca, más allá.
        val t2 = angulo(g, 368f); val clavo2 = g.pared(t2, 1.2f, g.radio + 0.02f)
        for (j in 0 until 4) {
            val w = 6.4f + j * 0.5f; val c = clavo2 + Offset(j * 0.6f - 1f, 9f + j * 0.4f)
            if (r.fuerza > 0.05f) drawOval(r.color.copy(alpha = 0.3f * r.fuerza), Offset(c.x - w + corre.x, c.y - 8f + corre.y), Size(w * 2f, 16f), style = Stroke(1f), blendMode = BlendMode.Multiply)
            drawOval(p.por(C.CRIN, if (j % 2 == 0) 0.3f else -0.3f), Offset(c.x - w, c.y - 8f), Size(w * 2f, 16f), style = Stroke(1.1f))
        }
        drawLine(p.medio(C.CRIN), clavo2, clavo2 + Offset(0f, 3f), 1.2f)
        drawCircle(p.medio(C.MADERA_OSC), 1f, clavo2)
    }

    /** El uso que le falta a la pared de la tienda de Bat: el barro salpicado del bajo, chorretones de lluvia, la hierba que crece arrimada. */
    private fun DrawScope.usoDeLaPared(p: Pincel, g: Ger, az: Azar, verde: Color) {
        val (a, b) = g.visible()
        val bajo = (0..24).map { g.pared(a + (b - a) * it / 24f, 0f) }; val alto = (0..24).map { g.pared(a + (b - a) * it / 24f, 0.42f) }
        val franja = poligono(bajo + alto.reversed())
        drawPath(franja, Brush.verticalGradient(listOf(Color.Transparent, p.sombra(lerp(C.TIERRA, C.ESTIERCOL, 0.4f)).copy(alpha = 0.6f)), alto.minOf { it.y }, bajo.maxOf { it.y }))
        clipPath(PiezasMongol.silueta(g)) {
            repeat(14) {
                val t = az.entre(a + 0.1f, b - 0.1f); if (abs(t) < g.mediaPuerta * 1.8f) return@repeat
                val q = g.pared(t, az.entre(0.05f, 0.5f)); drawOval(p.sombra(C.TIERRA).copy(alpha = 0.3f), Offset(q.x, q.y), Size(az.entre(1f, 3f), az.entre(0.6f, 1.4f)))
            }
            repeat(18) {
                val t = az.entre(a + 0.15f, b - 0.15f); if (abs(t) < g.mediaPuerta * 1.8f) return@repeat
                val y0 = g.pared - 0.08f; val q0 = g.pared(t, y0); val q1 = g.pared(t + az.entre(-0.01f, 0.01f), y0 - az.entre(0.3f, 0.9f))
                drawLine(p.sombra(lerp(C.FIELTRO, C.TIERRA, 0.5f)).copy(alpha = 0.3f), q0, q1, az.entre(0.6f, 1.6f), StrokeCap.Round)
            }
            // Un cerco de agua viejo, amarillento.
            val c = g.pared(angulo(g, 340f), 0.95f); mancha(c, 14f, 9f, p.medio(lerp(C.FIELTRO, C.ESTEPA, 0.5f)), 0.18f)
        }
        // La hierba que crece arrimada al pie, que nadie pisa.
        for (i in 0..24) { val q = bajo[i]; if (abs(a + (b - a) * i / 24f) < g.mediaPuerta * 1.6f) continue; for (j in 0 until 3) drawLine(p.por(verde, if (j == 1) 0.4f else -0.4f), q + Offset(j * 1.2f - 1f, 0.5f), q + Offset(j * 1.6f - 1.4f + az.entre(-1f, 1f), -az.entre(3f, 7f)), 0.7f, StrokeCap.Round) }
    }

    /**
     * El queso seco (aaruul) puesto a secar al sol sobre el techo de la tienda, en un paño viejo tendido sobre el
     * fieltro y sujeto con una piedra **[Diseño propio]**: el cuento dice que se hace queso seco, no dónde se seca.
     */
    private fun DrawScope.quesoEnElTecho(p: Pincel, sol: Sol3, g: Ger, az: Azar) {
        val t0 = angulo(g, 246f)
        fun techo(t: Float, k: Float): Offset { val r = g.alero + (g.aro - g.alero) * k; val y = g.pared - 0.06f + (g.cumbre - g.pared + 0.06f) * k; return g.s.p(r * sin(t), y + 0.03f, -r * cos(t)) }
        val a = t0 - 0.04f; val b = t0 + 0.3f; val k0 = 0.16f; val k1 = 0.5f
        // El paño, de borde irregular, que se adapta a la pendiente del techo.
        val borde = (0..5).map { techo(a + (b - a) * it / 5f, k0 + az.entre(-0.02f, 0.02f)) } + (5 downTo 0).map { techo(a + (b - a) * it / 5f + az.entre(-0.01f, 0.01f), k1 + az.entre(-0.03f, 0.02f)) }
        val pano = poligono(borde)
        drawPath(pano, p.arriba(lerp(C.LANA, C.ESTEPA, 0.25f)))
        drawPath(pano, p.sombra(lerp(C.LANA, C.ESTEPA, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.4f))
        // Los trozos de queso, sueltos, cada uno con su lado de luz y su sombrita en el paño.
        val queso = lerp(C.FIELTRO, C.ESTEPA, 0.18f)
        repeat(30) {
            val c = techo(az.entre(a + 0.02f, b - 0.02f), az.entre(k0 + 0.04f, k1 - 0.04f)); val w = az.entre(0.9f, 1.5f)
            drawOval(sol.r.color.copy(alpha = 0.6f * sol.r.fuerza), Offset(c.x - w + sol.r.ax * 0.6f, c.y - w * 0.2f), Size(w * 2.2f, w * 0.9f), blendMode = BlendMode.Multiply)
            drawRect(p.sombra(queso), Offset(c.x - w, c.y - w * 0.5f), Size(w * 2f, w))
            drawRect(p.luz(queso), Offset(if (p.solALaDerecha) c.x - w * 0.2f else c.x - w, c.y - w * 0.8f), Size(w * 1.2f, w * 0.7f))
        }
        // La piedra que sujeta el paño.
        val piedra = techo(b - 0.02f, k1 - 0.02f)
        drawOval(p.por(C.PIEDRA, -0.3f), Offset(piedra.x - 2.4f, piedra.y - 2f), Size(4.8f, 3f)); drawOval(p.luz(C.PIEDRA), Offset(piedra.x - 1.6f, piedra.y - 2.2f), Size(2.8f, 1.4f))
    }

    /** El cubo de cuero de ordeñar, lleno de leche; el día de la primera leche, con la cuchara de nueve agujeros apoyada. */
    private fun DrawScope.cuboDeLeche(p: Pincel, pie: Offset, cuchara: Boolean) {
        val w = 6.4f; val h = 8f
        drawOval(p.sombra(C.TIERRA).copy(alpha = 0.4f), Offset(pie.x - w * 0.9f, pie.y - 1f), Size(w * 2f, 2.4f))
        drawPath(poligono(pie + Offset(-w, -h), pie + Offset(w, -h), pie + Offset(w * 0.8f, 0f), pie + Offset(-w * 0.8f, 0f)), Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.CUERO), p.luz(C.CUERO)) else listOf(p.luz(C.CUERO), p.sombra(C.CUERO)), pie.x - w, pie.x + w))
        drawLine(p.sombra(C.HOLLIN).copy(alpha = 0.5f), pie + Offset(0f, -h), pie + Offset(0f, 0f), 0.5f)
        drawOval(p.medio(C.HOLLIN), Offset(pie.x - w, pie.y - h - 1.4f), Size(w * 2f, 2.8f))
        drawOval(p.luz(lerp(C.FIELTRO, Color.White, 0.4f)), Offset(pie.x - w * 0.86f, pie.y - h - 1f), Size(w * 1.72f, 2.2f))
        if (!cuchara) return
        val a = pie + Offset(-2f, -h - 0.6f); val b = pie + Offset(9f, -h - 9f)
        drawLine(p.medio(C.MADERA), a, b, 1.2f, StrokeCap.Round)
        drawOval(p.por(C.MADERA, 0.3f), Offset(b.x - 2.6f, b.y - 2.4f), Size(5.2f, 3.6f))
        for (j in 0 until 9) drawCircle(p.sombra(C.MADERA_OSC), 0.32f, b + Offset(-1.6f + (j % 3) * 1.6f, -1.2f + (j / 3) * 1.1f))
    }

    /** El cubo volcado de una patada, con la leche derramada en la hierba. */
    private fun DrawScope.cuboVolcado(p: Pincel, pie: Offset) {
        mancha(pie + Offset(8f, 0.6f), 11f, 2f, p.luz(lerp(C.FIELTRO, Color.White, 0.3f)), 0.75f)
        drawPath(poligono(pie + Offset(-6f, -5.6f), pie + Offset(3f, -6.6f), pie + Offset(4f, 0f), pie + Offset(-5f, 0.6f)), p.por(C.CUERO, 0.2f))
        drawOval(p.medio(C.HOLLIN), Offset(pie.x + 2.4f, pie.y - 6.6f), Size(2.6f, 6.8f))
    }

    /** La leche que el chamán echó al cielo con la cuchara, caída en la hierba en gotas blancas alrededor de la cuerda. */
    private fun DrawScope.lecheEnLaHierba(p: Pincel, az: Azar) {
        val gotas = (0 until 110).map { val t = az.real(); enLaCuerda(t) + Offset(az.entre(-30f, 24f) * (1f - 0.5f * t), az.entre(-4f, 8f)) }
        drawPoints(gotas, PointMode.Points, p.luz(lerp(C.FIELTRO, Color.White, 0.5f)).copy(alpha = 0.9f), 1.8f, StrokeCap.Round)
    }

    /** La liebre de Mönkh, colgada por las patas del armazón del odre. */
    private fun DrawScope.liebre(p: Pincel, gancho: Offset) {
        val pelo = lerp(C.ESTEPA, C.CUERO, 0.45f)
        drawLine(p.medio(C.CRIN), gancho, gancho + Offset(0f, 3f), 0.6f)
        val cuerpo = Path().apply { moveTo(gancho + Offset(-1.6f, 3f)); cubicTo(gancho + Offset(-3.6f, 8f), gancho + Offset(-3.4f, 14f), gancho + Offset(-1f, 17f)); cubicTo(gancho + Offset(1.6f, 14f), gancho + Offset(3f, 8f), gancho + Offset(1.6f, 3f)); close() }
        drawPath(cuerpo, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(pelo), p.luz(pelo)) else listOf(p.luz(pelo), p.sombra(pelo)), gancho.x - 3.6f, gancho.x + 3f))
        for (dx in listOf(-1.2f, 0.6f)) drawLine(p.medio(lerp(pelo, C.HOLLIN, 0.3f)), gancho + Offset(dx, 16.6f), gancho + Offset(dx * 1.8f, 22f), 0.9f, StrokeCap.Round)
        drawCircle(p.luz(C.FIELTRO), 0.7f, gancho + Offset(0.6f, 4f))
    }

    /** La piel de oveja tendida en la hierba, donde Bat duerme fuera las noches claras. */
    private fun DrawScope.pielDeOveja(p: Pincel, c: Offset, az: Azar) {
        val borde = (0 until 18).map { j -> val a = j / 18f * 2f * PI.toFloat(); c + Offset(cos(a) * 18f * az.entre(0.85f, 1.1f), sin(a) * 4.2f * az.entre(0.8f, 1.1f)) }
        drawPath(poligono(borde.map { it + Offset(0.6f, 1f) }), p.sombra(C.TIERRA).copy(alpha = 0.4f))
        drawPath(poligono(borde), p.arriba(C.LANA))
        repeat(26) { val q = c + Offset(az.entre(-15f, 15f), az.entre(-3f, 3f)); drawOval(p.sombra(C.LANA).copy(alpha = 0.3f), Offset(q.x - 1f, q.y - 0.3f), Size(2f, 0.7f)) }
    }

    /** Las tijeras de esquilar, de hierro, abiertas sobre la lana. */
    private fun DrawScope.tijeras(p: Pincel, c: Offset) {
        val hierro = lerp(C.HOLLIN, C.PIEDRA, 0.45f)
        drawLine(p.medio(hierro), c, c + Offset(9f, 2f), 1f, StrokeCap.Round); drawLine(p.medio(hierro), c + Offset(0.6f, 1f), c + Offset(8f, -2f), 1f, StrokeCap.Round)
        drawOval(p.luz(hierro), Offset(c.x - 2.4f, c.y - 1.4f), Size(3f, 3f), style = Stroke(0.6f))
    }

    /** Las matas altas de delante, cortadas por el borde de abajo, con alguna flor; a los lados, donde no estorban al gato. */
    private fun DrawScope.matasDeDelante(m: Momento, p: Pincel, az: Azar) {
        val verde = V.hierba(m); val flores = EscenaMongol.flores(m); val alta = 0.55f + 0.6f * EscenaMongol.pasto(m)
        val claras = Path(); val oscuras = Path(); val amarillas = ArrayList<Offset>(); val moradas = ArrayList<Offset>()
        for ((x0, x1) in listOf(-4f to 74f, 300f to 384f, 130f to 296f)) {
            var x = x0; val bajas = x0 > 100f && x0 < 200f
            while (x < x1) {
                val q = Offset(x, ALTO + 1f); val h = az.entre(8f, 20f) * alta * (if (bajas) 0.45f else 1f)
                val dobla = 3f + az.entre(-2f, 2f) + 3f * m.viento
                val camino = if (az.entero(3) == 0) claras else oscuras
                camino.moveTo(q); camino.cubicTo(q + Offset(0f, -h * 0.5f), q + Offset(dobla * 0.5f, -h * 0.9f), q + Offset(dobla, -h))
                if (az.real() < flores * 0.22f && !bajas) (if (az.entero(2) == 0) amarillas else moradas) += q + Offset(dobla, -h)
                x += az.entre(1.4f, 3.2f) * (if (bajas) 2.2f else 1f)
            }
        }
        drawPath(oscuras, p.por(lerp(verde, C.MONTE, 0.2f), -0.6f), style = Stroke(1.1f, cap = StrokeCap.Round))
        drawPath(claras, p.luz(lerp(verde, C.ESTEPA, 0.3f)), style = Stroke(1f, cap = StrokeCap.Round))
        drawPoints(amarillas, PointMode.Points, p.luz(C.ORO), 2.6f, StrokeCap.Round)
        drawPoints(moradas, PointMode.Points, p.luz(lerp(C.SOMBRA, C.ROJO, 0.32f)), 2.6f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- El marco: la vara de la carreta y la cuerda con lo colgado

    private val DESDE = VeranoMongol.VARA_PIE + (VeranoMongol.VARA_PUNTA - VeranoMongol.VARA_PIE) * 0.93f

    /** La cuerda de crin, de la punta de la vara al techo de la tienda: cuanto más carga, más se comba. */
    private fun lineaDeLaCuerda(m: Momento): List<Offset> {
        val caida = when (EscenaMongol.colgado(m)) { Colgado.LANA, Colgado.FIELTRO -> 32f; Colgado.CUAJADA -> 29f; Colgado.CINTAS -> 24f; Colgado.NADA -> 20f }
        return cuerda(DESDE, VeranoMongol.CUERDA_FIN, caida, 40)
    }

    private enum class Pieza { BOLSA, PANO, SARTA, ODRECILLO, PIEL, VELLON, FIELTRO, KHADAG }

    /**
     * Una cosa colgada de la cuerda: dónde ([q], en la cuerda; [t], de 0 a 1 a lo largo; [dir], hacia dónde baja la
     * cuerda ahí), qué es y su tamaño. [doble]: dos bolsas atadas al mismo nudo.
     */
    private class Colgada(val q: Offset, val t: Float, val dir: Offset, val pieza: Pieza, val ancho: Float, val alto: Float, val color: Color, val semilla: Int, val doble: Boolean = false)

    /**
     * Lo que cuelga hoy de la cuerda ([EscenaMongol.colgado]). Los días de leche: bolsas de cuajada que gotean (alguna
     * pareja atada al mismo nudo), sartas de queso seco, paños, un odrecillo y una piel; en la esquila, vellones de tres
     * colores; en la semana del fieltro, piezas de fieltro nuevo; en las fiestas, cintas azules y blancas. Ni en fila ni
     * a la misma distancia: cada día cambia un poco lo que hay y dónde **[Diseño propio]**.
     */
    private fun colgados(m: Momento, linea: List<Offset>): List<Colgada> {
        val tipo = EscenaMongol.colgado(m); if (tipo == Colgado.NADA) return emptyList()
        val az = m.azar("colgado"); val n = linea.size - 1; val llueve = V.rioTurbio(m)
        val lista = ArrayList<Colgada>()
        var t = 0.03f + az.entre(0f, 0.025f)
        var i = az.entero(4)
        val ciclo = listOf(Pieza.BOLSA, Pieza.PANO, Pieza.SARTA, Pieza.BOLSA, Pieza.PIEL, Pieza.ODRECILLO, Pieza.BOLSA, Pieza.SARTA, Pieza.PANO, Pieza.BOLSA)
        while (t < 0.9f) {
            val s = 1.9f - 0.95f * t
            val pieza0 = when (tipo) {
                Colgado.CUAJADA -> ciclo[i % ciclo.size]
                Colgado.LANA -> if (i % 4 == 3) Pieza.BOLSA else Pieza.VELLON
                Colgado.FIELTRO -> if (i % 3 == 2) Pieza.BOLSA else Pieza.FIELTRO
                Colgado.CINTAS -> if (i % 5 == 4) Pieza.BOLSA else Pieza.KHADAG
                Colgado.NADA -> Pieza.BOLSA
            }
            // Con lluvia se recoge lo que no se puede mojar (los paños, las pieles, la lana, el fieltro): quedan, sueltas, las
            // bolsas que escurren y las cintas.
            val recogida = llueve && (pieza0 == Pieza.PANO || pieza0 == Pieza.PIEL || pieza0 == Pieza.VELLON || pieza0 == Pieza.FIELTRO || pieza0 == Pieza.SARTA)
            val pieza = pieza0
            val (w, h) = when (pieza) {
                Pieza.BOLSA -> 7.6f to 10.5f; Pieza.PANO -> 14f to 13f; Pieza.SARTA -> 3f to 15f; Pieza.ODRECILLO -> 7.4f to 9f
                Pieza.PIEL -> 18f to 14f; Pieza.VELLON -> 15f to 11f; Pieza.FIELTRO -> 18f to 22f; Pieza.KHADAG -> 3.6f to 19f
            }
            val color = when (pieza) {
                Pieza.PANO -> listOf(C.ROJO, C.AZUL, lerp(C.FIELTRO, C.ESTEPA, 0.3f), C.ROJO)[az.entero(4)]
                Pieza.VELLON -> listOf(C.LANA, lerp(C.LANA, C.PIEDRA, 0.5f), lerp(C.LANA, C.CUERO, 0.55f), C.LANA)[az.entero(4)]
                Pieza.KHADAG -> if (az.entero(4) == 0) lerp(C.FIELTRO, Color.White, 0.3f) else lerp(C.AZUL, C.MONTE, az.entre(0f, 0.25f))
                else -> C.FIELTRO
            }
            val k = (t * n).toInt().coerceIn(1, n - 1)
            val d = linea[k + 1] - linea[k - 1]; val dir = d / d.getDistance().coerceAtLeast(0.01f)
            val cosa = Colgada(linea[k], t, dir, pieza, w * s * az.entre(0.85f, 1.15f), h * s * az.entre(0.85f, 1.18f), color, az.entero(1000), pieza == Pieza.BOLSA && az.real() < 0.35f)
            if (!recogida) lista += cosa
            val paso = when (pieza) { Pieza.KHADAG, Pieza.SARTA -> 0.05f; Pieza.BOLSA, Pieza.ODRECILLO -> 0.075f; Pieza.PANO, Pieza.PIEL, Pieza.VELLON -> 0.11f; Pieza.FIELTRO -> 0.15f }
            // A trechos se juntan, a trechos queda la cuerda sola.
            t += paso * (if (az.real() < 0.3f) 0.55f else az.entre(0.8f, 1.35f)) * (if (tipo == Colgado.CINTAS) 0.85f else 1f) * (0.75f + 0.5f * t); i++
        }
        return lista
    }

    fun DrawScope.marco(m: Momento, p: Pincel, sol: Sol3) {
        val madera = lerp(C.MADERA, C.PIEDRA, 0.2f); val pie = VeranoMongol.VARA_PIE; val punta = VeranoMongol.VARA_PUNTA
        val luzX = if (p.solALaDerecha) 1f else -1f
        // La vara: más gruesa abajo, con su corteza vieja, su lado de luz y las vueltas de crin que la atan a la carreta.
        val d = punta - pie; val nrm = Offset(-d.y, d.x) / d.getDistance()
        val vara = poligono(listOf(pie + nrm * 2.2f, punta + nrm * 1.5f, punta - nrm * 1.5f, pie - nrm * 2.2f))
        drawPath(vara, p.medio(madera))
        clipPath(vara) {
            drawPath(poligono(listOf(pie + nrm * (2.2f * luzX), punta + nrm * (1.5f * luzX), punta + nrm * (0.2f * luzX), pie + nrm * (0.3f * luzX))), p.luz(lerp(madera, C.FIELTRO, 0.15f)))
            drawPath(poligono(listOf(pie - nrm * (2.2f * luzX), punta - nrm * (1.5f * luzX), punta - nrm * (0.6f * luzX), pie - nrm * (0.9f * luzX))), p.sombra(madera))
            val az = Azar(Semilla.de("mongol", "vara")); repeat(16) { val k = az.real(); val q = pie + d * k; drawLine(p.sombra(madera).copy(alpha = 0.5f), q + nrm * az.entre(-1.4f, 1.4f), q + nrm * az.entre(-1.4f, 1.4f) + d * 0.03f, 0.35f) }
        }
        for (j in 0..4) { val c = pie + d * (0.03f + j * 0.018f); drawLine(p.sombra(C.CRIN), c - nrm * 3f, c + nrm * 3f + Offset(0f, 0.8f), 1.2f); drawLine(p.luz(lerp(C.CRIN, C.FIELTRO, 0.3f)).copy(alpha = 0.5f), c - nrm * 2.6f + Offset(0f, -0.4f), c + nrm * 2.6f + Offset(0f, 0.4f), 0.4f) }
        // La cuerda de crin que cruza el cielo, con el nudo en la vara.
        val linea = lineaDeLaCuerda(m)
        for (j in 0..2) { val c = DESDE + d * (j * 0.012f); drawLine(p.sombra(C.CRIN), c - nrm * 2.2f, c + nrm * 2.2f + Offset(0f, 0.6f), 1f) }
        drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.medio(C.CRIN), style = Stroke(1.2f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(linea[0] + Offset(0f, -0.4f)); for (q in linea.drop(1)) lineTo(q + Offset(0f, -0.4f)) }, p.luz(lerp(C.CRIN, C.FIELTRO, 0.35f)).copy(alpha = 0.4f), style = Stroke(0.4f, cap = StrokeCap.Round))
        // Lo que cuelga.
        val noche = p.l.noche
        for (cosa in colgados(m, linea)) colgada(p, cosa, m.viento, noche)
    }

    /** Pinta una cosa colgada de la cuerda: siempre con su lado de luz, su sombra propia y el nudo con que se ata. */
    private fun DrawScope.colgada(p: Pincel, c: Colgada, viento: Float, noche: Float) {
        val az = Azar(c.semilla.toLong()); val q = c.q; val w = c.ancho; val h = c.alto
        val luzIzq = !p.solALaDerecha
        fun lados(col: Color, x0: Float, x1: Float) = Brush.horizontalGradient(if (luzIzq) listOf(p.luz(col), p.medio(col), p.sombra(col)) else listOf(p.sombra(col), p.medio(col), p.luz(col)), x0, x1)
        // Lo pintado (un paño, una cinta): al sol sube y se calienta, en la sombra baja y se enfría (LuzVerano.pintado).
        fun pintados(col: Color, x0: Float, x1: Float) = Brush.horizontalGradient(if (luzIzq) listOf(LuzVerano.pintado(p, col, 0.9f), LuzVerano.pintado(p, col, 0.2f), LuzVerano.pintado(p, col, -0.8f)) else listOf(LuzVerano.pintado(p, col, -0.8f), LuzVerano.pintado(p, col, 0.2f), LuzVerano.pintado(p, col, 0.9f)), x0, x1)
        val tuerce = viento * 2.4f
        when (c.pieza) {
            Pieza.BOLSA -> {
                // La bolsa de tela con la cuajada que escurre: el cuello fruncido y atado, el cuerpo abultado y desigual,
                // mojada y amarillenta abajo, y gotea. A veces son dos atadas al mismo nudo.
                val cuantas = if (c.doble) 2 else 1
                for (j in 0 until cuantas) {
                    val corre = if (cuantas == 2) (if (j == 0) -w * 0.38f else w * 0.36f) else 0f
                    val ww = w * (if (j == 1) 0.86f else 1f); val hh = h * (if (j == 1) 0.84f else 1f) * az.entre(0.92f, 1.08f)
                    val nudo = q + Offset(corre * 0.25f, 2.2f)
                    val b = nudo + Offset(corre + tuerce * 0.3f, hh)
                    drawLine(p.medio(C.CRIN), q, nudo, 0.6f)
                    val l1 = az.entre(0.5f, 0.75f); val l2 = az.entre(0.5f, 0.75f)
                    val bolsa = Path().apply {
                        moveTo(nudo + Offset(-ww * 0.12f, 0.8f)); cubicTo(nudo + Offset(-ww * 0.3f, hh * 0.15f), b + Offset(-ww * 0.62f, -hh * l1), b + Offset(-ww * 0.5f, -hh * 0.3f))
                        cubicTo(b + Offset(-ww * 0.46f, -hh * 0.08f), b + Offset(-ww * 0.3f, 0.4f), b + Offset(-ww * 0.08f, -0.2f))
                        cubicTo(b + Offset(ww * 0.08f, 0.6f), b + Offset(ww * 0.42f, 0f), b + Offset(ww * 0.48f, -hh * 0.22f))
                        cubicTo(b + Offset(ww * 0.6f, -hh * l2), nudo + Offset(ww * 0.32f, hh * 0.18f), nudo + Offset(ww * 0.12f, 0.8f)); close()
                    }
                    val tela = lerp(C.FIELTRO, C.LANA, 0.45f)
                    drawPath(bolsa, lados(tela, b.x - ww * 0.6f, b.x + ww * 0.6f))
                    clipPath(bolsa) {
                        drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.medio(lerp(tela, C.ESTEPA, 0.6f)).copy(alpha = 0.7f)), nudo.y + hh * 0.5f, b.y), Offset(b.x - ww, nudo.y), Size(ww * 2f, hh + 1f))
                        for (k in -2..2) drawLine(p.sombra(tela).copy(alpha = 0.45f), nudo + Offset(k * 0.4f, 1f), b + Offset(k * ww * 0.18f, -hh * az.entre(0.2f, 0.5f)), 0.45f)
                        mancha(b + Offset(if (luzIzq) -ww * 0.2f else ww * 0.2f, -hh * 0.55f), ww * 0.22f, hh * 0.18f, p.luz(lerp(tela, Color.White, 0.3f)), 0.5f)
                    }
                    // El cuello fruncido y la cuerda que lo ata.
                    drawPath(poligono(nudo + Offset(-ww * 0.16f, 0f), nudo + Offset(ww * 0.16f, 0f), nudo + Offset(ww * 0.1f, 1.8f), nudo + Offset(-ww * 0.1f, 1.8f)), p.medio(tela))
                    drawLine(p.sombra(C.CRIN), nudo + Offset(-ww * 0.18f, 1f), nudo + Offset(ww * 0.18f, 0.8f), 0.8f)
                    // Las gotas de suero.
                    for (k in 0 until 3) drawCircle(p.luz(lerp(C.FIELTRO, C.RIO, 0.25f)).copy(alpha = 0.85f - 0.2f * k), 0.55f - 0.08f * k, b + Offset(az.entre(-0.8f, 0.8f), 1.6f + k * 2.8f + az.entre(0f, 1f)))
                }
            }
            Pieza.PANO, Pieza.PIEL, Pieza.VELLON, Pieza.FIELTRO -> {
                // Lo que se echa sobre la cuerda: el canto de arriba sigue la cuerda; cae a plomo, con el borde de abajo
                // desigual, y por el lado más bajo asoma la vuelta de detrás. Un paño con su franja tejida; una piel con
                // el cuero a la vista; un vellón de lana suelta; una pieza de fieltro nuevo, gruesa, con el borde sin cortar.
                val a = q - c.dir * (w * 0.5f); val b = q + c.dir * (w * 0.5f)
                val col = when (c.pieza) { Pieza.FIELTRO -> lerp(C.FIELTRO, Color.White, 0.4f); Pieza.PIEL -> lerp(C.LANA, C.FIELTRO_VIEJO, 0.4f); else -> c.color }
                val lana = c.pieza == Pieza.PIEL || c.pieza == Pieza.VELLON
                val pasos = 6
                val abajo = (0..pasos).map { j -> val u = j / pasos.toFloat(); val top = a + (b - a) * u
                    top + Offset(tuerce * (0.6f + 0.4f * u), h * (0.82f + 0.18f * sin(u * 3.1f)) * (if (lana) az.entre(0.82f, 1.12f) else az.entre(0.95f, 1.05f))) }
                // La vuelta de detrás, por el lado bajo de la cuerda.
                drawPath(poligono(listOf(b, b + Offset(1.6f + tuerce, h * 0.2f), abajo.last() + Offset(1.4f, -h * 0.25f), abajo.last())), p.sombra(lerp(col, C.HOLLIN, 0.12f)))
                val forma = Path().apply { moveTo(a); lineTo(b); lineTo(abajo.last()); for (j in pasos - 1 downTo 0) { val u0 = abajo[j + 1]; val u1 = abajo[j]; cubicTo(u0 + (u1 - u0) * 0.33f + Offset(0f, 1.2f), u0 + (u1 - u0) * 0.66f + Offset(0f, 1.2f), u1) }; close() }
                drawPath(forma, if (c.pieza == Pieza.PANO) pintados(col, minOf(a.x, b.x) - 1f, maxOf(a.x, b.x) + 1f) else lados(col, minOf(a.x, b.x) - 1f, maxOf(a.x, b.x) + 1f))
                clipPath(forma) {
                    // La sombra que hace la cuerda en el doblez, y los pliegues que bajan.
                    drawPath(Path().apply { moveTo(a); lineTo(b) }, p.sombra(col).copy(alpha = 0.6f), style = Stroke(2.4f))
                    when (c.pieza) {
                        Pieza.PANO -> {
                            for (k in 1..4) { val top = a + (b - a) * (k / 5f); drawLine(p.sombra(col).copy(alpha = 0.4f), top, top + Offset(tuerce + az.entre(-0.8f, 0.8f), h), 0.7f); drawLine(p.luz(col).copy(alpha = 0.3f), top + Offset(0.8f, 0f), top + Offset(tuerce + 0.8f, h), 0.5f) }
                            val franja = if (c.color == C.ROJO) C.ORO else if (c.color == C.AZUL) C.FIELTRO else C.ROJO
                            drawPath(Path().apply { moveTo(a + Offset(-2f, h * 0.66f)); lineTo(b + Offset(2f, h * 0.66f)) }, LuzVerano.pintado(p, franja, 0.3f).copy(alpha = 0.8f), style = Stroke(h * 0.08f))
                        }
                        Pieza.PIEL, Pieza.VELLON -> repeat((w * 1.4f).toInt()) {
                            val k = a + (b - a) * az.real() + Offset(az.entre(-1f, 1f), az.entre(1f, h))
                            drawOval(p.sombra(col).copy(alpha = 0.3f), Offset(k.x - 1f, k.y - 0.3f), Size(2f, 0.9f)); drawOval(p.luz(lerp(col, Color.White, 0.2f)).copy(alpha = 0.38f), Offset(k.x - 0.9f, k.y - 1f), Size(1.8f, 0.8f))
                        }
                        Pieza.FIELTRO -> repeat(20) { val k = a + (b - a) * az.real() + Offset(0f, az.entre(2f, h)); drawOval(p.medio(lerp(col, C.ESTEPA, 0.3f)).copy(alpha = 0.25f), Offset(k.x, k.y), Size(az.entre(1f, 3f), 0.6f)) }
                        else -> Unit
                    }
                }
                // La piel enseña el cuero en el borde de abajo; el fieltro, su canto grueso.
                if (c.pieza == Pieza.PIEL) for (j in 0 until pasos) drawLine(p.medio(lerp(C.CUERO, C.FIELTRO, 0.3f)), abajo[j], abajo[j + 1], 0.9f, StrokeCap.Round)
                if (c.pieza == Pieza.FIELTRO) drawPath(Path().apply { moveTo(abajo[0]); for (u in abajo.drop(1)) lineTo(u) }, p.sombra(col).copy(alpha = 0.6f), style = Stroke(1f))
            }
            Pieza.SARTA -> {
                // Una sarta de queso seco: trozos claros ensartados en un hilo, cada uno con su cara de luz.
                val n = (h / 2.4f).toInt()
                drawLine(p.medio(C.CRIN), q, q + Offset(tuerce * 0.5f, h), 0.4f)
                for (k in 0 until n) {
                    val c0 = q + Offset(tuerce * 0.5f * k / n, 1.6f + k * 2.3f); val s = 1.2f + az.entre(0f, 0.5f)
                    drawRect(p.sombra(lerp(C.FIELTRO, C.ESTEPA, 0.35f)), Offset(c0.x - s, c0.y - s * 0.8f), Size(s * 2f, s * 1.6f))
                    drawRect(p.luz(lerp(C.FIELTRO, C.ESTEPA, 0.2f)), Offset(if (luzIzq) c0.x - s else c0.x, c0.y - s * 0.8f), Size(s, s * 1.6f))
                }
            }
            Pieza.ODRECILLO -> {
                // Un odrecillo de cuero, el de llevar en la silla: plano, de cuello estrecho con su tapón, la costura que lo
                // rodea con su pespunte y la correa de la que cuelga.
                val c0 = q + Offset(-1f, 3f)
                drawPath(Path().apply { moveTo(q); cubicTo(q + Offset(-w * 0.5f, 1f), c0 + Offset(-w * 0.6f, 2f), c0 + Offset(-w * 0.45f, h * 0.4f)) }, p.medio(C.CUERO), style = Stroke(0.7f))
                val cuerpo = Path().apply { moveTo(c0 + Offset(-w * 0.12f, 1.4f)); cubicTo(c0 + Offset(-w * 0.62f, h * 0.2f), c0 + Offset(-w * 0.58f, h), c0 + Offset(0f, h)); cubicTo(c0 + Offset(w * 0.58f, h), c0 + Offset(w * 0.62f, h * 0.2f), c0 + Offset(w * 0.12f, 1.4f)); close() }
                drawPath(cuerpo, lados(C.CUERO, c0.x - w * 0.6f, c0.x + w * 0.6f))
                drawPath(Path().apply { moveTo(c0 + Offset(-w * 0.12f, 2.6f)); cubicTo(c0 + Offset(-w * 0.48f, h * 0.25f), c0 + Offset(-w * 0.45f, h * 0.88f), c0 + Offset(0f, h * 0.88f)); cubicTo(c0 + Offset(w * 0.45f, h * 0.88f), c0 + Offset(w * 0.48f, h * 0.25f), c0 + Offset(w * 0.12f, 2.6f)) }, p.luz(lerp(C.CUERO, C.ORO, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.5f))
                drawOval(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.28f * (1f - noche)), Color.Transparent), c0 + Offset(if (luzIzq) -w * 0.22f else w * 0.22f, h * 0.45f), w * 0.3f), Offset(c0.x - w * 0.5f, c0.y + h * 0.2f), Size(w, h * 0.5f))
                drawRect(p.medio(C.CUERO), Offset(c0.x - w * 0.12f, c0.y - 0.4f), Size(w * 0.24f, 2f))
                drawRect(p.medio(C.MADERA_OSC), Offset(c0.x - w * 0.08f, c0.y - 1.6f), Size(w * 0.16f, 1.4f))
            }
            Pieza.KHADAG -> {
                // Una cinta de seda azul (o blanca), atada con un nudo, con las dos puntas que ondean con el viento.
                for ((dx, largo) in listOf(-0.6f to h, 0.8f to h * 0.78f)) {
                    val a = q + Offset(dx, 0.6f); val b = a + Offset(tuerce * 2f + 1.2f, largo)
                    val cinta = Path().apply { moveTo(a); cubicTo(a + Offset(w * 0.6f + tuerce, largo * 0.3f), b + Offset(-w * 0.8f, -largo * 0.35f), b); lineTo(b + Offset(w * 0.55f, -0.4f)); cubicTo(b + Offset(-w * 0.2f, -largo * 0.35f), a + Offset(w * 1.2f + tuerce, largo * 0.3f), a + Offset(w * 0.5f, 0f)); close() }
                    drawPath(cinta, LuzVerano.pintado(p, c.color, if (dx < 0f == luzIzq) 0.7f else -0.4f))
                }
                drawCircle(p.sombra(c.color), 0.9f, q + Offset(0.2f, 0.8f))
            }
        }
    }
}
