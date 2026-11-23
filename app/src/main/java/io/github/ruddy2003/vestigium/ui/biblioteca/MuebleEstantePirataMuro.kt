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
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * La pared del estante de Pirata ([MuebleEstantePirata]): el bao de cubierta arriba; el mamparo de cuarterones de la cámara de
 * popa pintado de verde cardenillo, gastado en los cantos; a la izquierda, el costado de la aleta en escorzo con su ventana
 * de paños chicos (el cielo, el filo del mar; de noche, la luna y las estrellas; en los Sueños claros, el ocaso), que de día
 * echa en el mamparo su paño de luz cortado por los parteluces y en el bao el temblor del reflejo del agua; y, abajo, la
 * cubierta de tablones de pino fregado en escorzo, con sus costuras de brea y sus cabillas.
 */
internal object MuroEstantePirata {
    private val P = PaletaPirata

    /** El bao, en anchos: lo que baja desde arriba. */
    const val BAO = 0.052f
    /** La esquina con el costado de la aleta, en anchos. */
    const val ESQUINA = 0.17f

    /** Los cuatro vértices de la ventana en escorzo (arriba cerca, arriba lejos, abajo lejos, abajo cerca), en anchos. */
    private val VENTANA = floatArrayOf(0.022f, 0.08f, 0.14f, 0.108f, 0.14f, 0.35f, 0.022f, 0.385f)

    /** Un punto de la ventana: [fx] de cerca (0) a lejos (1), [fy] de arriba (0) a abajo (1); con escorzo a lo ancho. */
    private fun enVentana(w: Float, fx: Float, fy: Float): Offset {
        val v = VENTANA
        val gx = fx * (0.82f + 0.18f * fx)
        val x = v[0] + (v[2] - v[0]) * gx
        val yA = v[1] + (v[3] - v[1]) * gx
        val yB = v[7] + (v[5] - v[7]) * gx
        return Offset(x * w, (yA + (yB - yA) * fy) * w)
    }

    /** El paño de luz de la ventana en el mamparo, en anchos: (arriba izquierda, arriba derecha, abajo derecha, abajo izquierda). */
    fun panoLuz(a: AirePirata): FloatArray =
        if (a.noche && !a.oscuro) floatArrayOf(0.19f, 0.22f, 0.52f, 0.3f, 0.55f, 0.5f, 0.22f, 0.43f)
        else floatArrayOf(0.19f, 0.15f, 0.44f, 0.205f, 0.47f, 0.455f, 0.22f, 0.4f)

    /** Un punto del paño de luz: [fx] a lo ancho, [fy] a lo alto, de 0 a 1. */
    fun enPano(q: FloatArray, w: Float, fx: Float, fy: Float): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset((tx + (bx - tx) * fy) * w, (ty + (by - ty) * fy) * w)
    }

    /** Los seis cristales del paño de luz, en un solo camino (dejan el hueco de los parteluces). */
    fun cristalesPano(q: FloatArray, w: Float): Path {
        val g = 0.035f
        val c = Path()
        for (i in 0 until 2) for (j in 0 until 3) {
            val p0 = enPano(q, w, i * 0.5f + g, j / 3f + g); val p1 = enPano(q, w, (i + 1) * 0.5f - g, j / 3f + g)
            val p2 = enPano(q, w, (i + 1) * 0.5f - g, (j + 1) / 3f - g); val p3 = enPano(q, w, i * 0.5f + g, (j + 1) / 3f - g)
            c.moveTo(p0.x, p0.y); c.lineTo(p1.x, p1.y); c.lineTo(p2.x, p2.y); c.lineTo(p3.x, p3.y); c.close()
        }
        return c
    }

    /** El color de la luz de la ventana y su fuerza: el sol, la luna o el ocaso. */
    fun colorPano(a: AirePirata): Pair<Color, Float> = when {
        a.oscuro -> P.LUNA_RAS to (if (a.noche) 0.3f else 0.15f)
        a.noche -> P.OCASO to 0.55f
        else -> Color(0xFFFFE6AC) to 0.42f
    }

    // ---------------------------------------------------------------- la pared entera (arriba)

    fun DrawScope.mamparo(a: AirePirata, apoyo: Float, ch: Float, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val w = size.width
        val u = density
        val ab = apoyo + w * 0.01f
        // Largueros y peinazos: el fondo del mamparo, por tramos (de noche, la luz cambia a lo ancho).
        val tramos = if (a.lumbre) 18 else 1
        for (t in 0 until tramos) {
            val x0 = w * ESQUINA + (w - w * ESQUINA) * t / tramos; val x1 = w * ESQUINA + (w - w * ESQUINA) * (t + 1) / tramos + 1f
            drawRect(a.luz(P.VERDE, cerca(Offset((x0 + x1) / 2, w * 0.25f))), Offset(x0, 0f), Size(x1 - x0, ab))
        }
        // Los cuarterones: dos filas de tableros con su chaflán.
        val cols = listOf(0.19f to 0.383f, 0.405f to 0.598f, 0.62f to 0.813f, 0.835f to 1.03f)
        val filas = listOf(BAO + 0.03f to 0.256f, 0.28f to (apoyo / w + 0.06f))
        cols.forEachIndexed { i, (c0, c1) ->
            filas.forEachIndexed { j, (f0, f1) -> tablero(Rect(w * c0, w * f0, w * c1, w * f1), a, cerca, 300 + i * 7 + j) }
        }
        // Las pinceladas de los largueros, a lo alto, y el gastado de los cantos: asoma la madera bajo la pintura.
        val rayas = Path()
        for (j in 0 until 14) {
            val x = w * (0.18f + 0.82f * azar(314, j)); val y0 = w * (BAO + 0.02f + 0.2f * azar(315, j))
            rayas.moveTo(x, y0); rayas.lineTo(x + 1.4f * u, y0 + w * (0.05f + 0.1f * azar(316, j)))
        }
        drawPath(rayas, a.claro(0.08f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        val gastado = Path()
        for (j in 0 until 12) {
            val x = w * (0.19f + 0.8f * azar(311, j)); val y = w * (BAO + 0.03f + 0.4f * azar(312, j))
            gastado.moveTo(x, y); gastado.lineTo(x + w * (0.008f + 0.02f * azar(313, j)), y + 0.6f * u)
        }
        drawPath(gastado, a.luz(P.ROBLE, cerca(Offset(w * 0.5f, w * 0.25f))).copy(alpha = 0.55f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        costado(a, ab, cerca, frio)
        luzDeVentana(a)
        bao(a, cerca)
        reflejos(a)
        // La sombra del bao en el mamparo.
        drawRect(a.sombra(0.34f), Offset(w * ESQUINA, w * BAO), Size(w - w * ESQUINA, 2.6f * u))
        drawRect(a.sombra(0.14f), Offset(w * ESQUINA, w * BAO + 2.6f * u), Size(w - w * ESQUINA, 7f * u))
        // La esquina: la sombra del costado en el mamparo y el canto.
        drawRect(a.sombra(if (a.lumbre) 0.34f else 0.24f), Offset(w * ESQUINA, w * BAO), Size(w * 0.014f, ab - w * BAO))
        drawLine(a.luz(P.VERDE_HONDO, cerca(Offset(w * ESQUINA, w * 0.3f))), Offset(w * ESQUINA, w * BAO), Offset(w * ESQUINA, ab), 1.6f * u)
        if (a.lumbre) halo(a)
    }

    /** Un cuarterón: el tablero con su chaflán (luz arriba a la izquierda), el cuarto bocel que lo cerca y su pintura gastada. */
    private fun DrawScope.tablero(r: Rect, a: AirePirata, cerca: (Offset) -> Float, s: Int) {
        val u = density
        val k = cerca(r.center)
        val ch = minOf(r.width, r.height) * 0.11f
        val cara = Rect(r.left + ch, r.top + ch, r.right - ch, r.bottom - ch)
        // De día la luz viene de la ventana (izquierda); de noche, del candelero.
        val izq = !a.oscuro || r.center.x > size.width * MuebleEstantePirata.LAMP_X
        // El hueco del cuarto bocel: la sombra arriba a la izquierda, la luz abajo a la derecha.
        val m = 2.6f * u
        drawRect(a.luz(P.VERDE_HONDO, k), Offset(r.left - m, r.top - m), Size(r.width + 2 * m, r.height + 2 * m))
        drawRect(a.dia(P.VERDE_CLARO, 0.05f, k), Offset(r.left - m * 0.4f, r.top - m * 0.4f), Size(r.width + m * 1.4f, r.height + m * 1.4f))
        drawRect(a.luz(P.VERDE_OSC, k), r.topLeft, r.size)
        // El chaflán del tablero, cuatro caras.
        drawPath(poligonoPirata(r.left, r.top, r.right, r.top, cara.right, cara.top, cara.left, cara.top), a.dia(P.VERDE_CLARO, 0.12f, k))
        drawPath(poligonoPirata(r.left, r.top, cara.left, cara.top, cara.left, cara.bottom, r.left, r.bottom),
            if (izq) a.dia(P.VERDE_CLARO, 0f, k) else a.luz(P.VERDE_OSC, k))
        drawPath(poligonoPirata(r.right, r.top, r.right, r.bottom, cara.right, cara.bottom, cara.right, cara.top),
            if (izq) a.luz(mezcla(P.VERDE_OSC, P.VERDE_HONDO, 0.3f), k) else a.dia(P.VERDE_CLARO, 0f, k))
        drawPath(poligonoPirata(r.left, r.bottom, cara.left, cara.bottom, cara.right, cara.bottom, r.right, r.bottom), a.luz(mezcla(P.VERDE_OSC, P.VERDE_HONDO, 0.55f), k))
        drawRect(a.luz(mezcla(P.VERDE, P.VERDE_CLARO, 0.18f), k), cara.topLeft, cara.size)
        // Las pinceladas de la pintura, a lo alto, y una mancha de sal abajo.
        clipRect(cara.left, cara.top, cara.right, cara.bottom) {
            val p = Path(); val q = Path()
            for (j in 0 until 7) {
                val x = cara.left + cara.width * azar(s, j)
                val y0 = cara.top + cara.height * 0.4f * azar(s, j + 30)
                p.moveTo(x, y0); p.cubicTo(x + 2f * u, y0 + cara.height * 0.3f, x - 2f * u, y0 + cara.height * 0.6f, x + 1f * u, cara.bottom)
                val x2 = cara.left + cara.width * azar(s, j + 60)
                q.moveTo(x2, cara.top); q.lineTo(x2 + 1.5f * u, cara.top + cara.height * (0.3f + 0.6f * azar(s, j + 90)))
            }
            drawPath(p, a.dia(P.VERDE_CLARO, 0f, k).copy(alpha = 0.22f), style = Stroke((3f + 2f * azar(s, 9)) * u))
            drawPath(q, a.luz(P.VERDE_OSC, k).copy(alpha = 0.3f), style = Stroke(1.6f * u))
            drawOval(a.claro(0.12f), Offset(cara.left + cara.width * 0.15f, cara.bottom - cara.height * 0.18f), Size(cara.width * 0.55f, cara.height * 0.12f))
        }
        // El canto gastado de la arista de arriba: asoma la madera.
        drawLine(a.luz(P.ROBLE, k).copy(alpha = 0.5f), Offset(r.left + r.width * azar(s, 70), r.top + 0.6f * u), Offset(r.left + r.width * (0.2f + 0.5f * azar(s, 71)), r.top + 0.6f * u), 1.2f * u)
    }

    /** El costado de la aleta, en escorzo, con su ventana; y el bao que se va hacia la esquina. */
    private fun DrawScope.costado(a: AirePirata, ab: Float, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val w = size.width; val u = density
        val e = w * ESQUINA
        val k = cerca(Offset(e * 0.5f, w * 0.25f))
        val pared = poligonoPirata(0f, w * 0.03f, e, w * BAO, e, ab, 0f, ab)
        drawPath(pared, a.luz(mezcla(P.VERDE_OSC, P.VERDE_HONDO, 0.25f), k))
        // Un tablero en escorzo bajo la ventana y el larguero junto a la esquina.
        drawPath(poligonoPirata(w * 0.03f, w * 0.43f, w * 0.135f, w * 0.395f, w * 0.135f, ab, w * 0.03f, ab), a.luz(mezcla(P.VERDE_OSC, P.VERDE_HONDO, 0.5f), k))
        drawPath(poligonoPirata(w * 0.03f, w * 0.43f, w * 0.135f, w * 0.395f, w * 0.135f, w * 0.401f, w * 0.03f, w * 0.437f), a.dia(P.VERDE, 0.12f, k))
        drawLine(a.sombra(0.3f), Offset(w * 0.152f, w * BAO), Offset(w * 0.152f, ab), 1.2f * u)
        // La luna roza el costado de noche.
        val fr = frio(Offset(e * 0.5f, w * 0.3f))
        if (fr > 0.02f) drawPath(pared, a.lunar(0.14f * fr))
        ventana(a, cerca)
        // El bao que se va hacia la esquina.
        drawPath(poligonoPirata(0f, 0f, e, 0f, e, w * BAO, 0f, w * 0.03f), a.luz(mezcla(P.ROBLE_OSC, P.BREA, 0.3f), k))
        drawLine(a.claro(0.25f), Offset(0f, w * 0.03f), Offset(e, w * BAO), 1.2f * u)
    }

    /** La ventana de la aleta: el marco de caoba en escorzo, el derrame, los paños de cristal de corona y lo de fuera. */
    private fun DrawScope.ventana(a: AirePirata, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val v = VENTANA
        val hueco = poligonoPirata(v[0] * w, v[1] * w, v[2] * w, v[3] * w, v[4] * w, v[5] * w, v[6] * w, v[7] * w)
        val k = cerca(Offset(w * 0.08f, w * 0.22f))
        // El marco ancho, con su luz en el canto de arriba.
        val m = 0.016f * w
        drawPath(poligonoPirata(v[0] * w - m, v[1] * w - m, v[2] * w + m * 0.6f, v[3] * w - m * 0.7f, v[4] * w + m * 0.6f, v[5] * w + m * 0.7f, v[6] * w - m, v[7] * w + m),
            a.luz(P.CAOBA, k))
        drawPath(poligonoPirata(v[0] * w - m, v[1] * w - m, v[2] * w + m * 0.6f, v[3] * w - m * 0.7f, v[2] * w, v[3] * w, v[0] * w, v[1] * w), a.dia(P.CAOBA_LUZ, 0.05f, k))
        drawPath(poligonoPirata(v[0] * w - m, v[1] * w - m, v[0] * w, v[1] * w, v[6] * w, v[7] * w, v[6] * w - m, v[7] * w + m), a.luz(P.CAOBA_OSC, k))
        // Lo de fuera.
        clipPath(hueco) { fuera(a) }
        // El derrame de la jamba lejana, que de día recibe el sol.
        drawPath(poligonoPirata(v[2] * w - 0.018f * w, v[3] * w + 0.004f * w, v[2] * w, v[3] * w, v[4] * w, v[5] * w, v[4] * w - 0.018f * w, v[5] * w - 0.003f * w),
            if (a.lumbre) a.luz(P.CAOBA_OSC, k) else a.dia(P.CAOBA_LUZ, 0.3f))
        // Los parteluces: dos columnas y tres filas, en escorzo.
        val med = Path().apply {
            val c0 = enVentana(w, 0.47f, 0f); val c1 = enVentana(w, 0.47f, 1f)
            moveTo(c0.x, c0.y); lineTo(c1.x, c1.y)
            for (j in 1..2) { val f = j / 3f; val p0 = enVentana(w, 0f, f); val p1 = enVentana(w, 1f, f); moveTo(p0.x, p0.y); lineTo(p1.x, p1.y) }
        }
        drawPath(med, a.luz(P.CAOBA_OSC, k), style = Stroke(3f * u))
        drawPath(med, a.claro(0.2f), style = Stroke(0.7f * u))
        // El reflejo de los cristales.
        if (!a.oscuro) {
            for (j in 0 until 3) {
                val p0 = enVentana(w, 0.08f + 0.3f * j, 0.06f); val p1 = enVentana(w, 0.03f + 0.3f * j, 0.3f)
                drawLine(Color.White.copy(alpha = 0.25f), p0, p1, 1.6f * u)
            }
        }
        drawPath(hueco, a.sombra(0.45f), style = Stroke(1.4f * u))
        // El alféizar volado.
        val s0 = Offset(v[6] * w - m * 1.3f, v[7] * w + m); val s1 = Offset(v[4] * w + m, v[5] * w + m * 0.6f)
        drawPath(poligonoPirata(s0.x, s0.y, s1.x, s1.y, s1.x, s1.y + 0.01f * w, s0.x, s0.y + 0.015f * w), a.luz(P.CAOBA, k))
        drawLine(a.dia(P.CAOBA_LUZ, 0.25f, k), s0, s1, 1.6f * u)
        drawPath(poligonoPirata(s0.x, s0.y + 0.015f * w, s1.x, s1.y + 0.01f * w, s1.x, s1.y + 0.02f * w, s0.x, s0.y + 0.027f * w), a.sombra(0.35f))
    }

    /** Lo que se ve por la ventana: el cielo, el filo del mar; de noche la luna y las estrellas; en los Sueños claros, el ocaso. */
    private fun DrawScope.fuera(a: AirePirata) {
        val w = size.width; val u = density
        val x0 = 0f; val x1 = w * 0.16f
        val y0 = w * 0.07f; val y1 = w * 0.4f
        val horizonte = w * 0.33f
        val cieloA: Color; val cieloB: Color; val mar: Color
        when {
            a.oscuro && a.noche -> { cieloA = Color(0xFF0A1238); cieloB = Color(0xFF2A3A74); mar = Color(0xFF0B1834) }
            a.oscuro -> { cieloA = Color(0xFF06101C); cieloB = Color(0xFF1A2E48); mar = Color(0xFF07131C) }
            a.noche -> { cieloA = Color(0xFF4E4284); cieloB = P.OCASO; mar = Color(0xFF443460) }
            else -> { cieloA = P.CIELO_ALTO; cieloB = P.CIELO; mar = P.MAR }
        }
        val pasos = 8
        for (j in 0 until pasos) {
            val ya = y0 + (horizonte - y0) * j / pasos; val yb = y0 + (horizonte - y0) * (j + 1) / pasos + 1f
            drawRect(mezcla(cieloA, cieloB, (j + 0.5f) / pasos), Offset(x0, ya), Size(x1 - x0, yb - ya))
        }
        drawRect(mar, Offset(x0, horizonte), Size(x1 - x0, y1 - horizonte))
        when {
            a.oscuro -> {
                for (j in 0 until 16) {
                    val p = Offset(x0 + (x1 - x0) * azar(331, j), y0 + (horizonte - y0) * 0.92f * azar(332, j))
                    drawCircle(P.LUNA.copy(alpha = 0.45f + 0.5f * azar(333, j)), (0.45f + 0.7f * azar(334, j)) * u, p)
                }
                val l = Offset(w * 0.085f, w * (if (a.noche) 0.15f else 0.2f))
                val rl = w * (if (a.noche) 0.027f else 0.02f)
                mancha(l, rl * 4f, P.LUNA.copy(alpha = 0.3f))
                drawCircle(P.LUNA, rl, l)
                if (!a.noche) drawCircle(mezcla(cieloA, cieloB, 0.6f), rl * 0.85f, l + Offset(rl * 0.45f, -rl * 0.2f))
                else { drawCircle(Color(0xFFC8D2E2), rl * 0.22f, l + Offset(-rl * 0.3f, -rl * 0.1f)); drawCircle(Color(0xFFC8D2E2), rl * 0.15f, l + Offset(rl * 0.35f, rl * 0.3f)) }
                // El camino de la luna en el mar.
                var y = horizonte + 2f * u; var j = 0
                while (y < y1 && j < 30) {
                    val m = w * (0.01f + 0.004f * j)
                    drawLine(P.LUNA.copy(alpha = (0.55f - 0.04f * j).coerceAtLeast(0.1f)), Offset(l.x - m, y), Offset(l.x + m * 0.7f, y), 0.9f * u)
                    y += 2.6f * u; j++
                }
            }
            a.noche -> {
                // El ocaso: el sol ya bajo, su brillo en el agua y una nube encendida.
                val s = Offset(w * 0.06f, horizonte - w * 0.012f)
                mancha(s, w * 0.08f, Color(0xFFFFD08A).copy(alpha = 0.8f))
                drawCircle(Color(0xFFFFE2A8), w * 0.013f, s)
                drawOval(Color(0xFFE88A6A).copy(alpha = 0.8f), Offset(w * 0.03f, w * 0.17f), Size(w * 0.1f, w * 0.013f))
                var y = horizonte + 2f * u; var j = 0
                while (y < y1 && j < 30) { drawLine(Color(0xFFFFC07A).copy(alpha = (0.55f - 0.04f * j).coerceAtLeast(0.08f)), Offset(s.x - w * 0.01f, y), Offset(s.x + w * 0.02f, y), 0.9f * u); y += 2.4f * u; j++ }
            }
            else -> {
                drawOval(Color.White.copy(alpha = 0.85f), Offset(w * 0.03f, w * 0.14f), Size(w * 0.09f, w * 0.02f))
                drawOval(Color.White.copy(alpha = 0.6f), Offset(w * 0.06f, w * 0.127f), Size(w * 0.06f, w * 0.015f))
                drawOval(Color.White.copy(alpha = 0.7f), Offset(w * 0.0f, w * 0.25f), Size(w * 0.08f, w * 0.013f))
                // El brillo del sol en el agua y la espuma.
                drawRect(P.ESPUMA.copy(alpha = 0.5f), Offset(x0, horizonte), Size(x1 - x0, 1.2f * u))
                for (j in 0 until 7) {
                    val y = horizonte + (y1 - horizonte) * (0.15f + 0.12f * j)
                    drawLine(P.ESPUMA.copy(alpha = 0.55f), Offset(w * (0.04f + 0.03f * azar(335, j)), y), Offset(w * (0.08f + 0.03f * azar(336, j)), y), 0.9f * u)
                }
            }
        }
    }

    /**
     * La luz de la ventana en el mamparo: de día, el paño del sol cortado por los parteluces, cálido; de noche, el de la luna,
     * frío y tenue; en los Sueños claros, el del ocaso, naranja, más bajo y más largo. Y de día, el reflejo del agua en el bao.
     */
    private fun DrawScope.luzDeVentana(a: AirePirata) {
        val w = size.width; val u = density
        val q = panoLuz(a)
        val (color, fuerza) = colorPano(a)
        val todo = Path().apply { val p0 = enPano(q, w, 0f, 0f); val p1 = enPano(q, w, 1f, 0f); val p2 = enPano(q, w, 1f, 1f); val p3 = enPano(q, w, 0f, 1f); moveTo(p0.x, p0.y); lineTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close() }
        drawPath(todo, color.copy(alpha = fuerza * 0.3f))
        drawPath(cristalesPano(q, w), color.copy(alpha = fuerza))
    }

    /** De día, el temblor del reflejo del agua (que entra por la ventana) en el bao y en lo alto del mamparo. */
    private fun DrawScope.reflejos(a: AirePirata) {
        val w = size.width; val u = density
        if (!a.lumbre) {
            val hilos = Path(); val finos = Path()
            for (j in 0 until 11) {
                val y = w * (0.008f + 0.04f * azar(343, j)) + (if (j > 7) w * 0.03f else 0f)
                val x0 = w * (0.17f + 0.32f * azar(341, j) * azar(344, j)); val largo = w * (0.04f + 0.12f * azar(342, j))
                val p = if (j % 3 == 0) finos else hilos
                p.moveTo(x0, y)
                var x = x0; var n = 0
                while (x < x0 + largo && n < 60) {
                    val paso = (7f + 8f * azar(345 + j, n)) * u
                    val alto = (0.6f + 1.4f * azar(346 + j, n)) * u
                    p.quadraticTo(x + paso / 2, y + (if (n % 2 == 0) -alto else alto), x + paso, y + (azar(347 + j, n) - 0.5f) * u)
                    x += paso; n++
                }
            }
            drawPath(hilos, Color(0xFFFFF4D6).copy(alpha = 0.34f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            drawPath(finos, Color(0xFFFFF4D6).copy(alpha = 0.22f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        }
    }

    /** El paño de sol otra vez, tenue, por encima de lo que hay en la cornisa: lo que cae dentro del paño se enciende. */
    fun DrawScope.solEncima(a: AirePirata) {
        if (a.oscuro) return
        val (color, fuerza) = colorPano(a)
        drawPath(cristalesPano(panoLuz(a), size.width), color.copy(alpha = fuerza * 0.42f))
    }

    /** El bao de cubierta, de roble viejo, con su moldura baja y sus pernos. */
    private fun DrawScope.bao(a: AirePirata, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val e = w * ESQUINA
        val h = w * BAO
        val tramos = if (a.lumbre) 10 else 1
        for (t in 0 until tramos) {
            val x0 = e + (w - e) * t / tramos; val x1 = e + (w - e) * (t + 1) / tramos + 1f
            val k = cerca(Offset((x0 + x1) / 2, h * 0.5f))
            drawRect(a.luz(P.ROBLE_OSC, k), Offset(x0, 0f), Size(x1 - x0, h))
            drawRect(a.luz(P.ROBLE, k), Offset(x0, h - h * 0.32f), Size(x1 - x0, h * 0.14f))
            drawRect(a.luz(P.BREA, k), Offset(x0, h - h * 0.16f), Size(x1 - x0, h * 0.16f))
        }
        // La veta del roble.
        val veta = Path()
        for (j in 0 until 5) {
            val y = h * (0.1f + 0.1f * j)
            veta.moveTo(e, y)
            veta.cubicTo(e + (w - e) * 0.3f, y + h * 0.04f * (azar(351, j) - 0.5f), e + (w - e) * 0.6f, y - h * 0.05f, w, y + h * 0.03f)
        }
        drawPath(veta, a.sombra(0.2f), style = Stroke(0.8f * u))
        for (j in 0 until 5) remachePirata(Offset(e + (w - e) * (0.1f + 0.21f * j), h * 0.36f), 1.8f * u, a, cerca(Offset(e + (w - e) * (0.1f + 0.21f * j), h * 0.4f)))
        drawLine(a.claro(0.3f), Offset(e, h - h * 0.32f), Offset(w, h - h * 0.32f), 0.8f * u)
    }

    /** El halo del candelero en el verde, que tiñe la pared de su color (antes del bodegón, que se pinta encima). */
    private fun DrawScope.halo(a: AirePirata) {
        val w = size.width
        val l = Offset(w * MuebleEstantePirata.LAMP_X, w * MuebleEstantePirata.LAMP_Y)
        val f = if (a.oscuro) (if (a.noche) 0.75f else 1f) else 0.5f
        mancha(l, w * 0.36f, Color(0xFFF08A30).copy(alpha = 0.18f * f))
        mancha(l, w * 0.15f, P.LLAMA.copy(alpha = 0.32f * f))
    }

    /** Lo último de la corona de noche: el resplandor de la vela sobre lo que tiene cerca. */
    fun DrawScope.luzDeNoche(a: AirePirata, ch: Float) {
        if (!a.lumbre) return
        val w = size.width
        val l = Offset(w * MuebleEstantePirata.LAMP_X, w * MuebleEstantePirata.LAMP_Y)
        val f = if (a.oscuro) (if (a.noche) 0.7f else 1f) else 0.55f
        clipRect(0f, 0f, w, ch + 2f) {
            mancha(l, w * 0.3f, P.LLAMA.copy(alpha = 0.12f * f))
            mancha(l, w * 0.055f, P.FUEGO.copy(alpha = 0.45f * f))
        }
    }

    // ---------------------------------------------------------------- la cubierta (abajo)

    /**
     * La cubierta de tablones de pino fregado, en escorzo hacia quien mira (las costuras se abren un poco hacia abajo), con sus
     * costuras de brea, los topes alternados con sus cabillas, la sombra del cuerpo de cajones y, de noche, el charco del fanal.
     * [h] es el borde de abajo del estante.
     */
    fun DrawScope.cubierta(a: AirePirata, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        if (h <= suelo) return
        val fondo = h - suelo
        val vp = Offset(w * 0.5f, suelo - w * 1.4f)
        fun abajo(x: Float) = vp.x + (x - vp.x) * (h - vp.y) / (suelo - vp.y)
        val n = 14
        val ancho = w * 1.1f / n
        for (i in 0 until n) {
            val xa = -w * 0.05f + ancho * i; val xb = xa + ancho
            val k = cerca(Offset((xa + xb) / 2, suelo + fondo * 0.5f))
            val tono = mezcla(P.PINO_OSC, P.PINO, 0.5f + 0.45f * azar(361, i))
            drawPath(poligonoPirata(xa, suelo, xb + 0.5f, suelo, abajo(xb) + 0.5f, h, abajo(xa), h), a.luz(tono, k))
            // La veta del tablón, a lo largo.
            val f0 = 0.2f + 0.6f * azar(363, i)
            drawLine(a.luz(P.PINO_OSC, k).copy(alpha = 0.5f), Offset(xa + (xb - xa) * f0, suelo), Offset(abajo(xa) + (abajo(xb) - abajo(xa)) * f0, h), 0.8f * u)
            // El tope del tablón, con sus dos cabillas.
            val f = 0.25f + 0.5f * azar(362, i)
            val y = suelo + fondo * f
            val xL = xa + (abajo(xa) - xa) * f; val xR = xb + (abajo(xb) - xb) * f
            drawLine(a.luz(P.BREA, k).copy(alpha = 0.7f), Offset(xL, y), Offset(xR, y), 1.2f * u)
            drawCircle(a.luz(P.PINO_OSC, k), 1.6f * u, Offset(xL + (xR - xL) * 0.3f, y + 4f * u))
            drawCircle(a.luz(P.PINO_OSC, k), 1.6f * u, Offset(xL + (xR - xL) * 0.7f, y + 4f * u))
        }
        // Las costuras de brea, una sola pasada, y su filo de luz.
        val costuras = Path()
        for (i in 0..n) { val x = -w * 0.05f + ancho * i; costuras.moveTo(x, suelo); costuras.lineTo(abajo(x), h) }
        drawPath(costuras, a.luz(P.BREA, 0.6f).copy(alpha = 0.9f), style = Stroke(1.8f * u))
        // Lo gastado: el paso fregado más claro en medio, lo hondo hacia el frente.
        drawOval(a.claro(0.14f), Offset(w * 0.28f, suelo + fondo * 0.3f), Size(w * 0.44f, fondo * 0.45f))
        drawRect(a.sombra(0.12f), Offset(0f, h - fondo * 0.25f), Size(w, fondo * 0.25f))
        // La sombra del cuerpo de cajones sobre la cubierta.
        drawRect(a.sombra(0.45f), Offset(0f, suelo), Size(w, 2.6f * u))
        drawRect(a.sombra(0.2f), Offset(0f, suelo + 2.6f * u), Size(w, 9f * u))
    }
}
