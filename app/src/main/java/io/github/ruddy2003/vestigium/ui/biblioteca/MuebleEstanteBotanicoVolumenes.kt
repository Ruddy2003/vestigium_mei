package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil

/**
 * Las entradas del estante de Botánico ([MuebleEstanteBotanico]), tres cosas del naturalista según la semilla: el cuaderno de
 * campo (de pie, en piel flexible, con la etiqueta de papel del día, la correa con su hebilla, el lápiz y las hojas y frondas
 * prensadas que asoman por la cabeza), el frasco de semillas (de vidrio con hombros y cuello, el corcho atado con bramante y
 * las semillas que lo llenan según las palabras) y la talega de semillas (de lienzo, blanda, la boca fruncida con varias vueltas
 * de bramante y un solo cabo, con su tarjeta). El ánimo es la piel, las semillas y el lienzo, en todo el cuerpo
 * ([tonoDeDiaBotanico]); sin ánimo, cuero sin teñir, semillas pardas y lienzo crudo. En los Sueños, una media luna de plata
 * con su disco apagado.
 */
internal object VolumenEstanteBotanico {
    private val P = PaletaBotanico

    /** Lo que no tiene ánimo: la piel sin teñir, las semillas pardas y el lienzo crudo. */
    private val PIEL_CRUDA = Color(0xFF9A7650)
    private val SEMILLA_PARDA = Color(0xFF8A6A42)
    private val LIENZO_CRUDO = Color(0xFFBEA57A)
    private val CORCHO = Color(0xFFB8925E)

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.36f, 0.74f, 0.4f, 0.08f, -0.22f, -0.48f, -0.74f)

    /** Los colores de un tinte con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(ti: Color?, sin: Color, t: TonoBotanico, brillo: Float = 0.3f, sombra: Float = 0.55f): Tinte {
        val m = ti ?: sin
        val claro = mezcla(m, luzMueble(1f), brillo)
        val hondo = mezcla(m, sombraMueble(1f), sombra)
        return if (ti != null) Tinte(t.animo(m), t.animo(claro), t.animo(hondo)) else Tinte(t.c(m), t.c(claro), t.c(hondo))
    }

    /** Un cuerpo redondo en [r] por franjas opacas (sin degradados): el filo, el claro, la cara, el hondo, hacia [hacia]. */
    private fun DrawScope.redondo(r: Rect, ti: Tinte, hacia: Float) {
        for (i in 0 until 7) {
            val f0 = FRANJAS[i]; val f1 = FRANJAS[i + 1]
            val m = TONOS[i]
            val c = if (m >= 0f) mezcla(ti.cara, ti.claro, m) else mezcla(ti.cara, ti.hondo, -m)
            val mas = if (i < 6) 1.2f else 0f
            val x0 = if (hacia < 0f) r.left + r.width * f0 else r.right - r.width * f1 - mas
            drawRect(c, Offset(x0, r.top), Size(r.width * (f1 - f0) + mas, r.height))
        }
    }

    /** Las mismas franjas de [redondo] en un solo degradado de cortes francos, para pintar de una vez una forma cualquiera. */
    private fun franjas(ti: Tinte, hacia: Float, x0: Float, x1: Float): Brush {
        val paradas = ArrayList<Pair<Float, Color>>()
        for (i in 0 until 7) {
            val m = TONOS[i]
            val c = if (m >= 0f) mezcla(ti.cara, ti.claro, m) else mezcla(ti.cara, ti.hondo, -m)
            val a0 = if (hacia < 0f) FRANJAS[i] else 1f - FRANJAS[i + 1]
            val a1 = if (hacia < 0f) FRANJAS[i + 1] else 1f - FRANJAS[i]
            paradas += a0 to c; paradas += a1 to c
        }
        paradas.sortBy { it.first }
        return Brush.horizontalGradient(*paradas.toTypedArray(), startX = x0, endX = x1)
    }

    /** Los meses cortos en español. */
    private val MESES_CORTOS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")
    private val ROMANOS = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")

    /**
     * El mes corto en español, como «nov.». Botánico nombra los meses con su número romano («XI» o «XI-2026»): se vuelve al
     * número; si viene con su nombre, se toman sus tres primeras letras.
     */
    private fun mesCorto(mes: String): String {
        val m = mes.trim().substringBefore(' ').substringBefore('-').trim()
        val i = ROMANOS.indexOf(m.uppercase())
        if (i >= 0) return MESES_CORTOS[i]
        m.toIntOrNull()?.let { n -> if (n in 1..12) return MESES_CORTOS[n - 1] }
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
        textoEn(medidor, v.dia, Rect(r.left, y0, r.right, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.SemiBold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left, y0 + hd - 1.4f * u, r.right, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.85f), MES, minimo = 5f)
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

    /**
     * Las texturas de las entradas, pintadas una vez por densidad en baldosas (imágenes) que luego solo se pegan: el grano de la
     * piel (rayitas cortas en todas direcciones), la trama del lienzo (hilos finos a lo ancho y más tenues a lo alto) y las
     * semillas (lo hondo entre ellas, su sombra y su brillo, con la cara de cada una transparente para que se vea el color del
     * ánimo de debajo). Son neutras (sombra violeta y luz crema), así sirven para cualquier color.
     */
    private var densidadBaldosas = -1f
    private var baldosaGrano: ImageBitmap? = null
    private var baldosaTrama: ImageBitmap? = null
    private var baldosaSemillas: ImageBitmap? = null

    private fun pintarBaldosa(ancho: Float, alto: Float, pintar: DrawScope.() -> Unit): ImageBitmap {
        val img = ImageBitmap(ceil(ancho).toInt().coerceAtLeast(1), ceil(alto).toInt().coerceAtLeast(1))
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ancho, alto)) { pintar() }
        return img
    }

    private fun baldosas(u: Float) {
        if (u == densidadBaldosas) return
        val oscuro = SOMBRA_MUEBLE
        baldosaGrano = pintarBaldosa(60f * u, 120f * u) {
            val p = Path()
            for (j in 0 until 160) {
                val x = 60f * u * azar(8701, j); val y = 120f * u * azar(8702, j)
                val an = azar(8703, j) * 3.1416f
                val l = (1.2f + 1.6f * azar(8704, j)) * u
                p.moveTo(x, y); p.lineTo(x + l * kotlin.math.cos(an), y + l * kotlin.math.sin(an))
            }
            drawPath(p, oscuro.copy(alpha = 0.55f), style = Stroke(0.6f * u))
        }
        baldosaTrama = pintarBaldosa(60f * u, 120f * u) {
            val p = Path(); val q = Path()
            var y = 0f; var j = 0
            while (y < 120f * u && j < 120) { p.moveTo(0f, y); p.lineTo(60f * u, y + (azar(8901, j) - 0.5f) * 0.8f * u); y += (1.5f + 0.8f * azar(8902, j)) * u; j++ }
            var x = 0f; j = 0
            while (x < 60f * u && j < 60) { q.moveTo(x, 0f); q.lineTo(x + (azar(8903, j) - 0.5f) * 0.8f * u, 120f * u); x += (2.2f + 1.2f * azar(8904, j)) * u; j++ }
            drawPath(p, oscuro.copy(alpha = 0.5f), style = Stroke(0.4f * u))
            drawPath(q, LUZ_MUEBLE.copy(alpha = 0.45f), style = Stroke(0.4f * u))
        }
        baldosaSemillas = pintarBaldosa(50f * u, 110f * u) {
            val s = Path(); val b = Path()
            var y = 0f; var fila = 0
            while (y < 112f * u && fila < 60) {
                var x = (fila % 2) * 2.4f * u - 2f * u
                var j = 0
                while (x < 52f * u && j < 30) {
                    val rx = (2.1f + 0.9f * azar(8801 + fila, j)) * u; val ry = rx * (0.62f + 0.2f * azar(8802 + fila, j))
                    val dx = (azar(8803 + fila, j) - 0.5f) * 1.2f * u
                    s.addOval(Rect(x + dx - rx, y - ry, x + dx + rx, y + ry))
                    b.addOval(Rect(x + dx - rx * 0.6f, y - ry * 0.7f, x + dx - rx * 0.05f, y - ry * 0.25f))
                    x += rx * 1.9f; j++
                }
                y += 2.9f * u; fila++
            }
            // Lo hondo entre las semillas, la sombra de cada una en la de abajo, el hueco de su cara y su brillo.
            drawRect(oscuro.copy(alpha = 0.55f), Offset.Zero, size)
            translate(0.5f * u, 0.8f * u) { drawPath(s, oscuro.copy(alpha = 0.3f)) }
            drawPath(s, Color.Black, blendMode = BlendMode.Clear)
            drawPath(s, oscuro.copy(alpha = 0.35f), style = Stroke(0.5f * u))
            drawPath(b, LUZ_MUEBLE.copy(alpha = 0.7f))
        }
        densidadBaldosas = u
    }

    /**
     * La luna de los Sueños: el disco apagado de la luna (la luz cenicienta) y la media luna de plata encima, con su canto oscuro
     * y su sombra; así se lee luna y no emblema.
     */
    private fun DrawScope.luna(c: Offset, r: Float, t: TonoBotanico) {
        val u = density
        val m = creciente(r)
        drawCircle(Color(0xFF3A4652).copy(alpha = 0.55f), r, c)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, t.s(0.5f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFDDE6EE))
            drawPath(m, Color(0xFF2E3A44), style = Stroke(0.8f * u))
        }
        drawCircle(Color.White.copy(alpha = 0.85f), r * 0.14f, Offset(c.x - r * 0.6f, c.y - r * 0.16f))
    }

    /** Lo de todas: la sombra que dejan en la pared por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(t: TonoBotanico, hacia: Float, desde: Float, fuerza: Float = 1f) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        // La sombra que la entrada echa en la pared de detrás, corrida hacia donde no está la luz y un poco abajo: asoma en la
        // rendija, por encima de las vecinas más bajas y en el hueco. De noche, tanto más marcada cuanto más cerca de la lumbre.
        val fs = if (t.a.oscuro) fuerza * (0.3f + 0.9f * t.k).coerceAtMost(1f) else fuerza
        drawRoundRect(t.s(0.3f * fs), Offset(-hacia * 6f * u, desde + 4f * u), Size(w, h - desde - 4f * u), CornerRadius(3f * u))
        if (hacia < 0f) drawRect(t.s(0.42f * fuerza), Offset(w, desde), Size(sep, h - desde))
        else drawRect(t.s(0.42f * fuerza), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(t.s(0.5f * fuerza), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche), el del ocaso en los Sueños claros y el roce frío de la luna. */
    private fun DrawScope.filos(cuerpo: Rect, t: TonoBotanico, hacia: Float) {
        val u = density
        if (!t.a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(t.filo(0.7f), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (t.a.oscuro && t.a.noche) drawRect(t.lunar(0.7f), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    /** Una etiqueta de papel o cartulina en [et] con el día y el mes, su sombra y su borde gastado. */
    private fun DrawScope.etiqueta(v: Volumen, medidor: TextMeasurer, et: Rect, t: TonoBotanico, papel: Color, filete: Boolean, giro: Float = 0f) {
        val u = density
        rotate(giro, et.center) {
            drawRect(t.s(0.4f), Offset(et.left + 1f * u, et.top + 1.3f * u), et.size)
            drawRect(t.etiqueta(papel), et.topLeft, et.size)
            drawRect(t.etiqueta(P.CARTULINA_OSC).copy(alpha = 0.45f), Offset(et.left, et.bottom - et.height * 0.12f), Size(et.width, et.height * 0.12f))
            drawRect(t.etiqueta(P.CARTULINA_OSC).copy(alpha = 0.35f), et.topLeft, Size(et.width * 0.07f, et.height))
            if (filete) drawRect(t.tinta().copy(alpha = 0.4f), et.deflate(1.5f * u).topLeft, et.deflate(1.5f * u).size, style = Stroke(0.5f * u))
            diaYMes(v, medidor, et.deflate(1.6f * u), t.tinta())
        }
    }

    // ---------------------------------------------------------------- el cuaderno de campo

    /** Lo que asoma por la cabeza del cuaderno: frondas y hojas prensadas, más con más palabras. */
    private fun DrawScope.prensadas(v: Volumen, x0: Float, x1: Float, y0: Float, t: TonoBotanico) {
        val u = density
        val s = v.semilla
        val n = 1 + (v.lleno * 3.4f).toInt()
        for (j in 0 until n) {
            val x = x0 + (x1 - x0) * (if (n == 1) 0.45f else 0.15f + 0.7f * j / (n - 1)) + (azar(s, 60 + j) - 0.5f) * 3f * u
            val alto = y0 * (0.55f + 0.45f * azar(s, 64 + j)) + 2f * u
            val col = t.c(when ((j + s) and 3) { 0 -> P.HOJA; 1 -> mezcla(P.HOJA, P.KRAFT, 0.5f); 2 -> P.HOJA_CLARA; else -> mezcla(P.CUERO, P.KRAFT, 0.4f) })
            if ((j + (s ushr 3)) % 2 == 0) frondaBotanico(Offset(x, y0 + 2f * u), alto, -90f + (azar(s, 68 + j) - 0.5f) * 30f, col, 0.9f * u, 7)
            else hojaBotanico(Offset(x, y0 + 2f * u - alto * 0.42f), alto * 0.9f, (azar(s, 70 + j) - 0.5f) * 40f, col, t)
        }
    }

    fun DrawScope.cuaderno(v: Volumen, medidor: TextMeasurer, t: TonoBotanico, hacia: Float, ti: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val y0 = h * (0.08f + 0.04f * azar(s, 3))
        alrededor(t, hacia, y0)
        // Lo que asoma por la cabeza y el lápiz, detrás del lomo.
        prensadas(v, w * 0.1f, w * 0.75f, y0, t)
        val lapiz = azar(s, 4) > 0.35f
        if (lapiz) {
            val lx = w * (0.72f + 0.1f * azar(s, 5))
            val top = y0 * 0.15f
            drawLine(t.c(Color(0xFFB0703A), 0.05f), Offset(lx, y0 + 4f * u), Offset(lx + 1.2f * u, top + 2.4f * u), 2.4f * u)
            drawLine(t.c(Color(0xFF3A342C)), Offset(lx + 1.1f * u, top + 2.6f * u), Offset(lx + 1.3f * u, top), 1.1f * u, StrokeCap.Round)
        }
        val ti2 = tinte(ti, PIEL_CRUDA, t, 0.32f)
        // Las tapas que asoman arriba y abajo del lomo (las cejas), más hondas, y el lomo redondo por franjas.
        drawRoundRect(ti2.hondo, Offset(0f, y0), Size(w, 3f * u), CornerRadius(1.4f * u))
        drawRoundRect(ti2.hondo, Offset(0f, h - 3f * u), Size(w, 3f * u), CornerRadius(1.4f * u))
        val lomo = Rect(0.9f * u, y0 + 1.6f * u, w - 0.9f * u, h - 1.2f * u)
        redondo(lomo, ti2, hacia)
        // El grano de la piel, el desgaste y los roces.
        clipRect(lomo.left, lomo.top, lomo.right, lomo.bottom) {
            baldosas(u)
            baldosaGrano?.let { drawImage(it, Offset(-30f * u * azar(s, 2), -60f * u * azar(s, 6)), alpha = 0.7f) }
            for (j in 0 until 3) {
                val y = lomo.top + lomo.height * (0.1f + 0.85f * azar(s, 50 + j))
                drawLine(t.b(0.16f), Offset(lomo.left + lomo.width * 0.15f, y), Offset(lomo.left + lomo.width * (0.3f + 0.4f * azar(s, 54 + j)), y + 0.6f * u), 0.8f * u)
            }
        }
        // La cofia de arriba y la de abajo, gastadas (la piel más clara donde roza el dedo).
        drawRoundRect(ti2.claro, Offset(lomo.left, y0), Size(lomo.width, 2.6f * u), CornerRadius(1.2f * u))
        drawRect(mezcla(ti2.cara, ti2.claro, 0.6f), Offset(lomo.left, h - 3f * u), Size(lomo.width, 2.2f * u))
        // Los hilos de la costura que se ven en la cabeza.
        for (j in 0 until 4) drawLine(t.c(P.CRUDO, -0.1f), Offset(lomo.left + lomo.width * (0.2f + 0.2f * j), y0 + 0.4f * u), Offset(lomo.left + lomo.width * (0.2f + 0.2f * j), y0 + 2f * u), 0.6f * u)
        // Dos filetes en seco arriba y abajo.
        listOf(y0 + 6f * u, h - 7f * u).forEach { y ->
            drawLine(ti2.hondo, Offset(lomo.left + 1f * u, y), Offset(lomo.right - 1f * u, y), 0.8f * u)
            drawLine(ti2.claro.copy(alpha = 0.6f), Offset(lomo.left + 1f * u, y + 1f * u), Offset(lomo.right - 1f * u, y + 1f * u), 0.5f * u)
        }
        // La etiqueta de papel pegada con el día.
        val alto = azar(s, 7) > 0.4f
        val et0 = y0 + h * (if (alto) 0.07f else 0.14f)
        val et = Rect(lomo.left + 1.8f * u, et0, lomo.right - 1.8f * u, et0 + 30f * u)
        etiqueta(v, medidor, et, t, mezcla(P.CARTULINA, P.PAPEL, 0.25f), filete = true)
        // La correa de cuero que abraza el cuaderno, con su hebilla de latón (no en todos).
        if (azar(s, 8) > 0.3f) {
            val cy = et.bottom + (h - et.bottom) * (0.42f + 0.2f * azar(s, 9))
            val ch = 5f * u
            drawRect(t.s(0.4f), Offset(lomo.left, cy + ch), Size(lomo.width, 1.4f * u))
            drawRect(t.c(P.CUERO_OSC, 0.06f), Offset(0f, cy), Size(w, ch))
            drawRect(t.b(0.3f), Offset(0f, cy), Size(w, 0.8f * u))
            val hx = w * (0.3f + 0.25f * azar(s, 10))
            val hb = Rect(hx, cy - 1.4f * u, hx + ch * 1.1f, cy + ch + 1.4f * u)
            drawRect(t.s(0.35f), hb.topLeft + Offset(1f * u, 1.2f * u), hb.size, style = Stroke(1.3f * u))
            drawRect(t.c(P.LATON), hb.topLeft, hb.size, style = Stroke(1.3f * u))
            drawLine(t.c(P.LATON), Offset(hb.left, hb.center.y), Offset(hb.right + 1.4f * u, hb.center.y), 0.9f * u)
            destelloBotanico(Offset(if (hacia < 0f) hb.left else hb.right, hb.top), 0.9f * u, t)
            // La punta de la correa que sobra, un poco despegada.
            drawPath(poligonoBotanico(hb.right, cy, hb.right + w * 0.16f, cy + 0.6f * u, hb.right + w * 0.15f, cy + ch + 1f * u, hb.right, cy + ch), t.c(P.CUERO_OSC, -0.05f))
        }
        // El registro de cinta que cuelga por el pie, más con más palabras.
        if (v.lleno > 0.25f) {
            val xx = lomo.left + lomo.width * (0.3f + 0.4f * azar(s, 11))
            val fin = Offset(xx + (azar(s, 12) - 0.5f) * 3f * u, h + 2.6f * u)
            drawLine(t.c(if (s and 4 == 0) P.LACRE else P.VERDE), Offset(xx, h - 2f * u), fin, 1.8f * u)
            drawLine(t.s(0.3f), Offset(xx + 1f * u, h - 2f * u), fin + Offset(1f * u, 0f), 0.6f * u)
        }
        // La luna de los Sueños, bajo la etiqueta.
        if (v.noche) luna(Offset(lomo.center.x, minOf(et.bottom + 11f * u, h - 12f * u)), minOf(lomo.width * 0.26f, 5.6f * u), t)
        filos(lomo, t, hacia)
    }

    // ---------------------------------------------------------------- el frasco de semillas

    fun DrawScope.frasco(v: Volumen, medidor: TextMeasurer, t: TonoBotanico, hacia: Float, ti: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val ancho = (azar(s, 3) > 0.5f)
        val tapa = h * 0.11f
        val cuello = h * (0.2f + 0.03f * azar(s, 4))
        val hombro = cuello + h * (if (ancho) 0.08f else 0.12f)
        val cw = w * (if (ancho) 0.62f else 0.5f)
        val cx = w / 2
        val cuerpo = Path().apply {
            moveTo(cx - cw / 2, tapa)
            lineTo(cx - cw / 2, cuello)
            cubicTo(cx - cw / 2, cuello + (hombro - cuello) * 0.5f, 0.6f * u, cuello + (hombro - cuello) * 0.3f, 0.6f * u, hombro)
            lineTo(0.6f * u, h - 3f * u)
            quadraticTo(0.6f * u, h, 3f * u, h)
            lineTo(w - 3f * u, h)
            quadraticTo(w - 0.6f * u, h, w - 0.6f * u, h - 3f * u)
            lineTo(w - 0.6f * u, hombro)
            cubicTo(w - 0.6f * u, cuello + (hombro - cuello) * 0.3f, cx + cw / 2, cuello + (hombro - cuello) * 0.5f, cx + cw / 2, cuello)
            lineTo(cx + cw / 2, tapa)
            close()
        }
        // La sombra en la tabla: el vidrio deja pasar la luz (una mancha clara dentro de la sombra) y la del lado.
        alrededor(t, hacia, hombro, 0.7f)
        drawOval(t.b(0.3f), Offset(w * (if (hacia < 0f) 0.55f else 0.1f), h - 2f * u), Size(w * 0.35f, 2.2f * u))
        // El vidrio de atrás: la pared se ve a través, apenas verde (solo hasta donde empiezan las semillas).
        clipRect(0f, 0f, w, h - (h - hombro) * (0.56f + 0.4f * v.lleno) + 2f * u) { drawPath(cuerpo, t.c(P.VIDRIO_OSC).copy(alpha = 0.22f)) }
        // Las semillas: llenan según las palabras, del color del ánimo, con su superficie despareja (sin recortes caros: un
        // rectángulo redondeado del color, la baldosa de las semillas encima y una fila de granos sueltos en el borde de arriba).
        val sem = ti ?: SEMILLA_PARDA
        val lleno = 0.56f + 0.4f * v.lleno
        val nivel = h - (h - hombro) * lleno
        val cara = if (ti != null) t.animo(sem) else t.c(sem)
        val hondo = if (ti != null) t.animo(mezcla(sem, sombraMueble(1f), 0.5f)) else t.c(mezcla(sem, sombraMueble(1f), 0.5f))
        val claro = if (ti != null) t.animo(mezcla(sem, luzMueble(1f), 0.45f)) else t.c(mezcla(sem, luzMueble(1f), 0.45f))
        val dentro = Rect(1.6f * u, nivel, w - 1.6f * u, h - 1.2f * u)
        drawRoundRect(cara, dentro.topLeft, dentro.size, CornerRadius(2.4f * u))
        baldosas(u)
        clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom - 1.6f * u) {
            baldosaSemillas?.let { drawImage(it, Offset(-20f * u * azar(s, 6), nivel - 40f * u * azar(s, 7))) }
            // La luz de un lado y la sombra del otro, en el montón.
            redondoSombra(Rect(dentro.left, nivel, dentro.right, h), t, hacia)
        }
        // Los granos sueltos del borde de arriba, que hacen el montón desparejo.
        val n = ((dentro.width) / (3.6f * u)).toInt().coerceIn(2, 16)
        for (j in 0 until n) {
            val gx = dentro.left + dentro.width * (j + 0.5f) / n + (azar(s, 20 + j) - 0.5f) * 1.4f * u
            val gy = nivel - (0.4f + 1.6f * azar(s, 30 + j)) * u
            val rx = (1.9f + 0.7f * azar(s, 40 + j)) * u; val ry = rx * 0.7f
            drawOval(hondo, Offset(gx - rx + 0.5f * u, gy - ry + 0.8f * u), Size(rx * 2f, ry * 2f))
            drawOval(cara, Offset(gx - rx, gy - ry), Size(rx * 2f, ry * 2f))
            drawOval(claro.copy(alpha = 0.8f), Offset(gx - rx * 0.6f, gy - ry * 0.7f), Size(rx * 0.55f, ry * 0.45f))
        }
        // El reflejo largo del lado de la luz y uno fino del otro.
        val bx = if (hacia < 0f) w * 0.16f else w * 0.72f
        drawRect(t.b(0.6f), Offset(bx, hombro + 2f * u), Size(w * 0.09f, h - hombro - 6f * u))
        drawRect(t.b(0.22f), Offset(if (hacia < 0f) w * 0.8f else w * 0.12f, hombro + 4f * u), Size(w * 0.04f, h - hombro - 10f * u))
        drawRect(t.b(0.5f), Offset(cx - cw * 0.3f, cuello), Size(cw * 0.12f, hombro - cuello))
        // El culo grueso del vidrio, verdoso.
        drawRoundRect(t.c(mezcla(P.VIDRIO_OSC, P.VIDRIO, 0.4f)).copy(alpha = 0.55f), Offset(0.6f * u, h - 3.6f * u), Size(w - 1.2f * u, 3.6f * u), CornerRadius(2.6f * u))
        // El contorno del vidrio: oscuro en el lado de la sombra, con luz en el de la luz.
        drawPath(cuerpo, t.c(P.VIDRIO_OSC, -0.1f).copy(alpha = 0.75f), style = Stroke(1f * u))
        drawLine(t.b(0.55f), Offset(if (hacia < 0f) 1.4f * u else w - 1.4f * u, hombro + 3f * u), Offset(if (hacia < 0f) 1.4f * u else w - 1.4f * u, h - 4f * u), 0.8f * u)
        // El labio del cuello.
        val labio = Rect(cx - cw / 2 - 1.4f * u, tapa - 1f * u, cx + cw / 2 + 1.4f * u, tapa + 3.2f * u)
        drawRoundRect(t.c(P.VIDRIO_OSC, 0.15f).copy(alpha = 0.8f), labio.topLeft, labio.size, CornerRadius(1.4f * u))
        drawRect(t.b(0.5f), labio.topLeft + Offset(1f * u, 0.6f * u), Size(labio.width * 0.4f, 0.8f * u))
        // El corcho (o el capuchón de papel atado) y el bramante.
        val capuchon = azar(s, 8) > 0.62f
        val corcho = Rect(cx - cw * 0.42f, 0.6f * u, cx + cw * 0.42f, tapa + 1f * u)
        if (capuchon) {
            val cap = Path().apply {
                moveTo(corcho.left - cw * 0.1f, tapa + 5f * u)
                quadraticTo(corcho.left - cw * 0.12f, 0f, cx, 0.4f * u)
                quadraticTo(corcho.right + cw * 0.12f, 0f, corcho.right + cw * 0.1f, tapa + 5f * u)
                var x = corcho.right + cw * 0.1f; var j = 0
                while (x > corcho.left - cw * 0.1f && j < 12) { val x1 = x - cw * 0.12f; lineTo(x1, tapa + (if (j % 2 == 0) 7.6f else 5f) * u); x = x1; j++ }
                close()
            }
            drawPath(Path().apply { addPath(cap, Offset(1f * u, 1.2f * u)) }, t.s(0.3f))
            val papel = t.c(mezcla(P.KRAFT, P.CRUDO, 0.4f))
            val sombra = mezcla(papel, t.s(1f).copy(alpha = 1f), 0.22f)
            drawPath(cap, Brush.horizontalGradient(if (hacia < 0f) listOf(papel, papel, sombra) else listOf(sombra, papel, papel), corcho.left - cw * 0.1f, corcho.right + cw * 0.1f))
        } else {
            drawRect(t.c(CORCHO), corcho.topLeft, corcho.size)
            clipRect(corcho.left, corcho.top, corcho.right, corcho.bottom) {
                cilindroBotanico(corcho, t, hacia, 1f)
                for (j in 0 until 7) drawCircle(t.s(0.35f), 0.5f * u, Offset(corcho.left + corcho.width * azar(s, 40 + j), corcho.top + corcho.height * azar(s, 47 + j)))
            }
            drawRect(t.b(0.4f), corcho.topLeft, Size(corcho.width, 0.8f * u))
            // El lacre en la tapa, en algunos.
            if (azar(s, 9) > 0.6f) drawRoundRect(t.c(P.LACRE), Offset(corcho.left - 0.6f * u, corcho.top), Size(corcho.width + 1.2f * u, 3f * u), CornerRadius(1.4f * u))
        }
        // El bramante: tres vueltas al cuello y un cabo con su nudo.
        for (j in 0 until 3) {
            val y = tapa + (1.6f + 1.4f * j) * u
            drawLine(t.c(P.BRAMANTE, -0.05f), Offset(cx - cw / 2 - 1f * u, y), Offset(cx + cw / 2 + 1f * u, y + 0.6f * u), 0.9f * u)
        }
        val nudo = Offset(cx + cw / 2 + 0.6f * u, tapa + 3.4f * u)
        drawCircle(t.c(P.BRAMANTE, -0.1f), 1.4f * u, nudo)
        drawLine(t.c(P.BRAMANTE, -0.05f), nudo, Offset(nudo.x + 2.4f * u, nudo.y + h * 0.08f), 0.9f * u, StrokeCap.Round)
        // La etiqueta de papel pegada, con el día, arriba del cuerpo, bajo el hombro (algo ladeada en algunos): las semillas se
        // ven debajo.
        val ey = hombro + (h - hombro) * (0.02f + 0.06f * azar(s, 10))
        val et = Rect(2.6f * u, ey, w - 2.6f * u, ey + 30f * u)
        etiqueta(v, medidor, et, t, P.CARTULINA, filete = azar(s, 11) > 0.5f, giro = (azar(s, 12) - 0.5f) * 3f)
        // La luna de los Sueños, pintada en el vidrio, sobre la etiqueta.
        if (v.noche) luna(Offset(cx + w * 0.12f, (hombro + et.top) / 2 + 1f * u), minOf(w * 0.18f, 4.8f * u), t)
        filos(Rect(0f, hombro, w, h), t, hacia)
    }

    /** La luz y la sombra de un montón redondo (las semillas dentro del frasco), en franjas. */
    private fun DrawScope.redondoSombra(r: Rect, t: TonoBotanico, hacia: Float) {
        val izq = hacia < 0f
        drawRect(t.b(0.16f), Offset(if (izq) r.left else r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.s(0.3f), Offset(if (izq) r.right - r.width * 0.28f else r.left, r.top), Size(r.width * 0.28f, r.height))
        drawRect(t.s(0.25f), Offset(r.left, r.top), Size(r.width, 2.4f * density))
    }

    // ---------------------------------------------------------------- la talega de semillas

    /**
     * La talega: un saco blando de lienzo teñido, bajo y panzón, asentado en la tabla; la boca fruncida con varias vueltas de
     * bramante y un solo cabo con su nudo (sin lazo); por encima del atado, el lienzo sobrante se abre en un volante ancho que se
     * vence y cae hacia un lado. Un remiendo en algunas y la tarjeta de cartulina colgada del cabo con el día. Unas semillas se
     * salieron a la tabla.
     */
    fun DrawScope.talega(v: Volumen, medidor: TextMeasurer, t: TonoBotanico, hacia: Float, ti: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val lado = if (azar(s, 5) > 0.5f) 1f else -1f
        fun x(f: Float) = if (lado > 0f) w * f else w * (1f - f)
        val cuello = h * (0.33f + 0.05f * azar(s, 3))
        val fondo = h - 0.4f * u
        alrededor(t, hacia, cuello)
        val ti2 = tinte(ti?.let { mezcla(it, P.KRAFT, 0.14f) }, LIENZO_CRUDO, t, 0.34f, 0.66f)
        // El cuerpo: del atado se abre enseguida en hombros redondos y baja en una panza ancha que se aplasta en la tabla.
        val bolsa = Path().apply {
            moveTo(x(0.36f), cuello)
            cubicTo(x(0.12f), cuello + h * 0.03f, x(0.02f), cuello + h * 0.14f, x(0.02f), cuello + h * 0.3f)
            cubicTo(x(0.01f), h * 0.86f, x(0.0f), fondo - h * 0.02f, x(0.14f), fondo)
            lineTo(x(0.88f), fondo)
            cubicTo(x(1f) - 0.4f * u * lado, fondo - h * 0.02f, x(1f) - 0.4f * u * lado, h * 0.82f, x(0.99f), cuello + h * 0.28f)
            cubicTo(x(0.98f), cuello + h * 0.12f, x(0.86f), cuello + h * 0.03f, x(0.64f), cuello)
            close()
        }
        drawPath(Path().apply { addPath(bolsa, Offset(if (hacia < 0f) 2.2f * u else -2.2f * u, 1.4f * u)) }, t.s(0.32f))
        // El lienzo redondeado por franjas (un solo trazo con su degradado de cortes francos) y la panza más honda abajo.
        drawPath(bolsa, franjas(ti2, hacia, 0f, w))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, ti2.hondo.copy(alpha = 0.5f)), fondo - h * 0.12f, fondo - 1f * u),
            Offset(minOf(x(0.1f), x(0.9f)), fondo - h * 0.12f), Size(w * 0.8f, h * 0.12f - 1f * u))
        // La trama del lienzo, dentro de la panza (la baldosa pintada una vez).
        baldosas(u)
        clipRect(minOf(x(0.08f), x(0.92f)), cuello + h * 0.1f, maxOf(x(0.08f), x(0.92f)), fondo - 2f * u) {
            baldosaTrama?.let { drawImage(it, Offset(-30f * u * azar(s, 6), -60f * u * azar(s, 7)), alpha = 0.3f) }
        }
        // Los pliegues: tres que salen del atado en abanico (cortos y curvos), y dos que cruzan la panza vencida.
        for (j in 0 until 3) {
            val f0 = 0.42f + 0.08f * j
            val an = (j - 1f) * 0.5f + 0.15f * lado
            val l = h * (0.14f + 0.06f * azar(s, 20 + j))
            val p = Path().apply { moveTo(x(f0), cuello + 1f * u); quadraticTo(x(f0) + l * 0.2f * an * lado, cuello + l * 0.5f, x(f0) + l * kotlin.math.sin(an) * lado, cuello + l) }
            drawPath(p, t.s(0.3f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { addPath(p, Offset(1.4f * u * lado, 0f)) }, ti2.claro.copy(alpha = 0.5f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        listOf(0.6f, 0.78f).forEachIndexed { j, f ->
            val y = h * f + (azar(s, 26 + j) - 0.5f) * h * 0.04f
            val arr = Path().apply { moveTo(x(0.08f), y - h * 0.02f); quadraticTo(x(0.5f), y + h * (0.03f + 0.02f * j), x(0.94f), y - h * 0.035f) }
            drawPath(arr, t.s(0.24f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { addPath(arr, Offset(0f, -1.4f * u)) }, ti2.claro.copy(alpha = 0.4f), style = Stroke(0.7f * u))
        }
        // La costura del costado, con sus puntadas.
        val cos = Path().apply { moveTo(x(0.9f), cuello + h * 0.1f); quadraticTo(x(0.975f), h * 0.66f, x(0.9f), fondo - 1f * u) }
        drawPath(cos, ti2.hondo.copy(alpha = 0.6f), style = Stroke(0.8f * u))
        var yy = cuello + h * 0.14f
        while (yy < fondo - 4f * u) { drawLine(ti2.claro.copy(alpha = 0.55f), Offset(x(0.925f), yy), Offset(x(0.925f), yy + 1.6f * u), 0.6f * u); yy += 3.4f * u }
        // El remiendo de otro lienzo, con sus puntadas (en algunas).
        if (azar(s, 32) > 0.5f) {
            val rr = Rect(x(0.6f) - w * 0.15f, h * 0.62f, x(0.6f) + w * 0.15f, h * 0.75f)
            rotate(6f * lado, rr.center) {
                drawRect(t.c(if (ti == null) P.KRAFT else LIENZO_CRUDO, -0.05f), rr.topLeft, rr.size)
                drawRect(t.s(0.2f), Offset(rr.left, rr.bottom - 1.4f * u), Size(rr.width, 1.4f * u))
                var px = rr.left + 1f * u
                while (px < rr.right) { drawLine(ti2.hondo, Offset(px, rr.top - 0.8f * u), Offset(px + 0.8f * u, rr.top + 1.4f * u), 0.6f * u); px += 2.6f * u }
            }
        }
        drawPath(bolsa, ti2.hondo.copy(alpha = 0.5f), style = Stroke(0.7f * u))
        // El volante: el lienzo de más por encima del atado, que se abre, se vence y cae hacia [lado] en dos o tres ondas.
        val sube = h * (0.2f + 0.05f * azar(s, 4))
        val vol = Path().apply {
            moveTo(x(0.34f), cuello + 1.5f * u)
            cubicTo(x(0.22f), cuello - sube * 0.3f, x(0.18f), cuello - sube * 0.85f, x(0.3f), cuello - sube)
            quadraticTo(x(0.42f), cuello - sube * 0.86f, x(0.52f), cuello - sube * 0.95f)
            quadraticTo(x(0.64f), cuello - sube * 0.9f, x(0.76f), cuello - sube * 0.62f)
            cubicTo(x(0.92f), cuello - sube * 0.42f, x(0.98f), cuello - sube * 0.1f, x(0.9f), cuello + sube * 0.06f)
            quadraticTo(x(0.78f), cuello - sube * 0.06f, x(0.66f), cuello + 1.5f * u)
            close()
        }
        drawPath(Path().apply { addPath(vol, Offset(if (hacia < 0f) 1.6f * u else -1.6f * u, 1.2f * u)) }, t.s(0.3f))
        // Más claro arriba, donde se dobla hacia la luz, y hondo abajo, junto al atado.
        drawPath(vol, Brush.verticalGradient(0f to ti2.claro, 0.3f to ti2.cara, 0.72f to ti2.cara, 1f to mezcla(ti2.cara, ti2.hondo, 0.6f), startY = cuello - sube, endY = cuello))
        // Los pliegues del frunce que suben del atado y se abren.
        for (j in 0 until 4) {
            val xx = x(0.4f + 0.06f * j)
            drawLine(t.s(0.32f), Offset(xx, cuello), Offset(xx + (j - 1.5f) * 3f * u * lado, cuello - sube * 0.72f), 1.3f * u)
            drawLine(ti2.claro.copy(alpha = 0.6f), Offset(xx + 1.4f * u, cuello), Offset(xx + 1.4f * u + (j - 1.5f) * 3f * u * lado, cuello - sube * 0.68f), 0.7f * u)
        }
        drawPath(vol, ti2.hondo, style = Stroke(0.7f * u))
        // El borde del lienzo, deshilachado, en la caída.
        for (j in 0 until 5) {
            val xx = x(0.78f + 0.03f * j); val yy = cuello - sube * (0.5f - 0.1f * j)
            drawLine(ti2.claro.copy(alpha = 0.7f), Offset(xx, yy), Offset(xx + 1.6f * u * lado, yy + 1.4f * u), 0.5f * u)
        }
        // Las vueltas de bramante y el nudo; el cabo cae por la panza hasta la tarjeta.
        val bram = t.c(P.BRAMANTE, 0.02f)
        for (j in 0 until 4) {
            val y = cuello - 2.6f * u + j * 1.6f * u
            drawLine(t.s(0.3f), Offset(x(0.31f), y + 1f * u), Offset(x(0.69f), y + 1.4f * u), 1f * u)
            drawLine(bram, Offset(x(0.3f), y), Offset(x(0.7f), y + 0.5f * u), 1.1f * u, StrokeCap.Round)
        }
        val nudo = Offset(x(0.66f), cuello)
        drawCircle(t.s(0.4f), 2.2f * u, nudo + Offset(0.6f * u, 0.8f * u))
        drawCircle(t.c(P.BRAMANTE, -0.08f), 2.2f * u, nudo)
        drawCircle(t.b(0.4f), 0.8f * u, nudo - Offset(0.6f * u, 0.6f * u))
        // La tarjeta de cartulina con su ojal reforzado, colgada del cabo por el lado de la panza.
        val cw = (w * 0.66f).coerceIn(22f * u, 28f * u).coerceAtMost(w - 2f * u)
        val ccx = x(0.44f)
        val et = Rect(ccx - cw / 2, cuello + h * 0.1f, ccx + cw / 2, cuello + h * 0.1f + 31f * u)
        val giro = (azar(s, 11) - 0.5f) * 7f - 3f * lado
        val cabo = Path().apply { moveTo(nudo.x, nudo.y); quadraticTo(x(0.82f), cuello + h * 0.06f, et.center.x, et.top + 3f * u) }
        drawPath(Path().apply { addPath(cabo, Offset(1f * u, 1.2f * u)) }, t.s(0.3f), style = Stroke(1f * u))
        drawPath(cabo, bram, style = Stroke(1f * u, cap = StrokeCap.Round))
        val carton = mezcla(P.KRAFT, P.CARTULINA, 0.55f)
        etiqueta(v, medidor, Rect(et.left, et.top + 5f * u, et.right, et.bottom), t, carton, filete = false, giro = giro)
        rotate(giro, et.center) {
            // La cabeza de la tarjeta, con las esquinas cortadas y el ojal.
            drawPath(poligonoBotanico(et.left + 3f * u, et.top, et.right - 3f * u, et.top, et.right, et.top + 3f * u, et.right, et.top + 5.4f * u, et.left, et.top + 5.4f * u, et.left, et.top + 3f * u), t.etiqueta(carton))
            drawCircle(t.etiqueta(P.CARTULINA_OSC), 2f * u, Offset(et.center.x, et.top + 3f * u))
            drawCircle(t.s(0.6f), 0.9f * u, Offset(et.center.x, et.top + 3f * u))
        }
        // Unas semillas que se salieron a la tabla.
        val sem = ti ?: SEMILLA_PARDA
        val cs = if (ti != null) t.animo(sem) else t.c(sem)
        for (j in 0 until 3 + (v.lleno * 3).toInt()) {
            val sx = x(0.08f + 0.86f * azar(s, 80 + j)); val sy = h - 1.6f * u - 1.2f * u * azar(s, 86 + j)
            drawOval(t.s(0.4f), Offset(sx - 1.6f * u, sy - 0.2f * u), Size(3.6f * u, 1.8f * u))
            drawOval(cs, Offset(sx - 2f * u, sy - 1.3f * u), Size(3.6f * u, 2.4f * u))
            drawCircle(t.b(0.5f), 0.6f * u, Offset(sx - 0.8f * u, sy - 0.7f * u))
        }
        // La luna de los Sueños: bordada abajo en la panza, del otro lado de la tarjeta.
        if (v.noche) luna(Offset(x(0.74f), minOf(et.bottom + 8f * u, h - 9f * u)), minOf(w * 0.16f, 4.6f * u), t)
        filos(Rect(minOf(x(0.02f), x(1f)), cuello, maxOf(x(0.02f), x(1f)), fondo), t, hacia)
    }
}
