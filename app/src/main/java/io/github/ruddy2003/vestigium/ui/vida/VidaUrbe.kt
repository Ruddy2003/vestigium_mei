package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Urbe (la Roma de hoy): travertino y revoque ocre, el verde de las persianas y de las edículas, el latón, el cotto y los
 * sampietrini del suelo (la misma ciudad que su edícola del Ideario, su estantería bajo el arco y su sala de medallas).
 * - Social: la mesa larga de una trattoria en la piazza, vista desde arriba: los sampietrini, las sillas de bistró, los
 *   geranios en sus macetas de barro, el mantel a cuadros con el papel encima (donde va el grafo), los platos de pasta, el
 *   pan, el vino, las tazzine y la vela en su fiasco; dos guirnaldas de bombillas encendidas cruzan arriba y abajo.
 * - Mapas: el escritorio de nogal de un estudio romano, visto desde arriba: el tablero con el cuero verde y su filete de oro,
 *   el mapa de papel con sus dobleces; alrededor, la moka, la tazzina, la lupa, las llaves de la Vespa, el boleto del bus,
 *   el lápiz y las gafas de sol; abajo, el faldón con sus cajones y tiradores de latón y las patas en punta con regatones.
 * - Otras vidas: el escaparate de una tienda de monedas antiguas: la cornisa de travertino, la insegna verde con sus
 *   filetes y su medallón de oro, dos faroles encendidos, cinco vanos de vidrio con su reflejo entre montantes verdes, el
 *   alféizar de travertino, el zócalo con tableros y la acera.
 * - Íconos: el Coliseo con su anillo roto junto a un pino piñonero, el rasca y gana con su moneda de un euro, y el plano
 *   plegado con tres chinchetas y la ruta.
 * - Notas: la bacheca del barrio en un muro ocre: el tejadillo de tejas, el marco verde con escuadras, bisagras y cerradura
 *   de latón y el corcho; cada nota es un papel distinto según el tipo (la hoja amarilla con su esquina doblada, la hoja de
 *   cuaderno arrancada, la ficha con cinta de papel, el boleto rosa con su pinza de metal); la fijada lleva el timbro.
 */
internal object VidaUrbe : DisenoVida {
    private val TRAVERTINO = Color(0xFFE2D5BC)
    private val TRAVERTINO_LUZ = Color(0xFFF0E7D4)
    private val TRAVERTINO_SOMBRA = Color(0xFFC2B190)
    private val TRAVERTINO_PORO = Color(0xFFA8956F)
    private val OCRA = Color(0xFFD9A867)
    private val OCRA_OSC = Color(0xFFB4834A)
    private val COTTO = Color(0xFFC46A42)
    private val COTTO_LUZ = Color(0xFFDB8A5E)
    private val COTTO_OSC = Color(0xFF8E4528)
    private val SAMPIETRINO = Color(0xFF6E6A64)
    private val SAMPIETRINO_LUZ = Color(0xFF8E8A82)
    private val SAMPIETRINO_OSC = Color(0xFF3E3B37)
    private val VERDE = Color(0xFF2F4A36)
    private val VERDE_LUZ = Color(0xFF4E6E52)
    private val VERDE_OSC = Color(0xFF1C2E22)
    private val PELLE = Color(0xFF2E4B3B)
    private val NOGAL = Color(0xFF7A5232)
    private val NOGAL_LUZ = Color(0xFF9C6F45)
    private val NOGAL_OSC = Color(0xFF4A3020)
    private val OTTONE = Color(0xFFC9A24C)
    private val OTTONE_LUZ = Color(0xFFEBCB7A)
    private val OTTONE_OSC = Color(0xFF8A6A28)
    private val ACERO = Color(0xFFB8BCC0)
    private val ACERO_OSC = Color(0xFF6E7378)
    private val MANTEL = Color(0xFFF4EEE2)
    private val CUADRO = Color(0xFFB23A2E)
    private val GERANIO = Color(0xFFD2352B)
    private val HOJA = Color(0xFF4E7A3A)
    private val HOJA_OSC = Color(0xFF2F5224)
    private val PAN = Color(0xFFD9A15A)
    private val VINO = Color(0xFF6A1424)
    private val CAFE = Color(0xFF3A2216)
    private val PORCELANA = Color(0xFFFBF8F2)
    private val MIMBRE = Color(0xFFC09460)
    private val PASTA = Color(0xFFEBCB82)
    private val CORCHO_DIA = Color(0xFFEBDDC2)
    private val CORCHO_NOCHE = Color(0xFF2E2620)
    private val LUZ = Color(0xFFFFD98A)
    private val NOCHE = Color(0xFF0E0C0A)
    /** El papel donde va el contenido (el papel de la mesa, el mapa): crema de día, papel de estraza en penumbra de noche. */
    private val PAPEL_DIA = Color(0xFFF7F1E3)
    private val PAPEL_NOCHE = Color(0xFF2A231E)
    /** Los papeles de las notas, de día y de noche. */
    private val GIALLO = Color(0xFFF6E7A6)
    private val CUADERNO = Color(0xFFFBF8F0)
    private val FICHA = Color(0xFFEFF0E6)
    private val ROSA = Color(0xFFF4D8CC)
    private val TINTA = Color(0xFF2B2420)
    private val TINTA_NOCHE = Color(0xFFF1E6D2)

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un número estable de 0 a 1 (las irregularidades de cada cosa, siempre las mismas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Un material a la luz del modo: de noche, el mismo material bajo las farolas. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.45f) else c

    /** La unidad de dibujo: una centésima del lado corto del lienzo. */
    private fun DrawScope.un(): Float = size.minDimension / 100f

    /** El nicho de un mueble, en px. */
    private fun DrawScope.nichoPx(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.14f, 0.18f, 0.86f, 0.80f)
        MuebleVida.MAPAS -> Rect(0.11f, 0.16f, 0.89f, 0.78f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.05f, 0.25f, 0.95f, 0.80f)
        MuebleVida.NOTAS -> Rect(0.07f, 0.135f, 0.93f, 0.885f)
    }

    // ================================================================ Ayudas de dibujo

    /** Una mancha radial suave (luz, humedad). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) {
        if (r < 0.5f) return
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r), r, c)
    }

    /** Vetas de madera en un rectángulo: líneas onduladas a lo largo, con su grosor y su tono variables. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, paso: Float, vertical: Boolean = false, ondulacion: Float = 1f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / paso).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.2f + 0.6f * azar(semilla, k + 100)) * paso * ondulacion
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1f + 2f * azar(semilla, k + 300)
                val p = Path()
                for (s in 0..16) {
                    val a = largo * s / 16f
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16f)
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(paso * (0.12f + 0.18f * azar(semilla, k + 500))))
            }
        }
    }

    /** Una moldura horizontal: banda con su luz arriba y su sombra abajo. */
    private fun DrawScope.moldura(r: Rect, claro: Color, medio: Color, oscuro: Color) {
        drawRect(Brush.verticalGradient(listOf(claro, medio, oscuro), r.top, r.bottom), r.topLeft, r.size)
        drawLine(Color.White.copy(alpha = 0.25f), r.topLeft, Offset(r.right, r.top), maxOf(1f, r.height * 0.08f))
    }

    /** Travertino: la piedra clara con su luz, sus poros alargados y alguna veta horizontal. */
    private fun DrawScope.travertino(r: Rect, oscuro: Boolean, semilla: Int, vertical: Boolean = false) {
        val u = un()
        drawRect(Brush.verticalGradient(listOf(t(TRAVERTINO_LUZ, oscuro), t(TRAVERTINO, oscuro), t(TRAVERTINO_SOMBRA, oscuro)), r.top, r.bottom), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val n = (r.width * r.height / (14f * u * u)).toInt().coerceIn(3, 90)
            for (k in 0 until n) {
                val x = r.left + r.width * azar(semilla, k); val y = r.top + r.height * azar(semilla + 1, k)
                val l = (0.6f + 1.6f * azar(semilla + 2, k)) * u
                if (vertical) drawLine(t(TRAVERTINO_PORO, oscuro).copy(alpha = 0.6f), Offset(x, y), Offset(x, y + l), 0.3f * u, StrokeCap.Round)
                else drawLine(t(TRAVERTINO_PORO, oscuro).copy(alpha = 0.6f), Offset(x, y), Offset(x + l, y), 0.3f * u, StrokeCap.Round)
            }
        }
    }

    /** Sampietrini: los adoquines de basalto en hiladas alternadas, cada uno con su tono, su luz y su junta. */
    private fun DrawScope.sampietrini(r: Rect, lado: Float, oscuro: Boolean, semilla: Int) {
        drawRect(t(SAMPIETRINO_OSC, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top; var fila = 0
            while (y < r.bottom) {
                var x = r.left - (if (fila % 2 == 0) 0f else lado / 2); var k = 0
                while (x < r.right) {
                    val q = azar(semilla + fila, k)
                    val c = t(mez(SAMPIETRINO, SAMPIETRINO_LUZ, q), oscuro)
                    val g = lado * 0.1f
                    drawRoundRect(c, Offset(x + g, y + g), Size(lado - 2 * g, lado - 2 * g), CornerRadius(lado * 0.22f))
                    drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), Offset(x + g, y + g), Size(lado - 2 * g, (lado - 2 * g) * 0.4f), CornerRadius(lado * 0.22f))
                    x += lado; k++
                }
                y += lado; fila++
            }
        }
    }

    /** Una bombilla encendida con su halo, que no se mete en [fuera] (el nicho). */
    private fun DrawScope.bombilla(o: Offset, r: Float, oscuro: Boolean, fuera: Rect?) {
        val halo = { mancha(o, r * (if (oscuro) 6f else 3.5f), LUZ.copy(alpha = if (oscuro) 0.38f else 0.22f)) }
        if (fuera != null) clipRect(fuera.left, fuera.top, fuera.right, fuera.bottom, ClipOp.Difference) { halo() } else halo()
        drawCircle(Color(0xFF3A342E), r * 0.55f, Offset(o.x, o.y - r * 0.9f))
        drawCircle(Brush.radialGradient(listOf(Color.White, LUZ, Color(0xFFE8A84A)), Offset(o.x - r * 0.25f, o.y - r * 0.25f), r * 1.2f), r, o)
    }

    /** Una guirnalda de bombillas: el cable que cuelga entre dos puntos y las bombillas encendidas. */
    private fun DrawScope.guirnalda(a: Offset, b: Offset, caida: Float, n: Int, oscuro: Boolean, fuera: Rect) {
        val u = un()
        val m = Offset((a.x + b.x) / 2, (a.y + b.y) / 2 + caida * 2)
        val cable = Path().apply { moveTo(a.x, a.y); quadraticTo(m.x, m.y, b.x, b.y) }
        drawPath(cable, Color.Black.copy(alpha = 0.25f), style = Stroke(0.6f * u))
        drawPath(cable, Color(0xFF2A2622), style = Stroke(0.35f * u))
        for (k in 1 until n) {
            val f = k / n.toFloat()
            val g = 1 - f
            val p = Offset(g * g * a.x + 2 * g * f * m.x + f * f * b.x, g * g * a.y + 2 * g * f * m.y + f * f * b.y)
            bombilla(Offset(p.x, p.y + 1.4f * u), 1.1f * u, oscuro, fuera)
        }
    }

    // ================================================================ Social: la mesa de la trattoria

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = un()
        val n = nichoPx(MuebleVida.SOCIAL)
        // Los sampietrini de la piazza.
        sampietrini(Rect(0f, 0f, w, h), 4.4f * u, oscuro, e.semilla)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.05f), Offset.Zero, size)
        // Los geranios en sus macetas de barro, en las cuatro esquinas.
        listOf(Offset(w * 0.04f, h * 0.075f), Offset(w * 0.96f, h * 0.075f), Offset(w * 0.04f, h * 0.925f), Offset(w * 0.96f, h * 0.925f)).forEachIndexed { i, o -> geranio(o, 6.2f * u, oscuro, e.semilla + i) }
        // Las sillas de bistró alrededor, medio metidas bajo la mesa.
        val mantel = Rect(w * 0.085f, h * 0.09f, w * 0.915f, h * 0.91f)
        listOf(0.33f, 0.67f).forEach { f ->
            silla(Offset(mantel.left - 1.2f * u, h * f), 180f, oscuro)
            silla(Offset(mantel.right + 1.2f * u, h * f), 0f, oscuro)
        }
        listOf(0.28f, 0.5f, 0.72f).forEach { f ->
            silla(Offset(w * f, mantel.top - 1.4f * u), 270f, oscuro)
            silla(Offset(w * f, mantel.bottom + 1.4f * u), 90f, oscuro)
        }
        // El mantel a cuadros, con su sombra, sus pliegues en las esquinas y el dobladillo.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.28f), Offset(mantel.left + 1.2f * u, mantel.top + 2f * u), mantel.size, CornerRadius(1.2f * u))
        drawRoundRect(t(MANTEL, oscuro), mantel.topLeft, mantel.size, CornerRadius(1.2f * u))
        clipRect(mantel.left, mantel.top, mantel.right, mantel.bottom) {
            val paso = 3.2f * u
            val roja = t(CUADRO, oscuro).copy(alpha = 0.4f)
            var x = mantel.left; while (x < mantel.right) { drawRect(roja, Offset(x, mantel.top), Size(paso / 2, mantel.height)); x += paso }
            var y = mantel.top; while (y < mantel.bottom) { drawRect(roja, Offset(mantel.left, y), Size(mantel.width, paso / 2)); y += paso }
            // La caída del mantel en los bordes: un poco de sombra donde se dobla hacia abajo.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f)), mantel.bottom - 2.5f * u, mantel.bottom), Offset(mantel.left, mantel.bottom - 2.5f * u), Size(mantel.width, 2.5f * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.1f)), mantel.right - 2.5f * u, mantel.right), Offset(mantel.right - 2.5f * u, mantel.top), Size(2.5f * u, mantel.height))
        }
        drawRoundRect(t(mez(MANTEL, CUADRO, 0.35f), oscuro), Offset(mantel.left + 0.8f * u, mantel.top + 0.8f * u), Size(mantel.width - 1.6f * u, mantel.height - 1.6f * u), CornerRadius(0.8f * u), style = Stroke(0.3f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.9f * u, 0.7f * u))))
        // El papel de la mesa (el nicho), con su sombra y el borde un poco más grande.
        val m = 1.0f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(n.left - m + 0.6f * u, n.top - m + 1f * u), Size(n.width + 2 * m, n.height + 2 * m))
        drawRect(if (oscuro) mez(PAPEL_NOCHE, Color.White, 0.06f) else mez(PAPEL_DIA, Color(0xFFD9C79E), 0.35f), Offset(n.left - m, n.top - m), Size(n.width + 2 * m, n.height + 2 * m))
        drawRect(if (oscuro) PAPEL_NOCHE else PAPEL_DIA, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 16) mancha(Offset(n.left + n.width * azar(e.semilla + 40, k), n.top + n.height * azar(e.semilla + 41, k)), (5f + 9f * azar(e.semilla + 42, k)) * u, (if (oscuro) Color.White else Color(0xFFB59B62)).copy(alpha = 0.025f))
        }
        // Lo que hay sobre el mantel, alrededor del papel.
        val arriba = (mantel.top + n.top - m) / 2
        val abajo = (mantel.bottom + n.bottom + m) / 2
        val lado = minOf(3.6f * u, (n.left - m - mantel.left) * 0.42f)
        fiasco(Offset(w * 0.5f, arriba), 3.4f * u, oscuro, n)
        tazzina(Offset(w * 0.3f, arriba), 2.6f * u, oscuro)
        tazzina(Offset(w * 0.7f, arriba), 2.6f * u, oscuro)
        listOf(w * 0.4f, w * 0.43f).forEachIndexed { i, x ->
            drawCircle(Color.Black.copy(alpha = 0.2f), 1.2f * u, Offset(x + 0.3f * u, arriba + 0.5f * u))
            drawCircle(t(if (i == 0) PORCELANA else Color(0xFF6A5A4A), oscuro), 1.2f * u, Offset(x, arriba))
            drawCircle(t(ACERO, oscuro), 0.6f * u, Offset(x, arriba))
        }
        cestaPan(Offset(w * 0.5f, abajo), 6.5f * u, 3.4f * u, oscuro)
        copa(Offset(w * 0.39f, abajo), 2.2f * u, oscuro)
        copa(Offset(w * 0.61f, abajo), 2.2f * u, oscuro)
        plato(Offset(w * 0.25f, abajo), 4.0f * u, oscuro, e)
        plato(Offset(w * 0.75f, abajo), 4.0f * u, oscuro, e)
        plato(Offset((mantel.left + n.left - m) / 2, h * 0.5f), lado, oscuro, e)
        plato(Offset((mantel.right + n.right + m) / 2, h * 0.5f), lado, oscuro, e)
        // Las guirnaldas de bombillas que cruzan la piazza.
        guirnalda(Offset(-2 * u, h * 0.015f), Offset(w + 2 * u, h * 0.015f), 2.2f * u, 12, oscuro, n)
        guirnalda(Offset(-2 * u, h * 0.955f), Offset(w + 2 * u, h * 0.955f), 1.6f * u, 12, oscuro, n)
    }

    /** Un geranio en su maceta de barro, visto desde arriba: el borde de la maceta, las hojas y las flores rojas. */
    private fun DrawScope.geranio(c: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        val u = un()
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + 0.8f * u, c.y + 1.2f * u))
        drawCircle(Brush.radialGradient(listOf(t(COTTO_LUZ, oscuro), t(COTTO, oscuro), t(COTTO_OSC, oscuro)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.3f), r, c)
        drawCircle(t(COTTO_OSC, oscuro), r * 0.82f, c, style = Stroke(0.4f * u))
        for (k in 0 until 7) {
            val a = k * 0.9f + azar(semilla, k)
            val p = Offset(c.x + r * 0.45f * cos(a), c.y + r * 0.45f * sin(a))
            drawCircle(t(HOJA_OSC, oscuro), r * 0.42f, p)
            drawCircle(t(HOJA, oscuro), r * 0.34f, Offset(p.x - r * 0.05f, p.y - r * 0.05f))
        }
        for (k in 0 until 3) {
            val a = k * 2.1f + azar(semilla, 9)
            val p = Offset(c.x + r * 0.32f * cos(a), c.y + r * 0.32f * sin(a))
            for (j in 0 until 5) drawCircle(t(GERANIO, oscuro), r * 0.12f, Offset(p.x + r * 0.13f * cos(j * 1.26f), p.y + r * 0.13f * sin(j * 1.26f)))
            drawCircle(t(mez(GERANIO, Color.White, 0.3f), oscuro), r * 0.07f, p)
        }
    }

    /** Una silla de bistró vista desde arriba: el asiento de mimbre trenzado y el respaldo curvo de madera. */
    private fun DrawScope.silla(c: Offset, angulo: Float, oscuro: Boolean) {
        val u = un()
        val r = 4.4f * u
        rotate(angulo, c) {
            drawCircle(Color.Black.copy(alpha = 0.3f), r * 1.05f, Offset(c.x + 0.6f * u, c.y + 1f * u))
            drawCircle(t(MIMBRE, oscuro), r, c)
            for (k in -3..3) drawLine(t(mez(MIMBRE, NOGAL_OSC, 0.4f), oscuro).copy(alpha = 0.6f), Offset(c.x + k * r * 0.26f, c.y - r * 0.9f), Offset(c.x + k * r * 0.26f, c.y + r * 0.9f), 0.25f * u)
            for (k in -3..3) drawLine(t(mez(MIMBRE, Color.White, 0.3f), oscuro).copy(alpha = 0.4f), Offset(c.x - r * 0.9f, c.y + k * r * 0.26f), Offset(c.x + r * 0.9f, c.y + k * r * 0.26f), 0.25f * u)
            drawCircle(t(NOGAL_OSC, oscuro), r, c, style = Stroke(0.6f * u))
            drawArc(t(NOGAL, oscuro), -70f, 140f, false, Offset(c.x - r * 1.15f, c.y - r * 1.15f), Size(r * 2.3f, r * 2.3f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
            drawArc(t(NOGAL_LUZ, oscuro), -60f, 120f, false, Offset(c.x - r * 1.15f, c.y - r * 1.15f), Size(r * 2.3f, r * 2.3f), style = Stroke(0.4f * u, cap = StrokeCap.Round))
        }
    }

    /** El fiasco de Chianti con su vela: la panza forrada de paja, el cuello, la cera derramada y la llama con su halo. */
    private fun DrawScope.fiasco(c: Offset, r: Float, oscuro: Boolean, fuera: Rect) {
        val u = un()
        clipRect(fuera.left, fuera.top, fuera.right, fuera.bottom, ClipOp.Difference) { mancha(c, r * (if (oscuro) 6f else 3.6f), LUZ.copy(alpha = if (oscuro) 0.4f else 0.25f)) }
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + 0.6f * u, c.y + 1 * u))
        drawCircle(t(Color(0xFFD8B46A), oscuro), r, c)
        for (k in 0 until 12) {
            val a = k * Math.PI / 6
            drawLine(t(Color(0xFFA9823E), oscuro), Offset(c.x + r * 0.45f * cos(a).toFloat(), c.y + r * 0.45f * sin(a).toFloat()), Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), 0.3f * u)
        }
        drawCircle(t(Color(0xFF2E5A2E), oscuro), r * 0.48f, c)
        drawCircle(t(PORCELANA, oscuro), r * 0.34f, c)
        for (k in 0 until 4) drawCircle(t(PORCELANA, oscuro), r * 0.12f, Offset(c.x + r * 0.42f * cos(k * 1.7f), c.y + r * 0.42f * sin(k * 1.7f)))
        drawCircle(Color(0xFFFFE9A8), r * 0.2f, c)
        drawCircle(Color.White, r * 0.08f, c)
    }

    /** Una tazzina de espresso en su plato, vista desde arriba, con el café y la crema. */
    private fun DrawScope.tazzina(c: Offset, r: Float, oscuro: Boolean) {
        val u = un()
        drawCircle(Color.Black.copy(alpha = 0.25f), r * 1.5f, Offset(c.x + 0.5f * u, c.y + 0.8f * u))
        drawCircle(t(PORCELANA, oscuro), r * 1.5f, c)
        drawCircle(t(Color(0xFFD8D2C6), oscuro), r * 1.5f, c, style = Stroke(0.25f * u))
        drawRoundRect(t(PORCELANA, oscuro), Offset(c.x + r * 0.8f, c.y - r * 0.22f), Size(r * 0.75f, r * 0.44f), CornerRadius(r * 0.2f))
        drawCircle(t(PORCELANA, oscuro), r, c)
        drawCircle(t(Color(0xFFD8D2C6), oscuro), r, c, style = Stroke(0.3f * u))
        drawCircle(t(CAFE, oscuro), r * 0.76f, c)
        drawCircle(t(Color(0xFFB07A44), oscuro), r * 0.55f, c)
        drawCircle(t(Color(0xFFC99A60), oscuro), r * 0.3f, Offset(c.x - r * 0.1f, c.y - r * 0.1f))
        drawLine(t(ACERO, oscuro), Offset(c.x - r * 1.4f, c.y + r * 0.8f), Offset(c.x - r * 0.5f, c.y + r * 1.25f), 0.4f * u, StrokeCap.Round)
    }

    /** Un plato de pasta visto desde arriba: el ala con su filete, la pasta enroscada y el tenedor al lado. */
    private fun DrawScope.plato(c: Offset, r: Float, oscuro: Boolean, e: EstadoMueble) {
        val u = un()
        drawCircle(Color.Black.copy(alpha = 0.25f), r, Offset(c.x + 0.5f * u, c.y + 0.8f * u))
        drawCircle(Brush.radialGradient(listOf(t(PORCELANA, oscuro), t(Color(0xFFE6E0D4), oscuro)), c, r), r, c)
        drawCircle(e.acento.copy(alpha = if (oscuro) 0.6f else 0.7f), r * 0.88f, c, style = Stroke(0.25f * u))
        drawCircle(t(Color(0xFFE2DCCF), oscuro), r * 0.62f, c, style = Stroke(0.25f * u))
        for (k in 0 until 4) drawArc(t(mez(PASTA, OCRA_OSC, k * 0.15f), oscuro), k * 70f, 220f, false, Offset(c.x - r * (0.48f - k * 0.08f), c.y - r * (0.48f - k * 0.08f)), Size(r * (0.96f - k * 0.16f), r * (0.96f - k * 0.16f)), style = Stroke(r * 0.12f, cap = StrokeCap.Round))
        for (k in 0 until 6) drawCircle(Color(0xFF2A2420).copy(alpha = 0.6f), r * 0.03f, Offset(c.x + r * 0.35f * cos(k * 1.1f), c.y + r * 0.3f * sin(k * 1.7f)))
    }

    /** Una copa de vino tinto vista desde arriba: el pie que asoma, el cáliz y el vino con su brillo. */
    private fun DrawScope.copa(c: Offset, r: Float, oscuro: Boolean) {
        val u = un()
        drawCircle(Color.Black.copy(alpha = 0.18f), r * 1.2f, Offset(c.x + 0.8f * u, c.y + 1.2f * u))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.12f else 0.45f), r * 1.2f, c)
        drawCircle(t(ACERO_OSC, oscuro).copy(alpha = 0.5f), r * 1.2f, c, style = Stroke(0.25f * u))
        drawCircle(t(VINO, oscuro), r * 0.8f, c)
        drawCircle(Color.White.copy(alpha = 0.5f), r * 0.22f, Offset(c.x - r * 0.35f, c.y - r * 0.35f))
    }

    /** La cesta del pan: el mimbre trenzado y tres panecillos con su corte. */
    private fun DrawScope.cestaPan(c: Offset, rx: Float, ry: Float, oscuro: Boolean) {
        val u = un()
        drawOval(Color.Black.copy(alpha = 0.28f), Offset(c.x - rx + 0.6f * u, c.y - ry + 1 * u), Size(rx * 2, ry * 2))
        drawOval(t(MIMBRE, oscuro), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        for (k in 0 until 14) {
            val a = k * Math.PI / 7
            drawCircle(t(mez(MIMBRE, NOGAL_OSC, 0.4f), oscuro), 0.4f * u, Offset(c.x + rx * 0.88f * cos(a).toFloat(), c.y + ry * 0.82f * sin(a).toFloat()))
        }
        drawOval(t(mez(MIMBRE, NOGAL_OSC, 0.3f), oscuro), Offset(c.x - rx * 0.78f, c.y - ry * 0.7f), Size(rx * 1.56f, ry * 1.4f))
        listOf(-0.42f, 0f, 0.42f).forEachIndexed { i, f ->
            val p = Offset(c.x + rx * f, c.y + (if (i == 1) -ry * 0.12f else ry * 0.08f))
            drawOval(Brush.radialGradient(listOf(t(mez(PAN, Color.White, 0.3f), oscuro), t(PAN, oscuro), t(mez(PAN, NOGAL, 0.4f), oscuro)), Offset(p.x - rx * 0.1f, p.y - ry * 0.15f), rx * 0.4f), Offset(p.x - rx * 0.3f, p.y - ry * 0.45f), Size(rx * 0.6f, ry * 0.9f))
            drawLine(t(mez(PAN, NOGAL_OSC, 0.4f), oscuro), Offset(p.x - rx * 0.12f, p.y), Offset(p.x + rx * 0.12f, p.y), 0.3f * u, StrokeCap.Round)
        }
    }

    // ================================================================ Mapas: el escritorio del estudio

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = un()
        val n = nichoPx(MuebleVida.MAPAS)
        val tab = Rect(w * 0.012f, h * 0.02f, w * 0.988f, h * 0.83f)
        val frente = Rect(w * 0.03f, tab.bottom, w * 0.97f, h * 0.92f)
        // Las patas en punta, abiertas, con sus regatones de latón y su sombra.
        listOf(w * 0.08f to -1f, w * 0.92f to 1f).forEach { (x, s) ->
            val y0 = frente.bottom; val y1 = h * 0.995f
            drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Offset(x + s * 1.5f * u - 3 * u, y1 - 1.4f * u), Size(6 * u, 2 * u))
            val p = Path().apply { moveTo(x - 1.8f * u, y0); lineTo(x + 1.8f * u, y0); lineTo(x + s * 1.6f * u + 0.8f * u, y1 - 2.4f * u); lineTo(x + s * 1.6f * u - 0.8f * u, y1 - 2.4f * u); close() }
            drawPath(p, Brush.horizontalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), x - 2 * u, x + 2 * u))
            drawRect(Brush.horizontalGradient(listOf(t(OTTONE_OSC, oscuro), t(OTTONE_LUZ, oscuro), t(OTTONE, oscuro)), x + s * 1.6f * u - 0.9f * u, x + s * 1.6f * u + 0.9f * u), Offset(x + s * 1.6f * u - 0.9f * u, y1 - 2.6f * u), Size(1.8f * u, 2.6f * u))
        }
        // El faldón: el frente de nogal con dos cajones, sus tiradores de latón y la cerradura.
        drawRect(Brush.verticalGradient(listOf(t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), frente.top, frente.bottom), frente.topLeft, frente.size)
        vetas(frente, t(NOGAL_OSC, oscuro).copy(alpha = 0.6f), e.semilla + 5, 1.1f * u)
        moldura(Rect(frente.left - 0.6f * u, frente.top, frente.right + 0.6f * u, frente.top + frente.height * 0.2f), t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(frente.left, frente.top + frente.height * 0.2f), Size(frente.width, 0.5f * u))
        listOf(Rect(frente.left + frente.width * 0.06f, frente.top + frente.height * 0.32f, frente.left + frente.width * 0.44f, frente.bottom - frente.height * 0.14f),
            Rect(frente.left + frente.width * 0.56f, frente.top + frente.height * 0.32f, frente.right - frente.width * 0.06f, frente.bottom - frente.height * 0.14f)).forEach { c ->
            drawRect(Color.Black.copy(alpha = 0.35f), c.topLeft, c.size, style = Stroke(0.5f * u))
            drawLine(Color.White.copy(alpha = 0.12f), Offset(c.left, c.bottom), c.bottomRight, 0.4f * u)
            val tc = c.center
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(tc.x - 4.5f * u, tc.y + 0.7f * u), Offset(tc.x + 4.5f * u, tc.y + 0.7f * u), 1.2f * u, StrokeCap.Round)
            drawLine(t(OTTONE, oscuro), Offset(tc.x - 4.5f * u, tc.y), Offset(tc.x + 4.5f * u, tc.y), 1.0f * u, StrokeCap.Round)
            drawLine(t(OTTONE_LUZ, oscuro), Offset(tc.x - 4f * u, tc.y - 0.3f * u), Offset(tc.x + 4f * u, tc.y - 0.3f * u), 0.3f * u, StrokeCap.Round)
        }
        val cer = Offset(w * 0.5f, frente.top + frente.height * 0.6f)
        drawOval(t(OTTONE, oscuro), Offset(cer.x - 1.4f * u, cer.y - 2f * u), Size(2.8f * u, 4f * u))
        drawCircle(Color(0xFF1A120A), 0.5f * u, Offset(cer.x, cer.y - 0.4f * u))
        drawLine(Color(0xFF1A120A), Offset(cer.x, cer.y - 0.4f * u), Offset(cer.x, cer.y + 1f * u), 0.4f * u)
        // El tablero de nogal visto desde arriba, con su canto redondeado.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), Offset(tab.left + 0.8f * u, tab.top + 1.4f * u), tab.size, CornerRadius(2.6f * u))
        drawRoundRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), tab.top, tab.bottom), tab.topLeft, tab.size, CornerRadius(2.6f * u))
        vetas(tab, t(NOGAL_OSC, oscuro).copy(alpha = 0.55f), e.semilla, 1.3f * u, ondulacion = 1.6f)
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), tab.topLeft, tab.size, CornerRadius(2.6f * u), style = Stroke(0.7f * u))
        // El cuero verde con su filete de oro doble y las florecitas en las esquinas.
        val cuero = Rect(tab.left + 3.4f * u, tab.top + 3.4f * u, tab.right - 3.4f * u, tab.bottom - 3.4f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(cuero.left - 0.4f * u, cuero.top - 0.4f * u), Size(cuero.width + 0.8f * u, cuero.height + 0.8f * u))
        drawRect(Brush.radialGradient(listOf(t(mez(PELLE, Color.White, 0.08f), oscuro), t(PELLE, oscuro), t(mez(PELLE, Color.Black, 0.25f), oscuro)), cuero.center, cuero.width * 0.6f), cuero.topLeft, cuero.size)
        for (k in 0 until 40) drawCircle(Color.Black.copy(alpha = 0.08f), (0.3f + 0.5f * azar(e.semilla + 60, k)) * u, Offset(cuero.left + cuero.width * azar(e.semilla + 61, k), cuero.top + cuero.height * azar(e.semilla + 62, k)))
        val oro = t(OTTONE, oscuro)
        val f1 = Rect(cuero.left + 1.2f * u, cuero.top + 1.2f * u, cuero.right - 1.2f * u, cuero.bottom - 1.2f * u)
        drawRect(oro, f1.topLeft, f1.size, style = Stroke(0.35f * u))
        drawRect(oro.copy(alpha = 0.7f), Offset(f1.left + 0.9f * u, f1.top + 0.9f * u), Size(f1.width - 1.8f * u, f1.height - 1.8f * u), style = Stroke(0.2f * u))
        listOf(f1.topLeft, Offset(f1.right, f1.top), Offset(f1.left, f1.bottom), f1.bottomRight).forEach { o ->
            for (k in 0 until 4) { val a = k * Math.PI / 2 + Math.PI / 4; drawCircle(oro, 0.5f * u, Offset(o.x + 0.9f * u * cos(a).toFloat(), o.y + 0.9f * u * sin(a).toFloat())) }
        }
        // El mapa de papel (el nicho), algo más grande que él, con su sombra y sus dobleces.
        val m = 1.4f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f), Offset(n.left - m + 1f * u, n.top - m + 1.6f * u), Size(n.width + 2 * m, n.height + 2 * m))
        val borde = if (oscuro) mez(PAPEL_NOCHE, Color.White, 0.07f) else mez(PAPEL_DIA, Color(0xFFD9C79E), 0.4f)
        drawRect(borde, Offset(n.left - m, n.top - m), Size(n.width + 2 * m, n.height + 2 * m))
        drawRect(if (oscuro) PAPEL_NOCHE else PAPEL_DIA, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            listOf(0.25f, 0.5f, 0.75f).forEach { f -> drawLine(Color.Black.copy(alpha = if (oscuro) 0.12f else 0.05f), Offset(n.left + n.width * f, n.top), Offset(n.left + n.width * f, n.bottom), 0.4f * u) }
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.12f else 0.05f), Offset(n.left, n.center.y), Offset(n.right, n.center.y), 0.4f * u)
        }
        listOf(0.25f, 0.5f, 0.75f).forEach { f ->
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(n.left + n.width * f, n.top - m), Offset(n.left + n.width * f, n.top), 0.3f * u)
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(n.left + n.width * f, n.bottom), Offset(n.left + n.width * f, n.bottom + m), 0.3f * u)
        }
        // Los objetos del escritorio.
        val arriba = (cuero.top + n.top - m) / 2
        moka(Offset(w * 0.2f, arriba), 3.4f * u, oscuro)
        tazzina(Offset(w * 0.31f, arriba), 2.1f * u, oscuro)
        gafas(Offset(w * 0.78f, arriba), 3.2f * u, oscuro, e)
        lapiz(Offset(w * 0.47f, arriba + 1f * u), Offset(w * 0.64f, arriba - 0.6f * u), oscuro, e)
        val izq = (cuero.left + n.left - m) / 2
        llaves(Offset(izq, n.top + n.height * 0.18f), 2.4f * u, oscuro, e)
        boleto(Rect(izq - 2.6f * u, n.top + n.height * 0.55f, izq + 2.6f * u, n.top + n.height * 0.88f), oscuro, e)
        val der = (cuero.right + n.right + m) / 2
        lupa(Offset(der, n.top + n.height * 0.25f), 3.2f * u, Offset(der + 0.4f * u, n.top + n.height * 0.75f), oscuro)
        val abajo = (cuero.bottom + n.bottom + m) / 2
        listOf(w * 0.3f, w * 0.7f).forEach { x -> drawCircle(t(OTTONE, oscuro), 0.6f * u, Offset(x, abajo)) }
    }

    /** La moka vista desde arriba: la tapa ochavada de aluminio con su perilla, el pico y el asa negra. */
    private fun DrawScope.moka(c: Offset, r: Float, oscuro: Boolean) {
        val u = un()
        val oct = Path().apply { for (k in 0 until 8) { val a = k * Math.PI / 4 + Math.PI / 8; val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()); if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
        drawPath(oct, Color.Black.copy(alpha = 0.3f), style = Stroke(1.6f * u))
        drawRoundRect(t(Color(0xFF242220), oscuro), Offset(c.x + r * 0.8f, c.y - r * 0.28f), Size(r * 1.1f, r * 0.56f), CornerRadius(r * 0.25f))
        val pico = Path().apply { moveTo(c.x - r * 0.75f, c.y - r * 0.35f); lineTo(c.x - r * 1.45f, c.y); lineTo(c.x - r * 0.75f, c.y + r * 0.35f); close() }
        drawPath(pico, t(ACERO, oscuro))
        drawPath(oct, Brush.linearGradient(listOf(t(Color(0xFFEDEEEF), oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)))
        drawPath(oct, t(ACERO_OSC, oscuro), style = Stroke(0.3f * u))
        for (k in 0 until 8) { val a = k * Math.PI / 4 + Math.PI / 8; drawLine(t(ACERO_OSC, oscuro).copy(alpha = 0.5f), c, Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), 0.2f * u) }
        drawCircle(t(Color(0xFF242220), oscuro), r * 0.28f, c)
    }

    /** Unas gafas de sol plegadas: dos lentes oscuros con su brillo, el puente y las patillas. */
    private fun DrawScope.gafas(c: Offset, r: Float, oscuro: Boolean, e: EstadoMueble) {
        val u = un()
        listOf(-1f, 1f).forEach { s ->
            val o = Offset(c.x + s * r * 1.05f, c.y)
            drawOval(Color.Black.copy(alpha = 0.25f), Offset(o.x - r + 0.5f * u, o.y - r * 0.8f + 0.8f * u), Size(r * 2, r * 1.6f))
            drawOval(t(Color(0xFF2A2420), oscuro), Offset(o.x - r, o.y - r * 0.8f), Size(r * 2, r * 1.6f))
            drawOval(Brush.linearGradient(listOf(e.secundario.copy(alpha = 0.6f), Color(0xFF15120F)), Offset(o.x - r, o.y - r), Offset(o.x + r, o.y + r)), Offset(o.x - r * 0.82f, o.y - r * 0.64f), Size(r * 1.64f, r * 1.28f))
            drawLine(Color.White.copy(alpha = 0.35f), Offset(o.x - r * 0.5f, o.y - r * 0.3f), Offset(o.x - r * 0.1f, o.y - r * 0.5f), 0.4f * u, StrokeCap.Round)
        }
        drawArc(t(Color(0xFF2A2420), oscuro), 200f, 140f, false, Offset(c.x - r * 0.3f, c.y - r * 0.5f), Size(r * 0.6f, r * 0.6f), style = Stroke(0.6f * u))
        drawLine(t(Color(0xFF2A2420), oscuro), Offset(c.x - r * 2f, c.y - r * 0.5f), Offset(c.x + r * 1.6f, c.y + r * 0.9f), 0.6f * u)
    }

    /** Un lápiz amarillo con su punta afilada y la goma en su virola. */
    private fun DrawScope.lapiz(a: Offset, b: Offset, oscuro: Boolean, e: EstadoMueble) {
        val u = un()
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(a.x + 0.4f * u, a.y + 0.7f * u), Offset(b.x + 0.4f * u, b.y + 0.7f * u), 1.3f * u, StrokeCap.Round)
        val f = 0.86f
        val q = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
        drawLine(t(Color(0xFFE8B83A), oscuro), a, q, 1.2f * u)
        drawLine(t(Color(0xFFF6D36A), oscuro), Offset(a.x, a.y - 0.3f * u), Offset(q.x, q.y - 0.3f * u), 0.3f * u)
        drawLine(t(Color(0xFFE2C29A), oscuro), q, b, 1.0f * u, StrokeCap.Round)
        drawLine(Color(0xFF2A2420), Offset(q.x + (b.x - q.x) * 0.6f, q.y + (b.y - q.y) * 0.6f), b, 0.5f * u, StrokeCap.Round)
        val g = Offset(a.x + (b.x - a.x) * 0.06f, a.y + (b.y - a.y) * 0.06f)
        drawLine(t(ACERO, oscuro), g, Offset(a.x + (b.x - a.x) * 0.1f, a.y + (b.y - a.y) * 0.1f), 1.3f * u)
        drawLine(e.acento.copy(alpha = 0.9f), a, g, 1.2f * u, StrokeCap.Round)
    }

    /** Las llaves de la Vespa en su llavero: el aro, dos llaves de latón y la placa del acento. */
    private fun DrawScope.llaves(c: Offset, r: Float, oscuro: Boolean, e: EstadoMueble) {
        val u = un()
        drawCircle(Color.Black.copy(alpha = 0.25f), r, Offset(c.x + 0.4f * u, c.y + 0.7f * u), style = Stroke(0.6f * u))
        drawCircle(t(ACERO, oscuro), r, c, style = Stroke(0.5f * u))
        listOf(150f, 215f).forEachIndexed { i, ang ->
            val a = Math.toRadians(ang.toDouble())
            val o = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
            val d = Offset(o.x + r * 2.2f * cos(a).toFloat(), o.y + r * 2.2f * sin(a).toFloat())
            drawCircle(t(if (i == 0) OTTONE else ACERO, oscuro), r * 0.5f, o)
            drawCircle(Color.Black.copy(alpha = 0.4f), r * 0.16f, o)
            drawLine(t(if (i == 0) OTTONE else ACERO, oscuro), o, d, 0.7f * u, StrokeCap.Round)
        }
        val p = Offset(c.x + r * 0.6f, c.y + r * 1.4f)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(p.x - r * 0.6f + 0.4f * u, p.y + 0.6f * u), Size(r * 1.4f, r * 1.9f), CornerRadius(r * 0.4f))
        drawRoundRect(e.acento, Offset(p.x - r * 0.6f, p.y), Size(r * 1.4f, r * 1.9f), CornerRadius(r * 0.4f))
        drawCircle(Color.White.copy(alpha = 0.6f), r * 0.28f, Offset(p.x + r * 0.1f, p.y + r * 1.0f))
    }

    /** El boleto del bus: el cartón crema con la franja del secundario, la banda magnética y la marca del sello. */
    private fun DrawScope.boleto(r: Rect, oscuro: Boolean, e: EstadoMueble) {
        val u = un()
        rotate(-6f, r.center) {
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
            drawRect(t(Color(0xFFF2EAD6), oscuro), r.topLeft, r.size)
            drawRect(e.secundario, r.topLeft, Size(r.width, r.height * 0.18f))
            drawRect(t(Color(0xFF6A5A3A), oscuro), Offset(r.right - r.width * 0.28f, r.top + r.height * 0.22f), Size(r.width * 0.16f, r.height * 0.7f))
            for (k in 0 until 3) drawLine(t(TINTA, oscuro).copy(alpha = 0.4f), Offset(r.left + r.width * 0.15f, r.top + r.height * (0.35f + 0.14f * k)), Offset(r.left + r.width * 0.6f, r.top + r.height * (0.35f + 0.14f * k)), 0.3f * u)
        }
    }

    /** Una lupa: el aro de latón con el vidrio que brilla y el mango de madera. */
    private fun DrawScope.lupa(c: Offset, r: Float, fin: Offset, oscuro: Boolean) {
        val u = un()
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x + 0.5f * u, c.y + 0.8f * u), Offset(fin.x + 0.5f * u, fin.y + 0.8f * u), 1.4f * u, StrokeCap.Round)
        drawLine(t(NOGAL_OSC, oscuro), Offset(c.x + (fin.x - c.x) * 0.45f, c.y + (fin.y - c.y) * 0.45f), fin, 1.4f * u, StrokeCap.Round)
        drawLine(t(NOGAL_LUZ, oscuro), Offset(c.x + (fin.x - c.x) * 0.5f - 0.3f * u, c.y + (fin.y - c.y) * 0.5f), Offset(fin.x - 0.3f * u, fin.y - 0.4f * u), 0.4f * u, StrokeCap.Round)
        drawLine(t(OTTONE, oscuro), Offset(c.x, c.y + r), Offset(c.x + (fin.x - c.x) * 0.45f, c.y + (fin.y - c.y) * 0.45f), 0.9f * u)
        drawCircle(Color.Black.copy(alpha = 0.25f), r, Offset(c.x + 0.5f * u, c.y + 0.8f * u))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.1f else 0.3f), r, c)
        drawArc(Color.White.copy(alpha = 0.6f), 200f, 60f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(0.4f * u, cap = StrokeCap.Round))
        drawCircle(Brush.linearGradient(listOf(t(OTTONE_LUZ, oscuro), t(OTTONE_OSC, oscuro)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), r, c, style = Stroke(0.8f * u))
    }

    // ================================================================ Otras vidas: el escaparate de monedas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = un()
        val n = nichoPx(MuebleVida.OTRAS_VIDAS)
        val celda = n.width / 5f
        // La fachada de travertino.
        travertino(Rect(0f, 0f, w, h), oscuro, e.semilla)
        // Las juntas de los sillares de travertino.
        val junta = t(TRAVERTINO_SOMBRA, oscuro).copy(alpha = 0.8f)
        var yj = h * 0.2f; var fila = 0
        while (yj < h * 0.9f) {
            drawLine(junta, Offset(0f, yj), Offset(w, yj), 0.35f * u)
            var xj = if (fila % 2 == 0) w * 0.02f else w * 0.045f
            while (xj < w) { drawLine(junta, Offset(xj, yj), Offset(xj, yj + h * 0.12f), 0.35f * u); xj += w * 0.05f }
            yj += h * 0.12f; fila++
        }
        // El toldo a franjas verdes y crema, con su faldón festoneado.
        val toldo = Rect(0f, 0f, w, h * 0.05f)
        val franja = 4.2f * u
        var xf = 0f; var kf = 0
        while (xf < w) {
            val col = if (kf % 2 == 0) VERDE else Color(0xFFF2E8CF)
            drawRect(Brush.verticalGradient(listOf(t(mez(col, Color.White, 0.12f), oscuro), t(col, oscuro), t(mez(col, Color.Black, 0.2f), oscuro)), toldo.top, toldo.bottom), Offset(xf, toldo.top), Size(franja, toldo.height))
            drawArc(t(mez(col, Color.Black, 0.12f), oscuro), 0f, 180f, true, Offset(xf, toldo.bottom - franja * 0.5f), Size(franja, franja))
            xf += franja; kf++
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.18f), Offset(0f, toldo.bottom + franja * 0.5f), Size(w, 1.0f * u))
        drawLine(t(Color(0xFF2A2E2B), oscuro), Offset(0f, toldo.top + 0.4f * u), Offset(w, toldo.top + 0.4f * u), 0.8f * u)
        // La insegna: el panel verde con su doble filete de oro, las volutas y el medallón con una moneda.
        val ins = Rect(w * 0.08f, h * 0.09f, w * 0.92f, h * 0.18f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(ins.left + 0.6f * u, ins.top + 1 * u), ins.size)
        drawRect(Brush.verticalGradient(listOf(t(VERDE_LUZ, oscuro), t(VERDE, oscuro), t(VERDE_OSC, oscuro)), ins.top, ins.bottom), ins.topLeft, ins.size)
        val oro = t(OTTONE, oscuro)
        drawRect(oro, Offset(ins.left + 0.7f * u, ins.top + 0.7f * u), Size(ins.width - 1.4f * u, ins.height - 1.4f * u), style = Stroke(0.45f * u))
        drawRect(oro.copy(alpha = 0.6f), Offset(ins.left + 1.6f * u, ins.top + 1.6f * u), Size(ins.width - 3.2f * u, ins.height - 3.2f * u), style = Stroke(0.2f * u))
        val mc = ins.center
        val mr = ins.height * 0.38f
        listOf(-1f, 1f).forEach { s ->
            // Una voluta en C a cada lado del medallón, con su hoja, y una filita de perlas hasta la punta.
            val vc = Offset(mc.x + s * mr * 2.3f, mc.y)
            drawArc(oro, if (s > 0) 90f else -90f, 270f * s, false, Offset(vc.x - mr * 0.45f, vc.y - mr * 0.45f), Size(mr * 0.9f, mr * 0.9f), style = Stroke(0.45f * u, cap = StrokeCap.Round))
            drawLine(oro, Offset(mc.x + s * mr * 1.3f, mc.y), Offset(vc.x - s * mr * 0.45f, vc.y), 0.45f * u)
            drawCircle(oro, 0.6f * u, Offset(vc.x, vc.y))
            var x = vc.x + s * mr * 1.0f
            while (s * (x - mc.x) < ins.width * 0.45f) { drawCircle(oro.copy(alpha = 0.85f), 0.45f * u, Offset(x, mc.y)); x += s * 2.6f * u }
            listOf(ins.left, ins.right).forEach { xx -> drawCircle(oro, 0.5f * u, Offset(xx + (if (xx < mc.x) 1 else -1) * 3.2f * u, mc.y)) }
        }
        drawOval(t(VERDE_OSC, oscuro), Offset(mc.x - mr * 1.25f, mc.y - mr * 1.05f), Size(mr * 2.5f, mr * 2.1f))
        drawOval(oro, Offset(mc.x - mr * 1.25f, mc.y - mr * 1.05f), Size(mr * 2.5f, mr * 2.1f), style = Stroke(0.45f * u))
        drawCircle(Brush.radialGradient(listOf(t(OTTONE_LUZ, oscuro), t(OTTONE, oscuro), t(OTTONE_OSC, oscuro)), Offset(mc.x - mr * 0.3f, mc.y - mr * 0.3f), mr), mr * 0.72f, mc)
        drawCircle(t(OTTONE_OSC, oscuro), mr * 0.55f, mc, style = Stroke(0.3f * u))
        drawCircle(e.acento.copy(alpha = 0.85f), mr * 0.18f, mc)
        // Los faroles de hierro, encendidos, a los lados de la insegna.
        listOf(w * 0.035f, w * 0.965f).forEach { x -> farol(Offset(x, h * 0.15f), 2.4f * u, oscuro, n) }
        // El marco verde del escaparate, con su sombra.
        val marco = Rect(n.left - 2.2f * u, h * 0.2f, n.right + 2.2f * u, n.bottom + 2.2f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        drawRect(Brush.verticalGradient(listOf(t(VERDE_LUZ, oscuro), t(VERDE, oscuro)), marco.top, marco.bottom), marco.topLeft, marco.size)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.18f), marco.topLeft, marco.size, style = Stroke(0.5f * u))
        drawRect(t(VERDE_OSC, oscuro), Offset(n.left - 0.6f * u, n.top - 0.6f * u), Size(n.width + 1.2f * u, n.height + 1.2f * u))
        // Los cinco vanos (el nicho): el fondo de terciopelo, la luz de arriba y el reflejo del vidrio.
        drawRect(if (oscuro) Color(0xFF221C18) else Color(0xFFF3EBDC), n.topLeft, n.size)
        for (i in 0 until 5) {
            val c = Rect(n.left + celda * i, n.top, n.left + celda * (i + 1), n.bottom)
            clipRect(c.left, c.top, c.right, c.bottom) {
                mancha(Offset(c.center.x, c.top), c.width * 0.9f, (if (oscuro) LUZ.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.6f)))
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Color.Transparent), c.top, c.top + 3 * u), c.topLeft, Size(c.width, 3 * u))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.2f else 0.06f)), c.bottom - c.height * 0.25f, c.bottom), Offset(c.left, c.bottom - c.height * 0.25f), Size(c.width, c.height * 0.25f))
                val brillo = Color.White.copy(alpha = if (oscuro) 0.07f else 0.4f)
                drawLine(brillo, Offset(c.right - c.width * 0.36f, c.top), Offset(c.right, c.top + c.width * 0.36f), 1.6f * u)
                drawLine(brillo, Offset(c.right - c.width * 0.2f, c.top), Offset(c.right, c.top + c.width * 0.2f), 0.6f * u)
                // El escalón de terciopelo al pie de cada vano, con su varilla de latón.
                val esc = Rect(c.left, c.bottom - 2.6f * u, c.right, c.bottom)
                drawRect(Brush.verticalGradient(listOf(t(mez(VERDE_LUZ, Color.White, 0.1f), oscuro), t(VERDE_OSC, oscuro)), esc.top, esc.bottom), esc.topLeft, esc.size)
                drawLine(t(OTTONE_LUZ, oscuro), Offset(esc.left, esc.top), Offset(esc.right, esc.top), 0.4f * u)
            }
        }
        // Los montantes verdes entre los vanos, con su perfil de luz y sus tapas de latón.
        val mont = minOf(w * 0.012f, 1.9f * u)
        for (i in 1 until 5) {
            val x = n.left + celda * i
            drawRect(Brush.horizontalGradient(listOf(t(VERDE_OSC, oscuro), t(VERDE_LUZ, oscuro), t(VERDE, oscuro), t(VERDE_OSC, oscuro)), x - mont / 2, x + mont / 2), Offset(x - mont / 2, n.top), Size(mont, n.height))
            drawRect(t(OTTONE, oscuro), Offset(x - mont * 0.7f, n.top), Size(mont * 1.4f, 1.0f * u))
            drawRect(t(OTTONE, oscuro), Offset(x - mont * 0.7f, n.bottom - 1.0f * u), Size(mont * 1.4f, 1.0f * u))
        }
        // El alféizar de travertino, el zócalo verde con sus tableros y la acera.
        val alf = Rect(marco.left - 1.6f * u, marco.bottom, marco.right + 1.6f * u, marco.bottom + h * 0.045f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(alf.left, alf.bottom), Size(alf.width, 1 * u))
        travertino(alf, oscuro, e.semilla + 3)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.6f), alf.topLeft, Offset(alf.right, alf.top), 0.5f * u)
        val acera = h * 0.94f
        val zoc = Rect(marco.left, alf.bottom, marco.right, acera)
        drawRect(Brush.verticalGradient(listOf(t(VERDE, oscuro), t(VERDE_OSC, oscuro)), zoc.top, zoc.bottom), zoc.topLeft, zoc.size)
        for (i in 0 until 5) {
            val tb = Rect(n.left + celda * i + celda * 0.12f, zoc.top + zoc.height * 0.25f, n.left + celda * (i + 1) - celda * 0.12f, zoc.bottom - zoc.height * 0.2f)
            drawRect(t(VERDE_LUZ, oscuro), tb.topLeft, tb.size)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(tb.left, tb.top), Size(tb.width, 0.5f * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(tb.left, tb.top), Size(0.5f * u, tb.height))
            drawRect(Color.White.copy(alpha = 0.15f), Offset(tb.left, tb.bottom - 0.4f * u), Size(tb.width, 0.4f * u))
        }
        sampietrini(Rect(0f, acera, w, h), 2.6f * u, oscuro, e.semilla + 9)
        drawRect(t(TRAVERTINO_SOMBRA, oscuro), Offset(0f, acera), Size(w, 0.8f * u))
        // La placa de mármol de la calle en la pilastra de la izquierda, con su filete rojo.
        val pl = Rect(w * 0.006f, h * 0.38f, n.left - 2.8f * u, h * 0.5f)
        if (pl.width > 4 * u) {
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(pl.left + 0.4f * u, pl.top + 0.6f * u), pl.size)
            drawRect(t(Color(0xFFF5F2EA), oscuro), pl.topLeft, pl.size)
            drawRect(t(Color(0xFF8A2A1E), oscuro), Offset(pl.left + 0.6f * u, pl.top + 0.6f * u), Size(pl.width - 1.2f * u, pl.height - 1.2f * u), style = Stroke(0.35f * u))
        }
    }

    /** Un farol de hierro de pared: el brazo, el sombrerete, el vidrio encendido y el halo. */
    private fun DrawScope.farol(c: Offset, r: Float, oscuro: Boolean, fuera: Rect) {
        val u = un()
        val hierro = t(Color(0xFF2A2E2B), oscuro)
        clipRect(fuera.left, fuera.top, fuera.right, fuera.bottom, ClipOp.Difference) { mancha(c, r * (if (oscuro) 6f else 3.5f), LUZ.copy(alpha = if (oscuro) 0.4f else 0.25f)) }
        drawLine(hierro, Offset(c.x, c.y - r * 2.6f), Offset(c.x, c.y - r * 1.4f), 0.5f * u)
        val techo = Path().apply { moveTo(c.x - r * 1.1f, c.y - r * 1.0f); lineTo(c.x, c.y - r * 1.7f); lineTo(c.x + r * 1.1f, c.y - r * 1.0f); close() }
        drawPath(techo, hierro)
        val vidrio = Path().apply { moveTo(c.x - r * 0.8f, c.y - r); lineTo(c.x + r * 0.8f, c.y - r); lineTo(c.x + r * 0.55f, c.y + r * 0.9f); lineTo(c.x - r * 0.55f, c.y + r * 0.9f); close() }
        drawPath(vidrio, Brush.radialGradient(listOf(Color.White, LUZ, Color(0xFFE0A048)), c, r * 1.4f))
        drawPath(vidrio, hierro, style = Stroke(0.4f * u))
        drawLine(hierro, Offset(c.x, c.y - r), Offset(c.x, c.y + r * 0.9f), 0.3f * u)
        drawRect(hierro, Offset(c.x - r * 0.65f, c.y + r * 0.9f), Size(r * 1.3f, r * 0.3f))
        drawCircle(hierro, r * 0.2f, Offset(c.x, c.y + r * 1.4f))
    }

    // ================================================================ Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height
        val u = h / 100f
        val c = Offset(w / 2, h / 2)
        val linea = if (oscuro) Color.Black.copy(alpha = 0.55f) else Color(0xFF7A6A54)
        when (id) {
            "unesco" -> {
                // El Coliseo: tres órdenes de arcos y el ático, con el anillo roto a la derecha; un pino piñonero al lado.
                val a = Rect(c.x - 48 * u, h * 0.12f, c.x + 40 * u, h * 0.86f)
                drawOval(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Offset(a.left - 4 * u, a.bottom - 3 * u), Size(a.width + 22 * u, 8 * u))
                // El pino.
                val px = a.right + 10 * u
                drawLine(t(NOGAL, oscuro), Offset(px, a.bottom), Offset(px - 2 * u, h * 0.32f), 2.2f * u, StrokeCap.Round)
                listOf(Offset(-8f, 0f), Offset(0f, -4f), Offset(8f, 0f), Offset(-3f, 3f), Offset(4f, 3f)).forEach { o ->
                    drawOval(t(HOJA_OSC, oscuro), Offset(px - 2 * u + o.x * u - 9 * u, h * 0.25f + o.y * u - 5 * u), Size(18 * u, 10 * u))
                }
                drawOval(t(HOJA, oscuro), Offset(px - 2 * u - 10 * u, h * 0.2f - 4 * u), Size(16 * u, 8 * u))
                // La silueta con el borde roto.
                val forma = Path().apply {
                    moveTo(a.left, a.bottom); lineTo(a.left, a.top + 4 * u)
                    quadraticTo(a.left + a.width * 0.35f, a.top - 2 * u, a.left + a.width * 0.62f, a.top)
                    lineTo(a.left + a.width * 0.66f, a.top + a.height * 0.22f); lineTo(a.left + a.width * 0.78f, a.top + a.height * 0.26f)
                    lineTo(a.left + a.width * 0.84f, a.top + a.height * 0.5f); lineTo(a.right, a.top + a.height * 0.56f)
                    lineTo(a.right, a.bottom); close()
                }
                drawPath(forma, Brush.horizontalGradient(listOf(t(TRAVERTINO_SOMBRA, oscuro), t(TRAVERTINO_LUZ, oscuro), t(TRAVERTINO, oscuro), t(TRAVERTINO_SOMBRA, oscuro)), a.left, a.right))
                clipPath(forma) {
                    val pisos = listOf(0.26f, 0.5f, 0.74f)
                    val n = 7
                    pisos.forEachIndexed { j, yf ->
                        val y0 = a.top + a.height * yf
                        drawLine(linea, Offset(a.left, y0), Offset(a.right, y0), 0.8f * u)
                        val alto = a.height * 0.2f
                        for (k in 0 until n) {
                            val x0 = a.left + a.width * (k + 0.2f) / n
                            val aw = a.width / n * 0.6f
                            val arco = Path().apply { moveTo(x0, y0 + alto + 0.8f * u); lineTo(x0, y0 + alto * 0.45f + 1.4f * u); arcTo(Rect(x0, y0 + 1.4f * u, x0 + aw, y0 + aw + 1.4f * u), 180f, 180f, false); lineTo(x0 + aw, y0 + alto + 0.8f * u); close() }
                            drawPath(arco, mez(t(Color(0xFF5A4630), oscuro), Color.Black, if (k < 2) 0.3f else 0f))
                            drawPath(arco, Color.Black.copy(alpha = 0.25f), style = Stroke(0.4f * u))
                        }
                        if (j == 0) Unit
                    }
                    // El ático con sus ventanitas.
                    for (k in 0 until 8) drawRect(t(Color(0xFF5A4630), oscuro), Offset(a.left + a.width * (k + 0.35f) / 9f, a.top + a.height * 0.1f), Size(a.width / 9f * 0.3f, a.height * 0.08f))
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.15f)), a.top, a.bottom), a.topLeft, a.size)
                }
                drawPath(forma, linea, style = Stroke(0.8f * u))
                drawRect(e.c1, Offset(a.left + a.width * 0.08f, a.top + 1.2f * u - 6 * u), Size(0.8f * u, 6 * u))
                drawRect(e.c1, Offset(a.left + a.width * 0.08f + 0.8f * u, a.top + 1.2f * u - 6 * u), Size(4 * u, 2.6f * u))
            }
            "rascar" -> {
                // El rasca y gana: la tarjeta con su cabecera del acento, la capa plateada raspada y la moneda de un euro.
                val r = Rect(c.x - 46 * u, h * 0.1f, c.x + 34 * u, h * 0.86f)
                rotate(-4f, r.center) {
                    drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(3 * u))
                    drawRoundRect(t(Color(0xFFF7F1E3), oscuro), r.topLeft, r.size, CornerRadius(3 * u))
                    drawRoundRect(linea, r.topLeft, r.size, CornerRadius(3 * u), style = Stroke(0.6f * u))
                    drawRect(e.c1, Offset(r.left, r.top + 3 * u), Size(r.width, r.height * 0.18f))
                    for (k in 0 until 5) drawCircle(t(OTTONE_LUZ, oscuro), 1.2f * u, Offset(r.left + r.width * (0.2f + 0.15f * k), r.top + 3 * u + r.height * 0.09f))
                    val capa = Rect(r.left + 5 * u, r.top + r.height * 0.36f, r.right - 5 * u, r.bottom - 6 * u)
                    // Lo de abajo: un trozo de mapa con el Tíber.
                    drawRect(t(Color(0xFFEAD9B4), oscuro), capa.topLeft, capa.size)
                    val rio = Path().apply { moveTo(capa.left, capa.top + capa.height * 0.3f); cubicTo(capa.left + capa.width * 0.3f, capa.top + capa.height * 0.9f, capa.left + capa.width * 0.5f, capa.top, capa.right, capa.top + capa.height * 0.7f) }
                    drawPath(rio, mez(e.c2, Color.White, 0.2f), style = Stroke(2.2f * u))
                    drawCircle(e.c1, 1.6f * u, Offset(capa.left + capa.width * 0.62f, capa.top + capa.height * 0.55f))
                    val raspado = Path().apply { addOval(Rect(capa.left + capa.width * 0.18f, capa.top - 1 * u, capa.left + capa.width * 0.95f, capa.bottom + 2 * u)) }
                    clipPath(raspado, ClipOp.Difference) {
                        drawRect(Brush.linearGradient(listOf(t(Color(0xFFE8EAEC), oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro), t(ACERO, oscuro)), capa.topLeft, capa.bottomRight), capa.topLeft, capa.size)
                        for (k in 0 until 8) drawLine(Color.White.copy(alpha = 0.3f), Offset(capa.left + capa.width * k / 8f, capa.top), Offset(capa.left + capa.width * k / 8f - 4 * u, capa.bottom), 0.4f * u)
                    }
                    drawRect(linea, capa.topLeft, capa.size, style = Stroke(0.5f * u))
                }
                // La moneda de un euro: el anillo dorado y el centro plateado.
                val mc = Offset(r.right + 4 * u, h * 0.74f)
                val mr = 15 * u
                drawCircle(Color.Black.copy(alpha = 0.3f), mr, Offset(mc.x + 1.5f * u, mc.y + 2 * u))
                drawCircle(Brush.radialGradient(listOf(t(OTTONE_LUZ, oscuro), t(OTTONE, oscuro), t(OTTONE_OSC, oscuro)), Offset(mc.x - mr * 0.4f, mc.y - mr * 0.4f), mr * 1.5f), mr, mc)
                drawCircle(Brush.radialGradient(listOf(t(Color.White, oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro)), Offset(mc.x - mr * 0.3f, mc.y - mr * 0.3f), mr), mr * 0.64f, mc)
                drawCircle(t(ACERO_OSC, oscuro), mr * 0.64f, mc, style = Stroke(0.4f * u))
                for (k in 0 until 12) { val a = k * Math.PI / 6; drawCircle(t(OTTONE_OSC, oscuro), 0.6f * u, Offset(mc.x + mr * 0.82f * cos(a).toFloat(), mc.y + mr * 0.82f * sin(a).toFloat())) }
                drawArc(t(ACERO_OSC, oscuro), 200f, 140f, false, Offset(mc.x - mr * 0.38f, mc.y - mr * 0.38f), Size(mr * 0.76f, mr * 0.76f), style = Stroke(0.6f * u))
            }
            else -> {
                // Lugares: el plano plegado en tres, con la ruta y tres chinchetas de colores.
                val r = Rect(c.x - 52 * u, h * 0.22f, c.x + 52 * u, h * 0.86f)
                val p = r.width / 3f
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(r.left + 2 * u, r.top + 3 * u), r.size)
                for (k in 0 until 3) {
                    val pan = Rect(r.left + p * k, r.top + (if (k == 1) 2.5f * u else 0f), r.left + p * (k + 1), r.bottom + (if (k == 1) 2.5f * u else 0f))
                    drawRect(Brush.horizontalGradient(if (k == 1) listOf(t(Color(0xFFE0D3B6), oscuro), t(Color(0xFFF2E9D6), oscuro)) else listOf(t(Color(0xFFF7F1E3), oscuro), t(Color(0xFFE6DAC0), oscuro)), pan.left, pan.right), pan.topLeft, pan.size)
                    drawLine(t(OCRA, oscuro).copy(alpha = 0.5f), Offset(pan.left, pan.top + pan.height * 0.4f), Offset(pan.right, pan.top + pan.height * 0.55f), 1.4f * u)
                    drawLine(mez(e.c2, Color.White, 0.3f).copy(alpha = 0.7f), Offset(pan.left, pan.top + pan.height * 0.8f), Offset(pan.right, pan.top + pan.height * 0.7f), 2f * u)
                    drawRect(linea, pan.topLeft, pan.size, style = Stroke(0.5f * u))
                }
                val pts = listOf(Offset(r.left + r.width * 0.16f, r.top + r.height * 0.55f), Offset(r.left + r.width * 0.5f, r.top + r.height * 0.28f), Offset(r.left + r.width * 0.84f, r.top + r.height * 0.5f))
                val ruta = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(r.left + r.width * 0.3f, r.top + r.height * 0.15f, pts[1].x, pts[1].y); quadraticTo(r.left + r.width * 0.7f, r.top + r.height * 0.6f, pts[2].x, pts[2].y) }
                drawPath(ruta, e.c1, style = Stroke(0.9f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
                listOf(e.c1, e.c2, t(OTTONE, oscuro)).forEachIndexed { i, col ->
                    val o = pts[i]
                    val cabeza = Offset(o.x + 3 * u, o.y - 11 * u)
                    drawLine(Color.Black.copy(alpha = 0.25f), o, Offset(o.x + 6 * u, o.y + 1 * u), 1.6f * u, StrokeCap.Round)
                    drawLine(t(ACERO, oscuro), o, cabeza, 1.0f * u, StrokeCap.Round)
                    drawCircle(Color.Black.copy(alpha = 0.3f), 5.5f * u, Offset(cabeza.x + 1 * u, cabeza.y + 1.5f * u))
                    drawCircle(Brush.radialGradient(listOf(mez(col, Color.White, 0.5f), col, mez(col, Color.Black, 0.35f)), Offset(cabeza.x - 1.8f * u, cabeza.y - 1.8f * u), 7 * u), 5.5f * u, cabeza)
                    drawCircle(Color.White.copy(alpha = 0.6f), 1.4f * u, Offset(cabeza.x - 1.8f * u, cabeza.y - 1.8f * u))
                }
            }
        }
    }

    // ================================================================ Notas: la bacheca del barrio

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = un()
        val n = nichoPx(MuebleVida.NOTAS)
        // El muro de revoque ocre, con sus manchas y un desconchón que deja ver el ladrillo.
        drawRect(Brush.verticalGradient(listOf(t(OCRA, oscuro), t(OCRA_OSC, oscuro)), 0f, h), Offset.Zero, size)
        for (k in 0 until 30) mancha(Offset(w * azar(e.semilla + 1, k), h * azar(e.semilla + 2, k)), (5f + 12f * azar(e.semilla + 3, k)) * u, (if (k % 3 == 0) Color.White else Color(0xFF7A4A20)).copy(alpha = 0.06f))
        // El zócalo de travertino abajo.
        travertino(Rect(0f, h * 0.955f, w, h), oscuro, e.semilla + 4)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, h * 0.955f), Size(w, 0.5f * u))
        // El marco verde de la bacheca, con su sombra en el muro.
        val marco = Rect(w * 0.025f, h * 0.1f, w * 0.975f, h * 0.925f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f), Offset(marco.left + 1.2f * u, marco.top + 2f * u), marco.size)
        drawRect(t(VERDE, oscuro), marco.topLeft, marco.size)
        drawRect(Brush.horizontalGradient(listOf(t(VERDE_LUZ, oscuro), t(VERDE, oscuro)), marco.left, n.left), marco.topLeft, Size(n.left - marco.left, marco.height))
        drawRect(Brush.horizontalGradient(listOf(t(VERDE, oscuro), t(VERDE_OSC, oscuro)), n.right, marco.right), Offset(n.right, marco.top), Size(marco.right - n.right, marco.height))
        drawRect(Brush.verticalGradient(listOf(t(VERDE_LUZ, oscuro), t(VERDE, oscuro)), marco.top, n.top), Offset(n.left, marco.top), Size(n.width, n.top - marco.top))
        drawRect(Brush.verticalGradient(listOf(t(VERDE, oscuro), t(VERDE_OSC, oscuro)), n.bottom, marco.bottom), Offset(n.left, n.bottom), Size(n.width, marco.bottom - n.bottom))
        listOf(Offset(marco.left, marco.top) to Offset(n.left, n.top), Offset(marco.right, marco.top) to Offset(n.right, n.top), Offset(marco.left, marco.bottom) to Offset(n.left, n.bottom), Offset(marco.right, marco.bottom) to Offset(n.right, n.bottom)).forEach { (a, b) -> drawLine(t(VERDE_OSC, oscuro), a, b, 0.4f * u) }
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), marco.topLeft, marco.size, style = Stroke(0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(n.left - 0.8f * u, n.top - 0.8f * u), Size(n.width + 1.6f * u, n.height + 1.6f * u), style = Stroke(0.8f * u))
        // El corcho (el nicho), con sus granos.
        drawRect(if (oscuro) CORCHO_NOCHE else CORCHO_DIA, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val granos = (n.width * n.height / (5f * u * u)).toInt().coerceIn(60, 700)
            for (k in 0 until granos) drawCircle((if (oscuro) Color(0xFF5A4636) else Color(0xFFB8956A)).copy(alpha = 0.18f + 0.18f * azar(e.semilla + 7, k)), (0.2f + 0.35f * azar(e.semilla + 8, k)) * u, Offset(n.left + n.width * azar(e.semilla + 9, k), n.top + n.height * azar(e.semilla + 10, k)))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Color.Transparent), n.top, n.top + 3 * u), n.topLeft, Size(n.width, 3 * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Color.Transparent), n.left, n.left + 2.5f * u), n.topLeft, Size(2.5f * u, n.height))
        }
        // Las escuadras de latón en las esquinas, con sus tornillos.
        val ot = t(OTTONE, oscuro); val ol = t(OTTONE_LUZ, oscuro); val oo = t(OTTONE_OSC, oscuro)
        val l = 6f * u; val g = 1.6f * u
        listOf(Triple(marco.left, marco.top, Offset(1f, 1f)), Triple(marco.right, marco.top, Offset(-1f, 1f)), Triple(marco.left, marco.bottom, Offset(1f, -1f)), Triple(marco.right, marco.bottom, Offset(-1f, -1f))).forEach { (x, y, s) ->
            val p = Path().apply {
                moveTo(x, y); lineTo(x + s.x * l, y); lineTo(x + s.x * l, y + s.y * g); lineTo(x + s.x * g, y + s.y * g); lineTo(x + s.x * g, y + s.y * l); lineTo(x, y + s.y * l); close()
            }
            drawPath(p, Brush.linearGradient(listOf(ol, ot, oo), Offset(x, y), Offset(x + s.x * l, y + s.y * l)))
            drawCircle(oo, 0.35f * u, Offset(x + s.x * l * 0.7f, y + s.y * g * 0.5f))
            drawCircle(oo, 0.35f * u, Offset(x + s.x * g * 0.5f, y + s.y * l * 0.7f))
        }
        // Las bisagras a la izquierda y la cerradura a la derecha.
        listOf(0.25f, 0.75f).forEach { f ->
            val y = marco.top + marco.height * f
            drawRect(Brush.horizontalGradient(listOf(oo, ol, ot), marco.left - 0.8f * u, marco.left + 1.2f * u), Offset(marco.left - 0.8f * u, y - 3.5f * u), Size(2f * u, 7f * u))
            drawLine(oo, Offset(marco.left + 0.2f * u, y - 3.5f * u), Offset(marco.left + 0.2f * u, y + 3.5f * u), 0.3f * u)
        }
        val ce = Offset((n.right + marco.right) / 2, marco.top + marco.height * 0.5f)
        drawRoundRect(Brush.linearGradient(listOf(ol, ot, oo), Offset(ce.x - 1.5f * u, ce.y - 4 * u), Offset(ce.x + 1.5f * u, ce.y + 4 * u)), Offset(ce.x - 1.5f * u, ce.y - 4 * u), Size(3 * u, 8 * u), CornerRadius(0.6f * u))
        drawCircle(Color(0xFF1A120A), 0.55f * u, Offset(ce.x, ce.y - 0.6f * u))
        drawLine(Color(0xFF1A120A), Offset(ce.x, ce.y - 0.6f * u), Offset(ce.x, ce.y + 1.4f * u), 0.45f * u)
        // La plaquita de latón en el travesaño de abajo.
        val pq = Rect(w * 0.42f, (n.bottom + marco.bottom) / 2 - 1.4f * u, w * 0.58f, (n.bottom + marco.bottom) / 2 + 1.4f * u)
        drawRect(Brush.verticalGradient(listOf(ol, ot, oo), pq.top, pq.bottom), pq.topLeft, pq.size)
        listOf(pq.left + 0.8f * u, pq.right - 0.8f * u).forEach { x -> drawCircle(oo, 0.3f * u, Offset(x, pq.center.y)) }
        // El tejadillo de tejas sobre la bacheca, con su viga y su sombra.
        val viga = Rect(w * 0.01f, h * 0.075f, w * 0.99f, h * 0.1f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), Offset(viga.left, viga.bottom), Size(viga.width, 2.5f * u))
        drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL_OSC, oscuro)), viga.top, viga.bottom), viga.topLeft, viga.size)
        vetas(viga, t(NOGAL_OSC, oscuro).copy(alpha = 0.5f), e.semilla + 30, 0.8f * u)
        val tejas = Rect(0f, h * 0.012f, w, viga.top)
        val tw = 5.2f * u
        val nt = (tejas.width / tw).toInt() + 1
        drawRect(t(COTTO_OSC, oscuro), tejas.topLeft, tejas.size)
        for (k in 0 until nt) {
            val x = tejas.left + k * tw
            val col = mez(COTTO, COTTO_LUZ, azar(e.semilla + 11, k) * 0.6f)
            drawRoundRect(Brush.horizontalGradient(listOf(t(mez(col, Color.Black, 0.25f), oscuro), t(mez(col, Color.White, 0.18f), oscuro), t(col, oscuro), t(mez(col, Color.Black, 0.3f), oscuro)), x, x + tw), Offset(x + 0.2f * u, tejas.top), Size(tw - 0.4f * u, tejas.height + 0.6f * u), CornerRadius(tw * 0.45f))
            drawOval(t(mez(col, Color.Black, 0.45f), oscuro), Offset(x + tw * 0.12f, tejas.bottom - tw * 0.32f), Size(tw * 0.76f, tw * 0.5f))
        }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.25f), Offset(0f, tejas.top + 0.4f * u), Offset(w, tejas.top + 0.4f * u), 0.6f * u)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 4f || r.height < 4f) return
        val s = minOf(r.width, r.height) / 100f
        val hoja = Rect(r.left + 1.5f * s, r.top + 2f * s, r.right - 2.5f * s, r.bottom - 3f * s)
        val giro = (azar(e.indice + 5, 3) - 0.5f) * 2.4f
        val base = when (e.tipo) { "idea" -> GIALLO; "pensamiento" -> CUADERNO; "proposito" -> FICHA; else -> ROSA }
        val papel = if (oscuro) mez(base, Color(0xFF231D19), 0.82f) else base
        // La sombra y el papel, apenas torcido.
        rotate(giro, hoja.center) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(hoja.left + 1.6f * s, hoja.top + 2.4f * s), hoja.size)
            when (e.tipo) {
                "idea" -> {
                    // La hoja amarilla con la esquina de abajo doblada.
                    val d = 11f * s
                    val forma = Path().apply { moveTo(hoja.left, hoja.top); lineTo(hoja.right, hoja.top); lineTo(hoja.right, hoja.bottom - d); lineTo(hoja.right - d, hoja.bottom); lineTo(hoja.left, hoja.bottom); close() }
                    drawPath(forma, papel)
                    drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.03f else 0.25f), Color.Transparent), hoja.top, hoja.top + hoja.height * 0.3f), hoja.topLeft, Size(hoja.width, hoja.height * 0.3f))
                    val dob = Path().apply { moveTo(hoja.right, hoja.bottom - d); lineTo(hoja.right - d, hoja.bottom - d); lineTo(hoja.right - d, hoja.bottom); close() }
                    drawPath(dob, mez(papel, Color.Black, 0.15f))
                    drawPath(dob, Color.Black.copy(alpha = 0.2f), style = Stroke(0.4f * s))
                }
                "pensamiento" -> {
                    // La hoja de cuaderno arrancada: el borde de los agujeros, los renglones y el margen.
                    drawRect(papel, hoja.topLeft, hoja.size)
                    val renglon = if (oscuro) Color(0xFF6F8FA8).copy(alpha = 0.25f) else Color(0xFF8FB0C8).copy(alpha = 0.45f)
                    var y = hoja.top + hoja.height * 0.16f + 12f * s
                    while (y < hoja.bottom - 4 * s) { drawLine(renglon, Offset(hoja.left, y), Offset(hoja.right, y), 0.35f * s); y += 12f * s * 1.33f }
                    drawLine((if (oscuro) Color(0xFFC0605A) else Color(0xFFD9786E)).copy(alpha = 0.6f), Offset(hoja.left + hoja.width * 0.1f, hoja.top), Offset(hoja.left + hoja.width * 0.1f, hoja.bottom), 0.4f * s)
                    var k = 0f
                    while (k < hoja.height - 4 * s) { drawCircle(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.18f), 1.3f * s, Offset(hoja.left + hoja.width * 0.045f, hoja.top + 5 * s + k)); k += 8 * s }
                }
                "proposito" -> {
                    // La ficha de cartulina con su franja del acento abajo.
                    drawRect(papel, hoja.topLeft, hoja.size)
                    drawRect(e.acento.copy(alpha = if (oscuro) 0.5f else 0.7f), Offset(hoja.left, hoja.bottom - hoja.height * 0.06f), Size(hoja.width, hoja.height * 0.025f))
                    drawLine(Color.Black.copy(alpha = 0.12f), Offset(hoja.left + hoja.width * 0.06f, hoja.top + hoja.height * 0.13f), Offset(hoja.right - hoja.width * 0.06f, hoja.top + hoja.height * 0.13f), 0.4f * s)
                }
                else -> {
                    // El boleto rosa con el borde de arriba perforado.
                    drawRect(papel, hoja.topLeft, hoja.size)
                    var x = hoja.left + 3 * s
                    while (x < hoja.right - 2 * s) { drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), 0.8f * s, Offset(x, hoja.top + hoja.height * 0.09f)); x += 4 * s }
                    drawRect(mez(papel, Color(0xFFB5503A), 0.25f), Offset(hoja.left, hoja.bottom - hoja.height * 0.09f), Size(hoja.width, hoja.height * 0.09f))
                }
            }
            if (e.fijada) drawRect(e.acento.copy(alpha = 0.85f), Offset(hoja.left + 2 * s, hoja.top + 2 * s), Size(hoja.width - 4 * s, hoja.height - 4 * s), style = Stroke(0.8f * s))
        }
        // El sujetador de cada tipo, arriba al centro.
        val tope = Offset(hoja.center.x, hoja.top + 1.2f * s)
        when (e.tipo) {
            "idea", "pensamiento" -> chincheta(Offset(tope.x, tope.y + 3 * s), 4.6f * s, if (e.tipo == "idea") e.acento else Color(0xFF3D5E45), s)
            "proposito" -> {
                // La cinta de papel, en diagonal, con su textura.
                rotate(-8f, tope) {
                    val cinta = Rect(tope.x - 13 * s, tope.y - 3.5f * s, tope.x + 13 * s, tope.y + 3.5f * s)
                    drawRect((if (oscuro) Color(0xFF8E8268) else Color(0xFFE8DDB8)).copy(alpha = 0.85f), cinta.topLeft, cinta.size)
                    drawLine(Color.Black.copy(alpha = 0.1f), Offset(cinta.left, cinta.top), Offset(cinta.left + 1 * s, cinta.bottom), 0.6f * s)
                    drawLine(Color.Black.copy(alpha = 0.1f), Offset(cinta.right, cinta.top), Offset(cinta.right - 1 * s, cinta.bottom), 0.6f * s)
                }
            }
            else -> {
                // La pinza de metal.
                val pz = Rect(tope.x - 8 * s, tope.y - 1 * s, tope.x + 8 * s, tope.y + 6 * s)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(pz.left + 0.8f * s, pz.top + 1f * s), pz.size)
                drawRect(Brush.verticalGradient(listOf(Color(0xFF4A4E52), Color(0xFF1E2124)), pz.top, pz.bottom), pz.topLeft, pz.size)
                drawLine(Color.White.copy(alpha = 0.3f), Offset(pz.left, pz.top + 0.4f * s), Offset(pz.right, pz.top + 0.4f * s), 0.6f * s)
                listOf(-1f, 1f).forEach { k ->
                    val a = Offset(tope.x + k * 5 * s, pz.top)
                    drawLine(ACERO, a, Offset(tope.x + k * 3.5f * s, pz.top - 5 * s), 0.8f * s, StrokeCap.Round)
                }
                drawLine(ACERO, Offset(tope.x - 3.5f * s, pz.top - 5 * s), Offset(tope.x + 3.5f * s, pz.top - 5 * s), 0.8f * s, StrokeCap.Round)
            }
        }
        // Fijada: el timbro redondo del acento en la esquina de abajo.
        if (e.fijada) {
            val tc = Offset(hoja.right - hoja.width * 0.08f, hoja.bottom - hoja.height * 0.085f)
            val tr = minOf(hoja.width * 0.075f, hoja.height * 0.075f)
            drawCircle(e.acento.copy(alpha = 0.75f), tr, tc, style = Stroke(0.7f * s))
            drawCircle(e.acento.copy(alpha = 0.75f), tr * 0.62f, tc, style = Stroke(0.4f * s))
            drawCircle(e.acento.copy(alpha = 0.75f), tr * 0.22f, tc)
        }
    }

    /** Una chincheta de cabeza redonda, con su brillo y su sombra. */
    private fun DrawScope.chincheta(c: Offset, r: Float, color: Color, s: Float) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + 1.2f * s, c.y + 1.8f * s))
        drawCircle(Brush.radialGradient(listOf(mez(color, Color.White, 0.5f), color, mez(color, Color.Black, 0.35f)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.4f), r, c)
        drawCircle(Color.White.copy(alpha = 0.6f), r * 0.25f, Offset(c.x - r * 0.35f, c.y - r * 0.35f))
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) TINTA_NOCHE else TINTA
}
