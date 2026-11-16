package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Ciudad neón
// =====================================================================

/** Un rectángulo de [w] por [h] con el borde de abajo en dientes de sierra parejos (el corte de un ticket). */
private fun dentado(w: Float, h: Float, dientes: Int, hondo: Float) = Path().apply {
    moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - hondo)
    for (k in dientes * 2 downTo 0) lineTo(w * k / (dientes * 2), if (k % 2 == 0) h - hondo else h)
    close()
}

/** Un trazo de neón: un halo ancho y tenue y el tubo fino y brillante. */
private fun DrawScope.neon(p: Path, color: Color, grosor: Float) {
    drawPath(p, color.copy(alpha = 0.22f), style = Stroke(grosor * 4, cap = StrokeCap.Round))
    drawPath(p, color.copy(alpha = 0.45f), style = Stroke(grosor * 2, cap = StrokeCap.Round))
    drawPath(p, Color.White.copy(alpha = 0.9f), style = Stroke(grosor * 0.6f, cap = StrokeCap.Round))
    drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round))
}

/** Un corazón con centro [c] y alto [s]. */
private fun corazon(c: Offset, s: Float) = Path().apply {
    moveTo(c.x, c.y + s * 0.36f)
    cubicTo(c.x - s * 0.55f, c.y, c.x - s * 0.32f, c.y - s * 0.48f, c.x, c.y - s * 0.16f)
    cubicTo(c.x + s * 0.32f, c.y - s * 0.48f, c.x + s * 0.55f, c.y, c.x, c.y + s * 0.36f)
    close()
}

/**
 * Ciudad neón: la pared del fondo de la cocina de un puesto de comida de los niveles bajos, vista desde dentro (sin calle ni
 * ciudad). Es siempre de noche. La pared es de paneles de acero atornillados, con juntas, remaches, abolladuras, chorreras de
 * grasa, el vaho pegado y calcomanías gastadas; una canaleta con bridas la cruza. Arriba a la izquierda, atornillado a la chapa,
 * el letrero de neón (el punto focal): un tazón magenta con los palillos y el vapor cian, con sus sujeciones, sus electrodos,
 * el cable al transformador, un vapor fundido y el pie que parpadea. A la derecha, el farolillo rojo de papel colgado, la
 * pizarra del menú con signos inventados, la pantallita de los pedidos con sus barras y un ventilador con una tira de papel
 * atada. En la repisa alta: la figura de la suerte con el brazo levantado, una pila de tazones, un vaso de lata con palillos,
 * la arrocera humeando (con su piloto) y tres botellas de salsa sin etiqueta, la última cortada por el borde. El mueble son
 * montantes ranurados atornillados a la pared y, en cada fila, un riel de comandas arriba y una repisa de acero con sus
 * escuadras; bajo el canto de cada repisa cuelga el letrerito de neón verde con el tema. Cada tema es, según la semilla, el
 * riel con los tickets térmicos colgando (más cuantas más fichas, curvados por el calor), un pincho de mostrador con los tickets
 * ensartados (y alguno pendiente en el riel) o una cajita de acero con rollos de papel térmico y una tira que sale. Lo que no
 * tiene tema es un cuenco con tickets arrugados y el letrero apagado; un hueco sin tema es el riel vacío con una pinza suelta o
 * una calcomanía medio arrancada. Al pie, el canto del mostrador con dos tazones servidos y palillos, y un taburete rojo
 * cortado por el borde. La luz: el letrero baña en magenta y cian lo cercano y cae con la distancia (las primeras filas,
 * encendidas; las de abajo, solo con su letrerito verde y algún reflejo); el farolillo tiñe de rojo la pizarra, la arrocera
 * deja su brillo ámbar, el vapor recoge los colores y los letreritos de la última fila alumbran el mostrador. Por dentro, la
 * plancha de acero con reflejos de neón y vapor. Las fichas son tickets dentados.
 */
object IdeasNeon : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del puesto
    /** Las luces: el neón del letrero, los letreritos verdes, el farolillo, el piloto de la arrocera. */
    private val MAGENTA = Color(0xFFFF3D9A)
    private val CIAN = Color(0xFF3DE0FF)
    private val SODIO = Color(0xFF7CFF6B)
    private val ROJO = Color(0xFFD8463E)
    private val AMBAR = Color(0xFFFFB238)
    /** La chapa de la pared, el acero de rieles y repisas, y el negro violeta de la noche del puesto. */
    private val CHAPA = Color(0xFF6E6A88)
    private val INOX = Color(0xFFA6ABC0)
    private val NOCHE = Color(0xFF110B1C)
    private val SOMBRA = Color(0xFF09050F)
    /** El esmalte verde azulado (la arrocera, la franja de los tazones), la loza, el papel térmico y la grasa. */
    private val ESMALTE = Color(0xFF4E8A94)
    private val LOZA = Color(0xFFE2D8C4)
    private val TICKET = Color(0xFFF2EEE6)
    private val GRASA = Color(0xFF5A3A22)
    private val BAMBU = Color(0xFFC8A46A)
    private val MOSTAZA = Color(0xFFE6C63A)
    /** El filo que deja el letrero en lo que lo mira, y el del farolillo. */
    private val FILO = Color(0xFFE7A8F5)
    private val FILO_ROJO = Color(0xFFFF9A7A)
    /** El acero y el texto del interior y de las fichas (sin cambios). */
    private val ACERO = Color(0xFF2A2630)
    private val ACERO_LUZ = Color(0xFF4A4452)
    private val ACERO_OSC = Color(0xFF1A1620)
    private val TEXTO = Color(0xFFF4E7F0)
    /** El letrerito de cada tema: la placa negra y la letra verde clara (sobre la placa, más de 15 a 1). */
    private val PLACA = Color(0xFF07050B)
    private val LETRA = Color(0xFFDFFFD6)

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 1.25f
    override val corona = 0.7f
    override val base = 0.34f
    override val lado = 0.07f
    override val hueco = 0.05f

    /** En cada fila, en fracción de su alto: el riel de comandas, el fondo de la repisa, su canto y lo bajo del labio. */
    private const val RIEL = 0.07f
    private const val REPISA = 0.585f
    private const val CANTO = 0.62f
    private const val LABIO = 0.655f
    /** Donde pisan las cosas de la repisa alta (fracción del ancho). */
    private const val ALTA = 0.6f

    // ---------------------------------------------------------------- la luz

    /** El centro del letrero, el farolillo y la arrocera, en fracciones del ancho. */
    private val LETRERO = Offset(0.27f, 0.19f)
    private val FAROL = Offset(0.6f, 0.12f)
    private val ARROCERA = Offset(0.6f, 0.5f)

    /** Cuánto le llega del letrero a un punto (fracciones del ancho): cae despacio con la distancia, sin llegar a cero. */
    private fun luzLetrero(p: Offset): Float {
        val d = (p - LETRERO).getDistance() / 0.6f
        return 1f / (1f + d * d)
    }
    /** Y del farolillo: un charco corto. */
    private fun luzFarol(p: Offset): Float {
        val d = (p - FAROL).getDistance() / 0.13f
        return (1f / (1f + d * d)).coerceAtMost(1f)
    }

    /** Lo que alumbra un punto: el letrero [k], el farolillo [kf] y un letrerito verde [kv]. */
    private class Tono(val k: Float, val kf: Float = 0f, val kv: Float = 0f)

    /** El color propio de algo, hundido en la noche del puesto: oscuro pero de su tono. */
    private fun hondo(c: Color) = Color(c.red * 0.1f + NOCHE.red * 0.85f, c.green * 0.1f + NOCHE.green * 0.85f, c.blue * 0.1f + NOCHE.blue * 0.85f, c.alpha)

    /** Un color según la luz que le llega: de su hondo hacia el suyo, teñido de magenta, de rojo o de verde. */
    private fun Tono.c(col: Color): Color {
        val luz = (Math.pow((1.35f * k).toDouble().coerceIn(0.0, 1.0), 1.8).toFloat() + 0.9f * kf + 0.7f * kv).coerceIn(0f, 1f)
        var v = mezcla(hondo(col), col, luz)
        v = mezcla(v, MAGENTA, 0.2f * k)
        v = mezcla(v, ROJO, 0.32f * kf)
        v = mezcla(v, SODIO, 0.2f * kv)
        return v
    }

    /** La sombra: el negro violeta, nunca negro puro. */
    private fun Tono.s(a: Float) = SOMBRA.copy(alpha = a.coerceIn(0f, 1f))

    /** El filo de luz en lo que mira a la luz que más le llega; algo de filo siempre, para que nada sea una silueta plana. */
    private fun Tono.b(a: Float): Color {
        val col = when { kf > k -> FILO_ROJO; kv > k -> SODIO; else -> FILO }
        return col.copy(alpha = (a * (0.35f + 1.3f * maxOf(k, kf, kv))).coerceIn(0f, 1f))
    }

    private var anchoW = 1f
    private fun tn(p: Offset, kv: Float = 0f) = Tono(luzLetrero(p / anchoW), luzFarol(p / anchoW), kv)

    // ---------------------------------------------------------------- ayudantes

    private fun poli(vararg v: Float): Path = Path().apply {
        moveTo(v[0], v[1]); var i = 2
        while (i < v.size - 1) { lineTo(v[i], v[i + 1]); i += 2 }
        close()
    }

    /** Un tubo de neón encendido: el halo, el vidrio del color y el corazón casi blanco. [brillo] baja en el que parpadea. */
    private fun DrawScope.tubo(p: Path, color: Color, g: Float, brillo: Float = 1f) {
        drawPath(p, color.copy(alpha = 0.13f * brillo), style = Stroke(g * 5.5f, cap = StrokeCap.Round))
        drawPath(p, color.copy(alpha = 0.3f * brillo), style = Stroke(g * 2.6f, cap = StrokeCap.Round))
        drawPath(p, mezcla(NOCHE, color, 0.35f + 0.65f * brillo), style = Stroke(g, cap = StrokeCap.Round))
        drawPath(p, mezcla(color, Color.White, 0.65f).copy(alpha = 0.95f * brillo), style = Stroke(g * 0.35f, cap = StrokeCap.Round))
    }

    /** Un tubo fundido: el vidrio gris con su brillo, sin luz. */
    private fun DrawScope.tuboApagado(p: Path, g: Float, t: Tono) {
        drawPath(p, t.c(Color(0xFF5A5468)), style = Stroke(g, cap = StrokeCap.Round))
        drawPath(p, t.b(0.4f), style = Stroke(g * 0.3f, cap = StrokeCap.Round))
    }

    /** Un remache de cabeza redonda: la sombra abajo a la derecha, la cabeza y su brillo. */
    private fun DrawScope.remache(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.5f), r, p + Offset(r * 0.5f, r * 0.6f))
        drawCircle(t.c(INOX), r, p)
        drawCircle(t.b(0.7f), r * 0.4f, p - Offset(r * 0.35f, r * 0.35f))
    }

    /** El lado de luz (izquierda) y el de sombra (derecha) de un cuerpo de pie dentro de [r] (ir dentro de su recorte). */
    private fun DrawScope.cuerpo(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.22f * fuerza), r.topLeft, Size(r.width * 0.28f, r.height))
        drawRect(t.b(0.35f * fuerza), Offset(r.left + r.width * 0.12f, r.top), Size(r.width * 0.07f, r.height))
        drawRect(t.s(0.25f * fuerza), Offset(r.left + r.width * 0.6f, r.top), Size(r.width * 0.4f, r.height))
        drawRect(t.s(0.25f * fuerza), Offset(r.left + r.width * 0.82f, r.top), Size(r.width * 0.18f, r.height))
    }

    /** La sombra de contacto de lo que pisa una repisa, corrida a la derecha. */
    private fun DrawScope.contacto(c: Offset, ancho: Float, alto: Float, a: Float = 0.55f) =
        drawOval(SOMBRA.copy(alpha = a), Offset(c.x - ancho * 0.42f, c.y - alto / 2), Size(ancho, alto))

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        anchoW = w
        val b = d.base

        // ---- la pared de chapa
        pared(w, alto, d)

        // ---- en la pared, arriba: la canaleta, el transformador, el letrero, la pizarra, la pantallita, el ventilador, el farolillo
        canaleta(w, w * 0.362f)
        transformador(Rect(w * 0.165f, w * 0.375f, w * 0.255f, w * 0.445f), w)
        letrero(w)
        pizarra(Rect(w * 0.69f, w * 0.05f, w * 0.965f, w * 0.31f), w)
        pantallita(Rect(w * 0.7f, w * 0.355f, w * 0.815f, w * 0.44f))
        ventilador(Offset(w * 0.9f, w * 0.405f), w * 0.042f)

        // ---- los montantes ranurados del mueble
        val arriba = w * (ALTA - 0.03f)
        val abajo = b.top + w * 0.1f
        listOf(w * 0.022f to w * 0.032f, w * 0.484f to w * 0.032f, w * 0.946f to w * 0.032f).forEach { (x, a) -> montante(Rect(x, arriba, x + a, abajo)) }

        // ---- la repisa alta y lo que tiene encima, de atrás adelante
        repisa(w * ALTA - w * 0.02f, w * ALTA + w * 0.008f, w * ALTA + w * 0.03f, w, true)
        figura(Offset(w * 0.11f, w * ALTA), w * 0.13f)
        tazones(Offset(w * 0.3f, w * ALTA), w * 0.135f)
        vasoPalillos(Offset(w * 0.43f, w * ALTA), w)
        arrocera(Offset(w * 0.605f, w * ALTA), w)
        botella(Offset(w * 0.815f, w * ALTA), w * 0.045f, w * 0.13f, ROJO, 0)
        botella(Offset(w * 0.872f, w * ALTA), w * 0.043f, w * 0.115f, MOSTAZA, 1)
        botella(Offset(w * 0.975f, w * ALTA), w * 0.05f, w * 0.15f, GRASA, 2)

        // ---- cada fila: el riel de comandas arriba y la repisa con su canto
        d.filas.forEach { f ->
            val hh = f.height
            repisa(f.top + hh * REPISA, f.top + hh * CANTO, f.top + hh * LABIO, w, false)
            riel(f.top + hh * RIEL, w)
        }

        // ---- al pie: el zócalo, el mostrador con sus tazones y el taburete
        pie(d, w, alto)

        // ---- el farolillo, delante de todo lo de arriba, y las luces encima de lo que tienen cerca
        farolillo(Offset(w * FAROL.x, w * 0.035f), w)
        val l = LETRERO * w
        mancha(l, w * 0.55f, MAGENTA.copy(alpha = 0.16f))
        mancha(l + Offset(0f, -w * 0.08f), w * 0.2f, CIAN.copy(alpha = 0.14f))
        mancha(FAROL * w + Offset(0f, w * 0.03f), w * 0.17f, ROJO.copy(alpha = 0.3f))
        mancha(ARROCERA * w, w * 0.1f, AMBAR.copy(alpha = 0.16f))
        granoMueble(Rect(0f, 0f, w, alto), true)
    }

    /**
     * La pared de paneles de acero: la luz del letrero (un solo degradado radial, de su color hondo al encendido), las juntas
     * con sus remaches, el cepillado, las abolladuras, la grasa y el vaho, y las calcomanías.
     */
    private fun DrawScope.pared(w: Float, alto: Float, d: Disposicion) {
        val u = density
        val l = LETRERO * w
        val radio = alto.coerceAtLeast(w) * 1.05f
        val paradas = List(7) { i ->
            val f = i / 6f
            val p = LETRERO + Offset(0f, f * radio / w)
            f to Tono(luzLetrero(p)).c(CHAPA)
        }
        drawRect(Brush.radialGradient(*paradas.toTypedArray(), center = l, radius = radio), Offset.Zero, Size(w, alto))
        // El cepillado del acero: pinceladas largas y tenues.
        pinceladas(Rect(0f, 0f, w, w * 1.1f), FILO, 3301, 30, u, 0.035f)
        pinceladas(Rect(0f, 0f, w, alto), SOMBRA, 3302, 50, u, 0.12f)
        // Las juntas de los paneles: verticales irregulares, horizontales trabadas.
        val xs = listOf(0f, 0.205f, 0.515f, 0.78f, 1f)
        for (i in 0 until xs.size - 1) {
            val x0 = w * xs[i]; val x1 = w * xs[i + 1]
            if (i > 0) junta(Offset(x0, 0f), Offset(x0, alto), true)
            var y = w * (0.48f + 0.17f * azar(3310, i))
            while (y < alto) {
                junta(Offset(x0, y), Offset(x1, y), false)
                // Los remaches a lo largo de la junta.
                var x = x0 + w * 0.025f
                while (x < x1 - w * 0.015f) { remache(Offset(x, y - w * 0.012f), 1.5f * u, tn(Offset(x, y))); x += w * 0.06f }
                y += w * (0.52f + 0.2f * azar(3311, i + (y / w).toInt()))
            }
            var yy = w * 0.03f
            while (yy < alto) {
                remache(Offset(x0 + w * 0.013f, yy), 1.5f * u, tn(Offset(x0, yy)))
                yy += w * 0.075f
            }
        }
        // Abolladuras: la luz arriba a la izquierda del hueco, la sombra abajo a la derecha.
        for (k in 0 until 9) {
            val c = Offset(w * azar(3320, k), alto * azar(3321, k))
            val r = w * (0.02f + 0.025f * azar(3322, k))
            val t = tn(c)
            drawArc(t.s(0.22f), 200f, 140f, false, Offset(c.x - r, c.y - r * 0.6f), Size(r * 2, r * 1.2f), style = Stroke(1.2f * u))
            drawArc(t.b(0.2f), 20f, 140f, false, Offset(c.x - r, c.y - r * 0.6f), Size(r * 2, r * 1.2f), style = Stroke(1f * u))
        }
        // El vaho pegado arriba de la arrocera y la grasa que baja de la repisa alta.
        pinceladas(Rect(w * 0.45f, w * 0.12f, w * 0.78f, w * 0.5f), LOZA, 3330, 14, u, 0.05f)
        chorreras(w * (ALTA + 0.03f), 0f, w, 3340, 7)
        d.filas.forEach { f -> chorreras(f.top + f.height * LABIO, 0f, w, 3350 + (f.top / w * 10).toInt(), 5) }
        // Las calcomanías gastadas.
        calcomania(Offset(w * 0.55f, w * 0.06f), w * 0.05f, -8f, 0, w)
        calcomania(Offset(w * 0.06f, w * 0.43f), w * 0.045f, 12f, 1, w)
        calcomania(Offset(w * 0.47f, w * 0.43f), w * 0.04f, -4f, 2, w)
        d.filas.getOrNull(1)?.let { f -> calcomania(Offset(w * 0.5f, f.top + f.height * 0.36f), w * 0.034f, 6f, 3, w) }
        d.filas.getOrNull(3)?.let { f -> calcomania(Offset(w * 0.06f, f.top + f.height * 0.3f), w * 0.03f, -10f, 1, w) }
    }

    /** Una junta entre paneles: la ranura en sombra y el canto que mira a la luz. */
    private fun DrawScope.junta(a: Offset, b: Offset, vertical: Boolean) {
        val u = density
        val t = tn((a + b) / 2f)
        drawLine(t.s(0.6f), a, b, 1.6f * u)
        val o = if (vertical) Offset(1.6f * u, 0f) else Offset(0f, 1.6f * u)
        drawLine(t.b(0.35f), a + o, b + o, 0.8f * u)
    }

    /** Las chorreras de grasa bajo un canto en [y]: líneas pardas que bajan y acaban en gota. */
    private fun DrawScope.chorreras(y: Float, x0: Float, x1: Float, semilla: Int, cuantas: Int) {
        val u = density
        for (k in 0 until cuantas) {
            val x = x0 + (x1 - x0) * azar(semilla, k)
            val largo = anchoW * (0.02f + 0.07f * azar(semilla, k + 30))
            val t = tn(Offset(x, y))
            val col = t.c(GRASA).copy(alpha = 0.55f)
            drawLine(col, Offset(x, y), Offset(x, y + largo), (1.2f + 1.4f * azar(semilla, k + 60)) * u, StrokeCap.Round)
            drawCircle(col, (1.4f + 0.8f * azar(semilla, k + 90)) * u, Offset(x, y + largo))
            drawLine(t.b(0.25f), Offset(x - 0.5f * u, y), Offset(x - 0.5f * u, y + largo * 0.8f), 0.5f * u)
        }
    }

    /**
     * Una calcomanía gastada y medio despegada: un rectángulo de color de la paleta con un signo inventado (círculo, flecha o
     * rayo), las esquinas comidas y una dobladura con el reverso blanco.
     */
    private fun DrawScope.calcomania(c: Offset, s: Float, giro: Float, tipo: Int, w: Float) {
        val u = density
        val t = tn(c)
        val fondo = listOf(CIAN, MAGENTA, MOSTAZA, ESMALTE)[tipo % 4]
        rotate(giro, c) {
            val r = Rect(c.x - s, c.y - s * 0.65f, c.x + s, c.y + s * 0.65f)
            val forma = poli(r.left, r.top, r.right - s * 0.35f, r.top, r.right, r.top + s * 0.3f, r.right, r.bottom, r.left + s * 0.15f, r.bottom, r.left, r.bottom - s * 0.2f)
            drawPath(forma, t.c(mezcla(fondo, CHAPA, 0.45f)))
            drawPath(forma, t.c(LOZA).copy(alpha = 0.18f), style = Stroke(1.6f * u))
            val tinta = t.c(NOCHE)
            when (tipo % 4) {
                0 -> { drawCircle(tinta, s * 0.35f, r.center, style = Stroke(1.4f * u)); drawLine(tinta, r.center - Offset(s * 0.25f, 0f), r.center + Offset(s * 0.25f, 0f), 1.4f * u) }
                1 -> drawPath(poli(r.left + s * 0.4f, r.center.y - s * 0.3f, r.left + s * 1.3f, r.center.y, r.left + s * 0.4f, r.center.y + s * 0.3f), tinta)
                2 -> drawPath(poli(c.x - s * 0.1f, r.top + s * 0.15f, c.x - s * 0.35f, c.y + s * 0.05f, c.x, c.y + s * 0.05f, c.x - s * 0.2f, r.bottom - s * 0.12f, c.x + s * 0.35f, c.y - s * 0.12f, c.x, c.y - s * 0.12f), tinta)
                else -> for (j in 0 until 3) drawLine(tinta, Offset(r.left + s * 0.3f, r.top + s * (0.35f + 0.3f * j)), Offset(r.right - s * (0.3f + 0.3f * j), r.top + s * (0.35f + 0.3f * j)), 1.6f * u)
            }
            // Lo gastado: rayones claros y la esquina doblada con el reverso.
            for (j in 0 until 3) drawLine(t.c(LOZA).copy(alpha = 0.22f), Offset(r.left + s * 2f * azar(tipo + 70, j), r.top), Offset(r.left + s * 2f * azar(tipo + 71, j), r.bottom), 0.6f * u)
            drawPath(poli(r.right - s * 0.35f, r.top, r.right, r.top + s * 0.3f, r.right - s * 0.42f, r.top + s * 0.38f), t.c(TICKET))
            drawPath(poli(r.right - s * 0.35f, r.top, r.right - s * 0.42f, r.top + s * 0.38f, r.right - s * 0.36f, r.top + s * 0.44f), t.s(0.3f))
        }
    }

    /** La canaleta de cables de lado a lado, con sus bridas y un cable que se sale y cuelga. */
    private fun DrawScope.canaleta(w: Float, y: Float) {
        val u = density
        val g = w * 0.016f
        for (k in 0 until 3) {
            val t = tn(Offset(w * 0.5f, y))
            val yy = y + k * g * 0.42f
            drawLine(t.s(0.45f), Offset(0f, yy + 2.4f * u), Offset(w, yy + 2.4f * u), g * 0.4f)
            drawLine(t.c(listOf(NOCHE, Color(0xFF3A2E4A), GRASA)[k]), Offset(0f, yy), Offset(w, yy), g * 0.4f)
        }
        // El tubo de acero, encima.
        val tt = tn(Offset(w * 0.3f, y))
        drawLine(tt.c(INOX), Offset(0f, y - g * 0.5f), Offset(w, y - g * 0.5f), g * 0.55f)
        drawLine(tt.b(0.6f), Offset(0f, y - g * 0.68f), Offset(w, y - g * 0.68f), 0.8f * u)
        // Las bridas: anillos negros con su cola.
        listOf(0.04f, 0.16f, 0.36f, 0.53f, 0.66f, 0.86f, 0.97f).forEachIndexed { i, fx ->
            val x = w * fx
            val t = tn(Offset(x, y))
            drawRect(t.c(Color(0xFF1E1826)), Offset(x - 1.6f * u, y - g * 0.85f), Size(3.2f * u, g * 1.9f))
            drawRect(t.b(0.5f), Offset(x - 1.6f * u, y - g * 0.85f), Size(0.8f * u, g * 1.9f))
            drawLine(t.c(Color(0xFF1E1826)), Offset(x + 1.6f * u, y + g * 0.9f), Offset(x + (3f + 2f * azar(3360, i)) * u, y + g * 1.6f), 1.2f * u)
        }
        // Un cable que se sale de la canaleta y cuelga en comba.
        val t = tn(Offset(w * 0.6f, y))
        val p = Path().apply { moveTo(w * 0.45f, y + g * 0.6f); cubicTo(w * 0.48f, y + w * 0.06f, w * 0.56f, y + w * 0.065f, w * 0.6f, y + g * 0.6f) }
        drawPath(p, t.s(0.5f), style = Stroke(1.6f * u))
        translate(0f, -2f * u) { drawPath(p, t.c(Color(0xFF2A2236)), style = Stroke(1.6f * u)); drawPath(p, t.b(0.3f), style = Stroke(0.5f * u)) }
    }

    /** El transformador del neón: la caja de chapa con su rejilla, sus tornillos y el cable que sube al letrero. */
    private fun DrawScope.transformador(r: Rect, w: Float) {
        val u = density
        val t = tn(r.center)
        // El cable de alta, en curva hasta el electrodo del tazón.
        val cab = Path().apply { moveTo(r.left + r.width * 0.3f, r.top); cubicTo(r.left, r.top - w * 0.05f, w * 0.11f, w * 0.27f, w * 0.135f, w * 0.205f) }
        drawPath(cab, t.s(0.5f), style = Stroke(2f * u))
        translate(-1.5f * u, -1.5f * u) { drawPath(cab, t.c(Color(0xFF2A2236)), style = Stroke(2f * u)) }
        drawRect(t.s(0.5f), r.topLeft + Offset(2.5f * u, 3f * u), r.size)
        drawRect(t.c(Color(0xFF4A4458)), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { cuerpo(r, t, 0.8f) }
        drawRect(t.b(0.6f), r.topLeft, Size(r.width, 1f * u))
        for (k in 0 until 4) {
            val y = r.top + r.height * (0.25f + 0.13f * k)
            drawRect(t.s(0.6f), Offset(r.left + r.width * 0.2f, y), Size(r.width * 0.6f, 1.4f * u))
            drawRect(t.b(0.3f), Offset(r.left + r.width * 0.2f, y + 1.4f * u), Size(r.width * 0.6f, 0.6f * u))
        }
        listOf(Offset(r.left + 2.5f * u, r.top + 2.5f * u), Offset(r.right - 2.5f * u, r.bottom - 2.5f * u)).forEach { remache(it, 1.2f * u, t) }
        // Un adhesivo de alta tensión amarillo, sin letras.
        drawPath(poli(r.center.x, r.bottom - r.height * 0.2f - r.width * 0.2f, r.center.x + r.width * 0.15f, r.bottom - r.height * 0.12f, r.center.x - r.width * 0.15f, r.bottom - r.height * 0.12f), t.c(MOSTAZA))
    }

    /**
     * El letrero: los tubos atornillados a la chapa con sus sujeciones. El tazón en magenta (su pie parpadea), los palillos y el
     * vapor en cian (el vapor del medio está fundido); los electrodos pintados de negro en los extremos.
     */
    private fun DrawScope.letrero(w: Float) {
        val u = density
        val cx = w * LETRERO.x; val cy = w * LETRERO.y
        val r = w * 0.145f
        val g = 2.6f * u
        val t = tn(Offset(cx, cy))
        // El reflejo en la chapa bajo los tubos: el halo grande ya lo pone la pared; aquí, la mancha más cercana.
        mancha(Offset(cx, cy + r * 0.3f), r * 1.5f, MAGENTA.copy(alpha = 0.3f))
        val tazon = Path().apply {
            moveTo(cx - r, cy); lineTo(cx + r, cy)
            cubicTo(cx + r, cy + 0.55f * r, cx + 0.55f * r, cy + 0.8f * r, cx, cy + 0.8f * r)
            cubicTo(cx - 0.55f * r, cy + 0.8f * r, cx - r, cy + 0.55f * r, cx - r, cy)
        }
        val piePath = Path().apply { moveTo(cx - 0.32f * r, cy + 0.95f * r); lineTo(cx + 0.32f * r, cy + 0.95f * r) }
        val palillos = listOf(
            Path().apply { moveTo(cx + 0.18f * r, cy + 0.22f * r); lineTo(cx + 1.2f * r, cy - 0.95f * r) },
            Path().apply { moveTo(cx + 0.38f * r, cy + 0.28f * r); lineTo(cx + 1.38f * r, cy - 0.85f * r) },
        )
        fun vapor(x: Float) = Path().apply {
            moveTo(x, cy - 0.12f * r)
            cubicTo(x - 0.22f * r, cy - 0.32f * r, x + 0.22f * r, cy - 0.5f * r, x, cy - 0.7f * r)
            cubicTo(x - 0.22f * r, cy - 0.88f * r, x + 0.12f * r, cy - 1.05f * r, x - 0.02f * r, cy - 1.15f * r)
        }
        // Las sombras de los tubos en la chapa (el tubo está separado de la pared).
        translate(2.6f * u, 3.4f * u) {
            listOf(tazon, piePath, vapor(cx - 0.42f * r), vapor(cx), vapor(cx + 0.4f * r)).plus(palillos).forEach { drawPath(it, t.s(0.45f), style = Stroke(g, cap = StrokeCap.Round)) }
        }
        tubo(vapor(cx - 0.42f * r), CIAN, g)
        tuboApagado(vapor(cx), g, t)
        tubo(vapor(cx + 0.4f * r), CIAN, g, 0.85f)
        tubo(tazon, MAGENTA, g * 1.25f)
        tubo(piePath, MAGENTA, g * 1.1f, 0.35f)
        palillos.forEach { tubo(it, CIAN, g) }
        // Los electrodos: los extremos pintados de negro, con su capuchón.
        listOf(Offset(cx - r, cy), Offset(cx - 0.42f * r, cy - 0.12f * r), Offset(cx, cy - 0.12f * r), Offset(cx + 0.4f * r, cy - 0.12f * r),
            Offset(cx + 1.2f * r, cy - 0.95f * r), Offset(cx + 1.38f * r, cy - 0.85f * r)).forEach { p ->
            drawCircle(t.c(Color(0xFF15101C)), g * 0.9f, p)
            drawCircle(t.b(0.5f), g * 0.35f, p - Offset(g * 0.3f, g * 0.3f))
        }
        // Las sujeciones: el poste a la pared y la pinza que abraza el tubo.
        listOf(Offset(cx - 0.6f * r, cy), Offset(cx + 0.6f * r, cy), Offset(cx - 0.62f * r, cy + 0.62f * r), Offset(cx + 0.62f * r, cy + 0.62f * r),
            Offset(cx + 0.85f * r, cy - 0.5f * r), Offset(cx - 0.42f * r, cy - 0.7f * r)).forEach { p ->
            drawCircle(t.s(0.5f), 2.4f * u, p + Offset(2f * u, 2.6f * u))
            drawRect(t.c(INOX), Offset(p.x - 0.8f * u, p.y - 3f * u), Size(1.6f * u, 6f * u))
            drawRect(t.b(0.8f), Offset(p.x - 0.8f * u, p.y - 3f * u), Size(0.6f * u, 6f * u))
        }
    }

    /** El farolillo rojo de papel colgado de su cordel: las varillas, los aros negros, la borla y su luz por dentro. */
    private fun DrawScope.farolillo(arriba: Offset, w: Float) {
        val u = density
        val a = w * 0.088f; val al = w * 0.115f
        val c = Offset(arriba.x, arriba.y + w * 0.03f + al / 2)
        drawLine(Color(0xFF2A1A20), Offset(arriba.x, 0f), Offset(arriba.x, c.y - al / 2), 1.2f * u)
        val cuerpo = Rect(c.x - a / 2, c.y - al / 2, c.x + a / 2, c.y + al / 2)
        // La sombra en la pared, corrida a la derecha (el letrero alumbra desde la izquierda).
        drawOval(SOMBRA.copy(alpha = 0.35f), cuerpo.topLeft + Offset(w * 0.02f, w * 0.012f), cuerpo.size)
        val forma = Path().apply { addOval(cuerpo) }
        drawPath(forma, mezcla(ROJO, Color(0xFF6A1420), 0.25f))
        clipPath(forma) {
            mancha(c + Offset(-a * 0.08f, 0f), a * 0.55f, Color(0xFFFFB07A).copy(alpha = 0.85f))
            // Las varillas que se ven a través del papel.
            for (j in -2..2) {
                val ex = a * 0.2f * j
                drawOval(Color(0xFF6A1420).copy(alpha = 0.45f), Offset(c.x + ex - a * 0.06f * (3 - kotlin.math.abs(j)), cuerpo.top), Size(a * 0.12f * (3 - kotlin.math.abs(j)) + 0.1f, al), style = Stroke(0.7f * u))
            }
            drawRect(Color(0xFF4A0E18).copy(alpha = 0.45f), Offset(cuerpo.right - a * 0.22f, cuerpo.top), Size(a * 0.22f, al))
            drawRect(MAGENTA.copy(alpha = 0.25f), cuerpo.topLeft, Size(a * 0.14f, al))
        }
        // Los aros de arriba y abajo.
        listOf(cuerpo.top, cuerpo.bottom).forEach { y ->
            drawRoundRect(Color(0xFF1E1420), Offset(c.x - a * 0.28f, y - al * 0.05f), Size(a * 0.56f, al * 0.1f), CornerRadius(1.5f * u))
            drawLine(FILO_ROJO.copy(alpha = 0.6f), Offset(c.x - a * 0.26f, y - al * 0.05f), Offset(c.x + a * 0.1f, y - al * 0.05f), 0.8f * u)
        }
        // La borla.
        drawLine(Color(0xFFB0303A), Offset(c.x, cuerpo.bottom + al * 0.05f), Offset(c.x, cuerpo.bottom + al * 0.14f), 1.2f * u)
        for (j in -2..2) drawLine(Color(0xFFC8404A), Offset(c.x, cuerpo.bottom + al * 0.13f), Offset(c.x + j * 1.2f * u, cuerpo.bottom + al * 0.34f), 1f * u)
    }

    /** La pizarra del menú colgada de dos alambres: el marco de acero, los renglones de signos inventados y su precio de rayas. */
    private fun DrawScope.pizarra(r: Rect, w: Float) {
        val u = density
        val t = tn(r.center)
        val clavo = Offset(r.center.x, r.top - w * 0.035f)
        drawLine(t.c(INOX), clavo, Offset(r.left + r.width * 0.15f, r.top), 0.8f * u)
        drawLine(t.c(INOX), clavo, Offset(r.right - r.width * 0.15f, r.top), 0.8f * u)
        remache(clavo, 1.6f * u, t)
        drawRect(t.s(0.5f), r.topLeft + Offset(w * 0.012f, w * 0.012f), r.size)
        drawRect(t.c(Color(0xFF55506A)), r.topLeft, r.size)
        drawRect(t.b(0.6f), r.topLeft, Size(r.width, 1.2f * u))
        drawRect(t.b(0.4f), r.topLeft, Size(1.2f * u, r.height))
        val dentro = Rect(r.left + w * 0.009f, r.top + w * 0.009f, r.right - w * 0.009f, r.bottom - w * 0.009f)
        drawRect(t.c(Color(0xFF242A2C)), dentro.topLeft, dentro.size)
        pinceladas(dentro, LOZA, 3401, 10, u, 0.06f)
        val tiza = t.c(LOZA).copy(alpha = 0.85f)
        // El dibujo del tazón arriba a la izquierda, a tiza.
        val tc = Offset(dentro.left + dentro.width * 0.14f, dentro.top + dentro.height * 0.17f)
        val tr = dentro.width * 0.07f
        drawArc(tiza, 0f, 180f, false, Offset(tc.x - tr, tc.y - tr * 0.6f), Size(tr * 2, tr * 1.4f), style = Stroke(1.1f * u))
        drawLine(tiza, Offset(tc.x - tr, tc.y + tr * 0.1f), Offset(tc.x + tr, tc.y + tr * 0.1f), 1.1f * u)
        // Los renglones: signos hechos de trazos (no son letras de ningún idioma) y, a la derecha, el precio en rayas y puntos.
        val renglones = 6
        for (k in 0 until renglones) {
            val y = dentro.top + dentro.height * (0.16f + 0.135f * k)
            var x = dentro.left + dentro.width * (if (k == 0) 0.3f else 0.08f)
            val signos = 3 + (azar(3410, k) * 3).toInt()
            for (s in 0 until signos) {
                glifo(Offset(x, y), dentro.width * 0.045f, 3420 + k * 10 + s, tiza)
                x += dentro.width * 0.07f
            }
            if (k > 0) {
                val px = dentro.right - dentro.width * 0.18f
                drawLine(tiza.copy(alpha = 0.6f), Offset(x + dentro.width * 0.02f, y + dentro.width * 0.02f), Offset(px - dentro.width * 0.03f, y + dentro.width * 0.02f), 0.6f * u)
                drawCircle(tiza, 1.1f * u, Offset(px, y + dentro.width * 0.012f))
                drawLine(tiza, Offset(px + dentro.width * 0.035f, y), Offset(px + dentro.width * 0.035f, y + dentro.width * 0.04f), 1.1f * u)
                drawLine(tiza, Offset(px + dentro.width * 0.07f, y), Offset(px + dentro.width * 0.1f, y + dentro.width * 0.04f), 1.1f * u)
            }
        }
        // Un renglón tachado y vuelto a escribir en rojo.
        val yt = dentro.top + dentro.height * (0.16f + 0.135f * 3) + dentro.width * 0.02f
        drawLine(t.c(ROJO), Offset(dentro.left + dentro.width * 0.06f, yt), Offset(dentro.left + dentro.width * 0.4f, yt - 1.5f * u), 1.4f * u)
    }

    /** Un signo inventado de la pizarra: dos o tres trazos de entre seis formas, siempre los mismos para la misma [semilla]. */
    private fun DrawScope.glifo(p: Offset, s: Float, semilla: Int, col: Color) {
        val u = density
        val g = 1.1f * u
        val trazos = 2 + (azar(semilla, 0) * 2).toInt()
        for (k in 0 until trazos) {
            when ((azar(semilla, k + 1) * 6).toInt()) {
                0 -> drawLine(col, Offset(p.x, p.y), Offset(p.x + s, p.y), g)
                1 -> drawLine(col, Offset(p.x + s * 0.5f, p.y - s * 0.1f), Offset(p.x + s * 0.5f, p.y + s), g)
                2 -> drawLine(col, Offset(p.x, p.y + s), Offset(p.x + s, p.y + s * 0.2f), g)
                3 -> drawArc(col, 180f, 180f, false, Offset(p.x, p.y + s * 0.3f), Size(s, s * 0.7f), style = Stroke(g))
                4 -> drawRect(col, Offset(p.x + s * 0.15f, p.y + s * 0.25f), Size(s * 0.7f, s * 0.6f), style = Stroke(g))
                else -> drawCircle(col, 0.9f * u, Offset(p.x + s * 0.8f, p.y + s * 0.15f))
            }
        }
    }

    /** La pantallita de los pedidos en su brazo: el marco, las barras de colores (luz propia) y su reflejo. */
    private fun DrawScope.pantallita(r: Rect) {
        val u = density
        val t = tn(r.center)
        drawRect(t.c(INOX), Offset(r.center.x - 1.5f * u, r.bottom), Size(3f * u, anchoW * 0.03f))
        drawRect(t.s(0.5f), r.topLeft + Offset(3f * u, 3f * u), r.size)
        drawRoundRect(t.c(Color(0xFF241E2E)), r.topLeft, r.size, CornerRadius(2f * u))
        drawRect(t.b(0.6f), r.topLeft + Offset(2f * u, 0f), Size(r.width - 4f * u, 1f * u))
        val v = Rect(r.left + r.width * 0.08f, r.top + r.height * 0.12f, r.right - r.width * 0.08f, r.bottom - r.height * 0.14f)
        mancha(v.center, r.width * 0.9f, CIAN.copy(alpha = 0.18f))
        drawRect(Color(0xFF0E2A30), v.topLeft, v.size)
        val cols = listOf(SODIO, SODIO, AMBAR, CIAN, SODIO, MAGENTA)
        for (k in 0 until 6) {
            val y = v.top + v.height * (0.1f + 0.14f * k)
            drawRect(cols[k].copy(alpha = 0.85f), Offset(v.left + v.width * 0.08f, y), Size(v.width * (0.25f + 0.6f * azar(3450, k)), v.height * 0.07f))
        }
        drawPath(poli(v.left + v.width * 0.55f, v.top, v.left + v.width * 0.75f, v.top, v.left + v.width * 0.5f, v.bottom, v.left + v.width * 0.3f, v.bottom), Color.White.copy(alpha = 0.06f))
    }

    /** El ventilador de pared: el soporte, la rejilla de alambre, las aspas que se adivinan y la tira de papel atada que vuela. */
    private fun DrawScope.ventilador(c: Offset, r: Float) {
        val u = density
        val t = tn(c)
        // El brazo a la pared.
        drawLine(t.c(INOX), c + Offset(r * 0.2f, r * 0.9f), c + Offset(r * 0.5f, r * 1.6f), 2.2f * u)
        drawRect(t.c(Color(0xFF3A3448)), Offset(c.x + r * 0.2f, c.y + r * 1.5f), Size(r * 0.7f, r * 0.3f))
        drawCircle(t.s(0.45f), r, c + Offset(r * 0.25f, r * 0.2f))
        drawCircle(t.c(Color(0xFF2E2838)), r, c)
        // Las aspas tras la rejilla.
        for (k in 0 until 3) {
            val a = 0.4f + k * 2.094f
            rotate(Math.toDegrees(a.toDouble()).toFloat(), c) { drawOval(t.c(ESMALTE), Offset(c.x - r * 0.15f, c.y - r * 0.85f), Size(r * 0.42f, r * 0.75f)) }
        }
        drawCircle(t.c(INOX), r * 0.18f, c)
        // La rejilla de alambre.
        for (k in 1..3) drawCircle(t.c(INOX).copy(alpha = 0.8f), r * k / 3.2f, c, style = Stroke(0.7f * u))
        for (k in 0 until 8) {
            val a = k * Math.PI / 4
            drawLine(t.c(INOX).copy(alpha = 0.7f), c, Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), 0.6f * u)
        }
        drawCircle(t.c(INOX), r, c, style = Stroke(1.6f * u))
        drawArc(t.b(0.8f), 160f, 90f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1f * u))
        // La tira de papel que vuela hacia la izquierda.
        val tira = Path().apply {
            moveTo(c.x - r * 0.95f, c.y - r * 0.2f)
            cubicTo(c.x - r * 1.5f, c.y - r * 0.6f, c.x - r * 1.8f, c.y + r * 0.1f, c.x - r * 2.5f, c.y - r * 0.3f)
            lineTo(c.x - r * 2.5f, c.y - r * 0.12f)
            cubicTo(c.x - r * 1.8f, c.y + r * 0.28f, c.x - r * 1.5f, c.y - r * 0.42f, c.x - r * 0.95f, c.y - r * 0.02f)
            close()
        }
        drawPath(tira, t.c(MAGENTA))
        drawPath(tira, t.b(0.3f), style = Stroke(0.6f * u))
    }

    /** Un montante ranurado atornillado a la pared: la cara de luz, la de sombra, las ranuras y su sombra en la chapa. */
    private fun DrawScope.montante(r: Rect) {
        val u = density
        val t = tn(r.center)
        drawRect(SOMBRA.copy(alpha = 0.45f), Offset(r.right, r.top), Size(r.width * 0.45f, r.height))
        drawRect(t.c(INOX), r.topLeft, r.size)
        // Pocas franjas de luz a lo largo (la luz cae por distancia: arriba más que abajo).
        val pasos = 8
        for (k in 0 until pasos) {
            val y0 = r.top + r.height * k / pasos
            val tk = tn(Offset(r.center.x, y0 + r.height / pasos / 2))
            drawRect(tk.c(INOX), Offset(r.left, y0), Size(r.width, r.height / pasos + 1f))
            drawRect(tk.b(0.45f), Offset(r.left, y0), Size(r.width * 0.22f, r.height / pasos + 1f))
            drawRect(tk.s(0.4f), Offset(r.left + r.width * 0.7f, y0), Size(r.width * 0.3f, r.height / pasos + 1f))
        }
        // Las ranuras.
        var y = r.top + r.width
        while (y < r.bottom - r.width) {
            val tk = tn(Offset(r.center.x, y))
            drawRoundRect(tk.s(0.75f), Offset(r.left + r.width * 0.3f, y), Size(r.width * 0.4f, r.width * 0.75f), CornerRadius(r.width * 0.2f))
            drawLine(tk.b(0.4f), Offset(r.left + r.width * 0.3f, y + r.width * 0.78f), Offset(r.left + r.width * 0.7f, y + r.width * 0.78f), 0.6f * u)
            y += r.width * 1.5f
        }
    }

    /**
     * Una repisa de acero de lado a lado: el fondo (de [y0] a [y1], visto desde arriba), el labio de frente (de [y1] a [y2]) con
     * su filo de luz, abolladuras, el reflejo del letrero, y debajo su sombra en la pared y las escuadras. [alta], la de la corona.
     */
    private fun DrawScope.repisa(y0: Float, y1: Float, y2: Float, w: Float, alta: Boolean) {
        val u = density
        val x0 = w * 0.012f; val x1 = w * 0.988f
        // La sombra en la pared: más honda pegada a la repisa.
        val tt = tn(Offset(w * 0.4f, y2))
        drawRect(tt.s(0.55f), Offset(x0 + w * 0.01f, y2), Size(x1 - x0, w * 0.012f))
        drawRect(tt.s(0.25f), Offset(x0 + w * 0.015f, y2 + w * 0.012f), Size(x1 - x0, w * 0.02f))
        // Las escuadras.
        listOf(0.04f, 0.5f, 0.96f).forEach { fx ->
            val x = w * fx
            val t = tn(Offset(x, y2))
            val e = w * (if (alta) 0.05f else 0.035f)
            drawPath(poli(x - 1.5f * u, y2, x + e * 0.15f, y2, x - 1.5f * u, y2 + e), t.c(Color(0xFF4A4458)))
            drawLine(t.b(0.6f), Offset(x - 1.5f * u, y2), Offset(x - 1.5f * u, y2 + e), 0.8f * u)
        }
        // El fondo (la cara de arriba) y el labio, por tramos para que la luz caiga a lo largo.
        val tramos = 6
        for (k in 0 until tramos) {
            val a = x0 + (x1 - x0) * k / tramos
            val bw = (x1 - x0) / tramos + 1f
            val t = tn(Offset(a + bw / 2, y1))
            drawRect(t.c(mezcla(INOX, CHAPA, 0.35f)), Offset(a, y0), Size(bw, y1 - y0))
            drawRect(t.c(mezcla(INOX, NOCHE, 0.25f)), Offset(a, y1), Size(bw, y2 - y1))
            drawRect(t.b(0.8f), Offset(a, y1), Size(bw, 1.2f * u))
            drawRect(t.s(0.45f), Offset(a, y2 - 1.6f * u), Size(bw, 1.6f * u))
        }
        drawRect(SOMBRA.copy(alpha = 0.35f), Offset(x0, y0), Size(x1 - x0, 1.6f * u))
        // El reflejo del neón: una veta magenta y una cian sobre el labio, según cuánto le llega.
        val k = luzLetrero(Offset(0.3f, y1 / w))
        drawRect(MAGENTA.copy(alpha = (0.9f * k).coerceAtMost(0.55f)), Offset(w * 0.08f, y1 + (y2 - y1) * 0.35f), Size(w * 0.36f, 1.2f * u))
        drawRect(CIAN.copy(alpha = (0.6f * k).coerceAtMost(0.4f)), Offset(w * 0.2f, y1 + (y2 - y1) * 0.6f), Size(w * 0.14f, 0.9f * u))
        // Abolladuras y tornillos del labio.
        for (j in 0 until 3) {
            val x = w * (0.1f + 0.8f * azar((y0 / w * 100).toInt(), j))
            val t = tn(Offset(x, y1))
            drawLine(t.b(0.45f), Offset(x, y1 + (y2 - y1) * 0.45f), Offset(x + w * 0.04f, y1 + (y2 - y1) * 0.55f), 0.7f * u)
            drawOval(t.s(0.35f), Offset(x + w * 0.05f, y1 + (y2 - y1) * 0.3f), Size(w * 0.018f, (y2 - y1) * 0.4f))
        }
        listOf(0.04f, 0.5f, 0.96f).forEach { fx -> remache(Offset(w * fx, (y1 + y2) / 2), 1.3f * u, tn(Offset(w * fx, y1))) }
    }

    /** El riel de comandas: el perfil de aluminio con su canal, los soportes a la pared y su sombra. */
    private fun DrawScope.riel(y: Float, w: Float) {
        val u = density
        val g = w * 0.017f
        val x0 = w * 0.03f; val x1 = w * 0.97f
        drawRect(SOMBRA.copy(alpha = 0.4f), Offset(x0 + w * 0.012f, y + g * 0.8f), Size(x1 - x0, g * 0.7f))
        val tramos = 5
        for (k in 0 until tramos) {
            val a = x0 + (x1 - x0) * k / tramos
            val bw = (x1 - x0) / tramos + 1f
            val t = tn(Offset(a + bw / 2, y))
            drawRect(t.c(INOX), Offset(a, y), Size(bw, g))
            drawRect(t.b(0.85f), Offset(a, y), Size(bw, 1f * u))
            // El canal donde corren las bolas.
            drawRect(t.s(0.6f), Offset(a, y + g * 0.55f), Size(bw, g * 0.22f))
            drawRect(t.s(0.3f), Offset(a, y + g * 0.85f), Size(bw, g * 0.15f))
        }
        val k = luzLetrero(Offset(0.3f, y / w))
        drawRect(CIAN.copy(alpha = (0.8f * k).coerceAtMost(0.5f)), Offset(w * 0.1f, y + 0.3f * u), Size(w * 0.25f, 0.8f * u))
        listOf(0.03f, 0.5f, 0.97f).forEach { fx -> remache(Offset(w * fx, y + g * 0.35f), 1.6f * u, tn(Offset(w * fx, y))) }
    }

    // ---------------------------------------------------------------- lo que hay en la repisa alta

    /** La figura de la suerte de loza blanca: sentada, con el brazo levantado, el collar rojo, el cascabel y una moneda. */
    private fun DrawScope.figura(pie: Offset, s: Float) {
        val u = density
        val t = tn(pie - Offset(0f, s * 0.5f))
        val cx = pie.x
        contacto(Offset(cx, pie.y), s * 0.85f, s * 0.08f)
        // El cojín rojo.
        drawRoundRect(t.c(ROJO), Offset(cx - s * 0.4f, pie.y - s * 0.09f), Size(s * 0.8f, s * 0.09f), CornerRadius(s * 0.04f))
        drawRect(t.b(0.5f), Offset(cx - s * 0.36f, pie.y - s * 0.09f), Size(s * 0.6f, 0.8f * u))
        val cuerpoR = Rect(cx - s * 0.32f, pie.y - s * 0.66f, cx + s * 0.32f, pie.y - s * 0.07f)
        val cabeza = Rect(cx - s * 0.3f, pie.y - s * 0.98f, cx + s * 0.3f, pie.y - s * 0.5f)
        val formaCuerpo = Path().apply {
            moveTo(cuerpoR.left + s * 0.04f, cuerpoR.bottom)
            cubicTo(cuerpoR.left - s * 0.04f, cuerpoR.top + s * 0.25f, cuerpoR.left + s * 0.1f, cuerpoR.top, cuerpoR.center.x, cuerpoR.top)
            cubicTo(cuerpoR.right - s * 0.1f, cuerpoR.top, cuerpoR.right + s * 0.04f, cuerpoR.top + s * 0.25f, cuerpoR.right - s * 0.04f, cuerpoR.bottom)
            close()
        }
        val formaCabeza = Path().apply {
            addOval(cabeza)
            // Las orejas.
            addPath(poli(cabeza.left + s * 0.04f, cabeza.top + s * 0.14f, cabeza.left + s * 0.06f, cabeza.top - s * 0.06f, cabeza.left + s * 0.2f, cabeza.top + s * 0.04f))
            addPath(poli(cabeza.right - s * 0.04f, cabeza.top + s * 0.14f, cabeza.right - s * 0.06f, cabeza.top - s * 0.06f, cabeza.right - s * 0.2f, cabeza.top + s * 0.04f))
        }
        // El brazo levantado (a la izquierda de quien mira), de la cadera al hombro y arriba.
        val brazo = Path().apply { addRoundRect(RoundRect(Rect(cx - s * 0.44f, pie.y - s * 1.0f, cx - s * 0.26f, pie.y - s * 0.55f), CornerRadius(s * 0.09f))) }
        listOf(formaCuerpo, formaCabeza, brazo).forEach { f ->
            drawPath(f, t.c(LOZA))
            clipPath(f) {
                val b = f.getBounds()
                cuerpo(b, t, 1.1f)
            }
        }
        // El interior rosa de las orejas y la palma.
        drawCircle(t.c(Color(0xFFE89AA8)), s * 0.05f, Offset(cx - s * 0.35f, pie.y - s * 0.93f))
        // La cara: ojos cerrados de arco, la nariz, la boca y los bigotes.
        val ojo = t.c(NOCHE)
        drawArc(ojo, 200f, 140f, false, Offset(cx - s * 0.17f, pie.y - s * 0.8f), Size(s * 0.11f, s * 0.07f), style = Stroke(1.1f * u))
        drawArc(ojo, 200f, 140f, false, Offset(cx + s * 0.06f, pie.y - s * 0.8f), Size(s * 0.11f, s * 0.07f), style = Stroke(1.1f * u))
        drawCircle(t.c(Color(0xFFE06A7A)), s * 0.025f, Offset(cx, pie.y - s * 0.7f))
        for (j in listOf(-1f, 1f)) {
            drawLine(ojo.copy(alpha = 0.6f), Offset(cx + j * s * 0.12f, pie.y - s * 0.68f), Offset(cx + j * s * 0.28f, pie.y - s * 0.7f), 0.6f * u)
            drawLine(ojo.copy(alpha = 0.6f), Offset(cx + j * s * 0.12f, pie.y - s * 0.66f), Offset(cx + j * s * 0.27f, pie.y - s * 0.62f), 0.6f * u)
        }
        // El collar rojo con su cascabel y la moneda dorada en la otra pata.
        drawArc(t.c(ROJO), 20f, 140f, false, Offset(cx - s * 0.24f, pie.y - s * 0.66f), Size(s * 0.48f, s * 0.14f), style = Stroke(s * 0.05f))
        drawCircle(t.c(MOSTAZA), s * 0.055f, Offset(cx, pie.y - s * 0.52f))
        drawCircle(t.b(0.9f), s * 0.02f, Offset(cx - s * 0.02f, pie.y - s * 0.54f))
        drawOval(t.c(MOSTAZA), Offset(cx + s * 0.02f, pie.y - s * 0.4f), Size(s * 0.24f, s * 0.2f))
        drawOval(t.s(0.3f), Offset(cx + s * 0.13f, pie.y - s * 0.4f), Size(s * 0.13f, s * 0.2f))
        drawOval(t.c(LOZA), Offset(cx + s * 0.16f, pie.y - s * 0.42f), Size(s * 0.12f, s * 0.12f))
        // Un desconchón en el cojín: el uso.
        drawCircle(t.c(Color(0xFFB8A894)), s * 0.03f, Offset(cx + s * 0.28f, pie.y - s * 0.05f))
    }

    /** La pila de tazones de loza con su franja de esmalte, unos dentro de otros: se ve el borde de cada uno. */
    private fun DrawScope.tazones(pie: Offset, a: Float) {
        val u = density
        val t = tn(pie - Offset(0f, a * 0.4f))
        contacto(Offset(pie.x, pie.y), a * 1.1f, a * 0.08f)
        val alto = a * 0.42f
        val n = 3
        for (i in 0 until n) {
            val y = pie.y - a * 0.11f * i
            val r = Rect(pie.x - a / 2, y - alto, pie.x + a / 2, y)
            val f = Path().apply {
                moveTo(r.left, r.top)
                cubicTo(r.left, r.top + alto * 0.7f, r.left + a * 0.25f, r.bottom, r.center.x - a * 0.18f, r.bottom)
                lineTo(r.center.x + a * 0.18f, r.bottom)
                cubicTo(r.right - a * 0.25f, r.bottom, r.right, r.top + alto * 0.7f, r.right, r.top)
                close()
            }
            drawPath(f, t.c(LOZA))
            clipPath(f) {
                cuerpo(r, t, 1.2f)
                drawRect(t.c(ESMALTE), Offset(r.left, r.top + alto * 0.12f), Size(a, alto * 0.1f))
                drawRect(t.s(0.2f), Offset(r.left + a * 0.6f, r.top + alto * 0.12f), Size(a * 0.4f, alto * 0.1f))
            }
            drawRect(t.s(0.6f), Offset(r.left + a * 0.03f, r.top + alto * 0.02f), Size(a * 0.94f, 1.6f * u))
            drawRoundRect(t.c(mezcla(LOZA, Color.White, 0.2f)), Offset(r.left - a * 0.02f, r.top - 1.6f * u), Size(a * 1.04f, 2.4f * u), CornerRadius(1.2f * u))
            drawLine(t.b(0.9f), Offset(r.left + 1f * u, r.top - 1.6f * u), Offset(r.right - a * 0.4f, r.top - 1.6f * u), 0.9f * u)
        }
        // La boca del de arriba: la loza de dentro en sombra.
        val top = pie.y - a * 0.11f * (n - 1) - alto
        drawOval(t.c(mezcla(LOZA, NOCHE, 0.35f)), Offset(pie.x - a / 2, top - a * 0.06f), Size(a, a * 0.12f))
        drawOval(t.s(0.3f), Offset(pie.x - a * 0.4f, top - a * 0.03f), Size(a * 0.8f, a * 0.08f))
        drawArc(t.b(0.8f), 180f, 180f, false, Offset(pie.x - a / 2, top - a * 0.06f), Size(a, a * 0.12f), style = Stroke(0.9f * u))
    }

    /** El vaso de lata abollado con los palillos de bambú en abanico. */
    private fun DrawScope.vasoPalillos(pie: Offset, w: Float) {
        val u = density
        val a = w * 0.042f; val al = w * 0.062f
        val t = tn(pie - Offset(0f, al))
        contacto(Offset(pie.x, pie.y), a * 1.4f, a * 0.2f)
        for (k in 0 until 7) {
            val ang = -18f + 36f * k / 6f + (azar(3500, k) - 0.5f) * 6f
            val largo = w * (0.085f + 0.02f * azar(3501, k))
            rotate(ang, Offset(pie.x, pie.y - al * 0.4f)) {
                drawLine(t.c(BAMBU), Offset(pie.x, pie.y - al * 0.4f), Offset(pie.x, pie.y - al * 0.4f - largo), 1.6f * u, StrokeCap.Round)
                drawLine(t.b(0.5f), Offset(pie.x - 0.5f * u, pie.y - al * 0.4f), Offset(pie.x - 0.5f * u, pie.y - al * 0.4f - largo), 0.5f * u)
            }
        }
        val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
        drawRect(t.c(INOX), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { cuerpo(r, t, 1.3f) }
        drawOval(t.c(mezcla(INOX, NOCHE, 0.5f)), Offset(r.left, r.top - a * 0.12f), Size(a, a * 0.24f))
        drawArc(t.b(0.9f), 180f, 180f, false, Offset(r.left, r.top - a * 0.12f), Size(a, a * 0.24f), style = Stroke(0.9f * u))
        // Una abolladura.
        drawArc(t.s(0.5f), 200f, 120f, false, Offset(r.left + a * 0.45f, r.top + al * 0.45f), Size(a * 0.35f, al * 0.2f), style = Stroke(0.9f * u))
    }

    /**
     * La arrocera (el objeto grande): el cuerpo de esmalte verde azulado con su franja, la tapa de acero en cúpula con el asa
     * y la válvula, el panel con el piloto ámbar encendido, el cable que baja, y su vapor, que recoge el rojo y el magenta.
     */
    private fun DrawScope.arrocera(pie: Offset, w: Float) {
        val u = density
        val a = w * 0.2f; val al = w * 0.125f
        val t = tn(pie - Offset(0f, al * 0.5f))
        contacto(Offset(pie.x, pie.y), a * 1.1f, w * 0.014f, 0.65f)
        // El cable que baja por detrás de la repisa.
        drawPath(Path().apply { moveTo(pie.x + a * 0.45f, pie.y - al * 0.2f); cubicTo(pie.x + a * 0.6f, pie.y, pie.x + a * 0.55f, pie.y + w * 0.04f, pie.x + a * 0.62f, pie.y + w * 0.09f) }, t.c(Color(0xFF15101C)), style = Stroke(2f * u))
        val cuerpoR = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
        val forma = Path().apply {
            moveTo(cuerpoR.left + a * 0.06f, cuerpoR.bottom)
            cubicTo(cuerpoR.left - a * 0.02f, cuerpoR.bottom - al * 0.3f, cuerpoR.left, cuerpoR.top + al * 0.1f, cuerpoR.left + a * 0.03f, cuerpoR.top)
            lineTo(cuerpoR.right - a * 0.03f, cuerpoR.top)
            cubicTo(cuerpoR.right, cuerpoR.top + al * 0.1f, cuerpoR.right + a * 0.02f, cuerpoR.bottom - al * 0.3f, cuerpoR.right - a * 0.06f, cuerpoR.bottom)
            close()
        }
        drawPath(forma, t.c(ESMALTE))
        clipPath(forma) {
            cuerpo(cuerpoR, t, 1.2f)
            // La franja de loza con el dibujo de olas, y el desgaste.
            drawRect(t.c(LOZA), Offset(cuerpoR.left, cuerpoR.top + al * 0.18f), Size(a, al * 0.14f))
            for (k in 0 until 7) drawArc(t.c(ESMALTE), 180f, 180f, false, Offset(cuerpoR.left + a * k / 7f, cuerpoR.top + al * 0.2f), Size(a / 7f, al * 0.12f), style = Stroke(0.8f * u))
            drawRect(t.s(0.25f), Offset(cuerpoR.left + a * 0.6f, cuerpoR.top + al * 0.18f), Size(a * 0.4f, al * 0.14f))
            pinceladas(cuerpoR, GRASA, 3510, 6, u, 0.25f)
            // El reflejo de neón en el esmalte, a la izquierda.
            drawRect(MAGENTA.copy(alpha = 0.3f), Offset(cuerpoR.left + a * 0.08f, cuerpoR.top + al * 0.38f), Size(a * 0.03f, al * 0.5f))
        }
        // El panel con el piloto (luz propia) y la palanca.
        val panel = Rect(pie.x - a * 0.16f, pie.y - al * 0.58f, pie.x + a * 0.16f, pie.y - al * 0.14f)
        drawRoundRect(t.c(Color(0xFF241E2E)), panel.topLeft, panel.size, CornerRadius(2f * u))
        drawRect(t.b(0.5f), panel.topLeft + Offset(2f * u, 0f), Size(panel.width - 4f * u, 0.8f * u))
        val piloto = Offset(panel.left + panel.width * 0.3f, panel.top + panel.height * 0.35f)
        drawCircle(AMBAR.copy(alpha = 0.3f), 4.5f * u, piloto)
        drawCircle(AMBAR, 1.9f * u, piloto)
        drawCircle(Color.White.copy(alpha = 0.7f), 0.7f * u, piloto - Offset(0.5f * u, 0.5f * u))
        drawCircle(t.c(Color(0xFF4A1A1A)), 1.6f * u, Offset(panel.left + panel.width * 0.7f, piloto.y))
        drawRoundRect(t.c(INOX), Offset(panel.left + panel.width * 0.25f, panel.top + panel.height * 0.62f), Size(panel.width * 0.5f, panel.height * 0.22f), CornerRadius(1.5f * u))
        // La tapa en cúpula, el asa y la válvula del vapor.
        val tapa = Rect(cuerpoR.left + a * 0.02f, cuerpoR.top - al * 0.36f, cuerpoR.right - a * 0.02f, cuerpoR.top + al * 0.08f)
        val formaTapa = Path().apply { moveTo(tapa.left, tapa.bottom); cubicTo(tapa.left, tapa.top, tapa.right, tapa.top, tapa.right, tapa.bottom); close() }
        drawPath(formaTapa, t.c(INOX))
        clipPath(formaTapa) {
            cuerpo(tapa, t, 1.3f)
            drawOval(t.b(0.5f), Offset(tapa.left + tapa.width * 0.18f, tapa.top + tapa.height * 0.25f), Size(tapa.width * 0.2f, tapa.height * 0.15f))
        }
        drawRect(t.c(Color(0xFF3A3448)), Offset(tapa.left - a * 0.01f, tapa.bottom - al * 0.04f), Size(tapa.width + a * 0.02f, al * 0.06f))
        val asa = Rect(pie.x - a * 0.14f, tapa.top - al * 0.05f, pie.x + a * 0.14f, tapa.top + al * 0.12f)
        drawRoundRect(t.c(Color(0xFF241E2E)), asa.topLeft, asa.size, CornerRadius(asa.height / 2))
        drawLine(t.b(0.7f), Offset(asa.left + asa.height * 0.3f, asa.top + 0.8f * u), Offset(asa.right - asa.height * 0.3f, asa.top + 0.8f * u), 0.8f * u)
        val valvula = Offset(pie.x + a * 0.25f, tapa.top + tapa.height * 0.3f)
        drawCircle(t.c(Color(0xFF241E2E)), 2.4f * u, valvula)
        // El vapor: volutas que suben y toman el rojo del farolillo y el magenta del letrero.
        for (k in 0 until 7) {
            val f = k / 6f
            val c = Offset(valvula.x + w * (0.02f * sin(k * 1.7f) - 0.03f * f), valvula.y - w * (0.03f + 0.3f * f))
            val r = w * (0.018f + 0.035f * f)
            val col = mezcla(mezcla(LOZA, ROJO, 0.55f * luzFarol(c / w)), MAGENTA, 0.3f * f)
            drawOval(col.copy(alpha = 0.16f * (1f - f * 0.7f)), Offset(c.x - r, c.y - r * 0.7f), Size(r * 2f, r * 1.4f))
            drawOval(col.copy(alpha = 0.1f * (1f - f * 0.7f)), Offset(c.x - r * 0.6f, c.y - r * 0.9f), Size(r * 1.2f, r * 0.9f))
        }
    }

    /** Una botella de salsa sin etiqueta legible: el cuerpo, el hombro, el pitorro o el tapón, una chorrera y su brillo. */
    private fun DrawScope.botella(pie: Offset, a: Float, al: Float, col: Color, tipo: Int) {
        val u = density
        val t = tn(pie - Offset(0f, al * 0.5f))
        contacto(Offset(pie.x, pie.y), a * 1.3f, a * 0.25f)
        val r = Rect(pie.x - a / 2, pie.y - al * 0.72f, pie.x + a / 2, pie.y)
        val forma = Path().apply {
            addRoundRect(RoundRect(r, CornerRadius(a * 0.2f)))
            if (tipo == 2) addRect(Rect(pie.x - a * 0.16f, pie.y - al, pie.x + a * 0.16f, r.top + a * 0.1f))
            else addPath(poli(r.left + a * 0.05f, r.top + a * 0.1f, pie.x - a * 0.12f, pie.y - al * 0.86f, pie.x + a * 0.12f, pie.y - al * 0.86f, r.right - a * 0.05f, r.top + a * 0.1f))
        }
        drawPath(forma, t.c(col))
        clipPath(forma) {
            cuerpo(Rect(r.left, pie.y - al, r.right, pie.y), t, 1.3f)
            if (tipo == 2) {
                // La botella de vidrio oscuro: la etiqueta en blanco, gastada.
                drawRect(t.c(LOZA), Offset(r.left, r.top + al * 0.2f), Size(a, al * 0.28f))
                drawRect(t.c(GRASA).copy(alpha = 0.4f), Offset(r.left, r.top + al * 0.38f), Size(a, al * 0.06f))
                drawRect(t.s(0.3f), Offset(r.left + a * 0.6f, r.top + al * 0.2f), Size(a * 0.4f, al * 0.28f))
            }
        }
        if (tipo == 2) drawRect(t.c(ROJO), Offset(pie.x - a * 0.2f, pie.y - al - a * 0.12f), Size(a * 0.4f, a * 0.2f))
        else {
            // El pitorro y su tapita.
            drawPath(poli(pie.x - a * 0.12f, pie.y - al * 0.86f, pie.x - a * 0.03f, pie.y - al, pie.x + a * 0.03f, pie.y - al, pie.x + a * 0.12f, pie.y - al * 0.86f), t.c(if (tipo == 0) Color(0xFF3AA06A) else ROJO))
            // La chorrera seca bajo el pitorro.
            drawLine(t.c(mezcla(col, NOCHE, 0.3f)), Offset(pie.x + a * 0.1f, pie.y - al * 0.85f), Offset(pie.x + a * 0.15f, pie.y - al * 0.6f), 1.4f * u, StrokeCap.Round)
        }
        drawLine(t.b(0.9f), Offset(r.left + a * 0.2f, r.top + a * 0.2f), Offset(r.left + a * 0.2f, r.bottom - a * 0.3f), 1f * u)
    }

    // ---------------------------------------------------------------- al pie

    /** El zócalo perforado del mueble, el canto del mostrador con dos tazones servidos y palillos, y un taburete rojo. */
    private fun DrawScope.pie(d: Disposicion, w: Float, alto: Float) {
        val u = density
        val b = d.base
        // La luz de los letreritos de la última fila, que baja al mostrador.
        fun kv(y: Float) = (0.4f * (1f - (y - b.top) / (w * 0.32f))).coerceIn(0f, 0.4f)
        // El zócalo perforado.
        val z = Rect(w * 0.012f, b.top + w * 0.005f, w * 0.988f, b.top + w * 0.1f)
        val tz = tn(z.center, kv(z.center.y))
        drawRect(tz.c(mezcla(CHAPA, NOCHE, 0.5f)), z.topLeft, z.size)
        drawRect(tz.b(0.3f), z.topLeft, Size(z.width, 1f * u))
        var x = z.left + w * 0.02f
        var fila = 0
        while (x < z.right - w * 0.01f) {
            for (j in 0 until 3) {
                if ((j + fila) % 2 == 0) {
                    val p = Offset(x, z.top + z.height * (0.25f + 0.25f * j))
                    drawCircle(tz.s(0.6f), 1.6f * u, p)
                    drawArc(tz.b(0.4f), 0f, 180f, false, Offset(p.x - 1.6f * u, p.y - 1.6f * u), Size(3.2f * u, 3.2f * u), style = Stroke(0.5f * u))
                }
            }
            x += w * 0.022f; fila++
        }
        drawRect(tz.s(0.5f), Offset(z.left, z.bottom - 2f * u), Size(z.width, 2f * u))
        // La placa de «y N más»: un letrerito de neón verde atornillado al zócalo.
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRoundRect(PLACA, r.topLeft, r.size, CornerRadius(3f * u))
            tubo(Path().apply { addRoundRect(RoundRect(Rect(r.left + 3f * u, r.top + 3f * u, r.right - 3f * u, r.bottom - 3f * u), CornerRadius(3f * u))) }, SODIO, 1.2f * u)
            listOf(Offset(r.left - 3f * u, r.center.y), Offset(r.right + 3f * u, r.center.y)).forEach { remache(it, 1.4f * u, tz) }
        }
        // El mostrador: la cara de arriba vista desde dentro, el canto y el frente.
        val cTop = b.top + w * 0.13f
        val cEdge = b.top + w * 0.225f
        val cBot = b.top + w * 0.255f
        val cR = w * 0.74f
        val tm = tn(Offset(w * 0.35f, cTop), kv(cTop))
        drawRect(SOMBRA.copy(alpha = 0.55f), Offset(0f, cTop - w * 0.02f), Size(cR + w * 0.02f, w * 0.02f))
        drawRect(tm.c(mezcla(INOX, CHAPA, 0.25f)), Offset(0f, cTop), Size(cR, cEdge - cTop))
        pinceladas(Rect(0f, cTop, cR, cEdge), FILO, 3601, 10, u, 0.06f)
        pinceladas(Rect(0f, cTop, cR, cEdge), GRASA, 3602, 6, u, 0.2f)
        // Un cerco de tazón y unas gotas.
        drawOval(tm.s(0.35f), Offset(w * 0.6f, cTop + w * 0.03f), Size(w * 0.08f, w * 0.025f), style = Stroke(1f * u))
        val tc = tn(Offset(w * 0.35f, cEdge), kv(cEdge))
        drawRect(tc.c(INOX), Offset(0f, cEdge), Size(cR, cBot - cEdge))
        drawRect(tc.b(0.9f), Offset(0f, cEdge), Size(cR, 1.2f * u))
        drawRect(SODIO.copy(alpha = 0.3f), Offset(w * 0.05f, cEdge + 2f * u), Size(cR * 0.8f, 0.8f * u))
        drawRect(tc.c(mezcla(CHAPA, NOCHE, 0.4f)), Offset(0f, cBot), Size(cR, alto - cBot))
        drawRect(SOMBRA.copy(alpha = 0.5f), Offset(0f, cBot), Size(cR, w * 0.012f))
        drawRoundRect(tc.c(INOX), Offset(cR - w * 0.02f, cTop), Size(w * 0.025f, alto - cTop), CornerRadius(w * 0.012f))
        drawRect(tc.b(0.5f), Offset(cR - w * 0.018f, cTop), Size(1f * u, alto - cTop))
        mancha(Offset(w * 0.35f, cTop + w * 0.03f), w * 0.35f, SODIO.copy(alpha = 0.07f))
        // Los dos tazones servidos y los palillos.
        tazonServido(Offset(w * 0.2f, cTop + w * 0.075f), w * 0.17f, kv(cTop), 0)
        tazonServido(Offset(w * 0.47f, cTop + w * 0.07f), w * 0.145f, kv(cTop), 1)
        val tp = tn(Offset(w * 0.6f, cTop), kv(cTop))
        listOf(0f, w * 0.012f).forEach { dy ->
            drawLine(tp.s(0.4f), Offset(w * 0.53f, cTop + w * 0.055f + dy + 2f * u), Offset(w * 0.7f, cTop + w * 0.075f + dy + 2f * u), 1.8f * u, StrokeCap.Round)
            drawLine(tp.c(BAMBU), Offset(w * 0.53f, cTop + w * 0.055f + dy), Offset(w * 0.7f, cTop + w * 0.075f + dy), 1.8f * u, StrokeCap.Round)
            drawLine(tp.b(0.6f), Offset(w * 0.53f, cTop + w * 0.055f + dy - 0.6f * u), Offset(w * 0.7f, cTop + w * 0.075f + dy - 0.6f * u), 0.5f * u)
        }
        // El taburete rojo de plástico, cortado por el borde derecho y por abajo.
        val ts = tn(Offset(w * 0.88f, cEdge), kv(cEdge) * 0.8f)
        val asiento = Rect(w * 0.79f, cEdge - w * 0.02f, w * 1.04f, cEdge + w * 0.035f)
        drawOval(SOMBRA.copy(alpha = 0.45f), Offset(asiento.left + w * 0.02f, asiento.bottom), Size(asiento.width, w * 0.03f))
        listOf(0.82f, 0.98f).forEach { fx ->
            drawPath(poli(w * fx, asiento.center.y, w * (fx + 0.035f), asiento.center.y, w * (fx + (if (fx < 0.9f) -0.01f else 0.06f)), alto, w * (fx + (if (fx < 0.9f) -0.045f else 0.025f)), alto), ts.c(mezcla(ROJO, NOCHE, 0.25f)))
            drawLine(ts.b(0.5f), Offset(w * fx, asiento.center.y), Offset(w * (fx + (if (fx < 0.9f) -0.045f else 0.025f)), alto), 0.9f * u)
        }
        drawRect(ts.c(mezcla(ROJO, NOCHE, 0.4f)), Offset(w * 0.81f, asiento.bottom + w * 0.06f), Size(w * 0.2f, w * 0.012f))
        drawRect(ts.c(mezcla(ROJO, NOCHE, 0.15f)), Offset(asiento.left, asiento.center.y), Size(asiento.width, asiento.height * 0.55f))
        drawOval(ts.c(ROJO), asiento.topLeft, Size(asiento.width, asiento.height * 0.75f))
        drawOval(ts.s(0.45f), Offset(asiento.left + asiento.width * 0.35f, asiento.top + asiento.height * 0.22f), Size(asiento.width * 0.18f, asiento.height * 0.28f))
        drawArc(ts.b(0.8f), 180f, 90f, false, asiento.topLeft, Size(asiento.width, asiento.height * 0.75f), style = Stroke(1f * u))
    }

    /** Un tazón servido: la loza con su franja, el caldo, los fideos, la rodaja de huevo, la cebolleta y su vapor. */
    private fun DrawScope.tazonServido(c: Offset, a: Float, kv: Float, tipo: Int) {
        val u = density
        val t = tn(c, kv)
        val al = a * 0.42f
        contacto(Offset(c.x + a * 0.04f, c.y + al * 0.55f), a * 0.9f, a * 0.12f, 0.6f)
        val r = Rect(c.x - a / 2, c.y - al * 0.45f, c.x + a / 2, c.y + al * 0.55f)
        val f = Path().apply {
            moveTo(r.left, r.top)
            cubicTo(r.left, r.top + al * 0.7f, r.left + a * 0.25f, r.bottom, c.x - a * 0.16f, r.bottom)
            lineTo(c.x + a * 0.16f, r.bottom)
            cubicTo(r.right - a * 0.25f, r.bottom, r.right, r.top + al * 0.7f, r.right, r.top)
            close()
        }
        drawPath(f, t.c(LOZA))
        clipPath(f) {
            cuerpo(r, t, 1.2f)
            drawRect(t.c(if (tipo == 0) ROJO else ESMALTE), Offset(r.left, r.top + al * 0.18f), Size(a, al * 0.1f))
        }
        // La boca: el caldo ámbar con los fideos.
        val boca = Rect(r.left, r.top - a * 0.09f, r.right, r.top + a * 0.09f)
        drawOval(t.c(LOZA), boca.topLeft, boca.size)
        val caldo = Rect(boca.left + a * 0.05f, boca.top + a * 0.025f, boca.right - a * 0.05f, boca.bottom - a * 0.01f)
        drawOval(t.c(Color(0xFFB8782E)), caldo.topLeft, caldo.size)
        clipPath(Path().apply { addOval(caldo) }) {
            for (k in 0 until 6) drawArc(t.c(Color(0xFFEAD08A)), 200f, 140f, false, Offset(caldo.left + caldo.width * (0.05f + 0.13f * k), caldo.top + caldo.height * (0.15f + 0.1f * (k % 3))), Size(caldo.width * 0.3f, caldo.height * 0.5f), style = Stroke(1f * u))
            // La rodaja de huevo y la cebolleta.
            val h = Offset(caldo.left + caldo.width * (if (tipo == 0) 0.68f else 0.3f), caldo.center.y)
            drawOval(t.c(TICKET), Offset(h.x - caldo.width * 0.12f, h.y - caldo.height * 0.3f), Size(caldo.width * 0.24f, caldo.height * 0.55f))
            drawOval(t.c(MOSTAZA), Offset(h.x - caldo.width * 0.06f, h.y - caldo.height * 0.14f), Size(caldo.width * 0.12f, caldo.height * 0.28f))
            for (k in 0 until 5) drawCircle(t.c(Color(0xFF5CA862)), 1.3f * u, Offset(caldo.left + caldo.width * (0.2f + 0.12f * k), caldo.top + caldo.height * (0.3f + 0.4f * azar(3620 + tipo, k))))
        }
        drawArc(t.b(0.9f), 170f, 110f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
        // El vapor, que recoge el verde de los letreritos.
        for (k in 0 until 3) {
            val y = boca.top - a * (0.12f + 0.16f * k)
            val x = c.x + a * (0.1f * sin(k * 2.1f + tipo))
            val rr = a * (0.12f + 0.06f * k)
            drawOval(mezcla(LOZA, SODIO, 0.35f).copy(alpha = 0.13f - 0.03f * k), Offset(x - rr, y - rr * 0.5f), Size(rr * 2, rr))
        }
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (fracciones del ancho), para saber cuánto le llega del letrero.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH * 0.35f)
        val k = if (c.cols == cols) luzLetrero(centro) else 0.4f
        // El papel térmico y el acero de dentro devuelven más luz que la chapa.
        val t = Tono((k * 1.5f).coerceAtMost(1f))
        // Lo de abajo de la repisa toma algo del verde del propio letrerito.
        val tv = Tono(k, 0f, 0.22f)
        if (c.vacio) { vacio(w, h, c.semilla, t); return }
        val m = mezclar(c.semilla)
        when {
            c.sinTema -> cuenco(w, h, c, ab, Tono(k))
            else -> when (Math.floorMod(m, 3)) {
                0 -> tickets(w, h, c, m, ab, t)
                1 -> pincho(w, h, c, m, ab, t, tv)
                else -> cajita(w, h, c, m, ab, t, tv)
            }
        }
        letrerito(w, h, encendido = !c.sinTema)
    }

    /**
     * Un ticket térmico colgado de su bola en el riel: el papel con la curva del calor, el borde dentado, las líneas de la
     * impresora, el canto de luz a la izquierda y la sombra en la chapa.
     */
    private fun DrawScope.ticket(arriba: Offset, ancho: Float, largo: Float, curva: Float, semilla: Int, t: Tono) {
        val u = density
        val p = Path().apply {
            moveTo(arriba.x - ancho / 2, arriba.y)
            lineTo(arriba.x + ancho / 2, arriba.y)
            cubicTo(arriba.x + ancho / 2, arriba.y + largo * 0.5f, arriba.x + ancho / 2 + curva * 0.4f, arriba.y + largo * 0.8f, arriba.x + ancho / 2 + curva, arriba.y + largo)
            val dientes = 4
            for (j in 1..dientes * 2) {
                val f = j / (dientes * 2f)
                lineTo(arriba.x + ancho / 2 + curva - ancho * f, arriba.y + largo - (if (j % 2 == 1) 2.2f * u else 0f))
            }
            cubicTo(arriba.x - ancho / 2 + curva * 0.4f, arriba.y + largo * 0.8f, arriba.x - ancho / 2, arriba.y + largo * 0.5f, arriba.x - ancho / 2, arriba.y)
            close()
        }
        translate(ancho * 0.12f, ancho * 0.08f) { drawPath(p, t.s(0.4f)) }
        drawPath(p, t.c(TICKET))
        clipPath(p) {
            // El papel que se curva: la sombra del rizo abajo a la derecha.
            drawRect(t.s(0.18f), Offset(arriba.x + ancho * 0.15f + curva * 0.5f, arriba.y + largo * 0.6f), Size(ancho, largo * 0.4f))
            drawRect(t.b(0.45f), Offset(arriba.x - ancho / 2, arriba.y), Size(ancho * 0.12f, largo))
            val tinta = t.c(Color(0xFF3A3448))
            drawRect(tinta, Offset(arriba.x - ancho * 0.32f, arriba.y + largo * 0.08f), Size(ancho * 0.5f, 1.8f * u))
            var y = arriba.y + largo * 0.2f
            var j = 0
            while (y < arriba.y + largo * 0.82f) {
                val sesgo = curva * ((y - arriba.y) / largo).let { it * it }
                drawLine(tinta.copy(alpha = 0.7f), Offset(arriba.x - ancho * 0.34f + sesgo, y), Offset(arriba.x + ancho * (-0.2f + 0.48f * azar(semilla, j)) + sesgo, y), 0.9f * u)
                y += largo * 0.085f; j++
            }
            // El total, más grueso, y el reflejo magenta del letrero cuando está cerca.
            drawLine(tinta, Offset(arriba.x + ancho * 0.02f + curva * 0.7f, arriba.y + largo * 0.86f), Offset(arriba.x + ancho * 0.34f + curva * 0.7f, arriba.y + largo * 0.86f), 1.6f * u)
            drawRect(MAGENTA.copy(alpha = (0.5f * t.k).coerceAtMost(0.3f)), Offset(arriba.x - ancho / 2, arriba.y), Size(ancho * 0.05f, largo))
        }
    }

    /** La bola de acero que sujeta los tickets en el canal del riel. */
    private fun DrawScope.bola(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.5f), r, p + Offset(r * 0.4f, r * 0.5f))
        drawCircle(t.c(INOX), r, p)
        drawCircle(t.s(0.3f), r * 0.7f, p + Offset(r * 0.25f, r * 0.25f))
        drawCircle(t.b(0.95f), r * 0.32f, p - Offset(r * 0.35f, r * 0.35f))
    }

    /** El riel con los tickets colgando: más tickets cuantas más fichas, de largos distintos, curvados, encimados. */
    private fun DrawScope.tickets(w: Float, h: Float, c: EstadoCajon, m: Int, ab: Float, t: Tono) {
        val cuantos = c.n.coerceIn(1, 8)
        val yr = h * (RIEL + 0.035f)
        val ancho = w * 0.155f
        val margen = w * 0.09f
        for (i in 0 until cuantos) {
            val f = if (cuantos == 1) 0.4f else i / (cuantos - 1f)
            val x = margen + (w - 2 * margen) * (f * (0.55f + 0.06f * cuantos).coerceAtMost(1f)) + w * 0.03f * (azar(c.semilla, i + 40) - 0.5f) + (if (cuantos <= 3) w * 0.12f * azar(m, 1) else 0f)
            val largo = h * (0.36f + 0.16f * azar(c.semilla, i)) * (if (i % 3 == 2) 0.8f else 1f)
            val curva = w * 0.03f * (azar(c.semilla, i + 20) - 0.3f)
            val giro = (azar(c.semilla, i + 60) - 0.5f) * 5f + ab * 9f * sin(i * 1.7f + 0.5f)
            rotate(giro, Offset(x, yr)) { ticket(Offset(x, yr), ancho, largo, curva, c.semilla + i, t) }
            bola(Offset(x, yr - h * 0.005f), w * 0.016f, t)
        }
    }

    /** El pincho de mostrador con los tickets ensartados (la pila crece con las fichas) y, a la derecha, alguno pendiente en el riel. */
    private fun DrawScope.pincho(w: Float, h: Float, c: EstadoCajon, m: Int, ab: Float, t: Tono, tv: Tono) {
        val u = density
        val pie = h * (REPISA + 0.02f)
        val cx = w * (0.3f + 0.06f * azar(m, 2))
        val ensartados = c.n.coerceIn(1, 9)
        val sube = -h * 0.06f * ab
        // La base de fundición y su sombra.
        contacto(Offset(cx, pie), w * 0.22f, h * 0.04f)
        val base = Rect(cx - w * 0.085f, pie - h * 0.05f, cx + w * 0.085f, pie)
        drawRoundRect(tv.c(Color(0xFF2A2438)), base.topLeft, base.size, CornerRadius(base.height * 0.4f))
        drawOval(tv.c(Color(0xFF3A3448)), Offset(base.left, base.top - h * 0.015f), Size(base.width, h * 0.03f))
        drawArc(tv.b(0.8f), 180f, 120f, false, Offset(base.left, base.top - h * 0.015f), Size(base.width, h * 0.03f), style = Stroke(1f * u))
        // El pincho.
        val punta = base.top - h * (0.1f + 0.042f * ensartados) + sube
        drawLine(t.c(INOX), Offset(cx, base.top), Offset(cx, punta), 1.8f * u, StrokeCap.Round)
        drawLine(t.b(0.9f), Offset(cx - 0.6f * u, base.top), Offset(cx - 0.6f * u, punta), 0.6f * u)
        // Los tickets ensartados, de abajo arriba, cada uno girado y algunos doblados.
        for (i in 0 until ensartados) {
            val y = base.top - h * (0.04f + 0.042f * i) + sube * (i + 1) / ensartados
            val aw = w * (0.27f + 0.1f * azar(c.semilla, i))
            val ah = h * (0.05f + 0.02f * azar(c.semilla, i + 10))
            val giro = (azar(c.semilla, i + 20) - 0.5f) * 22f
            val dx = w * 0.04f * (azar(c.semilla, i + 30) - 0.5f)
            rotate(giro, Offset(cx, y)) {
                val r = Rect(cx - aw / 2 + dx, y - ah / 2, cx + aw / 2 + dx, y + ah / 2)
                drawRect(t.s(0.3f), Offset(r.left, r.bottom), Size(r.width, 1.4f * u))
                drawRect(t.c(if (i % 4 == 3) mezcla(TICKET, MOSTAZA, 0.18f) else TICKET), r.topLeft, r.size)
                drawRect(t.b(0.7f), r.topLeft, Size(r.width, 0.9f * u))
                drawRect(t.s(0.1f), Offset(r.right - aw * 0.18f, r.top), Size(aw * 0.18f, r.height))
                val tinta = t.c(Color(0xFF3A3448)).copy(alpha = 0.7f)
                drawLine(tinta, Offset(r.left + aw * 0.08f, r.top + ah * 0.35f), Offset(r.left + aw * 0.4f, r.top + ah * 0.35f), 1.2f * u)
                drawLine(tinta, Offset(r.left + aw * 0.55f, r.top + ah * 0.35f), Offset(r.left + aw * (0.62f + 0.25f * azar(c.semilla, i + 50)), r.top + ah * 0.35f), 0.8f * u)
                drawLine(tinta, Offset(r.left + aw * 0.08f, r.top + ah * 0.7f), Offset(r.left + aw * (0.3f + 0.3f * azar(c.semilla, i + 51)), r.top + ah * 0.7f), 0.8f * u)
                // La esquina doblada.
                if (azar(c.semilla, i + 40) > 0.5f) drawPath(poli(r.right - aw * 0.14f, r.top, r.right, r.top, r.right, r.top + ah * 0.6f), t.c(mezcla(TICKET, NOCHE, 0.3f)))
                drawCircle(t.s(0.6f), 1.2f * u, Offset(cx, y))
            }
        }
        // Lo pendiente, colgado del riel a la derecha.
        val pendientes = when { c.n >= 6 -> 3; c.n >= 3 -> 2; else -> 1 }
        val yr = h * (RIEL + 0.035f)
        for (i in 0 until pendientes) {
            val x = w * (0.66f + 0.11f * i) + w * 0.02f * (azar(c.semilla, i + 70) - 0.5f)
            val giro = (azar(c.semilla, i + 80) - 0.5f) * 6f + ab * 8f * sin(i * 2.1f)
            rotate(giro, Offset(x, yr)) { ticket(Offset(x, yr), w * 0.12f, h * (0.26f + 0.12f * azar(c.semilla, i + 90)), w * 0.02f, c.semilla + 50 + i, t) }
            bola(Offset(x, yr), w * 0.016f, t)
        }
    }

    /** La cajita de acero con los rollos de papel térmico (más rollos con más fichas), una tira que sale y un rollo tumbado al lado. */
    private fun DrawScope.cajita(w: Float, h: Float, c: EstadoCajon, m: Int, ab: Float, t: Tono, tv: Tono) {
        val u = density
        val pie = h * (REPISA + 0.02f)
        val caja = Rect(w * (0.1f + 0.04f * azar(m, 3)), pie - h * 0.2f, w * (0.6f + 0.04f * azar(m, 3)), pie)
        contacto(Offset(caja.center.x + w * 0.02f, pie), caja.width * 1.05f, h * 0.04f)
        // La pared de atrás de la caja, por dentro.
        drawRect(tv.c(mezcla(INOX, NOCHE, 0.55f)), Offset(caja.left + w * 0.02f, caja.top - h * 0.04f), Size(caja.width - w * 0.04f, h * 0.06f))
        // Los rollos: se ven sus caras de arriba, con el agujero, y suben al abrir.
        val rollos = (1 + c.n / 2).coerceIn(1, 4)
        val sube = -h * 0.07f * ab
        for (i in 0 until rollos) {
            val cx = caja.left + caja.width * (0.18f + 0.64f * (if (rollos == 1) 0.5f else i / (rollos - 1f)))
            val ry = h * 0.035f
            val rx = caja.width * 0.12f
            val top = caja.top - h * (0.08f + 0.07f * azar(c.semilla, i)) + sube
            drawRect(t.c(TICKET), Offset(cx - rx, top), Size(rx * 2, caja.top - top + h * 0.02f))
            drawRect(t.s(0.25f), Offset(cx + rx * 0.3f, top), Size(rx * 0.7f, caja.top - top + h * 0.02f))
            drawRect(t.b(0.5f), Offset(cx - rx, top), Size(rx * 0.25f, caja.top - top + h * 0.02f))
            drawOval(t.c(mezcla(TICKET, LOZA, 0.5f)), Offset(cx - rx, top - ry), Size(rx * 2, ry * 2))
            drawOval(t.c(mezcla(TICKET, CHAPA, 0.3f)), Offset(cx - rx, top - ry), Size(rx * 2, ry * 2), style = Stroke(0.6f * u))
            drawOval(t.c(Color(0xFF3A3448)), Offset(cx - rx * 0.28f, top - ry * 0.28f), Size(rx * 0.56f, ry * 0.56f))
        }
        // La caja: el frente de acero con remaches, una abolladura, el portaetiquetas en blanco y los cantos.
        drawRect(tv.c(INOX), caja.topLeft, caja.size)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) { cuerpo(caja, tv, 1.1f) }
        drawRect(tv.b(0.9f), caja.topLeft, Size(caja.width, 1.2f * u))
        drawRect(tv.s(0.5f), Offset(caja.left, caja.bottom - 1.6f * u), Size(caja.width, 1.6f * u))
        listOf(Offset(caja.left + 3f * u, caja.top + 4f * u), Offset(caja.right - 3f * u, caja.top + 4f * u), Offset(caja.left + 3f * u, caja.bottom - 4f * u), Offset(caja.right - 3f * u, caja.bottom - 4f * u)).forEach { remache(it, 1.2f * u, tv) }
        val porta = Rect(caja.center.x - caja.width * 0.2f, caja.top + caja.height * 0.35f, caja.center.x + caja.width * 0.2f, caja.top + caja.height * 0.65f)
        drawRect(tv.s(0.5f), porta.topLeft, porta.size)
        drawRect(tv.c(mezcla(TICKET, NOCHE, 0.2f)), porta.topLeft + Offset(1.5f * u, 1.5f * u), Size(porta.width - 3f * u, porta.height - 3f * u))
        drawArc(tv.s(0.5f), 200f, 120f, false, Offset(caja.left + caja.width * 0.7f, caja.top + caja.height * 0.55f), Size(caja.width * 0.15f, caja.height * 0.2f), style = Stroke(1f * u))
        // La tira que sale del primer rollo, pasa por encima del canto y cae rizándose por delante.
        val x0 = caja.left + caja.width * 0.18f
        val largo = h * (0.12f + 0.025f * c.n.coerceIn(0, 8)) - sube
        val tira = Path().apply {
            moveTo(x0 - w * 0.03f, caja.top - h * 0.02f)
            cubicTo(x0 - w * 0.03f, caja.top - h * 0.06f, x0 - w * 0.08f, caja.top - h * 0.03f, x0 - w * 0.08f, caja.top + h * 0.02f)
            cubicTo(x0 - w * 0.09f, caja.top + largo * 0.5f, x0 - w * 0.03f, caja.top + largo * 0.8f, x0 - w * 0.06f, caja.top + largo)
            lineTo(x0, caja.top + largo - h * 0.01f)
            cubicTo(x0 + w * 0.02f, caja.top + largo * 0.8f, x0 - w * 0.03f, caja.top + largo * 0.5f, x0 - w * 0.02f, caja.top + h * 0.02f)
            cubicTo(x0 - w * 0.02f, caja.top - h * 0.01f, x0 + w * 0.01f, caja.top - h * 0.02f, x0 + w * 0.02f, caja.top - h * 0.02f)
            close()
        }
        translate(w * 0.012f, h * 0.012f) { drawPath(tira, t.s(0.35f)) }
        drawPath(tira, t.c(TICKET))
        clipPath(tira) {
            for (j in 0 until 5) drawLine(t.c(Color(0xFF3A3448)).copy(alpha = 0.6f), Offset(x0 - w * 0.08f, caja.top + largo * (0.2f + 0.14f * j)), Offset(x0 - w * 0.04f, caja.top + largo * (0.22f + 0.14f * j)), 0.8f * u)
            drawRect(t.b(0.5f), Offset(x0 - w * 0.1f, caja.top), Size(w * 0.025f, largo))
        }
        // El rollo tumbado al lado, a la derecha.
        val rc = Offset(w * 0.78f, pie - h * 0.065f)
        val rr = h * 0.065f
        contacto(Offset(rc.x, pie), w * 0.2f, h * 0.03f)
        drawRect(tv.c(TICKET), Offset(rc.x - w * 0.08f, rc.y - rr), Size(w * 0.16f, rr * 2))
        drawRect(tv.s(0.3f), Offset(rc.x - w * 0.08f, rc.y + rr * 0.3f), Size(w * 0.16f, rr * 0.7f))
        drawRect(tv.b(0.6f), Offset(rc.x - w * 0.08f, rc.y - rr), Size(w * 0.16f, 1f * u))
        drawOval(tv.c(mezcla(TICKET, LOZA, 0.5f)), Offset(rc.x + w * 0.08f - rr * 0.45f, rc.y - rr), Size(rr * 0.9f, rr * 2))
        drawOval(tv.c(Color(0xFF3A3448)), Offset(rc.x + w * 0.08f - rr * 0.15f, rc.y - rr * 0.3f), Size(rr * 0.3f, rr * 0.6f))
    }

    /** Lo que no tiene tema: un cuenco de acero con tickets arrugados, más cuantas más fichas, alguno caído al lado. */
    private fun DrawScope.cuenco(w: Float, h: Float, c: EstadoCajon, ab: Float, t: Tono) {
        val u = density
        val pie = h * (REPISA + 0.02f)
        val cx = w * 0.42f
        val a = w * 0.42f; val al = h * 0.17f
        contacto(Offset(cx + w * 0.02f, pie), a * 1.05f, h * 0.04f)
        val bolas = (3 + c.n).coerceIn(4, 11)
        val sube = -h * 0.05f * ab
        // Las bolas de papel que asoman sobre la boca.
        for (i in 0 until bolas) {
            val bx = cx + a * 0.38f * (azar(c.semilla, i) * 2f - 1f)
            val by = pie - al - h * (0.01f + 0.06f * azar(c.semilla, i + 10)) * (1f + i / 6f) + sube
            val br = h * (0.04f + 0.025f * azar(c.semilla, i + 20))
            val bola = Path().apply {
                for (j in 0 until 7) {
                    val ang = j / 7f * 6.283f
                    val rr = br * (0.75f + 0.35f * azar(c.semilla + i, j))
                    val p = Offset(bx + rr * cos(ang), by + rr * sin(ang))
                    if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(bola, t.c(if (i % 3 == 1) mezcla(TICKET, MOSTAZA, 0.2f) else TICKET))
            drawPath(bola, t.s(0.3f), style = Stroke(0.7f * u))
            drawLine(t.s(0.3f), Offset(bx - br * 0.3f, by), Offset(bx + br * 0.4f, by + br * 0.3f), 0.6f * u)
            drawCircle(t.b(0.5f), br * 0.3f, Offset(bx - br * 0.35f, by - br * 0.35f))
        }
        // El cuenco de acero.
        val r = Rect(cx - a / 2, pie - al, cx + a / 2, pie)
        val f = Path().apply {
            moveTo(r.left, r.top)
            cubicTo(r.left, r.top + al * 0.8f, r.left + a * 0.2f, r.bottom, cx - a * 0.2f, r.bottom)
            lineTo(cx + a * 0.2f, r.bottom)
            cubicTo(r.right - a * 0.2f, r.bottom, r.right, r.top + al * 0.8f, r.right, r.top)
            close()
        }
        drawPath(f, t.c(INOX))
        clipPath(f) {
            cuerpo(r, t, 1.4f)
            drawRect(t.b(0.6f), Offset(r.left + a * 0.15f, r.top), Size(a * 0.04f, al))
        }
        drawRoundRect(t.c(mezcla(INOX, LOZA, 0.2f)), Offset(r.left - a * 0.02f, r.top - h * 0.012f), Size(a * 1.04f, h * 0.024f), CornerRadius(h * 0.012f))
        drawRect(t.b(0.8f), Offset(r.left, r.top - h * 0.012f), Size(a, 0.9f * u))
        // Uno caído en la repisa, a la derecha.
        drawCircle(t.s(0.4f), h * 0.04f, Offset(w * 0.8f + 2f * u, pie - h * 0.03f + 2f * u))
        drawCircle(t.c(TICKET), h * 0.04f, Offset(w * 0.8f, pie - h * 0.035f))
        drawLine(t.s(0.3f), Offset(w * 0.79f, pie - h * 0.05f), Offset(w * 0.815f, pie - h * 0.025f), 0.6f * u)
    }

    /**
     * El letrerito del tema bajo el canto de la repisa: la placa negra atornillada con sus dos pletinas y el tubo de neón verde
     * alrededor, con su halo (luz propia: igual de encendido cerca y lejos). Apagado, el tubo es de vidrio gris y sin halo.
     */
    private fun DrawScope.letrerito(w: Float, h: Float, encendido: Boolean) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val t = Tono(0f, 0f, if (encendido) 0.8f else 0f)
        // Las pletinas que la cuelgan del labio.
        listOf(r.left + r.width * 0.12f, r.right - r.width * 0.12f).forEach { x ->
            drawRect(t.c(INOX), Offset(x - 2f * u, h * LABIO - 2f * u), Size(4f * u, r.top - h * LABIO + 5f * u))
            drawRect(t.b(0.5f), Offset(x - 2f * u, h * LABIO - 2f * u), Size(1f * u, r.top - h * LABIO + 5f * u))
        }
        if (encendido) {
            drawRoundRect(SODIO.copy(alpha = 0.08f), Offset(r.left - 5f * u, r.top - 4f * u), Size(r.width + 10f * u, r.height + 8f * u), CornerRadius(8f * u))
            drawRoundRect(SODIO.copy(alpha = 0.1f), Offset(r.left - 2.5f * u, r.top - 2f * u), Size(r.width + 5f * u, r.height + 4f * u), CornerRadius(6f * u))
            // El verde que cae sobre el labio de la repisa.
            drawRect(SODIO.copy(alpha = 0.16f), Offset(r.left, h * CANTO), Size(r.width, h * (LABIO - CANTO)))
        }
        drawRoundRect(PLACA, r.topLeft, r.size, CornerRadius(4f * u))
        drawRoundRect(Color(0xFF2A2438), r.topLeft, r.size, CornerRadius(4f * u), style = Stroke(1f * u))
        val marco = Path().apply { addRoundRect(RoundRect(Rect(r.left + 3.5f * u, r.top + 3.5f * u, r.right - 3.5f * u, r.bottom - 3.5f * u), CornerRadius(3f * u))) }
        if (encendido) tubo(marco, SODIO, 1.3f * u) else tuboApagado(marco, 1.3f * u, Tono(0.15f))
        // Los cuatro tornillos de la placa.
        listOf(Offset(r.left + 1.8f * u, r.top + 1.8f * u), Offset(r.right - 1.8f * u, r.top + 1.8f * u), Offset(r.left + 1.8f * u, r.bottom - 1.8f * u), Offset(r.right - 1.8f * u, r.bottom - 1.8f * u))
            .forEach { drawCircle(Color(0xFF4A4458), 1.1f * u, it) }
    }

    /** Un hueco sin tema: el riel vacío con una pinza suelta, o una calcomanía medio arrancada en la chapa. */
    private fun DrawScope.vacio(w: Float, h: Float, semilla: Int, t: Tono) {
        val u = density
        if (Math.floorMod(semilla / 7919, 2) == 0) {
            // La pinza de muelle, colgando torcida de una sola patilla.
            val p = Offset(w * 0.62f, h * (RIEL + 0.035f))
            rotate(24f, p) {
                drawRect(t.s(0.45f), Offset(p.x - w * 0.02f + 2f * u, p.y + 2f * u), Size(w * 0.04f, h * 0.12f))
                drawRect(t.c(INOX), Offset(p.x - w * 0.02f, p.y), Size(w * 0.04f, h * 0.12f))
                drawRect(t.b(0.7f), Offset(p.x - w * 0.02f, p.y), Size(1f * u, h * 0.12f))
                drawCircle(t.c(Color(0xFF3A3448)), w * 0.012f, Offset(p.x, p.y + h * 0.04f), style = Stroke(1.2f * u))
                drawLine(t.s(0.5f), Offset(p.x - w * 0.02f, p.y + h * 0.08f), Offset(p.x + w * 0.02f, p.y + h * 0.08f), 1f * u)
            }
            bola(Offset(w * 0.25f, h * (RIEL + 0.03f)), w * 0.016f, t)
            // Un trozo de ticket roto que quedó en el riel.
            drawPath(poli(w * 0.3f, h * (RIEL + 0.04f), w * 0.36f, h * (RIEL + 0.04f), w * 0.35f, h * 0.18f, w * 0.33f, h * 0.15f, w * 0.31f, h * 0.19f), t.c(TICKET))
        } else {
            // La calcomanía medio arrancada: el resto de color, el papel blanco del reverso y las tiras.
            val r = Rect(w * 0.3f, h * 0.2f, w * 0.7f, h * 0.48f)
            drawPath(poli(r.left, r.top, r.right, r.top, r.right, r.top + r.height * 0.35f, r.left + r.width * 0.55f, r.bottom, r.left, r.bottom), t.c(mezcla(MAGENTA, CHAPA, 0.45f)))
            drawPath(poli(r.right, r.top + r.height * 0.35f, r.right, r.bottom, r.left + r.width * 0.55f, r.bottom), t.c(mezcla(TICKET, CHAPA, 0.4f)))
            for (j in 0 until 4) drawLine(t.c(TICKET).copy(alpha = 0.6f), Offset(r.left + r.width * (0.6f + 0.1f * j), r.bottom - r.height * 0.1f * j), Offset(r.left + r.width * (0.7f + 0.1f * j), r.bottom - r.height * (0.05f + 0.1f * j)), 0.8f * u)
            drawCircle(t.c(NOCHE), r.height * 0.18f, Offset(r.left + r.width * 0.3f, r.top + r.height * 0.4f), style = Stroke(1.6f * u))
            drawPath(poli(r.right, r.top + r.height * 0.35f, r.right + r.width * 0.08f, r.top + r.height * 0.55f, r.left + r.width * 0.62f, r.bottom + r.height * 0.05f, r.left + r.width * 0.55f, r.bottom), t.c(mezcla(MAGENTA, CHAPA, 0.3f)))
        }
    }

    override val rotulo = Rect(0.04f, 0.68f, 0.96f, 0.97f)
    override fun tintaRotulo(oscuro: Boolean) = LETRA
    override val letraRotulo: FontFamily = Letra.letrero
    override val salida = 0f
    override val placa = Rect(0.3f, 0.06f, 0.7f, 0.27f)
    override fun tintaPlaca(oscuro: Boolean) = LETRA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La plancha de acero cepillado, con reflejos de neón y vapor.
        drawRect(ACERO)
        var y = 0f
        while (y < h) { drawLine(ACERO_LUZ.copy(alpha = 0.12f), Offset(0f, y), Offset(w, y + 1 * u), 0.8f * u); y += 4 * u }
        mancha(Offset(w * 0.2f, h * 0.3f), w * 0.6f, MAGENTA.copy(alpha = 0.16f))
        mancha(Offset(w * 0.85f, h * 0.7f), w * 0.5f, CIAN.copy(alpha = 0.12f))
        for (k in 0 until 10) mancha(Offset(w * azar(61, k), h * azar(62, k)), (30f + 40f * azar(63, k)) * u, Color.White.copy(alpha = 0.05f))
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(ACERO_OSC, r.topLeft, r.size) }
        drawRect(MAGENTA.copy(alpha = 0.5f), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.2f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(dentado(s.width, s.height, 14, minOf(s.width, s.height) * 0.03f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El canto magenta, la línea de puntos del corte y un código de barras tenue.
        drawRect(MAGENTA.copy(alpha = 0.7f), Offset.Zero, Size(2.5f * u * g, h))
        var x = 6 * u
        while (x < w) { drawCircle(TEXTO.copy(alpha = 0.25f), 0.8f * u * g, Offset(x, 3 * u * g)); x += 4 * u * g }
        var bx = w - 30 * u * g; var k = 0
        while (bx < w - 6 * u * g) {
            val bw = (0.8f + 1.6f * azar(semilla, k)) * u * g
            drawRect(TEXTO.copy(alpha = 0.16f), Offset(bx, h - 18 * u * g), Size(bw, 9 * u * g))
            bx += bw + 1.2f * u * g; k++
        }
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 16.dp, 24.dp, 26.dp, 34.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(AMBAR, p.color)
            "convence" -> { drawPath(corazon(c, s * 0.9f), MAGENTA.copy(alpha = 0.3f), style = Stroke(s * 0.2f)); drawPath(corazon(c, s * 0.9f), MAGENTA, style = Stroke(s * 0.08f)) }
            // Unos palillos.
            "piensa" -> {
                drawLine(AMBAR, Offset(c.x - s * 0.3f, c.y + s * 0.4f), Offset(c.x + s * 0.06f, c.y - s * 0.42f), s * 0.08f, StrokeCap.Round)
                drawLine(AMBAR, Offset(c.x - s * 0.1f, c.y + s * 0.42f), Offset(c.x + s * 0.3f, c.y - s * 0.38f), s * 0.08f, StrokeCap.Round)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(MAGENTA)
            else -> {}
        }
    }

    /** Una pinza de comanda. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.38f, c.y + s * 0.3f); lineTo(c.x - s * 0.3f, c.y - s * 0.1f); lineTo(c.x + s * 0.3f, c.y - s * 0.1f); lineTo(c.x + s * 0.38f, c.y + s * 0.3f); close() }, ACERO_LUZ)
        drawArc(TEXTO, 180f, 180f, false, Offset(c.x - s * 0.26f, c.y - s * 0.46f), Size(s * 0.24f, s * 0.7f), style = Stroke(s * 0.06f))
        drawArc(TEXTO, 180f, 180f, false, Offset(c.x + s * 0.02f, c.y - s * 0.46f), Size(s * 0.24f, s * 0.7f), style = Stroke(s * 0.06f))
    }

    override val desorden = 2.5f
}
