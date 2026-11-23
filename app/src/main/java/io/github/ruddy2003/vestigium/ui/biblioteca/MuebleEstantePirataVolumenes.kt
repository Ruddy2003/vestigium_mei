package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Las entradas del estante de Pirata ([MuebleEstantePirata]), tres objetos de la cámara de popa según la semilla: el cuaderno
 * de bitácora (alto y delgado, de lomo redondo con sus nervios, su tejuelo de papel y la cinta que lo ciñe; asoman hojas
 * sueltas por la cabeza), el canuto de cartas (un estuche de cuero cosido con sus aros de latón y su tapa abombada; si va
 * lleno, la tapa cuelga de su cadenilla y asoman las cartas enrolladas; el día en una etiqueta de pergamino atada con
 * bramante) y el paquete de la correspondencia envuelto en hule (blando, con la solapa en pico, atado con bramante y
 * lacrado). El ánimo es el material, en todo el cuerpo ([materialPirata]); sin ánimo, lona embreada. Delante de todas pasa la barandilla de mar. En los Sueños, una luna de plata.
 */
internal object VolumenEstantePirata {
    private val P = PaletaPirata

    /** El papel de las etiquetas: viejo y algo tostado, para que no grite más que el objeto. */
    private val VIEJO = Color(0xFFE4D3AA)

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.45f, 0.85f, 0.5f, 0.12f, -0.22f, -0.55f, -0.85f)

    /** Los colores de un material con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(tinte: Color?, a: AirePirata, k: Float): Tinte {
        val m = tinte ?: P.EMBREADO
        val claro = mezcla(m, luzMueble(1f), if (tinte == null) 0.22f else 0.34f)
        val hondo = mezcla(m, sombraMueble(1f), 0.55f)
        return if (tinte != null) Tinte(a.animo(m, k), a.animo(claro, k), a.animo(hondo, k))
        else Tinte(a.luz(m, k), a.luz(claro, k), a.luz(hondo, k))
    }

    /** Un cuerpo redondo en [r] por franjas opacas (sin degradados): el filo, el claro, la cara, el hondo, hacia [hacia]. */
    private fun DrawScope.redondo(r: Rect, t: Tinte, hacia: Float) {
        for (i in 0 until 7) {
            val f0 = FRANJAS[i]; val f1 = FRANJAS[i + 1]
            val m = TONOS[i]
            val c = if (m >= 0f) mezcla(t.cara, t.claro, m) else mezcla(t.cara, t.hondo, -m)
            val x0 = if (hacia < 0f) r.left + r.width * f0 else r.right - r.width * f1
            drawRect(c, Offset(x0, r.top), Size(r.width * (f1 - f0) + 0.6f, r.height))
        }
    }

    /**
     * El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp sin achicarse) y el mes debajo, en una
     * de 9.5 dp; así el día sale del mismo tamaño en las tres entradas. Si [r] es más bajo, se reparten en proporción.
     */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        val dia = Rect(r.left, y0, r.right, y0 + hd)
        val mes = Rect(r.left, y0 + hd - 1f * u, r.right, y0 + hd + hm)
        textoEn(medidor, v.dia, dia, P.LETRA, tinta, DIA, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes, mes, P.LETRA, tinta.copy(alpha = 0.85f), MES, minimo = 5f)
    }

    /** La luna de plata de los Sueños: un creciente sobre su disco hondo, con su brillo. */
    private fun DrawScope.luna(c: Offset, r: Float, a: AirePirata) {
        val u = density
        val fondo = Color(0xFF151B33)
        drawCircle(a.sombra(0.45f), r * 1.18f, c + Offset(0.7f * u, 0.9f * u))
        drawCircle(fondo, r * 1.15f, c)
        drawCircle(mezcla(P.LATON, P.LUNA, 0.6f), r * 1.15f, c, style = Stroke(0.8f * u))
        drawCircle(P.LUNA, r * 0.86f, c - Offset(r * 0.1f, 0f))
        drawCircle(fondo, r * 0.7f, c + Offset(r * 0.25f, -r * 0.16f))
    }

    /** Lo de todas: la sombra que dejan en el fondo por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AirePirata, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.45f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.45f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira y el roce frío de la luna en el de la izquierda (de noche). */
    private fun DrawScope.filos(cuerpo: Rect, a: AirePirata, k: Float, f: Float, hacia: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.75f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (f > 0.03f) drawRect(a.lunar(0.55f * f), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    // ---------------------------------------------------------------- el cuaderno de bitácora

    fun DrawScope.cuaderno(v: Volumen, medidor: TextMeasurer, a: AirePirata, k: Float, f: Float, hacia: Float, tinte: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val top = h * 0.07f
        alrededor(a, hacia, top)
        val t = tinte(tinte, a, k)
        val lomo = Rect(0.5f * u, top, w - 0.5f * u, h)
        // Las hojas sueltas que asoman por la cabeza, más con más palabras.
        val sueltas = (v.lleno * 3.4f).toInt().coerceIn(0, 3)
        for (j in 0 until sueltas) {
            val x0 = w * (0.12f + 0.25f * j + 0.06f * azar(s, 40 + j)); val an = w * (0.22f + 0.14f * azar(s, 43 + j))
            val alto = top * (0.55f + 0.45f * azar(s, 46 + j))
            val hoja = Rect(x0, top - alto, x0 + an, top + 2f * u)
            drawRect(a.sombra(0.3f), Offset(hoja.left + 1f * u, hoja.top + 1f * u), hoja.size)
            drawRect(a.etiqueta(mezcla(P.PAPEL, P.PAPEL_OSC, 0.2f + 0.2f * j), k), hoja.topLeft, hoja.size)
            drawLine(a.luz(P.TINTA, k).copy(alpha = 0.3f), Offset(hoja.left + an * 0.2f, hoja.top + alto * 0.4f), Offset(hoja.right - an * 0.2f, hoja.top + alto * 0.4f), 0.6f * u)
        }
        // El lomo redondo, por franjas.
        redondo(lomo, t, hacia)
        // El grano y el roce del cuero o de la vitela, y una mancha de agua.
        for (j in 0 until 4) {
            val y = lomo.top + lomo.height * (0.1f + 0.85f * azar(s, 50 + j))
            drawLine(a.claro(0.18f), Offset(lomo.left + w * 0.2f * azar(s, 54 + j), y), Offset(lomo.left + w * (0.3f + 0.5f * azar(s, 58 + j)), y + 0.6f * u), 0.8f * u)
        }
        drawOval(a.sombra(0.12f), Offset(lomo.left + w * 0.1f, lomo.top + lomo.height * (0.78f + 0.1f * azar(s, 62))), Size(w * 0.6f, h * 0.06f))
        // Las cofias: la vuelta del cuero arriba y abajo.
        drawRoundRect(t.claro, Offset(lomo.left, lomo.top - 0.6f * u), Size(lomo.width, h * 0.024f), CornerRadius(w * 0.12f))
        drawRect(a.sombra(0.35f), Offset(lomo.left, lomo.top + h * 0.024f - 0.6f * u), Size(lomo.width, 1.2f * u))
        // Los nervios: el lomo de cada uno con su luz y su sombra.
        listOf(0.13f, 0.448f, 0.59f, 0.74f).forEach { fy ->
            val y = h * fy
            drawRect(a.sombra(0.42f), Offset(lomo.left, y + 1.8f * u), Size(lomo.width, 1.3f * u))
            drawRect(t.hondo, Offset(lomo.left, y), Size(lomo.width, 2.4f * u))
            drawRect(t.claro, Offset(lomo.left, y - 0.6f * u), Size(lomo.width, 1.5f * u))
            drawRect(a.brillo(0.35f, k), Offset(if (hacia < 0f) lomo.left + lomo.width * 0.1f else lomo.right - lomo.width * 0.4f, y - 0.4f * u), Size(lomo.width * 0.3f, 0.8f * u))
        }
        // El tejuelo de papel con el día y el mes, entre el primer y el segundo nervio.
        val et = Rect(w * 0.11f, h * 0.158f, w * 0.89f, h * 0.428f)
        drawRect(a.sombra(0.35f), Offset(et.left + 0.9f * u, et.top + 1.1f * u), et.size)
        drawRect(a.etiqueta(VIEJO, k), et.topLeft, et.size)
        drawRect(a.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.5f), Offset(et.left, et.bottom - et.height * 0.12f), Size(et.width, et.height * 0.12f))
        drawRect(a.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.4f), et.topLeft, Size(et.width * 0.08f, et.height))
        drawRect(a.luz(P.TINTA, k).copy(alpha = 0.55f), et.deflate(1.3f * u).topLeft, et.deflate(1.3f * u).size, style = Stroke(0.5f * u))
        diaYMes(v, medidor, et.deflate(1.7f * u), a.luz(P.TINTA, maxOf(k, 0.75f)))
        // La luna de los Sueños, entre el segundo y el tercer nervio.
        if (v.noche) luna(Offset(w / 2, h * 0.513f), minOf(w * 0.22f, 5.2f * u), a)
        // La cinta de cuero que ciñe el cuaderno, con su nudo y los cabos que cuelgan.
        val yc = h * 0.665f
        val cinta = a.luz(Color(0xFF4E2A18), k)
        drawRect(a.sombra(0.4f), Offset(lomo.left, yc + 1.5f * u), Size(lomo.width, 2.6f * u))
        drawRect(cinta, Offset(lomo.left, yc - 1.4f * u), Size(lomo.width, 2.8f * u))
        drawRect(a.brillo(0.3f, k), Offset(lomo.left, yc - 1.1f * u), Size(lomo.width, 0.6f * u))
        val nudo = Offset(w * (0.38f + 0.24f * azar(s, 66)), yc)
        val cabos = Path().apply {
            moveTo(nudo.x, nudo.y); cubicTo(nudo.x - w * 0.08f, nudo.y + h * 0.02f, nudo.x - w * 0.02f, nudo.y + h * 0.07f, nudo.x - w * 0.1f, nudo.y + h * 0.1f)
            moveTo(nudo.x, nudo.y); quadraticTo(nudo.x + w * 0.07f, nudo.y - h * 0.005f, nudo.x + w * 0.1f, nudo.y + h * 0.022f)
        }
        drawPath(cabos, cinta, style = Stroke(1.7f * u, cap = StrokeCap.Round))
        drawCircle(cinta, 2.6f * u, nudo)
        drawCircle(a.brillo(0.4f, k), 0.9f * u, nudo - Offset(0.8f * u, 0.8f * u))
        // Abajo, un doble filete gofrado.
        val yf = h * 0.79f
        drawLine(t.hondo, Offset(lomo.left + w * 0.12f, yf), Offset(lomo.right - w * 0.12f, yf), 0.9f * u)
        drawLine(t.claro, Offset(lomo.left + w * 0.12f, yf + 1.3f * u), Offset(lomo.right - w * 0.12f, yf + 1.3f * u), 0.7f * u)
        filos(lomo, a, k, f, hacia)
        varillaPirata(0f, w, h - MuebleEstantePirata.RIEL * u, a, k)
    }

    // ---------------------------------------------------------------- el canuto de cartas

    fun DrawScope.canuto(v: Volumen, medidor: TextMeasurer, a: AirePirata, k: Float, f: Float, hacia: Float, tinte: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val abierto = v.lleno >= 0.4f
        val an = w * 0.82f
        val cx = w * 0.5f
        val boca = h * (if (abierto) 0.2f else 0.15f)
        alrededor(a, hacia, boca)
        val t = tinte(tinte, a, k)
        val tubo = Rect(cx - an / 2, boca, cx + an / 2, h)
        if (abierto) {
            // Las cartas enrolladas que asoman por la boca abierta, más con más palabras.
            val rollos = 2 + ((v.lleno - 0.4f) * 3.4f).toInt().coerceIn(0, 2)
            for (j in 0 until rollos) {
                val rr = an * (0.2f - 0.015f * j)
                val x = cx + an * (-0.26f + 0.52f * j / (rollos - 1)) * 0.82f
                val alto = h * (0.07f + 0.11f * azar(s, 70 + j))
                val r = Rect(x - rr, boca - alto, x + rr, boca + 2f * u)
                val papel = mezcla(P.PAPEL, P.PAPEL_OSC, 0.3f * azar(s, 74 + j))
                drawRect(a.etiqueta(papel, k), r.topLeft, r.size)
                drawRect(a.sombra(0.28f), Offset(if (hacia < 0f) r.right - rr * 0.6f else r.left, r.top), Size(rr * 0.6f, r.height))
                drawOval(a.etiqueta(mezcla(papel, P.PAPEL_OSC, 0.35f), k), Offset(r.left, r.top - rr * 0.35f), Size(r.width, rr * 0.7f))
                drawArc(a.sombra(0.45f), 200f, 250f, false, Offset(x - rr * 0.5f, r.top - rr * 0.18f), Size(rr, rr * 0.36f), style = Stroke(0.7f * u))
                if (j == 0) drawRect(a.luz(P.LACRE, k), Offset(r.left, r.top + r.height * 0.35f), Size(r.width, 2f * u))
            }
        }
        // El cuerpo de cuero, redondo.
        redondo(tubo, t, hacia)
        // La costura de la piel, a lo largo, con su puntada.
        val xc = tubo.left + tubo.width * (if (hacia < 0f) 0.68f else 0.32f)
        drawLine(a.sombra(0.4f), Offset(xc, tubo.top), Offset(xc, tubo.bottom), 1.1f * u)
        drawLine(a.luz(P.CABO, k).copy(alpha = 0.85f), Offset(xc + 1.3f * u, tubo.top), Offset(xc + 1.3f * u, tubo.bottom), 1f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 2f * u)))
        // La correa de cuero que va del aro de arriba al de abajo, por el lado de la luz, con su costura.
        val xr = tubo.left + tubo.width * (if (hacia < 0f) 0.16f else 0.84f)
        val correa = Rect(xr - w * 0.06f, boca, xr + w * 0.06f, h * 0.75f)
        drawRect(a.sombra(0.35f), Offset(correa.left + (if (hacia < 0f) 1.2f else -1.2f) * u, correa.top), correa.size)
        drawRect(a.luz(Color(0xFF4A2816), k), correa.topLeft, correa.size)
        drawRect(a.brillo(0.25f, k), Offset(correa.left + correa.width * 0.15f, correa.top), Size(correa.width * 0.2f, correa.height))
        drawLine(a.luz(P.CABO, k).copy(alpha = 0.7f), Offset(correa.center.x, correa.top), Offset(correa.center.x, correa.bottom), 0.6f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.4f * u)))
        // Los aros de latón: el cuello de la boca y uno abajo.
        listOf(boca to h * 0.055f, h * 0.71f to h * 0.045f).forEach { (y, g) ->
            val r = Rect(tubo.left - 1.2f * u, y, tubo.right + 1.2f * u, y + g)
            drawRect(a.luz(P.LATON_OSC, k), r.topLeft, r.size)
            drawRect(a.luz(P.LATON, k), Offset(r.left, r.top), Size(r.width, g * 0.58f))
            drawRect(a.brillo(0.6f, k), Offset(if (hacia < 0f) r.left + r.width * 0.12f else r.right - r.width * 0.3f, r.top + 0.6f * u), Size(r.width * 0.18f, g * 0.45f))
            drawRect(a.sombra(0.4f), Offset(r.left, r.bottom), Size(r.width, 1.5f * u))
            drawLine(a.luz(P.LATON_OSC, k), Offset(r.left, r.top + g * 0.62f), Offset(r.right, r.top + g * 0.62f), 0.7f * u)
        }
        if (abierto) {
            drawOval(a.luz(P.LATON_OSC, k), Offset(tubo.left - 1.2f * u, boca - h * 0.014f), Size(tubo.width + 2.4f * u, h * 0.03f), style = Stroke(1.6f * u))
            // La tapa, colgando de su cadenilla por el lado de la sombra.
            val lado = if (hacia < 0f) 1f else -1f
            val tx = cx + lado * an * 0.36f
            val tapa = Rect(tx - w * 0.15f, h * 0.27f, tx + w * 0.15f, h * 0.35f)
            val cad = Path().apply { moveTo(cx + lado * an * 0.38f, boca + h * 0.03f); quadraticTo(tx + lado * w * 0.12f, boca + h * 0.05f, tapa.center.x + lado * w * 0.06f, tapa.top) }
            drawPath(cad, a.luz(P.LATON_OSC, k), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * u, 0.9f * u))))
            drawRoundRect(a.sombra(0.4f), tapa.topLeft + Offset(1f * u, 1.2f * u), tapa.size, CornerRadius(w * 0.06f))
            drawRoundRect(a.luz(P.LATON_OSC, k), tapa.topLeft, tapa.size, CornerRadius(w * 0.06f))
            drawRoundRect(a.luz(P.LATON, k), Offset(tapa.left, tapa.top), Size(tapa.width * 0.6f, tapa.height), CornerRadius(w * 0.06f))
            drawOval(a.luz(mezcla(P.LATON_OSC, P.TINTA, 0.3f), k), Offset(tapa.left + 1f * u, tapa.bottom - tapa.height * 0.4f), Size(tapa.width - 2f * u, tapa.height * 0.45f))
            destelloPirata(Offset(tapa.left + tapa.width * 0.25f, tapa.top + tapa.height * 0.3f), 1.1f * u, a, k)
        } else {
            // La tapa abombada puesta, con su anilla.
            val tp = Rect(tubo.left - 1.6f * u, h * 0.06f, tubo.right + 1.6f * u, boca + h * 0.03f)
            drawRoundRect(a.luz(P.LATON_OSC, k), tp.topLeft, tp.size, CornerRadius(tp.width * 0.5f, tp.height * 0.6f))
            clipPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(tp, CornerRadius(tp.width * 0.5f, tp.height * 0.6f))) }) {
                drawRect(a.luz(P.LATON, k), Offset(if (hacia < 0f) tp.left else tp.left + tp.width * 0.4f, tp.top), Size(tp.width * 0.6f, tp.height))
                drawRect(a.brillo(0.6f, k), Offset(if (hacia < 0f) tp.left + tp.width * 0.18f else tp.right - tp.width * 0.3f, tp.top), Size(tp.width * 0.12f, tp.height))
                drawRect(a.sombra(0.35f), Offset(tp.left, tp.bottom - tp.height * 0.22f), Size(tp.width, tp.height * 0.22f))
            }
            drawArc(a.luz(P.LATON_OSC, k), 180f, 180f, false, Offset(cx - w * 0.1f, h * 0.0f + 1f * u), Size(w * 0.2f, h * 0.12f), style = Stroke(1.6f * u))
        }
        // La etiqueta de pergamino atada con bramante al cuello, con el día y el mes.
        val et = Rect(w * 0.07f, h * 0.345f, w * 0.93f, h * 0.62f)
        drawLine(a.luz(P.CABO, k), Offset(cx - w * 0.14f, boca + h * 0.045f), Offset(et.center.x, et.top + h * 0.02f), 1f * u)
        drawLine(a.luz(P.CABO, k), Offset(cx + w * 0.16f, boca + h * 0.045f), Offset(et.center.x, et.top + h * 0.02f), 1f * u)
        val forma = poligonoPirata(et.left + et.width * 0.16f, et.top, et.right - et.width * 0.16f, et.top, et.right, et.top + et.height * 0.12f,
            et.right, et.bottom, et.left, et.bottom, et.left, et.top + et.height * 0.12f)
        translate(1f * u, 1.3f * u) { drawPath(forma, a.sombra(0.4f)) }
        drawPath(forma, a.etiqueta(mezcla(VIEJO, P.PAPEL_OSC, 0.15f), k))
        drawCircle(a.luz(P.CABO_OSC, k), 1.3f * u, Offset(et.center.x, et.top + h * 0.02f))
        drawRect(a.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.4f), Offset(et.left, et.bottom - et.height * 0.14f), Size(et.width, et.height * 0.14f))
        diaYMes(v, medidor, Rect(et.left + 1.5f * u, et.top + et.height * 0.12f, et.right - 1.5f * u, et.bottom - 1.6f * u), a.luz(P.TINTA, maxOf(k, 0.75f)))
        if (v.noche) luna(Offset(cx, h * 0.665f), minOf(w * 0.22f, 5.2f * u), a)
        filos(tubo, a, k, f, hacia)
        varillaPirata(0f, w, h - MuebleEstantePirata.RIEL * u, a, k)
    }

    // ---------------------------------------------------------------- el paquete de hule

    /**
     * El paquete de la correspondencia envuelto en hule (la tela encerada que no deja pasar el agua), de pie: blando, con los
     * costados un poco abombados (más con más palabras), la solapa del envoltorio doblada en pico por arriba, las arrugas y el
     * lustre del hule, dos ataduras de bramante (la de abajo, con más vueltas cuanto más se escribió) y el lacre en el nudo; el
     * día en una etiqueta de papel metida bajo el bramante de arriba. En los Sueños, el lacre es una luna de plata.
     */
    fun DrawScope.paquete(v: Volumen, medidor: TextMeasurer, a: AirePirata, k: Float, f: Float, hacia: Float, tinte: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val top = h * 0.05f
        alrededor(a, hacia, top)
        val t = tinte(tinte, a, k)
        val panza = w * (0.02f + 0.035f * v.lleno)
        val m = 1.2f * u
        // El cuerpo blando: los costados abombados y las esquinas redondeadas.
        val cuerpo = Path().apply {
            moveTo(m + w * 0.06f, top)
            lineTo(w - m - w * 0.06f, top)
            quadraticTo(w - m, top, w - m, top + h * 0.05f)
            quadraticTo(w - m + panza, h * 0.55f, w - m, h - h * 0.03f)
            quadraticTo(w - m, h, w - m - w * 0.08f, h)
            lineTo(m + w * 0.08f, h)
            quadraticTo(m, h, m, h - h * 0.03f)
            quadraticTo(m - panza, h * 0.55f, m, top + h * 0.05f)
            quadraticTo(m, top, m + w * 0.06f, top)
            close()
        }
        drawPath(cuerpo, t.cara)
        clipPath(cuerpo) {
            redondo(Rect(-panza, top, w + panza, h), t, hacia)
            // El lustre del hule: una veta de luz que ondula a lo largo y otra más fina.
            val lz = if (hacia < 0f) 0.26f else 0.7f
            val lustre = Path().apply {
                moveTo(w * lz, top); cubicTo(w * (lz + 0.08f), h * 0.35f, w * (lz - 0.07f), h * 0.6f, w * (lz + 0.04f), h)
            }
            drawPath(lustre, a.brillo(0.32f, k), style = Stroke(w * 0.07f, cap = StrokeCap.Round))
            drawPath(lustre, a.brillo(0.5f, k), style = Stroke(w * 0.018f, cap = StrokeCap.Round))
            // Las arrugas del hule: pliegues largos y casi rectos, con su lomo de luz.
            val arrugas = Path(); val lomos = Path()
            for (j in 0 until 3) {
                val y0 = h * (0.62f + 0.1f * j + 0.04f * azar(s, 80 + j))
                val x0 = w * (if (j % 2 == 0) 0.0f else 0.35f) + w * 0.1f * azar(s, 84 + j)
                val x1 = x0 + w * (0.45f + 0.2f * azar(s, 87 + j))
                arrugas.moveTo(x0, y0); arrugas.lineTo(x1, y0 + h * 0.025f)
                lomos.moveTo(x0, y0 + 1.4f * u); lomos.lineTo(x1, y0 + h * 0.025f + 1.4f * u)
            }
            drawPath(arrugas, mezcla(t.cara, t.hondo, 0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            drawPath(lomos, mezcla(t.cara, t.claro, 0.6f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            // La solapa del envoltorio, doblada en pico desde arriba, con su sombra.
            val pico = h * (0.27f + 0.03f * azar(s, 88))
            val solapa = poligonoPirata(-panza, top, w + panza, top, w * 0.55f, pico, w * 0.45f, pico)
            translate(0f, 2f * u) { drawPath(solapa, a.sombra(0.35f)) }
            drawPath(solapa, t.cara)
            drawPath(poligonoPirata(-panza, top, w * 0.5f, top, w * 0.45f, pico), if (hacia < 0f) t.claro else mezcla(t.cara, t.hondo, 0.4f))
            drawLine(t.claro, Offset(w * 0.03f, top + 1.5f * u), Offset(w * 0.45f, pico - 1f * u), 0.9f * u)
            drawRect(a.sombra(0.3f), Offset(0f, h * 0.92f), Size(w, h * 0.08f))
        }
        drawPath(cuerpo, a.sombra(0.3f), style = Stroke(0.7f * u))
        // El sobrescrito: la tira de papel con el día y el mes metida bajo el bramante, con la raya roja del escribano.
        val et = Rect(w * 0.08f, h * 0.3f, w * 0.92f, h * 0.595f)
        drawRect(a.sombra(0.38f), Offset(et.left + 1f * u, et.top + 1.3f * u), et.size)
        drawRect(a.etiqueta(VIEJO, k), et.topLeft, et.size)
        drawRect(a.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.45f), Offset(et.left, et.bottom - et.height * 0.1f), Size(et.width, et.height * 0.1f))
        drawLine(a.luz(P.LACRE, maxOf(k, 0.5f)).copy(alpha = 0.75f), Offset(et.left + 2.4f * u, et.bottom - et.height * 0.2f), Offset(et.right - 2.4f * u, et.bottom - et.height * 0.2f), 0.8f * u)
        diaYMes(v, medidor, Rect(et.left + 1.4f * u, et.top + 1f * u, et.right - 1.4f * u, et.bottom - et.height * 0.18f), a.luz(P.TINTA, maxOf(k, 0.75f)))
        // Las ataduras de bramante: la de arriba, sobre la etiqueta; la de abajo, con su nudo y su lacre.
        atadura(h * 0.272f, 1, panza, a, k)
        val yb = h * 0.7f
        atadura(yb, 1 + (v.lleno * 2.4f).toInt().coerceIn(0, 2), panza, a, k)
        val nudo = Offset(w * (0.42f + 0.16f * azar(s, 90)), yb + 1.5f * u)
        drawLine(a.luz(P.CABO, k), nudo, nudo + Offset(-w * 0.16f, h * 0.07f), 1.3f * u, StrokeCap.Round)
        drawLine(a.luz(P.CABO, k), nudo, nudo + Offset(w * 0.1f, h * 0.08f), 1.3f * u, StrokeCap.Round)
        if (v.noche) luna(nudo, minOf(w * 0.22f, 5.2f * u), a)
        else {
            val r = minOf(w * 0.16f, 4.4f * u)
            drawCircle(a.sombra(0.4f), r * 1.05f, nudo + Offset(0.8f * u, 1f * u))
            drawCircle(a.luz(P.LACRE, k), r, nudo)
            drawCircle(a.luz(mezcla(P.LACRE, P.TINTA, 0.45f), k), r * 0.62f, nudo, style = Stroke(0.8f * u))
            drawCircle(a.brillo(0.45f, k), r * 0.24f, nudo - Offset(r * 0.35f, r * 0.35f))
        }
        filos(Rect(m, top, w - m, h), a, k, f, hacia)
        varillaPirata(0f, w, h - MuebleEstantePirata.RIEL * u, a, k)
    }

    /** Una atadura de bramante a lo ancho del paquete en [y], con [vueltas] vueltas, su sombra y su luz. */
    private fun DrawScope.atadura(y: Float, vueltas: Int, panza: Float, a: AirePirata, k: Float) {
        val u = density; val w = size.width
        for (j in 0 until vueltas) {
            val yy = y + j * 2.2f * u
            drawLine(a.sombra(0.35f), Offset(0f, yy + 1.4f * u), Offset(w, yy + 1.4f * u), 1.4f * u)
            drawLine(a.luz(P.CABO_OSC, k), Offset(-panza, yy + 0.5f * u), Offset(w + panza, yy + 0.5f * u), 1.8f * u)
            drawLine(a.luz(P.CABO, k), Offset(-panza, yy), Offset(w + panza, yy), 1f * u)
        }
    }
}
