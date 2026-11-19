package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
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
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
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
    /** Lo grueso del códice: de 3 dp en blanco a 25 con 240 hojas o más (a ojos vistas entre 2, 30 y 123). */
    val grueso = (3f + 22f * sqrt(hojas.coerceIn(0, 240) / 240f)) * u
    /** Hacia dónde se ve lo grueso: el canto de delante, a la derecha, y el de abajo. */
    val canto = Offset(grueso * 0.62f, grueso * 0.42f)
    /** Donde descansa el libro: el borde de arriba del reborde del facistol. */
    val liston = h * 0.72f
    private val ancho = minOf(132f * u, w * 0.4f)
    private val cx = w * 0.51f
    /** El códice: la cara de la tapa; lo grueso sale a la derecha y abajo, hasta el reborde. */
    val codice = Rect(cx - ancho / 2 - canto.x / 2, liston - canto.y - 1.5f * u - ancho * 1.17f, cx + ancho / 2 - canto.x / 2, liston - canto.y - 1.5f * u)
    /** El tablero inclinado del facistol, alrededor del códice, con margen a la vista. */
    val tablero = Rect(codice.left - 9f * u, codice.top - 10f * u, codice.right + canto.x + 10f * u, liston + 2f * u)
    /** El eje del pie torneado. */
    val eje = tablero.center.x
    /** El candelero de pie, detrás y a la derecha del facistol: dónde pisa y dónde arde la vela. */
    val candelero = Offset(minOf(w - 36f * u, tablero.right + 46f * u), h * 0.95f)
    val llama = Offset(candelero.x, h * 0.2f)
}

/**
 * La libreta de momentos de Scriptorium (la usa `EstanteMedieval`): el «Códice de momentos» sobre el facistol, en el rincón del
 * scriptorium junto al pilar de la bóveda. Es otro rincón de la misma casa que el armario de la biblioteca y el del Ideario:
 * el mismo muro de cal sobre sillarejo, el mismo barro en el suelo, la misma ventana (fuera del cuadro, arriba a la izquierda)
 * y la misma noche.
 *
 * El facistol es de nogal: el tablero inclinado con su canto y su reborde, el faldón y el pie torneado sobre tres patas. El
 * códice va en tablas de roble con media piel en el lomo (sus nervios en relieve), dos manecillas de latón y la etiqueta de
 * pergamino con el nombre y la cuenta. Se lee libro por lo grueso: el bloque de las hojas por el canto de delante y por el de
 * abajo, que engorda a ojos vistas con las hojas, más cédulas que asoman por arriba y más registros que cuelgan del reborde.
 * Alrededor, con uso: la percha de roble con la bolsa de cuero de llevar el libro y las velas de repuesto colgadas por la
 * mecha; detrás, el candelero de hierro de pie con su vela; en el reborde, el plomo de pautar; al pie, los cuadernillos
 * todavía sin coser, atados, y los juncos del suelo de barro.
 *
 * La luz: de día, la de la ventana, que dibuja su arco de sol en el muro (con la sombra del pilar, de la percha y de la bolsa
 * dentro) y en el suelo, y echa a la derecha las sombras; el resto del muro, más frío. De noche (modo oscuro) arde la vela del
 * candelero: su charco tibio enciende el muro, el canto del libro, la etiqueta y el suelo cercano, y se apaga con la
 * distancia; la izquierda (el pilar, la bolsa) queda en la luna, fría y honda.
 */
internal object MuebleLibretaMedieval {
    private val P = PaletaMedieval

    /** Cuánta luz de la vela le llega a [p] (1 junto a la llama, 0 lejos); de día, toda. */
    fun cerca(p: Offset, s: SitioLibretaMedieval, a: AireMedieval): Float {
        if (!a.lumbre) return 1f
        val dx = (p.x - s.llama.x) / s.h; val dy = (p.y - s.llama.y) / s.h
        return (1f - sqrt(dx * dx * 0.85f + dy * dy) / 1.2f).coerceIn(0f, 1f).pow(1.2f)
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
            if (a.lumbre) {
                mancha(Offset(s.llama.x, s.h * 0.95f), h * 0.32f, P.LLAMA.copy(alpha = 0.14f))
            }
            with(PiezasLibretaMedieval) { percha(s, a) }
            pilar(s, a)
            if (a.oscuro) lunaFria(s)
            with(PiezasLibretaMedieval) { candelero(s, a) }
            with(FacistolLibretaMedieval) { facistol(s, a, e, medidor) }
            with(PiezasLibretaMedieval) { alPie(s, a, e.semilla) }
            with(PiezasLibretaMedieval) { if (a.lumbre) llama(s) }
            granoMueble(Rect(0f, 0f, w, h), oscuro)
        }
    }

    /**
     * Pinta [r] con [color] con la luz del momento: de día, liso; de noche, hundido y con la luz de la vela encima, que se apaga
     * con la distancia (un solo degradado).
     */
    fun DrawScope.alumbrado(r: Rect, color: Color, s: SitioLibretaMedieval, a: AireMedieval) {
        val lejos = a.luz(color, if (a.lumbre) 0.02f else 1f)
        drawRect(lejos, r.topLeft, r.size)
        if (!a.lumbre) return
        // La luz, un degradado de dos colores y solo donde llega (los degradados grandes son lo que más pesa al pintar).
        val radio = s.h * 1.05f
        val caja = Rect(maxOf(r.left, s.llama.x - radio), maxOf(r.top, s.llama.y - radio), minOf(r.right, s.llama.x + radio), minOf(r.bottom, s.llama.y + radio))
        if (caja.width > 0f && caja.height > 0f) drawRect(Brush.radialGradient(listOf(a.luz(color, 0.92f), lejos), center = s.llama, radius = radio), caja.topLeft, caja.size)
        mancha(s.llama, s.h * 0.3f, a.luz(color, 1f).copy(alpha = 0.6f))
    }

    // ---------------------------------------------------------------- el muro, el suelo y la ventana

    /**
     * El muro: cal vieja sobre el sillarejo (las hiladas se adivinan debajo), con pinceladas y desgaste, dos faltas donde
     * asoman las piedras, la humedad que sube del suelo y el hollín de la vela sobre el candelero.
     */
    private fun DrawScope.muro(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val r = Rect(0f, 0f, s.w, s.suelo)
        alumbrado(r, P.CAL, s, a)
        pinceladas(r, a.luz(P.PIEDRA_OSC, 0.45f), 3101, 34, u, 0.08f)
        pinceladas(r, a.luz(P.OCRE, 0.5f), 3103, 12, u, 0.07f)
        pinceladas(r, a.claro(1f), 3102, 16, u, 0.09f)
        // Las hiladas bajo la cal: un relieve leve, la llaga en sombra y su filo de luz.
        val llagas = Path(); val filos = Path()
        var y = s.h * 0.06f; var f = 0
        while (y < s.suelo - 4f * u) {
            var x = -s.w * 0.08f * azar(3110, f); var j = 0
            while (x < s.w) {
                val largo = s.w * (0.08f + 0.1f * azar(3111 + f, j)) + 8f * u
                if (azar(3112 + f, j) > 0.25f) {
                    val dy = 2f * u * (azar(3113 + f, j) - 0.5f)
                    llagas.moveTo(x, y + dy); llagas.lineTo(x + largo * 0.86f, y + dy + 1f * u * (azar(3114 + f, j) - 0.5f))
                    filos.moveTo(x, y + dy + 1.3f * u); filos.lineTo(x + largo * 0.86f, y + dy + 1.3f * u)
                }
                if (azar(3115 + f, j) > 0.45f) { llagas.moveTo(x + largo, y); llagas.lineTo(x + largo + 1f * u, y + s.h * 0.08f * azar(3116 + f, j)) }
                x += largo; j++
            }
            y += s.h * (0.085f + 0.03f * azar(3117, f)); f++
        }
        drawPath(llagas, a.frio(0.16f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawPath(filos, a.claro(0.32f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        falta(Offset((s.pilar.right + s.tablero.left) / 2, s.h * 0.72f), 15f * u, 10f * u, 3120, s, a)
        falta(Offset(s.w * 0.94f, s.h * 0.6f), 12f * u, 14f * u, 3140, s, a)
        // La humedad y el roce que suben del suelo, a manchas.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.frio(0.26f)), s.suelo - s.h * 0.24f, s.suelo), Offset(0f, s.suelo - s.h * 0.24f), Size(s.w, s.h * 0.24f))
        for (i in 0 until 5) {
            val c = Offset(s.w * (0.1f + 0.2f * i + 0.08f * azar(3130, i)), s.suelo - s.h * (0.02f + 0.06f * azar(3131, i)))
            drawOval(a.frio(0.05f), Offset(c.x - s.w * 0.08f, c.y - s.h * 0.03f), Size(s.w * 0.16f, s.h * 0.06f))
        }
        // El hollín de la vela, en el muro encima del candelero.
        val tizne = mezcla(P.NOGAL_OSC, P.NEGRO, 0.5f)
        for (i in 0 until 4) {
            val t = i / 3f
            val c = Offset(s.llama.x + 2f * u * (t - 0.5f), s.llama.y - 8f * u - s.h * 0.11f * t)
            drawOval(tizne.copy(alpha = 0.06f - 0.03f * t), Offset(c.x - (4f + 9f * t) * u, c.y - (6f + 7f * t) * u), Size((8f + 18f * t) * u, (12f + 14f * t) * u))
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
                val x = c.x + cos(ang) * rx * f; val y = c.y + sin(ang) * ry * f
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

    /**
     * El suelo de ladrillo de barro, a soga, con sus juntas que fugan, unos ladrillos más tostados que otros, el desgaste del paso
     * y los juncos que se echan encima; de día, el sol de la ventana en el suelo de la izquierda.
     */
    private fun DrawScope.suelo(s: SitioLibretaMedieval, a: AireMedieval, sem: Int) {
        val u = s.u
        val r = Rect(0f, s.suelo, s.w, s.h)
        alumbrado(r, P.BARRO, s, a)
        val filas = listOf(0f, 0.22f, 0.47f, 0.74f, 1f)
        val juntas = Path(); val tostados = Path()
        for (i in 0 until filas.size - 1) {
            val y0 = r.top + r.height * filas[i]; val y1 = r.top + r.height * filas[i + 1]
            juntas.moveTo(0f, y0); juntas.lineTo(s.w, y0)
            val paso = s.w * (0.075f + 0.02f * i)
            var x = -paso * (if (i % 2 == 0) 0.5f else 0f); var j = 0
            while (x < s.w + paso) {
                val fuga0 = (x - s.w * 0.5f) * 0.05f * filas[i]; val fuga1 = (x - s.w * 0.5f) * 0.05f * filas[i + 1]
                juntas.moveTo(x + fuga0, y0); juntas.lineTo(x + fuga1, y1)
                if (azar(sem + 3150 + i, j) > 0.62f) tostados.addRect(Rect(x + fuga0 + 1f * u, y0 + 1f * u, x + paso + fuga1 - 1f * u, y1 - 1f * u))
                x += paso; j++
            }
        }
        drawPath(tostados, a.sombra(0.14f))
        drawPath(juntas, a.sombra(0.32f), style = Stroke(1f * u))
        pinceladas(r, a.claro(1f), 3160, 12, u, 0.09f)
        pinceladas(r, a.sombra(1f), 3161, 9, u, 0.08f)
        // El paso: el centro, más gastado y claro.
        drawOval(a.claro(0.08f), Offset(s.w * 0.25f, r.top + r.height * 0.35f), Size(s.w * 0.5f, r.height * 0.5f))
        // De día, la luz de la ventana en el suelo, a la izquierda.
        if (!a.oscuro) drawPath(poligonoMedieval(s.pilar.right + s.w * 0.02f, r.top, s.pilar.right + s.w * 0.2f, r.top, s.pilar.right + s.w * 0.3f, s.h,
            s.pilar.right + s.w * 0.06f, s.h), P.SOL.copy(alpha = 0.22f))
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
     * La luz de la ventana en el muro, antes de lo que queda contra él (así sus sombras caen dentro): de día, el arco de sol que
     * dibuja la ventana (arriba a la izquierda, fuera del cuadro), con su borde blando y la raya del parteluz, sobre un muro que
     * se enfría lejos de él; de noche, la luna, fría y tenue, en el mismo sitio.
     */
    private fun DrawScope.ventana(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val x0 = s.pilar.right + s.w * 0.035f; val x1 = x0 + maxOf(s.w * 0.22f, 70f * u)
        val y0 = s.h * 0.04f; val y1 = s.h * 0.66f
        if (!a.oscuro) {
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.frio(0.22f)), x1, s.w), Offset(x1, 0f), Size(s.w - x1, s.suelo))
        }
        val arco = medioPuntoMedieval(x0, x1, y0, y1)
        val color = if (a.oscuro) P.LUNA else mezcla(P.SOL, P.LLAMA, 0.12f)
        val fuerza = if (a.oscuro) 0.13f else 0.62f
        drawPath(corridoMedieval(arco, -4f * u, -3f * u), color.copy(alpha = fuerza * 0.3f))
        drawPath(arco, color.copy(alpha = fuerza * 0.5f), style = Stroke(5f * u))
        drawPath(arco, color.copy(alpha = fuerza))
        drawLine(if (a.oscuro) a.sombra(0.25f) else a.frio(0.16f), Offset((x0 + x1) / 2, y0 + (x1 - x0) * 0.3f), Offset((x0 + x1) / 2 + 2f * u, y1), 3.4f * u)
    }

    /** De noche, lo que la vela no alcanza queda en la luna: la izquierda, fría y bastante más honda. */
    private fun DrawScope.lunaFria(s: SitioLibretaMedieval) {
        drawRect(Brush.horizontalGradient(listOf(P.NOCHE_SUENOS.copy(alpha = 0.5f), Color.Transparent), 0f, s.w * 0.5f), Offset.Zero, Size(s.w * 0.5f, s.h))
    }

    // ---------------------------------------------------------------- el pilar

    /**
     * El pilar de sillería con su media columna adosada: los sillares del machón, el fuste redondo (la luz de la ventana en su
     * lado, la sombra fría del otro), la basa con su toro sobre el plinto, y la sombra que echa en el muro y en el arco de sol.
     */
    private fun DrawScope.pilar(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val p = s.pilar
        val k = cerca(p.center, s, a)
        if (!a.oscuro) drawPath(poligonoMedieval(p.right, 0f, p.right + s.w * 0.05f, 0f, p.right + s.w * 0.11f, s.suelo, p.right, s.suelo), a.sombra(0.22f))
        drawRect(a.luz(mezcla(P.PIEDRA, P.PIEDRA_CLARA, 0.3f), k), p.topLeft, p.size)
        val juntas = Path()
        var y = s.h * 0.04f; var f = 0
        while (y < p.bottom) {
            juntas.moveTo(p.left, y); juntas.lineTo(p.right, y)
            val xj = p.left + p.width * (if (f % 2 == 0) 0.3f else 0.7f)
            juntas.moveTo(xj, y); juntas.lineTo(xj, minOf(y + s.h * 0.11f, p.bottom))
            y += s.h * 0.11f; f++
        }
        drawPath(juntas, a.frio(0.35f), style = Stroke(1f * u))
        pinceladas(p, a.frio(1f), 3190, 6, u, 0.08f)
        val fuste = Rect(p.left + p.width * 0.14f, 0f, p.right - p.width * 0.1f, s.suelo - 13f * u)
        drawRect(a.luz(mezcla(P.PIEDRA_CLARA, P.OCRE, 0.2f), k), fuste.topLeft, fuste.size)
        drawRect(Brush.horizontalGradient(listOf(a.claro(0.34f), Color.Transparent, a.frio(0.38f), a.frio(0.55f)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
        var t = s.h * 0.1f
        while (t < fuste.bottom - 4f * u) {
            drawArc(a.frio(0.3f), 10f, 160f, false, Offset(fuste.left, t - 3f * u), Size(fuste.width, 6f * u), style = Stroke(0.9f * u))
            t += s.h * 0.22f
        }
        val plinto = Rect(p.left, s.suelo - 6f * u, p.right + 3f * u, s.suelo + 4f * u)
        drawRect(a.luz(P.PIEDRA, k), plinto.topLeft, plinto.size)
        drawRect(a.frio(0.3f), Offset(plinto.left, plinto.bottom - 3f * u), Size(plinto.width, 3f * u))
        drawRoundRect(a.luz(P.PIEDRA_CLARA, k), Offset(fuste.left - 3f * u, s.suelo - 12f * u), Size(fuste.width + 6f * u, 6.5f * u), CornerRadius(3f * u))
        drawRect(a.frio(0.25f), Offset(fuste.left - 3f * u, s.suelo - 7f * u), Size(fuste.width + 6f * u, 1.2f * u))
        drawLine(a.claro(0.4f), Offset(fuste.left - 2f * u, s.suelo - 11.4f * u), Offset(fuste.right, s.suelo - 11.4f * u), 0.8f * u)
    }
}
