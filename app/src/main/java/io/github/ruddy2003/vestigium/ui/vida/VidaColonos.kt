package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Colonos: los muebles de Vida y de Diario de los vecinos que reconstruyen su pueblo, de la misma casa que su estante de
 * cajas de embalaje, su cajonera del almacén general y su almacén de logros. Pino rescatado de tonos distintos (alguna tabla
 * pintada de azul marino o de ladrillo), parches de lata, papel de gaceta, cinta amarillenta, cordel, latón y la luz del quinqué.
 * - Social: la asamblea vecinal: la mesa larga de tablas sobre caballetes, las sillas plegables de los extremos, la cafetera de
 *   peltre y la pila de gacetas, la pared de tablas apenas marcada, el cordel de banderines arriba y dos faroles en las esquinas
 *   (de noche, encendidos).
 * - Mapas: la mesa de la gaceta: los tablones con sus clavos, el pliego de papel de estraza un poco torcido con cinta en las
 *   esquinas, la brújula de latón, el lápiz de carpintero y el cerco de una taza de café.
 * - Otras vidas: los estantes de madera reciclada: una caja de fruta tumbada por medalla, con sus tablillas, sus clavos y una
 *   marca de embarque estarcida, sobre la tabla con sus ménsulas; de cada caja cuelga del clavo su tarjeta de envío con cordel.
 * - Íconos: el ayuntamiento reconstruido, con su torre, su campana y el andamio; la gaceta raspada con la moneda; tres
 *   banderines clavados como marcadores.
 * - Notas: el tablero comunitario de tablas con su tejadillo de ladrillo y los cordeles cruzados; las notas son recortes de
 *   gaceta de borde cortado a tijera, con su filete, sujetos con cinta (o con la chincheta de latón, si están fijadas) y una
 *   marca estarcida según el tipo.
 */
internal object VidaColonos : DisenoVida {
    private val PINO = Color(0xFFC49C66)
    private val PINO_LUZ = Color(0xFFDAB884)
    private val PINO_OSC = Color(0xFF8C6B41)
    private val VETA = Color(0xFF6E5030)
    private val CLAVO = Color(0xFF3A3530)
    private val MARINO = Color(0xFF2A3550)
    private val LADRILLO = Color(0xFF9E4A35)
    private val GACETA = Color(0xFFE8DFC6)
    private val MANILA = Color(0xFFD9BE86)
    private val CORDEL = Color(0xFFBFA272)
    private val CINTA = Color(0xFFE8D48A)
    private val LATON = Color(0xFFB89140)
    private val LATA = Color(0xFFA0A4A0)
    private val PELTRE = Color(0xFF5E6E86)
    private val LUZ = Color(0xFFF2C46B)
    private val CAFE = Color(0xFF6A4020)
    private val TINTA = Color(0xFF1E1A16)

    private fun DrawScope.u() = size.minDimension / 100f
    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un material del pueblo sobre la tarjeta: en claro, tal cual; en oscuro, hundido hacia el fondo (la noche azul). */
    private fun m(c: Color, e: EstadoMueble, oscuro: Boolean, hundido: Float = 0.45f): Color = if (oscuro) mezcla(c, e.fondo, hundido) else c

    /** El tono de la tabla [k] de un mueble de pino rescatado: casi todas de pino, una de azul marino y otra de ladrillo. */
    private fun tabla(k: Int, rnd: Random): Color = when (k % 7) {
        2 -> mezcla(MARINO, PINO, 0.3f)
        5 -> mezcla(LADRILLO, PINO, 0.35f)
        else -> mezcla(PINO_OSC, PINO_LUZ, 0.25f + 0.6f * rnd.nextFloat())
    }

    // ---------------------------------------------------------------- piezas

    /** Un clavo de cabeza redonda. */
    private fun DrawScope.clavo(c: Offset, r: Float, color: Color) {
        drawCircle(color, r, c)
        drawCircle(Color.White.copy(alpha = 0.25f), r * 0.4f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
    }

    /** Un farol de hojalata colgado de [arriba]: el alambre, el cuerpo de vidrio con su llama y el techito; de noche, su halo. */
    private fun DrawScope.farol(arriba: Offset, s: Float, e: EstadoMueble, oscuro: Boolean) {
        val c = Offset(arriba.x, arriba.y + 9f * s)
        val halo = LUZ.copy(alpha = if (oscuro) 0.38f else 0.3f)
        drawCircle(Brush.radialGradient(listOf(halo, halo.copy(alpha = 0f)), c, 9f * s), 9f * s, c)
        val metal = m(CLAVO, e, oscuro, 0.2f).copy(alpha = 0.8f)
        drawLine(metal, arriba, Offset(arriba.x, arriba.y + 4.4f * s), 0.4f * s)
        drawPath(Path().apply { moveTo(c.x - 3f * s, c.y - 3.2f * s); lineTo(c.x, c.y - 5f * s); lineTo(c.x + 3f * s, c.y - 3.2f * s); close() }, metal)
        drawRect(LUZ.copy(alpha = if (oscuro) 0.35f else 0.22f), Offset(c.x - 2.2f * s, c.y - 3.2f * s), Size(4.4f * s, 5.8f * s))
        drawOval(LUZ, Offset(c.x - 0.8f * s, c.y - 1.6f * s), Size(1.6f * s, 2.8f * s))
        drawRect(metal, Offset(c.x - 2.2f * s, c.y - 3.2f * s), Size(4.4f * s, 5.8f * s), style = Stroke(0.45f * s))
        drawRect(metal, Offset(c.x - 2.8f * s, c.y + 2.6f * s), Size(5.6f * s, 1.1f * s))
    }

    /**
     * Una silla plegable de tablas, de perfil, mirando hacia [dir] (1 a la derecha, −1 a la izquierda): las dos patas cruzadas,
     * el asiento, el respaldo con su tablilla y el travesaño; [x] es el cruce y [piso] el suelo.
     */
    private fun DrawScope.sillaPlegable(x: Float, piso: Float, s: Float, dir: Float, madera: Color, oscura: Color) {
        val asiento = piso - 13f * s
        val atras = x - dir * 5f * s; val frente = x + dir * 5f * s
        drawLine(oscura, Offset(atras, piso), Offset(frente, asiento), 1.1f * s, StrokeCap.Round)
        drawLine(madera, Offset(frente, piso), Offset(atras, asiento - 15f * s), 1.2f * s, StrokeCap.Round)
        drawLine(madera, Offset(atras - dir * 0.5f * s, asiento), Offset(frente + dir * 1.2f * s, asiento), 1.6f * s, StrokeCap.Round)
        val rx = atras - dir * 0.8f * s
        drawRect(madera, Offset(rx - 2f * s, asiento - 14f * s), Size(4f * s, 4.2f * s))
        drawRect(oscura, Offset(rx - 2f * s, asiento - 14f * s), Size(4f * s, 4.2f * s), style = Stroke(0.35f * s))
        drawLine(oscura, Offset(x - 3.4f * s, piso - 4f * s), Offset(x + 3.4f * s, piso - 4f * s), 0.6f * s)
    }

    /** Un banderín triangular colgado de su cordel en [a]. */
    private fun DrawScope.banderin(a: Offset, ancho: Float, alto: Float, color: Color) {
        drawPath(Path().apply { moveTo(a.x - ancho / 2, a.y); lineTo(a.x + ancho / 2, a.y); lineTo(a.x, a.y + alto); close() }, color)
    }

    /** Una marca de embarque estarcida: rombo, círculo con barra, estrella o flecha de «este lado arriba» (formas, no letras). */
    private fun DrawScope.marcaEmbarque(c: Offset, s: Float, tipo: Int, color: Color) {
        val g = s * 0.22f
        when (tipo % 4) {
            0 -> drawPath(Path().apply { moveTo(c.x, c.y - s); lineTo(c.x + s, c.y); lineTo(c.x, c.y + s); lineTo(c.x - s, c.y); close() }, color, style = Stroke(g))
            1 -> {
                drawCircle(color, s * 0.85f, c, style = Stroke(g))
                drawLine(color, Offset(c.x - s * 1.1f, c.y), Offset(c.x + s * 1.1f, c.y), g)
            }
            2 -> drawPath(estrella(c, s, 5, 0.42f), color)
            else -> {
                drawLine(color, Offset(c.x - s * 0.5f, c.y + s), Offset(c.x - s * 0.5f, c.y - s * 0.4f), g)
                drawLine(color, Offset(c.x + s * 0.5f, c.y + s), Offset(c.x + s * 0.5f, c.y - s * 0.4f), g)
                drawPath(Path().apply { moveTo(c.x - s, c.y - s * 0.2f); lineTo(c.x, c.y - s); lineTo(c.x + s, c.y - s * 0.2f) }, color, style = Stroke(g, join = StrokeJoin.Miter))
            }
        }
    }

    /** Una estrella de [puntas] puntas con centro [c] y radio [r]; [hondo] es el radio de los entrantes, en fracción de [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int, hondo: Float): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Un rectángulo con el borde cortado a tijera: cada lado en [n] tramos, un poco hacia dentro cada vez. */
    private fun recorte(r: Rect, n: Int, hondo: Float, semilla: Int): Path {
        val rnd = Random(semilla.toLong() * 31L + 7L)
        val p = Path()
        val pasos = n.coerceIn(2, 12)
        fun d() = hondo * rnd.nextFloat()
        for (k in 0 until pasos) { val x = r.left + r.width * k / pasos; if (k == 0) p.moveTo(x + d(), r.top + d()) else p.lineTo(x, r.top + d()) }
        for (k in 0 until pasos) p.lineTo(r.right - d(), r.top + r.height * k / pasos)
        for (k in 0 until pasos) p.lineTo(r.right - r.width * k / pasos, r.bottom - d())
        for (k in 0 until pasos) p.lineTo(r.left + d(), r.bottom - r.height * k / pasos)
        p.close()
        return p
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        // La pared de tablas de la casa comunal, apenas marcada, con sus juntas.
        val anchoTabla = 11f * u
        val nt = (w / anchoTabla).toInt().coerceIn(1, 60) + 1
        for (k in 0 until nt) {
            val x = k * anchoTabla
            drawRect(tabla(k, rnd).copy(alpha = if (oscuro) 0.05f else 0.07f), Offset(x, 0f), Size(anchoTabla, h))
            drawLine(e.tinta.copy(alpha = 0.06f), Offset(x, 0f), Offset(x, h), 0.35f * u)
        }
        // El cordel de banderines de lado a lado, con su comba.
        val cordel = m(CORDEL, e, oscuro, 0.2f)
        val y0 = 2.4f * u; val comba = 2.6f * u
        drawPath(Path().apply { moveTo(2f * u, y0); quadraticTo(w / 2, y0 + comba * 2, w - 2f * u, y0) }, cordel, style = Stroke(0.4f * u))
        val colores = listOf(m(MARINO, e, oscuro, 0.1f), m(LADRILLO, e, oscuro, 0.15f), m(GACETA, e, oscuro, 0.3f))
        val paso = 9f * u
        val nb = ((w - 8f * u) / paso).toInt().coerceIn(0, 60)
        for (k in 0..nb) {
            val x = 4f * u + k * paso
            val t = (x - 2f * u) / (w - 4f * u)
            val y = y0 + comba * 2 * 2 * t * (1 - t)
            banderin(Offset(x, y), 5f * u, 4.6f * u, colores[k % colores.size].copy(alpha = 0.75f))
        }
        // Los dos faroles de las esquinas.
        farol(Offset(10f * u, 5f * u), u, e, oscuro)
        farol(Offset(w - 10f * u, 5f * u), u, e, oscuro)
        // El piso de tablones.
        drawLine(m(PINO_OSC, e, oscuro, 0.3f).copy(alpha = 0.6f), Offset(0f, h - 1.2f * u), Offset(w, h - 1.2f * u), 0.8f * u)
        // Las sillas plegables de los extremos.
        val madera = m(PINO, e, oscuro, 0.35f); val oscura = m(VETA, e, oscuro, 0.3f)
        sillaPlegable(10f * u, h - 1.6f * u, u, 1f, madera, oscura)
        sillaPlegable(w - 10f * u, h - 1.6f * u, u, -1f, madera, oscura)
        // La mesa larga: tres tablas de distinto tono sobre dos caballetes.
        val top = h - 19f * u; val grueso = 4.6f * u
        val mesa = Rect(20f * u, top, w - 20f * u, top + grueso)
        for (k in 0 until 3) {
            val y = mesa.top + grueso * k / 3f
            drawRect(m(tabla(k + 3, rnd), e, oscuro, 0.35f).copy(alpha = 0.85f), Offset(mesa.left, y), Size(mesa.width, grueso / 3f))
            drawLine(oscura.copy(alpha = 0.5f), Offset(mesa.left, y), Offset(mesa.right, y), 0.3f * u)
        }
        drawRect(oscura.copy(alpha = 0.7f), mesa.topLeft, mesa.size, style = Stroke(0.4f * u))
        listOf(mesa.left + 2f * u, mesa.right - 2f * u).forEach { x -> clavo(Offset(x, mesa.center.y), 0.6f * u, m(CLAVO, e, oscuro, 0.2f)) }
        listOf(mesa.left + 12f * u, mesa.right - 12f * u).forEach { x ->
            drawLine(oscura, Offset(x - 5f * u, h - 1.6f * u), Offset(x + 2f * u, mesa.bottom), 1.2f * u, StrokeCap.Round)
            drawLine(oscura, Offset(x + 5f * u, h - 1.6f * u), Offset(x - 2f * u, mesa.bottom), 1.2f * u, StrokeCap.Round)
            drawLine(oscura, Offset(x - 3.4f * u, h - 6f * u), Offset(x + 3.4f * u, h - 6f * u), 0.7f * u)
        }
        // La cafetera de peltre en una punta y la pila de gacetas en la otra.
        val cx = mesa.left + 6f * u
        val peltre = m(PELTRE, e, oscuro, 0.3f)
        drawPath(Path().apply { moveTo(cx - 3f * u, top); lineTo(cx - 2.2f * u, top - 7f * u); lineTo(cx + 2.2f * u, top - 7f * u); lineTo(cx + 3f * u, top); close() }, peltre)
        drawOval(peltre, Offset(cx - 1.6f * u, top - 8.4f * u), Size(3.2f * u, 1.8f * u))
        drawLine(peltre, Offset(cx + 2.4f * u, top - 4f * u), Offset(cx + 4.6f * u, top - 7f * u), 0.7f * u, StrokeCap.Round)
        drawArc(peltre, -90f, 180f, false, Offset(cx - 4.6f * u, top - 6.4f * u), Size(3f * u, 4f * u), style = Stroke(0.6f * u))
        val gx = mesa.right - 13f * u
        for (k in 0 until 3) {
            val gy = top - (k + 1) * 1.3f * u
            drawRect(m(GACETA, e, oscuro, 0.45f), Offset(gx + (k % 2) * 0.8f * u, gy), Size(9f * u, 1.3f * u))
            drawLine(oscura.copy(alpha = 0.5f), Offset(gx + (k % 2) * 0.8f * u, gy), Offset(gx + (k % 2) * 0.8f * u + 9f * u, gy), 0.25f * u)
        }
        drawRect(m(LATA, e, oscuro, 0.35f), Offset(gx - 4f * u, top - 3.4f * u), Size(2.6f * u, 3.4f * u))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        // La mesa de la gaceta: tablones a lo largo, cada uno de su tono, con sus juntas y sus clavos.
        val nt = 6
        val alto = h / nt
        for (k in 0 until nt) {
            val y = k * alto
            drawRect(mezcla(e.fondo, tabla(k, rnd), if (oscuro) 0.22f else 0.3f), Offset(0f, y), Size(w, alto))
            drawLine(e.tinta.copy(alpha = 0.16f), Offset(0f, y), Offset(w, y), 0.4f * u)
            listOf(3f * u, w - 3f * u).forEach { x -> clavo(Offset(x, y + alto / 2), 0.55f * u, e.tinta.copy(alpha = 0.35f)) }
        }
        // El pliego de papel de estraza, un poco torcido, con cinta en las cuatro esquinas.
        val pliego = Rect(6.5f * u, 5f * u, w - 6.5f * u, h - 5f * u)
        val giro = -0.8f + 1.6f * rnd.nextFloat()
        rotate(giro, pliego.center) {
            drawRect(mezcla(e.fondo, MANILA, if (oscuro) 0.16f else 0.42f), pliego.topLeft, pliego.size)
            drawRect(e.tinta.copy(alpha = 0.18f), pliego.topLeft, pliego.size, style = Stroke(0.35f * u))
            val cinta = m(CINTA, e, oscuro, 0.45f).copy(alpha = 0.75f)
            listOf(pliego.topLeft to -35f, Offset(pliego.right, pliego.top) to 35f, Offset(pliego.left, pliego.bottom) to 35f, Offset(pliego.right, pliego.bottom) to -35f).forEach { (o, a) ->
                rotate(a, o) { drawRect(cinta, Offset(o.x - 4.5f * u, o.y - 1.4f * u), Size(9f * u, 2.8f * u)) }
            }
        }
        // La brújula de latón, arriba a la derecha.
        val b = Offset(w - 13f * u, 11f * u)
        val laton = m(LATON, e, oscuro, 0.3f)
        drawCircle(Color.Black.copy(alpha = 0.12f), 6.2f * u, Offset(b.x + 0.6f * u, b.y + 0.8f * u))
        drawCircle(laton, 6f * u, b)
        drawCircle(mezcla(e.fondo, GACETA, 0.8f), 4.6f * u, b)
        drawPath(Path().apply { moveTo(b.x, b.y - 3.8f * u); lineTo(b.x + 1f * u, b.y); lineTo(b.x - 1f * u, b.y); close() }, m(LADRILLO, e, oscuro, 0.1f))
        drawPath(Path().apply { moveTo(b.x, b.y + 3.8f * u); lineTo(b.x + 1f * u, b.y); lineTo(b.x - 1f * u, b.y); close() }, m(MARINO, e, oscuro, 0.1f))
        drawCircle(laton, 0.7f * u, b)
        // El lápiz de carpintero, abajo a la izquierda, y el cerco de una taza, abajo a la derecha.
        rotate(-12f, Offset(14f * u, h - 6f * u)) {
            drawRect(m(LADRILLO, e, oscuro, 0.3f), Offset(6f * u, h - 7f * u), Size(18f * u, 2.2f * u))
            drawPath(Path().apply { moveTo(24f * u, h - 7f * u); lineTo(27.5f * u, h - 5.9f * u); lineTo(24f * u, h - 4.8f * u); close() }, m(PINO_LUZ, e, oscuro, 0.3f))
        }
        drawCircle(CAFE.copy(alpha = if (oscuro) 0.3f else 0.2f), 7f * u, Offset(w - 16f * u, h - 9f * u), style = Stroke(1.1f * u))
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        // El fondo: tablas recicladas de pie, cada una de su tono, muy tenues.
        val anchoTabla = 12f * u
        val nt = (w / anchoTabla).toInt().coerceIn(1, 60) + 1
        for (k in 0 until nt) {
            drawRect(tabla(k, rnd).copy(alpha = if (oscuro) 0.06f else 0.08f), Offset(k * anchoTabla, 0f), Size(anchoTabla, h))
            drawLine(e.tinta.copy(alpha = 0.06f), Offset(k * anchoTabla, 0f), Offset(k * anchoTabla, h), 0.35f * u)
        }
        val madera = m(PINO, e, oscuro, 0.4f); val oscura = m(VETA, e, oscuro, 0.3f)
        val y = h * 0.8f
        // Una caja de fruta tumbada por medalla, con sus tablillas al fondo, sus clavos y su marca de embarque.
        val paso = w * 0.92f / 5f
        repeat(5) { i ->
            val cx = w * 0.04f + paso * (i + 0.5f)
            val cw = paso * 0.88f
            val caja = Rect(cx - cw / 2, h * 0.1f, cx + cw / 2, y)
            val tono = m(tabla(i + 1, rnd), e, oscuro, 0.4f)
            drawRect(tono.copy(alpha = 0.10f), caja.topLeft, caja.size)
            for (k in 1..3) drawLine(oscura.copy(alpha = 0.16f), Offset(caja.left + 2f * u, caja.top + caja.height * k / 4f), Offset(caja.right - 2f * u, caja.top + caja.height * k / 4f), 0.5f * u)
            val canto = 2f * u
            drawRect(tono, caja.topLeft, Size(caja.width, canto))
            drawRect(tono, caja.topLeft, Size(canto, caja.height))
            drawRect(tono, Offset(caja.right - canto, caja.top), Size(canto, caja.height))
            drawRect(oscura.copy(alpha = 0.6f), caja.topLeft, caja.size, style = Stroke(0.35f * u))
            listOf(Offset(caja.left + canto / 2, caja.top + canto / 2), Offset(caja.right - canto / 2, caja.top + canto / 2)).forEach { clavo(it, 0.5f * u, m(CLAVO, e, oscuro, 0.2f)) }
            marcaEmbarque(Offset(caja.right - 6f * u, caja.top + 6.2f * u), 2.4f * u, i + rnd.nextInt(4), (if (i % 2 == 0) m(MARINO, e, oscuro, 0.05f) else m(LADRILLO, e, oscuro, 0.1f)).copy(alpha = 0.7f))
        }
        // La tabla del estante con sus ménsulas.
        drawRect(madera, Offset(w * 0.02f, y), Size(w * 0.96f, 3f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(w * 0.02f, y), Offset(w * 0.98f, y), 0.5f * u)
        drawRect(oscura.copy(alpha = 0.6f), Offset(w * 0.02f, y), Size(w * 0.96f, 3f * u), style = Stroke(0.35f * u))
        listOf(w * 0.06f, w * 0.94f).forEach { x ->
            drawPath(Path().apply { moveTo(x - 2.5f * u, y + 3f * u); lineTo(x + 2.5f * u, y + 3f * u); lineTo(x + 2.5f * u, y + 11f * u); close() }, oscura.copy(alpha = 0.7f))
        }
        // La tarjeta de envío de cada caja, colgada del clavo con su cordel.
        val cordel = m(CORDEL, e, oscuro, 0.25f)
        repeat(5) { i ->
            val cx = w * 0.04f + paso * (i + 0.5f) + (rnd.nextFloat() - 0.5f) * 4f * u
            val clv = Offset(cx, y + 1.5f * u)
            clavo(clv, 0.6f * u, m(CLAVO, e, oscuro, 0.2f))
            val t = Rect(cx - 5f * u, y + 6f * u, cx + 5f * u, minOf(y + 15f * u, h - 1f * u))
            if (t.height > 3f * u) {
                val giro = (rnd.nextFloat() - 0.5f) * 14f
                drawLine(cordel, clv, Offset(cx, t.top + 1.4f * u), 0.35f * u)
                rotate(giro, clv) {
                    val tarjeta = Path().apply {
                        moveTo(t.left + 2f * u, t.top); lineTo(t.right - 2f * u, t.top); lineTo(t.right, t.top + 2f * u)
                        lineTo(t.right, t.bottom); lineTo(t.left, t.bottom); lineTo(t.left, t.top + 2f * u); close()
                    }
                    drawPath(tarjeta, m(MANILA, e, oscuro, 0.5f))
                    drawPath(tarjeta, oscura.copy(alpha = 0.5f), style = Stroke(0.3f * u))
                    drawCircle(oscura, 0.7f * u, Offset(cx, t.top + 1.4f * u), style = Stroke(0.3f * u))
                    drawLine(oscura.copy(alpha = 0.4f), Offset(t.left + 2f * u, t.top + t.height * 0.6f), Offset(t.right - 2f * u, t.top + t.height * 0.6f), 0.3f * u)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.36f
        val g = 1.6f * density
        val trazo = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (id) {
            "unesco" -> {
                // El ayuntamiento reconstruido: el cuerpo con su techo de ladrillo, la torre con su campana y la bandera, y el andamio.
                val cuerpo = Rect(c.x - r * 1.05f, c.y + r * 0.05f, c.x + r * 0.75f, c.y + r * 0.85f)
                drawRect(e.c1.copy(alpha = 0.15f), cuerpo.topLeft, cuerpo.size)
                for (k in 1..3) drawLine(e.c1.copy(alpha = 0.35f), Offset(cuerpo.left, cuerpo.top + cuerpo.height * k / 4f), Offset(cuerpo.right, cuerpo.top + cuerpo.height * k / 4f), g * 0.5f)
                drawRect(e.c1, cuerpo.topLeft, cuerpo.size, style = trazo)
                drawPath(Path().apply { moveTo(cuerpo.left - r * 0.12f, cuerpo.top); lineTo(c.x - r * 0.15f, c.y - r * 0.38f); lineTo(cuerpo.right + r * 0.12f, cuerpo.top); close() }, e.c2)
                listOf(-0.75f, 0.45f).forEach { k -> drawRect(e.c1, Offset(c.x + k * r, c.y + r * 0.28f), Size(r * 0.22f, r * 0.3f)) }
                drawRect(e.c2, Offset(c.x - r * 0.3f, c.y + r * 0.45f), Size(r * 0.3f, r * 0.4f))
                val torre = Rect(c.x - r * 0.32f, c.y - r * 0.72f, c.x + r * 0.02f, c.y - r * 0.2f)
                drawRect(e.fondo, torre.topLeft, torre.size)
                drawRect(e.c1, torre.topLeft, torre.size, style = trazo)
                drawArc(e.c2, 180f, 180f, true, Offset(torre.center.x - r * 0.1f, torre.top + r * 0.12f), Size(r * 0.2f, r * 0.24f))
                drawPath(Path().apply { moveTo(torre.left - r * 0.06f, torre.top); lineTo(torre.center.x, torre.top - r * 0.26f); lineTo(torre.right + r * 0.06f, torre.top); close() }, e.c1)
                drawLine(e.c1, Offset(torre.center.x, torre.top - r * 0.26f), Offset(torre.center.x, torre.top - r * 0.62f), g * 0.8f)
                drawPath(Path().apply { moveTo(torre.center.x, torre.top - r * 0.62f); lineTo(torre.center.x + r * 0.36f, torre.top - r * 0.52f); lineTo(torre.center.x, torre.top - r * 0.42f); close() }, e.c2)
                // El andamio de la obra, a la derecha.
                val ax0 = cuerpo.right + r * 0.1f; val ax1 = cuerpo.right + r * 0.5f
                listOf(ax0, ax1).forEach { x -> drawLine(e.c1, Offset(x, c.y - r * 0.2f), Offset(x, cuerpo.bottom), g * 0.7f) }
                listOf(0.1f, 0.5f).forEach { k -> drawLine(e.c1, Offset(ax0 - r * 0.05f, c.y + r * k), Offset(ax1 + r * 0.05f, c.y + r * k), g * 0.7f) }
                drawLine(e.c1, Offset(ax0, c.y + r * 0.5f), Offset(ax1, c.y + r * 0.1f), g * 0.5f)
                drawLine(e.c2, Offset(c.x - r * 1.25f, cuerpo.bottom), Offset(ax1 + r * 0.2f, cuerpo.bottom), g * 1.2f, StrokeCap.Round)
            }
            "rascar" -> {
                // La gaceta raspada: la cabecera, las tres columnas de renglones y la esquina descubierta, con la moneda que raspa.
                val hojaG = Rect(c.x - r * 1.35f, c.y - r * 0.9f, c.x + r * 1.15f, c.y + r * 0.9f)
                drawRect(e.fondo, hojaG.topLeft, hojaG.size)
                drawRect(e.c1.copy(alpha = 0.1f), hojaG.topLeft, hojaG.size)
                drawRect(e.c1, Offset(hojaG.left + r * 0.12f, hojaG.top + r * 0.14f), Size(hojaG.width - r * 0.24f, r * 0.22f))
                drawLine(e.c1, Offset(hojaG.left + r * 0.12f, hojaG.top + r * 0.48f), Offset(hojaG.right - r * 0.12f, hojaG.top + r * 0.48f), g * 0.5f)
                val colAncho = (hojaG.width - r * 0.24f - r * 0.2f) / 3f
                for (col in 0 until 3) for (fila in 0 until 5) {
                    val x = hojaG.left + r * 0.12f + col * (colAncho + r * 0.1f)
                    val yy = hojaG.top + r * (0.66f + fila * 0.22f)
                    drawLine(e.c1.copy(alpha = 0.55f), Offset(x, yy), Offset(x + colAncho * (if (fila == 4) 0.6f else 1f), yy), g * 0.5f)
                }
                val raspado = Path().apply {
                    moveTo(hojaG.right, hojaG.top + hojaG.height * 0.38f); lineTo(hojaG.right, hojaG.bottom); lineTo(hojaG.right - hojaG.width * 0.5f, hojaG.bottom)
                    lineTo(hojaG.right - hojaG.width * 0.42f, hojaG.bottom - hojaG.height * 0.16f); lineTo(hojaG.right - hojaG.width * 0.33f, hojaG.bottom - hojaG.height * 0.12f)
                    lineTo(hojaG.right - hojaG.width * 0.28f, hojaG.bottom - hojaG.height * 0.34f); lineTo(hojaG.right - hojaG.width * 0.16f, hojaG.bottom - hojaG.height * 0.3f)
                    lineTo(hojaG.right - hojaG.width * 0.12f, hojaG.top + hojaG.height * 0.42f); close()
                }
                drawPath(raspado, e.c2)
                drawRect(e.c1, hojaG.topLeft, hojaG.size, style = trazo)
                val mo = Offset(hojaG.right + r * 0.05f, hojaG.bottom - r * 0.05f)
                drawCircle(e.fondo, r * 0.36f, mo)
                drawCircle(e.c1, r * 0.36f, mo, style = trazo)
                drawCircle(e.c1, r * 0.22f, mo, style = Stroke(g * 0.6f))
            }
            else -> {
                // Tus lugares: tres banderines clavados en su montoncito de tierra.
                listOf(Offset(-0.95f, 0.15f) to 0.85f, Offset(0f, -0.2f) to 1.05f, Offset(0.95f, 0.05f) to 0.9f).forEachIndexed { i, (o, k) ->
                    val pie = Offset(c.x + o.x * r, c.y + o.y * r + r * 0.65f)
                    drawArc(e.c1.copy(alpha = 0.35f), 180f, 180f, true, Offset(pie.x - r * 0.3f * k, pie.y - r * 0.12f), Size(r * 0.6f * k, r * 0.24f))
                    val alto = r * 1.35f * k
                    drawLine(e.c1, pie, Offset(pie.x, pie.y - alto), g, StrokeCap.Round)
                    drawPath(Path().apply { moveTo(pie.x, pie.y - alto); lineTo(pie.x + r * 0.62f * k, pie.y - alto + r * 0.24f * k); lineTo(pie.x, pie.y - alto + r * 0.48f * k); close() },
                        if (i == 1) e.c1 else e.c2)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        val oscura = m(VETA, e, oscuro, 0.3f)
        // El tablero de tablas de pie, cada una de su tono, con sus juntas y alguna veta.
        val campo = Rect(2.4f * u, 4.6f * u, w - 2.4f * u, h - 2.4f * u)
        val nt = (campo.width / (14f * u)).toInt().coerceIn(3, 40)
        val at = campo.width / nt
        for (k in 0 until nt) {
            val x = campo.left + k * at
            drawRect(mezcla(e.fondo, tabla(k, rnd), if (oscuro) 0.3f else 0.45f), Offset(x, campo.top), Size(at, campo.height))
            drawLine(e.tinta.copy(alpha = 0.18f), Offset(x, campo.top), Offset(x, campo.bottom), 0.4f * u)
            val vx = x + at * (0.3f + 0.4f * rnd.nextFloat())
            drawLine(oscura.copy(alpha = 0.16f), Offset(vx, campo.top + 2f * u), Offset(vx + 0.6f * u, campo.bottom - 2f * u), 0.3f * u)
        }
        // El marco de tablones y un parche de lata clavado.
        drawRect(m(PINO_OSC, e, oscuro, 0.4f), campo.topLeft, campo.size, style = Stroke(1.6f * u))
        val parche = Rect(campo.left + 3f * u, campo.bottom - 9f * u, campo.left + 11f * u, campo.bottom - 3f * u)
        drawRect(m(LATA, e, oscuro, 0.45f), parche.topLeft, parche.size)
        listOf(parche.topLeft, Offset(parche.right, parche.top), Offset(parche.left, parche.bottom), Offset(parche.right, parche.bottom)).forEach { o ->
            clavo(Offset(o.x + (if (o.x < parche.center.x) 0.9f else -0.9f) * u, o.y + (if (o.y < parche.center.y) 0.9f else -0.9f) * u), 0.4f * u, m(CLAVO, e, oscuro, 0.2f))
        }
        // El tejadillo de tejas de ladrillo arriba, con su sombra.
        val teja = m(LADRILLO, e, oscuro, 0.3f)
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(campo.left, campo.top), Size(campo.width, 1.2f * u))
        drawRect(teja, Offset(0f, 0.6f * u), Size(w, 4f * u))
        val nTejas = (w / (5f * u)).toInt().coerceIn(1, 120)
        for (k in 0..nTejas) drawArc(mezcla(teja, Color.Black, 0.25f), 0f, 180f, false, Offset(k * 5f * u - 2.5f * u, 2.4f * u), Size(5f * u, 2.6f * u), style = Stroke(0.35f * u))
        // Los cordeles cruzados de lado a lado, con sus tachuelas.
        val cordel = m(CORDEL, e, oscuro, 0.2f).copy(alpha = 0.85f)
        val a1 = Offset(campo.left + 2f * u, campo.top + 4f * u); val b1 = Offset(campo.right - 2f * u, campo.bottom - 4f * u)
        val a2 = Offset(campo.left + 2f * u, campo.bottom - 4f * u); val b2 = Offset(campo.right - 2f * u, campo.top + 4f * u)
        drawLine(cordel, a1, b1, 0.5f * u)
        drawLine(cordel, a2, b2, 0.5f * u)
        listOf(a1, b1, a2, b2).forEach { clavo(it, 0.9f * u, m(LATON, e, oscuro, 0.2f)) }
    }

    /** El papel de gaceta de la nota: crema de día; de noche, a la luz del farol, pardo oscuro (la tinta se lee en los dos). */
    private fun papel(tipo: String, oscuro: Boolean): Color = when (tipo) {
        "idea" -> if (oscuro) Color(0xFF2F2B24) else Color(0xFFEDE4CC)
        "proposito" -> if (oscuro) Color(0xFF2B2E2A) else Color(0xFFE6E2D2)
        "recordatorio" -> if (oscuro) Color(0xFF342A22) else Color(0xFFEFDFC6)
        else -> if (oscuro) Color(0xFF2C2A2C) else Color(0xFFE9E3D3)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val k = size.minDimension / 100f
        if (k <= 0f) return
        val papel = papel(e.tipo, oscuro)
        val tinta = tintaNota(oscuro)
        val hoja = Rect(r.left + 1f * k, r.top + 1.5f * k, r.right - 2f * k, r.bottom - 2f * k)
        val contorno = recorte(hoja, 8, 1.6f * k, e.indice + 11)
        // La sombra, el recorte y sus filetes de columna arriba y abajo (en el margen).
        drawPath(recorte(Rect(hoja.left + 1.6f * k, hoja.top + 2f * k, hoja.right + 1.6f * k, hoja.bottom + 2f * k), 8, 1.6f * k, e.indice + 11), Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f))
        drawPath(contorno, papel)
        drawLine(tinta.copy(alpha = 0.4f), Offset(hoja.left + hoja.width * 0.08f, hoja.top + hoja.height * 0.1f), Offset(hoja.right - hoja.width * 0.2f, hoja.top + hoja.height * 0.1f), 0.9f * k)
        drawLine(tinta.copy(alpha = 0.25f), Offset(hoja.left + hoja.width * 0.08f, hoja.bottom - hoja.height * 0.08f), Offset(hoja.right - hoja.width * 0.08f, hoja.bottom - hoja.height * 0.08f), 0.6f * k)
        // La marca estarcida del tipo, arriba a la derecha; el recordatorio lleva además la esquina doblada.
        val marca = e.acento.copy(alpha = 0.85f)
        val ladrillo = if (oscuro) Color(0xFFE07A62) else LADRILLO
        val mc = Offset(hoja.right - hoja.width * 0.075f, hoja.top + hoja.height * 0.09f)
        val s = hoja.height * 0.055f
        when (e.tipo) {
            "idea" -> drawPath(estrella(mc, s * 1.2f, 5, 0.42f), marca)
            "proposito" -> drawPath(Path().apply { moveTo(mc.x, mc.y - s); lineTo(mc.x + s, mc.y); lineTo(mc.x, mc.y + s); lineTo(mc.x - s, mc.y); close() }, ladrillo)
            "recordatorio" -> {
                val d = hoja.height * 0.2f
                drawPath(Path().apply { moveTo(hoja.right - d, hoja.bottom); lineTo(hoja.right, hoja.bottom - d); lineTo(hoja.right - d, hoja.bottom - d); close() }, mezcla(papel, Color.Black, 0.18f))
                drawCircle(ladrillo, s * 0.8f, mc)
            }
            else -> drawCircle(marca, s * 0.85f, mc, style = Stroke(0.9f * k))
        }
        // La sujeción: la chincheta de latón si está fijada; si no, una tira de cinta amarillenta.
        if (e.fijada) {
            val tc = Offset(hoja.center.x, hoja.top + hoja.height * 0.07f)
            val tr = hoja.height * 0.07f
            drawCircle(Color.Black.copy(alpha = 0.3f), tr, Offset(tc.x + 0.8f * k, tc.y + 1f * k))
            drawCircle(LATON, tr, tc)
            drawCircle(Color.White.copy(alpha = 0.45f), tr * 0.36f, Offset(tc.x - tr * 0.3f, tc.y - tr * 0.3f))
        } else {
            val tc = Offset(hoja.center.x + (if (e.indice % 2 == 0) -1f else 1f) * hoja.width * 0.12f, hoja.top + 0.5f * k)
            rotate(if (e.indice % 2 == 0) -6f else 5f, tc) {
                drawRect(CINTA.copy(alpha = if (oscuro) 0.45f else 0.7f), Offset(tc.x - hoja.width * 0.13f, tc.y - hoja.height * 0.045f), Size(hoja.width * 0.26f, hoja.height * 0.09f))
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFEEE7D7) else TINTA
}
