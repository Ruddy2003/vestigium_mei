package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El pie del estante de Galia ([MuebleEstanteGalia]): el suelo de tierra apisonada con juncos esparcidos y la estera de
 * juncos tejida a la par; a la izquierda, el montón de leños de roble partidos; en medio, el hogar con su cerco de piedras (de
 * día, la ceniza, los tizones y un hilo de humo; de noche, las brasas y las llamas bajas, que tiñen el suelo y lo de abajo);
 * a la derecha, el cántaro del agua con su cazo, y el escabel de tres patas. Todo grande, agrupado y cortado por los bordes.
 */
internal object PieEstanteGalia {
    private val P = PaletaGalia

    fun DrawScope.alPie(a: AireGalia, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width
        val fuego = Offset(w * MuebleEstanteGalia.HOGAR_X, b.top + b.height * MuebleEstanteGalia.HOGAR_Y)
        tierra(Rect(0f, suelo, w, h), fuego, a, cerca)
        clipRect(0f, suelo - w * 0.2f, w, h) {
            estera(Rect(w * 0.44f, suelo + (h - suelo) * 0.3f, w * 1.04f, h + w * 0.02f), a, cerca)
            lenos(Offset(w * 0.0f, h), a, cerca)
            hogar(fuego, a, cerca)
            cantaro(Offset(w * 0.9f, suelo + (h - suelo) * 0.66f), a, cerca)
            escabel(Offset(w * 0.7f, suelo + (h - suelo) * 0.86f), a, cerca)
        }
        // La sombra de la solera en el suelo, franca junto a ella.
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.36f), Offset(0f, suelo), Size(w, w * 0.008f))
        drawRect(a.sombra(0.14f), Offset(0f, suelo + w * 0.008f), Size(w, w * 0.016f))
    }

    /** La tierra apisonada: de día, más clara junto a la puerta; de noche, el charco del hogar. Piedrecitas y juncos esparcidos. */
    private fun DrawScope.tierra(r: Rect, fuego: Offset, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        if (a.oscuro) {
            val radios = floatArrayOf(0f, 0.06f, 0.13f, 0.22f, 0.34f, 0.5f, 0.75f)
            val col = radios.map { rr -> a.luz(P.TIERRA, MuebleEstanteGalia.hogar(rr, 0f, a.noche)) }
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 0.75f to col[i] }, center = fuego, radius = w * 0.75f), r.topLeft, r.size)
        } else {
            drawRect(Brush.horizontalGradient(listOf(a.dia(mezcla(P.TIERRA, P.TIERRA_LUZ, 0.4f), 0f), a.dia(P.TIERRA, 0f), a.dia(P.TIERRA, -0.12f)), 0f, w), r.topLeft, r.size)
            if (a.lumbre) drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.22f), P.LLAMA.copy(alpha = 0f)), fuego, w * 0.3f), w * 0.3f, fuego)
        }
        // Las huellas del barrido y las piedrecitas, cada una con su luz; los juncos sueltos.
        val piedras = Path(); val luces = Path(); val juncos = Path()
        for (k in 0 until 34) {
            val p = Offset(w * azar(9800, k), r.top + r.height * (0.08f + 0.9f * azar(9801, k)))
            val rr = (0.8f + 1.8f * azar(9802, k)) * u
            piedras.addOval(Rect(p.x - rr, p.y - rr * 0.6f, p.x + rr, p.y + rr * 0.6f))
            luces.addOval(Rect(p.x - rr * 0.7f, p.y - rr * 0.6f, p.x + rr * 0.2f, p.y - rr * 0.1f))
        }
        for (k in 0 until 22) {
            val p = Offset(w * azar(9810, k), r.top + r.height * (0.1f + 0.85f * azar(9811, k)))
            val l = w * (0.02f + 0.03f * azar(9812, k)); val ang = (azar(9813, k) - 0.5f) * 1.2f
            juncos.moveTo(p.x, p.y); juncos.lineTo(p.x + l * cos(ang), p.y + l * sin(ang) * 0.4f)
        }
        val k = cerca(Offset(w * 0.5f, r.center.y))
        drawPath(piedras, a.dia(P.PIEDRA_OSC, 0f, k).copy(alpha = 0.7f))
        drawPath(luces, a.claro(0.3f))
        drawPath(juncos, a.dia(P.JUNCO, 0f, k).copy(alpha = 0.75f), style = Stroke(1f * u, cap = StrokeCap.Round))
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.22f), a.sombra(0f)), r.top, r.top + r.height * 0.35f), r.topLeft, Size(r.width, r.height * 0.35f))
    }

    /** La estera de juncos tejida a la par: cuadros de haces que van una vez de través y otra a lo largo, y la orilla trenzada. */
    private fun DrawScope.estera(r: Rect, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        // En escorzo: el lado de atrás más corto que el de delante.
        val forma = poligonoGalia(r.left + w * 0.04f, r.top, r.right, r.top, r.right, r.bottom, r.left - w * 0.02f, r.bottom)
        translate(w * 0.006f, w * 0.004f) { drawPath(forma, a.sombra(0.3f)) }
        val k = cerca(r.center)
        drawPath(forma, a.dia(P.JUNCO, 0f, k))
        clipPath(forma) {
            val lado = w * 0.042f
            val trav = Path(); val largo = Path(); val sombras = Path()
            var y = r.top; var fila = 0
            while (y < r.bottom) {
                var x = r.left - w * 0.03f; var col = 0
                while (x < r.right) {
                    val q = Rect(x, y, x + lado, y + lado * 0.5f)
                    if ((fila + col) % 2 == 0) {
                        for (i in 0 until 4) { val yy = q.top + q.height * (i + 0.5f) / 4f; trav.moveTo(q.left + 1f * u, yy); trav.lineTo(q.right - 1f * u, yy) }
                    } else {
                        for (i in 0 until 4) { val xx = q.left + q.width * (i + 0.5f) / 4f; largo.moveTo(xx, q.top + 0.6f * u); largo.lineTo(xx, q.bottom - 0.6f * u) }
                    }
                    sombras.moveTo(q.left, q.bottom); sombras.lineTo(q.right, q.bottom)
                    x += lado; col++
                }
                y += lado * 0.5f; fila++
            }
            drawPath(trav, a.dia(P.JUNCO_OSC, 0f, k).copy(alpha = 0.55f), style = Stroke(0.8f * u))
            drawPath(largo, a.dia(mezcla(P.JUNCO, P.MIMBRE_LUZ, 0.4f), 0f, k).copy(alpha = 0.7f), style = Stroke(0.8f * u))
            drawPath(sombras, a.sombra(0.22f), style = Stroke(0.7f * u))
            // El desgaste en el medio, donde se pisa.
            ovaloGalia(a.dia(P.JUNCO_OSC, 0f, k).copy(alpha = 0.25f), Rect(r.left + r.width * 0.2f, r.top + r.height * 0.3f, r.left + r.width * 0.7f, r.bottom))
        }
        // La orilla trenzada de atrás y del lado.
        drawLine(a.dia(P.JUNCO_OSC, 0f, k), Offset(r.left + w * 0.04f, r.top), Offset(r.right, r.top), w * 0.008f)
        drawLine(a.dia(P.JUNCO_OSC, 0f, k), Offset(r.left + w * 0.04f, r.top), Offset(r.left - w * 0.02f, r.bottom), w * 0.008f)
        drawLine(a.claro(0.3f), Offset(r.left + w * 0.04f, r.top - w * 0.002f), Offset(r.right, r.top - w * 0.002f), 0.9f * u)
    }

    /** El montón de leños de roble partidos, apilados de testa contra el borde: la cara de la testa con sus anillos y su grieta. */
    private fun DrawScope.lenos(esquina: Offset, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val r0 = w * 0.04f
        val sitios = listOf(
            Offset(0.03f, 0.03f), Offset(0.11f, 0.035f), Offset(0.185f, 0.03f),
            Offset(0.065f, 0.1f), Offset(0.145f, 0.098f),
            Offset(0.03f, 0.165f), Offset(0.105f, 0.165f),
        )
        // La sombra del montón en el suelo, larga hacia la derecha.
        ovaloGalia(a.sombra(0.35f), Rect(esquina.x, esquina.y - w * 0.05f, esquina.x + w * 0.29f, esquina.y + w * 0.02f))
        sitios.forEachIndexed { i, s ->
            val c = Offset(esquina.x + w * s.x, esquina.y - w * s.y)
            val r = r0 * (0.85f + 0.3f * azar(9820, i))
            val k = cerca(c)
            // El costado del leño (corteza), que asoma un poco a la derecha y abajo: se pinta antes.
            drawCircle(a.dia(P.ROBLE_OSC, 0f, k), r, c + Offset(r * 0.28f, r * 0.12f))
            // La testa: madera clara, los anillos, la grieta radial y la corteza alrededor.
            val testa = Path().apply {
                for (j in 0..12) {
                    val t = j / 12f * 6.283f
                    val rr = r * (1f + 0.06f * sin(t * 3f + i))
                    val p = Offset(c.x + rr * cos(t), c.y + rr * sin(t))
                    if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(testa, a.dia(P.CUERO, 0f, k))
            val dentro = r * 0.82f
            drawCircle(a.dia(mezcla(P.HAYA, P.ROBLE_LUZ, 0.4f), 0f, k), dentro, c)
            for (q in 1..3) drawCircle(a.dia(P.ROBLE, 0f, k).copy(alpha = 0.45f), dentro * q / 4f, c + Offset(dentro * 0.04f * q, 0f), style = Stroke(0.7f * u))
            // Las grietas de secado: cortas, desde el borde hacia dentro, sin llegar al corazón.
            for (q in 0 until 3) {
                val ang = (azar(9821, i * 3 + q) * 6.283f)
                val d0 = dentro * (0.95f - 0.05f * q); val d1 = dentro * (0.45f + 0.25f * azar(9822, i * 3 + q))
                drawLine(a.luz(P.ROBLE_HONDO, k).copy(alpha = 0.8f), c + Offset(d0 * cos(ang), d0 * sin(ang)), c + Offset(d1 * cos(ang + 0.1f), d1 * sin(ang + 0.1f)), 1f * u)
            }
            drawArc(a.claro(0.35f), 160f, 110f, false, Offset(c.x - dentro, c.y - dentro), Size(dentro * 2, dentro * 2), style = Stroke(1f * u))
            drawArc(a.sombra(0.3f), -20f, 120f, false, Offset(c.x - dentro, c.y - dentro), Size(dentro * 2, dentro * 2), style = Stroke(1.6f * u))
        }
    }

    /**
     * El hogar: el cerco de piedras en escorzo, la ceniza y los tizones cruzados. De día, la ceniza con su hilo de humo; de noche,
     * las brasas, las llamas bajas y su halo. Las piedras de delante se encienden por arriba.
     */
    private fun DrawScope.hogar(c: Offset, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val rx = w * 0.15f; val ry = w * 0.038f
        val lumbre = a.lumbre
        val f = if (a.oscuro && a.noche) 0.55f else if (a.oscuro) 1f else 0.6f
        // El halo de la lumbre en el aire (de noche); el suelo ya lo tiñe la tierra.
        if (lumbre) drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.32f * f), P.LLAMA.copy(alpha = 0f)), c - Offset(0f, w * 0.03f), w * 0.26f), w * 0.26f, c - Offset(0f, w * 0.03f))
        // La cama de ceniza dentro del cerco.
        ovaloGalia(if (lumbre) mezcla(P.BRASA, P.HOLLIN, 0.55f) else a.dia(P.CENIZA, 0f), Rect(c.x - rx * 0.88f, c.y - ry * 0.8f, c.x + rx * 0.88f, c.y + ry * 0.8f))
        if (lumbre) ovaloGalia(Brush.radialGradient(listOf(P.FUEGO.copy(alpha = 0.9f * f), P.BRASA.copy(alpha = 0.8f * f), P.BRASA.copy(alpha = 0f)), c, rx * 0.6f), Rect(c.x - rx * 0.7f, c.y - ry * 0.6f, c.x + rx * 0.7f, c.y + ry * 0.6f))
        // Las piedras de atrás (más chicas), los tizones y luego las de delante.
        fun piedra(t: Float, delante: Boolean, i: Int) {
            val p = Offset(c.x + rx * cos(t), c.y + ry * sin(t))
            val pw = w * (0.046f + 0.016f * azar(9830, i)) * (if (delante) 1f else 0.8f)
            val ph = pw * 0.62f
            val k = if (lumbre) 1f else cerca(p)
            val sil = Path().apply { addOval(Rect(p.x - pw / 2, p.y - ph / 2, p.x + pw / 2, p.y + ph / 2)) }
            ovaloGalia(a.sombra(0.35f), Rect(p.x - pw / 2 + w * 0.004f, p.y - ph / 2 + w * 0.004f, p.x + pw / 2 + w * 0.004f, p.y + ph / 2 + w * 0.003f))
            drawPath(sil, if (lumbre && !a.oscuro) a.dia(P.PIEDRA, 0f) else a.dia(P.PIEDRA, 0f, if (lumbre) (0.55f + 0.45f * (if (delante) 0.5f else 1f)) * f else k))
            clipPath(sil) {
                ovaloGalia(a.claro(0.4f), Rect(p.x - pw * 0.42f, p.y - ph * 0.5f, p.x + pw * 0.1f, p.y - ph * 0.05f))
                ovaloGalia(a.frio(0.35f), Rect(p.x, p.y - ph * 0.1f, p.x + pw * 0.6f, p.y + ph * 0.6f))
                // El canto que mira al fuego, encendido.
                if (lumbre) {
                    val hacia = Offset(c.x - p.x, c.y - p.y)
                    val d = hacia.getDistance().coerceAtLeast(1f)
                    drawCircle(P.LLAMA.copy(alpha = 0.55f * f), pw * 0.4f, p + hacia * (pw * 0.4f / d))
                }
            }
        }
        for (i in 0 until 7) piedra(3.4f + i * 0.45f, false, i)
        // Los tizones: tres leños echados casi en paralelo (sin cruzarse), negros, con la punta encendida de noche.
        listOf(Triple(-0.62f, 0.1f, 0.35f), Triple(-0.3f, 0.42f, 0.0f), Triple(0.05f, 0.66f, -0.3f)).forEachIndexed { i, (x0, x1, dy) ->
            val p0 = Offset(c.x + rx * x0, c.y + ry * (0.5f + dy * 0.3f)); val p1 = Offset(c.x + rx * x1, c.y - ry * (0.2f - dy * 0.5f))
            drawLine(a.sombra(0.4f), p0 + Offset(0f, w * 0.005f), p1 + Offset(0f, w * 0.005f), w * 0.016f, StrokeCap.Round)
            drawLine(if (lumbre) mezcla(P.HOLLIN, P.BRASA, 0.25f) else a.dia(P.HOLLIN, 0f), p0, p1, w * 0.015f, StrokeCap.Round)
            drawLine(if (lumbre) P.BRASA.copy(alpha = 0.8f * f) else a.dia(P.CENIZA, 0.1f).copy(alpha = 0.5f), p0 - Offset(0f, w * 0.004f), p1 - Offset(0f, w * 0.004f), 1.4f * u, StrokeCap.Round)
            if (lumbre) drawCircle(P.FUEGO.copy(alpha = 0.85f * f), w * 0.006f, p1)
        }
        if (lumbre) {
            // Las llamas bajas, varias lenguas.
            fun lengua(dx: Float, alto: Float, an: Float, col: Color) {
                val base = Offset(c.x + dx, c.y - ry * 0.25f)
                drawPath(Path().apply {
                    moveTo(base.x - an, base.y)
                    cubicTo(base.x - an * 1.1f, base.y - alto * 0.5f, base.x - an * 0.2f, base.y - alto * 0.7f, base.x + an * 0.2f, base.y - alto)
                    cubicTo(base.x + an * 0.3f, base.y - alto * 0.6f, base.x + an * 1.1f, base.y - alto * 0.4f, base.x + an, base.y)
                    close()
                }, col)
            }
            val alto = w * 0.075f * f
            listOf(-0.32f to 0.7f, 0.05f to 1f, 0.36f to 0.62f).forEach { (dx, e) ->
                lengua(rx * dx, alto * e, w * 0.02f * e, P.BRASA.copy(alpha = 0.9f))
                lengua(rx * dx + w * 0.002f, alto * e * 0.75f, w * 0.014f * e, P.LLAMA)
                lengua(rx * dx + w * 0.003f, alto * e * 0.45f, w * 0.008f * e, P.FUEGO)
            }
            for (q in 0 until 5) drawCircle(P.FUEGO.copy(alpha = 0.8f * f), (0.8f + 0.8f * azar(9840, q)) * u, Offset(c.x + rx * (azar(9841, q) - 0.5f) * 0.8f, c.y - alto * (1.1f + 0.6f * azar(9842, q))))
        } else {
            // El hilo de humo que sube de la ceniza tibia.
            val humo = Path().apply { moveTo(c.x + rx * 0.05f, c.y - ry * 0.4f); cubicTo(c.x - rx * 0.2f, c.y - w * 0.06f, c.x + rx * 0.3f, c.y - w * 0.1f, c.x + rx * 0.05f, c.y - w * 0.16f) }
            drawPath(humo, P.CENIZA.copy(alpha = 0.35f), style = Stroke(3f * u, cap = StrokeCap.Round))
            drawPath(humo, Color.White.copy(alpha = 0.18f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
        for (i in 0 until 6) piedra(0.25f + i * 0.5f, true, i + 7)
    }

    /** El cántaro del agua, de barro pardo, con su asa y el cazo de madera colgado del labio. */
    private fun DrawScope.cantaro(pie: Offset, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val al = w * 0.19f; val an = w * 0.17f
        val k = cerca(pie - Offset(0f, al * 0.5f))
        val sil = Path().apply {
            moveTo(pie.x - an * 0.24f, pie.y)
            cubicTo(pie.x - an * 0.56f, pie.y - al * 0.14f, pie.x - an * 0.6f, pie.y - al * 0.6f, pie.x - an * 0.24f, pie.y - al * 0.84f)
            lineTo(pie.x - an * 0.2f, pie.y - al * 0.94f); lineTo(pie.x - an * 0.27f, pie.y - al)
            lineTo(pie.x + an * 0.27f, pie.y - al); lineTo(pie.x + an * 0.2f, pie.y - al * 0.94f); lineTo(pie.x + an * 0.24f, pie.y - al * 0.84f)
            cubicTo(pie.x + an * 0.6f, pie.y - al * 0.6f, pie.x + an * 0.56f, pie.y - al * 0.14f, pie.x + an * 0.24f, pie.y)
            close()
        }
        contactoGalia(pie, an * 1.1f, w * 0.02f, a, 0.45f, -1f)
        val asa = Path().apply { moveTo(pie.x - an * 0.22f, pie.y - al * 0.88f); cubicTo(pie.x - an * 0.62f, pie.y - al * 0.92f, pie.x - an * 0.66f, pie.y - al * 0.62f, pie.x - an * 0.46f, pie.y - al * 0.52f) }
        drawPath(asa, a.dia(P.PARDO, -0.1f, k), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
        drawPath(sil, a.dia(P.PARDO, 0f, k))
        clipPath(sil) {
            cilindroGalia(Rect(pie.x - an * 0.6f, pie.y - al, pie.x + an * 0.6f, pie.y), a, k, if (a.oscuro) -1f else -1f, 1.3f)
            drawRect(a.sombra(0.35f), Offset(pie.x - an * 0.6f, pie.y - al * 0.86f), Size(an * 1.2f, al * 0.04f))
            for (j in 0 until 4) drawLine(a.sombra(0.15f), Offset(pie.x - an * 0.6f, pie.y - al * (0.5f + 0.06f * j)), Offset(pie.x + an * 0.6f, pie.y - al * (0.5f + 0.06f * j)), 0.8f * u)
        }
        ovaloGalia(a.luz(P.HOLLIN, k), Rect(pie.x - an * 0.24f, pie.y - al - an * 0.04f, pie.x + an * 0.24f, pie.y - al + an * 0.04f))
        // El cazo, colgado del labio por su mango ganchudo.
        val cz = Offset(pie.x + an * 0.3f, pie.y - al * 0.66f)
        drawLine(a.dia(P.HAYA_OSC, 0f, k), Offset(pie.x + an * 0.12f, pie.y - al * 1.02f), cz, w * 0.006f, StrokeCap.Round)
        ovaloGalia(a.sombra(0.3f), Rect(cz.x - an * 0.1f + w * 0.004f, cz.y - an * 0.05f + w * 0.004f, cz.x + an * 0.12f + w * 0.004f, cz.y + an * 0.1f + w * 0.004f))
        ovaloGalia(a.dia(P.HAYA_OSC, 0f, k), Rect(cz.x - an * 0.1f, cz.y - an * 0.05f, cz.x + an * 0.12f, cz.y + an * 0.1f))
        drawLine(a.brillo(0.6f, k), Offset(pie.x - an * 0.38f, pie.y - al * 0.62f), Offset(pie.x - an * 0.36f, pie.y - al * 0.3f), 1.6f * u, StrokeCap.Round)
    }

    /** El escabel de tres patas, de un tronco: el asiento en escorzo, con su veta y su canto, y las patas abiertas. */
    private fun DrawScope.escabel(pie: Offset, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(pie)
        val asiento = Rect(pie.x - w * 0.07f, pie.y - w * 0.1f, pie.x + w * 0.07f, pie.y - w * 0.07f)
        ovaloGalia(a.sombra(0.35f), Rect(pie.x - w * 0.06f, pie.y - w * 0.012f, pie.x + w * 0.1f, pie.y + w * 0.012f))
        listOf(-0.055f to 0.004f, 0.06f to 0.002f, 0.004f to -0.012f).forEach { (dx, dy) ->
            drawLine(a.dia(P.ROBLE_OSC, 0f, k), Offset(pie.x + dx * 0.4f * w, asiento.center.y), Offset(pie.x + dx * w, pie.y + dy * w), w * 0.012f, StrokeCap.Round)
        }
        ovaloGalia(a.dia(P.ROBLE, 0f, k), Rect(asiento.left, asiento.top + w * 0.008f, asiento.right, asiento.bottom + w * 0.008f))
        ovaloGalia(a.dia(P.ROBLE_LUZ, 0f, k), asiento)
        clipPath(Path().apply { addOval(asiento) }) {
            for (q in 1..3) ovaloGalia(a.luz(P.ROBLE_OSC, k).copy(alpha = 0.35f), Rect(asiento.center.x - asiento.width * q / 8f, asiento.center.y - asiento.height * q / 8f, asiento.center.x + asiento.width * q / 8f, asiento.center.y + asiento.height * q / 8f), style = Stroke(0.7f * u))
            ovaloGalia(a.claro(0.3f), Rect(asiento.left, asiento.top, asiento.center.x, asiento.center.y))
        }
        // Un vellón de oveja echado encima, que cae por un lado: el borde hecho de mechones y la lana en rizos.
        val vx0 = asiento.left + w * 0.01f; val vx1 = asiento.right + w * 0.012f
        val vy0 = asiento.top - w * 0.012f
        val v = Path().apply {
            moveTo(vx0, asiento.center.y + w * 0.004f)
            // Arriba, de mechón en mechón.
            var x = vx0; var q = 0
            while (x < vx1 - w * 0.01f) {
                val nx = minOf(vx1 - w * 0.01f, x + w * 0.018f)
                quadraticTo((x + nx) / 2f, vy0 - w * (0.006f + 0.004f * azar(9850, q)), nx, vy0 + w * 0.002f)
                x = nx; q++
            }
            // La caída por la derecha, hasta más abajo del asiento.
            quadraticTo(vx1 + w * 0.008f, asiento.center.y, vx1 - w * 0.002f, asiento.bottom + w * 0.03f)
            quadraticTo(vx1 - w * 0.012f, asiento.bottom + w * 0.042f, vx1 - w * 0.022f, asiento.bottom + w * 0.03f)
            quadraticTo(vx1 - w * 0.03f, asiento.bottom + w * 0.008f, asiento.center.x, asiento.bottom + w * 0.004f)
            quadraticTo(asiento.left + w * 0.02f, asiento.bottom + w * 0.002f, vx0, asiento.center.y + w * 0.004f)
            close()
        }
        drawPath(Path().apply { addPath(v, Offset(w * 0.006f, w * 0.006f)) }, a.sombra(0.3f))
        drawPath(v, a.dia(P.LANA_CRUDA, 0f, k))
        clipPath(v) {
            val rizos = Path()
            for (q in 0 until 16) {
                val cx = vx0 + (vx1 - vx0) * azar(9851, q); val cy = vy0 + w * 0.05f * azar(9852, q)
                rizos.moveTo(cx - w * 0.006f, cy); rizos.quadraticTo(cx, cy - w * 0.006f, cx + w * 0.006f, cy + w * 0.002f)
            }
            drawPath(rizos, a.dia(P.BLANCO_SOMBRA, -0.1f, k).copy(alpha = 0.7f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            drawRect(a.claro(0.3f), Offset(vx0, vy0 - w * 0.01f), Size(vx1 - vx0, w * 0.012f))
            drawRect(a.sombra(0.32f), Offset(vx1 - w * 0.026f, vy0), Size(w * 0.04f, w * 0.09f))
        }
    }
}
