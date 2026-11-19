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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que está sujeto arriba del módulo de estiba de Espacio ([MuebleEstanteEspacio]): lo principal, el ventilador del aire a
 * medio arreglar, con su rejilla flotando atada; y lo que lo acompaña a distintas profundidades: el tubo con el trigo de la
 * granja y la cinta con los tornillos, detrás, en el acolchado; el rotulador al aire, delante; el pasamanos (cortado por el
 * borde) con los auriculares.
 */
internal object ArribaEstanteEspacio {
    private val P = PaletaEstanteEspacio

    /** Una tira de cinta gris pegada al acolchado, con los cuatro tornillos del ventilador pegados en ella para que no se vayan flotando. */
    fun DrawScope.cintaTornillos(o: Offset, largo: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        val alto = largo * 0.17f
        rotate(-5f, o + Offset(largo / 2, alto / 2)) {
            val r = Rect(o.x, o.y, o.x + largo, o.y + alto)
            val n = 7
            val tira = Path().apply {
                moveTo(r.left, r.top)
                for (j in 0..n) lineTo(r.left + r.width * j / n, r.top + (if (j % 2 == 0) 0f else 1.2f * u))
                lineTo(r.right + 1.4f * u, r.top + r.height * 0.3f); lineTo(r.right, r.top + r.height * 0.6f); lineTo(r.right + 1.2f * u, r.bottom)
                for (j in n downTo 0) lineTo(r.left + r.width * j / n, r.bottom - (if (j % 2 == 0) 0f else 1f * u))
                lineTo(r.left - 1.2f * u, r.top + r.height * 0.7f); lineTo(r.left + 0.6f * u, r.top + r.height * 0.4f); close()
            }
            translate(caida.x * 0.4f, caida.y * 0.4f) { drawPath(tira, a.sombra(0.25f)) }
            drawPath(tira, a.luz(mezcla(P.ACERO, P.TELA, 0.35f), l))
            drawRect(a.claro(0.35f), Offset(r.left, r.top + 0.6f * u), Size(r.width, 0.8f * u))
            for (j in 0 until 6) drawLine(a.sombra(0.12f), Offset(r.left + r.width * (0.1f + 0.15f * j), r.top), Offset(r.left + r.width * (0.12f + 0.15f * j), r.bottom), 0.5f * u)
            for (j in 0 until 4) {
                val p = Offset(r.left + r.width * (0.16f + 0.22f * j), r.center.y)
                val gt = 12f + 20f * azar(77, j)
                rotate(gt, p) {
                    drawLine(a.sombra(0.35f), p + Offset(1f * u, 1.2f * u), p + Offset(alto * 0.95f + 1f * u, 1.2f * u), 1.3f * u)
                    drawLine(a.luz(P.ACERO, l), p, p + Offset(alto * 0.95f, 0f), 1.3f * u)
                    for (m in 1 until 4) drawLine(a.sombra(0.35f), p + Offset(alto * 0.22f * m, -0.7f * u), p + Offset(alto * 0.22f * m + 0.6f * u, 0.7f * u), 0.4f * u)
                    drawCircle(a.luz(mezcla(P.ACERO, P.PAPEL, 0.3f), l), alto * 0.24f, p)
                    drawLine(a.luz(P.TINTA, l), p - Offset(0f, alto * 0.14f), p + Offset(0f, alto * 0.14f), 0.6f * u)
                }
            }
        }
    }

    /** El tubo transparente con una espiga de trigo enano de la granja, sujeto al acolchado con velcro y una goma. */
    fun DrawScope.trigo(pie: Offset, alto: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        val an = alto * 0.2f
        rotate(-7f, pie) {
            val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
            with(PiezasEstanteEspacio) { velcro(Rect(r.left - 2f * u, r.top + alto * 0.4f, r.right + 2f * u, r.top + alto * 0.55f), a, l, 751) }
            translate(caida.x * 0.5f, caida.y * 0.5f) { drawRoundRect(a.sombra(0.22f), r.topLeft, r.size, CornerRadius(an / 2)) }
            // Lo de dentro: el tallo, la hoja y la espiga con sus granos y sus barbas.
            val tallo = a.luz(mezcla(P.TRIGO, P.VERDE, 0.45f), l)
            drawLine(tallo, Offset(pie.x, r.bottom - an * 0.4f), Offset(pie.x - an * 0.05f, r.top + alto * 0.42f), 1.2f * u)
            drawPath(Path().apply { moveTo(pie.x, r.top + alto * 0.7f); quadraticTo(pie.x + an * 0.6f, r.top + alto * 0.5f, pie.x + an * 0.2f, r.top + alto * 0.36f); quadraticTo(pie.x + an * 0.2f, r.top + alto * 0.55f, pie.x, r.top + alto * 0.72f); close() }, a.luz(P.VERDE, l))
            val grano = a.luz(P.TRIGO, l)
            for (j in 0 until 7) {
                val y = r.top + alto * (0.16f + 0.035f * j)
                val s = if (j % 2 == 0) -1f else 1f
                drawOval(grano, Offset(pie.x - an * 0.05f + s * an * 0.1f - an * 0.12f, y), Size(an * 0.24f, alto * 0.05f))
                drawLine(a.luz(mezcla(P.TRIGO, P.PAPEL, 0.3f), l), Offset(pie.x + s * an * 0.12f, y), Offset(pie.x + s * an * 0.32f, y - alto * 0.07f), 0.5f * u)
            }
            drawOval(a.claro(0.35f), Offset(pie.x - an * 0.2f, r.top + alto * 0.17f), Size(an * 0.12f, alto * 0.1f))
            // El vidrio: tenue, con su luz a la izquierda y su canto.
            drawRoundRect(a.luz(mezcla(P.LUZ_FRIA, P.TELA_AZUL, 0.3f), l).copy(alpha = 0.22f), r.topLeft, r.size, CornerRadius(an / 2))
            drawRoundRect(a.luz(mezcla(P.LUZ_FRIA, P.ACERO, 0.4f), l).copy(alpha = 0.6f), r.topLeft, r.size, CornerRadius(an / 2), style = Stroke(0.8f * u))
            drawLine(Color.White.copy(alpha = if (a.oscuro) 0.2f else 0.55f), Offset(r.left + an * 0.22f, r.top + an * 0.6f), Offset(r.left + an * 0.22f, r.bottom - an * 0.6f), 1.2f * u)
            // El tapón de goma naranja y la goma que lo abraza.
            drawRoundRect(a.luz(P.NARANJA, l), Offset(r.left - 0.8f * u, r.top - alto * 0.04f), Size(an + 1.6f * u, alto * 0.09f), CornerRadius(1.5f * u))
            drawRect(a.claro(0.35f), Offset(r.left, r.top - alto * 0.03f), Size(an * 0.4f, 0.8f * u))
            drawRect(a.luz(P.GOMA, l), Offset(r.left - 1f * u, r.top + alto * 0.62f), Size(an + 2f * u, 2.2f * u))
        }
    }

    /** El pasamanos naranja con sus dos patas atornilladas al acolchado, gastado en el medio. */
    fun DrawScope.pasamanos(r: Rect, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        val radio = CornerRadius(r.height / 2)
        drawRoundRect(a.sombra(if (a.lumbre) 0.4f else 0.26f), r.topLeft + caida, r.size, radio)
        listOf(r.left + r.width * 0.08f, r.right - r.width * 0.12f).forEach { x ->
            drawRect(a.sombra(0.3f), Offset(x + 1.5f * u, r.top + 1f * u), Size(r.width * 0.05f, r.height * 1.5f))
            drawRect(a.luz(P.ACERO, l), Offset(x, r.top - r.height * 0.25f), Size(r.width * 0.05f, r.height * 1.5f))
        }
        drawRoundRect(a.luz(P.NARANJA, l), r.topLeft, r.size, radio)
        drawRoundRect(a.luz(mezcla(P.NARANJA, P.TELA, 0.45f), l), Offset(r.left + r.width * 0.35f, r.top), Size(r.width * 0.3f, r.height), CornerRadius(1f * u))
        drawRect(a.claro(0.45f), Offset(r.left + r.height * 0.5f, r.top + r.height * 0.2f), Size(r.width - r.height, 0.9f * u))
        drawRect(a.sombra(0.25f), Offset(r.left + r.height * 0.5f, r.bottom - 1.6f * u), Size(r.width - r.height, 1.2f * u))
        if (a.lumbre) drawRect(a.filo(0.5f * l.k), Offset(r.left + r.height * 0.5f, r.top), Size(r.width - r.height, 0.8f * u))
    }

    /** Los auriculares enganchados del pasamanos por la diadema, con las copas al aire y el cable que baja flotando hasta la tabla alta. */
    fun DrawScope.auriculares(gancho: Offset, an: Float, apoyo: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        val izq = Offset(gancho.x - an * 0.55f, gancho.y + an * 0.55f)
        val der = Offset(gancho.x + an * 0.22f, gancho.y + an * 0.72f)
        // El cable, detrás: de la copa izquierda baja en una curva floja hasta la tabla alta.
        val cable = Path().apply { moveTo(izq.x, izq.y + an * 0.2f); cubicTo(izq.x - an * 0.1f, izq.y + an * 0.6f, izq.x + an * 0.5f, apoyo - an * 0.5f, izq.x + an * 0.25f, apoyo) }
        drawPath(corridoEspacio(cable, caida.x, caida.y), a.sombra(0.22f), style = Stroke(1.4f * u))
        drawPath(cable, a.luz(P.GOMA, l), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        // La diadema: un arco que pasa por encima del pasamanos.
        val diadema = Path().apply { moveTo(izq.x, izq.y); cubicTo(izq.x - an * 0.05f, gancho.y - an * 0.35f, der.x + an * 0.25f, gancho.y - an * 0.4f, der.x, der.y) }
        drawPath(corridoEspacio(diadema, caida.x, caida.y), a.sombra(0.3f), style = Stroke(3.2f * u))
        drawPath(diadema, a.luz(mezcla(P.GOMA, P.ACERO, 0.3f), l), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        drawPath(corridoEspacio(diadema, -0.8f * u, -0.8f * u), a.claro(0.3f), style = Stroke(0.7f * u))
        // Las copas, cada una con su almohadilla de tela azul.
        listOf(izq to -20f, der to 28f).forEach { (c, g) ->
            rotate(g, c) {
                val cr = Rect(c.x - an * 0.17f, c.y - an * 0.06f, c.x + an * 0.17f, c.y + an * 0.32f)
                drawRoundRect(a.sombra(0.3f), cr.topLeft + caida, cr.size, CornerRadius(an * 0.12f))
                drawRoundRect(a.luz(P.GOMA, l), cr.topLeft, cr.size, CornerRadius(an * 0.12f))
                drawRoundRect(a.luz(mezcla(P.TELA_AZUL, P.TELA, 0.15f), l), Offset(cr.left + an * 0.04f, cr.top + an * 0.05f), Size(cr.width - an * 0.08f, cr.height - an * 0.1f), CornerRadius(an * 0.1f))
                drawOval(a.claro(0.25f), Offset(cr.left + an * 0.07f, cr.top + an * 0.08f), Size(an * 0.1f, an * 0.14f))
                drawRect(a.luz(P.NARANJA, l), Offset(cr.left + cr.width * 0.3f, cr.top - an * 0.03f), Size(cr.width * 0.4f, an * 0.05f))
            }
        }
    }


    // ---------------------------------------------------------------- el ventilador, lo principal de arriba

    /**
     * La rejilla redonda del ventilador, que flota arriba a la derecha atada con su cordón: un disco de alambre visto de
     * sesgo (los aros y los radios), con su sombra en el acolchado.
     */
    fun DrawScope.rejilla(c: Offset, r: Float, ancla: Offset, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        // El cordón, en una ese floja de ingravidez, del borde de la boca a la rejilla.
        val cordon = Path().apply {
            moveTo(ancla.x, ancla.y)
            cubicTo(ancla.x + r * 0.2f, ancla.y - r * 1.4f, c.x - r * 1.9f, c.y + r * 0.9f, c.x - r * 0.62f, c.y + r * 0.42f)
        }
        drawPath(cordon, a.luz(P.NARANJA, l), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        rotate(-24f, c) {
            val o = Rect(c.x - r * 0.72f, c.y - r, c.x + r * 0.72f, c.y + r)
            translate(caida.x * 1.8f, caida.y * 1.8f) { drawOval(a.sombra(if (a.lumbre) 0.35f else 0.2f), o.topLeft, o.size) }
            val alambre = a.luz(mezcla(P.ACERO, P.PAPEL, 0.25f), l)
            drawOval(alambre, o.topLeft, o.size, style = Stroke(2.2f * u))
            for (j in 1..4) {
                val q = o.deflate(o.width * 0.5f * j / 5.2f)
                drawOval(alambre, q.topLeft, q.size, style = Stroke(0.9f * u))
            }
            for (j in 0 until 4) {
                val g = Math.toRadians(45.0 * j).toFloat()
                drawLine(alambre, Offset(c.x + o.width * 0.5f * cos(g), c.y + o.height * 0.5f * sin(g)), Offset(c.x - o.width * 0.5f * cos(g), c.y - o.height * 0.5f * sin(g)), 0.9f * u)
            }
            drawCircle(alambre, r * 0.13f, c)
            drawArc(a.claro(0.5f), 190f, 70f, false, o.topLeft, o.size, style = Stroke(1f * u))
            if (a.lumbre) drawArc(a.filo(0.6f * l.k), -60f, 80f, false, o.topLeft, o.size, style = Stroke(1f * u))
        }
    }

    /**
     * El ventilador del aire, de recambio y a medio arreglar: un tambor de chapa azul de la nave, tumbado sobre la tabla alta
     * con su boca hacia nosotros y un poco hacia la izquierda, sujeto con dos correas naranjas. La brida delantera con sus
     * pernos (faltan los de la rejilla), la boca abierta con las siete aspas y el motor, las aletas del tambor, la brida de
     * atrás y el mazo de cables que sale por detrás y flota en lazos.
     */
    fun DrawScope.ventilador(c: Offset, r: Float, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio, caida: Offset) {
        val u = density
        val largo = r * 1.45f
        val sx = 0.7f
        val l = luz(c)
        val lt = luz(c + Offset(largo, -r))
        val chapa = mezcla(P.TELA_AZUL, P.ACERO, 0.22f)
        val cuerpo = Rect(c.x, c.y - r * 0.84f, c.x + largo, c.y + r * 0.84f)
        val atras = Rect(c.x + largo - r * 0.24f, c.y - r * 0.96f, c.x + largo + r * 0.24f, c.y + r * 0.96f)
        // La sombra del tambor en el acolchado y en la tabla.
        translate(caida.x, caida.y) {
            drawRect(a.sombra(if (a.lumbre) 0.4f else 0.22f), cuerpo.topLeft, cuerpo.size)
            drawOval(a.sombra(if (a.lumbre) 0.4f else 0.22f), atras.topLeft, atras.size)
        }
        contactoEspacio(Offset(c.x + largo * 0.5f, c.y + r), largo * 2.2f, r * 0.12f, a, 0.42f)
        // El mazo de cables, que sale de atrás y flota en dos lazos, con su conector suelto (detrás del tambor).
        val m0 = Offset(atras.center.x + r * 0.1f, atras.top + r * 0.3f)
        val mazo = Path().apply {
            moveTo(m0.x, m0.y)
            cubicTo(m0.x + r * 0.9f, m0.y - r * 0.5f, m0.x + r * 0.2f, m0.y - r * 1.3f, m0.x + r * 0.7f, m0.y - r * 1.2f)
            cubicTo(m0.x + r * 1.2f, m0.y - r * 1.1f, m0.x + r * 0.9f, m0.y - r * 0.4f, m0.x + r * 1.35f, m0.y - r * 0.55f)
        }
        listOf(P.ROJO to -1f, P.GOMA to 0f, P.TELA to 1f).forEach { (col, d) ->
            drawPath(corridoEspacio(mazo, d * u, d * 0.6f * u), a.luz(col, lt), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        }
        val fin = Offset(m0.x + r * 1.35f, m0.y - r * 0.55f)
        drawRoundRect(a.luz(mezcla(P.PAPEL, P.ACERO, 0.4f), lt), Offset(fin.x - 2f * u, fin.y - 3.5f * u), Size(6f * u, 7f * u), CornerRadius(1f * u))
        // La brida de atrás, que asoma por arriba y por abajo del tambor.
        drawOval(a.luz(mezcla(chapa, P.TINTA, 0.3f), l), atras.topLeft, atras.size)
        // El tambor: la chapa con su luz arriba, su sombra abajo y las aletas de enfriar.
        drawRect(a.luz(chapa, l), cuerpo.topLeft, cuerpo.size)
        drawRect(a.claro(0.3f), Offset(cuerpo.left, cuerpo.top + r * 0.12f), Size(largo, r * 0.22f))
        drawRect(a.sombra(0.28f), Offset(cuerpo.left, cuerpo.bottom - r * 0.4f), Size(largo, r * 0.4f))
        drawRect(a.sombra(0.2f), Offset(cuerpo.left, cuerpo.bottom - r * 0.15f), Size(largo, r * 0.15f))
        var fx = cuerpo.left + r * 0.18f
        while (fx < cuerpo.right - r * 0.06f) {
            drawLine(a.sombra(0.3f), Offset(fx, cuerpo.top + 1f * u), Offset(fx, cuerpo.bottom - 1f * u), 1.4f * u)
            drawLine(a.claro(0.25f), Offset(fx + 1.4f * u, cuerpo.top + 1f * u), Offset(fx + 1.4f * u, cuerpo.bottom - 1f * u), 0.7f * u)
            fx += maxOf(1f, r * 0.14f)
        }
        // La pintura gastada del tambor: el aluminio que asoma en el canto de arriba.
        drawRect(a.luz(mezcla(P.ACERO, P.PAPEL, 0.3f), l), Offset(cuerpo.left + largo * 0.3f, cuerpo.top), Size(largo * 0.25f, 1.4f * u))
        // Las dos correas naranjas que lo sujetan a la tabla, con su hebilla.
        listOf(0.6f, 0.88f).forEach { f ->
            val x = cuerpo.left + largo * f
            drawRect(a.sombra(0.3f), Offset(x + 1.5f * u, cuerpo.top - r * 0.06f), Size(r * 0.16f, r * 1.9f))
            drawRect(a.luz(P.NARANJA, l), Offset(x, cuerpo.top - r * 0.08f), Size(r * 0.16f, r * 1.92f))
            drawRect(a.claro(0.3f), Offset(x, cuerpo.top - r * 0.08f), Size(1f * u, r * 1.92f))
            drawLine(a.luz(mezcla(P.NARANJA, P.TINTA, 0.35f), l).copy(alpha = 0.5f), Offset(x + r * 0.04f, cuerpo.top), Offset(x + r * 0.04f, cuerpo.bottom + r * 0.1f), 0.5f * u)
            drawRoundRect(a.luz(P.ACERO, l), Offset(x - 1f * u, c.y + r * 0.2f), Size(r * 0.16f + 2f * u, r * 0.2f), CornerRadius(1f * u))
        }
        if (a.lumbre) drawRect(a.filo(0.55f * lt.k), Offset(cuerpo.left, cuerpo.top), Size(largo, 1.2f * u))
        // La brida de delante: un óvalo (el tambor mira un poco hacia la izquierda), con sus pernos y la boca abierta.
        val brida = Rect(c.x - r * sx, c.y - r, c.x + r * sx, c.y + r)
        drawOval(a.luz(mezcla(chapa, P.PAPEL, 0.25f), l), brida.topLeft, brida.size)
        drawArc(a.claro(0.5f), 160f, 120f, false, brida.topLeft, brida.size, style = Stroke(2f * u))
        drawArc(a.sombra(0.35f), -20f, 130f, false, brida.topLeft, brida.size, style = Stroke(2.4f * u))
        for (j in 0 until 8) {
            val g = Math.toRadians(45.0 * j + 22.5).toFloat()
            val q = Offset(c.x + r * sx * 0.88f * cos(g), c.y + r * 0.88f * sin(g))
            if (j % 2 == 0) remacheEspacio(q, 1.6f * u, a.luz(P.ACERO, l), a) else drawCircle(a.luz(P.TINTA, l), 1.3f * u, q)
        }
        val boca = brida.deflate(r * 0.17f)
        drawOval(a.luz(mezcla(P.GOMA, P.TINTA, 0.3f), LuzEstanteEspacio(l.k * 0.6f, l.f * 0.5f)), boca.topLeft, boca.size)
        drawArc(a.sombra(0.5f), 180f, 120f, false, boca.topLeft, boca.size, style = Stroke(3f * u))
        // Las siete aspas, curvas, y el motor en el centro con sus tres brazos.
        val ra = boca.height * 0.46f
        clipPath(Path().apply { addOval(boca) }) {
            for (j in 0 until 7) {
                val g = (Math.toRadians(360.0 / 7 * j + 10)).toFloat()
                val p0 = c + Offset(cos(g) * sx, sin(g)) * (ra * 0.3f)
                val p1 = c + Offset(cos(g + 0.55f) * sx, sin(g + 0.55f)) * ra
                val p2 = c + Offset(cos(g + 0.95f) * sx, sin(g + 0.95f)) * (ra * 0.92f)
                val p3 = c + Offset(cos(g + 0.5f) * sx, sin(g + 0.5f)) * (ra * 0.32f)
                val aspa = poligonoEspacio(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
                drawPath(aspa, a.luz(mezcla(P.ACERO, P.PAPEL, 0.15f), l))
                drawLine(a.claro(0.4f), p0, p1, 0.8f * u)
                drawLine(a.sombra(0.3f), p3, p2, 0.8f * u)
            }
        }
        drawOval(a.luz(P.GOMA, l), Offset(c.x - ra * 0.36f * sx, c.y - ra * 0.36f), Size(ra * 0.72f * sx, ra * 0.72f))
        drawOval(a.claro(0.35f), Offset(c.x - ra * 0.26f * sx, c.y - ra * 0.28f), Size(ra * 0.24f * sx, ra * 0.2f))
        drawCircle(a.luz(P.ROJO, l), ra * 0.08f, c + Offset(ra * 0.12f, ra * 0.12f))
        if (a.lumbre) drawArc(a.filo(0.6f * l.k), -80f, 110f, false, brida.topLeft, brida.size, style = Stroke(1.4f * u))
    }

    /** El rotulador al aire, atado a la tabla alta con su cordón en espiral, que hace una curva floja de ingravidez. */
    fun DrawScope.rotulador(base: Offset, c: Offset, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density; val w = size.width
        val largo = w * 0.085f
        val giro = -28f
        val dir = Offset(cos(Math.toRadians(giro.toDouble())).toFloat(), sin(Math.toRadians(giro.toDouble())).toFloat())
        val cola = c - dir * (largo / 2)
        val n = 18
        val q0 = base; val q3 = cola
        val q1 = Offset(base.x - w * 0.07f, base.y - w * 0.02f); val q2 = Offset(cola.x - w * 0.06f, cola.y + w * 0.08f)
        drawCircle(a.luz(P.ACERO, l), 2f * u, base)
        for (j in 0..n) {
            val t = j / n.toFloat(); val s = 1f - t
            val p = q0 * (s * s * s) + q1 * (3 * s * s * t) + q2 * (3 * s * t * t) + q3 * (t * t * t)
            drawOval(a.luz(mezcla(P.NARANJA, P.GOMA, 0.2f), l), Offset(p.x - 2.6f * u, p.y - 1.4f * u), Size(5.2f * u, 2.8f * u), style = Stroke(0.8f * u))
        }
        rotate(giro, c) {
            val cuerpo = Rect(c.x - largo / 2, c.y - largo * 0.08f, c.x + largo * 0.25f, c.y + largo * 0.08f)
            drawRoundRect(a.sombra(0.3f), cuerpo.topLeft + caida, Size(largo, cuerpo.height), CornerRadius(cuerpo.height / 2))
            drawRoundRect(a.luz(P.PAPEL, l), cuerpo.topLeft, cuerpo.size, CornerRadius(cuerpo.height / 2))
            drawRect(a.luz(P.TELA_AZUL, l), Offset(cuerpo.left + largo * 0.15f, cuerpo.top), Size(largo * 0.3f, cuerpo.height))
            val tapa = Rect(cuerpo.right - 1f * u, cuerpo.top - 0.4f * u, c.x + largo / 2, cuerpo.bottom + 0.4f * u)
            drawRoundRect(a.luz(P.ROJO, l), tapa.topLeft, tapa.size, CornerRadius(tapa.height / 2))
            drawRect(a.luz(P.ROJO, l), Offset(tapa.left + 2f * u, cuerpo.top - 1.6f * u), Size(tapa.width * 0.6f, 1.4f * u))
            drawRect(a.claro(0.4f), Offset(cuerpo.left + 2f * u, cuerpo.top + 0.6f * u), Size(largo * 0.7f, 0.8f * u))
            drawRect(a.sombra(0.2f), Offset(cuerpo.left + 2f * u, cuerpo.bottom - 1.2f * u), Size(largo * 0.95f, 1f * u))
        }
    }
}
