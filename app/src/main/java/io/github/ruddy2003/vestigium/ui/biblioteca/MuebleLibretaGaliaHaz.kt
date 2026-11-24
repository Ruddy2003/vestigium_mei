package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
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
import kotlin.math.cos
import kotlin.math.sin

/**
 * El haz de tablillas de la libreta de Galia ([MuebleLibretaGalia]): tablillas gruesas de haya apiladas sobre la mesa, cada una
 * algo corrida y girada (asoman desiguales), con su grueso a la vista; las ata un tiento de cuero crudo que da dos vueltas por
 * encima de la de arriba y acaba en su nudo, con los cabos sueltos colgando por el canto. La de arriba lleva la cera rehundida en
 * su marco, con su brillo, las pasadas de la espátula, lo de antes a medio alisar, el nombre y la cuenta rayados (chicos, como se
 * apunta en una tablilla) y los renglones del día; el estilo de bronce va metido bajo el tiento. Al abrirse, la de arriba levanta
 * la trasera y el tiento se suelta.
 */
internal object HazLibretaGalia {
    private val P = PaletaGalia
    /** La cera de abeja de las tablillas: miel tostada, no amarilla; su fondo hondo y el surco que deja el estilo. */
    private val CERA = Color(0xFFA67C3A)
    private val CERA_HONDA = Color(0xFF7A5522)
    private val SURCO = Color(0xFF2E1E0C)
    private val LABIO = Color(0xFFF0DCA8)
    /** El cuero crudo del tiento. */
    private val TIENTO = mezcla(P.CUERO, P.CUERNO, 0.42f)

    /** Una tablilla del haz: su centro en el pie (en la pantalla, sin la altura), su giro (radianes) y su altura. */
    private class Tablilla(val c: Offset, giro: Float, val y: Float) {
        val ex = Offset(cos(giro), -0.75f * sin(giro))
        val ez = Offset(-sin(giro), -0.75f * cos(giro))
    }

    /** Los caminos de la cara de arriba, en sus propias coordenadas (largo a lo ancho, fondo hacia delante). */
    private class Cara(val largo: Float, val fondo: Float, val U: Float) {
        val marco = 10f * U
        val cera = Rect(marco, marco, largo - marco, fondo - marco)
        val anillo = Path().apply { fillType = PathFillType.EvenOdd; addRect(Rect(0f, 0f, largo, fondo)); addRect(cera) }
        val alisado = Path()
        val antes = Path()
        val renglones = Path()
        val veta = Path()
        /** Las dos vueltas del tiento: por dónde cruzan la cara (atrás y delante, en anchos del largo). */
        val vueltas = floatArrayOf(0.71f, 0.76f, 0.83f, 0.79f)

        init {
            for (j in 0 until 6) {
                val y = cera.top + cera.height * (0.1f + 0.8f * azar(9200, j))
                alisado.moveTo(cera.left + 3f * U, y); alisado.cubicTo(cera.left + cera.width * 0.3f, y - 1.5f * U, cera.left + cera.width * 0.6f, y + 1.5f * U, cera.right - 3f * U, y + (azar(9201, j) - 0.5f) * 2f * U)
            }
            for (j in 0 until 14) {
                val x = cera.left + cera.width * azar(9202, j); val y = cera.top + cera.height * (0.2f + 0.75f * azar(9203, j))
                antes.moveTo(x, y); antes.lineTo(x + (3f + 8f * azar(9204, j)) * U, y + (azar(9205, j) - 0.5f) * 1.6f * U)
            }
            // Los renglones del día, rayados debajo de la cuenta: trazos rectos de largos distintos, sin letras.
            for (f in 0 until 4) {
                var x = cera.left + 5f * U
                val y = cera.top + (44f + f * 8f) * U
                val fin = cera.left + cera.width * (if (f == 3) 0.34f else 0.6f)
                var j = 0
                while (x < fin) {
                    val palabra = (5f + 10f * azar(9210 + f, j)) * U
                    renglones.moveTo(x, y); renglones.lineTo(minOf(x + palabra, fin), y + (azar(9220 + f, j) - 0.5f) * 0.8f * U)
                    x += palabra + (2.5f + 2f * azar(9230 + f, j)) * U; j++
                }
            }
            listOf(3f, 6.5f).forEach { d ->
                veta.moveTo(2f * U, d * U); veta.lineTo(largo - 2f * U, d * U + 0.6f * U)
                veta.moveTo(2f * U, fondo - d * U); veta.lineTo(largo - 2f * U, fondo - d * U - 0.5f * U)
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

    private fun cuad(a: Offset, b: Offset, c: Offset, d: Offset) = poligonoGalia(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y)

    fun DrawScope.haz(e: EstadoLibreta, s: SitioLibretaGalia, a: AireGalia, medidor: TextMeasurer) {
        val U = s.U
        val L = s.largo; val F = s.fondo; val t = s.grueso; val n = s.n
        val ab = e.abierto.coerceIn(0f, 1f)
        val grado = 0.017453f
        val tabs = List(n) { i ->
            val arriba = i == n - 1
            val giro = (-4f + (if (arriba) (azar(9300, i) - 0.5f) * 2f else (azar(9300, i) - 0.5f) * 8f)) * grado
            val dx = if (arriba) 0f else (azar(9301, i) - 0.5f) * 12f * U
            val dz = if (arriba) 0f else (azar(9302, i) - 0.5f) * 9f * U
            Tablilla(s.centro + Offset(dx, -0.75f * dz), giro, (i + 1) * t)
        }
        fun esq(tb: Tablilla, sx: Float, sz: Float, y: Float) = tb.c + tb.ex * sx + tb.ez * sz + Offset(0f, -y)
        val luz = if (a.oscuro) Offset(-3.5f * U, 1.5f * U) else Offset(3.5f * U, 2f * U)

        // ---- la sombra del haz en la mesa (de día a la derecha; de noche a la izquierda, lejos de la lámpara)
        val pie = tabs.flatMap { tb -> listOf(esq(tb, -L / 2, -F / 2, 0f), esq(tb, L / 2, -F / 2, 0f), esq(tb, L / 2, F / 2, 0f), esq(tb, -L / 2, F / 2, 0f)) }
        val d = if (a.oscuro) Offset(-0.55f * s.alto - 6f * U, -0.05f * s.alto) else Offset(0.7f * s.alto + 4f * U, -0.3f * s.alto - 2f * U)
        clipRect(s.mesaIzq, s.mesaFondo, s.w, s.mesaFrente) { drawPath(envolvente(pie + pie.map { it + d }), a.sombra(if (a.oscuro) 0.32f else 0.3f)) }

        // ---- las tablillas, de abajo arriba: la sombra de cada una en la de debajo, su cara, su canto de delante y el de la derecha
        val cara = cara(L, F, U)
        val kMed = s.cerca(s.centro + Offset(0f, -s.alto - F * 0.3f), a.oscuro)
        val kDer = s.cerca(s.centro + Offset(L * 0.5f, -s.alto), a.oscuro)
        tabs.forEachIndexed { i, tb ->
            val y0 = tb.y - t; val y1 = tb.y
            val esTapa = i == n - 1
            val ezC = if (esTapa && ab > 0.01f) tb.ez * cos(ab * 0.5f) + Offset(0f, -0.656f * sin(ab * 0.5f)) else tb.ez
            val fl = esq(tb, -L / 2, -F / 2, y1); val fr = esq(tb, L / 2, -F / 2, y1)
            val br = fr + ezC * F; val bl = fl + ezC * F
            val k = s.cerca(fr, a.oscuro)
            // La sombra que echa en la de debajo (o en la mesa).
            drawPath(cuad(esq(tb, -L / 2, F / 2, y0) + luz, esq(tb, L / 2, F / 2, y0) + luz, esq(tb, L / 2, -F / 2, y0) + luz, esq(tb, -L / 2, -F / 2, y0) + luz), a.sombra(0.4f))
            // Los cantos: el de delante (la haya a contraluz) y el de la derecha (la testa, más honda; de noche, encendida).
            val fl0 = fl + Offset(0f, t); val fr0 = fr + Offset(0f, t); val br0 = br + Offset(0f, t)
            val frente = cuad(fl, fr, fr0, fl0)
            val lado = cuad(fr, br, br0, fr0)
            if (a.oscuro) {
                drawPath(frente, Brush.horizontalGradient(listOf(a.luz(P.HAYA_OSC, kMed * 0.6f), a.luz(P.HAYA, k)), fl.x, fr.x))
                drawPath(lado, a.luz(P.HAYA, (k * 1.25f).coerceAtMost(1f)))
                drawPath(lado, a.filo(0.45f * k), style = Stroke(0.8f * U))
            } else {
                drawPath(frente, Brush.horizontalGradient(listOf(a.dia(P.HAYA, -0.02f), a.dia(P.HAYA_OSC, -0.05f)), fl.x, fr.x))
                drawPath(lado, a.dia(P.HAYA_OSC, -0.38f))
            }
            // La veta del canto y su arista de arriba, que levanta la luz.
            val vetas = Path()
            for (q in 1 until 3) { val f = q / 3f; vetas.moveTo(fl.x + 4f * U, fl.y + t * f); vetas.lineTo(fr.x - 4f * U, fr.y + t * f + (azar(9310 + i, q) - 0.5f) * 1.2f * U) }
            drawPath(vetas, a.sombra(0.18f), style = Stroke(0.6f * U))
            drawLine(if (a.oscuro) a.filo(0.5f * k) else a.brillo(0.45f, 1f), fl + Offset(0f, 0.5f * U), fr + Offset(0f, 0.5f * U), 0.9f * U)
            drawLine(a.sombra(0.5f), fl0 - Offset(0f, 0.4f * U), fr0 - Offset(0f, 0.4f * U), 0.8f * U)
            // La cara: el marco de haya y, en la de arriba, la cera rehundida con todo lo suyo.
            afin(bl, tb.ex, ezC * -1f) {
                val marco = if (a.oscuro) Brush.horizontalGradient(listOf(a.luz(P.HAYA, kMed * 0.7f), a.luz(P.HAYA_LUZ, kDer)), 0f, L)
                else Brush.horizontalGradient(listOf(a.dia(P.HAYA, 0.06f), a.dia(P.HAYA, -0.06f)), 0f, L)
                drawRect(marco, Offset.Zero, Size(L, F))
                drawPath(cara.veta, a.sombra(0.16f), style = Stroke(0.6f * U))
                if (!esTapa) {
                    drawRect(a.dia(CERA_HONDA, 0f, kMed), cara.cera.topLeft, cara.cera.size)
                } else {
                    val cera = if (a.oscuro) Brush.horizontalGradient(listOf(a.etiqueta(CERA, kMed * 0.8f), a.etiqueta(CERA, (kDer * 1.1f).coerceAtMost(1f))), 0f, L)
                    else Brush.horizontalGradient(listOf(a.dia(CERA, 0.02f), a.dia(CERA, -0.08f)), 0f, L)
                    drawRect(cera, cara.cera.topLeft, cara.cera.size)
                }
                // Los cantos de la cara: claro el de atrás y el de la izquierda (miran a la puerta), con la sombra propia teñida.
                if (!a.oscuro) {
                    drawRect(a.brillo(0.5f, 1f), Offset.Zero, Size(L, 1.3f * U))
                    drawRect(a.brillo(0.4f, 1f), Offset.Zero, Size(1.3f * U, F))
                } else {
                    drawRect(a.lunar(0.4f), Offset.Zero, Size(1.2f * U, F))
                    drawRect(a.filo(0.7f * kDer), Offset(L - 1.4f * U, 0f), Size(1.4f * U, F))
                }
            }
            if (esTapa) tapa(e, s, a, medidor, cara, bl, tb.ex, ezC * -1f, kMed, kDer, ab)
        }
        val tb = tabs.last()
        val ezC = if (ab > 0.01f) tb.ez * cos(ab * 0.5f) + Offset(0f, -0.656f * sin(ab * 0.5f)) else tb.ez
        val fl = esq(tb, -L / 2, -F / 2, tb.y)
        val bl = fl + ezC * F
        fun en(x: Float, y: Float) = bl + tb.ex * x + ezC * -y

        // ---- la mancha del sol en la cara de arriba: prende el marco (no la cera, que solo brilla), corrida hacia la puerta
        if (!a.oscuro) clipPath(s.solAlto(s.alto)) { afin(bl, tb.ex, ezC * -1f) { drawPath(cara.anillo, a.sol(P.HAYA, a.dia(P.HAYA, 0.06f), 0.8f)) } }
        afin(bl, tb.ex, ezC * -1f) {
            val c = cara.cera
            // El brillo de la cera: un reflejo alargado, del lado de la puerta de día y del de la lámpara de noche.
            if (!a.oscuro) {
                // El sol en la cera no la blanquea: un reflejo angosto y corto, con su punto más vivo.
                val r = Rect(c.left + c.width * 0.08f, c.top + c.height * 0.6f, c.left + c.width * 0.42f, c.top + c.height * 0.67f)
                drawOval(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFFFFF0D0).copy(alpha = 0.55f), Color.Transparent), r.left, r.right), r.topLeft, r.size)
                drawLine(Color(0xFFFFF6E0).copy(alpha = 0.7f), Offset(r.left + r.width * 0.38f, r.center.y), Offset(r.left + r.width * 0.55f, r.center.y - 0.2f * U), 0.9f * U, StrokeCap.Round)
            } else {
                val r = Rect(c.left + c.width * 0.45f, c.top + c.height * 0.45f, c.right - 2f * U, c.top + c.height * 0.68f)
                drawOval(Brush.horizontalGradient(listOf(Color.Transparent, P.LLAMA.copy(alpha = 0.34f * kDer), Color.Transparent), r.left, r.right), r.topLeft, r.size)
            }
        }

        // ---- el tiento: dos vueltas por encima de la de arriba y por los cantos; el nudo delante y los cabos sueltos
        val alfa = (1f - ab * 1.6f).coerceIn(0f, 1f)
        if (alfa <= 0f) return
        val cuero = if (a.oscuro) a.luz(TIENTO, kDer) else a.dia(TIENTO, 0f)
        val v = cara.vueltas
        val camino = Path(); val sobre = Path()
        for (q in 0 until 2) {
            val atras = en(L * v[2 * q], 0f); val delante = en(L * v[2 * q + 1], F)
            sobre.moveTo(atras.x, atras.y); sobre.lineTo(delante.x, delante.y)
            // Por los cantos de delante: baja tirante hasta la de abajo y se mete por debajo.
            val pie0 = esq(tabs[0], -L / 2 + L * v[2 * q + 1], -F / 2, 0f) + Offset(q * 1.2f * U, -1f * U)
            camino.moveTo(delante.x, delante.y)
            camino.quadraticTo(delante.x + (pie0.x - delante.x) * 0.5f - 1.5f * U, (delante.y + pie0.y) / 2, pie0.x, pie0.y)
        }
        val todo = Path().apply { addPath(sobre); addPath(camino) }
        drawPath(Path().apply { addPath(todo, luz * 0.5f) }, a.sombra(0.45f * alfa), style = Stroke(4.2f * U))
        // El estilo de bronce, metido bajo las vueltas.
        estilo(s, a, en(L * 0.46f, F * 0.86f), en(L - 6f * U, F * 0.42f), luz)
        drawPath(todo, cuero.copy(alpha = alfa), style = Stroke(3.5f * U, cap = StrokeCap.Round))
        drawPath(todo, mezcla(cuero, Color.Black, 0.35f).copy(alpha = 0.6f * alfa), style = Stroke(0.6f * U))
        drawPath(sobre, a.brillo(0.35f * alfa, kDer), style = Stroke(0.8f * U, cap = StrokeCap.Round))
        // El nudo, en el canto de delante, y los dos cabos que cuelgan por los cantos hasta la mesa.
        val nudo = en(L * (v[1] + v[3]) / 2f, F - 2f * U)
        val cabos = Path().apply {
            moveTo(nudo.x, nudo.y); cubicTo(nudo.x - 4f * U, nudo.y + 6f * U, nudo.x - 9f * U, nudo.y + s.alto * 0.6f, nudo.x - 6f * U, nudo.y + s.alto + 6f * U)
            moveTo(nudo.x + 1f * U, nudo.y); cubicTo(nudo.x + 6f * U, nudo.y + 5f * U, nudo.x + 9f * U, nudo.y + s.alto * 0.5f, nudo.x + 13f * U, nudo.y + s.alto * 0.7f + 4f * U)
        }
        drawPath(Path().apply { addPath(cabos, luz * 0.6f) }, a.sombra(0.4f * alfa), style = Stroke(2.4f * U, cap = StrokeCap.Round))
        drawPath(cabos, cuero.copy(alpha = alfa), style = Stroke(2.2f * U, cap = StrokeCap.Round))
        drawOval(cuero.copy(alpha = alfa), Offset(nudo.x - 4.5f * U, nudo.y - 3.2f * U), Size(9f * U, 6.4f * U))
        drawOval(a.sombra(0.45f * alfa), Offset(nudo.x - 4.5f * U, nudo.y - 3.2f * U), Size(9f * U, 6.4f * U), style = Stroke(0.7f * U))
        drawLine(mezcla(cuero, Color.Black, 0.3f).copy(alpha = alfa), Offset(nudo.x - 1f * U, nudo.y - 2.6f * U), Offset(nudo.x + 1f * U, nudo.y + 2.6f * U), 0.7f * U)
        drawCircle(a.brillo(0.45f * alfa, kDer), 1.1f * U, Offset(nudo.x - 2f * U, nudo.y - 1.4f * U))
    }

    /** La cera de la de arriba: rehundida en el marco, alisada, con lo de antes, el nombre, la cuenta y los renglones del día. */
    private fun DrawScope.tapa(e: EstadoLibreta, s: SitioLibretaGalia, a: AireGalia, medidor: TextMeasurer, cara: Cara, o: Offset, ex: Offset, ey: Offset, k: Float, kDer: Float, ab: Float) {
        val U = s.U
        afin(o, ex, ey) {
            val c = cara.cera
            // Lo rehundido: la pared del marco de atrás, que se ve, y la sombra que echa el de la izquierda (de noche, el de la derecha).
            drawRect(a.dia(P.HAYA_OSC, -0.05f, k), c.topLeft, Size(c.width, 2.6f * U))
            drawRect(a.sombra(0.35f), Offset(if (a.oscuro) c.right - 3f * U else c.left, c.top), Size(3f * U, c.height))
            drawRect(a.sombra(0.25f), Offset(c.left, c.top + 2.6f * U), Size(c.width, 1.6f * U))
            drawRect(if (a.oscuro) a.filo(0.45f * kDer) else a.brillo(0.35f, 1f), Offset(c.left, c.bottom - 1.2f * U), Size(c.width, 1.2f * U))
            drawRect(CERA_HONDA.copy(alpha = 0.5f), c.topLeft, c.size, style = Stroke(0.8f * U))
            drawPath(cara.alisado, if (a.oscuro) a.filo(0.16f * k) else a.claro(0.22f), style = Stroke(1.6f * U, cap = StrokeCap.Round))
            drawPath(cara.antes, SURCO.copy(alpha = if (a.oscuro) 0.2f else 0.16f), style = Stroke(0.8f * U, cap = StrokeCap.Round))
            // El nombre y la cuenta, rayados chicos arriba a la izquierda, como se apunta en una tablilla.
            val tx = textosDe(e, medidor, cara)
            val hondo = if (a.oscuro) mezcla(a.etiqueta(CERA, k), Color.Black, 0.55f) else SURCO
            val labio = if (a.oscuro) mezcla(LABIO, P.LLAMA, 0.25f) else LABIO.copy(alpha = 0.6f)
            val ys = floatArrayOf(c.top + 12f * U, c.top + 25f * U, c.top + 34f * U)
            for (i in 0 until 3) {
                val capa = tx[i] ?: continue
                val esq = Offset(c.left + 6f * U, ys[i] - capa.size.height / 2f)
                if (a.oscuro) {
                    drawText(capa, color = hondo, topLeft = esq + Offset(0.5f * U, 0.7f * U))
                    drawText(capa, color = labio, topLeft = esq)
                } else {
                    drawText(capa, color = labio, topLeft = esq + Offset(0.4f * U, 0.6f * U))
                    drawText(capa, color = hondo, topLeft = esq)
                }
            }
            drawPath(cara.renglones, if (a.oscuro) labio.copy(alpha = 0.5f) else SURCO.copy(alpha = 0.55f), style = Stroke(1f * U, cap = StrokeCap.Round))
        }
    }

    /** El estilo de bronce: la punta y la paleta de alisar, con su sombra en la cera. */
    private fun DrawScope.estilo(s: SitioLibretaGalia, a: AireGalia, p0: Offset, p1: Offset, luz: Offset) {
        val U = s.U
        val dir = (p1 - p0) / (p1 - p0).getDistance().coerceAtLeast(1f); val nor = Offset(-dir.y, dir.x)
        val asta = poligonoGalia((p0 + nor * 0.5f * U).x, (p0 + nor * 0.5f * U).y, (p1 + nor * 1.3f * U).x, (p1 + nor * 1.3f * U).y, (p1 - nor * 1.3f * U).x, (p1 - nor * 1.3f * U).y, (p0 - nor * 0.5f * U).x, (p0 - nor * 0.5f * U).y)
        val paleta = poligonoGalia((p1 + nor * 1.2f * U).x, (p1 + nor * 1.2f * U).y, (p1 + dir * 7f * U + nor * 3.2f * U).x, (p1 + dir * 7f * U + nor * 3.2f * U).y,
            (p1 + dir * 7f * U - nor * 3.2f * U).x, (p1 + dir * 7f * U - nor * 3.2f * U).y, (p1 - nor * 1.2f * U).x, (p1 - nor * 1.2f * U).y)
        val ks = s.cerca(p1, a.oscuro)
        drawPath(Path().apply { addPath(asta, luz * 0.7f); addPath(paleta, luz * 0.7f) }, a.sombra(0.4f))
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
        val U = c.U; val ancho = c.cera.width * 0.66f
        val i = e.cuenta.indexOf(" · ")
        val uno = if (i > 0) e.cuenta.substring(0, i).trim() else e.cuenta
        val dos = if (i > 0) e.cuenta.substring(i + 3).trim() else ""
        fun medida(t: String, tam: Float, letra: androidx.compose.ui.text.font.FontFamily, peso: FontWeight, alto: Float): TextLayoutResult? {
            val r = Rect(0f, 0f, ancho, alto)
            val capa = capaEn(medidor, t, r, letra, SURCO, tam, peso = peso, minimo = tam * 0.6f) ?: return null
            return capa
        }
        textos = arrayOf(medida(e.nombre, 13f, P.TITULO, FontWeight.ExtraBold, 14f * U), medida(uno, 8.5f, P.LETRA, FontWeight.Bold, 9.5f * U), medida(dos, 8.5f, P.LETRA, FontWeight.SemiBold, 9.5f * U))
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
