package io.github.ruddy2003.vestigium.ui.biblioteca

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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * La luz que le toca a una pieza del estante de Navidad: la del momento ([k]), la mancha de la ventana si cae en ella ([lit] con
 * [m]) y, en los Sueños oscuros, la luna ([luna]). [hacia] dice de qué lado le llega (1, de la derecha; -1, de la izquierda).
 */
internal class LuzNavidad(val a: AireNavidad, val k: Float, val lit: Float = 0f, val m: ManchaNavidad? = null, val luna: Float = 0f, val hacia: Float = -1f) {
    /** El color de [col] con esa luz ([f]: más claro si es positivo, más hondo si es negativo). */
    fun c(col: Color, f: Float = 0f): Color {
        val b = a.en(col, f, k, lit, m)
        return if (luna > 0f) a.sol(col, b, luna * 0.75f) else b
    }
    fun sombra(x: Float) = a.sombra(x)
    fun claro(x: Float) = if (lit > 0f && !a.oscuro) a.claro((x * 1.35f).coerceAtMost(1f)) else if (a.oscuro) a.brillo(x * 0.8f, k) else a.claro(x)
    fun brillo(x: Float) = if (a.oscuro) a.brillo(x, k) else a.claro(x)
}

/** La llama de una vela en [p] (su centro), de alto [s]: el halo, la gota amarilla, el corazón claro. [fuerza] la baja en los Sueños. */
internal fun DrawScope.llamaNavidad(p: Offset, s: Float, fuerza: Float = 1f) {
    val P = PaletaNavidad
    mancha(p, s * 1.6f, P.LLAMA.copy(alpha = 0.5f * fuerza))
    val f = Path().apply {
        moveTo(p.x, p.y - s * 0.78f)
        cubicTo(p.x + s * 0.32f, p.y - s * 0.2f, p.x + s * 0.28f, p.y + s * 0.32f, p.x, p.y + s * 0.34f)
        cubicTo(p.x - s * 0.28f, p.y + s * 0.32f, p.x - s * 0.32f, p.y - s * 0.2f, p.x, p.y - s * 0.78f)
        close()
    }
    drawPath(f, mezcla(P.LLAMA, P.LUMBRE, 0.25f).copy(alpha = 0.55f + 0.45f * fuerza))
    drawPath(corridoNavidad(f, 0f, s * 0.08f), P.LLAMA.copy(alpha = 0.7f * fuerza))
    drawOval(P.FUEGO, Offset(p.x - s * 0.1f, p.y - s * 0.12f), Size(s * 0.2f, s * 0.4f))
    drawOval(Color(0xFF6A8CC8).copy(alpha = 0.6f), Offset(p.x - s * 0.08f, p.y + s * 0.2f), Size(s * 0.16f, s * 0.12f))
}

/**
 * La palmatoria de latón (la del Ideario): el platillo con su asa de anillo, el cañón y la vela de cera crema consumida, con sus
 * chorreras y el pabilo. [pie] es el centro del platillo en la tabla; [rx], su radio; [alto], lo que mide la vela. Devuelve dónde
 * arde la llama (la pinta quien la llama, al final, si está encendida).
 */
internal fun DrawScope.palmatoriaNavidad(pie: Offset, rx: Float, alto: Float, l: LuzNavidad, semilla: Int): Offset {
    val P = PaletaNavidad
    val u = density
    val ry = rx * 0.24f
    val velaW = rx * 0.42f
    val canonA = rx * 0.32f
    val arriba = pie.y - ry * 1.6f - canonA - alto
    val laton = l.c(P.LATON)
    // Las sombras en lo de detrás (a la derecha de día) y en la tabla.
    drawRect(l.sombra(if (l.a.oscuro) 0.3f else 0.18f), Offset(pie.x - velaW / 2 + rx * 0.5f * (-l.hacia), arriba + rx * 0.3f), Size(velaW, pie.y - arriba - rx * 0.3f))
    drawOval(l.sombra(0.4f), Offset(pie.x - rx * 0.9f, pie.y - ry * 0.4f), Size(rx * 2.2f, ry * 1.8f))
    // El asa de anillo, a la izquierda.
    drawCircle(laton, rx * 0.28f, Offset(pie.x - rx * 1.05f, pie.y - ry * 1.4f), style = Stroke(rx * 0.1f))
    // El platillo: el borde vuelto, el fondo y el cañón.
    drawOval(l.c(mezcla(P.LATON, P.PINO_OSC, 0.4f)), Offset(pie.x - rx, pie.y - ry * 2f), Size(rx * 2, ry * 2.2f))
    drawOval(laton, Offset(pie.x - rx, pie.y - ry * 2.6f), Size(rx * 2, ry * 2f))
    drawOval(l.c(mezcla(P.LATON, P.PINO_OSC, 0.22f)), Offset(pie.x - rx * 0.8f, pie.y - ry * 2.3f), Size(rx * 1.6f, ry * 1.4f))
    drawArc(l.brillo(0.75f), 190f, 70f, false, Offset(pie.x - rx, pie.y - ry * 2.6f), Size(rx * 2, ry * 2f), style = Stroke(1.2f * u))
    val canon = Rect(pie.x - velaW * 0.75f, pie.y - ry * 1.6f - canonA, pie.x + velaW * 0.75f, pie.y - ry * 1.4f)
    drawRect(laton, canon.topLeft, canon.size)
    drawRect(l.brillo(0.5f), canon.topLeft, Size(canon.width * 0.25f, canon.height))
    drawRect(l.sombra(0.3f), Offset(canon.right - canon.width * 0.3f, canon.top), Size(canon.width * 0.3f, canon.height))
    drawRect(l.c(P.LATON_OSC), Offset(canon.left - rx * 0.06f, canon.top - rx * 0.05f), Size(canon.width + rx * 0.12f, rx * 0.08f))
    // La vela: cera crema, con su cabeza hundida y una chorrera.
    val vela = Path().apply {
        moveTo(pie.x - velaW / 2, canon.top)
        lineTo(pie.x - velaW / 2, arriba + rx * 0.05f)
        cubicTo(pie.x - velaW * 0.2f, arriba - rx * 0.02f, pie.x + velaW * 0.2f, arriba + rx * 0.06f, pie.x + velaW / 2, arriba)
        lineTo(pie.x + velaW / 2, canon.top); close()
    }
    drawPath(vela, l.c(mezcla(P.PAPEL, P.LATON, 0.12f)))
    val lado = if (l.hacia < 0f) 1f else -1f
    drawRect(l.sombra(0.22f), Offset(if (lado > 0f) pie.x + velaW * 0.12f else pie.x - velaW / 2, arriba), Size(velaW * 0.38f, canon.top - arriba))
    drawRect(l.claro(0.4f), Offset(if (lado > 0f) pie.x - velaW / 2 else pie.x + velaW * 0.25f, arriba), Size(velaW * 0.22f, canon.top - arriba))
    val chx = pie.x + velaW * (if (azar(semilla, 1) > 0.5f) -0.3f else 0.18f)
    drawPath(Path().apply {
        moveTo(chx - velaW * 0.12f, arriba + rx * 0.03f); lineTo(chx + velaW * 0.12f, arriba + rx * 0.03f)
        cubicTo(chx + velaW * 0.14f, arriba + alto * 0.3f, chx + velaW * 0.12f, arriba + alto * 0.42f, chx, arriba + alto * 0.42f)
        cubicTo(chx - velaW * 0.12f, arriba + alto * 0.42f, chx - velaW * 0.14f, arriba + alto * 0.3f, chx - velaW * 0.12f, arriba + rx * 0.03f); close()
    }, l.c(mezcla(P.PAPEL, Color.White, 0.3f), 0.05f))
    // El pabilo.
    drawLine(l.c(P.TINTA), Offset(pie.x, arriba + rx * 0.02f), Offset(pie.x + rx * 0.04f, arriba - rx * 0.2f), 1.2f * u)
    return Offset(pie.x + rx * 0.03f, arriba - rx * 0.42f)
}

/** Una ramita de abeto de [a] a [b]: el tallo y las agujas en dos verdes, las de la luz más claras. */
internal fun DrawScope.ramaAbetoNavidad(a0: Offset, b0: Offset, l: LuzNavidad, ancho: Float) {
    val P = PaletaNavidad
    val u = density
    val oscuras = Path(); val claras = Path()
    val n = 9
    val dx = b0.x - a0.x; val dy = b0.y - a0.y
    val largo = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
    val ux = dx / largo; val uy = dy / largo
    for (i in 1..n) {
        val t = i / (n + 0.5f)
        val p = Offset(a0.x + dx * t, a0.y + dy * t)
        val la = ancho * (1f - t * 0.55f)
        for (s in listOf(-1f, 1f)) {
            val q = Offset(p.x + (ux * 0.45f - s * uy) * la, p.y + (uy * 0.45f + s * ux) * la)
            val luz = (s * -uy) + (s * ux) * 0.3f < 0f
            (if (luz) claras else oscuras).apply { moveTo(p.x, p.y); lineTo(q.x, q.y) }
        }
    }
    drawLine(l.c(P.PINO_OSC), a0, b0, 1.2f * u)
    drawPath(oscuras, l.c(P.ABETO_OSC), style = Stroke(1.5f * u, cap = StrokeCap.Round))
    drawPath(claras, l.c(P.ABETO_LUZ), style = Stroke(1.4f * u, cap = StrokeCap.Round))
}

/** Una ramita de acebo en [c] de tamaño [s], girada [giro]: tres hojas con sus picos y su nervio, y tres bayas con su brillo. */
internal fun DrawScope.aceboNavidad(c: Offset, s: Float, giro: Float, l: LuzNavidad) {
    val P = PaletaNavidad
    val u = density
    rotate(giro, c) {
        listOf(-150f, -30f, 95f).forEachIndexed { i, ang ->
            val an = Math.toRadians(ang.toDouble()).toFloat()
            val d = Offset(cos(an), sin(an)); val nrm = Offset(-d.y, d.x)
            val hoja = Path().apply {
                moveTo(c.x, c.y)
                for (j in 1..4) {
                    val t = j / 4.6f
                    val ancho = s * 0.2f * sin(t * 3.1416f) + s * 0.05f
                    val p = c + d * (s * t)
                    lineTo(p.x + nrm.x * ancho * (if (j % 2 == 0) 0.75f else 1.15f), p.y + nrm.y * ancho * (if (j % 2 == 0) 0.75f else 1.15f))
                }
                val punta = c + d * (s * 0.95f)
                lineTo(punta.x, punta.y)
                for (j in 4 downTo 1) {
                    val t = j / 4.6f
                    val ancho = s * 0.2f * sin(t * 3.1416f) + s * 0.05f
                    val p = c + d * (s * t)
                    lineTo(p.x - nrm.x * ancho * (if (j % 2 == 0) 0.75f else 1.15f), p.y - nrm.y * ancho * (if (j % 2 == 0) 0.75f else 1.15f))
                }
                close()
            }
            drawPath(hoja, l.c(if (i == 1) P.ABETO else P.ABETO_OSC))
            drawLine(l.c(P.ABETO_LUZ), c, c + d * (s * 0.8f), 0.7f * u)
            drawPath(Path().apply { op(hoja, corridoNavidad(hoja, -s * 0.06f, -s * 0.06f), PathOperation.Difference) }, l.claro(0.28f))
        }
        listOf(Offset(-0.08f, 0.02f), Offset(0.08f, 0.04f), Offset(0f, 0.15f)).forEach { o ->
            val q = Offset(c.x + o.x * s, c.y + o.y * s)
            drawCircle(l.c(P.VIDRIO_ROJO), s * 0.1f, q)
            drawCircle(l.sombra(0.3f), s * 0.1f, q + Offset(s * 0.03f, s * 0.03f), style = Stroke(s * 0.04f))
            drawCircle(l.brillo(0.75f), s * 0.03f, q - Offset(s * 0.03f, s * 0.03f))
        }
    }
}

/** Una naranja con clavos de [r] en [c]: la piel con su grano, los clavos en hileras, la sombra y el brillo. */
internal fun DrawScope.naranjaNavidad(c: Offset, r: Float, l: LuzNavidad, semilla: Int) {
    val P = PaletaNavidad
    drawOval(l.sombra(0.4f), Offset(c.x - r * 0.7f, c.y + r * 0.55f), Size(r * 2f, r * 0.6f))
    val f = Path().apply { addOval(Rect(c, r)) }
    drawPath(f, l.c(P.NARANJA))
    clipPath(f) {
        for (fila in -2..2) for (j in -3..3) {
            val an = j * 0.42f + azar(semilla, fila + 3) * 0.2f
            val x = c.x + r * sin(an) * 0.95f
            val y = c.y + fila * r * 0.36f
            if ((Offset(x, y) - c).getDistance() < r * 0.86f) drawCircle(l.c(P.PINO_OSC, -0.2f), r * 0.07f, Offset(x, y))
        }
        drawCircle(l.sombra(0.28f), r, c + Offset(-l.hacia * r * 0.45f, r * 0.3f), style = Stroke(r * 0.5f))
    }
    drawCircle(l.claro(0.55f), r * 0.18f, c + Offset(l.hacia * r * 0.38f, -r * 0.4f))
}

/**
 * Una piña de [rx] por [ry] en [c], girada [giro]: un huevo más ancho abajo, con las escamas abiertas en hileras alternas (cada una
 * con su punta clara y su hueco hondo debajo) y el rabito arriba.
 */
internal fun DrawScope.pinaNavidad(c: Offset, rx: Float, ry: Float, giro: Float, l: LuzNavidad) {
    val P = PaletaNavidad
    val u = density
    rotate(giro, c) {
        drawOval(l.sombra(0.35f), Offset(c.x - rx * 0.8f, c.y + ry * 0.7f), Size(rx * 2f, ry * 0.5f))
        val huevo = Path().apply {
            moveTo(c.x, c.y - ry)
            cubicTo(c.x + rx * 0.7f, c.y - ry, c.x + rx * 1.05f, c.y + ry * 0.2f, c.x + rx * 0.7f, c.y + ry * 0.75f)
            quadraticTo(c.x, c.y + ry * 1.12f, c.x - rx * 0.7f, c.y + ry * 0.75f)
            cubicTo(c.x - rx * 1.05f, c.y + ry * 0.2f, c.x - rx * 0.7f, c.y - ry, c.x, c.y - ry)
            close()
        }
        drawPath(huevo, l.c(mezcla(P.CORTEZA, P.TINTA, 0.35f)))
        clipPath(huevo) {
            val escamas = Path(); val puntas = Path()
            val filas = 6
            for (fy in 0 until filas) {
                val y = c.y - ry * 0.8f + ry * 1.75f * fy / (filas - 1f)
                val ancho = rx * (0.55f + 0.45f * kotlin.math.sin(3.1416f * (fy + 1f) / (filas + 1f)))
                val n = 3 + (fy % 2)
                for (fx in 0 until n) {
                    val x = c.x - ancho + 2f * ancho * (fx + 0.5f) / n
                    val e = Rect(x - rx * 0.3f, y - ry * 0.14f, x + rx * 0.3f, y + ry * 0.2f)
                    escamas.addOval(e)
                    puntas.moveTo(e.left + e.width * 0.25f, e.bottom - e.height * 0.25f); puntas.lineTo(e.right - e.width * 0.25f, e.bottom - e.height * 0.25f)
                }
            }
            drawPath(escamas, l.c(P.PINO, -0.08f))
            drawPath(escamas, l.c(P.CORTEZA, -0.2f), style = Stroke(0.8f * u))
            drawPath(puntas, l.claro(0.45f), style = Stroke(1f * u, cap = StrokeCap.Round))
            drawRect(l.sombra(0.28f), Offset(c.x + rx * 0.25f * (-l.hacia) - rx * 0.4f, c.y - ry), Size(rx * 0.9f, ry * 2.2f))
        }
        drawLine(l.c(P.CORTEZA), Offset(c.x, c.y - ry), Offset(c.x + rx * 0.15f, c.y - ry * 1.2f), 1.4f * u, StrokeCap.Round)
    }
}

/** Una esfera de vidrio de [r] en [c], de [color]: el cuerpo, su reflejo de ventana, la sombra, el casquillo y el aro. */
internal fun DrawScope.esferaNavidad(c: Offset, r: Float, color: Color, l: LuzNavidad, conAro: Boolean = true) {
    val P = PaletaNavidad
    val u = density
    val cuerpo = l.c(color)
    drawCircle(cuerpo, r, c)
    // El lado de la sombra, una media luna honda; el reflejo de la ventana (un rectangulito curvo) del lado de la luz.
    drawCircle(l.c(mezcla(color, P.TINTA, 0.45f)), r * 0.82f, c + Offset(-l.hacia * r * 0.3f, r * 0.24f), style = Stroke(r * 0.36f))
    drawCircle(cuerpo.copy(alpha = 0.45f), r * 0.6f, c + Offset(l.hacia * r * 0.12f, -r * 0.1f))
    val rx = c.x + l.hacia * r * 0.38f
    drawRect(l.brillo(0.85f), Offset(rx - r * 0.12f, c.y - r * 0.5f), Size(r * 0.22f, r * 0.3f))
    drawCircle(l.brillo(0.5f), r * 0.07f, Offset(rx - l.hacia * r * 0.05f, c.y + r * 0.05f))
    if (conAro) {
        val casq = Rect(c.x - r * 0.24f, c.y - r * 1.12f, c.x + r * 0.24f, c.y - r * 0.86f)
        drawRect(l.c(P.LATON), casq.topLeft, casq.size)
        drawRect(l.sombra(0.3f), Offset(casq.left + casq.width * 0.6f, casq.top), Size(casq.width * 0.4f, casq.height))
        drawArc(l.c(P.LATON_OSC), 180f, 180f, false, Offset(c.x - r * 0.12f, casq.top - r * 0.2f), Size(r * 0.24f, r * 0.4f), style = Stroke(0.9f * u))
    }
}

/** Un lazo de cinta con su nudo en [n], de ancho [s], de [color]: las dos orejas con su vuelta en sombra y las dos colas. */
internal fun DrawScope.lazoNavidad(n: Offset, s: Float, color: Color, l: LuzNavidad, colas: Float = 0.5f) {
    val P = PaletaNavidad
    val cara = l.c(color)
    val hondo = l.c(mezcla(color, P.TINTA, 0.4f))
    for (lado in listOf(-1f, 1f)) {
        val cola = Path().apply {
            moveTo(n.x - s * 0.04f * lado, n.y)
            lineTo(n.x + lado * s * 0.2f, n.y + s * colas)
            lineTo(n.x + lado * s * 0.08f, n.y + s * colas * 0.88f)
            lineTo(n.x + lado * s * 0.02f, n.y + s * colas * 0.98f)
            lineTo(n.x + lado * s * 0.05f, n.y); close()
        }
        drawPath(cola, if (lado < 0f) cara else hondo)
        val oreja = Path().apply {
            moveTo(n.x, n.y)
            cubicTo(n.x + lado * s * 0.2f, n.y - s * 0.36f, n.x + lado * s * 0.56f, n.y - s * 0.24f, n.x + lado * s * 0.46f, n.y + s * 0.06f)
            close()
        }
        drawPath(oreja, cara)
        drawPath(Path().apply { op(oreja, corridoNavidad(oreja, lado * s * 0.06f, -s * 0.06f), PathOperation.Difference) }, l.sombra(0.32f))
        drawPath(Path().apply { op(oreja, corridoNavidad(oreja, -lado * s * 0.04f, s * 0.04f), PathOperation.Difference) }, l.claro(0.3f))
    }
    drawOval(hondo, Offset(n.x - s * 0.1f, n.y - s * 0.08f), Size(s * 0.2f, s * 0.17f))
    drawOval(l.claro(0.35f), Offset(n.x - s * 0.06f, n.y - s * 0.06f), Size(s * 0.08f, s * 0.05f))
}

/** Un ovillo de lana de [r] en [c], de [color]: la bola, las vueltas del hilo cruzadas, su sombra y el cabo suelto. */
internal fun DrawScope.ovilloNavidad(c: Offset, r: Float, color: Color, l: LuzNavidad, semilla: Int) {
    val P = PaletaNavidad
    val u = density
    drawOval(l.sombra(0.42f), Offset(c.x - r * 0.8f, c.y + r * 0.6f), Size(r * 2.1f, r * 0.6f))
    val bola = Path().apply { addOval(Rect(c, r)) }
    drawPath(bola, l.c(color))
    clipPath(bola) {
        val hilos = Path()
        for (j in 0 until 7) {
            val an = (0.5f + j * 0.42f + azar(semilla, j) * 0.2f)
            val e = r * (0.35f + 0.65f * azar(semilla, j + 10))
            hilos.addOval(Rect(c.x - r * 1.05f, c.y - e, c.x + r * 1.05f, c.y + e))
            if (j % 2 == 0) hilos.addOval(Rect(c.x - e * cos(an), c.y - r * 1.05f, c.x + e * cos(an), c.y + r * 1.05f))
        }
        drawPath(hilos, l.c(mezcla(color, P.TINTA, 0.35f)).copy(alpha = 0.55f), style = Stroke(0.9f * u))
        drawPath(corridoNavidad(hilos, -0.8f * u, -0.8f * u), l.claro(0.25f), style = Stroke(0.6f * u))
        drawCircle(l.sombra(0.3f), r, c + Offset(-l.hacia * r * 0.5f, r * 0.35f), style = Stroke(r * 0.55f))
    }
    // El cabo suelto, que cae a la tabla.
    val cabo = Path().apply {
        moveTo(c.x + r * 0.7f, c.y + r * 0.5f)
        cubicTo(c.x + r * 1.2f, c.y + r * 0.9f, c.x + r * 1.5f, c.y + r * 0.7f, c.x + r * 1.9f, c.y + r * 1f)
    }
    drawPath(cabo, l.c(color), style = Stroke(1.1f * u, cap = StrokeCap.Round))
}

/** Una tarjeta de felicitación (las de las fichas del Ideario) en [r], girada [giro]: la cartulina, su filete y la ramita de acebo. */
internal fun DrawScope.tarjetaNavidad(r: Rect, giro: Float, filete: Color, l: LuzNavidad) {
    val P = PaletaNavidad
    val u = density
    rotate(giro, Offset(r.center.x, r.bottom)) {
        drawRect(l.sombra(if (l.a.oscuro) 0.35f else 0.22f), r.topLeft + Offset(-l.hacia * r.width * 0.12f, r.height * 0.06f), r.size)
        drawRect(l.c(P.PAPEL), r.topLeft, r.size)
        val m = r.width * 0.09f
        drawRect(l.c(filete), Offset(r.left + m, r.top + m), Size(r.width - 2 * m, r.height - 2 * m), style = Stroke(1.1f * u))
        aceboNavidad(Offset(r.center.x, r.top + r.height * 0.46f), r.width * 0.32f, 10f, l)
        drawRect(l.sombra(0.14f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
        drawRect(l.claro(0.35f), r.topLeft, Size(r.width, 1f * u))
    }
}
