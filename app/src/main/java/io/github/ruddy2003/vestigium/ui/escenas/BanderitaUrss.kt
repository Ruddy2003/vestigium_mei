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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Soviético: la banderola roja de la casa de cultura y del desfile (el cuento de Liudmila: «banderas rojas» en las
 * farolas y los balcones; la pestaña Vida: «la banderola roja con estrellas y fleco de oro»), hecha banderita: un paño
 * rojo de cartel, cortado abajo en diagonal como los bloques inclinados del tema, con una franja negra que lo cruza en
 * diagonal, la estrella de cinco puntas de oro arriba y dos espigas de oro que la abrazan desde abajo, y el fleco de oro
 * en el corte. Cuelga de una varilla negra con remates de oro y su cordón. Sin letras ni cifras, sin caras y sin hoz y
 * martillo (el cuento no los nombra). Al mantener el dedo se llena de oro desde abajo, y lo que queda bajo el oro pasa
 * a rojo oscuro, para que la estrella y las espigas se sigan viendo.
 * **[Diseño propio]**: el tema de pixel no tiene banderita; los colores son los del tema (`Disenios.kt`: rojo #C1121F,
 * negro #1D1D1B, oro #D4A017, papel #F7F0DE, rojo oscuro #8A0E14) y el motivo, la estrella y las espigas de la
 * banderola de la casa de cultura y la diagonal del cartel constructivista.
 */
internal object BanderitaUrss : Banderita {
    private val ROJO = Color(0xFFC1121F)
    private val ROJO_LUZ = Color(0xFFE5252F)
    private val ROJO_OSC = Color(0xFF8A0E14)
    private val NEGRO = Color(0xFF1D1D1B)
    private val NEGRO_LUZ = Color(0xFF4A4440)
    private val ORO = Color(0xFFD4A017)
    private val ORO_CLARO = Color(0xFFE8B923)
    private val ORO_LUZ = Color(0xFFF6DD8A)
    private val PAPEL = Color(0xFFF7F0DE)

    /** Lo alto del paño, sus orillas y el corte de abajo (más alto a la derecha). */
    private const val ARRIBA = 3.8f
    private const val IZQ = 3.4f
    private const val DER = 22.6f
    private const val BAJO_IZQ = 37.2f
    private const val BAJO_DER = 32.4f

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val nivel = BAJO_IZQ - (BAJO_IZQ - ARRIBA + 0.6f) * lleno.coerceIn(0f, 1f)
            // El paño: cae de la varilla, la orilla de la derecha ondula un poco, y abajo se corta en diagonal.
            val cuerpo = Path().apply {
                moveTo(IZQ, ARRIBA); lineTo(DER, ARRIBA)
                cubicTo(DER + 0.5f, 12f, DER - 0.4f, 22f, DER + 0.1f, BAJO_DER)
                lineTo(IZQ - 0.1f, BAJO_IZQ)
                cubicTo(IZQ + 0.3f, 26f, IZQ - 0.3f, 13f, IZQ, ARRIBA); close()
            }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.3f)) }, Color.Black.copy(alpha = 0.3f))
            clipPath(cuerpo) {
                drawRect(Brush.horizontalGradient(listOf(ROJO_OSC, ROJO, ROJO_LUZ, ROJO, ROJO_OSC), IZQ, DER), Offset(0f, 0f), Size(26f, 40f))
                // La franja negra en diagonal, de cartel, que baja de la orilla izquierda hacia el corte; con un filo de papel.
                val franja = Path().apply { moveTo(IZQ - 1f, 29.6f); lineTo(DER + 1f, 23.2f); lineTo(DER + 1f, 26.2f); lineTo(IZQ - 1f, 32.6f); close() }
                drawPath(franja, NEGRO)
                drawLine(PAPEL.copy(alpha = 0.85f), Offset(IZQ - 1f, 33.3f), Offset(DER + 1f, 26.9f), 0.55f)
                // Los pliegues de la tela, a lo largo.
                for ((x, a) in listOf(7.6f to 0.12f, 17.2f to 0.1f)) drawLine(Color.White.copy(alpha = a), Offset(x, ARRIBA), Offset(x + 0.5f, BAJO_IZQ), 1.6f)
                for (x in listOf(11f, 20.6f)) drawLine(NEGRO.copy(alpha = 0.16f), Offset(x, ARRIBA), Offset(x + 0.4f, BAJO_IZQ), 1.2f)
                // Lo que se ha llenado: el oro, que sube desde abajo.
                if (lleno > 0f) drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO_CLARO, ORO), nivel, BAJO_IZQ), Offset(0f, nivel), Size(26f, 40f - nivel))
            }
            // La estrella y las espigas: de oro por encima del nivel y de rojo oscuro por debajo.
            dosTintas(nivel) { tinta, clara ->
                val c = Offset(13f, 11.2f)
                drawPath(estrella(c, 4.9f), tinta)
                // Un brillo en las dos puntas de arriba a la izquierda, como de metal estampado.
                drawPath(Path().apply { moveTo(c.x, c.y - 4.9f); lineTo(c.x - 0.9f, c.y - 2.1f); lineTo(c.x, c.y - 0.2f); close() }, clara.copy(alpha = 0.7f))
                drawPath(Path().apply { moveTo(c.x - 4.66f, c.y - 1.5f); lineTo(c.x - 1.9f, c.y - 0.7f); lineTo(c.x, c.y - 0.2f); close() }, clara.copy(alpha = 0.45f))
                espiga(-1f, tinta)
                espiga(1f, tinta)
                // La cinta que ata las dos espigas abajo, donde se cruzan: dos cabos que caen.
                drawPath(Path().apply { moveTo(12.2f, 22.4f); lineTo(13.8f, 22.4f); lineTo(15.4f, 25.6f); lineTo(14.5f, 25.1f); lineTo(13.9f, 26f); lineTo(13f, 23.4f); lineTo(12.1f, 26f); lineTo(11.5f, 25.1f); lineTo(10.6f, 25.6f); close() }, tinta)
            }
            drawPath(cuerpo, NEGRO.copy(alpha = 0.5f), style = Stroke(0.45f))
            // El fleco de oro a lo largo del corte.
            for (j in 0..11) {
                val t = j / 11f
                val x = IZQ + (DER - IZQ) * t; val y = BAJO_IZQ + (BAJO_DER - BAJO_IZQ) * t
                drawLine(if (j % 2 == 0) ORO_CLARO else ORO, Offset(x, y - 0.2f), Offset(x + (j % 3 - 1) * 0.25f, y + 2f + (j % 2) * 0.5f), 0.55f, StrokeCap.Round)
            }
            // La varilla negra, con su brillo, sus remates de oro y las vueltas del cordón que sujeta el paño.
            drawRoundRect(Brush.verticalGradient(listOf(NEGRO_LUZ, NEGRO, Color(0xFF0C0B0A)), 0.8f, 4f), Offset(1f, 0.8f), Size(24f, 3.2f), CornerRadius(1.6f))
            drawLine(Color.White.copy(alpha = 0.22f), Offset(2.4f, 1.5f), Offset(23.6f, 1.5f), 0.5f, StrokeCap.Round)
            for (x in listOf(0.9f, 25.1f)) {
                drawCircle(ORO, 1.75f, Offset(x, 2.4f)); drawCircle(ORO_CLARO, 1.15f, Offset(x - 0.15f, 2.15f)); drawCircle(ORO_LUZ, 0.45f, Offset(x - 0.4f, 1.8f))
            }
            for (x in listOf(5.4f, 9.8f, 16.2f, 20.6f)) drawLine(ORO, Offset(x - 0.5f, 0.9f), Offset(x + 0.5f, 3.9f), 0.6f, StrokeCap.Round)
        }
    }

    /** Pinta [dibujo] dos veces: de oro por encima de [nivel] y de rojo oscuro por debajo (donde ya subió el oro). */
    private inline fun DrawScope.dosTintas(nivel: Float, crossinline dibujo: DrawScope.(Color, Color) -> Unit) {
        clipRect(0f, 0f, 26f, nivel) { dibujo(ORO, ORO_LUZ) }
        if (nivel < 40f) clipRect(0f, nivel, 26f, 40f) { dibujo(ROJO_OSC, ROJO_LUZ) }
    }

    /** Una estrella de cinco puntas con centro en [c]. */
    private fun estrella(c: Offset, radio: Float): Path = Path().apply {
        for (i in 0 until 10) {
            val a = -PI.toFloat() / 2f + i * PI.toFloat() / 5f; val r = if (i % 2 == 0) radio else radio * 0.4f
            val x = c.x + r * cos(a); val y = c.y + r * sin(a); if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * Una espiga que sube en arco desde abajo, donde se cruza con la otra, hasta el lado de la estrella: el tallo y
     * siete granos en punta, de dos en dos, con la arista del último. [lado]: −1 la de la izquierda, 1 la de la derecha.
     */
    private fun DrawScope.espiga(lado: Float, color: Color) {
        // El tallo: nace junto al medio, abajo, abre hacia fuera y vuelve un poco hacia la estrella.
        fun en(t: Float) = Offset(13f + lado * (-0.9f + 8f * sin(t * PI.toFloat() * 0.6f) - 0.9f * t * t), 23.6f - 15.4f * t)
        val tallo = Path().apply {
            val a = en(0f); moveTo(a.x, a.y)
            for (i in 1..4) { val b = en(i / 4f); val m = en((i - 0.5f) / 4f); cubicTo(m.x, m.y, m.x, m.y, b.x, b.y) }
        }
        drawPath(tallo, color, style = Stroke(0.5f, cap = StrokeCap.Round))
        for (i in 0 until 7) {
            val t = 0.22f + i * 0.11f
            val base = en(t); val sigue = en(t + 0.05f) - base
            val l = kotlin.math.sqrt(sigue.x * sigue.x + sigue.y * sigue.y).coerceAtLeast(0.01f)
            val d = sigue / l; val n = Offset(-d.y, d.x)
            for (s in listOf(-1f, 1f)) {
                if (i == 6 && s > 0f) continue
                val punta = base + d * 1.7f + n * (s * 1.05f)
                val m = (base + punta) / 2f; val ancho = Offset(-(punta - base).y, (punta - base).x) * 0.32f
                drawPath(Path().apply { moveTo(base.x, base.y); cubicTo(m.x + ancho.x, m.y + ancho.y, punta.x, punta.y, punta.x, punta.y); cubicTo(punta.x, punta.y, m.x - ancho.x, m.y - ancho.y, base.x, base.y); close() }, color)
            }
        }
        val fin = en(1f); val antes = en(0.95f)
        drawLine(color, fin, fin + (fin - antes) * 3f, 0.3f, StrokeCap.Round)
    }
}
