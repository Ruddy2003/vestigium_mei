package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.pantallas.simboloCalificacion
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Antigüedad
// =====================================================================

/**
 * Roma: el armarium, el armario de nogal de las bibliotecas romanas, con su frontón y una corona de laurel, las puertas
 * abiertas a los lados y el fondo pintado de rojo pompeyano. En cada anaquel hay una capsa de cuero con aros de bronce, con
 * los rollos asomando (más cuanto más llena) y su tabula ansata de bronce con el tema. Por dentro, una pared al fresco.
 * Las fichas son rollos: la hoja de papiro entre sus dos varillas con perillas.
 */
object IdeasRomano : DisenoIdeas {
    private val NOGAL = Color(0xFF5E3C22)
    private val NOGAL_LUZ = Color(0xFF8C6340)
    private val NOGAL_OSC = Color(0xFF331F10)
    private val POMPEYANO = Color(0xFF8E2B1F)
    private val NEGRO = Color(0xFF1E1714)
    private val FILETE = Color(0xFFC9A54A)
    private val ORO = Color(0xFFD4AF37)
    private val ORO_OSC = Color(0xFF8E6A1F)
    private val BRONCE = Color(0xFFD9B26A)
    private val BRONCE_OSC = Color(0xFF7A5524)
    private val CUERO = Color(0xFF7A4A28)
    private val CUERO_OSC = Color(0xFF45280F)
    private val CUERO_LUZ = Color(0xFFA8703F)
    private val PAPIRO = Color(0xFFE9D9A9)
    private val PAPIRO_OSC = Color(0xFFB59B62)
    private val LAUREL = Color(0xFF5E7A3A)
    private val LACRE = Color(0xFFA3172E)
    private val TINTA = Color(0xFF2A1A0C)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 0.9f
    override val corona = 0.22f
    override val base = 0.11f
    override val lado = 0.1f
    override val hueco = 0.035f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val cuerpo = Rect(w * 0.055f, d.corona.bottom, w * 0.945f, d.base.top + d.base.height * 0.3f)
        // El fondo pintado: rojo pompeyano con un filete amarillo alrededor de cada capsa y el zócalo negro.
        drawRect(Brush.verticalGradient(listOf(t(mezcla(POMPEYANO, Color.Black, 0.15f), oscuro), t(POMPEYANO, oscuro), t(mezcla(POMPEYANO, Color.Black, 0.2f), oscuro)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        for (k in 0 until 40) mancha(Offset(cuerpo.left + cuerpo.width * azar(3, k), cuerpo.top + cuerpo.height * azar(4, k)), (6f + 14f * azar(5, k)) * u, Color.Black.copy(alpha = 0.06f))
        d.huecos.forEach { r -> drawRect(t(FILETE, oscuro).copy(alpha = 0.7f), Offset(r.left - 3 * u, r.top - 3 * u), Size(r.width + 6 * u, r.height + 3 * u), style = Stroke(0.9f * u)) }
        drawRect(t(NEGRO, oscuro), Offset(cuerpo.left, cuerpo.bottom - d.separacion * 0.9f), Size(cuerpo.width, d.separacion * 0.9f))
        // Los anaqueles, bajo cada fila, con su canto claro y la sombra.
        d.filas.forEach { f ->
            val r = Rect(cuerpo.left, f.bottom, cuerpo.right, f.bottom + maxOf(d.separacion * 0.5f, 5 * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
            drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), r.top, r.bottom), r.topLeft, r.size)
            drawLine(Color.White.copy(alpha = 0.18f), Offset(r.left, r.top), Offset(r.right, r.top), 1f * u)
        }
        // Los montantes y las puertas abiertas, de lado, con su tablero y su argolla.
        val poste = w * 0.04f
        listOf(cuerpo.left to 1f, cuerpo.right to -1f).forEach { (x, s) ->
            val r = if (s > 0) Rect(x, cuerpo.top, x + poste, cuerpo.bottom) else Rect(x - poste, cuerpo.top, x, cuerpo.bottom)
            drawRect(Brush.horizontalGradient(listOf(t(NOGAL_OSC, oscuro), t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), r.left, r.right), r.topLeft, r.size)
            vetas(r, NOGAL_OSC.copy(alpha = 0.35f), if (s > 0) 7 else 8, 1f, vertical = true, grosor = 0.6f, ondulacion = 0.4f)
            val px = if (s > 0) x else x
            val ancho = w * 0.055f
            val puerta = Path().apply {
                moveTo(px, cuerpo.top + 4 * u); lineTo(px - s * ancho, cuerpo.top - 6 * u); lineTo(px - s * ancho, cuerpo.bottom + 6 * u); lineTo(px, cuerpo.bottom - 4 * u); close()
            }
            drawPath(puerta, Brush.horizontalGradient(listOf(t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), px - ancho, px + ancho))
            val tablero = Path().apply {
                moveTo(px - s * ancho * 0.25f, cuerpo.top + 10 * u); lineTo(px - s * ancho * 0.75f, cuerpo.top + 4 * u)
                lineTo(px - s * ancho * 0.75f, cuerpo.bottom - 2 * u); lineTo(px - s * ancho * 0.25f, cuerpo.bottom - 10 * u); close()
            }
            drawPath(tablero, Color.Black.copy(alpha = 0.25f), style = Stroke(1f * u))
            drawCircle(t(BRONCE, oscuro), 2.6f * u, Offset(px - s * ancho * 0.5f, (cuerpo.top + cuerpo.bottom) / 2), style = Stroke(1.2f * u))
        }
        // El entablamento con sus dentículos y el frontón con su corona de laurel; acroteras en las puntas.
        val ch = d.corona.height
        val arq = Rect(w * 0.02f, d.corona.bottom - ch * 0.3f, w * 0.98f, d.corona.bottom)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(arq.left + 3 * u, arq.bottom), Size(arq.width - 6 * u, 3 * u))
        drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), arq.top, arq.bottom), arq.topLeft, arq.size)
        val dent = (arq.width / (7 * u)).toInt()
        for (k in 0 until dent) drawRect(t(NOGAL_OSC, oscuro), Offset(arq.left + k * arq.width / dent + 1.5f * u, arq.top + arq.height * 0.55f), Size(arq.width / dent - 3 * u, arq.height * 0.3f))
        val frontonBase = arq.top
        val apice = Offset(w / 2, d.corona.top + ch * 0.1f)
        val fronton = Path().apply { moveTo(w * 0.04f, frontonBase); lineTo(apice.x, apice.y); lineTo(w * 0.96f, frontonBase); close() }
        drawPath(fronton, Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), apice.y, frontonBase))
        val timpano = Path().apply { moveTo(w * 0.12f, frontonBase - 2 * u); lineTo(apice.x, apice.y + ch * 0.16f); lineTo(w * 0.88f, frontonBase - 2 * u); close() }
        drawPath(timpano, t(NOGAL_OSC, oscuro))
        drawPath(fronton, Color.White.copy(alpha = 0.15f), style = Stroke(1.2f * u))
        laurel(Offset(w / 2, frontonBase - (frontonBase - apice.y) * 0.36f), (frontonBase - apice.y) * 0.24f, t(ORO, oscuro))
        listOf(Offset(w * 0.04f, frontonBase), apice, Offset(w * 0.96f, frontonBase)).forEach { o -> palmeta(o, ch * 0.13f, t(ORO, oscuro)) }
        // El zócalo moldurado, con las patas de garra a los lados.
        val zocalo = Rect(w * 0.035f, cuerpo.bottom, w * 0.965f, d.base.top + d.base.height * 0.78f)
        drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), zocalo.top, zocalo.bottom), zocalo.topLeft, zocalo.size)
        drawLine(Color.White.copy(alpha = 0.18f), zocalo.topLeft, Offset(zocalo.right, zocalo.top), 1f * u)
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(zocalo.left, zocalo.top + 3 * u), Offset(zocalo.right, zocalo.top + 3 * u), 0.8f * u)
        listOf(zocalo.left + 10 * u, zocalo.right - 10 * u).forEach { x ->
            val y = zocalo.bottom
            drawOval(t(NOGAL_OSC, oscuro), Offset(x - 9 * u, y - 4 * u), Size(18 * u, 10 * u))
            for (k in 0 until 3) drawCircle(t(NOGAL, oscuro), 2.6f * u, Offset(x - 5 * u + k * 5 * u, y + 4 * u))
        }
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            tabulaAnsata(r, oscuro)
        }
    }

    /** Una corona de laurel: dos ramas de hojas en círculo, abiertas arriba, y la cinta abajo. */
    private fun DrawScope.laurel(c: Offset, r: Float, color: Color) {
        for (lado in listOf(-1f, 1f)) for (k in 0 until 7) {
            val ang = (if (lado > 0) 100f + 22f * k else 80f - 22f * k)
            val rad = Math.toRadians(ang.toDouble())
            val p = Offset(c.x + r * kotlin.math.cos(rad).toFloat(), c.y + r * kotlin.math.sin(rad).toFloat())
            rotate(ang + 90f, p) { drawOval(color, Offset(p.x - r * 0.28f, p.y - r * 0.11f), Size(r * 0.56f, r * 0.22f)) }
        }
        drawLine(LACRE, Offset(c.x - r * 0.2f, c.y + r * 1.05f), Offset(c.x - r * 0.45f, c.y + r * 1.5f), r * 0.12f, StrokeCap.Round)
        drawLine(LACRE, Offset(c.x + r * 0.2f, c.y + r * 1.05f), Offset(c.x + r * 0.45f, c.y + r * 1.5f), r * 0.12f, StrokeCap.Round)
    }

    /** Una acrotera en forma de palmeta: un abanico de hojas sobre la punta. */
    private fun DrawScope.palmeta(o: Offset, r: Float, color: Color) {
        for (k in -2..2) {
            val ang = Math.toRadians(-90.0 + k * 24.0)
            drawLine(color, o, Offset(o.x + r * kotlin.math.cos(ang).toFloat(), o.y + r * kotlin.math.sin(ang).toFloat()), r * 0.2f, StrokeCap.Round)
        }
        drawCircle(color, r * 0.22f, o)
    }

    /** La tabula ansata: una placa de bronce con dos asas en cola de milano a los lados y sus clavos. */
    private fun DrawScope.tabulaAnsata(r: Rect, oscuro: Boolean) {
        val u = density
        val a = r.height * 0.55f
        val forma = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top)
            lineTo(r.right + a, r.top - r.height * 0.05f); lineTo(r.right + a * 0.55f, r.center.y); lineTo(r.right + a, r.bottom + r.height * 0.05f)
            lineTo(r.right, r.bottom); lineTo(r.left, r.bottom)
            lineTo(r.left - a, r.bottom + r.height * 0.05f); lineTo(r.left - a * 0.55f, r.center.y); lineTo(r.left - a, r.top - r.height * 0.05f)
            close()
        }
        drawPath(forma, Color.Black.copy(alpha = 0.3f), style = Stroke(2.5f * u))
        drawPath(forma, Brush.verticalGradient(listOf(t(mezcla(BRONCE, Color.White, 0.25f), oscuro), t(BRONCE, oscuro), t(mezcla(BRONCE, BRONCE_OSC, 0.4f), oscuro)), r.top, r.bottom))
        drawRect(t(BRONCE_OSC, oscuro).copy(alpha = 0.7f), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.8f * u))
        listOf(r.left - a * 0.72f, r.right + a * 0.72f).forEach { x -> drawCircle(t(BRONCE_OSC, oscuro), 1.3f * u, Offset(x, r.center.y)) }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre del anaquel: una lucerna de bronce encendida.
            val o = Offset(w / 2, h * 0.9f)
            drawOval(t(BRONCE, oscuro), Offset(o.x - w * 0.2f, o.y - h * 0.06f), Size(w * 0.4f, h * 0.1f))
            drawLine(t(BRONCE, oscuro), Offset(o.x + w * 0.18f, o.y - h * 0.02f), Offset(o.x + w * 0.28f, o.y - h * 0.06f), 3 * u, StrokeCap.Round)
            mancha(Offset(o.x - w * 0.22f, o.y - h * 0.12f), w * 0.2f, Color(0xFFF4C76E).copy(alpha = 0.5f))
            drawOval(Color(0xFFF4C76E), Offset(o.x - w * 0.25f, o.y - h * 0.17f), Size(w * 0.05f, h * 0.1f))
            return
        }
        val x0 = w * 0.13f; val x1 = w * 0.87f
        val eh = (x1 - x0) * 0.3f
        val arriba = h * 0.34f; val abajo = h * 0.98f - eh / 2
        // La tapa echada hacia atrás (más al abrirse).
        val subeTapa = ab * h * 0.08f
        drawOval(t(CUERO_OSC, oscuro), Offset(x0 - 2 * u, arriba - eh * 1.75f - subeTapa), Size(x1 - x0 + 4 * u, eh * 1.3f))
        drawOval(t(BRONCE, oscuro), Offset(x0 - 2 * u, arriba - eh * 1.75f - subeTapa), Size(x1 - x0 + 4 * u, eh * 1.3f), style = Stroke(1.4f * u))
        // La boca, oscura, y los rollos de pie, que suben al abrirse.
        drawOval(Color(0xFF1C0F06), Offset(x0, arriba - eh / 2), Size(x1 - x0, eh))
        val k = c.n.coerceIn(0, 6)
        for (i in 0 until k) {
            val fila = i % 2
            val cx = x0 + (x1 - x0) * (0.2f + 0.6f * ((i / 2) + 0.5f * fila + 0.25f) / 3f)
            val rr = (x1 - x0) * 0.09f
            val sube = h * (0.05f + 0.05f * azar(c.semilla, i)) + ab * h * 0.12f
            val y = arriba - eh * 0.1f + fila * eh * 0.25f
            drawRect(t(PAPIRO_OSC, oscuro), Offset(cx - rr, y - sube), Size(rr * 2, sube))
            drawOval(t(PAPIRO, oscuro), Offset(cx - rr, y - sube - rr * 0.45f), Size(rr * 2, rr * 0.9f))
            drawOval(t(PAPIRO_OSC, oscuro), Offset(cx - rr * 0.6f, y - sube - rr * 0.27f), Size(rr * 1.2f, rr * 0.54f), style = Stroke(0.7f * u))
            drawCircle(t(NOGAL_OSC, oscuro), rr * 0.3f, Offset(cx, y - sube))
        }
        // El cilindro de cuero, con su luz, el borde de la boca y los aros de bronce.
        val cuerpo = Rect(x0, arriba, x1, abajo)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(x0 - 2 * u, abajo - eh * 0.3f), Size(x1 - x0 + 6 * u, eh))
        drawOval(t(CUERO_OSC, oscuro), Offset(x0, abajo - eh / 2), Size(x1 - x0, eh))
        drawRect(Brush.horizontalGradient(listOf(t(CUERO_OSC, oscuro), t(CUERO, oscuro), t(CUERO_LUZ, oscuro), t(CUERO, oscuro), t(CUERO_OSC, oscuro)), x0, x1), cuerpo.topLeft, cuerpo.size)
        drawArc(t(CUERO_LUZ, oscuro), 0f, 180f, false, Offset(x0, arriba - eh / 2), Size(x1 - x0, eh), style = Stroke(2.2f * u))
        listOf(arriba + (abajo - arriba) * 0.1f, abajo - (abajo - arriba) * 0.08f).forEach { y ->
            drawArc(t(BRONCE_OSC, oscuro), 0f, 180f, false, Offset(x0, y - eh / 2), Size(x1 - x0, eh), style = Stroke(4 * u))
            drawArc(t(BRONCE, oscuro), 0f, 180f, false, Offset(x0, y - eh / 2 - 1 * u), Size(x1 - x0, eh), style = Stroke(1.6f * u))
        }
        // Costuras del cuero, a los lados.
        listOf(x0 + (x1 - x0) * 0.08f, x1 - (x1 - x0) * 0.08f).forEach { x ->
            var y = arriba + eh * 0.6f
            while (y < abajo - eh * 0.3f) { drawLine(t(CUERO_LUZ, oscuro).copy(alpha = 0.6f), Offset(x, y), Offset(x, y + 2 * u), 0.8f * u); y += 4 * u }
        }
        // La placa con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        tabulaAnsata(r, oscuro)
    }

    override val rotulo = Rect(0.24f, 0.54f, 0.76f, 0.78f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.inscripcion
    override val salida = 0f
    override val placa = Rect(0.33f, 0.12f, 0.67f, 0.62f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Una pared al fresco: el campo rojo, el friso negro arriba, el zócalo negro abajo, filetes amarillos y candelabros finos.
        drawRect(Brush.verticalGradient(listOf(t(POMPEYANO, oscuro), t(mezcla(POMPEYANO, Color.Black, 0.18f), oscuro))))
        for (k in 0 until 90) mancha(Offset(w * azar(31, k), h * azar(32, k)), (10f + 30f * azar(33, k)) * u, (if (k % 3 == 0) Color.White else Color.Black).copy(alpha = 0.05f))
        val friso = h * 0.07f; val zocalo = h * 0.09f
        drawRect(t(NEGRO, oscuro), Offset.Zero, Size(w, friso))
        drawRect(t(NEGRO, oscuro), Offset(0f, h - zocalo), Size(w, zocalo))
        // Una greca de ovas en el friso.
        var x = 6 * u
        while (x < w) { drawOval(t(FILETE, oscuro).copy(alpha = 0.6f), Offset(x, friso * 0.35f), Size(5 * u, friso * 0.4f), style = Stroke(0.8f * u)); x += 9 * u }
        drawLine(t(FILETE, oscuro), Offset(0f, friso), Offset(w, friso), 1.2f * u)
        drawLine(t(FILETE, oscuro), Offset(0f, h - zocalo), Offset(w, h - zocalo), 1.2f * u)
        val m = 8 * u
        drawRect(t(FILETE, oscuro).copy(alpha = 0.8f), Offset(m, friso + m), Size(w - 2 * m, h - friso - zocalo - 2 * m), style = Stroke(1f * u))
        listOf(m + 3 * u, w - m - 3 * u).forEach { cx ->
            drawLine(t(FILETE, oscuro).copy(alpha = 0.6f), Offset(cx, friso + m), Offset(cx, h - zocalo - m), 1f * u)
            var y = friso + m + 30 * u
            while (y < h - zocalo - m - 10 * u) { drawOval(t(FILETE, oscuro).copy(alpha = 0.7f), Offset(cx - 3 * u, y), Size(6 * u, 3 * u)); y += 70 * u }
        }
        // En el zócalo, pinceladas de mármol fingido.
        for (k in 0 until 12) {
            val y = h - zocalo + zocalo * azar(41, k)
            drawLine(Color.White.copy(alpha = 0.08f), Offset(w * azar(42, k), y), Offset(w * azar(42, k) + 30 * u, y + 6 * u), 1f * u)
        }
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFF3E6C8)
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    /** El grueso de las varillas del rollo, en px, según el tamaño de la ficha. */
    private fun varilla(s: Size) = (minOf(s.width, s.height) * 0.065f).coerceIn(22f, 56f)

    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val v = varilla(s)
        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(0f, 0f, s.width, v), androidx.compose.ui.geometry.CornerRadius(v / 2)))
        addRect(Rect(v * 0.55f, v * 0.5f, s.width - v * 0.55f, s.height - v * 0.5f))
        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(0f, s.height - v, s.width, s.height), androidx.compose.ui.geometry.CornerRadius(v / 2)))
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val v = varilla(size)
        // Las fibras del papiro: tiras horizontales y, debajo, las verticales; y una junta entre hojas.
        var y = v; while (y < h - v) { drawLine(PAPIRO_OSC.copy(alpha = 0.12f + 0.08f * azar(semilla, y.toInt())), Offset(v * 0.55f, y), Offset(w - v * 0.55f, y), 0.6f * u); y += 2.6f * u }
        var x = v; while (x < w - v) { drawLine(PAPIRO_OSC.copy(alpha = 0.06f), Offset(x, v), Offset(x, h - v), 0.5f * u); x += 4.5f * u }
        val junta = w * (0.3f + 0.4f * azar(semilla, 7))
        drawLine(PAPIRO_OSC.copy(alpha = 0.3f), Offset(junta, v), Offset(junta, h - v), 1.2f * u)
        // La sombra donde la hoja se enrolla en cada varilla.
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), v * 0.5f, v * 0.5f + 10 * u), Offset(0f, v * 0.5f), Size(w, 10 * u))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.22f)), h - v * 0.5f - 10 * u, h - v * 0.5f), Offset(0f, h - v * 0.5f - 10 * u), Size(w, 10 * u))
        // Las varillas (umbilici) de madera, con sus perillas de marfil.
        listOf(0f, h - v).forEach { y0 ->
            drawRoundRect(Brush.verticalGradient(listOf(NOGAL_LUZ, NOGAL, NOGAL_OSC), y0, y0 + v), Offset(v * 0.35f, y0 + v * 0.12f), Size(w - v * 0.7f, v * 0.76f), androidx.compose.ui.geometry.CornerRadius(v * 0.38f))
            listOf(v * 0.5f, w - v * 0.5f).forEach { cx ->
                drawCircle(Color(0xFFEDE3CC), v * 0.5f, Offset(cx, y0 + v / 2))
                drawCircle(Color(0xFFB8A784), v * 0.5f, Offset(cx, y0 + v / 2), style = Stroke(1f * u))
                drawCircle(Color.White.copy(alpha = 0.6f), v * 0.14f, Offset(cx - v * 0.15f, y0 + v * 0.35f))
            }
        }
    }

    override val margenes = MargenesFicha(18.dp, 16.dp, 18.dp, 32.dp, 32.dp, 36.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El lacre de una tablilla sellada.
            "hecho" -> selloHecho(LACRE, p.color)
            // Una corona de laurel chica.
            "convence" -> laurel(Offset(c.x, c.y - s * 0.05f), s * 0.36f, LAUREL)
            // Tres interpuntos triangulares, como en las inscripciones.
            "piensa" -> listOf(-0.32f, 0f, 0.32f).forEach { dx ->
                val o = Offset(c.x + dx * s, c.y)
                drawPath(Path().apply { moveTo(o.x - s * 0.1f, o.y - s * 0.09f); lineTo(o.x + s * 0.1f, o.y - s * 0.09f); lineTo(o.x, o.y + s * 0.12f); close() }, TINTA)
            }
            "duda" -> drawArc(p.tenue, 30f, 300f, false, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Size(s * 0.68f, s * 0.68f), style = Stroke(s * 0.12f))
            // La theta nigrum: la marca de los caídos en las listas.
            "desacuerdo" -> { drawCircle(LACRE, s * 0.36f, c, style = Stroke(s * 0.12f)); drawLine(LACRE, Offset(c.x - s * 0.36f, c.y), Offset(c.x + s * 0.36f, c.y), s * 0.12f) }
            else -> {}
        }
    }

    /** Un sello de lacre rojo, con su borde corrido y una L. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        for (k in 0 until 8) drawCircle(LACRE, s * 0.36f, Offset(c.x + s * 0.06f * kotlin.math.cos(k * 0.785f), c.y + s * 0.06f * kotlin.math.sin(k * 0.785f)))
        drawCircle(mezcla(LACRE, Color.Black, 0.25f), s * 0.24f, c, style = Stroke(s * 0.06f))
    }

    override val desorden = 0f
}

/**
 * Egipto: la Casa de la Vida, donde los escribas guardaban sus escritos. Arriba, la cornisa de gola con el friso jekeru en
 * azul, rojo y verde, y el disco solar alado; en el muro, columnas de jeroglíficos; estantes de piedra con bandas pintadas.
 * Cada tema es un cofre de cedro con tapa de capilla y marco de fayenza, cerrado con un hilo de lino entre dos perillas y un
 * terrón de barro sellado; el tema va dentro de un cartucho. Por dentro, el cofre forrado de lino. Las fichas son tablillas
 * de escriba: madera con una cara de estuco blanco y pautas en rojo.
 */
object IdeasEgipto : DisenoIdeas {
    private val CEDRO = Color(0xFF8E4E2C)
    private val CEDRO_LUZ = Color(0xFFB4703F)
    private val CEDRO_OSC = Color(0xFF55301A)
    private val LAPIS = Color(0xFF1D2F7A)
    private val FAYENZA = Color(0xFF2C8C9C)
    private val ORO = Color(0xFFD4AF37)
    private val CORNALINA = Color(0xFFB8471E)
    private val VERDE = Color(0xFF3E7A4A)
    private val MURO = Color(0xFFE8D7AE)
    private val PIEDRA = Color(0xFFCDB98E)
    private val PIEDRA_OSC = Color(0xFF9A835A)
    private val EBANO = Color(0xFF1E1610)
    private val LINO = Color(0xFFE6D8B4)
    private val LINO_OSC = Color(0xFFBFAD84)
    private val BARRO = Color(0xFF6E4A2E)
    private val ESTUCO = Color(0xFFF6F0E0)
    private val TINTA = Color(0xFF1E160C)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.25f
    override val corona = 0.24f
    override val base = 0.1f
    override val lado = 0.08f
    override val hueco = 0.07f

    /** Un jeroglífico sencillo (agua, boca, pan, junco, ojo, pájaro…), del tamaño [s], para las columnas del muro. */
    private fun DrawScope.signo(k: Int, o: Offset, s: Float, color: Color) {
        val u = density
        when (k % 7) {
            0 -> { // agua: zigzag
                val p = Path().apply { moveTo(o.x - s / 2, o.y); for (i in 1..6) lineTo(o.x - s / 2 + s * i / 6, o.y + (if (i % 2 == 0) 0f else -s * 0.18f)) }
                drawPath(p, color, style = Stroke(0.9f * u))
            }
            1 -> drawOval(color, Offset(o.x - s * 0.4f, o.y - s * 0.18f), Size(s * 0.8f, s * 0.36f)) // boca
            2 -> drawArc(color, 180f, 180f, true, Offset(o.x - s * 0.35f, o.y - s * 0.3f), Size(s * 0.7f, s * 0.6f)) // pan
            3 -> { drawLine(color, Offset(o.x, o.y + s * 0.4f), Offset(o.x, o.y - s * 0.4f), 1.1f * u); drawOval(color, Offset(o.x - s * 0.08f, o.y - s * 0.5f), Size(s * 0.24f, s * 0.3f)) } // junco
            4 -> { drawOval(color, Offset(o.x - s * 0.4f, o.y - s * 0.15f), Size(s * 0.8f, s * 0.3f), style = Stroke(0.9f * u)); drawCircle(color, s * 0.1f, o) } // ojo
            5 -> drawCircle(color, s * 0.25f, o, style = Stroke(1f * u)) // sol
            else -> { drawLine(color, Offset(o.x - s * 0.4f, o.y + s * 0.2f), Offset(o.x + s * 0.3f, o.y + s * 0.2f), 1f * u); drawCircle(color, s * 0.12f, Offset(o.x + s * 0.25f, o.y - s * 0.05f)) } // pájaro, muy simple
        }
    }

    /** El friso jekeru: haces de juncos atados, en franjas azul, roja y verde con filetes de oro. */
    private fun DrawScope.jekeru(r: Rect, oscuro: Boolean) {
        val u = density
        val n = (r.width / (6 * u)).toInt().coerceAtLeast(6)
        val colores = listOf(LAPIS, CORNALINA, VERDE)
        for (k in 0 until n) {
            val x = r.left + r.width * k / n
            drawRect(t(colores[k % 3], oscuro), Offset(x, r.top), Size(r.width / n, r.height))
            drawLine(t(ORO, oscuro), Offset(x, r.top), Offset(x, r.bottom), 0.6f * u)
            drawCircle(t(ORO, oscuro), r.width / n * 0.3f, Offset(x + r.width / n / 2, r.top + r.height * 0.25f))
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val y0 = d.corona.bottom; val y1 = d.base.top
        // El muro de estuco, con columnas de jeroglíficos entre los cofres y a los lados.
        drawRect(Brush.verticalGradient(listOf(t(mezcla(MURO, Color.Black, 0.08f), oscuro), t(MURO, oscuro)), y0, y1), Offset(0f, y0), Size(w, y1 - y0))
        val fila0 = d.huecos.take(cols)
        val columnas = listOf(fila0.first().left / 2, (fila0[0].right + fila0[1].left) / 2, (fila0.last().right + w) / 2)
        columnas.forEach { x ->
            drawLine(t(LAPIS, oscuro).copy(alpha = 0.3f), Offset(x - 9 * u, y0 + 4 * u), Offset(x - 9 * u, y1 - 4 * u), 0.8f * u)
            drawLine(t(LAPIS, oscuro).copy(alpha = 0.3f), Offset(x + 9 * u, y0 + 4 * u), Offset(x + 9 * u, y1 - 4 * u), 0.8f * u)
            var y = y0 + 14 * u; var k = (x / u).toInt()
            while (y < y1 - 8 * u) { signo(k, Offset(x, y), 10 * u, t(if (k % 4 == 0) CORNALINA else LAPIS, oscuro).copy(alpha = 0.45f)); y += 15 * u; k += 3 }
        }
        // Los estantes de piedra, con su banda pintada al frente.
        d.filas.forEach { f ->
            val r = Rect(w * 0.03f, f.bottom, w * 0.97f, f.bottom + maxOf(d.separacion * 0.45f, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
            drawRect(t(PIEDRA, oscuro), r.topLeft, r.size)
            val n = (r.width / (10 * u)).toInt()
            for (k in 0 until n) drawRect(t(listOf(LAPIS, CORNALINA, VERDE)[k % 3], oscuro).copy(alpha = 0.75f), Offset(r.left + r.width * k / n + 1 * u, r.top + r.height * 0.35f), Size(r.width / n - 2 * u, r.height * 0.3f))
            drawLine(Color.White.copy(alpha = 0.3f), r.topLeft, Offset(r.right, r.top), 0.8f * u)
        }
        // Los bordes del muro: el bocel atado (una moldura redonda envuelta en cintas).
        listOf(w * 0.015f, w * 0.985f).forEach { x ->
            drawRect(t(PIEDRA, oscuro), Offset(x - 4 * u, y0), Size(8 * u, y1 - y0))
            var y = y0
            while (y < y1) { drawLine(t(LAPIS, oscuro), Offset(x - 4 * u, y), Offset(x + 4 * u, y + 5 * u), 1.6f * u); y += 7 * u }
        }
        // La cornisa: el bocel abajo, la gola con el friso jekeru y, al centro, el disco solar alado.
        val ch = d.corona.height
        val bocel = Rect(0f, d.corona.bottom - ch * 0.12f, w, d.corona.bottom)
        drawRect(t(PIEDRA, oscuro), bocel.topLeft, bocel.size)
        var x = 0f
        while (x < w) { drawLine(t(LAPIS, oscuro), Offset(x, bocel.top), Offset(x + 5 * u, bocel.bottom), 1.6f * u); x += 7 * u }
        val gola = Path().apply {
            moveTo(w * 0.01f, bocel.top); lineTo(w * 0.99f, bocel.top)
            quadraticTo(w * 0.995f, d.corona.top + ch * 0.25f, w, d.corona.top + ch * 0.08f); lineTo(0f, d.corona.top + ch * 0.08f)
            quadraticTo(w * 0.005f, d.corona.top + ch * 0.25f, w * 0.01f, bocel.top); close()
        }
        drawPath(gola, t(PIEDRA, oscuro))
        clipPath(gola) { jekeru(Rect(0f, d.corona.top + ch * 0.08f, w, bocel.top), oscuro) }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), d.corona.top + ch * 0.08f, d.corona.top + ch * 0.4f), Offset(0f, d.corona.top + ch * 0.08f), Size(w, ch * 0.32f))
        drawRect(t(PIEDRA_OSC, oscuro), Offset(-2 * u, d.corona.top + ch * 0.02f), Size(w + 4 * u, ch * 0.07f))
        val sol = Offset(w / 2, d.corona.top + ch * 0.5f)
        val rs = ch * 0.16f
        listOf(-1f, 1f).forEach { s ->
            for (k in 0 until 3) {
                val ala = Path().apply {
                    moveTo(sol.x + s * rs * 0.8f, sol.y - rs * 0.5f + k * rs * 0.35f)
                    quadraticTo(sol.x + s * rs * 3.5f, sol.y - rs * (1.1f - k * 0.3f), sol.x + s * rs * (5.2f - k * 0.6f), sol.y - rs * (0.4f - k * 0.3f))
                    lineTo(sol.x + s * rs * (5f - k * 0.6f), sol.y + rs * (0.05f + k * 0.25f))
                    quadraticTo(sol.x + s * rs * 3f, sol.y - rs * (0.1f - k * 0.3f), sol.x + s * rs * 0.8f, sol.y + rs * 0.2f + k * rs * 0.3f); close()
                }
                drawPath(ala, t(listOf(LAPIS, FAYENZA, VERDE)[k], oscuro))
                drawPath(ala, t(ORO, oscuro), style = Stroke(0.7f * u))
            }
            // El ureo, la cobra a cada lado del disco.
            drawLine(t(ORO, oscuro), Offset(sol.x + s * rs * 0.9f, sol.y + rs * 0.6f), Offset(sol.x + s * rs * 1.1f, sol.y - rs * 0.4f), 1.8f * u, StrokeCap.Round)
        }
        drawCircle(t(CORNALINA, oscuro), rs, sol)
        drawCircle(t(ORO, oscuro), rs, sol, style = Stroke(1.2f * u))
        // El zócalo: franjas de colores y la línea del piso.
        val zocalo = Rect(0f, d.base.top, w, d.base.top + d.base.height * 0.7f)
        drawRect(t(PIEDRA, oscuro), zocalo.topLeft, zocalo.size)
        val nz = (w / (16 * u)).toInt()
        for (k in 0 until nz) drawRect(t(listOf(LAPIS, CORNALINA, VERDE, ORO)[k % 4], oscuro).copy(alpha = 0.8f), Offset(w * k / nz + 2 * u, zocalo.top + 3 * u), Size(w / nz - 4 * u, zocalo.height - 6 * u))
        drawRect(t(PIEDRA_OSC, oscuro), Offset(0f, zocalo.bottom), Size(w, d.base.bottom - zocalo.bottom))
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            drawRect(t(ESTUCO, oscuro), r.topLeft, r.size)
            drawRect(t(LAPIS, oscuro), r.topLeft, r.size, style = Stroke(1.6f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre del estante: un vaso de fayenza azul.
            val o = Offset(w / 2, h * 0.96f)
            val vaso = Path().apply { moveTo(o.x - w * 0.08f, o.y); quadraticTo(o.x - w * 0.16f, o.y - h * 0.2f, o.x - w * 0.06f, o.y - h * 0.32f); lineTo(o.x + w * 0.06f, o.y - h * 0.32f)
                quadraticTo(o.x + w * 0.16f, o.y - h * 0.2f, o.x + w * 0.08f, o.y); close() }
            drawPath(vaso, Brush.horizontalGradient(listOf(t(FAYENZA, oscuro), t(mezcla(FAYENZA, Color.White, 0.3f), oscuro), t(FAYENZA, oscuro)), o.x - w * 0.16f, o.x + w * 0.16f))
            drawLine(t(EBANO, oscuro), Offset(o.x - w * 0.1f, o.y - h * 0.16f), Offset(o.x + w * 0.1f, o.y - h * 0.16f), 1.2f * u)
            return
        }
        val cuerpo = Rect(w * 0.08f, h * 0.38f, w * 0.92f, h * 0.88f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.18f
        // Las patas cortas.
        listOf(cuerpo.left + 3 * u, cuerpo.right - 3 * u - w * 0.08f).forEach { x ->
            drawPath(Path().apply { moveTo(x, cuerpo.bottom); lineTo(x + w * 0.08f, cuerpo.bottom); lineTo(x + w * 0.06f, h * 0.98f); lineTo(x + w * 0.02f, h * 0.98f); close() }, t(CEDRO_OSC, oscuro))
        }
        drawOval(Color.Black.copy(alpha = 0.22f), Offset(cuerpo.left, h * 0.95f), Size(cuerpo.width, h * 0.05f))
        // Lo que asoma entre la tapa y el cofre: los cantos de las tablillas.
        if (sube > 1f) {
            drawRect(Color(0xFF1C110A), Offset(cuerpo.left + 3 * u, cuerpo.top - sube), Size(cuerpo.width - 6 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.12f + 0.13f * i)
                val y = cuerpo.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                drawRect(t(ESTUCO, oscuro), Offset(x, y), Size(cuerpo.width * 0.1f, cuerpo.top - y + 2 * u))
                drawRect(t(CEDRO_LUZ, oscuro), Offset(x, y), Size(cuerpo.width * 0.1f, 1.6f * u))
            }
        }
        // El cofre: cedro con su veta y el marco de taracea de fayenza y ébano.
        drawRect(Brush.verticalGradient(listOf(t(CEDRO_LUZ, oscuro), t(CEDRO, oscuro), t(CEDRO_OSC, oscuro)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, CEDRO_OSC.copy(alpha = 0.35f), c.semilla, 1f, grosor = 0.7f, ondulacion = 0.4f)
        val m = 3.5f * u; val cuadro = 3.2f * u
        val marco = Rect(cuerpo.left + m, cuerpo.top + m, cuerpo.right - m, cuerpo.bottom - m)
        var x = marco.left; var i = 0
        while (x < marco.right - cuadro) {
            listOf(marco.top, marco.bottom - cuadro).forEach { y -> drawRect(t(if (i % 2 == 0) FAYENZA else EBANO, oscuro), Offset(x, y), Size(cuadro, cuadro)) }
            x += cuadro; i++
        }
        var y = marco.top; i = 0
        while (y < marco.bottom - cuadro) {
            listOf(marco.left, marco.right - cuadro).forEach { xx -> drawRect(t(if (i % 2 == 0) FAYENZA else EBANO, oscuro), Offset(xx, y), Size(cuadro, cuadro)) }
            y += cuadro; i++
        }
        // La tapa de capilla: el techo inclinado con su cornisa, que sube al abrirse.
        val ty = cuerpo.top - sube
        val tapa = Path().apply {
            moveTo(cuerpo.left - 3 * u, ty); lineTo(cuerpo.right + 3 * u, ty)
            lineTo(cuerpo.right - w * 0.02f, ty - h * 0.08f); quadraticTo(w / 2, ty - h * 0.2f, cuerpo.left + w * 0.02f, ty - h * 0.08f); close()
        }
        drawPath(tapa, Brush.verticalGradient(listOf(t(CEDRO_LUZ, oscuro), t(CEDRO, oscuro)), ty - h * 0.2f, ty))
        drawLine(t(ORO, oscuro), Offset(cuerpo.left - 3 * u, ty), Offset(cuerpo.right + 3 * u, ty), 1.6f * u)
        // Las dos perillas, el hilo de lino entre ellas y el terrón de barro sellado.
        val p1 = Offset(w / 2, ty - h * 0.07f); val p2 = Offset(w / 2, cuerpo.top + h * 0.05f)
        listOf(p1, p2).forEach { o -> drawCircle(t(EBANO, oscuro), 3.2f * u, o); drawCircle(Color.White.copy(alpha = 0.3f), 1f * u, Offset(o.x - 1 * u, o.y - 1 * u)) }
        listOf(-2f, 2f).forEach { dx -> drawLine(t(LINO, oscuro), Offset(p1.x + dx * u, p1.y), Offset(p2.x - dx * u, p2.y), 1f * u) }
        drawOval(t(BARRO, oscuro), Offset(p2.x + 3 * u, p2.y - 1 * u), Size(10 * u, 8 * u))
        drawOval(t(mezcla(BARRO, Color.Black, 0.3f), oscuro), Offset(p2.x + 5 * u, p2.y + 0.5f * u), Size(6 * u, 4.5f * u), style = Stroke(0.7f * u))
        // El cartucho donde va el tema: un óvalo largo de estuco con su cuerda de lapislázuli y la barra al final.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRoundRect(t(ESTUCO, oscuro), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(r.height / 2))
        drawRoundRect(t(LAPIS, oscuro), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(r.height / 2), style = Stroke(1.8f * u))
        drawLine(t(LAPIS, oscuro), Offset(r.right + 2.5f * u, r.top + r.height * 0.15f), Offset(r.right + 2.5f * u, r.bottom - r.height * 0.15f), 2.2f * u)
    }

    override val rotulo = Rect(0.16f, 0.52f, 0.82f, 0.8f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.messiri
    override val salida = 0f
    override val placa = Rect(0.3f, 0.12f, 0.7f, 0.6f)
    override fun tintaPlaca(oscuro: Boolean) = LAPIS

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo del cofre, forrado de lino: pliegues suaves en diagonal.
        drawRect(t(LINO, oscuro))
        for (k in 0 until 14) {
            val x = w * (k / 10f) - w * 0.3f
            val pliegue = Path().apply { moveTo(x, 0f); cubicTo(x + w * 0.1f, h * 0.3f, x - w * 0.05f, h * 0.6f, x + w * 0.2f, h) }
            drawPath(pliegue, t(LINO_OSC, oscuro).copy(alpha = 0.35f), style = Stroke((6f + 10f * azar(71, k)) * u, cap = StrokeCap.Round))
            drawPath(pliegue, Color.White.copy(alpha = 0.18f), style = Stroke(1.5f * u))
        }
        // El tejido: hilos finos en las dos direcciones.
        var y = 0f; while (y < h) { drawLine(t(LINO_OSC, oscuro).copy(alpha = 0.12f), Offset(0f, y), Offset(w, y), 0.5f * u); y += 2.5f * u }
        // Las paredes de cedro y la sombra que echan sobre el lino.
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl * 1.6f, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(CEDRO, oscuro), r.topLeft, r.size)
            vetas(r, CEDRO_OSC.copy(alpha = 0.4f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.4f)
        }
        sombraInterior(Rect(pl, pl, w - pl, h - pl * 1.6f), 18 * u, 0.3f)
        // El borde de fayenza en el canto de la pared de enfrente.
        drawRect(t(FAYENZA, oscuro), Offset(0f, h - pl * 1.6f), Size(w, 2.5f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFF2E6CC) else TINTA
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 3

    override fun forma(semilla: Int): Shape = RoundedCornerShape(4.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        val m = 5 * u * g
        // La cara de estuco, más blanca que el papel, con su craquelado.
        drawRect(ESTUCO.copy(alpha = 0.55f), Offset(m, m), Size(w - 2 * m, h - 2 * m))
        for (k in 0 until (if (grande) 9 else 4)) {
            val p0 = Offset(m + (w - 2 * m) * azar(semilla, k), m + (h - 2 * m) * azar(semilla, k + 20))
            val q = Path().apply { moveTo(p0.x, p0.y); var o = p0; repeat(4) { j -> o = Offset(o.x + (azar(semilla, k * 7 + j + 40) - 0.5f) * 20 * u, o.y + (azar(semilla, k * 7 + j + 60) - 0.3f) * 14 * u); lineTo(o.x, o.y) } }
            drawPath(q, PIEDRA_OSC.copy(alpha = 0.25f), style = Stroke(0.5f * u))
        }
        // El marco de madera de la tablilla.
        drawRect(Brush.linearGradient(listOf(CEDRO_LUZ, CEDRO, CEDRO_OSC), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), style = Stroke(m * 2))
        drawRect(CEDRO_OSC, Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(0.8f * u))
        // El agujero para colgarla, con su cordel de lino, arriba a la derecha.
        val hoyo = Offset(w - m / 2, m * 1.2f)
        drawCircle(Color.Black.copy(alpha = 0.6f), m * 0.28f, hoyo)
        drawLine(LINO, hoyo, Offset(w, m * 0.2f), 1f * u * g)
        // Las dos pastillas de tinta, roja y negra, en la esquina de abajo a la izquierda.
        drawCircle(CORNALINA, 2.6f * u * g, Offset(m * 2.4f, h - m * 2.2f))
        drawCircle(EBANO, 2.6f * u * g, Offset(m * 2.4f + 6.5f * u * g, h - m * 2.2f))
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 16.dp, 22.dp, 28.dp, 32.dp)
    override val proporcionFicha = 1.25f
    override val anchoGrande = 1f
    override val proporcionGrande = 1f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El pilar djed: lo que se mantiene firme.
            "hecho" -> {
                drawRect(LAPIS, Offset(c.x - s * 0.1f, c.y - s * 0.24f), Size(s * 0.2f, s * 0.66f))
                for (k in 0..3) drawRect(LAPIS, Offset(c.x - s * 0.3f, c.y - s * 0.44f + k * s * 0.1f), Size(s * 0.6f, s * 0.06f))
            }
            // El ankh: la vida.
            "convence" -> {
                drawOval(LAPIS, Offset(c.x - s * 0.18f, c.y - s * 0.46f), Size(s * 0.36f, s * 0.4f), style = Stroke(s * 0.1f))
                drawLine(LAPIS, Offset(c.x, c.y - s * 0.06f), Offset(c.x, c.y + s * 0.46f), s * 0.12f)
                drawLine(LAPIS, Offset(c.x - s * 0.34f, c.y + 0.02f * s), Offset(c.x + s * 0.34f, c.y + 0.02f * s), s * 0.11f)
            }
            // El ojo: lo estás mirando.
            "piensa" -> {
                drawOval(EBANO, Offset(c.x - s * 0.42f, c.y - s * 0.18f), Size(s * 0.84f, s * 0.36f), style = Stroke(s * 0.08f))
                drawCircle(EBANO, s * 0.13f, c)
                drawLine(EBANO, Offset(c.x - s * 0.08f, c.y + s * 0.16f), Offset(c.x - s * 0.2f, c.y + s * 0.42f), s * 0.07f)
            }
            // El anillo shen, sin cerrar del todo.
            "duda" -> { drawCircle(p.tenue, s * 0.3f, Offset(c.x, c.y - s * 0.08f), style = Stroke(s * 0.1f)); drawLine(p.tenue, Offset(c.x - s * 0.34f, c.y + s * 0.34f), Offset(c.x + s * 0.34f, c.y + s * 0.34f), s * 0.1f) }
            "desacuerdo" -> { drawLine(CORNALINA, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Offset(c.x + s * 0.34f, c.y + s * 0.34f), s * 0.13f); drawLine(CORNALINA, Offset(c.x + s * 0.34f, c.y - s * 0.34f), Offset(c.x - s * 0.34f, c.y + s * 0.34f), s * 0.13f) }
            else -> {}
        }
    }

    /** Un escarabajo de lapislázuli con las alas de oro. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        listOf(-1f, 1f).forEach { k -> for (j in 0 until 3) drawLine(EBANO, Offset(c.x, c.y - s * 0.1f + j * s * 0.18f), Offset(c.x + k * s * 0.42f, c.y - s * 0.2f + j * s * 0.24f), s * 0.05f) }
        drawOval(LAPIS, Offset(c.x - s * 0.26f, c.y - s * 0.24f), Size(s * 0.52f, s * 0.62f))
        drawOval(LAPIS, Offset(c.x - s * 0.15f, c.y - s * 0.44f), Size(s * 0.3f, s * 0.22f))
        drawLine(ORO, Offset(c.x, c.y - s * 0.22f), Offset(c.x, c.y + s * 0.36f), s * 0.05f)
        drawLine(ORO, Offset(c.x - s * 0.24f, c.y - s * 0.12f), Offset(c.x + s * 0.24f, c.y - s * 0.12f), s * 0.05f)
    }

    override val desorden = 0f
}
