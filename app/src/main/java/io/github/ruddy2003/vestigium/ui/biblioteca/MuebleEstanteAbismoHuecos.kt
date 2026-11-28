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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que acompaña en los huecos libres de las repisas de Abismo ([MuebleEstanteAbismo]): grupos de dos o tres cosas del pañol,
 * solapadas, del 70 al 90 % del alto del hueco, que cambian de un hueco a otro: el farolillo de vela en su cardán (de noche, la luz
 * de media altura), las pizarras de buceo atadas con su lápiz y la piola, el catalejo de pie con la brújula de bote en su caja, la
 * esponja de copa con el cubo de lona de la cosecha, el caracol y el erizo, el molinillo de café de latón con la lata de café y la
 * taza, la botella de ron con el cabo adujado en la tabla y el vaso de lata (las piezas nuevas, en [PiezasHuecoAbismo]). Cada cosa con su
 * luz, su sombra en el fondo y su sombra en la tabla.
 */
internal object HuecosEstanteAbismo {
    private val P = PaletaAbismo

    /** El hueco libre de la repisa [k] en [r], de [oc] (donde acaban las entradas) al costado [der]; [xc], el farolillo o -1. */
    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, der: Float, xc: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width
        val x0 = oc + w * 0.02f; val x1 = der - w * 0.02f
        if (x1 - x0 < w * 0.07f) return
        val h = r.height
        var fin = x1
        if (xc >= 0f) {
            farolillo(Offset(xc, r.bottom), h, a, cerca)
            fin = xc - w * 0.075f
        }
        reparto(k, oc, der, xc, w).forEach { (g, a0, a1) -> grupoEn(g, a0, a1, r, a, cerca) }
    }

    /**
     * Los grupos del hueco libre de la repisa [k] (cuál y entre qué x): cinco que se turnan de una repisa a otra; en un hueco
     * muy ancho, dos o tres distintos. Lo usan el hueco y lo que cuelga por el canto.
     */
    private fun reparto(k: Int, oc: Float, der: Float, xc: Float, w: Float): List<Triple<Int, Float, Float>> {
        val x0 = oc + w * 0.02f; val x1 = der - w * 0.02f
        if (x1 - x0 < w * 0.07f) return emptyList()
        val fin = if (xc >= 0f) xc - w * 0.075f else x1
        val ancho = fin - x0
        if (ancho < w * 0.08f) return emptyList()
        val grupo = Math.floorMod(k * 2 + 1 + k / 5, 5)
        val l = ArrayList<Triple<Int, Float, Float>>()
        l.add(Triple(grupo, x0, if (ancho > w * 0.46f) x0 + w * 0.3f else fin))
        if (ancho > w * 0.46f) l.add(Triple(Math.floorMod(grupo + 2, 5), x0 + w * 0.31f, minOf(fin, x0 + w * 0.6f)))
        if (ancho > w * 0.62f) l.add(Triple(Math.floorMod(grupo + 4, 5), x0 + w * 0.58f, minOf(fin, x0 + w * 0.86f)))
        return l
    }

    /** Lo que cuelga por el canto de la tabla (se pinta después de las tablas): el chicote del cabo adujado con su guardacabo. */
    fun DrawScope.colgantes(k: Int, r: Rect, oc: Float, der: Float, xc: Float, a: AireAbismo, cerca: (Offset) -> Float, placas: List<Rect>) {
        val w = size.width
        reparto(k, oc, der, xc, w).forEach { (g, a0, a1) ->
            if (g != 4) return@forEach
            val p = Offset((a0 + a1) / 2 - w * 0.065f, r.bottom)
            // No cuelga encima de la placa del mes.
            val x = p.x + w * 0.023f
            if (placas.any { pl -> x > pl.left - w * 0.03f && x < pl.right + w * 0.03f && pl.top < r.bottom + w * 0.06f && pl.bottom > r.bottom }) return@forEach
            with(PiezasHuecoAbismo) { chicote(p, r.height, a, cerca) }
        }
    }

    private fun DrawScope.grupoEn(g: Int, x0: Float, x1: Float, r: Rect, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width
        val h = r.height
        val cx = (x0 + x1) / 2
        val y = r.bottom
        when (g) {
            0 -> { pizarras(Offset(cx - w * 0.01f, y), h, a, cerca); piola(Offset(minOf(x1 - w * 0.03f, cx + w * 0.07f), y), h, a, cerca) }
            1 -> { catalejo(Offset(cx - w * 0.06f, y), h, a, cerca); brujula(Offset(cx + w * 0.02f, y), h, a, cerca) }
            2 -> {
                esponjaCopa(Offset(cx - w * 0.045f, y), h, a, cerca)
                with(PiezasHuecoAbismo) { cuboLona(Offset(cx + w * 0.055f, y), h, a, cerca) }
                caracol(Offset(cx - w * 0.005f, y), h, a, cerca)
            }
            3 -> {
                molinillo(Offset(cx - w * 0.07f, y), h, a, cerca)
                with(PiezasHuecoAbismo) { lataCafe(Offset(cx + w * 0.005f, y), h, a, cerca) }
                taza(Offset(cx + w * 0.07f, y), h, a, cerca)
            }
            else -> {
                botella(Offset(cx + w * 0.02f, y), h, a, cerca)
                with(PiezasHuecoAbismo) { rolloTumbado(Offset(cx - w * 0.065f, y), h, a, cerca) }
            }
        }
    }

    /** De qué lado le llega la luz a algo en [p]: de día, de la izquierda (1 = la luz a la izquierda, sombra a la derecha). */
    private fun lado(a: AireAbismo, p: Offset, w: Float): Float = if (!a.oscuro) 1f else if (p.x / w < MuebleEstanteAbismo.FAROL_X) -1f else 1f

    /** La sombra de algo de pie en el fondo del hueco (corrida a la derecha y arriba) y en la tabla. */
    private fun DrawScope.sombras(pie: Offset, ancho: Float, alto: Float, a: AireAbismo, k: Float) {
        val w = size.width
        val dx = if (a.oscuro) 0f else w * 0.014f
        if (!a.oscuro || k > 0.2f) drawRect(a.sombra(if (a.oscuro) 0.25f * k else 0.16f), Offset(pie.x - ancho * 0.4f + dx, pie.y - alto * 0.92f), Size(ancho * 0.85f, alto * 0.92f))
        contactoAbismo(Offset(pie.x + w * 0.006f, pie.y - 1f), ancho * 1.25f, w * 0.014f, a, 0.4f)
    }

    // ---------------------------------------------------------------- el farolillo de cardán

    /**
     * El farolillo de vela en su cardán, atornillado al fondo del hueco: la escuadra de latón, la horquilla, el aro que bascula, el
     * vaso con la vela y su tubo de vidrio con el sombrerete. De día, apagado; de noche, la vela arde (su luz, en [llamaHueco]).
     */
    private fun DrawScope.farolillo(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val ly = pie.y - h * MuebleEstanteAbismo.CANDIL_Y
        val k = if (a.lumbre) 1f else cerca(Offset(pie.x, ly))
        fun c(col: Color, f: Float = 0f) = a.dia(col, f, k)
        // La escuadra en el fondo y su pie en la tabla.
        val esc = Rect(pie.x - w * 0.008f, pie.y - h * 0.86f, pie.x + w * 0.008f, pie.y)
        drawRect(a.sombra(0.3f), Offset(esc.left + 2f * u, esc.top + 2f * u), esc.size)
        drawRect(c(P.LATON_OSC), esc.topLeft, esc.size)
        drawRect(a.brillo(0.4f, k), esc.topLeft, Size(1f * u, esc.height))
        clavoAbismo(Offset(esc.center.x, esc.top + w * 0.01f), 1.2f * u, a, k)
        clavoAbismo(Offset(esc.center.x, esc.bottom - w * 0.012f), 1.2f * u, a, k)
        // La horquilla: un brazo que sale del fondo y abraza el aro.
        val hy = ly - h * 0.12f
        drawLine(c(P.LATON), Offset(esc.center.x, hy), Offset(esc.center.x, hy - h * 0.02f), 2f * u)
        val aro = Rect(pie.x - w * 0.034f, hy - w * 0.008f, pie.x + w * 0.034f, hy + w * 0.008f)
        drawArc(c(P.LATON), 0f, 180f, false, Offset(aro.left - w * 0.006f, hy - h * 0.06f), Size(aro.width + w * 0.012f, h * 0.12f), style = Stroke(2f * u))
        // El vaso de latón con su lastre abajo.
        val vaso = Rect(pie.x - w * 0.022f, hy, pie.x + w * 0.022f, hy + h * 0.2f)
        drawRoundRect(c(P.LATON), vaso.topLeft, vaso.size, CornerRadius(w * 0.01f))
        clipRect(vaso.left, vaso.top, vaso.right, vaso.bottom) { cilindroAbismo(vaso, a, k, if (a.oscuro) -1f else -1f, 1.3f) }
        drawOval(c(P.LATON_OSC), Offset(vaso.left - w * 0.004f, vaso.bottom - w * 0.012f), Size(vaso.width + w * 0.008f, w * 0.016f))
        // El aro que bascula, con sus pivotes.
        drawOval(c(P.LATON), aro.topLeft, aro.size, style = Stroke(1.8f * u))
        listOf(aro.left, aro.right).forEach { x -> clavoAbismo(Offset(x, hy), 1.4f * u, a, k) }
        // El tubo de vidrio con la vela dentro y el sombrerete.
        val tubo = Rect(pie.x - w * 0.018f, hy - h * 0.3f, pie.x + w * 0.018f, hy)
        val vela = Rect(pie.x - w * 0.007f, hy - h * 0.14f, pie.x + w * 0.007f, hy)
        drawRect(c(Color(0xFFF1EBDA)), vela.topLeft, vela.size)
        drawRect(a.sombra(0.2f), Offset(vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height))
        drawLine(c(P.TINTA), Offset(pie.x, vela.top), Offset(pie.x, vela.top - w * 0.006f), 1f * u)
        if (a.lumbre) drawRect(P.LLAMA.copy(alpha = 0.22f), tubo.topLeft, tubo.size)
        else drawRect(mezcla(P.MAMPARO, P.VIDRIO, 0.15f).copy(alpha = 0.35f), tubo.topLeft, tubo.size)
        drawRect(if (a.lumbre) P.LLAMA.copy(alpha = 0.5f) else luzMueble(0.6f), Offset(tubo.left + w * 0.004f, tubo.top + w * 0.006f), Size(1.4f * u, tubo.height * 0.7f))
        drawRect(a.sombra(0.3f), tubo.topLeft, tubo.size, style = Stroke(0.8f * u))
        val so = poligonoAbismo(tubo.left - w * 0.006f, tubo.top, tubo.right + w * 0.006f, tubo.top, tubo.right - w * 0.004f, tubo.top - w * 0.014f, tubo.left + w * 0.004f, tubo.top - w * 0.014f)
        drawPath(so, c(P.LATON))
        drawRect(c(P.LATON_OSC), Offset(pie.x - w * 0.005f, tubo.top - w * 0.024f), Size(w * 0.01f, w * 0.01f))
        if (!a.lumbre) destelloAbismo(Offset(vaso.left + vaso.width * 0.25f, vaso.top + vaso.height * 0.3f), 1.1f * u, a, k)
    }

    /** La llama del farolillo de la repisa [r] en [x] y su luz encima de lo cercano (de noche; en los Sueños, más baja). */
    fun DrawScope.llamaHueco(x: Float, r: Rect, a: AireAbismo) {
        if (!a.lumbre) return
        val w = size.width
        val hy = r.bottom - r.height * MuebleEstanteAbismo.CANDIL_Y - r.height * 0.12f
        val l = Offset(x, hy - r.height * 0.155f)
        val f = if (a.noche) 0.6f else 1f
        if (a.oscuro) {
            mancha(l, w * 0.24f, P.LLAMA.copy(alpha = 0.16f * f))
            mancha(l, w * 0.07f, Color(0xFFFFE2A8).copy(alpha = 0.32f * f))
        } else mancha(l, w * 0.08f, P.LLAMA.copy(alpha = 0.18f))
        val e = if (a.oscuro && a.noche) 0.75f else 1f
        drawOval(Color(0xFFFFB24A), Offset(l.x - w * 0.006f * e, l.y - w * 0.014f * e), Size(w * 0.012f * e, w * 0.022f * e))
        drawOval(P.FUEGO, Offset(l.x - w * 0.003f * e, l.y - w * 0.008f * e), Size(w * 0.006f * e, w * 0.013f * e))
    }

    // ---------------------------------------------------------------- las pizarras, la piola

    /** Tres pizarras de buceo de pie, desparejas y atadas con su cordón (las fichas del Ideario), con el lápiz colgando. */
    private fun DrawScope.pizarras(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.4f))
        val hacia = lado(a, pie, w)
        sombras(pie, w * 0.1f, h * 0.8f, a, k)
        listOf(Triple(-0.02f, 0.8f, -4f), Triple(0.012f, 0.72f, 3f), Triple(0.03f, 0.62f, 8f)).forEachIndexed { j, (dx, alto, giro) ->
            val sw = w * 0.085f; val sh = h * alto
            val r = Rect(pie.x + w * dx - sw / 2, pie.y - sh, pie.x + w * dx + sw / 2, pie.y)
            rotate(giro, Offset(r.left, r.bottom)) {
                drawRoundRect(a.sombra(0.3f), Offset(r.left + 2f * u, r.top + 2f * u), r.size, CornerRadius(w * 0.008f))
                drawRoundRect(a.dia(if (j == 1) Color(0xFFC9C9BE) else Color(0xFFD9D8CD), 0f, k), r.topLeft, r.size, CornerRadius(w * 0.008f))
                drawRect(a.sombra(0.12f), Offset(if (hacia > 0) r.right - r.width * 0.25f else r.left, r.top), Size(r.width * 0.25f, r.height))
                drawRoundRect(a.sombra(0.3f), r.topLeft, r.size, CornerRadius(w * 0.008f), style = Stroke(0.7f * u))
                // Lo escrito a lápiz: renglones grises que no se leen, y el agujero del cordón.
                for (i in 0 until 4) drawLine(a.dia(P.PLOMO, 0f, k).copy(alpha = 0.45f), Offset(r.left + r.width * 0.18f, r.top + r.height * (0.25f + 0.1f * i)), Offset(r.left + r.width * (0.5f + 0.3f * azar(8400 + j, i)), r.top + r.height * (0.25f + 0.1f * i)), 0.8f * u)
                drawCircle(a.sombra(0.6f), w * 0.005f, Offset(r.center.x, r.top + w * 0.012f))
                drawRect(a.brillo(0.5f, k), r.topLeft, Size(r.width, 1f * u))
            }
        }
        // El cordón que las ata por los agujeros y el lápiz que cuelga.
        val nudo = Offset(pie.x + w * 0.004f, pie.y - h * 0.74f)
        drawLine(a.dia(P.AMARILLO_CORDON, 0f, k), Offset(pie.x - w * 0.03f, pie.y - h * 0.78f), nudo, 1.2f * u)
        drawLine(a.dia(P.AMARILLO_CORDON, 0f, k), nudo, Offset(pie.x + w * 0.04f, pie.y - h * 0.6f), 1.2f * u)
        val lp = Offset(pie.x + w * 0.05f, pie.y - h * 0.36f)
        drawLine(a.dia(P.AMARILLO_CORDON, 0f, k), Offset(pie.x + w * 0.04f, pie.y - h * 0.6f), lp, 1f * u)
        rotate(12f, lp) {
            drawRect(a.dia(Color(0xFF8E5A2C), 0f, k), Offset(lp.x - w * 0.004f, lp.y), Size(w * 0.008f, h * 0.22f))
            drawRect(a.dia(P.PLOMO, 0f, k), Offset(lp.x - w * 0.004f, lp.y + h * 0.22f), Size(w * 0.008f, h * 0.025f))
        }
    }

    /** La madeja de piola (el cordel de las ataduras), adujada en rollo bajo, con su chicote. */
    private fun DrawScope.piola(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.1f))
        val rx = w * 0.045f; val alto = h * 0.24f
        contactoAbismo(Offset(pie.x + w * 0.006f, pie.y - 1f), rx * 2.4f, w * 0.014f, a, 0.42f)
        val cuerpo = Rect(pie.x - rx, pie.y - alto, pie.x + rx, pie.y - w * 0.005f)
        drawRoundRect(a.dia(P.ESPARTO, -0.05f, k), cuerpo.topLeft, cuerpo.size, CornerRadius(rx * 0.5f))
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            var y = cuerpo.top + 1.5f * u
            while (y < cuerpo.bottom) { drawLine(a.dia(P.MANILA_OSC, 0f, k).copy(alpha = 0.6f), Offset(cuerpo.left, y), Offset(cuerpo.right, y + 2f * u), 0.7f * u); y += 2.2f * u }
            cilindroAbismo(cuerpo, a, k, lado(a, pie, w) * -1f, 1.2f)
        }
        drawOval(a.dia(P.ESPARTO, 0.1f, k), Offset(cuerpo.left, cuerpo.top - w * 0.01f), Size(cuerpo.width, w * 0.02f))
        drawOval(a.sombra(0.5f), Offset(pie.x - rx * 0.35f, cuerpo.top - w * 0.006f), Size(rx * 0.7f, w * 0.012f))
        val ch = Path().apply { moveTo(cuerpo.right - w * 0.005f, cuerpo.top + w * 0.01f); cubicTo(cuerpo.right + w * 0.02f, cuerpo.top + w * 0.02f, cuerpo.right + w * 0.01f, pie.y - w * 0.01f, cuerpo.right + w * 0.03f, pie.y - w * 0.004f) }
        drawPath(ch, a.dia(P.MANILA, 0f, k), style = Stroke(1.6f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- el catalejo, la brújula

    /** El catalejo de pie: los tubos de latón que se meten uno en otro, el forro de cuero cosido y la tapa del objetivo. */
    private fun DrawScope.catalejo(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.4f))
        val hacia = lado(a, pie, w)
        val alto = h * 0.84f
        sombras(pie, w * 0.05f, alto, a, k)
        val tramos = listOf(Triple(0f, 0.45f, w * 0.042f), Triple(0.45f, 0.75f, w * 0.034f), Triple(0.75f, 1f, w * 0.028f))
        rotate(-3f, pie) {
            tramos.forEachIndexed { j, (f0, f1, an) ->
                val r = Rect(pie.x - an / 2, pie.y - alto * f1, pie.x + an / 2, pie.y - alto * f0)
                if (j == 0) {
                    drawRect(a.dia(P.CUERO, 0f, k), r.topLeft, r.size)
                    for (i in 0 until 6) drawLine(a.dia(P.MANILA, 0f, k).copy(alpha = 0.6f), Offset(r.left, r.top + r.height * (0.15f + 0.14f * i)), Offset(r.right, r.top + r.height * (0.15f + 0.14f * i) + 1.5f * u), 0.7f * u)
                } else drawRect(a.dia(P.LATON, 0f, k), r.topLeft, r.size)
                clipRect(r.left, r.top, r.right, r.bottom) { cilindroAbismo(r, a, k, -hacia, 1.2f) }
                drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(r.left - w * 0.003f, r.top), Size(r.width + w * 0.006f, w * 0.008f))
                drawRect(a.brillo(0.5f, k), Offset(r.left - w * 0.003f, r.top), Size(r.width + w * 0.006f, 1f * u))
            }
            val base = Rect(pie.x - w * 0.024f, pie.y - w * 0.012f, pie.x + w * 0.024f, pie.y)
            drawRect(a.dia(P.LATON_OSC, 0f, k), base.topLeft, base.size)
            destelloAbismo(Offset(pie.x - hacia * w * 0.008f, pie.y - alto * 0.85f), 1.2f * u, a, k)
        }
    }

    /**
     * La brújula de bote en su caja de teca con la tapa abierta hacia atrás: la caja, el aro de latón, la rosa sin letras con su
     * aguja de dos puntas y el vidrio con su reflejo.
     */
    private fun DrawScope.brujula(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.15f))
        val bw = w * 0.13f; val bh = h * 0.3f
        val caja = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
        // La tapa, abierta y apoyada en el fondo.
        val tapa = poligonoAbismo(caja.left + w * 0.006f, caja.top, caja.right - w * 0.006f, caja.top, caja.right - w * 0.014f, caja.top - h * 0.44f, caja.left + w * 0.014f, caja.top - h * 0.44f)
        drawPath(corridoAbismo(tapa, w * 0.012f, w * 0.004f), a.sombra(0.25f))
        drawPath(tapa, a.dia(P.TECA, -0.05f, k))
        drawPath(poligonoAbismo(caja.left + w * 0.016f, caja.top - h * 0.03f, caja.right - w * 0.016f, caja.top - h * 0.03f, caja.right - w * 0.022f, caja.top - h * 0.41f, caja.left + w * 0.022f, caja.top - h * 0.41f), a.dia(Color(0xFF3E5C50), 0f, k))
        contactoAbismo(Offset(pie.x + w * 0.006f, pie.y - 1f), bw * 1.3f, w * 0.014f, a, 0.42f)
        drawRect(a.dia(P.TECA, 0f, k), caja.topLeft, caja.size)
        drawRect(a.brillo(0.45f, k), caja.topLeft, Size(caja.width, 1.2f * u))
        drawRect(a.sombra(0.3f), Offset(caja.right - caja.width * 0.12f, caja.top), Size(caja.width * 0.12f, caja.height))
        listOf(caja.left, caja.right - w * 0.016f).forEach { x -> drawRect(a.dia(P.LATON, 0f, k), Offset(x, caja.top), Size(w * 0.016f, w * 0.01f)) }
        // La rosa, vista desde arriba y delante: un óvalo sobre la caja.
        val rc = Offset(pie.x, caja.top + w * 0.002f)
        val rr = bw * 0.38f
        drawOval(a.dia(P.LATON, 0f, k), Offset(rc.x - rr, rc.y - rr * 0.36f), Size(rr * 2, rr * 0.72f))
        drawOval(a.dia(mezcla(P.PAPEL, Color.White, 0.2f), 0f, k), Offset(rc.x - rr * 0.82f, rc.y - rr * 0.3f), Size(rr * 1.64f, rr * 0.6f))
        drawOval(a.dia(P.TINTA, 0f, k).copy(alpha = 0.4f), Offset(rc.x - rr * 0.65f, rc.y - rr * 0.23f), Size(rr * 1.3f, rr * 0.46f), style = Stroke(0.6f * u))
        val ag = poligonoAbismo(rc.x - rr * 0.7f, rc.y + rr * 0.06f, rc.x, rc.y - rr * 0.05f, rc.x + rr * 0.7f, rc.y - rr * 0.06f, rc.x, rc.y + rr * 0.05f)
        drawPath(ag, a.dia(P.MINIO, 0f, k))
        drawPath(poligonoAbismo(rc.x - rr * 0.7f, rc.y + rr * 0.06f, rc.x, rc.y - rr * 0.05f, rc.x, rc.y + rr * 0.05f), a.dia(P.TINTA, 0f, k))
        drawArc(if (a.oscuro) P.LLAMA.copy(alpha = 0.5f * k) else luzMueble(0.6f), 190f, 70f, false, Offset(rc.x - rr * 0.82f, rc.y - rr * 0.3f), Size(rr * 1.64f, rr * 0.6f), style = Stroke(1.2f * u))
    }

    // ---------------------------------------------------------------- la esponja de copa, el caracol

    /**
     * La esponja de copa, seca, de pie: un vaso ancho arriba y estrecho abajo, de borde abultado e irregular, ocre, con su
     * poro fino, las bocas (los ósculos) hondas con su borde claro y la boca grande de arriba.
     */
    private fun DrawScope.esponjaCopa(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.4f))
        val alto = h * 0.84f; val an = w * 0.14f
        sombras(pie, an, alto, a, k)
        // El contorno: el perfil de la copa con bultos irregulares a los dos lados.
        val n = 14
        fun ancho(t: Float) = an * (0.2f + 0.32f * t * t * (3f - 2f * t))
        val forma = Path().apply {
            moveTo(pie.x - an * 0.2f, pie.y)
            for (i in 1..n) {
                val t = i / n.toFloat()
                val b = 1f + 0.12f * (azar(8413, i) - 0.5f) * 2f
                val y = pie.y - alto * t
                val ym = pie.y - alto * (t - 0.5f / n)
                quadraticTo(pie.x - ancho(t - 0.5f / n) * b * 1.12f, ym, pie.x - ancho(t) * b, y)
            }
            // La boca de arriba, abultada.
            cubicTo(pie.x - an * 0.3f, pie.y - alto * 1.05f, pie.x + an * 0.25f, pie.y - alto * 1.04f, pie.x + ancho(1f), pie.y - alto * 0.98f)
            for (i in n - 1 downTo 0) {
                val t = i / n.toFloat()
                val b = 1f + 0.12f * (azar(8414, i) - 0.5f) * 2f
                val y = pie.y - alto * t
                val ym = pie.y - alto * (t + 0.5f / n)
                quadraticTo(pie.x + ancho(t + 0.5f / n) * b * 1.12f, ym, pie.x + ancho(t) * b, y)
            }
            close()
        }
        val col = mezcla(P.ESPONJA, P.ESPARTO, 0.3f)
        drawPath(forma, a.dia(col, 0f, k))
        clipPath(forma) {
            val sx = lado(a, pie, w)
            drawRect(a.sombra(0.3f), Offset(if (sx > 0) pie.x + an * 0.12f else pie.x - an * 0.7f, pie.y - alto * 1.1f), Size(an * 0.6f, alto * 1.1f))
            drawRect(a.brillo(0.25f, k), Offset(if (sx > 0) pie.x - an * 0.5f else pie.x + an * 0.22f, pie.y - alto * 1.1f), Size(an * 0.24f, alto * 1.1f))
            // El poro fino.
            for (j in 0 until 46) {
                val p = Offset(pie.x + an * (azar(8410, j) - 0.5f) * 1.1f, pie.y - alto * azar(8411, j))
                drawCircle(a.dia(mezcla(col, P.TINTA, 0.5f), 0f, k).copy(alpha = 0.55f), (0.6f + 0.9f * azar(8412, j)) * u, p)
            }
            // Las bocas: hondas, con su borde claro arriba.
            for (j in 0 until 6) {
                val t = 0.18f + 0.68f * azar(8415, j)
                val p = Offset(pie.x + ancho(t) * (azar(8416, j) - 0.5f) * 1.3f, pie.y - alto * t)
                val rr = an * (0.06f + 0.05f * azar(8417, j))
                drawOval(a.dia(P.ESPONJA_OSC, -0.3f, k), Offset(p.x - rr, p.y - rr * 0.75f), Size(rr * 2, rr * 1.5f))
                drawArc(a.brillo(0.45f, k), 200f, 140f, false, Offset(p.x - rr * 1.1f, p.y - rr * 0.9f), Size(rr * 2.2f, rr * 1.7f), style = Stroke(1f * u))
            }
        }
        // La boca grande de arriba.
        drawOval(a.dia(P.ESPONJA_OSC, -0.25f, k), Offset(pie.x - an * 0.38f, pie.y - alto * 1.02f), Size(an * 0.8f, alto * 0.08f))
        drawArc(a.brillo(0.4f, k), 180f, 180f, false, Offset(pie.x - an * 0.45f, pie.y - alto * 1.04f), Size(an * 0.94f, alto * 0.1f), style = Stroke(1.2f * u))
    }

    /** La botella de ron de vidrio oscuro con su corcho y el vaso de lata abollado delante. */
    private fun DrawScope.botella(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.35f))
        val hacia = lado(a, pie, w)
        val alto = h * 0.88f; val an = w * 0.07f
        sombras(pie, an, alto, a, k)
        val cuerpo = Path().apply {
            moveTo(pie.x - an / 2, pie.y)
            lineTo(pie.x - an / 2, pie.y - alto * 0.58f)
            cubicTo(pie.x - an / 2, pie.y - alto * 0.7f, pie.x - an * 0.16f, pie.y - alto * 0.72f, pie.x - an * 0.16f, pie.y - alto * 0.8f)
            lineTo(pie.x - an * 0.16f, pie.y - alto * 0.96f)
            lineTo(pie.x + an * 0.16f, pie.y - alto * 0.96f)
            lineTo(pie.x + an * 0.16f, pie.y - alto * 0.8f)
            cubicTo(pie.x + an * 0.16f, pie.y - alto * 0.72f, pie.x + an / 2, pie.y - alto * 0.7f, pie.x + an / 2, pie.y - alto * 0.58f)
            lineTo(pie.x + an / 2, pie.y)
            close()
        }
        drawPath(cuerpo, a.dia(Color(0xFF2E3A24), 0f, k))
        clipPath(cuerpo) {
            // Lo que queda de ron, ámbar, y el reflejo del vidrio.
            drawRect(a.dia(Color(0xFF6A3A14), 0f, k), Offset(pie.x - an / 2, pie.y - alto * 0.3f), Size(an, alto * 0.3f))
            cilindroAbismo(Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y), a, k, -hacia, 1.4f)
            drawRect(if (a.oscuro) P.LLAMA.copy(alpha = (0.7f * k).coerceAtMost(0.7f)) else luzMueble(0.75f), Offset(pie.x + hacia * an * 0.22f - 0.8f * u, pie.y - alto * 0.62f), Size(1.6f * u, alto * 0.5f))
        }
        drawRect(a.dia(P.CORCHO, 0f, k), Offset(pie.x - an * 0.13f, pie.y - alto * 1.02f), Size(an * 0.26f, alto * 0.08f))
        // El vaso de lata, delante a la derecha.
        val vx = pie.x + an * 0.9f
        val vr = Rect(vx - w * 0.022f, pie.y - h * 0.17f, vx + w * 0.022f, pie.y)
        contactoAbismo(Offset(vx + w * 0.004f, pie.y - 1f), vr.width * 1.4f, w * 0.012f, a, 0.4f)
        drawRect(a.dia(P.HOJALATA, -0.05f, k), vr.topLeft, vr.size)
        clipRect(vr.left, vr.top, vr.right, vr.bottom) { cilindroAbismo(vr, a, k, -hacia, 1.3f) }
        drawOval(a.dia(P.HOJALATA, 0.15f, k), Offset(vr.left, vr.top - w * 0.005f), Size(vr.width, w * 0.01f))
        drawOval(a.sombra(0.5f), Offset(vr.left + w * 0.004f, vr.top - w * 0.003f), Size(vr.width - w * 0.008f, w * 0.006f))
        drawArc(a.dia(P.HOJALATA, 0f, k), -80f, 160f, false, Offset(vr.right - w * 0.008f, vr.top + vr.height * 0.2f), Size(w * 0.022f, vr.height * 0.5f), style = Stroke(1.6f * u))
        drawLine(a.sombra(0.35f), Offset(vr.left + vr.width * 0.3f, vr.top + vr.height * 0.5f), Offset(vr.left + vr.width * 0.45f, vr.top + vr.height * 0.62f), 1f * u)
    }

    /** El caracol (un bígaro grande del fondo), tendido: la espira, las vueltas y la boca nacarada. */
    private fun DrawScope.caracol(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.1f))
        val s = w * 0.035f
        contactoAbismo(Offset(pie.x + w * 0.006f, pie.y - 1f), s * 2.6f, w * 0.014f, a, 0.42f)
        val c = Offset(pie.x, pie.y - s * 0.9f)
        drawCircle(a.dia(Color(0xFFCDAE88), 0f, k), s, c)
        drawPath(poligonoAbismo(c.x + s * 0.5f, c.y - s * 0.8f, c.x + s * 1.7f, c.y - s * 0.3f, c.x + s * 0.8f, c.y + s * 0.4f), a.dia(Color(0xFFBF9C74), 0f, k))
        for (j in 0 until 3) drawArc(a.dia(Color(0xFF7C5A3A), 0f, k), 200f, 200f, false, Offset(c.x - s * (0.9f - 0.25f * j), c.y - s * (0.9f - 0.25f * j)), Size(s * (1.8f - 0.5f * j), s * (1.8f - 0.5f * j)), style = Stroke(1f * u))
        drawOval(a.dia(Color(0xFFF2DCCB), 0.1f, k), Offset(c.x - s * 0.95f, c.y - s * 0.1f), Size(s * 0.9f, s * 0.8f))
        drawOval(a.sombra(0.35f), Offset(c.x - s * 0.75f, c.y + s * 0.05f), Size(s * 0.55f, s * 0.5f))
        destelloAbismo(Offset(c.x - s * 0.3f, c.y - s * 0.55f), 1f * u, a, k)
        // El erizo seco: el caparazón redondo con sus filas de puntos.
        val ec = Offset(pie.x - s * 2.3f, pie.y - s * 0.62f)
        drawOval(a.dia(Color(0xFF9E8AA0), 0f, k), Offset(ec.x - s * 0.9f, ec.y - s * 0.62f), Size(s * 1.8f, s * 1.24f))
        for (j in 0 until 5) {
            val ang = Math.toRadians(200.0 + j * 35.0)
            for (i in 1..3) drawCircle(a.dia(Color(0xFFE6DCE6), 0f, k).copy(alpha = 0.8f), 0.7f * u, Offset(ec.x + s * 0.27f * i * cos(ang).toFloat(), ec.y + s * 0.19f * i * sin(ang).toFloat()))
        }
        drawArc(a.sombra(0.35f), 0f, 180f, false, Offset(ec.x - s * 0.9f, ec.y - s * 0.62f), Size(s * 1.8f, s * 1.24f), style = Stroke(1.6f * u))
    }

    // ---------------------------------------------------------------- el molinillo, la taza

    /** El molinillo de café de latón, alto y cilíndrico, con su manivela plegada arriba y sus anillos repujados. */
    private fun DrawScope.molinillo(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.4f))
        val hacia = lado(a, pie, w)
        val alto = h * 0.82f; val an = w * 0.058f
        sombras(pie, an, alto, a, k)
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        drawRect(a.dia(P.LATON, 0f, k), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { cilindroAbismo(r, a, k, -hacia, 1.4f) }
        listOf(0.06f, 0.3f, 0.55f, 0.94f).forEach { f ->
            drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(r.left - w * 0.002f, r.top + alto * f), Size(r.width + w * 0.004f, w * 0.006f))
            drawRect(a.brillo(0.5f, k), Offset(r.left - w * 0.002f, r.top + alto * f), Size(r.width + w * 0.004f, 0.8f * u))
        }
        // El repujado: una banda de puntos (sin letras, cifras ni signos).
        for (j in 0 until 5) drawCircle(a.dia(P.LATON_OSC, 0f, k).copy(alpha = 0.7f), 1.1f * u, Offset(r.left + r.width * (0.12f + 0.19f * j), r.top + alto * 0.42f))
        for (j in 0 until 4) drawCircle(a.dia(P.LATON_OSC, 0f, k).copy(alpha = 0.7f), 1.1f * u, Offset(r.left + r.width * (0.21f + 0.19f * j), r.top + alto * 0.47f))
        // La tapa con la manivela plegada.
        drawOval(a.dia(P.LATON, 0.1f, k), Offset(r.left, r.top - w * 0.008f), Size(r.width, w * 0.016f))
        drawLine(a.dia(P.HIERRO, 0f, k), Offset(pie.x, r.top - w * 0.006f), Offset(pie.x + an * 0.9f, r.top - w * 0.028f), 2f * u, StrokeCap.Round)
        drawLine(a.dia(P.HIERRO, 0f, k), Offset(pie.x + an * 0.9f, r.top - w * 0.028f), Offset(pie.x + an * 0.9f, r.top + w * 0.01f), 2f * u, StrokeCap.Round)
        drawOval(a.dia(P.TECA_OSC, 0f, k), Offset(pie.x + an * 0.9f - w * 0.006f, r.top + w * 0.006f), Size(w * 0.012f, w * 0.024f))
        destelloAbismo(Offset(pie.x - hacia * an * 0.25f, r.top + alto * 0.2f), 1.2f * u, a, k)
    }

    /** La taza de loza gruesa, con su asa y el poso del café. */
    private fun DrawScope.taza(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.1f))
        val an = w * 0.05f; val alto = h * 0.2f
        contactoAbismo(Offset(pie.x + w * 0.006f, pie.y - 1f), an * 1.6f, w * 0.014f, a, 0.42f)
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        drawArc(a.dia(Color(0xFFE9E4D6), 0f, k), -70f, 140f, false, Offset(r.right - an * 0.25f, r.top + alto * 0.2f), Size(an * 0.45f, alto * 0.5f), style = Stroke(w * 0.006f))
        drawRoundRect(a.dia(Color(0xFFE9E4D6), 0f, k), r.topLeft, r.size, CornerRadius(an * 0.2f))
        clipRect(r.left, r.top, r.right, r.bottom) { cilindroAbismo(r, a, k, -lado(a, pie, w), 1.3f) }
        drawRect(a.dia(Color(0xFF2E5A6E), 0f, k), Offset(r.left, r.top + alto * 0.18f), Size(r.width, alto * 0.08f))
        drawOval(a.dia(Color(0xFF3A2416), 0f, k), Offset(r.left + 1f * u, r.top - alto * 0.06f), Size(r.width - 2f * u, alto * 0.14f))
    }
}
