package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El pie del estante de Napoleónico ([MuebleEstanteNapoleon]): el parqué de roble en espiga, encerado (una baldosa pintada una
 * vez y repetida, que se multiplica sobre la luz del suelo), y encima, cortados por los bordes: la silla de montar del oficial
 * sobre su mantilla de paño azul con galón, con un estribo caído; el farol de hojalata calada (la luz del pie: de noche, sus
 * agujeros echan puntos de luz en el suelo y en lo que tiene cerca); la tapa de viaje de una de las cajas, apoyada en el
 * zócalo; y el cesto de leña de abedul para la estufa, que queda fuera del cuadro.
 */
internal object PieEstanteNapoleon {
    private val P = PaletaNapoleon

    // ---------------------------------------------------------------- la baldosa del parqué

    private var densidadBaldosa = -1f
    private var baldosa: ImageBitmap? = null

    /**
     * La baldosa del parqué en espiga, ya en escorzo (aplastada a lo alto), en tonos claros para multiplicar: cada tabla de su
     * tono, su veta a lo largo y la junta oscura. Su tamaño en píxeles es entero y es justo el período del dibujo (las tablas
     * de largo cuatro veces su ancho, a 45 grados), así que se repite sin costura.
     */
    private fun baldosa(u: Float): ImageBitmap {
        val b0 = baldosa
        if (b0 != null && densidadBaldosa == u) return b0
        val n = maxOf(1, (18.4f * u / 2.625f / 11f).roundToInt())
        val th = 11 * n; val tw = 80 * n
        val img = ImageBitmap(tw, th)
        val esc = 0.55f
        val b = th / (esc * sqrt(2f))
        val l = 4f * b
        val r2 = sqrt(2f) / 2f
        CanvasDrawScope().draw(Density(u), LayoutDirection.Ltr, Canvas(img), Size(tw.toFloat(), th.toFloat())) {
            drawRect(Color(0xFFF4EEE4))
            // Un punto del marco girado (s a lo largo de las tablas horizontales, t a lo ancho) en la baldosa.
            fun p(s: Float, t: Float) = Offset((s - t) * r2, (s + t) * r2 * esc)
            val juntas = Path(); val vetas = Path()
            for (i in -8..8) for (j in -3..3) {
                val os = i * b + j * (l + b); val ot = i * b + j * (b - l)
                // La tabla echada y la parada, cada una con su tono.
                listOf(floatArrayOf(os, ot, os + l, ot + b), floatArrayOf(os + l, ot + b - l, os + l + b, ot + b)).forEachIndexed { q, r ->
                    val a = p(r[0], r[1]); val bb = p(r[2], r[1]); val c = p(r[2], r[3]); val d = p(r[0], r[3])
                    val minX = minOf(a.x, bb.x, c.x, d.x); val maxX = maxOf(a.x, bb.x, c.x, d.x)
                    val minY = minOf(a.y, bb.y, c.y, d.y); val maxY = maxOf(a.y, bb.y, c.y, d.y)
                    if (maxX < -2f || minX > tw + 2f || maxY < -2f || minY > th + 2f) return@forEachIndexed
                    val tabla = Path().apply { moveTo(a.x, a.y); lineTo(bb.x, bb.y); lineTo(c.x, c.y); lineTo(d.x, d.y); close() }
                    val tono = azar(9400 + Math.floorMod(i, 8) * 7 + q, Math.floorMod(j, 4))
                    drawPath(tabla, Color(1f - 0.16f * tono, 1f - 0.2f * tono, 1f - 0.26f * tono))
                    juntas.addPath(tabla)
                    // La veta: dos rayas a lo largo de la tabla.
                    for (v in 0 until 2) {
                        val f = 0.3f + 0.4f * v
                        if (q == 0) { val v0 = p(r[0], r[1] + b * f); val v1 = p(r[2], r[1] + b * f + 0.6f); vetas.moveTo(v0.x, v0.y); vetas.lineTo(v1.x, v1.y) }
                        else { val v0 = p(r[0] + b * f, r[1]); val v1 = p(r[0] + b * f + 0.6f, r[3]); vetas.moveTo(v0.x, v0.y); vetas.lineTo(v1.x, v1.y) }
                    }
                }
            }
            drawPath(vetas, Color(0xFFD8C8B0).copy(alpha = 0.7f), style = Stroke(0.6f * u))
            drawPath(juntas, Color(0xFF8A7058), style = Stroke(0.8f * u))
        }
        baldosa = img; densidadBaldosa = u
        return img
    }

    // ---------------------------------------------------------------- el suelo

    /** El suelo de [suelo] a [h]: el roble a la luz del momento, la espiga multiplicada encima, el brillo de la cera y la sombra del zócalo. */
    fun DrawScope.suelo(a: AireNapoleon, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val r = Rect(0f, suelo - 1f, w, h + 1f)
        val farol = Offset(w * MuebleEstanteNapoleon.FAROL_X, b.top + b.height * MuebleEstanteNapoleon.FAROL_Y)
        if (a.oscuro) {
            val radios = floatArrayOf(0f, 0.06f, 0.13f, 0.22f, 0.34f, 0.5f, 0.7f)
            val col = radios.map { rr -> a.luz(P.ROBLE, MuebleEstanteNapoleon.farol(rr, rr * 0.3f, a.noche)) }
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 0.7f to col[i] }, center = farol + Offset(0f, w * 0.06f), radius = w * 0.7f), r.topLeft, r.size)
        } else {
            val claro = a.dia(P.ROBLE, 0.04f); val hondo = a.dia(P.ROBLE, -0.22f)
            drawRect(Brush.horizontalGradient(listOf(claro, mezcla(claro, hondo, 0.5f), hondo), 0f, w), r.topLeft, r.size)
        }
        val img = baldosa(u)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = suelo - (img.height - 1).toFloat()
            var fila = 0
            while (y < h) {
                var x = -(fila * img.width / 3 % img.width).toFloat()
                while (x < w) {
                    drawImage(img, dstOffset = IntOffset(x.roundToInt(), y.roundToInt()), dstSize = IntSize(img.width, img.height), blendMode = BlendMode.Multiply)
                    x += img.width
                }
                y += img.height; fila = 0
            }
        }
        // El brillo de la cera: de día, la luz de la ventana en un paño alargado; de noche, el reflejo del farol.
        if (!a.oscuro) mancha(Offset(w * 0.3f, suelo + (h - suelo) * 0.55f), w * 0.3f, luzDeCera(a))
        // La sombra del zócalo en el suelo, franca junto a él.
        drawRect(a.sombra(0.5f), Offset(0f, suelo), Size(w, 2.6f * u))
        drawRect(a.sombra(0.22f), Offset(0f, suelo + 2.6f * u), Size(w, w * 0.02f))
    }

    private fun luzDeCera(a: AireNapoleon): Color = if (a.noche) P.OCASO.copy(alpha = 0.16f) else Color(0xFFFFF6E0).copy(alpha = 0.2f)

    // ---------------------------------------------------------------- lo que hay en el suelo

    fun DrawScope.objetos(a: AireNapoleon, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        tapaDeViaje(a, b, suelo, h, cerca)
        cesto(a, b, suelo, h, cerca)
        silla(a, b, suelo, h, cerca)
        farol(a, b, suelo, h, cerca)
    }

    /** La tapa de viaje de una caja, apoyada de canto contra el zócalo, ladeada, con sus esquinas de latón. */
    private fun DrawScope.tapaDeViaje(a: AireNapoleon, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val pie = suelo + (h - suelo) * 0.42f
        val r = Rect(w * 0.585f, pie - w * 0.135f, w * 0.78f, pie)
        val k = cerca(r.center)
        rotate(-7f, Offset(r.left, r.bottom)) {
            drawRect(a.sombra(0.35f), Offset(r.left + w * 0.01f, r.top + w * 0.006f), r.size)
            drawRect(a.dia(P.CAOBA, 0f, k), r.topLeft, r.size)
            // Su canto (el grueso de la tabla), a la izquierda, y la veta flameada.
            drawRect(a.dia(P.CAOBA_LUZ, -0.05f, k), r.topLeft, Size(w * 0.007f, r.height))
            val veta = Path()
            for (j in 0 until 7) { val x = r.left + r.width * (0.1f + 0.13f * j); veta.moveTo(x, r.bottom); veta.quadraticTo(x + r.width * 0.06f, r.center.y, x + r.width * 0.02f, r.top) }
            clipRect(r.left, r.top, r.right, r.bottom) { drawPath(veta, a.dia(P.CAOBA_OSC, 0f, k).copy(alpha = 0.45f), style = Stroke(0.8f * u)) }
            drawRect(a.dia(P.LATON, 0f, k), Rect(r.left + w * 0.012f, r.top + w * 0.012f, r.right - w * 0.012f, r.bottom - w * 0.012f).topLeft,
                Size(r.width - w * 0.024f, r.height - w * 0.024f), style = Stroke(1f * u))
            with(ObraEstanteNapoleon) {
                cantonera(Offset(r.left, r.top), w * 0.03f, w * 0.008f, 1f, 1f, a, k)
                cantonera(Offset(r.right, r.top), w * 0.03f, w * 0.008f, -1f, 1f, a, k)
                cantonera(Offset(r.left, r.bottom), w * 0.03f, w * 0.008f, 1f, -1f, a, k)
                cantonera(Offset(r.right, r.bottom), w * 0.03f, w * 0.008f, -1f, -1f, a, k)
            }
            // Las dos fallebas de latón que la sujetaban a la caja.
            listOf(0.3f, 0.7f).forEach { f -> drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(r.left + r.width * f - w * 0.008f, r.top - w * 0.004f), Size(w * 0.016f, w * 0.01f)) }
            drawRect(a.brillo(0.4f, k), r.topLeft, Size(r.width, 1f * u))
            if (a.oscuro) drawRect(a.filo(0.5f * k), Offset(r.left, r.top), Size(1.4f * u, r.height))
        }
        ovaloNapoleon(a.sombra(0.4f), Rect(r.left - w * 0.01f, r.bottom - w * 0.008f, r.right + w * 0.02f, r.bottom + w * 0.012f))
    }

    /** El cesto de mimbre con la leña de abedul apilada (las bocas de los leños de cara), cortado por el borde de la derecha. */
    private fun DrawScope.cesto(a: AireNapoleon, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val base = h - w * 0.012f
        val boca = base - w * 0.09f
        val x0 = w * 0.79f; val x1 = w * 1.08f
        val k = cerca(Offset(w * 0.85f, boca))
        contactoNapoleon(Offset((x0 + x1) / 2, base), x1 - x0 + w * 0.04f, w * 0.03f, a, 0.45f)
        // Un leño largo tumbado de través por detrás, con su corteza blanca y sus marcas.
        rotate(-12f, Offset(w * 0.9f, boca - w * 0.05f)) {
            val tr = Rect(w * 0.79f, boca - w * 0.075f, w * 1.06f, boca - w * 0.03f)
            drawRect(a.sombra(0.3f), tr.topLeft + Offset(1.6f * u, 2f * u), tr.size)
            drawRect(a.dia(P.ABEDUL, -0.05f, k), tr.topLeft, tr.size)
            clipRect(tr.left, tr.top, tr.right, tr.bottom) {
                drawRect(a.sombra(0.28f), Offset(tr.left, tr.bottom - tr.height * 0.4f), Size(tr.width, tr.height * 0.4f))
                drawRect(a.brillo(0.35f, k), Offset(tr.left, tr.top + tr.height * 0.15f), Size(tr.width, tr.height * 0.12f))
                for (j in 0 until 9) {
                    val x = tr.left + tr.width * azar(9530, j)
                    drawLine(a.dia(P.ABEDUL_MARCA, 0f, k).copy(alpha = 0.8f), Offset(x, tr.top + tr.height * azar(9531, j)), Offset(x + 0.6f * u, tr.top + tr.height * (0.3f + 0.6f * azar(9532, j))), 1.6f * u)
                }
            }
            val bo = Rect(tr.left - tr.height * 0.25f, tr.top, tr.left + tr.height * 0.25f, tr.bottom)
            ovaloNapoleon(a.dia(Color(0xFF8E8676), 0f, k), bo)
            ovaloNapoleon(a.dia(P.LENA, 0f, k), bo.deflate(1.4f * u))
        }
        // Los leños apilados con la boca de cara: la madera clara con sus anillos y la grieta, y la corteza alrededor.
        val bocas = listOf(Triple(0.83f, 0.0f, 0.026f), Triple(0.885f, -0.006f, 0.03f), Triple(0.945f, 0.002f, 0.027f), Triple(1.0f, -0.004f, 0.03f),
            Triple(0.855f, -0.045f, 0.025f), Triple(0.915f, -0.05f, 0.028f), Triple(0.975f, -0.044f, 0.026f))
        bocas.forEachIndexed { i, (fx, fy, fr) ->
            val c = Offset(w * fx, boca + w * fy)
            val r = w * fr
            val kc = cerca(c)
            drawCircle(a.sombra(0.4f), r * 1.02f, c + Offset(1.6f * u, 1.8f * u))
            drawCircle(a.dia(if (i % 3 == 1) Color(0xFF8E8676) else P.ABEDUL, -0.08f, kc), r, c)
            drawCircle(a.dia(P.LENA, 0f, kc), r * 0.84f, c)
            drawCircle(a.dia(Color(0xFFB88A55), 0f, kc), r * 0.58f, c, style = Stroke(0.7f * u))
            drawCircle(a.dia(Color(0xFFB88A55), 0f, kc), r * 0.32f, c, style = Stroke(0.6f * u))
            drawCircle(a.dia(Color(0xFF8A6036), 0f, kc), 1.1f * u, c + Offset(r * 0.06f, -r * 0.04f))
            val an = azar(9540, i) * 6.28f
            drawLine(a.dia(Color(0xFF6A4A2A), 0f, kc), c, c + Offset(r * 0.8f * kotlin.math.cos(an), r * 0.8f * kotlin.math.sin(an)), 0.9f * u)
            drawArc(a.brillo(0.4f, kc), 190f, 80f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1f * u))
        }
        // El cesto: el cuerpo de mimbre tejido, con su borde trenzado.
        val cuerpo = Path().apply {
            moveTo(x0, boca); lineTo(x1, boca); lineTo(x1, base); lineTo(x0 + w * 0.02f, base)
            quadraticTo(x0, base - w * 0.01f, x0, boca); close()
        }
        drawPath(cuerpo, a.dia(Color(0xFFB08A50), 0f, k))
        clipPath(cuerpo) {
            val tej = Path()
            var y = boca + w * 0.012f
            var f = 0
            while (y < base) {
                var x = x0 - (if (f % 2 == 0) 0f else w * 0.012f)
                while (x < x1) { tej.addOval(Rect(x, y - w * 0.006f, x + w * 0.022f, y + w * 0.006f)); x += w * 0.024f }
                y += w * 0.013f; f++
            }
            drawPath(tej, a.dia(Color(0xFF7A5A2E), 0f, k).copy(alpha = 0.7f), style = Stroke(0.8f * u))
            var x = x0 + w * 0.012f
            while (x < x1) { drawLine(a.dia(Color(0xFF6A4A24), 0f, k).copy(alpha = 0.6f), Offset(x, boca), Offset(x, base), 1.4f * u); x += w * 0.024f }
            cilindroNapoleon(Rect(x0, boca, x1, base), a, k, -1f, 1.1f)
        }
        val borde = Rect(x0 - w * 0.006f, boca - w * 0.008f, x1, boca + w * 0.01f)
        drawRoundRect(a.dia(Color(0xFF9A7440), 0f, k), borde.topLeft, borde.size, androidx.compose.ui.geometry.CornerRadius(w * 0.008f))
        var xb = borde.left
        while (xb < borde.right) { drawLine(a.dia(Color(0xFF6A4A24), 0f, k), Offset(xb, borde.top), Offset(xb + w * 0.01f, borde.bottom), 1f * u); xb += w * 0.012f }
        drawRect(a.brillo(0.35f, k), borde.topLeft, Size(borde.width, 1f * u))
        if (a.oscuro) drawLine(a.filo(0.6f * k), Offset(x0, boca), Offset(x0 + w * 0.01f, base), 1.4f * u)
    }

    /**
     * La silla de montar del oficial, de lado y asentada en el suelo, cortada por el borde de la izquierda: el borrén alto de
     * atrás y el arzón de delante con su clavo, el asiento hondo, el faldón con su costura y su rodillera, la cincha que cae al
     * suelo con su hebilla, la ación con el estribo y, debajo, la mantilla de paño azul con su galón.
     */
    private fun DrawScope.silla(a: AireNapoleon, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val piso = h - w * 0.014f
        fun x(f: Float) = w * (f + 0.035f)
        fun y(f: Float) = piso - w * f
        val k = cerca(Offset(x(0.2f), y(0.12f)))
        val kr = cerca(Offset(x(0.34f), y(0.08f)))
        val hacia = if (a.oscuro) 1f else -1f
        contactoNapoleon(Offset(x(0.17f), piso), w * 0.5f, w * 0.04f, a, 0.5f)
        // La mantilla de paño azul, bajo la silla, que cae hasta el suelo; su punta de atrás se esconde por el borde.
        val mant = Path().apply {
            moveTo(x(-0.05f), y(0.115f))
            lineTo(x(0.37f), y(0.12f))
            cubicTo(x(0.41f), y(0.11f), x(0.415f), y(0.04f), x(0.4f), y(0f))
            lineTo(x(-0.05f), y(0f))
            close()
        }
        drawPath(mant, a.dia(P.AZUL, 0f, k))
        clipPath(mant) {
            cilindroNapoleon(Rect(x(-0.05f), y(0.12f), x(0.42f), piso), a, kr, hacia, 0.5f)
            // Los pliegues del paño que se asienta en el suelo.
            listOf(0.05f, 0.16f, 0.29f).forEach { f ->
                drawLine(a.sombra(0.3f), Offset(x(f), y(0.09f)), Offset(x(f + 0.02f), piso), 2.4f * u)
                drawLine(a.claro(0.18f), Offset(x(f) + 2.4f * u, y(0.09f)), Offset(x(f + 0.02f) + 2.4f * u, piso), 1f * u)
            }
            drawRect(a.sombra(0.3f), Offset(x(-0.05f), y(0.03f)), Size(w * 0.5f, w * 0.03f))
        }
        // El galón dorado del borde de la mantilla, ancho, con su filete de dentro.
        val galon = Path().apply { moveTo(x(-0.05f), y(0.022f)); lineTo(x(0.36f), y(0.024f)); cubicTo(x(0.392f), y(0.03f), x(0.395f), y(0.08f), x(0.37f), y(0.11f)) }
        drawPath(galon, a.dia(P.GALON, 0f, kr), style = Stroke(w * 0.01f))
        drawPath(Path().apply { addPath(galon, Offset(-w * 0.006f, -w * 0.016f)) }, a.dia(P.GALON, -0.1f, kr), style = Stroke(w * 0.003f))
        drawPath(galon, a.brillo(0.45f, kr), style = Stroke(w * 0.002f))
        // La cincha de tela a rayas que cae del faldón hasta el suelo, con su hebilla.
        val cin = Rect(x(0.2f), y(0.09f), x(0.235f), piso)
        drawRect(a.dia(Color(0xFFC9B48A), 0f, k), cin.topLeft, cin.size)
        listOf(0.25f, 0.5f, 0.75f).forEach { f -> drawLine(a.dia(Color(0xFF8E7A58), 0f, k), Offset(cin.left + cin.width * f, cin.top), Offset(cin.left + cin.width * f, cin.bottom), 0.8f * u) }
        drawRect(a.sombra(0.3f), Offset(cin.right - cin.width * 0.3f, cin.top), Size(cin.width * 0.3f, cin.height))
        val heb = Rect(cin.left - w * 0.004f, y(0.05f), cin.right + w * 0.004f, y(0.03f))
        drawRect(a.dia(Color(0xFF5E5A56), 0f, k), heb.topLeft, heb.size, style = Stroke(1.6f * u))
        // El faldón: el ala de cuero que cuelga de la silla, con la rodillera abultada delante y la costura al borde.
        val faldon = Path().apply {
            moveTo(x(0.085f), y(0.16f))
            lineTo(x(0.29f), y(0.165f))
            cubicTo(x(0.31f), y(0.12f), x(0.3f), y(0.07f), x(0.275f), y(0.045f))
            lineTo(x(0.12f), y(0.04f))
            cubicTo(x(0.09f), y(0.05f), x(0.075f), y(0.12f), x(0.085f), y(0.16f))
            close()
        }
        drawPath(Path().apply { addPath(faldon, Offset(w * 0.006f, w * 0.006f)) }, a.sombra(0.4f))
        drawPath(faldon, a.dia(P.CUERO, -0.05f, k))
        clipPath(faldon) {
            cilindroNapoleon(Rect(x(0.07f), y(0.17f), x(0.31f), y(0.04f)), a, kr, hacia, 0.7f)
            // La rodillera: un rollo acolchado a lo alto, cerca del borde de delante.
            val rod = Path().apply { moveTo(x(0.255f), y(0.155f)); cubicTo(x(0.285f), y(0.12f), x(0.28f), y(0.08f), x(0.258f), y(0.055f)) }
            drawPath(rod, a.sombra(0.35f), style = Stroke(w * 0.016f))
            drawPath(rod, a.dia(P.CUERO, 0.08f, kr), style = Stroke(w * 0.011f))
            drawPath(Path().apply { addPath(rod, Offset(-w * 0.003f, 0f)) }, a.brillo(0.35f, kr), style = Stroke(w * 0.002f))
            drawPath(faldon, a.dia(P.CUERO_OSC, 0f, k).copy(alpha = 0.7f), style = Stroke(w * 0.01f))
        }
        val costura = Path().apply { moveTo(x(0.095f), y(0.15f)); cubicTo(x(0.085f), y(0.11f), x(0.1f), y(0.06f), x(0.125f), y(0.05f)); lineTo(x(0.268f), y(0.054f)) }
        drawPath(costura, a.dia(Color(0xFFD9C49A), 0f, k).copy(alpha = 0.65f), style = Stroke(0.7f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u))))
        // La silla: el borrén alto de atrás, el asiento hondo y el arzón de delante, en un solo cuerpo de cuero con su canto.
        val asiento = Path().apply {
            moveTo(x(-0.03f), y(0.14f))
            lineTo(x(-0.012f), y(0.24f))
            cubicTo(x(0.0f), y(0.272f), x(0.04f), y(0.27f), x(0.058f), y(0.232f))
            cubicTo(x(0.09f), y(0.175f), x(0.2f), y(0.16f), x(0.27f), y(0.185f))
            cubicTo(x(0.295f), y(0.195f), x(0.315f), y(0.215f), x(0.33f), y(0.205f))
            cubicTo(x(0.35f), y(0.19f), x(0.35f), y(0.16f), x(0.33f), y(0.145f))
            lineTo(x(0.3f), y(0.15f))
            cubicTo(x(0.2f), y(0.145f), x(0.1f), y(0.14f), x(-0.03f), y(0.12f))
            close()
        }
        drawPath(Path().apply { addPath(asiento, Offset(w * 0.008f, w * 0.008f)) }, a.sombra(0.4f))
        drawPath(asiento, a.dia(P.CUERO, 0.04f, k))
        clipPath(asiento) {
            // El cuero del asiento: la luz arriba, el canto hondo abajo.
            drawRect(a.dia(mezcla(P.CUERO, P.CUERO_OSC, 0.55f), 0f, k), Offset(x(-0.05f), y(0.165f)), Size(w * 0.45f, w * 0.05f))
            drawRect(a.dia(P.CUERO_OSC, 0f, k), Offset(x(-0.05f), y(0.145f)), Size(w * 0.45f, w * 0.03f))
            drawRect(a.brillo(0.3f, k), Offset(x(0.07f), y(0.2f)), Size(w * 0.18f, w * 0.009f))
        }
        drawPath(asiento, a.dia(P.CUERO_OSC, 0f, k), style = Stroke(1.2f * u))
        // El filo de luz del canto del asiento y del borrén.
        drawPath(Path().apply { moveTo(x(-0.012f), y(0.238f)); cubicTo(x(0.0f), y(0.268f), x(0.04f), y(0.266f), x(0.058f), y(0.229f)); cubicTo(x(0.09f), y(0.172f), x(0.2f), y(0.157f), x(0.27f), y(0.182f)) },
            a.brillo(0.5f, k), style = Stroke(1.1f * u))
        clavoNapoleon(Offset(x(0.325f), y(0.19f)), w * 0.006f, a, kr)
        clavoNapoleon(Offset(x(0.02f), y(0.245f)), w * 0.005f, a, k)
        // La ación y el estribo de hierro, que llega al suelo delante de la mantilla.
        val acion0 = Offset(x(0.165f), y(0.15f)); val est = Offset(x(0.31f), piso - w * 0.008f)
        drawLine(a.sombra(0.35f), acion0 + Offset(w * 0.006f, w * 0.004f), est + Offset(w * 0.006f, -w * 0.02f), w * 0.012f)
        drawLine(a.dia(P.CUERO_OSC, 0f, kr), acion0, est - Offset(0f, w * 0.022f), w * 0.012f, StrokeCap.Round)
        drawLine(a.brillo(0.3f, kr), acion0 - Offset(w * 0.003f, 0f), est - Offset(w * 0.003f, w * 0.022f), 0.8f * u)
        val hierro = a.dia(Color(0xFF5E5A56), 0f, kr)
        val estribo = Rect(est.x - w * 0.034f, est.y - w * 0.03f, est.x + w * 0.034f, est.y + w * 0.004f)
        drawArc(a.sombra(0.4f), 180f, 180f, false, estribo.topLeft + Offset(w * 0.004f, w * 0.004f), estribo.size, style = Stroke(w * 0.008f))
        drawArc(hierro, 180f, 180f, false, estribo.topLeft, estribo.size, style = Stroke(w * 0.007f))
        drawLine(hierro, Offset(estribo.left, estribo.center.y), Offset(estribo.right, estribo.center.y), w * 0.01f)
        drawArc(a.brillo(0.5f, kr), 200f, 80f, false, estribo.topLeft, estribo.size, style = Stroke(1f * u))
        if (a.oscuro) {
            drawPath(Path().apply { moveTo(x(0.3f), y(0.2f)); cubicTo(x(0.33f), y(0.215f), x(0.35f), y(0.19f), x(0.35f), y(0.165f)) }, a.filo(0.75f * kr), style = Stroke(1.6f * u))
            drawPath(Path().apply { moveTo(x(0.37f), y(0.12f)); cubicTo(x(0.41f), y(0.11f), x(0.415f), y(0.04f), x(0.4f), y(0f)) }, a.filo(0.6f * kr), style = Stroke(1.4f * u))
            drawPath(Path().apply { moveTo(x(0.29f), y(0.165f)); cubicTo(x(0.31f), y(0.12f), x(0.3f), y(0.07f), x(0.275f), y(0.045f)) }, a.filo(0.55f * kr), style = Stroke(1.2f * u))
        }
    }

    /**
     * El farol de hojalata calada: el cono del techo con su argolla, el cuerpo redondo con sus agujeros en hileras y arcos, la
     * puerta y el pie. De día, hojalata fría; de noche, la vela de dentro asoma por los agujeros y echa sus puntos de luz.
     */
    private fun DrawScope.farol(a: AireNapoleon, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val cx = w * MuebleEstanteNapoleon.FAROL_X
        val pie = h - w * 0.026f
        val cuerpo = Rect(cx - w * 0.045f, pie - w * 0.165f, cx + w * 0.045f, pie - w * 0.014f)
        // De noche arde; al ocaso de los Sueños, recién prendido (sus agujeros brillan poco y sin puntos en el suelo).
        val noche = a.oscuro
        val prendido = a.lumbre
        val f = if (!noche) 0.6f else if (a.noche) 0.7f else 1f
        val k = if (noche) 0.75f else cerca(cuerpo.center)
        // Los puntos de luz que echan los agujeros en el suelo y en el zócalo (de noche).
        if (noche) {
            mancha(Offset(cx, pie), w * 0.3f, P.LLAMA.copy(alpha = 0.2f * f))
            val puntos = Path()
            for (j in 0 until 40) {
                val an = azar(9600, j) * 6.283f
                val d = w * (0.06f + 0.26f * azar(9601, j))
                val p = Offset(cx + d * cos(an), pie + d * sin(an) * 0.28f - w * 0.01f)
                if (p.y < suelo + w * 0.005f) continue
                val rr = w * (0.003f + 0.004f * (1f - d / (w * 0.34f)))
                puntos.addOval(Rect(p.x - rr * 1.6f, p.y - rr * 0.6f, p.x + rr * 1.6f, p.y + rr * 0.6f))
            }
            drawPath(puntos, P.LLAMA.copy(alpha = 0.55f * f))
            val zoc = Path()
            for (j in 0 until 16) {
                val p = Offset(cx + w * (azar(9610, j) - 0.5f) * 0.5f, suelo - w * (0.01f + 0.06f * azar(9611, j)))
                zoc.addOval(Rect(p.x - w * 0.0035f, p.y - w * 0.0045f, p.x + w * 0.0035f, p.y + w * 0.0045f))
            }
            drawPath(zoc, P.LLAMA.copy(alpha = 0.45f * f))
        }
        contactoNapoleon(Offset(cx, pie), w * 0.11f, w * 0.014f, a, 0.5f)
        val lata = a.dia(P.HOJALATA, 0f, k)
        val lataOsc = a.dia(Color(0xFF5E6466), 0f, k)
        // El pie, un aro.
        drawRect(lataOsc, Offset(cuerpo.left - w * 0.004f, pie - w * 0.014f), Size(cuerpo.width + w * 0.008f, w * 0.014f))
        // El cuerpo, por franjas, y los agujeros (de noche, encendidos).
        drawRect(lata, cuerpo.topLeft, cuerpo.size)
        cilindroNapoleon(cuerpo, a, k, -1f, 1.4f)
        val agujeros = Path()
        for (fila in 0 until 6) {
            val y = cuerpo.top + cuerpo.height * (0.12f + 0.15f * fila)
            val n = 7
            for (j in 0 until n) {
                val fx = (j + 0.5f) / n
                val x = cuerpo.left + cuerpo.width * (0.5f - 0.5f * cos(fx * 3.1416f))
                val yy = y + (if (fila % 2 == 0) 0f else w * 0.004f * sin(fx * 3.1416f * 2f))
                val rr = w * 0.0034f * (0.5f + 0.5f * sin(fx * 3.1416f))
                agujeros.addOval(Rect(x - rr, yy - rr, x + rr, yy + rr))
            }
        }
        drawPath(agujeros, if (prendido) mezcla(P.FUEGO, P.LLAMA, 0.3f).copy(alpha = f) else a.dia(Color(0xFF2A2A2A), 0f, k))
        if (prendido) mancha(cuerpo.center, w * 0.05f, P.LLAMA.copy(alpha = 0.3f * f))
        // La puerta: su marco y la bisagra.
        drawRect(lataOsc, Offset(cuerpo.left + cuerpo.width * 0.2f, cuerpo.top + cuerpo.height * 0.05f), Size(cuerpo.width * 0.6f, cuerpo.height * 0.9f), style = Stroke(0.9f * u))
        drawRect(lataOsc, Offset(cuerpo.right - cuerpo.width * 0.24f, cuerpo.center.y - w * 0.004f), Size(w * 0.006f, w * 0.008f))
        // El techo cónico con su chimenea calada y la argolla.
        val techo = poligonoNapoleon(cuerpo.left - w * 0.006f, cuerpo.top, cuerpo.right + w * 0.006f, cuerpo.top, cx + w * 0.008f, cuerpo.top - w * 0.062f, cx - w * 0.008f, cuerpo.top - w * 0.062f)
        drawPath(techo, lata)
        clipPath(techo) { cilindroNapoleon(Rect(cuerpo.left - w * 0.006f, cuerpo.top - w * 0.062f, cuerpo.right + w * 0.006f, cuerpo.top), a, k, -1f, 1.3f) }
        for (j in 0 until 4) {
            val y = cuerpo.top - w * (0.01f + 0.01f * j)
            drawCircle(if (noche) P.FUEGO.copy(alpha = 0.8f * f) else a.dia(Color(0xFF2A2A2A), 0f, k), w * 0.002f, Offset(cx - w * 0.008f + w * 0.0055f * (j % 3), y))
        }
        drawRect(lataOsc, Offset(cuerpo.left - w * 0.006f, cuerpo.top - w * 0.003f), Size(cuerpo.width + w * 0.012f, w * 0.004f))
        val arg = Rect(cx - w * 0.02f, cuerpo.top - w * 0.105f, cx + w * 0.02f, cuerpo.top - w * 0.056f)
        drawArc(lataOsc, 180f, 180f, false, arg.topLeft, arg.size, style = Stroke(w * 0.004f))
        drawArc(a.brillo(0.4f, k), 200f, 70f, false, arg.topLeft, arg.size, style = Stroke(0.8f * u))
    }
}
