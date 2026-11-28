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
 * del 70 al 90 % del alto del hueco y cambian de un hueco a otro. El aritmómetro de manivela delante de dos carpetas de pie; el
 * ábaco del Ideario, aquí de pie contra la pared, con dos libros tumbados delante; el despertador de dos campanas sobre una pila de
 * libros, con la almohadilla de tinta; la plancha de linóleo grabada con su rodillo de entintar y la gubia. En un hueco muy ancho,
 * dos grupos.
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
            0 -> aritmometro(z, l, hacia, g)
            1 -> abaco(z, l, hacia, g)
            2 -> despertador(z, l, hacia, g)
            else -> linoleo(z, l, hacia, g)
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

    // ---------------------------------------------------------------- el aritmómetro delante de dos carpetas

    /**
     * Dos carpetas de cartón de pie, contra la pared (una gris y una roja, con sus cintas), y delante el aritmómetro: la caja de
     * chapa negra con la tapa inclinada y sus ranuras de palancas, el carro de abajo con sus ventanillas y la manivela del costado.
     */
    private fun DrawScope.aritmometro(z: Rect, l: LuzUrss, hacia: Float, g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = z.bottom
        // Las carpetas de pie.
        val cols = listOf(mezcla(P.CARTON, P.ACERO, 0.35f), mezcla(P.ROJO, P.GRANATE, 0.4f))
        val silueta = Path()
        cols.forEachIndexed { i, c ->
            val r = Rect(z.left + z.width * (0.06f + 0.2f * i), z.top + z.height * (0.14f + 0.05f * i), z.left + z.width * (0.24f + 0.2f * i), pie)
            silueta.addRect(r)
            val lc = g.luz(r.center, 0.03f)
            rotate(if (i == 0) -2f else 1.5f, Offset(r.center.x, pie)) {
                drawRect(lc.c(c), r.topLeft, r.size)
                drawRect(Brush.horizontalGradient(listOf(lc.b(0.25f), Color.Transparent, lc.s(0.3f)), r.left, r.right), r.topLeft, r.size)
                drawRect(lc.c(mezcla(P.CARTULINA, P.CARTON, 0.2f)), Offset(r.left + r.width * 0.15f, r.top - z.height * 0.03f), Size(r.width * 0.7f, z.height * 0.03f))
                // La cinta atada, que cuelga del canto.
                drawLine(lc.c(P.CARTULINA), Offset(r.right - r.width * 0.2f, r.top + r.height * 0.4f), Offset(r.right + r.width * 0.12f, r.top + r.height * 0.52f), 1.4f * u, StrokeCap.Round)
                drawRect(lc.c(ETIQUETA_URSS), Offset(r.left + r.width * 0.2f, r.top + r.height * 0.14f), Size(r.width * 0.6f, r.height * 0.12f))
            }
        }
        sombras(silueta, z.left, z.left + z.width * 0.46f, pie, l, hacia)
        // El aritmómetro.
        val c = Rect(z.left + z.width * 0.2f, pie - z.height * 0.36f, z.left + z.width * 0.98f, pie)
        val caja = Path().apply {
            moveTo(c.left, c.bottom); lineTo(c.left, c.top + c.height * 0.38f); lineTo(c.left + c.width * 0.08f, c.top)
            lineTo(c.right - c.width * 0.06f, c.top); lineTo(c.right, c.top + c.height * 0.3f); lineTo(c.right, c.bottom); close()
        }
        val la = g.luz(c.center, 0.05f)
        sombras(caja, c.left, c.right, pie, la, hacia)
        val negro = mezcla(P.NEGRO, P.ACERO_OSC, 0.25f)
        planosUrss(caja, la.c(negro), la, hacia, 0.18f, 0.22f)
        // La tapa inclinada, más clara, con las ranuras de las palancas y sus botones.
        val tapa = poligonoUrss(Offset(c.left + c.width * 0.08f, c.top), Offset(c.right - c.width * 0.06f, c.top), Offset(c.right - c.width * 0.04f, c.top + c.height * 0.42f), Offset(c.left + c.width * 0.02f, c.top + c.height * 0.42f))
        drawPath(tapa, la.c(mezcla(negro, P.ACERO, 0.3f)))
        drawPath(tapa, la.b(0.25f), style = Stroke(0.8f * u))
        val n = 8
        for (j in 0 until n) {
            val x = c.left + c.width * (0.14f + 0.72f * j / (n - 1))
            drawLine(la.c(P.NEGRO), Offset(x, c.top + c.height * 0.06f), Offset(x + c.width * 0.01f, c.top + c.height * 0.38f), 1.4f * u)
            val bt = Offset(x + c.width * 0.004f, c.top + c.height * (0.12f + 0.22f * azar(8101, j)))
            drawCircle(la.c(P.METAL), 1.5f * u, bt)
        }
        // El carro de abajo, con sus ventanillas (sin cifras).
        val carro = Rect(c.left + c.width * 0.04f, c.top + c.height * 0.56f, c.right - c.width * 0.02f, c.top + c.height * 0.8f)
        drawRect(la.c(mezcla(P.METAL, P.ACERO, 0.4f)), carro.topLeft, carro.size)
        drawRect(la.b(0.5f), carro.topLeft, Size(carro.width, 1f * u))
        for (j in 0 until 9) drawRect(la.c(P.NEGRO).copy(alpha = 0.8f), Offset(carro.left + carro.width * (0.06f + 0.1f * j), carro.top + carro.height * 0.28f), Size(carro.width * 0.055f, carro.height * 0.44f))
        // La manivela del costado de la derecha, con su mango.
        val ej = Offset(c.right - c.width * 0.02f, c.top + c.height * 0.45f)
        val mg = Offset(ej.x + z.width * 0.02f, c.top - c.height * 0.25f)
        drawLine(la.s(0.3f), ej + Offset(1.4f * u, 1.4f * u), mg + Offset(1.4f * u, 1.4f * u), 2f * u)
        drawLine(la.c(P.METAL), ej, mg, 2f * u, StrokeCap.Round)
        drawRoundRect(la.c(P.NEGRO), Offset(mg.x - 2f * u, mg.y - 6f * u), Size(4f * u, 8f * u), CornerRadius(2f * u))
        drawCircle(la.c(P.METAL), 2.4f * u, ej)
        if (la.k > 0.05f) drawLine(la.filo(0.8f), Offset(c.left + c.width * 0.08f, c.top), Offset(c.right - c.width * 0.06f, c.top), 1.2f * u)
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

    // ---------------------------------------------------------------- la plancha de linóleo, el rodillo y la gubia

    /**
     * La plancha de linóleo grabada para un cartel, de pie contra la pared: el linóleo pardo sobre su taco de madera, entintado de
     * negro, con lo tallado (bandas, un disco y una cuña) en el color del linóleo; delante, tumbados, el rodillo de entintar (el
     * caucho negro, el armazón de alambre y el mango) y la gubia de mango de madera.
     */
    private fun DrawScope.linoleo(z: Rect, l: LuzUrss, hacia: Float, g: MarcoUrss) {
        val w = g.w; val u = density
        val pie = z.bottom
        val pw = minOf(z.width * 0.74f, w * 0.17f); val ph = z.height * 0.8f
        val r = Rect(z.left + z.width * 0.12f, pie - ph, z.left + z.width * 0.12f + pw, pie)
        val lp = g.luz(r.center, 0.025f)
        rotate(3f, Offset(r.right, r.bottom)) {
            sombras(Path().apply { addRect(r) }, r.left, r.right, pie, lp, hacia)
            // El taco de madera (se ve su canto) y el linóleo encima.
            drawRect(lp.c(P.ROBLE_LUZ), Offset(r.left, r.top + 2f * u), Size(r.width + 2.5f * u, r.height - 2f * u))
            drawRect(lp.s(0.3f), Offset(r.right, r.top + 2f * u), Size(2.5f * u, r.height - 2f * u))
            val lino = Rect(r.left, r.top, r.right, r.bottom - 2f * u)
            drawRect(lp.c(mezcla(P.PARQUET, P.ACERO_OSC, 0.4f)), lino.topLeft, lino.size)
            // Lo entintado (negro) y lo tallado (el linóleo que se ve): un disco, una banda, una cuña.
            clipRect(lino.left, lino.top, lino.right, lino.bottom) {
                val tinta = lp.c(P.NEGRO)
                val f = Path().apply {
                    addRect(Rect(lino.left, lino.top, lino.right, lino.top + lino.height * 0.4f))
                    addRect(Rect(lino.left, lino.top + lino.height * 0.56f, lino.left + lino.width * 0.4f, lino.bottom))
                }
                drawPath(f, tinta)
                drawCircle(lp.c(mezcla(P.PARQUET, P.ACERO_OSC, 0.4f)), lino.width * 0.22f, Offset(lino.left + lino.width * 0.62f, lino.top + lino.height * 0.22f))
                drawPath(poligonoUrss(Offset(lino.left + lino.width * 0.45f, lino.bottom), Offset(lino.right, lino.top + lino.height * 0.5f), Offset(lino.right, lino.bottom)), tinta)
                // Las marcas de la gubia en lo tallado.
                for (j in 0 until 7) {
                    val x = lino.left + lino.width * (0.45f + 0.08f * j)
                    drawLine(lp.s(0.25f), Offset(x, lino.top + lino.height * 0.45f), Offset(x - lino.width * 0.04f, lino.top + lino.height * 0.55f), 0.7f * u)
                }
                drawRect(lp.b(0.15f), lino.topLeft, Size(lino.width * 0.3f, lino.height))
            }
            drawRect(lp.b(0.5f), lino.topLeft, Size(lino.width, 1f * u))
            if (lp.k > 0.05f) drawRect(lp.filo(0.8f), Offset(if (hacia < 0f) r.left else r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        }
        // El rodillo de entintar, tumbado delante: el caucho negro con la tinta brillante, el armazón y el mango.
        val ro = Rect(z.left + z.width * 0.3f, pie - z.height * 0.13f, z.left + z.width * 0.66f, pie - z.height * 0.01f)
        val lr = g.luz(ro.center, 0.05f)
        sombras(rr(ro, ro.height * 0.5f), ro.left, ro.right + z.width * 0.3f, pie, lr, hacia)
        val cau = rr(ro, ro.height * 0.5f)
        drawPath(cau, Brush.verticalGradient(listOf(lr.c(P.NEGRO), lr.c(mezcla(P.NEGRO, P.OLEO_OSC, 0.4f)), lr.c(P.NEGRO)), ro.top, ro.bottom))
        drawLine(lr.b(0.7f), Offset(ro.left + ro.height * 0.4f, ro.top + ro.height * 0.28f), Offset(ro.right - ro.height * 0.4f, ro.top + ro.height * 0.28f), 1.2f * u, StrokeCap.Round)
        val arm = Path().apply {
            moveTo(ro.left, ro.center.y); lineTo(ro.left - z.width * 0.02f, ro.center.y)
            lineTo(ro.left - z.width * 0.02f, ro.top - z.height * 0.04f); lineTo(ro.right + z.width * 0.04f, ro.top - z.height * 0.04f)
            lineTo(ro.right + z.width * 0.04f, ro.center.y); lineTo(ro.right, ro.center.y)
        }
        drawPath(arm, lr.c(P.METAL), style = Stroke(1.4f * u))
        val mango = Rect(ro.right + z.width * 0.04f, ro.top - z.height * 0.06f, ro.right + z.width * 0.3f, ro.top - z.height * 0.0f)
        drawRoundRect(lr.c(P.ROBLE_LUZ), mango.topLeft, mango.size, CornerRadius(mango.height * 0.5f))
        drawRoundRect(lr.b(0.4f), mango.topLeft, Size(mango.width, mango.height * 0.35f), CornerRadius(mango.height * 0.3f))
        // La gubia, tumbada sobre la tabla delante de la plancha.
        val gb = Offset(z.left + z.width * 0.06f, pie - z.height * 0.02f)
        drawLine(lr.s(0.3f), gb + Offset(1f * u, 1.4f * u), gb + Offset(z.width * 0.22f + 1f * u, -z.height * 0.03f + 1.4f * u), 3f * u, StrokeCap.Round)
        drawLine(lr.c(mezcla(P.ROBLE, P.TE, 0.3f)), gb, gb + Offset(z.width * 0.16f, -z.height * 0.022f), 3.6f * u, StrokeCap.Round)
        drawLine(lr.c(P.METAL), gb + Offset(z.width * 0.16f, -z.height * 0.022f), gb + Offset(z.width * 0.24f, -z.height * 0.032f), 1.4f * u, StrokeCap.Round)
    }
}
