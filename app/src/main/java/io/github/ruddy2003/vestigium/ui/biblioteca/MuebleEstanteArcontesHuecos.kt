package io.github.ruddy2003.vestigium.ui.biblioteca

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
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que hay en los huecos libres de las repisas del estante de Arcontes ([MuebleEstanteArcontes]): grupos de dos o tres cosas
 * de la casa del salador y del círculo, solapadas y del 70 al 90 % del alto del hueco, distintos de una repisa a otra: el cántaro
 * con el cuenco de sal y la pesa de piedra; el cesto de juncos con las hojas sueltas (el del Ideario) y el haz de cálamos; la jarra
 * y el vaso de piedra (de los que se usaban en Galilea en el siglo I) sobre las tablillas atadas; y el cesto bajo de la lana con el
 * huso, sobre el manto doblado de dos franjas. En las repisas con lámpara, la lámpara sobre su piedra con la alcuza: arde de noche.
 */
internal object HuecosEstanteArcontes {
    private val P = PaletaArcontes

    /** Cuántos grupos distintos hay (sin contar el de la lámpara). */
    const val GRUPOS = 4

    /** Si en el hueco libre de una repisa (de [oc] al costado [x1]) cabe algo. */
    fun conSitio(oc: Float, x1: Float, w: Float) = x1 - oc > w * 0.12f

    /**
     * El grupo [g] en el hueco de la repisa [k] ([r]), de [oc] (donde acaban las entradas) a [x1] (el costado). Si la repisa tiene
     * lámpara ([xVela] ≥ 0), va primero la lámpara; si sobra sitio, otro grupo a su derecha.
     */
    fun DrawScope.grupo(k: Int, r: Rect, oc: Float, x1: Float, xVela: Float, g: Int, s: SitioArcontes) {
        val w = size.width
        val piso = r.bottom - w * ObraEstanteArcontes.PISO * 0.5f
        val h = r.height
        var x0 = oc + w * 0.02f
        val fin = x1 - w * ObraEstanteArcontes.CANTO - w * 0.01f
        if (xVela >= 0f) {
            lamparaHueco(xVela, piso, r, s)
            x0 = xVela + w * 0.14f
        }
        if (fin - x0 < w * 0.1f) return
        val ancho = fin - x0
        // Uno o dos grupos, según el sitio.
        if (ancho > w * 0.46f) {
            dibujar(g, x0, x0 + ancho * 0.48f, piso, h, k, s)
            dibujar((g + 2) % GRUPOS, x0 + ancho * 0.52f, fin, piso, h, k + 7, s)
        } else dibujar(g, x0, fin, piso, h, k, s)
    }

    private fun DrawScope.dibujar(g: Int, x0: Float, x1: Float, piso: Float, h: Float, k: Int, s: SitioArcontes) {
        when (g) {
            0 -> cantaroConSal(x0, x1, piso, h, k, s)
            1 -> cestoDeHojas(x0, x1, piso, h, k, s)
            2 -> piedras(x0, x1, piso, h, k, s)
            else -> lana(x0, x1, piso, h, k, s)
        }
    }

    /** La sombra de lo que está en el hueco en la roca del fondo (de día, a la derecha; de noche, lejos de la llama). */
    private fun DrawScope.sombraFondo(forma: Path, t: TonoArcontes, hacia: Float) {
        val u = density
        translate(-hacia * 5f * u, -2f * u) { drawPath(forma, t.s(0.22f)) }
    }

    // ---------------------------------------------------------------- la lámpara del hueco

    /** La lámpara del hueco sobre su piedra, con la alcuza al lado: apagada de día, encendida de noche. Arde en [xv]. */
    private fun DrawScope.lamparaHueco(xv: Float, piso: Float, r: Rect, s: SitioArcontes) {
        val w = size.width; val u = density
        val lw = w * 0.1f
        val fuego = Offset(xv, r.bottom - r.height * MuebleEstanteArcontes.VELA_Y)
        val c = Offset(fuego.x - lw * 0.39f, fuego.y + lw * 0.264f)
        val tope = c.y + lw * 0.162f
        // La piedra donde está: un trozo de caliza de caras planas.
        val pi = Rect(c.x - lw * 0.42f, tope, c.x + lw * 0.5f, piso)
        val tp = s.t(pi.center)
        contactoArcontes(Offset(pi.center.x, piso), pi.width * 1.15f, w * 0.012f, tp, 0.45f)
        val cara = poligonoArcontes(pi.left, pi.top + 2f * u, pi.left + 3f * u, pi.top, pi.right - 2f * u, pi.top + 1f * u, pi.right, pi.bottom, pi.left - 1f * u, pi.bottom)
        drawPath(cara, s.aLoAncho(pi.left, pi.right, pi.center.y) { it.c(P.CALIZA, -0.04f) })
        clipPath(cara) {
            with(RocaEstanteArcontes) { textura(pi, if (s.a.oscuro) 0.6f else 1f, 91f) }
            drawRect(tp.s(0.3f), Offset(pi.right - pi.width * 0.25f, pi.top), Size(pi.width * 0.25f, pi.height))
        }
        drawRect(tp.b(0.5f), pi.topLeft, Size(pi.width, 1f * u))
        // La alcuza, detrás, a la derecha de la lámpara.
        with(CoronaEstanteArcontes) { alcuza(Offset(pi.right + w * 0.028f, piso), w * 0.075f, s) }
        val t = s.t(c)
        val enc = s.a.lumbre
        val barro = if (enc && s.a.oscuro) mezcla(t.c(P.BARRO_CLARO), P.LLAMA, 0.25f) else t.c(P.BARRO_CLARO)
        with(TrazosArcontes) { lampara(c, lw, barro, if (enc) (if (s.a.oscuro && !s.a.noche) 1f else 0.7f) else 0f) }
        if (!enc) drawCircle(P.TINTA.copy(alpha = 0.45f), lw * 0.05f, Offset(c.x + lw * 0.4f, c.y - lw * 0.36f * 0.36f))
    }

    /** El aire caliente alrededor de la llama del hueco, de noche (después de las tablas, para que tiña también sus cantos). */
    fun DrawScope.llamaHueco(xv: Float, r: Rect, s: SitioArcontes) {
        if (!s.a.lumbre) return
        val w = size.width
        val f = Offset(xv, r.bottom - r.height * MuebleEstanteArcontes.VELA_Y)
        val fuerza = if (s.a.oscuro && !s.a.noche) 1f else 0.5f
        charcoArcontes(f, w * 0.2f, P.LLAMA_HALO, 0.55f * fuerza)
        charcoArcontes(f, w * 0.08f, P.LLAMA_HALO, 0.9f * fuerza)
    }

    // ---------------------------------------------------------------- el cántaro con la sal

    /** Un cántaro de barro con su asa (de pie en [base], de alto [a]), con las marcas del torno y su filete. */
    private fun DrawScope.cantaro(base: Offset, a: Float, s: SitioArcontes, hacia: Float) {
        val u = density
        val t = s.t(base - Offset(0f, a * 0.5f))
        val x = base.x; val y = base.y
        val cuerpo = Path().apply {
            moveTo(x - a * 0.13f, y)
            cubicTo(x - a * 0.38f, y - a * 0.08f, x - a * 0.4f, y - a * 0.56f, x - a * 0.12f, y - a * 0.72f)
            lineTo(x - a * 0.1f, y - a * 0.9f); lineTo(x - a * 0.15f, y - a * 0.97f); lineTo(x + a * 0.15f, y - a * 0.97f); lineTo(x + a * 0.1f, y - a * 0.9f)
            lineTo(x + a * 0.12f, y - a * 0.72f)
            cubicTo(x + a * 0.4f, y - a * 0.56f, x + a * 0.38f, y - a * 0.08f, x + a * 0.13f, y)
            close()
        }
        sombraFondo(cuerpo, t, hacia)
        drawPath(Path().apply { moveTo(x + a * 0.1f, y - a * 0.88f); cubicTo(x + a * 0.36f, y - a * 0.92f, x + a * 0.38f, y - a * 0.62f, x + a * 0.25f, y - a * 0.55f) },
            t.c(P.BARRO, -0.25f), style = Stroke(a * 0.055f, cap = StrokeCap.Round))
        drawPath(cuerpo, Brush.horizontalGradient(listOf(t.c(P.BARRO_CLARO, if (hacia < 0f) 0.12f else -0.3f), t.c(P.BARRO_CLARO), t.c(P.BARRO, if (hacia < 0f) -0.35f else 0.1f)), x - a * 0.36f, x + a * 0.36f))
        for (k in 1..4) drawLine(t.s(0.12f), Offset(x - a * (0.34f - 0.02f * k), y - a * 0.12f * k), Offset(x + a * (0.34f - 0.02f * k), y - a * 0.12f * k), 0.5f * u)
        drawLine(t.c(P.BARRO_OSC).copy(alpha = 0.6f), Offset(x - a * 0.28f, y - a * 0.6f), Offset(x + a * 0.28f, y - a * 0.6f), 1f * u)
        drawOval(t.c(P.BARRO, -0.6f), Offset(x - a * 0.13f, y - a * 0.995f), Size(a * 0.26f, a * 0.06f))
        drawArc(t.b(0.4f), 195f, 55f, false, Offset(x - a * 0.34f, y - a * 0.62f), Size(a * 0.4f, a * 0.5f), style = Stroke(1.2f * u))
    }

    private fun DrawScope.cantaroConSal(x0: Float, x1: Float, piso: Float, h: Float, k: Int, s: SitioArcontes) {
        val w = size.width; val u = density
        val hacia = if (s.a.oscuro) 1f else -1f
        val a = h * (0.78f + 0.08f * azar(8001, k))
        val xc = x0 + minOf(x1 - x0, w * 0.3f) * 0.38f
        contactoArcontes(Offset(xc, piso), a * 0.7f, w * 0.012f, s.t(Offset(xc, piso)), 0.42f)
        cantaro(Offset(xc, piso), a, s, hacia)
        // El cuenco de sal delante, a la derecha, y la pesa de piedra.
        val cb = Offset(xc + a * 0.36f, piso)
        val r = h * 0.16f
        val t = s.t(cb)
        contactoArcontes(Offset(cb.x, piso), r * 2.3f, w * 0.01f, t, 0.4f)
        val boca = Rect(cb.x - r, cb.y - r * 0.9f, cb.x + r, cb.y - r * 0.48f)
        drawPath(Path().apply {
            moveTo(boca.left, boca.center.y); quadraticTo(cb.x - r * 0.95f, cb.y, cb.x, cb.y); quadraticTo(cb.x + r * 0.95f, cb.y, boca.right, boca.center.y); close()
        }, Brush.horizontalGradient(listOf(t.c(P.BARRO_CLARO, 0.08f), t.c(P.BARRO, -0.2f)), boca.left, boca.right))
        drawOval(t.c(P.BARRO, -0.5f), boca.topLeft, boca.size)
        drawPath(Path().apply {
            moveTo(boca.left + r * 0.12f, boca.center.y); quadraticTo(cb.x - r * 0.2f, boca.top - r * 0.5f, cb.x + r * 0.2f, boca.top - r * 0.35f)
            quadraticTo(cb.x + r * 0.62f, boca.top - r * 0.15f, boca.right - r * 0.1f, boca.center.y); close()
        }, t.c(P.SAL))
        drawArc(t.b(0.5f), 180f, 90f, false, boca.topLeft, boca.size, style = Stroke(0.8f * u))
        val pe = Offset(cb.x + r * 1.5f, piso)
        if (pe.x + r < x1) {
            val pr = Rect(pe.x - r * 0.55f, pe.y - r * 0.85f, pe.x + r * 0.55f, pe.y)
            val tp = s.t(pe)
            contactoArcontes(Offset(pe.x, piso), r * 1.4f, w * 0.008f, tp, 0.4f)
            drawRoundRect(tp.c(mezcla(P.CALIZA_SOMBRA, P.PLOMO, 0.4f)), pr.topLeft, pr.size, androidx.compose.ui.geometry.CornerRadius(r * 0.3f))
            drawRect(tp.b(0.4f), Offset(pr.left + r * 0.1f, pr.top), Size(pr.width * 0.6f, 1f * u))
            drawRect(tp.s(0.3f), Offset(pr.right - pr.width * 0.3f, pr.top), Size(pr.width * 0.3f, pr.height))
            drawCircle(tp.s(0.4f), 1.2f * u, Offset(pe.x, pr.top + 2f * u))
        }
    }

    // ---------------------------------------------------------------- el cesto de las hojas

    private fun DrawScope.cestoDeHojas(x0: Float, x1: Float, piso: Float, h: Float, k: Int, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = minOf(x1 - x0, w * 0.22f) * 0.78f
        val al = h * 0.5f
        val cx = x0 + an * 0.62f
        val t = s.t(Offset(cx, piso - al * 0.5f))
        // El haz de cálamos, apoyado contra la roca del fondo, detrás del cesto.
        val hx = cx + an * 0.5f
        for (j in 0 until 6) {
            val a0 = Offset(hx + j * 1.6f * u, piso)
            val a1 = Offset(hx - h * 0.26f + j * 2.2f * u, piso - h * (0.82f + 0.05f * azar(8011 + k, j)))
            drawLine(t.s(0.3f), a0 + Offset(2f * u, 0f), a1 + Offset(2f * u, 0f), 2f * u, StrokeCap.Round)
            drawLine(t.c(if (j % 2 == 0) P.JUNCO else mezcla(P.JUNCO, P.OLIVO_LUZ, 0.4f)), a0, a1, 1.8f * u, StrokeCap.Round)
        }
        val mitad = Offset(hx - h * 0.12f, piso - h * 0.42f)
        drawLine(t.c(P.CORDEL), mitad + Offset(-3f * u, -1f * u), mitad + Offset(9f * u, 1f * u), 1.6f * u, StrokeCap.Round)
        // Las hojas sueltas que asoman, cada una torcida a su modo.
        val boca = Rect(cx - an / 2, piso - al - an * 0.08f, cx + an / 2, piso - al + an * 0.08f)
        drawOval(t.c(P.JUNCO_OSC, -0.4f), boca.topLeft, boca.size)
        for (j in 0 until 5) {
            val x = boca.left + boca.width * (0.18f + 0.16f * j)
            val ha = h * (0.28f + 0.16f * azar(8021 + k, j))
            val hr = Rect(x - an * 0.14f, boca.center.y - ha, x + an * 0.14f, boca.center.y + al * 0.2f)
            rotate(-20f + 40f * azar(8022 + k, j), Offset(x, boca.center.y)) {
                val f = contornoIrregular(hr, 8023 + j + k, 22, 0.03f)
                drawPath(f, t.etiqueta(mezcla(P.PAPIRO, P.PAPIRO_OSC, 0.15f * (j % 3))))
                drawPath(f, t.c(P.FIBRA).copy(alpha = 0.5f), style = Stroke(0.6f * u))
                for (l in 0 until 3) drawLine(t.c(P.TINTA).copy(alpha = 0.32f), Offset(hr.left + hr.width * 0.2f, hr.top + hr.height * (0.14f + 0.08f * l)),
                    Offset(hr.left + hr.width * (0.45f + 0.35f * azar(8024 + k, j * 3 + l)), hr.top + hr.height * (0.14f + 0.08f * l)), 0.6f * u)
            }
        }
        // El cuerpo del cesto: se angosta hacia el pie, con las hileras del tejido.
        val cuerpo = Path().apply {
            moveTo(boca.left, boca.center.y); lineTo(boca.right, boca.center.y)
            lineTo(cx + an * 0.4f, piso); quadraticTo(cx, piso + an * 0.05f, cx - an * 0.4f, piso); close()
        }
        sombraFondo(cuerpo, t, if (s.a.oscuro) 1f else -1f)
        contactoArcontes(Offset(cx, piso), an * 1.05f, w * 0.012f, t, 0.42f)
        drawPath(cuerpo, Brush.horizontalGradient(listOf(t.c(P.JUNCO, 0.12f), t.c(P.JUNCO), t.c(P.JUNCO_OSC, -0.1f)), boca.left, boca.right))
        clipPath(cuerpo) {
            val sm = Path(); val lz = Path()
            var y = boca.center.y + 2f * u; var fila = 0
            while (y < piso + 4f * u && fila < 40) {
                var x = boca.left + (if (fila % 2 == 0) 0f else 2.5f * u)
                while (x < boca.right) {
                    sm.moveTo(x, y); sm.lineTo(x + 2.2f * u, y + 2.2f * u)
                    lz.moveTo(x + 2.5f * u, y + 2.2f * u); lz.lineTo(x + 4.7f * u, y)
                    x += 5f * u
                }
                y += 3.2f * u; fila++
            }
            drawPath(sm, t.s(0.28f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            drawPath(lz, t.b(0.3f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        }
        drawArc(t.c(P.JUNCO_OSC), 0f, 180f, false, boca.topLeft, boca.size, style = Stroke(3f * u))
        drawArc(t.b(0.45f), 0f, 180f, false, boca.topLeft, boca.size, style = Stroke(1.2f * u))
    }

    // ---------------------------------------------------------------- la jarra y el vaso de piedra

    private fun DrawScope.piedras(x0: Float, x1: Float, piso: Float, h: Float, k: Int, s: SitioArcontes) {
        val w = size.width; val u = density
        val hacia = if (s.a.oscuro) 1f else -1f
        // Las tablillas atadas, tumbadas, de base.
        val an = minOf(x1 - x0, w * 0.26f) * 0.8f
        val tb = Rect(x0 + w * 0.01f, piso - h * 0.13f, x0 + w * 0.01f + an, piso)
        val tt = s.t(tb.center)
        contactoArcontes(Offset(tb.center.x, piso), tb.width * 1.1f, w * 0.012f, tt, 0.42f)
        for (j in 0 until 4) {
            val r = Rect(tb.left + (azar(8031 + k, j) - 0.5f) * 3f * u, tb.top + tb.height * j / 4f, tb.right + (azar(8032 + k, j) - 0.5f) * 3f * u, tb.top + tb.height * (j + 1) / 4f)
            drawRect(tt.c(if (j % 2 == 0) P.OLIVO else mezcla(P.OLIVO, P.OLIVO_OSC, 0.35f)), r.topLeft, r.size)
            drawRect(tt.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
            drawRect(tt.c(P.CERA).copy(alpha = 0.5f), Offset(r.left + r.width * 0.05f, r.center.y - 0.4f * u), Size(r.width * 0.9f, 0.9f * u))
        }
        drawLine(tt.c(P.CORDEL), Offset(tb.left + an * 0.22f, tb.top - 1f * u), Offset(tb.left + an * 0.24f, tb.bottom), 1.5f * u)
        drawLine(tt.c(P.CORDEL), Offset(tb.left + an * 0.78f, tb.top - 1f * u), Offset(tb.left + an * 0.76f, tb.bottom), 1.5f * u)
        // La jarra de piedra, alta, labrada al torno, encima de las tablillas, a la izquierda.
        val ja = h * 0.66f
        val jb = Offset(tb.left + an * 0.32f, tb.top)
        val t = s.t(jb - Offset(0f, ja / 2))
        val jarra = Path().apply {
            val x = jb.x; val y = jb.y
            moveTo(x - ja * 0.14f, y)
            lineTo(x - ja * 0.2f, y - ja * 0.08f)
            cubicTo(x - ja * 0.34f, y - ja * 0.3f, x - ja * 0.32f, y - ja * 0.7f, x - ja * 0.22f, y - ja * 0.86f)
            lineTo(x - ja * 0.26f, y - ja * 0.92f); lineTo(x - ja * 0.26f, y - ja); lineTo(x + ja * 0.26f, y - ja); lineTo(x + ja * 0.26f, y - ja * 0.92f)
            lineTo(x + ja * 0.22f, y - ja * 0.86f)
            cubicTo(x + ja * 0.32f, y - ja * 0.7f, x + ja * 0.34f, y - ja * 0.3f, x + ja * 0.2f, y - ja * 0.08f)
            lineTo(x + ja * 0.14f, y); close()
        }
        sombraFondo(jarra, t, hacia)
        drawPath(jarra, Brush.horizontalGradient(listOf(t.c(P.CALIZA, if (hacia < 0f) 0.02f else -0.3f), t.c(mezcla(P.CALIZA, P.CALIZA_SOMBRA, 0.3f)), t.c(P.CALIZA_SOMBRA, if (hacia < 0f) -0.2f else 0.05f)),
            jb.x - ja * 0.32f, jb.x + ja * 0.32f))
        // Las molduras del torno, el labio y la boca honda.
        listOf(0.1f, 0.22f, 0.82f, 0.9f).forEach { f ->
            drawLine(t.s(0.3f), Offset(jb.x - ja * 0.3f, jb.y - ja * f), Offset(jb.x + ja * 0.3f, jb.y - ja * f), 0.8f * u)
            drawLine(t.b(0.4f), Offset(jb.x - ja * 0.3f, jb.y - ja * f - 1f * u), Offset(jb.x + ja * 0.3f, jb.y - ja * f - 1f * u), 0.5f * u)
        }
        drawOval(t.c(P.HUECO_HONDO, -0.3f), Offset(jb.x - ja * 0.22f, jb.y - ja * 1.03f), Size(ja * 0.44f, ja * 0.07f))
        drawRect(t.b(0.5f), Offset(jb.x - ja * 0.26f, jb.y - ja), Size(ja * 0.52f, 1f * u))
        // El vaso de piedra con su asa, a la derecha, en las tablillas.
        val va = h * 0.32f
        val vb = Offset(tb.left + an * 0.74f, tb.top)
        val tv = s.t(vb - Offset(0f, va / 2))
        val vaso = Rect(vb.x - va * 0.32f, vb.y - va, vb.x + va * 0.32f, vb.y)
        drawLine(tv.c(P.CALIZA_SOMBRA, -0.1f), Offset(vaso.right, vaso.top + va * 0.25f), Offset(vaso.right + va * 0.14f, vaso.top + va * 0.5f), va * 0.12f, StrokeCap.Round)
        drawRect(Brush.horizontalGradient(listOf(tv.c(P.CALIZA, 0.02f), tv.c(mezcla(P.CALIZA, P.CALIZA_SOMBRA, 0.3f)), tv.c(P.CALIZA_SOMBRA, -0.15f)), vaso.left, vaso.right), vaso.topLeft, vaso.size)
        // Las facetas del cincel: el vaso se labraba a mano, a golpes verticales.
        for (j in 1..4) drawLine(tv.s(0.18f), Offset(vaso.left + vaso.width * j / 5f, vaso.top + 2f * u), Offset(vaso.left + vaso.width * j / 5f, vaso.bottom), 0.7f * u)
        drawOval(tv.c(P.HUECO_HONDO, -0.2f), Offset(vaso.left + 1f * u, vaso.top - va * 0.05f), Size(vaso.width - 2f * u, va * 0.1f))
        drawRect(tv.b(0.5f), vaso.topLeft, Size(vaso.width, 0.9f * u))
    }

    // ---------------------------------------------------------------- la lana y el huso

    private fun DrawScope.lana(x0: Float, x1: Float, piso: Float, h: Float, k: Int, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = minOf(x1 - x0, w * 0.26f) * 0.86f
        // El manto doblado, de base: la lana con sus dos franjas oscuras (el del Ideario).
        val m = Rect(x0 + w * 0.008f, piso - h * 0.2f, x0 + w * 0.008f + an, piso)
        val tm = s.t(m.center)
        contactoArcontes(Offset(m.center.x, piso), m.width * 1.08f, w * 0.012f, tm, 0.42f)
        for (j in 0 until 3) {
            val r = Rect(m.left + j * 1.6f * u, m.top + m.height * j / 3f, m.right - j * 1.2f * u, m.top + m.height * (j + 1) / 3f + 1f)
            drawRoundRect(tm.c(if (j == 1) mezcla(P.LANA, P.LANA_CRUDA, 0.3f) else P.LANA, if (j == 0) 0.06f else 0f), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(r.height * 0.45f))
            drawLine(tm.s(0.3f), Offset(r.left + r.height * 0.4f, r.bottom - 0.6f * u), Offset(r.right - r.height * 0.4f, r.bottom - 0.6f * u), 0.9f * u)
            listOf(0.26f, 0.7f).forEach { f -> drawLine(tm.c(P.FRANJA), Offset(r.left + r.width * f, r.top + 0.5f * u), Offset(r.left + r.width * f, r.bottom - 0.5f * u), 2f * u) }
        }
        drawRect(tm.b(0.35f), Offset(m.left + 3f * u, m.top), Size(m.width - 6f * u, 0.9f * u))
        // El cesto bajo de la lana, encima del manto: los ovillos que asoman y el huso clavado.
        val cx = m.left + an * 0.5f
        val ca = h * 0.32f; val cb = m.top
        val boca = Rect(cx - an * 0.36f, cb - ca - an * 0.06f, cx + an * 0.36f, cb - ca + an * 0.06f)
        val t = s.t(Offset(cx, cb - ca * 0.5f))
        // El huso: el palo, la tortera de barro y la hebra que sube.
        val h0 = Offset(cx + an * 0.12f, cb - ca * 0.4f); val h1 = Offset(cx + an * 0.34f, cb - h * 0.78f)
        drawLine(t.s(0.3f), h0 + Offset(1.5f * u, 0f), h1 + Offset(1.5f * u, 0f), 2.2f * u, StrokeCap.Round)
        drawLine(t.c(P.OLIVO_LUZ), h0, h1, 1.8f * u, StrokeCap.Round)
        val tor = h0 + (h1 - h0) * 0.62f
        drawOval(t.c(P.BARRO_CLARO), Offset(tor.x - 5f * u, tor.y - 1.8f * u), Size(10f * u, 3.6f * u))
        drawOval(t.b(0.4f), Offset(tor.x - 4f * u, tor.y - 1.6f * u), Size(6f * u, 1.4f * u))
        val hebra = h0 + (h1 - h0) * 0.85f
        drawOval(t.c(P.LANA_CRUDA), Offset(hebra.x - 3.4f * u, hebra.y - 5f * u), Size(6.8f * u, 10f * u))
        // Los ovillos: uno teñido de añil, otro de gualda, otro crudo.
        listOf(Color(0xFF2F5E6E), Color(0xFFC79534), P.LANA_CRUDA).forEachIndexed { j, col ->
            val c = Offset(boca.left + boca.width * (0.25f + 0.25f * j), boca.center.y - ca * 0.12f)
            val r = ca * (0.26f + 0.04f * j)
            drawCircle(t.c(col), r, c)
            drawArc(t.s(0.3f), -30f, 150f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(r * 0.35f))
            for (l in 0 until 3) drawArc(t.c(col, -0.25f), 200f + l * 25f, 80f, false, Offset(c.x - r * (0.8f - 0.2f * l), c.y - r * (0.8f - 0.2f * l)),
                Size(r * (1.6f - 0.4f * l), r * (1.6f - 0.4f * l)), style = Stroke(0.6f * u))
            drawCircle(t.b(0.4f), r * 0.25f, c - Offset(r * 0.35f, r * 0.35f))
        }
        val cuerpo = Path().apply {
            moveTo(boca.left, boca.center.y); lineTo(boca.right, boca.center.y)
            lineTo(cx + an * 0.3f, cb); lineTo(cx - an * 0.3f, cb); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(t.c(P.JUNCO, 0.1f), t.c(P.JUNCO_OSC, -0.1f)), boca.left, boca.right))
        clipPath(cuerpo) {
            var y = boca.center.y + 2f * u; var j = 0
            while (y < cb && j < 20) { drawLine(t.s(0.25f), Offset(boca.left, y), Offset(boca.right, y), 0.8f * u); y += 2.8f * u; j++ }
        }
        drawArc(t.c(P.JUNCO_OSC), 0f, 180f, false, boca.topLeft, boca.size, style = Stroke(2.4f * u))
        drawArc(t.b(0.4f), 0f, 180f, false, boca.topLeft, boca.size, style = Stroke(0.9f * u))
    }
}
