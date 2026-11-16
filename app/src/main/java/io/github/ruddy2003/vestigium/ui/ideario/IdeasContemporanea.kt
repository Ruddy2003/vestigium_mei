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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
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
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Edad Contemporánea
// =====================================================================

/** Un engrane: dientes cuadrados alrededor de [c]. */
internal fun engrane(c: Offset, r: Float, dientes: Int): Path = Path().apply {
    val n = dientes * 4
    for (k in 0 until n) {
        val a = 2 * Math.PI * k / n
        val rr = if ((k / 2) % 2 == 0) r else r * 0.8f
        val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Una carátula de manómetro: aro, marcas y aguja ([valor] de 0 a 1). */
internal fun DrawScope.manometro(c: Offset, r: Float, aro: Color, cara: Color, aguja: Color, valor: Float = 0.62f) {
    val u = density
    drawCircle(aro, r, c)
    drawCircle(cara, r * 0.8f, c)
    for (k in 0..10) {
        val a = Math.toRadians(135.0 + k * 27.0)
        val p0 = Offset(c.x + r * 0.62f * cos(a).toFloat(), c.y + r * 0.62f * sin(a).toFloat())
        val p1 = Offset(c.x + r * 0.76f * cos(a).toFloat(), c.y + r * 0.76f * sin(a).toFloat())
        drawLine(Color.Black.copy(alpha = 0.6f), p0, p1, (if (k % 5 == 0) 1.4f else 0.7f) * u)
    }
    val a = Math.toRadians(135.0 + 270.0 * valor)
    drawLine(aguja, c, Offset(c.x + r * 0.66f * cos(a).toFloat(), c.y + r * 0.66f * sin(a).toFloat()), 1.4f * u, StrokeCap.Round)
    drawCircle(aro, r * 0.1f, c)
}

/**
 * Vapor: el gabinete de patentes. Caoba con flejes de latón remachados; arriba, un tubo de vapor con sus bridas, un manómetro
 * al centro, engranes y una válvula que suelta vapor; abajo, un zócalo de hierro con un volante. Cada tema es una gaveta de
 * latón remachada con un portatarjetas y un tirador; las más llenas quedan entreabiertas con sus tarjetas perforadas. Por
 * dentro, caoba con rieles de latón. Las fichas son tarjetas perforadas de esquina cortada.
 */
object IdeasVapor : DisenoIdeas {
    private val LATON = Color(0xFFC9A04A)
    private val LATON_LUZ = Color(0xFFE8CC7E)
    private val LATON_OSC = Color(0xFF8A6A24)
    private val COBRE = Color(0xFFB0663A)
    private val CAOBA = Color(0xFF5A2A1A)
    private val CAOBA_OSC = Color(0xFF2E140C)
    private val HIERRO = Color(0xFF2A2624)
    private val HIERRO_LUZ = Color(0xFF5A5450)
    private val CARTULINA = Color(0xFFE8DCB8)
    private val TINTA = Color(0xFF231B14)
    private val VIDRIO = Color(0xFFF4EFE0)
    private val ROJO = Color(0xFF9C3D24)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 2f
    override val corona = 0.3f
    override val base = 0.13f
    override val lado = 0.08f
    override val hueco = 0.03f

    private const val SALIDA = 0.18f

    /** Remaches a lo largo de una línea. */
    private fun DrawScope.remaches(a: Offset, b: Offset, paso: Float, color: Color) {
        val u = density
        val l = kotlin.math.hypot(b.x - a.x, b.y - a.y)
        val n = (l / paso).toInt()
        for (k in 0..n) {
            val f = if (n == 0) 0f else k / n.toFloat()
            val p = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
            drawCircle(color, 1.4f * u, p)
            drawCircle(Color.White.copy(alpha = 0.35f), 0.5f * u, Offset(p.x - 0.4f * u, p.y - 0.4f * u))
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo de caoba, con su marco de latón remachado.
        val cuerpo = Rect(w * 0.025f, d.corona.bottom - 6 * u, w * 0.975f, d.base.top + d.base.height * 0.3f)
        drawRect(t(CAOBA, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, CAOBA_OSC.copy(alpha = 0.45f), 29, 1f, grosor = 0.7f, ondulacion = 0.5f)
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.55f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u)) }
        drawRect(t(LATON, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(5 * u))
        remaches(Offset(cuerpo.left + 2.5f * u, cuerpo.top + 8 * u), Offset(cuerpo.left + 2.5f * u, cuerpo.bottom - 8 * u), 16 * u, t(LATON_OSC, oscuro))
        remaches(Offset(cuerpo.right - 2.5f * u, cuerpo.top + 8 * u), Offset(cuerpo.right - 2.5f * u, cuerpo.bottom - 8 * u), 16 * u, t(LATON_OSC, oscuro))
        // La tapa de latón y, encima, el tubo de vapor con sus bridas.
        val tapa = Rect(0f, d.corona.bottom - 12 * u, w, d.corona.bottom - 3 * u)
        drawRect(Brush.verticalGradient(listOf(t(LATON_LUZ, oscuro), t(LATON, oscuro), t(LATON_OSC, oscuro)), tapa.top, tapa.bottom), tapa.topLeft, tapa.size)
        val ty = d.corona.top + ch * 0.62f
        val tubo = 7 * u
        drawRect(t(COBRE, oscuro), Offset(w * 0.04f, ty - tubo / 2), Size(w * 0.92f, tubo))
        drawLine(Color.White.copy(alpha = 0.3f), Offset(w * 0.04f, ty - tubo * 0.25f), Offset(w * 0.96f, ty - tubo * 0.25f), 1f * u)
        listOf(0.06f, 0.3f, 0.7f, 0.94f).forEach { f -> drawRect(t(LATON_OSC, oscuro), Offset(w * f - 3 * u, ty - tubo * 0.85f), Size(6 * u, tubo * 1.7f)) }
        listOf(0.12f, 0.88f).forEach { f -> drawRect(t(COBRE, oscuro), Offset(w * f - tubo / 2, ty), Size(tubo, tapa.top - ty)) }
        // Los engranes a los lados y el manómetro al centro.
        val rg = ch * 0.26f
        listOf(Offset(w * 0.24f, ty - ch * 0.12f) to rg, Offset(w * 0.33f, ty - ch * 0.3f) to rg * 0.6f, Offset(w * 0.78f, ty - ch * 0.16f) to rg * 0.85f).forEachIndexed { i, (c, r) ->
            drawPath(engrane(c, r, if (i == 1) 8 else 12), t(if (i == 1) COBRE else LATON_OSC, oscuro))
            drawCircle(t(LATON, oscuro), r * 0.5f, c, style = Stroke(r * 0.15f))
            drawCircle(t(HIERRO, oscuro), r * 0.16f, c)
        }
        manometro(Offset(w / 2, ty - ch * 0.18f), ch * 0.36f, t(LATON, oscuro), t(VIDRIO, oscuro), ROJO)
        // La válvula que suelta vapor.
        val v = Offset(w * 0.9f, ty - tubo / 2)
        drawRect(t(LATON_OSC, oscuro), Offset(v.x - 3 * u, v.y - 10 * u), Size(6 * u, 10 * u))
        for (k in 0 until 4) drawCircle(Color.White.copy(alpha = if (oscuro) 0.14f else 0.5f), (4f + 3f * k) * u, Offset(v.x + k * 5 * u, v.y - 16 * u - k * 7 * u))
        // El zócalo de hierro remachado, con un volante.
        val zoc = Rect(w * 0.01f, cuerpo.bottom, w * 0.99f, d.base.bottom)
        drawRect(t(HIERRO, oscuro), zoc.topLeft, zoc.size)
        remaches(Offset(zoc.left + 6 * u, zoc.top + 4 * u), Offset(zoc.right - 6 * u, zoc.top + 4 * u), 14 * u, t(HIERRO_LUZ, oscuro))
        val vol = Offset(w * 0.1f, zoc.center.y + 2 * u)
        val rv = zoc.height * 0.3f
        drawCircle(t(ROJO, oscuro), rv, vol, style = Stroke(2.4f * u))
        for (k in 0 until 4) { val a = k * Math.PI / 2 + 0.4; drawLine(t(ROJO, oscuro), vol, Offset(vol.x + rv * cos(a).toFloat(), vol.y + rv * sin(a).toFloat()), 1.6f * u) }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(LATON, oscuro), r.topLeft, r.size)
            drawRect(t(LATON_OSC, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
            listOf(r.left + 4 * u, r.right - 4 * u).forEach { x -> drawCircle(t(LATON_OSC, oscuro), 1.4f * u, Offset(x, r.center.y)) }
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Las gavetas más llenas quedan entreabiertas y asoman las tarjetas.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF0E0806), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 7)
            for (i in 0 until k) {
                val x = w * (0.08f + 0.84f * (i + 0.5f) / k) - w * 0.05f
                val y0 = arriba * (0.1f + 0.5f * azar(c.semilla, i)) + 1 * u
                drawRect(t(CARTULINA, oscuro), Offset(x, y0), Size(w * 0.1f, arriba + 4 * u - y0))
                for (j in 0 until 3) drawRect(t(TINTA, oscuro).copy(alpha = 0.7f), Offset(x + 2 * u + j * 4 * u, y0 + 3 * u), Size(1.6f * u, 3 * u))
            }
        }
        // El frente: una plancha de latón con su doble filete grabado y remaches en el borde.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(t(LATON, oscuro), f.topLeft, f.size)
        drawLine(t(LATON_LUZ, oscuro), Offset(f.left, f.top + 1 * u), Offset(f.right, f.top + 1 * u), 1.4f * u)
        drawRect(t(LATON_OSC, oscuro), Offset(f.left, f.bottom - 2 * u), Size(f.width, 2 * u))
        val fh = f.bottom - f.top
        drawRect(t(LATON_OSC, oscuro), Offset(f.left + 7 * u, f.top + 7 * u), Size(f.width - 14 * u, fh - 14 * u), style = Stroke(0.8f * u))
        drawRect(t(LATON_OSC, oscuro), Offset(f.left + 9.5f * u, f.top + 9.5f * u), Size(f.width - 19 * u, fh - 19 * u), style = Stroke(0.5f * u))
        remaches(Offset(f.left + 3.5f * u, f.top + 3.5f * u), Offset(f.right - 3.5f * u, f.top + 3.5f * u), 18 * u, t(LATON_OSC, oscuro))
        remaches(Offset(f.left + 3.5f * u, f.bottom - 3.5f * u), Offset(f.right - 3.5f * u, f.bottom - 3.5f * u), 18 * u, t(LATON_OSC, oscuro))
        // El portatarjetas y su tarjeta.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        if (!c.vacio) drawRect(t(CARTULINA, oscuro), r.topLeft, r.size)
        drawRect(t(LATON_OSC, oscuro), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(2.4f * u))
        listOf(r.left - 5 * u, r.right + 5 * u).forEach { x -> drawCircle(t(LATON_OSC, oscuro), 1.8f * u, Offset(x, r.center.y)) }
        // El tirador de gota.
        val ti = Offset(w / 2, h * 0.8f + baja)
        val rt = minOf(w, h) * 0.08f
        drawCircle(t(LATON_OSC, oscuro), rt * 0.35f, Offset(ti.x - rt, ti.y - rt * 0.2f))
        drawCircle(t(LATON_OSC, oscuro), rt * 0.35f, Offset(ti.x + rt, ti.y - rt * 0.2f))
        drawArc(t(HIERRO, oscuro), 0f, 180f, false, Offset(ti.x - rt, ti.y - rt * 0.8f), Size(rt * 2, rt * 1.4f), style = Stroke(2 * u))
    }

    override val rotulo = Rect(0.26f, 0.16f, 0.74f, 0.62f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.caldera
    override val salida = SALIDA
    override val placa = Rect(0.32f, 0.36f, 0.68f, 0.86f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de caoba, con dos rieles de latón a lo largo.
        drawRect(t(CAOBA, oscuro))
        vetas(Rect(0f, 0f, w, h), CAOBA_OSC.copy(alpha = 0.4f), 71, 1.2f, vertical = true, grosor = 0.7f, ondulacion = 0.5f)
        listOf(w * 0.3f, w * 0.7f).forEach { x ->
            drawRect(t(LATON_OSC, oscuro), Offset(x - 3 * u, 0f), Size(6 * u, h))
            drawLine(t(LATON_LUZ, oscuro), Offset(x - 1.5f * u, 0f), Offset(x - 1.5f * u, h), 1f * u)
        }
        // Las paredes de latón remachadas.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(LATON, oscuro), r.topLeft, r.size) }
        remaches(Offset(pl / 2, pl), Offset(pl / 2, h - pl), 20 * u, t(LATON_OSC, oscuro))
        remaches(Offset(w - pl / 2, pl), Offset(w - pl / 2, h - pl), 20 * u, t(LATON_OSC, oscuro))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = VIDRIO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 4

    /** La tarjeta perforada, con la esquina de arriba a la izquierda cortada. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val k = minOf(s.width, s.height) * 0.1f
        moveTo(k, 0f); lineTo(s.width, 0f); lineTo(s.width, s.height); lineTo(0f, s.height); lineTo(0f, k); close()
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(CARTULINA.copy(alpha = 0.45f))
        // Dos bandas de perforaciones, arriba y abajo, con su patrón propio.
        val paso = 5.5f * u * g
        val hw = 1.8f * u * g; val hh = 3.4f * u * g
        listOf(4 * u * g, 9 * u * g, h - 12 * u * g).forEachIndexed { fila, y ->
            var x = minOf(w, h) * 0.12f; var k = 0
            while (x < w - 4 * u) {
                if (azar(semilla + fila * 31, k) < 0.55f) drawRect(TINTA.copy(alpha = 0.75f), Offset(x, y), Size(hw, hh))
                else drawRect(TINTA.copy(alpha = 0.12f), Offset(x, y), Size(hw, hh), style = Stroke(0.5f * u))
                x += paso; k++
            }
        }
        drawLine(ROJO.copy(alpha = 0.5f), Offset(0f, 14.5f * u * g), Offset(w, 14.5f * u * g), 0.7f * u)
    }

    override val margenes = MargenesFicha(18.dp, 12.dp, 16.dp, 32.dp, 24.dp, 28.dp)
    override val proporcionFicha = 1.3f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello de la patente, con sus dos cintas.
            "hecho" -> {
                drawPath(Path().apply { moveTo(c.x - s * 0.18f, c.y); lineTo(c.x - s * 0.3f, c.y + s * 0.46f); lineTo(c.x - s * 0.08f, c.y + s * 0.36f); close() }, LATON_OSC)
                drawPath(Path().apply { moveTo(c.x + s * 0.18f, c.y); lineTo(c.x + s * 0.3f, c.y + s * 0.46f); lineTo(c.x + s * 0.08f, c.y + s * 0.36f); close() }, LATON_OSC)
                drawCircle(ROJO, s * 0.3f, Offset(c.x, c.y - s * 0.08f)); drawCircle(LATON_LUZ, s * 0.2f, Offset(c.x, c.y - s * 0.08f), style = Stroke(s * 0.05f))
            }
            "convence" -> { drawPath(engrane(c, s * 0.46f, 8), LATON_OSC); drawCircle(LATON_LUZ, s * 0.16f, c) }
            "piensa" -> manometro(c, s * 0.46f, LATON_OSC, VIDRIO, ROJO, 0.4f)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un perno de cabeza hexagonal. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(estrella(c, s * 0.46f, 3, 1f, 0f), LATON_OSC)
        drawCircle(LATON_LUZ, s * 0.22f, c)
        drawCircle(LATON_OSC, s * 0.22f, c, style = Stroke(s * 0.05f))
    }

    override val desorden = 0f
}

/**
 * Napoleón: el secrétaire de campaña. Caoba con cantoneras y asas embutidas de latón, una cornisa imperio con su corona de
 * laurel dorada y una abeja; abajo, la tapa abatida forrada de cuero verde con su dorado, un tintero y una pluma. Cada tema es
 * una casilla con su pila de despachos lacrados y atados con cinta azul, y el tema en un portaetiquetas de latón. Por
 * dentro, cuero verde con hierros dorados. Las fichas son despachos con su filete azul y la abeja tenue.
 */
object IdeasNapoleon : DisenoIdeas {
    private val CAOBA = Color(0xFF6A2E1C)
    private val CAOBA_LUZ = Color(0xFF8E4428)
    private val CAOBA_OSC = Color(0xFF34140A)
    private val BRONCE = Color(0xFFC9A24C)
    private val BRONCE_OSC = Color(0xFF8A6A24)
    private val CUERO = Color(0xFF2E5A3A)
    private val CUERO_OSC = Color(0xFF1E3E28)
    private val HUECO = Color(0xFF1A0C06)
    private val PAPEL = Color(0xFFF6F0E0)
    private val PAPEL_OSC = Color(0xFFE2D8BE)
    private val AZUL = Color(0xFF1E3A8A)
    private val ROJO = Color(0xFFA3172E)
    private val TINTA = Color(0xFF14213D)
    private val LACRE = Color(0xFF9A1A1A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 0.9f
    override val corona = 0.24f
    override val base = 0.2f
    override val lado = 0.075f
    override val hueco = 0.025f

    /** Una corona de laurel: dos ramas de hojas que se cierran arriba. */
    private fun DrawScope.laurel(c: Offset, r: Float, color: Color) {
        val u = density
        listOf(-1f, 1f).forEach { s ->
            drawArc(color, if (s < 0) 100f else -80f, 160f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1f * u))
            for (k in 0 until 7) {
                val a = Math.toRadians((if (s < 0) 110.0 + k * 22.0 else 70.0 - k * 22.0))
                val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
                rotate(Math.toDegrees(a).toFloat() + 90f * s, p) { drawOval(color, Offset(p.x - r * 0.08f, p.y - r * 0.2f), Size(r * 0.16f, r * 0.34f)) }
            }
        }
    }

    /** Una abeja imperial, vista desde arriba. */
    private fun DrawScope.abeja(c: Offset, s: Float, color: Color) {
        drawOval(color, Offset(c.x - s * 0.36f, c.y - s * 0.32f), Size(s * 0.32f, s * 0.42f))
        drawOval(color, Offset(c.x + s * 0.04f, c.y - s * 0.32f), Size(s * 0.32f, s * 0.42f))
        drawOval(color, Offset(c.x - s * 0.12f, c.y - s * 0.3f), Size(s * 0.24f, s * 0.72f))
        drawCircle(color, s * 0.12f, Offset(c.x, c.y - s * 0.38f))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo de caoba, con sus cantoneras de latón y las asas embutidas de los costados.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 6 * u, w * 0.98f, d.base.top + 4 * u)
        drawRect(t(CAOBA, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, CAOBA_OSC.copy(alpha = 0.4f), 37, 1f, grosor = 0.7f, ondulacion = 0.7f)
        d.huecos.forEach { r -> drawRect(t(BRONCE_OSC, oscuro), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(1.2f * u)) }
        listOf(Offset(cuerpo.left, cuerpo.top) to Offset(1f, 1f), Offset(cuerpo.right, cuerpo.top) to Offset(-1f, 1f)).forEach { (o, s) ->
            drawRect(t(BRONCE, oscuro), Offset(if (s.x > 0) o.x else o.x - 14 * u, o.y), Size(14 * u, 4 * u))
            drawRect(t(BRONCE, oscuro), Offset(if (s.x > 0) o.x else o.x - 4 * u, o.y), Size(4 * u, 14 * u))
        }
        listOf(cuerpo.left + 2 * u, cuerpo.right - 8 * u).forEach { x ->
            val y = (cuerpo.top + cuerpo.bottom) / 2
            drawRect(t(BRONCE, oscuro), Offset(x, y - 14 * u), Size(6 * u, 28 * u))
            drawRect(t(CAOBA_OSC, oscuro), Offset(x + 1.5f * u, y - 10 * u), Size(3 * u, 20 * u))
        }
        // La cornisa imperio, con su perlado dorado, y encima la corona de laurel con la abeja.
        val cornisa = Rect(0f, d.corona.top + ch * 0.6f, w, d.corona.bottom)
        drawRect(t(CAOBA_OSC, oscuro), cornisa.topLeft, cornisa.size)
        drawRect(t(CAOBA_LUZ, oscuro), Offset(0f, cornisa.top), Size(w, cornisa.height * 0.3f))
        var x = 3 * u
        while (x < w) { drawCircle(t(BRONCE, oscuro), 1.3f * u, Offset(x, cornisa.top + cornisa.height * 0.62f)); x += 5 * u }
        val tim = Rect(w * 0.3f, d.corona.top + ch * 0.08f, w * 0.7f, cornisa.top)
        drawRect(t(CAOBA, oscuro), tim.topLeft, tim.size)
        drawRect(t(BRONCE, oscuro), tim.topLeft, tim.size, style = Stroke(1.2f * u))
        val lc = tim.center
        laurel(lc, tim.height * 0.4f, t(BRONCE, oscuro))
        abeja(Offset(lc.x, lc.y + tim.height * 0.05f), tim.height * 0.36f, t(BRONCE, oscuro))
        listOf(w * 0.15f, w * 0.85f).forEach { px -> drawPath(estrella(Offset(px, cornisa.top - ch * 0.12f), ch * 0.12f, 7, 0.4f), t(BRONCE, oscuro)) }
        // La tapa abatida: cuero verde con su doble filete dorado, un tintero y una pluma.
        val tapa = Rect(0f, d.base.top + 4 * u, w, d.base.bottom)
        drawRect(t(CAOBA, oscuro), tapa.topLeft, tapa.size)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, tapa.top), Size(w, 3 * u))
        val cuero = Rect(tapa.left + 8 * u, tapa.top + 7 * u, tapa.right - 8 * u, tapa.bottom - 6 * u)
        drawRect(t(CUERO, oscuro), cuero.topLeft, cuero.size)
        drawRect(t(BRONCE, oscuro), Offset(cuero.left + 3 * u, cuero.top + 3 * u), Size(cuero.width - 6 * u, cuero.height - 6 * u), style = Stroke(0.9f * u))
        drawRect(t(BRONCE, oscuro), Offset(cuero.left + 5.5f * u, cuero.top + 5.5f * u), Size(cuero.width - 11 * u, cuero.height - 11 * u), style = Stroke(0.5f * u))
        val ti = Offset(w * 0.86f, cuero.center.y)
        drawRect(Color(0xFF1A2430), Offset(ti.x - 7 * u, ti.y - 5 * u), Size(14 * u, 12 * u))
        drawRect(t(BRONCE, oscuro), Offset(ti.x - 8 * u, ti.y - 7 * u), Size(16 * u, 3 * u))
        drawLine(t(PAPEL, oscuro), Offset(ti.x, ti.y - 6 * u), Offset(ti.x + 16 * u, ti.y - 24 * u), 1.4f * u, StrokeCap.Round)
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
            drawRect(t(BRONCE, oscuro), r.topLeft, r.size, style = Stroke(1.6f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // La casilla: el hueco oscuro, con su sombra adentro.
        drawRect(t(HUECO, oscuro))
        sombraInterior(Rect(0f, 0f, w, h), w * 0.12f, 0.5f)
        val piso = h * 0.68f
        if (c.vacio) {
            // Un lugar libre: un catalejo de latón.
            val y = piso - h * 0.06f
            drawRoundRect(t(BRONCE, oscuro), Offset(w * 0.14f, y), Size(w * 0.3f, h * 0.07f), CornerRadius(2 * u))
            drawRoundRect(t(BRONCE_OSC, oscuro), Offset(w * 0.42f, y + h * 0.006f), Size(w * 0.24f, h * 0.058f), CornerRadius(2 * u))
            drawRoundRect(t(CAOBA_LUZ, oscuro), Offset(w * 0.64f, y + h * 0.012f), Size(w * 0.2f, h * 0.046f), CornerRadius(2 * u))
        } else {
            // La pila de despachos, atada con su cinta azul, con algún sello de lacre.
            val k = c.n.coerceIn(1, 8)
            val g = h * 0.035f
            for (i in 0 until k) {
                val y = piso - (i + 1) * g
                val dx = (azar(c.semilla, i) - 0.5f) * w * 0.08f
                drawRect(t(if (i % 2 == 0) PAPEL else PAPEL_OSC, oscuro), Offset(w * 0.12f + dx, y), Size(w * 0.76f, g - 0.8f * u))
                if (i % 3 == 1) drawCircle(t(LACRE, oscuro), g * 0.32f, Offset(w * 0.24f + dx, y + g * 0.45f))
            }
            val top = piso - k * g
            drawRect(t(AZUL, oscuro), Offset(w * 0.6f, top - 1 * u), Size(2.4f * u, piso - top + 1 * u))
            drawOval(t(AZUL, oscuro), Offset(w * 0.6f - 6 * u, top - 4 * u), Size(6 * u, 5 * u), style = Stroke(1.4f * u))
            drawOval(t(AZUL, oscuro), Offset(w * 0.6f + 2.4f * u, top - 4 * u), Size(6 * u, 5 * u), style = Stroke(1.4f * u))
            if (ab > 0f) {
                val y = top + ab * h * 0.2f
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.16f, y + 2 * u), Size(w * 0.7f, h * 0.06f))
                drawRect(t(PAPEL, oscuro), Offset(w * 0.14f, y), Size(w * 0.7f, h * 0.06f))
                drawCircle(t(LACRE, oscuro), h * 0.018f, Offset(w * 0.49f, y + h * 0.03f))
            }
        }
        // El canto de latón de la casilla y el portaetiquetas con el tema.
        drawRect(t(BRONCE_OSC, oscuro), Offset(0f, piso), Size(w, 2.5f * u))
        drawRect(t(CAOBA, oscuro), Offset(0f, piso + 2.5f * u), Size(w, h - piso))
        if (c.vacio) return
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
        drawRect(t(BRONCE, oscuro), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u), style = Stroke(2.2f * u))
    }

    override val rotulo = Rect(0.08f, 0.73f, 0.92f, 0.95f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.bodoniTitulo
    override val salida = 0f
    override val placa = Rect(0.32f, 0.3f, 0.68f, 0.72f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Cuero verde con hierros dorados: doble filete, florones en las esquinas y una corona de laurel al centro.
        drawRect(t(CUERO, oscuro))
        for (k in 0 until 30) mancha(Offset(w * azar(91, k), h * azar(92, k)), (20f + 30f * azar(93, k)) * u, CUERO_OSC.copy(alpha = 0.18f))
        val m = 30 * u
        drawRect(t(BRONCE, oscuro).copy(alpha = 0.7f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1.4f * u))
        drawRect(t(BRONCE, oscuro).copy(alpha = 0.5f), Offset(m + 6 * u, m + 6 * u), Size(w - 2 * m - 12 * u, h - 2 * m - 12 * u), style = Stroke(0.8f * u))
        listOf(Offset(m, m), Offset(w - m, m), Offset(m, h - m), Offset(w - m, h - m)).forEach { drawPath(estrella(it, 9 * u, 8, 0.4f), t(BRONCE, oscuro).copy(alpha = 0.7f)) }
        laurel(Offset(w / 2, h * 0.45f), w * 0.22f, BRONCE.copy(alpha = 0.12f))
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CAOBA, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = PAPEL
    override val pared: Dp = 18.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El doble filete azul de la cancillería, los dobleces en tercios y la abeja tenue abajo.
        val m = 4 * u * g
        drawRect(AZUL.copy(alpha = 0.5f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1f * u * g))
        drawRect(AZUL.copy(alpha = 0.35f), Offset(m + 2.4f * u * g, m + 2.4f * u * g), Size(w - 2 * m - 4.8f * u * g, h - 2 * m - 4.8f * u * g), style = Stroke(0.5f * u))
        listOf(w / 3, w * 2 / 3).forEach { x -> drawLine(Color.Black.copy(alpha = 0.05f), Offset(x, 0f), Offset(x, h), 1f * u) }
        abeja(Offset(w / 2, h - 12 * u * g), 12 * u * g, AZUL.copy(alpha = 0.22f))
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 14.dp, 26.dp, 28.dp, 32.dp)
    override val proporcionFicha = 0.82f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La N imperial en su sello.
            "hecho" -> {
                drawCircle(AZUL, s * 0.42f, c)
                drawPath(Path().apply { moveTo(c.x - s * 0.17f, c.y + s * 0.22f); lineTo(c.x - s * 0.17f, c.y - s * 0.22f); lineTo(c.x + s * 0.17f, c.y + s * 0.22f); lineTo(c.x + s * 0.17f, c.y - s * 0.22f) },
                    p.color, style = Stroke(s * 0.09f, join = androidx.compose.ui.graphics.StrokeJoin.Miter))
            }
            "convence" -> laurel(c, s * 0.36f, BRONCE_OSC)
            "piensa" -> abeja(c, s * 0.9f, AZUL)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** La estrella de la Legión de Honor, de esmalte blanco, colgada de su cinta roja. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRect(ROJO, Offset(c.x - s * 0.16f, c.y - s * 0.5f), Size(s * 0.32f, s * 0.26f))
        val e = Offset(c.x, c.y + s * 0.12f)
        drawPath(estrella(e, s * 0.38f, 5, 0.45f), BRONCE)
        drawPath(estrella(e, s * 0.3f, 5, 0.45f), Color.White)
        drawCircle(BRONCE, s * 0.08f, e)
    }

    override val desorden = 1f
}

/**
 * Art déco: el casillero de la recepción de un hotel. Laca negra con filetes de oro y pilastras estriadas; arriba, un remate
 * escalonado con un abanico de rayos; abajo, el mostrador de mármol verde con chevrones dorados y su timbre. Cada tema es un
 * casillero con su llave colgada y los telegramas que van llegando, y el tema en una placa de latón grabada. Por dentro, laca
 * negra con un sol de rayos de oro. Las fichas son telegramas con esquinas escalonadas.
 */
object IdeasDeco : DisenoIdeas {
    private val LACA = Color(0xFF161310)
    private val LACA_LUZ = Color(0xFF2E2820)
    private val ORO = Color(0xFFC9A24C)
    private val ORO_LUZ = Color(0xFFE8CF8A)
    private val ORO_OSC = Color(0xFF8A6A12)
    private val VERDE = Color(0xFF1F6B57)
    private val MARMOL = Color(0xFF2E5E50)
    private val MARMOL_VETA = Color(0xFF9CC8B8)
    private val CREMA = Color(0xFFF6F0E1)
    private val TELEGRAMA = Color(0xFFF2E6B8)
    private val TINTA = Color(0xFF16130E)
    private val GRANATE = Color(0xFF7A1E2A)
    private val ROJO = Color(0xFFA8322A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.25f) else c

    override val cols = 3
    override val proporcion = 0.95f
    override val corona = 0.3f
    override val base = 0.18f
    override val lado = 0.075f
    override val hueco = 0.025f

    /** Un abanico de rayos: medio sol con sus rayos alternados. */
    private fun DrawScope.abanico(c: Offset, r: Float, a: Color, b: Color) {
        val n = 14
        for (k in 0 until n) drawArc(if (k % 2 == 0) a else b, 180f + k * 180f / n, 180f / n, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        drawArc(LACA, 180f, 180f, true, Offset(c.x - r * 0.28f, c.y - r * 0.28f), Size(r * 0.56f, r * 0.56f))
        drawArc(ORO, 180f, 180f, false, Offset(c.x - r * 0.28f, c.y - r * 0.28f), Size(r * 0.56f, r * 0.56f), style = Stroke(1.2f * density))
    }

    /** Una llave de hotel, colgando de [c]. */
    private fun DrawScope.llave(c: Offset, l: Float, color: Color) {
        val u = density
        drawCircle(color, l * 0.16f, Offset(c.x, c.y + l * 0.16f), style = Stroke(1.6f * u))
        drawLine(color, Offset(c.x, c.y + l * 0.32f), Offset(c.x, c.y + l), 1.8f * u)
        drawLine(color, Offset(c.x, c.y + l * 0.86f), Offset(c.x + l * 0.16f, c.y + l * 0.86f), 1.8f * u)
        drawLine(color, Offset(c.x, c.y + l * 0.96f), Offset(c.x + l * 0.12f, c.y + l * 0.96f), 1.8f * u)
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo de laca negra, con el filete de oro en cada casillero.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 6 * u, w * 0.98f, d.base.top + 4 * u)
        drawRect(t(LACA, oscuro), cuerpo.topLeft, cuerpo.size)
        d.huecos.forEach { r -> drawRect(t(ORO, oscuro), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(1.4f * u)) }
        // Las pilastras estriadas de los costados.
        listOf(cuerpo.left, cuerpo.right - w * 0.045f).forEach { x ->
            val r = Rect(x, cuerpo.top, x + w * 0.045f, cuerpo.bottom)
            drawRect(t(LACA_LUZ, oscuro), r.topLeft, r.size)
            for (k in 1 until 4) drawLine(t(ORO, oscuro), Offset(r.left + r.width * k / 4, r.top + 6 * u), Offset(r.left + r.width * k / 4, r.bottom - 6 * u), 0.8f * u)
        }
        // El remate escalonado, con su abanico de rayos.
        val pasos = listOf(Rect(0f, d.corona.top + ch * 0.74f, w, d.corona.bottom), Rect(w * 0.12f, d.corona.top + ch * 0.56f, w * 0.88f, d.corona.top + ch * 0.75f), Rect(w * 0.26f, d.corona.top + ch * 0.38f, w * 0.74f, d.corona.top + ch * 0.57f))
        pasos.forEach { r ->
            drawRect(t(LACA, oscuro), r.topLeft, r.size)
            drawLine(t(ORO, oscuro), r.topLeft, Offset(r.right, r.top), 1.6f * u)
        }
        abanico(Offset(w / 2, pasos[2].top), ch * 0.36f, t(ORO, oscuro), t(ORO_OSC, oscuro))
        drawRect(t(ORO, oscuro), Offset(0f, d.corona.bottom - 3 * u), Size(w, 1.4f * u))
        // El mostrador: la cubierta de mármol verde veteado y el frente de laca con chevrones dorados, y el timbre.
        val cubierta = Rect(0f, d.base.top + 4 * u, w, d.base.top + d.base.height * 0.34f)
        drawRect(t(MARMOL, oscuro), cubierta.topLeft, cubierta.size)
        clipRect(cubierta.left, cubierta.top, cubierta.right, cubierta.bottom) {
            for (k in 0 until 7) {
                val p = Path().apply { moveTo(w * azar(61, k), cubierta.top); cubicTo(w * azar(62, k), cubierta.bottom, w * azar(63, k), cubierta.top, w * azar(64, k) + 40 * u, cubierta.bottom) }
                drawPath(p, MARMOL_VETA.copy(alpha = 0.35f), style = Stroke(0.8f * u))
            }
        }
        drawLine(t(ORO, oscuro), Offset(0f, cubierta.bottom), Offset(w, cubierta.bottom), 2 * u)
        val frente = Rect(0f, cubierta.bottom + 2 * u, w, d.base.bottom)
        drawRect(t(LACA, oscuro), frente.topLeft, frente.size)
        val cv = frente.height * 0.4f
        var x = 0f
        while (x < w) {
            drawPath(Path().apply { moveTo(x, frente.bottom - 4 * u); lineTo(x + cv / 2, frente.bottom - 4 * u - cv * 0.5f); lineTo(x + cv, frente.bottom - 4 * u) }, t(ORO, oscuro).copy(alpha = 0.6f), style = Stroke(1f * u))
            x += cv
        }
        val tim = Offset(w * 0.86f, cubierta.top + cubierta.height * 0.7f)
        drawArc(t(ORO_LUZ, oscuro), 180f, 180f, true, Offset(tim.x - 9 * u, tim.y - 9 * u), Size(18 * u, 18 * u))
        drawRect(t(ORO_OSC, oscuro), Offset(tim.x - 11 * u, tim.y), Size(22 * u, 2.5f * u))
        drawRect(t(ORO_OSC, oscuro), Offset(tim.x - 1.2f * u, tim.y - 12 * u), Size(2.4f * u, 3.5f * u))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(ORO, oscuro), r.topLeft, r.size)
            drawRect(t(ORO_OSC, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.8f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // El casillero: el fondo de laca con su sombra.
        drawRect(t(LACA_LUZ, oscuro))
        sombraInterior(Rect(0f, 0f, w, h), w * 0.14f, 0.5f)
        val piso = h * 0.68f
        // La llave colgada de su gancho.
        val gancho = Offset(w * 0.26f, h * 0.08f)
        drawCircle(t(ORO, oscuro), 1.8f * u, gancho)
        llave(Offset(gancho.x, gancho.y + 1 * u), h * 0.36f, t(ORO, oscuro))
        drawRoundRect(t(GRANATE, oscuro), Offset(gancho.x + 3 * u, gancho.y + h * 0.3f), Size(w * 0.12f, h * 0.12f), CornerRadius(w * 0.05f))
        if (!c.vacio) {
            // Los telegramas que van llegando, apoyados en el fondo (más al abrirlo).
            val k = c.n.coerceIn(1, 6)
            for (i in 0 until k) {
                val x = w * (0.44f + 0.07f * i)
                rotate(-8f + 3f * i, Offset(x, piso)) {
                    val alto = h * (0.3f + 0.05f * azar(c.semilla, i)) + ab * h * 0.12f
                    drawRect(t(TELEGRAMA, oscuro), Offset(x - w * 0.11f, piso - alto), Size(w * 0.22f, alto))
                    drawRect(t(VERDE, oscuro), Offset(x - w * 0.11f, piso - alto), Size(w * 0.22f, 2.5f * u))
                }
            }
        }
        // El canto dorado y la placa grabada con el tema.
        drawRect(t(ORO, oscuro), Offset(0f, piso), Size(w, 2 * u))
        drawRect(t(LACA, oscuro), Offset(0f, piso + 2 * u), Size(w, h - piso))
        if (c.vacio) return
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(ORO_LUZ, oscuro), r.topLeft, r.size)
        drawRect(t(ORO_OSC, oscuro), Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3 * u, r.height - 3 * u), style = Stroke(0.8f * u))
    }

    override val rotulo = Rect(0.07f, 0.73f, 0.93f, 0.95f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.pasaje
    override val salida = 0f
    override val placa = Rect(0.32f, 0.44f, 0.68f, 0.88f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Laca negra con un sol de rayos de oro que sale de abajo, y un doble filete.
        drawRect(t(LACA, oscuro))
        val o = Offset(w / 2, h * 1.05f)
        for (k in 0 until 36) {
            val a = Math.toRadians(180.0 + k * 5.0)
            drawLine(ORO.copy(alpha = if (k % 2 == 0) 0.1f else 0.05f), o, Offset(o.x + h * 1.4f * cos(a).toFloat(), o.y + h * 1.4f * sin(a).toFloat()), (if (k % 2 == 0) 3f else 1f) * u)
        }
        val pl = 14 * u
        drawRect(t(ORO, oscuro), Offset(pl * 0.5f, pl * 0.5f), Size(w - pl, h - pl), style = Stroke(1.6f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.5f), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(0.8f * u))
        escuadrasPared(w, h, pl * 0.5f, 34 * u, t(ORO, oscuro))
    }

    override fun tintaInterior(oscuro: Boolean) = CREMA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(TELEGRAMA.copy(alpha = 0.5f))
        // La cabecera verde y oro, y las esquinas escalonadas.
        drawRect(VERDE.copy(alpha = 0.8f), Offset.Zero, Size(w, 3 * u * g))
        drawRect(ORO, Offset(0f, 3 * u * g), Size(w, 1.2f * u * g))
        val e = 9 * u * g; val m = 4 * u * g
        listOf(Offset(m, m + 4 * u * g) to Offset(1f, 1f), Offset(w - m, m + 4 * u * g) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f)).forEach { (o, s) ->
            val p = Path().apply {
                moveTo(o.x + s.x * e, o.y); lineTo(o.x, o.y); lineTo(o.x, o.y + s.y * e)
                moveTo(o.x + s.x * e * 0.6f, o.y + s.y * 2.5f * u * g); lineTo(o.x + s.x * 2.5f * u * g, o.y + s.y * 2.5f * u * g); lineTo(o.x + s.x * 2.5f * u * g, o.y + s.y * e * 0.6f)
            }
            drawPath(p, ORO_OSC.copy(alpha = 0.7f), style = Stroke(0.9f * u * g))
        }
    }

    override val margenes = MargenesFicha(14.dp, 14.dp, 14.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.92f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un rombo de oro macizo.
            "hecho" -> {
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.46f); lineTo(c.x + s * 0.34f, c.y); lineTo(c.x, c.y + s * 0.46f); lineTo(c.x - s * 0.34f, c.y); close() }, ORO)
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.24f); lineTo(c.x + s * 0.18f, c.y); lineTo(c.x, c.y + s * 0.24f); lineTo(c.x - s * 0.18f, c.y); close() }, ORO_OSC, style = Stroke(s * 0.05f))
            }
            "convence" -> abanico(Offset(c.x, c.y + s * 0.26f), s * 0.48f, ORO, ORO_OSC)
            "piensa" -> listOf(-0.16f, 0.12f).forEach { dy -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y + dy * s - s * 0.12f); lineTo(c.x, c.y + dy * s + s * 0.14f); lineTo(c.x + s * 0.36f, c.y + dy * s - s * 0.12f) }, VERDE, style = Stroke(s * 0.1f)) }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** La llave de la habitación, con su placa granate. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        llave(Offset(c.x - s * 0.1f, c.y - s * 0.5f), s * 0.9f, ORO_OSC)
        drawRoundRect(GRANATE, Offset(c.x + s * 0.04f, c.y - s * 0.12f), Size(s * 0.3f, s * 0.4f), CornerRadius(s * 0.12f))
    }

    override val desorden = 1.5f
}

/**
 * URSS: la kartoteka metálica de la biblioteca. Un mueble de acero esmaltado gris verdoso con muchos cajoncitos; arriba, un
 * cartel constructivista en la pared, una pila de libros y una lámpara de pantalla verde encendida; abajo, el zócalo y un
 * parquet en espiga. Cada tema es un cajón de la kartoteka con su portaetiquetas de latón, su tirador y el botón de la varilla
 * que traba las fichas; los más llenos quedan entreabiertos con las fichas y sus pestañas. Por dentro, el cajón de acero con
 * la varilla al centro. Las fichas son fichas de catálogo con el agujero para la varilla abajo.
 */
object IdeasUrss : DisenoIdeas {
    private val ACERO = Color(0xFF7A8478)
    private val ACERO_LUZ = Color(0xFF9AA498)
    private val ACERO_OSC = Color(0xFF4E5650)
    private val LATON = Color(0xFFB89A50)
    private val LATON_OSC = Color(0xFF7A6430)
    private val ROJO = Color(0xFFC1121F)
    private val NEGRO = Color(0xFF161414)
    private val CARTULINA = Color(0xFFEFE6D2)
    private val CARTULINA_OSC = Color(0xFFD8CCB0)
    private val PARQUET = Color(0xFF8A6438)
    private val PARQUET_OSC = Color(0xFF5E4024)
    private val VERDE_LAMPARA = Color(0xFF2E5E3A)
    private val LUZ = Color(0xFFF2DC8A)
    private val TINTA = Color(0xFF161414)
    private val CARTEL = Color(0xFFE8DCC0)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 1.25f
    override val corona = 0.32f
    override val base = 0.12f
    override val lado = 0.065f
    override val hueco = 0.022f

    private const val SALIDA = 0.16f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cartel constructivista en la pared, a la izquierda: una franja roja en diagonal y un círculo negro.
        val cartel = Rect(w * 0.06f, d.corona.top + ch * 0.06f, w * 0.3f, d.corona.bottom - 14 * u)
        drawRect(t(CARTEL, oscuro), cartel.topLeft, cartel.size)
        clipRect(cartel.left, cartel.top, cartel.right, cartel.bottom) {
            rotate(-28f, cartel.center) { drawRect(t(ROJO, oscuro), Offset(cartel.left - cartel.width * 0.3f, cartel.center.y - cartel.height * 0.12f), Size(cartel.width * 1.6f, cartel.height * 0.22f)) }
            drawCircle(t(NEGRO, oscuro), cartel.width * 0.2f, Offset(cartel.left + cartel.width * 0.68f, cartel.top + cartel.height * 0.3f))
            drawRect(t(NEGRO, oscuro), Offset(cartel.left + cartel.width * 0.1f, cartel.bottom - cartel.height * 0.2f), Size(cartel.width * 0.6f, cartel.height * 0.06f))
        }
        drawRect(Color.Black.copy(alpha = 0.25f), cartel.topLeft, cartel.size, style = Stroke(1f * u))
        // La tapa del mueble; encima, una pila de libros y la lámpara de pantalla verde encendida.
        val tapa = Rect(0f, d.corona.bottom - 12 * u, w, d.corona.bottom - 4 * u)
        drawRect(t(ACERO_LUZ, oscuro), tapa.topLeft, tapa.size)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tapa.bottom), Size(w, 2 * u))
        listOf(Color(0xFF8A2A24), Color(0xFF2E3E5A), Color(0xFF5A5A3A), Color(0xFFB08A4A)).forEachIndexed { i, col ->
            drawRect(t(col, oscuro), Offset(w * (0.44f + 0.008f * i), tapa.top - (i + 1) * ch * 0.07f), Size(w * (0.18f - 0.015f * i), ch * 0.065f))
        }
        val lb = Offset(w * 0.8f, tapa.top)
        mancha(Offset(lb.x, lb.y - ch * 0.15f), ch * (if (oscuro) 0.9f else 0.6f), LUZ.copy(alpha = if (oscuro) 0.3f else 0.22f))
        drawRect(t(LATON, oscuro), Offset(lb.x - ch * 0.14f, lb.y - 4 * u), Size(ch * 0.28f, 4 * u))
        drawLine(t(LATON, oscuro), Offset(lb.x, lb.y - 4 * u), Offset(lb.x, lb.y - ch * 0.42f), 2 * u)
        val pantalla = Rect(lb.x - ch * 0.26f, lb.y - ch * 0.56f, lb.x + ch * 0.26f, lb.y - ch * 0.38f)
        drawArc(t(VERDE_LAMPARA, oscuro), 180f, 180f, true, Offset(pantalla.left, pantalla.top), Size(pantalla.width, pantalla.height * 2))
        drawLine(LUZ, Offset(pantalla.left + 3 * u, pantalla.bottom), Offset(pantalla.right - 3 * u, pantalla.bottom), 2 * u)
        // El cuerpo de acero esmaltado, con el rebaje de cada cajón.
        val cuerpo = Rect(w * 0.015f, d.corona.bottom - 4 * u, w * 0.985f, d.base.top + d.base.height * 0.4f)
        drawRect(Brush.horizontalGradient(listOf(t(ACERO_OSC, oscuro), t(ACERO, oscuro), t(ACERO_LUZ, oscuro), t(ACERO, oscuro)), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size)
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.55f), Offset(r.left - 1.2f * u, r.top - 1.2f * u), Size(r.width + 2.4f * u, r.height + 2.4f * u)) }
        drawRect(t(ACERO_OSC, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(1.6f * u))
        // El parquet en espiga.
        val piso = Rect(0f, cuerpo.bottom, w, d.base.bottom)
        drawRect(t(PARQUET_OSC, oscuro), piso.topLeft, piso.size)
        clipRect(piso.left, piso.top, piso.right, piso.bottom) {
            val l = 16 * u; val a = 5 * u
            var x = -l; var k = 0
            while (x < w + l) {
                rotate(if (k % 2 == 0) 45f else -45f, Offset(x, piso.center.y)) { drawRect(t(mezcla(PARQUET_OSC, PARQUET, 0.5f + 0.5f * azar(3, k)), oscuro), Offset(x - l / 2, piso.center.y - a / 2), Size(l, a - 0.6f * u)) }
                x += a * 1.42f; k++
            }
        }
        drawRect(t(ACERO_OSC, oscuro), Offset(cuerpo.left + 6 * u, cuerpo.bottom), Size(cuerpo.width - 12 * u, 4 * u))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(CARTULINA, oscuro), r.topLeft, r.size)
            drawRect(t(LATON, oscuro), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u), style = Stroke(2 * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Los cajones más llenos quedan entreabiertos y asoman las fichas, con alguna pestaña roja.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF101210), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 7)
            for (i in 0 until k) {
                val y0 = arriba * (0.15f + 0.08f * i).coerceAtMost(0.8f)
                drawRect(t(if (i % 2 == 0) CARTULINA else CARTULINA_OSC, oscuro), Offset(w * 0.08f, y0), Size(w * 0.84f, arriba + 4 * u - y0))
                drawLine(Color.Black.copy(alpha = 0.2f), Offset(w * 0.08f, y0), Offset(w * 0.92f, y0), 0.6f * u)
                if (i % 3 == 0) drawRect(t(ROJO, oscuro), Offset(w * (0.2f + 0.18f * (i % 4)), y0 - 3 * u), Size(w * 0.12f, 3.5f * u))
            }
        }
        // El frente de acero esmaltado.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRoundRect(t(ACERO, oscuro), f.topLeft, f.size, CornerRadius(2 * u))
        bisel(f, Color.White.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.35f), 1.2f * u)
        // El portaetiquetas de latón (vacío en un lugar libre) con su tarjeta.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        if (!c.vacio) drawRect(t(CARTULINA, oscuro), r.topLeft, r.size)
        drawPath(Path().apply { moveTo(r.left - 2 * u, r.top - 2 * u); lineTo(r.left - 2 * u, r.bottom + 2 * u); lineTo(r.right + 2 * u, r.bottom + 2 * u); lineTo(r.right + 2 * u, r.top - 2 * u) }, t(LATON, oscuro), style = Stroke(2.2f * u))
        listOf(r.left - 5 * u, r.right + 5 * u).forEach { x -> drawCircle(t(LATON_OSC, oscuro), 1.2f * u, Offset(x, r.center.y)) }
        // El tirador y el botón de la varilla.
        val ty = h * 0.72f + baja
        drawRoundRect(t(LATON, oscuro), Offset(w * 0.32f, ty - 2.5f * u), Size(w * 0.36f, 5 * u), CornerRadius(2.5f * u))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.32f, ty + 1.5f * u), Size(w * 0.36f, 1.5f * u), CornerRadius(1 * u))
        val boton = Offset(w / 2, h * 0.88f + baja)
        drawCircle(t(LATON_OSC, oscuro), 3.4f * u, boton)
        drawCircle(t(LATON, oscuro), 2.2f * u, Offset(boton.x - 0.5f * u, boton.y - 0.5f * u))
    }

    override val rotulo = Rect(0.16f, 0.14f, 0.84f, 0.56f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.russo
    override val salida = SALIDA
    override val placa = Rect(0.24f, 0.04f, 0.76f, 0.38f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo del cajón de acero, con la varilla que corre al centro.
        drawRect(t(ACERO_OSC, oscuro))
        for (k in 0 until 30) drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, h * azar(31, k)), Offset(w, h * azar(31, k) + 2 * u), 1f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(w / 2 - 6 * u, 0f), Size(12 * u, h))
        drawRect(t(ACERO_LUZ, oscuro), Offset(w / 2 - 2.5f * u, 0f), Size(5 * u, h))
        drawLine(Color.White.copy(alpha = 0.4f), Offset(w / 2 - 1 * u, 0f), Offset(w / 2 - 1 * u, h), 1f * u)
        drawCircle(t(LATON, oscuro), 7 * u, Offset(w / 2, h - 22 * u))
        // Las paredes del cajón, de acero más claro.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(ACERO, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = CARTULINA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    /** La ficha de catálogo, con el agujero de la varilla abajo al centro. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        fillType = PathFillType.EvenOdd
        addRect(Rect(0f, 0f, s.width, s.height))
        val r = minOf(s.width, s.height).coerceAtMost(s.width * 0.3f) * 0.03f
        addOval(Rect(s.width / 2 - r, s.height - r * 3.6f, s.width / 2 + r, s.height - r * 1.6f))
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(CARTULINA.copy(alpha = 0.4f))
        // El filete rojo de arriba, la línea vertical del margen y el refuerzo del agujero.
        drawLine(ROJO.copy(alpha = 0.7f), Offset(0f, 3 * u * g), Offset(w, 3 * u * g), 1f * u * g)
        drawLine(ROJO.copy(alpha = 0.35f), Offset(10 * u * g, 0f), Offset(10 * u * g, h), 0.8f * u)
        val r = minOf(w, h).coerceAtMost(w * 0.3f) * 0.03f
        drawCircle(TINTA.copy(alpha = 0.25f), r * 1.5f, Offset(w / 2, h - r * 2.6f), style = Stroke(0.8f * u))
    }

    override val margenes = MargenesFicha(12.dp, 16.dp, 24.dp, 22.dp, 28.dp, 46.dp)
    override val proporcionFicha = 1.25f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.82f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello del plan cumplido.
            "hecho" -> selloHecho(ROJO, p.color, cuadrado = true)
            "convence" -> drawPath(estrella(c, s * 0.48f, 5, 0.42f), ROJO)
            // Un lápiz.
            "piensa" -> rotate(-45f, c) {
                drawRect(Color(0xFF3A3A3A), Offset(c.x - s * 0.36f, c.y - s * 0.09f), Size(s * 0.56f, s * 0.18f))
                drawPath(Path().apply { moveTo(c.x + s * 0.2f, c.y - s * 0.09f); lineTo(c.x + s * 0.42f, c.y); lineTo(c.x + s * 0.2f, c.y + s * 0.09f); close() }, Color(0xFFD8B880))
                drawRect(ROJO, Offset(c.x - s * 0.44f, c.y - s * 0.09f), Size(s * 0.08f, s * 0.18f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Una pestaña roja de separador. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.46f, c.y + s * 0.36f); lineTo(c.x - s * 0.3f, c.y - s * 0.36f); lineTo(c.x + s * 0.3f, c.y - s * 0.36f); lineTo(c.x + s * 0.46f, c.y + s * 0.36f); close() }, ROJO)
        drawLine(CARTULINA, Offset(c.x - s * 0.18f, c.y), Offset(c.x + s * 0.18f, c.y), s * 0.08f)
    }

    override val desorden = 0f
}
