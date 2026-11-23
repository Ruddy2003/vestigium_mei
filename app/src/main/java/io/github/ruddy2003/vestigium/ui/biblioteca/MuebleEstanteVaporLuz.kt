package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect

/**
 * La luz que entra por la ventana en los Sueños de Vapor (ver [MuebleEstanteVapor]): de noche, la luna en tres haces que bajan
 * en diagonal por la pared y cruzan el mueble (los dos montantes de la ventana los separan); al anochecer, el paño del ocaso con
 * la forma de los quince cuarterones. No es un vidrio de color puesto delante: pinta lo que toca.
 * - Donde cae, la superficie toma el tono de la luz conservando su luminosidad (`BlendMode.Color`: la pared se vuelve naranja o
 *   plata sin perder sus pinceladas, su cenefa ni su grieta) y se aclara en proporción a lo que ya tiene (`BlendMode.ColorDodge`:
 *   lo oscuro sigue oscuro, los colores se conservan, más claros y entibiados o enfriados). Las entradas y lo de encima del mueble
 *   solo se aclaran: conservan su color. La tinta del día se vuelve a poner encima.
 * - El paño tiene borde neto y se corre hacia la ventana tanto como la superficie sobresale de la pared ([PARED], [VOLUMEN],
 *   [LADO], [MUEBLE]): se quiebra al pasar de la pared al frente del mueble, a la cara de dentro del montante y a la tabla.
 * - No entra donde la tabla de arriba y el montante echan su sombra franca en la pared de cada hueco.
 * - En el aire queda apenas un rastro, muy tenue y de borde blando ([rastro]).
 * El tono se pone antes de lo que va delante de la pared ([tonoEnCorona], [tonoEnHuecos]); la luz que aclara, después
 * ([luzEnCorona], [luzEnCuerpo], [luzEnVolumen]).
 */
internal object LuzSuenosVapor {
    // Lo que sobresale de la pared cada superficie, en anchos del mueble, medido a lo largo del rayo.
    const val PARED = 0f
    const val VOLUMEN = 0.03f
    const val LADO = 0.034f
    const val MUEBLE = 0.06f
    /** El suelo, delante del zócalo. */
    private const val PISO = 0.1f

    /** El rayo del ocaso: de la ventana a su paño en la pared (ver [panoVapor]). */
    private val RAYO_OCASO = Offset(0.21f, 0.978f)
    /** Dónde empieza en la pared el plano de la luna, a lo largo de su rayo: bajo el alféizar. */
    private const val T0 = 0.17f
    /** Los tres haces de la luna, a lo ancho del plano (de −1 a 1): entre ellos, la sombra de los montantes de la ventana. */
    private val HACES = listOf(-1f to -0.4f, -0.26f to 0.26f, 0.4f to 1f)
    /** El ancho de los montantes y peinazos de la ventana, en anchos, para los cuarterones del ocaso. */
    private const val BARRA = 0.006f

    /** El tono de cada luz y lo que aclara (el color que divide en el `ColorDodge`: cuanto más claro, más aclara). */
    private val TONO_LUNA = Color(0xFFCFD6E2)
    private val ACLARA_LUNA = Color(0xFF9A9EA8)
    private val TONO_OCASO = Color(0xFFF0883E)
    private val ACLARA_OCASO = Color(0xFF80521A)

    private fun rayo(a: AireVapor): Offset = if (a.lunar) PlanoLunaVapor.D else RAYO_OCASO

    /** Si le llega luz a algún punto del rectángulo [o] + [ancho] × [alto] (en anchos) de una superficie que sobresale [hondo]. */
    fun toca(a: AireVapor, o: Offset, ancho: Float, alto: Float, hondo: Float): Boolean {
        val d = rayo(a)
        val m = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
        return m.any { fx -> m.any { fy ->
            val x = o.x + ancho * fx + d.x * hondo; val y = o.y + alto * fy + d.y * hondo
            (if (a.lunar) lunaVapor(x, y) else enPanoVapor(x, y)) > 0f
        } }
    }

    /** El punto [x], [y] (en anchos) de una superficie que sobresale [hondo], en píxeles de un lienzo de ancho [w] que empieza en [o]. */
    private fun en(a: AireVapor, x: Float, y: Float, w: Float, o: Offset, hondo: Float): Offset {
        val d = rayo(a)
        return Offset((x - d.x * hondo - o.x) * w, (y - d.y * hondo - o.y) * w)
    }

    /** Dónde cae la luz: los tres haces de la luna o los quince cuarterones del ocaso, con borde neto. */
    private fun pano(a: AireVapor, w: Float, o: Offset, hondo: Float): Path {
        val p = Path()
        fun cuadro(q: List<Offset>) { p.moveTo(q[0].x, q[0].y); for (i in 1 until q.size) p.lineTo(q[i].x, q[i].y); p.close() }
        if (a.lunar) {
            val l = PlanoLunaVapor
            fun eje(t: Float, s: Float): Offset {
                val h = (l.ANCHO0 + l.ABRE * t) * s
                return en(a, l.C0.x + l.D.x * t + l.N.x * h, l.C0.y + l.D.y * t + l.N.y * h, w, o, hondo)
            }
            HACES.forEach { (s0, s1) -> cuadro(listOf(eje(T0, s0), eje(l.LARGO, s0), eje(l.LARGO, s1), eje(T0, s1))) }
        } else {
            fun q(x: Float, y: Float): Offset { val v = panoVapor(x, y); return en(a, v.x, v.y, w, o, hondo) }
            for (i in 0 until 3) for (j in 0 until 5) {
                val x0 = 0.03f + 0.2f * i / 3f + (if (i > 0) BARRA else 0f); val x1 = 0.03f + 0.2f * (i + 1) / 3f - (if (i < 2) BARRA else 0f)
                val y0 = 0.08f + 0.28f * j / 5f + (if (j > 0) BARRA else 0f); val y1 = 0.08f + 0.28f * (j + 1) / 5f - (if (j < 4) BARRA else 0f)
                cuadro(listOf(q(x0, y0), q(x1, y0), q(x1, y1), q(x0, y1)))
            }
        }
        return p
    }

    /** El color [c] con la fuerza [al], que cae a lo largo del rayo: la luna se apaga al bajar; el ocaso, apenas. */
    private fun pincel(a: AireVapor, c: Color, al: Float, w: Float, o: Offset, hondo: Float): Brush {
        val (i0, i1) = if (a.lunar) {
            val l = PlanoLunaVapor
            Offset(l.C0.x + l.D.x * T0, l.C0.y + l.D.y * T0) to Offset(l.C0.x + l.D.x * l.LARGO, l.C0.y + l.D.y * l.LARGO)
        } else panoVapor(0.13f, 0.08f) to panoVapor(0.13f, 0.36f)
        val (m, f) = if (a.lunar) 0.74f to 0.46f else 0.92f to 0.82f
        val al0 = al.coerceIn(0f, 1f)
        return Brush.linearGradient(listOf(c.copy(alpha = al0), c.copy(alpha = al0 * m), c.copy(alpha = al0 * f)),
            en(a, i0.x, i0.y, w, o, hondo), en(a, i1.x, i1.y, w, o, hondo))
    }

    /** El tono de la luz en lo que toca, con su misma luminosidad ([tono]), y un poco de luz que levanta lo más oscuro ([alza]). */
    private fun DrawScope.tenir(a: AireVapor, w: Float, o: Offset, hondo: Float, tono: Float, alza: Float) {
        val p = pano(a, w, o, hondo)
        val c = if (a.lunar) TONO_LUNA else TONO_OCASO
        if (tono > 0f) drawPath(p, pincel(a, c, tono, w, o, hondo), blendMode = BlendMode.Color)
        if (alza > 0f) drawPath(p, pincel(a, c, alza, w, o, hondo), blendMode = BlendMode.Screen)
    }

    /** La luz que aclara lo que toca, en proporción a lo que ya tiene. */
    private fun DrawScope.aclarar(a: AireVapor, w: Float, o: Offset, hondo: Float, fuerza: Float) {
        drawPath(pano(a, w, o, hondo), pincel(a, if (a.lunar) ACLARA_LUNA else ACLARA_OCASO, fuerza, w, o, hondo), blendMode = BlendMode.ColorDodge)
    }

    // Cuánto tono toma y cuánto se aclara cada superficie, con la luna o con el ocaso: el pino y las entradas, ya claros y de
    // color, se aclaran menos con el ocaso para no quemarse.
    private fun tonoPared(a: AireVapor) = if (a.lunar) 0.62f else 0.74f
    private fun alzaPared(a: AireVapor) = if (a.lunar) 0.07f else 0.05f
    private fun tonoMueble(a: AireVapor) = if (a.lunar) 0.32f else 0.2f
    private fun aclaraMueble(a: AireVapor) = if (a.lunar) 0.85f else 0.55f
    private fun aclaraCara(a: AireVapor) = if (a.lunar) 1f else 0.72f

    // ---------------------------------------------------------------- la corona

    /** Antes de lo de encima del mueble: el tono de la luz en la pared, lo colgado y la cornisa. */
    fun DrawScope.tonoEnCorona(a: AireVapor, g: MarcoVapor) {
        val w = g.w
        clipRect(0f, 0f, w, g.apoyo) { tenir(a, w, Offset.Zero, PARED, tonoPared(a), alzaPared(a)) }
        clipRect(0f, g.apoyo, w, g.ch) { tenir(a, w, Offset.Zero, MUEBLE, tonoMueble(a), 0f) }
    }

    /** Después: la luz que aclara la pared y lo que está delante de ella, el frente de la cornisa (corrido) y el rastro en el aire. */
    fun DrawScope.luzEnCorona(a: AireVapor, g: MarcoVapor) {
        val w = g.w
        clipRect(0f, 0f, w, g.apoyo) { aclarar(a, w, Offset.Zero, PARED, 0.9f) }
        clipRect(0f, g.apoyo, w, g.ch) { aclarar(a, w, Offset.Zero, MUEBLE, aclaraMueble(a)) }
        clipRect(0f, 0f, w, g.ch) { rastro(a, w) }
    }

    // ---------------------------------------------------------------- el cuerpo

    /** Lo de un hueco adonde puede llegar la luz: todo menos la sombra franca de la tabla de arriba y del montante. */
    private fun alcance(r: Rect, g: MarcoVapor): Path = Path().apply {
        op(Path().apply { addRect(Rect(g.x0, r.top, g.x1, r.bottom)) }, MuebleCuerpoVapor.sombraFranca(r, g, true), PathOperation.Difference)
    }

    private fun paredDe(r: Rect, g: MarcoVapor): Rect {
        val w = g.w
        return Rect(g.x0 + w * MuebleCuerpoVapor.HONDO_LADO, r.top + w * MuebleCuerpoVapor.HONDO_ARRIBA, g.x1 - w * MuebleCuerpoVapor.HONDO_LADO, r.bottom - w * MuebleCuerpoVapor.HONDO_ABAJO)
    }

    /** Antes de las tablas, los montantes y lo de los huecos: el tono de la luz en la pared de cada hueco. */
    fun DrawScope.tonoEnHuecos(a: AireVapor, g: MarcoVapor, repisas: List<Rect>) {
        repisas.forEach { r ->
            val p = paredDe(r, g)
            clipPath(alcance(r, g)) { clipRect(p.left, p.top, p.right, p.bottom) { tenir(a, g.w, Offset.Zero, PARED, tonoPared(a), alzaPared(a)) } }
        }
    }

    /**
     * Después del cuerpo: la luz en la pared de cada hueco (y en lo que hay en los huecos libres), en la cara de dentro del
     * montante derecho (la que mira a la ventana) y en la tabla de abajo vista desde arriba, más fuerte porque miran a la luz; en el
     * frente de los montantes y las tablas, corrida; en la pared que asoma a los lados; y el rastro en el aire.
     */
    fun DrawScope.luzEnCuerpo(a: AireVapor, g: MarcoVapor, repisas: List<Rect>, tablas: List<Rect>) {
        val w = g.w; val o = Offset.Zero
        val dL = w * MuebleCuerpoVapor.HONDO_LADO; val dB = w * MuebleCuerpoVapor.HONDO_ABAJO
        repisas.forEach { r ->
            val p = paredDe(r, g)
            clipPath(alcance(r, g)) {
                clipRect(p.left, p.top, p.right, p.bottom) { aclarar(a, w, o, PARED, 0.9f) }
                clipPath(poligonoVapor(g.x1, r.top, g.x1 - dL, p.top, g.x1 - dL, p.bottom, g.x1, r.bottom)) {
                    tenir(a, w, o, LADO, tonoMueble(a) + 0.06f, 0f); aclarar(a, w, o, LADO, aclaraCara(a))
                }
                clipPath(poligonoVapor(g.x0, r.bottom - dB, g.x1 - dL, r.bottom - dB, g.x1, r.bottom, g.x0, r.bottom)) {
                    tenir(a, w, o, VOLUMEN, tonoMueble(a), 0f); aclarar(a, w, o, VOLUMEN, aclaraCara(a))
                }
            }
        }
        val frente = Path().apply {
            addRect(Rect(g.xa, g.ch, g.x0, g.fondo)); addRect(Rect(g.x1, g.ch, g.xb, g.fondo))
            tablas.forEach { t -> addRect(Rect(g.x0, t.top, g.x1, t.bottom)) }
        }
        clipPath(frente) { tenir(a, w, o, MUEBLE, tonoMueble(a), 0f); aclarar(a, w, o, MUEBLE, aclaraMueble(a)) }
        val lados = Path().apply { addRect(Rect(0f, g.ch, g.xa, g.fondo)); addRect(Rect(g.xb, g.ch, w, g.fondo)) }
        clipPath(lados) { tenir(a, w, o, PARED, tonoPared(a), alzaPared(a)); aclarar(a, w, o, PARED, 0.9f) }
        rastro(a, w)
    }

    /**
     * La luna que sigue bajando (los Sueños de noche): el zócalo, corrido como el frente del mueble, y el suelo, más corrido todavía,
     * más tenue. Se llama después del suelo y la estufa y antes del paragüero (que la luna no alcanza).
     */
    fun DrawScope.luzEnPie(a: AireVapor, g: MarcoVapor) {
        if (!a.lunar) return
        val w = g.w; val o = Offset.Zero
        clipRect(g.xa, g.fondo, g.xb, g.zocalo) { tenir(a, w, o, MUEBLE, tonoMueble(a), 0f); aclarar(a, w, o, MUEBLE, aclaraMueble(a)) }
        clipRect(0f, g.zocalo, w, g.h) { tenir(a, w, o, PISO, 0.4f, 0.04f); aclarar(a, w, o, PISO, 0.75f) }
    }

    // ---------------------------------------------------------------- las entradas

    /**
     * La luz en una entrada (que se pinta en su propio lienzo, de ancho [w] por ancho del mueble, que empieza en [o]): solo dentro
     * de su [silueta], sin cambiarle el color, más clara y entibiada o enfriada; un poco más en lo de arriba ([cimas]), que mira a la
     * luz. Con la luna, menos que la pared: la tela y la laca claras se quemaban hacia el pastel.
     */
    fun DrawScope.luzEnVolumen(a: AireVapor, w: Float, o: Offset, silueta: Path, cimas: Path) {
        clipPath(silueta) {
            aclarar(a, w, o, VOLUMEN, if (a.lunar) 0.5f else 0.6f)
            clipPath(cimas) { aclarar(a, w, o, VOLUMEN, if (a.lunar) 0.45f else 0.4f) }
        }
    }

    // ---------------------------------------------------------------- el aire

    /** El rastro de la luz en el aire: muy tenue y de borde blando (tres capas que se ensanchan). */
    private fun DrawScope.rastro(a: AireVapor, w: Float) {
        fun px(x: Float, y: Float) = Offset(x * w, y * w)
        if (a.lunar) {
            val l = PlanoLunaVapor
            val c1 = Offset(l.C0.x + l.D.x * l.LARGO, l.C0.y + l.D.y * l.LARGO)
            val pincel = Brush.linearGradient(listOf(TONO_LUNA.copy(alpha = 0.02f), TONO_LUNA.copy(alpha = 0.012f), Color.Transparent), px(l.C0.x, l.C0.y), px(c1.x, c1.y))
            listOf(1.5f, 1.25f, 1.02f).forEach { fr ->
                val h0 = l.ANCHO0 * fr; val h1 = (l.ANCHO0 + l.ABRE * l.LARGO) * fr
                drawPath(poligonoVapor(
                    (l.C0.x + l.N.x * h0) * w, (l.C0.y + l.N.y * h0) * w, (c1.x + l.N.x * h1) * w, (c1.y + l.N.y * h1) * w,
                    (c1.x - l.N.x * h1) * w, (c1.y - l.N.y * h1) * w, (l.C0.x - l.N.x * h0) * w, (l.C0.y - l.N.y * h0) * w), pincel)
            }
        } else {
            // Del vidrio al paño: el aire que atraviesa el ocaso.
            val pts = listOf(Offset(0.03f, 0.08f), Offset(0.23f, 0.08f), panoVapor(0.23f, 0.08f) + Offset(0.004f, 0f), panoVapor(0.23f, 0.36f), panoVapor(0.03f, 0.36f), Offset(0.03f, 0.36f))
            val pincel = Brush.linearGradient(listOf(TONO_OCASO.copy(alpha = 0.035f), TONO_OCASO.copy(alpha = 0.015f), Color.Transparent), px(0.13f, 0.2f), px(0.25f, 0.86f))
            drawPath(poligonoVapor(*pts.flatMap { listOf(it.x * w, it.y * w) }.toFloatArray()), pincel)
        }
    }
}
