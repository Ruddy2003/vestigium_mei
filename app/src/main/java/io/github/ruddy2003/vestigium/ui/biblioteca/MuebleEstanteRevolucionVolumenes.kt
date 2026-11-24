package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Temas
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Las entradas del estante de Revolución ([MuebleEstanteRevolucion]), tres cosas de la trastienda de la imprenta según la
 * semilla: el tomo en cartoné a la Bradel (alto, cubierto de papel teñido a la brocha, con las ranuras de las bisagras, las
 * cabezas rozadas donde asoma el cartón gris, la etiqueta impresa en el lomo y, si va lleno, su registro de cinta), el fajo de
 * folletos cosidos (más bajo, de cuadernillos desiguales y sueltos, con la cinta de seda cruzada y su lazo, alguna esquina
 * doblada y la etiqueta metida bajo la cinta) y los
 * rollos de carteles (dos o tres, más bajos, abiertos en abanico y atados por el medio con bramante, con la espiral de la boca,
 * el borde de la hoja de fuera despegado y la etiqueta colgada del nudo). El ánimo es el papel, en todo el cuerpo ([papelRevolucion]);
 * sin ánimo, papel gris de estraza. En los Sueños, una media luna de papel de plata con su disco apagado.
 */
internal object VolumenEstanteRevolucion {
    private val P = PaletaRevolucion

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.38f, 0.75f, 0.42f, 0.08f, -0.2f, -0.48f, -0.75f)

    /** Los meses cortos en español, para la etiqueta (el nombre republicano ya lo dice la etiqueta de la repisa). */
    private val MESES_CORTOS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

    /** El mes corto de una entrada: se ubica el mes republicano del tema; si no está, se acorta el nombre tal cual. */
    private fun mesCorto(mes: String): String {
        val m = mes.substringBefore(' ').trim()
        for (n in 1..12) if (Temas.mesRepublicano(n).equals(m, ignoreCase = true)) return MESES_CORTOS[n - 1]
        return if (m.length > 4) m.take(3).lowercase() + "." else m.lowercase()
    }

    /** La luz que le toca a una entrada: la del momento ([k]) y, en los Sueños oscuros, la luna de la ventana ([luna]). */
    private class Luz(val a: AireRevolucion, val k: Float, val luna: Float) {
        fun c(col: Color, f: Float = 0f): Color {
            val base = a.dia(col, f, k)
            return if (luna > 0f) a.sol(col, base, luna * 0.75f) else base
        }
        fun animo(t: Color): Color {
            val base = a.animo(t, k)
            return if (luna > 0f) a.sol(t, base, luna * 0.75f) else base
        }
        fun etiqueta(col: Color): Color {
            val base = a.etiqueta(col, k)
            return if (luna > 0f) a.sol(col, base, luna * 0.6f) else base
        }
    }

    /** Los colores de un papel con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(papel: Color?, l: Luz, varia: Float = 0f): Tinte {
        val m0 = papel ?: P.ESTRAZA
        val m = if (varia > 0f) mezcla(m0, luzMueble(1f), varia) else if (varia < 0f) mezcla(m0, sombraMueble(1f), -varia) else m0
        val claro = mezcla(m, luzMueble(1f), 0.3f)
        val hondo = mezcla(m, sombraMueble(1f), 0.5f)
        return if (papel != null) Tinte(l.animo(m), l.animo(claro), l.animo(hondo)) else Tinte(l.c(m), l.c(claro), l.c(hondo))
    }

    /** Un cuerpo redondo en [r] por franjas opacas (sin degradados): el filo, el claro, la cara, el hondo, hacia [hacia]. */
    private fun DrawScope.redondo(r: Rect, t: Tinte, hacia: Float, suave: Float = 1f) {
        for (i in 0 until 7) {
            val f0 = FRANJAS[i]; val f1 = FRANJAS[i + 1]
            val m = TONOS[i] * suave
            val c = if (m >= 0f) mezcla(t.cara, t.claro, m) else mezcla(t.cara, t.hondo, -m)
            val mas = if (i < 6) 1.2f else 0f
            val x0 = if (hacia < 0f) r.left + r.width * f0 else r.right - r.width * f1 - mas
            drawRect(c, Offset(x0, r.top), Size(r.width * (f1 - f0) + mas, r.height))
        }
    }

    /** El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp) y el mes debajo, en una de 9.5 dp. */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left, y0, r.right, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.Bold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left, y0 + hd - 1.2f * u, r.right, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.85f), MES, minimo = 5f)
    }

    /** La media luna de papel de plata, de radio [r], armada una vez por tamaño. */
    private var radioLuna = -1f
    private var caminoLuna = Path()

    private fun creciente(r: Float): Path {
        if (r != radioLuna) {
            val fuera = Path().apply { addOval(Rect(-r, -r, r, r)) }
            val dentro = Path().apply { addOval(Rect(-r * 0.8f + r * 0.5f, -r * 0.86f - r * 0.2f, r * 0.8f + r * 0.5f, r * 0.86f - r * 0.2f)) }
            caminoLuna = Path().apply { op(fuera, dentro, PathOperation.Difference) }
            radioLuna = r
        }
        return caminoLuna
    }

    /** La luna de los Sueños: el disco apagado (la luz cenicienta) y la media luna de papel de plata encima, con su canto y su sombra. */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireRevolucion) {
        val u = density
        val m = creciente(r)
        drawCircle(Color(0xFF3A4258).copy(alpha = 0.5f), r, c)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, a.sombra(0.5f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFDDE4F0))
            drawPath(m, Color(0xFF2E3448), style = Stroke(0.8f * u))
        }
        drawCircle(Color.White.copy(alpha = 0.85f), r * 0.14f, Offset(c.x - r * 0.62f, c.y - r * 0.16f))
    }

    /** Lo de todas: la sombra que dejan en el fondo por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireRevolucion, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.42f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.42f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche) y el roce frío de la luna. */
    private fun DrawScope.filos(cuerpo: Rect, a: AireRevolucion, k: Float, hacia: Float, luna: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.7f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (luna > 0f) drawRect(a.lunar(0.6f * luna), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    /** Una etiqueta impresa en [et] con el día y el mes: papel tostado, el doble filete de imprenta, su sombra y el borde gastado. */
    private fun DrawScope.etiqueta(v: Volumen, medidor: TextMeasurer, et: Rect, l: Luz, a: AireRevolucion, giro: Float = 0f) {
        val u = density
        rotate(giro, et.center) {
            drawRect(a.sombra(0.35f), Offset(et.left + 0.9f * u, et.top + 1.1f * u), et.size)
            drawRect(l.etiqueta(P.TOSTADO), et.topLeft, et.size)
            drawRect(l.etiqueta(P.PAPEL_OSC).copy(alpha = 0.45f), Offset(et.left, et.bottom - et.height * 0.12f), Size(et.width, et.height * 0.12f))
            drawRect(l.etiqueta(P.PAPEL_OSC).copy(alpha = 0.35f), et.topLeft, Size(et.width * 0.08f, et.height))
            val tinta = if (a.oscuro) Color(0xFF140C08) else a.luz(P.TINTA, maxOf(l.k, 0.75f))
            val f1 = et.deflate(1.2f * u); val f2 = et.deflate(2.3f * u)
            drawRect(tinta.copy(alpha = 0.7f), f1.topLeft, f1.size, style = Stroke(0.9f * u))
            drawRect(tinta.copy(alpha = 0.45f), f2.topLeft, f2.size, style = Stroke(0.45f * u))
            diaYMes(v, medidor, et.deflate(2.4f * u), tinta)
        }
    }

    // ---------------------------------------------------------------- el tomo en cartoné

    fun DrawScope.tomo(v: Volumen, medidor: TextMeasurer, a: AireRevolucion, k: Float, hacia: Float, papel: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val cab = 3.4f * u
        alrededor(a, hacia, cab)
        val t = tinte(papel, l, (azar(s, 4) - 0.5f) * 0.12f)
        // La cabeza: los cantos de las tapas de cartón y, entre ellos, el corte del libro con sus barbas finas.
        val canto = w * 0.11f
        drawRect(l.c(P.ESTRAZA_OSC), Offset(0f, 0f), Size(canto, cab + 2f * u))
        drawRect(l.c(P.ESTRAZA_OSC), Offset(w - canto, 0f), Size(canto, cab + 2f * u))
        val barbas = Path().apply {
            moveTo(canto, cab + 2f * u)
            lineTo(canto, 1.4f * u)
            var x = canto
            var j = 0
            while (x < w - canto) { x = minOf(x + 1.6f * u, w - canto); lineTo(x, 1f * u + 0.7f * u * azar(s, 100 + j)); j++ }
            lineTo(w - canto, cab + 2f * u); close()
        }
        drawPath(barbas, l.c(P.PAPEL, -0.04f))
        drawLine(a.sombra(0.25f), Offset(canto, cab + 0.6f * u), Offset(w - canto, cab + 0.6f * u), 0.7f * u)
        // El cuerpo: las tapas y el lomo cubiertos del papel teñido, algo redondeado.
        val cuerpo = Rect(0f, cab, w, h)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            redondo(cuerpo, t, hacia, 0.75f)
            // El papel teñido a la brocha: vetas finas a lo largo.
            val vetas = Path()
            for (j in 0 until 7) {
                val x = w * azar(s, 30 + j)
                vetas.moveTo(x, cab); vetas.lineTo(x + (azar(s, 40 + j) - 0.5f) * 2f * u, h)
            }
            drawPath(vetas, t.hondo.copy(alpha = 0.22f), style = Stroke(1f * u))
            // Las ranuras de las bisagras (lo propio del cartoné a la Bradel), con su luz.
            listOf(canto, w - canto).forEach { x ->
                drawRect(t.hondo, Offset(x - 0.8f * u, cab), Size(1.6f * u, h - cab))
                drawRect(t.claro.copy(alpha = 0.6f), Offset(x + 0.8f * u, cab), Size(0.8f * u, h - cab))
            }
            // Las cabezas rozadas: el papel gastado en las esquinas, donde asoma el cartón gris.
            val roce = Path()
            val e0 = (3f + 3f * azar(s, 56)) * u; val e1 = (3f + 4f * azar(s, 57)) * u
            roce.addPath(poligonoRevolucion(0f, cab, e0 * 1.6f, cab, 0f, cab + e0 * 1.3f))
            roce.addPath(poligonoRevolucion(w, cab, w - e0 * 1.2f, cab, w, cab + e0))
            roce.addPath(poligonoRevolucion(0f, h, e1 * 1.4f, h, 0f, h - e1 * 1.4f))
            roce.addPath(poligonoRevolucion(w, h, w - e1 * 1.8f, h, w, h - e1))
            roce.addRect(Rect(w * (0.3f + 0.2f * azar(s, 58)), h - 1.4f * u, w * (0.55f + 0.2f * azar(s, 58)), h))
            drawPath(roce, l.c(mezcla(P.ESTRAZA, P.ESTRAZA_OSC, 0.3f)))
            // Una mancha de agua y un roce claro de las manos.
            drawOval(t.hondo.copy(alpha = 0.22f), Offset(w * 0.15f, h * (0.62f + 0.2f * azar(s, 61))), Size(w * 0.6f, h * 0.07f))
            drawLine(a.claro(0.18f), Offset(w * 0.25f, h * 0.45f), Offset(w * 0.4f, h * 0.47f), 1f * u)
        }
        // Los filetes impresos del lomo (pares, en tinta) y, en algunos, la pieza de tomo: un papel de otro tinte con su filete.
        val tinta = l.c(P.TINTA).copy(alpha = 0.6f)
        val e0 = h * (0.07f + 0.06f * azar(s, 7))
        val f1 = e0 + 31f * u + 4f * u
        listOf(e0 - 3.2f * u, f1).forEach { y ->
            drawRect(tinta, Offset(canto + 1.5f * u, y), Size(w - 2 * canto - 3f * u, 0.9f * u))
            drawRect(tinta, Offset(canto + 1.5f * u, y + 2f * u), Size(w - 2 * canto - 3f * u, 0.5f * u))
        }
        val fondoF = h * (0.86f + 0.05f * azar(s, 8))
        drawRect(tinta, Offset(canto + 1.5f * u, fondoF), Size(w - 2 * canto - 3f * u, 0.9f * u))
        drawRect(tinta, Offset(canto + 1.5f * u, fondoF + 2f * u), Size(w - 2 * canto - 3f * u, 0.5f * u))
        if (azar(s, 9) > 0.45f && !v.noche) {
            val e2 = Rect(canto + 2f * u, h * 0.66f, w - canto - 2f * u, h * 0.66f + 9f * u)
            drawRect(a.sombra(0.3f), Offset(e2.left + 0.8f * u, e2.top + 1f * u), e2.size)
            drawRect(l.c(if (papel != null && papel.red > papel.blue) Color(0xFF2E4A3A) else Color(0xFF7A2A22)), e2.topLeft, e2.size)
            drawRect(l.c(P.LATON, -0.1f).copy(alpha = 0.7f), Offset(e2.left + 1.4f * u, e2.top + 1.6f * u), Size(e2.width - 2.8f * u, 0.7f * u))
            drawRect(l.c(P.LATON, -0.1f).copy(alpha = 0.7f), Offset(e2.left + 1.4f * u, e2.bottom - 2.3f * u), Size(e2.width - 2.8f * u, 0.7f * u))
        }
        // La etiqueta del lomo con el día.
        etiqueta(v, medidor, Rect(canto + 1f * u, e0, w - canto - 1f * u, e0 + 31f * u), l, a)
        // El registro de cinta que asoma por el pie, si va lleno.
        if (v.lleno > 0.3f) {
            val x = w * (0.35f + 0.3f * azar(s, 10))
            val col = if (azar(s, 11) > 0.5f) Color(0xFF3E6A44) else Color(0xFFB08A3A)
            drawLine(l.c(col), Offset(x, h - 2f * u), Offset(x + (azar(s, 12) - 0.5f) * 4f * u, h + 3f * u), 2f * u, StrokeCap.Round)
        }
        // La luna de los Sueños, entre la etiqueta y los filetes del pie.
        if (v.noche) luna(Offset(w / 2, h * 0.72f), minOf((w - 2 * canto) * 0.3f, 6f * u), a)
        filos(cuerpo, a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- el fajo de folletos

    /**
     * El fajo de folletos: de tres a cinco cuadernillos cosidos, desiguales de alto y de grueso, cada uno algo ladeado a su
     * aire (sueltos dentro del atado), con el lomo doblado y sus puntadas de hilo, alguna esquina doblada; atados con una cinta de
     * seda cruzada (a lo ancho y a lo alto) con su lazo, y la etiqueta metida bajo la cinta.
     */
    fun DrawScope.fajo(v: Volumen, medidor: TextMeasurer, a: AireRevolucion, k: Float, hacia: Float, papel: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val n = (3 + (v.lleno * 2.4f).toInt() + (if (azar(s, 2) > 0.6f) 1 else 0)).coerceIn(3, 5)
        val anchos = FloatArray(n) { 0.45f + 1f * azar(s, 70 + it) }
        val suma = anchos.sum()
        val altos = FloatArray(n) { h * (0.78f + 0.22f * azar(s, 80 + it)) }
        val lazoY = h * (0.56f + 0.06f * azar(s, 3))
        val oreja = Math.floorMod(s ushr 3, n)
        alrededor(a, hacia, h - altos.max())
        var x = 0f
        for (i in 0 until n) {
            val an = w * anchos[i] / suma
            val top = h - altos[i]
            val r = Rect(x, top, x + an + 0.6f * u, h)
            val giro = (azar(s, 60 + i) - 0.5f) * 7f
            val t = tinte(papel, l, (azar(s, 90 + i) - 0.5f) * 0.3f)
            rotate(giro, Offset(r.center.x, h)) {
                // El lomo doblado del cuadernillo, algo redondo, con el corte de las hojas arriba.
                redondo(r, t, hacia, 0.65f)
                drawRect(l.c(P.PAPEL, -0.04f), Offset(r.left + r.width * 0.12f, r.top - 1.8f * u), Size(r.width * 0.76f, 2f * u))
                drawRect(t.claro, Offset(r.left, r.top), Size(r.width, 1.1f * u))
                // Las puntadas: el hilo que sale del doblez y vuelve a entrar, en tres sitios, con su nudo.
                if (an > 5f * u) listOf(0.22f, 0.5f, 0.8f).forEach { f ->
                    val py = top + (h - top) * f
                    drawLine(l.c(P.PAPEL_LUZ, -0.1f), Offset(r.center.x, py - 2.2f * u), Offset(r.center.x, py + 2.2f * u), 0.9f * u)
                    drawCircle(t.hondo, 0.8f * u, Offset(r.center.x, py))
                }
                // Una esquina de arriba doblada: el revés del papel y su sombra.
                if (i == oreja) {
                    val e = minOf(an * 0.7f, 7f * u)
                    drawPath(poligonoRevolucion(r.right - e, top, r.right, top, r.right, top + e), a.sombra(0.35f))
                    drawPath(poligonoRevolucion(r.right - e, top, r.right - e * 0.1f, top + e * 0.9f, r.right - e, top + e * 0.75f), l.c(P.PAPEL, 0.05f))
                }
                drawRect(a.sombra(0.5f), Offset(r.right - 0.9f * u, top + 1f * u), Size(1.3f * u, h - top))
            }
            x += an
        }
        // La cinta de seda cruzada (negra, verde o de oro: nunca roja ni azul): la que sube y baja por delante, y la de lado a lado.
        val cintaCol = when (Math.floorMod(s, 3)) { 0 -> Color(0xFF1E1C22); 1 -> Color(0xFF3E6A44); else -> Color(0xFFB08A3A) }
        val cinta = l.c(cintaCol)
        val cg = 3.2f * u
        val lx = w * (if (azar(s, 13) > 0.5f) 0.26f else 0.74f)
        val alto0 = h - altos.max()
        drawRect(a.sombra(0.35f), Offset(lx - cg / 2 + 1f * u, alto0), Size(cg, h - alto0))
        drawRect(cinta, Offset(lx - cg / 2, alto0 - 0.5f * u), Size(cg, h - alto0 + 0.5f * u))
        drawRect(a.brillo(0.3f, k), Offset(lx - cg / 2, alto0), Size(0.7f * u, h - alto0))
        // La etiqueta, metida bajo la cinta de lado a lado y algo ladeada: sube desde ella.
        val et = Rect(2.2f * u, lazoY - 31f * u + 1.5f * u, w - 2.2f * u, lazoY + 1.5f * u)
        etiqueta(v, medidor, et, l, a, (azar(s, 12) - 0.5f) * 5f)
        drawRect(a.sombra(0.4f), Offset(0f, lazoY + cg - 0.4f * u), Size(w, 1.6f * u))
        drawRect(cinta, Offset(-0.5f * u, lazoY), Size(w + 1f * u, cg))
        drawRect(a.brillo(0.35f, k), Offset(0f, lazoY), Size(w, 0.8f * u))
        // El lazo en el cruce, con los cabos caídos.
        val ly = lazoY + cg / 2
        val lazo = Path().apply {
            moveTo(lx, ly); cubicTo(lx - 10f * u, ly + 2f * u, lx - 9f * u, ly + 10f * u, lx, ly)
            moveTo(lx, ly); cubicTo(lx + 10f * u, ly + 2f * u, lx + 9f * u, ly + 10f * u, lx, ly)
        }
        drawPath(Path().apply { addPath(lazo, Offset(0.8f * u, 1.2f * u)) }, a.sombra(0.4f), style = Stroke(2.4f * u))
        drawPath(lazo, cinta, style = Stroke(2.6f * u))
        drawPath(lazo, a.brillo(0.3f, k), style = Stroke(0.7f * u))
        drawLine(cinta, Offset(lx, ly), Offset(lx - 3.4f * u, ly + h * 0.2f), 2.2f * u)
        drawLine(cinta, Offset(lx, ly), Offset(lx + 3f * u, ly + h * 0.17f), 2.2f * u)
        drawCircle(cinta, 2.2f * u, Offset(lx, ly))
        if (v.noche) luna(Offset(if (lx < w / 2) w * 0.66f else w * 0.34f, h * 0.84f), minOf(w * 0.17f, 5f * u), a)
        filos(Rect(0f, alto0, w, h), a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- los rollos de carteles

    /** Un rollo de pie en [r] (el cuerpo y la boca), del tinte [t], con la boca de alto [elip]; [catalejo]: los pliegos de dentro asoman. */
    private fun DrawScope.unRollo(r: Rect, elip: Float, t: Tinte, l: Luz, a: AireRevolucion, hacia: Float, catalejo: Float, s: Int) {
        val u = density
        // El cuerpo: la hoja de fuera, del papel teñido, con sus arrugas y el borde de la hoja que baja casi a plomo.
        val cuerpo = Rect(r.left, r.top + elip / 2, r.right, r.bottom)
        redondo(cuerpo, t, hacia)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            for (j in 0 until 3) {
                val y = cuerpo.top + cuerpo.height * (0.15f + 0.75f * azar(s, 20 + j))
                drawLine(t.hondo.copy(alpha = 0.35f), Offset(r.left + r.width * 0.1f, y), Offset(r.left + r.width * (0.4f + 0.4f * azar(s, 24 + j)), y + 1.6f * u), 0.9f * u)
            }
            val bx = r.left + r.width * (if (hacia < 0f) 0.72f else 0.28f)
            drawLine(t.hondo, Offset(bx, cuerpo.top), Offset(bx + r.width * 0.05f, cuerpo.bottom), 1.4f * u)
            drawLine(t.claro, Offset(bx - 1.2f * u, cuerpo.top), Offset(bx + r.width * 0.05f - 1.2f * u, cuerpo.bottom), 0.8f * u)
        }
        // La boca: el canto de la hoja de fuera y, dentro, la espiral de los pliegos (que asoman un poco, como un catalejo).
        val boca = Rect(r.left, r.top, r.right, r.top + elip)
        drawOval(t.hondo, boca.topLeft, boca.size)
        val dentro = boca.deflate(r.width * 0.07f)
        if (catalejo > 0f) drawRect(l.c(P.PAPEL, -0.12f), Offset(dentro.left, dentro.center.y - catalejo), Size(dentro.width, catalejo))
        drawOval(l.c(P.PAPEL, 0.06f), Offset(dentro.left, dentro.top - catalejo), dentro.size)
        val c0 = Offset(dentro.center.x, dentro.center.y - catalejo)
        val espiral = Path()
        var ang = 0.0
        espiral.moveTo(c0.x, c0.y)
        while (ang < 6.28 * 3) {
            val f = (ang / (6.28 * 3)).toFloat()
            espiral.lineTo(c0.x + kotlin.math.cos(ang).toFloat() * dentro.width / 2 * f, c0.y + kotlin.math.sin(ang).toFloat() * dentro.height / 2 * f)
            ang += 0.3
        }
        drawPath(espiral, l.c(P.PAPEL_OSC, -0.15f), style = Stroke(0.9f * u))
        drawOval(a.sombra(0.45f), Offset(c0.x - dentro.width * 0.09f, c0.y - dentro.height * 0.09f), Size(dentro.width * 0.18f, dentro.height * 0.18f))
    }

    /**
     * Los rollos de carteles: dos o tres rollos de pie, más bajos que el tomo y de altos distintos, abiertos en abanico y atados
     * por el medio con dos vueltas de bramante; cada uno con la espiral de la boca, y el de delante con el borde de la hoja de
     * fuera despegado (se ve el impreso por dentro). La etiqueta cuelga del nudo.
     */
    fun DrawScope.rollo(v: Volumen, medidor: TextMeasurer, a: AireRevolucion, k: Float, hacia: Float, papel: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val n = if (w > 38f * u && azar(s, 14) > 0.45f) 3 else 2
        val atado = h * (0.4f + 0.06f * azar(s, 17))
        alrededor(a, hacia, h * 0.1f)
        // Los rollos, de atrás adelante: el de en medio más alto; cada uno con su diámetro, su alto y su giro sobre el atado.
        val pesos = if (n == 3) floatArrayOf(0.36f, 0.4f, 0.34f) else floatArrayOf(0.54f, 0.5f)
        val suma = pesos.sum() - (n - 1) * 0.06f
        val orden = if (n == 3) intArrayOf(1, 0, 2) else intArrayOf(0, 1)
        val xs = FloatArray(n); var x = 0f
        for (i in 0 until n) { xs[i] = x; x += w * (pesos[i] - 0.06f) / suma }
        orden.forEach { i ->
            val d = w * pesos[i] / suma
            val alto = h * (if (i == 1 && n == 3) 1f else 0.8f + 0.16f * azar(s, 30 + i))
            val r = Rect(xs[i], h - alto, xs[i] + d, h)
            val giro = (i - (n - 1) / 2f) * 5f + (azar(s, 40 + i) - 0.5f) * 3f
            val t = tinte(papel, l, (azar(s, 50 + i) - 0.5f) * 0.22f)
            rotate(giro, Offset(r.center.x, atado)) {
                unRollo(r, d * 0.42f, t, l, a, hacia, if (azar(s, 60 + i) > 0.5f) (1.5f + 2f * azar(s, 61 + i)) * u else 0f, s + i)
                drawRect(a.sombra(0.4f), Offset(r.right - 1f * u, r.top + d * 0.2f), Size(1.4f * u, r.height - d * 0.2f))
            }
        }
        // El borde de la hoja de fuera del rollo de delante, despegado arriba: se levanta y deja ver el impreso por dentro.
        val delante = orden.last()
        val dd = w * pesos[delante] / suma
        val bx = xs[delante] + dd * (if (hacia < 0f) 0.95f else 0.05f)
        val lado = if (hacia < 0f) 1f else -1f
        val yb = h * 0.22f
        val oreja = poligonoRevolucion(bx, yb, bx + lado * dd * 0.34f, yb - h * 0.05f, bx + lado * dd * 0.28f, yb + h * 0.12f, bx, yb + h * 0.16f)
        drawPath(Path().apply { addPath(oreja, Offset(lado * 1f * u, 1.3f * u)) }, a.sombra(0.35f))
        drawPath(oreja, l.c(P.PAPEL, 0.04f))
        clipPath(oreja) {
            val x0 = minOf(bx, bx + lado * dd * 0.3f)
            drawRect(l.c(P.TINTA).copy(alpha = 0.7f), Offset(x0, yb + h * 0.0f), Size(dd * 0.3f, h * 0.02f))
            for (j in 0 until 3) drawRect(l.c(P.TINTA).copy(alpha = 0.35f), Offset(x0, yb + h * (0.04f + 0.025f * j)), Size(dd * 0.3f, 0.8f * u))
        }
        drawPath(oreja, a.sombra(0.3f), style = Stroke(0.6f * u))
        // Las dos vueltas de bramante del atado, que muerden el papel, con el nudo y los cabos.
        listOf(atado - 1.6f * u, atado + 1.6f * u).forEach { y ->
            val cuerda = Path().apply { moveTo(-0.5f * u, y); quadraticTo(w / 2, y + 2.6f * u, w + 0.5f * u, y) }
            drawPath(Path().apply { addPath(cuerda, Offset(0f, 1.3f * u)) }, a.sombra(0.4f), style = Stroke(1.5f * u))
            drawPath(cuerda, l.c(P.BRAMANTE), style = Stroke(1.8f * u))
            drawPath(cuerda, l.c(P.BRAMANTE_OSC), style = Stroke(0.5f * u))
        }
        val nudo = Offset(w * 0.5f, atado + 2.4f * u)
        drawLine(l.c(P.BRAMANTE), nudo, Offset(nudo.x - 4f * u, nudo.y + h * 0.1f), 1.2f * u, StrokeCap.Round)
        // La etiqueta, colgada del nudo, algo ladeada.
        val et = Rect(2f * u, atado + h * 0.08f, w - 2f * u, atado + h * 0.08f + 31f * u)
        drawLine(l.c(P.BRAMANTE), nudo, Offset(et.center.x - 3f * u, et.top + 2f * u), 0.9f * u)
        drawLine(l.c(P.BRAMANTE), nudo, Offset(et.center.x + 3f * u, et.top + 2f * u), 0.9f * u)
        etiqueta(v, medidor, et, l, a, (azar(s, 19) - 0.5f) * 5f)
        drawCircle(a.sombra(0.5f), 1.1f * u, Offset(et.center.x, et.top + 2.6f * u))
        drawCircle(l.c(P.BRAMANTE), 2f * u, nudo)
        // La luna de los Sueños, en el rollo de delante, bajo la etiqueta (o encima si no cabe).
        if (v.noche) {
            val ly = if (et.bottom + 7f * u < h) (et.bottom + h) / 2 else atado - h * 0.12f
            luna(Offset(xs[delante] + dd / 2, ly), minOf(dd * 0.24f, 5f * u), a)
        }
        filos(Rect(0f, h * 0.1f, w, h), a, k, hacia, luna)
    }
}
