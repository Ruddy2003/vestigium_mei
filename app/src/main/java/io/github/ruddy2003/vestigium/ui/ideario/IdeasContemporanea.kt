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
