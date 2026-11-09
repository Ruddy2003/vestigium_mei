package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Orden: Vida en la casa capitular de acero de la Orden, con el acero pavonado, el carmesí y el latón de su Ideario y sus
 * estantes: planchas remachadas, el escudo del engrane y la espada, estandartes de cola en punta, arcos apuntados y franjas
 * de peligro.
 * - Social: la sala del capítulo vista desde arriba. El piso de planchas de cubierta remachadas, la alfombra carmesí con su
 *   orla y sus flecos, la mesa del consejo de esquinas cortadas con su aro de latón remachado y el tablero de acero cepillado
 *   (el nicho, con el engrane grabado), los sitiales de respaldo de acero con cojín carmesí, los dos tronos de las cabeceras
 *   y cuatro braseros encendidos en las esquinas, con su halo.
 * - Mapas: la mesa táctica. El tablero de acero cepillado con su filete y sus cantoneras de latón, el plano azul sujeto por
 *   esquineras (el nicho), la placa de datos con su pantalla verde, el compás de puntas, el auspex con su barrido, el sello de
 *   lacre y la regla de latón; el canto moldurado, el faldón con tres gavetas y las patas de viga con su travesaño de peligro.
 * - Otras vidas: el relicario. Cornisa con dentículos y el escudo de la Orden, cinco hornacinas de arco apuntado con su
 *   moldura y un engrane en cada enjuta, forradas de acero claro (el nicho, partido en cinco por parteluces), columnas
 *   acanaladas con basa y capitel de latón, y el zócalo con su franja de peligro y su placa.
 * - Íconos: la ciudadela gótica con su rosetón de engrane y el estandarte, la placa de latón que una moneda dentada rasca,
 *   las chinchetas de escudo sobre el plano.
 * - Notas: el tablón de órdenes. Marco de acero remachado, cornisa con el escudo, dos estandartes colgados a los lados, el
 *   panel perforado (el nicho) y el zócalo de peligro; las notas son placas esmaltadas de esquinas cortadas, remachadas, con
 *   la banda de su tipo; las fijadas llevan la pinza de latón y la cinta carmesí.
 */
internal object VidaOrden : DisenoVida {
    private val ACERO = Color(0xFF3A4048)
    private val ACERO_LUZ = Color(0xFF5E6670)
    private val ACERO_OSC = Color(0xFF1C2127)
    private val ACERO_CLARO = Color(0xFF8A939D)
    private val ACERO_BRILLO = Color(0xFFB4BCC5)
    private val LATON = Color(0xFFD6B25A)
    private val LATON_LUZ = Color(0xFFF0D98E)
    private val LATON_OSC = Color(0xFF8A7030)
    private val CARMESI = Color(0xFFB0202A)
    private val CARMESI_OSC = Color(0xFF6E1018)
    private val AMARILLO = Color(0xFFE0B83A)
    private val NEGRO = Color(0xFF101214)
    private val PLANO = Color(0xFF1E3A5C)
    private val HOLO = Color(0xFF8FB3C8)
    private val FOSFORO = Color(0xFF7CE0A0)
    private val PANTALLA = Color(0xFF0E241A)
    private val FUEGO = Color(0xFFFFA040)
    private val BRASA = Color(0xFFD8461E)
    private val LLAMA = Color(0xFFFFE08A)

    /** El esmalte de las placas de las notas: blanco de día, acero pavonado de noche. */
    private val ESMALTE = Color(0xFFF6F7F8)
    private val ESMALTE_NOCHE = Color(0xFF2B3036)

    // ---------------------------------------------------------------------
    //  Ayudas
    // ---------------------------------------------------------------------

    private fun DrawScope.u() = size.minDimension / 100f

    /** Un color entre dos. */
    private fun mezcla(a: Color, b: Color, f: Float) = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en modo oscuro: el acero se hunde en la penumbra, como en el Ideario. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.28f) else c

    /** El latón se hunde menos, para que siga brillando de noche. */
    private fun tl(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.16f) else c

    /** El carmesí de la Orden, teñido con el acento del esquema. */
    private fun carmesi(e: Color, oscuro: Boolean) = t(mezcla(CARMESI, e, 0.4f), oscuro)

    /** Un número estable de 0 a 1. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** El nicho de un mueble, en píxeles del lienzo. */
    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    /** El acero del nicho: cercano al fondo de la tarjeta, para que el contenido se lea encima. */
    private fun aceroNicho(e: EstadoMueble, oscuro: Boolean) =
        if (oscuro) mezcla(e.fondo, ACERO, 0.36f) else mezcla(e.fondo, Color(0xFF98A1AB), 0.15f)

    /** Un rectángulo con las cuatro esquinas cortadas en [ch]. */
    private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
        lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    /** Un engrane: dientes cuadrados alrededor de [c]. */
    private fun engrane(c: Offset, r: Float, dientes: Int): Path = Path().apply {
        val n = dientes * 4
        for (k in 0 until n) {
            val a = 2 * Math.PI * k / n
            val rr = if ((k / 2) % 2 == 0) r else r * 0.8f
            val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /** Un arco apuntado (ojival): [r] con la punta arriba al centro; [flecha] es el alto de la parte curva. */
    private fun arcoApuntado(r: Rect, flecha: Float): Path = Path().apply {
        val cx = r.center.x
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + flecha)
        cubicTo(r.left, r.top + flecha * 0.45f, cx - r.width * 0.22f, r.top + flecha * 0.1f, cx, r.top)
        cubicTo(cx + r.width * 0.22f, r.top + flecha * 0.1f, r.right, r.top + flecha * 0.45f, r.right, r.top + flecha)
        lineTo(r.right, r.bottom); close()
    }

    /** El escudo de la Orden: arriba recto y abajo en punta, de [ancho] × [alto] con el centro de su borde de arriba en [c]. */
    private fun escudo(c: Offset, ancho: Float, alto: Float): Path = Path().apply {
        moveTo(c.x - ancho / 2, c.y); lineTo(c.x + ancho / 2, c.y); lineTo(c.x + ancho / 2, c.y + alto * 0.45f)
        quadraticTo(c.x + ancho / 2, c.y + alto * 0.82f, c.x, c.y + alto)
        quadraticTo(c.x - ancho / 2, c.y + alto * 0.82f, c.x - ancho / 2, c.y + alto * 0.45f); close()
    }

    /** Una sombra suave debajo de algo. */
    private fun DrawScope.sombraSuave(r: Rect, radio: Float, alcance: Float, fuerza: Float = 0.22f) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.6f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(radio + e))
        }
    }

    /** Los cuatro lados de [r] más hondos que su centro. */
    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.5f)), r.bottom - ancho, r.bottom), Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.7f), Color.Transparent), r.left, r.left + ancho), r.topLeft, Size(ancho, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.7f)), r.right - ancho, r.right), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    /** Un bisel: luz arriba e izquierda, sombra abajo y derecha. */
    private fun DrawScope.bisel(r: Rect, luz: Color, sombra: Color, ancho: Float) {
        drawRect(luz, r.topLeft, Size(r.width, ancho))
        drawRect(luz.copy(alpha = luz.alpha * 0.7f), r.topLeft, Size(ancho, r.height))
        drawRect(sombra, Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(sombra.copy(alpha = sombra.alpha * 0.8f), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    /** Un halo de luz alrededor de [c]. */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color, fuerza: Float) =
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = fuerza), color.copy(alpha = fuerza * 0.35f), Color.Transparent), c, r.coerceAtLeast(1f)), r, c)

    /** Un remache: la cabeza, su punto de luz y su borde. */
    private fun DrawScope.remache(p: Offset, r: Float, oscuro: Boolean, cuerpo: Color = ACERO_CLARO) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(p.x + r * 0.3f, p.y + r * 0.35f))
        drawCircle(t(cuerpo, oscuro), r, p)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.25f else 0.45f), r * 0.42f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
    }

    /** Remaches a lo largo de una línea, uno cada [paso]. */
    private fun DrawScope.remaches(a: Offset, b: Offset, paso: Float, r: Float, oscuro: Boolean, cuerpo: Color = ACERO_CLARO) {
        val l = kotlin.math.hypot(b.x - a.x, b.y - a.y)
        val n = (l / paso).toInt().coerceIn(1, 200)
        for (k in 0..n) {
            val f = k / n.toFloat()
            remache(Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f), r, oscuro, cuerpo)
        }
    }

    /** Una plancha de acero: su tono, el bisel y un remache en cada esquina. */
    private fun DrawScope.plancha(r: Rect, tono: Color, oscuro: Boolean, u: Float) {
        drawRect(t(tono, oscuro), r.topLeft, r.size)
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.06f else 0.1f), Color.Black.copy(alpha = 0.32f), 0.5f * u)
        val m = minOf(2.2f * u, r.width * 0.2f, r.height * 0.2f)
        listOf(Offset(r.left + m, r.top + m), Offset(r.right - m, r.top + m), Offset(r.left + m, r.bottom - m), Offset(r.right - m, r.bottom - m))
            .forEach { remache(it, 0.6f * u, oscuro) }
    }

    /** Franjas de peligro amarillas y negras en [r], con su filo oscuro arriba y abajo. */
    private fun DrawScope.franjas(r: Rect, oscuro: Boolean) {
        drawRect(t(NEGRO, oscuro), r.topLeft, r.size)
        val paso = r.height * 2.4f
        if (paso < 1f) return
        val rayas = Path()
        var x = r.left - r.height * 1.2f; var k = 0
        while (x < r.right && k < 200) {
            rayas.moveTo(x, r.bottom); rayas.lineTo(x + r.height, r.top); rayas.lineTo(x + r.height + paso / 2, r.top); rayas.lineTo(x + paso / 2, r.bottom); rayas.close()
            x += paso; k++
        }
        clipRect(r.left, r.top, r.right, r.bottom) { drawPath(rayas, tl(AMARILLO, oscuro)) }
        drawLine(Color.Black.copy(alpha = 0.5f), Offset(r.left, r.top), Offset(r.right, r.top), r.height * 0.12f)
        drawLine(Color.Black.copy(alpha = 0.5f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), r.height * 0.12f)
    }

    /** El escudo de la Orden: carmesí con orla de latón, el engrane y la espada encima. [c] es el centro de su borde de arriba. */
    private fun DrawScope.emblema(c: Offset, ancho: Float, alto: Float, acento: Color, oscuro: Boolean) {
        val p = escudo(c, ancho, alto)
        drawPath(escudo(Offset(c.x + ancho * 0.05f, c.y + alto * 0.05f), ancho, alto), Color.Black.copy(alpha = 0.35f))
        drawPath(p, carmesi(acento, oscuro))
        clipPath(p) { drawRect(Color.Black.copy(alpha = 0.18f), Offset(c.x, c.y), Size(ancho, alto)) }
        drawPath(p, tl(LATON, oscuro), style = Stroke(ancho * 0.07f))
        val g = Offset(c.x, c.y + alto * 0.42f)
        drawPath(engrane(g, ancho * 0.26f, 10), tl(LATON, oscuro))
        drawCircle(carmesi(acento, oscuro), ancho * 0.1f, g)
        // La espada: hoja de acero claro, guarda y pomo de latón.
        drawLine(t(ACERO_BRILLO, oscuro), Offset(c.x, c.y + alto * 0.14f), Offset(c.x, c.y + alto * 0.84f), ancho * 0.07f)
        drawLine(tl(LATON_OSC, oscuro), Offset(c.x - ancho * 0.17f, c.y + alto * 0.24f), Offset(c.x + ancho * 0.17f, c.y + alto * 0.24f), ancho * 0.07f, StrokeCap.Round)
        drawCircle(tl(LATON, oscuro), ancho * 0.05f, Offset(c.x, c.y + alto * 0.11f))
    }

    /** Un estandarte carmesí de cola en punta colgado de su barra: [r] es el paño. */
    private fun DrawScope.estandarte(r: Rect, acento: Color, oscuro: Boolean, u: Float) {
        val pano = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); lineTo(r.center.x, r.bottom - r.width * 0.42f); lineTo(r.left, r.bottom); close()
        }
        drawPath(Path().apply { addPath(pano, Offset(0.8f * u, 0.8f * u)) }, Color.Black.copy(alpha = 0.3f))
        drawPath(pano, carmesi(acento, oscuro))
        // Los pliegues: una franja de sombra y una de luz.
        clipPath(pano) {
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.left + r.width * 0.62f, r.top), Size(r.width * 0.38f, r.height))
            drawRect(Color.White.copy(alpha = 0.08f), Offset(r.left + r.width * 0.14f, r.top), Size(r.width * 0.16f, r.height))
        }
        drawLine(tl(LATON, oscuro), Offset(r.left + r.width * 0.1f, r.top), Offset(r.left + r.width * 0.1f, r.bottom - r.width * 0.12f), 0.5f * u)
        drawLine(tl(LATON, oscuro), Offset(r.right - r.width * 0.1f, r.top), Offset(r.right - r.width * 0.1f, r.bottom - r.width * 0.12f), 0.5f * u)
        val g = Offset(r.center.x, r.top + r.height * 0.38f)
        drawPath(engrane(g, r.width * 0.26f, 8), tl(LATON, oscuro))
        drawCircle(carmesi(acento, oscuro), r.width * 0.1f, g)
        // La barra y sus remates.
        drawLine(t(ACERO_OSC, oscuro), Offset(r.left - 1.2f * u, r.top), Offset(r.right + 1.2f * u, r.top), 1.1f * u, StrokeCap.Round)
        drawCircle(tl(LATON, oscuro), 0.9f * u, Offset(r.left - 1.2f * u, r.top))
        drawCircle(tl(LATON, oscuro), 0.9f * u, Offset(r.right + 1.2f * u, r.top))
    }

    /** Una columna acanalada de acero en [r], con su basa y su capitel de latón. */
    private fun DrawScope.columna(r: Rect, oscuro: Boolean, u: Float) {
        val cap = minOf(r.height * 0.12f, 4.5f * u)
        val fuste = Rect(r.left + r.width * 0.14f, r.top + cap, r.right - r.width * 0.14f, r.bottom - cap)
        drawRect(Brush.horizontalGradient(listOf(t(ACERO_LUZ, oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
        for (k in 1..3) {
            val x = fuste.left + fuste.width * k / 4f
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(x, fuste.top + 1f * u), Offset(x, fuste.bottom - 1f * u), 0.45f * u)
            drawLine(Color.White.copy(alpha = 0.12f), Offset(x - 0.5f * u, fuste.top + 1f * u), Offset(x - 0.5f * u, fuste.bottom - 1f * u), 0.3f * u)
        }
        // El capitel y la basa: dos molduras de latón cada uno.
        listOf(Rect(r.left, r.top, r.right, r.top + cap * 0.55f), Rect(r.left + r.width * 0.07f, r.top + cap * 0.55f, r.right - r.width * 0.07f, r.top + cap),
            Rect(r.left + r.width * 0.07f, r.bottom - cap, r.right - r.width * 0.07f, r.bottom - cap * 0.55f), Rect(r.left, r.bottom - cap * 0.55f, r.right, r.bottom)).forEach { m ->
            drawRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), m.top, m.bottom), m.topLeft, m.size)
        }
    }

    // ---------------------------------------------------------------------
    //  Los nichos
    // ---------------------------------------------------------------------

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.15f, 0.2f, 0.85f, 0.8f)
        MuebleVida.MAPAS -> Rect(0.15f, 0.1f, 0.85f, 0.74f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.05f, 0.27f, 0.95f, 0.83f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.155f, 0.925f, 0.885f)
    }

    // ---------------------------------------------------------------------
    //  Social: la sala del capítulo, vista desde arriba
    // ---------------------------------------------------------------------

    /**
     * Un sitial visto desde arriba, mirando hacia +y antes de girar [giro] grados alrededor de [c]: el respaldo de acero con
     * sus remates de latón, los brazos y el cojín carmesí. El [trono] es más ancho y lleva el engrane en el respaldo.
     */
    private fun DrawScope.sitial(c: Offset, ancho: Float, fondo: Float, giro: Float, trono: Boolean, acento: Color, oscuro: Boolean, u: Float) {
        rotate(giro, c) {
            val asiento = Rect(c.x - ancho / 2, c.y - fondo * 0.2f, c.x + ancho / 2, c.y + fondo / 2)
            val alto = fondo * (if (trono) 0.4f else 0.34f)
            val respaldo = Rect(c.x - ancho / 2 - 0.9f * u, c.y - fondo / 2, c.x + ancho / 2 + 0.9f * u, c.y - fondo / 2 + alto)
            val cresta = alto * 0.42f
            val silueta = Path().apply {
                moveTo(respaldo.left, respaldo.top + cresta); lineTo(c.x - ancho * 0.2f, respaldo.top + cresta); lineTo(c.x, respaldo.top)
                lineTo(c.x + ancho * 0.2f, respaldo.top + cresta); lineTo(respaldo.right, respaldo.top + cresta); lineTo(respaldo.right, respaldo.bottom)
                lineTo(respaldo.left, respaldo.bottom); close()
            }
            // La sombra del sitial sobre las planchas.
            drawRoundRect(Color.Black.copy(alpha = 0.38f), Offset(respaldo.left + 0.9f * u, respaldo.top + cresta + 1.1f * u), Size(respaldo.width, asiento.bottom - respaldo.top - cresta), CornerRadius(1.2f * u))
            // El asiento de acero y el cojín capitoneado.
            drawRoundRect(t(ACERO_OSC, oscuro), asiento.topLeft, asiento.size, CornerRadius(1.2f * u))
            val cojin = Rect(asiento.left + 1.6f * u, asiento.top + 0.4f * u, asiento.right - 1.6f * u, asiento.bottom - 0.6f * u)
            drawRoundRect(carmesi(acento, oscuro), cojin.topLeft, cojin.size, CornerRadius(1.6f * u))
            drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), cojin.top, cojin.bottom), cojin.topLeft, cojin.size, CornerRadius(1.6f * u))
            for (i in 0 until 2) for (j in 0 until 2) {
                val b = Offset(cojin.left + cojin.width * (0.3f + 0.4f * i), cojin.top + cojin.height * (0.32f + 0.36f * j))
                drawCircle(Color.Black.copy(alpha = 0.35f), 0.42f * u, b)
                drawCircle(tl(LATON, oscuro), 0.22f * u, Offset(b.x - 0.1f * u, b.y - 0.1f * u))
            }
            // Los brazos, con su pomo de latón al frente.
            listOf(asiento.left, asiento.right - 1.4f * u).forEach { x ->
                drawRoundRect(Brush.horizontalGradient(listOf(t(ACERO_BRILLO, oscuro), t(ACERO_LUZ, oscuro), t(ACERO, oscuro)), x, x + 1.4f * u), Offset(x, asiento.top - 0.4f * u), Size(1.4f * u, asiento.height * 0.9f), CornerRadius(0.7f * u))
                drawCircle(tl(LATON, oscuro), 0.8f * u, Offset(x + 0.7f * u, asiento.top + asiento.height * 0.86f))
            }
            // El respaldo alto, con su cresta en punta, su filo de latón y los remates.
            drawPath(silueta, Brush.verticalGradient(listOf(t(ACERO_BRILLO, oscuro), t(ACERO_LUZ, oscuro), t(ACERO, oscuro)), respaldo.top, respaldo.bottom))
            drawPath(silueta, tl(LATON_OSC, oscuro), style = Stroke(0.4f * u))
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(respaldo.left + 0.5f * u, respaldo.bottom - 0.3f * u), Offset(respaldo.right - 0.5f * u, respaldo.bottom - 0.3f * u), 0.5f * u)
            listOf(respaldo.left, respaldo.right).forEach { x ->
                val p = Offset(x, respaldo.top + cresta + (respaldo.height - cresta) / 2)
                drawCircle(tl(LATON_OSC, oscuro), 1.1f * u, p)
                drawCircle(tl(LATON_LUZ, oscuro), 0.45f * u, Offset(p.x - 0.25f * u, p.y - 0.25f * u))
            }
            if (trono) {
                val g = Offset(c.x, respaldo.top + cresta + (respaldo.height - cresta) * 0.45f)
                val rg = (respaldo.height - cresta) * 0.75f
                drawPath(engrane(g, rg, 10), tl(LATON_OSC, oscuro))
                drawPath(engrane(g, rg * 0.86f, 10), tl(LATON, oscuro))
                drawCircle(carmesi(acento, oscuro), rg * 0.36f, g)
            } else drawCircle(carmesi(acento, oscuro), 0.6f * u, Offset(c.x, respaldo.top + cresta * 1.2f))
        }
    }

    /** Un brasero de trípode visto desde arriba, con las brasas, la llama y su halo. */
    private fun DrawScope.brasero(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        halo(c, r * (if (oscuro) 3.4f else 2.6f), FUEGO, if (oscuro) 0.34f else 0.2f)
        for (k in 0 until 3) {
            val a = Math.toRadians(-90.0 + k * 120.0 + 60.0)
            val p = Offset(c.x + r * 1.28f * cos(a).toFloat(), c.y + r * 1.28f * sin(a).toFloat())
            drawLine(t(ACERO_OSC, oscuro), c, p, r * 0.18f, StrokeCap.Round)
            drawCircle(tl(LATON, oscuro), r * 0.13f, p)
        }
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + r * 0.12f, c.y + r * 0.16f))
        drawCircle(t(ACERO_OSC, oscuro), r, c)
        drawCircle(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), c.y - r, c.y + r), r, c, style = Stroke(r * 0.16f))
        drawCircle(Color(0xFF2A0D06), r * 0.74f, c)
        for (k in 0 until 7) {
            val a = k * 2.4 + 0.3
            val d = r * (0.18f + 0.42f * ((k * 37) % 10) / 10f)
            drawCircle(mezcla(BRASA, FUEGO, (k % 3) / 2f), r * (0.14f + 0.03f * (k % 3)), Offset(c.x + d * cos(a).toFloat(), c.y + d * sin(a).toFloat()))
        }
        drawCircle(Brush.radialGradient(listOf(Color.White, LLAMA, FUEGO, BRASA.copy(alpha = 0f)), c, r * 0.62f), r * 0.62f, c)
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.SOCIAL)
        // El piso: planchas de cubierta remachadas, cada una con su tono.
        drawRect(t(ACERO_OSC, oscuro))
        val lado = h / 3.6f
        val tonos = Random(e.semilla.toLong())
        val cols = (w / lado).toInt().coerceAtLeast(1) + 1
        val pw = w / cols
        var fila = 0; var y = -lado * 0.4f
        while (y < h) {
            for (k in 0 until cols) {
                val x = k * pw + if (fila % 2 == 0) 0f else -pw / 2
                val r = Rect(x + 0.4f * u, y + 0.4f * u, x + pw - 0.4f * u, y + lado - 0.4f * u)
                plancha(r, mezcla(ACERO, ACERO_LUZ, 0.06f + 0.22f * tonos.nextFloat()), oscuro, u)
                if (fila % 2 == 1 && k == cols - 1) plancha(Rect(r.left + pw, r.top, r.right + pw, r.bottom), mezcla(ACERO, ACERO_LUZ, 0.1f), oscuro, u)
            }
            y += lado; fila++
        }
        // La alfombra carmesí de cabecera a cabecera, con su orla de latón y sus flecos.
        val alfombra = Rect(w * 0.02f, a.center.y - a.height * 0.36f, w * 0.98f, a.center.y + a.height * 0.36f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(alfombra.left + 0.6f * u, alfombra.top + 0.8f * u), alfombra.size)
        drawRect(carmesi(e.acento, oscuro), alfombra.topLeft, alfombra.size)
        drawRect(mezcla(carmesi(e.acento, oscuro), Color.Black, 0.3f), Offset(alfombra.left, alfombra.top), Size(alfombra.width, 1.6f * u))
        drawRect(mezcla(carmesi(e.acento, oscuro), Color.Black, 0.3f), Offset(alfombra.left, alfombra.bottom - 1.6f * u), Size(alfombra.width, 1.6f * u))
        listOf(2.4f * u, 3.4f * u).forEach { m ->
            drawRect(tl(LATON, oscuro), Offset(alfombra.left + m, alfombra.top + m), Size(alfombra.width - 2 * m, alfombra.height - 2 * m), style = Stroke(0.35f * u))
        }
        listOf(alfombra.left, alfombra.right).forEach { x ->
            var fy = alfombra.top + 0.8f * u
            val s = if (x < w / 2) -1f else 1f
            while (fy < alfombra.bottom) { drawLine(tl(LATON_OSC, oscuro), Offset(x, fy), Offset(x + s * 1.4f * u, fy), 0.35f * u); fy += 1.1f * u }
        }
        // Las franjas de peligro de los bordes de la sala.
        franjas(Rect(0f, 0f, w, 2.6f * u), oscuro)
        franjas(Rect(0f, h - 2.6f * u, w, h), oscuro)
        // Los cuatro braseros de las esquinas.
        val rb = minOf(5.6f * u, a.left * 0.32f)
        val bx = a.left * 0.48f
        listOf(Offset(bx, a.top * 0.62f + 1.4f * u), Offset(w - bx, a.top * 0.62f + 1.4f * u), Offset(bx, h - a.top * 0.62f - 1.4f * u), Offset(w - bx, h - a.top * 0.62f - 1.4f * u))
            .forEach { brasero(it, rb, oscuro, u) }
        // Los sitiales: tantos como quepan por lado, metidos bajo el aro de la mesa.
        val aro = 3.4f * u
        val fondoS = minOf(a.top - aro - 3.4f * u, 11.5f * u)
        val anchoS = fondoS * 1.05f
        val n = ((a.width - 4f * u) / (anchoS * 1.9f)).toInt().coerceIn(3, 7)
        for (k in 0 until n) {
            val x = a.left + a.width * (k + 0.5f) / n
            sitial(Offset(x, a.top - aro - fondoS * 0.42f), anchoS, fondoS, 0f, false, e.acento, oscuro, u)
            sitial(Offset(x, a.bottom + aro + fondoS * 0.42f), anchoS, fondoS, 180f, false, e.acento, oscuro, u)
        }
        // Los tronos de las cabeceras.
        val fondoT = minOf(fondoS * 1.15f, a.left - aro - rb * 0.6f)
        sitial(Offset(a.left - aro - fondoT * 0.42f, a.center.y), anchoS * 1.35f, fondoT, -90f, true, e.acento, oscuro, u)
        sitial(Offset(a.right + aro + fondoT * 0.42f, a.center.y), anchoS * 1.35f, fondoT, 90f, true, e.acento, oscuro, u)
        // La mesa: el canto oscuro, el aro de latón remachado y el filete de acero.
        val fuera = Rect(a.left - aro, a.top - aro, a.right + aro, a.bottom + aro)
        sombraSuave(fuera, 2f * u, 4f * u, if (oscuro) 0.5f else 0.35f)
        drawPath(ochavado(Rect(fuera.left - 0.7f * u, fuera.top - 0.7f * u, fuera.right + 0.7f * u, fuera.bottom + 0.7f * u), 5f * u), t(ACERO_OSC, oscuro))
        drawPath(ochavado(fuera, 4.6f * u), Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), fuera.top, fuera.bottom))
        val dentro = Rect(fuera.left + 1.7f * u, fuera.top + 1.7f * u, fuera.right - 1.7f * u, fuera.bottom - 1.7f * u)
        drawPath(ochavado(dentro, 3.2f * u), t(ACERO, oscuro))
        remaches(Offset(fuera.left + 5.4f * u, fuera.top + 0.85f * u), Offset(fuera.right - 5.4f * u, fuera.top + 0.85f * u), 7f * u, 0.4f * u, oscuro, LATON_OSC)
        remaches(Offset(fuera.left + 5.4f * u, fuera.bottom - 0.85f * u), Offset(fuera.right - 5.4f * u, fuera.bottom - 0.85f * u), 7f * u, 0.4f * u, oscuro, LATON_OSC)
        // El tablero de acero cepillado (el nicho), con el engrane de la Orden grabado.
        val fondo = aceroNicho(e, oscuro)
        drawRect(fondo, a.topLeft, a.size)
        var ly = a.top + 1.2f * u
        while (ly < a.bottom) { drawLine((if (oscuro) Color.White else Color.Black).copy(alpha = 0.025f), Offset(a.left, ly), Offset(a.right, ly), 0.3f * u); ly += 1.5f * u }
        val tinte = if (oscuro) e.secundario else e.tinta
        drawPath(engrane(a.center, a.height * 0.38f, 16), tinte.copy(alpha = 0.05f), style = Stroke(0.5f * u))
        drawCircle(tinte.copy(alpha = 0.04f), a.height * 0.22f, a.center, style = Stroke(0.5f * u))
        drawRect(e.secundario.copy(alpha = 0.35f), Offset(a.left + 1.2f * u, a.top + 1.2f * u), Size(a.width - 2.4f * u, a.height - 2.4f * u), style = Stroke(0.3f * u))
        sombraInterior(a, 2.4f * u, if (oscuro) 0.3f else 0.12f)
    }

    // ---------------------------------------------------------------------
    //  Mapas: la mesa táctica
    // ---------------------------------------------------------------------

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.MAPAS)
        // La pared de planchas detrás de la mesa.
        drawRect(t(ACERO_OSC, oscuro))
        val pw = w / 4f
        for (k in 0 until 4) plancha(Rect(k * pw + 0.4f * u, -2f * u, (k + 1) * pw - 0.4f * u, h * 0.5f), mezcla(ACERO, ACERO_OSC, 0.4f + 0.2f * azar(e.semilla, k)), oscuro, u)
        for (k in 0 until 4) plancha(Rect(k * pw + 0.4f * u, h * 0.5f + 0.4f * u, (k + 1) * pw - 0.4f * u, h + 2f * u), mezcla(ACERO, ACERO_OSC, 0.45f + 0.2f * azar(e.semilla, k + 9)), oscuro, u)
        // Las patas de viga, con su travesaño de franjas de peligro.
        val tablero = Rect(w * 0.015f, h * 0.025f, w * 0.985f, h * 0.8f)
        val pie = h - 1f * u
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, pie - 0.6f * u), Size(w, h - pie + 0.6f * u))
        franjas(Rect(w * 0.06f, h * 0.935f, w * 0.94f, h * 0.935f + 2.6f * u), oscuro)
        listOf(w * 0.055f, w * 0.945f).forEach { x ->
            val pata = Rect(x - 3f * u, h * 0.86f, x + 3f * u, pie - 1.2f * u)
            drawRect(Brush.horizontalGradient(listOf(t(ACERO_LUZ, oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro)), pata.left, pata.right), pata.topLeft, pata.size)
            drawRect(t(ACERO_OSC, oscuro), Offset(pata.center.x - 0.5f * u, pata.top), Size(1f * u, pata.height))
            drawRoundRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), pie - 1.6f * u, pie), Offset(pata.left - 1.4f * u, pie - 1.6f * u), Size(pata.width + 2.8f * u, 1.6f * u), CornerRadius(0.4f * u))
        }
        // El faldón con sus tres gavetas.
        val faldon = Rect(w * 0.03f, h * 0.86f, w * 0.97f, h * 0.925f)
        drawRect(t(ACERO_OSC, oscuro), faldon.topLeft, faldon.size)
        for (k in 0 until 3) {
            val cx = w * (0.25f + 0.25f * k)
            val g = Rect(cx - w * 0.1f, faldon.top + 0.5f * u, cx + w * 0.1f, faldon.bottom - 0.5f * u)
            drawRect(t(ACERO, oscuro), g.topLeft, g.size)
            bisel(g, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.4f), 0.4f * u)
            drawRect(tl(LATON, oscuro), Offset(cx - 3f * u, g.top + 0.8f * u), Size(6f * u, g.height * 0.45f))
            drawRoundRect(t(NEGRO, oscuro), Offset(cx - 4f * u, g.bottom - 1.6f * u), Size(8f * u, 0.9f * u), CornerRadius(0.45f * u))
        }
        // La sombra del tablero sobre la pared.
        sombraSuave(tablero, 1f * u, 4f * u, if (oscuro) 0.5f else 0.35f)
        // El canto moldurado, remachado.
        val canto = Rect(w * 0.005f, tablero.bottom, w * 0.995f, h * 0.865f)
        drawRect(Brush.verticalGradient(listOf(t(ACERO_LUZ, oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro)), canto.top, canto.bottom), canto.topLeft, canto.size)
        drawLine(tl(LATON, oscuro), Offset(canto.left, canto.top + 0.5f * u), Offset(canto.right, canto.top + 0.5f * u), 0.9f * u)
        drawLine(Color.Black.copy(alpha = 0.45f), Offset(canto.left, canto.bottom), Offset(canto.right, canto.bottom), 0.6f * u)
        remaches(Offset(canto.left + 3f * u, canto.center.y + 0.5f * u), Offset(canto.right - 3f * u, canto.center.y + 0.5f * u), 9f * u, 0.55f * u, oscuro)
        // El tablero de acero cepillado, con su filete y sus cantoneras de latón.
        drawRect(t(ACERO, oscuro), tablero.topLeft, tablero.size)
        var ly = tablero.top + 1f * u
        var j = 0
        while (ly < tablero.bottom) { drawLine(Color.White.copy(alpha = 0.03f + 0.03f * azar(e.semilla + 5, j)), Offset(tablero.left, ly), Offset(tablero.right, ly), 0.3f * u); ly += 1.3f * u; j++ }
        drawRect(tl(LATON_OSC, oscuro), Offset(tablero.left + 1.8f * u, tablero.top + 1.8f * u), Size(tablero.width - 3.6f * u, tablero.height - 3.6f * u), style = Stroke(0.5f * u))
        bisel(tablero, Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.3f), 0.5f * u)
        val k6 = 6.5f * u
        listOf(tablero.topLeft to Offset(1f, 1f), Offset(tablero.right, tablero.top) to Offset(-1f, 1f), Offset(tablero.left, tablero.bottom) to Offset(1f, -1f), Offset(tablero.right, tablero.bottom) to Offset(-1f, -1f))
            .forEach { (o, s) ->
                val p = Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * k6, o.y); lineTo(o.x, o.y + s.y * k6); close() }
                drawPath(p, Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), o, Offset(o.x + s.x * k6, o.y + s.y * k6)))
                remache(Offset(o.x + s.x * 1.9f * u, o.y + s.y * 1.9f * u), 0.55f * u, oscuro, LATON_OSC)
            }
        // Los instrumentos de la izquierda: la placa de datos y el compás de puntas.
        val izq = Rect(tablero.left + 3.2f * u, a.top, a.left - 3.2f * u, a.bottom)
        val der = Rect(a.right + 3.2f * u, a.top, tablero.right - 3.2f * u, a.bottom)
        val m = izq.width
        if (m > 2f * u) {
            val placa = Rect(izq.left, izq.top + 1f * u, izq.right, izq.top + 1f * u + m * 1.25f)
            drawRoundRect(Color.Black.copy(alpha = 0.4f), Offset(placa.left + 0.8f * u, placa.top + 1f * u), placa.size, CornerRadius(1.2f * u))
            drawRoundRect(t(ACERO_OSC, oscuro), placa.topLeft, placa.size, CornerRadius(1.2f * u))
            drawRoundRect(t(ACERO_LUZ, oscuro), placa.topLeft, placa.size, CornerRadius(1.2f * u), style = Stroke(0.4f * u))
            val pant = Rect(placa.left + m * 0.12f, placa.top + m * 0.14f, placa.right - m * 0.12f, placa.bottom - m * 0.36f)
            drawRect(PANTALLA, pant.topLeft, pant.size)
            for (k in 0 until 5) {
                val yy = pant.top + pant.height * (0.16f + 0.17f * k)
                drawLine(FOSFORO.copy(alpha = if (oscuro) 0.85f else 0.7f), Offset(pant.left + pant.width * 0.12f, yy), Offset(pant.left + pant.width * (0.4f + 0.45f * azar(e.semilla + 3, k)), yy), 0.45f * u)
            }
            if (oscuro) halo(pant.center, pant.width * 0.9f, FOSFORO, 0.12f)
            drawCircle(carmesi(e.acento, oscuro), m * 0.07f, Offset(placa.center.x - m * 0.18f, placa.bottom - m * 0.18f))
            drawCircle(tl(LATON, oscuro), m * 0.07f, Offset(placa.center.x + m * 0.18f, placa.bottom - m * 0.18f))
            // El compás de puntas, abierto.
            val bis = Offset(izq.center.x, placa.bottom + m * 0.42f)
            val largo = minOf(m * 1.5f, izq.bottom - bis.y - 1f * u)
            listOf(-1f, 1f).forEach { s ->
                val p = Offset(bis.x + s * m * 0.3f, bis.y + largo)
                drawLine(Color.Black.copy(alpha = 0.35f), Offset(bis.x + 0.6f * u, bis.y + 0.8f * u), Offset(p.x + 0.6f * u, p.y + 0.8f * u), 0.9f * u)
                drawLine(t(ACERO_BRILLO, oscuro), bis, Offset(bis.x + (p.x - bis.x) * 0.82f, bis.y + (p.y - bis.y) * 0.82f), 0.9f * u, StrokeCap.Round)
                drawLine(t(ACERO_OSC, oscuro), Offset(bis.x + (p.x - bis.x) * 0.8f, bis.y + (p.y - bis.y) * 0.8f), p, 0.45f * u, StrokeCap.Round)
            }
            drawArc(tl(LATON_OSC, oscuro), 30f, 120f, false, Offset(bis.x - m * 0.24f, bis.y + largo * 0.3f), Size(m * 0.48f, largo * 0.4f), style = Stroke(0.35f * u))
            drawCircle(tl(LATON, oscuro), 1.3f * u, bis)
            drawCircle(tl(LATON_OSC, oscuro), 0.5f * u, bis)
        }
        // Los de la derecha: el auspex con su barrido y el sello de lacre.
        val n = der.width
        if (n > 2f * u) {
            val c = Offset(der.center.x, der.top + n * 0.5f + 1f * u)
            val r = n * 0.48f
            drawCircle(Color.Black.copy(alpha = 0.4f), r, Offset(c.x + 0.8f * u, c.y + 1f * u))
            drawCircle(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), r, c)
            drawCircle(PANTALLA, r * 0.78f, c)
            drawArc(Brush.sweepGradient(listOf(Color.Transparent, FOSFORO.copy(alpha = 0.55f), Color.Transparent), c), -90f, 70f, true, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f))
            listOf(0.3f, 0.55f).forEach { f -> drawCircle(FOSFORO.copy(alpha = 0.35f), r * 0.78f * f, c, style = Stroke(0.3f * u)) }
            drawLine(FOSFORO.copy(alpha = 0.35f), Offset(c.x - r * 0.78f, c.y), Offset(c.x + r * 0.78f, c.y), 0.3f * u)
            drawLine(FOSFORO.copy(alpha = 0.35f), Offset(c.x, c.y - r * 0.78f), Offset(c.x, c.y + r * 0.78f), 0.3f * u)
            drawCircle(FOSFORO, 0.6f * u, Offset(c.x + r * 0.3f, c.y - r * 0.35f))
            drawCircle(FOSFORO, 0.5f * u, Offset(c.x - r * 0.4f, c.y + r * 0.2f))
            for (k in 0 until 4) remache(Offset(c.x + r * 0.89f * cos(k * Math.PI / 2 + 0.78).toFloat(), c.y + r * 0.89f * sin(k * Math.PI / 2 + 0.78).toFloat()), 0.4f * u, oscuro, LATON_OSC)
            // El sello: el lacre con el engrane estampado y, al lado, el sello de latón tumbado.
            val s = Offset(der.center.x - n * 0.12f, c.y + r + n * 0.62f)
            val rs = n * 0.26f
            if (s.y + rs < der.bottom + 2f * u) {
                val lacre = Path().apply {
                    for (k in 0 until 14) {
                        val ang = k / 14.0 * 2 * Math.PI
                        val rr = rs * (0.92f + 0.14f * azar(e.semilla + 11, k))
                        val p = Offset(s.x + rr * cos(ang).toFloat(), s.y + rr * sin(ang).toFloat())
                        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                    close()
                }
                drawPath(Path().apply { addPath(lacre, Offset(0.6f * u, 0.8f * u)) }, Color.Black.copy(alpha = 0.35f))
                drawPath(lacre, carmesi(e.acento, oscuro))
                drawPath(engrane(s, rs * 0.58f, 8), mezcla(carmesi(e.acento, oscuro), Color.Black, 0.35f))
                drawCircle(Color.White.copy(alpha = 0.18f), rs * 0.22f, Offset(s.x - rs * 0.4f, s.y - rs * 0.4f))
                val m0 = Offset(s.x + rs * 1.1f, s.y + rs * 0.7f)
                rotate(-30f, m0) {
                    drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(m0.x + 0.5f * u, m0.y - rs * 0.25f + 0.7f * u), Size(rs * 1.6f, rs * 0.5f), CornerRadius(rs * 0.2f))
                    drawRoundRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), m0.y - rs * 0.25f, m0.y + rs * 0.25f), Offset(m0.x, m0.y - rs * 0.25f), Size(rs * 1.6f, rs * 0.5f), CornerRadius(rs * 0.2f))
                    drawCircle(t(ACERO_OSC, oscuro), rs * 0.36f, Offset(m0.x + rs * 1.7f, m0.y))
                }
                // El rollo de órdenes atado con su cinta carmesí.
                val y0 = s.y + rs + n * 0.42f
                val rollo = Rect(der.center.x - n * 0.17f, y0, der.center.x + n * 0.17f, der.bottom + 1.2f * u)
                if (rollo.height > n * 0.5f) {
                    val perg = t(Color(0xFFE6DCC2), oscuro)
                    drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(rollo.left + 0.8f * u, rollo.top + 1f * u), rollo.size, CornerRadius(rollo.width / 2))
                    drawRoundRect(Brush.horizontalGradient(listOf(mezcla(perg, Color.Black, 0.18f), perg, mezcla(perg, Color.Black, 0.3f)), rollo.left, rollo.right), rollo.topLeft, rollo.size, CornerRadius(rollo.width / 2))
                    drawOval(mezcla(perg, Color.Black, 0.25f), Offset(rollo.left, rollo.top), Size(rollo.width, rollo.width * 0.5f))
                    drawOval(mezcla(perg, Color.Black, 0.45f), Offset(rollo.center.x - rollo.width * 0.18f, rollo.top + rollo.width * 0.16f), Size(rollo.width * 0.36f, rollo.width * 0.18f))
                    val cy = rollo.top + rollo.height * 0.45f
                    drawRect(carmesi(e.acento, oscuro), Offset(rollo.left - 0.2f * u, cy - 0.9f * u), Size(rollo.width + 0.4f * u, 1.8f * u))
                    drawLine(carmesi(e.acento, oscuro), Offset(rollo.right, cy), Offset(rollo.right + n * 0.16f, cy + n * 0.26f), 0.9f * u, StrokeCap.Round)
                    drawLine(carmesi(e.acento, oscuro), Offset(rollo.right, cy), Offset(rollo.right + n * 0.24f, cy + n * 0.12f), 0.9f * u, StrokeCap.Round)
                }
            }
        }
        // La regla de latón, abajo del plano.
        val regla = Rect(a.left + a.width * 0.04f, a.bottom + 1.8f * u, a.left + a.width * 0.6f, minOf(a.bottom + 5.2f * u, tablero.bottom - 1.2f * u))
        if (regla.height > 1f * u) {
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(regla.left + 0.6f * u, regla.top + 0.8f * u), regla.size)
            drawRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro)), regla.top, regla.bottom), regla.topLeft, regla.size)
            var x = regla.left + 1f * u; var k = 0
            while (x < regla.right - 0.5f * u) {
                drawLine(tl(LATON_OSC, oscuro), Offset(x, regla.top), Offset(x, regla.top + regla.height * (if (k % 5 == 0) 0.6f else 0.32f)), 0.3f * u)
                x += 1.6f * u; k++
            }
        }
        // El plano (el nicho), sujeto por cuatro esquineras de latón.
        val hoja = Rect(a.left - 1.4f * u, a.top - 1.4f * u, a.right + 1.4f * u, a.bottom + 1.4f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(hoja.left + 0.8f * u, hoja.top + 1.1f * u), hoja.size)
        val papel = if (oscuro) mezcla(e.fondo, PLANO, 0.42f) else mezcla(e.fondo, Color(0xFF8FA9C4), 0.17f)
        drawRect(mezcla(papel, if (oscuro) Color.Black else Color(0xFF8FA9C4), 0.2f), hoja.topLeft, hoja.size)
        drawRect(papel, a.topLeft, a.size)
        val reticula = if (oscuro) HOLO.copy(alpha = 0.07f) else PLANO.copy(alpha = 0.07f)
        val paso = a.height / 8f
        var gx = a.left + paso; while (gx < a.right - 0.5f * u) { drawLine(reticula, Offset(gx, a.top), Offset(gx, a.bottom), 0.3f * u); gx += paso }
        var gy = a.top + paso; while (gy < a.bottom - 0.5f * u) { drawLine(reticula, Offset(a.left, gy), Offset(a.right, gy), 0.3f * u); gy += paso }
        val esq = 3f * u
        listOf(hoja.topLeft to Offset(1f, 1f), Offset(hoja.right, hoja.top) to Offset(-1f, 1f), Offset(hoja.left, hoja.bottom) to Offset(1f, -1f), Offset(hoja.right, hoja.bottom) to Offset(-1f, -1f))
            .forEach { (o, s) ->
                val q = Offset(o.x - s.x * 0.6f * u, o.y - s.y * 0.6f * u)
                drawPath(Path().apply { moveTo(q.x, q.y); lineTo(q.x + s.x * (esq + 0.6f * u), q.y); lineTo(q.x, q.y + s.y * (esq + 0.6f * u)); close() }, tl(LATON, oscuro))
                drawCircle(tl(LATON_OSC, oscuro), 0.45f * u, Offset(q.x + s.x * 1.1f * u, q.y + s.y * 1.1f * u))
            }
    }

    // ---------------------------------------------------------------------
    //  Otras vidas: el relicario de cinco hornacinas
    // ---------------------------------------------------------------------

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.OTRAS_VIDAS)
        val forro = aceroNicho(e, oscuro)
        // El cuerpo de planchas.
        drawRect(t(ACERO, oscuro))
        val col = a.width / 5f
        // Las hornacinas: el arco apuntado de cada una sobre el nicho, forrado como él.
        val cornisa = h * 0.13f
        for (k in 0 until 5) {
            val x0 = a.left + col * k; val x1 = x0 + col
            val arco = Rect(x0 + 0.9f * u, cornisa + 1.4f * u, x1 - 0.9f * u, a.top + 1f)
            val flecha = (arco.height * 0.95f).coerceAtMost(arco.width * 0.6f)
            // Las enjutas: el acero con su engrane de latón.
            if (k > 0) {
                val g = Offset(x0, cornisa + (a.top - cornisa) * 0.36f)
                val rg = minOf(col * 0.14f, (a.top - cornisa) * 0.3f)
                drawPath(engrane(g, rg, 8), tl(LATON_OSC, oscuro))
                drawPath(engrane(g, rg * 0.86f, 8), tl(LATON, oscuro))
                drawCircle(t(ACERO_OSC, oscuro), rg * 0.32f, g)
            }
            val p = arcoApuntado(Rect(arco.left - 0.9f * u, arco.top - 0.9f * u, arco.right + 0.9f * u, arco.bottom), flecha + 0.9f * u)
            drawPath(p, t(ACERO_LUZ, oscuro))
            drawPath(arcoApuntado(arco, flecha), tl(LATON_OSC, oscuro))
            val dentro = Rect(arco.left + 0.6f * u, arco.top + 0.6f * u, arco.right - 0.6f * u, arco.bottom)
            drawPath(arcoApuntado(dentro, flecha - 0.6f * u), forro)
            clipPath(arcoApuntado(dentro, flecha - 0.6f * u)) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Color.Transparent), dentro.top, dentro.bottom), dentro.topLeft, dentro.size)
            }
        }
        // El nicho: el forro de acero claro, partido en cinco por los parteluces.
        drawRect(forro, a.topLeft, a.size)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.16f else 0.06f)), a.top, a.bottom), a.topLeft, a.size)
        for (k in 1 until 5) {
            val x = a.left + col * k
            val pt = Rect(x - 0.9f * u, a.top, x + 0.9f * u, a.bottom)
            drawRect(Brush.horizontalGradient(listOf(t(ACERO_LUZ, oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro)), pt.left, pt.right), pt.topLeft, pt.size)
            drawRect(tl(LATON, oscuro), Offset(pt.left - 0.3f * u, a.top), Size(pt.width + 0.6f * u, 1.2f * u))
            drawRect(tl(LATON_OSC, oscuro), Offset(pt.left - 0.3f * u, a.bottom - 1.2f * u), Size(pt.width + 0.6f * u, 1.2f * u))
        }
        sombraInterior(a, 2.2f * u, if (oscuro) 0.3f else 0.1f)
        // Las columnas de los lados.
        columna(Rect(0.4f * u, cornisa - 1f * u, a.left - 0.4f * u, a.bottom + 1.5f * u), oscuro, u)
        columna(Rect(a.right + 0.4f * u, cornisa - 1f * u, w - 0.4f * u, a.bottom + 1.5f * u), oscuro, u)
        // La cornisa: el remate, los dentículos y el friso remachado.
        val remate = Rect(0f, 0f, w, cornisa * 0.32f)
        drawRect(Brush.verticalGradient(listOf(t(ACERO_BRILLO, oscuro), t(ACERO_LUZ, oscuro)), remate.top, remate.bottom), remate.topLeft, remate.size)
        drawRect(tl(LATON, oscuro), Offset(0f, remate.bottom), Size(w, 0.8f * u))
        val dent = Rect(0f, remate.bottom + 0.8f * u, w, cornisa * 0.62f)
        drawRect(t(ACERO_OSC, oscuro), dent.topLeft, dent.size)
        var dx = 0.6f * u
        while (dx < w) { drawRect(t(ACERO, oscuro), Offset(dx, dent.top), Size(1.8f * u, dent.height * 0.75f)); dx += 3f * u }
        val friso = Rect(0f, dent.bottom, w, cornisa)
        drawRect(t(ACERO_LUZ, oscuro), friso.topLeft, friso.size)
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, friso.bottom), Offset(w, friso.bottom), 0.6f * u)
        remaches(Offset(2f * u, friso.center.y), Offset(w - 2f * u, friso.center.y), 6f * u, 0.45f * u, oscuro)
        // El escudo de la Orden en el centro de la cornisa, con dos colas de estandarte.
        val ea = minOf(cornisa * 1.05f, col * 0.42f)
        listOf(-1f, 1f).forEach { s ->
            val cola = Path().apply {
                moveTo(w / 2 + s * ea * 0.3f, friso.top); lineTo(w / 2 + s * ea * 1.35f, friso.top + 0.4f * u); lineTo(w / 2 + s * ea * 1.1f, friso.bottom - 0.4f * u)
                lineTo(w / 2 + s * ea * 1.3f, friso.bottom + 1.6f * u); lineTo(w / 2 + s * ea * 0.3f, friso.bottom + 0.6f * u); close()
            }
            drawPath(cola, carmesi(e.acento, oscuro))
            drawPath(cola, mezcla(carmesi(e.acento, oscuro), Color.Black, 0.35f), style = Stroke(0.3f * u))
        }
        emblema(Offset(w / 2, 0.5f * u), ea, minOf(a.top - 1.5f * u, ea * 1.25f), e.acento, oscuro)
        // El zócalo: moldura de latón, franja de peligro, placa y el pie.
        val zocalo = Rect(0f, a.bottom + 1.5f * u, w, h)
        drawRect(t(ACERO_OSC, oscuro), zocalo.topLeft, zocalo.size)
        drawRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), zocalo.top, zocalo.top + 1.4f * u), zocalo.topLeft, Size(w, 1.4f * u))
        val fr = Rect(0f, zocalo.top + zocalo.height * 0.36f, w, zocalo.top + zocalo.height * 0.36f + 3.2f * u)
        franjas(fr, oscuro)
        val pl = Rect(w / 2 - col * 0.7f, fr.top - 1.4f * u, w / 2 + col * 0.7f, fr.bottom + 1.4f * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(pl.left + 0.5f * u, pl.top + 0.7f * u), pl.size)
        drawRect(tl(LATON, oscuro), pl.topLeft, pl.size)
        drawRect(tl(LATON_OSC, oscuro), Offset(pl.left + 0.8f * u, pl.top + 0.8f * u), Size(pl.width - 1.6f * u, pl.height - 1.6f * u), style = Stroke(0.35f * u))
        remache(Offset(pl.left + 1.8f * u, pl.center.y), 0.45f * u, oscuro, LATON_OSC)
        remache(Offset(pl.right - 1.8f * u, pl.center.y), 0.45f * u, oscuro, LATON_OSC)
        remaches(Offset(2f * u, h - 2.2f * u), Offset(w - 2f * u, h - 2.2f * u), 8f * u, 0.45f * u, oscuro)
    }

    // ---------------------------------------------------------------------
    //  Íconos de los mapas
    // ---------------------------------------------------------------------

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 1f || h < 1f) return
        val k = h / 100f
        val c = Offset(w / 2, h / 2)
        val sombra = Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f)
        when (id) {
            "unesco" -> {
                // La ciudadela de la Orden: dos torres, la torre del homenaje con su aguja y el rosetón de engrane.
                drawOval(sombra, Offset(c.x - 50f * k, c.y + 37f * k), Size(100f * k, 8f * k))
                val zocalo = Rect(c.x - 50f * k, c.y + 32f * k, c.x + 50f * k, c.y + 40f * k)
                drawRect(t(ACERO_OSC, oscuro), zocalo.topLeft, zocalo.size)
                drawLine(e.c2, Offset(zocalo.left, zocalo.top), Offset(zocalo.right, zocalo.top), 1.4f * k)
                // La muralla con sus almenas.
                val muro = Rect(c.x - 40f * k, c.y + 6f * k, c.x + 40f * k, c.y + 32f * k)
                drawRect(t(ACERO, oscuro), muro.topLeft, muro.size)
                var mx = muro.left
                while (mx < muro.right - 1f) { drawRect(t(ACERO, oscuro), Offset(mx, muro.top - 4f * k), Size(4f * k, 4f * k)); mx += 7f * k }
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(muro.left, muro.top + 6f * k), Offset(muro.right, muro.top + 6f * k), 0.8f * k)
                // Las torres de los lados, con su tejado en punta y la saetera encendida.
                listOf(-1f, 1f).forEach { s ->
                    val tx = c.x + s * 36f * k
                    val torre = Rect(tx - 8f * k, c.y - 12f * k, tx + 8f * k, c.y + 32f * k)
                    drawRect(t(ACERO_LUZ, oscuro), torre.topLeft, Size(torre.width / 2, torre.height))
                    drawRect(t(ACERO, oscuro), Offset(torre.center.x, torre.top), Size(torre.width / 2, torre.height))
                    drawPath(Path().apply { moveTo(torre.left - 2f * k, torre.top); lineTo(tx, torre.top - 18f * k); lineTo(torre.right + 2f * k, torre.top); close() }, t(ACERO_OSC, oscuro))
                    drawPath(Path().apply { moveTo(tx, torre.top - 18f * k); lineTo(torre.right + 2f * k, torre.top); lineTo(tx, torre.top); close() }, Color.Black.copy(alpha = 0.25f))
                    drawPath(arcoApuntado(Rect(tx - 2f * k, torre.top + 8f * k, tx + 2f * k, torre.top + 18f * k), 3f * k), if (oscuro) LLAMA else t(NEGRO, oscuro))
                    drawLine(e.c2, Offset(torre.left, torre.top + 1f * k), Offset(torre.right, torre.top + 1f * k), 1.2f * k)
                }
                // El estandarte en la torre de la izquierda.
                val asta = Offset(c.x - 36f * k, c.y - 30f * k)
                drawLine(t(ACERO_OSC, oscuro), asta, Offset(asta.x, asta.y - 14f * k), 1f * k)
                drawPath(Path().apply { moveTo(asta.x, asta.y - 14f * k); lineTo(asta.x + 13f * k, asta.y - 12f * k); lineTo(asta.x + 9f * k, asta.y - 9f * k); lineTo(asta.x + 13f * k, asta.y - 6f * k); lineTo(asta.x, asta.y - 6f * k); close() }, e.c1)
                // La torre del homenaje.
                val keep = Rect(c.x - 16f * k, c.y - 16f * k, c.x + 16f * k, c.y + 32f * k)
                drawRect(t(ACERO_LUZ, oscuro), keep.topLeft, Size(keep.width / 2, keep.height))
                drawRect(t(ACERO, oscuro), Offset(c.x, keep.top), Size(keep.width / 2, keep.height))
                drawPath(Path().apply { moveTo(keep.left - 2f * k, keep.top); lineTo(c.x, c.y - 46f * k); lineTo(keep.right + 2f * k, keep.top); close() }, t(ACERO_OSC, oscuro))
                drawPath(Path().apply { moveTo(c.x, c.y - 46f * k); lineTo(keep.right + 2f * k, keep.top); lineTo(c.x, keep.top); close() }, Color.Black.copy(alpha = 0.25f))
                drawLine(e.c2, Offset(c.x, c.y - 46f * k), Offset(c.x, keep.top), 1f * k)
                drawLine(e.c2, Offset(keep.left - 2f * k, keep.top), Offset(keep.right + 2f * k, keep.top), 1.6f * k)
                val ro = Offset(c.x, c.y - 3f * k)
                drawPath(engrane(ro, 9f * k, 10), e.c2)
                drawCircle(if (oscuro) mezcla(e.c1, LLAMA, 0.3f) else e.c1, 5f * k, ro)
                drawCircle(t(ACERO_OSC, oscuro), 1.6f * k, ro)
                // La puerta de arco apuntado con su rastrillo.
                val puerta = Rect(c.x - 7f * k, c.y + 13f * k, c.x + 7f * k, c.y + 32f * k)
                drawPath(arcoApuntado(puerta, 7f * k), t(NEGRO, oscuro))
                clipPath(arcoApuntado(puerta, 7f * k)) {
                    for (j in 0 until 4) drawLine(e.c2.copy(alpha = 0.8f), Offset(puerta.left + puerta.width * (j + 0.5f) / 4f, puerta.top), Offset(puerta.left + puerta.width * (j + 0.5f) / 4f, puerta.bottom), 0.8f * k)
                    drawLine(e.c2.copy(alpha = 0.8f), Offset(puerta.left, puerta.top + 10f * k), Offset(puerta.right, puerta.top + 10f * k), 0.8f * k)
                }
            }
            "rascar" -> {
                // La placa de acero remachada: la capa gris raspada deja ver el latón grabado con el mapa.
                val placa = Rect(c.x - 46f * k, c.y - 32f * k, c.x + 46f * k, c.y + 32f * k)
                drawRoundRect(sombra, Offset(placa.left + 2f * k, placa.top + 3f * k), placa.size, CornerRadius(4f * k))
                drawPath(ochavado(placa, 6f * k), t(ACERO_OSC, oscuro))
                val campo = Rect(placa.left + 5f * k, placa.top + 5f * k, placa.right - 5f * k, placa.bottom - 5f * k)
                drawRect(mezcla(LATON, e.c2, 0.3f).let { tl(it, oscuro) }, campo.topLeft, campo.size)
                val grabado = tl(LATON_OSC, oscuro)
                drawPath(Path().apply {
                    moveTo(campo.left + 8f * k, campo.top + 14f * k); cubicTo(campo.left + 18f * k, campo.top + 6f * k, campo.left + 30f * k, campo.top + 12f * k, campo.left + 28f * k, campo.top + 24f * k)
                    cubicTo(campo.left + 26f * k, campo.top + 36f * k, campo.left + 14f * k, campo.top + 46f * k, campo.left + 10f * k, campo.top + 36f * k); close()
                }, grabado, style = Stroke(1.2f * k))
                drawPath(Path().apply {
                    moveTo(campo.left + 36f * k, campo.top + 10f * k); cubicTo(campo.left + 48f * k, campo.top + 4f * k, campo.left + 58f * k, campo.top + 14f * k, campo.left + 54f * k, campo.top + 24f * k)
                    cubicTo(campo.left + 50f * k, campo.top + 30f * k, campo.left + 40f * k, campo.top + 26f * k, campo.left + 36f * k, campo.top + 10f * k)
                }, grabado, style = Stroke(1.2f * k))
                drawCircle(e.c1, 2.6f * k, Offset(campo.left + 20f * k, campo.top + 24f * k))
                // La capa por raspar, con borde irregular.
                val capa = Path().apply {
                    moveTo(campo.right, campo.top); lineTo(campo.left + 52f * k, campo.top)
                    var yy = campo.top; var j = 0
                    while (yy < campo.bottom) { val x = campo.left + 50f * k - (yy - campo.top) * 0.3f + (if (j % 2 == 0) 3f else -2f) * k; lineTo(x, yy); yy += 4f * k; j++ }
                    lineTo(campo.left + 34f * k, campo.bottom); lineTo(campo.right, campo.bottom); close()
                }
                drawPath(capa, t(ACERO_CLARO, oscuro))
                clipPath(capa) {
                    var xx = campo.left; while (xx < campo.right + campo.height) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(xx, campo.top), Offset(xx - campo.height, campo.bottom), 0.7f * k); xx += 3f * k }
                }
                listOf(placa.left + 2.6f * k to placa.top + 9f * k, placa.right - 2.6f * k to placa.top + 9f * k, placa.left + 2.6f * k to placa.bottom - 9f * k, placa.right - 2.6f * k to placa.bottom - 9f * k)
                    .forEach { (x, y) -> remache(Offset(x, y), 1.4f * k, oscuro) }
                // La moneda dentada que raspa, con las virutas.
                val mo = Offset(campo.left + 52f * k, campo.top + 38f * k)
                drawCircle(sombra, 13f * k, Offset(mo.x + 2f * k, mo.y + 3f * k))
                drawPath(engrane(mo, 13f * k, 14), tl(LATON_OSC, oscuro))
                drawCircle(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro)), Offset(mo.x - 10f * k, mo.y - 10f * k), Offset(mo.x + 10f * k, mo.y + 10f * k)), 10f * k, mo)
                drawPath(escudo(Offset(mo.x, mo.y - 5f * k), 8f * k, 10f * k), e.c1)
                for (j in 0 until 5) drawRect(t(ACERO_CLARO, oscuro), Offset(mo.x - 16f * k - j * 2.6f * k, mo.y - 2f * k + (j % 2) * 4f * k), Size(1.6f * k, 1.6f * k))
            }
            else -> {
                // Tus lugares: el plano de la Orden con tres chinchetas de escudo unidas por la ruta.
                val placa = Rect(c.x - 48f * k, c.y - 30f * k, c.x + 48f * k, c.y + 34f * k)
                drawRect(sombra, Offset(placa.left + 2f * k, placa.top + 3f * k), placa.size)
                drawPath(ochavado(placa, 5f * k), t(ACERO_OSC, oscuro))
                val campo = Rect(placa.left + 4f * k, placa.top + 4f * k, placa.right - 4f * k, placa.bottom - 4f * k)
                drawRect(if (oscuro) mezcla(PLANO, Color.Black, 0.2f) else mezcla(PLANO, Color.White, 0.78f), campo.topLeft, campo.size)
                val ret = (if (oscuro) HOLO else PLANO).copy(alpha = 0.18f)
                var gx = campo.left + 8f * k; while (gx < campo.right) { drawLine(ret, Offset(gx, campo.top), Offset(gx, campo.bottom), 0.6f * k); gx += 8f * k }
                var gy = campo.top + 8f * k; while (gy < campo.bottom) { drawLine(ret, Offset(campo.left, gy), Offset(campo.right, gy), 0.6f * k); gy += 8f * k }
                val pts = listOf(Offset(c.x - 32f * k, c.y + 14f * k), Offset(c.x - 2f * k, c.y - 10f * k), Offset(c.x + 30f * k, c.y + 10f * k))
                val ruta = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - 22f * k, c.y - 14f * k, pts[1].x, pts[1].y); quadraticTo(c.x + 20f * k, c.y - 8f * k, pts[2].x, pts[2].y) }
                drawPath(ruta, e.c2, style = Stroke(1.4f * k, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3f * k, 3f * k))))
                pts.forEachIndexed { j, p ->
                    val cab = Offset(p.x, p.y - 18f * k)
                    drawLine(sombra, Offset(p.x + 1f * k, p.y), Offset(p.x + 5f * k, p.y - 2f * k), 2f * k, StrokeCap.Round)
                    drawLine(t(ACERO_BRILLO, oscuro), p, Offset(p.x, cab.y + 8f * k), 1.2f * k)
                    drawPath(escudo(Offset(cab.x + 1.2f * k, cab.y + 1.2f * k), 14f * k, 16f * k), sombra)
                    drawPath(escudo(cab, 14f * k, 16f * k), e.c1)
                    clipPath(escudo(cab, 14f * k, 16f * k)) { drawRect(Color.Black.copy(alpha = 0.2f), Offset(cab.x, cab.y), Size(8f * k, 16f * k)) }
                    drawPath(escudo(cab, 14f * k, 16f * k), e.c2, style = Stroke(1.2f * k))
                    drawPath(engrane(Offset(cab.x, cab.y + 6.5f * k), 3.6f * k, 6), e.c2)
                    if (j == 1) drawCircle(e.c2, 1f * k, Offset(cab.x, cab.y + 6.5f * k))
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    //  Notas: el tablón de órdenes
    // ---------------------------------------------------------------------

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.NOTAS)
        // El marco: planchas de acero remachadas.
        drawRect(t(ACERO_OSC, oscuro))
        val marco = Rect(0f, h * 0.075f, w, h * 0.92f)
        listOf(Rect(0f, marco.top, a.left - 1.2f * u, marco.bottom), Rect(a.right + 1.2f * u, marco.top, w, marco.bottom)).forEach { r ->
            val mitad = r.top + r.height / 2
            plancha(Rect(r.left + 0.4f * u, r.top, r.right - 0.4f * u, mitad - 0.3f * u), ACERO, oscuro, u)
            plancha(Rect(r.left + 0.4f * u, mitad + 0.3f * u, r.right - 0.4f * u, r.bottom), ACERO, oscuro, u)
        }
        val listones = listOf(Rect(a.left - 1.2f * u, marco.top, a.right + 1.2f * u, a.top - 1.2f * u), Rect(a.left - 1.2f * u, a.bottom + 1.2f * u, a.right + 1.2f * u, marco.bottom))
        listones.forEach { r -> drawRect(t(ACERO, oscuro), r.topLeft, r.size); bisel(r, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.3f), 0.5f * u) }
        // El filete de latón alrededor del panel.
        val filete = Rect(a.left - 1.2f * u, a.top - 1.2f * u, a.right + 1.2f * u, a.bottom + 1.2f * u)
        drawRect(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), filete.topLeft, filete.bottomRight), filete.topLeft, filete.size)
        // El panel perforado (el nicho).
        val fondo = aceroNicho(e, oscuro)
        drawRect(fondo, a.topLeft, a.size)
        val paso = 4.2f * u
        val hueco = if (oscuro) Color.Black.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.07f)
        var py = a.top + paso / 2; var fila = 0
        while (py < a.bottom) {
            var px = a.left + paso / 2 + if (fila % 2 == 0) 0f else paso / 2
            while (px < a.right) { drawCircle(hueco, 0.42f * u, Offset(px, py)); px += paso }
            py += paso; fila++
        }
        sombraInterior(a, 2.6f * u, if (oscuro) 0.35f else 0.14f)
        // Los estandartes colgados a los lados.
        val ancho = (a.left - 1.2f * u) * 0.56f
        listOf(a.left * 0.5f - 0.6f * u, w - a.left * 0.5f + 0.6f * u).forEach { cx ->
            estandarte(Rect(cx - ancho / 2, marco.top + 3f * u, cx + ancho / 2, marco.top + 3f * u + h * 0.44f), e.acento, oscuro, u)
        }
        // La cornisa: el remate de acero, el friso remachado con su filete de latón y el escudo.
        val corn = Rect(0f, 0f, w, h * 0.085f)
        drawRect(Brush.verticalGradient(listOf(t(ACERO_BRILLO, oscuro), t(ACERO_LUZ, oscuro), t(ACERO, oscuro)), corn.top, corn.bottom), corn.topLeft, corn.size)
        drawRect(tl(LATON, oscuro), Offset(0f, corn.bottom - 1.2f * u), Size(w, 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(0f, corn.bottom), Size(w, 0.8f * u))
        remaches(Offset(2.4f * u, corn.center.y - 0.4f * u), Offset(w - 2.4f * u, corn.center.y - 0.4f * u), 6f * u, 0.5f * u, oscuro)
        val ea = minOf(h * 0.11f, w * 0.1f)
        drawRect(t(ACERO_OSC, oscuro), Offset(w / 2 - ea * 0.85f, 0f), Size(ea * 1.7f, corn.bottom))
        drawRect(tl(LATON_OSC, oscuro), Offset(w / 2 - ea * 0.85f, 0f), Size(ea * 1.7f, corn.bottom), style = Stroke(0.4f * u))
        emblema(Offset(w / 2, 0.8f * u), ea, minOf(a.top - 2f * u, ea * 1.3f), e.acento, oscuro)
        // El zócalo de peligro, con su placa.
        val zoc = Rect(0f, marco.bottom, w, h)
        drawRect(t(NEGRO, oscuro), zoc.topLeft, zoc.size)
        franjas(Rect(0f, zoc.top + 0.6f * u, w, zoc.top + zoc.height * 0.62f), oscuro)
        val pl = Rect(w / 2 - w * 0.13f, zoc.top + 0.8f * u, w / 2 + w * 0.13f, zoc.bottom - 1.4f * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(pl.left + 0.5f * u, pl.top + 0.7f * u), pl.size)
        drawRect(tl(LATON, oscuro), pl.topLeft, pl.size)
        drawRect(tl(LATON_OSC, oscuro), Offset(pl.left + 0.8f * u, pl.top + 0.8f * u), Size(pl.width - 1.6f * u, pl.height - 1.6f * u), style = Stroke(0.35f * u))
        remache(Offset(pl.left + 2f * u, pl.center.y), 0.5f * u, oscuro, LATON_OSC)
        remache(Offset(pl.right - 2f * u, pl.center.y), 0.5f * u, oscuro, LATON_OSC)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        if (r0.width < 1f || r0.height < 1f) return
        val k = minOf(r0.width, r0.height) / 100f
        val r = Rect(r0.left + 2f * k, r0.top + 2f * k, r0.right - 2f * k, r0.bottom - 2f * k)
        val ch = 7f * k
        // La sombra y la placa esmaltada de esquinas cortadas, con su canto de acero.
        drawPath(Path().apply { addPath(ochavado(r, ch), Offset(1.6f * k, 2.4f * k)) }, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f))
        drawPath(ochavado(r, ch), t(ACERO_CLARO, oscuro))
        val esmalte = Rect(r.left + 1.4f * k, r.top + 1.4f * k, r.right - 1.4f * k, r.bottom - 1.4f * k)
        val tono = if (oscuro) ESMALTE_NOCHE else ESMALTE
        drawPath(ochavado(esmalte, ch * 0.8f), tono)
        drawPath(ochavado(esmalte, ch * 0.8f), Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.5f), Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.12f else 0.05f)), esmalte.top, esmalte.bottom))
        // La banda del tipo, arriba, entre los dos remaches.
        val banda = Rect(esmalte.left + 9f * k, esmalte.top + 2.4f * k, esmalte.right - 9f * k, esmalte.top + 8f * k)
        when (e.tipo) {
            "idea" -> drawRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), banda.top, banda.bottom), banda.topLeft, banda.size)
            "pensamiento" -> drawRect(if (oscuro) mezcla(PLANO, HOLO, 0.35f) else PLANO, banda.topLeft, banda.size)
            "proposito" -> drawRect(mezcla(CARMESI, e.acento, 0.5f), banda.topLeft, banda.size)
            "recordatorio" -> franjas(banda, oscuro)
            else -> drawRect(t(ACERO_LUZ, oscuro), banda.topLeft, banda.size)
        }
        listOf(Offset(esmalte.left + 4.5f * k, esmalte.top + 5.2f * k), Offset(esmalte.right - 4.5f * k, esmalte.top + 5.2f * k),
            Offset(esmalte.left + 4.5f * k, esmalte.bottom - 4.5f * k), Offset(esmalte.right - 4.5f * k, esmalte.bottom - 4.5f * k)).forEach { remache(it, 1.5f * k, oscuro) }
        // El sello del tipo, abajo a la derecha.
        val s = Offset(esmalte.right - 12f * k, esmalte.bottom - 6f * k)
        val marca = if (oscuro) ESMALTE.copy(alpha = 0.32f) else ACERO.copy(alpha = 0.4f)
        when (e.tipo) {
            "idea" -> { drawPath(engrane(s, 3.4f * k, 8), marca); drawCircle(tono, 1.2f * k, s) }
            "pensamiento" -> { drawCircle(marca, 3.2f * k, s, style = Stroke(0.8f * k)); drawCircle(marca, 1f * k, s) }
            "proposito" -> {
                drawLine(marca, Offset(s.x, s.y - 3.6f * k), Offset(s.x, s.y + 3.6f * k), 0.9f * k)
                drawLine(marca, Offset(s.x - 2.2f * k, s.y - 1.4f * k), Offset(s.x + 2.2f * k, s.y - 1.4f * k), 0.9f * k)
            }
            "recordatorio" -> drawPath(Path().apply { moveTo(s.x, s.y - 3.4f * k); lineTo(s.x + 3.4f * k, s.y + 2.6f * k); lineTo(s.x - 3.4f * k, s.y + 2.6f * k); close() }, marca, style = Stroke(0.8f * k))
            else -> {}
        }
        if (e.fijada) {
            // Fijada: la cinta carmesí en la esquina y la pinza de latón arriba.
            val cinta = Path().apply {
                moveTo(esmalte.right - 17f * k, esmalte.top); lineTo(esmalte.right - ch * 0.8f, esmalte.top); lineTo(esmalte.right, esmalte.top + ch * 0.8f); lineTo(esmalte.right, esmalte.top + 17f * k); close()
            }
            clipPath(ochavado(esmalte, ch * 0.8f)) {
                drawPath(cinta, mezcla(CARMESI, e.acento, 0.5f))
                drawPath(cinta, Color.Black.copy(alpha = 0.25f), style = Stroke(0.5f * k))
            }
            val pz = Rect(r.center.x - 9f * k, r0.top, r.center.x + 9f * k, r.top + 7f * k)
            drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(pz.left + 0.8f * k, pz.top + 1.2f * k), pz.size, CornerRadius(1.4f * k))
            drawRoundRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), pz.top, pz.bottom), pz.topLeft, pz.size, CornerRadius(1.4f * k))
            drawLine(tl(LATON_OSC, oscuro), Offset(pz.left + 2f * k, pz.center.y), Offset(pz.right - 2f * k, pz.center.y), 0.6f * k)
            remache(Offset(pz.left + 2.6f * k, pz.top + 2.2f * k), 0.9f * k, oscuro, LATON_OSC)
            remache(Offset(pz.right - 2.6f * k, pz.top + 2.2f * k), 0.9f * k, oscuro, LATON_OSC)
        }
    }

    /** La tinta de las notas: negro de estarcido sobre el esmalte blanco; de noche, el acero claro sobre el pavonado. */
    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFDDE2E7) else Color(0xFF15181B)
}
