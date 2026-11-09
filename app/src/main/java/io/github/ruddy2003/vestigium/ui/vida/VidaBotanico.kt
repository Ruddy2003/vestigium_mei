package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import java.util.Random
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/**
 * Botánico: Vida en el invernadero del naturalista, con los materiales de su gabinete de roble y de su caja de Ward: roble
 * con veta y nudos, hierro colado pintado de verde, vidrio con sus parteluces, latón, barro cocido, baldosas hidráulicas,
 * cuero verde con filete dorado, papel de herbario, helechos, palmas y musgo. De noche, el invernadero en penumbra, con la
 * madera oscura y luciérnagas.
 * - Social: la mesa del té en el invernadero, vista desde arriba. El piso de baldosas hidráulicas de barro y crema con las
 *   rejillas de la calefacción, las cuatro macetas grandes de las esquinas (dos palmas y dos helechos), las sillas de jardín
 *   de hierro colado con su cojín, y la mesa de roble con el mantel de lino de orilla festoneada (el grafo va en el mantel).
 * - Mapas: el escritorio del naturalista, en perspectiva, contra el ventanal del invernadero con sus plantas y un helecho
 *   colgante. Sobre el roble, el secante de cuero verde con esquineras y filete dorado (el mapa va encima); el microscopio de
 *   latón, la caja de herborizar con un helecho asomando, los libros con una maceta y el tintero con su plumilla. El canto
 *   moldurado con el cajón, su tirador de asa y su bocallave; las patas torneadas y el piso de barro.
 * - Otras vidas: el gabinete de herbario. Encima, la caja de Ward con su helecho, las macetas, los libros y la lupa; la
 *   cornisa con dentículos, el friso con cinco portaetiquetas de latón, las pilastras talladas con un helecho, cinco
 *   compartimentos forrados de papel secante y el zócalo de cajoncitos sobre sus pies de bollo.
 * - Íconos: la casa de las palmas de hierro y vidrio, el pliego de herbario con la capa dorada que se raspa con un penique,
 *   y el plano con alfileres de entomólogo y un escarabajo.
 * - Notas: el tablero de pliegos en su marco de roble moldurado, con el frontón tallado de helechos y su placa oval, los
 *   remates de bellota, las escuadras de hierro y la repisa con el lápiz, la lupa y un plantón. Las notas son papeles del
 *   naturalista: el pliego con su doble filete y una ramita (idea), el sobre de semillas (pensamiento), la etiqueta de
 *   herbario con su banda verde (propósito) y la etiqueta de cartón con su ojal y su cordel (recordatorio). Las fijadas
 *   llevan una flor prensada y un alfiler de cabeza roja; las demás, un alfiler de entomólogo.
 */
internal object VidaBotanico : DisenoVida {
    private val ROBLE = Color(0xFF8A6A42)
    private val ROBLE_LUZ = Color(0xFFA8865A)
    private val ROBLE_OSC = Color(0xFF5E4528)
    private val VETA = Color(0xFF3E2C18)
    private val HIERRO = Color(0xFF24382C)
    private val HIERRO_LUZ = Color(0xFF4A6553)
    private val LATON = Color(0xFFC9A659)
    private val LATON_OSC = Color(0xFF7E6128)
    private val VIDRIO = Color(0xFFDDEDE2)
    private val PAPEL = Color(0xFFF1EAD6)
    private val KRAFT = Color(0xFFD9C29A)
    private val LINO = Color(0xFFEDE5CC)
    private val SECANTE = Color(0xFFE4D8BC)
    private val TINTA = Color(0xFF2A2318)
    private val HELECHO = Color(0xFF4F7D3E)
    private val HOJA_CLARA = Color(0xFF86A95B)
    private val MUSGO = Color(0xFF6B8A3F)
    private val PALMA = Color(0xFF3F6A3A)
    private val TERRACOTA = Color(0xFFB0603A)
    private val BARRO_OSC = Color(0xFF7E3F24)
    private val CREMA = Color(0xFFE8DCBC)
    private val TIERRA = Color(0xFF4A3526)
    private val CUERO_VERDE = Color(0xFF3E5A3A)
    private val TELA = Color(0xFF4E6A3A)
    private val FLOR = Color(0xFF9A5A8A)
    private val ROJO = Color(0xFFA8322A)
    private val PAPEL_PARED = Color(0xFFDCD6BC)
    private val LUCIERNAGA = Color(0xFFE8F27A)
    private val NOCHE = Color(0xFF0D120E)

    /** Las telas de los libros: verde musgo, castaño, rojo óxido y verde mar. */
    private val LIBROS = listOf(Color(0xFF3E5E3A), Color(0xFF6A4A2E), Color(0xFF8A3E2A), Color(0xFF2F4F45))

    private fun DrawScope.u() = size.minDimension / 100f

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en oscuro: el invernadero en penumbra, la madera de noche. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.44f) else c

    /** El fondo del nicho: el material apenas mezclado con el fondo del esquema, para que se lea lo de encima. */
    private fun nicho(fondo: Color, material: Color, oscuro: Boolean): Color = mezcla(fondo, material, if (oscuro) 0.16f else 0.32f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.18f, 0.23f, 0.82f, 0.79f)
        MuebleVida.MAPAS -> Rect(0.22f, 0.27f, 0.78f, 0.79f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.058f, 0.37f, 0.942f, 0.84f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.18f, 0.925f, 0.875f)
    }

    // ---------------------------------------------------------------- Ayudas de dibujo

    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float, radio: Float = 0f) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.3f), CornerRadius(radio + e))
        }
    }

    private fun DrawScope.bisel(r: Rect, luz: Color, sombra: Color, ancho: Float) {
        drawRect(luz, r.topLeft, Size(r.width, ancho))
        drawRect(luz.copy(alpha = luz.alpha * 0.7f), r.topLeft, Size(ancho, r.height))
        drawRect(sombra, Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(sombra.copy(alpha = sombra.alpha * 0.8f), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    /** La veta de la madera: líneas onduladas a lo largo de la tabla, de grosor y fuerza variables. */
    private fun DrawScope.vetas(r: Rect, color: Color, rnd: Random, paso: Float, vertical: Boolean = false, grosor: Float = 1f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / paso).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + rnd.nextFloat()) / n
                val amp = paso * (0.15f + 0.5f * rnd.nextFloat())
                val fase = rnd.nextFloat() * 6.28f
                val ondas = 0.8f + 1.8f * rnd.nextFloat()
                val p = Path()
                for (s in 0..16) {
                    val a = largo * s / 16f
                    val b = base + amp * sin(fase + ondas * 6.28f * s / 16f)
                    if (vertical) { if (s == 0) p.moveTo(r.left + b, r.top + a) else p.lineTo(r.left + b, r.top + a) } else { if (s == 0) p.moveTo(r.left + a, r.top + b) else p.lineTo(r.left + a, r.top + b) }
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.35f + 0.65f * rnd.nextFloat())), style = Stroke(grosor * (0.5f + rnd.nextFloat())))
            }
        }
    }

    /** Un nudo de la madera: óvalos concéntricos. */
    private fun DrawScope.nudo(c: Offset, rx: Float, ry: Float, color: Color, grosor: Float) {
        for (k in 0 until 3) {
            val f = 1f - k * 0.3f
            drawOval(color.copy(alpha = color.alpha * (0.5f + k * 0.2f)), Offset(c.x - rx * f, c.y - ry * f), Size(rx * 2 * f, ry * 2 * f), style = Stroke(grosor))
        }
        drawOval(color, Offset(c.x - rx * 0.2f, c.y - ry * 0.2f), Size(rx * 0.4f, ry * 0.4f))
    }

    /** Una tabla de roble: el degradado de luz, la veta y, si se pide, un nudo. */
    private fun DrawScope.madera(r: Rect, base: Color, oscuro: Boolean, rnd: Random, u: Float, vertical: Boolean = false, nudos: Int = 0) {
        val b = t(base, oscuro)
        if (vertical) drawRect(Brush.horizontalGradient(listOf(mezcla(b, Color.White, 0.08f), b, mezcla(b, Color.Black, 0.12f)), r.left, r.right), r.topLeft, r.size)
        else drawRect(Brush.verticalGradient(listOf(mezcla(b, Color.White, 0.08f), b, mezcla(b, Color.Black, 0.12f)), r.top, r.bottom), r.topLeft, r.size)
        vetas(r, t(VETA, oscuro).copy(alpha = 0.45f), rnd, 1.6f * u, vertical, 0.3f * u)
        repeat(nudos) {
            val c = Offset(r.left + r.width * (0.15f + 0.7f * rnd.nextFloat()), r.top + r.height * (0.2f + 0.6f * rnd.nextFloat()))
            if (vertical) nudo(c, 0.9f * u, 2.2f * u, t(VETA, oscuro).copy(alpha = 0.5f), 0.2f * u) else nudo(c, 2.2f * u, 0.9f * u, t(VETA, oscuro).copy(alpha = 0.5f), 0.2f * u)
        }
    }

    /** Una moldura horizontal de perfil: filete de luz, gola en sombra, junquillo y filete bajo. */
    private fun DrawScope.moldura(r: Rect, base: Color, oscuro: Boolean) {
        val b = t(base, oscuro)
        val luz = mezcla(b, Color.White, if (oscuro) 0.1f else 0.25f)
        val som = mezcla(b, Color.Black, 0.35f)
        drawRect(Brush.verticalGradient(0f to luz, 0.18f to b, 0.45f to som, 0.55f to luz, 0.72f to b, 0.9f to som, 1f to mezcla(som, Color.Black, 0.3f), startY = r.top, endY = r.bottom), r.topLeft, r.size)
    }

    /** Un pomo de latón con su sombra y su brillo. */
    private fun DrawScope.pomo(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + r * 0.25f, c.y + r * 0.35f))
        drawCircle(Brush.radialGradient(listOf(mezcla(LATON, Color.White, 0.45f), t(LATON, oscuro), t(LATON_OSC, oscuro)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.4f), r, c)
    }

    /** Una fronda de helecho: el raquis curvo y sus pinnas a los dos lados, que se achican hacia la punta. */
    private fun DrawScope.fronda(base: Offset, largo: Float, angulo: Float, curva: Float, color: Color, grosor: Float, pasos: Int = 11) {
        var p = base
        var a = Math.toRadians(angulo.toDouble())
        val da = Math.toRadians((curva / pasos).toDouble())
        for (k in 0 until pasos) {
            val f = 1f - k / pasos.toFloat()
            val l = largo / pasos
            val q = Offset(p.x + l * cos(a).toFloat(), p.y + l * sin(a).toFloat())
            drawLine(color, p, q, grosor * (0.45f + 0.55f * f), StrokeCap.Round)
            val pinna = largo * 0.2f * (0.35f + 0.65f * f) * (if (k == 0) 0.6f else 1f)
            listOf(-1.05, 1.05).forEach { s ->
                val b = a + s
                drawLine(color, q, Offset(q.x + pinna * cos(b).toFloat(), q.y + pinna * sin(b).toFloat()), grosor * 1.5f * f + grosor * 0.3f, StrokeCap.Round)
            }
            p = q; a += da
        }
    }

    /** Una hoja entera con su nervio: de la base [b] hacia [angulo] grados. */
    private fun DrawScope.hoja(b: Offset, largo: Float, ancho: Float, angulo: Float, color: Color, nervio: Color? = null) {
        rotate(angulo, b) {
            val p = Path().apply {
                moveTo(b.x, b.y)
                quadraticTo(b.x + largo * 0.35f, b.y - ancho, b.x + largo, b.y)
                quadraticTo(b.x + largo * 0.35f, b.y + ancho, b.x, b.y)
                close()
            }
            drawPath(p, color)
            if (nervio != null) drawLine(nervio, b, Offset(b.x + largo * 0.9f, b.y), ancho * 0.12f)
        }
    }

    /** Una palma vista desde arriba: hojas pinnadas que salen del centro. */
    private fun DrawScope.palmaArriba(c: Offset, r: Float, color: Color, luz: Color, rnd: Random) {
        val n = 8
        for (k in 0 until n) {
            val a = k * 360f / n + rnd.nextFloat() * 20f
            val largo = r * (0.85f + 0.3f * rnd.nextFloat())
            val rad = Math.toRadians(a.toDouble())
            val fin = Offset(c.x + largo * cos(rad).toFloat(), c.y + largo * sin(rad).toFloat())
            val pasos = 9
            for (s in 1..pasos) {
                val f = s / pasos.toFloat()
                val q = Offset(c.x + (fin.x - c.x) * f, c.y + (fin.y - c.y) * f)
                val l = largo * 0.22f * (1f - f * 0.6f)
                listOf(-1.2, 1.2).forEach { d ->
                    val b = rad + d
                    drawLine(if (s % 2 == 0) color else luz, q, Offset(q.x + l * cos(b).toFloat(), q.y + l * sin(b).toFloat()), largo * 0.05f, StrokeCap.Round)
                }
            }
            drawLine(mezcla(color, Color.Black, 0.3f), c, fin, largo * 0.025f)
        }
    }

    /** Una luciérnaga: el punto de luz con su halo. */
    private fun DrawScope.luciernaga(o: Offset, r: Float) {
        drawCircle(Brush.radialGradient(listOf(LUCIERNAGA.copy(alpha = 0.45f), Color.Transparent), o, r * 5f), r * 5f, o)
        drawCircle(LUCIERNAGA, r, o)
    }

    /** Una maceta de barro de frente: el cuerpo cónico, el borde y la tierra, con su sombra. */
    private fun DrawScope.maceta(base: Offset, ancho: Float, alto: Float, oscuro: Boolean) {
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(base.x - ancho * 0.55f, base.y - alto * 0.06f), Size(ancho * 1.1f, alto * 0.14f))
        val cuerpo = Path().apply {
            moveTo(base.x - ancho * 0.42f, base.y - alto * 0.78f); lineTo(base.x + ancho * 0.42f, base.y - alto * 0.78f)
            lineTo(base.x + ancho * 0.32f, base.y); lineTo(base.x - ancho * 0.32f, base.y); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(t(mezcla(TERRACOTA, Color.White, 0.15f), oscuro), t(TERRACOTA, oscuro), t(BARRO_OSC, oscuro)), base.x - ancho * 0.42f, base.x + ancho * 0.42f))
        val borde = Rect(base.x - ancho / 2, base.y - alto, base.x + ancho / 2, base.y - alto * 0.76f)
        drawRect(Brush.horizontalGradient(listOf(t(mezcla(TERRACOTA, Color.White, 0.2f), oscuro), t(TERRACOTA, oscuro), t(BARRO_OSC, oscuro)), borde.left, borde.right), borde.topLeft, borde.size)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(borde.left, borde.bottom - alto * 0.03f), Size(borde.width, alto * 0.03f))
        drawOval(t(TIERRA, oscuro), Offset(borde.left + ancho * 0.06f, borde.top - alto * 0.05f), Size(ancho * 0.88f, alto * 0.1f))
    }

    /** Un alfiler: la cabeza redonda con su brillo y su sombra larga. */
    private fun DrawScope.alfiler(c: Offset, r: Float, color: Color) {
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x + r * 0.4f, c.y + r * 0.6f), Offset(c.x + r * 2.6f, c.y + r * 3.2f), r * 0.35f, StrokeCap.Round)
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + r * 0.3f, c.y + r * 0.4f))
        drawCircle(Brush.radialGradient(listOf(mezcla(color, Color.White, 0.5f), color, mezcla(color, Color.Black, 0.4f)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.5f), r, c)
    }

    /** Un escarabajo visto desde arriba: élitros de verde metálico, protórax, cabeza y patas. */
    private fun DrawScope.escarabajo(c: Offset, s: Float, color: Color) {
        val osc = mezcla(color, Color.Black, 0.45f)
        for (k in -1..1) {
            listOf(-1f, 1f).forEach { d ->
                val y = c.y + k * s * 0.35f
                drawLine(osc, Offset(c.x + d * s * 0.3f, y), Offset(c.x + d * s * 0.75f, y + k * s * 0.25f + s * 0.1f), s * 0.07f, StrokeCap.Round)
            }
        }
        drawOval(Brush.linearGradient(listOf(mezcla(color, Color.White, 0.4f), color, osc), Offset(c.x - s * 0.4f, c.y - s * 0.3f), Offset(c.x + s * 0.4f, c.y + s * 0.6f)), Offset(c.x - s * 0.4f, c.y - s * 0.25f), Size(s * 0.8f, s * 0.95f))
        drawLine(osc, Offset(c.x, c.y - s * 0.22f), Offset(c.x, c.y + s * 0.68f), s * 0.04f)
        drawOval(osc, Offset(c.x - s * 0.26f, c.y - s * 0.52f), Size(s * 0.52f, s * 0.32f))
        drawCircle(osc, s * 0.14f, Offset(c.x, c.y - s * 0.6f))
        drawLine(osc, Offset(c.x - s * 0.06f, c.y - s * 0.7f), Offset(c.x - s * 0.22f, c.y - s * 0.92f), s * 0.04f)
        drawLine(osc, Offset(c.x + s * 0.06f, c.y - s * 0.7f), Offset(c.x + s * 0.22f, c.y - s * 0.92f), s * 0.04f)
    }

    // ---------------------------------------------------------------- Social: la mesa del té en el invernadero

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.SOCIAL))
        val rnd = Random(e.semilla.toLong())

        // El piso de baldosas hidráulicas: barro y crema en damero; las cremas con su rombo verde y sus puntas de barro.
        val lado = 8f * u
        drawRect(c(CREMA))
        val ox = w / 2 - lado * (ceil(w / 2 / lado).toFloat())
        val oy = h / 2 - lado * (ceil(h / 2 / lado).toFloat())
        var j = 0; var y = oy
        while (y < h && j < 30) {
            var i = 0; var x = ox
            while (x < w && i < 60) {
                val r = Rect(x, y, x + lado, y + lado)
                if ((i + j) % 2 == 0) {
                    drawRect(c(mezcla(mezcla(TERRACOTA, CREMA, 0.18f), BARRO_OSC, rnd.nextFloat() * 0.22f)), r.topLeft, r.size)
                } else {
                    val cc = r.center
                    val rombo = Path().apply { moveTo(cc.x, cc.y - lado * 0.3f); lineTo(cc.x + lado * 0.3f, cc.y); lineTo(cc.x, cc.y + lado * 0.3f); lineTo(cc.x - lado * 0.3f, cc.y); close() }
                    drawPath(rombo, c(HIERRO_LUZ).copy(alpha = 0.75f))
                    drawCircle(c(CREMA), lado * 0.07f, cc)
                    listOf(r.topLeft, r.topRight, r.bottomLeft, r.bottomRight).forEach { q -> drawCircle(c(BARRO_OSC).copy(alpha = 0.55f), lado * 0.13f, q) }
                }
                x += lado; i++
            }
            y += lado; j++
        }
        val juntas = Path()
        var gx = ox; while (gx < w) { juntas.moveTo(gx, 0f); juntas.lineTo(gx, h); gx += lado }
        var gy = oy; while (gy < h) { juntas.moveTo(0f, gy); juntas.lineTo(w, gy); gy += lado }
        drawPath(juntas, Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), style = Stroke(0.3f * u))
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f)), Offset(w / 2, h / 2), w * 0.62f), Offset.Zero, size)

        // Las rejillas de hierro de la calefacción, a los lados.
        listOf(1.4f * u, w - 6.4f * u).forEach { x ->
            val rj = Rect(x, h * 0.3f, x + 5f * u, h * 0.7f)
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(rj.left + 0.5f * u, rj.top + 0.7f * u), rj.size)
            drawRect(c(HIERRO), rj.topLeft, rj.size)
            var yy = rj.top + 1.2f * u
            while (yy < rj.bottom - 1f * u) { drawRoundRect(c(NOCHE), Offset(rj.left + 1f * u, yy), Size(rj.width - 2f * u, 0.9f * u), CornerRadius(0.45f * u)); yy += 1.9f * u }
            drawRect(c(HIERRO_LUZ), rj.topLeft, rj.size, style = Stroke(0.4f * u))
        }

        // Las cuatro macetas grandes de las esquinas: palmas y helechos vistos desde arriba.
        val mr = 7.5f * u
        listOf(Offset(9.5f * u, 10f * u), Offset(w - 9.5f * u, 10f * u), Offset(9.5f * u, h - 10f * u), Offset(w - 9.5f * u, h - 10f * u)).forEachIndexed { k, p ->
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.28f), mr * 1.05f, Offset(p.x + 0.9f * u, p.y + 1.4f * u))
            drawCircle(Brush.radialGradient(listOf(c(mezcla(TERRACOTA, Color.White, 0.18f)), c(TERRACOTA), c(BARRO_OSC)), Offset(p.x - mr * 0.3f, p.y - mr * 0.3f), mr * 1.4f), mr, p)
            drawCircle(c(BARRO_OSC), mr * 0.82f, p, style = Stroke(0.4f * u))
            drawCircle(c(TIERRA), mr * 0.78f, p)
            if (k == 0 || k == 3) palmaArriba(p, mr * 1.9f, c(PALMA), c(mezcla(PALMA, HOJA_CLARA, 0.5f)), rnd)
            else for (f in 0 until 7) fronda(p, mr * 1.5f, f * 360f / 7 + rnd.nextFloat() * 15f, 25f, c(if (f % 2 == 0) HELECHO else mezcla(HELECHO, HOJA_CLARA, 0.45f)), 0.45f * u, 9)
        }
        // Hojas caídas en el piso.
        for (k in 0 until 7) {
            val p = Offset(w * rnd.nextFloat(), if (k % 2 == 0) h * (0.02f + 0.12f * rnd.nextFloat()) else h * (0.86f + 0.12f * rnd.nextFloat()))
            hoja(p, 2.4f * u, 0.9f * u, rnd.nextFloat() * 360f, c(if (k % 3 == 0) Color(0xFFB0773A) else HOJA_CLARA), c(MUSGO))
        }

        // Las sillas de jardín de hierro colado con su cojín, alrededor de la mesa.
        val canto = Rect(n.left - 3.4f * u, n.top - 3.4f * u, n.right + 3.4f * u, n.bottom + 3.4f * u)
        val sr = 5.2f * u
        val sillas = mutableListOf<Pair<Offset, Float>>()
        listOf(0.25f, 0.5f, 0.75f).forEach { f ->
            val x = canto.left + canto.width * f
            sillas += Offset(x, canto.top - sr * 1.05f) to -90f
            sillas += Offset(x, canto.bottom + sr * 1.05f) to 90f
        }
        sillas += Offset(canto.left - sr * 1.05f, canto.center.y) to 180f
        sillas += Offset(canto.right + sr * 1.05f, canto.center.y) to 0f
        val cojin = mezcla(e.secundario, Color(0xFF8A3E2A), 0.4f)
        sillas.forEach { (p, hacia) ->
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), sr * 1.15f, Offset(p.x + 0.8f * u, p.y + 1.2f * u))
            // El respaldo: un arco de hierro hacia afuera, con volutas en las puntas.
            val rr = sr * 1.18f
            val ini = hacia - 100f
            drawArc(c(HIERRO), ini, 200f, false, Offset(p.x - rr, p.y - rr), Size(rr * 2, rr * 2), style = Stroke(1.3f * u, cap = StrokeCap.Round))
            drawArc(c(HIERRO_LUZ), ini + 20f, 160f, false, Offset(p.x - rr, p.y - rr), Size(rr * 2, rr * 2), style = Stroke(0.35f * u))
            listOf(ini, ini + 200f).forEach { a ->
                val rad = Math.toRadians(a.toDouble())
                val q = Offset(p.x + rr * cos(rad).toFloat(), p.y + rr * sin(rad).toFloat())
                drawCircle(c(HIERRO), 0.9f * u, q, style = Stroke(0.5f * u))
            }
            // El asiento calado y el cojín con su botón.
            drawCircle(c(HIERRO), sr, p)
            for (k in 0 until 12) {
                val rad = Math.toRadians(k * 30.0)
                drawLine(c(HIERRO_LUZ), Offset(p.x + sr * 0.55f * cos(rad).toFloat(), p.y + sr * 0.55f * sin(rad).toFloat()), Offset(p.x + sr * 0.92f * cos(rad).toFloat(), p.y + sr * 0.92f * sin(rad).toFloat()), 0.3f * u)
            }
            drawCircle(c(HIERRO_LUZ), sr * 0.94f, p, style = Stroke(0.3f * u))
            drawCircle(Brush.radialGradient(listOf(c(mezcla(cojin, Color.White, 0.3f)), c(cojin), c(mezcla(cojin, Color.Black, 0.35f))), Offset(p.x - sr * 0.2f, p.y - sr * 0.25f), sr * 0.75f), sr * 0.6f, p)
            drawCircle(Color.Black.copy(alpha = 0.3f), sr * 0.08f, p)
        }

        // La mesa de roble: su sombra, el canto con veta y las cantoneras de latón.
        sombra(canto, 3f * u, if (oscuro) 0.55f else 0.35f, 1.4f * u)
        drawRoundRect(c(ROBLE_OSC), canto.topLeft, canto.size, CornerRadius(1.4f * u))
        madera(Rect(canto.left + 0.5f * u, canto.top + 0.5f * u, canto.right - 0.5f * u, canto.bottom - 0.5f * u), ROBLE, oscuro, rnd, u, nudos = 2)
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), Offset(canto.left + 0.6f * u, canto.top + 0.6f * u), Size(canto.width - 1.2f * u, canto.height - 1.2f * u), CornerRadius(1.2f * u), style = Stroke(0.35f * u))
        listOf(canto.topLeft to Offset(1f, 1f), canto.topRight to Offset(-1f, 1f), canto.bottomLeft to Offset(1f, -1f), canto.bottomRight to Offset(-1f, -1f)).forEach { (q, d) ->
            val l = 3.2f * u
            val esc = Path().apply { moveTo(q.x, q.y); lineTo(q.x + d.x * l, q.y); lineTo(q.x + d.x * l, q.y + d.y * 0.9f * u); lineTo(q.x + d.x * 0.9f * u, q.y + d.y * 0.9f * u); lineTo(q.x + d.x * 0.9f * u, q.y + d.y * l); lineTo(q.x, q.y + d.y * l); close() }
            drawPath(esc, Brush.linearGradient(listOf(mezcla(LATON, Color.White, 0.3f), c(LATON), c(LATON_OSC)), q, Offset(q.x + d.x * l, q.y + d.y * l)))
        }

        // El mantel de lino con su orilla festoneada que cae sobre el canto; el grafo va en el mantel.
        val linoCol = nicho(e.fondo, LINO, oscuro)
        val orilla = Rect(n.left - 1.6f * u, n.top - 1.6f * u, n.right + 1.6f * u, n.bottom + 1.6f * u)
        val paso = 2.6f * u
        val fest = Path()
        var fx = orilla.left
        while (fx <= orilla.right) { fest.addOval(Rect(fx - paso * 0.5f, orilla.top - paso * 0.42f, fx + paso * 0.5f, orilla.top + paso * 0.42f)); fest.addOval(Rect(fx - paso * 0.5f, orilla.bottom - paso * 0.42f, fx + paso * 0.5f, orilla.bottom + paso * 0.42f)); fx += paso }
        var fy = orilla.top
        while (fy <= orilla.bottom) { fest.addOval(Rect(orilla.left - paso * 0.42f, fy - paso * 0.5f, orilla.left + paso * 0.42f, fy + paso * 0.5f)); fest.addOval(Rect(orilla.right - paso * 0.42f, fy - paso * 0.5f, orilla.right + paso * 0.42f, fy + paso * 0.5f)); fy += paso }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(orilla.left + 0.4f * u, orilla.top + 0.8f * u), orilla.size)
        drawPath(fest, mezcla(linoCol, Color.Black, 0.05f))
        drawRect(mezcla(linoCol, Color.Black, 0.05f), orilla.topLeft, orilla.size)
        drawRect(linoCol, n.topLeft, n.size)
        // La trama del lino, apenas visible, y el filete bordado.
        clipRect(n.left, n.top, n.right, n.bottom) {
            val trama = Color.Black.copy(alpha = if (oscuro) 0.05f else 0.025f)
            var ty = n.top; while (ty < n.bottom) { drawLine(trama, Offset(n.left, ty), Offset(n.right, ty), 0.2f * u); ty += 1.1f * u }
            var tx = n.left; while (tx < n.right) { drawLine(trama, Offset(tx, n.top), Offset(tx, n.bottom), 0.2f * u); tx += 1.1f * u }
        }
        drawRect(c(HELECHO).copy(alpha = 0.35f), Offset(n.left + 0.9f * u, n.top + 0.9f * u), Size(n.width - 1.8f * u, n.height - 1.8f * u), style = Stroke(0.3f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f * u, 0.7f * u))))

        // De noche, las luciérnagas sobre el piso.
        if (oscuro) {
            listOf(0.05f to 0.45f, 0.13f to 0.88f, 0.9f to 0.36f, 0.96f to 0.78f, 0.36f to 0.04f, 0.66f to 0.96f).forEach { (fx2, fy2) -> luciernaga(Offset(w * fx2, h * fy2), 0.5f * u) }
        }
    }

    // ---------------------------------------------------------------- Mapas: el escritorio del naturalista

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.MAPAS))
        val rnd = Random(e.semilla.toLong())
        val yB = h * 0.2f; val yF = h * 0.85f

        // El ventanal del invernadero: el vidrio, las plantas de afuera, los parteluces de hierro y los reflejos.
        val cielo = if (oscuro) listOf(Color(0xFF14231E), Color(0xFF0E1814)) else listOf(Color(0xFFE6F0E2), Color(0xFFC9DCC4))
        drawRect(Brush.verticalGradient(cielo, 0f, yB), Offset.Zero, Size(w, yB + 1f * u))
        clipRect(0f, 0f, w, yB) {
            for (k in 0 until 9) {
                val b = Offset(w * (0.05f + 0.11f * k + 0.04f * rnd.nextFloat()), yB + 1f * u)
                if (k % 3 == 1) {
                    for (j in 0 until 3) hoja(b, yB * (0.9f + 0.3f * rnd.nextFloat()), yB * 0.22f, -70f - j * 25f + rnd.nextFloat() * 10f, c(mezcla(PALMA, HOJA_CLARA, 0.3f * j)).copy(alpha = 0.75f), c(PALMA).copy(alpha = 0.6f))
                } else {
                    for (j in 0 until 3) fronda(b, yB * (0.8f + 0.3f * rnd.nextFloat()), -60f - j * 30f, 30f - j * 20f, c(if (j == 1) HOJA_CLARA else HELECHO).copy(alpha = 0.7f), 0.45f * u, 9)
                }
            }
        }
        drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.18f), Offset.Zero, Size(w, yB))
        val montantes = 7
        for (k in 0..montantes) {
            val x = w * k / montantes
            drawRect(c(HIERRO), Offset(x - 0.7f * u, 0f), Size(1.4f * u, yB))
            drawLine(c(HIERRO_LUZ), Offset(x - 0.4f * u, 0f), Offset(x - 0.4f * u, yB), 0.25f * u)
        }
        drawRect(c(HIERRO), Offset(0f, yB * 0.42f), Size(w, 1f * u))
        drawRect(c(HIERRO), Offset(0f, 0f), Size(w, 1.4f * u))
        for (k in 0 until montantes) {
            val x0 = w * k / montantes
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f), Offset(x0 + w / montantes * 0.25f, yB * 0.95f), Offset(x0 + w / montantes * 0.55f, yB * 0.48f), 0.6f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.25f), Offset(x0 + w / montantes * 0.4f, yB * 0.38f), Offset(x0 + w / montantes * 0.6f, 1.6f * u), 0.4f * u)
        }
        if (oscuro) listOf(0.12f to 0.3f, 0.47f to 0.6f, 0.83f to 0.25f, 0.66f to 0.75f).forEach { (fx, fy) -> luciernaga(Offset(w * fx, yB * fy), 0.45f * u) }

        // El helecho colgante en su cesta de alambre, arriba a la izquierda.
        val ce = Offset(w * 0.105f, yB * 0.62f)
        drawLine(c(LATON_OSC), Offset(ce.x, 0f), Offset(ce.x - 3f * u, ce.y - 1f * u), 0.25f * u)
        drawLine(c(LATON_OSC), Offset(ce.x, 0f), Offset(ce.x + 3f * u, ce.y - 1f * u), 0.25f * u)
        listOf(160f, 130f, 50f, 20f, 95f).forEachIndexed { k, a -> fronda(Offset(ce.x, ce.y - 1f * u), 7f * u, a, if (a > 90f) 30f else -30f, c(if (k % 2 == 0) HELECHO else HOJA_CLARA), 0.4f * u, 9) }
        drawArc(c(HIERRO), 0f, 180f, true, Offset(ce.x - 3.4f * u, ce.y - 3f * u), Size(6.8f * u, 5.6f * u))
        drawArc(c(MUSGO), 0f, 180f, false, Offset(ce.x - 3.4f * u, ce.y - 3f * u), Size(6.8f * u, 5.6f * u), style = Stroke(0.5f * u))
        drawLine(c(HIERRO_LUZ), Offset(ce.x - 3.4f * u, ce.y - 0.2f * u), Offset(ce.x + 3.4f * u, ce.y - 0.2f * u), 0.4f * u)

        // El murete de ladrillo bajo el ventanal, que se ve a los lados del escritorio.
        drawRect(c(BARRO_OSC), Offset(0f, yB), Size(w, yF - yB))
        val hiladas = Path()
        var hy = yB; var hk = 0
        while (hy < yF && hk < 40) {
            hiladas.moveTo(0f, hy); hiladas.lineTo(w, hy)
            var hx = if (hk % 2 == 0) 0f else 3f * u
            while (hx < w) { hiladas.moveTo(hx, hy); hiladas.lineTo(hx, hy + 2.4f * u); hx += 6f * u }
            hy += 2.4f * u; hk++
        }
        drawPath(hiladas, c(CREMA).copy(alpha = if (oscuro) 0.2f else 0.35f), style = Stroke(0.3f * u))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), yB, yB + 6f * u), Offset(0f, yB), Size(w, 6f * u))
        drawRect(c(HIERRO), Offset(0f, yB - 1f * u), Size(w, 1.2f * u))

        // El tablero de roble en perspectiva, con su veta y la luz de la ventana.
        val tablero = Path().apply { moveTo(w * 0.05f, yB); lineTo(w * 0.95f, yB); lineTo(w * 0.995f, yF); lineTo(w * 0.005f, yF); close() }
        drawPath(tablero, c(ROBLE))
        clipPath(tablero) {
            vetas(Rect(0f, yB, w, yF), c(VETA).copy(alpha = 0.4f), rnd, 2.2f * u, false, 0.3f * u)
            nudo(Offset(w * 0.12f, yF - 6f * u), 2.6f * u, 1f * u, c(VETA).copy(alpha = 0.45f), 0.2f * u)
            nudo(Offset(w * 0.86f, yB + 5f * u), 2.2f * u, 0.8f * u, c(VETA).copy(alpha = 0.45f), 0.2f * u)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.16f), Color.Transparent), yB, yB + (yF - yB) * 0.5f), Offset(0f, yB), Size(w, yF - yB))
        }
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(w * 0.05f, yB), Offset(w * 0.95f, yB), 0.6f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.3f), Offset(w * 0.05f, yB + 0.6f * u), Offset(w * 0.95f, yB + 0.6f * u), 0.3f * u)

        // El secante: cuero verde con esquineras de cuero oscuro y filete dorado; el papel del mapa encima.
        val sec = Rect(n.left - 3f * u, n.top - 3f * u, n.right + 3f * u, n.bottom + 3f * u)
        sombra(sec, 1.8f * u, if (oscuro) 0.5f else 0.3f)
        drawRect(Brush.verticalGradient(listOf(c(mezcla(CUERO_VERDE, Color.White, 0.1f)), c(CUERO_VERDE), c(mezcla(CUERO_VERDE, Color.Black, 0.25f))), sec.top, sec.bottom), sec.topLeft, sec.size)
        drawRect(c(LATON).copy(alpha = 0.7f), Offset(sec.left + 1f * u, sec.top + 1f * u), Size(sec.width - 2f * u, sec.height - 2f * u), style = Stroke(0.25f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 0.5f * u))))
        drawRect(nicho(e.fondo, SECANTE, oscuro), n.topLeft, n.size)
        listOf(sec.topLeft to Offset(1f, 1f), sec.topRight to Offset(-1f, 1f), sec.bottomLeft to Offset(1f, -1f), sec.bottomRight to Offset(-1f, -1f)).forEach { (q, d) ->
            val l = 7f * u
            val esq = Path().apply { moveTo(q.x, q.y); lineTo(q.x + d.x * l, q.y); lineTo(q.x, q.y + d.y * l); close() }
            drawPath(esq, c(mezcla(Color(0xFF5A3420), Color.Black, 0.1f)))
            drawLine(c(LATON).copy(alpha = 0.8f), Offset(q.x + d.x * (l - 1f * u), q.y + d.y * 0.4f * u), Offset(q.x + d.x * 0.4f * u, q.y + d.y * (l - 1f * u)), 0.3f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.25f), n.topLeft, n.size, style = Stroke(0.3f * u))

        // A la izquierda, al fondo: el microscopio de latón con su pie de herradura negro, la platina y el espejo.
        val colI = n.left - 4f * u
        val mx = colI * 0.55f; val mb = h * 0.53f
        val ms = minOf(colI * 0.85f, 26f * u) / 20f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(mx - 8f * ms, mb - 1f * ms), Size(17f * ms, 2.4f * ms))
        val pie = Path().apply { moveTo(mx - 7f * ms, mb); lineTo(mx - 5f * ms, mb - 3f * ms); lineTo(mx + 5f * ms, mb - 3f * ms); lineTo(mx + 7f * ms, mb); close() }
        drawPath(pie, Brush.verticalGradient(listOf(c(Color(0xFF3A3A36)), c(Color(0xFF141412))), mb - 3f * ms, mb))
        val laton = Brush.horizontalGradient(listOf(c(LATON_OSC), mezcla(c(LATON), Color.White, 0.35f), c(LATON), c(LATON_OSC)), mx - 3f * ms, mx + 3f * ms)
        drawRect(laton, Offset(mx - 1f * ms, mb - 11f * ms), Size(2f * ms, 8f * ms))
        drawCircle(c(LATON_OSC), 2f * ms, Offset(mx - 0.2f * ms, mb - 5.6f * ms))
        drawCircle(c(VIDRIO), 1.4f * ms, Offset(mx - 0.2f * ms, mb - 5.6f * ms))
        drawRect(c(Color(0xFF1E1E1C)), Offset(mx - 5f * ms, mb - 9f * ms), Size(9f * ms, 1.2f * ms))
        drawRect(c(VIDRIO).copy(alpha = 0.8f), Offset(mx - 2.5f * ms, mb - 9.6f * ms), Size(4f * ms, 0.6f * ms))
        val brazo = Path().apply { moveTo(mx + 0.8f * ms, mb - 11f * ms); cubicTo(mx + 5f * ms, mb - 13f * ms, mx + 4.5f * ms, mb - 19f * ms, mx + 1.5f * ms, mb - 21f * ms) }
        drawPath(brazo, c(LATON_OSC), style = Stroke(1.8f * ms, cap = StrokeCap.Round))
        drawPath(brazo, mezcla(c(LATON), Color.White, 0.2f), style = Stroke(0.6f * ms, cap = StrokeCap.Round))
        rotate(-12f, Offset(mx - 1f * ms, mb - 16f * ms)) {
            drawRect(laton, Offset(mx - 2.4f * ms, mb - 24f * ms), Size(3f * ms, 12f * ms))
            drawRect(c(Color(0xFF1E1E1C)), Offset(mx - 2.8f * ms, mb - 25.2f * ms), Size(3.8f * ms, 1.4f * ms))
            drawRect(c(LATON_OSC), Offset(mx - 2f * ms, mb - 12.6f * ms), Size(2.2f * ms, 1.6f * ms))
            drawRect(c(LATON), Offset(mx - 2.8f * ms, mb - 19f * ms), Size(3.8f * ms, 0.8f * ms))
        }
        drawCircle(c(LATON), 1.3f * ms, Offset(mx + 2.6f * ms, mb - 15f * ms))
        drawCircle(Color.White.copy(alpha = 0.4f), 0.5f * ms, Offset(mx + 2.3f * ms, mb - 15.3f * ms))

        // A la izquierda, al frente: la caja de herborizar de hojalata verde, con su asa de cuero y un helecho asomando.
        val va = Rect(w * 0.025f, h * 0.63f, colI + 1f * u, h * 0.79f)
        fronda(Offset(va.right - 1f * u, va.center.y), va.width * 0.55f, -20f, -40f, c(HELECHO), 0.45f * u, 9)
        fronda(Offset(va.right - 1f * u, va.center.y + 1f * u), va.width * 0.45f, 15f, 30f, c(HOJA_CLARA), 0.4f * u, 9)
        sombra(va, 1.6f * u, 0.4f, va.height / 2)
        drawArc(c(Color(0xFF5A3420)), 180f, 180f, false, Offset(va.left + va.width * 0.2f, va.top - va.height * 0.5f), Size(va.width * 0.6f, va.height * 1.0f), style = Stroke(0.9f * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(mezcla(TELA, Color.White, 0.25f)), c(TELA), c(mezcla(TELA, Color.Black, 0.4f))), va.top, va.bottom), va.topLeft, va.size, CornerRadius(va.height / 2))
        drawOval(c(mezcla(TELA, Color.Black, 0.25f)), Offset(va.left, va.top), Size(va.height * 0.5f, va.height))
        drawOval(c(LATON_OSC), Offset(va.left, va.top), Size(va.height * 0.5f, va.height), style = Stroke(0.3f * u))
        drawLine(c(LATON), Offset(va.left + va.width * 0.25f, va.top + va.height * 0.22f), Offset(va.right - va.width * 0.12f, va.top + va.height * 0.22f), 0.45f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.35f), Offset(va.left + va.width * 0.2f, va.top + va.height * 0.12f), Offset(va.right - va.width * 0.2f, va.top + va.height * 0.12f), 0.4f * u)
        pomo(Offset(va.left + va.width * 0.62f, va.top + va.height * 0.55f), 0.7f * u, oscuro)

        // A la derecha, al fondo: tres libros apilados con sus nervios dorados, y encima una maceta con un helecho.
        val colD0 = n.right + 4f * u
        val dc = (colD0 + w * 0.97f) / 2
        val bw = minOf(w * 0.97f - colD0, 30f * u)
        val bb = h * 0.52f
        for (k in 0 until 3) {
            val lw = bw * (0.95f - k * 0.08f); val lh = 2.6f * u
            val r = Rect(dc - lw / 2 + (k - 1) * 0.6f * u, bb - (k + 1) * lh, dc + lw / 2 + (k - 1) * 0.6f * u, bb - k * lh)
            if (k == 0) sombra(r, 1.2f * u, 0.4f)
            drawRect(c(LIBROS[k]), r.topLeft, r.size)
            drawRect(c(PAPEL), Offset(r.right - 1f * u, r.top + 0.3f * u), Size(0.8f * u, lh - 0.6f * u))
            drawRect(c(LATON), Offset(r.left + lw * 0.12f, r.top), Size(0.4f * u, lh))
            drawRect(c(LATON), Offset(r.left + lw * 0.2f, r.top), Size(0.4f * u, lh))
            drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, 0.5f * u))
        }
        val pb = Offset(dc, bb - 7.8f * u)
        listOf(-120f, -95f, -70f, -45f, -140f).forEachIndexed { k, a -> fronda(Offset(pb.x, pb.y - 5f * u), 9f * u, a, if (a < -90f) -25f else 25f, c(if (k % 2 == 0) HELECHO else HOJA_CLARA), 0.45f * u, 10) }
        maceta(pb, 7f * u, 6f * u, oscuro)

        // A la derecha, al frente: el tintero de vidrio con su tapa de latón y la plumilla.
        val ti = Offset(dc - bw * 0.15f, h * 0.76f)
        val tw = 6f * u
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(ti.x - tw * 0.7f, ti.y - 0.6f * u), Size(tw * 1.5f, 1.6f * u))
        drawRoundRect(c(Color(0xFF2E3A40)).copy(alpha = 0.9f), Offset(ti.x - tw / 2, ti.y - tw * 0.85f), Size(tw, tw * 0.85f), CornerRadius(0.8f * u))
        drawRect(c(Color(0xFF101418)), Offset(ti.x - tw / 2 + 0.6f * u, ti.y - tw * 0.45f), Size(tw - 1.2f * u, tw * 0.4f))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.55f), Offset(ti.x - tw * 0.32f, ti.y - tw * 0.75f), Offset(ti.x - tw * 0.32f, ti.y - tw * 0.15f), 0.4f * u)
        drawRect(Brush.horizontalGradient(listOf(c(LATON_OSC), mezcla(c(LATON), Color.White, 0.3f), c(LATON_OSC)), ti.x - tw * 0.3f, ti.x + tw * 0.3f), Offset(ti.x - tw * 0.3f, ti.y - tw * 1.1f), Size(tw * 0.6f, tw * 0.28f))
        val pl0 = Offset(ti.x + tw * 0.2f, ti.y - tw * 1.05f)
        val pl1 = Offset(ti.x + tw * 1.7f, ti.y - tw * 2.4f)
        drawLine(c(Color(0xFF3A2416)), pl0, pl1, 0.8f * u, StrokeCap.Round)
        drawLine(c(LATON), pl0, Offset(pl0.x + (pl1.x - pl0.x) * 0.15f, pl0.y + (pl1.y - pl0.y) * 0.15f), 0.9f * u)
        // Y la lupa de mango de hueso, tumbada.
        val lu = Offset(dc + bw * 0.28f, h * 0.7f)
        drawLine(c(Color(0xFFE8DCC0)), Offset(lu.x + 1.6f * u, lu.y + 2f * u), Offset(lu.x + 3.4f * u, lu.y + 6f * u), 1.1f * u, StrokeCap.Round)
        drawCircle(Color.Black.copy(alpha = 0.25f), 3f * u, Offset(lu.x + 0.5f * u, lu.y + 0.8f * u))
        drawCircle(c(VIDRIO).copy(alpha = 0.55f), 2.6f * u, lu)
        drawCircle(c(LATON), 2.8f * u, lu, style = Stroke(0.6f * u))
        drawArc(Color.White.copy(alpha = 0.6f), 200f, 60f, false, Offset(lu.x - 1.8f * u, lu.y - 1.8f * u), Size(3.6f * u, 3.6f * u), style = Stroke(0.35f * u))

        // El canto del escritorio: la moldura, el cajón con su tirador de asa y su bocallave; las patas torneadas y el piso.
        drawRect(c(Color(0xFF5E3624)), Offset(0f, yF), Size(w, h - yF))
        var tx = 0f; var tk = 0
        while (tx < w && tk < 60) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(tx, yF), Offset(tx, h), 0.3f * u); tx += 6f * u; tk++ }
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, h * 0.95f), Offset(w, h * 0.95f), 0.3f * u)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent), yF, h), Offset(0f, yF), Size(w, h - yF))
        val frente = Rect(w * 0.005f, yF, w * 0.995f, yF + h * 0.075f)
        moldura(Rect(frente.left, frente.top, frente.right, frente.top + 1.8f * u), ROBLE_LUZ, oscuro)
        madera(Rect(frente.left, frente.top + 1.8f * u, frente.right, frente.bottom), ROBLE, oscuro, rnd, u)
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(frente.left, frente.bottom), Offset(frente.right, frente.bottom), 0.5f * u)
        val caj = Rect(w * 0.32f, frente.top + 2.4f * u, w * 0.68f, frente.bottom - 0.8f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), caj.topLeft, caj.size, style = Stroke(0.4f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.15f), Offset(caj.left + 0.4f * u, caj.top + 0.4f * u), Size(caj.width - 0.8f * u, 0.3f * u))
        val ac = Offset(caj.center.x, caj.center.y)
        drawArc(c(LATON), 0f, 180f, false, Offset(ac.x - 3f * u, ac.y - 1.6f * u), Size(6f * u, 3.2f * u), style = Stroke(0.5f * u))
        listOf(-3f, 3f).forEach { dx -> pomo(Offset(ac.x + dx * u, ac.y - 0.1f * u), 0.55f * u, oscuro) }
        listOf(caj.left + 3f * u, caj.right - 3f * u).forEach { x ->
            drawCircle(c(LATON), 0.8f * u, Offset(x, ac.y)); drawRect(c(VETA), Offset(x - 0.15f * u, ac.y - 0.3f * u), Size(0.3f * u, 0.9f * u))
        }
        listOf(w * 0.025f, w * 0.935f).forEach { x ->
            val pw = w * 0.04f
            val lp = Rect(x, frente.bottom, x + pw, h * 0.99f)
            val cx = lp.center.x
            val torn = Brush.horizontalGradient(listOf(c(ROBLE_OSC), c(ROBLE_LUZ), c(ROBLE), c(ROBLE_OSC)), lp.left, lp.right)
            drawRect(torn, Offset(cx - pw * 0.32f, lp.top), Size(pw * 0.64f, lp.height))
            drawOval(torn, Offset(lp.left, lp.top + lp.height * 0.18f), Size(pw, lp.height * 0.36f))
            drawRect(torn, Offset(lp.left + pw * 0.08f, lp.top + lp.height * 0.05f), Size(pw * 0.84f, lp.height * 0.08f))
            drawRect(c(VETA).copy(alpha = 0.5f), Offset(cx - pw * 0.32f, lp.top + lp.height * 0.6f), Size(pw * 0.64f, 0.3f * u))
        }
    }

    // ---------------------------------------------------------------- Otras vidas: el gabinete de herbario

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.OTRAS_VIDAS))
        val rnd = Random(e.semilla.toLong())
        val yT = h * 0.17f

        // El papel de la pared, con su motivo de hojas apenas marcado.
        drawRect(if (oscuro) mezcla(PAPEL_PARED, NOCHE, 0.78f) else PAPEL_PARED, Offset.Zero, Size(w, yT))
        var px = 4f * u; var pk = 0
        while (px < w && pk < 80) { hoja(Offset(px, yT * (if (pk % 2 == 0) 0.3f else 0.75f)), 3f * u, 1f * u, if (pk % 2 == 0) -60f else -120f, c(HELECHO).copy(alpha = if (oscuro) 0.25f else 0.14f)); px += 7f * u; pk++ }

        // Encima del gabinete: las macetas de los extremos, los libros con la lupa y la caja de Ward en el centro.
        listOf(w * 0.075f, w * 0.925f).forEachIndexed { k, x ->
            val b = Offset(x, yT)
            listOf(-130f, -105f, -80f, -55f, -150f, -30f).forEachIndexed { j, a -> fronda(Offset(b.x, b.y - 4.6f * u), 10f * u, a, if (a < -90f) -30f else 30f, c(if ((j + k) % 2 == 0) HELECHO else HOJA_CLARA), 0.45f * u, 10) }
            maceta(b, 7f * u, 5.6f * u, oscuro)
        }
        val lb = w * 0.24f
        for (k in 0 until 3) {
            val lw = 3f * u; val lh = (9.5f - k * 1.2f) * u
            val r = Rect(lb + k * lw, yT - lh, lb + (k + 1) * lw, yT)
            drawRect(c(LIBROS[k]), r.topLeft, r.size)
            drawRect(c(LATON), Offset(r.left, r.top + lh * 0.15f), Size(lw, 0.35f * u))
            drawRect(c(LATON), Offset(r.left, r.bottom - lh * 0.2f), Size(lw, 0.35f * u))
            drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(0.5f * u, r.height))
        }
        rotate(-25f, Offset(lb + 9f * u, yT)) {
            drawRect(c(LIBROS[3]), Offset(lb + 9f * u, yT - 8f * u), Size(3f * u, 8f * u))
            drawRect(c(LATON), Offset(lb + 9f * u, yT - 6.6f * u), Size(3f * u, 0.35f * u))
        }
        val lu = Offset(w * 0.75f, yT - 3.4f * u)
        drawLine(c(ROBLE_OSC), Offset(lu.x + 2f * u, lu.y + 1.5f * u), Offset(lu.x + 6f * u, lu.y + 3f * u), 1.1f * u, StrokeCap.Round)
        drawCircle(c(VIDRIO).copy(alpha = 0.5f), 2.8f * u, lu)
        drawCircle(c(LATON), 3f * u, lu, style = Stroke(0.6f * u))
        drawArc(Color.White.copy(alpha = 0.6f), 200f, 60f, false, Offset(lu.x - 2f * u, lu.y - 2f * u), Size(4f * u, 4f * u), style = Stroke(0.35f * u))
        val fr = Offset(w * 0.66f, yT)
        drawRect(c(VIDRIO).copy(alpha = 0.45f), Offset(fr.x - 2.4f * u, fr.y - 8f * u), Size(4.8f * u, 8f * u))
        drawRect(c(LATON_OSC), Offset(fr.x - 2.6f * u, fr.y - 9.2f * u), Size(5.2f * u, 1.4f * u))
        escarabajo(Offset(fr.x, fr.y - 4f * u), 3f * u, c(Color(0xFF2E7A5A)))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(fr.x - 1.6f * u, fr.y - 7f * u), Offset(fr.x - 1.6f * u, fr.y - 1f * u), 0.3f * u)
        // La caja de Ward: la base de roble, el vidrio con el helecho y el musgo, el tejado a dos aguas y el remate.
        val cw0 = minOf(w * 0.2f, 34f * u)
        val wc = Rect(w / 2 - cw0 / 2, yT - 9.5f * u, w / 2 + cw0 / 2, yT - 2f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(wc.left - 1f * u, yT - 0.6f * u), Size(wc.width + 2f * u, 0.8f * u))
        drawRect(c(ROBLE_OSC), Offset(wc.left - 1f * u, yT - 2f * u), Size(wc.width + 2f * u, 2f * u))
        drawRect(c(if (oscuro) Color(0xFF1A2A20) else Color(0xFFD6E6D6)), wc.topLeft, wc.size)
        clipRect(wc.left, wc.top - 6f * u, wc.right, wc.bottom) {
            drawOval(c(MUSGO), Offset(wc.left, wc.bottom - 1.8f * u), Size(wc.width, 3f * u))
            listOf(-125f, -100f, -75f, -50f, -150f, -28f).forEachIndexed { j, a -> fronda(Offset(wc.center.x + (j - 2.5f) * 1f * u, wc.bottom - 0.6f * u), 9f * u, a, if (a < -90f) -20f else 20f, c(if (j % 2 == 0) HELECHO else HOJA_CLARA), 0.4f * u, 10) }
        }
        val techo = Path().apply { moveTo(wc.left - 0.6f * u, wc.top); lineTo(wc.center.x, wc.top - 5f * u); lineTo(wc.right + 0.6f * u, wc.top); close() }
        drawPath(techo, c(VIDRIO).copy(alpha = if (oscuro) 0.25f else 0.5f))
        drawPath(techo, c(HIERRO), style = Stroke(0.6f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), wc.topLeft, wc.size)
        drawRect(c(HIERRO), wc.topLeft, wc.size, style = Stroke(0.6f * u))
        listOf(0.33f, 0.66f).forEach { f -> drawLine(c(HIERRO), Offset(wc.left + wc.width * f, wc.top - 5f * u * (1f - abs(f - 0.5f) * 2f)), Offset(wc.left + wc.width * f, wc.bottom), 0.4f * u) }
        drawLine(Color.White.copy(alpha = 0.55f), Offset(wc.left + 1.5f * u, wc.bottom - 1f * u), Offset(wc.left + 4f * u, wc.top + 1f * u), 0.35f * u)
        drawCircle(c(LATON), 0.9f * u, Offset(wc.center.x, wc.top - 5.6f * u))

        // La cornisa: el tablero volado, la gola y la fila de dentículos.
        val cuerpo = Rect(0f, yT, w, h * 0.955f)
        drawRect(c(ROBLE_OSC), cuerpo.topLeft, cuerpo.size)
        madera(Rect(0f, yT, w, yT + 2.2f * u), ROBLE_LUZ, oscuro, rnd, u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, yT + 2.2f * u), Size(w, 0.5f * u))
        moldura(Rect(cuerpo.left, yT + 2.2f * u, cuerpo.right, yT + 5f * u), ROBLE, oscuro)
        val den = Rect(cuerpo.left, yT + 5f * u, cuerpo.right, yT + 7.4f * u)
        drawRect(c(ROBLE_OSC), den.topLeft, den.size)
        var dx = den.left + 0.6f * u
        while (dx < den.right - 1.6f * u) {
            drawRect(Brush.verticalGradient(listOf(c(ROBLE_LUZ), c(ROBLE)), den.top, den.bottom), Offset(dx, den.top), Size(1.6f * u, den.height - 0.3f * u))
            dx += 2.6f * u
        }
        moldura(Rect(cuerpo.left, yT + 7.4f * u, cuerpo.right, yT + 8.6f * u), ROBLE_LUZ, oscuro)

        // El friso con los cinco portaetiquetas de latón, uno sobre cada compartimento.
        val friso = Rect(cuerpo.left, yT + 8.6f * u, cuerpo.right, n.top - 2f * u)
        madera(friso, ROBLE, oscuro, rnd, u, nudos = 1)
        val cw = n.width / 5f
        for (i in 0 until 5) {
            val cx = n.left + cw * (i + 0.5f)
            val pw = minOf(cw * 0.5f, 14f * u); val ph = minOf(friso.height * 0.62f, 4.4f * u)
            val pr = Rect(cx - pw / 2, friso.center.y - ph / 2, cx + pw / 2, friso.center.y + ph / 2)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(pr.left + 0.3f * u, pr.top + 0.4f * u), pr.size)
            drawRect(Brush.verticalGradient(listOf(mezcla(c(LATON), Color.White, 0.3f), c(LATON), c(LATON_OSC)), pr.top, pr.bottom), pr.topLeft, pr.size)
            drawRect(c(PAPEL), Offset(pr.left + 0.8f * u, pr.top + 0.7f * u), Size(pr.width - 1.6f * u, pr.height - 1.4f * u))
            drawLine(c(TINTA).copy(alpha = 0.35f), Offset(pr.left + pw * 0.25f, pr.center.y), Offset(pr.right - pw * 0.25f, pr.center.y), 0.25f * u)
            listOf(pr.left - 0.9f * u, pr.right + 0.9f * u).forEach { x -> drawCircle(c(LATON_OSC), 0.45f * u, Offset(x, pr.center.y)) }
        }
        moldura(Rect(cuerpo.left, n.top - 2f * u, cuerpo.right, n.top), ROBLE_LUZ, oscuro)

        // Las pilastras talladas de los costados, con un helecho en relieve y su capitel.
        listOf(cuerpo.left to n.left, n.right to cuerpo.right).forEach { (x0, x1) ->
            val p = Rect(x0, n.top, x1, n.bottom)
            madera(p, ROBLE, oscuro, rnd, u, vertical = true)
            val pan = Rect(p.left + p.width * 0.2f, p.top + 2.6f * u, p.right - p.width * 0.2f, p.bottom - 2.6f * u)
            drawRect(c(ROBLE_OSC), pan.topLeft, pan.size)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.2f), Offset(pan.left, pan.bottom - 0.3f * u), Size(pan.width, 0.3f * u))
            fronda(Offset(pan.center.x + 0.2f * u, pan.bottom - 1f * u), pan.height * 0.9f, -90f, 8f, c(ROBLE_LUZ).copy(alpha = 0.55f), 0.35f * u, 12)
            fronda(Offset(pan.center.x, pan.bottom - 1.2f * u), pan.height * 0.9f, -90f, 8f, c(ROBLE).copy(alpha = 0.9f), 0.35f * u, 12)
            drawRect(c(ROBLE_LUZ), Offset(p.left, p.top), Size(p.width, 1.2f * u))
            drawRect(c(ROBLE_LUZ), Offset(p.left, p.bottom - 1.2f * u), Size(p.width, 1.2f * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(p.right - 0.4f * u, p.top), Size(0.4f * u, p.height))
        }

        // El nicho: cinco compartimentos forrados de secante, con sus montantes de roble y la sombra de arriba.
        drawRect(nicho(e.fondo, SECANTE, oscuro), n.topLeft, n.size)
        for (i in 0 until 5) {
            val r = Rect(n.left + cw * i, n.top, n.left + cw * (i + 1), n.bottom)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Color.Transparent), r.top, r.top + 4f * u), r.topLeft, Size(r.width, 4f * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), Color.Transparent), r.left, r.left + 2.5f * u), r.topLeft, Size(2.5f * u, r.height))
        }
        val mont = 1.8f * u
        for (i in 1 until 5) {
            val x = n.left + cw * i
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(x + mont / 2, n.top), Size(0.6f * u, n.height))
            drawRect(Brush.horizontalGradient(listOf(c(ROBLE_LUZ), c(ROBLE), c(ROBLE_OSC)), x - mont / 2, x + mont / 2), Offset(x - mont / 2, n.top), Size(mont, n.height))
        }
        drawRect(Color.Black.copy(alpha = 0.35f), n.topLeft, n.size, style = Stroke(0.35f * u))

        // El zócalo: la repisa moldurada, la fila de cajoncitos con su tirador y su portaetiqueta, y los pies de bollo.
        moldura(Rect(0f, n.bottom, w, n.bottom + 2.4f * u), ROBLE_LUZ, oscuro)
        val zo = Rect(cuerpo.left, n.bottom + 2.4f * u, cuerpo.right, h * 0.95f)
        madera(zo, ROBLE_OSC, oscuro, rnd, u)
        val nc = 6
        for (j in 0 until nc) {
            val r = Rect(zo.left + zo.width * j / nc + 1f * u, zo.top + 0.9f * u, zo.left + zo.width * (j + 1) / nc - 1f * u, zo.bottom - 0.9f * u)
            drawRect(c(ROBLE), r.topLeft, r.size)
            drawRect(Color.Black.copy(alpha = 0.35f), r.topLeft, r.size, style = Stroke(0.3f * u))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.15f), Offset(r.left, r.top), Size(r.width, 0.3f * u))
            val et = Rect(r.center.x - 2.4f * u, r.top + r.height * 0.18f, r.center.x + 2.4f * u, r.top + r.height * 0.5f)
            drawRect(c(LATON), et.topLeft, et.size, style = Stroke(0.35f * u))
            drawRect(c(PAPEL).copy(alpha = 0.8f), Offset(et.left + 0.3f * u, et.top + 0.3f * u), Size(et.width - 0.6f * u, et.height - 0.6f * u))
            pomo(Offset(r.center.x, r.top + r.height * 0.74f), 0.6f * u, oscuro)
        }
        drawRect(c(Color(0xFF5E3624)), Offset(0f, h * 0.95f), Size(w, h * 0.05f))
        var tx = 0f; var tk = 0
        while (tx < w && tk < 80) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(tx, h * 0.95f), Offset(tx, h), 0.3f * u); tx += 5f * u; tk++ }
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, h * 0.95f), Size(w, 0.8f * u))
        listOf(w * 0.05f, w * 0.95f).forEach { x ->
            drawOval(Brush.horizontalGradient(listOf(c(ROBLE_OSC), c(ROBLE_LUZ), c(ROBLE_OSC)), x - 2.6f * u, x + 2.6f * u), Offset(x - 2.6f * u, h * 0.945f), Size(5.2f * u, h * 0.05f))
        }
        if (oscuro) listOf(0.18f to 0.07f, 0.41f to 0.04f, 0.58f to 0.1f, 0.83f to 0.06f).forEach { (fx, fy) -> luciernaga(Offset(w * fx, h * fy), 0.5f * u) }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f // un dp del ícono (el lienzo mide ≈ 70 dp de alto)
        val c = Offset(w / 2, h / 2)
        fun k(x: Color) = t(x, oscuro)
        val rnd = Random(11)
        when (id) {
            "unesco" -> {
                // La casa de las palmas: la nave central con su cúpula de costillas de hierro, las dos alas, el zócalo de ladrillo.
                val suelo = c.y + 26f * s
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(c.x - 50f * s, suelo - 2f * s), Size(100f * s, 5f * s))
                drawRect(k(mezcla(MUSGO, HOJA_CLARA, 0.3f)), Offset(c.x - 50f * s, suelo - 1f * s), Size(100f * s, 2.5f * s))
                val vidrio = if (oscuro) Color(0xFF2A4038) else Color(0xFFD8EADF)
                fun nave(cx: Float, ancho: Float, alto: Float, base: Float) {
                    val r = Rect(cx - ancho / 2, base - alto, cx + ancho / 2, base)
                    val forma = Path().apply { moveTo(r.left, r.bottom); lineTo(r.left, r.top + alto * 0.45f); cubicTo(r.left, r.top - alto * 0.05f, r.right, r.top - alto * 0.05f, r.right, r.top + alto * 0.45f); lineTo(r.right, r.bottom); close() }
                    drawPath(forma, Brush.verticalGradient(listOf(vidrio, mezcla(vidrio, e.c1, 0.35f)), r.top, r.bottom))
                    clipPath(forma) {
                        // La palma de adentro.
                        val pb = Offset(cx, base)
                        drawLine(k(Color(0xFF6A4A2E)), pb, Offset(cx, r.top + alto * 0.35f), 1.6f * s)
                        for (j in 0 until 6) hoja(Offset(cx, r.top + alto * 0.35f), alto * 0.42f, alto * 0.08f, -170f + j * 32f, e.c1.copy(alpha = 0.85f))
                        var x = r.left + 4f * s
                        while (x < r.right) { drawLine(k(HIERRO), Offset(x, r.top), Offset(x, r.bottom), 0.7f * s); x += 5f * s }
                        var y = r.top + alto * 0.45f
                        while (y < r.bottom) { drawLine(k(HIERRO), Offset(r.left, y), Offset(r.right, y), 0.6f * s); y += 5f * s }
                        for (j in 1 until 6) drawArc(k(HIERRO), 180f, 180f, false, Offset(r.left + ancho * j / 12f, r.top), Size(ancho * (1f - j / 6f), alto * 0.9f), style = Stroke(0.6f * s))
                        drawLine(Color.White.copy(alpha = 0.6f), Offset(r.left + ancho * 0.2f, r.bottom - 2f * s), Offset(r.left + ancho * 0.35f, r.top + alto * 0.3f), 1.2f * s)
                    }
                    drawPath(forma, k(HIERRO), style = Stroke(1.1f * s))
                }
                nave(c.x - 30f * s, 30f * s, 22f * s, suelo - 3f * s)
                nave(c.x + 30f * s, 30f * s, 22f * s, suelo - 3f * s)
                nave(c.x, 36f * s, 46f * s, suelo - 3f * s)
                drawRect(Brush.verticalGradient(listOf(k(Color(0xFFB0603A)), k(Color(0xFF7E3F24))), suelo - 4f * s, suelo), Offset(c.x - 46f * s, suelo - 4f * s), Size(92f * s, 4f * s))
                var bx = c.x - 46f * s
                while (bx < c.x + 46f * s) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(bx, suelo - 4f * s), Offset(bx, suelo), 0.4f * s); bx += 4f * s }
                drawLine(e.c2, Offset(c.x, suelo - 49f * s), Offset(c.x, suelo - 56f * s), 0.8f * s)
                drawPath(Path().apply { moveTo(c.x, suelo - 56f * s); lineTo(c.x + 6f * s, suelo - 54.5f * s); lineTo(c.x, suelo - 53f * s); close() }, e.c2)
                drawCircle(k(LATON), 1.3f * s, Offset(c.x, suelo - 49f * s))
            }
            "rascar" -> {
                // El pliego de herbario sobre su tabla, con la capa dorada que se raspa con un penique.
                rotate(-4f, c) {
                    val tb = Rect(c.x - 34f * s, c.y - 27f * s, c.x + 34f * s, c.y + 27f * s)
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(tb.left + 1.5f * s, tb.top + 2f * s), tb.size)
                    drawRect(Brush.verticalGradient(listOf(k(ROBLE_LUZ), k(ROBLE_OSC)), tb.top, tb.bottom), tb.topLeft, tb.size)
                    vetas(tb, k(VETA).copy(alpha = 0.4f), rnd, 3f * s, false, 0.5f * s)
                    val hoja0 = Rect(tb.left + 4f * s, tb.top + 4f * s, tb.right - 4f * s, tb.bottom - 4f * s)
                    drawRect(k(PAPEL), hoja0.topLeft, hoja0.size)
                    drawRect(k(TINTA).copy(alpha = 0.5f), Offset(hoja0.left + 1.5f * s, hoja0.top + 1.5f * s), Size(hoja0.width - 3f * s, hoja0.height - 3f * s), style = Stroke(0.5f * s))
                    val tierra = Path().apply {
                        moveTo(hoja0.left + 6f * s, hoja0.top + 12f * s); cubicTo(hoja0.left + 16f * s, hoja0.top + 3f * s, hoja0.left + 30f * s, hoja0.top + 14f * s, hoja0.left + 40f * s, hoja0.top + 7f * s)
                        cubicTo(hoja0.right - 4f * s, hoja0.top + 4f * s, hoja0.right - 3f * s, hoja0.top + 24f * s, hoja0.right - 12f * s, hoja0.top + 34f * s)
                        cubicTo(hoja0.left + 30f * s, hoja0.bottom - 3f * s, hoja0.left + 8f * s, hoja0.bottom - 10f * s, hoja0.left + 6f * s, hoja0.top + 12f * s); close()
                    }
                    drawPath(tierra, e.c1.copy(alpha = 0.3f)); drawPath(tierra, e.c1.copy(alpha = 0.85f), style = Stroke(0.8f * s))
                    fronda(Offset(hoja0.left + 10f * s, hoja0.bottom - 6f * s), 14f * s, -60f, 20f, e.c1.copy(alpha = 0.7f), 0.7f * s, 9)
                    // La capa dorada sobre la mitad derecha, con el tramo raspado.
                    val capa = Rect(hoja0.center.x - 2f * s, hoja0.top + 2f * s, hoja0.right - 2f * s, hoja0.bottom - 2f * s)
                    val raspado = Path().apply {
                        moveTo(capa.left + 3f * s, capa.top + 5f * s); lineTo(capa.left + 16f * s, capa.top + 2f * s); lineTo(capa.left + 20f * s, capa.top + 12f * s)
                        lineTo(capa.left + 12f * s, capa.top + 20f * s); lineTo(capa.left + 16f * s, capa.bottom - 6f * s); lineTo(capa.left + 4f * s, capa.bottom - 3f * s); close()
                    }
                    clipPath(raspado, clipOp = ClipOp.Difference) {
                        drawRect(Brush.linearGradient(listOf(k(Color(0xFFD9BE6E)), k(Color(0xFFA8883E)), k(Color(0xFFD9BE6E))), capa.topLeft, capa.bottomRight), capa.topLeft, capa.size)
                        for (j in 0 until 5) drawLine(Color.White.copy(alpha = 0.25f), Offset(capa.left + j * 6f * s, capa.top), Offset(capa.left + j * 6f * s + 8f * s, capa.bottom), 0.5f * s)
                    }
                    drawPath(raspado, k(LATON_OSC).copy(alpha = 0.7f), style = Stroke(0.5f * s))
                }
                // El penique de bronce.
                val ch = Offset(c.x + 30f * s, c.y + 18f * s); val cr = 9f * s
                drawCircle(Color.Black.copy(alpha = 0.35f), cr, Offset(ch.x + 1f * s, ch.y + 1.5f * s))
                drawCircle(Brush.radialGradient(listOf(mezcla(e.c2, Color.White, 0.4f), e.c2, mezcla(e.c2, Color.Black, 0.35f)), Offset(ch.x - 3f * s, ch.y - 3f * s), cr * 1.4f), cr, ch)
                drawCircle(mezcla(e.c2, Color.Black, 0.3f), cr * 0.78f, ch, style = Stroke(0.6f * s))
                hoja(Offset(ch.x - 3f * s, ch.y + 3f * s), 7f * s, 2.4f * s, -45f, mezcla(e.c2, Color.Black, 0.3f))
            }
            else -> {
                // El plano sobre su tabla, con la ruta, tres alfileres de entomólogo y un escarabajo clavado.
                val pl = Rect(c.x - 44f * s, c.y - 26f * s, c.x + 44f * s, c.y + 26f * s)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(pl.left + 1.5f * s, pl.top + 2f * s), pl.size)
                drawRect(k(PAPEL), pl.topLeft, pl.size)
                clipRect(pl.left, pl.top, pl.right, pl.bottom) {
                    val rio = Path().apply { moveTo(pl.left, pl.top + 30f * s); cubicTo(pl.left + 24f * s, pl.top + 20f * s, pl.left + 40f * s, pl.top + 44f * s, pl.right, pl.top + 36f * s) }
                    drawPath(rio, k(Color(0xFF6E9AA0)).copy(alpha = 0.7f), style = Stroke(2.4f * s))
                    for (j in 0 until 14) {
                        val p = Offset(pl.left + pl.width * rnd.nextFloat(), pl.top + pl.height * rnd.nextFloat())
                        drawCircle(k(HELECHO).copy(alpha = 0.3f), (1.5f + 1.5f * rnd.nextFloat()) * s, p)
                    }
                    for (j in 1 until 8) drawLine(k(TINTA).copy(alpha = 0.12f), Offset(pl.left + j * 11f * s, pl.top), Offset(pl.left + j * 11f * s, pl.bottom), 0.4f * s)
                    for (j in 1 until 5) drawLine(k(TINTA).copy(alpha = 0.12f), Offset(pl.left, pl.top + j * 10.4f * s), Offset(pl.right, pl.top + j * 10.4f * s), 0.4f * s)
                }
                drawRect(k(TINTA).copy(alpha = 0.6f), pl.topLeft, pl.size, style = Stroke(0.8f * s))
                drawRect(k(TINTA).copy(alpha = 0.4f), Offset(pl.left + 2f * s, pl.top + 2f * s), Size(pl.width - 4f * s, pl.height - 4f * s), style = Stroke(0.4f * s))
                val ms = listOf(Offset(pl.left + 18f * s, pl.top + 16f * s) to e.c1, Offset(pl.left + 46f * s, pl.top + 30f * s) to e.c2, Offset(pl.right - 18f * s, pl.top + 14f * s) to k(LATON))
                val ruta = Path().apply { moveTo(ms[0].first.x, ms[0].first.y); ms.drop(1).forEach { lineTo(it.first.x, it.first.y) } }
                drawPath(ruta, k(ROJO).copy(alpha = 0.75f), style = Stroke(0.9f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * s, 1.8f * s))))
                escarabajo(Offset(pl.left + 16f * s, pl.bottom - 12f * s), 9f * s, k(Color(0xFF2E7A5A)))
                drawCircle(k(TINTA), 0.9f * s, Offset(pl.left + 16f * s, pl.bottom - 12f * s))
                ms.forEach { (p, col) -> alfiler(p, 3.4f * s, col) }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el tablero de pliegos

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.NOTAS))
        val rnd = Random(e.semilla.toLong())
        val marco = Rect(0f, h * 0.085f, w, h * 0.925f)

        // El frontón tallado: un arco rebajado de roble con dos helechos y la placa oval de latón; los remates de bellota.
        val fw = minOf(w * 0.4f, 52f * u)
        val fr = Rect(w / 2 - fw / 2, 0.6f * u, w / 2 + fw / 2, marco.top + 2f * u)
        val arco = Path().apply { moveTo(fr.left, fr.bottom); lineTo(fr.left, fr.top + fr.height * 0.6f); cubicTo(fr.left + fw * 0.2f, fr.top, fr.right - fw * 0.2f, fr.top, fr.right, fr.top + fr.height * 0.6f); lineTo(fr.right, fr.bottom); close() }
        drawPath(arco, c(ROBLE_OSC))
        clipPath(arco) { madera(fr, ROBLE, oscuro, rnd, u) }
        drawPath(arco, Color.Black.copy(alpha = 0.35f), style = Stroke(0.4f * u))
        val pc = Offset(w / 2, fr.top + fr.height * 0.55f)
        listOf(-1f, 1f).forEach { d ->
            fronda(Offset(pc.x + d * 2f * u, pc.y + 2.6f * u), fw * 0.42f, if (d < 0) 190f else -10f, d * -25f, c(ROBLE_OSC), 0.45f * u, 11)
            fronda(Offset(pc.x + d * 2f * u, pc.y + 2.3f * u), fw * 0.42f, if (d < 0) 190f else -10f, d * -25f, c(ROBLE_LUZ).copy(alpha = 0.7f), 0.3f * u, 11)
        }
        val ow = 9f * u; val oh = minOf(fr.height * 0.6f, 5.6f * u)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(pc.x - ow / 2 + 0.3f * u, pc.y - oh / 2 + 0.5f * u), Size(ow, oh))
        drawOval(Brush.radialGradient(listOf(mezcla(LATON, Color.White, 0.35f), c(LATON), c(LATON_OSC)), Offset(pc.x - ow * 0.2f, pc.y - oh * 0.3f), ow * 0.7f), Offset(pc.x - ow / 2, pc.y - oh / 2), Size(ow, oh))
        drawOval(c(LATON_OSC), Offset(pc.x - ow * 0.36f, pc.y - oh * 0.32f), Size(ow * 0.72f, oh * 0.64f), style = Stroke(0.25f * u))
        hoja(Offset(pc.x - 2.2f * u, pc.y + 0.8f * u), 4.4f * u, 1.2f * u, -20f, c(LATON_OSC))
        listOf(2.8f * u, w - 2.8f * u).forEach { x ->
            val b = Offset(x, marco.top)
            drawOval(Brush.horizontalGradient(listOf(c(ROBLE_OSC), c(ROBLE_LUZ), c(ROBLE_OSC)), b.x - 1.8f * u, b.x + 1.8f * u), Offset(b.x - 1.8f * u, b.y - 4.6f * u), Size(3.6f * u, 4.2f * u))
            drawArc(c(VETA), 180f, 180f, true, Offset(b.x - 2.1f * u, b.y - 5.4f * u), Size(4.2f * u, 3.2f * u))
            drawCircle(c(VETA), 0.4f * u, Offset(b.x, b.y - 5.8f * u))
        }

        // El marco de roble: cuatro tablas con la veta a lo largo, ingletes, junquillo de luz y la orilla de latón.
        val iz = Rect(marco.left, marco.top, n.left, marco.bottom)
        val de = Rect(n.right, marco.top, marco.right, marco.bottom)
        val ar = Rect(marco.left, marco.top, marco.right, n.top)
        val ab = Rect(marco.left, n.bottom, marco.right, marco.bottom)
        fun trapecio(a: Offset, b: Offset, cc: Offset, d: Offset) = Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(cc.x, cc.y); lineTo(d.x, d.y); close() }
        val tA = trapecio(ar.topLeft, ar.topRight, Offset(n.right, n.top), Offset(n.left, n.top))
        val tB = trapecio(ab.bottomLeft, ab.bottomRight, Offset(n.right, n.bottom), Offset(n.left, n.bottom))
        val tI = trapecio(iz.topLeft, Offset(n.left, n.top), Offset(n.left, n.bottom), iz.bottomLeft)
        val tD = trapecio(de.topRight, Offset(n.right, n.top), Offset(n.right, n.bottom), de.bottomRight)
        clipPath(tA) { madera(ar, ROBLE, oscuro, rnd, u, nudos = 1) }
        clipPath(tB) { madera(ab, ROBLE, oscuro, rnd, u) }
        clipPath(tI) { madera(iz, ROBLE, oscuro, rnd, u, vertical = true) }
        clipPath(tD) { madera(de, ROBLE, oscuro, rnd, u, vertical = true, nudos = 1) }
        drawPath(tA, Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f))
        drawPath(tD, Color.Black.copy(alpha = 0.1f))
        drawPath(tB, Color.Black.copy(alpha = 0.14f))
        listOf(marco.topLeft to Offset(n.left, n.top), marco.topRight to Offset(n.right, n.top), marco.bottomLeft to Offset(n.left, n.bottom), marco.bottomRight to Offset(n.right, n.bottom)).forEach { (a, b) -> drawLine(Color.Black.copy(alpha = 0.35f), a, b, 0.3f * u) }
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.22f), Offset(marco.left + 1.2f * u, marco.top + 1.2f * u), Size(marco.width - 2.4f * u, marco.height - 2.4f * u), style = Stroke(0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left + 1.8f * u, marco.top + 1.8f * u), Size(marco.width - 3.6f * u, marco.height - 3.6f * u), style = Stroke(0.3f * u))
        drawRect(Brush.verticalGradient(listOf(mezcla(c(LATON), Color.White, 0.3f), c(LATON_OSC)), n.top - 1f * u, n.bottom + 1f * u), Offset(n.left - 1f * u, n.top - 1f * u), Size(n.width + 2f * u, n.height + 2f * u))

        // Las escuadras de hierro colado en las esquinas del marco, con su voluta y su tornillo.
        listOf(marco.topLeft to Offset(1f, 1f), marco.topRight to Offset(-1f, 1f), marco.bottomLeft to Offset(1f, -1f), marco.bottomRight to Offset(-1f, -1f)).forEach { (q, d) ->
            val l = minOf(n.left - marco.left, 9f * u) * 0.95f
            val esc = Path().apply { moveTo(q.x, q.y); lineTo(q.x + d.x * l, q.y); quadraticTo(q.x + d.x * l * 0.45f, q.y + d.y * l * 0.45f, q.x, q.y + d.y * l); close() }
            drawPath(esc, Color.Black.copy(alpha = 0.35f))
            drawPath(esc, c(HIERRO))
            drawPath(esc, c(HIERRO_LUZ), style = Stroke(0.3f * u))
            drawCircle(c(HIERRO_LUZ), l * 0.16f, Offset(q.x + d.x * l * 0.36f, q.y + d.y * l * 0.36f), style = Stroke(0.35f * u))
            drawCircle(c(LATON), 0.45f * u, Offset(q.x + d.x * l * 0.2f, q.y + d.y * l * 0.2f))
        }

        // El nicho: lino crudo sobre el tablero, con su trama apenas visible y la sombra del marco.
        drawRect(nicho(e.fondo, LINO, oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val trama = Color.Black.copy(alpha = if (oscuro) 0.06f else 0.03f)
            var ty = n.top; while (ty < n.bottom) { drawLine(trama, Offset(n.left, ty), Offset(n.right, ty), 0.2f * u); ty += 1.2f * u }
            var tx = n.left; while (tx < n.right) { drawLine(trama, Offset(tx, n.top), Offset(tx, n.bottom), 0.2f * u); tx += 1.2f * u }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Color.Transparent), n.top, n.top + 3f * u), n.topLeft, Size(n.width, 3f * u))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), n.left, n.left + 2.4f * u), n.topLeft, Size(2.4f * u, n.height))

        // La repisa volada con su moldura y, encima, el lápiz, la lupa y un plantón en su maceta.
        val rep = Rect(0f, marco.bottom, w, h)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(0f, rep.top), Size(w, 1f * u))
        madera(Rect(0f, rep.top, w, rep.top + 2.6f * u), ROBLE_LUZ, oscuro, rnd, u)
        moldura(Rect(0f, rep.top + 2.6f * u, w, h), ROBLE, oscuro)
        val ly = rep.top + 0.4f * u
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(w * 0.14f, ly + 0.6f * u), Offset(w * 0.36f, ly + 0.6f * u), 1.4f * u, StrokeCap.Round)
        drawLine(c(Color(0xFF4F6A3A)), Offset(w * 0.15f, ly - 0.6f * u), Offset(w * 0.33f, ly - 0.6f * u), 1.4f * u)
        drawLine(Color.White.copy(alpha = 0.25f), Offset(w * 0.15f, ly - 0.9f * u), Offset(w * 0.33f, ly - 0.9f * u), 0.3f * u)
        drawLine(c(LATON), Offset(w * 0.33f, ly - 0.6f * u), Offset(w * 0.345f, ly - 0.6f * u), 1.4f * u)
        drawPath(Path().apply { moveTo(w * 0.15f, ly - 1.3f * u); lineTo(w * 0.15f, ly + 0.1f * u); lineTo(w * 0.15f - 2f * u, ly - 0.6f * u); close() }, c(Color(0xFFE2C9A0)))
        val lu = Offset(w * 0.62f, ly - 1.2f * u)
        drawLine(c(ROBLE_OSC), Offset(lu.x + 3f * u, lu.y), Offset(lu.x + 7f * u, lu.y), 1.1f * u, StrokeCap.Round)
        drawOval(c(LATON), Offset(lu.x - 3f * u, lu.y - 1f * u), Size(6f * u, 2f * u))
        drawOval(c(VIDRIO).copy(alpha = 0.6f), Offset(lu.x - 2.4f * u, lu.y - 0.6f * u), Size(4.8f * u, 1.2f * u))
        val mp = Offset(w * 0.86f, rep.top + 0.6f * u)
        listOf(-120f, -95f, -70f, -45f).forEachIndexed { k, a -> fronda(Offset(mp.x, mp.y - 4f * u), 5.6f * u, a, if (a < -90f) -25f else 25f, c(if (k % 2 == 0) HELECHO else HOJA_CLARA), 0.35f * u, 9) }
        maceta(mp, 5f * u, 4.4f * u, oscuro)
        if (oscuro) listOf(0.04f to 0.04f, 0.3f to 0.05f, 0.7f to 0.03f, 0.96f to 0.06f).forEach { (fx, fy) -> luciernaga(Offset(w * fx, h * fy), 0.45f * u) }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        val u = minOf(r0.width, r0.height) / 100f
        val r = Rect(r0.left + r0.width * 0.035f, r0.top + r0.height * 0.045f, r0.right - r0.width * 0.035f, r0.bottom - r0.height * 0.035f)
        val gir = if (e.fijada) 0f else ((e.indice * 37) % 5 - 2) * 0.55f
        fun p(x: Color) = if (oscuro) mezcla(x, NOCHE, 0.34f) else x
        fun k(x: Color) = t(x, oscuro)
        val papel = p(when (e.tipo) {
            "pensamiento" -> KRAFT
            "proposito" -> Color(0xFFEFE8D2)
            "recordatorio" -> Color(0xFFE6DABA)
            else -> PAPEL
        })
        rotate(gir, r.center) {
            val forma = Path().apply {
                if (e.tipo == "recordatorio") {
                    // La etiqueta de cartón con las esquinas de arriba cortadas.
                    val cc = r.width * 0.12f
                    moveTo(r.left + cc, r.top); lineTo(r.right - cc, r.top); lineTo(r.right, r.top + cc); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.top + cc); close()
                } else addRoundRect(RoundRect(r, CornerRadius(0.8f * u)))
            }
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 1.4f * u, r.top + 2.2f * u), r.size)
            drawPath(forma, papel)
            clipPath(forma) {
                // Una mancha suave de luz y la fibra del papel.
                drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.06f)), r.topLeft, r.bottomRight), r.topLeft, r.size)
                when (e.tipo) {
                    "pensamiento" -> {
                        // El sobre de semillas: la solapa de arriba con su doblez y la costura de los lados.
                        val sol = Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + r.height * 0.05f); lineTo(r.center.x, r.top + r.height * 0.13f); lineTo(r.left, r.top + r.height * 0.05f); close() }
                        drawPath(sol, mezcla(papel, Color.Black, 0.12f))
                        drawPath(Path().apply { moveTo(r.left, r.top + r.height * 0.05f); lineTo(r.center.x, r.top + r.height * 0.13f); lineTo(r.right, r.top + r.height * 0.05f) }, Color.Black.copy(alpha = 0.2f), style = Stroke(0.4f * u))
                        drawLine(Color.Black.copy(alpha = 0.12f), Offset(r.left + r.width * 0.05f, r.top + r.height * 0.1f), Offset(r.left + r.width * 0.05f, r.bottom), 0.4f * u)
                        drawLine(Color.Black.copy(alpha = 0.12f), Offset(r.right - r.width * 0.05f, r.top + r.height * 0.1f), Offset(r.right - r.width * 0.05f, r.bottom), 0.4f * u)
                    }
                    "proposito" -> {
                        // La etiqueta de herbario: la banda verde de arriba con su filete dorado.
                        drawRect(k(mezcla(HELECHO, e.acento, 0.3f)), r.topLeft, Size(r.width, r.height * 0.09f))
                        drawLine(k(LATON), Offset(r.left, r.top + r.height * 0.1f), Offset(r.right, r.top + r.height * 0.1f), 0.5f * u)
                        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + r.width * 0.04f, r.top + r.height * 0.13f), Size(r.width * 0.92f, r.height * 0.83f), style = Stroke(0.3f * u))
                    }
                    "recordatorio" -> {
                        // El ojal reforzado y la orilla de color.
                        drawRect(k(mezcla(e.acento, TERRACOTA, 0.5f)).copy(alpha = 0.85f), Offset(r.left, r.bottom - r.height * 0.06f), Size(r.width, r.height * 0.06f))
                    }
                    else -> {
                        // El pliego: doble filete impreso.
                        drawRect(k(TINTA).copy(alpha = 0.45f), Offset(r.left + r.width * 0.045f, r.top + r.height * 0.05f), Size(r.width * 0.91f, r.height * 0.9f), style = Stroke(0.45f * u))
                        drawRect(k(TINTA).copy(alpha = 0.25f), Offset(r.left + r.width * 0.065f, r.top + r.height * 0.07f), Size(r.width * 0.87f, r.height * 0.86f), style = Stroke(0.25f * u))
                    }
                }
            }
            drawPath(forma, Color.Black.copy(alpha = 0.22f), style = Stroke(0.35f * u))
            when (e.tipo) {
                "idea" -> fronda(Offset(r.right - r.width * 0.09f, r.bottom - r.height * 0.07f), r.height * 0.14f, -140f, 25f, k(HELECHO).copy(alpha = 0.8f), 0.6f * u, 9)
                "proposito" -> {
                    val cc = Offset(r.right - r.width * 0.12f, r.bottom - r.height * 0.08f)
                    drawCircle(k(HELECHO).copy(alpha = 0.7f), r.height * 0.035f, cc, style = Stroke(0.5f * u))
                    drawPath(Path().apply { moveTo(cc.x - r.height * 0.018f, cc.y); lineTo(cc.x - r.height * 0.004f, cc.y + r.height * 0.016f); lineTo(cc.x + r.height * 0.024f, cc.y - r.height * 0.022f) }, k(HELECHO), style = Stroke(0.6f * u))
                }
                "recordatorio" -> {
                    // El ojal con su arandela y el cordel que sube.
                    val oj = Offset(r.center.x, r.top + r.height * 0.075f)
                    drawCircle(k(LATON), r.width * 0.045f, oj)
                    drawCircle(mezcla(k(LATON), Color.White, 0.3f), r.width * 0.045f, oj, style = Stroke(0.3f * u))
                    drawCircle(Color.Black.copy(alpha = 0.55f), r.width * 0.022f, oj)
                    val cordel = Path().apply { moveTo(oj.x, oj.y); cubicTo(oj.x - r.width * 0.12f, oj.y - r.height * 0.02f, oj.x - r.width * 0.08f, r0.top + 1f * u, oj.x - r.width * 0.02f, r0.top + 0.5f * u) }
                    drawPath(cordel, k(Color(0xFFB39A70)), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                    drawPath(Path().apply { moveTo(oj.x, oj.y); cubicTo(oj.x + r.width * 0.1f, oj.y - r.height * 0.02f, oj.x + r.width * 0.06f, r0.top + 1f * u, oj.x + r.width * 0.02f, r0.top + 0.5f * u) }, k(Color(0xFFB39A70)), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                }
                else -> {}
            }
            if (e.fijada) {
                // La flor prensada en la esquina y el alfiler de cabeza roja.
                val fc = Offset(r.left + r.width * 0.07f, r.top + r.height * 0.07f)
                hoja(fc, r.width * 0.12f, r.width * 0.03f, 50f, k(HELECHO).copy(alpha = 0.9f))
                hoja(fc, r.width * 0.1f, r.width * 0.028f, 15f, k(HOJA_CLARA).copy(alpha = 0.9f))
                for (j in 0 until 5) {
                    val a = Math.toRadians(-90.0 + j * 72.0)
                    drawCircle(k(FLOR), r.width * 0.026f, Offset(fc.x + r.width * 0.03f * cos(a).toFloat(), fc.y + r.width * 0.03f * sin(a).toFloat()))
                }
                drawCircle(k(Color(0xFFE0B84A)), r.width * 0.017f, fc)
                if (e.tipo != "recordatorio") alfiler(Offset(r.center.x, r.top + r.height * 0.045f), 2.4f * u, k(ROJO))
            } else if (e.tipo != "recordatorio") {
                alfiler(Offset(r.center.x, r.top + r.height * 0.045f), 1.7f * u, if (e.tipo == "pensamiento") k(LATON) else k(Color(0xFF26221C)))
            }
        }
    }

    /** El texto de las notas: tinta sepia del naturalista sobre el papel; el papel se oscurece poco de noche y la tinta sigue. */
    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFF15110A) else TINTA
}
