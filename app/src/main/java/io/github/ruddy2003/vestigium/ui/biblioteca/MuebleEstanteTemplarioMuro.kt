package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sqrt

/**
 * La pared del estante de Templario ([MuebleEstanteTemplario]): la esquina de la cámara alta de la torre. Al fondo, el enlucido
 * de cal con su despiece fingido de sillares a la almagra (en doble línea, gastado), la cenefa de dientes de sierra bajo la viga
 * carrera con sus canecillos, el hollín encima de la lámpara, una mancha de humedad, grietas y dos desconchones donde asoma la
 * caliza dorada; a la izquierda, el costado en escorzo con el ajimez (dos luces de medio punto con su columnilla, el hondo del
 * muro y el postigo de cedro abierto), por donde de día entra el sol que deja en la cal la mancha de las dos luces.
 */
internal object MuroEstanteTemplario {
    private val P = PaletaTemplario

    /** La esquina con el costado del ajimez, en anchos. */
    const val ESQUINA = 0.15f
    /** El bajo de la viga carrera en la pared del fondo, en anchos, y el de la cenefa. */
    const val VIGA = 0.044f
    const val CENEFA = 0.084f
    /** La altura de los ojos (para el escorzo del costado), en anchos. */
    private const val OJOS = 0.72f

    /** Un punto del costado: [p] de la esquina (0) hacia quien mira (1), a la altura [y] que tendría en la esquina. */
    fun enCostado(w: Float, p: Float, y: Float): Offset {
        val x = ESQUINA * (1f - p * (0.85f + 0.15f * p))
        val yy = OJOS + (y - OJOS) * (1f + 0.16f * p)
        return Offset(x * w, yy * w)
    }

    /** Un punto del ajimez: [fx] de lejos (0, junto a la esquina) a cerca (1), [fy] de arriba (0) a abajo (1). */
    fun enAjimez(w: Float, fx: Float, fy: Float): Offset = enCostado(w, 0.16f + 0.62f * fx, 0.112f + 0.25f * fy)

    /** El color de la cal en el plano del fondo, según la luz del momento ([lit]: dentro de la mancha del ajimez). */
    private fun cal(a: AireTemplario, c: Color, k: Float, lit: Float, m: ManchaTemplario?): Color = a.en(c, 0f, k, lit, m)

    // ---------------------------------------------------------------- la pared entera (arriba)

    fun DrawScope.pared(a: AireTemplario, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width
        val fondo = Rect(w * ESQUINA, 0f, w, apoyo + w * 0.02f)
        // La cal de fondo, con la pintura encima.
        baseCal(fondo, a, cerca, null)
        pintura(fondo, a, cerca, null)
        // La mancha del ajimez: la misma pared, alumbrada, dentro de las dos luces.
        if (m != null) {
            clipRect(fondo.left, fondo.top, fondo.right, fondo.bottom) {
                // La penumbra, una franja angosta a media luz, y la mancha entera.
                val medio = ManchaTemplario(m.q, m.color, m.fuerza * 0.45f)
                clipPath(m.forma(w, 0f, 0.035f)) { baseCal(fondo, a, cerca, medio); pintura(fondo, a, cerca, medio) }
                clipPath(m.forma(w)) { baseCal(fondo, a, cerca, m); pintura(fondo, a, cerca, m) }
            }
        }
        // La sombra de la esquina en la pared del fondo.
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.16f), Offset(w * ESQUINA, 0f), Size(w * 0.012f, fondo.bottom))
        drawRect(a.sombra(if (a.lumbre) 0.22f else 0.08f), Offset(w * ESQUINA + w * 0.012f, 0f), Size(w * 0.03f, fondo.bottom))
        costado(a, apoyo, cerca, m)
        viga(a, cerca, m)
    }

    /** La cal lisa de [r]: de día, más clara junto al ajimez; de noche, encendida alrededor de la lámpara y hundida lejos. */
    private fun DrawScope.baseCal(r: Rect, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width
        if (m != null && !a.oscuro) {
            // Dentro de la mancha: la cal alumbrada, más fuerte arriba (junto al ajimez) y algo menos abajo.
            val c0 = a.en(mezcla(P.CAL, P.CAL_SOMBRA, 0.3f), 0f, 1f, 1f, m); val c1 = a.en(mezcla(P.CAL, P.CAL_SOMBRA, 0.5f), 0f, 1f, 1f, m)
            drawRect(Brush.verticalGradient(listOf(c0, c1), m.en(0f, 0f).y * w, m.en(1f, 1f).y * w), r.topLeft, r.size)
            return
        }
        if (a.lumbre && a.oscuro) {
            // El charco de la lámpara: el color de la cal a cada distancia de la llama (un solo degradado, en la imagen guardada).
            val c = Offset(w * MuebleEstanteTemplario.LAMP_X, w * MuebleEstanteTemplario.LAMP_Y)
            val radios = floatArrayOf(0f, 0.05f, 0.11f, 0.18f, 0.27f, 0.38f, 0.52f, 0.7f, 0.9f)
            val col = radios.map { rr -> a.luz(mezcla(P.CAL, P.CAL_SOMBRA, 0.2f), MuebleEstanteTemplario.lamparaArriba(rr, 0f, a.noche)) }
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 0.9f to col[i] }, center = c, radius = w * 0.9f), r.topLeft, r.size)
            // La luna se suma a la cal que ya alumbra la lámpara: un plata frío, solo dentro de la mancha.
            if (m != null) drawRect(Color(0xFF8EA4D6).copy(alpha = (0.62f * m.fuerza).coerceAtMost(1f)), r.topLeft, r.size, blendMode = BlendMode.Screen)
            return
        }
        // De día (y al ocaso): la cal a la sombra del cuarto, más viva junto al ajimez y más honda arriba bajo la viga.
        val claro = a.dia(mezcla(P.CAL, P.CAL_SOMBRA, 0.42f), 0f)
        val hondo = a.dia(mezcla(P.CAL_SOMBRA, P.CALIZA_OSC, 0.12f), 0f)
        drawRect(Brush.horizontalGradient(listOf(claro, mezcla(claro, hondo, 0.5f), hondo), r.left, r.right), r.topLeft, r.size)
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.16f), a.sombra(0f)), r.top, r.top + w * 0.16f), r.topLeft, Size(r.width, w * 0.16f))
    }

    /**
     * La pintura de la cal (con mezcla de multiplicar, así sigue la luz que tenga debajo): el despiece fingido en doble línea de
     * almagra, la cenefa de dientes de sierra, el hollín de la lámpara, la mancha de humedad y las grietas; y los desconchones,
     * donde asoma la caliza.
     */
    private fun DrawScope.pintura(r: Rect, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val ocre = Color(0xFFB4533B)
        // La cal no es lisa: el moteado de las manos de cal (más oscuras, con multiplicar) y los brochazos a lo largo.
        val motas = Path()
        for (j in 0 until 18) {
            val mx = r.left + r.width * azar(9050, j); val my = w * (CENEFA + 0.01f) + (r.bottom - w * CENEFA) * azar(9051, j)
            val mw = w * (0.08f + 0.12f * azar(9052, j)); val mh = mw * (0.3f + 0.25f * azar(9053, j))
            motas.addOval(Rect(mx - mw / 2, my - mh / 2, mx + mw / 2, my + mh / 2))
        }
        drawPath(motas, Color(0xFFF0E6D4).copy(alpha = 0.3f), blendMode = BlendMode.Multiply)
        val brochazos = Path()
        for (j in 0 until 22) {
            val bx = r.left + r.width * azar(9060, j); val by = w * (CENEFA + 0.02f) + (r.bottom - w * CENEFA) * azar(9061, j)
            val bl = w * (0.08f + 0.12f * azar(9062, j))
            brochazos.moveTo(bx, by); brochazos.quadraticTo(bx + bl * 0.5f, by - w * 0.006f, bx + bl, by + w * 0.002f)
        }
        drawPath(brochazos, Color(0xFFEAE0CC).copy(alpha = 0.4f), style = Stroke(w * 0.005f, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        // El despiece: hiladas de 0.072 anchos desde la cenefa, juntas verticales al tresbolillo.
        val lineas = Path()
        val hc = w * 0.074f
        var y = w * CENEFA + w * 0.012f
        var fila = 0
        while (y < r.bottom) {
            // La doble línea de la hilada, a trozos (la brocha se corta y la cal se ha gastado).
            var x = r.left
            var j = 0
            while (x < r.right) {
                val largo = w * (0.06f + 0.12f * azar(9100 + fila, j))
                val hueco = if (azar(9200 + fila, j) > 0.8f) w * 0.012f else 0f
                val x1 = minOf(x + largo, r.right)
                val dy = (azar(9300 + fila, j) - 0.5f) * 0.8f * u
                lineas.moveTo(x, y + dy); lineas.lineTo(x1, y + dy * 0.4f)
                lineas.moveTo(x, y + 2.4f * u + dy); lineas.lineTo(x1, y + 2.4f * u + dy * 0.4f)
                x = x1 + hueco; j++
            }
            // Las juntas verticales de la hilada de debajo.
            val y1 = minOf(y + hc, r.bottom)
            var xv = r.left + (if (fila % 2 == 0) w * 0.08f else w * 0.19f)
            var q = 0
            while (xv < r.right) {
                val inc = (azar(9400 + fila, q) - 0.5f) * 1.2f * u
                lineas.moveTo(xv, y + 2.4f * u); lineas.lineTo(xv + inc, y1)
                lineas.moveTo(xv + 2.4f * u, y + 2.4f * u); lineas.lineTo(xv + 2.4f * u + inc, y1)
                xv += w * (0.2f + 0.04f * azar(9500 + fila, q)); q++
            }
            y += hc; fila++
        }
        clipRect(r.left, w * CENEFA, r.right, r.bottom) {
            drawPath(lineas, ocre.copy(alpha = 0.78f), style = Stroke(1.1f * u), blendMode = BlendMode.Multiply)
        }
        // La cenefa: una banda de almagra con dientes de sierra en cal, entre dos filetes negros.
        val c0 = w * (VIGA + 0.012f); val c1 = w * CENEFA
        val dientes = Path()
        val paso = w * 0.032f
        var xd = r.left - paso * 0.5f
        dientes.moveTo(xd, c0 + 2.6f * u)
        while (xd < r.right + paso) {
            dientes.lineTo(xd + paso * 0.5f, c1 - 2.6f * u); dientes.lineTo(xd + paso, c0 + 2.6f * u)
            xd += paso
        }
        dientes.lineTo(r.right + paso, c0 + 2.6f * u); dientes.close()
        drawPath(dientes, ocre.copy(alpha = 0.85f), blendMode = BlendMode.Multiply)
        drawRect(Color(0xFF55463C).copy(alpha = 0.85f), Offset(r.left, c0), Size(r.width, 2.4f * u), blendMode = BlendMode.Multiply)
        drawRect(Color(0xFF55463C).copy(alpha = 0.85f), Offset(r.left, c1 - 2.4f * u), Size(r.width, 2.4f * u), blendMode = BlendMode.Multiply)
        // Lo gastado de la cenefa: rozaduras de cal.
        for (j in 0 until 9) {
            val x = r.left + r.width * azar(9600, j)
            drawRect(mezcla(cal(a, P.CAL, cerca(Offset(x, c0)), if (m != null) 1f else 0f, m), Color.Transparent, 0.35f),
                Offset(x, c0 + (c1 - c0) * (0.2f + 0.5f * azar(9601, j))), Size(w * (0.01f + 0.025f * azar(9602, j)), 1.6f * u))
        }
        // El hollín de la lámpara: una lengua parda que sube de la llama y se abre bajo la viga.
        val lx = w * MuebleEstanteTemplario.LAMP_X
        val hollin = Path().apply {
            moveTo(lx - w * 0.008f, w * 0.27f)
            cubicTo(lx - w * 0.03f, w * 0.2f, lx - w * 0.06f, w * 0.14f, lx - w * 0.05f, w * 0.09f)
            lineTo(lx + w * 0.06f, w * 0.09f)
            cubicTo(lx + w * 0.07f, w * 0.14f, lx + w * 0.03f, w * 0.2f, lx + w * 0.008f, w * 0.27f); close()
        }
        drawPath(hollin, Color(0xFF8A7A6E).copy(alpha = 0.16f), blendMode = BlendMode.Multiply)
        drawPath(Path().apply { addPath(hollin, Offset(0f, -w * 0.02f)) }, Color(0xFF8A7A6E).copy(alpha = 0.12f), blendMode = BlendMode.Multiply)
        // La mancha de humedad, abajo a la derecha: dos cercos pardos, sin borde.
        drawOval(Color(0xFFCDBA98).copy(alpha = 0.5f), Offset(w * 0.86f, w * 0.36f), Size(w * 0.13f, w * 0.1f), blendMode = BlendMode.Multiply)
        drawOval(Color(0xFFC0AC88).copy(alpha = 0.45f), Offset(w * 0.9f, w * 0.39f), Size(w * 0.09f, w * 0.08f), blendMode = BlendMode.Multiply)
        // Las grietas finas.
        val grietas = Path()
        listOf(Triple(0.47f, 0.1f, 9800), Triple(0.9f, 0.13f, 9810), Triple(0.62f, 0.3f, 9820)).forEach { (gx, gy, s) ->
            var p = Offset(w * gx, w * gy)
            grietas.moveTo(p.x, p.y)
            for (q in 0 until 6) {
                p = Offset(p.x + w * (azar(s, q) - 0.4f) * 0.02f, p.y + w * (0.008f + 0.012f * azar(s, q + 10)))
                grietas.lineTo(p.x, p.y)
            }
        }
        drawPath(grietas, Color(0xFF6E5E4E).copy(alpha = 0.7f), style = Stroke(0.8f * u, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        // Los desconchones: donde se cayó la cal asoma la caliza de la torre.
        desconchon(Rect(w * 0.43f, w * 0.12f, w * 0.495f, w * 0.155f), 9900, a, cerca, m)
        desconchon(Rect(w * 0.905f, w * 0.215f, w * 0.96f, w * 0.248f), 9910, a, cerca, m)
    }

    /** Un desconchón: el hueco con la caliza y su junta, la sombra que echa el borde de la cal y el canto alumbrado del otro lado. */
    private fun DrawScope.desconchon(r: Rect, s: Int, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val u = density
        val k = cerca(r.center)
        val lit = if (m != null) 1f else 0f
        // El hueco: una mancha redondeada e irregular (no un recorte de papel).
        val hueco = Path()
        val n = 11
        for (j in 0..n) {
            val t = j / n.toFloat() * 6.2832f
            val rr = 0.78f + 0.22f * azar(s, j % n)
            val p = Offset(r.center.x + kotlin.math.cos(t) * r.width / 2 * rr, r.center.y + kotlin.math.sin(t) * r.height / 2 * rr)
            if (j == 0) hueco.moveTo(p.x, p.y) else hueco.lineTo(p.x, p.y)
        }
        hueco.close()
        drawPath(hueco, cal(a, mezcla(P.CALIZA, P.CALIZA_OSC, 0.5f), k, lit, m))
        clipPath(hueco) {
            // La junta de la sillería de verdad, que no coincide con la pintada.
            drawLine(cal(a, P.CALIZA_OSC, k, lit, m), Offset(r.left, r.top + r.height * 0.55f), Offset(r.right, r.top + r.height * 0.5f), 2f * u)
            drawLine(cal(a, P.CALIZA_OSC, k, lit, m), Offset(r.left + r.width * 0.62f, r.top), Offset(r.left + r.width * 0.6f, r.top + r.height * 0.52f), 1.6f * u)
            for (j in 0 until 5) drawCircle(a.sombra(0.25f), 0.9f * u, Offset(r.left + r.width * azar(s, j), r.top + r.height * azar(s, j + 9)))
            // La sombra del borde de la cal arriba a la izquierda (de día; de noche, poca).
            translate(-1.8f * u, -2f * u) { drawPath(hueco, a.sombra(0.55f), style = Stroke(4.4f * u)) }
        }
        // El grueso de la cal, que coge la luz abajo a la derecha.
        clipRect(r.center.x - r.width * 0.1f, r.center.y, r.right + 2f * u, r.bottom + 2f * u) {
            drawPath(hueco, cal(a, P.CAL_LUZ, k, lit, m).copy(alpha = 0.8f), style = Stroke(1.2f * u))
        }
    }

    // ---------------------------------------------------------------- el costado con el ajimez

    private fun DrawScope.costado(a: AireTemplario, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val k = cerca(Offset(w * 0.07f, w * 0.25f))
        val sup = enCostado(w, 0f, VIGA); val supC = enCostado(w, 1f, VIGA)
        val muro = Path().apply {
            moveTo(supC.x, supC.y); lineTo(sup.x, sup.y); lineTo(w * ESQUINA, apoyo + w * 0.02f); lineTo(0f, apoyo + w * 0.02f); close()
        }
        // El costado está de espaldas al ajimez: en la sombra del cuarto; de noche, casi perdido.
        drawPath(muro, a.dia(mezcla(P.CAL_SOMBRA, P.CAL, 0.15f), -0.12f, k))
        // Su despiece, en escorzo.
        val lineas = Path()
        var y = CENEFA + 0.012f
        while (y < apoyo / w + 0.05f) {
            val p0 = enCostado(w, 0f, y); val p1 = enCostado(w, 1f, y)
            lineas.moveTo(p0.x, p0.y); lineas.lineTo(p1.x, p1.y)
            lineas.moveTo(p0.x, p0.y + 2.2f * u); lineas.lineTo(p1.x, p1.y + 2.4f * u)
            y += 0.074f
        }
        clipPath(muro) { drawPath(lineas, Color(0xFFB4533B).copy(alpha = 0.6f), style = Stroke(1f * u), blendMode = BlendMode.Multiply) }
        // La cenefa en escorzo.
        val c0 = enCostado(w, 0f, VIGA + 0.012f); val c1 = enCostado(w, 1f, VIGA + 0.012f)
        val d0 = enCostado(w, 0f, CENEFA); val d1 = enCostado(w, 1f, CENEFA)
        clipPath(muro) { drawPath(poligonoTemplario(c0.x, c0.y, c1.x, c1.y, d1.x, d1.y, d0.x, d0.y), Color(0xFFB4533B).copy(alpha = 0.7f), blendMode = BlendMode.Multiply) }
        ajimez(a, cerca, m)
    }

    /**
     * El ajimez: el hondo del muro (el derrame, la peana y el intradós), las dos luces de medio punto con su columnilla y su
     * cielo, y el postigo de cedro abierto contra el costado.
     */
    private fun DrawScope.ajimez(a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val k = cerca(Offset(w * 0.07f, w * 0.24f))
        // El hondo: la boca del derrame en el costado (un arco grande que abarca las dos luces).
        val boca = Path()
        val nB = 14
        for (j in 0..nB) {
            val t = j / nB.toFloat()
            val fy = 0.16f * (1f - sqrt((1f - (2f * t - 1f) * (2f * t - 1f)).coerceAtLeast(0f))) - 0.06f
            val o = enAjimez(w, -0.06f + 1.12f * t, fy)
            if (j == 0) boca.moveTo(o.x, o.y) else boca.lineTo(o.x, o.y)
        }
        val bd = enAjimez(w, 1.06f, 1.05f); val bi = enAjimez(w, -0.06f, 1.05f)
        boca.lineTo(bd.x, bd.y); boca.lineTo(bi.x, bi.y); boca.close()
        // El derrame es de caliza sin enlucir, a la sombra; la peana coge el sol.
        drawPath(boca, a.dia(mezcla(P.CALIZA, P.CALIZA_OSC, 0.55f), -0.08f, k))
        clipPath(boca) {
            // El derrame de la izquierda (el lado cercano, que se ve de frente) y el intradós arriba, en sombra.
            val e0 = enAjimez(w, 1.06f, -0.2f)
            drawRect(a.sombra(0.3f), Offset(e0.x - w * 0.03f, 0f), Size(w * 0.03f, w))
            drawRect(a.sombra(0.25f), Offset(0f, enAjimez(w, 0.5f, -0.06f).y - w * 0.01f), Size(w * 0.2f, w * 0.035f))
            // La peana de abajo, al sol (o a la luna).
            val p0 = enAjimez(w, -0.06f, 0.97f); val p1 = enAjimez(w, 1.06f, 0.97f)
            val peana = poligonoTemplario(p0.x, p0.y, p1.x, p1.y, bd.x, bd.y, bi.x, bi.y)
            drawPath(peana, if (a.oscuro && !a.noche) a.dia(P.CALIZA, 0f, k) else a.en(P.CALIZA, 0f, k, 1f, m ?: ManchaTemplario(FloatArray(8), P.SOL, 0.5f)))
        }
        // Las dos luces con su cielo.
        val luces = Path()
        for (i in 0 until 2) {
            val x0 = if (i == 0) 0.04f else 0.55f; val x1 = if (i == 0) 0.45f else 0.96f
            val n = 10
            for (j in 0..n) {
                val t = j / n.toFloat()
                val fy = 0.22f * (1f - sqrt((1f - (2f * t - 1f) * (2f * t - 1f)).coerceAtLeast(0f)))
                val o = enAjimez(w, x0 + (x1 - x0) * t, fy)
                if (j == 0) luces.moveTo(o.x, o.y) else luces.lineTo(o.x, o.y)
            }
            val b1 = enAjimez(w, x1, 0.94f); val b0 = enAjimez(w, x0, 0.94f)
            luces.lineTo(b1.x, b1.y); luces.lineTo(b0.x, b0.y); luces.close()
        }
        val arriba = enAjimez(w, 0.5f, 0f).y; val abajo = enAjimez(w, 0.5f, 0.94f).y
        val cielo = when {
            a.oscuro -> listOf(P.CIELO_NOCHE_ALTO, P.CIELO_NOCHE)
            a.noche -> listOf(Color(0xFF5E4E8E), Color(0xFFE88A6A), Color(0xFFF6C07A))
            else -> listOf(P.CIELO_ALTO, mezcla(P.CIELO_ALTO, P.CIELO, 0.6f), P.CIELO)
        }
        drawPath(luces, Brush.verticalGradient(cielo, arriba, abajo))
        clipPath(luces) {
            if (a.oscuro) {
                // Las estrellas de la vigilia y, en los Sueños, la luna.
                for (j in 0 until 9) {
                    val o = enAjimez(w, azar(9950, j), 0.05f + 0.8f * azar(9951, j))
                    drawCircle(Color(0xFFE6ECF8).copy(alpha = 0.5f + 0.5f * azar(9952, j)), (0.6f + 0.7f * azar(9953, j)) * u, o)
                }
                if (a.noche) {
                    val c = enAjimez(w, 0.25f, 0.3f)
                    drawCircle(P.LUNA.copy(alpha = 0.25f), w * 0.016f, c)
                    drawCircle(P.LUNA, w * 0.01f, c)
                }
            } else if (!a.noche) {
                // La reverberación de la arena abajo: el cielo más blanco junto a la peana.
                drawRect(Color(0xFFFFF6DE).copy(alpha = 0.5f), Offset(0f, abajo - w * 0.04f), Size(w * 0.2f, w * 0.04f))
            }
        }
        // El grueso del muro en el borde de las luces que da al derrame lejano (la jamba, en escorzo).
        drawPath(luces, a.sombra(0.35f), style = Stroke(1.2f * u))
        // La columnilla, con su basa y su capitel.
        val ca = enAjimez(w, 0.5f, 0.04f); val cb = enAjimez(w, 0.5f, 0.94f)
        val cw = w * 0.011f
        val fuste = Rect(ca.x - cw / 2, ca.y + w * 0.012f, ca.x + cw / 2, cb.y - w * 0.008f)
        drawRect(a.dia(P.CALIZA, 0.05f, k), fuste.topLeft, fuste.size)
        drawRect(a.sombra(0.3f), Offset(fuste.right - cw * 0.35f, fuste.top), Size(cw * 0.35f, fuste.height))
        drawRect(a.claro(0.3f), Offset(fuste.left + cw * 0.15f, fuste.top), Size(cw * 0.2f, fuste.height))
        drawPath(poligonoTemplario(fuste.left - cw * 0.7f, fuste.top - w * 0.012f, fuste.right + cw * 0.7f, fuste.top - w * 0.012f,
            fuste.right, fuste.top, fuste.left, fuste.top), a.dia(P.CALIZA, 0.1f, k))
        drawRect(a.dia(P.CALIZA_OSC, 0f, k), Offset(fuste.left - cw * 0.5f, fuste.bottom), Size(cw * 2f, w * 0.008f))
        // El postigo de cedro, abierto contra el costado, entre el ajimez y la esquina.
        postigo(a, k)
    }

    /** El postigo: tablas de cedro con su travesaño y los pernios de hierro, abierto contra el costado (en escorzo). */
    private fun DrawScope.postigo(a: AireTemplario, k: Float) {
        val w = size.width; val u = density
        val p0 = enCostado(w, 0.02f, 0.1f); val p1 = enCostado(w, 0.13f, 0.1f)
        val p2 = enCostado(w, 0.13f, 0.375f); val p3 = enCostado(w, 0.02f, 0.375f)
        val hoja = poligonoTemplario(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
        drawPath(hoja, a.dia(P.CEDRO, -0.05f, k))
        clipPath(hoja) {
            // Las tablas, a lo alto.
            for (j in 1 until 3) {
                val f = j / 3f
                val t0 = enCostado(w, 0.02f + 0.11f * f, 0.1f); val t1 = enCostado(w, 0.02f + 0.11f * f, 0.375f)
                drawLine(a.luz(P.CEDRO_HONDO, k).copy(alpha = 0.7f), t0, t1, 1f * u)
            }
            // Los dos pernios de hierro, de través.
            listOf(0.15f, 0.33f).forEach { fy ->
                val q0 = enCostado(w, 0.02f, fy); val q1 = enCostado(w, 0.13f, fy)
                drawLine(a.luz(P.HIERRO, k), q0, q1, 2.4f * u)
                drawCircle(a.luz(P.HIERRO_LUZ, k), 1f * u, Offset((q0.x + q1.x) / 2, (q0.y + q1.y) / 2 - 0.4f * u))
            }
            drawRect(a.claro(0.12f), Offset(p0.x, 0f), Size(w * 0.004f, w))
        }
        drawPath(hoja, a.luz(P.CEDRO_HONDO, k).copy(alpha = 0.8f), style = Stroke(1f * u))
    }

    /** La viga carrera arriba, con sus canecillos debajo, a lo ancho del fondo y en escorzo en el costado. */
    private fun DrawScope.viga(a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val k = cerca(Offset(w * 0.6f, w * 0.02f))
        val bajo = w * VIGA
        // En el fondo.
        val tramos = if (a.oscuro) 12 else 1
        for (t in 0 until tramos) {
            val x0 = w * ESQUINA + (w - w * ESQUINA) * t / tramos; val x1 = w * ESQUINA + (w - w * ESQUINA) * (t + 1) / tramos + 1f
            val kk = if (tramos > 1) cerca(Offset((x0 + x1) / 2, bajo)) else k
            drawRect(a.dia(P.CEDRO_OSC, -0.05f, kk), Offset(x0, 0f), Size(x1 - x0, bajo))
            drawRect(a.dia(P.CEDRO, 0f, kk), Offset(x0, bajo - w * 0.012f), Size(x1 - x0, w * 0.012f))
        }
        drawRect(a.claro(0.22f), Offset(w * ESQUINA, bajo - w * 0.012f), Size(w - w * ESQUINA, 1f * u))
        drawRect(a.sombra(0.5f), Offset(w * ESQUINA, bajo), Size(w - w * ESQUINA, 1.6f * u))
        // Las vetas de la viga.
        val vetas = Path()
        for (j in 0 until 6) {
            val yy = w * (0.006f + 0.005f * j)
            vetas.moveTo(w * ESQUINA, yy)
            var x = w * ESQUINA
            while (x < w) { x += w * 0.08f; vetas.lineTo(x, yy + (azar(9960 + j, (x / w * 40).toInt()) - 0.5f) * 1.4f * u) }
        }
        drawPath(vetas, a.luz(P.CEDRO_HONDO, k).copy(alpha = 0.45f), style = Stroke(0.7f * u))
        // Los canecillos: tacos de cedro bajo la viga, con su sombra en la cal.
        var x = w * (ESQUINA + 0.05f)
        while (x < w) {
            val kk = cerca(Offset(x, bajo))
            val c = Rect(x, bajo, x + w * 0.026f, bajo + w * 0.022f)
            val lit = m?.toca(c.center.x / w, (c.bottom + w * 0.01f) / w) ?: 0f
            drawPath(poligonoTemplario(c.right, c.top, c.right + w * 0.016f, c.top + w * 0.008f, c.right + w * 0.012f, c.bottom + w * 0.014f, c.left + w * 0.01f, c.bottom + w * 0.012f),
                a.sombra(if (lit > 0f) 0.42f else 0.24f))
            drawRect(a.dia(P.CEDRO, 0f, kk), c.topLeft, c.size)
            drawRect(a.dia(P.CEDRO_LUZ, 0.05f, kk), c.topLeft, Size(c.width * 0.3f, c.height))
            drawRect(a.luz(P.CEDRO_OSC, kk), Offset(c.left, c.bottom - c.height * 0.3f), Size(c.width, c.height * 0.3f))
            drawRect(a.sombra(0.3f), Offset(c.right - c.width * 0.18f, c.top), Size(c.width * 0.18f, c.height))
            x += w * 0.125f
        }
        // En el costado, en escorzo.
        val s0 = enCostado(w, 0f, 0f); val s1 = enCostado(w, 1f, 0f)
        val b0 = enCostado(w, 0f, VIGA); val b1 = enCostado(w, 1f, VIGA)
        drawPath(poligonoTemplario(s0.x, -1f, s1.x, -1f, b1.x, b1.y, b0.x, b0.y), a.dia(P.CEDRO_OSC, -0.15f, cerca(Offset(w * 0.07f, w * 0.03f))))
        drawLine(a.claro(0.15f), Offset(b1.x, b1.y - 1f * u), Offset(b0.x, b0.y - 1f * u), 1f * u)
        drawLine(a.sombra(0.5f), Offset(b1.x, b1.y + 0.6f * u), Offset(b0.x, b0.y + 0.6f * u), 1.4f * u)
    }

    /** De noche, el halo cálido de la lámpara sobre lo que hay delante de la pared (tenue: la pared ya se pintó encendida). */
    fun DrawScope.luzDeNoche(a: AireTemplario, ch: Float) {
        if (!a.oscuro) return
        val w = size.width
        val c = Offset(w * MuebleEstanteTemplario.LAMP_X, w * MuebleEstanteTemplario.LAMP_Y)
        val f = if (a.noche) 0.6f else 1f
        clipRect(0f, 0f, w, ch) {
            drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.16f * f), P.LLAMA.copy(alpha = 0f)), c, w * 0.2f), w * 0.2f, c)
        }
    }
}
