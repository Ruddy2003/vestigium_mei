package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.cos
import kotlin.math.sin

/**
 * El bodegón de encima del armario de Abismo ([MuebleEstanteAbismo]). Manda la bomba de aire de dos volantes (la que da aire a la
 * escafandra del Ideario por la tubería de cobre): la caja de teca con sus paneles, las escuadras de latón y el manómetro, las
 * tapas de los cilindros, los volantes de hierro pintados de minio con sus cinco rayos curvos y la manivela, sobre sus patines y
 * trincada a la balda. A la izquierda, el cabo de vida de manila adujado en espiral, con la bola de vidrio de las redes en su
 * centro y la gaza que cae por la cornisa; a la derecha, bajo el farol, la caracola reina de Florida y la aceitera de la bomba; el
 * bichero apoyado en la curva. Delante, la barandilla de mar de la balda. Lo que cae dentro de la mancha de la escotilla se
 * pinta otra vez con esa luz.
 */
internal object CoronaEstanteAbismo {
    private val P = PaletaAbismo

    /** Lo de encima, de atrás adelante, y la barandilla. */
    fun DrawScope.encima(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        val w = size.width
        conLuz(m) { lit -> bichero(a, apoyo, cerca, m, lit) }
        conLuz(m) { lit -> aceitera(Offset(w * 0.805f, apoyo), a, cerca, m, lit) }
        conLuz(m) { lit -> bomba(Offset(w * 0.555f, apoyo), a, apoyo, cerca, m, lit) }
        conLuz(m) { lit -> cabo(Offset(w * 0.25f, apoyo), a, apoyo, cerca, m, lit) }
        conLuz(m) { lit -> caracola(Offset(w * 0.895f, apoyo), a, apoyo, cerca, m, lit) }
        barandilla(a, apoyo, cerca)
    }

    /** Pinta [que] con la luz normal y, si hay mancha, otra vez dentro de ella (corrida al plano del frente) con esa luz. */
    private fun DrawScope.conLuz(m: ManchaAbismo?, que: DrawScope.(Float) -> Unit) {
        que(0f)
        if (m != null) clipPath(m.forma(size.width, ManchaAbismo.FRENTE)) { que(1f) }
    }

    /** La sombra de algo en la pared de detrás (de día corrida a la derecha; de noche, hacia fuera del farol). */
    private fun DrawScope.sombraPared(p: Path, desde: Offset, apoyo: Float, a: AireAbismo, cerca: (Offset) -> Float, fuerza: Float = 0.2f, lit: Float) {
        if (lit > 0f) return
        with(MuroEstanteAbismo) { sombraEnPared(p, desde, apoyo, a, cerca, fuerza) }
    }

    // ---------------------------------------------------------------- la bomba de aire

    /**
     * La bomba de aire, con el pie de su frente en [pie]: el volante de atrás asoma a la izquierda; la caja de teca (el frente con
     * sus dos paneles, la tapa y el costado en escorzo), las escuadras de latón, la chapa con el manómetro, las tapas de los
     * cilindros y la salida; el volante de delante, a la derecha, con su manivela; los patines y la trinca.
     */
    private fun DrawScope.bomba(pie: Offset, a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val bw = w * 0.31f; val bh = w * 0.175f
        val dx = w * 0.04f; val dy = -w * 0.03f
        val x0 = pie.x - bw / 2; val x1 = pie.x + bw / 2
        val y1 = pie.y - w * 0.018f; val y0 = y1 - bh
        val k = cerca(Offset(pie.x, y0))
        fun c(col: Color, f: Float = 0f, p: Offset = Offset(pie.x, y0)) = a.en(col, f, cerca(p), lit, m)
        val radio = w * 0.116f
        val eje = Offset(x1 + dx * 0.5f, y1 - bh * 0.62f + dy * 0.5f)
        val ejeAtras = Offset(x0 + dx * 0.5f - w * 0.012f, y1 - bh * 0.62f + dy * 0.5f)
        // Su sombra en la pared y en la balda.
        val silueta = Path().apply {
            addRect(Rect(x0, y0 + dy, x1 + dx, y1))
            addOval(Rect(eje.x - radio * 0.3f, eje.y - radio, eje.x + radio * 0.3f, eje.y + radio))
            addOval(Rect(ejeAtras.x - radio * 0.3f, ejeAtras.y - radio, ejeAtras.x + radio * 0.3f, ejeAtras.y + radio))
        }
        sombraPared(silueta, Offset(pie.x, y0), apoyo, a, cerca, 0.24f, lit)
        if (lit == 0f) contactoAbismo(Offset(pie.x + w * 0.02f, pie.y), bw * 1.25f, w * 0.022f, a, 0.42f)
        // El volante de atrás.
        volante(ejeAtras, radio, a, cerca, m, lit, atras = true)
        // Los patines.
        listOf(x0 - w * 0.01f, x1 - w * 0.05f).forEach { px ->
            val pr = Rect(px, y1, px + w * 0.06f, pie.y)
            drawRect(c(P.TECA_OSC, 0f, pr.center), pr.topLeft, pr.size)
            drawRect(a.brillo(0.35f, k), pr.topLeft, Size(pr.width, 1f * u))
        }
        // La tapa y el costado en escorzo.
        val tapa = poligonoAbismo(x0, y0, x1, y0, x1 + dx, y0 + dy, x0 + dx, y0 + dy)
        drawPath(tapa, c(P.TECA_LUZ, 0.08f))
        clipPath(tapa) { vetas(Rect(x0, y0 + dy, x1 + dx, y0), c(P.TECA_OSC).copy(alpha = 0.35f), 8301, densidad = 0.8f, grosor = 0.5f) }
        val costado = poligonoAbismo(x1, y0, x1 + dx, y0 + dy, x1 + dx, y1 + dy, x1, y1)
        drawPath(costado, c(P.TECA_OSC, -0.05f, Offset(x1, y0)))
        // El frente: el bastidor y los dos paneles rehundidos.
        drawRect(c(P.TECA), Offset(x0, y0), Size(bw, bh))
        clipRect(x0, y0, x1, y1) { vetas(Rect(x0, y0, x1, y1), c(P.TECA_OSC).copy(alpha = 0.35f), 8302, densidad = 0.7f, grosor = 0.6f) }
        val mar = w * 0.016f
        val pw = (bw - mar * 3) / 2
        for (j in 0 until 2) {
            val pr = Rect(x0 + mar + j * (pw + mar), y0 + bh * 0.34f, x0 + mar + j * (pw + mar) + pw, y1 - mar)
            drawRect(c(P.TECA, -0.08f, pr.center), pr.topLeft, pr.size)
            drawRect(a.sombra(0.35f), pr.topLeft, Size(pr.width, 1.6f * u))
            drawRect(a.sombra(0.25f), pr.topLeft, Size(1.6f * u, pr.height))
            drawRect(a.brillo(0.35f, k), Offset(pr.left, pr.bottom - 1f * u), Size(pr.width, 1f * u))
            drawRect(a.brillo(0.3f, k), Offset(pr.right - 1f * u, pr.top), Size(1f * u, pr.height))
        }
        // La chapa de latón de arriba del frente, con el manómetro y la llave de purga.
        val ch = Rect(x0 + mar * 0.6f, y0 + bh * 0.06f, x1 - mar * 0.6f, y0 + bh * 0.28f)
        drawRect(c(P.LATON, 0f, ch.center), ch.topLeft, ch.size)
        drawRect(a.brillo(0.55f, k), ch.topLeft, Size(ch.width, 1f * u))
        drawRect(a.sombra(0.3f), Offset(ch.left, ch.bottom - 1.2f * u), Size(ch.width, 1.2f * u))
        listOf(ch.left + 3f * u, ch.right - 3f * u).forEach { x -> clavoAbismo(Offset(x, ch.center.y), 1.1f * u, a, k, P.LATON_OSC) }
        val mc = Offset(x0 + bw * 0.36f, y0 + bh * 0.2f)
        manometro(mc, w * 0.048f, a, cerca, m, lit)
        val pu = Offset(x0 + bw * 0.72f, ch.center.y)
        drawRect(c(P.LATON_OSC), Offset(pu.x - w * 0.012f, pu.y - w * 0.006f), Size(w * 0.024f, w * 0.012f))
        drawCircle(c(P.LATON), w * 0.008f, pu)
        destelloAbismo(pu - Offset(w * 0.003f, w * 0.003f), 0.9f * u, a, k)
        // Las escuadras de latón de las esquinas.
        listOf(Offset(x0, y0) to 0, Offset(x1, y0) to 1, Offset(x0, y1) to 2, Offset(x1, y1) to 3).forEach { (p, j) ->
            val e = w * 0.022f
            val sx = if (j % 2 == 0) 1f else -1f; val sy = if (j < 2) 1f else -1f
            val esq = poligonoAbismo(p.x, p.y, p.x + sx * e, p.y, p.x + sx * e, p.y + sy * e * 0.35f, p.x + sx * e * 0.35f, p.y + sy * e * 0.35f, p.x + sx * e * 0.35f, p.y + sy * e, p.x, p.y + sy * e)
            drawPath(esq, c(P.LATON, 0f, p))
            drawPath(esq, a.sombra(0.3f), style = Stroke(0.6f * u))
            clavoAbismo(Offset(p.x + sx * e * 0.2f, p.y + sy * e * 0.2f), 0.8f * u, a, k, P.LATON_OSC)
        }
        // Las tapas de los cilindros y la salida, sobre la tapa de la caja.
        listOf(0.3f, 0.62f).forEach { fx ->
            val tc = Offset(x0 + bw * fx + dx * 0.5f, y0 + dy * 0.5f)
            val tr = w * 0.03f
            drawOval(a.sombra(0.35f), Offset(tc.x - tr + 1.5f * u, tc.y - tr * 0.35f + 1.5f * u), Size(tr * 2, tr * 0.7f))
            drawRect(c(P.LATON_OSC, 0f, tc), Offset(tc.x - tr, tc.y - w * 0.012f), Size(tr * 2, w * 0.012f))
            drawOval(c(P.LATON_OSC, 0f, tc), Offset(tc.x - tr, tc.y - tr * 0.35f), Size(tr * 2, tr * 0.7f))
            drawOval(c(P.LATON, 0.1f, tc), Offset(tc.x - tr, tc.y - w * 0.012f - tr * 0.35f), Size(tr * 2, tr * 0.7f))
            drawArc(a.brillo(0.6f, k), 180f, 120f, false, Offset(tc.x - tr, tc.y - w * 0.012f - tr * 0.35f), Size(tr * 2, tr * 0.7f), style = Stroke(1f * u))
            drawCircle(c(P.LATON_OSC, 0f, tc), 1.4f * u, Offset(tc.x, tc.y - w * 0.012f))
        }
        val sl = Offset(w * MuroEstanteAbismo.TUBO_X, y0 + dy * 0.6f)
        drawRect(c(P.LATON, 0f, sl), Offset(sl.x - w * 0.008f, sl.y - w * 0.035f), Size(w * 0.016f, w * 0.035f))
        drawRect(c(P.LATON_OSC, 0f, sl), Offset(sl.x - w * 0.012f, sl.y - w * 0.04f), Size(w * 0.024f, w * 0.01f))
        drawRect(a.brillo(0.5f, k), Offset(sl.x - w * 0.008f, sl.y - w * 0.035f), Size(1f * u, w * 0.035f))
        // La manguera de goma (la misma negra del Ideario), de la salida al racor de la tubería de cobre, con sus abrazaderas.
        val arriba = w * MuroEstanteAbismo.TUBO_FIN
        val bajo = sl.y - w * 0.04f
        val gm = w * 0.021f
        val mang = Path().apply { moveTo(sl.x, bajo); cubicTo(sl.x - w * 0.018f, bajo - (bajo - arriba) * 0.4f, sl.x + w * 0.014f, arriba + (bajo - arriba) * 0.4f, sl.x, arriba) }
        if (lit == 0f && !a.oscuro) translate(w * 0.014f, w * 0.008f) { drawPath(mang, a.sombra(0.22f), style = Stroke(gm)) }
        drawPath(mang, c(P.GOMA, 0f, Offset(sl.x, arriba)), style = Stroke(gm, cap = StrokeCap.Butt))
        drawPath(mang, c(mezcla(P.GOMA, P.LONA, 0.3f), 0f, Offset(sl.x, arriba)), style = Stroke(gm * 0.3f))
        translate(-gm * 0.25f, 0f) { drawPath(mang, a.brillo(0.4f, k), style = Stroke(gm * 0.12f)) }
        listOf(bajo + w * 0.002f, arriba - w * 0.002f).forEach { yy ->
            drawRect(c(P.LATON, 0f, Offset(sl.x, yy)), Offset(sl.x - gm * 0.65f, yy - w * 0.004f), Size(gm * 1.3f, w * 0.008f))
            drawRect(a.brillo(0.5f, k), Offset(sl.x - gm * 0.65f, yy - w * 0.004f), Size(gm * 1.3f, 1f * u))
        }
        // El filo de la tapa, el canto en sombra de la derecha y la junta del frente con el costado.
        drawRect(a.brillo(0.5f, k), Offset(x0, y0), Size(bw, 1.2f * u))
        drawRect(a.sombra(0.35f), Offset(x1 - 1.2f * u, y0), Size(1.2f * u, bh))
        // La trinca: dos cabos de los patines a los cáncamos de la balda, cada uno con sus vueltas.
        listOf(Offset(x0 - w * 0.005f, y1 + w * 0.004f) to Offset(x0 - w * 0.04f, pie.y + w * 0.004f), Offset(x1 + w * 0.004f, y1 + w * 0.004f) to Offset(x1 + w * 0.05f, pie.y + w * 0.004f)).forEach { (de, a0) ->
            drawLine(a.sombra(0.3f), de + Offset(1.5f * u, 1.5f * u), a0 + Offset(1.5f * u, 1.5f * u), 2.4f * u)
            drawLine(c(P.MANILA, 0f, de), de, a0, 2.2f * u, StrokeCap.Round)
            drawLine(c(P.MANILA_OSC, 0f, de), de + Offset(0f, 0.8f * u), a0 + Offset(0f, 0.8f * u), 0.7f * u)
            drawCircle(c(P.PLOMO, 0f, a0), w * 0.007f, a0, style = Stroke(1.6f * u))
        }
        // El volante de delante, con su manivela.
        volante(eje, radio, a, cerca, m, lit, atras = false)
        if (a.lumbre && lit == 0f) {
            // El filo del farol en lo que lo mira (el costado de la derecha y el volante).
            val kf = cerca(Offset(x1, y0))
            drawRect(a.filo(0.6f * kf), Offset(x1 + dx - 1.5f * u, y0 + dy), Size(1.5f * u, bh))
        }
    }

    /**
     * Un volante de hierro pintado de minio en escorzo, de eje [c] y radio [r]: la llanta gruesa con su luz, los cinco rayos curvos
     * (ni cruz ni asterisco), el cubo y, en el de delante, la manivela con su puño de madera.
     */
    private fun DrawScope.volante(c: Offset, r: Float, a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float, atras: Boolean) {
        val u = density
        val k = cerca(c)
        fun col(cc: Color, f: Float = 0f) = a.en(cc, if (atras) f - 0.15f else f, k, lit, m)
        // Un punto de la llanta: el círculo del plano del costado, en escorzo (la hondura va arriba a la derecha).
        fun en(t: Double, rr: Float) = Offset(c.x + rr * 0.26f * sin(t).toFloat(), c.y - rr * cos(t).toFloat() - rr * 0.18f * sin(t).toFloat())
        val llanta = Path(); val dentro = Path()
        for (i in 0..36) {
            val t = 2 * Math.PI * i / 36
            val p = en(t, r); val q = en(t, r * 0.84f)
            if (i == 0) { llanta.moveTo(p.x, p.y); dentro.moveTo(q.x, q.y) } else { llanta.lineTo(p.x, p.y); dentro.lineTo(q.x, q.y) }
        }
        val g = r * 0.14f
        if (!atras && lit == 0f) translate(r * 0.06f, r * 0.05f) { drawPath(llanta, a.sombra(0.3f), style = Stroke(g)) }
        drawPath(llanta, col(P.MINIO_OSC), style = Stroke(g))
        drawPath(llanta, col(P.MINIO), style = Stroke(g * 0.6f))
        translate(-g * 0.15f, -g * 0.15f) { drawPath(llanta, col(P.MINIO_LUZ).copy(alpha = 0.8f), style = Stroke(g * 0.18f)) }
        drawPath(dentro, col(P.MINIO_OSC).copy(alpha = 0.7f), style = Stroke(1f * u))
        // Los cinco rayos curvos.
        val rayos = Path()
        for (j in 0 until 5) {
            val t = 2 * Math.PI * j / 5 + 0.3
            val p = en(t, r * 0.86f); val mid = en(t + 0.32, r * 0.5f)
            rayos.moveTo(c.x, c.y); rayos.quadraticTo(mid.x, mid.y, p.x, p.y)
        }
        drawPath(rayos, col(P.MINIO_OSC), style = Stroke(r * 0.085f, cap = StrokeCap.Round))
        drawPath(rayos, col(P.MINIO), style = Stroke(r * 0.05f, cap = StrokeCap.Round))
        // El cubo.
        drawOval(col(P.HIERRO), Offset(c.x - r * 0.07f, c.y - r * 0.16f), Size(r * 0.14f, r * 0.32f))
        drawOval(col(P.HIERRO_LUZ), Offset(c.x - r * 0.04f, c.y - r * 0.12f), Size(r * 0.06f, r * 0.14f))
        if (atras) return
        // La manivela: el brazo hasta cerca de la llanta y el puño de madera que sale hacia fuera.
        val pm = en(2.3, r * 0.72f)
        drawLine(col(P.HIERRO), c, pm, r * 0.07f, StrokeCap.Round)
        val puno = Rect(pm.x - r * 0.02f, pm.y - r * 0.05f, pm.x + r * 0.3f, pm.y + r * 0.05f)
        drawRoundRect(a.sombra(0.3f), Offset(puno.left + 1.5f * u, puno.top + 2f * u), puno.size, CornerRadius(r * 0.05f))
        drawRoundRect(col(P.TECA_OSC), puno.topLeft, puno.size, CornerRadius(r * 0.05f))
        drawRect(col(P.TECA_LUZ), Offset(puno.left + r * 0.03f, puno.top + r * 0.015f), Size(puno.width * 0.8f, r * 0.025f))
        drawCircle(col(P.LATON), r * 0.04f, Offset(puno.right, pm.y))
        destelloAbismo(en(5.6, r * 0.95f), 1.4f * u, a, k)
        if (a.lumbre && lit == 0f) drawPath(llanta, a.filo(0.5f * k), style = Stroke(1.2f * u))
    }

    /** El manómetro de la bomba en [c], de radio [r]: el bisel de latón, la esfera de papel, las marcas sin cifras, la aguja y el reflejo. */
    private fun DrawScope.manometro(c: Offset, r: Float, a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val u = density
        val k = cerca(c)
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        drawCircle(a.sombra(0.35f), r * 1.05f, c + Offset(r * 0.15f, r * 0.18f))
        drawCircle(col(P.LATON), r, c)
        drawArc(col(P.LATON_LUZ), 180f, 120f, false, Offset(c.x - r * 0.88f, c.y - r * 0.88f), Size(r * 1.76f, r * 1.76f), style = Stroke(r * 0.1f))
        val cara = r * 0.76f
        drawCircle(col(mezcla(P.PAPEL, Color.White, 0.3f)), cara, c)
        drawCircle(a.sombra(0.2f), cara, c + Offset(r * 0.05f, r * 0.08f), style = Stroke(r * 0.1f))
        for (j in 0..10) {
            val ang = Math.toRadians(135.0 + j * 27.0)
            val p0 = Offset(c.x + cara * 0.72f * cos(ang).toFloat(), c.y + cara * 0.72f * sin(ang).toFloat())
            val p1 = Offset(c.x + cara * 0.9f * cos(ang).toFloat(), c.y + cara * 0.9f * sin(ang).toFloat())
            drawLine(col(P.TINTA).copy(alpha = 0.7f), p0, p1, (if (j % 5 == 0) 1.2f else 0.6f) * u)
        }
        drawArc(col(P.MINIO).copy(alpha = 0.85f), 330f, 75f, false, Offset(c.x - cara * 0.8f, c.y - cara * 0.8f), Size(cara * 1.6f, cara * 1.6f), style = Stroke(1.3f * u))
        val ang = Math.toRadians(135.0 + 270.0 * 0.36)
        drawLine(col(P.TINTA), c, Offset(c.x + cara * 0.75f * cos(ang).toFloat(), c.y + cara * 0.75f * sin(ang).toFloat()), 1.2f * u, StrokeCap.Round)
        drawCircle(col(P.LATON), 1.6f * u, c)
        if (a.oscuro) drawArc(a.brillo(0.8f, k), 10f, 60f, false, Offset(c.x - cara * 0.85f, c.y - cara * 0.85f), Size(cara * 1.7f, cara * 1.7f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        else drawArc(luzMueble(0.7f), 195f, 70f, false, Offset(c.x - cara * 0.8f, c.y - cara * 0.8f), Size(cara * 1.6f, cara * 1.6f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- el cabo, la bola, la caracola, la aceitera y el bichero

    /**
     * El cabo de vida de manila adujado en espiral plana sobre la balda (la aduja que se hace para que corra sin cocas): las
     * vueltas con su colchado, la bola de vidrio verde de las redes en su red de cordel, en el centro, y el gaza que cae por la
     * cornisa con su falcaceado.
     */
    private fun DrawScope.cabo(pie: Offset, a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val k = cerca(pie)
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        val rx = w * 0.09f; val ry = w * 0.03f
        val cc = Offset(pie.x, pie.y - ry - w * 0.004f)
        if (lit == 0f) contactoAbismo(Offset(pie.x + w * 0.01f, pie.y - w * 0.006f), rx * 2.2f, w * 0.022f, a, 0.4f)
        // La espiral: de fuera adentro, cada vuelta con su sombra abajo, su cara y su luz arriba.
        val g = w * 0.011f
        val esp = Path(); val tic = Path()
        val vueltas = 4.4f
        val n = 160
        for (i in 0..n) {
            val f = i / n.toFloat()
            val t = f * vueltas * 2 * Math.PI
            val rr = 1f - 0.78f * f
            val x = cc.x + rx * rr * cos(t).toFloat(); val y = cc.y + ry * rr * sin(t).toFloat()
            if (i == 0) esp.moveTo(x, y) else esp.lineTo(x, y)
            if (i % 2 == 0) { tic.moveTo(x - g * 0.3f, y - g * 0.35f); tic.lineTo(x + g * 0.25f, y + g * 0.3f) }
        }
        translate(0f, g * 0.35f) { drawPath(esp, a.sombra(0.45f), style = Stroke(g)) }
        drawPath(esp, col(P.MANILA_OSC), style = Stroke(g))
        drawPath(esp, col(P.MANILA), style = Stroke(g * 0.7f))
        drawPath(tic, col(P.MANILA_OSC).copy(alpha = 0.8f), style = Stroke(0.8f * u))
        translate(-g * 0.12f, -g * 0.22f) { drawPath(esp, a.brillo(0.35f, k), style = Stroke(g * 0.15f)) }
        // La bola de vidrio en su red, en el centro de la aduja.
        val br = w * 0.04f
        val bc = Offset(cc.x + w * 0.005f, cc.y - br * 0.75f)
        sombraPared(Path().apply { addOval(Rect(bc, br)) }, bc, apoyo, a, cerca, 0.16f, lit)
        drawOval(a.sombra(0.45f), Offset(bc.x - br * 0.8f, cc.y - br * 0.1f), Size(br * 1.8f, br * 0.45f))
        drawCircle(col(Color(0xFF2E6E56)), br, bc)
        drawCircle(col(Color(0xFF4E9A78), 0.05f), br * 0.78f, bc - Offset(br * 0.15f, br * 0.15f))
        drawCircle(col(Color(0xFF7BC0A0), 0.1f), br * 0.4f, bc - Offset(br * 0.38f, br * 0.38f))
        drawArc(a.sombra(0.4f), -10f, 160f, false, Offset(bc.x - br, bc.y - br), Size(br * 2, br * 2), style = Stroke(br * 0.18f))
        drawCircle(if (a.oscuro) P.LLAMA.copy(alpha = (0.9f * k).coerceAtMost(0.9f)) else luzMueble(0.95f), br * 0.12f, bc - Offset(br * 0.42f, br * 0.45f))
        // La red de cordel: meridianos y paralelos sobre la bola.
        val red = Path()
        listOf(-0.55f, 0f, 0.55f).forEach { f -> red.addOval(Rect(bc.x - br * kotlin.math.abs(f) - 0.5f, bc.y - br, bc.x + br * kotlin.math.abs(f) + 0.5f, bc.y + br)) }
        red.moveTo(bc.x - br * 0.95f, bc.y - br * 0.3f); red.quadraticTo(bc.x, bc.y - br * 0.05f, bc.x + br * 0.95f, bc.y - br * 0.3f)
        red.moveTo(bc.x - br * 0.9f, bc.y + br * 0.4f); red.quadraticTo(bc.x, bc.y + br * 0.65f, bc.x + br * 0.9f, bc.y + br * 0.4f)
        clipPath(Path().apply { addOval(Rect(bc, br)) }) { drawPath(red, col(P.ESPARTO), style = Stroke(1f * u)) }
        // El chicote que cae por la cornisa: la gaza con su falcaceado.
        val ch0 = Offset(cc.x + rx * 0.95f, cc.y + ry * 0.2f)
        val cae = Path().apply { moveTo(ch0.x, ch0.y); cubicTo(ch0.x + w * 0.02f, ch0.y + w * 0.01f, ch0.x + w * 0.01f, apoyo + w * 0.03f, ch0.x - w * 0.012f, apoyo + w * 0.06f) }
        if (lit == 0f) translate(w * 0.006f, w * 0.004f) { drawPath(cae, a.sombra(0.35f), style = Stroke(g)) }
        drawPath(cae, col(P.MANILA_OSC), style = Stroke(g, cap = StrokeCap.Round))
        drawPath(cae, col(P.MANILA), style = Stroke(g * 0.65f, cap = StrokeCap.Round))
        val gz = Offset(ch0.x - w * 0.016f, apoyo + w * 0.07f)
        drawOval(col(P.MANILA_OSC), Offset(gz.x - w * 0.014f, gz.y - w * 0.014f), Size(w * 0.028f, w * 0.03f), style = Stroke(g * 0.7f))
        drawRect(col(P.TINTA).copy(alpha = 0.7f), Offset(gz.x - g * 0.45f, gz.y - w * 0.026f), Size(g * 0.9f, w * 0.008f))
    }

    /**
     * La caracola reina (la de los cayos de Florida), tendida con la boca hacia delante: la espira con sus nudos a la izquierda, el
     * cuerpo con sus vueltas y el labio ancho rosado, brillante por dentro.
     */
    private fun DrawScope.caracola(pie: Offset, a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val k = cerca(pie)
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        val s = w * 0.075f
        val cx = pie.x; val by = pie.y - w * 0.004f
        val cuerpo = Path().apply {
            moveTo(cx - s * 1.15f, by - s * 0.62f)
            cubicTo(cx - s * 0.6f, by - s * 1.05f, cx + s * 0.2f, by - s * 1.12f, cx + s * 0.62f, by - s * 0.82f)
            cubicTo(cx + s * 0.95f, by - s * 0.55f, cx + s * 0.92f, by - s * 0.05f, cx + s * 0.5f, by)
            lineTo(cx - s * 0.45f, by)
            cubicTo(cx - s * 0.8f, by - s * 0.1f, cx - s * 1.05f, by - s * 0.35f, cx - s * 1.15f, by - s * 0.62f)
            close()
        }
        sombraPared(cuerpo, Offset(cx, by - s * 0.5f), apoyo, a, cerca, 0.2f, lit)
        if (lit == 0f) contactoAbismo(Offset(cx + w * 0.01f, pie.y), s * 2.1f, w * 0.018f, a, 0.4f)
        drawPath(cuerpo, col(P.CONCHA))
        clipPath(cuerpo) {
            // Las vueltas de la concha, con sus nudos.
            for (j in 0 until 4) {
                val x = cx - s * (0.95f - 0.38f * j)
                drawLine(col(P.CONCHA, -0.25f), Offset(x, by - s * 1.1f), Offset(x + s * 0.25f, by), 1.4f * u)
                drawCircle(col(P.CONCHA_LUZ, 0.1f), s * 0.1f, Offset(x + s * 0.08f, by - s * (0.78f - 0.06f * j)))
                drawCircle(a.sombra(0.2f), s * 0.1f, Offset(x + s * 0.12f, by - s * (0.74f - 0.06f * j)), style = Stroke(1f * u))
            }
            drawRect(a.sombra(0.25f), Offset(cx - s * 1.2f, by - s * 0.3f), Size(s * 2.4f, s * 0.3f))
            drawRect(a.brillo(0.3f, k), Offset(cx - s * 1.2f, by - s * 1.1f), Size(s * 2.4f, s * 0.22f))
        }
        // El labio rosado, hacia delante a la derecha.
        val labio = Path().apply {
            moveTo(cx - s * 0.1f, by - s * 0.05f)
            cubicTo(cx + s * 0.05f, by - s * 0.72f, cx + s * 0.6f, by - s * 1.0f, cx + s * 1.05f, by - s * 0.78f)
            cubicTo(cx + s * 1.25f, by - s * 0.5f, cx + s * 1.05f, by - s * 0.1f, cx + s * 0.6f, by)
            close()
        }
        drawPath(labio, col(P.CONCHA_ROSA))
        clipPath(labio) {
            drawOval(col(mezcla(P.CONCHA_ROSA, P.CONCHA_LUZ, 0.5f), 0.1f), Offset(cx + s * 0.15f, by - s * 0.75f), Size(s * 0.8f, s * 0.5f))
            drawOval(a.sombra(0.3f), Offset(cx - s * 0.05f, by - s * 0.35f), Size(s * 0.6f, s * 0.4f))
        }
        drawPath(labio, col(P.CONCHA_LUZ, 0.2f), style = Stroke(1.2f * u))
        destelloAbismo(Offset(cx + s * 0.55f, by - s * 0.68f), 1.3f * u, a, k)
    }

    /** La aceitera de la bomba: el cuerpo de hojalata cónico con su pulsador de latón y el pico largo y fino. */
    private fun DrawScope.aceitera(pie: Offset, a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val k = cerca(pie)
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        val bw = w * 0.05f; val bh = w * 0.045f
        val by = pie.y - w * 0.006f
        val cono = poligonoAbismo(pie.x - bw / 2, by, pie.x + bw / 2, by, pie.x + bw * 0.16f, by - bh, pie.x - bw * 0.16f, by - bh)
        if (lit == 0f) contactoAbismo(Offset(pie.x + w * 0.008f, pie.y), bw * 1.5f, w * 0.014f, a, 0.4f)
        drawPath(cono, col(P.HOJALATA))
        clipPath(cono) { cilindroAbismo(Rect(pie.x - bw / 2, by - bh, pie.x + bw / 2, by), a, k, if (a.oscuro) 1f else -1f, 1.2f) }
        drawRect(col(P.HOJALATA, -0.2f), Offset(pie.x - bw / 2, by - w * 0.006f), Size(bw, w * 0.006f))
        drawRect(col(P.LATON), Offset(pie.x - bw * 0.14f, by - bh - w * 0.012f), Size(bw * 0.28f, w * 0.012f))
        drawCircle(col(P.LATON_OSC), w * 0.006f, Offset(pie.x, by - bh - w * 0.014f))
        val pico = Path().apply { moveTo(pie.x + bw * 0.1f, by - bh - w * 0.006f); quadraticTo(pie.x + bw * 0.4f, by - bh - w * 0.05f, pie.x - bw * 0.6f, by - bh - w * 0.11f) }
        drawPath(pico, col(P.HOJALATA, -0.15f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawPath(pico, a.brillo(0.4f, k), style = Stroke(0.6f * u))
        drawOval(col(P.ESPONJA_OSC).copy(alpha = 0.5f), Offset(pie.x - bw * 0.3f, by - bh * 0.35f), Size(bw * 0.35f, bh * 0.25f))
    }

    /** El bichero apoyado en la curva del bao: el asta de fresno y el hierro con su gancho y su punta. */
    private fun DrawScope.bichero(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val p0 = Offset(w * 0.985f, apoyo); val p1 = Offset(w * 0.925f, w * 0.085f)
        val k = cerca(Offset(w * 0.95f, w * 0.3f))
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        val asta = Path().apply { moveTo(p0.x, p0.y); lineTo(p1.x, p1.y) }
        if (lit == 0f) {
            val so = Path().apply { moveTo(p0.x, p0.y); lineTo(p1.x + w * 0.02f, p1.y + w * 0.012f) }
            if (!a.oscuro) drawPath(so, a.sombra(0.2f), style = Stroke(w * 0.012f))
        }
        drawPath(asta, col(P.ESPARTO, -0.1f), style = Stroke(w * 0.011f, cap = StrokeCap.Round))
        translate(-w * 0.003f, 0f) { drawPath(asta, col(P.ESPARTO, 0.2f), style = Stroke(w * 0.003f)) }
        // El hierro: el cuello, el gancho vuelto hacia la izquierda y la punta.
        val cu = Offset(p1.x - w * 0.003f, p1.y + w * 0.02f)
        drawLine(col(P.HIERRO), cu, Offset(p1.x - w * 0.006f, p1.y - w * 0.012f), w * 0.009f, StrokeCap.Round)
        val gancho = Path().apply { moveTo(p1.x - w * 0.003f, p1.y); cubicTo(p1.x - w * 0.03f, p1.y - w * 0.005f, p1.x - w * 0.03f, p1.y + w * 0.025f, p1.x - w * 0.016f, p1.y + w * 0.02f) }
        drawPath(gancho, col(P.HIERRO), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
        drawPath(gancho, a.brillo(0.35f, k), style = Stroke(0.6f * u))
        drawCircle(col(P.HIERRO_LUZ), w * 0.004f, Offset(p1.x - w * 0.006f, p1.y - w * 0.014f))
        // Las vueltas de piola que sujetan el hierro.
        for (j in 0 until 3) drawLine(col(P.MANILA), Offset(cu.x - w * 0.007f, cu.y + j * 2f * u), Offset(cu.x + w * 0.007f, cu.y + j * 2f * u - 1f * u), 1.2f * u)
    }

    // ---------------------------------------------------------------- la barandilla

    /** La barandilla de mar de la balda de arriba: los candeleros de latón y el pasamanos de teca, delante de lo que hay encima. */
    private fun DrawScope.barandilla(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val alto = w * 0.024f
        val y1 = apoyo + w * 0.006f; val y0 = y1 - alto
        listOf(0.015f, 0.19f, 0.405f, 0.7f, 0.985f).forEach { fx ->
            val x = w * fx
            val k = cerca(Offset(x, y0))
            drawRect(a.sombra(0.3f), Offset(x + 1.5f * u, y0 + 2f * u), Size(w * 0.006f, alto))
            drawRect(a.dia(P.LATON, 0f, k), Offset(x - w * 0.003f, y0), Size(w * 0.006f, alto))
            drawRect(a.brillo(0.45f, k), Offset(x - w * 0.003f, y0), Size(1f * u, alto))
            drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(x - w * 0.005f, y1 - w * 0.004f), Size(w * 0.01f, w * 0.004f))
        }
        val g = w * 0.009f
        val n = if (a.lumbre) 24 else 1
        for (t in 0 until n) {
            val x0 = w * t / n
            val k = cerca(Offset(x0 + w / n / 2, y0))
            drawRect(a.sombra(0.25f), Offset(x0, y0 + g), Size(w / n + 0.5f, 2.4f * u))
            drawRoundRect(a.dia(mezcla(P.TECA, P.TECA_LUZ, 0.3f), 0f, k), Offset(x0, y0 - g * 0.2f), Size(w / n + 0.5f, g), CornerRadius(if (n == 1) g / 2 else 0f))
            drawRect(a.brillo(0.6f, k), Offset(x0, y0 - g * 0.2f), Size(w / n + 0.5f, 1f * u))
        }
    }
}
