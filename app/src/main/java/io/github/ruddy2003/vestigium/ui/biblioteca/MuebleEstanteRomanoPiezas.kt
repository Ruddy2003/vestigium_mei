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
 * campamento del Danubio hacia el 110 d. C.: en cada repisa una cosa distinta (el pan redondo partido en ocho en su paño; el
 * cuenco de terra sigillata con las pesas de bronce en fila; la lucerna de barro con dos higos; la sartén de bronce con el
 * rollo de cuerda de la tienda; el tintero de barro con sus cálamos); al pie, la muela de mano del contubernio con su harina
 * y el ánfora de aceite de la Bética, grandes y cortadas por el borde.
 */
internal object PiezasEstanteRomano {
    private val P = PaletaRomano

    // ---------------------------------------------------------------- los huecos

    /** Lo que queda en el hueco libre de una repisa: una cosa distinta en cada repisa (de cinco). */
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, a: AireRomano, cerca: Float) {
        val w = size.width
        val libre = r.right - w * 0.024f - ocupado
        if (libre < w * 0.14f || r.height < 4f) return
        val pie = Offset(ocupado + libre * 0.52f, r.bottom - r.height * 0.03f)
        val t = minOf(r.height * 0.5f, libre * 0.62f)
        when (Math.floorMod(k, 5)) {
            0 -> pan(a, pie, t, cerca)
            1 -> cuencoYPesas(a, pie, t, cerca)
            2 -> lucerna(a, pie, t, cerca)
            3 -> sarten(a, pie, t, cerca)
            else -> tintero(a, pie, t, cerca)
        }
    }

    /**
     * La lucerna de barro rojo, apagada: el cuerpo redondo con su disco hundido y su agujero de llenar, el pico con la mecha
     * negra y el asa de anillo; su brillo de barniz y su sombra; a su lado, dos higos secos.
     */
    private fun DrawScope.lucerna(a: AireRomano, pie: Offset, t: Float, k: Float) {
        val u = density
        val c = Offset(pie.x - t * 0.15f, pie.y - t * 0.26f)
        val r = t * 0.55f
        contactoRomano(Offset(pie.x, pie.y), t * 2.4f, 5f * u, a)
        val barro = a.luz(mezcla(P.SIGILLATA, P.BARRO, 0.4f), k)
        // El asa de anillo, detrás.
        drawArc(barro, 140f, 260f, false, Offset(c.x - r * 1.45f, c.y - r * 0.75f), Size(r * 0.7f, r * 0.9f), style = Stroke(r * 0.16f))
        // El pico, con su mecha.
        val pico = poligonoRomano(c.x + r * 0.55f, c.y - r * 0.28f, c.x + r * 1.55f, c.y - r * 0.18f, c.x + r * 1.65f, c.y + r * 0.05f, c.x + r * 1.5f, c.y + r * 0.3f, c.x + r * 0.55f, c.y + r * 0.3f)
        drawPath(pico, barro)
        drawCircle(a.luz(P.TINTA, k), r * 0.13f, Offset(c.x + r * 1.42f, c.y - r * 0.05f))
        drawLine(a.luz(P.TINTA, k), Offset(c.x + r * 1.45f, c.y - r * 0.12f), Offset(c.x + r * 1.55f, c.y - r * 0.35f), 1f * u, cap = StrokeCap.Round)
        // El cuerpo: un disco aplastado con su cara de arriba hundida.
        val cuerpo = Rect(c.x - r, c.y - r * 0.5f, c.x + r, c.y + r * 0.55f)
        drawOval(barro, cuerpo.topLeft, cuerpo.size)
        val disco = Rect(c.x - r * 0.68f, c.y - r * 0.42f, c.x + r * 0.68f, c.y + r * 0.02f)
        drawOval(a.luz(mezcla(P.SIGILLATA, P.TINTA, 0.3f), k), disco.topLeft, disco.size)
        drawOval(a.brillo(0.3f, k), Offset(disco.left, disco.top + disco.height * 0.4f), Size(disco.width, disco.height * 0.6f), style = Stroke(0.8f * u))
        drawCircle(a.luz(P.TINTA, k), r * 0.09f, Offset(c.x - r * 0.05f, c.y - r * 0.2f))
        // La luz del barniz y la sombra.
        drawOval(a.brillo(0.45f, k), Offset(c.x - r * 0.85f, c.y + r * 0.05f), Size(r * 0.5f, r * 0.2f))
        drawOval(a.sombra(0.25f), Offset(c.x + r * 0.1f, c.y + r * 0.15f), Size(r * 0.85f, r * 0.4f))
        // Dos higos secos.
        for (j in 0 until 2) {
            val f = Offset(pie.x + t * (0.85f + 0.32f * j), pie.y - t * 0.16f)
            drawOval(a.luz(Color(0xFF6E4A3A), k), Offset(f.x - t * 0.16f, f.y - t * 0.15f), Size(t * 0.32f, t * 0.3f))
            drawOval(a.brillo(0.3f, k), Offset(f.x - t * 0.1f, f.y - t * 0.12f), Size(t * 0.1f, t * 0.08f))
            drawLine(a.luz(Color(0xFF4A3024), k), Offset(f.x, f.y - t * 0.15f), Offset(f.x + t * 0.04f, f.y - t * 0.24f), 1f * u)
        }
    }

    /**
     * El tintero de barro negro de dos cuerpos, con su boca, y los cálamos de caña apoyados en él, uno con la punta mojada;
     * delante, una mancha seca de tinta en la tabla.
     */
    private fun DrawScope.tintero(a: AireRomano, pie: Offset, t: Float, k: Float) {
        val u = density
        contactoRomano(pie, t * 2f, 5f * u, a)
        drawOval(P.TINTA.copy(alpha = if (a.oscuro) 0.3f else 0.4f), Offset(pie.x + t * 0.3f, pie.y - t * 0.05f), Size(t * 0.6f, t * 0.1f))
        // Los cálamos, recostados.
        for (j in 0 until 3) {
            val b = Offset(pie.x + t * (0.1f + 0.22f * j), pie.y - 1f * u)
            val tp = Offset(pie.x - t * (0.55f - 0.18f * j), pie.y - t * (1.35f - 0.12f * j))
            drawLine(a.sombra(0.3f), b + Offset(2f * u, 0f), tp + Offset(2f * u, 1f * u), 2.2f * u)
            drawLine(a.luz(Color(0xFFC7A66A), k), b, tp, 2.2f * u, cap = StrokeCap.Round)
            drawLine(a.brillo(0.4f, k), b + Offset(-0.6f * u, 0f), tp + Offset(-0.6f * u, 0f), 0.6f * u)
            if (j == 1) drawLine(a.luz(P.TINTA, k), tp, tp + (b - tp) * 0.08f, 2.2f * u, cap = StrokeCap.Round)
        }
        // El tintero: dos cilindros unidos, negro con brillo.
        val negro = a.luz(Color(0xFF2A2420), k)
        for (j in 0 until 2) {
            val cx = pie.x - t * 0.35f + j * t * 0.48f
            val an = t * 0.46f
            val al = t * 0.62f
            val r = Rect(cx - an / 2, pie.y - al, cx + an / 2, pie.y)
            drawRect(negro, r.topLeft, r.size)
            drawRect(a.brillo(0.25f, k), Offset(r.left + r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
            drawRect(a.sombra(0.35f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            drawOval(a.luz(Color(0xFF4A3F36), k), Offset(r.left, r.top - an * 0.15f), Size(an, an * 0.3f))
            drawCircle(a.luz(P.TINTA, k), an * 0.1f, Offset(cx, r.top))
            drawOval(negro, Offset(r.left, r.bottom - an * 0.15f), Size(an, an * 0.3f))
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
     * La muela de mano: la piedra de abajo, ancha, con el canal por donde sale la harina, y la de encima, que gira; cada una con
     * su cara de encima vista desde arriba, su canto picado a mano (luz de un lado, sombra del otro y los golpes del pico); el
     * ojo hondo de la de encima con grano, el mango de madera en su encaje de hierro y la harina caída alrededor.
     */
    private fun DrawScope.muela(a: AireRomano, pie: Offset, ancho: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(pie)
        val gr = ancho * 0.2f
        drawOval(a.luz(Color(0xFFEDE6D6), k).copy(alpha = 0.9f), Offset(pie.x - ancho * 0.72f, pie.y - ancho * 0.08f), Size(ancho * 1.4f, ancho * 0.17f))
        contactoRomano(pie + Offset(ancho * 0.12f, -ancho * 0.02f), ancho * 1.2f, ancho * 0.12f, a, 0.45f)
        for (capa in 0..1) {
            val an = if (capa == 0) ancho else ancho * 0.82f
            val base = pie.y - (if (capa == 0) 0f else gr)
            val r = Rect(pie.x - an / 2, base - gr, pie.x + an / 2, base)
            val elipse = an * 0.34f
            val canto = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom)
                arcTo(Rect(r.left, r.bottom - elipse / 2, r.right, r.bottom + elipse / 2), 0f, 180f, false)
                close()
            }
            drawPath(canto, a.luz(P.BASALTO, k))
            clipPath(canto) {
                drawRect(a.brillo(0.25f, k), Offset(r.left, r.top), Size(r.width * 0.25f, r.height + elipse))
                drawRect(a.brillo(0.45f, k), Offset(r.left + r.width * 0.12f, r.top), Size(r.width * 0.05f, r.height + elipse))
                drawRect(a.sombra(0.35f), Offset(r.right - r.width * 0.35f, r.top), Size(r.width * 0.35f, r.height + elipse))
                // Los golpes del pico en el canto.
                val golpes = Path()
                for (j in 0 until 18) {
                    val gx = r.left + r.width * azar(200 + capa, j); val gy = r.top + r.height * (0.2f + 0.9f * azar(210 + capa, j))
                    golpes.moveTo(gx, gy); golpes.lineTo(gx + 1.4f * u, gy + 0.6f * u)
                }
                drawPath(golpes, a.sombra(0.45f), style = Stroke(0.8f * u))
            }
            // La cara de encima, vista desde arriba.
            val cara = Rect(r.left, r.top - elipse / 2, r.right, r.top + elipse / 2)
            drawOval(a.luz(mezcla(P.BASALTO, P.CAL, 0.22f), k), cara.topLeft, cara.size)
            drawOval(a.brillo(0.3f, k), Offset(cara.left + cara.width * 0.08f, cara.top + cara.height * 0.1f), Size(cara.width * 0.45f, cara.height * 0.35f))
            drawOval(a.sombra(0.25f), cara.topLeft, cara.size, style = Stroke(0.8f * u))
            if (capa == 0) {
                // El canal por donde sale la harina, al frente.
                val canal = poligonoRomano(pie.x - an * 0.05f, r.top + gr * 0.35f, pie.x + an * 0.05f, r.top + gr * 0.35f, pie.x + an * 0.07f, r.bottom + elipse * 0.42f,
                    pie.x - an * 0.07f, r.bottom + elipse * 0.42f)
                drawPath(canal, a.luz(mezcla(P.BASALTO, P.TINTA, 0.35f), k))
                drawLine(a.luz(Color(0xFFE6DDCB), k).copy(alpha = 0.8f), Offset(pie.x, r.top + gr * 0.5f), Offset(pie.x, r.bottom + elipse * 0.4f), an * 0.05f)
            } else {
                // El ojo hondo con el grano, y el mango en su encaje.
                val ojo = Rect(pie.x - an * 0.13f, cara.center.y - elipse * 0.16f, pie.x + an * 0.13f, cara.center.y + elipse * 0.16f)
                drawOval(a.luz(P.TINTA, k), ojo.topLeft, ojo.size)
                drawOval(a.luz(P.TRIGO, k), Offset(ojo.left + ojo.width * 0.2f, ojo.top + ojo.height * 0.35f), Size(ojo.width * 0.6f, ojo.height * 0.45f))
                drawOval(a.brillo(0.35f, k), ojo.topLeft, ojo.size, style = Stroke(0.7f * u))
                if (a.oscuro) drawRect(a.lunar(0.22f), Offset(r.left, r.top), Size(1f * u, r.height))
                val mx = pie.x + an * 0.4f
                val tope = cara.center.y
                drawRect(a.luz(P.HIERRO, k), Offset(mx - 2.4f * u, tope - 1.6f * u), Size(4.8f * u, 3.2f * u))
                drawLine(a.sombra(0.3f), Offset(mx + 2.5f * u, tope), Offset(mx + 5f * u, tope - ancho * 0.36f), 3.8f * u)
                drawLine(a.luz(Color(0xFF8E6A44), k), Offset(mx, tope), Offset(mx + 1.5f * u, tope - ancho * 0.38f), 3.8f * u, cap = StrokeCap.Round)
                drawLine(a.brillo(0.4f, k), Offset(mx - 1f * u, tope - 1f * u), Offset(mx + 0.4f * u, tope - ancho * 0.36f), 0.9f * u)
            }
        }
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
