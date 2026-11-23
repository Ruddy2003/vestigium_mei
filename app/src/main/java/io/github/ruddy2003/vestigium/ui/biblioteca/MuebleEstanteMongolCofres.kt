package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import kotlin.math.roundToInt

// La pila de cofres de mimbre forrados de fieltro, una de las tres clases de entrada de la alacena (MuebleEstanteMongol): dos o
// tres cofres, del más grande abajo al más chico arriba, atados para la mudanza (como los que describe Rubruck: cajones de
// varas trenzadas forrados de fieltro untado de sebo).

/**
 * El tejido del mimbre, pintado una sola vez como pincel: solo la luz de cada vara y las juntas hondas, en blanco y negro con
 * transparencia, sobre el color que pone cada cofre con su luz. Así el mimbre de una pila entera son unas pocas operaciones.
 */
private object MimbreMongol {
    private var densidad = 0f
    private var pincel: ShaderBrush? = null
    fun pincel(u: Float): ShaderBrush {
        val p = pincel
        if (p != null && densidad == u) return p
        val an = (7.2f * u).roundToInt().coerceAtLeast(6)
        val al = (4.6f * u).roundToInt().coerceAtLeast(4)
        val fw = an / 2f; val fh = al / 2f
        val img = ImageBitmap(an, al)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(an.toFloat(), al.toFloat())) {
            for (fila in 0..1) {
                for (col in -1..2) {
                    val x = col * fw + (if (fila == 1) fw / 2 else 0f)
                    val y = fila * fh
                    drawRoundRect(Color.White.copy(alpha = 0.34f), Offset(x + 0.12f * fw, y + 0.1f * fh), Size(fw * 0.8f, fh * 0.7f), CornerRadius(fh * 0.35f))
                    drawRect(Color.Black.copy(alpha = 0.16f), Offset(x + 0.12f * fw, y + 0.58f * fh), Size(fw * 0.8f, fh * 0.22f))
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(x + 0.92f * fw, y), Size(fw * 0.12f, fh))
                }
                drawRect(Color.Black.copy(alpha = 0.34f), Offset(0f, fila * fh + fh * 0.84f), Size(an.toFloat(), fh * 0.16f))
            }
        }
        return ShaderBrush(ImageShader(img, TileMode.Repeated, TileMode.Repeated)).also { pincel = it; densidad = u }
    }
}

/** El mimbre en un rectángulo: el color de la vara con su luz y, encima, el tejido. */
private fun DrawScope.mimbreMongol(r: Rect, l: LuzMongol) {
    if (r.width <= 0f || r.height <= 0f) return
    drawRect(l.c(P.MIMBRE), r.topLeft, r.size)
    drawRect(MimbreMongol.pincel(density), r.topLeft, r.size, alpha = if (l.a.oscuro) 0.45f + 0.45f * l.k else 0.95f)
}

/**
 * La pila de cofres: dos o tres (tres si la pila es alta para su ancho), cada uno más ancho que alto, el de abajo el más grande
 * y los de arriba corridos a un lado o al otro. Cada cofre deja ver su mimbre en los cantos y abajo, bajo el forro de fieltro
 * del ánimo con su costura gruesa a punto de festón; el de abajo lleva una tira de dientes de lobo, el de en medio dos
 * pespuntes y el de arriba una franja lisa. Solo el de arriba tiene la tapa abombada, con el brillo del sebo; los de abajo,
 * su tapa plana con el ribete. La correa de cuero que ata la pila baja derecha por todos, con su hebilla de hueso (una placa
 * maciza, sin agujeros) en el frente de uno de ellos, y de ahí cuelga la tablilla.
 */
internal fun DrawScope.cofresMongol(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzMongol, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val base = l.animo(tono)
    val n = if (h > w * 1.5f) 3 else 2
    val partes = if (n == 3) floatArrayOf(0.38f, 0.33f, 0.29f) else floatArrayOf(0.55f, 0.45f)
    // El de abajo se sale un poco del sitio (cabe en la separación entre entradas): un cofre es más ancho que alto.
    val anchos = if (n == 3) floatArrayOf(1.12f, 0.98f, 0.84f) else floatArrayOf(1.12f, 0.92f)
    val cofres = ArrayList<Rect>(n)
    var pie = h
    var desvio = 0f
    for (i in 0 until n) {
        val an = w * anchos[i]
        if (i > 0) desvio += w * (0.02f + 0.06f * azar(v.semilla, 59 + i)) * (if (azar(v.semilla, 62 + i) < 0.5f) 1f else -1f)
        val x0 = ((w - an) / 2 + desvio).coerceIn(minOf(-w * 0.03f, (w - an) / 2), maxOf(w - an + w * 0.03f, (w - an) / 2))
        val tope = pie - h * partes[i]
        cofres.add(Rect(x0, tope, x0 + an, pie + if (i > 0) 1f * u else 0f))
        pie = tope
    }
    cofres.forEach { r -> sombraDetrasRectaMongol(r.left, r.right, r.top, r.bottom, l, hacia) }
    contactoEntradaMongol(l, hacia, cofres[0].left, cofres[0].right)
    val bandas = bandasMongol(base, l, hacia)
    val fieltro = Brush.horizontalGradient(listOf(bandas[1], bandas[2], bandas[3], bandas[4]), 0f, w)
    val hilo = hiloMongol(base, l)
    val aplicacion = l.c(aplicacionMongol(fieltroSeguroMongol(tono)))
    cofres.forEachIndexed { i, r ->
        if (i > 0) {
            // La sombra del cofre de arriba en la tapa del de abajo, del lado contrario a la luz.
            drawRect(l.s(0.55f), Offset(r.left - hacia * 2.6f * u, r.bottom - 1.6f * u), Size(r.width + 2f * u, 2.8f * u))
        }
        cofre(v, r, i, i == n - 1, base, fieltro, hilo, aplicacion, l, hacia)
    }
    // La sombra que echa la vecina del lado de la luz, honda abajo: en cada cofre, la franja de su canto (sin recorte).
    cofres.forEach { r ->
        val y0 = maxOf(r.top + 2f * u, h * 0.3f)
        if (y0 < r.bottom) {
            val ancho = minOf(w * 0.2f, r.width * 0.25f)
            drawRect(l.s(if (l.a.oscuro) 0.14f else 0.2f), Offset(if (hacia < 0f) r.left else r.right - ancho, y0), Size(ancho, r.bottom - y0))
        }
    }
    // La correa que ata la pila, derecha de la tapa de arriba al pie, con su costura.
    val izq = cofres.maxOf { it.left } + 3f * u
    val der = cofres.minOf { it.right } - 3f * u
    val cx = if (der > izq) izq + (der - izq) * (0.25f + 0.5f * azar(v.semilla, 61)) else w / 2
    val arriba = cofres.last().top
    val pe = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.4f * u), 0f)
    drawRect(l.s(0.42f), Offset(cx - 1.8f * u - hacia * 1.2f * u, arriba + 1f * u), Size(4f * u, h - arriba - 1f * u))
    drawRect(l.c(P.CUERO), Offset(cx - 2.1f * u, arriba + 0.5f * u), Size(4.2f * u, h - arriba - 1.5f * u))
    drawRect(if (l.a.oscuro) l.filo(0.6f) else l.b(0.3f), Offset(if (hacia < 0f) cx - 2.1f * u else cx + 1.2f * u, arriba), Size(0.9f * u, h - arriba))
    drawLine(l.c(P.CRUDO).copy(alpha = 0.6f), Offset(cx, arriba + 1f * u), Offset(cx, h - 1f * u), 0.6f * u, pathEffect = pe)
    // La hebilla de hueso: una placa maciza, más ancha que alta, con su canto tallado y la lengüeta de la correa que sale debajo.
    // En el frente de uno de los cofres, el que deje caber la tablilla entera sobre la tabla.
    val largoTablilla = (if (v.noche) 35.5f else 29.5f) * u + 6f * u
    var kb = if (n == 3 && azar(v.semilla, 64) < 0.5f) 1 else 0
    while (kb < n - 1 && cofres[kb].top + cofres[kb].height * 0.42f > h - largoTablilla) kb++
    val rc = cofres[kb]
    val yb = minOf(rc.top + rc.height * (0.42f + 0.12f * azar(v.semilla, 65)), h - largoTablilla)
    val hb = Rect(cx - 4.4f * u, yb - 2.4f * u, cx + 4.4f * u, yb + 2.4f * u)
    drawRect(l.c(mezcla(P.CUERO, P.HOLLIN, 0.2f)), Offset(cx - 1.6f * u, hb.bottom - 0.5f * u), Size(3.2f * u, 3.6f * u))
    drawRoundRect(l.s(0.5f), hb.topLeft + Offset(-hacia * 1f * u, 1.1f * u), hb.size, CornerRadius(1.4f * u))
    drawRoundRect(l.c(P.HUESO), hb.topLeft, hb.size, CornerRadius(1.4f * u))
    drawRoundRect(l.c(mezcla(P.HUESO, P.CUERO, 0.35f)), Offset(hb.left + 1f * u, hb.top + 1f * u), Size(hb.width - 2f * u, hb.height - 2f * u), CornerRadius(0.8f * u), style = Stroke(0.6f * u))
    drawRect(if (l.a.oscuro) l.filo(0.7f) else l.b(0.65f), Offset(hb.left + 1f * u, hb.top + 0.4f * u), Size(hb.width * 0.5f, 0.8f * u))
    val rt = rectTablillaMongol(hb.bottom + 3.4f * u, v, cx)
    tablillaMongol(v, medidor, l, rt, Offset(cx, hb.bottom + 2.6f * u), hacia)
}

/**
 * Un cofre de la pila ([k] = 0 es el de abajo): el cajón de mimbre, que se ve en los cantos y abajo; el forro de fieltro del
 * frente, gastado abajo, con su costura gruesa a los lados y su adorno de puntadas; y la tapa: abombada en el de [arriba], plana
 * con su ribete en los demás.
 */
private fun DrawScope.cofre(v: Volumen, r: Rect, k: Int, arriba: Boolean, base: Color, fieltro: Brush, hilo: Color, aplicacion: Color, l: LuzMongol, hacia: Float) {
    val u = density
    val tapaAlto = if (arriba) minOf(r.height * 0.32f, 9f * u) else minOf(r.height * 0.2f, 5f * u)
    val cuerpo = Rect(r.left, r.top + tapaAlto * (if (arriba) 0.72f else 0.6f), r.right, r.bottom)
    // El forro de fieltro del frente: deja ver el mimbre en los cantos y abajo, donde se gastó.
    val margen = minOf(2.8f * u, r.width * 0.08f)
    val fondoForro = cuerpo.bottom - minOf(cuerpo.height * 0.2f, 4.6f * u)
    mimbreMongol(Rect(cuerpo.left, cuerpo.top, cuerpo.left + margen + 0.5f, cuerpo.bottom), l)
    mimbreMongol(Rect(cuerpo.right - margen - 0.5f, cuerpo.top, cuerpo.right, cuerpo.bottom), l)
    mimbreMongol(Rect(cuerpo.left, fondoForro - 2.5f * u, cuerpo.right, cuerpo.bottom), l)
    drawRect(l.s(0.35f), Offset(cuerpo.left, cuerpo.bottom - 1.2f * u), Size(cuerpo.width, 1.2f * u))
    val forro = Path().apply {
        moveTo(cuerpo.left + margen, cuerpo.top)
        lineTo(cuerpo.right - margen, cuerpo.top)
        lineTo(cuerpo.right - margen, fondoForro)
        var x = cuerpo.right - margen
        var j = 0
        while (x > cuerpo.left + margen) { x -= 3f * u; lineTo(maxOf(x, cuerpo.left + margen), fondoForro + (azar(v.semilla + k, 66 + j) - 0.4f) * 2.2f * u); j++ }
        close()
    }
    drawPath(corridoMongol(forro, 0f, 1f * u), l.s(0.3f))
    drawPath(forro, fieltro)
    pelusaMongol(forro, l)
    val alto = fondoForro - cuerpo.top
    when (k) {
        0 -> {
            // La tira de dientes de lobo, cosida.
            val t0 = cuerpo.top + alto * 0.42f
            val t1 = t0 + minOf(6f * u, alto * 0.34f)
            drawRect(l.s(0.3f), Offset(cuerpo.left + margen, t1), Size(cuerpo.width - 2 * margen, 1.1f * u))
            drawRect(aplicacion, Offset(cuerpo.left + margen, t0), Size(cuerpo.width - 2 * margen, t1 - t0))
            drawPath(dientesMongol(cuerpo.left + margen + 0.5f * u, cuerpo.right - margen, t1 - 0.6f * u, t1 - t0 - 1.4f * u, 3.2f * u), l.c(mezcla(base, P.BERMELLON, 0.45f)))
        }
        1 -> {
            // Dos pespuntes que corren a lo ancho.
            val pe = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u), 0f)
            listOf(0.4f, 0.62f).forEach { f ->
                val y = cuerpo.top + alto * f
                drawLine(l.s(0.25f), Offset(cuerpo.left + margen, y + 0.8f * u), Offset(cuerpo.right - margen, y + 0.8f * u), 0.8f * u)
                drawLine(hilo, Offset(cuerpo.left + margen + 1f * u, y), Offset(cuerpo.right - margen - 1f * u, y), 0.8f * u, pathEffect = pe)
            }
        }
        else -> {
            // Una franja lisa de otro fieltro.
            val t0 = cuerpo.top + alto * 0.5f
            drawRect(l.s(0.25f), Offset(cuerpo.left + margen, t0 + 2.6f * u), Size(cuerpo.width - 2 * margen, 0.9f * u))
            drawRect(aplicacion, Offset(cuerpo.left + margen, t0), Size(cuerpo.width - 2 * margen, 2.6f * u))
        }
    }
    // La sombra de la tapa en el forro.
    drawRect(l.s(0.4f), Offset(cuerpo.left + margen, cuerpo.top), Size(cuerpo.width - 2 * margen, 2.4f * u))
    // La costura gruesa a los lados del forro: el canto alzado y las puntadas de festón que lo cruzan.
    val festones = Path()
    listOf(cuerpo.left + margen, cuerpo.right - margen).forEachIndexed { i, x ->
        drawLine(l.s(0.5f), Offset(x + (if (i == 0) -0.6f else 0.6f) * u, cuerpo.top), Offset(x + (if (i == 0) -0.6f else 0.6f) * u, fondoForro), 1.1f * u)
        var y = cuerpo.top + 1.5f * u
        while (y < fondoForro - 1f * u) { festones.moveTo(x - 1.4f * u, y); festones.lineTo(x + 1.4f * u, y + 1.3f * u); y += 2.4f * u }
    }
    drawPath(festones, hilo, style = Stroke(0.8f * u))
    if (l.a.oscuro) drawRect(l.filo(0.7f), Offset(if (hacia > 0f) cuerpo.right - 1.3f * u else cuerpo.left, cuerpo.top), Size(1.3f * u, cuerpo.height))
    else drawRect(l.b(0.4f), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    // La tapa, forrada de fieltro, un poco más ancha que el cajón.
    val t = Rect(r.left - 1.2f * u, r.top, r.right + 1.2f * u, r.top + tapaAlto)
    val tapaCol = mezcla(base, if (l.a.oscuro) l.a.hondo else SOMBRA_MUEBLE, 0.12f)
    if (arriba) {
        // Abombada, con los hombros redondos y el lomo casi plano; el brillo del sebo y su ribete.
        val tapa = Path().apply {
            moveTo(t.left, t.bottom)
            cubicTo(t.left, t.top + tapaAlto * 0.3f, t.left + t.width * 0.1f, t.top, t.left + t.width * 0.24f, t.top)
            lineTo(t.right - t.width * 0.24f, t.top)
            cubicTo(t.right - t.width * 0.1f, t.top, t.right, t.top + tapaAlto * 0.3f, t.right, t.bottom)
            close()
        }
        drawPath(corridoMongol(tapa, -hacia * 1.4f * u, 1.6f * u), l.s(0.42f))
        drawPath(tapa, Brush.verticalGradient(listOf(l.c(mezcla(tapaCol, LUZ_MUEBLE, if (l.a.oscuro) 0.04f else 0.18f)), tapaCol, mezcla(tapaCol, if (l.a.oscuro) l.a.hondo else SOMBRA_MUEBLE, 0.3f)), t.top, t.bottom))
        if (!l.a.oscuro) drawPath(tapa, FieltroMongol.pincel(u), alpha = 0.6f)
        val brillo = Path().apply { moveTo(t.left + t.width * 0.12f, t.top + tapaAlto * 0.5f); quadraticTo(t.left + t.width * 0.2f, t.top + tapaAlto * 0.14f, t.left + t.width * 0.45f, t.top + tapaAlto * 0.16f) }
        drawPath(brillo, if (l.a.oscuro) l.filo(0.6f) else l.b(0.7f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        if (l.a.oscuro && l.f > 0.05f) drawPath(brillo, l.frio(0.6f), style = Stroke(1f * u, cap = StrokeCap.Round))
    } else {
        // Plana: el canto de fieltro doblado sobre el mimbre, con su luz arriba.
        drawRect(l.c(tapaCol), t.topLeft, t.size)
        drawRect(if (l.a.oscuro) l.filo(0.4f) else l.b(0.4f), t.topLeft, Size(t.width, 1f * u))
        mimbreMongol(Rect(t.left, t.top + 1f * u, t.left + 2f * u, t.bottom - 1f * u), l)
        mimbreMongol(Rect(t.right - 2f * u, t.top + 1f * u, t.right, t.bottom - 1f * u), l)
    }
    drawRect(l.c(mezcla(tapaCol, P.HOLLIN, 0.35f)), Offset(t.left, t.bottom - 1.4f * u), Size(t.width, 1.6f * u))
    val ribete = Path()
    var x = t.left + 1.2f * u
    while (x < t.right - 1f * u) { ribete.moveTo(x, t.bottom - 2.4f * u); ribete.lineTo(x + 1f * u, t.bottom - 0.6f * u); x += 2.2f * u }
    drawPath(ribete, hilo.copy(alpha = 0.75f), style = Stroke(0.7f * u))
}
