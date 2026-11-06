package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Edad Moderna
// =====================================================================

/**
 * Japón Edo: el tansu, la cómoda de cajones de madera de keyaki con herrajes de hierro negro, sobre el tatami y con una rama
 * de ciruelo en flor encima. Cada tema es un cajón con su papel de washi pegado y su tirador de hierro; los cajones más
 * llenos quedan entreabiertos y asoman los tanzaku de colores. Por dentro, el cajón es de kiri (paulonia), claro y de veta
 * recta. Las fichas son tanzaku teñidos en nube (uchigumori: añil arriba, violeta abajo) con hojuelas de oro y su cordón.
 */
object IdeasEdo : DisenoIdeas {
    private val KEYAKI = Color(0xFF8B5A2E)
    private val KEYAKI_OSC = Color(0xFF55331A)
    private val KEYAKI_LUZ = Color(0xFFB98552)
    private val HIERRO = Color(0xFF1E1B1A)
    private val HIERRO_LUZ = Color(0xFF5C5650)
    private val KIRI = Color(0xFFE8D8B9)
    private val KIRI_VETA = Color(0xFFB89D70)
    private val WASHI = Color(0xFFF4EEDF)
    private val SUMI = Color(0xFF1B1A1E)
    private val BERMELLON = Color(0xFFC9442E)
    private val ANIL = Color(0xFF1E3A5F)
    private val VIOLETA = Color(0xFF5E4383)
    private val ORO = Color(0xFFC9A24C)
    private val TATAMI = Color(0xFFC8B77A)
    private val TATAMI_OSC = Color(0xFF9A8A50)
    private val HERI = Color(0xFF1F2B3D)
    /** Los colores de los tanzaku que asoman de los cajones. */
    private val TANZAKU = listOf(WASHI, Color(0xFFBCCADC), Color(0xFFEBB9A8), Color(0xFFE9DCA4), Color(0xFFCFBEDB), Color(0xFFBFD6B9))

    private fun madera(oscuro: Boolean) = if (oscuro) mezcla(KEYAKI, Color.Black, 0.22f) else KEYAKI

    override val cols = 3
    override val proporcion = 1.55f
    override val corona = 0.17f
    override val base = 0.1f
    override val lado = 0.055f
    override val hueco = 0.022f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val kk = madera(oscuro)
        // El tatami bajo el mueble, con su borde (heri) de tela añil.
        val piso = d.base.top + d.base.height * 0.55f
        drawRect(Brush.verticalGradient(listOf(mezcla(TATAMI, Color.Black, if (oscuro) 0.45f else 0.08f), mezcla(TATAMI_OSC, Color.Black, if (oscuro) 0.5f else 0.1f)), piso, h), Offset(0f, piso), Size(w, h - piso))
        var y = piso + 3 * u
        while (y < h) { drawLine(TATAMI_OSC.copy(alpha = 0.55f), Offset(0f, y), Offset(w, y), 0.7f * u); y += 2.2f * u }
        drawRect(HERI, Offset(0f, piso), Size(w, 4 * u))
        for (k in 0 until (w / (7 * u)).toInt()) drawCircle(Color.White.copy(alpha = 0.18f), 0.8f * u, Offset(k * 7 * u + 3 * u, piso + 2 * u))
        // El cuerpo: de la corona al zócalo.
        val cuerpo = Rect(w * 0.025f, d.corona.bottom - 2 * u, w * 0.975f, d.base.top + d.base.height * 0.42f)
        sombraSuave(Rect(cuerpo.left, cuerpo.bottom - 6 * u, cuerpo.right, cuerpo.bottom + 4 * u), 4 * u, 10 * u, 0.35f)
        drawRect(Brush.horizontalGradient(listOf(mezcla(kk, Color.Black, 0.18f), kk, mezcla(kk, KEYAKI_LUZ, 0.25f), kk, mezcla(kk, Color.Black, 0.22f)), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, KEYAKI_OSC.copy(alpha = 0.3f), 11, 1.2f, grosor = 0.7f, ondulacion = 0.4f)
        // Los huecos de los cajones: la sombra del rebaje alrededor de cada uno.
        d.huecos.forEach { r ->
            drawRect(Color.Black.copy(alpha = 0.55f), Offset(r.left - 1.4f * u, r.top - 1.4f * u), Size(r.width + 2.8f * u, r.height + 2.8f * u))
            drawLine(KEYAKI_LUZ.copy(alpha = 0.5f), Offset(r.left - 1.4f * u, r.bottom + 1.8f * u), Offset(r.right + 1.4f * u, r.bottom + 1.8f * u), 0.8f * u)
        }
        // Los travesaños entre filas: una línea de luz arriba y otra de sombra abajo, como una tabla con canto.
        d.filas.zipWithNext().forEach { (a, b) ->
            val m = (a.bottom + b.top) / 2
            drawLine(KEYAKI_OSC.copy(alpha = 0.5f), Offset(cuerpo.left + 3 * u, m), Offset(cuerpo.right - 3 * u, m), 0.6f * u)
        }
        // Los costados: la tabla gruesa de cada lado, con su canto iluminado.
        listOf(cuerpo.left to 1f, cuerpo.right to -1f).forEach { (x, s) ->
            drawLine(Color.White.copy(alpha = 0.12f), Offset(x + s * 1.2f * u, cuerpo.top), Offset(x + s * 1.2f * u, cuerpo.bottom), 1.2f * u)
        }
        // Las asas laterales de hierro (para cargarlo entre dos), asomando por los costados.
        listOf(cuerpo.left, cuerpo.right).forEach { x ->
            val yc = cuerpo.top + cuerpo.height * 0.32f; val r = 7 * u
            drawArc(HIERRO, if (x < w / 2) 90f else -90f, 180f, false, Offset(x - r, yc - r), Size(2 * r, 2 * r), style = Stroke(2.4f * u))
            drawRect(HIERRO, Offset(x - 2.5f * u, yc - r - 2 * u), Size(5 * u, 2 * r + 4 * u))
            drawCircle(HIERRO_LUZ, 0.9f * u, Offset(x, yc - r + 1 * u)); drawCircle(HIERRO_LUZ, 0.9f * u, Offset(x, yc + r - 1 * u))
        }
        // Escuadras de hierro en las cuatro esquinas del cuerpo.
        escuadra(Offset(cuerpo.left, cuerpo.top), 1f, 1f, 16 * u)
        escuadra(Offset(cuerpo.right, cuerpo.top), -1f, 1f, 16 * u)
        escuadra(Offset(cuerpo.left, cuerpo.bottom), 1f, -1f, 16 * u)
        escuadra(Offset(cuerpo.right, cuerpo.bottom), -1f, -1f, 16 * u)
        // El zócalo, un poco hundido, con sus tapas de hierro en las esquinas.
        val zocalo = Rect(cuerpo.left + 6 * u, cuerpo.bottom, cuerpo.right - 6 * u, piso + 1 * u)
        drawRect(mezcla(kk, Color.Black, 0.4f), zocalo.topLeft, zocalo.size)
        vetas(zocalo, Color.Black.copy(alpha = 0.25f), 5, 0.5f)
        drawRect(Color.Black.copy(alpha = 0.35f), zocalo.topLeft, Size(zocalo.width, 2 * u))
        if (d.mas > 0) placa?.let { p ->
            val r = Rect(d.base.left + d.base.width * p.left, d.base.top + d.base.height * p.top, d.base.left + d.base.width * p.right, d.base.top + d.base.height * p.bottom)
            drawRoundRect(WASHI.copy(alpha = if (oscuro) 0.85f else 0.95f), r.topLeft, r.size, CornerRadius(1.5f * u))
            drawRect(BERMELLON, Offset(r.right - r.height * 0.7f, r.top + r.height * 0.18f), Size(r.height * 0.5f, r.height * 0.5f))
        }
        // La tapa de arriba: más ancha que el cuerpo, gruesa, con sus cantoneras.
        val tapa = Rect(0f, d.corona.bottom - d.corona.height * 0.3f, w, d.corona.bottom + 1 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + 2 * u, tapa.bottom), Size(tapa.width - 4 * u, 3 * u))
        drawRect(Brush.verticalGradient(listOf(mezcla(kk, KEYAKI_LUZ, 0.45f), kk, mezcla(kk, Color.Black, 0.3f)), tapa.top, tapa.bottom), tapa.topLeft, tapa.size)
        vetas(tapa, KEYAKI_OSC.copy(alpha = 0.35f), 23, 1.3f, grosor = 0.7f, ondulacion = 0.35f)
        listOf(tapa.left, tapa.right - 12 * u).forEach { x ->
            drawRect(HIERRO, Offset(x, tapa.top), Size(12 * u, tapa.height))
            drawCircle(HIERRO_LUZ, 1.1f * u, Offset(x + 6 * u, tapa.center.y))
        }
        // Encima, un florero de cerámica oscura con una rama de ciruelo en flor.
        ramaCiruelo(Offset(w * 0.8f, tapa.top), d.corona.height * 0.68f, oscuro)
    }

    /** Una escuadra de hierro con sus remaches, en la esquina [o]; [sx] y [sy] dicen hacia dónde se abre. */
    private fun DrawScope.escuadra(o: Offset, sx: Float, sy: Float, l: Float) {
        val g = 3.2f * density
        drawRect(HIERRO, Offset(if (sx > 0) o.x else o.x - l, if (sy > 0) o.y else o.y - g), Size(l, g))
        drawRect(HIERRO, Offset(if (sx > 0) o.x else o.x - g, if (sy > 0) o.y else o.y - l), Size(g, l))
        // La punta en forma de hoja (warabi) al final de cada brazo.
        drawCircle(HIERRO, g * 0.6f, Offset(o.x + sx * l, o.y + sy * g / 2))
        drawCircle(HIERRO, g * 0.6f, Offset(o.x + sx * g / 2, o.y + sy * l))
        listOf(Offset(o.x + sx * l * 0.55f, o.y + sy * g / 2), Offset(o.x + sx * g / 2, o.y + sy * l * 0.55f), Offset(o.x + sx * g / 2, o.y + sy * g / 2)).forEach {
            drawCircle(HIERRO_LUZ, 0.75f * density, it)
        }
    }

    /** El florero (un tsubo de cerámica con vidriado oscuro) y la rama torcida de ciruelo con sus flores bermellón. */
    private fun DrawScope.ramaCiruelo(base: Offset, alto: Float, oscuro: Boolean) {
        val u = density
        val ancho = alto * 0.42f
        val v = Path().apply {
            moveTo(base.x - ancho * 0.28f, base.y)
            cubicTo(base.x - ancho * 0.62f, base.y - alto * 0.18f, base.x - ancho * 0.5f, base.y - alto * 0.42f, base.x - ancho * 0.16f, base.y - alto * 0.48f)
            lineTo(base.x + ancho * 0.16f, base.y - alto * 0.48f)
            cubicTo(base.x + ancho * 0.5f, base.y - alto * 0.42f, base.x + ancho * 0.62f, base.y - alto * 0.18f, base.x + ancho * 0.28f, base.y)
            close()
        }
        drawPath(v, Brush.horizontalGradient(listOf(Color(0xFF2B2622), Color(0xFF5A4E44), Color(0xFF2B2622)), base.x - ancho * 0.6f, base.x + ancho * 0.6f))
        drawLine(Color(0xFF8C7A62), Offset(base.x - ancho * 0.45f, base.y - alto * 0.22f), Offset(base.x + ancho * 0.45f, base.y - alto * 0.22f), 1.2f * u)
        drawOval(Color(0xFF1A1714), Offset(base.x - ancho * 0.18f, base.y - alto * 0.52f), Size(ancho * 0.36f, alto * 0.07f))
        // La rama: tramos quebrados, cada vez más finos.
        val tronco = listOf(Offset(base.x, base.y - alto * 0.5f), Offset(base.x - alto * 0.12f, base.y - alto * 0.72f), Offset(base.x - alto * 0.05f, base.y - alto * 0.86f),
            Offset(base.x - alto * 0.32f, base.y - alto * 1.0f))
        tronco.zipWithNext().forEachIndexed { k, (a, b) -> drawLine(Color(0xFF3A2A20), a, b, (2.6f - k * 0.6f) * u, StrokeCap.Round) }
        val brote = listOf(Offset(base.x - alto * 0.05f, base.y - alto * 0.86f), Offset(base.x + alto * 0.2f, base.y - alto * 0.95f))
        drawLine(Color(0xFF3A2A20), brote[0], brote[1], 1.2f * u, StrokeCap.Round)
        val flores = listOf(tronco[1], tronco[2], tronco[3], brote[1], Offset(base.x - alto * 0.2f, base.y - alto * 0.92f), Offset(base.x + alto * 0.08f, base.y - alto * 0.9f))
        flores.forEachIndexed { k, f ->
            val r = (2.2f + 1.2f * azar(77, k)) * u
            for (p in 0 until 5) {
                val ang = p * 1.2566f + azar(78, k)
                drawCircle(if (oscuro) mezcla(BERMELLON, Color.White, 0.1f) else BERMELLON, r * 0.55f, Offset(f.x + r * 0.6f * kotlin.math.cos(ang), f.y + r * 0.6f * kotlin.math.sin(ang)))
            }
            drawCircle(ORO, r * 0.3f, f)
        }
    }

    /** Cuánto baja el frente del cajón al abrirse completo, en fracciones de su alto. */
    private const val SALIDA = 0.2f

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val kk = madera(oscuro)
        val ab = c.abierto.coerceIn(0f, 1f)
        // Los cajones más llenos quedan entreabiertos y asoman los tanzaku.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF120B06), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 7)
            for (i in 0 until k) {
                val x = w * (0.1f + 0.78f * (i + 0.5f) / k) - w * 0.035f
                val y0 = arriba * (0.08f + 0.5f * azar(c.semilla, i)) + 1 * u
                val col = TANZAKU[(i + c.semilla.mod(TANZAKU.size)) % TANZAKU.size].let { if (oscuro) mezcla(it, Color.Black, 0.25f) else it }
                drawRect(col, Offset(x, y0), Size(w * 0.07f, arriba + 4 * u - y0))
                drawRect(ANIL.copy(alpha = 0.45f), Offset(x, y0), Size(w * 0.07f, 2.4f * u))
                drawCircle(Color.Black.copy(alpha = 0.4f), 0.8f * u, Offset(x + w * 0.035f, y0 + 4 * u))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent), 0f, arriba * 0.6f), Offset.Zero, Size(w, arriba))
        }
        // El frente del cajón: al abrirse baja y se acerca un poco.
        val e = w * 0.03f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(Brush.verticalGradient(listOf(mezcla(kk, KEYAKI_LUZ, 0.3f), kk, mezcla(kk, Color.Black, 0.15f)), f.top, f.bottom), f.topLeft, f.size)
        vetas(f, KEYAKI_OSC.copy(alpha = 0.38f), c.semilla, 1.4f, grosor = 0.7f, ondulacion = 0.45f)
        bisel(f, Color.White.copy(alpha = 0.18f), Color.Black.copy(alpha = 0.35f), 1.3f * u)
        // Escuadras en las cuatro esquinas del frente.
        val l = minOf(w, f.height) * 0.2f
        escuadra(Offset(f.left, f.top), 1f, 1f, l)
        escuadra(Offset(f.right, f.top), -1f, 1f, l)
        escuadra(Offset(f.left, f.bottom), 1f, -1f, l)
        escuadra(Offset(f.right, f.bottom), -1f, -1f, l)
        // El papel de washi con el tema, pegado al frente, con su sello bermellón.
        if (!c.vacio) {
            val r = Rect(w * rotulo.left - 2 * u, h * rotulo.top + baja - 1 * u, w * rotulo.right + 2 * u, h * rotulo.bottom + baja + 1 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1 * u, r.top + 1.2f * u), r.size)
            drawRect(if (oscuro) mezcla(WASHI, Color(0xFF6B5E48), 0.18f) else WASHI, r.topLeft, r.size)
            for (k in 0 until 7) {
                val y = r.top + r.height * azar(c.semilla + 9, k)
                drawLine(Color(0xFFB5A27E).copy(alpha = 0.35f), Offset(r.left + r.width * azar(c.semilla, k + 20) * 0.5f, y), Offset(r.left + r.width * (0.4f + azar(c.semilla, k + 40) * 0.6f), y + 1 * u), 0.5f * u)
            }
            val s = minOf(r.height * 0.32f, 7 * u)
            drawRect(BERMELLON.copy(alpha = 0.85f), Offset(r.right - s - 2 * u, r.bottom - s - 2 * u), Size(s, s))
            drawRect(WASHI.copy(alpha = 0.7f), Offset(r.right - s * 0.75f - 2 * u, r.bottom - s * 0.75f - 2 * u), Size(s * 0.5f, s * 0.5f), style = Stroke(0.6f * u))
        }
        // El tirador: una placa de hierro de cuatro lóbulos (mokkō) con su asa de media luna.
        val rp = minOf(w, f.height) * 0.1f
        val cp = Offset(w / 2, h * 0.79f + baja)
        listOf(Offset(-1f, 0f), Offset(1f, 0f), Offset(0f, -1f), Offset(0f, 1f)).forEach { o -> drawCircle(HIERRO, rp * 0.62f, Offset(cp.x + o.x * rp * 0.5f, cp.y + o.y * rp * 0.42f)) }
        drawCircle(HIERRO_LUZ.copy(alpha = 0.6f), rp * 0.3f, cp, style = Stroke(0.7f * u))
        drawArc(HIERRO, 15f, 150f, false, Offset(cp.x - rp * 1.3f, cp.y - rp * 0.9f), Size(rp * 2.6f, rp * 2.2f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawArc(HIERRO_LUZ.copy(alpha = 0.5f), 40f, 60f, false, Offset(cp.x - rp * 1.3f, cp.y - rp * 0.9f), Size(rp * 2.6f, rp * 2.2f), style = Stroke(0.6f * u))
    }

    override val rotulo = Rect(0.16f, 0.17f, 0.84f, 0.6f)
    override fun tintaRotulo(oscuro: Boolean) = SUMI
    override val letraRotulo: FontFamily = LetraExtra.shippori
    override val salida = SALIDA
    override val placa = Rect(0.3f, 0.06f, 0.7f, 0.36f)
    override fun tintaPlaca(oscuro: Boolean) = SUMI

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ki = if (oscuro) mezcla(KIRI, Color.Black, 0.6f) else KIRI
        // El fondo del cajón: tablas de kiri a lo largo, de veta recta y fina.
        drawRect(ki)
        val tablas = 4
        for (k in 0 until tablas) {
            val r = Rect(w * k / tablas, 0f, w * (k + 1) / tablas, h)
            drawRect(mezcla(ki, if (k % 2 == 0) Color.White else KIRI_VETA, 0.06f), r.topLeft, r.size)
            vetas(r, KIRI_VETA.copy(alpha = if (oscuro) 0.25f else 0.4f), 31 + k, 1.4f, vertical = true, grosor = 0.7f)
            if (k > 0) drawLine(Color.Black.copy(alpha = 0.18f), Offset(r.left, 0f), Offset(r.left, h), 1f * u)
        }
        // Las paredes, vistas desde arriba: más gruesa la de enfrente (la del tirador), con su canto claro y su sombra adentro.
        val pl = 14 * u; val pa = 12 * u; val pf = 26 * u
        fun tabla(r: Rect, horizontal: Boolean) {
            drawRect(mezcla(ki, KIRI_VETA, 0.35f), r.topLeft, r.size)
            vetas(r, KIRI_VETA.copy(alpha = 0.5f), (r.left + r.top).toInt(), 1f, vertical = !horizontal, grosor = 0.6f)
        }
        tabla(Rect(0f, 0f, w, pa), true)
        tabla(Rect(0f, h - pf, w, h), true)
        drawRect(mezcla(kk(oscuro), Color.Black, 0.1f), Offset(0f, h - pf * 0.55f), Size(w, pf * 0.55f))
        vetas(Rect(0f, h - pf * 0.55f, w, h), KEYAKI_OSC.copy(alpha = 0.5f), 61, 1f)
        tabla(Rect(0f, 0f, pl, h), false)
        tabla(Rect(w - pl, 0f, w, h), false)
        // Los cantos (la luz sobre el borde de cada tabla) y la sombra que echan sobre el fondo.
        drawLine(Color.White.copy(alpha = 0.35f), Offset(0f, pa), Offset(w, pa), 1f * u)
        drawLine(Color.White.copy(alpha = 0.35f), Offset(pl, 0f), Offset(pl, h), 1f * u)
        drawLine(Color.White.copy(alpha = 0.2f), Offset(w - pl, 0f), Offset(w - pl, h), 1f * u)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), pa, pa + 22 * u), Offset(pl, pa), Size(w - 2 * pl, 22 * u))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), pl, pl + 16 * u), Offset(pl, pa), Size(16 * u, h - pa - pf))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.16f)), w - pl - 14 * u, w - pl), Offset(w - pl - 14 * u, pa), Size(14 * u, h - pa - pf))
        // Las colas de milano en las cuatro esquinas, donde se ensamblan las tablas.
        listOf(0f to 0f, w - pl to 0f, 0f to h - pf, w - pl to h - pf).forEach { (x, y) ->
            val alto = if (y == 0f) pa else pf
            for (k in 0 until 3) drawRect(Color.Black.copy(alpha = 0.16f), Offset(x + pl * 0.2f, y + alto * (0.15f + 0.3f * k)), Size(pl * 0.6f, alto * 0.14f))
        }
    }

    private fun kk(oscuro: Boolean) = madera(oscuro)

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) WASHI else SUMI
    override val pared: Dp = 16.dp
    override val columnas = 3
    override val lineas = 8

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.5.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 2f else 1f
        // El teñido en nube: añil arriba y violeta abajo, en manchas suaves que se funden con el papel.
        val nubeA = 12 * u * g; val nubeV = 9 * u * g
        for (k in 0 until 11) {
            val x = w * (k + azar(semilla, k)) / 10f
            mancha(Offset(x, nubeA * (0.05f + 0.35f * azar(semilla, k + 10))), nubeA * (0.8f + 0.5f * azar(semilla, k + 20)), ANIL.copy(alpha = 0.26f))
            mancha(Offset(x, h - nubeV * (0.05f + 0.35f * azar(semilla, k + 30))), nubeV * (0.8f + 0.5f * azar(semilla, k + 40)), VIOLETA.copy(alpha = 0.22f))
        }
        // Una franja fina de añil pegada al borde, donde el tinte cargó más.
        drawRect(Brush.verticalGradient(listOf(ANIL.copy(alpha = 0.35f), Color.Transparent), 0f, nubeA * 0.7f), Offset.Zero, Size(w, nubeA * 0.7f))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, VIOLETA.copy(alpha = 0.3f)), h - nubeV * 0.7f, h), Offset(0f, h - nubeV * 0.7f), Size(w, nubeV * 0.7f))
        // Hojuelas de oro (kirihaku), cuadraditos esparcidos.
        val n = if (grande) 46 else 16
        for (k in 0 until n) {
            val c = Offset(w * azar(semilla, k + 100), h * azar(semilla, k + 200))
            val s = (0.8f + 1.6f * azar(semilla, k + 300)) * u * (if (grande) 1.3f else 1f)
            rotate(azar(semilla, k + 400) * 90f, c) { drawRect(ORO.copy(alpha = 0.35f + 0.4f * azar(semilla, k + 500)), Offset(c.x - s / 2, c.y - s / 2), Size(s, s)) }
        }
        // El agujero y el cordón de papel torcido (koyori), atado arriba.
        val hoyo = Offset(w / 2, 8 * u * g + 4 * u)
        drawCircle(Color.Black.copy(alpha = 0.55f), 1.6f * u * g, hoyo)
        drawLine(BERMELLON, hoyo, Offset(w / 2 - 5 * u * g, 0f), 1.3f * u * g, StrokeCap.Round)
        drawLine(mezcla(BERMELLON, Color.White, 0.25f), hoyo, Offset(w / 2 + 5 * u * g, 0f), 1.3f * u * g, StrokeCap.Round)
        drawCircle(BERMELLON, 1.8f * u * g, Offset(w / 2, hoyo.y - 3 * u * g))
    }

    override val margenes = MargenesFicha(22.dp, 7.dp, 14.dp, 40.dp, 22.dp, 34.dp)
    override val proporcionFicha = 0.46f
    override val anchoGrande = 0.8f
    override val proporcionGrande = 0.42f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = true

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val u = density
        val s = size.minDimension; val c = center
        when (id) {
            // El sello: un cuadrado bermellón con su marco blanco adentro.
            "convence" -> { drawRect(BERMELLON, Offset(c.x - s * 0.42f, c.y - s * 0.42f), Size(s * 0.84f, s * 0.84f)); drawRect(WASHI, Offset(c.x - s * 0.24f, c.y - s * 0.24f), Size(s * 0.48f, s * 0.48f), style = Stroke(0.8f * u)) }
            // Un círculo añil con un punto: lo estás pensando.
            "piensa" -> { drawCircle(ANIL, s * 0.42f, c); drawCircle(WASHI, s * 0.12f, c) }
            // Un ensō abierto de tinta sumi: todavía no se cierra.
            "duda" -> drawArc(SUMI.copy(alpha = 0.8f), 40f, 290f, false, Offset(c.x - s * 0.38f, c.y - s * 0.38f), Size(s * 0.76f, s * 0.76f), style = Stroke(s * 0.14f, cap = StrokeCap.Round))
            // Dos trazos de pincel cruzados.
            "desacuerdo" -> { drawLine(SUMI, Offset(c.x - s * 0.36f, c.y - s * 0.36f), Offset(c.x + s * 0.36f, c.y + s * 0.36f), s * 0.16f, StrokeCap.Round); drawLine(BERMELLON, Offset(c.x + s * 0.36f, c.y - s * 0.36f), Offset(c.x - s * 0.36f, c.y + s * 0.36f), s * 0.16f, StrokeCap.Round) }
            else -> {}
        }
    }

    /** Un nudo de mizuhiki: dos lazos de cordón, bermellón y oro, con sus puntas. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        val g = s * 0.11f
        drawOval(BERMELLON, Offset(c.x - s * 0.48f, c.y - s * 0.26f), Size(s * 0.5f, s * 0.36f), style = Stroke(g))
        drawOval(ORO, Offset(c.x - s * 0.02f, c.y - s * 0.26f), Size(s * 0.5f, s * 0.36f), style = Stroke(g))
        drawLine(BERMELLON, Offset(c.x, c.y), Offset(c.x - s * 0.26f, c.y + s * 0.44f), g, StrokeCap.Round)
        drawLine(ORO, Offset(c.x, c.y), Offset(c.x + s * 0.26f, c.y + s * 0.44f), g, StrokeCap.Round)
    }

    override val desorden = 0f
}

/** Un rectángulo de [w] por [h] con el borde de abajo rasgado en [dientes] picos de hasta [hondo]. */
private fun rasgado(w: Float, h: Float, dientes: Int, hondo: Float, semilla: Int) = Path().apply {
    moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - hondo)
    for (k in dientes downTo 0) lineTo(w * k / dientes, h - hondo * (if (k % 2 == 0) 1f else 0.2f + 0.4f * azar(semilla, k)))
    close()
}

/**
 * Renacimiento: el studiolo. Un mueble de nogal con taracea de maderas claras: candelabros en las pilastras, un entablamento
 * con dentículos y, en el frontón, un mazzocchio en perspectiva dentro de su tondo; el zócalo es un trampantojo de cubos.
 * Cada tema es un cajoncito con su panel de peral filetado en damero, el tema en un cartellino de papel (la tira que los
 * pintores ponían con su firma) y un botón de bronce; los más llenos quedan entreabiertos con sus hojas. Por dentro, tablas
 * de peral con un compás y una regla. Las fichas son hojas de boceto con un estudio a la sanguina.
 */
object IdeasRenacimiento : DisenoIdeas {
    private val NOGAL = Color(0xFF5E3E22)
    private val NOGAL_LUZ = Color(0xFF7E5834)
    private val NOGAL_OSC = Color(0xFF3A2614)
    private val PERAL = Color(0xFFD9B98A)
    private val ARCE = Color(0xFFEAD7AE)
    private val EBANO = Color(0xFF2A1E14)
    private val BRONCE = Color(0xFFA88438)
    private val SANGUINA = Color(0xFFA8472A)
    private val PAPEL = Color(0xFFE9D8B2)
    private val CARTELLINO = Color(0xFFF4EAD0)
    private val TINTA = Color(0xFF3A2614)
    private val AZUL = Color(0xFF2F4F6F)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 1.3f
    override val corona = 0.26f
    override val base = 0.12f
    override val lado = 0.09f
    override val hueco = 0.025f

    private const val SALIDA = 0.16f

    /** Un mazzocchio: el anillo facetado de los estudios de perspectiva, en taracea de dos tonos. */
    private fun DrawScope.mazzocchio(c: Offset, r: Float, oscuro: Boolean) {
        val n = 16
        val caras = (0 until n).map { k ->
            val a0 = k * 2 * Math.PI / n; val a1 = (k + 1) * 2 * Math.PI / n
            fun ext(a: Double) = Offset(c.x + r * cos(a).toFloat(), c.y + r * 0.5f * sin(a).toFloat())
            fun int(a: Double) = Offset(c.x + r * 0.5f * cos(a).toFloat(), c.y - r * 0.08f + r * 0.2f * sin(a).toFloat())
            Triple(sin((a0 + a1) / 2), k, Path().apply { val p = ext(a0); moveTo(p.x, p.y); ext(a1).let { lineTo(it.x, it.y) }; int(a1).let { lineTo(it.x, it.y) }; int(a0).let { lineTo(it.x, it.y) }; close() })
        }.sortedBy { it.first }
        caras.forEach { (_, k, p) ->
            drawPath(p, t(if (k % 2 == 0) ARCE else NOGAL_OSC, oscuro))
            drawPath(p, t(EBANO, oscuro), style = Stroke(0.6f * density))
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo de nogal.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 4 * u, w * 0.98f, d.base.top + d.base.height * 0.2f)
        drawRect(t(NOGAL, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, NOGAL_OSC.copy(alpha = 0.4f), 17, 1f, grosor = 0.7f, ondulacion = 0.6f)
        // El rebaje de cada cajón, filetado de arce.
        d.huecos.forEach { r ->
            drawRect(Color.Black.copy(alpha = 0.5f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u))
            drawRect(t(ARCE, oscuro), Offset(r.left - 3 * u, r.top - 3 * u), Size(r.width + 6 * u, r.height + 6 * u), style = Stroke(0.8f * u))
        }
        // Las pilastras con su candelabro de taracea.
        listOf(cuerpo.left + w * 0.012f, cuerpo.right - w * 0.012f - w * 0.045f).forEach { x ->
            val r = Rect(x, cuerpo.top + 4 * u, x + w * 0.045f, cuerpo.bottom - 2 * u)
            drawRect(t(PERAL, oscuro), r.topLeft, r.size)
            drawRect(t(EBANO, oscuro), r.topLeft, r.size, style = Stroke(1f * u))
            drawLine(t(NOGAL_OSC, oscuro), Offset(r.center.x, r.top + 6 * u), Offset(r.center.x, r.bottom - 6 * u), 1.2f * u)
            var y = r.top + 12 * u
            while (y < r.bottom - 10 * u) {
                drawOval(t(NOGAL_OSC, oscuro), Offset(r.center.x - r.width * 0.42f, y - 2 * u), Size(r.width * 0.38f, 4 * u))
                drawOval(t(NOGAL_OSC, oscuro), Offset(r.center.x + r.width * 0.04f, y - 2 * u), Size(r.width * 0.38f, 4 * u))
                y += 16 * u
            }
            drawRect(t(NOGAL_LUZ, oscuro), Offset(r.left - 2 * u, r.top - 4 * u), Size(r.width + 4 * u, 5 * u))
        }
        // El entablamento: arquitrabe, friso con roleo de taracea y cornisa con dentículos.
        val arq = Rect(0f, d.corona.bottom - ch * 0.16f, w, d.corona.bottom)
        drawRect(t(NOGAL_LUZ, oscuro), arq.topLeft, arq.size)
        drawLine(t(ARCE, oscuro), Offset(0f, arq.center.y), Offset(w, arq.center.y), 0.8f * u)
        val friso = Rect(0f, arq.top - ch * 0.2f, w, arq.top)
        drawRect(t(PERAL, oscuro), friso.topLeft, friso.size)
        val roleo = Path()
        var x = 0f
        roleo.moveTo(0f, friso.center.y)
        while (x < w) { roleo.quadraticTo(x + 10 * u, friso.top + friso.height * 0.15f, x + 20 * u, friso.center.y); roleo.quadraticTo(x + 30 * u, friso.bottom - friso.height * 0.15f, x + 40 * u, friso.center.y); x += 40 * u }
        drawPath(roleo, t(NOGAL_OSC, oscuro), style = Stroke(1.2f * u))
        x = 10 * u
        while (x < w) { drawCircle(t(NOGAL_OSC, oscuro), 1.6f * u, Offset(x, friso.top + friso.height * 0.3f)); drawCircle(t(NOGAL_OSC, oscuro), 1.6f * u, Offset(x + 20 * u, friso.bottom - friso.height * 0.3f)); x += 40 * u }
        val cornisa = Rect(0f, friso.top - ch * 0.14f, w, friso.top)
        drawRect(t(NOGAL_OSC, oscuro), cornisa.topLeft, cornisa.size)
        x = 2 * u
        while (x < w) { drawRect(t(NOGAL_LUZ, oscuro), Offset(x, cornisa.top + cornisa.height * 0.4f), Size(4 * u, cornisa.height * 0.6f)); x += 7 * u }
        drawRect(t(NOGAL_LUZ, oscuro), Offset(0f, cornisa.top), Size(w, cornisa.height * 0.3f))
        // El frontón curvo con el tondo y su mazzocchio.
        val fr = Rect(w * 0.22f, d.corona.top + 2 * u, w * 0.78f, cornisa.top + (cornisa.top - d.corona.top))
        drawArc(t(NOGAL, oscuro), 180f, 180f, true, fr.topLeft, fr.size)
        drawArc(t(NOGAL_OSC, oscuro), 180f, 180f, false, fr.topLeft, fr.size, style = Stroke(2.5f * u))
        val tc = Offset(w / 2, cornisa.top - (cornisa.top - d.corona.top) * 0.42f)
        val tr = (cornisa.top - d.corona.top) * 0.36f
        drawCircle(t(PERAL, oscuro), tr, tc)
        drawCircle(t(EBANO, oscuro), tr, tc, style = Stroke(1.6f * u))
        mazzocchio(Offset(tc.x, tc.y + tr * 0.06f), tr * 0.78f, oscuro)
        // El zócalo: un trampantojo de cubos en tres maderas.
        val zoc = Rect(0f, cuerpo.bottom, w, d.base.bottom)
        drawRect(t(EBANO, oscuro), zoc.topLeft, zoc.size)
        clipRect(zoc.left, zoc.top, zoc.right, zoc.bottom) {
            val s = zoc.height * 0.36f
            var fila = 0
            var cy = zoc.top
            while (cy < zoc.bottom + s) {
                var cx = if (fila % 2 == 0) 0f else s * 0.866f
                while (cx < w + s) {
                    drawPath(Path().apply { moveTo(cx, cy); lineTo(cx + 0.866f * s, cy - 0.5f * s); lineTo(cx, cy - s); lineTo(cx - 0.866f * s, cy - 0.5f * s); close() }, t(ARCE, oscuro))
                    drawPath(Path().apply { moveTo(cx, cy); lineTo(cx - 0.866f * s, cy - 0.5f * s); lineTo(cx - 0.866f * s, cy + 0.5f * s); lineTo(cx, cy + s); close() }, t(PERAL, oscuro))
                    drawPath(Path().apply { moveTo(cx, cy); lineTo(cx + 0.866f * s, cy - 0.5f * s); lineTo(cx + 0.866f * s, cy + 0.5f * s); lineTo(cx, cy + s); close() }, t(NOGAL_OSC, oscuro))
                    cx += 1.732f * s
                }
                cy += 1.5f * s; fila++
            }
        }
        drawRect(t(NOGAL_LUZ, oscuro), Offset(0f, zoc.top), Size(w, 3 * u))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(CARTELLINO, oscuro), r.topLeft, r.size)
            drawPath(Path().apply { moveTo(r.right - 6 * u, r.bottom); lineTo(r.right, r.bottom - 6 * u); lineTo(r.right, r.bottom); close() }, Color.Black.copy(alpha = 0.3f))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Los cajoncitos más llenos quedan entreabiertos y asoman las hojas.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF120B06), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 6)
            for (i in 0 until k) {
                val x = w * (0.08f + 0.84f * (i + 0.5f) / k) - w * 0.07f
                val y0 = arriba * (0.1f + 0.5f * azar(c.semilla, i)) + 1 * u
                drawRect(t(PAPEL, oscuro), Offset(x, y0), Size(w * 0.14f, arriba + 4 * u - y0))
                drawLine(t(SANGUINA, oscuro).copy(alpha = 0.6f), Offset(x + 2 * u, y0 + 3 * u), Offset(x + w * 0.1f, y0 + 5 * u), 0.8f * u)
            }
        }
        // El frente: un marco de nogal y un panel de peral filetado en damero.
        val e = w * 0.03f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(t(NOGAL, oscuro), f.topLeft, f.size)
        vetas(f, NOGAL_OSC.copy(alpha = 0.4f), c.semilla, 1.2f, grosor = 0.6f, ondulacion = 0.6f)
        bisel(f, Color.White.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.35f), 1.2f * u)
        val fh = f.bottom - f.top
        val panel = Rect(f.left + w * 0.07f, f.top + fh * 0.11f, f.right - w * 0.07f, f.bottom - fh * 0.11f)
        drawRect(t(PERAL, oscuro), panel.topLeft, panel.size)
        val q = 2.4f * u
        var px = panel.left; var k = 0
        while (px < panel.right - q) {
            drawRect(t(if (k % 2 == 0) EBANO else ARCE, oscuro), Offset(px, panel.top), Size(q, q))
            drawRect(t(if (k % 2 == 1) EBANO else ARCE, oscuro), Offset(px, panel.bottom - q), Size(q, q))
            px += q; k++
        }
        var py = panel.top + q; k = 0
        while (py < panel.bottom - 2 * q) {
            drawRect(t(if (k % 2 == 0) EBANO else ARCE, oscuro), Offset(panel.left, py), Size(q, q))
            drawRect(t(if (k % 2 == 1) EBANO else ARCE, oscuro), Offset(panel.right - q, py), Size(q, q))
            py += q; k++
        }
        // El botón de bronce.
        val bc = Offset(w / 2, panel.bottom - fh * 0.12f)
        val br = minOf(w, h) * 0.055f
        if (c.vacio) {
            // Un lugar libre: un rombo de taracea en el panel.
            drawPath(estrella(Offset(w / 2, panel.center.y - fh * 0.04f), panel.height * 0.26f, 2, 1f, 0f), t(NOGAL_OSC, oscuro))
            drawCircle(t(BRONCE, oscuro), br, bc)
            return
        }
        // El cartellino con el tema, sujeto con dos gotas de cera y con una esquina levantada.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.5f * u, r.top + 2 * u), r.size)
        drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 6 * u); lineTo(r.right - 6 * u, r.bottom); lineTo(r.left, r.bottom); close() }, t(CARTELLINO, oscuro))
        drawPath(Path().apply { moveTo(r.right, r.bottom - 6 * u); lineTo(r.right - 6 * u, r.bottom); lineTo(r.right - 7 * u, r.bottom - 7 * u); close() }, t(mezcla(CARTELLINO, NOGAL, 0.3f), oscuro))
        drawCircle(t(SANGUINA, oscuro), 1.6f * u, Offset(r.left + 4 * u, r.top + 3.5f * u))
        drawCircle(t(SANGUINA, oscuro), 1.6f * u, Offset(r.right - 4 * u, r.top + 3.5f * u))
        drawCircle(Color.Black.copy(alpha = 0.3f), br, Offset(bc.x + 1 * u, bc.y + 1.5f * u))
        drawCircle(t(BRONCE, oscuro), br, bc)
        drawCircle(Color.White.copy(alpha = 0.35f), br * 0.35f, Offset(bc.x - br * 0.3f, bc.y - br * 0.3f))
    }

    override val rotulo = Rect(0.17f, 0.2f, 0.83f, 0.66f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.cormorant
    override val salida = SALIDA
    override val placa = Rect(0.32f, 0.3f, 0.68f, 0.85f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Tablas de peral, y sobre ellas un compás y una regla.
        val pe = if (oscuro) mezcla(PERAL, Color.Black, 0.55f) else PERAL
        drawRect(pe)
        val tablas = 4
        for (k in 0 until tablas) {
            val r = Rect(w * k / tablas, 0f, w * (k + 1) / tablas, h)
            vetas(r, NOGAL.copy(alpha = 0.18f), 91 + k, 1.2f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
            if (k > 0) drawLine(Color.Black.copy(alpha = 0.2f), Offset(r.left, 0f), Offset(r.left, h), 1f * u)
        }
        val bisagra = Offset(w * 0.78f, h * 0.8f)
        drawLine(t(BRONCE, oscuro), bisagra, Offset(bisagra.x - 40 * u, bisagra.y + 110 * u), 2.4f * u, StrokeCap.Round)
        drawLine(t(BRONCE, oscuro), bisagra, Offset(bisagra.x + 10 * u, bisagra.y + 116 * u), 2.4f * u, StrokeCap.Round)
        drawCircle(t(BRONCE, oscuro), 5 * u, bisagra)
        rotate(-12f, Offset(w * 0.3f, h * 0.88f)) {
            drawRect(t(NOGAL_LUZ, oscuro), Offset(w * 0.12f, h * 0.88f), Size(w * 0.42f, 12 * u))
            var rx = w * 0.12f + 6 * u
            while (rx < w * 0.54f) { drawLine(t(EBANO, oscuro), Offset(rx, h * 0.88f), Offset(rx, h * 0.88f + 5 * u), 0.8f * u); rx += 8 * u }
        }
        // Las paredes de nogal con su filete de ébano y arce.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(NOGAL, oscuro), r.topLeft, r.size) }
        drawRect(t(ARCE, oscuro), Offset(pl * 0.4f, pl * 0.4f), Size(w - pl * 0.8f, h - pl * 0.8f), style = Stroke(1.2f * u))
        drawRect(t(EBANO, oscuro), Offset(pl * 0.6f, pl * 0.6f), Size(w - pl * 1.2f, h - pl * 1.2f), style = Stroke(1f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) CARTELLINO else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 26, 0.006f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(PAPEL.copy(alpha = 0.5f))
        // Un estudio a la sanguina, tenue: un círculo inscrito en un cuadrado con sus diagonales, abajo a la derecha.
        val l = minOf(w, h) * 0.5f
        val o = Offset(w - l * 0.8f, h - l * 0.85f)
        val trazo = SANGUINA.copy(alpha = 0.16f)
        drawRect(trazo, o, Size(l, l), style = Stroke(1f * u))
        drawCircle(trazo, l / 2, Offset(o.x + l / 2, o.y + l / 2), style = Stroke(1f * u))
        drawLine(trazo, o, Offset(o.x + l, o.y + l), 0.8f * u)
        drawLine(trazo, Offset(o.x + l, o.y), Offset(o.x, o.y + l), 0.8f * u)
        // Unos sombreados en diagonal, arriba a la derecha.
        for (k in 0 until 7) {
            val x = w * 0.62f + k * 4 * u
            drawLine(SANGUINA.copy(alpha = 0.12f), Offset(x, h * 0.04f + 10 * u), Offset(x + 10 * u, h * 0.04f), 0.9f * u)
        }
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 12.dp, 24.dp, 28.dp, 28.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y); lineTo(c.x - s * 0.08f, c.y + s * 0.3f); lineTo(c.x + s * 0.4f, c.y - s * 0.36f) }, SANGUINA, style = Stroke(s * 0.13f, cap = StrokeCap.Round))
            // Un compás de puntas.
            "piensa" -> {
                drawLine(AZUL, Offset(c.x, c.y - s * 0.36f), Offset(c.x - s * 0.26f, c.y + s * 0.42f), s * 0.09f, StrokeCap.Round)
                drawLine(AZUL, Offset(c.x, c.y - s * 0.36f), Offset(c.x + s * 0.26f, c.y + s * 0.42f), s * 0.09f, StrokeCap.Round)
                drawCircle(AZUL, s * 0.1f, Offset(c.x, c.y - s * 0.36f))
            }
            "duda" -> circuloDuda(p.tenue)
            // Una tachadura de sombreado cruzado.
            "desacuerdo" -> for (k in -2..2) {
                drawLine(SANGUINA, Offset(c.x - s * 0.36f + k * s * 0.12f, c.y + s * 0.36f), Offset(c.x + s * 0.12f + k * s * 0.12f, c.y - s * 0.36f), s * 0.06f)
                drawLine(SANGUINA, Offset(c.x - s * 0.12f + k * s * 0.12f, c.y - s * 0.36f), Offset(c.x + s * 0.36f + k * s * 0.12f, c.y + s * 0.36f), s * 0.06f)
            }
            else -> {}
        }
    }

    /** Un alfiler de cabeza de bronce. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawLine(Color(0xFF8C8C8C), Offset(c.x - s * 0.04f, c.y - s * 0.1f), Offset(c.x + s * 0.3f, c.y + s * 0.44f), s * 0.06f, StrokeCap.Round)
        drawCircle(BRONCE, s * 0.24f, Offset(c.x - s * 0.1f, c.y - s * 0.2f))
        drawCircle(Color.White.copy(alpha = 0.4f), s * 0.07f, Offset(c.x - s * 0.16f, c.y - s * 0.27f))
    }

    override val desorden = 1f
}

/**
 * Pirata: la bodega del barco. Tablazón curva y cuadernas, los baos de cubierta con un farol encendido y una adujada de
 * cabo colgada; abajo, el piso y dos barriles. Cada tema es un cofre de marino pintado de verde, más ancho abajo, con
 * cantoneras de latón, gazas de cabo a los lados y un candado; el tema va escrito con tiza en una pizarrita clavada al
 * frente, y asoman cartas y monedas bajo la tapa. Las filas de cofres van trincadas con un cabo. Por dentro, pino
 * embreado con un barco pintado en la tapa. Las fichas son cartas de marear con sus rumbos.
 */
object IdeasPirata : DisenoIdeas {
    private val PINO = Color(0xFF7A5634)
    private val PINO_LUZ = Color(0xFF9C7448)
    private val PINO_OSC = Color(0xFF4A321C)
    private val CUADERNA = Color(0xFF3A2614)
    private val BREA = Color(0xFF1E1610)
    private val CUERDA = Color(0xFFC9A86A)
    private val CUERDA_OSC = Color(0xFF8E7040)
    private val LATON = Color(0xFFC09A48)
    private val VERDE = Color(0xFF2E4A3A)
    private val VERDE_OSC = Color(0xFF1C3026)
    private val PIZARRA = Color(0xFF2C3033)
    private val TIZA = Color(0xFFEDEAE0)
    private val PERGAMINO = Color(0xFFE6D6AE)
    private val ROJO = Color(0xFF9A2A1C)
    private val RUMBO = Color(0xFF2E6A4A)
    private val TINTA = Color(0xFF2A1C0F)
    private val LUZ = Color(0xFFF2C46B)
    private val MONEDA = Color(0xFFE0B848)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.45f
    override val corona = 0.27f
    override val base = 0.13f
    override val lado = 0.075f
    override val hueco = 0.07f

    /** Una adujada: un cabo enrollado en vueltas, visto de frente. */
    private fun DrawScope.adujada(c: Offset, r: Float, oscuro: Boolean) {
        val u = density
        for (k in 0 until 5) {
            val rr = r * (1f - k * 0.14f)
            drawOval(t(CUERDA_OSC, oscuro), Offset(c.x - rr, c.y - rr * 0.55f), Size(rr * 2, rr * 1.1f), style = Stroke(3.2f * u))
            drawOval(t(CUERDA, oscuro), Offset(c.x - rr, c.y - rr * 0.55f), Size(rr * 2, rr * 1.1f), style = Stroke(2 * u))
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.35f
        // La tablazón del casco: tablas a lo largo, calafateadas con brea y con sus cabillas.
        val alto = 18 * u
        var y = d.corona.bottom - alto; var k = 0
        while (y < piso) {
            drawRect(t(mezcla(PINO_OSC, PINO, 0.4f + 0.5f * azar(7, k)), oscuro), Offset(0f, y), Size(w, alto))
            drawLine(t(BREA, oscuro), Offset(0f, y), Offset(w, y), 1.6f * u)
            var cx = (k % 3) * 40 * u + 20 * u
            while (cx < w) { drawCircle(t(BREA, oscuro).copy(alpha = 0.6f), 1.2f * u, Offset(cx, y + alto / 2)); cx += 120 * u }
            y += alto; k++
        }
        // Las cuadernas: costillas curvas a los lados y al centro.
        listOf(w * 0.01f, w * 0.5f - w * 0.025f, w * 0.99f - w * 0.05f).forEach { x ->
            val cw = w * 0.05f
            val cuad = Path().apply { moveTo(x, d.corona.bottom); lineTo(x + cw, d.corona.bottom); quadraticTo(x + cw * 1.3f, (d.corona.bottom + piso) / 2, x + cw, piso); lineTo(x, piso); quadraticTo(x + cw * 0.3f, (d.corona.bottom + piso) / 2, x, d.corona.bottom); close() }
            drawPath(cuad, t(CUADERNA, oscuro))
            drawLine(Color.White.copy(alpha = 0.1f), Offset(x + cw * 0.3f, d.corona.bottom), Offset(x + cw * 0.3f, piso), 1f * u)
        }
        // Las filas de cofres sobre su tablón, trincadas con un cabo de cuaderna a cuaderna.
        d.filas.forEach { f ->
            val r = Rect(0f, f.bottom - 1 * u, w, f.bottom + maxOf(d.separacion * 0.45f, 7 * u))
            drawRect(t(PINO_LUZ, oscuro), r.topLeft, r.size)
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, r.bottom), Size(w, 2.5f * u))
        }
        // Los baos de cubierta, con sus curvas, el farol encendido y una adujada colgada.
        drawRect(t(PINO_OSC, oscuro), d.corona.topLeft, Size(w, ch))
        var px = 0f
        while (px < w) { drawLine(Color.Black.copy(alpha = 0.4f), Offset(px, d.corona.top), Offset(px, d.corona.top + ch * 0.45f), 1f * u); px += 22 * u }
        val bao = Rect(0f, d.corona.top + ch * 0.45f, w, d.corona.top + ch * 0.72f)
        drawRect(t(CUADERNA, oscuro), bao.topLeft, bao.size)
        drawLine(Color.White.copy(alpha = 0.12f), Offset(0f, bao.top + 1.5f * u), Offset(w, bao.top + 1.5f * u), 1.2f * u)
        listOf(w * 0.035f, w * 0.5f, w * 0.965f).forEach { x ->
            drawPath(Path().apply { moveTo(x - 10 * u, bao.bottom); lineTo(x + 10 * u, bao.bottom); lineTo(x, d.corona.bottom + 4 * u); close() }, t(CUADERNA, oscuro))
        }
        val farol = Offset(w * 0.72f, bao.bottom + ch * 0.2f)
        mancha(farol, ch * (if (oscuro) 1.4f else 1f), LUZ.copy(alpha = if (oscuro) 0.38f else 0.25f))
        drawLine(t(LATON, oscuro), Offset(farol.x, bao.bottom), Offset(farol.x, farol.y - ch * 0.12f), 1.2f * u)
        drawRect(LUZ.copy(alpha = 0.85f), Offset(farol.x - ch * 0.07f, farol.y - ch * 0.1f), Size(ch * 0.14f, ch * 0.2f))
        drawRect(t(LATON, oscuro), Offset(farol.x - ch * 0.07f, farol.y - ch * 0.1f), Size(ch * 0.14f, ch * 0.2f), style = Stroke(1.6f * u))
        drawLine(t(LATON, oscuro), Offset(farol.x, farol.y - ch * 0.1f), Offset(farol.x, farol.y + ch * 0.1f), 1f * u)
        drawPath(Path().apply { moveTo(farol.x - ch * 0.09f, farol.y - ch * 0.1f); lineTo(farol.x, farol.y - ch * 0.16f); lineTo(farol.x + ch * 0.09f, farol.y - ch * 0.1f); close() }, t(LATON, oscuro))
        drawOval(Color(0xFFFFF0C0), Offset(farol.x - 2 * u, farol.y - 3 * u), Size(4 * u, 7 * u))
        drawLine(t(PINO_LUZ, oscuro), Offset(w * 0.22f, bao.bottom), Offset(w * 0.22f, bao.bottom + 6 * u), 3 * u)
        adujada(Offset(w * 0.22f, bao.bottom + ch * 0.18f), ch * 0.15f, oscuro)
        // El piso, y un barril a cada lado.
        drawRect(t(PINO_OSC, oscuro), Offset(0f, piso), Size(w, d.base.bottom - piso))
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, piso), Offset(w, piso), 2 * u)
        listOf(w * 0.01f, w * 0.87f).forEach { x ->
            val b = Rect(x, d.base.top + d.base.height * 0.05f, x + w * 0.12f, d.base.bottom - 2 * u)
            drawRoundRect(t(PINO, oscuro), b.topLeft, b.size, CornerRadius(b.width * 0.3f, b.height * 0.12f))
            for (j in 1..3) drawLine(Color.Black.copy(alpha = 0.3f), Offset(b.left + b.width * j / 4, b.top + 2 * u), Offset(b.left + b.width * j / 4, b.bottom - 2 * u), 0.8f * u)
            listOf(0.2f, 0.8f).forEach { f -> drawRect(t(BREA, oscuro), Offset(b.left, b.top + b.height * f - 1.5f * u), Size(b.width, 3 * u)) }
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PINO_LUZ, oscuro), Offset(r.left - 3 * u, r.top - 3 * u), Size(r.width + 6 * u, r.height + 6 * u))
            drawRect(t(PIZARRA, oscuro), r.topLeft, r.size)
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // El cabo que trinca la fila, por delante del pie de los cofres.
        val trinca = h * 0.86f
        if (c.vacio) {
            // Un lugar libre: una adujada de cabo sobre el tablón.
            adujada(Offset(w / 2, h * 0.82f), w * 0.2f, oscuro)
            drawLine(t(CUERDA_OSC, oscuro), Offset(0f, trinca), Offset(w, trinca), 2.4f * u)
            return
        }
        val top = h * 0.4f; val bot = h * 0.95f
        val cuerpo = Path().apply { moveTo(w * 0.12f, top); lineTo(w * 0.88f, top); lineTo(w * 0.92f, bot); lineTo(w * 0.08f, bot); close() }
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.18f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(w * 0.08f, bot - h * 0.03f), Size(w * 0.84f, h * 0.06f))
        // Las cartas enrolladas y las monedas que asoman bajo la tapa.
        if (sube > 1f) {
            drawRect(Color(0xFF0E0A06), Offset(w * 0.14f, top - sube), Size(w * 0.72f, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = w * (0.16f + 0.11f * i)
                val y = top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                if (i % 3 == 2) drawCircle(MONEDA, 3 * u, Offset(x + w * 0.04f, top - 3 * u))
                else drawRect(t(PERGAMINO, oscuro), Offset(x, y), Size(w * 0.09f, top - y + 2 * u))
            }
        }
        // El cofre verde, con sus tablas.
        drawPath(cuerpo, t(VERDE, oscuro))
        clipPath(cuerpo) { for (j in 1 until 4) drawLine(Color.Black.copy(alpha = 0.35f), Offset(0f, top + (bot - top) * j / 4), Offset(w, top + (bot - top) * j / 4), 1f * u) }
        // Las gazas de cabo de los costados.
        drawArc(t(CUERDA, oscuro), 90f, 180f, false, Offset(w * 0.02f, h * 0.52f), Size(w * 0.1f, h * 0.16f), style = Stroke(2.6f * u))
        drawArc(t(CUERDA, oscuro), -90f, 180f, false, Offset(w * 0.88f, h * 0.52f), Size(w * 0.1f, h * 0.16f), style = Stroke(2.6f * u))
        // Las cantoneras de latón.
        listOf(Offset(w * 0.12f, top) to 1f, Offset(w * 0.88f, top) to -1f).forEach { (o, s) ->
            drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + s * 10 * u, o.y); lineTo(o.x + s * 0.5f * u, o.y + 10 * u); close() }, t(LATON, oscuro))
        }
        listOf(Offset(w * 0.08f, bot) to 1f, Offset(w * 0.92f, bot) to -1f).forEach { (o, s) ->
            drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + s * 10 * u, o.y); lineTo(o.x - s * 1.4f * u, o.y - 10 * u); close() }, t(LATON, oscuro))
        }
        // La tapa, con su ribete de latón y el candado.
        val tapa = Rect(w * 0.09f, top - sube - h * 0.12f, w * 0.91f, top - sube + 1 * u)
        drawRect(t(VERDE_OSC, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(LATON, oscuro), Offset(tapa.left, tapa.bottom - 2.5f * u), Size(tapa.width, 2.5f * u))
        drawRect(t(LATON, oscuro), Offset(w / 2 - 3 * u, tapa.bottom - 3 * u), Size(6 * u, h * 0.06f))
        val cand = Offset(w / 2, top + h * 0.06f)
        drawArc(t(LATON, oscuro), 180f, 180f, false, Offset(cand.x - 3.5f * u, cand.y - 7 * u), Size(7 * u, 7 * u), style = Stroke(1.4f * u))
        drawRoundRect(t(LATON, oscuro), Offset(cand.x - 4.5f * u, cand.y - 3.5f * u), Size(9 * u, 7 * u), CornerRadius(1.5f * u))
        // La pizarrita clavada, con su marco de pino.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(PINO_LUZ, oscuro), Offset(r.left - 3 * u, r.top - 3 * u), Size(r.width + 6 * u, r.height + 6 * u))
        drawRect(PIZARRA, r.topLeft, r.size)
        for (j in 0 until 3) drawLine(Color.White.copy(alpha = 0.06f), Offset(r.left + r.width * azar(c.semilla, j + 70), r.top + 2 * u), Offset(r.left + r.width * azar(c.semilla, j + 80), r.bottom - 2 * u), 4 * u)
        listOf(Offset(r.left - 1.5f * u, r.top - 1.5f * u), Offset(r.right + 1.5f * u, r.top - 1.5f * u)).forEach { drawCircle(t(BREA, oscuro), 1.3f * u, it) }
        // El cabo de la trinca.
        drawLine(t(CUERDA_OSC, oscuro), Offset(0f, trinca), Offset(w, trinca), 2.8f * u)
        drawLine(t(CUERDA, oscuro), Offset(0f, trinca - 0.5f * u), Offset(w, trinca - 0.5f * u), 1.4f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u)))
    }

    override val rotulo = Rect(0.24f, 0.48f, 0.76f, 0.78f)
    override fun tintaRotulo(oscuro: Boolean) = TIZA
    override val letraRotulo: FontFamily = LetraExtra.caslon
    override val salida = 0f
    override val placa = Rect(0.32f, 0.38f, 0.68f, 0.88f)
    override fun tintaPlaca(oscuro: Boolean) = TIZA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Pino embreado, de tablas verticales.
        drawRect(t(PINO_OSC, oscuro))
        val n = 6
        for (k in 0 until n) {
            val r = Rect(w * k / n, 0f, w * (k + 1) / n, h)
            drawRect(t(mezcla(PINO_OSC, PINO, 0.2f + 0.4f * azar(17, k)), oscuro), r.topLeft, r.size)
            vetas(r, Color.Black.copy(alpha = 0.3f), 30 + k, 0.8f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
            drawLine(t(BREA, oscuro), Offset(r.left, 0f), Offset(r.left, h), 1.6f * u)
        }
        // Un barco pintado en la tapa, desvaído.
        val b = Offset(w / 2, h * 0.2f)
        val pintura = TIZA.copy(alpha = 0.16f)
        drawPath(Path().apply { moveTo(b.x - 80 * u, b.y); lineTo(b.x + 90 * u, b.y); lineTo(b.x + 70 * u, b.y + 22 * u); lineTo(b.x - 66 * u, b.y + 22 * u); close() }, pintura)
        listOf(-40f, 10f, 55f).forEachIndexed { i, dx ->
            val alto = (if (i == 1) 110f else 86f) * u
            drawLine(pintura, Offset(b.x + dx * u, b.y), Offset(b.x + dx * u, b.y - alto), 2 * u)
            for (j in 0 until 3) drawRect(pintura, Offset(b.x + dx * u - 22 * u + j * 3 * u, b.y - alto + 10 * u + j * 30 * u), Size(44 * u - j * 6 * u, 24 * u))
        }
        var ox = 0f
        while (ox < w) { drawArc(pintura, 0f, 180f, false, Offset(ox, b.y + 26 * u), Size(24 * u, 10 * u), style = Stroke(1.6f * u)); ox += 24 * u }
        // Las paredes del cofre, con sus cantoneras.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(VERDE_OSC, oscuro), r.topLeft, r.size) }
        listOf(Offset(0f, 0f), Offset(w - 30 * u, 0f), Offset(0f, h - 30 * u), Offset(w - 30 * u, h - 30 * u)).forEach { o -> drawRect(t(LATON, oscuro), o, Size(30 * u, 30 * u), style = Stroke(4 * u)) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = PERGAMINO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 30, 0.008f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PERGAMINO.copy(alpha = 0.45f))
        // Los rumbos: dieciséis líneas desde un centro, en negro, verde y rojo, como en un portulano.
        val c = Offset(w * (0.5f + 0.25f * azar(semilla, 1)), h * (0.45f + 0.25f * azar(semilla, 2)))
        val l = maxOf(w, h) * 1.5f
        for (k in 0 until 16) {
            val a = k * Math.PI / 8
            val col = when (k % 4) { 0 -> TINTA; 2 -> RUMBO; else -> ROJO }
            drawLine(col.copy(alpha = 0.13f), c, Offset(c.x + l * cos(a).toFloat(), c.y + l * sin(a).toFloat()), 0.8f * u)
        }
        drawPath(estrella(c, 9 * u * g, 8, 0.35f), TINTA.copy(alpha = 0.3f))
        drawCircle(TINTA.copy(alpha = 0.25f), 6 * u * g, c, style = Stroke(0.6f * u))
        // Un trozo de costa por la izquierda, con sus nombres en rayitas.
        val costa = Path().apply {
            moveTo(0f, h * 0.15f)
            var y = h * 0.15f; var k = 0
            while (y < h * 0.9f) { y += 10 * u * g; lineTo(w * (0.04f + 0.1f * azar(semilla, k + 10)), y); k++ }
            lineTo(0f, h * 0.9f)
        }
        drawPath(costa, Color(0xFF8A6A3A).copy(alpha = 0.3f), style = Stroke(1.2f * u))
        for (k in 0 until 5) {
            val y = h * (0.22f + 0.14f * k)
            drawLine(ROJO.copy(alpha = 0.25f), Offset(w * 0.12f, y), Offset(w * (0.18f + 0.06f * azar(semilla, k + 30)), y - 3 * u), 0.8f * u)
        }
    }

    override val margenes = MargenesFicha(13.dp, 14.dp, 13.dp, 26.dp, 28.dp, 28.dp)
    override val proporcionFicha = 1.05f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un ancla.
            "convence" -> {
                drawCircle(TINTA, s * 0.1f, Offset(c.x, c.y - s * 0.36f), style = Stroke(s * 0.07f))
                drawLine(TINTA, Offset(c.x, c.y - s * 0.26f), Offset(c.x, c.y + s * 0.4f), s * 0.09f)
                drawLine(TINTA, Offset(c.x - s * 0.22f, c.y - s * 0.16f), Offset(c.x + s * 0.22f, c.y - s * 0.16f), s * 0.08f)
                drawArc(TINTA, 20f, 140f, false, Offset(c.x - s * 0.36f, c.y - s * 0.1f), Size(s * 0.72f, s * 0.52f), style = Stroke(s * 0.09f, cap = StrokeCap.Round))
            }
            // Una brújula con la aguja al norte.
            "piensa" -> {
                drawCircle(TINTA, s * 0.38f, c, style = Stroke(s * 0.07f))
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.3f); lineTo(c.x + s * 0.09f, c.y); lineTo(c.x - s * 0.09f, c.y); close() }, ROJO)
                drawPath(Path().apply { moveTo(c.x, c.y + s * 0.3f); lineTo(c.x + s * 0.09f, c.y); lineTo(c.x - s * 0.09f, c.y); close() }, TINTA)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un puñal clavado. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.07f, c.y - s * 0.05f); lineTo(c.x + s * 0.07f, c.y - s * 0.05f); lineTo(c.x, c.y + s * 0.5f); close() }, Color(0xFFB8BCC0))
        drawLine(LATON, Offset(c.x - s * 0.24f, c.y - s * 0.08f), Offset(c.x + s * 0.24f, c.y - s * 0.08f), s * 0.09f, StrokeCap.Round)
        drawLine(PINO_OSC, Offset(c.x, c.y - s * 0.12f), Offset(c.x, c.y - s * 0.42f), s * 0.12f, StrokeCap.Round)
        drawCircle(LATON, s * 0.07f, Offset(c.x, c.y - s * 0.46f))
    }

    override val desorden = 2f
}

/**
 * Revolución: el estante de la sección. Una pared de yeso con decretos pegados, estantes de pino con escuadras de hierro y,
 * arriba, colgaduras tricolores con escarapelas; el piso es de baldosas hexagonales de barro (tomettes). Cada tema es una
 * caja de pino de listones, atada con una cinta tricolor, con los cuadernos de quejas asomando por arriba y el tema en un
 * cartel impreso pegado al frente. Por dentro, listones forrados de papel de periódico. Las fichas son pasquines: papel de
 * imprenta con su doble filete y el borde de abajo rasgado.
 */
object IdeasRevolucion : DisenoIdeas {
    private val PINO = Color(0xFFB08A5A)
    private val PINO_LUZ = Color(0xFFCBA676)
    private val PINO_OSC = Color(0xFF7A5A36)
    private val AZUL = Color(0xFF1F3A6E)
    private val ROJO = Color(0xFFB22234)
    private val BLANCO = Color(0xFFF4F0E6)
    private val PAPEL = Color(0xFFEFE8D6)
    private val NEGRO = Color(0xFF1C1A17)
    private val YESO = Color(0xFFE3D9C2)
    private val YESO_OSC = Color(0xFFC9BC9E)
    private val TOMETTE = Color(0xFFA8442A)
    private val TOMETTE_OSC = Color(0xFF6E2A18)
    private val HIERRO = Color(0xFF2A2826)
    private val CUADERNOS = listOf(Color(0xFF8C9AB0), Color(0xFFD8B87A), Color(0xFFD9A6A0), Color(0xFFE8E0CC), Color(0xFF9CB094))

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.4f
    override val corona = 0.25f
    override val base = 0.11f
    override val lado = 0.075f
    override val hueco = 0.06f

    /** Una escarapela: azul afuera, blanco y rojo al centro, con su plisado. */
    private fun DrawScope.escarapela(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(t(AZUL, oscuro), r, c)
        drawCircle(t(BLANCO, oscuro), r * 0.66f, c)
        drawCircle(t(ROJO, oscuro), r * 0.36f, c)
        for (k in 0 until 16) {
            val a = k * Math.PI / 8
            drawLine(Color.Black.copy(alpha = 0.15f), Offset(c.x + r * 0.4f * cos(a).toFloat(), c.y + r * 0.4f * sin(a).toFloat()), Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), 0.6f * density)
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.35f
        // La pared de yeso, con decretos pegados.
        drawRect(t(YESO, oscuro), Offset.Zero, Size(w, piso))
        for (k in 0 until 7) {
            val c = Offset(w * azar(41, k), d.corona.bottom + (piso - d.corona.bottom) * azar(42, k))
            rotate(-4f + 8f * azar(43, k), c) {
                val r = Rect(c.x - 24 * u, c.y - 32 * u, c.x + 24 * u, c.y + 32 * u)
                drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
                drawRect(t(NEGRO, oscuro), Offset(r.left + 4 * u, r.top + 5 * u), Size(r.width - 8 * u, 3 * u))
                for (j in 0 until 8) drawLine(t(NEGRO, oscuro).copy(alpha = 0.3f), Offset(r.left + 5 * u, r.top + 14 * u + j * 6 * u), Offset(r.right - 5 * u, r.top + 14 * u + j * 6 * u), 1f * u)
            }
        }
        // Los estantes de pino, con sus escuadras de hierro, y los costados.
        listOf(w * 0.015f, w * 0.985f - w * 0.035f).forEach { x ->
            drawRect(t(PINO_OSC, oscuro), Offset(x, d.corona.bottom - 4 * u), Size(w * 0.035f, piso - d.corona.bottom + 4 * u))
            vetas(Rect(x, d.corona.bottom, x + w * 0.035f, piso), Color.Black.copy(alpha = 0.2f), (x / u).toInt(), 0.6f, vertical = true, grosor = 0.5f)
        }
        d.filas.forEach { f ->
            val r = Rect(w * 0.015f, f.bottom - 1 * u, w * 0.985f, f.bottom + maxOf(d.separacion * 0.4f, 7 * u))
            drawRect(t(PINO_LUZ, oscuro), r.topLeft, r.size)
            vetas(r, PINO_OSC.copy(alpha = 0.35f), (f.top / u).toInt(), 0.6f, grosor = 0.5f, ondulacion = 0.4f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 2.5f * u))
            listOf(w * 0.2f, w * 0.8f).forEach { x ->
                drawPath(Path().apply { moveTo(x - 1 * u, r.bottom); lineTo(x + 1 * u, r.bottom); lineTo(x + 1 * u, r.bottom + 14 * u); lineTo(x - 1 * u, r.bottom + 14 * u); close() }, t(HIERRO, oscuro))
                drawLine(t(HIERRO, oscuro), Offset(x, r.bottom + 12 * u), Offset(x + 12 * u, r.bottom), 1.6f * u)
            }
        }
        // El tablón de arriba y las colgaduras tricolores, con escarapelas en cada sujeción.
        val tablon = Rect(0f, d.corona.top + ch * 0.08f, w, d.corona.top + ch * 0.26f)
        drawRect(t(PINO, oscuro), tablon.topLeft, tablon.size)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tablon.bottom), Size(w, 2 * u))
        val n = 3
        val sw = w / n
        for (j in 0 until n) {
            val x0 = sw * j; val x1 = sw * (j + 1)
            val y0 = tablon.bottom
            listOf(ROJO to 0.95f, BLANCO to 0.7f, AZUL to 0.45f).forEach { (col, f) ->
                val hondo = (d.corona.bottom - y0) * f
                drawPath(Path().apply { moveTo(x0, y0); quadraticTo((x0 + x1) / 2, y0 + hondo * 2, x1, y0); close() }, t(col, oscuro))
            }
            for (k in 1..4) {
                val f = k / 5f
                drawLine(Color.Black.copy(alpha = 0.1f), Offset(x0 + sw * f, y0), Offset(x0 + sw * f, y0 + (d.corona.bottom - y0) * 0.8f * (1 - kotlin.math.abs(f - 0.5f) * 1.2f)), 1f * u)
            }
        }
        for (j in 0..n) escarapela(Offset(sw * j, tablon.bottom + 2 * u), ch * (if (j == 0 || j == n) 0.12f else 0.15f), oscuro)
        // El piso de tomettes, hexágonos de barro.
        val zoc = Rect(0f, piso, w, d.base.bottom)
        drawRect(t(TOMETTE_OSC, oscuro), zoc.topLeft, zoc.size)
        clipRect(zoc.left, zoc.top, zoc.right, zoc.bottom) {
            val r = zoc.height * 0.38f
            var fila = 0
            var cy = zoc.top
            while (cy < zoc.bottom + r) {
                var cx = if (fila % 2 == 0) 0f else r * 0.866f * 1.04f
                while (cx < w + r) {
                    drawPath(estrella(Offset(cx, cy), r * 0.96f, 3, 1f, -90f), t(mezcla(TOMETTE, TOMETTE_OSC, 0.25f * azar(fila, (cx / r).toInt())), oscuro))
                    cx += r * 1.732f * 1.04f
                }
                cy += r * 1.5f; fila++
            }
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, zoc.top), Size(w, 2 * u))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
            drawRect(t(NEGRO, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: unos periódicos doblados, apilados.
            for (k in 0 until 3) {
                val r = Rect(w * (0.22f + 0.03f * k), h * (0.86f - 0.07f * k), w * (0.74f + 0.02f * k), h * (0.93f - 0.07f * k))
                drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
                drawRect(t(NEGRO, oscuro).copy(alpha = 0.5f), Offset(r.left, r.bottom - 1 * u), Size(r.width, 1 * u))
                for (j in 1 until 4) drawLine(t(NEGRO, oscuro).copy(alpha = 0.25f), Offset(r.left + r.width * j / 4, r.top + 2 * u), Offset(r.left + r.width * j / 4, r.bottom - 2 * u), 0.6f * u)
            }
            return
        }
        val caja = Rect(w * 0.08f, h * 0.38f, w * 0.92f, h * 0.95f)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(caja.left, caja.bottom - h * 0.03f), Size(caja.width, h * 0.06f))
        // Los cuadernos de quejas, de pie, que asoman por arriba (y suben al abrirla).
        drawRect(Color(0xFF1A120A), caja.topLeft, caja.size)
        val k = c.n.coerceIn(1, 8)
        val sube = h * 0.08f + h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.16f
        for (i in 0 until k) {
            val x = caja.left + 4 * u + (caja.width - 8 * u) * i / k
            val y = caja.top - sube * (0.5f + 0.5f * azar(c.semilla, i))
            drawRect(t(CUADERNOS[(i + c.semilla.mod(CUADERNOS.size)) % CUADERNOS.size], oscuro), Offset(x, y), Size((caja.width - 8 * u) / k - 1.5f * u, caja.top - y + h * 0.15f))
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(x + 2 * u, y + 4 * u), Offset(x + (caja.width - 8 * u) / k - 4 * u, y + 4 * u), 0.8f * u)
        }
        // Los listones de pino del frente, con sus rendijas, y los montantes de las esquinas.
        val lis = 3
        val lh = caja.height / lis
        for (j in 0 until lis) {
            val r = Rect(caja.left, caja.top + lh * j + (if (j == 0) 0f else 2 * u), caja.right, caja.top + lh * (j + 1) - 2 * u)
            drawRect(t(PINO, oscuro), r.topLeft, r.size)
            vetas(r, PINO_OSC.copy(alpha = 0.4f), c.semilla + j, 0.7f, grosor = 0.5f, ondulacion = 0.5f)
        }
        listOf(caja.left, caja.right - 8 * u).forEach { x ->
            drawRect(t(PINO_OSC, oscuro), Offset(x, caja.top), Size(8 * u, caja.height))
            for (j in 0 until lis) drawCircle(t(HIERRO, oscuro), 1.1f * u, Offset(x + 4 * u, caja.top + lh * (j + 0.5f)))
        }
        // La cinta tricolor, atada con su lazo.
        val cx = w * 0.84f
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(t(col, oscuro), Offset(cx - 4.5f * u + i * 3 * u, caja.top), Size(3 * u, caja.height)) }
        val lazo = Offset(cx, caja.top + 4 * u)
        drawOval(t(ROJO, oscuro), Offset(lazo.x - 11 * u, lazo.y - 5 * u), Size(10 * u, 8 * u), style = Stroke(2.4f * u))
        drawOval(t(AZUL, oscuro), Offset(lazo.x + 1 * u, lazo.y - 5 * u), Size(10 * u, 8 * u), style = Stroke(2.4f * u))
        drawCircle(t(BLANCO, oscuro), 2.4f * u, lazo)
        // El cartel impreso pegado al frente, con su doble filete.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
        drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
        drawRect(t(NEGRO, oscuro), Offset(r.left + 3 * u, r.top + 3 * u), Size(r.width - 6 * u, 1.8f * u))
        drawRect(t(NEGRO, oscuro), Offset(r.left + 3 * u, r.top + 6 * u), Size(r.width - 6 * u, 0.6f * u))
    }

    override val rotulo = Rect(0.16f, 0.5f, 0.74f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = NEGRO
    override val letraRotulo: FontFamily = LetraExtra.convencion
    override val salida = 0f
    override val placa = Rect(0.32f, 0.4f, 0.68f, 0.92f)
    override fun tintaPlaca(oscuro: Boolean) = NEGRO

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Listones de pino forrados de papel de periódico: columnas de renglones grises.
        drawRect(t(PINO, oscuro))
        val hoja = if (oscuro) mezcla(PAPEL, Color.Black, 0.58f) else PAPEL
        val pl = 14 * u
        val hojas = listOf(Rect(pl, pl, w * 0.62f, h * 0.48f), Rect(w * 0.45f, h * 0.3f, w - pl, h * 0.78f), Rect(pl, h * 0.55f, w * 0.6f, h - pl))
        hojas.forEachIndexed { i, r ->
            rotate(if (i == 1) 2f else -1.5f, r.center) {
                drawRect(hoja, r.topLeft, r.size)
                drawRect(NEGRO.copy(alpha = 0.4f), Offset(r.left + 8 * u, r.top + 8 * u), Size(r.width - 16 * u, 6 * u))
                val col = 4
                val cw = (r.width - 16 * u) / col
                for (j in 0 until col) {
                    var y = r.top + 22 * u
                    while (y < r.bottom - 8 * u) { drawLine(NEGRO.copy(alpha = 0.16f), Offset(r.left + 8 * u + cw * j + 2 * u, y), Offset(r.left + 8 * u + cw * (j + 1) - 4 * u, y), 1.2f * u); y += 5 * u }
                }
            }
        }
        // Los montantes de pino y las rendijas de la caja.
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(PINO_OSC, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) PAPEL else NEGRO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(rasgado(s.width, s.height, 22, minOf(s.width, s.height) * 0.035f, semilla)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PAPEL.copy(alpha = 0.45f))
        // El doble filete de imprenta arriba, y uno fino abajo.
        val m = 8 * u * g
        drawRect(NEGRO.copy(alpha = 0.85f), Offset(m, 4 * u * g), Size(w - 2 * m, 2.4f * u * g))
        drawRect(NEGRO.copy(alpha = 0.85f), Offset(m, 8 * u * g), Size(w - 2 * m, 0.7f * u * g))
        drawRect(NEGRO.copy(alpha = 0.5f), Offset(m, h - 12 * u * g), Size(w - 2 * m, 0.7f * u * g))
        // El engrudo en las esquinas de arriba.
        mancha(Offset(4 * u, 4 * u), 14 * u * g, Color.White.copy(alpha = 0.35f))
        mancha(Offset(w - 4 * u, 4 * u), 14 * u * g, Color.White.copy(alpha = 0.35f))
    }

    override val margenes = MargenesFicha(16.dp, 12.dp, 18.dp, 28.dp, 24.dp, 32.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Las tres franjas: azul, blanco y rojo.
            "convence" -> {
                listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(col, Offset(c.x - s * 0.39f + i * s * 0.26f, c.y - s * 0.3f), Size(s * 0.26f, s * 0.6f)) }
                drawRect(NEGRO.copy(alpha = 0.4f), Offset(c.x - s * 0.39f, c.y - s * 0.3f), Size(s * 0.78f, s * 0.6f), style = Stroke(s * 0.04f))
            }
            // Una pluma de ave.
            "piensa" -> {
                drawLine(NEGRO, Offset(c.x - s * 0.36f, c.y + s * 0.42f), Offset(c.x + s * 0.3f, c.y - s * 0.38f), s * 0.05f, StrokeCap.Round)
                drawPath(Path().apply { moveTo(c.x - s * 0.1f, c.y + s * 0.08f); quadraticTo(c.x - s * 0.12f, c.y - s * 0.4f, c.x + s * 0.34f, c.y - s * 0.44f); quadraticTo(c.x + s * 0.24f, c.y - s * 0.02f, c.x - s * 0.1f, c.y + s * 0.08f); close() }, NEGRO.copy(alpha = 0.75f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Una escarapela tricolor. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension
        escarapela(center, s * 0.46f, false)
    }

    override val desorden = 2.5f
}
