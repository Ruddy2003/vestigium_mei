package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las entradas del estante de Frente ([MuebleEstanteFrente]), tres cosas del archivo de la célula según la semilla: el cartucho
 * de datos (rígido, de esquinas redondas: la carcasa del color del ánimo, la ventana con las dos bobinas de cinta parda, la placa
 * de aluminio de abajo y la etiqueta de cinta de papel arriba), el tubo de la carta estelar (un cilindro de cartón forrado del
 * color del ánimo, con su costura en espiral, la tapa y el fondo de aluminio, la correa ceñida y la vuelta de cinta de papel con
 * el día; en algunos, sin tapa, asoma la carta enrollada) y la valija de lona (blanda y panzona, del color del ánimo, con su
 * solapa cosida, la correa con su hebilla y la tarjeta del día en su portaetiquetas remachado). Sin ánimo: baquelita, cartón
 * pardo y lona cruda. En los Sueños, una media luna de plata.
 */
internal object VolumenEstanteFrente {
    private val P = PaletaFrente

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Los meses cortos en español, para la etiqueta (el ciclo ya lo dice el rótulo de la repisa). */
    private val MESES_CORTOS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

    /** El mes corto de una entrada: del «CICLO 11» del tema sale «nov.»; si no se entiende, el nombre tal cual, acortado. */
    private fun mesCorto(mes: String): String {
        val n = mes.filter { it.isDigit() }.take(2).toIntOrNull()
        if (n != null && n in 1..12) return MESES_CORTOS[n - 1]
        return if (mes.length > 4) mes.take(3).lowercase() + "." else mes.lowercase()
    }

    /** La luz que le toca a una entrada: la cálida del momento ([k]) y la fría del vidrio ([kv]). */
    private class Luz(val a: AireFrente, val k: Float, val kv: Float) {
        fun c(col: Color, f: Float = 0f): Color = a.dia(col, f, k, kv)
        fun animo(t: Color): Color = a.animo(t, k, kv)
        fun etiqueta(col: Color): Color = a.etiqueta(col, k, kv)
    }

    /** Los colores de un material con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(material: Color?, defecto: Color, l: Luz, varia: Float = 0f): Tinte {
        val m0 = material ?: defecto
        val m = if (varia > 0f) mezcla(m0, P.HUESO_LUZ, varia) else if (varia < 0f) mezcla(m0, P.SOMBRA, -varia) else m0
        val claro = mezcla(m, P.HUESO_LUZ, 0.32f)
        val hondo = mezcla(m, P.SOMBRA, 0.5f)
        return if (material != null) Tinte(l.animo(m), l.animo(claro), l.animo(hondo)) else Tinte(l.c(m), l.c(claro), l.c(hondo))
    }

    /** El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp) y el mes debajo, en una de 9.5 dp. */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left - 1f * u, y0, r.right + 1f * u, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.Bold, minimo = 11f)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left - 1f * u, y0 + hd - 1.4f * u, r.right + 1f * u, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.85f), MES, peso = FontWeight.Medium, minimo = 5f)
    }

    /** La media luna de los Sueños, de plata, con su canto y su sombra, en [c]. */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireFrente) {
        val u = density
        val m = lunaFrente(r)
        translate(c.x + 0.7f * u, c.y + 1f * u) { drawPath(m, a.sombra(0.55f)) }
        translate(c.x, c.y) {
            drawPath(m, if (a.oscuro) Color(0xFFCFE0DA) else Color(0xFFE4E8EC))
            drawPath(m, Color(0xFF26302E), style = Stroke(0.7f * u))
        }
    }

    /** Lo de todas: la sombra que dejan en la piedra por la rendija con la vecina y la de contacto en la bandeja. */
    private fun DrawScope.alrededor(a: AireFrente, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.45f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.45f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.55f), Offset(-1f * u, h - 0.5f * u), Size(w + 2f * u, 2.6f * u))
    }

    /**
     * Cuánto rojo del foco de su hueco le llega a la entrada que se está dibujando (lo pone el estante antes de cada una: cada
     * volumen se dibuja en su propio lienzo y no sabe dónde está el foco).
     */
    var rojo = 0f

    /** El filo de la luz cálida en el canto que la mira (de noche; rojo, si la que manda es la del foco) y el frío del vidrio en el otro. */
    private fun DrawScope.filos(cuerpo: Rect, a: AireFrente, k: Float, kv: Float, hacia: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(if (rojo > 0f) P.FOCO.copy(alpha = (0.85f * rojo).coerceIn(0f, 1f)) else a.filo(0.7f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (rojo > 0.2f) drawRect(P.FOCO.copy(alpha = 0.16f * rojo), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, cuerpo.height * 0.35f))
        if (kv > 0.08f) {
            drawRect(a.filoVidrio(0.75f * kv), Offset(cuerpo.right - 1.2f * u, cuerpo.top), Size(1.2f * u, cuerpo.height))
            // La cabeza, que mira arriba, hacia el vidrio: su filo frío.
            if (a.dormida) drawRect(a.filoVidrio((1.1f * kv).coerceAtMost(0.85f)), Offset(cuerpo.left + 1f * u, cuerpo.top), Size(cuerpo.width - 2f * u, 1.3f * u))
        }
    }

    /** Las líneas de la trama de una lona, de [y0] a [y1] cada [paso], en un solo camino (un solo trazo, no uno por hilo). */
    private fun trama(w: Float, y0: Float, y1: Float, paso: Float): Path {
        val p = Path()
        if (paso <= 0.1f) return p
        var y = y0
        while (y < y1) { p.moveTo(0f, y); p.lineTo(w, y); y += paso }
        return p
    }

    /** Una tira de cinta de papel en [r] con las puntas rasgadas, su sombra y el día y el mes escritos encima. */
    private fun DrawScope.cinta(v: Volumen, medidor: TextMeasurer, r: Rect, l: Luz, a: AireFrente, semilla: Int) {
        val u = density
        val n = 5
        val p = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top)
            for (j in 1..n) lineTo(r.right + (if (j % 2 == 1) -1.3f else 0.3f) * u * (0.6f + azar(semilla, j)), r.top + r.height * j / n)
            lineTo(r.left, r.bottom)
            for (j in n - 1 downTo 1) lineTo(r.left + (if (j % 2 == 1) 1.3f else -0.3f) * u * (0.6f + azar(semilla, j + 10)), r.top + r.height * j / n)
            close()
        }
        translate(0.8f * u, 1.1f * u) { drawPath(p, a.sombra(0.35f)) }
        drawPath(p, l.etiqueta(P.CINTA))
        drawRect(l.etiqueta(P.CINTA_OSC).copy(alpha = 0.55f), Offset(r.left + 1.6f * u, r.bottom - r.height * 0.14f), Size(r.width - 3.2f * u, r.height * 0.14f))
        drawRect(a.claro(0.25f), Offset(r.left + 1.6f * u, r.top), Size(r.width - 3.2f * u, r.height * 0.14f))
        diaYMes(v, medidor, Rect(r.left + 1f * u, r.top + 0.5f * u, r.right - 1f * u, r.bottom - 0.5f * u), a.tinta(l.k))
    }

    // ---------------------------------------------------------------- el cartucho de datos

    /**
     * El cartucho de datos, de pie: la carcasa del color del ánimo, de esquinas redondas, con la muesca de lectura y la pestaña
     * contra escritura arriba; la ventana ahumada grande con las dos bobinas de cinta parda (más en la de arriba cuanto más
     * escribiste); la etiqueta de cinta de papel con el día debajo, y la placa de aluminio al pie.
     */
    fun DrawScope.cartucho(v: Volumen, medidor: TextMeasurer, a: AireFrente, k: Float, kv: Float, hacia: Float, material: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, kv)
        alrededor(a, hacia, 4f * u)
        val t = tinte(material, P.BAQUELITA, l, (azar(s, 4) - 0.5f) * 0.1f)
        val cuerpo = Rect(0.4f * u, 0.4f * u, w - 0.4f * u, h)
        val rad = 4.4f * u
        // La carcasa, con la muesca de lectura arriba.
        val caja = Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(rad))) }
        val mx = w * (0.56f + 0.14f * azar(s, 5))
        val muesca = Path().apply { addRect(Rect(mx - w * 0.1f, -1f, mx + w * 0.1f, 3.6f * u)) }
        val carcasa = Path().apply { op(caja, muesca, PathOperation.Difference) }
        // La carcasa lisa y, encima, los cantos redondeados: claro del lado de la luz, hondo del otro (colores lisos y opacos, sin
        // recortar: lo más barato de pintar).
        val lado = minOf(3.4f * u, w * 0.1f)
        val izq = if (hacia < 0f) t.claro else t.hondo
        val der = if (hacia < 0f) t.hondo else t.claro
        drawPath(carcasa, t.cara)
        drawRect(izq, Offset(cuerpo.left, cuerpo.top + rad), Size(lado, cuerpo.height - 2 * rad))
        drawRect(mezcla(izq, t.cara, 0.5f), Offset(cuerpo.left + lado, cuerpo.top + rad * 0.5f), Size(lado * 0.6f, cuerpo.height - rad))
        drawRect(der, Offset(cuerpo.right - lado, cuerpo.top + rad), Size(lado, cuerpo.height - 2 * rad))
        // La cara, más clara arriba; las rayas y los roces del plástico.
        drawRect(a.claro(0.14f), Offset(cuerpo.left + rad, cuerpo.top + 3.6f * u), Size(cuerpo.width - 2 * rad, cuerpo.height * 0.12f - 3.6f * u))
        for (j in 0 until 3) {
            val y = h * (0.55f + 0.35f * azar(s, 40 + j)); val x = w * (0.15f + 0.5f * azar(s, 50 + j))
            drawLine(a.claro(0.2f), Offset(x, y), Offset(x + w * 0.25f, y + 0.6f * u), 0.6f * u)
        }
        drawPath(carcasa, a.sombra(0.6f), style = Stroke(0.8f * u))
        drawLine(a.brillo(0.5f, k), Offset(rad, 1.3f * u), Offset(mx - w * 0.11f, 1.3f * u), 0.9f * u)
        // La pestaña contra escritura, en su ranura (corrida si va lleno).
        val pe = Rect(w * 0.14f, 4.2f * u, w * 0.4f, 7.8f * u)
        drawRoundRect(a.sombra(0.75f), pe.topLeft, pe.size, CornerRadius(0.8f * u))
        drawRoundRect(l.c(if (azar(s, 6) > 0.5f) P.AMBAR else P.BOTON), Offset(pe.left + (if (v.lleno > 0.6f) pe.width * 0.4f else 0f), pe.top + 0.3f * u), Size(pe.width * 0.6f, pe.height - 0.6f * u), CornerRadius(0.8f * u))
        // La ventana ahumada, grande, con las dos bobinas de cinta parda.
        val ven = Rect(w * 0.13f, h * 0.12f, w * 0.87f, h * (0.5f + 0.04f * azar(s, 7)))
        val vr = CornerRadius(minOf(ven.width, ven.height) * 0.24f)
        drawRoundRect(a.sombra(0.6f), Offset(ven.left - 0.7f * u, ven.top - 0.9f * u), Size(ven.width + 1.4f * u, ven.height + 1.4f * u), vr)
        drawRoundRect(l.c(Color(0xFF181715)), ven.topLeft, ven.size, vr)
        clipRect(ven.left, ven.top, ven.right, ven.bottom) {
            val rMax = minOf(ven.width * 0.46f, ven.height * 0.27f)
            val c1 = Offset(ven.center.x, ven.top + ven.height * 0.28f)
            val c2 = Offset(ven.center.x, ven.bottom - ven.height * 0.28f)
            val r1 = rMax * (0.62f + 0.38f * v.lleno)
            val r2 = rMax * (1f - 0.36f * v.lleno)
            listOf(c1 to r1, c2 to r2).forEach { (c, r) ->
                drawCircle(l.c(P.PELICULA), r, c)
                drawCircle(l.c(P.MIEL).copy(alpha = 0.6f), r * 0.84f, c, style = Stroke(0.6f * u))
                drawCircle(l.c(P.MIEL).copy(alpha = 0.4f), r * 0.66f, c, style = Stroke(0.5f * u))
                // El cubo blanco de la bobina, con su aro dentado.
                drawCircle(l.c(P.HUESO, 0.05f), rMax * 0.36f, c)
                drawCircle(l.c(P.HUESO_OSC), rMax * 0.3f, c, style = Stroke(1f * u))
                drawCircle(l.c(Color(0xFF181715)), rMax * 0.12f, c)
            }
            // La cinta que corre de una bobina a la otra por el lado del cabezal.
            drawLine(l.c(P.PELICULA), Offset(c1.x + r1 * 0.98f, c1.y), Offset(ven.right - 1.4f * u, ven.center.y), 1.1f * u)
            drawLine(l.c(P.PELICULA), Offset(c2.x + r2 * 0.98f, c2.y), Offset(ven.right - 1.4f * u, ven.center.y), 1.1f * u)
            // El reflejo del plástico ahumado.
            drawLine(Color.White.copy(alpha = if (a.oscuro) 0.06f * k + 0.02f else 0.2f), Offset(ven.left + ven.width * 0.15f, ven.bottom), Offset(ven.left + ven.width * 0.65f, ven.top), ven.width * 0.14f)
        }
        drawRoundRect(a.brillo(0.35f, k), ven.topLeft, ven.size, vr, style = Stroke(0.7f * u))
        // La etiqueta de cinta de papel con el día y el mes, debajo de la ventana.
        val eh = minOf(h * 0.32f, 29f * u)
        val ey = minOf(ven.bottom + h * 0.035f, h * 0.88f - eh)
        cinta(v, medidor, Rect(w * 0.06f, ey, w * 0.94f, ey + eh), l, a, s)
        // La placa de aluminio de abajo, con sus dos tornillos.
        val pl = Rect(0.8f * u, h * 0.9f, w - 0.8f * u, h - 0.8f * u)
        drawRect(l.c(P.ALUMINIO, 0.05f), pl.topLeft, pl.size)
        drawRect(a.claro(0.35f), pl.topLeft, Size(pl.width, 0.9f * u))
        drawRect(a.sombra(0.35f), Offset(pl.left, pl.bottom - 0.9f * u), Size(pl.width, 0.9f * u))
        listOf(pl.left + 3.4f * u, pl.right - 3.4f * u).forEachIndexed { i, x -> tornilloFrente(Offset(x, pl.center.y), minOf(1.3f * u, pl.height * 0.32f), a, k, 30f + 50f * i, kv) }
        if (v.noche) luna(Offset(w * 0.78f, h * 0.075f + 1f * u), minOf(w * 0.12f, 3.8f * u), a)
        filos(cuerpo, a, k, kv, hacia)
    }

    // ---------------------------------------------------------------- el tubo de la carta estelar

    /**
     * El tubo de la carta estelar, de pie: un cilindro de cartón forrado del color del ánimo, algo más angosto que su sitio, con
     * la costura en espiral, el fondo de aluminio, la tapa de aluminio con su cara de arriba vista (o, sin tapa, la carta
     * enrollada que asoma con su ruta punteada), la vuelta de cinta de papel con el día, curvada como el tubo, y la correa de
     * cuero ceñida con su hebilla.
     */
    fun DrawScope.tubo(v: Volumen, medidor: TextMeasurer, a: AireFrente, k: Float, kv: Float, hacia: Float, material: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, kv)
        val sinTapa = azar(s, 8) > 0.66f
        val t = tinte(material, P.CARTON, l, (azar(s, 4) - 0.5f) * 0.12f)
        val m = w * 0.1f
        val elipse = minOf(w * 0.3f, 7f * u)
        val tb = Rect(m, if (sinTapa) h * 0.14f else h * 0.11f, w - m, h - h * 0.05f)
        alrededor(a, hacia, tb.top)
        // Sin tapa: la carta enrollada que asoma por la boca, con su borde impreso y su espiral.
        if (sinTapa) {
            val rollo = Rect(tb.left + tb.width * 0.14f, 1.5f * u, tb.right - tb.width * 0.14f, tb.top + 3f * u)
            redondoFrente(rollo, l.c(P.PAPEL), l.c(P.PAPEL, 0.3f), l.c(P.PAPEL, -0.3f), hacia, 0.7f)
            drawLine(l.c(P.QUEMADO_TINTA).copy(alpha = 0.85f), Offset(rollo.left + rollo.width * 0.32f, rollo.top + 4f * u), Offset(rollo.left + rollo.width * 0.32f, rollo.bottom),
                1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.4f * u, 2.6f * u)), cap = StrokeCap.Round)
            drawCircle(l.c(P.BOTON), 1.6f * u, Offset(rollo.left + rollo.width * 0.32f, rollo.top + 4.5f * u), style = Stroke(0.8f * u))
            val boca = Rect(rollo.left, rollo.top - elipse * 0.35f, rollo.right, rollo.top + elipse * 0.35f)
            drawOval(l.c(P.PAPEL, 0.2f), boca.topLeft, boca.size)
            // La espiral del papel enrollado, vista desde arriba.
            drawArc(l.c(P.CINTA_OSC), 200f, 250f, false, Offset(boca.left + boca.width * 0.25f, boca.top + boca.height * 0.2f), Size(boca.width * 0.5f, boca.height * 0.6f), style = Stroke(0.7f * u))
        }
        // El cuerpo de cartón forrado, redondo, con la costura en espiral y su fondo curvo.
        val forma = Path().apply {
            moveTo(tb.left, tb.top); lineTo(tb.right, tb.top); lineTo(tb.right, tb.bottom)
            quadraticTo(tb.center.x, tb.bottom + elipse * 0.7f, tb.left, tb.bottom); close()
        }
        drawPath(forma, t.cara)
        clipRect(tb.left, tb.top, tb.right, tb.bottom) {
            redondoFrente(Rect(tb.left, tb.top, tb.right, tb.bottom - h * 0.05f), t.cara, t.claro, t.hondo, hacia)
            val paso = maxOf(7f * u, tb.width * 0.7f)
            var y = tb.top - tb.width
            while (y < tb.bottom) {
                drawLine(mezcla(t.hondo, t.cara, 0.15f), Offset(tb.left, y + tb.width * 0.4f), Offset(tb.right, y), 1f * u)
                drawLine(a.claro(0.16f), Offset(tb.left, y + tb.width * 0.4f + 1f * u), Offset(tb.right, y + 1f * u), 0.6f * u)
                y += paso
            }
            // Los roces del forro, donde asoma el cartón.
            for (j in 0 until 2) {
                val yy = tb.top + tb.height * (0.7f + 0.25f * azar(s, 60 + j))
                drawOval(l.c(P.CARTON).copy(alpha = 0.7f), Offset(tb.left + tb.width * azar(s, 70 + j) * 0.6f, yy), Size(tb.width * 0.3f, 2.4f * u))
            }
        }
        // El fondo de aluminio, un aro curvo.
        val yf = tb.bottom - h * 0.06f
        val fondo = Path().apply {
            moveTo(tb.left, yf); quadraticTo(tb.center.x, yf + elipse * 0.7f, tb.right, yf)
            lineTo(tb.right, tb.bottom); quadraticTo(tb.center.x, tb.bottom + elipse * 0.7f, tb.left, tb.bottom); close()
        }
        drawPath(fondo, l.c(P.ALUMINIO))
        drawRect(l.c(P.CROMO_LUZ), Offset(if (hacia < 0f) tb.left + tb.width * 0.1f else tb.right - tb.width * 0.2f, yf + elipse * 0.25f), Size(tb.width * 0.1f, h * 0.045f))
        drawRect(l.c(P.CROMO_OSC), Offset(if (hacia < 0f) tb.right - tb.width * 0.2f else tb.left + tb.width * 0.05f, yf + elipse * 0.15f), Size(tb.width * 0.15f, h * 0.05f))
        drawPath(Path().apply { moveTo(tb.left, tb.bottom - h * 0.06f); quadraticTo(tb.center.x, tb.bottom - h * 0.06f + elipse * 0.7f, tb.right, tb.bottom - h * 0.06f) },
            a.sombra(0.45f), style = Stroke(0.9f * u))
        // La tapa de aluminio: el canto con sus aros y, encima, su cara redonda vista desde arriba.
        if (!sinTapa) {
            val tapa = Rect(m * 0.4f, elipse * 0.5f + 0.6f * u, w - m * 0.4f, tb.top + h * 0.06f)
            redondoFrente(tapa, l.c(P.ALUMINIO), l.c(P.CROMO_LUZ, 0.1f), l.c(P.CROMO_OSC), hacia)
            listOf(0.4f, 0.7f).forEach { f -> drawLine(a.sombra(0.35f), Offset(tapa.left, tapa.top + tapa.height * f), Offset(tapa.right, tapa.top + tapa.height * f), 0.8f * u) }
            drawPath(Path().apply { moveTo(tapa.left, tapa.bottom); quadraticTo(tapa.center.x, tapa.bottom + elipse * 0.6f, tapa.right, tapa.bottom) }, a.sombra(0.55f), style = Stroke(1.2f * u))
            val cara = Rect(tapa.left, tapa.top - elipse * 0.5f, tapa.right, tapa.top + elipse * 0.5f)
            drawOval(l.c(P.CROMO_LUZ, 0.05f), cara.topLeft, cara.size)
            drawOval(a.sombra(0.25f), Offset(cara.left + cara.width * 0.15f, cara.top + cara.height * 0.18f), Size(cara.width * 0.7f, cara.height * 0.64f), style = Stroke(0.7f * u))
            drawOval(a.sombra(0.4f), cara.topLeft, cara.size, style = Stroke(0.6f * u))
            if (v.noche) luna(Offset(w * 0.5f, tapa.top + tapa.height * 0.55f), minOf(w * 0.13f, tapa.height * 0.38f), a)
        } else {
            drawOval(t.hondo, Offset(tb.left, tb.top - elipse * 0.4f), Size(tb.width, elipse * 0.8f), style = Stroke(1.6f * u))
            drawArc(a.brillo(0.45f, k), 200f, 140f, false, Offset(tb.left, tb.top - elipse * 0.4f), Size(tb.width, elipse * 0.8f), style = Stroke(0.7f * u))
        }
        // La vuelta de cinta de papel con el día, curvada como el tubo, a una altura que cambia de uno a otro.
        val eh = minOf(h * 0.3f, 29f * u)
        val ey = tb.top + h * (0.08f + 0.2f * azar(s, 9))
        val et = Rect(tb.left - 0.4f * u, ey, tb.right + 0.4f * u, ey + eh)
        val curva = minOf(2.6f * u, tb.width * 0.08f)
        val banda = Path().apply {
            moveTo(et.left, et.top); quadraticTo(et.center.x, et.top + curva * 2f, et.right, et.top)
            lineTo(et.right, et.bottom); quadraticTo(et.center.x, et.bottom + curva * 2f, et.left, et.bottom); close()
        }
        translate(0.6f * u, 1.2f * u) { drawPath(banda, a.sombra(0.35f)) }
        // La cinta es redonda como el tubo: más clara del lado de la luz, más honda del otro.
        val ce = l.etiqueta(P.CINTA)
        drawPath(banda, ce)
        val ladoB = et.width * 0.18f
        drawRect(mezcla(ce, a.hondo, if (a.oscuro) 0.35f else 0.28f), Offset(if (hacia < 0f) et.right - ladoB else et.left, et.top + curva), Size(ladoB, et.height))
        drawRect(mezcla(ce, P.HUESO_LUZ, if (a.oscuro) 0.06f else 0.28f), Offset(if (hacia < 0f) et.left + et.width * 0.06f else et.right - et.width * 0.06f - ladoB * 0.6f, et.top + curva), Size(ladoB * 0.6f, et.height))
        diaYMes(v, medidor, Rect(et.left + 1f * u, et.top + curva, et.right - 1f * u, et.bottom + curva), a.tinta(k))
        // La correa ceñida, de cuero, con su hebilla, más abajo.
        val cy = minOf(tb.bottom - h * 0.12f, et.bottom + curva * 2f + h * (0.08f + 0.1f * azar(s, 10)))
        val co = Path().apply {
            moveTo(tb.left - 0.5f * u, cy); quadraticTo(tb.center.x, cy + curva * 2f, tb.right + 0.5f * u, cy)
            lineTo(tb.right + 0.5f * u, cy + 4.6f * u); quadraticTo(tb.center.x, cy + 4.6f * u + curva * 2f, tb.left - 0.5f * u, cy + 4.6f * u); close()
        }
        drawPath(co, l.c(P.CUERO))
        drawLine(l.c(P.CUERO_LUZ), Offset(tb.left, cy + 1.2f * u), Offset(tb.right, cy + 1.2f * u), 0.9f * u)
        drawPath(Path().apply { moveTo(tb.left, cy + 5f * u); quadraticTo(tb.center.x, cy + 5f * u + curva * 2f, tb.right, cy + 5f * u) }, a.sombra(0.5f), style = Stroke(0.9f * u))
        val hx = tb.left + tb.width * (0.3f + 0.3f * azar(s, 11))
        drawRect(l.c(P.CROMO), Offset(hx, cy - 0.6f * u + curva), Size(4f * u, 5.8f * u), style = Stroke(1.1f * u))
        drawLine(a.brillo(0.55f, k), Offset(hx, cy - 0.2f * u + curva), Offset(hx + 4f * u, cy - 0.2f * u + curva), 0.6f * u)
        if (v.noche && sinTapa) luna(Offset(tb.center.x, minOf(tb.bottom - h * 0.1f, cy + h * 0.13f)), minOf(w * 0.13f, 4f * u), a)
        filos(tb, a, k, kv, hacia)
    }

    // ---------------------------------------------------------------- la valija de lona

    /**
     * La valija de correo de la red, de lona del color del ánimo: el asa de cuero arriba en sus dos anillas, el cuerpo blando y algo
     * caído de un lado, con el fuelle abultado del costado, sus costuras y las cantoneras de cuero de abajo; la solapa ribeteada de
     * cuero con la tarjeta del día en su portaetiquetas remachado, y una o dos correas que bajan de debajo de la solapa a sus
     * hebillas de cromo. Por semilla: a qué lado cae, una o dos correas, el asa levantada o tumbada, un remiendo.
     */
    fun DrawScope.valija(v: Volumen, medidor: TextMeasurer, a: AireFrente, k: Float, kv: Float, hacia: Float, material: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, kv)
        val t = tinte(material, P.LONA, l, (azar(s, 4) - 0.5f) * 0.12f)
        val cuero = l.c(P.CUERO); val cueroLuz = l.c(P.CUERO_LUZ); val cueroOsc = l.c(mezcla(P.CUERO, P.SOMBRA, 0.45f))
        val caeIzq = azar(s, 16) > 0.5f
        val cae = h * (0.025f + 0.025f * azar(s, 17))
        val arriba = h * 0.12f
        val yi = arriba + (if (caeIzq) cae else 0f); val yd = arriba + (if (caeIzq) 0f else cae)
        alrededor(a, hacia, arriba)
        // El asa de cuero, levantada (o tumbada a un lado), en sus dos anillas de cromo.
        val tumbada = azar(s, 18) > 0.6f
        val a0 = Offset(w * 0.3f, yi + (yd - yi) * 0.3f); val a1 = Offset(w * 0.7f, yi + (yd - yi) * 0.7f)
        val asa = Path().apply {
            moveTo(a0.x, a0.y)
            if (tumbada) cubicTo(a0.x - w * 0.02f, a0.y - h * 0.05f, a1.x + w * 0.12f, a1.y - h * 0.06f, a1.x, a1.y)
            else cubicTo(a0.x - w * 0.04f, 0.6f * u, a1.x + w * 0.04f, 0.6f * u, a1.x, a1.y)
        }
        drawPath(asa, a.sombra(0.4f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        drawPath(asa, cuero, style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawPath(asa, cueroLuz.copy(alpha = 0.7f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        listOf(a0, a1).forEach { o -> drawCircle(l.c(P.CROMO), 1.8f * u, o, style = Stroke(1f * u)) }
        // La bolsa: blanda, más ancha abajo, caída de un lado, con las esquinas de abajo redondas.
        val panza = 0.015f + 0.03f * v.lleno
        val bolsa = Path().apply {
            moveTo(w * 0.07f, yi)
            lineTo(w * 0.93f, yd)
            cubicTo(w * (0.97f + panza), yd + h * 0.2f, w * (1f + panza), h * 0.6f, w * (0.98f + panza * 0.5f), h * 0.86f)
            quadraticTo(w * 0.97f, h, w * 0.8f, h)
            lineTo(w * 0.2f, h)
            quadraticTo(w * 0.03f, h, w * (0.02f - panza * 0.5f), h * 0.86f)
            cubicTo(w * (0f - panza), h * 0.6f, w * (0.03f - panza), yi + h * 0.2f, w * 0.07f, yi)
            close()
        }
        drawPath(bolsa, t.cara)
        clipRect(w * 0.05f, minOf(yi, yd), w * 0.95f, h - 1.2f * u) {
            redondoFrente(Rect(w * 0.02f, 0f, w * 0.98f, h), t.cara, t.claro, t.hondo, hacia, 0.85f)
            // La trama de la lona.
            drawPath(trama(w, arriba, h, 1.8f * u), t.hondo.copy(alpha = 0.14f), style = Stroke(0.5f * u))
            // El fuelle del costado de la sombra: una banda abultada más honda, con su pliegue.
            val fx = if (hacia < 0f) w * 0.84f else w * 0.06f
            val fuelle = Path().apply {
                moveTo(fx, h * 0.3f); quadraticTo(fx + w * 0.05f, h * 0.62f, fx + w * 0.01f, h * 0.98f)
                lineTo(fx + w * 0.11f, h * 0.98f); quadraticTo(fx + w * 0.14f, h * 0.6f, fx + w * 0.1f, h * 0.3f); close()
            }
            drawPath(fuelle, t.hondo.copy(alpha = 0.55f))
            drawPath(Path().apply { moveTo(fx + w * 0.05f, h * 0.34f); quadraticTo(fx + w * 0.1f, h * 0.64f, fx + w * 0.06f, h * 0.96f) }, t.claro.copy(alpha = 0.45f), style = Stroke(0.8f * u))
            // La panza, abajo, que se comba con su luz.
            drawOval(a.claro(0.16f), Offset(w * (if (hacia < 0f) 0.12f else 0.46f), h * 0.66f), Size(w * 0.4f, h * 0.2f))
            // Un remiendo de otra lona, en algunas.
            if (azar(s, 12) > 0.62f) {
                val rx = w * (0.18f + 0.3f * azar(s, 13))
                val rp = Rect(rx, h * 0.8f, rx + w * 0.3f, h * 0.92f)
                drawRect(l.c(mezcla(P.LONA, P.SOMBRA, 0.25f)), rp.topLeft, rp.size)
                drawRect(t.hondo.copy(alpha = 0.6f), rp.topLeft, rp.size, style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 1f * u))))
            }
        }
        // Las costuras de los costados y de abajo.
        val costura = Path().apply {
            moveTo(w * 0.1f, yi + h * 0.04f); quadraticTo(w * 0.04f, h * 0.6f, w * 0.12f, h - 3f * u); lineTo(w * 0.88f, h - 3f * u)
            quadraticTo(w * 0.96f, h * 0.6f, w * 0.9f, yd + h * 0.04f)
        }
        drawPath(costura, t.hondo.copy(alpha = 0.7f), style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * u, 1.1f * u))))
        drawPath(bolsa, t.hondo.copy(alpha = 0.65f), style = Stroke(0.8f * u))
        // Las cantoneras de cuero de las esquinas de abajo, con su remache.
        listOf(true, false).forEach { izq ->
            val x0 = if (izq) w * 0.02f else w * 0.98f
            val sx = if (izq) 1f else -1f
            val esq = Path().apply {
                moveTo(x0, h * 0.82f); quadraticTo(x0 + sx * w * 0.01f, h - 0.5f * u, x0 + sx * w * 0.2f, h - 0.5f * u)
                lineTo(x0 + sx * w * 0.24f, h - h * 0.05f); quadraticTo(x0 + sx * w * 0.06f, h - h * 0.06f, x0 + sx * w * 0.07f, h * 0.82f); close()
            }
            drawPath(esq, if ((izq && hacia < 0f) || (!izq && hacia > 0f)) cueroLuz else cueroOsc)
            drawCircle(l.c(P.CROMO), 0.9f * u, Offset(x0 + sx * w * 0.1f, h - h * 0.04f))
        }
        // La solapa, ribeteada de cuero, de esquinas redondas, con su sombra en la bolsa.
        val sb = h * (0.52f + 0.04f * azar(s, 14))
        val ri = sb + (if (caeIzq) cae * 0.5f else 0f); val rd = sb + (if (caeIzq) 0f else cae * 0.5f)
        val solapa = Path().apply {
            moveTo(w * 0.06f, yi - 1f * u); lineTo(w * 0.94f, yd - 1f * u)
            lineTo(w * 0.95f, rd - w * 0.16f); quadraticTo(w * 0.95f, rd, w * 0.78f, rd)
            lineTo(w * 0.22f, ri); quadraticTo(w * 0.05f, ri, w * 0.05f, ri - w * 0.16f); close()
        }
        translate(0.6f * u, 2.6f * u) { drawPath(solapa, a.sombra(0.5f)) }
        drawPath(solapa, mezcla(t.cara, t.claro, 0.15f))
        clipRect(w * 0.1f, minOf(yi, yd), w * 0.9f, maxOf(ri, rd) - h * 0.05f) {
            redondoFrente(Rect(w * 0.06f, 0f, w * 0.94f, h), mezcla(t.cara, t.claro, 0.15f), t.claro, t.hondo, hacia, 0.7f)
            drawPath(trama(w, arriba, sb, 1.8f * u), t.hondo.copy(alpha = 0.12f), style = Stroke(0.5f * u))
        }
        drawPath(solapa, cuero, style = Stroke(2f * u))
        drawPath(solapa, cueroLuz.copy(alpha = 0.6f), style = Stroke(0.6f * u))
        // La tarjeta del día en su portaetiquetas remachado, en la solapa.
        val eh = minOf(sb - arriba - h * 0.08f, 29f * u)
        val ey = minOf(yi, yd) + h * 0.045f
        val et = Rect(w * 0.13f, ey, w * 0.87f, ey + eh)
        drawRect(a.sombra(0.4f), Offset(et.left + 0.8f * u, et.top + 1.2f * u), et.size)
        drawRect(l.c(P.CROMO_OSC), et.inflate(0.9f * u).topLeft, et.inflate(0.9f * u).size)
        drawRect(l.etiqueta(P.MANILA), et.topLeft, et.size)
        drawRect(a.claro(0.3f), et.topLeft, Size(et.width, 0.8f * u))
        diaYMes(v, medidor, Rect(et.left + 0.5f * u, et.top + 0.5f * u, et.right - 0.5f * u, et.bottom - 0.5f * u), a.tinta(k))
        listOf(et.left + 0.4f * u, et.right - 0.4f * u).forEach { x -> drawCircle(l.c(P.CROMO), 1f * u, Offset(x, et.top + 1.6f * u)) }
        // Las correas que salen de debajo de la solapa y bajan a sus hebillas.
        val dos = w > 36f * u && azar(s, 15) > 0.35f
        val xs = if (dos) listOf(w * 0.3f, w * 0.7f) else listOf(w * (0.46f + 0.08f * azar(s, 19)))
        val cw = minOf(w * (if (dos) 0.16f else 0.24f), 6.4f * u)
        xs.forEach { cx ->
            val yTop = ri + (rd - ri) * (cx / w) - 2f * u
            val co = Rect(cx - cw / 2, yTop, cx + cw / 2, yTop + h * 0.25f)
            drawRect(a.sombra(0.45f), Offset(co.left + 1f * u, co.top + 1.4f * u), co.size)
            redondoFrente(co, cuero, cueroLuz, cueroOsc, hacia, 0.8f)
            drawLine(cueroOsc, Offset(co.left + 0.8f * u, co.top), Offset(co.left + 0.8f * u, co.bottom), 0.5f * u)
            val hb = Rect(co.left - 1.6f * u, co.top + h * 0.1f, co.right + 1.6f * u, co.top + h * 0.17f)
            drawRect(a.sombra(0.4f), Offset(hb.left + 0.8f * u, hb.top + 1f * u), hb.size, style = Stroke(1.3f * u))
            drawRect(l.c(P.CROMO), hb.topLeft, hb.size, style = Stroke(1.3f * u))
            drawLine(l.c(P.CROMO_LUZ), Offset(hb.center.x, hb.top), Offset(hb.center.x, hb.bottom), 0.9f * u)
            drawLine(a.brillo(0.6f, k), Offset(hb.left, hb.top), Offset(hb.right, hb.top), 0.6f * u)
            // La punta de la correa, que sobra por debajo de la hebilla.
            drawRect(cueroOsc, Offset(co.left + cw * 0.15f, co.bottom - 0.5f * u), Size(cw * 0.7f, h * 0.035f))
        }
        if (v.noche) luna(Offset(w * (if (dos) 0.5f else if (xs[0] > w * 0.5f) 0.26f else 0.74f), sb + h * 0.24f), minOf(w * 0.12f, 4.2f * u), a)
        filos(Rect(w * 0.05f, arriba, w * 0.95f, h), a, k, kv, hacia)
    }
}
