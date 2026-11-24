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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que llena los huecos de las repisas donde no llegan las entradas: grupos de dos o tres cosas del cuarto, solapadas, que
 * llenan del 70 al 90 % del alto del hueco y cambian de un hueco a otro. El nido sobre una pila de libros tumbados; la tetera
 * rota con un helecho y la lupa; la maceta con su plantón y la paleta; la vela en su plato con las piñas; la calabaza seca con
 * las mazorcas. En un hueco muy ancho, dos grupos.
 */
internal object HuecosEstanteRuinas {
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, g: MarcoRuinas) {
        val w = g.w
        val x0 = ocupado + w * 0.012f
        val libre = r.right - x0 - w * 0.02f
        if (libre < w * 0.11f) return
        // El grupo de cada hueco sale de su repisa (es lo que acompaña, no una entrada): nido, maceta, vela, calabaza, tetera.
        // (La primera repisa y la sexta no repiten grupo: el paso salta uno cada cinco repisas.)
        val orden = intArrayOf(0, 2, 3, 4, 1)
        val i = Math.floorMod(2 * k + k / 5, 5)
        val base = orden[i]
        if (libre > w * 0.48f) {
            val mitad = libre * 0.5f
            grupo(base, Rect(x0, r.top, x0 + mitad, r.bottom), g)
            grupo(orden[Math.floorMod(i + 2, 5)], Rect(x0 + mitad + w * 0.01f, r.top, r.right - w * 0.02f, r.bottom), g)
        } else grupo(base, Rect(x0, r.top, x0 + libre, r.bottom), g)
    }

    private fun DrawScope.grupo(cual: Int, r: Rect, g: MarcoRuinas) {
        val w = g.w
        // Cada grupo se pone a la izquierda de su sitio, sin pasar de un ancho razonable.
        val an = minOf(r.width, w * 0.3f)
        val z = Rect(r.left, r.top, r.left + an, r.bottom)
        val l = g.luz(z.center.x, z.top + z.height * 0.55f, 0.05f, ParedEstanteRuinas.solEn(g, Offset(z.center.x, z.top + z.height * 0.4f), 0.05f))
        val hacia = if (g.a.oscuro && !g.a.noche) (if (z.center.x > g.w * 0.62f) 1f else -1f) else -1f
        when (cual) {
            0 -> nidoSobreLibros(z, l, hacia, g)
            1 -> teteraConHelecho(z, l, hacia, g)
            2 -> macetaConPlanton(z, l, hacia, g)
            3 -> velaConPinas(z, l, hacia, g)
            else -> calabazaConMazorcas(z, l, hacia, g)
        }
    }

    // ---------------------------------------------------------------- el nido sobre los libros

    private fun DrawScope.nidoSobreLibros(z: Rect, l: LuzRuinas, hacia: Float, g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = z.bottom
        val cx = z.left + minOf(z.width, w * 0.2f) * 0.5f
        val an = minOf(z.width * 0.95f, w * 0.17f)
        // La pila de libros tumbados: cuatro lomos de tela de colores apagados, desiguales y algo corridos.
        val telas = listOf(Color(0xFF6E3A2A), Color(0xFF3A5648), Color(0xFF8A7448), Color(0xFF4A4A5E))
        var y = pie
        contactoRuinas(cx, pie, an * 1.05f, 4f * u, l, hacia)
        for (i in 0 until 4) {
            val al = z.height * (0.12f + 0.03f * (i % 2))
            val dx = (azar(2001, i) - 0.5f) * an * 0.12f
            val lomo = Rect(cx - an * (0.5f - 0.04f * i) + dx, y - al, cx + an * (0.5f - 0.04f * i) + dx, y)
            val tela = l.c(telas[i])
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(tela, Color.White, 0.15f), tela, mezcla(tela, g.a.hondo, 0.4f)), lomo.top, lomo.bottom), lomo.topLeft, lomo.size, CornerRadius(al * 0.4f))
            // Las hojas a la vista del lado de la luz.
            drawRect(l.c(mezcla(P.PAPEL, P.KRAFT, 0.3f)), Offset(if (hacia < 0f) lomo.left else lomo.right - lomo.width * 0.1f, lomo.top + al * 0.15f), Size(lomo.width * 0.1f, al * 0.7f))
            drawLine(l.c(P.LATON).copy(alpha = 0.6f), Offset(lomo.left + lomo.width * 0.3f, lomo.center.y), Offset(lomo.left + lomo.width * 0.7f, lomo.center.y), 1f * u)
            drawLine(l.s(0.35f), Offset(lomo.left, lomo.bottom - 0.6f * u), Offset(lomo.right, lomo.bottom - 0.6f * u), 1f * u)
            if (l.k > 0.08f) drawLine(l.filo(0.7f), Offset(lomo.left, lomo.top + 1f), Offset(lomo.right, lomo.top + 1f), 1f * u)
            y -= al
        }
        // El nido: una taza de ramitas y hierba seca, con tres huevos moteados.
        val nc = Offset(cx, y - z.height * 0.07f)
        val nr = an * 0.42f; val nh = z.height * 0.14f
        val copa = Path().apply {
            moveTo(nc.x - nr, nc.y - nh * 0.2f)
            cubicTo(nc.x - nr * 0.9f, nc.y + nh * 0.75f, nc.x + nr * 0.9f, nc.y + nh * 0.75f, nc.x + nr, nc.y - nh * 0.2f)
            lineTo(nc.x - nr, nc.y - nh * 0.2f); close()
        }
        drawPath(corridoRuinas(copa, -hacia * 3f * u, 2f * u), l.s(0.35f))
        drawPath(copa, l.c(Color(0xFF7A6040)))
        // Las huevas, dentro de la boca, asomando.
        val boca = Rect(nc.x - nr * 0.85f, nc.y - nh * 0.55f, nc.x + nr * 0.85f, nc.y + nh * 0.05f)
        drawOval(l.c(Color(0xFF3E2E1E)), boca.topLeft, boca.size)
        for (i in 0 until 3) {
            val e = Offset(nc.x + nr * (-0.4f + 0.4f * i), nc.y - nh * (0.3f + 0.1f * (i % 2)))
            val huevo = Rect(e.x - nr * 0.2f, e.y - nh * 0.32f, e.x + nr * 0.2f, e.y + nh * 0.22f)
            drawOval(l.c(Color(0xFFB8D4C8)), huevo.topLeft, huevo.size)
            drawOval(l.s(0.25f), Offset(huevo.left + huevo.width * 0.45f, huevo.top), Size(huevo.width * 0.55f, huevo.height))
            drawCircle(l.b(0.6f), huevo.width * 0.12f, Offset(huevo.left + huevo.width * 0.3f, huevo.top + huevo.height * 0.3f))
            for (m in 0 until 4) drawCircle(l.c(Color(0xFF6A4A30)), 0.7f * u, Offset(huevo.left + huevo.width * azar(2010 + i, m), huevo.top + huevo.height * (0.3f + 0.5f * azar(2020 + i, m))))
        }
        // Las ramitas del borde, cruzadas.
        for (i in 0 until 14) {
            val t = i / 13f
            val p = Offset(nc.x - nr + 2 * nr * t, nc.y - nh * 0.15f + nh * 0.5f * 4f * t * (1 - t) * 0.4f)
            val ang = (azar(2030, i) - 0.5f) * 60f
            val d = giradoRuinas(Offset.Zero, nr * 0.35f, 0f, ang)
            drawLine(l.c(if (i % 3 == 0) Color(0xFFA08458) else Color(0xFF5A4430)), p - d, p + d, 1f * u, StrokeCap.Round)
        }
        drawPath(bordeRuinas(copa, hacia * nr * 0.1f, 0f), l.s(0.25f))
        // Una pluma caída junto a la pila.
        val pl = Offset(cx + an * 0.55f, pie - 1.5f * u)
        rotate(-14f, pl) {
            drawOval(l.c(Color(0xFF8A8478)), Offset(pl.x - an * 0.22f, pl.y - 2.6f * u), Size(an * 0.22f, 4f * u))
            drawLine(l.c(P.YESO_BLANCO), Offset(pl.x - an * 0.24f, pl.y - 0.6f * u), pl, 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- la tetera rota con el helecho

    private fun DrawScope.teteraConHelecho(z: Rect, l: LuzRuinas, hacia: Float, g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.7f, w * 0.12f)
        val cx = z.left + an * 0.65f
        val al = z.height * 0.4f
        // El helecho que crece en la tetera (detrás de su boca): frondas altas que llenan el hueco.
        val boca = Offset(cx, pie - al * 0.98f)
        listOf(-40f to 0.5f, -16f to 0.62f, 6f to 0.66f, 26f to 0.56f, 48f to 0.4f).forEachIndexed { i, (ang, lar) ->
            frondaRuinas(boca, z.height * lar, ang, 0.32f * (if (ang < 0f) -1f else 1f), if (i % 2 == 0) P.HELECHO else mezcla(P.HELECHO, P.HIEDRA, 0.45f), l, hacia, 2101 + i, 9)
        }
        // La tetera de esmalte blanco con sus saltaduras, la tapa perdida y el pico.
        val cuerpo = cerradaRuinas(listOf(Offset(cx - an * 0.32f, pie - al * 0.95f), Offset(cx + an * 0.32f, pie - al * 0.95f), Offset(cx + an * 0.5f, pie - al * 0.5f),
            Offset(cx + an * 0.44f, pie - 1f * u), Offset(cx - an * 0.44f, pie - 1f * u), Offset(cx - an * 0.5f, pie - al * 0.5f)))
        val pico = Path().apply { moveTo(cx + an * 0.42f, pie - al * 0.45f); cubicTo(cx + an * 0.7f, pie - al * 0.5f, cx + an * 0.72f, pie - al * 0.8f, cx + an * 0.86f, pie - al * 0.9f); lineTo(cx + an * 0.8f, pie - al * 0.72f); cubicTo(cx + an * 0.66f, pie - al * 0.55f, cx + an * 0.6f, pie - al * 0.3f, cx + an * 0.4f, pie - al * 0.25f); close() }
        val asa = Path().apply { moveTo(cx - an * 0.4f, pie - al * 0.8f); cubicTo(cx - an * 0.85f, pie - al * 0.85f, cx - an * 0.85f, pie - al * 0.25f, cx - an * 0.42f, pie - al * 0.3f) }
        drawPath(corridoRuinas(cuerpo, -hacia * 4f * u, 2.6f * u), l.s(0.35f))
        contactoRuinas(cx, pie, an, 4f * u, l, hacia)
        drawPath(asa, l.c(P.ESMALTE), style = Stroke(an * 0.08f, cap = StrokeCap.Round))
        drawPath(pico, l.c(P.ESMALTE_BLANCO))
        modeladoRapidoRuinas(cuerpo, l.c(P.ESMALTE_BLANCO), l, hacia, 1f, 0.4f)
        drawRect(l.c(P.ESMALTE), Offset(cx - an * 0.34f, pie - al * 0.97f), Size(an * 0.68f, al * 0.07f))
        // Una saltadura del esmalte, junto al pico (una sola, para que la tetera no tenga cara).
        drawPath(bultoRuinas(Offset(cx + an * 0.3f, pie - al * 0.72f), an * 0.06f, al * 0.05f, 2110, 7, 0.3f), l.c(P.HIERRO))
        // La lupa apoyada contra la tetera: el aro de latón, el vidrio con su reflejo y el mango de madera.
        val lc = Offset(z.left + an * 1.18f, pie - z.height * 0.18f)
        if (lc.x + an * 0.2f < z.right + w * 0.04f) {
            val rr = z.height * 0.12f
            rotate(18f, lc) {
                drawRoundRect(l.c(P.ROBLE_OSC), Offset(lc.x - rr * 0.18f, lc.y + rr * 0.9f), Size(rr * 0.36f, rr * 1.4f), CornerRadius(rr * 0.15f))
                drawCircle(l.s(0.3f), rr * 1.05f, lc + Offset(-hacia * 2f * u, 2f * u))
                drawCircle(mezcla(l.c(P.VIDRIO_CLARO), l.c(P.ROBLE_OSC), 0.5f).copy(alpha = 0.7f), rr * 0.86f, lc)
                drawCircle(l.c(P.LATON), rr, lc, style = Stroke(rr * 0.2f))
                drawArc(l.b(0.8f), 200f, 60f, false, Offset(lc.x - rr * 0.6f, lc.y - rr * 0.6f), Size(rr * 1.2f, rr * 1.2f), style = Stroke(1.2f * u))
            }
        }
    }

    // ---------------------------------------------------------------- la maceta con el plantón

    private fun DrawScope.macetaConPlanton(z: Rect, l: LuzRuinas, hacia: Float, g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.6f, w * 0.1f)
        val cx = z.left + an * 0.6f
        val al = z.height * 0.36f
        // El plantón de tomatera (un tallo con hojas compuestas) atado a una caña.
        val tope = pie - z.height * 0.88f
        drawLine(l.c(mezcla(P.KRAFT, P.GASTADO, 0.3f)), Offset(cx + an * 0.12f, pie - al * 0.8f), Offset(cx + an * 0.14f, tope), 1.6f * u)
        val tallo = abiertaRuinas(listOf(Offset(cx, pie - al * 0.85f), Offset(cx - an * 0.06f, pie - al * 1.5f), Offset(cx + an * 0.04f, pie - al * 2f), Offset(cx + an * 0.02f, tope + z.height * 0.04f)))
        drawPath(tallo, l.c(P.HIEDRA), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        for (i in 0 until 6) {
            val t = 0.2f + i * 0.14f
            val p = Offset(cx + an * 0.02f * (i % 2), pie - al * 0.85f - (pie - al * 0.85f - tope) * t)
            val lado = if (i % 2 == 0) -1f else 1f
            for (j in 0 until 3) {
                val q = p + Offset(lado * an * (0.18f + 0.14f * j), -an * 0.06f * j + an * 0.04f)
                drawPath(bultoRuinas(q, an * 0.1f, an * 0.07f, 2201 + i * 3 + j, 7, 0.25f), l.c(if (j % 2 == 0) P.HIEDRA_LUZ else P.HELECHO))
            }
            drawLine(l.c(P.HIEDRA), p, p + Offset(lado * an * 0.45f, -an * 0.06f), 0.9f * u)
        }
        // Dos lazadas de bramante a la caña.
        for (i in 0 until 2) drawCircle(l.c(P.BRAMANTE), 1.6f * u, Offset(cx + an * 0.1f, pie - al * (1.4f + 0.5f * i)), style = Stroke(1f * u))
        // La maceta de barro y su plato.
        val forma = poligonoRuinas(Offset(cx - an * 0.4f, pie - 2f * u), Offset(cx + an * 0.4f, pie - 2f * u), Offset(cx + an * 0.5f, pie - al * 0.85f), Offset(cx - an * 0.5f, pie - al * 0.85f))
        drawPath(corridoRuinas(forma, -hacia * 4f * u, 2.6f * u), l.s(0.35f))
        drawRoundRect(l.c(P.BARRO_OSC), Offset(cx - an * 0.52f, pie - 3f * u), Size(an * 1.04f, 3f * u), CornerRadius(1.5f * u))
        modeladoRapidoRuinas(forma, l.c(P.BARRO), l, hacia, 1f, 0.3f)
        drawRect(l.c(mezcla(P.BARRO, P.OCRE, 0.25f)), Offset(cx - an * 0.54f, pie - al * 0.88f), Size(an * 1.08f, al * 0.16f))
        drawLine(l.b(0.4f), Offset(cx - an * 0.54f, pie - al * 0.87f), Offset(cx + an * 0.54f, pie - al * 0.87f), 1f * u)
        drawRect(l.c(P.YESO_BLANCO).copy(alpha = 0.25f), Offset(cx - an * 0.35f, pie - al * 0.35f), Size(an * 0.7f, al * 0.1f))
        // La paleta de jardín apoyada, con su hoja de hierro y el mango de madera.
        val pc = Offset(cx + an * 0.85f, pie - 2f * u)
        if (pc.x < z.right + w * 0.05f) rotate(-24f, pc) {
            drawRoundRect(l.c(P.ROBLE), Offset(pc.x - 2.2f * u, pc.y - z.height * 0.5f), Size(4.4f * u, z.height * 0.2f), CornerRadius(2f * u))
            val hoja = cerradaRuinas(listOf(Offset(pc.x, pc.y - z.height * 0.3f), Offset(pc.x + an * 0.18f, pc.y - z.height * 0.2f), Offset(pc.x, pc.y), Offset(pc.x - an * 0.18f, pc.y - z.height * 0.2f)))
            drawPath(hoja, l.c(P.HIERRO_LUZ))
            drawPath(bordeRuinas(hoja, -hacia * 2f * u, 0f), l.b(0.4f))
            drawRect(l.c(P.TIERRA).copy(alpha = 0.6f), Offset(pc.x - an * 0.1f, pc.y - z.height * 0.08f), Size(an * 0.2f, z.height * 0.06f))
        }
    }

    // ---------------------------------------------------------------- la vela con las piñas

    private fun DrawScope.velaConPinas(z: Rect, l: LuzRuinas, hacia: Float, g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.5f, w * 0.09f)
        val cx = z.left + an * 0.7f
        // Detrás, un manojo de espigas secas atado con bramante, apoyado en la trasera: llega casi a la tabla de arriba.
        val pieM = Offset(cx - an * 0.35f, pie - 2f * u)
        for (k in 0 until 9) {
            val tope = Offset(cx - an * 0.9f + an * 0.16f * k, pie - z.height * (0.78f + 0.08f * azar(2140, k)))
            drawLine(l.c(mezcla(P.KRAFT, P.HELECHO, 0.25f)), pieM, tope, 0.9f * u)
            val dir = (tope - pieM) / (tope - pieM).getDistance()
            for (j in 0 until 4) {
                val p = tope - dir * (j * 4.2f * u)
                drawOval(l.c(mezcla(P.OCRE, P.KRAFT, 0.35f + 0.1f * (k % 2))), Offset(p.x - 1.6f * u, p.y - 2.6f * u), Size(3.2f * u, 5.2f * u))
            }
        }
        drawLine(l.c(P.BRAMANTE), Offset(pieM.x - 4f * u, pie - z.height * 0.3f), Offset(pieM.x + 4f * u, pie - z.height * 0.3f), 2f * u)
        // La vela gastada en su palmatoria de esmalte, con su cera chorreada; de noche, encendida.
        val vela = Rect(cx - an * 0.16f, pie - z.height * 0.46f, cx + an * 0.16f, pie - 6f * u)
        val arde = g.a.oscuro
        if (arde) {
            mancha(Offset(cx, vela.top - z.height * 0.1f), z.height * 0.55f, P.LLAMA.copy(alpha = 0.32f))
        } else drawRect(l.s(0.3f), vela.topLeft + Offset(-hacia * 5f * u, 3f * u), vela.size)
        drawRect(Brush.horizontalGradient(listOf(l.c(Color(0xFFF0E6CC)), l.c(Color(0xFFE2D4B0)), mezcla(l.c(Color(0xFFD8C8A0)), g.a.hondo, 0.3f)), vela.left, vela.right), vela.topLeft, vela.size)
        for (i in 0 until 2) drawRoundRect(l.c(Color(0xFFF4ECD6)), Offset(vela.left + vela.width * (0.1f + 0.5f * i), vela.top), Size(vela.width * 0.25f, vela.height * (0.25f + 0.2f * i)), CornerRadius(vela.width * 0.12f))
        drawLine(l.c(P.HIERRO), Offset(cx, vela.top), Offset(cx, vela.top - 3f * u), 0.9f * u)
        if (arde) {
            val f = Offset(cx, vela.top - z.height * 0.08f)
            drawPath(cerradaRuinas(listOf(Offset(cx, f.y - z.height * 0.08f), Offset(cx + an * 0.1f, f.y + z.height * 0.02f), Offset(cx, f.y + z.height * 0.05f), Offset(cx - an * 0.1f, f.y + z.height * 0.02f))), P.FUEGO)
            drawPath(cerradaRuinas(listOf(Offset(cx, f.y - z.height * 0.045f), Offset(cx + an * 0.05f, f.y + z.height * 0.02f), Offset(cx, f.y + z.height * 0.04f), Offset(cx - an * 0.05f, f.y + z.height * 0.02f))), Color(0xFFFFF4C8))
            drawLine(P.LLAMA.copy(alpha = 0.7f), Offset(vela.left + 0.6f * u, vela.top), Offset(vela.left + 0.6f * u, vela.bottom), 1f * u)
        }
        drawOval(l.c(P.ESMALTE), Offset(cx - an * 0.55f, pie - 7f * u), Size(an * 1.1f, 7f * u))
        drawOval(l.c(P.ESMALTE_BLANCO), Offset(cx - an * 0.44f, pie - 7.6f * u), Size(an * 0.88f, 4f * u))
        drawCircle(l.c(P.ESMALTE), 3.2f * u, Offset(cx + an * 0.62f, pie - 5f * u), style = Stroke(1.6f * u))
        drawRect(l.c(P.ESMALTE), Offset(vela.left - 1.4f * u, vela.bottom - 3f * u), Size(vela.width + 2.8f * u, 3.4f * u))
        // Las piñas: dos, una de pie y otra tumbada, con sus escamas.
        listOf(Triple(Offset(cx + an * 0.95f, pie), 0f, 1f), Triple(Offset(cx + an * 1.6f, pie), 70f, 0.85f)).forEach { (b, giro, t) ->
            val al = z.height * 0.3f * t; val ap = an * 0.55f * t
            rotate(giro, Offset(b.x, b.y - al * 0.3f)) {
                val c = Offset(b.x, b.y - al * 0.5f)
                contactoRuinas(c.x, b.y, ap * 1.1f, 3f * u, l, hacia)
                drawOval(l.c(Color(0xFF6A4628)), Offset(c.x - ap * 0.5f, c.y - al * 0.5f), Size(ap, al))
                for (f in 0 until 6) for (j in 0 until 3) {
                    val q = Offset(c.x + ap * (-0.28f + 0.28f * j + (if (f % 2 == 0) 0.14f else 0f)), c.y - al * 0.4f + al * 0.15f * f)
                    if (kotlin.math.abs(q.x - c.x) > ap * 0.42f) continue
                    drawArc(l.c(Color(0xFFA0784A)), 0f, 180f, true, Offset(q.x - ap * 0.13f, q.y - al * 0.06f), Size(ap * 0.26f, al * 0.12f))
                }
                drawOval(l.s(0.3f), Offset(c.x, c.y - al * 0.5f), Size(ap * 0.5f, al))
            }
        }
    }

    // ---------------------------------------------------------------- la calabaza con las mazorcas

    private fun DrawScope.calabazaConMazorcas(z: Rect, l: LuzRuinas, hacia: Float, g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.55f, w * 0.1f)
        val cx = z.left + an * 0.6f
        val al = z.height * 0.82f
        // La calabaza de peregrino seca: dos panzas, el cuello y el rabo, con su cordel.
        val forma = cerradaRuinas(listOf(Offset(cx, pie - al), Offset(cx + an * 0.2f, pie - al * 0.84f), Offset(cx + an * 0.3f, pie - al * 0.64f), Offset(cx + an * 0.2f, pie - al * 0.5f),
            Offset(cx + an * 0.5f, pie - al * 0.3f), Offset(cx + an * 0.4f, pie - 1f * u), Offset(cx - an * 0.4f, pie - 1f * u), Offset(cx - an * 0.5f, pie - al * 0.3f),
            Offset(cx - an * 0.2f, pie - al * 0.5f), Offset(cx - an * 0.3f, pie - al * 0.64f), Offset(cx - an * 0.2f, pie - al * 0.84f)))
        drawPath(corridoRuinas(forma, -hacia * 5f * u, 3f * u), l.s(0.35f))
        contactoRuinas(cx, pie, an, 4f * u, l, hacia)
        modeladoRapidoRuinas(forma, l.c(Color(0xFFC9A05A)), l, hacia, 1.1f, 0.4f)
        for (i in 0 until 5) mancha(Offset(cx + an * (azar(2301, i) - 0.5f) * 0.6f, pie - al * (0.1f + 0.7f * azar(2302, i))), an * 0.1f, l.c(Color(0xFF8A6A3A)).copy(alpha = 0.5f))
        drawLine(l.c(P.TIERRA), Offset(cx, pie - al), Offset(cx + an * 0.08f, pie - al - z.height * 0.06f), 2f * u, StrokeCap.Round)
        drawLine(l.c(P.BRAMANTE), Offset(cx - an * 0.2f, pie - al * 0.52f), Offset(cx + an * 0.2f, pie - al * 0.5f), 1.4f * u)
        // Las mazorcas secas tumbadas delante, con sus hojas abiertas.
        listOf(Offset(cx + an * 0.9f, pie), Offset(cx + an * 1.35f, pie - 1f * u)).forEachIndexed { i, b ->
            if (b.x > z.right + w * 0.06f) return@forEachIndexed
            val la = z.height * 0.36f; val gr = z.height * 0.11f
            rotate(-8f + 14f * i, b) {
                val maz = Rect(b.x - la * 0.5f, b.y - gr, b.x + la * 0.5f, b.y)
                contactoRuinas(b.x, b.y, la, 3f * u, l, hacia)
                drawRoundRect(l.c(if (i == 0) Color(0xFFD89A2A) else Color(0xFFB8452A)), maz.topLeft, maz.size, CornerRadius(gr * 0.5f))
                for (f in 0 until 3) for (j in 0 until 8) drawCircle(l.b(0.25f), gr * 0.1f, Offset(maz.left + la * (0.1f + 0.11f * j), maz.top + gr * (0.25f + 0.25f * f)))
                drawRect(l.s(0.3f), Offset(maz.left, maz.bottom - gr * 0.3f), Size(la, gr * 0.3f))
                val hoja = cerradaRuinas(listOf(Offset(maz.right - la * 0.1f, maz.center.y), Offset(maz.right + la * 0.25f, maz.top - gr * 0.6f), Offset(maz.right + la * 0.35f, maz.top - gr * 0.2f), Offset(maz.right + la * 0.05f, maz.bottom)))
                drawPath(hoja, l.c(Color(0xFFD8C69A)))
                drawPath(bordeRuinas(hoja, 0f, gr * 0.2f), l.s(0.2f))
            }
        }
    }
}
