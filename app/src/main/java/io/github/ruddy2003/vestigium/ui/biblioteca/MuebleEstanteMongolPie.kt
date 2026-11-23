package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El pie del estante de Mongol y lo de los huecos: al pie, el cesto de mimbre con las tortas de boñiga seca y su horquilla
 * (cortado por la izquierda) y el hogar (cortado por abajo a la derecha): el aro de hierro con sus patas, la olla tiznada y el
 * fuego (de día brasa con su hilo de humo; de noche, llamas, la luz del cuarto). En los huecos de las repisas, cosas de la casa
 * según la repisa: el rollo de soga de cuero crudo y las tabas; la bolsa del pedernal con su eslabón y la piedra de afilar; el
 * cucharón de asta y un cuenco; las tijeras de esquilar y un ovillo.
 */
internal object PieEstanteMongol {

    // ---------------------------------------------------------------- los huecos

    /**
     * Lo que hay en el hueco libre de una repisa, de [ocupado] al costado: de izquierda a derecha, las cosas del grupo de esa
     * repisa que quepan (a escala de la repisa), cada una con su luz y su sombra.
     */
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, g: MarcoMongol) {
        val w = g.w
        var x = ocupado + w * 0.03f
        val x1 = g.x1 - w * 0.025f
        val pie = r.bottom - w * 0.008f
        val e = 1.45f
        // Cada repisa con su grupo: los fieltros, la soga y el cuenco de las tabas; el cuenco de cuajada, el pedernal y la piedra;
        // el ovillo, las tijeras y el cucharón.
        val grupo = Math.floorMod(k * 7 / 3, 3)
        val piezas = when (grupo) { 0 -> listOf(0, 1, 2); 1 -> listOf(3, 4, 5); else -> listOf(6, 8, 7) }
        val anchos = floatArrayOf(0.2f, 0.2f, 0.13f, 0.17f, 0.12f, 0.15f, 0.1f, 0.17f, 0.13f)
        for (p in piezas) {
            val ancho = w * anchos[p]
            if (x + ancho > x1) break
            val l = g.luz(Offset(x + ancho / 2, r.center.y))
            val cx = x + ancho / 2
            scale(e, e, Offset(cx, pie)) {
                when (p) {
                    0 -> fieltros(Offset(cx, pie), w, l, k)
                    1 -> soga(Offset(cx, pie), w * 0.12f, l)
                    2 -> cuencoTabas(Offset(cx - w * 0.01f, pie), w, l)
                    3 -> cuencoCuajada(Offset(cx, pie), w * 0.11f, l)
                    4 -> pedernal(Offset(cx, pie), w, l)
                    5 -> piedra(Offset(cx - w * 0.05f, pie), w, l)
                    6 -> ovillo(Offset(cx - w * 0.01f, pie), w * 0.03f, l)
                    7 -> scale(1.35f, 1.35f, Offset(cx, pie)) { tijeras(Offset(cx - w * 0.05f, pie), w, l) }
                    else -> cucharon(Offset(cx - w * 0.04f, pie), w, l)
                }
            }
            x += ancho + w * 0.025f
        }
    }

    /**
     * La pila de fieltros doblados: tres o cuatro paños (crudo, rojo de rubia, añil, pardo), cada uno con su doblez redondo a
     * la izquierda, su pelusa, su pespunte y su sombra en el de abajo; el de arriba con la punta caída.
     */
    private fun DrawScope.fieltros(pie: Offset, w: Float, l: LuzMongol, k: Int) {
        val u = density
        val an = w * 0.13f
        val gr = w * 0.019f
        val colores = listOf(P.CREMA, P.BERMELLON, P.ANIL, mezcla(P.CREMA, P.CRUDO, 0.5f))
        val n = 3 + (k % 2)
        contactoMongol(Offset(pie.x, pie.y), an * 1.1f, w * 0.012f, l)
        for (i in 0 until n) {
            val y1 = pie.y - gr * i
            val dx = (azar(940 + k, i) - 0.5f) * w * 0.012f
            val r = Rect(pie.x - an / 2 + dx, y1 - gr, pie.x + an / 2 + dx, y1)
            val p = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(gr * 0.5f, gr * 0.5f))) }
            val col = colores[(i + k) % colores.size]
            drawPath(p, Brush.verticalGradient(listOf(l.c(mezcla(col, P.CREMA, 0.12f)), l.c(col), l.c(mezcla(col, P.HOLLIN, 0.3f))), r.top, r.bottom))
            // El doblez de la izquierda (la vuelta del paño) y el pespunte.
            drawOval(l.b(0.25f), Offset(r.left, r.top + gr * 0.1f), Size(gr * 0.9f, gr * 0.8f))
            drawLine(l.s(0.35f), Offset(r.left + gr * 0.5f, r.top + gr * 0.5f), Offset(r.right - gr * 0.3f, r.top + gr * 0.5f), 0.7f * u)
            drawLine(l.c(mezcla(col, P.HOLLIN, 0.4f)).copy(alpha = 0.7f), Offset(r.left + gr * 0.8f, r.bottom - gr * 0.3f), Offset(r.right - gr * 0.4f, r.bottom - gr * 0.3f), 0.7f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.4f * u), 0f))
            drawRect(l.s(0.4f), Offset(r.left + gr * 0.3f, r.bottom - 0.9f * u), Size(r.width - gr * 0.3f, 0.9f * u))
        }
        // La punta del de arriba, caída por delante.
        val top = pie.y - gr * n
        val punta = poligonoMongol(pie.x + an * 0.1f, top + gr * 0.2f, pie.x + an * 0.42f, top + gr * 0.1f, pie.x + an * 0.38f, top + gr * 1.5f, pie.x + an * 0.18f, top + gr * 1.2f)
        drawPath(corridoMongol(punta, 1f * u, 1.2f * u), l.s(0.35f))
        drawPath(punta, l.c(colores[(n - 1 + k) % colores.size]))
    }

    /** El cuenco grande de madera colmado de cuajada fresca: los trozos blancos amontonados por encima del labio. */
    private fun DrawScope.cuencoCuajada(pie: Offset, an: Float, l: LuzMongol) {
        val u = density
        cuenco(pie, an, l)
        val al = an * 0.42f
        val boca = pie.y - al
        for (i in 0 until 9) {
            val fila = if (i < 5) 0 else 1
            val j = if (fila == 0) i else i - 5
            val n = if (fila == 0) 5 else 4
            val cx = pie.x - an * 0.38f + an * 0.76f * (j + 0.5f + 0.25f * fila) / (n + 0.5f * fila)
            val cy = boca - an * (0.02f + 0.07f * fila) - an * 0.03f * azar(950, i)
            val s = an * (0.13f + 0.04f * azar(951, i))
            val pz = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(cx - s / 2, cy - s * 0.6f, cx + s / 2, cy), CornerRadius(s * 0.3f))) }
            drawPath(corridoMongol(pz, s * 0.15f, s * 0.1f), l.s(0.3f))
            drawPath(pz, l.c(mezcla(P.CUAJADA, P.CRUDO, 0.1f + 0.15f * azar(952, i))))
            drawRect(l.b(0.5f), Offset(cx - s * 0.4f, cy - s * 0.6f), Size(s * 0.45f, s * 0.18f))
        }
        drawArc(l.b(0.55f), 180f, 110f, false, Offset(pie.x - an / 2, boca - an * 0.06f), Size(an, an * 0.12f), style = Stroke(1.2f * u))
    }

    /** El rollo de soga de cuero crudo trenzada, tumbado: las vueltas que se ven por delante y su cabo suelto. */
    private fun DrawScope.soga(pie: Offset, ancho: Float, l: LuzMongol) {
        val u = density
        val alto = ancho * 0.5f
        val c = Offset(pie.x, pie.y - alto / 2)
        contactoMongol(Offset(c.x, pie.y), ancho * 1.05f, ancho * 0.14f, l)
        for (i in 0 until 5) {
            val rx = ancho * (0.5f - 0.035f * i); val ry = alto * (0.5f - 0.05f * i)
            val rr = Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)
            drawOval(l.s(0.35f), rr.topLeft + Offset(1.2f * u, 1.4f * u), rr.size, style = Stroke(ancho * 0.07f))
            drawOval(l.c(mezcla(P.CRUDO, P.CUERO, 0.15f * (i % 2))), rr.topLeft, rr.size, style = Stroke(ancho * 0.065f))
            drawOval(l.c(P.CUERO).copy(alpha = 0.6f), rr.topLeft, rr.size, style = Stroke(ancho * 0.065f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 2.6f * u), i.toFloat())))
            drawArc(l.b(0.45f), 190f, 80f, false, rr.topLeft, rr.size, style = Stroke(ancho * 0.02f))
        }
        val cabo = Path().apply { moveTo(c.x + ancho * 0.45f, c.y + alto * 0.2f); cubicTo(c.x + ancho * 0.6f, c.y + alto * 0.4f, c.x + ancho * 0.55f, pie.y, c.x + ancho * 0.75f, pie.y - 1f * u) }
        drawPath(cabo, l.c(P.CRUDO), style = Stroke(ancho * 0.06f, cap = StrokeCap.Round))
    }

    /** El cuenco chico de madera donde se guardan las tabas (los huesos del tobillo de la oveja, de jugar), con una fuera. */
    private fun DrawScope.cuencoTabas(pie: Offset, w: Float, l: LuzMongol) {
        val an = w * 0.07f
        cuenco(pie, an, l)
        tabas(Offset(pie.x - an * 0.36f, pie.y - an * 0.4f), w, l, 3, an * 0.2f)
        tabas(Offset(pie.x + an * 0.62f, pie.y), w, l, 1, 0f)
    }

    /** [n] tabas: huesos del tobillo de la oveja, de los de jugar (cada uno con su forma de dado blando y su hoyuelo). */
    private fun DrawScope.tabas(pie: Offset, w: Float, l: LuzMongol, n: Int = 5, paso: Float = w * 0.026f) {
        val u = density
        for (i in 0 until n) {
            val x = pie.x + paso * i + w * 0.004f * azar(960, i)
            val s = w * (0.024f + 0.004f * azar(961, i))
            val y = pie.y - (if (n > 1 && i == 1) s * 0.45f else 0f)
            rotate((azar(962, i) - 0.5f) * 40f, Offset(x, y - s * 0.3f)) {
                val t = Path().apply {
                    moveTo(x - s * 0.5f, y); cubicTo(x - s * 0.6f, y - s * 0.4f, x - s * 0.3f, y - s * 0.55f, x - s * 0.05f, y - s * 0.45f)
                    cubicTo(x + s * 0.1f, y - s * 0.62f, x + s * 0.55f, y - s * 0.5f, x + s * 0.5f, y - s * 0.15f)
                    cubicTo(x + s * 0.48f, y, x + s * 0.2f, y + s * 0.02f, x, y - s * 0.06f); cubicTo(x - s * 0.2f, y + s * 0.03f, x - s * 0.4f, y + s * 0.03f, x - s * 0.5f, y); close()
                }
                drawPath(corridoMongol(t, s * 0.12f, s * 0.08f), l.s(0.35f))
                drawPath(t, l.c(mezcla(P.HUESO, P.CRUDO, 0.25f * azar(963, i))))
                drawArc(l.s(0.3f), 0f, 180f, false, Offset(x - s * 0.15f, y - s * 0.4f), Size(s * 0.3f, s * 0.2f), style = Stroke(0.8f * u))
                drawOval(l.b(0.5f), Offset(x - s * 0.4f, y - s * 0.45f), Size(s * 0.3f, s * 0.15f))
            }
        }
    }

    /** La bolsa del pedernal: cuero repujado con su solapa, el eslabón de acero en el canto de abajo y la tira de colgar. */
    private fun DrawScope.pedernal(pie: Offset, w: Float, l: LuzMongol) {
        val u = density
        val an = w * 0.08f; val al = an * 0.75f
        val b = Path().apply {
            moveTo(pie.x - an / 2, pie.y - al * 0.15f); cubicTo(pie.x - an * 0.52f, pie.y - al * 0.7f, pie.x - an * 0.4f, pie.y - al, pie.x - an * 0.1f, pie.y - al)
            lineTo(pie.x + an * 0.1f, pie.y - al); cubicTo(pie.x + an * 0.4f, pie.y - al, pie.x + an * 0.52f, pie.y - al * 0.7f, pie.x + an / 2, pie.y - al * 0.15f); close()
        }
        contactoMongol(Offset(pie.x, pie.y), an * 1.2f, w * 0.01f, l)
        drawPath(corridoMongol(b, w * 0.006f, w * 0.002f), l.s(0.3f))
        drawPath(b, l.c(P.CUERO))
        clipPath(b) {
            drawPath(Path().apply { moveTo(pie.x - an / 2, pie.y - al * 0.62f); quadraticTo(pie.x, pie.y - al * 0.3f, pie.x + an / 2, pie.y - al * 0.62f); lineTo(pie.x + an, pie.y - al * 1.2f); lineTo(pie.x - an, pie.y - al * 1.2f); close() },
                l.c(mezcla(P.CUERO, P.ALERCE_OSC, 0.35f)))
            drawPath(Path().apply { moveTo(pie.x - an * 0.4f, pie.y - al * 0.55f); quadraticTo(pie.x, pie.y - al * 0.26f, pie.x + an * 0.4f, pie.y - al * 0.55f) },
                l.c(P.CRUDO).copy(alpha = 0.6f), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.6f * u), 0f)))
            drawRect(l.b(0.25f), Offset(pie.x - an / 2, pie.y - al), Size(an * 0.25f, al))
        }
        // El eslabón: la pieza de acero en el canto de abajo, con su brillo frío.
        drawRoundRect(l.c(P.HIERRO), Offset(pie.x - an * 0.55f, pie.y - al * 0.2f), Size(an * 1.1f, al * 0.2f), CornerRadius(al * 0.08f))
        drawLine(l.b(0.7f), Offset(pie.x - an * 0.45f, pie.y - al * 0.17f), Offset(pie.x + an * 0.4f, pie.y - al * 0.17f), 0.9f * u)
        // La tira de colgar, caída sobre la tabla.
        drawPath(Path().apply { moveTo(pie.x, pie.y - al); cubicTo(pie.x + an * 0.3f, pie.y - al * 1.5f, pie.x + an * 0.9f, pie.y - al * 0.4f, pie.x + an * 0.8f, pie.y - 1f * u) },
            l.c(mezcla(P.CUERO, P.ALERCE_OSC, 0.3f)), style = Stroke(w * 0.005f, cap = StrokeCap.Round))
    }

    /** La piedra de afilar: un canto alargado y gris, gastado en el medio, con su agujero para el cordel. */
    private fun DrawScope.piedra(pie: Offset, w: Float, l: LuzMongol) {
        val u = density
        val an = w * 0.1f; val al = w * 0.024f
        val p = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(pie.x, pie.y - al, pie.x + an, pie.y), CornerRadius(al * 0.45f))) }
        contactoMongol(Offset(pie.x + an / 2, pie.y), an * 1.05f, w * 0.01f, l)
        drawPath(p, l.c(Color(0xFF6B6A6E)))
        clipPath(p) {
            drawRect(l.b(0.35f), Offset(pie.x, pie.y - al), Size(an, al * 0.3f))
            drawRect(l.s(0.3f), Offset(pie.x, pie.y - al * 0.3f), Size(an, al * 0.3f))
            drawOval(l.c(Color(0xFF85858A)), Offset(pie.x + an * 0.3f, pie.y - al * 0.95f), Size(an * 0.4f, al * 0.4f))
        }
        drawCircle(l.c(P.HOLLIN), al * 0.16f, Offset(pie.x + an * 0.12f, pie.y - al * 0.5f))
        drawPath(Path().apply { moveTo(pie.x + an * 0.12f, pie.y - al * 0.5f); cubicTo(pie.x - an * 0.1f, pie.y - al * 0.3f, pie.x - an * 0.05f, pie.y, pie.x - an * 0.2f, pie.y - 0.5f * u) },
            l.c(P.CRUDO), style = Stroke(1.2f * u))
    }

    /** Un cuenco de madera derecho, con su cara de luz, la veta y el labio. */
    private fun DrawScope.cuenco(pie: Offset, an: Float, l: LuzMongol) {
        val u = density
        val al = an * 0.42f
        val c = Path().apply {
            moveTo(pie.x - an / 2, pie.y - al); cubicTo(pie.x - an / 2, pie.y - al * 0.2f, pie.x - an * 0.2f, pie.y, pie.x - an * 0.15f, pie.y)
            lineTo(pie.x + an * 0.15f, pie.y); cubicTo(pie.x + an * 0.2f, pie.y, pie.x + an / 2, pie.y - al * 0.2f, pie.x + an / 2, pie.y - al); close()
        }
        contactoMongol(Offset(pie.x, pie.y), an * 0.9f, an * 0.12f, l)
        drawPath(c, Brush.horizontalGradient(listOf(l.c(P.ALERCE_LUZ), l.c(P.ALERCE), l.c(P.ALERCE_OSC)), pie.x - an / 2, pie.x + an / 2))
        clipPath(c) { vetas(c.getBounds(), l.c(P.ALERCE_OSC).copy(alpha = 0.35f), 970, 0.6f) }
        drawOval(l.c(P.ALERCE_OSC), Offset(pie.x - an / 2, pie.y - al - an * 0.06f), Size(an, an * 0.12f))
        drawOval(l.c(mezcla(P.ALERCE_OSC, P.HOLLIN, 0.4f)), Offset(pie.x - an * 0.44f, pie.y - al - an * 0.045f), Size(an * 0.88f, an * 0.08f))
        drawArc(l.b(0.6f), 180f, 120f, false, Offset(pie.x - an / 2, pie.y - al - an * 0.06f), Size(an, an * 0.12f), style = Stroke(1.2f * u))
    }

    /** El cucharón de asta: la cuchara honda de cuerno pulido y su mango, apoyado contra la pared del casillero. */
    private fun DrawScope.cucharon(pie: Offset, w: Float, l: LuzMongol) {
        val u = density
        val h0 = Offset(pie.x + w * 0.02f, pie.y - w * 0.012f)
        val h1 = Offset(pie.x + w * 0.1f, pie.y - w * 0.15f)
        drawLine(l.s(0.3f), h0 + Offset(w * 0.008f, 0f), h1 + Offset(w * 0.012f, w * 0.004f), w * 0.012f, StrokeCap.Round)
        drawLine(l.c(mezcla(P.CRUDO, P.CUERO, 0.4f)), h0, h1, w * 0.01f, StrokeCap.Round)
        drawLine(l.b(0.5f), h0 - Offset(w * 0.003f, 0f), h1 - Offset(w * 0.003f, 0f), w * 0.0025f)
        val cuch = Rect(pie.x - w * 0.012f, pie.y - w * 0.036f, pie.x + w * 0.05f, pie.y)
        drawOval(l.s(0.35f), cuch.topLeft + Offset(w * 0.006f, w * 0.003f), cuch.size)
        drawOval(l.c(mezcla(P.CRUDO, P.HOLLIN, 0.2f)), cuch.topLeft, cuch.size)
        drawOval(l.c(mezcla(P.CUERO, P.HOLLIN, 0.4f)), Offset(cuch.left + cuch.width * 0.12f, cuch.top + cuch.height * 0.12f), Size(cuch.width * 0.76f, cuch.height * 0.45f))
        drawArc(l.b(0.6f), 190f, 70f, false, cuch.topLeft, cuch.size, style = Stroke(1.2f * u))
    }

    /** Un ovillo de lana teñida de rubia, con sus vueltas cruzadas. */
    private fun DrawScope.ovillo(pie: Offset, r: Float, l: LuzMongol) {
        val u = density
        val c = Offset(pie.x, pie.y - r)
        contactoMongol(Offset(c.x, pie.y), r * 2.1f, r * 0.35f, l)
        drawCircle(l.c(mezcla(P.BERMELLON, P.CREMA, 0.2f)), r, c)
        clipPath(Path().apply { addOval(Rect(c, r)) }) {
            for (q in 0 until 7) rotate(q * 26f - 40f, c) { drawLine(l.c(P.BERMELLON_OSC).copy(alpha = 0.45f), Offset(c.x - r, c.y - r * 0.5f + q * r * 0.15f), Offset(c.x + r, c.y - r * 0.3f + q * r * 0.15f), 0.9f * u) }
            drawCircle(l.s(0.35f), r, c + Offset(r * 0.45f, r * 0.3f))
            drawCircle(l.b(0.35f), r * 0.4f, c - Offset(r * 0.35f, r * 0.35f))
        }
        drawPath(Path().apply { moveTo(c.x + r * 0.7f, c.y + r * 0.6f); cubicTo(c.x + r * 1.4f, pie.y, c.x + r * 1.8f, pie.y - r * 0.2f, c.x + r * 2.2f, pie.y - 0.5f * u) },
            l.c(mezcla(P.BERMELLON, P.CREMA, 0.2f)), style = Stroke(0.9f * u, cap = StrokeCap.Round))
    }

    /** Las tijeras de esquilar de hierro: dos hojas unidas por el arco del muelle, tumbadas. */
    private fun DrawScope.tijeras(pie: Offset, w: Float, l: LuzMongol) {
        val u = density
        val y = pie.y - w * 0.008f
        val muelle = Path().apply { moveTo(pie.x + w * 0.07f, y - w * 0.004f); cubicTo(pie.x + w * 0.11f, y - w * 0.03f, pie.x + w * 0.11f, y + w * 0.012f, pie.x + w * 0.07f, y + w * 0.002f) }
        contactoMongol(Offset(pie.x + w * 0.05f, pie.y), w * 0.13f, w * 0.01f, l)
        drawPath(corridoMongol(muelle, 1.2f * u, 1.4f * u), l.s(0.35f), style = Stroke(w * 0.005f))
        drawPath(muelle, l.c(P.HIERRO), style = Stroke(w * 0.005f, cap = StrokeCap.Round))
        listOf(-1f, 1f).forEach { s ->
            val hoja = poligonoMongol(pie.x, y + s * w * 0.001f, pie.x + w * 0.04f, y - w * 0.006f + s * w * 0.004f, pie.x + w * 0.072f, y - w * 0.004f + s * w * 0.002f,
                pie.x + w * 0.04f, y + w * 0.001f + s * w * 0.003f)
            drawPath(corridoMongol(hoja, 1.2f * u, 1.4f * u), l.s(0.35f))
            drawPath(hoja, l.c(if (s < 0) P.HIERRO_LUZ else P.HIERRO))
        }
        drawLine(l.b(0.7f), Offset(pie.x + w * 0.005f, y - w * 0.003f), Offset(pie.x + w * 0.04f, y - w * 0.008f), 0.8f * u)
    }

    // ---------------------------------------------------------------- al pie

    fun DrawScope.alPie(g: MarcoMongol) {
        cesto(g)
        hogar(g)
    }

    /**
     * El cesto de mimbre de la boñiga, a la izquierda y cortado por el borde: el tejido en hileras que se alternan, el aro del
     * borde, las tortas de boñiga seca colmándolo y la horquilla de madera apoyada.
     */
    private fun DrawScope.cesto(g: MarcoMongol) {
        val u = density
        val w = g.w; val h = g.h
        val pie = h - w * 0.02f
        val x0 = -w * 0.04f; val x1 = w * 0.2f
        val top = pie - w * 0.2f
        val l = g.luz(Offset(w * 0.08f, pie - w * 0.1f))
        val cuerpo = poligonoMongol(x0, top, x1, top, x1 - w * 0.02f, pie, x0 + w * 0.02f, pie)
        // De día, la sombra larga que el cesto y la horquilla echan en el sol de la puerta, hacia la derecha.
        if (!g.a.oscuro) clipPath(poligonoSolMongol(g)) {
            drawPath(poligonoMongol(x1 - w * 0.01f, top - w * 0.03f, x1 + w * 0.2f, top + w * 0.05f, x1 + w * 0.24f, pie, x1 - w * 0.02f, pie), l.s(0.32f))
            drawLine(l.s(0.3f), Offset(w * 0.24f, pie), Offset(w * 0.44f, pie - w * 0.12f), w * 0.012f, StrokeCap.Round)
        }
        drawOval(l.s(0.45f), Offset(x0, pie - w * 0.012f), Size(x1 - x0 + w * 0.06f, w * 0.024f))
        // La horquilla, detrás: dos dientes y su mango.
        val m0 = Offset(w * 0.24f, pie - w * 0.002f); val m1 = Offset(w * 0.12f, top - w * 0.2f)
        drawLine(l.s(0.3f), m0 + Offset(w * 0.012f, 0f), m1 + Offset(w * 0.02f, w * 0.01f), w * 0.01f)
        drawLine(l.c(P.ALERCE), m0, m1, w * 0.01f, StrokeCap.Round)
        drawLine(l.b(0.35f), m0 - Offset(w * 0.003f, 0f), m1 - Offset(w * 0.003f, 0f), w * 0.003f)
        // Las tortas de boñiga, colmando la boca.
        for (i in 0 until 7) {
            val cx = x0 + (x1 - x0) * (0.1f + 0.13f * i)
            val cy = top - w * (0.01f + 0.025f * azar(980, i))
            val rr = w * (0.04f + 0.012f * azar(981, i))
            rotate((azar(982, i) - 0.5f) * 50f, Offset(cx, cy)) {
                drawOval(l.s(0.35f), Offset(cx - rr + w * 0.004f, cy - rr * 0.35f + w * 0.004f), Size(rr * 2, rr * 0.7f))
                drawOval(l.c(mezcla(P.BONIGA, P.FIELTRO_GRIS, 0.3f * azar(983, i))), Offset(cx - rr, cy - rr * 0.35f), Size(rr * 2, rr * 0.7f))
                drawOval(l.b(0.25f), Offset(cx - rr * 0.8f, cy - rr * 0.33f), Size(rr * 1.2f, rr * 0.25f))
                drawLine(l.c(P.HOLLIN).copy(alpha = 0.3f), Offset(cx - rr * 0.5f, cy), Offset(cx + rr * 0.4f, cy + rr * 0.05f), 1f * u)
            }
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(l.c(P.MIMBRE), l.c(P.MIMBRE), l.c(mezcla(P.MIMBRE, P.ALERCE_OSC, 0.45f))), x0, x1))
        clipPath(cuerpo) {
            // El tejido de mimbre torcido: hileras de puntadas inclinadas, una hilera hacia un lado y la siguiente hacia el otro
            // (la espiga), cada puntada con su luz arriba y su hueco oscuro, y las varas que asoman cada tanto.
            val filas = 11
            val hh = (pie - top) / filas
            val claro = l.c(mezcla(P.MIMBRE, P.CREMA, 0.3f))
            val hueco = l.s(0.4f)
            for (f in 0 until filas) {
                val y = top + hh * f
                val s = if (f % 2 == 0) 1f else -1f
                val puntadas = Path()
                val huecos = Path()
                var x = x0 - w * 0.02f
                while (x < x1 + w * 0.02f) {
                    puntadas.moveTo(x - s * w * 0.008f, y + hh * 0.85f); puntadas.lineTo(x + s * w * 0.008f, y + hh * 0.15f)
                    huecos.moveTo(x + w * 0.011f - s * w * 0.008f, y + hh * 0.85f); huecos.lineTo(x + w * 0.011f + s * w * 0.008f, y + hh * 0.15f)
                    x += w * 0.022f
                }
                drawPath(huecos, hueco, style = Stroke(w * 0.004f, cap = StrokeCap.Round))
                drawPath(puntadas, claro, style = Stroke(w * 0.0095f, cap = StrokeCap.Round))
                drawPath(corridoMongol(puntadas, -w * 0.002f, -w * 0.002f), l.b(0.3f), style = Stroke(w * 0.0025f, cap = StrokeCap.Round))
            }
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, l.s(0.45f)), x0 + (x1 - x0) * 0.5f, x1), Offset(x0, top), Size(x1 - x0, pie - top))
        }
        // El aro del borde.
        drawRect(l.c(mezcla(P.MIMBRE, P.ALERCE_OSC, 0.25f)), Offset(x0, top - w * 0.008f), Size(x1 - x0, w * 0.016f))
        drawRect(l.b(0.4f), Offset(x0, top - w * 0.008f), Size(x1 - x0, 1.4f * u))
    }

    /**
     * El hogar, abajo a la derecha y cortado por el borde: el lecho de ceniza con las tortas de boñiga que arden, el aro de
     * hierro con sus tres patas, la olla tiznada encima con su tapa y su asa, y el fuego (de día, brasa bajo la ceniza y un hilo
     * de humo; de noche, las llamas que lamen el culo de la olla y su charco de luz; en los Sueños, brasas).
     */
    private fun DrawScope.hogar(g: MarcoMongol) {
        val u = density
        val w = g.w; val h = g.h
        val a = g.a
        val c = g.hogar
        val l = g.luz(Offset(c.x, c.y - w * 0.1f))
        val aroY = c.y - w * 0.03f
        val aro = Rect(c.x - w * 0.17f, aroY - w * 0.024f, c.x + w * 0.17f, aroY + w * 0.024f)
        // El charco de luz de la noche, antes que nada.
        if (a.oscuro) {
            mancha(Offset(c.x, c.y + w * 0.03f), w * 0.8f, a.fuego.copy(alpha = if (a.llamas) 0.2f else 0.1f))
            mancha(Offset(c.x, c.y + w * 0.03f), w * 0.38f, a.fuego.copy(alpha = if (a.llamas) 0.32f else 0.18f))
        } else if (a.noche) mancha(Offset(c.x, c.y + w * 0.03f), w * 0.32f, a.fuego.copy(alpha = 0.2f))
        // El lecho de ceniza y las tortas de boñiga que arden.
        val lecho = Offset(c.x, c.y + w * 0.065f)
        drawOval(l.c(mezcla(P.FIELTRO_GRIS, P.HOLLIN, 0.35f)), Offset(lecho.x - w * 0.2f, lecho.y - w * 0.03f), Size(w * 0.4f, w * 0.07f))
        drawOval(l.c(mezcla(P.CREMA, P.FIELTRO_GRIS, 0.6f)), Offset(lecho.x - w * 0.16f, lecho.y - w * 0.026f), Size(w * 0.3f, w * 0.03f))
        // Las patas de atrás.
        drawLine(l.c(P.HIERRO), Offset(c.x + w * 0.06f, aroY - w * 0.02f), Offset(c.x + w * 0.08f, lecho.y), w * 0.011f)
        val brasa = if (a.lumbre) P.BRASA else P.FUEGO
        for (i in 0 until 5) {
            val x = lecho.x + w * (-0.12f + 0.06f * i)
            val rr = w * (0.032f + 0.01f * azar(990, i))
            val y = lecho.y - w * 0.012f - w * 0.008f * (i % 2)
            drawOval(l.c(P.BONIGA), Offset(x - rr, y - rr * 0.32f), Size(rr * 2, rr * 0.68f))
            drawOval(brasa.copy(alpha = if (a.oscuro) 0.95f else if (a.noche) 0.75f else 0.55f), Offset(x - rr * 0.75f, y - rr * 0.3f), Size(rr * 1.5f, rr * 0.28f))
            drawOval(Color(0xFFFFD07A).copy(alpha = if (a.oscuro) 0.7f else 0.3f), Offset(x - rr * 0.3f, y - rr * 0.26f), Size(rr * 0.6f, rr * 0.12f))
        }
        if (a.llamas) llamas(Offset(lecho.x, lecho.y - w * 0.02f), w * 0.15f, w)
        else if (a.lumbre) llamas(Offset(lecho.x, lecho.y - w * 0.02f), w * 0.07f, w, brasa = true)
        // La olla de hierro tiznada sobre el aro: panzuda, con su tapa de madera y su asa.
        val ol = Rect(c.x - w * 0.115f, aroY - w * 0.12f, c.x + w * 0.115f, aroY + w * 0.015f)
        val olla = Path().apply {
            moveTo(ol.left + w * 0.012f, ol.top + w * 0.015f)
            cubicTo(ol.left - w * 0.012f, ol.top + ol.height * 0.55f, ol.left + w * 0.04f, ol.bottom, c.x, ol.bottom)
            cubicTo(ol.right - w * 0.04f, ol.bottom, ol.right + w * 0.012f, ol.top + ol.height * 0.55f, ol.right - w * 0.012f, ol.top + w * 0.015f)
            close()
        }
        // El asa, detrás.
        drawPath(Path().apply { moveTo(ol.left + w * 0.015f, ol.top + w * 0.02f); cubicTo(ol.left + w * 0.03f, ol.top - w * 0.09f, ol.right - w * 0.03f, ol.top - w * 0.09f, ol.right - w * 0.015f, ol.top + w * 0.02f) },
            l.c(P.HIERRO), style = Stroke(w * 0.006f))
        drawPath(olla, Brush.radialGradient(listOf(l.c(mezcla(P.HIERRO, P.HIERRO_LUZ, if (a.oscuro) 0.05f else 0.35f)), l.c(P.HIERRO), l.c(P.HOLLIN)),
            Offset(ol.left + ol.width * 0.32f, ol.top + ol.height * 0.3f), ol.width * 0.7f))
        clipPath(olla) {
            pinceladas(ol, l.c(P.HOLLIN), 991, 8, u, 0.35f)
            if (a.lumbre) {
                drawPath(bordeMongol(olla, 0f, -ol.height * 0.3f), a.fuego.copy(alpha = if (a.llamas) 0.6f else 0.35f))
                drawPath(bordeMongol(olla, 0f, -ol.height * 0.1f), Color(0xFFFFD890).copy(alpha = if (a.llamas) 0.55f else 0.25f))
            } else {
                drawRoundRect(luzDuraMongol(0.3f), Offset(ol.left + ol.width * 0.18f, ol.top + ol.height * 0.22f), Size(ol.width * 0.04f, ol.height * 0.42f), CornerRadius(ol.width * 0.02f))
                drawPath(bordeMongol(olla, ol.width * 0.04f, 0f), l.b(0.18f))
            }
        }
        // La tapa de madera con su asidero.
        val tapa = Rect(ol.left + w * 0.006f, ol.top - w * 0.006f, ol.right - w * 0.006f, ol.top + w * 0.026f)
        drawOval(l.c(P.ALERCE_OSC), tapa.topLeft, tapa.size)
        drawOval(l.c(mezcla(P.ALERCE, P.ALERCE_OSC, 0.3f)), Offset(tapa.left + w * 0.01f, tapa.top + w * 0.002f), Size(tapa.width - w * 0.02f, tapa.height * 0.7f))
        drawRoundRect(l.c(P.ALERCE), Offset(c.x - w * 0.02f, tapa.top - w * 0.008f), Size(w * 0.04f, w * 0.014f), CornerRadius(w * 0.006f))
        drawArc(l.b(0.45f), 190f, 80f, false, tapa.topLeft, tapa.size, style = Stroke(1.2f * u))
        // El aro de hierro y las dos patas de delante.
        drawOval(l.s(0.35f), aro.topLeft + Offset(0f, w * 0.006f), aro.size, style = Stroke(w * 0.012f))
        drawArc(l.c(P.HIERRO), 0f, 180f, false, aro.topLeft, aro.size, style = Stroke(w * 0.012f))
        drawArc(if (a.oscuro) l.filo(0.95f) else l.b(0.45f), 20f, 140f, false, Offset(aro.left, aro.top + w * 0.004f), aro.size, style = Stroke(w * 0.0035f))
        listOf(c.x - w * 0.15f to -1f, c.x + w * 0.13f to 1f).forEach { (x, s) ->
            val p0 = Offset(x, aroY + w * 0.018f)
            val p1 = Offset(x + s * w * 0.025f, h + 2f)
            drawLine(l.c(P.HIERRO), p0, p1, w * 0.013f, StrokeCap.Round)
            drawLine(if (a.oscuro) l.filo(0.75f) else l.b(0.35f), p0 + Offset(-s * w * 0.004f, 0f), p1 + Offset(-s * w * 0.004f, 0f), w * 0.0035f)
        }
        // El humo: de día, un hilo gris que sube y se deshace; de noche, alumbrado desde abajo.
        val humo = Path().apply {
            moveTo(c.x - w * 0.01f, tapa.top)
            cubicTo(c.x - w * 0.05f, tapa.top - w * 0.06f, c.x + w * 0.03f, tapa.top - w * 0.1f, c.x - w * 0.02f, tapa.top - w * 0.16f)
            if (a.oscuro) cubicTo(c.x - w * 0.06f, tapa.top - w * 0.22f, c.x + w * 0.02f, tapa.top - w * 0.28f, c.x - w * 0.04f, tapa.top - w * 0.36f)
        }
        val colHumo = if (a.oscuro) mezcla(a.fuego, P.FIELTRO_GRIS, 0.5f) else mezcla(P.CREMA, P.FIELTRO_GRIS, 0.4f)
        drawPath(humo, colHumo.copy(alpha = if (a.oscuro) 0.1f else 0.07f), style = Stroke(w * 0.04f, cap = StrokeCap.Round))
        drawPath(corridoMongol(humo, w * 0.012f, w * 0.01f), colHumo.copy(alpha = if (a.oscuro) 0.12f else 0.09f), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
    }

    /**
     * Las llamas del hogar: tres lenguas grandes y dos chicas, ladeadas y de alturas distintas, cada una en tres capas (el rojo
     * de fuera, el naranja y el corazón claro), y unas chispas que suben; con [brasa], solo la lumbre baja de las brasas.
     */
    private fun DrawScope.llamas(base: Offset, alto: Float, w: Float, brasa: Boolean = false) {
        val lenguas = listOf(-0.09f to 0.6f, -0.035f to 1f, 0.02f to 0.82f, 0.075f to 0.55f, 0.115f to 0.35f)
        val capas = listOf(P.FUEGO to 1f, P.LLAMA to 0.72f, Color(0xFFFFF0C2) to 0.42f)
        capas.forEachIndexed { ci, (col, f) ->
            lenguas.forEachIndexed { i, (dx, al0) ->
                val x = base.x + w * dx * (if (brasa) 0.85f else 1f)
                val al = alto * al0 * f * (0.85f + 0.3f * azar(995, i))
                val an = w * 0.055f * (0.5f + 0.5f * f) * (0.7f + 0.3f * al0)
                val ladeo = (azar(996, i) - 0.4f) * an * 0.8f
                val ll = Path().apply {
                    moveTo(x - an / 2, base.y)
                    cubicTo(x - an * 0.7f, base.y - al * 0.45f, x + ladeo - an * 0.2f, base.y - al * 0.7f, x + ladeo, base.y - al)
                    cubicTo(x + ladeo + an * 0.1f, base.y - al * 0.62f, x + an * 0.7f, base.y - al * 0.35f, x + an / 2, base.y)
                    close()
                }
                drawPath(ll, col.copy(alpha = if (brasa) 0.7f else 0.93f))
            }
            if (ci == 0) drawOval(P.FUEGO.copy(alpha = if (brasa) 0.5f else 0.8f), Offset(base.x - w * 0.12f, base.y - alto * 0.12f), Size(w * 0.24f, alto * 0.2f))
        }
        if (!brasa) for (i in 0 until 7) {
            drawCircle(Color(0xFFFFD890).copy(alpha = 0.85f), w * (0.0025f + 0.002f * azar(999, i)),
                Offset(base.x + w * (azar(997, i) - 0.5f) * 0.22f, base.y - alto * (1.05f + 0.9f * azar(998, i))))
        }
    }
}
