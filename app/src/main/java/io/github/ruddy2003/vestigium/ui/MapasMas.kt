package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El motor de rutas: cada campaña es un mapa con su punto de partida, siete paradas con nombre del tema, el trazo del
 * camino (calzada, ruta de caravanas, postas del yam, huellas de carreta…) y el destino. El marcador del tema avanza por
 * la ruta con los días cumplidos. Aquí están los siete temas del lote 2 y el marcador de todos.
 */
object MapasMas {
    /** Las siete paradas de cada tema, en fracciones del mapa (más o menos donde quedan en la geografía). Null = el zigzag de siempre. */
    fun puntos(tema: TemaId): List<Offset>? = when (tema) {
        // Toledo → Calatrava → Córdoba → Sevilla → Málaga → Almería → Granada.
        TemaId.ANDALUS -> geo(listOf(-4.02 to 39.86, -3.83 to 38.9, -4.78 to 37.88, -5.98 to 37.39, -4.42 to 36.72, -2.46 to 36.84, -3.6 to 37.18), -6.4, -2.1, 36.4, 40.2)
        // Chang'an → Dunhuang → Turfán → Kashgar → Samarcanda → Bujará → Antioquía (de oriente a occidente).
        TemaId.SEDA -> geo(listOf(108.9 to 34.3, 94.7 to 40.1, 89.2 to 42.9, 76.0 to 39.5, 66.9 to 39.6, 64.4 to 39.8, 36.2 to 36.2), 112.0, 33.0, 31.0, 45.0)
        // Las postas del yam, de Janbalic a Karakórum.
        TemaId.MONGOL -> listOf(Offset(0.93f, 0.7f), Offset(0.8f, 0.42f), Offset(0.66f, 0.62f), Offset(0.52f, 0.36f), Offset(0.38f, 0.56f), Offset(0.22f, 0.3f), Offset(0.07f, 0.44f))
        // La caravana de carretas: del puerto al pueblo nuevo, cruzando el río.
        TemaId.COLONOS -> listOf(Offset(0.07f, 0.72f), Offset(0.2f, 0.5f), Offset(0.34f, 0.62f), Offset(0.5f, 0.38f), Offset(0.64f, 0.56f), Offset(0.79f, 0.32f), Offset(0.93f, 0.48f))
        // La expedición por los biomas, en línea casi recta, de franja en franja.
        TemaId.BOTANICO -> (0 until 7).map { i -> Offset(0.07f + 0.86f * i / 6f, 0.5f + 0.16f * sin(i * 1.3f)) }
        // Los mercados de Navidad: Estrasburgo, Colmar, Núremberg, Dresde, Praga, Viena, Salzburgo.
        TemaId.NAVIDAD -> geo(listOf(7.75 to 48.58, 7.36 to 48.08, 11.08 to 49.45, 13.74 to 51.05, 14.42 to 50.08, 16.37 to 48.21, 13.04 to 47.8), 6.4, 17.2, 47.2, 51.6)
        // La travesía de la compañía: aldea, bosque, vado, montañas, mina, puente y fortaleza.
        TemaId.ANTIGUAS -> listOf(Offset(0.07f, 0.74f), Offset(0.22f, 0.56f), Offset(0.36f, 0.7f), Offset(0.5f, 0.3f), Offset(0.62f, 0.52f), Offset(0.78f, 0.62f), Offset(0.93f, 0.3f))
        // Los puntos de control: piso por piso, de la ventanilla de abajo al Ministerio en lo alto.
        TemaId.DISTOPIA -> listOf(Offset(0.08f, 0.84f), Offset(0.22f, 0.72f), Offset(0.36f, 0.74f), Offset(0.5f, 0.54f), Offset(0.62f, 0.5f), Offset(0.74f, 0.32f), Offset(0.88f, 0.16f))
        // La traza de la red: saltos de servidor en servidor, de la máquina propia a la raíz.
        TemaId.CIBER -> listOf(Offset(0.07f, 0.52f), Offset(0.2f, 0.28f), Offset(0.34f, 0.6f), Offset(0.48f, 0.36f), Offset(0.62f, 0.7f), Offset(0.77f, 0.4f), Offset(0.93f, 0.6f))
        else -> null
    }

    /** Llama a [f] cada [paso] a lo largo de [p] (para traviesas, paquetes, marcas de órbita…). */
    private fun aLoLargo(p: Path, paso: Float, desde: Float = paso / 2, f: (Offset) -> Unit) {
        val pm = PathMeasure().apply { setPath(p, false) }
        var s = desde; while (s < pm.length) { f(pm.getPosition(s)); s += paso }
    }

    /** Proyecta longitudes y latitudes al rectángulo del mapa (con margen). */
    private fun geo(l: List<Pair<Double, Double>>, lon0: Double, lon1: Double, lat0: Double, lat1: Double): List<Offset> =
        l.map { (lon, lat) -> Offset((0.07 + 0.86 * (lon - lon0) / (lon1 - lon0)).toFloat(), (0.92 - 0.84 * (lat - lat0) / (lat1 - lat0)).toFloat()) }

    // ================================================================== fondos

    fun DrawScope.fondoMas(tema: TemaId, cs: ColorScheme, avance: Float) {
        val oscuro = cs.background.luminance() < 0.5f
        val w = size.width; val h = size.height; val d = density
        val tinta = cs.onSurfaceVariant.copy(alpha = 0.22f)
        val agua = Color(0xFF4F86B8).copy(alpha = if (oscuro) 0.28f else 0.2f)
        fun monte(x: Float, y: Float, s: Float, c: Color) = drawPath(Path().apply { moveTo(x - s, y); lineTo(x, y - s * 1.1f); lineTo(x + s, y) }, c, style = Stroke(1.2f * d))
        when (tema) {
            TemaId.ANDALUS -> {
                // El mar al sur, el Guadalquivir y la Sierra Nevada.
                drawPath(Path().apply { moveTo(0f, h); lineTo(0f, h * 0.86f); cubicTo(w * 0.2f, h * 0.92f, w * 0.4f, h * 1.02f, w * 0.6f, h * 0.98f); cubicTo(w * 0.8f, h * 0.95f, w * 0.9f, h * 0.98f, w, h * 0.9f); lineTo(w, h); close() }, agua)
                drawPath(Path().apply { moveTo(w * 0.62f, h * 0.36f); cubicTo(w * 0.4f, h * 0.4f, w * 0.3f, h * 0.55f, w * 0.12f, h * 0.68f); lineTo(w * 0.05f, h * 0.86f) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(2 * d))
                for (k in 0 until 5) monte(w * (0.62f + k * 0.04f), h * 0.86f, 7 * d, tinta)
                // Un fondo de lazo tenue.
                with(TrofeosMas) { for (i in 0 until 6) estrellaLazo(Offset(w * (0.1f + i * 0.17f), h * 0.12f), 6 * d, Brush.linearGradient(listOf(tinta.copy(alpha = 0.12f), tinta.copy(alpha = 0.12f))), null, 0f) }
            }
            TemaId.SEDA -> {
                // Dunas del Taklamakán, los montes del Pamir y el oasis.
                val arena = Color(0xFFD9B57A).copy(alpha = if (oscuro) 0.16f else 0.22f)
                for (k in 0 until 7) { val x = w * (0.3f + k * 0.07f); drawArc(arena, 200f, 140f, true, Offset(x, h * 0.6f), Size(70 * d, 30 * d)) }
                for (k in 0 until 4) monte(w * (0.44f + k * 0.035f), h * 0.3f, 9 * d, tinta)
                drawCircle(Color(0xFF4E9A72).copy(alpha = 0.25f), 10 * d, Offset(w * 0.8f, h * 0.32f))
                // La costa del Mediterráneo al oeste.
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(w * 0.04f, 0f); cubicTo(w * 0.07f, h * 0.4f, w * 0.03f, h * 0.7f, w * 0.06f, h); lineTo(0f, h); close() }, agua)
            }
            TemaId.MONGOL -> {
                // La estepa: pasto, el Gobi al sur, los montes al norte y el gran cielo.
                drawRect(Brush.verticalGradient(listOf(Color(0xFF2E6DB4).copy(alpha = if (oscuro) 0.14f else 0.1f), Color.Transparent), 0f, h * 0.4f), Offset.Zero, Size(w, h * 0.4f))
                val az = java.util.Random(9)
                repeat(70) { val x = az.nextFloat() * w; val y = h * (0.3f + az.nextFloat() * 0.6f); drawLine(Color(0xFF6B8A3A).copy(alpha = 0.25f), Offset(x, y), Offset(x + 2 * d, y - 5 * d), 1f * d) }
                drawOval(Color(0xFFD9B57A).copy(alpha = 0.2f), Offset(w * 0.5f, h * 0.68f), Size(w * 0.35f, h * 0.3f))
                for (k in 0 until 6) monte(w * (0.1f + k * 0.06f), h * 0.2f, 8 * d, tinta)
            }
            TemaId.COLONOS -> {
                // Lomas, un río que la caravana cruza y bosquecitos.
                for (k in 0 until 4) drawArc(Color(0xFF6B8A3A).copy(alpha = 0.15f), 180f, 180f, true, Offset(w * (k * 0.27f - 0.05f), h * 0.6f), Size(w * 0.4f, h * 0.6f))
                drawPath(Path().apply { moveTo(w * 0.28f, 0f); cubicTo(w * 0.24f, h * 0.3f, w * 0.32f, h * 0.6f, w * 0.27f, h) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(4 * d))
                listOf(0.12f to 0.25f, 0.45f to 0.78f, 0.72f to 0.78f, 0.86f to 0.2f).forEach { (x, y) -> for (j in 0 until 3) drawPath(Path().apply { val cx = w * x + j * 8 * d; moveTo(cx - 4 * d, h * y); lineTo(cx, h * y - 10 * d); lineTo(cx + 4 * d, h * y); close() }, Color(0xFF3F6A3A).copy(alpha = 0.3f)) }
            }
            TemaId.BOTANICO -> {
                // Franjas de bioma: bosque, pradera, desierto, selva y manglar; se colorean con los días cumplidos.
                val biomas = listOf(Color(0xFF6B8A3A), Color(0xFFB5B85A), Color(0xFFD9B57A), Color(0xFF2E6B3A), Color(0xFF5A8A7A), Color(0xFF8AB07A))
                biomas.forEachIndexed { i, c ->
                    val x0 = w * i / biomas.size
                    val lleno = (i + 0.5f) / biomas.size <= avance
                    drawRect(c.copy(alpha = if (lleno) (if (oscuro) 0.22f else 0.2f) else 0.07f), Offset(x0, 0f), Size(w / biomas.size, h))
                }
                // Hojitas dibujadas a tinta sepia.
                val az = java.util.Random(14)
                repeat(14) { val o = Offset(az.nextFloat() * w, az.nextFloat() * h); drawPath(Path().apply { moveTo(o.x, o.y); quadraticTo(o.x + 6 * d, o.y - 6 * d, o.x + 12 * d, o.y); quadraticTo(o.x + 6 * d, o.y + 4 * d, o.x, o.y); close() }, Color(0xFF6E5530).copy(alpha = 0.18f)) }
            }
            TemaId.NAVIDAD -> {
                // Lomas nevadas, pinos, y la nieve cayendo.
                drawRect(Brush.verticalGradient(listOf(Color(0xFF1F3A6E).copy(alpha = if (oscuro) 0.25f else 0.1f), Color.Transparent), 0f, h * 0.5f), Offset.Zero, Size(w, h * 0.5f))
                for (k in 0 until 4) drawArc(Color.White.copy(alpha = if (oscuro) 0.12f else 0.6f), 180f, 180f, true, Offset(w * (k * 0.28f - 0.06f), h * 0.66f), Size(w * 0.42f, h * 0.6f))
                val az = java.util.Random(25)
                repeat(10) { val x = az.nextFloat() * w; val y = h * (0.15f + az.nextFloat() * 0.7f); drawPath(Path().apply { moveTo(x - 5 * d, y); lineTo(x, y - 14 * d); lineTo(x + 5 * d, y); close() }, Color(0xFF1F6B3A).copy(alpha = 0.3f)) }
                repeat(40) { drawCircle(Color.White.copy(alpha = if (oscuro) 0.35f else 0.8f), (0.8f + az.nextFloat() * 1.4f) * d, Offset(az.nextFloat() * w, az.nextFloat() * h)) }
            }
            TemaId.ANTIGUAS -> {
                // Pergamino con el bosque viejo, la cordillera, la boca de la mina y el río del vado.
                drawRect(Color(0xFFE9DCC0).copy(alpha = if (oscuro) 0.05f else 0.25f), Offset.Zero, size)
                val az = java.util.Random(31)
                repeat(22) { val c = Offset(w * (0.14f + az.nextFloat() * 0.18f), h * (0.32f + az.nextFloat() * 0.3f)); drawCircle(Color(0xFF3F6A3A).copy(alpha = 0.22f), (4 + az.nextFloat() * 3) * d, c) }
                for (k in 0 until 7) monte(w * (0.42f + k * 0.035f), h * (0.36f + (k % 2) * 0.06f), 10 * d, tinta.copy(alpha = 0.4f))
                drawArc(Color(0xFF2A2420).copy(alpha = 0.35f), 180f, 180f, true, Offset(w * 0.6f, h * 0.5f), Size(16 * d, 16 * d))
                drawPath(Path().apply { moveTo(w * 0.3f, 0f); cubicTo(w * 0.34f, h * 0.4f, w * 0.38f, h * 0.7f, w * 0.34f, h) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(3 * d))
            }
            TemaId.DISTOPIA -> {
                // Pisos de un edificio de oficinas en corte, con sus ventanillas; a la derecha, la torre del Ministerio.
                val linea = cs.onSurface.copy(alpha = if (oscuro) 0.18f else 0.14f)
                for (k in 1 until 6) { val y = h * k / 6f; drawLine(linea, Offset(0f, y), Offset(w * 0.8f, y), 1f * d) }
                val az = java.util.Random(17)
                for (k in 0 until 6) for (j in 0 until 10) if (az.nextInt(3) > 0) drawRect(linea, Offset(w * (0.03f + j * 0.075f), h * (k / 6f + 0.04f)), Size(w * 0.035f, h * 0.07f))
                drawRect(cs.onSurface.copy(alpha = if (oscuro) 0.22f else 0.16f), Offset(w * 0.82f, h * 0.04f), Size(w * 0.14f, h * 0.96f))
                for (k in 0 until 9) drawRect(Color(0xFFC8102E).copy(alpha = 0.25f + 0.5f * avance), Offset(w * 0.86f, h * (0.12f + k * 0.09f)), Size(w * 0.06f, h * 0.03f))
                drawRect(Color(0xFFC8102E).copy(alpha = 0.6f), Offset(w * 0.82f, h * 0.04f), Size(w * 0.14f, h * 0.02f))
            }
            TemaId.CIBER -> {
                // La red a oscuras: rejilla de puntos, servidores sueltos unidos por enlaces tenues y un volcado en hexadecimal a la derecha.
                val cian = Color(0xFF00E5FF)
                var y = 6 * d; while (y < h) { var x = 6 * d; while (x < w) { drawCircle(cian.copy(alpha = 0.09f), 0.8f * d, Offset(x, y)); x += 12 * d }; y += 12 * d }
                val az = java.util.Random(23)
                val nodos = List(12) { Offset(w * (0.04f + 0.8f * az.nextFloat()), h * (0.08f + 0.84f * az.nextFloat())) }
                nodos.forEachIndexed { i, a -> drawLine(cian.copy(alpha = 0.1f), a, nodos[(i * 5 + 3) % nodos.size], 0.8f * d) }
                nodos.forEach { drawRect(cian.copy(alpha = 0.18f), Offset(it.x - 4 * d, it.y - 3 * d), Size(8 * d, 6 * d), style = Stroke(0.8f * d)) }
                val hex = "0123456789ABCDEF"
                with(TrofeosMas) {
                    var yy = 10 * d; while (yy < h) {
                        val t = (0 until 4).joinToString(" ") { "" + hex[az.nextInt(16)] + hex[az.nextInt(16)] }
                        mono(t, w - 4 * d, yy, 7 * d, cian.copy(alpha = 0.06f + 0.14f * avance), android.graphics.Paint.Align.RIGHT); yy += 9 * d
                    }
                }
            }
            else -> {}
        }
    }

    // ================================================================== rutas

    fun DrawScope.rutaMas(tema: TemaId, cs: ColorScheme, p: Path) {
        val d = density
        when (tema) {
            // Doble filete de oro con puntos de lazo.
            TemaId.ANDALUS -> { drawPath(p, Color(0xFFC9A04A).copy(alpha = 0.7f), style = Stroke(4 * d)); drawPath(p, cs.surface, style = Stroke(2 * d)); drawPath(p, Color(0xFF1E6B5A), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * d, 5 * d)))) }
            // La huella de la caravana en la arena: puntos dobles.
            TemaId.SEDA -> { drawPath(p, Color(0xFFD9B57A).copy(alpha = 0.6f), style = Stroke(5 * d)); drawPath(p, Color(0xFF8E1B1B).copy(alpha = 0.8f), style = Stroke(1.6f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 5 * d)))) }
            // El camino de las postas, en azul de cielo.
            TemaId.MONGOL -> drawPath(p, Color(0xFF2E6DB4).copy(alpha = 0.8f), style = Stroke(2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8 * d, 4 * d))))
            // Las rodadas de las carretas: dos líneas paralelas.
            TemaId.COLONOS -> { drawPath(p, Color(0xFF8A5A2B).copy(alpha = 0.5f), style = Stroke(6 * d)); drawPath(p, cs.surface.copy(alpha = 0.8f), style = Stroke(3 * d)) }
            // Un trazo de lápiz de naturalista.
            TemaId.BOTANICO -> drawPath(p, Color(0xFF6E5530).copy(alpha = 0.7f), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * d, 2 * d, 1 * d, 2 * d))))
            // Rayado de bastón de caramelo.
            TemaId.NAVIDAD -> { drawPath(p, Color.White, style = Stroke(5 * d)); drawPath(p, Color(0xFFB3122E), style = Stroke(5 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * d, 5 * d)))) }
            // La línea roja del mapa de la travesía.
            TemaId.ANTIGUAS -> drawPath(p, Color(0xFF7A1A12).copy(alpha = 0.8f), style = Stroke(1.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 3 * d))))
            // La línea roja del trámite, como cinta de señalización.
            TemaId.DISTOPIA -> { drawPath(p, Color(0xFF141414).copy(alpha = 0.35f), style = Stroke(4 * d)); drawPath(p, Color(0xFFC8102E), style = Stroke(2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * d, 4 * d)))) }
            // El enlace de la traza: la línea cian y los paquetes que viajan de nodo en nodo.
            TemaId.CIBER -> {
                val cian = Color(0xFF00E5FF)
                drawPath(p, cian.copy(alpha = 0.16f), style = Stroke(6 * d))
                drawPath(p, cian.copy(alpha = 0.75f), style = Stroke(1 * d))
                aLoLargo(p, 15 * d) { o -> drawRect(cian, Offset(o.x - 1.6f * d, o.y - 1.6f * d), Size(3.2f * d, 3.2f * d)) }
            }
            else -> drawPath(p, cs.outline, style = Stroke(2 * d))
        }
    }

    // ================================================================== paradas

    fun DrawScope.etapaMas(tema: TemaId, cs: ColorScheme, c: Offset, r: Float, col: Color, meta: Boolean) {
        val d = density
        when (tema) {
            TemaId.ANDALUS -> with(TrofeosMas) { estrellaLazo(c, r * 1.2f, Brush.linearGradient(listOf(col, col)), Color(0xFFC9A04A), 0.8f * d) }
            // Una moneda de cobre con agujero cuadrado.
            TemaId.SEDA -> { drawCircle(col, r, c); drawRect(cs.surface, Offset(c.x - r * 0.3f, c.y - r * 0.3f), Size(r * 0.6f, r * 0.6f)); drawCircle(Color(0xFF8E6A1F), r, c, style = Stroke(0.8f * d)) }
            // Una yurta.
            TemaId.MONGOL -> { drawArc(col, 180f, 180f, true, Offset(c.x - r, c.y - r * 0.9f), Size(2 * r, r * 1.4f)); drawRect(col, Offset(c.x - r, c.y - r * 0.2f), Size(2 * r, r * 0.9f)); drawRect(Color(0xFFD9772A), Offset(c.x - r * 0.25f, c.y + r * 0.1f), Size(r * 0.5f, r * 0.6f)) }
            // Una cabaña con su techo.
            TemaId.COLONOS -> { drawRect(col, Offset(c.x - r * 0.8f, c.y - r * 0.2f), Size(r * 1.6f, r * 1.1f)); drawPath(Path().apply { moveTo(c.x - r, c.y - r * 0.2f); lineTo(c.x, c.y - r * 1.1f); lineTo(c.x + r, c.y - r * 0.2f); close() }, lerp(col, Color.Black, 0.25f)) }
            // Una hoja.
            TemaId.BOTANICO -> drawPath(Path().apply { moveTo(c.x - r * 1.1f, c.y + r * 0.4f); quadraticTo(c.x - r * 0.6f, c.y - r * 1.2f, c.x + r * 1.1f, c.y - r * 0.6f); quadraticTo(c.x + r * 0.4f, c.y + r * 1.0f, c.x - r * 1.1f, c.y + r * 0.4f); close() }, col)
            // El puesto del mercado, con su toldo rayado.
            TemaId.NAVIDAD -> { drawRect(col, Offset(c.x - r * 0.8f, c.y - r * 0.2f), Size(r * 1.6f, r * 1.0f)); for (k in 0 until 4) drawRect(if (k % 2 == 0) Color(0xFFB3122E) else Color.White, Offset(c.x - r + k * r * 0.5f, c.y - r * 0.8f), Size(r * 0.5f, r * 0.6f)) }
            // Una piedra rúnica.
            TemaId.ANTIGUAS -> {
                drawPath(Path().apply { moveTo(c.x - r * 0.7f, c.y + r); lineTo(c.x - r * 0.8f, c.y - r * 0.4f); quadraticTo(c.x, c.y - r * 1.4f, c.x + r * 0.8f, c.y - r * 0.4f); lineTo(c.x + r * 0.7f, c.y + r); close() }, col)
                with(TrofeosMas) { runas(Offset(c.x - r * 0.3f, c.y), r * 0.6f, r * 0.8f, Color.Black.copy(alpha = 0.35f), 0.8f * d, 1, (c.x + c.y).toInt()) }
            }
            // Una ventanilla de control: caseta cuadrada con su número.
            TemaId.DISTOPIA -> { drawRect(col, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r)); drawRect(Color(0xFF141414).copy(alpha = 0.6f), Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(0.8f * d)); drawRect(Color(0xFFF2EFE6).copy(alpha = 0.8f), Offset(c.x - r * 0.5f, c.y - r * 0.6f), Size(r, r * 0.5f)) }
            // Un servidor: caja negra con su filo, dos ranuras y la luz.
            TemaId.CIBER -> {
                drawRect(Color(0xFF05070B), Offset(c.x - r, c.y - r * 0.8f), Size(2 * r, 1.6f * r))
                drawRect(col, Offset(c.x - r, c.y - r * 0.8f), Size(2 * r, 1.6f * r), style = Stroke(1.2f * d))
                for (k in 0 until 2) drawLine(col, Offset(c.x - r * 0.6f, c.y - r * 0.3f + k * r * 0.55f), Offset(c.x + r * 0.15f, c.y - r * 0.3f + k * r * 0.55f), 0.9f * d)
                drawCircle(col, r * 0.16f, Offset(c.x + r * 0.55f, c.y - r * 0.3f))
                if (meta) drawRect(col.copy(alpha = 0.4f), Offset(c.x - r * 1.35f, c.y - r * 1.15f), Size(2.7f * r, 2.3f * r), style = Stroke(0.8f * d))
            }
            else -> drawCircle(col, r, c)
        }
    }

    fun DrawScope.victoriaMas(tema: TemaId, cs: ColorScheme, m: Offset) {
        val d = density
        when (tema) {
            // Un arco de herradura sobre la meta.
            TemaId.ANDALUS -> drawArc(Color(0xFFC9A04A), 150f, 240f, false, Offset(m.x - 14 * d, m.y - 30 * d), Size(28 * d, 28 * d), style = Stroke(3 * d))
            // Un farolillo rojo.
            TemaId.SEDA -> { drawLine(cs.onSurface, m, m + Offset(0f, -20 * d), 1f * d); drawOval(Color(0xFFC0392B), Offset(m.x - 6 * d, m.y - 32 * d), Size(12 * d, 14 * d)) }
            // La bufanda azul en su vara.
            TemaId.MONGOL -> { drawLine(cs.onSurface, m, m + Offset(0f, -22 * d), 1f * d); drawPath(Path().apply { moveTo(m.x, m.y - 22 * d); cubicTo(m.x + 8 * d, m.y - 26 * d, m.x + 12 * d, m.y - 16 * d, m.x + 20 * d, m.y - 20 * d); lineTo(m.x + 18 * d, m.y - 14 * d); cubicTo(m.x + 10 * d, m.y - 10 * d, m.x + 6 * d, m.y - 18 * d, m.x, m.y - 16 * d); close() }, Color(0xFF2E6DB4)) }
            // La bandera del pueblo nuevo.
            TemaId.COLONOS -> { drawLine(cs.onSurface, m, m + Offset(0f, -22 * d), 1f * d); drawRect(Color(0xFF1F3A6E), Offset(m.x, m.y - 22 * d), Size(12 * d, 8 * d)) }
            // Una flor que se abre.
            TemaId.BOTANICO -> { for (k in 0 until 6) { val a = k * PI / 3; drawCircle(Color(0xFFC86A7A), 4 * d, Offset(m.x + (7 * d * cos(a)).toFloat(), m.y - 20 * d + (7 * d * sin(a)).toFloat())) }; drawCircle(Color(0xFFE2B34A), 3 * d, Offset(m.x, m.y - 20 * d)) }
            // El árbol de Navidad con su estrella.
            TemaId.NAVIDAD -> {
                drawPath(Path().apply { moveTo(m.x - 12 * d, m.y - 8 * d); lineTo(m.x, m.y - 34 * d); lineTo(m.x + 12 * d, m.y - 8 * d); close() }, Color(0xFF1F6B3A))
                with(TrofeosTema) { estrella(Offset(m.x, m.y - 36 * d), 5 * d, 2 * d, 5, Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFD4AF37)))) }
            }
            // El estandarte en lo alto de la fortaleza.
            TemaId.ANTIGUAS -> { drawLine(cs.onSurface, m, m + Offset(0f, -24 * d), 1f * d); drawPath(Path().apply { moveTo(m.x, m.y - 24 * d); lineTo(m.x + 14 * d, m.y - 24 * d); lineTo(m.x + 14 * d, m.y - 12 * d); lineTo(m.x + 7 * d, m.y - 15 * d); lineTo(m.x, m.y - 12 * d); close() }, Color(0xFF1F3A5A)) }
            // El sello rojo de «aprobado» sobre el Ministerio.
            TemaId.DISTOPIA -> { drawRect(Color(0xFFC8102E), Offset(m.x - 14 * d, m.y - 34 * d), Size(28 * d, 14 * d), style = Stroke(2 * d)); drawLine(Color(0xFFC8102E), Offset(m.x - 10 * d, m.y - 27 * d), Offset(m.x + 10 * d, m.y - 27 * d), 2 * d) }
            // «ACCESO CONCEDIDO» en un recuadro de consola sobre la raíz.
            TemaId.CIBER -> with(TrofeosMas) {
                val cian = Color(0xFF00E5FF); val t = "ACCESO CONCEDIDO"; val tam = 8 * d
                val tw = t.length * tam * 0.6f; val x = (m.x - tw / 2).coerceIn(3 * d, size.width - tw - 3 * d); val y = m.y - 30 * d
                drawRect(Color(0xFF05070B), Offset(x - 3 * d, y - tam), Size(tw + 6 * d, tam * 1.5f))
                drawRect(cian, Offset(x - 3 * d, y - tam), Size(tw + 6 * d, tam * 1.5f), style = Stroke(1f * d))
                mono(t, x, y, tam, cian, negrita = true)
            }
            else -> {}
        }
    }

    // ================================================================== el marcador que avanza

    /**
     * El marcador de la campaña: un alfiler con el emblema del tema, sobre la parada a la que llegaste.
     * [salto] lo hace brincar un poco mientras hoy falta cumplir.
     */
    fun DrawScope.marcador(tema: TemaId, cs: ColorScheme, c: Offset, salto: Float = 0f) {
        val d = density
        val base = Offset(c.x, c.y - 10 * d - salto * 3 * d)
        val color = cs.tertiary
        drawOval(Color.Black.copy(alpha = 0.18f), Offset(c.x - 5 * d, c.y - 2 * d), Size(10 * d, 4 * d))
        // Distopía: no un alfiler, sino el banderín rojo del punto de control.
        if (tema == TemaId.DISTOPIA) {
            val top = c.y - 24 * d - salto * 3 * d
            drawLine(Color(0xFF141414), Offset(c.x, c.y), Offset(c.x, top), 1.6f * d)
            drawRect(Color(0xFFC8102E), Offset(c.x, top), Size(13 * d, 9 * d))
            drawRect(Color(0xFFF2EFE6), Offset(c.x + 2 * d, top + 3.5f * d), Size(9 * d, 2 * d))
            return
        }
        drawPath(Path().apply { moveTo(base.x, base.y + 9 * d); cubicTo(base.x - 9 * d, base.y - 2 * d, base.x - 7 * d, base.y - 13 * d, base.x, base.y - 13 * d); cubicTo(base.x + 7 * d, base.y - 13 * d, base.x + 9 * d, base.y - 2 * d, base.x, base.y + 9 * d); close() }, color)
        val g = Offset(base.x, base.y - 5 * d); val r = 4.2f * d
        val tinta = if (color.luminance() > 0.5f) Color(0xFF2A1E0E) else Color.White
        drawCircle(tinta.copy(alpha = 0.25f), r + 0.8f * d, g)
        when (tema) {
            // Gorro frigio, bicornio, vela, venera… un emblema chico de cada tema.
            TemaId.REVOLUCION -> drawPath(Path().apply { moveTo(g.x - r * 0.7f, g.y + r * 0.6f); cubicTo(g.x - r * 0.8f, g.y - r * 0.4f, g.x, g.y - r, g.x + r * 0.7f, g.y - r * 0.4f); lineTo(g.x + r * 0.5f, g.y); lineTo(g.x + r * 0.7f, g.y + r * 0.6f); close() }, Color(0xFFB22234))
            TemaId.NAPOLEON -> drawPath(Path().apply { moveTo(g.x - r, g.y + r * 0.3f); quadraticTo(g.x, g.y - r * 1.2f, g.x + r, g.y + r * 0.3f); close() }, Color(0xFF1A1A22))
            TemaId.HELENO -> drawPath(Path().apply { moveTo(g.x - r * 0.6f, g.y - r * 0.6f); lineTo(g.x + r * 0.6f, g.y - r * 0.6f); lineTo(g.x + r * 0.7f, g.y + r * 0.4f); lineTo(g.x - r * 0.7f, g.y + r * 0.4f); close() }, Color.White)
            TemaId.SEDA -> drawOval(Color(0xFFC0392B), Offset(g.x - r * 0.6f, g.y - r * 0.8f), Size(r * 1.2f, r * 1.6f))
            TemaId.MONGOL -> drawRoundRect(Color(0xFFD4AF37), Offset(g.x - r * 0.45f, g.y - r * 0.9f), Size(r * 0.9f, r * 1.8f), androidx.compose.ui.geometry.CornerRadius(r * 0.45f))
            TemaId.NAVIDAD -> with(TrofeosTema) { estrella(g, r * 0.9f, r * 0.38f, 5, Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFD4AF37)))) }
            TemaId.ANDALUS -> with(TrofeosMas) { estrellaLazo(g, r * 0.85f, Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFD4AF37))), null, 0f) }
            TemaId.BOTANICO -> drawPath(Path().apply { moveTo(g.x - r * 0.8f, g.y + r * 0.4f); quadraticTo(g.x - r * 0.4f, g.y - r, g.x + r * 0.8f, g.y - r * 0.5f); quadraticTo(g.x + r * 0.3f, g.y + r * 0.8f, g.x - r * 0.8f, g.y + r * 0.4f); close() }, Color(0xFF3F7A3A))
            TemaId.COLONOS -> { drawArc(Color(0xFFF1E6C8), 180f, 180f, true, Offset(g.x - r * 0.8f, g.y - r * 0.8f), Size(r * 1.6f, r * 1.4f)); drawCircle(Color(0xFF5A3A22), r * 0.3f, Offset(g.x - r * 0.4f, g.y + r * 0.4f)); drawCircle(Color(0xFF5A3A22), r * 0.3f, Offset(g.x + r * 0.4f, g.y + r * 0.4f)) }
            TemaId.ANTIGUAS -> with(TrofeosMas) { runas(Offset(g.x - r * 0.3f, g.y), r * 0.6f, r * 1.2f, tinta, 1f * d, 1, 4) }
            TemaId.DISTOPIA -> { drawRect(Color(0xFFC8102E), Offset(g.x - r * 0.7f, g.y - r * 0.7f), Size(r * 1.4f, r * 1.4f)); drawLine(Color(0xFFF2EFE6), Offset(g.x - r * 0.45f, g.y), Offset(g.x + r * 0.45f, g.y), 1.2f * d) }
            // El cursor de la consola.
            TemaId.CIBER -> { drawCircle(Color(0xFF05070B), r * 0.95f, g); drawRect(Color(0xFF00E5FF), Offset(g.x - r * 0.32f, g.y - r * 0.55f), Size(r * 0.64f, r * 1.1f)) }
            else -> drawCircle(tinta, r * 0.45f, g)
        }
    }
}
