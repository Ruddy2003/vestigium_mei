package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Horror cósmico: los muebles de Vida y de Diario del archivo de un pueblo costero, de la misma casa que sus cajas de
 * legajos del Ideario, su librería de puertas de vidrio y su vitrina de hallazgos: roble oscuro con veta, latón gastado,
 * papel de legajo amarillento con cercos de té, tinta verde-negra, lacre, bramante, moho, la niebla, el faro y un verde raro
 * que brilla de noche.
 * - Social: la sala de la Sociedad Histórica: el papel pintado verde con sus manchas de humedad, la cornisa con dentículos y
 *   la moldura de cuadros, el gran árbol genealógico del pueblo (el nicho: papel de legajo de día, verde de niebla de noche)
 *   en su marco de roble con filete de latón, colgado de dos cordones, y su sello del ojo; a un lado la ventana con la niebla
 *   y el faro que barre la noche, al otro el retrato ovalado en sepia; dos quinqués de pared encendidos, el friso de paneles
 *   y el piso de tablas.
 * - Mapas: la mesa de cartas del farero vista desde arriba: el tablero de roble con su canto, el tapete verde con tachuelas
 *   de latón y la carta náutica (el nicho) clavada con chinchetas; a un lado la brújula en su caja, la lupa y un cerco de
 *   café; al otro el quinqué encendido, el atado de cartas con su bramante y su lacre y el frasco con algo que brilla; al
 *   frente, los cajones con sus tiradores de concha y sus portaetiquetas, y las patas torneadas.
 * - Otras vidas: la vitrina del museo del pueblo: la cornisa con dentículos y el sello del ojo, encima los frascos de
 *   especímenes, los legajos y una telaraña; cinco compartimentos forrados (el nicho) entre columnillas torneadas, el
 *   listón de portaetiquetas de latón, las puertas de vidrio abiertas a los lados y el zócalo de cajones.
 * - Íconos: el faro sobre las rocas con su haz y la niebla; la carta que una moneda vieja raspa y deja ver la tinta del
 *   abismo; tres chinchetas de latón unidas por el hilo rojo sobre el mapa de la costa.
 * - Notas: el tablón de corcho del archivo: el marco de roble con cantoneras de latón, el copete con su portaetiquetas, el
 *   corcho (el nicho) con sus agujeros viejos y el hilo rojo que une las chinchetas, y la canaleta con la lupa, el lápiz, la
 *   llave y el cabo de vela. Las notas son papeles distintos según el tipo (la ficha de catálogo, el recorte de periódico, la
 *   etiqueta de manila con su ojal, el telegrama con su clip), clavados con una chincheta; si están fijados, con el alfiler
 *   grande del hilo rojo y el sello de lacre del ojo.
 */
internal object VidaArcano : DisenoVida {
    // ---------------------------------------------------------------- materiales (los de sus cajas de legajos, su librería y su vitrina)
    private val ROBLE = Color(0xFF3B2A1C)
    private val ROBLE_LUZ = Color(0xFF5E4630)
    private val ROBLE_OSC = Color(0xFF22170F)
    private val VETA = Color(0xFF1A110A)
    private val LATON = Color(0xFFB8963E)
    private val LATON_LUZ = Color(0xFFE2C67A)
    private val LATON_OSC = Color(0xFF6E5622)
    private val PAPEL = Color(0xFFE8DFC8)
    private val PAPEL_VIEJO = Color(0xFFD6C7A0)
    private val FICHA = Color(0xFFF1EADA)
    private val MANILA = Color(0xFFE2CFA0)
    private val PERIODICO = Color(0xFFDAD6C8)
    private val TELEGRAMA = Color(0xFFEDE0B0)
    private val TE = Color(0xFF9C7A48)
    private val TINTA = Color(0xFF1E3329)
    private val LACRE = Color(0xFF7A2C24)
    private val HILO = Color(0xFFA8322A)
    private val CORDEL = Color(0xFFC9B58A)
    private val MOHO = Color(0xFF4A6B5C)
    private val VERDE_RARO = Color(0xFFB8E07A)
    private val NIEBLA = Color(0xFFDCE3DE)
    private val MAR = Color(0xFF2E4A44)
    private val TAPETE = Color(0xFF2E4A3A)
    private val CORCHO = Color(0xFFC4A57A)
    private val TERCIOPELO = Color(0xFF2F4A3E)
    private val PINTADO = Color(0xFFCFC7A6)
    private val PINTADO_OSC = Color(0xFF223029)
    private val VIDRIO = Color(0xFF9FB4B0)
    private val CERA = Color(0xFFEDE3C8)
    private val LLAMA = Color(0xFFF2C46B)
    private val RAYADO = Color(0xFF8AA0B8)
    private val MARGEN_ROJO = Color(0xFFB0473A)
    private val TELARANA = Color(0xFFDDD8CC)
    private val NOCHE = Color(0xFF0A0F0D)

    /** El material de día o de noche: de noche, en la penumbra húmeda del archivo. */
    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.42f): Color = if (oscuro) mezcla(c, NOCHE, cuanto) else c

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.14f, 0.16f, 0.86f, 0.8f)
        MuebleVida.MAPAS -> Rect(0.18f, 0.08f, 0.82f, 0.7f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.085f, 0.25f, 0.915f, 0.8f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.16f, 0.925f, 0.875f)
    }

    // ---------------------------------------------------------------- ayudas de dibujo

    /** Un rectángulo dado en fracciones del lienzo, en píxeles. */
    private fun DrawScope.caja(r: Rect) = Rect(size.width * r.left, size.height * r.top, size.width * r.right, size.height * r.bottom)

    private fun Rect.crece(d: Float) = Rect(left - d, top - d, right + d, bottom + d)

    private fun DrawScope.rect(c: Color, r: Rect) = drawRect(c, r.topLeft, r.size)

    /** Un número estable de 0 a 1 para lo que varía (vetas, manchas, agujeros). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Vetas de madera: líneas onduladas a lo largo, separadas ≈ [paso], de opacidad y grosor variables. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, paso: Float, vertical: Boolean = false, grosor: Float = 1f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / paso).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = paso * (0.15f + 0.45f * azar(semilla, k + 100))
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.2f + 2.2f * azar(semilla, k + 300)
                val p = Path()
                for (s in 0..16) {
                    val a = largo * s / 16
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16)
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(grosor * (0.5f + azar(semilla, k + 500))))
            }
        }
    }

    /** Una pieza de roble oscuro con su veta, luz arriba y sombra abajo. */
    private fun DrawScope.roble(r: Rect, oscuro: Boolean, semilla: Int, u: Float, base: Color = ROBLE, vertical: Boolean = false) {
        rect(t(base, oscuro), r)
        vetas(r, t(VETA, oscuro).copy(alpha = 0.55f), semilla, 1.1f * u, vertical, 0.3f * u)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), r.topLeft, Size(r.width, 0.3f * u))
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(r.left, r.bottom - 0.3f * u), Size(r.width, 0.3f * u))
    }

    /** Un bisel: luz arriba e izquierda, sombra abajo y derecha. */
    private fun DrawScope.bisel(r: Rect, luz: Color, sombra: Color, ancho: Float) {
        drawRect(luz, r.topLeft, Size(r.width, ancho))
        drawRect(luz.copy(alpha = luz.alpha * 0.7f), r.topLeft, Size(ancho, r.height))
        drawRect(sombra, Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(sombra.copy(alpha = sombra.alpha * 0.8f), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    /** Una mancha radial suave (luz de la llama, humedad, moho). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Un cerco de té o de café: el anillo más oscuro en el borde y algo de tinte dentro. */
    private fun DrawScope.cerco(c: Offset, r: Float, color: Color, u: Float) {
        drawCircle(color.copy(alpha = color.alpha * 0.35f), r, c)
        drawArc(color, 20f, 290f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(0.5f * u))
        drawArc(color.copy(alpha = color.alpha * 0.6f), 330f, 40f, false, Offset(c.x - r * 0.96f, c.y - r * 0.96f), Size(1.92f * r, 1.92f * r), style = Stroke(0.3f * u))
    }

    /** Un remache o una tachuela de cabeza redonda, con su brillo. */
    private fun DrawScope.tachuela(c: Offset, r: Float, metal: Color = LATON, luz: Color = LATON_LUZ) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(r * 0.35f, r * 0.45f))
        drawCircle(metal, r, c)
        drawCircle(luz, r * 0.42f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una chincheta vista desde arriba: la cabeza de color con su brillo y su sombra. */
    private fun DrawScope.chincheta(c: Offset, r: Float, color: Color) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(r * 0.5f, r * 0.7f))
        drawCircle(mezcla(color, Color.Black, 0.35f), r, c)
        drawCircle(color, r * 0.8f, c - Offset(r * 0.08f, r * 0.08f))
        drawCircle(Color.White.copy(alpha = 0.45f), r * 0.28f, c - Offset(r * 0.32f, r * 0.32f))
    }

    /** El papel pintado: listas verticales de dos verdes, un motivo de rombos con su punto y las manchas de humedad que bajan. */
    private fun DrawScope.papelPintado(r: Rect, oscuro: Boolean, semilla: Int, u: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            val base = if (oscuro) PINTADO_OSC else PINTADO
            rect(base, r)
            val lista = 6f * u
            var x = r.left; var k = 0
            while (x < r.right) {
                if (k % 2 == 0) drawRect(Color.Black.copy(alpha = if (oscuro) 0.08f else 0.05f), Offset(x, r.top), Size(lista, r.height))
                drawLine((if (oscuro) MOHO else MOHO).copy(alpha = if (oscuro) 0.25f else 0.18f), Offset(x, r.top), Offset(x, r.bottom), 0.25f * u)
                x += lista; k++
            }
            // El motivo: un rombo pequeño con su punto, al tresbolillo.
            val motivo = (if (oscuro) Color(0xFF3A4A40) else Color(0xFFB0A884))
            var y = r.top + 3f * u; var f = 0
            while (y < r.bottom) {
                var mx = r.left + (if (f % 2 == 0) lista * 0.5f else lista * 1.5f)
                while (mx < r.right) {
                    drawPath(Path().apply { moveTo(mx, y - 1.2f * u); lineTo(mx + 0.8f * u, y); lineTo(mx, y + 1.2f * u); lineTo(mx - 0.8f * u, y); close() }, motivo)
                    drawCircle(motivo, 0.3f * u, Offset(mx, y - 2.2f * u))
                    mx += lista * 2
                }
                y += 4.5f * u; f++
            }
            // Las manchas de humedad que bajan del techo.
            for (j in 0 until 5) {
                val c = Offset(r.left + r.width * azar(semilla, j), r.top + r.height * (0.05f + 0.35f * azar(semilla, j + 10)))
                val rr = (8f + 14f * azar(semilla, j + 20)) * u
                halo(c, rr, (if (oscuro) MOHO else TE).copy(alpha = if (oscuro) 0.22f else 0.2f))
                drawLine((if (oscuro) MOHO else TE).copy(alpha = 0.12f), c, c + Offset(0.5f * u, rr * 1.6f), 2.4f * u, StrokeCap.Round)
            }
        }
    }

    /** El piso de tablas de roble, con sus juntas. */
    private fun DrawScope.tablas(r: Rect, oscuro: Boolean, semilla: Int, u: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            val alto = 3.2f * u
            var y = r.top; var f = 0
            while (y < r.bottom) {
                val tb = Rect(r.left, y, r.right, y + alto)
                rect(t(mezcla(ROBLE, ROBLE_LUZ, 0.3f * azar(semilla, f)), oscuro), tb)
                vetas(tb, t(VETA, oscuro).copy(alpha = 0.5f), semilla + f, 1f * u, grosor = 0.25f * u)
                drawLine(Color.Black.copy(alpha = 0.5f), Offset(r.left, y), Offset(r.right, y), 0.3f * u)
                var x = r.left + r.width * azar(semilla, f + 30) * 0.5f
                while (x < r.right) { drawLine(Color.Black.copy(alpha = 0.45f), Offset(x, y), Offset(x, y + alto), 0.3f * u); x += r.width * 0.45f }
                y += alto; f++
            }
        }
    }

    /** El sello del ojo: un hexágono con el ojo almendrado y su pupila, en [color]. */
    private fun DrawScope.ojo(c: Offset, r: Float, color: Color, fondo: Color, trazo: Float) {
        val hex = Path().apply {
            for (k in 0 until 6) {
                val a = Math.toRadians(-90.0 + k * 60.0)
                val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
                if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(hex, fondo)
        drawPath(hex, color, style = Stroke(trazo))
        val almendra = Path().apply {
            moveTo(c.x - r * 0.62f, c.y)
            quadraticTo(c.x, c.y - r * 0.6f, c.x + r * 0.62f, c.y)
            quadraticTo(c.x, c.y + r * 0.6f, c.x - r * 0.62f, c.y)
            close()
        }
        drawPath(almendra, color, style = Stroke(trazo))
        drawCircle(color, r * 0.18f, c)
        // Las pestañas como rayos de abajo, que parecen tentáculos.
        for (k in -1..1) drawLine(color, Offset(c.x + k * r * 0.25f, c.y + r * 0.3f), Offset(c.x + k * r * 0.35f, c.y + r * 0.6f), trazo * 0.8f, StrokeCap.Round)
    }

    /** Una llama de mecha vista de frente, con su halo. */
    private fun DrawScope.llama(base: Offset, alto: Float, oscuro: Boolean) {
        halo(base - Offset(0f, alto * 0.5f), alto * 2.6f, LLAMA.copy(alpha = if (oscuro) 0.45f else 0.28f))
        val p = Path().apply {
            moveTo(base.x, base.y - alto)
            quadraticTo(base.x + alto * 0.42f, base.y - alto * 0.3f, base.x, base.y)
            quadraticTo(base.x - alto * 0.42f, base.y - alto * 0.3f, base.x, base.y - alto)
            close()
        }
        drawPath(p, Color(0xFFF5A62E))
        drawOval(Color(0xFFFFF3C4), Offset(base.x - alto * 0.12f, base.y - alto * 0.55f), Size(alto * 0.24f, alto * 0.45f))
    }

    /** El quinqué de pared: el brazo de latón, el depósito, la llama y el tubo de vidrio; [c] es el centro de la llama. */
    private fun DrawScope.quinque(c: Offset, a: Float, pared: Float, oscuro: Boolean, u: Float) {
        halo(c, a * 7f, LLAMA.copy(alpha = if (oscuro) 0.3f else 0.16f))
        val laton = t(LATON, oscuro, 0.2f); val luz = t(LATON_LUZ, oscuro, 0.2f); val osc = t(LATON_OSC, oscuro, 0.2f)
        // El brazo hasta la pared, con su roseta.
        val yb = c.y + a * 2.6f
        drawLine(osc, Offset(pared, yb + a * 0.6f), Offset(c.x, yb + a * 0.6f), a * 0.32f)
        drawLine(osc, Offset(pared, yb - a * 0.6f), Offset(c.x - (c.x - pared) * 0.3f, yb + a * 0.6f), a * 0.22f)
        drawOval(laton, Offset(pared - a * 0.45f, yb - a * 1.2f), Size(a * 0.9f, a * 2.6f))
        // El depósito de latón.
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - a * 1.1f + 0.3f * u, yb - a * 0.3f + 0.4f * u), Size(a * 2.2f, a * 1.5f))
        drawOval(laton, Offset(c.x - a * 1.1f, yb - a * 0.3f), Size(a * 2.2f, a * 1.5f))
        drawOval(luz, Offset(c.x - a * 0.7f, yb - a * 0.1f), Size(a * 0.6f, a * 0.4f))
        drawRect(osc, Offset(c.x - a * 0.5f, yb - a * 0.7f), Size(a, a * 0.5f))
        // La llama y el tubo de vidrio.
        llama(c + Offset(0f, a * 0.9f), a * 1.5f, oscuro)
        val tubo = Path().apply {
            moveTo(c.x - a * 0.55f, yb - a * 0.7f); lineTo(c.x - a * 0.55f, c.y + a * 0.4f)
            quadraticTo(c.x - a * 0.85f, c.y - a * 0.2f, c.x - a * 0.4f, c.y - a * 1.4f); lineTo(c.x - a * 0.4f, c.y - a * 2.4f)
            lineTo(c.x + a * 0.4f, c.y - a * 2.4f); lineTo(c.x + a * 0.4f, c.y - a * 1.4f)
            quadraticTo(c.x + a * 0.85f, c.y - a * 0.2f, c.x + a * 0.55f, c.y + a * 0.4f); lineTo(c.x + a * 0.55f, yb - a * 0.7f); close()
        }
        drawPath(tubo, VIDRIO.copy(alpha = 0.22f))
        drawPath(tubo, Color.White.copy(alpha = 0.5f), style = Stroke(0.2f * u))
        drawLine(Color.White.copy(alpha = 0.55f), Offset(c.x - a * 0.28f, c.y - a * 2.2f), Offset(c.x - a * 0.28f, c.y - a * 1.5f), 0.25f * u)
    }

    /** Una telaraña en la esquina [c], abierta hacia [dx], [dy] (±1). */
    private fun DrawScope.telarana(c: Offset, r: Float, dx: Float, dy: Float, oscuro: Boolean, u: Float) {
        val col = TELARANA.copy(alpha = if (oscuro) 0.35f else 0.55f)
        val rayos = 5
        val puntos = (0 until rayos).map { k -> val a = Math.toRadians(k * 90.0 / (rayos - 1)); Offset(cos(a).toFloat() * dx, sin(a).toFloat() * dy) }
        puntos.forEach { d -> drawLine(col, c, c + d * r, 0.2f * u) }
        for (anillo in 1..4) {
            val f = anillo / 4.5f
            val p = Path()
            puntos.forEachIndexed { k, d -> val q = c + d * (r * f); if (k == 0) p.moveTo(q.x, q.y) else p.quadraticTo(c.x + (q.x - c.x) * 0.8f, c.y + (q.y - c.y) * 0.8f, q.x, q.y) }
            drawPath(p, col, style = Stroke(0.18f * u))
        }
    }

    /** Un frasco de especímenes de frente: el vidrio, el líquido verde, la cosa enroscada que brilla y la tapa de latón. */
    private fun DrawScope.frasco(r: Rect, oscuro: Boolean, u: Float, semilla: Int) {
        if (oscuro) halo(r.center, r.width * 1.4f, VERDE_RARO.copy(alpha = 0.22f))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 0.4f * u, r.top + 0.6f * u), r.size, CornerRadius(r.width * 0.18f))
        val liquido = Rect(r.left + r.width * 0.06f, r.top + r.height * 0.28f, r.right - r.width * 0.06f, r.bottom - r.height * 0.04f)
        drawRoundRect(VIDRIO.copy(alpha = 0.35f), r.topLeft, r.size, CornerRadius(r.width * 0.18f))
        drawRoundRect(t(Color(0xFF3E6A50), oscuro, 0.15f), liquido.topLeft, liquido.size, CornerRadius(r.width * 0.14f))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(liquido.left, liquido.top), Size(liquido.width, 0.4f * u))
        // La cosa enroscada.
        val c = Offset(liquido.center.x, liquido.center.y + liquido.height * 0.05f)
        val bicho = if (oscuro) VERDE_RARO else Color(0xFFA9C98A)
        val p = Path().apply {
            moveTo(c.x - liquido.width * 0.2f, liquido.bottom - 0.6f * u)
            cubicTo(c.x - liquido.width * 0.35f, c.y, c.x + liquido.width * (0.2f + 0.1f * azar(semilla, 1)), c.y - liquido.height * 0.35f, c.x + liquido.width * 0.05f, c.y - liquido.height * 0.05f)
            cubicTo(c.x - liquido.width * 0.12f, c.y + liquido.height * 0.12f, c.x + liquido.width * 0.15f, c.y + liquido.height * 0.15f, c.x + liquido.width * 0.12f, c.y)
        }
        drawPath(p, bicho.copy(alpha = 0.85f), style = Stroke(r.width * 0.09f, cap = StrokeCap.Round))
        drawCircle(bicho, r.width * 0.07f, Offset(c.x + liquido.width * 0.12f, c.y))
        // El brillo del vidrio, el cuello y la tapa.
        drawRoundRect(Color.White.copy(alpha = 0.6f), r.topLeft, r.size, CornerRadius(r.width * 0.18f), style = Stroke(0.25f * u))
        drawLine(Color.White.copy(alpha = 0.55f), Offset(r.left + r.width * 0.2f, r.top + r.height * 0.2f), Offset(r.left + r.width * 0.2f, r.bottom - r.height * 0.15f), r.width * 0.07f, StrokeCap.Round)
        val tapa = Rect(r.left - r.width * 0.04f, r.top - r.height * 0.1f, r.right + r.width * 0.04f, r.top + r.height * 0.04f)
        drawRoundRect(t(LATON, oscuro, 0.2f), tapa.topLeft, tapa.size, CornerRadius(0.4f * u))
        drawRect(t(LATON_LUZ, oscuro, 0.2f), Offset(tapa.left + 0.4f * u, tapa.top + tapa.height * 0.2f), Size(tapa.width - 0.8f * u, tapa.height * 0.2f))
        drawLine(t(CORDEL, oscuro, 0.2f), Offset(r.left, r.top + r.height * 0.1f), Offset(r.right, r.top + r.height * 0.1f), 0.35f * u)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.SOCIAL))
        // El papel pintado, el friso de paneles y el piso de tablas.
        papelPintado(Rect(0f, 0f, w, h), oscuro, e.semilla, u)
        val friso = h * 0.85f; val piso = h * 0.955f
        tablas(Rect(0f, piso, w, h), oscuro, e.semilla + 3, u)
        roble(Rect(0f, friso, w, piso), oscuro, e.semilla + 4, u, ROBLE)
        val nP = (w / (16f * u)).toInt().coerceAtLeast(5)
        for (j in 0 until nP) {
            val p = Rect(w * j / nP + 1.6f * u, friso + 2.2f * u, w * (j + 1) / nP - 1.6f * u, piso - 1.6f * u)
            rect(t(ROBLE_OSC, oscuro), p)
            bisel(p, Color.Black.copy(alpha = 0.35f), Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), 0.4f * u)
            val q = p.crece(-1f * u)
            roble(q, oscuro, e.semilla + 40 + j, u, ROBLE)
        }
        val moldura = Rect(0f, friso - 0.4f * u, w, friso + 1.4f * u)
        roble(moldura, oscuro, e.semilla + 5, u, ROBLE_LUZ)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, moldura.bottom), Size(w, 0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(0f, piso), Size(w, 0.8f * u))
        // La cornisa con dentículos y la moldura de cuadros.
        val cornisa = Rect(0f, 0f, w, h * 0.055f)
        roble(cornisa, oscuro, e.semilla + 6, u, ROBLE)
        var dx = 0.6f * u
        while (dx < w) { drawRect(t(ROBLE_LUZ, oscuro), Offset(dx, cornisa.bottom - 2.4f * u), Size(1.2f * u, 1.8f * u)); dx += 2.2f * u }
        drawRect(t(ROBLE_OSC, oscuro), Offset(0f, cornisa.bottom - 0.6f * u), Size(w, 0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, cornisa.bottom), Size(w, 1.2f * u))
        val riel = Rect(0f, h * 0.085f, w, h * 0.085f + 1.2f * u)
        roble(riel, oscuro, e.semilla + 7, u, ROBLE_LUZ)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, riel.bottom), Size(w, 0.5f * u))
        // La ventana con la niebla y el faro, a la izquierda; el retrato ovalado, a la derecha.
        val lado = n.left - 3.2f * u
        val vw = minOf(lado * 0.5f, 9f * u)
        val vent = Rect(lado * 0.1f, h * 0.2f, lado * 0.1f + vw, h * 0.72f)
        ventana(vent, oscuro, u, e.semilla)
        val rw = minOf(lado * 0.48f, 8.5f * u)
        val ret = Rect(w - lado * 0.1f - rw, h * 0.34f, w - lado * 0.1f, h * 0.34f + rw * 1.3f)
        retrato(ret, oscuro, u)
        // Los quinqués de pared a los lados del marco.
        val qx = lado * 0.1f + vw + (lado - lado * 0.1f - vw) * 0.5f
        val qa = minOf(1.4f * u, (lado - lado * 0.1f - vw) * 0.18f)
        quinque(Offset(qx, h * 0.3f), qa, vent.right + 0.2f * u, oscuro, u)
        quinque(Offset(w - qx, h * 0.3f), qa, ret.left - 0.2f * u, oscuro, u)
        // Los cordones del gancho al marco.
        val marco = n.crece(3.2f * u)
        val gancho = Offset(w / 2, riel.bottom + 0.4f * u)
        listOf(Offset(marco.left + marco.width * 0.12f, marco.top + 0.6f * u), Offset(marco.right - marco.width * 0.12f, marco.top + 0.6f * u)).forEach { p ->
            drawLine(t(CORDEL, oscuro, 0.35f), gancho, p, 0.45f * u)
            drawLine(Color.Black.copy(alpha = 0.2f), gancho + Offset(0f, 0.5f * u), p + Offset(0f, 0.5f * u), 0.3f * u)
            tachuela(p, 0.7f * u, t(LATON, oscuro, 0.2f), t(LATON_LUZ, oscuro, 0.2f))
        }
        tachuela(gancho, 1f * u, t(LATON, oscuro, 0.2f), t(LATON_LUZ, oscuro, 0.2f))
        // El marco de roble con su filete de latón, sus cantoneras y la placa del sello.
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        roble(marco, oscuro, e.semilla + 8, u, ROBLE_LUZ)
        bisel(marco, Color.White.copy(alpha = if (oscuro) 0.06f else 0.16f), Color.Black.copy(alpha = 0.4f), 0.8f * u)
        val medio = marco.crece(-1.6f * u)
        drawRect(t(ROBLE_OSC, oscuro), medio.topLeft, medio.size, style = Stroke(0.6f * u))
        val filete = n.crece(0.7f * u)
        rect(t(LATON, oscuro, 0.25f), filete)
        drawRect(t(LATON_LUZ, oscuro, 0.25f), filete.topLeft, Size(filete.width, 0.25f * u))
        drawRect(t(LATON_OSC, oscuro, 0.25f), Offset(filete.left, filete.bottom - 0.25f * u), Size(filete.width, 0.25f * u))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { c ->
            val sx = if (c.x < w / 2) 1f else -1f; val sy = if (c.y < h / 2) 1f else -1f
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + sx * 3.4f * u, c.y); lineTo(c.x + sx * 3.4f * u, c.y + sy * 0.9f * u); lineTo(c.x + sx * 0.9f * u, c.y + sy * 0.9f * u); lineTo(c.x + sx * 0.9f * u, c.y + sy * 3.4f * u); lineTo(c.x, c.y + sy * 3.4f * u); close() }, t(LATON, oscuro, 0.25f))
            tachuela(Offset(c.x + sx * 0.45f * u, c.y + sy * 0.45f * u), 0.4f * u, t(LATON_OSC, oscuro, 0.25f), t(LATON_LUZ, oscuro, 0.25f))
        }
        // El papel del árbol genealógico (el nicho): de legajo de día, verde de niebla de noche, con sus cercos y su moteado.
        rect(if (oscuro) mezcla(e.fondo, Color(0xFF22332C), 0.3f) else mezcla(e.fondo, PAPEL, 0.45f), n)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val manchaC = if (oscuro) MOHO.copy(alpha = 0.18f) else TE.copy(alpha = 0.16f)
            cerco(Offset(n.right - n.width * 0.1f, n.bottom - n.height * 0.16f), 5f * u, manchaC, u)
            for (k in 0 until 26) {
                drawCircle(manchaC.copy(alpha = manchaC.alpha * 0.6f), (0.2f + 0.4f * azar(e.semilla, k)) * u, Offset(n.left + n.width * azar(e.semilla, k + 50), n.top + n.height * azar(e.semilla, k + 90)))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, manchaC.copy(alpha = manchaC.alpha * 0.8f)), n.bottom - n.height * 0.2f, n.bottom), Offset(n.left, n.bottom - n.height * 0.2f), Size(n.width, n.height * 0.2f))
            // El pliegue de en medio, de haberlo guardado doblado.
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.12f else 0.06f), Offset(n.center.x, n.top), Offset(n.center.x, n.bottom), 0.5f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.25f), Offset(n.center.x + 0.5f * u, n.top), Offset(n.center.x + 0.5f * u, n.bottom), 0.4f * u)
        }
        // La placa del sello del ojo, en el larguero de abajo.
        val pl = Offset(w / 2, marco.bottom - 1.6f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(pl.x - 6f * u + 0.3f * u, pl.y - 1.6f * u + 0.4f * u), Size(12f * u, 3.2f * u), CornerRadius(0.6f * u))
        drawRoundRect(t(LATON, oscuro, 0.2f), Offset(pl.x - 6f * u, pl.y - 1.6f * u), Size(12f * u, 3.2f * u), CornerRadius(0.6f * u))
        drawRoundRect(t(LATON_OSC, oscuro, 0.2f), Offset(pl.x - 5.4f * u, pl.y - 1.1f * u), Size(10.8f * u, 2.2f * u), CornerRadius(0.4f * u), style = Stroke(0.2f * u))
        ojo(pl, 1.2f * u, t(LATON_OSC, oscuro, 0.2f), t(LATON, oscuro, 0.2f), 0.22f * u)
    }

    /** La ventana de guillotina: el marco blanco desconchado, los vidrios con la niebla, el mar y el faro que barre. */
    private fun DrawScope.ventana(r: Rect, oscuro: Boolean, u: Float, semilla: Int) {
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
        val marco = t(Color(0xFFCFC9B6), oscuro, 0.5f)
        rect(marco, r.crece(1f * u))
        val vidrio = r.crece(-0.3f * u)
        // El exterior: el cielo de niebla, el mar y el faro lejano.
        val cielo = if (oscuro) listOf(Color(0xFF1A2A2A), Color(0xFF2C3E3A)) else listOf(Color(0xFFB9C6C2), NIEBLA)
        drawRect(Brush.verticalGradient(cielo, vidrio.top, vidrio.bottom), vidrio.topLeft, vidrio.size)
        clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
            val mar = vidrio.top + vidrio.height * 0.68f
            drawRect(t(MAR, oscuro, 0.3f), Offset(vidrio.left, mar), Size(vidrio.width, vidrio.bottom - mar))
            for (k in 0 until 4) drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.25f), Offset(vidrio.left + vidrio.width * azar(semilla, k) * 0.6f, mar + (k + 1) * 1.6f * u), Offset(vidrio.left + vidrio.width * (0.3f + azar(semilla, k) * 0.6f), mar + (k + 1) * 1.6f * u), 0.25f * u)
            val fx = vidrio.left + vidrio.width * 0.62f
            val luz = Offset(fx, mar - vidrio.height * 0.2f)
            // El haz.
            drawPath(Path().apply { moveTo(luz.x, luz.y); lineTo(vidrio.left - vidrio.width, luz.y - vidrio.height * 0.18f); lineTo(vidrio.left - vidrio.width, luz.y + vidrio.height * 0.02f); close() }, Color(0xFFFFF2C0).copy(alpha = if (oscuro) 0.35f else 0.25f))
            drawRect(t(Color(0xFF3A4440), oscuro, 0.2f), Offset(fx - 0.6f * u, luz.y), Size(1.2f * u, mar - luz.y))
            drawRect(t(Color(0xFF2A302E), oscuro, 0.2f), Offset(fx - 1.6f * u, mar - 0.8f * u), Size(3.2f * u, 0.8f * u))
            halo(luz, 3.2f * u, Color(0xFFFFF2C0).copy(alpha = if (oscuro) 0.9f else 0.7f))
            // La niebla que pasa.
            for (k in 0 until 3) {
                val y = vidrio.top + vidrio.height * (0.35f + 0.18f * k)
                drawRoundRect(NIEBLA.copy(alpha = if (oscuro) 0.08f else 0.4f), Offset(vidrio.left - 2f * u + k * 2f * u, y), Size(vidrio.width * 0.9f, 1.6f * u), CornerRadius(0.8f * u))
            }
        }
        // Los parteluces y la guillotina.
        val cruz = t(Color(0xFFCFC9B6), oscuro, 0.5f)
        drawLine(cruz, Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 0.6f * u)
        listOf(0.25f, 0.5f, 0.75f).forEach { f -> drawLine(cruz, Offset(r.left, r.top + r.height * f), Offset(r.right, r.top + r.height * f), if (f == 0.5f) 1.2f * u else 0.6f * u) }
        drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft, r.size, style = Stroke(0.3f * u))
        // El alféizar.
        val alf = Rect(r.left - 1.6f * u, r.bottom + 1f * u, r.right + 1.6f * u, r.bottom + 2.2f * u)
        rect(t(Color(0xFFCFC9B6), oscuro, 0.5f), alf)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(alf.left, alf.bottom), Size(alf.width, 0.6f * u))
    }

    /** El retrato ovalado en sepia: el marco de latón con su lazo, el fondo y la figura del antepasado con su barba. */
    private fun DrawScope.retrato(r: Rect, oscuro: Boolean, u: Float) {
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
        drawOval(t(LATON, oscuro, 0.25f), r.topLeft, r.size)
        drawOval(t(LATON_LUZ, oscuro, 0.25f), r.topLeft, r.size, style = Stroke(0.3f * u))
        val f = r.crece(-0.9f * u)
        drawOval(t(Color(0xFFB59E78), oscuro, 0.4f), f.topLeft, f.size)
        clipPath(Path().apply { addOval(f) }) {
            drawRect(Brush.radialGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.25f), Color.Black.copy(alpha = 0.3f)), f.center, f.width), f.topLeft, f.size)
            val sil = t(Color(0xFF4A3A28), oscuro, 0.3f)
            drawOval(sil, Offset(f.center.x - f.width * 0.42f, f.top + f.height * 0.68f), Size(f.width * 0.84f, f.height * 0.6f))
            drawOval(sil, Offset(f.center.x - f.width * 0.18f, f.top + f.height * 0.22f), Size(f.width * 0.36f, f.height * 0.36f))
            drawPath(Path().apply { moveTo(f.center.x - f.width * 0.17f, f.top + f.height * 0.46f); quadraticTo(f.center.x, f.top + f.height * 0.8f, f.center.x + f.width * 0.17f, f.top + f.height * 0.46f); close() }, t(Color(0xFF6A5640), oscuro, 0.3f))
        }
        // El clavo y el lazo de arriba.
        val c = Offset(r.center.x, r.top - 0.6f * u)
        drawLine(t(LACRE, oscuro, 0.3f), c, c + Offset(-1.6f * u, -1.2f * u), 0.6f * u, StrokeCap.Round)
        drawLine(t(LACRE, oscuro, 0.3f), c, c + Offset(1.6f * u, -1.2f * u), 0.6f * u, StrokeCap.Round)
        tachuela(c, 0.6f * u, t(LATON, oscuro, 0.2f), t(LATON_LUZ, oscuro, 0.2f))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.MAPAS))
        // El piso de tablas en la penumbra bajo la mesa.
        tablas(Rect(0f, 0f, w, h), oscuro, e.semilla + 1, u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.35f))
        // Las patas torneadas.
        val faldon = Rect(w * 0.02f, h * 0.775f, w * 0.98f, h * 0.9f)
        listOf(w * 0.04f, w * 0.96f).forEachIndexed { i, x ->
            val pw = 4.2f * u
            drawOval(Color.Black.copy(alpha = 0.4f), Offset(x - pw, h * 0.965f), Size(pw * 2, 1.6f * u))
            val top = faldon.bottom; val bot = h * 0.98f
            val tramo = bot - top
            drawRect(Brush.horizontalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE, oscuro), t(ROBLE_OSC, oscuro)), x - pw * 0.32f, x + pw * 0.32f), Offset(x - pw * 0.32f, top), Size(pw * 0.64f, tramo))
            listOf(0.18f, 0.55f).forEach { f ->
                val c = Offset(x, top + tramo * f)
                drawOval(Brush.horizontalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE_OSC, oscuro)), x - pw * 0.5f, x + pw * 0.5f), Offset(c.x - pw * 0.5f, c.y - tramo * 0.12f), Size(pw, tramo * 0.24f))
            }
            drawRect(t(LATON, oscuro, 0.25f), Offset(x - pw * 0.4f, bot - 1.2f * u), Size(pw * 0.8f, 1.2f * u))
        }
        // El faldón con sus dos cajones.
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(faldon.left, faldon.bottom), Size(faldon.width, 1.2f * u))
        roble(faldon, oscuro, e.semilla + 2, u, ROBLE)
        listOf(Rect(faldon.left + faldon.width * 0.06f, faldon.top + faldon.height * 0.16f, faldon.left + faldon.width * 0.47f, faldon.bottom - faldon.height * 0.16f),
            Rect(faldon.left + faldon.width * 0.53f, faldon.top + faldon.height * 0.16f, faldon.right - faldon.width * 0.06f, faldon.bottom - faldon.height * 0.16f)).forEachIndexed { i, c ->
            drawRect(Color.Black.copy(alpha = 0.5f), c.crece(0.4f * u).topLeft, c.crece(0.4f * u).size)
            roble(c, oscuro, e.semilla + 10 + i, u, ROBLE_LUZ)
            bisel(c, Color.White.copy(alpha = 0.08f), Color.Black.copy(alpha = 0.3f), 0.3f * u)
            // El portaetiquetas con su tarjeta en blanco.
            val pe = Rect(c.center.x - 4.5f * u, c.top + c.height * 0.18f, c.center.x + 4.5f * u, c.top + c.height * 0.48f)
            rect(t(FICHA, oscuro, 0.35f), pe)
            drawRect(t(LATON, oscuro, 0.2f), pe.topLeft, pe.size, style = Stroke(0.5f * u))
            listOf(pe.left, pe.right).forEach { x -> tachuela(Offset(x, pe.center.y), 0.35f * u, t(LATON_OSC, oscuro, 0.2f), t(LATON_LUZ, oscuro, 0.2f)) }
            // El tirador de concha.
            val tc = Offset(c.center.x, c.top + c.height * 0.72f)
            drawArc(t(LATON, oscuro, 0.2f), 0f, 180f, true, Offset(tc.x - 2.6f * u, tc.y - 1.4f * u), Size(5.2f * u, 2.8f * u))
            drawArc(t(LATON_OSC, oscuro, 0.2f), 0f, 180f, true, Offset(tc.x - 1.8f * u, tc.y - 0.8f * u), Size(3.6f * u, 1.6f * u))
        }
        // El tablero de roble visto desde arriba, con su canto moldurado.
        val mesa = Rect(w * 0.012f, h * 0.012f, w * 0.988f, faldon.top + 0.5f * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(mesa.left + 0.8f * u, mesa.top + 1.2f * u), mesa.size)
        rect(t(ROBLE_OSC, oscuro), mesa)
        val tab = mesa.crece(-1.2f * u)
        val nT = 6
        for (k in 0 until nT) {
            val r = Rect(tab.left, tab.top + tab.height * k / nT, tab.right, tab.top + tab.height * (k + 1) / nT)
            rect(t(mezcla(ROBLE, ROBLE_LUZ, 0.15f + 0.35f * azar(e.semilla, k)), oscuro), r)
            vetas(r, t(VETA, oscuro).copy(alpha = 0.5f), e.semilla * 3 + k, 1.1f * u, grosor = 0.3f * u)
            drawLine(Color.Black.copy(alpha = 0.5f), Offset(r.left, r.top), Offset(r.right, r.top), 0.3f * u)
        }
        drawRect(t(ROBLE_LUZ, oscuro), tab.topLeft, tab.size, style = Stroke(0.5f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), tab.crece(-0.9f * u).topLeft, tab.crece(-0.9f * u).size, style = Stroke(0.3f * u))
        // El tapete verde con su ribete de tachuelas de latón.
        val tapete = Rect(n.left - 3.6f * u, n.top - 2.6f * u, n.right + 3.6f * u, n.bottom + 3f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapete.left + 0.5f * u, tapete.top + 0.8f * u), tapete.size)
        rect(t(TAPETE, oscuro, 0.3f), tapete)
        clipRect(tapete.left, tapete.top, tapete.right, tapete.bottom) {
            var yy = tapete.top
            while (yy < tapete.bottom) { drawLine(Color.Black.copy(alpha = 0.06f), Offset(tapete.left, yy), Offset(tapete.right, yy), 0.25f * u); yy += 0.8f * u }
        }
        val rb = tapete.crece(-1.1f * u)
        listOf(rb.left to rb.right).forEach { (a, b) ->
            var x = a
            while (x <= b + 0.1f) { tachuela(Offset(x, rb.top), 0.42f * u, t(LATON, oscuro, 0.25f), t(LATON_LUZ, oscuro, 0.25f)); tachuela(Offset(x, rb.bottom), 0.42f * u, t(LATON, oscuro, 0.25f), t(LATON_LUZ, oscuro, 0.25f)); x += 2.2f * u }
        }
        var ty = rb.top + 2.2f * u
        while (ty < rb.bottom - 1f * u) { tachuela(Offset(rb.left, ty), 0.42f * u, t(LATON, oscuro, 0.25f), t(LATON_LUZ, oscuro, 0.25f)); tachuela(Offset(rb.right, ty), 0.42f * u, t(LATON, oscuro, 0.25f), t(LATON_LUZ, oscuro, 0.25f)); ty += 2.2f * u }
        // La carta náutica (el nicho), con su borde gastado y sus chinchetas.
        val hoja = n.crece(1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(hoja.left + 0.5f * u, hoja.top + 0.8f * u), hoja.size)
        rect(if (oscuro) mezcla(e.fondo, PAPEL_VIEJO, 0.12f) else mezcla(PAPEL_VIEJO, TE, 0.15f), hoja)
        rect(if (oscuro) mezcla(e.fondo, MAR, 0.2f) else mezcla(e.fondo, PAPEL_VIEJO, 0.4f), n)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val m = if (oscuro) MOHO.copy(alpha = 0.18f) else TE.copy(alpha = 0.18f)
            cerco(Offset(n.left + n.width * 0.08f, n.bottom - n.height * 0.12f), 4f * u, m, u)
            drawRect(Brush.horizontalGradient(listOf(m, Color.Transparent), n.left, n.left + n.width * 0.08f), n.topLeft, Size(n.width * 0.08f, n.height))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, m), n.right - n.width * 0.08f, n.right), Offset(n.right - n.width * 0.08f, n.top), Size(n.width * 0.08f, n.height))
        }
        drawRect(t(TINTA, oscuro, 0.1f).copy(alpha = if (oscuro) 0.5f else 0.6f), n.topLeft, n.size, style = Stroke(0.3f * u))
        listOf(hoja.topLeft, Offset(hoja.right, hoja.top), Offset(hoja.left, hoja.bottom), hoja.bottomRight).forEachIndexed { i, c ->
            chincheta(c + Offset(if (c.x < w / 2) 0.8f * u else -0.8f * u, if (c.y < h / 2) 0.8f * u else -0.8f * u), 1f * u, t(if (i % 2 == 0) LACRE else LATON, oscuro, 0.2f))
        }
        // A la izquierda: la brújula en su caja, la lupa y un cerco de café en la madera.
        val izq = Rect(tab.left + 1.4f * u, tab.top + 1.4f * u, tapete.left - 1f * u, tab.bottom - 1.4f * u)
        cerco(Offset(izq.center.x + izq.width * 0.1f, izq.top + izq.height * 0.62f), minOf(izq.width * 0.3f, 4.5f * u), Color(0xFF140C06).copy(alpha = 0.45f), u)
        val bl = minOf(izq.width * 0.86f, izq.height * 0.42f)
        brujula(Rect(izq.center.x - bl / 2, izq.top + izq.height * 0.03f, izq.center.x + bl / 2, izq.top + izq.height * 0.03f + bl), oscuro, u)
        lupa(Offset(izq.left + izq.width * 0.42f, izq.top + izq.height * 0.74f), minOf(izq.width * 0.3f, 4.6f * u), oscuro, u)
        // A la derecha: el quinqué visto desde arriba, el atado de cartas con su lacre y el frasco.
        val der = Rect(tapete.right + 1f * u, tab.top + 1.4f * u, tab.right - 1.4f * u, tab.bottom - 1.4f * u)
        val lc = Offset(der.center.x, der.top + der.height * 0.18f)
        val lr = minOf(der.width * 0.3f, 4.4f * u)
        halo(lc, lr * 4f, LLAMA.copy(alpha = if (oscuro) 0.4f else 0.2f))
        drawCircle(Color.Black.copy(alpha = 0.35f), lr * 1.15f, lc + Offset(0.5f * u, 0.8f * u))
        drawCircle(t(LATON, oscuro, 0.2f), lr * 1.15f, lc)
        drawCircle(t(LATON_OSC, oscuro, 0.2f), lr * 1.15f, lc, style = Stroke(0.3f * u))
        drawCircle(t(LATON_LUZ, oscuro, 0.2f), lr * 0.85f, lc, style = Stroke(0.25f * u))
        drawCircle(VIDRIO.copy(alpha = 0.5f), lr * 0.62f, lc)
        drawCircle(Color.White.copy(alpha = 0.6f), lr * 0.62f, lc, style = Stroke(0.25f * u))
        drawCircle(Color(0xFFFFE6A0), lr * 0.34f, lc)
        drawCircle(Color(0xFFF5A62E), lr * 0.15f, lc)
        val cw = minOf(der.width * 0.86f, 13f * u)
        cartas(Rect(der.center.x - cw / 2, der.top + der.height * 0.38f, der.center.x + cw / 2, der.top + der.height * 0.38f + cw * 0.66f), oscuro, u)
        val fr = minOf(der.width * 0.36f, 5.4f * u)
        val fc = Offset(der.center.x - der.width * 0.05f, der.bottom - fr * 1.25f)
        // El frasco visto desde arriba: el vidrio, el líquido verde y la cosa enroscada.
        if (oscuro) halo(fc, fr * 2.4f, VERDE_RARO.copy(alpha = 0.3f))
        drawCircle(Color.Black.copy(alpha = 0.35f), fr, fc + Offset(0.5f * u, 0.8f * u))
        drawCircle(VIDRIO.copy(alpha = 0.7f), fr, fc)
        drawCircle(t(Color(0xFF3E6A50), oscuro, 0.15f), fr * 0.82f, fc)
        drawPath(Path().apply { moveTo(fc.x - fr * 0.5f, fc.y + fr * 0.2f); cubicTo(fc.x - fr * 0.2f, fc.y - fr * 0.6f, fc.x + fr * 0.6f, fc.y - fr * 0.2f, fc.x + fr * 0.1f, fc.y + fr * 0.15f); cubicTo(fc.x - fr * 0.1f, fc.y + fr * 0.3f, fc.x + fr * 0.2f, fc.y + fr * 0.4f, fc.x + fr * 0.3f, fc.y + fr * 0.2f) },
            (if (oscuro) VERDE_RARO else Color(0xFFA9C98A)).copy(alpha = 0.9f), style = Stroke(fr * 0.14f, cap = StrokeCap.Round))
        drawCircle(Color.White.copy(alpha = 0.7f), fr, fc, style = Stroke(0.3f * u))
        drawArc(Color.White.copy(alpha = 0.6f), 200f, 70f, false, Offset(fc.x - fr * 0.8f, fc.y - fr * 0.8f), Size(fr * 1.6f, fr * 1.6f), style = Stroke(0.5f * u))
    }

    /** La brújula en su caja de roble: el aro de latón, la rosa de los vientos y la aguja. */
    private fun DrawScope.brujula(r: Rect, oscuro: Boolean, u: Float) {
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
        roble(r, oscuro, 77, u, ROBLE_LUZ)
        bisel(r, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.35f), 0.4f * u)
        val c = r.center; val rr = r.width * 0.42f
        drawCircle(t(LATON, oscuro, 0.2f), rr, c)
        drawCircle(t(LATON_LUZ, oscuro, 0.2f), rr, c, style = Stroke(0.3f * u))
        drawCircle(t(PAPEL, oscuro, 0.3f), rr * 0.82f, c)
        for (k in 0 until 32) {
            val a = Math.toRadians(k * 11.25)
            val r0 = if (k % 8 == 0) rr * 0.66f else if (k % 2 == 0) rr * 0.72f else rr * 0.76f
            drawLine(t(TINTA, oscuro, 0.2f).copy(alpha = 0.7f), Offset(c.x + r0 * cos(a).toFloat(), c.y + r0 * sin(a).toFloat()), Offset(c.x + rr * 0.8f * cos(a).toFloat(), c.y + rr * 0.8f * sin(a).toFloat()), 0.15f * u)
        }
        // La rosa: ocho puntas en dos tonos.
        for (k in 0 until 8) {
            val a = Math.toRadians(-90.0 + k * 45.0)
            val l = if (k % 2 == 0) rr * 0.62f else rr * 0.38f
            val p = Offset(c.x + l * cos(a).toFloat(), c.y + l * sin(a).toFloat())
            val a1 = Math.toRadians(-90.0 + k * 45.0 + 90.0)
            val q = Offset(c.x + rr * 0.1f * cos(a1).toFloat(), c.y + rr * 0.1f * sin(a1).toFloat())
            val q2 = Offset(c.x - rr * 0.1f * cos(a1).toFloat(), c.y - rr * 0.1f * sin(a1).toFloat())
            drawPath(Path().apply { moveTo(q.x, q.y); lineTo(p.x, p.y); lineTo(c.x, c.y); close() }, t(if (k % 2 == 0) TINTA else MOHO, oscuro, 0.15f))
            drawPath(Path().apply { moveTo(q2.x, q2.y); lineTo(p.x, p.y); lineTo(c.x, c.y); close() }, t(if (k % 2 == 0) PAPEL_VIEJO else TE, oscuro, 0.2f))
        }
        // La aguja, desviada hacia algo que no es el norte.
        rotate(28f, c) {
            drawPath(Path().apply { moveTo(c.x, c.y - rr * 0.7f); lineTo(c.x + rr * 0.08f, c.y); lineTo(c.x - rr * 0.08f, c.y); close() }, t(LACRE, oscuro, 0.1f))
            drawPath(Path().apply { moveTo(c.x, c.y + rr * 0.7f); lineTo(c.x + rr * 0.08f, c.y); lineTo(c.x - rr * 0.08f, c.y); close() }, t(Color(0xFF3A3A38), oscuro, 0.1f))
        }
        drawCircle(t(LATON, oscuro, 0.2f), rr * 0.09f, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.1f else 0.25f), rr * 0.45f, c - Offset(rr * 0.3f, rr * 0.3f))
    }

    /** La lupa: el aro de latón con su vidrio y su brillo y el mango de madera torneada. */
    private fun DrawScope.lupa(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        val fin = c + Offset(r * 1.4f, r * 1.4f)
        drawLine(Color.Black.copy(alpha = 0.3f), c + Offset(r * 0.8f + 0.5f * u, r * 0.8f + 0.8f * u), fin + Offset(r * 0.6f + 0.5f * u, r * 0.6f + 0.8f * u), r * 0.42f, StrokeCap.Round)
        drawLine(t(LATON_OSC, oscuro, 0.2f), c + Offset(r * 0.72f, r * 0.72f), c + Offset(r * 1.05f, r * 1.05f), r * 0.32f)
        drawLine(t(ROBLE_OSC, oscuro, 0.1f), c + Offset(r * 1.05f, r * 1.05f), fin + Offset(r * 0.6f, r * 0.6f), r * 0.38f, StrokeCap.Round)
        drawLine(Color.White.copy(alpha = 0.18f), c + Offset(r * 1.1f, r * 0.95f), fin + Offset(r * 0.5f, r * 0.4f), r * 0.08f)
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(0.5f * u, 0.8f * u))
        drawCircle(VIDRIO.copy(alpha = 0.45f), r, c)
        drawCircle(t(LATON, oscuro, 0.2f), r, c, style = Stroke(r * 0.18f))
        drawCircle(t(LATON_LUZ, oscuro, 0.2f), r * 1.05f, c, style = Stroke(r * 0.04f))
        drawArc(Color.White.copy(alpha = 0.7f), 200f, 60f, false, Offset(c.x - r * 0.65f, c.y - r * 0.65f), Size(r * 1.3f, r * 1.3f), style = Stroke(r * 0.12f, cap = StrokeCap.Round))
    }

    /** El atado de cartas: los sobres, el bramante en cruz y el sello de lacre. */
    private fun DrawScope.cartas(r: Rect, oscuro: Boolean, u: Float) {
        listOf(-6f, 4f, -1f).forEachIndexed { i, g ->
            rotate(g, r.center) {
                val s = r.crece(-i * 0.4f * u)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(s.left + 0.4f * u, s.top + 0.6f * u), s.size)
                rect(t(if (i == 1) MANILA else PAPEL, oscuro, 0.35f), s)
                drawRect(t(PAPEL_VIEJO, oscuro, 0.35f), s.topLeft, s.size, style = Stroke(0.25f * u))
                if (i == 2) {
                    drawPath(Path().apply { moveTo(s.left, s.top); lineTo(s.center.x, s.center.y); lineTo(s.right, s.top) }, t(PAPEL_VIEJO, oscuro, 0.35f), style = Stroke(0.3f * u))
                    drawLine(t(TINTA, oscuro, 0.2f).copy(alpha = 0.5f), Offset(s.left + s.width * 0.55f, s.top + s.height * 0.72f), Offset(s.right - s.width * 0.1f, s.top + s.height * 0.72f), 0.3f * u)
                    drawLine(t(TINTA, oscuro, 0.2f).copy(alpha = 0.5f), Offset(s.left + s.width * 0.55f, s.top + s.height * 0.84f), Offset(s.right - s.width * 0.18f, s.top + s.height * 0.84f), 0.3f * u)
                }
            }
        }
        drawLine(t(CORDEL, oscuro, 0.25f), Offset(r.center.x, r.top - 0.6f * u), Offset(r.center.x, r.bottom + 0.6f * u), 0.5f * u)
        drawLine(t(CORDEL, oscuro, 0.25f), Offset(r.left - 0.6f * u, r.center.y), Offset(r.right + 0.6f * u, r.center.y), 0.5f * u)
        val sc = r.center
        val rs = minOf(r.height * 0.24f, 2.2f * u)
        drawCircle(Color.Black.copy(alpha = 0.3f), rs, sc + Offset(0.3f * u, 0.5f * u))
        drawPath(Path().apply {
            for (k in 0 until 14) {
                val a = Math.toRadians(k * 360.0 / 14)
                val rr = rs * (if (k % 2 == 0) 1f else 0.88f)
                val p = Offset(sc.x + rr * cos(a).toFloat(), sc.y + rr * sin(a).toFloat())
                if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }, t(LACRE, oscuro, 0.2f))
        ojo(sc, rs * 0.6f, t(mezcla(LACRE, Color.Black, 0.35f), oscuro, 0.2f), t(LACRE, oscuro, 0.2f), 0.18f * u)
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.OTRAS_VIDAS))
        // La pared y el piso.
        papelPintado(Rect(0f, 0f, w, h), oscuro, e.semilla + 2, u)
        tablas(Rect(0f, h * 0.965f, w, h), oscuro, e.semilla, u)
        // Las puertas de vidrio abiertas, a los lados, vistas de canto.
        listOf(Rect(0f, h * 0.2f, w * 0.03f, h * 0.84f), Rect(w * 0.97f, h * 0.2f, w, h * 0.84f)).forEachIndexed { i, r ->
            roble(r, oscuro, e.semilla + 60 + i, u, ROBLE, vertical = true)
            val v = r.crece(-0.5f * u)
            drawRect(VIDRIO.copy(alpha = if (oscuro) 0.18f else 0.35f), v.topLeft, v.size)
            clipRect(v.left, v.top, v.right, v.bottom) {
                var y = v.top + 2f * u
                while (y < v.bottom) { drawLine(Color.White.copy(alpha = 0.45f), Offset(v.left, y + v.width), Offset(v.right, y), 0.3f * u); y += 7f * u }
            }
            drawRect(t(ROBLE_OSC, oscuro), r.topLeft, r.size, style = Stroke(0.4f * u))
        }
        val cuerpo = Rect(w * 0.035f, h * 0.17f, w * 0.965f, h * 0.965f)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(cuerpo.left + 0.8f * u, cuerpo.top + 1.2f * u), cuerpo.size)
        roble(cuerpo, oscuro, e.semilla + 1, u, ROBLE)
        // El zócalo de cajones con sus tiradores.
        val zocalo = Rect(cuerpo.left, n.bottom + 4.6f * u, cuerpo.right, cuerpo.bottom)
        roble(zocalo, oscuro, e.semilla + 3, u, ROBLE_OSC)
        val nC = 3
        for (k in 0 until nC) {
            val c = Rect(zocalo.left + zocalo.width * k / nC + 1.4f * u, zocalo.top + 1.2f * u, zocalo.left + zocalo.width * (k + 1) / nC - 1.4f * u, zocalo.bottom - 1.6f * u)
            if (c.height < 2f * u) continue
            drawRect(Color.Black.copy(alpha = 0.5f), c.crece(0.3f * u).topLeft, c.crece(0.3f * u).size)
            roble(c, oscuro, e.semilla + 20 + k, u, ROBLE_LUZ)
            val tc = c.center
            drawCircle(Color.Black.copy(alpha = 0.3f), 0.9f * u, tc + Offset(0.2f * u, 0.4f * u))
            drawCircle(t(LATON, oscuro, 0.2f), 0.9f * u, tc)
            drawCircle(t(LATON_LUZ, oscuro, 0.2f), 0.35f * u, tc - Offset(0.25f * u, 0.25f * u))
        }
        // El listón de portaetiquetas de latón bajo los compartimentos.
        val liston = Rect(cuerpo.left, n.bottom, cuerpo.right, n.bottom + 4.6f * u)
        roble(liston, oscuro, e.semilla + 4, u, ROBLE_LUZ)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(liston.left, liston.bottom - 0.6f * u), Size(liston.width, 0.6f * u))
        val cw = n.width / 5f
        for (i in 0 until 5) {
            val cx = n.left + cw * (i + 0.5f)
            val pe = Rect(cx - minOf(cw * 0.32f, 7f * u), liston.top + liston.height * 0.2f, cx + minOf(cw * 0.32f, 7f * u), liston.bottom - liston.height * 0.28f)
            rect(t(FICHA, oscuro, 0.35f), pe)
            drawLine(t(TINTA, oscuro, 0.2f).copy(alpha = 0.35f), Offset(pe.left + pe.width * 0.15f, pe.center.y), Offset(pe.right - pe.width * 0.3f, pe.center.y), 0.25f * u)
            drawRect(t(LATON, oscuro, 0.2f), pe.topLeft, pe.size, style = Stroke(0.45f * u))
            listOf(pe.left, pe.right).forEach { x -> tachuela(Offset(x, pe.center.y), 0.32f * u, t(LATON_OSC, oscuro, 0.2f), t(LATON_LUZ, oscuro, 0.2f)) }
        }
        // Los compartimentos forrados (el nicho): papel de legajo de día, terciopelo verde gastado de noche.
        val forro = if (oscuro) mezcla(e.fondo, TERCIOPELO, 0.3f) else mezcla(e.fondo, PAPEL, 0.4f)
        rect(forro, n)
        clipRect(n.left, n.top, n.right, n.bottom) {
            if (oscuro) {
                var x = n.left
                while (x < n.right) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(x, n.top), Offset(x, n.bottom), 0.25f * u); x += 0.9f * u }
            } else {
                for (k in 0 until 30) drawCircle(TE.copy(alpha = 0.1f), (0.2f + 0.4f * azar(e.semilla, k)) * u, Offset(n.left + n.width * azar(e.semilla, k + 40), n.top + n.height * azar(e.semilla, k + 80)))
            }
            for (i in 0 until 5) {
                val c = Rect(n.left + cw * i, n.top, n.left + cw * (i + 1), n.bottom)
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Color.Transparent), c.top, c.top + 5f * u), c.topLeft, Size(c.width, 5f * u))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.12f), Color.Transparent), c.left, c.left + 2.5f * u), c.topLeft, Size(2.5f * u, c.height))
            }
        }
        // Las columnillas torneadas entre los compartimentos y a los lados.
        for (i in 0..5) {
            val x = n.left + cw * i
            val ancho = if (i == 0 || i == 5) 2.6f * u else 1.4f * u
            val cx = if (i == 0) x - ancho / 2 else if (i == 5) x + ancho / 2 else x
            columnilla(cx, n.top, n.bottom, ancho, oscuro, u)
        }
        listOf(Rect(cuerpo.left, n.top, n.left - 2.6f * u, n.bottom), Rect(n.right + 2.6f * u, n.top, cuerpo.right, n.bottom)).forEachIndexed { i, r ->
            if (r.width <= 0f) return@forEachIndexed
            roble(r, oscuro, e.semilla + 30 + i, u, ROBLE, vertical = true)
            // Las bisagras de latón de las puertas.
            listOf(0.15f, 0.85f).forEach { f ->
                val b = Rect(if (i == 0) r.left else r.right - 1.4f * u, r.top + r.height * f - 2f * u, if (i == 0) r.left + 1.4f * u else r.right, r.top + r.height * f + 2f * u)
                rect(t(LATON, oscuro, 0.2f), b)
                drawLine(t(LATON_OSC, oscuro, 0.2f), Offset(b.left, b.center.y), Offset(b.right, b.center.y), 0.25f * u)
            }
        }
        // La cornisa con dentículos y el sello del ojo al centro.
        val corn = Rect(w * 0.02f, h * 0.15f, w * 0.98f, n.top)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(corn.left, corn.bottom), Size(corn.width, 1.2f * u))
        roble(corn, oscuro, e.semilla + 5, u, ROBLE)
        val alero = Rect(corn.left - 0.6f * u, corn.top, corn.right + 0.6f * u, corn.top + corn.height * 0.3f)
        roble(alero, oscuro, e.semilla + 6, u, ROBLE_LUZ)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(alero.left, alero.bottom), Size(alero.width, 0.5f * u))
        var dx = corn.left + 0.8f * u
        val dy = alero.bottom + 0.9f * u
        while (dx < corn.right - 1f * u) { drawRect(t(ROBLE_LUZ, oscuro), Offset(dx, dy), Size(1.2f * u, 1.6f * u)); drawRect(Color.Black.copy(alpha = 0.3f), Offset(dx + 1.2f * u, dy), Size(0.4f * u, 1.6f * u)); dx += 2.4f * u }
        drawLine(t(LATON, oscuro, 0.25f), Offset(corn.left, corn.bottom - 0.6f * u), Offset(corn.right, corn.bottom - 0.6f * u), 0.4f * u)
        val sello = Offset(w / 2, corn.center.y + corn.height * 0.1f)
        val rs = minOf(corn.height * 0.48f, 5f * u)
        drawCircle(Color.Black.copy(alpha = 0.3f), rs * 1.15f, sello + Offset(0.3f * u, 0.5f * u))
        drawCircle(t(ROBLE_OSC, oscuro), rs * 1.15f, sello)
        ojo(sello, rs, t(LATON, oscuro, 0.2f), t(ROBLE_OSC, oscuro), 0.35f * u)
        // Encima: los legajos atados, dos frascos y la vela.
        val sobre = alero.top
        val lg = Rect(w * 0.1f, sobre - 5.8f * u, w * 0.1f + 14f * u, sobre)
        listOf(Color(0xFF4A2A2A), Color(0xFF2E3B2E), Color(0xFF5A3E22)).forEachIndexed { i, c ->
            val r = Rect(lg.left + i * 0.6f * u, lg.bottom - (i + 1) * 1.9f * u, lg.right - (2 - i) * 0.8f * u, lg.bottom - i * 1.9f * u)
            rect(t(c, oscuro, 0.35f), r)
            drawRect(t(PAPEL_VIEJO, oscuro, 0.35f), Offset(r.right - 0.7f * u, r.top + 0.3f * u), Size(0.5f * u, r.height - 0.6f * u))
            drawLine(t(LATON, oscuro, 0.25f), Offset(r.left + 1f * u, r.top + 0.3f * u), Offset(r.left + 1f * u, r.bottom - 0.3f * u), 0.3f * u)
        }
        drawLine(t(CORDEL, oscuro, 0.3f), Offset(lg.center.x, lg.top), Offset(lg.center.x, lg.bottom), 0.4f * u)
        val fh = minOf(7.2f * u, sobre - h * 0.01f)
        frasco(Rect(w * 0.78f, sobre - fh, w * 0.78f + fh * 0.65f, sobre), oscuro, u, e.semilla)
        frasco(Rect(w * 0.78f + fh * 0.8f, sobre - fh * 0.75f, w * 0.78f + fh * 0.8f + fh * 0.5f, sobre), oscuro, u, e.semilla + 1)
        val vela = Offset(w * 0.32f, sobre)
        drawOval(t(LATON, oscuro, 0.2f), Offset(vela.x - 2f * u, vela.y - 0.8f * u), Size(4f * u, 1.2f * u))
        drawRect(t(CERA, oscuro, 0.2f), Offset(vela.x - 0.6f * u, vela.y - 3.4f * u), Size(1.2f * u, 2.8f * u))
        llama(Offset(vela.x, vela.y - 3.5f * u), 1.6f * u, oscuro)
        telarana(Offset(cuerpo.right, corn.bottom), minOf(cw * 0.35f, 8f * u), -1f, 1f, oscuro, u)
        telarana(Offset(w * 0.965f, sobre - 0.2f * u), 5f * u, -1f, -1f, oscuro, u)
    }

    /** Una columnilla torneada de roble: el fuste con sus anillos, la luz a un lado y su base y capitel. */
    private fun DrawScope.columnilla(cx: Float, arriba: Float, abajo: Float, ancho: Float, oscuro: Boolean, u: Float) {
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(cx + ancho / 2, arriba), Size(0.5f * u, abajo - arriba))
        val pincel = Brush.horizontalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE, oscuro), t(ROBLE_OSC, oscuro)), cx - ancho / 2, cx + ancho / 2)
        drawRect(pincel, Offset(cx - ancho * 0.36f, arriba), Size(ancho * 0.72f, abajo - arriba))
        val alto = abajo - arriba
        listOf(0f, 0.33f, 0.66f, 1f).forEach { f ->
            val y = arriba + alto * f
            drawOval(pincel, Offset(cx - ancho / 2, y - ancho * 0.3f), Size(ancho, ancho * 0.6f))
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - ancho / 2, y - ancho * 0.3f), Size(ancho, ancho * 0.6f), style = Stroke(0.15f * u))
        }
        drawLine(t(LATON, oscuro, 0.25f), Offset(cx, arriba + alto * 0.45f), Offset(cx, arriba + alto * 0.54f), ancho * 0.3f)
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f
        val cx = w / 2; val base = h / 2 + 30f * s
        when (id) {
            "unesco" -> {
                // El faro sobre las rocas, con su haz, su casa de torrero y la niebla.
                val luz = Offset(cx + 2f * s, base - 47f * s)
                listOf(-1f, 1f).forEach { sgn ->
                    drawPath(Path().apply { moveTo(luz.x, luz.y); lineTo(luz.x + sgn * 48f * s, luz.y - 9f * s); lineTo(luz.x + sgn * 48f * s, luz.y + 5f * s); close() }, Brush.horizontalGradient(listOf(Color(0xFFFFF2C0).copy(alpha = if (oscuro) 0.6f else 0.45f), Color.Transparent), luz.x, luz.x + sgn * 48f * s))
                }
                // Las rocas.
                val roca = Path().apply { moveTo(cx - 34f * s, base); lineTo(cx - 26f * s, base - 8f * s); lineTo(cx - 14f * s, base - 11f * s); lineTo(cx - 2f * s, base - 13f * s); lineTo(cx + 14f * s, base - 10f * s); lineTo(cx + 24f * s, base - 6f * s); lineTo(cx + 32f * s, base); close() }
                drawPath(roca, t(Color(0xFF4A4A44), oscuro, 0.3f))
                drawPath(Path().apply { moveTo(cx - 26f * s, base - 8f * s); lineTo(cx - 14f * s, base - 11f * s); lineTo(cx - 10f * s, base - 4f * s); close() }, Color.White.copy(alpha = 0.12f))
                // La casa del torrero.
                val casa = Rect(cx + 8f * s, base - 19f * s, cx + 20f * s, base - 9f * s)
                rect(t(Color(0xFFE6E0D0), oscuro, 0.35f), casa)
                drawPath(Path().apply { moveTo(casa.left - 1f * s, casa.top); lineTo(casa.center.x, casa.top - 5f * s); lineTo(casa.right + 1f * s, casa.top); close() }, t(LACRE, oscuro, 0.25f))
                drawRect(t(if (oscuro) LLAMA else TINTA, oscuro, 0f), Offset(casa.left + 3f * s, casa.top + 3f * s), Size(2.4f * s, 3f * s))
                // La torre: troncocónica, de franjas, con su balcón, su linterna y su cúpula.
                val torre = Path().apply { moveTo(cx - 6f * s, base - 11f * s); lineTo(cx - 3.5f * s, base - 42f * s); lineTo(cx + 7.5f * s, base - 42f * s); lineTo(cx + 10f * s, base - 11f * s); close() }
                drawPath(torre, t(Color(0xFFEDE7D8), oscuro, 0.35f))
                clipPath(torre) {
                    listOf(0.25f, 0.55f).forEach { f -> drawRect(t(e.c1, oscuro, 0f), Offset(cx - 8f * s, base - 11f * s - 31f * s * f - 3f * s), Size(20f * s, 5f * s)) }
                    drawRect(Color.Black.copy(alpha = 0.18f), Offset(cx + 3f * s, base - 44f * s), Size(10f * s, 34f * s))
                }
                drawRect(t(Color(0xFF2E2E2C), oscuro, 0.2f), Offset(cx - 5.5f * s, base - 43.5f * s), Size(15f * s, 1.6f * s))
                drawRect(t(Color(0xFF2E2E2C), oscuro, 0.2f), Offset(cx - 1.5f * s, base - 50f * s), Size(7f * s, 6.5f * s))
                halo(luz, 9f * s, Color(0xFFFFF2C0).copy(alpha = if (oscuro) 0.85f else 0.6f))
                drawRect(Color(0xFFFFE9A0), Offset(cx - 0.5f * s, base - 49f * s), Size(5f * s, 4.5f * s))
                drawPath(Path().apply { moveTo(cx - 2.5f * s, base - 50f * s); quadraticTo(cx + 2f * s, base - 57f * s, cx + 6.5f * s, base - 50f * s); close() }, t(e.c2, oscuro, 0.1f))
                drawLine(t(Color(0xFF2E2E2C), oscuro, 0.2f), Offset(cx + 2f * s, base - 55f * s), Offset(cx + 2f * s, base - 58f * s), 0.6f * s)
                // El mar y la niebla.
                drawLine(t(MAR, oscuro, 0.1f), Offset(cx - 44f * s, base), Offset(cx + 44f * s, base), 1.2f * s)
                listOf(-1f, 0.4f).forEachIndexed { i, k -> drawRoundRect(NIEBLA.copy(alpha = if (oscuro) 0.18f else 0.6f), Offset(cx + k * 30f * s - 14f * s, base - (16f + 9f * i) * s), Size(28f * s, 2.4f * s), CornerRadius(1.2f * s)) }
            }
            "rascar" -> {
                // La carta vieja que una moneda raspa: debajo asoma la tinta del abismo, con algo que se enrosca.
                val hoja = Rect(cx - 30f * s, base - 54f * s, cx + 30f * s, base - 4f * s)
                rotate(3f, hoja.center) {
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(hoja.left + 1.2f * s, hoja.top + 1.8f * s), hoja.size)
                    rect(t(PAPEL_VIEJO, oscuro, 0.3f), hoja)
                    cerco(Offset(hoja.left + 12f * s, hoja.top + 12f * s), 7f * s, TE.copy(alpha = 0.5f), s)
                    // Las líneas de rumbo de la carta.
                    listOf(0f, 30f, 60f, 90f, 120f, 150f).forEach { a ->
                        val r = Math.toRadians(a.toDouble())
                        val c0 = Offset(hoja.left + hoja.width * 0.35f, hoja.top + hoja.height * 0.55f)
                        drawLine(TINTA.copy(alpha = 0.18f), c0 - Offset(cos(r).toFloat(), sin(r).toFloat()) * 40f * s, c0 + Offset(cos(r).toFloat(), sin(r).toFloat()) * 40f * s, 0.4f * s)
                    }
                    // La parte raspada: el verde negro del fondo del mar, con un tentáculo.
                    val raspado = Path().apply {
                        moveTo(hoja.right - 3f * s, hoja.top + 8f * s)
                        cubicTo(hoja.right - 20f * s, hoja.top + 10f * s, hoja.right - 28f * s, hoja.top + 26f * s, hoja.right - 22f * s, hoja.bottom - 3f * s)
                        lineTo(hoja.right - 3f * s, hoja.bottom - 3f * s); close()
                    }
                    drawPath(raspado, t(Color(0xFF14261F), oscuro, 0f))
                    clipPath(raspado) {
                        drawPath(Path().apply { moveTo(hoja.right - 4f * s, hoja.bottom - 4f * s); cubicTo(hoja.right - 18f * s, hoja.bottom - 12f * s, hoja.right - 4f * s, hoja.top + 22f * s, hoja.right - 13f * s, hoja.top + 18f * s); cubicTo(hoja.right - 18f * s, hoja.top + 16f * s, hoja.right - 16f * s, hoja.top + 24f * s, hoja.right - 12f * s, hoja.top + 23f * s) },
                            e.c2.copy(alpha = 0.9f), style = Stroke(2f * s, cap = StrokeCap.Round))
                        drawCircle(VERDE_RARO.copy(alpha = 0.5f), 1f * s, Offset(hoja.right - 9f * s, hoja.top + 34f * s))
                        drawCircle(VERDE_RARO.copy(alpha = 0.5f), 0.7f * s, Offset(hoja.right - 15f * s, hoja.bottom - 10f * s))
                    }
                    drawPath(raspado, PAPEL.copy(alpha = 0.7f), style = Stroke(0.6f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * s, 0.8f * s))))
                    drawRect(t(TINTA, oscuro, 0.2f).copy(alpha = 0.6f), hoja.crece(-2.4f * s).topLeft, hoja.crece(-2.4f * s).size, style = Stroke(0.5f * s))
                }
                // La moneda vieja de bronce, con su sello del ojo, raspando.
                val m = Offset(cx + 20f * s, base - 12f * s)
                drawOval(Color.Black.copy(alpha = 0.35f), Offset(m.x - 9f * s, m.y - 6f * s), Size(19f * s, 14f * s))
                drawOval(t(LATON_OSC, oscuro, 0.1f), Offset(m.x - 10f * s, m.y - 8.4f * s), Size(20f * s, 15f * s))
                drawOval(t(Color(0xFF9C8A52), oscuro, 0.1f), Offset(m.x - 10f * s, m.y - 9.4f * s), Size(20f * s, 14f * s))
                drawOval(MOHO.copy(alpha = 0.45f), Offset(m.x + 1f * s, m.y - 4f * s), Size(6f * s, 4f * s))
                ojo(Offset(m.x, m.y - 2.4f * s), 4.4f * s, t(LATON_OSC, oscuro, 0.1f), Color.Transparent, 0.6f * s)
                drawOval(Color.White.copy(alpha = 0.35f), Offset(m.x - 7f * s, m.y - 8.4f * s), Size(6f * s, 2.4f * s))
            }
            else -> {
                // Tus lugares: tres chinchetas de latón unidas por el hilo rojo sobre el mapa de la costa.
                val hoja = Path().apply {
                    moveTo(cx - 44f * s, base - 42f * s); lineTo(cx - 10f * s, base - 40f * s); lineTo(cx + 44f * s, base - 43f * s)
                    lineTo(cx + 46f * s, base - 5f * s); lineTo(cx + 8f * s, base - 7f * s); lineTo(cx - 46f * s, base - 4f * s); close()
                }
                translate(1.2f * s, 1.8f * s) { drawPath(hoja, Color.Black.copy(alpha = 0.3f)) }
                drawPath(hoja, t(PAPEL_VIEJO, oscuro, 0.3f))
                clipPath(hoja) {
                    // La costa recortada y el mar rayado.
                    val costa = Path().apply {
                        moveTo(cx - 46f * s, base - 30f * s); cubicTo(cx - 30f * s, base - 26f * s, cx - 24f * s, base - 36f * s, cx - 10f * s, base - 30f * s)
                        cubicTo(cx, base - 26f * s, cx + 8f * s, base - 34f * s, cx + 20f * s, base - 26f * s); cubicTo(cx + 30f * s, base - 20f * s, cx + 36f * s, base - 30f * s, cx + 46f * s, base - 24f * s)
                        lineTo(cx + 46f * s, base); lineTo(cx - 46f * s, base); close()
                    }
                    drawPath(costa, t(e.c1, oscuro, 0f).copy(alpha = 0.22f))
                    drawPath(costa, t(TINTA, oscuro, 0.1f).copy(alpha = 0.55f), style = Stroke(0.6f * s))
                    var y = base - 22f * s
                    while (y < base) { drawLine(t(TINTA, oscuro, 0.1f).copy(alpha = 0.15f), Offset(cx - 46f * s, y), Offset(cx + 46f * s, y), 0.4f * s); y += 3f * s }
                    cerco(Offset(cx + 30f * s, base - 34f * s), 5f * s, TE.copy(alpha = 0.5f), s)
                }
                drawPath(hoja, t(TE, oscuro, 0.3f), style = Stroke(0.6f * s))
                val pts = listOf(Offset(cx - 30f * s, base - 33f * s), Offset(cx + 2f * s, base - 18f * s), Offset(cx + 26f * s, base - 36f * s))
                drawLine(t(HILO, oscuro, 0f), pts[0], pts[1], 0.9f * s)
                drawLine(t(HILO, oscuro, 0f), pts[1], pts[2], 0.9f * s)
                drawLine(t(HILO, oscuro, 0f).copy(alpha = 0.7f), pts[0], pts[2], 0.7f * s)
                pts.forEach { p ->
                    drawCircle(Color.Black.copy(alpha = 0.35f), 4f * s, p + Offset(1.6f * s, 2.2f * s))
                    drawCircle(t(LATON_OSC, oscuro, 0.1f), 4f * s, p)
                    drawCircle(t(LATON, oscuro, 0.1f), 3.3f * s, p - Offset(0.3f * s, 0.3f * s))
                    drawCircle(t(LATON_LUZ, oscuro, 0.1f), 1.2f * s, p - Offset(1.3f * s, 1.3f * s))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.NOTAS))
        papelPintado(Rect(0f, 0f, w, h), oscuro, e.semilla + 5, u)
        // El marco de roble.
        val marco = Rect(w * 0.025f, h * 0.065f, w * 0.975f, h * 0.93f)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        roble(marco, oscuro, e.semilla + 1, u, ROBLE_LUZ)
        bisel(marco, Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), Color.Black.copy(alpha = 0.4f), 0.6f * u)
        val medio = marco.crece(-1.4f * u)
        drawRect(t(ROBLE_OSC, oscuro), medio.topLeft, medio.size, style = Stroke(0.5f * u))
        // El copete con su portaetiquetas en blanco y sus dos ganchos.
        val copete = Rect(marco.left + 2.2f * u, marco.top + 1.8f * u, marco.right - 2.2f * u, n.top - 1.6f * u)
        roble(copete, oscuro, e.semilla + 2, u, ROBLE)
        bisel(copete, Color.Black.copy(alpha = 0.3f), Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), 0.4f * u)
        val pe = Rect(w / 2 - 12f * u, copete.top + copete.height * 0.2f, w / 2 + 12f * u, copete.bottom - copete.height * 0.2f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(pe.left + 0.3f * u, pe.top + 0.5f * u), pe.size)
        rect(t(FICHA, oscuro, 0.35f), pe)
        drawLine(MARGEN_ROJO.copy(alpha = 0.5f), Offset(pe.left + 1f * u, pe.top + pe.height * 0.3f), Offset(pe.right - 1f * u, pe.top + pe.height * 0.3f), 0.25f * u)
        drawRect(t(LATON, oscuro, 0.2f), pe.topLeft, pe.size, style = Stroke(0.7f * u))
        listOf(pe.left, pe.right).forEach { x -> tachuela(Offset(x, pe.center.y), 0.5f * u, t(LATON_OSC, oscuro, 0.2f), t(LATON_LUZ, oscuro, 0.2f)) }
        listOf(copete.left + copete.width * 0.12f, copete.right - copete.width * 0.12f).forEachIndexed { i, x ->
            val g = Offset(x, copete.center.y)
            tachuela(g, 0.9f * u, t(LATON, oscuro, 0.2f), t(LATON_LUZ, oscuro, 0.2f))
            drawArc(t(LATON_OSC, oscuro, 0.2f), 0f, 180f, false, Offset(g.x - 1.2f * u, g.y), Size(2.4f * u, 2.4f * u), style = Stroke(0.4f * u))
            if (i == 0) {
                // Del gancho cuelga una llave vieja.
                val k = g + Offset(0f, 2.4f * u)
                drawLine(t(CORDEL, oscuro, 0.3f), g + Offset(0f, 1.2f * u), k, 0.3f * u)
                drawCircle(t(LATON_OSC, oscuro, 0.2f), 0.9f * u, k + Offset(0f, 0.9f * u), style = Stroke(0.4f * u))
                drawLine(t(LATON_OSC, oscuro, 0.2f), k + Offset(0f, 1.8f * u), k + Offset(0f, 4.4f * u), 0.4f * u)
                drawLine(t(LATON_OSC, oscuro, 0.2f), k + Offset(0f, 3.8f * u), k + Offset(0.8f * u, 3.8f * u), 0.4f * u)
            }
        }
        // El corcho (el nicho): granulado, con agujeros viejos, y el hilo rojo de chincheta a chincheta.
        val rebaje = n.crece(1f * u)
        rect(t(ROBLE_OSC, oscuro), rebaje)
        rect(if (oscuro) mezcla(e.fondo, CORCHO, 0.14f) else mezcla(e.fondo, CORCHO, 0.42f), n)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val oscGrano = (if (oscuro) Color.Black else Color(0xFF8A6A40)).copy(alpha = if (oscuro) 0.25f else 0.22f)
            val claroGrano = Color.White.copy(alpha = if (oscuro) 0.04f else 0.25f)
            val nGr = (n.width * n.height / (u * u) / 9f).toInt().coerceIn(200, 900)
            for (k in 0 until nGr) {
                val p = Offset(n.left + n.width * azar(e.semilla + 11, k), n.top + n.height * azar(e.semilla + 13, k))
                drawCircle(if (k % 3 == 0) claroGrano else oscGrano, (0.15f + 0.3f * azar(e.semilla + 17, k)) * u, p)
            }
            for (k in 0 until 14) {
                val p = Offset(n.left + n.width * azar(e.semilla + 23, k), n.top + n.height * azar(e.semilla + 29, k))
                drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.35f), 0.25f * u, p)
            }
            cerco(Offset(n.left + n.width * 0.52f, n.top + n.height * 0.5f), 6f * u, (if (oscuro) MOHO else TE).copy(alpha = 0.22f), u)
            // El hilo rojo entre las chinchetas (bajo las notas: se ve en los huecos).
            val hueco = n.width * 0.03f
            val cw = (n.width - 2 * hueco) / 3f; val ch = (n.height - hueco) / 2f
            val pins = (0 until 6).map { i -> Offset(n.left + (i % 3) * (cw + hueco) + cw / 2, n.top + (i / 3) * (ch + hueco) + ch * 0.075f) }
            val hilo = t(HILO, oscuro, 0.25f)
            listOf(0 to 4, 4 to 2, 1 to 3, 3 to 5, 0 to 5).forEach { (a, b) ->
                drawLine(Color.Black.copy(alpha = 0.25f), pins[a] + Offset(0.3f * u, 0.6f * u), pins[b] + Offset(0.3f * u, 0.6f * u), 0.35f * u)
                drawLine(hilo, pins[a], pins[b], 0.4f * u)
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.25f), Color.Transparent), n.top, n.top + 3f * u), n.topLeft, Size(n.width, 3f * u))
        }
        // Las cantoneras de latón.
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { c ->
            val sx = if (c.x < w / 2) 1f else -1f; val sy = if (c.y < h / 2) 1f else -1f
            val k = 4.2f * u
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + sx * k, c.y); lineTo(c.x, c.y + sy * k); close() }, t(LATON, oscuro, 0.25f))
            drawPath(Path().apply { moveTo(c.x + sx * k, c.y); lineTo(c.x, c.y + sy * k) }, t(LATON_OSC, oscuro, 0.25f), style = Stroke(0.3f * u))
            tachuela(Offset(c.x + sx * 1.3f * u, c.y + sy * 1.3f * u), 0.45f * u, t(LATON_OSC, oscuro, 0.25f), t(LATON_LUZ, oscuro, 0.25f))
        }
        // La canaleta de abajo con la lupa, el lápiz y el cabo de vela.
        val can = Rect(n.left - 1f * u, n.bottom + 1f * u, n.right + 1f * u, marco.bottom - 1.4f * u)
        rect(t(ROBLE_OSC, oscuro), can)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent), can.top, can.bottom), can.topLeft, can.size)
        val lab = Rect(can.left, can.bottom - 1.4f * u, can.right, can.bottom + 1.4f * u)
        roble(lab, oscuro, e.semilla + 3, u, ROBLE_LUZ)
        val ly = can.center.y
        // El lápiz.
        val l0 = Offset(can.left + can.width * 0.58f, ly + 0.2f * u)
        drawLine(Color.Black.copy(alpha = 0.3f), l0 + Offset(0.3f * u, 0.5f * u), l0 + Offset(18f * u, 0.5f * u), 1.2f * u)
        drawLine(t(Color(0xFF2E4A3A), oscuro, 0.2f), l0, l0 + Offset(16f * u, 0f), 1.2f * u)
        drawLine(Color.White.copy(alpha = 0.2f), l0 - Offset(0f, 0.3f * u), l0 + Offset(16f * u, -0.3f * u), 0.3f * u)
        drawPath(Path().apply { moveTo(l0.x + 16f * u, l0.y - 0.6f * u); lineTo(l0.x + 18.4f * u, l0.y); lineTo(l0.x + 16f * u, l0.y + 0.6f * u); close() }, t(Color(0xFFD9B98A), oscuro, 0.2f))
        drawCircle(t(TINTA, oscuro, 0.1f), 0.25f * u, Offset(l0.x + 18.2f * u, l0.y))
        drawRect(t(LATON, oscuro, 0.2f), Offset(l0.x - 1f * u, l0.y - 0.6f * u), Size(1f * u, 1.2f * u))
        drawRect(t(Color(0xFFB07070), oscuro, 0.2f), Offset(l0.x - 2f * u, l0.y - 0.6f * u), Size(1f * u, 1.2f * u))
        // El cabo de vela en su platillo, y la lupa.
        val v = Offset(can.left + can.width * 0.08f, can.bottom - 0.6f * u)
        drawOval(t(LATON, oscuro, 0.2f), Offset(v.x - 2.4f * u, v.y - 1f * u), Size(4.8f * u, 1.4f * u))
        drawRect(t(CERA, oscuro, 0.2f), Offset(v.x - 0.8f * u, v.y - 3.4f * u), Size(1.6f * u, 2.8f * u))
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(v.x + 0.2f * u, v.y - 3.4f * u), Size(0.6f * u, 2.8f * u))
        llama(Offset(v.x, v.y - 3.5f * u), 2f * u, oscuro)
        val lc = Offset(can.left + can.width * 0.3f, ly)
        drawLine(t(ROBLE_OSC, oscuro, 0.1f), lc + Offset(2f * u, 0f), lc + Offset(8f * u, 0f), 1.2f * u, StrokeCap.Round)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(lc.x - 2.2f * u, ly - 1f * u + 0.4f * u), Size(4.4f * u, 2f * u))
        drawOval(VIDRIO.copy(alpha = 0.6f), Offset(lc.x - 2.2f * u, ly - 1f * u), Size(4.4f * u, 2f * u))
        drawOval(t(LATON, oscuro, 0.2f), Offset(lc.x - 2.2f * u, ly - 1f * u), Size(4.4f * u, 2f * u), style = Stroke(0.4f * u))
        // La telaraña en la esquina de arriba.
        telarana(Offset(medio.right, medio.top), 7f * u, -1f, 1f, oscuro, u)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val w = r.width; val h = r.height; val s = minOf(w, h) / 100f
        val hoja = Rect(r.left + w * 0.02f, r.top + h * 0.03f, r.right - w * 0.02f, r.bottom - h * 0.02f)
        val giro = (azar(e.indice + 7, 2) - 0.5f) * 3f
        // El papel según el tipo: ficha de catálogo, recorte de periódico, etiqueta de manila o telegrama.
        val (base, borde) = when (e.tipo) {
            "idea" -> FICHA to PAPEL_VIEJO
            "pensamiento" -> PERIODICO to Color(0xFFA8A496)
            "proposito" -> MANILA to TE
            else -> TELEGRAMA to Color(0xFFC0A860)
        }
        val papel = if (oscuro) mezcla(base, Color(0xFF2A2418), 0.3f) else base
        rotate(giro, hoja.center) {
            val forma = when (e.tipo) {
                "pensamiento" -> Path().apply {
                    // El recorte: el borde rasgado por arriba y por la derecha.
                    moveTo(hoja.left, hoja.top + 1.5f * s)
                    var x = hoja.left; var k = 0
                    while (x < hoja.right) { lineTo(x, hoja.top + (if (k % 2 == 0) 0f else 1.6f * s) * (0.5f + azar(e.indice, k))); x += 3f * s; k++ }
                    var y = hoja.top
                    while (y < hoja.bottom) { lineTo(hoja.right - (if (k % 2 == 0) 0f else 1.4f * s) * (0.5f + azar(e.indice, k)), y); y += 3f * s; k++ }
                    lineTo(hoja.right, hoja.bottom); lineTo(hoja.left, hoja.bottom); close()
                }
                "proposito" -> Path().apply {
                    // La etiqueta de manila: las esquinas de arriba cortadas.
                    val c = 7f * s
                    moveTo(hoja.left + c, hoja.top); lineTo(hoja.right - c, hoja.top); lineTo(hoja.right, hoja.top + c)
                    lineTo(hoja.right, hoja.bottom); lineTo(hoja.left, hoja.bottom); lineTo(hoja.left, hoja.top + c); close()
                }
                else -> Path().apply { addRect(hoja) }
            }
            translate(1.2f * s, 2f * s) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.55f else 0.3f)) }
            drawPath(forma, papel)
            clipPath(forma) {
                // El amarilleo de los bordes.
                val b = 8f * s
                drawRect(Brush.verticalGradient(listOf(TE.copy(alpha = 0.25f), Color.Transparent), hoja.top, hoja.top + b), hoja.topLeft, Size(hoja.width, b))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, TE.copy(alpha = 0.25f)), hoja.bottom - b, hoja.bottom), Offset(hoja.left, hoja.bottom - b), Size(hoja.width, b))
                when (e.tipo) {
                    "idea" -> {
                        // La ficha: el renglón rojo de arriba, los renglones azules y el agujero de la varilla.
                        drawLine(MARGEN_ROJO.copy(alpha = 0.6f), Offset(hoja.left, r.top + h * 0.15f), Offset(hoja.right, r.top + h * 0.15f), 0.6f * s)
                        var y = r.top + h * 0.15f + 13.5f * s
                        while (y < hoja.bottom - 2f * s) { drawLine(RAYADO.copy(alpha = 0.35f), Offset(hoja.left, y), Offset(hoja.right, y), 0.4f * s); y += 13.5f * s }
                        drawCircle(Color.Black.copy(alpha = 0.5f), 2.2f * s, Offset(r.center.x, hoja.bottom - 5f * s))
                    }
                    "pensamiento" -> {
                        // El recorte: el filete de la columna y el titular en la franja de arriba.
                        drawRect(Color(0xFF3A3A36).copy(alpha = 0.55f), Offset(r.left + w * 0.12f, r.top + h * 0.075f), Size(w * 0.5f, 2.2f * s))
                        drawLine(Color(0xFF3A3A36).copy(alpha = 0.35f), Offset(r.left + w * 0.12f, r.top + h * 0.13f), Offset(r.right - w * 0.12f, r.top + h * 0.13f), 0.4f * s)
                        drawLine(Color(0xFF3A3A36).copy(alpha = 0.25f), Offset(r.right - w * 0.06f, r.top + h * 0.16f), Offset(r.right - w * 0.06f, r.bottom - h * 0.08f), 0.4f * s)
                    }
                    "proposito" -> {
                        // La etiqueta: el ojal reforzado y su bramante.
                        val oj = Offset(r.center.x, r.top + h * 0.085f)
                        drawCircle(t(MANILA, oscuro, 0.2f).copy(alpha = 1f), 4f * s, oj)
                        drawCircle(TE.copy(alpha = 0.7f), 4f * s, oj, style = Stroke(0.7f * s))
                        drawCircle(Color.Black.copy(alpha = 0.6f), 1.8f * s, oj)
                    }
                    else -> {
                        // El telegrama: las dos franjas de las tiras pegadas.
                        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(hoja.left + w * 0.1f, r.top + h * 0.3f), Size(hoja.width - w * 0.2f, 0.6f * s))
                        drawRect(Color.Black.copy(alpha = 0.06f), Offset(hoja.left + w * 0.1f, r.top + h * 0.55f), Size(hoja.width - w * 0.2f, 0.6f * s))
                    }
                }
                // Un cerco de té en algunas notas.
                if (e.indice % 3 == 1) cerco(Offset(hoja.right - w * 0.2f, hoja.bottom - h * 0.16f), 10f * s, TE.copy(alpha = 0.3f), s)
            }
            drawPath(forma, mezcla(borde, Color.Black, if (oscuro) 0.4f else 0.1f), style = Stroke(0.6f * s))
            // El clip del telegrama.
            if (e.tipo == "recordatorio") {
                val cx = hoja.left + w * 0.18f
                val clip = Path().apply {
                    moveTo(cx, hoja.top + 8f * s); lineTo(cx, hoja.top - 4f * s); quadraticTo(cx, hoja.top - 6.5f * s, cx + 2.2f * s, hoja.top - 6.5f * s)
                    quadraticTo(cx + 4.4f * s, hoja.top - 6.5f * s, cx + 4.4f * s, hoja.top - 4f * s); lineTo(cx + 4.4f * s, hoja.top + 10f * s)
                    quadraticTo(cx + 4.4f * s, hoja.top + 12f * s, cx + 2.6f * s, hoja.top + 12f * s); quadraticTo(cx + 1f * s, hoja.top + 12f * s, cx + 1f * s, hoja.top + 10f * s); lineTo(cx + 1f * s, hoja.top - 2f * s)
                }
                translate(0.5f * s, 0.8f * s) { drawPath(clip, Color.Black.copy(alpha = 0.3f), style = Stroke(0.9f * s)) }
                drawPath(clip, Color(0xFFA8AAA8), style = Stroke(0.9f * s))
            }
            // La chincheta de arriba; si la nota está fijada, el alfiler grande del hilo rojo y el sello de lacre.
            val cc = Offset(r.center.x + (if (e.tipo == "proposito") w * 0.2f else 0f), r.top + h * 0.075f)
            if (e.fijada) {
                val sc = Offset(r.right - w * 0.13f, r.bottom - h * 0.1f)
                val rs = 6f * s
                drawCircle(Color.Black.copy(alpha = 0.35f), rs, sc + Offset(0.8f * s, 1.2f * s))
                drawPath(Path().apply {
                    for (k in 0 until 16) {
                        val a = Math.toRadians(k * 360.0 / 16)
                        val rr = rs * (if (k % 2 == 0) 1f else 0.88f)
                        val p = Offset(sc.x + rr * cos(a).toFloat(), sc.y + rr * sin(a).toFloat())
                        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                    close()
                }, mezcla(LACRE, e.acento, 0.15f))
                ojo(sc, rs * 0.62f, mezcla(LACRE, Color.Black, 0.4f), mezcla(LACRE, e.acento, 0.15f), 0.5f * s)
                drawCircle(Color.White.copy(alpha = 0.3f), rs * 0.25f, sc - Offset(rs * 0.45f, rs * 0.45f))
                drawLine(HILO, cc, cc + Offset(-6f * s, -4f * s), 0.7f * s)
                drawLine(HILO, cc, cc + Offset(7f * s, -3.5f * s), 0.7f * s)
                chincheta(cc, 3.4f * s, HILO)
            } else chincheta(cc, 2.6f * s, listOf(LATON, Color(0xFF3E6A50), Color(0xFF3A4A6A), LACRE)[e.indice % 4])
        }
    }

    /** La tinta verde-negra del archivo: los papeles se ven claros también de noche, a la luz del quinqué. */
    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
