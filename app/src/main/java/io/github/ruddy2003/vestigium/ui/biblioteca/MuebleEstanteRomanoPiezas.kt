package io.github.ruddy2003.vestigium.ui.biblioteca

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
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que queda en los huecos de las repisas y al pie del estante de Romano ([MuebleEstanteRomano]), todo del cuartel de un
 * campamento del Danubio hacia el 110 d. C.: el pan redondo partido en ocho en su paño; el cuenco de terra sigillata con las
 * pesas de bronce en fila; la sartén de bronce con el rollo de cuerda de la tienda; al pie, la muela de mano del contubernio
 * con su harina y el ánfora de aceite de la Bética, grandes y cortadas por el borde.
 */
internal object PiezasEstanteRomano {
    private val P = PaletaRomano

    // ---------------------------------------------------------------- los huecos

    /** Lo que queda en el hueco libre de una repisa, según la repisa: el pan, el cuenco con las pesas o la sartén con la cuerda. */
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, a: AireRomano, cerca: Float) {
        val w = size.width
        val libre = r.right - w * 0.024f - ocupado
        if (libre < w * 0.14f || r.height < 4f) return
        val pie = Offset(ocupado + libre * 0.52f, r.bottom - r.height * 0.03f)
        val t = minOf(r.height * 0.5f, libre * 0.62f)
        when (Math.floorMod(k, 3)) {
            0 -> pan(a, pie, t, cerca)
            1 -> cuencoYPesas(a, pie, t, cerca)
            else -> sarten(a, pie, t, cerca)
        }
    }

    /** El pan redondo partido en ocho, dorado arriba, con su cordel, sobre su paño de lino doblado. */
    private fun DrawScope.pan(a: AireRomano, pie: Offset, t: Float, k: Float) {
        val u = density
        contactoRomano(pie, t * 2.2f, 5f * u, a)
        val pano = poligonoRomano(pie.x - t * 1.0f, pie.y, pie.x - t * 0.75f, pie.y - t * 0.14f, pie.x + t * 0.85f, pie.y - t * 0.12f, pie.x + t * 1.05f, pie.y)
        drawPath(pano, a.luz(Color(0xFFE2D8C2), k))
        drawLine(a.sombra(0.25f), Offset(pie.x - t * 0.5f, pie.y - t * 0.06f), Offset(pie.x - t * 0.25f, pie.y), 0.8f * u)
        drawLine(a.luz(P.CINABRIO, k).copy(alpha = 0.6f), Offset(pie.x - t * 0.95f, pie.y - 1.2f * u), Offset(pie.x + t * 1.0f, pie.y - 1.2f * u), 0.9f * u)
        val hogaza = Rect(pie.x - t * 0.78f, pie.y - t * 0.7f, pie.x + t * 0.78f, pie.y - t * 0.05f)
        val forma = Path().apply { addOval(hogaza) }
        drawPath(forma, a.luz(P.PAN, k))
        clipPath(forma) {
            drawOval(a.luz(Color(0xFFDDA864), k), Offset(hogaza.left + hogaza.width * 0.08f, hogaza.top + 1f * u), Size(hogaza.width * 0.6f, hogaza.height * 0.5f))
            drawOval(a.brillo(0.3f, k), Offset(hogaza.left + hogaza.width * 0.18f, hogaza.top + hogaza.height * 0.08f), Size(hogaza.width * 0.3f, hogaza.height * 0.2f))
            drawRect(a.sombra(0.3f), Offset(hogaza.left, hogaza.top + hogaza.height * 0.7f), Size(hogaza.width, hogaza.height * 0.3f))
            drawRect(a.sombra(0.18f), Offset(hogaza.right - hogaza.width * 0.25f, hogaza.top), Size(hogaza.width * 0.25f, hogaza.height))
        }
        // Los cortes de los ocho gajos, hondos, con su labio dorado, y el cordel que lo ciñe.
        for (j in 0 until 5) {
            val x = hogaza.left + hogaza.width * (0.12f + 0.19f * j)
            drawLine(a.luz(Color(0xFF6E3F1C), k), Offset(x, hogaza.top + hogaza.height * 0.12f), Offset(x + (j - 2) * 1f * u, hogaza.bottom - hogaza.height * 0.18f), 1.2f * u)
            drawLine(a.luz(Color(0xFFE8B978), k), Offset(x - 1f * u, hogaza.top + hogaza.height * 0.14f), Offset(x - 1f * u + (j - 2) * 1f * u, hogaza.bottom - hogaza.height * 0.22f), 0.5f * u)
        }
        drawLine(a.luz(P.CANAMO, k), Offset(hogaza.left + 2f * u, hogaza.center.y + hogaza.height * 0.12f), Offset(hogaza.right - 2f * u, hogaza.center.y + hogaza.height * 0.12f), 1f * u)
        if (a.oscuro) drawOval(a.filo(0.5f * k), Offset(hogaza.right - hogaza.width * 0.25f, hogaza.top + 1f * u), Size(hogaza.width * 0.2f, hogaza.height * 0.2f))
    }

    /**
     * El cuenco de terra sigillata, rojo y con brillo, con su pie, su labio y su faja en relieve, y a su lado las pesas de bronce
     * troncocónicas en fila, de mayor a menor (una de plomo), con la marca del peso hecha a punzón (puntos, no letras).
     */
    private fun DrawScope.cuencoYPesas(a: AireRomano, pie: Offset, t: Float, k: Float) {
        val u = density
        contactoRomano(pie + Offset(t * 0.2f, 0f), t * 2.8f, 5f * u, a)
        val c = Offset(pie.x - t * 0.5f, pie.y)
        val an = t * 1.4f
        val alto = t * 0.66f
        val cuenco = Path().apply {
            moveTo(c.x - an / 2, c.y - alto)
            cubicTo(c.x - an * 0.48f, c.y - alto * 0.2f, c.x - an * 0.2f, c.y - alto * 0.12f, c.x - an * 0.16f, c.y - alto * 0.12f)
            lineTo(c.x - an * 0.18f, c.y)
            lineTo(c.x + an * 0.18f, c.y)
            lineTo(c.x + an * 0.16f, c.y - alto * 0.12f)
            cubicTo(c.x + an * 0.2f, c.y - alto * 0.12f, c.x + an * 0.48f, c.y - alto * 0.2f, c.x + an / 2, c.y - alto)
            close()
        }
        drawPath(cuenco, a.luz(P.SIGILLATA, k))
        clipPath(cuenco) {
            drawRect(a.brillo(0.22f, k), Offset(c.x - an / 2, c.y - alto), Size(an * 0.3f, alto))
            drawRect(a.sombra(0.28f), Offset(c.x + an * 0.18f, c.y - alto), Size(an * 0.32f, alto))
            drawRect(a.sombra(0.25f), Offset(c.x - an / 2, c.y - alto * 0.3f), Size(an, alto * 0.3f))
            // La faja en relieve: una fila de óvalos.
            var x = c.x - an * 0.45f
            var n = 0
            while (x < c.x + an * 0.45f && n < 20) { drawOval(a.luz(mezcla(P.SIGILLATA, P.TINTA, 0.25f), k), Offset(x, c.y - alto * 0.62f), Size(an * 0.06f, alto * 0.14f)); x += an * 0.09f; n++ }
        }
        drawOval(a.luz(mezcla(P.SIGILLATA, P.TINTA, 0.4f), k), Offset(c.x - an / 2, c.y - alto - an * 0.06f), Size(an, an * 0.12f))
        drawOval(a.luz(mezcla(P.SIGILLATA, P.HUESO, 0.2f), k), Offset(c.x - an / 2, c.y - alto - an * 0.06f), Size(an, an * 0.12f), style = Stroke(1.2f * u))
        // El brillo del barniz.
        drawOval(Color.White.copy(alpha = if (a.oscuro) 0.3f * k else 0.55f), Offset(c.x - an * 0.36f, c.y - alto * 0.92f), Size(an * 0.08f, alto * 0.42f))
        // Las pesas, de mayor a menor.
        var x = pie.x + t * 0.42f
        for (j in 0 until 4) {
            val b = t * (0.52f - 0.09f * j)
            val al = b * 0.85f
            val metal = if (j == 2) P.PLOMO else P.BRONCE
            val p = poligonoRomano(x - b / 2, pie.y, x + b / 2, pie.y, x + b * 0.32f, pie.y - al, x - b * 0.32f, pie.y - al)
            drawPath(corridoRomano(p, 1.4f * u, 0f), a.sombra(0.3f))
            drawPath(p, a.luz(metal, k))
            clipPath(p) {
                drawRect(a.brillo(0.3f, k), Offset(x - b / 2, pie.y - al), Size(b * 0.3f, al))
                drawRect(a.sombra(0.3f), Offset(x + b * 0.15f, pie.y - al), Size(b * 0.4f, al))
            }
            drawOval(a.luz(if (j == 2) mezcla(P.PLOMO, P.HUESO, 0.25f) else P.BRONCE_LUZ, k), Offset(x - b * 0.32f, pie.y - al - b * 0.06f), Size(b * 0.64f, b * 0.12f))
            for (d in 0 until (4 - j)) drawCircle(P.TINTA.copy(alpha = 0.5f), 0.5f * u, Offset(x - b * 0.15f + d * 1.7f * u, pie.y - al * 0.5f))
            x += b * 0.95f + 1.5f * u
        }
    }

    /** La sartén de bronce de mango largo apoyada de canto contra el fondo, y el rollo de cuerda de la tienda. */
    private fun DrawScope.sarten(a: AireRomano, pie: Offset, t: Float, k: Float) {
        val u = density
        val c = Offset(pie.x - t * 0.25f, pie.y - t * 0.66f)
        val r = t * 0.64f
        contactoRomano(pie, t * 1.8f, 5f * u, a)
        drawLine(a.sombra(0.3f), c + Offset(r * 0.6f + 2f * u, -r * 0.4f + 2f * u), c + Offset(r * 1.6f + 2f * u, -r * 1.25f + 2f * u), 3f * u)
        drawLine(a.luz(P.BRONCE, k), c + Offset(r * 0.6f, -r * 0.4f), c + Offset(r * 1.6f, -r * 1.25f), 3f * u, cap = StrokeCap.Round)
        drawLine(a.brillo(0.5f, k), c + Offset(r * 0.6f, -r * 0.45f), c + Offset(r * 1.55f, -r * 1.3f), 0.8f * u)
        drawCircle(a.luz(P.BRONCE, k), r * 0.16f, c + Offset(r * 1.6f, -r * 1.25f), style = Stroke(1.4f * u))
        drawCircle(a.sombra(0.35f), r, c + Offset(2.4f * u, 2.4f * u))
        drawCircle(a.luz(P.BRONCE, k), r, c)
        drawCircle(a.luz(mezcla(P.BRONCE, P.TINTA, 0.4f), k), r * 0.8f, c)
        drawCircle(a.luz(P.BRONCE_LUZ, k), r * 0.86f, c, style = Stroke(0.9f * u))
        drawArc(a.brillo(0.6f, k), 200f, 70f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(1.4f * u))
        drawArc(a.sombra(0.3f), 20f, 120f, false, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f), style = Stroke(1.6f * u))
        if (a.oscuro) drawArc(a.filo(0.6f * k), -60f, 80f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.4f * u))
        // El rollo de cuerda, tumbado: vueltas apretadas vistas de canto y un cabo suelto.
        val o = Offset(pie.x + t * 0.95f, pie.y - t * 0.22f)
        val ro = t * 0.46f
        drawOval(a.luz(P.CANAMO, k), Offset(o.x - ro, o.y - ro * 0.5f), Size(ro * 2, ro * 1.0f))
        for (j in 0 until 5) drawOval(a.luz(mezcla(P.CANAMO, P.TINTA, 0.35f), k), Offset(o.x - ro * (1f - 0.16f * j), o.y - ro * (0.5f - 0.08f * j)), Size(ro * (2f - 0.32f * j), ro * (1f - 0.16f * j)), style = Stroke(0.8f * u))
        drawOval(a.brillo(0.3f, k), Offset(o.x - ro * 0.8f, o.y - ro * 0.45f), Size(ro * 0.8f, ro * 0.25f))
        drawOval(a.luz(mezcla(P.CANAMO, P.TINTA, 0.6f), k), Offset(o.x - ro * 0.22f, o.y - ro * 0.1f), Size(ro * 0.44f, ro * 0.2f))
        drawLine(a.luz(P.CANAMO, k), o + Offset(ro * 0.7f, ro * 0.3f), o + Offset(ro * 1.3f, ro * 0.5f), 1.1f * u, cap = StrokeCap.Round)
    }

    // ---------------------------------------------------------------- al pie

    /** Al pie: la muela de mano del contubernio, con su harina, y el ánfora de aceite de la Bética, cortada por el borde. */
    fun DrawScope.alPie(a: AireRomano, suelo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val h = size.height
        if (h - suelo < 4f) return
        muela(a, Offset(w * 0.17f, minOf(h - 2f, suelo + (h - suelo) * 0.78f)), w * 0.24f, cerca)
        anfora(a, Offset(w * 0.93f, suelo + (h - suelo) * 0.3f), w * 0.15f, cerca)
    }

    /**
     * La muela de mano: la piedra de abajo y la de encima, de basalto poroso, cada una con su cara de encima, su canto redondo
     * con luz y sombra y sus poros; la boca con grano, el mango de madera y la harina caída alrededor.
     */
    private fun DrawScope.muela(a: AireRomano, pie: Offset, ancho: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(pie)
        val gr = ancho * 0.22f
        drawOval(a.luz(Color(0xFFEDE6D6), k).copy(alpha = 0.85f), Offset(pie.x - ancho * 0.75f, pie.y - ancho * 0.07f), Size(ancho * 1.5f, ancho * 0.16f))
        contactoRomano(pie + Offset(ancho * 0.12f, -ancho * 0.02f), ancho * 1.2f, ancho * 0.12f, a, 0.45f)
        for (capa in 0..1) {
            val an = if (capa == 0) ancho else ancho * 0.88f
            val base = pie.y - (if (capa == 0) 0f else gr)
            val r = Rect(pie.x - an / 2, base - gr, pie.x + an / 2, base)
            val elipse = an * 0.2f
            val canto = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom)
                arcTo(Rect(r.left, r.bottom - elipse / 2, r.right, r.bottom + elipse / 2), 0f, 180f, false)
                close()
            }
            drawPath(canto, a.luz(P.BASALTO, k))
            clipPath(canto) {
                drawRect(a.brillo(0.25f, k), Offset(r.left, r.top), Size(r.width * 0.25f, r.height + elipse))
                drawRect(a.brillo(0.4f, k), Offset(r.left + r.width * 0.12f, r.top), Size(r.width * 0.06f, r.height + elipse))
                drawRect(a.sombra(0.3f), Offset(r.right - r.width * 0.35f, r.top), Size(r.width * 0.35f, r.height + elipse))
                for (j in 0 until 14) drawCircle(a.sombra(0.4f), 0.8f * u, Offset(r.left + r.width * azar(200 + capa, j), r.top + r.height * azar(210 + capa, j)))
            }
            drawOval(a.luz(mezcla(P.BASALTO, P.CAL, 0.25f), k), Offset(r.left, r.top - elipse / 2), Size(r.width, elipse))
            drawOval(a.brillo(0.3f, k), Offset(r.left + r.width * 0.1f, r.top - elipse * 0.4f), Size(r.width * 0.4f, elipse * 0.35f))
            if (a.oscuro) drawRect(a.lunar(0.22f), Offset(r.left, r.top), Size(1f * u, r.height))
        }
        val tope = pie.y - 2 * gr
        drawOval(a.luz(P.TINTA, k).copy(alpha = 0.75f), Offset(pie.x - ancho * 0.08f, tope - ancho * 0.03f), Size(ancho * 0.16f, ancho * 0.06f))
        drawOval(a.luz(P.TRIGO, k), Offset(pie.x - ancho * 0.05f, tope - ancho * 0.022f), Size(ancho * 0.1f, ancho * 0.035f))
        val mx = pie.x + ancho * 0.33f
        drawLine(a.sombra(0.3f), Offset(mx + 2.5f * u, tope + gr * 0.3f), Offset(mx + 5f * u, tope - ancho * 0.3f), 3.6f * u)
        drawLine(a.luz(Color(0xFF8E6A44), k), Offset(mx, tope + gr * 0.3f), Offset(mx + 1.5f * u, tope - ancho * 0.32f), 3.6f * u, cap = StrokeCap.Round)
        drawLine(a.brillo(0.35f, k), Offset(mx - 1f * u, tope + gr * 0.2f), Offset(mx + 0.5f * u, tope - ancho * 0.3f), 0.9f * u)
    }

    /**
     * El ánfora globular de aceite de la Bética, apoyada en el podio y cortada por el borde: el cuerpo redondo con su luz y su
     * sombra, las vueltas del torno, el cuello, el labio, las dos asas gruesas, el sello en el asa y un rastro de aceite.
     */
    private fun DrawScope.anfora(a: AireRomano, c: Offset, r: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(c)
        clipRect(0f, 0f, size.width, size.height) {
            rotate(-12f, c) {
                drawCircle(a.sombra(0.35f), r, c + Offset(-r * 0.25f, r * 0.12f))
                drawRect(a.luz(P.BARRO, k), Offset(c.x - r * 0.2f, c.y - r * 1.3f), Size(r * 0.4f, r * 0.5f))
                drawRect(a.luz(Color(0xFFCB8664), k), Offset(c.x - r * 0.28f, c.y - r * 1.38f), Size(r * 0.56f, r * 0.14f))
                for (s in listOf(-1f, 1f)) drawArc(a.luz(P.BARRO, k), if (s < 0) 180f else -90f, 90f, false,
                    Offset(c.x + s * r * 0.2f - r * 0.4f, c.y - r * 1.15f), Size(r * 0.8f, r * 0.7f), style = Stroke(r * 0.13f))
                val cuerpo = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
                drawPath(cuerpo, a.luz(P.BARRO, k))
                clipPath(cuerpo) {
                    drawCircle(a.brillo(0.2f, k), r * 0.62f, c + Offset(-r * 0.32f, -r * 0.32f))
                    drawCircle(a.sombra(0.32f), r * 1.1f, c + Offset(r * 0.6f, r * 0.55f))
                    for (j in 0 until 4) drawArc(a.luz(Color(0xFF8E4E36), k).copy(alpha = 0.4f), 200f, 110f, false,
                        Offset(c.x - r * (0.9f - 0.1f * j), c.y - r * (0.6f - 0.25f * j)), Size(r * (1.8f - 0.2f * j), r * 0.9f), style = Stroke(0.7f * u))
                }
                drawOval(a.brillo(0.45f, k), Offset(c.x - r * 0.64f, c.y - r * 0.62f), Size(r * 0.2f, r * 0.45f))
                drawRect(a.luz(Color(0xFF8E4E36), k), Offset(c.x - r * 0.58f, c.y - r * 1.02f), Size(r * 0.18f, r * 0.08f))
                if (a.oscuro) {
                    drawArc(a.filo(0.5f * k), -60f, 70f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.6f * u))
                    drawArc(a.lunar(0.25f), 165f, 60f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1f * u))
                }
            }
        }
    }
}
