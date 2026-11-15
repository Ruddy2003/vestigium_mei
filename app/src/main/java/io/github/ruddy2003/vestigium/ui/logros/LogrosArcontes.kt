package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Arcontes
// =====================================================================

/**
 * Arcontes: los logros del círculo escondido, en la sala tallada en la caliza donde se reúnen los que saben. Cada trofeo
 * descansa en un sillar labrado (la cornisa, el frente con su arquería en relieve, el disco del metal del nivel engastado en
 * el arco del medio y el zócalo), y en el hall, en la repisa de la misma piedra; su placa es un óstracon, el tiesto de barro
 * donde se escribía, atado con un alambre del metal del nivel. El salón es la cueva tallada: un nicho en arco por cada trofeo,
 * las lámparas de aceite en los pilares, la estera en el suelo y, arriba, la grieta por donde entra la luz. En la campaña,
 * el alma sube con su lámpara y, al ganar, llega a la puerta de la Ogdóada.
 */
object LogrosArcontes : DisenoLogros {
    private val CALIZA = TrazosArcontes.CALIZA
    private val CALIZA_SOMBRA = TrazosArcontes.CALIZA_SOMBRA
    private val CALIZA_LUZ = Color(0xFFEFE5CB)
    private val LINO = TrazosArcontes.LINO
    private val BARRO = TrazosArcontes.BARRO
    private val BARRO_CLARO = TrazosArcontes.BARRO_CLARO
    private val NOCHE = TrazosArcontes.CIELO_ANTIGUO
    private val GRIETA = TrazosArcontes.GRIETA
    private val GRIETA_HALO = TrazosArcontes.GRIETA_HALO
    private val LLAMA_HALO = TrazosArcontes.LLAMA_HALO
    /** El junco de la estera y la tierra apisonada del suelo. */
    private val ESTERA = Color(0xFFB79C62)
    private val TIERRA = Color(0xFF8C7656)

    /** El color de noche: la misma piedra a la luz de la lámpara, hundida en el azul negro del cielo antiguo. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.55f) else c

    /** El gris translúcido de lo que aún no se gana. */
    private fun silueta(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E2EE).copy(alpha = 0.13f) else Color(0xFF1E1A2B).copy(alpha = 0.13f)

    private fun DrawScope.fr(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)
    private fun DrawScope.fp(x: Float, y: Float) = Offset(size.width * x, size.height * y)

    /** La piedra en volumen: la luz entra por la izquierda. */
    private fun piedra(c: Color, r: Rect): Brush = Brush.horizontalGradient(listOf(mezcla(c, Color.White, 0.18f), c, mezcla(c, Color.Black, 0.3f)), r.left, r.right)

    /** Un arco de medio punto en relieve hundido, de [r]: el hueco más oscuro, la sombra arriba a la izquierda y la luz en el borde de abajo a la derecha. */
    private fun DrawScope.arcoHundido(r: Rect, piedraC: Color, lw: Float) {
        val p = Path().apply {
            moveTo(r.left, r.bottom); lineTo(r.left, r.top + r.width / 2)
            arcTo(Rect(r.left, r.top, r.right, r.top + r.width), 180f, 180f, false)
            lineTo(r.right, r.bottom); close()
        }
        drawPath(p, mezcla(piedraC, Color.Black, 0.22f))
        clipPath(p) { drawPath(Path().apply { addPath(p, Offset(lw * 1.2f, lw * 1.2f)) }, mezcla(piedraC, Color.Black, 0.08f)) }
        drawPath(p, Color.Black.copy(alpha = 0.25f), style = Stroke(lw * 0.6f))
    }

    /** El disco del metal del nivel, engastado: el borde oscuro, la cara y la chispa grabada; el legendario, con su brillo. */
    private fun DrawScope.discoNivel(c: Offset, r: Float, e: EstadoPedestal, fino: Boolean) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.25f), r, c + Offset(r * 0.12f, r * 0.16f))
        drawCircle(Brush.radialGradient(listOf(e.brillo, e.base, e.metalOscuro), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.5f), r, c)
        drawCircle(e.metalOscuro, r, c, style = Stroke(maxOf(r * 0.12f, 0.45f * u)))
        if (fino) with(TrazosArcontes) { chispa(c, r * 0.6f, 1f, e.metalOscuro.copy(alpha = 0.85f), e.brillo.copy(alpha = 0f)) }
        else drawCircle(e.metalOscuro, r * 0.25f, c)
        if (e.nivel >= 4 && fino) drawLine(Color.White.copy(alpha = 0.85f), Offset(c.x - r * 0.62f, c.y + r * 0.1f), Offset(c.x - r * 0.18f, c.y - r * 0.6f), maxOf(r * 0.12f, 0.5f * u), StrokeCap.Round)
    }

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El sillar labrado: la cornisa con su arista clara, el frente con la arquería de tres arcos hundidos (el del medio con
     * el disco del metal del nivel y la chispa grabada), las huellas del cincel y el zócalo; de noche, la misma piedra con la
     * luz de una lámpara que le da de lado.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val h = size.height; val u = density
        val fino = h > 70 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val cornisa = fr(0.1f, 0.765f, 0.9f, 0.797f)
        val cuerpo = fr(0.14f, 0.797f, 0.86f, 0.93f)
        val zocalo = fr(0.11f, 0.93f, 0.89f, 0.957f)
        if (!e.hecho) { drawPath(Path().apply { addRect(cornisa); addRect(cuerpo); addRect(zocalo) }, silueta(oscuro)); return }
        // La sombra al pie.
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), fp(0.08f, 0.945f), Size(size.width * 0.86f, h * 0.026f))
        val c = t(CALIZA, oscuro)
        drawRect(piedra(mezcla(c, Color.Black, 0.12f), zocalo), zocalo.topLeft, zocalo.size)
        drawRect(piedra(c, cuerpo), cuerpo.topLeft, cuerpo.size)
        // La arquería: tres arcos hundidos; en el del medio, el disco del nivel.
        val ancho = cuerpo.width * 0.2f
        listOf(0.2f, 0.5f, 0.8f).forEach { f ->
            val cx = cuerpo.left + cuerpo.width * f
            arcoHundido(Rect(cx - ancho / 2, cuerpo.top + cuerpo.height * 0.16f, cx + ancho / 2, cuerpo.bottom - cuerpo.height * 0.12f), c, lw)
        }
        if (fino) {
            // Las huellas del cincel, en diagonal, en las enjutas.
            var x = cuerpo.left + 2 * lw
            while (x < cuerpo.right - 2 * lw) {
                drawLine(mezcla(c, Color.Black, 0.25f).copy(alpha = 0.5f), Offset(x, cuerpo.top + cuerpo.height * 0.06f), Offset(x + lw * 2.2f, cuerpo.top + cuerpo.height * 0.14f), lw * 0.5f)
                x += lw * 4.5f
            }
            // La línea de la base de los arcos.
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.1f), Offset(cuerpo.right, cuerpo.bottom - cuerpo.height * 0.1f), lw * 0.7f)
        }
        discoNivel(Offset(cuerpo.center.x, cuerpo.top + cuerpo.height * 0.52f), h * 0.029f, e, fino)
        // La cornisa, con la arista clara arriba y su sombra sobre el frente.
        drawRect(piedra(mezcla(c, CALIZA_LUZ, if (oscuro) 0.1f else 0.35f), cornisa), cornisa.topLeft, cornisa.size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.55f), Offset(cornisa.left, cornisa.top + lw * 0.5f), Offset(cornisa.right, cornisa.top + lw * 0.5f), lw)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), cuerpo.top, cuerpo.top + cuerpo.height * 0.15f), cuerpo.topLeft, Size(cuerpo.width, cuerpo.height * 0.15f))
        // De noche, la luz cálida de una lámpara que le da de lado.
        if (oscuro) drawRect(Brush.horizontalGradient(listOf(LLAMA_HALO.copy(alpha = 0.16f), Color.Transparent), cornisa.left, cornisa.center.x), Offset(cornisa.left, cornisa.top), Size(cornisa.width / 2, zocalo.bottom - cornisa.top))
    }

    /** La repisa de caliza del hall: el canto claro, el frente con su arquería de cinco arcos hundidos y el disco del nivel en el del medio. */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val canto = fr(0f, 0f, 1f, 0.3f)
        val frente = fr(0.04f, 0.3f, 0.96f, 1f)
        if (!e.hecho) { drawPath(Path().apply { addRect(canto); addRect(frente) }, silueta(oscuro)); return }
        val c = t(CALIZA, oscuro)
        drawRect(piedra(c, frente), frente.topLeft, frente.size)
        val ancho = frente.width * 0.1f
        for (k in 0 until 5) {
            if (k == 2) continue
            val cx = frente.left + frente.width * (0.1f + k * 0.2f)
            arcoHundido(Rect(cx - ancho / 2, frente.top + frente.height * 0.2f, cx + ancho / 2, frente.bottom - frente.height * 0.12f), c, lw * 0.6f)
        }
        discoNivel(Offset(frente.center.x, frente.top + frente.height * 0.52f), h * 0.27f, e, true)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), frente.top, frente.top + frente.height * 0.25f), frente.topLeft, Size(frente.width, frente.height * 0.25f))
        drawRect(piedra(mezcla(c, CALIZA_LUZ, if (oscuro) 0.1f else 0.35f), canto), canto.topLeft, canto.size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.5f), Offset(0f, lw * 0.5f), Offset(size.width, lw * 0.5f), lw * 0.8f)
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFFF7EEDA)
    override fun rotuloPlaca(oscuro: Boolean): Color = Color(0xFFF3C47A)

    /**
     * El óstracon: el tiesto de barro cocido donde se escribían recibos y cartas cortas, con su borde roto, la cara y el
     * canto más claro, y el alambre del metal del nivel que lo ata por un agujero.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = minOf(3f * u, h * 0.1f)
        val tiesto = Path().apply {
            val k = listOf(0.03f, 0.0f, 0.12f, 0.02f, 0.35f, 0.0f, 0.62f, 0.03f, 0.86f, 0.0f, 1f, 0.12f, 0.98f, 0.45f, 1f, 0.82f, 0.9f, 1f, 0.6f, 0.97f, 0.3f, 1f, 0.06f, 0.94f, 0f, 0.6f, 0.02f, 0.25f)
            var i = 0
            while (i + 1 < k.size) {
                val x = m + (w - 2 * m) * k[i] + (azar(e.semilla, i) - 0.5f) * m * 0.8f; val y = m + (h - 2 * m) * k[i + 1]
                if (i == 0) moveTo(x, y) else lineTo(x, y); i += 2
            }
            close()
        }
        if (!e.hecho) { drawPath(tiesto, silueta(oscuro)); return }
        drawPath(Path().apply { addPath(tiesto, Offset(0.8f * u, 1.2f * u)) }, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f))
        drawPath(tiesto, Brush.verticalGradient(listOf(t(BARRO_CLARO, oscuro), t(BARRO, oscuro), mezcla(t(BARRO, oscuro), Color.Black, 0.2f)), 0f, h))
        // Las vueltas del torno, apenas, y el canto claro de la rotura.
        clipPath(tiesto) {
            var y = h * 0.3f; while (y < h) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(0f, y), Offset(w, y + h * 0.04f), 0.6f * u); y += h * 0.22f }
        }
        drawPath(tiesto, mezcla(t(BARRO_CLARO, oscuro), Color.White, 0.25f).copy(alpha = 0.7f), style = Stroke(0.9f * u, join = StrokeJoin.Round))
        // El agujero y el alambre del nivel.
        val a = Offset(m + 5f * u, h / 2)
        drawCircle(Color.Black.copy(alpha = 0.55f), 1.5f * u, a)
        drawArc(Brush.linearGradient(listOf(e.metalOscuro, e.brillo, e.base), Offset(a.x - 4 * u, a.y), Offset(a.x + 4 * u, a.y)), 90f, 180f, false,
            Offset(a.x - 4.5f * u, a.y - 3.5f * u), Size(9f * u, 7f * u), style = Stroke(1.1f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- el salón del hall

    /** La geometría de la cueva: una franja por columna, el pilar de roca en el borde de cada franja y el nicho del trofeo en medio. */
    private class GeoCueva(val w: Float, val h: Float, val u: Float, columnas: Int) {
        val n = columnas.coerceIn(1, 6)
        val paso = w / n
        val pil = minOf(paso * 0.12f, 16f * u)
        val techo = minOf(20f * u, h * 0.06f)
        val piso = minOf(18f * u, h * 0.07f)
        val suelo = h - piso
        val modulo = maxOf(230f * u, 1f)
        fun borde(i: Int) = i * paso
        fun cx(i: Int) = (i + 0.5f) * paso
    }

    /**
     * La sala escondida del círculo, tallada en la caliza: la roca con las hiladas de golpes de pico, el techo bajo de roca
     * viva y, en él, la grieta por donde baja un haz de luz blanca y violeta; un nicho en arco por cada trofeo y por cada
     * fila, con su repisa; en los pilares de roca entre los nichos, los nichos chicos de las lámparas de aceite, encendidas,
     * con el hollín encima; abajo, la tierra apisonada con la estera de junco y la jarra sellada en un rincón. De día, la
     * piedra clara y la luz que entra; de noche, la misma sala a la luz de las lámparas.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoCueva(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaCueva(g, e, oscuro) }
    }

    private fun DrawScope.escenaCueva(g: GeoCueva, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)
        val roca = m(CALIZA_SOMBRA, if (oscuro) 0.22f else 0.3f)
        val rocaHonda = m(Color(0xFF3A3020), if (oscuro) 0.35f else 0.16f)
        // La roca: los golpes del pico en hiladas y alguna veta.
        var y = g.techo + 6 * u; var fila = 0
        while (y < g.suelo && fila < 400) {
            var x = (if (fila % 2 == 0) 3f else 9f) * u; var k = 0
            while (x < w && k < 200) {
                val l = (3f + azar(e.semilla + fila, k) * 2.5f) * u; val inclina = if ((fila + k) % 3 == 0) -0.5f else 0.65f
                drawLine(roca, Offset(x, y), Offset(x + l * inclina, y + l), 0.9f * u, StrokeCap.Round)
                x += (9f + azar(e.semilla, fila * 31 + k) * 7f) * u; k++
            }
            y += 14f * u; fila++
        }
        // Los nichos de los trofeos, uno por columna y por fila, con su repisa.
        for (i in 0 until g.n) {
            val x0 = g.borde(i) + g.pil + 3f * u; val x1 = g.borde(i + 1) - g.pil - 3f * u
            if (x1 - x0 < 8 * u) continue
            var y0 = g.techo + 8f * u; var k = 0
            while (y0 < g.suelo - 40 * u && k < 20) {
                val y1 = minOf(y0 + g.modulo - 14f * u, g.suelo - 4 * u)
                val r = Rect(x0, y0, x1, y1)
                val nicho = Path().apply {
                    moveTo(r.left, r.bottom); lineTo(r.left, r.top + r.width / 2)
                    arcTo(Rect(r.left, r.top, r.right, r.top + r.width), 180f, 180f, false)
                    lineTo(r.right, r.bottom); close()
                }
                drawPath(nicho, m(CALIZA_SOMBRA, if (oscuro) 0.1f else 0.12f))
                clipPath(nicho) {
                    drawPath(Path().apply { addPath(nicho, Offset(3f * u, 4f * u)) }, rocaHonda, style = Stroke(6f * u))
                }
                drawPath(nicho, m(CALIZA_LUZ, if (oscuro) 0.12f else 0.55f), style = Stroke(1.2f * u))
                drawRect(m(CALIZA, if (oscuro) 0.2f else 0.45f), Offset(r.left - 3 * u, r.bottom - 3 * u), Size(r.width + 6 * u, 3 * u))
                y0 += g.modulo; k++
            }
        }
        // Los pilares de roca entre los nichos, con los nichos chicos de las lámparas.
        for (i in 0..g.n) {
            val cx = g.borde(i)
            drawRect(m(Color(0xFF2A2218), if (oscuro) 0.12f else 0.05f), Offset(cx - g.pil, g.techo), Size(g.pil * 2, g.suelo - g.techo))
            var yl = g.techo + g.modulo * 0.42f; var k = 0
            while (yl < g.suelo - 30 * u && k < 20) {
                val lx = cx.coerceIn(16f * u, w - 16f * u)
                val r = Rect(lx - 9 * u, yl - 14 * u, lx + 9 * u, yl + 4 * u)
                // El hollín encima del nicho y la luz de la llama.
                drawCircle(Brush.radialGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.12f), Color.Transparent), Offset(lx, r.top - 2 * u), 14 * u), 14 * u, Offset(lx, r.top - 2 * u))
                drawCircle(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = if (oscuro) 0.3f else 0.18f), Color.Transparent), Offset(lx + 4 * u, yl - 8 * u), if (oscuro) 42 * u else 24 * u),
                    if (oscuro) 42 * u else 24 * u, Offset(lx + 4 * u, yl - 8 * u))
                arcoHundido(r, m(CALIZA, if (oscuro) 0.35f else 0.6f), 1.2f * u)
                with(TrazosArcontes) { lampara(Offset(lx - 1 * u, yl + 0.5f * u), 13 * u, if (oscuro) BARRO_CLARO else BARRO, 0.9f + 0.2f * azar(e.semilla + i, k)) }
                yl += g.modulo; k++
            }
        }
        // El techo bajo de roca viva, con su borde desparejo.
        val techo = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, g.techo)
            var x = w; var k = 0
            while (x > 0f && k < 200) { x -= (10f + azar(e.semilla, 500 + k) * 14f) * u; lineTo(maxOf(x, 0f), g.techo + (azar(e.semilla, 700 + k) - 0.3f) * 6f * u); k++ }
            close()
        }
        drawPath(techo, m(Color(0xFF4A3E2C), if (oscuro) 0.5f else 0.22f))
        // La grieta del techo y el haz de luz que baja por ella.
        val gx = w * 0.62f
        val haz = Path().apply { moveTo(gx - 2 * u, g.techo); lineTo(gx + 2 * u, g.techo); lineTo(gx + 70 * u, minOf(h, g.techo + 360 * u)); lineTo(gx - 10 * u, minOf(h, g.techo + 360 * u)); close() }
        drawPath(haz, Brush.verticalGradient(listOf(GRIETA_HALO.copy(alpha = if (oscuro) 0.16f else 0.1f), Color.Transparent), g.techo, minOf(h, g.techo + 360 * u)))
        with(TrazosArcontes) { grieta(Offset(gx - 6 * u, 0f), Offset(gx + 1 * u, g.techo + 2 * u), 1.1f * u, if (oscuro) 1f else 0.75f, e.semilla + 2) }
        // El suelo de tierra apisonada, la estera de junco y la jarra sellada en el rincón.
        drawRect(m(TIERRA, if (oscuro) 0.35f else 0.28f), Offset(0f, g.suelo), Size(w, g.piso))
        drawLine(m(Color.Black, 0.25f), Offset(0f, g.suelo), Offset(w, g.suelo), 1 * u)
        val estera = Rect(w * 0.18f, g.suelo + g.piso * 0.25f, w * 0.82f, g.suelo + g.piso * 0.85f)
        drawRect(m(ESTERA, if (oscuro) 0.4f else 0.5f), estera.topLeft, estera.size)
        clipRect(estera.left, estera.top, estera.right, estera.bottom) {
            var x = estera.left - estera.height; var k = 0
            while (x < estera.right && k < 400) {
                drawLine(m(Color(0xFF5C4826), 0.3f), Offset(x, estera.bottom), Offset(x + estera.height, estera.top), 0.8f * u)
                drawLine(m(Color(0xFF5C4826), 0.18f), Offset(x, estera.top), Offset(x + estera.height, estera.bottom), 0.6f * u)
                x += 4f * u; k++
            }
        }
        val jx = 12f * u; val jb = g.suelo + g.piso * 0.7f
        drawPath(Path().apply {
            moveTo(jx - 5 * u, jb); cubicTo(jx - 12 * u, jb - 8 * u, jx - 11 * u, jb - 26 * u, jx - 4 * u, jb - 30 * u); lineTo(jx - 4 * u, jb - 34 * u)
            lineTo(jx + 4 * u, jb - 34 * u); lineTo(jx + 4 * u, jb - 30 * u); cubicTo(jx + 11 * u, jb - 26 * u, jx + 12 * u, jb - 8 * u, jx + 5 * u, jb); close()
        }, m(BARRO, if (oscuro) 0.45f else 0.4f))
        drawArc(m(BARRO, if (oscuro) 0.55f else 0.5f), 180f, 180f, true, Offset(jx - 6 * u, jb - 38 * u), Size(12 * u, 8 * u))
    }

    // ---------------------------------------------------------------- las campañas

    /** La sombra del marcador en el suelo, bajo [p]: se achica y se aclara cuando brinca ([salto], de 0 a 1). */
    private fun DrawScope.sombraAlma(p: Offset, ancho: Float, salto: Float, oscuro: Boolean) {
        val f = 1f - 0.35f * salto.coerceIn(0f, 1f)
        val a = ancho * f; val b = 3.2f * density * f
        drawOval(Color.Black.copy(alpha = (if (oscuro) 0.5f else 0.2f) * f), Offset(p.x - a / 2, p.y - b / 2), Size(a, b))
    }

    /**
     * El alma que sube, con su lámpara: la lámpara de aceite de barro sobre la parada, con la llama que crece cuando brinca
     * (hoy falta cumplir) y la chispa que se le escapa hacia arriba; de noche, con su halo.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        sombraAlma(e.punto, 16f * u, s, oscuro)
        val pie = e.punto.y - 4f * u - s * 3.5f * u
        val c = Offset(e.punto.x - 1 * u, pie - 4f * u)
        if (oscuro) drawCircle(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = 0.3f), Color.Transparent), Offset(c.x + 8 * u, c.y - 10 * u), 18 * u), 18 * u, Offset(c.x + 8 * u, c.y - 10 * u))
        with(TrazosArcontes) {
            lampara(c, 22f * u, BARRO_CLARO, 1f + 0.35f * s)
            if (s > 0.05f) chispa(Offset(c.x + 9 * u, c.y - 18 * u - s * 4 * u), (2.2f + 1.4f * s) * u, s, if (oscuro) GRIETA else e.acento, GRIETA_HALO)
        }
        if (!oscuro) drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x - 7 * u, pie + 0.5f * u), Offset(c.x + 5 * u, pie + 0.5f * u), 0.6f * u)
    }

    /**
     * La puerta de la Ogdóada: el arco de piedra abierto en la meta, con la luz blanca y violeta de dentro; encima, las ocho
     * estrellas de la octava esfera en arco, y al pie del umbral, la lámpara que el alma deja encendida.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val ancho = 44f * u; val alto = 46f * u; val mg = 2f * u
        val x = if (size.width > ancho + 2 * mg) e.meta.x.coerceIn(ancho / 2 + mg, size.width - ancho / 2 - mg) else size.width / 2
        val y = if (size.height > alto + 2 * mg) (e.meta.y - 2f * u).coerceIn(alto + mg, size.height - mg) else size.height - mg
        val piedraC = if (oscuro) mezcla(CALIZA, NOCHE, 0.4f) else CALIZA
        val linea = if (oscuro) LINO.copy(alpha = 0.5f) else Color(0xFF5C4A30)
        // La luz que sale de la puerta.
        drawCircle(Brush.radialGradient(listOf(GRIETA_HALO.copy(alpha = if (oscuro) 0.45f else 0.3f), Color.Transparent), Offset(x, y - 16 * u), 26 * u), 26 * u, Offset(x, y - 16 * u))
        // Las jambas y el arco de piedra, con la puerta abierta.
        val fuera = Rect(x - 13 * u, y - 34 * u, x + 13 * u, y)
        val dentro = Rect(x - 8 * u, y - 29 * u, x + 8 * u, y)
        fun arco(r: Rect) = Path().apply {
            moveTo(r.left, r.bottom); lineTo(r.left, r.top + r.width / 2); arcTo(Rect(r.left, r.top, r.right, r.top + r.width), 180f, 180f, false); lineTo(r.right, r.bottom); close()
        }
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), Offset(x - 18 * u, y - 1.6f * u), Size(36 * u, 3.2f * u))
        drawPath(Path.combine(PathOperation.Difference, arco(fuera), arco(dentro)), Brush.horizontalGradient(listOf(mezcla(piedraC, Color.White, 0.2f), piedraC, mezcla(piedraC, Color.Black, 0.3f)), fuera.left, fuera.right))
        drawPath(arco(dentro), Brush.verticalGradient(listOf(GRIETA, mezcla(GRIETA, GRIETA_HALO, 0.5f)), dentro.top, dentro.bottom))
        drawPath(arco(fuera), linea, style = Stroke(0.8f * u))
        drawPath(arco(dentro), linea.copy(alpha = 0.6f), style = Stroke(0.6f * u))
        // La dovela clave y las juntas del arco.
        drawLine(linea, Offset(x, fuera.top), Offset(x, dentro.top), 0.7f * u)
        listOf(-50f, -130f).forEach { g -> val a = Math.toRadians(g.toDouble()); drawLine(linea.copy(alpha = 0.6f), Offset(x + 8 * u * cos(a).toFloat(), y - 21 * u + 8 * u * sin(a).toFloat()), Offset(x + 13 * u * cos(a).toFloat(), y - 21 * u + 13 * u * sin(a).toFloat()), 0.6f * u) }
        // Las ocho estrellas de la octava esfera, en arco sobre la puerta.
        with(TrazosArcontes) {
            for (k in 0 until 8) {
                val a = Math.toRadians(-160.0 + k * 140.0 / 7)
                chispa(Offset(x + 20 * u * cos(a).toFloat(), y - 22 * u + 20 * u * sin(a).toFloat()), 2.4f * u, 1f, if (oscuro) GRIETA else Color(0xFF6A4FC0), GRIETA_HALO)
            }
            lampara(Offset(x + 15 * u, y - 3 * u), 12 * u, BARRO_CLARO, 1f)
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Los objetos de Tu año de Arcontes: la luna, la creciente sobre el lago con su reflejo; la huella, la sandalia de cuero
     * de Galilea con sus correas; la pesa, la piedra de moler de basalto, con el agujero de la tolva y el del palo.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val c = e.centro; val u = density
        val g = maxOf(r * 0.1f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawPath(Path.combine(PathOperation.Difference, Path().apply { addOval(Rect(Offset(c.x - r * 0.12f, c.y - r * 0.18f), r * 0.72f)) },
                    Path().apply { addOval(Rect(Offset(c.x + r * 0.2f, c.y - r * 0.36f), r * 0.6f)) }), e.color)
                if (r >= 6f * u) for (k in 0 until 2) {
                    val yy = c.y + r * (0.72f + k * 0.2f); val a = r * (0.7f - k * 0.25f)
                    drawLine(e.color.copy(alpha = 0.8f - k * 0.3f), Offset(c.x - a, yy), Offset(c.x + a * 0.6f, yy), g * 0.8f, StrokeCap.Round)
                }
            }
            "huella" -> rotate(-12f, c) {
                val x = c.x; val y = c.y
                drawPath(Path().apply {
                    moveTo(x, y - r)
                    cubicTo(x + r * 0.5f, y - r, x + r * 0.5f, y - r * 0.2f, x + r * 0.32f, y + r * 0.2f)
                    cubicTo(x + r * 0.28f, y + r * 0.5f, x + r * 0.36f, y + r * 0.95f, x, y + r * 0.98f)
                    cubicTo(x - r * 0.36f, y + r * 0.95f, x - r * 0.28f, y + r * 0.5f, x - r * 0.32f, y + r * 0.2f)
                    cubicTo(x - r * 0.5f, y - r * 0.2f, x - r * 0.5f, y - r, x, y - r); close()
                }, e.color)
                // Las correas: la del dedo y las dos que cruzan el empeine.
                drawLine(e.fondo, Offset(x - r * 0.05f, y - r * 0.75f), Offset(x, y - r * 0.35f), g, StrokeCap.Round)
                drawLine(e.fondo, Offset(x - r * 0.4f, y - r * 0.2f), Offset(x + r * 0.4f, y - r * 0.4f), g, StrokeCap.Round)
                if (r >= 6f * u) drawLine(e.fondo, Offset(x - r * 0.36f, y + r * 0.35f), Offset(x + r * 0.36f, y + r * 0.2f), g, StrokeCap.Round)
            }
            "pesa" -> {
                // La piedra de moler de arriba, vista un poco desde arriba: el disco grueso, el canto y los dos agujeros.
                drawOval(e.color, Offset(c.x - r * 0.95f, c.y - r * 0.2f), Size(r * 1.9f, r * 0.95f))
                drawOval(e.color, Offset(c.x - r * 0.95f, c.y - r * 0.6f), Size(r * 1.9f, r * 0.95f))
                drawOval(e.fondo, Offset(c.x - r * 0.95f, c.y - r * 0.6f), Size(r * 1.9f, r * 0.95f), style = Stroke(g * 0.7f))
                drawOval(e.fondo, Offset(c.x - r * 0.24f, c.y - r * 0.24f), Size(r * 0.48f, r * 0.22f))
                if (r >= 6f * u) drawCircle(e.fondo, r * 0.08f, Offset(c.x + r * 0.62f, c.y - r * 0.13f))
            }
            else -> {}
        }
    }
}
