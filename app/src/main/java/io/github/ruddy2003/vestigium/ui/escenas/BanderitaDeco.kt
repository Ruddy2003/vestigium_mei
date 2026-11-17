package io.github.ruddy2003.vestigium.ui.escenas

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
 * Art déco: un banderín de laca negra con el filete de oro, como el frente del piano y la barra del club de Lou. Arriba,
 * el abanico solar del tema (un medio sol de coral con sus rayos de oro y su arco, como los del friso del club); debajo,
 * una octava de teclas de marfil y laca, las del piano de Lou; abajo termina en la corona escalonada de un rascacielos,
 * tres retranqueos a cada lado, en jade. Cuelga de una barra de oro con remates escalonados. Sin letras ni cifras. Al
 * mantener el dedo se llena de oro desde la punta, y lo que queda bajo el oro pasa a laca.
 * **[Diseño propio]**: el tema de pixel no tiene banderita y el cuento no da ninguna bandera de Lou. Los colores son los
 * del tema (`Disenios.kt`: laca #16130E, oro #A8801E / #D9B44A, jade #1F6B57, coral #B5523B, marfil #F3ECDC) y las
 * formas, las del tema (`Tema.kt`: el abanico solar de la figura de muestra y la corona escalonada de tres retranqueos).
 */
internal object BanderitaDeco : Banderita {
    private val LACA = Color(0xFF16130E)
    private val LACA_LUZ = Color(0xFF3A342B)
    private val ORO = Color(0xFFA8801E)
    private val ORO_CLARO = Color(0xFFD9B44A)
    private val ORO_LUZ = Color(0xFFF2D27A)
    private val JADE = Color(0xFF1F6B57)
    private val JADE_LUZ = Color(0xFF3E9C7E)
    private val CORAL = Color(0xFFB5523B)
    private val CORAL_LUZ = Color(0xFFE48A6E)
    private val MARFIL = Color(0xFFF3ECDC)
    private val MARFIL_OSC = Color(0xFFD6C9A8)

    private const val ARRIBA = 3.4f
    private const val IZQ = 3.4f
    private const val DER = 22.6f
    /** Donde empieza la corona escalonada, y el alto de cada retranqueo. */
    private const val HOMBRO = 28.6f
    private const val PELDANO = 3.1f
    private const val MEDIO = 13f

    /** La silueta: el paño recto y, abajo, tres escalones a cada lado que acaban en una punta roma. */
    private fun silueta(dentro: Float = 0f): Path = Path().apply {
        val e = dentro
        // El lado derecho, de arriba abajo, y luego el izquierdo, que es su espejo, de abajo arriba.
        val derecho = buildList {
            add(Offset(DER - e, ARRIBA + e)); add(Offset(DER - e, HOMBRO - e))
            for (k in 1..3) { add(Offset(DER - 2.6f * k - e, HOMBRO + PELDANO * (k - 1) - e)); add(Offset(DER - 2.6f * k - e, HOMBRO + PELDANO * k - e)) }
        }
        val punta = HOMBRO + PELDANO * 3f + 1.4f - e
        moveTo(derecho[0].x, derecho[0].y)
        for (q in derecho.drop(1)) lineTo(q.x, q.y)
        val xp = derecho.last().x
        lineTo(MEDIO + (xp - MEDIO) * 0.45f, punta); lineTo(MEDIO - (xp - MEDIO) * 0.45f, punta)
        for (q in derecho.reversed()) lineTo(2f * MEDIO - q.x, q.y)
        close()
    }

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val fondo = HOMBRO + 3f * PELDANO + 1.4f
            val nivel = fondo - (fondo - ARRIBA + 0.6f) * lleno.coerceIn(0f, 1f)
            val cuerpo = silueta()
            // La sombra que echa, para que se despegue del techo.
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.3f)) }, Color.Black.copy(alpha = 0.32f))
            clipPath(cuerpo) {
                // La laca: negra, con el brillo largo en diagonal.
                drawRect(Brush.horizontalGradient(listOf(LACA, LACA_LUZ, LACA, LACA), IZQ, DER), Offset(0f, 0f), Size(26f, 40f))
                drawPath(Path().apply { moveTo(IZQ + 5f, ARRIBA); lineTo(IZQ + 8.5f, ARRIBA); lineTo(IZQ - 1f, 26f); lineTo(IZQ - 4.5f, 26f); close() }, Color.White.copy(alpha = 0.07f))
                // La corona escalonada, en jade, con un brillo en el lado de la luz.
                drawRect(Brush.horizontalGradient(listOf(JADE_LUZ, JADE, JADE), IZQ, DER), Offset(0f, HOMBRO), Size(26f, 40f - HOMBRO))
                // El oro que sube desde la punta.
                if (lleno > 0f) drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO_CLARO, ORO), nivel, fondo), Offset(0f, nivel), Size(26f, 40f - nivel))
            }
            dosTintas(nivel) { tinta, clara, sobreOro ->
                // El filete por dentro del canto, siguiendo los escalones.
                drawPath(silueta(1.2f), tinta, style = Stroke(0.5f))
                // La raya que separa el paño de la corona, y la de debajo de las teclas.
                drawLine(tinta, Offset(IZQ + 1.2f, HOMBRO), Offset(DER - 1.2f, HOMBRO), 0.5f)
                // El abanico solar: el medio sol de coral, los rayos (uno largo, uno corto) y el arco que los cierra.
                val c = Offset(MEDIO, 17.4f)
                for (i in 0..10) {
                    val a = (PI + PI * i / 10.0).toFloat()
                    val largo = if (i % 2 == 0) 9f else 6.8f
                    val u = Offset(cos(a), sin(a)); val w = Offset(-u.y, u.x)
                    val r0 = 3.3f
                    val rayo = Path().apply {
                        moveTo(c.x + u.x * r0 + w.x * 0.35f, c.y + u.y * r0 + w.y * 0.35f)
                        lineTo(c.x + u.x * largo + w.x * 0.75f, c.y + u.y * largo + w.y * 0.75f)
                        lineTo(c.x + u.x * largo - w.x * 0.75f, c.y + u.y * largo - w.y * 0.75f)
                        lineTo(c.x + u.x * r0 - w.x * 0.35f, c.y + u.y * r0 - w.y * 0.35f); close()
                    }
                    drawPath(rayo, if (i % 2 == 0) tinta else clara)
                }
                drawArc(tinta, 180f, 180f, false, Offset(c.x - 8.6f, c.y - 8.6f), Size(17.2f, 17.2f), style = Stroke(0.45f))
                val sol = if (sobreOro) tinta else CORAL
                drawArc(sol, 180f, 180f, true, Offset(c.x - 2.7f, c.y - 2.7f), Size(5.4f, 5.4f))
                if (!sobreOro) drawArc(CORAL_LUZ, 200f, 60f, false, Offset(c.x - 1.9f, c.y - 1.9f), Size(3.8f, 3.8f), style = Stroke(0.4f))
                drawLine(tinta, Offset(IZQ + 1.6f, c.y + 0.4f), Offset(DER - 1.6f, c.y + 0.4f), 0.6f)
                // Las teclas: siete de marfil y las cinco negras de una octava.
                val t0 = 19.6f; val t1 = 26.6f; val x0 = 5.4f; val ancho = (20.6f - x0) / 7f
                if (sobreOro) {
                    drawRect(tinta, Offset(x0, t0), Size(ancho * 7f, t1 - t0), style = Stroke(0.4f))
                    for (j in 1..6) drawLine(tinta, Offset(x0 + ancho * j, t0), Offset(x0 + ancho * j, t1), 0.3f)
                } else {
                    drawRect(Brush.verticalGradient(listOf(MARFIL, MARFIL, MARFIL_OSC), t0, t1), Offset(x0, t0), Size(ancho * 7f, t1 - t0))
                    for (j in 1..6) drawLine(MARFIL_OSC, Offset(x0 + ancho * j, t0), Offset(x0 + ancho * j, t1), 0.3f)
                    drawRect(tinta, Offset(x0, t0), Size(ancho * 7f, t1 - t0), style = Stroke(0.35f))
                }
                for (j in listOf(1, 2, 4, 5, 6)) drawRect(LACA, Offset(x0 + ancho * j - ancho * 0.3f, t0), Size(ancho * 0.6f, (t1 - t0) * 0.6f))
                // En la corona: un chevrón por escalón, que baja hacia la punta.
                for (j in 0..2) {
                    val y = HOMBRO + 1.4f + j * PELDANO
                    val m = 6.2f - j * 2f
                    drawPath(Path().apply { moveTo(MEDIO - m, y); lineTo(MEDIO, y + 1.5f); lineTo(MEDIO + m, y) }, tinta, style = Stroke(0.5f, cap = StrokeCap.Round))
                }
            }
            drawPath(cuerpo, ORO.copy(alpha = 0.9f), style = Stroke(0.45f))
            // La barra de oro con sus remates escalonados y las tres anillas que sujetan el paño.
            drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO_CLARO, ORO), 0.6f, 3.6f), Offset(1.2f, 0.9f), Size(23.6f, 2.6f))
            drawLine(Color.White.copy(alpha = 0.35f), Offset(1.6f, 1.4f), Offset(24.4f, 1.4f), 0.4f)
            for (x in listOf(1.1f, 24.9f)) {
                val l = if (x < MEDIO) -1f else 1f
                drawRect(ORO, Offset(x - 1.1f, 0f), Size(2.2f, 4.4f))
                drawRect(ORO_CLARO, Offset(x - 1.1f + (if (l < 0f) 0f else 0.6f), 0.5f), Size(1.6f, 3.4f))
                drawRect(ORO_LUZ, Offset(x - 0.5f, 1f), Size(1f, 2.4f))
            }
            for (x in listOf(6.4f, MEDIO, 19.6f)) drawLine(LACA.copy(alpha = 0.55f), Offset(x, 3.2f), Offset(x, 4.4f), 0.6f, StrokeCap.Round)
        }
    }

    /** Pinta [dibujo] dos veces: en oro por encima de [nivel] y en laca por debajo (donde ya subió el oro). */
    private inline fun DrawScope.dosTintas(nivel: Float, crossinline dibujo: DrawScope.(Color, Color, Boolean) -> Unit) {
        clipRect(0f, 0f, 26f, nivel) { dibujo(ORO_CLARO, ORO, false) }
        if (nivel < 40f) clipRect(0f, nivel, 26f, 40f) { dibujo(LACA, LACA_LUZ, true) }
    }
}
