package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar

/** Cómo es el techo de una casa: cada época y cada lugar tiene el suyo. */
enum class Techo {
    /** A dos aguas, de teja de barro, de poca pendiente: el Mediterráneo, de la Antigüedad a hoy. */
    TEJA,
    /** De paja o de caña, alto y redondeado: la aldea, de la prehistoria a la Edad Media. */
    PAJA,
    /** Azotea plana, con sus vigas asomando: el adobe de Egipto, del desierto y de Mesoamérica. */
    PLANO,
    /** De pizarra o de plomo, muy empinado: el norte de Europa. */
    PIZARRA,
    /** De tablas o tejamanil: la montaña, la estepa y los colonos. */
    MADERA,
}

/**
 * De qué está hecha una casa: el color del [muro], la forma y el color del [techo], la madera de la [puerta] y cuántas
 * [ventanas] tiene. [zocalo]: una franja de otro color al pie del muro (piedra, o el rojo de las casas griegas).
 */
class Casa(val muro: Color, val techo: Techo, val colorTecho: Color, val puerta: Color = Color(0xFF4A3524), val ventanas: Int = 1, val zocalo: Color? = null)

/** La luz de una ventana o de una puerta con fuego dentro. */
val LUZ_DE_CASA = Color(0xFFFFC56A)

private fun alturaTecho(c: Casa, ancho: Float) = when (c.techo) {
    Techo.TEJA -> ancho * 0.22f
    Techo.PAJA -> ancho * 0.50f
    Techo.PLANO -> ancho * 0.05f
    Techo.PIZARRA -> ancho * 0.62f
    Techo.MADERA -> ancho * 0.34f
}

/**
 * Una casa vista de frente. [muro] es el rectángulo de la pared (el techo va encima y sobresale un poco).
 *
 * - [encendida]: las ventanas y la puerta dejan ver la luz de dentro (de noche, con gente despierta).
 * - [dano], de 0 a 1 (el arco «daños» del calendario): desde 0.3 tiene hollín sobre los vanos; desde 0.5 le falta medio
 *   techo y se ven las vigas quemadas; desde 0.8 es una ruina, sin techo y con el muro desmochado.
 */
fun DrawScope.casa(l: Luces, muro: Rect, c: Casa, lejos: Float, azar: Azar, encendida: Boolean = false, dano: Float = 0f) {
    val w = muro.width; val h = muro.height
    val solALaDerecha = l.solX > muro.center.x / ANCHO
    val pared = l.tono(c.muro, lejos)
    val sombra = l.tono(lerp(c.muro, Color(0xFF2A2C3A), 0.30f), lejos)
    val tiznado = l.tono(lerp(c.muro, Color(0xFF1C1816), 0.75f), lejos)
    val ruina = dano >= 0.8f

    // El muro: entero, o desmochado si es una ruina.
    val contorno = if (!ruina) Path().apply { addRect(muro) } else Path().apply {
        moveTo(muro.left, muro.bottom); lineTo(muro.left, muro.top + h * azar.entre(0.1f, 0.3f))
        var x = muro.left
        while (x < muro.right) { x = minOf(x + w * azar.entre(0.08f, 0.2f), muro.right); lineTo(x, muro.top + h * azar.entre(0.05f, 0.6f)) }
        lineTo(muro.right, muro.bottom); close()
    }
    clipPath(contorno) {
        drawRect(Brush.horizontalGradient(if (solALaDerecha) listOf(sombra, pared, pared) else listOf(pared, pared, sombra), muro.left, muro.right), muro.topLeft, muro.size)
        c.zocalo?.let { drawRect(l.tono(it, lejos), Offset(muro.left, muro.bottom - h * 0.22f), Size(w, h * 0.22f)) }
        // La sombra del alero.
        if (!ruina) drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), muro.top, muro.top + h * 0.2f), muro.topLeft, Size(w, h * 0.2f))
        // La puerta y las ventanas.
        val pw = w * 0.2f; val ph = h * 0.58f
        val px = muro.left + w * (if (c.ventanas >= 2) 0.5f else 0.3f) - pw / 2f
        val vano = if (encendida && !ruina) LUZ_DE_CASA else l.tono(lerp(c.puerta, Color(0xFF14110F), 0.5f), lejos * 0.6f)
        drawRect(vano, Offset(px, muro.bottom - ph), Size(pw, ph))
        if (!encendida && dano < 0.5f) drawRect(l.tono(c.puerta, lejos), Offset(px + pw * 0.12f, muro.bottom - ph + ph * 0.06f), Size(pw * 0.76f, ph * 0.94f))
        val vw = w * 0.13f; val vh = h * 0.22f
        val lugares = when (c.ventanas) { 0 -> emptyList(); 1 -> listOf(0.72f); 2 -> listOf(0.2f, 0.8f); else -> listOf(0.18f, 0.82f, 0.5f) }
        lugares.forEachIndexed { i, k ->
            val vy = if (i == 2) muro.top + h * 0.08f else muro.top + h * 0.3f
            drawRect(vano, Offset(muro.left + w * k - vw / 2f, vy), Size(vw, vh))
        }
        if (dano >= 0.3f) {
            // Hollín que sube desde los vanos.
            val f = ((dano - 0.3f) / 0.5f).coerceIn(0f, 1f)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, tiznado.copy(alpha = 0.75f * f)), muro.bottom - ph - h * 0.5f, muro.bottom - ph), Offset(px - pw * 0.3f, muro.top), Size(pw * 1.6f, h - ph))
            lugares.forEach { k -> drawRect(tiznado.copy(alpha = 0.6f * f), Offset(muro.left + w * k - vw * 0.8f, muro.top), Size(vw * 1.6f, h * 0.32f)) }
        }
    }
    if (encendida && !ruina && l.noche > 0.2f) {
        // El resplandor de la puerta sobre el suelo.
        drawOval(Brush.radialGradient(listOf(LUZ_DE_CASA.copy(alpha = 0.35f * l.noche), Color.Transparent), Offset(muro.left + w * 0.4f, muro.bottom), w * 0.6f),
            Offset(muro.left - w * 0.2f, muro.bottom - h * 0.15f), Size(w * 1.2f, h * 0.4f), blendMode = BlendMode.Plus)
    }
    if (ruina) {
        // Escombros al pie y una viga caída.
        val piedra = l.tono(lerp(c.muro, Color(0xFF3A3632), 0.4f), lejos)
        repeat(6) { drawCircle(piedra, w * azar.entre(0.03f, 0.07f), Offset(azar.entre(muro.left - w * 0.1f, muro.right + w * 0.1f), muro.bottom - w * 0.02f)) }
        drawLine(l.tono(Color(0xFF241C18), lejos), Offset(muro.left + w * 0.15f, muro.bottom - h * 0.1f), Offset(muro.left + w * 0.7f, muro.top + h * 0.35f), w * 0.035f, StrokeCap.Round)
        return
    }

    // El techo.
    val alero = w * 0.07f
    val alto = alturaTecho(c, w)
    val teja = l.tono(c.colorTecho, lejos); val tejaLuz = l.tono(lerp(c.colorTecho, Color.White, 0.22f), lejos); val tejaSombra = l.tono(lerp(c.colorTecho, Color(0xFF20181A), 0.35f), lejos)
    val x0 = muro.left - alero; val x1 = muro.right + alero; val y0 = muro.top + h * 0.02f
    val forma = when (c.techo) {
        Techo.TEJA, Techo.MADERA -> poligono(Offset(x0, y0), Offset(x1, y0), Offset(x1 - w * 0.16f, y0 - alto), Offset(x0 + w * 0.16f, y0 - alto))
        Techo.PIZARRA -> poligono(Offset(x0, y0), Offset(x1, y0), Offset(muro.center.x + w * 0.06f, y0 - alto), Offset(muro.center.x - w * 0.06f, y0 - alto))
        Techo.PAJA -> Path().apply {
            moveTo(x0 - w * 0.03f, y0 + h * 0.06f)
            cubicTo(x0 + w * 0.1f, y0 - alto * 0.9f, muro.center.x - w * 0.2f, y0 - alto, muro.center.x, y0 - alto)
            cubicTo(muro.center.x + w * 0.2f, y0 - alto, x1 - w * 0.1f, y0 - alto * 0.9f, x1 + w * 0.03f, y0 + h * 0.06f)
            close()
        }
        Techo.PLANO -> poligono(Offset(x0 + alero * 0.5f, y0), Offset(x1 - alero * 0.5f, y0), Offset(x1 - alero * 0.5f, y0 - alto), Offset(x0 + alero * 0.5f, y0 - alto))
    }
    // Con el techo medio caído solo queda la parte de un lado, y las vigas.
    val queda = if (dano >= 0.5f) Rect(x0 - 2f, y0 - alto - 2f, x0 + (x1 - x0) * (1f - (dano - 0.3f)), y0 + h * 0.1f) else null
    if (queda != null) {
        val viga = l.tono(Color(0xFF241C18), lejos)
        for (i in 1..4) { val x = queda.right + (x1 - queda.right) * i / 5f; drawLine(viga, Offset(x, y0), Offset(x - w * 0.1f, y0 - alto * (1f - i / 6f)), w * 0.022f, StrokeCap.Round) }
        drawLine(viga, Offset(queda.right, y0 - alto * 0.5f), Offset(x1 - w * 0.1f, y0 - alto * 0.1f), w * 0.022f, StrokeCap.Round)
    }
    val pintar: DrawScope.() -> Unit = {
        drawRect(Brush.verticalGradient(listOf(tejaLuz, teja, tejaSombra), y0 - alto, y0 + h * 0.06f), Offset(x0 - w * 0.05f, y0 - alto), Size(x1 - x0 + w * 0.1f, alto + h * 0.08f))
        val raya = tejaSombra.copy(alpha = 0.55f)
        when (c.techo) {
            Techo.TEJA -> {
                // Canales de teja que bajan, y la cumbrera.
                var x = x0; while (x < x1) { drawLine(raya, Offset(x + (muro.center.x - x) * 0.28f, y0 - alto), Offset(x, y0), 0.5f); x += maxOf(1.6f, w * 0.055f) }
                drawLine(tejaLuz, Offset(x0 + w * 0.16f, y0 - alto), Offset(x1 - w * 0.16f, y0 - alto), maxOf(0.7f, w * 0.025f), StrokeCap.Round)
            }
            Techo.MADERA, Techo.PIZARRA -> {
                var y = y0 - alto + alto * 0.18f; while (y < y0) { drawLine(raya, Offset(x0, y), Offset(x1, y), 0.5f); y += maxOf(1.5f, alto * 0.16f) }
            }
            Techo.PAJA -> repeat((w * 1.2f).toInt().coerceAtMost(80)) {
                val x = azar.entre(x0, x1); val y = azar.entre(y0 - alto * 0.9f, y0)
                drawLine((if (azar.real() > 0.5f) tejaLuz else tejaSombra).copy(alpha = 0.5f), Offset(x, y), Offset(x + (x - muro.center.x) * 0.08f, y + alto * 0.16f), 0.5f)
            }
            Techo.PLANO -> Unit
        }
    }
    clipPath(forma) { if (queda != null) clipPath(Path().apply { addRect(queda) }) { pintar() } else pintar() }
    if (c.techo == Techo.PLANO) {
        // Las vigas que asoman bajo el pretil.
        val viga = l.tono(Color(0xFF4A3624), lejos)
        var x = muro.left + w * 0.1f; while (x < muro.right - w * 0.05f) { drawCircle(viga, maxOf(0.6f, w * 0.018f), Offset(x, y0 + h * 0.07f)); x += w * 0.14f }
    }
    if (dano < 0.5f) drawLine(tejaSombra, Offset(x0, y0 + 0.3f), Offset(x1, y0 + 0.3f), maxOf(0.6f, w * 0.02f))
}

/** Un muro largo de piedra o de adobe, de [desde] a [hasta], con [alto] de altura y, si se pide, almenas. */
fun DrawScope.muralla(l: Luces, desde: Offset, hasta: Offset, alto: Float, propio: Color, lejos: Float, almenas: Boolean = false) {
    val c = l.tono(propio, lejos); val luz = l.tono(lerp(propio, Color.White, 0.2f), lejos)
    drawPath(poligono(desde, hasta, hasta + Offset(0f, -alto), desde + Offset(0f, -alto)), Brush.verticalGradient(listOf(luz, c), minOf(desde.y, hasta.y) - alto, maxOf(desde.y, hasta.y)))
    if (almenas) {
        val largo = hasta.x - desde.x; val n = (largo / (alto * 0.9f)).toInt().coerceIn(2, 60)
        for (i in 0 until n step 2) {
            val a = desde + (hasta - desde) * (i / n.toFloat()) + Offset(0f, -alto); val b = desde + (hasta - desde) * ((i + 1) / n.toFloat()) + Offset(0f, -alto)
            drawPath(poligono(a, b, b + Offset(0f, -alto * 0.28f), a + Offset(0f, -alto * 0.28f)), luz)
        }
    }
}
