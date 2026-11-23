package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que hay en los huecos de las repisas del estante de Art nouveau ([MuebleEstanteNouveau]): grupos de dos o tres cosas del
 * despacho, grandes (de siete a nueve décimos del hueco) y distintos de un hueco a otro: el tarro de grès flameado con rollos de
 * pruebas y pinceles; el plato de loza apoyado de canto con la tetera y su taza delante; la maceta de helecho con la pila de
 * almanaques; la carpeta apoyada en el fondo con los almanaques acostados y el cuentahílos de latón. En uno o dos huecos, el
 * quinqué de petróleo con su tulipa de vidrio grabado (de día, apagado; de noche, encendido: ver [MuebleEstanteNouveau.quinques]).
 */
internal object HuecosEstanteNouveau {
    private val P = PaletaNouveau

    /** Lo que se deja en el hueco libre de la repisa [k], de [oc] (hasta donde llegan los volúmenes) a [xDer] (el montante). */
    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, xDer: Float, xq: Float, a: AireNouveau, cerca: (Offset) -> Float) {
        val w = size.width
        val x0 = oc + w * 0.012f
        val x1 = xDer - w * 0.03f
        if (x1 - x0 < w * 0.07f) return
        val h = r.height; val piso = r.bottom
        val ha = if (a.oscuro) 1f else -1f
        clipRect(oc, r.top, xDer, r.bottom + 2f) {
            // Lo que acompaña: a la derecha del quinqué, si lo hay; si no, en todo el hueco (uno o dos grupos).
            val desde = if (xq >= 0f) xq + w * 0.07f else x0
            val resto = x1 - desde
            val cuantos = if (resto >= w * 0.42f) 2 else if (resto >= w * 0.1f) 1 else 0
            for (j in 0 until cuantos) {
                val ancho = resto / cuantos
                val c = desde + ancho * (j + 0.5f)
                val kc = cerca(Offset(c, r.center.y))
                val tipo = Math.floorMod(k * 3 + 1 + j * 2, 4)
                when (tipo) {
                    0 -> gres(Offset(c, piso), h, ancho, a, kc, ha)
                    1 -> loza(Offset(c, piso), h, ancho, a, kc, ha)
                    2 -> helecho(Offset(c, piso), h, ancho, a, kc, ha)
                    else -> almanaques(Offset(c, piso), h, ancho, a, kc, ha)
                }
            }
            if (xq >= 0f) quinque(Offset(xq, piso), h, a, if (a.lumbre) 1f else cerca(Offset(xq, r.center.y)))
        }
    }

    /** La sombra de algo en el fondo del casillero, corrida lejos de la luz: un óvalo alto y blando. */
    private fun DrawScope.sombraFondo(c: Offset, an: Float, al: Float, a: AireNouveau, ha: Float) =
        drawOval(a.sombra(0.22f), Offset(c.x - an / 2 - ha * an * 0.25f, c.y - al), Size(an, al))

    /** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
    private fun DrawScope.redondo(r: Rect, a: AireNouveau, k: Float, ha: Float, fuerza: Float = 1f) {
        val der = ha > 0f
        fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
        drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0.06f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(a.brillo(0.32f * fuerza, k), Offset(x(0.16f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(a.sombra(0.18f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
        drawRect(a.sombra(0.28f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    // ---------------------------------------------------------------- los grupos

    /** El tarro de grès flameado (esmalte verde azul chorreado sobre el barro), con rollos de pruebas y pinceles. */
    private fun DrawScope.gres(pie: Offset, h: Float, ancho: Float, a: AireNouveau, k: Float, ha: Float) {
        val u = density
        val rr = minOf(ancho * 0.3f, h * 0.2f)
        val alto = h * 0.46f
        sombraFondo(Offset(pie.x, pie.y), rr * 3.2f, h * 0.86f, a, ha)
        // Los rollos de pruebas y los pinceles, detrás.
        val rollos = listOf(-0.5f to 0.88f, 0.1f to 0.8f, 0.55f to 0.72f)
        rollos.forEachIndexed { i, (dx, al) ->
            val x = pie.x + dx * rr
            val top = pie.y - h * al
            rotate(dx * 9f, Offset(x, pie.y - alto)) {
                val g = rr * 0.42f
                drawRect(a.luz(if (i == 1) Color(0xFFE6D8B6) else P.PAPEL, k), Offset(x - g / 2, top), Size(g, pie.y - alto - top + 2f * u))
                drawRect(a.sombra(0.25f), Offset(x + g * 0.12f, top), Size(g * 0.38f, pie.y - alto - top))
                drawOval(a.luz(P.PAPEL_OSC, k), Offset(x - g / 2, top - g * 0.2f), Size(g, g * 0.4f))
                drawArc(a.luz(P.TINTA, k).copy(alpha = 0.4f), 0f, 300f, false, Offset(x - g * 0.3f, top - g * 0.12f), Size(g * 0.6f, g * 0.24f), style = Stroke(0.5f * u))
                if (i == 0) drawRect(a.luz(P.IRIS, k).copy(alpha = 0.7f), Offset(x - g / 2, top + g * 0.6f), Size(g * 0.25f, h * 0.14f))
            }
        }
        listOf(-0.15f to 0.95f, 0.32f to 0.9f).forEach { (dx, al) ->
            val x = pie.x + dx * rr
            drawLine(a.luz(Color(0xFF8A5A30), k), Offset(x, pie.y - alto), Offset(x + dx * rr * 0.4f, pie.y - h * al + h * 0.08f), 1.6f * u, StrokeCap.Round)
            drawLine(a.luz(P.LATON, k), Offset(x + dx * rr * 0.3f, pie.y - h * al + h * 0.12f), Offset(x + dx * rr * 0.36f, pie.y - h * al + h * 0.08f), 2f * u)
            drawLine(a.luz(P.TINTA, k), Offset(x + dx * rr * 0.36f, pie.y - h * al + h * 0.08f), Offset(x + dx * rr * 0.4f, pie.y - h * al + h * 0.02f), 2.4f * u, StrokeCap.Round)
        }
        // El tarro: panza, cuello corto y labio, con el esmalte que chorrea sobre el barro.
        val t = Path().apply {
            moveTo(pie.x - rr * 0.8f, pie.y)
            cubicTo(pie.x - rr * 1.1f, pie.y - alto * 0.4f, pie.x - rr * 1.05f, pie.y - alto * 0.8f, pie.x - rr * 0.72f, pie.y - alto * 0.92f)
            lineTo(pie.x - rr * 0.78f, pie.y - alto); lineTo(pie.x + rr * 0.78f, pie.y - alto); lineTo(pie.x + rr * 0.72f, pie.y - alto * 0.92f)
            cubicTo(pie.x + rr * 1.05f, pie.y - alto * 0.8f, pie.x + rr * 1.1f, pie.y - alto * 0.4f, pie.x + rr * 0.8f, pie.y)
            close()
        }
        contactoNouveau(Offset(pie.x, pie.y - 1f * u), rr * 2.4f, 5f * u, a, hacia = -ha)
        drawPath(t, a.luz(P.GRES_BARRO, k))
        clipPath(t) {
            val esmalte = Path().apply {
                moveTo(pie.x - rr * 1.2f, pie.y - alto * 1.1f); lineTo(pie.x + rr * 1.2f, pie.y - alto * 1.1f)
                lineTo(pie.x + rr * 1.2f, pie.y - alto * 0.3f)
                var x = pie.x + rr * 1.2f; var i = 0
                while (x > pie.x - rr * 1.2f) {
                    val baja = alto * (0.12f + 0.2f * azar(971, i))
                    quadraticTo(x - rr * 0.12f, pie.y - alto * 0.3f + baja, x - rr * 0.24f, pie.y - alto * 0.32f)
                    x -= rr * 0.24f; i++
                }
                close()
            }
            drawPath(esmalte, a.luz(P.GRES, k))
            for (i in 0 until 5) {
                val x = pie.x - rr * 0.9f + rr * 0.42f * i
                drawLine(a.luz(P.GRES_CHORRO, k).copy(alpha = 0.6f), Offset(x, pie.y - alto), Offset(x + rr * 0.05f, pie.y - alto * (0.4f + 0.2f * azar(972, i))), rr * 0.12f, StrokeCap.Round)
            }
            redondo(Rect(pie.x - rr * 1.1f, pie.y - alto, pie.x + rr * 1.1f, pie.y), a, k, ha)
        }
        drawOval(a.luz(Color(0xFF1E3A40), k), Offset(pie.x - rr * 0.74f, pie.y - alto - rr * 0.14f), Size(rr * 1.48f, rr * 0.3f))
        destelloNouveau(Offset(pie.x - ha * rr * 0.5f, pie.y - alto * 0.62f), 1.3f * u, a, k)
    }

    /** El plato de loza apoyado de canto en el fondo, pintado con una flor, y delante la tetera y su taza. */
    private fun DrawScope.loza(pie: Offset, h: Float, ancho: Float, a: AireNouveau, k: Float, ha: Float) {
        val u = density
        val rp = minOf(ancho * 0.42f, h * 0.4f)
        val cp = Offset(pie.x + rp * 0.15f, pie.y - rp - h * 0.04f)
        // El plato, de canto contra el fondo, con su sombra.
        drawCircle(a.sombra(0.3f), rp, cp + Offset(-ha * rp * 0.12f, rp * 0.06f))
        drawCircle(a.luz(Color(0xFFF0E8D4), k), rp, cp)
        drawCircle(a.luz(Color(0xFF3E6A6A), k), rp * 0.94f, cp, style = Stroke(rp * 0.06f))
        drawCircle(a.luz(Color(0xFFE6DCC2), k), rp * 0.62f, cp)
        with(FloresNouveau) {
            deColor(0, cp + Offset(-rp * 0.08f, rp * 0.08f), rp * 0.52f, a.luz(Color(0xFF4E6E8E), k), a.luz(Color(0xFF5E7A55), k), a.luz(Color(0xFF2E3E4E), k), a.luz(Color(0xFF2E3E4E), k), -24f)
        }
        drawArc(a.sombra(0.25f), if (ha < 0) -60f else 120f, 180f, false, Offset(cp.x - rp, cp.y - rp), Size(rp * 2, rp * 2), style = Stroke(rp * 0.12f))
        drawArc(a.brillo(0.45f, k), if (ha < 0) 150f else -30f, 60f, false, Offset(cp.x - rp * 0.9f, cp.y - rp * 0.9f), Size(rp * 1.8f, rp * 1.8f), style = Stroke(1f * u))
        // La tetera de loza crema con su asa, su pico y su tapa.
        val tc = Offset(pie.x - rp * 0.45f, pie.y)
        val tr = minOf(rp * 0.55f, h * 0.2f)
        contactoNouveau(Offset(tc.x, tc.y - 1f * u), tr * 2.8f, 5f * u, a, hacia = -ha)
        val asa = Path().apply { moveTo(tc.x + tr * 0.8f, tc.y - tr * 1.2f); cubicTo(tc.x + tr * 1.6f, tc.y - tr * 1.3f, tc.x + tr * 1.5f, tc.y - tr * 0.4f, tc.x + tr * 0.85f, tc.y - tr * 0.45f) }
        drawPath(asa, a.luz(Color(0xFFE8DCC0), k), style = Stroke(tr * 0.2f, cap = StrokeCap.Round))
        val pico = Path().apply { moveTo(tc.x - tr * 0.8f, tc.y - tr * 0.7f); quadraticTo(tc.x - tr * 1.3f, tc.y - tr * 0.9f, tc.x - tr * 1.6f, tc.y - tr * 1.5f); lineTo(tc.x - tr * 1.45f, tc.y - tr * 1.55f); quadraticTo(tc.x - tr * 1.2f, tc.y - tr * 1.1f, tc.x - tr * 0.75f, tc.y - tr * 1.05f); close() }
        drawPath(pico, a.luz(Color(0xFFE8DCC0), k))
        val cuerpo = Rect(tc.x - tr, tc.y - tr * 1.7f, tc.x + tr, tc.y)
        drawOval(a.luz(Color(0xFFEDE3CB), k), cuerpo.topLeft, cuerpo.size)
        clipPath(Path().apply { addOval(cuerpo) }) {
            drawRect(a.luz(Color(0xFF3E6A6A), k), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.42f), Size(cuerpo.width, cuerpo.height * 0.1f))
            redondo(cuerpo, a, k, ha)
        }
        drawOval(a.luz(Color(0xFFE2D6BA), k), Offset(tc.x - tr * 0.55f, cuerpo.top - tr * 0.18f), Size(tr * 1.1f, tr * 0.36f))
        drawCircle(a.luz(Color(0xFF3E6A6A), k), tr * 0.14f, Offset(tc.x, cuerpo.top - tr * 0.2f))
        destelloNouveau(Offset(tc.x - ha * tr * 0.45f, tc.y - tr * 1.15f), 1.2f * u, a, k)
        // La taza en su platillo, delante a la derecha.
        val cc = Offset(pie.x + rp * 0.7f, pie.y)
        val cr = tr * 0.55f
        contactoNouveau(Offset(cc.x, cc.y - 0.5f * u), cr * 3f, 3f * u, a, hacia = -ha)
        drawOval(a.luz(Color(0xFFE6DCC2), k), Offset(cc.x - cr * 1.4f, cc.y - cr * 0.3f), Size(cr * 2.8f, cr * 0.5f))
        val taza = Path().apply { moveTo(cc.x - cr, cc.y - cr * 1.4f); lineTo(cc.x + cr, cc.y - cr * 1.4f); quadraticTo(cc.x + cr * 0.9f, cc.y - cr * 0.1f, cc.x, cc.y - cr * 0.1f); quadraticTo(cc.x - cr * 0.9f, cc.y - cr * 0.1f, cc.x - cr, cc.y - cr * 1.4f); close() }
        drawPath(taza, a.luz(Color(0xFFEDE3CB), k))
        drawRect(a.luz(Color(0xFF3E6A6A), k), Offset(cc.x - cr * 0.98f, cc.y - cr * 1.3f), Size(cr * 1.96f, cr * 0.14f))
        drawArc(a.luz(Color(0xFFE2D6BA), k), -80f, 160f, false, Offset(cc.x + cr * 0.7f, cc.y - cr * 1.25f), Size(cr * 0.7f, cr * 0.7f), style = Stroke(cr * 0.16f))
    }

    /** La maceta de barro vidriado con un helecho de hojas que caen, y al lado una pila de almanaques acostados. */
    private fun DrawScope.helecho(pie: Offset, h: Float, ancho: Float, a: AireNouveau, k: Float, ha: Float) {
        val u = density
        val mx = pie.x - ancho * 0.12f
        val mr = minOf(ancho * 0.26f, h * 0.2f)
        val alto = h * 0.34f
        sombraFondo(Offset(mx, pie.y), mr * 4f, h * 0.9f, a, ha)
        // Las frondas: arcos de hojuelas, que suben y caen.
        val frondas = listOf(-150f to 0.82f, -122f to 0.92f, -95f to 0.96f, -68f to 0.9f, -40f to 0.8f, -165f to 0.55f, -18f to 0.5f)
        frondas.forEachIndexed { i, (ang, l) ->
            val largo = h * l * 0.62f
            val base = Offset(mx + (i - 3) * mr * 0.12f, pie.y - alto)
            val ra = Math.toRadians(ang.toDouble())
            val fin = Offset(base.x + largo * cos(ra).toFloat() * 1.1f, base.y + largo * sin(ra).toFloat())
            val caida = Offset(fin.x + (fin.x - base.x) * 0.25f, fin.y + largo * 0.35f)
            val c = if (i % 2 == 0) P.HOJA else mezcla(P.HOJA, P.TALLO, 0.35f)
            val raquis = Path().apply { moveTo(base.x, base.y); quadraticTo(fin.x, fin.y, caida.x, caida.y) }
            drawPath(raquis, a.luz(P.HOJA_OSC, k), style = Stroke(0.9f * u))
            // Las hojuelas, a los dos lados del raquis.
            for (j in 1 until 9) {
                val t = j / 9f
                val m = 1f - t
                val px = m * m * base.x + 2 * m * t * fin.x + t * t * caida.x
                val py = m * m * base.y + 2 * m * t * fin.y + t * t * caida.y
                val tam = mr * 0.5f * (1f - t * 0.6f)
                drawOval(a.luz(c, k), Offset(px - tam, py - tam * 0.5f), Size(tam * 1.1f, tam * 0.6f))
                drawOval(a.luz(mezcla(c, P.HOJA_OSC, 0.35f), k), Offset(px - tam * 0.1f, py - tam * 0.1f), Size(tam * 1.1f, tam * 0.6f))
            }
        }
        // La maceta, vidriada de ocre con un filete violeta.
        val m = Path().apply {
            moveTo(mx - mr, pie.y - alto); lineTo(mx + mr, pie.y - alto); lineTo(mx + mr * 0.78f, pie.y); lineTo(mx - mr * 0.78f, pie.y); close()
        }
        contactoNouveau(Offset(mx, pie.y - 1f * u), mr * 2.3f, 4.5f * u, a, hacia = -ha)
        drawPath(m, a.luz(Color(0xFFB8863A), k))
        clipPath(m) {
            drawRect(a.luz(P.IRIS_OSC, k), Offset(mx - mr, pie.y - alto * 0.72f), Size(mr * 2, alto * 0.08f))
            redondo(Rect(mx - mr, pie.y - alto, mx + mr, pie.y), a, k, ha)
        }
        drawRect(a.luz(Color(0xFFC89A4A), k), Offset(mx - mr * 1.06f, pie.y - alto - mr * 0.12f), Size(mr * 2.12f, mr * 0.2f))
        drawRect(a.brillo(0.3f, k), Offset(mx - mr * 1.06f, pie.y - alto - mr * 0.12f), Size(mr * 2.12f, mr * 0.05f))
        // La pila de almanaques a la derecha.
        pila(Offset(pie.x + ancho * 0.28f, pie.y), minOf(ancho * 0.36f, h * 0.5f), h * 0.36f, a, k, ha, 3)
    }

    /** Una pila de almanaques acostados de [ancho], hasta [alto]: cubiertas de colores, algo desalineadas, con el canto de papel. */
    private fun DrawScope.pila(pie: Offset, ancho: Float, alto: Float, a: AireNouveau, k: Float, ha: Float, s: Int) {
        val u = density
        val cols = listOf(Color(0xFFE6D8B6), Color(0xFF7A9670), Color(0xFFC9A0B8), Color(0xFF6A5282), Color(0xFFD9B35C), Color(0xFF8E5A3E))
        val n = 5
        val g = alto / n
        contactoNouveau(Offset(pie.x, pie.y - 1f * u), ancho * 1.2f, 5f * u, a, hacia = -ha)
        for (i in 0 until n) {
            val y = pie.y - g * (i + 1)
            val dx = (azar(s * 13 + 981, i) - 0.5f) * ancho * 0.16f
            val an = ancho * (0.86f + 0.14f * azar(s * 13 + 982, i))
            val c = cols[Math.floorMod(s + i * 2, cols.size)]
            val x = pie.x - an / 2 + dx
            drawRect(a.sombra(0.3f), Offset(x + 1f * u, y + 1f * u), Size(an, g))
            drawRect(a.luz(c, k), Offset(x, y), Size(an, g))
            drawRect(a.luz(P.PAPEL, k), Offset(x + an * 0.04f, y + g * 0.28f), Size(an * 0.94f, g * 0.44f))
            drawRect(a.dia(c, 0.3f, k), Offset(x, y), Size(an, g * 0.14f))
            drawRect(a.sombra(0.22f), Offset(x, y + g * 0.86f), Size(an, g * 0.14f))
        }
    }

    /** La carpeta apoyada en el fondo, en diagonal, con los almanaques acostados delante y el cuentahílos de latón encima. */
    private fun DrawScope.almanaques(pie: Offset, h: Float, ancho: Float, a: AireNouveau, k: Float, ha: Float) {
        val u = density
        // La carpeta apoyada: una tabla de tela verde con sus cantoneras y su cinta.
        val alto = h * 0.88f
        val an = minOf(ancho * 0.7f, h * 0.62f)
        val base = Offset(pie.x - an * 0.4f, pie.y)
        val p = poligonoNouveau(base.x, base.y, base.x + an, base.y, base.x + an + alto * 0.18f, base.y - alto, base.x + alto * 0.18f, base.y - alto)
        translate(-ha * an * 0.08f, 0f) { drawPath(p, a.sombra(0.22f)) }
        drawPath(p, a.luz(Color(0xFF34503F), k))
        clipPath(p) {
            // El papel jaspeado de la tapa.
            for (i in 0 until 9) {
                val c = Offset(base.x + an * azar(991, i) + alto * 0.18f * azar(992, i), base.y - alto * azar(993, i))
                drawOval(a.luz(if (i % 2 == 0) Color(0xFF5E7E6A) else Color(0xFF8A6E9A), k).copy(alpha = 0.55f), Offset(c.x - an * 0.18f, c.y - alto * 0.05f), Size(an * 0.36f, alto * 0.1f))
            }
            drawRect(a.luz(P.TAFILETE, k), Offset(base.x - an, base.y - alto), Size(an * 0.2f + alto * 0.2f + an, alto * 0.07f))
        }
        drawPath(p, a.sombra(0.35f), style = Stroke(0.8f * u))
        drawLine(a.luz(P.SEDA, k), Offset(base.x + an + alto * 0.09f, base.y - alto * 0.5f), Offset(base.x + an + alto * 0.09f + 4f * u, base.y - alto * 0.32f), 2f * u, StrokeCap.Round)
        // Los almanaques acostados, delante.
        pila(Offset(pie.x + ancho * 0.08f, pie.y), minOf(ancho * 0.55f, h * 0.62f), h * 0.3f, a, k, ha, 7)
        // El cuentahílos de latón, plegado encima: la base cuadrada con su lente y la tapa abierta.
        val cx = pie.x + ancho * 0.08f; val cy = pie.y - h * 0.3f
        val l = h * 0.09f
        drawRect(a.sombra(0.3f), Offset(cx - l * 0.4f, cy - 1f * u), Size(l * 1.2f, 1.6f * u))
        drawRect(a.luz(P.LATON_OSC, k), Offset(cx - l * 0.5f, cy - l * 0.3f), Size(l, l * 0.3f))
        drawPath(poligonoNouveau(cx - l * 0.45f, cy - l * 0.3f, cx + l * 0.45f, cy - l * 0.3f, cx + l * 0.3f, cy - l * 1.15f, cx - l * 0.6f, cy - l * 1.15f), a.luz(P.LATON, k))
        drawOval(a.luz(Color(0xFFB8D0CC), k), Offset(cx - l * 0.32f, cy - l * 0.95f), Size(l * 0.56f, l * 0.42f))
        drawOval(a.brillo(0.6f, k), Offset(cx - l * 0.25f, cy - l * 0.9f), Size(l * 0.16f, l * 0.1f))
    }

    // ---------------------------------------------------------------- el quinqué

    /**
     * El quinqué de petróleo: el pie redondo de latón con su fuste, el depósito de vidrio violeta con el petróleo dentro, el
     * collar del mechero, el globo de vidrio esmerilado con una guirnalda grabada y el tubo de vidrio que sale por arriba. El
     * centro del globo queda a la altura [MuebleEstanteNouveau.QUINQUE_Y] del hueco (de noche arde: [llamaHueco]).
     */
    private fun DrawScope.quinque(pie: Offset, h: Float, a: AireNouveau, k: Float) {
        val u = density
        val rg = h * 0.15f
        val yg = pie.y - h * MuebleEstanteNouveau.QUINQUE_Y
        val r = h * 0.12f
        contactoNouveau(Offset(pie.x, pie.y - 1f * u), r * 2.6f, 5f * u, a, hacia = 1f)
        // La sombra del quinqué en el fondo del casillero.
        drawOval(a.sombra(0.2f), Offset(pie.x - rg * (if (a.oscuro) 2.2f else 0.4f), yg - rg * 1.6f), Size(rg * 2.6f, h * 0.9f))
        // El pie y el fuste.
        drawOval(a.luz(P.LATON_OSC, k), Offset(pie.x - r, pie.y - r * 0.3f), Size(r * 2, r * 0.4f))
        drawPath(poligonoNouveau(pie.x - r * 0.92f, pie.y - r * 0.12f, pie.x - r * 0.24f, pie.y - r * 0.62f, pie.x + r * 0.24f, pie.y - r * 0.62f, pie.x + r * 0.92f, pie.y - r * 0.12f), a.luz(P.LATON, k))
        drawPath(poligonoNouveau(pie.x - r * 0.92f, pie.y - r * 0.12f, pie.x - r * 0.24f, pie.y - r * 0.62f, pie.x - r * 0.1f, pie.y - r * 0.62f, pie.x - r * 0.6f, pie.y - r * 0.12f), a.brillo(0.35f, k))
        val fuste = Rect(pie.x - r * 0.15f, yg + rg * 1.8f, pie.x + r * 0.15f, pie.y - r * 0.55f)
        drawRect(a.luz(P.LATON, k), fuste.topLeft, fuste.size)
        drawRect(a.brillo(0.5f, k), Offset(fuste.left + fuste.width * 0.15f, fuste.top), Size(fuste.width * 0.22f, fuste.height))
        drawRect(a.sombra(0.3f), Offset(fuste.right - fuste.width * 0.35f, fuste.top), Size(fuste.width * 0.35f, fuste.height))
        drawRect(a.luz(P.LATON_OSC, k), Offset(pie.x - r * 0.3f, fuste.top + fuste.height * 0.45f), Size(r * 0.6f, 2f * u))
        // El depósito de vidrio violeta, con el petróleo hasta la mitad.
        val dep = Rect(pie.x - rg * 0.78f, yg + rg * 0.86f, pie.x + rg * 0.78f, yg + rg * 1.9f)
        drawOval(a.luz(mezcla(P.VIOLETA, P.IRIS, 0.3f), k), dep.topLeft, dep.size)
        clipPath(Path().apply { addOval(dep) }) {
            drawRect(a.luz(Color(0xFFB88A3A), k).copy(alpha = 0.5f), Offset(dep.left, dep.top + dep.height * 0.5f), Size(dep.width, dep.height))
            redondo(dep, a, k, -1f)
        }
        drawOval(a.luz(P.IRIS_OSC, k).copy(alpha = 0.6f), dep.topLeft, dep.size, style = Stroke(0.6f * u))
        // El collar del mechero, con su rueda.
        drawRect(a.luz(P.LATON_OSC, k), Offset(pie.x - rg * 0.5f, yg + rg * 0.7f), Size(rg, rg * 0.22f))
        drawRect(a.luz(P.LATON, k), Offset(pie.x - rg * 0.5f, yg + rg * 0.7f), Size(rg, rg * 0.07f))
        drawCircle(a.luz(P.LATON, k), rg * 0.12f, Offset(pie.x + rg * 0.62f, yg + rg * 0.8f))
        // El tubo de vidrio, que sale por arriba del globo.
        val tubo = Rect(pie.x - rg * 0.17f, yg - rg * 1.65f, pie.x + rg * 0.17f, yg - rg * 0.7f)
        drawRect(a.luz(Color(0xFFD8E2DE), k).copy(alpha = 0.6f), tubo.topLeft, tubo.size)
        drawRect(a.brillo(0.7f, k), Offset(tubo.left + tubo.width * 0.15f, tubo.top), Size(tubo.width * 0.2f, tubo.height))
        drawRect(a.sombra(0.25f), Offset(tubo.right - tubo.width * 0.25f, tubo.top), Size(tubo.width * 0.25f, tubo.height))
        drawOval(a.luz(Color(0xFFC8D4D0), k), Offset(tubo.left, tubo.top - rg * 0.06f), Size(tubo.width, rg * 0.12f), style = Stroke(0.6f * u))
        // El globo esmerilado, apagado (de noche lo enciende [llamaHueco]).
        if (!a.lumbre) {
            val g = Rect(pie.x - rg, yg - rg, pie.x + rg, yg + rg * 0.86f)
            // El esmerilado: claro arriba a la izquierda, donde le da la luz, y hondo abajo a la derecha.
            drawOval(Brush.radialGradient(listOf(a.luz(Color(0xFFF6F1E4), k), a.luz(Color(0xFFE6DECB), k), a.luz(Color(0xFFC9BFA8), k)),
                Offset(pie.x - rg * 0.35f, yg - rg * 0.4f), rg * 1.6f), g.topLeft, g.size)
            clipPath(Path().apply { addOval(g) }) {
                cenefaGlobo(pie.x, yg, rg, Color(0xFFA89A78).copy(alpha = 0.6f))
                drawRect(a.sombra(0.14f), Offset(pie.x + rg * 0.4f, g.top), Size(rg, g.height))
            }
            drawOval(a.sombra(0.3f), g.topLeft, g.size, style = Stroke(0.6f * u))
            drawOval(a.luz(Color(0xFFD8D0C0), k), Offset(pie.x - rg * 0.34f, g.top - rg * 0.04f), Size(rg * 0.68f, rg * 0.16f))
        }
    }

    /**
     * La cenefa grabada del globo: dos filetes que lo rodean por debajo del ecuador (combados como su vientre) y, entre ellos, una
     * onda continua, en [c] (el esmerilado más hondo). Nada suelto: es vidrio.
     */
    private fun DrawScope.cenefaGlobo(x: Float, y: Float, rg: Float, c: Color) {
        val u = density
        val y0 = y + rg * 0.12f; val y1 = y + rg * 0.36f
        val filetes = Path().apply {
            moveTo(x - rg * 1.1f, y0); quadraticTo(x, y0 + rg * 0.2f, x + rg * 1.1f, y0)
            moveTo(x - rg * 1.1f, y1); quadraticTo(x, y1 + rg * 0.2f, x + rg * 1.1f, y1)
        }
        drawPath(filetes, c, style = Stroke(0.9f * u))
        val onda = Path()
        val n = 24
        for (i in 0..n) {
            val f = i / n.toFloat()
            val xx = x - rg * 1.1f + rg * 2.2f * f
            val comba = rg * 0.2f * (1f - (2f * f - 1f) * (2f * f - 1f))
            val yy = (y0 + y1) / 2 + comba * 0.5f + rg * 0.06f * kotlin.math.sin(f * 6.283f * 3f)
            if (i == 0) onda.moveTo(xx, yy) else onda.lineTo(xx, yy)
        }
        drawPath(onda, c, style = Stroke(1.1f * u))
    }

    /** El globo encendido del quinqué del hueco y su resplandor (de noche; va encima de la luz del cuerpo). */
    fun DrawScope.llamaHueco(r: Rect, d: DisposicionEstante, xq: Float, a: AireNouveau) {
        if (!a.lumbre || xq < 0f) return
        val w = size.width; val u = density
        val h = r.height
        val rg = h * 0.15f
        val c = Offset(xq, r.bottom - h * MuebleEstanteNouveau.QUINQUE_Y)
        val f = if (a.oscuro) (if (a.noche) 0.7f else 1f) else 0.6f
        clipRect(d.izquierda.right, r.top - h * 0.12f, d.derecha.left, r.bottom + h * 0.1f) {
            mancha(c, w * 0.26f, P.LLAMA.copy(alpha = 0.22f * f))
            mancha(c, w * 0.09f, P.FUEGO.copy(alpha = 0.4f * f))
        }
        val g = Rect(xq - rg, c.y - rg, xq + rg, c.y + rg * 0.86f)
        drawOval(androidx.compose.ui.graphics.Brush.radialGradient(listOf(P.FUEGO, mezcla(P.LLAMA, P.FUEGO, 0.5f), mezcla(P.AMBAR, P.LLAMA, 0.5f), mezcla(P.AMBAR, P.CAOBA, 0.25f)),
            Offset(xq, c.y + rg * 0.1f), rg * 1.15f), g.topLeft, g.size)
        clipPath(Path().apply { addOval(g) }) { cenefaGlobo(xq, c.y, rg, Color(0xFFC0742E).copy(alpha = 0.45f)) }
        drawOval(P.LATON_OSC.copy(alpha = 0.5f), g.topLeft, g.size, style = Stroke(0.6f * u))
        // La llama que se ve por el tubo, por encima del globo.
        val ll = Path().apply {
            moveTo(xq, c.y - rg * 1.35f); quadraticTo(xq + rg * 0.16f, c.y - rg * 1.0f, xq, c.y - rg * 0.75f); quadraticTo(xq - rg * 0.16f, c.y - rg * 1.0f, xq, c.y - rg * 1.35f); close()
        }
        drawPath(ll, P.FUEGO.copy(alpha = 0.85f * f))
    }
}
