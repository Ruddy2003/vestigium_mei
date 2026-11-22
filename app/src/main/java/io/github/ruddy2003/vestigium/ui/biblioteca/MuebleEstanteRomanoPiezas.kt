package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Los objetos del estante de Romano ([MuebleEstanteRomano]), todos del cuarto del escribiente de un campamento del Danubio
 * hacia el 110 d. C.: arriba, el modio de medir el grano con su rasero y la romana de bronce colgada que pesa la ración (el
 * punto donde cae el ojo), la dolabra colgada bajo la ventana, la jarra de la posca y la linterna de cuerno; en los huecos de
 * las repisas, el pan partido en ocho, las pesas de piedra y la sartén de bronce; al pie, la muela de mano del contubernio y
 * el ánfora de aceite de la Bética apoyada en el pilar.
 */
internal object PiezasEstanteRomano {
    private val P = PaletaRomano

    /** Hacia dónde cae la sombra de algo que está en [p]: de día, a la derecha y abajo; de noche, lejos de la linterna. */
    private fun DrawScope.caida(p: Offset, largo: Float, a: AireRomano): Offset {
        if (!a.oscuro) return Offset(largo * 1.1f, largo * 0.7f)
        val llama = Offset(size.width * MuebleEstanteRomano.LAMP_X, size.width * MuebleEstanteRomano.LAMP_Y)
        val v = p - llama
        return v / v.getDistance().coerceAtLeast(1f) * (largo * 2f)
    }

    // ---------------------------------------------------------------- arriba

    fun DrawScope.arriba(a: AireRomano, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width
        dolabra(a, Offset(w * 0.05f, w * 0.39f), w * 0.235f, cerca)
        modio(a, Offset(w * 0.36f, apoyo), w * 0.165f, w * 0.125f, cerca)
        romana(a, Offset(w * 0.565f, w * 0.115f), cerca)
        jarra(a, Offset(w * 0.69f, apoyo), w * 0.078f, cerca)
    }

    /**
     * La dolabra colgada de dos estacas bajo la ventana: el mango de fresno, la cabeza de hierro con el hacha que baja y el pico
     * que sube, la funda de cuero en el filo; su sombra en la cal.
     */
    private fun DrawScope.dolabra(a: AireRomano, desde: Offset, largo: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(desde + Offset(largo / 2, 0f))
        val g = 3.4f * u
        val sombra = caida(desde, 3f * u, a)
        // Las estacas.
        for (f in listOf(0.3f, 0.82f)) {
            val p = desde + Offset(largo * f, g * 0.6f)
            drawCircle(a.sombra(0.3f), 1.8f * u, p + Offset(1f * u, 1f * u))
            drawCircle(a.luz(P.ROBLE_OSC, k), 1.6f * u, p)
        }
        // La sombra del mango y de la cabeza.
        drawLine(a.sombra(0.25f), desde + sombra, desde + sombra + Offset(largo, -1.2f * u), g, cap = StrokeCap.Round)
        drawPath(corridoRomano(cabezaDolabra(desde, largo), sombra.x, sombra.y), a.sombra(0.25f))
        // El mango.
        drawLine(a.luz(Color(0xFF9C7A52), k), desde, desde + Offset(largo, -1.2f * u), g, cap = StrokeCap.Round)
        drawLine(a.claro(0.35f), desde + Offset(largo * 0.1f, -g * 0.3f), desde + Offset(largo, -1.2f * u - g * 0.3f), 0.7f * u)
        // La cabeza de hierro.
        drawPath(cabezaDolabra(desde, largo), a.luz(P.HIERRO, k))
        drawLine(a.luz(P.HIERRO_LUZ, k), desde + Offset(largo * 0.012f, -largo * 0.2f), desde + Offset(largo * 0.03f, largo * 0.15f), 0.8f * u)
        // La funda de cuero del filo, con su correa.
        val funda = Rect(desde.x - largo * 0.035f, desde.y + largo * 0.06f, desde.x + largo * 0.075f, desde.y + largo * 0.19f)
        drawRect(a.luz(P.CUERO, k), funda.topLeft, funda.size)
        drawRect(a.claro(0.25f), funda.topLeft, Size(funda.width * 0.25f, funda.height))
        drawLine(a.luz(P.ROBLE_OSC, k), Offset(funda.left, funda.top + funda.height * 0.45f), Offset(funda.right, funda.top + funda.height * 0.4f), 0.8f * u)
    }

    private fun cabezaDolabra(o: Offset, largo: Float) = Path().apply {
        // El ojo, el hacha que baja (ancha abajo) y el pico curvo que sube.
        moveTo(o.x - largo * 0.02f, o.y - largo * 0.025f)
        lineTo(o.x + largo * 0.045f, o.y - largo * 0.025f)
        lineTo(o.x + largo * 0.06f, o.y + largo * 0.2f)
        lineTo(o.x - largo * 0.045f, o.y + largo * 0.21f)
        lineTo(o.x - largo * 0.01f, o.y + largo * 0.02f)
        quadraticTo(o.x - largo * 0.03f, o.y - largo * 0.15f, o.x + largo * 0.05f, o.y - largo * 0.27f)
        quadraticTo(o.x + largo * 0.012f, o.y - largo * 0.12f, o.x + largo * 0.03f, o.y - largo * 0.03f)
        close()
    }

    /**
     * El modio de medir el grano (el objeto que manda): un cubo de duelas de roble, más ancho abajo, con dos aros de bronce,
     * lleno de trigo colmado; el rasero de alisar apoyado a su izquierda y granos sueltos en la viga.
     */
    private fun DrawScope.modio(a: AireRomano, pie: Offset, ancho: Float, alto: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(pie + Offset(0f, -alto / 2))
        val abajo = ancho / 2; val arriba = ancho * 0.4f
        val cuerpo = poligonoRomano(pie.x - abajo, pie.y, pie.x - arriba, pie.y - alto, pie.x + arriba, pie.y - alto, pie.x + abajo, pie.y)
        val colmo = Path().apply {
            moveTo(pie.x - arriba * 1.02f, pie.y - alto)
            quadraticTo(pie.x - arriba * 0.3f, pie.y - alto - ancho * 0.3f, pie.x + arriba * 0.2f, pie.y - alto - ancho * 0.26f)
            quadraticTo(pie.x + arriba * 0.7f, pie.y - alto - ancho * 0.14f, pie.x + arriba * 1.02f, pie.y - alto)
            close()
        }
        // La sombra en la cal y en la viga.
        val s = caida(pie + Offset(0f, -alto), alto * 0.22f, a)
        drawPath(corridoRomano(cuerpo, s.x, -alto * 0.02f), a.sombra(0.3f))
        drawPath(corridoRomano(colmo, s.x, -alto * 0.02f), a.sombra(0.3f))
        contactoRomano(pie + Offset(ancho * 0.12f, 0f), ancho * 1.25f, 4f * u, a)
        // El rasero, apoyado.
        drawLine(a.sombra(0.3f), pie + Offset(-abajo - ancho * 0.12f + 3f * u, 0f), pie + Offset(-arriba - ancho * 0.02f + 3f * u, -alto * 1.25f), 2.4f * u)
        drawLine(a.luz(Color(0xFFB49064), k), pie + Offset(-abajo - ancho * 0.12f, 0f), pie + Offset(-arriba - ancho * 0.02f, -alto * 1.25f), 2.4f * u, cap = StrokeCap.Round)
        // El cubo: duelas, luz a un lado, sombra al otro.
        drawPath(cuerpo, a.luz(P.ROBLE, k))
        for (j in 1 until 7) {
            val f = j / 7f
            drawLine(a.luz(P.ROBLE_OSC, k).copy(alpha = 0.55f), Offset(pie.x - abajo + ancho * f, pie.y), Offset(pie.x - arriba + arriba * 2 * f, pie.y - alto), 0.7f * u)
        }
        drawPath(poligonoRomano(pie.x - abajo, pie.y, pie.x - arriba, pie.y - alto, pie.x - arriba * 0.55f, pie.y - alto, pie.x - abajo * 0.6f, pie.y), a.claro(0.28f))
        drawPath(poligonoRomano(pie.x + abajo * 0.45f, pie.y, pie.x + arriba * 0.4f, pie.y - alto, pie.x + arriba, pie.y - alto, pie.x + abajo, pie.y), a.frio(0.28f))
        if (a.lumbre) drawPath(poligonoRomano(pie.x + abajo * 0.8f, pie.y, pie.x + arriba * 0.8f, pie.y - alto, pie.x + arriba, pie.y - alto, pie.x + abajo, pie.y), a.filo(0.5f * k))
        // Los aros de bronce.
        for (f in listOf(0.16f, 0.82f)) {
            val y = pie.y - alto * f
            val m = abajo + (arriba - abajo) * f
            drawLine(a.luz(P.BRONCE, k), Offset(pie.x - m, y), Offset(pie.x + m, y), 2.6f * u)
            drawLine(a.luz(P.BRONCE_LUZ, k), Offset(pie.x - m, y - 0.8f * u), Offset(pie.x - m * 0.1f, y - 0.8f * u), 0.8f * u)
            clavoRomano(Offset(pie.x - m * 0.5f, y), 0.9f * u, a, k)
        }
        // El trigo colmado, con su grano.
        drawPath(colmo, a.luz(P.TRIGO, k))
        drawPath(corridoRomano(colmo, ancho * 0.08f, ancho * 0.02f), a.frio(0.12f))
        for (j in 0 until 18) {
            val x = pie.x - arriba + 2 * arriba * azar(91, j)
            val y = pie.y - alto - ancho * 0.22f * azar(92, j) * (1f - kotlin.math.abs(x - pie.x) / (arriba * 1.2f)).coerceAtLeast(0f)
            drawOval(a.luz(if (j % 3 == 0) Color(0xFFA77A35) else Color(0xFFEAC77A), k), Offset(x, y - 0.6f * u), Size(1.6f * u, 1f * u))
        }
        // Granos sueltos en la viga.
        for (j in 0 until 7) drawOval(a.luz(P.TRIGO, k), Offset(pie.x + abajo + ancho * 0.5f * azar(93, j), pie.y - 1.2f * u), Size(1.5f * u, 0.9f * u))
    }

    /**
     * La romana de bronce colgada de una estaca de hierro en la pared: el brazo con sus marcas, el platillo colgado de tres
     * cadenitas con un puñado de grano y la pesa en forma de bellota, que corre por el brazo. Su sombra en la cal.
     */
    private fun DrawScope.romana(a: AireRomano, clavo: Offset, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width * 1.25f
        val k = cerca(clavo + Offset(0f, w * 0.1f))
        val fiel = clavo + Offset(0f, w * 0.045f)
        val corto = fiel + Offset(-w * 0.045f, w * 0.006f)
        val largo = fiel + Offset(w * 0.16f, -w * 0.012f)
        val plato = Offset(corto.x, w * 0.3f)
        val pesa = largo + Offset(-w * 0.035f, 0f)
        val s = caida(fiel, 2.6f * u, a)
        for (fase in 0..1) {
            val sombra = fase == 0
            val o = if (sombra) s else Offset.Zero
            val bronce = if (sombra) a.sombra(0.22f) else a.luz(P.BRONCE, k)
            val cadena = if (sombra) a.sombra(0.18f) else a.luz(P.BRONCE, k * 0.9f)
            drawLine(cadena, clavo + o, fiel + o, 0.9f * u)
            drawLine(bronce, corto + o, largo + o, 2.2f * u, cap = StrokeCap.Round)
            for (dx in listOf(-w * 0.03f, 0f, w * 0.03f)) drawLine(cadena, corto + o, Offset(plato.x + dx, plato.y) + o, 0.6f * u)
            val bol = Rect(plato.x - w * 0.042f + o.x, plato.y + o.y, plato.x + w * 0.042f + o.x, plato.y + w * 0.026f + o.y)
            drawArc(bronce, 0f, 180f, true, Offset(bol.left, bol.top - bol.height), Size(bol.width, bol.height * 2))
            val gancho = pesa + o
            drawLine(cadena, gancho, gancho + Offset(0f, w * 0.03f), 0.8f * u)
            drawOval(bronce, Offset(gancho.x - w * 0.016f, gancho.y + w * 0.028f), Size(w * 0.032f, w * 0.042f))
            if (!sombra) {
                // El grano en el platillo, las marcas del brazo, la luz del bronce y la cúpula de la bellota.
                drawArc(a.luz(P.TRIGO, k), 180f, 180f, true, Offset(bol.left + bol.width * 0.2f, bol.top - bol.height * 0.35f), Size(bol.width * 0.6f, bol.height * 0.7f))
                drawLine(a.luz(P.BRONCE_LUZ, k), Offset(bol.left + bol.width * 0.12f, bol.top + 0.9f * u), Offset(bol.left + bol.width * 0.45f, bol.top + 0.9f * u), 0.9f * u)
                for (j in 0 until 9) {
                    val f = 0.25f + 0.08f * j
                    val p = Offset(corto.x + (largo.x - corto.x) * f, corto.y + (largo.y - corto.y) * f)
                    drawLine(a.luz(P.BRONCE_LUZ, k), p + Offset(0f, -1.6f * u), p + Offset(0f, 1.6f * u), 0.5f * u)
                }
                drawLine(a.luz(P.BRONCE_LUZ, k), corto + Offset(2f * u, -0.8f * u), largo + Offset(-2f * u, -0.8f * u), 0.6f * u)
                drawArc(a.luz(P.BRONCE_LUZ, k), 180f, 180f, true, Offset(gancho.x - w * 0.018f, gancho.y + w * 0.024f), Size(w * 0.036f, w * 0.016f))
                drawOval(a.claro(0.4f), Offset(gancho.x - w * 0.011f, gancho.y + w * 0.04f), Size(w * 0.007f, w * 0.016f))
                clavoRomano(clavo, 1.8f * u, a, k)
                drawCircle(a.luz(P.BRONCE_LUZ, k), 1.6f * u, fiel, style = Stroke(0.8f * u))
            }
        }
    }

    /** La jarra de barro de la posca, con su asa, y el vaso volcado a su lado. */
    private fun DrawScope.jarra(a: AireRomano, pie: Offset, ancho: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(pie + Offset(0f, -ancho))
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
        val s = caida(pie, ancho * 0.18f, a)
        drawPath(corridoRomano(cuerpo, s.x, -ancho * 0.05f), a.sombra(0.28f))
        contactoRomano(pie + Offset(ancho * 0.1f, 0f), ancho * 1.1f, 3.5f * u, a)
        // El asa.
        drawArc(a.luz(P.BARRO, k), -80f, 170f, false, Offset(pie.x + ancho * 0.05f, pie.y - alto * 0.95f), Size(ancho * 0.5f, alto * 0.5f), style = Stroke(2.2f * u))
        drawPath(cuerpo, a.luz(P.BARRO, k))
        // El engobe más claro arriba, la luz y la sombra redondas.
        drawRect(a.luz(Color(0xFFD9A57E), k).copy(alpha = 0.5f), Offset(pie.x - ancho * 0.5f, pie.y - alto * 0.62f), Size(ancho, 1.2f * u))
        drawOval(a.claro(0.3f), Offset(pie.x - ancho * 0.4f, pie.y - alto * 0.62f), Size(ancho * 0.16f, alto * 0.42f))
        drawOval(a.frio(0.22f), Offset(pie.x + ancho * 0.12f, pie.y - alto * 0.65f), Size(ancho * 0.3f, alto * 0.6f))
        drawRect(a.luz(P.ALMAGRE_OSC, k), Offset(pie.x - ancho * 0.15f, pie.y - alto - 1f * u), Size(ancho * 0.3f, 1.6f * u))
        if (a.oscuro) drawOval(a.filo(0.5f * k), Offset(pie.x + ancho * 0.3f, pie.y - alto * 0.6f), Size(ancho * 0.1f, alto * 0.4f))
        // El vaso volcado.
        val v = Offset(pie.x + ancho * 0.85f, pie.y - ancho * 0.18f)
        rotate(-80f, v) {
            val vaso = poligonoRomano(v.x - ancho * 0.18f, v.y - ancho * 0.24f, v.x + ancho * 0.18f, v.y - ancho * 0.24f, v.x + ancho * 0.13f, v.y + ancho * 0.14f, v.x - ancho * 0.13f, v.y + ancho * 0.14f)
            drawPath(vaso, a.luz(P.BARRO, k))
            drawRect(a.frio(0.25f), Offset(v.x + ancho * 0.05f, v.y - ancho * 0.24f), Size(ancho * 0.12f, ancho * 0.38f))
        }
    }

    // ---------------------------------------------------------------- la linterna

    /**
     * La linterna de cuerno colgada del brazo de hierro clavado en la pared: el sombrerete de bronce con su anilla, los
     * montantes y los paños de cuerno raspado, ámbar y turbios. De día está apagada (se ve el pabilo); de noche arde: los paños
     * se encienden y la llama se adivina detrás.
     */
    fun DrawScope.linterna(a: AireRomano) {
        val u = density
        val w = size.width
        val c = Offset(w * MuebleEstanteRomano.LAMP_X, w * MuebleEstanteRomano.LAMP_Y)
        val an = w * 0.088f
        val cuerpo = Rect(c.x - an / 2, c.y - w * 0.066f, c.x + an / 2, c.y + w * 0.06f)
        val k = 1f
        // El brazo de hierro y su gancho.
        val muro = Offset(w * 0.94f, w * 0.16f)
        val gancho = Offset(c.x, w * 0.16f)
        drawLine(a.sombra(0.3f), muro + Offset(2f * u, 2f * u), gancho + Offset(2f * u, 2f * u), 2f * u)
        drawLine(a.luz(P.HIERRO, k), muro, gancho, 2f * u, cap = StrokeCap.Round)
        drawLine(a.luz(P.HIERRO, k), muro, muro + Offset(-w * 0.03f, w * 0.03f), 1.6f * u)
        clavoRomano(muro, 2f * u, a, k)
        drawArc(a.luz(P.HIERRO, k), 0f, 180f, false, Offset(gancho.x - 2f * u, gancho.y - 2f * u), Size(4f * u, 6f * u), style = Stroke(1.2f * u))
        // La cadenita del gancho a la anilla del sombrerete, la anilla y el sombrerete cónico.
        val tapa = cuerpo.top
        val anilla = Offset(c.x, tapa - w * 0.032f - 3f * u)
        var cy = gancho.y + 3f * u
        var e = 0
        while (cy < anilla.y - 3f * u && e < 30) {
            drawOval(a.luz(P.HIERRO, k), Offset(c.x - (if (e % 2 == 0) 1.2f else 0.6f) * u, cy), Size((if (e % 2 == 0) 2.4f else 1.2f) * u, 3.2f * u), style = Stroke(0.7f * u))
            cy += 2.6f * u; e++
        }
        drawCircle(a.luz(P.BRONCE, k), 3f * u, anilla, style = Stroke(1.2f * u))
        if (!a.lumbre) drawPath(corridoRomano(poligonoRomano(c.x - an * 0.55f, cuerpo.bottom, c.x + an * 0.55f, cuerpo.bottom, c.x + an * 0.55f, tapa, c.x, tapa - w * 0.03f, c.x - an * 0.55f, tapa), 4f * u, 3f * u), a.sombra(0.25f))
        val sombrero = poligonoRomano(c.x - an * 0.58f, tapa, c.x - an * 0.12f, tapa - w * 0.032f, c.x + an * 0.12f, tapa - w * 0.032f, c.x + an * 0.58f, tapa)
        drawPath(sombrero, a.luz(P.BRONCE, k))
        drawPath(poligonoRomano(c.x - an * 0.58f, tapa, c.x - an * 0.12f, tapa - w * 0.032f, c.x - an * 0.02f, tapa - w * 0.032f, c.x - an * 0.25f, tapa), a.claro(0.35f))
        // Los paños de cuerno.
        val pano = Rect(cuerpo.left + 1f * u, tapa + 1.5f * u, cuerpo.right - 1f * u, cuerpo.bottom - 2f * u)
        if (a.lumbre) {
            drawRect(P.LLAMA, pano.topLeft, pano.size)
            mancha(Offset(c.x, pano.center.y + pano.height * 0.1f), pano.width * 0.7f, P.FUEGO.copy(alpha = 0.95f))
            val llama = Path().apply {
                moveTo(c.x - 2.4f * u, pano.bottom - pano.height * 0.22f)
                quadraticTo(c.x - 2.6f * u, pano.center.y, c.x + 0.4f * u, pano.top + pano.height * 0.22f)
                quadraticTo(c.x + 2.8f * u, pano.center.y, c.x + 2.4f * u, pano.bottom - pano.height * 0.22f)
                close()
            }
            drawPath(llama, Color.White.copy(alpha = 0.75f))
        } else {
            drawRect(P.CUERNO, pano.topLeft, pano.size)
            drawRect(Color(0xFF8A6430).copy(alpha = 0.35f), Offset(pano.center.x, pano.top), Size(pano.width / 2, pano.height))
            drawLine(P.TINTA.copy(alpha = 0.5f), Offset(c.x, pano.bottom - pano.height * 0.2f), Offset(c.x + 0.6f * u, pano.bottom - pano.height * 0.38f), 1f * u)
            drawRect(a.claro(0.35f), Offset(pano.left + pano.width * 0.1f, pano.top), Size(pano.width * 0.12f, pano.height))
        }
        // Las vetas del cuerno raspado, y lo redondo: luz a la izquierda, el costado derecho que se va.
        for (j in 0 until 2) drawLine(Color(0xFF7A5424).copy(alpha = 0.2f), Offset(pano.left, pano.top + pano.height * (0.3f + 0.35f * j)), Offset(pano.right, pano.top + pano.height * (0.36f + 0.3f * j)), 0.5f * u)
        drawRect(Color(0xFF5A3A18).copy(alpha = if (a.lumbre) 0.28f else 0.3f), Offset(pano.right - pano.width * 0.2f, pano.top), Size(pano.width * 0.2f, pano.height))
        drawRect(Color(0xFF5A3A18).copy(alpha = 0.18f), Offset(pano.left, pano.top), Size(pano.width * 0.1f, pano.height))
        // El plato de abajo, más ancho, con su sombra.
        drawOval(a.luz(mezcla(P.BRONCE, P.TINTA, 0.3f), k), Offset(cuerpo.left - 2.5f * u, cuerpo.bottom - 2.5f * u), Size(an + 5f * u, 4.5f * u))
        // Los montantes y las fajas de bronce.
        val monta = if (a.lumbre) mezcla(P.BRONCE, P.TINTA, 0.45f) else a.luz(P.BRONCE, k)
        for (f in listOf(0f, 0.5f, 1f)) drawLine(monta, Offset(cuerpo.left + an * f, tapa), Offset(cuerpo.left + an * f, cuerpo.bottom), 1.6f * u)
        drawRect(monta, Offset(cuerpo.left - 1f * u, cuerpo.bottom - 2.4f * u), Size(an + 2f * u, 2.4f * u))
        drawRect(monta, Offset(cuerpo.left - 0.5f * u, tapa), Size(an + 1f * u, 1.6f * u))
        if (a.lumbre) {
            drawLine(P.FUEGO.copy(alpha = 0.6f), Offset(cuerpo.left - 1f * u, cuerpo.bottom - 2.4f * u), Offset(cuerpo.right + 1f * u, cuerpo.bottom - 2.4f * u), 0.7f * u)
            drawPath(sombrero, P.LLAMA.copy(alpha = 0.18f))
        }
    }

    // ---------------------------------------------------------------- los huecos

    /** Lo que queda en el hueco libre de una repisa, según la repisa: el pan, las pesas de piedra o la sartén de bronce. */
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, a: AireRomano, cerca: Float) {
        val w = size.width
        val libre = r.right - ocupado
        if (libre < w * 0.12f || r.height < 4f) return
        val pie = Offset(ocupado + libre * 0.55f, r.bottom)
        val t = minOf(r.height * 0.42f, libre * 0.7f)
        when (Math.floorMod(k, 3)) {
            0 -> pan(a, pie, t, cerca)
            1 -> pesas(a, pie, t, cerca)
            else -> sarten(a, pie, t, cerca)
        }
    }

    /** El pan redondo partido en ocho, en su paño de lino. */
    private fun DrawScope.pan(a: AireRomano, pie: Offset, t: Float, k: Float) {
        val u = density
        val pano = poligonoRomano(pie.x - t * 0.95f, pie.y, pie.x - t * 0.7f, pie.y - t * 0.12f, pie.x + t * 0.8f, pie.y - t * 0.1f, pie.x + t * 1.0f, pie.y)
        contactoRomano(pie, t * 2.1f, 4f * u, a)
        drawPath(pano, a.luz(Color(0xFFDCD3BE), k))
        drawLine(a.frio(0.25f), Offset(pie.x - t * 0.4f, pie.y - t * 0.05f), Offset(pie.x - t * 0.2f, pie.y), 0.7f * u)
        val hogaza = Rect(pie.x - t * 0.7f, pie.y - t * 0.62f, pie.x + t * 0.7f, pie.y - t * 0.04f)
        drawOval(a.luz(P.PAN, k), hogaza.topLeft, hogaza.size)
        drawOval(a.luz(Color(0xFFD9A262), k), Offset(hogaza.left + hogaza.width * 0.1f, hogaza.top + 1f * u), Size(hogaza.width * 0.55f, hogaza.height * 0.45f))
        drawOval(a.frio(0.22f), Offset(hogaza.center.x, hogaza.top + hogaza.height * 0.35f), Size(hogaza.width * 0.48f, hogaza.height * 0.6f))
        // Los cortes de los ocho gajos y el cordel que lo ciñe.
        for (j in 0 until 5) {
            val x = hogaza.left + hogaza.width * (0.12f + 0.19f * j)
            drawLine(a.luz(Color(0xFF7A4A22), k), Offset(x, hogaza.top + hogaza.height * 0.15f), Offset(x + (j - 2) * 0.8f * u, hogaza.bottom - hogaza.height * 0.2f), 0.9f * u)
        }
        drawLine(a.luz(Color(0xFF7A4A22), k), Offset(hogaza.left + 2f * u, hogaza.center.y + hogaza.height * 0.1f), Offset(hogaza.right - 2f * u, hogaza.center.y + hogaza.height * 0.1f), 0.8f * u)
        if (a.oscuro) drawOval(a.filo(0.5f * k), Offset(hogaza.right - hogaza.width * 0.25f, hogaza.top + 1f * u), Size(hogaza.width * 0.2f, hogaza.height * 0.2f))
    }

    /** Tres pesas de piedra pulida apiladas, de mayor a menor, con su asa de hierro la de arriba. */
    private fun DrawScope.pesas(a: AireRomano, pie: Offset, t: Float, k: Float) {
        val u = density
        contactoRomano(pie, t * 1.9f, 4f * u, a)
        var y = pie.y
        val piedras = listOf(Color(0xFF6E6A63), Color(0xFF8A8478), Color(0xFF4E4C4A))
        for (j in 0 until 3) {
            val an = t * (1.4f - 0.3f * j)
            val al = an * 0.42f
            val r = Rect(pie.x - an / 2 + j * 1.5f * u, y - al, pie.x + an / 2 + j * 1.5f * u, y)
            drawOval(a.luz(piedras[j], k), r.topLeft, r.size)
            drawOval(a.claro(0.3f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.12f), Size(r.width * 0.4f, r.height * 0.3f))
            drawOval(a.frio(0.3f), Offset(r.left + r.width * 0.45f, r.top + r.height * 0.45f), Size(r.width * 0.5f, r.height * 0.5f))
            drawLine(a.luz(P.HIERRO_LUZ, k), Offset(r.center.x - r.width * 0.1f, r.top + r.height * 0.35f), Offset(r.center.x + r.width * 0.1f, r.top + r.height * 0.35f), 0.7f * u)
            y -= al * 0.88f
        }
        drawArc(a.luz(P.HIERRO, k), 180f, 180f, false, Offset(pie.x - t * 0.18f + 3f * u, y - t * 0.2f), Size(t * 0.36f, t * 0.4f), style = Stroke(1.6f * u))
    }

    /** La sartén de bronce de mango largo apoyada de canto contra el fondo, y un ovillo de cordel. */
    private fun DrawScope.sarten(a: AireRomano, pie: Offset, t: Float, k: Float) {
        val u = density
        val c = Offset(pie.x - t * 0.2f, pie.y - t * 0.62f)
        val r = t * 0.6f
        contactoRomano(pie, t * 1.6f, 4f * u, a)
        // El mango, que sube recostado.
        drawLine(a.sombra(0.25f), c + Offset(r * 0.6f + 2f * u, -r * 0.4f + 2f * u), c + Offset(r * 1.6f + 2f * u, -r * 1.25f + 2f * u), 2.6f * u)
        drawLine(a.luz(P.BRONCE, k), c + Offset(r * 0.6f, -r * 0.4f), c + Offset(r * 1.6f, -r * 1.25f), 2.6f * u, cap = StrokeCap.Round)
        drawCircle(a.luz(P.BRONCE, k), r * 0.16f, c + Offset(r * 1.6f, -r * 1.25f), style = Stroke(1.2f * u))
        drawCircle(a.sombra(0.3f), r, c + Offset(2f * u, 2f * u))
        drawCircle(a.luz(P.BRONCE, k), r, c)
        drawCircle(a.luz(mezcla(P.BRONCE, P.TINTA, 0.35f), k), r * 0.8f, c)
        drawCircle(a.luz(P.BRONCE_LUZ, k), r * 0.86f, c, style = Stroke(0.8f * u))
        drawArc(a.claro(0.5f), 200f, 70f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(1.2f * u))
        if (a.oscuro) drawArc(a.filo(0.6f * k), -60f, 80f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.2f * u))
        // El ovillo de cáñamo.
        val o = Offset(pie.x + t * 0.75f, pie.y - t * 0.24f)
        drawCircle(a.luz(P.CANAMO, k), t * 0.24f, o)
        for (j in 0 until 4) drawArc(a.luz(Color(0xFF8E7650), k), 20f + 40f * j, 120f, false, Offset(o.x - t * 0.2f, o.y - t * 0.2f), Size(t * 0.4f, t * 0.4f), style = Stroke(0.6f * u))
        drawLine(a.luz(P.CANAMO, k), o + Offset(-t * 0.1f, t * 0.22f), o + Offset(-t * 0.5f, t * 0.24f), 0.7f * u)
    }

    // ---------------------------------------------------------------- al pie

    /** Al pie: la muela de mano del contubernio, con su harina, y el ánfora de aceite apoyada en el pilar derecho, cortada por el borde. */
    fun DrawScope.alPie(a: AireRomano, suelo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val h = size.height
        if (h - suelo < 4f) return
        muela(a, Offset(w * 0.2f, minOf(h - 2f, suelo + (h - suelo) * 0.72f)), w * 0.2f, cerca)
        anfora(a, Offset(w * 0.95f, suelo + (h - suelo) * 0.1f), w * 0.11f, cerca)
    }

    /** La muela de mano: la piedra de abajo y la de encima, de basalto poroso, con la boca, el mango de madera y la harina. */
    private fun DrawScope.muela(a: AireRomano, pie: Offset, ancho: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(pie)
        val gr = ancho * 0.28f
        // La harina caída y la sombra.
        drawOval(a.luz(Color(0xFFEDE6D6), k).copy(alpha = 0.85f), Offset(pie.x - ancho * 0.7f, pie.y - ancho * 0.06f), Size(ancho * 1.4f, ancho * 0.14f))
        contactoRomano(pie + Offset(ancho * 0.1f, -ancho * 0.02f), ancho * 1.15f, ancho * 0.1f, a, 0.4f)
        for (capa in 0..1) {
            val an = if (capa == 0) ancho else ancho * 0.86f
            val base = pie.y - (if (capa == 0) 0f else gr)
            val r = Rect(pie.x - an / 2, base - gr, pie.x + an / 2, base)
            val elipse = an * 0.14f
            drawRect(a.luz(P.BASALTO, k), Offset(r.left, r.top), Size(r.width, r.height))
            drawOval(a.luz(P.BASALTO, k), Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse))
            drawRect(a.claro(0.18f), Offset(r.left, r.top), Size(r.width * 0.2f, r.height))
            drawRect(a.frio(0.3f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            drawOval(a.luz(mezcla(P.BASALTO, P.CAL, 0.25f), k), Offset(r.left, r.top - elipse / 2), Size(r.width, elipse))
            // Los poros del basalto.
            for (j in 0 until 10) drawCircle(a.frio(0.35f), 0.7f * u, Offset(r.left + r.width * azar(200 + capa, j), r.top + r.height * azar(210 + capa, j)))
        }
        // La boca de la piedra de encima, con grano, y el mango de madera clavado de canto.
        val tope = pie.y - 2 * gr
        drawOval(a.luz(P.TINTA, k).copy(alpha = 0.7f), Offset(pie.x - ancho * 0.08f, tope - ancho * 0.025f), Size(ancho * 0.16f, ancho * 0.05f))
        drawOval(a.luz(P.TRIGO, k), Offset(pie.x - ancho * 0.05f, tope - ancho * 0.02f), Size(ancho * 0.1f, ancho * 0.03f))
        val mx = pie.x + ancho * 0.34f
        drawLine(a.sombra(0.3f), Offset(mx + 2f * u, tope + gr * 0.3f), Offset(mx + 4f * u, tope - ancho * 0.3f), 3f * u)
        drawLine(a.luz(Color(0xFF8E6A44), k), Offset(mx, tope + gr * 0.3f), Offset(mx + 1.5f * u, tope - ancho * 0.32f), 3f * u, cap = StrokeCap.Round)
        drawLine(a.claro(0.3f), Offset(mx - 0.8f * u, tope + gr * 0.2f), Offset(mx + 0.6f * u, tope - ancho * 0.3f), 0.8f * u)
    }

    /** El ánfora globular de aceite de la Bética, apoyada de lado en el pilar derecho, con sus dos asas gruesas y un sello. */
    private fun DrawScope.anfora(a: AireRomano, c: Offset, r: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(c)
        clipRect(0f, 0f, size.width, size.height) {
            rotate(-14f, c) {
                drawCircle(a.sombra(0.35f), r, c + Offset(-r * 0.25f, r * 0.12f))
                // El cuello y la boca.
                drawRect(a.luz(P.BARRO, k), Offset(c.x - r * 0.2f, c.y - r * 1.3f), Size(r * 0.4f, r * 0.5f))
                drawRect(a.luz(Color(0xFFC98462), k), Offset(c.x - r * 0.28f, c.y - r * 1.38f), Size(r * 0.56f, r * 0.14f))
                // Las asas.
                for (s in listOf(-1f, 1f)) drawArc(a.luz(P.BARRO, k), if (s < 0) 180f else -90f, 90f, false,
                    Offset(c.x + s * r * 0.2f - r * 0.4f, c.y - r * 1.15f), Size(r * 0.8f, r * 0.7f), style = Stroke(r * 0.12f))
                drawCircle(a.luz(P.BARRO, k), r, c)
                drawCircle(a.claro(0.18f), r * 0.6f, c + Offset(-r * 0.3f, -r * 0.3f))
                drawArc(a.frio(0.3f), -40f, 150f, true, Offset(c.x - r, c.y - r), Size(r * 2, r * 2))
                drawOval(a.claro(0.3f), Offset(c.x - r * 0.62f, c.y - r * 0.6f), Size(r * 0.22f, r * 0.5f))
                // Las vueltas del torno y el sello en el asa.
                for (j in 0 until 3) drawArc(a.luz(Color(0xFF8E4E36), k).copy(alpha = 0.4f), 200f, 110f, false,
                    Offset(c.x - r * (0.9f - 0.1f * j), c.y - r * (0.6f - 0.25f * j)), Size(r * (1.8f - 0.2f * j), r * 0.9f), style = Stroke(0.6f * u))
                drawRect(a.luz(Color(0xFF8E4E36), k), Offset(c.x - r * 0.58f, c.y - r * 1.02f), Size(r * 0.18f, r * 0.08f))
                if (a.oscuro) drawArc(a.filo(0.5f * k), 200f, 70f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.4f * u))
            }
        }
    }
}
