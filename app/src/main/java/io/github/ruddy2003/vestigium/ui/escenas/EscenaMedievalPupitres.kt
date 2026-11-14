package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Los muebles del scriptorium. Al fondo ([fondo]): el banco de las tintas, bajo la ventana del claustro, y los
 * pupitres de los otros copistas, arrimados al muro del sur para coger la luz. Delante ([primerPlano]): el bastidor de
 * las pieles, el arca del pergamino, el facistol con el libro de donde copia, el pupitre de Odo con el libro que
 * escribe, su banco alto y, desde que llega, el banquillo de Heinrich.
 */
internal object PupitresMedieval {
    private val C = ColorMedieval
    private val S = SalaMedieval
    private val E = S.E

    private fun p(x: Float, y: Float, t: Float = 0f): Offset = E.p(x, y, t)

    /** La altura a la que pisa el gato. */
    const val PISO_GATO = 276f

    /** Dónde va el cántaro de ramas de los días de fiesta. */
    const val ADORNO = 132f

    /** Dónde se apoya el objeto del día: en el banco alto de Odo. */
    val BANCO = Offset(328f, 240.6f)

    // ---------------------------------------------------------------- El fondo de la sala

    fun DrawScope.fondo(m: Momento, a: Alumbrado) {
        bancoDeTintas(m, a)
        val sol = EscenaMedieval.sol(m)
        // A mediodía el sol cae a plomo sobre los pupitres del muro del sur.
        val alSol = if (sol != null && !sol.deLuna) ((sol.e - 0.5f) / 0.6f).coerceIn(0f, 1f) * sol.fuerza else 0f
        // La sombra de cada pupitre, hacia el lado contrario a la ventana.
        clipPath(E.formaDelSuelo()) { for (t in listOf(16f, 41f, 66f)) {
            // Con sol, cada pupitre corta la mancha con su sombra, tendida según la hora; sin sol, una sombra corta.
            val largo = if (sol != null && !sol.deLuna) (30f / sol.e).coerceIn(14f, 120f) else 26f; val corre = if (sol != null && !sol.deLuna) sol.a * largo else 4f
            val x1 = E.x0 + 30f
            drawPath(poligono(p(E.x0 + 5f, 196f, t - 9f), p(x1 + largo, 196f, t - 9f + corre), p(x1 + largo, 196f, t + 9f + corre), p(E.x0 + 5f, 196f, t + 9f)), C.SOMBRA.copy(alpha = if (sol != null) 0.5f else 0.3f), blendMode = BlendMode.Multiply)
        } }
        for ((i, t) in listOf(16f, 41f, 66f).withIndex()) pupitreDelMuro(m, a, t, i, alSol)
    }

    /** El banco donde se hace la tinta: el mortero de las agallas, la jarra del vino, los cuencos y un manojo de plumas por cortar. */
    private fun DrawScope.bancoDeTintas(m: Momento, a: Alumbrado) {
        val x0 = 120f; val x1 = 186f; val y = 181f; val d = 13f
        drawPath(poligono(p(x0, 196f, d), p(x1 + 12f, 196f, d + 3f), p(x1 + 12f, 196f, d + 9f), p(x0, 196f, d + 6f)), C.SOMBRA.copy(alpha = 0.26f), blendMode = BlendMode.Multiply)
        for (x in listOf(x0 + 3f, x1 - 3f)) drawPath(poligono(p(x - 2f, y, d), p(x + 2f, y, d), p(x + 3f, 196f, d), p(x - 3f, 196f, d)), a.en(p(x, 190f, d), C.MADERA_OSC))
        drawPath(poligono(p(x0, y, 1f), p(x1, y, 1f), p(x1, y, d), p(x0, y, d)), a.en(p(150f, y, 7f), lerp(C.MADERA, C.CAL, 0.12f)))
        drawPath(poligono(p(x0, y, d), p(x1, y, d), p(x1, y + 3.4f, d), p(x0, y + 3.4f, d)), a.en(p(150f, y + 2f, d), C.MADERA_OSC))
        // Manchas de tinta en el tablero.
        val az = Azar(Semilla.de("medieval", "tintas"))
        repeat(6) { val o = p(az.entre(x0 + 4f, x1 - 4f), y, az.entre(3f, 11f)); drawOval(a.en(o, C.TINTA).copy(alpha = 0.5f), o - Offset(2f, 0.7f), Size(az.entre(2f, 5f), 1.4f)) }
        // El mortero de piedra con su mano.
        val mo = p(134f, y, 7f)
        cuerpo(a, Path().apply { moveTo(mo.x - 6f, mo.y - 7f); lineTo(mo.x + 6f, mo.y - 7f); cubicTo(mo.x + 6f, mo.y - 1f, mo.x + 3f, mo.y, mo.x, mo.y); cubicTo(mo.x - 3f, mo.y, mo.x - 6f, mo.y - 1f, mo.x - 6f, mo.y - 7f); close() }, mo.x - 6f, mo.x + 6f, mo.y - 4f, lerp(C.ARENISCA, C.PIZARRA, 0.5f))
        drawOval(a.en(mo, lerp(C.TINTA, C.MADERA_OSC, 0.5f)), Offset(mo.x - 5.4f, mo.y - 8.2f), Size(10.8f, 2.6f)); drawLine(a.en(mo, C.MADERA), mo + Offset(1f, -7f), mo + Offset(6.6f, -14f), 1.6f, StrokeCap.Round)
        // La jarra de barro del vino y dos cuencos: la caparrosa y la goma.
        val j = p(152f, y, 5f)
        cuerpo(a, Path().apply { moveTo(j.x - 2.4f, j.y - 14f); lineTo(j.x + 2.4f, j.y - 14f); cubicTo(j.x + 7f, j.y - 9f, j.x + 6f, j.y - 1f, j.x + 3.4f, j.y); lineTo(j.x - 3.4f, j.y); cubicTo(j.x - 6f, j.y - 1f, j.x - 7f, j.y - 9f, j.x - 2.4f, j.y - 14f); close() }, j.x - 6f, j.x + 6f, j.y - 6f, C.LOSA)
        drawArc(a.en(j, lerp(C.LOSA, C.MADERA_OSC, 0.3f)), -80f, 160f, false, Offset(j.x + 2f, j.y - 12f), Size(6f, 8f), style = Stroke(1.1f))
        for ((x, c) in listOf(166f to lerp(C.VERDE, C.AZUL, 0.5f), 176f to lerp(C.ORO, C.MADERA, 0.5f))) { val o = p(x, y, 8f)
            drawPath(Path().apply { moveTo(o.x - 4f, o.y - 3.4f); lineTo(o.x + 4f, o.y - 3.4f); cubicTo(o.x + 3.4f, o.y, o.x - 3.4f, o.y, o.x - 4f, o.y - 3.4f); close() }, a.en(o, lerp(C.MADERA, C.ARENISCA, 0.4f))); drawOval(a.en(o, c), Offset(o.x - 3.4f, o.y - 4.4f), Size(6.8f, 1.9f)) }
        // Debajo del banco, un cesto con las agallas de roble.
        val ce = p(140f, 196f, 6f)
        cuerpo(a, poligono(ce + Offset(-9f, -9f), ce + Offset(9f, -9f), ce + Offset(7f, 0f), ce + Offset(-7f, 0f)), ce.x - 9f, ce.x + 9f, ce.y - 5f, lerp(C.MADERA, C.ARENISCA, 0.3f))
        for (k in 0..2) drawLine(a.en(ce, C.MADERA_OSC).copy(alpha = 0.5f), ce + Offset(-8.4f + k * 0.5f, -6.6f + k * 2.4f), ce + Offset(8.4f - k * 0.5f, -6.6f + k * 2.4f), 0.5f)
        for (k in 0..6) drawCircle(a.en(ce, lerp(C.MADERA_OSC, C.VERDE, 0.2f)), 1.5f, ce + Offset(-6f + k * 2f, -10f - (k % 2) * 0.8f))
    }

    /**
     * Un pupitre de los otros copistas, de lado: el tablero inclinado hacia la ventana, su costado y el banquillo. En
     * las horas de trabajo tiene la hoja a la vista y la pluma en el cuerno; si no, un paño encima.
     */
    private fun DrawScope.pupitreDelMuro(m: Momento, a: Alumbrado, t: Float, cual: Int, alSol: Float) {
        val xw = E.x0 + 5f; val xr = E.x0 + 30f; val d = 9f; val yw = 149f; val yr = 160f
        val enUso = EscenaMedieval.salaEnMarcha(m)
        // El costado que mira a la sala (en sombra) y el que mira a quien entra.
        drawPath(poligono(p(xr, yr, t - d), p(xr, yr, t + d), p(xr, 196f, t + d), p(xr, 196f, t - d)), a.en(p(xr, 178f, t), C.MADERA_OSC, 1f))
        val costado = poligono(p(xw, yw, t + d), p(xr, yr, t + d), p(xr, 196f, t + d), p(xw, 196f, t + d))
        drawPath(costado, a.en(p(xw + 12f, 176f, t + d), lerp(C.MADERA_OSC, C.MADERA, 0.4f)))
        clipPath(costado) { for (y in listOf(168f, 180f)) drawLine(a.en(p(xw, y, t + d), C.TINTA).copy(alpha = 0.3f), p(xw, y, t + d), p(xr, y, t + d), 0.5f) }
        // El tablero, que coge la luz de la ventana.
        val tablero = poligono(p(xw, yw, t - d), p(xw, yw, t + d), p(xr, yr, t + d), p(xr, yr, t - d))
        val madera = a.en(p(xw + 8f, yw, t), lerp(C.MADERA, C.CAL, 0.2f), -1f)
        drawPath(tablero, lerp(madera, Color(0xFFFFF4DC), 0.3f * alSol))
        fun en(u: Float, v: Float): Offset { val x = xw + (xr - xw) * u; val y = yw + (yr - yw) * u; return p(x, y, t - d + 2f * d * v) }
        if (enUso) {
            // La hoja, con sus renglones, y la pluma.
            val hoja = poligono(en(0.12f, 0.16f), en(0.12f, 0.84f), en(0.84f, 0.84f), en(0.84f, 0.16f))
            drawPath(hoja, lerp(a.en(en(0.5f, 0.5f), C.PERGAMINO, -1f), Color.White, 0.3f * alSol))
            val az = Azar(Semilla.de("medieval", "hoja", cual, m.ficha.dia))
            val llenas = 3 + az.entero(5)
            for (k in 0 until llenas) { val u = 0.2f + k * 0.085f; drawLine(a.en(en(u, 0.5f), C.TINTA).copy(alpha = 0.6f), en(u, 0.24f), en(u, 0.76f), 0.45f) }
            drawLine(a.en(en(0.5f, 0.9f), Color.White), en(0.3f, 0.9f), en(0.3f, 0.9f) + Offset(3f, -8f) * E.s(t), 0.8f * E.s(t), StrokeCap.Round)
        } else {
            val pano = poligono(en(0.04f, 0.08f), en(0.04f, 0.92f), en(0.98f, 0.96f), en(1.06f, 0.5f), en(0.98f, 0.04f))
            drawPath(pano, a.en(en(0.5f, 0.5f), lerp(C.PERGAMINO, C.PIZARRA, 0.3f), -1f))
            drawLine(a.en(en(0.5f, 0.5f), C.PIZARRA).copy(alpha = 0.4f), en(0.1f, 0.4f), en(0.96f, 0.46f), 0.5f)
        }
        // El cuerno de la tinta, metido en su agujero.
        val cuerno = en(0.1f, 0.94f)
        drawLine(a.en(cuerno, lerp(C.TINTA, C.MADERA_OSC, 0.4f)), cuerno, cuerno + Offset(1f, -4f) * E.s(t), 1.8f * E.s(t), StrokeCap.Round)
        drawLine(a.en(p(xw, yw, t), lerp(C.MADERA, C.CAL, 0.4f), -1f).copy(alpha = 0.7f), p(xw, yw, t - d), p(xw, yw, t + d), 0.7f)
        // El banquillo.
        val bx0 = xr + 7f; val bx1 = xr + 21f; val by = 173f
        for ((x, tt) in listOf(bx0 + 1f to t + 5f, bx1 - 1f to t + 5f, bx1 - 1f to t - 5f)) drawLine(a.en(p(x, 185f, tt), C.MADERA_OSC), p(x, by, tt), p(x + (if (x < bx0 + 3f) -1.4f else 1.4f), 196f, tt), 1.5f * E.s(tt))
        drawPath(poligono(p(bx0, by, t - 6f), p(bx1, by, t - 6f), p(bx1, by, t + 6f), p(bx0, by, t + 6f)), a.en(p(bx0 + 7f, by, t), lerp(C.MADERA, C.CAL, 0.1f)))
        drawPath(poligono(p(bx0, by, t + 6f), p(bx1, by, t + 6f), p(bx1, by + 2.4f, t + 6f), p(bx0, by + 2.4f, t + 6f)), a.en(p(bx0 + 7f, by + 1f, t + 6f), C.MADERA_OSC))
    }

    // ---------------------------------------------------------------- El primer plano

    /** Un punto del tablero del pupitre de Odo: [u] de izquierda a derecha y [v] de abajo arriba, de 0 a 1. */
    private fun tablero(u: Float, v: Float): Offset {
        val abajo = Offset(202f + 90f * u, 228f); val arriba = Offset(210f + 74f * u, 183f)
        return abajo + (arriba - abajo) * v
    }

    private fun cuadro(u0: Float, v0: Float, u1: Float, v1: Float): Path = poligono(tablero(u0, v0), tablero(u1, v0), tablero(u1, v1), tablero(u0, v1))

    /** Unos renglones de trazos que no se leen, entre [u0] y [u1], de [v] hacia abajo. */
    private fun DrawScope.renglones(a: Alumbrado, u0: Float, u1: Float, v: Float, cuantos: Int, az: Azar, tinta: Color = C.TINTA, paso: Float = 0.033f) {
        for (k in 0 until cuantos) {
            val vv = v - k * paso; var u = u0
            while (u < u1 - 0.01f) { val w = az.entre(0.02f, 0.06f).coerceAtMost(u1 - u); drawLine(a.en(tablero(u, vv), tinta).copy(alpha = 0.72f), tablero(u, vv), tablero(u + w, vv), 0.62f); u += w + 0.008f }
        }
    }

    private fun cosas(m: Momento, a: Alumbrado): List<Cosa> = buildList {
        val E0 = EscenaMedieval
        // ---- El bastidor de las pieles, apoyado en el muro: un marco de palos con la piel tensada por sus cordeles.
        add(Cosa(poligono(Offset(2f, 170f), Offset(43f, 176f), Offset(46f, 250f), Offset(0f, 261f)), Offset(24f, 257f)) {
            val palo = a.en(Offset(22f, 210f), lerp(C.MADERA, C.ARENISCA, 0.2f), 1f); val canto = a.en(Offset(22f, 210f), C.MADERA_OSC, 1f)
            val esquinas = listOf(Offset(2f, 170f), Offset(43f, 176f), Offset(46f, 250f), Offset(0f, 261f))
            if (E0.pieles(m)) {
                // La piel de ternero: clara, algo traslúcida, con el borde ondulado, las venas y el sitio del espinazo.
                val az = Azar(Semilla.de("medieval", "piel"))
                val piel = Path().apply {
                    moveTo(8f, 181f); cubicTo(16f, 178.6f, 28f, 184f, 37.6f, 183f); cubicTo(36f, 196f, 41f, 212f, 39.6f, 226f); cubicTo(40.6f, 234f, 38f, 240f, 40f, 244.6f)
                    cubicTo(30f, 245f, 18f, 251f, 6.6f, 252f); cubicTo(8f, 240f, 4.6f, 226f, 6.6f, 212f); cubicTo(5f, 200f, 9f, 190f, 8f, 181f); close()
                }
                val contraluz = (1f - a.l.noche) * 0.25f
                drawPath(piel, Brush.verticalGradient(0f to lerp(a.en(Offset(22f, 190f), lerp(C.PERGAMINO, C.ARENISCA, 0.3f), 1f), Color(0xFFFFF6DC), contraluz * 0.5f), 1f to a.en(Offset(22f, 244f), lerp(C.PERGAMINO, C.ARENISCA, 0.55f), 1f), startY = 180f, endY = 252f))
                clipPath(piel) {
                    drawLine(a.en(Offset(22f, 214f), lerp(C.PERGAMINO, C.ARENISCA, 0.5f)).copy(alpha = 0.5f), Offset(22f, 182f), Offset(23.4f, 250f), 2.2f)
                    repeat(7) { val o = Offset(az.entre(8f, 38f), az.entre(186f, 246f)); drawLine(a.en(o, lerp(C.PERGAMINO, C.ARENISCA, 0.6f)).copy(alpha = 0.4f), o, o + Offset(az.entre(-7f, 7f), az.entre(3f, 10f)), 0.4f) }
                    drawOval(a.en(Offset(30f, 230f), lerp(C.PERGAMINO, C.MADERA, 0.4f)).copy(alpha = 0.35f), Offset(26f, 226f), Size(8f, 10f))
                }
                drawPath(piel, a.en(Offset(22f, 214f), lerp(C.PERGAMINO, C.MADERA_OSC, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.6f))
                // Los cordeles, cada uno a su clavija.
                for ((de, al) in listOf(Offset(8f, 181f) to Offset(4f, 172f), Offset(22f, 180.6f) to Offset(22f, 173.4f), Offset(37.6f, 183f) to Offset(41f, 176.6f), Offset(39.6f, 214f) to Offset(44.4f, 213f), Offset(40f, 244.6f) to Offset(45f, 249f), Offset(22f, 248.6f) to Offset(22.6f, 255.6f), Offset(6.6f, 252f) to Offset(2f, 259f), Offset(6.4f, 214f) to Offset(1.4f, 216f)))
                    drawLine(a.en(de, lerp(C.PERGAMINO, C.MADERA, 0.5f)), de, al, 0.5f)
            }
            // Los cuatro palos, con sus clavijas.
            for (i in 0..3) { val p0 = esquinas[i]; val p1 = esquinas[(i + 1) % 4]; drawLine(canto, p0 + Offset(0.7f, 1f), p1 + Offset(0.7f, 1f), 4.4f, StrokeCap.Round); drawLine(palo, p0, p1, 3.4f, StrokeCap.Round) }
            for (q in listOf(Offset(4f, 172f), Offset(22f, 173.4f), Offset(41f, 176.6f), Offset(44.4f, 213f), Offset(45f, 249f), Offset(22.6f, 255.6f), Offset(2f, 259f), Offset(1.4f, 216f))) drawCircle(canto, 1.1f, q)
        })
        // ---- El arca del pergamino: roble con fajas de hierro; encima, las hojas ya cortadas y rayadas.
        add(Cosa(poligono(Offset(14f, 229f), Offset(92f, 229f), Offset(96f, 268f), Offset(10f, 268f)), Offset(53f, 268f)) {
            val frente = poligono(Offset(11f, 238f), Offset(95f, 238f), Offset(95f, 268f), Offset(11f, 268f))
            planoH(a, frente, 11f, 95f, 252f, C.MADERA_OSC)
            drawPath(poligono(Offset(17f, 229f), Offset(90f, 229f), Offset(95f, 238f), Offset(11f, 238f)), a.en(Offset(53f, 233f), lerp(C.MADERA_OSC, C.MADERA, 0.55f)))
            clipPath(frente) {
                val az = Azar(Semilla.de("medieval", "arca"))
                for (y in listOf(246f, 254f, 261f)) drawLine(a.en(Offset(53f, y), C.TINTA).copy(alpha = 0.4f), Offset(11f, y + az.entre(-0.5f, 0.5f)), Offset(95f, y + az.entre(-0.5f, 0.5f)), 0.5f)
                repeat(6) { val o = Offset(az.entre(14f, 92f), az.entre(240f, 266f)); drawLine(a.en(o, lerp(C.MADERA, C.CAL, 0.2f)).copy(alpha = 0.35f), o, o + Offset(az.entre(6f, 16f), az.entre(-0.4f, 0.4f)), 0.5f) }
            }
            for (x in listOf(24f, 82f)) { drawRect(a.en(Offset(x, 252f), C.HIERRO), Offset(x - 2f, 238f), Size(4f, 30f)); drawLine(a.en(Offset(x, 233f), C.HIERRO), Offset(x - 0.8f, 229f), Offset(x - 1.6f, 238f), 3.2f); for (y in listOf(243f, 252f, 262f)) drawCircle(a.en(Offset(x, y), lerp(C.HIERRO, C.CAL, 0.4f)), 0.7f, Offset(x, y)) }
            drawRect(a.en(Offset(53f, 245f), lerp(C.HIERRO, C.ORO, 0.3f)), Offset(50f, 240f), Size(6f, 7.4f)); drawCircle(a.en(Offset(53f, 244f), C.TINTA), 1f, Offset(53f, 243.4f))
            drawLine(a.en(Offset(53f, 238f), lerp(C.MADERA, C.CAL, 0.4f)).copy(alpha = 0.6f), Offset(11f, 238.3f), Offset(95f, 238.3f), 0.7f)
            // Las hojas de pergamino, apiladas y algo descuadradas, y la regla de rayar.
            val az = Azar(Semilla.de("medieval", "hojas"))
            for (k in 0..4) { val dx = az.entre(-1.6f, 1.6f); val y = 231.6f - k * 1.5f
                drawPath(poligono(Offset(64f + dx, y), Offset(88f + dx, y - 0.4f), Offset(90f + dx, y + 3.4f), Offset(62f + dx, y + 3.8f)), a.en(Offset(76f, y), lerp(C.PERGAMINO, C.ARENISCA, 0.12f * (k % 2))))
                drawLine(a.en(Offset(76f, y), C.MADERA_OSC).copy(alpha = 0.4f), Offset(62f + dx, y + 3.8f), Offset(90f + dx, y + 3.4f), 0.4f) }
            drawLine(a.en(Offset(76f, 224f), C.MADERA), Offset(66f, 226.4f), Offset(90f, 224f), 1.3f, StrokeCap.Round)
        })
        // ---- El facistol, con el libro de donde copia: viejo, de hojas más tostadas.
        add(Cosa(poligono(listOf(Offset(166f, 184f), Offset(198f, 180f), Offset(196f, 206f), Offset(186f, 208f), Offset(192f, 264f), Offset(172f, 264f), Offset(178f, 208f), Offset(168f, 208f))), Offset(182f, 264f)) {
            val pie = Offset(182f, 264f)
            for (dx in listOf(-10f, 0f, 10f)) drawLine(a.en(pie, C.MADERA_OSC), Offset(182f, 250f), Offset(182f + dx, if (dx == 0f) 266f else 264f), 2f, StrokeCap.Round)
            cuerpo(a, poligono(Offset(180f, 206f), Offset(184f, 206f), Offset(185f, 252f), Offset(179f, 252f)), 179f, 185f, 230f, C.MADERA)
            for (y in listOf(214f, 240f)) drawRect(a.en(Offset(182f, y), lerp(C.MADERA, C.CAL, 0.2f)), Offset(178f, y), Size(8f, 2.4f))
            // El tablero y el libro, vuelto hacia el pupitre de Odo.
            val tabla = poligono(Offset(166f, 207f), Offset(198f, 203f), Offset(196f, 183f), Offset(168f, 187f))
            drawPath(tabla, a.en(Offset(182f, 196f), lerp(C.MADERA, C.CAL, 0.1f), -1f)); drawLine(a.en(Offset(182f, 206f), C.MADERA_OSC), Offset(165f, 208.4f), Offset(199f, 204.2f), 2f)
            val viejo = lerp(C.PERGAMINO, C.ARENISCA, 0.3f)
            if (E0.copiando(m)) {
                drawPath(poligono(Offset(168.6f, 205f), Offset(196f, 201.6f), Offset(194.4f, 185.6f), Offset(170f, 189f)), a.en(Offset(182f, 196f), lerp(C.ROJO, C.MADERA_OSC, 0.5f)))
                for ((x0, x1) in listOf(170f to 181.4f, 182.6f to 194.6f)) {
                    drawPath(poligono(Offset(x0, 204f - (x0 - 168f) * 0.12f), Offset(x1, 204f - (x1 - 168f) * 0.12f), Offset(x1 - 0.8f, 188f - (x1 - 168f) * 0.12f), Offset(x0 + 0.6f, 188f - (x0 - 168f) * 0.12f)), a.en(Offset((x0 + x1) / 2f, 196f), viejo, -1f))
                    for (k in 0..7) { val y = 190.6f + k * 1.7f - (x0 - 168f) * 0.12f; drawLine(a.en(Offset(x0, y), C.TINTA).copy(alpha = 0.55f), Offset(x0 + 1.6f, y), Offset(x1 - 1.8f, y - 1.2f), 0.4f) }
                }
                drawRect(a.en(Offset(172f, 191f), C.ROJO), Offset(171.8f, 189.6f), Size(2.6f, 2.8f))
            } else {
                // Cerrado: la tapa de cuero con sus bullones.
                val tapa = poligono(Offset(171f, 204f), Offset(194f, 201f), Offset(192.6f, 187.6f), Offset(172f, 190.4f))
                drawPath(tapa, a.en(Offset(182f, 196f), lerp(C.ROJO, C.MADERA_OSC, 0.55f), -1f))
                for (q in listOf(Offset(174f, 201.6f), Offset(191f, 199.4f), Offset(190f, 190f), Offset(174.6f, 192.2f), Offset(182.4f, 195.8f))) drawCircle(a.en(q, C.ORO), 0.9f, q)
            }
        })
        // ---- El pupitre de Odo.
        add(Cosa(poligono(listOf(Offset(206f, 180f), Offset(288f, 180f), Offset(298f, 228f), Offset(296f, 264f), Offset(198f, 264f), Offset(196f, 228f))), Offset(247f, 264f)) { pupitreDeOdo(m, a) })
        // ---- Su banco alto, apartado a un lado: encima, el objeto del día.
        add(Cosa(poligono(Offset(298f, 236f), Offset(356f, 234f), Offset(362f, 266f), Offset(298f, 268f)), Offset(328f, 267f)) {
            for ((x0, x1) in listOf(303f to 300f, 353f to 357f, 310f to 309f, 346f to 348f)) drawLine(a.en(Offset(x0, 255f), if (x0 in 305f..350f) lerp(C.MADERA_OSC, C.TINTA, 0.3f) else C.MADERA_OSC), Offset(x0, 244f), Offset(x1, if (x0 in 305f..350f) 262f else 267f), 2.4f, StrokeCap.Round)
            drawLine(a.en(Offset(328f, 258f), C.MADERA_OSC), Offset(301.6f, 258f), Offset(355.4f, 257f), 1.7f)
            val asiento = poligono(Offset(302f, 236.4f), Offset(350f, 234.6f), Offset(358f, 242.6f), Offset(298f, 245f))
            drawPath(asiento, a.en(Offset(250f, 200f), lerp(C.MADERA, C.CAL, 0.35f)))
            drawPath(poligono(Offset(298f, 245f), Offset(358f, 242.6f), Offset(358f, 246f), Offset(298f, 248.4f)), a.en(Offset(328f, 246f), C.MADERA_OSC))
            clipPath(asiento) { for (k in 0..2) drawLine(a.en(Offset(328f, 240f), C.MADERA_OSC).copy(alpha = 0.3f), Offset(300f, 238.4f + k * 2.2f), Offset(358f, 236.6f + k * 2.2f), 0.4f) }
            with(ObjetosMedieval) { objeto(m.ficha.objeto, BANCO, 1.6f) { lerp(a.en(BANCO, it), a.en(Offset(230f, 190f), lerp(it, Color.White, 0.08f)), 0.6f) } }
        })
        if (E0.fiesta(m) || E0.campanas(m)) {
            // ---- En fiesta, un cántaro con ramas verdes y flores del huerto, en el suelo, junto al arca.
            add(Cosa(poligono(Offset(120f, 226f), Offset(146f, 226f), Offset(142f, 264f), Offset(124f, 264f)), Offset(ADORNO, 264f)) {
                val jarra = Path().apply { moveTo(127f, 264f); cubicTo(120f, 256f, 123f, 246f, 128.4f, 243f); lineTo(135.6f, 243f); cubicTo(141f, 246f, 144f, 256f, 137f, 264f); close() }
                cuerpo(a, jarra, 122f, 142f, 254f, C.LOSA); drawOval(a.en(Offset(132f, 243f), lerp(C.LOSA, C.TINTA, 0.5f)), Offset(128f, 241.8f), Size(8f, 2.4f))
                val az = Azar(Semilla.de("medieval", "ramo"))
                repeat(9) { val q = Offset(132f + az.entre(-13f, 13f), 228f + az.entre(-9f, 8f))
                    drawLine(a.en(q, lerp(C.VERDE, C.TINTA, 0.3f)), Offset(132f, 243f), q, 0.7f)
                    for (k in 0..2) { val h = Offset(132f, 243f) + (q - Offset(132f, 243f)) * (0.5f + 0.22f * k); drawOval(a.en(h, lerp(C.VERDE, if (k == 1) C.ORO else C.TINTA, 0.2f)), h - Offset(2.6f, 1.2f), Size(5.2f, 2.4f)) }
                    if (it % 2 == 0) drawCircle(a.en(q, listOf(Color.White, C.ROJO, C.ORO)[it % 3]), 1.7f, q) }
            })
        }
        if (E0.heinrich(m)) {
            // ---- El banquillo de Heinrich y su tabla de escribir: la tablilla de cera y, con los años, una hoja de verdad.
            add(Cosa(poligono(Offset(352f, 244f), Offset(380f, 244f), Offset(380f, 270f), Offset(350f, 270f)), Offset(366f, 270f)) {
                for (x in listOf(354f, 376f)) drawLine(a.en(Offset(x, 260f), C.MADERA_OSC), Offset(x + 1f, 252f), Offset(x, 270f), 2f, StrokeCap.Round)
                drawPath(poligono(Offset(352f, 247.4f), Offset(382f, 246f), Offset(384f, 252f), Offset(350f, 253.6f)), a.en(Offset(366f, 250f), lerp(C.MADERA, C.CAL, 0.12f)))
                drawPath(poligono(Offset(350f, 253.6f), Offset(384f, 252f), Offset(384f, 255f), Offset(350f, 256.6f)), a.en(Offset(366f, 254f), C.MADERA_OSC))
                if (E0.heinrichCopia(m)) {
                    drawPath(poligono(Offset(356f, 251.6f), Offset(374f, 250.8f), Offset(373f, 241.6f), Offset(357f, 242.4f)), a.en(Offset(365f, 246f), C.PERGAMINO))
                    for (k in 0..3) drawLine(a.en(Offset(365f, 246f), C.TINTA).copy(alpha = 0.6f), Offset(358.6f, 244.4f + k * 1.8f), Offset(371.4f - k % 2 * 3f, 243.8f + k * 1.8f), 0.5f)
                } else {
                    drawPath(poligono(Offset(356f, 251.6f), Offset(374f, 250.8f), Offset(373f, 241.6f), Offset(357f, 242.4f)), a.en(Offset(365f, 246f), C.MADERA))
                    drawPath(poligono(Offset(357.6f, 250.4f), Offset(372.6f, 249.8f), Offset(371.8f, 243f), Offset(358.4f, 243.6f)), a.en(Offset(365f, 246f), lerp(C.TINTA, C.VERDE, 0.2f)))
                    for (k in 0..2) drawLine(a.en(Offset(365f, 246f), C.PERGAMINO).copy(alpha = 0.5f), Offset(359.6f, 245f + k * 1.8f), Offset(368f + k * 1.2f, 244.6f + k * 1.8f), 0.4f)
                }
            })
        }
    }

    /** El pupitre grande: el cuerpo con su estante, el tablero inclinado, el libro que copia, los cuernos y la pluma. */
    private fun DrawScope.pupitreDeOdo(m: Momento, a: Alumbrado) {
        val E0 = EscenaMedieval
        // El cuerpo: dos costados y, entre ellos, el estante donde se apilan los cuadernos ya escritos.
        val cuerpo = poligono(Offset(200f, 230f), Offset(294f, 230f), Offset(296f, 264f), Offset(198f, 264f))
        planoH(a, cuerpo, 198f, 296f, 248f, C.MADERA_OSC)
        val hueco = poligono(Offset(208f, 235f), Offset(286f, 235f), Offset(287f, 258f), Offset(207f, 258f))
        drawPath(hueco, a.en(Offset(247f, 246f), lerp(C.TINTA, C.MADERA_OSC, 0.35f)))
        drawRect(a.en(Offset(247f, 259f), lerp(C.MADERA, C.CAL, 0.1f)), Offset(207f, 258f), Size(80f, 1.8f))
        clipPath(hueco) {
            // Los cuadernos del tomo que copia: cada uno, un pliego de hojas dobladas.
            val n = E0.cuadernos(m); val az = Azar(Semilla.de("medieval", "cuadernos"))
            for (k in 0 until n) { val dx = az.entre(-1.4f, 1.4f); val y = 255f - k * 2.5f
                drawRect(a.en(Offset(226f, y), lerp(C.PERGAMINO, C.ARENISCA, 0.1f + 0.1f * (k % 2))), Offset(212f + dx, y), Size(28f, 2.5f))
                drawLine(a.en(Offset(226f, y), C.MADERA_OSC).copy(alpha = 0.5f), Offset(212f + dx, y + 2.5f), Offset(240f + dx, y + 2.5f), 0.4f)
                drawLine(a.en(Offset(226f, y), C.ROJO).copy(alpha = 0.6f), Offset(212.6f + dx, y + 0.4f), Offset(212.6f + dx, y + 2.1f), 0.6f) }
            // Lo que guarda siempre ahí: la caja de las plumas y unos rollos de retales.
            drawRect(a.en(Offset(272f, 254f), lerp(C.MADERA, C.ARENISCA, 0.2f)), Offset(264f, 251f), Size(20f, 7f)); drawLine(a.en(Offset(272f, 251f), C.MADERA_OSC), Offset(264f, 251.3f), Offset(284f, 251.3f), 0.6f)
            for ((x, y) in listOf(267f to 249.6f, 271f to 248.6f, 275.6f to 249.8f)) drawLine(a.en(Offset(x, y), Color.White), Offset(x, 251f), Offset(x + 1.6f, y - 5f), 1.1f, StrokeCap.Round)
            if (E0.tomos(m) == 0) for ((x, y) in listOf(248f to 255.4f, 253.4f to 255.4f, 250.8f to 251.2f)) { drawCircle(a.en(Offset(x, y), lerp(C.PERGAMINO, C.ARENISCA, 0.2f)), 2.6f, Offset(x, y)); drawCircle(a.en(Offset(x, y), lerp(C.PERGAMINO, C.MADERA_OSC, 0.5f)), 1f, Offset(x, y), style = Stroke(0.4f)) }
            // Los tomos ya encuadernados, tumbados: tapas de tabla forradas de cuero, con sus bullones y sus broches.
            for (k in 0 until E0.tomos(m)) { val y = 250f - k * 8.4f
                drawRect(a.en(Offset(266f, y), lerp(C.ROJO, C.MADERA_OSC, 0.45f)), Offset(246f, y), Size(38f, 8f)); drawRect(a.en(Offset(266f, y + 4f), C.PERGAMINO), Offset(247f, y + 1.3f), Size(1.6f, 5.4f))
                drawRect(a.en(Offset(266f, y + 4f), lerp(C.PERGAMINO, C.ARENISCA, 0.2f)), Offset(281f, y + 1.3f), Size(3f, 5.4f))
                for (x in listOf(256f, 270f)) drawRect(a.en(Offset(x, y + 4f), C.ORO), Offset(x, y + 0.4f), Size(1.5f, 7.2f)) }
        }
        // Tablas, vetas y el canto de luz.
        for (x in listOf(203f, 291f)) drawLine(a.en(Offset(x, 248f), C.TINTA).copy(alpha = 0.3f), Offset(x, 231f), Offset(x + (if (x < 240f) -0.6f else 0.6f), 264f), 0.5f)
        if (E0.llave(m)) {
            // La llave del armario de los libros, colgada de un clavo.
            val k = Offset(202.6f, 238f)
            drawCircle(a.en(k, C.HIERRO), 0.8f, k); drawCircle(a.en(k, lerp(C.HIERRO, C.CAL, 0.3f)), 2.2f, k + Offset(0f, 3.4f), style = Stroke(1f))
            drawLine(a.en(k, lerp(C.HIERRO, C.CAL, 0.3f)), k + Offset(0f, 5.6f), k + Offset(0f, 15f), 1.1f); drawLine(a.en(k, lerp(C.HIERRO, C.CAL, 0.3f)), k + Offset(0f, 12f), k + Offset(2.4f, 12f), 1f); drawLine(a.en(k, lerp(C.HIERRO, C.CAL, 0.3f)), k + Offset(0f, 14.6f), k + Offset(2.4f, 14.6f), 1f)
        }
        // El tablero inclinado: lo que mira a la ventana, más claro. Abajo, el borde donde se apoya la hoja.
        val tabla = cuadro(0f, 0f, 1f, 1f)
        drawPath(tabla, Brush.horizontalGradient(0f to a.en(tablero(0f, 0.5f), lerp(C.MADERA, C.CAL, 0.28f), -1f), 1f to a.en(tablero(1f, 0.5f), C.MADERA, 1f), startX = 204f, endX = 292f))
        clipPath(tabla) {
            val az = Azar(Semilla.de("medieval", "tablero"))
            repeat(9) { val v = az.entre(0.04f, 0.96f); drawLine(a.en(tablero(0.5f, v), C.MADERA_OSC).copy(alpha = 0.28f), tablero(0f, v), tablero(1f, v + az.entre(-0.02f, 0.02f)), 0.5f) }
            repeat(7) { val o = tablero(az.entre(0.05f, 0.95f), az.entre(0.05f, 0.95f)); drawOval(a.en(o, C.TINTA).copy(alpha = az.entre(0.2f, 0.45f)), o - Offset(1.4f, 0.8f), Size(az.entre(1.6f, 4f), az.entre(1f, 2f))) }
        }
        drawLine(a.en(tablero(0.5f, 1f), lerp(C.MADERA, C.CAL, 0.4f)).copy(alpha = 0.7f), tablero(0f, 1f), tablero(1f, 1f), 0.9f)
        drawPath(poligono(Offset(199f, 226.4f), Offset(295f, 226.4f), Offset(296f, 231f), Offset(198f, 231f)), a.en(Offset(247f, 229f), lerp(C.MADERA_OSC, C.MADERA, 0.25f)))
        drawLine(a.en(Offset(247f, 226f), lerp(C.MADERA, C.CAL, 0.4f)).copy(alpha = 0.7f), Offset(199f, 226.6f), Offset(295f, 226.6f), 0.7f)
        if (E0.enLorsch(m)) {
            // Su sitio vacío: los dos agujeros de los cuernos, sin cuerno, y el polvo que se va posando.
            for (v in listOf(0.82f, 0.64f)) drawOval(a.en(tablero(0.93f, v), C.TINTA).copy(alpha = 0.7f), tablero(0.93f, v) - Offset(2f, 1.2f), Size(4f, 2.4f))
            return
        }
        val az = Azar(Semilla.de("medieval", "pagina", m.ficha.dia))
        val hoja = a.en(tablero(0.5f, 0.5f), C.PERGAMINO, -1f)
        if (E0.copiando(m)) {
            // El cuaderno abierto: a la izquierda, la página escrita ayer; a la derecha, la de hoy.
            val grande = !E0.libroNuevo(m)
            val u0 = if (grande) 0.1f else 0.2f; val u1 = if (grande) 0.84f else 0.74f; val v0 = if (grande) 0.07f else 0.14f; val v1 = if (grande) 0.93f else 0.8f; val um = (u0 + u1) / 2f
            drawPath(cuadro(u0 - 0.012f, v0 - 0.02f, u1 + 0.012f, v1), a.en(tablero(um, 0.5f), lerp(C.PERGAMINO, C.ARENISCA, 0.45f)))
            drawPath(cuadro(u0, v0, um - 0.006f, v1), hoja); drawPath(cuadro(um + 0.006f, v0, u1, v1), lerp(hoja, a.en(tablero(um, 0.5f), C.CAL), 0.2f))
            drawLine(a.en(tablero(um, 0.5f), lerp(C.PERGAMINO, C.MADERA_OSC, 0.5f)), tablero(um, v0), tablero(um, v1), 0.8f)
            val filas = if (grande) 22 else 16; val paso = (v1 - v0 - 0.12f) / filas
            // La página de ayer: entera, con su rúbrica y, desde que la pintó, la gran inicial.
            drawLine(a.en(tablero(u0, v1), C.ROJO), tablero(u0 + 0.03f, v1 - 0.05f), tablero(um - 0.1f, v1 - 0.05f), 0.8f)
            val conInicial = grande
            if (conInicial) inicial(a, u0 + 0.03f, v1 - 0.1f, E0.inicial(m))
            renglones(a, u0 + 0.03f + (if (conInicial) 0.1f else 0f), um - 0.03f, v1 - 0.1f, 5, az, paso = paso)
            renglones(a, u0 + 0.03f, um - 0.03f, v1 - 0.1f - 5 * paso, filas - 5, az, paso = paso)
            // La de hoy: tantos renglones como horas lleva.
            val hoy = (E0.renglones(m) * filas / 22f).toInt()
            // El rayado a punta seca, que se ve en lo que falta por escribir.
            for (k in hoy until filas) { val v = v1 - 0.06f - k * paso; drawLine(a.en(tablero(um, v), lerp(C.PERGAMINO, C.ARENISCA, 0.5f)).copy(alpha = 0.5f), tablero(um + 0.03f, v), tablero(u1 - 0.03f, v), 0.3f) }
            renglones(a, um + 0.03f, u1 - 0.03f, v1 - 0.06f, hoy, az, paso = paso)
            if (E0.piedraDeLeer(m) && !E0.cubierto(m)) {
                // La piedra de leer: media bola de cristal de roca, que agranda la letra que tiene debajo.
                val o = tablero(u0 + 0.16f, 0.42f)
                drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(o.x - 5.6f, o.y - 1.6f), Size(12f, 4.6f))
                drawPath(Path().apply { moveTo(o.x - 5f, o.y); cubicTo(o.x - 5f, o.y - 7f, o.x + 5f, o.y - 7f, o.x + 5f, o.y); cubicTo(o.x + 3f, o.y + 1.6f, o.x - 3f, o.y + 1.6f, o.x - 5f, o.y); close() }, lerp(hoja, Color.White, 0.5f).copy(alpha = 0.85f))
                drawLine(a.en(o, C.TINTA).copy(alpha = 0.6f), o + Offset(-3f, -1.6f), o + Offset(3f, -1.6f), 1f); drawArc(Color.White.copy(alpha = 0.8f), 200f, 70f, false, Offset(o.x - 4f, o.y - 5.6f), Size(8f, 8f), style = Stroke(0.7f))
            }
        } else {
            // Sin libro grande entre manos: una hoja suelta, con unas líneas.
            drawPath(cuadro(0.3f, 0.2f, 0.62f, 0.78f), hoja)
            if (!E0.cerrado(m)) renglones(a, 0.33f, 0.59f, 0.72f, 3 + E0.renglones(m) / 3, az)
        }
        if (E0.cubierto(m)) {
            // El paño con que cubre la página al irse: lino crudo, con sus dobleces, y una esquina de la hoja que asoma.
            val pano = Path().apply {
                moveTo(tablero(0.06f, 0.98f)); lineTo(tablero(0.88f, 0.99f)); cubicTo(tablero(0.9f, 0.6f), tablero(0.87f, 0.3f), tablero(0.9f, -0.08f))
                cubicTo(tablero(0.7f, -0.02f), tablero(0.6f, -0.12f), tablero(0.44f, -0.05f)); cubicTo(tablero(0.3f, -0.12f), tablero(0.2f, -0.02f), tablero(0.1f, 0.12f)); close()
            }
            val lino = lerp(C.PERGAMINO, C.PIZARRA, 0.3f)
            drawPath(pano, Brush.horizontalGradient(0f to a.en(tablero(0.1f, 0.5f), lerp(lino, C.CAL, 0.3f), -1f), 1f to a.en(tablero(0.9f, 0.5f), lino, 1f), startX = 206f, endX = 286f))
            clipPath(pano) {
                for ((u, ancho) in listOf(0.3f to 0.05f, 0.56f to 0.06f, 0.76f to 0.04f)) { drawPath(poligono(tablero(u, 1f), tablero(u + ancho, 1f), tablero(u + ancho + 0.02f, -0.1f), tablero(u + 0.02f, -0.1f)), a.en(tablero(u, 0.5f), lerp(lino, C.SOMBRA, 0.45f)).copy(alpha = 0.3f)) }
                drawLine(a.en(tablero(0.5f, 0.1f), lerp(C.ROJO, lino, 0.4f)), tablero(0.08f, 0.06f), tablero(0.9f, 0.04f), 0.9f)
            }
            drawPath(pano, a.en(tablero(0.5f, 0.5f), lerp(lino, C.MADERA_OSC, 0.5f)).copy(alpha = 0.5f), style = Stroke(0.5f))
        }
        // Los dos cuernos, en sus agujeros: el de la tinta negra y el de la roja, con su tapón cuando no se trabaja.
        for ((v, borde) in listOf(0.82f to C.TINTA, 0.64f to C.ROJO)) {
            val o = tablero(0.94f, v)
            val cuerno = Path().apply { moveTo(o.x - 2.6f, o.y - 5f); lineTo(o.x + 2.6f, o.y - 5f); cubicTo(o.x + 2f, o.y + 2f, o.x + 4f, o.y + 6f, o.x + 6.6f, o.y + 8.6f); cubicTo(o.x + 2f, o.y + 7f, o.x - 2f, o.y + 3f, o.x - 2.6f, o.y - 5f); close() }
            cuerpo(a, cuerno, o.x - 2.6f, o.x + 6f, o.y, lerp(C.MADERA_OSC, C.TINTA, 0.4f))
            drawOval(a.en(o, if (E0.trabajando(m)) borde else lerp(C.MADERA, C.CAL, 0.2f)), Offset(o.x - 2.6f, o.y - 6.2f), Size(5.2f, 2.4f)); drawOval(a.en(o, lerp(C.ARENISCA, C.CAL, 0.5f)), Offset(o.x - 2.6f, o.y - 6.2f), Size(5.2f, 2.4f), style = Stroke(0.5f))
        }
        // La pluma de ganso: en el cuerno mientras escribe; limpia y tumbada en el borde cuando no.
        if (E0.trabajando(m)) pluma(a, tablero(0.94f, 0.82f) + Offset(0f, -5.4f), Offset(13f, -21f)) else pluma(a, Offset(262f, 225.4f), Offset(24f, -1.6f))
        // El cortaplumas y la piedra pómez, en el borde.
        val k = Offset(214f, 225.6f)
        drawLine(a.en(k, lerp(C.HIERRO, C.CAL, 0.5f)), k, k + Offset(9f, -0.6f), 1.2f, StrokeCap.Round); drawLine(a.en(k, lerp(C.PERGAMINO, C.MADERA, 0.3f)), k + Offset(9f, -0.6f), k + Offset(15f, -1f), 1.9f, StrokeCap.Round)
        drawOval(a.en(k, lerp(C.PIZARRA, C.CAL, 0.45f)), Offset(236f, 222.6f), Size(8f, 4.2f)); for (q in listOf(Offset(238f, 224f), Offset(240.6f, 225f), Offset(241.6f, 223.6f))) drawCircle(a.en(k, C.PIZARRA).copy(alpha = 0.7f), 0.5f, q)
    }

    /** La gran inicial pintada: un recuadro azul con su orla de oro y un tallo rojo y verde enroscado; antes de pintarla, el hueco que se le deja. */
    private fun DrawScope.inicial(a: Alumbrado, u: Float, v: Float, pintada: Boolean) {
        val marco = cuadro(u, v - 0.15f, u + 0.085f, v + 0.02f); val o = tablero(u + 0.042f, v - 0.065f)
        if (!pintada) { drawPath(marco, a.en(o, lerp(C.PERGAMINO, C.ARENISCA, 0.5f)).copy(alpha = 0.6f), style = Stroke(0.5f)); return }
        drawPath(marco, a.en(o, C.AZUL)); drawPath(marco, a.en(o, C.ORO), style = Stroke(1.1f))
        drawPath(Path().apply { moveTo(o + Offset(-2.4f, 3f)); cubicTo(o + Offset(-3f, -3.6f), o + Offset(3.6f, -3.6f), o + Offset(2.6f, 0.4f)); cubicTo(o + Offset(1.6f, 3f), o + Offset(-1f, 2.4f), o + Offset(-0.4f, 0f)) }, a.en(o, C.ROJO), style = Stroke(1.2f, cap = StrokeCap.Round))
        drawCircle(a.en(o, C.VERDE), 0.9f, o + Offset(2.4f, 2.6f)); drawCircle(a.en(o, C.ORO), 0.8f, o + Offset(-0.4f, 0f))
    }

    /** Una pluma de ganso: el cañón desde [pico], y las barbas hacia [hacia]. */
    private fun DrawScope.pluma(a: Alumbrado, pico: Offset, hacia: Offset) {
        val fin = pico + hacia; val lado = Offset(-hacia.y, hacia.x) / hacia.getDistance() * 2.6f; val medio = pico + hacia * 0.34f
        drawPath(Path().apply { moveTo(medio); cubicTo(medio + lado * 1.2f, fin + lado, fin); cubicTo(fin - lado * 0.6f, medio - lado * 0.9f, medio); close() }, a.en(fin, Color.White))
        drawLine(a.en(pico, lerp(C.PERGAMINO, C.MADERA, 0.35f)), pico, fin, 0.7f, StrokeCap.Round)
        for (k in 1..3) { val q = medio + (fin - medio) * (k / 4f); drawLine(a.en(q, C.PIZARRA).copy(alpha = 0.35f), q, q + lado * 0.9f + hacia * 0.06f, 0.35f) }
    }

    fun DrawScope.primerPlano(m: Momento, a: Alumbrado, f: List<Foco>) {
        val cosas = cosas(m, a).sortedBy { it.pie.y }
        // Las sombras: a cada cosa la tumba el sol que entra o, sin él, la claridad de la ventana; de noche, la lámpara de la puerta.
        capaSombra(rayoFijo(-0.6f, 40f, 0.8f, C.SOMBRA)) { c ->
            clipPath(E.formaDelSuelo()) { for (k in cosas) { val r = EscenaMedieval.rayoEn(m, f, k.pie); sombraArrojada(r, k.forma, k.pie, c.copy(alpha = r.fuerza.coerceIn(0f, 1f)), E.suelo, E.suelo) } }
        }
        for (k in cosas) { val b = k.forma.getBounds(); drawOval(C.SOMBRA.copy(alpha = 0.34f), Offset(b.left - 1f, k.pie.y - 2.4f), Size(b.width + 2f, 5f)) }
        cosas.forEach { it.pintar(this) }
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el tramo del gato: el arca, el facistol y el pupitre. El banco es el sitio del objeto del día. */
    fun estorbos(m: Momento): List<Float> = if (EscenaMedieval.fiesta(m) || EscenaMedieval.campanas(m)) listOf(53f, ADORNO, 182f, 247f) else listOf(53f, 182f, 247f)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 20f, PISO_GATO, hasta = 352f))
        add(Ancla(TipoAncla.MIRADOR, 40f, 229.6f))                               // encima del arca
        add(Ancla(TipoAncla.OBJETO, BANCO.x, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 214f, PISO_GATO))                            // al pie del pupitre
        if (EscenaMedieval.fiesta(m) || EscenaMedieval.campanas(m)) add(Ancla(TipoAncla.ADORNO, ADORNO, 262f))
    }
}
