package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La ventana honda de la pared de la izquierda del estante de Tierras Antiguas ([MuebleEstanteAntiguas]), vista en escorzo
 * arriba a la izquierda: el hueco de arco de medio punto en el muro, la jamba del fondo y el intradós del arco (el grueso de la
 * pared), el bastidor de roble, los vidrios en rombo emplomados con sus dos barras de hierro. Todo en anchos del mueble; el muro
 * del costado va de 0 a [MuebleEstanteAntiguas.ESQUINA] y se fuga hacia ([MuebleEstanteAntiguas.FUGA_X], [MuebleEstanteAntiguas.FUGA_Y]).
 */
internal object VentanaAntiguas {
    /** Los bordes del hueco en el muro del costado, en la pantalla. */
    const val XA = 0.014f
    const val XB = 0.114f
    /** La altura del arranque del arco, la flecha y el alféizar, medidas en la esquina (la pared del fondo). */
    const val ARRANQUE = 0.205f
    const val FLECHA = 0.07f
    const val ALFEIZAR = 0.43f
    /** Lo que se ve de la jamba del fondo (el grueso del muro), en la pantalla. */
    const val JAMBA = 0.024f
    /** Cuántos rombos de vidrio hay a lo ancho y a lo alto. */
    const val COLUMNAS = 3
    const val FILAS = 8

    /** Cuánto crece lo que está más cerca: 1 en la esquina, más hacia el borde izquierdo. */
    fun fuga(x: Float): Float {
        val m = MuebleEstanteAntiguas
        return (m.FUGA_X - x) / (m.FUGA_X - m.ESQUINA)
    }

    /** La y en la pantalla de un punto del muro del costado que, en la esquina, estaría a la altura [yb]. */
    fun y(x: Float, yb: Float): Float {
        val m = MuebleEstanteAntiguas
        return m.FUGA_Y + (yb - m.FUGA_Y) * fuga(x)
    }

    /** Un punto del hueco: [d] de 0 (borde cercano) a 1 (la jamba del fondo) y la altura [yb] medida en la esquina. */
    fun punto(d: Float, yb: Float, x0: Float = XA, x1: Float = XB): Offset {
        val x = x0 + (x1 - x0) * d
        return Offset(x, y(x, yb))
    }

    /** La altura (medida en la esquina) del arco en [d], de 0 a 1 a lo ancho del hueco. */
    fun arco(d: Float): Float {
        val t = (2f * d - 1f).coerceIn(-1f, 1f)
        return ARRANQUE - FLECHA * sqrt(1f - t * t)
    }

    /** El contorno del hueco (o del vidrio, entre [x0] y [x1]) en píxeles de un lienzo de ancho [w]. */
    fun contorno(w: Float, x0: Float = XA, x1: Float = XB, abajo: Float = ALFEIZAR): Path = Path().apply {
        val pasos = 14
        val p0 = punto(0f, abajo, x0, x1)
        moveTo(p0.x * w, p0.y * w)
        for (i in 0..pasos) {
            val d = i / pasos.toFloat()
            val p = punto(d, arco(d), x0, x1)
            lineTo(p.x * w, p.y * w)
        }
        val p1 = punto(1f, abajo, x0, x1)
        lineTo(p1.x * w, p1.y * w)
        close()
    }
}

/**
 * El paño de luz que la ventana echa en la pared del fondo: el sol de día, el ocaso de los Sueños en claro y la luna de los
 * Sueños en oscuro. Es la ventana proyectada: lados verticales (las jambas) entre [x0] y [x0] + [ancho], de [alto] desde [arriba],
 * que bajan [pendiente] por cada ancho hacia la derecha, con el arco arriba ([flecha]); dentro, un rombo de luz por cada vidrio,
 * separados por la sombra de los plomos. Lo que está delante de la pared lo recibe corrido ([FRENTE] por cada plano de hondo):
 * así el paño se quiebra en la cornisa, en las tablas y en lo de encima. No vela: aclara y tiñe con mezcla de pantalla, solo
 * donde cae.
 */
internal class PanoAntiguas(
    val x0: Float, val ancho: Float, val arriba: Float, val alto: Float, val pendiente: Float, val flecha: Float,
    val color: Color, val fuerza: Float, val tinte: Float = 0f,
) {
    private fun dentro(x: Float, y: Float, plano: Float): Float? {
        val px = x - FRENTE_X * plano - x0; val py = y - FRENTE_Y * plano
        val d = px / ancho
        if (d < 0f || d > 1f) return null
        val v = (py - arriba - pendiente * px) / alto
        val t = 2f * d - 1f
        val tope = flecha / alto * (1f - sqrt((1f - t * t).coerceAtLeast(0f)))
        if (v < tope || v > 1f) return null
        return v
    }

    /** Cuánto le toca a ([x], [y]) (en anchos) en el plano [plano]: de 1 arriba a 0.45 abajo; 0 fuera. */
    fun en(x: Float, y: Float, plano: Float = 0f): Float {
        val v = dentro(x, y, plano) ?: return 0f
        return 1f - 0.55f * v
    }

    /** Un punto del paño en píxeles: [d] a lo ancho y [v] a lo alto (de 0 a 1), en el plano [plano]. */
    private fun p(w: Float, d: Float, v: Float, plano: Float): Offset {
        val x = x0 + ancho * d
        return Offset((x + FRENTE_X * plano) * w, (arriba + alto * v + pendiente * ancho * d + FRENTE_Y * plano) * w)
    }

    /** El contorno del paño, con el arco arriba. */
    fun forma(w: Float, plano: Float): Path = Path().apply {
        val pasos = 12
        val a0 = p(w, 0f, 1f, plano)
        moveTo(a0.x, a0.y)
        for (i in 0..pasos) {
            val d = i / pasos.toFloat()
            val t = 2f * d - 1f
            val q = p(w, d, flecha / alto * (1f - sqrt((1f - t * t).coerceAtLeast(0f))), plano)
            lineTo(q.x, q.y)
        }
        val a1 = p(w, 1f, 1f, plano)
        lineTo(a1.x, a1.y)
        close()
    }

    /** Los rombos de luz, uno por vidrio, separados por la sombra de los plomos. */
    fun rombos(w: Float, plano: Float): Path = Path().apply {
        val nc = VentanaAntiguas.COLUMNAS; val nf = VentanaAntiguas.FILAS
        val du = 0.5f / nc; val dv = 0.5f / nf
        val k = 0.8f
        for (j in -1..nf) for (i in -1..nc) for (s in 0..1) {
            val cu = (i + 0.5f * s + 0.25f) / nc
            val cv = (j + 0.5f * s + 0.25f) / nf
            val a = p(w, cu, cv - dv * k, plano); val b = p(w, cu + du * k, cv, plano)
            val c = p(w, cu, cv + dv * k, plano); val d = p(w, cu - du * k, cv, plano)
            moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); lineTo(d.x, d.y); close()
        }
    }

    /** La luz a lo largo del paño: fuerte arriba, junto a la ventana, y apagándose hacia abajo y la derecha. */
    fun pincel(w: Float, plano: Float, alfa: Float): Brush {
        val a = p(w, 0f, 0f, plano); val b = p(w, 1f, 1f, plano)
        return Brush.linearGradient(listOf(color.copy(alpha = alfa), color.copy(alpha = alfa * 0.45f)), a, b)
    }

    companion object {
        /** Cuánto se corre el paño por cada plano de hondo que algo está delante de la pared (la luz entra hacia la derecha y abajo). */
        const val FRENTE_X = -0.034f
        const val FRENTE_Y = -0.014f
        /** El plano del frente del mueble (la cornisa, las tablas, los costados y el canto de lo guardado). */
        const val FRENTE = 1f
        /** El plano de lo que está encima de la cornisa (el bodegón). */
        const val ENCIMA = 1.5f
    }
}

/** El paño de la ventana según el momento: el sol de día, el ocaso de los Sueños en claro, la luna de los Sueños en oscuro. */
internal fun panoAntiguas(a: AireAntiguas): PanoAntiguas? = when {
    !a.oscuro && !a.noche -> PanoAntiguas(0.15f, 0.27f, 0.13f, 0.4f, 0.3f, 0.07f, PaletaAntiguas.SOL, 0.42f)
    !a.oscuro && a.noche -> PanoAntiguas(0.18f, 0.3f, 0.31f, 0.19f, 0.06f, 0.035f, Color(0xFFF2B040), 0.22f, tinte = 0.74f)
    a.oscuro && a.noche -> PanoAntiguas(0.155f, 0.3f, 0.2f, 0.46f, 0.52f, 0.07f, Color(0xFFA8BCE8), 0.3f)
    else -> null
}

/**
 * El paño en lo que hay dentro de [donde], en el plano [plano]: los rombos de luz, de borde decidido. El del ocaso, además, tiñe
 * de su color lo que toca (con [PanoAntiguas.tinte]): sobre el azul, la luz naranja en pantalla se volvería lila.
 */
internal fun DrawScope.panoEnAntiguas(p: PanoAntiguas, donde: Path, plano: Float, fuerza: Float = 1f) {
    val w = size.width
    clipPath(donde) {
        val rombos = p.rombos(w, plano)
        if (p.tinte > 0f) drawPath(rombos, p.pincel(w, plano, p.tinte * fuerza))
        drawPath(rombos, p.pincel(w, plano, p.fuerza * fuerza), blendMode = BlendMode.Screen)
    }
}

/** El paño en la pared del fondo (de la esquina a la derecha, por encima de la cornisa), y su rastro tenue en el aire. */
internal fun DrawScope.panoParedAntiguas(p: PanoAntiguas, g: MarcoAntiguas) {
    val w = g.w
    val pared = Path().apply { addRect(Rect(g.esquina, 0f, w, g.apoyo)) }
    clipRect(g.esquina, w * 0.055f, w, g.apoyo) {
        // El rastro en el aire: muy tenue y de borde blando.
        drawPath(p.forma(w, 0.6f), p.pincel(w, 0.6f, 0.05f))
    }
    panoEnAntiguas(p, pared, 0f)
}

/** El paño en el frente de la cornisa (corrido: está delante de la pared). */
internal fun DrawScope.panoCornisaAntiguas(p: PanoAntiguas, g: MarcoAntiguas) {
    panoEnAntiguas(p, Path().apply { addRect(Rect(0f, g.apoyo, g.w, g.ch + 1f)) }, PanoAntiguas.FRENTE, 0.85f)
}

/**
 * El paño en el cuerpo del mueble: en el fondo de cada casillero (el plano de la pared, en la sombra de la tabla de arriba) y en
 * las tablas y los costados (el plano del frente, corrido). Lo guardado lo recibe en su canto de arriba.
 */
internal fun DrawScope.panoCuerpoAntiguas(p: PanoAntiguas, d: DisposicionEstante, g: MarcoAntiguas) {
    val w = g.w
    val dT = w * 0.02f; val dI = w * 0.026f
    val fondo = Path()
    d.repisas.forEach { r -> fondo.addRect(Rect(g.x0 + dI, r.top + dT, g.x1 - dI, r.bottom)) }
    panoEnAntiguas(p, fondo, 0f, 0.7f)
    val frente = Path()
    d.tablas.forEach { r -> frente.addRect(r) }
    val abajo = d.tablas.lastOrNull()?.bottom ?: g.ch
    frente.addRect(Rect(0f, g.ch, g.x0, abajo))
    frente.addRect(Rect(g.x1, g.ch, w, abajo))
    panoEnAntiguas(p, frente, PanoAntiguas.FRENTE)
}

/**
 * La ventana, lo que va debajo de la noche: el muro del costado alrededor del hueco con su luz, la jamba del fondo y el
 * intradós del arco (de día los alumbra el sol), el alféizar y el bastidor de roble.
 */
internal fun DrawScope.ventanaAntiguas(g: MarcoAntiguas) {
    val P = PaletaAntiguas
    val V = VentanaAntiguas
    val w = g.w; val u = density
    val a = g.a
    val k = g.cerca(Offset(w * 0.07f, w * 0.3f))
    val hueco = V.contorno(w)
    // El derrame: el grueso del muro, de piedra enlucida. La jamba del fondo y el intradós miran hacia aquí; de día les da el sol.
    drawPath(hueco, a.luz(mezcla(P.CAL, P.LAPIS, 0.45f), k))
    val vidrioX1 = V.XB - V.JAMBA
    val jamba = Path().apply {
        val pasos = 10
        val a0 = V.punto(1f, V.ALFEIZAR, V.XA, vidrioX1)
        moveTo(a0.x * w, a0.y * w)
        for (i in 0..pasos) {
            val d = 0.5f + 0.5f * i / pasos
            val q = V.punto(d, V.arco(d), V.XA, vidrioX1)
            lineTo(q.x * w, q.y * w)
        }
        for (i in pasos downTo 0) {
            val d = 0.5f + 0.5f * i / pasos
            val q = V.punto(d, V.arco(d))
            lineTo(q.x * w, q.y * w)
        }
        val a1 = V.punto(1f, V.ALFEIZAR)
        lineTo(a1.x * w, a1.y * w)
        close()
    }
    val sol = !a.oscuro
    drawPath(jamba, if (sol) a.dia(mezcla(P.CAL, P.SOL, 0.35f), 0.1f) else a.luz(mezcla(P.CAL, P.LAPIS, 0.55f), k))
    // Las piedras del derrame, apenas: unas juntas en la jamba.
    clipPath(jamba) {
        for (j in 0 until 5) {
            val yb = V.ARRANQUE + (V.ALFEIZAR - V.ARRANQUE) * (j + 0.4f) / 5f
            val q0 = V.punto(0.8f, yb); val q1 = V.punto(1f, yb + 0.004f)
            drawLine(a.sombra(0.22f), Offset(q0.x * w, q0.y * w), Offset(q1.x * w, q1.y * w), 0.8f * u)
        }
    }
    // El alféizar: el canto de la piedra, abajo.
    val s0 = V.punto(0f, V.ALFEIZAR); val s1 = V.punto(1f, V.ALFEIZAR)
    drawLine(a.luz(P.PIEDRA, k), Offset(s0.x * w, s0.y * w), Offset(s1.x * w, s1.y * w), 3.2f * u)
    drawLine(a.claro(0.4f), Offset(s0.x * w, s0.y * w - 1.4f * u), Offset(s1.x * w, s1.y * w - 1.4f * u), 0.9f * u)
    drawLine(a.sombra(0.5f), Offset(s0.x * w, s0.y * w + 1.8f * u), Offset(s1.x * w, s1.y * w + 1.8f * u), 1.4f * u)
    // El canto del hueco en el muro: una arista de luz y otra de sombra.
    drawPath(hueco, a.sombra(0.45f), style = Stroke(1.6f * u))
}

/**
 * Los vidrios de la ventana, encima de la noche: de día el cielo pálido de altura (cada rombo de su vidrio, verdoso o ambarino),
 * de noche el añil con dos estrellas y, en los Sueños en oscuro, la media luna; los plomos, las dos barras de hierro y el
 * bastidor de roble.
 */
internal fun DrawScope.vidrioAntiguas(g: MarcoAntiguas) {
    val P = PaletaAntiguas
    val V = VentanaAntiguas
    val w = g.w; val u = density
    val a = g.a
    val x1 = V.XB - V.JAMBA
    val vidrio = V.contorno(w, V.XA, x1, V.ALFEIZAR - 0.006f)
    val noche = a.oscuro
    val luna = a.oscuro && a.noche
    val ocaso = !a.oscuro && a.noche
    clipPath(vidrio) {
        val r = Rect(V.XA * w, 0f, x1 * w, V.ALFEIZAR * w * 1.1f)
        val cielo = when {
            luna -> Color(0xFF233467)
            noche -> P.CIELO_NOCHE
            ocaso -> Color(0xFFF6C08A)
            else -> P.CIELO
        }
        val alto = when {
            luna -> Color(0xFF16204A)
            noche -> Color(0xFF0E1638)
            ocaso -> Color(0xFFE9A4A0)
            else -> Color(0xFFCFE0EA)
        }
        drawRect(Brush.verticalGradient(listOf(alto, cielo), r.top + w * 0.1f, r.bottom), r.topLeft, r.size)
        // Cada vidrio de su color: el vidrio viejo no es parejo.
        val nc = V.COLUMNAS; val nf = V.FILAS
        for (j in 0 until nf) for (i in 0 until nc) {
            val cu = (i + 0.5f) / nc
            val cv = (j + 0.5f) / nf
            val yb = V.ARRANQUE - V.FLECHA + (V.ALFEIZAR - V.ARRANQUE + V.FLECHA) * cv
            val q = V.punto(cu, yb, V.XA, x1)
            val tinte = when (Math.floorMod(i * 5 + j * 3, 4)) { 0 -> Color(0xFF9CC4A8); 1 -> Color(0xFFE6C882); 2 -> Color(0xFFB8C8E6); else -> Color.White }
            drawCircle(tinte.copy(alpha = if (noche) 0.07f else 0.16f), w * 0.022f, Offset(q.x * w, q.y * w))
        }
        if (noche && !luna) {
            // Dos estrellas, chicas, en vidrios distintos (con la luna no: una media luna con una estrella al lado es un emblema).
            listOf(0.3f to 0.22f, 0.7f to 0.33f).forEach { (cu, ybf) ->
                val q = V.punto(cu, ybf, V.XA, x1)
                drawCircle(Color(0xFFEFF2FA).copy(alpha = 0.85f), 1.1f * u, Offset(q.x * w, q.y * w))
                drawCircle(Color(0xFFEFF2FA).copy(alpha = 0.2f), 3f * u, Offset(q.x * w, q.y * w))
            }
        }
        if (luna) {
            // La media luna, alta, partida por los plomos (va antes que ellos).
            val q = V.punto(0.45f, 0.27f, V.XA, x1)
            val c = Offset(q.x * w, q.y * w); val rr = w * 0.026f
            mancha(c, rr * 3.2f, P.LUNA.copy(alpha = 0.35f))
            val m = Path().apply {
                addOval(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr))
                val dentro = Path().apply { addOval(Rect(c.x - rr * 0.55f, c.y - rr * 1.05f, c.x + rr * 1.25f, c.y + rr * 0.75f)) }
                op(this, dentro, androidx.compose.ui.graphics.PathOperation.Difference)
            }
            drawPath(m, Color(0xFFF1F4FB))
        }
        if (!noche) {
            // El sol que entra: el brillo de los vidrios de abajo, donde da de lleno.
            val q = V.punto(0.55f, V.ALFEIZAR - 0.08f, V.XA, x1)
            mancha(Offset(q.x * w, q.y * w), w * 0.08f, (if (ocaso) P.OCASO else Color.White).copy(alpha = 0.55f))
        }
        // Los plomos: dos familias de líneas en rombo, de la jamba cercana a la del fondo.
        val plomo = Path()
        val nl = nc + nf + 2
        val altoV = V.ALFEIZAR - V.ARRANQUE + V.FLECHA
        val paso = altoV / nf
        for (m in -nl..nl) {
            val ya = V.ARRANQUE - V.FLECHA + paso * m
            listOf(1f, -1f).forEach { s ->
                val p0 = V.punto(0f, ya, V.XA, x1); val p1 = V.punto(1f, ya + s * paso * nc * 0.5f, V.XA, x1)
                plomo.moveTo(p0.x * w, p0.y * w); plomo.lineTo(p1.x * w, p1.y * w)
            }
        }
        val colorPlomo = if (noche) Color(0xFF0A0C16) else Color(0xFF3A3A42)
        drawPath(plomo, colorPlomo, style = Stroke(1.3f * u))
        if (!noche) drawPath(plomo, Color.White.copy(alpha = 0.25f), style = Stroke(0.4f * u))
        // Las dos barras de hierro que sujetan los plomos.
        listOf(0.42f, 0.66f).forEach { f ->
            val yb = V.ARRANQUE - V.FLECHA + altoV * f
            val p0 = V.punto(0f, yb, V.XA, x1); val p1 = V.punto(1f, yb, V.XA, x1)
            drawLine(a.luz(P.HIERRO, 0.5f), Offset(p0.x * w, p0.y * w), Offset(p1.x * w, p1.y * w), 2.2f * u)
            if (!noche) drawLine(a.claro(0.35f), Offset(p0.x * w, p0.y * w - 0.9f * u), Offset(p1.x * w, p1.y * w - 0.9f * u), 0.6f * u)
        }
    }
    // El bastidor de roble alrededor del vidrio y la falleba.
    val kb = g.cerca(Offset(w * 0.06f, w * 0.3f))
    drawPath(vidrio, g.am.luz(P.ROBLE_OSC, kb), style = Stroke(3.4f * u))
    drawPath(vidrio, g.am.luz(P.ROBLE_LUZ, kb).copy(alpha = 0.5f), style = Stroke(0.9f * u))
    val f0 = V.punto(0.92f, V.ARRANQUE + 0.11f, V.XA, x1)
    drawLine(g.am.luz(P.HIERRO, kb), Offset(f0.x * w, f0.y * w), Offset(f0.x * w, f0.y * w + w * 0.05f), 2f * u, StrokeCap.Round)
    if (!noche) {
        // La luz del día en el muro del costado, junto al hueco (rebota del derrame).
        val q = V.punto(1.05f, V.ALFEIZAR - 0.06f)
        mancha(Offset(q.x * w, q.y * w), w * 0.09f, (if (ocaso) P.OCASO else P.SOL).copy(alpha = 0.22f))
    }
}

/** Una media luna de plata recortada, de radio [r] alrededor de [c] (el disco menos otro corrido arriba a la derecha). */
internal fun crecienteAntiguas(c: Offset, r: Float): Path {
    val fuera = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val dentro = Path().apply { addOval(Rect(c.x - r * 0.28f, c.y - r * 1.04f, c.x + r * 1.28f, c.y + r * 0.6f)) }
    return Path().apply { op(fuera, dentro, androidx.compose.ui.graphics.PathOperation.Difference) }
}

/** Un punto de [n] puntos alrededor de un círculo de radio [r] (para los engarces y las rosetas). */
internal fun puntosEnCirculoAntiguas(c: Offset, r: Float, n: Int, giro: Float = 0f): List<Offset> =
    List(n.coerceAtLeast(1)) { i ->
        val t = giro + 2.0 * PI * i / n.coerceAtLeast(1)
        Offset(c.x + (cos(t) * r).toFloat(), c.y + (sin(t) * r).toFloat())
    }
