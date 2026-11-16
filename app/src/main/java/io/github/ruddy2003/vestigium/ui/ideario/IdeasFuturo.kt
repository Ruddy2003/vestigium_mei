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
//  Futuro
// =====================================================================

/**
 * Colonos: la cajonera del almacén general. Tablas de pino rescatadas de distintos tonos (alguna pintada de azul o de
 * ladrillo) con parches de lata clavados; arriba, el letrero pintado del almacén, un quinqué colgado y una balanza de
 * platillos; abajo, el mostrador con una gaceta clavada. Cada tema es un cajetín con su tirador de cordel y una etiqueta de
 * papel de gaceta pegada con cinta; los más llenos quedan entreabiertos con sus recortes. Por dentro, el cajón forrado de
 * gaceta, con tachuelas y un cerco de café. Las fichas son recortes cortados a tijera con su cinta.
 */
object IdeasColonos : DisenoIdeas {
    private val PINO = Color(0xFFB08A5A)
    private val PINO_LUZ = Color(0xFFCBA676)
    private val PINO_OSC = Color(0xFF7A5A36)
    private val LADRILLO = Color(0xFF9E2B25)
    private val MARINO = Color(0xFF3E4B60)
    private val GACETA = Color(0xFFEDE4CC)
    private val LATA = Color(0xFFA0A4A0)
    private val TINTA = Color(0xFF1E1A16)
    private val CINTA = Color(0xFFE8D48A)
    private val LUZ = Color(0xFFF2C46B)
    private val LATON = Color(0xFFB89A50)
    private val CUERDA = Color(0xFFC9A86A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 1.2f
    override val corona = 0.32f
    override val base = 0.15f
    override val lado = 0.06f
    override val hueco = 0.025f

    private const val SALIDA = 0.16f

    /** Un parche de lata clavado, con sus clavos. */
    private fun DrawScope.parche(r: Rect, oscuro: Boolean) {
        val u = density
        drawRect(t(LATA, oscuro), r.topLeft, r.size)
        drawLine(Color.White.copy(alpha = 0.3f), r.topLeft, Offset(r.right, r.top), 1f * u)
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), Offset(r.right, r.bottom)).forEach { o -> drawCircle(Color.Black.copy(alpha = 0.6f), 1 * u, Offset(o.x + (if (o.x < r.center.x) 2.5f else -2.5f) * u, o.y + (if (o.y < r.center.y) 2.5f else -2.5f) * u)) }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo: tablas de pino rescatadas, cada una de su tono (alguna pintada), con parches de lata.
        val cuerpo = Rect(w * 0.015f, d.corona.bottom - 6 * u, w * 0.985f, d.base.top + d.base.height * 0.3f)
        val nt = 9
        for (k in 0 until nt) {
            val r = Rect(cuerpo.left + cuerpo.width * k / nt, cuerpo.top, cuerpo.left + cuerpo.width * (k + 1) / nt, cuerpo.bottom)
            val tono = when (k) { 2 -> mezcla(MARINO, PINO, 0.35f); 6 -> mezcla(LADRILLO, PINO, 0.4f); else -> mezcla(PINO_OSC, PINO_LUZ, 0.3f + 0.6f * azar(11, k)) }
            drawRect(t(tono, oscuro), r.topLeft, r.size)
            vetas(r, PINO_OSC.copy(alpha = 0.35f), 20 + k, 0.6f, vertical = true, grosor = 0.5f, ondulacion = 0.5f)
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(r.left, r.top), Offset(r.left, r.bottom), 1f * u)
        }
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.5f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u)) }
        parche(Rect(cuerpo.left + 4 * u, cuerpo.top + cuerpo.height * 0.4f, cuerpo.left + w * 0.05f, cuerpo.top + cuerpo.height * 0.4f + 22 * u), oscuro)
        parche(Rect(cuerpo.right - w * 0.05f, cuerpo.top + cuerpo.height * 0.15f, cuerpo.right - 4 * u, cuerpo.top + cuerpo.height * 0.15f + 18 * u), oscuro)
        // El letrero pintado del almacén, con su filete y sus letras de lejos.
        val letrero = Rect(w * 0.22f, d.corona.top + ch * 0.36f, w * 0.78f, d.corona.bottom - 10 * u)
        drawRect(t(LADRILLO, oscuro), letrero.topLeft, letrero.size)
        drawRect(t(MARINO, oscuro), letrero.topLeft, letrero.size, style = Stroke(4 * u))
        drawRect(t(GACETA, oscuro), Offset(letrero.left + 7 * u, letrero.top + 7 * u), Size(letrero.width - 14 * u, letrero.height - 14 * u), style = Stroke(0.8f * u))
        var lx = letrero.left + letrero.width * 0.14f; var q = 0
        while (lx < letrero.right - letrero.width * 0.14f) {
            if (q % 6 != 5) drawRect(t(GACETA, oscuro), Offset(lx, letrero.center.y - letrero.height * 0.16f), Size(letrero.width * 0.04f, letrero.height * 0.32f))
            lx += letrero.width * 0.055f; q++
        }
        listOf(letrero.left + 10 * u, letrero.right - 10 * u).forEach { x -> drawLine(t(PINO_OSC, oscuro), Offset(x, letrero.bottom), Offset(x, d.corona.bottom - 6 * u), 3 * u) }
        // El quinqué colgado, con su luz.
        val q0 = Offset(w * 0.1f, d.corona.top + ch * 0.12f)
        drawLine(t(TINTA, oscuro), Offset(q0.x, d.corona.top), q0, 1 * u)
        mancha(Offset(q0.x, q0.y + ch * 0.22f), ch * (if (oscuro) 0.7f else 0.5f), LUZ.copy(alpha = if (oscuro) 0.35f else 0.25f))
        drawOval(Color.White.copy(alpha = 0.45f), Offset(q0.x - ch * 0.07f, q0.y + ch * 0.06f), Size(ch * 0.14f, ch * 0.22f))
        drawOval(LUZ, Offset(q0.x - ch * 0.02f, q0.y + ch * 0.13f), Size(ch * 0.04f, ch * 0.08f))
        drawRect(t(LATON, oscuro), Offset(q0.x - ch * 0.08f, q0.y + ch * 0.27f), Size(ch * 0.16f, ch * 0.08f))
        // La balanza de platillos sobre el mueble.
        val bc = Offset(w * 0.9f, d.corona.bottom - 6 * u)
        drawLine(t(LATON, oscuro), bc, Offset(bc.x, bc.y - ch * 0.38f), 2.4f * u)
        drawLine(t(LATON, oscuro), Offset(bc.x - ch * 0.22f, bc.y - ch * 0.34f), Offset(bc.x + ch * 0.22f, bc.y - ch * 0.38f), 2 * u)
        listOf(-1f, 1f).forEach { s ->
            val px = bc.x + s * ch * 0.22f; val py = bc.y - ch * (if (s < 0) 0.34f else 0.38f)
            drawLine(t(LATON, oscuro), Offset(px, py), Offset(px - ch * 0.06f, py + ch * 0.16f), 0.8f * u)
            drawLine(t(LATON, oscuro), Offset(px, py), Offset(px + ch * 0.06f, py + ch * 0.16f), 0.8f * u)
            drawArc(t(LATON, oscuro), 0f, 180f, true, Offset(px - ch * 0.08f, py + ch * 0.12f), Size(ch * 0.16f, ch * 0.08f))
        }
        // El mostrador, con una gaceta clavada al frente.
        val most = Rect(0f, cuerpo.bottom, w, d.base.bottom)
        drawRect(t(PINO_LUZ, oscuro), Offset(0f, most.top), Size(w, 7 * u))
        drawRect(t(PINO, oscuro), Offset(0f, most.top + 7 * u), Size(w, most.height - 7 * u))
        for (k in 1 until 8) drawLine(Color.Black.copy(alpha = 0.3f), Offset(w * k / 8f, most.top + 7 * u), Offset(w * k / 8f, most.bottom), 1f * u)
        val gz = Rect(w * 0.06f, most.top + 10 * u, w * 0.2f, most.bottom - 4 * u)
        drawRect(t(GACETA, oscuro), gz.topLeft, gz.size)
        for (j in 0 until 5) drawLine(t(TINTA, oscuro).copy(alpha = 0.3f), Offset(gz.left + 3 * u, gz.top + 6 * u + j * 4 * u), Offset(gz.right - 3 * u, gz.top + 6 * u + j * 4 * u), 0.8f * u)
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(GACETA, oscuro), r.topLeft, r.size)
            drawRect(t(CINTA, oscuro).copy(alpha = 0.8f), Offset(r.left - 3 * u, r.top - 2 * u), Size(12 * u, 5 * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Los cajetines más llenos quedan entreabiertos y asoman los recortes.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF120C06), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 6)
            for (i in 0 until k) {
                val x = w * (0.08f + 0.84f * (i + 0.5f) / k) - w * 0.08f
                val y0 = arriba * (0.1f + 0.5f * azar(c.semilla, i)) + 1 * u
                rotate(-6f + 12f * azar(c.semilla, i + 9), Offset(x, arriba)) { drawRect(t(GACETA, oscuro), Offset(x, y0), Size(w * 0.16f, arriba + 4 * u - y0)) }
            }
        }
        // El frente de pino, de su propio tono, con su veta.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(t(mezcla(PINO_OSC, PINO_LUZ, 0.3f + 0.6f * azar(c.semilla, 77)), oscuro), f.topLeft, f.size)
        vetas(f, PINO_OSC.copy(alpha = 0.4f), c.semilla, 1f, grosor = 0.6f, ondulacion = 0.6f)
        bisel(f, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.35f), 1.2f * u)
        // La etiqueta de gaceta pegada con cinta.
        if (!c.vacio) {
            val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
            drawRect(t(GACETA, oscuro), r.topLeft, r.size)
            rotate(-20f, Offset(r.left, r.top)) { drawRect(t(CINTA, oscuro).copy(alpha = 0.75f), Offset(r.left - 6 * u, r.top - 3 * u), Size(16 * u, 6 * u)) }
            rotate(20f, Offset(r.right, r.top)) { drawRect(t(CINTA, oscuro).copy(alpha = 0.75f), Offset(r.right - 10 * u, r.top - 3 * u), Size(16 * u, 6 * u)) }
        }
        // El tirador de cordel.
        val tc = Offset(w / 2, h * 0.82f + baja)
        drawCircle(t(PINO_OSC, oscuro), 2 * u, Offset(tc.x - w * 0.08f, tc.y - 2 * u))
        drawCircle(t(PINO_OSC, oscuro), 2 * u, Offset(tc.x + w * 0.08f, tc.y - 2 * u))
        drawArc(t(CUERDA, oscuro), 0f, 180f, false, Offset(tc.x - w * 0.08f, tc.y - 6 * u), Size(w * 0.16f, 12 * u), style = Stroke(2 * u))
    }

    override val rotulo = Rect(0.14f, 0.18f, 0.86f, 0.64f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.fell
    override val salida = SALIDA
    override val placa = Rect(0.32f, 0.36f, 0.68f, 0.86f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo forrado de gaceta: columnas de renglones, tachuelas en las esquinas y un cerco de café.
        val g = if (oscuro) mezcla(GACETA, Color.Black, 0.58f) else GACETA
        drawRect(g)
        val col = 3
        val cw = w / col
        for (j in 0 until col) {
            drawRect(TINTA.copy(alpha = 0.35f), Offset(cw * j + 14 * u, 24 * u), Size(cw - 28 * u, 7 * u))
            var y = 40 * u
            while (y < h - 16 * u) { drawLine(TINTA.copy(alpha = 0.12f), Offset(cw * j + 14 * u, y), Offset(cw * (j + 1) - 14 * u, y), 1.2f * u); y += 6 * u }
            if (j > 0) drawLine(TINTA.copy(alpha = 0.2f), Offset(cw * j, 20 * u), Offset(cw * j, h - 14 * u), 0.8f * u)
        }
        drawCircle(Color(0xFF6A4020).copy(alpha = 0.25f), 32 * u, Offset(w * 0.7f, h * 0.65f), style = Stroke(4 * u))
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(PINO, oscuro), r.topLeft, r.size) }
        listOf(Offset(pl + 8 * u, pl + 8 * u), Offset(w - pl - 8 * u, pl + 8 * u), Offset(pl + 8 * u, h - pl - 8 * u), Offset(w - pl - 8 * u, h - pl - 8 * u)).forEach { drawCircle(t(LATON, oscuro), 3 * u, it) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) GACETA else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 12, 0.018f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(GACETA.copy(alpha = 0.45f))
        // Los filetes de columna arriba y abajo, y una tira de cinta amarillenta.
        drawLine(TINTA.copy(alpha = 0.4f), Offset(8 * u, 6 * u * g), Offset(w - 8 * u, 6 * u * g), 0.8f * u)
        drawLine(TINTA.copy(alpha = 0.3f), Offset(8 * u, h - 6 * u * g), Offset(w - 8 * u, h - 6 * u * g), 0.6f * u)
        val c = Offset(w * (0.4f + 0.2f * azar(semilla, 1)), 2 * u * g)
        rotate(-4f + 8f * azar(semilla, 2), c) { drawRect(CINTA.copy(alpha = 0.6f), Offset(c.x - 14 * u * g, c.y - 2 * u * g), Size(28 * u * g, 7 * u * g)) }
    }

    override val margenes = MargenesFicha(14.dp, 14.dp, 13.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.9f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(MARINO, p.color)
            // Un martillo.
            "convence" -> {
                drawLine(PINO_OSC, Offset(c.x - s * 0.3f, c.y + s * 0.4f), Offset(c.x + s * 0.12f, c.y - s * 0.12f), s * 0.12f, StrokeCap.Round)
                rotate(45f, Offset(c.x + s * 0.16f, c.y - s * 0.16f)) { drawRect(MARINO, Offset(c.x - s * 0.04f, c.y - s * 0.26f), Size(s * 0.4f, s * 0.2f)) }
            }
            // Un quinqué.
            "piensa" -> {
                drawOval(LUZ, Offset(c.x - s * 0.18f, c.y - s * 0.4f), Size(s * 0.36f, s * 0.5f))
                drawRect(LATON, Offset(c.x - s * 0.24f, c.y + s * 0.1f), Size(s * 0.48f, s * 0.24f))
                drawCircle(Color(0xFFE07A2A), s * 0.07f, Offset(c.x, c.y - s * 0.1f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(LADRILLO)
            else -> {}
        }
    }

    /** Una tachuela. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(Color.Black.copy(alpha = 0.25f), s * 0.36f, Offset(c.x + s * 0.06f, c.y + s * 0.06f))
        drawCircle(LATON, s * 0.36f, c)
        drawCircle(Color.White.copy(alpha = 0.45f), s * 0.12f, Offset(c.x - s * 0.1f, c.y - s * 0.1f))
    }

    override val desorden = 2.5f
}

/**
 * Espacio: la sala de control. Arriba, una ventana panorámica a la nebulosa con un planeta anillado, entre dos pantallas de
 * telemetría; los racks oscuros llevan sus rieles atornillados y rejillas; abajo, el piso con luces de guía. Cada tema es un
 * módulo de rack con sus asas, una fila de LED de estado (más encendidos cuanto más lleno), una onda en su pantallita y el
 * tema en un visor; al abrirlo, el módulo sale. Por dentro, el panel con su retícula. Las fichas son registros de misión en
 * vidrio oscuro con corchetes en las esquinas y una barra de progreso.
 */
object IdeasEspacio : DisenoIdeas {
    private val VACIO = Color(0xFF050A18)
    private val PANEL = Color(0xFF141C30)
    private val PANEL_LUZ = Color(0xFF2A3654)
    private val PANEL_OSC = Color(0xFF0C1222)
    private val AZUL = Color(0xFF8AB4FF)
    private val NARANJA = Color(0xFFFFB86B)
    private val VERDE = Color(0xFF5CFFB0)
    private val TEXTO = Color(0xFFE8ECF8)
    private val NEBULOSA = Color(0xFF7A3AA8)
    private val ROSA = Color(0xFFC85AA0)
    private val METAL = Color(0xFF6E7BA0)
    private val PLANETA = Color(0xFFD8A06A)

    override val cols = 2
    override val proporcion = 1.8f
    override val corona = 0.38f
    override val base = 0.09f
    override val lado = 0.075f
    override val hueco = 0.04f

    private const val SALIDA = 0.1f

    private fun DrawScope.corchetes(r: Rect, l: Float, color: Color, grosor: Float) {
        listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.left, r.bottom) to Offset(1f, -1f), Offset(r.right, r.bottom) to Offset(-1f, -1f)).forEach { (o, s) ->
            drawLine(color, o, Offset(o.x + s.x * l, o.y), grosor)
            drawLine(color, o, Offset(o.x, o.y + s.y * l), grosor)
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        drawRect(PANEL_OSC, Offset(0f, d.corona.top), Size(w, d.base.bottom - d.corona.top))
        // La ventana panorámica: estrellas, la nebulosa y un planeta con su anillo.
        val ven = Rect(w * 0.2f, d.corona.top + ch * 0.06f, w * 0.8f, d.corona.bottom - ch * 0.14f)
        drawRoundRect(PANEL_LUZ, Offset(ven.left - 5 * u, ven.top - 5 * u), Size(ven.width + 10 * u, ven.height + 10 * u), CornerRadius(14 * u))
        drawRoundRect(VACIO, ven.topLeft, ven.size, CornerRadius(10 * u))
        clipRect(ven.left, ven.top, ven.right, ven.bottom) {
            mancha(Offset(ven.left + ven.width * 0.35f, ven.center.y), ven.width * 0.4f, NEBULOSA.copy(alpha = 0.45f))
            mancha(Offset(ven.left + ven.width * 0.55f, ven.top + ven.height * 0.3f), ven.width * 0.25f, ROSA.copy(alpha = 0.3f))
            for (k in 0 until 50) drawCircle(Color.White.copy(alpha = 0.4f + 0.6f * azar(91, k)), (0.5f + 1.2f * azar(92, k)) * u, Offset(ven.left + ven.width * azar(93, k), ven.top + ven.height * azar(94, k)))
            val pc = Offset(ven.left + ven.width * 0.74f, ven.top + ven.height * 0.58f)
            val pr = ven.height * 0.26f
            drawArc(AZUL.copy(alpha = 0.5f), 180f, 180f, false, Offset(pc.x - pr * 1.8f, pc.y - pr * 0.4f), Size(pr * 3.6f, pr * 0.8f), style = Stroke(2 * u))
            drawCircle(PLANETA, pr, pc)
            drawCircle(Color.Black.copy(alpha = 0.35f), pr, Offset(pc.x + pr * 0.35f, pc.y + pr * 0.2f))
            drawArc(AZUL, 0f, 180f, false, Offset(pc.x - pr * 1.8f, pc.y - pr * 0.4f), Size(pr * 3.6f, pr * 0.8f), style = Stroke(2 * u))
        }
        // Las pantallas de telemetría a los lados.
        listOf(Rect(w * 0.03f, ven.top + 4 * u, w * 0.17f, ven.bottom - 4 * u), Rect(w * 0.83f, ven.top + 4 * u, w * 0.97f, ven.bottom - 4 * u)).forEachIndexed { i, r ->
            drawRect(PANEL, r.topLeft, r.size)
            drawRect(AZUL.copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(1 * u))
            val onda = Path().apply { for (k in 0..20) { val x = r.left + r.width * k / 20f; val y = r.top + r.height * (0.3f + 0.12f * sin(k * 0.9f + i * 2f)); if (k == 0) moveTo(x, y) else lineTo(x, y) } }
            drawPath(onda, (if (i == 0) VERDE else AZUL).copy(alpha = 0.8f), style = Stroke(1.2f * u))
            for (k in 0 until 4) drawRect(AZUL.copy(alpha = 0.6f), Offset(r.left + 4 * u, r.top + r.height * 0.55f + k * 6 * u), Size(r.width * (0.3f + 0.5f * azar(95 + i, k)), 2.5f * u))
        }
        // Los racks: los rieles atornillados a los lados y las rejillas.
        val cuerpo = Rect(w * 0.015f, d.corona.bottom - 6 * u, w * 0.985f, d.base.top + 2 * u)
        drawRect(PANEL, cuerpo.topLeft, cuerpo.size)
        listOf(cuerpo.left, cuerpo.right - w * 0.05f).forEach { x ->
            drawRect(METAL, Offset(x, cuerpo.top), Size(w * 0.05f, cuerpo.height))
            var y = cuerpo.top + 6 * u
            while (y < cuerpo.bottom - 4 * u) { drawRect(PANEL_OSC, Offset(x + w * 0.02f, y), Size(w * 0.012f, 3 * u)); y += 9 * u }
        }
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.6f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u)) }
        // El piso con sus luces de guía.
        val piso = Rect(0f, d.base.top + 2 * u, w, d.base.bottom)
        drawRect(PANEL_LUZ, piso.topLeft, piso.size)
        var lx = 8 * u
        while (lx < w) { drawCircle(AZUL, 1.6f * u, Offset(lx, piso.center.y)); mancha(Offset(lx, piso.center.y), 6 * u, AZUL.copy(alpha = 0.4f)); lx += 22 * u }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(PANEL_OSC, r.topLeft, r.size)
            drawRect(AZUL, r.topLeft, r.size, style = Stroke(1 * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // El hueco del rack.
        drawRect(Color(0xFF02040A))
        if (c.vacio) {
            // Un lugar libre: una tapa ciega con su rejilla.
            drawRect(PANEL, Offset(w * 0.04f, h * 0.06f), Size(w * 0.92f, h * 0.88f))
            var y = h * 0.2f
            while (y < h * 0.8f) { drawRoundRect(PANEL_OSC, Offset(w * 0.2f, y), Size(w * 0.6f, 3 * u), CornerRadius(1.5f * u)); y += 7 * u }
            return
        }
        // El módulo, que sale al abrirlo.
        val baja = h * SALIDA * ab
        val e = w * 0.02f * ab
        val m = Rect(w * 0.02f - e, h * 0.04f + baja, w * 0.98f + e, h * 0.98f + baja)
        if (ab > 0f) sombraSuave(Rect(m.left, m.bottom - 4 * u, m.right, m.bottom + 2 * u), 2 * u, 8 * u * ab, 0.4f * ab)
        drawRoundRect(PANEL_LUZ, m.topLeft, m.size, CornerRadius(3 * u))
        bisel(m, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.4f), 1.2f * u)
        // Las asas de los lados.
        listOf(m.left + w * 0.04f, m.right - w * 0.04f - 6 * u).forEach { x ->
            drawRoundRect(METAL, Offset(x, m.top + m.height * 0.2f), Size(6 * u, m.height * 0.6f), CornerRadius(3 * u))
            drawRoundRect(PANEL_OSC, Offset(x + 1.5f * u, m.top + m.height * 0.26f), Size(3 * u, m.height * 0.48f), CornerRadius(1.5f * u))
        }
        // La fila de LED de estado: tantos encendidos como fichas, hasta ocho.
        val k = c.n.coerceIn(0, 8)
        for (i in 0 until 8) {
            val p = Offset(m.left + w * 0.16f + i * w * 0.04f, m.top + m.height * 0.16f)
            if (i < k) { mancha(p, 5 * u, VERDE.copy(alpha = 0.5f)); drawCircle(VERDE, 1.6f * u, p) } else drawCircle(PANEL_OSC, 1.6f * u, p)
        }
        // La pantallita con su onda.
        val pa = Rect(m.right - w * 0.36f, m.top + m.height * 0.08f, m.right - w * 0.14f, m.top + m.height * 0.26f)
        drawRect(PANEL_OSC, pa.topLeft, pa.size)
        val onda = Path().apply { for (j in 0..16) { val x = pa.left + pa.width * j / 16f; val y = pa.center.y + pa.height * 0.3f * sin(j * 0.8f + c.semilla); if (j == 0) moveTo(x, y) else lineTo(x, y) } }
        drawPath(onda, AZUL, style = Stroke(1 * u))
        // El visor con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        drawRect(PANEL_OSC, r.topLeft, r.size)
        drawRect(AZUL.copy(alpha = 0.25f), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(3 * u))
        drawRect(AZUL, r.topLeft, r.size, style = Stroke(1 * u))
    }

    override val rotulo = Rect(0.16f, 0.4f, 0.84f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = TEXTO
    override val letraRotulo: FontFamily = LetraExtra.orbita
    override val salida = SALIDA
    override val placa = Rect(0.3f, 0.12f, 0.7f, 0.88f)
    override fun tintaPlaca(oscuro: Boolean) = TEXTO

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El panel oscuro con unas estrellas tenues, la retícula y los corchetes de las esquinas.
        drawRect(VACIO)
        for (k in 0 until 60) drawCircle(Color.White.copy(alpha = 0.1f + 0.25f * azar(97, k)), (0.5f + 1f * azar(98, k)) * u, Offset(w * azar(99, k), h * azar(100, k)))
        val c = Offset(w / 2, h * 0.45f)
        drawCircle(AZUL.copy(alpha = 0.1f), w * 0.3f, c, style = Stroke(1 * u))
        drawCircle(AZUL.copy(alpha = 0.08f), w * 0.18f, c, style = Stroke(1 * u))
        drawLine(AZUL.copy(alpha = 0.08f), Offset(c.x - w * 0.4f, c.y), Offset(c.x + w * 0.4f, c.y), 1 * u)
        drawLine(AZUL.copy(alpha = 0.08f), Offset(c.x, c.y - w * 0.4f), Offset(c.x, c.y + w * 0.4f), 1 * u)
        corchetes(Rect(10 * u, 10 * u, w - 10 * u, h - 10 * u), 30 * u, AZUL.copy(alpha = 0.6f), 2 * u)
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 18.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(4.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // Los corchetes del visor, una barra de progreso fina abajo y las marcas del día juliano arriba.
        val m = 3 * u * g
        corchetes(Rect(m, m, w - m, h - m), 10 * u * g, AZUL.copy(alpha = 0.7f), 1.2f * u * g)
        val by = h - 6 * u * g
        drawLine(AZUL.copy(alpha = 0.15f), Offset(w * 0.25f, by), Offset(w * 0.75f, by), 2 * u * g)
        drawLine(AZUL, Offset(w * 0.25f, by), Offset(w * (0.25f + 0.5f * (0.2f + 0.8f * azar(semilla, 1))), by), 2 * u * g)
        for (k in 0 until 12) drawLine(AZUL.copy(alpha = 0.25f), Offset(w * 0.3f + k * 4 * u * g, 3 * u * g), Offset(w * 0.3f + k * 4 * u * g, (if (k % 4 == 0) 7 else 5) * u * g), 0.6f * u)
    }

    override val margenes = MargenesFicha(13.dp, 14.dp, 14.dp, 26.dp, 26.dp, 30.dp)
    override val proporcionFicha = 1.1f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(AZUL, p.color)
            "convence" -> drawPath(estrella(c, s * 0.48f, 5, 0.42f), NARANJA)
            // Una órbita con su satélite.
            "piensa" -> {
                rotate(-25f, c) { drawOval(AZUL, Offset(c.x - s * 0.44f, c.y - s * 0.2f), Size(s * 0.88f, s * 0.4f), style = Stroke(s * 0.06f)) }
                drawCircle(AZUL, s * 0.12f, c)
                drawCircle(NARANJA, s * 0.08f, Offset(c.x + s * 0.36f, c.y - s * 0.16f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(NARANJA)
            else -> {}
        }
    }

    /** Un parche de misión. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(PANEL_LUZ, s * 0.46f, c)
        drawCircle(AZUL, s * 0.46f, c, style = Stroke(s * 0.08f))
        drawPath(estrella(c, s * 0.26f, 5, 0.42f), Color.White)
    }

    override val desorden = 0f
}
