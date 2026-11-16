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
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Laboratorio: la bandera de papel verde de Botánica, la que la doctora cuelga entre los tanques de los jardines el Día
 * de la Fundación (el cuento: «banderas de papel entre los tanques, con los colores de los departamentos. La de Botánica
 * es verde. La cuelga ella»). Es papel de fibra de los jardines hecho a mano, como sus cuadernos: con el canto
 * irregular, sus fibras y los dos dobleces de haberla guardado plegada. Arriba se dobla sobre un cordel y la sujetan
 * dos cintas cian, las de las etiquetas del laboratorio; abajo se corta en curva, «arriba recto, abajo muy redondo»,
 * como la forma del tema. Pintada en crema, una mata de grama que brota de su semilla parda, con las raíces hacia
 * abajo: lo que ella investiga. Sin letras ni cifras, y nada de la química de hoy. Al mantener el dedo sube desde abajo
 * el violeta de los jardines de noche, «la única luz que no imita nada»; la mata, crema, se sigue viendo encima.
 * **[Diseño propio]**: el tema de pixel no tiene banderita; los colores son los del tema (`Disenios.kt`: verde
 * #1E8E5A, cian #0B8FA1, violeta #5E4FD1 / #9D93FF, tinta #12202B) llevados al papel y a la grama del cuadro.
 */
internal object BanderitaLaboratorio : Banderita {
    private val VERDE = Color(0xFF3B9160)
    private val VERDE_LUZ = Color(0xFF5DAE78)
    private val VERDE_OSC = Color(0xFF256B45)
    private val FIBRA_CLARA = Color(0xFF8CC79A)
    private val FIBRA_OSC = Color(0xFF1F5C3B)
    private val CREMA = Color(0xFFF1ECDC)
    private val CREMA_OSC = Color(0xFFCFC6AC)
    private val SEMILLA = Color(0xFF8A5E36)
    private val SEMILLA_LUZ = Color(0xFFC09060)
    private val CORDEL = Color(0xFFD9CDAE)
    private val CORDEL_OSC = Color(0xFF6E5E44)
    private val CIAN = Color(0xFF0B8FA1)
    private val CIAN_LUZ = Color(0xFF5CC3CF)
    private val VIOLETA = Color(0xFF5E4FD1)
    private val VIOLETA_LUZ = Color(0xFF9D93FF)
    private val VIOLETA_FILO = Color(0xFFD2CCFF)
    private val TINTA = Color(0xFF12202B)

    /** Las orillas del papel, dónde empieza la curva de abajo y su punto más bajo. */
    private const val IZQ = 3.4f
    private const val DER = 22.6f
    private const val HOMBRO = 29.2f
    private const val FONDO = 37.6f

    /** El cordel, que se comba un poco entre sus dos nudos: su altura en [x]. */
    private fun cordel(x: Float): Float = 2.1f + 0.55f * sin(PI.toFloat() * ((x - 0.7f) / 24.6f).coerceIn(0f, 1f))

    /** Lo de arriba del papel: el doblez que pasa por encima del cordel. */
    private fun arriba(x: Float): Float = cordel(x) - 0.55f

    /**
     * El papel: arriba sigue el cordel; las orillas, hechas a mano, ondulan un poco (la derecha cae un poco hacia fuera);
     * abajo, la curva cortada con tijera, no del todo pareja.
     */
    private fun papel(dx: Float = 0f, dy: Float = 0f): Path = Path().apply {
        val o = Offset(dx, dy)
        moveTo(Offset(IZQ, arriba(IZQ)) + o)
        for (i in 1..6) { val x = IZQ + (DER - IZQ) * i / 6f; lineTo(Offset(x, arriba(x)) + o) }
        // La orilla derecha, con su canto de papel de tina (pequeñas ondas) y la caída.
        val der = listOf(6.5f to 0.12f, 10.5f to -0.08f, 14.4f to 0.16f, 18.6f to 0.02f, 22.4f to 0.22f, 26f to 0.18f)
        for ((y, x) in der) lineTo(Offset(DER + x + 0.02f * (y - 4f), y) + o)
        lineTo(Offset(DER + 0.62f, HOMBRO) + o)
        // La curva de abajo: dos tramos, el de la derecha algo más largo, con un tijeretazo apenas marcado.
        cubicTo(Offset(DER + 0.4f, 33.4f) + o, Offset(18.4f, FONDO - 0.2f) + o, Offset(13.6f, FONDO) + o)
        lineTo(Offset(12.9f, FONDO - 0.25f) + o)
        cubicTo(Offset(8f, FONDO + 0.1f) + o, Offset(IZQ - 0.3f, 33.6f) + o, Offset(IZQ - 0.1f, HOMBRO) + o)
        val izq = listOf(25.2f to -0.14f, 21f to 0.06f, 16.8f to -0.18f, 12.4f to -0.02f, 8.2f to -0.12f)
        for ((y, x) in izq) lineTo(Offset(IZQ + x, y) + o)
        close()
    }

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val nivel = FONDO - (FONDO - arriba(13f) + 0.4f) * lleno.coerceIn(0f, 1f)
            val hoja = papel()
            // La sombra que el papel echa detrás, y el papel con su luz de arriba a la izquierda.
            drawPath(papel(0.9f, 1.25f), Color.Black.copy(alpha = 0.3f))
            drawPath(hoja, Brush.linearGradient(listOf(VERDE_LUZ, VERDE, VERDE_OSC), Offset(IZQ, 3f), Offset(DER, FONDO)))
            clipPath(hoja) {
                dobleces()
                fibras()
                pestana()
                if (lleno > 0f) {
                    drawRect(Brush.verticalGradient(listOf(VIOLETA_LUZ, VIOLETA), nivel, FONDO), Offset(0f, nivel), Size(26f, FONDO - nivel + 1f))
                    drawLine(VIOLETA_FILO, Offset(0f, nivel + 0.25f), Offset(26f, nivel + 0.25f), 0.5f)
                }
            }
            grama()
            drawPath(hoja, TINTA.copy(alpha = 0.55f), style = Stroke(0.45f))
            cordelYCintas()
        }
    }

    /**
     * Los dos dobleces de haberla guardado plegada en cuatro: uno a lo largo, por la mitad, y otro a lo ancho. Cada uno
     * es un lomo: de un lado le da la luz y del otro queda en sombra.
     */
    private fun DrawScope.dobleces() {
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, VERDE_OSC.copy(alpha = 0.35f)), 13.2f, DER + 1f), Offset(13.2f, 0f), Size(13f, 40f))
        drawLine(VERDE_LUZ.copy(alpha = 0.8f), Offset(12.9f, 4f), Offset(13.1f, FONDO), 0.35f)
        drawLine(VERDE_OSC.copy(alpha = 0.7f), Offset(13.35f, 4f), Offset(13.55f, FONDO), 0.3f)
        drawLine(VERDE_LUZ.copy(alpha = 0.55f), Offset(IZQ, 18.9f), Offset(DER + 1f, 19.3f), 0.3f)
        drawLine(VERDE_OSC.copy(alpha = 0.5f), Offset(IZQ, 19.3f), Offset(DER + 1f, 19.7f), 0.3f)
    }

    /** Las fibras de los jardines que se ven en la pasta del papel, claras y oscuras, y dos briznas pardas. */
    private fun DrawScope.fibras() {
        val f = listOf(
            Triple(5.2f, 9.6f, 0.4f), Triple(8.4f, 13.4f, -0.6f), Triple(17.6f, 9f, 0.2f), Triple(20.4f, 15.6f, -0.3f),
            Triple(6.2f, 22.4f, 0.7f), Triple(18.8f, 24.2f, 0.5f), Triple(10.4f, 31.6f, -0.4f), Triple(16.4f, 33.2f, 0.3f),
            Triple(4.8f, 16.8f, -0.2f), Triple(21f, 27.8f, -0.6f), Triple(9.6f, 6.6f, 0.1f), Triple(15.4f, 14.2f, 0.8f),
        )
        for ((i, fibra) in f.withIndex()) {
            val (x, y, a) = fibra
            val d = Offset(1f, a) / sqrt(1f + a * a) * (0.7f + 0.15f * (i % 4))
            drawLine(if (i % 2 == 0) FIBRA_CLARA.copy(alpha = 0.55f) else FIBRA_OSC.copy(alpha = 0.5f), Offset(x, y) - d, Offset(x, y) + d, 0.2f, StrokeCap.Round)
        }
        drawLine(SEMILLA.copy(alpha = 0.55f), Offset(7.4f, 27.6f), Offset(8.3f, 27.9f), 0.25f, StrokeCap.Round)
        drawLine(SEMILLA.copy(alpha = 0.5f), Offset(19.2f, 6.8f), Offset(19.8f, 7.4f), 0.22f, StrokeCap.Round)
    }

    /** La pestaña de arriba: el papel vuelto sobre el cordel, con su lomo claro y la sombra que deja su orilla. */
    private fun DrawScope.pestana() {
        val orilla = Path().apply {
            moveTo(Offset(0f, cordel(0.7f) + 2.4f))
            for (i in 0..8) { val x = IZQ + (DER - IZQ) * i / 8f; lineTo(Offset(x, cordel(x) + 2.5f + 0.18f * ((i * 7) % 3 - 1))) }
            lineTo(Offset(26f, cordel(25.3f) + 2.4f)); lineTo(Offset(26f, 0f)); lineTo(Offset(0f, 0f)); close()
        }
        drawPath(orilla, Brush.verticalGradient(listOf(VERDE_LUZ, VERDE), 1.5f, 5.4f))
        drawPath(Path().apply { addPath(orilla, Offset(0f, 0.45f)) }, FIBRA_OSC.copy(alpha = 0.35f), style = Stroke(0.35f))
        drawPath(orilla, VERDE_OSC.copy(alpha = 0.8f), style = Stroke(0.25f))
    }

    /**
     * Una hoja de grama: larga, estrecha y en punta, de [base] a [punta] combada hacia [comba], de [ancho] en el pie.
     * Se dibuja por puntos de su nervio, a lo largo, con el ancho que se afina.
     */
    private fun lamina(base: Offset, comba: Offset, punta: Offset, ancho: Float): Path {
        fun en(t: Float): Offset { val u = 1f - t; return base * (u * u) + comba * (2f * u * t) + punta * (t * t) }
        val lados = (0..8).map { i ->
            val t = i / 8f
            val d = en((t + 0.02f).coerceAtMost(1f)) - en((t - 0.02f).coerceAtLeast(0f))
            val l = sqrt(d.x * d.x + d.y * d.y).coerceAtLeast(0.001f)
            Triple(en(t), Offset(-d.y / l, d.x / l), ancho * (1f - t) * (0.55f + 0.45f * (1f - t)))
        }
        return Path().apply {
            moveTo(lados[0].first + lados[0].second * lados[0].third)
            for ((c, n, w) in lados.drop(1)) lineTo(c + n * w)
            for ((c, n, w) in lados.reversed()) lineTo(c - n * w)
            close()
        }
    }

    /** La mata de grama pintada en crema: cinco hojas que brotan de la semilla parda, y las raíces hacia abajo. */
    private fun DrawScope.grama() {
        val pie = Offset(13.1f, 26.4f)
        // Las raíces: tres hilos que bajan de la semilla y se abren, con sus pelillos.
        for ((fin, comba) in listOf(Offset(10.9f, 32.4f) to Offset(12f, 30.4f), Offset(13.6f, 33.6f) to Offset(12.9f, 31f), Offset(15.6f, 31.8f) to Offset(14.6f, 29.8f))) {
            drawPath(Path().apply { moveTo(pie + Offset(0f, 1.6f)); cubicTo(comba, comba, fin) }, CREMA_OSC, style = Stroke(0.32f, cap = StrokeCap.Round))
            val m = (comba + fin) / 2f
            drawLine(CREMA_OSC.copy(alpha = 0.8f), m, m + Offset(if (fin.x < 13f) -0.7f else 0.7f, 0.3f), 0.18f, StrokeCap.Round)
        }
        // Las hojas, finas y de largos distintos, algunas vencidas por la punta: las de atrás en crema sombreado; las de
        // delante en crema, con su nervio.
        val hojas = listOf(
            Triple(Offset(8.2f, 15.2f), Offset(5.2f, 13.4f), 0.8f), Triple(Offset(18.8f, 16f), Offset(20.8f, 14.6f), 0.75f),
            Triple(Offset(16.6f, 21f), Offset(19.4f, 20.2f), 0.6f),
            Triple(Offset(10.2f, 14.6f), Offset(8.4f, 8.6f), 0.95f), Triple(Offset(13.6f, 13.2f), Offset(12.3f, 5.4f), 1f),
            Triple(Offset(15.2f, 14.4f), Offset(17.2f, 8.2f), 0.9f), Triple(Offset(14.6f, 9.2f), Offset(19f, 10.4f), 0.85f),
        )
        for ((i, h) in hojas.withIndex()) {
            val (comba, punta, ancho) = h
            val atras = i < 3
            val base = pie + Offset((comba.x - pie.x) * 0.08f, -0.4f)
            drawPath(lamina(base, comba, punta, ancho), if (atras) CREMA_OSC else CREMA)
            if (!atras) drawPath(Path().apply { moveTo(base); cubicTo(base + (comba - base) * 0.67f, comba + (punta - comba) * 0.4f, comba + (punta - comba) * 0.8f) }, CREMA_OSC.copy(alpha = 0.75f), style = Stroke(0.2f))
        }
        // La semilla parda, lisa, con su brillo.
        drawOval(SEMILLA, Offset(pie.x - 1.25f, pie.y - 0.25f), Size(2.5f, 1.8f))
        drawOval(SEMILLA_LUZ, Offset(pie.x - 0.75f, pie.y + 0.05f), Size(0.9f, 0.5f))
        drawOval(TINTA.copy(alpha = 0.35f), Offset(pie.x - 1.25f, pie.y - 0.25f), Size(2.5f, 1.8f), style = Stroke(0.2f))
    }

    /** El cordel de fibra que pasa por el doblez, con sus nudos en las puntas, y las dos cintas cian que lo sujetan. */
    private fun DrawScope.cordelYCintas() {
        val cuerda = Path().apply { moveTo(Offset(0.7f, cordel(0.7f))); for (i in 1..12) { val x = 0.7f + 24.6f * i / 12f; lineTo(Offset(x, cordel(x))) } }
        drawPath(cuerda, CORDEL_OSC, style = Stroke(0.85f, cap = StrokeCap.Round))
        drawPath(cuerda, CORDEL, style = Stroke(0.45f, cap = StrokeCap.Round))
        // Las vueltas del cordel, a trechos.
        for (i in 0..11) { val x = 1.3f + i * 2.1f; drawLine(CORDEL_OSC.copy(alpha = 0.7f), Offset(x - 0.25f, cordel(x) - 0.3f), Offset(x + 0.25f, cordel(x) + 0.3f), 0.16f) }
        for (x in listOf(0.9f, 25.1f)) {
            drawCircle(CORDEL_OSC, 0.85f, Offset(x, cordel(x))); drawCircle(CORDEL, 0.5f, Offset(x - 0.15f, cordel(x) - 0.15f))
            drawLine(CORDEL, Offset(x, cordel(x) + 0.5f), Offset(x + (if (x < 13f) -0.2f else 0.2f), cordel(x) + 2.4f), 0.32f, StrokeCap.Round)
        }
        for ((x, giro) in listOf(6.6f to -0.35f, 19.4f to 0.3f)) {
            val c = Offset(x, cordel(x) + 1.1f)
            val ax = Offset(1.15f, giro); val ay = Offset(-giro * 0.4f, 1.55f)
            val tira = Path().apply { moveTo(c - ax - ay); lineTo(c + ax - ay); lineTo(c + ax + ay); lineTo(c - ax + ay); close() }
            drawPath(tira, CIAN)
            drawLine(CIAN_LUZ.copy(alpha = 0.8f), c - ax - ay * 0.6f, c + ax - ay * 0.6f, 0.28f)
            drawPath(tira, TINTA.copy(alpha = 0.35f), style = Stroke(0.18f))
        }
    }
}
