package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que acompaña a las entradas en los huecos libres de las repisas del estante de Tierras Antiguas ([MuebleEstanteAntiguas]):
 * grupos de dos o tres cosas solapadas que llenan del 70 al 90 % del hueco y cambian de uno a otro. Donde hay sitio, primero la
 * candil de hierro colgado bajo la tabla de arriba, sobre dos tomos tumbados y otro de pie apoyado detrás (de noche arde); después, según la
 * repisa, la balanza de joyero con sus pesas y unas monedas, la jarra de peltre con su copa, o el pico de minero apoyado con
 * un trozo de mena con cristales verdes.
 */
internal object HuecosEstanteAntiguas {
    private val P = PaletaAntiguas

    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, g: MarcoAntiguas) {
        val w = g.w
        val dI = w * 0.026f
        val fin = g.x1 - dI - w * 0.008f
        var x = oc + w * 0.015f
        if (fin - x < w * 0.07f) return
        val piso = r.bottom - w * 0.006f
        val alto = r.height
        val xl = MuebleEstanteAntiguas.xCandil(oc, g.x1, w)
        if (xl != null) {
            candilConTomos(xl, piso, alto, k, g)
            x += w * 0.19f
        }
        var i = 0
        while (fin - x >= w * 0.08f && i < 3) {
            val sitio = fin - x
            val cual = Math.floorMod(k * 2 + i + (if (xl != null) 0 else 1), 3)
            val usado = when (cual) {
                0 -> balanza(x, minOf(sitio, w * 0.17f), piso, alto, g)
                1 -> jarra(x, minOf(sitio, w * 0.15f), piso, alto, g)
                else -> pico(x, minOf(sitio, w * 0.17f), piso, alto, g)
            }
            x += usado + w * 0.02f; i++
        }
    }

    /** La sombra que algo deja en el fondo del casillero: de día a la derecha (la ventana está a la izquierda); de noche, hacia fuera de la luz. */
    private fun DrawScope.sombraFondo(p: Path, desde: Offset, g: MarcoAntiguas, fuerza: Float = 0.3f) {
        val w = g.w
        val dx = if (!g.a.oscuro) 0.022f else {
            // De noche la luz viene del candil, de la vela o del brasero: se va hacia el otro lado.
            val haciaDer = desde.x > g.vela.x || desde.x > g.brasero.x
            if (haciaDer) 0.018f else -0.018f
        }
        rotate(0f) { drawPath(Path().apply { addPath(p, Offset(w * dx, w * 0.006f)) }, g.am.sombra(fuerza)) }
    }

    // ---------------------------------------------------------------- el candil sobre los tomos

    /**
     * El candil de hierro colgado de un clavo bajo la tabla de arriba, sobre dos tomos tumbados, con otro de pie apoyado detrás
     * contra el fondo.
     */
    private fun DrawScope.candilConTomos(xl: Float, piso: Float, alto: Float, k0: Int, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(Offset(xl, piso - alto * 0.5f))
        // El tomo de pie, apoyado en diagonal contra el fondo.
        val ta = alto * 0.84f; val tw = w * 0.045f
        val pieT = Offset(xl - w * 0.07f, piso)
        val tomo = Path().apply {
            moveTo(pieT.x, pieT.y); lineTo(pieT.x + tw, pieT.y)
            lineTo(pieT.x + tw + ta * 0.2f, pieT.y - ta); lineTo(pieT.x + ta * 0.2f, pieT.y - ta); close()
        }
        sombraFondo(tomo, pieT, g, 0.3f)
        val cueroT = if (Math.floorMod(k0, 2) == 0) Color(0xFF4A2A3E) else Color(0xFF2E3E52)
        drawPath(tomo, am.luz(cueroT, k))
        clipPath(tomo) {
            for (i in 1..4) {
                val f = 0.15f + 0.18f * i
                val y = pieT.y - ta * f
                val xo = pieT.x + ta * 0.2f * f
                drawRect(am.luz(mezcla(cueroT, Color.White, 0.18f), k), Offset(xo - w * 0.01f, y - 1.2f * u), Size(tw + w * 0.02f, 2.2f * u))
                drawRect(am.sombra(0.35f), Offset(xo - w * 0.01f, y + 1f * u), Size(tw + w * 0.02f, 1.2f * u))
            }
            drawRect(am.claro(0.18f), Offset(pieT.x - w * 0.02f, pieT.y - ta), Size(w * 0.03f, ta))
        }
        // Los dos tomos tumbados.
        val t1 = Rect(xl - w * 0.06f, piso - alto * 0.13f, xl + w * 0.07f, piso)
        val t2 = Rect(xl - w * 0.05f, piso - alto * 0.24f, xl + w * 0.055f, piso - alto * 0.13f)
        contactoAntiguas(Offset(t1.center.x + w * 0.01f, piso), t1.width * 1.1f, w * 0.012f, am, 0.45f)
        sombraFondo(Path().apply { addRect(t1); addRect(t2) }, t1.center, g, 0.28f)
        listOf(t1 to Color(0xFF6A2E1E), t2 to Color(0xFF3E4A2A)).forEach { (r, c) ->
            drawRoundRect(am.luz(c, k), r.topLeft, r.size, CornerRadius(r.height * 0.18f))
            drawRect(am.claro(0.2f), Offset(r.left + 2f * u, r.top + r.height * 0.12f), Size(r.width - 4f * u, r.height * 0.2f))
            drawRect(am.sombra(0.3f), Offset(r.left, r.bottom - r.height * 0.25f), Size(r.width, r.height * 0.25f))
            for (i in 1..3) drawRect(am.luz(mezcla(c, Color.White, 0.2f), k), Offset(r.left + r.width * 0.22f * i, r.top), Size(1.6f * u, r.height))
            drawRect(am.luz(P.ORO, k), Offset(r.left + r.width * 0.18f, r.center.y), Size(r.width * 0.64f, 0.8f * u))
            if (am.oscuro) drawRect(P.LLAMA.copy(alpha = (0.55f * k).coerceAtMost(0.5f)), r.topLeft, Size(r.width, 1.2f * u))
        }
        // El candil de hierro, colgado de un clavo bajo la tabla de arriba: el gancho, la varilla, la cazoleta con su pico (donde
        // arde la torcida) y el platillo de abajo, que recoge el aceite que gotea.
        val hierro = am.luz(P.HIERRO, k)
        val hierroL = am.brillo(0.5f, k)
        val techo = piso + w * 0.006f - alto + w * 0.022f
        val llamaY = piso - alto * MuebleEstanteAntiguas.CANDIL_Y
        val pico = Offset(xl + w * 0.045f, llamaY + w * 0.006f)
        val yCaz = pico.y + w * 0.004f
        val yPlato = yCaz + w * 0.036f
        val gancho = Offset(xl - w * 0.012f, techo + w * 0.006f)
        drawRect(am.sombra(0.5f), Offset(gancho.x - 1.6f * u, techo - 1f * u), Size(4.2f * u, 3.6f * u))
        drawRect(hierro, Offset(gancho.x - 2f * u, techo - 1.4f * u), Size(4f * u, 3f * u))
        drawArc(hierro, 180f, 270f, false, Offset(gancho.x - w * 0.006f, gancho.y), Size(w * 0.012f, w * 0.012f), style = Stroke(1.6f * u))
        val varilla0 = Offset(gancho.x, gancho.y + w * 0.012f)
        drawLine(hierro, varilla0, Offset(gancho.x, yPlato), 1.8f * u)
        drawLine(hierroL, varilla0 - Offset(0.7f * u, 0f), Offset(gancho.x - 0.7f * u, yPlato), 0.5f * u)
        // Los dientes de la cremallera, donde se sube o se baja la cazoleta.
        for (i in 0 until 4) drawLine(hierro, Offset(gancho.x, varilla0.y + w * (0.01f + 0.008f * i)), Offset(gancho.x + w * 0.005f, varilla0.y + w * (0.013f + 0.008f * i)), 1.2f * u)
        // La sombra del candil en el fondo.
        sombraFondo(Path().apply {
            addRect(Rect(gancho.x - 1f * u, varilla0.y, gancho.x + 1f * u, yPlato))
            addPath(poligonoAntiguas(xl - w * 0.03f, yCaz, pico.x, pico.y, xl + w * 0.02f, yCaz + w * 0.014f))
            addPath(poligonoAntiguas(xl - w * 0.036f, yPlato, pico.x + w * 0.006f, yPlato - w * 0.006f, xl + w * 0.02f, yPlato + w * 0.016f))
        }, Offset(xl, yCaz), g, 0.3f)
        // El platillo de abajo y la cazoleta: cada una, un cuenco de hierro de canto alto que se estrecha en el pico.
        listOf(yPlato to 1.15f, yCaz to 1f).forEach { (y, e) ->
            val p = Offset(xl + (pico.x - xl) * e, y - w * 0.006f * e)
            val cuenco = Path().apply {
                moveTo(xl - w * 0.032f * e, y)
                lineTo(p.x, p.y)
                quadraticTo(xl + w * 0.026f * e, y + w * 0.006f, xl + w * 0.012f * e, y + w * 0.014f * e)
                quadraticTo(xl - w * 0.026f * e, y + w * 0.016f * e, xl - w * 0.032f * e, y)
                close()
            }
            drawPath(cuenco, hierro)
            // El canto vuelto del cuenco, con su luz, y el reflejo en la panza.
            drawLine(am.luz(P.HIERRO_LUZ, k), Offset(xl - w * 0.032f * e, y), Offset(p.x, p.y), 1.6f * u)
            drawLine(hierroL, Offset(xl - w * 0.03f * e, y + 0.4f * u), Offset(p.x - w * 0.004f, p.y + 0.6f * u), 0.9f * u)
            drawLine(am.brillo(0.35f, k), Offset(xl - w * 0.02f * e, y + w * 0.008f * e), Offset(xl + w * 0.004f * e, y + w * 0.011f * e), 1.2f * u, StrokeCap.Round)
            drawOval(am.luz(Color(0xFF16100C), k), Offset(xl - w * 0.026f * e, y - w * 0.004f), Size(w * 0.052f * e, w * 0.008f))
        }
        // El asa de atrás de la cazoleta, que sube hasta la varilla.
        drawPath(Path().apply { moveTo(xl - w * 0.03f, yCaz); quadraticTo(xl - w * 0.034f, yCaz - w * 0.03f, gancho.x, varilla0.y + w * 0.045f) }, hierro, style = Stroke(1.6f * u))
        // La torcida en el pico.
        drawLine(Color(0xFF221810), Offset(pico.x - w * 0.004f, pico.y + w * 0.002f), Offset(pico.x + w * 0.001f, pico.y - w * 0.004f), 1.6f * u, StrokeCap.Round)
        if (am.oscuro && k > 0.1f) drawLine(P.LLAMA.copy(alpha = (0.7f * k).coerceAtMost(0.7f)), Offset(xl - w * 0.032f, yCaz - 0.6f * u), pico, 1f * u)
    }

    /** La llama del candil y su charco en el hueco (de noche; en los Sueños claros, recién encendida y chica). */
    fun DrawScope.llama(k: Int, r: Rect, d: DisposicionEstante, g: MarcoAntiguas) {
        val a = g.a
        if (!a.lumbre) return
        val oc = d.ocupado.getOrNull(k) ?: return
        val w = g.w
        val xl = MuebleEstanteAntiguas.xCandil(oc, g.x1, w) ?: return
        val piso = r.bottom - w * 0.006f
        val c = Offset(xl + w * 0.045f, piso - r.height * MuebleEstanteAntiguas.CANDIL_Y - w * 0.004f)
        val f = if (!a.oscuro) 0.4f else if (a.noche) 0.6f else 1f
        clipRect(g.x0, r.top, g.x1, r.bottom + d.tablas[k].height * 0.5f) {
            drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.32f * f), P.LLAMA.copy(alpha = 0.1f * f), Color.Transparent), c, w * 0.16f), w * 0.16f, c, blendMode = BlendMode.Screen)
        }
        llamaAntiguas(c, w * (if (a.noche) 0.014f else 0.018f), a)
    }

    // ---------------------------------------------------------------- los otros grupos

    /**
     * La balanza de joyero: la caja de roble, el pilar de latón, la cruz del fiel con su aguja, los dos platillos colgados de sus
     * hilos (uno más bajo, con una gema) y, delante, las pesas y las monedas. Devuelve el ancho que ocupa.
     */
    private fun DrawScope.balanza(x: Float, sitio: Float, piso: Float, alto: Float, g: MarcoAntiguas): Float {
        val w = g.w; val u = density
        val am = g.am
        val an = sitio
        val cx = x + an * 0.5f
        val k = g.cerca(Offset(cx, piso - alto * 0.5f))
        val caja = Rect(x + an * 0.12f, piso - alto * 0.17f, x + an * 0.88f, piso)
        val pilarTop = piso - alto * 0.86f
        val silueta = Path().apply { addRect(caja); addRect(Rect(cx - w * 0.006f, pilarTop, cx + w * 0.006f, caja.top)); addRect(Rect(x, pilarTop, x + an, pilarTop + w * 0.008f)) }
        sombraFondo(silueta, Offset(cx, piso - alto * 0.4f), g, 0.28f)
        contactoAntiguas(Offset(caja.center.x + w * 0.008f, piso), caja.width * 1.1f, w * 0.012f, am, 0.45f)
        drawRect(am.luz(P.ROBLE, k), caja.topLeft, caja.size)
        drawRect(am.luz(P.ROBLE_LUZ, k), caja.topLeft, Size(caja.width, 2f * u))
        drawRect(am.sombra(0.32f), Offset(caja.right - caja.width * 0.22f, caja.top), Size(caja.width * 0.22f, caja.height))
        drawRect(am.luz(P.ROBLE_OSC, k), Offset(caja.left + caja.width * 0.15f, caja.top + caja.height * 0.42f), Size(caja.width * 0.7f, caja.height * 0.32f))
        drawCircle(am.luz(P.LATON, k), 1.8f * u, Offset(caja.center.x, caja.top + caja.height * 0.58f))
        // El pilar y la cruz del fiel, algo inclinada.
        drawRect(am.luz(P.LATON, k), Offset(cx - w * 0.005f, pilarTop), Size(w * 0.01f, caja.top - pilarTop))
        drawRect(am.brillo(0.5f, k), Offset(cx - w * 0.005f, pilarTop), Size(1.2f * u, caja.top - pilarTop))
        val giro = -7f
        rotate(giro, Offset(cx, pilarTop)) {
            drawRect(am.luz(P.LATON, k), Offset(x + an * 0.04f, pilarTop - w * 0.003f), Size(an * 0.92f, w * 0.006f))
            drawRect(am.brillo(0.5f, k), Offset(x + an * 0.04f, pilarTop - w * 0.003f), Size(an * 0.92f, 0.8f * u))
            drawLine(am.luz(P.LATON_OSC, k), Offset(cx, pilarTop), Offset(cx, pilarTop - w * 0.03f), 1.4f * u)
        }
        drawCircle(am.luz(P.LATON, k), w * 0.007f, Offset(cx, pilarTop))
        // Los platillos colgados.
        listOf(-1f to 0.48f, 1f to 0.56f).forEachIndexed { i, (lado, baja) ->
            val px = cx + lado * an * 0.42f
            val top = pilarTop - lado * an * 0.42f * 0.12f
            val py = piso - alto * baja
            listOf(-0.05f, 0f, 0.05f).forEach { dx -> drawLine(am.luz(P.CUERDA, k).copy(alpha = 0.8f), Offset(px, top), Offset(px + w * dx * 0.6f, py), 0.6f * u) }
            val plato = Rect(px - w * 0.032f, py - w * 0.004f, px + w * 0.032f, py + w * 0.008f)
            drawOval(am.luz(P.LATON_OSC, k), plato.topLeft, plato.size)
            drawOval(am.luz(P.LATON, k), Offset(plato.left, plato.top), Size(plato.width, plato.height * 0.55f))
            if (i == 1) {
                val q = Offset(px + w * 0.004f, py - w * 0.006f)
                drawPath(poligonoAntiguas(q.x, q.y - w * 0.008f, q.x + w * 0.008f, q.y, q.x, q.y + w * 0.004f, q.x - w * 0.008f, q.y), am.luz(P.RUBI, k))
                drawCircle(am.claro(0.9f), 0.8f * u, q - Offset(w * 0.003f, w * 0.004f))
            }
        }
        // Las pesas de latón y las monedas, delante, a la izquierda de la caja.
        pesasYMonedas(Offset(x + an * 0.16f, piso), alto, g)
        return an
    }

    /** Las pesas de latón encajadas (de mayor a menor, con su botón) y dos pilas de monedas de oro sin cuño, con una caída. */
    private fun DrawScope.pesasYMonedas(pie: Offset, alto: Float, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(pie - Offset(0f, alto * 0.2f))
        contactoAntiguas(Offset(pie.x + w * 0.012f, pie.y), w * 0.12f, w * 0.01f, am, 0.45f)
        // Las pesas: tres cilindros bajos uno sobre otro y el botón.
        var y = pie.y
        listOf(0.034f to 0.022f, 0.026f to 0.018f, 0.018f to 0.015f).forEach { (r, a0) ->
            val c = Rect(pie.x - w * r, y - w * a0, pie.x + w * r, y)
            drawRect(am.luz(P.LATON, k), c.topLeft, c.size)
            clipRect(c.left, c.top, c.right, c.bottom) { cilindroAntiguas(c, am, k, -1f, 1.2f) }
            drawOval(am.luz(P.LATON_LUZ, k), Offset(c.left, c.top - w * 0.004f), Size(c.width, w * 0.008f))
            drawOval(am.luz(P.LATON_OSC, k), Offset(c.left, c.bottom - w * 0.004f), Size(c.width, w * 0.008f), style = Stroke(0.7f * u))
            y = c.top
        }
        drawCircle(am.luz(P.LATON, k), w * 0.008f, Offset(pie.x, y - w * 0.006f))
        drawCircle(am.brillo(0.6f, k), w * 0.003f, Offset(pie.x - w * 0.003f, y - w * 0.008f))
        // Las monedas: dos pilas, a la derecha, y una tumbada delante.
        listOf(0.05f to 6, 0.075f to 4).forEach { (dx, n) ->
            var yy = pie.y
            for (i in 0 until n) {
                val cx = pie.x + w * dx + w * 0.002f * (azar(8340 + n, i) - 0.5f)
                drawOval(am.luz(P.LATON_OSC, k), Offset(cx - w * 0.013f, yy - w * 0.006f), Size(w * 0.026f, w * 0.008f))
                drawOval(am.luz(mezcla(P.LATON, P.ORO_LUZ, 0.3f), k), Offset(cx - w * 0.013f, yy - w * 0.0085f), Size(w * 0.026f, w * 0.007f))
                yy -= w * 0.0045f
            }
        }
        drawOval(am.luz(P.LATON, k), Offset(pie.x + w * 0.02f, pie.y - w * 0.003f), Size(w * 0.022f, w * 0.006f))
        if (am.oscuro) destelloAntiguas(Offset(pie.x + w * 0.05f, pie.y - w * 0.03f), 0.9f * u, am, k)
    }

    /** La jarra de peltre con su tapa de bisagra y el asa, y una copa de peltre delante. Devuelve el ancho que ocupa. */
    private fun DrawScope.jarra(x: Float, sitio: Float, piso: Float, alto: Float, g: MarcoAntiguas): Float {
        val w = g.w; val u = density
        val am = g.am
        val an = sitio
        val k = g.cerca(Offset(x + an * 0.5f, piso - alto * 0.4f))
        val cuerpo = Rect(x + an * 0.12f, piso - alto * 0.66f, x + an * 0.62f, piso)
        val forma = Path().apply {
            moveTo(cuerpo.left + cuerpo.width * 0.12f, cuerpo.top)
            lineTo(cuerpo.right - cuerpo.width * 0.12f, cuerpo.top)
            cubicTo(cuerpo.right, cuerpo.top + cuerpo.height * 0.4f, cuerpo.right + cuerpo.width * 0.04f, cuerpo.bottom - cuerpo.height * 0.15f, cuerpo.right - cuerpo.width * 0.04f, cuerpo.bottom)
            lineTo(cuerpo.left + cuerpo.width * 0.04f, cuerpo.bottom)
            cubicTo(cuerpo.left - cuerpo.width * 0.04f, cuerpo.bottom - cuerpo.height * 0.15f, cuerpo.left, cuerpo.top + cuerpo.height * 0.4f, cuerpo.left + cuerpo.width * 0.12f, cuerpo.top)
            close()
        }
        val asa = Path().apply {
            moveTo(cuerpo.right - cuerpo.width * 0.06f, cuerpo.top + cuerpo.height * 0.12f)
            cubicTo(cuerpo.right + cuerpo.width * 0.45f, cuerpo.top + cuerpo.height * 0.1f, cuerpo.right + cuerpo.width * 0.4f, cuerpo.top + cuerpo.height * 0.65f, cuerpo.right - cuerpo.width * 0.02f, cuerpo.top + cuerpo.height * 0.72f)
        }
        sombraFondo(Path().apply { addPath(forma); addRect(Rect(cuerpo.right, cuerpo.top, cuerpo.right + cuerpo.width * 0.4f, cuerpo.top + cuerpo.height * 0.7f)) }, cuerpo.center, g, 0.28f)
        contactoAntiguas(Offset(cuerpo.center.x + w * 0.008f, piso), cuerpo.width * 1.15f, w * 0.012f, am, 0.45f)
        drawPath(asa, am.luz(P.PELTRE_OSC, k), style = Stroke(w * 0.01f, cap = StrokeCap.Round))
        drawPath(forma, am.luz(P.PELTRE, k))
        clipPath(forma) { cilindroAntiguas(cuerpo, am, k, if (am.oscuro && cuerpo.center.x > g.vela.x) 1f else -1f, 1.3f) }
        // Los aros del cuerpo y el labio.
        listOf(0.22f, 0.84f).forEach { f -> drawRect(am.luz(P.PELTRE_OSC, k), Offset(cuerpo.left + cuerpo.width * 0.03f, cuerpo.top + cuerpo.height * f), Size(cuerpo.width * 0.94f, 1.6f * u)) }
        // La tapa con su bisagra y el pulgar.
        val tapa = Rect(cuerpo.left + cuerpo.width * 0.06f, cuerpo.top - w * 0.012f, cuerpo.right - cuerpo.width * 0.06f, cuerpo.top + w * 0.004f)
        drawRoundRect(am.luz(P.PELTRE, k), tapa.topLeft, tapa.size, CornerRadius(w * 0.008f))
        drawRect(am.brillo(0.45f, k), Offset(tapa.left + tapa.width * 0.15f, tapa.top + 1f * u), Size(tapa.width * 0.4f, 1.2f * u))
        drawCircle(am.luz(P.PELTRE_OSC, k), w * 0.007f, Offset(tapa.right, tapa.top + w * 0.004f))
        drawCircle(am.luz(P.PELTRE, k), w * 0.005f, Offset(tapa.right + w * 0.006f, tapa.top - w * 0.004f))
        if (am.oscuro) destelloAntiguas(Offset(cuerpo.left + cuerpo.width * 0.3f, cuerpo.top + cuerpo.height * 0.35f), 1.1f * u, am, k)
        // La copa, delante a la derecha.
        val c0 = x + an * 0.62f
        val copa = Rect(c0, piso - alto * 0.38f, c0 + an * 0.34f, piso)
        contactoAntiguas(Offset(copa.center.x + w * 0.006f, piso), copa.width * 1.1f, w * 0.01f, am, 0.45f)
        val vaso = Path().apply {
            moveTo(copa.left, copa.top); lineTo(copa.right, copa.top)
            cubicTo(copa.right, copa.top + copa.height * 0.35f, copa.center.x + copa.width * 0.15f, copa.top + copa.height * 0.5f, copa.center.x + copa.width * 0.06f, copa.top + copa.height * 0.55f)
            lineTo(copa.center.x - copa.width * 0.06f, copa.top + copa.height * 0.55f)
            cubicTo(copa.center.x - copa.width * 0.15f, copa.top + copa.height * 0.5f, copa.left, copa.top + copa.height * 0.35f, copa.left, copa.top)
            close()
        }
        drawPath(vaso, am.luz(P.PELTRE, k))
        clipPath(vaso) { cilindroAntiguas(copa, am, k, -1f, 1.2f) }
        drawOval(am.luz(P.PELTRE_OSC, k), Offset(copa.left, copa.top - w * 0.004f), Size(copa.width, w * 0.008f))
        drawRect(am.luz(P.PELTRE, k), Offset(copa.center.x - w * 0.003f, copa.top + copa.height * 0.55f), Size(w * 0.006f, copa.height * 0.38f))
        drawOval(am.luz(P.PELTRE_OSC, k), Offset(copa.left + copa.width * 0.12f, piso - w * 0.008f), Size(copa.width * 0.76f, w * 0.009f))
        return an
    }

    /**
     * El pico de minero apoyado en diagonal contra el fondo (el mango de fresno, la cabeza de hierro con su pico y su azuela) y, a
     * su pie, un trozo de mena gris con cristales verdes. Devuelve el ancho que ocupa.
     */
    private fun DrawScope.pico(x: Float, sitio: Float, piso: Float, alto: Float, g: MarcoAntiguas): Float {
        val w = g.w; val u = density
        val am = g.am
        val an = sitio
        val k = g.cerca(Offset(x + an * 0.5f, piso - alto * 0.5f))
        val p0 = Offset(x + an * 0.18f, piso - w * 0.004f)
        val p1 = Offset(x + an * 0.72f, piso - alto * 0.8f)
        val mango = Path().apply { moveTo(p0.x, p0.y); lineTo(p1.x, p1.y) }
        val cabeza = Path().apply {
            moveTo(p1.x - an * 0.5f, p1.y + alto * 0.06f)
            quadraticTo(p1.x - an * 0.1f, p1.y - alto * 0.07f, p1.x + an * 0.05f, p1.y - alto * 0.05f)
            quadraticTo(p1.x + an * 0.25f, p1.y - alto * 0.02f, p1.x + an * 0.42f, p1.y + alto * 0.02f)
            lineTo(p1.x + an * 0.4f, p1.y + alto * 0.05f)
            quadraticTo(p1.x + an * 0.18f, p1.y + alto * 0.03f, p1.x + an * 0.04f, p1.y + alto * 0.03f)
            quadraticTo(p1.x - an * 0.15f, p1.y + alto * 0.02f, p1.x - an * 0.5f, p1.y + alto * 0.06f)
            close()
        }
        sombraFondo(Path().apply { addPath(cabeza); addRect(Rect(minOf(p0.x, p1.x), p1.y, maxOf(p0.x, p1.x), p0.y)) }, Offset(x + an * 0.5f, piso - alto * 0.5f), g, 0.18f)
        drawPath(mango, am.luz(Color(0xFFB08A5A), k), style = Stroke(w * 0.011f, cap = StrokeCap.Round))
        drawPath(mango, am.claro(0.3f), style = Stroke(w * 0.003f))
        drawPath(mango, am.sombra(0.25f), style = Stroke(w * 0.011f, cap = StrokeCap.Round, pathEffect = null))
        drawLine(am.luz(Color(0xFFB08A5A), k), p0, p1, w * 0.008f, StrokeCap.Round)
        drawPath(cabeza, am.luz(P.HIERRO, k))
        drawPath(cabeza, am.brillo(0.45f, k), style = Stroke(0.7f * u))
        drawRect(am.luz(P.HIERRO, k), Offset(p1.x - w * 0.008f, p1.y - alto * 0.03f), Size(w * 0.016f, alto * 0.08f))
        // La mena: piedra gris de caras planas con cristales verdes.
        val m = Rect(x + an * 0.3f, piso - alto * 0.3f, x + an * 0.95f, piso)
        val roca = poligonoAntiguas(m.left, m.bottom, m.left + m.width * 0.05f, m.top + m.height * 0.45f, m.left + m.width * 0.3f, m.top + m.height * 0.1f,
            m.left + m.width * 0.62f, m.top, m.right - m.width * 0.08f, m.top + m.height * 0.3f, m.right, m.bottom)
        contactoAntiguas(Offset(m.center.x + w * 0.008f, piso), m.width * 1.1f, w * 0.012f, am, 0.45f)
        drawPath(roca, am.luz(P.PIEDRA, k))
        clipPath(roca) {
            drawPath(poligonoAntiguas(m.left, m.bottom, m.left + m.width * 0.05f, m.top + m.height * 0.45f, m.left + m.width * 0.3f, m.top + m.height * 0.1f, m.left + m.width * 0.42f, m.bottom), am.claro(0.25f))
            drawPath(poligonoAntiguas(m.left + m.width * 0.62f, m.top, m.right - m.width * 0.08f, m.top + m.height * 0.3f, m.right, m.bottom, m.left + m.width * 0.66f, m.bottom), am.sombra(0.32f))
            // Los cristales verdes metidos en la piedra: prismas cortos, cada uno con su giro, su cara clara y su cara honda.
            for (i in 0 until 5) {
                val q = Offset(m.left + m.width * (0.3f + 0.5f * azar(8301, i)), m.top + m.height * (0.25f + 0.55f * azar(8302, i)))
                val s = w * (0.008f + 0.005f * azar(8303, i))
                rotate(-60f + 120f * azar(8304, i), q) {
                    drawRect(am.luz(mezcla(P.ESMERALDA, Color.Black, 0.3f), k), Offset(q.x - s * 1.4f, q.y - s * 0.5f), Size(s * 2.8f, s))
                    drawRect(am.luz(mezcla(P.ESMERALDA, Color.White, 0.25f), k), Offset(q.x - s * 1.4f, q.y - s * 0.5f), Size(s * 2.8f, s * 0.45f))
                    drawPath(poligonoAntiguas(q.x + s * 1.4f, q.y - s * 0.5f, q.x + s * 2f, q.y, q.x + s * 1.4f, q.y + s * 0.5f), am.luz(P.ESMERALDA, k))
                    drawLine(am.claro(0.7f), Offset(q.x - s * 1.2f, q.y - s * 0.4f), Offset(q.x + s * 0.6f, q.y - s * 0.4f), 0.7f * u)
                }
            }
        }
        drawPath(roca, am.sombra(0.35f), style = Stroke(0.8f * u))
        return an
    }
}
