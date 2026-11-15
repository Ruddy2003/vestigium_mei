package io.github.ruddy2003.vestigium.ui.logros

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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.TrazosGalia
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Galia
// =====================================================================

/**
 * Galia: los logros de la casa grande del jefe, en el oppidum. Cada trofeo descansa en un banco de roble labrado a azuela,
 * con la falera del metal del nivel clavada al frente, y en el hall, en la viga de la misma madera; su placa es una tablilla
 * de cera en su marco, atada con un cordel que pasa por una argolla del metal del nivel. El salón es la casa grande por
 * dentro: los postes de roble entre los trofeos, de donde cuelgan los mantos a cuadros, los escudos largos y las lanzas; las
 * paredes de zarzo y barro; arriba, las vigas y la paja del techo; abajo, el hogar en el centro, con el caldero colgado. De
 * noche, la misma casa a la luz del fuego. En la campaña corre un jinete con su lanza, y al ganar, en Alesia, quedan las armas
 * entregadas con honra y el torques encima.
 */
object LogrosGalia : DisenoLogros {
    private val ROBLE_LABRADO = Color(0xFF8A6A44)
    private val ROBLE_OSCURO = Color(0xFF5A4128)
    private val ROBLE_LUZ = Color(0xFFB8956A)
    private val CORTEZA = Color(0xFF4A3A28)
    private val CERA = Color(0xFF3B2C1E)
    private val BARRO = TrazosGalia.BARRO
    private val PAJA = TrazosGalia.PAJA
    private val NOCHE = Color(0xFF0F1410)
    private val FUEGO = TrazosGalia.FUEGO
    private val HALO = TrazosGalia.FUEGO_HALO
    /** La tierra apisonada del suelo y las piedras del hogar. */
    private val TIERRA = Color(0xFF7C6648)
    private val PIEDRA = Color(0xFF8E8A7E)

    /** El color de noche: la misma madera a la luz del fuego, hundida en el verde negro del robledal. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.5f) else c

    /** El gris translúcido de lo que aún no se gana. */
    private fun silueta(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1CF).copy(alpha = 0.13f) else Color(0xFF20251A).copy(alpha = 0.13f)

    private fun DrawScope.fr(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)
    private fun DrawScope.fp(x: Float, y: Float) = Offset(size.width * x, size.height * y)

    /** La madera en volumen: la luz entra por la izquierda. */
    private fun madera(c: Color, r: Rect): Brush = Brush.horizontalGradient(listOf(mezcla(c, Color.White, 0.16f), c, mezcla(c, Color.Black, 0.32f)), r.left, r.right)

    /** Las huellas de la azuela en una cara de madera: conchas cortas en hileras, con su lado en sombra. */
    private fun DrawScope.azuela(r: Rect, c: Color, lw: Float, semilla: Int) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + lw * 2.5f; var fila = 0
            while (y < r.bottom && fila < 60) {
                var x = r.left + (if (fila % 2 == 0) 0f else lw * 4f); var k = 0
                while (x < r.right && k < 200) {
                    val l = lw * (5f + 3f * azar(semilla + fila, k))
                    drawArc(mezcla(c, Color.Black, 0.3f).copy(alpha = 0.45f), 200f, 140f, false, Offset(x, y - lw * 1.2f), Size(l, lw * 2.4f), style = Stroke(lw * 0.5f))
                    x += l + lw * 1.5f; k++
                }
                y += lw * 4.2f; fila++
            }
        }
    }

    /** La falera: el disco del metal del nivel clavado a la madera, con su anillo de perlas y el botón del centro; el legendario, con su brillo. */
    private fun DrawScope.falera(c: Offset, r: Float, e: EstadoPedestal, fino: Boolean) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.28f), r, c + Offset(r * 0.12f, r * 0.16f))
        drawCircle(Brush.radialGradient(listOf(e.brillo, e.base, e.metalOscuro), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.5f), r, c)
        drawCircle(e.metalOscuro, r, c, style = Stroke(maxOf(r * 0.1f, 0.45f * u)))
        if (fino) {
            for (k in 0 until 12) { val a = k * PI / 6; drawCircle(e.metalOscuro.copy(alpha = 0.7f), r * 0.08f, Offset(c.x + r * 0.72f * cos(a).toFloat(), c.y + r * 0.72f * sin(a).toFloat())) }
            drawCircle(e.metalOscuro.copy(alpha = 0.6f), r * 0.45f, c, style = Stroke(r * 0.08f))
        }
        drawCircle(Brush.radialGradient(listOf(e.brillo, e.metalOscuro), Offset(c.x - r * 0.08f, c.y - r * 0.08f), r * 0.3f), r * 0.24f, c)
        if (e.nivel >= 4 && fino) drawLine(Color.White.copy(alpha = 0.85f), Offset(c.x - r * 0.62f, c.y + r * 0.1f), Offset(c.x - r * 0.18f, c.y - r * 0.6f), maxOf(r * 0.12f, 0.5f * u), StrokeCap.Round)
    }

    /** La onda corrida de La Tène, tallada a lo largo de [r]: una línea que sube y baja en curvas, con su sombra. */
    private fun DrawScope.ondaTallada(r: Rect, c: Color, lw: Float) {
        val paso = r.height * 1.6f
        val onda = Path().apply {
            var x = r.left; moveTo(x, r.center.y)
            while (x < r.right) {
                cubicTo(x + paso * 0.2f, r.top, x + paso * 0.45f, r.top, x + paso * 0.5f, r.center.y)
                cubicTo(x + paso * 0.55f, r.bottom, x + paso * 0.8f, r.bottom, x + paso, r.center.y); x += paso
            }
        }
        clipRect(r.left, r.top - lw, r.right, r.bottom + lw) {
            drawPath(onda, Color.White.copy(alpha = 0.18f), style = Stroke(lw * 1.1f), alpha = 1f)
            drawPath(Path().apply { addPath(onda, Offset(lw * 0.6f, lw * 0.6f)) }, mezcla(c, Color.Black, 0.45f).copy(alpha = 0.7f), style = Stroke(lw * 0.9f))
        }
    }

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El banco de roble labrado a azuela: la tabla de arriba con su canto claro, el frente con la onda tallada y la falera del
     * metal del nivel clavada en medio, y la base más oscura; de noche, la misma madera con la luz del hogar que le da de lado.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val h = size.height; val u = density
        val fino = h > 70 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tabla = fr(0.09f, 0.768f, 0.91f, 0.8f)
        val cuerpo = fr(0.13f, 0.8f, 0.87f, 0.928f)
        val base = fr(0.1f, 0.928f, 0.9f, 0.957f)
        if (!e.hecho) { drawPath(Path().apply { addRect(tabla); addRect(cuerpo); addRect(base) }, silueta(oscuro)); return }
        // La sombra al pie.
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.38f else 0.17f), fp(0.07f, 0.945f), Size(size.width * 0.88f, h * 0.026f))
        val c = t(ROBLE_LABRADO, oscuro)
        drawRect(madera(t(ROBLE_OSCURO, oscuro), base), base.topLeft, base.size)
        drawRect(madera(c, cuerpo), cuerpo.topLeft, cuerpo.size)
        if (fino) {
            azuela(cuerpo, c, lw, e.semilla)
            // La onda tallada, a los dos lados de la falera.
            val alto = cuerpo.height * 0.2f
            listOf(Rect(cuerpo.left + lw * 3, cuerpo.center.y - alto / 2, cuerpo.center.x - h * 0.05f, cuerpo.center.y + alto / 2),
                Rect(cuerpo.center.x + h * 0.05f, cuerpo.center.y - alto / 2, cuerpo.right - lw * 3, cuerpo.center.y + alto / 2)).forEach { ondaTallada(it, c, lw) }
            // Los clavos de hierro de las esquinas.
            listOf(cuerpo.left + lw * 3, cuerpo.right - lw * 3).forEach { x -> listOf(cuerpo.top + lw * 3, cuerpo.bottom - lw * 3).forEach { y -> drawCircle(t(TrazosGalia.HIERRO, oscuro), lw * 0.9f, Offset(x, y)) } }
        }
        falera(Offset(cuerpo.center.x, cuerpo.center.y), h * 0.034f, e, fino)
        // La tabla de arriba, con el canto claro y su sombra sobre el frente.
        drawRect(madera(mezcla(c, ROBLE_LUZ, if (oscuro) 0.15f else 0.45f), tabla), tabla.topLeft, tabla.size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.45f), Offset(tabla.left, tabla.top + lw * 0.5f), Offset(tabla.right, tabla.top + lw * 0.5f), lw)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), cuerpo.top, cuerpo.top + cuerpo.height * 0.16f), cuerpo.topLeft, Size(cuerpo.width, cuerpo.height * 0.16f))
        // Las vetas de la tabla.
        if (fino) listOf(0.3f, 0.62f).forEach { f -> drawLine(mezcla(c, Color.Black, 0.3f).copy(alpha = 0.4f), Offset(tabla.left + tabla.width * f, tabla.top + tabla.height * 0.4f), Offset(tabla.left + tabla.width * (f + 0.18f), tabla.top + tabla.height * 0.55f), lw * 0.5f) }
        // De noche, la luz del hogar que le da de lado.
        if (oscuro) drawRect(Brush.horizontalGradient(listOf(HALO.copy(alpha = 0.2f), Color.Transparent), tabla.left, tabla.center.x), Offset(tabla.left, tabla.top), Size(tabla.width / 2, base.bottom - tabla.top))
    }

    /** La viga de roble del hall: el canto claro, el frente labrado a azuela con la onda tallada y la falera del nivel en medio. */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val canto = fr(0f, 0f, 1f, 0.3f)
        val frente = fr(0.03f, 0.3f, 0.97f, 1f)
        if (!e.hecho) { drawPath(Path().apply { addRect(canto); addRect(frente) }, silueta(oscuro)); return }
        val c = t(ROBLE_LABRADO, oscuro)
        drawRect(madera(c, frente), frente.topLeft, frente.size)
        azuela(frente, c, lw * 0.45f, e.semilla)
        val alto = frente.height * 0.3f
        listOf(Rect(frente.left + lw * 2, frente.center.y - alto / 2, frente.center.x - h * 0.42f, frente.center.y + alto / 2),
            Rect(frente.center.x + h * 0.42f, frente.center.y - alto / 2, frente.right - lw * 2, frente.center.y + alto / 2)).forEach { ondaTallada(it, c, lw * 0.5f) }
        falera(Offset(frente.center.x, frente.top + frente.height * 0.52f), h * 0.28f, e, true)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), frente.top, frente.top + frente.height * 0.25f), frente.topLeft, Size(frente.width, frente.height * 0.25f))
        drawRect(madera(mezcla(c, ROBLE_LUZ, if (oscuro) 0.15f else 0.45f), canto), canto.topLeft, canto.size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.45f), Offset(0f, lw * 0.5f), Offset(size.width, lw * 0.5f), lw * 0.8f)
    }

    override val margenPlaca: Dp = 11.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFFF1E7CC)
    override fun rotuloPlaca(oscuro: Boolean): Color = Color(0xFFE9B55C)

    /**
     * La tablilla de cera: el marco de roble con sus esquinas, la cera oscura donde se escribe con el punzón (con unos
     * rasguños de lo que se borró) y, a la izquierda, el cordel que la ata por una argolla del metal del nivel.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = minOf(2.5f * u, h * 0.08f)
        val marco = Rect(m, m, w - m, h - m)
        val borde = minOf(4f * u, h * 0.12f)
        val cera = Rect(marco.left + borde, marco.top + borde, marco.right - borde, marco.bottom - borde)
        if (!e.hecho) { drawRect(silueta(oscuro), marco.topLeft, marco.size); return }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), marco.topLeft + Offset(0.8f * u, 1.2f * u), marco.size)
        drawRect(Brush.verticalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE_LABRADO, oscuro), mezcla(t(ROBLE_LABRADO, oscuro), Color.Black, 0.25f)), marco.top, marco.bottom), marco.topLeft, marco.size)
        // Las vetas del marco.
        listOf(0.3f, 0.7f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.18f), Offset(marco.left + 2 * u, marco.top + borde * f), Offset(marco.right - 2 * u, marco.top + borde * f), 0.5f * u) }
        listOf(0.3f, 0.7f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.18f), Offset(marco.left + 2 * u, cera.bottom + borde * f), Offset(marco.right - 2 * u, cera.bottom + borde * f), 0.5f * u) }
        // La cera, hundida en el marco, con los rasguños de lo borrado.
        drawRect(Brush.verticalGradient(listOf(mezcla(CERA, Color.Black, 0.25f), CERA, mezcla(CERA, Color(0xFF6A5236), 0.25f)), cera.top, cera.bottom), cera.topLeft, cera.size)
        clipRect(cera.left, cera.top, cera.right, cera.bottom) {
            for (k in 0 until 7) {
                val x = cera.left + cera.width * azar(e.semilla, k); val y = cera.top + cera.height * azar(e.semilla, 20 + k)
                drawLine(Color(0xFFB8956A).copy(alpha = 0.12f), Offset(x, y), Offset(x + 6 * u, y + (azar(e.semilla, 40 + k) - 0.5f) * 2 * u), 0.5f * u)
            }
        }
        drawRect(Color.Black.copy(alpha = 0.45f), cera.topLeft, Size(cera.width, 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), cera.topLeft, Size(1.2f * u, cera.height))
        // La argolla del nivel y el cordel.
        val a = Offset(marco.left + borde * 0.5f, h / 2)
        drawCircle(Color.Black.copy(alpha = 0.6f), 1.1f * u, a)
        drawCircle(Brush.linearGradient(listOf(e.metalOscuro, e.brillo, e.base), Offset(a.x - 3 * u, a.y - 3 * u), Offset(a.x + 3 * u, a.y + 3 * u)), 3f * u, a, style = Stroke(1f * u))
        drawPath(Path().apply { moveTo(a.x - 2.6f * u, a.y + 1.5f * u); quadraticTo(a.x - 5f * u, a.y + 6f * u, a.x - 2f * u, minOf(h, a.y + 11f * u)) }, t(Color(0xFFCDB98E), oscuro), style = Stroke(0.9f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- el salón del hall

    /** La geometría de la casa grande: una franja por columna, el poste de roble en el borde de cada franja y el trofeo en medio. */
    private class GeoCasa(val w: Float, val h: Float, val u: Float, columnas: Int) {
        val n = columnas.coerceIn(1, 6)
        val paso = w / n
        val poste = minOf(paso * 0.11f, 14f * u)
        val techo = minOf(26f * u, h * 0.08f)
        val piso = minOf(22f * u, h * 0.08f)
        val suelo = h - piso
        val modulo = maxOf(230f * u, 1f)
        fun borde(i: Int) = i * paso
    }

    /**
     * La casa grande del jefe, por dentro: las paredes de zarzo y barro con su trama asomando, los postes de roble entre los
     * trofeos con lo que cuelga de ellos (el manto a cuadros, el escudo largo con la lanza, el torques), la tabla de cada
     * trofeo, las vigas y la paja del techo, y abajo la tierra apisonada con el hogar en el centro, el caldero colgado y el
     * humo que sube. De día, la luz que entra por la puerta; de noche, la misma casa a la luz del fuego.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoCasa(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaCasa(g, e, oscuro) }
    }

    private fun DrawScope.escenaCasa(g: GeoCasa, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)
        // La pared de zarzo y barro: las estacas, las varas tejidas que asoman donde se cayó el barro y las grietas.
        var x = 18f * u; var k = 0
        while (x < w && k < 200) {
            drawLine(m(ROBLE_OSCURO, if (oscuro) 0.16f else 0.1f), Offset(x, g.techo), Offset(x, g.suelo), 2f * u)
            x += 34f * u; k++
        }
        var y = g.techo + 12f * u; var fila = 0
        while (y < g.suelo && fila < 300) {
            if (azar(e.semilla + 3, fila) < 0.5f) {
                val x0 = azar(e.semilla, fila) * w; val l = (40f + 70f * azar(e.semilla, 100 + fila)) * u
                val trama = Path().apply {
                    moveTo(x0, y); var xx = x0; var arriba = true
                    while (xx < x0 + l) { quadraticTo(xx + 8.5f * u, y + (if (arriba) -2.2f else 2.2f) * u, xx + 17f * u, y); xx += 17f * u; arriba = !arriba }
                }
                drawPath(trama, m(PAJA, if (oscuro) 0.14f else 0.22f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            }
            y += 9f * u; fila++
        }
        // El barro alisado, con algunas grietas.
        repeat(10) { i ->
            val a = Offset(azar(e.semilla, 300 + i) * w, g.techo + azar(e.semilla, 320 + i) * (g.suelo - g.techo))
            val b = a + Offset((azar(e.semilla, 340 + i) - 0.5f) * 14f * u, 10f * u)
            drawLine(m(Color(0xFF3A2C1E), if (oscuro) 0.14f else 0.1f), a, b, 0.7f * u)
            drawLine(m(Color(0xFF3A2C1E), if (oscuro) 0.12f else 0.08f), b, b + Offset(4f * u, 7f * u), 0.6f * u)
        }
        // La luz: de día, la que entra por la puerta abierta a la izquierda; de noche, la del hogar que sube desde abajo.
        if (oscuro) drawRect(Brush.verticalGradient(listOf(Color.Transparent, HALO.copy(alpha = 0.08f), HALO.copy(alpha = 0.16f)), g.techo, g.suelo), Offset(0f, g.techo), Size(w, g.suelo - g.techo))
        else drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent), 0f, w * 0.5f), Offset(0f, g.techo), Size(w * 0.5f, g.suelo - g.techo))
        // La tabla de cada trofeo, una por columna y por fila.
        for (i in 0 until g.n) {
            val x0 = g.borde(i) + g.poste + 4f * u; val x1 = g.borde(i + 1) - g.poste - 4f * u
            if (x1 - x0 < 8 * u) continue
            var y0 = g.techo + 8f * u; var k2 = 0
            while (y0 < g.suelo - 40 * u && k2 < 20) {
                val yb = minOf(y0 + g.modulo - 14f * u, g.suelo - 4 * u)
                drawRect(m(ROBLE_LABRADO, if (oscuro) 0.3f else 0.4f), Offset(x0 - 2 * u, yb - 3 * u), Size(x1 - x0 + 4 * u, 3 * u))
                drawRect(Brush.verticalGradient(listOf(m(Color.Black, 0.12f), Color.Transparent), yb, yb + 6 * u), Offset(x0 - 2 * u, yb), Size(x1 - x0 + 4 * u, 6 * u))
                // Las dos ménsulas que la sostienen.
                listOf(x0 + 2 * u, x1 - 6 * u).forEach { xm -> drawPath(Path().apply { moveTo(xm, yb); lineTo(xm + 4 * u, yb); lineTo(xm + 4 * u, yb + 6 * u); close() }, m(ROBLE_OSCURO, if (oscuro) 0.3f else 0.35f)) }
                y0 += g.modulo; k2++
            }
        }
        // Los postes de roble, con su corteza, y lo que cuelga de ellos en cada fila.
        for (i in 0..g.n) {
            val cx = g.borde(i)
            val poste = Rect(cx - g.poste, g.techo, cx + g.poste, g.suelo)
            drawRect(Brush.horizontalGradient(listOf(m(ROBLE_LABRADO, if (oscuro) 0.32f else 0.4f), m(CORTEZA, if (oscuro) 0.42f else 0.45f)), poste.left, poste.right), poste.topLeft, poste.size)
            var yv = g.techo + 6 * u; var kk = 0
            while (yv < g.suelo && kk < 400) { val xx = poste.left + azar(e.semilla + i, kk) * poste.width; drawLine(m(Color.Black, 0.12f), Offset(xx, yv), Offset(xx + (azar(e.semilla, kk) - 0.5f) * 2 * u, yv + 14 * u), 0.7f * u); yv += 7 * u; kk++ }
            var yc = g.techo + g.modulo * 0.3f; var kc = 0
            while (yc < g.suelo - 50 * u && kc < 20) {
                val px = cx.coerceIn(14f * u, w - 14f * u)
                drawCircle(m(ROBLE_OSCURO, 0.5f), 1.6f * u, Offset(px, yc))
                when ((i + kc) % 3) {
                    0 -> mantoColgado(Offset(px, yc), u, oscuro, e.fondo)
                    1 -> escudoColgado(Offset(px, yc), u, oscuro, e.fondo)
                    else -> {
                        drawLine(m(TrazosGalia.HIERRO, 0.45f), Offset(px, yc + 1 * u), Offset(px, yc + 6 * u), 0.7f * u)
                        with(TrazosGalia) { torques(Offset(px, yc + 12 * u), 6f * u, 1.6f * u, m(TrazosGalia.ORO, if (oscuro) 0.6f else 0.55f), m(TrazosGalia.ORO_BRILLO, 0.5f)) }
                    }
                }
                yc += g.modulo; kc++
            }
        }
        // El techo: la paja en la penumbra, las vigas y el larguero.
        val techo = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, g.techo)
            var xx = w; var kt = 0
            while (xx > 0f && kt < 300) { xx -= (5f + azar(e.semilla, 500 + kt) * 5f) * u; lineTo(maxOf(xx, 0f), g.techo + (azar(e.semilla, 700 + kt) * 4f) * u); kt++ }
            close()
        }
        drawPath(techo, m(Color(0xFF6A5534), if (oscuro) 0.5f else 0.3f))
        clipPath(techo) {
            var xp = -g.techo; var kp = 0
            while (xp < w && kp < 400) { drawLine(m(PAJA, if (oscuro) 0.22f else 0.38f), Offset(xp, 0f), Offset(xp + g.techo * 0.6f, g.techo + 4 * u), 0.8f * u); xp += 3.2f * u; kp++ }
        }
        drawRect(m(ROBLE_OSCURO, if (oscuro) 0.6f else 0.5f), Offset(0f, g.techo - 3 * u), Size(w, 5 * u))
        for (i in 0..g.n) {
            val cx = g.borde(i)
            drawLine(m(ROBLE_OSCURO, if (oscuro) 0.55f else 0.45f), Offset(cx, g.techo), Offset(cx + (if (i * 2 < g.n) 1f else -1f) * 22 * u, 0f), 4 * u)
        }
        // El suelo de tierra apisonada y el hogar en el centro, con su anillo de piedras, el fuego, el caldero y el humo.
        drawRect(m(TIERRA, if (oscuro) 0.4f else 0.32f), Offset(0f, g.suelo), Size(w, g.piso))
        drawLine(m(Color.Black, 0.25f), Offset(0f, g.suelo), Offset(w, g.suelo), 1 * u)
        repeat(30) { i -> drawCircle(m(Color(0xFF4A3A28), 0.2f), (0.6f + azar(e.semilla, 900 + i)) * u, Offset(azar(e.semilla, 950 + i) * w, g.suelo + 3 * u + azar(e.semilla, 990 + i) * (g.piso - 5 * u))) }
        val hx = w / 2; val hy = g.suelo + g.piso * 0.62f
        if (oscuro) drawCircle(Brush.radialGradient(listOf(HALO.copy(alpha = 0.35f), Color.Transparent), Offset(hx, hy - 10 * u), 90 * u), 90 * u, Offset(hx, hy - 10 * u))
        drawPath(Path().apply { moveTo(hx, hy - 18 * u); quadraticTo(hx - 8 * u, hy - 40 * u, hx + 3 * u, hy - 60 * u); quadraticTo(hx + 12 * u, hy - 80 * u, hx + 2 * u, hy - 100 * u) },
            m(TrazosGalia.HUMO, if (oscuro) 0.18f else 0.3f), style = Stroke(5 * u, cap = StrokeCap.Round))
        for (k in 0 until 9) { val a = PI * k / 8; drawOval(m(PIEDRA, if (oscuro) 0.45f else 0.6f), Offset(hx - 18 * u * cos(a).toFloat() - 3 * u, hy - 1.5f * u), Size(6 * u, 4 * u)) }
        with(TrazosGalia) { hoguera(Offset(hx, hy), 20f * u, if (oscuro) 1f else 0.85f, m(TrazosGalia.MADERA, 0.8f)) }
        // El caldero colgado de su cadena sobre el fuego, a un lado para no tapar la llama.
        drawLine(m(TrazosGalia.HIERRO, 0.6f), Offset(hx + 16 * u, g.suelo - 40 * u), Offset(hx + 16 * u, g.suelo - 12 * u), 0.9f * u)
        drawPath(Path().apply { moveTo(hx + 8 * u, g.suelo - 12 * u); lineTo(hx + 24 * u, g.suelo - 12 * u); quadraticTo(hx + 25 * u, g.suelo - 1 * u, hx + 16 * u, g.suelo); quadraticTo(hx + 7 * u, g.suelo - 1 * u, hx + 8 * u, g.suelo - 12 * u); close() },
            m(TrazosGalia.BRONCE, if (oscuro) 0.6f else 0.65f))
    }

    /** El manto a cuadros, colgado de su clavija y caído en pliegues. */
    private fun DrawScope.mantoColgado(o: Offset, u: Float, oscuro: Boolean, fondo: Color) {
        fun m(c: Color, k: Float) = mezcla(fondo, c, k)
        val manto = Path().apply {
            moveTo(o.x - 3 * u, o.y); lineTo(o.x + 3 * u, o.y)
            quadraticTo(o.x + 9 * u, o.y + 16 * u, o.x + 8 * u, o.y + 32 * u); lineTo(o.x + 4 * u, o.y + 30 * u); lineTo(o.x, o.y + 34 * u); lineTo(o.x - 4 * u, o.y + 30 * u); lineTo(o.x - 8 * u, o.y + 32 * u)
            quadraticTo(o.x - 9 * u, o.y + 16 * u, o.x - 3 * u, o.y); close()
        }
        drawPath(manto, m(TrazosGalia.LANA_ROJA, if (oscuro) 0.5f else 0.55f))
        clipPath(manto) {
            listOf(-5f, 1f, 6f).forEach { dx -> drawRect(m(TrazosGalia.LANA_VERDE, if (oscuro) 0.4f else 0.45f), Offset(o.x + dx * u - 1 * u, o.y), Size(2 * u, 36 * u)) }
            listOf(8f, 18f, 27f).forEach { dy -> drawRect(m(TrazosGalia.LANA_VERDE, if (oscuro) 0.35f else 0.4f), Offset(o.x - 10 * u, o.y + dy * u), Size(20 * u, 2 * u)) }
            listOf(13f, 23f).forEach { dy -> drawLine(m(TrazosGalia.LANA_OCRE, 0.5f), Offset(o.x - 10 * u, o.y + dy * u), Offset(o.x + 10 * u, o.y + dy * u), 0.6f * u) }
            listOf(-2f, 3f).forEach { dx -> drawLine(m(Color.Black, 0.25f), Offset(o.x + dx * u, o.y + 3 * u), Offset(o.x + dx * 1.6f * u, o.y + 32 * u), 1 * u) }
        }
    }

    /** El escudo largo de tablas, ovalado, con su espina y su umbo, colgado con la lanza detrás. */
    private fun DrawScope.escudoColgado(o: Offset, u: Float, oscuro: Boolean, fondo: Color) {
        fun m(c: Color, k: Float) = mezcla(fondo, c, k)
        drawLine(m(ROBLE_OSCURO, 0.45f), Offset(o.x - 9 * u, o.y + 40 * u), Offset(o.x + 8 * u, o.y - 6 * u), 1.1f * u)
        drawPath(Path().apply { moveTo(o.x + 8 * u, o.y - 6 * u); lineTo(o.x + 9.5f * u, o.y - 13 * u); lineTo(o.x + 11 * u, o.y - 5 * u); close() }, m(TrazosGalia.HIERRO, 0.55f))
        val esc = Rect(o.x - 6.5f * u, o.y + 2 * u, o.x + 6.5f * u, o.y + 36 * u)
        drawOval(m(TrazosGalia.LANA_VERDE, if (oscuro) 0.45f else 0.5f), esc.topLeft, esc.size)
        drawOval(m(ROBLE_OSCURO, 0.55f), esc.topLeft, esc.size, style = Stroke(1 * u))
        drawLine(m(ROBLE_LUZ, 0.5f), Offset(o.x, esc.top + 2 * u), Offset(o.x, esc.bottom - 2 * u), 1.4f * u)
        drawOval(m(TrazosGalia.HIERRO, 0.6f), Offset(o.x - 3 * u, esc.center.y - 4 * u), Size(6 * u, 8 * u))
    }

    // ---------------------------------------------------------------- las campañas

    /** La sombra del marcador en el suelo, bajo [p]: se achica y se aclara cuando brinca ([salto], de 0 a 1). */
    private fun DrawScope.sombraJinete(p: Offset, ancho: Float, salto: Float, oscuro: Boolean) {
        val f = 1f - 0.35f * salto.coerceIn(0f, 1f)
        val a = ancho * f; val b = 3.2f * density * f
        drawOval(Color.Black.copy(alpha = (if (oscuro) 0.5f else 0.22f) * f), Offset(p.x - a / 2, p.y - b / 2), Size(a, b))
    }

    /**
     * El jinete que lleva el grito de oppidum en oppidum: el caballo al trote, el jinete con su manto a cuadros que ondea y la
     * lanza alzada; cuando brinca (hoy falta cumplir), el caballo se encabrita. De noche, con la luz de una antorcha en la lanza.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        sombraJinete(e.punto, 20f * u, s, oscuro)
        val pelo = if (oscuro) Color(0xFF8A6A48) else Color(0xFF5E4026)
        val crin = if (oscuro) Color(0xFF3A2A1A) else Color(0xFF2E2014)
        val pie = e.punto.y - 1f * u - s * 4f * u
        val c = Offset(e.punto.x, pie - 9f * u)
        rotate(-14f * s, Offset(c.x - 5 * u, pie)) {
            fun q(x: Float, y: Float) = Offset(c.x + x * u, c.y + y * u)
            // Las patas.
            listOf(Triple(-6f, -7.5f, 0f), Triple(-4f, -3.5f, 0f), Triple(4f, 5f, 0f), Triple(6f, 9f, -3f)).forEachIndexed { i, (x0, x1, dy) ->
                val col = if (i % 2 == 0) crin else pelo
                drawLine(col, q(x0, 2f), q(x1, 8.5f + dy), 1.5f * u, StrokeCap.Round)
            }
            // El cuerpo, el cuello, la cabeza y la cola.
            drawOval(pelo, Offset(c.x - 8.5f * u, c.y - 2.5f * u), Size(17f * u, 6.5f * u))
            drawLine(pelo, q(5.5f, 0f), q(9f, -6f), 3.2f * u, StrokeCap.Round)
            drawLine(pelo, q(9f, -6.5f), q(12.5f, -4.5f), 2.4f * u, StrokeCap.Round)
            drawLine(crin, q(8f, -7.5f), q(7.6f, -9.5f), 0.9f * u, StrokeCap.Round)
            drawLine(crin, q(5f, -1.5f), q(8f, -7f), 1.1f * u, StrokeCap.Round)
            drawPath(Path().apply { moveTo(q(-8f, -1f).x, q(-8f, -1f).y); quadraticTo(q(-12f, -2f).x, q(-12f, -2f).y, q(-11f, 4f).x, q(-11f, 4f).y) }, crin, style = Stroke(1.3f * u, cap = StrokeCap.Round))
            // El manto a cuadros que ondea, el jinete y la lanza.
            val manto = Path().apply { moveTo(q(-0.5f, -9f).x, q(-0.5f, -9f).y); quadraticTo(q(-6f, -8f).x, q(-6f, -8f).y, q(-9f, -4f - 2f * s).x, q(-9f, -4f - 2f * s).y); lineTo(q(-1f, -3f).x, q(-1f, -3f).y); close() }
            drawPath(manto, TrazosGalia.LANA_ROJA)
            drawLine(TrazosGalia.LANA_VERDE, q(-2.5f, -8.5f), q(-5f, -4f), 0.7f * u)
            drawLine(if (oscuro) Color(0xFFD9C9A8) else Color(0xFF6B5136), q(0f, -2.5f), q(0f, -9f), 2.6f * u, StrokeCap.Round)
            drawCircle(if (oscuro) Color(0xFFE1C9A4) else Color(0xFFC79A72), 1.9f * u, q(0.3f, -11f))
            drawLine(TrazosGalia.ORO, q(-1.2f, -9.2f), q(1.6f, -9.2f), 0.8f * u, StrokeCap.Round)
            drawLine(if (oscuro) Color(0xFFCFC3A8) else TrazosGalia.MADERA, q(2.5f, 2f), q(4.5f, -20f), 0.9f * u, StrokeCap.Round)
            drawPath(Path().apply { moveTo(q(4.5f, -20f).x, q(4.5f, -20f).y); lineTo(q(3.4f, -17f).x, q(3.4f, -17f).y); lineTo(q(4.8f, -24f).x, q(4.8f, -24f).y); lineTo(q(5.6f, -17f).x, q(5.6f, -17f).y); close() }, TrazosGalia.HIERRO)
            if (oscuro) drawCircle(Brush.radialGradient(listOf(HALO.copy(alpha = 0.35f), Color.Transparent), q(4.8f, -22f), 10 * u), 10 * u, q(4.8f, -22f))
        }
    }

    /**
     * La entrega con honra, en Alesia (César 7.89; Plutarco, *César* 27, cuenta que llegó con sus mejores armas, dio la vuelta
     * a caballo y las arrojó al suelo): el escudo largo de pie, la espada tendida delante, el casco al lado y, encima, el
     * torques que brilla. Detrás, las dos hogueras del cerco; de noche, encendidas.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val ancho = 46f * u; val alto = 48f * u; val mg = 2f * u
        val x = if (size.width > ancho + 2 * mg) e.meta.x.coerceIn(ancho / 2 + mg, size.width - ancho / 2 - mg) else size.width / 2
        val y = if (size.height > alto + 2 * mg) (e.meta.y - 2f * u).coerceIn(alto + mg, size.height - mg) else size.height - mg
        val linea = if (oscuro) Color(0xFFE6E1CF).copy(alpha = 0.5f) else Color(0xFF3A2C1E)
        // La luz del honor, detrás del torques.
        drawCircle(Brush.radialGradient(listOf(HALO.copy(alpha = if (oscuro) 0.5f else 0.35f), Color.Transparent), Offset(x, y - 34 * u), 20 * u), 20 * u, Offset(x, y - 34 * u))
        // Las hogueras del cerco, chicas y lejos.
        with(TrazosGalia) {
            hoguera(Offset(x - 19 * u, y - 2 * u), 7f * u, if (oscuro) 1f else 0.6f)
            hoguera(Offset(x + 20 * u, y - 3 * u), 6f * u, if (oscuro) 0.9f else 0.5f)
        }
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(x - 17 * u, y - 1.6f * u), Size(34 * u, 3.2f * u))
        // El escudo largo, de pie y un poco inclinado.
        rotate(-8f, Offset(x - 4 * u, y)) {
            val esc = Rect(x - 12 * u, y - 30 * u, x + 2 * u, y)
            drawOval(Brush.horizontalGradient(listOf(mezcla(TrazosGalia.LANA_VERDE, Color.White, 0.2f), TrazosGalia.LANA_VERDE, mezcla(TrazosGalia.LANA_VERDE, Color.Black, 0.35f)), esc.left, esc.right), esc.topLeft, esc.size)
            drawOval(linea, esc.topLeft, esc.size, style = Stroke(0.8f * u))
            drawLine(ROBLE_LUZ, Offset(esc.center.x, esc.top + 2 * u), Offset(esc.center.x, esc.bottom - 2 * u), 1.4f * u)
            drawOval(Brush.radialGradient(listOf(Color(0xFFB9BBBC), TrazosGalia.HIERRO), Offset(esc.center.x - 1 * u, esc.center.y - 2 * u), 4 * u), Offset(esc.center.x - 3 * u, esc.center.y - 4.5f * u), Size(6 * u, 9 * u))
        }
        // El casco al lado, en el suelo.
        val cc = Offset(x + 10 * u, y - 4 * u)
        drawArc(Brush.radialGradient(listOf(TrazosGalia.ORO_BRILLO, TrazosGalia.BRONCE), Offset(cc.x - 2 * u, cc.y - 4 * u), 8 * u), 180f, 180f, true, Offset(cc.x - 6 * u, cc.y - 6 * u), Size(12 * u, 12 * u))
        drawLine(TrazosGalia.BRONCE, Offset(cc.x - 7 * u, cc.y), Offset(cc.x + 7 * u, cc.y), 1.2f * u, StrokeCap.Round)
        drawCircle(TrazosGalia.BRONCE, 1.1f * u, Offset(cc.x, cc.y - 6.6f * u))
        // La espada, tendida delante, sobre la tierra.
        drawLine(Color(0xFFB9BBBC), Offset(x - 16 * u, y - 0.5f * u), Offset(x + 11 * u, y - 2.5f * u), 1.5f * u, StrokeCap.Round)
        drawLine(TrazosGalia.MADERA, Offset(x + 11 * u, y - 2.5f * u), Offset(x + 16 * u, y - 3 * u), 1.6f * u, StrokeCap.Round)
        drawLine(TrazosGalia.BRONCE, Offset(x + 10.5f * u, y - 5 * u), Offset(x + 11.5f * u, y), 1.1f * u, StrokeCap.Round)
        // El torques, arriba, con su brillo.
        with(TrazosGalia) { torques(Offset(x, y - 34 * u), 7.5f * u, 2.2f * u) }
        if (oscuro) drawCircle(FUEGO.copy(alpha = 0.18f), 3 * u, Offset(x, y - 34 * u))
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Los objetos de Tu año de Galia: la luna, la creciente sobre la hoja de roble (los galos cuentan por noches); la huella, la
     * del casco del caballo, con su ranilla; la pesa, el lingote de hierro en doble pirámide con que se comerciaba el hierro.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val c = e.centro; val u = density
        val g = maxOf(r * 0.1f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                val luna = Path().apply {
                    addOval(Rect(Offset(c.x - r * 0.1f, c.y - r * 0.25f), r * 0.68f))
                }
                clipPath(Path().apply { addOval(Rect(Offset(c.x + r * 0.22f, c.y - r * 0.45f), r * 0.6f)) }, androidx.compose.ui.graphics.ClipOp.Difference) { drawPath(luna, e.color) }
                if (r >= 6f * u) with(TrazosGalia) { hojaRoble(Offset(c.x + r * 0.45f, c.y + r * 0.55f), r * 0.75f, 60f, e.color, e.fondo) }
            }
            "huella" -> {
                // El casco del caballo, de frente: la herradura de la pared, la ranilla en V y los talones.
                val casco = Path().apply {
                    moveTo(c.x - r * 0.62f, c.y + r * 0.85f)
                    cubicTo(c.x - r * 0.95f, c.y + r * 0.2f, c.x - r * 0.7f, c.y - r * 0.95f, c.x, c.y - r * 0.95f)
                    cubicTo(c.x + r * 0.7f, c.y - r * 0.95f, c.x + r * 0.95f, c.y + r * 0.2f, c.x + r * 0.62f, c.y + r * 0.85f)
                    lineTo(c.x + r * 0.32f, c.y + r * 0.85f)
                    cubicTo(c.x + r * 0.5f, c.y + r * 0.2f, c.x + r * 0.35f, c.y - r * 0.6f, c.x, c.y - r * 0.6f)
                    cubicTo(c.x - r * 0.35f, c.y - r * 0.6f, c.x - r * 0.5f, c.y + r * 0.2f, c.x - r * 0.32f, c.y + r * 0.85f); close()
                }
                drawPath(casco, e.color)
                drawPath(Path().apply { moveTo(c.x, c.y - r * 0.35f); lineTo(c.x + r * 0.25f, c.y + r * 0.7f); lineTo(c.x - r * 0.25f, c.y + r * 0.7f); close() }, e.color)
                if (r >= 6f * u) drawLine(e.fondo, Offset(c.x, c.y - r * 0.1f), Offset(c.x, c.y + r * 0.55f), g * 0.8f, StrokeCap.Round)
            }
            "pesa" -> {
                // El lingote de hierro en doble pirámide, tendido, con su arista de luz.
                val lingote = Path().apply {
                    moveTo(c.x - r * 1f, c.y + r * 0.05f); lineTo(c.x, c.y - r * 0.45f); lineTo(c.x + r * 1f, c.y - r * 0.05f); lineTo(c.x, c.y + r * 0.45f); close()
                }
                drawPath(lingote, e.color)
                drawLine(e.fondo, Offset(c.x, c.y - r * 0.42f), Offset(c.x, c.y + r * 0.42f), g * 0.8f)
                if (r >= 6f * u) drawLine(e.fondo.copy(alpha = 0.6f), Offset(c.x - r * 0.9f, c.y + r * 0.04f), Offset(c.x + r * 0.9f, c.y - r * 0.04f), g * 0.6f)
            }
            else -> {}
        }
    }
}
