package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/**
 * Los objetos del estante de Scriptorium ([MuebleEstanteMedieval]): sobre el tablero, la campanilla de bronce, el registro
 * de Cuaresma con la lista de los libros prestados colgando, el rollo de pieles para pergamino y la alcuza del aceite del
 * candil; en el muro, el candil y el manojo de llaves del arca; al pie, la escoba de brezo y el arca de las cartas; en los
 * huecos de las repisas, lo del encuadernador (las tablas para tapas, el ovillo de cáñamo con su aguja y el pote de cola).
 */
internal object PiezasEstanteMedieval {
    private val P = PaletaMedieval

    // ---------------------------------------------------------------- sobre el tablero

    fun DrawScope.arriba(a: AireMedieval, apoyo: Float, cerca: (Offset) -> Float, caida: (Offset, Float) -> Offset) {
        val w = size.width
        fun dePie(p: Offset, largo: Float): Offset = caida(p, largo).let { Offset(it.x, minOf(it.y, largo * 0.4f)) }
        val pieLlaves = Offset(w * 0.935f, w * 0.17f)
        llaves(pieLlaves, w * 0.05f, a, cerca(pieLlaves + Offset(0f, w * 0.06f)), caida(pieLlaves, w * 0.01f))
        val piePieles = Offset(w * 0.672f, apoyo)
        pieles(piePieles, w * 0.072f, w * 0.235f, a, cerca(piePieles - Offset(0f, w * 0.1f)), dePie(piePieles, w * 0.014f))
        val pieAlcuza = Offset(w * 0.79f, apoyo)
        alcuza(pieAlcuza, w * 0.074f, a, cerca(pieAlcuza - Offset(0f, w * 0.03f)), dePie(pieAlcuza, w * 0.01f))
        val pieReg = Offset(w * 0.255f, apoyo)
        registro(pieReg, w * 0.335f, a, cerca(pieReg + Offset(w * 0.15f, -w * 0.02f)), dePie(pieReg + Offset(w * 0.15f, 0f), w * 0.012f))
        val pieCamp = Offset(w * 0.13f, apoyo)
        campanilla(pieCamp, w * 0.07f, a, cerca(pieCamp - Offset(0f, w * 0.03f)), dePie(pieCamp, w * 0.01f))
    }

    /** La campanilla de mano: el bronce con su labio, el mango de madera torneado y su sombra. */
    private fun DrawScope.campanilla(pie: Offset, an: Float, a: AireMedieval, k: Float, caida: Offset) {
        val u = density
        val alto = an * 0.9f
        val cuerpo = Path().apply {
            moveTo(pie.x - an / 2, pie.y)
            cubicTo(pie.x - an * 0.42f, pie.y - alto * 0.3f, pie.x - an * 0.3f, pie.y - alto * 0.55f, pie.x - an * 0.28f, pie.y - alto * 0.85f)
            cubicTo(pie.x - an * 0.26f, pie.y - alto * 1.02f, pie.x + an * 0.26f, pie.y - alto * 1.02f, pie.x + an * 0.28f, pie.y - alto * 0.85f)
            cubicTo(pie.x + an * 0.3f, pie.y - alto * 0.55f, pie.x + an * 0.42f, pie.y - alto * 0.3f, pie.x + an / 2, pie.y)
            close()
        }
        translate(caida.x, caida.y) { drawPath(cuerpo, a.sombra(if (a.oscuro) 0.3f else 0.2f)) }
        contactoMedieval(Offset(pie.x + 2f * u, pie.y), an * 1.2f, 4f * u, a, 0.38f)
        // El mango, de madera torneada, con su bola.
        val m0 = pie.y - alto * 0.98f
        drawRect(a.luz(P.NOGAL_LUZ, k), Offset(pie.x - an * 0.07f, m0 - an * 0.42f), Size(an * 0.14f, an * 0.44f))
        drawRect(a.sombra(0.3f), Offset(pie.x + an * 0.01f, m0 - an * 0.42f), Size(an * 0.06f, an * 0.44f))
        drawCircle(a.luz(P.NOGAL_LUZ, k), an * 0.12f, Offset(pie.x, m0 - an * 0.48f))
        drawCircle(a.claro(0.4f), an * 0.04f, Offset(pie.x - an * 0.04f, m0 - an * 0.52f))
        drawPath(cuerpo, a.luz(P.BRONCE, k))
        clipPath(cuerpo) {
            drawRect(a.claro(0.45f), Offset(pie.x - an * 0.34f, pie.y - alto), Size(an * 0.12f, alto))
            drawRect(a.sombra(0.32f), Offset(pie.x + an * 0.14f, pie.y - alto), Size(an * 0.4f, alto))
            drawRect(a.sombra(0.2f), Offset(pie.x - an / 2, pie.y - alto * 0.62f), Size(an, 1.2f * u))
            drawRect(a.claro(0.25f), Offset(pie.x - an / 2, pie.y - alto * 0.62f + 1.2f * u), Size(an, 0.8f * u))
        }
        // El labio.
        drawOval(a.luz(mezcla(P.BRONCE, P.FUEGO, 0.2f), k), Offset(pie.x - an / 2, pie.y - 2.4f * u), Size(an, 3.6f * u))
        drawOval(a.sombra(0.45f), Offset(pie.x - an * 0.42f, pie.y - 1.8f * u), Size(an * 0.84f, 2.2f * u))
        if (a.oscuro) drawPath(cuerpo, a.filo(0.5f * k), style = Stroke(0.8f * u))
    }

    /**
     * El registro de Cuaresma tumbado: un códice grande de tablas desnudas con el lomo de badana, visto por su canto largo,
     * con su correa y su pasador; entre las hojas sale la lista de los libros prestados, que cuelga por delante del tablero.
     */
    private fun DrawScope.registro(izq: Offset, largo: Float, a: AireMedieval, k: Float, caida: Offset) {
        val u = density; val w = size.width
        val g = largo * 0.2f
        val y0 = izq.y - g
        val sol = !a.oscuro && !a.noche
        drawRect(a.sombra(if (a.oscuro) 0.34f else 0.2f), Offset(izq.x + caida.x * 2f, y0 + caida.y), Size(largo, g))
        contactoMedieval(Offset(izq.x + largo * 0.5f, izq.y), largo * 1.04f, 5f * u, a, 0.4f)
        val tabla = g * 0.22f
        // Las dos tablas de roble, y entre ellas el canto de las hojas.
        val roble = mezcla(P.NOGAL_LUZ, P.PINO, 0.4f)
        drawRect(a.luz(roble, k), Offset(izq.x, y0), Size(largo, tabla))
        drawRect(a.luz(roble, k), Offset(izq.x, izq.y - tabla), Size(largo, tabla))
        drawRect(a.luz(P.PERG_OSC, k), Offset(izq.x + 2f * u, y0 + tabla), Size(largo - 4f * u, g - 2 * tabla))
        for (j in 1 until 8) {
            val yy = y0 + tabla + (g - 2 * tabla) * j / 8f
            drawLine(a.luz(mezcla(P.PERG_OSC, P.CUERO, 0.4f), k).copy(alpha = 0.5f), Offset(izq.x + 2f * u, yy), Offset(izq.x + largo - 2f * u, yy + 0.4f * u), 0.5f * u)
        }
        drawRect(a.claro(0.4f), Offset(izq.x, y0), Size(largo, 1f * u))
        drawRect(a.claro(0.25f), Offset(izq.x + 2f * u, y0 + tabla), Size(largo * 0.6f, 1f * u))
        drawRect(a.sombra(0.3f), Offset(izq.x + largo * 0.86f, y0), Size(largo * 0.14f, g))
        // La cara de la tabla de arriba, que se ve apenas, con sus cantoneras de bronce.
        val fondo = g * 0.32f
        val cara = poligonoMedieval(izq.x, y0, izq.x + largo, y0, izq.x + largo - fondo * 0.5f, y0 - fondo, izq.x + fondo * 0.4f, y0 - fondo)
        drawPath(cara, a.luz(mezcla(roble, P.PINO, 0.3f), k))
        clipPath(cara) {
            for (j in 0 until 3) drawLine(a.sombra(0.16f), Offset(izq.x, y0 - fondo * (0.25f + 0.25f * j)), Offset(izq.x + largo, y0 - fondo * (0.2f + 0.25f * j)), 0.6f * u)
        }
        for (x in listOf(izq.x + largo * 0.94f, izq.x + largo * 0.25f)) {
            drawCircle(a.luz(P.BRONCE, k), 1.8f * u, Offset(x, y0 - fondo * 0.5f))
            drawCircle(a.claro(0.5f), 0.6f * u, Offset(x - 0.5f * u, y0 - fondo * 0.5f - 0.5f * u))
        }
        // El lomo de badana, a la izquierda, que monta sobre las tablas.
        drawRoundRect(a.luz(P.BADANA, k), Offset(izq.x - 2f * u, y0 - fondo * 0.8f), Size(largo * 0.12f, g + fondo * 0.8f), CornerRadius(2f * u))
        drawRect(a.sombra(0.2f), Offset(izq.x + largo * 0.08f, y0), Size(largo * 0.02f, g))
        drawRect(a.claro(0.3f), Offset(izq.x - 1f * u, y0 - fondo * 0.6f), Size(largo * 0.03f, g))
        // La correa de cuero que pasa por el canto y se clava al pasador.
        val xc = izq.x + largo * 0.62f
        drawRect(a.luz(P.CUERO, k), Offset(xc, y0 - 0.6f * u), Size(5f * u, g + 0.6f * u))
        drawRect(a.claro(0.3f), Offset(xc, y0 - 0.6f * u), Size(1f * u, g))
        clavoMedieval(Offset(xc + 2.5f * u, izq.y - tabla * 0.5f), 1.4f * u, a.luz(P.BRONCE, k), a, k)
        // La lista de los préstamos: una tira larga de pergamino que sale de entre las hojas y cuelga, rizada.
        val x0 = izq.x + largo * 0.38f
        val baja = w * 0.082f
        val tira = Path().apply {
            moveTo(x0, y0 + tabla)
            cubicTo(x0 + 2f * u, izq.y + 2f * u, x0 - 3f * u, izq.y + baja * 0.5f, x0 + 1f * u, izq.y + baja)
            cubicTo(x0 + 4f * u, izq.y + baja + 3f * u, x0 + 9f * u, izq.y + baja - 1f * u, x0 + 8f * u, izq.y + baja - 3f * u)
            lineTo(x0 + 10f * u, izq.y + baja * 0.92f)
            cubicTo(x0 + 6f * u, izq.y + baja * 0.5f, x0 + 11f * u, izq.y + 2f * u, x0 + 9f * u, y0 + tabla)
            close()
        }
        drawPath(Path().apply { addPath(tira, Offset(2.5f * u, 2f * u)) }, a.sombra(0.35f))
        drawPath(tira, a.etiqueta(maxOf(k, 0.2f)))
        clipPath(tira) {
            for (j in 0 until 7) {
                val yy = izq.y + 3f * u + j * baja / 7f
                drawLine(a.tinta.copy(alpha = 0.45f), Offset(x0 + 1.5f * u, yy), Offset(x0 + (4f + 4f * azar(1501, j)) * u, yy), 0.6f * u)
            }
            drawLine(P.RUBRICA.copy(alpha = 0.7f), Offset(x0 + 1f * u, izq.y + 1.5f * u), Offset(x0 + 8f * u, izq.y + 1.5f * u), 0.9f * u)
            drawRect(a.sombra(0.15f), Offset(x0 + 6f * u, y0), Size(6f * u, g + baja))
        }
        // El sol sobre el libro: el canto y la tabla de arriba se encienden.
        if (sol) {
            clipPath(cara) { drawRect(P.FUEGO.copy(alpha = 0.5f), Offset(izq.x + largo * 0.22f, y0 - fondo), Size(largo * 0.62f, fondo)) }
            drawRect(P.FUEGO.copy(alpha = 0.35f), Offset(izq.x + largo * 0.2f, y0), Size(largo * 0.6f, tabla))
            drawRect(Color.White.copy(alpha = 0.25f), Offset(izq.x + largo * 0.25f, y0 + tabla), Size(largo * 0.5f, g - 2 * tabla))
        }
        if (a.oscuro && k > 0.05f) drawRect(a.filo(0.6f * k), Offset(izq.x + largo * 0.3f, y0), Size(largo * 0.7f, 1f * u))
    }

    /** El rollo de pieles para pergamino, de pie y apoyado en el muro, atado con dos cordeles. */
    private fun DrawScope.pieles(pie: Offset, d: Float, alto: Float, a: AireMedieval, k: Float, caida: Offset) {
        val u = density
        rotate(-5f, pie) {
            val r = Rect(pie.x - d / 2, pie.y - alto, pie.x + d / 2, pie.y)
            drawRoundRect(a.sombra(if (a.oscuro) 0.3f else 0.18f), r.topLeft + Offset(caida.x * 2.4f, -caida.y * 0.2f), r.size, CornerRadius(d * 0.3f))
            drawRoundRect(a.luz(mezcla(P.BADANA, P.PERG_OSC, 0.4f), k), r.topLeft, r.size, CornerRadius(d * 0.25f))
            // El cilindro: la luz a un lado, la sombra al otro, y la última vuelta de la piel, que se despega.
            drawRect(a.claro(0.35f), Offset(r.left + d * 0.12f, r.top + 3f * u), Size(d * 0.16f, alto - 6f * u))
            drawRect(a.sombra(0.28f), Offset(r.right - d * 0.32f, r.top + 2f * u), Size(d * 0.32f, alto - 4f * u))
            drawLine(a.sombra(0.4f), Offset(r.left + d * 0.62f, r.top + 2f * u), Offset(r.left + d * 0.66f, r.bottom - 2f * u), 0.9f * u)
            // La boca de arriba, con la espiral de la piel.
            drawOval(a.luz(P.PERGAMINO, k), Offset(r.left, r.top - d * 0.16f), Size(d, d * 0.34f))
            drawOval(a.sombra(0.3f), Offset(r.left + d * 0.22f, r.top - d * 0.08f), Size(d * 0.56f, d * 0.18f), style = Stroke(0.8f * u))
            drawOval(a.sombra(0.5f), Offset(r.left + d * 0.4f, r.top - d * 0.03f), Size(d * 0.2f, d * 0.08f))
            // El borde de la piel, con sus barbas, que asoma por arriba.
            drawPath(poligonoMedieval(r.right - d * 0.1f, r.top, r.right + d * 0.06f, r.top - d * 0.3f, r.right + d * 0.12f, r.top - d * 0.18f, r.right, r.top + d * 0.2f), a.luz(P.BADANA, k))
            // Los cordeles.
            for (f in listOf(0.24f, 0.7f)) {
                val y = r.top + alto * f
                drawLine(a.luz(P.CUERO, k), Offset(r.left - 0.5f * u, y), Offset(r.right + 0.5f * u, y + 1.4f * u), 1.6f * u)
                drawLine(a.claro(0.3f), Offset(r.left, y - 0.6f * u), Offset(r.left + d * 0.4f, y - 0.2f * u), 0.5f * u)
            }
            if (a.oscuro && k > 0.05f) drawRect(a.filo(0.55f * k), Offset(r.right - 1.4f * u, r.top + 2f * u), Size(1.2f * u, alto - 4f * u))
        }
    }

    /** La alcuza de barro del aceite del candil, con su pico, el asa y el chorreón. */
    private fun DrawScope.alcuza(pie: Offset, an: Float, a: AireMedieval, k: Float, caida: Offset) {
        val u = density
        val alto = an * 1.3f
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.3f, pie.y)
            cubicTo(pie.x - an * 0.58f, pie.y - alto * 0.3f, pie.x - an * 0.5f, pie.y - alto * 0.62f, pie.x - an * 0.16f, pie.y - alto * 0.78f)
            lineTo(pie.x - an * 0.14f, pie.y - alto * 0.95f)
            lineTo(pie.x + an * 0.14f, pie.y - alto * 0.95f)
            lineTo(pie.x + an * 0.16f, pie.y - alto * 0.78f)
            cubicTo(pie.x + an * 0.5f, pie.y - alto * 0.62f, pie.x + an * 0.58f, pie.y - alto * 0.3f, pie.x + an * 0.3f, pie.y)
            close()
        }
        translate(caida.x, caida.y) { drawPath(cuerpo, a.sombra(if (a.oscuro) 0.3f else 0.2f)) }
        contactoMedieval(Offset(pie.x + 1.5f * u, pie.y), an * 1.1f, 4f * u, a, 0.4f)
        // El asa, detrás a la derecha.
        drawArc(a.luz(mezcla(P.BARRO, P.NOGAL_OSC, 0.2f), k), -90f, 180f, false, Offset(pie.x + an * 0.05f, pie.y - alto * 0.88f), Size(an * 0.5f, alto * 0.5f), style = Stroke(2.4f * u))
        drawPath(cuerpo, a.luz(P.BARRO, k))
        clipPath(cuerpo) {
            drawRect(a.claro(0.35f), Offset(pie.x - an * 0.4f, pie.y - alto), Size(an * 0.14f, alto))
            drawRect(a.sombra(0.3f), Offset(pie.x + an * 0.12f, pie.y - alto), Size(an * 0.5f, alto))
            // El torneado y el chorreón de aceite que oscurece el barro.
            for (j in 0 until 3) drawLine(a.sombra(0.15f), Offset(pie.x - an / 2, pie.y - alto * (0.2f + 0.16f * j)), Offset(pie.x + an / 2, pie.y - alto * (0.2f + 0.16f * j)), 0.6f * u)
            drawRect(a.sombra(0.25f), Offset(pie.x - an * 0.08f, pie.y - alto * 0.8f), Size(an * 0.12f, alto * 0.6f))
        }
        // El pico, hacia la izquierda.
        drawPath(poligonoMedieval(pie.x - an * 0.14f, pie.y - alto * 0.93f, pie.x - an * 0.36f, pie.y - alto * 1.02f, pie.x - an * 0.34f, pie.y - alto * 0.96f, pie.x - an * 0.14f, pie.y - alto * 0.84f), a.luz(P.BARRO, k))
        drawOval(a.sombra(0.5f), Offset(pie.x - an * 0.12f, pie.y - alto * 0.98f), Size(an * 0.24f, 2f * u))
        if (a.oscuro && k > 0.05f) drawPath(cuerpo, a.filo(0.45f * k), style = Stroke(0.8f * u))
    }

    /** El manojo de llaves del arca, colgado de una escarpia en el muro: la argolla y tres llaves de paletón. */
    private fun DrawScope.llaves(clavo: Offset, largo: Float, a: AireMedieval, k: Float, caida: Offset) {
        val u = density
        val hierro = a.luz(P.HIERRO, k)
        val anillo = Offset(clavo.x, clavo.y + 5f * u)
        val angulos = listOf(-14f, 4f, 20f)
        for (pasada in 0..1) {
            angulos.forEachIndexed { i, ang ->
                val l = largo * (0.85f + 0.2f * i)
                val desp = if (pasada == 0) caida * 1.6f else Offset.Zero
                rotate(ang, anillo + desp) {
                    val top = anillo + desp + Offset(0f, 3f * u)
                    val col = if (pasada == 0) a.sombra(if (a.oscuro) 0.25f else 0.18f) else hierro
                    drawCircle(col, 2.6f * u, top + Offset(0f, 2.4f * u), style = Stroke(1.3f * u))
                    drawLine(col, top + Offset(0f, 5f * u), top + Offset(0f, l), 1.5f * u)
                    drawRect(col, top + Offset(0f, l - 5f * u), Size(4f * u, 5f * u))
                    if (pasada == 1) {
                        drawRect(a.sombra(0.5f), top + Offset(1.5f * u, l - 3.5f * u), Size(1.2f * u, 2f * u))
                        drawLine(a.luz(P.HIERRO_LUZ, k), top + Offset(-0.5f * u, 5f * u), top + Offset(-0.5f * u, l - 1f * u), 0.5f * u)
                    }
                }
            }
        }
        drawCircle(hierro, 4.4f * u, anillo + Offset(0f, 1f * u), style = Stroke(1.3f * u))
        clavoMedieval(clavo, 1.6f * u, a.luz(P.HIERRO_LUZ, k), a, k)
    }

    // ---------------------------------------------------------------- el candil

    /** Dónde arde el candil, en píxeles. */
    private fun DrawScope.punta(): Offset = Offset(size.width * MuebleEstanteMedieval.LAMP_X, size.width * MuebleEstanteMedieval.LAMP_Y)

    /**
     * El candil de hierro colgado de su escarpia: el garabato, la varilla, la cazoleta con su pico (donde arde la mecha) y
     * la cazoleta de abajo que recoge lo que gotea. Apagado de día; de noche, la mecha arde y la cazoleta brilla por dentro.
     */
    fun DrawScope.candil(a: AireMedieval) {
        val u = density; val w = size.width * 1.35f
        val p = punta()
        val k = if (a.lumbre) 1f else 0.7f
        val hierro = a.luz(P.HIERRO, k)
        val esc = Offset(p.x + w * 0.04f, w * 0.1f)
        // La escarpia clavada en una junta, y el garabato.
        drawLine(a.sombra(0.35f), esc + Offset(1f * u, 1.5f * u), esc + Offset(-w * 0.012f, 2.5f * u), 2.4f * u)
        drawLine(hierro, esc, esc + Offset(-w * 0.012f, 1f * u), 2.4f * u, StrokeCap.Round)
        val gancho = Path().apply {
            moveTo(esc.x - w * 0.008f, esc.y - 1f * u)
            cubicTo(esc.x - w * 0.016f, esc.y - 4f * u, esc.x - w * 0.026f, esc.y + 1f * u, esc.x - w * 0.02f, esc.y + 5f * u)
        }
        drawPath(gancho, hierro, style = Stroke(1.4f * u, cap = StrokeCap.Round))
        val bowl = Offset(p.x + w * 0.028f, p.y + w * 0.006f)
        // La varilla, que baja hasta el asa de la cazoleta.
        drawLine(a.sombra(0.3f), Offset(esc.x - w * 0.02f + 2f * u, esc.y + 5f * u), Offset(bowl.x + 2f * u, bowl.y - w * 0.012f), 1.6f * u)
        drawLine(hierro, Offset(esc.x - w * 0.02f, esc.y + 5f * u), Offset(bowl.x, bowl.y - w * 0.014f), 1.6f * u, StrokeCap.Round)
        // La sombra del candil en el muro.
        if (!a.oscuro) drawOval(a.sombra(0.1f), Offset(bowl.x - w * 0.03f + w * 0.03f, bowl.y + w * 0.012f), Size(w * 0.07f, w * 0.018f))
        // La cazoleta de abajo.
        drawLine(hierro, Offset(bowl.x, bowl.y), Offset(bowl.x + w * 0.004f, bowl.y + w * 0.024f), 1.2f * u)
        drawArc(hierro, 0f, 180f, true, Offset(bowl.x - w * 0.024f, bowl.y + w * 0.016f), Size(w * 0.05f, w * 0.018f))
        drawLine(a.luz(P.HIERRO_LUZ, k), Offset(bowl.x - w * 0.024f, bowl.y + w * 0.025f), Offset(bowl.x + w * 0.026f, bowl.y + w * 0.025f), 1f * u)
        if (a.lumbre) drawLine(P.LLAMA.copy(alpha = 0.5f), Offset(bowl.x - w * 0.02f, bowl.y + w * 0.024f), Offset(bowl.x + w * 0.004f, bowl.y + w * 0.024f), 0.8f * u)
        // La cazoleta de arriba, con su pico hacia la izquierda.
        val caz = Path().apply {
            moveTo(p.x - w * 0.004f, p.y + w * 0.002f)
            cubicTo(p.x + w * 0.01f, p.y - w * 0.006f, bowl.x + w * 0.03f, bowl.y - w * 0.012f, bowl.x + w * 0.036f, bowl.y - w * 0.002f)
            cubicTo(bowl.x + w * 0.03f, bowl.y + w * 0.016f, p.x + w * 0.014f, p.y + w * 0.02f, p.x - w * 0.004f, p.y + w * 0.006f)
            close()
        }
        drawPath(caz, hierro)
        drawPath(Path().apply {
            moveTo(p.x + w * 0.004f, p.y)
            cubicTo(p.x + w * 0.014f, p.y - w * 0.004f, bowl.x + w * 0.026f, bowl.y - w * 0.01f, bowl.x + w * 0.032f, bowl.y - w * 0.004f)
        }, if (a.lumbre) P.LLAMA.copy(alpha = 0.7f) else a.luz(P.HIERRO_LUZ, k), style = Stroke(1f * u))
        // La mecha que sale del pico.
        drawLine(a.luz(P.NOGAL_OSC, k), Offset(p.x - w * 0.002f, p.y + w * 0.004f), Offset(p.x, p.y - 1f * u), 1.2f * u)
    }

    /** La llama del candil y su resplandor, encima de todo, con su propio color. */
    fun DrawScope.llamaCandil(a: AireMedieval) {
        val w = size.width
        val p = punta()
        val s = w * 0.028f
        val f = if (a.oscuro) 1f else 0.7f
        drawCircle(P.FUEGO.copy(alpha = 0.28f * f), s * 1.2f, p - Offset(0f, s * 0.45f))
        val lengua = Path().apply {
            moveTo(p.x - s * 0.24f, p.y)
            cubicTo(p.x - s * 0.36f, p.y - s * 0.42f, p.x - s * 0.06f, p.y - s * 0.72f, p.x - s * 0.04f, p.y - s * 1.2f)
            cubicTo(p.x + s * 0.12f, p.y - s * 0.66f, p.x + s * 0.34f, p.y - s * 0.38f, p.x + s * 0.22f, p.y)
            close()
        }
        drawPath(lengua, Color(0xFFFF9A34).copy(alpha = 0.92f * f))
        drawCircle(Color(0xFFFFF4D8).copy(alpha = 0.9f * f), s * 0.16f, p - Offset(0f, s * 0.24f))
    }

    // ---------------------------------------------------------------- al pie

    fun DrawScope.alPie(a: AireMedieval, suelo: Float, h: Float, d: DisposicionEstante, cerca: (Offset) -> Float) {
        val w = size.width
        val alto = h - suelo
        if (alto < 2f) return
        escoba(Offset(w * 0.03f, h - alto * 0.12f), d.izquierda.right * 0.8f, d.base.top - w * 0.06f, a, maxOf(cerca(Offset(w * 0.05f, suelo)), 0.1f))
        arca(Offset(w * 0.64f, h - alto * 0.1f), w * 0.42f, w * 0.15f, a, maxOf(cerca(Offset(w * 0.8f, suelo)), 0.1f))
    }

    /** La escoba de brezo apoyada en el pie derecho de la izquierda, con su atadura de mimbre. */
    private fun DrawScope.escoba(pie: Offset, xTope: Float, yTope: Float, a: AireMedieval, k: Float) {
        val u = density; val w = size.width
        val tope = Offset(xTope, yTope)
        val atado = Offset(pie.x + (tope.x - pie.x) * 0.22f, pie.y - (pie.y - tope.y) * 0.22f)
        contactoMedieval(Offset(pie.x + w * 0.02f, pie.y + 1f * u), w * 0.16f, 4f * u, a, 0.35f)
        drawLine(a.sombra(0.3f), tope + Offset(3f * u, 2f * u), atado + Offset(3f * u, 2f * u), 3.4f * u)
        drawLine(a.luz(mezcla(P.NOGAL_LUZ, P.PINO, 0.5f), k), tope, atado, 3.4f * u, StrokeCap.Round)
        drawLine(a.claro(0.35f), tope + Offset(-1f * u, 0f), atado + Offset(-1f * u, 0f), 0.8f * u)
        // Las ramas, que se abren hacia el suelo: las de atrás más oscuras.
        for (pasada in 0..1) for (i in 0 until 14) {
            val t = i / 13f
            val fin = Offset(pie.x - w * 0.05f + w * 0.13f * t + pasada * 2f * u, pie.y + 1f * u * azar(1511, i) - pasada * 1.5f * u)
            val tono = mezcla(P.CUERO, P.PINO, 0.2f + 0.5f * azar(1512 + pasada, i))
            drawLine(a.luz(if (pasada == 0) mezcla(tono, P.NOGAL_OSC, 0.4f) else tono, k), atado + Offset((t - 0.5f) * 5f * u, 0f), fin, (0.9f + 0.7f * azar(1513, i)) * u)
        }
        drawLine(a.luz(P.BADANA, k), atado + Offset(-3f * u, -1f * u), atado + Offset(3.5f * u, 1f * u), 2f * u)
        drawLine(a.luz(P.BADANA, k), atado + Offset(-3.5f * u, 3f * u), atado + Offset(4f * u, 5f * u), 2f * u)
    }

    /**
     * El arca de las cartas, cortada por el borde: tablones de roble con sus pies, flejes de hierro con los rizos, la
     * cerradura con su aldaba y, encima, un legajo atado.
     */
    private fun DrawScope.arca(izq: Offset, an: Float, alto: Float, a: AireMedieval, k: Float) {
        val u = density
        val r = Rect(izq.x, izq.y - alto, izq.x + an, izq.y - alto * 0.12f)
        contactoMedieval(Offset(r.center.x, izq.y), an * 1.1f, 8f * u, a, 0.45f)
        // Los pies (la prolongación de los montantes).
        for (x in listOf(r.left, r.left + an * 0.62f)) {
            drawRect(a.luz(mezcla(P.NOGAL, P.PINO, 0.3f), k), Offset(x, r.bottom), Size(an * 0.1f, izq.y - r.bottom))
            drawRect(a.sombra(0.3f), Offset(x + an * 0.07f, r.bottom), Size(an * 0.03f, izq.y - r.bottom))
        }
        val roble = mezcla(P.NOGAL, P.PINO, 0.3f)
        drawRect(a.luz(roble, k), r.topLeft, r.size)
        vetas(r, P.NOGAL_OSC.copy(alpha = 0.45f), 1521, densidad = 0.7f, grosor = 0.7f)
        // La tapa, que vuela un poco.
        drawRect(a.luz(mezcla(roble, P.NOGAL_LUZ, 0.4f), k), Offset(r.left - 3f * u, r.top - 6f * u), Size(an + 6f * u, 7f * u))
        drawRect(a.claro(0.35f), Offset(r.left - 3f * u, r.top - 6f * u), Size(an + 6f * u, 1.2f * u))
        drawRect(a.sombra(0.4f), Offset(r.left, r.top + 1f * u), Size(an, 2.5f * u))
        // Las juntas de los tablones y los montantes.
        drawLine(a.sombra(0.4f), Offset(r.left, r.center.y), Offset(r.right, r.center.y), 1f * u)
        drawRect(a.sombra(0.25f), Offset(r.left + an * 0.1f, r.top), Size(1.2f * u, r.height))
        // Los flejes, de arriba abajo, con sus clavos y sus rizos.
        for (x in listOf(r.left + an * 0.2f, r.left + an * 0.52f)) {
            drawRect(a.luz(P.HIERRO, k), Offset(x, r.top - 6f * u), Size(5f * u, r.height + 6f * u))
            drawRect(a.claro(0.25f), Offset(x, r.top - 6f * u), Size(1f * u, r.height + 6f * u))
            for (j in 0 until 4) clavoMedieval(Offset(x + 2.5f * u, r.top + r.height * (0.12f + 0.25f * j)), 1f * u, a.luz(P.HIERRO_LUZ, k), a, k)
            for (s in listOf(-1f, 1f)) drawArc(a.luz(P.HIERRO, k), if (s < 0) 180f else 0f, 180f, false, Offset(x + 2.5f * u + s * 3f * u - 3f * u, r.bottom - 6f * u), Size(6f * u, 6f * u), style = Stroke(1.4f * u))
        }
        // La cerradura con su aldaba.
        val c = Offset(r.left + an * 0.36f, r.top + r.height * 0.32f)
        drawRect(a.sombra(0.4f), Offset(c.x - 6f * u, c.y - 6f * u) + Offset(1f * u, 1.4f * u), Size(12f * u, 14f * u))
        drawRect(a.luz(P.HIERRO, k), Offset(c.x - 6f * u, c.y - 6f * u), Size(12f * u, 14f * u))
        drawRect(a.claro(0.25f), Offset(c.x - 6f * u, c.y - 6f * u), Size(12f * u, 1f * u))
        drawCircle(a.sombra(0.7f), 1.4f * u, c + Offset(0f, 2f * u))
        drawRect(a.sombra(0.7f), Offset(c.x - 0.6f * u, c.y + 2f * u), Size(1.2f * u, 3.5f * u))
        drawRect(a.luz(P.HIERRO_LUZ, k), Offset(c.x - 2f * u, r.top - 6f * u), Size(4f * u, 10f * u))
        // El legajo atado encima de la tapa.
        val lg = Rect(r.left + an * 0.04f, r.top - 6f * u - 9f * u, r.left + an * 0.3f, r.top - 6f * u)
        drawRect(a.sombra(0.3f), lg.topLeft + Offset(3f * u, 1f * u), lg.size)
        drawRect(a.luz(P.PERG_OSC, k), lg.topLeft, lg.size)
        for (j in 1 until 4) drawLine(a.sombra(0.25f), Offset(lg.left, lg.top + lg.height * j / 4f), Offset(lg.right, lg.top + lg.height * j / 4f), 0.5f * u)
        drawRect(a.luz(P.CUERO, k), Offset(lg.left + lg.width * 0.4f, lg.top - 0.5f * u), Size(3f * u, lg.height + 1f * u))
        drawCircle(a.luz(P.CERAS[1], k), 3.2f * u, Offset(lg.left + lg.width * 0.7f, lg.bottom + 3f * u))
        drawRect(a.sombra(0.25f), Offset(r.right - an * 0.15f, r.top), Size(an * 0.15f, r.height))
    }

    // ---------------------------------------------------------------- los huecos libres de las repisas

    /** Lo que hay donde una repisa queda libre: elegido por el ancho del hueco y por la repisa, para que no se repita. */
    fun DrawScope.huecoLibre(k: Int, r: Rect, oc: Float, a: AireMedieval, cerca: Float) {
        val u = density
        val libre = (r.right - oc) / u
        val y = r.bottom
        when {
            libre >= 150f -> if (k % 2 == 1) {
                tablasTapas(Offset(oc + 8f * u, y), 70f * u, a, cerca + 0.03f, k)
                ovillo(Offset(oc + 96f * u, y), 9f * u, a, cerca)
                if (libre >= 200f) poteCola(Offset(r.right - 40f * u, y), 20f * u, a, cerca - 0.03f)
            } else {
                poteCola(Offset(oc + 18f * u, y), 20f * u, a, cerca + 0.02f)
                tablasTapas(Offset(r.right - 92f * u, y), 76f * u, a, cerca - 0.03f, k)
            }
            libre >= 92f -> when (k % 3) {
                0 -> tablasTapas(Offset(oc + 6f * u, y), minOf(72f * u, libre * u - 14f * u), a, cerca, k)
                1 -> { ovillo(Offset(oc + 16f * u, y), 9f * u, a, cerca); poteCola(Offset(oc + 52f * u, y), 19f * u, a, cerca) }
                else -> { poteCola(Offset(oc + 18f * u, y), 19f * u, a, cerca); ovillo(Offset(oc + 56f * u, y), 8.5f * u, a, cerca) }
            }
            libre >= 50f -> when (k % 2) {
                0 -> poteCola(Offset((oc + r.right) / 2, y), 18f * u, a, cerca)
                else -> ovillo(Offset((oc + r.right) / 2, y), 9f * u, a, cerca)
            }
            libre >= 28f -> ovillo(Offset((oc + r.right) / 2, y), 7f * u, a, cerca)
        }
    }

    /** Las tablas de roble desbastadas para las tapas de un libro, apiladas y atadas con un cordel. */
    private fun DrawScope.tablasTapas(izq: Offset, an: Float, a: AireMedieval, k: Float, semilla: Int) {
        val u = density
        val g = 5f * u
        val n = 4
        contactoMedieval(Offset(izq.x + an * 0.5f, izq.y), an * 1.1f, 5f * u, a, 0.4f)
        drawRect(a.sombra(if (a.oscuro) 0.34f else 0.2f), Offset(izq.x + 8f * u, izq.y - n * g + 2f * u), Size(an, n * g - 2f * u))
        for (j in 0 until n) {
            val dx = (azar(semilla + 1531, j) - 0.5f) * 6f * u
            val r = Rect(izq.x + dx, izq.y - (j + 1) * g, izq.x + dx + an * (0.9f + 0.1f * azar(semilla + 1532, j)), izq.y - j * g)
            drawRect(a.luz(mezcla(mezcla(P.NOGAL_LUZ, P.PINO, 0.5f), P.NOGAL, 0.3f * azar(semilla + 1533, j)), k), r.topLeft, r.size)
            drawRect(a.claro(0.3f), r.topLeft, Size(r.width, 0.8f * u))
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - 0.8f * u), Size(r.width, 0.8f * u))
            // La testa de la tabla, con la sierra.
            drawRect(a.sombra(0.2f), Offset(r.right - 3f * u, r.top), Size(3f * u, r.height))
            for (q in 0 until 3) drawLine(a.sombra(0.18f), Offset(r.left + r.width * (0.2f + 0.25f * q), r.top + 1.5f * u), Offset(r.left + r.width * (0.3f + 0.25f * q), r.bottom - 1.5f * u), 0.5f * u)
        }
        // El cordel en cruz.
        val x = izq.x + an * 0.32f
        drawLine(a.luz(P.LINO, k), Offset(x, izq.y - n * g - 0.5f * u), Offset(x + 2f * u, izq.y), 1.4f * u)
        drawCircle(a.luz(P.LINO, k), 1.6f * u, Offset(x + 1f * u, izq.y - n * g))
        if (a.oscuro) drawRect(a.filo(0.5f * k), Offset(izq.x, izq.y - n * g), Size(an * 0.8f, 0.8f * u))
    }

    /** El ovillo de cáñamo para coser los cuadernillos, con la aguja clavada y la hebra que se suelta. */
    private fun DrawScope.ovillo(c0: Offset, r: Float, a: AireMedieval, k: Float) {
        val u = density
        val c = Offset(c0.x, c0.y - r)
        contactoMedieval(Offset(c.x + 1f * u, c0.y), r * 2.4f, 3.5f * u, a, 0.4f)
        drawCircle(a.luz(P.LINO, k), r, c)
        clipPath(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }) {
            for (j in 0 until 6) {
                val ang = 20f + 28f * j
                rotate(ang, c) { drawOval(a.sombra(0.22f), Offset(c.x - r * 1.2f, c.y - r * 0.25f + (j % 3 - 1) * r * 0.25f), Size(r * 2.4f, r * 0.5f), style = Stroke(0.6f * u)) }
            }
            drawCircle(a.claro(0.3f), r * 0.5f, c - Offset(r * 0.35f, r * 0.35f))
            drawCircle(a.sombra(0.3f), r, c + Offset(r * 0.45f, r * 0.35f))
        }
        // La aguja de hueso clavada.
        drawLine(a.luz(P.BADANA, k), c + Offset(-r * 0.2f, -r * 0.3f), c + Offset(r * 0.7f, -r * 1.5f), 1.1f * u, StrokeCap.Round)
        // La hebra que se suelta por el suelo de la repisa.
        drawPath(Path().apply {
            moveTo(c.x + r * 0.9f, c.y + r * 0.4f)
            cubicTo(c.x + r * 1.6f, c.y + r * 0.9f, c.x + r * 2.2f, c0.y - 1f * u, c.x + r * 3f, c0.y - 0.5f * u)
        }, a.luz(P.LINO, k), style = Stroke(0.7f * u))
    }

    /** El pote de cola de barro, con la brocha dentro y los chorreones secos. */
    private fun DrawScope.poteCola(pie: Offset, an: Float, a: AireMedieval, k: Float) {
        val u = density
        val alto = an * 0.8f
        contactoMedieval(Offset(pie.x + 1f * u, pie.y), an * 1.3f, 4f * u, a, 0.4f)
        // La brocha, inclinada.
        drawLine(a.luz(P.NOGAL_LUZ, k), Offset(pie.x - an * 0.1f, pie.y - alto * 0.6f), Offset(pie.x + an * 0.42f, pie.y - alto * 1.7f), 2f * u, StrokeCap.Round)
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.42f, pie.y)
            cubicTo(pie.x - an * 0.56f, pie.y - alto * 0.5f, pie.x - an * 0.5f, pie.y - alto * 0.86f, pie.x - an * 0.48f, pie.y - alto)
            lineTo(pie.x + an * 0.48f, pie.y - alto)
            cubicTo(pie.x + an * 0.5f, pie.y - alto * 0.86f, pie.x + an * 0.56f, pie.y - alto * 0.5f, pie.x + an * 0.42f, pie.y)
            close()
        }
        drawPath(cuerpo, a.luz(mezcla(P.BARRO, P.ARENISCA_OSC, 0.3f), k))
        clipPath(cuerpo) {
            drawRect(a.claro(0.3f), Offset(pie.x - an * 0.4f, pie.y - alto), Size(an * 0.14f, alto))
            drawRect(a.sombra(0.3f), Offset(pie.x + an * 0.12f, pie.y - alto), Size(an * 0.5f, alto))
            drawLine(a.luz(P.BADANA, k).copy(alpha = 0.7f), Offset(pie.x - an * 0.2f, pie.y - alto), Offset(pie.x - an * 0.22f, pie.y - alto * 0.55f), 1.6f * u)
        }
        drawOval(a.luz(mezcla(P.BARRO, P.ARENISCA_CLARA, 0.3f), k), Offset(pie.x - an * 0.5f, pie.y - alto - 2f * u), Size(an, 4f * u))
        drawOval(a.luz(P.BADANA, k * 0.8f), Offset(pie.x - an * 0.38f, pie.y - alto - 1f * u), Size(an * 0.76f, 2.4f * u))
        if (a.oscuro && k > 0.05f) drawPath(cuerpo, a.filo(0.4f * k), style = Stroke(0.7f * u))
    }
}
