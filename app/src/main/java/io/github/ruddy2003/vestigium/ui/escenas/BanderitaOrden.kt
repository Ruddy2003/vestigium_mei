package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Orden: el pendón de la Orden del Candil. Un paño de lana carmesí de esquinas cortadas (la «compuerta de esquinas
 * cortadas» del tema), con su caída en pliegues que bajan de las presillas, colgado por cuatro presillas de una vara de
 * hierro de la fragua con remates de latón. En medio, bordado en hilo de latón, **el candil, que es el signo de la orden**
 * («el signo de la orden y también su lámpara: un vaso de latón con aceite de cabra y una mecha», dice el cuento de la
 * escriba): el vaso con su pie, la mecha y la llama, como el que lleva el pendón de la torre en el cuadro. Un
 * filete de latón por dentro del canto, el fleco de latón abajo y un zurcido viejo en un costado. Sin letras ni lemas (ni
 * engranes, espadas ni alas: nada del emblema de la obra en que se inspira la vida). Al mantener el dedo sube desde abajo
 * la luz cálida del candil, con su borde de lenguas de llama, y lo bordado que queda debajo pasa a carmesí hondo para que
 * se siga viendo.
 * **[Diseño propio]**: el tema de pixel no tiene banderita; los colores son los del tema y del cuadro (`ColorOrden`:
 * carmesí #9E1B22, latón #C9A04A, el latón oscuro del tema #7E6219, hierro #2F3139, lumbre #FFB45A).
 */
internal object BanderitaOrden : Banderita {
    private val CARMESI = Color(0xFF9E1B22)
    private val CARMESI_LUZ = Color(0xFFC0303A)
    private val CARMESI_OSC = Color(0xFF601A1F)
    private val CARMESI_HONDO = Color(0xFF3E0E12)
    private val LATON = Color(0xFFC9A04A)
    private val LATON_LUZ = Color(0xFFF2D27A)
    private val LATON_OSC = Color(0xFF7E6219)
    private val HIERRO = Color(0xFF2F3139)
    private val HIERRO_LUZ = Color(0xFF6E7480)
    private val HIERRO_OSC = Color(0xFF17181D)
    private val LUMBRE = Color(0xFFFFB45A)
    private val LUMBRE_LUZ = Color(0xFFFFE7A8)
    private val LUMBRE_HONDA = Color(0xFFE07A28)
    private val LLAMA_NUCLEO = Color(0xFFFFF4D6)

    /** El paño: sus orillas, dónde empiezan las esquinas cortadas y el borde de abajo. */
    private const val IZQ = 3.4f
    private const val DER = 22.6f
    private const val ARRIBA = 5.2f
    private const val HOMBRO = 31.4f
    private const val ABAJO = 35.4f
    private const val CORTE = 3.8f
    private const val MEDIO = 13f
    /** Las cuatro presillas de las que cuelga, en la vara. */
    private val PRESILLAS = listOf(5.6f, 10.6f, 15.4f, 20.4f)
    /** El candil bordado: el pie del vaso, la boca del vaso y la mecha (de donde sale la llama). */
    private const val PIE_Y = 27.4f
    private const val BOCA_Y = 20.2f
    private val MECHA = Offset(13.3f, 19.2f)

    /** Los colores de lo bordado: los suyos, o los de tinta (carmesí hondo) donde ya subió la luz. */
    private class Hilos(val laton: Color, val latonLuz: Color, val latonOsc: Color, val llama: Color, val llamaLuz: Color, val nucleo: Color, val punto: Color)

    private val PROPIOS = Hilos(LATON, LATON_LUZ, LATON_OSC, Color(0xFFE8A93A), LUMBRE_LUZ, LLAMA_NUCLEO, CARMESI_HONDO.copy(alpha = 0.35f))
    private val DE_TINTA = Hilos(CARMESI_OSC, CARMESI, CARMESI_HONDO, CARMESI, CARMESI_LUZ, CARMESI_OSC, CARMESI_HONDO.copy(alpha = 0.25f))

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val l = lleno.coerceIn(0f, 1f)
            val nivel = ABAJO + 0.6f - (ABAJO - ARRIBA + 1.4f) * l
            val pano = cuerpo()
            drawPath(Path().apply { addPath(pano, Offset(0.9f, 1.3f)) }, Color.Black.copy(alpha = 0.3f))
            fleco(l)
            tela(pano, l, nivel)
            clipPath(pano) {
                if (l <= 0f) bordado(PROPIOS)
                else {
                    val borde = lenguas(nivel)
                    clipPath(borde, ClipOp.Difference) { bordado(PROPIOS) }
                    clipPath(borde) { bordado(DE_TINTA) }
                }
            }
            drawPath(pano, CARMESI_HONDO.copy(alpha = 0.55f), style = Stroke(0.45f))
            vara()
            for (x in PRESILLAS) presilla(x)
        }
    }

    /**
     * El paño con su caída: el canto de arriba se combra un poco entre presilla y presilla; la orilla de la derecha ondula,
     * y el borde de abajo, entre las dos esquinas cortadas, sube y baja con los pliegues.
     */
    private fun cuerpo(): Path = Path().apply {
        moveTo(IZQ, ARRIBA)
        var x0 = IZQ
        for (x in PRESILLAS + DER) { val m = (x0 + x) / 2f; cubicTo(m - 0.8f, ARRIBA + 0.75f, m + 0.8f, ARRIBA + 0.75f, x, ARRIBA); x0 = x }
        cubicTo(DER + 0.5f, 13f, DER - 0.4f, 22f, DER + 0.25f, HOMBRO)
        lineTo(DER - CORTE + 0.2f, ABAJO - 0.1f)
        cubicTo(17.6f, ABAJO + 0.5f, 15.8f, ABAJO - 0.5f, MEDIO + 0.4f, ABAJO + 0.2f)
        cubicTo(11f, ABAJO + 0.6f, 9.4f, ABAJO - 0.4f, IZQ + CORTE, ABAJO)
        lineTo(IZQ - 0.15f, HOMBRO)
        cubicTo(IZQ + 0.25f, 22f, IZQ - 0.25f, 13f, IZQ, ARRIBA)
        close()
    }

    /** La lana: el carmesí con su luz a la izquierda, los pliegues que bajan de las presillas, el zurcido y la luz que sube. */
    private fun DrawScope.tela(pano: Path, l: Float, nivel: Float) {
        drawPath(pano, Brush.horizontalGradient(listOf(CARMESI_LUZ, CARMESI, CARMESI, CARMESI_OSC), IZQ, DER))
        clipPath(pano) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, CARMESI_HONDO.copy(alpha = 0.3f)), 20f, ABAJO + 1f), Offset(0f, 20f), Size(26f, 20f))
            // Los pliegues: bajan de cada presilla, anchos, con su lado de luz y su lado de sombra.
            for ((x, a) in listOf(5.9f to 0.16f, 10.9f to 0.1f, 15.7f to 0.11f)) drawLine(Color.White.copy(alpha = a), Offset(x, ARRIBA), Offset(x - 0.5f, ABAJO + 1f), 1.5f)
            for ((x, a) in listOf(8.4f to 0.2f, 13.2f to 0.16f, 18.1f to 0.22f, 21.4f to 0.18f)) drawLine(CARMESI_HONDO.copy(alpha = a), Offset(x, ARRIBA + 0.6f), Offset(x + 0.6f, ABAJO + 1f), 1.25f)
            // Entre presilla y presilla, la tela que se combra deja una uve de sombra.
            for (i in 0 until PRESILLAS.size - 1) {
                val m = (PRESILLAS[i] + PRESILLAS[i + 1]) / 2f
                drawPath(Path().apply { moveTo(PRESILLAS[i] + 0.9f, ARRIBA + 0.3f); cubicTo(m - 0.6f, ARRIBA + 1.4f, m - 0.2f, ARRIBA + 2.2f, m, ARRIBA + 3f); cubicTo(m + 0.2f, ARRIBA + 2.2f, m + 0.6f, ARRIBA + 1.4f, PRESILLAS[i + 1] - 0.9f, ARRIBA + 0.3f) }, CARMESI_HONDO.copy(alpha = 0.22f), style = Stroke(0.5f, cap = StrokeCap.Round))
            }
            zurcido()
            // La trama de la lana: rayitas finas en diagonal.
            var y = ARRIBA - 4f
            while (y < ABAJO) { drawLine(CARMESI_HONDO.copy(alpha = 0.07f), Offset(IZQ - 1f, y), Offset(DER + 1f, y + 3.4f), 0.22f); y += 1.1f }
            if (l > 0f) luz(nivel)
        }
    }

    /** Un zurcido viejo abajo a la derecha: un parche de lana más clara con sus puntadas cruzadas. */
    private fun DrawScope.zurcido() {
        val o = Offset(17.2f, 27.2f)
        drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + 2.9f, o.y - 0.3f); lineTo(o.x + 3.1f, o.y + 2.5f); lineTo(o.x + 0.2f, o.y + 2.7f); close() }, Color(0xFFAE3036).copy(alpha = 0.75f))
        for (i in 0..4) drawLine(CARMESI_HONDO.copy(alpha = 0.4f), o + Offset(0.3f + i * 0.62f, -0.4f), o + Offset(0.45f + i * 0.62f, 3f), 0.16f)
        for (i in 0..3) drawLine(CARMESI_HONDO.copy(alpha = 0.32f), o + Offset(-0.3f, 0.35f + i * 0.66f), o + Offset(3.4f, 0.1f + i * 0.66f), 0.14f)
    }

    /** El borde de la luz que sube: lenguas de llama bajas, que se mueven con lo que se ha llenado. */
    private fun lenguas(nivel: Float): Path = Path().apply {
        moveTo(0f, 40f); lineTo(0f, nivel)
        var x = 0f; var i = 0
        while (x < 26f) {
            val alto = if (i % 2 == 0) 1.6f else 0.9f
            val f = (nivel * 0.7f + i * 1.3f)
            cubicTo(x + 0.6f, nivel - alto * 0.3f, x + 1f + 0.3f * sin(f), nivel - alto, x + 1.3f, nivel - alto)
            cubicTo(x + 1.6f, nivel - alto * 0.4f, x + 2.2f, nivel, x + 2.6f, nivel)
            x += 2.6f; i++
        }
        lineTo(26f, 40f); close()
    }

    /** La luz cálida del candil, que sube con su borde de lenguas y un filo claro. */
    private fun DrawScope.luz(nivel: Float) {
        val forma = lenguas(nivel)
        drawPath(forma, Brush.verticalGradient(listOf(LUMBRE_LUZ, LUMBRE, LUMBRE_HONDA), nivel - 1.6f, ABAJO + 0.6f))
        drawPath(forma, Color.White.copy(alpha = 0.7f), style = Stroke(0.35f))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, LUMBRE.copy(alpha = 0.35f)), nivel - 4f, nivel - 1f), Offset(0f, nivel - 4f), Size(26f, 3f))
    }

    /** Lo bordado: el filete de latón por dentro del canto y el candil. */
    private fun DrawScope.bordado(h: Hilos) {
        val filete = Path().apply {
            moveTo(IZQ + 1.3f, ARRIBA + 1.9f); lineTo(DER - 1.3f, ARRIBA + 1.9f); lineTo(DER - 1.15f, HOMBRO - 0.4f)
            lineTo(DER - CORTE + 0.5f, ABAJO - 1.5f); lineTo(IZQ + CORTE - 0.5f, ABAJO - 1.5f); lineTo(IZQ + 1.15f, HOMBRO - 0.4f); close()
        }
        drawPath(filete, h.latonOsc, style = Stroke(0.75f))
        drawPath(filete, h.laton, style = Stroke(0.42f))
        candil(h)
    }

    /** El vaso del candil, con su boca en [BOCA_Y]: se abre hacia arriba. */
    private fun vaso(): Path = Path().apply {
        moveTo(8.9f, BOCA_Y); lineTo(17.7f, BOCA_Y)
        cubicTo(17.5f, 22.6f, 16.2f, 24.4f, 14.6f, 24.9f)
        lineTo(11.8f, 24.9f)
        cubicTo(10.2f, 24.4f, 9.1f, 22.6f, 8.9f, BOCA_Y)
        close()
    }

    /**
     * El candil bordado en hilo de latón: el pie y el nudo del cuello, el vaso con su punto de raso (rayitas en diagonal),
     * su luz a la izquierda y su borde, la boca con el aceite, la mecha y la llama con su núcleo claro, y unas puntadas de
     * luz alrededor.
     */
    private fun DrawScope.candil(h: Hilos) {
        val sombra = Offset(0.3f, 0.4f)
        // El pie y el cuello.
        val pie = Path().apply { moveTo(10.2f, PIE_Y); lineTo(16.4f, PIE_Y); cubicTo(16f, PIE_Y - 1.1f, 14.6f, PIE_Y - 1.3f, 14.1f, PIE_Y - 2.1f); lineTo(12.5f, PIE_Y - 2.1f); cubicTo(12f, PIE_Y - 1.3f, 10.6f, PIE_Y - 1.1f, 10.2f, PIE_Y); close() }
        drawPath(Path().apply { addPath(pie, sombra) }, h.punto)
        drawPath(pie, Brush.horizontalGradient(listOf(h.latonLuz, h.laton, h.latonOsc), 10.2f, 16.4f))
        drawPath(pie, h.latonOsc, style = Stroke(0.28f))
        // El nudo del cuello, entre el pie y el vaso.
        drawOval(h.latonOsc, Offset(11.9f, PIE_Y - 3.1f), Size(2.8f, 1.3f))
        drawOval(h.laton, Offset(12f, PIE_Y - 3.15f), Size(2.4f, 1f))
        // El vaso (sin asa: con ella se leía como una taza).
        val v = vaso()
        drawPath(Path().apply { addPath(v, sombra) }, h.punto)
        drawPath(v, Brush.horizontalGradient(listOf(h.latonLuz, h.laton, h.laton, h.latonOsc), 8.9f, 17.7f))
        clipPath(v) {
            var x = 6f
            while (x < 20f) { drawLine(h.latonOsc.copy(alpha = 0.3f), Offset(x, BOCA_Y - 0.5f), Offset(x + 2.2f, 25.4f), 0.18f); x += 0.75f }
            drawLine(h.latonLuz.copy(alpha = 0.8f), Offset(10.3f, BOCA_Y + 0.9f), Offset(11.4f, 24f), 0.45f, StrokeCap.Round)
            // Una franja de cordoncillo a media altura del vaso.
            drawLine(h.latonOsc, Offset(8f, 22.4f), Offset(19f, 22.4f), 0.35f)
        }
        drawPath(v, h.latonOsc, style = Stroke(0.3f))
        // La boca: el aceite en sombra y el labio de latón.
        drawOval(h.latonOsc, Offset(8.8f, BOCA_Y - 0.7f), Size(9f, 1.5f))
        drawOval(h.latonLuz, Offset(8.8f, BOCA_Y - 0.7f), Size(9f, 1.5f), style = Stroke(0.35f))
        // La mecha y la llama.
        drawLine(h.latonOsc, MECHA + Offset(-0.2f, 1.1f), MECHA, 0.45f, StrokeCap.Round)
        llama(h)
    }

    /** La llama: lengua alta, el núcleo claro y unas puntadas cortas de luz alrededor (sin rayos: no es un sol). */
    private fun DrawScope.llama(h: Hilos) {
        val b = MECHA
        fun lengua(ancho: Float, alto: Float, vaiven: Float) = Path().apply {
            moveTo(b.x, b.y + 0.2f)
            cubicTo(b.x - ancho, b.y - alto * 0.12f, b.x - ancho * 0.9f, b.y - alto * 0.55f, b.x + vaiven, b.y - alto)
            cubicTo(b.x + ancho * 0.85f, b.y - alto * 0.55f, b.x + ancho, b.y - alto * 0.12f, b.x, b.y + 0.2f)
            close()
        }
        drawPath(Path().apply { addPath(lengua(2.6f, 9.4f, -0.6f), Offset(0.3f, 0.4f)) }, h.punto)
        drawPath(lengua(2.6f, 9.4f, -0.6f), h.llama)
        drawPath(lengua(2.6f, 9.4f, -0.6f), h.latonOsc, style = Stroke(0.28f))
        drawPath(lengua(1.65f, 6.6f, -0.35f), h.llamaLuz)
        drawPath(lengua(0.8f, 3.6f, -0.1f), h.nucleo)
        // Las puntadas de luz: seis trazos cortos en corona, lejos de la llama.
        val c = b + Offset(-0.2f, -4.4f)
        for (i in 0 until 6) {
            val a = (-PI / 2 + (i - 2.5) * 0.55).toFloat()
            val u = Offset(cos(a), sin(a))
            drawLine(h.laton.copy(alpha = 0.85f), c + u * 5.6f, c + u * 6.6f, 0.4f, StrokeCap.Round)
        }
    }

    /** El fleco de latón del borde de abajo, entre las dos esquinas; cuando sube la luz, se enciende. */
    private fun DrawScope.fleco(l: Float) {
        val n = 13
        for (j in 0..n) {
            val x = IZQ + CORTE + 0.2f + (DER - IZQ - 2 * CORTE - 0.4f) * j / n
            val y = ABAJO - 0.3f + 0.35f * sin(x * 1.3f)
            val col = if (l > 0.05f) (if (j % 2 == 0) LUMBRE_LUZ else LUMBRE) else (if (j % 2 == 0) LATON else LATON_OSC)
            drawLine(col, Offset(x, y), Offset(x + (j % 3 - 1) * 0.3f, y + 2.6f + (j % 2) * 0.5f), 0.5f, StrokeCap.Round)
        }
    }

    /** La vara de hierro de la fragua: forjada, con sus marcas de martillo, y los remates de latón en las puntas. */
    private fun DrawScope.vara() {
        drawRoundRect(Brush.verticalGradient(listOf(HIERRO_LUZ, HIERRO, HIERRO_OSC), 1f, 3.6f), Offset(1.6f, 1f), Size(22.8f, 2.6f), CornerRadius(1.3f))
        drawLine(Color.White.copy(alpha = 0.3f), Offset(2.6f, 1.6f), Offset(23.4f, 1.6f), 0.35f, StrokeCap.Round)
        for (x in listOf(3.8f, 8.2f, 13f, 17.6f, 22.1f)) drawLine(HIERRO_OSC.copy(alpha = 0.6f), Offset(x, 1.2f), Offset(x + 0.4f, 3.4f), 0.3f)
        for (l in listOf(-1f, 1f)) {
            val x = MEDIO + l * 11.9f
            drawCircle(LATON_OSC, 1.55f, Offset(x + 0.15f, 2.5f))
            drawCircle(LATON, 1.35f, Offset(x, 2.3f))
            drawCircle(LATON_LUZ, 0.5f, Offset(x - 0.4f, 1.85f))
            // La puntita del remate, hacia fuera.
            drawPath(Path().apply { moveTo(x + l * 1.1f, 1.7f); lineTo(x + l * 1.1f, 2.9f); lineTo(x + l * 1.95f, 2.3f); close() }, LATON)
        }
    }

    /** Una presilla del paño, echada sobre la vara: la vuelta de arriba con su luz y la caída hasta el canto del paño. */
    private fun DrawScope.presilla(x: Float) {
        val w = 2.6f
        drawRoundRect(CARMESI_HONDO, Offset(x - w / 2f + 0.25f, 0.6f), Size(w, ARRIBA + 0.4f), CornerRadius(1f))
        drawRoundRect(Brush.horizontalGradient(listOf(CARMESI_LUZ, CARMESI, CARMESI_OSC), x - w / 2f, x + w / 2f), Offset(x - w / 2f, 0.4f), Size(w, ARRIBA + 0.4f), CornerRadius(1f))
        // La vuelta sobre la vara, con su luz, y la sombra donde la tela dobla bajo ella.
        drawLine(Color.White.copy(alpha = 0.35f), Offset(x - w / 2f + 0.5f, 1f), Offset(x + w / 2f - 0.7f, 1f), 0.35f, StrokeCap.Round)
        drawLine(CARMESI_HONDO.copy(alpha = 0.55f), Offset(x - w / 2f, 3.75f), Offset(x + w / 2f, 3.75f), 0.4f)
        // La costura de la presilla al paño.
        for (i in 0..2) drawLine(LATON_OSC.copy(alpha = 0.7f), Offset(x - 0.8f + i * 0.8f, ARRIBA + 0.1f), Offset(x - 0.6f + i * 0.8f, ARRIBA + 0.5f), 0.18f)
    }
}
