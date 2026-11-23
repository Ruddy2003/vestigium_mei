package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.AZUL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PINO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.TIZNE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.VETA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El cuerpo de la estantería de pino tea (ver [MuebleEstanteVapor]): la tapa vista desde un poco arriba, la cornisa volada con
 * su filete, su gola y su bocel, el friso con acanaladuras y rosetas incisas y sus dados sobre los montantes; el fondo de cada
 * hueco (la pared azul en sombra, con la sombra de la tabla de arriba y del montante), las caras de dentro de los montantes y
 * la tabla de debajo vista desde arriba; el canto boleado de cada tabla con sus golpes; los montantes con sus chaflanes de remate
 * en punta; el portaetiquetas de latón de cada mes; y el zócalo con su moldura y los golpes de las botas.
 */
internal object MuebleCuerpoVapor {
    /** La hondura que se ve de los montantes y de la tabla de arriba dentro de cada hueco, y de la tabla de abajo, en anchos. */
    private const val HONDO_LADO = 0.026f
    private const val HONDO_ARRIBA = 0.016f
    private const val HONDO_ABAJO = 0.013f

    // ---------------------------------------------------------------- arriba (va en la imagen guardada de la corona)

    /** La tapa, la cornisa y el friso, de [MarcoVapor.fondoTapa] a [MarcoVapor.ch]. */
    fun DrawScope.cornisaVapor(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val t0 = g.fondoTapa; val t1 = g.apoyo
        // La tapa vista desde arriba: el pino más claro, con su veta a lo largo, el cerco de una taza, rozaduras y polvo.
        franja(mezcla(PINO_LUZ, PINO, 0.35f), 0f, w, t0, t1 - t0, a, g, t1)
        vetas(Rect(0f, t0, w, t1), a.grano(VETA, 0.35f), 3201, 0.9f, grosor = 0.5f, ondulacion = 0.35f)
        drawRect(a.sombra(0.4f), Offset(0f, t0), Size(w, 1.6f * u))
        drawRect(a.sombra(0.16f), Offset(0f, t0 + 1.6f * u), Size(w, 2.5f * u))
        pinceladas(Rect(0f, t0, w, t1), a.grano(TIZNE, 1f), 3202, 10, u, 0.12f)
        drawOval(a.sombra(0.25f), Offset(w * 0.205f, t0 + (t1 - t0) * 0.3f), Size(w * 0.05f, (t1 - t0) * 0.45f), style = Stroke(1f * u))
        // La cornisa: el filete de arriba (en la luz), la gola (luz arriba, sombra abajo) y el bocel, por tramos.
        val f1 = t1 + w * 0.009f; val f2 = t1 + w * 0.03f; val f3 = t1 + w * 0.038f; val f4 = t1 + w * 0.045f
        franja(PINO_LUZ, 0f, w, t1, f1 - t1 + 0.5f, a, g, f2)
        franja(mezcla(PINO_LUZ, PINO, 0.4f), 0f, w, f1, (f2 - f1) * 0.45f + 0.5f, a, g, f2)
        franja(PINO, 0f, w, f1 + (f2 - f1) * 0.45f, (f2 - f1) * 0.3f + 0.5f, a, g, f2)
        franja(PINO_OSC, 0f, w, f1 + (f2 - f1) * 0.75f, (f2 - f1) * 0.25f + 0.5f, a, g, f2)
        franja(mezcla(PINO_LUZ, PINO, 0.2f), 0f, w, f2, (f3 - f2) * 0.55f + 0.5f, a, g, f2)
        franja(PINO_OSC, 0f, w, f2 + (f3 - f2) * 0.55f, (f3 - f2) * 0.45f + 0.5f, a, g, f2)
        franja(mezcla(PINO_OSC, VETA, 0.4f), 0f, w, f3, f4 - f3, a, g, f2)
        drawRect(a.brillo(0.75f, g.cerca(Offset(w * 0.5f, t1))), Offset(0f, t1), Size(w, 1.1f * u))
        drawRect(a.brillo(0.45f, g.cerca(Offset(w * 0.5f, f2))), Offset(0f, f2), Size(w, 0.9f * u))
        // La esquina de la cornisa golpeada, a la izquierda: la madera viva.
        drawPath(poligonoVapor(w * 0.04f, t1, w * 0.075f, t1, w * 0.06f, f1 + w * 0.006f), a.luz(mezcla(PINO_LUZ, PAPEL, 0.3f), g.cerca(Offset(w * 0.05f, t1))))
        // El friso, del pie de la cornisa al bocel de abajo, de montante a montante.
        val fr0 = f4; val fr1 = g.ch - w * 0.012f
        franja(PINO, 0f, w, fr0, fr1 - fr0, a, g)
        vetas(Rect(0f, fr0, w, fr1), a.grano(VETA, 0.4f), 3203, 0.7f, grosor = 0.6f, ondulacion = 0.6f)
        if (!a.lumbre) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.sombra(0.18f)), 0f, w), Offset(0f, fr0), Size(w, fr1 - fr0))
        // El tablero rehundido del friso, entre los dados: más oscuro, con el canto de arriba en sombra y el de abajo en la luz.
        val panel = Rect(g.x0 + w * 0.01f, fr0 + w * 0.011f, g.x1 - w * 0.01f, fr1 - w * 0.008f)
        drawRect(a.sombra(if (a.oscuro) 0.2f else 0.16f), panel.topLeft, panel.size)
        drawRect(a.sombra(0.4f), panel.topLeft, Size(panel.width, 1.4f * u))
        drawRect(a.sombra(0.3f), panel.topLeft, Size(1.2f * u, panel.height))
        drawRect(a.brillo(0.45f, g.cerca(Offset(w * 0.5f, panel.bottom))), Offset(panel.left, panel.bottom - 1f * u), Size(panel.width, 1f * u))
        // La sombra de la cornisa en el friso.
        drawRect(a.sombra(0.45f), Offset(0f, fr0), Size(w, 1.6f * u))
        drawRect(a.sombra(0.2f), Offset(0f, fr0 + 1.6f * u), Size(w, w * 0.008f))
        // Los dados sobre los montantes, con su roseta, y entre ellos las acanaladuras y las rosetas incisas.
        val yc = (fr0 + fr1) / 2 + w * 0.003f
        listOf(g.xa to g.x0, g.x1 to g.xb).forEach { (l, r) ->
            val k = g.cerca(Offset((l + r) / 2, yc))
            drawRect(a.luz(mezcla(PINO_LUZ, PINO, 0.3f), k), Offset(l, fr0), Size(r - l, fr1 - fr0))
            drawRect(a.sombra(0.4f), Offset(r - 1.2f * u, fr0), Size(1.2f * u, fr1 - fr0))
            drawRect(a.brillo(0.4f, k), Offset(l, fr0), Size(1f * u, fr1 - fr0))
            roseta(Offset((l + r) / 2, yc), (r - l) * 0.3f, a, k)
        }
        val ancho = g.x1 - g.x0
        val grupos = 4
        for (i in 0 until grupos) {
            val cx = g.x0 + ancho * (i + 0.5f) / grupos
            roseta(Offset(cx, yc), w * 0.016f, a, g.cerca(Offset(cx, yc)))
            // A cada lado de la roseta, cinco acanaladuras.
            listOf(-1f, 1f).forEach { s ->
                val acan = Path()
                for (j in 0 until 5) {
                    val x = cx + s * (w * 0.032f + w * 0.0085f * j)
                    acan.moveTo(x, yc - (fr1 - fr0) * 0.28f); acan.lineTo(x, yc + (fr1 - fr0) * 0.28f)
                }
                val k = g.cerca(Offset(cx + s * w * 0.05f, yc))
                drawPath(acan, a.sombra(0.5f), style = Stroke(1.3f * u))
                drawPath(corridoVapor(acan, 0.9f * u, 0f), a.brillo(0.3f, k), style = Stroke(0.6f * u))
            }
        }
        // El bocel de abajo y la línea donde empieza el cuerpo.
        franja(PINO_LUZ, 0f, w, fr1, (g.ch - fr1) * 0.5f + 0.5f, a, g, fr1)
        franja(PINO_OSC, 0f, w, fr1 + (g.ch - fr1) * 0.5f, (g.ch - fr1) * 0.5f + 1f, a, g, fr1)
        drawRect(a.brillo(0.6f, g.cerca(Offset(w * 0.5f, fr1))), Offset(0f, fr1), Size(w, 0.9f * u))
        drawRect(a.sombra(0.35f), Offset(0f, fr1 - 1f * u), Size(w, 1f * u))
    }

    /** Una franja horizontal de [c], de [x0] a [x1] y de [y] a [y] + [alto], con la luz que corre a lo largo (medida en [yLuz]). */
    private fun DrawScope.franja(c: Color, x0: Float, x1: Float, y: Float, alto: Float, a: AireVapor, g: MarcoVapor, yLuz: Float = y + alto / 2) {
        if (alto <= 0f || x1 <= x0) return
        val n = if (a.lumbre) 8 else 3
        val cols = List(n) { i -> val p = Offset(x0 + (x1 - x0) * i / (n - 1), yLuz); a.luz(c, g.cerca(p), g.frio(p), g.brasa(p)) }
        drawRect(Brush.horizontalGradient(cols, x0, x1), Offset(x0, y), Size(x1 - x0, alto))
    }

    /** Una roseta incisa de ocho pétalos con su botón (no es una cruz ni una estrella de cinco). */
    private fun DrawScope.roseta(c: Offset, r: Float, a: AireVapor, k: Float) {
        val u = density
        drawCircle(a.sombra(0.45f), r, c, style = Stroke(1.2f * u))
        drawCircle(a.brillo(0.3f, k), r, c + Offset(0.8f * u, 0.8f * u), style = Stroke(0.6f * u))
        val petalos = Path()
        for (i in 0 until 8) {
            val ang = Math.toRadians(22.5 + 45.0 * i)
            val dx = kotlin.math.cos(ang).toFloat(); val dy = kotlin.math.sin(ang).toFloat()
            petalos.moveTo(c.x + dx * r * 0.3f, c.y + dy * r * 0.3f); petalos.lineTo(c.x + dx * r * 0.78f, c.y + dy * r * 0.78f)
        }
        drawPath(petalos, a.sombra(0.45f), style = Stroke(1.1f * u))
        drawCircle(a.sombra(0.4f), r * 0.27f, c + Offset(0.6f * u, 0.8f * u))
        drawCircle(a.luz(LATON, k), r * 0.25f, c)
        drawCircle(a.luz(LATON_OSC, k), r * 0.25f, c, style = Stroke(0.5f * u))
        drawCircle(a.brillo(0.8f, maxOf(k, 0.3f)), r * 0.09f, c - Offset(r * 0.08f, r * 0.08f))
    }

    // ---------------------------------------------------------------- el cuerpo (se pinta en cada dibujo)

    /**
     * Un hueco con su hondura: al fondo la pared azul en sombra (la estantería no tiene trasera), oscura arriba por la tabla de
     * encima y a la izquierda por el montante; la tabla de arriba vista por debajo; las caras de dentro de los montantes (la de la
     * derecha mira a la ventana); y la tabla de abajo vista desde arriba, donde se paran los volúmenes.
     */
    fun DrawScope.huecoVapor(k: Int, r: Rect, a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val x0 = g.x0; val x1 = g.x1
        val dL = w * HONDO_LADO; val dT = w * HONDO_ARRIBA; val dB = w * HONDO_ABAJO
        val yc = r.center.y
        val kl = g.cerca(Offset(x0, yc)); val kc = g.cerca(Offset((x0 + x1) / 2, yc)); val kr = g.cerca(Offset(x1, yc))
        val fl = g.frio(Offset(x0, yc)); val fr = g.frio(Offset(x1, yc))
        val bl = g.brasa(Offset(x0, yc)); val brr = g.brasa(Offset(x1, yc))
        // El fondo: la pared, que de noche se enciende cerca del gas y de la estufa.
        val pared = mezcla(AZUL, AZUL_OSC, 0.45f)
        drawRect(Brush.horizontalGradient(listOf(a.luz(pared, kl * 0.8f, fl, bl), a.luz(pared, kc * 0.8f, (fl + fr) / 2, (bl + brr) / 2), a.luz(pared, kr * 0.8f, fr, brr)), x0, x1),
            Offset(x0, r.top), Size(x1 - x0, r.height))
        pinceladas(Rect(x0, r.top, x1, r.bottom), a.hondo, 3300 + k, 6, u, 0.16f)
        if (!a.oscuro) pinceladas(Rect(x0, r.top, x1, r.bottom), PaletaVapor.AZUL_CLARO, 3320 + k, 5, u, 0.12f)
        if (!a.lumbre) mancha(Offset(x0 + dL, r.top + dT), w * 0.42f, luzMueble(0.1f * kl))
        val grieta = Path().apply {
            val gx = x0 + (x1 - x0) * (0.3f + 0.4f * azar(3310, k)); val gy = r.top + r.height * 0.35f
            moveTo(gx, gy); lineTo(gx + w * 0.012f, gy + r.height * 0.12f); lineTo(gx + w * 0.006f, gy + r.height * 0.22f); lineTo(gx + w * 0.02f, gy + r.height * 0.34f)
        }
        drawPath(grieta, a.sombra(0.4f), style = Stroke(0.8f * u))
        // La sombra de la tabla de arriba en la pared (más honda junto a ella) y la del montante izquierdo.
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.62f), a.sombra(0.2f), Color.Transparent), r.top + dT, r.top + r.height * 0.55f),
            Offset(x0, r.top + dT), Size(x1 - x0, r.height * 0.55f - dT))
        val ladoSombra = if (a.oscuro) x1 - dL - w * 0.05f else x0 + dL
        drawRect(Brush.horizontalGradient(if (a.oscuro) listOf(Color.Transparent, a.sombra(0.4f)) else listOf(a.sombra(0.4f), Color.Transparent), ladoSombra, ladoSombra + w * 0.05f),
            Offset(ladoSombra, r.top), Size(w * 0.05f, r.height))
        // La tabla de arriba vista por debajo.
        drawRect(a.luz(mezcla(PINO_OSC, VETA, 0.35f), kc * 0.7f, 0f, (bl + brr) / 2), Offset(x0, r.top), Size(x1 - x0, dT))
        drawRect(a.sombra(0.4f), Offset(x0, r.top + dT - 1.2f * u), Size(x1 - x0, 1.2f * u))
        // La tabla de abajo vista desde arriba: el pino claro, con su canto de luz.
        drawRect(Brush.horizontalGradient(listOf(a.luz(PINO, kl, fl, bl), a.luz(PINO, kr, fr, brr)), x0, x1), Offset(x0, r.bottom - dB), Size(x1 - x0, dB))
        drawRect(a.sombra(0.35f), Offset(x0, r.bottom - dB), Size(x1 - x0, 1.2f * u))
        // Las caras de dentro de los montantes: trapecios que se cierran hacia la pared.
        val izq = poligonoVapor(x0, r.top, x0 + dL, r.top + dT, x0 + dL, r.bottom - dB, x0, r.bottom)
        val der = poligonoVapor(x1, r.top, x1 - dL, r.top + dT, x1 - dL, r.bottom - dB, x1, r.bottom)
        drawPath(izq, a.luz(if (a.oscuro) PINO else PINO_OSC, kl, fl, bl))
        drawPath(der, a.luz(if (a.oscuro) PINO_OSC else mezcla(PINO, PINO_LUZ, 0.35f), kr, fr, brr))
        drawPath(izq, a.sombra(if (a.oscuro) 0.15f else 0.3f))
        drawRect(a.sombra(0.3f), Offset(x0 + dL - 1f * u, r.top + dT), Size(1f * u, r.height - dT - dB))
        drawRect(a.brillo(0.3f, kr, brr), Offset(x1 - dL, r.top + dT), Size(1f * u, r.height - dT - dB))
        // Los listones donde se apoya la tabla de abajo, en cada cara de dentro.
        drawRect(a.luz(PINO_OSC, kl, fl, bl), Offset(x0, r.bottom - dB - w * 0.008f), Size(dL, w * 0.008f))
        drawRect(a.luz(PINO, kr, fr, brr), Offset(x1 - dL, r.bottom - dB - w * 0.008f), Size(dL, w * 0.008f))
    }

    /** El canto boleado de una tabla: la luz arriba, la sombra abajo, su veta, los golpes y la sombra que echa en el hueco de debajo. */
    fun DrawScope.tablaVapor(k: Int, r: Rect, a: AireVapor, g: MarcoVapor) {
        val u = density
        val l = r.left; val rr = r.right; val hh = r.height
        val ks = listOf(r.left, (r.left + r.right) / 2, r.right).map { x -> Offset(x, r.center.y) }
        fun banda(c: Color, y: Float, alto: Float) =
            drawRect(Brush.horizontalGradient(ks.map { p -> a.luz(c, g.cerca(p), g.frio(p), g.brasa(p)) }, l, rr), Offset(l, y), Size(rr - l, alto))
        // La fila de la junta con el hueco de arriba, que puede caer a medio píxel: debajo, el pino de la tabla.
        filaJuntaVapor(r.top, l, rr, a.luz(PINO_LUZ, g.cerca(ks[1])))
        banda(PINO_LUZ, r.top, hh * 0.32f)
        banda(PINO, r.top + hh * 0.32f, hh * 0.38f)
        banda(PINO_OSC, r.top + hh * 0.7f, hh * 0.3f)
        val kc = g.cerca(ks[1])
        drawRect(a.brillo(0.75f, kc, g.brasa(ks[1])), Offset(l, r.top), Size(rr - l, 1.1f * u))
        drawRect(a.brillo(0.3f, kc, g.brasa(ks[1])), Offset(l, r.top + hh * 0.18f), Size(rr - l, 0.8f * u))
        vetas(Rect(l, r.top + hh * 0.3f, rr, r.bottom - hh * 0.12f), a.grano(VETA, 0.32f), 3400 + k, 0.5f, grosor = 0.5f, ondulacion = 0.3f)
        // Los golpes del canto: la madera viva que asoma.
        for (j in 0 until 3) {
            val x = l + (rr - l) * (0.12f + 0.76f * azar(3410 + k, j))
            drawRect(a.luz(mezcla(PINO_LUZ, PAPEL, 0.3f), g.cerca(Offset(x, r.top))), Offset(x, r.top + hh * 0.05f), Size((rr - l) * 0.012f, 1.3f * u))
        }
        drawRect(a.sombra(0.55f), Offset(l, r.bottom - 1.3f * u), Size(rr - l, 1.3f * u))
        // La sombra de la tabla en lo de debajo (la pared del hueco siguiente o el zócalo).
        drawRect(a.sombra(0.3f), Offset(l, r.bottom), Size(rr - l, 2.2f * u))
    }

    /**
     * Los dos montantes, como pilastras: la cara de pino con su veta resinosa a lo largo; en cada tramo entre tablas, dos
     * acanaladuras con sus remates redondos y los chaflanes de los cantos con su remate en punta (el de afuera del izquierdo y el
     * de dentro del derecho miran a la ventana); a la altura de cada tabla, un dado que sobresale con su roseta incisa; y a los
     * lados, la pared que asoma con la sombra del mueble.
     */
    fun DrawScope.montantesVapor(a: AireVapor, g: MarcoVapor, tablas: List<Rect>) {
        val u = density; val w = g.w
        val top = g.ch; val bot = g.fondo
        if (bot - top < 1f) return
        val ym = (top + bot) / 2
        // La pared que asoma a los lados y la sombra del mueble en ella (de día, a la derecha; de noche, a la izquierda).
        listOf(0f to g.xa, g.xb to w).forEachIndexed { i, (l, r) ->
            val p = Offset((l + r) / 2, ym)
            drawRect(Brush.verticalGradient(listOf(a.luz(AZUL, g.cerca(Offset(p.x, top)), g.frio(p)), a.luz(AZUL, g.cerca(Offset(p.x, bot)), g.frio(p), g.brasa(Offset(p.x, bot)))), top, bot),
                Offset(l, top), Size(r - l, bot - top))
            if ((i == 1) != a.oscuro) drawRect(a.sombra(0.4f), Offset(l, top), Size(r - l, bot - top))
        }
        // Los tramos de cada montante: de la corona a la primera tabla, entre tablas.
        val tramosY = mutableListOf<Pair<Float, Float>>()
        var desde = top
        tablas.forEach { t -> if (t.top - desde > 1f) tramosY.add(desde to t.top); desde = t.bottom }
        listOf(true, false).forEach { izq ->
            val l = if (izq) g.xa else g.x1
            val r = if (izq) g.x0 else g.xb
            val xc = (l + r) / 2
            val n = if (a.lumbre) 8 else 4
            val cols = List(n) { i -> val p = Offset(xc, top + (bot - top) * i / (n - 1)); a.luz(PINO, g.cerca(p), g.frio(p), g.brasa(p)) }
            drawRect(Brush.verticalGradient(cols, top, bot), Offset(l, top), Size(r - l, bot - top))
            // El brillo del barniz, una franja a lo largo del lado de la luz.
            val lb = if (a.oscuro) r - (r - l) * 0.34f else l + (r - l) * 0.12f
            drawRect(a.brillo(if (a.oscuro) 0.14f else 0.18f, g.cerca(Offset(xc, top + (bot - top) * 0.2f))), Offset(lb, top), Size((r - l) * 0.22f, bot - top))
            vetas(Rect(l, top, r, bot), a.grano(VETA, 0.45f), if (izq) 3501 else 3502, 0.9f, vertical = true, grosor = 0.9f, ondulacion = 0.7f)
            // Las acanaladuras y los chaflanes de cada tramo.
            val ch = w * 0.009f
            val claro = Path(); val oscuroP = Path(); val surcos = Path()
            tramosY.forEach { (y0t, y1t) ->
                val y0 = y0t + w * 0.022f; val y1 = y1t - w * 0.022f
                if (y1 - y0 < ch * 5) return@forEach
                listOf(l to true, r to false).forEach { (x, deIzq) ->
                    val camino = if (deIzq != a.oscuro) claro else oscuroP
                    val dentro = if (deIzq) x + ch else x - ch
                    camino.moveTo(x, y0); camino.lineTo(dentro, y0 + ch * 2.4f); camino.lineTo(dentro, y1 - ch * 2.4f); camino.lineTo(x, y1); camino.close()
                }
                listOf(xc - (r - l) * 0.16f, xc + (r - l) * 0.16f).forEach { x -> surcos.moveTo(x, y0 + w * 0.012f); surcos.lineTo(x, y1 - w * 0.012f) }
            }
            val km = g.cerca(Offset(xc, top + (bot - top) * 0.2f))
            drawPath(claro, a.luz(mezcla(PINO_LUZ, PAPEL, 0.15f), km).copy(alpha = if (a.oscuro) 0.5f else 0.75f))
            drawPath(oscuroP, a.sombra(0.34f))
            drawPath(surcos, a.sombra(0.5f), style = Stroke(w * 0.0065f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            drawPath(corridoVapor(surcos, (if (a.oscuro) -1f else 1f) * w * 0.0032f), a.brillo(0.35f, km), style = Stroke(0.8f * u, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            // El dado a la altura de cada tabla, que sobresale, con su roseta.
            tablas.forEach { t ->
                val p = Offset(xc, t.center.y)
                val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
                val d = Rect(l - w * 0.004f, t.top - w * 0.006f, r + w * 0.004f, t.bottom + w * 0.006f)
                drawRect(a.sombra(0.35f), Offset(d.left + w * 0.003f, d.top + w * 0.004f), d.size)
                drawRect(a.luz(mezcla(PINO_LUZ, PINO, 0.35f), k, f, br), d.topLeft, d.size)
                vetas(d, a.grano(VETA, 0.3f), 3510 + (t.top / 7f).toInt(), 0.6f, grosor = 0.6f, ondulacion = 0.3f)
                drawRect(a.brillo(0.6f, maxOf(k, 0.25f), br), d.topLeft, Size(d.width, 1f * u))
                drawRect(a.sombra(0.45f), Offset(d.left, d.bottom - 1.3f * u), Size(d.width, 1.3f * u))
                drawRect(a.sombra(0.3f), Offset(if (a.oscuro) d.left else d.right - 1.2f * u, d.top), Size(1.2f * u, d.height))
                val rc = minOf(d.width, d.height) * 0.26f
                drawCircle(a.sombra(0.45f), rc, p, style = Stroke(1f * u))
                drawCircle(a.brillo(0.3f, maxOf(k, 0.2f), br), rc, p + Offset(0.7f * u, 0.7f * u), style = Stroke(0.6f * u))
                drawCircle(a.luz(PINO_OSC, k, f, br), rc * 0.32f, p)
            }
            // El tizne de las manos donde se agarra, y los cantos: la luz del lado de la ventana, la sombra del otro.
            drawOval(a.grano(TIZNE, 0.14f), Offset(xc - (r - l) * 0.35f, top + (bot - top) * 0.3f), Size((r - l) * 0.7f, w * 0.12f))
            drawRect(a.brillo(0.5f, g.cerca(Offset(l, ym))), Offset(l, top), Size(1f * u, bot - top))
            drawRect(a.sombra(0.45f), Offset(r - 1.2f * u, top), Size(1.2f * u, bot - top))
        }
    }

    /** El portaetiquetas de latón atornillado al canto de la tabla, con la tarjeta del mes dentro. */
    fun DrawScope.portaetiquetasVapor(p: PlacaEstante, medidor: TextMeasurer, a: AireVapor, k: Float, br: Float) {
        val u = density
        val r = p.r
        val marco = Rect(r.left - 3f * u, r.top - 0.6f * u, r.right + 3f * u, r.bottom + 0.6f * u)
        drawRect(a.sombra(0.4f), Offset(marco.left + 1f * u, marco.top + 1.4f * u), marco.size)
        drawRect(a.luz(LATON, k, 0f, br), marco.topLeft, marco.size)
        drawRect(a.brillo(0.75f, maxOf(k, 0.3f), br), marco.topLeft, Size(marco.width, 0.9f * u))
        drawRect(a.luz(LATON_OSC, k, 0f, br), Offset(marco.left, marco.bottom - 1.1f * u), Size(marco.width, 1.1f * u))
        val tarjeta = Rect(r.left + 1.5f * u, r.top + 1.8f * u, r.right - 1.5f * u, r.bottom - 1.6f * u)
        drawRect(a.etiqueta(k, PAPEL), tarjeta.topLeft, tarjeta.size)
        drawRect(a.sombra(0.3f), tarjeta.topLeft, Size(tarjeta.width, 1f * u))
        // Los labios del marco que sujetan la tarjeta, y los dos tornillos.
        drawRect(a.luz(LATON_LUZ, k, 0f, br), Offset(tarjeta.left, tarjeta.top - 0.6f * u), Size(tarjeta.width, 0.6f * u))
        listOf(marco.left + 1.6f * u, marco.right - 1.6f * u).forEach { x ->
            val c = Offset(x, marco.center.y)
            drawCircle(a.luz(LATON_OSC, k, 0f, br), 1.3f * u, c)
            drawLine(a.sombra(0.5f), c - Offset(0.9f * u, 0.4f * u), c + Offset(0.9f * u, 0.4f * u), 0.5f * u)
        }
        textoEn(medidor, p.texto, Rect(tarjeta.left + 2f * u, tarjeta.top, tarjeta.right - 2f * u, tarjeta.bottom), PaletaVapor.LETRA, a.tinta, 9.5f, minimo = 6f)
    }

    // ---------------------------------------------------------------- el zócalo (va en la imagen guardada del pie)

    fun DrawScope.zocaloVapor(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val y0 = g.fondo; val y3 = g.zocalo
        val y1 = y0 + w * 0.02f; val y2 = y3 - w * 0.007f
        val l = g.xa - w * 0.006f; val r = g.xb + w * 0.006f
        // La moldura de arriba (un bocel: luz arriba, sombra abajo), la tabla del zócalo y el filete de abajo.
        val ym = (y0 + y3) / 2
        franja(PINO_LUZ, l, r, y0, (y1 - y0) * 0.5f + 0.5f, a, g, ym)
        franja(PINO_OSC, l, r, y0 + (y1 - y0) * 0.5f, (y1 - y0) * 0.5f + 0.5f, a, g, ym)
        franja(mezcla(PINO, PINO_OSC, 0.45f), l, r, y1, y2 - y1 + 0.5f, a, g, ym)
        franja(mezcla(PINO_OSC, VETA, 0.4f), l, r, y2, y3 - y2, a, g, ym)
        // Un tablero rehundido a lo largo del zócalo.
        val tz = Rect(l + w * 0.03f, y1 + (y2 - y1) * 0.22f, r - w * 0.03f, y2 - (y2 - y1) * 0.2f)
        drawRect(a.sombra(0.12f), tz.topLeft, tz.size)
        drawRect(a.sombra(0.35f), tz.topLeft, Size(tz.width, 1.2f * u))
        drawRect(a.brillo(0.35f, g.cerca(Offset(w * 0.5f, tz.bottom))), Offset(tz.left, tz.bottom - 0.9f * u), Size(tz.width, 0.9f * u))
        drawRect(a.brillo(0.6f, g.cerca(Offset(w * 0.5f, y0))), Offset(l, y0), Size(r - l, 1f * u))
        drawRect(a.sombra(0.45f), Offset(l, y1), Size(r - l, 1.5f * u))
        vetas(Rect(l, y1, r, y2), a.grano(VETA, 0.35f), 3601, 0.6f, grosor = 0.5f, ondulacion = 0.4f)
        // Los golpes de las botas: rayas oscuras y el barniz saltado.
        for (j in 0 until 7) {
            val x = l + (r - l) * (0.06f + 0.88f * azar(3602, j))
            val y = y1 + (y2 - y1) * (0.35f + 0.5f * azar(3603, j))
            drawLine(a.grano(TIZNE, 1f).copy(alpha = 0.5f), Offset(x, y), Offset(x + w * (0.02f + 0.03f * azar(3604, j)), y + w * 0.004f), 1.2f * u)
            if (j % 2 == 0) drawOval(a.luz(mezcla(PINO_LUZ, PAPEL, 0.25f), g.cerca(Offset(x, y))), Offset(x + w * 0.01f, y - w * 0.004f), Size(w * 0.012f, w * 0.005f))
        }
        drawRect(a.sombra(0.3f), Offset(r - w * 0.012f, y1), Size(w * 0.012f, y3 - y1))
    }
}
