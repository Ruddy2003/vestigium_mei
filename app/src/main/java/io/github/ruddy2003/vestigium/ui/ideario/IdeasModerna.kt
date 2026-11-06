package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina

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
