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
            "hecho" -> selloHecho(SANGUINA, p.color)
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
            // La escarapela.
            "hecho" -> { drawCircle(AZUL, s * 0.44f, c); drawCircle(BLANCO, s * 0.3f, c); drawCircle(ROJO, s * 0.16f, c) }
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
