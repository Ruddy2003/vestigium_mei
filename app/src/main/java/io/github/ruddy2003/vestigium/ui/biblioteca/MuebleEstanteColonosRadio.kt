package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.BOMBILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CORDEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CRUDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.NOGAL_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.OXIDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/*
 * La radio de onda corta de la cocina de Colonos, la pieza que manda arriba: armada en el pueblo con piezas de antes, en una
 * caja de nogal; con el tapete de ganchillo de debajo, la batería que la alimenta y sus cables.
 */

/** La tela del altavoz: un beige de lino, y la baquelita de las perillas. */
private val TELA_RADIO = Color(0xFFC9AE7A)
private val BAQUELITA = Color(0xFF3B2417)

/**
 * La radio, en [r]: la caja de nogal con el remate en arco, su veta y su canto de luz; el marco de moldura; la boca del
 * altavoz con su tela de lino y las cinco varillas de madera calada delante; el dial de media luna con sus rayas (sin cifras),
 * su aguja roja y su vidrio; las tres perillas (una de otra radio, roja); el remiendo de lata clavado en la esquina; las dos
 * patas; y su sombra en la pared. De noche, el dial se enciende.
 */
internal fun DrawScope.radioColonos(g: MarcoColonos, r: Rect) {
    val u = density; val w = g.w; val a = g.a
    val arco = r.height * 0.22f
    val caja = Path().apply {
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + arco)
        cubicTo(r.left, r.top + arco * 0.2f, r.left + r.width * 0.2f, r.top, r.center.x, r.top)
        cubicTo(r.right - r.width * 0.2f, r.top, r.right, r.top + arco * 0.2f, r.right, r.top + arco)
        lineTo(r.right, r.bottom); close()
    }
    // El costado de la caja, a la derecha (en sombra de día), que le da cuerpo.
    val fondoC = w * 0.016f
    val costado = poligonoColonos(r.right, r.top + arco, r.right + fondoC, r.top + arco - fondoC * 0.6f, r.right + fondoC, r.bottom - fondoC * 0.6f, r.right, r.bottom)
    sombraParedColonos(g, Path().apply { addPath(caja); addPath(costado) }, r.center, g.apoyoAtras, 0.2f)
    val c = r.center
    val k = g.k(c); val br = g.br(c)
    val kd = g.k(Offset(r.right, c.y))
    drawPath(costado, if (a.oscuro) a.luz(NOGAL, kd, g.br(Offset(r.right, c.y))) else a.luz(mezcla(NOGAL, HIERRO, 0.35f), 1f))
    drawPath(caja, a.luz(NOGAL, k, br))
    clipPath(caja) {
        vetas(r, a.luz(mezcla(NOGAL, HIERRO, 0.5f), k).copy(alpha = 0.5f), 7801, 1.1f, vertical = true, grosor = 0.6f, ondulacion = 1.2f)
        // La luz de la ventana en el lado izquierdo y lo alto del arco (de día); de noche, la de la bombilla a la derecha.
        if (!a.oscuro) {
            drawRect(a.claro(0.22f), Offset(r.left, r.top), Size(r.width * 0.16f, r.height))
            drawRect(a.sombra(0.22f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
        } else {
            drawRect(a.sombra(0.35f), Offset(r.left, r.top), Size(r.width * 0.35f, r.height))
            drawRect(a.brillo(0.16f, kd), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
        }
    }
    // El marco de moldura de dentro: una línea clara y una oscura, siguiendo el arco.
    val m = w * 0.012f
    val marco = Path().apply {
        moveTo(r.left + m, r.bottom - m); lineTo(r.left + m, r.top + arco + m * 0.3f)
        cubicTo(r.left + m, r.top + arco * 0.3f + m, r.left + r.width * 0.22f, r.top + m, r.center.x, r.top + m)
        cubicTo(r.right - r.width * 0.22f, r.top + m, r.right - m, r.top + arco * 0.3f + m, r.right - m, r.top + arco + m * 0.3f)
        lineTo(r.right - m, r.bottom - m); close()
    }
    drawPath(corridoColonos(marco, 0.8f * u, 0.8f * u), a.sombra(0.4f), style = Stroke(1.2f * u))
    drawPath(marco, a.luz(NOGAL_LUZ, k, br), style = Stroke(1.2f * u))
    // La boca del altavoz: la tela de lino con su trama y, delante, las varillas caladas.
    val boca = Rect(r.left + r.width * 0.15f, r.top + arco * 0.7f, r.right - r.width * 0.15f, r.top + r.height * 0.56f)
    val bocaP = Path().apply {
        moveTo(boca.left, boca.bottom); lineTo(boca.left, boca.top + boca.height * 0.3f)
        cubicTo(boca.left, boca.top, boca.right, boca.top, boca.right, boca.top + boca.height * 0.3f)
        lineTo(boca.right, boca.bottom); close()
    }
    drawPath(corridoColonos(bocaP, -0.8f * u, -0.8f * u), a.sombra(0.45f), style = Stroke(2f * u))
    drawPath(bocaP, a.luz(TELA_RADIO, k, br))
    clipPath(bocaP) {
        val trama = Path()
        var y = boca.top; while (y < boca.bottom) { trama.moveTo(boca.left, y); trama.lineTo(boca.right, y); y += 1.6f * u }
        var x = boca.left; while (x < boca.right) { trama.moveTo(x, boca.top); trama.lineTo(x, boca.bottom); x += 1.6f * u }
        drawPath(trama, a.sombra(0.12f), style = Stroke(0.5f * u))
        drawRect(a.sombra(0.35f), boca.topLeft, Size(boca.width, boca.height * 0.12f))
        // Las varillas de madera calada, con su sombra en la tela.
        val varillas = Path(); val sombras = Path()
        for (i in 1..5) {
            val vx = boca.left + boca.width * i / 6f
            varillas.addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(vx - w * 0.005f, boca.top - 2f, vx + w * 0.005f, boca.bottom + 2f), androidx.compose.ui.geometry.CornerRadius(w * 0.005f)))
            sombras.addRect(Rect(vx + w * 0.005f, boca.top, vx + w * 0.009f, boca.bottom))
        }
        drawPath(sombras, a.sombra(0.35f))
        drawPath(varillas, a.luz(NOGAL, k, br))
        drawPath(varillas, a.luz(NOGAL_LUZ, k, br).copy(alpha = 0.6f), style = Stroke(0.6f * u))
    }
    drawPath(bocaP, a.luz(mezcla(NOGAL, HIERRO, 0.4f), k), style = Stroke(1f * u))
    // El dial de media luna, con su bisel de lata.
    val dc = Offset(r.center.x, r.top + r.height * 0.79f)
    val dr = r.width * 0.15f
    val dial = Rect(dc.x - dr, dc.y - dr, dc.x + dr, dc.y + dr)
    val media = Path().apply { moveTo(dial.left, dc.y); arcTo(dial, 180f, 180f, false); close() }
    drawPath(corridoColonos(media, 1f * u, 1.2f * u), a.sombra(0.45f))
    val encendido = a.oscuro
    drawPath(media, if (encendido) mezcla(Color(0xFFFFE2A0), BOMBILLA, 0.35f) else a.luz(PAPEL, k, br))
    val rayas = Path()
    for (i in 0..16) {
        val ang = Math.toRadians((190 + i * 160.0 / 16)).toFloat()
        val l = if (i % 4 == 0) 0.3f else 0.16f
        rayas.moveTo(dc.x + dr * 0.9f * kotlin.math.cos(ang), dc.y + dr * 0.9f * kotlin.math.sin(ang))
        rayas.lineTo(dc.x + dr * (0.9f - l) * kotlin.math.cos(ang), dc.y + dr * (0.9f - l) * kotlin.math.sin(ang))
    }
    drawPath(rayas, (if (encendido) Color(0xFF5A3410) else TINTA).copy(alpha = 0.75f), style = Stroke(0.6f * u))
    val aguja = Math.toRadians(238.0).toFloat()
    drawLine(if (encendido) Color(0xFFB0301C) else a.luz(LADRILLO, k), dc, Offset(dc.x + dr * 0.86f * kotlin.math.cos(aguja), dc.y + dr * 0.86f * kotlin.math.sin(aguja)), 1.1f * u)
    drawPath(media, a.luz(LATA, k, br), style = Stroke(1.6f * u))
    drawArc(Color.White.copy(alpha = if (encendido) 0.35f else 0.55f), 200f, 50f, false, Offset(dial.left + dr * 0.2f, dial.top + dr * 0.2f), Size(dr * 1.6f, dr * 1.6f), style = Stroke(1f * u))
    if (encendido) mancha(Offset(dc.x, dc.y - dr * 0.4f), w * 0.06f, BOMBILLA.copy(alpha = 0.3f))
    // Las tres perillas: dos de baquelita y una roja que vino de otra radio.
    listOf(0.24f to BAQUELITA, 0.5f to BAQUELITA, 0.76f to LADRILLO).forEachIndexed { i, (fx, col) ->
        val pc = Offset(r.left + r.width * fx, r.bottom - r.height * 0.1f + (if (i == 1) r.height * 0.02f else 0f))
        val pr = w * (if (i == 2) 0.011f else 0.013f)
        drawCircle(a.sombra(0.5f), pr, pc + Offset(1f * u, 1.4f * u))
        drawCircle(a.luz(col, k, br), pr, pc)
        drawCircle(a.luz(mezcla(col, HIERRO, 0.4f), k, br), pr * 0.7f, pc)
        drawLine(a.luz(CRUDO, k, br).copy(alpha = 0.7f), pc, pc + Offset(pr * 0.5f * (i - 1), -pr * 0.6f), 0.8f * u)
        drawCircle(a.brillo(0.6f, maxOf(k, 0.25f), br), pr * 0.25f, pc - Offset(pr * 0.4f, pr * 0.4f))
    }
    // El remiendo de lata en la esquina de arriba a la izquierda, con sus cuatro clavitos.
    val parche = poligonoColonos(r.left - w * 0.004f, r.top + arco * 0.55f, r.left + r.width * 0.15f, r.top + arco * 0.12f, r.left + r.width * 0.2f, r.top + arco * 0.95f,
        r.left - w * 0.002f, r.top + arco * 1.3f)
    drawPath(corridoColonos(parche, 1f * u, 1.2f * u), a.sombra(0.4f))
    drawPath(parche, a.luz(LATA, k, br))
    drawPath(parche, a.luz(OXIDO, k, br).copy(alpha = 0.3f), style = Stroke(1.4f * u))
    listOf(Offset(r.left + w * 0.002f, r.top + arco * 0.6f), Offset(r.left + r.width * 0.13f, r.top + arco * 0.3f), Offset(r.left + r.width * 0.17f, r.top + arco * 0.85f),
        Offset(r.left + w * 0.004f, r.top + arco * 1.15f)).forEach { p -> drawCircle(a.luz(LATA_OSC, k), 0.9f * u, p) }
    // El canto de luz del arco y las patas.
    drawPath(Path().apply {
        moveTo(r.left, r.top + arco); cubicTo(r.left, r.top + arco * 0.2f, r.left + r.width * 0.2f, r.top, r.center.x, r.top)
        if (a.oscuro) cubicTo(r.right - r.width * 0.2f, r.top, r.right, r.top + arco * 0.2f, r.right, r.top + arco)
    }, a.brillo(0.45f, maxOf(k, if (a.oscuro) 0f else 1f), br), style = Stroke(1.2f * u))
    listOf(r.left + r.width * 0.08f, r.right - r.width * 0.16f).forEach { x ->
        drawRect(a.luz(mezcla(NOGAL, HIERRO, 0.5f), k), Offset(x, r.bottom - 1f), Size(r.width * 0.08f, w * 0.006f))
    }
}

/**
 * El tapete de ganchillo de debajo de la radio: su cara de arriba apenas se ve; delante cuelga por el canto de la cornisa una
 * solapa de encaje con sus anillos de agujeros y el borde en ondas, con su sombra en la madera.
 */
internal fun DrawScope.tapeteGanchilloColonos(g: MarcoColonos, r: Rect) {
    val u = density; val w = g.w; val a = g.a
    val hilo = mezcla(CRUDO, Color.White, 0.45f)
    val c = r.center
    val k = g.k(c); val br = g.br(c)
    // La cara de arriba, sobre la tabla.
    drawOval(a.luz(hilo, k, br), Offset(r.left, r.top - w * 0.004f), Size(r.width, r.height + w * 0.004f))
    // La solapa que cuelga por el canto: un medio óvalo con el borde en ondas.
    val sx0 = r.left + r.width * 0.12f; val sx1 = r.right - r.width * 0.1f
    val alto = w * 0.034f
    val solapa = Path().apply {
        moveTo(sx0, r.bottom)
        val n = 9
        for (i in 0 until n) {
            val f0 = i / n.toFloat(); val f1 = (i + 1) / n.toFloat()
            val x0 = sx0 + (sx1 - sx0) * f0; val x1 = sx0 + (sx1 - sx0) * f1
            val y0 = r.bottom + alto * kotlin.math.sin(f0 * Math.PI).toFloat(); val y1 = r.bottom + alto * kotlin.math.sin(f1 * Math.PI).toFloat()
            quadraticTo((x0 + x1) / 2, (y0 + y1) / 2 + w * 0.007f, x1, y1)
        }
        lineTo(sx1, r.bottom); close()
    }
    val kc = g.k(Offset(c.x, r.bottom + alto * 0.5f))
    drawPath(corridoColonos(solapa, w * 0.004f, w * 0.005f), a.sombra(0.35f))
    drawPath(solapa, a.luz(hilo, kc, br))
    clipPath(solapa) {
        // Los agujeros del encaje, en dos anillos, y la sombra de la madera por ellos.
        val agujeros = Path()
        for (fila in 0 until 2) {
            val n = 12 - fila * 3
            for (i in 0 until n) {
                val f = (i + 0.5f) / n
                val x = sx0 + (sx1 - sx0) * (0.08f + 0.84f * f)
                val y = r.bottom + alto * kotlin.math.sin(f * Math.PI).toFloat() * (0.4f + 0.32f * fila) - fila * w * 0.002f
                val s = w * (0.0035f - 0.0008f * fila)
                agujeros.addOval(Rect(x - s, y - s, x + s, y + s))
            }
        }
        drawPath(agujeros, a.luz(PaletaColonos.MAIZ_OSC, kc).copy(alpha = 0.85f))
        drawRect(a.sombra(0.2f), Offset(sx0, r.bottom), Size(sx1 - sx0, w * 0.004f))
    }
    drawPath(solapa, a.sombra(0.2f), style = Stroke(0.6f * u))
}

/**
 * La batería de antes de la guerra que alimenta la radio, en [r]: la caja de pasta negra con su tapa, las costillas del
 * costado, los dos bornes con su capuchón (uno rojo y uno negro, sin signos), una cinta de tela pegada y el polvo; y su sombra.
 */
internal fun DrawScope.bateriaColonos(g: MarcoColonos, r: Rect) {
    val u = density; val w = g.w; val a = g.a
    val caja = Color(0xFF34343A)
    sombraParedColonos(g, Path().apply { addRect(r) }, r.center, g.apoyoAtras)
    contactoColonos(Offset(r.center.x, r.bottom), r.width * 1.15f, w * 0.012f, a, 0.4f)
    val k = g.k(r.center); val br = g.br(r.center)
    val tapa = Rect(r.left - w * 0.003f, r.top, r.right + w * 0.003f, r.top + r.height * 0.18f)
    drawRect(a.luz(caja, k, br), r.topLeft, r.size)
    drawRect(a.luz(mezcla(caja, HIERRO_LUZ, 0.2f), k, br), tapa.topLeft, tapa.size)
    drawRect(a.sombra(0.4f), Offset(r.left, tapa.bottom), Size(r.width, 1.2f * u))
    // Las costillas del frente.
    val costillas = Path()
    for (i in 1..5) { val x = r.left + r.width * i / 6f; costillas.moveTo(x, tapa.bottom + 2f * u); costillas.lineTo(x, r.bottom - 2f * u) }
    drawPath(costillas, a.sombra(0.35f), style = Stroke(1f * u))
    drawPath(corridoColonos(costillas, 1f * u), a.brillo(0.16f, maxOf(k, 0.3f), br), style = Stroke(0.6f * u))
    // La cinta de tela pegada, con su borde roto.
    val cinta = Rect(r.left + r.width * 0.12f, r.top + r.height * 0.46f, r.right - r.width * 0.08f, r.top + r.height * 0.66f)
    drawRect(a.luz(mezcla(CRUDO, CORDEL, 0.35f), k, br), cinta.topLeft, cinta.size)
    drawRect(a.sombra(0.2f), Offset(cinta.left, cinta.bottom - 0.8f * u), Size(cinta.width, 0.8f * u))
    // La luz de la ventana a la izquierda (de día) y el polvo en la tapa.
    if (!a.oscuro) drawRect(a.claro(0.18f), r.topLeft, Size(r.width * 0.18f, r.height))
    drawRect(a.luz(mezcla(CRUDO, caja, 0.5f), k).copy(alpha = 0.3f), Offset(tapa.left, tapa.top), Size(tapa.width, 1.4f * u))
    // Los bornes con su capuchón.
    listOf(0.24f to LADRILLO, 0.76f to Color(0xFF1C1B1E)).forEach { (fx, col) ->
        val b = Offset(r.left + r.width * fx, tapa.top)
        drawRect(a.luz(LATA_OSC, k, br), Offset(b.x - w * 0.004f, b.y - w * 0.008f), Size(w * 0.008f, w * 0.008f))
        drawOval(a.luz(col, k, br), Offset(b.x - w * 0.009f, b.y - w * 0.014f), Size(w * 0.018f, w * 0.01f))
        drawOval(a.brillo(0.4f, maxOf(k, 0.3f), br), Offset(b.x - w * 0.006f, b.y - w * 0.013f), Size(w * 0.007f, w * 0.004f))
    }
    if (a.oscuro && k > 0.08f) drawRect(a.brillo(0.6f * k, k, br), Offset(r.right - 1.3f * u, r.top), Size(1.3f * u, r.height))
}

/** Los dos cables de la batería a la radio (rojo y negro), con sus pinzas de cocodrilo en los bornes. */
internal fun DrawScope.cablesRadioColonos(g: MarcoColonos, radio: Offset, rojo: Offset, negro: Offset) {
    val u = density; val w = g.w; val a = g.a
    val k = g.k(rojo); val br = g.br(rojo)
    listOf(rojo to LADRILLO, negro to Color(0xFF1C1B1E)).forEachIndexed { i, (p, col) ->
        val cable = Path().apply {
            moveTo(radio.x, radio.y + i * w * 0.006f)
            cubicTo(radio.x + w * 0.02f, radio.y + w * 0.03f + i * w * 0.01f, p.x - w * 0.03f, p.y - w * 0.03f, p.x, p.y - w * 0.006f)
        }
        drawPath(corridoColonos(cable, 1f * u, 1.4f * u), a.sombra(0.3f), style = Stroke(1.4f * u))
        drawPath(cable, a.luz(col, k, br), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        // La pinza: dos quijadas de lata.
        val pinza = poligonoColonos(p.x - w * 0.006f, p.y - w * 0.012f, p.x + w * 0.007f, p.y - w * 0.01f, p.x + w * 0.004f, p.y - w * 0.002f, p.x - w * 0.006f, p.y - w * 0.004f)
        drawPath(pinza, a.luz(LATA, k, br))
        drawLine(a.sombra(0.5f), Offset(p.x - w * 0.006f, p.y - w * 0.007f), Offset(p.x + w * 0.005f, p.y - w * 0.006f), 0.7f * u)
        drawLine(a.brillo(0.5f, maxOf(k, 0.3f), br), Offset(p.x - w * 0.005f, p.y - w * 0.011f), Offset(p.x + w * 0.005f, p.y - w * 0.0098f), 0.6f * u)
    }
}
