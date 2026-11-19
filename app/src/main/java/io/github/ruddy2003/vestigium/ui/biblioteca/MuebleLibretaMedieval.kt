package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Dónde va cada cosa del rincón de la libreta de Scriptorium, en píxeles. El códice no se deforma con el ancho: mide lo mismo
 * en cualquier teléfono (más angosto solo si no cabe) y engorda con las hojas ([grueso]); lo de alrededor se reparte.
 */
internal class SitioLibretaMedieval(val w: Float, val h: Float, val u: Float, hojas: Int) {
    /** Donde empieza el suelo (el pie del muro). */
    val suelo = h * 0.86f
    /** El pilar con su media columna, a la izquierda. */
    val pilar = Rect(0f, 0f, (w * 0.12f).coerceIn(38f * u, 52f * u), suelo)
    /** La percha de roble clavada al muro, de lado a lado. */
    val percha = h * 0.17f
    /** Lo grueso del códice: de 4 dp con un folio a 18 con 240 o más. */
    val grueso = (4f + 14f * sqrt(hojas.coerceIn(0, 240) / 240f)) * u
    /** El códice: la tapa (su cara), sobre el listón del facistol. */
    private val ancho = minOf(140f * u, w * 0.46f)
    val liston = h * 0.705f
    val codice = Rect(w * 0.53f - ancho / 2 - grueso * 0.5f, liston - ancho * 1.13f, w * 0.53f + ancho / 2 - grueso * 0.5f, liston)
    /** El tablero inclinado del facistol, detrás del códice. */
    val tablero = Rect(codice.left - 11f * u, codice.top - grueso * 0.5f - 9f * u, codice.right + grueso + 12f * u, liston + 3f * u)
    /** El eje del pie torneado. */
    val eje = codice.center.x + grueso * 0.5f
    /** El candelero de pie, a la derecha: dónde pisa y dónde arde la vela. */
    val candelero = Offset(w - (w * 0.1f).coerceIn(34f * u, 46f * u), h * 0.975f)
    val llama = Offset(candelero.x, h * 0.085f)
}

/**
 * La libreta de momentos de Scriptorium (la usa `EstanteMedieval`): el «Códice de momentos» sobre el facistol, en el rincón del
 * scriptorium junto al pilar de la bóveda. Es otro rincón de la misma casa que el armario de la biblioteca y el del Ideario:
 * el mismo muro de cal sobre sillarejo, el mismo barro en el suelo, la misma ventana (fuera del cuadro, arriba a la izquierda)
 * y la misma noche.
 *
 * El facistol es de roble: el tablero inclinado con su listón, el faldón y el pie torneado sobre tres patas. El códice va en
 * tablas de roble desnudas con media piel en el lomo, clavada, y su correa con broche; en la tabla, la etiqueta de pergamino
 * con el nombre y la cuenta. Engorda con las hojas: el canto y la cabeza, cada vez más gruesos, más cédulas que asoman por
 * arriba y más registros que cuelgan del listón. Alrededor, con uso: la percha de roble con la bolsa de cuero de llevar el
 * libro y las velas de repuesto colgadas por la mecha; el candelero de hierro de pie con su vela gruesa; en el listón, el plomo
 * de pautar; al pie, los cuadernillos todavía sin coser, atados, y los juncos del suelo.
 *
 * La luz: de día, la de la ventana, que dibuja su arco en el muro y echa a la derecha las sombras del pilar, del facistol y de
 * lo que cuelga. De noche (modo oscuro) arde la vela del candelero: lo cercano se enciende (el códice, su etiqueta, la percha)
 * y lo lejano se pierde en la noche del tema, y por la ventana entra la luna, fría, en el muro de la izquierda.
 */
internal object MuebleLibretaMedieval {
    private val P = PaletaMedieval

    /** Cuánta luz de la vela le llega a [p] (1 junto a la llama, 0 lejos); de día, toda. */
    fun cerca(p: Offset, s: SitioLibretaMedieval, a: AireMedieval): Float {
        if (!a.lumbre) return 1f
        val dx = (p.x - s.llama.x) / s.h; val dy = (p.y - s.llama.y) / s.h
        return (1f - sqrt(dx * dx * 0.8f + dy * dy) / 1.45f).coerceIn(0f, 1f).pow(1.25f)
    }

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireMedieval(oscuro, false)
        val s = SitioLibretaMedieval(w, h, u, e.hojas)
        clipRect(0f, 0f, w, h) {
            muro(s, a)
            suelo(s, a, e.semilla)
            ventana(s, a)
            if (a.lumbre) mancha(s.llama, h * 0.95f, P.LLAMA.copy(alpha = 0.16f))
            with(PiezasLibretaMedieval) { percha(s, a) }
            pilar(s, a)
            with(FacistolLibretaMedieval) { facistol(s, a, e, medidor) }
            with(PiezasLibretaMedieval) { alPie(s, a, e.semilla); candelero(s, a) }
            granoMueble(Rect(0f, 0f, w, h), oscuro)
        }
    }

    /**
     * Pinta [r] con [color] con la luz del momento: de día, liso; de noche, hundido y con la luz de la vela encima, que se apaga
     * con la distancia (un solo degradado).
     */
    fun DrawScope.alumbrado(r: Rect, color: Color, s: SitioLibretaMedieval, a: AireMedieval) {
        drawRect(a.luz(color, if (a.lumbre) 0.02f else 1f), r.topLeft, r.size)
        if (!a.lumbre) return
        drawRect(Brush.radialGradient(0f to a.luz(color, 0.95f), 0.25f to a.luz(color, 0.62f).copy(alpha = 0.9f), 0.55f to a.luz(color, 0.3f).copy(alpha = 0.5f),
            1f to Color.Transparent, center = s.llama, radius = s.h * 1.45f), r.topLeft, r.size)
    }

    // ---------------------------------------------------------------- el muro, el suelo y la ventana

    /**
     * El muro: cal vieja sobre el sillarejo (las hiladas se adivinan debajo), dos faltas donde asoman las piedras, la humedad
     * que sube del suelo y, de noche, el hollín de la vela sobre el candelero.
     */
    private fun DrawScope.muro(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val r = Rect(0f, 0f, s.w, s.suelo)
        alumbrado(r, P.CAL, s, a)
        pinceladas(r, a.luz(P.PIEDRA_OSC, 0.4f), 3101, 26, u, 0.07f)
        pinceladas(r, a.claro(1f), 3102, 14, u, 0.08f)
        // Las hiladas bajo la cal: un relieve leve, la llaga en sombra y su filo de luz.
        val sombra = a.frio(0.12f); val luz = a.claro(0.3f)
        val llagas = Path(); val filos = Path()
        var y = s.h * 0.06f; var f = 0
        while (y < s.suelo - 4f * u) {
            var x = -s.w * 0.08f * azar(3110, f); var j = 0
            while (x < s.w) {
                val largo = s.w * (0.08f + 0.1f * azar(3111 + f, j)) + 8f * u
                if (azar(3112 + f, j) > 0.3f) {
                    val dy = 2f * u * (azar(3113 + f, j) - 0.5f)
                    llagas.moveTo(x, y + dy); llagas.lineTo(x + largo * 0.86f, y + dy + 1f * u * (azar(3114 + f, j) - 0.5f))
                    filos.moveTo(x, y + dy + 1.2f * u); filos.lineTo(x + largo * 0.86f, y + dy + 1.2f * u)
                }
                if (azar(3115 + f, j) > 0.5f) { llagas.moveTo(x + largo, y); llagas.lineTo(x + largo + 1f * u, y + s.h * 0.08f * azar(3116 + f, j)) }
                x += largo; j++
            }
            y += s.h * (0.085f + 0.03f * azar(3117, f)); f++
        }
        drawPath(llagas, sombra, style = Stroke(1f * u, cap = StrokeCap.Round))
        drawPath(filos, luz, style = Stroke(0.7f * u, cap = StrokeCap.Round))
        falta(Offset((s.pilar.right + s.tablero.left) / 2, s.h * 0.7f), 15f * u, 10f * u, 3120, s, a)
        falta(Offset(s.w * 0.93f, s.h * 0.55f), 13f * u, 15f * u, 3140, s, a)
        // La humedad y el roce que suben del suelo.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.frio(0.22f)), s.suelo - s.h * 0.22f, s.suelo), Offset(0f, s.suelo - s.h * 0.22f), Size(s.w, s.h * 0.22f))
        // El hollín de la vela, en el muro encima del candelero.
        val tizne = mezcla(P.NOGAL_OSC, P.NEGRO, 0.5f)
        for (i in 0 until 4) {
            val t = i / 3f
            val c = Offset(s.llama.x + 2f * u * (t - 0.5f), s.llama.y - 6f * u - s.h * 0.1f * t)
            drawOval(tizne.copy(alpha = 0.09f - 0.04f * t), Offset(c.x - (4f + 8f * t) * u, c.y - (6f + 6f * t) * u), Size((8f + 16f * t) * u, (12f + 12f * t) * u))
        }
    }

    /**
     * Una falta del enlucido: el hueco de contorno deshecho, las piedras redondeadas del sillarejo con su mortero, la sombra que
     * echa dentro el canto de la cal y el filo de luz de ese canto.
     */
    private fun DrawScope.falta(c: Offset, rx: Float, ry: Float, sem: Int, s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val k = cerca(c, s, a)
        val borde = Path().apply {
            val n = 13
            for (i in 0 until n) {
                val ang = i * 6.2832f / n
                val f = 0.72f + 0.42f * azar(sem, i)
                val x = c.x + kotlin.math.cos(ang) * rx * f; val y = c.y + kotlin.math.sin(ang) * ry * f
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(borde, a.luz(mezcla(P.PIEDRA, P.MORTERO, 0.5f), k * 0.9f))
        clipPath(borde) {
            for (j in 0 until 6) {
                val q = Offset(c.x + rx * 1.5f * (azar(sem, 20 + j) - 0.5f), c.y + ry * 1.5f * (azar(sem, 30 + j) - 0.5f))
                val ax = rx * (0.3f + 0.2f * azar(sem, 40 + j)); val ay = ax * (0.55f + 0.2f * azar(sem, 50 + j))
                val tono = mezcla(P.PIEDRA, if (j % 3 == 0) P.OCRE else P.GRIS, 0.15f + 0.3f * azar(sem, 60 + j))
                drawOval(a.frio(0.3f), Offset(q.x - ax + 1f * u, q.y - ay + 1f * u), Size(2 * ax, 2 * ay))
                drawOval(a.luz(tono, k), Offset(q.x - ax, q.y - ay), Size(2 * ax, 2 * ay))
                drawArc(a.claro(0.4f), 190f, 80f, false, Offset(q.x - ax, q.y - ay), Size(2 * ax, 2 * ay), style = Stroke(0.8f * u))
            }
            drawPath(corridoMedieval(borde, 1.8f * u, 1.8f * u), a.frio(0.4f), style = Stroke(3f * u))
        }
        drawPath(corridoMedieval(borde, 0.8f * u, 0.8f * u), a.claro(0.4f), style = Stroke(1f * u))
        drawPath(borde, a.frio(0.25f), style = Stroke(0.7f * u))
    }

    /** El suelo de baldosas de barro, gastadas, con sus juntas, y los juncos que se echan encima. */
    private fun DrawScope.suelo(s: SitioLibretaMedieval, a: AireMedieval, sem: Int) {
        val u = s.u
        val r = Rect(0f, s.suelo, s.w, s.h)
        alumbrado(r, P.BARRO, s, a)
        // Las juntas: hiladas que se acercan, y las llagas que fugan hacia el centro.
        val juntas = Path()
        val filas = listOf(0f, 0.28f, 0.6f, 1f)
        for (f in filas) { val y = r.top + r.height * f; juntas.moveTo(0f, y); juntas.lineTo(s.w, y) }
        for (i in 0 until 3) {
            val y0 = r.top + r.height * filas[i]; val y1 = r.top + r.height * filas[i + 1]
            var x = -s.w * 0.04f * (i + 1); val paso = s.w * (0.11f + 0.025f * i)
            while (x < s.w + paso) {
                val fuga = (x - s.w * 0.5f) * 0.06f
                juntas.moveTo(x - fuga * filas[i], y0); juntas.lineTo(x + fuga * (1f - filas[i + 1]), y1)
                x += paso
            }
        }
        drawPath(juntas, a.sombra(0.3f), style = Stroke(1f * u))
        pinceladas(r, a.claro(1f), 3160, 10, u, 0.08f)
        pinceladas(r, a.sombra(1f), 3161, 8, u, 0.08f)
        // Los juncos secos echados en el suelo.
        val juncos = Path()
        for (i in 0 until 22) {
            val x = s.w * azar(sem + 3170, i); val y = r.top + r.height * (0.15f + 0.8f * azar(sem + 3171, i))
            val l = (8f + 10f * azar(sem + 3172, i)) * u
            val g = (azar(sem + 3173, i) - 0.5f) * 0.5f
            juncos.moveTo(x, y); juncos.quadraticTo(x + l * 0.5f, y + l * g - 1f * u, x + l, y + l * g * 1.6f)
        }
        drawPath(juncos, a.luz(mezcla(P.OCRE_PINTURA, P.PIEDRA_CLARA, 0.3f), 0.5f).copy(alpha = 0.75f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        drawRect(a.sombra(0.35f), Offset(0f, s.suelo), Size(s.w, 2f * u))
    }

    /**
     * La luz de la ventana en el muro, por encima de todo lo que queda contra él: de día, el arco de sol que dibuja la ventana
     * (que queda arriba a la izquierda, fuera del cuadro) y el muro que se enfría hacia la derecha; de noche, la luna, fría y
     * tenue, en el mismo sitio.
     */
    private fun DrawScope.ventana(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val x0 = s.pilar.right + s.w * 0.04f; val x1 = x0 + s.w * 0.2f
        val y0 = s.h * 0.05f; val y1 = s.h * 0.62f
        val arco = medioPuntoMedieval(x0, x1, y0, y1)
        val inclinado = Path().apply { addPath(arco) }
        val color = if (a.oscuro) P.LUNA else P.SOL
        val fuerza = if (a.oscuro) 0.12f else 0.5f
        drawPath(corridoMedieval(inclinado, -3f * u, -2f * u), color.copy(alpha = fuerza * 0.4f))
        drawPath(inclinado, color.copy(alpha = fuerza))
        // El parteluz de la ventana deja su raya de sombra en medio.
        drawLine(a.frio(if (a.oscuro) 0.2f else 0.12f), Offset((x0 + x1) / 2, y0 + (x1 - x0) * 0.3f), Offset((x0 + x1) / 2 + 2f * u, y1), 3f * u)
        if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.frio(0.18f)), s.w * 0.5f, s.w), Offset(s.w * 0.5f, 0f), Size(s.w * 0.5f, s.suelo))
    }

    // ---------------------------------------------------------------- el pilar

    /**
     * El pilar de sillería con su media columna adosada: los sillares del machón, el fuste redondo (la luz de la ventana en su
     * lado, la sombra fría del otro; de noche, casi perdido), la basa con su toro sobre el plinto, y la sombra que echa en el muro.
     */
    private fun DrawScope.pilar(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val p = s.pilar
        val k = cerca(p.center, s, a)
        // La sombra en el muro, a la derecha (de día, de la ventana; de noche, la de la vela cae hacia la izquierda y no se ve).
        if (!a.oscuro) drawPath(poligonoMedieval(p.right, 0f, p.right + s.w * 0.06f, 0f, p.right + s.w * 0.1f, s.suelo, p.right, s.suelo), a.sombra(0.16f))
        val piedra = a.luz(mezcla(P.PIEDRA, P.PIEDRA_CLARA, 0.3f), k)
        drawRect(piedra, p.topLeft, p.size)
        // Los sillares del machón, por hiladas regulares.
        val juntas = Path()
        var y = s.h * 0.04f; var f = 0
        while (y < p.bottom) {
            juntas.moveTo(p.left, y); juntas.lineTo(p.right, y)
            val xj = p.left + p.width * (if (f % 2 == 0) 0.3f else 0.7f)
            juntas.moveTo(xj, y); juntas.lineTo(xj, minOf(y + s.h * 0.11f, p.bottom))
            y += s.h * 0.11f; f++
        }
        drawPath(juntas, a.frio(0.35f), style = Stroke(1f * u))
        // La media columna: el fuste con su luz y su sombra, y las juntas de los tambores.
        val fuste = Rect(p.left + p.width * 0.14f, 0f, p.right - p.width * 0.1f, s.suelo - 13f * u)
        drawRect(a.luz(mezcla(P.PIEDRA_CLARA, P.OCRE, 0.2f), k), fuste.topLeft, fuste.size)
        drawRect(Brush.horizontalGradient(listOf(a.claro(0.3f), Color.Transparent, a.frio(0.35f), a.frio(0.5f)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
        var t = s.h * 0.1f
        while (t < fuste.bottom - 4f * u) {
            drawArc(a.frio(0.3f), 10f, 160f, false, Offset(fuste.left, t - 3f * u), Size(fuste.width, 6f * u), style = Stroke(0.9f * u))
            t += s.h * 0.22f
        }
        // La basa: el toro, la escocia y el plinto.
        val plinto = Rect(p.left, s.suelo - 6f * u, p.right + 3f * u, s.suelo + 4f * u)
        drawRect(a.luz(P.PIEDRA, k), plinto.topLeft, plinto.size)
        drawRect(a.frio(0.3f), Offset(plinto.left, plinto.bottom - 3f * u), Size(plinto.width, 3f * u))
        drawRoundRect(a.luz(P.PIEDRA_CLARA, k), Offset(fuste.left - 3f * u, s.suelo - 12f * u), Size(fuste.width + 6f * u, 6.5f * u), androidx.compose.ui.geometry.CornerRadius(3f * u))
        drawRect(a.frio(0.25f), Offset(fuste.left - 3f * u, s.suelo - 7f * u), Size(fuste.width + 6f * u, 1.2f * u))
        drawLine(a.claro(0.4f), Offset(fuste.left - 2f * u, s.suelo - 11.4f * u), Offset(fuste.right, s.suelo - 11.4f * u), 0.8f * u)
        if (a.oscuro) drawRect(a.filo(0.18f * k), Offset(p.right - 2f * u, 0f), Size(2f * u, p.bottom))
    }
}
