package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sin

/**
 * El bodegón de encima de la alacena de Navidad ([MuebleEstanteNavidad]), en la imagen guardada de arriba: el abeto en su maceta
 * envuelta en el vichy rojo del mantel del Ideario, con sus esferas de vidrio y la tira de bombillas enrollada (de día, apagadas;
 * de noche, cada una con su halo); delante, las tarjetas apoyadas y un regalo chico; a la izquierda, el molde de cobre con la
 * cuchara de palo; a la derecha, la palmatoria (de noche, la luz de arriba) y la jarra de loza con abeto y acebo, cortada por el
 * borde. Cada cosa con su sombra en la pared y en el tablero.
 */
internal object CoronaEstanteNavidad {
    private val P = PaletaNavidad

    /** La luz que le toca a lo que está en [p] (de día, con la mancha de la ventana si cae en ella). */
    private fun luzEn(p: Offset, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?, w: Float): LuzNavidad {
        val lit = if (m != null && !a.oscuro) m.toca(p.x / w, p.y / w, ManchaNavidad.FRENTE) else 0f
        val luna = if (a.oscuro && a.noche && m != null) m.toca(p.x / w, p.y / w, ManchaNavidad.FRENTE) * 0.8f else 0f
        val h = if (!a.oscuro) -1f else if (p.x / w < MuebleEstanteNavidad.VELA_X) 1f else -1f
        return LuzNavidad(a, cerca(p), lit, m, luna, h)
    }

    fun DrawScope.encima(a: AireNavidad, apoyo: Float, ch: Float, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width
        cuencoNueces(Offset(w * 0.14f, apoyo + w * 0.004f), w * 0.17f, a, cerca, m)
        plato(Offset(w * 0.3f, apoyo - w * 0.08f), w * 0.072f, a, cerca, m)
        abeto(a, apoyo, cerca, m)
        molde(Offset(w * 0.4f, apoyo + w * 0.004f), w * 0.13f, a, cerca, m)
        maceta(a, apoyo, cerca, m)
        tarjetas(a, apoyo, cerca, m)
        regaloChico(Rect(w * 0.655f, apoyo - w * 0.085f, w * 0.775f, apoyo + w * 0.004f), a, cerca, m)
        jarra(Offset(w * 0.94f, apoyo + w * 0.004f), w * 0.15f, w * 0.15f, a, cerca, m)
        val lv = luzEn(Offset(w * MuebleEstanteNavidad.VELA_X, apoyo - w * 0.08f), a, cerca, m, w)
        val llama = palmatoriaNavidad(Offset(w * MuebleEstanteNavidad.VELA_X, apoyo + w * 0.004f), w * 0.05f,
            apoyo - w * MuebleEstanteNavidad.VELA_Y - w * 0.064f, LuzNavidad(a, maxOf(lv.k, if (a.oscuro) 0.85f else 0f), lv.lit, m, 0f, lv.hacia), 9700)
        if (a.lumbre) llamaNavidad(llama, w * 0.042f, if (a.oscuro && a.noche) 0.6f else 1f)
    }

    // ---------------------------------------------------------------- el abeto

    /** Los pisos del abeto: de cada uno, la altura de su borde de abajo y su medio ancho (en anchos). */
    private val PISOS = listOf(0.4f to 0.2f, 0.31f to 0.175f, 0.22f to 0.15f, 0.135f to 0.125f, 0.055f to 0.1f, -0.02f to 0.075f)

    /**
     * El abeto en maceta: los pisos de ramas, del de abajo al de arriba (cada uno cae sobre el de abajo y le deja su sombra), cada
     * rama con sus agujas en tres verdes (claras del lado de la ventana; de noche, del lado de la vela), las esferas, la tira de
     * bombillas y dos lazos.
     */
    private fun DrawScope.abeto(a: AireNavidad, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val cx = w * MuebleEstanteNavidad.ARBOL_X
        val baseY = apoyo - w * 0.1f
        val kt = cerca(Offset(cx, baseY))
        drawRect(a.dia(P.CORTEZA, 0f, kt), Offset(cx - w * 0.01f, baseY - w * 0.04f), Size(w * 0.02f, w * 0.05f))
        // La silueta de cada piso: un abanico que cae, con el borde de abajo picado por las puntas de las ramas.
        fun piso(y: Float, mitad: Float, alto: Float, semilla: Int): Path = Path().apply {
            val top = y - alto
            moveTo(cx, top)
            val n = 11
            lineTo(cx + mitad * 0.4f, top + alto * 0.45f)
            for (i in 0..n) {
                val f = i / n.toFloat()
                val x = cx + mitad - 2f * mitad * f
                val yy = y + w * 0.01f * sin(f * 3.1416f) + (if (i % 2 == 0) w * 0.014f * (0.5f + 0.5f * azar(semilla, i)) else -w * 0.004f)
                lineTo(x, yy)
            }
            lineTo(cx - mitad * 0.4f, top + alto * 0.45f)
            close()
        }
        val altos = PISOS.indices.map { i -> w * (0.13f + 0.012f * i) }
        val siluetas = PISOS.mapIndexed { i, (fy, fm) -> piso(w * fy + (baseY - w * 0.4f), w * fm, altos[i], 9710 + i) }
        val sombraArbol = Path().apply { siluetas.forEach { addPath(it) } }
        val corre = if (a.oscuro) Offset(-w * 0.025f, w * 0.01f) else Offset(w * 0.03f, w * 0.018f)
        drawPath(corridoNavidad(sombraArbol, corre.x, corre.y), a.sombra(if (a.oscuro) 0.35f else 0.22f))
        val nocheVela = a.oscuro
        for (i in siluetas.indices) {
            val (fy, fm) = PISOS[i]
            val y = w * fy + (baseY - w * 0.4f)
            val mitad = w * fm
            val top = y - altos[i]
            val s = siluetas[i]
            val k = cerca(Offset(cx, y - w * 0.05f))
            // La sombra de este piso en el de abajo.
            if (i > 0) clipPath(siluetas[i - 1]) { drawPath(corridoNavidad(s, w * 0.006f, w * 0.016f), a.sombra(0.45f)) }
            drawPath(s, a.dia(P.ABETO_HONDO, 0f, k))
            clipPath(s) {
                val claras = Path(); val medias = Path(); val hondas = Path(); val alSol = Path(); val filo = Path()
                val ramas = 11
                for (j in 0 until ramas) {
                    val f = -1f + 2f * (j + 0.5f) / ramas + (azar(9720 + i, j) - 0.5f) * 0.08f
                    val s0 = Offset(cx + f * mitad * 0.12f, top + altos[i] * 0.25f)
                    val e0 = Offset(cx + f * mitad * 0.98f, y + w * 0.006f)
                    val c0 = Offset(cx + f * mitad * 0.62f, top + altos[i] * (0.32f + 0.1f * kotlin.math.abs(f)))
                    val pasos = 12
                    for (q in 2..pasos) {
                        val t = q / pasos.toFloat()
                        val mt = 1f - t
                        val p = Offset(mt * mt * s0.x + 2 * mt * t * c0.x + t * t * e0.x, mt * mt * s0.y + 2 * mt * t * c0.y + t * t * e0.y)
                        val l = w * (0.011f + 0.008f * azar(9721 + i, j * 20 + q))
                        for (lado0 in listOf(-1f, 1f)) {
                            val z = j * 40 + q * 2 + (if (lado0 > 0f) 1 else 0)
                            if (azar(9725 + i, z) < 0.18f) continue
                            val gir = (azar(9726 + i, z) - 0.5f) * 0.7f
                            val dx = (f * 0.55f + lado0 * (0.3f + 0.3f * azar(9727 + i, z)) + gir) * l
                            val dy = l * (0.45f + 0.4f * azar(9722 + i, z))
                            val luzLado = (if (nocheVela) f else -f) + lado0 * 0.2f * (if (nocheVela) 1f else -1f) + (azar(9723 + i, j * 20 + q) - 0.5f) * 0.4f
                            val lit = if (m != null && !a.oscuro) m.toca(p.x / w, p.y / w, ManchaNavidad.FRENTE) else 0f
                            val dest = when {
                                lit > 0f && luzLado > -0.2f -> alSol
                                luzLado > 0.35f -> claras
                                luzLado < -0.45f -> hondas
                                else -> medias
                            }
                            dest.moveTo(p.x, p.y); dest.lineTo(p.x + dx, p.y + dy)
                            if (nocheVela && f > 0.25f && lado0 > 0f) { filo.moveTo(p.x, p.y); filo.lineTo(p.x + dx, p.y + dy) }
                        }
                    }
                }
                drawPath(hondas, a.dia(P.ABETO_OSC, 0f, k), style = Stroke(1.7f * u, cap = StrokeCap.Round))
                drawPath(medias, a.dia(P.ABETO, 0f, k), style = Stroke(1.7f * u, cap = StrokeCap.Round))
                drawPath(claras, a.dia(P.ABETO_LUZ, 0f, k), style = Stroke(1.6f * u, cap = StrokeCap.Round))
                if (m != null && !a.oscuro) drawPath(alSol, a.sol(P.ABETO_LUZ, a.dia(P.ABETO_LUZ, 0.1f, k), m.fuerza), style = Stroke(1.6f * u, cap = StrokeCap.Round))
                if (nocheVela) drawPath(filo, P.LLAMA.copy(alpha = (0.5f * cerca(Offset(cx + mitad, y))).coerceIn(0.08f, 0.6f)), style = Stroke(1f * u, cap = StrokeCap.Round))
                // Lo hondo junto al eje, donde no llega la luz.
                drawRect(a.sombra(0.25f), Offset(cx - mitad * 0.12f, top), Size(mitad * 0.24f, altos[i]))
            }
        }
        // Las esferas de vidrio, colgadas de las puntas de las ramas.
        val colores = listOf(P.VIDRIO_ROJO, P.VIDRIO_ORO, P.VIDRIO_AZUL, P.VIDRIO_PLATA, P.VIDRIO_ROJO, P.VIDRIO_ORO, P.VIDRIO_AZUL, P.VIDRIO_ROJO, P.VIDRIO_PLATA, P.VIDRIO_ORO)
        val lugares = listOf(-0.75f to 0, 0.55f to 0, -0.2f to 0, 0.8f to 1, -0.6f to 1, 0.2f to 1, -0.35f to 2, 0.6f to 2, 0.1f to 3, -0.5f to 3)
        lugares.forEachIndexed { j, (f, piso) ->
            val (fy, fm) = PISOS[piso]
            val y = w * fy + (baseY - w * 0.4f)
            val c = Offset(cx + f * w * fm * 0.85f, y - w * 0.006f + w * 0.012f * azar(9730, j))
            val r = w * (0.014f + 0.008f * azar(9731, j))
            val l = luzEn(c, a, cerca, m, w)
            drawLine(l.c(P.LATON_OSC), c - Offset(0f, r * 1.1f), c - Offset(0f, r * 1.9f), 0.8f * u)
            drawCircle(l.sombra(0.35f), r, c + (if (a.oscuro) Offset(-r * 0.3f, r * 0.4f) else Offset(r * 0.5f, r * 0.45f)))
            esferaNavidad(c, r, colores[j], l)
        }
        bombillas(cx, baseY, a, cerca, m)
        listOf(Offset(cx - w * 0.07f, baseY - w * 0.19f), Offset(cx + w * 0.06f, baseY - w * 0.3f)).forEach { n ->
            lazoNavidad(n, w * 0.045f, P.CARMIN, luzEn(n, a, cerca, m, w), 0.55f)
        }
    }

    /**
     * La tira de bombillas (la del Ideario), enrollada en el abeto de arriba abajo en tres vueltas: el cable verde y las bombillas
     * de vidrio de color en forma de gota; de noche, encendidas, cada una con su halo en las ramas.
     */
    private fun DrawScope.bombillas(cx: Float, baseY: Float, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val vueltas = listOf(Triple(0.14f, 0.1f, 0.26f), Triple(0.25f, 0.13f, 0.34f), Triple(0.36f, 0.16f, 0.44f))
        var n = 0
        vueltas.forEachIndexed { v, (fy0, fm, fy1) ->
            val izq = Offset(cx - w * fm, baseY - w * 0.4f + w * fy0)
            val der = Offset(cx + w * fm * 1.05f, baseY - w * 0.4f + w * (fy0 + 0.02f))
            val caida = w * (fy1 - fy0) * 0.35f
            fun punto(t: Float) = Offset(izq.x + (der.x - izq.x) * t, izq.y + (der.y - izq.y) * t + caida * 4f * t * (1f - t))
            val cable = Path().apply {
                val p0 = punto(0f); moveTo(p0.x, p0.y)
                for (s in 1..16) { val p = punto(s / 16f); lineTo(p.x, p.y) }
            }
            val kc = cerca(punto(0.5f))
            drawPath(cable, a.dia(mezcla(P.FILETE, P.NOCHE, 0.4f), 0f, kc), style = Stroke(1.1f * u))
            val cuantas = 4 + v
            for (i in 0 until cuantas) {
                val t = (i + 0.5f) / cuantas
                val p = punto(t)
                val col = n % 4
                n++
                rotate((t - 0.5f) * 40f + (if (i % 2 == 0) 0f else 180f), p) {
                    bombilla(p, col, a, cerca(p))
                }
            }
        }
    }

    /** Una bombilla de la tira en [p] (su casquillo), apuntando hacia abajo en su giro: apagada de día, encendida de noche. */
    private fun DrawScope.bombilla(p: Offset, col: Int, a: AireNavidad, k: Float) {
        val u = density
        drawRect(a.dia(mezcla(P.FILETE, P.NOCHE, 0.2f), 0f, k), Offset(p.x - 1.6f * u, p.y), Size(3.2f * u, 3.4f * u))
        val b = Path().apply {
            moveTo(p.x - 1.8f * u, p.y + 3.2f * u)
            cubicTo(p.x - 3.6f * u, p.y + 6f * u, p.x - 2f * u, p.y + 10.5f * u, p.x, p.y + 11f * u)
            cubicTo(p.x + 2f * u, p.y + 10.5f * u, p.x + 3.6f * u, p.y + 6f * u, p.x + 1.8f * u, p.y + 3.2f * u)
            close()
        }
        if (a.lumbre && a.oscuro) {
            val lc = P.BOMBILLAS[col]
            val f = if (a.noche) 0.6f else 1f
            mancha(Offset(p.x, p.y + 7.5f * u), 16f * u, lc.copy(alpha = 0.3f * f))
            drawPath(b, mezcla(lc, Color.White, 0.25f))
            drawCircle(mezcla(lc, Color.White, 0.75f), 1.4f * u, Offset(p.x, p.y + 7.5f * u))
        } else {
            val vidrio = P.BOMBILLAS_DIA[col]
            drawPath(corridoNavidad(b, 1.2f * u, 1.2f * u), a.sombra(0.22f))
            drawPath(b, a.dia(mezcla(vidrio, Color.White, 0.22f), 0f, k))
            drawPath(Path().apply { op(b, corridoNavidad(b, -1.4f * u, 0f), PathOperation.Difference) }, a.sombra(0.25f))
            drawCircle(a.claro(0.8f), 0.8f * u, Offset(p.x - 1.1f * u, p.y + 5.5f * u))
            // Al ocaso de los Sueños, recién encendidas: un punto de luz.
            if (a.noche) drawCircle(P.BOMBILLAS[col].copy(alpha = 0.8f), 1.3f * u, Offset(p.x, p.y + 7f * u))
        }
    }

    // ---------------------------------------------------------------- la maceta, las tarjetas y el regalo

    /** La maceta de barro envuelta en el vichy rojo del mantel, con el frunce por arriba y el cordel atado con su lazo. */
    private fun DrawScope.maceta(a: AireNavidad, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val cx = w * MuebleEstanteNavidad.ARBOL_X
        val arriba = apoyo - w * 0.1f
        val forma = poligonoNavidad(cx - w * 0.082f, arriba, cx + w * 0.082f, arriba, cx + w * 0.066f, apoyo, cx - w * 0.066f, apoyo)
        val l = luzEn(Offset(cx, apoyo - w * 0.05f), a, cerca, m, w)
        // La sombra en la pared y en el tablero.
        drawPath(corridoNavidad(forma, if (a.oscuro) -w * 0.02f else w * 0.022f, w * 0.008f), a.sombra(if (a.oscuro) 0.4f else 0.24f))
        drawOval(a.sombra(0.45f), Offset(cx - w * 0.08f, apoyo - w * 0.008f), Size(w * 0.19f, w * 0.018f))
        clipPath(forma) {
            // El vichy: el fondo crema y las franjas rojas que se cruzan (más hondo donde se cruzan).
            drawRect(l.c(P.PAPEL), Offset(cx - w * 0.09f, arriba), Size(w * 0.18f, w * 0.1f))
            val paso = w * 0.012f
            var x = cx - w * 0.09f
            while (x < cx + w * 0.09f) { drawRect(l.c(P.CARMIN).copy(alpha = 0.55f), Offset(x, arriba), Size(paso, w * 0.1f)); x += paso * 2 }
            var y = arriba
            while (y < apoyo) { drawRect(l.c(P.CARMIN).copy(alpha = 0.55f), Offset(cx - w * 0.09f, y), Size(w * 0.18f, paso)); y += paso * 2 }
            // Los pliegues de la tela recogida, y el lado de la sombra.
            listOf(-0.05f, -0.015f, 0.025f, 0.055f).forEach { f ->
                drawRect(l.sombra(0.2f), Offset(cx + w * f, arriba), Size(w * 0.012f, w * 0.1f))
                drawRect(l.claro(0.18f), Offset(cx + w * f - w * 0.008f, arriba), Size(w * 0.007f, w * 0.1f))
            }
            drawRect(l.sombra(0.3f), Offset(cx + w * 0.035f, arriba), Size(w * 0.05f, w * 0.1f))
            drawRect(l.claro(0.25f), Offset(cx - w * 0.085f, arriba), Size(w * 0.02f, w * 0.1f))
        }
        // El frunce de arriba, que asoma sobre el borde de la maceta en ondas.
        val frunce = Path().apply {
            moveTo(cx - w * 0.088f, arriba + w * 0.004f)
            for (i in 0 until 8) {
                val x0 = cx - w * 0.088f + w * 0.022f * i
                quadraticTo(x0 + w * 0.011f, arriba - w * (0.02f + 0.008f * (i % 2)), x0 + w * 0.022f, arriba + w * 0.004f)
            }
            lineTo(cx + w * 0.088f, arriba + w * 0.012f); lineTo(cx - w * 0.088f, arriba + w * 0.012f); close()
        }
        drawPath(frunce, l.c(mezcla(P.PAPEL, P.CARMIN, 0.3f)))
        drawPath(frunce, l.sombra(0.3f), style = Stroke(0.8f * u))
        // El cordel con su lazo.
        val yc = arriba + w * 0.024f
        drawLine(l.c(P.CARTULINA, -0.1f), Offset(cx - w * 0.08f, yc), Offset(cx + w * 0.08f, yc + w * 0.002f), 1.8f * u)
        lazoNavidad(Offset(cx - w * 0.02f, yc), w * 0.04f, P.CARTULINA, l, 0.7f)
    }

    /** Las tarjetas de felicitación apoyadas delante de la maceta (las de las fichas del Ideario). */
    private fun DrawScope.tarjetas(a: AireNavidad, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width
        val cx = w * MuebleEstanteNavidad.ARBOL_X
        val r1 = Rect(cx - w * 0.13f, apoyo - w * 0.1f, cx - w * 0.055f, apoyo)
        tarjetaNavidad(r1, -8f, P.CARMIN, luzEn(r1.center, a, cerca, m, w))
        val r2 = Rect(cx - w * 0.075f, apoyo - w * 0.085f, cx + w * 0.003f, apoyo + w * 0.002f)
        tarjetaNavidad(r2, 5f, P.FILETE, luzEn(r2.center, a, cerca, m, w))
    }

    /** El regalo chico, envuelto en papel crema de rayas rojas, con su cinta verde y el lazo. */
    private fun DrawScope.regaloChico(r: Rect, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width
        val l = luzEn(r.center, a, cerca, m, w)
        val tapa = r.height * 0.2f
        drawOval(a.sombra(0.45f), Offset(r.left - r.width * 0.04f, r.bottom - r.height * 0.12f), Size(r.width * 1.15f, r.height * 0.22f))
        drawRect(a.sombra(if (a.oscuro) 0.35f else 0.2f), Offset(r.left + w * 0.016f, r.top + tapa + w * 0.008f), Size(r.width, r.height - tapa))
        val frente = Rect(r.left, r.top + tapa, r.right, r.bottom)
        drawRect(l.c(P.PAPEL), frente.topLeft, frente.size)
        var x = frente.left
        var j = 0
        while (x < frente.right && j < 40) { drawRect(l.c(P.CARMIN), Offset(x, frente.top), Size(r.width * 0.06f, frente.height)); x += r.width * 0.16f; j++ }
        drawPath(poligonoNavidad(r.left, r.top + tapa, r.left + r.width * 0.12f, r.top, r.right + r.width * 0.1f, r.top, r.right, r.top + tapa), l.c(mezcla(P.PAPEL, P.CARMIN, 0.2f), 0.1f))
        drawRect(l.sombra(0.25f), Offset(r.right - r.width * 0.22f, frente.top), Size(r.width * 0.22f, frente.height))
        drawRect(l.claro(0.3f), frente.topLeft, Size(r.width * 0.08f, frente.height))
        val cx = r.left + r.width * 0.45f
        drawRect(l.c(P.FILETE), Offset(cx - r.width * 0.06f, frente.top), Size(r.width * 0.12f, frente.height))
        drawPath(poligonoNavidad(cx - r.width * 0.06f, frente.top, cx + r.width * 0.06f, frente.top, cx + r.width * 0.16f, r.top, cx + r.width * 0.04f, r.top), l.c(P.FILETE))
        lazoNavidad(Offset(cx + r.width * 0.06f, r.top + tapa * 0.4f), r.width * 0.5f, P.FILETE, l, 0.3f)
    }

    // ---------------------------------------------------------------- el plato y el cuenco

    /**
     * El plato de loza apoyado de canto contra el azulejo: el ala con su filete verde, el fondo con una corona de hojas de acebo y
     * bayas pintadas, el borde de luz y su sombra en la pared.
     */
    private fun DrawScope.plato(c: Offset, r: Float, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val l = luzEn(c, a, cerca, m, w)
        val ov = Rect(c.x - r, c.y - r * 0.97f, c.x + r, c.y + r * 0.97f)
        drawOval(a.sombra(if (a.oscuro) 0.35f else 0.22f), ov.topLeft + (if (a.oscuro) Offset(-r * 0.25f, r * 0.08f) else Offset(r * 0.22f, r * 0.12f)), ov.size)
        drawOval(l.c(P.LOZA, -0.04f), ov.topLeft, ov.size)
        drawOval(l.c(P.FILETE), ov.deflate(r * 0.08f).topLeft, ov.deflate(r * 0.08f).size, style = Stroke(r * 0.05f))
        val fondo = ov.deflate(r * 0.28f)
        drawOval(l.c(P.LOZA, -0.12f), fondo.topLeft, fondo.size)
        // La corona pintada: hojitas verdes en círculo y bayas rojas entre ellas.
        val hojas = Path(); val bayas = Path()
        for (j in 0 until 12) {
            val an = j * 0.5236f
            val p = Offset(c.x + r * 0.52f * kotlin.math.cos(an), c.y + r * 0.5f * kotlin.math.sin(an))
            val d = Offset(-kotlin.math.sin(an), kotlin.math.cos(an)) * (r * 0.11f)
            hojas.moveTo(p.x - d.x, p.y - d.y); hojas.quadraticTo(p.x + d.y * 0.6f, p.y - d.x * 0.6f, p.x + d.x, p.y + d.y); hojas.quadraticTo(p.x - d.y * 0.6f, p.y + d.x * 0.6f, p.x - d.x, p.y - d.y)
            if (j % 2 == 0) bayas.addOval(Rect(Offset(c.x + r * 0.4f * kotlin.math.cos(an + 0.26f), c.y + r * 0.38f * kotlin.math.sin(an + 0.26f)), r * 0.04f))
        }
        drawPath(hojas, l.c(P.FILETE_LUZ))
        drawPath(bayas, l.c(P.CARMIN))
        // El volumen: el fondo hundido en sombra arriba, el ala clara del lado de la luz y el brillo del vidriado.
        drawArc(l.sombra(0.2f), 200f, 140f, false, fondo.topLeft, fondo.size, style = Stroke(r * 0.06f))
        drawArc(l.brillo(0.6f), 150f, 70f, false, ov.deflate(r * 0.02f).topLeft, ov.deflate(r * 0.02f).size, style = Stroke(1.4f * u))
        drawArc(l.sombra(0.3f), 330f, 100f, false, ov.topLeft, ov.size, style = Stroke(r * 0.06f))
    }

    /** El cuenco de madera torneada con nueces, avellanas y una naranja con clavos, bajo la ventana. */
    private fun DrawScope.cuencoNueces(pie: Offset, ancho: Float, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val alto = ancho * 0.32f
        val l = luzEn(Offset(pie.x, pie.y - alto), a, cerca, m, w)
        val boca = pie.y - alto
        drawOval(a.sombra(0.45f), Offset(pie.x - ancho * 0.45f, pie.y - w * 0.008f), Size(ancho * 0.98f, w * 0.016f))
        // Lo que hay dentro, asomando por la boca: las nueces arrugadas, las avellanas y la naranja.
        naranjaNavidad(Offset(pie.x + ancho * 0.16f, boca - ancho * 0.06f), ancho * 0.13f, l, 9740)
        for (j in 0 until 7) {
            val c = Offset(pie.x - ancho * 0.36f + ancho * 0.11f * j + (if (j == 4 || j == 5) ancho * 0.1f else 0f), boca - ancho * (0.02f + 0.05f * azar(9741, j)))
            if (j % 3 == 2) {
                drawCircle(l.c(mezcla(P.PINO_OSC, P.COBRE, 0.4f)), ancho * 0.045f, c)
                drawCircle(l.brillo(0.5f), ancho * 0.014f, c - Offset(ancho * 0.015f, ancho * 0.015f))
            } else {
                val n = Rect(c.x - ancho * 0.06f, c.y - ancho * 0.05f, c.x + ancho * 0.06f, c.y + ancho * 0.05f)
                drawOval(l.c(mezcla(P.PINO, P.CORTEZA, 0.35f)), n.topLeft, n.size)
                drawLine(l.c(P.CORTEZA, -0.2f), Offset(n.left + n.width * 0.1f, n.center.y), Offset(n.right - n.width * 0.1f, n.center.y), 0.8f * u)
                drawArc(l.c(P.CORTEZA, -0.1f), 200f, 140f, false, n.deflate(n.width * 0.18f).topLeft, n.deflate(n.width * 0.18f).size, style = Stroke(0.7f * u))
                drawArc(l.claro(0.35f), 200f, 60f, false, n.topLeft, n.size, style = Stroke(1f * u))
            }
        }
        // El cuenco: la panza con sus anillos de torno, el borde y el pie.
        val cuenco = Path().apply {
            moveTo(pie.x - ancho * 0.5f, boca)
            lineTo(pie.x + ancho * 0.5f, boca)
            cubicTo(pie.x + ancho * 0.48f, boca + alto * 0.7f, pie.x + ancho * 0.25f, pie.y - alto * 0.1f, pie.x + ancho * 0.16f, pie.y - alto * 0.1f)
            lineTo(pie.x - ancho * 0.16f, pie.y - alto * 0.1f)
            cubicTo(pie.x - ancho * 0.25f, pie.y - alto * 0.1f, pie.x - ancho * 0.48f, boca + alto * 0.7f, pie.x - ancho * 0.5f, boca)
            close()
        }
        drawPath(cuenco, l.c(P.PINO, -0.08f))
        clipPath(cuenco) {
            listOf(0.3f, 0.55f).forEach { f -> drawLine(l.c(P.PINO_OSC).copy(alpha = 0.5f), Offset(pie.x - ancho, boca + alto * f), Offset(pie.x + ancho, boca + alto * f), 0.9f * u) }
            drawRect(l.claro(0.3f), Offset(pie.x - ancho * 0.5f, boca), Size(ancho * 0.18f, alto))
            drawRect(l.sombra(0.3f), Offset(pie.x + ancho * 0.2f, boca), Size(ancho * 0.3f, alto))
        }
        drawRect(l.c(P.PINO_OSC), Offset(pie.x - ancho * 0.18f, pie.y - alto * 0.12f), Size(ancho * 0.36f, alto * 0.12f))
        drawLine(l.c(P.PINO_LUZ), Offset(pie.x - ancho * 0.5f, boca), Offset(pie.x + ancho * 0.5f, boca), 2f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- el molde de cobre y la jarra

    /**
     * El molde de cobre acanalado, boca abajo sobre el tablero: la cúpula con sus gajos, cada uno con su luz y su reflejo hondo, el
     * ala de abajo y la cuchara de palo apoyada.
     */
    private fun DrawScope.molde(pie: Offset, ancho: Float, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val alto = ancho * 0.78f
        val l = luzEn(Offset(pie.x, pie.y - alto * 0.5f), a, cerca, m, w)
        // La cuchara de palo, apoyada detrás.
        val c0 = Offset(pie.x + ancho * 0.25f, pie.y - w * 0.004f); val c1 = Offset(pie.x - ancho * 0.55f, pie.y - alto * 1.55f)
        drawLine(a.sombra(0.25f), c0 + Offset(w * 0.02f, w * 0.01f), c1 + Offset(w * 0.02f, w * 0.01f), w * 0.011f, StrokeCap.Round)
        drawLine(l.c(P.PINO), c0, c1, w * 0.011f, StrokeCap.Round)
        drawLine(l.claro(0.35f), c0 - Offset(w * 0.003f, 0f), c1 - Offset(w * 0.003f, 0f), w * 0.003f, StrokeCap.Round)
        rotate(-28f, c1) { drawOval(l.c(P.PINO, -0.05f), Offset(c1.x - w * 0.014f, c1.y - w * 0.026f), Size(w * 0.028f, w * 0.04f)) }
        // La sombra del molde en la pared y en el tablero.
        drawOval(a.sombra(0.45f), Offset(pie.x - ancho * 0.48f, pie.y - w * 0.008f), Size(ancho * 1.08f, w * 0.016f))
        val ala = Rect(pie.x - ancho * 0.5f, pie.y - alto * 0.14f, pie.x + ancho * 0.5f, pie.y)
        val cupula = Path().apply {
            moveTo(pie.x - ancho * 0.42f, ala.top)
            cubicTo(pie.x - ancho * 0.44f, pie.y - alto * 0.7f, pie.x - ancho * 0.24f, pie.y - alto, pie.x, pie.y - alto)
            cubicTo(pie.x + ancho * 0.24f, pie.y - alto, pie.x + ancho * 0.44f, pie.y - alto * 0.7f, pie.x + ancho * 0.42f, ala.top)
            close()
        }
        drawPath(corridoNavidad(cupula, if (a.oscuro) -ancho * 0.15f else ancho * 0.2f, ancho * 0.08f), a.sombra(if (a.oscuro) 0.35f else 0.2f))
        drawPath(cupula, l.c(P.COBRE))
        clipPath(cupula) {
            // Los gajos: franjas curvas que se cierran arriba, alternando el reflejo claro y el hondo.
            val gajos = 7
            for (i in 0 until gajos) {
                val f0 = i / gajos.toFloat(); val f1 = (i + 1) / gajos.toFloat()
                val xa = pie.x - ancho * 0.44f + ancho * 0.88f * f0; val xb = pie.x - ancho * 0.44f + ancho * 0.88f * f1
                val g = Path().apply {
                    moveTo(xa, ala.top); lineTo(xb, ala.top)
                    quadraticTo(xb + (pie.x - xb) * 0.3f, pie.y - alto * 0.7f, pie.x, pie.y - alto)
                    quadraticTo(xa + (pie.x - xa) * 0.3f, pie.y - alto * 0.7f, xa, ala.top); close()
                }
                val luzG = (f0 + f1) / 2f
                // El metal pulido: gajos claros y hondos alternados, con el reflejo hondo del cuarto en los de la sombra.
                val col = when {
                    i == 1 -> l.c(P.COBRE_LUZ)
                    luzG > 0.7f -> l.c(mezcla(P.COBRE_OSC, P.TINTA, 0.3f))
                    i % 2 == 0 -> l.c(P.COBRE)
                    else -> l.c(mezcla(P.COBRE_OSC, P.COBRE, 0.35f))
                }
                drawPath(g, col)
                drawPath(g, l.sombra(0.25f), style = Stroke(0.7f * u))
            }
            drawOval(l.brillo(0.75f), Offset(pie.x - ancho * 0.3f, pie.y - alto * 0.78f), Size(ancho * 0.08f, alto * 0.4f))
        }
        // El ala y su borde.
        drawRect(l.c(P.COBRE_OSC), ala.topLeft, ala.size)
        drawRect(l.c(P.COBRE_LUZ), ala.topLeft, Size(ala.width * 0.5f, ala.height * 0.4f))
        drawRect(l.c(P.COBRE), Offset(ala.left + ala.width * 0.5f, ala.top), Size(ala.width * 0.5f, ala.height * 0.4f))
        drawRect(l.brillo(0.5f), ala.topLeft, Size(ala.width * 0.4f, 1f * u))
    }

    /** La jarra de loza con su franja verde y su pico, con ramas de abeto y de acebo, cortada por el borde derecho. */
    private fun DrawScope.jarra(pie: Offset, ancho: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width
        val l = luzEn(Offset(pie.x, pie.y - alto * 0.5f), a, cerca, m, w)
        // Las ramas, detrás de la boca.
        val boca = Offset(pie.x, pie.y - alto * 0.98f)
        ramaAbetoNavidad(boca + Offset(-ancho * 0.1f, 0f), boca + Offset(-ancho * 0.75f, -alto * 0.9f), l, w * 0.026f)
        ramaAbetoNavidad(boca + Offset(ancho * 0.05f, 0f), boca + Offset(ancho * 0.25f, -alto * 1.15f), l, w * 0.026f)
        ramaAbetoNavidad(boca, boca + Offset(-ancho * 0.25f, -alto * 1.05f), l, w * 0.024f)
        aceboNavidad(boca + Offset(-ancho * 0.32f, -alto * 0.35f), w * 0.05f, -30f, l)
        aceboNavidad(boca + Offset(ancho * 0.08f, -alto * 0.5f), w * 0.045f, 40f, l)
        jarraNavidad(pie, ancho, alto, l)
    }
}

/** El cuerpo de una jarra de loza con su franja verde, su pico y su asa: la sombra, la panza con su luz, el brillo. */
internal fun DrawScope.jarraNavidad(pie: Offset, ancho: Float, alto: Float, l: LuzNavidad) {
    val P = PaletaNavidad
    val u = density
    val cuerpo = Path().apply {
        moveTo(pie.x - ancho * 0.32f, pie.y)
        cubicTo(pie.x - ancho * 0.56f, pie.y - alto * 0.35f, pie.x - ancho * 0.48f, pie.y - alto * 0.75f, pie.x - ancho * 0.3f, pie.y - alto * 0.92f)
        lineTo(pie.x - ancho * 0.38f, pie.y - alto)
        lineTo(pie.x + ancho * 0.3f, pie.y - alto)
        cubicTo(pie.x + ancho * 0.44f, pie.y - alto * 0.75f, pie.x + ancho * 0.52f, pie.y - alto * 0.35f, pie.x + ancho * 0.32f, pie.y)
        close()
    }
    drawOval(l.sombra(0.45f), Offset(pie.x - ancho * 0.4f, pie.y - ancho * 0.05f), Size(ancho * 0.9f, ancho * 0.1f))
    drawPath(corridoNavidad(cuerpo, ancho * 0.12f * (-l.hacia), ancho * 0.04f), l.sombra(if (l.a.oscuro) 0.3f else 0.18f))
    // El asa, a la derecha.
    drawArc(l.c(P.LOZA, -0.08f), -80f, 160f, false, Offset(pie.x + ancho * 0.25f, pie.y - alto * 0.85f), Size(ancho * 0.36f, alto * 0.5f), style = Stroke(ancho * 0.07f))
    drawPath(cuerpo, l.c(P.LOZA))
    clipPath(cuerpo) {
        drawRect(l.c(P.FILETE), Offset(pie.x - ancho, pie.y - alto * 0.72f), Size(ancho * 2f, alto * 0.1f))
        drawRect(l.c(P.FILETE), Offset(pie.x - ancho, pie.y - alto * 0.58f), Size(ancho * 2f, alto * 0.025f))
        drawRect(l.sombra(0.22f), Offset(pie.x + ancho * 0.08f * (-l.hacia) - ancho * 0.05f, pie.y - alto), Size(ancho * 0.6f, alto))
        drawRect(l.sombra(0.15f), Offset(pie.x - ancho, pie.y - alto * 0.12f), Size(ancho * 2f, alto * 0.12f))
        drawOval(l.claro(0.55f), Offset(pie.x - ancho * 0.36f * (-l.hacia) - ancho * 0.05f, pie.y - alto * 0.68f), Size(ancho * 0.1f, alto * 0.36f))
    }
    // La boca: el canto de la loza y lo hondo de dentro.
    drawOval(l.c(P.LOZA, -0.25f), Offset(pie.x - ancho * 0.36f, pie.y - alto * 1.03f), Size(ancho * 0.68f, alto * 0.08f))
    drawOval(l.c(P.TINTA, 0f).copy(alpha = 0.6f), Offset(pie.x - ancho * 0.3f, pie.y - alto * 1.02f), Size(ancho * 0.56f, alto * 0.05f))
    drawLine(l.brillo(0.5f), Offset(pie.x - ancho * 0.36f, pie.y - alto), Offset(pie.x, pie.y - alto * 1.02f), 0.9f * u)
}
