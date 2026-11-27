package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.RAYA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Los colores de la consola de registro (la libreta de Terminal), además de los del cuarto ([PaletaCiber]: la pared azul pizarra,
 * la chapa beis, el negro, el fósforo cian, el papel continuo): la bombilla del flexo y su alma, el rodillo y el acrílico ahumado
 * del carro, las teclas y la cinta de papel perforado.
 */
internal object PaletaLibretaCiber {
    /** La bombilla del flexo: su luz de tungsteno y su alma. */
    val CALOR = Color(0xFFFFB45E)
    val ALMA = Color(0xFFFFF1CC)
    /** El rodillo y el acrílico ahumado de la tapa del carro. */
    val RODILLO = Color(0xFF1C1E24)
    val AHUMADO = Color(0xFF3A3028)
    /** Las teclas: beis claro y gris. */
    val TECLA = Color(0xFFE4D8B4)
    val TECLA_OSC = Color(0xFF8E8A80)
    /** La cinta de papel perforado. */
    val CINTA = Color(0xFFE6D9A8)
}

/**
 * La luz del rincón de la consola, en píxeles: el flexo ([lampara], la bombilla), que alumbra hacia abajo con un charco cálido y
 * casi nada hacia arriba, y el fósforo cian del monitor del Ideario, que queda fuera del cuadro a la izquierda y echa su luz fría
 * hacia la derecha. Lo demás se hunde en la noche azul del cuarto ([AireCiber.hondo]).
 */
internal class LuzLibretaCiber(val h: Float, val lampara: Offset) {
    val aire = AireCiber(false)

    /** Lo que llega del flexo a [p]. */
    fun kCalor(p: Offset): Float {
        val dx = p.x - lampara.x; val dy = p.y - lampara.y
        val dyE = if (dy < 0f) dy * 2.4f else dy * 0.8f
        return 1.3f * exp(-sqrt(dx * dx + dyE * dyE) / (0.3f * h))
    }

    /** Lo que llega del monitor de fuera del cuadro, a la izquierda. */
    fun kFrio(p: Offset): Float = 0.62f * exp(-maxOf(0f, p.x) / (0.42f * h))

    fun t(p: Offset): TonoCiber {
        val kc = kCalor(p); val kf = kFrio(p)
        val k = kc + kf
        if (k < 1e-4f) return TonoCiber(aire, 0f, PaletaLibretaCiber.CALOR, 0.45f, 1f)
        val c = PaletaLibretaCiber.CALOR; val f = PaletaCiber.CIAN_LUZ
        val l = Color((c.red * kc + f.red * kf) / k, (c.green * kc + f.green * kf) / k, (c.blue * kc + f.blue * kf) / k)
        val lado = kc * (if (lampara.x > p.x) 1f else -1f) - kf
        return TonoCiber(aire, k.coerceAtMost(1.3f), l, (0.48f * kc + 0.3f * kf) / k, if (lado >= 0f) 1f else -1f)
    }

    fun t(x: Float, y: Float): TonoCiber = t(Offset(x, y))
}

/**
 * Dónde va cada cosa en un lienzo de [w] × [h] (el alto es fijo; el ancho cambia con el teléfono y el rincón se estira a lo ancho
 * sin deformar la consola): el rincón de la pared con el suelo; la consola (su ancho [mw], de [x0] a [x1]): la caja del carro,
 * la cubierta del teclado, el frente y el pie con sus patas; el papel que sube del carro; el flexo y el cesto.
 */
internal class EscenaLibretaCiber(val w: Float, val h: Float, val u: Float) {
    val rincon = h * 0.775f
    val cx = w * 0.47f
    val mw = minOf(h * 0.96f, w * 0.66f)
    val x0 = cx - mw / 2
    val x1 = cx + mw / 2
    /** La caja del carro (arriba), la cubierta del teclado, el frente y la base de la consola. */
    val capota = h * 0.355f
    val carro = h * 0.475f
    val teclado = h * 0.565f
    val base = h * 0.665f
    /** El estante bajo del pie y donde pisan las patas. */
    val estante = h * 0.83f
    val suelo = h * 0.915f
    /** La tapa ahumada del carro, de [hx0] a [hx1]. */
    val hx0 = x0 + mw * 0.12f
    val hx1 = x1 - mw * 0.12f
    /** El papel que sale del carro: de [px0] a [px1], desde el rizo de arriba ([papelArriba]). */
    val pw = minOf(h * 0.58f, mw * 0.62f)
    val px0 = cx - pw / 2
    val px1 = cx + pw / 2
    val papelArriba = h * 0.075f
    /** La guía de alambre del papel, justo encima de la tapa del carro. */
    val guia = capota - h * 0.018f
    val lampara = Offset(px1 + h * 0.085f, h * 0.2f)
    val cesto = Rect(x1 + h * 0.03f, h * 0.64f, x1 + h * 0.36f, h * 0.945f)
    val luz = LuzLibretaCiber(h, lampara)
}

/**
 * La libreta de momentos de Terminal (la usa `EstanteCiber`, en `BibliotecaContemporanea.kt`): la consola de registro, en el
 * mismo cuarto del Ideario y del estante, en la pared de al lado. Es una terminal de impresión de las de centro de cómputo (la
 * teleimpresora de la consola: teclado y carro de impresión en una caja de plástico beis, sobre su pie de chapa), donde cada
 * momento queda impreso en el papel continuo: el papel sube del carro con la cabecera (el nombre y la cuenta) y los renglones del
 * registro, se riza arriba y lo ya impreso cae doblado en el cesto de alambre del suelo, que se llena con las hojas. El papel en
 * blanco sube de su caja, en el estante bajo del pie. Contra la pared azul pizarra, con la esquina del tablero perforado del
 * Ideario, la cinta de papel perforado colgada y el mazo de cables con sus bridas que la cruza. El flexo del Ideario, aquí
 * encendido, alumbra el papel (la luz cálida, con su charco); por la izquierda llega el fósforo cian del monitor (la fría). La
 * taza, en la caja del carro. El tema es solo de noche: el dibujo es el mismo con [oscuro] o sin él. Al abrirse, el carro corre,
 * el papel avanza y el piloto de listo se pone verde. Lo fijo se pinta una vez por tamaño en una imagen guardada
 * (`MuebleLibretaCiberRincon.kt`); el índice, en `MuebleLibretaCiberIndice.kt`.
 */
internal object MuebleLibretaCiber {
    val ALTO: Dp = 252.dp

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer) {
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val g = EscenaLibretaCiber(w, h, density)
        clipRect(0f, 0f, w, h) {
            fija(g)
            impreso(e, medidor, g)
            carro(e, g)
            cesto(e, g)
        }
    }

    /** El rincón con la consola, sin lo que cambia, pintado una vez por tamaño y guardado (unos 2.7 MB en un teléfono de 1080 px). */
    private fun DrawScope.fija(g: EscenaLibretaCiber) {
        val ancho = g.w.roundToInt(); val alto = g.h.roundToInt()
        val k = "$ancho|$alto|$density|$fontScale"
        var img = imagen
        if (img == null || clave != k) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                with(RinconLibretaCiber) { rincon(g) }
                granoMueble(Rect(0f, 0f, g.w, g.h), true)
            }
            imagen = nueva; clave = k; img = nueva
        }
        drawImage(img)
    }

    // ---------------------------------------------------------------- lo impreso

    /**
     * Lo impreso en el papel, de arriba abajo: el nombre de la consola en negrita, la cuenta (en dos renglones, partida por el
     * punto), una raya de guiones y los renglones del registro (bloques, no letras), que al abrirse suben con el papel. La tinta es
     * la de la cinta de la impresora, más gastada a la derecha.
     */
    private fun DrawScope.impreso(e: EstadoLibreta, medidor: TextMeasurer, g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val ab = e.abierto.coerceIn(0f, 1f)
        val m = g.pw * 0.12f
        val x0 = g.px0 + m; val x1 = g.px1 - m
        val lh = h * 0.034f
        val sube = ab * lh * 1.6f
        val tinta = mezcla(TINTA, NOCHE, 0.15f)
        clipRect(g.px0, g.papelArriba + h * 0.008f, g.px1, g.guia - h * 0.006f) {
            var y = g.papelArriba + h * 0.03f - sube
            textoEn(medidor, e.nombre, Rect(x0, y, x1, y + lh * 1.25f), Letra.codigo, tinta, 12f, peso = FontWeight.Bold, minimo = 7f)
            y += lh * 1.3f
            val partes = e.cuenta.split(" · ")
            partes.take(2).forEach { p ->
                textoEn(medidor, p, Rect(x0, y, x1, y + lh), Letra.codigo, tinta.copy(alpha = 0.88f), 9f, minimo = 6f)
                y += lh
            }
            y += lh * 0.3f
            var xx = x0
            while (xx < x1) { drawLine(tinta.copy(alpha = 0.6f), Offset(xx, y), Offset(minOf(xx + 3f * u, x1), y), 0.9f * u); xx += 5.4f * u }
            y += lh * 0.7f
            // Los renglones del registro: guiones de largo al azar; cada tanto, uno sangrado.
            var k = 0
            while (y < g.guia + sube && k < 24) {
                var x = x0 + (if (k % 4 == 3) g.pw * 0.08f else 0f)
                val fin = x0 + (x1 - x0) * (0.35f + 0.6f * azar(e.semilla, 40 + k))
                var j = 0
                while (x < fin && j < 6) {
                    val l = g.pw * (0.05f + 0.12f * azar(e.semilla + k, j))
                    val a = 0.82f - 0.3f * ((x - x0) / (x1 - x0))
                    drawRect(tinta.copy(alpha = a), Offset(x, y - lh * 0.18f), Size(minOf(l, fin - x), lh * 0.34f))
                    x += l + g.pw * 0.035f; j++
                }
                y += lh; k++
            }
        }
    }

    // ---------------------------------------------------------------- el carro

    /**
     * El carro de impresión bajo su tapa: el rodillo negro que cruza el papel, el cabezal con su cartucho de cinta (corre a la
     * derecha al abrirse), la tapa de acrílico ahumado con su brillo y su filo, y el piloto de listo (ámbar en reposo, verde al
     * abrirse) en el frente.
     */
    private fun DrawScope.carro(e: EstadoLibreta, g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val P = PaletaLibretaCiber
        val ab = e.abierto.coerceIn(0f, 1f)
        val t = g.luz.t(g.cx, g.capota + h * 0.06f)
        // El rodillo, que asoma a los lados del papel y por debajo de él.
        val ry0 = g.capota + h * 0.07f; val ry1 = g.capota + h * 0.098f
        drawRect(t.c(P.RODILLO, 1.2f), Offset(g.hx0 + h * 0.01f, ry0), Size(g.hx1 - g.hx0 - h * 0.02f, ry1 - ry0))
        drawRect(t.b(0.55f), Offset(g.hx0 + h * 0.01f, ry0 + 0.8f * u), Size(g.hx1 - g.hx0 - h * 0.02f, 1f * u))
        // El cabezal con su cartucho de cinta: corre sobre su barra.
        val cxh = g.px0 + g.pw * (0.18f + 0.62f * ab) + g.pw * 0.06f * azar(e.semilla, 3)
        val tc = g.luz.t(cxh, g.capota + h * 0.05f)
        drawLine(tc.c(ACERO, 1.1f), Offset(g.hx0 + h * 0.015f, g.capota + h * 0.104f), Offset(g.hx1 - h * 0.015f, g.capota + h * 0.104f), 2f * u)
        val cab = Rect(cxh - h * 0.045f, g.capota + h * 0.045f, cxh + h * 0.045f, g.capota + h * 0.112f)
        drawRoundRect(tc.s(0.5f), cab.translate(1.2f * u, 1.6f * u).topLeft, cab.size, CornerRadius(2f * u))
        drawRoundRect(tc.c(NEGRO, 1.2f), cab.topLeft, cab.size, CornerRadius(2f * u))
        drawRect(tc.c(mezcla(NEGRO, ACERO, 0.4f), 1.2f), Offset(cab.left + cab.width * 0.12f, cab.top + cab.height * 0.15f), Size(cab.width * 0.76f, cab.height * 0.3f))
        drawRect(tc.b(0.6f), cab.topLeft, Size(cab.width, 0.9f * u))
        drawRect(tc.c(ACERO, 1.2f), Offset(cxh - h * 0.006f, cab.bottom - h * 0.012f), Size(h * 0.012f, h * 0.012f))
        // La tapa de acrílico ahumado: un trapecio que se inclina hacia quien mira, con su filo de luz arriba y un brillo.
        val tapa = poligonoCiber(g.hx0, g.capota, g.hx1, g.capota, g.hx1 + h * 0.012f, g.carro, g.hx0 - h * 0.012f, g.carro)
        drawPath(tapa, t.c(P.AHUMADO, 1.1f).copy(alpha = 0.62f))
        drawPath(tapa, t.b(0.5f), style = Stroke(1f * u))
        drawLine(Color.White.copy(alpha = 0.1f), Offset(g.hx0 + (g.hx1 - g.hx0) * 0.12f, g.carro - h * 0.004f), Offset(g.hx0 + (g.hx1 - g.hx0) * 0.2f, g.capota + h * 0.006f), 6f * u)
        drawLine(Color.White.copy(alpha = 0.06f), Offset(g.hx0 + (g.hx1 - g.hx0) * 0.25f, g.carro - h * 0.004f), Offset(g.hx0 + (g.hx1 - g.hx0) * 0.29f, g.capota + h * 0.006f), 2.4f * u)
        drawLine(g.luz.t(g.lampara.x, g.capota).b(0.9f), Offset(g.hx0, g.capota), Offset(g.hx1, g.capota), 1.2f * u)
        // El piloto de listo, en el frente.
        val p = Offset(g.x1 - g.mw * 0.1f, g.teclado + (g.base - g.teclado) * 0.38f)
        pilotoCiber(p, 1.8f * u, if (ab > 0.02f) VERDE else AMBAR, true, g.luz.t(p))
    }

    // ---------------------------------------------------------------- el cesto

    /**
     * Lo impreso, doblado en acordeón en el cesto de alambre: los pliegues de canto (más cuantos más días tiene la consola), cada
     * uno un poco corrido, con su banda verde de vez en cuando, y el borde de delante del cesto con sus alambres por encima.
     */
    private fun DrawScope.cesto(e: EstadoLibreta, g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val r = g.cesto
        val lleno = sqrt((e.hojas.coerceIn(1, 240) - 1) / 239f)
        val alto = (h * 0.035f + h * 0.2f * lleno).coerceAtMost(r.height * 0.85f)
        val grueso = 3.2f * u
        var y = r.bottom - h * 0.012f - grueso
        val tope = r.bottom - h * 0.012f - alto
        var k = 0
        while (y > tope && k < 80) {
            val dx = h * 0.008f * (if (k % 2 == 0) 1f else -1f) * (0.5f + azar(e.semilla, k))
            val iz = r.left + r.width * 0.1f + dx; val de = r.right - r.width * 0.1f + dx
            val tk = g.luz.t((iz + de) / 2, y)
            drawRoundRect(tk.c(PAPEL, 0.95f), Offset(iz, y), Size(de - iz, grueso), CornerRadius(grueso / 2))
            drawRect(tk.s(0.32f), Offset(iz, y + grueso - 0.7f * u), Size(de - iz, 0.7f * u))
            if (k % 5 == 2) drawRect(tk.c(RAYA, 0.95f), Offset(iz + (de - iz) * 0.1f, y + grueso * 0.35f), Size((de - iz) * 0.8f, grueso * 0.3f))
            y -= grueso; k++
        }
        if (k > 0) drawLine(g.luz.t(r.center.x, tope).b(0.55f), Offset(r.left + r.width * 0.1f, y + grueso), Offset(r.right - r.width * 0.1f, y + grueso), 0.9f * u)
        // El borde de delante del cesto y sus alambres, por delante del papel.
        val tc = g.luz.t(r.center)
        val alambre = tc.c(mezcla(NEGRO, ACERO, 0.35f), 1.1f)
        val arriba = r.top + h * 0.06f
        drawLine(alambre, Offset(r.left, arriba), Offset(r.right, arriba), 2f * u)
        drawLine(tc.b(0.5f), Offset(r.left, arriba - 1f * u), Offset(r.right, arriba - 1f * u), 0.7f * u)
        var x = r.left + r.width * 0.06f
        var j = 0
        while (x < r.right && j < 14) {
            val xb = r.left + r.width * 0.06f + (x - r.left - r.width * 0.06f) * 0.9f + r.width * 0.04f
            drawLine(alambre.copy(alpha = 0.85f), Offset(x, arriba), Offset(xb, r.bottom - h * 0.01f), 1.2f * u)
            x += r.width / 9f; j++
        }
        drawLine(alambre, Offset(r.left + r.width * 0.06f, r.bottom - h * 0.01f), Offset(r.right - r.width * 0.02f, r.bottom - h * 0.01f), 1.6f * u)
    }
}
