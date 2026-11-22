package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El bodegón de encima del estante de Romano ([MuebleEstanteRomano]): en la pared, la patera de bronce colgada de un clavo;
 * sobre la cornisa, el sagum de lana
 * roja doblado con su fíbula y un pliegue que cae por el canto; el modio grande de medir el grano, de duelas con sus aros de
 * bronce y el trigo colmado, con el rasero apoyado; delante, la romana de bronce colgada de la pared que pesa la ración; la
 * jarra de la posca; y la linterna de cuerno colgada de su brazo de hierro, que de noche arde.
 */
internal object CoronaEstanteRomano {
    private val P = PaletaRomano

    /** Hacia dónde cae la sombra en la pared de algo que está en [p]: de día, a la derecha y abajo; de noche, lejos de la linterna. */
    private fun DrawScope.caida(p: Offset, largo: Float, a: AireRomano): Offset {
        if (!a.oscuro) return Offset(largo * 1.1f, largo * 0.6f)
        val llama = Offset(size.width * MuebleEstanteRomano.LAMP_X, size.width * MuebleEstanteRomano.LAMP_Y)
        val v = p - llama
        return v / v.getDistance().coerceAtLeast(1f) * (largo * 1.8f)
    }

    /** La sombra de [forma] en la pared, corrida según la luz. */
    private fun DrawScope.sombraEnPared(forma: Path, desde: Offset, largo: Float, a: AireRomano, fuerza: Float = 0.22f) {
        val c = caida(desde, largo, a)
        translate(c.x, c.y) { drawPath(forma, a.sombra(fuerza)) }
    }

    /** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): la luz hacia la luz y la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, a: AireRomano, k: Float, der: Boolean, fuerza: Float = 1f) {
        fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
        drawRect(a.brillo(0.18f * fuerza, k), Offset(x(0f, 0.32f), r.top), Size(r.width * 0.32f, r.height))
        drawRect(a.brillo(0.34f * fuerza, k), Offset(x(0.14f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(a.sombra(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(a.sombra(0.24f * fuerza), Offset(x(0.82f, 0.18f), r.top), Size(r.width * 0.18f, r.height))
    }

    /** Un destello en el bronce: de día crema; de noche, de la llama. */
    private fun DrawScope.destello(c: Offset, r: Float, a: AireRomano, k: Float) {
        if (a.oscuro) { if (k > 0.06f) { drawCircle(P.LLAMA.copy(alpha = (0.35f * k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(P.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(a.claro(0.85f), r, c)
    }

    // ---------------------------------------------------------------- en la pared

    fun DrawScope.pared(a: AireRomano, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width
        patera(a, Offset(w * 0.765f, w * 0.288f), w * 0.042f, cerca)
    }

    /**
     * La patera de bronce colgada de un clavo por el agujero de su mango: el plato redondo visto de frente, con su labio, el
     * hondo, el ombligo y el brillo; su sombra en el ocre.
     */
    private fun DrawScope.patera(a: AireRomano, clavo: Offset, r: Float, cerca: (Offset) -> Float) {
        val u = density
        val c = clavo + Offset(0f, r * 1.55f)
        val k = cerca(c)
        val sc = caida(c, 3f * u, a)
        drawCircle(a.sombra(0.25f), r, c + sc)
        drawLine(a.sombra(0.2f), clavo + sc, c + sc, r * 0.32f)
        // El mango, que sube hasta el clavo.
        drawLine(a.luz(P.BRONCE, k), clavo + Offset(0f, r * 0.1f), c, r * 0.3f, cap = StrokeCap.Round)
        drawLine(a.brillo(0.45f, k), clavo + Offset(-r * 0.08f, r * 0.15f), c + Offset(-r * 0.08f, -r * 0.6f), 0.7f * u)
        drawCircle(a.luz(P.TINTA, k), r * 0.08f, clavo + Offset(0f, r * 0.15f))
        remacheRomano(clavo, 1.5f * u, a, k)
        // El plato.
        drawCircle(a.luz(P.BRONCE, k), r, c)
        drawCircle(a.luz(mezcla(P.BRONCE, P.TINTA, 0.35f), k), r * 0.8f, c)
        drawCircle(a.luz(P.BRONCE_LUZ, k), r * 0.86f, c, style = Stroke(0.9f * u))
        drawArc(a.brillo(0.6f, k), 195f, 80f, false, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(1.3f * u))
        drawArc(a.sombra(0.3f), 15f, 120f, false, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f), style = Stroke(1.4f * u))
        drawCircle(a.luz(P.BRONCE, k), r * 0.22f, c)
        drawCircle(a.brillo(0.55f, k), r * 0.08f, c - Offset(r * 0.07f, r * 0.07f))
        destello(c + Offset(-r * 0.45f, -r * 0.4f), 1f * u, a, k)
    }

    // ---------------------------------------------------------------- encima de la cornisa

    fun DrawScope.encima(a: AireRomano, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width
        sagum(a, Rect(w * 0.02f, apoyo - w * 0.075f, w * 0.25f, apoyo), cerca)
        modio(a, Offset(w * 0.43f, apoyo), w * 0.27f, w * 0.2f, cerca)
        jarra(a, Offset(w * 0.685f, apoyo), w * 0.088f, cerca)
        romana(a, Offset(w * 0.575f, w * 0.07f), w * 1.42f, cerca)
        linterna(a)
    }

    /**
     * El sagum de lana roja echado sobre la cornisa: un bulto blando con sus lomos de luz y sus hondos, la trama de la lana,
     * la orilla con su franja, la fíbula de bronce prendida y el pliegue que cae por el canto de la cornisa, con sus ondas.
     */
    private fun DrawScope.sagum(a: AireRomano, r: Rect, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(r.center)
        val lana = a.luz(P.LANA, k)
        val honda = a.luz(mezcla(P.LANA, P.TINTA, 0.45f), k)
        contactoRomano(Offset(r.center.x, r.bottom), r.width * 1.05f, 5f * u, a)
        // El pliegue que cae por delante del canto de la cornisa.
        val cae = Path().apply {
            moveTo(r.left + r.width * 0.08f, r.bottom - 2f * u)
            lineTo(r.left + r.width * 0.5f, r.bottom - 2f * u)
            cubicTo(r.left + r.width * 0.52f, r.bottom + r.height * 0.6f, r.left + r.width * 0.42f, r.bottom + r.height * 1.1f, r.left + r.width * 0.47f, r.bottom + r.height * 1.45f)
            quadraticTo(r.left + r.width * 0.34f, r.bottom + r.height * 1.55f, r.left + r.width * 0.22f, r.bottom + r.height * 1.4f)
            quadraticTo(r.left + r.width * 0.12f, r.bottom + r.height * 1.52f, r.left + r.width * 0.06f, r.bottom + r.height * 1.32f)
            cubicTo(r.left + r.width * 0.1f, r.bottom + r.height * 0.8f, r.left + r.width * 0.04f, r.bottom + r.height * 0.4f, r.left + r.width * 0.08f, r.bottom - 2f * u)
            close()
        }
        drawPath(corridoRomano(cae, 2.5f * u, 2f * u), a.sombra(0.3f))
        drawPath(cae, lana)
        clipPath(cae) {
            for ((i, f) in listOf(0.18f, 0.33f, 0.44f).withIndex()) {
                val x = r.left + r.width * f
                drawLine(honda.copy(alpha = 0.7f), Offset(x, r.bottom), Offset(x + (i - 1) * 2f * u, r.bottom + r.height * 1.5f), 2.4f * u)
                drawLine(a.brillo(0.25f, k), Offset(x - 2.4f * u, r.bottom), Offset(x - 2.4f * u + (i - 1) * 2f * u, r.bottom + r.height * 1.5f), 1.2f * u)
            }
            drawRect(a.sombra(0.25f), Offset(r.left, r.bottom), Size(r.width, r.height * 0.25f))
        }
        // El bulto de lana echado sobre la cornisa: un solo paño blando, con sus lomos y sus hondos, sin cantos.
        val lomo = Path().apply {
            moveTo(r.left + r.width * 0.02f, r.bottom)
            cubicTo(r.left - r.width * 0.02f, r.top + r.height * 0.55f, r.left + r.width * 0.1f, r.top + r.height * 0.1f, r.left + r.width * 0.3f, r.top + r.height * 0.18f)
            cubicTo(r.left + r.width * 0.42f, r.top - r.height * 0.05f, r.left + r.width * 0.62f, r.top + r.height * 0.02f, r.left + r.width * 0.72f, r.top + r.height * 0.22f)
            cubicTo(r.left + r.width * 0.86f, r.top + r.height * 0.2f, r.right + r.width * 0.02f, r.top + r.height * 0.5f, r.right - r.width * 0.02f, r.bottom)
            close()
        }
        drawPath(corridoRomano(lomo, 3f * u, 1f * u), a.sombra(0.25f))
        drawPath(lomo, lana)
        clipPath(lomo) {
            // La luz en los lomos y los hondos entre ellos.
            drawOval(a.brillo(0.3f, k), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.12f), Size(r.width * 0.3f, r.height * 0.35f))
            drawOval(a.brillo(0.22f, k), Offset(r.left + r.width * 0.45f, r.top + r.height * 0.05f), Size(r.width * 0.25f, r.height * 0.3f))
            for ((i, f) in listOf(0.3f, 0.68f).withIndex()) {
                val pl = Path().apply {
                    moveTo(r.left + r.width * f, r.top + r.height * (0.15f + 0.05f * i))
                    quadraticTo(r.left + r.width * (f + 0.06f), r.top + r.height * 0.55f, r.left + r.width * (f - 0.02f), r.bottom)
                }
                drawPath(pl, honda.copy(alpha = 0.7f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
                drawPath(corridoRomano(pl, -2f * u, 0f), a.brillo(0.2f, k), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            }
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - r.height * 0.25f), Size(r.width, r.height * 0.25f))
            drawRect(a.sombra(0.2f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
            // La trama de la lana.
            var tx = r.left + 2f * u
            var n = 0
            while (tx < r.right && n < 60) { drawLine(honda.copy(alpha = 0.18f), Offset(tx, r.top), Offset(tx + 2f * u, r.bottom), 0.5f * u); tx += 3f * u; n++ }
        }
        // La orilla tejida, con su franja.
        drawLine(a.luz(P.OCRE, k).copy(alpha = 0.7f), Offset(r.left + r.width * 0.1f, r.bottom - 2f * u), Offset(r.left + r.width * 0.5f, r.bottom - 2f * u), 1.2f * u)
        // La fíbula de bronce prendida en el paño.
        val fb = Offset(r.left + r.width * 0.6f, r.top + r.height * 0.32f)
        drawCircle(a.sombra(0.35f), r.height * 0.16f, fb + Offset(1.2f * u, 1.2f * u))
        drawCircle(a.luz(P.BRONCE, k), r.height * 0.16f, fb)
        drawCircle(a.luz(P.BRONCE_OSC, k), r.height * 0.08f, fb)
        destello(fb - Offset(r.height * 0.06f, r.height * 0.06f), 0.9f * u, a, k)
        drawLine(a.luz(P.BRONCE_LUZ, k), fb + Offset(-r.height * 0.4f, r.height * 0.05f), fb + Offset(r.height * 0.3f, -r.height * 0.02f), 1f * u)
    }

    /**
     * El modio de medir el grano: un cubo de duelas de roble, más ancho abajo, con su luz y su sombra redondas, tres aros de
     * bronce con sus clavos y su brillo, el trigo colmado con su grano, el rasero apoyado; su sombra en la pared y en la cornisa.
     */
    private fun DrawScope.modio(a: AireRomano, pie: Offset, ancho: Float, alto: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(pie + Offset(0f, -alto / 2))
        val der = a.oscuro && pie.x / size.width < MuebleEstanteRomano.LAMP_X
        val abajo = ancho / 2; val arriba = ancho * 0.41f
        val cuerpo = poligonoRomano(pie.x - abajo, pie.y, pie.x - arriba, pie.y - alto, pie.x + arriba, pie.y - alto, pie.x + abajo, pie.y)
        val colmo = Path().apply {
            moveTo(pie.x - arriba * 1.02f, pie.y - alto)
            quadraticTo(pie.x - arriba * 0.35f, pie.y - alto - ancho * 0.3f, pie.x + arriba * 0.15f, pie.y - alto - ancho * 0.27f)
            quadraticTo(pie.x + arriba * 0.7f, pie.y - alto - ancho * 0.14f, pie.x + arriba * 1.02f, pie.y - alto)
            close()
        }
        sombraEnPared(cuerpo, pie, alto * 0.16f, a)
        sombraEnPared(colmo, pie, alto * 0.16f, a)
        contactoRomano(pie + Offset(ancho * 0.08f, 0f), ancho * 1.2f, 6f * u, a, 0.42f)
        // El rasero, apoyado a la izquierda.
        val r0 = pie + Offset(-abajo - ancho * 0.1f, 0f); val r1 = pie + Offset(-arriba - ancho * 0.02f, -alto * 1.3f)
        drawLine(a.sombra(0.3f), r0 + Offset(3f * u, 0f), r1 + Offset(3f * u, 2f * u), 3.4f * u)
        drawLine(a.luz(Color(0xFFB8925E), k), r0, r1, 3.4f * u, cap = StrokeCap.Round)
        drawLine(a.brillo(0.35f, k), r0 + Offset(-1f * u, 0f), r1 + Offset(-1f * u, 0f), 0.9f * u)
        // Las duelas.
        drawPath(cuerpo, a.luz(P.ROBLE, k))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - abajo, pie.y - alto, pie.x + abajo, pie.y), a, k, der, 1.2f)
            for (j in 1 until 9) {
                val f = j / 9f
                drawLine(a.luz(P.ROBLE_OSC, k).copy(alpha = 0.6f), Offset(pie.x - abajo + ancho * f, pie.y), Offset(pie.x - arriba + arriba * 2 * f, pie.y - alto), 0.8f * u)
                drawLine(a.brillo(0.15f, k), Offset(pie.x - abajo + ancho * f + 1.2f * u, pie.y), Offset(pie.x - arriba + arriba * 2 * f + 1.2f * u, pie.y - alto), 0.5f * u)
            }
            if (a.oscuro && k > 0.06f) drawRect(a.filo(0.5f * k), Offset(pie.x + abajo * 0.85f, pie.y - alto), Size(abajo * 0.2f, alto))
        }
        // Los aros de bronce, con su brillo y sus clavos.
        for (f in listOf(0.1f, 0.5f, 0.9f)) {
            val y = pie.y - alto * f
            val m = abajo + (arriba - abajo) * f
            val alto2 = 3.2f * u
            drawRect(a.sombra(0.35f), Offset(pie.x - m, y + alto2 / 2), Size(m * 2, 1.2f * u))
            drawRect(a.luz(P.BRONCE, k), Offset(pie.x - m, y - alto2 / 2), Size(m * 2, alto2))
            drawRect(a.brillo(0.5f, k), Offset(pie.x - m * (if (der) -0.1f else 0.85f), y - alto2 / 2), Size(m * 0.75f, 1f * u))
            destello(Offset(pie.x + m * (if (der) 0.55f else -0.55f), y - alto2 * 0.2f), 1f * u, a, k)
            for (cx in listOf(-0.6f, 0f, 0.6f)) remacheRomano(Offset(pie.x + m * cx, y), 0.9f * u, a, k, P.BRONCE_OSC)
        }
        // El trigo colmado, con su luz y su grano.
        drawPath(colmo, a.luz(P.TRIGO, k))
        clipPath(colmo) {
            drawRect(a.sombra(0.22f), Offset(pie.x + arriba * 0.15f, pie.y - alto - ancho * 0.3f), Size(arriba, ancho * 0.3f))
            drawRect(a.brillo(0.3f, k), Offset(pie.x - arriba, pie.y - alto - ancho * 0.3f), Size(arriba * 0.6f, ancho * 0.3f))
            for (j in 0 until 34) {
                val x = pie.x - arriba + 2 * arriba * azar(91, j)
                val y = pie.y - alto - ancho * 0.28f * azar(92, j)
                drawOval(a.luz(if (j % 3 == 0) Color(0xFFA77A35) else Color(0xFFEDCB7E), k), Offset(x, y), Size(1.8f * u, 1.1f * u))
            }
        }
        drawOval(a.luz(P.ROBLE_OSC, k), Offset(pie.x - arriba, pie.y - alto - 1.5f * u), Size(arriba * 2, 3f * u), style = Stroke(1f * u))
        // Granos sueltos en la cornisa.
        for (j in 0 until 8) drawOval(a.luz(P.TRIGO, k), Offset(pie.x + abajo + ancho * 0.45f * azar(93, j), pie.y - 1.4f * u), Size(1.6f * u, 1f * u))
    }

    /**
     * La romana de bronce colgada de una estaca de hierro en la pared: el brazo con sus marcas, el gancho, el platillo colgado de
     * tres cadenitas con un puñado de grano y la pesa en bellota que corre por el brazo, todo con su brillo; su sombra en el ocre.
     */
    private fun DrawScope.romana(a: AireRomano, clavo: Offset, e: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(clavo + Offset(0f, e * 0.12f))
        val fiel = clavo + Offset(0f, e * 0.045f)
        val corto = fiel + Offset(-e * 0.045f, e * 0.006f)
        val largo = fiel + Offset(e * 0.16f, -e * 0.012f)
        val plato = corto + Offset(0f, e * 0.13f)
        val pesa = largo + Offset(-e * 0.04f, 0f)
        val sombra = caida(fiel, 3f * u, a)
        for (fase in 0..1) {
            val enSombra = fase == 0
            val o = if (enSombra) sombra else Offset.Zero
            val bronce = if (enSombra) a.sombra(0.2f) else a.luz(P.BRONCE, k)
            val cadena = if (enSombra) a.sombra(0.16f) else a.luz(P.BRONCE_OSC, k)
            drawLine(cadena, clavo + o, fiel + o, 1f * u)
            drawLine(bronce, corto + o, largo + o, 2.8f * u, cap = StrokeCap.Round)
            for (dx in listOf(-e * 0.034f, 0f, e * 0.034f)) drawLine(cadena, corto + o, Offset(plato.x + dx, plato.y) + o, 0.7f * u)
            val bol = Rect(plato.x - e * 0.043f + o.x, plato.y + o.y, plato.x + e * 0.043f + o.x, plato.y + e * 0.026f + o.y)
            drawArc(bronce, 0f, 180f, true, Offset(bol.left, bol.top - bol.height), Size(bol.width, bol.height * 2))
            val g = pesa + o
            drawLine(cadena, g, g + Offset(0f, e * 0.03f), 0.9f * u)
            drawOval(bronce, Offset(g.x - e * 0.017f, g.y + e * 0.028f), Size(e * 0.034f, e * 0.044f))
            if (!enSombra) {
                // El brillo del brazo y sus marcas a punzón.
                drawLine(a.brillo(0.6f, k), corto + Offset(2f * u, -1f * u), largo + Offset(-2f * u, -1f * u), 0.7f * u)
                for (j in 0 until 10) {
                    val f = 0.25f + 0.075f * j
                    val p = Offset(corto.x + (largo.x - corto.x) * f, corto.y + (largo.y - corto.y) * f)
                    drawLine(a.luz(P.BRONCE_OSC, k), p + Offset(0f, -1.7f * u), p + Offset(0f, 1.4f * u), 0.5f * u)
                }
                // El platillo: luz a la izquierda, sombra a la derecha, el labio y el grano.
                clipPath(Path().apply { addArc(Rect(bol.left, bol.top - bol.height, bol.right, bol.bottom), 0f, 180f) }) {
                    drawRect(a.brillo(0.35f, k), Offset(bol.left, bol.top), Size(bol.width * 0.3f, bol.height))
                    drawRect(a.sombra(0.3f), Offset(bol.right - bol.width * 0.35f, bol.top), Size(bol.width * 0.35f, bol.height))
                }
                drawArc(a.luz(P.TRIGO, k), 180f, 180f, true, Offset(bol.left + bol.width * 0.12f, bol.top - bol.height * 0.45f), Size(bol.width * 0.76f, bol.height * 0.9f))
                drawOval(a.luz(P.BRONCE_LUZ, k), Offset(bol.left, bol.top - 1f * u), Size(bol.width, 2f * u), style = Stroke(0.9f * u))
                destello(Offset(bol.left + bol.width * 0.2f, bol.top + bol.height * 0.35f), 1.1f * u, a, k)
                // La bellota: su cúpula y su brillo.
                drawArc(a.luz(P.BRONCE_OSC, k), 180f, 180f, true, Offset(g.x - e * 0.019f, g.y + e * 0.022f), Size(e * 0.038f, e * 0.02f))
                drawOval(a.brillo(0.45f, k), Offset(g.x - e * 0.012f, g.y + e * 0.04f), Size(e * 0.008f, e * 0.018f))
                drawOval(a.sombra(0.25f), Offset(g.x + e * 0.002f, g.y + e * 0.035f), Size(e * 0.013f, e * 0.03f))
                remacheRomano(clavo, 2f * u, a, k)
                drawCircle(a.luz(P.BRONCE_LUZ, k), 1.8f * u, fiel, style = Stroke(0.9f * u))
            }
        }
    }

    /** La jarra de barro de la posca: el cuerpo redondo con su luz y su sombra, el engobe claro, el asa y el labio; el vaso volcado. */
    private fun DrawScope.jarra(a: AireRomano, pie: Offset, ancho: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(pie + Offset(0f, -ancho))
        val der = a.oscuro && pie.x / size.width < MuebleEstanteRomano.LAMP_X
        val alto = ancho * 1.35f
        val cuerpo = Path().apply {
            moveTo(pie.x - ancho * 0.22f, pie.y)
            cubicTo(pie.x - ancho * 0.62f, pie.y - alto * 0.2f, pie.x - ancho * 0.58f, pie.y - alto * 0.72f, pie.x - ancho * 0.14f, pie.y - alto * 0.8f)
            lineTo(pie.x - ancho * 0.12f, pie.y - alto)
            lineTo(pie.x + ancho * 0.12f, pie.y - alto)
            lineTo(pie.x + ancho * 0.14f, pie.y - alto * 0.8f)
            cubicTo(pie.x + ancho * 0.58f, pie.y - alto * 0.72f, pie.x + ancho * 0.62f, pie.y - alto * 0.2f, pie.x + ancho * 0.22f, pie.y)
            close()
        }
        sombraEnPared(cuerpo, pie, ancho * 0.2f, a)
        contactoRomano(pie + Offset(ancho * 0.1f, 0f), ancho * 1.1f, 4f * u, a)
        drawArc(a.luz(P.BARRO, k), -80f, 170f, false, Offset(pie.x + ancho * 0.05f, pie.y - alto * 0.95f), Size(ancho * 0.5f, alto * 0.5f), style = Stroke(2.6f * u))
        drawPath(cuerpo, a.luz(P.BARRO, k))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - ancho * 0.6f, pie.y - alto, pie.x + ancho * 0.6f, pie.y), a, k, der, 1.2f)
            drawRect(a.luz(Color(0xFFDDAA80), k).copy(alpha = 0.55f), Offset(pie.x - ancho, pie.y - alto * 0.64f), Size(ancho * 2, 1.4f * u))
            drawRect(a.luz(P.CINABRIO_OSC, k).copy(alpha = 0.4f), Offset(pie.x - ancho, pie.y - alto * 0.6f), Size(ancho * 2, 0.8f * u))
        }
        destello(Offset(pie.x - ancho * 0.28f, pie.y - alto * 0.55f), 1.2f * u, a, k)
        drawRect(a.luz(mezcla(P.BARRO, P.TINTA, 0.3f), k), Offset(pie.x - ancho * 0.16f, pie.y - alto - 1.2f * u), Size(ancho * 0.32f, 2f * u))
        if (a.oscuro && k > 0.06f) drawOval(a.filo(0.5f * k), Offset(pie.x + ancho * 0.3f, pie.y - alto * 0.6f), Size(ancho * 0.1f, alto * 0.4f))
        // El vaso volcado.
        val v = Offset(pie.x + ancho * 0.85f, pie.y - ancho * 0.16f)
        val vaso = poligonoRomano(v.x - ancho * 0.24f, v.y - ancho * 0.16f, v.x + ancho * 0.16f, v.y - ancho * 0.12f, v.x + ancho * 0.16f, v.y + ancho * 0.12f, v.x - ancho * 0.24f, v.y + ancho * 0.16f)
        drawPath(vaso, a.luz(P.SIGILLATA, k))
        drawRect(a.brillo(0.35f, k), Offset(v.x - ancho * 0.22f, v.y - ancho * 0.13f), Size(ancho * 0.36f, 1f * u))
        drawOval(a.luz(mezcla(P.SIGILLATA, P.TINTA, 0.5f), k), Offset(v.x - ancho * 0.28f, v.y - ancho * 0.16f), Size(ancho * 0.1f, ancho * 0.32f))
    }

    // ---------------------------------------------------------------- la linterna

    /**
     * La linterna de cuerno colgada del brazo de hierro clavado en la pared: la cadenita, el sombrerete de bronce con su anilla,
     * los montantes y los paños de cuerno raspado, ámbar y turbios, y el plato de abajo. De día está apagada (se ve el pabilo);
     * de noche arde: los paños se encienden y la llama se adivina detrás.
     */
    fun DrawScope.linterna(a: AireRomano) {
        val u = density
        val w = size.width
        val c = Offset(w * MuebleEstanteRomano.LAMP_X, w * MuebleEstanteRomano.LAMP_Y)
        val an = w * 0.095f
        val cuerpo = Rect(c.x - an / 2, c.y - w * 0.068f, c.x + an / 2, c.y + w * 0.062f)
        val k = 1f
        val muro = Offset(w * 0.955f, w * 0.15f)
        val gancho = Offset(c.x, w * 0.15f)
        drawLine(a.sombra(0.3f), muro + Offset(2f * u, 2f * u), gancho + Offset(2f * u, 2f * u), 2.4f * u)
        drawLine(a.luz(P.HIERRO, k), muro, gancho, 2.4f * u, cap = StrokeCap.Round)
        drawLine(a.luz(P.HIERRO, k), muro, muro + Offset(-w * 0.035f, w * 0.035f), 1.8f * u)
        drawLine(a.brillo(0.35f, k), muro + Offset(0f, -1f * u), gancho + Offset(0f, -1f * u), 0.7f * u)
        remacheRomano(muro, 2.4f * u, a, k)
        val tapa = cuerpo.top
        val anilla = Offset(c.x, tapa - w * 0.034f - 3f * u)
        var cy = gancho.y + 2f * u
        var n = 0
        while (cy < anilla.y - 3f * u && n < 30) {
            drawOval(a.luz(P.HIERRO, k), Offset(c.x - (if (n % 2 == 0) 1.3f else 0.6f) * u, cy), Size((if (n % 2 == 0) 2.6f else 1.2f) * u, 3.4f * u), style = Stroke(0.8f * u))
            cy += 2.8f * u; n++
        }
        drawCircle(a.luz(P.BRONCE, k), 3.2f * u, anilla, style = Stroke(1.3f * u))
        if (!a.lumbre) drawRect(a.sombra(0.22f), Offset(cuerpo.left + w * 0.02f, cuerpo.top + w * 0.012f), cuerpo.size)
        val sombrero = poligonoRomano(c.x - an * 0.6f, tapa, c.x - an * 0.12f, tapa - w * 0.034f, c.x + an * 0.12f, tapa - w * 0.034f, c.x + an * 0.6f, tapa)
        drawPath(sombrero, a.luz(P.BRONCE, k))
        drawPath(poligonoRomano(c.x - an * 0.6f, tapa, c.x - an * 0.12f, tapa - w * 0.034f, c.x - an * 0.02f, tapa - w * 0.034f, c.x - an * 0.25f, tapa), a.brillo(0.35f, 0.9f))
        // Los agujeros del sombrerete por donde sale el humo.
        for (j in 0 until 3) drawCircle(a.luz(P.TINTA, k), 1f * u, Offset(c.x + (j - 1) * an * 0.18f, tapa - w * 0.012f))
        val pano = Rect(cuerpo.left + 1f * u, tapa + 1.5f * u, cuerpo.right - 1f * u, cuerpo.bottom - 2f * u)
        if (a.lumbre) {
            drawRect(if (a.oscuro) P.LLAMA else mezcla(P.LLAMA, P.CUERNO, 0.3f), pano.topLeft, pano.size)
            mancha(Offset(c.x, pano.center.y + pano.height * 0.1f), pano.width * 0.75f, P.FUEGO.copy(alpha = 0.95f))
            val llama = Path().apply {
                moveTo(c.x - 2.6f * u, pano.bottom - pano.height * 0.22f)
                quadraticTo(c.x - 2.8f * u, pano.center.y, c.x + 0.4f * u, pano.top + pano.height * 0.2f)
                quadraticTo(c.x + 3f * u, pano.center.y, c.x + 2.6f * u, pano.bottom - pano.height * 0.22f)
                close()
            }
            drawPath(llama, Color.White.copy(alpha = 0.8f))
        } else {
            drawRect(P.CUERNO, pano.topLeft, pano.size)
            drawLine(P.TINTA.copy(alpha = 0.55f), Offset(c.x, pano.bottom - pano.height * 0.18f), Offset(c.x + 0.6f * u, pano.bottom - pano.height * 0.36f), 1.2f * u)
            drawRect(a.claro(0.4f), Offset(pano.left + pano.width * 0.1f, pano.top), Size(pano.width * 0.12f, pano.height))
        }
        for (j in 0 until 2) drawLine(Color(0xFF7A5424).copy(alpha = 0.22f), Offset(pano.left, pano.top + pano.height * (0.3f + 0.35f * j)), Offset(pano.right, pano.top + pano.height * (0.36f + 0.3f * j)), 0.6f * u)
        drawRect(Color(0xFF5A3A18).copy(alpha = 0.3f), Offset(pano.right - pano.width * 0.2f, pano.top), Size(pano.width * 0.2f, pano.height))
        drawRect(Color(0xFF5A3A18).copy(alpha = 0.18f), Offset(pano.left, pano.top), Size(pano.width * 0.1f, pano.height))
        val monta = if (a.lumbre) mezcla(P.BRONCE, P.TINTA, 0.45f) else a.luz(P.BRONCE, k)
        for (f in listOf(0f, 0.5f, 1f)) drawLine(monta, Offset(cuerpo.left + an * f, tapa), Offset(cuerpo.left + an * f, cuerpo.bottom), 1.8f * u)
        drawRect(monta, Offset(cuerpo.left - 0.5f * u, tapa), Size(an + 1f * u, 1.8f * u))
        drawOval(a.luz(mezcla(P.BRONCE, P.TINTA, 0.3f), k), Offset(cuerpo.left - 3f * u, cuerpo.bottom - 3f * u), Size(an + 6f * u, 5f * u))
        drawOval(a.brillo(0.4f, 0.9f), Offset(cuerpo.left - 2f * u, cuerpo.bottom - 3f * u), Size(an * 0.6f, 1.6f * u))
        if (a.lumbre) {
            drawLine(P.FUEGO.copy(alpha = 0.7f), Offset(cuerpo.left - 1f * u, cuerpo.bottom - 2.6f * u), Offset(cuerpo.right + 1f * u, cuerpo.bottom - 2.6f * u), 0.8f * u)
            drawPath(sombrero, P.LLAMA.copy(alpha = 0.22f))
        }
    }
}
