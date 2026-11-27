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
 * Las entradas del estante de Navidad ([MuebleEstanteNavidad]), tres cosas de la alacena de la cocina según la semilla: el álbum
 * de las tarjetas (de pie, forrado de tela, con el lomo redondo, sus nervios, los filetes dorados, la etiqueta del día pegada y,
 * por arriba, los cantos de las tarjetas que guarda), el regalo (la caja envuelta en papel con lunares, cuadros o rayas, la cinta
 * cruzada, el lazo arriba y la etiqueta de regalo atada con el día; sin ánimo, papel de estraza con cordel y una ramita) y el
 * calcetín de lana (colgado de su gancho bajo la tabla de arriba, de punto, con el puño crema, el talón, la puntera, la etiqueta
 * cosida y una ramita o una carta que asoma por arriba). El ánimo es la tela, el papel y la lana, en todo el cuerpo ([tonoDeDiaNavidad]); sin
 * ánimo, lino crudo, estraza o lana sin teñir. En los Sueños, una media luna de plata con su disco apagado.
 */
internal object VolumenEstanteNavidad {
    private val P = PaletaNavidad

    /** El oro de los dorados; el lino crudo, la estraza y la lana sin teñir de lo que no tiene ánimo. */
    private val ORO = Color(0xFFE2BE62)
    private val ORO_OSC = Color(0xFF94701F)
    private val LINO = Color(0xFFB4A684)
    private val ESTRAZA = Color(0xFFB0845A)
    private val LANA_CRUDA = Color(0xFFC4B696)

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.38f, 0.75f, 0.42f, 0.08f, -0.22f, -0.5f, -0.76f)

    /** La luz que le toca a una entrada: la del momento ([k]) y, en los Sueños oscuros, la luna de la ventana ([luna]). */
    private class Luz(val a: AireNavidad, val k: Float, val luna: Float) {
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
        fun oro(col: Color = ORO): Color = if (a.oscuro) mezcla(a.luz(col, k), Color(0xFFFFD98A), 0.35f * k) else c(col)
        /** La pieza con la luz, para las piezas compartidas (lazos, ramitas, naranjas). */
        fun pieza(hacia: Float) = LuzNavidad(a, k, 0f, null, luna, hacia)
    }

    /** Los colores de un tinte con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color, val base: Color)

    private fun tinte(t: Color?, l: Luz, crudo: Color, brillo: Float = 0.3f): Tinte {
        val m = t ?: crudo
        val claro = mezcla(m, luzMueble(1f), if (t == null) 0.2f else brillo)
        val hondo = mezcla(m, sombraMueble(1f), 0.55f)
        return if (t != null) Tinte(l.animo(m), l.animo(claro), l.animo(hondo), m) else Tinte(l.c(m), l.c(claro), l.c(hondo), m)
    }

    /** Si el tinte es rojizo (para elegir la cinta y el hilo que contrasten con él). */
    private fun rojizo(c: Color) = c.red > c.green + 0.12f
    private fun claroDe(c: Color) = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f > 0.55f

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

    /** El mes corto en español, como «nov.» (el de diciembre, que el tema llama «de la Navidad», es «dic.»). */
    private fun mesCorto(mes: String): String {
        val m = mes.removePrefix("Mes de ").removePrefix("mes de ").trim()
        if (m.startsWith("la Navidad")) return "dic."
        val n = m.substringBefore(' ')
        return if (n.length > 4) n.take(3).lowercase() + "." else n.lowercase()
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

    /** La tinta del día: oscura siempre, para que se lea en la cartulina. */
    private fun tinta(a: AireNavidad, k: Float) = if (a.oscuro) Color(0xFF140C06) else a.luz(P.TINTA, maxOf(k, 0.75f))

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
    private fun DrawScope.luna(c: Offset, r: Float, a: AireNavidad) {
        val u = density
        val m = creciente(r)
        drawCircle(Color(0xFF3A4258).copy(alpha = 0.55f), r, c)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, a.sombra(0.5f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFDDE4F0))
            drawPath(m, Color(0xFF2E3448), style = Stroke(0.8f * u))
        }
        drawCircle(Color.White.copy(alpha = 0.85f), r * 0.14f, Offset(c.x - r * 0.6f, c.y - r * 0.16f))
    }

    /** Lo de las que están de pie: la sombra que dejan en el fondo por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireNavidad, hacia: Float, desde: Float, ancho: Float = size.width) {
        val u = density
        val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.42f), Offset(ancho, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.42f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.4f * u), Size(ancho + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche) y el roce frío de la luna. */
    private fun DrawScope.filos(cuerpo: Rect, a: AireNavidad, k: Float, hacia: Float, luna: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.7f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (luna > 0f) drawRect(a.lunar(0.6f * luna), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    /** La etiqueta de cartulina en [et] con el día y el mes: su sombra, el filete rojo y el borde gastado. */
    private fun DrawScope.etiquetaDia(v: Volumen, medidor: TextMeasurer, et: Rect, l: Luz, chaflan: Float = 0f) {
        val u = density
        val a = l.a
        val forma = if (chaflan > 0f) poligonoNavidad(et.left + chaflan, et.top, et.right - chaflan, et.top, et.right, et.top + chaflan, et.right, et.bottom,
            et.left, et.bottom, et.left, et.top + chaflan) else Path().apply { addRect(et) }
        drawPath(corridoNavidad(forma, 1f * u, 1.3f * u), a.sombra(0.38f))
        drawPath(forma, l.etiqueta(P.CARTULINA))
        drawRect(l.etiqueta(P.CARTULINA_OSC).copy(alpha = 0.45f), Offset(et.left, et.bottom - et.height * 0.12f), Size(et.width, et.height * 0.12f))
        val dentro = et.deflate(1.5f * u)
        drawRect(a.luz(P.CARMIN, maxOf(l.k, 0.5f)).copy(alpha = if (a.oscuro) 0.55f else 0.7f), dentro.topLeft, dentro.size, style = Stroke(0.6f * u))
        diaYMes(v, medidor, et.deflate(1.8f * u), tinta(a, l.k))
    }

    // ---------------------------------------------------------------- el álbum de las tarjetas

    fun DrawScope.album(v: Volumen, medidor: TextMeasurer, a: AireNavidad, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        // Las tarjetas que asoman por la cabeza, entre las tapas: más cuantas más palabras.
        val asoman = h * (0.05f + 0.06f * v.lleno)
        val ceja = asoman + 2.4f * u
        alrededor(a, hacia, ceja)
        val cuantas = 2 + (v.lleno * 3f).toInt()
        for (i in 0 until cuantas) {
            val x0 = w * (0.1f + 0.75f * azar(s, 30 + i)) - w * 0.2f
            val tw = w * (0.38f + 0.25f * azar(s, 40 + i))
            val y0 = ceja - asoman * (0.5f + 0.5f * azar(s, 50 + i))
            val col = listOf(P.PAPEL, P.CREMA, mezcla(P.PAPEL, P.SALVIA, 0.3f))[i % 3]
            rotate((azar(s, 60 + i) - 0.5f) * 10f, Offset(x0 + tw / 2, ceja)) {
                drawRect(l.c(col), Offset(x0.coerceAtLeast(0f), y0), Size(minOf(tw, w - x0.coerceAtLeast(0f)), ceja - y0 + 2f * u))
                drawRect(l.c(if (i % 2 == 0) P.CARMIN else P.FILETE), Offset(x0.coerceAtLeast(0f) + 1.4f * u, y0 + 1.4f * u), Size((minOf(tw, w - x0.coerceAtLeast(0f)) - 2.8f * u).coerceAtLeast(0f), 0.8f * u))
                drawRect(a.sombra(0.18f), Offset(x0.coerceAtLeast(0f), y0), Size(1f * u, ceja - y0))
            }
        }
        val t = tinte(ti, l, LINO)
        // Los cantos de las tapas que asoman arriba y abajo del lomo, en la misma tela, más hondos.
        drawRoundRect(t.hondo, Offset(0f, ceja - 2.4f * u), Size(w, h - ceja + 2.4f * u), CornerRadius(1.6f * u))
        val lomo = Rect(0.8f * u, ceja, w - 0.8f * u, h - 2f * u)
        redondo(lomo, t, hacia)
        // La trama de la tela: rayitas finas a lo ancho y alguna a lo alto, y el roce en los cantos.
        clipRect(lomo.left, lomo.top, lomo.right, lomo.bottom) {
            val trama = Path()
            var y = lomo.top + 1f * u
            var j = 0
            while (y < lomo.bottom && j < 200) { trama.moveTo(lomo.left, y); trama.lineTo(lomo.right, y); y += 2.2f * u; j++ }
            drawPath(trama, t.hondo.copy(alpha = 0.16f), style = Stroke(0.5f * u))
            drawRect(t.claro.copy(alpha = 0.4f), Offset(lomo.left, lomo.top), Size(lomo.width, 1.6f * u))
        }
        // La cofia arriba y las dos ruedas doradas de cabeza y pie.
        drawRoundRect(t.claro, Offset(lomo.left, ceja - 0.6f * u), Size(lomo.width, 2.2f * u), CornerRadius(1f * u))
        val oro = l.oro(); val oroOsc = l.oro(ORO_OSC)
        listOf(lomo.top + 4f * u, lomo.bottom - 4f * u).forEach { y ->
            drawLine(oro, Offset(lomo.left + 1f * u, y), Offset(lomo.right - 1f * u, y), 0.8f * u)
            drawLine(oro.copy(alpha = 0.7f), Offset(lomo.left + 1f * u, y + 1.6f * u), Offset(lomo.right - 1f * u, y + 1.6f * u), 0.5f * u)
        }
        // La etiqueta, más arriba o más abajo según el álbum, y los nervios alrededor.
        val et0 = lomo.top + lomo.height * (0.1f + 0.16f * azar(s, 7))
        val etH = 31f * u
        val et = Rect(lomo.left + 2.2f * u, et0, lomo.right - 2.2f * u, et0 + etH)
        val nervs = listOf(et.top - 4f * u, et.bottom + 4f * u, et.bottom + (lomo.bottom - et.bottom) * 0.42f, et.bottom + (lomo.bottom - et.bottom) * 0.76f)
        nervs.forEach { y ->
            if (y < lomo.top + 3f * u || y > lomo.bottom - 5f * u) return@forEach
            drawRect(a.sombra(0.45f), Offset(lomo.left, y + 1.6f * u), Size(lomo.width, 1.2f * u))
            drawRect(t.hondo, Offset(lomo.left, y - 0.4f * u), Size(lomo.width, 2.4f * u))
            drawRect(t.claro, Offset(lomo.left, y - 1.2f * u), Size(lomo.width, 1.4f * u))
            drawLine(oro, Offset(lomo.left + 1f * u, y - 2.6f * u), Offset(lomo.right - 1f * u, y - 2.6f * u), 0.6f * u)
        }
        // Un florón dorado en el entrenervio de abajo: un botón con seis puntos alrededor (en los Sueños, la luna).
        val y0 = nervs[2]; val y1 = nervs[3]
        val cc = Offset(lomo.center.x, (y0 + y1) / 2)
        val rr = minOf(lomo.width * 0.22f, (y1 - y0) * 0.28f, 5.5f * u)
        if (v.noche) luna(cc, minOf(lomo.width * 0.28f, (y1 - y0) * 0.34f, 7.5f * u), a)
        else if (rr > 2.5f * u) {
            val floron = Path().apply {
                addOval(Rect(cc, rr * 0.34f))
                for (j in 0 until 6) { val an = j * 1.0472f + 0.5236f; addOval(Rect(Offset(cc.x + rr * kotlin.math.cos(an), cc.y + rr * kotlin.math.sin(an)), rr * 0.17f)) }
            }
            drawPath(corridoNavidad(floron, 0.4f * u, 0.5f * u), oroOsc)
            drawPath(floron, oro)
        }
        etiquetaDia(v, medidor, et, l)
        // La cinta de registro que cuelga por el pie, y el gastado de la tela en la cabeza.
        val xx = lomo.left + lomo.width * (0.35f + 0.3f * azar(s, 70))
        val cinta = if (ti != null && rojizo(ti)) P.FILETE else P.CARMIN
        drawLine(l.c(cinta), Offset(xx, h - 2.4f * u), Offset(xx + (azar(s, 71) - 0.5f) * 3f * u, h + 3f * u), 2f * u)
        drawLine(a.claro(0.22f), Offset(lomo.left + lomo.width * 0.2f, lomo.top + 2f * u), Offset(lomo.left + lomo.width * 0.5f, lomo.top + 2.4f * u), 0.9f * u)
        filos(lomo, a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- el regalo

    fun DrawScope.regalo(v: Volumen, medidor: TextMeasurer, a: AireNavidad, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val pl = l.pieza(hacia)
        val t = tinte(ti, l, ESTRAZA, 0.26f)
        val crudo = ti == null
        // La caja: el frente, la tapa vista desde arriba y el costado en sombra; arriba queda sitio para el lazo.
        val lado = w * 0.14f
        val tapaH = 4f * u
        val arriba = h * (0.2f + 0.06f * azar(s, 3))
        val frente = Rect(0f, arriba + tapaH, w - lado, h)
        alrededor(a, hacia, arriba, w)
        val costado = poligonoNavidad(frente.right, frente.top, w, arriba, w, h - tapaH * 0.6f, frente.right, h)
        val tapa = poligonoNavidad(0f, frente.top, lado, arriba, w, arriba, frente.right, frente.top)
        drawRect(t.cara, frente.topLeft, frente.size)
        drawPath(costado, t.hondo)
        drawPath(tapa, t.claro)
        // El dibujo del papel, según la caja: lunares, cuadros escoceses, rayas finas o liso con su filete dorado; la estraza, lisa con sus fibras.
        val motivo = if (crudo) -1 else Math.floorMod(s ushr 3, 4)
        val dibujo = if (claroDe(t.base)) l.animo(mezcla(t.base, if (rojizo(t.base)) P.FILETE else P.CARMIN, 0.6f)) else l.c(P.PAPEL, -0.1f).copy(alpha = 0.85f)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            val p = Path()
            when (motivo) {
                0 -> { var y = frente.top + 3f * u; var j = 0
                    while (y < frente.bottom && j < 60) { var x = frente.left + (if (j % 2 == 0) 3f else 7f) * u; while (x < frente.right) { p.addOval(Rect(Offset(x, y), 1.4f * u)); x += 8f * u }; y += 5f * u; j++ }
                    drawPath(p, dibujo) }
                1 -> {
                    // Cuadros escoceses: bandas anchas a lo alto y a lo ancho, y un hilo fino de otro color entre ellas.
                    val hilo = Path()
                    var x = frente.left + 2f * u; var j = 0
                    while (x < frente.right && j < 40) { p.addRect(Rect(x, frente.top, x + 4f * u, frente.bottom)); hilo.moveTo(x + 7f * u, frente.top); hilo.lineTo(x + 7f * u, frente.bottom); x += 10f * u; j++ }
                    var y = frente.top + 3f * u; j = 0
                    while (y < frente.bottom && j < 60) { p.addRect(Rect(frente.left, y, frente.right, y + 4f * u)); hilo.moveTo(frente.left, y + 7f * u); hilo.lineTo(frente.right, y + 7f * u); y += 10f * u; j++ }
                    drawPath(p, t.hondo.copy(alpha = 0.32f))
                    drawPath(hilo, dibujo.copy(alpha = dibujo.alpha * 0.7f), style = Stroke(0.7f * u)) }
                2 -> { var x = frente.left + 2f * u; var j = 0
                    while (x < frente.right && j < 60) { p.moveTo(x, frente.top); p.lineTo(x, frente.bottom); x += 4.5f * u; j++ }
                    drawPath(p, dibujo.copy(alpha = dibujo.alpha * 0.6f), style = Stroke(1.2f * u)) }
                3 -> drawRect(l.oro().copy(alpha = 0.8f), frente.deflate(3f * u).topLeft, frente.deflate(3f * u).size, style = Stroke(0.8f * u))
                else -> { for (j in 0 until 14) { val y = frente.top + frente.height * azar(s, 80 + j); p.moveTo(frente.left, y); p.lineTo(frente.left + frente.width * (0.3f + 0.6f * azar(s, 90 + j)), y + 1f * u) }
                    drawPath(p, t.hondo.copy(alpha = 0.3f), style = Stroke(0.6f * u)) }
            }
            // El volumen: la luz del lado de la ventana y la sombra del otro; el pliegue del papel en la esquina.
            drawRect(a.claro(0.2f), Offset(frente.left, frente.top), Size(frente.width * 0.12f, frente.height))
            drawRect(a.sombra(0.14f), Offset(frente.right - frente.width * 0.2f, frente.top), Size(frente.width * 0.2f, frente.height))
            drawLine(a.sombra(0.25f), Offset(frente.left, frente.bottom - 3f * u), Offset(frente.left + 5f * u, frente.bottom), 0.8f * u)
        }
        drawLine(a.claro(0.4f), Offset(0f, frente.top), Offset(frente.right, frente.top), 1f * u)
        // La cinta (o el cordel de la estraza), en cruz, de un color que contraste con el papel.
        val cintaCol = when {
            crudo -> P.CARTULINA
            claroDe(t.base) -> if (rojizo(t.base)) P.FILETE else P.CARMIN
            rojizo(t.base) -> ORO
            else -> P.CARMIN
        }
        val cx = frente.left + frente.width * (0.4f + 0.2f * azar(s, 4))
        val anchoC = if (crudo) 1.4f * u else minOf(frente.width * 0.16f, 5f * u)
        val cyC = frente.top + frente.height * (0.52f + 0.16f * azar(s, 5))
        val cc = l.c(cintaCol)
        drawRect(cc, Offset(cx - anchoC / 2, frente.top), Size(anchoC, frente.height))
        drawRect(cc, Offset(frente.left, cyC - anchoC / 2), Size(frente.width, anchoC))
        drawPath(poligonoNavidad(frente.right, cyC - anchoC / 2, w, cyC - anchoC / 2 - lado * 0.9f, w, cyC + anchoC / 2 - lado * 0.9f, frente.right, cyC + anchoC / 2), l.c(cintaCol, -0.3f))
        drawPath(poligonoNavidad(cx - anchoC / 2, frente.top, cx - anchoC / 2 + lado * 0.8f, arriba, cx + anchoC / 2 + lado * 0.8f, arriba, cx + anchoC / 2, frente.top), l.c(cintaCol, 0.1f))
        if (!crudo) drawRect(a.claro(0.3f), Offset(cx - anchoC / 2, frente.top), Size(1f * u, frente.height))
        // El lazo arriba (o el nudo del cordel con su ramita de abeto).
        val nudo = Offset(cx + lado * 0.4f, arriba + 1f * u)
        if (crudo) {
            ramaAbetoNavidad(nudo + Offset(-w * 0.35f, w * 0.05f), nudo + Offset(w * 0.4f, -h * 0.12f), pl, w * 0.12f)
            drawCircle(cc, 2f * u, nudo)
            drawArc(cc, 200f, 200f, false, Offset(nudo.x - 6f * u, nudo.y - 5f * u), Size(6f * u, 6f * u), style = Stroke(1.2f * u))
            drawArc(cc, 140f, 200f, false, Offset(nudo.x, nudo.y - 5f * u), Size(6f * u, 6f * u), style = Stroke(1.2f * u))
        } else lazoNavidad(nudo, minOf(w * 0.75f, 30f * u) * (0.85f + 0.3f * azar(s, 6)), cintaCol, pl, 0.55f)
        // La etiqueta de regalo, atada al lazo con su hilo, del lado contrario a la cinta.
        val etW = (w - lado - 6f * u).coerceAtLeast(18f * u)
        val etH = 30f * u
        val etX = (frente.width - etW) / 2f + (if (cx > frente.center.x) -1.5f else 1.5f) * u
        val etY = (frente.top + frente.height * (0.08f + 0.22f * azar(s, 9))).coerceAtMost(h - etH - 4f * u)
        val et = Rect(etX, etY, etX + etW, etY + etH)
        drawLine(l.c(P.CARTULINA_OSC), nudo, Offset(et.left + 4f * u, et.top + 4f * u), 0.8f * u)
        val giro = (azar(s, 8) - 0.5f) * 8f
        rotate(giro, Offset(et.left + 4f * u, et.top + 4f * u)) {
            etiquetaDia(v, medidor, et, l, 5f * u)
            drawCircle(l.c(P.TINTA).copy(alpha = 0.6f), 1.2f * u, Offset(et.left + 4f * u, et.top + 4f * u))
        }
        if (v.noche) luna(Offset(frente.left + frente.width * 0.3f, h - h * 0.12f), minOf(frente.width * 0.2f, 7f * u), a)
        filos(Rect(0f, arriba, w, h), a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- el calcetín de lana

    fun DrawScope.calcetin(v: Volumen, medidor: TextMeasurer, a: AireNavidad, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val pl = l.pieza(hacia)
        val t = tinte(ti, l, LANA_CRUDA, 0.24f)
        // Hacia dónde mira el pie, y cuánto se ladea al colgar.
        val izq = azar(s, 3) > 0.5f
        val giro = (azar(s, 4) - 0.5f) * 5f
        val pierna = w * 0.5f
        val px0 = if (izq) w - pierna - 2f * u else 2f * u
        val px1 = px0 + pierna
        val gancho = Offset((px0 + px1) / 2, 2f * u)
        val puno0 = h * 0.11f
        val puno1 = h * 0.4f
        val talonY = h * 0.71f
        val fin = h * (0.91f + 0.03f * azar(s, 5))
        // El gancho de latón bajo la tabla, y la presilla de lana que baja al puño.
        drawRect(a.sombra(0.4f), Offset(gancho.x - 2f * u, 0f), Size(4f * u, 2.4f * u))
        drawArc(pl.c(P.LATON), 0f, 200f, false, Offset(gancho.x - 3f * u, gancho.y), Size(6f * u, 6f * u), style = Stroke(1.4f * u))
        val presilla = Path().apply { moveTo(gancho.x, gancho.y + 6f * u); lineTo(gancho.x - 3f * u, puno0 + 2f * u); moveTo(gancho.x, gancho.y + 6f * u); lineTo(gancho.x + 3f * u, puno0 + 2f * u) }
        drawPath(presilla, l.c(P.LANA_CREMA, -0.1f), style = Stroke(1.6f * u))
        rotate(giro, gancho) {
            // La silueta, pensada con la puntera a la derecha y volteada si mira a la izquierda: la pierna, el empeine que baja,
            // la puntera redonda, la planta y el talón redondeado atrás.
            fun fx(x: Float) = if (izq) w - x else x
            val q0 = if (izq) w - px1 else px0
            val q1 = q0 + pierna
            val radioT = minOf(w * 0.24f, (fin - talonY) * 0.9f)
            val forma = Path().apply {
                moveTo(fx(q0), puno0)
                lineTo(fx(q1), puno0)
                lineTo(fx(q1), talonY - h * 0.03f)
                quadraticTo(fx(q1 + w * 0.04f), talonY + h * 0.02f, fx(w * 0.8f), talonY + h * 0.03f)
                cubicTo(fx(w * 1.0f), talonY + h * 0.035f, fx(w * 1.0f), fin, fx(w * 0.8f), fin)
                lineTo(fx(q0 + radioT), fin)
                quadraticTo(fx(q0), fin, fx(q0), fin - radioT)
                close()
            }
            // La sombra en el fondo (de día, a la derecha y abajo; de noche, lejos de la luz) y lo que asoma por arriba.
            drawPath(corridoNavidad(forma, if (hacia < 0f) 5f * u else -5f * u, 4f * u), a.sombra(if (a.oscuro) 0.4f else 0.24f))
            asoma(s, v.lleno, px0, px1, puno0, pl, l)
            drawPath(forma, t.cara)
            val contraste = if (claroDe(t.base)) P.LANA_ROJA else P.LANA_CREMA
            clipPath(forma) {
                // El punto: columnas de puntos en el tono hondo, y las rayas de otro color en algunos.
                val punto = Path()
                var y = puno1 + 2f * u
                var j = 0
                while (y < fin + 4f * u && j < 80) {
                    var x = (if (j % 2 == 0) 0f else 1.8f * u)
                    while (x < w + 2f * u) { punto.moveTo(x, y); punto.lineTo(x + 1.3f * u, y + 2.4f * u); punto.lineTo(x + 2.6f * u, y); x += 3.6f * u }
                    y += 3f * u; j++
                }
                drawPath(punto, t.hondo.copy(alpha = 0.3f), style = Stroke(0.7f * u))
                val rayas = Math.floorMod(s ushr 4, 3)
                val rayaCol = if (claroDe(t.base)) (if (rojizo(t.base)) P.FILETE else P.CARMIN) else P.LANA_CREMA
                if (rayas > 0) for (q in 0 until rayas + 1) {
                    val yr = puno1 + (talonY - puno1) * (0.16f + 0.2f * q)
                    drawRect(l.c(rayaCol, -0.05f), Offset(0f, yr), Size(w, 3.4f * u))
                }
                // El talón (el casquete de atrás) y la puntera, de otro color, con la costura de su borde.
                val talon = Path().apply { addOval(Rect(fx(q0) - radioT * 1.1f, fin - radioT * 1.5f, fx(q0) + radioT * 1.1f, fin + radioT * 0.5f)) }
                val puntera = Path().apply { addOval(Rect(fx(w * 0.86f) - w * 0.2f, talonY - h * 0.01f, fx(w * 0.86f) + w * 0.2f, fin + h * 0.03f)) }
                drawPath(talon, l.c(contraste, -0.04f))
                drawPath(puntera, l.c(contraste, -0.04f))
                drawPath(talon, t.hondo.copy(alpha = 0.5f), style = Stroke(0.9f * u))
                drawPath(puntera, t.hondo.copy(alpha = 0.5f), style = Stroke(0.9f * u))
                // El volumen: la pierna es un tubo blando (claro del lado de la luz, hondo del otro), y el pie, más hondo abajo.
                val lo = Rect(fx(if (izq) q1 else q0), puno1, fx(if (izq) q0 else q1), talonY)
                drawRect(a.claro(0.24f), Offset(if (hacia < 0f) lo.left else lo.right - lo.width * 0.22f, lo.top), Size(lo.width * 0.22f, h))
                drawRect(a.sombra(0.22f), Offset(if (hacia < 0f) lo.right - lo.width * 0.3f else lo.left, lo.top), Size(lo.width * 0.3f, h))
                drawRect(a.sombra(0.2f), Offset(0f, fin - h * 0.03f), Size(w, h * 0.06f))
                drawRect(a.claro(0.18f), Offset(0f, talonY + h * 0.03f), Size(w, 1.4f * u))
            }
            // El borde de la silueta, para que se lea a tamaño de teléfono.
            drawPath(forma, t.hondo.copy(alpha = 0.65f), style = Stroke(1f * u))
            // El puño vuelto, de punto crema y más ancho que la pierna, con sus canalés, su sombra en la pierna y su luz.
            val pu = Rect(minOf(px0, px1) - w * 0.08f, puno0, maxOf(px0, px1) + w * 0.08f, puno1)
            drawRect(a.sombra(0.35f), Offset(pu.left + 1.5f * u, pu.bottom), Size(pu.width - 3f * u, 2.6f * u))
            drawRoundRect(l.c(P.LANA_CREMA), pu.topLeft, pu.size, CornerRadius(2f * u))
            val canal = Path()
            var xc = pu.left + 2.5f * u
            while (xc < pu.right - 1f * u) { canal.moveTo(xc, pu.top + 1f * u); canal.lineTo(xc, pu.bottom - 1f * u); xc += 3f * u }
            drawPath(canal, l.c(P.LANA_CREMA, -0.25f).copy(alpha = 0.5f), style = Stroke(0.8f * u))
            drawRect(a.claro(0.3f), Offset(if (hacia < 0f) pu.left else pu.right - pu.width * 0.2f, pu.top), Size(pu.width * 0.2f, pu.height))
            drawRect(a.sombra(0.2f), Offset(if (hacia < 0f) pu.right - pu.width * 0.25f else pu.left, pu.top), Size(pu.width * 0.25f, pu.height))
            drawRoundRect(t.hondo.copy(alpha = 0.4f), pu.topLeft, pu.size, CornerRadius(2f * u), style = Stroke(0.8f * u))
            filos(Rect(px0, puno0, px1, fin), a, k, hacia, luna)
            // La etiqueta cosida al puño, con sus puntadas de hilo rojo.
            val etW = (pierna + w * 0.12f).coerceAtLeast(18f * u).coerceAtMost(w - 2f * u)
            val cy = (puno0 + puno1) / 2
            val cxE = ((px0 + px1) / 2).coerceIn(etW / 2 + 1f * u, w - etW / 2 - 1f * u)
            val et = Rect(cxE - etW / 2, cy - 15f * u, cxE + etW / 2, cy + 15f * u)
            etiquetaDia(v, medidor, et, l)
            listOf(et.left + 2f * u, et.right - 2f * u).forEach { xx -> drawLine(l.c(P.CARMIN), Offset(xx, et.top + 2f * u), Offset(xx, et.top + 5f * u), 1f * u) }
            if (v.noche) luna(Offset((px0 + px1) / 2, talonY - (talonY - et.bottom) * 0.45f), minOf(pierna * 0.3f, 7f * u), a)
        }
    }

    /** Lo que asoma por la boca del calcetín: una ramita de abeto, una de acebo o una carta enrollada (con más palabras, dos). */
    private fun DrawScope.asoma(s: Int, lleno: Float, x0: Float, x1: Float, y: Float, pl: LuzNavidad, l: Luz) {
        val u = density
        val ancho = x1 - x0
        val cuantas = if (lleno > 0.45f) 2 else 1
        for (i in 0 until cuantas) {
            val tipo = Math.floorMod(s + i * 5, 3)
            val cx = x0 + ancho * (if (cuantas == 1) 0.5f else 0.3f + 0.4f * i)
            when (tipo) {
                0 -> ramaAbetoNavidad(Offset(cx, y + 6f * u), Offset(cx + (i - 0.5f) * ancho * 0.6f, y - 18f * u), pl, ancho * 0.28f)
                1 -> aceboNavidad(Offset(cx, y - 2f * u), minOf(ancho * 0.7f, 14f * u), -90f + (i - 0.5f) * 30f, pl)
                else -> {
                    val r = Rect(cx - 3f * u, y - 16f * u, cx + 3f * u, y + 4f * u)
                    rotate(12f - 24f * i, Offset(cx, y)) {
                        drawRect(l.c(P.PAPEL), r.topLeft, r.size)
                        drawRect(l.a.sombra(0.25f), Offset(r.right - 2f * u, r.top), Size(2f * u, r.height))
                        drawOval(l.c(P.PAPEL, -0.15f), Offset(r.left, r.top - 1.4f * u), Size(r.width, 2.8f * u))
                        drawRect(l.c(P.CARMIN), Offset(r.left, r.top + r.height * 0.4f), Size(r.width, 1.6f * u))
                    }
                }
            }
        }
    }
}
