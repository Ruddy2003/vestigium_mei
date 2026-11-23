package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
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
 * sueltas por la cabeza), el canuto de cartas (un estuche de cuero cosido con su tapa y su correa con hebilla; si va
 * lleno, la tapa cuelga de la correa y asoma el rollo de la carta; el día en una etiqueta de pergamino atada con
 * bramante) y el paquete de la correspondencia envuelto en hule (blando, con la cabeza fruncida, embalado con bramante y
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

    /** La media luna de plata, de radio [r], armada una vez por tamaño (un creciente: el disco menos otro corrido). */
    private var radioLuna = -1f
    private var caminoLuna = Path()

    private fun creciente(r: Float): Path {
        if (r != radioLuna) {
            val fuera = Path().apply { addOval(Rect(-r, -r, r, r)) }
            val dentro = Path().apply { addOval(Rect(-r * 0.78f + r * 0.5f, -r * 0.82f - r * 0.22f, r * 0.78f + r * 0.5f, r * 0.82f - r * 0.22f)) }
            caminoLuna = Path().apply { op(fuera, dentro, PathOperation.Difference) }
            radioLuna = r
        }
        return caminoLuna
    }

    /**
     * La luna de los Sueños: una media luna de plata recortada, sin disco alrededor (un círculo claro con su hueco oscuro, en
     * fila, se leía como una hilera de ojos), con su canto oscuro para que se vea sobre lo claro y su sombra.
     */
    private fun DrawScope.luna(c: Offset, r: Float, a: AirePirata) {
        val u = density
        val m = creciente(r)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, a.sombra(0.55f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFDDE4F0))
            drawPath(m, Color(0xFF3A4258), style = Stroke(0.9f * u))
            drawCircle(Color.White.copy(alpha = 0.85f), r * 0.16f, Offset(-r * 0.62f, -r * 0.18f))
        }
    }

    /** El plano de luna de los Sueños oscuros sobre una entrada que cae dentro de él ([f], de 0 a 1): fría, más en su canto. */
    fun DrawScope.lunaEncima(f: Float) {
        val u = density
        drawRect(P.LUNA_RAS.copy(alpha = (0.16f * f).coerceAtMost(0.2f)), Offset.Zero, size)
        drawRect(P.LUNA_RAS.copy(alpha = (0.5f * f).coerceAtMost(0.5f)), Offset.Zero, Size(1.6f * u, size.height))
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
        if (v.noche) luna(Offset(w / 2, h * 0.516f), minOf(w * 0.24f, 6.4f * u), a)
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

    /**
     * El canuto de cartas: un estuche de cuero cosido a lo largo, con su vira cosida en la boca y otra abajo, y la tapa de cuero
     * con su correa, que baja por el costado hasta su hebilla de latón. Con pocas palabras, la tapa puesta; con muchas, la tapa
     * cuelga de la correa y asoma por la boca el rollo de la carta con su cinta. El día, en una etiqueta de pergamino atada.
     */
    fun DrawScope.canuto(v: Volumen, medidor: TextMeasurer, a: AirePirata, k: Float, f: Float, hacia: Float, tinte: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val abierto = v.lleno >= 0.4f
        val an = w * 0.8f
        val cx = w * 0.5f
        val boca = h * (if (abierto) 0.2f else 0.17f)
        alrededor(a, hacia, if (abierto) boca else h * 0.05f)
        val t = tinte(tinte, a, k)
        val m = tinte ?: P.EMBREADO
        val oscuroCuero = tinte(mezcla(m, P.BREA, 0.42f), a, k)
        val tubo = Rect(cx - an / 2, boca, cx + an / 2, h)
        val lado = if (hacia < 0f) -1f else 1f
        if (abierto) {
            // El rollo de la carta que asoma por la boca, y otro más delgado detrás.
            listOf(Triple(-0.12f, 0.17f, 0.36f), Triple(0.14f, 0.12f, 0.27f)).forEachIndexed { j, (dx, alto, gr) ->
                val rr = an * gr
                val x = cx + an * dx
                val r = Rect(x - rr, boca - h * (alto + 0.03f * azar(s, 70 + j)), x + rr, boca + 2f * u)
                val papel = mezcla(P.PAPEL, P.PAPEL_OSC, 0.12f + 0.25f * j)
                drawRect(a.etiqueta(papel, k), r.topLeft, r.size)
                drawRect(a.sombra(0.3f), Offset(if (hacia < 0f) r.right - rr * 0.7f else r.left, r.top), Size(rr * 0.7f, r.height))
                drawRect(a.brillo(0.3f, k), Offset(if (hacia < 0f) r.left + rr * 0.25f else r.right - rr * 0.45f, r.top), Size(rr * 0.2f, r.height))
                drawOval(a.etiqueta(mezcla(papel, P.PAPEL_OSC, 0.4f), k), Offset(r.left, r.top - rr * 0.32f), Size(r.width, rr * 0.64f))
                drawArc(a.sombra(0.5f), 200f, 250f, false, Offset(x - rr * 0.55f, r.top - rr * 0.18f), Size(rr * 1.1f, rr * 0.36f), style = Stroke(0.8f * u))
                drawArc(a.sombra(0.35f), 210f, 220f, false, Offset(x - rr * 0.28f, r.top - rr * 0.09f), Size(rr * 0.56f, rr * 0.18f), style = Stroke(0.6f * u))
                if (j == 0) {
                    drawRect(a.luz(P.LACRE, k), Offset(r.left, r.top + r.height * 0.42f), Size(r.width, 2.4f * u))
                    drawLine(a.luz(P.LACRE, k), Offset(x + rr * 0.6f, r.top + r.height * 0.42f + 2f * u), Offset(x + rr * 1.1f, r.top + r.height * 0.42f + h * 0.05f), 1.6f * u, StrokeCap.Round)
                }
            }
        }
        // El cuerpo de cuero, redondo, y la vira cosida de la boca.
        redondo(tubo, t, hacia)
        drawRect(oscuroCuero.cara, Offset(tubo.left - 0.8f * u, boca), Size(tubo.width + 1.6f * u, h * 0.035f))
        drawLine(a.luz(P.CABO, k).copy(alpha = 0.75f), Offset(tubo.left, boca + h * 0.026f), Offset(tubo.right, boca + h * 0.026f), 0.8f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.8f * u, 1.6f * u)))
        drawRect(a.brillo(0.3f, k), Offset(tubo.left, boca), Size(tubo.width, 0.8f * u))
        // La costura a lo largo, con su puntada.
        val xc = tubo.left + tubo.width * (if (hacia < 0f) 0.7f else 0.3f)
        drawLine(a.sombra(0.4f), Offset(xc, boca), Offset(xc, h), 1.1f * u)
        drawLine(a.luz(P.CABO, k).copy(alpha = 0.85f), Offset(xc + 1.3f * u, boca), Offset(xc + 1.3f * u, h), 1f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 2f * u)))
        // La correa, por el lado de la luz, de la tapa a la hebilla.
        val xr = cx - lado * an * 0.3f
        val hebillaY = h * 0.7f
        val correa = Rect(xr - w * 0.065f, if (abierto) boca else h * 0.1f, xr + w * 0.065f, hebillaY + h * 0.06f)
        drawRect(a.sombra(0.35f), Offset(correa.left - lado * 1.2f * u, correa.top + 1f * u), correa.size)
        drawRect(oscuroCuero.cara, correa.topLeft, correa.size)
        drawRect(oscuroCuero.claro, Offset(correa.left, correa.top), Size(correa.width * 0.3f, correa.height))
        drawLine(a.luz(P.CABO, k).copy(alpha = 0.7f), Offset(correa.center.x, correa.top), Offset(correa.center.x, correa.bottom), 0.6f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.4f * u)))
        val hebilla = Rect(correa.left - 1.2f * u, hebillaY - h * 0.025f, correa.right + 1.2f * u, hebillaY + h * 0.025f)
        drawRect(a.luz(P.LATON, k), hebilla.topLeft, hebilla.size, style = Stroke(1.6f * u))
        drawLine(a.luz(P.LATON_OSC, k), Offset(hebilla.center.x, hebilla.top), Offset(hebilla.center.x, hebilla.bottom), 1.2f * u)
        destelloPirata(Offset(hebilla.left + 1f * u, hebilla.top + 1f * u), 0.9f * u, a, k)
        if (abierto) {
            // La tapa, colgando de su correa por el lado de la sombra, ladeada.
            val tc = Offset(cx + lado * an * 0.32f, h * 0.27f)
            rotate(lado * 18f, tc) {
                val tp = Rect(tc.x - an * 0.34f, tc.y - h * 0.055f, tc.x + an * 0.34f, tc.y + h * 0.055f)
                drawRoundRect(a.sombra(0.4f), tp.topLeft + Offset(1f * u, 1.4f * u), tp.size, CornerRadius(3f * u))
                drawRoundRect(oscuroCuero.cara, tp.topLeft, tp.size, CornerRadius(3f * u))
                drawRect(oscuroCuero.claro, Offset(tp.left + 1f * u, tp.top + 1f * u), Size(tp.width - 2f * u, tp.height * 0.25f))
                drawOval(a.luz(P.BREA, k), Offset(tp.left + 1.5f * u, tp.bottom - tp.height * 0.32f), Size(tp.width - 3f * u, tp.height * 0.4f))
                drawLine(a.luz(P.CABO, k).copy(alpha = 0.7f), Offset(tp.left + 2f * u, tp.top + tp.height * 0.55f), Offset(tp.right - 2f * u, tp.top + tp.height * 0.55f), 0.7f * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.4f * u)))
            }
        } else {
            // La tapa de cuero puesta, algo más ancha, con su vira cosida y un remache.
            val tp = Rect(tubo.left - 1.8f * u, h * 0.05f, tubo.right + 1.8f * u, boca + h * 0.02f)
            drawRoundRect(oscuroCuero.cara, tp.topLeft, tp.size, CornerRadius(tp.width * 0.22f, tp.height * 0.35f))
            redondo(Rect(tp.left, tp.top + tp.height * 0.3f, tp.right, tp.bottom), oscuroCuero, hacia)
            drawOval(oscuroCuero.claro, Offset(tp.left + tp.width * 0.08f, tp.top), Size(tp.width * 0.84f, tp.height * 0.42f))
            drawLine(a.luz(P.CABO, k).copy(alpha = 0.75f), Offset(tp.left + 1f * u, tp.bottom - h * 0.018f), Offset(tp.right - 1f * u, tp.bottom - h * 0.018f), 0.8f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.8f * u, 1.6f * u)))
            drawRect(a.sombra(0.4f), Offset(tp.left, tp.bottom), Size(tp.width, 1.4f * u))
            remachePirata(Offset(xr, tp.top + tp.height * 0.6f), 1.5f * u, a, k, P.LATON)
            // La gaza de la correa por encima de la tapa, para colgarlo.
            val gaza = Path().apply { moveTo(xr, tp.top + tp.height * 0.45f); cubicTo(xr, tp.top - h * 0.055f, cx + lado * an * 0.2f, tp.top - h * 0.055f, cx + lado * an * 0.25f, tp.top + tp.height * 0.3f) }
            drawPath(gaza, a.sombra(0.35f), style = Stroke(w * 0.11f))
            drawPath(gaza, oscuroCuero.cara, style = Stroke(w * 0.09f))
            drawPath(gaza, oscuroCuero.claro, style = Stroke(w * 0.025f))
        }
        // La etiqueta de pergamino atada con bramante a la vira, con el día y el mes.
        val et = Rect(w * 0.07f, h * 0.345f, w * 0.93f, h * 0.62f)
        drawLine(a.luz(P.CABO, k), Offset(cx - w * 0.14f, boca + h * 0.03f), Offset(et.center.x, et.top + h * 0.02f), 1f * u)
        drawLine(a.luz(P.CABO, k), Offset(cx + w * 0.16f, boca + h * 0.03f), Offset(et.center.x, et.top + h * 0.02f), 1f * u)
        val forma = poligonoPirata(et.left + et.width * 0.16f, et.top, et.right - et.width * 0.16f, et.top, et.right, et.top + et.height * 0.12f,
            et.right, et.bottom, et.left, et.bottom, et.left, et.top + et.height * 0.12f)
        translate(1f * u, 1.3f * u) { drawPath(forma, a.sombra(0.4f)) }
        drawPath(forma, a.etiqueta(mezcla(VIEJO, P.PAPEL_OSC, 0.15f), k))
        drawCircle(a.luz(P.CABO_OSC, k), 1.3f * u, Offset(et.center.x, et.top + h * 0.02f))
        drawRect(a.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.4f), Offset(et.left, et.bottom - et.height * 0.14f), Size(et.width, et.height * 0.14f))
        diaYMes(v, medidor, Rect(et.left + 1.5f * u, et.top + et.height * 0.12f, et.right - 1.5f * u, et.bottom - 1.6f * u), a.luz(P.TINTA, maxOf(k, 0.75f)))
        if (v.noche) luna(Offset(cx + lado * w * 0.12f, h * 0.68f), minOf(w * 0.24f, 6.4f * u), a)
        filos(tubo, a, k, f, hacia)
        varillaPirata(0f, w, h - MuebleEstantePirata.RIEL * u, a, k)
    }

    // ---------------------------------------------------------------- el paquete de hule

    /**
     * El paquete de la correspondencia envuelto en hule (la tela encerada que no deja pasar el agua), de pie y blando: los
     * costados abombados (más con más palabras), la cabeza fruncida e irregular donde el envoltorio se recoge bajo el cordel,
     * las arrugas y el lustre del hule; el bramante que lo embala (una vuelta a lo alto, corrida de su eje y algo ladeada, y
     * las vueltas de abajo, más cuanto más se escribió) con el lacre junto al nudo, no en el cruce. El día, en un sobrescrito de
     * papel metido bajo el bramante. En los Sueños, junto al lacre, la media luna.
     */
    fun DrawScope.paquete(v: Volumen, medidor: TextMeasurer, a: AirePirata, k: Float, f: Float, hacia: Float, tinte: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val top = h * 0.06f
        alrededor(a, hacia, top)
        val t = tinte(tinte, a, k)
        val panza = w * (0.02f + 0.035f * v.lleno)
        val m = 1.2f * u
        val xv = w * (0.6f + 0.08f * azar(s, 93))
        // El cuerpo blando: la cabeza fruncida en lóbulos desiguales, los costados abombados y las esquinas de abajo redondas.
        val cuerpo = Path().apply {
            moveTo(m, top + h * 0.06f)
            quadraticTo(w * 0.1f, top - h * 0.01f, w * 0.24f, top + h * 0.02f)
            quadraticTo(w * 0.36f, top + h * 0.05f, w * 0.44f, top + h * 0.005f)
            quadraticTo(xv - w * 0.06f, top + h * 0.04f, xv, top + h * 0.045f)
            quadraticTo(xv + w * 0.12f, top - h * 0.015f, w * 0.84f, top + h * 0.015f)
            quadraticTo(w - m, top + h * 0.02f, w - m, top + h * 0.07f)
            quadraticTo(w - m + panza, h * 0.55f, w - m, h - h * 0.03f)
            quadraticTo(w - m, h, w - m - w * 0.08f, h)
            lineTo(m + w * 0.08f, h)
            quadraticTo(m, h, m, h - h * 0.03f)
            quadraticTo(m - panza, h * 0.55f, m, top + h * 0.06f)
            close()
        }
        drawPath(cuerpo, t.cara)
        clipPath(cuerpo) {
            redondo(Rect(-panza, top - h * 0.02f, w + panza, h), t, hacia)
            // Los frunces que bajan de la cabeza hacia donde la recoge el cordel.
            val frunces = Path(); val lomos = Path()
            listOf(0.14f, 0.32f, 0.82f).forEachIndexed { j, fx ->
                val x0 = w * fx; val y1 = top + h * (0.14f + 0.06f * azar(s, 96 + j))
                frunces.moveTo(x0, top + h * 0.02f); frunces.quadraticTo((x0 + xv) / 2, top + h * 0.06f, xv + (x0 - xv) * 0.3f, y1)
                lomos.moveTo(x0 + 1.6f * u, top + h * 0.03f); lomos.quadraticTo((x0 + xv) / 2 + 1.6f * u, top + h * 0.07f, xv + (x0 - xv) * 0.3f + 1.6f * u, y1 - 1.5f * u)
            }
            drawPath(frunces, t.hondo, style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(lomos, t.claro, style = Stroke(1f * u, cap = StrokeCap.Round))
            // El lustre del hule: una veta de luz que ondula a lo largo y otra más fina.
            val lz = if (hacia < 0f) 0.24f else 0.74f
            val lustre = Path().apply { moveTo(w * lz, top + h * 0.15f); cubicTo(w * (lz + 0.08f), h * 0.4f, w * (lz - 0.07f), h * 0.62f, w * (lz + 0.04f), h) }
            drawPath(lustre, a.brillo(0.3f, k), style = Stroke(w * 0.08f, cap = StrokeCap.Round))
            drawPath(lustre, a.brillo(0.5f, k), style = Stroke(w * 0.02f, cap = StrokeCap.Round))
            // Las arrugas del hule abajo: pliegues largos y casi rectos, con su lomo de luz.
            val arrugas = Path(); val lomos2 = Path()
            for (j in 0 until 3) {
                val y0 = h * (0.64f + 0.1f * j + 0.04f * azar(s, 80 + j))
                val x0 = w * (if (j % 2 == 0) 0.0f else 0.32f) + w * 0.1f * azar(s, 84 + j)
                val x1 = x0 + w * (0.42f + 0.2f * azar(s, 87 + j))
                arrugas.moveTo(x0, y0); arrugas.lineTo(x1, y0 + h * 0.03f)
                lomos2.moveTo(x0, y0 + 1.4f * u); lomos2.lineTo(x1, y0 + h * 0.03f + 1.4f * u)
            }
            drawPath(arrugas, mezcla(t.cara, t.hondo, 0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            drawPath(lomos2, mezcla(t.cara, t.claro, 0.6f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            drawRect(a.sombra(0.3f), Offset(0f, h * 0.92f), Size(w, h * 0.08f))
        }
        drawPath(cuerpo, a.sombra(0.3f), style = Stroke(0.7f * u))
        // El bramante a lo alto: corrido del eje y algo ladeado, pasa sobre la cabeza (donde la recoge) y baja por delante.
        val abajoX = xv + w * 0.05f
        val alto = Path().apply { moveTo(xv - w * 0.02f, top - h * 0.01f); quadraticTo(xv, top + h * 0.03f, xv + w * 0.004f, top + h * 0.06f); lineTo(abajoX, h) }
        drawPath(alto, a.sombra(0.35f), style = Stroke(1.6f * u))
        drawPath(alto, a.luz(P.CABO_OSC, k), style = Stroke(1.8f * u))
        drawPath(alto, a.luz(P.CABO, k), style = Stroke(1f * u))
        // El sobrescrito: la tira de papel con el día y el mes metida bajo el bramante, con la raya roja del escribano.
        val et = Rect(w * 0.08f, h * 0.29f, w * 0.92f, h * 0.585f)
        drawRect(a.sombra(0.38f), Offset(et.left + 1f * u, et.top + 1.3f * u), et.size)
        drawRect(a.etiqueta(VIEJO, k), et.topLeft, et.size)
        drawRect(a.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.45f), Offset(et.left, et.bottom - et.height * 0.1f), Size(et.width, et.height * 0.1f))
        drawLine(a.luz(P.LACRE, maxOf(k, 0.5f)).copy(alpha = 0.75f), Offset(et.left + 2.4f * u, et.bottom - et.height * 0.2f), Offset(et.right - 2.4f * u, et.bottom - et.height * 0.2f), 0.8f * u)
        diaYMes(v, medidor, Rect(et.left + 1.4f * u, et.top + 1f * u, et.right - 1.4f * u, et.bottom - et.height * 0.18f), a.luz(P.TINTA, maxOf(k, 0.75f)))
        // El bramante asoma por arriba y por abajo de la tira.
        drawLine(a.luz(P.CABO, k), Offset(xv + (abajoX - xv) * 0.27f, et.top - 2f * u), Offset(xv + (abajoX - xv) * 0.27f, et.top + 1.5f * u), 1.4f * u)
        // Las vueltas de abajo (más con más palabras), el nudo y el lacre a su lado.
        val yb = h * 0.7f
        val vueltas = 1 + (v.lleno * 2.4f).toInt().coerceIn(0, 2)
        for (j in 0 until vueltas) {
            val yy = yb + j * 2.3f * u + (if (j == 1) 0.6f * u else 0f)
            drawLine(a.sombra(0.35f), Offset(0f, yy + 1.4f * u), Offset(w, yy + 2.2f * u), 1.4f * u)
            drawLine(a.luz(P.CABO_OSC, k), Offset(-panza, yy + 0.5f * u), Offset(w + panza, yy + 1.2f * u), 1.8f * u)
            drawLine(a.luz(P.CABO, k), Offset(-panza, yy), Offset(w + panza, yy + 0.7f * u), 1f * u)
        }
        val nudo = Offset(xv + (abajoX - xv) * 0.62f, yb + 1.2f * u)
        drawCircle(a.luz(P.CABO, k), 2.6f * u, nudo)
        drawCircle(a.luz(P.CABO_OSC, k), 2.6f * u, nudo, style = Stroke(0.7f * u))
        val cabos = Path().apply {
            moveTo(nudo.x, nudo.y); cubicTo(nudo.x + w * 0.06f, nudo.y + h * 0.03f, nudo.x + w * 0.02f, nudo.y + h * 0.07f, nudo.x + w * 0.09f, nudo.y + h * 0.1f)
            moveTo(nudo.x, nudo.y); quadraticTo(nudo.x - w * 0.08f, nudo.y + h * 0.015f, nudo.x - w * 0.12f, nudo.y + h * 0.045f)
        }
        drawPath(cabos, a.luz(P.CABO, k), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        val sello = Offset(nudo.x - w * 0.26f, yb - h * 0.055f)
        if (v.noche) luna(sello, minOf(w * 0.24f, 6.4f * u), a)
        else {
            val r = minOf(w * 0.16f, 4.4f * u)
            drawCircle(a.sombra(0.4f), r * 1.05f, sello + Offset(0.8f * u, 1f * u))
            drawCircle(a.luz(P.LACRE, k), r, sello)
            drawCircle(a.luz(mezcla(P.LACRE, P.TINTA, 0.45f), k), r * 0.62f, sello, style = Stroke(0.8f * u))
            drawCircle(a.brillo(0.45f, k), r * 0.24f, sello - Offset(r * 0.35f, r * 0.35f))
        }
        filos(Rect(m, top, w - m, h), a, k, f, hacia)
        varillaPirata(0f, w, h - MuebleEstantePirata.RIEL * u, a, k)
    }
}
