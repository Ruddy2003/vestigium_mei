package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Las flores del estante de Art nouveau ([MuebleEstanteNouveau]): seis flores inventadas, dibujadas a la manera de 1900 (la
 * planta como adorno, no como lámina), siempre de perfil o ladeadas y con su tallo y su hoja, para que ninguna se lea como un
 * emblema (una flor de tres pétalos de frente sería una flor de lis): la campanilla que cuelga de un tallo arqueado, la copa
 * de amapola con su capullo, la trompeta de lirio, el cardo de penacho, la umbela de rayos y la rosa silvestre de cinco
 * pétalos. «Cada día trae su flor»: cada entrada lleva la suya, que sale de su semilla ([tipo]), dorada en el lomo del tomo,
 * impresa en el papel de los rollos o seca bajo la cinta del legajo. Las formas se arman una vez, en un cuadro de medio alto
 * 1 centrado en el origen, y se dibujan con una transformación (ningún camino nuevo por volumen).
 */
internal object FloresNouveau {
    const val TIPOS = 6

    /** La flor de una entrada, de su semilla. */
    fun tipo(semilla: Int): Int = Math.floorMod(semilla * 7 + (semilla ushr 11) + (semilla ushr 3), TIPOS)

    /**
     * Las partes de una flor: el tallo (trazo), las hojas, la flor, lo de dentro (el corazón, los puntos), lo que va delante de él
     * (los pétalos de delante de la copa de la amapola) y los pliegues de los pétalos (trazo fino), en un cuadro de medio alto 1.
     */
    class Partes(val tallo: Path, val hojas: Path, val flor: Path, val dentro: Path, val pelos: Path, val frente: Path, val pliegues: Path)

    /** Una hoja de lanza de [b] a [t] con su ancho [an] (fracción del largo) y su comba. */
    private fun Path.hoja(bx: Float, by: Float, tx: Float, ty: Float, an: Float, comba: Float = 0.15f) {
        val dx = tx - bx; val dy = ty - by
        val nx = -dy; val ny = dx
        moveTo(bx, by)
        cubicTo(bx + dx * 0.3f + nx * (an + comba), by + dy * 0.3f + ny * (an + comba), bx + dx * 0.75f + nx * (an * 0.6f + comba), by + dy * 0.75f + ny * (an * 0.6f + comba), tx, ty)
        cubicTo(bx + dx * 0.75f - nx * (an * 0.6f - comba), by + dy * 0.75f - ny * (an * 0.6f - comba), bx + dx * 0.3f - nx * (an - comba), by + dy * 0.3f - ny * (an - comba), bx, by)
        close()
    }

    /** Una campana que cuelga de ([x], [y]), de alto [s], con la boca de tres puntas abierta abajo. */
    private fun Path.campana(x: Float, y: Float, s: Float) {
        moveTo(x, y)
        cubicTo(x - s * 0.28f, y + s * 0.1f, x - s * 0.42f, y + s * 0.6f, x - s * 0.56f, y + s * 0.98f)
        quadraticTo(x - s * 0.3f, y + s * 0.82f, x - s * 0.14f, y + s * 1.02f)
        quadraticTo(x, y + s * 0.84f, x + s * 0.16f, y + s * 1.02f)
        quadraticTo(x + s * 0.32f, y + s * 0.82f, x + s * 0.56f, y + s * 0.96f)
        cubicTo(x + s * 0.4f, y + s * 0.6f, x + s * 0.28f, y + s * 0.1f, x, y)
        close()
    }

    private fun armar(tipo: Int): Partes {
        val tallo = Path(); val hojas = Path(); val flor = Path(); val dentro = Path(); val pelos = Path(); val frente = Path(); val pliegues = Path()
        when (tipo) {
            0 -> { // La campanilla: el tallo se arquea a la derecha y cuelgan dos campanas.
                tallo.moveTo(-0.3f, 1f); tallo.cubicTo(-0.46f, 0.1f, -0.2f, -0.95f, 0.3f, -0.86f)
                tallo.cubicTo(0.5f, -0.83f, 0.56f, -0.72f, 0.52f, -0.56f)
                tallo.moveTo(-0.27f, -0.5f); tallo.quadraticTo(-0.1f, -0.62f, -0.02f, -0.42f)
                flor.campana(0.52f, -0.58f, 0.62f)
                flor.campana(-0.02f, -0.44f, 0.4f)
                hojas.hoja(-0.36f, 0.5f, -0.86f, 0.06f, 0.22f, 0.04f)
                hojas.hoja(-0.33f, 0.2f, 0.2f, 0.12f, 0.16f, -0.03f)
                dentro.addOval(Rect(0.47f, -0.1f, 0.57f, 0.02f))
            }
            1 -> { // La amapola, de tres cuartos y ladeada: dos pétalos de atrás que suben, el corazón chico con sus estambres y dos de
                // delante que se solapan, cada uno con sus pliegues; y su capullo que cuelga.
                tallo.moveTo(0.12f, 1f); tallo.cubicTo(0.38f, 0.45f, -0.32f, 0.12f, -0.02f, -0.38f)
                tallo.moveTo(0.02f, 0.22f); tallo.cubicTo(0.3f, 0.12f, 0.52f, 0.0f, 0.56f, -0.1f)
                // Los dos pétalos de atrás, de borde ondulado.
                flor.moveTo(-0.06f, -0.5f)
                flor.cubicTo(-0.52f, -0.5f, -0.7f, -0.84f, -0.46f, -0.98f)
                flor.quadraticTo(-0.36f, -0.9f, -0.28f, -1.0f)
                flor.cubicTo(-0.14f, -1.04f, -0.06f, -0.9f, -0.02f, -0.74f)
                flor.close()
                flor.moveTo(0.0f, -0.52f)
                flor.cubicTo(0.44f, -0.54f, 0.62f, -0.8f, 0.46f, -0.96f)
                flor.quadraticTo(0.34f, -0.9f, 0.24f, -1.02f)
                flor.cubicTo(0.1f, -1.04f, 0.02f, -0.92f, -0.02f, -0.74f)
                flor.close()
                // El capullo que cuelga, con sus dos sépalos.
                flor.moveTo(0.57f, -0.14f)
                flor.cubicTo(0.7f, -0.12f, 0.72f, 0.08f, 0.6f, 0.16f)
                flor.cubicTo(0.48f, 0.1f, 0.46f, -0.1f, 0.57f, -0.14f)
                flor.close()
                // El corazón: el ovario chico y el anillo de estambres, medio tapados por los pétalos de delante.
                dentro.addOval(Rect(-0.15f, -0.74f, 0.13f, -0.6f))
                pelos.moveTo(-0.18f, -0.66f); pelos.lineTo(-0.24f, -0.76f)
                pelos.moveTo(0.14f, -0.66f); pelos.lineTo(0.22f, -0.77f)
                pelos.moveTo(-0.06f, -0.72f); pelos.lineTo(-0.07f, -0.82f)
                pelos.moveTo(0.05f, -0.72f); pelos.lineTo(0.07f, -0.82f)
                // Los dos pétalos de delante, que se solapan.
                frente.moveTo(-0.02f, -0.36f)
                frente.cubicTo(-0.46f, -0.3f, -0.7f, -0.5f, -0.56f, -0.66f)
                frente.quadraticTo(-0.46f, -0.64f, -0.4f, -0.72f)
                frente.cubicTo(-0.26f, -0.76f, -0.12f, -0.68f, 0.02f, -0.64f)
                frente.close()
                frente.moveTo(0.06f, -0.34f)
                frente.cubicTo(0.44f, -0.3f, 0.64f, -0.46f, 0.54f, -0.62f)
                frente.quadraticTo(0.44f, -0.62f, 0.38f, -0.7f)
                frente.cubicTo(0.24f, -0.74f, 0.1f, -0.66f, -0.06f, -0.6f)
                frente.close()
                // Los pliegues: rayas finas del arranque al borde de cada pétalo.
                listOf(-0.42f to -0.9f, -0.22f to -0.94f, 0.3f to -0.92f, 0.14f to -0.96f).forEach { (x, y) -> pliegues.moveTo(-0.02f, -0.6f); pliegues.quadraticTo(x * 0.6f, (y - 0.6f) * 0.5f, x, y) }
                listOf(-0.5f to -0.6f, -0.32f to -0.68f, 0.46f to -0.56f, 0.28f to -0.66f).forEach { (x, y) -> pliegues.moveTo(0f, -0.4f); pliegues.quadraticTo(x * 0.5f, -0.4f + (y + 0.4f) * 0.4f, x, y) }
                hojas.hoja(0.2f, 0.66f, -0.52f, 0.78f, 0.2f, 0.06f)
                hojas.hoja(0.14f, 0.5f, 0.74f, 0.4f, 0.14f, -0.05f)
            }
            2 -> { // El lirio: la trompeta que se abre arriba a la derecha con sus puntas vueltas.
                tallo.moveTo(-0.22f, 1f); tallo.cubicTo(-0.12f, 0.5f, -0.1f, 0.2f, 0.0f, -0.1f)
                flor.moveTo(-0.06f, -0.06f)
                flor.cubicTo(-0.08f, -0.4f, -0.08f, -0.7f, 0.0f, -0.96f)
                flor.quadraticTo(0.14f, -0.72f, 0.3f, -0.74f)
                flor.quadraticTo(0.38f, -0.98f, 0.52f, -1.02f)
                flor.quadraticTo(0.5f, -0.78f, 0.56f, -0.66f)
                flor.quadraticTo(0.74f, -0.74f, 0.86f, -0.66f)
                flor.cubicTo(0.6f, -0.5f, 0.3f, -0.18f, 0.08f, -0.04f)
                flor.close()
                hojas.hoja(-0.2f, 0.84f, -0.66f, 0.0f, 0.12f, 0.05f)
                hojas.hoja(-0.15f, 0.55f, 0.42f, 0.2f, 0.11f, -0.04f)
                pelos.moveTo(0.18f, -0.4f); pelos.lineTo(0.42f, -0.9f)
                pelos.moveTo(0.22f, -0.38f); pelos.lineTo(0.58f, -0.82f)
                dentro.addOval(Rect(0.36f, -0.96f, 0.47f, -0.86f)); dentro.addOval(Rect(0.53f, -0.88f, 0.64f, -0.78f))
            }
            3 -> { // El cardo: el penacho como una cúpula de flecos sobre su cáliz de escamas, ladeado, y la hoja dentada.
                tallo.moveTo(0.04f, 1f); tallo.cubicTo(0.12f, 0.5f, -0.04f, 0.1f, 0.06f, -0.22f)
                flor.moveTo(-0.24f, -0.42f)
                flor.cubicTo(-0.26f, -0.26f, -0.12f, -0.18f, 0.06f, -0.18f)
                flor.cubicTo(0.26f, -0.18f, 0.38f, -0.28f, 0.34f, -0.44f)
                flor.close()
                // La cúpula del penacho: un borde de flecos que sube de un lado al otro, más alto a la derecha.
                flor.moveTo(-0.3f, -0.44f)
                val n = 9
                for (j in 0..n) {
                    val t = j / n.toFloat()
                    val ang = (PI * (1.0 + t)).toFloat()
                    val rx = 0.36f; val ry = 0.42f + 0.08f * t
                    val r = if (j % 2 == 0) 1f else 0.82f
                    flor.lineTo(0.03f + rx * r * cos(ang), -0.44f + ry * r * sin(ang))
                }
                flor.lineTo(0.4f, -0.44f)
                flor.close()
                hojas.moveTo(0.06f, 0.42f)
                hojas.lineTo(-0.2f, 0.36f); hojas.lineTo(-0.26f, 0.22f); hojas.lineTo(-0.44f, 0.24f); hojas.lineTo(-0.5f, 0.08f)
                hojas.lineTo(-0.74f, 0.06f); hojas.lineTo(-0.56f, 0.2f); hojas.lineTo(-0.6f, 0.34f); hojas.lineTo(-0.36f, 0.4f)
                hojas.lineTo(-0.32f, 0.52f); hojas.close()
                dentro.moveTo(-0.16f, -0.36f); dentro.lineTo(0.02f, -0.26f); dentro.lineTo(-0.02f, -0.36f); dentro.close()
                dentro.moveTo(0.1f, -0.36f); dentro.lineTo(0.26f, -0.27f); dentro.lineTo(0.22f, -0.38f); dentro.close()
            }
            4 -> { // La umbela: los rayos que salen de lo alto del tallo y, en cada uno, un ramillete de puntos.
                tallo.moveTo(-0.08f, 1f); tallo.cubicTo(0.0f, 0.5f, -0.06f, 0.1f, 0.0f, -0.16f)
                val angs = floatArrayOf(-165f, -128f, -96f, -64f, -26f)
                angs.forEachIndexed { j, g ->
                    val ra = Math.toRadians(g.toDouble())
                    val l = if (j == 2) 0.64f else 0.56f
                    val ex = l * cos(ra).toFloat(); val ey = -0.16f + l * sin(ra).toFloat() * 0.92f
                    tallo.moveTo(0.0f, -0.16f); tallo.quadraticTo(ex * 0.4f, -0.16f + (ey + 0.16f) * 0.75f, ex, ey)
                    for (q in 0 until 3) {
                        val qa = ra + (q - 1) * 0.7
                        val cx = ex + 0.1f * cos(qa).toFloat(); val cy = ey + 0.1f * sin(qa).toFloat()
                        flor.addOval(Rect(cx - 0.085f, cy - 0.085f, cx + 0.085f, cy + 0.085f))
                    }
                }
                hojas.hoja(-0.06f, 0.6f, -0.62f, 0.3f, 0.14f, 0.05f)
                hojas.hoja(-0.04f, 0.78f, 0.5f, 0.64f, 0.12f, -0.04f)
            }
            else -> { // La rosa silvestre: cinco pétalos redondos ladeados sobre un tallo corto, y dos hojas.
                tallo.moveTo(-0.26f, 1f); tallo.cubicTo(-0.1f, 0.5f, -0.18f, 0.06f, 0.02f, -0.24f)
                val cx = 0.06f; val cy = -0.5f
                for (j in 0 until 5) {
                    val ang = (2 * PI * j / 5 - PI / 2 + 0.3).toFloat()
                    val px = cx + 0.27f * cos(ang); val py = cy + 0.24f * sin(ang)
                    flor.addOval(Rect(px - 0.21f, py - 0.19f, px + 0.21f, py + 0.19f))
                }
                hojas.hoja(-0.14f, 0.4f, -0.68f, 0.18f, 0.24f, 0.06f)
                hojas.hoja(-0.12f, 0.2f, 0.4f, 0.0f, 0.2f, -0.05f)
                dentro.addOval(Rect(cx - 0.11f, cy - 0.1f, cx + 0.11f, cy + 0.1f))
            }
        }
        return Partes(tallo, hojas, flor, dentro, pelos, frente, pliegues)
    }

    private val PARTES: Array<Partes> by lazy { Array(TIPOS) { armar(it) } }

    fun partes(tipo: Int): Partes = PARTES[Math.floorMod(tipo, TIPOS)]

    /**
     * La flor [tipo] estampada en un solo color [c] (dorada, impresa), en el cuadro de centro [o] y medio alto [r]; [hueco] es el
     * color del fondo, con que se calan el corazón y los puntos. [espejo] la voltea a lo ancho.
     */
    fun DrawScope.estampa(tipo: Int, o: Offset, r: Float, c: Color, hueco: Color? = null, espejo: Boolean = false) {
        if (r <= 0.5f) return
        val p = partes(tipo)
        withTransform({ translate(o.x, o.y); scale(if (espejo) -r else r, r, Offset.Zero) }) {
            drawPath(p.tallo, c, style = Stroke(0.085f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(p.pelos, c, style = Stroke(0.07f, cap = StrokeCap.Round))
            drawPath(p.hojas, c)
            drawPath(p.flor, c)
            if (hueco != null) drawPath(p.dentro, hueco)
            if (hueco != null) drawPath(p.frente, hueco, style = Stroke(0.1f))
            drawPath(p.frente, c)
            if (hueco != null) drawPath(p.pliegues, hueco.copy(alpha = 0.6f * hueco.alpha), style = Stroke(0.04f, cap = StrokeCap.Round))
        }
    }

    /**
     * La flor [tipo] de color, en el cuadro de centro [o] y medio alto [r], girada [giro] grados: el tallo y las hojas en
     * [hoja] (con su sombra), la flor en [flor] con su luz arriba a la izquierda y su contorno, y el corazón en [centro].
     */
    fun DrawScope.deColor(tipo: Int, o: Offset, r: Float, flor: Color, hoja: Color, centro: Color, contorno: Color,
                          giro: Float = 0f, espejo: Boolean = false, luz: Color = Color.Transparent) {
        if (r <= 0.5f) return
        val p = partes(tipo)
        withTransform({ translate(o.x, o.y); rotate(giro, Offset.Zero); scale(if (espejo) -r else r, r, Offset.Zero) }) {
            drawPath(p.tallo, mezcla(hoja, contorno, 0.25f), style = Stroke(0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(p.hojas, hoja)
            drawPath(p.hojas, contorno.copy(alpha = 0.5f * contorno.alpha), style = Stroke(0.03f))
            drawPath(p.flor, flor)
            if (luz.alpha > 0f) withTransform({ translate(-0.05f, -0.05f); scale(0.82f, 0.82f, Offset(0.1f, -0.5f)) }) { drawPath(p.flor, luz) }
            drawPath(p.flor, contorno.copy(alpha = 0.7f * contorno.alpha), style = Stroke(0.035f))
            drawPath(p.pelos, centro, style = Stroke(0.05f, cap = StrokeCap.Round))
            drawPath(p.dentro, centro)
            drawPath(p.frente, mezcla(flor, luz.copy(alpha = 1f), 0.12f * luz.alpha))
            drawPath(p.frente, contorno.copy(alpha = 0.7f * contorno.alpha), style = Stroke(0.035f))
            drawPath(p.pliegues, mezcla(flor, contorno, 0.5f).copy(alpha = 0.7f * flor.alpha), style = Stroke(0.025f, cap = StrokeCap.Round))
        }
    }
}
