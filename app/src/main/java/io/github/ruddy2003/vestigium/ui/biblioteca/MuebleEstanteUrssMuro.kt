package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrss as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que cuelga en la pared de la estantería (en la corona guardada): el altavoz de la radio por cable (la radiotochka de cada
 * casa y cada club) colgado de su clavo, con su cordón; el pliego de prueba de la imprenta del club, clavado con chinchetas y
 * cortado por arriba; y la lámpara colgada del techo, de vidrio verde (la misma clase de lámpara que la de mesa del Ideario).
 */
internal object MuroEstanteUrss {

    fun DrawScope.colgado(g: MarcoUrss) {
        val w = g.w
        conPanosTodoUrss(g, 0.01f, Rect(0f, 0f, w, g.apoyo)) {
            pliego(g)
            altavoz(g)
            reloj(g)
        }
    }

    // ---------------------------------------------------------------- el reloj de pared

    /**
     * El reloj de pared del club: la caja redonda de madera con su canto, el aro de latón, la esfera crema con las rayas de las
     * horas (sin cifras), las dos agujas negras y el segundero rojo, y el cordón del clavo.
     */
    private fun DrawScope.reloj(g: MarcoUrss) {
        val w = g.w; val u = density
        val c = Offset(w * 0.37f, w * 0.085f)
        val r = w * 0.054f
        val l = g.luz(c, 0.012f)
        val caja = ovaloUrss(c, r, r)
        val sa = g.arrojada(0.5f, c.x)
        drawPath(corridoUrss(caja, sa.x, sa.y), l.s(0.26f))
        val clavo = Offset(c.x, c.y - r - w * 0.016f)
        drawLine(l.c(P.NEGRO).copy(alpha = 0.6f), clavo, Offset(c.x - r * 0.5f, c.y - r * 0.85f), 0.8f * u)
        drawLine(l.c(P.NEGRO).copy(alpha = 0.6f), clavo, Offset(c.x + r * 0.5f, c.y - r * 0.85f), 0.8f * u)
        tornilloUrss(clavo, 1.2f * u, l, 7840, P.METAL)
        cilindroUrss(caja, l.c(mezcla(P.ROBLE, P.ROBLE_OSC, 0.3f)), l, -1f, 0.3f)
        drawCircle(l.c(P.LATON), r * 0.84f, c)
        drawCircle(l.b(0.5f), r * 0.84f, c, style = Stroke(0.8f * u))
        drawCircle(l.c(mezcla(P.CARTULINA, P.CAL, 0.3f)), r * 0.76f, c)
        val rayas = Path()
        for (j in 0 until 12) {
            val a = j * 30f * 0.017453f
            val r0 = if (j % 3 == 0) r * 0.56f else r * 0.63f
            rayas.moveTo(c.x + kotlin.math.cos(a) * r0, c.y + kotlin.math.sin(a) * r0)
            rayas.lineTo(c.x + kotlin.math.cos(a) * r * 0.7f, c.y + kotlin.math.sin(a) * r * 0.7f)
        }
        drawPath(rayas, l.c(P.NEGRO), style = Stroke(1.2f * u))
        drawLine(l.c(P.NEGRO), c, c + Offset(-r * 0.3f, -r * 0.24f), 2f * u, StrokeCap.Round)
        drawLine(l.c(P.NEGRO), c, c + Offset(r * 0.12f, -r * 0.58f), 1.4f * u, StrokeCap.Round)
        drawLine(l.c(P.ROJO), c + Offset(0f, r * 0.12f), c + Offset(r * 0.42f, r * 0.36f), 0.8f * u, StrokeCap.Round)
        drawCircle(l.c(P.LATON), 1.6f * u, c)
        // El reflejo del vidrio, arriba a la izquierda.
        drawArc(l.b(0.5f), 200f, 60f, false, Offset(c.x - r * 0.66f, c.y - r * 0.66f), Size(r * 1.32f, r * 1.32f), style = Stroke(2f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- el altavoz de la radio por cable

    /**
     * El altavoz: una caja de baquelita parda con la tapa de arriba en arco, el frente de tela cruda tras cinco listones, el costado
     * de la derecha con su único botón (el del volumen), colgado de un clavo; el cordón trenzado baja por la pared hacia el enchufe,
     * que queda tapado por la estantería.
     */
    private fun DrawScope.altavoz(g: MarcoUrss) {
        val w = g.w; val u = density
        val r = Rect(w * 0.03f, w * 0.035f, w * 0.148f, w * 0.15f)
        val l = g.luz(r.center, 0.012f)
        val clavo = Offset(r.center.x, r.top - w * 0.012f)
        val caja = Path().apply {
            moveTo(r.left, r.bottom)
            lineTo(r.left, r.top + r.height * 0.22f)
            cubicTo(r.left + r.width / 3f, r.top - r.height * 0.007f, r.right - r.width / 3f, r.top - r.height * 0.007f, r.right, r.top + r.height * 0.22f)
            lineTo(r.right, r.bottom)
            close()
        }
        val lado = poligonoUrss(Offset(r.right, r.top + r.height * 0.22f), Offset(r.right + w * 0.014f, r.top + r.height * 0.3f), Offset(r.right + w * 0.014f, r.bottom - w * 0.004f), Offset(r.right, r.bottom))
        // La sombra en la pared y el cordón del clavo.
        drawPath(corridoUrss(caja, g.arrojada(0.6f, r.center.x).x, g.arrojada(0.6f, r.center.x).y), l.s(0.26f))
        drawLine(l.c(P.NEGRO).copy(alpha = 0.7f), clavo, Offset(r.left + r.width * 0.2f, r.top + r.height * 0.05f), 0.8f * u)
        drawLine(l.c(P.NEGRO).copy(alpha = 0.7f), clavo, Offset(r.right - r.width * 0.2f, r.top + r.height * 0.05f), 0.8f * u)
        tornilloUrss(clavo, 1.3f * u, l, 7801, P.METAL)
        // El cordón trenzado que baja por la pared, a la izquierda, hasta perderse detrás del proyector.
        val cordon = abiertaUrss(listOf(Offset(r.left + r.width * 0.2f, r.bottom - w * 0.004f), Offset(r.left + r.width * 0.1f, r.bottom + w * 0.03f),
            Offset(w * 0.04f, r.bottom + w * 0.1f), Offset(w * 0.028f, g.apoyo)))
        val lc = g.luz(w * 0.04f, r.bottom + w * 0.1f, 0.005f)
        translate(g.arrojada(0.25f, w * 0.04f).x, g.arrojada(0.25f, w * 0.04f).y) { drawPath(cordon, lc.s(0.3f), style = Stroke(2f * u)) }
        drawPath(cordon, lc.c(mezcla(P.PARQUET_OSC, P.NEGRO, 0.4f)), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawPath(cordon, lc.c(P.BRAMANTE).copy(alpha = 0.4f), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 2.2f * u))))
        // La caja de baquelita parda y su costado en sombra.
        val baquelita = mezcla(P.PARQUET_OSC, P.NEGRO, 0.45f)
        drawPath(lado, l.c(mezcla(baquelita, P.NEGRO, 0.4f)))
        drawPath(caja, l.c(baquelita))
        clipPath(caja) {
            translate(-w * 0.08f, -w * 0.006f) { drawPath(caja, l.b(0.18f)) }
            drawRect(l.b(0.5f), Offset(r.left + r.width * 0.06f, r.top), Size(1.2f * u, r.height))
        }
        drawPath(caja, l.b(0.4f), style = Stroke(1f * u))
        // El frente: la boca redonda del altavoz, con la tela cruda tensa detrás y los aros prensados del cono; la chapita de abajo.
        val cb = Offset(r.center.x, r.top + r.height * 0.52f)
        val rb = r.width * 0.36f
        drawCircle(l.b(0.35f), rb + 1.6f * u, cb - Offset(0.8f * u, 0.8f * u))
        drawCircle(l.s(0.45f), rb + 1.4f * u, cb + Offset(0.8f * u, 0.8f * u))
        drawCircle(l.c(mezcla(P.BRAMANTE, P.CARTON, 0.3f)), rb, cb)
        clipPath(ovaloUrss(cb, rb, rb)) {
            var yy = cb.y - rb; var j = 0
            while (yy < cb.y + rb && j < 60) { drawLine(l.s(0.1f), Offset(cb.x - rb, yy), Offset(cb.x + rb, yy), 0.5f * u); yy += 1.6f * u; j++ }
            drawCircle(l.s(0.3f), rb, cb + Offset(rb * 0.25f, rb * 0.25f), style = Stroke(rb * 0.3f))
        }
        drawRect(l.c(P.METAL), Offset(r.center.x - r.width * 0.16f, r.bottom - r.height * 0.1f), Size(r.width * 0.32f, r.height * 0.045f))
        // El único botón, en el costado: de lado, como un disco estriado.
        val bo = Offset(r.right + w * 0.011f, r.bottom - r.height * 0.3f)
        drawOval(l.c(P.NEGRO), Offset(bo.x - w * 0.004f, bo.y - w * 0.011f), Size(w * 0.01f, w * 0.022f))
        drawOval(l.b(0.4f), Offset(bo.x - w * 0.002f, bo.y - w * 0.008f), Size(w * 0.003f, w * 0.012f))
    }

    // ---------------------------------------------------------------- el pliego de prueba

    /**
     * El pliego de prueba de la imprenta del club, clavado con tres chinchetas y cortado por arriba: el papel de cartel crudo con sus
     * dobleces, un disco rojo grande, una banda negra a lo ancho con su filete rojo, un cuadro negro y renglones de bloques que no
     * se leen, y abajo la tira de control de la tinta (cuadros rojos, negros y grises), que es lo que lo hace prueba. La esquina de
     * abajo a la derecha se despega.
     */
    private fun DrawScope.pliego(g: MarcoUrss) {
        val w = g.w; val u = density
        val r = Rect(w * 0.7f, -w * 0.03f, w * 0.915f, w * 0.262f)
        val l = g.luz(r.center.x, r.top + r.height * 0.6f, 0.004f)
        rotate(-1.4f, r.center) {
            val papel = Path().apply { addRect(r) }
            val sa = g.arrojada(0.3f, r.center.x)
            drawPath(corridoUrss(papel, sa.x, sa.y), l.s(0.24f))
            val fondo = l.c(mezcla(P.CARTULINA, P.CARTON, 0.16f))
            drawRect(fondo, r.topLeft, r.size)
            val pw = r.width; val ph = r.height
            fun px(f: Float) = r.left + pw * f
            fun py(f: Float) = r.top + ph * f
            val negro = l.c(mezcla(P.NEGRO, P.OLEO_OSC, 0.08f))
            val rojo = l.c(P.ROJO)
            clipRect(r.left, r.top, r.right, r.bottom) {
                pinceladas2(r, if (g.a.oscuro) g.a.hondo else P.CARTON, 7820)
                // La banda roja ancha que cruza el pliego en diagonal, de abajo a la izquierda arriba a la derecha.
                drawPath(poligonoUrss(Offset(px(0f), py(0.62f)), Offset(px(1f), py(0.2f)), Offset(px(1f), py(0.4f)), Offset(px(0f), py(0.84f))), rojo)
                // El cuadro negro de arriba a la izquierda y las barras de la cabecera, de largo desigual, que no se leen.
                drawRect(negro, Offset(px(0.07f), py(0.08f)), Size(pw * 0.2f, pw * 0.2f))
                drawRect(negro, Offset(px(0.36f), py(0.1f)), Size(pw * 0.56f, ph * 0.065f))
                drawRect(negro, Offset(px(0.36f), py(0.2f)), Size(pw * 0.36f, ph * 0.045f))
                drawPath(bloquesUrss(Offset(px(0.07f), py(0.32f)), pw * 0.4f, pw * 0.022f, 7821, 3), negro)
                // El filete negro bajo la banda y los renglones del pie.
                drawPath(poligonoUrss(Offset(px(0f), py(0.88f)), Offset(px(1f), py(0.44f)), Offset(px(1f), py(0.465f)), Offset(px(0f), py(0.905f))), negro)
                drawPath(bloquesUrss(Offset(px(0.4f), py(0.82f)), pw * 0.52f, pw * 0.016f, 7822, 2), negro.copy(alpha = 0.8f))
                // La tira de control de la tinta, al pie.
                for (j in 0 until 8) {
                    val c = when (j % 4) { 0 -> rojo; 1 -> negro; 2 -> mezcla(fondo, negro, 0.45f); else -> mezcla(fondo, rojo, 0.5f) }
                    drawRect(c, Offset(px(0.06f) + j * pw * 0.065f, py(0.92f)), Size(pw * 0.055f, ph * 0.035f))
                }
                // Los dobleces del papel.
                drawRect(l.s(0.12f), Offset(r.left, py(0.5f)), Size(pw, 1f * u)); drawRect(l.b(0.3f), Offset(r.left, py(0.5f) + 1f * u), Size(pw, 0.8f * u))
                drawRect(l.s(0.12f), Offset(px(0.5f), r.top), Size(1f * u, ph)); drawRect(l.b(0.3f), Offset(px(0.5f) + 1f * u, r.top), Size(0.8f * u, ph))
                // La luz que resbala por el papel.
                drawRect(l.b(0.08f), r.topLeft, Size(pw * 0.5f, ph))
                drawRect(l.s(0.08f), Offset(px(0.8f), r.top), Size(pw * 0.2f, ph))
            }
            drawRect(l.c(P.CARTON).copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(0.8f * u))
            // La esquina que se despega: el dorso del papel y su sombra.
            val e = pw * 0.13f
            drawPath(poligonoUrss(Offset(r.right, r.bottom - e), Offset(r.right - e, r.bottom), Offset(r.right, r.bottom)), g.luz(r.right, r.bottom, 0f).c(P.CAL))
            drawPath(poligonoUrss(Offset(r.right - e * 1.15f, r.bottom), Offset(r.right, r.bottom - e * 1.15f), Offset(r.right - e * 0.8f, r.bottom - e * 0.6f)), l.s(0.22f))
            drawPath(poligonoUrss(Offset(r.right, r.bottom - e), Offset(r.right - e, r.bottom), Offset(r.right - e * 0.7f, r.bottom - e * 0.68f)), l.c(mezcla(P.CARTULINA, Color.White, 0.25f)))
            // Las chinchetas.
            listOf(Offset(px(0.05f), py(0.62f)), Offset(px(0.95f), py(0.18f)), Offset(px(0.06f), py(0.96f))).forEach { p ->
                drawCircle(l.s(0.4f), 2f * u, p + Offset(1.2f * u, 1.4f * u))
                drawCircle(l.c(P.METAL), 1.9f * u, p)
                drawCircle(l.b(0.8f), 0.7f * u, p - Offset(0.6f * u, 0.6f * u))
            }
        }
    }

    /** Lo amarillento del papel viejo: unas manchas largas y flojas. */
    private fun DrawScope.pinceladas2(r: Rect, color: Color, semilla: Int) {
        for (k in 0 until 9) {
            val c = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla + 1, k))
            drawOval(color.copy(alpha = 0.1f), c - Offset(r.width * 0.18f, r.height * 0.04f), Size(r.width * (0.2f + 0.2f * azar(semilla + 2, k)), r.height * 0.08f))
        }
    }

    // ---------------------------------------------------------------- la lámpara colgada

    /**
     * La lámpara colgada del techo: el cordón trenzado que baja de fuera del cuadro, el portalámparas de baquelita y la pantalla de
     * vidrio verde en campana, blanca por dentro, con la bombilla que asoma por debajo. De día, el vidrio con su brillo duro y su
     * sombra en la pared; de noche, encendida por dentro: verde en el vidrio, el borde cálido y la bombilla, lo más claro del cuadro.
     */
    fun DrawScope.lampara(g: MarcoUrss) {
        val w = g.w; val u = density
        val m = MuebleEstanteUrss
        val cx = w * m.LAMPARA_X
        val yb = w * m.BORDE_Y
        val ya = yb - w * 0.075f
        val an = w * 0.105f
        val l = g.luz(cx, ya, 0.06f)
        val enc = g.a.lampara > 0f
        val f = g.a.lampara
        val campana = Path().apply {
            moveTo(cx - an, yb)
            cubicTo(cx - an * 0.98f, yb - w * 0.03f, cx - an * 0.62f, ya + w * 0.004f, cx - an * 0.22f, ya)
            lineTo(cx + an * 0.22f, ya)
            cubicTo(cx + an * 0.62f, ya + w * 0.004f, cx + an * 0.98f, yb - w * 0.03f, cx + an, yb)
            cubicTo(cx + an * 0.5f, yb + w * 0.012f, cx - an * 0.5f, yb + w * 0.012f, cx - an, yb)
            close()
        }
        // De día, su sombra en la pared, abajo a la derecha.
        if (!enc) {
            val sa = g.arrojada(1.4f, cx)
            drawPath(corridoUrss(campana, sa.x, sa.y), l.s(0.2f))
            drawLine(l.s(0.18f), Offset(cx + sa.x, 0f), Offset(cx + sa.x, ya + sa.y), 1.6f * u)
        }
        // El cordón y el portalámparas.
        drawLine(l.c(mezcla(P.PARQUET_OSC, P.NEGRO, 0.5f)), Offset(cx, 0f), Offset(cx, ya - w * 0.016f), 2f * u)
        drawLine(l.c(P.BRAMANTE).copy(alpha = 0.35f), Offset(cx - 0.4f * u, 0f), Offset(cx - 0.4f * u, ya - w * 0.016f), 0.6f * u)
        val porta = Rect(cx - w * 0.012f, ya - w * 0.022f, cx + w * 0.012f, ya + w * 0.004f)
        drawRoundRect(l.c(P.NEGRO), porta.topLeft, porta.size, CornerRadius(w * 0.004f))
        drawRect(l.b(0.5f), Offset(porta.left + w * 0.003f, porta.top), Size(1.2f * u, porta.height))
        // La bombilla, que asoma por debajo del borde.
        val bc = Offset(cx, w * m.LAMPARA_Y)
        if (enc) {
            mancha(bc, w * 0.07f, P.LLAMA.copy(alpha = 0.55f * f))
            drawOval(P.LLAMA, Offset(bc.x - w * 0.02f, bc.y - w * 0.018f), Size(w * 0.04f, w * 0.03f))
            drawOval(Color(0xFFFFF7E2), Offset(bc.x - w * 0.013f, bc.y - w * 0.013f), Size(w * 0.026f, w * 0.019f))
        } else {
            drawOval(l.c(mezcla(P.CARTULINA, P.OLEO, 0.2f)), Offset(bc.x - w * 0.016f, bc.y - w * 0.018f), Size(w * 0.032f, w * 0.028f))
            drawOval(l.b(0.7f), Offset(bc.x - w * 0.01f, bc.y - w * 0.012f), Size(w * 0.008f, w * 0.008f))
        }
        // La campana de vidrio verde.
        if (!enc) {
            drawPath(campana, l.c(P.VERDE))
            clipPath(campana) {
                translate(-w * 0.14f, -w * 0.008f) { drawPath(campana, l.b(0.18f)) }
                translate(w * 0.12f, w * 0.004f) { drawPath(campana, l.s(0.28f)) }
                translate(0f, w * 0.06f) { drawPath(campana, mezcla(P.VERDE, P.VERDE_LUZ, 0.45f).copy(alpha = 0.3f)) }
                drawPath(Path().apply { moveTo(cx - an * 0.86f, yb - w * 0.024f); cubicTo(cx - an * 0.78f, yb - w * 0.05f, cx - an * 0.6f, ya + w * 0.012f, cx - an * 0.26f, ya + w * 0.008f) },
                    l.b(0.8f), style = Stroke(2f * u, cap = StrokeCap.Round))
            }
            // El borde: lo blanco de dentro que asoma.
            drawPath(Path().apply { moveTo(cx - an, yb); cubicTo(cx - an * 0.5f, yb + w * 0.012f, cx + an * 0.5f, yb + w * 0.012f, cx + an, yb) },
                l.c(mezcla(P.CARTULINA, P.VERDE, 0.2f)), style = Stroke(1.8f * u))
        } else {
            val lv = mezcla(P.VERDE, P.VERDE_LUZ, 0.25f + 0.3f * f)
            drawPath(campana, lv)
            clipPath(campana) {
                drawOval(P.VERDE_LUZ.copy(alpha = 0.5f * f), Offset(cx - an * 0.75f, yb - w * 0.06f), Size(an * 1.5f, w * 0.09f))
                drawOval(mezcla(P.VERDE_LUZ, P.LLAMA, 0.5f).copy(alpha = 0.55f * f), Offset(cx - an * 0.45f, yb - w * 0.03f), Size(an * 0.9f, w * 0.05f))
                drawRect(g.a.hondo.copy(alpha = 0.32f), Offset(cx - an, ya), Size(an * 2f, w * 0.018f))
                drawRect(g.a.hondo.copy(alpha = 0.28f), Offset(cx + an * 0.7f, ya), Size(an * 0.3f, w * 0.1f))
            }
            drawPath(Path().apply { moveTo(cx - an, yb); cubicTo(cx - an * 0.5f, yb + w * 0.012f, cx + an * 0.5f, yb + w * 0.012f, cx + an, yb) },
                Color(0xFFFFF4DA).copy(alpha = 0.6f + 0.4f * f), style = Stroke(2f * u))
        }
        // El aro de latón de arriba de la campana.
        drawRect(l.c(P.LATON), Offset(cx - an * 0.24f, ya - w * 0.004f), Size(an * 0.48f, w * 0.007f))
        drawRect(l.b(0.6f), Offset(cx - an * 0.24f, ya - w * 0.004f), Size(an * 0.48f, 1f * u))
    }
}
