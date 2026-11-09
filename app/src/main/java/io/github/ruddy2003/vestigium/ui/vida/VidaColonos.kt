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
 * Colonos: los muebles de Vida y de Diario de los vecinos que reconstruyen su pueblo, de la misma casa que su estante de
 * cajas de embalaje, su cajonera del almacén general y su almacén de logros. Pino rescatado de tonos distintos con su veta,
 * sus nudos, sus rendijas y sus clavos; alguna tabla pintada de azul marino o de ladrillo; parches de lata, papel de gaceta,
 * cartulina manila, cinta amarillenta, cordel, latón y la luz del quinqué. En oscuro, la madera de noche y los quinqués
 * encendidos.
 * - Social: la cena del pueblo vista desde arriba: el piso de tablones, el tapete trenzado de trapo, la mesa larga de tablas
 *   con sus cabeceras clavadas y el mantel (el grafo va en el mantel); los bancos a lo largo, las sillas en las puntas, el
 *   quinqué encendido, la cafetera de peltre, el pan sobre su tabla, el pastel de enrejado y las tazas de peltre esmaltado.
 * - Mapas: la mesa del agrimensor vista desde arriba: el tablero de tablas, el pliego de papel de estraza con sus tachuelas
 *   de latón (el mapa va en el pliego), la brújula de latón con su rosa y sus pínulas, el mapa enrollado y atado, la plomada
 *   con su cordel, el lápiz de carpintero, la taza de café y su cerco, y el metro plegable de madera; al frente, el faldón con
 *   su cajón y su tirador.
 * - Otras vidas: el almacén de cajas de fruta: cinco cajas puestas de boca, forradas de gaceta (una medalla en cada una),
 *   con sus tablas, sus clavos y una marca de embarque estarcida; encima, el letrero pintado del almacén; a los lados, los
 *   postes con el quinqué y el rollo de cuerda; abajo, la tabla con una tarjeta de envío colgada bajo cada caja y la tarima.
 * - Íconos: el ayuntamiento de tablas con su campanario y el andamio de la reconstrucción; la placa de lata del mapa que la
 *   moneda raspa; tus lugares como estacas de agrimensor con banderín, unidas por el cordel.
 * - Notas: el tablón comunitario: el tejadillo de tejamanil con su caballete, su alero y sus ménsulas, el marco de tablas clavadas, el
 *   fondo de tablas encaladas (las notas van en él) y la repisa con el martillo y la lata de tachuelas. Las notas son
 *   papeles del pueblo con cinta o con tachuela de latón (si están fijadas): el recorte de gaceta cortado a tijera, la carta
 *   doblada, la tarjeta de envío con su ojal y el boleto con su talón picado.
 */
internal object VidaColonos : DisenoVida {
    private val PINO = Color(0xFFC49C66)
    private val PINO_LUZ = Color(0xFFDAB884)
    private val PINO_OSC = Color(0xFF8C6B41)
    private val TABLON = Color(0xFF7A5B3A)
    private val RENDIJA = Color(0xFF2E2418)
    private val VETA = Color(0xFF6E5030)
    private val CLAVO = Color(0xFF3A3530)
    private val MARINO = Color(0xFF2A3550)
    private val LADRILLO = Color(0xFF9E4A35)
    private val GACETA = Color(0xFFE8DFC6)
    private val MANILA = Color(0xFFD9BE86)
    private val CORDEL = Color(0xFFBFA272)
    private val CINTA = Color(0xFFE8D48A)
    private val LATON = Color(0xFFB89140)
    private val LATA = Color(0xFFA0A4A0)
    private val PELTRE = Color(0xFF4F6A8C)
    private val VIDRIO = Color(0xFFDCE6E8)
    private val LLAMA = Color(0xFFFFC24A)
    private val CAFE = Color(0xFF6A4020)
    private val CAL = Color(0xFFEDE5D2)
    private val TINTA = Color(0xFF1F2A3A)
    private val NOCHE = Color(0xFF100C08)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.12f, 0.2f, 0.88f, 0.8f)
        MuebleVida.MAPAS -> Rect(0.185f, 0.095f, 0.815f, 0.785f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.065f, 0.275f, 0.935f, 0.785f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.175f, 0.925f, 0.885f)
    }

    // ---------------------------------------------------------------- ayudas

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material con la luz del momento: tal cual en claro; en oscuro, la madera y el papel de noche. */
    private fun c(x: Color, oscuro: Boolean, f: Float = 0.58f): Color = if (oscuro) mezcla(x, NOCHE, f) else x

    /** El fondo sólido de un nicho: el material en claro y, en oscuro, apenas separado de la superficie de la tarjeta. */
    private fun nicho(x: Color, e: EstadoMueble, oscuro: Boolean): Color = if (oscuro) mezcla(e.fondo, x, 0.09f) else mezcla(e.fondo, x, 0.55f)

    private fun Rect.mas(d: Float) = Rect(left - d, top - d, right + d, bottom + d)

    private fun DrawScope.caja(r: Rect, color: Color) = drawRect(color, r.topLeft, r.size)

    /** Un número estable de 0 a 1 para cada cosa. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** La veta de la madera en [r]: líneas onduladas a lo largo, más o menos marcadas. [vertical] si la tabla va de arriba abajo. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, paso: Float, vertical: Boolean = false, grosor: Float = 1f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (paso <= 0.5f || largo <= 1f) return
        val n = (ancho / paso).toInt().coerceIn(2, 40)
        for (k in 0 until n) {
            val base = ancho * (k + azar(semilla, k)) / n
            val amp = paso * (0.15f + 0.4f * azar(semilla, k + 100))
            val fase = azar(semilla, k + 200) * 6.28f
            val ondas = 1.2f + 2f * azar(semilla, k + 300)
            val p = Path()
            val pasos = 14
            for (s in 0..pasos) {
                val t = largo * s / pasos
                val v = base + amp * sin(fase + ondas * 6.28f * s / pasos)
                val x = if (vertical) r.left + v else r.left + t
                val y = if (vertical) r.top + t else r.top + v
                if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            drawPath(p, color.copy(alpha = color.alpha * (0.35f + 0.65f * azar(semilla, k + 400))), style = Stroke(grosor * (0.5f + azar(semilla, k + 500))))
        }
    }

    /** Un nudo de la madera: el ojo oscuro con sus anillos. */
    private fun DrawScope.nudo(o: Offset, r: Float, color: Color) {
        drawOval(color.copy(alpha = color.alpha * 0.5f), Offset(o.x - r * 1.8f, o.y - r), Size(r * 3.6f, r * 2f), style = Stroke(r * 0.25f))
        drawOval(color, Offset(o.x - r, o.y - r * 0.55f), Size(r * 2f, r * 1.1f))
    }

    /**
     * Una tabla de pino: su tono, la veta, a veces un nudo, la luz en el canto de arriba y la rendija oscura en el de abajo.
     * [u] es la unidad del lienzo.
     */
    private fun DrawScope.tabla(r: Rect, tono: Color, semilla: Int, u: Float, oscuro: Boolean, vertical: Boolean = false, nudos: Boolean = true) {
        caja(r, tono)
        vetas(r, c(VETA, oscuro, 0.4f).copy(alpha = 0.45f), semilla, 1.4f * u, vertical, 0.25f * u)
        if (nudos && azar(semilla, 9) < 0.4f) {
            val o = if (vertical) Offset(r.center.x, r.top + r.height * (0.2f + 0.6f * azar(semilla, 10))) else Offset(r.left + r.width * (0.15f + 0.7f * azar(semilla, 10)), r.center.y)
            nudo(o, minOf(r.width, r.height) * 0.14f, c(VETA, oscuro, 0.3f).copy(alpha = 0.7f))
        }
        val l = minOf(0.45f * u, minOf(r.width, r.height) * 0.12f)
        if (vertical) {
            caja(Rect(r.left, r.top, r.left + l, r.bottom), Color.White.copy(alpha = if (oscuro) 0.06f else 0.18f))
            caja(Rect(r.right - l, r.top, r.right, r.bottom), c(RENDIJA, oscuro, 0.2f).copy(alpha = 0.7f))
        } else {
            caja(Rect(r.left, r.top, r.right, r.top + l), Color.White.copy(alpha = if (oscuro) 0.06f else 0.18f))
            caja(Rect(r.left, r.bottom - l, r.right, r.bottom), c(RENDIJA, oscuro, 0.2f).copy(alpha = 0.7f))
        }
    }

    /** El tono de cada tabla rescatada: pino de distinta edad, y de vez en cuando una pintada de marino o de ladrillo. */
    private fun tono(k: Int, semilla: Int, oscuro: Boolean, pintadas: Boolean = true): Color {
        val f = azar(semilla, k)
        val base = when {
            pintadas && k % 7 == 3 -> mezcla(MARINO, PINO, 0.35f)
            pintadas && k % 9 == 6 -> mezcla(LADRILLO, PINO, 0.3f)
            else -> mezcla(PINO_OSC, PINO_LUZ, 0.25f + 0.65f * f)
        }
        return c(base, oscuro)
    }

    /** Un clavo de cabeza redonda. */
    private fun DrawScope.clavo(o: Offset, r: Float, oscuro: Boolean) {
        drawCircle(c(CLAVO, oscuro, 0.2f), r, o)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.12f else 0.3f), r * 0.45f, Offset(o.x - r * 0.3f, o.y - r * 0.3f))
    }

    /** Una tachuela de latón con su brillo y su sombra. */
    private fun DrawScope.tachuela(o: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(o.x + r * 0.3f, o.y + r * 0.4f))
        drawCircle(c(mezcla(LATON, Color.Black, 0.2f), oscuro, 0.25f), r, o)
        drawCircle(c(LATON, oscuro, 0.2f), r * 0.78f, Offset(o.x - r * 0.08f, o.y - r * 0.08f))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.35f else 0.6f), r * 0.28f, Offset(o.x - r * 0.32f, o.y - r * 0.32f))
    }

    /** Un parche de lata clavado en sus cuatro esquinas. */
    private fun DrawScope.parche(r: Rect, u: Float, oscuro: Boolean) {
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 0.4f * u, r.top + 0.5f * u), r.size)
        caja(r, c(LATA, oscuro))
        caja(Rect(r.left, r.top, r.right, r.top + 0.4f * u), Color.White.copy(alpha = 0.35f))
        drawLine(Color.White.copy(alpha = 0.2f), Offset(r.left + r.width * 0.2f, r.bottom), Offset(r.left + r.width * 0.7f, r.top), 0.3f * u)
        val d = minOf(r.width, r.height) * 0.18f
        listOf(Offset(r.left + d, r.top + d), Offset(r.right - d, r.top + d), Offset(r.left + d, r.bottom - d), Offset(r.right - d, r.bottom - d)).forEach { clavo(it, 0.35f * u, oscuro) }
    }

    /** Una marca de embarque estarcida (formas, no letras): rombo con punto, círculo con barra, flechas, estrella o cruz en círculo. */
    private fun DrawScope.marca(k: Int, o: Offset, s: Float, color: Color) {
        val g = s * 0.2f
        when (k % 5) {
            0 -> {
                drawPath(Path().apply { moveTo(o.x, o.y - s); lineTo(o.x + s, o.y); lineTo(o.x, o.y + s); lineTo(o.x - s, o.y); close() }, color, style = Stroke(g))
                drawCircle(color, s * 0.2f, o)
            }
            1 -> {
                drawCircle(color, s * 0.8f, o, style = Stroke(g))
                drawLine(color, Offset(o.x - s, o.y), Offset(o.x + s, o.y), g)
            }
            2 -> for (j in -1..1 step 2) {
                val x = o.x + j * s * 0.45f
                drawLine(color, Offset(x, o.y + s), Offset(x, o.y - s * 0.4f), g)
                drawPath(Path().apply { moveTo(x - s * 0.35f, o.y - s * 0.3f); lineTo(x, o.y - s); lineTo(x + s * 0.35f, o.y - s * 0.3f); close() }, color)
            }
            3 -> drawPath(estrella(o, s, 0.42f), color)
            else -> {
                drawCircle(color, s * 0.8f, o, style = Stroke(g))
                drawLine(color, Offset(o.x, o.y - s * 0.8f), Offset(o.x, o.y + s * 0.8f), g * 0.9f)
                drawLine(color, Offset(o.x - s * 0.8f, o.y), Offset(o.x + s * 0.8f, o.y), g * 0.9f)
            }
        }
    }

    /** Una estrella de cinco puntas de radio [r]. */
    private fun estrella(c0: Offset, r: Float, hondo: Float): Path = Path().apply {
        for (k in 0 until 10) {
            val a = Math.toRadians(-90.0 + k * 36.0)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c0.x + rr * cos(a).toFloat(); val y = c0.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Una mancha de luz que se desvanece (el halo del quinqué). */
    private fun DrawScope.halo(o: Offset, r: Float, color: Color) {
        if (r < 1f) return
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), o, r), r, o)
    }

    /** Un contorno cortado a tijera dentro de [r]: el borde con pequeñas entradas irregulares. */
    private fun tijera(r: Rect, semilla: Int, hondo: Float): Path = Path().apply {
        val n = 28
        for (k in 0 until n) {
            val t = k / n.toFloat()
            val (x, y) = when {
                t < 0.25f -> r.left + r.width * (t / 0.25f) to r.top
                t < 0.5f -> r.right to r.top + r.height * ((t - 0.25f) / 0.25f)
                t < 0.75f -> r.right - r.width * ((t - 0.5f) / 0.25f) to r.bottom
                else -> r.left to r.bottom - r.height * ((t - 0.75f) / 0.25f)
            }
            val d = hondo * azar(semilla, k)
            val px = x + (if (x <= r.left + 0.5f) d else if (x >= r.right - 0.5f) -d else 0f)
            val py = y + (if (y <= r.top + 0.5f) d else if (y >= r.bottom - 0.5f) -d else 0f)
            if (k == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }

    // ---------------------------------------------------------------- Social

    /** Una taza de peltre esmaltado vista desde arriba: el borde azul con motas, el café y el asa. */
    private fun DrawScope.taza(o: Offset, r: Float, asa: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(o.x + r * 0.15f, o.y + r * 0.2f))
        val ang = Math.toRadians(asa.toDouble())
        val ax = o.x + r * 1.15f * cos(ang).toFloat(); val ay = o.y + r * 1.15f * sin(ang).toFloat()
        drawLine(c(PELTRE, oscuro, 0.35f), o, Offset(ax, ay), r * 0.45f, StrokeCap.Round)
        drawCircle(c(PELTRE, oscuro, 0.35f), r, o)
        listOf(0.3f to 1.1f, 0.7f to 3.4f, 0.55f to 5.2f).forEach { (d, a) -> drawCircle(c(Color.White, oscuro, 0.4f).copy(alpha = 0.7f), r * 0.07f, Offset(o.x + r * d * cos(a), o.y + r * d * sin(a))) }
        drawCircle(c(CAFE, oscuro, 0.3f), r * 0.72f, o)
        drawCircle(Color.White.copy(alpha = 0.25f), r * 0.2f, Offset(o.x - r * 0.25f, o.y - r * 0.25f))
        drawCircle(c(MARINO, oscuro, 0.3f), r, o, style = Stroke(r * 0.12f))
    }

    /** El quinqué visto desde arriba: el pie de latón, el depósito, el tubo de vidrio y la llama (su luz va aparte). */
    private fun DrawScope.quinqueArriba(o: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(o.x + r * 0.15f, o.y + r * 0.2f))
        drawCircle(c(LATON, oscuro, 0.3f), r, o)
        drawCircle(c(mezcla(LATON, Color.Black, 0.25f), oscuro, 0.3f), r, o, style = Stroke(r * 0.1f))
        drawCircle(c(VIDRIO, oscuro, 0.25f), r * 0.6f, o)
        drawCircle(LLAMA, r * 0.28f, o)
        drawCircle(Color.White, r * 0.12f, o)
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = h / 100f
        if (u <= 0f) return
        val a = area(MuebleVida.SOCIAL).let { Rect(w * it.left, h * it.top, w * it.right, h * it.bottom) }
        // El piso de tablones, con sus juntas escalonadas y sus clavos.
        val alto = 10f * u
        var y = 0f; var k = 0
        while (y < h) {
            val desfase = w * (0.2f + 0.5f * azar(e.semilla, k + 50))
            listOf(0f to desfase, desfase to w).forEachIndexed { m, (x0, x1) ->
                tabla(Rect(x0, y, x1, minOf(y + alto, h)), mezcla(tono(k * 2 + m, e.semilla, oscuro, false), c(TABLON, oscuro), 0.45f), k * 5 + m, u, oscuro)
            }
            caja(Rect(desfase - 0.25f * u, y, desfase + 0.25f * u, y + alto), c(RENDIJA, oscuro, 0.2f))
            clavo(Offset(desfase + 1.4f * u, y + alto * 0.3f), 0.4f * u, oscuro); clavo(Offset(desfase + 1.4f * u, y + alto * 0.7f), 0.4f * u, oscuro)
            y += alto; k++
        }
        // El tapete trenzado de trapo: anillos de colores, con la trenza marcada en el de fuera.
        val tc = Offset(w / 2, h / 2)
        val anillos = listOf(LADRILLO, GACETA, MARINO, MANILA, LADRILLO, PINO_OSC, GACETA)
        val rx0 = w * 0.5f; val ry0 = h * 0.5f
        anillos.forEachIndexed { i, col ->
            val f = 1f - i * 0.035f
            drawOval(c(col, oscuro, 0.5f), Offset(tc.x - rx0 * f, tc.y - ry0 * f * 1.02f), Size(rx0 * 2 * f, ry0 * 2 * f * 1.02f))
        }
        val trenza = 46
        for (i in 0 until trenza) {
            val ang = i * 6.283f / trenza
            val p = Offset(tc.x + rx0 * 0.985f * cos(ang), tc.y + ry0 * 1.0f * sin(ang))
            drawLine(c(Color.Black, oscuro, 0f).copy(alpha = 0.25f), Offset(p.x - 0.8f * u, p.y - 0.8f * u), Offset(p.x + 0.8f * u, p.y + 0.8f * u), 0.35f * u)
        }
        // Los bancos de tablas a lo largo de la mesa, que asoman arriba y abajo, con su sombra.
        listOf(Rect(w * 0.2f, -h * 0.02f, w * 0.8f, h * 0.045f), Rect(w * 0.2f, h * 0.955f, w * 0.8f, h * 1.02f)).forEachIndexed { i, b ->
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(b.left + 0.8f * u, b.top + 1f * u), b.size)
            tabla(b, tono(20 + i * 2, e.semilla, oscuro, false), 40 + i, u, oscuro, nudos = false)
            listOf(b.left + 3f * u, b.right - 3f * u).forEach { x -> clavo(Offset(x, b.center.y), 0.4f * u, oscuro) }
        }
        // Las sillas de las puntas: el asiento de tablas y el respaldo de escalera, hacia afuera.
        listOf(-1f, 1f).forEach { lado ->
            val s = minOf(10f * u, w * 0.06f)
            val cx = if (lado < 0) s * 0.3f else w - s * 0.3f
            val asiento = Rect(cx - s * 0.5f, h * 0.5f - s * 0.62f, cx + s * 0.5f, h * 0.5f + s * 0.62f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(asiento.left + 0.8f * u, asiento.top + 1f * u), asiento.size)
            tabla(asiento, tono(30 + lado.toInt(), e.semilla, oscuro, false), 60 + lado.toInt(), u, oscuro, vertical = true, nudos = false)
        }
        // La mesa larga: las tablas del tablero, las cabeceras clavadas a lo ancho y su sombra en el tapete.
        val mesa = Rect(w * 0.045f, h * 0.055f, w * 0.955f, h * 0.945f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.3f), Offset(mesa.left + 1.2f * u, mesa.top + 1.6f * u), mesa.size)
        val cab = minOf(4.4f * u, w * 0.03f)
        val nt = 5
        for (i in 0 until nt) {
            val r = Rect(mesa.left + cab, mesa.top + mesa.height * i / nt, mesa.right - cab, mesa.top + mesa.height * (i + 1) / nt)
            tabla(r, tono(i, e.semilla + 3, oscuro, false), e.semilla * 3 + i, u, oscuro)
        }
        listOf(Rect(mesa.left, mesa.top, mesa.left + cab, mesa.bottom), Rect(mesa.right - cab, mesa.top, mesa.right, mesa.bottom)).forEachIndexed { i, r ->
            tabla(r, c(mezcla(PINO, PINO_OSC, 0.4f), oscuro), 80 + i, u, oscuro, vertical = true, nudos = false)
            for (j in 0 until nt) clavo(Offset(r.center.x, mesa.top + mesa.height * (j + 0.5f) / nt), 0.45f * u, oscuro)
        }
        // La luz del quinqué sobre la madera (antes del mantel, para no tocar el nicho).
        val ya0 = (mesa.top + a.top - 1.8f * u) / 2
        halo(Offset(w * 0.17f, ya0), 14f * u, LLAMA.copy(alpha = if (oscuro) 0.4f else 0.25f))
        // El mantel de lino (el nicho del grafo), con su dobladillo de dos franjas, ladrillo y marino.
        val mantel = a.mas(1.8f * u)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(mantel.left + 0.4f * u, mantel.top + 0.6f * u), mantel.size)
        caja(mantel, c(GACETA, oscuro, 0.45f))
        drawRect(c(LADRILLO, oscuro, 0.3f), Offset(mantel.left + 0.4f * u, mantel.top + 0.4f * u), Size(mantel.width - 0.8f * u, mantel.height - 0.8f * u), style = Stroke(0.55f * u))
        drawRect(c(MARINO, oscuro, 0.3f), Offset(mantel.left + 1.2f * u, mantel.top + 1.2f * u), Size(mantel.width - 2.4f * u, mantel.height - 2.4f * u), style = Stroke(0.3f * u))
        caja(a, nicho(GACETA, e, oscuro))
        // La cena en los dos bordes de la mesa, fuera del mantel.
        val ya = (mesa.top + mantel.top) / 2; val yb = (mesa.bottom + mantel.bottom) / 2
        val g = minOf(1f, (mantel.top - mesa.top) / (12f * u))
        quinqueArriba(Offset(w * 0.17f, ya), 3.6f * u * g, oscuro)
        taza(Offset(w * 0.31f, ya), 3f * u * g, -40f, oscuro)
        panSobreTabla(Offset(w * 0.48f, ya), 22f * u * g, 9f * u * g, u, oscuro)
        taza(Offset(w * 0.65f, ya), 3f * u * g, -140f, oscuro)
        cafetera(Offset(w * 0.81f, ya), 4.4f * u * g, oscuro)
        taza(Offset(w * 0.18f, yb), 3f * u * g, 140f, oscuro)
        plato(Offset(w * 0.33f, yb), 4.6f * u * g, oscuro)
        pastel(Offset(w * 0.5f, yb), 5f * u * g, oscuro)
        plato(Offset(w * 0.67f, yb), 4.6f * u * g, oscuro)
        taza(Offset(w * 0.82f, yb), 3f * u * g, 40f, oscuro)
    }

    /** La cafetera de peltre vista desde arriba: el cuerpo, la tapa con su perilla, el pico y el asa. */
    private fun DrawScope.cafetera(cc: Offset, cr: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), cr, Offset(cc.x + cr * 0.15f, cc.y + cr * 0.2f))
        drawLine(c(PELTRE, oscuro, 0.35f), Offset(cc.x + cr * 0.5f, cc.y), Offset(cc.x + cr * 1.5f, cc.y), cr * 0.28f, StrokeCap.Round)
        drawPath(Path().apply { moveTo(cc.x - cr * 0.8f, cc.y - cr * 0.3f); lineTo(cc.x - cr * 1.6f, cc.y - cr * 0.1f); lineTo(cc.x - cr * 1.6f, cc.y + cr * 0.1f); lineTo(cc.x - cr * 0.8f, cc.y + cr * 0.3f); close() }, c(mezcla(LATA, Color.Black, 0.2f), oscuro, 0.4f))
        drawCircle(c(LATA, oscuro, 0.4f), cr, cc)
        drawCircle(c(mezcla(LATA, Color.Black, 0.25f), oscuro, 0.4f), cr * 0.66f, cc)
        drawCircle(c(LATA, oscuro, 0.4f), cr * 0.54f, cc)
        drawCircle(c(CLAVO, oscuro, 0.2f), cr * 0.16f, cc)
        drawCircle(Color.White.copy(alpha = 0.35f), cr * 0.25f, Offset(cc.x - cr * 0.5f, cc.y - cr * 0.45f))
    }

    /** El pan en su tabla de cortar, con sus tajos. */
    private fun DrawScope.panSobreTabla(o: Offset, ancho: Float, alto: Float, u: Float, oscuro: Boolean) {
        val tb = Rect(o.x - ancho / 2, o.y - alto / 2, o.x + ancho / 2, o.y + alto / 2)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tb.left + 0.6f * u, tb.top + 0.8f * u), tb.size)
        tabla(tb, c(PINO_LUZ, oscuro), 91, u, oscuro, nudos = false)
        drawCircle(c(PINO_OSC, oscuro), alto * 0.12f, Offset(tb.right - alto * 0.3f, o.y))
        val pan = Rect(tb.left + ancho * 0.08f, tb.top + alto * 0.12f, tb.right - ancho * 0.28f, tb.bottom - alto * 0.12f)
        drawOval(c(Color(0xFFB0702E), oscuro, 0.35f), pan.topLeft, pan.size)
        drawOval(c(Color(0xFFD9A15A), oscuro, 0.35f), Offset(pan.left + pan.width * 0.06f, pan.top + pan.height * 0.12f), Size(pan.width * 0.82f, pan.height * 0.6f))
        for (j in 1..4) {
            val x = pan.left + pan.width * j / 5f
            drawLine(c(Color(0xFF8A5220), oscuro, 0.35f), Offset(x - pan.width * 0.04f, pan.top + pan.height * 0.22f), Offset(x + pan.width * 0.04f, pan.bottom - pan.height * 0.22f), alto * 0.06f, StrokeCap.Round)
        }
    }

    /** El pastel de enrejado en su molde de lata. */
    private fun DrawScope.pastel(pc: Offset, pr: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), pr, Offset(pc.x + pr * 0.12f, pc.y + pr * 0.16f))
        drawCircle(c(LATA, oscuro, 0.4f), pr, pc)
        drawCircle(c(Color(0xFFD9A15A), oscuro, 0.35f), pr * 0.86f, pc)
        drawCircle(c(Color(0xFF8E2A3A), oscuro, 0.35f), pr * 0.68f, pc)
        clipPath(Path().apply { addOval(Rect(pc.x - pr * 0.72f, pc.y - pr * 0.72f, pc.x + pr * 0.72f, pc.y + pr * 0.72f)) }) {
            for (j in -2..2) {
                drawLine(c(Color(0xFFE2B26E), oscuro, 0.35f), Offset(pc.x + j * pr * 0.3f, pc.y - pr), Offset(pc.x + j * pr * 0.3f, pc.y + pr), pr * 0.13f)
                drawLine(c(Color(0xFFE2B26E), oscuro, 0.35f), Offset(pc.x - pr, pc.y + j * pr * 0.3f), Offset(pc.x + pr, pc.y + j * pr * 0.3f), pr * 0.13f)
            }
        }
        val rnd = Random(5)
        repeat(10) { val a = it * 0.628f; drawCircle(c(Color(0xFFC08A48), oscuro, 0.35f), pr * 0.06f, Offset(pc.x + pr * 0.78f * cos(a), pc.y + pr * 0.78f * sin(a) + rnd.nextFloat() * 0.01f)) }
    }

    /** Un plato de lata con su galleta y su cuchara. */
    private fun DrawScope.plato(o: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(o.x + r * 0.12f, o.y + r * 0.16f))
        drawCircle(c(LATA, oscuro, 0.4f), r, o)
        drawCircle(c(mezcla(LATA, Color.White, 0.25f), oscuro, 0.4f), r * 0.72f, o)
        drawCircle(c(mezcla(LATA, Color.Black, 0.2f), oscuro, 0.4f), r * 0.72f, o, style = Stroke(r * 0.05f))
        drawCircle(c(Color(0xFFD9A15A), oscuro, 0.35f), r * 0.34f, Offset(o.x - r * 0.12f, o.y + r * 0.08f))
        drawCircle(c(Color(0xFFB0702E), oscuro, 0.35f), r * 0.34f, Offset(o.x - r * 0.12f, o.y + r * 0.08f), style = Stroke(r * 0.06f))
        drawLine(c(LATA, oscuro, 0.3f), Offset(o.x + r * 1.05f, o.y - r * 0.5f), Offset(o.x + r * 1.15f, o.y + r * 0.8f), r * 0.12f, StrokeCap.Round)
        drawOval(c(LATA, oscuro, 0.3f), Offset(o.x + r * 0.95f, o.y - r * 0.85f), Size(r * 0.3f, r * 0.45f))
    }


    // ---------------------------------------------------------------- Mapas

    /** La brújula de agrimensor de latón: la caja redonda, la rosa, la aguja y las dos pínulas. */
    private fun DrawScope.brujula(o: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r * 1.05f, Offset(o.x + r * 0.1f, o.y + r * 0.14f))
        listOf(-1f, 1f).forEach { s ->
            val p = Offset(o.x, o.y + s * r * 1.25f)
            drawRoundRect(c(LATON, oscuro, 0.3f), Offset(p.x - r * 0.12f, p.y - r * 0.3f), Size(r * 0.24f, r * 0.6f), CornerRadius(r * 0.05f))
            drawRect(c(CLAVO, oscuro, 0.2f), Offset(p.x - r * 0.03f, p.y - r * 0.22f), Size(r * 0.06f, r * 0.44f))
        }
        drawCircle(c(mezcla(LATON, Color.Black, 0.25f), oscuro, 0.3f), r, o)
        drawCircle(c(LATON, oscuro, 0.3f), r * 0.9f, o)
        drawCircle(c(GACETA, oscuro, 0.3f), r * 0.74f, o)
        for (k in 0 until 16) {
            val a = k * Math.PI / 8
            val l = if (k % 4 == 0) 0.2f else 0.1f
            drawLine(c(TINTA, oscuro, 0.2f), Offset(o.x + r * 0.72f * cos(a).toFloat(), o.y + r * 0.72f * sin(a).toFloat()), Offset(o.x + r * (0.72f - l) * cos(a).toFloat(), o.y + r * (0.72f - l) * sin(a).toFloat()), r * 0.035f)
        }
        drawPath(estrella(o, r * 0.42f, 0.35f), c(MARINO, oscuro, 0.25f).copy(alpha = 0.5f))
        rotate(20f, o) {
            drawPath(Path().apply { moveTo(o.x, o.y - r * 0.62f); lineTo(o.x + r * 0.08f, o.y); lineTo(o.x - r * 0.08f, o.y); close() }, c(LADRILLO, oscuro, 0.2f))
            drawPath(Path().apply { moveTo(o.x, o.y + r * 0.62f); lineTo(o.x + r * 0.08f, o.y); lineTo(o.x - r * 0.08f, o.y); close() }, c(MARINO, oscuro, 0.2f))
        }
        drawCircle(c(LATON, oscuro, 0.2f), r * 0.07f, o)
        drawArc(Color.White.copy(alpha = if (oscuro) 0.2f else 0.55f), 200f, 70f, false, Offset(o.x - r * 0.66f, o.y - r * 0.66f), Size(r * 1.32f, r * 1.32f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = h / 100f
        if (u <= 0f) return
        val a = area(MuebleVida.MAPAS).let { Rect(w * it.left, h * it.top, w * it.right, h * it.bottom) }
        val canto = h * 0.875f
        // El tablero de tablas visto desde arriba, con su veta, sus nudos y los clavos de los travesaños.
        val nt = 6
        for (i in 0 until nt) {
            val r = Rect(0f, canto * i / nt, w, canto * (i + 1) / nt)
            tabla(r, tono(i, e.semilla, oscuro, false), e.semilla * 7 + i, u, oscuro)
            listOf(w * 0.035f, w * 0.965f).forEach { x -> clavo(Offset(x, r.center.y), 0.45f * u, oscuro) }
        }
        // El faldón de enfrente: la moldura, el cajón con su tirador de latón y las patas.
        caja(Rect(0f, canto, w, canto + 2.2f * u), c(PINO_LUZ, oscuro))
        caja(Rect(0f, canto + 1.6f * u, w, canto + 2.2f * u), c(PINO_OSC, oscuro))
        tabla(Rect(0f, canto + 2.2f * u, w, h), c(PINO, oscuro), 95, u, oscuro, nudos = false)
        listOf(Rect(0f, canto + 2.2f * u, w * 0.05f, h), Rect(w * 0.95f, canto + 2.2f * u, w, h)).forEach { r -> caja(r, c(PINO_OSC, oscuro)); caja(Rect(r.left, r.top, r.left + 0.5f * u, r.bottom), Color.White.copy(alpha = 0.12f)) }
        val cajon = Rect(w * 0.36f, canto + 3.4f * u, w * 0.64f, h - 1.2f * u)
        drawRect(c(RENDIJA, oscuro, 0.2f), Offset(cajon.left - 0.4f * u, cajon.top - 0.4f * u), Size(cajon.width + 0.8f * u, cajon.height + 0.8f * u))
        tabla(cajon, c(PINO_LUZ, oscuro), 97, u, oscuro, nudos = false)
        drawRoundRect(c(LATON, oscuro, 0.3f), Offset(cajon.center.x - 3f * u, cajon.center.y - 0.7f * u), Size(6f * u, 1.4f * u), CornerRadius(0.7f * u))
        drawCircle(c(LATON, oscuro, 0.3f), 0.8f * u, Offset(cajon.center.x - 3f * u, cajon.center.y)); drawCircle(c(LATON, oscuro, 0.3f), 0.8f * u, Offset(cajon.center.x + 3f * u, cajon.center.y))
        // El cerco de la taza de café en la madera, a la izquierda.
        drawCircle(c(CAFE, oscuro, 0.3f).copy(alpha = 0.35f), 5.5f * u, Offset(a.left * 0.55f, h * 0.73f), style = Stroke(0.7f * u))
        // El pliego de papel de estraza (el nicho del mapa), con su sombra, su borde cortado y sus tachuelas.
        val pliego = a.mas(2.4f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.28f), Offset(pliego.left + 0.8f * u, pliego.top + 1.2f * u), pliego.size)
        drawPath(tijera(pliego, 7, 0.5f * u), c(GACETA, oscuro, 0.45f))
        caja(a, nicho(GACETA, e, oscuro))
        listOf(Offset(pliego.left + 1.3f * u, pliego.top + 1.3f * u), Offset(pliego.right - 1.3f * u, pliego.top + 1.3f * u), Offset(pliego.left + 1.3f * u, pliego.bottom - 1.3f * u), Offset(pliego.right - 1.3f * u, pliego.bottom - 1.3f * u)).forEach { tachuela(it, 1.1f * u, oscuro) }
        // El metro plegable de madera, abajo, con sus tramos, sus remaches de latón y sus marcas.
        val mr = Rect(a.left + a.width * 0.04f, a.bottom + 4f * u, a.left + a.width * 0.62f, a.bottom + 7.6f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(mr.left + 0.6f * u, mr.top + 0.8f * u), mr.size)
        val tramos = 4
        for (i in 0 until tramos) {
            val r = Rect(mr.left + mr.width * i / tramos, mr.top, mr.left + mr.width * (i + 1) / tramos, mr.bottom)
            caja(r, c(Color(0xFFE9C878), oscuro))
            var x = r.left + 1f * u; var k = 0
            while (x < r.right - 0.6f * u) { drawLine(c(TINTA, oscuro, 0.3f), Offset(x, r.top), Offset(x, r.top + r.height * (if (k % 5 == 0) 0.55f else 0.3f)), 0.22f * u); x += 1.1f * u; k++ }
            drawLine(c(PINO_OSC, oscuro), Offset(r.right, r.top), Offset(r.right, r.bottom), 0.3f * u)
            drawCircle(c(LATON, oscuro, 0.2f), 0.6f * u, Offset(r.right - 1f * u, r.center.y + 0.6f * u))
        }
        // A la izquierda: la brújula de latón y el mapa enrollado, atado con cordel.
        val izq = pliego.left
        brujula(Offset(izq * 0.5f, h * 0.24f), minOf(9f * u, izq * 0.36f), oscuro)
        val rollo = Rect(izq * 0.18f, h * 0.48f, izq * 0.82f, h * 0.48f + minOf(7f * u, izq * 0.24f))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(rollo.left + 0.6f * u, rollo.top + 1f * u), rollo.size, CornerRadius(rollo.height / 2))
        drawRoundRect(c(MANILA, oscuro, 0.4f), rollo.topLeft, rollo.size, CornerRadius(rollo.height / 2))
        caja(Rect(rollo.left + rollo.height / 2, rollo.top + rollo.height * 0.15f, rollo.right - rollo.height / 2, rollo.top + rollo.height * 0.32f), Color.White.copy(alpha = 0.3f))
        caja(Rect(rollo.left + rollo.height / 2, rollo.bottom - rollo.height * 0.25f, rollo.right - rollo.height / 2, rollo.bottom), c(PINO_OSC, oscuro).copy(alpha = 0.3f))
        drawOval(c(mezcla(MANILA, Color.Black, 0.2f), oscuro, 0.4f), Offset(rollo.right - rollo.height * 0.5f, rollo.top), Size(rollo.height * 0.5f, rollo.height))
        drawOval(c(mezcla(MANILA, Color.Black, 0.35f), oscuro, 0.4f), Offset(rollo.right - rollo.height * 0.38f, rollo.top + rollo.height * 0.25f), Size(rollo.height * 0.26f, rollo.height * 0.5f), style = Stroke(0.3f * u))
        listOf(0.32f, 0.62f).forEach { f -> caja(Rect(rollo.left + rollo.width * f, rollo.top - 0.2f * u, rollo.left + rollo.width * f + 1f * u, rollo.bottom + 0.2f * u), c(CORDEL, oscuro, 0.35f)) }
        // A la derecha: la plomada de latón con su cordel enrollado, el lápiz de carpintero y la taza de café.
        val der = pliego.right
        val dw = w - der
        val pl = Offset(der + dw * 0.55f, h * 0.2f)
        val ps = minOf(5f * u, dw * 0.2f)
        drawPath(Path().apply { moveTo(pl.x - dw * 0.3f, pl.y - ps * 1.5f); cubicTo(pl.x - dw * 0.4f, pl.y + ps, pl.x + dw * 0.1f, pl.y + ps * 2f, pl.x - ps * 0.2f, pl.y - ps * 0.3f) }, c(CORDEL, oscuro, 0.35f), style = Stroke(0.45f * u, cap = StrokeCap.Round))
        drawOval(c(CORDEL, oscuro, 0.35f), Offset(pl.x - dw * 0.42f, pl.y - ps * 2.1f), Size(ps * 1.4f, ps * 1.2f))
        drawOval(c(mezcla(CORDEL, Color.Black, 0.3f), oscuro, 0.35f), Offset(pl.x - dw * 0.42f + ps * 0.4f, pl.y - ps * 1.8f), Size(ps * 0.6f, ps * 0.6f))
        rotate(35f, pl) {
            drawPath(Path().apply { moveTo(pl.x - ps * 0.6f, pl.y - ps * 0.4f); lineTo(pl.x + ps * 0.6f, pl.y - ps * 0.4f); lineTo(pl.x, pl.y + ps * 1.3f); close() }, c(LATON, oscuro, 0.3f))
            drawRect(c(LATON, oscuro, 0.3f), Offset(pl.x - ps * 0.6f, pl.y - ps * 0.9f), Size(ps * 1.2f, ps * 0.5f))
            drawLine(Color.White.copy(alpha = 0.45f), Offset(pl.x - ps * 0.35f, pl.y - ps * 0.3f), Offset(pl.x - ps * 0.05f, pl.y + ps * 0.8f), ps * 0.14f, StrokeCap.Round)
        }
        val lz0 = Offset(der + dw * 0.2f, h * 0.44f); val lz1 = Offset(der + dw * 0.86f, h * 0.53f)
        drawLine(Color.Black.copy(alpha = 0.3f), lz0 + Offset(0.6f * u, 0.8f * u), lz1 + Offset(0.6f * u, 0.8f * u), 2.2f * u)
        drawLine(c(LADRILLO, oscuro, 0.3f), lz0, lz1, 2.2f * u)
        drawLine(Color.White.copy(alpha = 0.2f), lz0 + Offset(0f, -0.6f * u), lz1 + Offset(0f, -0.6f * u), 0.5f * u)
        val dir = Offset(lz1.x - lz0.x, lz1.y - lz0.y)
        val punta = Offset(lz0.x - dir.x * 0.12f, lz0.y - dir.y * 0.12f)
        drawLine(c(PINO_LUZ, oscuro), lz0, punta, 1.8f * u, StrokeCap.Round)
        drawLine(c(CLAVO, oscuro, 0.2f), Offset(lz0.x - dir.x * 0.08f, lz0.y - dir.y * 0.08f), punta, 0.7f * u, StrokeCap.Round)
        taza(Offset(der + dw * 0.5f, h * 0.7f), minOf(5f * u, dw * 0.24f), 20f, oscuro)
    }

    // ---------------------------------------------------------------- Otras vidas

    /** El quinqué colgado de su gancho: el depósito de latón, el tubo de vidrio con la llama y su luz. */
    private fun DrawScope.quinque(o: Offset, s: Float, oscuro: Boolean) {
        halo(Offset(o.x, o.y + s * 0.9f), s * (if (oscuro) 3.4f else 2.4f), LLAMA.copy(alpha = if (oscuro) 0.38f else 0.22f))
        drawLine(c(CLAVO, oscuro, 0.2f), o, Offset(o.x, o.y + s * 0.3f), s * 0.06f)
        drawArc(c(LATON, oscuro, 0.25f), 180f, 180f, false, Offset(o.x - s * 0.3f, o.y + s * 0.3f), Size(s * 0.6f, s * 0.5f), style = Stroke(s * 0.06f))
        drawRoundRect(c(LATON, oscuro, 0.25f), Offset(o.x - s * 0.18f, o.y + s * 0.45f), Size(s * 0.36f, s * 0.12f), CornerRadius(s * 0.04f))
        drawOval(c(VIDRIO, oscuro, 0.2f).copy(alpha = 0.95f), Offset(o.x - s * 0.24f, o.y + s * 0.55f), Size(s * 0.48f, s * 0.7f))
        drawOval(LLAMA, Offset(o.x - s * 0.07f, o.y + s * 0.78f), Size(s * 0.14f, s * 0.24f))
        drawOval(Color.White, Offset(o.x - s * 0.03f, o.y + s * 0.86f), Size(s * 0.06f, s * 0.1f))
        drawLine(Color.White.copy(alpha = 0.6f), Offset(o.x - s * 0.14f, o.y + s * 0.7f), Offset(o.x - s * 0.14f, o.y + s * 1.05f), s * 0.04f, StrokeCap.Round)
        drawRoundRect(c(LATON, oscuro, 0.25f), Offset(o.x - s * 0.3f, o.y + s * 1.2f), Size(s * 0.6f, s * 0.32f), CornerRadius(s * 0.1f))
        drawRect(Color.White.copy(alpha = 0.3f), Offset(o.x - s * 0.24f, o.y + s * 1.24f), Size(s * 0.12f, s * 0.24f))
    }

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = h / 100f
        if (u <= 0f) return
        val a = area(MuebleVida.OTRAS_VIDAS).let { Rect(w * it.left, h * it.top, w * it.right, h * it.bottom) }
        // La pared del almacén: tablas verticales con sus tapajuntas.
        val pt = 9f * u
        var x = 0f; var k = 0
        while (x < w) { tabla(Rect(x, 0f, minOf(x + pt, w), h), mezcla(tono(k, e.semilla, oscuro, false), c(TABLON, oscuro), 0.55f), 200 + k, u, oscuro, vertical = true); x += pt; k++ }
        // Los postes de los lados, de madera gruesa, con un parche de lata.
        val poste = a.left - 0.6f * u
        listOf(Rect(0f, 0f, poste, h), Rect(w - poste, 0f, w, h)).forEachIndexed { i, r ->
            tabla(r, c(mezcla(PINO, PINO_OSC, 0.5f), oscuro), 300 + i, u, oscuro, vertical = true, nudos = false)
            caja(Rect(if (i == 0) r.right - 0.8f * u else r.left, r.top, if (i == 0) r.right else r.left + 0.8f * u, r.bottom), Color.Black.copy(alpha = 0.3f))
        }
        parche(Rect(0.6f * u, h * 0.5f, poste - 0.6f * u, h * 0.5f + minOf(9f * u, poste * 1.2f)), u, oscuro)
        // El letrero pintado del almacén: el fondo ladrillo, el filete marino, el filo crema y una fila de estrellas.
        val letrero = Rect(w * 0.2f, h * 0.03f, w * 0.8f, h * 0.19f)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(letrero.left + 0.8f * u, letrero.top + 1.2f * u), letrero.size)
        caja(letrero, c(MARINO, oscuro, 0.4f))
        val dentro = Rect(letrero.left + 1.6f * u, letrero.top + 1.6f * u, letrero.right - 1.6f * u, letrero.bottom - 1.6f * u)
        caja(dentro, c(LADRILLO, oscuro, 0.4f))
        vetas(dentro, Color.Black.copy(alpha = 0.12f), 77, 1.6f * u, grosor = 0.3f * u)
        drawRect(c(GACETA, oscuro, 0.35f), Offset(dentro.left + 1.2f * u, dentro.top + 1.2f * u), Size(dentro.width - 2.4f * u, dentro.height - 2.4f * u), style = Stroke(0.45f * u))
        val ne = 5
        for (i in 0 until ne) drawPath(estrella(Offset(dentro.left + dentro.width * (i + 0.5f) / ne, dentro.center.y), dentro.height * 0.22f, 0.42f), c(GACETA, oscuro, 0.35f))
        listOf(letrero.left + 2f * u, letrero.right - 2f * u).forEach { xx -> clavo(Offset(xx, letrero.center.y), 0.5f * u, oscuro) }
        // El quinqué colgado del poste izquierdo y el rollo de cuerda en el derecho.
        quinque(Offset(w * 0.11f, h * 0.02f), minOf(16f * u, w * 0.08f), oscuro)
        val rc = Offset(w * 0.89f, h * 0.12f)
        val rr = minOf(6.5f * u, w * 0.04f)
        drawCircle(Color.Black.copy(alpha = 0.3f), rr, Offset(rc.x + 0.6f * u, rc.y + 1f * u))
        for (j in 0 until 4) drawCircle(c(mezcla(CORDEL, PINO_OSC, j * 0.12f), oscuro, 0.35f), rr * (1f - j * 0.2f), rc, style = Stroke(rr * 0.2f))
        drawLine(c(CORDEL, oscuro, 0.35f), Offset(rc.x + rr * 0.6f, rc.y + rr * 0.6f), Offset(rc.x + rr * 0.9f, rc.y + rr * 2.2f), rr * 0.18f, StrokeCap.Round)
        clavo(Offset(rc.x, rc.y - rr * 0.95f), 0.6f * u, oscuro)
        // La tabla de arriba de las cajas, con la marca estarcida de cada una.
        val cw = a.width / 5f
        val arriba = Rect(a.left - 0.6f * u, a.top - 6f * u, a.right + 0.6f * u, a.top)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(arriba.left, arriba.top - 1f * u), Size(arriba.width, 1f * u))
        for (i in 0 until 5) {
            val r = Rect(a.left + cw * i, arriba.top, a.left + cw * (i + 1), arriba.bottom)
            tabla(r, tono(i + 1, e.semilla + 1, oscuro), 400 + i, u, oscuro, nudos = false)
            marca(i + e.semilla, r.center, r.height * 0.34f, c(if (i % 2 == 0) MARINO else LADRILLO, oscuro, 0.25f).copy(alpha = 0.8f))
            clavo(Offset(r.left + 1.6f * u, r.center.y), 0.45f * u, oscuro); clavo(Offset(r.right - 1.6f * u, r.center.y), 0.45f * u, oscuro)
        }
        // Las cinco cajas de fruta de boca: el forro de gaceta (el nicho) y la sombra de la tabla de arriba.
        caja(a, nicho(GACETA, e, oscuro))
        for (i in 0 until 5) {
            val l = a.left + cw * i
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), a.top, a.top + 4f * u), Offset(l, a.top), Size(cw, 4f * u))
        }
        // Los costados de las cajas: dos tablas juntas entre caja y caja, con su clavo arriba y abajo.
        val lado = 1.3f * u
        for (i in 0..5) {
            val xc = a.left + cw * i
            listOf(-1, 1).forEach { s ->
                if ((i == 0 && s < 0) || (i == 5 && s > 0)) return@forEach
                val r = if (s < 0) Rect(xc - lado, a.top, xc, a.bottom) else Rect(xc, a.top, xc + lado, a.bottom)
                caja(r, tono(i * 2 + s + 10, e.semilla, oscuro, false))
                caja(Rect(r.left, r.top, r.left + 0.3f * u, r.bottom), Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f))
                caja(Rect(r.right - 0.3f * u, r.top, r.right, r.bottom), c(RENDIJA, oscuro, 0.2f).copy(alpha = 0.6f))
                clavo(Offset(r.center.x, a.top + 2f * u), 0.32f * u, oscuro); clavo(Offset(r.center.x, a.bottom - 2f * u), 0.32f * u, oscuro)
            }
        }
        // La tabla de abajo de las cajas y la repisa larga, con una tarjeta de envío colgada bajo cada caja.
        val abajo = Rect(a.left - 0.6f * u, a.bottom, a.right + 0.6f * u, a.bottom + 3.4f * u)
        for (i in 0 until 5) tabla(Rect(a.left + cw * i, abajo.top, a.left + cw * (i + 1), abajo.bottom), tono(i + 4, e.semilla + 2, oscuro), 500 + i, u, oscuro, nudos = false)
        val repisa = Rect(0f, abajo.bottom, w, abajo.bottom + 5f * u)
        tabla(repisa, c(PINO_LUZ, oscuro), 600, u, oscuro)
        caja(Rect(0f, repisa.bottom, w, repisa.bottom + 1.2f * u), Color.Black.copy(alpha = 0.3f))
        // Abajo, el piso de tablones con los tacos de la tarima asomando bajo la repisa.
        val piso = Rect(0f, repisa.bottom + 1.2f * u, w, h)
        val ap = piso.height / 2f
        for (j in 0 until 2) tabla(Rect(0f, piso.top + ap * j, w, piso.top + ap * (j + 1)), mezcla(tono(j + 40, e.semilla, oscuro, false), c(TABLON, oscuro), 0.5f), 700 + j, u, oscuro)
        caja(Rect(0f, piso.top, w, piso.top + 1.4f * u), Color.Black.copy(alpha = 0.3f))
        listOf(0.04f, 0.5f, 0.96f).forEachIndexed { i, f ->
            val r = Rect(w * f - 3.5f * u, piso.top, w * f + 3.5f * u, piso.top + ap * 1.2f)
            tabla(r, c(PINO_OSC, oscuro), 710 + i, u, oscuro, nudos = false)
            caja(Rect(r.left, r.bottom, r.right, r.bottom + 0.8f * u), Color.Black.copy(alpha = 0.3f))
            for (i in 0 until 5) {
            val cx = a.left + cw * (i + 0.5f)
            clavo(Offset(cx, repisa.top + 1.6f * u), 0.5f * u, oscuro)
            val tw = minOf(cw * 0.36f, 12f * u); val th = 9f * u
            val tg = Rect(cx - tw / 2, repisa.top + 3f * u, cx + tw / 2, repisa.top + 3f * u + th)
            rotate(if (i % 2 == 0) -6f else 5f, Offset(cx, repisa.top + 1.6f * u)) {
                drawLine(c(CORDEL, oscuro, 0.35f), Offset(cx, repisa.top + 1.6f * u), Offset(cx, tg.top + 1.4f * u), 0.3f * u)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(tg.left + 0.4f * u, tg.top + 0.6f * u), tg.size)
                val tag = Path().apply { moveTo(tg.left + tw * 0.25f, tg.top); lineTo(tg.right - tw * 0.25f, tg.top); lineTo(tg.right, tg.top + th * 0.2f); lineTo(tg.right, tg.bottom); lineTo(tg.left, tg.bottom); lineTo(tg.left, tg.top + th * 0.2f); close() }
                drawPath(tag, c(MANILA, oscuro, 0.4f))
                drawCircle(c(GACETA, oscuro, 0.3f), tw * 0.14f, Offset(cx, tg.top + 1.4f * u))
                drawCircle(c(PINO_OSC, oscuro, 0.3f), tw * 0.06f, Offset(cx, tg.top + 1.4f * u))
                for (j in 0 until 2) drawLine(c(TINTA, oscuro, 0.3f).copy(alpha = 0.4f), Offset(tg.left + tw * 0.18f, tg.top + th * (0.55f + 0.2f * j)), Offset(tg.right - tw * 0.18f, tg.top + th * (0.55f + 0.2f * j)), 0.25f * u)
            }
        }
    }
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = minOf(h, w / 2.2f) / 62f
        if (s <= 0f) return
        val c0 = Offset(w / 2, h / 2)
        val linea = if (oscuro) mezcla(e.tinta, e.fondo, 0.35f) else TINTA
        when (id) {
            "unesco" -> {
                // El ayuntamiento de tablas: la escalinata, el cuerpo de tablas, el frontón, el campanario y el andamio.
                val suelo = c0.y + 27f * s
                drawRect(Color.Black.copy(alpha = 0.15f), Offset(c0.x - 40f * s, suelo), Size(80f * s, 2f * s))
                val cuerpo = Rect(c0.x - 24f * s, suelo - 24f * s, c0.x + 24f * s, suelo - 3f * s)
                caja(Rect(c0.x - 8f * s, suelo - 3f * s, c0.x + 8f * s, suelo), c(PINO_OSC, oscuro, 0.3f))
                caja(cuerpo, c(GACETA, oscuro, 0.35f))
                var yy = cuerpo.top + 2.2f * s
                while (yy < cuerpo.bottom) { drawLine(c(PINO_OSC, oscuro, 0.3f).copy(alpha = 0.6f), Offset(cuerpo.left, yy), Offset(cuerpo.right, yy), 0.45f * s); yy += 2.6f * s }
                listOf(-15f, 15f).forEach { dx ->
                    val v = Rect(c0.x + dx * s - 4f * s, cuerpo.top + 5f * s, c0.x + dx * s + 4f * s, cuerpo.top + 15f * s)
                    caja(v.mas(1f * s), c(MARINO, oscuro, 0.2f))
                    caja(v, mezcla(e.c1, Color.White, 0.55f))
                    drawLine(c(MARINO, oscuro, 0.2f), Offset(v.center.x, v.top), Offset(v.center.x, v.bottom), 0.6f * s)
                    drawLine(c(MARINO, oscuro, 0.2f), Offset(v.left, v.center.y), Offset(v.right, v.center.y), 0.6f * s)
                }
                caja(Rect(c0.x - 4.5f * s, cuerpo.bottom - 13f * s, c0.x + 4.5f * s, cuerpo.bottom), e.c2)
                drawCircle(c(LATON, oscuro, 0.1f), 0.7f * s, Offset(c0.x + 2.6f * s, cuerpo.bottom - 6.5f * s))
                drawRect(linea, cuerpo.topLeft, cuerpo.size, style = Stroke(0.8f * s))
                // El frontón de tejas color ladrillo.
                val fr = Path().apply { moveTo(cuerpo.left - 3f * s, cuerpo.top); lineTo(c0.x, cuerpo.top - 11f * s); lineTo(cuerpo.right + 3f * s, cuerpo.top); close() }
                drawPath(fr, c(LADRILLO, oscuro, 0.2f))
                clipPath(fr) { for (j in 1..4) drawLine(Color.Black.copy(alpha = 0.2f), Offset(cuerpo.left - 4f * s, cuerpo.top - j * 2.4f * s), Offset(cuerpo.right + 4f * s, cuerpo.top - j * 2.4f * s), 0.5f * s) }
                drawPath(fr, linea, style = Stroke(0.8f * s, join = StrokeJoin.Round))
                // El campanario con su campana de latón, su chapitel y la bandera.
                val torre = Rect(c0.x - 5.5f * s, cuerpo.top - 22f * s, c0.x + 5.5f * s, cuerpo.top - 8f * s)
                caja(torre, c(GACETA, oscuro, 0.35f))
                caja(Rect(torre.left + 2f * s, torre.top + 2f * s, torre.right - 2f * s, torre.bottom - 3f * s), c(RENDIJA, oscuro, 0.1f))
                drawArc(c(LATON, oscuro, 0.1f), 180f, 180f, true, Offset(c0.x - 2.6f * s, torre.top + 4f * s), Size(5.2f * s, 7f * s))
                caja(Rect(c0.x - 3f * s, torre.top + 7.3f * s, c0.x + 3f * s, torre.top + 8.2f * s), c(LATON, oscuro, 0.1f))
                drawRect(linea, torre.topLeft, torre.size, style = Stroke(0.8f * s))
                val ch = Path().apply { moveTo(torre.left - 1.5f * s, torre.top); lineTo(c0.x, torre.top - 8f * s); lineTo(torre.right + 1.5f * s, torre.top); close() }
                drawPath(ch, c(MARINO, oscuro, 0.1f))
                drawPath(ch, linea, style = Stroke(0.8f * s, join = StrokeJoin.Round))
                drawLine(linea, Offset(c0.x, torre.top - 8f * s), Offset(c0.x, torre.top - 13f * s), 0.7f * s)
                drawPath(Path().apply { moveTo(c0.x, torre.top - 13f * s); lineTo(c0.x + 7f * s, torre.top - 11.5f * s); lineTo(c0.x, torre.top - 10f * s); close() }, e.c1)
                // El andamio de la reconstrucción, a la derecha: los palos, los travesaños y la tabla.
                val an = c(PINO, oscuro, 0.3f)
                listOf(28f, 36f).forEach { dx -> drawLine(an, Offset(c0.x + dx * s, suelo), Offset(c0.x + dx * s, suelo - 30f * s), 1.2f * s) }
                listOf(10f, 20f).forEach { dy -> drawLine(an, Offset(c0.x + 24f * s, suelo - dy * s), Offset(c0.x + 38f * s, suelo - dy * s), 1f * s) }
                drawLine(an, Offset(c0.x + 28f * s, suelo), Offset(c0.x + 36f * s, suelo - 10f * s), 0.7f * s)
                drawLine(an, Offset(c0.x + 36f * s, suelo - 10f * s), Offset(c0.x + 28f * s, suelo - 20f * s), 0.7f * s)
                caja(Rect(c0.x + 23f * s, suelo - 21f * s, c0.x + 39f * s, suelo - 19.4f * s), c(PINO_OSC, oscuro, 0.3f))
                // Un barril a la izquierda.
                val br = Rect(c0.x - 36f * s, suelo - 10f * s, c0.x - 28f * s, suelo)
                drawRoundRect(c(PINO_OSC, oscuro, 0.3f), br.topLeft, br.size, CornerRadius(2f * s))
                listOf(0.25f, 0.75f).forEach { f -> caja(Rect(br.left, br.top + br.height * f - 0.5f * s, br.right, br.top + br.height * f + 0.5f * s), c(CLAVO, oscuro, 0.1f)) }
                drawRoundRect(linea, br.topLeft, br.size, CornerRadius(2f * s), style = Stroke(0.6f * s))
            }
            "rascar" -> {
                // La placa de lata clavada, con el mapa a medio raspar y la moneda que raspa.
                val r = Rect(c0.x - 34f * s, c0.y - 22f * s, c0.x + 26f * s, c0.y + 24f * s)
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1.2f * s, r.top + 1.6f * s), r.size)
                caja(r, mezcla(e.c1, e.fondo, if (oscuro) 0.4f else 0.65f))
                val tierra = Path().apply {
                    moveTo(r.left + r.width * 0.1f, r.top + r.height * 0.35f); cubicTo(r.left + r.width * 0.25f, r.top + r.height * 0.1f, r.left + r.width * 0.45f, r.top + r.height * 0.2f, r.left + r.width * 0.42f, r.top + r.height * 0.45f)
                    cubicTo(r.left + r.width * 0.38f, r.top + r.height * 0.7f, r.left + r.width * 0.3f, r.top + r.height * 0.9f, r.left + r.width * 0.2f, r.top + r.height * 0.85f); cubicTo(r.left + r.width * 0.12f, r.top + r.height * 0.7f, r.left + r.width * 0.02f, r.top + r.height * 0.5f, r.left + r.width * 0.1f, r.top + r.height * 0.35f); close()
                    moveTo(r.left + r.width * 0.55f, r.top + r.height * 0.15f); cubicTo(r.left + r.width * 0.8f, r.top + r.height * 0.1f, r.left + r.width * 0.95f, r.top + r.height * 0.4f, r.left + r.width * 0.8f, r.top + r.height * 0.55f)
                    cubicTo(r.left + r.width * 0.68f, r.top + r.height * 0.5f, r.left + r.width * 0.5f, r.top + r.height * 0.35f, r.left + r.width * 0.55f, r.top + r.height * 0.15f); close()
                }
                drawPath(tierra, e.c2)
                // La capa de lata que falta raspar: la mitad derecha, con el borde mordido y sus rayas.
                val capa = Path().apply {
                    moveTo(r.left + r.width * 0.5f, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); lineTo(r.left + r.width * 0.36f, r.bottom)
                    var yy = r.bottom; var k = 0
                    while (yy > r.top) { yy -= r.height / 9f; lineTo(r.left + r.width * (0.36f + 0.14f * (r.bottom - yy) / r.height) + (if (k % 2 == 0) 2f * s else -1.5f * s), yy); k++ }
                    close()
                }
                clipPath(Path().apply { addRect(r) }) {
                    drawPath(capa, if (oscuro) Color(0xFF8E928E) else LATA)
                    for (k in 0 until 8) drawLine(Color.White.copy(alpha = 0.35f), Offset(r.left + r.width * (0.45f + k * 0.08f), r.top), Offset(r.left + r.width * (0.35f + k * 0.08f), r.bottom), 0.5f * s)
                }
                drawRect(linea, r.topLeft, r.size, style = Stroke(1f * s))
                listOf(Offset(r.left + 2.5f * s, r.top + 2.5f * s), Offset(r.right - 2.5f * s, r.top + 2.5f * s), Offset(r.left + 2.5f * s, r.bottom - 2.5f * s), Offset(r.right - 2.5f * s, r.bottom - 2.5f * s)).forEach { clavo(it, 1f * s, oscuro) }
                // La moneda de latón, de canto un poco, con su estrella.
                val mo = Offset(r.right + 4f * s, r.top + r.height * 0.62f)
                val mr = 10f * s
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(mo.x - mr + 1f * s, mo.y - mr * 0.85f + 2f * s), Size(mr * 2, mr * 1.7f))
                drawOval(c(mezcla(LATON, Color.Black, 0.3f), oscuro, 0.15f), Offset(mo.x - mr, mo.y - mr * 0.85f + 1.6f * s), Size(mr * 2, mr * 1.7f))
                drawOval(c(LATON, oscuro, 0.15f), Offset(mo.x - mr, mo.y - mr * 0.85f), Size(mr * 2, mr * 1.7f))
                drawOval(c(mezcla(LATON, Color.Black, 0.2f), oscuro, 0.15f), Offset(mo.x - mr * 0.8f, mo.y - mr * 0.68f), Size(mr * 1.6f, mr * 1.36f), style = Stroke(0.6f * s))
                drawPath(estrella(mo, mr * 0.45f, 0.42f), c(mezcla(LATON, Color.Black, 0.3f), oscuro, 0.15f))
                drawArc(Color.White.copy(alpha = 0.55f), 200f, 60f, false, Offset(mo.x - mr * 0.7f, mo.y - mr * 0.6f), Size(mr * 1.4f, mr * 1.2f), style = Stroke(1f * s, cap = StrokeCap.Round))
                listOf(Offset(-6f, 12f), Offset(-3f, 15f), Offset(-9f, 16f)).forEach { o -> drawCircle(if (oscuro) Color(0xFF8E928E) else LATA, 0.9f * s, Offset(mo.x + o.x * s, mo.y + o.y * s)) }
            }
            else -> {
                // Tus lugares: tres estacas de agrimensor con su banderín, clavadas en la tierra y unidas por el cordel.
                val suelo = Path().apply { moveTo(c0.x - 44f * s, c0.y + 26f * s); lineTo(c0.x + 44f * s, c0.y + 26f * s); lineTo(c0.x + 36f * s, c0.y + 6f * s); lineTo(c0.x - 36f * s, c0.y + 6f * s); close() }
                drawPath(suelo, c(Color(0xFFB59E74), oscuro, 0.35f))
                clipPath(suelo) {
                    val rnd = Random(11)
                    repeat(14) { val p = Offset(c0.x - 40f * s + rnd.nextFloat() * 80f * s, c0.y + 9f * s + rnd.nextFloat() * 16f * s); drawLine(c(Color(0xFF6E7A3A), oscuro, 0.3f), p, Offset(p.x + 0.8f * s, p.y - 2.2f * s), 0.6f * s, StrokeCap.Round) }
                }
                drawPath(suelo, linea, style = Stroke(0.8f * s, join = StrokeJoin.Round))
                val pies = listOf(Offset(c0.x - 26f * s, c0.y + 20f * s), Offset(c0.x + 2f * s, c0.y + 11f * s), Offset(c0.x + 28f * s, c0.y + 18f * s))
                val altos = listOf(24f, 30f, 24f)
                // El cordel de una estaca a otra.
                for (i in 0 until 2) drawLine(c(CORDEL, oscuro, 0.1f), Offset(pies[i].x, pies[i].y - 4f * s), Offset(pies[i + 1].x, pies[i + 1].y - 4f * s), 0.7f * s)
                listOf(e.c1, e.c2, c(LADRILLO, oscuro, 0.1f)).forEachIndexed { i, col ->
                    val p = pies[i]; val top = Offset(p.x, p.y - altos[i] * s)
                    drawOval(Color.Black.copy(alpha = 0.25f), Offset(p.x - 3.5f * s, p.y - 1f * s), Size(7f * s, 2f * s))
                    caja(Rect(p.x - 1.3f * s, top.y, p.x + 1.3f * s, p.y), c(PINO_LUZ, oscuro, 0.3f))
                    caja(Rect(p.x + 0.4f * s, top.y, p.x + 1.3f * s, p.y), c(PINO_OSC, oscuro, 0.3f))
                    drawPath(Path().apply { moveTo(p.x - 1.3f * s, p.y); lineTo(p.x, p.y + 2f * s); lineTo(p.x + 1.3f * s, p.y); close() }, c(PINO_OSC, oscuro, 0.3f))
                    // El banderín de tela con su pliegue.
                    val b = Path().apply { moveTo(p.x + 1.3f * s, top.y); quadraticTo(p.x + 7f * s, top.y + 1.5f * s, p.x + 13f * s, top.y + 0.5f * s); lineTo(p.x + 10f * s, top.y + 4.5f * s); lineTo(p.x + 13f * s, top.y + 8.5f * s); quadraticTo(p.x + 7f * s, top.y + 9.5f * s, p.x + 1.3f * s, top.y + 8f * s); close() }
                    drawPath(b, col)
                    drawLine(mezcla(col, Color.Black, 0.25f), Offset(p.x + 6f * s, top.y + 1.4f * s), Offset(p.x + 6.5f * s, top.y + 8.6f * s), 0.6f * s)
                    drawPath(b, linea, style = Stroke(0.6f * s, join = StrokeJoin.Round))
                    drawRect(linea, Offset(p.x - 1.3f * s, top.y), Size(2.6f * s, p.y - top.y), style = Stroke(0.5f * s))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = h / 100f
        if (u <= 0f) return
        val a = area(MuebleVida.NOTAS).let { Rect(w * it.left, h * it.top, w * it.right, h * it.bottom) }
        // La pared del almacén detrás del tablón.
        val pt = 8f * u
        var x = 0f; var k = 0
        while (x < w) { tabla(Rect(x, 0f, minOf(x + pt, w), h), mezcla(tono(k, e.semilla, oscuro, false), c(TABLON, oscuro), 0.6f), 800 + k, u, oscuro, vertical = true); x += pt; k++ }
        // El marco de tablas clavadas alrededor del fondo encalado.
        val marco = Rect(a.left - 3.2f * u, a.top - 2.6f * u, a.right + 3.2f * u, a.bottom + 2.6f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.35f), Offset(marco.left + 1f * u, marco.top + 1.4f * u), marco.size)
        tabla(Rect(marco.left, marco.top, marco.right, a.top), c(PINO, oscuro), 900, u, oscuro, nudos = false)
        tabla(Rect(marco.left, a.bottom, marco.right, marco.bottom), c(PINO, oscuro), 901, u, oscuro, nudos = false)
        tabla(Rect(marco.left, marco.top, a.left, marco.bottom), c(PINO_OSC, oscuro), 902, u, oscuro, vertical = true)
        tabla(Rect(a.right, marco.top, marco.right, marco.bottom), c(PINO_OSC, oscuro), 903, u, oscuro, vertical = true)
        listOf(Offset(marco.left, marco.top), Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), Offset(marco.right, marco.bottom)).forEach { o ->
            val dx = if (o.x < w / 2) 1.6f * u else -1.6f * u; val dy = if (o.y < h / 2) 1.3f * u else -1.3f * u
            clavo(Offset(o.x + dx, o.y + dy), 0.5f * u, oscuro); clavo(Offset(o.x + dx, o.y + dy * 2.6f), 0.5f * u, oscuro)
        }
        parche(Rect(marco.right - 3.2f * u + 0.3f * u, a.top + a.height * 0.55f, marco.right - 0.3f * u, a.top + a.height * 0.55f + 9f * u), u, oscuro)
        // El fondo de tablas encaladas (el nicho de las notas), con las juntas apenas marcadas.
        caja(a, nicho(CAL, e, oscuro))
        val nj = 7
        for (j in 1 until nj) {
            val jx = a.left + a.width * j / nj
            drawLine((if (oscuro) Color.Black else PINO_OSC).copy(alpha = if (oscuro) 0.18f else 0.12f), Offset(jx, a.top), Offset(jx, a.bottom), 0.3f * u)
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), a.top, a.top + 4f * u), a.topLeft, Size(a.width, 4f * u))
        // El tejadillo: las ménsulas, el alero con su tabla de frente y las hileras de tejamanil.
        val alero = a.top - 2.6f * u
        listOf(marco.left + 1.6f * u, marco.right - 1.6f * u).forEach { mx ->
            drawPath(Path().apply { moveTo(mx - 1.4f * u, alero); lineTo(mx + 1.4f * u, alero); lineTo(mx + 1.4f * u, alero + 7f * u); close() }.let { p -> if (mx < w / 2) p else Path().apply { moveTo(mx - 1.4f * u, alero); lineTo(mx + 1.4f * u, alero); lineTo(mx - 1.4f * u, alero + 7f * u); close() } }, c(PINO_OSC, oscuro))
        }
        val techo = Rect(marco.left - 3f * u, 0.8f * u, marco.right + 3f * u, alero - 1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(techo.left, alero - 1.2f * u), Size(techo.width, 2f * u))
        // El faldón del tejado se abre hacia el alero: cada hilera de tejamanil sobresale un poco más que la de arriba.
        val filas = 3
        val fh = techo.height / filas
        for (f in 0 until filas) {
            val yy = techo.top + fh * f
            val vuelo = (filas - 1 - f) * 1.8f * u
            val x0 = techo.left + vuelo; val x1 = techo.right - vuelo
            caja(Rect(x0, yy, x1, yy + fh + 0.8f * u), c(RENDIJA, oscuro, 0.2f))
            var xx = x0 - 2f * u * azar(f, 1)
            var t = 0
            while (xx < x1) {
                val tw = (3.6f + 2.8f * azar(f * 31 + t, 7)) * u
                val largo = fh * (0.9f + 0.18f * azar(f * 31 + t, 5))
                val r = Rect(maxOf(xx, x0), yy, minOf(xx + tw - 0.35f * u, x1), yy + largo + 0.8f * u)
                val tn = c(mezcla(Color(0xFF7E6650), Color(0xFFB08E66), azar(f * 31 + t, 3)), oscuro)
                caja(r, tn)
                drawLine(Color.Black.copy(alpha = 0.18f), Offset(r.left + r.width * (0.3f + 0.4f * azar(t, f)), r.top + fh * 0.3f), Offset(r.left + r.width * (0.3f + 0.4f * azar(t, f)), r.bottom - 1f * u), 0.25f * u)
                caja(Rect(r.left, r.bottom - 0.9f * u, r.right, r.bottom), mezcla(tn, Color.Black, 0.3f))
                caja(Rect(r.left, r.top, r.left + 0.35f * u, r.bottom), Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f))
                xx += tw; t++
            }
            // La sombra que la hilera de arriba echa sobre ésta.
            if (f > 0) drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), yy, yy + fh * 0.4f), Offset(x0, yy), Size(x1 - x0, fh * 0.4f))
        }
        // El caballete de arriba, con su tabla de remate.
        tabla(Rect(techo.left + filas * 1.4f * u - 1f * u, 0f, techo.right - filas * 1.4f * u + 1f * u, 2.2f * u), c(PINO_OSC, oscuro), 951, u, oscuro, nudos = false)
        val frente = Rect(techo.left, alero - 2.4f * u, techo.right, alero)
        tabla(frente, c(PINO_LUZ, oscuro), 950, u, oscuro, nudos = false)
        // La repisa de abajo con el martillo y la lata de tachuelas.
        val rep = Rect(marco.left - 1.6f * u, marco.bottom, marco.right + 1.6f * u, marco.bottom + 2.6f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(rep.left, rep.bottom), Size(rep.width, 1.4f * u))
        tabla(rep, c(PINO_LUZ, oscuro), 960, u, oscuro, nudos = false)
        val mz = Offset(a.left + a.width * 0.12f, rep.top)
        caja(Rect(mz.x, mz.y - 1.4f * u, mz.x + 15f * u, mz.y - 0.2f * u), c(PINO, oscuro))
        caja(Rect(mz.x, mz.y - 1.4f * u, mz.x + 15f * u, mz.y - 1f * u), Color.White.copy(alpha = 0.2f))
        drawRoundRect(c(CLAVO, oscuro, 0.2f), Offset(mz.x + 13f * u, mz.y - 3.2f * u), Size(3.6f * u, 3f * u), CornerRadius(0.4f * u))
        drawRect(Color.White.copy(alpha = 0.15f), Offset(mz.x + 13.2f * u, mz.y - 3f * u), Size(3.2f * u, 0.5f * u))
        val lt = Rect(a.right - a.width * 0.16f, rep.top - 3.6f * u, a.right - a.width * 0.16f + 5.4f * u, rep.top)
        caja(lt, c(LATA, oscuro))
        caja(Rect(lt.left, lt.top, lt.right, lt.top + 0.6f * u), Color.White.copy(alpha = 0.4f))
        caja(Rect(lt.left + 0.8f * u, lt.top, lt.left + 1.6f * u, lt.bottom), Color.White.copy(alpha = 0.25f))
        drawRect(c(LADRILLO, oscuro, 0.3f), Offset(lt.left, lt.top + 1.2f * u), Size(lt.width, 1.4f * u))
        listOf(1.4f, 2.7f, 4f).forEach { dx -> drawCircle(c(LATON, oscuro, 0.2f), 0.5f * u, Offset(lt.left + dx * u, lt.top - 0.2f * u)) }
        // El lápiz colgado de su cordel en el costado derecho.
        val cl = Offset(marco.right - 1.6f * u, a.top + a.height * 0.18f)
        drawLine(c(CORDEL, oscuro, 0.35f), cl, Offset(cl.x + 0.2f * u, cl.y + 10f * u), 0.3f * u)
        drawLine(c(LADRILLO, oscuro, 0.3f), Offset(cl.x + 0.2f * u, cl.y + 10f * u), Offset(cl.x + 0.2f * u, cl.y + 18f * u), 1.1f * u)
        drawLine(c(PINO_LUZ, oscuro), Offset(cl.x + 0.2f * u, cl.y + 18f * u), Offset(cl.x + 0.2f * u, cl.y + 19.6f * u), 1.1f * u, StrokeCap.Round)
        clavo(cl, 0.5f * u, oscuro)
    }

    /** El papel de cada tipo de nota: la gaceta, el papel de carta, la cartulina manila y el boleto. */
    private fun papel(tipo: String, oscuro: Boolean): Color {
        val p = when (tipo) {
            "idea" -> GACETA
            "proposito" -> Color(0xFFE2CA96)
            "recordatorio" -> Color(0xFFF1E3C8)
            else -> Color(0xFFF6F0E2)
        }
        return if (oscuro) mezcla(p, NOCHE, 0.2f) else p
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val k = minOf(r.width, r.height) / 100f
        if (k <= 0f) return
        val hoja = Rect(r.left + 2f * k, r.top + 2.5f * k, r.right - 2.5f * k, r.bottom - 3f * k)
        val p = papel(e.tipo, oscuro)
        val tinta = tintaNota(oscuro)
        val mx = r.width * 0.12f; val my = r.height * 0.16f
        val gira = (if (e.indice % 2 == 0) -1.2f else 1f) * (0.5f + (e.indice % 3) * 0.4f)
        rotate(gira, hoja.center) {
            val forma = when (e.tipo) {
                // El recorte de gaceta, cortado a tijera.
                "idea" -> tijera(hoja, e.indice * 17 + 3, 1.2f * k)
                // La tarjeta de envío, con las esquinas de arriba cortadas.
                "proposito" -> Path().apply {
                    val d = hoja.width * 0.14f
                    moveTo(hoja.left + d, hoja.top); lineTo(hoja.right - d, hoja.top); lineTo(hoja.right, hoja.top + d); lineTo(hoja.right, hoja.bottom); lineTo(hoja.left, hoja.bottom); lineTo(hoja.left, hoja.top + d); close()
                }
                // El boleto, con las muescas del talón.
                "recordatorio" -> Path().apply {
                    val ty = hoja.bottom - my * 0.75f
                    val n = my * 0.18f
                    moveTo(hoja.left, hoja.top); lineTo(hoja.right, hoja.top); lineTo(hoja.right, ty - n)
                    quadraticTo(hoja.right - n * 1.4f, ty, hoja.right, ty + n); lineTo(hoja.right, hoja.bottom); lineTo(hoja.left, hoja.bottom); lineTo(hoja.left, ty + n)
                    quadraticTo(hoja.left + n * 1.4f, ty, hoja.left, ty - n); close()
                }
                else -> Path().apply { addRect(hoja) }
            }
            // La sombra en las tablas y el papel.
            drawPath(forma.let { f -> Path().apply { addPath(f, Offset(1.6f * k, 2.2f * k)) } }, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f))
            drawPath(forma, p)
            when (e.tipo) {
                "idea" -> {
                    // Los filetes de la columna de la gaceta, en el margen de arriba y el de abajo, y una estrella de imprenta.
                    drawLine(tinta.copy(alpha = 0.55f), Offset(hoja.left + 4f * k, r.top + my * 0.55f), Offset(hoja.right - 4f * k, r.top + my * 0.55f), 0.9f * k)
                    drawLine(tinta.copy(alpha = 0.4f), Offset(hoja.left + 4f * k, r.top + my * 0.55f + 1.6f * k), Offset(hoja.right - 4f * k, r.top + my * 0.55f + 1.6f * k), 0.4f * k)
                    drawLine(tinta.copy(alpha = 0.35f), Offset(hoja.left + 4f * k, r.bottom - my * 0.5f), Offset(hoja.right - 4f * k, r.bottom - my * 0.5f), 0.4f * k)
                }
                "proposito" -> {
                    // El ojal reforzado con su cordel.
                    val oj = Offset(hoja.center.x, hoja.top + my * 0.45f)
                    drawCircle(c(GACETA, oscuro, 0.25f), my * 0.26f, oj)
                    drawCircle(c(PINO_OSC, oscuro, 0.2f), my * 0.12f, oj)
                    drawPath(Path().apply { moveTo(oj.x, oj.y); cubicTo(oj.x - 4f * k, oj.y - 6f * k, oj.x + 6f * k, oj.y - 8f * k, oj.x + 10f * k, oj.y - 3f * k) }, c(CORDEL, oscuro, 0.25f), style = Stroke(0.7f * k, cap = StrokeCap.Round))
                    drawLine(mezcla(p, Color.Black, 0.18f), Offset(hoja.left, r.bottom - my * 0.6f), Offset(hoja.right, r.bottom - my * 0.6f), 0.4f * k)
                }
                "recordatorio" -> {
                    // La franja ladrillo de arriba y la línea picada del talón.
                    caja(Rect(hoja.left, hoja.top, hoja.right, hoja.top + my * 0.38f), c(LADRILLO, oscuro, 0.15f))
                    val ty = hoja.bottom - my * 0.75f
                    var xx = hoja.left + my * 0.4f
                    while (xx < hoja.right - my * 0.4f) { drawCircle(mezcla(p, Color.Black, 0.35f), 0.45f * k, Offset(xx, ty)); xx += 2.2f * k }
                    drawPath(estrella(Offset(hoja.center.x, (ty + hoja.bottom) / 2), my * 0.16f, 0.42f), c(LADRILLO, oscuro, 0.15f))
                }
                else -> {
                    // La carta doblada en tres: los dobleces apenas marcados y la orilla de arriba en marino.
                    listOf(1f / 3f, 2f / 3f).forEach { f -> drawLine(mezcla(p, Color.Black, 0.07f), Offset(hoja.left, hoja.top + hoja.height * f), Offset(hoja.right, hoja.top + hoja.height * f), 0.5f * k) }
                    drawLine(c(MARINO, oscuro, 0.15f).copy(alpha = 0.6f), Offset(hoja.left + 3f * k, r.top + my * 0.6f), Offset(hoja.right - 3f * k, r.top + my * 0.6f), 0.5f * k)
                }
            }
            // La marca estarcida del tipo, en la esquina de arriba a la derecha.
            val mc = Offset(r.right - mx * 0.72f, r.top + my * 0.62f)
            val ms = minOf(mx, my) * 0.3f
            val marcaColor = mezcla(e.acento, tinta, 0.2f)
            when (e.tipo) {
                "idea" -> drawPath(estrella(mc, ms, 0.42f), marcaColor)
                "proposito" -> marca(2, mc, ms, marcaColor)
                "recordatorio" -> marca(1, Offset(mc.x, mc.y + my * 0.2f), ms, mezcla(c(GACETA, oscuro, 0.1f), Color.White, 0.3f))
                else -> marca(0, mc, ms, marcaColor)
            }
            drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), style = Stroke(0.4f * k))
        }
        if (e.fijada) {
            // La tachuela de latón arriba al centro.
            tachuela(Offset(r.center.x, hoja.top + my * 0.32f), minOf(my * 0.26f, 4.4f * k), oscuro)
        } else {
            // La cinta amarillenta cruzada arriba, en una esquina.
            val izq = e.indice % 2 == 0
            val cx = if (izq) hoja.left + 3f * k else hoja.right - 3f * k
            rotate(if (izq) -35f else 35f, Offset(cx, hoja.top + 1f * k)) {
                drawRect(c(CINTA, oscuro, 0.25f).copy(alpha = 0.9f), Offset(cx - 8f * k, hoja.top - 2f * k), Size(16f * k, 5.5f * k))
                drawRect(Color.White.copy(alpha = 0.25f), Offset(cx - 8f * k, hoja.top - 2f * k), Size(16f * k, 1.2f * k))
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
