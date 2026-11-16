package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
 * Abismo: el gallardete del barco de Nikos, hecho banderita. Es «la bandera azul y blanca» que el cuento iza en el palo
 * (la cruz blanca sobre azul arriba y dos franjas blancas, como la de los barcos griegos), cortado abajo en dos puntas,
 * y en medio, de latón, el casco de buzo de tres ventanillas sobre su peto, que es el oficio de los esponjeros de Tarpon
 * Springs y lo que lleva el gato del tema. Cuelga de una verga de madera atada con cáñamo, con remates de latón. Sin
 * letras ni cifras. Al mantener el dedo sube desde las puntas el agua que se enciende de verde «cuando la tocas»
 * (cuento), con su ola y sus burbujas, y lo que queda debajo pasa a azul tinta para que se siga viendo.
 * **[Diseño propio]**: el tema de pixel no tiene banderita; los colores son los del tema (`Disenios.kt`: azul #0A5C8C,
 * tinta #05263A, latón #8C6A1F, el verde agua de noche #5CF2C6) y los del casco del gato de pixel (#C9A24A, #EBD27E,
 * vidrio #A8DCE6).
 */
internal object BanderitaAbismo : Banderita {
    private val AZUL = Color(0xFF0A5C8C)
    private val AZUL_LUZ = Color(0xFF2F80B2)
    private val AZUL_OSC = Color(0xFF063E60)
    private val BLANCO = Color(0xFFF2F0E8)
    private val LATON = Color(0xFFC9A24A)
    private val LATON_LUZ = Color(0xFFEBD27E)
    private val LATON_OSC = Color(0xFF8C6A1F)
    private val VIDRIO = Color(0xFFA8DCE6)
    private val VIDRIO_OSC = Color(0xFF173E52)
    private val TINTA = Color(0xFF05263A)
    private val TINTA_LUZ = Color(0xFF235873)
    private val VERDE = Color(0xFF5CF2C6)
    private val VERDE_HONDO = Color(0xFF1FA58A)
    private val ESPUMA = Color(0xFFE6FFF8)
    private val MADERA = Color(0xFF9A7046)
    private val MADERA_OSC = Color(0xFF4E3420)
    private val CANAMO = Color(0xFFD2BE8E)

    /** Lo alto del paño, sus orillas, las dos puntas y la muesca entre ellas. */
    private const val ARRIBA = 4.2f
    private const val IZQ = 3.6f
    private const val DER = 22.4f
    private const val PUNTA = 38.4f
    private const val MUESCA = 31.8f
    private const val MEDIO = 13f
    /** El paño de la cruz y las dos franjas blancas. */
    private const val CRUZ_ABAJO = 15f
    private const val FRANJA = 1.7f
    private const val FRANJA_BAJA = 29.6f

    /** El paño: cae de la verga, la orilla de la derecha ondula un poco, y abajo se abre en dos puntas. */
    private fun pano(dx: Float = 0f, dy: Float = 0f): Path = Path().apply {
        moveTo(IZQ + dx, ARRIBA + dy); lineTo(DER + dx, ARRIBA + dy)
        cubicTo(DER + 0.6f + dx, 13f + dy, DER - 0.5f + dx, 25f + dy, DER + 0.3f + dx, PUNTA + dy)
        lineTo(MEDIO + 0.4f + dx, MUESCA + dy)
        lineTo(IZQ - 0.2f + dx, PUNTA - 0.6f + dy)
        cubicTo(IZQ + 0.3f + dx, 25f + dy, IZQ - 0.3f + dx, 13f + dy, IZQ + dx, ARRIBA + dy); close()
    }

    /** La ola del agua que sube hasta [nivel], de lado a lado; [cerrada]: con lo que queda debajo de ella. */
    private fun ola(nivel: Float, cerrada: Boolean): Path = Path().apply {
        moveTo(-1f, nivel)
        var x = -1f
        while (x < 27f) {
            val x2 = x + 3f
            cubicTo(x + 1f, nivel - 0.75f, x2 - 1f, nivel - 0.75f, x2, nivel)
            x = x2
        }
        if (cerrada) { lineTo(x, 41f); lineTo(-1f, 41f); close() }
    }

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val l = lleno.coerceIn(0f, 1f)
            val nivel = PUNTA + 0.8f - (PUNTA - ARRIBA + 1.8f) * l
            val cuerpo = pano()
            val agua = ola(nivel, cerrada = true)
            // La sombra que el paño echa detrás, para que se despegue del cielo y del palo.
            drawPath(pano(0.9f, 1.3f), Color.Black.copy(alpha = 0.3f))
            clipPath(cuerpo) {
                drawRect(Brush.horizontalGradient(listOf(AZUL_OSC, AZUL, AZUL_LUZ, AZUL, AZUL_OSC), IZQ, DER), Offset(0f, 0f), Size(26f, 40f))
                // Lo que se ha llenado: el agua que se enciende, más honda abajo, con la espuma de la ola.
                if (l > 0f) drawPath(agua, Brush.verticalGradient(listOf(VERDE, VERDE_HONDO), nivel - 0.8f, PUNTA))
            }
            // La cruz, las franjas y el casco: claros sobre el azul y azul tinta bajo el agua.
            dosTintas(cuerpo, agua, l > 0f) { blanco, sombra, laton, luzLaton, osc, vidrio, brillo ->
                // La cruz blanca del paño de arriba, de brazos iguales, que llega a sus cuatro lados.
                drawRect(blanco, Offset(MEDIO - 1.2f, ARRIBA - 1f), Size(2.4f, CRUZ_ABAJO - ARRIBA + 1f))
                drawRect(blanco, Offset(IZQ - 1f, (ARRIBA + CRUZ_ABAJO) / 2f - 1.2f), Size(DER - IZQ + 2f, 2.4f))
                // Las dos franjas blancas, la de debajo de la cruz y la de encima de las puntas.
                drawRect(blanco, Offset(IZQ - 1f, CRUZ_ABAJO), Size(DER - IZQ + 2f, FRANJA))
                drawRect(blanco, Offset(IZQ - 1f, FRANJA_BAJA), Size(DER - IZQ + 2f, FRANJA))
                // El casco, cosido encima del paño: su sombra corta debajo, y él.
                drawPath(siluetaCasco(Offset(MEDIO + 0.4f, 22.7f)), sombra)
                casco(Offset(MEDIO, 22.2f), laton, luzLaton, osc, vidrio, brillo)
            }
            clipPath(cuerpo) {
                // Los pliegues de la tela: franjas de luz y de sombra que caen a lo largo.
                for ((x, a) in listOf(7.6f to 0.12f, 16.8f to 0.1f)) drawLine(Color.White.copy(alpha = a), Offset(x, ARRIBA), Offset(x + 0.6f, PUNTA), 1.6f)
                for (x in listOf(11f, 20.6f)) drawLine(TINTA.copy(alpha = 0.16f), Offset(x, ARRIBA), Offset(x + 0.4f, PUNTA), 1.2f)
                // El dobladillo: el canto del paño, vuelto y cosido, un poco más oscuro.
                drawPath(cuerpo, AZUL_OSC.copy(alpha = 0.4f), style = Stroke(1.2f))
                // La ola: su cresta de espuma y tres burbujas que suben por el agua.
                if (l > 0f) {
                    drawPath(ola(nivel, cerrada = false), ESPUMA.copy(alpha = 0.85f), style = Stroke(0.45f, cap = StrokeCap.Round))
                    for ((i, b) in listOf(Offset(7f, 4.5f), Offset(15.6f, 9.4f), Offset(10.6f, 15f)).withIndex()) {
                        val y = PUNTA - b.y * (0.35f + 0.65f * l) - 1f
                        if (y > nivel + 1.2f) drawCircle(ESPUMA.copy(alpha = 0.75f), 0.55f + 0.15f * i, Offset(b.x, y), style = Stroke(0.3f))
                    }
                }
            }
            drawPath(cuerpo, TINTA.copy(alpha = 0.5f), style = Stroke(0.45f))
            // La verga de madera, con su veta, los remates de latón y las ligaduras de cáñamo que sujetan el paño.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFC09868), MADERA, MADERA_OSC), 0.8f, 4f), Offset(1f, 0.8f), Size(24f, 3.2f), CornerRadius(1.6f))
            drawLine(Color.White.copy(alpha = 0.2f), Offset(2.4f, 1.5f), Offset(23.6f, 1.5f), 0.45f, StrokeCap.Round)
            drawLine(MADERA_OSC.copy(alpha = 0.5f), Offset(4f, 2.9f), Offset(17f, 2.7f), 0.25f, StrokeCap.Round)
            for (x in listOf(0.9f, 25.1f)) {
                drawCircle(LATON_OSC, 1.8f, Offset(x, 2.4f)); drawCircle(LATON, 1.25f, Offset(x - 0.15f, 2.15f)); drawCircle(LATON_LUZ, 0.45f, Offset(x - 0.4f, 1.8f))
            }
            for (x0 in listOf(4.4f, 20.2f)) for (j in 0..2) {
                val x = x0 + j * 0.62f
                drawLine(MADERA_OSC, Offset(x - 0.35f, 0.7f), Offset(x + 0.35f, 4.1f), 0.6f, StrokeCap.Round)
                drawLine(CANAMO, Offset(x - 0.45f, 0.8f), Offset(x + 0.25f, 4f), 0.42f, StrokeCap.Round)
            }
        }
    }

    /**
     * Pinta [dibujo] dos veces: con sus colores por encima del agua, y en azul tinta y verde hondo por debajo (donde ya
     * subió), para que la cruz, las franjas y el casco se sigan viendo.
     */
    private inline fun DrawScope.dosTintas(
        cuerpo: Path, agua: Path, conAgua: Boolean,
        crossinline dibujo: DrawScope.(Color, Color, Color, Color, Color, Color, Color) -> Unit,
    ) {
        clipPath(cuerpo) {
            val sombra = TINTA.copy(alpha = 0.4f)
            if (conAgua) clipPath(agua, ClipOp.Difference) { dibujo(BLANCO, sombra, LATON, LATON_LUZ, LATON_OSC, VIDRIO_OSC, VIDRIO) }
            else dibujo(BLANCO, sombra, LATON, LATON_LUZ, LATON_OSC, VIDRIO_OSC, VIDRIO)
            if (conAgua) clipPath(agua) { dibujo(TINTA, VERDE_HONDO, TINTA, TINTA_LUZ, TINTA, VERDE_HONDO, ESPUMA) }
        }
    }

    /**
     * El casco de buzo sobre su peto, de frente: la cúpula de latón con su luz arriba a la izquierda, el ojo de buey
     * grande de delante con su aro y sus pernos, los dos chicos de los lados, la válvula arriba y el peto con tres pernos.
     */
    private const val R_CASCO = 4.6f

    /** El peto del casco con centro en [c]: una placa ancha que asoma por debajo de la cúpula. */
    private fun peto(c: Offset): Path = Path().apply {
        moveTo(c.x - 6.2f, c.y + 6.6f); cubicTo(c.x - 5.6f, c.y + 4.4f, c.x - 3.4f, c.y + 3.6f, c.x, c.y + 3.6f)
        cubicTo(c.x + 3.4f, c.y + 3.6f, c.x + 5.6f, c.y + 4.4f, c.x + 6.2f, c.y + 6.6f)
        cubicTo(c.x + 3f, c.y + 7.4f, c.x - 3f, c.y + 7.4f, c.x - 6.2f, c.y + 6.6f); close()
    }

    /** La silueta entera del casco (la cúpula, la válvula y el peto), para su sombra sobre el paño. */
    private fun siluetaCasco(c: Offset): Path = Path().apply {
        addPath(peto(c))
        addOval(Rect(c, R_CASCO))
        addRect(Rect(c.x + 1.2f, c.y - R_CASCO - 1.9f, c.x + 3.2f, c.y - R_CASCO + 0.8f))
    }

    private fun DrawScope.casco(c: Offset, laton: Color, luz: Color, osc: Color, vidrio: Color, brillo: Color) {
        val r = R_CASCO
        // El peto, con su canto y tres pernos.
        val peto = peto(c)
        drawPath(peto, Brush.verticalGradient(listOf(luz, laton, osc), c.y + 3.6f, c.y + 7.4f))
        drawPath(peto, osc, style = Stroke(0.3f))
        for (x in listOf(-4f, 0f, 4f)) drawCircle(luz, 0.42f, Offset(c.x + x, c.y + 6.2f - if (x == 0f) 0.3f else 0f))
        // La válvula de arriba, un poco ladeada, detrás de la cúpula.
        drawRoundRect(osc, Offset(c.x + 1.4f, c.y - r - 1.6f), Size(1.6f, 2.4f), CornerRadius(0.4f))
        drawRect(luz, Offset(c.x + 1.2f, c.y - r - 1.9f), Size(2f, 0.6f))
        // La cúpula, con su luz y la junta del cuello.
        drawCircle(Brush.radialGradient(listOf(luz, laton, osc), Offset(c.x - 1.6f, c.y - 1.9f), r * 1.5f), r, c)
        drawArc(osc, 20f, 140f, false, Offset(c.x - r, c.y - r), Size(r * 2f, r * 2f), style = Stroke(0.35f))
        drawLine(osc, Offset(c.x - 3.2f, c.y + 3.3f), Offset(c.x + 3.2f, c.y + 3.3f), 0.7f, StrokeCap.Round)
        // Los dos ojos de buey de los lados.
        for (l in listOf(-1f, 1f)) {
            val o = Offset(c.x + l * 3.45f, c.y + 0.2f)
            drawOval(vidrio, Offset(o.x - 0.62f, o.y - 0.85f), Size(1.24f, 1.7f))
            drawOval(luz, Offset(o.x - 0.62f, o.y - 0.85f), Size(1.24f, 1.7f), style = Stroke(0.3f))
        }
        // El ojo de buey de delante: el vidrio oscuro, su reflejo, el aro grueso y sus pernos.
        val v = Offset(c.x, c.y + 0.5f)
        drawCircle(vidrio, 2.05f, v)
        drawArc(brillo, 200f, 70f, false, Offset(v.x - 1.4f, v.y - 1.4f), Size(2.8f, 2.8f), style = Stroke(0.4f, cap = StrokeCap.Round))
        drawCircle(luz, 2.35f, v, style = Stroke(0.6f))
        for (i in 0 until 6) {
            val a = (PI / 3 * i + PI / 6).toFloat()
            drawCircle(osc, 0.26f, v + Offset(cos(a), sin(a)) * 2.35f)
        }
        // El ojo de buey chico de arriba.
        drawCircle(vidrio, 0.7f, Offset(c.x - 1.2f, c.y - 3.1f))
        drawCircle(luz, 0.7f, Offset(c.x - 1.2f, c.y - 3.1f), style = Stroke(0.25f))
        // El brillo del latón.
        drawOval(brillo.copy(alpha = 0.6f), Offset(c.x - 3.4f, c.y - 3f), Size(1.6f, 0.8f))
    }
}
