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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El pie del estante de Abismo ([MuebleEstanteAbismo]): la cubierta calafateada del Ideario (tablas que corren de lado a lado, más
 * anchas cuanto más cerca, el calafate negro, las testas con sus tapones y la sal en las juntas) y, encima, el cofre de marinero
 * pintado de verde con sus asas de cabo, sus flejes y la manta de lana doblada encima, cortado por la izquierda; el anafe de barro con
 * las brasas y el cazo de café de cobre (de noche, la luz del pie); y la garrafa forrada de mimbre, cortada por la derecha.
 */
internal object PieEstanteAbismo {
    private val P = PaletaAbismo

    /** Dónde pisan las cosas del pie, en fracción de lo que va del suelo al borde de abajo. */
    private const val PISAN = 0.72f

    /** La cubierta, del borde de arriba del suelo [suelo] al de abajo [h]. */
    fun DrawScope.suelo(a: AireAbismo, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        if (h - suelo < 2f) return
        val base = mezcla(P.TECA, P.MAMPARO_OSC, 0.25f)
        val n = if (a.lumbre) 18 else 1
        var y = suelo
        var g = w * 0.024f
        var i = 0
        while (y < h) {
            val alto = minOf(g, h - y)
            // Cada tabla, a tramos de noche (la luz cambia a lo ancho).
            val f = if (i % 2 == 1) -0.06f else 0f
            if (n <= 1) drawRect(a.dia(base, f, 1f), Offset(0f, y), Size(w, alto))
            else {
                val paradas = Array(n + 1) { t -> (t / n.toFloat()) to a.dia(base, f, cerca(Offset(w * t / n, y + alto / 2))) }
                drawRect(Brush.horizontalGradient(*paradas, startX = 0f, endX = w), Offset(0f, y), Size(w, alto))
            }
            drawRect(a.dia(mezcla(P.TINTA, P.TECA_OSC, 0.3f), 0f, 0.4f), Offset(0f, y + g - 1.4f * u), Size(w, 1.4f * u))
            drawRect(a.brillo(0.3f, cerca(Offset(w * 0.5f, y))), Offset(0f, y), Size(w, 0.8f * u))
            // Las testas a tresbolillo, con sus dos tapones.
            listOf(0.1f + 0.8f * azar(8501, i), 0.1f + 0.8f * azar(8502, i)).forEach { fx ->
                val xt = w * fx
                drawRect(a.dia(mezcla(P.TINTA, P.TECA_OSC, 0.3f), 0f, 0.4f), Offset(xt, y), Size(1.2f * u, alto))
                listOf(xt - w * 0.014f, xt + w * 0.014f).forEach { x -> drawCircle(a.dia(P.TECA_OSC, 0f, cerca(Offset(x, y))), 1.1f * u, Offset(x, y + alto / 2)) }
            }
            y += g; g *= 1.17f; i++
        }
        val zona = Rect(0f, suelo, w, h)
        vetas(zona, a.dia(P.TECA_OSC, 0f, 0.5f).copy(alpha = 0.25f), 8503, densidad = 0.5f, grosor = 0.5f)
        pinceladas(zona, if (a.oscuro) P.NOCHE else P.TECA_LUZ, 8504, 18, u, if (a.oscuro) 0.06f else 0.1f)
        pinceladas(zona, if (a.oscuro) P.NOCHE else Color(0xFFF2EEE2), 8505, 8, u, if (a.oscuro) 0.04f else 0.07f)
        // La sombra del armario en la cubierta.
        drawRect(a.sombra(0.4f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(a.sombra(0.18f), Offset(0f, suelo + 2.5f * u), Size(w, 7f * u))
    }

    /** Lo de encima de la cubierta, de atrás adelante. */
    fun DrawScope.objetos(a: AireAbismo, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width
        val pie = suelo + (h - suelo) * PISAN
        garrafa(Offset(w * 0.95f, pie + w * 0.012f), a, cerca)
        cofre(Offset(w * 0.12f, pie + w * 0.02f), a, cerca)
        anafe(Offset(w * MuebleEstanteAbismo.LUMBRE_X, pie), b, a, cerca)
    }

    /**
     * El cofre de marinero pintado de verde de barco, con la tapa algo más ancha, los flejes de hierro, el cierre de latón, la asa
     * de cabo trenzado del costado y, encima, la manta de lana azul de a bordo doblada, con sus listas rojas y una punta que cae.
     */
    private fun DrawScope.cofre(pie: Offset, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val x0 = -w * 0.05f; val x1 = w * 0.29f
        val alto = w * 0.15f
        val y0 = pie.y - alto
        val dx = w * 0.03f; val dy = -w * 0.025f
        val k = cerca(Offset(w * 0.2f, y0))
        fun c(col: Color, f: Float = 0f, p: Offset = Offset(w * 0.2f, y0)) = a.dia(col, f, cerca(p))
        contactoAbismo(Offset((x0 + x1) / 2 + w * 0.03f, pie.y), (x1 - x0) * 1.15f, w * 0.03f, a, 0.45f)
        // El costado en escorzo y la tapa.
        drawPath(poligonoAbismo(x1, y0, x1 + dx, y0 + dy, x1 + dx, pie.y + dy, x1, pie.y), c(P.VERDE_COFRE, -0.25f, Offset(x1, y0)))
        val tapaArriba = poligonoAbismo(x0 - w * 0.008f, y0, x1 + w * 0.008f, y0, x1 + dx + w * 0.008f, y0 + dy, x0 + dx - w * 0.008f, y0 + dy)
        drawPath(tapaArriba, c(P.VERDE_COFRE_LUZ, 0.06f))
        // El frente, con la pintura gastada donde asoma la madera.
        drawRect(c(P.VERDE_COFRE), Offset(x0, y0), Size(x1 - x0, alto))
        drawRect(c(P.VERDE_COFRE_LUZ), Offset(x0 - w * 0.008f, y0), Size(x1 - x0 + w * 0.016f, alto * 0.2f))
        drawRect(a.sombra(0.35f), Offset(x0, y0 + alto * 0.2f), Size(x1 - x0, 1.6f * u))
        pinceladas(Rect(x0, y0, x1, pie.y), if (a.oscuro) P.NOCHE else P.VERDE_COFRE_LUZ, 8510, 14, u, 0.15f)
        for (j in 0 until 5) {
            val p = Offset(w * (0.02f + 0.24f * azar(8511, j)), y0 + alto * (0.3f + 0.6f * azar(8512, j)))
            drawOval(c(P.TECA, 0f, p), p, Size(w * (0.012f + 0.02f * azar(8513, j)), w * 0.006f))
        }
        // Los flejes de hierro y sus clavos.
        listOf(0.06f, 0.24f).forEach { fx ->
            val x = w * fx
            drawRect(c(mezcla(P.HIERRO, P.OXIDO, 0.3f), 0f, Offset(x, y0)), Offset(x, y0 - w * 0.002f), Size(w * 0.014f, alto + w * 0.002f))
            drawRect(a.brillo(0.3f, k), Offset(x, y0), Size(1f * u, alto))
            for (j in 0 until 4) drawCircle(c(P.HIERRO_LUZ), 1f * u, Offset(x + w * 0.007f, y0 + alto * (0.1f + 0.27f * j)))
        }
        // El cierre de latón.
        val ci = Rect(w * 0.14f, y0 + alto * 0.12f, w * 0.17f, y0 + alto * 0.42f)
        drawRect(a.sombra(0.3f), Offset(ci.left + 1.5f * u, ci.top + 1.5f * u), ci.size)
        drawRect(c(P.LATON, 0f, ci.center), ci.topLeft, ci.size)
        drawRect(a.brillo(0.5f, k), ci.topLeft, Size(ci.width, 1f * u))
        drawRect(c(P.LATON_OSC, 0f, ci.center), Offset(ci.center.x - w * 0.003f, ci.top + ci.height * 0.55f), Size(w * 0.006f, ci.height * 0.3f))
        // La asa de cabo en el costado (la beca trenzada).
        val as0 = Offset(x1 + dx * 0.5f, y0 + alto * 0.35f + dy * 0.5f)
        drawArc(c(P.MANILA, 0f, as0), 260f, 200f, false, Offset(as0.x - w * 0.012f, as0.y - w * 0.015f), Size(w * 0.03f, w * 0.05f), style = Stroke(2.4f * u))
        drawArc(c(P.MANILA_OSC, 0f, as0), 260f, 200f, false, Offset(as0.x - w * 0.012f, as0.y - w * 0.015f), Size(w * 0.03f, w * 0.05f), style = Stroke(0.8f * u))
        if (a.lumbre) drawRect(a.filo(0.7f * k), Offset(x1 + dx - 1.5f * u, y0 + dy), Size(1.5f * u, alto))
        // La manta de lana azul de a bordo, doblada en tres sobre la tapa, con sus dos listas rojas y una punta que cae por delante.
        val ks = cerca(Offset(w * 0.14f, y0))
        val top = y0 + dy * 0.45f
        val lana = P.LANA
        val capas = listOf(Triple(0.004f, 0.25f, 0f), Triple(0.012f, 0.244f, 0.019f), Triple(0.02f, 0.236f, 0.037f))
        capas.forEachIndexed { j, (x0f, x1f, sube) ->
            val ga = w * 0.02f
            val yb = top - w * sube
            val capa = Rect(w * x0f, yb - ga, w * x1f, yb + w * 0.003f)
            drawRoundRect(a.sombra(0.4f), Offset(capa.left + w * 0.006f, capa.top + w * 0.004f), capa.size, CornerRadius(ga * 0.45f))
            drawRoundRect(a.dia(lana, if (j == 2) 0.06f else 0f, ks), capa.topLeft, capa.size, CornerRadius(ga * 0.45f))
            // El doblez de delante: su luz arriba y su hondo abajo; las listas rojas, alineadas; el pelo de la lana.
            drawRect(a.dia(lana, -0.35f, ks), Offset(capa.left + ga * 0.3f, capa.bottom - ga * 0.34f), Size(capa.width - ga * 0.6f, ga * 0.34f))
            drawRect(a.brillo(0.25f, ks), Offset(capa.left + ga * 0.4f, capa.top + ga * 0.18f), Size(capa.width - ga * 0.8f, 1.2f * u))
            listOf(0.14f, 0.175f).forEach { fx -> drawRect(a.dia(P.ROJO_TRAPO, 0f, ks), Offset(w * fx, capa.top + 0.5f * u), Size(w * 0.012f, capa.height - 1f * u)) }
            drawRect(a.sombra(0.25f), Offset(w * 0.14f, capa.bottom - ga * 0.34f), Size(w * 0.047f, ga * 0.34f))
        }
        // La punta que cae por delante del cofre, con su fleco.
        val punta = poligonoAbismo(w * 0.035f, top, w * 0.105f, top, w * 0.09f, top + w * 0.05f, w * 0.06f, top + w * 0.062f, w * 0.042f, top + w * 0.035f)
        drawPath(corridoAbismo(punta, w * 0.007f, w * 0.004f), a.sombra(0.35f))
        drawPath(punta, a.dia(lana, -0.1f, ks))
        clipPath(punta) { drawRect(a.dia(lana, -0.35f, ks), Offset(w * 0.075f, top), Size(w * 0.04f, w * 0.07f)) }
        for (j in 0 until 5) drawLine(a.dia(lana, 0.05f, ks), Offset(w * (0.063f + 0.005f * j), top + w * (0.059f - 0.004f * j)), Offset(w * (0.063f + 0.005f * j), top + w * (0.068f - 0.004f * j)), 0.8f * u)
    }

    /**
     * El anafe de barro (el hornillo de los esponjeros): el cuerpo con su pie, la boca del tiro ancha por delante con las brasas, el
     * lecho de brasas que se ve por arriba entre los tres tetones, el cazo de café de cobre encima con su mango largo, las tenazas
     * apoyadas y unos tizones en la cubierta. De noche, las brasas son la luz del pie.
     */
    private fun DrawScope.anafe(pie: Offset, b: Rect, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = if (a.oscuro) cerca(Offset(pie.x, pie.y - w * 0.08f)) * 0.5f else cerca(Offset(pie.x, pie.y - w * 0.08f))
        fun c(col: Color, f: Float = 0f) = a.dia(col, f, k)
        val an = w * 0.2f; val alto = w * 0.15f
        val y0 = pie.y - alto
        val brasa = if (a.lumbre) P.BRASA else mezcla(P.BRASA, Color(0xFF6A2E1A), 0.45f)
        // De noche, el charco de las brasas en la cubierta y en el zócalo (por debajo de todo lo del anafe).
        if (a.lumbre) {
            val f = if (a.oscuro) (if (a.noche) 0.55f else 1f) else 0.5f
            mancha(Offset(pie.x, pie.y - alto * 0.3f), w * 0.45f, P.BRASA.copy(alpha = 0.18f * f))
            mancha(Offset(pie.x, pie.y - alto * 0.2f), w * 0.2f, P.LLAMA.copy(alpha = 0.24f * f))
        }
        contactoAbismo(Offset(pie.x + w * 0.012f, pie.y), an * 1.35f, w * 0.026f, a, 0.48f)
        // Los tizones en la cubierta, delante a la izquierda.
        for (j in 0 until 4) {
            val p = Offset(pie.x - an * (0.55f + 0.18f * azar(8530, j)), pie.y - w * 0.004f - w * 0.006f * azar(8531, j))
            drawOval(Color(0xFF1E1410), Offset(p.x - w * 0.01f, p.y - w * 0.006f), Size(w * 0.02f, w * 0.011f))
            drawOval(c(P.HIERRO_LUZ).copy(alpha = 0.6f), Offset(p.x - w * 0.006f, p.y - w * 0.006f), Size(w * 0.008f, w * 0.003f))
        }
        // El cuerpo: el pie, la panza recta y el borde ancho.
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.44f, y0 + alto * 0.1f)
            lineTo(pie.x - an * 0.4f, pie.y - alto * 0.16f)
            lineTo(pie.x - an * 0.47f, pie.y)
            lineTo(pie.x + an * 0.47f, pie.y)
            lineTo(pie.x + an * 0.4f, pie.y - alto * 0.16f)
            lineTo(pie.x + an * 0.44f, y0 + alto * 0.1f)
            close()
        }
        drawPath(cuerpo, c(P.BARRO))
        clipPath(cuerpo) {
            drawRect(a.brillo(0.22f, k), Offset(pie.x - an * 0.42f, y0), Size(an * 0.14f, alto))
            if (!a.oscuro) drawRect(a.sombra(0.3f), Offset(pie.x + an * 0.16f, y0), Size(an * 0.4f, alto))
            drawRect(c(P.BARRO_OSC).copy(alpha = 0.6f), Offset(pie.x - an * 0.6f, pie.y - alto * 0.17f), Size(an * 1.2f, alto * 0.03f))
            drawOval(c(P.TINTA).copy(alpha = 0.3f), Offset(pie.x - an * 0.3f, y0 + alto * 0.1f), Size(an * 0.6f, alto * 0.22f))
        }
        // La boca del tiro: ancha, en arco, con su labio de barro y las brasas dentro.
        val boca = Path().apply {
            moveTo(pie.x - an * 0.28f, pie.y - alto * 0.2f)
            lineTo(pie.x - an * 0.28f, pie.y - alto * 0.5f)
            quadraticTo(pie.x, pie.y - alto * 0.72f, pie.x + an * 0.28f, pie.y - alto * 0.5f)
            lineTo(pie.x + an * 0.28f, pie.y - alto * 0.2f)
            close()
        }
        drawPath(boca, Color(0xFF1C0E08))
        clipPath(boca) {
            if (a.lumbre) mancha(Offset(pie.x, pie.y - alto * 0.3f), an * 0.35f, P.LLAMA.copy(alpha = 0.55f))
            for (j in 0 until 9) {
                val p = Offset(pie.x - an * 0.22f + an * 0.44f * azar(8521, j), pie.y - alto * (0.22f + 0.16f * azar(8522, j)))
                drawCircle(Color(0xFF2A1810), w * 0.013f, p)
                drawCircle(brasa.copy(alpha = 0.75f + 0.25f * azar(8523, j)), w * (0.005f + 0.004f * azar(8524, j)), p - Offset(w * 0.002f, w * 0.002f))
            }
            // La rejilla de hierro que sujeta las brasas.
            for (j in 0 until 5) drawLine(c(P.HIERRO), Offset(pie.x - an * 0.28f + an * 0.14f * j, pie.y - alto * 0.38f), Offset(pie.x - an * 0.28f + an * 0.14f * j, pie.y - alto * 0.2f), 1.2f * u)
        }
        drawPath(boca, c(P.BARRO, 0.12f), style = Stroke(2.4f * u))
        drawPath(boca, c(P.BARRO_OSC), style = Stroke(0.8f * u))
        // El borde ancho de arriba y el lecho de brasas que se ve entre los tetones.
        drawOval(c(P.BARRO, 0.12f), Offset(pie.x - an * 0.5f, y0), Size(an, alto * 0.2f))
        drawOval(Color(0xFF241208), Offset(pie.x - an * 0.42f, y0 + alto * 0.03f), Size(an * 0.84f, alto * 0.13f))
        clipPath(Path().apply { addOval(Rect(pie.x - an * 0.42f, y0 + alto * 0.03f, pie.x + an * 0.42f, y0 + alto * 0.16f)) }) {
            for (j in 0 until 10) {
                val p = Offset(pie.x - an * 0.38f + an * 0.76f * azar(8525, j), y0 + alto * (0.05f + 0.1f * azar(8526, j)))
                drawCircle(Color(0xFF2E1A10), w * 0.01f, p)
                drawCircle(brasa.copy(alpha = if (a.lumbre) 0.95f else 0.6f), w * 0.0045f, p - Offset(w * 0.002f, w * 0.0015f))
            }
        }
        // Los tres tetones del borde.
        listOf(-0.36f, 0f, 0.36f).forEach { f ->
            val p = Offset(pie.x + an * f, y0 + alto * (if (f == 0f) 0.04f else 0.09f))
            drawRoundRect(c(P.BARRO, 0.05f), Offset(p.x - w * 0.01f, p.y - w * 0.014f), Size(w * 0.02f, w * 0.018f), CornerRadius(w * 0.005f))
            drawRect(a.brillo(0.3f, k), Offset(p.x - w * 0.01f, p.y - w * 0.014f), Size(w * 0.02f, 1f * u))
        }
        // El cazo de café de cobre: la panza, la cintura, la boca abierta con su pico, y el mango largo de latón con su puño.
        val cz = Offset(pie.x - an * 0.04f, y0 + alto * 0.02f)
        val cw = w * 0.07f; val chh = w * 0.07f
        val cazo = Path().apply {
            moveTo(cz.x - cw * 0.5f, cz.y)
            lineTo(cz.x + cw * 0.5f, cz.y)
            cubicTo(cz.x + cw * 0.55f, cz.y - chh * 0.3f, cz.x + cw * 0.3f, cz.y - chh * 0.55f, cz.x + cw * 0.32f, cz.y - chh * 0.72f)
            lineTo(cz.x + cw * 0.5f, cz.y - chh)
            lineTo(cz.x - cw * 0.44f, cz.y - chh)
            lineTo(cz.x - cw * 0.32f, cz.y - chh * 0.72f)
            cubicTo(cz.x - cw * 0.3f, cz.y - chh * 0.55f, cz.x - cw * 0.55f, cz.y - chh * 0.3f, cz.x - cw * 0.5f, cz.y)
            close()
        }
        drawPath(corridoAbismo(cazo, w * 0.006f, w * 0.004f), a.sombra(0.3f))
        drawPath(cazo, c(P.COBRE))
        clipPath(cazo) {
            cilindroAbismo(Rect(cz.x - cw * 0.55f, cz.y - chh, cz.x + cw * 0.55f, cz.y), a, k, -1f, 1.4f)
            drawRect(c(P.COBRE_OSC), Offset(cz.x - cw * 0.6f, cz.y - chh * 0.74f), Size(cw * 1.2f, w * 0.004f))
            if (a.lumbre) drawRect(a.filo(0.9f), Offset(cz.x - cw * 0.6f, cz.y - w * 0.006f), Size(cw * 1.2f, w * 0.006f))
        }
        drawOval(c(P.COBRE_OSC, -0.1f), Offset(cz.x - cw * 0.44f, cz.y - chh - w * 0.004f), Size(cw * 0.94f, w * 0.009f))
        val mango0 = Offset(cz.x + cw * 0.42f, cz.y - chh * 0.62f)
        val mango1 = Offset(cz.x + cw * 2.3f, cz.y - chh * 1.15f)
        drawLine(a.sombra(0.3f), mango0 + Offset(1.5f * u, 2f * u), mango1 + Offset(1.5f * u, 2f * u), 2.6f * u, StrokeCap.Round)
        drawLine(c(P.LATON), mango0, mango1, 2.4f * u, StrokeCap.Round)
        drawLine(c(P.TECA_OSC), Offset(mango0.x + (mango1.x - mango0.x) * 0.55f, mango0.y + (mango1.y - mango0.y) * 0.55f), mango1, 4f * u, StrokeCap.Round)
        destelloAbismo(Offset(cz.x - cw * 0.25f, cz.y - chh * 0.4f), 1.4f * u, a, k)
        // Las tenazas de hierro apoyadas en el costado derecho.
        val t0 = Offset(pie.x + an * 0.56f, pie.y - w * 0.002f)
        val t1 = Offset(pie.x + an * 0.38f, y0 - alto * 0.1f)
        drawLine(a.sombra(0.3f), t0 + Offset(2f * u, 1f * u), t1 + Offset(2f * u, 1f * u), 2f * u)
        drawLine(c(P.HIERRO), t0, t1, 1.8f * u, StrokeCap.Round)
        drawLine(c(P.HIERRO), Offset(t0.x + w * 0.008f, t0.y), Offset(t1.x + w * 0.004f, t1.y + w * 0.004f), 1.8f * u, StrokeCap.Round)
        drawCircle(c(P.HIERRO), w * 0.007f, t1, style = Stroke(1.6f * u))
        // De día, un hilo de humo del cazo.
        if (!a.oscuro) {
            val humo = Path().apply { moveTo(cz.x - cw * 0.1f, cz.y - chh); cubicTo(cz.x - cw * 0.6f, cz.y - chh * 1.7f, cz.x + cw * 0.4f, cz.y - chh * 2.1f, cz.x - cw * 0.2f, cz.y - chh * 2.8f) }
            drawPath(humo, luzMueble(0.25f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        }
        if (a.lumbre) drawPath(cuerpo, a.filo(0.4f), style = Stroke(1.2f * u))
    }

    /** La garrafa forrada de mimbre, cortada por la derecha: el cuerpo redondo trenzado, el cuello de vidrio verde y el corcho. */
    private fun DrawScope.garrafa(pie: Offset, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - w * 0.1f))
        fun c(col: Color, f: Float = 0f) = a.dia(col, f, k)
        val r = w * 0.115f
        val cc = Offset(pie.x, pie.y - r * 0.95f)
        contactoAbismo(Offset(pie.x + w * 0.01f, pie.y), r * 2.2f, w * 0.03f, a, 0.45f)
        // El cuello de vidrio y el corcho.
        val cuello = Rect(cc.x - r * 0.18f, cc.y - r * 1.45f, cc.x + r * 0.18f, cc.y - r * 0.8f)
        drawRect(c(Color(0xFF3E6E50)), cuello.topLeft, cuello.size)
        drawRect(c(Color(0xFF7AB090), 0.1f), Offset(cuello.left + cuello.width * 0.2f, cuello.top), Size(cuello.width * 0.15f, cuello.height))
        drawRect(c(P.CORCHO), Offset(cuello.left + cuello.width * 0.1f, cuello.top - r * 0.14f), Size(cuello.width * 0.8f, r * 0.16f))
        // El cuerpo de mimbre.
        val cuerpo = Path().apply { addOval(Rect(cc.x - r, cc.y - r * 0.95f, cc.x + r, cc.y + r * 0.95f)) }
        drawPath(cuerpo, c(P.MIMBRE))
        clipPath(cuerpo) {
            var y = cc.y - r
            var i = 0
            while (y < cc.y + r) {
                drawRect(c(mezcla(P.MIMBRE, P.TECA_OSC, 0.35f)), Offset(cc.x - r, y), Size(r * 2, 1.2f * u))
                var x = cc.x - r + (if (i % 2 == 0) 0f else r * 0.06f)
                while (x < cc.x + r) { drawRect(c(mezcla(P.MIMBRE, P.TECA_OSC, 0.25f)), Offset(x, y), Size(1f * u, r * 0.08f)); x += r * 0.12f }
                y += r * 0.08f; i++
            }
            drawCircle(a.sombra(0.35f), r * 1.05f, cc + Offset(r * 0.45f, r * 0.4f))
            drawCircle(a.brillo(0.2f, k), r * 0.5f, cc - Offset(r * 0.4f, r * 0.45f))
        }
        // El cuello trenzado del mimbre y el asa.
        drawRect(c(mezcla(P.MIMBRE, P.TECA_OSC, 0.2f)), Offset(cc.x - r * 0.3f, cc.y - r * 1.0f), Size(r * 0.6f, r * 0.12f))
        drawArc(c(P.MIMBRE), 190f, 160f, false, Offset(cc.x - r * 0.55f, cc.y - r * 1.25f), Size(r * 1.1f, r * 0.7f), style = Stroke(3f * u))
        if (a.lumbre) drawArc(a.filo(0.6f * k), 120f, 100f, false, Offset(cc.x - r, cc.y - r * 0.95f), Size(r * 2, r * 1.9f), style = Stroke(1.4f * u))
    }
}
