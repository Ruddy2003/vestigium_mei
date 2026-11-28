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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Las entradas del estante de Abismo ([MuebleEstanteAbismo]), tres cosas del armario del patrón según la semilla: el cuaderno de
 * bitácora (de pie, lomo afuera: la tela del color del ánimo, las cabezas de cuero con sus nervios, las cantoneras de latón, el
 * cerco blanco de la sal, la etiqueta de papel con el día y, por arriba, las hojas sueltas que guarda), el petate de lona (blando y
 * panzudo, de lona teñida, con la boca fruncida por el cabo que pasa por los ollaos, su costura y la chapa del día colgada del
 * cabo) y los rollos de cartas (dos o tres cartas náuticas enrolladas en su funda de hule, atadas con piola, con el papel en
 * espiral que asoma arriba a distintas alturas). El ánimo es la tela, la lona y el hule, en todo el cuerpo ([tonoDeDiaAbismo]);
 * sin ánimo, la lona cruda o el hule pardo. En los Sueños, una media luna de plata con su disco apagado.
 */
internal object VolumenEstanteAbismo {
    private val P = PaletaAbismo

    /** La lona cruda, el hule pardo y la tela de vela de lo que no tiene ánimo. */
    private val LONA_CRUDA = Color(0xFFB8A882)
    private val HULE_PARDO = Color(0xFF8A6E48)
    private val TELA_CRUDA = Color(0xFFA89A7A)

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.38f, 0.75f, 0.42f, 0.08f, -0.22f, -0.5f, -0.76f)

    /** La luz que le toca a una entrada: la del momento ([k]) y, en los Sueños oscuros, la luna del ojo de buey ([luna]). */
    private class Luz(val a: AireAbismo, val k: Float, val luna: Float) {
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
        fun laton(col: Color = P.LATON): Color = if (a.oscuro) mezcla(a.luz(col, k), Color(0xFFFFD98A), 0.35f * k) else c(col)
    }

    /** Los colores de un tinte con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color, val base: Color)

    private fun tinte(t: Color?, l: Luz, crudo: Color, brillo: Float = 0.3f): Tinte {
        val m = t ?: crudo
        val claro = mezcla(m, luzMueble(1f), if (t == null) 0.2f else brillo)
        val hondo = mezcla(m, sombraMueble(1f), 0.55f)
        return if (t != null) Tinte(l.animo(m), l.animo(claro), l.animo(hondo), m) else Tinte(l.c(m), l.c(claro), l.c(hondo), m)
    }

    /** Un cuerpo redondo en [r] por franjas opacas (sin degradados): el filo, el claro, la cara, el hondo, hacia [hacia]. */
    private fun DrawScope.redondo(r: Rect, t: Tinte, hacia: Float) {
        for (i in 0 until 7) {
            val f0 = FRANJAS[i]; val f1 = FRANJAS[i + 1]
            val m = TONOS[i]
            val c = if (m >= 0f) mezcla(t.cara, t.claro, m) else mezcla(t.cara, t.hondo, -m)
            val mas = if (i < 6) 1.2f else 0f
            val x0 = if (hacia < 0f) r.left + r.width * f0 else r.right - r.width * f1 - mas
            drawRect(c, Offset(x0, r.top), Size(r.width * (f1 - f0) + mas, r.height))
        }
    }

    /** El mes corto en español: «▾ NOVIEMBRE» da «nov.». */
    private fun mesCorto(mes: String): String {
        val m = mes.replace("▾", "").trim().substringBefore(' ').lowercase()
        return if (m.length > 4) m.take(3) + "." else m
    }

    /** El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp) y el mes debajo, en una de 9.5 dp. */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left - 1f * u, y0, r.right + 1f * u, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.Bold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left - 1f * u, y0 + hd - 1.6f * u, r.right + 1f * u, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.88f), MES, peso = FontWeight.SemiBold, minimo = 5f)
    }

    /** La tinta del día: oscura siempre, para que se lea en el papel. */
    private fun tinta(a: AireAbismo, k: Float) = if (a.oscuro) Color(0xFF0E1214) else a.luz(P.TINTA, maxOf(k, 0.75f))

    // ---------------------------------------------------------------- lo compartido

    private var radioLuna = -1f
    private var caminoLuna = Path()

    private fun creciente(r: Float): Path {
        if (r != radioLuna) {
            val fuera = Path().apply { addOval(Rect(-r, -r, r, r)) }
            val dentro = Path().apply { addOval(Rect(-r * 0.8f + r * 0.48f, -r * 0.86f - r * 0.2f, r * 0.8f + r * 0.48f, r * 0.86f - r * 0.2f)) }
            caminoLuna = Path().apply { op(fuera, dentro, PathOperation.Difference) }
            radioLuna = r
        }
        return caminoLuna
    }

    /** La luna de los Sueños: el disco apagado (la luz cenicienta) y la media luna de plata encima, con su canto y su sombra. */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireAbismo) {
        val u = density
        val m = creciente(r)
        drawCircle(Color(0xFF2E4252).copy(alpha = 0.6f), r, c)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, a.sombra(0.5f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFDDE6F0))
            drawPath(m, Color(0xFF26343E), style = Stroke(0.8f * u))
        }
        drawCircle(Color.White.copy(alpha = 0.85f), r * 0.14f, Offset(c.x - r * 0.6f, c.y - r * 0.16f))
    }

    /** Lo de las que están de pie: la sombra que dejan en el fondo por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireAbismo, hacia: Float, desde: Float, ancho: Float = size.width) {
        val u = density
        val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.42f), Offset(ancho, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.42f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.4f * u), Size(ancho + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche) y el roce frío de la luna. */
    private fun DrawScope.filos(cuerpo: Rect, a: AireAbismo, k: Float, hacia: Float, luna: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.7f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (luna > 0f) drawRect(a.lunar(0.6f * luna), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    /** La etiqueta de papel tostado en [et] con el día y el mes: su sombra, el filete de tinta y el borde gastado. */
    private fun DrawScope.etiquetaDia(v: Volumen, medidor: TextMeasurer, et: Rect, l: Luz, giro: Float = 0f) {
        val u = density
        val a = l.a
        // De noche, lejos de toda luz, el papel se hunde y el día se lee en claro; cerca de la llama, ámbar con la tinta oscura.
        val hundida = a.oscuro && l.k + l.luna * 0.6f < 0.3f
        val fondo = if (hundida) mezcla(Color(0xFF263230), Color(0xFF3E4038), ((l.k + l.luna * 0.6f) / 0.3f).coerceIn(0f, 1f)) else l.etiqueta(P.ETIQUETA)
        val borde = if (hundida) Color(0xFF1A2224) else l.etiqueta(P.ETIQUETA_OSC)
        val letra = if (hundida) Color(0xFFD6CCB0) else tinta(a, l.k)
        rotate(giro, et.center) {
            drawRect(a.sombra(0.4f), Offset(et.left + 1f * u, et.top + 1.3f * u), et.size)
            drawRect(fondo, et.topLeft, et.size)
            drawRect(borde.copy(alpha = 0.45f), Offset(et.left, et.bottom - et.height * 0.12f), Size(et.width, et.height * 0.12f))
            drawRect(borde.copy(alpha = 0.3f), Offset(et.right - et.width * 0.1f, et.top), Size(et.width * 0.1f, et.height))
            val d = Rect(et.left + 1.5f * u, et.top + 1.5f * u, et.right - 1.5f * u, et.bottom - 1.5f * u)
            drawRect((if (hundida) letra else a.luz(P.TINTA, maxOf(l.k, 0.5f))).copy(alpha = if (hundida) 0.3f else if (a.oscuro) 0.5f else 0.55f), d.topLeft, d.size, style = Stroke(0.6f * u))
            diaYMes(v, medidor, Rect(et.left + 1.8f * u, et.top + 1.8f * u, et.right - 1.8f * u, et.bottom - 1.8f * u), letra)
        }
    }

    // ---------------------------------------------------------------- el cuaderno de bitácora

    fun DrawScope.cuaderno(v: Volumen, medidor: TextMeasurer, a: AireAbismo, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        // Las hojas sueltas y las cartas que asoman por la cabeza: más cuantas más palabras.
        val asoman = h * (0.04f + 0.06f * v.lleno)
        val ceja = asoman + 2f * u
        alrededor(a, hacia, ceja)
        val cuantas = 1 + (v.lleno * 3f).toInt()
        for (i in 0 until cuantas) {
            val x0 = (w * (0.08f + 0.6f * azar(s, 30 + i))).coerceAtLeast(0f)
            val tw = minOf(w * (0.35f + 0.25f * azar(s, 40 + i)), w - x0)
            val y0 = ceja - asoman * (0.5f + 0.5f * azar(s, 50 + i))
            val col = listOf(P.PAPEL, mezcla(P.PAPEL, P.MAR, 0.15f), mezcla(P.PAPEL, P.ESPARTO, 0.3f))[i % 3]
            rotate((azar(s, 60 + i) - 0.5f) * 8f, Offset(x0 + tw / 2, ceja)) {
                drawRect(l.c(col), Offset(x0, y0), Size(tw, ceja - y0 + 2f * u))
                drawRect(a.sombra(0.18f), Offset(x0, y0), Size(1f * u, ceja - y0))
                if (i % 3 == 1) drawLine(l.c(P.AZUL_CARTA), Offset(x0 + 1.5f * u, y0 + 2f * u), Offset(x0 + tw - 1.5f * u, y0 + 3f * u), 0.8f * u)
            }
        }
        val t = tinte(ti, l, TELA_CRUDA)
        // Los cantos de las tapas, más hondos, y el lomo redondo de tela.
        drawRoundRect(t.hondo, Offset(0f, ceja - 2f * u), Size(w, h - ceja + 2f * u), CornerRadius(1.6f * u))
        val lomo = Rect(0.8f * u, ceja, w - 0.8f * u, h - 1.6f * u)
        redondo(lomo, t, hacia)
        // La trama de la tela.
        clipRect(lomo.left, lomo.top, lomo.right, lomo.bottom) {
            val trama = Path()
            var y = lomo.top + 1f * u
            var j = 0
            while (y < lomo.bottom && j < 200) { trama.moveTo(lomo.left, y); trama.lineTo(lomo.right, y); y += 2.2f * u; j++ }
            drawPath(trama, t.hondo.copy(alpha = 0.15f), style = Stroke(0.5f * u))
        }
        // Las cabezas de cuero, arriba y abajo, con sus nervios.
        val cab = h * (0.1f + 0.03f * azar(s, 6))
        val cuero = Tinte(l.c(P.CUERO), l.c(mezcla(P.CUERO, P.TECA_LUZ, 0.45f)), l.c(mezcla(P.CUERO, Color.Black, 0.4f)), P.CUERO)
        redondo(Rect(lomo.left, lomo.top, lomo.right, lomo.top + cab), cuero, hacia)
        redondo(Rect(lomo.left, lomo.bottom - cab * 0.85f, lomo.right, lomo.bottom), cuero, hacia)
        listOf(lomo.top + cab, lomo.bottom - cab * 0.85f).forEach { y ->
            drawRect(a.sombra(0.45f), Offset(lomo.left, y), Size(lomo.width, 1.4f * u))
            drawRect(cuero.claro, Offset(lomo.left, y - 1.2f * u), Size(lomo.width, 1.2f * u))
        }
        listOf(lomo.top + cab * 0.45f, lomo.bottom - cab * 0.42f).forEach { y ->
            drawRect(cuero.hondo, Offset(lomo.left, y + 0.8f * u), Size(lomo.width, 1.6f * u))
            drawRect(cuero.claro, Offset(lomo.left, y - 0.8f * u), Size(lomo.width, 1.4f * u))
        }
        // Las cantoneras de latón de las tapas, que asoman en las cuatro esquinas.
        val cn = minOf(w * 0.18f, 5f * u)
        val laton = l.laton()
        listOf(Offset(0f, ceja - 2f * u) to 1f, Offset(w, ceja - 2f * u) to -1f).forEach { (p, sx) ->
            drawPath(poligonoAbismo(p.x, p.y, p.x + sx * cn, p.y, p.x, p.y + cn), laton)
        }
        listOf(Offset(0f, h) to 1f, Offset(w, h) to -1f).forEach { (p, sx) ->
            drawPath(poligonoAbismo(p.x, p.y, p.x + sx * cn, p.y, p.x, p.y - cn), laton)
        }
        // La etiqueta, más arriba o más abajo según la bitácora.
        val etH = 30f * u
        val et0 = lomo.top + cab + 4f * u + (lomo.height - cab * 2 - etH - 14f * u) * (0.05f + 0.25f * azar(s, 7))
        val et = Rect(lomo.left + 2f * u, et0, lomo.right - 2f * u, et0 + etH)
        etiquetaDia(v, medidor, et, l)
        // Debajo, la marca de la sal donde se mojó: una línea de agua quebrada de lado a lado y el tinte algo comido por debajo (o,
        // en los Sueños, la luna).
        val y1 = lomo.bottom - cab * 0.85f
        if (v.noche) luna(Offset(lomo.center.x, (et.bottom + y1) / 2), minOf(lomo.width * 0.28f, (y1 - et.bottom) * 0.3f, 7.5f * u), a)
        else if (azar(s, 8) > 0.35f) {
            val ys = et.bottom + (y1 - et.bottom) * (0.55f + 0.25f * azar(s, 10))
            val sal = Path().apply {
                moveTo(lomo.left, ys)
                var x = lomo.left; var j = 0
                while (x < lomo.right && j < 12) {
                    val paso = lomo.width * (0.14f + 0.08f * azar(s, 80 + j))
                    lineTo(minOf(lomo.right, x + paso), ys + (azar(s, 90 + j) - 0.5f) * 3.2f * u)
                    x += paso; j++
                }
            }
            clipRect(lomo.left, ys, lomo.right, y1) { drawRect(l.c(Color(0xFFF2EEE2)).copy(alpha = 0.12f), Offset(lomo.left, ys), Size(lomo.width, y1 - ys)) }
            drawPath(sal, l.c(Color(0xFFF2EEE2)).copy(alpha = 0.6f), style = Stroke(1f * u))
        }
        // El registro de piola que cuelga por el pie en algunas, y el roce de la tela en la cabeza.
        if (azar(s, 70) > 0.45f) {
            val xx = lomo.left + lomo.width * (0.35f + 0.3f * azar(s, 71))
            drawLine(l.c(P.MANILA), Offset(xx, h - 2.4f * u), Offset(xx + (azar(s, 72) - 0.5f) * 3f * u, h + 3f * u), 1.6f * u)
        }
        filos(lomo, a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- el petate de lona

    fun DrawScope.petate(v: Volumen, medidor: TextMeasurer, a: AireAbismo, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val t = tinte(ti, l, LONA_CRUDA, 0.26f)
        val panza = 0.02f + 0.04f * azar(s, 3)
        val cuello = h * (0.17f + 0.04f * azar(s, 4))
        val boca = h * 0.03f
        val cx = w * (0.5f + 0.03f * (azar(s, 5) - 0.5f))
        // La silueta: blanda, más ancha abajo (asienta en la tabla), estrecha en el cuello y fruncida arriba.
        val cuerpo = Path().apply {
            moveTo(cx - w * 0.28f, cuello)
            cubicTo(cx - w * (0.48f + panza), cuello + h * 0.12f, -w * panza, h * 0.55f, 0f, h * 0.84f)
            cubicTo(w * 0.01f, h * 0.97f, w * 0.15f, h, w * 0.3f, h)
            lineTo(w * 0.72f, h)
            cubicTo(w * 0.87f, h, w * 0.99f, h * 0.97f, w, h * 0.84f)
            cubicTo(w * (1f + panza), h * 0.55f, cx + w * (0.48f + panza), cuello + h * 0.12f, cx + w * 0.28f, cuello)
            close()
        }
        // La sombra en el fondo y en la tabla.
        val dx = if (hacia < 0f) 2.5f * u else -2.5f * u
        drawPath(corridoAbismo(cuerpo, dx, 1.5f * u), a.sombra(0.35f))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.6f * u), Size(w + 2f * u, 2.4f * u))
        drawPath(cuerpo, t.cara)
        clipPath(cuerpo) {
            redondo(Rect(0f, cuello, w, h), t, hacia)
            // La trama de la lona y la costura de un lado, con sus puntadas.
            val trama = Path()
            var y = cuello + 1.5f * u
            var j = 0
            while (y < h && j < 200) { trama.moveTo(0f, y); trama.lineTo(w, y + 1f * u); y += 2.4f * u; j++ }
            drawPath(trama, t.hondo.copy(alpha = 0.13f), style = Stroke(0.5f * u))
            val xc = if (azar(s, 11) > 0.5f) w * 0.22f else w * 0.78f
            drawLine(t.hondo.copy(alpha = 0.7f), Offset(xc, cuello + h * 0.06f), Offset(xc + (w * 0.5f - xc) * 0.08f, h * 0.98f), 1.2f * u)
            var yy = cuello + h * 0.08f
            while (yy < h * 0.96f) { drawLine(t.claro.copy(alpha = 0.6f), Offset(xc - 1.4f * u, yy), Offset(xc + 1.4f * u, yy + 1f * u), 0.7f * u); yy += 4f * u }
            // Los pliegues blandos: los que bajan del cuello y el que hace al asentarse.
            val pl = Path()
            for (i in 0 until 3) {
                val x0 = cx + w * (-0.18f + 0.18f * i)
                pl.moveTo(x0, cuello + h * 0.02f)
                pl.quadraticTo(x0 + w * (0.06f - 0.06f * i), cuello + h * 0.14f, x0 + w * (0.12f - 0.12f * i), cuello + h * (0.22f + 0.06f * azar(s, 12 + i)))
            }
            pl.moveTo(w * 0.12f, h * 0.9f); pl.quadraticTo(w * 0.5f, h * 0.86f, w * 0.88f, h * 0.91f)
            drawPath(pl, t.hondo.copy(alpha = 0.55f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            translate(-1.2f * u, -1f * u) { drawPath(pl, t.claro.copy(alpha = 0.45f), style = Stroke(0.9f * u, cap = StrokeCap.Round)) }
            // Un remiendo en algunos.
            if (azar(s, 15) > 0.6f) {
                val rp = Rect(w * 0.14f, h * 0.72f, w * 0.44f, h * 0.84f)
                drawRect(t.hondo.copy(alpha = 0.5f), rp.topLeft, rp.size)
                drawRect(t.claro.copy(alpha = 0.6f), rp.topLeft, rp.size, style = Stroke(0.7f * u))
            }
        }
        // La boca fruncida: los bullones de la lona sobre el cuello, con su luz y su sombra.
        val bocaP = Path().apply {
            moveTo(cx - w * 0.28f, cuello + 1f * u)
            cubicTo(cx - w * 0.42f, cuello - h * 0.03f, cx - w * 0.36f, boca, cx - w * 0.16f, boca + h * 0.02f)
            quadraticTo(cx - w * 0.04f, boca - h * 0.015f, cx + w * 0.06f, boca + h * 0.015f)
            quadraticTo(cx + w * 0.2f, boca - h * 0.01f, cx + w * 0.34f, boca + h * 0.035f)
            cubicTo(cx + w * 0.44f, cuello - h * 0.04f, cx + w * 0.38f, cuello, cx + w * 0.28f, cuello + 1f * u)
            close()
        }
        drawPath(bocaP, t.claro)
        clipPath(bocaP) {
            for (i in 0 until 5) {
                val x = cx - w * 0.3f + w * 0.15f * i
                drawLine(t.hondo.copy(alpha = 0.7f), Offset(x, boca + h * 0.03f), Offset(x + w * 0.03f, cuello), 1.2f * u)
            }
            drawRect(t.hondo.copy(alpha = 0.35f), Offset(if (hacia < 0f) cx + w * 0.08f else cx - w * 0.4f, 0f), Size(w * 0.32f, cuello))
        }
        drawPath(bocaP, t.hondo.copy(alpha = 0.6f), style = Stroke(0.8f * u))
        // El cabo que cierra la boca, con los ollaos de latón, el nudo y el chicote.
        val yc = cuello + h * 0.012f
        drawRect(a.sombra(0.4f), Offset(cx - w * 0.3f, yc + 1.5f * u), Size(w * 0.6f, 2.2f * u))
        drawLine(l.c(P.MANILA), Offset(cx - w * 0.3f, yc), Offset(cx + w * 0.3f, yc), 2.4f * u, StrokeCap.Round)
        drawLine(l.c(P.MANILA_OSC), Offset(cx - w * 0.3f, yc + 0.8f * u), Offset(cx + w * 0.3f, yc + 0.8f * u), 0.7f * u)
        val laton = l.laton()
        listOf(-0.2f, 0f, 0.2f).forEach { fx -> drawCircle(laton, 1.6f * u, Offset(cx + w * fx, yc), style = Stroke(1f * u)) }
        val nudo = Offset(cx + w * 0.12f, yc)
        drawCircle(l.c(P.MANILA_OSC), 2.4f * u, nudo)
        drawCircle(l.c(P.MANILA), 1.6f * u, nudo - Offset(0.5f * u, 0.5f * u))
        // La chapa del día, colgada del cabo por su cordel, en el frente (un poco ladeada).
        val etH = 30f * u
        val etW = minOf(w * 0.8f, 40f * u)
        val et0 = cuello + h * (0.14f + 0.08f * azar(s, 16))
        val et = Rect(cx - etW / 2, et0, cx + etW / 2, et0 + etH)
        drawLine(l.c(P.MANILA_OSC), nudo, Offset(et.center.x - etW * 0.1f, et.top + 2f * u), 1f * u)
        drawLine(l.c(P.MANILA_OSC), nudo, Offset(et.center.x + etW * 0.1f, et.top + 2f * u), 1f * u)
        etiquetaDia(v, medidor, et, l, (azar(s, 17) - 0.5f) * 5f)
        drawCircle(laton, 1.3f * u, Offset(et.center.x, et.top + 2.4f * u), style = Stroke(0.8f * u))
        // En los Sueños, la luna en la panza.
        if (v.noche) luna(Offset(cx, et.bottom + (h - et.bottom) * 0.42f), minOf(w * 0.18f, (h - et.bottom) * 0.25f, 7.5f * u), a)
        filos(Rect(0f, cuello, w, h), a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- los rollos de cartas

    fun DrawScope.rollos(v: Volumen, medidor: TextMeasurer, a: AireAbismo, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val t = tinte(ti, l, HULE_PARDO, 0.34f)
        val n = if (w > 38f * u) 3 else 2
        val funda = h * (0.24f + 0.06f * azar(s, 3))
        alrededor(a, hacia, funda * 0.6f)
        // Los rollos de papel que asoman sobre la funda, cada uno a su altura, con la espiral de su canto.
        val rw = w / n
        val viejo = mezcla(P.PAPEL, P.ESPARTO, 0.32f)
        val papel = Tinte(l.c(viejo), l.c(mezcla(viejo, luzMueble(1f), 0.3f)), l.c(mezcla(viejo, P.TECA_OSC, 0.45f)), viejo)
        for (i in 0 until n) {
            val top = h * (0.02f + 0.12f * azar(s, 20 + i))
            val r = Rect(rw * i + 0.4f * u, top + rw * 0.18f, rw * (i + 1) - 0.4f * u, funda + 4f * u)
            redondo(r, papel, hacia)
            // El borde impreso de la carta de fuera: una banda azul y una ocre cerca del canto, y manchas de agua.
            drawRect(l.c(P.AZUL_CARTA), Offset(r.left, r.top + r.width * 0.32f), Size(r.width, 1.6f * u))
            drawRect(l.c(P.ESPONJA).copy(alpha = 0.7f), Offset(r.left, r.top + r.width * 0.32f + 2.6f * u), Size(r.width, 1f * u))
            drawOval(l.c(P.ESPARTO).copy(alpha = 0.55f), Offset(r.left + r.width * 0.2f, r.top + (r.bottom - r.top) * (0.45f + 0.3f * azar(s, 25 + i))), Size(r.width * 0.5f, r.width * 0.3f))
            // Lo dibujado en la carta: una línea de costa y una sonda, en tinta desvaída.
            drawLine(l.c(P.AZUL_CARTA).copy(alpha = 0.6f), Offset(r.left + r.width * 0.2f, r.top + (r.bottom - r.top) * 0.4f), Offset(r.right - r.width * 0.15f, r.top + (r.bottom - r.top) * 0.55f), 0.8f * u)
            // El canto de arriba: un óvalo con la espiral del papel.
            val tc = Offset(r.center.x, r.top)
            val tr = r.width / 2
            drawOval(papel.claro, Offset(r.left, tc.y - tr * 0.36f), Size(r.width, tr * 0.72f))
            drawArc(papel.hondo, 0f, 300f, false, Offset(tc.x - tr * 0.6f, tc.y - tr * 0.22f), Size(tr * 1.2f, tr * 0.44f), style = Stroke(0.8f * u))
            drawArc(papel.hondo, 60f, 280f, false, Offset(tc.x - tr * 0.3f, tc.y - tr * 0.11f), Size(tr * 0.6f, tr * 0.22f), style = Stroke(0.8f * u))
            drawOval(a.sombra(0.25f), Offset(tc.x - tr * 0.12f, tc.y - tr * 0.05f), Size(tr * 0.24f, tr * 0.1f))
            // La hoja de fuera, que se despega un poco.
            drawLine(papel.hondo, Offset(r.right - 0.5f * u, r.top), Offset(r.right - 0.5f * u, r.bottom), 0.8f * u)
        }
        // La funda de hule, del color del ánimo, con su dobladillo arriba y su brillo encerado.
        val fr = Rect(0f, funda, w, h)
        drawRect(a.sombra(0.35f), Offset(fr.left, fr.top + 1.5f * u), Size(fr.width, 2f * u))
        drawRoundRect(t.cara, fr.topLeft, fr.size, CornerRadius(1.8f * u))
        redondo(Rect(fr.left, fr.top, fr.right, fr.bottom), t, hacia)
        clipRect(fr.left, fr.top, fr.right, fr.bottom) {
            // Los rollos que se marcan debajo del hule.
            for (i in 1 until n) drawLine(t.hondo.copy(alpha = 0.6f), Offset(rw * i, fr.top), Offset(rw * i, fr.bottom), 1.2f * u)
            // El brillo del hule: una veta clara a lo largo y unas grietas finas.
            val xb = if (hacia < 0f) w * 0.22f else w * 0.68f
            drawRect(a.brillo(0.35f, k), Offset(xb, fr.top + 3f * u), Size(w * 0.08f, fr.height - 6f * u))
            drawRect(a.brillo(0.6f, k), Offset(xb + w * 0.02f, fr.top + 3f * u), Size(1f * u, fr.height - 6f * u))
            for (i in 0 until 5) {
                val y = fr.top + fr.height * azar(s, 30 + i)
                drawLine(t.hondo.copy(alpha = 0.5f), Offset(w * azar(s, 40 + i), y), Offset(w * azar(s, 40 + i) + w * 0.2f, y + 1.5f * u), 0.5f * u)
            }
        }
        // El dobladillo de arriba.
        drawRect(t.claro, Offset(fr.left, fr.top), Size(fr.width, 2.6f * u))
        drawRect(t.hondo, Offset(fr.left, fr.top + 2.6f * u), Size(fr.width, 1f * u))
        // Las dos ataduras de piola, con sus vueltas.
        val ataduras = listOf(fr.top + fr.height * 0.12f, fr.bottom - fr.height * 0.1f)
        ataduras.forEach { y ->
            drawRect(a.sombra(0.4f), Offset(0f, y + 1.2f * u), Size(w, 1.6f * u))
            drawRect(l.c(P.ESPARTO), Offset(0f, y - 1.2f * u), Size(w, 2.4f * u))
            var x = 1f * u
            while (x < w) { drawLine(l.c(P.MANILA_OSC), Offset(x, y - 1.2f * u), Offset(x + 1.2f * u, y + 1.2f * u), 0.6f * u); x += 2.4f * u }
        }
        // La etiqueta pegada en el hule, entre las dos ataduras.
        val etH = 30f * u
        val libre0 = ataduras[0] + 4f * u; val libre1 = ataduras[1] - 4f * u
        val et0 = libre0 + ((libre1 - libre0 - etH) * (0.1f + 0.4f * azar(s, 7))).coerceAtLeast(0f)
        val et = Rect(2.4f * u, et0, w - 2.4f * u, et0 + etH)
        etiquetaDia(v, medidor, et, l)
        // En los Sueños, la luna en el hule, bajo la etiqueta.
        if (v.noche) luna(Offset(w / 2, (et.bottom + libre1) / 2), minOf(w * 0.18f, (libre1 - et.bottom) * 0.32f, 7.5f * u), a)
        filos(fr, a, k, hacia, luna)
    }
}
