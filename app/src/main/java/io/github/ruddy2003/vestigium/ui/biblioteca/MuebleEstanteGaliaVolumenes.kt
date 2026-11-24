package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Galia
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Las entradas del estante de Galia ([MuebleEstanteGalia]), tres cosas de la casa según la semilla: la funda de lana a cuadros
 * (alta y angosta, con la boca vuelta y atada con un cordón, de la que asoman las tablillas de haya; el día va en la cera de
 * la de delante), el cofre de mimbre (recto, tejido por hileras, con su tapa, el borde trenzado, las bisagras de correa y la
 * clavija del cierre, de la que cuelga la tablilla del día) y el zurrón de cuero blando (la panza caída, la solapa redonda
 * cosida y cerrada con una fíbula de bronce, de la que cuelga la tablilla del día). El ánimo es el material entero, teñido
 * ([tonoDeDiaGalia]); sin ánimo, lana parda, mimbre con su corteza vieja o cuero sin teñir. En los Sueños, la luna: su disco
 * apagado y la media luna clara encima.
 */
internal object VolumenEstanteGalia {
    private val P = PaletaGalia

    /** El tinte de lo que no tiene ánimo: lana parda sin teñir, mimbre viejo, cuero sin teñir. */
    private val PARDO_LANA = Color(0xFF6E5E4A)
    private val PARDO_MIMBRE = Color(0xFF8E7448)
    private val PARDO_CUERO = Color(0xFF6A4A30)

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.42f, 0.8f, 0.46f, 0.1f, -0.22f, -0.52f, -0.8f)

    /** Los meses cortos en español, para la etiqueta (el nombre de Coligny ya lo dice el rótulo de la repisa). */
    private val MESES_CORTOS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

    /** El mes corto de una entrada: se ubica el mes de Coligny entre los del tema (Samonios es noviembre); si no está, queda tal cual. */
    private fun mesCorto(mes: String): String {
        val nombre = mes.substringBefore(' ').trim()
        val i = Galia.MESES.indexOfFirst { it.nombre == nombre }
        return if (i >= 0) MESES_CORTOS[Math.floorMod(i + 10, 12)] else mes
    }

    /** La luz que le toca a una entrada: la del momento ([k]) y, si le alcanza, la de la puerta ([sol]). */
    private class Luz(val a: AireGalia, val k: Float, val sol: Float) {
        fun c(col: Color, f: Float = 0f): Color {
            val base = a.dia(col, f, k)
            return if (sol > 0f) a.sol(col, base, sol * 0.75f) else base
        }
        fun animo(t: Color): Color {
            val base = a.animo(t, k)
            return if (sol > 0f) a.sol(t, base, sol * 0.75f) else base
        }
        fun etiqueta(col: Color): Color {
            val base = a.etiqueta(col, k)
            return if (sol > 0f) a.sol(col, base, sol * 0.6f) else base
        }
    }

    /** Los colores de un material con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(t: Color?, pardo: Color, l: Luz): Tinte {
        val m = t ?: pardo
        val claro = mezcla(m, luzMueble(1f), if (t == null) 0.2f else 0.3f)
        val hondo = mezcla(m, sombraMueble(1f), 0.55f)
        return if (t != null) Tinte(l.animo(m), l.animo(claro), l.animo(hondo)) else Tinte(l.c(m), l.c(claro), l.c(hondo))
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

    /**
     * El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp sin achicarse) y el mes debajo, en una
     * de 9.5 dp; así el día sale del mismo tamaño en las tres entradas.
     */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left - 2f * u, y0, r.right + 2f * u, y0 + hd), P.TITULO, tinta, DIA, peso = FontWeight.Bold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left - 1f * u, y0 + hd - 1.4f * u, r.right + 1f * u, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.85f), MES, peso = FontWeight.SemiBold, minimo = 5f)
    }

    /** La tinta del estilo en la cera: de día, la del estilo; de noche, casi negra (la cera ya va teñida por la luz). */
    /**
     * La tinta del día: de día, la del estilo, oscura sobre la cera de miel; de noche, la raya del estilo deja ver la haya clara
     * bajo la cera hundida, y esa raya coge la luz que le llega (ámbar cerca, apagada lejos, pero siempre legible).
     */
    private fun tinta(a: AireGalia, k: Float) = if (a.oscuro) mezcla(Color(0xFFB88C56), Color(0xFFFFE6B8), k.coerceIn(0f, 1f)) else a.luz(P.TINTA, maxOf(k, 0.75f))

    /** La cera: de miel de día; de noche, hundida (más que el marco), para que la raya clara se lea. */
    private fun ceraDe(l: Luz) = if (l.a.oscuro) mezcla(l.etiqueta(P.CERA_OSC), l.a.hondo, 0.4f - 0.25f * l.k) else l.etiqueta(P.CERA)

    /**
     * Una tablilla de haya de esquinas cortadas en [r], con el marco moteado y la cera hundida, donde va el día rayado con el
     * estilo. [giro] la ladea; [agujero] le pone el agujero de la correa arriba.
     */
    private fun DrawScope.tablilla(v: Volumen, medidor: TextMeasurer, r: Rect, l: Luz, giro: Float = 0f, agujero: Boolean = true) {
        val u = density
        val a = l.a
        rotate(giro, r.center) {
            val corte = minOf(r.width, r.height) * 0.14f
            val forma = poligonoGalia(r.left + corte, r.top, r.right - corte, r.top, r.right, r.top + corte, r.right, r.bottom - corte,
                r.right - corte, r.bottom, r.left + corte, r.bottom, r.left, r.bottom - corte, r.left, r.top + corte)
            drawPath(Path().apply { addPath(forma, Offset(0.9f * u, 1.2f * u)) }, a.sombra(0.4f))
            drawPath(forma, l.etiqueta(P.HAYA))
            val marco = 1.7f * u
            val cera = Rect(r.left + marco, r.top + marco + (if (agujero) 1.2f * u else 0f), r.right - marco, r.bottom - marco)
            drawRect(ceraDe(l), cera.topLeft, cera.size)
            drawRect(l.etiqueta(P.CERA_OSC).copy(alpha = 0.7f), cera.topLeft, Size(cera.width, 1.2f * u))
            drawRect(l.etiqueta(P.CERA_OSC).copy(alpha = 0.5f), cera.topLeft, Size(1f * u, cera.height))
            drawRect(l.etiqueta(P.HAYA_LUZ).copy(alpha = 0.7f), Offset(r.left + corte, r.top), Size(r.width - 2 * corte, 0.8f * u))
            // Las motas de la haya en el marco de abajo.
            drawLine(l.etiqueta(P.HAYA_MOTA).copy(alpha = 0.6f), Offset(r.left + r.width * 0.2f, r.bottom - marco * 0.5f), Offset(r.left + r.width * 0.32f, r.bottom - marco * 0.5f), 0.7f * u)
            drawLine(l.etiqueta(P.HAYA_MOTA).copy(alpha = 0.6f), Offset(r.left + r.width * 0.6f, r.bottom - marco * 0.5f), Offset(r.left + r.width * 0.7f, r.bottom - marco * 0.5f), 0.7f * u)
            if (agujero) drawCircle(a.luz(P.ROBLE_HONDO, l.k), 0.9f * u, Offset(r.center.x, r.top + marco * 0.75f))
            diaYMes(v, medidor, cera.deflate(0.6f * u), tinta(a, l.k))
        }
    }

    /** La media luna de los Sueños, de radio [r], armada una vez por tamaño. */
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

    /** La luna de los Sueños: el disco apagado de la luna y la media luna clara encima, con su canto oscuro y su sombra. */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireGalia) {
        val u = density
        val m = creciente(r)
        drawCircle(Color(0xFF3A4652).copy(alpha = 0.6f), r, c)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, a.sombra(0.5f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFE2EAF0))
            drawPath(m, Color(0xFF2C3440), style = Stroke(0.8f * u))
        }
        drawCircle(Color.White.copy(alpha = 0.85f), r * 0.14f, Offset(c.x - r * 0.6f, c.y - r * 0.16f))
    }

    /** Lo de todas: la sombra que dejan en el fondo por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireGalia, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.45f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.45f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche) y el roce frío de la luna. */
    private fun DrawScope.filos(cuerpo: Rect, a: AireGalia, k: Float, hacia: Float, sol: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.7f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (sol > 0f && a.oscuro) drawRect(a.lunar(0.6f * sol), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    // ---------------------------------------------------------------- la funda de lana a cuadros

    fun DrawScope.funda(v: Volumen, medidor: TextMeasurer, a: AireGalia, k: Float, hacia: Float, t: Color?, sol: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, sol)
        val tn = tinte(t, PARDO_LANA, l)
        // Las tablillas que asoman por la boca: la de delante, con el día, y una o dos detrás, más con más palabras.
        val boca = 33f * u + h * (0.03f + 0.04f * azar(s, 3))
        alrededor(a, hacia, boca - 6f * u)
        val detras = 1 + (v.lleno * 2f).toInt().coerceIn(0, 1)
        for (j in 0 until detras) {
            val g = if (j == 0) (if (azar(s, 4) > 0.5f) 7f else -7f) else -4f
            val tw = w * 0.42f
            val x0 = if (j == 0) w * 0.08f else w * 0.5f
            rotate(g, Offset(x0 + tw / 2, boca)) {
                val tr = Rect(x0, boca - 30f * u - h * 0.04f * (1 - j), x0 + tw, boca + 4f * u)
                drawRect(a.sombra(0.35f), Offset(tr.left + 1f * u, tr.top + 1f * u), tr.size)
                drawRect(l.etiqueta(mezcla(P.HAYA, P.HAYA_OSC, 0.3f)), tr.topLeft, tr.size)
                drawRect(l.etiqueta(P.CERA_OSC), Offset(tr.left + 1.4f * u, tr.top + 1.4f * u), Size(tr.width - 2.8f * u, tr.height))
                drawRect(a.brillo(0.3f, k), tr.topLeft, Size(tr.width, 0.8f * u))
            }
        }
        // La de delante: la tablilla del día, un poco ladeada.
        val et = Rect(1.6f * u, boca - 33f * u, w - 1.6f * u, boca + 3f * u)
        tablilla(v, medidor, et, l, (azar(s, 11) - 0.5f) * 4f)
        // La funda: algo más ancha abajo, con la panza blanda y el fondo redondeado.
        val vuelta = 7f * u + h * 0.05f * azar(s, 5)
        val cuerpo = Path().apply {
            moveTo(0.6f * u, boca)
            lineTo(w - 0.6f * u, boca)
            cubicTo(w + 0.4f * u, h * 0.5f, w + 0.6f * u, h * 0.8f, w - 1.2f * u, h - 1f * u)
            quadraticTo(w * 0.5f, h + 1.2f * u, 1.2f * u, h - 1f * u)
            cubicTo(-0.6f * u, h * 0.8f, -0.4f * u, h * 0.5f, 0.6f * u, boca)
            close()
        }
        clipPath(cuerpo) {
            redondo(Rect(0f, boca, w, h + 2f * u), tn, hacia)
            // Los cuadros: franjas anchas del mismo tinte, más hondo, que se cruzan, y un hilo fino de otro color.
            val hilo = listOf(P.LANA_ROJA, P.LANA_OCRE, P.GLASTO, P.LANA_CRUDA, P.GLASTO)[Math.floorMod(s shr 3, 5)]
            val paso = (7f + 3f * azar(s, 6)) * u
            val anchas = Path(); val finos = Path()
            var x = paso * (0.2f + 0.6f * azar(s, 7))
            while (x < w) { anchas.addRect(Rect(x, boca, x + paso * 0.36f, h)); finos.moveTo(x + paso * 0.68f, boca); finos.lineTo(x + paso * 0.68f, h); x += paso }
            var y = boca + paso * 0.4f
            while (y < h) { anchas.addRect(Rect(0f, y, w, y + paso * 0.36f)); finos.moveTo(0f, y + paso * 0.68f); finos.lineTo(w, y + paso * 0.68f); y += paso }
            drawPath(anchas, tn.hondo.copy(alpha = 0.5f))
            drawPath(finos, l.c(hilo, 0f).copy(alpha = 0.85f), style = Stroke(0.8f * u))
            // Los pliegues blandos que bajan del cordón, y la sombra del fondo.
            for (j in 0 until 2) {
                val xp = w * (0.3f + 0.4f * j + 0.08f * (azar(s, 30 + j) - 0.5f))
                drawRect(a.sombra(0.22f), Offset(xp, boca + vuelta), Size(1.6f * u, h * 0.5f))
                drawRect(tn.claro.copy(alpha = 0.4f), Offset(xp - 1.4f * u, boca + vuelta), Size(1f * u, h * 0.45f))
            }
            drawRect(a.sombra(0.3f), Offset(0f, h - h * 0.08f), Size(w, h * 0.08f))
        }
        // La boca vuelta: el doblez de lana, con su canto claro arriba y el revés más hondo.
        val cuff = Path().apply {
            moveTo(-0.4f * u, boca - 1f * u); lineTo(w + 0.4f * u, boca - 1f * u)
            lineTo(w + 0.6f * u, boca + vuelta); quadraticTo(w * 0.5f, boca + vuelta + 1.6f * u, -0.6f * u, boca + vuelta); close()
        }
        drawPath(cuff, mezcla(tn.cara, tn.hondo, 0.25f))
        clipPath(cuff) {
            val pasoV = 3f * u
            var xv = 0f
            while (xv < w) { drawLine(tn.hondo.copy(alpha = 0.5f), Offset(xv, boca), Offset(xv + 1f * u, boca + vuelta), 0.6f * u); xv += pasoV }
        }
        drawRect(tn.claro, Offset(-0.4f * u, boca - 1f * u), Size(w + 0.8f * u, 1.3f * u))
        drawRect(a.sombra(0.4f), Offset(0f, boca + vuelta), Size(w, 1.4f * u))
        // El cordón de lana torcida, dos vueltas bajo el doblez, con su nudo y los dos cabos.
        val yc = boca + vuelta + 3.4f * u
        val cord = l.c(listOf(P.CUERO, P.LANA_CRUDA, P.LANA_ROJA)[Math.floorMod(s shr 5, 3)], 0f)
        drawRect(cord, Offset(-0.4f * u, yc), Size(w + 0.8f * u, 1.8f * u))
        drawRect(cord, Offset(-0.4f * u, yc + 2.6f * u), Size(w + 0.8f * u, 1.6f * u))
        drawRect(a.brillo(0.3f, k), Offset(0f, yc), Size(w, 0.6f * u))
        val nx = w * (if (azar(s, 8) > 0.5f) 0.28f else 0.7f)
        drawCircle(cord, 2.2f * u, Offset(nx, yc + 1.8f * u))
        drawLine(cord, Offset(nx, yc + 2f * u), Offset(nx - 2f * u, yc + 11f * u + h * 0.04f * azar(s, 9)), 1.2f * u, StrokeCap.Round)
        drawLine(cord, Offset(nx, yc + 2f * u), Offset(nx + 2.4f * u, yc + 9f * u), 1.2f * u, StrokeCap.Round)
        // La luna de los Sueños, en la panza.
        if (v.noche) luna(Offset(w * 0.5f, h * 0.72f), minOf(w * 0.2f, 5f * u), a)
        filos(Rect(0f, boca, w, h), a, k, hacia, sol)
    }

    // ---------------------------------------------------------------- el cofre de mimbre

    fun DrawScope.cofre(v: Volumen, medidor: TextMeasurer, a: AireGalia, k: Float, hacia: Float, t: Color?, sol: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, sol)
        val tn = tinte(t, PARDO_MIMBRE, l)
        val lado = w * 0.12f
        val tapa = h * (0.17f + 0.04f * azar(s, 3))
        val tope = 2.2f * u
        alrededor(a, hacia, tope)
        // El canto de la derecha, en escorzo y en sombra (el cofre tiene fondo).
        val frente = Rect(0f, tope, w - lado, h)
        drawPath(poligonoGalia(w - lado, tope + lado * 0.4f, w, tope, w, h - lado * 0.4f, w - lado, h), tn.hondo)
        // El frente tejido: las estacas de pie y las hileras de mimbre que entran y salen.
        val cuerpo = Rect(frente.left, tope + tapa, frente.right, h)
        drawRect(tn.cara, cuerpo.topLeft, cuerpo.size)
        clipPath(Path().apply { addRect(cuerpo) }) {
            val fila = 3f * u; val estaca = 4.4f * u
            val claras = Path(); val hondas = Path(); val luces = Path()
            var y = cuerpo.top + 1f * u; var j = 0
            while (y < cuerpo.bottom) {
                var x = cuerpo.left + (if (j % 2 == 0) 0f else estaca / 2)
                while (x < cuerpo.right) {
                    claras.addOval(Rect(x - estaca * 0.46f, y - fila * 0.45f, x + estaca * 0.46f, y + fila * 0.45f))
                    luces.moveTo(x - estaca * 0.3f, y - fila * 0.22f); luces.lineTo(x + estaca * 0.1f, y - fila * 0.3f)
                    hondas.addRect(Rect(x + estaca * 0.46f, y - fila * 0.5f, x + estaca * 0.56f, y + fila * 0.5f))
                    x += estaca
                }
                y += fila; j++
            }
            drawPath(claras, tn.claro.copy(alpha = 0.55f))
            drawPath(hondas, tn.hondo.copy(alpha = 0.75f))
            drawPath(luces, a.brillo(0.3f, k), style = Stroke(0.6f * u))
            // Una franja de mimbre sin teñir a media altura, en unos sí y en otros no.
            if (azar(s, 4) > 0.45f) {
                val yf = cuerpo.top + cuerpo.height * (0.68f + 0.1f * azar(s, 5))
                drawRect(l.c(P.MIMBRE, 0f).copy(alpha = 0.85f), Offset(cuerpo.left, yf), Size(cuerpo.width, fila * 2f))
                drawRect(tn.hondo.copy(alpha = 0.4f), Offset(cuerpo.left, yf + fila * 2f), Size(cuerpo.width, 0.8f * u))
            }
            // La luz de un lado y la sombra del otro, y el fondo más hondo.
            drawRect(a.brillo(0.16f, k), Offset(if (hacia < 0f) cuerpo.left else cuerpo.right - cuerpo.width * 0.25f, cuerpo.top), Size(cuerpo.width * 0.25f, cuerpo.height))
            drawRect(a.sombra(0.22f), Offset(if (hacia < 0f) cuerpo.right - cuerpo.width * 0.3f else cuerpo.left, cuerpo.top), Size(cuerpo.width * 0.3f, cuerpo.height))
            drawRect(a.sombra(0.3f), Offset(cuerpo.left, cuerpo.bottom - 4f * u), Size(cuerpo.width, 4f * u))
        }
        // Las esquinas: dos estacas gruesas, y el canto de la derecha con su tejido en sombra.
        drawRect(tn.hondo, Offset(cuerpo.right - 1.6f * u, cuerpo.top), Size(1.6f * u, cuerpo.height))
        drawRect(tn.claro, Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
        // El pie: un trenzado más grueso.
        drawRect(tn.hondo, Offset(0f, h - 3.2f * u), Size(w - lado, 3.2f * u))
        drawRect(tn.claro.copy(alpha = 0.6f), Offset(0f, h - 3.2f * u), Size(w - lado, 0.8f * u))
        // La tapa: un poco más ancha, con su canto de arriba en escorzo y el borde trenzado.
        val tp = Rect(-0.8f * u, tope, w - lado + 0.8f * u, tope + tapa)
        drawRect(a.sombra(0.4f), Offset(0f, tp.bottom), Size(w - lado, 2.4f * u))
        drawRect(mezcla(tn.cara, tn.hondo, 0.15f), tp.topLeft, tp.size)
        drawPath(poligonoGalia(tp.left, tp.top, tp.left + lado, tp.top - lado * 0.4f, w + 0.8f * u, tp.top - lado * 0.4f, tp.right, tp.top), tn.claro)
        drawPath(poligonoGalia(tp.right, tp.top, w + 0.8f * u, tp.top - lado * 0.4f, w + 0.8f * u, tp.bottom - lado * 0.4f, tp.right, tp.bottom), mezcla(tn.hondo, tn.cara, 0.2f))
        clipPath(Path().apply { addRect(tp) }) {
            // El tejido de la tapa, más apretado, y su trenza de abajo.
            val trenza = Path()
            var x = tp.left
            while (x < tp.right) { trenza.moveTo(x, tp.bottom - 1f * u); trenza.lineTo(x + 2.4f * u, tp.bottom - 4.4f * u); x += 2.6f * u }
            var yy = tp.top + 2f * u
            while (yy < tp.bottom - 5f * u) { drawLine(tn.hondo.copy(alpha = 0.45f), Offset(tp.left, yy), Offset(tp.right, yy), 0.7f * u); yy += 2.6f * u }
            drawPath(trenza, tn.claro.copy(alpha = 0.8f), style = Stroke(1.2f * u))
            drawRect(a.brillo(0.3f, k), Offset(tp.left, tp.top), Size(tp.width, 0.9f * u))
        }
        // Las bisagras de correa, en el canto de la izquierda.
        val cuero = l.c(P.CUERO, 0f)
        listOf(0.5f).forEach { f ->
            val y = tope + tapa * f + tapa * 0.5f
            drawRoundRect(cuero, Offset(-1.6f * u, y - 2f * u), Size(4f * u, 4.6f * u), CornerRadius(1.4f * u))
            drawRect(a.brillo(0.25f, k), Offset(-1.6f * u, y - 2f * u), Size(4f * u, 0.7f * u))
        }
        // El cierre: el lazo de correa que baja de la tapa y la clavija de roble que lo pasa, de la que cuelga la tablilla del día.
        val cx = (w - lado) * 0.5f
        val clav = Offset(cx, tope + tapa + 3.4f * u)
        drawRoundRect(cuero, Offset(cx - 2.2f * u, tope + tapa - 4f * u), Size(4.4f * u, 9f * u), CornerRadius(2f * u))
        drawLine(a.sombra(0.4f), clav + Offset(-4.4f * u, 1.4f * u), clav + Offset(4.8f * u, 1.4f * u), 2.4f * u, StrokeCap.Round)
        drawLine(l.c(P.ROBLE_LUZ, 0f), clav - Offset(4.6f * u, 0f), clav + Offset(4.6f * u, 0f), 2.4f * u, StrokeCap.Round)
        val giro = (azar(s, 11) - 0.5f) * 8f
        val et = Rect(1.2f * u, clav.y + 5f * u + h * 0.04f * azar(s, 12), w - lado - 1.2f * u, clav.y + 5f * u + h * 0.04f * azar(s, 12) + 32f * u)
        drawLine(l.c(P.CUERO_OSC, 0f), clav, Offset(et.center.x, et.top + 2f * u), 1f * u)
        tablilla(v, medidor, et, l, giro)
        // La luna de los Sueños, en la tapa.
        if (v.noche) luna(Offset((w - lado) * 0.5f, h * 0.86f - 2f * u), minOf(w * 0.18f, 4.8f * u), a)
        filos(Rect(0f, tope, w - lado, h), a, k, hacia, sol)
    }

    // ---------------------------------------------------------------- el zurrón de cuero

    fun DrawScope.zurron(v: Volumen, medidor: TextMeasurer, a: AireGalia, k: Float, hacia: Float, t: Color?, sol: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, sol)
        val tn = tinte(t, PARDO_CUERO, l)
        val caida = if (azar(s, 5) > 0.5f) 1f else -1f
        val boca = h * (0.05f + 0.04f * azar(s, 3))
        alrededor(a, hacia, boca)
        // Blanda, se ladea contra la vecina del lado hacia donde se cae.
        rotate(caida * 3.5f, Offset(w * 0.5f, h)) {
            // Los tientos de las esquinas, donde se ata la correa (se guarda sin ella).
            val correa = l.c(if ((s shr 2) and 1 == 0) P.CUERO_OSC else P.CUERO, 0f)
            listOf(w * 0.14f, w * 0.86f).forEach { x ->
                val p = Offset(x + (if (x < w / 2) -1.6f else 1.6f) * u, boca - 4.4f * u)
                drawLine(correa, Offset(x, boca + 2f * u), p, 1.8f * u, StrokeCap.Round)
                drawCircle(correa, 1.6f * u, p)
            }
            // La bolsa: llena de tablillas, ancha y blanda, con el fondo que se aplasta en la tabla y se cae hacia un lado.
            val ld = if (caida > 0f) 1.6f * u else -0.6f * u
            val li = if (caida < 0f) -1.6f * u else 0.6f * u
            val bolsa = Path().apply {
                moveTo(w * 0.1f, boca + 1.2f * u)
                quadraticTo(w * 0.3f, boca - 1.6f * u, w * 0.5f, boca + 0.8f * u)
                quadraticTo(w * 0.7f, boca + 2.2f * u, w * 0.9f, boca - 0.4f * u)
                cubicTo(w * 0.94f, h * 0.36f, w + ld, h * 0.62f, w - 0.2f * u + ld * 0.6f, h * 0.88f)
                cubicTo(w - 1.2f * u, h * 0.99f, w * 0.72f, h + 0.4f * u, w * 0.5f, h)
                cubicTo(w * 0.28f, h + 0.4f * u, 1.2f * u, h * 0.99f, 0.2f * u + li * 0.6f, h * 0.88f)
                cubicTo(li, h * 0.62f, w * 0.06f, h * 0.36f, w * 0.1f, boca + 1.2f * u)
                close()
            }
            clipPath(bolsa) {
                redondo(Rect(0f, boca, w, h), tn, hacia)
                // Los fuelles de los lados, que dan el grueso de la bolsa: una franja más honda con su costura.
                drawRect(tn.hondo.copy(alpha = 0.55f), Offset(if (hacia < 0f) w * 0.86f else 0f, boca), Size(w * 0.14f, h))
                drawLine(tn.hondo, Offset(if (hacia < 0f) w * 0.86f else w * 0.14f, h * 0.5f), Offset(if (hacia < 0f) w * 0.84f else w * 0.16f, h), 1f * u)
                // El lustre del cuero en la panza: una veta clara curva del lado de la luz y un brillo.
                val lx = if (hacia < 0f) w * 0.22f else w * 0.68f
                drawPath(Path().apply { moveTo(lx + w * 0.05f, h * 0.62f); quadraticTo(lx - w * 0.04f, h * 0.76f, lx + w * 0.03f, h * 0.92f) }, a.brillo(0.4f, k), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                drawCircle(a.brillo(0.65f, k), 1.2f * u, Offset(lx + w * 0.04f, h * 0.66f))
                // Las arrugas del cuero blando, que salen de la punta de la solapa hacia los lados, y la de abajo.
                // (De los lados, nunca en medio: en medio, con la punta de la solapa, harían una cara.)
                val arrugas = Path().apply {
                    moveTo(w * 0.04f, h * (0.74f + 0.04f * azar(s, 32))); quadraticTo(w * 0.14f, h * 0.84f, w * 0.26f, h * 0.97f)
                    moveTo(w * 0.96f, h * (0.72f + 0.05f * azar(s, 33))); quadraticTo(w * 0.86f, h * 0.83f, w * 0.76f, h * 0.97f)
                    val xr = if (caida > 0f) w * 0.7f else w * 0.3f
                    moveTo(xr, h * 0.7f); quadraticTo(xr + caida * w * 0.04f, h * 0.8f, xr - caida * w * 0.02f, h * 0.9f)
                }
                drawPath(arrugas, a.sombra(0.3f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { addPath(arrugas, Offset(0f, -1.5f * u)) }, tn.claro.copy(alpha = 0.5f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
                drawOval(a.sombra(0.32f), Offset(w * 0.04f, h * 0.93f), Size(w * 0.92f, h * 0.1f))
            }
            // El canto del cuero, más hondo.
            drawPath(bolsa, tn.hondo, style = Stroke(1.2f * u))
            // La solapa: cubre más de media bolsa, con la punta redonda, más honda que la bolsa y cosida en su orilla.
            val bajo = h * (0.53f + 0.05f * azar(s, 6))
            val punta = w * (0.5f + 0.1f * caida)
            val solapa = Path().apply {
                moveTo(w * 0.03f, boca - 1f * u)
                quadraticTo(w * 0.5f, boca + 1.4f * u, w * 0.97f, boca - 1f * u)
                lineTo(w * 0.97f, bajo - w * 0.42f)
                quadraticTo(w * 0.95f, bajo, punta, bajo)
                quadraticTo(w * 0.05f, bajo, w * 0.03f, bajo - w * 0.42f)
                close()
            }
            drawPath(Path().apply { addPath(solapa, Offset(0.6f * u, 2f * u)) }, a.sombra(0.42f))
            clipPath(solapa) {
                redondo(Rect(0f, boca - 1f * u, w, bajo), Tinte(mezcla(tn.cara, tn.hondo, 0.22f), tn.claro, tn.hondo), hacia)
                drawRect(tn.claro.copy(alpha = 0.55f), Offset(0f, boca - 1f * u), Size(w, 1.4f * u))
            }
            // Las puntadas de la orilla de la solapa, que siguen su borde por dentro.
            val punt = Path()
            val m = PathMeasure().apply { setPath(solapa, false) }
            val largo = m.length
            var d = 0f
            while (d < largo) {
                val p = m.getPosition(d)
                if (p.y > boca + 3f * u) {
                    val hacia0 = Offset(w * 0.5f - p.x, h * 0.35f - p.y)
                    val n = hacia0.getDistance().coerceAtLeast(1f)
                    val q = p + hacia0 * (2.2f * u / n)
                    punt.moveTo(q.x, q.y); punt.lineTo(q.x + 1.1f * u, q.y + 0.5f * u)
                }
                d += 3.2f * u
            }
            drawPath(punt, l.c(P.LANA_CRUDA, -0.05f).copy(alpha = 0.8f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            // La tablilla del día, metida bajo un tiento que cruza la solapa por dos ojales.
            val et = Rect(1.8f * u, boca + 3.6f * u, w - 1.8f * u, boca + 3.6f * u + 31f * u)
            val et2 = Rect(et.left, et.top, et.right, minOf(et.bottom, bajo - 6f * u))
            tablilla(v, medidor, et2, l, (azar(s, 12) - 0.5f) * 4f, agujero = false)
            val yt = et2.bottom - 1.6f * u
            drawRect(a.sombra(0.4f), Offset(0f, yt + 1f * u), Size(w, 2.2f * u))
            drawRect(correa, Offset(w * 0.02f, yt - 1.1f * u), Size(w * 0.96f, 2.2f * u))
            drawRect(a.brillo(0.3f, k), Offset(w * 0.02f, yt - 1.1f * u), Size(w * 0.96f, 0.6f * u))
            listOf(w * 0.06f, w * 0.94f).forEach { x -> drawCircle(tn.hondo, 1.4f * u, Offset(x, yt)) }
            // La fíbula de bronce que cierra la punta de la solapa: el muelle enrollado, el puente arqueado y el pie vuelto.
            val fb = Offset(punta, bajo - 1.4f * u)
            val bronce = l.c(P.BRONCE, 0f)
            val fw = minOf(w * 0.42f, 11f * u)
            drawLine(a.sombra(0.45f), fb + Offset(-fw * 0.5f, 1.6f * u), fb + Offset(fw * 0.55f, 1.6f * u), 1.6f * u)
            drawLine(l.c(P.BRONCE_OSC, 0f), fb + Offset(-fw * 0.5f, 0.6f * u), fb + Offset(fw * 0.5f, 0.6f * u), 0.8f * u)
            drawPath(Path().apply { moveTo(fb.x - fw * 0.38f, fb.y); quadraticTo(fb.x, fb.y - fw * 0.6f, fb.x + fw * 0.42f, fb.y) }, bronce, style = Stroke(1.8f * u, cap = StrokeCap.Round))
            drawCircle(bronce, 1.8f * u, fb + Offset(-fw * 0.48f, 0f), style = Stroke(1.1f * u))
            drawCircle(bronce, 1.3f * u, fb + Offset(fw * 0.5f, -0.4f * u))
            drawPath(Path().apply { moveTo(fb.x - fw * 0.26f, fb.y - fw * 0.2f); quadraticTo(fb.x - fw * 0.04f, fb.y - fw * 0.38f, fb.x + fw * 0.1f, fb.y - fw * 0.28f) }, a.brillo(0.7f, k), style = Stroke(0.7f * u))
            // La luna de los Sueños: en la panza, bajo la solapa.
            if (v.noche) luna(Offset(w * 0.5f, (bajo + h) / 2f + 2f * u), minOf(w * 0.19f, 4.8f * u), a)
            filos(Rect(0f, boca, w, h), a, k, hacia, sol)
        }
    }
}
