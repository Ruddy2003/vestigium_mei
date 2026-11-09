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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Renacimiento: el taller y el studiolo de los cuadernos de Leonardo, con los materiales de su biblioteca, su Ideario y su
 * galería de logros: nogal con veta, taracea de arce y ébano (los cubos en perspectiva), pietra serena gris con sus
 * pilastras corintias, enlucido claro, papel verjurado con tinta sepia, sanguina y trazos de construcción, latón y oro.
 * - Social: el refectorio de la Última Cena. Arriba, el artesonado con rosetas de oro y el entablamento de pietra serena
 *   (cornisa, dentículos, friso con clípeos, arquitrabe); a los lados, dos pilastras corintias; en medio, el cartón
 *   enmarcado donde va tu gente; abajo, la mesa larga con su mantel de lino con franjas azules, el pan, las copas de vino,
 *   los platos de peltre, la fruta y dos candeleros.
 * - Mapas: el escritorio de nogal con canto moldurado y cenefa de taracea; el pliego con su regla, sujeto con tachuelas;
 *   a la izquierda, el códice cerrado, el tintero con la pluma y el portalápiz de sanguina; a la derecha, el compás, el
 *   astrolabio y la escuadra. Al frente, el faldón con la taracea de cubos, el cajón con su argolla y las patas torneadas.
 * - Otras vidas: el armario del studiolo, con el frontón curvo y la concha dorada, la cornisa con dentículos, el friso de
 *   taracea, cinco hornacinas de arce con su venera, separadas por pilastras acanaladas, y el zócalo de cubos.
 * - Íconos: la cúpula de Brunelleschi sobre Santa Maria del Fiore, un florín de oro que raspa un pliego sombreado y un
 *   mapa con tres sellos de lacre unidos por un camino punteado y su rosa de los vientos.
 * - Notas: un panel de taracea en su marco de nogal, con frontón y concha, pilastras y la repisa con el compás y la
 *   sanguina; cada nota es una hoja de cuaderno con los bordes de barba, sujeta con una tachuela (o, fijada, con un sello).
 */
internal object VidaRenacimiento : DisenoVida {
    private val NOGAL = Color(0xFF6B4426)
    private val NOGAL_LUZ = Color(0xFF97683E)
    private val NOGAL_OSC = Color(0xFF3A2312)
    private val ARCE = Color(0xFFE8CF9C)
    private val ARCE_OSC = Color(0xFFC9A468)
    private val EBANO = Color(0xFF2A1A0E)
    private val PIETRA = Color(0xFF8E959C)
    private val PIETRA_LUZ = Color(0xFFC0C5CA)
    private val PIETRA_OSC = Color(0xFF60666E)
    private val PIETRA_SOMBRA = Color(0xFF454A51)
    private val ENLUCIDO = Color(0xFFEFE5CE)
    private val ENLUCIDO_OSC = Color(0xFFD7C8A8)
    private val CARTA = Color(0xFFF3E6C6)
    private val CARTA_OSC = Color(0xFFDCC594)
    private val SEPIA = Color(0xFF5B3A1F)
    private val SANGUINA = Color(0xFFB0502E)
    private val ORO = Color(0xFFC9A04A)
    private val ORO_LUZ = Color(0xFFF1D88E)
    private val ORO_OSC = Color(0xFF8A6624)
    private val LATON = Color(0xFFB98A40)
    private val TERRACOTA = Color(0xFFB8653B)
    private val TERRA_OSC = Color(0xFF7E3F22)
    private val TERRA_LUZ = Color(0xFFD48A5C)
    private val VERDE = Color(0xFF4B6B3E)
    private val LINO = Color(0xFFF5F0E4)
    private val LINO_OSC = Color(0xFFD8CEB9)
    private val AZUL_LINO = Color(0xFF3D5A84)
    private val VINO = Color(0xFF7A1F2A)
    private val PELTRE = Color(0xFFB9BAB4)
    private val PELTRE_OSC = Color(0xFF7D7F7A)
    private val CUERO = Color(0xFF7A3E22)
    private val LACRE = Color(0xFFB3261E)
    private val NARANJA = Color(0xFFE08A2E)
    private val LLAMA = Color(0xFFFFC870)
    private val NOCHE = Color(0xFF0E0904)

    /** El papel de las notas: claro de día; de noche, el pergamino a la luz de la vela. */
    private val HOJA = Color(0xFFF4E8CA)
    private val HOJA_NOCHE = Color(0xFF3E2F20)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.135f, 0.205f, 0.865f, 0.755f)
        MuebleVida.MAPAS -> Rect(0.18f, 0.10f, 0.82f, 0.68f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.08f, 0.275f, 0.92f, 0.80f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.15f, 0.925f, 0.875f)
    }

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material de noche: el mismo, en la penumbra del taller. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.52f) else c

    /** El enlucido de las paredes de noche: más hundido en la sombra que los muebles. */
    private fun nf(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.72f) else c

    /** El oro y el latón de noche: guardan más luz. */
    private fun nm(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.3f) else c

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mez(e.fondo, mez(material, NOCHE, 0.8f), 0.4f) else mez(e.fondo, material, 0.35f)

    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)
    private fun DrawScope.rect(r: Rect, b: Brush) = drawRect(b, r.topLeft, r.size)
    private fun vert(r: Rect, c: List<Color>) = Brush.verticalGradient(c, r.top, r.bottom)
    private fun vert(r: Rect, a: Color, b: Color) = vert(r, listOf(a, b))
    private fun vert(r: Rect, a: Color, b: Color, c: Color) = vert(r, listOf(a, b, c))
    private fun horiz(r: Rect, c: List<Color>) = Brush.horizontalGradient(c, r.left, r.right)
    private fun horiz(r: Rect, a: Color, b: Color, c: Color) = horiz(r, listOf(a, b, c))
    private fun horiz(r: Rect, a: Color, b: Color, c: Color, d: Color) = horiz(r, listOf(a, b, c, d))

    /** Un número estable de 0 a 1 (la misma irregularidad cada vez). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Una mancha radial suave (la luz de la vela, la humedad del enlucido). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** La sombra que una pieza deja debajo (un óvalo difuso). */
    private fun DrawScope.sombraBajo(x0: Float, x1: Float, y: Float, alto: Float, fuerza: Float = 0.35f) {
        val c = Offset((x0 + x1) / 2, y)
        drawOval(Brush.radialGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), c, (x1 - x0) / 2), Offset(x0, y - alto / 2), Size(x1 - x0, alto))
    }

    /** Vetas de madera en [r]: líneas onduladas a lo largo, de grosor [p] (≈ 1 dp). */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, p: Float, densidad: Float = 1f, vertical: Boolean = false) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        val n = (ancho / (3.2f * p) * densidad).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.6f + 1.6f * azar(semilla, k + 100)) * p
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.5f + 2.5f * azar(semilla, k + 300)
                val path = Path()
                for (s in 0..16) {
                    val t = largo * s / 16
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16)
                    val x = if (vertical) r.left + v else r.left + t
                    val y = if (vertical) r.top + t else r.top + v
                    if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(p * (0.5f + 0.6f * azar(semilla, k + 500))))
            }
        }
    }

    /** Una tabla de nogal con su veta. */
    private fun DrawScope.nogal(r: Rect, o: Boolean, semilla: Int, p: Float, vertical: Boolean = false) {
        rect(r, if (vertical) horiz(r, n(NOGAL_OSC, o), n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)) else vert(r, n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)))
        vetas(r, n(NOGAL_OSC, o).copy(alpha = 0.55f), semilla, p, vertical = vertical)
    }

    /** La taracea de cubos en perspectiva (arce, nogal y ébano), recortada a [r], con cubos de lado [s]. */
    private fun DrawScope.cubos(r: Rect, s: Float, o: Boolean) {
        val a = s * sqrt(3f) / 2f
        val claro = n(ARCE, o); val medio = n(NOGAL_LUZ, o); val oscuro = n(EBANO, o)
        clipRect(r.left, r.top, r.right, r.bottom) {
            rect(r, oscuro)
            var fila = 0
            var y = r.top
            while (y < r.bottom + 2 * s && fila < 80) {
                var x = r.left - a + (if (fila % 2 == 1) a else 0f)
                while (x < r.right + 2 * a) {
                    val c = Offset(x, y)
                    drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x - a, c.y - s / 2); lineTo(c.x, c.y - s); lineTo(c.x + a, c.y - s / 2); close() }, claro)
                    drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x - a, c.y - s / 2); lineTo(c.x - a, c.y + s / 2); lineTo(c.x, c.y + s); close() }, medio)
                    x += 2 * a
                }
                y += 1.5f * s; fila++
            }
        }
    }

    /** Dentículos: la fila de bloquecitos bajo la cornisa, con su sombra. */
    private fun DrawScope.denticulos(r: Rect, luz: Color, osc: Color, fondo: Color) {
        rect(r, fondo)
        val d = r.height * 0.75f
        var x = r.left + d * 0.2f
        while (x < r.right) {
            rect(Rect(x, r.top, x + d, r.bottom - r.height * 0.14f), vert(r, luz, osc))
            x += d * 1.5f
        }
    }

    /** Una roseta dorada de [petalos] pétalos. */
    private fun DrawScope.roseta(c: Offset, r: Float, color: Color, centro: Color, petalos: Int = 8) {
        for (k in 0 until petalos) {
            val a = 2 * Math.PI * k / petalos
            drawCircle(color, r * 0.36f, Offset(c.x + r * 0.55f * cos(a).toFloat(), c.y + r * 0.55f * sin(a).toFloat()))
        }
        drawCircle(centro, r * 0.32f, c)
    }

    /** Una venera (la concha de las hornacinas) de radio [r] que nace en [base] y se abre hacia arriba. */
    private fun DrawScope.venera(base: Offset, r: Float, ry: Float, luz: Color, osc: Color, linea: Color, g: Float) {
        val forma = Path().apply {
            moveTo(base.x - r, base.y)
            val n = 11
            for (k in 0 until n) {
                val a0 = Math.PI - Math.PI * k / n; val a1 = Math.PI - Math.PI * (k + 1) / n
                val am = (a0 + a1) / 2
                val p1 = Offset(base.x + r * cos(a1).toFloat(), base.y - ry * sin(a1).toFloat())
                val pm = Offset(base.x + r * 1.1f * cos(am).toFloat(), base.y - ry * 1.12f * sin(am).toFloat())
                quadraticTo(pm.x, pm.y, p1.x, p1.y)
            }
            close()
        }
        drawPath(forma, Brush.radialGradient(listOf(luz, osc), Offset(base.x, base.y - ry * 0.3f), r * 1.1f))
        clipPath(forma) {
            for (k in 1 until 11) {
                val a = Math.PI - Math.PI * k / 11
                drawLine(linea, base, Offset(base.x + r * 1.1f * cos(a).toFloat(), base.y - ry * 1.1f * sin(a).toFloat()), g)
            }
        }
        drawPath(forma, linea, style = Stroke(g * 0.8f))
        drawOval(osc, Offset(base.x - r * 0.22f, base.y - ry * 0.16f), Size(r * 0.44f, ry * 0.3f))
    }

    /** Una pilastra corintia de fuste [cw]: basa, fuste acanalado, capitel de hojas y volutas, ábaco. */
    private fun DrawScope.pilastra(xc: Float, top: Float, bottom: Float, cw: Float, mat: Color, luz: Color, osc: Color, capitel: Color, capLuz: Color, p: Float, sombra: Boolean = true) {
        val capH = cw * 1.05f; val baseH = cw * 0.55f
        val fTop = top + capH; val fBot = bottom - baseH
        if (sombra) drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), xc + cw * 0.55f, xc + cw * 1.0f), Offset(xc + cw * 0.55f, top), Size(cw * 0.45f, bottom - top))
        val f = Rect(xc - cw / 2, fTop, xc + cw / 2, fBot)
        rect(f, horiz(f, osc, luz, mat, osc))
        // Las estrías.
        for (k in 1 until 5) {
            val x = f.left + cw * k / 5
            drawRoundRect(osc.copy(alpha = 0.75f), Offset(x - cw * 0.05f, fTop + cw * 0.25f), Size(cw * 0.1f, fBot - fTop - cw * 0.5f), CornerRadius(cw * 0.05f))
            drawLine(luz.copy(alpha = 0.7f), Offset(x + cw * 0.07f, fTop + cw * 0.25f), Offset(x + cw * 0.07f, fBot - cw * 0.25f), p * 0.45f)
        }
        // El capitel: collarino, dos filas de hojas de acanto, volutas y el ábaco de lados cóncavos.
        rect(Rect(xc - cw * 0.55f, fTop - cw * 0.08f, xc + cw * 0.55f, fTop), vert(Rect(0f, fTop - cw * 0.08f, 0f, fTop), capLuz, capitel))
        val cap = Rect(xc - cw * 0.58f, top + capH * 0.2f, xc + cw * 0.58f, fTop - cw * 0.08f)
        rect(cap, vert(cap, mez(capitel, Color.Black, 0.25f), capitel))
        listOf(0.55f to 4, 0.18f to 3).forEach { (fy, nh) ->
            for (k in 0 until nh) {
                val hx = cap.left + cap.width * (k + 0.5f) / nh
                val hb = cap.top + cap.height * (fy + 0.45f)
                val hoja = Path().apply { moveTo(hx - cap.width / nh * 0.45f, hb); quadraticTo(hx - cap.width / nh * 0.4f, hb - cap.height * 0.5f, hx, hb - cap.height * 0.55f); quadraticTo(hx + cap.width / nh * 0.4f, hb - cap.height * 0.5f, hx + cap.width / nh * 0.45f, hb); close() }
                drawPath(hoja, Brush.verticalGradient(listOf(capLuz, capitel), hb - cap.height * 0.55f, hb))
                drawLine(mez(capitel, Color.Black, 0.35f), Offset(hx, hb), Offset(hx, hb - cap.height * 0.4f), p * 0.4f)
            }
        }
        listOf(-1f, 1f).forEach { s ->
            val vc = Offset(xc + s * cw * 0.55f, top + capH * 0.28f)
            drawCircle(capLuz, cw * 0.14f, vc)
            drawCircle(mez(capitel, Color.Black, 0.35f), cw * 0.14f, vc, style = Stroke(p * 0.45f))
            drawCircle(mez(capitel, Color.Black, 0.35f), cw * 0.04f, vc)
        }
        val ab = Path().apply {
            moveTo(xc - cw * 0.8f, top); lineTo(xc + cw * 0.8f, top); lineTo(xc + cw * 0.72f, top + capH * 0.16f)
            quadraticTo(xc, top + capH * 0.1f, xc - cw * 0.72f, top + capH * 0.16f); close()
        }
        drawPath(ab, vert(Rect(0f, top, 0f, top + capH * 0.16f), luz, osc))
        // La basa: toro, escocia, toro y plinto.
        val t1 = Rect(xc - cw * 0.58f, fBot, xc + cw * 0.58f, fBot + baseH * 0.25f)
        drawRoundRect(vert(t1, luz, osc), t1.topLeft, t1.size, CornerRadius(t1.height / 2))
        val t2 = Rect(xc - cw * 0.68f, fBot + baseH * 0.3f, xc + cw * 0.68f, fBot + baseH * 0.62f)
        drawRoundRect(vert(t2, luz, mat, osc), t2.topLeft, t2.size, CornerRadius(t2.height / 2))
        val pl = Rect(xc - cw * 0.74f, fBot + baseH * 0.6f, xc + cw * 0.74f, bottom)
        rect(pl, vert(pl, mat, osc))
        drawLine(luz, Offset(pl.left, pl.top), Offset(pl.right, pl.top), p * 0.5f)
    }

    /** La llama de una vela, con su halo. */
    private fun DrawScope.llama(o: Offset, r: Float, oscuro: Boolean) {
        mancha(o, r * (if (oscuro) 9f else 3f), LLAMA.copy(alpha = if (oscuro) 0.4f else 0.2f))
        drawPath(Path().apply {
            moveTo(o.x, o.y - r * 1.8f); quadraticTo(o.x + r * 0.85f, o.y - r * 0.2f, o.x, o.y + r * 0.5f); quadraticTo(o.x - r * 0.85f, o.y - r * 0.2f, o.x, o.y - r * 1.8f); close()
        }, Color(0xFFFFA733))
        drawOval(Color(0xFFFFF1C2), Offset(o.x - r * 0.3f, o.y - r * 0.6f), Size(r * 0.6f, r * 0.95f))
    }

    /** Un candelero de latón con su vela encendida, de pie sobre [base], de alto [alto]. */
    private fun DrawScope.candelero(base: Offset, alto: Float, o: Boolean, p: Float) {
        val x = base.x; val y = base.y; val a = alto
        val lat = horiz(Rect(x - a * 0.2f, 0f, x + a * 0.2f, 0f), nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(LATON, o), nm(ORO_OSC, o))
        sombraBajo(x - a * 0.25f, x + a * 0.25f, y, a * 0.08f, 0.4f)
        drawPath(Path().apply { moveTo(x - a * 0.2f, y); lineTo(x + a * 0.2f, y); quadraticTo(x + a * 0.06f, y - a * 0.06f, x + a * 0.04f, y - a * 0.14f); lineTo(x - a * 0.04f, y - a * 0.14f); quadraticTo(x - a * 0.06f, y - a * 0.06f, x - a * 0.2f, y); close() }, lat)
        drawOval(lat, Offset(x - a * 0.07f, y - a * 0.3f), Size(a * 0.14f, a * 0.18f))
        drawRect(lat, Offset(x - a * 0.03f, y - a * 0.42f), Size(a * 0.06f, a * 0.3f))
        drawOval(lat, Offset(x - a * 0.13f, y - a * 0.47f), Size(a * 0.26f, a * 0.07f))
        val vela = Rect(x - a * 0.05f, y - a * 0.8f, x + a * 0.05f, y - a * 0.44f)
        rect(vela, horiz(vela, n(Color(0xFFD9CDB0), o), n(Color(0xFFF8F2E2), o), n(Color(0xFFD9CDB0), o)))
        drawLine(n(EBANO, o), Offset(x, vela.top), Offset(x, vela.top - a * 0.04f), p * 0.5f)
        llama(Offset(x, vela.top - a * 0.08f), a * 0.05f, o)
    }

    /** Una tachuela de latón con su sombra. */
    private fun DrawScope.tachuela(c: Offset, r: Float, o: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + r * 0.35f, c.y + r * 0.5f))
        drawCircle(Brush.radialGradient(listOf(nm(ORO_LUZ, o), nm(LATON, o), nm(ORO_OSC, o)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.4f), r, c)
    }

    /** Un sello de lacre con su borde derramado y la flor de lis en relieve. */
    private fun DrawScope.sello(c: Offset, r: Float, color: Color, semilla: Int) {
        val forma = Path()
        for (k in 0 until 14) {
            val a = 6.2832f * k / 14
            val rr = r * (0.9f + 0.2f * azar(semilla, k))
            val x = c.x + rr * cos(a); val y = c.y + rr * sin(a)
            if (k == 0) forma.moveTo(x, y) else forma.lineTo(x, y)
        }
        forma.close()
        translate(r * 0.15f, r * 0.25f) { drawPath(forma, Color.Black.copy(alpha = 0.35f)) }
        drawPath(forma, Brush.radialGradient(listOf(mez(color, Color.White, 0.25f), color, mez(color, Color.Black, 0.35f)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.4f))
        drawCircle(mez(color, Color.Black, 0.3f), r * 0.66f, c, style = Stroke(r * 0.08f))
        lis(c, r * 0.48f, mez(color, Color.Black, 0.35f))
    }

    /** La flor de lis de Florencia, de alto 2·[r]. */
    private fun DrawScope.lis(c: Offset, r: Float, color: Color) {
        drawPath(Path().apply { moveTo(c.x, c.y - r); quadraticTo(c.x + r * 0.35f, c.y - r * 0.3f, c.x, c.y + r * 0.2f); quadraticTo(c.x - r * 0.35f, c.y - r * 0.3f, c.x, c.y - r); close() }, color)
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply { moveTo(c.x + s * r * 0.1f, c.y + r * 0.15f); quadraticTo(c.x + s * r * 0.9f, c.y - r * 0.1f, c.x + s * r * 0.75f, c.y - r * 0.7f); quadraticTo(c.x + s * r * 0.5f, c.y - r * 0.05f, c.x + s * r * 0.1f, c.y + r * 0.15f); close() }, color)
            drawLine(color, Offset(c.x, c.y + r * 0.35f), Offset(c.x + s * r * 0.35f, c.y + r * 0.95f), r * 0.18f, StrokeCap.Round)
        }
        drawRect(color, Offset(c.x - r * 0.45f, c.y + r * 0.15f), Size(r * 0.9f, r * 0.2f))
        drawLine(color, Offset(c.x, c.y + r * 0.35f), Offset(c.x, c.y + r * 0.95f), r * 0.18f, StrokeCap.Round)
    }

    /** Los trazos de construcción de los cuadernos (un círculo, su cuadrado, las diagonales), muy tenues. */
    private fun DrawScope.construccion(r: Rect, color: Color, g: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            val c = r.center; val rr = r.height * 0.42f
            drawCircle(color, rr, c, style = Stroke(g))
            drawRect(color, Offset(c.x - rr * 0.89f, c.y - rr * 0.89f), Size(rr * 1.78f, rr * 1.78f), style = Stroke(g))
            drawLine(color, Offset(c.x - rr * 1.6f, c.y), Offset(c.x + rr * 1.6f, c.y), g)
            drawLine(color, Offset(c.x, r.top), Offset(c.x, r.bottom), g)
            drawLine(color, Offset(c.x - rr * 1.2f, c.y - rr * 1.2f), Offset(c.x + rr * 1.2f, c.y + rr * 1.2f), g)
            drawLine(color, Offset(c.x - rr * 1.2f, c.y + rr * 1.2f), Offset(c.x + rr * 1.2f, c.y - rr * 1.2f), g)
            drawCircle(color, rr * 0.5f, Offset(c.x + rr * 1.35f, c.y - rr * 0.2f), style = Stroke(g))
            drawCircle(color, rr * 0.5f, Offset(c.x - rr * 1.35f, c.y - rr * 0.2f), style = Stroke(g))
        }
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.SOCIAL)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val suelo = h * 0.955f

        // La pared de enlucido, con sus manchas de humedad.
        val pared = Rect(0f, 0f, w, suelo)
        rect(pared, vert(pared, nf(ENLUCIDO_OSC, o), nf(ENLUCIDO, o), nf(ENLUCIDO_OSC, o)))
        val rnd = Random(e.semilla.toLong() * 31 + 7)
        repeat(16) { mancha(Offset(w * rnd.nextFloat(), suelo * rnd.nextFloat()), (5f + 12f * rnd.nextFloat()) * u, n(SEPIA, o).copy(alpha = 0.05f)) }

        // El piso de cotto, con las juntas en perspectiva.
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(TERRA_OSC, o), n(TERRACOTA, o)))
        drawLine(n(TERRA_LUZ, o), Offset(0f, suelo + (h - suelo) * 0.5f), Offset(w, suelo + (h - suelo) * 0.5f), p * 0.5f)
        var xj = (w / 2) % (9f * u)
        while (xj < w) { drawLine(n(TERRA_OSC, o), Offset(xj, suelo), Offset(xj + (xj - w / 2) * 0.15f, h), p * 0.6f); xj += 9f * u }

        // El artesonado: casetones hundidos con rosetas de oro.
        val art = Rect(0f, 0f, w, h * 0.07f)
        rect(art, vert(art, n(NOGAL_OSC, o), n(NOGAL, o)))
        val cas = art.height * 0.78f
        val nCas = (w / (cas * 1.25f)).toInt().coerceAtLeast(6)
        val paso = w / nCas
        for (k in 0 until nCas) {
            val c = Rect(paso * k + (paso - cas) / 2, art.top + (art.height - cas) / 2, paso * k + (paso + cas) / 2, art.bottom - (art.height - cas) / 2)
            rect(c, Brush.linearGradient(listOf(n(NOGAL_LUZ, o), n(NOGAL_OSC, o)), c.topLeft, c.bottomRight))
            val in2 = c.deflate(cas * 0.16f)
            rect(in2, n(Color(0xFF283C5E), o))
            roseta(in2.center, in2.width * 0.36f, nm(ORO, o), nm(ORO_LUZ, o))
        }
        // El entablamento de pietra serena: cornisa, dentículos, friso con clípeos y arquitrabe.
        val cor = Rect(0f, art.bottom, w, h * 0.095f)
        rect(cor, vert(cor, n(PIETRA_LUZ, o), n(PIETRA, o), n(PIETRA_OSC, o)))
        denticulos(Rect(0f, cor.bottom, w, h * 0.113f), n(PIETRA_LUZ, o), n(PIETRA, o), n(PIETRA_SOMBRA, o))
        val fr = Rect(0f, h * 0.113f, w, h * 0.15f)
        rect(fr, vert(fr, nf(ENLUCIDO, o), nf(ENLUCIDO_OSC, o)))
        val nCli = 5
        for (k in 0 until nCli) {
            val c = Offset(w * (k + 0.5f) / nCli, fr.center.y)
            drawCircle(n(PIETRA, o), fr.height * 0.38f, c)
            drawCircle(n(PIETRA_LUZ, o), fr.height * 0.38f, c, style = Stroke(p * 0.6f))
            drawCircle(Brush.radialGradient(listOf(n(Color(0xFF6E8BB5), o), n(Color(0xFF2E4A78), o)), c, fr.height * 0.3f), fr.height * 0.28f, c)
            if (k == nCli / 2) lis(c, fr.height * 0.18f, nm(ORO_LUZ, o)) else roseta(c, fr.height * 0.18f, nm(ORO, o), nm(ORO_LUZ, o), 6)
        }
        val arq = Rect(0f, fr.bottom, w, h * 0.172f)
        rect(arq, vert(arq, n(PIETRA_LUZ, o), n(PIETRA, o)))
        drawLine(n(PIETRA_OSC, o), Offset(0f, arq.center.y), Offset(w, arq.center.y), p * 0.6f)
        drawLine(n(PIETRA_SOMBRA, o), Offset(0f, arq.bottom), Offset(w, arq.bottom), p * 0.6f)
        rect(Rect(0f, arq.bottom, w, arq.bottom + 2.4f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), arq.bottom, arq.bottom + 2.4f * u))

        // Los candeleros encendidos: su halo cae en la pared (el cartón lo tapa después).
        val yMesa = h * 0.815f
        val velas = listOf(Offset(w * 0.115f, yMesa), Offset(w * 0.885f, yMesa))
        if (o) velas.forEach { mancha(Offset(it.x, it.y - 6f * u), 34f * u, LLAMA.copy(alpha = 0.18f)) }

        // El cartón enmarcado en pietra serena: el nicho, papel con los trazos de construcción.
        val marco = ni.inflate(2.6f * u)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        rect(marco, Brush.linearGradient(listOf(n(PIETRA_LUZ, o), n(PIETRA, o), n(PIETRA_OSC, o)), marco.topLeft, marco.bottomRight))
        drawRect(n(PIETRA_SOMBRA, o), marco.deflate(0.9f * u).topLeft, marco.deflate(0.9f * u).size, style = Stroke(p * 0.6f))
        drawRect(n(PIETRA_LUZ, o), marco.deflate(1.3f * u).topLeft, marco.deflate(1.3f * u).size, style = Stroke(p * 0.4f))
        rect(ni.inflate(0.6f * u), n(SEPIA, o))
        rect(ni, nicho(e, o, CARTA))
        construccion(ni, (if (o) CARTA else SEPIA).copy(alpha = if (o) 0.06f else 0.08f), p * 0.6f)
        drawLine((if (o) TERRA_LUZ else SANGUINA).copy(alpha = if (o) 0.1f else 0.12f), Offset(ni.left, ni.top + ni.height * 0.62f), Offset(ni.right, ni.top + ni.height * 0.62f), p * 0.7f)
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 2.4f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent), ni.top, ni.top + 2.4f * u))

        // Las dos pilastras corintias de pietra serena.
        val cw = minOf(w * 0.045f, 7f * u)
        listOf(w * 0.05f, w * 0.95f).forEach {
            pilastra(it, arq.bottom, suelo, cw, n(PIETRA, o), n(PIETRA_LUZ, o), n(PIETRA_OSC, o), n(PIETRA, o), n(PIETRA_LUZ, o), p)
        }

        // La mesa larga del refectorio, con su mantel.
        mesaCena(w * 0.1f, w * 0.9f, yMesa, suelo, e, o, p, u)
        velas.forEach { candelero(it, 13f * u, o, p) }
    }

    /** La mesa de la cena: el mantel de lino con pliegues y franjas azules, las patas, el pan, el vino, el peltre y la fruta. */
    private fun DrawScope.mesaCena(x0: Float, x1: Float, yT: Float, yF: Float, e: EstadoMueble, o: Boolean, p: Float, u: Float) {
        sombraBajo(x0, x1, yF, 2.4f * u, 0.45f)
        // Las patas de caballete que asoman bajo el mantel.
        val caida = yT + (yF - yT) * 0.78f
        listOf(x0 + (x1 - x0) * 0.06f, x1 - (x1 - x0) * 0.06f).forEach { x ->
            val pr = Rect(x - 1.4f * u, caida, x + 1.4f * u, yF)
            rect(pr, horiz(pr, n(NOGAL_OSC, o), n(NOGAL_LUZ, o), n(NOGAL_OSC, o)))
            rect(Rect(x - 3f * u, yF - 1f * u, x + 3f * u, yF), n(NOGAL_OSC, o))
        }
        // El mantel: la tabla cubierta y la caída frontal con pliegues.
        val tab = Rect(x0, yT - 1.6f * u, x1, yT)
        rect(tab, vert(tab, n(LINO, o), n(LINO_OSC, o)))
        val cae = Path().apply {
            moveTo(x0, yT); lineTo(x1, yT); lineTo(x1 + 0.6f * u, caida)
            val n = 16
            for (k in n downTo 1) {
                val xa = x0 + (x1 - x0) * k / n; val xb = x0 + (x1 - x0) * (k - 0.5f) / n
                quadraticTo(xb, caida + 1.4f * u, x0 + (x1 - x0) * (k - 1) / n, caida)
                if (xa < 0f) break
            }
            lineTo(x0 - 0.6f * u, caida); close()
        }
        val pl = mutableListOf<Color>()
        for (k in 0..16) pl.add(if (k % 2 == 0) n(LINO, o) else n(LINO_OSC, o))
        drawPath(cae, Brush.horizontalGradient(pl, x0, x1))
        clipPath(cae) {
            // Las franjas azules tejidas cerca del dobladillo (los manteles de Perugia).
            listOf(0.6f, 0.78f).forEach { f ->
                val y = yT + (caida - yT) * f
                drawLine(n(AZUL_LINO, o), Offset(x0 - u, y), Offset(x1 + u, y), 1.0f * u)
            }
            val y = yT + (caida - yT) * 0.69f
            var x = x0
            while (x < x1) {
                drawPath(Path().apply { moveTo(x, y); lineTo(x + 0.9f * u, y - 0.9f * u); lineTo(x + 1.8f * u, y); lineTo(x + 0.9f * u, y + 0.9f * u); close() }, n(AZUL_LINO, o))
                x += 3.2f * u
            }
            rect(Rect(x0, yT, x1, yT + 1.2f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.15f), Color.Transparent), yT, yT + 1.2f * u))
        }
        drawLine(n(LINO, o), Offset(x0, yT - 1.6f * u), Offset(x1, yT - 1.6f * u), p * 0.6f)
        // Lo que hay sobre la mesa, repartido a lo largo.
        val l = x1 - x0
        val ys = yT - 1.2f * u
        val semilla = e.semilla
        for (k in 0 until 9) {
            val x = x0 + l * (0.08f + 0.84f * k / 8f)
            when (k % 3) {
                0 -> { // un plato de peltre con un pan
                    drawOval(n(PELTRE_OSC, o), Offset(x - 2.8f * u, ys - 0.7f * u), Size(5.6f * u, 1.4f * u))
                    drawOval(n(PELTRE, o), Offset(x - 2.4f * u, ys - 0.75f * u), Size(4.8f * u, 1.1f * u))
                    drawOval(Brush.verticalGradient(listOf(n(Color(0xFFE2B06A), o), n(Color(0xFFA8692E), o)), ys - 2.8f * u, ys), Offset(x - 1.7f * u, ys - 2.6f * u), Size(3.4f * u, 2.3f * u))
                    drawLine(n(Color(0xFF8A5222), o), Offset(x - 0.6f * u, ys - 2.2f * u), Offset(x + 0.4f * u, ys - 1.2f * u), p * 0.5f)
                }
                1 -> { // una copa de vino
                    val g = Path().apply { moveTo(x - 1.1f * u, ys - 4.4f * u); quadraticTo(x - 1.1f * u, ys - 2.2f * u, x, ys - 2.1f * u); quadraticTo(x + 1.1f * u, ys - 2.2f * u, x + 1.1f * u, ys - 4.4f * u); close() }
                    drawPath(g, Color.White.copy(alpha = if (o) 0.15f else 0.45f))
                    drawPath(Path().apply { moveTo(x - 1.0f * u, ys - 3.4f * u); quadraticTo(x - 0.95f * u, ys - 2.3f * u, x, ys - 2.2f * u); quadraticTo(x + 0.95f * u, ys - 2.3f * u, x + 1.0f * u, ys - 3.4f * u); close() }, n(VINO, o))
                    drawPath(g, n(PELTRE_OSC, o).copy(alpha = 0.7f), style = Stroke(p * 0.35f))
                    drawLine(n(PELTRE_OSC, o).copy(alpha = 0.8f), Offset(x, ys - 2.1f * u), Offset(x, ys - 0.3f * u), p * 0.5f)
                    drawOval(n(PELTRE_OSC, o).copy(alpha = 0.8f), Offset(x - 0.9f * u, ys - 0.5f * u), Size(1.8f * u, 0.6f * u))
                }
                else -> { // fruta: naranjas o una granada
                    val c1 = if (azar(semilla, k) > 0.5f) n(NARANJA, o) else n(Color(0xFFB0303A), o)
                    listOf(-1.1f, 1.1f, 0f).forEachIndexed { i, dx ->
                        val c = Offset(x + dx * u, ys - (if (i == 2) 2.4f else 1.1f) * u)
                        drawCircle(Brush.radialGradient(listOf(mez(c1, Color.White, 0.3f), c1, mez(c1, Color.Black, 0.3f)), Offset(c.x - 0.4f * u, c.y - 0.4f * u), 1.6f * u), 1.15f * u, c)
                    }
                    drawPath(Path().apply { moveTo(x + 0.3f * u, ys - 3.4f * u); quadraticTo(x + 1.6f * u, ys - 4.4f * u, x + 2.2f * u, ys - 3.4f * u); quadraticTo(x + 1.2f * u, ys - 3f * u, x + 0.3f * u, ys - 3.4f * u); close() }, n(VERDE, o))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.MAPAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val suelo = h * 0.965f

        // Detrás, la pared de enlucido y el piso de cotto.
        val pared = Rect(0f, 0f, w, suelo)
        rect(pared, vert(pared, nf(ENLUCIDO_OSC, o), nf(ENLUCIDO, o)))
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(TERRA_OSC, o), n(TERRACOTA, o)))

        // El tablero de nogal visto desde arriba: canto moldurado, cenefa de taracea y la superficie con su veta.
        val tab = Rect(w * 0.015f, h * 0.03f, w * 0.985f, h * 0.765f)
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(tab.left + 0.8f * u, tab.top + 1.4f * u), tab.size)
        rect(tab, Brush.linearGradient(listOf(n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)), tab.topLeft, tab.bottomRight))
        val sup = tab.deflate(1.3f * u)
        drawRect(n(NOGAL_OSC, o), sup.topLeft, sup.size, style = Stroke(p * 0.6f))
        nogal(sup, o, e.semilla, p)
        // La cenefa: filete de arce, banda de ébano con rombos de arce, filete de arce.
        val cen = sup.deflate(1.4f * u)
        drawRect(n(ARCE, o), cen.topLeft, cen.size, style = Stroke(0.5f * u))
        val cen2 = cen.deflate(1.2f * u)
        drawRect(n(EBANO, o), cen2.topLeft, cen2.size, style = Stroke(1.3f * u))
        var xr = cen2.left + 2.4f * u
        while (xr < cen2.right - 1.2f * u) {
            listOf(cen2.top, cen2.bottom).forEach { y -> drawPath(Path().apply { moveTo(xr - 0.9f * u, y); lineTo(xr, y - 0.55f * u); lineTo(xr + 0.9f * u, y); lineTo(xr, y + 0.55f * u); close() }, n(ARCE, o)) }
            xr += 2.4f * u
        }
        var yr = cen2.top + 2.4f * u
        while (yr < cen2.bottom - 1.2f * u) {
            listOf(cen2.left, cen2.right).forEach { x -> drawPath(Path().apply { moveTo(x, yr - 0.9f * u); lineTo(x + 0.55f * u, yr); lineTo(x, yr + 0.9f * u); lineTo(x - 0.55f * u, yr); close() }, n(ARCE, o)) }
            yr += 2.4f * u
        }
        drawRect(n(ARCE, o), cen2.deflate(1.2f * u).topLeft, cen2.deflate(1.2f * u).size, style = Stroke(0.4f * u))
        // Rosetas de taracea en las esquinas.
        listOf(cen2.topLeft, Offset(cen2.right, cen2.top), Offset(cen2.left, cen2.bottom), cen2.bottomRight).forEach { c ->
            drawCircle(n(ARCE, o), 1.4f * u, c); roseta(c, 1.1f * u, n(NOGAL, o), n(EBANO, o), 6)
        }
        if (o) mancha(Offset(w * 0.1f, h * 0.2f), 36f * u, LLAMA.copy(alpha = 0.16f))

        // El pliego: el papel con su borde de barba, sujeto con cuatro tachuelas, y la regla debajo.
        val pli = ni.inflate(2.2f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(pli.left + 0.8f * u, pli.top + 1.2f * u), pli.size)
        val papel = if (o) mez(n(CARTA, o), NOCHE, 0.15f) else CARTA
        rect(pli, Brush.linearGradient(listOf(mez(papel, Color.White, 0.2f), papel, mez(papel, CARTA_OSC, 0.6f)), pli.topLeft, pli.bottomRight))
        clipRect(pli.left, pli.top, pli.right, pli.bottom) {
            // Los trazos de construcción en el margen: un arco de sanguina y unas medidas en sepia.
            drawCircle((if (o) TERRA_LUZ else SANGUINA).copy(alpha = 0.35f), pli.height * 0.5f, Offset(pli.right + pli.height * 0.25f, pli.bottom), style = Stroke(p * 0.5f))
            for (k in 0 until 22) drawLine(n(SEPIA, o).copy(alpha = 0.4f), Offset(pli.left + pli.width * k / 22f, pli.bottom), Offset(pli.left + pli.width * k / 22f, pli.bottom - (if (k % 5 == 0) 1.4f else 0.7f) * u), p * 0.4f)
        }
        drawRect(n(CARTA_OSC, o), pli.topLeft, pli.size, style = Stroke(p * 0.5f))
        rect(ni, nicho(e, o, CARTA))
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 2f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.07f), Color.Transparent), ni.top, ni.top + 2f * u))
        listOf(Offset(pli.left + 1.1f * u, pli.top + 1.1f * u), Offset(pli.right - 1.1f * u, pli.top + 1.1f * u), Offset(pli.left + 1.1f * u, pli.bottom - 1.1f * u), Offset(pli.right - 1.1f * u, pli.bottom - 1.1f * u)).forEach { tachuela(it, 0.8f * u, o) }
        val reg = Rect(pli.left + pli.width * 0.08f, pli.bottom + 1.2f * u, pli.left + pli.width * 0.62f, pli.bottom + 3f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(reg.left + 0.4f * u, reg.top + 0.7f * u), reg.size)
        rect(reg, vert(reg, n(ARCE, o), n(ARCE_OSC, o)))
        for (k in 0..30) { val x = reg.left + reg.width * k / 30f; drawLine(n(SEPIA, o), Offset(x, reg.top), Offset(x, reg.top + (if (k % 5 == 0) 0.9f else 0.5f) * u), p * 0.4f) }

        // A la izquierda: el códice cerrado, el tintero con la pluma y el portalápiz de sanguina.
        val iz0 = cen2.left + 1.8f * u; val iz1 = pli.left - 1.8f * u
        val izW = iz1 - iz0
        codice(Rect(iz0, h * 0.1f, iz1, h * 0.38f), o, p, u)
        tintero(Offset(iz0 + izW * 0.5f, h * 0.55f), minOf(izW * 0.28f, 3.4f * u), o, p, u)
        val pc0 = Offset(iz0 + izW * 0.1f, h * 0.7f); val pc1 = Offset(iz0 + izW * 0.95f, h * 0.645f)
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(pc0.x + 0.5f * u, pc0.y + 0.8f * u), Offset(pc1.x + 0.5f * u, pc1.y + 0.8f * u), 1.0f * u, StrokeCap.Round)
        drawLine(nm(LATON, o), pc0, pc1, 0.8f * u, StrokeCap.Round)
        drawLine(nm(ORO_LUZ, o), Offset(pc0.x, pc0.y - 0.2f * u), Offset(pc1.x, pc1.y - 0.2f * u), 0.25f * u)
        val ddx = (pc1.x - pc0.x) * 0.12f; val ddy = (pc1.y - pc0.y) * 0.12f
        drawLine(n(SANGUINA, o), Offset(pc0.x - ddx * 0.2f, pc0.y - ddy * 0.2f), Offset(pc0.x - ddx, pc0.y - ddy), 0.9f * u, StrokeCap.Round)

        // A la derecha: el compás abierto, el astrolabio y la escuadra.
        val de0 = pli.right + 1.8f * u; val de1 = cen2.right - 1.8f * u
        val deW = de1 - de0
        compas(Offset(de0 + deW * 0.5f, h * 0.09f), deW * 0.36f, h * 0.27f, o, p, u)
        astrolabio(Offset(de0 + deW * 0.5f, h * 0.5f), minOf(deW * 0.42f, 7f * u), o, p, u)
        val esc = Path().apply {
            val x = de0 + deW * 0.1f; val y = h * 0.72f
            moveTo(x, y); lineTo(x + deW * 0.8f, y); lineTo(x + deW * 0.8f, y - 1.3f * u); lineTo(x + 1.3f * u, y - 1.3f * u); lineTo(x + 1.3f * u, y - deW * 0.45f); lineTo(x, y - deW * 0.45f); close()
        }
        translate(0.5f * u, 0.8f * u) { drawPath(esc, Color.Black.copy(alpha = 0.3f)) }
        drawPath(esc, Brush.linearGradient(listOf(n(ARCE, o), n(ARCE_OSC, o)), Offset(de0, h * 0.6f), Offset(de1, h * 0.72f)))
        drawPath(esc, n(NOGAL_OSC, o), style = Stroke(p * 0.4f))

        // El canto del tablero, el faldón con la taracea de cubos, el cajón y las patas torneadas.
        val canto = Rect(tab.left, tab.bottom, tab.right, tab.bottom + 2f * u)
        rect(canto, vert(canto, n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)))
        drawLine(n(ARCE, o).copy(alpha = 0.7f), Offset(canto.left, canto.top + canto.height * 0.4f), Offset(canto.right, canto.top + canto.height * 0.4f), p * 0.5f)
        val fal = Rect(tab.left + w * 0.02f, canto.bottom, tab.right - w * 0.02f, h * 0.875f)
        nogal(fal, o, e.semilla + 3, p)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), fal.top, fal.top + 1.4f * u), fal.topLeft, Size(fal.width, 1.4f * u))
        val bc = Rect(fal.left + 2f * u, fal.top + fal.height * 0.22f, fal.right - 2f * u, fal.bottom - fal.height * 0.22f)
        cubos(bc, bc.height * 0.36f, o)
        drawRect(n(ARCE, o), bc.topLeft, bc.size, style = Stroke(p * 0.6f))
        val caj = Rect(fal.center.x - fal.width * 0.12f, fal.top + fal.height * 0.12f, fal.center.x + fal.width * 0.12f, fal.bottom - fal.height * 0.12f)
        nogal(caj, o, e.semilla + 5, p)
        drawRect(n(NOGAL_OSC, o), caj.topLeft, caj.size, style = Stroke(p * 0.8f))
        drawRect(n(ARCE, o), caj.deflate(0.7f * u).topLeft, caj.deflate(0.7f * u).size, style = Stroke(p * 0.4f))
        drawCircle(nm(LATON, o), caj.height * 0.25f, Offset(caj.center.x, caj.center.y + caj.height * 0.06f), style = Stroke(p * 0.9f))
        tachuela(Offset(caj.center.x, caj.center.y - caj.height * 0.2f), 0.6f * u, o)
        listOf(tab.left + w * 0.035f, tab.right - w * 0.035f, tab.left + w * 0.26f, tab.right - w * 0.26f).forEachIndexed { i, x ->
            val atras = i >= 2
            pataTorneada(x, fal.bottom, if (atras) suelo - 0.8f * u else suelo, if (atras) w * 0.035f else w * 0.045f, o, p, u, atras)
        }
    }

    /** Una pata torneada en balaustre: dado, anillo, jarrón, cuello, carrete y pie. */
    private fun DrawScope.pataTorneada(cx: Float, y0: Float, y1: Float, an: Float, o: Boolean, p: Float, u: Float, atras: Boolean) {
        val f = if (atras) 0.35f else 0f
        val mad = mez(n(NOGAL, o), Color.Black, f); val luz = mez(n(NOGAL_LUZ, o), Color.Black, f); val osc = mez(n(NOGAL_OSC, o), Color.Black, f)
        if (!atras) sombraBajo(cx - an, cx + an, y1, 1.4f * u, 0.4f)
        val hh = y1 - y0
        fun pieza(r: Rect, oval: Boolean) {
            val b = Brush.horizontalGradient(listOf(osc, luz, mad, osc), r.left, r.right)
            if (oval) drawOval(b, r.topLeft, r.size) else rect(r, b)
        }
        pieza(Rect(cx - an * 0.5f, y0, cx + an * 0.5f, y0 + hh * 0.18f), false)
        pieza(Rect(cx - an * 0.55f, y0 + hh * 0.16f, cx + an * 0.55f, y0 + hh * 0.26f), true)
        pieza(Rect(cx - an * 0.48f, y0 + hh * 0.24f, cx + an * 0.48f, y0 + hh * 0.6f), true)
        pieza(Rect(cx - an * 0.2f, y0 + hh * 0.56f, cx + an * 0.2f, y0 + hh * 0.72f), false)
        pieza(Rect(cx - an * 0.4f, y0 + hh * 0.68f, cx + an * 0.4f, y0 + hh * 0.82f), true)
        pieza(Rect(cx - an * 0.5f, y0 + hh * 0.84f, cx + an * 0.5f, y1), false)
    }

    /** El códice cerrado: tapas de cuero con cantoneras y broches de latón y el canto de las hojas. */
    private fun DrawScope.codice(r: Rect, o: Boolean, p: Float, u: Float) {
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left + 0.7f * u, r.top + 1.1f * u), r.size)
        val hojas = Rect(r.left + 0.8f * u, r.top + 0.6f * u, r.right + 0.1f * u, r.bottom + 0.4f * u)
        rect(hojas, n(CARTA, o))
        for (k in 0..5) drawLine(n(CARTA_OSC, o), Offset(hojas.left, hojas.bottom - k * 0.15f * u), Offset(hojas.right, hojas.bottom - k * 0.15f * u), p * 0.3f)
        rect(r, Brush.linearGradient(listOf(n(mez(CUERO, Color.White, 0.15f), o), n(CUERO, o), n(mez(CUERO, Color.Black, 0.3f), o)), r.topLeft, r.bottomRight))
        drawRect(nm(ORO, o), r.deflate(1f * u).topLeft, r.deflate(1f * u).size, style = Stroke(p * 0.5f))
        drawRect(nm(ORO, o), r.deflate(1.6f * u).topLeft, r.deflate(1.6f * u).size, style = Stroke(p * 0.3f))
        // El nervio del lomo, el medallón y las cantoneras.
        rect(Rect(r.left, r.top, r.left + 1.2f * u, r.bottom), horiz(Rect(r.left, 0f, r.left + 1.2f * u, 0f), n(mez(CUERO, Color.Black, 0.35f), o), n(CUERO, o), n(mez(CUERO, Color.Black, 0.35f), o)))
        drawCircle(nm(ORO, o), minOf(r.width, r.height) * 0.16f, r.center, style = Stroke(p * 0.6f))
        roseta(r.center, minOf(r.width, r.height) * 0.11f, nm(ORO, o), nm(ORO_LUZ, o), 6)
        val ca = minOf(r.width, r.height) * 0.16f
        listOf(Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.right, r.bottom) to Offset(-1f, -1f)).forEach { (c, s) ->
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + s.x * ca, c.y); lineTo(c.x, c.y + s.y * ca); close() }, nm(LATON, o))
        }
        listOf(0.3f, 0.7f).forEach { f ->
            val y = r.top + r.height * f
            rect(Rect(r.right - 1.4f * u, y - 0.6f * u, r.right + 0.6f * u, y + 0.6f * u), vert(Rect(0f, y - 0.6f * u, 0f, y + 0.6f * u), nm(ORO_LUZ, o), nm(ORO_OSC, o)))
        }
    }

    /** El tintero de bronce con su pluma de ave. */
    private fun DrawScope.tintero(c: Offset, r: Float, o: Boolean, p: Float, u: Float) {
        sombraBajo(c.x - r * 1.5f, c.x + r * 1.5f, c.y + r * 0.9f, r * 0.6f, 0.4f)
        drawOval(Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(LATON, o), nm(ORO_OSC, o)), c.x - r * 1.3f, c.x + r * 1.3f), Offset(c.x - r * 1.3f, c.y - r * 0.2f), Size(r * 2.6f, r * 1.1f))
        val cuerpo = Path().apply { moveTo(c.x - r * 1.05f, c.y + r * 0.2f); quadraticTo(c.x - r * 1.2f, c.y - r * 1.1f, c.x - r * 0.45f, c.y - r * 1.05f); lineTo(c.x + r * 0.45f, c.y - r * 1.05f); quadraticTo(c.x + r * 1.2f, c.y - r * 1.1f, c.x + r * 1.05f, c.y + r * 0.2f); close() }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(LATON, o), nm(ORO_OSC, o)), c.x - r * 1.2f, c.x + r * 1.2f))
        drawOval(n(EBANO, o), Offset(c.x - r * 0.45f, c.y - r * 1.2f), Size(r * 0.9f, r * 0.3f))
        // La pluma, inclinada hacia atrás.
        val base = Offset(c.x - r * 0.1f, c.y - r * 1.05f)
        val punta = Offset(c.x + r * 1.6f, c.y - r * 4.2f)
        val pluma = Path().apply {
            moveTo(base.x, base.y); quadraticTo(base.x + r * 0.4f, base.y - r * 1.4f, punta.x - r * 0.9f, punta.y + r * 0.2f)
            quadraticTo(punta.x, punta.y - r * 0.2f, punta.x + r * 0.15f, punta.y - r * 0.4f)
            quadraticTo(punta.x - r * 0.1f, punta.y + r * 1.4f, base.x + r * 0.35f, base.y - r * 0.6f); close()
        }
        drawPath(pluma, Brush.linearGradient(listOf(n(Color(0xFFF7F2E6), o), n(Color(0xFFCFC3AA), o)), base, punta))
        drawLine(n(Color(0xFFA89878), o), base, Offset(punta.x - r * 0.3f, punta.y), p * 0.5f)
        drawPath(pluma, n(Color(0xFFA89878), o).copy(alpha = 0.6f), style = Stroke(p * 0.3f))
    }

    /** El compás de latón abierto, colgado de su eje en [eje], de piernas [largo] y abertura [abre]. */
    private fun DrawScope.compas(eje: Offset, abre: Float, largo: Float, o: Boolean, p: Float, u: Float) {
        val pi = Offset(eje.x - abre, eje.y + largo); val pd = Offset(eje.x + abre, eje.y + largo)
        listOf(pi, pd).forEach { pt ->
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(eje.x + 0.5f * u, eje.y + 0.8f * u), Offset(pt.x + 0.5f * u, pt.y + 0.8f * u), 1f * u, StrokeCap.Round)
        }
        listOf(pi, pd).forEach { pt ->
            val m = Offset(eje.x + (pt.x - eje.x) * 0.85f, eje.y + (pt.y - eje.y) * 0.85f)
            drawLine(nm(LATON, o), eje, m, 0.9f * u, StrokeCap.Round)
            drawLine(nm(ORO_LUZ, o), Offset(eje.x - 0.2f * u, eje.y), Offset(m.x - 0.2f * u, m.y), 0.25f * u)
            drawLine(n(PELTRE, o), m, pt, 0.45f * u, StrokeCap.Round)
        }
        drawCircle(Brush.radialGradient(listOf(nm(ORO_LUZ, o), nm(LATON, o), nm(ORO_OSC, o)), Offset(eje.x - 0.3f * u, eje.y - 0.3f * u), 1.6f * u), 1.2f * u, eje)
        drawCircle(nm(ORO_OSC, o), 0.35f * u, eje)
        // El arco de medida entre las piernas.
        drawArc(nm(LATON, o), 60f, 60f, false, Offset(eje.x - largo * 0.55f, eje.y - largo * 0.55f), Size(largo * 1.1f, largo * 1.1f), style = Stroke(0.35f * u))
    }

    /** El astrolabio de latón: la madre con su limbo graduado, la red con sus estrellas, la alidada y el trono con la argolla. */
    private fun DrawScope.astrolabio(c: Offset, r: Float, o: Boolean, p: Float, u: Float) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + 0.6f * u, c.y + 1f * u))
        // El trono y la argolla.
        drawPath(Path().apply { moveTo(c.x - r * 0.3f, c.y - r * 0.9f); quadraticTo(c.x, c.y - r * 1.45f, c.x + r * 0.3f, c.y - r * 0.9f); close() }, nm(LATON, o))
        drawCircle(nm(ORO_OSC, o), r * 0.18f, Offset(c.x, c.y - r * 1.42f), style = Stroke(p * 0.8f))
        drawCircle(Brush.radialGradient(listOf(nm(ORO_LUZ, o), nm(LATON, o), nm(ORO_OSC, o)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.4f), r, c)
        drawCircle(nm(ORO_OSC, o), r * 0.82f, c, style = Stroke(p * 0.5f))
        for (k in 0 until 36) {
            val a = 6.2832f * k / 36
            val r0 = if (k % 3 == 0) r * 0.82f else r * 0.88f
            drawLine(nm(ORO_OSC, o), Offset(c.x + r0 * cos(a), c.y + r0 * sin(a)), Offset(c.x + r * 0.95f * cos(a), c.y + r * 0.95f * sin(a)), p * 0.35f)
        }
        drawCircle(nm(Color(0xFF9C7230), o), r * 0.8f, c)
        // La red: el círculo de la eclíptica, el trópico y los punteros de las estrellas.
        drawCircle(nm(ORO_LUZ, o), r * 0.5f, Offset(c.x, c.y - r * 0.2f), style = Stroke(p * 0.6f))
        drawCircle(nm(ORO_LUZ, o), r * 0.7f, c, style = Stroke(p * 0.5f))
        for (k in 0 until 5) {
            val a = 6.2832f * k / 5 + 0.4f
            val t = Offset(c.x + r * 0.62f * cos(a), c.y + r * 0.62f * sin(a))
            drawPath(Path().apply { moveTo(c.x + r * 0.35f * cos(a + 0.3f), c.y + r * 0.35f * sin(a + 0.3f)); quadraticTo(c.x + r * 0.55f * cos(a + 0.4f), c.y + r * 0.55f * sin(a + 0.4f), t.x, t.y) }, nm(ORO_LUZ, o), style = Stroke(p * 0.5f))
        }
        // La alidada.
        drawLine(nm(ORO_OSC, o), Offset(c.x - r * 0.8f * 0.7f, c.y + r * 0.8f * 0.7f), Offset(c.x + r * 0.8f * 0.7f, c.y - r * 0.8f * 0.7f), p * 1.2f)
        drawCircle(nm(ORO_LUZ, o), r * 0.1f, c)
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.OTRAS_VIDAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val x0 = w * 0.012f; val x1 = w * 0.988f

        // El frontón curvo con la concha de oro y las acróteras.
        val corTop = h * 0.125f
        val fron = Path().apply {
            moveTo(w * 0.24f, corTop); quadraticTo(w * 0.5f, -h * 0.07f, w * 0.76f, corTop); close()
        }
        drawPath(fron, vert(Rect(0f, 0f, 0f, corTop), n(NOGAL_LUZ, o), n(NOGAL_OSC, o)))
        drawPath(Path().apply { moveTo(w * 0.24f, corTop); quadraticTo(w * 0.5f, -h * 0.07f, w * 0.76f, corTop) }, n(NOGAL_OSC, o), style = Stroke(p * 1.2f))
        drawPath(Path().apply { moveTo(w * 0.27f, corTop); quadraticTo(w * 0.5f, -h * 0.03f, w * 0.73f, corTop) }, n(ARCE, o).copy(alpha = 0.7f), style = Stroke(p * 0.5f))
        venera(Offset(w / 2, corTop - 0.3f * u), minOf(w * 0.06f, 9f * u), (corTop - h * 0.02f) * 0.95f, nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o), p * 0.5f)
        listOf(x0 + w * 0.03f, x1 - w * 0.03f, w * 0.24f, w * 0.76f).forEach { x ->
            drawRect(n(NOGAL, o), Offset(x - 1.4f * u, corTop - 1.4f * u), Size(2.8f * u, 1.4f * u))
            drawCircle(Brush.radialGradient(listOf(nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o)), Offset(x - 0.6f * u, corTop - 3.4f * u), 2.4f * u), 1.6f * u, Offset(x, corTop - 2.9f * u))
        }

        // La cornisa con dentículos y el friso de taracea con rosetas.
        val cor = Rect(x0, corTop, x1, corTop + h * 0.045f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cor.left, cor.bottom), Size(cor.width, 1.2f * u))
        rect(cor, vert(cor, n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)))
        drawLine(n(ARCE, o).copy(alpha = 0.6f), Offset(cor.left, cor.top + p), Offset(cor.right, cor.top + p), p * 0.5f)
        denticulos(Rect(x0 + w * 0.006f, cor.bottom, x1 - w * 0.006f, cor.bottom + h * 0.03f), n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o))
        val fri = Rect(x0 + w * 0.01f, cor.bottom + h * 0.03f, x1 - w * 0.01f, ni.top - h * 0.03f)
        rect(fri, n(EBANO, o))
        drawRect(n(ARCE, o), fri.deflate(0.5f * u).topLeft, fri.deflate(0.5f * u).size, style = Stroke(p * 0.5f))
        val nR = 9
        for (k in 0 until nR) {
            val c = Offset(fri.left + fri.width * (k + 0.5f) / nR, fri.center.y)
            roseta(c, fri.height * 0.3f, n(ARCE, o), n(ARCE_OSC, o), 8)
            if (k < nR - 1) {
                val cx = fri.left + fri.width * (k + 1f) / nR
                drawPath(Path().apply { moveTo(cx - fri.height * 0.5f, fri.center.y); lineTo(cx, fri.center.y - fri.height * 0.22f); lineTo(cx + fri.height * 0.5f, fri.center.y); lineTo(cx, fri.center.y + fri.height * 0.22f); close() }, n(NOGAL_LUZ, o))
            }
        }
        val arq = Rect(x0 + w * 0.006f, fri.bottom, x1 - w * 0.006f, ni.top - h * 0.008f)
        rect(arq, vert(arq, n(NOGAL_LUZ, o), n(NOGAL_OSC, o)))

        // El cuerpo de nogal y las dos pilastras exteriores.
        val cuerpo = Rect(x0 + w * 0.01f, arq.bottom, x1 - w * 0.01f, ni.bottom + h * 0.025f)
        nogal(cuerpo, o, e.semilla, p, vertical = true)
        val pw = minOf((ni.left - cuerpo.left) * 0.62f, 6f * u)
        listOf(cuerpo.left + (ni.left - cuerpo.left) / 2, cuerpo.right - (cuerpo.right - ni.right) / 2).forEach { cx ->
            pilastra(cx, cuerpo.top + 0.4f * u, cuerpo.bottom - 0.4f * u, pw, n(NOGAL, o), n(NOGAL_LUZ, o), n(NOGAL_OSC, o), nm(ORO, o), nm(ORO_LUZ, o), p, sombra = false)
        }

        // Las cinco hornacinas de arce, iguales, con su arco, su venera y la sombra del fondo.
        val sep = 2.2f * u
        val cw = ni.width / 5f
        rect(ni, n(NOGAL_OSC, o))
        val forro = nicho(e, o, ARCE)
        for (i in 0 until 5) {
            val c = Rect(ni.left + cw * i + sep / 2, ni.top, ni.left + cw * (i + 1) - sep / 2, ni.bottom)
            rect(c, forro)
            vetas(c, (if (o) Color.White else ARCE_OSC).copy(alpha = if (o) 0.03f else 0.18f), e.semilla + i, p, densidad = 0.5f, vertical = true)
            rect(Rect(c.left, c.top, c.left + 2f * u, c.bottom), Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (o) 0.22f else 0.12f), Color.Transparent), c.left, c.left + 2f * u))
            rect(Rect(c.right - 1.2f * u, c.top, c.right, c.bottom), Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (o) 0.15f else 0.07f)), c.right - 1.2f * u, c.right))
            // El arco de medio punto: las enjutas de nogal con su filete de arce.
            val ah = minOf(c.width * 0.5f, c.height * 0.2f)
            val en = Path().apply {
                moveTo(c.left, c.top); lineTo(c.right, c.top); lineTo(c.right, c.top + ah)
                quadraticTo(c.right, c.top, c.center.x, c.top); quadraticTo(c.left, c.top, c.left, c.top + ah); close()
            }
            drawPath(en, vert(Rect(0f, c.top, 0f, c.top + ah), n(NOGAL_LUZ, o), n(NOGAL, o)))
            drawPath(Path().apply { moveTo(c.right, c.top + ah); quadraticTo(c.right, c.top, c.center.x, c.top); quadraticTo(c.left, c.top, c.left, c.top + ah) }, nm(ORO, o), style = Stroke(p * 0.6f))
            // La venera dentro del arco, tenue sobre el arce.
            venera(Offset(c.center.x, c.top + ah * 1.05f), c.width * 0.36f, ah * 0.78f, (if (o) mez(forro, Color.White, 0.06f) else mez(forro, Color.White, 0.3f)), (if (o) mez(forro, Color.Black, 0.2f) else mez(forro, ARCE_OSC, 0.5f)),
                (if (o) Color.White.copy(alpha = 0.12f) else ARCE_OSC), p * 0.4f)
            rect(Rect(c.left, c.top + ah * 0.6f, c.right, c.top + ah * 1.3f), Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (o) 0.15f else 0.06f), Color.Transparent), c.top + ah * 0.6f, c.top + ah * 1.3f))
            // El repisón al pie.
            val rp = Rect(c.left, c.bottom - minOf(2f * u, c.height * 0.05f), c.right, c.bottom)
            rect(rp, vert(rp, n(NOGAL_LUZ, o), n(NOGAL_OSC, o)))
        }
        // Las pilastrillas acanaladas entre hornacinas.
        for (i in 0..5) {
            val x = ni.left + cw * i
            val s = Rect(x - sep / 2, ni.top, x + sep / 2, ni.bottom)
            rect(s, horiz(s, n(NOGAL_OSC, o), n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)))
            drawLine(n(NOGAL_OSC, o), Offset(x, s.top + 2f * u), Offset(x, s.bottom - 1.5f * u), p * 0.5f)
            rect(Rect(s.left - 0.3f * u, s.top, s.right + 0.3f * u, s.top + 1.4f * u), vert(Rect(0f, s.top, 0f, s.top + 1.4f * u), nm(ORO_LUZ, o), nm(ORO_OSC, o)))
        }
        drawRect(n(NOGAL_OSC, o), ni.topLeft, ni.size, style = Stroke(p * 0.8f))

        // El zócalo: moldura, banda de taracea de cubos y las patas de bollo.
        val zm = Rect(x0, cuerpo.bottom, x1, cuerpo.bottom + h * 0.03f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zm.left, zm.top - 0.6f * u), Size(zm.width, 0.6f * u))
        rect(zm, vert(zm, n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)))
        drawLine(n(ARCE, o).copy(alpha = 0.6f), Offset(zm.left, zm.top + p), Offset(zm.right, zm.top + p), p * 0.5f)
        val zo = Rect(x0 + w * 0.01f, zm.bottom, x1 - w * 0.01f, h * 0.935f)
        nogal(zo, o, e.semilla + 2, p)
        val bc = zo.deflate(zo.height * 0.2f)
        cubos(bc, bc.height * 0.36f, o)
        drawRect(n(ARCE, o), bc.topLeft, bc.size, style = Stroke(p * 0.6f))
        val suelo = h * 0.995f
        listOf(x0 + w * 0.05f, x1 - w * 0.05f, w * 0.35f, w * 0.65f).forEach { x ->
            val r = (suelo - zo.bottom) * 0.62f
            sombraBajo(x - r * 1.5f, x + r * 1.5f, suelo, r * 0.6f, 0.4f)
            drawOval(Brush.radialGradient(listOf(n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)), Offset(x - r * 0.4f, zo.bottom + r * 0.3f), r * 1.6f), Offset(x - r * 1.2f, zo.bottom), Size(r * 2.4f, suelo - zo.bottom))
        }
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u0 = h / 70f
        val o = oscuro
        val cx = w / 2
        when (id) {
            "unesco" -> {
                // La cúpula de Brunelleschi sobre su tambor, con la nave de mármol blanco y verde y la linterna.
                val u = u0 * 1.1f
                val suelo = h * 0.9f
                sombraBajo(cx - 40f * u, cx + 40f * u, suelo, 4f * u, 0.3f)
                // Las naves a los lados, con mármol blanco y franjas verdes.
                listOf(-1f, 1f).forEach { s ->
                    val nv = Rect(if (s < 0) cx - 38f * u else cx + 10f * u, suelo - 14f * u, if (s < 0) cx - 10f * u else cx + 38f * u, suelo)
                    rect(nv, vert(nv, n(Color(0xFFF5EFE2), o), n(Color(0xFFD9CFBC), o)))
                    for (k in 0..4) { val x = nv.left + nv.width * k / 4f; drawLine(n(VERDE, o), Offset(x, nv.top), Offset(x, nv.bottom), 0.8f * u) }
                    drawLine(n(VERDE, o), Offset(nv.left, nv.top + 3f * u), Offset(nv.right, nv.top + 3f * u), 0.6f * u)
                    for (k in 0 until 4) drawPath(Path().apply { val x = nv.left + nv.width * (k + 0.5f) / 4f; moveTo(x - 1.2f * u, nv.bottom - 2f * u); lineTo(x - 1.2f * u, nv.top + 6f * u); quadraticTo(x, nv.top + 4.6f * u, x + 1.2f * u, nv.top + 6f * u); lineTo(x + 1.2f * u, nv.bottom - 2f * u); close() }, n(Color(0xFF3A3328), o))
                    val te = Path().apply { moveTo(nv.left - 1f * u, nv.top); lineTo(nv.right + 1f * u, nv.top); lineTo(nv.right - 1f * u, nv.top - 3f * u); lineTo(nv.left + 1f * u, nv.top - 3f * u); close() }
                    drawPath(te, vert(Rect(0f, nv.top - 3f * u, 0f, nv.top), n(TERRA_LUZ, o), n(TERRA_OSC, o)))
                }
                // El tambor octogonal con sus óculos.
                val tb = Rect(cx - 13f * u, suelo - 24f * u, cx + 13f * u, suelo - 12f * u)
                rect(tb, horiz(tb, n(Color(0xFFD9CFBC), o), n(Color(0xFFF7F2E6), o), n(Color(0xFFE8E0CE), o), n(Color(0xFFBDB29A), o)))
                listOf(-1f, 1f).forEach { s -> drawLine(n(VERDE, o), Offset(cx + s * 6.5f * u, tb.top), Offset(cx + s * 6.5f * u, tb.bottom), 0.6f * u) }
                listOf(-9.8f, 0f, 9.8f).forEach { dx -> drawCircle(n(Color(0xFF3A3328), o), 1.6f * u, Offset(cx + dx * u, tb.center.y)); drawCircle(n(VERDE, o), 1.6f * u, Offset(cx + dx * u, tb.center.y), style = Stroke(0.5f * u)) }
                rect(Rect(tb.left - 1f * u, tb.top - 1.2f * u, tb.right + 1f * u, tb.top), n(Color(0xFFF7F2E6), o))
                // La cúpula de ladrillo con sus ocho nervios blancos.
                val top = tb.top - 22f * u
                val cup = Path().apply { moveTo(tb.left, tb.top - 1.2f * u); cubicTo(tb.left, top + 9f * u, cx - 5f * u, top + 1f * u, cx - 3f * u, top); lineTo(cx + 3f * u, top); cubicTo(cx + 5f * u, top + 1f * u, tb.right, top + 9f * u, tb.right, tb.top - 1.2f * u); close() }
                drawPath(cup, Brush.horizontalGradient(listOf(n(TERRA_OSC, o), n(TERRA_LUZ, o), n(TERRACOTA, o), n(TERRA_OSC, o)), tb.left, tb.right))
                clipPath(cup) {
                    for (k in 1..5) { val y = tb.top - 1.2f * u - k * 3.6f * u; drawLine(n(TERRA_OSC, o).copy(alpha = 0.5f), Offset(tb.left, y), Offset(tb.right, y), 0.3f * u) }
                }
                listOf(-1f, -0.45f, 0.45f, 1f).forEach { f ->
                    drawPath(Path().apply { moveTo(cx + f * 13f * u, tb.top - 1.2f * u); quadraticTo(cx + f * 11f * u, top + 6f * u, cx + f * 3f * u, top) }, n(Color(0xFFF7F2E6), o), style = Stroke(0.9f * u))
                }
                // La linterna con su bola dorada.
                val li = Rect(cx - 2.6f * u, top - 6f * u, cx + 2.6f * u, top)
                rect(li, horiz(li, n(Color(0xFFD9CFBC), o), n(Color(0xFFF7F2E6), o), n(Color(0xFFBDB29A), o)))
                drawLine(n(Color(0xFF3A3328), o), Offset(cx, li.top + 1f * u), Offset(cx, li.bottom - 1f * u), 0.8f * u)
                drawPath(Path().apply { moveTo(li.left, li.top); lineTo(cx, li.top - 4f * u); lineTo(li.right, li.top) }, n(Color(0xFFF7F2E6), o))
                drawCircle(Brush.radialGradient(listOf(nm(ORO_LUZ, o), nm(ORO, o)), Offset(cx - 0.3f * u, li.top - 4.8f * u), 1.4f * u), 1f * u, Offset(cx, li.top - 4.6f * u))
                drawLine(nm(ORO, o), Offset(cx, li.top - 5.6f * u), Offset(cx, li.top - 7.4f * u), 0.4f * u)
                drawLine(nm(ORO, o), Offset(cx - 0.9f * u, li.top - 6.7f * u), Offset(cx + 0.9f * u, li.top - 6.7f * u), 0.4f * u)
                // La puerta mayor y el estandarte con el acento.
                drawPath(Path().apply { moveTo(cx - 2.4f * u, suelo); lineTo(cx - 2.4f * u, suelo - 6f * u); quadraticTo(cx, suelo - 8.5f * u, cx + 2.4f * u, suelo - 6f * u); lineTo(cx + 2.4f * u, suelo); close() }, n(e.c1, o))
                rect(Rect(cx - 10f * u, suelo - 12f * u, cx + 10f * u, suelo), Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f)), suelo - 12f * u, suelo))
            }
            "rascar" -> {
                // Un pliego sombreado a plumilla (la capa que se rasca), una esquina ya rascada y el florín de oro.
                val u = u0 * 1.1f
                val r = Rect(cx - 34f * u, h * 0.5f - 24f * u, cx + 18f * u, h * 0.5f + 24f * u)
                rotate(-4f, r.center) {
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1f * u, r.top + 1.6f * u), r.size)
                    rect(r, Brush.linearGradient(listOf(n(Color(0xFFF7ECCF), o), n(CARTA_OSC, o)), r.topLeft, r.bottomRight))
                    val capa = r.deflate(3.5f * u)
                    rect(capa, n(Color(0xFF8E6A44), o))
                    clipRect(capa.left, capa.top, capa.right, capa.bottom) {
                        var x = capa.left - capa.height
                        while (x < capa.right) { drawLine(n(SEPIA, o), Offset(x, capa.bottom), Offset(x + capa.height, capa.top), 0.5f * u); x += 1.6f * u }
                        val ras = Path().apply { moveTo(capa.left, capa.top + capa.height * 0.35f); quadraticTo(capa.left + capa.width * 0.4f, capa.top + capa.height * 0.15f, capa.left + capa.width * 0.62f, capa.top + capa.height * 0.55f); quadraticTo(capa.left + capa.width * 0.45f, capa.bottom, capa.left + capa.width * 0.15f, capa.bottom); lineTo(capa.left, capa.bottom); close() }
                        drawPath(ras, n(Color(0xFFF3E6C6), o))
                        drawPath(Path().apply { moveTo(capa.left + capa.width * 0.06f, capa.top + capa.height * 0.55f); quadraticTo(capa.left + capa.width * 0.3f, capa.top + capa.height * 0.3f, capa.left + capa.width * 0.45f, capa.top + capa.height * 0.7f); lineTo(capa.left + capa.width * 0.2f, capa.bottom - 1f * u); close() }, n(e.c1, o))
                        drawCircle((if (o) TERRA_LUZ else SANGUINA).copy(alpha = 0.8f), capa.height * 0.35f, Offset(capa.left + capa.width * 0.25f, capa.bottom), style = Stroke(0.4f * u))
                        drawPath(ras, n(SEPIA, o), style = Stroke(0.5f * u))
                    }
                }
                // El florín de oro con la flor de lis, de canto.
                val fc = Offset(cx + 22f * u, h * 0.5f + 6f * u)
                val fr = 15f * u
                drawCircle(Color.Black.copy(alpha = 0.35f), fr, Offset(fc.x + 1.4f * u, fc.y + 2f * u))
                drawCircle(Brush.radialGradient(listOf(nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o)), Offset(fc.x - fr * 0.35f, fc.y - fr * 0.35f), fr * 1.5f), fr, fc)
                drawCircle(nm(ORO_OSC, o), fr * 0.86f, fc, style = Stroke(0.6f * u))
                for (k in 0 until 40) { val an = 6.2832f * k / 40; drawCircle(nm(ORO_OSC, o), 0.35f * u, Offset(fc.x + fr * 0.93f * cos(an), fc.y + fr * 0.93f * sin(an))) }
                lis(fc, fr * 0.55f, nm(ORO_OSC, o))
                drawPath(Path().apply { addArc(Rect(fc.x - fr * 0.75f, fc.y - fr * 0.75f, fc.x + fr * 0.75f, fc.y + fr * 0.75f), 200f, 60f) }, Color.White.copy(alpha = 0.5f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
                listOf(Offset(fc.x - 16f * u, fc.y + 12f * u), Offset(fc.x - 12f * u, fc.y + 16f * u)).forEach { drawCircle(n(Color(0xFF8E6A44), o), 0.8f * u, it) }
            }
            else -> {
                // Tus lugares: un mapa de cuaderno con tres sellos de lacre, el camino punteado y la rosa de los vientos.
                val u = u0 * 1.1f
                val r = Rect(cx - 40f * u, h * 0.5f - 22f * u, cx + 40f * u, h * 0.5f + 22f * u)
                drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + 1f * u, r.top + 1.6f * u), r.size)
                rect(r, Brush.linearGradient(listOf(n(Color(0xFFF7ECCF), o), n(CARTA_OSC, o)), r.topLeft, r.bottomRight))
                drawRect(n(SEPIA, o), r.deflate(1.5f * u).topLeft, r.deflate(1.5f * u).size, style = Stroke(0.4f * u))
                // La costa y el río a plumilla, con el sombreado del mar.
                val costa = Path().apply { moveTo(r.left + 1.5f * u, r.top + 30f * u); quadraticTo(r.left + 12f * u, r.top + 24f * u, r.left + 16f * u, r.bottom - 1.5f * u) }
                drawPath(costa, n(SEPIA, o), style = Stroke(0.6f * u))
                clipRect(r.left + 1.5f * u, r.top + 24f * u, r.left + 16f * u, r.bottom - 1.5f * u) {
                    for (k in 0 until 6) drawLine(n(SEPIA, o).copy(alpha = 0.5f), Offset(r.left + 1.5f * u, r.top + 33f * u + k * 1.6f * u), Offset(r.left + 6f * u + k * 1.2f * u, r.top + 33f * u + k * 1.6f * u), 0.3f * u)
                }
                drawPath(Path().apply { moveTo(r.left + 26f * u, r.top + 1.5f * u); cubicTo(r.left + 30f * u, r.top + 14f * u, r.left + 40f * u, r.top + 18f * u, r.right - 1.5f * u, r.top + 24f * u) }, n(Color(0xFF4A6A8A), o), style = Stroke(0.7f * u))
                // La rosa de los vientos.
                val rc = Offset(r.right - 9f * u, r.bottom - 9f * u)
                drawCircle(n(SEPIA, o), 5f * u, rc, style = Stroke(0.35f * u))
                for (k in 0 until 8) {
                    val an = 6.2832f * k / 8 - 1.5708f
                    val l = if (k % 2 == 0) 6.4f * u else 3.6f * u
                    val pt = Offset(rc.x + l * cos(an), rc.y + l * sin(an))
                    val lat = Offset(rc.x + 1f * u * cos(an + 1.5708f), rc.y + 1f * u * sin(an + 1.5708f))
                    drawPath(Path().apply { moveTo(rc.x, rc.y); lineTo(lat.x, lat.y); lineTo(pt.x, pt.y); close() }, if (k == 0) n(SANGUINA, o) else n(SEPIA, o))
                }
                // El camino y los tres sellos.
                val lugares = listOf(Offset(r.left + 22f * u, r.bottom - 10f * u), Offset(cx + 2f * u, r.top + 11f * u), Offset(r.right - 22f * u, r.top + 22f * u))
                drawPath(Path().apply { moveTo(lugares[0].x, lugares[0].y); quadraticTo(cx - 14f * u, r.top + 8f * u, lugares[1].x, lugares[1].y); quadraticTo(cx + 16f * u, r.top + 10f * u, lugares[2].x, lugares[2].y) },
                    n(SANGUINA, o), style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 1.2f * u))))
                lugares.forEachIndexed { i, c -> sello(c, (if (i == 1) 4.6f else 4f) * u, n(if (i == 0) e.c2 else e.c1, o), 31 + i) }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.NOTAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val x0 = w * 0.01f; val x1 = w * 0.99f

        // El frontón bajo con la concha y las acróteras.
        val corTop = h * 0.06f
        drawPath(Path().apply { moveTo(w * 0.28f, corTop); quadraticTo(w * 0.5f, -h * 0.035f, w * 0.72f, corTop); close() }, vert(Rect(0f, 0f, 0f, corTop), n(NOGAL_LUZ, o), n(NOGAL_OSC, o)))
        drawPath(Path().apply { moveTo(w * 0.28f, corTop); quadraticTo(w * 0.5f, -h * 0.035f, w * 0.72f, corTop) }, n(NOGAL_OSC, o), style = Stroke(p * 1.2f))
        venera(Offset(w / 2, corTop - 0.2f * u), minOf(w * 0.05f, 7f * u), corTop * 0.72f, nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o), p * 0.5f)
        listOf(x0 + w * 0.03f, x1 - w * 0.03f).forEach { x ->
            drawCircle(Brush.radialGradient(listOf(nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o)), Offset(x - 0.6f * u, corTop - 2.6f * u), 2.2f * u), 1.5f * u, Offset(x, corTop - 1.6f * u))
        }

        // El marco: cornisa con dentículos, largueros con pilastras y el panel de taracea (el nicho).
        val marco = Rect(x0, corTop, x1, h * 0.935f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        nogal(marco, o, e.semilla, p)
        val cor = Rect(x0, corTop, x1, corTop + h * 0.03f)
        rect(cor, vert(cor, n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)))
        drawLine(n(ARCE, o).copy(alpha = 0.6f), Offset(cor.left, cor.top + p), Offset(cor.right, cor.top + p), p * 0.5f)
        denticulos(Rect(x0 + w * 0.006f, cor.bottom, x1 - w * 0.006f, cor.bottom + h * 0.022f), n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o))
        val fri = Rect(x0 + w * 0.03f, cor.bottom + h * 0.03f, x1 - w * 0.03f, ni.top - 1.6f * u)
        if (fri.height > 0f) {
            cubos(fri, fri.height * 0.36f, o)
            drawRect(n(ARCE, o), fri.topLeft, fri.size, style = Stroke(p * 0.6f))
        }
        val pw = minOf((ni.left - marco.left) * 0.55f, 6f * u)
        listOf(marco.left + (ni.left - marco.left) / 2, marco.right - (marco.right - ni.right) / 2).forEach { cx ->
            pilastra(cx, ni.top - 1f * u, ni.bottom + 1f * u, pw, n(NOGAL, o), n(NOGAL_LUZ, o), n(NOGAL_OSC, o), nm(ORO, o), nm(ORO_LUZ, o), p, sombra = false)
        }
        // El panel: un bisel de nogal, un filete de arce y el campo de taracea de arce con su veta tenue.
        rect(ni.inflate(1.3f * u), Brush.linearGradient(listOf(n(NOGAL_OSC, o), n(NOGAL_LUZ, o)), ni.topLeft, ni.bottomRight))
        rect(ni.inflate(0.5f * u), n(ARCE, o))
        rect(ni, nicho(e, o, ARCE))
        vetas(ni, (if (o) Color.White else ARCE_OSC).copy(alpha = if (o) 0.025f else 0.16f), e.semilla + 4, p * 1.2f, densidad = 0.5f)
        construccion(ni, (if (o) ARCE else SEPIA).copy(alpha = if (o) 0.04f else 0.05f), p * 0.6f)
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 2.6f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent), ni.top, ni.top + 2.6f * u))
        rect(Rect(ni.left, ni.top, ni.left + 2f * u, ni.bottom), Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent), ni.left, ni.left + 2f * u))

        // La repisa moldurada con sus ménsulas; encima, el compás, la barra de sanguina y la vela.
        val rep = Rect(0f, ni.bottom + 1.6f * u, w, ni.bottom + 4.4f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 1.2f * u))
        rect(rep, vert(rep, n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)))
        drawLine(n(ARCE, o).copy(alpha = 0.7f), Offset(0f, rep.top + rep.height * 0.35f), Offset(w, rep.top + rep.height * 0.35f), p * 0.5f)
        listOf(w * 0.12f, w * 0.88f).forEach { x ->
            val men = Path().apply { moveTo(x - 2.6f * u, rep.bottom); lineTo(x + 2.6f * u, rep.bottom); quadraticTo(x + 2.4f * u, h * 0.975f, x, h * 0.995f); quadraticTo(x - 2.4f * u, h * 0.975f, x - 2.6f * u, rep.bottom); close() }
            drawPath(men, Brush.horizontalGradient(listOf(n(NOGAL_OSC, o), n(NOGAL_LUZ, o), n(NOGAL, o)), x - 2.6f * u, x + 2.6f * u))
            drawCircle(nm(ORO, o), 0.9f * u, Offset(x, rep.bottom + 2f * u), style = Stroke(p * 0.6f))
        }
        // La barra de sanguina en su portalápiz.
        val s0 = Offset(w * 0.24f, rep.top - 0.6f * u); val s1 = Offset(w * 0.38f, rep.top - 1.1f * u)
        drawLine(nm(LATON, o), s0, s1, 0.8f * u, StrokeCap.Round)
        drawLine(n(SANGUINA, o), s1, Offset(s1.x + w * 0.025f, s1.y - 0.1f * u), 0.9f * u, StrokeCap.Round)
        // El compás tumbado.
        val cp = Offset(w * 0.58f, rep.top - 0.8f * u)
        drawLine(nm(LATON, o), cp, Offset(cp.x + w * 0.09f, rep.top - 0.3f * u), 0.7f * u, StrokeCap.Round)
        drawLine(nm(LATON, o), cp, Offset(cp.x + w * 0.085f, rep.top - 2.2f * u), 0.7f * u, StrokeCap.Round)
        drawCircle(nm(ORO_LUZ, o), 0.8f * u, cp)
        // La vela en su palmatoria.
        val vx = w * 0.76f
        drawOval(Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(LATON, o)), vx - 2.4f * u, vx + 2.4f * u), Offset(vx - 2.4f * u, rep.top - 1f * u), Size(4.8f * u, 1.3f * u))
        val vela = Rect(vx - 0.8f * u, rep.top - 5.4f * u, vx + 0.8f * u, rep.top - 0.5f * u)
        rect(vela, horiz(vela, n(Color(0xFFD9CDB0), o), n(Color(0xFFF8F2E2), o), n(Color(0xFFD9CDB0), o)))
        llama(Offset(vx, vela.top - 1.2f * u), 0.8f * u, o)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val semilla = e.indice * 131 + e.tipo.length * 7
        // La hoja de cuaderno: los bordes de barba (irregulares) y una leve inclinación.
        val hoja = r.deflate(2.2f * un)
        val forma = Path().apply {
            var k = 0
            moveTo(hoja.left, hoja.top)
            for (s in 1..10) lineTo(hoja.left + hoja.width * s / 10, hoja.top + (azar(semilla, k++) - 0.5f) * 1.2f * un)
            for (s in 1..10) lineTo(hoja.right + (azar(semilla, k++) - 0.5f) * 1.2f * un, hoja.top + hoja.height * s / 10)
            for (s in 9 downTo 0) lineTo(hoja.left + hoja.width * s / 10, hoja.bottom + (azar(semilla, k++) - 0.5f) * 1.2f * un)
            for (s in 9 downTo 1) lineTo(hoja.left + (azar(semilla, k++) - 0.5f) * 1.2f * un, hoja.top + hoja.height * s / 10)
            close()
        }
        val giro = (azar(semilla, 99) - 0.5f) * 2.2f
        rotate(giro, r.center) {
            translate(1.2f * un, 2f * un) { drawPath(forma, Color.Black.copy(alpha = if (o) 0.45f else 0.25f)) }
            val papel = if (o) HOJA_NOCHE else HOJA
            drawPath(forma, Brush.linearGradient(listOf(mez(papel, Color.White, if (o) 0.05f else 0.35f), papel, mez(papel, if (o) Color.Black else CARTA_OSC, 0.4f)), r.topLeft, r.bottomRight))
            clipPath(forma) {
                // Las líneas de la verjura, muy tenues, y una mancha de los años en un borde.
                val lin = (if (o) Color.White else SEPIA).copy(alpha = if (o) 0.035f else 0.05f)
                var y = hoja.top + 2f * un
                while (y < hoja.bottom) { drawLine(lin, Offset(hoja.left, y), Offset(hoja.right, y), 0.3f * un); y += 1.6f * un }
                mancha(Offset(hoja.right - hoja.width * 0.1f, hoja.top + hoja.height * (0.2f + 0.6f * azar(semilla, 5))), hoja.width * 0.25f, (if (o) Color.Black else Color(0xFFC9A468)).copy(alpha = if (o) 0.2f else 0.22f))
                // Un trazo de construcción de sanguina en la esquina de arriba a la derecha (fuera del texto).
                val san = (if (o) TERRA_LUZ else SANGUINA).copy(alpha = if (o) 0.45f else 0.5f)
                drawArc(san, 90f, 90f, false, Offset(hoja.right - hoja.width * 0.12f, hoja.top - hoja.height * 0.1f), Size(hoja.width * 0.24f, hoja.height * 0.24f), style = Stroke(0.5f * un))
                drawLine(san, Offset(hoja.right - hoja.width * 0.12f, hoja.top + 1f * un), Offset(hoja.right - 1f * un, hoja.top + hoja.height * 0.12f), 0.4f * un)
            }
            drawPath(forma, if (o) Color(0xFF1E160E) else CARTA_OSC, style = Stroke(0.6f * un, join = StrokeJoin.Round))

            // El dibujo del tipo, abajo a la izquierda (fuera del texto).
            val q = Offset(r.left + r.width * 0.13f, r.bottom - r.height * 0.085f)
            val t = r.height * 0.045f
            val sep = if (o) Color(0xFFE6CFA5) else SEPIA
            val sang = if (o) Color(0xFFE79A72) else SANGUINA
            when (e.tipo) {
                "idea" -> { // una rueda dentada de sanguina
                    for (k in 0 until 10) {
                        val an = 6.2832f * k / 10
                        drawLine(sang, Offset(q.x + t * 0.75f * cos(an), q.y + t * 0.75f * sin(an)), Offset(q.x + t * 1.1f * cos(an), q.y + t * 1.1f * sin(an)), 0.8f * un)
                    }
                    drawCircle(sang, t * 0.8f, q, style = Stroke(0.6f * un))
                    drawCircle(sang, t * 0.25f, q, style = Stroke(0.5f * un))
                }
                "pensamiento" -> { // la espiral áurea en sepia, con sus cuadrados
                    val path = Path()
                    for (k in 0..40) {
                        val an = k / 40f * 2.6f * 6.2832f
                        val rr = t * 1.1f * Math.pow(0.5, (k / 40.0) * 2.6).toFloat()
                        val x = q.x + rr * cos(an); val y = q.y + rr * sin(an)
                        if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, sep, style = Stroke(0.6f * un))
                    drawRect(sep.copy(alpha = 0.5f), Offset(q.x - t * 1.1f, q.y - t * 1.1f), Size(t * 2.2f, t * 2.2f), style = Stroke(0.35f * un))
                }
                "proposito" -> { // un compás abierto
                    val eje = Offset(q.x, q.y - t * 1.1f)
                    drawLine(sep, eje, Offset(q.x - t * 0.8f, q.y + t * 1.1f), 0.7f * un, StrokeCap.Round)
                    drawLine(sep, eje, Offset(q.x + t * 0.8f, q.y + t * 1.1f), 0.7f * un, StrokeCap.Round)
                    drawCircle(sep, t * 0.22f, eje)
                    drawArc(sang, 50f, 80f, false, Offset(eje.x - t * 1.9f, eje.y - t * 1.9f), Size(t * 3.8f, t * 3.8f), style = Stroke(0.45f * un))
                }
                else -> { // el lazo de la cinta, en el acento
                    val c = if (o) mez(e.acento, Color.White, 0.2f) else e.acento
                    drawCircle(c, t * 0.55f, Offset(q.x - t * 0.5f, q.y), style = Stroke(0.8f * un))
                    drawCircle(c, t * 0.55f, Offset(q.x + t * 0.5f, q.y), style = Stroke(0.8f * un))
                    drawLine(c, q, Offset(q.x - t * 0.4f, q.y + t * 1.2f), 0.8f * un)
                    drawLine(c, q, Offset(q.x + t * 0.5f, q.y + t * 1.1f), 0.8f * un)
                }
            }

            // Arriba: la tachuela de latón; fijada, un sello de lacre con la flor de lis.
            val cl = Offset(r.center.x, r.top + r.height * 0.065f)
            if (e.fijada) sello(cl, 4f * un, if (o) Color(0xFFC8372C) else LACRE, semilla)
            else tachuela(cl, 2.2f * un, o)
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF2E4C4) else Color(0xFF2E1D0E)
}
