package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El haz de tablillas de la libreta de Galia ([MuebleLibretaGalia]): las tablillas de haya apiladas sobre la mesa, con su canto
 * por delante y a la derecha; los dos tientos que las atan por delante; la de arriba, con su marco y su cera de miel, el nombre
 * y la cuenta rayados, los renglones del día, lo de antes a medio alisar y el estilo de bronce. Al abrirse, la tablilla de arriba
 * levanta la trasera sobre los tientos.
 */
internal object HazLibretaGalia {
    private val P = PaletaGalia
    /** La cera de miel de las tablillas, su fondo hondo y el surco que deja el estilo. */
    private val CERA = Color(0xFFC9A257)
    private val CERA_HONDA = Color(0xFF9A7634)
    private val SURCO = Color(0xFF3A2812)
    private val LABIO = Color(0xFFF0DCA8)
    /** El cuero crudo de los tientos. */
    private val TIENTO = mezcla(P.CUERO, P.CUERNO, 0.38f)

    /** Los caminos de la cara de arriba, en sus propias coordenadas (largo a lo ancho, fondo hacia delante). */
    private class Cara(val largo: Float, val fondo: Float, val U: Float) {
        val marco = 11f * U
        val cera = Rect(marco, marco, largo - marco, fondo - marco)
        val alisado = Path()
        val antes = Path()
        val renglones = Path()
        val veta = Path()
        val agujeros = floatArrayOf(largo * 0.16f, largo * 0.84f)

        init {
            for (j in 0 until 7) {
                val y = cera.top + cera.height * (0.08f + 0.86f * azar(9200, j))
                alisado.moveTo(cera.left + 2f * U, y); alisado.cubicTo(cera.left + cera.width * 0.3f, y - 1.5f * U, cera.left + cera.width * 0.7f, y + 1.5f * U, cera.right - 2f * U, y + (azar(9201, j) - 0.5f) * 2f * U)
            }
            // Lo de antes, alisado a medias: trazos sueltos que ya no se leen.
            for (j in 0 until 16) {
                val x = cera.left + cera.width * azar(9202, j); val y = cera.top + cera.height * (0.15f + 0.8f * azar(9203, j))
                antes.moveTo(x, y); antes.lineTo(x + (3f + 9f * azar(9204, j)) * U, y + (azar(9205, j) - 0.5f) * 1.6f * U)
            }
            // Los renglones del día, rayados debajo de la cuenta: trazos rectos de largos distintos, sin letras.
            for (f in 0 until 3) {
                var x = cera.left + 6f * U
                val y = fondo - 31f * U + f * 6.5f * U
                val fin = cera.left + cera.width * (if (f == 2) 0.36f else 0.52f)
                var j = 0
                while (x < fin) {
                    val palabra = (5f + 10f * azar(9210 + f, j)) * U
                    renglones.moveTo(x, y); renglones.lineTo(x + palabra, y + (azar(9220 + f, j) - 0.5f) * 0.8f * U)
                    x += palabra + (2.5f + 2f * azar(9230 + f, j)) * U; j++
                }
            }
            listOf(3.5f, 7.5f).forEach { d ->
                veta.moveTo(2f * U, d * U); veta.lineTo(largo - 2f * U, d * U + 0.6f * U)
                veta.moveTo(2f * U, fondo - d * U); veta.lineTo(largo - 2f * U, fondo - d * U - 0.5f * U)
                veta.moveTo(d * U, marco); veta.lineTo(d * U + 0.4f * U, fondo - marco)
                veta.moveTo(largo - d * U, marco); veta.lineTo(largo - d * U - 0.4f * U, fondo - marco)
            }
        }
    }

    private var cara: Cara? = null
    private fun cara(largo: Float, fondo: Float, U: Float): Cara = cara?.takeIf { it.largo == largo && it.fondo == fondo && it.U == U } ?: Cara(largo, fondo, U).also { cara = it }

    /** El nombre y los dos renglones de la cuenta, ya medidos (cambian muy pocas veces). */
    private var claveTextos = ""
    private var textos: Array<TextLayoutResult?> = arrayOfNulls(3)

    private fun DrawScope.afin(o: Offset, ex: Offset, ey: Offset, bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(ex.x, ex.y, 0f, 0f, ey.x, ey.y, 0f, 0f, 0f, 0f, 1f, 0f, o.x, o.y, 0f, 1f))) }) { bloque() }
    }

    fun DrawScope.haz(e: EstadoLibreta, s: SitioLibretaGalia, a: AireGalia, medidor: TextMeasurer) {
        val U = s.U
        val L = s.largo; val F = s.fondo; val t = s.grueso; val n = s.n
        val ex = s.ex; val ez = s.ez
        val ab = e.abierto.coerceIn(0f, 1f)
        fun base(i: Int) = s.origen + ex * ((azar(9300, i) - 0.5f) * 3f * U) + ez * ((azar(9301, i) - 0.5f) * 3f * U) + Offset(0f, -i * t)
        val arriba = base(n - 1) + Offset(0f, -t)
        val kIzq = s.cerca(arriba, a.oscuro); val kDer = s.cerca(arriba + ex * L, a.oscuro); val kMed = s.cerca(arriba + ex * (L / 2) + ez * (F / 2), a.oscuro)

        // ---- la sombra del haz en la mesa: de día hacia la derecha y atrás; de noche, lejos de la lámpara (a la izquierda)
        val o = s.origen
        val d = if (a.oscuro) Offset(-1.3f * s.alto - 8f * U, -0.1f * s.alto) else Offset(0.8f * s.alto + 4f * U, -0.35f * s.alto - 2f * U)
        val pie = listOf(o, o + ex * L, o + ex * L + ez * F, o + ez * F)
        clipRect(s.mesaIzq, s.mesaFondo, s.w, s.mesaFrente) { drawPath(envolvente(pie + pie.map { it + d }), a.sombra(if (a.oscuro) 0.32f else 0.3f)) }
        drawPath(poligonoGalia(o.x - 2f * U, o.y - 1f * U, (o + ex * L).x + 2f * U, (o + ex * L).y - 1f * U, (o + ex * L).x + 3f * U, (o + ex * L).y + 2.5f * U, o.x - 2f * U, o.y + 2.5f * U), a.sombra(0.4f))

        // ---- los cantos de las tablillas: el de delante y el de la derecha, en un solo camino cada uno
        val frentes = Path(); val lados = Path(); val juntas = Path(); val aristas = Path(); val pares = Path()
        for (i in 0 until n) {
            val b = base(i)
            val c0 = b; val c1 = b + ex * L; val c2 = c1 + ez * F
            frentes.moveTo(c0.x, c0.y); frentes.lineTo(c1.x, c1.y); frentes.lineTo(c1.x, c1.y - t); frentes.lineTo(c0.x, c0.y - t); frentes.close()
            lados.moveTo(c1.x, c1.y); lados.lineTo(c2.x, c2.y); lados.lineTo(c2.x, c2.y - t); lados.lineTo(c1.x, c1.y - t); lados.close()
            if (i % 2 == 1) { pares.moveTo(c0.x, c0.y); pares.lineTo(c1.x, c1.y); pares.lineTo(c1.x, c1.y - t); pares.lineTo(c0.x, c0.y - t); pares.close() }
            juntas.moveTo(c0.x, c0.y - 0.5f * U); juntas.lineTo(c1.x, c1.y - 0.5f * U); juntas.lineTo(c2.x, c2.y - 0.5f * U)
            aristas.moveTo(c0.x, c0.y - t + 0.5f * U); aristas.lineTo(c1.x, c1.y - t + 0.5f * U)
        }
        val xa = o.x; val xb = o.x + L
        if (a.oscuro) {
            drawPath(frentes, Brush.horizontalGradient(listOf(a.luz(P.HAYA_OSC, kIzq), a.luz(P.HAYA_OSC, kMed), a.luz(P.HAYA, kDer)), xa, xb))
            drawPath(lados, a.luz(P.HAYA, (kDer * 1.25f).coerceAtMost(1f)))
        } else {
            drawPath(frentes, Brush.horizontalGradient(listOf(a.dia(P.HAYA, -0.04f), a.dia(P.HAYA_OSC, 0f)), xa, xb))
            drawPath(lados, a.dia(P.HAYA_OSC, -0.35f))
        }
        drawPath(pares, a.sombra(0.1f))
        drawPath(juntas, a.sombra(0.6f), style = Stroke(1f * U))
        drawPath(aristas, if (a.oscuro) a.filo(0.35f * kDer) else a.brillo(0.35f, 1f), style = Stroke(0.7f * U))
        if (a.oscuro) drawPath(lados, a.filo(0.5f * kDer), style = Stroke(0.8f * U))

        // ---- la tablilla de arriba (al abrirse, levanta la trasera sobre los tientos, que están delante)
        val giro = ab * 0.5f
        val ezTapa = ez * kotlin.math.cos(giro) + Offset(0f, -0.656f * kotlin.math.sin(giro))
        val atras = arriba + ezTapa * F
        if (ab > 0.01f) {
            val r0 = arriba + ex * L; val r1 = r0 + ez * F; val r2 = arriba + ex * L + ezTapa * F
            drawPath(poligonoGalia(r0.x, r0.y, r1.x, r1.y, r2.x, r2.y), a.sombra(0.8f))
        }
        val cara = cara(L, F, U)
        val sup = poligonoGalia(arriba.x, arriba.y, (arriba + ex * L).x, (arriba + ex * L).y, (atras + ex * L).x, (atras + ex * L).y, atras.x, atras.y)
        val ejeY = ezTapa * -1f
        fun pinta(sol: Float) = afin(atras, ex, ejeY) {
            val marco: Brush; val cera: Brush
            if (a.oscuro) {
                marco = Brush.horizontalGradient(listOf(a.luz(P.HAYA, kIzq), a.luz(P.HAYA, kMed), a.luz(P.HAYA_LUZ, kDer)), 0f, L)
                cera = Brush.horizontalGradient(listOf(a.etiqueta(CERA, kIzq), a.etiqueta(CERA, kMed), a.etiqueta(CERA, (kDer * 1.1f).coerceAtMost(1f))), 0f, L)
            } else {
                val m = a.dia(P.HAYA, 0.04f); val c = a.dia(CERA, 0f)
                marco = Brush.horizontalGradient(listOf(if (sol > 0f) a.sol(P.HAYA, m, sol) else m, if (sol > 0f) a.sol(P.HAYA, a.dia(P.HAYA, -0.06f), sol) else a.dia(P.HAYA, -0.06f)), 0f, L)
                cera = Brush.horizontalGradient(listOf(if (sol > 0f) a.sol(CERA, c, sol) else c, if (sol > 0f) a.sol(CERA, a.dia(CERA, -0.08f), sol) else a.dia(CERA, -0.08f)), 0f, L)
            }
            drawRect(marco, Offset.Zero, Size(L, F))
            drawRect(cera, cara.cera.topLeft, cara.cera.size)
        }
        pinta(0f)
        // La mancha del sol en la tablilla de arriba: corrida hacia la puerta tanto como levanta el haz.
        if (!a.oscuro) clipPath(sup) { clipPath(s.solMesa(s.alto + t)) { pinta(0.85f) } }
        val k = kMed
        afin(atras, ex, ejeY) {
            val c = cara.cera
            // El marco: su veta, el filo de luz que mira a la puerta y, de noche, el de la llama a la derecha.
            drawPath(cara.veta, a.sombra(0.18f), style = Stroke(0.6f * U))
            drawRect(a.sombra(0.35f), Offset(0f, F - 1.4f * U), Size(L, 1.4f * U))
            if (!a.oscuro) {
                drawRect(a.brillo(0.55f, 1f), Offset.Zero, Size(L, 1.4f * U))
                drawRect(a.brillo(0.45f, 1f), Offset.Zero, Size(1.4f * U, F))
            } else {
                drawRect(a.lunar(0.5f), Offset.Zero, Size(1.3f * U, F))
                drawRect(a.filo(0.75f * kDer), Offset(L - 1.4f * U, 0f), Size(1.4f * U, F))
                drawRect(a.filo(0.4f * k), Offset(L * 0.45f, 0f), Size(L * 0.55f, 1.1f * U))
            }
            // La cera, hundida en el marco: la sombra del lado de la luz y la pared de enfrente encendida.
            val izq = !a.oscuro
            drawRect(a.sombra(0.32f), Offset(if (izq) c.left else c.right - 3f * U, c.top), Size(3f * U, c.height))
            drawRect(a.sombra(0.22f), Offset(c.left, c.bottom - 2.4f * U), Size(c.width, 2.4f * U))
            drawRect(if (a.oscuro) a.filo(0.5f * kIzq.coerceAtLeast(0.3f)) else a.brillo(0.4f, 1f), Offset(if (izq) c.right - 1.4f * U else c.left, c.top), Size(1.4f * U, c.height))
            drawRect(a.brillo(0.3f, k), Offset(c.left, c.top), Size(c.width, 1.2f * U))
            drawRect(CERA_HONDA.copy(alpha = 0.4f), c.topLeft, c.size, style = Stroke(0.8f * U))
            // Las pasadas de la espátula y lo de antes, a medio alisar.
            drawPath(cara.alisado, (if (a.oscuro) a.filo(0.18f * k) else a.claro(0.3f)), style = Stroke(1.6f * U, cap = StrokeCap.Round))
            drawPath(cara.antes, SURCO.copy(alpha = if (a.oscuro) 0.16f else 0.13f), style = Stroke(0.8f * U, cap = StrokeCap.Round))
            // Los agujeros de los tientos, en el marco de delante.
            cara.agujeros.forEach { x -> drawOval(a.sombra(0.75f), Offset(x - 2f * U, F - 7.5f * U), Size(4f * U, 3f * U)) }
            // El nombre y la cuenta, rayados en la cera: el surco hondo y el labio que levanta la luz (de noche, al revés:
            // la llama prende el labio y el surco queda en sombra).
            val tx = textosDe(e, medidor, cara)
            val hondo = if (a.oscuro) mezcla(a.etiqueta(CERA, k), Color.Black, 0.55f) else SURCO
            val labio = if (a.oscuro) mezcla(LABIO, P.LLAMA, 0.25f) else LABIO.copy(alpha = 0.8f)
            val cajas = arrayOf(Rect(c.left + 3f * U, 16f * U, c.right - 3f * U, 44f * U), Rect(c.left + 6f * U, 52f * U, c.right - 6f * U, 66f * U), Rect(c.left + 6f * U, 66f * U, c.right - 6f * U, 79f * U))
            for (i in 0 until 3) {
                val capa = tx[i] ?: continue
                val esq = Offset(cajas[i].center.x - capa.size.width / 2f, cajas[i].center.y - capa.size.height / 2f)
                if (a.oscuro) {
                    drawText(capa, color = hondo, topLeft = esq + Offset(0.6f * U, 0.8f * U))
                    drawText(capa, color = labio, topLeft = esq)
                } else {
                    drawText(capa, color = labio, topLeft = esq + Offset(0.5f * U, 0.7f * U))
                    drawText(capa, color = hondo, topLeft = esq)
                }
            }
            // La raya bajo el nombre y los renglones del día.
            val raya = if (a.oscuro) labio.copy(alpha = 0.7f) else SURCO.copy(alpha = 0.75f)
            drawLine(raya, Offset(L * 0.3f, 48f * U), Offset(L * 0.47f, 48.4f * U), 0.9f * U, StrokeCap.Round)
            drawLine(raya, Offset(L * 0.53f, 48.5f * U), Offset(L * 0.7f, 48.2f * U), 0.9f * U, StrokeCap.Round)
            drawPath(cara.renglones, if (a.oscuro) labio.copy(alpha = 0.5f) else SURCO.copy(alpha = 0.5f), style = Stroke(1.1f * U, cap = StrokeCap.Round))
        }

        // ---- los tientos: salen del agujero, pasan por el canto de delante, bajan por los cantos y se meten por debajo
        val abajo = base(0)
        cara.agujeros.forEachIndexed { i, x ->
            val ag = atras + ex * x + ejeY * (F - 6f * U)
            val borde = arriba + ex * x
            val pie2 = abajo + ex * x + Offset(0f, 0.5f * U)
            val kk = s.cerca(borde, a.oscuro)
            val cuero = if (a.oscuro) a.luz(TIENTO, kk) else a.dia(TIENTO, 0f)
            val camino = Path().apply { moveTo(ag.x, ag.y); quadraticTo(borde.x - 1f * U, borde.y - 1.5f * U, borde.x, borde.y + 0.5f * U); lineTo(pie2.x + 0.6f * U, pie2.y) }
            drawPath(Path().apply { addPath(camino, Offset(if (a.oscuro) -1.2f * U else 1.2f * U, 0.8f * U)) }, a.sombra(0.45f), style = Stroke(3f * U))
            drawPath(camino, cuero, style = Stroke(2.8f * U))
            drawPath(camino, a.brillo(0.3f, kk), style = Stroke(0.6f * U))
            // El nudo junto al agujero y sus dos cabos, que cuelgan por el canto.
            val cabo = Path().apply {
                moveTo(ag.x, ag.y); quadraticTo(ag.x - 5f * U, ag.y + 4f * U, borde.x - 4f * U - i * 1f * U, borde.y + minOf(14f * U, s.alto * 0.6f + 6f * U))
                moveTo(ag.x + 1f * U, ag.y); quadraticTo(ag.x + 6f * U, ag.y + 3f * U, borde.x + 4.5f * U, borde.y + minOf(10f * U, s.alto * 0.45f + 4f * U))
            }
            drawPath(cabo, cuero, style = Stroke(1.5f * U, cap = StrokeCap.Round))
            drawOval(cuero, Offset(ag.x - 3f * U, ag.y - 2.4f * U), Size(6f * U, 4.6f * U))
            drawOval(a.sombra(0.4f), Offset(ag.x - 3f * U, ag.y - 2.4f * U), Size(6f * U, 4.6f * U), style = Stroke(0.6f * U))
        }

        // ---- el estilo de bronce, tendido en la cera abajo a la derecha: la punta y la paleta de alisar
        fun en(x: Float, y: Float) = atras + ex * x + ejeY * y
        val p0 = en(L * 0.5f, F - 19f * U); val p1 = en(L - 12f * U, F - 36f * U)
        val dir = (p1 - p0) / (p1 - p0).getDistance().coerceAtLeast(1f); val nor = Offset(-dir.y, dir.x)
        val so = if (a.oscuro) Offset(-2.5f * U, 1.6f * U) else Offset(2f * U, 2.6f * U)
        val asta = poligonoGalia((p0 + nor * 0.5f * U).x, (p0 + nor * 0.5f * U).y, (p1 + nor * 1.3f * U).x, (p1 + nor * 1.3f * U).y, (p1 - nor * 1.3f * U).x, (p1 - nor * 1.3f * U).y, (p0 - nor * 0.5f * U).x, (p0 - nor * 0.5f * U).y)
        val paleta = poligonoGalia((p1 + nor * 1.2f * U).x, (p1 + nor * 1.2f * U).y, (p1 + dir * 7f * U + nor * 3.2f * U).x, (p1 + dir * 7f * U + nor * 3.2f * U).y,
            (p1 + dir * 7f * U - nor * 3.2f * U).x, (p1 + dir * 7f * U - nor * 3.2f * U).y, (p1 - nor * 1.2f * U).x, (p1 - nor * 1.2f * U).y)
        val ks = s.cerca(p1, a.oscuro)
        drawPath(Path().apply { addPath(asta, so); addPath(paleta, so) }, a.sombra(0.4f))
        drawPath(asta, a.dia(P.BRONCE, 0f, ks))
        drawPath(paleta, a.dia(P.BRONCE_OSC, 0f, ks))
        drawLine(a.dia(P.BRONCE_LUZ, 0f, ks).copy(alpha = 0.85f), p0 + nor * 0.3f * U, p1 + nor * 0.8f * U, 0.6f * U, StrokeCap.Round)
        drawCircle(a.dia(P.BRONCE_OSC, 0f, ks), 1.6f * U, p1)
        destelloGalia(p1 + dir * 3.5f * U + nor * 1f * U, 0.8f * U, a, ks)
    }

    /** El nombre y la cuenta medidos una vez por texto, tamaño y escala (el medidor de la pantalla guarda pocas medidas). */
    private fun DrawScope.textosDe(e: EstadoLibreta, medidor: TextMeasurer, c: Cara): Array<TextLayoutResult?> {
        val clave = "${e.nombre}|${e.cuenta}|${c.largo}|${c.U}|$density|$fontScale"
        if (clave == claveTextos) return textos
        val U = c.U; val cera = c.cera
        val i = e.cuenta.indexOf(" · ")
        val uno = if (i > 0) e.cuenta.substring(0, i).trim() else e.cuenta
        val dos = if (i > 0) e.cuenta.substring(i + 3).trim() else ""
        textos = arrayOf(
            capaEn(medidor, e.nombre, Rect(cera.left + 3f * U, 16f * U, cera.right - 3f * U, 44f * U), P.TITULO, SURCO, 19f, peso = FontWeight.ExtraBold, minimo = 11f),
            capaEn(medidor, uno, Rect(cera.left + 6f * U, 52f * U, cera.right - 6f * U, 66f * U), P.LETRA, SURCO, 10.5f, peso = FontWeight.Bold, minimo = 7f),
            capaEn(medidor, dos, Rect(cera.left + 6f * U, 66f * U, cera.right - 6f * U, 79f * U), P.LETRA, SURCO, 10f, peso = FontWeight.SemiBold, espaciado = 0.2f, minimo = 7f),
        )
        claveTextos = clave
        return textos
    }

    /** La envolvente convexa de unos puntos (para la sombra del haz: el pie y la tapa corrida, juntos). */
    private fun envolvente(pts: List<Offset>): Path {
        val ps = pts.sortedWith(compareBy({ it.x }, { it.y }))
        fun cruz(o: Offset, a: Offset, b: Offset) = (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x)
        val abajo = ArrayList<Offset>(); val arriba = ArrayList<Offset>()
        for (p in ps) { while (abajo.size >= 2 && cruz(abajo[abajo.size - 2], abajo[abajo.size - 1], p) <= 0f) abajo.removeAt(abajo.size - 1); abajo.add(p) }
        for (p in ps.asReversed()) { while (arriba.size >= 2 && cruz(arriba[arriba.size - 2], arriba[arriba.size - 1], p) <= 0f) arriba.removeAt(arriba.size - 1); arriba.add(p) }
        val casco = abajo.dropLast(1) + arriba.dropLast(1)
        return Path().apply { casco.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
    }
}
