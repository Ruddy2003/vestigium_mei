package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.sqrt

/**
 * Ciudad neón: los niveles bajos de una megaciudad, siempre de noche. Acero y ladrillo oscuros, vapor, lluvia y charcos,
 * y la luz de los letreros: el magenta del acento, el ámbar del secundario, el verde de sodio y el cian (la misma casa que
 * su puesto de fideos del Ideario y su caja de cerveza sobre el charco). Cada tubo de neón lleva su halo y su alma blanca.
 * - Social: la barra de un puesto de fideos: arriba, el dintel con la cortina noren y dos farolillos de papel; a los lados,
 *   letreros verticales con signos de neón; abajo, la barra de acero con su filo magenta, dos tazones humeantes, los
 *   taburetes y los charcos. El vapor, tenue, en el centro.
 * - Mapas: la mesa de un callejón con el mapa holográfico: las paredes de ladrillo con su tubería a los lados, los cables
 *   y un foco ámbar arriba, la rejilla cian tenue con las esquinas del cuadro proyectado, el canto de la mesa con franjas
 *   de peligro y el proyector al frente, y la lluvia en los márgenes.
 * - Otras vidas: una fila de cinco cápsulas de hotel, cada una iluminada por dentro con un color, con su persiana a medio
 *   bajar, su filo de luz y su placa de número; el techo con su tira magenta y el escalón con la ámbar.
 * - Íconos: un torii de neón entre las torres, la tarjeta holográfica que se rasca con el chip de crédito y balizas de
 *   neón sobre la rejilla del suelo.
 * - Notas: un pilar de concreto bajo un poste de luz, cubierto de restos de volantes, stickers y grapas; cada nota es un
 *   volante oscuro con su marco de tubo del color del tipo, la franja impresa y las grapas; la fijada lleva un sticker
 *   holográfico y brilla más; el recordatorio, un código de barras.
 */
internal object VidaNeon : DisenoVida {
    private val NOCHE = Color(0xFF120C17)
    private val ACERO = Color(0xFF2A2630)
    private val ACERO_LUZ = Color(0xFF4A4452)
    private val ACERO_OSC = Color(0xFF1A1620)
    private val SODIO = Color(0xFF8CFF5A)
    private val CIAN = Color(0xFF3DE0FF)
    private val AMBAR = Color(0xFFFFB238)
    private val LONA = Color(0xFF7A1E3A)
    private val FAROL = Color(0xFFE0443A)
    private val LADRILLO = Color(0xFF4A2A3A)
    private val CARCASA = Color(0xFF221C33)
    private val CARCASA_LUZ = Color(0xFF3B3354)
    private val ALAMBRE = Color(0xFFB9C1CE)
    private val LLUVIA = Color(0xFFB4CCFF)
    private val TICKET = Color(0xFFF2EEE6)
    private val LACA = Color(0xFF3A2420)
    /** El papel de los volantes: oscuro, para que la letra clara del tema se lea y el neón del marco destaque. */
    private val VOLANTE = Color(0xFF231A2E)
    private val TEXTO = Color(0xFFF4E7F0)

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un número estable de 0 a 1 (las irregularidades de cada cosa, siempre las mismas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Una mancha radial suave (el resplandor de una luz, el vapor). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) {
        val rr = r.coerceAtLeast(1f)
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, rr), rr, c)
    }

    /** Un tubo de neón: el halo ancho y tenue, el tubo del color y su alma blanca. */
    private fun DrawScope.neon(p: Path, color: Color, grosor: Float) {
        drawPath(p, color.copy(alpha = 0.18f), style = Stroke(grosor * 4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color.copy(alpha = 0.4f), style = Stroke(grosor * 2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, Color.White.copy(alpha = 0.7f), style = Stroke(grosor * 0.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** Un papel arrancado: el borde de arriba recto y los otros tres mordidos al azar hasta [hondo]. */
    private fun rasgado(r: Rect, semilla: Int, hondo: Float): Path = Path().apply {
        moveTo(r.left, r.top); lineTo(r.right, r.top)
        val n = 6
        for (i in 1..n) lineTo(r.right - hondo * azar(semilla, i), r.top + r.height * i / n)
        for (i in 1..n) lineTo(r.right - r.width * i / n, r.bottom - hondo * azar(semilla, i + 10))
        for (i in 1 until n) lineTo(r.left + hondo * azar(semilla, i + 20), r.bottom - r.height * i / n)
        close()
    }

    // ---------------------------------------------------------------- Social: el puesto de fideos

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        // El vapor del puesto, tenue en el centro.
        mancha(Offset(w * 0.5f, h * 0.55f), w * 0.42f, e.acento.copy(alpha = 0.07f))
        mancha(Offset(w * 0.32f, h * 0.4f), w * 0.25f, e.secundario.copy(alpha = 0.04f))
        // El dintel y la cortina noren, en paños con sus aberturas y el dobladillo magenta.
        drawRect(ACERO_OSC, Offset.Zero, Size(w, h * 0.035f))
        val panos = 7; val x0 = w * 0.14f; val ancho = w * 0.72f / panos
        for (i in 0 until panos) {
            val x = x0 + i * ancho
            drawRect(LONA, Offset(x + 0.5f * u, h * 0.035f), Size(ancho - 1f * u, h * 0.085f))
            drawLine(e.acento.copy(alpha = 0.55f), Offset(x + 0.5f * u, h * 0.118f), Offset(x + ancho - 0.5f * u, h * 0.118f), 0.4f * u)
        }
        drawCircle(TICKET.copy(alpha = 0.3f), ancho * 0.22f, Offset(w / 2, h * 0.075f), style = Stroke(0.6f * u))
        // Los farolillos de papel en las esquinas de arriba.
        farolillo(Offset(w * 0.09f, h * 0.2f), 4.6f * u)
        farolillo(Offset(w * 0.91f, h * 0.2f), 4.6f * u)
        // Los letreros verticales a los lados.
        letreroVertical(Rect(w * 0.012f, h * 0.34f, w * 0.062f, h * 0.76f), e.secundario, e.acento, e.semilla + 3)
        letreroVertical(Rect(w * 0.938f, h * 0.36f, w * 0.988f, h * 0.78f), SODIO, CIAN, e.semilla + 7)
        // La barra de acero, con su filo de neón, los tazones humeantes, los taburetes y los charcos.
        val barra = h * 0.8f
        val filo = barra + h * 0.05f
        drawRect(ACERO_LUZ, Offset(0f, barra), Size(w, filo - barra))
        drawLine(Color.White.copy(alpha = 0.18f), Offset(0f, barra), Offset(w, barra), 0.5f * u)
        drawRect(ACERO_OSC, Offset(0f, filo), Size(w, h - filo))
        var x = w / 6f
        while (x < w) { drawLine(Color.Black.copy(alpha = 0.4f), Offset(x, filo + u), Offset(x, h), 0.5f * u); x += w / 6f }
        neon(Path().apply { moveTo(0f, filo); lineTo(w, filo) }, e.acento, 0.6f * u)
        listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f).forEach { f -> taburete(Offset(w * f, h * 0.91f)) }
        tazon(Offset(w * 0.13f, barra), e)
        tazon(Offset(w * 0.86f, barra), e)
        drawOval(e.acento.copy(alpha = 0.14f), Offset(w * 0.18f, h * 0.965f), Size(w * 0.14f, h * 0.025f))
        drawOval(CIAN.copy(alpha = 0.1f), Offset(w * 0.6f, h * 0.97f), Size(w * 0.1f, h * 0.02f))
    }

    /** Un farolillo de papel rojo (chōchin) colgado de su cable, con su resplandor, sus aros y las tapas negras. */
    private fun DrawScope.farolillo(c: Offset, r: Float) {
        val u = size.minDimension / 100f
        drawLine(ACERO_LUZ, Offset(c.x, 0f), Offset(c.x, c.y - r * 1.2f), 0.4f * u)
        mancha(c, r * 3.2f, FAROL.copy(alpha = 0.3f))
        drawOval(FAROL, Offset(c.x - r, c.y - r * 1.2f), Size(r * 2, r * 2.4f))
        for (j in -2..2) {
            val f = j * 0.35f
            val y = c.y + j * r * 0.42f
            val medio = r * sqrt(1f - f * f)
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x - medio, y), Offset(c.x + medio, y), 0.35f * u)
        }
        drawOval(Color.White.copy(alpha = 0.25f), Offset(c.x - r * 0.6f, c.y - r * 0.8f), Size(r * 0.5f, r * 1.1f))
        drawRect(Color.Black, Offset(c.x - r * 0.5f, c.y - r * 1.35f), Size(r, r * 0.25f))
        drawRect(Color.Black, Offset(c.x - r * 0.5f, c.y + r * 1.1f), Size(r, r * 0.25f))
    }

    /** Un letrero vertical sobre su placa negra: el marco de tubo [tubo] y tres signos de trazos cortos en [glifo]. */
    private fun DrawScope.letreroVertical(r: Rect, tubo: Color, glifo: Color, semilla: Int) {
        val u = size.minDimension / 100f
        mancha(r.center, r.height * 0.7f, tubo.copy(alpha = 0.1f))
        drawRect(NOCHE, r.topLeft, r.size)
        neon(Path().apply { addRect(Rect(r.left + 0.8f * u, r.top + 0.8f * u, r.right - 0.8f * u, r.bottom - 0.8f * u)) }, tubo, 0.5f * u)
        val n = 3; val alto = (r.height - 4f * u) / n; val ancho = r.width - 3.6f * u
        for (i in 0 until n) {
            val o = Offset(r.left + 1.8f * u, r.top + 2f * u + i * alto)
            val p = Path()
            p.moveTo(o.x + ancho * 0.15f, o.y + alto * 0.22f); p.lineTo(o.x + ancho * 0.85f, o.y + alto * 0.22f)
            val vx = o.x + ancho * (0.3f + 0.4f * azar(semilla, i))
            p.moveTo(vx, o.y + alto * 0.22f); p.lineTo(vx, o.y + alto * 0.8f)
            if (azar(semilla, i + 10) > 0.5f) {
                p.moveTo(o.x + ancho * 0.2f, o.y + alto * 0.55f); p.lineTo(o.x + ancho * 0.8f, o.y + alto * 0.55f)
            } else {
                p.moveTo(o.x + ancho * 0.2f, o.y + alto * 0.75f); p.lineTo(o.x + ancho * 0.8f, o.y + alto * 0.45f)
            }
            neon(p, glifo, 0.45f * u)
        }
    }

    /** Un taburete con el asiento en [c]: el cojín rojo con su brillo y la pata hasta el suelo. */
    private fun DrawScope.taburete(c: Offset) {
        val u = size.minDimension / 100f
        val w = size.width
        drawLine(ACERO_LUZ, c, Offset(c.x, size.height), 0.9f * u)
        drawOval(LONA, Offset(c.x - w * 0.035f, c.y - 1.4f * u), Size(w * 0.07f, 2.8f * u))
        drawOval(Color.White.copy(alpha = 0.15f), Offset(c.x - w * 0.025f, c.y - 1.2f * u), Size(w * 0.05f, 1f * u))
    }

    /** Un tazón de fideos apoyado en [b], con su franja del acento, los palillos y el vapor que sube. */
    private fun DrawScope.tazon(b: Offset, e: EstadoMueble) {
        val u = size.minDimension / 100f
        val r = 4.5f * u
        for (j in 0 until 3) {
            val vx = b.x - r * 0.5f + j * r * 0.5f
            val p = Path().apply {
                moveTo(vx, b.y - r * 0.9f)
                quadraticTo(vx - r * 0.5f, b.y - r * 1.8f, vx, b.y - r * 2.6f)
                quadraticTo(vx + r * 0.5f, b.y - r * 3.4f, vx, b.y - r * 4.2f)
            }
            drawPath(p, Color.White.copy(alpha = 0.14f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        }
        drawLine(LACA, Offset(b.x + r * 0.2f, b.y - r * 0.9f), Offset(b.x + r * 1.5f, b.y - r * 2.5f), 0.6f * u, StrokeCap.Round)
        drawLine(LACA, Offset(b.x + r * 0.5f, b.y - r * 0.9f), Offset(b.x + r * 1.8f, b.y - r * 2.3f), 0.6f * u, StrokeCap.Round)
        val cuenco = Path().apply {
            moveTo(b.x - r * 1.2f, b.y - r * 0.9f); lineTo(b.x + r * 1.2f, b.y - r * 0.9f)
            quadraticTo(b.x + r * 1.1f, b.y, b.x, b.y)
            quadraticTo(b.x - r * 1.1f, b.y, b.x - r * 1.2f, b.y - r * 0.9f); close()
        }
        drawPath(cuenco, TICKET.copy(alpha = 0.85f))
        drawLine(e.acento, Offset(b.x - r * 1.0f, b.y - r * 0.55f), Offset(b.x + r * 1.0f, b.y - r * 0.55f), 0.5f * u)
    }

    // ---------------------------------------------------------------- Mapas: el holograma del callejón

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val mapa = Rect(w * 0.06f, h * 0.1f, w * 0.94f, h * 0.9f)
        // Las paredes del callejón a los lados.
        pared(Rect(0f, 0f, w * 0.05f, h), true, e)
        pared(Rect(w * 0.95f, 0f, w, h), false, e)
        // El holograma: una rejilla cian tenue y las esquinas del cuadro proyectado.
        drawRect(CIAN.copy(alpha = 0.035f), mapa.topLeft, mapa.size)
        val rej = CIAN.copy(alpha = 0.07f)
        for (i in 0..16) { val x = mapa.left + mapa.width * i / 16f; drawLine(rej, Offset(x, mapa.top), Offset(x, mapa.bottom), 0.4f * u) }
        for (j in 0..8) { val y = mapa.top + mapa.height * j / 8f; drawLine(rej, Offset(mapa.left, y), Offset(mapa.right, y), 0.4f * u) }
        val k = 5f * u
        listOf(
            mapa.topLeft to Offset(1f, 1f), Offset(mapa.right, mapa.top) to Offset(-1f, 1f),
            Offset(mapa.left, mapa.bottom) to Offset(1f, -1f), Offset(mapa.right, mapa.bottom) to Offset(-1f, -1f),
        ).forEach { (p, d) ->
            val q = Offset(p.x - d.x * 1.2f * u, p.y - d.y * 1.2f * u)
            neon(Path().apply { moveTo(q.x + d.x * k, q.y); lineTo(q.x, q.y); lineTo(q.x, q.y + d.y * k) }, CIAN, 0.45f * u)
        }
        // La mesa: el canto de acero con franjas de peligro a los lados, las patas y el proyector al frente.
        val canto = Rect(w * 0.05f, h * 0.905f, w * 0.95f, h * 0.95f)
        drawRect(ACERO, canto.topLeft, canto.size)
        drawLine(Color.White.copy(alpha = 0.15f), canto.topLeft, Offset(canto.right, canto.top), 0.5f * u)
        listOf(Rect(canto.left, canto.top, w * 0.32f, canto.bottom), Rect(w * 0.68f, canto.top, canto.right, canto.bottom)).forEach { z ->
            clipRect(z.left, z.top, z.right, z.bottom) {
                var sx = z.left - z.height; var j = 0
                while (sx < z.right && j < 60) {
                    drawPath(Path().apply {
                        moveTo(sx, z.bottom); lineTo(sx + z.height, z.top); lineTo(sx + z.height * 1.8f, z.top); lineTo(sx + z.height * 0.8f, z.bottom); close()
                    }, e.secundario.copy(alpha = 0.5f))
                    sx += z.height * 2f; j++
                }
            }
        }
        listOf(0.16f, 0.84f).forEach { f -> drawRect(ACERO_OSC, Offset(w * f - 1.4f * u, canto.bottom), Size(2.8f * u, h - canto.bottom)) }
        val pr = Rect(w * 0.42f, h * 0.912f, w * 0.58f, h * 0.943f)
        drawRect(Color.Black, pr.topLeft, pr.size)
        neon(Path().apply { moveTo(pr.left + u, pr.center.y); lineTo(pr.right - u, pr.center.y) }, CIAN, 0.5f * u)
        listOf(mapa.left, mapa.right).forEach { x -> drawLine(CIAN.copy(alpha = 0.2f), Offset(pr.center.x, pr.top), Offset(x, mapa.bottom), 0.5f * u) }
        // Los cables que cuelgan arriba, con un foco ámbar.
        listOf(
            Path().apply { moveTo(0f, h * 0.02f); quadraticTo(w * 0.5f, h * 0.13f, w, h * 0.03f) },
            Path().apply { moveTo(0f, h * 0.05f); quadraticTo(w * 0.4f, h * 0.1f, w, h * 0.012f) },
        ).forEach { p ->
            drawPath(p, Color.Black.copy(alpha = 0.9f), style = Stroke(1f * u))
            drawPath(p, ACERO_LUZ.copy(alpha = 0.6f), style = Stroke(0.35f * u))
        }
        val foco = Offset(w * 0.3f, h * 0.088f)
        drawLine(ACERO_LUZ, Offset(foco.x, h * 0.066f), foco, 0.4f * u)
        mancha(foco, 10f * u, e.secundario.copy(alpha = 0.25f))
        drawCircle(e.secundario, 1.3f * u, foco)
        // La lluvia, solo en los márgenes.
        val gota = LLUVIA.copy(alpha = 0.16f)
        for (j in 0 until 60) {
            val x = w * azar(e.semilla, j); val y = h * azar(e.semilla, j + 100)
            if (mapa.contains(Offset(x, y))) continue
            drawLine(gota, Offset(x, y), Offset(x - 1.2f * u, y + 4f * u), 0.35f * u)
        }
    }

    /** Una pared de ladrillo del callejón con sus juntas, una tubería con sus abrazaderas y el reflejo del neón. */
    private fun DrawScope.pared(r: Rect, izquierda: Boolean, e: EstadoMueble) {
        val u = size.minDimension / 100f
        drawRect(LADRILLO, r.topLeft, r.size)
        val alto = 2.6f * u; val largo = 6f * u
        var y = r.top; var f = 0
        while (y < r.bottom && f < 80) {
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(r.left, y), Offset(r.right, y), 0.35f * u)
            var x = r.left + (if (f % 2 == 0) 0f else largo / 2)
            while (x < r.right) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, y), Offset(x, y + alto), 0.35f * u); x += largo }
            y += alto; f++
        }
        val tx = r.left + r.width * (if (izquierda) 0.55f else 0.45f)
        drawLine(ACERO_LUZ, Offset(tx, r.top), Offset(tx, r.bottom), 1.4f * u)
        drawLine(Color.White.copy(alpha = 0.2f), Offset(tx - 0.4f * u, r.top), Offset(tx - 0.4f * u, r.bottom), 0.3f * u)
        listOf(0.3f, 0.7f).forEach { f2 -> drawRect(ACERO_OSC, Offset(tx - 1.4f * u, r.top + r.height * f2), Size(2.8f * u, 1.6f * u)) }
        mancha(Offset(if (izquierda) r.right else r.left, r.center.y), r.height * 0.5f, (if (izquierda) e.acento else CIAN).copy(alpha = 0.1f))
    }

    // ---------------------------------------------------------------- Otras vidas: el hotel cápsula

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        drawRect(mez(CARCASA, e.fondo, 0.3f), Offset.Zero, size)
        // El techo con su tira de neón magenta.
        drawRect(ACERO_OSC, Offset.Zero, Size(w, h * 0.07f))
        neon(Path().apply { moveTo(w * 0.03f, h * 0.07f); lineTo(w * 0.97f, h * 0.07f) }, e.acento, 0.5f * u)
        // El escalón de abajo, con su franja ámbar.
        drawRect(ACERO, Offset(0f, h * 0.88f), Size(w, h * 0.12f))
        drawLine(Color.White.copy(alpha = 0.12f), Offset(0f, h * 0.88f), Offset(w, h * 0.88f), 0.5f * u)
        neon(Path().apply { moveTo(w * 0.03f, h * 0.94f); lineTo(w * 0.97f, h * 0.94f) }, e.secundario, 0.35f * u)
        // Las cinco cápsulas, cada una con su luz.
        val luces = listOf(e.acento, e.secundario, CIAN, SODIO)
        val paso = w * 0.184f
        val cw = minOf(paso * 0.9f, h * 0.6f)
        for (i in 0 until 5) {
            val cx = w / 2 + (i - 2) * paso
            capsula(Rect(cx - cw / 2, h * 0.11f, cx + cw / 2, h * 0.85f), luces[(i + e.semilla).mod(4)])
        }
    }

    /** Una cápsula: la carcasa, la boca iluminada por dentro con su filo de luz, la persiana a medio bajar y la placa del número. */
    private fun DrawScope.capsula(r: Rect, luz: Color) {
        val u = size.minDimension / 100f
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(r.left + 0.6f * u, r.top + 1f * u), r.size, CornerRadius(r.width * 0.26f))
        drawRoundRect(CARCASA_LUZ, r.topLeft, r.size, CornerRadius(r.width * 0.26f))
        drawRoundRect(Color.White.copy(alpha = 0.08f), r.topLeft, r.size, CornerRadius(r.width * 0.26f), style = Stroke(0.6f * u))
        val boca = Rect(r.left + 2.2f * u, r.top + 5f * u, r.right - 2.2f * u, r.bottom - 2.4f * u)
        val rad = CornerRadius(boca.width * 0.22f)
        drawRoundRect(mez(NOCHE, luz, 0.1f), boca.topLeft, boca.size, rad)
        mancha(boca.center, minOf(boca.width, boca.height) * 0.55f, luz.copy(alpha = 0.18f))
        // La persiana, medio bajada.
        val pers = boca.height * 0.16f
        clipRect(boca.left, boca.top, boca.right, boca.top + pers) {
            drawRoundRect(ACERO_LUZ, boca.topLeft, boca.size, rad)
            var y = boca.top + 1.2f * u
            while (y < boca.top + pers) { drawLine(Color.Black.copy(alpha = 0.35f), Offset(boca.left, y), Offset(boca.right, y), 0.35f * u); y += 1.3f * u }
        }
        drawRoundRect(luz.copy(alpha = 0.15f), boca.topLeft, boca.size, rad, style = Stroke(2f * u))
        drawRoundRect(luz.copy(alpha = 0.55f), boca.topLeft, boca.size, rad, style = Stroke(0.5f * u))
        // La placa del número, con su piloto.
        val pl = Rect(r.center.x - r.width * 0.18f, r.top + 1.2f * u, r.center.x + r.width * 0.18f, r.top + 3.8f * u)
        drawRect(Color.Black, pl.topLeft, pl.size)
        drawCircle(luz, 0.7f * u, Offset(pl.left + 1.2f * u, pl.center.y))
        drawLine(ALAMBRE.copy(alpha = 0.5f), Offset(pl.left + 2.6f * u, pl.center.y), Offset(pl.right - 1f * u, pl.center.y), 0.5f * u)
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val c = Offset(size.width / 2, size.height / 2)
        val a = size.height * 0.4f
        val g = 1.6f * density
        when (id) {
            "unesco" -> torii(c, a, g, e)
            "rascar" -> tarjetaHolo(c, a, g, e)
            else -> balizas(c, a, g, e)
        }
    }

    /** Un torii de neón (el santuario que quedó entre las torres), con la ciudad y sus ventanas detrás y su reflejo. */
    private fun DrawScope.torii(c: Offset, a: Float, g: Float, e: EstadoIcono) {
        val base = c.y + a * 0.92f
        val alturas = listOf(0.9f, 1.5f, 1.1f, 1.8f, 0.7f, 1.3f, 1.6f, 1.0f)
        val ancho = a * 2.8f / alturas.size
        alturas.forEachIndexed { i, k ->
            val x = c.x - a * 1.4f + i * ancho
            drawRect(e.c2.copy(alpha = 0.18f), Offset(x + ancho * 0.08f, base - a * k), Size(ancho * 0.84f, a * k))
            drawRect(e.c2.copy(alpha = 0.45f), Offset(x + ancho * 0.35f, base - a * k + a * 0.15f), Size(ancho * 0.18f, ancho * 0.18f))
        }
        val p = Path().apply {
            moveTo(c.x - a * 0.62f, base); lineTo(c.x - a * 0.55f, c.y - a * 0.5f)
            moveTo(c.x + a * 0.62f, base); lineTo(c.x + a * 0.55f, c.y - a * 0.5f)
            moveTo(c.x - a * 0.92f, c.y - a * 0.38f); lineTo(c.x + a * 0.92f, c.y - a * 0.38f)
            moveTo(c.x, c.y - a * 0.38f); lineTo(c.x, c.y - a * 0.66f)
            moveTo(c.x - a * 0.95f, c.y - a * 0.66f); lineTo(c.x + a * 0.95f, c.y - a * 0.66f)
            moveTo(c.x - a * 1.2f, c.y - a * 0.92f); quadraticTo(c.x, c.y - a * 0.7f, c.x + a * 1.2f, c.y - a * 0.92f)
        }
        neon(p, e.c1, g)
        drawLine(e.c1.copy(alpha = 0.3f), Offset(c.x - a * 1.0f, base + g * 1.5f), Offset(c.x + a * 1.0f, base + g * 1.5f), g * 0.8f)
    }

    /** El mapa para rascar: la tarjeta holográfica con su capa rascada en zigzag y el chip de crédito que rasca. */
    private fun DrawScope.tarjetaHolo(c: Offset, a: Float, g: Float, e: EstadoIcono) {
        val tj = Rect(c.x - a * 1.35f, c.y - a * 0.82f, c.x + a * 1.2f, c.y + a * 0.85f)
        val rad = CornerRadius(a * 0.16f)
        drawRoundRect(e.c2.copy(alpha = 0.12f), tj.topLeft, tj.size, rad)
        val capa = Rect(tj.left + a * 0.18f, tj.top + a * 0.36f, tj.right - a * 0.18f, tj.bottom - a * 0.2f)
        drawRect(e.c2.copy(alpha = 0.5f), capa.topLeft, capa.size)
        clipRect(capa.left, capa.top, capa.right, capa.bottom) {
            val z = Path().apply {
                moveTo(capa.left + a * 0.1f, capa.bottom - a * 0.12f)
                lineTo(capa.left + a * 0.45f, capa.top + a * 0.12f)
                lineTo(capa.left + a * 0.8f, capa.bottom - a * 0.12f)
                lineTo(capa.left + a * 1.15f, capa.top + a * 0.14f)
            }
            drawPath(z, e.fondo, style = Stroke(a * 0.26f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(z, e.c1.copy(alpha = 0.85f), style = Stroke(a * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        neon(Path().apply { addRoundRect(RoundRect(tj, rad)) }, e.c1, g * 0.8f)
        val mc = Offset(capa.left + a * 1.4f, capa.top + a * 0.02f); val mr = a * 0.34f
        drawCircle(e.fondo, mr, mc)
        neon(Path().apply { addOval(Rect(mc.x - mr, mc.y - mr, mc.x + mr, mc.y + mr)) }, e.c2, g * 0.7f)
        drawRect(e.c2, Offset(mc.x - mr * 0.4f, mc.y - mr * 0.3f), Size(mr * 0.8f, mr * 0.6f), style = Stroke(g * 0.5f))
        drawLine(e.c2, Offset(mc.x, mc.y - mr * 0.3f), Offset(mc.x, mc.y + mr * 0.3f), g * 0.5f)
    }

    /** Tus lugares: tres balizas de neón sobre la rejilla del suelo en perspectiva. */
    private fun DrawScope.balizas(c: Offset, a: Float, g: Float, e: EstadoIcono) {
        for (k in 0 until 3) {
            val y = c.y + a * (0.45f + 0.22f * k)
            drawLine(e.c2.copy(alpha = 0.25f), Offset(c.x - a * 1.5f, y), Offset(c.x + a * 1.5f, y), g * 0.5f)
        }
        for (k in -3..3) drawLine(e.c2.copy(alpha = 0.2f), Offset(c.x + k * a * 0.25f, c.y + a * 0.45f), Offset(c.x + k * a * 0.5f, c.y + a * 0.95f), g * 0.5f)
        listOf(
            Triple(Offset(c.x - a * 0.95f, c.y + a * 0.62f), 0.85f, e.c1),
            Triple(Offset(c.x + a * 0.05f, c.y + a * 0.52f), 1.1f, e.c2),
            Triple(Offset(c.x + a * 1.0f, c.y + a * 0.74f), 0.8f, SODIO),
        ).forEach { (p, k, col) -> baliza(p, a * 0.36f * k, g, col) }
    }

    /** Una baliza: la gota de neón con la punta en [p], su luz en el suelo y el piloto dentro. */
    private fun DrawScope.baliza(p: Offset, s: Float, g: Float, col: Color) {
        mancha(p, s * 1.1f, col.copy(alpha = 0.35f))
        val gota = Path().apply {
            moveTo(p.x, p.y)
            cubicTo(p.x - s * 0.95f, p.y - s * 0.9f, p.x - s * 0.8f, p.y - s * 2f, p.x, p.y - s * 2f)
            cubicTo(p.x + s * 0.8f, p.y - s * 2f, p.x + s * 0.95f, p.y - s * 0.9f, p.x, p.y)
            close()
        }
        drawPath(gota, col.copy(alpha = 0.18f))
        neon(gota, col, g * 0.7f)
        drawCircle(col, s * 0.24f, Offset(p.x, p.y - s * 1.3f))
    }

    // ---------------------------------------------------------------- Notas: el pilar de los volantes

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        // El pilar de concreto, manchado de humedad, con la luz del farol y el reflejo de un letrero.
        drawRoundRect(mez(ACERO_OSC, e.fondo, 0.2f), Offset.Zero, size, CornerRadius(1.5f * u))
        for (k in 0 until 8) mancha(Offset(w * azar(e.semilla, k), h * azar(e.semilla, k + 20)), (10f + 18f * azar(e.semilla, k + 40)) * u, Color.Black.copy(alpha = 0.18f))
        mancha(Offset(w * 0.1f, h * 0.05f), w * 0.5f, e.secundario.copy(alpha = 0.12f))
        mancha(Offset(w * 0.95f, h * 0.95f), w * 0.35f, e.acento.copy(alpha = 0.1f))
        // Los restos de volantes arrancados.
        val colores = listOf(e.acento, e.secundario, CIAN, SODIO, TICKET)
        for (k in 0 until 14) {
            val cx = w * azar(e.semilla + 1, k); val cy = h * azar(e.semilla + 2, k)
            val ww = w * (0.07f + 0.1f * azar(e.semilla + 3, k)); val hh = h * (0.12f + 0.16f * azar(e.semilla + 4, k))
            rotate(-8f + 16f * azar(e.semilla + 5, k), Offset(cx, cy)) {
                drawPath(rasgado(Rect(cx - ww / 2, cy - hh / 2, cx + ww / 2, cy + hh / 2), e.semilla + k, 1.6f * u), colores[k % 5].copy(alpha = 0.09f))
            }
        }
        // Los stickers, con su brillo, y las grapas sueltas.
        for (k in 0 until 12) {
            val p = Offset(w * azar(e.semilla + 6, k), h * azar(e.semilla + 7, k))
            val s = (2.2f + 2.4f * azar(e.semilla + 8, k)) * u
            val col = colores[(k + 2) % 4]
            rotate(-20f + 40f * azar(e.semilla + 9, k), p) {
                if (k % 3 == 0) {
                    drawCircle(col.copy(alpha = 0.15f), s * 1.6f, p)
                    drawCircle(col.copy(alpha = 0.45f), s, p)
                    drawCircle(Color.White.copy(alpha = 0.2f), s, p, style = Stroke(0.4f * u))
                } else {
                    drawRoundRect(col.copy(alpha = 0.12f), Offset(p.x - s * 1.6f, p.y - s), Size(s * 3.2f, s * 2f), CornerRadius(s * 0.5f))
                    drawRoundRect(col.copy(alpha = 0.45f), Offset(p.x - s * 1.3f, p.y - s * 0.7f), Size(s * 2.6f, s * 1.4f), CornerRadius(s * 0.3f))
                    drawRoundRect(Color.White.copy(alpha = 0.2f), Offset(p.x - s * 1.3f, p.y - s * 0.7f), Size(s * 2.6f, s * 1.4f), CornerRadius(s * 0.3f), style = Stroke(0.4f * u))
                }
            }
        }
        for (k in 0 until 10) {
            val p = Offset(w * azar(e.semilla + 10, k), h * azar(e.semilla + 11, k))
            drawLine(ALAMBRE.copy(alpha = 0.45f), p, Offset(p.x + 1.6f * u, p.y + 0.2f * u), 0.4f * u)
        }
        // El poste de luz a la izquierda, con sus abrazaderas, el brazo y la lámpara ámbar.
        drawRect(ACERO_LUZ, Offset.Zero, Size(w * 0.025f, h))
        drawLine(Color.White.copy(alpha = 0.15f), Offset(w * 0.008f, 0f), Offset(w * 0.008f, h), 0.4f * u)
        listOf(0.3f, 0.62f).forEach { f -> drawRect(ACERO_OSC, Offset(0f, h * f), Size(w * 0.03f, 2f * u)) }
        drawLine(ACERO_LUZ, Offset(w * 0.02f, h * 0.03f), Offset(w * 0.1f, h * 0.03f), 1.2f * u)
        drawRoundRect(ACERO, Offset(w * 0.08f, h * 0.012f), Size(w * 0.05f, h * 0.035f), CornerRadius(u))
        neon(Path().apply { moveTo(w * 0.087f, h * 0.05f); lineTo(w * 0.123f, h * 0.05f) }, e.secundario, 0.6f * u)
        // La lluvia.
        val gota = LLUVIA.copy(alpha = 0.12f)
        for (k in 0 until 26) {
            val x = w * azar(e.semilla + 12, k); val y = h * azar(e.semilla + 13, k)
            drawLine(gota, Offset(x, y), Offset(x - 1.2f * u, y + 4f * u), 0.35f * u)
        }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 1f || r.height < 1f) return
        val k = minOf(r.width, r.height) / 100f
        val tubo = when (e.tipo) {
            "idea" -> e.acento
            "pensamiento" -> CIAN
            "proposito" -> SODIO
            "recordatorio" -> AMBAR
            else -> ALAMBRE
        }
        // El resplandor sobre el pilar y el volante, con la esquina de abajo arrancada.
        drawRect(tubo.copy(alpha = if (e.fijada) 0.2f else 0.1f), Offset(r.left - 1.5f * k, r.top - 1.5f * k), Size(r.width + 3f * k, r.height + 3f * k))
        val hoja = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 7f * k)
            lineTo(r.right - 7f * k, r.bottom); lineTo(r.left, r.bottom); close()
        }
        drawPath(hoja, VOLANTE)
        // La franja impresa arriba y el marco de tubo del color del tipo.
        drawRect(tubo.copy(alpha = 0.3f), Offset(r.left + 3f * k, r.top + 3f * k), Size(r.width - 6f * k, 8f * k))
        val borde = Path().apply {
            moveTo(r.left + 2f * k, r.top + 2f * k); lineTo(r.right - 2f * k, r.top + 2f * k); lineTo(r.right - 2f * k, r.bottom - 8f * k)
            lineTo(r.right - 8f * k, r.bottom - 2f * k); lineTo(r.left + 2f * k, r.bottom - 2f * k); close()
        }
        neon(borde, tubo, if (e.fijada) 0.9f * k else 0.6f * k)
        // Las grapas de arriba.
        listOf(r.left + 6f * k, r.right - 12f * k).forEach { x -> drawLine(ALAMBRE, Offset(x, r.top + 5f * k), Offset(x + 5f * k, r.top + 5f * k), 0.9f * k, StrokeCap.Round) }
        if (e.tipo == "recordatorio") {
            // Un código de barras en el margen de abajo.
            var x = r.left + 6f * k; var j = 0
            while (j < 9) {
                val g = (0.6f + 1.2f * azar(e.indice + 5, j)) * k
                drawLine(ALAMBRE.copy(alpha = 0.6f), Offset(x, r.bottom - 11f * k), Offset(x, r.bottom - 5f * k), g)
                x += g + 1.2f * k; j++
            }
        }
        if (e.fijada) {
            // El sticker holográfico en la esquina, mitad cian y mitad magenta, con su borde blanco.
            val c = Offset(r.right - 8f * k, r.top + 9f * k); val rr = 5f * k
            drawCircle(CIAN.copy(alpha = 0.2f), rr * 1.6f, c)
            drawArc(CIAN, 135f, 180f, true, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2))
            drawArc(e.acento, -45f, 180f, true, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2))
            drawCircle(Color.White.copy(alpha = 0.85f), rr, c, style = Stroke(0.8f * k))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TEXTO
}
