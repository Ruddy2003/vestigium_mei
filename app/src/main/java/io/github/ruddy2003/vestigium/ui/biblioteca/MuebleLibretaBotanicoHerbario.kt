package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import kotlin.math.PI
import kotlin.math.sin

/**
 * El herbario de momentos de la libreta de Botánico ([MuebleLibretaBotanico]), en vivo: la carpeta de cartón forrada de tela
 * verde, apoyada en el caballete, con el lomo y las puntas de piel, la etiqueta pegada (el nombre y la cuenta) y dos pares de
 * cintas de algodón anudadas en el canto; entre las tapas, los pliegos con sus secantes, que se ven por arriba y por la derecha y
 * engordan con la cuenta, y de los que asoman las puntas de lo prensado (frondas, espigas y hojas: más cuantos más pliegos). Al
 * abrirse, las cintas se sueltan y la tapa gira sobre el lomo: dentro, el pliego de arriba con su helecho montado.
 */
internal object HerbarioLibretaBotanico {
    private val P = PaletaBotanico
    /** La tela de las tapas: un verde botella apagado, de la familia del verde del gabinete. */
    val TELA_TAPA = Color(0xFF3F5B40)
    val CINTA = Color(0xFFE9DCBC)

    private fun tiempos(a: Float): Pair<Float, Float> {
        val x = a.coerceIn(0f, 1f)
        return (x / 0.3f).coerceIn(0f, 1f) to ((x - 0.3f) / 0.7f).coerceIn(0f, 1f)
    }

    fun DrawScope.herbario(e: EstadoLibreta, s: SitioLibretaBotanico, l: LuzLibretaBotanico, medidor: TextMeasurer) {
        val U = s.U
        val (suelta, abre) = tiempos(e.abierto)
        val f = s.tapa; val at = s.atras
        val t = l.t(f.center)
        // La sombra en la pared y en el caballete: de día, a la derecha y abajo; de noche, lejos del quinqué, a la izquierda.
        val silueta = poligonoBotanico(f.left, f.top, f.left + at.x, f.top + at.y, f.right + at.x, f.top + at.y, f.right + at.x, f.bottom + at.y, f.right, f.bottom, f.left, f.bottom)
        clipRect(0f, 0f, size.width, s.mesa + 2f * U) {
            if (!l.oscuro) translate(12f * U, 7f * U) { drawPath(silueta, t.s(0.2f)) }
            // De noche, la sombra del quinqué no apaga el paño de la luna.
            else clipPath(l.camino(l.pano, 2f * U), ClipOp.Difference) { translate(-16f * U, 5f * U) { drawPath(silueta, P.NOCHE.copy(alpha = 0.38f)) } }
        }
        rotate(s.ladeo, Offset(f.left, f.bottom)) { carpeta(e, s, l, medidor, suelta, abre) }
        // El pliego de herbario de pie delante, apoyado en la carpeta: al abrir, se aparta.
        if (suelta < 0.99f) translate(suelta * 46f * U, suelta * 10f * U) { pliegoDelante(e, s, l) }
    }

    /** La carpeta: la tapa de atrás, los cantos de los cartones y de los pliegos, lo que asoma, la tapa de delante, el lomo y las cintas. */
    private fun DrawScope.carpeta(e: EstadoLibreta, s: SitioLibretaBotanico, l: LuzLibretaBotanico, medidor: TextMeasurer, suelta: Float, abre: Float) {
        val U = s.U
        val f = s.tapa; val at = s.atras
        // La tapa de atrás, que asoma por arriba y por la derecha.
        val tras = Rect(f.left + at.x, f.top + at.y, f.right + at.x, f.bottom + at.y)
        drawRect(l.aLoAncho(tras.left, tras.right, tras.top) { it.c(TELA_TAPA, -0.22f) }, tras.topLeft, tras.size)
        carton(s, l, at, -0.1f)
        cantos(s, l, e)
        asoman(s, l, e)
        // Dentro (se ve al abrirse): el pliego de arriba con su helecho montado y su etiqueta de herbario.
        if (abre > 0.01f) pliegoDeArriba(s, l)
        // El canto del cartón de la tapa de delante, forrado de tela.
        if (abre < 0.05f) carton(s, l, Offset.Zero, 0.06f)
        // La tapa de delante, que gira sobre el lomo al abrirse.
        val sx = 1f - abre
        if (sx > 0.02f) scale(sx, 1f, Offset(f.left, f.center.y)) {
            tapa(e, s, l, medidor)
            if (abre > 0f) drawRect(Color.Black.copy(alpha = 0.35f * abre), f.topLeft, f.size)
        }
        if (abre > 0.01f && sx > 0.02f) {
            val borde = f.left + f.width * sx
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(borde, f.top), Size(14f * U * sin(PI.toFloat() * abre), f.height))
        }
        // El lomo de piel, que queda en su sitio.
        lomo(s, l)
        // Las cintas de algodón del canto, anudadas en lazo: al abrir se sueltan.
        val queda = 1f - suelta
        if (queda > 0.01f) cintas(s, l, queda, suelta)
    }

    /** El canto del cartón de una tapa ([d]: dónde está su cara, corrida del frente), arriba más claro y a la derecha más hondo. */
    private fun DrawScope.carton(s: SitioLibretaBotanico, l: LuzLibretaBotanico, d: Offset, f: Float) {
        val U = s.U; val u = density
        val r = s.tapa; val at = s.atras
        val g = 3f * U / at.getDistance().coerceAtLeast(1f)
        val b = Offset(at.x * g, at.y * g)
        val x0 = r.left + s.lomo * 0.5f + d.x; val y0 = r.top + d.y; val x1 = r.right + d.x; val y1 = r.bottom + d.y
        val tA = l.t(Offset(r.center.x, r.top)); val tL = l.t(Offset(r.right, r.center.y))
        drawPath(poligonoBotanico(x0, y0, x1, y0, x1 + b.x, y0 + b.y, x0 + b.x, y0 + b.y), tA.c(TELA_TAPA, 0.1f + f))
        drawPath(poligonoBotanico(x1, y0, x1 + b.x, y0 + b.y, x1 + b.x, y1 + b.y, x1, y1), tL.c(TELA_TAPA, -0.12f + f))
        drawLine(tA.b(0.4f), Offset(x0, y0 + b.y), Offset(x1 + b.x, y0 + b.y), 0.7f * u)
        if (l.oscuro) drawLine(P.LLAMA.copy(alpha = (0.6f * tL.k).coerceAtMost(0.45f)), Offset(x1 + b.x, y0 + b.y), Offset(x1 + b.x, y1 + b.y), 0.9f * u)
    }

    /** Los cantos de los pliegos entre las dos tapas, arriba y a la derecha: hojas de papel y secantes alternos, algo desparejos. */
    private fun DrawScope.cantos(s: SitioLibretaBotanico, l: LuzLibretaBotanico, e: EstadoLibreta) {
        val U = s.U; val u = density
        val f = s.tapa; val at = s.atras
        val izq = f.left + s.lomo * 0.6f
        val arriba = poligonoBotanico(izq, f.top + 1.5f * U, f.right - 1f * U, f.top + 1.5f * U, f.right - 1f * U + at.x, f.top + 1.5f * U + at.y, izq + at.x, f.top + 1.5f * U + at.y)
        val lado = poligonoBotanico(f.right - 1f * U, f.top + 1.5f * U, f.right - 1f * U + at.x, f.top + 1.5f * U + at.y, f.right - 1f * U + at.x, f.bottom - 1.5f * U + at.y, f.right - 1f * U, f.bottom - 1.5f * U)
        drawPath(arriba, l.aLoAncho(izq, f.right + at.x, f.top) { it.c(P.PAPEL, 0.04f) })
        drawPath(lado, l.aLoAlto(f.top, f.bottom, f.right) { it.c(P.PAPEL, -0.12f) })
        // Los cantos de cada hoja (cada pocas, un secante rosado), en el sentido del grueso.
        val n = (s.grueso / (1.7f * U)).toInt().coerceIn(3, 22)
        val tA = l.t(Offset(f.center.x, f.top)); val tL = l.t(Offset(f.right, f.center.y))
        val raya = tA.s(0.22f); val rayaL = tL.s(0.3f)
        val secA = tA.c(mezcla(P.SECANTE, P.CORAL, 0.3f), -0.04f); val secL = tL.c(mezcla(P.SECANTE, P.CORAL, 0.3f), -0.16f)
        for (i in 1 until n) {
            val k = i / n.toFloat()
            val d = Offset(at.x * k, at.y * k)
            val desp = (azar(e.semilla, 300 + i) - 0.5f) * 3f * U
            if (i % 3 == 1) {
                drawLine(secA, Offset(izq + d.x + desp, f.top + 1.5f * U + d.y), Offset(f.right - 1f * U + d.x, f.top + 1.5f * U + d.y), 1.2f * u)
                drawLine(secL, Offset(f.right - 1f * U + d.x, f.top + 1.5f * U + d.y), Offset(f.right - 1f * U + d.x, f.bottom - 1.5f * U + d.y), 1.2f * u)
            } else {
                drawLine(raya, Offset(izq + d.x + desp, f.top + 1.5f * U + d.y), Offset(f.right - 1f * U + d.x, f.top + 1.5f * U + d.y), 0.5f * u)
                drawLine(rayaL, Offset(f.right - 1f * U + d.x, f.top + 1.5f * U + d.y), Offset(f.right - 1f * U + d.x, f.bottom - 1.5f * U + d.y), 0.5f * u)
            }
        }
        // Un pliego más grande que asoma de lado, con su canto torcido.
        if (e.hojas > 6) {
            val k = 0.55f
            val d = Offset(at.x * k, at.y * k)
            drawPath(poligonoBotanico(f.right + d.x, f.top + 20f * U + d.y, f.right + d.x + 5f * U, f.top + 22f * U + d.y, f.right + d.x + 4f * U, f.top + 58f * U + d.y, f.right + d.x, f.top + 60f * U + d.y), tL.c(P.PAPEL, -0.06f))
        }
        // De noche, el canto de la derecha mira al quinqué.
        if (l.oscuro) drawPath(lado, P.LLAMA.copy(alpha = (0.4f * tL.k).coerceAtMost(0.32f)))
    }

    /** Lo prensado que asoma entre los pliegos, por arriba y por la derecha: frondas, espigas y hojas, más cuantos más pliegos. */
    private fun DrawScope.asoman(s: SitioLibretaBotanico, l: LuzLibretaBotanico, e: EstadoLibreta) {
        val U = s.U; val u = density
        val f = s.tapa; val at = s.atras
        val n = (1 + e.hojas / 10).coerceAtMost(8)
        for (j in 0 until n) {
            val k = (0.2f + 0.6f * azar(e.semilla, 320 + j))
            val d = Offset(at.x * k, at.y * k)
            val tipo = (j + (e.semilla and 3)) % 4
            if (j % 2 == 0) {
                // Por arriba.
                val x = f.left + s.lomo + (f.width - s.lomo - 20f * U) * ((j / 2 + 0.5f) / ((n + 1) / 2f)) + (azar(e.semilla, 330 + j) - 0.5f) * 14f * U
                val b = Offset(x + d.x, f.top + 6f * U + d.y)
                val t = l.t(b)
                when (tipo) {
                    0 -> frondaBotanico(b, (20f + 8f * azar(e.semilla, 340 + j)) * U, -100f + 30f * azar(e.semilla, 341 + j), t.c(P.HOJA_OSC, 0.02f), 1.2f * U, 9)
                    1 -> espiga(b, (18f + 6f * azar(e.semilla, 342 + j)) * U, -96f + 20f * azar(e.semilla, 343 + j), t)
                    2 -> frondaBotanico(b, (17f + 6f * azar(e.semilla, 344 + j)) * U, -80f - 20f * azar(e.semilla, 345 + j), t.c(mezcla(P.OCRE, P.TERRACOTA, 0.3f), -0.04f), 1.1f * U, 8)
                    else -> espiga(b, (15f + 5f * azar(e.semilla, 345 + j)) * U, -80f + 16f * azar(e.semilla, 346 + j), t)
                }
            } else {
                // Por la derecha.
                val y = f.top + 26f * U + (f.height - 60f * U) * ((j / 2 + 0.5f) / (n / 2f).coerceAtLeast(1f))
                val b = Offset(f.right - 4f * U + d.x, y + d.y)
                val t = l.t(b)
                when (tipo) {
                    0, 1 -> frondaBotanico(b, (16f + 6f * azar(e.semilla, 346 + j)) * U, -10f + 30f * azar(e.semilla, 347 + j), t.c(P.HOJA, -0.06f), 1.1f * U, 8)
                    2 -> hojaBotanico(b + Offset(7f * U, 0f), 13f * U, 70f + 30f * azar(e.semilla, 348 + j), t.c(mezcla(P.HOJA_CLARA, P.OCRE, 0.4f)), t)
                    else -> espiga(b, 16f * U, -20f + 30f * azar(e.semilla, 349 + j), t)
                }
            }
        }
    }

    /** Una espiga de gramínea: el tallo y sus granos en dos hileras. */
    private fun DrawScope.espiga(b: Offset, largo: Float, giro: Float, t: TonoBotanico) {
        val u = density
        rotate(giro + 90f, b) {
            val c = t.c(mezcla(P.PAJA, P.HOJA_CLARA, 0.3f))
            drawLine(c, b, Offset(b.x, b.y - largo), 0.9f * u)
            for (q in 0 until 6) {
                val y = b.y - largo * (0.35f + 0.11f * q)
                listOf(-1f, 1f).forEach { s0 -> drawOval(c, Offset(b.x + s0 * largo * 0.03f - largo * 0.04f + (if (s0 > 0) 0f else -largo * 0.02f), y - largo * 0.05f), Size(largo * 0.08f, largo * 0.11f)) }
            }
            drawLine(t.s(0.25f), Offset(b.x + 0.6f * u, b.y - largo * 0.3f), Offset(b.x + 0.6f * u, b.y - largo), 0.5f * u)
        }
    }

    /** La tapa de delante: la tela con la luz, las puntas de piel, el borde del cartón y la etiqueta con el nombre y la cuenta. */
    private fun DrawScope.tapa(e: EstadoLibreta, s: SitioLibretaBotanico, l: LuzLibretaBotanico, medidor: TextMeasurer) {
        val U = s.U; val u = density
        val f = s.tapa
        val t = l.t(f.center)
        // La tela: de día, más clara arriba a la izquierda (la ventana) y honda abajo a la derecha; de noche, encendida del lado
        // del quinqué.
        if (!l.oscuro) {
            val tl = l.t(f.topLeft)
            drawRect(Brush.linearGradient(listOf(tl.c(TELA_TAPA, 0.12f), t.c(TELA_TAPA, 0.02f), t.c(TELA_TAPA, -0.14f)), f.topLeft, f.bottomRight), f.topLeft, f.size)
        } else {
            drawRect(l.aLoAncho(f.left, f.right, f.center.y) { it.c(TELA_TAPA, 0.02f) }, f.topLeft, f.size)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, P.NOCHE.copy(alpha = 0.3f)), f.top, f.bottom), f.topLeft, f.size)
        }
        // La trama de la tela, apenas, y el gastado de los cantos.
        var y = f.top + 2f * U
        val trama = t.s(0.07f)
        while (y < f.bottom) { drawLine(trama, Offset(f.left, y), Offset(f.right, y), 0.5f * u); y += 2.4f * U }
        var xt = f.left + s.lomo + 1f * U
        val tramaV = t.b(0.05f)
        while (xt < f.right) { drawLine(tramaV, Offset(xt, f.top), Offset(xt, f.bottom), 0.5f * u); xt += 2.4f * U }
        // De día, el paño de la ventana también cae en la tapa (corrido, porque está más adelante que la pared): la misma tela,
        // un paso más clara, con su penumbra.
        if (!l.oscuro) clipRect(f.left, f.top, f.right, f.bottom) { pano(s, l, mezcla(mezcla(TELA_TAPA, LUZ_MUEBLE, 0.2f), P.OCASO, 0.06f)) }
        // El filete en seco del cartón: un recuadro hundido, con su luz debajo.
        val fi = Rect(f.left + s.lomo + 7f * U, f.top + 7f * U, f.right - 7f * U, f.bottom - 7f * U)
        drawRect(t.s(0.42f), fi.topLeft, fi.size, style = Stroke(1.1f * u))
        drawRect(t.b(0.22f), fi.topLeft + Offset(1f * u, 1f * u), fi.size, style = Stroke(0.8f * u))
        val fi2 = fi.deflate(3.2f * U)
        drawRect(t.s(0.3f), fi2.topLeft, fi2.size, style = Stroke(0.7f * u))
        drawRect(t.b(0.16f), fi2.topLeft + Offset(0.8f * u, 0.8f * u), fi2.size, style = Stroke(0.6f * u))
        drawRect(t.b(0.3f), f.topLeft, Size(f.width, 1.2f * u))
        drawRect(t.s(0.4f), Offset(f.left, f.bottom - 1.6f * u), Size(f.width, 1.6f * u))
        drawRect(t.s(0.3f), Offset(f.right - 1.6f * u, f.top), Size(1.6f * u, f.height))
        if (l.oscuro) drawRect(P.LLAMA.copy(alpha = (0.9f * l.k(Offset(f.right, f.center.y))).coerceAtMost(0.7f)), Offset(f.right - 1.4f * u, f.top), Size(1.4f * u, f.height))
        // Las puntas de piel, arriba y abajo a la derecha.
        val pu = 22f * U
        val tP = l.t(Offset(f.right, f.top))
        listOf(true, false).forEach { arriba ->
            val y0 = if (arriba) f.top else f.bottom
            val sg = if (arriba) 1f else -1f
            val tri = poligonoBotanico(f.right - pu, y0, f.right, y0, f.right, y0 + sg * pu)
            drawPath(tri, tP.c(P.CUERO, if (arriba) 0.04f else -0.06f))
            drawLine(tP.s(0.35f), Offset(f.right - pu, y0), Offset(f.right, y0 + sg * pu), 1.2f * u)
            drawLine(tP.b(0.3f), Offset(f.right - pu + 1.6f * U, y0), Offset(f.right, y0 + sg * (pu - 1.6f * U)), 0.6f * u)
            // Y junto al lomo, las de la izquierda (más chicas: la piel del lomo ya cubre el canto).
            val xl = f.left + s.lomo; val pl = pu * 0.72f
            drawPath(poligonoBotanico(xl, y0, xl + pl, y0, xl, y0 + sg * pl), tP.c(P.CUERO, if (arriba) 0.06f else -0.04f))
            drawLine(tP.s(0.35f), Offset(xl + pl, y0), Offset(xl, y0 + sg * pl), 1.1f * u)
            drawLine(tP.b(0.35f), Offset(xl + pl - 1.6f * U, y0), Offset(xl, y0 + sg * (pl - 1.6f * U)), 0.6f * u)
        }
        etiqueta(e, s, l, medidor)
    }

    /** La etiqueta de papel pegada en la tapa, con su doble filete: el nombre, una raya y la cuenta en dos renglones. */
    private fun DrawScope.etiqueta(e: EstadoLibreta, s: SitioLibretaBotanico, l: LuzLibretaBotanico, medidor: TextMeasurer) {
        val U = s.U; val u = density
        val r = s.etiqueta
        val t = l.t(r.center)
        val papel = t.etiqueta(P.PAPEL)
        val tinta = t.tinta()
        drawRect(t.s(0.4f), r.topLeft + Offset(1.2f * u, 1.8f * u), r.size)
        if (l.oscuro) drawRect(l.aLoAncho(r.left, r.right, r.center.y) { it.etiqueta(P.PAPEL) }, r.topLeft, r.size)
        else {
            drawRect(papel, r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { pano(s, l, mezcla(mezcla(P.PAPEL, LUZ_MUEBLE, 0.5f), P.OCASO, 0.05f)) }
        }
        drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.9f * u))
        drawRect(tinta.copy(alpha = 0.55f), r.deflate(3f * U).topLeft, r.deflate(3f * U).size, style = Stroke(0.8f * u))
        drawRect(tinta.copy(alpha = 0.35f), r.deflate(4.8f * U).topLeft, r.deflate(4.8f * U).size, style = Stroke(0.5f * u))
        textoEn(medidor, e.nombre, Rect(r.left + 8f * U, r.top + 6f * U, r.right - 8f * U, r.top + 23f * U), P.LETRA, tinta, 13.5f, peso = FontWeight.Bold, minimo = 8f)
        drawLine(tinta.copy(alpha = 0.45f), Offset(r.center.x - r.width * 0.2f, r.top + 25.5f * U), Offset(r.center.x + r.width * 0.2f, r.top + 25.5f * U), 0.7f * u)
        val partes = e.cuenta.split(" · ", limit = 2)
        textoEn(medidor, partes[0], Rect(r.left + 8f * U, r.top + 27f * U, r.right - 8f * U, r.top + 39f * U), P.LETRA, tinta, 10.5f, peso = FontWeight.SemiBold, minimo = 6.5f)
        if (partes.size > 1) textoEn(medidor, partes[1], Rect(r.left + 8f * U, r.top + 39f * U, r.right - 8f * U, r.bottom - 4f * U), P.LETRA, tinta.copy(alpha = 0.85f), 9.5f, minimo = 6f)
    }

    /** El paño de la ventana en la tapa (de día), del color [luz], con su penumbra en cuatro pasos. */
    private fun DrawScope.pano(s: SitioLibretaBotanico, l: LuzLibretaBotanico, luz: Color) {
        val U = s.U
        val q = l.panoCorrido(-16f * U, -6f * U)
        drawPath(l.camino(q, 5f * U), luz, alpha = 0.28f)
        drawPath(l.camino(q, 2.5f * U), luz, alpha = 0.42f)
        drawPath(l.camino(q), luz, alpha = 0.6f)
        drawPath(l.camino(q, -2.5f * U), luz)
    }

    /** El lomo de piel a la izquierda: envuelve el canto de la tapa, con sus dos filetes en seco y su luz. */
    private fun DrawScope.lomo(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U; val u = density
        val f = s.tapa
        val r = Rect(f.left - 1.5f * U, f.top - 0.5f * U, f.left + s.lomo, f.bottom + 0.5f * U)
        val t = l.t(r.center)
        // La piel, redonda sobre el canto del cartón: clara hacia la ventana y honda donde dobla hacia la tela.
        val piel = if (l.oscuro) t.c(P.CUERO, -0.04f) else t.c(P.CUERO, 0.04f)
        drawRect(Brush.horizontalGradient(listOf(mezcla(piel, LUZ_MUEBLE, if (l.oscuro) 0f else 0.16f), piel, mezcla(piel, P.CUERO_OSC, 0.55f)), r.left, r.right), r.topLeft, r.size)
        drawLine(t.s(0.45f), Offset(r.right, r.top), Offset(r.right, r.bottom), 1f * u)
        drawLine(t.c(P.CUERO_OSC).copy(alpha = 0.55f), Offset(r.right - 5f * U, r.top + 3f * U), Offset(r.right - 5f * U, r.bottom - 3f * U), 0.7f * u)
        drawLine(t.b(0.18f), Offset(r.right - 4.2f * U, r.top + 3f * U), Offset(r.right - 4.2f * U, r.bottom - 3f * U), 0.5f * u)
        // El brillo de la piel, a lo largo, del lado de la luz.
        drawRect(if (l.oscuro) P.LUNA_RAS.copy(alpha = 0.16f) else LUZ_MUEBLE.copy(alpha = 0.32f), Offset(r.left + 3.4f * U, r.top + 2f * U), Size(1.6f * U, r.height - 4f * U))
        // Los nervios: cinco bandas en relieve, cada una con su luz arriba y su sombra abajo.
        for (k in 0 until 5) {
            val y = r.top + r.height * (0.12f + 0.19f * k)
            drawRect(t.c(P.CUERO, 0.06f), Offset(r.left, y - 1.8f * U), Size(r.width, 3.6f * U))
            drawRect(t.b(0.4f), Offset(r.left, y - 1.8f * U), Size(r.width, 0.9f * u))
            drawRect(t.s(0.45f), Offset(r.left, y + 1.8f * U - 0.9f * u), Size(r.width, 1.2f * u))
        }
        // El roce de la piel en las esquinas.
        drawRect(t.c(mezcla(P.CUERO, P.KRAFT, 0.4f)), Offset(r.left, r.top), Size(r.width, 2f * U))
        drawRect(t.c(mezcla(P.CUERO, P.KRAFT, 0.3f), -0.1f), Offset(r.left, r.bottom - 2f * U), Size(r.width, 2f * U))
        if (l.oscuro) drawLine(P.LUNA_RAS.copy(alpha = 0.5f), Offset(r.left + 0.6f * u, r.top), Offset(r.left + 0.6f * u, r.bottom), 1.1f * u)
    }

    /**
     * Las dos cintas de algodón del canto: la de delante sale de bajo la tela, la de atrás asoma por el canto de los pliegos, y se
     * anudan en lazo sobre el canto, con sus cabos que cuelgan. Al abrir ([suelta]), el lazo se deshace y caen.
     */
    private fun DrawScope.cintas(s: SitioLibretaBotanico, l: LuzLibretaBotanico, queda: Float, suelta: Float) {
        val U = s.U; val u = density
        val f = s.tapa; val at = s.atras
        listOf(s.cinta1, s.cinta2).forEach { y0 ->
            val y = y0 + suelta * 8f * U
            val nudo = Offset(f.right + at.x * 0.35f, y + at.y * 0.35f)
            val t = l.t(nudo)
            val c = t.c(CINTA).copy(alpha = queda)
            val som = t.s(0.3f * queda)
            // La de delante, por la tela, con su sombra.
            drawLine(som, Offset(f.right - 26f * U, y + 1.6f * U), Offset(nudo.x, nudo.y + 1.6f * U), 3.4f * U)
            drawLine(c, Offset(f.right - 26f * U, y), nudo, 3.2f * U)
            drawLine(t.s(0.2f * queda), Offset(f.right - 26f * U, y + 1.4f * U), nudo + Offset(0f, 1.4f * U), 0.6f * u)
            // La de atrás, que asoma por el canto.
            drawLine(t.c(CINTA, -0.12f).copy(alpha = queda), Offset(f.right + at.x - 1f * U, y + at.y), nudo, 3f * U)
            // El lazo: dos orejas y dos cabos.
            listOf(-1f, 1f).forEach { sg ->
                val oreja = Path().apply {
                    moveTo(nudo.x, nudo.y)
                    quadraticTo(nudo.x + sg * 10f * U, nudo.y - 9f * U, nudo.x + sg * 11f * U, nudo.y - 1f * U)
                    quadraticTo(nudo.x + sg * 7f * U, nudo.y + 2f * U, nudo.x, nudo.y)
                }
                drawPath(oreja, som, style = Stroke(2.6f * U))
                drawPath(oreja, c, style = Stroke(2.2f * U))
                val cabo = Offset(nudo.x + sg * 5f * U, nudo.y + 15f * U + suelta * 6f * U)
                drawLine(c, nudo, cabo, 2.4f * U, StrokeCap.Round)
                drawLine(t.s(0.25f * queda), nudo + Offset(sg * 1f * U, 1f * U), cabo + Offset(sg * 1f * U, 0f), 0.6f * u)
            }
            drawCircle(t.c(mezcla(CINTA, P.KRAFT, 0.3f)).copy(alpha = queda), 2.6f * U, nudo)
            if (l.oscuro) drawCircle(P.LLAMA.copy(alpha = (0.5f * t.k * queda).coerceAtMost(0.4f)), 1.4f * U, nudo + Offset(1.2f * U, -1f * U))
        }
    }

    /**
     * El pliego de herbario de pie delante de la carpeta, apoyado en ella: el papel crema algo combado, una fronda prensada
     * montada con sus tres tiras engomadas, la etiquetita sin letra abajo a la derecha y la esquina de arriba doblada. Echa su
     * sombra en la tapa (de día, a la derecha; de noche, lejos del quinqué).
     */
    private fun DrawScope.pliegoDelante(e: EstadoLibreta, s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U; val u = density
        val q = s.pliego
        val c = Offset((q[0] + q[4]) / 2, (q[1] + q[5]) / 2)
        val t = l.t(c)
        val hoja = poligonoBotanico(q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7])
        val dx = if (l.oscuro) -8f * U else 8f * U
        translate(dx, 3f * U) { drawPath(hoja, t.s(if (l.oscuro) 0.45f else 0.3f)) }
        drawPath(hoja, if (l.oscuro) l.aLoAncho(q[0], q[2], c.y) { it.etiqueta(P.PAPEL) } else Brush.linearGradient(listOf(t.c(P.PAPEL, 0.08f), t.c(P.PAPEL, -0.06f)), Offset(q[6], q[7]), Offset(q[2], q[3])))
        // El combado del papel: una sombra suave a lo largo y la luz en el canto de arriba.
        drawPath(poligonoBotanico(q[2] - 12f * U, q[3], q[2], q[3], q[4], q[5], q[4] - 12f * U, q[5]), t.s(0.12f))
        drawLine(t.b(0.5f), Offset(q[6], q[7]), Offset(q[4] - 8f * U, q[5]), 0.9f * u)
        if (l.oscuro) drawLine(P.LLAMA.copy(alpha = (0.8f * t.k).coerceAtMost(0.6f)), Offset(q[2], q[3]), Offset(q[4], q[5]), 1f * u)
        // La fronda montada: el raquis desde abajo, algo curvo, y sus pinnas; y una segunda más chica.
        val base = Offset(q[0] + 20f * U, q[1] - 7f * U)
        frondaBotanico(base + Offset(-2f * U, 0f), 30f * U, -116f, t.c(mezcla(P.HOJA, P.HOJA_OSC, 0.4f)), 1.4f * U, 9)
        frondaBotanico(base, 60f * U, -85f, t.c(P.HOJA, 0.02f), 1.9f * U, 14)
        frondaBotanico(base + Offset(3f * U, 0f), 40f * U, -58f, t.c(P.HOJA_CLARA, 0.04f), 1.5f * U, 10)
        // Las tiras engomadas, de papel kraft, que la sujetan.
        listOf(Triple(0.2f, -12f, 0f), Triple(0.56f, 10f, -1f), Triple(0.14f, 30f, 7f)).forEach { (k, giro, ox) ->
            val p = Offset(base.x + ox * U + 1f * U, base.y - 60f * U * k)
            rotate(giro, p) {
                drawRect(t.s(0.25f), Offset(p.x - 6.4f * U, p.y - 1.4f * U), Size(12.8f * U, 3.8f * U))
                drawRect(t.c(P.KRAFT, 0.04f), Offset(p.x - 6.4f * U, p.y - 1.9f * U), Size(12.8f * U, 3.8f * U))
            }
        }
        // La etiquetita de herbario, sin letra, abajo a la derecha.
        val et = Rect(q[2] - 22f * U, q[3] - 15f * U, q[2] - 4f * U, q[3] - 5f * U)
        drawRect(t.c(P.PAPEL, 0.06f), et.topLeft, et.size)
        drawRect(t.tinta().copy(alpha = 0.55f), et.topLeft, et.size, style = Stroke(0.7f * u))
        listOf(3f, 6f).forEach { y -> drawLine(t.tinta().copy(alpha = 0.4f), Offset(et.left + 2.5f * U, et.top + y * U), Offset(et.right - (2.5f + y) * U, et.top + y * U), 0.6f * u) }
        // La esquina de arriba a la derecha, doblada.
        val d = 9f * U
        drawPath(poligonoBotanico(q[4] - d, q[5], q[4], q[5], q[4] - 0.4f * U, q[5] + d), t.s(0.4f))
        drawPath(poligonoBotanico(q[4] - d, q[5], q[4] - 0.4f * U, q[5] + d, q[4] - d * 0.8f, q[5] + d * 0.8f), t.c(P.PAPEL, -0.14f))
        if (e.hojas > 30) {
            // Con muchos pliegos, otro detrás, un poco más alto y corrido, del que solo asoma el canto.
            drawLine(t.s(0.2f), Offset(q[6] + 6f * U, q[7] - 1f * U), Offset(q[4] + 6f * U, q[5] - 1f * U), 0.8f * u)
        }
    }

    /** El pliego de arriba, al abrir: el papel, el helecho montado con sus tiras engomadas y la etiqueta del herbario. */
    private fun DrawScope.pliegoDeArriba(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U; val u = density
        val f = s.tapa
        val r = Rect(f.left + 2f * U, f.top + 1.5f * U, f.right - 1f * U, f.bottom - 1.5f * U)
        val t = l.t(r.center)
        drawRect(t.etiqueta(P.PAPEL), r.topLeft, r.size)
        val b = Offset(r.left + r.width * 0.36f, r.bottom - 12f * U)
        frondaBotanico(b, r.height * 0.78f, -84f, t.c(P.HOJA), 1.6f * U, 13)
        frondaBotanico(b + Offset(6f * U, 0f), r.height * 0.6f, -64f, t.c(P.HOJA_CLARA), 1.4f * U, 11)
        listOf(0.25f, 0.55f).forEach { k ->
            val p = Offset(b.x + 4f * U, b.y - r.height * 0.78f * k)
            rotate(-14f, p) { drawRect(t.c(P.KRAFT).copy(alpha = 0.85f), Offset(p.x - 8f * U, p.y - 2.2f * U), Size(16f * U, 4.4f * U)) }
        }
        val et = Rect(r.right - r.width * 0.36f, r.bottom - r.height * 0.3f, r.right - 8f * U, r.bottom - 8f * U)
        drawRect(t.tinta().copy(alpha = 0.6f), et.topLeft, et.size, style = Stroke(0.8f * u))
        for (k in 0 until 3) drawLine(t.tinta().copy(alpha = 0.4f), Offset(et.left + 5f * U, et.top + (7 + k * 8) * U), Offset(et.right - (5 + k * 9) * U, et.top + (7 + k * 8) * U), 0.7f * u)
    }
}
