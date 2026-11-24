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
 * Las entradas del estante de Napoleónico ([MuebleEstanteNapoleon]), tres objetos del alojamiento según la semilla: el tomo (el
 * diario del oficial encuadernado en tafilete, de pie, con el lomo redondo, sus nervios, los filetes y florones dorados, la
 * cabezada de seda y el tejuelo de otro color con el día dorado), la caja de archivo (alta, forrada de papel jaspeado, con sus
 * cantos y esquinas de becerro, la tapa que sobresale, el portaetiquetas de latón con la tarjeta del día y la argolla; alguna
 * con la tapa entreabierta y los papeles asomando) y el ridículo de seda (la bolsita de mano fruncida por su cordón, con el
 * volante del frunce, el galón tejido, la borla dorada y la tarjeta del día atada al cordón). El ánimo es el tafilete, el papel
 * y la seda, en todo el cuerpo ([tonoDeDiaNapoleon]); sin ánimo, becerro sin teñir. En los Sueños, una media luna de plata con
 * su disco apagado.
 */
internal object VolumenEstanteNapoleon {
    private val P = PaletaNapoleon

    /** El oro de los dorados y el becerro sin teñir de lo que no tiene ánimo. */
    private val ORO = Color(0xFFE4BE5E)
    private val ORO_OSC = Color(0xFF9A7426)
    private val CRUDO = Color(0xFF8A6A48)

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.38f, 0.78f, 0.42f, 0.08f, -0.22f, -0.5f, -0.78f)

    /** La luz que le toca a una entrada: la del momento ([k]) y, en los Sueños oscuros, la luna de la ventana ([luna]). */
    private class Luz(val a: AireNapoleon, val k: Float, val luna: Float) {
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
        /** El dorado: de día, oro; de noche, se enciende cerca de la llama y se apaga lejos (sin perder el contraste). */
        fun oro(col: Color = ORO): Color = if (a.oscuro) mezcla(a.luz(col, k), Color(0xFFFFD98A), 0.35f * k) else c(col)
    }

    /** Los colores de un tinte con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(t: Color?, l: Luz, brillo: Float = 0.3f): Tinte {
        val m = t ?: CRUDO
        val claro = mezcla(m, luzMueble(1f), if (t == null) 0.2f else brillo)
        val hondo = mezcla(m, sombraMueble(1f), 0.55f)
        return if (t != null) Tinte(l.animo(m), l.animo(claro), l.animo(hondo)) else Tinte(l.c(m), l.c(claro), l.c(hondo))
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

    /** El mes corto en español, como «nov.». */
    private fun mesCorto(mes: String): String {
        val m = mes.substringBefore(' ').trim()
        return if (m.length > 4) m.take(3).lowercase() + "." else m.lowercase()
    }

    /**
     * El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp sin achicarse) y el mes debajo, en una
     * de 9.5 dp; así el día sale del mismo tamaño en las tres entradas.
     */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left, y0, r.right, y0 + hd), P.LETRA_TITULO, tinta, DIA, peso = FontWeight.SemiBold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left, y0 + hd - 1.4f * u, r.right, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.88f), MES, minimo = 5f)
    }

    // ---------------------------------------------------------------- lo compartido (armado una vez por tamaño)

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

    /** El grano del tafilete (rayitas cortas en todas direcciones), en una baldosa de 60 × 120 dp armada una vez por densidad. */
    private var densidadGrano = -1f
    private var caminoGrano = Path()
    private fun grano(u: Float): Path {
        if (u != densidadGrano) {
            val p = Path()
            for (j in 0 until 260) {
                val x = 60f * u * azar(8101, j); val y = 120f * u * azar(8102, j)
                val an = azar(8103, j) * 3.1416f
                val l = (1.2f + 1.6f * azar(8104, j)) * u
                p.moveTo(x, y); p.lineTo(x + l * kotlin.math.cos(an), y + l * kotlin.math.sin(an))
            }
            caminoGrano = p; densidadGrano = u
        }
        return caminoGrano
    }

    /**
     * El jaspeado del papel «de piedra», en una baldosa de 70 × 150 dp armada una vez por densidad: piedras redondeadas muy
     * juntas (entre ellas asoma el fondo, que hace de vena), unas pocas de otro color y algunas venas finas que las cruzan.
     */
    private var densidadJaspe = -1f
    private var piedras = Path()
    private var piedras2 = Path()
    private var venas = Path()
    private fun jaspe(u: Float) {
        if (u == densidadJaspe) return
        fun piedra(cx: Float, cy: Float, rx: Float, ry: Float, j: Int): Path {
            val pts = Array(8) { s0 ->
                val an = s0 * 0.785f
                val e = 0.8f + 0.32f * azar(8205 + s0, j)
                Offset(cx + rx * e * kotlin.math.cos(an), cy + ry * e * kotlin.math.sin(an))
            }
            return Path().apply {
                val m0 = Offset((pts[7].x + pts[0].x) / 2, (pts[7].y + pts[0].y) / 2)
                moveTo(m0.x, m0.y)
                for (q in 0 until 8) { val a0 = pts[q]; val b0 = pts[(q + 1) % 8]; quadraticTo(a0.x, a0.y, (a0.x + b0.x) / 2, (a0.y + b0.y) / 2) }
                close()
            }
        }
        val pi = Path(); val p2 = Path(); val ve = Path()
        for (j in 0 until 150) {
            val cx = 70f * u * azar(8201, j); val cy = 150f * u * azar(8202, j)
            val rx = (2.4f + 3.2f * azar(8203, j)) * u; val ry = rx * (0.6f + 0.45f * azar(8204, j))
            if (j % 5 == 0) p2.addPath(piedra(cx, cy, rx * 0.8f, ry * 0.8f, j)) else pi.addPath(piedra(cx, cy, rx, ry, j))
        }
        for (j in 0 until 16) {
            val x0 = 70f * u * azar(8221, j); val y0 = 150f * u * azar(8222, j)
            ve.moveTo(x0, y0)
            ve.cubicTo(x0 + 8f * u, y0 + 4f * u * (azar(8223, j) - 0.5f), x0 + 12f * u, y0 + 10f * u, x0 + 22f * u * azar(8224, j), y0 + 18f * u)
        }
        piedras = pi; piedras2 = p2; venas = ve; densidadJaspe = u
    }

    /**
     * La luna de los Sueños: el disco apagado de la luna (la luz cenicienta) y la media luna de plata encima, con su canto oscuro
     * y su sombra; así se lee luna y no emblema.
     */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireNapoleon) {
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

    /** Lo de todas: la sombra que dejan en el paño por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireNapoleon, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.45f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.45f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche) y el roce frío de la luna. */
    private fun DrawScope.filos(cuerpo: Rect, a: AireNapoleon, k: Float, hacia: Float, luna: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.7f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (luna > 0f) drawRect(a.lunar(0.6f * luna), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    /** Una tarjeta de cartulina en [et] con el día y el mes, su sombra y su borde gastado. */
    private fun DrawScope.tarjeta(v: Volumen, medidor: TextMeasurer, et: Rect, l: Luz, a: AireNapoleon, giro: Float = 0f) {
        val u = density
        rotate(giro, et.center) {
            drawRect(a.sombra(0.38f), Offset(et.left + 1f * u, et.top + 1.3f * u), et.size)
            drawRect(l.etiqueta(P.CARTULINA), et.topLeft, et.size)
            drawRect(l.etiqueta(P.CARTULINA_OSC).copy(alpha = 0.5f), Offset(et.left, et.bottom - et.height * 0.12f), Size(et.width, et.height * 0.12f))
            drawRect(l.etiqueta(P.CARTULINA_OSC).copy(alpha = 0.4f), et.topLeft, Size(et.width * 0.07f, et.height))
            drawRect(a.luz(P.TINTA, l.k).copy(alpha = 0.45f), et.deflate(1.4f * u).topLeft, et.deflate(1.4f * u).size, style = Stroke(0.5f * u))
            diaYMes(v, medidor, et.deflate(1.6f * u), if (a.oscuro) Color(0xFF140C06) else a.luz(P.TINTA, maxOf(l.k, 0.75f)))
        }
    }

    // ---------------------------------------------------------------- el tomo

    /** El color del tejuelo: oscuro y de otro tono que el tafilete (para que el día dorado se lea). */
    private fun tejueloPara(t: Color?): Color {
        if (t == null) return Color(0xFF7A1A24)
        val l = t.red * 0.3f + t.green * 0.59f + t.blue * 0.11f
        return when {
            t.blue > t.red + 0.05f -> Color(0xFF7A1824)
            l > 0.4f -> Color(0xFF2A2019)
            else -> Color(0xFF1E3E2C)
        }
    }

    fun DrawScope.tomo(v: Volumen, medidor: TextMeasurer, a: AireNapoleon, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val ceja = 2.6f * u
        alrededor(a, hacia, ceja)
        val t = tinte(ti, l)
        // Los cantos de las tapas que asoman arriba y abajo del lomo (las cejas), en el mismo tafilete, más hondos.
        drawRoundRect(t.hondo, Offset(0f, 0f), Size(w, h), CornerRadius(1.6f * u))
        // El lomo redondo, por franjas, con la cofia arriba.
        val lomo = Rect(0.8f * u, ceja, w - 0.8f * u, h - ceja * 0.6f)
        redondo(lomo, t, hacia)
        // El grano del tafilete y su roce.
        clipRect(lomo.left, lomo.top, lomo.right, lomo.bottom) {
            translate(-60f * u * azar(s, 2) * 0.5f, -120f * u * azar(s, 3) * 0.3f) {
                drawPath(grano(u), t.hondo.copy(alpha = 0.35f), style = Stroke(0.6f * u))
            }
        }
        for (j in 0 until 3) {
            val y = lomo.top + lomo.height * (0.1f + 0.85f * azar(s, 50 + j))
            drawLine(a.claro(0.18f), Offset(lomo.left + lomo.width * 0.15f, y), Offset(lomo.left + lomo.width * (0.3f + 0.4f * azar(s, 58 + j)), y + 0.6f * u), 0.8f * u)
        }
        // La cabezada de seda a rayas, que asoma bajo la cofia, y la cofia.
        val cab = Rect(lomo.left + lomo.width * 0.1f, 0.4f * u, lomo.right - lomo.width * 0.1f, ceja + 0.6f * u)
        var x = cab.left; var j = 0
        val hilos = listOf(l.c(P.PAPEL, -0.1f), l.c(if (s and 4 == 0) P.LACRE else P.VERDE, 0f))
        while (x < cab.right) { drawRect(hilos[j % 2], Offset(x, cab.top), Size(minOf(1.6f * u, cab.right - x), cab.height)); x += 1.6f * u; j++ }
        drawRoundRect(t.claro, Offset(lomo.left, ceja - 0.8f * u), Size(lomo.width, 2.2f * u), CornerRadius(1f * u))
        // Las ruedas doradas de la cabeza y el pie.
        val oro = l.oro(); val oroOsc = l.oro(ORO_OSC)
        listOf(lomo.top + 3.6f * u, lomo.bottom - 3.6f * u).forEach { y ->
            drawLine(oro, Offset(lomo.left + 1f * u, y), Offset(lomo.right - 1f * u, y), 0.8f * u)
            drawLine(oro.copy(alpha = 0.7f), Offset(lomo.left + 1f * u, y + 1.6f * u), Offset(lomo.right - 1f * u, y + 1.6f * u), 0.5f * u)
        }
        // El tejuelo, alto o más abajo según el tomo, y los nervios alrededor.
        val alto = azar(s, 7) > 0.35f
        val tj0 = h * (if (alto) 0.11f else 0.2f)
        val tjH = 30f * u
        val tj1 = tj0 + tjH
        val resto = (h - ceja - 6f * u) - (tj1 + 6f * u)
        val nervs = ArrayList<Float>()
        nervs.add(tj0 - 3.6f * u)
        nervs.add(tj1 + 3.6f * u)
        val cuantos = if (resto > 60f * u) 3 else 2
        for (i in 1..cuantos) nervs.add(tj1 + 3.6f * u + resto * i / (cuantos + 0.6f))
        if (!alto) nervs.add(0, lomo.top + (tj0 - 3.6f * u - lomo.top) * 0.45f)
        nervs.forEach { y ->
            if (y < lomo.top + 2f * u || y > lomo.bottom - 4f * u) return@forEach
            drawRect(a.sombra(0.45f), Offset(lomo.left, y + 1.6f * u), Size(lomo.width, 1.2f * u))
            drawRect(t.hondo, Offset(lomo.left, y - 0.4f * u), Size(lomo.width, 2.4f * u))
            drawRect(t.claro, Offset(lomo.left, y - 1.2f * u), Size(lomo.width, 1.4f * u))
            drawRect(a.brillo(0.3f, k), Offset(if (hacia < 0f) lomo.left + lomo.width * 0.12f else lomo.right - lomo.width * 0.4f, y - 1f * u), Size(lomo.width * 0.28f, 0.8f * u))
            drawLine(oro, Offset(lomo.left + 1f * u, y - 2.4f * u), Offset(lomo.right - 1f * u, y - 2.4f * u), 0.6f * u)
            drawLine(oro, Offset(lomo.left + 1f * u, y + 3f * u), Offset(lomo.right - 1f * u, y + 3f * u), 0.6f * u)
        }
        // Los florones dorados de los entrenervios de abajo: un rombo de puntos con su botón.
        val abajo = nervs.filter { it > tj1 }.sorted()
        abajo.zipWithNext().forEachIndexed { i, (y0, y1) ->
            val c = Offset(lomo.center.x + (if (hacia < 0f) -0.06f else 0.06f) * lomo.width, (y0 + y1) / 2 + 0.3f * u)
            if (v.noche && i == 0) return@forEachIndexed
            val rr = minOf(lomo.width * 0.2f, (y1 - y0) * 0.25f, 4.6f * u)
            drawCircle(oroOsc, rr * 0.36f, c + Offset(0.3f * u, 0.4f * u))
            drawCircle(oro, rr * 0.32f, c)
            listOf(Offset(0f, -rr), Offset(rr, 0f), Offset(0f, rr), Offset(-rr, 0f), Offset(rr * 0.6f, -rr * 0.6f), Offset(-rr * 0.6f, rr * 0.6f),
                Offset(rr * 0.6f, rr * 0.6f), Offset(-rr * 0.6f, -rr * 0.6f)).forEach { o -> drawCircle(oro, rr * 0.13f, c + o) }
        }
        // El tejuelo de tafilete de otro color, con su filete dorado, y el día y el mes dorados.
        val tj = Rect(lomo.left + 1.4f * u, tj0, lomo.right - 1.4f * u, tj1)
        val tjc = l.c(tejueloPara(ti), 0f)
        drawRect(a.sombra(0.4f), tj.topLeft + Offset(0.6f * u, 0.8f * u), tj.size)
        drawRect(tjc, tj.topLeft, tj.size)
        drawRect(mezcla(tjc, luzMueble(1f), if (a.oscuro) 0.05f else 0.16f), tj.topLeft, Size(tj.width * 0.3f, tj.height))
        drawRect(oro.copy(alpha = 0.85f), tj.deflate(1.2f * u).topLeft, tj.deflate(1.2f * u).size, style = Stroke(0.5f * u))
        diaYMes(v, medidor, tj.deflate(1.4f * u), oro)
        // Los registros de cinta que cuelgan por el pie, más con más palabras.
        val cintas = (v.lleno * 2.6f).toInt().coerceIn(0, 2)
        val colores = listOf(P.LACRE, P.VERDE)
        for (q in 0 until cintas) {
            val xx = lomo.left + lomo.width * (0.32f + 0.26f * q)
            val fin = Offset(xx + (azar(s, 70 + q) - 0.5f) * 3f * u, h + 2.6f * u)
            drawLine(l.c(colores[(q + (s and 1)) % 2]), Offset(xx, h - ceja), fin, 1.8f * u)
            drawLine(a.sombra(0.3f), Offset(xx + 1f * u, h - ceja), fin + Offset(1f * u, 0f), 0.6f * u)
        }
        // La luna de los Sueños, en el primer entrenervio de abajo.
        if (v.noche && abajo.size >= 2) luna(Offset(lomo.center.x, (abajo[0] + abajo[1]) / 2 + 0.5f * u), minOf(lomo.width * 0.26f, 5.6f * u), a)
        filos(lomo, a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- la caja de archivo

    fun DrawScope.caja(v: Volumen, medidor: TextMeasurer, a: AireNapoleon, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val t = tinte(ti, l, 0.3f)
        // Entreabierta (la tapa de arriba levantada y los papeles asomando) en una de cada cuatro, más si va llena.
        val abierta = azar(s, 15) + 0.35f * v.lleno > 0.82f
        val cara = 3.2f * u
        alrededor(a, hacia, 2f * u)
        val cuerpo = Rect(0.6f * u, cara, w - 0.6f * u, h)
        val becerro = l.c(if ((s shr 3) and 1 == 0) P.BECERRO else P.CUERO_OSC, 0f)
        // Los papeles que asoman por arriba si va entreabierta: hojas plegadas de distinto alto.
        if (abierta) {
            val hojas = 2 + (v.lleno * 2f).toInt().coerceIn(0, 2)
            for (q in 0 until hojas) {
                val x0 = cuerpo.left + cuerpo.width * (0.1f + 0.2f * q)
                val hw = cuerpo.width * 0.5f
                val top = -1.5f * u + 4f * u * azar(s, 30 + q)
                rotate((azar(s, 33 + q) - 0.5f) * 8f, Offset(x0 + hw / 2, cara)) {
                    drawRect(l.etiqueta(mezcla(P.PAPEL, P.CARTULINA, 0.3f * q)), Offset(x0, top), Size(hw, cara - top + 6f * u))
                    drawRect(a.sombra(0.25f), Offset(x0 + hw * 0.7f, top), Size(hw * 0.3f, cara - top + 6f * u))
                }
            }
        }
        // La cara de arriba, vista apenas (el canto de la tapa, de becerro), y su filo de luz.
        val ya = if (abierta) 2f * u else 0f
        drawRect(mezcla(becerro, luzMueble(1f), if (a.oscuro) 0.05f else 0.18f), Offset(cuerpo.left, ya), Size(cuerpo.width, cara))
        drawRect(a.brillo(0.4f, k), Offset(cuerpo.left, ya), Size(cuerpo.width, 0.8f * u))
        // El frente: el papel jaspeado del color del ánimo (el fondo, más hondo, hace de vena entre las piedras).
        drawRect(mezcla(t.cara, t.hondo, 0.55f), cuerpo.topLeft, cuerpo.size)
        jaspe(u)
        val acento = l.c(if (ti != null && ti.red > ti.blue) Color(0xFFD8B060) else Color(0xFFB8463A), 0f)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            translate(-70f * u * 0.5f * azar(s, 4), -150f * u * 0.4f * azar(s, 5)) {
                drawPath(piedras, mezcla(t.cara, t.claro, 0.2f))
                drawPath(piedras2, mezcla(acento, t.cara, 0.35f).copy(alpha = 0.8f))
                drawPath(piedras, t.hondo.copy(alpha = 0.6f), style = Stroke(0.6f * u))
                drawPath(venas, mezcla(t.hondo, l.c(P.TINTA), 0.4f).copy(alpha = 0.55f), style = Stroke(0.5f * u))
            }
            // La luz de un lado y la sombra del otro, en franjas francas.
            val luzX = if (hacia < 0f) cuerpo.left else cuerpo.right - cuerpo.width * 0.28f
            drawRect(a.brillo(0.16f, k), Offset(luzX, cuerpo.top), Size(cuerpo.width * 0.28f, cuerpo.height))
            drawRect(a.sombra(0.26f), Offset(if (hacia < 0f) cuerpo.right - cuerpo.width * 0.24f else cuerpo.left, cuerpo.top), Size(cuerpo.width * 0.24f, cuerpo.height))
        }
        // El lomo de becerro de arriba (una banda a lo ancho), los cantos a lo alto y las esquinas de abajo.
        val banda = cuerpo.height * 0.09f
        drawRect(becerro, cuerpo.topLeft, Size(cuerpo.width, banda))
        drawRect(a.sombra(0.4f), Offset(cuerpo.left, cuerpo.top + banda), Size(cuerpo.width, 1f * u))
        drawRect(a.brillo(0.3f, k), Offset(cuerpo.left, cuerpo.top + 0.6f * u), Size(cuerpo.width, 0.7f * u))
        val bw = 2.6f * u
        drawRect(becerro, Offset(cuerpo.left, cuerpo.top), Size(bw, cuerpo.height))
        drawRect(becerro, Offset(cuerpo.right - bw, cuerpo.top), Size(bw, cuerpo.height))
        drawRect(a.brillo(0.25f, k), Offset(if (hacia < 0f) cuerpo.left else cuerpo.right - 0.8f * u, cuerpo.top), Size(0.8f * u, cuerpo.height))
        val esq = minOf(cuerpo.width * 0.32f, 10f * u)
        listOf(true, false).forEach { izq ->
            val x = if (izq) cuerpo.left else cuerpo.right
            val sx = if (izq) 1f else -1f
            drawPath(poligonoNapoleon(x, cuerpo.bottom - esq, x + sx * esq, cuerpo.bottom, x, cuerpo.bottom), becerro)
            drawLine(a.sombra(0.4f), Offset(x + sx * esq, cuerpo.bottom), Offset(x, cuerpo.bottom - esq), 0.8f * u)
        }
        // El roce del uso en el borde de abajo.
        drawRect(a.claro(0.22f), Offset(cuerpo.left + esq, cuerpo.bottom - 2f * u), Size(cuerpo.width - 2 * esq, 1f * u))
        // El portaetiquetas de latón con la tarjeta del día, bajo el lomo.
        val py = cuerpo.top + banda + cuerpo.height * (0.04f + 0.04f * azar(s, 13))
        val placa = Rect(cuerpo.left + bw + 0.6f * u, py, cuerpo.right - bw - 0.6f * u, py + 33f * u)
        drawRect(a.sombra(0.45f), placa.topLeft + Offset(1f * u, 1.4f * u), placa.size)
        drawRect(l.c(P.LATON_OSC), placa.topLeft, placa.size)
        drawRect(l.c(P.LATON), placa.topLeft + Offset(0.7f * u, 0.7f * u), Size(placa.width - 1.4f * u, placa.height - 1.4f * u))
        drawRect(a.brillo(0.5f, k), placa.topLeft + Offset(0.7f * u, 0.7f * u), Size(placa.width - 1.4f * u, 0.8f * u))
        val card = placa.deflate(2f * u)
        drawRect(l.etiqueta(P.CARTULINA), card.topLeft, card.size)
        drawRect(a.sombra(0.25f), card.topLeft, Size(card.width, 1.6f * u))
        diaYMes(v, medidor, card.deflate(0.6f * u), if (a.oscuro) Color(0xFF140C06) else a.luz(P.TINTA, maxOf(k, 0.75f)))
        // La argolla de latón colgando de su botón, con su sombra en el papel.
        val bo = Offset(cuerpo.center.x, placa.bottom + (cuerpo.bottom - placa.bottom) * (0.36f + 0.1f * azar(s, 14)))
        val ra = minOf(cuerpo.width * 0.22f, 7f * u)
        drawCircle(a.sombra(0.4f), 2.6f * u, bo + Offset(1f * u, 1.2f * u))
        drawCircle(l.c(P.LATON_OSC), 2.6f * u, bo)
        drawCircle(l.c(P.LATON), 1.9f * u, bo)
        val aro = Rect(bo.x - ra * 0.75f, bo.y, bo.x + ra * 0.75f, bo.y + ra * 1.6f)
        drawOval(a.sombra(0.4f), aro.topLeft + Offset(1.4f * u, 1.8f * u), aro.size, style = Stroke(1.7f * u))
        drawOval(l.c(P.LATON), aro.topLeft, aro.size, style = Stroke(1.7f * u))
        drawArc(a.brillo(0.6f, k), 150f, 90f, false, aro.topLeft, aro.size, style = Stroke(0.7f * u))
        drawCircle(a.brillo(0.7f, k), 0.7f * u, bo - Offset(0.6f * u, 0.6f * u))
        // La luna de los Sueños, debajo de la argolla.
        if (v.noche) luna(Offset(cuerpo.center.x, minOf(aro.bottom + 8f * u, cuerpo.bottom - 8f * u)), minOf(cuerpo.width * 0.2f, 5f * u), a)
        filos(cuerpo, a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- el ridículo de seda

    fun DrawScope.ridiculo(v: Volumen, medidor: TextMeasurer, a: AireNapoleon, k: Float, hacia: Float, ti: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val t = tinte(ti, l, 0.45f)
        val lado = if (azar(s, 5) > 0.5f) 1f else -1f
        val cuello = h * (0.25f + 0.04f * azar(s, 3))
        val volante = cuello - h * (0.07f + 0.02f * azar(s, 4))
        val hombro = h * (0.5f + 0.05f * azar(s, 6))
        alrededor(a, hacia, volante)
        // El asa: la vuelta del cordón que sube del frunce y se apoya, ladeada, en el paño de atrás.
        val oroCordon = l.oro(mezcla(ORO, P.LATON, 0.4f))
        val asa = Path().apply {
            moveTo(w * 0.36f, cuello)
            cubicTo(w * (0.3f + 0.12f * lado), h * 0.02f, w * (0.62f + 0.12f * lado), -h * 0.01f, w * (0.64f + 0.06f * lado), cuello)
        }
        drawPath(Path().apply { addPath(asa, Offset(1.4f * u, 1.6f * u)) }, a.sombra(0.35f), style = Stroke(1.6f * u))
        drawPath(asa, oroCordon, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(asa, a.brillo(0.4f, k), style = Stroke(0.5f * u))
        // Las cartas plegadas que asoman por la boca, más cuanto más se escribió.
        val cartas = (v.lleno * 2.6f).toInt().coerceIn(0, 2)
        for (q in 0 until cartas) {
            val x0 = w * (if (q == 0) 0.26f else 0.46f)
            rotate(if (q == 0) -10f else 9f, Offset(x0 + w * 0.14f, cuello)) {
                drawRect(l.etiqueta(mezcla(P.PAPEL, P.CARTULINA, 0.35f * q)), Offset(x0, volante - h * 0.05f), Size(w * 0.28f, h * 0.1f))
                drawRect(a.sombra(0.22f), Offset(x0 + w * 0.19f, volante - h * 0.05f), Size(w * 0.09f, h * 0.1f))
            }
        }
        // El cuerpo: una bolsa blanda que se asienta en la tabla, ancha abajo y con la panza caída hacia un lado.
        val ix = 0.6f * u
        val fondo = h - 0.4f * u
        val bolsa = Path().apply {
            moveTo(w * 0.3f, cuello)
            cubicTo(w * (0.12f - 0.04f * lado), cuello + h * 0.08f, ix, hombro - h * 0.06f, ix, hombro + h * 0.12f)
            cubicTo(ix - 0.4f * u, fondo - h * 0.08f, w * 0.06f, fondo, w * 0.3f, fondo)
            lineTo(w * 0.7f, fondo)
            cubicTo(w * 0.94f, fondo, w - ix + 0.4f * u, fondo - h * 0.08f, w - ix, hombro + h * 0.12f)
            cubicTo(w - ix, hombro - h * 0.06f, w * (0.88f - 0.04f * lado), cuello + h * 0.08f, w * 0.7f, cuello)
            close()
        }
        drawPath(Path().apply { addPath(bolsa, Offset(if (hacia < 0f) 2f * u else -2f * u, 1.4f * u)) }, a.sombra(0.3f))
        clipPath(bolsa) {
            redondo(Rect(0f, cuello, w, h), t, hacia)
            // El brillo de la seda: una veta clara y viva a lo largo, del lado de la luz, y otra más débil.
            val bx = if (hacia < 0f) w * 0.24f else w * 0.66f
            drawRect(a.brillo(if (a.oscuro) 0.5f else 0.45f, k), Offset(bx, cuello), Size(w * 0.07f, h))
            drawRect(a.brillo(0.18f, k), Offset(bx + (if (hacia < 0f) w * 0.13f else -w * 0.14f), cuello), Size(w * 0.05f, h))
            // Los pliegues que bajan del frunce y se abren, cada uno con su luz y su sombra.
            for (j in 0 until 5) {
                val x = w * (0.32f + 0.09f * j)
                val bajo = cuello + h * (0.2f + 0.16f * azar(s, 20 + j))
                val dx = (j - 2f) * 4f * u
                val p = Path().apply { moveTo(x, cuello); cubicTo(x + dx * 0.3f, cuello + h * 0.06f, x + lado * 2f * u + dx * 0.7f, bajo - h * 0.05f, x + dx, bajo) }
                drawPath(p, a.sombra(0.32f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { addPath(p, Offset(1.8f * u, 0f)) }, t.claro.copy(alpha = 0.6f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            }
            // La panza que se asienta: una arruga de través abajo.
            val arr = Path().apply { moveTo(w * 0.08f, fondo - h * 0.12f); quadraticTo(w * 0.5f, fondo - h * (0.16f + 0.03f * azar(s, 31)), w * 0.92f, fondo - h * 0.11f) }
            drawPath(arr, a.sombra(0.28f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { addPath(arr, Offset(0f, -1.6f * u)) }, t.claro.copy(alpha = 0.5f), style = Stroke(0.8f * u))
            // El galón tejido de abajo: una banda dorada con su labor de puntos, entre dos filetes.
            val gy = fondo - h * 0.09f
            drawRect(a.sombra(0.3f), Offset(0f, gy + 4.2f * u), Size(w, 1.2f * u))
            drawRect(l.oro(mezcla(ORO, P.LATON, 0.3f)), Offset(0f, gy), Size(w, 4.2f * u))
            drawRect(l.oro(ORO_OSC), Offset(0f, gy), Size(w, 0.7f * u))
            drawRect(l.oro(ORO_OSC), Offset(0f, gy + 3.5f * u), Size(w, 0.7f * u))
            var xg = 2f * u
            while (xg < w) { drawCircle(l.oro(ORO_OSC), 0.6f * u, Offset(xg, gy + 2.1f * u)); xg += 3f * u }
            drawRect(a.sombra(0.35f), Offset(0f, fondo - h * 0.04f), Size(w, h * 0.04f))
        }
        // El volante del frunce: la seda recogida en ondas por encima del cordón, con su sombra dentro.
        val vol = Path().apply {
            moveTo(w * 0.24f, cuello + 2.4f * u)
            var xv = w * 0.24f; var j = 0
            val paso = w * 0.075f
            while (xv < w * 0.76f) {
                val alto = if (j % 2 == 0) volante else volante + h * 0.025f
                quadraticTo(xv + paso * 0.5f, alto - h * 0.012f, xv + paso, if (j % 2 == 0) volante + h * 0.022f else volante + 0.5f * u)
                xv += paso; j++
            }
            lineTo(w * 0.76f, cuello + 2.4f * u); close()
        }
        drawPath(vol, t.cara)
        clipPath(vol) {
            drawRect(t.hondo.copy(alpha = 0.5f), Offset(0f, volante), Size(w, h * 0.03f))
            for (j in 0 until 6) {
                val xx = w * (0.27f + 0.09f * j)
                drawLine(if (j % 2 == 0) a.sombra(0.35f) else t.claro.copy(alpha = 0.7f), Offset(xx, volante), Offset(xx + (j - 2.5f) * 0.6f * u, cuello + 2f * u), 1.4f * u)
            }
        }
        drawPath(vol, t.hondo, style = Stroke(0.7f * u))
        // El cordón que frunce, con sus dos cabos que caen por delante con una borlita cada uno.
        drawRect(a.sombra(0.35f), Offset(w * 0.25f, cuello + 2.2f * u), Size(w * 0.5f, 1.4f * u))
        drawRect(oroCordon, Offset(w * 0.24f, cuello), Size(w * 0.52f, 2.2f * u))
        drawRect(a.brillo(0.35f, k), Offset(w * 0.24f, cuello), Size(w * 0.52f, 0.6f * u))
        val nudo = Offset(w * (0.5f + 0.2f * lado), cuello + 1.1f * u)
        listOf(-1f, 1f).forEach { q ->
            val fin = Offset(nudo.x + q * 2.6f * u + lado * 1.5f * u, cuello + h * (0.17f + 0.04f * q))
            drawLine(a.sombra(0.3f), nudo + Offset(1f * u, 1f * u), fin + Offset(1f * u, 1f * u), 1f * u)
            drawLine(oroCordon, nudo, fin, 1f * u, StrokeCap.Round)
            val bl = Path()
            for (j in 0 until 5) { val f = (j - 2f) / 2f; bl.moveTo(fin.x + f * 0.8f * u, fin.y + 1f * u); bl.lineTo(fin.x + f * 1.6f * u, fin.y + 5f * u) }
            drawPath(bl, oroCordon, style = Stroke(0.7f * u))
            drawCircle(l.oro(ORO_OSC), 1.5f * u, fin)
        }
        drawCircle(a.sombra(0.4f), 2.4f * u, nudo + Offset(0.6f * u, 0.8f * u))
        drawCircle(l.oro(ORO_OSC), 2.4f * u, nudo)
        drawCircle(l.oro(), 1.7f * u, nudo - Offset(0.3f * u, 0.3f * u))
        // La tarjeta del día, atada al cordón con un hilo y colgando por delante, algo ladeada.
        val et = Rect(2f * u, hombro - 13f * u, w - 2f * u, hombro + 17f * u)
        val giro = (azar(s, 11) - 0.5f) * 7f
        drawLine(l.c(P.TINTA, 0f).copy(alpha = 0.7f), nudo, Offset(et.center.x, et.top + 2.4f * u), 0.7f * u)
        tarjeta(v, medidor, et, l, a, giro)
        drawCircle(a.sombra(0.55f), 1f * u, Offset(et.center.x, et.top + 2.8f * u))
        // La luna de los Sueños: bordada en la panza, bajo la tarjeta.
        if (v.noche) luna(Offset(w * 0.5f, minOf(et.bottom + 9f * u, fondo - h * 0.12f)), minOf(w * 0.2f, 4.8f * u), a)
        filos(Rect(ix, cuello, w - ix, fondo), a, k, hacia, luna)
    }
}
