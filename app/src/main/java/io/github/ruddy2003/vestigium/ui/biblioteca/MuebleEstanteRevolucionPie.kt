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
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El pie del estante de Revolución ([MuebleEstanteRevolucion]): el suelo de losas de piedra de liais con sus esquinas cortadas y
 * los tacos de pizarra negra en los cruces, gastado y con una mancha de tinta; a la izquierda, cortada por el borde, la estufa
 * de hierro de patas cortas con su puerta (de noche, entreabierta, con las brasas: la luz del pie); en medio, las resmas
 * envueltas en papel de estraza, una sobre otra y otra ladeada, con su bramante; a la derecha, cortada, la tina de remojar el
 * papel, de duelas con aros de hierro, con su agua y un pliego mojado colgando del borde.
 */
internal object PieEstanteRevolucion {
    private val P = PaletaRevolucion

    fun DrawScope.alPie(a: AireRevolucion, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width
        if (h - suelo < 2f) return
        losas(a, suelo, h, cerca)
        val pie = h - w * 0.012f
        resmas(a, Offset(w * 0.4f, pie - w * 0.006f), cerca)
        estufa(a, Offset(w * 0.07f, pie), cerca)
        tina(a, Offset(w * 0.88f, pie + w * 0.004f), cerca)
        // De noche, el charco de la estufa en el suelo y en lo bajo del mueble (solo hasta donde se ve).
        if (a.oscuro) {
            val c = Offset(w * MuebleEstanteRevolucion.ESTUFA_X, b.top + b.height * MuebleEstanteRevolucion.ESTUFA_Y)
            val rr = w * 0.5f
            val f = if (a.noche) 0.6f else 1f
            clipRect(0f, b.top, w, h) {
                drawCircle(Brush.radialGradient(listOf(P.BRASA.copy(alpha = 0.22f * f), P.LLAMA.copy(alpha = 0.08f * f), Color.Transparent), c, rr), rr, c)
            }
        }
    }

    // ---------------------------------------------------------------- las losas

    /** Las losas de liais con las esquinas cortadas y los tacos de pizarra, en perspectiva (crecen hacia quien mira). */
    private fun DrawScope.losas(a: AireRevolucion, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val fuga = Offset(w * 0.5f, suelo - w * 0.7f)
        // Las filas: más bajas al fondo.
        val ys = ArrayList<Float>()
        var y = suelo; var fh = w * 0.026f
        ys.add(y)
        while (y < h) { y += fh; ys.add(y); fh *= 1.4f }
        // El color de la piedra por tramos (de noche, encendida junto a la estufa).
        val n = if (a.lumbre) 16 else 1
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, (suelo + h) / 2))
            drawRect(a.dia(P.PIEDRA, 0f, k), Offset(x0, suelo), Size(x1 - x0, h - suelo + 1f))
        }
        val paso = w * 0.15f
        // Cada losa, de su tono: la piedra no es pareja (unas más pardas, otras más claras o con su veta gris), y el canto de
        // arriba de cada una, gastado y claro.
        val pardas = Path(); val claras = Path(); val grises = Path(); val bordes = Path()
        for (f in 0 until ys.size - 1) {
            val ya = ys[f]; val yb = minOf(ys[f + 1], h)
            var xq = -paso * 3; var j = 0
            while (xq < w + paso * 3) {
                fun en(yy: Float, xx: Float): Float = fuga.x + (xx - fuga.x) * (yy - fuga.y) / (h - fuga.y)
                val q = poligonoRevolucion(en(ya, xq), ya, en(ya, xq + paso), ya, en(yb, xq + paso), yb, en(yb, xq), yb)
                val z = azar(6550 + f, j)
                if (z < 0.28f) pardas.addPath(q) else if (z > 0.78f) claras.addPath(q) else if (z > 0.62f) grises.addPath(q)
                bordes.moveTo(en(ya, xq) + 2f * u, ya + 1.4f * u); bordes.lineTo(en(ya, xq + paso) - 2f * u, ya + 1.4f * u)
                xq += paso; j++
            }
        }
        val kt = cerca(Offset(w * 0.5f, (suelo + h) / 2))
        drawPath(pardas, a.dia(mezcla(P.PIEDRA, P.ROBLE, 0.22f), 0f, kt).copy(alpha = 0.55f))
        drawPath(claras, a.claro(0.22f))
        drawPath(grises, a.dia(mezcla(P.PIEDRA, P.PIZARRA, 0.15f), 0f, kt).copy(alpha = 0.45f))
        drawPath(bordes, a.claro(0.3f), style = Stroke(1.1f * u))
        // De día, el suelo más oscuro al fondo, bajo el mueble, y más claro delante.
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.5f), a.sombra(0.1f)), suelo, suelo + (h - suelo) * 0.6f), Offset(0f, suelo), Size(w, (h - suelo) * 0.6f))
        // Las juntas: las de las filas, a lo ancho; las de las losas, hacia el punto de fuga.
        val juntas = Path(); val tacos = Path(); val cantos = Path()
        ys.forEach { yy -> juntas.moveTo(0f, yy); juntas.lineTo(w, yy) }
        var xb = -paso * 3
        while (xb < w + paso * 3) {
            // Del borde de abajo (en el plano del suelo, a la altura h) hacia la fuga.
            fun en(yy: Float): Float = fuga.x + (xb - fuga.x) * (yy - fuga.y) / (h - fuga.y)
            juntas.moveTo(en(suelo), suelo); juntas.lineTo(en(h), h)
            // Los tacos en cada cruce: un rombito de pizarra que crece con la cercanía.
            ys.forEach { yy ->
                val tx = en(yy)
                val ta = w * 0.012f * (yy - fuga.y) / (h - fuga.y)
                if (tx > -ta && tx < w + ta) {
                    tacos.moveTo(tx, yy - ta * 0.5f); tacos.lineTo(tx + ta, yy); tacos.lineTo(tx, yy + ta * 0.5f); tacos.lineTo(tx - ta, yy); tacos.close()
                    cantos.moveTo(tx - ta, yy); cantos.lineTo(tx, yy - ta * 0.5f)
                }
            }
            xb += paso
        }
        val km = cerca(Offset(w * 0.5f, h))
        drawPath(juntas, a.dia(P.PIEDRA_OSC, -0.2f, km), style = Stroke(1.2f * u))
        drawPath(tacos, a.dia(P.PIZARRA, 0f, km))
        drawPath(cantos, a.claro(0.3f), style = Stroke(0.8f * u))
        // El gastado: pisadas más claras por el medio, alguna losa más parda y una mancha de tinta.
        drawOval(a.claro(0.12f), Offset(w * 0.3f, suelo + (h - suelo) * 0.45f), Size(w * 0.4f, (h - suelo) * 0.5f))
        for (j in 0 until 4) {
            val px = w * azar(6500, j); val py = suelo + (h - suelo) * (0.3f + 0.6f * azar(6501, j))
            drawOval(a.dia(mezcla(P.PIEDRA, P.PIEDRA_OSC, 0.6f), 0f, cerca(Offset(px, py))).copy(alpha = 0.4f), Offset(px, py), Size(w * (0.05f + 0.06f * azar(6502, j)), (h - suelo) * 0.12f))
        }
        val tinta = Path().apply {
            moveTo(w * 0.58f, h - (h - suelo) * 0.3f)
            cubicTo(w * 0.6f, h - (h - suelo) * 0.42f, w * 0.65f, h - (h - suelo) * 0.38f, w * 0.66f, h - (h - suelo) * 0.3f)
            cubicTo(w * 0.67f, h - (h - suelo) * 0.2f, w * 0.6f, h - (h - suelo) * 0.18f, w * 0.58f, h - (h - suelo) * 0.3f); close()
        }
        drawPath(tinta, a.dia(P.TINTA, 0f, km).copy(alpha = 0.55f))
        drawCircle(a.dia(P.TINTA, 0f, km).copy(alpha = 0.5f), w * 0.004f, Offset(w * 0.68f, h - (h - suelo) * 0.36f))
    }

    // ---------------------------------------------------------------- la estufa de hierro

    /**
     * La estufa redonda de hierro colado sobre tres patas curvas: el cuerpo con sus molduras, la placa de arriba con el cazo de
     * cobre de calentar la cola, el tubo que sube y se mete en el muro por la izquierda, y la puerta de arco con sus rejillas
     * (de noche, entreabierta, con las brasas: la luz del pie).
     */
    private fun DrawScope.estufa(a: AireRevolucion, pie: Offset, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val an = w * 0.2f
        val cuerpo = Rect(pie.x - an / 2, pie.y - w * 0.245f, pie.x + an / 2, pie.y - w * 0.04f)
        val k = cerca(Offset(cuerpo.right, cuerpo.center.y))
        // El hierro es negro: de noche no se enciende entero, solo coge el filo de las brasas y de la vela.
        val ki = if (a.oscuro) k * 0.4f else k
        val hc = if (a.oscuro) 1f else -1f
        contactoRevolucion(Offset(pie.x + w * 0.01f, pie.y), an * 1.3f, w * 0.016f, a, 0.5f, 1f)
        // El tubo: sube de la placa y, en el codo, se mete en el muro por la izquierda, fuera del cuadro.
        val tx = cuerpo.left + an * 0.3f
        val tubo = Rect(tx - w * 0.024f, cuerpo.top - w * 0.085f, tx + w * 0.024f, cuerpo.top)
        drawRect(a.dia(P.HIERRO, 0f, ki), tubo.topLeft, tubo.size)
        clipRect(tubo.left, tubo.top, tubo.right, tubo.bottom) { cilindroRevolucion(tubo, a, k, hc, 1.2f) }
        val codo = Rect(-w * 0.02f, tubo.top - w * 0.008f, tubo.right, tubo.top + w * 0.04f)
        drawRect(a.dia(P.HIERRO, 0f, ki), codo.topLeft, codo.size)
        drawRect(a.dia(P.HIERRO_LUZ, 0f, ki), codo.topLeft, Size(codo.width, w * 0.005f))
        drawRect(a.sombra(0.45f), Offset(codo.left, codo.bottom - w * 0.006f), Size(codo.width, w * 0.006f))
        drawRect(a.dia(P.HIERRO_LUZ, -0.2f, ki), Offset(tubo.left - w * 0.003f, tubo.bottom - w * 0.03f), Size(tubo.width + w * 0.006f, w * 0.006f))
        // Las tres patas curvas de hierro colado.
        listOf(-0.4f, 0f, 0.4f).forEach { f ->
            val px = pie.x + an * f
            val lado = if (f < 0f) -1f else if (f > 0f) 1f else 0f
            val pata = Path().apply {
                moveTo(px - w * 0.012f, cuerpo.bottom - 1f)
                lineTo(px + w * 0.012f, cuerpo.bottom - 1f)
                quadraticTo(px + w * 0.01f + lado * w * 0.012f, pie.y - w * 0.012f, px + lado * w * 0.02f + w * 0.008f, pie.y)
                lineTo(px + lado * w * 0.02f - w * 0.008f, pie.y)
                quadraticTo(px - w * 0.008f + lado * w * 0.01f, pie.y - w * 0.014f, px - w * 0.012f, cuerpo.bottom - 1f)
                close()
            }
            drawPath(pata, a.dia(P.HIERRO, if (f == 0f) 0.05f else 0f, ki))
        }
        // El cuerpo: un tambor negro con su luz, la moldura de abajo y la placa de arriba, más ancha.
        drawRect(a.dia(P.HIERRO, 0f, ki), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            cilindroRevolucion(cuerpo, a, k, hc, 1.4f)
            // Las costillas del colado, a lo alto, con su brillo.
            for (j in 1 until 6) {
                val x = cuerpo.left + an * j / 6f
                drawLine(a.dia(P.HIERRO_LUZ, -0.25f, ki), Offset(x, cuerpo.top + w * 0.025f), Offset(x, cuerpo.bottom - w * 0.022f), 1.2f * u)
            }
        }
        listOf(cuerpo.top + w * 0.012f to w * 0.012f, cuerpo.bottom - w * 0.016f to w * 0.016f).forEach { (y, g) ->
            val anillo = Rect(cuerpo.left - w * 0.006f, y - g / 2, cuerpo.right + w * 0.006f, y + g / 2)
            drawRect(a.dia(P.HIERRO, 0.04f, ki), anillo.topLeft, anillo.size)
            drawRect(a.dia(P.HIERRO_LUZ, -0.05f, ki), anillo.topLeft, Size(anillo.width, 1.2f * u))
            drawRect(a.sombra(0.5f), Offset(anillo.left, anillo.bottom - 1f * u), Size(anillo.width, 1f * u))
        }
        val placa = Rect(cuerpo.left - w * 0.012f, cuerpo.top - w * 0.012f, cuerpo.right + w * 0.012f, cuerpo.top + w * 0.002f)
        drawRect(a.dia(P.HIERRO, 0.06f, ki), placa.topLeft, placa.size)
        drawRect(a.dia(P.HIERRO_LUZ, 0f, ki), placa.topLeft, Size(placa.width, w * 0.004f))
        // La puerta de arco: de día, cerrada, con sus rejillas de tiro y el pestillo de latón; de noche, entreabierta.
        val boca = Rect(pie.x - an * 0.18f, cuerpo.top + w * 0.06f, pie.x + an * 0.26f, cuerpo.bottom - w * 0.03f)
        val arco = Path().apply {
            moveTo(boca.left, boca.bottom); lineTo(boca.left, boca.top + boca.width / 2)
            quadraticTo(boca.left, boca.top, boca.center.x, boca.top)
            quadraticTo(boca.right, boca.top, boca.right, boca.top + boca.width / 2)
            lineTo(boca.right, boca.bottom); close()
        }
        drawPath(arco, a.dia(P.HIERRO_LUZ, -0.3f, ki), style = Stroke(w * 0.007f))
        if (a.oscuro || a.noche) {
            val f = if (a.oscuro) (if (a.noche) 0.7f else 1f) else 0.5f
            drawPath(arco, Color(0xFF2A0A04))
            clipPath(arco) {
                drawRect(Brush.verticalGradient(listOf(P.BRASA.copy(alpha = 0.85f * f), P.LLAMA.copy(alpha = f)), boca.top + boca.height * 0.3f, boca.bottom), Offset(boca.left, boca.top + boca.height * 0.4f), Size(boca.width, boca.height * 0.6f))
                for (j in 0 until 7) drawCircle(P.FUEGO.copy(alpha = 0.85f * f), w * (0.004f + 0.004f * azar(6600, j)), Offset(boca.left + boca.width * azar(6601, j), boca.bottom - boca.height * 0.3f * azar(6602, j)))
                val lengua = Path().apply { moveTo(boca.center.x - boca.width * 0.2f, boca.bottom - boca.height * 0.3f); quadraticTo(boca.center.x, boca.top + boca.height * 0.3f, boca.center.x + boca.width * 0.15f, boca.bottom - boca.height * 0.32f); close() }
                drawPath(lengua, Color(0xFFFFA048).copy(alpha = 0.8f * f))
            }
            // La hoja abierta, de canto, a la derecha, con el reflejo de las brasas.
            drawPath(poligonoRevolucion(boca.right, boca.top + boca.width * 0.3f, boca.right + w * 0.045f, boca.top + boca.width * 0.4f, boca.right + w * 0.045f, boca.bottom + w * 0.006f, boca.right, boca.bottom), a.dia(P.HIERRO, 0f, ki))
            drawLine(P.LLAMA.copy(alpha = 0.7f * f), Offset(boca.right + w * 0.002f, boca.top + boca.width * 0.32f), Offset(boca.right + w * 0.002f, boca.bottom), 1.6f * u)
            if (a.oscuro) drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.32f * f), Color.Transparent), boca.center, w * 0.15f), w * 0.15f, boca.center)
        } else {
            drawPath(arco, a.dia(P.HIERRO, -0.1f, ki))
            // El registro del tiro: una ranura a lo ancho con su tapa corrediza medio abierta y su botón.
            val ranura = Rect(boca.left + boca.width * 0.14f, boca.top + boca.height * 0.66f, boca.right - boca.width * 0.14f, boca.top + boca.height * 0.78f)
            drawRect(a.sombra(0.75f), ranura.topLeft, ranura.size)
            drawRect(a.dia(P.HIERRO_LUZ, -0.25f, ki), Offset(ranura.left, ranura.top - w * 0.002f), Size(ranura.width * 0.55f, ranura.height + w * 0.004f))
            drawCircle(a.dia(P.HIERRO_LUZ, 0f, ki), w * 0.0035f, Offset(ranura.left + ranura.width * 0.5f, ranura.center.y))
            drawCircle(a.dia(P.LATON, -0.2f, k), w * 0.006f, Offset(boca.right - boca.width * 0.14f, boca.center.y))
            drawPath(arco, a.claro(0.2f), style = Stroke(1f * u))
        }
        if (a.oscuro) drawRect(a.filo((0.6f * k).coerceAtMost(0.55f)), Offset(cuerpo.right - 1.4f * u, cuerpo.top), Size(1.4f * u, cuerpo.height))
        // Encima, el cazo de cobre donde se calienta la cola, con su mango.
        val cz = Rect(cuerpo.left + an * 0.5f, placa.top - w * 0.045f, cuerpo.left + an * 0.92f, placa.top)
        val cobre = Color(0xFFB0643A)
        drawPath(poligonoRevolucion(cz.left, cz.top, cz.right, cz.top, cz.right - w * 0.006f, cz.bottom, cz.left + w * 0.006f, cz.bottom), a.dia(cobre, 0f, k))
        clipRect(cz.left, cz.top, cz.right, cz.bottom) { cilindroRevolucion(cz, a, k, hc, 1.3f) }
        drawOval(a.dia(Color(0xFF6A3018), 0f, k), Offset(cz.left, cz.top - w * 0.004f), Size(cz.width, w * 0.01f))
        drawLine(a.dia(P.HIERRO, 0f, ki), Offset(cz.right - w * 0.004f, cz.top + w * 0.01f), Offset(cz.right + w * 0.06f, cz.top - w * 0.006f), w * 0.006f, StrokeCap.Round)
        destelloRevolucion(Offset(cz.left + cz.width * 0.25f, cz.center.y), 1.8f * u, a, k)
    }

    // ---------------------------------------------------------------- las resmas

    private fun DrawScope.resmas(a: AireRevolucion, pie: Offset, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - w * 0.06f))
        val an = w * 0.27f; val g = w * 0.058f
        val r1 = Rect(pie.x - an / 2, pie.y - g, pie.x + an / 2, pie.y)
        val r2 = Rect(pie.x - an / 2 + w * 0.018f, r1.top - g * 0.95f, pie.x + an / 2 - w * 0.006f, r1.top)
        val r0 = Rect(pie.x - an / 2 - w * 0.008f, r2.top - g * 0.9f, pie.x + an / 2 - w * 0.03f, r2.top)
        contactoRevolucion(Offset(pie.x, pie.y), an * 1.1f, w * 0.016f, a, 0.5f, if (a.oscuro) -1f else 1f)
        listOf(r1 to P.ESTRAZA, r2 to Color(0xFF7E8A96), r0 to mezcla(P.TOSTADO, P.ESTRAZA, 0.4f)).forEachIndexed { i, (r, col) ->
            drawRect(a.dia(col, 0f, k), r.topLeft, r.size)
            drawPath(poligonoRevolucion(r.left, r.top, r.left + r.height * 0.6f, r.center.y, r.left, r.bottom), a.sombra(0.2f))
            drawPath(poligonoRevolucion(r.right, r.top, r.right - r.height * 0.6f, r.center.y, r.right, r.bottom), a.sombra(0.14f))
            drawRect(a.claro(0.35f), r.topLeft, Size(r.width, 1.4f * u))
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - r.height * 0.2f), Size(r.width, r.height * 0.2f))
            drawRect(a.sombra(0.45f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            // Un desgarrón en el envoltorio por donde asoman los cantos de los pliegos.
            if (i == 0) {
                val d = Rect(r.left + r.width * 0.62f, r.top + r.height * 0.25f, r.left + r.width * 0.84f, r.bottom - r.height * 0.15f)
                drawRect(a.dia(P.PAPEL, 0f, k), d.topLeft, d.size)
                for (j in 0 until 5) drawLine(a.sombra(0.18f), Offset(d.left, d.top + d.height * (0.15f + 0.18f * j)), Offset(d.right, d.top + d.height * (0.15f + 0.18f * j)), 0.7f * u)
                drawPath(poligonoRevolucion(d.left, d.top, d.right, d.top, d.right - d.width * 0.2f, d.top + d.height * 0.3f), a.dia(col, -0.1f, k))
            }
            val bx = r.left + r.width * (0.32f + 0.12f * i)
            drawLine(a.dia(P.BRAMANTE, 0f, k), Offset(bx, r.top), Offset(bx, r.bottom), 1.8f * u)
            drawLine(a.dia(P.BRAMANTE, 0f, k), Offset(r.left, r.center.y + w * 0.002f), Offset(r.right, r.center.y - w * 0.002f), 1.6f * u)
            if (a.oscuro) drawRect(a.filo((0.6f * k).coerceAtMost(0.5f)), Offset(r.left, r.top), Size(1.4f * u, r.height))
        }
        // La tercera, ladeada contra las otras dos.
        val c = Offset(r1.right + w * 0.02f, pie.y)
        val r3 = Path().apply { moveTo(c.x - w * 0.012f, c.y); lineTo(c.x + w * 0.055f, c.y); lineTo(c.x + w * 0.035f, c.y - w * 0.15f); lineTo(c.x - w * 0.035f, c.y - w * 0.145f); close() }
        drawPath(r3, a.dia(mezcla(P.ESTRAZA, P.PAPEL, 0.25f), 0f, k))
        clipPath(r3) {
            drawRect(a.sombra(0.22f), Offset(c.x + w * 0.02f, c.y - w * 0.16f), Size(w * 0.04f, w * 0.16f))
            drawLine(a.dia(P.BRAMANTE, 0f, k), Offset(c.x - w * 0.035f, c.y - w * 0.075f), Offset(c.x + w * 0.05f, c.y - w * 0.07f), 1.6f * u)
        }
        drawPath(r3, a.sombra(0.3f), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- la tina de remojar el papel

    private fun DrawScope.tina(a: AireRevolucion, pie: Offset, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - w * 0.08f))
        val an = w * 0.3f; val al = w * 0.19f
        val boca = Rect(pie.x - an / 2, pie.y - al - w * 0.02f, pie.x + an / 2, pie.y - al + w * 0.02f)
        contactoRevolucion(Offset(pie.x, pie.y), an * 1.08f, w * 0.018f, a, 0.5f, -1f)
        val cuerpo = Path().apply {
            moveTo(boca.left, boca.center.y)
            lineTo(boca.left + an * 0.06f, pie.y - w * 0.012f)
            quadraticTo(pie.x, pie.y + w * 0.012f, boca.right - an * 0.06f, pie.y - w * 0.012f)
            lineTo(boca.right, boca.center.y); close()
        }
        drawPath(cuerpo, a.dia(P.PINO, -0.15f, k))
        clipPath(cuerpo) {
            // Las duelas, cada una con su luz, y la sombra del lado contrario a la luz.
            val duelas = Path()
            var x = boca.left + an * 0.07f
            while (x < boca.right) { duelas.moveTo(x, boca.center.y); duelas.lineTo(x + (x - pie.x) * 0.05f, pie.y); x += an * 0.11f }
            drawPath(duelas, a.dia(P.PINO_OSC, 0f, k), style = Stroke(1.2f * u))
            cilindroRevolucion(Rect(boca.left, boca.top, boca.right, pie.y + w * 0.02f), a, k, -1f, 1.2f)
            // Los aros de hierro, curvados.
            listOf(0.28f, 0.78f).forEach { f ->
                val y = boca.center.y + (pie.y - boca.center.y) * f
                val aro = Path().apply { moveTo(boca.left, y); quadraticTo(pie.x, y + w * 0.022f, boca.right, y) }
                drawPath(aro, a.dia(P.HIERRO, 0f, k), style = Stroke(w * 0.009f))
                drawPath(Path().apply { moveTo(boca.left, y - w * 0.003f); quadraticTo(pie.x, y + w * 0.019f, boca.right, y - w * 0.003f) }, a.claro(0.3f), style = Stroke(1f * u))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.sombra(0.3f)), pie.y - w * 0.05f, pie.y), Offset(boca.left, pie.y - w * 0.05f), Size(an, w * 0.06f))
        }
        // La boca: el canto de las duelas y el agua oscura con el reflejo de la ventana (de noche, el de la estufa).
        drawOval(a.dia(P.PINO_LUZ, -0.1f, k), boca.topLeft, boca.size)
        val agua = boca.deflate(w * 0.008f)
        drawOval(a.dia(Color(0xFF3A4A48), 0f, k), agua.topLeft, agua.size)
        drawOval(a.sombra(0.4f), Offset(agua.left, agua.top), Size(agua.width, agua.height * 0.5f))
        val reflejo = if (a.oscuro) P.LLAMA.copy(alpha = 0.35f * k) else a.claro(0.5f)
        drawLine(reflejo, Offset(agua.left + agua.width * 0.2f, agua.center.y + agua.height * 0.1f), Offset(agua.left + agua.width * 0.45f, agua.center.y + agua.height * 0.12f), 1.6f * u, StrokeCap.Round)
        drawLine(reflejo, Offset(agua.left + agua.width * 0.3f, agua.center.y + agua.height * 0.28f), Offset(agua.left + agua.width * 0.42f, agua.center.y + agua.height * 0.3f), 1.2f * u, StrokeCap.Round)
        // El pliego mojado, colgado del borde por delante: translúcido, con el impreso que se transparenta.
        val px = boca.left + an * 0.18f
        val pl = Path().apply {
            moveTo(px, boca.center.y + w * 0.004f); lineTo(px + an * 0.3f, boca.center.y + w * 0.012f)
            quadraticTo(px + an * 0.34f, boca.center.y + w * 0.06f, px + an * 0.28f, boca.center.y + w * 0.09f)
            lineTo(px + an * 0.02f, boca.center.y + w * 0.08f); close()
        }
        drawPath(pl, a.dia(mezcla(P.PAPEL, Color(0xFFB8C4C0), 0.25f), 0f, k).copy(alpha = 0.92f))
        clipPath(pl) {
            for (j in 0 until 6) drawLine(a.dia(P.TINTA, 0f, k).copy(alpha = 0.22f), Offset(px + an * 0.04f, boca.center.y + w * (0.02f + 0.01f * j)), Offset(px + an * 0.26f, boca.center.y + w * (0.022f + 0.01f * j)), w * 0.003f)
            drawRect(a.sombra(0.2f), Offset(px + an * 0.18f, boca.center.y), Size(an * 0.2f, w * 0.1f))
        }
        drawPath(pl, a.sombra(0.25f), style = Stroke(0.8f * u))
        drawOval(a.dia(P.PINO_LUZ, 0f, k), boca.topLeft, boca.size, style = Stroke(w * 0.006f))
        drawOval(a.claro(0.35f), boca.topLeft, boca.size, style = Stroke(1f * u))
    }
}
