package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Camino
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.runa
import io.github.ruddy2003.vestigium.ui.escenas.RefugiosAntiguas.refugioDeArn
import io.github.ruddy2003.vestigium.ui.escenas.RefugiosAntiguas.refugioPiedraPartida
import io.github.ruddy2003.vestigium.ui.escenas.RefugiosAntiguas.filaDeMulas
import io.github.ruddy2003.vestigium.ui.escenas.RefugiosAntiguas.nieve
import io.github.ruddy2003.vestigium.ui.escenas.RefugiosAntiguas.cascada
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIEDRAS
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIE_CARA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.PIE_DERECHA
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.TORRENTE
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.ZETAS
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.alturaEn
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * La ladera del paso, lo que se ve «desde la puerta de la posada»: «la ladera de los alerces, la pedrera, la cornisa y,
 * arriba, el collado», cuatro tramos que se distinguen por materia y color: abajo, los alerces, verdes y plumosos; en
 * medio, la pedrera, gris, de piedra suelta; arriba, la cornisa, una banda de roca con el escarpe en sombra y la repisa
 * del camino encima; y el collado. El camino sube en zetas, de tierra pisada más clara que la ladera, con su talud, del
 * puente al collado; en cada vuelta una piedra marcada (un mojón de piedra con su runa).
 */
internal object PasoAntiguas {
    private val C = ColorAntiguas
    const val LEJOS = 0.2f

    /** La forma de la ladera: del pie de la cara norte al collado, del collado a detrás de la posada y, abajo, la orilla de allá del torrente. */
    val FORMA: Path by lazy {
        val orilla = TORRENTE.filter { it.first.x in 150f..360f }.map { (q, w) -> q + Offset(0f, -w - 0.6f) }
        poligono(PIE_CARA + PIE_DERECHA.drop(1) + listOf(Offset(360f, 192f)) + orilla)
    }

    /** La cornisa: su canto de arriba (la repisa por donde va el camino) y el de abajo (el pie del escarpe). */
    val CORNISA_ARRIBA = pts(172f, 146f, 189f, 140.4f, 210f, 135f, 230f, 130.4f, 250f, 125.4f, 268f, 120.8f, 286f, 115.6f, 305f, 111.4f, 322f, 114.4f, 340f, 119.6f, 360f, 124f)
    private val CORNISA_ABAJO = pts(170f, 156f, 188f, 150.4f, 208f, 147.4f, 228f, 143.6f, 250f, 138.2f, 270f, 133.4f, 290f, 128.6f, 310f, 126f, 330f, 128f, 360f, 134f)

    /** La pedrera: el abanico de piedra suelta que baja de la cornisa, gris, hasta los primeros alerces. */
    private val PEDRERA = pts(166f, 160f, 182f, 152f, 204f, 148.6f, 228f, 145f, 252f, 139.4f, 280f, 132f, 320f, 130f, 360f, 136f, 360f, 166f, 300f, 168f, 262f, 167.4f, 244f, 164f, 226f, 166.4f, 206f, 163.6f, 188f, 166.8f, 172f, 168f)

    fun DrawScope.paso(v: VistaAntiguas) {
        val az = fijo("paso")
        clipPath(FORMA) {
            suelo(v, az)
            pedrera(v, az)
            cornisa(v, az)
            val zanja = v.camino == Camino.ZANJA
            if (!zanja) camino(v)
            alerces(v, az)
            if (!zanja) piedras(v)
            refugioPiedraPartida(v)
            nieve(v, az)
            // Recién abierto, la zanja y sus piedras se abren en la nieve: van encima.
            if (zanja) { camino(v); piedras(v) }
            filaDeMulas(v)
        }
        refugioDeArn(v)
        cascada(v, az)
    }

    // ---------------------------------------------------------------- El suelo

    /** La hierba de la ladera, con los lomos anchos que dan la cara al sol de la mañana o al de la tarde. */
    private fun DrawScope.suelo(v: VistaAntiguas, az: Azar) {
        val p = v.p; val hierba = EscenaAntiguas.hierba(v.m)
        val roca = lerp(C.ROCA, C.PIEDRA, 0.4f)
        drawRect(Brush.verticalGradient(0f to p.medio(roca, LEJOS), 0.3f to p.medio(lerp(roca, hierba, 0.35f), LEJOS), 0.55f to p.medio(lerp(hierba, C.ALERCE, 0.2f), LEJOS), 1f to p.medio(lerp(hierba, C.PINO, 0.35f), LEJOS * 0.8f), startY = 108f, endY = 212f), Offset(150f, 104f), Size(212f, 112f))
        // Los lomos: el de la izquierda mira a la derecha (al sol de la mañana), el de la derecha a la izquierda.
        val lomos = listOf(pts(178f, 168f, 196f, 178f, 206f, 194f, 210f, 212f) to V3(0.55f, 0.45f, -0.7f), pts(226f, 166f, 242f, 178f, 250f, 196f, 254f, 212f) to V3(-0.5f, 0.45f, -0.74f))
        for ((eje, n) in lomos) {
            val t = ((v.sol.t(n) - 0.3f) * 1.4f).coerceIn(-1f, 1f)
            val forma = TrazosValle.cerrada(eje + eje.reversed().map { it + Offset(20f, 4f) })
            drawPath(forma, (if (t > 0f) p.luz(hierba, LEJOS) else p.sombra(hierba, LEJOS)).copy(alpha = 0.3f * kotlin.math.abs(t)))
        }
        // La hierba: pinceladas cortas con su luz, más grandes abajo; matas de rododendro y de enebro en manchas.
        val claros = ArrayList<Offset>(); val oscuros = ArrayList<Offset>()
        repeat(360) { val q = Offset(az.entre(160f, 300f), az.entre(160f, 210f)); if (az.real() < 0.5f) claros += q else oscuros += q }
        drawPoints(claros, PointMode.Points, p.luz(lerp(hierba, C.ORO, 0.2f), LEJOS).copy(alpha = 0.45f), 1f, StrokeCap.Round)
        drawPoints(oscuros, PointMode.Points, p.sombra(lerp(hierba, C.PINO, 0.4f), LEJOS).copy(alpha = 0.45f), 1f, StrokeCap.Round)
        repeat(14) {
            val q = Offset(az.entre(166f, 262f), az.entre(160f, 205f)); val w = az.entre(4f, 9f)
            drawPath(borron(q, w, w * 0.32f, az, 9, 0.35f), p.sombra(lerp(hierba, C.PINO, 0.55f), LEJOS).copy(alpha = 0.5f))
        }
    }

    /** La pedrera: piedra suelta, gris y clara, con sus bloques grandes, sus regueros y el borde de abajo comido por la hierba. */
    private fun DrawScope.pedrera(v: VistaAntiguas, az: Azar) {
        val p = v.p; val roca = lerp(C.ROCA, C.PIEDRA, 0.5f)
        val forma = TrazosValle.cerrada(PEDRERA)
        drawPath(forma, Brush.verticalGradient(listOf(p.por(lerp(roca, C.NIEVE, 0.16f), 0.15f, LEJOS), p.por(roca, -0.05f, LEJOS)), 130f, 168f))
        clipPath(forma) {
            // Los regueros por donde baja la piedra, en sombra del lado contrario al sol, en abanico desde la cornisa.
            val lado = if (p.solALaDerecha) -1f else 1f
            for (x in listOf(184f, 204f, 226f, 248f, 270f)) {
                val r = rota(pts(x + 4f, 140f, x, 150f, x - 5f, 168f), az, 1.2f, 2)
                drawPath(curva(r.map { it + Offset(lado * 1.2f, 0f) }), p.sombra(roca, LEJOS).copy(alpha = 0.35f), style = Stroke(2.2f))
                drawPath(curva(r), p.luz(lerp(roca, C.NIEVE, 0.3f), LEJOS).copy(alpha = 0.3f), style = Stroke(0.8f))
            }
            val piedras = (0 until 300).map { Offset(az.entre(166f, 300f), az.entre(132f, 170f)) }
            drawPoints(piedras, PointMode.Points, p.sombra(lerp(roca, C.ESCORIA, 0.3f), LEJOS).copy(alpha = 0.6f), 1.2f, StrokeCap.Round)
            drawPoints(piedras.map { it + Offset(if (p.solALaDerecha) 0.45f else -0.45f, -0.45f) }, PointMode.Points, p.luz(lerp(roca, C.NIEVE, 0.3f), LEJOS).copy(alpha = 0.55f), 0.9f, StrokeCap.Round)
            // Unos bloques grandes caídos, con su cara de luz y su sombra.
            for (q in listOf(Offset(196f, 160f), Offset(222f, 152f), Offset(258f, 158f), Offset(178f, 164f), Offset(246f, 146f))) {
                val w = az.entre(2.6f, 4.2f)
                drawPath(poligono(q + Offset(-w, 0.5f), q + Offset(w, 0.5f), q + Offset(w * 0.7f, -w * 0.9f), q + Offset(-w * 0.4f, -w * 1.1f)), p.por(roca, v.sol.t(V3(0.4f, 0.5f, -0.75f)), LEJOS))
                drawPath(poligono(q + Offset(-lado * w, 0.5f), q + Offset(0f, 0.5f), q + Offset(-lado * w * 0.1f, -w * 1.05f), q + Offset(-lado * w * 0.45f, -w * 1.05f)), p.sombra(roca, LEJOS).copy(alpha = 0.7f))
            }
        }
        // El borde de abajo, donde la hierba se mete entre las piedras.
        repeat(40) { val x = az.entre(168f, 296f); val y = alturaEn(PEDRERA.subList(PEDRERA.size / 2, PEDRERA.size).reversed(), x) - az.entre(0f, 4f); drawLine(p.por(lerp(EscenaAntiguas.hierba(v.m), C.PINO, 0.3f), 0f, LEJOS).copy(alpha = 0.6f), Offset(x, y), Offset(x + az.entre(-1f, 1f), y - az.entre(1.5f, 3f)), 0.7f) }
    }

    /** La cornisa: la banda de roca, con el escarpe en sombra, sus grietas y su musgo, y la repisa del camino encima. */
    private fun DrawScope.cornisa(v: VistaAntiguas, az: Azar) {
        val p = v.p; val roca = lerp(C.ROCA, C.PIEDRA_OSC, 0.25f)
        val banda = poligono(rota(CORNISA_ARRIBA, az, 0.5f, 2) + rota(CORNISA_ABAJO, az, 1.1f, 2).reversed())
        val t = v.sol.t(V3(-0.15f, 0.1f, -1f))
        drawPath(banda, Brush.verticalGradient(listOf(p.por(roca, t - 0.1f, LEJOS), p.por(lerp(roca, C.ESCORIA, 0.25f), t - 0.35f, LEJOS)), 110f, 156f))
        clipPath(banda) {
            // Las grietas verticales del escarpe, cada una con su filo de luz; el musgo y el agua que baja.
            var x = 172f
            while (x < 340f) {
                val y0 = alturaEn(CORNISA_ARRIBA, x) + 1.5f; val y1 = alturaEn(CORNISA_ABAJO, x)
                drawLine(p.sombra(lerp(roca, C.ESCORIA, 0.5f), LEJOS).copy(alpha = 0.75f), Offset(x, y0), Offset(x + az.entre(-1.5f, 1.5f), y1), az.entre(0.6f, 1.2f))
                drawLine(p.luz(roca, LEJOS).copy(alpha = 0.3f * p.r.dia), Offset(x + 0.8f, y0), Offset(x + 0.8f + az.entre(-1f, 1f), y1), 0.5f)
                x += az.entre(4f, 9f)
            }
            repeat(10) { val xx = az.entre(176f, 330f); drawCircle(p.medio(lerp(C.MUSGO, C.ALERCE, 0.4f), LEJOS).copy(alpha = 0.55f), az.entre(0.8f, 1.6f), Offset(xx, alturaEn(CORNISA_ABAJO, xx) - az.entre(1f, 4f))) }
        }
        // La repisa: un filo de luz, y la roca de encima (el pie de la cara norte) con su sombra sobre el camino.
        drawPath(curva(CORNISA_ARRIBA), p.luz(lerp(roca, C.NIEVE, 0.3f), LEJOS).copy(alpha = 0.55f), style = Stroke(1f))
        val pie = PIE_CARA.filter { it.x > 186f }
        drawPath(poligono(pie + CORNISA_ARRIBA.filter { it.x > 186f && it.x < 306f }.reversed().map { it + Offset(0f, -2.4f) }), p.sombra(lerp(roca, C.ESCORIA, 0.2f), LEJOS).copy(alpha = 0.55f))
    }

    // ---------------------------------------------------------------- El camino

    /** El ancho del camino a la altura [y]: más ancho cerca, fino arriba. */
    fun ancho(y: Float): Float = 1.1f + ((y - 108f) / 92f).coerceIn(0f, 1f) * 3.2f

    /**
     * El camino en zetas: una cinta de tierra pisada más clara que la ladera, con el talud de arriba (el corte, en sombra o
     * al sol) y el canto de abajo en sombra; continuo del puente al collado. Recién abierto, una zanja azul en la nieve con
     * huellas; el día que se cierra, solo abajo.
     */
    private fun DrawScope.camino(v: VistaAntiguas) {
        val p = v.p; val tierra = lerp(C.TIERRA, C.LANA, 0.5f)
        val hasta = when (v.camino) { Camino.ABIERTO, Camino.ZANJA -> 1f; Camino.TAPANDOSE -> 0.35f; Camino.BORRADO -> return }
        val zanja = v.camino == Camino.ZANJA
        val talud = if (v.sol.t(V3(0f, 0.2f, -1f)) > 0.2f) p.luz(lerp(C.TIERRA, C.LANA, 0.2f), LEJOS) else p.sombra(lerp(C.TIERRA, C.ESCORIA, 0.2f), LEJOS)
        for (i in 0 until ZETAS.size - 1) {
            val a = ZETAS[i]; val b = ZETAS[i + 1]
            val f = i / (ZETAS.size - 1f); if (f > hasta) break
            val w = ancho((a.y + b.y) / 2f)
            val alfa = if (v.camino == Camino.TAPANDOSE) 1f - f / hasta * 0.7f else 1f
            val col = if (zanja) p.sombra(lerp(C.NIEVE, C.SOMBRA, 0.3f), LEJOS) else p.por(tierra, 0.3f, LEJOS)
            // El canto de abajo en sombra, el talud de arriba y la cinta del camino.
            drawLine(p.sombra(lerp(C.TIERRA, C.ESCORIA, 0.4f), LEJOS).copy(alpha = 0.6f * alfa), a + Offset(0f, w * 0.55f), b + Offset(0f, w * 0.55f), w * 0.55f, StrokeCap.Round)
            if (!zanja) drawLine(talud.copy(alpha = 0.7f * alfa), a + Offset(0f, -w * 0.62f), b + Offset(0f, -w * 0.62f), w * 0.45f, StrokeCap.Round)
            drawLine(col.copy(alpha = alfa), a, b, w, StrokeCap.Round)
            if (!zanja) drawLine(p.luz(lerp(tierra, C.NIEVE, 0.3f), LEJOS).copy(alpha = 0.5f * alfa * p.r.dia), a + Offset(0f, -w * 0.15f), b + Offset(0f, -w * 0.15f), w * 0.3f, StrokeCap.Round)
            // Las huellas de los que subieron: en la zanja, siempre; en la travesía, las de la compañía de verano.
            if (zanja || v.lugar == EscenaAntiguas.Lugar.TRAVESIA || Senal.PEREGRINOS in v.senales) {
                val pasos = ((b - a).getDistance() / (2.2f * w + 0.6f)).toInt().coerceAtLeast(2)
                val col2 = p.sombra(if (zanja) C.NIEVE_SOMBRA else lerp(tierra, C.ESCORIA, 0.4f), LEJOS).copy(alpha = 0.6f)
                for (k in 0 until pasos) { val q = a + (b - a) * ((k + 0.5f) / pasos) + Offset(0f, if (k % 2 == 0) -w * 0.18f else w * 0.18f); drawCircle(col2, w * 0.18f, q) }
            }
        }
    }

    /** Las piedras marcadas: un mojón de piedra gris en cada vuelta, con su runa en la de cara; los hitos que el ojo sigue hasta el collado. */
    private fun DrawScope.piedras(v: VistaAntiguas) {
        val n = EscenaAntiguas.piedrasALaVista(v.m); if (n == 0) return
        val az = fijo("piedras")
        val frescas = Senal.RUNAS_FRESCAS in v.senales
        for ((i, pr) in PIEDRAS.take(n).withIndex()) {
            val (pie, runa) = pr
            val k = 0.5f + ((pie.y - 108f) / 90f).coerceIn(0f, 1f) * 1.25f
            mojon(v.p, pie, k, runa, az, LEJOS, gastada = if (i == PIEDRAS.size - 1) 0.45f else 0f, fresca = if (frescas) 0.4f else 0f, nieve = v.nieveLadera, primera = i == PIEDRAS.size - 1 && Senal.PRIMERA_RUNA in v.senales)
        }
    }

    /**
     * Un mojón en [pie], de tamaño [k] (1 ≈ 8 unidades de alto): una piedra alzada, ancha y tosca, gris, con otra más chica
     * encima; su lado de luz y su lado de sombra, el liquen, su sombra en el suelo y su runa tallada en la cara. [primera]:
     * la de la Primera Runa, repasada y untada de sebo, con su cinta.
     */
    fun DrawScope.mojon(p: Pincel, pie: Offset, k: Float, runa: Runa, az: Azar, lejos: Float, gastada: Float = 0f, fresca: Float = 0f, nieve: Float = 0f, primera: Boolean = false) {
        val w = 2.8f * k; val h = 6f * k
        val lado = if (p.solALaDerecha) 1f else -1f
        val piedra = lerp(C.PIEDRA, C.LANA, 0.12f)
        drawOval(p.r.color.copy(alpha = 0.45f * p.r.fuerza), pie + Offset(-w - lado * w * 0.9f, -0.6f * k), Size(w * 3f, 1.4f * k), blendMode = BlendMode.Multiply)
        val base = poligono(pie + Offset(-w * 0.62f, 0.4f), pie + Offset(w * 0.66f, 0.4f), pie + Offset(w * 0.56f, -h * 0.7f), pie + Offset(w * 0.12f, -h * 0.8f), pie + Offset(-w * 0.52f, -h * 0.74f))
        drawPath(base, p.por(piedra, 0.12f, lejos))
        drawPath(poligono(pie + Offset(-lado * w * 0.62f, 0.4f), pie + Offset(-lado * w * 0.08f, 0.4f), pie + Offset(-lado * w * 0.04f, -h * 0.78f), pie + Offset(-lado * w * 0.52f, -h * 0.74f)), p.sombra(piedra, lejos).copy(alpha = 0.85f))
        // La piedra de encima, más chica y ladeada.
        val cima = poligono(pie + Offset(-w * 0.4f, -h * 0.72f), pie + Offset(w * 0.42f, -h * 0.76f), pie + Offset(w * 0.3f, -h * 1.02f), pie + Offset(-w * 0.26f, -h))
        drawPath(cima, p.por(lerp(piedra, C.NIEVE, 0.12f), 0.3f, lejos))
        drawLine(p.sombra(piedra, lejos), pie + Offset(-w * 0.4f, -h * 0.72f), pie + Offset(w * 0.42f, -h * 0.76f), 0.5f * k)
        repeat(3) { drawCircle(p.medio(C.LIQUEN, lejos).copy(alpha = 0.7f), az.entre(0.25f, 0.5f) * k, pie + Offset(az.entre(-0.4f, 0.4f) * w, -az.entre(0.1f, 0.7f) * h)) }
        if (k > 0.75f) runa(p, runa, pie + Offset(lado * w * 0.14f, -h * 0.4f), h * 0.4f, piedra, lejos, fresca = if (primera) 1f else fresca, gastada = gastada)
        if (primera) { drawLine(p.por(C.RUBI, 0.3f, lejos), pie + Offset(-w * 0.55f, -h * 0.2f), pie + Offset(w * 0.62f, -h * 0.24f), 0.5f * k); drawLine(p.luz(C.ORO, lejos).copy(alpha = 0.5f), pie + Offset(-w * 0.4f, -h * 0.72f), pie + Offset(w * 0.3f, -h * 0.7f), 0.4f * k) }
        if (nieve > 0.25f) drawPath(poligono(pie + Offset(-w * 0.32f, -h * 0.98f), pie + Offset(w * 0.32f, -h * 1.04f), pie + Offset(w * 0.45f, -h * 0.82f), pie + Offset(-w * 0.42f, -h * 0.8f)), p.por(C.NIEVE, 0.5f, lejos).copy(alpha = nieve))
    }

    // ---------------------------------------------------------------- Los alerces

    /** Los alerces de la ladera baja: filas de árboles plumosos, cada uno con su tamaño y su color, con claros donde pasa el camino. */
    private fun DrawScope.alerces(v: VistaAntiguas, az: Azar) {
        val agujas = EscenaAntiguas.agujas(v.m); val amarillo = EscenaAntiguas.amarillo(v.m)
        val arboles = ArrayList<Offset>()
        repeat(170) {
            val x = az.entre(160f, 300f); val y = az.entre(160f, 212f)
            if (y > SitioAntiguas.torrenteEn(x) - 4f) return@repeat
            if (y < alturaEn(PEDRERA.subList(PEDRERA.size / 2, PEDRERA.size).reversed(), x) + 3f) return@repeat
            if (cercaDelCamino(Offset(x, y), 3.6f)) return@repeat
            arboles += Offset(x, y)
        }
        for (q in arboles.sortedBy { it.y }) {
            val alto = 5f + (q.y - 156f) * 0.24f + az.entre(-1f, 2f)
            alerce(v, q, alto, agujas, (amarillo * (0.75f + 0.5f * ((210f - q.y) / 44f)) + az.entre(-0.15f, 0.15f)).coerceIn(0f, 1f), az.real())
        }
    }

    /** Si [q] está a menos de [margen] unidades del camino (para dejar el claro). */
    private fun cercaDelCamino(q: Offset, margen: Float): Boolean {
        for (i in 0 until ZETAS.size - 1) {
            val a = ZETAS[i]; val b = ZETAS[i + 1]; val ab = b - a; val t = (((q - a).x * ab.x + (q - a).y * ab.y) / (ab.x * ab.x + ab.y * ab.y)).coerceIn(0f, 1f)
            if ((a + ab * t - q).getDistance() < margen + ancho(q.y)) return true
        }
        return false
    }

    /**
     * Un alerce de [alto] unidades: un huso de pisos plumosos (no un triángulo), con su lado de luz y de sombra; con poca
     * aguja, las ramas grises asoman; amarillo «de arriba abajo» en otoño.
     */
    private fun DrawScope.alerce(v: VistaAntiguas, pie: Offset, alto: Float, agujas: Float, amarillo: Float, tono: Float) {
        val p = v.pincelEn(pie); val lado = if (p.solALaDerecha) 1f else -1f
        val verde = lerp(lerp(C.ALERCE, C.PINO, 0.25f + 0.25f * tono), C.ORO, amarillo)
        val w = alto * 0.34f
        val tronco = p.medio(lerp(C.MADERA, C.PIEDRA, 0.4f), LEJOS)
        drawLine(tronco, pie, pie + Offset(0f, -alto), 0.5f, StrokeCap.Round)
        drawOval(p.r.color.copy(alpha = 0.35f * p.r.fuerza), pie + Offset(-w - lado * alto * 0.25f, -0.5f), Size(w * 2f + alto * 0.3f, 1.3f), blendMode = BlendMode.Multiply)
        if (agujas < 0.1f) {
            for (j in 1..4) { val y = pie.y - alto * j / 5f; val r = w * (1f - j / 5.5f); drawLine(tronco.copy(alpha = 0.7f), Offset(pie.x, y - 0.4f), Offset(pie.x - r, y + 0.8f), 0.3f); drawLine(tronco.copy(alpha = 0.7f), Offset(pie.x, y - 0.4f), Offset(pie.x + r, y + 0.8f), 0.3f) }
            return
        }
        val forma = Path().apply {
            moveTo(pie.x, pie.y - alto); val pisos = 4
            for (j in 1..pisos) { val y = pie.y - alto + alto * j / pisos * 0.88f; val r = w * (0.25f + 0.75f * j / pisos); lineTo(pie.x + r, y + 0.6f); lineTo(pie.x + r * 0.55f, y - 0.4f) }
            lineTo(pie.x + w * 0.3f, pie.y - alto * 0.12f); lineTo(pie.x - w * 0.3f, pie.y - alto * 0.12f)
            for (j in pisos downTo 1) { val y = pie.y - alto + alto * j / pisos * 0.88f; val r = w * (0.25f + 0.75f * j / pisos); lineTo(pie.x - r * 0.55f, y - 0.4f); lineTo(pie.x - r, y + 0.6f) }
            close()
        }
        val a = (0.35f + 0.65f * agujas)
        drawPath(forma, p.medio(verde, LEJOS).copy(alpha = a))
        clipPath(forma) { drawRect(p.luz(lerp(verde, C.LANA, 0.15f), LEJOS).copy(alpha = 0.6f * a), Offset(if (lado > 0f) pie.x else pie.x - w, pie.y - alto), Size(w, alto)); drawRect(p.sombra(verde, LEJOS).copy(alpha = 0.55f * a), Offset(if (lado > 0f) pie.x - w else pie.x, pie.y - alto), Size(w, alto)) }
    }
}
