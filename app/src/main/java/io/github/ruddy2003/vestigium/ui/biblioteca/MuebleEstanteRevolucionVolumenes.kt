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
 * folletos cosidos (de alturas desparejas, atados con una cinta de seda y su lazo, la etiqueta metida bajo la cinta) y el
 * rollo de carteles (la hoja de fuera que se abre arriba y deja ver el impreso, la espiral de la boca, dos vueltas de bramante
 * y la etiqueta colgada; a veces, con un rollo flaco al lado). El ánimo es el papel, en todo el cuerpo ([papelRevolucion]);
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

    fun DrawScope.fajo(v: Volumen, medidor: TextMeasurer, a: AireRevolucion, k: Float, hacia: Float, papel: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        // Cuántos folletos: de tres a cinco, más con más palabras; cada uno con su lomo, su alto y su tinte.
        val n = (3 + (v.lleno * 2.4f).toInt() + (if (azar(s, 2) > 0.6f) 1 else 0)).coerceIn(3, 5)
        val anchos = FloatArray(n) { 0.7f + 0.6f * azar(s, 70 + it) }
        val suma = anchos.sum()
        val altos = FloatArray(n) { h * (0.84f + 0.16f * azar(s, 80 + it)) }
        val lazoY = h * (0.58f + 0.05f * azar(s, 3))
        alrededor(a, hacia, h - altos.max())
        // Atados por la cinta, los folletos se abren en abanico por arriba: cada uno gira un poco sobre la cinta.
        var x = 0f
        for (i in 0 until n) {
            val an = w * anchos[i] / suma
            val top = h - altos[i]
            val r = Rect(x, top, x + an + 0.6f * u, h)
            val giro = (i - (n - 1) / 2f) * 2.2f
            val t = tinte(papel, l, (azar(s, 90 + i) - 0.5f) * 0.26f)
            rotate(giro, Offset(r.center.x, lazoY)) {
                clipRect(-w, -h, w * 2, h) {
                    // El lomo del folleto: la cubierta de papel doblada, algo redonda, con la costura que asoma.
                    redondo(r, t, hacia, 0.6f)
                    drawRect(t.claro, Offset(r.left, r.top), Size(r.width, 1.2f * u))
                    drawRect(l.c(P.PAPEL, -0.05f), Offset(r.left + r.width * 0.15f, r.top - 1.6f * u), Size(r.width * 0.7f, 1.8f * u))
                    if (an > 7f * u) {
                        var py = top + 6f * u
                        while (py < h - 4f * u) { drawLine(t.hondo, Offset(r.center.x, py), Offset(r.center.x, py + 1.6f * u), 0.7f * u); py += 6f * u }
                    }
                    // La rendija con el vecino.
                    drawRect(a.sombra(0.5f), Offset(r.right - 0.9f * u, top + 1f * u), Size(1.3f * u, h - top))
                }
            }
            x += an
        }
        // La cinta de seda que los ata, con su lazo delante y los cabos caídos (negra, verde o de oro: nunca roja ni azul).
        val cintaCol = when (Math.floorMod(s, 3)) { 0 -> Color(0xFF1E1C22); 1 -> Color(0xFF3E6A44); else -> Color(0xFFB08A3A) }
        val cinta = l.c(cintaCol)
        val cg = 3.6f * u
        // La etiqueta, metida bajo la cinta y algo ladeada: sube desde la cinta.
        val et = Rect(2.4f * u, lazoY - 31f * u - h * 0.05f, w - 2.4f * u, lazoY - h * 0.05f + 3f * u)
        etiqueta(v, medidor, et, l, a, (azar(s, 12) - 0.5f) * 4f)
        drawRect(a.sombra(0.4f), Offset(0f, lazoY + cg - 0.4f * u), Size(w, 1.6f * u))
        drawRect(cinta, Offset(-0.5f * u, lazoY), Size(w + 1f * u, cg))
        drawRect(a.brillo(0.35f, k), Offset(0f, lazoY), Size(w, 0.8f * u))
        val lx = w * (0.35f + 0.3f * azar(s, 13)); val ly = lazoY + cg / 2
        val lazo = Path().apply {
            moveTo(lx, ly); cubicTo(lx - 10f * u, ly - 8f * u, lx - 12f * u, ly + 4f * u, lx, ly)
            moveTo(lx, ly); cubicTo(lx + 10f * u, ly - 7f * u, lx + 11f * u, ly + 5f * u, lx, ly)
        }
        drawPath(Path().apply { addPath(lazo, Offset(0.8f * u, 1.2f * u)) }, a.sombra(0.4f), style = Stroke(2.4f * u))
        drawPath(lazo, cinta, style = Stroke(2.6f * u))
        drawPath(lazo, a.brillo(0.3f, k), style = Stroke(0.7f * u))
        drawLine(cinta, Offset(lx, ly), Offset(lx - 4f * u, ly + h * 0.15f), 2.4f * u)
        drawLine(cinta, Offset(lx, ly), Offset(lx + 3.4f * u, ly + h * 0.13f), 2.4f * u)
        drawCircle(cinta, 2.2f * u, Offset(lx, ly))
        if (v.noche) luna(Offset(w / 2, h * 0.88f), minOf(w * 0.18f, 5.4f * u), a)
        filos(Rect(0f, h - altos.max(), w, h), a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- el rollo de carteles

    fun DrawScope.rollo(v: Volumen, medidor: TextMeasurer, a: AireRevolucion, k: Float, hacia: Float, papel: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val t = tinte(papel, l)
        // Con un rollo flaco detrás (en uno de cada tres): el gordo va delante, del lado de la luz.
        val flaco = azar(s, 14) > 0.66f
        val rw = if (flaco) w * 0.76f else w * 0.9f
        val rx = if (hacia < 0f) 0f else w - rw
        val elip = rw * 0.3f
        alrededor(a, hacia, elip * 0.5f)
        if (flaco) {
            val fw = w * 0.36f
            val fx = if (hacia < 0f) w - fw else 0f
            val ft = h * (0.02f + 0.04f * azar(s, 15))
            val tf = tinte(mezcla(P.PAPEL, P.TOSTADO, 0.4f), l)
            redondo(Rect(fx, ft + fw * 0.15f, fx + fw, h), tf, hacia)
            drawOval(l.c(P.PAPEL, 0.05f), Offset(fx, ft), Size(fw, fw * 0.3f))
            drawOval(l.c(P.PAPEL_OSC), Offset(fx + fw * 0.3f, ft + fw * 0.08f), Size(fw * 0.4f, fw * 0.14f), style = Stroke(0.7f * u))
            drawLine(l.c(P.BRAMANTE), Offset(fx, h * 0.3f), Offset(fx + fw, h * 0.31f), 1.4f * u)
        }
        // El cuerpo: la hoja de fuera, del papel teñido, que muerden las dos vueltas de bramante.
        val y1 = h * (0.25f + 0.04f * azar(s, 17)); val y2 = h * (0.84f + 0.04f * azar(s, 18))
        val top = elip * 0.5f
        val muerde = 1.2f * u
        val cuerpo = Path().apply {
            moveTo(rx, top)
            lineTo(rx + rw, top)
            lineTo(rx + rw, y1 - 2f * u); lineTo(rx + rw - muerde, y1); lineTo(rx + rw, y1 + 2f * u)
            lineTo(rx + rw, y2 - 2f * u); lineTo(rx + rw - muerde, y2); lineTo(rx + rw, y2 + 2f * u)
            lineTo(rx + rw, h); lineTo(rx, h)
            lineTo(rx, y2 + 2f * u); lineTo(rx + muerde, y2); lineTo(rx, y2 - 2f * u)
            lineTo(rx, y1 + 2f * u); lineTo(rx + muerde, y1); lineTo(rx, y1 - 2f * u)
            close()
        }
        clipPath(cuerpo) {
            redondo(Rect(rx, top, rx + rw, h), t, hacia)
            // Las arrugas del papel enrollado a mano.
            for (j in 0 until 4) {
                val y = h * (0.15f + 0.75f * azar(s, 20 + j))
                drawLine(t.hondo.copy(alpha = 0.35f), Offset(rx + rw * 0.1f, y), Offset(rx + rw * (0.4f + 0.4f * azar(s, 24 + j)), y + 2f * u), 0.9f * u)
            }
            // El borde de la hoja de fuera, que baja casi a plomo, con su canto y su sombra.
            val bx0 = rx + rw * (if (hacia < 0f) 0.7f else 0.3f)
            val dx = rw * 0.06f * (if (hacia < 0f) 1f else -1f)
            drawLine(t.hondo, Offset(bx0, top), Offset(bx0 + dx, h), 1.6f * u)
            drawLine(t.claro, Offset(bx0 - 1.3f * u, top), Offset(bx0 + dx - 1.3f * u, h), 0.9f * u)
        }
        // La boca: el canto de la hoja de fuera (del papel teñido) y, dentro, la espiral de los pliegos impresos, que asoman un
        // poco más, como un catalejo.
        val boca = Rect(rx, 0f, rx + rw, elip)
        drawOval(t.hondo, boca.topLeft, boca.size)
        val dentro = boca.deflate(rw * 0.06f)
        val sube = (1.5f + 2.5f * azar(s, 16)) * u
        drawRect(l.c(P.PAPEL, -0.1f), Offset(dentro.left, dentro.center.y - sube), Size(dentro.width, sube))
        drawOval(l.c(P.PAPEL, 0.06f), Offset(dentro.left, dentro.top - sube), dentro.size)
        val espiral = Path()
        val c0 = Offset(dentro.center.x, dentro.center.y - sube)
        var ang = 0.0
        espiral.moveTo(c0.x, c0.y)
        while (ang < 6.28 * 3) {
            val f = (ang / (6.28 * 3)).toFloat()
            espiral.lineTo(c0.x + kotlin.math.cos(ang).toFloat() * dentro.width / 2 * f, c0.y + kotlin.math.sin(ang).toFloat() * dentro.height / 2 * f)
            ang += 0.35
        }
        drawPath(espiral, l.c(P.PAPEL_OSC, -0.1f), style = Stroke(0.8f * u))
        drawOval(a.sombra(0.4f), Offset(c0.x - dentro.width * 0.08f, c0.y - dentro.height * 0.08f), Size(dentro.width * 0.16f, dentro.height * 0.16f))
        // La punta de la hoja de fuera, que se despega arriba y deja ver el impreso por dentro.
        val lado = if (hacia < 0f) 1f else -1f
        val bx = rx + rw * (if (hacia < 0f) 0.7f else 0.3f)
        val oreja = poligonoRevolucion(bx, top + 1f * u, bx + lado * rw * 0.32f, top - 0.5f * u, bx + lado * rw * 0.24f, top + h * 0.1f, bx + lado * 1.5f * u, top + h * 0.07f)
        drawPath(Path().apply { addPath(oreja, Offset(lado * 1f * u, 1.4f * u)) }, a.sombra(0.35f))
        drawPath(oreja, l.c(P.PAPEL, 0.04f))
        clipPath(oreja) {
            drawRect(l.c(P.TINTA).copy(alpha = 0.7f), Offset(minOf(bx, bx + lado * rw * 0.3f), top + h * 0.02f), Size(rw * 0.3f, h * 0.012f))
            for (j in 0 until 2) drawRect(l.c(P.TINTA).copy(alpha = 0.35f), Offset(minOf(bx, bx + lado * rw * 0.3f), top + h * (0.045f + 0.02f * j)), Size(rw * 0.3f, 0.8f * u))
        }
        drawPath(oreja, a.sombra(0.3f), style = Stroke(0.6f * u))
        // Las dos vueltas de bramante.
        listOf(y1, y2).forEach { y ->
            val cuerda = Path().apply { moveTo(rx, y); quadraticTo(rx + rw / 2, y + 2.4f * u, rx + rw, y) }
            drawPath(Path().apply { addPath(cuerda, Offset(0f, 1.3f * u)) }, a.sombra(0.4f), style = Stroke(1.5f * u))
            drawPath(cuerda, l.c(P.BRAMANTE), style = Stroke(1.8f * u))
            drawPath(cuerda, l.c(P.BRAMANTE_OSC), style = Stroke(0.5f * u))
        }
        // La etiqueta, colgada de la vuelta de arriba por un lazo de bramante, algo ladeada.
        val et = Rect(rx + 2f * u, y1 + h * 0.06f, rx + rw - 2f * u, y1 + h * 0.06f + 31f * u)
        val nudo = Offset(rx + rw * 0.5f, y1 + 1.4f * u)
        drawLine(l.c(P.BRAMANTE), nudo, Offset(et.center.x - 3f * u, et.top + 2f * u), 0.9f * u)
        drawLine(l.c(P.BRAMANTE), nudo, Offset(et.center.x + 3f * u, et.top + 2f * u), 0.9f * u)
        etiqueta(v, medidor, et, l, a, (azar(s, 19) - 0.5f) * 5f)
        drawCircle(a.sombra(0.5f), 1.1f * u, Offset(et.center.x, et.top + 2.6f * u))
        drawCircle(l.c(P.BRAMANTE), 1.9f * u, nudo)
        // La luna de los Sueños, entre la etiqueta y la vuelta de abajo.
        if (v.noche) luna(Offset(rx + rw / 2, (et.bottom + y2) / 2), minOf(rw * 0.22f, 5.6f * u), a)
        filos(Rect(rx, top, rx + rw, h), a, k, hacia, luna)
    }
}
