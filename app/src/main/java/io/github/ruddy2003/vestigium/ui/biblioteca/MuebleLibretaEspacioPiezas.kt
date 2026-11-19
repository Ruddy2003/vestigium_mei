package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * Lo que rodea la carpeta en la mesa del comedor de Espacio ([MuebleLibretaEspacio]): la bolsa de malla colgada del pasamanos
 * con la comida, la bandeja sujeta a la mesa, las tijeras atadas, la gota que se escapó, la cuchara y el sobre de sal pegados
 * con velcro, la lámpara de pinza y el piloto de guardia. Cada pieza recibe la luz en un punto ([luz]) y hacia dónde cae su
 * sombra ([caida]).
 */
internal object PiezasLibretaEspacio {
    private val P = PaletaEstanteEspacio
    private val trazo = Path()
    private val malla = Path()

    /** El papel de aluminio de las bolsas de comida, la tortilla y el verde de la etiqueta de las algas. */
    private val LAMINA = Color(0xFFC3C8D0)
    private val TORTILLA = Color(0xFFDDBE86)
    private val ALGA = Color(0xFF4F7F5A)
    private val TOSTADO = Color(0xFF9C6A2E)
    /** La toalla de rizo, del azul de la tela de la nave. */
    private val TOALLA = Color(0xFF6F86AA)

    /**
     * La bolsa de malla colgada del pasamanos con un mosquetón, abultada por lo que lleva: el paquete de tortillas (la
     * bolsa transparente con su disco tostado), una bolsa de lámina con su etiqueta naranja y otra de algas; la malla en
     * rombos encima, el dobladillo elástico arriba y su sombra en la pared.
     */
    fun DrawScope.bolsaMalla(r: Rect, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio) {
        val u = density
        val l = luz(r.center)
        trazo.reset()
        trazo.moveTo(r.left + r.width * 0.12f, r.top)
        trazo.lineTo(r.right - r.width * 0.12f, r.top)
        trazo.cubicTo(r.right + r.width * 0.06f, r.top + r.height * 0.3f, r.right, r.bottom - r.height * 0.1f, r.center.x + r.width * 0.12f, r.bottom)
        trazo.cubicTo(r.center.x, r.bottom + r.height * 0.05f, r.left + r.width * 0.05f, r.bottom - r.height * 0.05f, r.left, r.top + r.height * 0.5f)
        trazo.cubicTo(r.left - r.width * 0.02f, r.top + r.height * 0.3f, r.left + r.width * 0.04f, r.top + r.height * 0.1f, r.left + r.width * 0.12f, r.top)
        trazo.close()
        translate(4f * u, 4f * u) { drawPath(trazo, a.sombra(0.22f)) }
        clipPath(trazo) {
            drawRect(a.luz(mezcla(P.GOMA, P.ACOLCHADO, 0.3f), l).copy(alpha = 0.35f), r.topLeft, r.size)
            // Lo de dentro: el paquete de tortillas, de lado; la bolsa de lámina y la de algas.
            rotate(-14f, r.center) {
                val t = Rect(r.left + r.width * 0.05f, r.top + r.height * 0.2f, r.left + r.width * 0.62f, r.top + r.height * 0.86f)
                drawRoundRect(a.luz(mezcla(P.LUZ_FRIA, LAMINA, 0.4f), l).copy(alpha = 0.7f), t.topLeft, t.size, CornerRadius(4f * u))
                drawOval(a.luz(TORTILLA, l), Offset(t.left + 3f * u, t.top + 3f * u), Size(t.width - 6f * u, t.height - 6f * u))
                drawOval(a.luz(mezcla(TORTILLA, TOSTADO, 0.5f), l), Offset(t.center.x - 3f * u, t.center.y - 4f * u), Size(5f * u, 3f * u))
                drawOval(a.luz(mezcla(TORTILLA, TOSTADO, 0.5f), l), Offset(t.center.x + 5f * u, t.center.y + 6f * u), Size(4f * u, 2.6f * u))
                drawRect(a.claro(0.35f), Offset(t.left + 3f * u, t.top + 2f * u), Size(t.width * 0.4f, 1.2f * u))
            }
            rotate(9f, r.center) {
                val b = Rect(r.left + r.width * 0.5f, r.top + r.height * 0.12f, r.right - r.width * 0.04f, r.top + r.height * 0.7f)
                drawRoundRect(a.luz(LAMINA, l), b.topLeft, b.size, CornerRadius(2f * u))
                drawRect(a.luz(P.NARANJA, l), Offset(b.left, b.top + b.height * 0.42f), Size(b.width, b.height * 0.24f))
                drawRect(a.claro(0.5f), Offset(b.left + 2f * u, b.top + 1.5f * u), Size(1.2f * u, b.height - 3f * u))
                val c = Rect(r.left + r.width * 0.42f, r.top + r.height * 0.62f, r.right - r.width * 0.12f, r.bottom)
                drawRoundRect(a.luz(ALGA, l), c.topLeft, c.size, CornerRadius(2f * u))
                drawRect(a.luz(P.PAPEL, l), Offset(c.left + c.width * 0.2f, c.top + c.height * 0.25f), Size(c.width * 0.6f, c.height * 0.25f))
            }
            // La malla: rombos, en un solo camino armado una vez por tamaño.
            malla.reset()
            val paso = maxOf(1f, 5.5f * u)
            var k = r.left - r.height
            while (k < r.right + r.height) {
                malla.moveTo(k, r.top); malla.lineTo(k + r.height, r.bottom)
                malla.moveTo(k + r.height, r.top); malla.lineTo(k, r.bottom)
                k += paso
            }
            drawPath(malla, a.luz(mezcla(P.TELA, P.ACOLCHADO, 0.4f), l).copy(alpha = 0.8f), style = Stroke(0.7f * u))
            drawRect(a.sombra(0.18f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
        }
        drawPath(trazo, a.luz(mezcla(P.TELA, P.ACOLCHADO, 0.3f), l).copy(alpha = 0.7f), style = Stroke(1f * u))
        // El dobladillo elástico y el mosquetón que la cuelga del pasamanos.
        drawRoundRect(a.luz(P.GOMA, l), Offset(r.left + r.width * 0.1f, r.top - 1.5f * u), Size(r.width * 0.8f, 4f * u), CornerRadius(2f * u))
        val m = Offset(r.center.x, r.top - 4f * u)
        drawRoundRect(a.luz(P.NARANJA, l), Offset(m.x - 3f * u, m.y - 7f * u), Size(6f * u, 11f * u), CornerRadius(3f * u), style = Stroke(1.6f * u))
        drawLine(a.claro(0.5f), Offset(m.x - 2.4f * u, m.y - 5f * u), Offset(m.x - 2.4f * u, m.y + 1f * u), 0.6f * u)
    }

    /**
     * La bandeja de comida, sujeta a la mesa con velcro: el marco de aluminio con su grueso, el fondo, dos gomas que cruzan y
     * lo que sujetan (una lata vista desde arriba, una bolsa de lámina con su boquilla y una cuchara metida bajo la goma).
     */
    fun DrawScope.bandeja(r: Rect, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio, caida: (Offset, Float) -> Offset) {
        val u = density
        val l = luz(r.center)
        val grueso = 6f * u
        val d = caida(r.center, 5f * u)
        drawRoundRect(a.sombra(if (a.oscuro) 0.5f else 0.28f), Offset(r.left + d.x, r.top + d.y + grueso * 0.6f), Size(r.width, r.height + grueso), CornerRadius(5f * u))
        // El canto de delante y la cara de arriba del marco.
        drawRoundRect(a.luz(mezcla(P.ACERO, P.CASCO_OSC, 0.45f), l), Offset(r.left, r.top + grueso), Size(r.width, r.height), CornerRadius(5f * u))
        drawRoundRect(a.luz(P.ACERO, l), r.topLeft, r.size, CornerRadius(5f * u))
        drawRoundRect(a.claro(0.45f), r.topLeft, Size(r.width, 1.2f * u), CornerRadius(1f * u))
        val fondo = r.deflate(4f * u)
        drawRoundRect(a.luz(mezcla(P.GOMA, P.ACERO, 0.4f), l), fondo.topLeft, fondo.size, CornerRadius(3f * u))
        drawRect(a.sombra(0.3f), fondo.topLeft, Size(fondo.width, 3f * u))
        // La lata: la tapa redonda con su anilla.
        val lc = Offset(fondo.left + fondo.width * 0.27f, fondo.top + fondo.height * 0.5f)
        val lr = minOf(fondo.width * 0.2f, fondo.height * 0.38f)
        drawOval(a.sombra(0.35f), Offset(lc.x - lr + 2f * u, lc.y - lr * 0.8f + 2.5f * u), Size(lr * 2f, lr * 1.6f))
        drawOval(a.luz(P.ACERO, l), Offset(lc.x - lr, lc.y - lr * 0.8f), Size(lr * 2f, lr * 1.6f))
        drawOval(a.luz(mezcla(P.ACERO, P.CASCO, 0.5f), l), Offset(lc.x - lr * 0.82f, lc.y - lr * 0.65f), Size(lr * 1.64f, lr * 1.3f))
        drawOval(a.claro(0.5f), Offset(lc.x - lr * 0.82f, lc.y - lr * 0.65f), Size(lr * 1.64f, lr * 1.3f), style = Stroke(0.7f * u))
        drawRoundRect(a.luz(mezcla(P.ACERO, P.TINTA, 0.3f), l), Offset(lc.x - lr * 0.15f, lc.y - lr * 0.5f), Size(lr * 0.7f, lr * 0.38f), CornerRadius(2f * u), style = Stroke(1f * u))
        // La bolsa de lámina con su boquilla, un poco ladeada.
        rotate(-6f, Offset(fondo.left + fondo.width * 0.72f, fondo.center.y)) {
            val b = Rect(fondo.left + fondo.width * 0.52f, fondo.top + fondo.height * 0.12f, fondo.right - fondo.width * 0.06f, fondo.bottom - fondo.height * 0.1f)
            drawRoundRect(a.sombra(0.3f), b.topLeft + Offset(1.5f * u, 2f * u), b.size, CornerRadius(2.5f * u))
            drawRoundRect(a.luz(LAMINA, l), b.topLeft, b.size, CornerRadius(2.5f * u))
            for (j in 0 until 4) drawLine(a.sombra(0.15f), Offset(b.left + b.width * azar(1501, j), b.top + 2f * u), Offset(b.left + b.width * azar(1502, j), b.bottom - 2f * u), 0.6f * u)
            drawRect(a.luz(P.PAPEL, l), Offset(b.left + 2.5f * u, b.top + b.height * 0.38f), Size(b.width - 5f * u, b.height * 0.26f))
            drawRect(a.luz(P.NARANJA, l), Offset(b.left + 2.5f * u, b.top + b.height * 0.38f), Size(b.width * 0.22f, b.height * 0.26f))
            drawRoundRect(a.luz(P.GOMA, l), Offset(b.center.x - 2.6f * u, b.top - 4f * u), Size(5.2f * u, 6f * u), CornerRadius(1.5f * u))
            drawRect(a.claro(0.55f), Offset(b.left + 1.5f * u, b.top + 2f * u), Size(1.2f * u, b.height - 4f * u))
        }
        // Las dos gomas que cruzan la bandeja y la cuchara metida bajo una.
        listOf(0.3f, 0.74f).forEach { f ->
            val y = r.top + r.height * f
            drawRect(a.sombra(0.35f), Offset(r.left, y + 2.4f * u), Size(r.width, 1.4f * u))
            drawRect(a.luz(P.GOMA, l), Offset(r.left - 1f * u, y - 1.4f * u), Size(r.width + 2f * u, 3.2f * u))
            drawRect(a.claro(0.25f), Offset(r.left, y - 1f * u), Size(r.width, 0.6f * u))
        }
        val c0 = Offset(r.left + r.width * 0.06f, r.top + r.height * 0.82f)
        drawLine(a.sombra(0.3f), c0 + Offset(1.5f * u, 2f * u), Offset(r.right - r.width * 0.3f, r.top + r.height * 0.66f) + Offset(1.5f * u, 2f * u), 2.4f * u, StrokeCap.Round)
        drawLine(a.luz(P.ACERO, l), c0, Offset(r.right - r.width * 0.3f, r.top + r.height * 0.66f), 2.2f * u, StrokeCap.Round)
        drawOval(a.luz(P.ACERO, l), Offset(c0.x - 6f * u, c0.y - 2.5f * u), Size(9f * u, 6f * u))
        drawLine(a.claro(0.55f), c0 + Offset(4f * u, -1f * u), Offset(r.right - r.width * 0.32f, r.top + r.height * 0.655f), 0.6f * u)
        if (a.lumbre) drawRoundRect(a.filo(0.6f * l.k), r.topLeft, Size(r.width, 1.2f * u), CornerRadius(1f * u))
    }

    /**
     * Las tijeras atadas a la bandeja con su cordón en espiral (en el comedor se cortan las bolsas y nada puede quedar suelto),
     * flotando: las dos hojas, el eje y los ojos de goma naranja.
     */
    fun DrawScope.tijeras(c: Offset, ancla: Offset, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio, caida: (Offset, Float) -> Offset) {
        val u = density
        val l = luz(c)
        trazo.reset()
        trazo.moveTo(ancla.x, ancla.y)
        trazo.cubicTo(ancla.x + 14f * u, ancla.y - 10f * u, c.x + 18f * u, c.y + 16f * u, c.x + 7f * u, c.y + 6f * u)
        drawPath(trazo, a.luz(P.GOMA, l), style = Stroke(1f * u))
        drawPath(trazo, a.luz(mezcla(P.GOMA, P.TELA_AZUL, 0.4f), l), style = Stroke(3f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 1.4f * u))))
        val d = caida(c, 7f * u)
        rotate(-28f, c) {
            for (sombra in listOf(true, false)) {
                val o = if (sombra) d else Offset.Zero
                val hoja = if (sombra) a.sombra(0.3f) else a.luz(P.ACERO, l)
                val ojo = if (sombra) a.sombra(0.3f) else a.luz(P.NARANJA, l)
                trazo.reset()
                trazo.moveTo(c.x + o.x, c.y - 1.6f * u + o.y); trazo.lineTo(c.x + 24f * u + o.x, c.y - 4.5f * u + o.y); trazo.lineTo(c.x + 2f * u + o.x, c.y + 1.4f * u + o.y); trazo.close()
                drawPath(trazo, hoja)
                trazo.reset()
                trazo.moveTo(c.x + o.x, c.y + 1.6f * u + o.y); trazo.lineTo(c.x + 23f * u + o.x, c.y + 3.5f * u + o.y); trazo.lineTo(c.x + 2f * u + o.x, c.y - 1.4f * u + o.y); trazo.close()
                drawPath(trazo, hoja)
                drawOval(ojo, Offset(c.x - 12f * u + o.x, c.y - 7.5f * u + o.y), Size(11f * u, 6.5f * u), style = Stroke(2.4f * u))
                drawOval(ojo, Offset(c.x - 12f * u + o.x, c.y + 1f * u + o.y), Size(11f * u, 6.5f * u), style = Stroke(2.4f * u))
            }
            drawLine(a.claro(0.6f), Offset(c.x + 3f * u, c.y - 2f * u), Offset(c.x + 20f * u, c.y - 4f * u), 0.6f * u)
            drawCircle(a.luz(P.GOMA, l), 1.4f * u, c)
        }
    }

    /** Una gota de agua que se escapó de una bolsa y flota: la esfera transparente con su brillo y su reflejo de abajo. */
    fun DrawScope.gota(c: Offset, r: Float, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio) {
        val u = density
        val l = luz(c)
        drawCircle(a.luz(mezcla(P.LUZ_FRIA, P.TELA_AZUL, 0.3f), l).copy(alpha = 0.45f), r, c)
        drawCircle(a.claro(0.7f), r, c, style = Stroke(0.7f * u))
        drawCircle(Color.White.copy(alpha = if (a.oscuro) 0.4f else 0.85f), r * 0.3f, c - Offset(r * 0.35f, r * 0.35f))
        drawArc(a.sombra(0.3f), 20f, 120f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(0.8f * u))
        if (a.lumbre) drawCircle(P.AMBAR.copy(alpha = 0.6f * l.k), r * 0.22f, c + Offset(r * 0.38f, -r * 0.3f))
    }

    /**
     * La toalla de mano metida detrás del pasamanos: lo que queda por detrás se abre hacia arriba y hacia la izquierda como una
     * bandera (sin peso la tela no cuelga), ancha, con el borde libre en ola, una esquina vuelta que enseña el revés, el rizo,
     * las dos rayas del borde y su sombra en la pared. [agarre] es dónde la aprieta el pasamanos; [ab] la mece un poco.
     */
    fun DrawScope.toalla(agarre: Offset, mesa: Float, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio, caida: (Offset, Float) -> Offset, ab: Float) {
        val u = density
        val x = agarre.x; val y = agarre.y
        val m = ab * 3f * u
        val H = (y - 8f * u).coerceAtLeast(4f * u).coerceAtMost(46f * u)
        val l = luz(Offset(x, y - H * 0.5f))
        // El contorno: abajo, ancha y apretada por el pasamanos; sube abriéndose a la izquierda; arriba, el borde libre en ola.
        val i0 = Offset(x - 22f * u, y); val d0 = Offset(x + 22f * u, y)
        val i1 = Offset(x - 40f * u - m, y - H * 0.92f); val d1 = Offset(x + 12f * u + m, y - H)
        trazo.reset()
        trazo.moveTo(i0.x, i0.y)
        trazo.cubicTo(x - 28f * u, y - H * 0.35f, x - 40f * u - m, y - H * 0.55f, i1.x, i1.y)
        trazo.quadraticTo(x - 30f * u - m, y - H * 1.08f, x - 16f * u, y - H * 0.94f)
        trazo.quadraticTo(x - 2f * u + m, y - H * 0.82f, d1.x, d1.y)
        trazo.cubicTo(x + 16f * u + m, y - H * 0.6f, x + 18f * u, y - H * 0.3f, d0.x, d0.y)
        trazo.close()
        val d = caida(Offset(x, y - H * 0.5f), 4f * u)
        translate(d.x, d.y) { drawPath(trazo, a.sombra(0.22f)) }
        drawPath(trazo, a.luz(TOALLA, l))
        clipPath(trazo) {
            // La panza que mira a la luz y el pliegue en sombra que baja hacia el pasamanos.
            drawOval(a.claro(0.22f), Offset(x - 40f * u, y - H * 0.95f), Size(30f * u, H * 0.7f))
            drawOval(a.sombra(0.18f), Offset(x - 4f * u, y - H * 0.75f), Size(24f * u, H * 0.85f))
            pinceladas(Rect(x - 42f * u, y - H * 1.1f, x + 22f * u, y), a.sombra(1f), 1471, 12, u, 0.07f)
            // Las dos rayas cerca del borde libre, siguiendo la ola.
            listOf(5f, 9f).forEach { k ->
                translate(0f, k * u) {
                    trazo.reset()
                    trazo.moveTo(i1.x - 2f * u, i1.y)
                    trazo.quadraticTo(x - 30f * u - m, y - H * 1.08f, x - 16f * u, y - H * 0.94f)
                    trazo.quadraticTo(x - 2f * u + m, y - H * 0.82f, d1.x + 2f * u, d1.y)
                    drawPath(trazo, a.luz(mezcla(P.TELA, P.PAPEL, 0.5f), l), style = Stroke(1.8f * u))
                }
            }
        }
        // La esquina de arriba a la derecha, vuelta: el revés, más claro, con la sombra que deja.
        poligonoEnEspacio(trazo, d1.x, d1.y, d1.x - 12f * u, d1.y + 1.5f * u, d1.x - 1f * u, d1.y + 11f * u)
        translate(-1f * u, 1.5f * u) { drawPath(trazo, a.sombra(0.25f)) }
        drawPath(trazo, a.luz(mezcla(TOALLA, P.PAPEL, 0.45f), l))
        if (mesa < 0f) return
    }

    /** La parte de la toalla que da la vuelta por delante del pasamanos: un doblez ancho y corto, con su dobladillo y su raya. */
    fun DrawScope.toallaDelante(agarre: Offset, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio) {
        val u = density
        val l = luz(agarre)
        val r = Rect(agarre.x - 22f * u, agarre.y - 4f * u, agarre.x + 22f * u, agarre.y + 11f * u)
        poligonoEnEspacio(trazo, r.left, r.top, r.right, r.top, r.right + 1f * u, r.bottom - 1f * u, r.left + 3f * u, r.bottom + 1f * u)
        translate(1.5f * u, 2f * u) { drawPath(trazo, a.sombra(0.3f)) }
        drawPath(trazo, a.luz(TOALLA, l))
        drawRect(a.claro(0.3f), Offset(r.left, r.top + 1f * u), Size(r.width, 1.6f * u))
        drawLine(a.luz(mezcla(P.TELA, P.PAPEL, 0.5f), l), Offset(r.left + 1f * u, r.bottom - 4.5f * u), Offset(r.right, r.bottom - 5f * u), 1.8f * u)
        drawRect(a.sombra(0.2f), Offset(r.right - 8f * u, r.top), Size(8f * u, r.height))
    }

    /**
     * Un frasco de gotero pegado al velcro por su base: la sal (tapa azul) o la pimienta (tapa negra y el líquido oscuro), con
     * su etiqueta y su brillo, un poco ladeado.
     */
    fun DrawScope.frasco(r: Rect, pimienta: Boolean, giro: Float, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio, caida: (Offset, Float) -> Offset) {
        val u = density
        val l = luz(r.center)
        val d = caida(r.center, 3f * u)
        rotate(giro, r.center) {
            val cuerpo = Rect(r.left, r.top + r.height * 0.3f, r.right, r.bottom)
            drawRoundRect(a.sombra(0.3f), cuerpo.topLeft + d, cuerpo.size, CornerRadius(3f * u))
            drawRoundRect(a.luz(if (pimienta) mezcla(P.TINTA, P.TRIGO, 0.25f) else mezcla(P.PAPEL, P.LUZ_FRIA, 0.5f), l), cuerpo.topLeft, cuerpo.size, CornerRadius(3f * u))
            drawRoundRect(a.luz(if (pimienta) P.GOMA else P.TELA_AZUL, l), Offset(r.left + r.width * 0.28f, r.top), Size(r.width * 0.44f, r.height * 0.34f), CornerRadius(1.5f * u))
            drawRect(a.luz(P.PAPEL, l), Offset(cuerpo.left + 1.5f * u, cuerpo.top + cuerpo.height * 0.35f), Size(cuerpo.width - 3f * u, cuerpo.height * 0.3f))
            drawRect(a.luz(if (pimienta) P.ROJO else P.TELA_AZUL, l), Offset(cuerpo.left + 1.5f * u, cuerpo.top + cuerpo.height * 0.35f), Size(cuerpo.width * 0.25f, cuerpo.height * 0.3f))
            drawRect(a.claro(0.5f), Offset(cuerpo.left + 2f * u, cuerpo.top + 2f * u), Size(1.2f * u, cuerpo.height - 4f * u))
        }
    }


    /**
     * La lámpara de pinza sujeta al pasamanos: la pinza, el brazo corto y la cabeza que mira abajo a la izquierda, hacia la
     * carpeta. Encendida en oscuro, con el punto de su boca.
     */
    fun DrawScope.lampara(boca: Offset, pasa: Float, a: AireEstanteEspacio) {
        val u = density
        val encendida = a.oscuro
        val l = LuzEstanteEspacio(if (encendida) 0.9f else 1f, 0.1f)
        val pinza = Offset(boca.x + 14f * u, pasa)
        drawRoundRect(a.sombra(0.3f), Offset(pinza.x - 5f * u + 2f * u, pinza.y - 7f * u + 2f * u), Size(10f * u, 14f * u), CornerRadius(2f * u))
        drawRoundRect(a.luz(P.GOMA, l), Offset(pinza.x - 5f * u, pinza.y - 7f * u), Size(10f * u, 14f * u), CornerRadius(2f * u))
        drawRect(a.luz(P.NARANJA, l), Offset(pinza.x - 5f * u, pinza.y + 3f * u), Size(10f * u, 2f * u))
        drawLine(a.luz(P.ACERO, l), pinza, boca + Offset(6f * u, -6f * u), 2.4f * u, StrokeCap.Round)
        rotate(135f, boca) {
            val r = Rect(boca.x - 15f * u, boca.y - 5f * u, boca.x, boca.y + 5f * u)
            drawRoundRect(a.sombra(0.3f), r.topLeft + Offset(0f, -2f * u), r.size, CornerRadius(3f * u))
            drawRoundRect(a.luz(P.GOMA, l), r.topLeft, r.size, CornerRadius(3f * u))
            drawRect(a.claro(0.3f), Offset(r.left + 2f * u, r.bottom - 1.8f * u), Size(r.width - 4f * u, 0.8f * u))
            drawOval(if (encendida) Color(0xFFFFEBC8) else a.luz(mezcla(P.LUZ_FRIA, P.ACERO, 0.45f), l), Offset(boca.x - 2f * u, boca.y - 5.4f * u), Size(4f * u, 10.8f * u))
        }
        if (encendida) {
            drawCircle(P.AMBAR.copy(alpha = 0.3f), 7f * u, boca)
            drawCircle(Color(0xFFFFF0D6), 2.6f * u, boca)
        }
    }

    /** El piloto azul de guardia en la pared: apagado de día, encendido (tenue) en oscuro. */
    fun DrawScope.piloto(c: Offset, a: AireEstanteEspacio) {
        val u = density
        drawRoundRect(a.sombra(0.35f), Offset(c.x - 3.5f * u, c.y - 5f * u), Size(9f * u, 12f * u), CornerRadius(2.5f * u))
        drawRoundRect(a.luz(P.GOMA, LuzEstanteEspacio(0.45f, 0f)), Offset(c.x - 4.5f * u, c.y - 6f * u), Size(9f * u, 12f * u), CornerRadius(2.5f * u))
        if (a.lumbre) {
            drawCircle(P.GUARDIA.copy(alpha = 0.3f), 7f * u, c)
            drawCircle(mezcla(P.GUARDIA, Color.White, 0.5f), 2.6f * u, c)
        } else {
            drawCircle(a.luz(mezcla(P.GUARDIA, P.TINTA, 0.45f), LuzEstanteEspacio(1f, 0f)), 2.6f * u, c)
            drawCircle(a.claro(0.5f), 0.9f * u, c - Offset(0.9f * u, 0.9f * u))
        }
    }
}
