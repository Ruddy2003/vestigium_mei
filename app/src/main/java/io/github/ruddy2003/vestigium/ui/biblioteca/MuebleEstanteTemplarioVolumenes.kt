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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Las entradas del estante de Templario ([MuebleEstanteTemplario]), tres objetos de la cámara alta según la semilla: el libro de
 * la casa (un códice de tablas forradas de piel teñida, de pie, con sus nervios dobles, las lengüetas de las cabezadas, los
 * bullones que asoman por el canto de las tablas y el tejuelo de pergamino), el estuche de cuero hervido para un rollo (alto,
 * con la tapa honda de hombros redondos, las orejas por donde pasa la correa, la labor incisa y la etiqueta colgada de un
 * cabo) y la escarcela de las cartas (blanda, fruncida por su cordón, con las cartas dobladas que asoman). El ánimo es la piel,
 * en todo el cuerpo ([cueroTemplario]); sin ánimo, cuero sin teñir. En los Sueños, una media luna de plata con su disco apagado.
 */
internal object VolumenEstanteTemplario {
    private val P = PaletaTemplario

    /** El pergamino de las etiquetas: viejo y algo tostado, de valor medio, para que no grite más que el objeto. */
    private val VIEJO = Color(0xFFD5C094)
    /** El cuero sin teñir de lo que no tiene ánimo. */
    private val CRUDO = Color(0xFF5A4636)

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.4f, 0.8f, 0.45f, 0.1f, -0.22f, -0.52f, -0.8f)

    /** La luz que le toca a una entrada: la del momento ([k]) y, en los Sueños oscuros, la luna del ajimez ([luna]). */
    private class Luz(val a: AireTemplario, val k: Float, val luna: Float) {
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
    }

    /** Los colores de una piel con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(piel: Color?, l: Luz): Tinte {
        val m = piel ?: CRUDO
        val claro = mezcla(m, luzMueble(1f), if (piel == null) 0.2f else 0.32f)
        val hondo = mezcla(m, sombraMueble(1f), 0.55f)
        return if (piel != null) Tinte(l.animo(m), l.animo(claro), l.animo(hondo)) else Tinte(l.c(m), l.c(claro), l.c(hondo))
    }

    /** Un cuerpo redondo en [r] por franjas opacas (sin degradados): el filo, el claro, la cara, el hondo, hacia [hacia]. */
    private fun DrawScope.redondo(r: Rect, t: Tinte, hacia: Float) {
        for (i in 0 until 7) {
            val f0 = FRANJAS[i]; val f1 = FRANJAS[i + 1]
            val m = TONOS[i]
            val c = if (m >= 0f) mezcla(t.cara, t.claro, m) else mezcla(t.cara, t.hondo, -m)
            // Cada franja se mete un píxel en la que se pinta después, para que las juntas no dejen ver lo de detrás.
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
        textoEn(medidor, v.dia, Rect(r.left, y0, r.right, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.SemiBold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left, y0 + hd - 1.2f * u, r.right, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.85f), MES, minimo = 5f)
    }

    /** La media luna de plata, de radio [r], armada una vez por tamaño. */
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
     * La luna de los Sueños: el disco apagado de la luna (la luz cenicienta) y la media luna de plata encima, con su canto oscuro
     * y su sombra; así se lee luna y no emblema.
     */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireTemplario) {
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

    /** Lo de todas: la sombra que dejan en el fondo por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireTemplario, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.42f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.42f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche) y el roce frío de la luna. */
    private fun DrawScope.filos(cuerpo: Rect, a: AireTemplario, k: Float, hacia: Float, luna: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.7f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (luna > 0f) drawRect(a.lunar(0.6f * luna), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    /** Una etiqueta de pergamino en [et] con el día y el mes, su sombra y su borde gastado. */
    private fun DrawScope.etiqueta(v: Volumen, medidor: TextMeasurer, et: Rect, l: Luz, a: AireTemplario, giro: Float = 0f) {
        val u = density
        rotate(giro, et.center) {
            drawRect(a.sombra(0.35f), Offset(et.left + 0.9f * u, et.top + 1.1f * u), et.size)
            drawRect(l.etiqueta(VIEJO), et.topLeft, et.size)
            drawRect(l.etiqueta(P.PERG_OSC).copy(alpha = 0.5f), Offset(et.left, et.bottom - et.height * 0.12f), Size(et.width, et.height * 0.12f))
            drawRect(l.etiqueta(P.PERG_OSC).copy(alpha = 0.4f), et.topLeft, Size(et.width * 0.08f, et.height))
            drawRect(a.luz(P.TINTA, l.k).copy(alpha = 0.5f), et.deflate(1.3f * u).topLeft, et.deflate(1.3f * u).size, style = Stroke(0.5f * u))
            diaYMes(v, medidor, et.deflate(1.6f * u), if (a.oscuro) Color(0xFF140C06) else a.luz(P.TINTA, maxOf(l.k, 0.75f)))
        }
    }

    // ---------------------------------------------------------------- el libro de la casa

    fun DrawScope.libro(v: Volumen, medidor: TextMeasurer, a: AireTemplario, k: Float, hacia: Float, piel: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val tab = 4.2f * u
        alrededor(a, hacia, tab)
        val t = tinte(piel, l)
        // El canto de las tablas de roble forradas, a los dos lados del lomo.
        val tabla = w * 0.13f
        val lomo = Rect(tabla, tab, w - tabla, h - tab * 0.6f)
        val tablas = Rect(0f, tab + 1f * u, w, h)
        drawRoundRect(t.hondo, tablas.topLeft, tablas.size, CornerRadius(1.6f * u))
        drawRect(t.cara, Offset(0f, tablas.top), Size(tabla * 0.55f, tablas.height))
        drawRect(t.cara, Offset(w - tabla * 0.55f, tablas.top), Size(tabla * 0.55f, tablas.height))
        drawRect(l.c(P.CEDRO_LUZ, -0.1f).copy(alpha = 0.35f), Offset(tabla * 0.55f, tablas.top), Size(tabla * 0.45f, tablas.height))
        // Los bullones de latón que asoman por el canto de las tablas, arriba y abajo.
        listOf(0.16f, 0.88f).forEach { fy ->
            listOf(0f, w).forEach { bx ->
                val c = Offset(bx, tablas.top + tablas.height * fy)
                drawCircle(a.sombra(0.35f), 2.6f * u, c + Offset(0.6f * u, 0.8f * u))
                drawCircle(l.c(P.LATON), 2.4f * u, c)
                drawCircle(a.brillo(0.6f, k), 0.9f * u, c - Offset(0.7f * u, 0.7f * u))
            }
        }
        // Las lengüetas de las cabezadas, arriba y abajo del lomo.
        val lw = w * 0.42f
        drawRoundRect(t.hondo, Offset(w / 2 - lw / 2, 0f), Size(lw, tab * 2f), CornerRadius(lw / 2, tab))
        drawRoundRect(t.claro, Offset(w / 2 - lw / 2 + 0.8f * u, 0.6f * u), Size(lw - 1.6f * u, tab * 0.9f), CornerRadius(lw / 2, tab))
        // El lomo redondo, por franjas.
        redondo(lomo, t, hacia)
        // El roce del cuero y una mancha.
        for (j in 0 until 4) {
            val y = lomo.top + lomo.height * (0.1f + 0.85f * azar(s, 50 + j))
            drawLine(a.claro(0.16f), Offset(lomo.left + lomo.width * 0.2f * azar(s, 54 + j), y), Offset(lomo.left + lomo.width * (0.3f + 0.5f * azar(s, 58 + j)), y + 0.6f * u), 0.8f * u)
        }
        drawOval(a.sombra(0.12f), Offset(lomo.left + lomo.width * 0.1f, lomo.top + lomo.height * (0.8f + 0.1f * azar(s, 62))), Size(lomo.width * 0.6f, h * 0.05f))
        // Los nervios dobles: cada uno, dos lomos chicos con su luz y su sombra.
        val nervios = if (azar(s, 7) > 0.35f) listOf(0.49f, 0.65f, 0.81f) else listOf(0.52f, 0.74f)
        nervios.forEach { fy ->
            listOf(0f, 3.4f * u).forEach { dy ->
                val y = h * fy + dy
                drawRect(a.sombra(0.42f), Offset(lomo.left, y + 1.6f * u), Size(lomo.width, 1.1f * u))
                drawRect(t.hondo, Offset(lomo.left, y), Size(lomo.width, 2.2f * u))
                drawRect(t.claro, Offset(lomo.left, y - 0.5f * u), Size(lomo.width, 1.3f * u))
            }
            drawRect(a.brillo(0.3f, k), Offset(if (hacia < 0f) lomo.left + lomo.width * 0.12f else lomo.right - lomo.width * 0.4f, h * fy - 0.4f * u), Size(lomo.width * 0.28f, 0.8f * u))
        }
        // El tejuelo de pergamino, entre la cabeza y el primer nervio.
        val et = Rect(lomo.left + 1f * u, h * 0.12f, lomo.right - 1f * u, h * 0.12f + 30f * u)
        etiqueta(v, medidor, Rect(et.left, et.top, et.right, minOf(et.bottom, h * nervios[0] - 2f * u)), l, a)
        // Los registros de cinta que cuelgan por el pie, más con más palabras.
        val cintas = (v.lleno * 3.2f).toInt().coerceIn(0, 3)
        val colores = listOf(P.CRUZ, Color(0xFF2E5E3A), P.LATON)
        for (j in 0 until cintas) {
            val x = lomo.left + lomo.width * (0.3f + 0.18f * j)
            drawLine(l.c(colores[(j + (s and 1)) % 3]), Offset(x, h - tab), Offset(x + (azar(s, 70 + j) - 0.5f) * 2f * u, h + 2.4f * u), 1.6f * u)
        }
        // La luna de los Sueños, entre el segundo y el tercer nervio (o bajo el último).
        if (v.noche) luna(Offset(w / 2, h * (if (nervios.size == 3) 0.74f else 0.63f) + 1.7f * u), minOf(lomo.width * 0.24f, 5.6f * u), a)
        filos(lomo, a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- el estuche de cuero hervido

    fun DrawScope.estuche(v: Volumen, medidor: TextMeasurer, a: AireTemplario, k: Float, hacia: Float, piel: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val t = tinte(piel, l)
        // La tapa honda que se encaja por fuera del cuerpo, de hombros redondos; el cuerpo, algo más ancho abajo.
        val tapa = h * (0.27f + 0.05f * azar(s, 9))
        val hombro = w * 0.2f
        alrededor(a, hacia, 1f * u)
        val ent = w * 0.07f
        val cuerpo = Path().apply { moveTo(ent, tapa - 2f * u); lineTo(w - ent, tapa - 2f * u); lineTo(w - 1f * u, h); lineTo(1f * u, h); close() }
        clipPath(cuerpo) { redondo(Rect(0f, tapa, w, h), t, hacia) }
        // La labor incisa del cuerpo: dos bandas de filetes, una con zigzag y otra con puntos, y la costura de arriba abajo.
        val labor = Path()
        val bandas = listOf(0.72f + 0.04f * azar(s, 13), 0.9f)
        bandas.forEachIndexed { i, fy ->
            val y = h * fy
            labor.moveTo(0f, y); labor.lineTo(w, y)
            labor.moveTo(0f, y + 5f * u); labor.lineTo(w, y + 5f * u)
            if ((i + (s and 1)) % 2 == 0) {
                var x = 0f; var alto = true
                labor.moveTo(x, y + 4.4f * u)
                while (x < w) { x += 3f * u; labor.lineTo(x, y + (if (alto) 0.6f else 4.4f) * u); alto = !alto }
            }
        }
        clipPath(cuerpo) {
            drawPath(labor, t.hondo, style = Stroke(0.8f * u))
            translate(0.5f * u, 0.6f * u) { drawPath(labor, t.claro.copy(alpha = 0.5f), style = Stroke(0.5f * u)) }
            if ((s and 1) == 1) { var x = 3f * u; while (x < w) { drawCircle(t.hondo, 0.8f * u, Offset(x, h * bandas[0] + 2.5f * u)); x += 4.2f * u } }
            val cx = if (hacia < 0f) w * 0.72f else w * 0.28f
            var y = tapa + 2f * u
            while (y < h - 2f * u) { drawLine(t.hondo, Offset(cx, y), Offset(cx, y + 1.6f * u), 0.9f * u); y += 3.6f * u }
            drawRect(a.sombra(0.25f), Offset(0f, h - 3f * u), Size(w, 3f * u))
            for (j in 0 until 3) drawLine(a.claro(0.18f), Offset(w * azar(s, 80 + j), h * (0.45f + 0.2f * azar(s, 83 + j))), Offset(w * azar(s, 86 + j), h * (0.47f + 0.2f * azar(s, 83 + j))), 0.8f * u)
        }
        // La tapa: un cilindro más ancho con hombros redondos y el reborde de abajo, más oscura.
        val tp = Path().apply {
            moveTo(0.4f * u, tapa)
            lineTo(0.4f * u, hombro * 0.9f)
            quadraticTo(0.4f * u, 1.2f * u, hombro, 0.6f * u)
            lineTo(w - hombro, 0.6f * u)
            quadraticTo(w - 0.4f * u, 1.2f * u, w - 0.4f * u, hombro * 0.9f)
            lineTo(w - 0.4f * u, tapa); close()
        }
        clipPath(tp) {
            redondo(Rect(0f, 0f, w, tapa), Tinte(mezcla(t.cara, t.hondo, 0.22f), t.claro, t.hondo), hacia)
            drawRect(t.claro.copy(alpha = 0.55f), Offset(hombro * 0.6f, 0.6f * u), Size(w - hombro * 1.2f, 1.4f * u))
            // Una banda de zigzag en la tapa.
            val z = Path()
            var x = 0f; var alto = true
            val yz = tapa * 0.48f
            z.moveTo(0f, yz); z.lineTo(w, yz); z.moveTo(0f, yz + 5f * u); z.lineTo(w, yz + 5f * u)
            z.moveTo(0f, yz + 4.4f * u)
            while (x < w) { x += 3f * u; z.lineTo(x, yz + (if (alto) 0.6f else 4.4f) * u); alto = !alto }
            drawPath(z, t.hondo, style = Stroke(0.8f * u))
        }
        drawRect(t.claro, Offset(0f, tapa - 3.4f * u), Size(w, 1.2f * u))
        drawRect(t.hondo, Offset(0f, tapa - 2.2f * u), Size(w, 2.2f * u))
        drawRect(a.sombra(0.45f), Offset(ent, tapa), Size(w - 2 * ent, 2f * u))
        // Las orejas de los lados (en la tapa y en el cuerpo) y la correa que pasa por ellas, de arriba abajo.
        val correa = l.c(if ((s shr 2) and 1 == 0) Color(0xFF3A2414) else Color(0xFFCDB47C), -0.05f)
        listOf(1.4f * u, w - 1.4f * u).forEach { ox ->
            drawLine(a.sombra(0.4f), Offset(ox + 0.8f * u, tapa * 0.3f), Offset(ox + 0.8f * u, h * 0.86f), 2.2f * u)
            drawLine(correa, Offset(ox, tapa * 0.28f), Offset(ox, h * 0.86f), 1.8f * u)
            listOf(tapa * 0.3f, tapa + h * 0.08f, h * 0.84f).forEach { oy ->
                drawRoundRect(t.hondo, Offset(ox - 2.2f * u, oy - 3f * u), Size(4.4f * u, 6f * u), CornerRadius(1.6f * u))
                drawRect(t.claro.copy(alpha = 0.6f), Offset(ox - 2.2f * u, oy - 3f * u), Size(4.4f * u, 0.8f * u))
            }
        }
        // La etiqueta, colgada de un cabo que sale de la oreja de la tapa, algo ladeada.
        val et = Rect(2.6f * u, tapa + h * 0.06f, w - 2.6f * u, tapa + h * 0.06f + 30f * u)
        val giro = (azar(s, 11) - 0.5f) * 6f
        val oreja = Offset(if (hacia < 0f) w - 1.4f * u else 1.4f * u, tapa * 0.3f)
        drawLine(correa, oreja, Offset(w * 0.5f, et.top + 2f * u), 1f * u)
        etiqueta(v, medidor, et, l, a, giro)
        drawCircle(a.sombra(0.5f), 1f * u, Offset(w * 0.5f, et.top + 2.6f * u))
        // La luna de los Sueños, en la tapa.
        if (v.noche) luna(Offset(w / 2, tapa * 0.5f + 1f * u), minOf(w * 0.2f, 4.8f * u), a)
        filos(Rect(0f, 0f, w, h), a, k, hacia, luna)
    }

    // ---------------------------------------------------------------- la escarcela de las cartas

    fun DrawScope.escarcela(v: Volumen, medidor: TextMeasurer, a: AireTemplario, k: Float, hacia: Float, piel: Color?, luna: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, luna)
        val t = tinte(piel, l)
        val cuello = h * (0.4f + 0.04f * azar(s, 3))
        val lado = if (azar(s, 5) > 0.5f) 1f else -1f
        alrededor(a, hacia, cuello)
        // Las cartas que asoman por el cuello: más con más palabras; la de delante lleva el día.
        val cartas = 1 + (v.lleno * 2.4f).toInt().coerceIn(0, 2)
        for (j in cartas - 1 downTo 1) {
            val x0 = w * (if (j == 1) -0.06f else 0.42f); val alto = cuello * (1.02f + 0.06f * j)
            rotate((if (j == 1) -9f else 8f), Offset(x0 + w * 0.32f, cuello)) {
                drawRect(l.etiqueta(mezcla(VIEJO, P.PERG_OSC, 0.3f * j)), Offset(x0, cuello - alto), Size(w * 0.64f, alto))
                drawRect(a.sombra(0.25f), Offset(x0 + w * 0.5f, cuello - alto), Size(w * 0.14f, alto))
                drawLine(a.sombra(0.3f), Offset(x0, cuello - alto * 0.55f), Offset(x0 + w * 0.64f, cuello - alto * 0.5f), 0.7f * u)
            }
        }
        val et = Rect(2.4f * u, cuello - 30f * u - h * 0.01f, w - 2.4f * u, cuello - h * 0.01f)
        // La bolsa: blanda, el fondo aplastado en la tabla y la panza caída hacia un lado.
        val bolsa = Path().apply {
            moveTo(w * 0.22f, cuello)
            cubicTo(w * (0.02f - 0.03f * lado), cuello + h * 0.1f, -0.6f * u, h * 0.66f, 0.8f * u, h * 0.9f)
            cubicTo(1.2f * u, h * 0.985f, w * 0.25f, h, w * 0.5f, h)
            cubicTo(w * 0.75f, h, w - 1.2f * u, h * 0.985f, w - 0.8f * u, h * 0.9f)
            cubicTo(w + 0.6f * u, h * 0.66f, w * (0.98f - 0.03f * lado), cuello + h * 0.1f, w * 0.78f, cuello)
            close()
        }
        clipPath(bolsa) {
            redondo(Rect(0f, cuello, w, h), t, hacia)
            // Los pliegues que bajan del frunce, torcidos, y una arruga de través abajo.
            for (j in 0 until 4) {
                val x = w * (0.24f + 0.17f * j)
                val bajo = cuello + h * (0.2f + 0.14f * azar(s, 20 + j))
                val p = Path().apply { moveTo(x, cuello); cubicTo(x + (j - 1.5f) * 2f * u, cuello + h * 0.08f, x + lado * 3f * u, bajo - h * 0.06f, x + (j - 1.5f) * 4f * u + lado * 2f * u, bajo) }
                drawPath(p, a.sombra(0.32f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { addPath(p, Offset(2f * u, 0f)) }, t.claro.copy(alpha = 0.55f), style = Stroke(1f * u, cap = StrokeCap.Round))
            }
            val arruga = Path().apply { moveTo(w * 0.1f, h * 0.8f); quadraticTo(w * 0.45f, h * (0.76f + 0.04f * azar(s, 31)), w * 0.85f, h * 0.83f) }
            drawPath(arruga, a.sombra(0.25f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { addPath(arruga, Offset(0f, -1.6f * u)) }, t.claro.copy(alpha = 0.45f), style = Stroke(0.8f * u))
            drawOval(a.sombra(0.3f), Offset(w * 0.06f, h * 0.9f), Size(w * 0.88f, h * 0.12f))
            // La costura de abajo.
            val punt = Path()
            var x = w * 0.1f
            while (x < w * 0.9f) { punt.moveTo(x, h * 0.95f); punt.lineTo(x + 1.6f * u, h * 0.95f); x += 3.4f * u }
            drawPath(punt, a.dia(P.LINO_OSC, 0f, k).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        }
        // El frunce del cuello: el cuero recogido en picos por encima del cordón.
        val frunce = Path().apply {
            moveTo(w * 0.16f, cuello + 3f * u)
            var x = w * 0.16f; var j = 0
            while (x < w * 0.84f) { x += w * 0.085f; lineTo(x, cuello - (if (j % 2 == 0) 4f else 0.6f) * u); j++ }
            lineTo(w * 0.84f, cuello + 3f * u); close()
        }
        // La carta de delante con el día, metida en el cuello (detrás del frunce).
        etiqueta(v, medidor, et, l, a, (azar(s, 12) - 0.5f) * 4f)
        drawPath(frunce, t.cara)
        drawPath(frunce, t.hondo, style = Stroke(0.8f * u))
        // El cordón, dos vueltas, con el nudo y los dos cabos que cuelgan con sus nudillos.
        val cord = l.c(Color(0xFF3A2414), 0f)
        drawRect(cord, Offset(w * 0.17f, cuello + 2.2f * u), Size(w * 0.66f, 2.2f * u))
        drawRect(cord, Offset(w * 0.18f, cuello + 5f * u), Size(w * 0.64f, 2f * u))
        drawRect(a.brillo(0.3f, k), Offset(w * 0.17f, cuello + 2.2f * u), Size(w * 0.66f, 0.7f * u))
        val nudo = Offset(if (lado > 0f) w * 0.3f else w * 0.7f, cuello + 4f * u)
        drawCircle(cord, 2.4f * u, nudo)
        listOf(-1f, 1f).forEach { q ->
            val fin = Offset(nudo.x + q * 2.4f * u, nudo.y + h * (0.14f + 0.04f * q * lado))
            drawLine(cord, nudo, fin, 1.3f * u, StrokeCap.Round)
            drawCircle(cord, 1.7f * u, fin)
        }
        // La luna de los Sueños: un broche de plata en la bolsa.
        if (v.noche) luna(Offset(w * 0.55f, h * 0.7f), minOf(w * 0.2f, 5f * u), a)
        filos(Rect(0f, cuello, w, h), a, k, hacia, luna)
    }
}
