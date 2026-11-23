package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.BRASA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LACA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LACA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LINO_VERDE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.MADERA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.MAGENTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.NARANJA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.NEGRO_LACA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.SODIO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

private val AZUL_BOTA_NEON = Color(0xFF2B48A6)
private val AMARILLO_CAJA_NEON = Color(0xFFE0A830)

/**
 * El pie: el cuerpo bajo de la alacena (dos cajones con sus tiradores de latón, uno entreabierto, y el zócalo negro sobre sus
 * patas de escuadra), el suelo de linóleo a cuadros gastado con el charco, y lo que hay en él: la estufa de resistencias
 * encendida (la luz de abajo), las botas de agua y la caja de plástico con botellas vacías, cortada por el borde.
 */
internal fun DrawScope.pieNeon(g: MarcoNeon) {
    val u = density; val w = g.w; val h = g.h
    if (h - g.fondo < 2f) return
    // Las juntas del suelo con la pared y del cuerpo bajo con la última tabla, que pueden caer a medio píxel.
    filaJuntaNeon(g.suelo, 0f, w, g.c(LINO_VERDE, Offset(w * 0.5f, g.suelo), 0.7f))
    filaJuntaNeon(g.fondo, g.ml, g.mr, g.c(LACA, Offset(w * 0.5f, g.fondo)))
    sueloNeon(g)
    cuerpoBajoNeon(g)
    charcoNeon(g)
    botasNeon(g, Offset(w * 0.6f, minOf(h - w * 0.012f, g.suelo + w * 0.15f)))
    cajaBotellasNeon(g, Offset(w * 0.79f, minOf(h - w * 0.004f, g.suelo + w * 0.175f)))
    estufaNeon(g, Offset(w * MuebleEstanteNeon.ESTUFA_X, minOf(h - w * 0.01f, g.suelo + w * 0.135f)))
    // La sombra del rincón en la pared de los lados, sobre el suelo.
    drawRect(g.a.sombra(0.35f), Offset(0f, g.suelo), Size(w, 2.4f * u))
}

/** El suelo de linóleo: cuadros crema y verde en perspectiva, gastados donde se pisa, con la luz que les llega. */
private fun DrawScope.sueloNeon(g: MarcoNeon) {
    val u = density; val w = g.w; val h = g.h
    val y0 = g.suelo
    if (h <= y0) return
    clipRect(0f, y0, w, h) {
        drawRect(g.t(w * 0.5f, (y0 + h) / 2).c(LINO_VERDE, 0.7f), Offset(0f, y0), Size(w, h - y0))
        // Las filas, cada vez más altas hacia delante; las columnas, hacia el punto de fuga de arriba en medio.
        val fuga = Offset(w * 0.5f, y0 - w * 1.4f)
        val filas = 5
        var ya = y0
        for (f in 0 until filas) {
            val alto = w * (0.022f + 0.012f * f)
            val yb = ya + alto
            for (c in -9..9) {
                fun xEn(y: Float, cc: Int): Float { val x0 = w * 0.5f + cc * w * 0.085f; return fuga.x + (x0 - fuga.x) * (y - fuga.y) / (y0 + w * 0.06f - fuga.y) }
                val xa0 = xEn(ya, c); val xa1 = xEn(ya, c + 1); val xb0 = xEn(yb, c); val xb1 = xEn(yb, c + 1)
                if (maxOf(xa1, xb1) < 0f || minOf(xa0, xb0) > w) continue
                val centro = Offset((xa0 + xb1) / 2, (ya + yb) / 2)
                val t = g.t(centro)
                val gastado = (1f - kotlin.math.abs(centro.x / w - 0.5f) * 1.6f).coerceIn(0f, 1f) * (1f - f / filas.toFloat()) * 0.5f
                val base = if ((c + f) % 2 == 0) LINO else LINO_VERDE
                drawPath(poligonoNeon(xa0, ya, xa1, ya, xb1, yb, xb0, yb), t.c(mezcla(base, mezcla(LINO, LINO_VERDE, 0.5f), gastado + 0.15f * azar(6301, c * 7 + f))))
            }
            drawLine(g.t(w * 0.5f, ya).s(0.3f), Offset(0f, ya), Offset(w, ya), 0.8f * u)
            ya = yb
        }
        // El brillo encerado que deja la luz de la estufa y el de la ventana, en tiras.
        for (j in 0 until 6) {
            val x = w * (0.08f + 0.15f * j)
            val t = g.t(x, (y0 + h) / 2)
            drawLine(t.b(0.25f), Offset(x, y0 + (h - y0) * 0.3f), Offset(x + w * 0.05f, y0 + (h - y0) * 0.32f), 1f * u)
        }
    }
}

/**
 * El cuerpo bajo: la caja de los cajones (laca roja con la luz a lo ancho), dos cajones con su junquillo, su tirador de asa de
 * latón y su bocallave (el de la derecha, entreabierto, con un cable que asoma), el zócalo de laca negra con su filete y las
 * patas de escuadra en las esquinas, y la sombra debajo.
 */
private fun DrawScope.cuerpoBajoNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    val y0 = g.fondo; val y1 = g.cajones; val yz = g.zocalo
    val x0 = g.ml; val x1 = g.mr
    // El hueco oscuro bajo el zócalo, entre las patas: la pared y el suelo en sombra (opacos, para que no asome el fondo).
    drawRect(g.t(w * 0.5f, yz).c(PaletaNeon.JUNTA, 0.6f), Offset(x0, yz - 1f), Size(x1 - x0, g.suelo - yz + 2f))
    drawRect(g.a.sombra(0.6f), Offset(x0, yz), Size(x1 - x0, g.pisan - yz))
    filaJuntaNeon(y1, x0, x1, g.c(NEGRO_LACA, Offset(w * 0.5f, y1)))
    // La caja de los cajones.
    bandaNeon(x0, x1, y0, y1, LACA, g)
    val m = w * 0.012f
    val medio = (x0 + x1) / 2
    listOf(Rect(x0 + m * 1.6f, y0 + m, medio - m * 0.5f, y1 - m), Rect(medio + m * 0.5f, y0 + m, x1 - m * 1.6f, y1 - m)).forEachIndexed { i, r ->
        val t = g.t(r.center)
        val abierto = i == 1
        val cara = if (abierto) Rect(r.left, r.top + 2.4f * u, r.right, r.bottom + 1.2f * u) else r
        if (abierto) {
            drawRect(g.a.sombra(0.85f), r.topLeft, Size(r.width, 4f * u))
            // El cable que asoma por la rendija.
            val p = Path().apply { moveTo(r.left + r.width * 0.3f, r.top + 2f * u); cubicTo(r.left + r.width * 0.32f, r.top - w * 0.02f, r.left + r.width * 0.45f, r.top - w * 0.01f, r.left + r.width * 0.48f, r.top + 2f * u) }
            drawPath(p, t.c(Color(0xFFE8E0D0)), style = Stroke(1.6f * u))
        }
        // El canto del frente, la cara y el junquillo.
        drawRect(t.s(0.5f), Offset(cara.left - 1.2f * u, cara.top - 1.2f * u), Size(cara.width + 2.4f * u, cara.height + 2.4f * u))
        bandaNeon(cara.left, cara.right, cara.top, cara.bottom, LACA, g, 1.05f)
        drawRect(t.b(0.45f), cara.topLeft, Size(cara.width, 1.2f * u))
        drawRect(t.s(0.35f), Offset(cara.left, cara.bottom - 1.4f * u), Size(cara.width, 1.4f * u))
        val dentro = Rect(cara.left + m * 0.8f, cara.top + m * 0.8f, cara.right - m * 0.8f, cara.bottom - m * 0.8f)
        drawRect(t.c(NEGRO_LACA), dentro.topLeft, dentro.size, style = Stroke(1.6f * u))
        drawRect(t.c(LATON, 0.8f), Offset(dentro.left, dentro.top + 1.6f * u), Size(dentro.width, maxOf(1f, 0.7f * u)))
        // La laca saltada en las esquinas de abajo.
        drawPath(poligonoNeon(cara.left, cara.bottom, cara.left + w * 0.02f, cara.bottom, cara.left, cara.bottom - w * 0.012f), t.c(MADERA, 0.8f))
        // El tirador de asa de latón con sus dos pletinas, y la bocallave.
        val c = Offset(cara.center.x, cara.center.y + cara.height * 0.05f)
        val an = w * 0.07f
        listOf(-1f, 1f).forEach { s ->
            val pl = Rect(c.x + s * an / 2 - w * 0.009f, c.y - w * 0.014f, c.x + s * an / 2 + w * 0.009f, c.y + w * 0.006f)
            drawRoundRect(t.s(0.45f), pl.topLeft + Offset(1.4f * u, 1.4f * u), pl.size, CornerRadius(2f * u))
            drawRoundRect(t.c(LATON_OSC), pl.topLeft, pl.size, CornerRadius(2f * u))
            drawRoundRect(t.c(LATON), pl.topLeft, Size(pl.width, pl.height * 0.5f), CornerRadius(2f * u))
        }
        drawArc(t.s(0.45f), 10f, 160f, false, Offset(c.x - an / 2 + 1.4f * u, c.y - w * 0.02f + 2f * u), Size(an, w * 0.04f), style = Stroke(2.2f * u))
        drawArc(t.c(LATON), 10f, 160f, false, Offset(c.x - an / 2, c.y - w * 0.02f), Size(an, w * 0.04f), style = Stroke(2.2f * u))
        drawArc(t.b(0.7f, 1.2f), 40f, 60f, false, Offset(c.x - an / 2, c.y - w * 0.02f - 0.6f * u), Size(an, w * 0.04f), style = Stroke(0.8f * u))
        val bl = Rect(c.x - w * 0.006f, cara.top + cara.height * 0.14f, c.x + w * 0.006f, cara.top + cara.height * 0.34f)
        drawRect(t.c(LATON), bl.topLeft, bl.size)
        drawRect(t.c(Color(0xFF0A0608)), Offset(bl.center.x - 0.6f * u, bl.top + bl.height * 0.25f), Size(1.2f * u, bl.height * 0.5f))
    }
    // El zócalo de laca negra, con su filete dorado y su moldura.
    bandaNeon(x0 - w * 0.006f, x1 + w * 0.006f, y1, yz, NEGRO_LACA, g)
    bandaNeon(x0 - w * 0.006f, x1 + w * 0.006f, y1, y1 + w * 0.005f, mezcla(NEGRO_LACA, LACA_LUZ, 0.45f), g, 1.1f)
    bandaNeon(x0, x1, yz - w * 0.01f, yz - w * 0.008f, LATON, g, 0.9f)
    // Las patas de escuadra.
    listOf(true, false).forEach { izq ->
        val xa = if (izq) x0 else x1 - w * 0.07f
        val r = Rect(xa, yz, xa + w * 0.07f, g.pisan)
        val t = g.t(r.center)
        val pata = if (izq) poligonoNeon(r.left, r.top, r.right, r.top, r.right - r.width * 0.15f, r.top + r.height * 0.35f, r.left + r.width * 0.45f, r.top + r.height * 0.45f, r.left + r.width * 0.32f, r.bottom, r.left, r.bottom)
            else poligonoNeon(r.left, r.top, r.right, r.top, r.right, r.bottom, r.right - r.width * 0.32f, r.bottom, r.right - r.width * 0.45f, r.top + r.height * 0.45f, r.left + r.width * 0.15f, r.top + r.height * 0.35f)
        drawPath(pata, t.c(NEGRO_LACA, 1.1f))
        drawPath(pata, t.b(0.35f), style = Stroke(0.8f * u))
        contactoNeon(Offset(if (izq) r.left + r.width * 0.16f else r.right - r.width * 0.16f, r.bottom), r.width * 0.6f, 3f * u, t, 0.6f)
    }
}

/** El charco en el linóleo (de las botas y del paraguas), que refleja el neón de la ventana y la estufa. */
private fun DrawScope.charcoNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    val c = Offset(w * 0.5f, minOf(g.h - w * 0.02f, g.suelo + w * 0.14f))
    val r = Rect(c.x - w * 0.16f, c.y - w * 0.018f, c.x + w * 0.2f, c.y + w * 0.022f)
    val p = Path().apply {
        moveTo(r.left, r.center.y)
        cubicTo(r.left + r.width * 0.1f, r.top, r.left + r.width * 0.45f, r.top - w * 0.006f, r.left + r.width * 0.6f, r.top + w * 0.004f)
        cubicTo(r.right - r.width * 0.1f, r.top, r.right, r.center.y, r.right - r.width * 0.05f, r.bottom - w * 0.008f)
        cubicTo(r.left + r.width * 0.6f, r.bottom + w * 0.004f, r.left + r.width * 0.2f, r.bottom, r.left, r.center.y)
        close()
    }
    val arriba = if (g.a.noche) SODIO else MAGENTA
    drawPath(p, Brush.horizontalGradient(listOf(mezcla(g.a.colores[3], Color(0xFF14181C), 0.3f), mezcla(LINO_VERDE, Color(0xFF101418), 0.5f), mezcla(arriba, Color(0xFF14181C), 0.35f)), r.left, r.right))
    clipPath(p) {
        for (j in 0 until 5) {
            val x = r.left + r.width * (0.1f + 0.2f * j)
            drawLine((if (j < 2) g.a.colores[3] else arriba).copy(alpha = 0.6f), Offset(x, r.center.y - w * 0.004f), Offset(x + w * 0.03f, r.center.y - w * 0.004f), 1.2f * u, StrokeCap.Round)
        }
    }
    drawPath(p, Color.White.copy(alpha = 0.12f), style = Stroke(0.8f * u))
}

/**
 * La estufa de resistencias, en el suelo a la izquierda: el reflector de chapa pulida que devuelve el fuego, las dos barras
 * encendidas (naranja; en los Sueños, al mínimo, rojas), la rejilla de alambre de delante, el marco cromado, el asa, la perilla,
 * las patas de tubo y el cable. Su luz sube por los cajones y las repisas de abajo y tiñe el suelo.
 */
private fun DrawScope.estufaNeon(g: MarcoNeon, pie: Offset) {
    val u = density; val w = g.w
    val noche = g.a.noche
    val r = Rect(pie.x - w * 0.11f, pie.y - w * 0.21f, pie.x + w * 0.11f, pie.y - w * 0.02f)
    val t = g.t(r.center)
    val fuego = if (noche) BRASA else NARANJA
    val alma = if (noche) Color(0xFFFF8A5A) else Color(0xFFFFE08A)
    // El resplandor en el suelo y en lo de alrededor.
    mancha(Offset(r.center.x, pie.y), w * 0.32f, fuego.copy(alpha = if (noche) 0.2f else 0.36f))
    // Las patas de tubo.
    drawLine(t.c(ALUMINIO), Offset(r.left + r.width * 0.08f, r.bottom), Offset(r.left - w * 0.01f, pie.y), 2.4f * u, StrokeCap.Round)
    drawLine(t.c(ALUMINIO), Offset(r.right - r.width * 0.08f, r.bottom), Offset(r.right + w * 0.01f, pie.y), 2.4f * u, StrokeCap.Round)
    drawOval(g.a.sombra(0.5f), Offset(r.left - w * 0.03f, pie.y - 2f * u), Size(r.width + w * 0.06f, 5f * u))
    // El cuerpo: la caja de chapa y el reflector.
    drawRoundRect(t.c(Color(0xFF4A4650)), r.topLeft, r.size, CornerRadius(4f * u))
    val ref = Rect(r.left + r.width * 0.06f, r.top + r.height * 0.1f, r.right - r.width * 0.06f, r.bottom - r.height * 0.1f)
    drawRect(Brush.verticalGradient(listOf(mezcla(fuego, Color(0xFF2A1410), 0.55f), mezcla(fuego, Color.White, 0.2f), mezcla(fuego, Color(0xFF2A1410), 0.5f)), ref.top, ref.bottom), ref.topLeft, ref.size)
    // Las dos barras encendidas, con sus espiras.
    listOf(0.32f, 0.68f).forEach { fy ->
        val y = ref.top + ref.height * fy
        drawLine(fuego.copy(alpha = 0.5f), Offset(ref.left + 2f * u, y), Offset(ref.right - 2f * u, y), 7f * u, StrokeCap.Round)
        drawLine(fuego, Offset(ref.left + 2f * u, y), Offset(ref.right - 2f * u, y), 4f * u, StrokeCap.Round)
        drawLine(alma, Offset(ref.left + 3f * u, y), Offset(ref.right - 3f * u, y), 1.6f * u, StrokeCap.Round)
        var x = ref.left + 4f * u
        while (x < ref.right - 4f * u) { drawLine(mezcla(fuego, Color(0xFF5A1A10), 0.4f), Offset(x, y - 2f * u), Offset(x + 1.6f * u, y + 2f * u), 0.7f * u); x += 3.2f * u }
        drawRect(t.c(ALUMINIO), Offset(ref.left - 1f * u, y - 3f * u), Size(3f * u, 6f * u))
        drawRect(t.c(ALUMINIO), Offset(ref.right - 2f * u, y - 3f * u), Size(3f * u, 6f * u))
    }
    // La rejilla de alambre.
    var x = ref.left + ref.width * 0.08f
    while (x < ref.right) { drawLine(Color(0xFF2A2428).copy(alpha = 0.7f), Offset(x, ref.top), Offset(x, ref.bottom), 0.8f * u); x += ref.width * 0.08f }
    // El marco cromado, encendido por el fuego por dentro y con su filo.
    drawRoundRect(t.c(ALUMINIO, 1.1f), ref.topLeft, ref.size, CornerRadius(2f * u), style = Stroke(2.2f * u))
    drawRect(mezcla(fuego, Color.White, 0.5f).copy(alpha = 0.6f), Offset(ref.left, ref.bottom - 1.2f * u), Size(ref.width, 1.2f * u))
    drawRoundRect(t.b(0.5f), r.topLeft, r.size, CornerRadius(4f * u), style = Stroke(0.8f * u))
    // El asa y la perilla.
    drawRoundRect(t.c(ALUMINIO), Offset(r.left + r.width * 0.3f, r.top - w * 0.025f), Size(r.width * 0.4f, w * 0.04f), CornerRadius(4f * u), style = Stroke(2f * u))
    drawCircle(t.c(Color(0xFF1C1A22)), w * 0.012f, Offset(r.right - r.width * 0.05f, r.top + r.height * 0.05f))
    drawCircle(t.b(0.6f), w * 0.004f, Offset(r.right - r.width * 0.06f, r.top + r.height * 0.04f))
    // El cable que va a la pared.
    val cable = Path().apply { moveTo(r.left + r.width * 0.1f, r.bottom); cubicTo(r.left - w * 0.03f, pie.y + w * 0.01f, r.left - w * 0.06f, g.suelo + w * 0.02f, 0f, g.suelo + w * 0.03f) }
    drawPath(cable, t.c(Color(0xFF1A1820)), style = Stroke(1.6f * u))
}

/**
 * Las botas de agua, una detrás de la otra: la caña, el talón y el pie de perfil, la boca oscura, el ribete, el brillo largo de la
 * goma mojada y las gotas; su sombra en el charco.
 */
private fun DrawScope.botasNeon(g: MarcoNeon, pie: Offset) {
    val u = density; val w = g.w
    listOf(Offset(w * 0.05f, -w * 0.012f) to 0.85f, Offset(0f, 0f) to 1f).forEach { (o, luz) ->
        val b = pie + o
        val t = g.t(b.x, b.y - w * 0.08f)
        val alto = w * 0.2f; val ancho = w * 0.08f
        val bota = Path().apply {
            moveTo(b.x - ancho * 0.5f, b.y - alto)
            lineTo(b.x + ancho * 0.5f, b.y - alto)
            lineTo(b.x + ancho * 0.48f, b.y - alto * 0.32f)
            cubicTo(b.x + ancho * 0.6f, b.y - alto * 0.2f, b.x + ancho * 1.3f, b.y - alto * 0.2f, b.x + ancho * 1.38f, b.y - alto * 0.06f)
            lineTo(b.x + ancho * 1.38f, b.y)
            lineTo(b.x - ancho * 0.52f, b.y)
            close()
        }
        drawOval(g.a.sombra(0.5f), Offset(b.x - ancho * 0.6f, b.y - 2f * u), Size(ancho * 2.2f, 5f * u))
        drawPath(bota, t.c(AZUL_BOTA_NEON, luz))
        clipPath(bota) {
            drawRect(t.s(0.35f), Offset(b.x + ancho * 0.1f, b.y - alto), Size(ancho * 0.5f, alto))
            drawRect(t.b(0.5f), Offset(b.x - ancho * 0.36f, b.y - alto * 0.92f), Size(ancho * 0.12f, alto * 0.6f))
            drawRect(t.c(Color(0xFF15181E)), Offset(b.x - ancho, b.y - alto * 0.07f), Size(ancho * 3f, alto * 0.07f))
            drawRect(t.c(Color(0xFFE8E0D0), 0.8f), Offset(b.x - ancho, b.y - alto * 0.1f), Size(ancho * 3f, alto * 0.025f))
        }
        drawOval(t.c(Color(0xFF0E1018)), Offset(b.x - ancho * 0.5f, b.y - alto - ancho * 0.12f), Size(ancho, ancho * 0.24f))
        drawOval(t.b(0.5f), Offset(b.x - ancho * 0.5f, b.y - alto - ancho * 0.12f), Size(ancho, ancho * 0.24f), style = Stroke(0.8f * u))
        // Las gotas en la goma.
        for (j in 0 until 3) drawCircle(t.b(0.8f, 1.3f), 1.1f * u, Offset(b.x - ancho * 0.2f + ancho * 0.3f * j, b.y - alto * (0.4f + 0.15f * j)))
    }
}

/**
 * La caja de plástico de botellas, cortada por el borde derecho: las asas caladas, los huecos de la rejilla con el vidrio de las
 * botellas detrás (pardo y verde) y los cuellos que asoman, con su brillo.
 */
private fun DrawScope.cajaBotellasNeon(g: MarcoNeon, pie: Offset) {
    val u = density; val w = g.w
    val r = Rect(pie.x, pie.y - w * 0.14f, pie.x + w * 0.26f, pie.y)
    val t = g.t(r.center)
    // Los cuellos de las botellas.
    for (j in 0 until 4) {
        val x = r.left + r.width * (0.14f + 0.24f * j)
        val vidrio = if (j % 2 == 0) Color(0xFF6A3A1A) else Color(0xFF2E6A3A)
        val cuello = Rect(x - w * 0.011f, r.top - w * 0.07f, x + w * 0.011f, r.top + w * 0.02f)
        drawRect(t.c(vidrio, 1.2f), cuello.topLeft, cuello.size)
        drawRect(t.b(0.7f, 1.3f), Offset(cuello.left + 1.2f * u, cuello.top + 2f * u), Size(1.2f * u, cuello.height - 4f * u))
        drawRect(t.c(vidrio, 1.4f), Offset(cuello.left - 1f * u, cuello.top), Size(cuello.width + 2f * u, w * 0.008f))
    }
    drawOval(g.a.sombra(0.5f), Offset(r.left - w * 0.01f, pie.y - 2f * u), Size(r.width + w * 0.04f, 5f * u))
    drawRect(t.c(AMARILLO_CAJA_NEON), r.topLeft, r.size)
    clipRect(r.left, r.top, r.right, r.bottom) {
        planosNeon(Rect(r.left, r.top, r.left + r.width * 0.9f, r.bottom), t)
        // La rejilla: huecos por donde se ve el vidrio.
        for (f in 0 until 2) for (c in 0 until 5) {
            val hx = r.left + r.width * (0.05f + 0.19f * c); val hy = r.top + r.height * (0.34f + 0.3f * f)
            val hueco = Rect(hx, hy, hx + r.width * 0.13f, hy + r.height * 0.2f)
            drawRoundRect(t.c(if (c % 2 == 0) Color(0xFF4A2A14) else Color(0xFF1E4A2A), 0.8f), hueco.topLeft, hueco.size, CornerRadius(2f * u))
            drawRect(t.b(0.35f), Offset(hueco.left + hueco.width * 0.2f, hueco.top + 1f * u), Size(1f * u, hueco.height - 2f * u))
            drawRect(t.s(0.4f), hueco.topLeft, Size(hueco.width, 1.2f * u))
        }
        // El asa calada.
        drawRoundRect(t.c(Color(0xFF1A1418)), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.08f), Size(r.width * 0.35f, r.height * 0.14f), CornerRadius(4f * u))
        drawRect(t.b(0.55f), r.topLeft, Size(r.width, 1.4f * u))
    }
}
