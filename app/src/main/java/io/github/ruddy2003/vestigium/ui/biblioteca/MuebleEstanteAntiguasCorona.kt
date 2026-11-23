package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo de arriba del estante de Tierras Antiguas ([MuebleEstanteAntiguas]): el bodegón sobre la cornisa y lo colgado en la pared.
 * Manda la drusa de amatista de la mina, en su peana de roble, con el sol de la ventana en las caras; delante, el plato de latón
 * con las gemas talladas; a la izquierda, el mortero de piedra con el polvo de lapislázuli y su mano; a la derecha, el tomo grande
 * de la compañía recostado contra la pared, dos tomos tumbados con la vela de las lunas en su candelero de pincho, el cuerno de
 * tinta con la pluma y, al final, el laúd apoyado. Colgado de su clavo, el aro de llaves de la posada.
 */
internal object CoronaEstanteAntiguas {
    private val P = PaletaAntiguas

    /** La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera de la vela, más marcada cerca. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, g: MarcoAntiguas, fuerza: Float = 0.26f) {
        val w = g.w
        val a = g.a
        if (!a.oscuro) {
            val dx = if (a.noche) 0.045f else 0.026f
            translate(w * dx, w * 0.012f) { drawPath(p, a.sombra(fuerza)) }
        } else {
            val k = g.cerca(desde)
            if (k < 0.12f) return
            val v = g.vela
            val dx = ((desde.x - v.x) / w).coerceIn(-0.3f, 0.3f) / 0.3f
            translate(w * 0.04f * dx, w * 0.014f) { drawPath(p, a.sombra((0.5f * k).coerceAtMost(0.42f))) }
        }
    }

    // ---------------------------------------------------------------- lo colgado

    fun DrawScope.colgado(g: MarcoAntiguas) {
        llaves(Offset(g.w * 0.79f, g.w * 0.142f), g)
    }

    /** Un clavo de forja hincado en la pared: la cabeza cuadrada con su luz. */
    private fun DrawScope.clavo(p: Offset, g: MarcoAntiguas, k: Float) {
        val u = density
        val am = g.am
        drawRect(am.sombra(0.45f), Offset(p.x - 2f * u, p.y - 1.4f * u), Size(5f * u, 4.6f * u))
        drawRect(am.luz(P.HIERRO, k), Offset(p.x - 2.4f * u, p.y - 2.4f * u), Size(4.8f * u, 4.8f * u))
        drawRect(am.brillo(0.5f, k), Offset(p.x - 2.4f * u, p.y - 2.4f * u), Size(4.8f * u, 1f * u))
    }

    /** El aro de llaves de la posada: el aro de hierro colgado del clavo y cuatro llaves de paletón en abanico, de largos distintos. */
    private fun DrawScope.llaves(clavoP: Offset, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(clavoP + Offset(0f, w * 0.07f))
        val ra = w * 0.026f
        val centro = clavoP + Offset(0f, ra * 0.95f)
        val abajo = centro + Offset(0f, ra * 0.9f)
        // Cada llave: su giro (las de los lados se abren), su largo y la forma del ojo.
        val llaves = listOf(Triple(24f, 0.1f, 0), Triple(8f, 0.13f, 1), Triple(-9f, 0.115f, 2), Triple(-25f, 0.095f, 0))
        val silueta = Path().apply {
            addOval(Rect(centro.x - ra, centro.y - ra, centro.x + ra, centro.y + ra))
            llaves.forEach { (giro, largo, _) ->
                val l = w * largo
                val dx = -kotlin.math.sin(Math.toRadians(giro.toDouble())).toFloat() * l
                addPath(poligonoAntiguas(abajo.x - 2f * u, abajo.y, abajo.x + 2f * u, abajo.y, abajo.x + dx + 2f * u, abajo.y + l, abajo.x + dx - 2f * u, abajo.y + l))
            }
        }
        sombraEnPared(silueta, centro + Offset(0f, w * 0.05f), g, 0.3f)
        clavo(clavoP, g, k)
        llaves.forEach { (giro, largo, forma) -> rotate(giro, abajo) { llave(abajo, w * largo, forma, am, k) } }
        drawCircle(am.luz(P.HIERRO, k), ra, centro, style = Stroke(2.6f * u))
        drawArc(am.brillo(0.55f, k), 160f, 110f, false, Offset(centro.x - ra, centro.y - ra), Size(ra * 2, ra * 2), style = Stroke(0.9f * u))
    }

    /** Una llave de paletón colgada por su ojo: el ojo (redondo, oval o de lazo), la caña y el paletón con sus dientes. */
    private fun DrawScope.llave(ojo: Offset, largo: Float, forma: Int, am: AireAntiguas, k: Float) {
        val u = density
        val hierro = am.luz(if (forma == 1) P.LATON_OSC else P.HIERRO, k)
        val r = largo * 0.17f
        val c = ojo + Offset(0f, r * 0.85f)
        when (forma) {
            0 -> drawCircle(hierro, r, c, style = Stroke(2.2f * u))
            1 -> drawOval(hierro, Offset(c.x - r * 0.8f, c.y - r), Size(r * 1.6f, r * 2f), style = Stroke(2.2f * u))
            else -> {
                drawCircle(hierro, r * 0.85f, c, style = Stroke(2.2f * u))
                drawCircle(hierro, r * 0.3f, c)
            }
        }
        val y0 = c.y + r
        val fin = ojo.y + largo
        drawRect(hierro, Offset(c.x - r * 0.45f, y0 - 0.5f * u), Size(r * 0.9f, 2f * u))
        drawLine(hierro, Offset(c.x, y0), Offset(c.x, fin), 2.6f * u, StrokeCap.Round)
        drawLine(am.brillo(0.45f, k), Offset(c.x - 0.9f * u, y0), Offset(c.x - 0.9f * u, fin - 3f * u), 0.7f * u)
        // El paletón: un bloque a un lado del extremo, con sus dos dientes.
        val pb = Rect(c.x, fin - largo * 0.22f, c.x + largo * 0.18f, fin - largo * 0.02f)
        drawRect(hierro, pb.topLeft, pb.size)
        drawRect(am.brillo(0.35f, k), pb.topLeft, Size(pb.width, 0.8f * u))
        drawRect(am.sombra(0.7f), Offset(pb.left + pb.width * 0.4f, pb.top + pb.height * 0.28f), Size(pb.width * 0.6f, pb.height * 0.18f))
        drawRect(am.sombra(0.7f), Offset(pb.left + pb.width * 0.6f, pb.top + pb.height * 0.62f), Size(pb.width * 0.4f, pb.height * 0.16f))
    }

    // ---------------------------------------------------------------- encima de la cornisa

    fun DrawScope.encima(g: MarcoAntiguas, pano: PanoAntiguas?) {
        laud(Offset(g.w * 0.925f, g.apoyo), g, pano)
        tomoApoyado(Offset(g.w * 0.47f, g.apoyo), g, pano)
        mortero(Offset(g.w * 0.172f, g.apoyo), g, pano)
        drusaAmatista(Offset(g.w * 0.325f, g.apoyo), g, pano)
        platoGemas(Offset(g.w * 0.27f, g.apoyo + g.w * 0.004f), g, pano)
        tomosYVela(Offset(g.w * 0.655f, g.apoyo), g)
        tintero(Offset(g.w * 0.8f, g.apoyo), g)
        luzVela(g)
    }

    /**
     * El tomo grande de la compañía, de pie y recostado contra la pared entre la drusa y los tomos tumbados: el lomo de cuero añil
     * con sus nervios y sus filetes, las cantoneras de latón y la cinta que asoma.
     */
    private fun DrawScope.tomoApoyado(pie: Offset, g: MarcoAntiguas, pano: PanoAntiguas?) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(pie - Offset(0f, w * 0.1f))
        val an = w * 0.062f; val al = w * 0.215f
        val giro = 9f
        val cuero = Color(0xFF243A6A)
        val r = Rect(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y)
        rotate(giro, Offset(r.right, pie.y)) {
            sombraEnPared(Path().apply { addRect(r) }, r.center, g, 0.3f)
            drawRoundRect(am.luz(cuero, k), r.topLeft, r.size, CornerRadius(an * 0.12f))
            drawRect(am.claro(0.22f), Offset(r.left + an * 0.1f, r.top), Size(an * 0.22f, al))
            drawRect(am.sombra(0.3f), Offset(r.right - an * 0.28f, r.top), Size(an * 0.28f, al))
            listOf(0.1f, 0.3f, 0.45f, 0.6f, 0.8f).forEach { f ->
                val y = r.top + al * f
                drawRect(am.sombra(0.4f), Offset(r.left, y + 1.6f * u), Size(an, 1.2f * u))
                drawRect(am.luz(mezcla(cuero, Color.White, 0.2f), k), Offset(r.left, y - 0.6f * u), Size(an, 2f * u))
                drawRect(am.luz(P.ORO, k).copy(alpha = 0.8f), Offset(r.left + an * 0.15f, y + 3.4f * u), Size(an * 0.7f, 0.7f * u))
            }
            // Un rombo dorado en el entrenervio de en medio.
            val c = Offset(r.center.x, r.top + al * 0.525f)
            val q = an * 0.18f
            drawPath(poligonoAntiguas(c.x, c.y - q, c.x + q * 0.75f, c.y, c.x, c.y + q, c.x - q * 0.75f, c.y), am.luz(P.ORO, k))
            listOf(r.top, r.bottom - an * 0.3f).forEach { y ->
                drawRect(am.luz(P.LATON, k), Offset(r.left, y), Size(an, an * 0.3f))
                drawRect(am.brillo(0.5f, k), Offset(r.left, y), Size(an, 1f * u))
            }
            drawLine(am.luz(P.RUBIA, k), Offset(r.left + an * 0.3f, r.bottom - 1f * u), Offset(r.left + an * 0.26f, r.bottom + w * 0.004f), 2f * u)
            if (am.oscuro && k > 0.1f) drawRect(P.LLAMA.copy(alpha = (0.5f * k).coerceAtMost(0.45f)), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, al))
            pano?.let { panoEnAntiguas(it, Path().apply { addRect(r) }, PanoAntiguas.ENCIMA, 0.8f) }
        }
    }

    /**
     * La drusa de amatista de la mina, en su peana de roble: la matriz de piedra parda y los cristales de seis caras que salen
     * de ella en abanico, pálidos en la base y morados hacia la punta, cada uno con su cara a la luz y su cara en sombra, los de
     * atrás más altos y los de delante más bajos; destellos en las puntas.
     */
    private fun DrawScope.drusaAmatista(pie: Offset, g: MarcoAntiguas, pano: PanoAntiguas?) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(pie - Offset(0f, w * 0.1f))
        val an = w * 0.2f
        // La peana: un zócalo bajo con su moldura.
        val peana = Rect(pie.x - an * 0.52f, pie.y - w * 0.02f, pie.x + an * 0.52f, pie.y)
        val base = pie.y - w * 0.02f
        // Los cristales: (lugar a lo ancho, alto, giro, grueso); primero los de atrás.
        val cristales = listOf(
            floatArrayOf(-0.3f, 0.13f, -16f, 0.024f), floatArrayOf(0.32f, 0.14f, 15f, 0.026f), floatArrayOf(-0.08f, 0.185f, -4f, 0.03f),
            floatArrayOf(0.12f, 0.17f, 7f, 0.028f), floatArrayOf(-0.2f, 0.15f, -10f, 0.026f), floatArrayOf(0.22f, 0.12f, 12f, 0.024f),
            floatArrayOf(0.02f, 0.2f, 1f, 0.032f), floatArrayOf(-0.4f, 0.08f, -24f, 0.02f), floatArrayOf(0.42f, 0.085f, 24f, 0.02f),
            floatArrayOf(-0.14f, 0.1f, -8f, 0.024f), floatArrayOf(0.08f, 0.11f, 5f, 0.026f), floatArrayOf(0.28f, 0.07f, 16f, 0.02f),
            floatArrayOf(-0.27f, 0.065f, -14f, 0.02f),
        )
        val silueta = Path().apply {
            addRect(peana)
            cristales.forEach { cr ->
                val x = pie.x + an * cr[0]; val alto = w * cr[1]
                addPath(poligonoAntiguas(x - w * cr[3], base, x + w * cr[3], base, x + w * cr[3] * 0.4f + alto * cr[2] / 60f, base - alto, x - w * cr[3] * 0.4f + alto * cr[2] / 60f, base - alto))
            }
        }
        sombraEnPared(silueta, pie - Offset(0f, w * 0.1f), g, 0.28f)
        contactoAntiguas(Offset(pie.x + w * 0.012f, pie.y), an * 1.1f, w * 0.014f, am, 0.45f)
        drawRect(am.luz(P.ROBLE, k), peana.topLeft, peana.size)
        drawRect(am.luz(P.ROBLE_LUZ, k), peana.topLeft, Size(peana.width, w * 0.005f))
        drawRect(am.luz(P.ORO, k), Offset(peana.left, peana.top + w * 0.008f), Size(peana.width, 1f * u))
        drawRect(am.sombra(0.35f), Offset(peana.right - peana.width * 0.22f, peana.top), Size(peana.width * 0.22f, peana.height))
        // La matriz de piedra.
        val matriz = poligonoAntiguas(pie.x - an * 0.5f, base + 1f, pie.x - an * 0.46f, base - w * 0.03f, pie.x - an * 0.2f, base - w * 0.045f,
            pie.x + an * 0.18f, base - w * 0.042f, pie.x + an * 0.47f, base - w * 0.028f, pie.x + an * 0.5f, base + 1f)
        drawPath(matriz, am.luz(mezcla(P.PIEDRA_OSC, P.CUERO, 0.3f), k))
        cristales.forEachIndexed { i, cr -> cristal(Offset(pie.x + an * cr[0], base - w * 0.01f), w * cr[1], cr[2], w * cr[3], am, k, i) }
        // La matriz de delante, que tapa el arranque de los cristales, con sus poros.
        val delante = poligonoAntiguas(pie.x - an * 0.5f, base + 1f, pie.x - an * 0.42f, base - w * 0.016f, pie.x - an * 0.1f, base - w * 0.022f,
            pie.x + an * 0.25f, base - w * 0.018f, pie.x + an * 0.5f, base - w * 0.008f, pie.x + an * 0.5f, base + 1f)
        drawPath(delante, am.luz(mezcla(P.PIEDRA, P.CUERO, 0.3f), k))
        drawPath(poligonoAntiguas(pie.x - an * 0.5f, base + 1f, pie.x - an * 0.42f, base - w * 0.016f, pie.x - an * 0.18f, base - w * 0.02f, pie.x - an * 0.25f, base + 1f), am.claro(0.3f))
        for (i in 0 until 7) drawCircle(am.sombra(0.3f), (1f + azar(8120, i)) * u, Offset(pie.x + an * (azar(8121, i) - 0.5f) * 0.9f, base - w * 0.006f * azar(8122, i) - 1f * u))
        pano?.let { panoEnAntiguas(it, silueta, PanoAntiguas.ENCIMA, 0.85f) }
    }

    /**
     * Un cristal de amatista de seis caras, de pie en [b], de [alto] y [grueso], girado [giro] grados: la cara de la izquierda a
     * la luz, la de la derecha en sombra, pálido en la base y morado hacia la punta piramidal, con el brillo de la arista.
     */
    private fun DrawScope.cristal(b: Offset, alto: Float, giro: Float, grueso: Float, am: AireAntiguas, k: Float, s: Int) {
        val u = density
        rotate(giro, b) {
            val g2 = grueso
            val cuerpo = alto * 0.78f
            val top = b.y - cuerpo
            val punta = Offset(b.x + g2 * 0.1f, b.y - alto)
            val izq = poligonoAntiguas(b.x - g2, b.y, b.x, b.y, b.x, top, b.x - g2, top + g2 * 0.15f)
            val der = poligonoAntiguas(b.x, b.y, b.x + g2, b.y, b.x + g2, top + g2 * 0.15f, b.x, top)
            val claro = mezcla(P.AMATISTA_LUZ, P.CUARZO, 0.35f)
            val medio = mezcla(P.AMATISTA, P.AMATISTA_LUZ, 0.25f + 0.2f * azar(8130, s))
            // Pálido abajo, morado arriba: dos tramos en cada cara.
            drawPath(izq, am.luz(claro, k))
            drawPath(der, am.luz(mezcla(claro, P.AMATISTA_OSC, 0.45f), k))
            clipRect(b.x - g2 * 1.2f, top - g2, b.x + g2 * 1.2f, b.y - cuerpo * 0.45f) {
                drawPath(izq, am.luz(medio, k))
                drawPath(der, am.luz(mezcla(P.AMATISTA, P.AMATISTA_OSC, 0.6f), k))
            }
            // La punta: dos facetas.
            drawPath(poligonoAntiguas(b.x - g2, top + g2 * 0.15f, b.x, top, punta.x, punta.y), am.luz(mezcla(P.AMATISTA, P.AMATISTA_LUZ, 0.45f), k))
            drawPath(poligonoAntiguas(b.x, top, b.x + g2, top + g2 * 0.15f, punta.x, punta.y), am.luz(P.AMATISTA_OSC, k))
            drawLine(am.luz(mezcla(P.AMATISTA_LUZ, Color.White, 0.5f), k), Offset(b.x, b.y - cuerpo * 0.1f), Offset(b.x, top), 0.8f * u)
            drawLine(am.claro(0.6f), Offset(b.x - g2 * 0.6f, top + g2 * 0.4f), Offset(punta.x - g2 * 0.2f, punta.y + alto * 0.05f), 0.8f * u)
            if (am.oscuro) destelloAntiguas(Offset(punta.x - g2 * 0.15f, punta.y + alto * 0.08f), 1f * u, am, k)
            else if (s % 3 == 0) drawCircle(am.claro(0.95f), 1.2f * u, Offset(punta.x - g2 * 0.15f, punta.y + alto * 0.08f))
        }
    }

    /**
     * El laúd apoyado contra la pared al final de la cornisa, algo inclinado: el vientre de abeto con su rosa calada, el puente y
     * las cuerdas, la caja de duelas que asoma por el costado, el mástil con sus trastes y el clavijero vuelto atrás con sus
     * clavijas; una cinta de lana roja atada al mástil.
     */
    private fun DrawScope.laud(pie: Offset, g: MarcoAntiguas, pano: PanoAntiguas?) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(pie - Offset(0f, w * 0.12f))
        val an = w * 0.128f; val al = w * 0.165f
        val giro = -13f
        val abeto = Color(0xFFE6C88E)
        val caja = Path().apply {
            moveTo(pie.x, pie.y - al)
            cubicTo(pie.x + an * 0.32f, pie.y - al * 0.96f, pie.x + an * 0.55f, pie.y - al * 0.45f, pie.x + an * 0.48f, pie.y - al * 0.18f)
            cubicTo(pie.x + an * 0.42f, pie.y + al * 0.02f, pie.x - an * 0.42f, pie.y + al * 0.02f, pie.x - an * 0.48f, pie.y - al * 0.18f)
            cubicTo(pie.x - an * 0.55f, pie.y - al * 0.45f, pie.x - an * 0.32f, pie.y - al * 0.96f, pie.x, pie.y - al)
            close()
        }
        val mastil0 = Offset(pie.x, pie.y - al * 0.97f)
        val mastil1 = Offset(pie.x, pie.y - al * 1.85f)
        rotate(giro, pie) {
            val silueta = Path().apply {
                addPath(caja)
                addRect(Rect(pie.x - w * 0.012f, mastil1.y - w * 0.04f, pie.x + w * 0.012f, mastil0.y))
            }
            sombraEnPared(silueta, pie - Offset(0f, w * 0.12f), g, 0.3f)
        }
        contactoAntiguas(Offset(pie.x + w * 0.01f, pie.y), an * 0.8f, w * 0.012f, am, 0.45f)
        rotate(giro, pie) {
            // La caja de duelas, que asoma por la derecha (el laúd está un poco de lado).
            translate(an * 0.07f, 0f) { drawPath(caja, am.luz(Color(0xFF6A3A1E), k)) }
            clipPath(Path().apply { addPath(caja, Offset(an * 0.07f, 0f)) }) {
                for (i in 0 until 4) drawLine(am.luz(Color(0xFF9A5A2E), k), Offset(pie.x + an * (0.3f + 0.06f * i), pie.y - al), Offset(pie.x + an * (0.32f + 0.06f * i), pie.y), 0.9f * u)
            }
            // El vientre.
            drawPath(caja, am.luz(abeto, k))
            clipPath(caja) {
                for (i in 0 until 7) drawLine(am.luz(mezcla(abeto, P.ROBLE, 0.25f), k).copy(alpha = 0.5f), Offset(pie.x - an * 0.45f + an * 0.15f * i, pie.y - al), Offset(pie.x - an * 0.45f + an * 0.15f * i, pie.y), 0.6f * u)
                drawRect(am.claro(0.2f), Offset(pie.x - an * 0.5f, pie.y - al), Size(an * 0.25f, al))
                drawRect(am.sombra(0.25f), Offset(pie.x + an * 0.22f, pie.y - al), Size(an * 0.3f, al))
            }
            drawPath(caja, am.luz(P.ROBLE_OSC, k), style = Stroke(1.6f * u))
            // La rosa: el agujero con su anillo de latón y un cerco de puntos tallados.
            val rosa = Offset(pie.x, pie.y - al * 0.6f)
            val rr = an * 0.17f
            drawCircle(am.luz(Color(0xFF1E140C), k), rr, rosa)
            puntosEnCirculoAntiguas(rosa, rr * 0.55f, 9).forEach { drawCircle(am.luz(abeto, k), rr * 0.13f, it) }
            drawCircle(am.luz(P.LATON, k), rr * 1.08f, rosa, style = Stroke(1.2f * u))
            drawCircle(am.luz(P.ROBLE_OSC, k), rr * 1.35f, rosa, style = Stroke(0.7f * u))
            // El puente.
            val puente = Rect(pie.x - an * 0.24f, pie.y - al * 0.22f, pie.x + an * 0.24f, pie.y - al * 0.17f)
            drawRoundRect(am.luz(Color(0xFF2A1A10), k), puente.topLeft, puente.size, CornerRadius(1f * u))
            // El mástil, con sus trastes; el clavijero vuelto atrás, con las clavijas.
            drawLine(am.luz(Color(0xFF2E1E14), k), mastil0, mastil1, w * 0.022f)
            drawLine(am.brillo(0.35f, k), mastil0 - Offset(w * 0.009f, 0f), mastil1 - Offset(w * 0.009f, 0f), 0.8f * u)
            for (i in 1..6) {
                val y = mastil0.y + (mastil1.y - mastil0.y) * i / 7f
                drawLine(am.luz(P.CUERDA, k), Offset(pie.x - w * 0.011f, y), Offset(pie.x + w * 0.011f, y), 0.7f * u)
            }
            val clav = poligonoAntiguas(pie.x - w * 0.012f, mastil1.y, pie.x + w * 0.012f, mastil1.y, pie.x + w * 0.02f, mastil1.y - w * 0.045f, pie.x + w * 0.002f, mastil1.y - w * 0.05f)
            drawPath(clav, am.luz(Color(0xFF3E2616), k))
            for (i in 0 until 4) {
                val y = mastil1.y - w * (0.008f + 0.011f * i)
                val x = pie.x + w * (0.002f + 0.004f * i)
                drawLine(am.luz(P.ROBLE_LUZ, k), Offset(x - w * 0.018f, y), Offset(x - w * 0.008f, y), 1.6f * u, StrokeCap.Round)
                drawLine(am.luz(P.ROBLE_LUZ, k), Offset(x + w * 0.012f, y + w * 0.002f), Offset(x + w * 0.022f, y + w * 0.002f), 1.6f * u, StrokeCap.Round)
            }
            // Las cuerdas, del puente a la cejuela.
            for (i in 0 until 5) {
                val x = pie.x - an * 0.12f + an * 0.06f * i
                drawLine(am.claro(0.55f).copy(alpha = if (am.oscuro) 0f else 0.7f), Offset(x, puente.center.y), Offset(pie.x - w * 0.006f + w * 0.003f * i, mastil1.y), 0.5f * u)
                if (am.oscuro && k > 0.15f) drawLine(P.LLAMA.copy(alpha = (0.6f * k).coerceAtMost(0.5f)), Offset(x, puente.center.y), Offset(pie.x - w * 0.006f + w * 0.003f * i, mastil1.y), 0.5f * u)
            }
            // La cinta de lana atada al mástil, que cuelga.
            val nudo = Offset(pie.x, mastil0.y - w * 0.012f)
            drawLine(am.luz(P.RUBIA, k), nudo, nudo + Offset(-w * 0.018f, w * 0.055f), 2.2f * u, StrokeCap.Round)
            drawLine(am.luz(P.RUBIA, k), nudo, nudo + Offset(-w * 0.004f, w * 0.065f), 2.2f * u, StrokeCap.Round)
            drawCircle(am.luz(P.RUBIA, k), 2.4f * u, nudo)
            pano?.let { panoEnAntiguas(it, caja, PanoAntiguas.ENCIMA, 0.8f) }
        }
    }

    /** El plato de latón con las gemas talladas: rubíes, esmeraldas y un zafiro, con sus facetas y su brillo. */
    private fun DrawScope.platoGemas(c: Offset, g: MarcoAntiguas, pano: PanoAntiguas?) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(c)
        val r = Rect(c.x - w * 0.05f, c.y - w * 0.014f, c.x + w * 0.05f, c.y + w * 0.004f)
        contactoAntiguas(Offset(c.x + w * 0.008f, c.y + w * 0.002f), w * 0.11f, w * 0.01f, am, 0.42f)
        drawOval(am.luz(P.LATON_OSC, k), r.topLeft, r.size)
        drawOval(am.luz(P.LATON, k), Offset(r.left + w * 0.004f, r.top), Size(r.width - w * 0.008f, r.height * 0.7f))
        drawOval(am.luz(P.LATON_OSC, k), Offset(r.left + w * 0.012f, r.top + r.height * 0.12f), Size(r.width - w * 0.024f, r.height * 0.45f))
        val gemas = listOf(Triple(-0.028f, P.RUBI, 0), Triple(-0.008f, P.ESMERALDA, 1), Triple(0.012f, P.RUBI, 1), Triple(0.03f, P.ZAFIRO, 0), Triple(0.002f, P.AMATISTA, 0))
        gemas.forEachIndexed { i, (dx, col, forma) ->
            val q = Offset(c.x + w * dx, c.y - w * (0.012f + 0.006f * (i % 2)))
            val s = w * (0.009f + 0.002f * (i % 3))
            val gema = if (forma == 0) poligonoAntiguas(q.x, q.y - s, q.x + s, q.y, q.x, q.y + s * 0.8f, q.x - s, q.y)
            else Path().apply { addOval(Rect(q.x - s, q.y - s * 0.7f, q.x + s, q.y + s * 0.7f)) }
            drawPath(gema, am.luz(mezcla(col, Color.Black, 0.25f), k))
            clipPath(gema) {
                drawRect(am.luz(mezcla(col, Color.White, 0.25f), k), Offset(q.x - s, q.y - s), Size(s, s))
                drawRect(am.luz(col, k), Offset(q.x, q.y - s), Size(s, s))
            }
            drawCircle(am.claro(0.9f), 0.9f * u, q - Offset(s * 0.35f, s * 0.35f))
            if (am.oscuro) destelloAntiguas(q - Offset(s * 0.35f, s * 0.35f), 0.9f * u, am, k)
        }
        pano?.let { panoEnAntiguas(it, Path().apply { addOval(r) }, PanoAntiguas.ENCIMA, 0.7f) }
    }

    /** El mortero de piedra con el polvo de lapislázuli, la mano apoyada dentro y unos granos azules derramados. */
    private fun DrawScope.mortero(pie: Offset, g: MarcoAntiguas, pano: PanoAntiguas?) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(pie)
        val an = w * 0.085f; val al = w * 0.058f
        val cuerpo = Path().apply {
            moveTo(pie.x - an / 2, pie.y - al)
            cubicTo(pie.x - an / 2, pie.y - al * 0.2f, pie.x - an * 0.3f, pie.y, pie.x - an * 0.2f, pie.y)
            lineTo(pie.x + an * 0.2f, pie.y)
            cubicTo(pie.x + an * 0.3f, pie.y, pie.x + an / 2, pie.y - al * 0.2f, pie.x + an / 2, pie.y - al)
            close()
        }
        sombraEnPared(cuerpo, pie - Offset(0f, al / 2), g, 0.26f)
        contactoAntiguas(Offset(pie.x + w * 0.008f, pie.y), an * 1.1f, w * 0.012f, am, 0.42f)
        // La mano de madera, que asoma inclinada (detrás del borde de delante).
        val m0 = Offset(pie.x + an * 0.05f, pie.y - al * 0.7f); val m1 = Offset(pie.x + an * 0.55f, pie.y - al * 1.85f)
        drawLine(am.luz(P.ROBLE_LUZ, k), m0, m1, w * 0.014f, StrokeCap.Round)
        drawLine(am.sombra(0.3f), m0 + Offset(w * 0.004f, w * 0.002f), m1 + Offset(w * 0.004f, w * 0.002f), w * 0.005f, StrokeCap.Round)
        drawCircle(am.luz(P.ROBLE, k), w * 0.011f, m1)
        // La piedra del mortero, verde gris, con su luz a la izquierda.
        drawPath(cuerpo, am.luz(mezcla(P.PIEDRA, P.ESMERALDA, 0.12f), k))
        clipPath(cuerpo) {
            drawRect(am.claro(0.3f), Offset(pie.x - an / 2, pie.y - al), Size(an * 0.28f, al))
            drawRect(am.sombra(0.3f), Offset(pie.x + an * 0.2f, pie.y - al), Size(an * 0.3f, al))
            for (i in 0 until 8) drawCircle(am.sombra(0.25f), 1f * u, Offset(pie.x + an * (azar(8201, i) - 0.5f) * 0.9f, pie.y - al * azar(8202, i) * 0.8f))
        }
        // La boca con el polvo azul.
        val boca = Rect(pie.x - an / 2, pie.y - al - w * 0.01f, pie.x + an / 2, pie.y - al + w * 0.01f)
        drawOval(am.luz(P.PIEDRA_OSC, k), boca.topLeft, boca.size)
        drawOval(am.luz(P.LAPIS, k), Offset(boca.left + an * 0.1f, boca.top + w * 0.003f), Size(an * 0.8f, boca.height * 0.65f))
        drawOval(am.luz(P.LAPIS_LUZ, k), Offset(boca.left + an * 0.2f, boca.top + w * 0.004f), Size(an * 0.35f, boca.height * 0.3f))
        // La mano otra vez, la parte que queda delante del polvo.
        drawLine(am.luz(P.ROBLE_LUZ, k), m0, Offset(m0.x + (m1.x - m0.x) * 0.25f, m0.y + (m1.y - m0.y) * 0.25f), w * 0.014f, StrokeCap.Round)
        drawOval(am.claro(0.35f), Offset(boca.left, boca.top), Size(an * 0.5f, 1.4f * u))
        // Unos granos de lapislázuli derramados en la tapa.
        for (i in 0 until 6) drawCircle(am.luz(P.LAPIS_LUZ, k), (0.9f + 0.8f * azar(8203, i)) * u, Offset(pie.x + an * (0.55f + 0.5f * azar(8204, i)), pie.y - w * 0.003f * azar(8205, i)))
        pano?.let { panoEnAntiguas(it, cuerpo, PanoAntiguas.ENCIMA, 0.8f) }
    }

    /**
     * Dos tomos grandes tumbados, uno sobre otro y algo corrido, con el lomo hacia aquí (nervios, filetes dorados y cantoneras), y
     * encima la vela de las lunas en su candelero de pincho: una vela gruesa de cera cruda con anillos pintados para contar las
     * horas, sus chorreones y la mecha; de noche arde.
     */
    private fun DrawScope.tomosYVela(pie: Offset, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(pie - Offset(0f, w * 0.04f))
        val m = MuebleEstanteAntiguas
        val tomos = listOf(
            Rect(pie.x - w * 0.115f, pie.y - w * 0.042f, pie.x + w * 0.125f, pie.y) to Color(0xFF5A2418),
            Rect(pie.x - w * 0.085f, pie.y - w * 0.078f, pie.x + w * 0.105f, pie.y - w * 0.042f) to Color(0xFF233A30),
        )
        val silueta = Path().apply { tomos.forEach { addRect(it.first) } }
        sombraEnPared(silueta, pie - Offset(0f, w * 0.04f), g, 0.3f)
        contactoAntiguas(Offset(pie.x + w * 0.012f, pie.y), w * 0.25f, w * 0.012f, am, 0.45f)
        tomos.forEachIndexed { i, (r, cuero) ->
            translate(0f, 0f) { tomoTumbado(r, cuero, am, k, i) }
        }
        // El candelero de pincho: el plato, la caña y el pincho, de hierro.
        val vx = w * m.VELA_X
        val cima = tomos[1].first.top
        val plato = Rect(vx - w * 0.034f, cima - w * 0.008f, vx + w * 0.034f, cima)
        contactoAntiguas(Offset(vx + w * 0.006f, cima), w * 0.075f, w * 0.008f, am, 0.45f)
        drawOval(am.luz(P.HIERRO, k), plato.topLeft, plato.size)
        drawOval(am.brillo(0.45f, k), Offset(plato.left + w * 0.006f, plato.top), Size(plato.width * 0.45f, plato.height * 0.4f))
        val cana = Rect(vx - w * 0.006f, cima - w * 0.028f, vx + w * 0.006f, cima - w * 0.005f)
        drawRect(am.luz(P.HIERRO, k), cana.topLeft, cana.size)
        drawRect(am.brillo(0.4f, k), cana.topLeft, Size(1.2f * u, cana.height))
        val bandeja = Rect(vx - w * 0.022f, cana.top - w * 0.005f, vx + w * 0.022f, cana.top + w * 0.002f)
        drawOval(am.luz(P.HIERRO, k), bandeja.topLeft, bandeja.size)
        // La vela: gruesa, con sus anillos de horas y los chorreones.
        val vela = Rect(vx - w * 0.016f, w * (m.VELA_Y + 0.016f), vx + w * 0.016f, bandeja.top + w * 0.002f)
        drawRect(am.luz(P.CERA, k), vela.topLeft, vela.size)
        drawRect(am.brillo(0.35f, k), Offset(vela.left + vela.width * 0.12f, vela.top), Size(vela.width * 0.2f, vela.height))
        drawRect(am.sombra(0.28f), Offset(vela.right - vela.width * 0.3f, vela.top), Size(vela.width * 0.3f, vela.height))
        var y = vela.top + vela.height * 0.2f
        var j = 0
        while (y < vela.bottom - 2f * u) {
            drawRect(am.luz(if (j % 2 == 0) P.RUBIA else P.OCRE_OSC, k).copy(alpha = 0.8f), Offset(vela.left, y), Size(vela.width, 1.2f * u))
            y += vela.height * 0.18f; j++
        }
        // Los chorreones de cera por el costado de la luz y la boca derretida.
        listOf(0.2f to 0.35f, 0.62f to 0.22f).forEach { (fx, largo) ->
            val x = vela.left + vela.width * fx
            drawRoundRect(am.luz(P.CERA, k), Offset(x, vela.top - 1f * u), Size(w * 0.006f, vela.height * largo), CornerRadius(w * 0.003f))
            drawRect(am.brillo(0.3f, k), Offset(x, vela.top), Size(1f * u, vela.height * largo * 0.8f))
        }
        drawOval(am.luz(mezcla(P.CERA, P.CERA_OSC, 0.5f), k), Offset(vela.left, vela.top - w * 0.005f), Size(vela.width, w * 0.01f))
        // La mecha y, de noche, la llama.
        val mecha = Offset(vx, vela.top - w * 0.004f)
        drawLine(Color(0xFF1C1612), mecha + Offset(0f, w * 0.004f), mecha - Offset(0f, w * 0.006f), 1.3f * u, StrokeCap.Round)
        if (am.lumbre) llamaAntiguas(Offset(vx, w * m.VELA_Y), w * (if (am.noche) 0.017f else 0.022f), am)
    }

    /** Un tomo tumbado con el lomo hacia aquí: el cuero con su luz arriba, los nervios, los filetes dorados y las cantoneras. */
    private fun DrawScope.tomoTumbado(r: Rect, cuero: Color, am: AireAntiguas, k: Float, s: Int) {
        val u = density
        drawRoundRect(am.luz(cuero, k), r.topLeft, r.size, CornerRadius(r.height * 0.2f))
        drawRect(am.claro(0.22f), Offset(r.left + r.height * 0.2f, r.top + r.height * 0.1f), Size(r.width - r.height * 0.4f, r.height * 0.22f))
        drawRect(am.sombra(0.32f), Offset(r.left, r.bottom - r.height * 0.28f), Size(r.width, r.height * 0.28f))
        // Los nervios (corren a lo alto, porque el libro está tumbado) y los filetes.
        for (i in 1..4) {
            val x = r.left + r.width * (0.18f + 0.16f * i)
            drawRect(am.sombra(0.35f), Offset(x + 1.2f * u, r.top), Size(1.4f * u, r.height))
            drawRect(am.luz(mezcla(cuero, Color.White, 0.2f), k), Offset(x - 1f * u, r.top), Size(2f * u, r.height))
        }
        drawRect(am.luz(P.ORO, k), Offset(r.left + r.width * 0.26f, r.top + r.height * 0.3f), Size(r.width * 0.5f, 0.9f * u))
        drawRect(am.luz(P.ORO, k), Offset(r.left + r.width * 0.26f, r.bottom - r.height * 0.32f), Size(r.width * 0.5f, 0.9f * u))
        // Las cantoneras de latón en las dos puntas.
        listOf(r.left, r.right - r.width * 0.07f).forEach { x ->
            drawRect(am.luz(P.LATON, k), Offset(x, r.top), Size(r.width * 0.07f, r.height))
            drawRect(am.brillo(0.5f, k), Offset(x, r.top), Size(r.width * 0.07f, 1f * u))
            drawRect(am.sombra(0.3f), Offset(x, r.bottom - 1.6f * u), Size(r.width * 0.07f, 1.6f * u))
        }
        if (am.oscuro && k > 0.1f) drawRect(P.LLAMA.copy(alpha = (0.5f * k).coerceAtMost(0.45f)), Offset(r.left, r.top), Size(r.width, 1.2f * u))
    }

    /** El cuerno de tinta en su taco de madera, con la pluma de ave metida, de pluma clara y punta oscura. */
    private fun DrawScope.tintero(pie: Offset, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(pie - Offset(0f, w * 0.05f))
        val taco = Rect(pie.x - w * 0.032f, pie.y - w * 0.026f, pie.x + w * 0.032f, pie.y)
        val cuerno = Path().apply {
            moveTo(pie.x - w * 0.014f, taco.top + w * 0.004f)
            cubicTo(pie.x - w * 0.016f, taco.top - w * 0.03f, pie.x - w * 0.002f, taco.top - w * 0.05f, pie.x + w * 0.018f, taco.top - w * 0.058f)
            lineTo(pie.x + w * 0.024f, taco.top - w * 0.048f)
            cubicTo(pie.x + w * 0.008f, taco.top - w * 0.038f, pie.x + w * 0.012f, taco.top - w * 0.01f, pie.x + w * 0.014f, taco.top + w * 0.004f)
            close()
        }
        // La pluma: el cañón y las barbas, inclinada hacia la derecha.
        val p0 = Offset(pie.x + w * 0.02f, taco.top - w * 0.052f); val p1 = Offset(pie.x + w * 0.072f, taco.top - w * 0.165f)
        val pluma = Path().apply {
            moveTo(p0.x, p0.y)
            quadraticTo(p0.x + w * 0.04f, p0.y - w * 0.05f, p1.x + w * 0.006f, p1.y)
            quadraticTo(p0.x + w * 0.012f, p0.y - w * 0.07f, p0.x, p0.y)
            close()
        }
        val silueta = Path().apply { addRect(taco); addPath(cuerno); addPath(pluma) }
        sombraEnPared(silueta, pie - Offset(0f, w * 0.06f), g, 0.26f)
        contactoAntiguas(Offset(pie.x + w * 0.006f, pie.y), w * 0.075f, w * 0.01f, am, 0.42f)
        drawPath(pluma, am.luz(Color(0xFFE6E2D6), k))
        drawLine(am.luz(Color(0xFFB8B2A2), k), p0, Offset(p1.x + w * 0.004f, p1.y + w * 0.004f), 0.9f * u)
        clipPath(pluma) { drawRect(am.luz(Color(0xFF3A3530), k), Offset(p1.x - w * 0.03f, p1.y - w * 0.01f), Size(w * 0.06f, w * 0.03f)) }
        drawRoundRect(am.luz(P.ROBLE, k), taco.topLeft, taco.size, CornerRadius(w * 0.004f))
        drawRect(am.luz(P.ROBLE_LUZ, k), taco.topLeft, Size(taco.width, w * 0.005f))
        drawRect(am.sombra(0.35f), Offset(taco.right - taco.width * 0.25f, taco.top), Size(taco.width * 0.25f, taco.height))
        drawPath(cuerno, am.luz(Color(0xFFD9C08C), k))
        clipPath(cuerno) {
            drawRect(am.luz(Color(0xFF6A4A2A), k), Offset(pie.x - w * 0.02f, taco.top - w * 0.012f), Size(w * 0.05f, w * 0.016f))
            drawRect(am.sombra(0.3f), Offset(pie.x + w * 0.004f, taco.top - w * 0.06f), Size(w * 0.03f, w * 0.07f))
        }
        drawOval(am.luz(Color(0xFF1A140F), k), Offset(pie.x + w * 0.014f, taco.top - w * 0.06f), Size(w * 0.012f, w * 0.008f))
    }

    /** La luz de la vela en lo de arriba: el halo cálido en la pared y en lo cercano (de mezcla de pantalla: aclara, no vela). */
    private fun DrawScope.luzVela(g: MarcoAntiguas) {
        val a = g.a
        if (!a.lumbre) return
        val w = g.w
        val v = g.vela
        val f = if (!a.oscuro) 0.35f else if (a.noche) 0.6f else 1f
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB04C).copy(alpha = 0.3f * f), Color(0xFFFFB04C).copy(alpha = 0.07f * f), Color.Transparent), v, w * 0.2f), w * 0.2f, v, blendMode = BlendMode.Screen)
        mancha(v, w * 0.06f, P.FUEGO.copy(alpha = 0.4f * f))
    }
}

/** Una llama de vela en [c] de alto [alto]: el halo, el cuerpo naranja, el corazón claro y la base azulada. */
internal fun DrawScope.llamaAntiguas(c: Offset, alto: Float, a: AireAntiguas) {
    val P = PaletaAntiguas
    val an = alto * 0.42f
    mancha(c, alto * 2.2f, P.LLAMA.copy(alpha = 0.45f))
    val cuerpo = Path().apply {
        moveTo(c.x, c.y - alto * 0.62f)
        cubicTo(c.x + an * 0.55f, c.y - alto * 0.1f, c.x + an * 0.7f, c.y + alto * 0.32f, c.x, c.y + alto * 0.4f)
        cubicTo(c.x - an * 0.7f, c.y + alto * 0.32f, c.x - an * 0.55f, c.y - alto * 0.1f, c.x, c.y - alto * 0.62f)
        close()
    }
    drawPath(cuerpo, P.LLAMA)
    drawOval(P.FUEGO, Offset(c.x - an * 0.28f, c.y - alto * 0.12f), Size(an * 0.56f, alto * 0.48f))
    drawOval(Color(0xFF7A8CC8).copy(alpha = 0.6f), Offset(c.x - an * 0.2f, c.y + alto * 0.22f), Size(an * 0.4f, alto * 0.16f))
}
