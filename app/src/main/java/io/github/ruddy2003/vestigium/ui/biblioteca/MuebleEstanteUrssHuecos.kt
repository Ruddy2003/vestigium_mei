package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrss as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que llena los huecos de las repisas donde no llegan las entradas: grupos de dos o tres cosas del club, solapadas, que llenan
 * del 70 al 90 % del alto del hueco y cambian de un hueco a otro. El samovar del club con la tetera encima, en su bandeja; el
 * ábaco del Ideario, aquí de pie contra la pared, con dos libros tumbados delante; el despertador de dos campanas sobre una pila de
 * libros, con la almohadilla de tinta; el gramófono de maleta abierto, con un disco en su funda. En un hueco muy ancho, dos grupos.
 */
internal object HuecosEstanteUrss {
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, g: MarcoUrss) {
        val w = g.w
        val x0 = ocupado + w * 0.018f
        val libre = r.right - x0 - w * 0.03f
        if (libre < w * 0.13f) return
        val i = Math.floorMod(k + k / 4, 4)
        if (libre > w * 0.48f) {
            val mitad = libre * 0.5f
            grupo(i, Rect(x0, r.top, x0 + mitad, r.bottom), g, centrado = true)
            grupo(Math.floorMod(i + 2, 4), Rect(x0 + mitad + w * 0.012f, r.top, r.right - w * 0.03f, r.bottom), g, centrado = true)
        } else grupo(i, Rect(x0, r.top, x0 + libre, r.bottom), g)
    }

    /** Un grupo en su sitio [r]: pegado a las entradas o, si comparte un hueco muy ancho con otro, en medio de su mitad. */
    private fun DrawScope.grupo(cual: Int, r: Rect, g: MarcoUrss, centrado: Boolean = false) {
        val w = g.w
        val an = minOf(r.width, w * 0.3f)
        val x0 = if (centrado) r.left + (r.width - an) * 0.5f else r.left
        val z = Rect(x0, r.top, x0 + an, r.bottom)
        val l = g.luz(z.center.x, z.top + z.height * 0.55f, 0.05f)
        val hacia = g.hacia(z.center.x, z.center.y)
        when (cual) {
            0 -> samovar(z, l, hacia, g)
            1 -> abaco(z, l, hacia, g)
            2 -> despertador(z, l, hacia, g)
            else -> gramofono(z, l, hacia, g)
        }
    }

    /** La sombra que una cosa echa en la pared del fondo (lejos de la luz) y la de contacto en la tabla. */
    private fun DrawScope.sombras(f: Path, x0: Float, x1: Float, pie: Float, l: LuzUrss, hacia: Float) {
        val u = density
        drawPath(corridoUrss(f, -hacia * 6f * u, if (l.a.oscuro) -1f * u else 3f * u), l.s(if (l.a.oscuro) 0.45f else 0.34f))
        drawOval(l.s(0.55f), Offset(x0 - hacia * 2f * u, pie - 2.6f * u), Size(x1 - x0 + 4f * u, 4.4f * u))
    }

    private fun rr(r: Rect, c: Float) = Path().apply { addRoundRect(RoundRect(r, CornerRadius(c))) }

    /** Un libro tumbado, con el lomo hacia quien mira: la tela, los filetes, la banda estampada y el tejuelo. */
    private fun DrawScope.libroTumbado(r: Rect, tela: Color, l: LuzUrss, semilla: Int) {
        val u = density
        drawRoundRect(l.c(tela), r.topLeft, r.size, CornerRadius(1.6f * u))
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(Brush.verticalGradient(listOf(l.b(0.25f), Color.Transparent, l.s(0.3f)), r.top, r.bottom), r.topLeft, r.size)
            listOf(0.07f, 0.92f).forEach { f -> drawRect(l.s(0.3f), Offset(r.left + r.width * f, r.top + 1.6f * u), Size(1f * u, r.height - 3.2f * u)) }
            drawRect(l.c(if (azar(semilla, 1) < 0.5f) P.ROJO else P.NEGRO).copy(alpha = 0.8f), Offset(r.left + r.width * (0.55f + 0.15f * azar(semilla, 2)), r.top), Size(r.width * 0.06f, r.height))
        }
        val tj = Rect(r.left + r.width * 0.16f, r.top + r.height * 0.22f, r.left + r.width * 0.16f + r.height * 1.1f, r.bottom - r.height * 0.22f)
        drawRect(l.c(ETIQUETA_URSS), tj.topLeft, tj.size)
        drawRect(l.c(P.NEGRO).copy(alpha = 0.5f), tj.topLeft, tj.size, style = Stroke(0.6f * u))
        if (l.k > 0.05f) drawRect(l.filo(0.7f), r.topLeft, Size(r.width, 1.2f * u))
    }

    // ---------------------------------------------------------------- el samovar con su tetera

    /**
     * El samovar eléctrico del club, de latón, sobre su bandeja: el pie cuadrado con sus cuatro patitas, el cuerpo de urna con sus
     * estrías, las dos asas de madera negra en sus brazos, el grifo con su llave y el pico, la corona de arriba y, encima, la tetera
     * de loza blanca con su banda roja. Es lo que acompaña al vaso de té del Ideario.
     */
    private fun DrawScope.samovar(z: Rect, l: LuzUrss, hacia: Float, g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.66f, w * 0.15f)
        val cx = z.left + z.width * 0.42f
        val alto = z.height * 0.88f
        val ls = g.luz(cx, pie - alto * 0.5f, 0.05f)
        val laton = mezcla(P.LATON, P.TE, 0.15f)
        // La bandeja, de chapa pintada, vista un poco desde arriba.
        val bandeja = Rect(cx - an * 0.7f, pie - an * 0.12f, cx + an * 0.7f, pie)
        drawOval(ls.s(0.45f), Offset(bandeja.left - hacia * 2f * u, pie - an * 0.06f), Size(bandeja.width + 4f * u, an * 0.1f))
        drawOval(ls.c(mezcla(P.ROJO, P.GRANATE, 0.4f)), bandeja.topLeft, bandeja.size)
        drawOval(ls.c(P.NEGRO), bandeja.deflate(an * 0.05f).topLeft, bandeja.deflate(an * 0.05f).size)
        drawArc(ls.b(0.5f), 190f, 100f, false, bandeja.topLeft, bandeja.size, style = Stroke(1f * u))
        // El pie: el zócalo cuadrado y sus patitas.
        val y0 = pie - an * 0.06f
        val zoc = poligonoUrss(Offset(cx - an * 0.3f, y0), Offset(cx + an * 0.3f, y0), Offset(cx + an * 0.22f, y0 - alto * 0.12f), Offset(cx - an * 0.22f, y0 - alto * 0.12f))
        listOf(-0.3f, 0.3f).forEach { f -> drawCircle(ls.c(laton), an * 0.04f, Offset(cx + an * f, y0)) }
        planosUrss(zoc, ls.c(laton), ls, hacia, 0.25f, 0.3f)
        // El cuello del pie y el cuerpo de urna.
        val yc = y0 - alto * 0.12f
        val ya = yc - alto * 0.5f
        val cuerpo = Path().apply {
            moveTo(cx - an * 0.14f, yc)
            cubicTo(cx - an * 0.55f, yc - alto * 0.06f, cx - an * 0.56f, ya + alto * 0.1f, cx - an * 0.38f, ya)
            lineTo(cx + an * 0.38f, ya)
            cubicTo(cx + an * 0.56f, ya + alto * 0.1f, cx + an * 0.55f, yc - alto * 0.06f, cx + an * 0.14f, yc)
            close()
        }
        drawPath(corridoUrss(cuerpo, -hacia * 6f * u, if (ls.a.oscuro) -1f * u else 3f * u), ls.s(if (ls.a.oscuro) 0.45f else 0.34f))
        cilindroUrss(cuerpo, ls.c(laton), ls, hacia, 0.75f)
        clipPath(cuerpo) {
            // Las estrías del cuerpo.
            for (j in 1..7) {
                val x = cx - an * 0.5f + an * j / 8f
                drawLine(ls.s(0.22f), Offset(x, ya), Offset(x + (x - cx) * 0.2f, yc), 0.9f * u)
            }
            drawRect(ls.s(0.3f), Offset(cx - an, ya), Size(an * 2, alto * 0.03f))
        }
        // Las asas: brazos de latón y mangos de madera negra.
        listOf(-1f, 1f).forEach { sg ->
            val a0 = Offset(cx + sg * an * 0.42f, ya + alto * 0.08f)
            val a1 = Offset(cx + sg * an * 0.62f, ya + alto * 0.05f)
            drawLine(ls.c(laton), a0, a1, 1.6f * u)
            drawLine(ls.c(P.NEGRO), a1, a1 + Offset(sg * an * 0.02f, alto * 0.12f), 3.2f * u, StrokeCap.Round)
            drawLine(ls.b(0.35f), a1 + Offset(-0.8f * u, 0f), a1 + Offset(sg * an * 0.02f - 0.8f * u, alto * 0.12f), 0.7f * u)
        }
        // El grifo: el caño, la llave y el pico, hacia delante.
        val gr = Offset(cx + an * 0.1f, yc - alto * 0.03f)
        drawLine(ls.c(laton), gr, gr + Offset(an * 0.08f, alto * 0.03f), 3f * u, StrokeCap.Round)
        drawLine(ls.c(laton), gr + Offset(an * 0.08f, alto * 0.03f), gr + Offset(an * 0.09f, alto * 0.09f), 2.2f * u, StrokeCap.Round)
        drawLine(ls.c(P.NEGRO), gr + Offset(an * 0.02f, -alto * 0.03f), gr + Offset(an * 0.12f, -alto * 0.05f), 2f * u, StrokeCap.Round)
        drawCircle(ls.c(laton), 2.2f * u, gr)
        // La corona de arriba y la tetera de loza encima.
        val cor = Rect(cx - an * 0.2f, ya - alto * 0.06f, cx + an * 0.2f, ya)
        drawRect(ls.c(laton), cor.topLeft, cor.size)
        drawRect(ls.b(0.5f), cor.topLeft, Size(cor.width, 1f * u))
        val lt = g.luz(cx, ya - alto * 0.15f, 0.05f)
        val te = Path().apply {
            val yb = cor.top; val yt = yb - alto * 0.2f
            moveTo(cx - an * 0.18f, yb)
            cubicTo(cx - an * 0.34f, yb - alto * 0.02f, cx - an * 0.32f, yt + alto * 0.04f, cx - an * 0.14f, yt)
            lineTo(cx + an * 0.14f, yt)
            cubicTo(cx + an * 0.32f, yt + alto * 0.04f, cx + an * 0.34f, yb - alto * 0.02f, cx + an * 0.18f, yb)
            close()
        }
        val tb = te.getBounds()
        // El pico de la tetera y el asa.
        drawLine(lt.c(P.CARTULINA), Offset(tb.left + tb.width * 0.12f, tb.center.y + tb.height * 0.1f), Offset(tb.left - an * 0.12f, tb.top + tb.height * 0.05f), 2.6f * u, StrokeCap.Round)
        drawArc(lt.c(P.CARTULINA), -80f, 160f, false, Offset(tb.right - an * 0.08f, tb.top + tb.height * 0.15f), Size(an * 0.16f, tb.height * 0.6f), style = Stroke(2f * u))
        cilindroUrss(te, lt.c(P.CARTULINA), lt, hacia, 0.6f)
        clipPath(te) { drawRect(lt.c(P.ROJO), Offset(tb.left, tb.top + tb.height * 0.42f), Size(tb.width, tb.height * 0.14f)) }
        drawOval(lt.c(P.CARTULINA), Offset(cx - an * 0.1f, tb.top - alto * 0.03f), Size(an * 0.2f, alto * 0.05f))
        drawCircle(lt.c(P.ROJO), alto * 0.018f, Offset(cx, tb.top - alto * 0.03f))
        if (ls.k > 0.05f) drawLine(ls.filo(0.85f), Offset(if (hacia < 0f) cx - an * 0.48f else cx + an * 0.48f, ya + alto * 0.08f), Offset(if (hacia < 0f) cx - an * 0.3f else cx + an * 0.3f, yc - alto * 0.04f), 1.4f * u)
    }

    // ---------------------------------------------------------------- el ábaco de pie con dos libros tumbados

    /**
     * El ábaco del Ideario (el schioty), aquí de pie contra la pared del hueco y un poco ladeado: el marco de madera, las varillas de
     * alambre y las cuentas claras, con las dos del medio oscuras, corridas como quedaron; delante, dos libros tumbados.
     */
    private fun DrawScope.abaco(z: Rect, l: LuzUrss, hacia: Float, g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = z.bottom
        val aw = minOf(z.width * 0.8f, w * 0.17f); val ah = z.height * 0.86f
        val r = Rect(z.left + z.width * 0.08f, pie - ah, z.left + z.width * 0.08f + aw, pie)
        val la = g.luz(r.center, 0.03f)
        rotate(-4f, Offset(r.left, r.bottom)) {
            sombras(Path().apply { addRect(r) }, r.left, r.right, pie, la, hacia)
            val gr = w * 0.013f
            val dentro = Rect(r.left + gr, r.top + gr, r.right - gr, r.bottom - gr)
            drawRect(la.s(0.15f), dentro.topLeft, dentro.size)
            val filas = 10
            val corridas = listOf(3, 7, 2, 9, 5, 1, 6, 0, 8, 4)
            for (f in 0 until filas) {
                val yy = dentro.top + dentro.height * (f + 0.5f) / filas
                val lf = g.luz(r.center.x, yy, 0.035f)
                drawLine(lf.c(mezcla(P.METAL, P.ACERO_OSC, 0.4f)), Offset(dentro.left, yy), Offset(dentro.right, yy), 1f * u)
                val cuartos = f == 6
                val cuentas = if (cuartos) 4 else 10
                val izq = if (cuartos) 1 else corridas[f]
                val cw = minOf(dentro.width / 13f, w * 0.011f); val chh = dentro.height / filas * 0.82f
                for (j in 0 until cuentas) {
                    val x = if (j < izq) dentro.left + cw * (j + 0.5f) + 0.6f * u else dentro.right - cw * (cuentas - j - 0.5f) - 0.6f * u
                    val oscura = !cuartos && (j == 4 || j == 5)
                    val col = if (oscura) mezcla(P.NEGRO, P.PARQUET_OSC, 0.35f) else mezcla(P.PARQUET, P.CARTULINA, if ((j + f) % 3 == 0) 0.3f else 0.42f)
                    drawOval(lf.c(col), Offset(x - cw * 0.48f, yy - chh / 2), Size(cw * 0.96f, chh))
                    drawOval(lf.s(0.22f), Offset(x + cw * 0.05f, yy - chh * 0.4f), Size(cw * 0.4f, chh * 0.8f))
                    drawOval(lf.b(0.6f), Offset(x - cw * 0.3f, yy - chh * 0.34f), Size(cw * 0.26f, chh * 0.32f))
                }
            }
            val madera = mezcla(P.PARQUET, P.TE, 0.25f)
            listOf(Rect(r.left, r.top, r.left + gr, r.bottom), Rect(r.right - gr, r.top, r.right, r.bottom)).forEach { m ->
                drawRect(la.c(madera), m.topLeft, m.size)
                drawRect(la.b(0.4f), m.topLeft, Size(1f * u, m.height))
                drawRect(la.s(0.3f), Offset(m.right - 1.2f * u, m.top), Size(1.2f * u, m.height))
            }
            listOf(Rect(r.left - 1f * u, r.top, r.right + 1f * u, r.top + gr), Rect(r.left - 1f * u, r.bottom - gr, r.right + 1f * u, r.bottom)).forEach { m ->
                drawRect(la.c(mezcla(madera, P.PARQUET_OSC, 0.15f)), m.topLeft, m.size)
                drawRect(la.b(0.5f), m.topLeft, Size(m.width, 1f * u))
                drawRect(la.s(0.3f), Offset(m.left, m.bottom - 1.2f * u), Size(m.width, 1.2f * u))
            }
            if (la.k > 0.05f) drawRect(la.filo(0.8f), Offset(if (hacia < 0f) r.left else r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        }
        // Los dos libros tumbados delante, al pie del ábaco.
        val lb = g.luz(z.center.x, pie - z.height * 0.1f, 0.05f)
        val b1 = Rect(z.left + z.width * 0.3f, pie - z.height * 0.12f, z.left + z.width * 0.98f, pie)
        val b2 = Rect(z.left + z.width * 0.36f, b1.top - z.height * 0.1f, z.left + z.width * 0.92f, b1.top)
        sombras(Path().apply { addRect(b1); addRect(b2) }, b1.left, b1.right, pie, lb, hacia)
        libroTumbado(b1, P.OLEO_OSC, lb, 8201)
        drawRect(lb.s(0.35f), Offset(b2.left + 2f * u, b2.bottom), Size(b2.width, 1.8f * u))
        libroTumbado(b2, mezcla(P.CARTON, P.PARQUET, 0.4f), lb, 8202)
    }

    // ---------------------------------------------------------------- el despertador sobre los libros, con la almohadilla

    /**
     * Tres libros tumbados y encima el despertador de dos campanas: la caja redonda de chapa esmaltada con el aro cromado, la esfera
     * con sus marcas (sin cifras) y sus dos agujas, las dos campanas con el martillo en medio, el asa de arriba y las dos patas; a su
     * lado, la almohadilla de tinta violeta en su lata.
     */
    private fun DrawScope.despertador(z: Rect, l: LuzUrss, hacia: Float, g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = z.bottom
        val telas = listOf(mezcla(P.NEGRO, P.PARQUET_OSC, 0.3f), mezcla(P.ROJO, P.GRANATE, 0.5f), mezcla(P.VERDE, P.NEGRO, 0.2f))
        var y = pie
        val pila = telas.mapIndexed { i, _ ->
            val al = z.height * (0.1f + 0.012f * i)
            val r = Rect(z.left + z.width * (0.02f + 0.04f * i), y - al, z.left + z.width * (0.86f - 0.03f * i), y)
            y -= al; r
        }
        val lb = g.luz(z.center.x, pie - z.height * 0.15f, 0.05f)
        sombras(Path().apply { pila.forEach { addRect(it) } }, pila[0].left, pila[0].right, pie, lb, hacia)
        pila.forEachIndexed { i, r ->
            if (i > 0) drawRect(lb.s(0.35f), Offset(r.left + 2f * u, r.bottom), Size(r.width, 1.8f * u))
            libroTumbado(r, telas[i], lb, 8300 + i)
        }
        // El despertador.
        val sup = pila.last().top
        val rr0 = minOf(z.width * 0.24f, z.height * 0.2f, w * 0.06f)
        val c = Offset(z.left + z.width * 0.42f, sup - rr0 - w * 0.012f)
        val ld = g.luz(c, 0.05f)
        // Las patas.
        listOf(-0.55f, 0.55f).forEach { f -> drawLine(ld.c(P.METAL), Offset(c.x + rr0 * f, c.y + rr0 * 0.75f), Offset(c.x + rr0 * f * 1.25f, sup), 2f * u, StrokeCap.Round) }
        // Las campanas y el martillo.
        listOf(-1f, 1f).forEach { sg ->
            val cc = Offset(c.x + sg * rr0 * 0.62f, c.y - rr0 * 0.95f)
            val campana = Path().apply { addArc(Rect(cc.x - rr0 * 0.42f, cc.y - rr0 * 0.42f, cc.x + rr0 * 0.42f, cc.y + rr0 * 0.42f), 180f, 180f); close() }
            rotate(sg * 24f, cc) {
                drawPath(campana, ld.c(mezcla(P.METAL, P.LATON, 0.3f)))
                drawPath(corridoUrss(campana, -sg * rr0 * 0.15f, rr0 * 0.05f), ld.b(0.35f))
                drawLine(ld.s(0.4f), Offset(cc.x - rr0 * 0.42f, cc.y), Offset(cc.x + rr0 * 0.42f, cc.y), 1f * u)
            }
        }
        drawLine(ld.c(P.METAL), Offset(c.x, c.y - rr0), Offset(c.x, c.y - rr0 * 1.45f), 1.4f * u)
        drawCircle(ld.c(P.METAL), 1.6f * u, Offset(c.x, c.y - rr0 * 1.45f))
        // La caja redonda, de esmalte rojo, con su aro.
        val caja = ovaloUrss(c, rr0, rr0)
        sombras(caja, c.x - rr0, c.x + rr0, sup, ld, hacia)
        cilindroUrss(caja, ld.c(mezcla(P.ROJO, P.GRANATE, 0.2f)), ld, hacia, 0.4f)
        drawCircle(ld.c(P.METAL), rr0 * 0.82f, c)
        val esfera = ld.c(mezcla(P.CARTULINA, P.CAL, 0.4f))
        drawCircle(esfera, rr0 * 0.72f, c)
        drawCircle(ld.b(0.6f), rr0 * 0.82f, c, style = Stroke(0.8f * u))
        // Las marcas de las horas: rayitas, sin cifras.
        for (j in 0 until 12) {
            val a = j * 30f * 0.017453f
            val p0 = Offset(c.x + cos(a) * rr0 * 0.6f, c.y + sin(a) * rr0 * 0.6f)
            val p1 = Offset(c.x + cos(a) * rr0 * 0.68f, c.y + sin(a) * rr0 * 0.68f)
            drawLine(ld.c(P.NEGRO), p0, p1, if (j % 3 == 0) 1.3f * u else 0.7f * u)
        }
        drawLine(ld.c(P.NEGRO), c, c + Offset(rr0 * 0.08f, -rr0 * 0.42f), 1.4f * u, StrokeCap.Round)
        drawLine(ld.c(P.NEGRO), c, c + Offset(rr0 * 0.38f, rr0 * 0.18f), 1f * u, StrokeCap.Round)
        drawCircle(ld.c(P.ROJO), 1.3f * u, c)
        drawArc(ld.b(0.7f), 200f, 70f, false, Offset(c.x - rr0 * 0.66f, c.y - rr0 * 0.66f), Size(rr0 * 1.32f, rr0 * 1.32f), style = Stroke(1.2f * u))
        if (ld.k > 0.05f) drawArc(ld.filo(0.9f), if (hacia < 0f) 160f else -20f, 60f, false, Offset(c.x - rr0, c.y - rr0), Size(rr0 * 2, rr0 * 2), style = Stroke(1.4f * u))
        // La almohadilla de tinta en su lata, a la derecha, sobre el libro de arriba.
        val lt = Rect(z.left + z.width * 0.62f, sup - z.height * 0.06f, z.left + z.width * 0.86f, sup)
        val lla = g.luz(lt.center, 0.05f)
        drawRect(lla.c(mezcla(P.OLEO_OSC, P.NEGRO, 0.25f)), lt.topLeft, lt.size)
        drawRect(lla.c(P.METAL), lt.topLeft, Size(lt.width, lt.height * 0.3f))
        drawRect(lla.c(P.VIOLETA), Offset(lt.left + lt.width * 0.1f, lt.top + lt.height * 0.05f), Size(lt.width * 0.8f, lt.height * 0.2f))
        drawRect(lla.b(0.4f), lt.topLeft, Size(lt.width, 1f * u))
    }

    // ---------------------------------------------------------------- el gramófono de maleta

    /**
     * El gramófono de maleta del club (el patefón): la caja forrada de tela granate con sus cantoneras de metal, la tapa abierta de
     * pie detrás, forrada de tela cruda por dentro, el disco negro con sus surcos y su etiqueta, el brazo cromado con la cabeza
     * apoyada en el disco y la manivela del costado; a su lado, un disco en su funda de papel apoyado contra la pared.
     */
    private fun DrawScope.gramofono(z: Rect, l: LuzUrss, hacia: Float, g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.78f, w * 0.2f)
        val x0 = z.left + z.width * 0.04f
        val alCaja = z.height * 0.24f
        val yc = pie - alCaja
        val lg = g.luz(x0 + an * 0.5f, pie - z.height * 0.4f, 0.04f)
        val tela = mezcla(P.GRANATE, P.ROJO, 0.15f)
        // El disco en su funda, de pie contra la pared, a la derecha (detrás de la caja).
        val fx = x0 + an * 0.78f
        val funda = Rect(fx, pie - z.height * 0.62f, fx + minOf(z.width * 0.3f, z.height * 0.6f), pie)
        val lf = g.luz(funda.center, 0.02f)
        rotate(4f, Offset(funda.left, funda.bottom)) {
            drawPath(corridoUrss(Path().apply { addRect(funda) }, -hacia * 5f * u, 2.5f * u), lf.s(0.3f))
            drawRect(lf.c(P.ESTRAZA), funda.topLeft, funda.size)
            drawCircle(lf.c(P.NEGRO), funda.width * 0.2f, funda.center)
            drawCircle(lf.c(P.CARTULINA_OSC), funda.width * 0.08f, funda.center)
            drawRect(Brush.horizontalGradient(listOf(lf.b(0.2f), Color.Transparent, lf.s(0.25f)), funda.left, funda.right), funda.topLeft, funda.size)
        }
        // La tapa abierta, de pie detrás de la caja, y su tela cruda por dentro.
        val tapa = Rect(x0 + an * 0.04f, pie - z.height * 0.86f, x0 + an * 0.96f, yc + alCaja * 0.2f)
        drawPath(corridoUrss(Path().apply { addRect(tapa) }, -hacia * 6f * u, 3f * u), lg.s(0.32f))
        drawRect(lg.c(tela), tapa.topLeft, tapa.size)
        val dentro = tapa.deflate(an * 0.05f)
        drawRect(lg.c(mezcla(P.BRAMANTE, P.CARTULINA, 0.3f)), dentro.topLeft, dentro.size)
        drawRect(Brush.verticalGradient(listOf(lg.s(0.3f), Color.Transparent), dentro.top, dentro.top + dentro.height * 0.4f), dentro.topLeft, dentro.size)
        drawRect(lg.b(0.4f), tapa.topLeft, Size(tapa.width, 1f * u))
        // El cierre y la bisagra.
        drawRect(lg.c(P.METAL), Offset(tapa.center.x - an * 0.05f, tapa.top + 1f * u), Size(an * 0.1f, an * 0.04f))
        // La caja forrada de tela, con sus cantoneras.
        val caja = Rect(x0, yc, x0 + an, pie)
        drawOval(lg.s(0.55f), Offset(caja.left - hacia * 2f * u, pie - 2.6f * u), Size(caja.width + 4f * u, 4.4f * u))
        planosUrss(Path().apply { addRect(caja) }, lg.c(tela), lg, hacia, 0.18f, 0.22f)
        drawRect(lg.c(P.METAL), Offset(caja.left, caja.top + caja.height * 0.45f), Size(caja.width, 1.2f * u))
        listOf(caja.left, caja.right - an * 0.08f).forEach { x ->
            drawRect(lg.c(P.METAL), Offset(x, caja.top), Size(an * 0.08f, an * 0.05f))
            drawRect(lg.c(P.METAL), Offset(x, caja.bottom - an * 0.05f), Size(an * 0.08f, an * 0.05f))
        }
        // La cara de arriba, vista desde arriba: el plato con el disco, sus surcos y su etiqueta, y el brazo.
        val top = Rect(x0 + an * 0.04f, yc - alCaja * 0.42f, x0 + an * 0.96f, yc + alCaja * 0.04f)
        drawPath(poligonoUrss(Offset(x0, yc), Offset(x0 + an, yc), Offset(x0 + an * 0.96f, top.top), Offset(x0 + an * 0.04f, top.top)), lg.c(mezcla(tela, P.NEGRO, 0.3f)))
        val disco = Rect(x0 + an * 0.12f, top.top + top.height * 0.08f, x0 + an * 0.7f, top.bottom - top.height * 0.08f)
        drawOval(lg.c(P.NEGRO), disco.topLeft, disco.size)
        val surcos = Path()
        for (k in 1..3) surcos.addOval(disco.deflate(disco.height * 0.1f * k).let { Rect(it.left - disco.width * 0.04f * k + disco.width * 0.1f * k * 0.6f, it.top, it.right + disco.width * 0.04f * k - disco.width * 0.1f * k * 0.6f, it.bottom) })
        drawPath(surcos, lg.b(0.18f), style = Stroke(0.6f * u))
        drawArc(lg.b(0.5f), 200f, 70f, false, disco.topLeft, disco.size, style = Stroke(1.2f * u))
        drawOval(lg.c(mezcla(P.ROJO, P.CARTULINA, 0.15f)), Offset(disco.center.x - disco.width * 0.13f, disco.center.y - disco.height * 0.13f), Size(disco.width * 0.26f, disco.height * 0.26f))
        // El brazo cromado: del pivote de atrás a la derecha hasta la cabeza, apoyada en el disco.
        val piv = Offset(x0 + an * 0.86f, top.top + top.height * 0.25f)
        val cab = Offset(x0 + an * 0.58f, disco.center.y + disco.height * 0.15f)
        drawCircle(lg.c(P.METAL), an * 0.05f, piv)
        val brazo = abiertaUrss(listOf(piv, Offset(x0 + an * 0.82f, disco.center.y + disco.height * 0.3f), cab))
        drawPath(corridoUrss(brazo, 1.2f * u, 1.4f * u), lg.s(0.35f), style = Stroke(2.2f * u))
        drawPath(brazo, lg.c(P.METAL), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawRoundRect(lg.c(P.METAL), Offset(cab.x - an * 0.05f, cab.y - an * 0.025f), Size(an * 0.08f, an * 0.05f), CornerRadius(an * 0.01f))
        // La manivela del costado.
        val mv = Offset(caja.right, caja.top + caja.height * 0.5f)
        drawLine(lg.c(P.METAL), mv, mv + Offset(an * 0.1f, -alCaja * 0.1f), 1.8f * u, StrokeCap.Round)
        drawLine(lg.c(P.NEGRO), mv + Offset(an * 0.1f, -alCaja * 0.1f), mv + Offset(an * 0.1f, alCaja * 0.25f), 3f * u, StrokeCap.Round)
        if (lg.k > 0.05f) drawRect(lg.filo(0.8f), Offset(tapa.left, tapa.top), Size(tapa.width, 1.4f * u))
    }
}
