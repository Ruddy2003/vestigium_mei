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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que acompaña en los huecos de las repisas de Frente (de lo que ocupan las entradas al costado derecho): grupos de dos o
 * tres cosas de la base, solapadas y grandes, que cambian de un hueco a otro: las pilas de latas de microfilm con su película;
 * la caja de válvulas de radio; el termo con su taza; el transmisor de mano con su batería; el frasco del revelador con su
 * cubeta. En un hueco muy ancho, dos grupos. En algunos huecos, atornillado bajo la tabla de arriba, el aplique rojo de cuarto
 * oscuro (de noche, la luz de su hueco).
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
     * grupos ya puestos en las repisas de arriba: cada grupo nuevo es el siguiente de los cinco, así no se repite ni de una
     * repisa a la siguiente ni dentro de un hueco. Si [xFoco] no es -1, ahí va el aplique rojo, atornillado bajo la tabla de
     * arriba, y el grupo de ese hueco queda debajo de su luz. Devuelve cuántos grupos puso.
     */
    fun DrawScope.hueco(n: Int, r: Rect, oc: Float, der: Float, xFoco: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float): Int {
        val w = size.width
        val x0 = oc + w * 0.018f; val x1 = der - w * 0.022f
        val libre = x1 - x0
        if (libre < w * 0.1f) return 0
        val h = r.height
        val piso = r.bottom - 2.5f * density
        val puestos: Int
        if (libre > w * 0.5f) {
            grupo(n % 5 + 1, x0 + libre * 0.25f, piso, h, libre * 0.44f, a, cerca, frio)
            grupo((n + 1) % 5 + 1, x0 + libre * 0.73f, piso, h, libre * 0.44f, a, cerca, frio)
            puestos = 2
        } else {
            grupo(n % 5 + 1, x0 + libre * 0.5f, piso, h, libre, a, cerca, frio)
            puestos = 1
        }
        if (xFoco >= 0f) aplique(Offset(xFoco, piso), r.top, h, a, cerca, frio)
        return puestos
    }

    /** Uno de los grupos (1 a 5) centrado en [cx], en [libre] de ancho como mucho. */
    private fun DrawScope.grupo(g: Int, cx: Float, piso: Float, h: Float, libre: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val s = minOf(1f, libre / (h * 0.75f)).coerceAtLeast(0.6f)
        when (g) {
            1 -> latas(cx, piso, h * s, a, cerca, frio)
            2 -> valvulas(cx, piso, h * s, a, cerca, frio)
            3 -> termo(cx, piso, h * s, a, cerca, frio)
            4 -> transmisor(cx, piso, h * s, a, cerca, frio)
            else -> revelado(cx, piso, h * s, a, cerca, frio)
        }
    }

    /** La sombra que deja algo de [r] en la piedra de atrás (la luz viene de la izquierda) y en la bandeja. */
    private fun DrawScope.sombras(r: Rect, a: AireFrente, hacia: Float = 1f) {
        val u = density
        drawRoundRect(a.sombra(0.32f), Offset(r.left + hacia * r.width * 0.22f + 2f * u, r.top + r.height * 0.1f), Size(r.width, r.height * 0.9f), CornerRadius(r.width * 0.2f))
        contactoFrente(Offset(r.center.x, r.bottom), r.width * 1.2f, 5f * u, a, 0.45f, hacia)
    }

    // ---------------------------------------------------------------- el aplique rojo de cuarto oscuro

    /**
     * El aplique rojo de cuarto oscuro (el foco de los nichos del Ideario), de mamparo: la base redonda de consola oscura
     * atornillada bajo la tabla de arriba ([techo]), el aro de cromo y la cúpula de vidrio rojo dentro de su jaula de alambre. De
     * día, el vidrio rojo oscuro y apagado, con su brillo; de noche, encendido, con su halo y su charco en la bandeja.
     */
    private fun DrawScope.aplique(base: Offset, techo: Float, h: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val enc = a.oscuro
        val c = Offset(base.x, base.y - h * M.FOCO_Y)
        val fuerza = if (a.noche) 0.6f else 1f
        val k = cerca(c); val kv = frio(c)
        // El charco rojo en la bandeja, debajo del aplique.
        if (enc) drawOval(Brush.radialGradient(listOf(P.FOCO.copy(alpha = 0.3f * fuerza), P.FOCO.copy(alpha = 0f)), Offset(c.x, base.y), h * 0.5f),
            Offset(c.x - h * 0.5f, base.y - h * 0.08f), Size(h, h * 0.16f))
        val rb = h * 0.1f
        val boca = techo + h * 0.07f
        // La base atornillada bajo la tabla, con sus dos tornillos, y el aro.
        val pie = Rect(c.x - rb * 1.35f, techo + 1f * u, c.x + rb * 1.35f, techo + h * 0.045f)
        drawRoundRect(a.sombra(0.4f), Offset(pie.left + 1.2f * u, pie.top + 1.6f * u), pie.size, CornerRadius(pie.height * 0.5f))
        drawRoundRect(Brush.verticalGradient(listOf(a.luz(P.CONSOLA_LUZ, k, kv), a.luz(P.CONSOLA, k, kv)), pie.top, pie.bottom), pie.topLeft, pie.size, CornerRadius(pie.height * 0.5f))
        listOf(pie.left + pie.width * 0.14f, pie.right - pie.width * 0.14f).forEachIndexed { i, x -> tornilloFrente(Offset(x, pie.center.y), 1f * u, a, k, 30f + 60f * i, kv) }
        val aro = Rect(c.x - rb * 1.05f, pie.bottom - 0.5f * u, c.x + rb * 1.05f, boca)
        drawRect(Brush.horizontalGradient(listOf(a.luz(P.CROMO_LUZ, k, kv), a.luz(P.CROMO, k, kv), a.luz(P.CROMO_OSC, k, kv)), aro.left, aro.right), aro.topLeft, aro.size)
        // La cúpula de vidrio rojo.
        val cup = Rect(c.x - rb, boca - 1f * u, c.x + rb, boca + rb * 1.55f)
        val domo = Path().apply {
            moveTo(cup.left, cup.top); lineTo(cup.right, cup.top)
            cubicTo(cup.right, cup.top + cup.height * 0.7f, cup.right - rb * 0.35f, cup.bottom, c.x, cup.bottom)
            cubicTo(cup.left + rb * 0.35f, cup.bottom, cup.left, cup.top + cup.height * 0.7f, cup.left, cup.top); close()
        }
        val cv = Offset(c.x, cup.top + cup.height * 0.5f)
        if (enc) {
            drawCircle(Brush.radialGradient(listOf(P.FOCO.copy(alpha = 0.4f * fuerza), P.FOCO.copy(alpha = 0.1f * fuerza), P.FOCO.copy(alpha = 0f)), cv, h * 0.42f), h * 0.42f, cv)
            drawPath(domo, Brush.radialGradient(listOf(Color(0xFFFFB08A), P.FOCO, Color(0xFF9A1A0E)), Offset(c.x - rb * 0.15f, cv.y), rb * 1.4f))
        } else {
            drawPath(domo, Brush.horizontalGradient(listOf(a.dia(Color(0xFF8E2418), 0f), a.dia(Color(0xFF5A120C), 0f), a.dia(Color(0xFF3A0A08), 0f)), cup.left, cup.right))
            drawOval(a.claro(0.55f), Offset(cup.left + rb * 0.3f, cup.top + cup.height * 0.18f), Size(rb * 0.36f, cup.height * 0.42f))
        }
        // La jaula de alambre: tres varillas que bajan por la cúpula y el aro de en medio.
        val alambre = if (enc) mezcla(a.luz(P.CROMO_OSC, k, kv), P.FOCO, 0.3f * fuerza) else a.dia(P.CROMO_OSC, 0f, k)
        listOf(-0.62f, 0f, 0.62f).forEach { f ->
            val p = Path().apply { moveTo(c.x + rb * f * 1.06f, cup.top); quadraticTo(c.x + rb * f * 1.25f, cup.top + cup.height * 0.75f, c.x + rb * f * 0.4f, cup.bottom + 1.4f * u) }
            drawPath(p, alambre, style = Stroke(1f * u, cap = StrokeCap.Round))
        }
        drawOval(alambre, Offset(cup.left - 1.2f * u, cv.y - rb * 0.18f), Size(rb * 2f + 2.4f * u, rb * 0.36f), style = Stroke(1f * u))
        drawCircle(alambre, 1.4f * u, Offset(c.x, cup.bottom + 1.4f * u))
    }

    // ---------------------------------------------------------------- el revelado

    /**
     * El revelado: el frasco pardo del revelador con su tapón y su etiqueta de cinta, alto, y delante la cubeta de esmalte con la
     * hoja asomando y una pinza de tender.
     */
    private fun DrawScope.revelado(cx: Float, piso: Float, h: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val fr = Rect(cx - h * 0.04f, piso - h * 0.76f, cx + h * 0.28f, piso)
        sombras(fr, a)
        val kf = cerca(fr.center); val vf = frio(fr.center)
        val vidrioPardo = Color(0xFF5A2E12)
        val cuello = Rect(fr.center.x - fr.width * 0.17f, fr.top, fr.center.x + fr.width * 0.17f, fr.top + h * 0.11f)
        drawRect(a.luz(vidrioPardo, kf, vf), cuello.topLeft, cuello.size)
        drawRoundRect(a.luz(P.BAQUELITA, kf, vf), Offset(cuello.left - 1.4f * u, cuello.top - h * 0.06f), Size(cuello.width + 2.8f * u, h * 0.07f), CornerRadius(1.5f * u))
        val cuerpo = Rect(fr.left, fr.top + h * 0.17f, fr.right, fr.bottom)
        // El cuerpo del frasco: los hombros y el vidrio por franjas (lisas y opacas).
        val hombros = Path().apply {
            moveTo(cuello.left, cuello.bottom); quadraticTo(cuerpo.left, cuello.bottom, cuerpo.left, cuerpo.top)
            lineTo(cuerpo.right, cuerpo.top); quadraticTo(cuerpo.right, cuello.bottom, cuello.right, cuello.bottom); close()
        }
        drawPath(hombros, a.luz(vidrioPardo, kf, vf))
        redondoFrente(cuerpo, a.luz(vidrioPardo, kf, vf), a.luz(mezcla(vidrioPardo, P.AMBAR, 0.35f), kf, vf), a.luz(mezcla(vidrioPardo, Color.Black, 0.5f), kf, vf), -1f)
        // La etiqueta de cinta de papel, sin letras: una franja de tinta.
        val e = Rect(cuerpo.left, cuerpo.top + cuerpo.height * 0.26f, cuerpo.right, cuerpo.top + cuerpo.height * 0.6f)
        drawRect(a.etiqueta(P.CINTA, kf, vf), e.topLeft, e.size)
        drawRect(a.luz(P.QUEMADO_TINTA, kf, vf), Offset(e.left, e.top + e.height * 0.2f), Size(e.width, e.height * 0.16f))
        if (a.oscuro) drawLine(a.filo(0.6f * kf), Offset(cuerpo.left + 0.8f * u, cuerpo.top + 3f * u), Offset(cuerpo.left + 0.8f * u, cuerpo.bottom - 3f * u), 1.3f * u)
        else drawLine(a.claro(0.55f), Offset(cuerpo.left + cuerpo.width * 0.2f, cuerpo.top + 3f * u), Offset(cuerpo.left + cuerpo.width * 0.2f, cuerpo.bottom - 4f * u), 1.2f * u)
        // La cubeta de revelar, delante a la izquierda, con su hoja y la pinza.
        val cu = Rect(cx - h * 0.4f, piso - h * 0.13f, cx + h * 0.12f, piso)
        contactoFrente(Offset(cu.center.x, piso), cu.width * 1.05f, 4f * u, a, 0.45f)
        val kc = cerca(cu.center); val vc = frio(cu.center)
        rotate(-6f, Offset(cu.left + cu.width * 0.3f, cu.top)) {
            drawRect(a.luz(P.PAPEL, kc, vc), Offset(cu.left + cu.width * 0.1f, cu.top - h * 0.07f), Size(cu.width * 0.46f, h * 0.09f))
            drawRect(a.sombra(0.3f), Offset(cu.left + cu.width * 0.1f, cu.top - h * 0.07f + h * 0.085f), Size(cu.width * 0.46f, 1f * u))
        }
        drawRoundRect(Brush.verticalGradient(listOf(a.luz(P.HUESO_LUZ, kc, vc), a.luz(P.HUESO_OSC, kc, vc)), cu.top, cu.bottom), cu.topLeft, cu.size, CornerRadius(2f * u))
        drawRect(a.luz(P.CONSOLA, kc, vc), cu.topLeft, Size(cu.width, 1.8f * u))
        drawLine(a.sombra(0.35f), Offset(cu.left + 2f * u, cu.top + cu.height * 0.55f), Offset(cu.right - 2f * u, cu.top + cu.height * 0.55f), 0.7f * u)
        val pz = Offset(cu.left + cu.width * 0.6f, cu.top - h * 0.03f)
        drawRect(a.luz(P.CROMO, kc, vc), Offset(pz.x - 1.4f * u, pz.y - h * 0.05f), Size(2.8f * u, h * 0.09f))
        drawRect(a.luz(P.CROMO_OSC, kc, vc), Offset(pz.x - 1.4f * u, pz.y - h * 0.05f), Size(2.8f * u, h * 0.09f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- las latas sueltas

    /**
     * Dos pilas de latas de microfilm acostadas, como en los casilleros del Ideario: la de atrás, alta, de cinco; la de delante,
     * de tres, tapándola a medias, con la lata de arriba abierta (el rollo a la vista y la tapa corrida) y la película colgando
     * por el canto de la bandeja.
     */
    private fun DrawScope.latas(cx: Float, piso: Float, h: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val ancho = h * 0.42f
        val alto = h * 0.15f
        val atras = listOf(LATAS[0], LATAS[3], LATAS[2], LATAS[0], LATAS[1])
        val delante = listOf(LATAS[2], LATAS[1], LATAS[3])
        val xa = cx - h * 0.15f; val xd = cx + h * 0.17f
        sombras(Rect(xa - ancho / 2, piso - alto * 5, xa + ancho / 2, piso), a)
        sombras(Rect(xd - ancho / 2, piso - alto * 3, xd + ancho / 2, piso), a)
        atras.forEachIndexed { i, c ->
            val dx = (azar(641, i) - 0.5f) * h * 0.05f
            lata(Rect(xa - ancho / 2 + dx, piso - (i + 1) * alto, xa + ancho / 2 + dx, piso - i * alto), c, if (i == 4) 1 else 0, a, cerca, frio)
        }
        delante.forEachIndexed { i, c ->
            val dx = (azar(643, i) - 0.5f) * h * 0.05f
            lata(Rect(xd - ancho / 2 + dx, piso - (i + 1) * alto, xd + ancho / 2 + dx, piso - i * alto), c, if (i == 2) 2 else 0, a, cerca, frio)
        }
        // La película que sale de la lata abierta y cuelga por el canto de la bandeja.
        val sale = Offset(xd + ancho * 0.3f, piso - alto * 3.1f)
        with(CoronaEstanteFrente) { tira(sale, Offset(sale.x + h * 0.16f, sale.y + h * 0.02f), Offset(sale.x + h * 0.12f, piso + h * 0.02f), Offset(sale.x + h * 0.06f, piso + h * 0.16f), 4.4f * u, a, cerca, frio) }
    }

    /** Los colores de las latas: aluminio, latón anodizado, rojo óxido y verde gris (los del Ideario). */
    private val LATAS = listOf(Color(0xFFB9B6AE), Color(0xFFC9A15A), Color(0xFF8E3A2C), Color(0xFF6E7F72))

    /**
     * Una lata de microfilm acostada en [r], vista un poco desde arriba: el canto de metal con su reborde y su etiqueta; arriba,
     * si [arriba] es 1, la tapa cerrada (un solo aro prensado, nada de dianas) y, si es 2, abierta, con el rollo y la tapa corrida.
     */
    private fun DrawScope.lata(r: Rect, color: Color, arriba: Int, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val k = cerca(r.center); val kv = frio(r.center)
        val c = en(color, 0f, r.center, a, cerca, frio)
        val elipse = r.height * 0.8f
        val metal = Brush.horizontalGradient(0f to mezcla(c, Color.Black, 0.4f), 0.22f to mezcla(c, Color.White, if (a.oscuro) 0.1f else 0.3f), 0.5f to c,
            1f to mezcla(c, Color.Black, 0.5f), startX = r.left, endX = r.right)
        drawRect(metal, r.topLeft, r.size)
        drawArc(metal, 0f, 180f, true, Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse))
        drawArc(a.sombra(0.5f), 0f, 180f, false, Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse), style = Stroke(0.8f * u))
        // El reborde de la tapa, a media altura del canto.
        drawArc(a.sombra(0.35f), 0f, 180f, false, Offset(r.left, r.top + r.height * 0.32f - elipse / 2), Size(r.width, elipse), style = Stroke(0.8f * u))
        drawArc(a.brillo(0.3f, k), 15f, 150f, false, Offset(r.left, r.top + r.height * 0.4f - elipse / 2), Size(r.width, elipse), style = Stroke(0.7f * u))
        if (color != LATAS[0]) {
            val e = Rect(r.center.x - r.width * 0.17f, r.top + r.height * 0.42f, r.center.x + r.width * 0.13f, r.bottom + elipse * 0.2f)
            drawRect(a.etiqueta(P.CINTA, k, kv), e.topLeft, e.size)
        }
        val boca = Rect(r.left, r.top - elipse / 2, r.right, r.top + elipse / 2)
        when (arriba) {
            1 -> {
                drawOval(Brush.linearGradient(listOf(mezcla(c, Color.White, if (a.oscuro) 0.12f else 0.35f), c, mezcla(c, Color.Black, 0.2f)), boca.topLeft, boca.bottomRight), boca.topLeft, boca.size)
                drawOval(a.sombra(0.22f), Offset(boca.left + boca.width * 0.2f, boca.top + boca.height * 0.2f), Size(boca.width * 0.6f, boca.height * 0.6f), style = Stroke(0.7f * u))
            }
            2 -> {
                drawOval(Color(0xFF0B0907), boca.topLeft, boca.size)
                val rollo = Rect(boca.left + boca.width * 0.1f, boca.top + boca.height * 0.14f, boca.right - boca.width * 0.1f, boca.bottom - boca.height * 0.06f)
                drawOval(a.luz(P.PELICULA, k, kv), rollo.topLeft, rollo.size)
                drawOval(a.luz(P.CROMO, k, kv), Offset(rollo.center.x - rollo.width * 0.12f, rollo.center.y - rollo.height * 0.12f), Size(rollo.width * 0.24f, rollo.height * 0.24f))
                // La tapa, corrida hacia atrás a la derecha, apoyada en la pila de atrás.
                val tp = boca.translate(r.width * 0.3f, -r.height * 0.5f)
                rotate(7f, tp.center) {
                    drawOval(a.sombra(0.3f), tp.translate(1.4f * u, 2f * u).topLeft, tp.size)
                    drawOval(Brush.linearGradient(listOf(mezcla(c, Color.White, if (a.oscuro) 0.12f else 0.35f), c, mezcla(c, Color.Black, 0.25f)), tp.topLeft, tp.bottomRight), tp.topLeft, tp.size)
                    drawOval(a.sombra(0.22f), Offset(tp.left + tp.width * 0.2f, tp.top + tp.height * 0.2f), Size(tp.width * 0.6f, tp.height * 0.6f), style = Stroke(0.7f * u))
                }
            }
        }
        if (a.oscuro && kv > 0.08f) drawRect(a.filoVidrio(0.6f * kv), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
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
            if (a.oscuro && i == 1 && !a.dormida) drawOval(P.AMBAR.copy(alpha = 0.3f), Offset(v.center.x - v.width * 0.18f, v.top + v.height * 0.36f), Size(v.width * 0.36f, v.height * 0.38f))
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
