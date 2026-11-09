package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Laboratorio: los muebles de Vida y de Diario del laboratorio limpio, de la misma casa que su estante abierto de reactivos
 * contra el azulejo, su armario clínico de muestras y su sala blanca de logros. Azulejo blanco con sus juntas, acero pintado
 * con su cremallera de ranuras, aluminio con bisel, la cubierta de resina epóxica negra, melamina blanca, vidrio con su
 * líquido de color y las etiquetas impresas con franja cian, código de barras y rombo de riesgo. De noche (modo oscuro) el
 * laboratorio se apaga: el acero y el azulejo en penumbra y solo brillan los paneles de luz y los líquidos.
 * - Social: la sala de seminarios: la pizarra blanca grande con su marco de aluminio y su bandeja de marcadores (el grafo va
 *   en la pizarra); a la izquierda, el reloj de pared, la señal de riesgo y el microscopio sobre la mesa; a la derecha, el
 *   botiquín, la repisa con el frasco ámbar y la piseta, y el matraz y el vaso de precipitados; abajo, la mesa de resina con
 *   sus patas de acero, el mueble de puertas de melamina y el piso de vinilo.
 * - Mapas: la mesa de trabajo vista desde arriba: la cubierta de resina negra con la mesa de luz encastrada (el mapa va en su
 *   panel encendido), la gradilla con sus tubos, las cajas de Petri con colonias, el vaso de precipitados, la pipeta, la regla
 *   de acero y el cuaderno; al frente, el canto de la cubierta y los cajones de melamina con sus tiradores.
 * - Otras vidas: la vitrina de muestras de acero y vidrio: la cornisa con el friso de la tabla periódica (cuadritos de color
 *   por bloques), los rieles de las puertas corredizas, cinco compartimentos iluminados (uno por medalla), los cajones con su
 *   etiqueta impresa y el zócalo con ruedas; encima, matraces, la gradilla y el frasco ámbar.
 * - Íconos: el instituto con su rotonda y la cúpula del observatorio abierta; la caja de Petri con la capa plateada que la
 *   espátula raspa; tus lugares como átomos clavados en el papel milimetrado, unidos por sus enlaces.
 * - Notas: la puerta del ultracongelador: el panel de control con su pantalla, las luces y la cerradura; la puerta de esmalte
 *   blanco (las notas van en ella) con sus bisagras, su manija cromada y la etiqueta de frío; abajo, la rejilla y las ruedas.
 *   Las notas son papeles del laboratorio pegados con cinta de rotular o con un imán (si están fijadas): la nota adhesiva, la
 *   ficha de protocolo con sus casillas, la tarjeta pautada y la etiqueta de criogenia con su código de barras.
 */
internal object VidaLaboratorio : DisenoVida {
    private val AZULEJO = Color(0xFFE6ECEF)
    private val JUNTA = Color(0xFFC6D0D7)
    private val BLANCO = Color(0xFFF7F9FA)
    private val GRIS = Color(0xFFAFB9C1)
    private val GRIS_LUZ = Color(0xFFD3DADF)
    private val GRIS_OSC = Color(0xFF6B7781)
    private val ACERO = Color(0xFF4A5560)
    private val EPOXI = Color(0xFF22282D)
    private val MUEBLE = Color(0xFFD5DCE1)
    private val VINILO = Color(0xFF9AA4AB)
    private val CIAN = Color(0xFF1FB5C9)
    private val VIOLETA = Color(0xFF7A4FD0)
    private val AMBAR = Color(0xFF8A4B14)
    private val AMARILLO = Color(0xFFF2C232)
    private val ROJO = Color(0xFFD8312A)
    private val VERDE = Color(0xFF2E9B5F)
    private val VIDRIO = Color(0xFFD9EEF2)
    private val TINTA = Color(0xFF12202B)
    private val NOCHE = Color(0xFF0B0E12)
    /** Los bloques de la tabla periódica: alcalinos, alcalinotérreos, transición, bloque p, gases nobles, lantánidos, actínidos. */
    private val BLOQUES = listOf(Color(0xFFF2A3A3), Color(0xFFF5C99A), Color(0xFFA9C8EE), Color(0xFFB4E0AE), Color(0xFFD5C0F0), Color(0xFFF2DDA0), Color(0xFFF0B9A6))
    /** Los tapones de los tubos: cian, violeta, rojo, amarillo y verde. */
    private val TAPONES = listOf(CIAN, VIOLETA, ROJO, AMARILLO, VERDE)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.165f, 0.085f, 0.835f, 0.715f)
        MuebleVida.MAPAS -> Rect(0.185f, 0.085f, 0.815f, 0.775f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.055f, 0.265f, 0.945f, 0.80f)
        MuebleVida.NOTAS -> Rect(0.065f, 0.13f, 0.885f, 0.915f)
    }

    // ---------------------------------------------------------------- ayudas

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material con la luz del momento: tal cual en claro; en oscuro, en la penumbra del laboratorio apagado. */
    private fun c(x: Color, oscuro: Boolean, f: Float = 0.6f): Color = if (oscuro) mezcla(x, NOCHE, f) else x

    /** El fondo sólido de un nicho: el material en claro y, en oscuro, apenas separado de la superficie de la tarjeta. */
    private fun nicho(x: Color, e: EstadoMueble, oscuro: Boolean): Color = if (oscuro) mezcla(e.fondo, x, 0.10f) else mezcla(e.fondo, x, 0.7f)

    private fun Rect.mas(d: Float) = Rect(left - d, top - d, right + d, bottom + d)

    private fun DrawScope.caja(r: Rect, color: Color) = drawRect(color, r.topLeft, r.size)

    /** Una mancha de luz radial que se desvanece (el halo de un panel encendido). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) {
        if (r < 1f) return
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r), r, c)
    }

    /** El azulejo blanco con sus juntas, en piezas de [pieza]. */
    private fun DrawScope.azulejo(r: Rect, pieza: Float, oscuro: Boolean) {
        caja(r, c(AZULEJO, oscuro, 0.74f))
        val junta = c(JUNTA, oscuro, 0.7f)
        var y = r.top + pieza * 0.5f
        while (y < r.bottom) { drawLine(junta, Offset(r.left, y), Offset(r.right, y), pieza * 0.05f); y += pieza }
        var x = r.left + pieza * 0.3f
        while (x < r.right) { drawLine(junta, Offset(x, r.top), Offset(x, r.bottom), pieza * 0.05f); x += pieza }
    }

    /** El contorno de un matraz de Erlenmeyer de base [b]. */
    private fun matrazPath(b: Offset, alto: Float): Path {
        val a = alto * 0.78f
        return Path().apply {
            moveTo(b.x - a / 2, b.y); lineTo(b.x - a * 0.13f, b.y - alto * 0.6f); lineTo(b.x - a * 0.13f, b.y - alto * 0.95f)
            lineTo(b.x - a * 0.18f, b.y - alto); lineTo(b.x + a * 0.18f, b.y - alto); lineTo(b.x + a * 0.13f, b.y - alto * 0.95f)
            lineTo(b.x + a * 0.13f, b.y - alto * 0.6f); lineTo(b.x + a / 2, b.y); close()
        }
    }

    /** Un matraz de vidrio con su líquido, su contorno y el brillo del vidrio. */
    private fun DrawScope.matraz(b: Offset, alto: Float, liquido: Color, oscuro: Boolean, nivel: Float = 0.42f) {
        val p = matrazPath(b, alto)
        val a = alto * 0.78f
        drawPath(p, c(VIDRIO, oscuro, 0.55f))
        clipPath(p) {
            drawRect(liquido, Offset(b.x - a, b.y - alto * nivel), Size(a * 2, alto * nivel))
            drawRect(Color.White.copy(alpha = 0.35f), Offset(b.x - a, b.y - alto * nivel), Size(a * 2, alto * 0.03f))
        }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.7f), Offset(b.x - a * 0.28f, b.y - alto * 0.18f), Offset(b.x - a * 0.08f, b.y - alto * 0.55f), alto * 0.04f, StrokeCap.Round)
        drawPath(p, c(GRIS_OSC, oscuro, 0.3f), style = Stroke(alto * 0.035f, join = StrokeJoin.Round))
    }

    /** Un vaso de precipitados con su pico, sus graduaciones y su líquido; la base en [b]. */
    private fun DrawScope.vaso(b: Offset, ancho: Float, alto: Float, liquido: Color, oscuro: Boolean) {
        val r = Rect(b.x - ancho / 2, b.y - alto, b.x + ancho / 2, b.y)
        drawRoundRect(c(VIDRIO, oscuro, 0.55f), r.topLeft, r.size, CornerRadius(ancho * 0.08f))
        drawRoundRect(liquido, Offset(r.left, r.top + alto * 0.45f), Size(ancho, alto * 0.55f), CornerRadius(ancho * 0.08f))
        drawRect(Color.White.copy(alpha = 0.35f), Offset(r.left, r.top + alto * 0.45f), Size(ancho, alto * 0.04f))
        for (k in 1..3) drawLine(c(GRIS_OSC, oscuro, 0.3f), Offset(r.right - ancho * 0.35f, r.top + alto * 0.22f * k), Offset(r.right - ancho * 0.12f, r.top + alto * 0.22f * k), ancho * 0.04f)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.7f), Offset(r.left + ancho * 0.18f, r.top + alto * 0.15f), Offset(r.left + ancho * 0.18f, r.bottom - alto * 0.15f), ancho * 0.07f, StrokeCap.Round)
        drawRoundRect(c(GRIS_OSC, oscuro, 0.3f), r.topLeft, r.size, CornerRadius(ancho * 0.08f), style = Stroke(ancho * 0.05f))
        // El pico y el reborde.
        drawPath(Path().apply { moveTo(r.left - ancho * 0.1f, r.top - ancho * 0.06f); lineTo(r.left + ancho * 0.12f, r.top); lineTo(r.left, r.top + ancho * 0.1f); close() }, c(GRIS_OSC, oscuro, 0.3f))
        drawLine(c(GRIS_OSC, oscuro, 0.3f), Offset(r.left, r.top), Offset(r.right + ancho * 0.04f, r.top), ancho * 0.07f)
    }

    /** Un frasco ámbar de reactivo con su tapa negra y su etiqueta con el rombo de riesgo. */
    private fun DrawScope.frascoAmbar(b: Offset, ancho: Float, alto: Float, oscuro: Boolean) {
        val r = Rect(b.x - ancho / 2, b.y - alto * 0.8f, b.x + ancho / 2, b.y)
        drawRoundRect(c(AMBAR, oscuro, 0.35f), r.topLeft, r.size, CornerRadius(ancho * 0.18f))
        drawRect(c(AMBAR, oscuro, 0.35f), Offset(b.x - ancho * 0.3f, b.y - alto * 0.88f), Size(ancho * 0.6f, alto * 0.1f))
        drawRoundRect(c(EPOXI, oscuro, 0.2f), Offset(b.x - ancho * 0.36f, b.y - alto), Size(ancho * 0.72f, alto * 0.14f), CornerRadius(ancho * 0.05f))
        drawLine(Color.White.copy(alpha = 0.3f), Offset(r.left + ancho * 0.16f, r.top + alto * 0.1f), Offset(r.left + ancho * 0.16f, r.bottom - alto * 0.12f), ancho * 0.08f, StrokeCap.Round)
        val et = Rect(r.left + ancho * 0.12f, r.top + alto * 0.24f, r.right - ancho * 0.12f, r.top + alto * 0.62f)
        caja(et, c(BLANCO, oscuro, 0.25f))
        caja(Rect(et.left, et.top, et.right, et.top + et.height * 0.16f), c(ROJO, oscuro, 0.2f))
        rombo(Offset(et.center.x, et.center.y + et.height * 0.12f), et.height * 0.3f, oscuro)
    }

    /** El rombo rojo de riesgo con su señal de exclamación. */
    private fun DrawScope.rombo(c0: Offset, s: Float, oscuro: Boolean) {
        val p = Path().apply { moveTo(c0.x, c0.y - s); lineTo(c0.x + s, c0.y); lineTo(c0.x, c0.y + s); lineTo(c0.x - s, c0.y); close() }
        drawPath(p, c(BLANCO, oscuro, 0.2f))
        drawPath(p, c(ROJO, oscuro, 0.2f), style = Stroke(s * 0.2f, join = StrokeJoin.Miter))
        drawLine(c(TINTA, oscuro, 0f), Offset(c0.x, c0.y - s * 0.45f), Offset(c0.x, c0.y + s * 0.15f), s * 0.18f)
        drawCircle(c(TINTA, oscuro, 0f), s * 0.1f, Offset(c0.x, c0.y + s * 0.4f))
    }

    /** La señal triangular amarilla de riesgo con su exclamación. */
    private fun DrawScope.senal(c0: Offset, s: Float, oscuro: Boolean) {
        val p = Path().apply { moveTo(c0.x, c0.y - s); lineTo(c0.x + s * 1.1f, c0.y + s * 0.85f); lineTo(c0.x - s * 1.1f, c0.y + s * 0.85f); close() }
        drawPath(p, c(AMARILLO, oscuro, 0.3f))
        drawPath(p, c(TINTA, oscuro, 0f), style = Stroke(s * 0.16f, join = StrokeJoin.Round))
        drawLine(c(TINTA, oscuro, 0f), Offset(c0.x, c0.y - s * 0.35f), Offset(c0.x, c0.y + s * 0.3f), s * 0.17f, StrokeCap.Round)
        drawCircle(c(TINTA, oscuro, 0f), s * 0.1f, Offset(c0.x, c0.y + s * 0.6f))
    }

    /** Un código de barras de grosores fijos por la semilla, en [r]. */
    private fun DrawScope.codigo(r: Rect, color: Color, semilla: Int) {
        val rnd = Random(semilla.toLong())
        var x = r.left
        while (x < r.right) {
            val g = r.width * (0.025f + 0.05f * rnd.nextFloat())
            if (x + g > r.right) break
            drawRect(color, Offset(x, r.top), Size(g, r.height))
            x += g + r.width * (0.03f + 0.04f * rnd.nextFloat())
        }
    }

    /** Un tornillo de cabeza redonda con su ranura. */
    private fun DrawScope.tornillo(o: Offset, r: Float, oscuro: Boolean) {
        drawCircle(c(GRIS_OSC, oscuro, 0.4f), r, o)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f), r * 0.55f, Offset(o.x - r * 0.25f, o.y - r * 0.25f))
        drawLine(c(ACERO, oscuro, 0.3f), Offset(o.x - r * 0.6f, o.y + r * 0.3f), Offset(o.x + r * 0.6f, o.y - r * 0.3f), r * 0.3f)
    }

    /** Un marco de aluminio de [ancho] alrededor de [r], con su bisel y su sombra en la pared. */
    private fun DrawScope.marcoAluminio(r: Rect, ancho: Float, oscuro: Boolean, sombra: Boolean = true) {
        val f = r.mas(ancho)
        if (sombra) drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(f.left + ancho * 0.4f, f.top + ancho * 0.8f), f.size, CornerRadius(ancho * 0.6f))
        drawRoundRect(c(GRIS, oscuro), f.topLeft, f.size, CornerRadius(ancho * 0.6f))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.14f else 0.5f), Offset(f.left + ancho * 0.3f, f.top + ancho * 0.15f), Size(f.width - ancho * 0.6f, ancho * 0.25f))
        drawRect(c(GRIS_OSC, oscuro, 0.4f), Offset(f.left + ancho * 0.3f, f.bottom - ancho * 0.35f), Size(f.width - ancho * 0.6f, ancho * 0.22f))
        drawRect(c(GRIS_OSC, oscuro, 0.4f), Offset(r.left - ancho * 0.18f, r.top - ancho * 0.18f), Size(r.width + ancho * 0.36f, r.height + ancho * 0.36f), style = Stroke(ancho * 0.16f))
    }

    // ---------------------------------------------------------------- Social

    /** El reloj de pared del laboratorio: el aro negro, la esfera blanca con sus marcas y las agujas (la roja, el segundero). */
    private fun DrawScope.reloj(o: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.18f), r, Offset(o.x + r * 0.08f, o.y + r * 0.12f))
        drawCircle(c(ACERO, oscuro, 0.3f), r, o)
        drawCircle(c(BLANCO, oscuro, 0.45f), r * 0.82f, o)
        for (k in 0 until 12) {
            val a = k * Math.PI / 6
            val l = if (k % 3 == 0) 0.2f else 0.1f
            drawLine(c(TINTA, oscuro, 0.2f), Offset(o.x + r * 0.72f * cos(a).toFloat(), o.y + r * 0.72f * sin(a).toFloat()),
                Offset(o.x + r * (0.72f - l) * cos(a).toFloat(), o.y + r * (0.72f - l) * sin(a).toFloat()), r * (if (k % 3 == 0) 0.07f else 0.04f))
        }
        drawLine(c(TINTA, oscuro, 0.2f), o, Offset(o.x - r * 0.25f, o.y - r * 0.38f), r * 0.09f, StrokeCap.Round)
        drawLine(c(TINTA, oscuro, 0.2f), o, Offset(o.x + r * 0.55f, o.y - r * 0.1f), r * 0.06f, StrokeCap.Round)
        drawLine(c(ROJO, oscuro, 0.2f), Offset(o.x - r * 0.15f, o.y - r * 0.12f), Offset(o.x + r * 0.45f, o.y + r * 0.4f), r * 0.03f, StrokeCap.Round)
        drawCircle(c(ROJO, oscuro, 0.2f), r * 0.07f, o)
    }

    /** El microscopio sobre la mesa, de [alto], con la base en [b]. */
    private fun DrawScope.microscopio(b: Offset, alto: Float, oscuro: Boolean) {
        val s = alto
        val oscuroMetal = c(ACERO, oscuro, 0.3f)
        val claroMetal = c(GRIS_LUZ, oscuro, 0.5f)
        // La base en herradura y el brazo curvo.
        drawRoundRect(oscuroMetal, Offset(b.x - s * 0.32f, b.y - s * 0.09f), Size(s * 0.62f, s * 0.09f), CornerRadius(s * 0.03f))
        drawRect(Color.White.copy(alpha = 0.15f), Offset(b.x - s * 0.3f, b.y - s * 0.09f), Size(s * 0.58f, s * 0.015f))
        val brazo = Path().apply { moveTo(b.x + s * 0.18f, b.y - s * 0.09f); cubicTo(b.x + s * 0.34f, b.y - s * 0.4f, b.x + s * 0.3f, b.y - s * 0.72f, b.x + s * 0.04f, b.y - s * 0.86f) }
        drawPath(brazo, oscuroMetal, style = Stroke(s * 0.11f, cap = StrokeCap.Round))
        drawPath(brazo, Color.White.copy(alpha = 0.12f), style = Stroke(s * 0.03f, cap = StrokeCap.Round))
        // La platina con su portaobjetos y la muestra.
        drawRect(oscuroMetal, Offset(b.x - s * 0.26f, b.y - s * 0.4f), Size(s * 0.48f, s * 0.05f))
        drawRect(c(VIDRIO, oscuro, 0.4f), Offset(b.x - s * 0.22f, b.y - s * 0.43f), Size(s * 0.32f, s * 0.03f))
        drawCircle(c(VIOLETA, oscuro, 0.2f), s * 0.025f, Offset(b.x - s * 0.08f, b.y - s * 0.415f))
        // El revólver con sus objetivos, el tubo inclinado y el ocular.
        drawRect(claroMetal, Offset(b.x - s * 0.12f, b.y - s * 0.58f), Size(s * 0.18f, s * 0.08f))
        drawRect(oscuroMetal, Offset(b.x - s * 0.1f, b.y - s * 0.5f), Size(s * 0.05f, s * 0.05f))
        drawRect(c(CIAN, oscuro, 0.3f), Offset(b.x - s * 0.02f, b.y - s * 0.5f), Size(s * 0.05f, s * 0.04f))
        rotate(-28f, Offset(b.x - s * 0.03f, b.y - s * 0.6f)) {
            drawRoundRect(claroMetal, Offset(b.x - s * 0.1f, b.y - s * 1.0f), Size(s * 0.14f, s * 0.42f), CornerRadius(s * 0.03f))
            drawRect(Color.White.copy(alpha = 0.35f), Offset(b.x - s * 0.08f, b.y - s * 0.98f), Size(s * 0.025f, s * 0.38f))
            drawRoundRect(oscuroMetal, Offset(b.x - s * 0.08f, b.y - s * 1.1f), Size(s * 0.1f, s * 0.12f), CornerRadius(s * 0.02f))
        }
        // La perilla del enfoque.
        drawCircle(oscuroMetal, s * 0.07f, Offset(b.x + s * 0.22f, b.y - s * 0.32f))
        drawCircle(claroMetal, s * 0.035f, Offset(b.x + s * 0.22f, b.y - s * 0.32f))
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = h / 100f
        if (u <= 0f) return
        val a = area(MuebleVida.SOCIAL).let { Rect(w * it.left, h * it.top, w * it.right, h * it.bottom) }
        val piso = h * 0.935f
        // La pared de azulejo y el piso de vinilo con su zócalo.
        azulejo(Rect(0f, 0f, w, piso), 9f * u, oscuro)
        caja(Rect(0f, piso, w, h), c(VINILO, oscuro, 0.6f))
        var px = 0f
        while (px < w) { drawLine(c(GRIS_OSC, oscuro, 0.6f), Offset(px, piso), Offset(px - 3f * u, h), 0.3f * u); px += 14f * u }
        caja(Rect(0f, piso - 1.6f * u, w, piso), c(ACERO, oscuro, 0.4f))
        // La pizarra blanca: el marco de aluminio con sus esquineros y el esmalte (el nicho del grafo).
        marcoAluminio(a, 2.2f * u, oscuro)
        listOf(Offset(a.left, a.top), Offset(a.right, a.top), Offset(a.left, a.bottom), Offset(a.right, a.bottom)).forEach { o ->
            drawRoundRect(c(EPOXI, oscuro, 0.2f), Offset(o.x - 2.6f * u, o.y - 2.6f * u), Size(5.2f * u, 5.2f * u), CornerRadius(1.2f * u))
            drawRect(Color.White.copy(alpha = 0.15f), Offset(o.x - 2.2f * u, o.y - 2.4f * u), Size(4.4f * u, 0.6f * u))
        }
        caja(a, nicho(BLANCO, e, oscuro))
        // Lo que queda de lo borrado: dos velos apenas visibles en las esquinas de abajo.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, (if (oscuro) Color.White else GRIS).copy(alpha = 0.05f)), a.bottom - 10f * u, a.bottom), Offset(a.left, a.bottom - 10f * u), Size(a.width, 10f * u))
        // La bandeja de marcadores, con el cian, el violeta, el rojo y el borrador.
        val bandeja = Rect(a.left + a.width * 0.08f, a.bottom + 2.2f * u, a.right - a.width * 0.08f, a.bottom + 4.6f * u)
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(bandeja.left, bandeja.bottom), Size(bandeja.width, 1f * u))
        caja(bandeja, c(GRIS, oscuro))
        caja(Rect(bandeja.left, bandeja.top, bandeja.right, bandeja.top + 0.6f * u), Color.White.copy(alpha = if (oscuro) 0.15f else 0.5f))
        listOf(CIAN, VIOLETA, ROJO).forEachIndexed { k, col ->
            val x0 = bandeja.left + bandeja.width * 0.06f + k * 13f * u
            drawRoundRect(c(BLANCO, oscuro, 0.4f), Offset(x0, bandeja.top - 1.4f * u), Size(9f * u, 1.8f * u), CornerRadius(0.9f * u))
            drawRoundRect(c(col, oscuro, 0.2f), Offset(x0 + 6f * u, bandeja.top - 1.5f * u), Size(4f * u, 2f * u), CornerRadius(0.9f * u))
        }
        val borrador = Rect(bandeja.right - bandeja.width * 0.06f - 11f * u, bandeja.top - 2.6f * u, bandeja.right - bandeja.width * 0.06f, bandeja.top)
        drawRoundRect(c(EPOXI, oscuro, 0.2f), borrador.topLeft, borrador.size, CornerRadius(0.6f * u))
        caja(Rect(borrador.left, borrador.bottom - 0.8f * u, borrador.right, borrador.bottom), c(GRIS_LUZ, oscuro, 0.4f))
        // La mesa de resina negra con su canto y sus patas de acero.
        val mesa = Rect(w * 0.02f, h * 0.795f, w * 0.98f, h * 0.83f)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(mesa.left, mesa.bottom), Size(mesa.width, 1.2f * u))
        listOf(0.06f, 0.94f).forEach { f ->
            val x = w * f
            caja(Rect(x - 1.4f * u, mesa.bottom, x + 1.4f * u, piso), c(ACERO, oscuro, 0.35f))
            caja(Rect(x - 1.4f * u, mesa.bottom, x - 0.7f * u, piso), Color.White.copy(alpha = 0.15f))
            caja(Rect(x - 2.4f * u, piso - 1f * u, x + 2.4f * u, piso), c(EPOXI, oscuro, 0.2f))
        }
        caja(Rect(w * 0.06f, mesa.bottom + 3.5f * u, w * 0.94f, mesa.bottom + 4.6f * u), c(ACERO, oscuro, 0.4f))
        caja(mesa, c(EPOXI, oscuro, 0.25f))
        caja(Rect(mesa.left, mesa.top, mesa.right, mesa.top + 0.8f * u), mezcla(c(EPOXI, oscuro, 0.25f), Color.White, 0.18f))
        caja(Rect(mesa.left, mesa.bottom - 0.6f * u, mesa.right, mesa.bottom), c(CIAN, oscuro, 0.35f))
        // Debajo, el mueble de puertas de melamina con sus tiradores.
        val bajo = Rect(w * 0.085f, mesa.bottom + 4.6f * u, w * 0.915f, piso - 1.6f * u)
        caja(bajo, c(GRIS, oscuro))
        val np = 4
        for (i in 0 until np) {
            val f = Rect(bajo.left + bajo.width * i / np + 0.5f * u, bajo.top + 0.6f * u, bajo.left + bajo.width * (i + 1) / np - 0.5f * u, bajo.bottom - 0.4f * u)
            caja(f, c(MUEBLE, oscuro))
            caja(Rect(f.left, f.top, f.right, f.top + 0.5f * u), Color.White.copy(alpha = if (oscuro) 0.08f else 0.6f))
            val hx = if (i % 2 == 0) f.right - 2.4f * u else f.left + 1.6f * u
            drawRoundRect(c(ACERO, oscuro, 0.35f), Offset(hx, f.top + f.height * 0.2f), Size(0.8f * u, f.height * 0.45f), CornerRadius(0.4f * u))
        }
        // A la izquierda: el reloj de pared, la señal de riesgo y el microscopio sobre la mesa.
        val izq = a.left / 2f
        reloj(Offset(izq, h * 0.17f), minOf(8f * u, a.left * 0.36f), oscuro)
        senal(Offset(izq, h * 0.4f), minOf(4.6f * u, a.left * 0.22f), oscuro)
        microscopio(Offset(izq, mesa.top), minOf(28f * u, a.left * 1.25f), oscuro)
        // A la derecha: el botiquín, la repisa con el frasco ámbar y la piseta, y los matraces sobre la mesa.
        val der = (a.right + w) / 2f
        val bs = minOf(7f * u, (w - a.right) * 0.3f)
        drawRoundRect(Color.Black.copy(alpha = 0.15f), Offset(der - bs + 0.5f * u, h * 0.16f - bs + 0.8f * u), Size(bs * 2, bs * 1.7f), CornerRadius(bs * 0.2f))
        drawRoundRect(c(VERDE, oscuro, 0.4f), Offset(der - bs, h * 0.16f - bs), Size(bs * 2, bs * 1.7f), CornerRadius(bs * 0.2f))
        drawRect(c(BLANCO, oscuro, 0.3f), Offset(der - bs * 0.18f, h * 0.16f - bs * 0.62f), Size(bs * 0.36f, bs * 0.95f))
        drawRect(c(BLANCO, oscuro, 0.3f), Offset(der - bs * 0.48f, h * 0.16f - bs * 0.32f), Size(bs * 0.96f, bs * 0.36f))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(der - bs, h * 0.16f - bs), Size(bs * 2, bs * 0.15f))
        val repisa = h * 0.47f
        val rx0 = a.right + 3.6f * u; val rx1 = w - 1.5f * u
        caja(Rect(rx0, repisa, rx1, repisa + 1.3f * u), c(BLANCO, oscuro, 0.45f))
        caja(Rect(rx0, repisa + 1.3f * u, rx1, repisa + 1.9f * u), c(GRIS, oscuro))
        listOf(rx0 + 2f * u, rx1 - 3f * u).forEach { x -> drawPath(Path().apply { moveTo(x, repisa + 1.9f * u); lineTo(x + 1f * u, repisa + 1.9f * u); lineTo(x + 1f * u, repisa + 6f * u); close() }, c(GRIS_OSC, oscuro, 0.4f)) }
        val ancho = rx1 - rx0
        frascoAmbar(Offset(rx0 + ancho * 0.3f, repisa), minOf(7f * u, ancho * 0.36f), 12f * u, oscuro)
        // La piseta de pico doblado.
        val pc = Offset(rx0 + ancho * 0.74f, repisa)
        val pw = minOf(6f * u, ancho * 0.3f)
        drawRoundRect(c(BLANCO, oscuro, 0.45f), Offset(pc.x - pw / 2, pc.y - 10f * u), Size(pw, 10f * u), CornerRadius(pw * 0.3f))
        drawRoundRect(c(CIAN, oscuro, 0.35f), Offset(pc.x - pw / 2, pc.y - 4.5f * u), Size(pw, 4.5f * u), CornerRadius(pw * 0.3f))
        drawRoundRect(c(GRIS_OSC, oscuro, 0.35f), Offset(pc.x - pw / 2, pc.y - 10f * u), Size(pw, 10f * u), CornerRadius(pw * 0.3f), style = Stroke(0.35f * u))
        drawRect(c(CIAN, oscuro, 0.35f), Offset(pc.x - pw * 0.3f, pc.y - 11.6f * u), Size(pw * 0.6f, 1.8f * u))
        drawPath(Path().apply { moveTo(pc.x, pc.y - 11.6f * u); lineTo(pc.x, pc.y - 14f * u); lineTo(pc.x - pw * 0.7f, pc.y - 13f * u) }, c(GRIS_OSC, oscuro, 0.35f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
        // Sobre la mesa: el matraz violeta y el vaso cian.
        val mt = mesa.top
        matraz(Offset(der + (w - a.right) * 0.12f, mt), minOf(17f * u, (w - a.right) * 0.6f), c(VIOLETA, oscuro, 0.25f), oscuro)
        vaso(Offset(der - (w - a.right) * 0.25f, mt), minOf(7f * u, (w - a.right) * 0.22f), 8f * u, c(CIAN, oscuro, 0.25f), oscuro)
    }

    // ---------------------------------------------------------------- Mapas

    /** Una caja de Petri vista desde arriba: el vidrio, el agar y sus colonias. */
    private fun DrawScope.petri(o: Offset, r: Float, agar: Color, colonia: Color, semilla: Int, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(o.x + r * 0.08f, o.y + r * 0.1f))
        drawCircle(c(VIDRIO, oscuro, 0.5f), r, o)
        drawCircle(agar, r * 0.86f, o)
        val rnd = Random(semilla.toLong())
        repeat(7) {
            val ang = rnd.nextFloat() * 6.28f; val d = r * 0.65f * rnd.nextFloat()
            drawCircle(colonia, r * (0.05f + 0.07f * rnd.nextFloat()), Offset(o.x + d * cos(ang), o.y + d * sin(ang)))
        }
        drawArc(Color.White.copy(alpha = if (oscuro) 0.25f else 0.6f), 200f, 70f, false, Offset(o.x - r * 0.8f, o.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.08f, cap = StrokeCap.Round))
        drawCircle(c(GRIS_OSC, oscuro, 0.3f), r, o, style = Stroke(r * 0.06f))
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = h / 100f
        if (u <= 0f) return
        val a = area(MuebleVida.MAPAS).let { Rect(w * it.left, h * it.top, w * it.right, h * it.bottom) }
        val canto = h * 0.88f
        // La cubierta de resina negra vista desde arriba, con un reflejo largo de la lámpara del techo.
        caja(Rect(0f, 0f, w, canto), c(EPOXI, oscuro, 0.3f))
        drawPath(Path().apply { moveTo(w * 0.05f, 0f); lineTo(w * 0.2f, 0f); lineTo(w * 0.08f, canto); lineTo(-w * 0.07f, canto); close() }, Color.White.copy(alpha = if (oscuro) 0.025f else 0.05f))
        // El canto de la cubierta y los cajones de melamina con sus tiradores.
        caja(Rect(0f, canto, w, canto + 2.6f * u), mezcla(c(EPOXI, oscuro, 0.3f), Color.White, 0.1f))
        caja(Rect(0f, canto, w, canto + 0.5f * u), Color.White.copy(alpha = if (oscuro) 0.12f else 0.25f))
        caja(Rect(0f, canto + 2.6f * u, w, h), c(MUEBLE, oscuro, 0.55f))
        caja(Rect(0f, canto + 2.6f * u, w, canto + 3.6f * u), Color.Black.copy(alpha = 0.22f))
        val n = 4
        for (k in 0 until n) {
            val x0 = w * k / n + 1.2f * u; val x1 = w * (k + 1) / n - 1.2f * u
            val f = Rect(x0, canto + 4.2f * u, x1, h)
            caja(f, c(BLANCO, oscuro, 0.55f))
            caja(Rect(f.left, f.top, f.right, f.top + 0.5f * u), Color.White.copy(alpha = 0.4f))
            drawRoundRect(c(GRIS_OSC, oscuro, 0.4f), Offset(f.center.x - 5f * u, f.top + 1.6f * u), Size(10f * u, 1.4f * u), CornerRadius(0.7f * u))
        }
        // La mesa de luz encastrada: el marco de aluminio con sus tornillos, el piloto encendido y el panel (el nicho del mapa).
        if (oscuro) halo(a.center, a.width * 0.62f, CIAN.copy(alpha = 0.10f))
        marcoAluminio(a, 2.6f * u, oscuro)
        listOf(Offset(a.left - 1.3f * u, a.top - 1.3f * u), Offset(a.right + 1.3f * u, a.top - 1.3f * u), Offset(a.left - 1.3f * u, a.bottom + 1.3f * u), Offset(a.right + 1.3f * u, a.bottom + 1.3f * u)).forEach { tornillo(it, 0.9f * u, oscuro) }
        drawCircle(c(CIAN, oscuro, 0f), 0.8f * u, Offset(a.right - 5f * u, a.bottom + 1.3f * u))
        if (oscuro) halo(Offset(a.right - 5f * u, a.bottom + 1.3f * u), 3f * u, CIAN.copy(alpha = 0.5f))
        caja(a, if (oscuro) mezcla(e.fondo, CIAN, 0.07f) else Color(0xFFF6FBFC))
        // Debajo del marco, la regla de acero con sus graduaciones.
        val rg = Rect(a.left + a.width * 0.12f, a.bottom + 4.6f * u, a.right - a.width * 0.12f, a.bottom + 8.2f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rg.left + 0.6f * u, rg.top + 0.8f * u), rg.size)
        caja(rg, c(GRIS_LUZ, oscuro, 0.45f))
        caja(Rect(rg.left, rg.top, rg.right, rg.top + 0.6f * u), Color.White.copy(alpha = 0.5f))
        var k = 0; var x = rg.left + 1f * u
        while (x < rg.right - 0.5f * u) { val l = if (k % 10 == 0) 0.6f else if (k % 5 == 0) 0.42f else 0.26f; drawLine(c(TINTA, oscuro, 0.3f), Offset(x, rg.top), Offset(x, rg.top + rg.height * l), 0.22f * u); x += 1.2f * u; k++ }
        // A la izquierda: la gradilla con sus tubos vista desde arriba y la pila de cajas de Petri.
        val izq = a.left - 2.6f * u
        val gx0 = izq * 0.14f; val gx1 = izq * 0.86f
        val gr = Rect(gx0, h * 0.08f, gx1, h * 0.46f)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(gr.left + 0.8f * u, gr.top + 1f * u), gr.size)
        caja(gr, c(GRIS_LUZ, oscuro, 0.5f))
        drawRect(c(GRIS, oscuro), gr.topLeft, gr.size, style = Stroke(0.5f * u))
        val filas = 4; val cols = 2
        for (i in 0 until filas) for (j in 0 until cols) {
            val o = Offset(gr.left + gr.width * (j + 0.5f) / cols, gr.top + gr.height * (i + 0.5f) / filas)
            val r = minOf(gr.width / cols, gr.height / filas) * 0.36f
            drawCircle(c(ACERO, oscuro, 0.3f), r * 1.1f, o)
            if ((i * cols + j + e.semilla) % 5 != 0) {
                drawCircle(c(TAPONES[(i * cols + j) % TAPONES.size], oscuro, 0.25f), r, o)
                drawCircle(Color.White.copy(alpha = 0.35f), r * 0.45f, Offset(o.x - r * 0.25f, o.y - r * 0.25f))
            }
        }
        val pr = minOf(9f * u, izq * 0.36f)
        petri(Offset(izq * 0.5f, h * 0.62f), pr, c(Color(0xFFF2D7A6), oscuro, 0.35f), c(Color(0xFFE59A4A), oscuro, 0.3f), e.semilla, oscuro)
        petri(Offset(izq * 0.55f, h * 0.78f), pr * 0.9f, c(Color(0xFFF4C3CE), oscuro, 0.35f), c(VIOLETA, oscuro, 0.3f), e.semilla + 3, oscuro)
        // A la derecha: el vaso de precipitados desde arriba, la pipeta y el cuaderno de tapa dura.
        val der0 = a.right + 2.6f * u
        val dw = w - der0
        val vc = Offset(der0 + dw * 0.5f, h * 0.2f)
        val vr = minOf(9f * u, dw * 0.36f)
        drawCircle(Color.Black.copy(alpha = 0.3f), vr, Offset(vc.x + 0.8f * u, vc.y + 1f * u))
        drawCircle(c(VIDRIO, oscuro, 0.5f), vr, vc)
        drawCircle(c(CIAN, oscuro, 0.25f), vr * 0.82f, vc)
        drawCircle(Color.White.copy(alpha = 0.3f), vr * 0.82f, vc, style = Stroke(vr * 0.08f))
        drawCircle(c(GRIS_OSC, oscuro, 0.3f), vr, vc, style = Stroke(0.45f * u))
        // El pico del vaso, hacia arriba a la izquierda.
        val pico = Offset(vc.x - vr * 0.7f, vc.y - vr * 0.7f)
        drawPath(Path().apply { moveTo(pico.x + vr * 0.28f, pico.y - vr * 0.1f); lineTo(pico.x - vr * 0.18f, pico.y - vr * 0.18f); lineTo(pico.x - vr * 0.1f, pico.y + vr * 0.28f); close() }, c(VIDRIO, oscuro, 0.5f))
        drawPath(Path().apply { moveTo(pico.x + vr * 0.28f, pico.y - vr * 0.1f); lineTo(pico.x - vr * 0.18f, pico.y - vr * 0.18f); lineTo(pico.x - vr * 0.1f, pico.y + vr * 0.28f) }, c(GRIS_OSC, oscuro, 0.3f), style = Stroke(0.45f * u, join = StrokeJoin.Round))
        // Unas gotas derramadas junto al vaso.
        listOf(Offset(0.9f, 1.25f) to 0.12f, Offset(1.15f, 1.05f) to 0.08f).forEach { (o, r) -> drawCircle(c(CIAN, oscuro, 0.3f).copy(alpha = 0.8f), vr * r, Offset(vc.x + vr * o.x, vc.y + vr * o.y)) }
        // La pipeta de vidrio con su pera, en diagonal.
        val p0 = Offset(der0 + dw * 0.18f, h * 0.42f); val p1 = Offset(der0 + dw * 0.8f, h * 0.58f)
        drawLine(Color.Black.copy(alpha = 0.3f), p0 + Offset(0.6f * u, 0.8f * u), p1 + Offset(0.6f * u, 0.8f * u), 1.4f * u, StrokeCap.Round)
        drawLine(c(VIDRIO, oscuro, 0.4f), p0, p1, 1.2f * u, StrokeCap.Round)
        drawLine(c(GRIS_OSC, oscuro, 0.3f), p0, p1, 0.25f * u)
        drawOval(c(ROJO, oscuro, 0.3f), Offset(p1.x - 2.2f * u, p1.y - 1.6f * u), Size(4.4f * u, 3.2f * u))
        // El cuaderno de laboratorio, con su tapa azul, la etiqueta y la liga elástica.
        val cu = Rect(der0 + dw * 0.12f, h * 0.64f, der0 + dw * 0.92f, canto - 1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(cu.left + 0.8f * u, cu.top + 1f * u), cu.size)
        drawRoundRect(c(Color(0xFF1F5FA8), oscuro, 0.35f), cu.topLeft, cu.size, CornerRadius(0.6f * u))
        caja(Rect(cu.left, cu.top, cu.left + 2f * u, cu.bottom), c(EPOXI, oscuro, 0.2f))
        val et = Rect(cu.left + cu.width * 0.25f, cu.top + cu.height * 0.18f, cu.right - cu.width * 0.12f, cu.top + cu.height * 0.48f)
        caja(et, c(BLANCO, oscuro, 0.3f))
        caja(Rect(et.left, et.top, et.right, et.top + et.height * 0.22f), c(CIAN, oscuro, 0.25f))
        caja(Rect(cu.right - cu.width * 0.2f, cu.top, cu.right - cu.width * 0.14f, cu.bottom), c(EPOXI, oscuro, 0.2f))
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = h / 100f
        if (u <= 0f) return
        val a = area(MuebleVida.OTRAS_VIDAS).let { Rect(w * it.left, h * it.top, w * it.right, h * it.bottom) }
        // La pared de azulejo y el piso.
        azulejo(Rect(0f, 0f, w, h * 0.97f), 11f * u, oscuro)
        caja(Rect(0f, h * 0.97f, w, h), c(VINILO, oscuro, 0.6f))
        val x0 = w * 0.025f; val x1 = w * 0.975f
        // Encima de la vitrina: la gradilla con sus tubos, dos matraces y el frasco ámbar.
        val tope = h * 0.15f
        val gr = Rect(w * 0.1f, tope - 4f * u, w * 0.27f, tope - 1.5f * u)
        for (k in 0 until 5) {
            val tx = gr.left + gr.width * (k + 0.5f) / 5f
            drawRoundRect(c(VIDRIO, oscuro, 0.5f), Offset(tx - 1.3f * u, tope - 12f * u), Size(2.6f * u, 10.5f * u), CornerRadius(1.3f * u))
            drawRoundRect(c(TAPONES[k], oscuro, 0.25f), Offset(tx - 1.3f * u, tope - 6f * u), Size(2.6f * u, 4.5f * u), CornerRadius(1.3f * u))
            drawRect(c(TAPONES[(k + 2) % 5], oscuro, 0.25f), Offset(tx - 1.5f * u, tope - 13.4f * u), Size(3f * u, 2f * u))
        }
        caja(gr, c(GRIS, oscuro))
        caja(Rect(gr.left, gr.top, gr.right, gr.top + 0.6f * u), Color.White.copy(alpha = 0.4f))
        caja(Rect(gr.left + 1f * u, gr.bottom, gr.left + 2f * u, tope), c(GRIS_OSC, oscuro, 0.4f))
        caja(Rect(gr.right - 2f * u, gr.bottom, gr.right - 1f * u, tope), c(GRIS_OSC, oscuro, 0.4f))
        matraz(Offset(w * 0.5f, tope), 13f * u, c(CIAN, oscuro, 0.25f), oscuro)
        matraz(Offset(w * 0.6f, tope), 10f * u, c(VIOLETA, oscuro, 0.25f), oscuro)
        frascoAmbar(Offset(w * 0.82f, tope), 7f * u, 12f * u, oscuro)
        vaso(Offset(w * 0.9f, tope), 5f * u, 6.5f * u, c(Color(0xFFF4C3CE), oscuro, 0.3f), oscuro)
        // La cornisa de acero con su vuelo y su sombra.
        val cor = Rect(x0 - 0.8f * u, tope, x1 + 0.8f * u, tope + 2.6f * u)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(cor.left, cor.bottom), Size(cor.width, 1.4f * u))
        caja(cor, c(GRIS, oscuro))
        caja(Rect(cor.left, cor.top, cor.right, cor.top + 0.7f * u), Color.White.copy(alpha = if (oscuro) 0.15f else 0.5f))
        caja(Rect(cor.left, cor.bottom - 0.5f * u, cor.right, cor.bottom), c(GRIS_OSC, oscuro, 0.4f))
        // El cuerpo: la caja de esmalte blanco con sus postes de acero ranurados.
        val cuerpo = Rect(x0, cor.bottom, x1, h * 0.94f)
        caja(cuerpo, c(BLANCO, oscuro, 0.55f))
        // El friso de la tabla periódica: cuadritos de color por bloques (sin letras), con dos rombos de riesgo en las puntas.
        val fr = Rect(x0 + 9f * u, cor.bottom + 1.4f * u, x1 - 9f * u, a.top - 3.6f * u)
        val lado = fr.height
        val nCol = (fr.width / (lado * 1.12f)).toInt().coerceAtLeast(1)
        val paso = fr.width / nCol
        for (k in 0 until nCol) {
            val bloque = when {
                k == 0 -> 0; k == 1 -> 1; k >= nCol - 1 -> 4; k >= nCol - 6 -> 3
                k < nCol / 2 -> if (k % 7 == 3) 5 else 2
                else -> if (k % 5 == 0) 6 else 2
            }
            val col = BLOQUES[bloque]
            val r = Rect(fr.left + k * paso + paso * 0.06f, fr.top, fr.left + (k + 1) * paso - paso * 0.06f, fr.bottom)
            caja(r, c(col, oscuro, 0.55f))
            caja(Rect(r.left, r.top, r.right, r.top + r.height * 0.2f), Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f))
            caja(Rect(r.left + r.width * 0.25f, r.top + r.height * 0.45f, r.right - r.width * 0.25f, r.top + r.height * 0.6f), c(TINTA, oscuro, 0.4f).copy(alpha = 0.35f))
        }
        rombo(Offset(x0 + 4.5f * u, fr.center.y), minOf(3.6f * u, fr.height * 0.75f), oscuro)
        rombo(Offset(x1 - 4.5f * u, fr.center.y), minOf(3.6f * u, fr.height * 0.75f), oscuro)
        // El riel de arriba de las puertas corredizas.
        val riel = Rect(x0, a.top - 2.6f * u, x1, a.top)
        caja(riel, c(GRIS, oscuro))
        caja(Rect(riel.left, riel.top, riel.right, riel.top + 0.6f * u), Color.White.copy(alpha = 0.35f))
        caja(Rect(riel.left, riel.bottom - 0.6f * u, riel.right, riel.bottom), c(GRIS_OSC, oscuro, 0.4f))
        // Los cinco compartimentos iluminados (el nicho), con su luz arriba.
        caja(a, nicho(BLANCO, e, oscuro))
        val cw = a.width / 5f
        for (i in 0 until 5) {
            val l = a.left + cw * i
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Color.Transparent), a.top, a.top + 3.4f * u), Offset(l, a.top), Size(cw, 3.4f * u))
            if (oscuro) halo(Offset(l + cw / 2, a.top), cw * 0.45f, CIAN.copy(alpha = 0.08f))
            // El brillo del vidrio en la esquina de arriba a la derecha.
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.9f), Offset(l + cw * 0.78f, a.top + 2f * u), Offset(l + cw * 0.92f, a.top + 10f * u), 0.6f * u, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.9f), Offset(l + cw * 0.86f, a.top + 2f * u), Offset(l + cw * 0.94f, a.top + 6.5f * u), 0.4f * u, StrokeCap.Round)
        }
        // Los parteluces de acero entre compartimentos y los postes de los lados, con su cremallera de ranuras.
        fun poste(cx: Float, ancho: Float, y0: Float, y1: Float) {
            caja(Rect(cx - ancho / 2, y0, cx + ancho / 2, y1), c(GRIS, oscuro))
            caja(Rect(cx - ancho / 2, y0, cx - ancho / 2 + ancho * 0.22f, y1), Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f))
            caja(Rect(cx + ancho / 2 - ancho * 0.2f, y0, cx + ancho / 2, y1), c(GRIS_OSC, oscuro, 0.4f))
            var y = y0 + 2f * u
            while (y < y1 - 2f * u) { drawRoundRect(c(ACERO, oscuro, 0.3f), Offset(cx - ancho * 0.12f, y), Size(ancho * 0.24f, 1.6f * u), CornerRadius(0.4f * u)); y += 3.4f * u }
        }
        for (i in 1 until 5) poste(a.left + cw * i, 1.8f * u, a.top, a.bottom)
        poste((x0 + a.left) / 2, a.left - x0, riel.top, cuerpo.bottom)
        poste((x1 + a.right) / 2, x1 - a.right, riel.top, cuerpo.bottom)
        // El riel de abajo y la fila de cajones con su etiqueta impresa y su tirador.
        val riel2 = Rect(a.left, a.bottom, a.right, a.bottom + 2.4f * u)
        caja(riel2, c(GRIS, oscuro))
        caja(Rect(riel2.left, riel2.top, riel2.right, riel2.top + 0.7f * u), c(GRIS_OSC, oscuro, 0.4f))
        val cajones = Rect(a.left, riel2.bottom + 0.8f * u, a.right, cuerpo.bottom - 0.8f * u)
        for (i in 0 until 5) {
            val f = Rect(cajones.left + cw * i + 0.6f * u, cajones.top, cajones.left + cw * (i + 1) - 0.6f * u, cajones.bottom)
            caja(f, c(MUEBLE, oscuro, 0.55f))
            caja(Rect(f.left, f.top, f.right, f.top + 0.5f * u), Color.White.copy(alpha = if (oscuro) 0.1f else 0.6f))
            caja(Rect(f.left, f.bottom - 0.5f * u, f.right, f.bottom), c(GRIS_OSC, oscuro, 0.4f))
            val et = Rect(f.left + f.width * 0.1f, f.top + f.height * 0.22f, f.left + f.width * 0.5f, f.bottom - f.height * 0.22f)
            caja(et, c(BLANCO, oscuro, 0.3f))
            caja(Rect(et.left, et.top, et.left + et.width * 0.14f, et.bottom), c(if (i % 2 == 0) CIAN else VIOLETA, oscuro, 0.25f))
            codigo(Rect(et.left + et.width * 0.24f, et.top + et.height * 0.25f, et.right - et.width * 0.08f, et.bottom - et.height * 0.25f), c(TINTA, oscuro, 0.3f), e.semilla * 7 + i)
            drawRoundRect(c(ACERO, oscuro, 0.3f), Offset(f.right - f.width * 0.36f, f.center.y - 0.7f * u), Size(f.width * 0.24f, 1.4f * u), CornerRadius(0.7f * u))
        }
        // El zócalo con sus ruedas.
        val zoc = Rect(x0 + 1.5f * u, cuerpo.bottom, x1 - 1.5f * u, h * 0.955f)
        caja(zoc, c(ACERO, oscuro, 0.35f))
        listOf(x0 + 7f * u, x1 - 7f * u).forEach { x ->
            caja(Rect(x - 1.2f * u, zoc.bottom, x + 1.2f * u, zoc.bottom + 0.8f * u), c(GRIS_OSC, oscuro, 0.4f))
            drawCircle(c(EPOXI, oscuro, 0.2f), 1.6f * u, Offset(x, h * 0.97f + 0.2f * u))
            drawCircle(c(GRIS, oscuro), 0.6f * u, Offset(x, h * 0.97f + 0.2f * u))
        }
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(x0, h * 0.97f), Size(x1 - x0, 1.2f * u))
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = minOf(h, w / 2.2f) / 62f
        if (s <= 0f) return
        val c0 = Offset(w / 2, h / 2)
        val linea = c(TINTA, oscuro, 0f).let { if (oscuro) mezcla(e.tinta, e.fondo, 0.35f) else it }
        val muro = if (oscuro) mezcla(BLANCO, NOCHE, 0.45f) else BLANCO
        when (id) {
            "unesco" -> {
                // El instituto: las gradas, las dos alas con sus ventanas, la rotunda con columnas y la cúpula abierta.
                val base = c0.y + 28f * s
                drawRect(Color.Black.copy(alpha = 0.12f), Offset(c0.x - 44f * s, base), Size(88f * s, 2f * s))
                for (k in 0 until 3) drawRect(c(GRIS_LUZ, oscuro, 0.45f), Offset(c0.x - (40f - 4f * k) * s, base - (k + 1) * 2.4f * s), Size((80f - 8f * k) * s, 2.4f * s))
                for (k in 0 until 3) drawLine(linea.copy(alpha = 0.4f), Offset(c0.x - (40f - 4f * k) * s, base - k * 2.4f * s), Offset(c0.x + (40f - 4f * k) * s, base - k * 2.4f * s), 0.5f * s)
                val suelo = base - 7.2f * s
                listOf(-1f, 1f).forEach { lado ->
                    val ala = Rect(if (lado < 0) c0.x - 36f * s else c0.x + 12f * s, suelo - 22f * s, if (lado < 0) c0.x - 12f * s else c0.x + 36f * s, suelo)
                    caja(ala, muro)
                    caja(Rect(ala.left, ala.top, ala.right, ala.top + 3f * s), c(GRIS, oscuro))
                    for (i in 0 until 3) for (j in 0 until 2) {
                        val v = Rect(ala.left + 3f * s + i * 7.4f * s, ala.top + 6f * s + j * 7.5f * s, ala.left + 7.4f * s + i * 7.4f * s, ala.top + 10.5f * s + j * 7.5f * s)
                        caja(v, mezcla(e.c1, NOCHE, if (oscuro) 0.2f else 0.35f))
                        caja(Rect(v.left, v.top, v.right, v.top + 1f * s), Color.White.copy(alpha = 0.4f))
                    }
                    drawRect(linea, ala.topLeft, ala.size, style = Stroke(0.8f * s))
                }
                // La rotunda con columnas.
                val rot = Rect(c0.x - 12f * s, suelo - 26f * s, c0.x + 12f * s, suelo)
                caja(rot, mezcla(muro, GRIS, 0.15f))
                for (k in 0 until 4) {
                    val x = rot.left + 3f * s + k * 6f * s
                    caja(Rect(x, rot.top + 3f * s, x + 2.4f * s, rot.bottom), muro)
                    drawLine(linea.copy(alpha = 0.5f), Offset(x + 2.4f * s, rot.top + 3f * s), Offset(x + 2.4f * s, rot.bottom), 0.5f * s)
                }
                caja(Rect(c0.x - 3.2f * s, rot.bottom - 10f * s, c0.x + 3.2f * s, rot.bottom), e.c2)
                caja(Rect(rot.left - 1.5f * s, rot.top, rot.right + 1.5f * s, rot.top + 3f * s), c(GRIS, oscuro))
                drawRect(linea, rot.topLeft, rot.size, style = Stroke(0.8f * s))
                // La cúpula con su luz y su sombra, la rendija abierta y el telescopio asomando.
                val cr = 13f * s
                val cc = Offset(c0.x, rot.top)
                drawArc(e.c1, 180f, 180f, true, Offset(cc.x - cr, cc.y - cr), Size(cr * 2, cr * 2))
                drawArc(mezcla(e.c1, Color.White, 0.35f), 180f, 60f, true, Offset(cc.x - cr, cc.y - cr), Size(cr * 2, cr * 2))
                drawArc(mezcla(e.c1, Color.Black, 0.25f), 300f, 60f, true, Offset(cc.x - cr, cc.y - cr), Size(cr * 2, cr * 2))
                for (k in 1..2) drawArc(mezcla(e.c1, Color.Black, 0.3f), 180f, 180f, false, Offset(cc.x - cr * k / 3f, cc.y - cr), Size(cr * 2 * k / 3f, cr * 2), style = Stroke(0.5f * s))
                drawRect(c(EPOXI, oscuro, 0.1f), Offset(cc.x - 2.2f * s, cc.y - cr), Size(4.4f * s, cr))
                rotate(32f, Offset(cc.x, cc.y - cr * 0.4f)) {
                    drawRoundRect(c(GRIS_LUZ, oscuro, 0.3f), Offset(cc.x - 1.6f * s, cc.y - cr * 1.45f), Size(3.2f * s, cr * 1.1f), CornerRadius(0.8f * s))
                    drawRect(e.c2, Offset(cc.x - 1.9f * s, cc.y - cr * 1.48f), Size(3.8f * s, 2f * s))
                }
                drawArc(linea, 180f, 180f, true, Offset(cc.x - cr, cc.y - cr), Size(cr * 2, cr * 2), style = Stroke(0.9f * s))
            }
            "rascar" -> {
                // La caja de Petri con la capa plateada: el mundo asoma donde la espátula ya raspó.
                val o = Offset(c0.x - 6f * s, c0.y + 1f * s)
                val r = 27f * s
                drawOval(Color.Black.copy(alpha = 0.18f), Offset(o.x - r, o.y + r * 0.7f), Size(r * 2, r * 0.5f))
                drawCircle(mezcla(VIDRIO, e.fondo, if (oscuro) 0.6f else 0.2f), r, o)
                val ri = r * 0.86f
                drawCircle(mezcla(e.c1, e.fondo, if (oscuro) 0.55f else 0.75f), ri, o)
                val tierra = Path().apply {
                    moveTo(o.x - ri * 0.7f, o.y - ri * 0.3f); cubicTo(o.x - ri * 0.4f, o.y - ri * 0.75f, o.x + ri * 0.1f, o.y - ri * 0.55f, o.x - ri * 0.05f, o.y - ri * 0.1f)
                    cubicTo(o.x - ri * 0.2f, o.y + ri * 0.3f, o.x + ri * 0.05f, o.y + ri * 0.6f, o.x - ri * 0.3f, o.y + ri * 0.7f); cubicTo(o.x - ri * 0.6f, o.y + ri * 0.4f, o.x - ri * 0.9f, o.y, o.x - ri * 0.7f, o.y - ri * 0.3f); close()
                    moveTo(o.x + ri * 0.2f, o.y - ri * 0.6f); cubicTo(o.x + ri * 0.7f, o.y - ri * 0.7f, o.x + ri * 0.85f, o.y - ri * 0.1f, o.x + ri * 0.5f, o.y + ri * 0.1f)
                    cubicTo(o.x + ri * 0.3f, o.y - ri * 0.1f, o.x + ri * 0.1f, o.y - ri * 0.3f, o.x + ri * 0.2f, o.y - ri * 0.6f); close()
                }
                drawPath(tierra, e.c2)
                // La capa plateada todavía sin raspar (a la derecha), con su borde mordido.
                val capa = Path().apply {
                    moveTo(o.x + ri * 0.05f, o.y - ri); lineTo(o.x + ri, o.y - ri); lineTo(o.x + ri, o.y + ri); lineTo(o.x - ri * 0.3f, o.y + ri)
                    var y = o.y + ri; var k = 0
                    while (y > o.y - ri) { y -= ri * 0.18f; val x = o.x - ri * 0.3f + ri * 0.35f * (o.y + ri - y) / (2 * ri) + (if (k % 2 == 0) ri * 0.06f else -ri * 0.04f); lineTo(x, y); k++ }
                    close()
                }
                clipPath(Path().apply { addOval(Rect(o.x - ri, o.y - ri, o.x + ri, o.y + ri)) }) {
                    drawPath(capa, if (oscuro) Color(0xFF8C969E) else Color(0xFFB9C2C8))
                    for (k in 0 until 6) drawLine(Color.White.copy(alpha = 0.35f), Offset(o.x + ri * (0.1f + k * 0.16f), o.y - ri), Offset(o.x + ri * (-0.2f + k * 0.16f), o.y + ri), 0.6f * s)
                }
                drawArc(Color.White.copy(alpha = 0.7f), 200f, 60f, false, Offset(o.x - r * 0.9f, o.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(1.6f * s, cap = StrokeCap.Round))
                drawCircle(linea, r, o, style = Stroke(1.1f * s))
                drawCircle(linea.copy(alpha = 0.5f), ri, o, style = Stroke(0.5f * s))
                // La espátula de acero con su mango.
                rotate(-35f, Offset(o.x + r * 0.9f, o.y + r * 0.2f)) {
                    drawRoundRect(c(GRIS_LUZ, oscuro, 0.3f), Offset(o.x + r * 0.2f, o.y + r * 0.12f), Size(r * 0.95f, 4f * s), CornerRadius(2f * s))
                    drawRect(Color.White.copy(alpha = 0.5f), Offset(o.x + r * 0.25f, o.y + r * 0.14f), Size(r * 0.8f, 1f * s))
                    drawRoundRect(e.c1, Offset(o.x + r * 1.1f, o.y + r * 0.08f), Size(r * 0.75f, 6f * s), CornerRadius(3f * s))
                    drawRoundRect(linea, Offset(o.x + r * 0.2f, o.y + r * 0.08f), Size(r * 1.65f, 6f * s), CornerRadius(3f * s), style = Stroke(0.6f * s))
                }
                // Las virutas plateadas.
                listOf(Offset(0.15f, 0.95f), Offset(0.3f, 1.05f), Offset(-0.05f, 1.08f)).forEach { k -> drawCircle(Color(0xFFB9C2C8), 1.3f * s, Offset(o.x + r * k.x, o.y + r * k.y)) }
            }
            else -> {
                // Tus lugares: el papel milimetrado en perspectiva y tres átomos clavados, unidos por sus enlaces.
                val hoja = Path().apply { moveTo(c0.x - 44f * s, c0.y + 26f * s); lineTo(c0.x + 44f * s, c0.y + 26f * s); lineTo(c0.x + 34f * s, c0.y - 6f * s); lineTo(c0.x - 34f * s, c0.y - 6f * s); close() }
                drawPath(Path().apply { moveTo(c0.x - 44f * s, c0.y + 28f * s); lineTo(c0.x + 44f * s, c0.y + 28f * s); lineTo(c0.x + 44f * s, c0.y + 26f * s); lineTo(c0.x - 44f * s, c0.y + 26f * s); close() }, Color.Black.copy(alpha = 0.15f))
                drawPath(hoja, muro)
                clipPath(hoja) {
                    for (k in 0..8) { val f = k / 8f; drawLine(e.c1.copy(alpha = 0.3f), Offset(c0.x - 34f * s + 68f * s * f, c0.y - 6f * s), Offset(c0.x - 44f * s + 88f * s * f, c0.y + 26f * s), 0.5f * s) }
                    for (k in 0..4) { val f = k / 4f; val y = c0.y - 6f * s + 32f * s * f * f * 0.4f + 32f * s * f * 0.6f; drawLine(e.c1.copy(alpha = 0.3f), Offset(0f, y), Offset(w, y), 0.5f * s) }
                }
                drawPath(hoja, linea, style = Stroke(0.8f * s, join = StrokeJoin.Round))
                val pts = listOf(Offset(c0.x - 24f * s, c0.y + 12f * s), Offset(c0.x + 2f * s, c0.y - 16f * s), Offset(c0.x + 26f * s, c0.y + 6f * s))
                val pie = listOf(Offset(c0.x - 24f * s, c0.y + 20f * s), Offset(c0.x + 2f * s, c0.y + 6f * s), Offset(c0.x + 26f * s, c0.y + 16f * s))
                drawLine(linea, pts[0], pts[1], 1.8f * s); drawLine(linea, pts[1], pts[2], 1.8f * s)
                drawLine(linea, Offset(pts[1].x + 1.5f * s, pts[1].y), Offset(pts[2].x + 1.5f * s, pts[2].y), 0.8f * s)
                listOf(e.c1, e.c2, c(ROJO, oscuro, 0.1f)).forEachIndexed { i, col ->
                    val p = pts[i]; val f = pie[i]
                    drawOval(Color.Black.copy(alpha = 0.25f), Offset(f.x - 3f * s, f.y - 1f * s), Size(6f * s, 2f * s))
                    drawLine(c(GRIS_OSC, oscuro, 0.2f), p, f, 1f * s)
                    val r = if (i == 1) 8f * s else 6.5f * s
                    drawCircle(mezcla(col, Color.Black, 0.3f), r, Offset(p.x + 0.6f * s, p.y + 0.8f * s))
                    drawCircle(col, r, p)
                    drawCircle(mezcla(col, Color.White, 0.35f), r * 0.6f, Offset(p.x - r * 0.15f, p.y - r * 0.15f))
                    drawCircle(Color.White.copy(alpha = 0.8f), r * 0.22f, Offset(p.x - r * 0.38f, p.y - r * 0.38f))
                    drawCircle(linea, r, p, style = Stroke(0.7f * s))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = h / 100f
        if (u <= 0f) return
        val a = area(MuebleVida.NOTAS).let { Rect(w * it.left, h * it.top, w * it.right, h * it.bottom) }
        // La pared de azulejo detrás del ultracongelador y el piso.
        azulejo(Rect(0f, 0f, w, h * 0.975f), 9f * u, oscuro)
        caja(Rect(0f, h * 0.975f, w, h), c(VINILO, oscuro, 0.6f))
        // El cuerpo del ultracongelador: esmalte blanco con sus cantos redondeados y su sombra.
        val cuerpo = Rect(w * 0.012f, h * 0.008f, w * 0.988f, h * 0.955f)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f), Offset(cuerpo.left + 1f * u, cuerpo.top + 1.2f * u), cuerpo.size, CornerRadius(2.4f * u))
        drawRoundRect(c(MUEBLE, oscuro, 0.55f), cuerpo.topLeft, cuerpo.size, CornerRadius(2.4f * u))
        // El panel de control: la franja gris, la pantalla con su lectura, las luces, el teclado y la cerradura.
        val panel = Rect(cuerpo.left + 1.5f * u, cuerpo.top + 1.2f * u, cuerpo.right - 1.5f * u, h * 0.105f)
        drawRoundRect(c(GRIS_OSC, oscuro, 0.45f), panel.topLeft, panel.size, CornerRadius(1.4f * u))
        caja(Rect(panel.left + 1f * u, panel.top + 0.4f * u, panel.right - 1f * u, panel.top + 0.9f * u), Color.White.copy(alpha = 0.15f))
        val pant = Rect(panel.left + panel.width * 0.06f, panel.top + panel.height * 0.2f, panel.left + panel.width * 0.06f + 20f * u, panel.bottom - panel.height * 0.2f)
        drawRoundRect(c(EPOXI, oscuro, 0.3f), pant.topLeft, pant.size, CornerRadius(0.8f * u))
        // La gráfica de la temperatura en la pantalla: la línea estable con su alarma y la barra del nivel.
        val seg = c(CIAN, oscuro, 0f)
        if (oscuro) halo(pant.center, pant.width * 0.6f, CIAN.copy(alpha = 0.18f))
        val gy = pant.top + pant.height * 0.6f
        drawLine(c(ROJO, oscuro, 0.1f).copy(alpha = 0.8f), Offset(pant.left + pant.width * 0.08f, pant.top + pant.height * 0.3f), Offset(pant.left + pant.width * 0.66f, pant.top + pant.height * 0.3f), 0.3f * u)
        drawPath(Path().apply {
            moveTo(pant.left + pant.width * 0.08f, gy)
            for (k in 1..8) lineTo(pant.left + pant.width * (0.08f + 0.07f * k), gy + (if (k % 2 == 0) -1f else 1f) * pant.height * (if (k == 5) 0.18f else 0.07f))
        }, seg, style = Stroke(0.45f * u, join = StrokeJoin.Round))
        for (k in 0 until 4) drawRect(if (k < 3) seg else seg.copy(alpha = 0.3f), Offset(pant.left + pant.width * 0.74f, pant.bottom - pant.height * (0.2f + 0.16f * k) - pant.height * 0.1f), Size(pant.width * 0.16f, pant.height * 0.1f))
        listOf(VERDE, AMARILLO, ROJO).forEachIndexed { k, col ->
            val o = Offset(pant.right + 5f * u + k * 4.2f * u, panel.center.y)
            drawCircle(c(EPOXI, oscuro, 0.3f), 1.5f * u, o)
            drawCircle(if (k == 0) col else c(col, oscuro, 0.6f).copy(alpha = 0.6f), 1f * u, o)
            if (k == 0) halo(o, 3.5f * u, col.copy(alpha = if (oscuro) 0.5f else 0.3f))
        }
        val tec = Rect(panel.right - panel.width * 0.3f, panel.top + panel.height * 0.25f, panel.right - panel.width * 0.12f, panel.bottom - panel.height * 0.25f)
        for (i in 0 until 4) {
            val bw = tec.width / 4f
            drawRoundRect(c(GRIS_LUZ, oscuro, 0.5f), Offset(tec.left + bw * i + 0.4f * u, tec.top), Size(bw - 0.8f * u, tec.height), CornerRadius(0.5f * u))
            caja(Rect(tec.left + bw * i + 0.4f * u, tec.bottom - 0.4f * u, tec.left + bw * (i + 1) - 0.4f * u, tec.bottom), c(GRIS_OSC, oscuro, 0.4f))
        }
        val cer = Offset(panel.right - panel.width * 0.05f, panel.center.y)
        drawCircle(c(GRIS_LUZ, oscuro, 0.4f), 2.2f * u, cer)
        drawCircle(Color.White.copy(alpha = 0.3f), 1.2f * u, Offset(cer.x - 0.6f * u, cer.y - 0.6f * u))
        drawLine(c(EPOXI, oscuro, 0.2f), Offset(cer.x, cer.y - 1.1f * u), Offset(cer.x, cer.y + 1.1f * u), 0.5f * u, StrokeCap.Round)
        // La puerta: el esmalte (el nicho de las notas), la junta de goma alrededor y las bisagras.
        val puerta = Rect(cuerpo.left + 1.2f * u, h * 0.115f, cuerpo.right - 1.2f * u, h * 0.935f)
        drawRoundRect(c(EPOXI, oscuro, 0.3f), Offset(puerta.left - 0.5f * u, puerta.top - 0.5f * u), Size(puerta.width + 1f * u, puerta.height + 1f * u), CornerRadius(1.8f * u))
        drawRoundRect(c(BLANCO, oscuro, 0.55f), puerta.topLeft, puerta.size, CornerRadius(1.6f * u))
        caja(Rect(puerta.left + 1.6f * u, puerta.top + 0.4f * u, puerta.right - 1.6f * u, puerta.top + 0.9f * u), Color.White.copy(alpha = if (oscuro) 0.1f else 0.7f))
        caja(a, nicho(BLANCO, e, oscuro))
        listOf(0.22f, 0.8f).forEach { f ->
            val y = puerta.top + puerta.height * f
            val bis = Rect(cuerpo.left - 0.2f * u, y - 4f * u, cuerpo.left + 1.8f * u, y + 4f * u)
            drawRoundRect(c(GRIS, oscuro), bis.topLeft, bis.size, CornerRadius(0.6f * u))
            caja(Rect(bis.left, bis.top, bis.right, bis.top + 0.6f * u), Color.White.copy(alpha = 0.4f))
            drawCircle(c(ACERO, oscuro, 0.3f), 0.4f * u, Offset(bis.center.x, bis.top + 1.6f * u)); drawCircle(c(ACERO, oscuro, 0.3f), 0.4f * u, Offset(bis.center.x, bis.bottom - 1.6f * u))
        }
        // La etiqueta de frío (copo azul) y la de riesgo, en la franja de la manija.
        val franja = Rect(a.right, puerta.top, puerta.right, puerta.bottom)
        val et = Offset(franja.center.x, puerta.top + 10f * u)
        val es = minOf(4f * u, franja.width * 0.32f)
        drawRoundRect(c(Color(0xFF2F6FB8), oscuro, 0.35f), Offset(et.x - es, et.y - es), Size(es * 2, es * 2), CornerRadius(es * 0.2f))
        for (k in 0 until 3) {
            val ang = k * Math.PI / 3
            val dx = (es * 0.7f * cos(ang)).toFloat(); val dy = (es * 0.7f * sin(ang)).toFloat()
            drawLine(c(BLANCO, oscuro, 0.2f), Offset(et.x - dx, et.y - dy), Offset(et.x + dx, et.y + dy), es * 0.14f, StrokeCap.Round)
        }
        senal(Offset(franja.center.x, puerta.bottom - 9f * u), es * 0.95f, oscuro)
        // La manija cromada vertical, con sus soportes y su brillo.
        val mx = franja.center.x
        val m0 = puerta.top + puerta.height * 0.28f; val m1 = puerta.top + puerta.height * 0.72f
        listOf(m0, m1).forEach { y -> drawRoundRect(c(ACERO, oscuro, 0.3f), Offset(mx - 1.8f * u, y - 1.6f * u), Size(3.6f * u, 3.2f * u), CornerRadius(0.6f * u)) }
        drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(mx - 0.4f * u, m0), Size(2.8f * u, m1 - m0 + 1f * u), CornerRadius(1.4f * u))
        drawRoundRect(c(GRIS_LUZ, oscuro, 0.45f), Offset(mx - 1.4f * u, m0), Size(2.8f * u, m1 - m0), CornerRadius(1.4f * u))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.2f else 0.75f), Offset(mx - 0.9f * u, m0 + 1f * u), Size(0.7f * u, m1 - m0 - 2f * u), CornerRadius(0.35f * u))
        drawRoundRect(c(GRIS_OSC, oscuro, 0.4f), Offset(mx + 0.6f * u, m0 + 1f * u), Size(0.6f * u, m1 - m0 - 2f * u), CornerRadius(0.3f * u))
        // El zócalo con la rejilla de ventilación y las ruedas.
        val zoc = Rect(cuerpo.left + 1.2f * u, h * 0.938f, cuerpo.right - 1.2f * u, cuerpo.bottom - 0.4f * u)
        caja(zoc, c(GRIS_OSC, oscuro, 0.45f))
        var gx = zoc.left + zoc.width * 0.25f
        while (gx < zoc.right - zoc.width * 0.25f) { drawLine(c(EPOXI, oscuro, 0.3f), Offset(gx, zoc.top + 0.4f * u), Offset(gx, zoc.bottom - 0.4f * u), 0.6f * u); gx += 1.6f * u }
        listOf(cuerpo.left + 6f * u, cuerpo.right - 6f * u).forEach { x ->
            drawCircle(c(EPOXI, oscuro, 0.2f), 1.4f * u, Offset(x, h * 0.968f))
            drawCircle(c(GRIS, oscuro), 0.5f * u, Offset(x, h * 0.968f))
        }
    }

    /** El papel de cada tipo de nota: la nota adhesiva cian, la tarjeta pautada, la ficha de protocolo y la etiqueta amarilla. */
    private fun papel(tipo: String, oscuro: Boolean): Color {
        val p = when (tipo) {
            "idea" -> Color(0xFFD8F1F5)
            "proposito" -> Color(0xFFF7F9FA)
            "recordatorio" -> Color(0xFFFFEFB0)
            else -> Color(0xFFF1EEFB)
        }
        return if (oscuro) mezcla(p, NOCHE, 0.24f) else p
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val k = minOf(r.width, r.height) / 100f
        if (k <= 0f) return
        val hoja = Rect(r.left + 1.5f * k, r.top + 2f * k, r.right - 2.5f * k, r.bottom - 3f * k)
        val p = papel(e.tipo, oscuro)
        val tinta = tintaNota(oscuro)
        // La sombra corta en la puerta y el papel.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(hoja.left + 1.6f * k, hoja.top + 2.2f * k), hoja.size)
        caja(hoja, p)
        val mx = r.width * 0.12f; val my = r.height * 0.16f
        val franjaAlto = my * 0.42f
        when (e.tipo) {
            // La nota adhesiva: la franja de pegamento más hondas y la esquina de abajo que se levanta.
            "idea" -> {
                caja(Rect(hoja.left, hoja.top, hoja.right, hoja.top + franjaAlto), mezcla(p, CIAN, 0.18f))
                val d = hoja.height * 0.12f
                drawPath(Path().apply { moveTo(hoja.right - d, hoja.bottom); lineTo(hoja.right, hoja.bottom - d); lineTo(hoja.right, hoja.bottom); close() }, if (oscuro) mezcla(e.fondo, NOCHE, 0.5f) else Color(0xFFE6ECEF))
                drawPath(Path().apply { moveTo(hoja.right - d, hoja.bottom); lineTo(hoja.right, hoja.bottom - d); lineTo(hoja.right - d * 0.85f, hoja.bottom - d * 0.85f); close() }, mezcla(p, Color.Black, 0.12f))
            }
            // La ficha de protocolo: el encabezado cian y la columna de casillas en el margen izquierdo.
            "proposito" -> {
                caja(Rect(hoja.left, hoja.top, hoja.right, hoja.top + franjaAlto), c(CIAN, oscuro, 0.2f))
                var y = r.top + my + 2f * k; var i = 0
                val cs = minOf(mx * 0.5f, 5f * k)
                while (y < r.bottom - my - cs) {
                    drawRect(c(GRIS_OSC, oscuro, 0.1f), Offset(hoja.left + (mx - (hoja.left - r.left) - cs) / 2f, y), Size(cs, cs), style = Stroke(0.6f * k))
                    if (i == 0) drawPath(Path().apply { val x0 = hoja.left + (mx - (hoja.left - r.left) - cs) / 2f; moveTo(x0 + cs * 0.15f, y + cs * 0.5f); lineTo(x0 + cs * 0.45f, y + cs * 0.85f); lineTo(x0 + cs, y - cs * 0.1f) },
                        c(CIAN, oscuro, 0.1f), style = Stroke(0.8f * k, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    y += cs * 2.6f; i++
                }
            }
            // La etiqueta de criogenia: la banda de rayas de precaución arriba y el código de barras abajo.
            "recordatorio" -> {
                val banda = Rect(hoja.left, hoja.top, hoja.right, hoja.top + franjaAlto)
                clipPath(Path().apply { addRect(banda) }) {
                    caja(banda, c(AMARILLO, oscuro, 0.2f))
                    var x = banda.left - banda.height
                    while (x < banda.right) {
                        drawPath(Path().apply { moveTo(x, banda.bottom); lineTo(x + banda.height, banda.top); lineTo(x + banda.height * 1.8f, banda.top); lineTo(x + banda.height * 0.8f, banda.bottom); close() }, c(TINTA, oscuro, 0.1f))
                        x += banda.height * 2f
                    }
                }
                codigo(Rect(r.right - mx - r.width * 0.34f, r.bottom - my + my * 0.18f, r.right - mx, r.bottom - my + my * 0.5f), tinta.copy(alpha = 0.75f), e.indice * 13 + 5)
            }
            // La tarjeta pautada: la línea roja de encabezado y las pautas sólo en los márgenes.
            else -> {
                drawLine(c(ROJO, oscuro, 0.15f).copy(alpha = 0.7f), Offset(hoja.left, r.top + my - 2f * k), Offset(hoja.right, r.top + my - 2f * k), 0.6f * k)
                drawLine(c(VIOLETA, oscuro, 0.1f).copy(alpha = 0.45f), Offset(r.left + mx - 2.5f * k, hoja.top), Offset(r.left + mx - 2.5f * k, hoja.bottom), 0.5f * k)
            }
        }
        // El símbolo del tipo en la esquina de arriba a la derecha, dentro del margen.
        val sc = Offset(r.right - mx * 0.75f, r.top + my * 0.7f)
        val ss = minOf(mx, my) * 0.36f
        val marca = mezcla(e.acento, tinta, 0.25f)
        when (e.tipo) {
            "idea" -> drawPath(matrazPath(Offset(sc.x, sc.y + ss), ss * 2f), marca)
            "proposito" -> {
                drawCircle(marca, ss, sc, style = Stroke(0.6f * k))
                drawCircle(marca, ss * 0.55f, sc, style = Stroke(0.6f * k))
                drawCircle(marca, ss * 0.18f, sc)
            }
            "recordatorio" -> {
                drawCircle(c(BLANCO, oscuro, 0.2f), ss, sc)
                drawCircle(tinta, ss, sc, style = Stroke(0.6f * k))
                drawLine(tinta, sc, Offset(sc.x, sc.y - ss * 0.65f), 0.6f * k, StrokeCap.Round)
                drawLine(tinta, sc, Offset(sc.x + ss * 0.5f, sc.y), 0.6f * k, StrokeCap.Round)
            }
            else -> {
                drawLine(marca, Offset(sc.x - ss * 0.8f, sc.y + ss * 0.4f), Offset(sc.x + ss * 0.7f, sc.y - ss * 0.5f), 0.7f * k)
                drawCircle(marca, ss * 0.45f, Offset(sc.x - ss * 0.8f, sc.y + ss * 0.4f))
                drawCircle(marca, ss * 0.32f, Offset(sc.x + ss * 0.7f, sc.y - ss * 0.5f))
            }
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), hoja.topLeft, hoja.size, style = Stroke(0.4f * k))
        if (e.fijada) {
            // El imán redondo con su brillo y su sombra.
            val ic = Offset(r.left + r.width * 0.5f, hoja.top + 1.5f * k)
            val ir = minOf(my * 0.42f, 7f * k)
            drawCircle(Color.Black.copy(alpha = 0.3f), ir, Offset(ic.x + 1f * k, ic.y + 1.4f * k))
            drawCircle(mezcla(e.acento, Color.Black, 0.25f), ir, ic)
            drawCircle(e.acento, ir * 0.82f, Offset(ic.x - ir * 0.06f, ic.y - ir * 0.08f))
            drawCircle(Color.White.copy(alpha = 0.55f), ir * 0.28f, Offset(ic.x - ir * 0.35f, ic.y - ir * 0.35f))
        } else {
            // La cinta de rotular, de color según el lugar en el tablero, cruzada arriba.
            val col = TAPONES[(e.indice + 1) % TAPONES.size]
            val cx = r.left + r.width * (0.3f + 0.4f * ((e.indice * 37) % 10) / 10f)
            rotate(if (e.indice % 2 == 0) -5f else 6f, Offset(cx, hoja.top)) {
                drawRect(c(mezcla(col, Color.White, 0.35f), oscuro, 0.3f), Offset(cx - r.width * 0.14f, hoja.top - 2.2f * k), Size(r.width * 0.28f, 5f * k))
                drawRect(Color.White.copy(alpha = 0.3f), Offset(cx - r.width * 0.14f, hoja.top - 2.2f * k), Size(r.width * 0.28f, 1f * k))
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
