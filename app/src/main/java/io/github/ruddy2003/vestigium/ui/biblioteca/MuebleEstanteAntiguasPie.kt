package io.github.ruddy2003.vestigium.ui.biblioteca

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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El pie del estante de Tierras Antiguas ([MuebleEstanteAntiguas]), sobre el suelo de tablas y la alfombra: a la izquierda, cortado
 * por el borde, el saco de arpillera de la mina, abierto, con piedras en bruto que ruedan por la alfombra y el pico apoyado; a la
 * derecha, el brasero de cobre de tres patas con sus brasas y las tenazas. De noche el brasero es la tercera luz.
 */
internal object PieEstanteAntiguas {
    private val P = PaletaAntiguas

    fun DrawScope.alPie(g: MarcoAntiguas, arriba: Float) {
        val w = g.w
        // La noche del zócalo y del suelo, en su mapa de luz.
        val clave = "p:${w.toInt()}:$arriba:${g.h}:${g.a.oscuro}:${g.a.noche}"
        with(MapaLuzAntiguas) {
            pintar(2, clave, arriba, g.h, 40) { x, y -> mascaraAntiguas(g.am, x, y, g.cerca(Offset(x * w, y * w))) }
        }
        saco(g)
        brasero(g)
    }

    /** El saco de arpillera de la mina, abierto y vencido, con las piedras en bruto que asoman y las que ruedan; el pico apoyado. */
    private fun DrawScope.saco(g: MarcoAntiguas) {
        val w = g.w; val h = g.h; val u = density
        val am = g.am
        val pie = Offset(w * 0.075f, h - w * 0.03f)
        val alto = w * 0.17f; val an = w * 0.2f
        val k = g.cerca(pie - Offset(0f, alto / 2))
        // El pico, apoyado detrás del saco.
        val m0 = Offset(w * 0.2f, h - w * 0.06f); val m1 = Offset(w * 0.05f, g.suelo - w * 0.05f)
        drawLine(am.sombra(0.3f), m0 + Offset(w * 0.012f, w * 0.004f), m1 + Offset(w * 0.02f, w * 0.01f), w * 0.012f, StrokeCap.Round)
        drawLine(am.luz(Color(0xFFB08A5A), k), m0, m1, w * 0.012f, StrokeCap.Round)
        drawLine(am.claro(0.3f), m0 - Offset(w * 0.003f, 0f), m1 - Offset(w * 0.003f, 0f), 1f * u)
        val cab = Path().apply {
            moveTo(m1.x - w * 0.06f, m1.y + w * 0.03f)
            quadraticTo(m1.x - w * 0.02f, m1.y - w * 0.012f, m1.x + w * 0.01f, m1.y - w * 0.01f)
            quadraticTo(m1.x + w * 0.05f, m1.y, m1.x + w * 0.085f, m1.y + w * 0.028f)
            lineTo(m1.x + w * 0.082f, m1.y + w * 0.034f)
            quadraticTo(m1.x + w * 0.04f, m1.y + w * 0.012f, m1.x + w * 0.005f, m1.y + w * 0.01f)
            quadraticTo(m1.x - w * 0.025f, m1.y + w * 0.012f, m1.x - w * 0.06f, m1.y + w * 0.03f)
            close()
        }
        drawPath(cab, am.luz(P.HIERRO, k))
        drawPath(cab, am.brillo(0.4f, k), style = Stroke(0.7f * u))
        // El saco: el cuerpo vencido, la boca abierta y doblada, el pelo de la arpillera.
        contactoAntiguas(Offset(pie.x + w * 0.02f, pie.y + w * 0.006f), an * 1.15f, w * 0.03f, am, 0.5f)
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.6f, pie.y)
            cubicTo(pie.x - an * 0.62f, pie.y - alto * 0.6f, pie.x - an * 0.4f, pie.y - alto * 0.95f, pie.x - an * 0.1f, pie.y - alto)
            cubicTo(pie.x + an * 0.2f, pie.y - alto * 1.02f, pie.x + an * 0.42f, pie.y - alto * 0.85f, pie.x + an * 0.5f, pie.y - alto * 0.55f)
            cubicTo(pie.x + an * 0.58f, pie.y - alto * 0.25f, pie.x + an * 0.5f, pie.y - alto * 0.02f, pie.x + an * 0.3f, pie.y)
            close()
        }
        drawPath(cuerpo, am.luz(P.ARPILLERA, k))
        clipPath(cuerpo) {
            pinceladas(Rect(pie.x - an, pie.y - alto, pie.x + an, pie.y), am.luz(mezcla(P.ARPILLERA, P.CUERO_OSC, 0.5f), k), 8401, 18, u, 0.3f)
            // La trama de la arpillera.
            val trama = Path()
            var y = pie.y - alto
            while (y < pie.y) { trama.moveTo(pie.x - an, y); trama.lineTo(pie.x + an, y + w * 0.004f); y += 2.6f * u }
            drawPath(trama, am.sombra(0.12f), style = Stroke(0.6f * u))
            drawRect(am.claro(0.25f), Offset(pie.x - an * 0.6f, pie.y - alto), Size(an * 0.35f, alto))
            drawRect(am.sombra(0.32f), Offset(pie.x + an * 0.2f, pie.y - alto), Size(an * 0.4f, alto))
            // Los pliegues.
            listOf(0.1f, 0.32f).forEach { f -> drawLine(am.sombra(0.3f), Offset(pie.x - an * 0.4f + an * f, pie.y - alto * 0.8f), Offset(pie.x - an * 0.3f + an * f * 1.3f, pie.y - alto * 0.1f), 1.4f * u) }
        }
        // La boca: el borde doblado hacia fuera y las piedras que asoman.
        val boca = Rect(pie.x - an * 0.36f, pie.y - alto * 1.02f, pie.x + an * 0.36f, pie.y - alto * 0.82f)
        drawOval(am.luz(mezcla(P.ARPILLERA, P.CUERO_OSC, 0.6f), k), boca.topLeft, boca.size)
        piedras(Rect(boca.left + an * 0.04f, boca.top - w * 0.03f, boca.right - an * 0.04f, boca.center.y), 8410, 6, g, k)
        drawArc(am.luz(mezcla(P.ARPILLERA, P.SOL, 0.15f), k), 0f, 180f, false, boca.topLeft, boca.size, style = Stroke(w * 0.012f))
        // El cordel suelto.
        drawPath(Path().apply { moveTo(boca.right - an * 0.05f, boca.center.y + w * 0.01f); cubicTo(boca.right + an * 0.2f, boca.bottom + w * 0.03f, boca.right + an * 0.1f, pie.y - w * 0.02f, boca.right + an * 0.35f, pie.y - w * 0.002f) }, am.luz(P.CUERDA, k), style = Stroke(1.6f * u))
        // Las que rodaron por la alfombra.
        piedras(Rect(w * 0.18f, h - w * 0.06f, w * 0.34f, h - w * 0.028f), 8420, 4, g, g.cerca(Offset(w * 0.26f, h - w * 0.04f)))
    }

    /** Unas piedras en bruto de la mina: grises de caras planas, alguna con un cristal de color. */
    private fun DrawScope.piedras(r: Rect, s: Int, n: Int, g: MarcoAntiguas, k: Float) {
        val w = g.w; val u = density
        val am = g.am
        for (i in 0 until n) {
            val c = Offset(r.left + r.width * (i + 0.5f) / n + r.width * 0.08f * (azar(s, i) - 0.5f), r.bottom - r.height * (0.3f + 0.4f * azar(s + 1, i)))
            val t = w * (0.014f + 0.01f * azar(s + 2, i))
            val roca = poligonoAntiguas(c.x - t, c.y + t * 0.6f, c.x - t * 0.8f, c.y - t * 0.4f, c.x - t * 0.1f, c.y - t * 0.8f, c.x + t * 0.8f, c.y - t * 0.3f, c.x + t, c.y + t * 0.6f)
            drawOval(am.sombra(0.4f), Offset(c.x - t * 0.8f, c.y + t * 0.4f), Size(t * 2.2f, t * 0.6f))
            drawPath(roca, am.luz(mezcla(P.PIEDRA, P.PIEDRA_OSC, 0.4f * azar(s + 3, i)), k))
            drawPath(poligonoAntiguas(c.x - t * 0.8f, c.y - t * 0.4f, c.x - t * 0.1f, c.y - t * 0.8f, c.x + t * 0.1f, c.y), am.claro(0.3f))
            drawPath(poligonoAntiguas(c.x + t * 0.8f, c.y - t * 0.3f, c.x + t, c.y + t * 0.6f, c.x + t * 0.1f, c.y + t * 0.6f), am.sombra(0.3f))
            if (i % 2 == 0) {
                val gema = listOf(P.ESMERALDA, P.RUBI, P.AMATISTA, P.ZAFIRO)[Math.floorMod(s + i, 4)]
                drawPath(poligonoAntiguas(c.x, c.y - t * 0.5f, c.x + t * 0.3f, c.y, c.x, c.y + t * 0.2f, c.x - t * 0.3f, c.y), am.luz(gema, k))
                drawCircle(am.claro(0.8f), 0.7f * u, Offset(c.x - t * 0.1f, c.y - t * 0.3f))
            }
        }
    }

    /** La leña apilada junto al brasero, cortada por el borde: los troncos tumbados, con la corteza y los anillos de la cara. */
    private fun DrawScope.lena(g: MarcoAntiguas) {
        val w = g.w; val h = g.h; val u = density
        val am = g.am
        val k = g.cerca(Offset(w * 0.95f, h - w * 0.08f))
        val troncos = listOf(Triple(0.93f, 0.045f, 0.03f), Triple(1.0f, 0.045f, 0.032f), Triple(0.965f, 0.1f, 0.03f), Triple(1.03f, 0.1f, 0.028f))
        contactoAntiguas(Offset(w * 0.97f, h - w * 0.026f), w * 0.16f, w * 0.02f, am, 0.5f)
        troncos.forEach { (fx, fy, fr) ->
            val c = Offset(w * fx, h - w * fy)
            val r = w * fr
            // El costado del tronco, que se va hacia atrás, con su corteza.
            drawRect(am.luz(Color(0xFF4A3220), k), Offset(c.x - r, c.y - r - w * 0.02f), Size(r * 2, r + w * 0.02f))
            drawOval(am.luz(Color(0xFF3A2616), k), Offset(c.x - r, c.y - r - w * 0.03f), Size(r * 2, r * 0.8f))
            // La cara cortada, con sus anillos y una grieta.
            drawCircle(am.luz(Color(0xFF5A3A22), k), r, c)
            drawCircle(am.luz(Color(0xFFC9A26A), k), r * 0.82f, c)
            drawCircle(am.luz(Color(0xFFA67C46), k), r * 0.55f, c, style = Stroke(0.8f * u))
            drawCircle(am.luz(Color(0xFFA67C46), k), r * 0.3f, c, style = Stroke(0.8f * u))
            drawLine(am.sombra(0.4f), c, c + Offset(r * 0.6f, -r * 0.4f), 0.9f * u)
            drawCircle(am.claro(0.3f), r * 0.82f, c - Offset(r * 0.05f, r * 0.05f), style = Stroke(0.8f * u))
        }
    }

    /** El brasero de cobre de tres patas: la taza martillada, el borde vuelto, las brasas y las tenazas apoyadas; de noche, el fuego. */
    private fun DrawScope.brasero(g: MarcoAntiguas) {
        val w = g.w; val h = g.h; val u = density
        val am = g.am
        val a = g.a
        val c = g.brasero
        val k = if (a.oscuro) 1f else g.cerca(c)
        val suelo = h - w * 0.03f
        val taza = Rect(c.x - w * 0.1f, c.y - w * 0.014f, c.x + w * 0.1f, c.y + w * 0.058f)
        lena(g)
        contactoAntiguas(Offset(c.x + w * 0.01f, suelo), w * 0.26f, w * 0.034f, am, 0.5f)
        // Las tres patas de hierro, con su pie vuelto.
        listOf(-0.065f, 0f, 0.065f).forEachIndexed { i, dx ->
            val top = Offset(c.x + w * dx * 0.8f, taza.bottom - w * 0.01f)
            val bot = Offset(c.x + w * dx * 1.25f, suelo - (if (i == 1) w * 0.008f else 0f))
            drawLine(am.luz(P.HIERRO, k), top, bot, 2.6f * u, StrokeCap.Round)
            drawLine(am.brillo(0.4f, k), top - Offset(0.8f * u, 0f), bot - Offset(0.8f * u, 0f), 0.6f * u)
            drawOval(am.luz(P.HIERRO, k), Offset(bot.x - w * 0.008f, bot.y - w * 0.004f), Size(w * 0.016f, w * 0.007f))
        }
        // Las tenazas, apoyadas a la derecha.
        val t0 = Offset(taza.right + w * 0.03f, suelo); val t1 = Offset(taza.right - w * 0.01f, taza.top - w * 0.05f)
        drawLine(am.luz(P.HIERRO, k), t0, t1, 1.8f * u, StrokeCap.Round)
        drawLine(am.luz(P.HIERRO, k), t0 + Offset(w * 0.012f, 0f), t1 + Offset(w * 0.006f, 0f), 1.8f * u, StrokeCap.Round)
        drawCircle(am.luz(P.HIERRO, k), w * 0.007f, t1 + Offset(w * 0.003f, -w * 0.004f), style = Stroke(1.6f * u))
        // La taza de cobre, martillada.
        val cuerpo = Path().apply {
            moveTo(taza.left, taza.top)
            cubicTo(taza.left + taza.width * 0.05f, taza.bottom, taza.right - taza.width * 0.05f, taza.bottom, taza.right, taza.top)
            close()
        }
        drawPath(cuerpo, am.luz(P.COBRE, k))
        clipPath(cuerpo) {
            if (a.oscuro) {
                // De noche la taza se ve contra el fuego: su cara de delante en sombra, el borde encendido.
                drawRect(am.sombra(0.35f), taza.topLeft, taza.size)
            } else {
                drawRect(am.claro(0.3f), Offset(taza.left, taza.top), Size(taza.width * 0.25f, taza.height))
                drawRect(am.sombra(0.3f), Offset(taza.right - taza.width * 0.3f, taza.top), Size(taza.width * 0.3f, taza.height))
            }
            // Los golpes del martillo.
            for (i in 0 until 16) drawCircle(am.luz(if (i % 2 == 0) P.COBRE_LUZ else P.COBRE_OSC, k).copy(alpha = 0.35f), 1.6f * u, Offset(taza.left + taza.width * azar(8501, i), taza.top + taza.height * (0.2f + 0.6f * azar(8502, i))))
        }
        // El borde vuelto y las brasas.
        val boca = Rect(taza.left - w * 0.004f, taza.top - w * 0.012f, taza.right + w * 0.004f, taza.top + w * 0.012f)
        drawOval(am.luz(P.COBRE_OSC, k), boca.topLeft, boca.size)
        val brasas = Rect(boca.left + w * 0.012f, boca.top + w * 0.003f, boca.right - w * 0.012f, boca.bottom - w * 0.003f)
        val encendidas = a.lumbre
        drawOval(if (encendidas) P.BRASA else am.luz(Color(0xFF3A2E2A), k), brasas.topLeft, brasas.size)
        for (i in 0 until 9) {
            val q = Offset(brasas.left + brasas.width * (0.1f + 0.8f * azar(8503, i)), brasas.top + brasas.height * (0.2f + 0.5f * azar(8504, i)))
            val r = w * (0.008f + 0.006f * azar(8505, i))
            drawCircle(if (encendidas) (if (i % 3 == 0) P.FUEGO else Color(0xFFB8361A)) else am.luz(if (i % 2 == 0) Color(0xFF55463E) else Color(0xFF8C7E74), k), r, q)
        }
        drawOval(am.luz(P.COBRE_LUZ, k), Offset(boca.left, boca.top), Size(boca.width, boca.height * 0.35f), style = Stroke(1.4f * u))
        if (a.oscuro) drawArc(P.LLAMA.copy(alpha = 0.8f), 180f, 180f, false, boca.topLeft, boca.size, style = Stroke(1.6f * u))
        // El fuego: de noche, unas llamas bajas y el charco en el suelo y el zócalo; en los Sueños, solo brasas.
        if (a.oscuro) {
            val f = if (a.noche) 0.6f else 1f
            drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.34f * f), P.BRASA.copy(alpha = 0.12f * f), Color.Transparent), c, w * 0.32f), w * 0.32f, c, blendMode = BlendMode.Screen)
            if (!a.noche) {
                listOf(-0.03f to 0.05f, 0f to 0.075f, 0.028f to 0.055f).forEach { (dx, al) ->
                    val base = Offset(c.x + w * dx, boca.center.y)
                    val llama = Path().apply {
                        moveTo(base.x - w * 0.014f, base.y)
                        quadraticTo(base.x - w * 0.012f, base.y - w * al * 0.6f, base.x + w * 0.004f, base.y - w * al)
                        quadraticTo(base.x + w * 0.01f, base.y - w * al * 0.4f, base.x + w * 0.014f, base.y)
                        close()
                    }
                    drawPath(llama, P.LLAMA.copy(alpha = 0.9f))
                    drawPath(Path().apply { addOval(Rect(base.x - w * 0.006f, base.y - w * al * 0.45f, base.x + w * 0.006f, base.y)) }, P.FUEGO)
                }
            }
            mancha(Offset(c.x, boca.center.y), w * 0.07f, P.FUEGO.copy(alpha = 0.45f * f))
        } else if (a.noche) {
            mancha(Offset(c.x, boca.center.y), w * 0.05f, P.BRASA.copy(alpha = 0.3f))
        }
    }
}
