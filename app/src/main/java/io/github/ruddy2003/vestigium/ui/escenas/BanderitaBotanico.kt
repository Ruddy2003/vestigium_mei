package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import kotlin.math.sqrt

/**
 * Botánico: una hoja de herbario colgada con bramante de una ramita, cortada como la forma del tema (dos esquinas
 * redondas en diagonal, «la hoja»). En el papel envejecido, un helecho prensado sujeto con dos tiras de papel engomado
 * color óxido y un escarabajo verde metálico clavado con su alfiler, como en el cuaderno del naturalista; abajo, la
 * etiqueta en blanco (solo sus renglones). Sin letras. Al mantener el dedo el papel se tiñe de verde musgo desde abajo,
 * y lo que queda debajo cambia a papel para que se siga viendo. **[Diseño propio]**: el tema de pixel no tiene
 * banderita; los colores son los del tema (`ui/Disenios.kt`: papel #EFE8D6, tinta #2A2318, musgo #3F5A2C, óxido
 * #8C4A22, verde escarabajo #2E5E5A) y el motivo, el herbario y los escarabajos de la descripción del tema.
 */
internal object BanderitaBotanico : Banderita {
    private val PAPEL = Color(0xFFEFE8D6)
    private val PAPEL_LUZ = Color(0xFFF7F2E4)
    private val PAPEL_OSC = Color(0xFFD9CDB0)
    private val BORDE = Color(0xFFBFAF8A)
    private val TINTA = Color(0xFF2A2318)
    private val MUSGO = Color(0xFF3F5A2C)
    private val MUSGO_LUZ = Color(0xFF6E8C48)
    private val MUSGO_LLENO = Color(0xFF557236)
    private val OXIDO = Color(0xFF8C4A22)
    private val OXIDO_LUZ = Color(0xFFB0683A)
    private val ESCARABAJO = Color(0xFF2E5E5A)
    private val ESCARABAJO_LUZ = Color(0xFF6FC2B0)
    private val RAMA = Color(0xFF7A5A3A)
    private val RAMA_OSC = Color(0xFF4A3524)
    private val BRAMANTE = Color(0xFFC8B48A)

    private const val ARRIBA = 6f
    private const val ABAJO = 37f
    private const val IZQ = 3.6f
    private const val DER = 22.4f

    /** Una hojita de punta: de [base] a [punta], con [ancho] a cada lado. */
    private fun hojita(base: Offset, punta: Offset, ancho: Float): Path {
        val d = punta - base
        val l = sqrt(d.x * d.x + d.y * d.y).coerceAtLeast(0.01f)
        val n = Offset(-d.y / l, d.x / l) * ancho
        return Path().apply {
            moveTo(base)
            cubicTo(base + d * 0.25f + n, base + d * 0.7f + n * 0.9f, punta)
            cubicTo(base + d * 0.7f - n * 0.9f, base + d * 0.25f - n, base)
            close()
        }
    }

    /** El papel: rectángulo con las esquinas de arriba a la izquierda y de abajo a la derecha muy redondas. */
    private fun papel(dx: Float = 0f, dy: Float = 0f): Path = Path().apply {
        val g = 6.4f; val c = 1f
        val a = IZQ + dx; val b = DER + dx; val t = ARRIBA + dy; val f = ABAJO + dy
        moveTo(a + g, t); lineTo(b - c, t); cubicTo(b - c * 0.45f, t, b, t + c * 0.45f, b, t + c)
        lineTo(b, f - g); cubicTo(b, f - g * 0.45f, b - g * 0.45f, f, b - g, f)
        lineTo(a + c, f); cubicTo(a + c * 0.45f, f, a, f - c * 0.45f, a, f - c)
        lineTo(a, t + g); cubicTo(a, t + g * 0.45f, a + g * 0.45f, t, a + g, t); close()
    }

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val nivel = ABAJO - (ABAJO - ARRIBA) * lleno.coerceIn(0f, 1f)
            /** Si el musgo ya llegó a la altura [y]. */
            fun cubre(y: Float) = lleno > 0f && y > nivel
            val hoja = papel()
            // La sombra que el papel echa detrás, y el papel con su luz (de arriba a la izquierda) y su borde gastado.
            drawPath(papel(0.9f, 1.2f), Color.Black.copy(alpha = 0.3f))
            drawPath(hoja, Brush.linearGradient(listOf(PAPEL_LUZ, PAPEL, PAPEL_OSC), Offset(IZQ, ARRIBA), Offset(DER, ABAJO)))
            clipPath(hoja) {
                // Manchas de humedad del viaje: dos aureolas pálidas.
                drawOval(PAPEL_OSC.copy(alpha = 0.55f), Offset(15.2f, 8f), Size(6.4f, 4.4f))
                drawOval(PAPEL_OSC.copy(alpha = 0.4f), Offset(4f, 27f), Size(5f, 6f))
                // Lo que se ha llenado: el musgo, que sube desde abajo, con un filo más claro.
                if (lleno > 0f) {
                    drawRect(Brush.verticalGradient(listOf(MUSGO_LLENO, MUSGO), nivel, ABAJO), Offset(0f, nivel), Size(26f, ABAJO - nivel + 1f))
                    drawLine(MUSGO_LUZ, Offset(0f, nivel + 0.25f), Offset(26f, nivel + 0.25f), 0.5f)
                }
            }
            // El helecho prensado: el raquis curvo, de abajo a la izquierda hacia arriba, y las pinnas alternas que se achican.
            val r0 = Offset(8.2f, 33.2f); val r1 = Offset(7.4f, 24f); val r2 = Offset(15.6f, 16f); val r3 = Offset(17.6f, 9.6f)
            fun raquis(t: Float): Offset {
                val u = 1f - t
                return r0 * (u * u * u) + r1 * (3 * u * u * t) + r2 * (3 * u * t * t) + r3 * (t * t * t)
            }
            for (i in 0..12) {
                val t = 0.1f + i * 0.068f
                val p = raquis(t)
                val d = raquis(t + 0.02f) - p
                val l = sqrt(d.x * d.x + d.y * d.y).coerceAtLeast(0.01f)
                val tg = d / l; val n = Offset(-tg.y, tg.x)
                val largo = 4.6f * (1f - t * 0.72f)
                for (lado in listOf(-1f, 1f)) {
                    val base = p + tg * (if (lado > 0f) 0.15f else -0.15f)
                    val punta = base + n * (lado * largo) + tg * (largo * 0.55f)
                    val color = when {
                        cubre(punta.y) -> PAPEL
                        (i + (if (lado > 0f) 1 else 0)) % 2 == 0 -> MUSGO
                        else -> MUSGO_LUZ
                    }
                    drawPath(hojita(base, punta, 0.78f * (1f - t * 0.4f)), color)
                }
            }
            // El raquis: oscuro sobre el papel y de papel donde ya subió el musgo.
            val tallo = Path().apply { moveTo(r0); cubicTo(r1, r2, r3) }
            clipRect(0f, 0f, 26f, nivel) { drawPath(tallo, RAMA_OSC, style = Stroke(0.55f, cap = StrokeCap.Round)) }
            if (lleno > 0f) clipRect(0f, nivel, 26f, 40f) { drawPath(tallo, PAPEL, style = Stroke(0.55f, cap = StrokeCap.Round)) }
            // Las dos tiras de papel engomado que lo sujetan.
            for ((c, giro) in listOf(Offset(8.1f, 29.2f) to 0.35f, Offset(13.6f, 17.6f) to -0.5f)) {
                val ax = Offset(1.9f, giro); val ay = Offset(-giro * 0.3f, 0.75f)
                val tira = Path().apply { moveTo(c - ax - ay); lineTo(c + ax - ay); lineTo(c + ax + ay); lineTo(c - ax + ay); close() }
                drawPath(tira, if (cubre(c.y)) OXIDO_LUZ else OXIDO)
                drawLine(OXIDO_LUZ.copy(alpha = 0.7f), c - ax - ay * 0.5f, c + ax - ay * 0.5f, 0.25f)
            }
            // El escarabajo verde metálico, clavado con su alfiler, arriba a la izquierda.
            val e = Offset(8.6f, 13.4f)
            val tapado = cubre(e.y)
            val caparazon = if (tapado) PAPEL else ESCARABAJO
            for (lado in listOf(-1f, 1f)) for (j in 0..2) {
                val a = e + Offset(lado * 1.1f, -0.6f + j * 1.1f)
                drawLine(if (tapado) PAPEL else TINTA, a, a + Offset(lado * 1.4f, -0.5f + j * 0.7f), 0.28f, StrokeCap.Round)
            }
            drawOval(if (tapado) PAPEL else TINTA, Offset(e.x - 0.85f, e.y - 2.6f), Size(1.7f, 1.4f))
            drawOval(caparazon, Offset(e.x - 1.55f, e.y - 1.5f), Size(3.1f, 4.2f))
            if (!tapado) {
                drawLine(TINTA.copy(alpha = 0.7f), Offset(e.x, e.y - 1.4f), Offset(e.x, e.y + 2.6f), 0.22f)
                drawOval(ESCARABAJO_LUZ.copy(alpha = 0.8f), Offset(e.x - 1.1f, e.y - 1f), Size(0.8f, 2f))
            }
            drawLine(Color(0xFF8A8C90), e + Offset(0.15f, 0.3f), e + Offset(0.9f, -0.5f), 0.25f)
            drawCircle(OXIDO, 0.5f, e + Offset(0.2f, 0.2f))
            // La etiqueta, abajo a la derecha, con sus renglones y su filete.
            val et = Rect(13.4f, 27.6f, 20.6f, 32.6f)
            drawRect(if (cubre(et.top)) PAPEL_OSC else PAPEL_LUZ, et.topLeft, et.size)
            drawRect(if (cubre(et.top)) MUSGO else BORDE, et.topLeft, et.size, style = Stroke(0.3f))
            for (y in listOf(29.4f, 30.9f)) drawLine(if (cubre(et.top)) MUSGO_LUZ else TINTA.copy(alpha = 0.35f), Offset(14.4f, y), Offset(19.6f - (y - 29.4f) * 1.2f, y), 0.25f)
            // El canto del papel: un borde fino oscuro para que se lea sobre claro y sobre oscuro.
            drawPath(hoja, TINTA.copy(alpha = 0.55f), style = Stroke(0.45f))
            drawPath(hoja, BORDE.copy(alpha = 0.6f), style = Stroke(0.2f))
            // El bramante: de los dos ojales del papel a la ramita, y su nudo.
            for ((x0, x1) in listOf(7.4f to 9.8f, 18.6f to 16.2f)) {
                drawLine(BRAMANTE, Offset(x0, ARRIBA + 1.2f), Offset(x1, 2.6f), 0.42f, StrokeCap.Round)
                drawCircle(TINTA.copy(alpha = 0.7f), 0.5f, Offset(x0, ARRIBA + 1.2f))
                drawCircle(PAPEL_OSC, 0.25f, Offset(x0, ARRIBA + 1.2f))
            }
            // La ramita, con su corteza, un nudo, una horquilla y dos hojitas en el extremo.
            drawLine(RAMA_OSC, Offset(1f, 3.1f), Offset(24.2f, 2.4f), 2.1f, StrokeCap.Round)
            drawLine(RAMA, Offset(1f, 2.7f), Offset(24.2f, 2f), 1.1f, StrokeCap.Round)
            drawLine(RAMA_OSC, Offset(4.4f, 2.9f), Offset(2.6f, 0.6f), 0.7f, StrokeCap.Round)
            drawCircle(RAMA_OSC, 0.55f, Offset(13f, 2.6f))
            drawPath(hojita(Offset(23.6f, 2.2f), Offset(25.6f, 5.6f), 0.9f), MUSGO)
            drawPath(hojita(Offset(23.8f, 2f), Offset(25.8f, 0.2f), 0.8f), MUSGO_LUZ)
            for (x in listOf(9.8f, 16.2f)) drawCircle(BRAMANTE, 0.6f, Offset(x, 2.5f))
        }
    }
}
