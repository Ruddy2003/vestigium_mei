package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Frente exterior
// =====================================================================

/** El gris translúcido de lo que aún no se gana: la misma silueta tenue que la figura sin ganar. */
private fun siluetaFrente(oscuro: Boolean): Color = if (oscuro) Color(0xFFE4E4E4).copy(alpha = 0.13f) else Color(0xFF1E1E1E).copy(alpha = 0.13f)

/** Un rectángulo dado en fracciones del lienzo. */
private fun DrawScope.fracR(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)

/** Un punto dado en fracciones del lienzo. */
private fun DrawScope.fracP(x: Float, y: Float) = Offset(size.width * x, size.height * y)

/** Un polígono cerrado con sus vértices en fracciones del lienzo: x0, y0, x1, y1… */
private fun DrawScope.fracPoli(vararg f: Float): Path = Path().apply {
    var k = 0
    while (k + 1 < f.size) { val x = size.width * f[k]; val y = size.height * f[k + 1]; if (k == 0) moveTo(x, y) else lineTo(x, y); k += 2 }
    close()
}

/** La sombra al pie de la consola, de [x0] a [x1] (fracciones del ancho): dos óvalos bajos, el de adentro más hondo. */
private fun DrawScope.sombraConsola(x0: Float, x1: Float, oscuro: Boolean, y: Float = 0.955f) {
    val w = size.width; val h = size.height
    drawOval(Color.Black.copy(alpha = if (oscuro) 0.32f else 0.15f), Offset(w * x0, h * (y - 0.012f)), Size(w * (x1 - x0), h * 0.03f))
    drawOval(Color.Black.copy(alpha = if (oscuro) 0.22f else 0.1f), Offset(w * (x0 + 0.05f), h * (y - 0.006f)), Size(w * (x1 - x0 - 0.1f), h * 0.016f))
}

/**
 * La geometría de la sala: el lienzo ([w] × [h], en píxeles, con [u] píxeles por dp) se parte en [n] franjas de ancho [paso],
 * una por columna de trofeos, con el trofeo al centro de la suya ([cx]). Las torres de la sala van en los bordes de las
 * franjas ([borde]), con medio ancho [pil], que siempre queda libre entre dos pedestales; arriba, la viga de alto [friso];
 * abajo, el piso desde [suelo]. Lo que se repite por filas usa [modulo] (≈ 230 dp, el alto de una fila del hall).
 */
private class GeoSala(val w: Float, val h: Float, val u: Float, columnas: Int) {
    val n = columnas.coerceIn(1, 6)
    val paso = w / n
    val pil = minOf(paso * 0.12f, 15f * u)
    val friso = minOf(14f * u, h * 0.05f)
    val piso = minOf(16f * u, h * 0.06f)
    val suelo = h - piso
    val modulo = maxOf(230f * u, 1f)
    fun cx(i: Int) = (i + 0.5f) * paso
    fun borde(i: Int) = i * paso
    /** El vano de la franja [i] entre sus dos torres. */
    fun vano(i: Int) = Rect(borde(i) + pil, friso, borde(i + 1) - pil, suelo)
}

/** Repite [f] de [y0] a [y1] con [paso] (con su mínimo de un píxel y a lo más [tope] veces). */
private inline fun repetirSala(y0: Float, y1: Float, paso: Float, tope: Int = 40, f: (k: Int, y: Float) -> Unit) {
    var y = y0; var k = 0
    val p = maxOf(paso, 1f)
    while (y < y1 && k < tope) { f(k, y); y += p; k++ }
}

/** La sombra del marcador en el suelo, bajo [p]: se achica y se aclara cuando brinca ([salto], de 0 a 1). */
private fun DrawScope.sombraBaliza(p: Offset, ancho: Float, salto: Float, oscuro: Boolean) {
    val u = density
    val f = 1f - 0.35f * salto.coerceIn(0f, 1f)
    val a = ancho * f; val b = 3.2f * u * f
    drawOval(Color.Black.copy(alpha = (if (oscuro) 0.5f else 0.2f) * f), Offset(p.x - a / 2, p.y - b / 2), Size(a, b))
    drawOval(Color.Black.copy(alpha = (if (oscuro) 0.3f else 0.12f) * f), Offset(p.x - a * 0.3f, p.y - b * 0.3f), Size(a * 0.6f, b * 0.6f))
}

/**
 * El pie de la escena de victoria: la meta [m] subida [sobre] dp y corrida lo necesario para que una escena de [ancho] ×
 * [alto] (centrada en x, hacia arriba desde el pie) quepa en el lienzo con 2 dp de margen.
 */
private fun DrawScope.pieVictoriaFrente(m: Offset, ancho: Float, alto: Float, sobre: Float = 2f): Offset {
    val u = density; val mg = 2f * u
    val x = if (size.width > ancho + 2 * mg) m.x.coerceIn(ancho / 2 + mg, size.width - ancho / 2 - mg) else size.width / 2
    val y = if (size.height > alto + 2 * mg) (m.y - sobre * u).coerceIn(alto + mg, size.height - mg) else size.height - mg
    return Offset(x, y)
}

/** El grosor de un trazo de los objetos de Tu año: [f] veces el radio [r], pero nunca menos de 0.8 dp. */
private fun DrawScope.grosorFrente(r: Float, f: Float): Float = maxOf(r * f, 0.8f * density)

/** Una creciente: el círculo de centro [c] y radio [r] menos otro de radio r · [k] corrido ([dx], [dy]). */
private fun crecienteFrente(c: Offset, r: Float, dx: Float, dy: Float, k: Float = 0.8f): Path =
    Path.combine(PathOperation.Difference, Path().apply { addOval(Rect(c, r)) }, Path().apply { addOval(Rect(Offset(c.x + dx, c.y + dy), r * k)) })

/**
 * Frente exterior: los logros de la red. Los trofeos son parches de misión bordados e insignias de la red; cada uno descansa
 * en un tramo de la consola curva color hueso de la sala de mando, con su fila de interruptores, su pantalla de radar y la
 * insignia de la red en el metal del nivel. En el hall va sobre un módulo de consola, y su placa es la cinta de nombre
 * bordada, como la de la chaqueta de vuelo.
 */
object LogrosFrente : DisenoLogros {
    private val BAQUELITA = TrazosFrente.BAQUELITA
    private val HUESO = Color(0xFFE9E2D2)
    private val HUESO_LUZ = Color(0xFFF6F1E6)
    private val HUESO_SOMBRA = Color(0xFFB9AF9C)
    private val CROMO = TrazosFrente.CROMO
    private val CROMO_LUZ = Color(0xFFE6E4DE)
    private val CROMO_SOMBRA = Color(0xFF7D7A73)
    private val PANTALLA = TrazosFrente.PANTALLA
    private val TRAZO = TrazosFrente.RADAR
    private val ROJO = TrazosFrente.PILOTO_ROJO
    private val AMBAR = TrazosFrente.PILOTO_AMBAR
    private val PENUMBRA = Color(0xFF121518)
    /** El ladrillo del puerto, donde está escondida la sala. */
    private val LADRILLO = Color(0xFF8A4B36)
    private val CEMENTO = Color(0xFF8C877C)
    /** El mármol del Directorio y su bronce: solo para lo que es del régimen. */
    private val MARMOL = TrazosFrente.MARMOL
    private val BRONCE = TrazosFrente.BRONCE
    /** La sarga petróleo de la cinta de nombre bordada y el hilo hueso de sus letras. */
    private val SARGA = Color(0xFF2E3A40)
    private val HILO = Color(0xFFEFE8D8)
    private val PAPEL = Color(0xFFF2ECDC)
    private val CARTON = Color(0xFFB59D72)

    /** El color en oscuro: la misma sala con las luces bajas. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, PENUMBRA, 0.5f) else c

    /** El tono de la baliza y de la escena de la meta: de noche, apagado un poco, para que se lean sobre el mapa. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, PANTALLA, 0.2f) else c

    /** Un piloto de la consola: el aro de cromo, el vidrio de [luz] (apagado, hundido hacia la baquelita) y, encendido, su halo. */
    private fun DrawScope.piloto(c: Offset, r: Float, luz: Color, encendido: Boolean, oscuro: Boolean) {
        if (encendido) {
            drawCircle(luz.copy(alpha = if (oscuro) 0.2f else 0.14f), r * 2.6f, c)
            drawCircle(luz.copy(alpha = if (oscuro) 0.34f else 0.24f), r * 1.75f, c)
        }
        drawCircle(t(CROMO, oscuro), r * 1.38f, c)
        drawCircle(t(CROMO_SOMBRA, oscuro), r * 1.38f, c, style = Stroke(maxOf(r * 0.2f, 0.3f * density)))
        drawCircle(if (encendido) luz else mezcla(luz, BAQUELITA, if (oscuro) 0.74f else 0.62f), r, c)
        drawCircle(Color.White.copy(alpha = if (encendido) 0.75f else 0.22f), r * 0.32f, Offset(c.x - r * 0.36f, c.y - r * 0.36f))
    }

    /** Una perilla de baquelita: la falda de cromo, la tapa negra con su brillo y la raya índice hacia [angulo] (grados). */
    private fun DrawScope.perilla(c: Offset, r: Float, angulo: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), r, c + Offset(r * 0.12f, r * 0.18f))
        drawCircle(t(CROMO, oscuro), r, c)
        drawCircle(t(CROMO_SOMBRA, oscuro), r, c, style = Stroke(maxOf(r * 0.14f, 0.3f * density)))
        val tapa = r * 0.74f
        drawCircle(t(BAQUELITA, oscuro), tapa, c)
        drawCircle(Color.White.copy(alpha = 0.14f), tapa * 0.5f, Offset(c.x - tapa * 0.3f, c.y - tapa * 0.32f))
        val a = Math.toRadians(angulo.toDouble()).toFloat()
        drawLine(HUESO_LUZ, c + Offset(cos(a), sin(a)) * (tapa * 0.2f), c + Offset(cos(a), sin(a)) * (tapa * 0.92f), maxOf(r * 0.16f, 0.4f * density), StrokeCap.Round)
    }

    /**
     * Una pantalla redonda de radar en [c]: el aro de cromo, el vidrio verde oscuro, la retícula, el barrido en [angulo] con su
     * estela y, si [eco], un contacto recién barrido. De noche, con su resplandor.
     */
    private fun DrawScope.pantallaRadar(c: Offset, r: Float, angulo: Float, oscuro: Boolean, eco: Boolean, grosor: Float) {
        if (oscuro) drawCircle(TRAZO.copy(alpha = 0.16f), r * 1.9f, c)
        drawCircle(t(CROMO, oscuro), r * 1.22f, c)
        drawCircle(t(CROMO_SOMBRA, oscuro), r * 1.22f, c, style = Stroke(grosor))
        drawCircle(PANTALLA, r, c)
        drawCircle(TRAZO.copy(alpha = 0.35f), r * 0.55f, c, style = Stroke(grosor * 0.7f))
        drawLine(TRAZO.copy(alpha = 0.3f), Offset(c.x - r, c.y), Offset(c.x + r, c.y), grosor * 0.6f)
        drawLine(TRAZO.copy(alpha = 0.3f), Offset(c.x, c.y - r), Offset(c.x, c.y + r), grosor * 0.6f)
        for (k in 0 until 5) drawArc(TRAZO.copy(alpha = 0.32f - k * 0.055f), angulo - (k + 1) * 10f, 10f, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        val a = Math.toRadians(angulo.toDouble()).toFloat()
        drawLine(TRAZO, c, Offset(c.x + r * cos(a), c.y + r * sin(a)), grosor * 0.8f, StrokeCap.Round)
        if (eco) {
            val ae = a - 0.6f
            drawCircle(TRAZO, maxOf(r * 0.1f, 0.4f * density), Offset(c.x + r * 0.6f * cos(ae), c.y + r * 0.6f * sin(ae)))
        }
    }

    /**
     * Una chapa en el metal del nivel: la base, el filo de luz arriba, la sombra abajo y su contorno oscuro; la legendaria
     * ([EstadoPedestal.nivel] 4) lleva además su faceta blanca en diagonal.
     */
    private fun DrawScope.chapa(r: Rect, e: EstadoPedestal, radio: Float) {
        if (r.width < 1f || r.height < 1f) return
        val u = density
        val rr = radio.coerceAtMost(minOf(r.width, r.height) / 2)
        drawRoundRect(e.base, r.topLeft, r.size, CornerRadius(rr))
        val filo = (r.height * 0.22f).coerceIn(minOf(0.5f * u, r.height * 0.4f), r.height * 0.4f)
        drawRect(e.brillo, Offset(r.left + rr * 0.6f, r.top + filo * 0.25f), Size(r.width - rr * 1.2f, filo))
        drawRect(e.metalOscuro.copy(alpha = 0.5f), Offset(r.left + rr * 0.6f, r.bottom - filo), Size(r.width - rr * 1.2f, filo * 0.8f))
        drawRoundRect(e.metalOscuro, r.topLeft, r.size, CornerRadius(rr), style = Stroke(maxOf(minOf(r.width, r.height) * 0.08f, 0.45f * u)))
        if (e.nivel >= 4) drawLine(Color.White.copy(alpha = 0.85f), Offset(r.left + r.width * 0.16f, r.bottom - r.height * 0.18f),
            Offset(r.left + r.width * 0.26f, r.top + r.height * 0.18f), maxOf(r.height * 0.08f, 0.5f * u), StrokeCap.Round)
    }

    /**
     * El frente curvo de una consola en [r]: el canto de arriba recto y las esquinas de abajo en curva grande ([curva], en
     * fracción del ancho), como la consola que se dobla hacia el piso.
     */
    private fun frenteCurvo(r: Rect, curva: Float): Path = Path().apply {
        val cw = r.width * curva; val ch = minOf(r.height * 0.55f, cw * 1.4f)
        moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - ch)
        cubicTo(r.right, r.bottom - ch * 0.3f, r.right - cw * 0.35f, r.bottom, r.right - cw, r.bottom)
        lineTo(r.left + cw, r.bottom)
        cubicTo(r.left + cw * 0.35f, r.bottom, r.left, r.bottom - ch * 0.3f, r.left, r.bottom - ch)
        close()
    }

    /** El hueso de un frente de consola: la luz que baja de arriba, la cara de la izquierda iluminada y la de la derecha en sombra. */
    private fun DrawScope.luzConsola(p: Path, r: Rect, oscuro: Boolean) {
        clipPath(p) {
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.06f else 0.38f), Color.Transparent,
                Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f)), r.top, r.bottom), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.22f), r.topLeft, Size(r.width * 0.05f, r.height))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(r.right - r.width * 0.07f, r.top), Size(r.width * 0.07f, r.height))
        }
    }

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * Un tramo de la consola curva de la sala de mando. Arriba, el tablero inclinado con su fila de interruptores de palanca
     * (unos arriba, otros abajo) entre el piloto verde de «en línea» y el ámbar; la moldura de cromo; abajo, el frente color
     * hueso que se dobla hacia el piso, con la ranura grabada, la pantallita de radar con su barrido a la izquierda, la perilla
     * y dos pilotos a la derecha, y al centro la insignia de la red en el metal del nivel; al pie, el zócalo de baquelita.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tapa = fracPoli(0.14f, 0.765f, 0.86f, 0.765f, 0.9f, 0.8f, 0.1f, 0.8f)
        val rf = fracR(0.1f, 0.8f, 0.9f, 0.935f)
        val frente = frenteCurvo(rf, 0.1f)
        val zocalo = fracR(0.2f, 0.93f, 0.8f, 0.955f)
        if (!e.hecho) { drawPath(Path().apply { addPath(tapa); addPath(frente); addRect(zocalo) }, siluetaFrente(oscuro)); return }
        sombraConsola(0.1f, 0.9f, oscuro)
        // El zócalo de baquelita, metido bajo la curva.
        drawRect(t(BAQUELITA, oscuro), zocalo.topLeft, zocalo.size)
        drawRect(Color.White.copy(alpha = 0.08f), zocalo.topLeft, Size(zocalo.width, maxOf(h * 0.003f, 0.4f * u)))
        // El frente curvo.
        drawPath(frente, t(HUESO, oscuro))
        luzConsola(frente, rf, oscuro)
        drawPath(frente, t(HUESO_SOMBRA, oscuro), style = Stroke(lw))
        if (fino) {
            // La ranura grabada bajo la moldura.
            drawLine(Color.Black.copy(alpha = 0.25f), fracP(0.14f, 0.818f), fracP(0.86f, 0.818f), lw * 0.9f)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.5f), fracP(0.14f, 0.818f) + Offset(0f, lw), fracP(0.86f, 0.818f) + Offset(0f, lw), lw * 0.7f)
            // La pantallita de radar de la izquierda.
            pantallaRadar(fracP(0.27f, 0.868f), h * 0.029f, -50f - 120f * azar(e.semilla, 3), oscuro, muyFino, lw)
            // La perilla y los dos pilotos de la derecha.
            perilla(fracP(0.71f, 0.871f), h * 0.02f, 210f + 100f * azar(e.semilla, 4), oscuro)
            piloto(fracP(0.81f, 0.853f), h * 0.0075f, AMBAR, true, oscuro)
            piloto(fracP(0.81f, 0.888f), h * 0.0075f, ROJO, false, oscuro)
            if (muyFino) listOf(0.15f, 0.85f).forEach { x ->
                val c = fracP(x, 0.9f)
                drawCircle(t(CROMO, oscuro), h * 0.0055f, c)
                drawCircle(t(CROMO_SOMBRA, oscuro), h * 0.0055f, c, style = Stroke(lw * 0.6f))
            }
        }
        // La insignia de la red, en el metal del nivel, al centro del frente.
        val c = fracP(0.5f, 0.868f); val r = h * 0.033f
        drawCircle(Color.Black.copy(alpha = 0.2f), r, c + Offset(r * 0.12f, r * 0.16f))
        drawCircle(e.base, r, c)
        drawCircle(e.brillo, r * 0.8f, c, style = Stroke(maxOf(r * 0.1f, 0.4f * u)))
        drawCircle(e.metalOscuro, r, c, style = Stroke(maxOf(r * 0.12f, 0.45f * u)))
        if (fino) with(TrazosFrente) { emblemaRed(c, r * 0.48f, e.metalOscuro, maxOf(r * 0.1f, 0.5f * u)) }
        else drawCircle(e.metalOscuro, r * 0.3f, c)
        if (e.nivel >= 4 && fino) drawLine(Color.White.copy(alpha = 0.85f), Offset(c.x - r * 0.62f, c.y + r * 0.1f), Offset(c.x - r * 0.18f, c.y - r * 0.6f),
            maxOf(r * 0.12f, 0.5f * u), StrokeCap.Round)
        // El tablero de arriba, con su luz, y la moldura de cromo.
        drawPath(tapa, t(HUESO_LUZ, oscuro))
        drawPath(tapa, t(HUESO_SOMBRA, oscuro), style = Stroke(lw * 0.8f))
        drawRect(t(CROMO, oscuro), fracP(0.1f, 0.795f), Size(w * 0.8f, h * 0.008f))
        drawRect(t(CROMO_LUZ, oscuro), fracP(0.1f, 0.795f), Size(w * 0.8f, maxOf(h * 0.002f, 0.4f * u)))
        if (fino) {
            // La fila de interruptores de palanca, entre los dos pilotos.
            val cuantos = if (muyFino) 7 else 5
            for (k in 0 until cuantos) {
                val x = 0.27f + k * (0.46f / (cuantos - 1))
                val arriba = azar(e.semilla, k + 20) > 0.45f
                val base = fracP(x, 0.784f)
                val punta = Offset(base.x, base.y + (if (arriba) -h * 0.012f else h * 0.007f))
                drawCircle(t(CROMO_SOMBRA, oscuro), h * 0.0045f, base)
                drawLine(Color.Black.copy(alpha = 0.18f), base + Offset(h * 0.002f, h * 0.002f), punta + Offset(h * 0.003f, h * 0.003f), maxOf(h * 0.004f, 0.5f * u), StrokeCap.Round)
                drawLine(t(CROMO, oscuro), base, punta, maxOf(h * 0.0035f, 0.5f * u), StrokeCap.Round)
                drawCircle(t(CROMO_LUZ, oscuro), maxOf(h * 0.003f, 0.4f * u), punta)
            }
            piloto(fracP(0.19f, 0.783f), h * 0.005f, TRAZO, true, oscuro)
            piloto(fracP(0.81f, 0.783f), h * 0.005f, AMBAR, azar(e.semilla, 30) > 0.3f, oscuro)
        }
    }

    /**
     * El módulo de consola del hall: el tablero inclinado con su hilera de palancas, la moldura de cromo y el frente curvo con
     * la pantallita de radar, la chapa del nivel con el emblema de la red al centro y los dos pilotos de la derecha.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val tapa = fracPoli(0.08f, 0f, 0.92f, 0f, 0.97f, 0.34f, 0.03f, 0.34f)
        val rf = fracR(0.03f, 0.34f, 0.97f, 1f)
        val frente = frenteCurvo(rf, 0.08f)
        if (!e.hecho) { drawPath(Path().apply { addPath(tapa); addPath(frente) }, siluetaFrente(oscuro)); return }
        // El frente curvo, con su luz.
        drawPath(frente, t(HUESO, oscuro))
        luzConsola(frente, rf, oscuro)
        drawPath(frente, t(HUESO_SOMBRA, oscuro), style = Stroke(lw * 0.6f))
        // La pantallita de radar.
        pantallaRadar(fracP(0.17f, 0.66f), h * 0.19f, -60f - 90f * azar(e.semilla, 5), oscuro, false, lw * 0.6f)
        // La chapa del nivel, con el emblema.
        val pl = fracR(0.37f, 0.47f, 0.63f, 0.88f)
        chapa(pl, e, h * 0.08f)
        with(TrazosFrente) { emblemaRed(pl.center, pl.height * 0.27f, e.metalOscuro, maxOf(lw * 0.6f, 0.4f * u)) }
        // Los pilotos de la derecha y su perilla.
        piloto(fracP(0.74f, 0.6f), h * 0.07f, AMBAR, true, oscuro)
        piloto(fracP(0.84f, 0.6f), h * 0.07f, ROJO, false, oscuro)
        drawLine(Color.Black.copy(alpha = 0.22f), fracP(0.7f, 0.82f), fracP(0.88f, 0.82f), lw * 0.6f)
        // El tablero inclinado, la moldura y la hilera de palancas.
        drawPath(tapa, t(HUESO_LUZ, oscuro))
        drawPath(tapa, t(HUESO_SOMBRA, oscuro), style = Stroke(lw * 0.5f))
        drawRect(t(CROMO, oscuro), fracP(0.03f, 0.31f), Size(w * 0.94f, h * 0.07f))
        drawRect(t(CROMO_LUZ, oscuro), fracP(0.03f, 0.31f), Size(w * 0.94f, maxOf(h * 0.02f, 0.4f * u)))
        for (k in 0 until 6) {
            val base = fracP(0.22f + k * 0.112f, 0.2f)
            val arriba = azar(e.semilla + 7, k) > 0.5f
            val punta = Offset(base.x, base.y + (if (arriba) -h * 0.13f else h * 0.07f))
            drawCircle(t(CROMO_SOMBRA, oscuro), h * 0.045f, base)
            drawLine(t(CROMO, oscuro), base, punta, maxOf(h * 0.035f, 0.5f * u), StrokeCap.Round)
            drawCircle(t(CROMO_LUZ, oscuro), maxOf(h * 0.03f, 0.4f * u), punta)
        }
        piloto(fracP(0.12f, 0.17f), h * 0.05f, TRAZO, true, oscuro)
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = HILO
    override fun rotuloPlaca(oscuro: Boolean): Color = AMBAR

    /**
     * La cinta de nombre bordada, como la de la chaqueta de vuelo: la sarga petróleo con su trama en diagonal, el borde
     * sobrehilado en el hilo del nivel (las puntadas apretadas y ladeadas, con el brillo del hilo) y el pespunte que la cose,
     * por dentro.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val radio = minOf(6 * u, h / 2, w / 2)
        if (!e.hecho) { drawRoundRect(siluetaFrente(oscuro), Offset.Zero, size, CornerRadius(radio)); return }
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(radio))) }
        drawPath(forma, if (oscuro) mezcla(SARGA, PENUMBRA, 0.2f) else SARGA)
        // La trama de la sarga, en diagonal.
        clipPath(forma) {
            val trama = Path()
            var x = -h; var k = 0
            while (x < w && k < 500) { trama.moveTo(x, h); trama.lineTo(x + h, 0f); x += maxOf(2.2f * u, 1f); k++ }
            drawPath(trama, Color.White.copy(alpha = 0.05f), style = Stroke(0.6f * u))
        }
        // El borde sobrehilado: la cinta de hilo del nivel…
        val b = minOf(2.8f * u, h * 0.2f)
        drawPath(Path().apply { addRoundRect(RoundRect(Rect(b / 2, b / 2, w - b / 2, h - b / 2), CornerRadius(maxOf(radio - b / 2, 0.5f * u)))) }, e.base, style = Stroke(b))
        // …con sus puntadas ladeadas por los cuatro cantos rectos…
        val puntadas = Path()
        val paso = maxOf(1.3f * u, 1f); val lado = 0.9f * u; val m = 0.3f * u
        var x = radio; var j = 0
        while (x < w - radio && j < 600) {
            puntadas.moveTo(x, m); puntadas.lineTo(x + lado, b - m)
            puntadas.moveTo(x, h - b + m); puntadas.lineTo(x + lado, h - m)
            x += paso; j++
        }
        var y = radio; j = 0
        while (y < h - radio && j < 300) {
            puntadas.moveTo(m, y); puntadas.lineTo(b - m, y + lado)
            puntadas.moveTo(w - b + m, y); puntadas.lineTo(w - m, y + lado)
            y += paso; j++
        }
        drawPath(puntadas, e.metalOscuro.copy(alpha = 0.55f), style = Stroke(0.5f * u))
        // …y el brillo del hilo, arriba.
        drawLine(e.brillo.copy(alpha = 0.8f), Offset(radio, b * 0.35f), Offset(w - radio, b * 0.35f), 0.5f * u)
        // El pespunte de adentro.
        val p = b + 1.2f * u
        if (w > 2 * p && h > 2 * p) drawPath(Path().apply { addRoundRect(RoundRect(Rect(p, p, w - p, h - p), CornerRadius(maxOf(radio - p, 0.5f * u)))) },
            e.base.copy(alpha = 0.55f), style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.2f * u))))
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * La sala de mando escondida, en el sótano de ladrillo del puerto. El muro es de ladrillo trabado, con algún ladrillo más
     * quemado; en el fondo de cada vano cuelga, clavado con chinches rojas, un mapa estelar impreso, doblado en cuatro, con su
     * cuadrícula, las rutas de salto punteadas y el blanco marcado, o se ve el emblema de la red pintado con plantilla, con
     * sus chorreones. Entre los vanos se levantan las torres de cómputo color hueso, de remate curvo: arriba, su pantalla
     * redonda de radar; luego, por cada tramo, la ventanilla de las dos bobinas de cinta, la fila de pilotos, la de palancas
     * y la rejilla. Arriba corre la viga de cemento con el haz de cables colgando entre sus ganchos y una lámpara de pantalla
     * verde sobre cada vano; al centro, el piloto rojo de «en el aire». Abajo, en cada vano, la consola curva con su tablero
     * de palancas y pilotos, y el piso de cemento con sus juntas. De día, las lámparas echan su luz cálida hacia abajo; de
     * noche, son la misma sala en la ventana de transmisión: las lámparas bajas, el piloto rojo encendido y la luz verde de
     * las pantallas.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSala(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaSala(g, e, oscuro) }
    }

    private fun DrawScope.escenaSala(g: GeoSala, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        /** El color [c] mezclado con el fondo: [k] = 0 es el fondo, 1 el color tal cual. */
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)
        val hueso = m(HUESO, if (oscuro) 0.26f else 0.62f)
        val huesoLuz = m(HUESO_LUZ, if (oscuro) 0.3f else 0.75f)
        val huesoSombra = m(HUESO_SOMBRA, if (oscuro) 0.3f else 0.55f)
        val cromo = m(CROMO, if (oscuro) 0.32f else 0.55f)
        val vidrio = m(PANTALLA, if (oscuro) 0.85f else 0.45f)
        val traza = TRAZO.copy(alpha = if (oscuro) 0.75f else 0.5f)
        val ambar = m(AMBAR, if (oscuro) 0.85f else 0.6f)
        val rojo = m(ROJO, if (oscuro) 0.8f else 0.5f)

        // El muro de ladrillo trabado, con algún ladrillo más quemado.
        drawRect(m(LADRILLO, if (oscuro) 0.16f else 0.1f), Offset(0f, g.friso), Size(w, g.suelo - g.friso))
        val pared = Path(); val quemados = Path()
        val altoL = 10f * u; val largoL = 24f * u
        repetirSala(g.friso, g.suelo, altoL, 200) { k, y ->
            val y1 = minOf(y + altoL, g.suelo)
            pared.moveTo(0f, y); pared.lineTo(w, y)
            var x = if (k % 2 == 0) 0f else -largoL / 2; var j = 0
            while (x < w && j < 80) {
                pared.moveTo(x, y); pared.lineTo(x, y1)
                if (azar(e.semilla + 5, k * 83 + j) > 0.8f) quemados.addRect(Rect(maxOf(x, 0f) + 0.8f * u, y + 0.8f * u, x + largoL - 0.8f * u, y1 - 0.8f * u))
                x += largoL; j++
            }
        }
        drawPath(quemados, m(LADRILLO, if (oscuro) 0.26f else 0.18f))
        drawPath(pared, m(Color(0xFF6E5246), if (oscuro) 0.3f else 0.2f), style = Stroke(0.8f * u))

        // El fondo de cada vano: el mapa estelar clavado o el emblema de la red pintado con plantilla, alternados por tramo.
        for (i in 0 until g.n) {
            val v = g.vano(i)
            repetirSala(v.top + 24f * u, v.bottom - 70f * u, g.modulo, 10) { k, y0 ->
                val cx = g.cx(i)
                if ((k + i) % 2 == 0) {
                    val mw = minOf(v.width * 0.82f, 130f * u); val mh = mw * 0.66f
                    mapaEstelar(Rect(cx - mw / 2, y0, cx + mw / 2, y0 + mh), -2.5f + 5f * azar(e.semilla + i, k), e.semilla + i * 13 + k,
                        m(PAPEL, if (oscuro) 0.2f else 0.6f), m(Color(0xFF3E4C57), if (oscuro) 0.32f else 0.26f), rojo, ambar, oscuro)
                } else {
                    val r = minOf(v.width * 0.26f, 34f * u)
                    val c = Offset(cx, y0 + r + 14f * u)
                    val pintura = m(ROJO, if (oscuro) 0.22f else 0.16f)
                    with(TrazosFrente) {
                        emblemaRed(c, r, m(ROJO, if (oscuro) 0.1f else 0.07f), r * 0.32f)
                        emblemaRed(c, r, pintura, r * 0.15f)
                    }
                    listOf(-0.35f to 0.5f, 0.12f to 0.8f, 0.5f to 0.35f).forEach { (dx, l) ->
                        val x = c.x + dx * r
                        val y = c.y + sqrt(maxOf(r * r - (dx * r) * (dx * r), 0f))
                        drawLine(pintura, Offset(x, y), Offset(x, y + l * r), r * 0.05f, StrokeCap.Round)
                        drawCircle(pintura, r * 0.05f, Offset(x, y + l * r))
                    }
                }
            }
        }

        // De día, la luz cálida de las lámparas, en cono hacia abajo (un solo degradado).
        if (!oscuro) {
            val conos = Path()
            for (i in 0 until g.n) {
                val c = g.cx(i); val v = g.vano(i)
                conos.moveTo(c - 9f * u, g.friso + 9f * u); conos.lineTo(c + 9f * u, g.friso + 9f * u)
                conos.lineTo(minOf(c + v.width * 0.5f, v.right), g.friso + 180f * u); conos.lineTo(maxOf(c - v.width * 0.5f, v.left), g.friso + 180f * u); conos.close()
            }
            drawPath(conos, Brush.verticalGradient(listOf(Color(0xFFFFE6B0).copy(alpha = 0.32f), Color(0xFFFFE6B0).copy(alpha = 0f)), g.friso + 9f * u, g.friso + 180f * u))
        }

        // Las torres de cómputo entre los vanos (y medias en los bordes).
        for (i in 0..g.n) {
            val x = g.borde(i); val a = g.pil
            val arriba = g.friso + 4f * u
            val caja = Rect(x - a, arriba, x + a, g.suelo)
            val curva = minOf(a, 12f * u)
            val torre = Path().apply { addRoundRect(RoundRect(caja.left, caja.top, caja.right, caja.bottom, CornerRadius(curva), CornerRadius(curva), CornerRadius.Zero, CornerRadius.Zero)) }
            // Su sombra en el muro, a la derecha (la luz viene de arriba a la izquierda).
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.07f), Offset(caja.right, caja.top + 8f * u), Size(4f * u, caja.height))
            drawPath(torre, hueso)
            clipPath(torre) {
                drawRect(huesoLuz, caja.topLeft, Size(2f * u, caja.height))
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), Offset(caja.right - 3f * u, caja.top), Size(3f * u, caja.height))
            }
            drawPath(torre, huesoSombra, style = Stroke(0.9f * u))
            // La pantalla de radar del remate (de noche, con su resplandor: un degradado por torre).
            val rr = minOf(a * 0.68f, 10f * u)
            val rc = Offset(x, arriba + curva * 0.6f + rr + 3f * u)
            if (oscuro) drawCircle(Brush.radialGradient(listOf(TRAZO.copy(alpha = 0.14f), Color.Transparent), rc, rr * 5f), rr * 5f, rc)
            drawCircle(cromo, rr * 1.25f, rc)
            drawCircle(vidrio, rr, rc)
            drawCircle(traza.copy(alpha = traza.alpha * 0.45f), rr * 0.55f, rc, style = Stroke(0.6f * u))
            drawLine(traza.copy(alpha = traza.alpha * 0.4f), Offset(rc.x - rr, rc.y), Offset(rc.x + rr, rc.y), 0.5f * u)
            drawLine(traza.copy(alpha = traza.alpha * 0.4f), Offset(rc.x, rc.y - rr), Offset(rc.x, rc.y + rr), 0.5f * u)
            val ang = 360f * azar(e.semilla + 40, i)
            for (k in 0 until 4) drawArc(traza.copy(alpha = traza.alpha * (0.5f - k * 0.11f)), ang - (k + 1) * 12f, 12f, true, Offset(rc.x - rr, rc.y - rr), Size(2 * rr, 2 * rr))
            // Los tramos de la torre: las bobinas, los pilotos, las palancas y la rejilla.
            repetirSala(rc.y + rr + 14f * u, g.suelo - 60f * u, g.modulo, 10) { k, y0 ->
                val rb = minOf(a * 0.62f, 8.5f * u)
                val vent = Rect(x - a + 3f * u, y0, x + a - 3f * u, y0 + 4 * rb + 9f * u)
                drawRoundRect(vidrio.copy(alpha = if (oscuro) 0.9f else 0.55f), vent.topLeft, vent.size, CornerRadius(3f * u))
                drawRoundRect(cromo, vent.topLeft, vent.size, CornerRadius(3f * u), style = Stroke(1f * u))
                listOf(0.25f + 0.5f * azar(e.semilla + i, k), 0.85f - 0.4f * azar(e.semilla + i, k)).forEachIndexed { j, lleno ->
                    bobina(Offset(x, y0 + 3f * u + rb + j * (2 * rb + 3f * u)), rb, 40f * azar(e.semilla + 77 + i, k + j), lleno,
                        m(CROMO_LUZ, if (oscuro) 0.4f else 0.8f), m(Color(0xFF6B4A2E), if (oscuro) 0.55f else 0.5f), vidrio)
                }
                // Los pilotos, encendidos al azar (más vivos de noche).
                val yp = vent.bottom + 7f * u
                for (j in 0 until 3) {
                    val p = Offset(x - a * 0.55f + j * a * 0.55f, yp)
                    val col = if (j == 1) ROJO else AMBAR
                    val prendido = azar(e.semilla + 90 + i, k * 3 + j) > 0.4f
                    if (prendido && oscuro) drawCircle(col.copy(alpha = 0.22f), 3.6f * u, p)
                    drawCircle(cromo, 1.9f * u, p)
                    drawCircle(if (prendido) m(col, if (oscuro) 0.95f else 0.7f) else m(BAQUELITA, 0.4f), 1.3f * u, p)
                }
                // Las palancas.
                val ypa = yp + 9f * u
                for (j in 0 until 3) {
                    val p = Offset(x - a * 0.55f + j * a * 0.55f, ypa)
                    val sube = azar(e.semilla + 120 + i, k * 3 + j) > 0.5f
                    drawCircle(cromo, 1.6f * u, p)
                    drawLine(cromo, p, Offset(p.x, p.y + (if (sube) -4.5f else 3.5f) * u), 1.2f * u, StrokeCap.Round)
                    drawCircle(huesoLuz, 0.9f * u, Offset(p.x, p.y + (if (sube) -4.5f else 3.5f) * u))
                }
                // La rejilla de ventilación.
                for (j in 0 until 4) {
                    val yr = ypa + 9f * u + j * 3.4f * u
                    drawRoundRect(huesoSombra, Offset(x - a * 0.6f, yr), Size(a * 1.2f, 1.6f * u), CornerRadius(0.8f * u))
                }
            }
        }

        // La viga de cemento, con su canto de sombra.
        drawRect(m(CEMENTO, if (oscuro) 0.4f else 0.42f), Offset.Zero, Size(w, g.friso))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(0f, g.friso - 2f * u), Size(w, 2f * u))
        // El haz de cables que cuelga entre los ganchos.
        val cables = Path()
        val tramo = 60f * u
        var xg = 0f; var kg = 0
        while (xg < w && kg < 40) {
            listOf(0f, 1.6f, 3.2f).forEach { d ->
                cables.moveTo(xg, g.friso + d * u * 0.3f)
                cables.quadraticTo(xg + tramo / 2, g.friso + (5f + d) * u, xg + tramo, g.friso + d * u * 0.3f)
            }
            drawLine(m(BAQUELITA, if (oscuro) 0.5f else 0.45f), Offset(xg, g.friso * 0.6f), Offset(xg, g.friso + 1f * u), 1f * u)
            xg += tramo; kg++
        }
        drawPath(cables, m(BAQUELITA, if (oscuro) 0.55f else 0.4f), style = Stroke(1.1f * u))
        // La lámpara de pantalla verde sobre cada vano: encendida de día; de noche, baja, con su foquito tenue.
        val esmalte = m(Color(0xFF3F5A48), if (oscuro) 0.6f else 0.7f)
        for (i in 0 until g.n) {
            val c = g.cx(i)
            drawLine(m(BAQUELITA, 0.6f), Offset(c, 0f), Offset(c, g.friso + 2f * u), 0.8f * u)
            val pantalla = Path().apply {
                moveTo(c - 9f * u, g.friso + 9f * u); cubicTo(c - 8f * u, g.friso + 3f * u, c - 4f * u, g.friso + 2f * u, c, g.friso + 2f * u)
                cubicTo(c + 4f * u, g.friso + 2f * u, c + 8f * u, g.friso + 3f * u, c + 9f * u, g.friso + 9f * u); close()
            }
            drawPath(pantalla, esmalte)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.35f), Offset(c - 6f * u, g.friso + 4.5f * u), Offset(c - 2f * u, g.friso + 3f * u), 0.8f * u, StrokeCap.Round)
            val foco = Offset(c, g.friso + 9.5f * u)
            if (oscuro) {
                drawCircle(Color(0xFFFFD9A0).copy(alpha = 0.12f), 6f * u, foco)
                drawCircle(Color(0xFFFFD9A0).copy(alpha = 0.5f), 1.8f * u, foco)
            } else drawCircle(Color(0xFFFFF4D8), 2.4f * u, foco)
            drawLine(m(HUESO_LUZ, if (oscuro) 0.35f else 0.9f), Offset(c - 9f * u, g.friso + 9f * u), Offset(c + 9f * u, g.friso + 9f * u), 0.9f * u)
        }
        // El piloto de «en el aire», al centro de la viga: rojo encendido de noche, en la ventana de transmisión.
        val aire = Rect(w / 2 - 9f * u, g.friso * 0.18f, w / 2 + 9f * u, g.friso * 0.82f)
        drawRoundRect(m(BAQUELITA, if (oscuro) 0.8f else 0.6f), aire.topLeft, aire.size, CornerRadius(2f * u))
        val lente = aire.deflate(1.6f * u)
        if (oscuro) drawCircle(ROJO.copy(alpha = 0.18f), 14f * u, aire.center)
        drawRoundRect(if (oscuro) ROJO else m(ROJO, 0.35f), lente.topLeft, lente.size, CornerRadius(1.2f * u))
        for (j in 1 until 4) drawLine(Color.Black.copy(alpha = 0.25f), Offset(lente.left + lente.width * j / 4f, lente.top), Offset(lente.left + lente.width * j / 4f, lente.bottom), 0.5f * u)

        // El piso de cemento con sus juntas.
        drawRect(m(CEMENTO, if (oscuro) 0.3f else 0.32f), Offset(0f, g.suelo), Size(w, h - g.suelo))
        val juntas = Path()
        var xj = 20f * u; var kj = 0
        while (xj < w && kj < 60) { juntas.moveTo(xj, g.suelo); juntas.lineTo(xj - 8f * u, h); xj += 48f * u; kj++ }
        drawPath(juntas, m(BAQUELITA, 0.2f), style = Stroke(0.8f * u))
        // La consola curva de cada vano, al pie: el tablero con sus palancas y pilotos y el frente que se dobla hacia el piso.
        for (i in 0 until g.n) {
            val v = g.vano(i)
            val r = Rect(v.left + 5f * u, g.suelo + 1f * u, v.right - 5f * u, h)
            if (r.width < 30f * u || r.height < 6f * u) continue
            val tablero = Rect(r.left + 3f * u, r.top, r.right - 3f * u, r.top + r.height * 0.32f)
            drawRect(huesoLuz, tablero.topLeft, tablero.size)
            val frente = frenteCurvo(Rect(r.left, tablero.bottom, r.right, r.bottom), 0.06f)
            drawPath(frente, hueso)
            drawPath(frente, huesoSombra, style = Stroke(0.8f * u))
            drawRect(cromo, Offset(r.left, tablero.bottom - 0.6f * u), Size(r.width, 1.4f * u))
            // Las palancas y los pilotos del tablero.
            var xp = tablero.left + 8f * u; var j = 0
            while (xp < tablero.right - 8f * u && j < 40) {
                val c = Offset(xp, tablero.center.y)
                if (j % 4 == 3) {
                    val prendido = azar(e.semilla + 150 + i, j) > 0.35f
                    if (prendido && oscuro) drawCircle(AMBAR.copy(alpha = 0.22f), 3f * u, c)
                    drawCircle(if (prendido) ambar else m(BAQUELITA, 0.4f), 1.2f * u, c)
                } else {
                    drawCircle(cromo, 0.9f * u, c)
                    drawLine(cromo, c, Offset(c.x, c.y - (if (azar(e.semilla + 160 + i, j) > 0.5f) 2.2f else -1.4f) * u), 0.9f * u, StrokeCap.Round)
                }
                xp += 6f * u; j++
            }
            // El frente, con su ranura grabada y una rejilla al centro.
            val yf = tablero.bottom + (r.bottom - tablero.bottom) * 0.45f
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.15f), Offset(r.left + 8f * u, yf), Offset(r.right - 8f * u, yf), 0.7f * u)
            for (k in 0 until 3) drawRoundRect(huesoSombra, Offset(g.cx(i) - 12f * u, yf + 2f * u + k * 2.4f * u), Size(24f * u, 1.2f * u), CornerRadius(0.6f * u))
        }
        // Los cables del piso, de cada torre a la consola de al lado.
        val piso = Path()
        for (i in 0..g.n) {
            val x = g.borde(i)
            val y = g.suelo + (h - g.suelo) * 0.75f
            piso.moveTo(x - g.pil * 0.4f, g.suelo); piso.cubicTo(x - g.pil * 0.4f, y, x - g.pil - 6f * u, y, x - g.pil - 10f * u, y + 1f * u)
            piso.moveTo(x + g.pil * 0.4f, g.suelo); piso.cubicTo(x + g.pil * 0.4f, y, x + g.pil + 6f * u, y, x + g.pil + 10f * u, y + 1f * u)
        }
        drawPath(piso, m(BAQUELITA, if (oscuro) 0.6f else 0.45f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
    }

    /**
     * Un mapa estelar impreso, clavado en el muro, en [r] girado [giro] grados: el papel con su sombra, el doblez en cuatro,
     * la cuadrícula y el marco impresos, las estrellas, los mundos unidos por la ruta de salto punteada, el blanco marcado en
     * rojo con su cruz y una chinche roja en cada esquina.
     */
    private fun DrawScope.mapaEstelar(r: Rect, giro: Float, semilla: Int, papel: Color, tinta: Color, rojo: Color, ambar: Color, oscuro: Boolean) {
        val u = density
        rotate(giro, r.center) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.07f), r.topLeft + Offset(2f * u, 3f * u), r.size)
            drawRect(papel, r.topLeft, r.size)
            // El doblez: la sombra de una mitad y la luz de la otra.
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.1f else 0.04f), Offset(r.center.x, r.top), Size(r.width / 2, r.height / 2))
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.15f else 0.06f), Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 0.7f * u)
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.12f else 0.05f), Offset(r.left, r.center.y), Offset(r.right, r.center.y), 0.7f * u)
            // El marco y la cuadrícula impresos.
            val dentro = r.deflate(4f * u)
            drawRect(tinta, dentro.topLeft, dentro.size, style = Stroke(0.7f * u))
            val rejilla = Path()
            for (k in 1 until 6) { val x = dentro.left + dentro.width * k / 6f; rejilla.moveTo(x, dentro.top); rejilla.lineTo(x, dentro.bottom) }
            for (k in 1 until 4) { val y = dentro.top + dentro.height * k / 4f; rejilla.moveTo(dentro.left, y); rejilla.lineTo(dentro.right, y) }
            drawPath(rejilla, tinta.copy(alpha = tinta.alpha * 0.45f), style = Stroke(0.5f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f * u, 1.6f * u))))
            // Las estrellas.
            val estrellas = Path()
            for (k in 0 until 22) {
                val p = Offset(dentro.left + dentro.width * azar(semilla, k), dentro.top + dentro.height * azar(semilla, k + 40))
                estrellas.addOval(Rect(p, (0.4f + 0.6f * azar(semilla, k + 80)) * u))
            }
            drawPath(estrellas, tinta)
            // Los mundos y la ruta de salto entre ellos; el último es el blanco.
            val mundos = List(4) { k ->
                Offset(dentro.left + dentro.width * (0.14f + 0.24f * k + 0.06f * azar(semilla, k + 120)), dentro.top + dentro.height * (0.2f + 0.6f * azar(semilla, k + 130)))
            }
            with(TrazosFrente) { rutaSalto(mundos, ambar, 1.2f * u, 2.2f * u) }
            mundos.forEach { p -> drawCircle(tinta, 1.2f * u, p) }
            val blanco = mundos.last()
            drawCircle(rojo, 4f * u, blanco, style = Stroke(1f * u))
            drawLine(rojo, Offset(blanco.x - 6f * u, blanco.y), Offset(blanco.x + 6f * u, blanco.y), 0.8f * u)
            drawLine(rojo, Offset(blanco.x, blanco.y - 6f * u), Offset(blanco.x, blanco.y + 6f * u), 0.8f * u)
            // Las chinches.
            listOf(Offset(r.left + 3f * u, r.top + 3f * u), Offset(r.right - 3f * u, r.top + 3f * u), Offset(r.left + 3f * u, r.bottom - 3f * u), Offset(r.right - 3f * u, r.bottom - 3f * u)).forEach { p ->
                drawCircle(Color.Black.copy(alpha = 0.15f), 1.8f * u, p + Offset(0.5f * u, 0.7f * u))
                drawCircle(rojo, 1.8f * u, p)
                drawCircle(Color.White.copy(alpha = 0.4f), 0.6f * u, p - Offset(0.5f * u, 0.5f * u))
            }
        }
    }

    /**
     * Una bobina de cinta en [c]: la cinta enrollada (más o menos llena, [lleno] de 0 a 1), las tres aspas de la brida giradas
     * [giro] grados, el aro y el cubo con su hueco.
     */
    private fun DrawScope.bobina(c: Offset, r: Float, giro: Float, lleno: Float, brida: Color, cinta: Color, hueco: Color) {
        val u = density
        drawCircle(cinta, r * (0.42f + 0.48f * lleno.coerceIn(0f, 1f)), c)
        for (k in 0 until 3) {
            val a = Math.toRadians((giro + k * 120f).toDouble()).toFloat()
            drawLine(brida, c, Offset(c.x + r * cos(a), c.y + r * sin(a)), maxOf(r * 0.2f, 0.8f * u), StrokeCap.Round)
        }
        drawCircle(brida, r, c, style = Stroke(maxOf(r * 0.1f, 0.6f * u)))
        drawCircle(brida, r * 0.3f, c)
        drawCircle(hueco, r * 0.12f, c)
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La baliza de la célula, plantada en cada mundo: el trípode de cromo, la radio de maleta de baquelita con sus esquineras,
     * su asa, la rejilla del altavoz, el dial ámbar encendido y el piloto en el color de acento, y en su mástil la antena de
     * plato que apunta al cielo. Cuando brinca, transmite: las ondas salen de la antena.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraBaliza(e.punto, 15f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val linea = if (oscuro) Color(0xFFE7E1D4).copy(alpha = 0.55f) else BAQUELITA
        // El trípode: la pata de atrás primero y luego las dos de adelante, con sus regatones.
        val cabeza = Offset(x, pie - 8.5f * u)
        drawLine(n(CROMO_SOMBRA, oscuro), cabeza, Offset(x + 1.4f * u, pie - 1.6f * u), 0.9f * u, StrokeCap.Round)
        listOf(-5.5f, 5.5f).forEach { dx ->
            val p = Offset(x + dx * u, pie)
            drawLine(linea.copy(alpha = 0.6f), cabeza, p, 1.5f * u, StrokeCap.Round)
            drawLine(n(CROMO, oscuro), cabeza, p, 0.9f * u, StrokeCap.Round)
            drawCircle(linea, 0.7f * u, p)
        }
        // La maleta: el cuerpo, las esquineras, el asa, la rejilla, el dial y el piloto.
        val m = Rect(x - 6.5f * u, pie - 16f * u, x + 6.5f * u, pie - 8.5f * u)
        drawArc(n(CROMO, oscuro), 180f, 180f, false, Offset(x - 2.6f * u, m.top - 2.2f * u), Size(5.2f * u, 4.4f * u), style = Stroke(0.8f * u))
        drawRoundRect(n(BAQUELITA, oscuro), m.topLeft, m.size, CornerRadius(1.4f * u))
        if (oscuro) drawRoundRect(linea, m.topLeft, m.size, CornerRadius(1.4f * u), style = Stroke(0.5f * u))
        drawLine(Color.White.copy(alpha = 0.18f), Offset(m.left + 1.4f * u, m.top + 0.6f * u), Offset(m.right - 1.4f * u, m.top + 0.6f * u), 0.5f * u)
        listOf(Offset(m.left, m.top), Offset(m.right - 1.6f * u, m.top), Offset(m.left, m.bottom - 1.6f * u), Offset(m.right - 1.6f * u, m.bottom - 1.6f * u)).forEach { o ->
            drawRect(n(CROMO, oscuro), o, Size(1.6f * u, 1.6f * u))
        }
        val altavoz = Offset(m.left + 3.4f * u, m.center.y + 0.4f * u)
        drawCircle(n(CROMO_SOMBRA, oscuro), 2.1f * u, altavoz)
        listOf(Offset(-0.7f, -0.7f), Offset(0.7f, -0.7f), Offset(-0.7f, 0.7f), Offset(0.7f, 0.7f)).forEach { d -> drawCircle(n(BAQUELITA, oscuro), 0.38f * u, altavoz + d * u) }
        val dial = Rect(m.left + 6.6f * u, m.top + 1.6f * u, m.right - 1.4f * u, m.top + 3.8f * u)
        if (oscuro) drawRect(AMBAR.copy(alpha = 0.25f), dial.inflate(1.2f * u).topLeft, dial.inflate(1.2f * u).size)
        drawRect(n(AMBAR, oscuro), dial.topLeft, dial.size)
        drawLine(n(BAQUELITA, oscuro), Offset(dial.left + dial.width * 0.62f, dial.top), Offset(dial.left + dial.width * 0.62f, dial.bottom), 0.4f * u)
        val luz = Offset(m.right - 2.4f * u, m.bottom - 2.3f * u)
        if (oscuro) { drawCircle(e.acento.copy(alpha = 0.14f), 5f * u, luz); drawCircle(e.acento.copy(alpha = 0.3f), 2.4f * u, luz) }
        drawCircle(e.acento, 0.95f * u, luz)
        // El mástil y la antena de plato, que mira al cielo hacia arriba a la izquierda.
        val tope = Offset(x + 2.6f * u, pie - 21f * u)
        drawLine(n(CROMO, oscuro), Offset(tope.x, m.top), tope, 0.8f * u)
        rotate(-35f, tope) {
            drawArc(n(CROMO, oscuro), 0f, 180f, true, Offset(tope.x - 5f * u, tope.y - 3f * u), Size(10f * u, 6f * u))
            drawArc(linea.copy(alpha = 0.7f), 0f, 180f, false, Offset(tope.x - 5f * u, tope.y - 3f * u), Size(10f * u, 6f * u), style = Stroke(0.5f * u))
            drawLine(n(CROMO_LUZ, oscuro), Offset(tope.x - 5f * u, tope.y), Offset(tope.x + 5f * u, tope.y), 0.6f * u)
            drawLine(n(CROMO_SOMBRA, oscuro), tope, Offset(tope.x, tope.y - 3.6f * u), 0.6f * u)
            drawCircle(n(CROMO_SOMBRA, oscuro), 0.7f * u, Offset(tope.x, tope.y - 3.8f * u))
        }
        // Las ondas de la transmisión, al brincar.
        if (s > 0.02f) {
            val a = Math.toRadians(35.0).toFloat()
            val antena = Offset(tope.x - 3.8f * u * sin(a), tope.y - 3.8f * u * cos(a))
            for (k in 1..3) {
                val rk = (1.5f + k * 2.2f + s * 2f) * u
                drawArc(e.acento.copy(alpha = (1f - s * 0.4f) * (0.95f - k * 0.22f)), 200f, 70f, false, Offset(antena.x - rk, antena.y - rk), Size(2 * rk, 2 * rk),
                    style = Stroke(0.8f * u, cap = StrokeCap.Round))
            }
        }
    }

    /**
     * La victoria de la red en Albar, la capital: el muro de mármol del Directorio, con sus sillares, sus vetas, su cornisa de
     * bronce con los dentículos y su anillo de bronce, y encima el emblema de la red pintado con aerosol en rojo, con su
     * rocío y sus chorreones; al pie, la plantilla de cartón con el emblema calado y la lata, y en el aire las octavillas que
     * vuelan. De noche, el haz del
     * reflector del Directorio barre el cielo sin encontrar a nadie y salen dos estrellas.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFrente(e.meta, 56f * u, 48f * u)
        val x = b.x; val y = b.y
        val marmol = if (oscuro) mezcla(MARMOL, Color(0xFF2A3440), 0.55f) else MARMOL
        val veta = if (oscuro) Color(0xFF5E6A74) else Color(0xFFC9C3B6)
        val bronce = if (oscuro) mezcla(BRONCE, PENUMBRA, 0.3f) else BRONCE
        val linea = if (oscuro) Color(0xFFE7E1D4).copy(alpha = 0.45f) else BAQUELITA.copy(alpha = 0.7f)
        // De noche: el haz del reflector (el único degradado) y dos estrellas.
        if (oscuro) {
            val haz = Path().apply { moveTo(x + 27f * u, y - 8f * u); lineTo(x - 27f * u, y - 47f * u); lineTo(x - 14f * u, y - 48f * u); close() }
            drawPath(haz, Brush.linearGradient(listOf(Color(0xFFE8F0FF).copy(alpha = 0.16f), Color(0xFFE8F0FF).copy(alpha = 0.02f)),
                Offset(x + 27f * u, y - 8f * u), Offset(x - 20f * u, y - 47f * u)))
            drawCircle(Color.White.copy(alpha = 0.8f), 0.7f * u, Offset(x + 8f * u, y - 45f * u))
            drawCircle(Color.White.copy(alpha = 0.6f), 0.5f * u, Offset(x + 22f * u, y - 40f * u))
        }
        // El muro de mármol: la sombra al pie, los sillares con sus juntas y sus vetas, y la cara de la derecha en sombra.
        val muro = Rect(x - 24f * u, y - 30f * u, x + 18f * u, y)
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), Offset(muro.left - 2f * u, y - 1.6f * u), Size(muro.width + 14f * u, 3.2f * u))
        drawRect(marmol, muro.topLeft, muro.size)
        listOf(10f, 20f).forEach { dy -> drawLine(veta, Offset(muro.left, y - dy * u), Offset(muro.right, y - dy * u), 0.6f * u) }
        listOf(0f to listOf(-10f, 8f), 10f to listOf(-19f, -1f, 12f), 20f to listOf(-10f, 8f)).forEach { (dy, xs) ->
            xs.forEach { dx -> drawLine(veta, Offset(x + dx * u, y - dy * u), Offset(x + dx * u, y - (dy + 10f) * u), 0.6f * u) }
        }
        drawPath(Path().apply { moveTo(muro.left + 3f * u, y - 24f * u); cubicTo(x - 12f * u, y - 27f * u, x - 8f * u, y - 18f * u, x + 4f * u, y - 22f * u) },
            veta.copy(alpha = 0.5f), style = Stroke(0.4f * u))
        drawPath(Path().apply { moveTo(x - 2f * u, y - 3f * u); cubicTo(x + 4f * u, y - 7f * u, x + 9f * u, y - 2f * u, muro.right - 2f * u, y - 6f * u) },
            veta.copy(alpha = 0.5f), style = Stroke(0.4f * u))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.15f else 0.07f), Offset(muro.right - 3f * u, muro.top), Size(3f * u, muro.height))
        drawRect(linea.copy(alpha = 0.35f), muro.topLeft, muro.size, style = Stroke(0.5f * u))
        // La cornisa de bronce con su brillo y sus dentículos.
        val cornisa = Rect(muro.left - 2f * u, muro.top - 3.4f * u, muro.right + 2f * u, muro.top)
        drawRect(bronce, cornisa.topLeft, cornisa.size)
        drawLine(mezcla(bronce, Color.White, 0.35f), Offset(cornisa.left, cornisa.top + 0.5f * u), Offset(cornisa.right, cornisa.top + 0.5f * u), 0.8f * u)
        var xd = muro.left + 1f * u; var kd = 0
        while (xd < muro.right - 1f * u && kd < 30) { drawRect(bronce.copy(alpha = 0.85f), Offset(xd, muro.top), Size(1.6f * u, 1.6f * u)); xd += 3.2f * u; kd++ }
        // El emblema de la red pintado con aerosol sobre el anillo de bronce del Directorio: el rocío, el trazo y los chorreones.
        val ce = Offset(x - 4f * u, y - 15.5f * u); val re = 7.2f * u
        drawCircle(bronce, 10f * u, ce, style = Stroke(2f * u))
        drawArc(mezcla(bronce, Color.White, 0.35f), 200f, 80f, false, Offset(ce.x - 10f * u, ce.y - 10f * u), Size(20f * u, 20f * u), style = Stroke(0.6f * u))
        with(TrazosFrente) {
            emblemaRed(ce, re, ROJO.copy(alpha = 0.18f), 3.6f * u)
            emblemaRed(ce, re, ROJO, 1.7f * u)
        }
        listOf(-3.4f to 4.6f, 1.4f to 6.4f, 4.8f to 3.2f).forEach { (dx, l) ->
            val p0 = Offset(ce.x + dx * u, ce.y + sqrt(maxOf(re * re - (dx * u) * (dx * u), 0f)))
            drawLine(ROJO, p0, Offset(p0.x, p0.y + l * u), 0.7f * u, StrokeCap.Round)
            drawCircle(ROJO, 0.75f * u, Offset(p0.x, p0.y + l * u))
        }
        // La plantilla de cartón apoyada al pie del muro, con el emblema calado y su borde manchado.
        val piv = Offset(x + 13f * u, y)
        rotate(12f, piv) {
            val pr = Rect(x + 8f * u, y - 13f * u, x + 18f * u, y)
            drawRect(n(CARTON, oscuro), pr.topLeft, pr.size)
            drawRect(linea.copy(alpha = 0.5f), pr.topLeft, pr.size, style = Stroke(0.5f * u))
            with(TrazosFrente) { emblemaRed(Offset(pr.center.x, pr.center.y - 0.5f * u), 2.8f * u, Color.Black.copy(alpha = 0.55f), 0.9f * u) }
            for (k in 0 until 6) drawCircle(ROJO.copy(alpha = 0.6f), (0.3f + 0.3f * azar(23, k)) * u, Offset(pr.left + pr.width * azar(29, k), pr.top + pr.height * azar(31, k)))
        }
        // La lata de aerosol, de pie junto a la plantilla.
        val lx = x + 23.5f * u
        drawRoundRect(n(CROMO, oscuro), Offset(lx - 1.8f * u, y - 8f * u), Size(3.6f * u, 8f * u), CornerRadius(0.8f * u))
        drawRect(ROJO, Offset(lx - 1.8f * u, y - 5.6f * u), Size(3.6f * u, 2.4f * u))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(lx - 0.9f * u, y - 7.4f * u), Offset(lx - 0.9f * u, y - 0.8f * u), 0.5f * u)
        drawRoundRect(n(BAQUELITA, oscuro), Offset(lx - 1.2f * u, y - 9.6f * u), Size(2.4f * u, 1.8f * u), CornerRadius(0.6f * u))
        drawRect(n(BAQUELITA, oscuro), Offset(lx - 0.4f * u, y - 10.6f * u), Size(0.8f * u, 1f * u))
        // Las octavillas que vuelan.
        val papel = if (oscuro) mezcla(PAPEL, PENUMBRA, 0.35f) else PAPEL
        listOf(Triple(-17f, -38f, -18f), Triple(3f, -42f, 14f), Triple(15f, -35f, 32f)).forEach { (dx, dy, g) ->
            val c = Offset(x + dx * u, y + dy * u)
            rotate(g, c) {
                val hoja = Rect(c.x - 1.8f * u, c.y - 2.3f * u, c.x + 1.8f * u, c.y + 2.3f * u)
                drawRect(papel, hoja.topLeft, hoja.size)
                drawRect(linea.copy(alpha = 0.5f), hoja.topLeft, hoja.size, style = Stroke(0.4f * u))
                drawLine(ROJO, Offset(hoja.left + 0.6f * u, hoja.top + 1f * u), Offset(hoja.right - 0.6f * u, hoja.top + 1f * u), 0.6f * u)
                listOf(2.2f, 3.4f).forEach { yy -> drawLine(linea.copy(alpha = 0.45f), Offset(hoja.left + 0.6f * u, hoja.top + yy * u), Offset(hoja.right - 0.8f * u, hoja.top + yy * u), 0.35f * u) }
            }
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Los objetos de Tu año de la red: la luna, la creciente del mundo ocupado, con la patrulla del Directorio (el bloque largo
     * con su anillo) que la cruza; la huella, la suela de la bota de vuelo con los galones de su dibujo y la ranura del tacón;
     * la pesa, la barra del desguace con dos bobinas de cinta por discos, con sus tres ventanas.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        when (e.forma) {
            "luna" -> lunaAnio(e)
            "huella" -> huellaAnio(e)
            "pesa" -> pesaAnio(e)
            else -> {}
        }
    }

    /** La creciente con la boca arriba a la derecha y, si cabe, la patrulla del Directorio recortada contra ella. */
    private fun DrawScope.lunaAnio(e: EstadoObjeto) {
        val r = e.radio; val c = e.centro; val u = density
        drawPath(crecienteFrente(Offset(c.x - r * 0.12f, c.y - r * 0.05f), r * 0.82f, r * 0.46f, -r * 0.2f, 0.8f), e.color)
        if (r >= 7f * u) rotate(-12f, c) {
            val y = c.y + r * 0.52f
            val cuerpo = Rect(c.x - r * 0.8f, y - r * 0.09f, c.x + r * 0.46f, y + r * 0.09f)
            val anillo = Offset(c.x + r * 0.62f, y)
            drawRect(e.fondo, cuerpo.inflate(r * 0.07f).topLeft, cuerpo.inflate(r * 0.07f).size)
            drawCircle(e.fondo, r * 0.24f, anillo, style = Stroke(r * 0.22f))
            drawRect(e.color, cuerpo.topLeft, cuerpo.size)
            drawCircle(e.color, r * 0.24f, anillo, style = Stroke(grosorFrente(r, 0.09f)))
            drawLine(e.color, Offset(cuerpo.right, y), Offset(anillo.x - r * 0.24f, y), grosorFrente(r, 0.08f))
        } else drawCircle(e.color, maxOf(r * 0.08f, 0.6f * u), Offset(c.x + r * 0.55f, c.y - r * 0.5f))
    }

    /** La suela entera de la bota de vuelo, ladeada: la puntera ancha, el talle y el tacón, con los galones en hueco. */
    private fun DrawScope.huellaAnio(e: EstadoObjeto) {
        val r = e.radio; val c = e.centro; val x = c.x; val y = c.y; val u = density
        rotate(-14f, c) {
            val suela = Path().apply {
                moveTo(x, y - r)
                cubicTo(x + r * 0.55f, y - r, x + r * 0.55f, y - r * 0.2f, x + r * 0.3f, y + r * 0.15f)
                cubicTo(x + r * 0.22f, y + r * 0.3f, x + r * 0.4f, y + r * 0.5f, x + r * 0.38f, y + r * 0.78f)
                cubicTo(x + r * 0.36f, y + r * 1.02f, x - r * 0.36f, y + r * 1.02f, x - r * 0.38f, y + r * 0.78f)
                cubicTo(x - r * 0.4f, y + r * 0.5f, x - r * 0.22f, y + r * 0.3f, x - r * 0.3f, y + r * 0.15f)
                cubicTo(x - r * 0.55f, y - r * 0.2f, x - r * 0.55f, y - r, x, y - r)
                close()
            }
            drawPath(suela, e.color)
            val g = grosorFrente(r, 0.1f)
            val cuantos = if (r < 7f * u) 3 else 4
            for (k in 0 until cuantos) {
                val yy = y - r * 0.64f + k * r * (if (cuantos == 3) 0.28f else 0.2f)
                val a = r * (0.27f - k * 0.025f)
                drawPath(Path().apply { moveTo(x - a, yy + r * 0.12f); lineTo(x, yy); lineTo(x + a, yy + r * 0.12f) }, e.fondo,
                    style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            drawLine(e.fondo, Offset(x - r * 0.34f, y + r * 0.5f), Offset(x + r * 0.34f, y + r * 0.5f), grosorFrente(r, 0.08f))
        }
    }

    /** La barra con una bobina de cinta en cada punta, vista un poco de lado: los discos ovalados con sus tres ventanas, si cabe. */
    private fun DrawScope.pesaAnio(e: EstadoObjeto) {
        val r = e.radio; val c = e.centro; val x = c.x; val y = c.y; val u = density
        val gb = grosorFrente(r, 0.16f)
        drawRoundRect(e.color, Offset(x - r * 0.98f, y - gb / 2), Size(r * 1.96f, gb), CornerRadius(gb / 2))
        listOf(-1f, 1f).forEach { s ->
            val bc = Offset(x + s * r * 0.6f, y)
            val rx = r * 0.26f; val ry = r * 0.76f
            drawOval(e.color, Offset(bc.x - rx, bc.y - ry), Size(2 * rx, 2 * ry))
            if (r >= 6f * u) {
                for (k in 0 until 3) {
                    val a = Math.toRadians(k * 120.0 - 90.0).toFloat()
                    val p = Offset(bc.x + rx * 0.45f * cos(a), bc.y + ry * 0.52f * sin(a))
                    drawOval(e.fondo, Offset(p.x - rx * 0.24f, p.y - ry * 0.17f), Size(rx * 0.48f, ry * 0.34f))
                }
                drawOval(e.fondo, Offset(bc.x - rx * 0.14f, bc.y - ry * 0.08f), Size(rx * 0.28f, ry * 0.16f))
            }
        }
    }
}
