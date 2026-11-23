package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
 * Las entradas del estante de Art nouveau ([MuebleEstanteNouveau]), tres objetos del despacho según la semilla: el tomo de la
 * revista ilustrada (alto y recto: percalina del color del ánimo, lomo redondo con sus nervios, el tejuelo de tafilete con el
 * día en oro, la flor del día dorada y una línea de látigo), el rollo de carteles (de uno a tres rollos, según las palabras,
 * envueltos en papel de seda teñido, con la espiral de las hojas impresas arriba, atados con bramante y con su etiqueta de
 * cartulina; la flor del día impresa en el papel) y el legajo de cartas (un mazo de sobres y pliegos de papel teñido, de cantos
 * desiguales, atado con una cinta de seda con su lazo, la tarjeta del día y la flor seca bajo la cinta). El ánimo es el tinte,
 * en todo el cuerpo ([tonoDeDiaNouveau]); sin ánimo, percalina habano o papel de estraza. En los Sueños, una media luna de plata.
 */
internal object VolumenEstanteNouveau {
    private val P = PaletaNouveau

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.42f, 0.82f, 0.48f, 0.1f, -0.2f, -0.52f, -0.82f)

    /** Los colores de un material con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(t: Color?, sin: Color, a: AireNouveau, k: Float): Tinte {
        val m = t ?: sin
        val claro = mezcla(m, luzMueble(1f), if (t == null) 0.22f else 0.32f)
        val hondo = mezcla(m, sombraMueble(1f), 0.55f)
        return if (t != null) Tinte(a.animo(m, k), a.animo(claro, k), a.animo(hondo, k))
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

    /** El mes como lo abrevia el almanaque, en francés: «Almanach de novembre» se vuelve «nov.». */
    private fun mesCorto(mes: String): String {
        val m = mes.lowercase()
        val tabla = listOf("janv" to "janv.", "févr" to "févr.", "mars" to "mars", "avr" to "avr.", "mai" to "mai", "juin" to "juin",
            "juil" to "juil.", "août" to "août", "sept" to "sept.", "oct" to "oct.", "nov" to "nov.", "déc" to "déc.")
        tabla.forEach { (clave, corto) -> if (m.contains(clave)) return corto }
        val limpio = mes.substringAfterLast(' ').trim()
        return if (limpio.length > 5) limpio.take(4) + "." else limpio
    }

    /**
     * El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp sin achicarse) y el mes debajo, en una de
     * 9.5 dp; así el día sale del mismo tamaño en las tres entradas.
     */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color, sombra: Color? = null) {
        val u = density
        val hd = 17.5f * u; val hm = 9.5f * u
        val y0 = r.center.y - (hd + hm) / 2
        val dia = Rect(r.left - 2f * u, y0, r.right + 2f * u, y0 + hd)
        val mes = Rect(r.left - 1f * u, y0 + hd - 1.2f * u, r.right + 1f * u, y0 + hd + hm)
        if (sombra != null) textoEn(medidor, v.dia, dia.translate(0.4f * u, 0.6f * u), P.LETRA, sombra, DIA, peso = FontWeight.Bold)
        textoEn(medidor, v.dia, dia, P.LETRA, tinta, DIA, peso = FontWeight.Bold)
        textoEn(medidor, mesCorto(v.mes), mes, P.TEXTO, tinta.copy(alpha = 0.9f), MES, peso = FontWeight.SemiBold, minimo = 5f)
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

    /** La luna de los Sueños: una media luna de plata recortada, con su canto oscuro para que se vea sobre lo claro, y su sombra. */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireNouveau) {
        val u = density
        val m = creciente(r)
        translate(c.x + 0.7f * u, c.y + 1f * u) { drawPath(m, a.sombra(0.55f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFE2E8F2))
            drawPath(m, Color(0xFF3A4258), style = Stroke(0.8f * u))
            drawCircle(Color.White.copy(alpha = 0.85f), r * 0.16f, Offset(-r * 0.62f, -r * 0.18f))
        }
    }

    /** Lo de todas: la sombra que dejan en el fondo por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireNouveau, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.5f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.5f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.55f), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la lámpara en el canto que la mira y el roce frío del escaparate (el café, la luna) en el de la izquierda. */
    private fun DrawScope.filos(cuerpo: Rect, a: AireNouveau, k: Float, f: Float, hacia: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.75f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (f > 0.03f) drawRect(a.fria(0.6f * f), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    // ---------------------------------------------------------------- el tomo de la revista ilustrada

    fun DrawScope.tomo(v: Volumen, medidor: TextMeasurer, a: AireNouveau, k: Float, f: Float, hacia: Float, tinte: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val top = h * 0.012f
        alrededor(a, hacia, top + h * 0.02f)
        val t = tinte(tinte, P.HABANO, a, k)
        val lomo = Rect(0.5f * u, top, w - 0.5f * u, h)
        redondo(lomo, t, hacia)
        // El grano de la percalina y el roce de los años en las puntas.
        for (j in 0 until 5) {
            val y = lomo.top + lomo.height * (0.1f + 0.85f * azar(s, 50 + j))
            drawLine(a.claro(0.12f), Offset(lomo.left + w * 0.15f * azar(s, 54 + j), y), Offset(lomo.left + w * (0.3f + 0.5f * azar(s, 58 + j)), y + 0.5f * u), 0.7f * u)
        }
        drawRect(mezcla(t.claro, P.PAPEL_OSC, 0.3f), Offset(lomo.left, h - 2.4f * u), Size(lomo.width, 2.4f * u))
        // La cofia de arriba y la cabezada de seda de dos colores.
        drawRoundRect(t.claro, Offset(lomo.left, lomo.top - 0.5f * u), Size(lomo.width, h * 0.026f), CornerRadius(w * 0.14f))
        var x = lomo.left + 1.5f * u; var q = 0
        while (x < lomo.right - 1.5f * u) {
            drawRect(a.luz(if (q % 2 == 0) Color(0xFF8E2C3A) else P.SEDA, k), Offset(x, lomo.top + h * 0.024f), Size(1.2f * u, 1.2f * u))
            x += 1.5f * u; q++
        }
        drawRect(a.sombra(0.35f), Offset(lomo.left, lomo.top + h * 0.024f + 1.2f * u), Size(lomo.width, 0.9f * u))
        // Los nervios: el tejuelo arriba o en medio, según la semilla.
        val arriba = azar(s, 3) < 0.6f
        val nervios = if (arriba) floatArrayOf(0.06f, 0.43f, 0.63f, 0.86f) else floatArrayOf(0.06f, 0.26f, 0.6f, 0.86f)
        val oro = a.luz(P.ORO, k)
        nervios.forEach { fy ->
            val y = h * fy
            drawRect(a.sombra(0.4f), Offset(lomo.left, y + 2f * u), Size(lomo.width, 1.2f * u))
            drawRect(t.hondo, Offset(lomo.left, y), Size(lomo.width, 2.4f * u))
            drawRect(t.claro, Offset(lomo.left, y - 0.5f * u), Size(lomo.width, 1.3f * u))
            drawRect(oro.copy(alpha = 0.85f), Offset(lomo.left + 1f * u, y + 2.9f * u), Size(lomo.width - 2f * u, 0.6f * u))
            drawRect(a.brillo(0.3f, k), Offset(if (hacia < 0f) lomo.left + lomo.width * 0.1f else lomo.right - lomo.width * 0.4f, y - 0.3f * u), Size(lomo.width * 0.3f, 0.7f * u))
        }
        // El tejuelo de tafilete con el día y el mes en oro.
        val e0 = if (arriba) nervios[0] else nervios[1]; val e1 = if (arriba) nervios[1] else nervios[2]
        val et = Rect(w * 0.09f, h * e0 + 4.4f * u, w * 0.91f, h * e1 - 1.6f * u)
        drawRect(a.sombra(0.4f), Offset(et.left + 0.8f * u, et.top + 1f * u), et.size)
        val taf = a.etiqueta(if (Math.floorMod(s, 3) == 0) Color(0xFF2E3A30) else P.TAFILETE, k)
        drawRect(taf, et.topLeft, et.size)
        drawRect(mezcla(taf, Color.White, 0.12f), et.topLeft, Size(et.width, 1f * u))
        drawRect(oro.copy(alpha = 0.7f), et.deflate(1.2f * u).topLeft, et.deflate(1.2f * u).size, style = Stroke(0.5f * u))
        diaYMes(v, medidor, et.deflate(1.4f * u), a.etiqueta(P.ORO, maxOf(k, 0.7f)), Color.Black.copy(alpha = 0.45f))
        // La flor del día, dorada, en el campo libre de arriba o de en medio; la línea de látigo o la luna, en el de abajo.
        val campoFlor = if (arriba) h * nervios[1] to h * nervios[2] else h * nervios[0] to h * nervios[1]
        val cf = Offset(w / 2, (campoFlor.first + campoFlor.second) / 2 + 1.2f * u)
        val rf = minOf((campoFlor.second - campoFlor.first) * 0.42f, w * 0.42f)
        with(FloresNouveau) {
            estampa(FloresNouveau.tipo(s), cf + Offset(0.4f * u, 0.6f * u), rf, Color.Black.copy(alpha = 0.3f), espejo = s % 2 == 0)
            estampa(FloresNouveau.tipo(s), cf, rf, oro, t.cara, espejo = s % 2 == 0)
        }
        // El campo de abajo: un recuadro de filete dorado con las esquinas redondeadas y, dentro, una roseta de puntos (en los
        // Sueños, la media luna).
        val y0 = h * nervios[2] + 4.6f * u; val y1 = h * nervios[3] - 2.2f * u
        if (y1 - y0 > 8f * u) {
            val marco = Rect(w * 0.2f, y0, w * 0.8f, y1)
            drawRoundRect(oro.copy(alpha = 0.9f), marco.topLeft, marco.size, CornerRadius(w * 0.12f), style = Stroke(maxOf(0.7f * u, w * 0.025f)))
            val c2 = marco.center
            if (v.noche) luna(c2, minOf(w * 0.2f, (y1 - y0) * 0.32f, 6f * u), a)
            else {
                val rr = minOf(w * 0.07f, (y1 - y0) * 0.12f)
                for (j in 0 until 5) {
                    val ang = (2 * Math.PI * j / 5 - Math.PI / 2).toFloat()
                    drawCircle(oro, rr, Offset(c2.x + rr * 1.5f * kotlin.math.cos(ang), c2.y + rr * 1.5f * kotlin.math.sin(ang)))
                }
                drawCircle(t.cara, rr * 0.7f, c2)
            }
        } else if (v.noche) luna(Offset(w / 2, (y0 + y1) / 2), minOf(w * 0.2f, 5f * u), a)
        // La cola: dos filetes dorados.
        drawRect(oro.copy(alpha = 0.8f), Offset(lomo.left + 1f * u, h * 0.92f), Size(lomo.width - 2f * u, 0.6f * u))
        drawRect(oro.copy(alpha = 0.8f), Offset(lomo.left + 1f * u, h * 0.95f), Size(lomo.width - 2f * u, 0.6f * u))
        // Una mancha de agua o de dedo, en algunos.
        if (azar(s, 7) < 0.4f) drawOval(a.sombra(0.12f), Offset(lomo.left + w * 0.15f, h * (0.66f + 0.1f * azar(s, 8))), Size(w * 0.6f, h * 0.06f))
        filos(lomo, a, k, f, hacia)
    }

    // ---------------------------------------------------------------- el rollo de carteles

    fun DrawScope.rollo(v: Volumen, medidor: TextMeasurer, a: AireNouveau, k: Float, f: Float, hacia: Float, tinte: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val tres = w >= 40f * u
        alrededor(a, hacia, h * 0.1f)
        val t = tinte(tinte, P.ESTRAZA, a, k)
        // Los rollos: dos o tres, el de delante más grueso y más alto; los de atrás, algo ladeados y de altos distintos.
        val rollos = if (tres) listOf(Triple(0.24f, 0.44f, 0.1f + 0.1f * azar(s, 20)), Triple(0.76f, 0.44f, 0.06f + 0.12f * azar(s, 21)), Triple(0.5f, 0.52f, 0.02f))
        else listOf(Triple(0.3f, 0.54f, 0.08f + 0.12f * azar(s, 20)), Triple(0.6f, 0.66f, 0.02f))
        rollos.forEachIndexed { i, (fx, fa, ft) ->
            val frente = i == rollos.size - 1
            val ladeo = if (frente) (azar(s, 22) - 0.5f) * 2f else (fx - 0.5f) * 8f
            rotate(ladeo, Offset(w * fx, h)) { unRollo(w * fx, h * ft, w * fa, a, k, t, hacia, s + i * 7, frente, v.noche) }
        }
        // El bramante: dos vueltas que abrazan los rollos, con su nudo delante.
        val bram = a.luz(Color(0xFFB89A68), k)
        listOf(0.3f, 0.74f).forEach { fy ->
            val y = h * fy
            val p = Path().apply { moveTo(w * 0.04f, y); quadraticTo(w / 2, y + 2.6f * u, w * 0.96f, y) }
            drawPath(p, a.sombra(0.4f), style = Stroke(1.5f * u))
            translate(0f, -0.8f * u) { drawPath(p, bram, style = Stroke(1.2f * u)) }
        }
        val nudo = Offset(w * (0.4f + 0.2f * azar(s, 30)), h * 0.3f + 0.8f * u)
        drawCircle(bram, 1.8f * u, nudo)
        drawLine(bram, nudo, nudo + Offset(-2.4f * u, 5.4f * u), 1f * u, StrokeCap.Round)
        // La etiqueta de cartulina colgada del bramante, con su ojal reforzado.
        val ew = minOf(w * 0.84f, 31f * u)
        val eh = 30f * u
        val giro = (azar(s, 31) - 0.5f) * 7f
        val ec = Offset(w / 2 + (azar(s, 32) - 0.5f) * (w - ew) * 0.6f, h * 0.36f + eh / 2)
        rotate(giro, Offset(ec.x, ec.y - eh / 2)) {
            val et = Rect(ec.x - ew / 2, ec.y - eh / 2, ec.x + ew / 2, ec.y + eh / 2)
            drawLine(bram, Offset(et.center.x, et.top + 3f * u), nudo, 0.9f * u)
            val forma = Path().apply {
                moveTo(et.left + 3f * u, et.top); lineTo(et.right - 3f * u, et.top); lineTo(et.right, et.top + 3f * u)
                lineTo(et.right, et.bottom); lineTo(et.left, et.bottom); lineTo(et.left, et.top + 3f * u); close()
            }
            translate(0.9f * u, 1.3f * u) { drawPath(forma, a.sombra(0.45f)) }
            val cart = a.etiqueta(P.CARTULINA, k)
            drawPath(forma, cart)
            drawRect(mezcla(cart, Color.White, 0.15f), Offset(et.left, et.top + 3f * u), Size(1f * u, et.height - 3f * u))
            drawCircle(a.etiqueta(Color(0xFF9A8058), k), 2.3f * u, Offset(et.center.x, et.top + 3.3f * u))
            drawCircle(a.luz(P.TINTA, k).copy(alpha = 0.7f), 1f * u, Offset(et.center.x, et.top + 3.3f * u))
            diaYMes(v, medidor, Rect(et.left + 1f * u, et.top + 5f * u, et.right - 1f * u, et.bottom - 0.5f * u), a.luz(P.TINTA, maxOf(k, 0.85f)))
        }
        if (v.noche) luna(Offset(w * 0.5f, h * 0.8f), minOf(w * 0.18f, 5.2f * u), a)
        filos(Rect(0f, h * 0.08f, w, h), a, k, f, hacia)
    }

    /**
     * Un rollo de [ancho] centrado en [cx], de [top] al piso: las hojas impresas que asoman arriba del envoltorio, con la espiral en
     * la boca y el canto de color de las tintas, y el alma de dentro un poco salida; el papel de seda teñido con su borde rasgado y
     * sus arrugas; en el de delante, la flor del día impresa en el papel.
     */
    private fun DrawScope.unRollo(cx: Float, top: Float, ancho: Float, a: AireNouveau, k: Float, t: Tinte, hacia: Float, s: Int, frente: Boolean, suenos: Boolean) {
        val u = density
        val h = size.height
        val e = ancho * 0.34f
        val sale = 9f * u
        // El alma, salida de la boca.
        val alma = Rect(cx - ancho * 0.24f, top, cx + ancho * 0.24f, top + e * 0.5f + 3f * u)
        drawRect(a.luz(P.PAPEL, k), Offset(alma.left, alma.top + e * 0.25f), Size(alma.width, alma.height - e * 0.25f))
        drawRect(a.sombra(0.22f), Offset(if (hacia < 0f) alma.right - alma.width * 0.35f else alma.left, alma.top + e * 0.25f), Size(alma.width * 0.35f, alma.height - e * 0.25f))
        // Las hojas, de la boca al envoltorio.
        val hojas = Rect(cx - ancho / 2, top + 3f * u + e * 0.5f, cx + ancho / 2, top + 3f * u + e * 0.5f + sale)
        drawRect(a.luz(P.PAPEL, k), hojas.topLeft, hojas.size)
        drawRect(a.dia(P.PAPEL, 0.2f, k), Offset(if (hacia < 0f) hojas.left else hojas.right - hojas.width * 0.3f, hojas.top), Size(hojas.width * 0.3f, hojas.height))
        drawRect(a.sombra(0.26f), Offset(if (hacia < 0f) hojas.right - hojas.width * 0.32f else hojas.left, hojas.top), Size(hojas.width * 0.32f, hojas.height))
        // El canto de color de las tintas: el borde de la hoja de fuera, impreso.
        drawRect(a.luz(if (s % 3 == 0) P.IRIS else Color(0xFFC9583E), k).copy(alpha = 0.75f), Offset(hojas.left, hojas.bottom - 2.6f * u), Size(hojas.width, 1.4f * u))
        // La boca: el canto de las vueltas, con la espiral.
        val boca = Rect(hojas.left, hojas.top - e * 0.5f, hojas.right, hojas.top + e * 0.5f)
        drawOval(a.luz(P.PAPEL_OSC, k), boca.topLeft, boca.size)
        val espiral = Path()
        val n = 26
        for (i in 0..n) {
            val tt = i / n.toFloat()
            val ang = tt * 2.6f * 2f * Math.PI.toFloat()
            val rr = 0.22f + 0.74f * tt
            val p = Offset(boca.center.x + boca.width * 0.5f * rr * kotlin.math.cos(ang), boca.center.y + boca.height * 0.5f * rr * kotlin.math.sin(ang))
            if (i == 0) espiral.moveTo(p.x, p.y) else espiral.lineTo(p.x, p.y)
        }
        drawPath(espiral, a.luz(Color(0xFF8A7A5A), k), style = Stroke(0.55f * u))
        drawArc(a.luz(Color(0xFFC9583E), k).copy(alpha = 0.6f), 30f, 120f, false, boca.deflate(0.8f * u).topLeft, boca.deflate(0.8f * u).size, style = Stroke(0.6f * u))
        drawOval(a.luz(P.PAPEL, k), Offset(alma.left, alma.top), Size(alma.width, e * 0.5f))
        drawOval(a.sombra(0.5f), Offset(cx - ancho * 0.07f, alma.top + e * 0.12f), Size(ancho * 0.14f, e * 0.26f))
        // El envoltorio de papel de seda, con su borde de arriba rasgado.
        val y0 = hojas.bottom
        val env = Path().apply {
            moveTo(hojas.left - 0.4f * u, h)
            lineTo(hojas.left - 0.4f * u, y0 + 1f * u)
            var x = hojas.left; var i = 0
            while (x < hojas.right) {
                val x2 = minOf(hojas.right, x + ancho * 0.2f)
                lineTo((x + x2) / 2, y0 + (if (i % 2 == 0) -2f else 1.6f) * u * (0.6f + azar(s, 40 + i)))
                lineTo(x2, y0 + 0.6f * u)
                x = x2; i++
            }
            lineTo(hojas.right + 0.4f * u, h); close()
        }
        clipPath(env) {
            redondo(Rect(hojas.left - 0.4f * u, y0 - 3f * u, hojas.right + 0.4f * u, h), t, hacia)
            // Las arrugas del papel de seda.
            for (j in 0 until 6) {
                val y = y0 + (h - y0) * (0.08f + 0.88f * azar(s, 50 + j))
                drawLine(t.hondo.copy(alpha = 0.4f), Offset(hojas.left + ancho * 0.1f, y), Offset(hojas.left + ancho * (0.4f + 0.5f * azar(s, 55 + j)), y + 2.2f * u), 0.6f * u)
                drawLine(t.claro.copy(alpha = 0.45f), Offset(hojas.left + ancho * 0.15f, y - 1f * u), Offset(hojas.left + ancho * 0.5f, y + 0.6f * u), 0.5f * u)
            }
            // La flor del día impresa en el papel, abajo, en el tono hondo del tinte.
            if (frente) with(FloresNouveau) {
                val rf = minOf(ancho * 0.42f, h * 0.1f) * (if (suenos) 0.75f else 1f)
                estampa(FloresNouveau.tipo(s), Offset(cx + ancho * 0.04f, if (suenos) h * 0.915f else h * 0.87f), rf, t.hondo.copy(alpha = 0.8f), espejo = s % 2 == 1)
            }
        }
        drawPath(env, a.sombra(0.32f), style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- el legajo de cartas

    fun DrawScope.legajo(v: Volumen, medidor: TextMeasurer, a: AireNouveau, k: Float, f: Float, hacia: Float, tinte: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val top = h * 0.06f
        alrededor(a, hacia, top + h * 0.05f)
        val t = tinte(tinte, P.ESTRAZA, a, k)
        val yc = h * (0.6f + 0.06f * azar(s, 4))
        val ladeo = (azar(s, 5) - 0.5f) * 4f
        rotate(ladeo, Offset(w / 2, h)) {
            // La silueta blanda: los costados se abomban entre la cinta y los cantos, y la cinta frunce el mazo.
            val bulto = Path().apply {
                moveTo(1.6f * u, top + 2f * u)
                lineTo(w - 1.6f * u, top + 2f * u)
                quadraticTo(w + 0.8f * u, (top + yc) / 2, w - 1.4f * u, yc)
                quadraticTo(w + 0.8f * u, (yc + h) / 2, w - 0.4f * u, h)
                lineTo(0.4f * u, h)
                quadraticTo(-0.8f * u, (yc + h) / 2, 1.4f * u, yc)
                quadraticTo(-0.8f * u, (top + yc) / 2, 1.6f * u, top + 2f * u)
                close()
            }
            clipPath(bulto) { redondo(Rect(0f, 0f, w, h), t, hacia) }
            // Los pliegos y los sobres de canto, de gruesos y altos desiguales; alguna carta crema; la solapa de algún sobre.
            var x = 0.6f * u; var i = 0
            val lineas = Path(); val claros = Path()
            while (x < w - 1f * u && i < 30) {
                val sobre = azar(s, 500 + i) < 0.6f
                val g = (if (sobre) 3.4f + 1.8f * azar(s, 510 + i) else 1.4f + 0.8f * azar(s, 520 + i)) * u
                val crema = azar(s, 100 + i) < 0.1f
                val sube = (azar(s, 200 + i) - 0.35f) * 8f * u
                val col = if (crema) mezcla(a.etiqueta(P.PAPEL, k * 0.9f), t.cara, 0.25f) else mezcla(t.cara, if (i % 2 == 0) t.claro else t.hondo, 0.04f + 0.12f * azar(s, 300 + i))
                val y0 = top + 3f * u - sube
                val g2 = minOf(g, w - 0.6f * u - x)
                if (g2 <= 0.2f * u) break
                drawRect(col, Offset(x, y0), Size(g2, h - y0))
                if (sobre && azar(s, 400 + i) < 0.7f) {
                    // La solapa del sobre, algo levantada, con su canto.
                    val solapa = poligonoNouveau(x, y0 + 0.2f * u, x + g2, y0 + 0.2f * u, x + g2 * 0.55f, y0 - 3.4f * u)
                    drawPath(solapa, mezcla(col, t.claro, 0.25f))
                    drawPath(solapa, a.sombra(0.3f), style = Stroke(0.4f * u))
                }
                if (!sobre && azar(s, 410 + i) < 0.4f) drawPath(poligonoNouveau(x, y0, x + g2, y0, x + g2, y0 + 2.4f * u), a.sombra(0.25f))
                lineas.moveTo(x + g2, y0); lineas.lineTo(x + g2, h)
                if (sobre) { claros.moveTo(x + 0.6f * u, y0 + 1f * u); claros.lineTo(x + 0.6f * u, h) }
                x += g; i++
            }
            clipPath(bulto) {
                drawPath(lineas, a.sombra(0.22f), style = Stroke(0.5f * u))
                drawPath(claros, a.claro(0.12f), style = Stroke(0.5f * u))
                // La luz del costado que mira a la lámpara o al escaparate, y la sombra del otro.
                drawRect(a.brillo(0.16f, k), Offset(if (hacia < 0f) 0f else w * 0.8f, 0f), Size(w * 0.2f, h))
                drawRect(a.sombra(0.2f), Offset(if (hacia < 0f) w * 0.78f else 0f, 0f), Size(w * 0.22f, h))
            }
            drawPath(bulto, a.sombra(0.4f), style = Stroke(0.6f * u))
            // La tarjeta del día, metida bajo la cinta.
            val ew = minOf(w * 0.84f, 30f * u)
            val eh = 31f * u
            val ex = w / 2 + (azar(s, 33) - 0.5f) * (w - ew) * 0.5f
            val et = Rect(ex - ew / 2, yc - eh + 3.4f * u, ex + ew / 2, yc + 3.4f * u)
            val giro = (azar(s, 34) - 0.5f) * 5f
            rotate(giro, Offset(et.center.x, et.bottom)) {
                translate(0.8f * u, 1f * u) { drawRect(a.sombra(0.42f), et.topLeft, et.size) }
                val tj = a.etiqueta(P.TARJETA, k)
                drawRect(tj, et.topLeft, et.size)
                drawRect(mezcla(tj, Color.White, 0.18f), et.topLeft, Size(et.width, 0.9f * u))
                drawRect(a.luz(P.TINTA, k).copy(alpha = 0.4f), et.deflate(1.4f * u).topLeft, et.deflate(1.4f * u).size, style = Stroke(0.45f * u))
                diaYMes(v, medidor, Rect(et.left + 1f * u, et.top + 0.5f * u, et.right - 1f * u, et.bottom - 4.4f * u), a.luz(P.TINTA, maxOf(k, 0.85f)))
            }
            // La flor seca bajo la cinta: el tallo sube por debajo y la cabeza asoma abajo, de lado.
            val izq = ex > w / 2
            with(FloresNouveau) {
                val fc = Offset(if (izq) w * 0.28f else w * 0.72f, yc + minOf(w * 0.32f, 12f * u) * 0.6f)
                deColor(FloresNouveau.tipo(s), fc, minOf(w * 0.32f, 12f * u), a.animo(Color(0xFFC48E8C), k), a.animo(Color(0xFF8E8C5A), k), a.animo(Color(0xFFD9B35C), k),
                    a.luz(Color(0xFF4A3A2A), k), if (izq) 168f else 192f, izq)
            }
            // La cinta de seda que da la vuelta, con su lazo y sus dos cabos.
            val seda = a.luz(Color(0xFFD4A84A), k)
            val sedaH = a.luz(Color(0xFF8E6A24), k)
            drawRect(a.sombra(0.42f), Offset(0f, yc + 2.8f * u), Size(w, 1.4f * u))
            drawRect(seda, Offset(-0.5f * u, yc - 2f * u), Size(w + 1f * u, 4.4f * u))
            drawRect(a.brillo(0.5f, k), Offset(0f, yc - 1.5f * u), Size(w, 0.8f * u))
            drawRect(sedaH, Offset(0f, yc + 1.6f * u), Size(w, 0.7f * u))
            val lc = Offset(ex + ew * (if (izq) -0.22f else 0.22f), yc)
            val lazo = Path().apply {
                moveTo(lc.x, lc.y); cubicTo(lc.x - 8f * u, lc.y - 8f * u, lc.x - 10f * u, lc.y + 1.4f * u, lc.x, lc.y)
                moveTo(lc.x, lc.y); cubicTo(lc.x + 8f * u, lc.y - 8f * u, lc.x + 10f * u, lc.y + 1.4f * u, lc.x, lc.y)
            }
            translate(0.8f * u, 1.1f * u) { drawPath(lazo, a.sombra(0.42f)) }
            drawPath(lazo, seda)
            drawPath(lazo, sedaH, style = Stroke(0.6f * u))
            val cabos = Path().apply {
                moveTo(lc.x - 0.6f * u, lc.y); quadraticTo(lc.x - 3f * u, lc.y + 6f * u, lc.x - 4.6f * u, lc.y + 12f * u)
                moveTo(lc.x + 0.6f * u, lc.y); quadraticTo(lc.x + 2.6f * u, lc.y + 7f * u, lc.x + 2.4f * u, lc.y + 13f * u)
            }
            translate(0.6f * u, 0.8f * u) { drawPath(cabos, a.sombra(0.35f), style = Stroke(2.6f * u)) }
            drawPath(cabos, seda, style = Stroke(2.6f * u, cap = StrokeCap.Butt))
            drawCircle(sedaH, 1.9f * u, lc)
            drawCircle(seda, 1.4f * u, lc)
            if (v.noche) luna(Offset(if (izq) w * 0.7f else w * 0.3f, h * 0.88f), minOf(w * 0.2f, 5.6f * u), a)
            filos(Rect(0f, top, w, h), a, k, f, hacia)
        }
    }
}
