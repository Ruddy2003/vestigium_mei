package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Napoleónico: la bandera del regimiento, la de Jean-Baptiste (el cuento: «la bandera del regimiento», «las águilas van
 * envueltas en trapos»), hecha banderita: el modelo de 1804, con el rombo crema en el medio y las esquinas azul y
 * carmesí, el filete de oro alrededor y el águila de oro con las alas abiertas en el rombo. Abajo termina en punta,
 * como el escudo del tema, con su fleco de oro; cuelga de un asta de azul tinta con regatones de oro y lleva la corbata
 * carmesí atada junto al asta, con su borla. Sin letras ni cifras (la de verdad llevaba el nombre del regimiento y sus
 * batallas: aquí no). Al mantener el dedo se llena de oro desde la punta, y lo que queda bajo el oro pasa a azul tinta.
 * **[Diseño propio]**: el tema de pixel no tiene banderita; los colores son los del tema (`Disenios.kt`: azul imperial
 * #1E3A8A, carmesí #A3172E, oro #B8892B, papel #F4EEDF, tinta #14213D) y el motivo es la bandera de su regimiento.
 */
internal object BanderitaNapoleon : Banderita {
    private val AZUL = Color(0xFF1E3A8A)
    private val AZUL_LUZ = Color(0xFF3556A8)
    private val CARMESI = Color(0xFFA3172E)
    private val CARMESI_LUZ = Color(0xFFC23347)
    private val CREMA = Color(0xFFF4EEDF)
    private val CREMA_OSC = Color(0xFFDCCFAE)
    private val ORO = Color(0xFFB8892B)
    private val ORO_CLARO = Color(0xFFD9B25B)
    private val ORO_LUZ = Color(0xFFF2D78E)
    private val TINTA = Color(0xFF14213D)

    /** Lo alto de la tela, su ancho y dónde empieza la punta. */
    private const val ARRIBA = 4.2f
    private const val IZQ = 3.6f
    private const val DER = 22.4f
    private const val HOMBRO = 30.2f
    private const val PUNTA = 37.4f
    private const val MEDIO = 13f

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val nivel = PUNTA - (PUNTA - ARRIBA + 0.6f) * lleno.coerceIn(0f, 1f)
            // La tela: cae del asta, con la orilla de la derecha que ondula un poco, y termina en punta.
            val cuerpo = Path().apply {
                moveTo(IZQ, ARRIBA); lineTo(DER, ARRIBA)
                cubicTo(DER + 0.5f, 12f, DER - 0.4f, 21f, DER + 0.2f, HOMBRO)
                cubicTo(19f, 32.4f, 15.6f, 35f, MEDIO, PUNTA)
                cubicTo(10.4f, 35f, 7f, 32.4f, IZQ - 0.2f, HOMBRO)
                cubicTo(IZQ + 0.3f, 21f, IZQ - 0.3f, 12f, IZQ, ARRIBA); close()
            }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.3f)) }, Color.Black.copy(alpha = 0.3f))
            val cy = (ARRIBA + HOMBRO) / 2f
            clipPath(cuerpo) {
                // Las cuatro esquinas: arriba azul y carmesí, abajo carmesí y azul (las de abajo siguen por la punta).
                drawRect(AZUL, Offset(0f, 0f), Size(MEDIO, cy))
                drawRect(CARMESI, Offset(MEDIO, 0f), Size(26f - MEDIO, cy))
                drawRect(CARMESI, Offset(0f, cy), Size(MEDIO, 40f - cy))
                drawRect(AZUL, Offset(MEDIO, cy), Size(26f - MEDIO, 40f - cy))
                // El rombo crema, que toca los cuatro lados.
                val rombo = Path().apply { moveTo(MEDIO, ARRIBA + 0.2f); lineTo(DER + 0.2f, cy); lineTo(MEDIO, HOMBRO + 1.4f); lineTo(IZQ - 0.2f, cy); close() }
                drawPath(rombo, Brush.horizontalGradient(listOf(CREMA_OSC, CREMA, CREMA, CREMA_OSC), IZQ, DER))
                // Los pliegues de la seda: franjas de luz y de sombra que caen a lo largo.
                for ((x, a) in listOf(7.4f to 0.13f, 16.8f to 0.1f)) drawLine(Color.White.copy(alpha = a), Offset(x, ARRIBA), Offset(x + 0.6f, PUNTA), 1.6f)
                for (x in listOf(10.8f, 20.4f)) drawLine(TINTA.copy(alpha = 0.16f), Offset(x, ARRIBA), Offset(x + 0.4f, PUNTA), 1.2f)
                // Las esquinas, un poco más claras donde la seda da a la luz.
                drawLine(AZUL_LUZ.copy(alpha = 0.5f), Offset(IZQ + 1.2f, ARRIBA + 1f), Offset(IZQ + 1.2f, cy - 4f), 0.8f)
                drawLine(CARMESI_LUZ.copy(alpha = 0.45f), Offset(IZQ + 1.2f, cy + 4f), Offset(IZQ + 1.6f, HOMBRO), 0.8f)
                // Lo que se ha llenado: el oro, que sube desde la punta.
                if (lleno > 0f) drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO_CLARO, ORO), nivel, PUNTA), Offset(0f, nivel), Size(26f, 40f - nivel))
            }
            // Lo que va encima de la tela, en oro por encima del nivel y en azul tinta por debajo, para que se siga viendo.
            dosTintas(nivel) { tinta, clara ->
                // El filete de oro alrededor, por dentro del canto, y las cuatro coronitas de laurel de las esquinas.
                val filete = Path().apply {
                    moveTo(IZQ + 1.3f, ARRIBA + 1.3f); lineTo(DER - 1.3f, ARRIBA + 1.3f); lineTo(DER - 1.1f, HOMBRO - 0.4f)
                    cubicTo(18.4f, 32f, 15.2f, 34.2f, MEDIO, 35.6f); cubicTo(10.8f, 34.2f, 7.6f, 32f, IZQ + 1.1f, HOMBRO - 0.4f); close()
                }
                drawPath(filete, tinta, style = Stroke(0.5f))
                for ((x, y, gira) in listOf(Triple(IZQ + 3.5f, ARRIBA + 3.6f, 1f), Triple(DER - 3.5f, ARRIBA + 3.6f, -1f), Triple(IZQ + 3.4f, HOMBRO - 3.8f, 1f), Triple(DER - 3.4f, HOMBRO - 3.8f, -1f)))
                    corona(Offset(x, y), gira, tinta)
                // El águila en el rombo.
                aguila(Offset(MEDIO, cy + 0.2f), tinta, clara)
            }
            drawPath(cuerpo, TINTA.copy(alpha = 0.45f), style = Stroke(0.45f))
            // El fleco de oro de las dos orillas de la punta.
            for (j in 0..10) {
                val t = j / 10f
                val (x, y) = if (t < 0.5f) Pair(IZQ + (MEDIO - IZQ) * t * 2f, HOMBRO + (PUNTA - HOMBRO) * t * 2f) else Pair(MEDIO + (DER - MEDIO) * (t - 0.5f) * 2f, PUNTA - (PUNTA - HOMBRO) * (t - 0.5f) * 2f)
                drawLine(if (j % 2 == 0) ORO_CLARO else ORO, Offset(x, y - 0.2f), Offset(x + (j % 3 - 1) * 0.25f, y + 1.9f + (j % 2) * 0.5f), 0.55f, StrokeCap.Round)
            }
            // El asta: azul tinta, con los regatones de oro en las puntas y las vueltas del cordón que sujeta la tela.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF3A4A72), TINTA, Color(0xFF0A1022)), 0.8f, 4f), Offset(1f, 0.8f), Size(24f, 3.2f), CornerRadius(1.6f))
            drawLine(Color.White.copy(alpha = 0.22f), Offset(2.4f, 1.5f), Offset(23.6f, 1.5f), 0.5f, StrokeCap.Round)
            for (x in listOf(0.9f, 25.1f)) {
                drawCircle(ORO, 1.75f, Offset(x, 2.4f)); drawCircle(ORO_CLARO, 1.15f, Offset(x - 0.15f, 2.15f)); drawCircle(ORO_LUZ, 0.45f, Offset(x - 0.4f, 1.8f))
            }
            for (x in listOf(5.2f, 9.6f, 16.4f, 20.8f)) { drawLine(ORO, Offset(x - 0.5f, 0.9f), Offset(x + 0.5f, 3.9f), 0.6f, StrokeCap.Round) }
            // La corbata carmesí atada al asta, a la izquierda, con su caída y su borla de oro.
            drawPath(Path().apply { moveTo(2.6f, 3.4f); cubicTo(2.2f, 7f, 1.2f, 10f, 1.4f, 13.4f); lineTo(2.8f, 13f); cubicTo(2.8f, 10f, 3.6f, 7f, 4.2f, 3.6f); close() }, CARMESI)
            drawPath(Path().apply { moveTo(3.4f, 3.6f); cubicTo(3.2f, 7.4f, 2.4f, 10.4f, 2.6f, 12.8f) }, CARMESI_LUZ.copy(alpha = 0.7f), style = Stroke(0.35f))
            drawOval(CARMESI, Offset(1.6f, 1.6f), Size(3.4f, 3f)); drawOval(CARMESI_LUZ, Offset(2.2f, 2f), Size(1.4f, 1f))
            drawPath(Path().apply { moveTo(2.1f, 13.2f); lineTo(1.2f, 16.6f); lineTo(3.4f, 16.6f); close() }, ORO)
            for (dx in listOf(1.5f, 2.1f, 2.7f, 3.2f)) drawLine(ORO_CLARO, Offset(dx, 15.8f), Offset(dx - 0.1f, 17.6f), 0.35f, StrokeCap.Round)
            drawCircle(ORO_CLARO, 0.6f, Offset(2.1f, 13.2f))
        }
    }

    /** Pinta [dibujo] dos veces: con oro por encima de [nivel] y con azul tinta por debajo (donde ya subió el oro). */
    private inline fun DrawScope.dosTintas(nivel: Float, crossinline dibujo: DrawScope.(Color, Color) -> Unit) {
        clipRect(0f, 0f, 26f, nivel) { dibujo(ORO, ORO_LUZ) }
        if (nivel < 40f) clipRect(0f, nivel, 26f, 40f) { dibujo(TINTA, Color(0xFF3A4A72)) }
    }

    /**
     * Una coronita de laurel: dos ramas que suben desde abajo, cada una con tres hojas en punta que miran hacia arriba,
     * y abierta arriba. [gira] da hacia qué lado cae el cabo del lazo de abajo.
     */
    private fun DrawScope.corona(c: Offset, gira: Float, color: Color) {
        val r = 1.35f
        for (l in listOf(-1f, 1f)) {
            // La rama: un arco del pie (abajo) hasta arriba, por este lado.
            drawArc(color, if (l < 0f) 100f else -10f, 110f, false, Offset(c.x - r, c.y - r), Size(r * 2f, r * 2f), style = Stroke(0.32f, cap = StrokeCap.Round))
            for (grados in listOf(70f, 25f, -20f)) {
                val a = Math.toRadians((if (l < 0f) 180f - grados else grados).toDouble()).toFloat()
                val u = Offset(cos(a), sin(a))
                val base = c + u * r
                val subir = Offset(-sin(a), cos(a)).let { if (it.y > 0f) it * -1f else it }   // a lo largo de la rama, hacia arriba
                val punta = base + subir * 1.05f + u * 0.45f
                val ancho = Offset(-(punta - base).y, (punta - base).x) * 0.38f
                val medio = (base + punta) / 2f
                drawPath(Path().apply { moveTo(base.x, base.y); cubicTo(medio.x + ancho.x, medio.y + ancho.y, punta.x, punta.y, punta.x, punta.y); cubicTo(punta.x, punta.y, medio.x - ancho.x, medio.y - ancho.y, base.x, base.y); close() }, color)
            }
        }
        drawLine(color, c + Offset(0f, r), c + Offset(0.7f * gira, r + 0.9f), 0.3f, StrokeCap.Round)
    }

    /**
     * El águila imperial, de frente, con la cabeza vuelta a su derecha y las alas abiertas hacia abajo, sobre el haz de
     * rayos. [tinta] es su color y [clara] el de sus brillos.
     */
    private fun DrawScope.aguila(c: Offset, tinta: Color, clara: Color) {
        // Las alas abiertas en alto (el águila «explayada» de los escudos): suben del hombro hasta la punta, bajan por
        // fuera y vuelven hacia el cuerpo en cuatro plumas largas.
        for (l in listOf(-1f, 1f)) {
            val h = c + Offset(l * 1f, -2.2f)
            val ala = Path().apply {
                moveTo(h.x, h.y)
                cubicTo(h.x + l * 0.8f, h.y - 2.6f, h.x + l * 2.4f, h.y - 4.4f, h.x + l * 4.6f, h.y - 5.6f)
                cubicTo(h.x + l * 5.2f, h.y - 3.6f, h.x + l * 5.6f, h.y - 1.4f, h.x + l * 5.5f, h.y + 1.2f)
                for (i in 0..3) {
                    val px = h.x + l * (5.5f - i * 1.15f); val py = h.y + 1.2f + i * 0.35f
                    lineTo(px - l * 0.2f, py + 1.5f); lineTo(px - l * 1.1f, py + 0.2f)
                }
                cubicTo(h.x + l * 0.8f, h.y + 1.6f, h.x + l * 0.3f, h.y + 1f, h.x, h.y + 0.6f)
                close()
            }
            drawPath(ala, tinta)
            // Las líneas de las plumas, en claro, que bajan del arco del ala.
            for (i in 0..2) drawLine(clara.copy(alpha = 0.75f), h + Offset(l * (1.7f + i * 1.15f), -2.6f + i * 0.9f), h + Offset(l * (1.7f + i * 1.15f), 1.4f), 0.24f)
            drawLine(clara.copy(alpha = 0.6f), h + Offset(l * 0.7f, -1.2f), h + Offset(l * 4.4f, -5f), 0.22f)
        }
        // El cuerpo, el pecho con su brillo y la cola en abanico.
        drawOval(tinta, Offset(c.x - 1.4f, c.y - 3.6f), Size(2.8f, 5.8f))
        drawPath(Path().apply { moveTo(c.x - 0.9f, c.y + 1.6f); lineTo(c.x - 2.1f, c.y + 4.8f); lineTo(c.x - 0.7f, c.y + 4.3f); lineTo(c.x, c.y + 5.2f); lineTo(c.x + 0.7f, c.y + 4.3f); lineTo(c.x + 2.1f, c.y + 4.8f); lineTo(c.x + 0.9f, c.y + 1.6f); close() }, tinta)
        drawLine(clara.copy(alpha = 0.8f), c + Offset(-0.45f, -2.6f), c + Offset(-0.55f, 0.8f), 0.32f, StrokeCap.Round)
        // La cabeza, vuelta a un lado, con el pico ganchudo y el ojo.
        val cabeza = c + Offset(0.5f, -4.6f)
        drawCircle(tinta, 1.1f, cabeza)
        drawPath(Path().apply { moveTo(cabeza.x + 0.6f, cabeza.y - 0.7f); cubicTo(cabeza.x + 1.7f, cabeza.y - 0.8f, cabeza.x + 2.3f, cabeza.y - 0.1f, cabeza.x + 2f, cabeza.y + 0.8f); lineTo(cabeza.x + 0.7f, cabeza.y + 0.4f); close() }, tinta)
        drawCircle(clara, 0.26f, cabeza + Offset(0.3f, -0.25f))
        // Las garras sobre el haz de rayos: un zigzag a lo ancho, bajo el pecho.
        drawPath(Path().apply { moveTo(c.x - 3.6f, c.y + 3f); lineTo(c.x - 2f, c.y + 2.3f); lineTo(c.x - 1.3f, c.y + 3.3f); lineTo(c.x + 0.4f, c.y + 2.5f); lineTo(c.x + 1.2f, c.y + 3.4f); lineTo(c.x + 3.6f, c.y + 2.6f) }, tinta, style = Stroke(0.6f, cap = StrokeCap.Round))
    }
}
