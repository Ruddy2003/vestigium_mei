package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * La obra del estante de Romano ([MuebleEstanteRomano]): los dos pilares de ladrillo cocido con su mortero grueso, las tablas
 * de roble labradas a azuela que se encajan en ellos, la viga de arriba, el fondo de cada repisa (la cal en sombra) y el
 * letrero del mes, una tablilla encalada con las letras de almagre.
 */
internal object ObraEstanteRomano {
    private val P = PaletaRomano

    /**
     * El fondo de una repisa: la cal en la sombra de la tabla de arriba (fría, en otro valor que los volúmenes), con la
     * sombra del pilar izquierdo; de día, la luz de la ventana que entra de lado en las dos de arriba; de noche, el charco de
     * la linterna solo donde llega.
     */
    fun DrawScope.fondoRepisa(k: Int, r: Rect, a: AireRomano, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        if (r.width <= 0f || r.height <= 0f) return
        val lejos = if (a.lumbre) 0.02f else 1f
        drawRect(a.luz(P.CAL_SOMBRA, lejos), r.topLeft, r.size)
        pinceladas(r, a.luz(P.CAL, lejos), 300 + k, 16, u, alfa = if (a.oscuro) 0.05f else 0.1f)
        pinceladas(r, a.frio(1f), 320 + k, 10, u, alfa = 0.1f)
        // Una mancha de humedad que baja desde la tabla y el roce más claro donde se apoyan las cosas.
        val mx = r.left + r.width * (0.2f + 0.6f * azar(k, 340))
        drawOval(a.luz(P.CAL_SOMBRA, lejos).copy(alpha = 0.5f), Offset(mx, r.top), Size(r.width * 0.12f, r.height * 0.5f))
        drawRect(a.luz(P.CAL, lejos).copy(alpha = 0.18f), Offset(r.left, r.bottom - r.height * 0.32f), Size(r.width, r.height * 0.2f))
        // La faja de almagre que sigue por detrás, a media altura, gastada.
        drawRect(a.luz(P.ALMAGRE, lejos).copy(alpha = 0.3f), Offset(r.left, r.top + r.height * 0.56f), Size(r.width, 1.3f * u))
        if (!a.oscuro && !a.noche && k < 2) {
            // El sol de la ventana que entra en diagonal por la izquierda.
            val f = if (k == 0) 1f else 0.55f
            drawPath(poligonoRomano(r.left, r.top + r.height * 0.15f, r.left + r.width * 0.42f * f, r.top + r.height * 0.15f,
                r.left + r.width * 0.2f * f, r.bottom, r.left, r.bottom), P.CAL_CLARA.copy(alpha = 0.3f))
        }
        if (a.lumbre) {
            val llama = Offset(w * MuebleEstanteRomano.LAMP_X, w * MuebleEstanteRomano.LAMP_Y)
            val kc = cerca(Offset(r.right - r.width * 0.15f, r.center.y))
            if (kc > 0.04f) clipRect(r.left, r.top, r.right, r.bottom) {
                mancha(llama, w * (0.5f + 0.35f * kc), P.LLAMA.copy(alpha = (if (a.oscuro) 0.5f else 0.25f) * kc))
            }
        }
        // La sombra de la tabla de arriba, en dos pasos, y la del pilar izquierdo (la luz viene de la izquierda).
        drawRect(a.frio(0.42f), r.topLeft, Size(r.width, r.height * 0.09f))
        drawRect(a.frio(0.2f), Offset(r.left, r.top + r.height * 0.09f), Size(r.width, r.height * 0.13f))
        drawRect(a.frio(0.3f), r.topLeft, Size(w * 0.022f, r.height))
        drawRect(a.frio(0.14f), Offset(r.left + w * 0.022f, r.top), Size(w * 0.03f, r.height))
        // La sombra que dejan los volúmenes en la cal detrás de sí la pone el fondo entero, abajo: la penumbra del rincón.
        drawRect(a.sombra(0.16f), Offset(r.left, r.bottom - r.height * 0.12f), Size(r.width, r.height * 0.12f))
    }

    /**
     * Una tabla de roble: la cara de arriba clara, el canto labrado a azuela (facetas y golpes), sus vetas y, de noche, cada
     * tramo con la luz que le llega y el filo donde mira a la linterna.
     */
    fun DrawScope.tabla(r: Rect, semilla: Int, a: AireRomano, cerca: (Offset) -> Float) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        val tramos = 5
        val an = r.width / tramos
        for (t in 0 until tramos) {
            val x0 = r.left + an * t
            val k = cerca(Offset(x0 + an / 2, r.center.y))
            drawRect(a.luz(P.ROBLE, k), Offset(x0, r.top), Size(an + 1f, r.height))
            drawRect(a.luz(P.ROBLE_LUZ, k), Offset(x0, r.top), Size(an + 1f, r.height * 0.2f))
            if (a.oscuro && k > 0.1f) drawRect(a.filo(0.5f * k), Offset(x0, r.top + r.height * 0.2f), Size(an + 1f, 0.9f * u))
        }
        // Las facetas de la azuela y sus golpes.
        for (j in 0 until 9) {
            val x = r.left + r.width * (j + azar(semilla, j)) / 9f
            val ancho = r.width * (0.05f + 0.05f * azar(semilla, 20 + j))
            drawOval(a.frio(0.12f), Offset(x, r.top + r.height * 0.3f), Size(ancho, r.height * 0.55f))
            drawLine(a.claro(0.18f), Offset(x, r.top + r.height * 0.32f), Offset(x + ancho * 0.8f, r.top + r.height * 0.3f), 0.6f * u)
        }
        // Las vetas a lo largo.
        for (j in 0 until 3) {
            val y = r.top + r.height * (0.38f + 0.18f * j)
            drawLine(a.luz(P.ROBLE_OSC, 0.3f).copy(alpha = 0.35f), Offset(r.left + r.width * azar(semilla, 40 + j) * 0.3f, y),
                Offset(r.right - r.width * azar(semilla, 50 + j) * 0.3f, y + (azar(semilla, 60 + j) - 0.5f) * 2f * u), 0.6f * u)
        }
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
    }

    /**
     * El letrero del mes: una tablilla de madera encalada colgada de dos clavos en el canto, con su sombra y las letras pintadas
     * con almagre; de noche se apaga poco, para que el mes se lea siempre.
     */
    fun DrawScope.letrero(p: PlacaEstante, medidor: TextMeasurer, a: AireRomano, k: Float) {
        val u = density
        val r = p.r
        if (r.width <= 0f || r.height <= 0f) return
        val kk = k.coerceIn(0f, 1f)
        val cara = if (a.oscuro) mezcla(mezcla(P.CAL_CLARA, a.hondo, 0.25f - 0.2f * kk), P.LLAMA, 0.12f * kk)
        else if (a.noche) mezcla(P.CAL_CLARA, P.ANOCHECER, 0.1f) else P.CAL_CLARA
        val tabla = poligonoRomano(r.left + 1f * u, r.top, r.right, r.top + 0.6f * u, r.right - 0.8f * u, r.bottom, r.left, r.bottom - 0.5f * u)
        drawPath(corridoRomano(tabla, 1.4f * u, 1.6f * u), a.sombra(0.45f))
        drawPath(tabla, cara)
        drawRect(P.CAL_SOMBRA.copy(alpha = 0.18f), Offset(r.left, r.bottom - r.height * 0.25f), Size(r.width, r.height * 0.25f))
        drawLine(P.ROBLE.copy(alpha = 0.2f), Offset(r.left + r.width * 0.05f, r.top + r.height * 0.3f), Offset(r.right - r.width * 0.1f, r.top + r.height * 0.34f), 0.5f * u)
        // El filete de almagre, a pincel, sin cerrar del todo.
        val filete = r.deflate(1.8f * u)
        drawRect(P.ALMAGRE.copy(alpha = 0.55f), filete.topLeft, filete.size, style = Stroke(0.6f * u))
        clavoRomano(Offset(r.left + 3.2f * u, r.center.y), 1.3f * u, a, kk)
        clavoRomano(Offset(r.right - 3.2f * u, r.center.y), 1.3f * u, a, kk)
        textoEn(medidor, p.texto, Rect(r.left + 7f * u, r.top + 0.5f * u, r.right - 7f * u, r.bottom), P.LETRA, P.ALMAGRE_OSC, 9.5f,
            peso = FontWeight.Bold, espaciado = 0.8f, minimo = 5f)
    }

    /**
     * Un pilar de ladrillo cocido de arriba abajo: hiladas de ladrillos largos y planos con el mortero grueso entre ellas (como
     * se levantaba en los campamentos), cada ladrillo con su tono, algún canto roto y un ladrillo con el sello de su horno; la
     * cara de la izquierda con la luz, la de la derecha en sombra; de noche, apagado por tramos según su distancia a la linterna.
     */
    fun DrawScope.pilar(r: Rect, semilla: Int, a: AireRomano, cerca: (Offset) -> Float) {
        val u = density
        if (r.width <= 1f || r.height <= 1f) return
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(P.MORTERO, r.topLeft, r.size)
            val hilada = maxOf(3f * u, 6.6f * u)
            val ladrillo = hilada * 0.7f
            val largo = r.width * 0.64f
            val claros = Path(); val medios = Path(); val oscuros = Path()
            var y = r.top
            var f = 0
            while (y < r.bottom && f < 400) {
                var x = r.left - (if (f % 2 == 0) largo * 0.35f else largo * 0.8f)
                var j = 0
                while (x < r.right && j < 8) {
                    val l = largo * (0.85f + 0.3f * azar(semilla + f, j))
                    val t = azar(semilla * 7 + f, j)
                    val caida = (azar(semilla + 3 * f, j) - 0.5f) * 0.6f * u
                    val p = poligonoRomano(x + 1.2f * u, y + 0.4f * u + caida, x + l - 1f * u, y + 0.3f * u, x + l - 1.2f * u, y + ladrillo, x + 1f * u, y + ladrillo - caida)
                    (if (t < 0.28f) claros else if (t < 0.78f) medios else oscuros).addPath(p)
                    x += l + 1.6f * u
                    j++
                }
                y += hilada
                f++
            }
            drawPath(claros, P.LADRILLO_CLARO)
            drawPath(medios, P.LADRILLO)
            drawPath(oscuros, P.LADRILLO_OSC)
            // El sello de un ladrillo: una cartela rehundida con marcas gastadas que no se leen.
            val sy = r.top + r.height * (0.3f + 0.3f * azar(semilla, 5))
            val sello = Rect(r.left + r.width * 0.22f, sy, r.left + r.width * 0.68f, sy + ladrillo * 0.7f)
            drawRoundRect(P.LADRILLO_OSC.copy(alpha = 0.7f), sello.topLeft, sello.size, CornerRadius(1.2f * u))
            drawRect(P.LADRILLO_CLARO.copy(alpha = 0.6f), Offset(sello.left, sello.bottom - 0.6f * u), Size(sello.width, 0.6f * u))
            for (j in 0 until 4) drawLine(P.LADRILLO_CLARO.copy(alpha = 0.55f), Offset(sello.left + sello.width * (0.18f + 0.2f * j), sello.top + sello.height * 0.3f),
                Offset(sello.left + sello.width * (0.24f + 0.2f * j), sello.top + sello.height * (0.55f + 0.2f * azar(semilla, 10 + j))), 0.6f * u)
            // La luz del día: la cara izquierda clara, la derecha en sombra fría.
            drawRect(a.claro(0.22f), r.topLeft, Size(r.width * 0.16f, r.height))
            drawRect(a.frio(0.22f), Offset(r.right - r.width * 0.22f, r.top), Size(r.width * 0.22f, r.height))
            // La noche, por tramos de cuatro hiladas.
            val tramo = maxOf(4f * u, hilada * 4f)
            var ty = r.top
            var i = 0
            while (ty < r.bottom && i < 400) {
                veloRomano(Rect(r.left, ty, r.right, minOf(ty + tramo + 0.5f, r.bottom)), a, cerca(Offset(r.center.x, ty + tramo / 2)))
                ty += tramo
                i++
            }
            // El filo de la linterna en la cara que la mira (el pilar derecho, de noche).
            if (a.oscuro) {
                val kf = cerca(Offset(r.center.x, r.top + r.height * 0.1f))
                if (kf > 0.1f) drawRect(a.filo(0.45f * kf), Offset(r.left, r.top), Size(1.2f * u, r.height * 0.45f))
            }
        }
        // El pie: la sombra en el suelo.
        drawRect(a.sombra(0.35f), Offset(r.right, r.bottom - 2f * u), Size(r.width * 0.5f, 4f * u))
    }

    /**
     * La viga de roble de arriba, que apoya en los dos pilares y cruza el cuadro de borde a borde: la cara de encima clara
     * (donde se dejan las cosas), el canto con facetas de azuela, sus vetas, dos clavos grandes de hierro donde apoya y la
     * sombra que deja en los pilares; de noche, por tramos.
     */
    fun DrawScope.viga(r: Rect, a: AireRomano, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        if (r.height <= 0f) return
        val encima = r.height * 0.22f
        // La sombra de la viga en la cal de la corona, arriba del canto no: abajo, en los pilares y el fondo.
        drawRect(a.sombra(0.3f), Offset(r.left, r.bottom), Size(r.width, 3.5f * u))
        val tramos = 8
        val an = r.width / tramos
        for (t in 0 until tramos) {
            val x0 = r.left + an * t
            val k = cerca(Offset(x0 + an / 2, r.center.y))
            drawRect(a.luz(P.ROBLE, k), Offset(x0, r.top), Size(an + 1f, r.height))
            drawRect(a.luz(P.ROBLE_LUZ, k), Offset(x0, r.top), Size(an + 1f, encima))
            if (a.oscuro && k > 0.1f) drawRect(a.filo(0.55f * k), Offset(x0, r.top + encima), Size(an + 1f, 1f * u))
        }
        for (j in 0 until 14) {
            val x = r.left + r.width * (j + azar(77, j)) / 14f
            val ancho = r.width * (0.03f + 0.04f * azar(78, j))
            drawOval(a.frio(0.13f), Offset(x, r.top + encima + r.height * 0.1f), Size(ancho, r.height * 0.6f))
            drawLine(a.claro(0.16f), Offset(x, r.top + encima + 1f * u), Offset(x + ancho * 0.7f, r.top + encima + 1f * u), 0.6f * u)
        }
        for (j in 0 until 3) {
            val y = r.top + encima + (r.height - encima) * (0.3f + 0.22f * j)
            drawLine(a.luz(P.ROBLE_OSC, 0.3f).copy(alpha = 0.35f), Offset(r.left, y), Offset(r.right, y + (azar(79, j) - 0.5f) * 3f * u), 0.7f * u)
        }
        drawRect(a.claro(0.3f), Offset(r.left, r.top), Size(r.width, 0.8f * u))
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - 1.5f * u), Size(r.width, 1.5f * u))
        // Los clavos grandes donde la viga apoya en cada pilar.
        val l = w * MuebleEstanteRomano.LADO
        for (x in listOf(l * 0.5f, w - l * 0.5f)) clavoRomano(Offset(x, r.top + encima + (r.height - encima) * 0.5f), 1.9f * u, a, cerca(Offset(x, r.center.y)), 1f)
    }

    /** El hueco bajo la última tabla, entre los pilares: la cal en sombra con su zócalo de almagre. */
    fun DrawScope.bajo(r: Rect, a: AireRomano) {
        if (r.width <= 0f || r.height <= 0f) return
        drawRect(a.frio(0.35f), r.topLeft, Size(r.width, r.height * 0.25f))
        drawRect(a.frio(0.15f), Offset(r.left, r.top + r.height * 0.25f), Size(r.width, r.height * 0.3f))
    }
}
