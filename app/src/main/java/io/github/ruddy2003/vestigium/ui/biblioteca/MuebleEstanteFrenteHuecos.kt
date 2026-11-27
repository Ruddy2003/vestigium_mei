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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que acompaña en los huecos de las repisas de Frente (de lo que ocupan las entradas al costado derecho): grupos de dos o
 * tres cosas de la base, solapadas y grandes, que cambian de un hueco a otro. El foco rojo de cuarto oscuro con su frasco de
 * revelador (de noche, la luz de su hueco); las latas de microfilm sueltas con su película; la caja de válvulas de radio; el
 * termo con su taza; el transmisor de mano con su batería. En un hueco muy ancho, dos grupos.
 */
internal object HuecosEstanteFrente {
    private val P = PaletaFrente
    private val M = MuebleEstanteFrente

    /** El color de [c] en [p]: de día con el charco de la cúpula; de noche, con la luz que le llega. */
    private fun DrawScope.en(c: Color, f: Float, p: Offset, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float): Color {
        val w = size.width
        return if (a.oscuro) a.luz(c, cerca(p), frio(p)) else a.cupula(a.dia(c, f, cerca(p)), M.cupula(p.x / w - M.CUPULA_X, p.y / w - M.CUPULA_Y) * 0.25f)
    }

    /**
     * Pone lo que acompaña en el hueco de la repisa [r], de [oc] (donde acaban las entradas) a [der] (el costado). [n] cuenta los
     * grupos ya puestos en las repisas de arriba: cada grupo nuevo es el siguiente de los cuatro, así no se repite de una repisa a
     * la otra. Si [xFoco] no es -1, el foco rojo va ahí. Devuelve cuántos grupos puso (el foco no cuenta).
     */
    fun DrawScope.hueco(n: Int, r: Rect, oc: Float, der: Float, xFoco: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float): Int {
        val w = size.width
        val x0 = oc + w * 0.018f; val x1 = der - w * 0.022f
        val libre = x1 - x0
        if (libre < w * 0.1f) return 0
        val h = r.height
        val piso = r.bottom - 2.5f * density
        if (xFoco >= 0f) {
            foco(Offset(xFoco, piso), r.top, h, a, cerca, frio)
            // Si queda sitio a la derecha del foco, otro grupo.
            if (x1 - (xFoco + h * 0.62f) > w * 0.16f) {
                grupo(n % 4 + 1, x1 - (x1 - xFoco - h * 0.5f) * 0.5f, piso, h, minOf(x1 - xFoco - h * 0.5f, h * 0.9f), a, cerca, frio)
                return 1
            }
            return 0
        }
        if (libre > w * 0.5f) {
            grupo(n % 4 + 1, x0 + libre * 0.25f, piso, h, libre * 0.42f, a, cerca, frio)
            grupo((n + 1) % 4 + 1, x0 + libre * 0.72f, piso, h, libre * 0.42f, a, cerca, frio)
            return 2
        }
        grupo(n % 4 + 1, x0 + libre * 0.5f, piso, h, libre, a, cerca, frio)
        return 1
    }

    /** Uno de los grupos (1 a 4) centrado en [cx], en [libre] de ancho como mucho. */
    private fun DrawScope.grupo(g: Int, cx: Float, piso: Float, h: Float, libre: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val s = minOf(1f, libre / (h * 0.75f)).coerceAtLeast(0.55f)
        when (g) {
            1 -> latas(cx, piso, h * s, a, cerca, frio)
            2 -> valvulas(cx, piso, h * s, a, cerca, frio)
            3 -> termo(cx, piso, h * s, a, cerca, frio)
            else -> transmisor(cx, piso, h * s, a, cerca, frio)
        }
    }

    /** La sombra que deja algo de [r] en la piedra de atrás (la luz viene de la izquierda) y en la bandeja. */
    private fun DrawScope.sombras(r: Rect, a: AireFrente, hacia: Float = 1f) {
        val u = density
        drawRoundRect(a.sombra(0.32f), Offset(r.left + hacia * r.width * 0.22f + 2f * u, r.top + r.height * 0.1f), Size(r.width, r.height * 0.9f), CornerRadius(r.width * 0.2f))
        contactoFrente(Offset(r.center.x, r.bottom), r.width * 1.2f, 5f * u, a, 0.45f, hacia)
    }

    // ---------------------------------------------------------------- el foco rojo de cuarto oscuro

    /**
     * El foco rojo de cuarto oscuro, colgado de la tabla de arriba como en los nichos del Ideario: el cable, el casquillo de
     * baquelita y la bombilla de vidrio rojo (de noche, encendida, con su halo y su charco en la bandeja; de día, apagada). Debajo,
     * en la bandeja, el frasco pardo del revelador con su tapón y, delante, la cubeta de revelar con su hoja.
     */
    private fun DrawScope.foco(base: Offset, techo: Float, h: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val enc = a.oscuro
        val c = Offset(base.x, base.y - h * M.FOCO_Y)
        val fuerza = if (a.noche) 0.6f else 1f
        // El charco rojo en la bandeja, debajo del foco.
        if (enc) drawOval(androidx.compose.ui.graphics.Brush.radialGradient(listOf(P.FOCO.copy(alpha = 0.3f * fuerza), P.FOCO.copy(alpha = 0f)), Offset(c.x, base.y), h * 0.5f),
            Offset(c.x - h * 0.5f, base.y - h * 0.08f), Size(h, h * 0.16f))
        // El frasco del revelador, a la derecha.
        val fr = Rect(base.x + h * 0.06f, base.y - h * 0.6f, base.x + h * 0.34f, base.y)
        sombras(fr, a)
        val kf = cerca(fr.center); val vf = frio(fr.center)
        val vidrioPardo = Color(0xFF5A2E12)
        val cuello = Rect(fr.center.x - fr.width * 0.18f, fr.top, fr.center.x + fr.width * 0.18f, fr.top + h * 0.1f)
        drawRect(a.luz(vidrioPardo, kf, vf), cuello.topLeft, cuello.size)
        drawRoundRect(a.luz(P.BAQUELITA, kf, vf), Offset(cuello.left - 1.4f * u, cuello.top - h * 0.06f), Size(cuello.width + 2.8f * u, h * 0.07f), CornerRadius(1.5f * u))
        val cuerpo = Rect(fr.left, fr.top + h * 0.15f, fr.right, fr.bottom)
        val forma = Path().apply {
            moveTo(cuello.left, cuello.bottom); quadraticTo(cuerpo.left, cuello.bottom, cuerpo.left, cuerpo.top + h * 0.04f)
            lineTo(cuerpo.left, cuerpo.bottom); lineTo(cuerpo.right, cuerpo.bottom); lineTo(cuerpo.right, cuerpo.top + h * 0.04f)
            quadraticTo(cuerpo.right, cuello.bottom, cuello.right, cuello.bottom); close()
        }
        clipPath(forma) {
            redondoFrente(Rect(cuerpo.left, cuello.bottom, cuerpo.right, cuerpo.bottom), a.luz(vidrioPardo, kf, vf), a.luz(mezcla(vidrioPardo, P.AMBAR, 0.35f), kf, vf), a.luz(mezcla(vidrioPardo, Color.Black, 0.5f), kf, vf), -1f)
            // La etiqueta de cinta de papel, sin letras: una franja de tinta.
            val e = Rect(cuerpo.left, cuerpo.top + cuerpo.height * 0.3f, cuerpo.right, cuerpo.top + cuerpo.height * 0.64f)
            drawRect(a.etiqueta(P.CINTA, kf, vf), e.topLeft, e.size)
            drawRect(a.luz(P.QUEMADO_TINTA, kf, vf), Offset(e.left, e.top + e.height * 0.2f), Size(e.width, e.height * 0.16f))
        }
        if (enc) drawLine(P.FOCO.copy(alpha = 0.75f * fuerza), Offset(cuerpo.left + 0.8f * u, cuerpo.top + 3f * u), Offset(cuerpo.left + 0.8f * u, cuerpo.bottom - 3f * u), 1.4f * u)
        else drawLine(a.claro(0.55f), Offset(cuerpo.left + cuerpo.width * 0.2f, cuerpo.top + 3f * u), Offset(cuerpo.left + cuerpo.width * 0.2f, cuerpo.bottom - 4f * u), 1.2f * u)
        // La cubeta de revelar, delante a la izquierda: la bandeja baja de esmalte con su hoja asomando.
        val cu = Rect(base.x - h * 0.36f, base.y - h * 0.11f, base.x + h * 0.12f, base.y)
        contactoFrente(Offset(cu.center.x, base.y), cu.width * 1.05f, 4f * u, a, 0.45f)
        val kc = cerca(cu.center); val vc = frio(cu.center)
        drawRect(a.luz(P.PAPEL, kc, vc), Offset(cu.left + cu.width * 0.12f, cu.top - h * 0.03f), Size(cu.width * 0.5f, h * 0.05f))
        drawRoundRect(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(a.luz(P.HUESO_LUZ, kc, vc), a.luz(P.HUESO_OSC, kc, vc)), cu.top, cu.bottom), cu.topLeft, cu.size, CornerRadius(2f * u))
        drawRect(a.luz(P.CONSOLA, kc, vc), cu.topLeft, Size(cu.width, 1.6f * u))
        drawLine(a.sombra(0.35f), Offset(cu.left + 2f * u, cu.top + cu.height * 0.55f), Offset(cu.right - 2f * u, cu.top + cu.height * 0.55f), 0.7f * u)
        // El cable que baja de la tabla de arriba y el casquillo.
        val k = cerca(c); val kv = frio(c)
        val casq = Rect(c.x - h * 0.045f, c.y - h * 0.15f, c.x + h * 0.045f, c.y - h * 0.065f)
        drawLine(a.sombra(0.35f), Offset(c.x + 2f * u, techo + 4f * u), Offset(c.x + 2f * u, casq.top + 2f * u), 1.4f * u)
        drawLine(a.luz(P.BAQUELITA, k, kv), Offset(c.x, techo + 3f * u), Offset(c.x, casq.top), 1.3f * u)
        drawRoundRect(a.luz(P.BAQUELITA_LUZ, k, kv), casq.topLeft, casq.size, CornerRadius(1.5f * u))
        drawLine(a.brillo(0.35f, k), Offset(casq.left + 1f * u, casq.top + 1f * u), Offset(casq.left + 1f * u, casq.bottom - 1f * u), 0.7f * u)
        // La bombilla de vidrio rojo, en forma de pera.
        val rb = h * 0.085f
        val pera = Path().apply {
            moveTo(c.x - rb * 0.42f, casq.bottom)
            cubicTo(c.x - rb * 0.5f, c.y - rb * 0.3f, c.x - rb * 1.05f, c.y, c.x - rb, c.y + rb * 0.25f)
            cubicTo(c.x - rb, c.y + rb * 1.2f, c.x + rb, c.y + rb * 1.2f, c.x + rb, c.y + rb * 0.25f)
            cubicTo(c.x + rb * 1.05f, c.y, c.x + rb * 0.5f, c.y - rb * 0.3f, c.x + rb * 0.42f, casq.bottom); close()
        }
        if (enc) {
            drawCircle(androidx.compose.ui.graphics.Brush.radialGradient(listOf(P.FOCO.copy(alpha = 0.42f * fuerza), P.FOCO.copy(alpha = 0.12f * fuerza), P.FOCO.copy(alpha = 0f)), c, h * 0.42f), h * 0.42f, c)
            drawPath(pera, androidx.compose.ui.graphics.Brush.radialGradient(listOf(Color(0xFFFFB08A), P.FOCO, Color(0xFF9A1A0E)), Offset(c.x - rb * 0.2f, c.y + rb * 0.2f), rb * 1.4f))
            drawLine(Color(0xFFFFE0C0), Offset(c.x - rb * 0.3f, c.y + rb * 0.25f), Offset(c.x + rb * 0.3f, c.y + rb * 0.25f), 1.1f * u, StrokeCap.Round)
        } else {
            drawPath(pera, a.dia(Color(0xFF7A1C12), 0f))
            drawPath(pera, a.sombra(0.35f), style = Stroke(0.6f * u))
            drawCircle(a.claro(0.6f), rb * 0.22f, Offset(c.x - rb * 0.45f, c.y + rb * 0.05f))
        }
    }

    // ---------------------------------------------------------------- las latas sueltas

    /** Dos latas de microfilm acostadas, una sobre otra y corridas, y una de pie detrás, asomando; la película colgando. */
    private fun DrawScope.latas(cx: Float, piso: Float, h: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val ancho = h * 0.62f
        val alto = h * 0.13f
        // La de pie, detrás: el canto redondo que asoma por encima de las otras.
        val rp = h * 0.36f
        val cp = Offset(cx + h * 0.08f, piso - rp)
        val kp = cerca(cp); val vp = frio(cp)
        drawCircle(a.sombra(0.32f), rp, Offset(cp.x + 4f * u, cp.y + 2f * u))
        drawCircle(Brush.linearGradient(listOf(a.luz(P.CROMO_LUZ, kp, vp), a.luz(P.ALUMINIO, kp, vp), a.luz(P.CROMO_OSC, kp, vp)), Offset(cp.x - rp, cp.y - rp), Offset(cp.x + rp, cp.y + rp)), rp, cp)
        drawCircle(a.sombra(0.25f), rp * 0.84f, cp, style = Stroke(1f * u))
        drawCircle(a.brillo(0.3f, kp), rp * 0.62f, cp, style = Stroke(0.8f * u))
        // La etiqueta cruzada con su franja de color, que tapa el centro.
        rotate(-12f, cp) {
            val e = Rect(cp.x - rp * 0.7f, cp.y - rp * 0.2f, cp.x + rp * 0.7f, cp.y + rp * 0.2f)
            drawRect(a.etiqueta(P.CINTA, kp, vp), e.topLeft, e.size)
            drawRect(a.luz(Color(0xFF2F6F62), kp, vp), e.topLeft, Size(e.width * 0.18f, e.height))
        }
        // Las dos acostadas, delante.
        val colores = listOf(Color(0xFF8E3A2C), Color(0xFFC9A15A))
        for (i in 0 until 2) {
            val dx = if (i == 0) -h * 0.08f else h * 0.02f
            val r = Rect(cx - ancho / 2 + dx, piso - (i + 1) * alto, cx + ancho / 2 + dx, piso - i * alto)
            if (i == 0) contactoFrente(Offset(r.center.x, piso), ancho * 1.15f, 5f * u, a, 0.45f)
            val k = cerca(r.center); val kv = frio(r.center)
            val col = a.luz(colores[i], k, kv)
            val elipse = r.height * 0.9f
            val metal = Brush.horizontalGradient(0f to mezcla(col, Color.Black, 0.4f), 0.22f to mezcla(col, Color.White, if (a.oscuro) 0.1f else 0.3f), 0.5f to col,
                1f to mezcla(col, Color.Black, 0.5f), startX = r.left, endX = r.right)
            drawRect(metal, r.topLeft, r.size)
            drawArc(metal, 0f, 180f, true, Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse))
            drawArc(a.sombra(0.5f), 0f, 180f, false, Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse), style = Stroke(0.8f * u))
            if (i == 1) {
                drawOval(Brush.linearGradient(listOf(mezcla(col, Color.White, if (a.oscuro) 0.12f else 0.35f), col), Offset(r.left, r.top - elipse / 2), Offset(r.right, r.top + elipse / 2)),
                    Offset(r.left, r.top - elipse / 2), Size(r.width, elipse))
                drawOval(a.sombra(0.25f), Offset(r.left + r.width * 0.18f, r.top - elipse * 0.32f), Size(r.width * 0.64f, elipse * 0.64f), style = Stroke(0.7f * u))
            }
            val e = Rect(r.center.x - r.width * 0.15f, r.top + r.height * 0.36f, r.center.x + r.width * 0.15f, r.bottom + elipse * 0.18f)
            drawRect(a.etiqueta(P.CINTA, k, kv), e.topLeft, e.size)
        }
        // La película que sale de la de abajo y cuelga por el canto de la bandeja.
        val sale = Offset(cx + ancho * 0.36f - h * 0.08f, piso - alto * 0.5f)
        with(CoronaEstanteFrente) { tira(sale, Offset(sale.x + h * 0.2f, sale.y), Offset(sale.x + h * 0.16f, piso + h * 0.02f), Offset(sale.x + h * 0.1f, piso + h * 0.16f), 4.4f * u, a, cerca, frio) }
    }

    // ---------------------------------------------------------------- las válvulas

    /**
     * La caja de cartón de las válvulas de radio, abierta, con tres válvulas de vidrio de pie (las placas grises, el espejo del
     * vacío arriba y la base de baquelita) y una acostada delante.
     */
    private fun DrawScope.valvulas(cx: Float, piso: Float, h: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val caja = Rect(cx - h * 0.3f, piso - h * 0.42f, cx + h * 0.3f, piso)
        sombras(caja, a)
        // Las válvulas, de pie dentro de la caja (se dibujan antes que su frente).
        listOf(-0.17f to 0.82f, 0.02f to 0.74f, 0.19f to 0.8f).forEachIndexed { i, (dx, alto) ->
            val x = cx + h * dx
            val v = Rect(x - h * 0.07f, piso - h * alto, x + h * 0.07f, piso - h * 0.3f)
            valvula(v, a, cerca, frio, i)
        }
        // El frente de la caja, con la solapa abierta hacia delante.
        val k = cerca(caja.center); val kv = frio(caja.center)
        drawRect(Brush.horizontalGradient(listOf(en(P.CARTON, 0.08f, caja.center, a, cerca, frio), en(P.CARTON, -0.12f, caja.center, a, cerca, frio)), caja.left, caja.right), caja.topLeft, caja.size)
        val solapa = poligonoFrente(caja.left, caja.top, caja.right, caja.top, caja.right - h * 0.04f, caja.top + h * 0.1f, caja.left + h * 0.04f, caja.top + h * 0.1f)
        drawPath(solapa, en(mezcla(P.CARTON, P.CINTA, 0.3f), 0.1f, caja.center, a, cerca, frio))
        drawLine(a.sombra(0.4f), Offset(caja.left, caja.top + h * 0.1f), Offset(caja.right, caja.top + h * 0.1f), 0.8f * u)
        // Una franja de color impresa en la caja (sin letras) y la cinta de papel que la cerraba, rota.
        drawRect(a.luz(Color(0xFF2F6F62), k, kv), Offset(caja.left, caja.top + h * 0.2f), Size(caja.width, h * 0.05f))
        drawRect(a.etiqueta(P.CINTA, k, kv), Offset(caja.left + caja.width * 0.4f, caja.top + h * 0.1f), Size(caja.width * 0.14f, h * 0.2f))
        drawLine(a.sombra(0.35f), Offset(caja.right, caja.top), Offset(caja.right, caja.bottom), 1.2f * u)
        // La válvula acostada delante.
        val acostada = Rect(caja.left - h * 0.06f, piso - h * 0.11f, caja.left + h * 0.32f, piso)
        rotate(-4f, acostada.center) {
            contactoFrente(Offset(acostada.center.x, piso), acostada.width, 4f * u, a, 0.4f)
            val kk = cerca(acostada.center); val vv = frio(acostada.center)
            drawRoundRect(a.luz(P.BAQUELITA, kk, vv), Offset(acostada.left, acostada.top + acostada.height * 0.15f), Size(acostada.width * 0.26f, acostada.height * 0.7f), CornerRadius(2f * u))
            val vid = Rect(acostada.left + acostada.width * 0.24f, acostada.top, acostada.right, acostada.bottom)
            drawRoundRect(a.luz(mezcla(P.VIDRIO_LUZ, P.HUESO, 0.6f), kk, vv).copy(alpha = 0.55f), vid.topLeft, vid.size, CornerRadius(vid.height * 0.5f))
            drawRect(a.luz(P.CROMO_OSC, kk, vv), Offset(vid.left + vid.width * 0.15f, vid.top + vid.height * 0.3f), Size(vid.width * 0.5f, vid.height * 0.4f))
            drawRoundRect(a.brillo(0.6f, kk), Offset(vid.left + 2f * u, vid.top + 1.2f * u), Size(vid.width * 0.7f, 1.2f * u), CornerRadius(0.6f * u))
        }
    }

    /** Una válvula de pie en [v]: la base de baquelita (que queda dentro de la caja), la ampolla de vidrio, las placas y el espejo. */
    private fun DrawScope.valvula(v: Rect, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float, i: Int) {
        val u = density
        val k = cerca(v.center); val kv = frio(v.center)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(v, CornerRadius(v.width * 0.5f, v.width * 0.55f), CornerRadius(v.width * 0.5f, v.width * 0.55f), CornerRadius.Zero, CornerRadius.Zero)) }
        // El vidrio, casi transparente: deja ver la piedra con un velo verdoso.
        drawPath(forma, a.luz(mezcla(P.VIDRIO_LUZ, P.HUESO, 0.55f), k, kv).copy(alpha = 0.42f))
        clipPath(forma) {
            // El espejo del vacío arriba (plateado) y las placas grises.
            drawRect(a.luz(mezcla(P.CROMO, P.ACERO, 0.4f), k, kv), Offset(v.left, v.top), Size(v.width, v.height * 0.16f))
            drawRect(a.luz(P.CROMO_OSC, k, kv), Offset(v.left + v.width * 0.26f, v.top + v.height * 0.3f), Size(v.width * 0.48f, v.height * 0.5f))
            drawRect(a.luz(P.CROMO, k, kv), Offset(v.left + v.width * 0.3f, v.top + v.height * 0.3f), Size(v.width * 0.1f, v.height * 0.5f))
            drawLine(a.luz(P.CROMO_OSC, k, kv), Offset(v.center.x, v.top + v.height * 0.8f), Offset(v.center.x, v.bottom), 0.7f * u)
            // De noche, alguna conserva el resplandor de su filamento (solo la del medio, apenas).
            if (a.oscuro && i == 1 && !a.dormida) drawCircle(P.AMBAR.copy(alpha = 0.35f), v.width * 0.3f, Offset(v.center.x, v.top + v.height * 0.55f))
        }
        drawPath(forma, a.luz(mezcla(P.VIDRIO_LUZ, P.HUESO, 0.4f), k, kv).copy(alpha = 0.7f), style = Stroke(0.8f * u))
        drawLine(a.brillo(0.75f, k), Offset(v.left + v.width * 0.24f, v.top + v.height * 0.18f), Offset(v.left + v.width * 0.24f, v.bottom - 2f * u), 1.3f * u, StrokeCap.Round)
        // La punta de cierre del vidrio, arriba.
        drawCircle(a.luz(mezcla(P.VIDRIO_LUZ, P.HUESO, 0.5f), k, kv), v.width * 0.1f, Offset(v.center.x, v.top - v.width * 0.04f))
    }

    // ---------------------------------------------------------------- el termo y la taza

    /** El termo de chapa pintada de verde gris, abollado, con su tapa-taza de aluminio, y la taza de lata esmaltada delante. */
    private fun DrawScope.termo(cx: Float, piso: Float, h: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val t = Rect(cx - h * 0.14f, piso - h * 0.8f, cx + h * 0.14f, piso)
        sombras(t, a)
        val k = cerca(t.center); val kv = frio(t.center)
        val verde = Color(0xFF5E7466)
        val cuerpo = Rect(t.left, t.top + h * 0.17f, t.right, t.bottom)
        drawRoundRect(a.luz(verde, k, kv), cuerpo.topLeft, cuerpo.size, CornerRadius(t.width * 0.18f))
        clipPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(cuerpo, CornerRadius(t.width * 0.18f))) }) {
            redondoFrente(cuerpo, a.luz(verde, k, kv), a.luz(mezcla(verde, P.HUESO_LUZ, 0.35f), k, kv), a.luz(mezcla(verde, P.SOMBRA, 0.5f), k * 0.7f, kv), -1f)
            // La abolladura, con su luz y su sombra, y la pintura saltada abajo.
            drawOval(a.sombra(0.3f), Offset(cuerpo.left + cuerpo.width * 0.45f, cuerpo.top + cuerpo.height * 0.42f), Size(cuerpo.width * 0.4f, cuerpo.height * 0.14f))
            drawOval(a.claro(0.3f), Offset(cuerpo.left + cuerpo.width * 0.42f, cuerpo.top + cuerpo.height * 0.4f), Size(cuerpo.width * 0.3f, cuerpo.height * 0.05f))
            drawRect(a.luz(P.ALUMINIO, k, kv), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.08f), Size(cuerpo.width, cuerpo.height * 0.08f))
            drawRect(a.luz(P.ACERO, k, kv).copy(alpha = 0.6f), Offset(cuerpo.left + cuerpo.width * 0.6f, cuerpo.bottom - cuerpo.height * 0.2f), Size(cuerpo.width * 0.18f, cuerpo.height * 0.08f))
        }
        // La tapa-taza de aluminio, algo más ancha, con sus estrías.
        val tapa = Rect(t.left - t.width * 0.06f, t.top, t.right + t.width * 0.06f, cuerpo.top + h * 0.02f)
        drawRoundRect(Brush.horizontalGradient(listOf(a.luz(P.CROMO_LUZ, k, kv), a.luz(P.ALUMINIO, k, kv), a.luz(P.CROMO_OSC, k, kv)), tapa.left, tapa.right), tapa.topLeft, tapa.size, CornerRadius(tapa.width * 0.12f))
        for (j in 1..3) drawLine(a.sombra(0.3f), Offset(tapa.left + 1f * u, tapa.top + tapa.height * 0.25f * j), Offset(tapa.right - 1f * u, tapa.top + tapa.height * 0.25f * j), 0.6f * u)
        drawRect(a.sombra(0.4f), Offset(tapa.left, tapa.bottom - 1f * u), Size(tapa.width, 1.2f * u))
        if (a.oscuro) drawLine(a.filo(0.5f * k), Offset(t.left + 0.6f * u, cuerpo.top + 3f * u), Offset(t.left + 0.6f * u, cuerpo.bottom - 3f * u), 1.2f * u)
        // La taza, delante a la derecha.
        tazaChica(Offset(t.right + h * 0.12f, piso), h * 0.26f, a, cerca, frio)
    }

    /** Una taza de lata esmaltada chica, de ancho [an], con su asa a la derecha y su borde oscuro. */
    private fun DrawScope.tazaChica(base: Offset, an: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val alto = an * 1.05f
        val r = Rect(base.x - an / 2, base.y - alto, base.x + an / 2, base.y)
        val k = cerca(r.center); val kv = frio(r.center)
        contactoFrente(base, an * 1.5f, 4.5f * u, a, 0.45f)
        drawArc(a.luz(P.HUESO, k, kv), -80f, 170f, false, Offset(r.right - an * 0.22f, r.top + alto * 0.2f), Size(an * 0.5f, alto * 0.5f), style = Stroke(2f * u))
        redondoFrente(r, a.luz(P.HUESO, k, kv), a.luz(P.HUESO_LUZ, k, kv), a.luz(P.HUESO_OSC, k * 0.7f, kv), -1f)
        drawRect(a.luz(P.CONSOLA, k, kv), r.topLeft, Size(r.width, 2f * u))
        drawOval(a.luz(P.CONSOLA_OSC, k * 0.6f, kv), Offset(r.left, r.top - 1.8f * u), Size(r.width, 3.6f * u))
        drawRect(a.luz(P.CONSOLA, k, kv), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        drawOval(a.luz(P.CONSOLA_OSC, k, kv), Offset(r.left + an * 0.22f, r.top + alto * 0.22f), Size(an * 0.14f, alto * 0.09f))
    }

    // ---------------------------------------------------------------- el transmisor de mano

    /**
     * El transmisor de mano, de pie y algo ladeado contra la piedra: la caja de consola oscura con su rejilla, la perilla y la
     * antena de cromo que sube; delante, la batería de repuesto, de chapa óxido con sus bornes.
     */
    private fun DrawScope.transmisor(cx: Float, piso: Float, h: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val t = Rect(cx - h * 0.15f, piso - h * 0.56f, cx + h * 0.15f, piso)
        sombras(t, a)
        val k = cerca(t.center); val kv = frio(t.center)
        rotate(4f, Offset(t.center.x, t.bottom)) {
            // La antena, de tramos de cromo, que sube por detrás.
            val ax = t.right - t.width * 0.22f
            drawLine(a.sombra(0.35f), Offset(ax + 2f * u, t.top + 2f * u), Offset(ax + 2f * u - h * 0.02f, piso - h * 0.9f + 2f * u), 1.6f * u)
            drawLine(a.luz(P.CROMO, k, kv), Offset(ax, t.top), Offset(ax - h * 0.01f, piso - h * 0.75f), 1.8f * u)
            drawLine(a.luz(P.CROMO_LUZ, k, kv), Offset(ax - h * 0.01f, piso - h * 0.75f), Offset(ax - h * 0.02f, piso - h * 0.9f), 1.2f * u)
            drawCircle(a.luz(P.CROMO, k, kv), 1.6f * u, Offset(ax - h * 0.02f, piso - h * 0.9f))
            drawRoundRect(Brush.horizontalGradient(listOf(a.luz(P.CONSOLA_LUZ, k, kv), a.luz(P.CONSOLA, k, kv), a.luz(P.CONSOLA_OSC, k, kv)), t.left, t.right), t.topLeft, t.size, CornerRadius(t.width * 0.16f))
            drawLine(a.brillo(0.3f, k), Offset(t.left + 3f * u, t.top + 1.2f * u), Offset(t.right - 3f * u, t.top + 1.2f * u), 0.8f * u)
            // La rejilla del altavoz, de ranuras.
            for (j in 0 until 6) {
                val y = t.top + t.height * (0.18f + 0.07f * j)
                drawRoundRect(Color.Black.copy(alpha = 0.75f), Offset(t.left + t.width * 0.2f, y), Size(t.width * 0.6f, 1.6f * u), CornerRadius(0.8f * u))
                drawLine(a.brillo(0.12f, k), Offset(t.left + t.width * 0.2f, y + 2.2f * u), Offset(t.right - t.width * 0.2f, y + 2.2f * u), 0.5f * u)
            }
            // El botón de hablar en el costado y el piloto.
            drawRect(a.luz(P.BAQUELITA, k, kv), Offset(t.left - 2f * u, t.top + t.height * 0.3f), Size(2.4f * u, t.height * 0.22f))
            pilotoFrente(Offset(t.left + t.width * 0.3f, t.top + t.height * 0.68f), 1.6f * u, P.ROJO, a.oscuro && !a.dormida, a, k)
            // La perilla arriba.
            drawRect(a.luz(P.BAQUELITA_LUZ, k, kv), Offset(t.left + t.width * 0.2f, t.top - h * 0.05f), Size(t.width * 0.24f, h * 0.05f))
            drawLine(a.brillo(0.35f, k), Offset(t.left + t.width * 0.22f, t.top - h * 0.045f), Offset(t.left + t.width * 0.42f, t.top - h * 0.045f), 0.7f * u)
        }
        // La batería de repuesto, delante a la derecha.
        val b = Rect(t.right - h * 0.04f, piso - h * 0.24f, t.right + h * 0.24f, piso)
        sombras(b, a)
        val kb = cerca(b.center); val vb = frio(b.center)
        drawRect(Brush.horizontalGradient(listOf(a.luz(mezcla(P.OXIDO, P.HUESO_LUZ, 0.2f), kb, vb), a.luz(P.OXIDO, kb, vb), a.luz(mezcla(P.OXIDO, P.SOMBRA, 0.4f), kb, vb)), b.left, b.right), b.topLeft, b.size)
        drawRect(a.luz(P.CONSOLA, kb, vb), Offset(b.left, b.top + b.height * 0.3f), Size(b.width, b.height * 0.14f))
        listOf(0.28f, 0.72f).forEach { f -> drawRect(a.luz(P.CROMO, kb, vb), Offset(b.left + b.width * f - 2f * u, b.top - 2.4f * u), Size(4f * u, 2.6f * u)) }
        drawLine(a.brillo(0.3f, kb), Offset(b.left + 1f * u, b.top + 0.8f * u), Offset(b.right - 1f * u, b.top + 0.8f * u), 0.8f * u)
    }
}
