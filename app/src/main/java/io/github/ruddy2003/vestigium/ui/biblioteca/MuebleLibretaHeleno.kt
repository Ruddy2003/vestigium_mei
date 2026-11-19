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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.sqrt

/**
 * La libreta de momentos de Helénico (la usa `EstanteHeleno`, en `BibliotecaAntiguedad.kt`): el rincón del telar de la misma
 * casa ática del Ideario y del estante. El muro, encalado arriba y con un zócalo alto gris violeta, con el filete rojo y el
 * azul entre los dos. A la izquierda, cortado por el borde, el telar de pesas: el enjulio con lo tejido, la tela de lana cruda
 * con su franja púrpura, la urdimbre en haces y las pesas de barro. En el banco de tablón, de pie contra el muro, las
 * tablillas de momentos: un políptico de boj atado por la izquierda con su cordón (del que cuelga el estilo), con el nombre y
 * la cuenta escritos a tinta en la tapa; por el canto se ven las tablillas, más cuantas más hojas. Al lado, la píxide y el
 * ovillo; arriba, el espejo de bronce colgado; abajo, en el suelo, el brasero de barro. De día el sol entra por la izquierda;
 * de noche alumbra el brasero desde abajo a la derecha y entra un poco de luna por la izquierda. Al abrirse, la tapa gira sobre
 * el cordón y deja ver la cera de la primera tablilla.
 */
internal object MuebleLibretaHeleno {
    private val P = PaletaLibretaHeleno
    private val LETRA: FontFamily = Letra.garamond

    val ALTO: Dp = 252.dp

    // ---------------------------------------------------------------- lo que no cambia de un dibujo a otro

    /** Los caminos que solo dependen del tamaño: la luz que entra, los guijarros del suelo y la veta del boj. */
    private class Piezas(val w: Float, val h: Float, u: Float) {
        val sol = Path()
        val penumbra = Path()
        val guijarros = Path()
        val guijarrosLuz = Path()

        init {
            sol.moveTo(0f, 0f); sol.lineTo(w * 0.44f, 0f); sol.lineTo(w * 0.6f, h); sol.lineTo(0f, h); sol.close()
            penumbra.moveTo(w * 0.44f, 0f); penumbra.lineTo(w * 0.5f, 0f); penumbra.lineTo(w * 0.66f, h); penumbra.lineTo(w * 0.6f, h); penumbra.close()
            val suelo = h * 0.86f
            for (k in 0 until 18) {
                val p = Offset(w * azar(761, k), suelo + (h - suelo) * (0.2f + 0.75f * azar(762, k)))
                val r = (0.8f + 1.6f * azar(763, k)) * u
                guijarros.addOval(Rect(p.x - r + 0.6f * u, p.y - r * 0.4f, p.x + r + 0.6f * u, p.y + r * 0.7f))
                guijarrosLuz.addOval(Rect(p.x - r, p.y - r * 0.6f, p.x + r, p.y + r * 0.5f))
            }
        }
    }

    private var cache: Piezas? = null
    private fun piezas(w: Float, h: Float, u: Float): Piezas = cache?.takeIf { it.w == w && it.h == h } ?: Piezas(w, h, u).also { cache = it }

    // ---------------------------------------------------------------- la escena

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val a = AireLibretaHeleno(oscuro)
        val pz = piezas(w, h, u)
        val ab = e.abierto.coerceIn(0f, 1f)
        val friso = h * 0.25f
        val suelo = h * 0.86f
        val tope = h * 0.6f
        // El brasero, abajo a la derecha: la luz de la noche.
        val brasa = Offset(w - h * 0.2f, h * 0.83f)
        fun k(p: Offset): Float = (1f - (p - brasa).getDistance() / (h * 1.35f)).coerceIn(0f, 1f)
        fun l(p: Offset): Float = if (!oscuro) 0f else (1f - p.x / (w * 0.55f)).coerceIn(0f, 1f) * (1f - p.y / (h * 1.3f)).coerceIn(0f, 1f)
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo * 0.95f, largo * 0.55f)
            val v = p - brasa; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 1.5f)
        }

        // ---- el muro: el revoque encalado de arriba, los filetes y el zócalo alto
        val lejos = Offset(w * 0.3f, h * 0.2f)
        drawRect(a.luz(P.CAL, k(lejos), l(lejos)), Offset.Zero, Size(w, friso))
        pinceladas(Rect(0f, 0f, w, friso), a.luz(mezcla(P.CAL, sombraMueble(1f), 0.25f), k(lejos)), 771, 26, u, 0.08f)
        pinceladas(Rect(0f, 0f, w, friso), a.luz(mezcla(P.CAL, P.ARCILLA, 0.3f), k(lejos)), 772, 12, u, 0.07f)
        // Un remiendo del revoque y una grieta fina.
        drawOval(a.luz(mezcla(P.CAL, Color.White, 0.3f), k(lejos)).copy(alpha = 0.4f), Offset(w * 0.62f, h * 0.06f), Size(h * 0.16f, h * 0.07f))
        drawPath(Path().apply { moveTo(w * 0.7f, 0f); lineTo(w * 0.705f, h * 0.04f); lineTo(w * 0.698f, h * 0.07f); lineTo(w * 0.71f, h * 0.1f) }, a.sombra(0.3f), style = Stroke(0.7f * u))
        val zoc = Offset(w * 0.5f, h * 0.55f)
        drawRect(a.luz(P.ZOCALO, k(zoc), l(zoc)), Offset(0f, friso), Size(w, suelo - friso))
        pinceladas(Rect(0f, friso, w, suelo), a.luz(mezcla(P.ZOCALO, luzMueble(1f), 0.25f), k(zoc)), 773, 22, u, 0.09f)
        pinceladas(Rect(0f, friso, w, suelo), a.sombra(1f), 774, 14, u, 0.08f)
        drawRect(a.luz(P.EGEO, k(lejos), l(lejos)), Offset(0f, friso - h * 0.026f), Size(w, h * 0.008f))
        drawRect(a.luz(P.ROJO, k(lejos), l(lejos)), Offset(0f, friso - h * 0.012f), Size(w, h * 0.016f))
        // Las orillas de la pintura, a mano.
        for (j in 0 until 14) {
            val x = w * j / 13f
            drawOval(a.luz(P.ZOCALO, k(zoc), l(zoc)), Offset(x - w * 0.04f, friso + h * 0.002f - 1.2f * u * azar(775, j)), Size(w * 0.08f, 2.4f * u))
        }
        // El zócalo gastado: dos desconchones donde asoma el revoque.
        for (j in 0 until 3) {
            val o = Offset(w * (0.15f + 0.7f * azar(776, j)), friso + (suelo - friso) * (0.5f + 0.4f * azar(777, j)))
            val r = h * (0.012f + 0.01f * azar(778, j))
            drawOval(a.luz(mezcla(P.ZOCALO, P.CAL, 0.5f), k(o), l(o)), Offset(o.x - r * 1.5f, o.y - r * 0.7f), Size(r * 3f, r * 1.4f))
            drawOval(a.sombra(0.4f), Offset(o.x - r * 1.5f, o.y - r * 0.7f), Size(r * 3f, r * 0.5f))
        }
        // ---- el suelo de tierra apisonada
        val cs = Offset(w * 0.5f, h * 0.93f)
        drawRect(a.luz(P.TIERRA, k(cs), l(cs)), Offset(0f, suelo), Size(w, h - suelo))
        pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(P.TIERRA, sombraMueble(1f), 0.4f), k(cs)), 779, 12, u, 0.12f)
        drawPath(pz.guijarros, a.sombra(0.3f))
        drawPath(pz.guijarrosLuz, a.luz(mezcla(P.TIERRA, P.CAL, 0.4f), k(cs), l(cs)))
        drawRect(a.sombra(0.42f), Offset(0f, suelo), Size(w, 1.5f * u))
        drawRect(a.sombra(0.14f), Offset(0f, suelo + 1.5f * u), Size(w, 4f * u))

        // ---- la luz que entra por la izquierda, en el muro (el sol; de noche, la luna)
        if (!oscuro) {
            drawPath(pz.sol, luzMueble(0.16f))
            drawPath(pz.penumbra, luzMueble(0.06f))
        } else {
            drawPath(pz.sol, P.LUNA.copy(alpha = 0.05f))
            // El charco de la brasa, que sube por el zócalo desde abajo a la derecha.
            mancha(brasa, h * 1.5f, P.BRASA.copy(alpha = 0.24f))
            mancha(brasa, h * 0.6f, P.BRASA.copy(alpha = 0.26f))
        }

        // ---- el telar, a la izquierda
        val xTelar = h * 0.34f
        with(RinconLibretaHeleno) { telar(xTelar, h, suelo, a, ::k, ::l, caida(Offset(xTelar * 0.5f, h * 0.5f), h * 0.03f)) }

        // ---- el espejo, colgado arriba a la derecha
        val clavo = Offset(w - h * 0.24f, h * 0.035f)
        with(RinconLibretaHeleno) { espejo(clavo, h * 0.075f, a, k(clavo + Offset(0f, h * 0.17f)), l(clavo), caida(clavo + Offset(0f, h * 0.17f), h * 0.025f)) }

        // ---- el banco, con las tablillas de pie contra el muro, la píxide y el ovillo
        val x0b = xTelar + h * 0.02f
        with(RinconLibretaHeleno) { banco(x0b, w + h * 0.05f, tope, h, suelo, a, ::k, ::l) }
        // Las tablillas: el políptico, grande, en el centro del banco.
        val lleno = (sqrt(e.hojas.coerceAtLeast(0).toFloat()) / 14f).coerceIn(0f, 1f)
        val grueso = h * (0.035f + 0.1f * lleno)
        // En un teléfono angosto el políptico se achica un poco para no salirse por la derecha.
        val ancho = (w - x0b - h * 0.14f - grueso).coerceIn(h * 0.52f, h * 0.66f); val alto = ancho * 0.64f
        val cx = (w * 0.5f + h * 0.08f).coerceIn(x0b + ancho * 0.5f + h * 0.04f, maxOf(x0b + ancho * 0.5f + h * 0.04f, w - ancho * 0.5f - grueso - h * 0.06f))
        val tapa = Rect(cx - ancho / 2, tope + h * 0.012f - alto, cx + ancho / 2, tope + h * 0.012f)
        tablillas(e, medidor, tapa, grueso, ab, a, k(tapa.center), l(tapa.center), caida(tapa.center, h * 0.045f))
        // La píxide y el ovillo, a la derecha de las tablillas.
        val xPix = tapa.right + grueso * 0.6f + h * 0.12f
        if (xPix < w - h * 0.04f) with(RinconLibretaHeleno) { pixide(Offset(xPix, tope + h * 0.018f), h, a, k(Offset(xPix, tope)), l(Offset(xPix, tope)), caida(Offset(xPix, tope), h * 0.03f)) }
        val ovC = Offset(tapa.left - h * 0.02f, tope + h * 0.0f)
        with(RinconLibretaHeleno) { ovillo(ovC, h * 0.032f, Offset(xTelar - h * 0.01f, h * 0.53f), a, k(ovC), l(ovC)) }

        // ---- lo de debajo del banco: la madeja colgada del canto y las pesas de repuesto en el suelo
        val xMad = tapa.left + tapa.width * 0.3f
        with(RinconLibretaHeleno) { madeja(xMad, tope + h * 0.024f, h * 0.17f, a, k(Offset(xMad, tope + h * 0.1f)), l(Offset(xMad, tope + h * 0.1f))) }
        val pPesas = Offset(x0b + h * 0.26f, h * 0.93f)
        with(RinconLibretaHeleno) { pesasSueltas(pPesas, h, a, k(pPesas), l(pPesas)) }

        // ---- el brasero, en el suelo a la derecha
        with(RinconLibretaHeleno) { brasero(Offset(brasa.x, h * 0.975f), h, a, maxOf(k(brasa), if (oscuro) 0.9f else 0f), l(brasa), caida(brasa, h * 0.03f)) }

        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- las tablillas

    /**
     * El políptico de tablillas, de pie sobre el banco y apoyado en el muro: el canto con una tablilla por cada tanto de hojas,
     * la tapa de boj con su marco, el nombre y la cuenta a tinta, el cordón de la izquierda con el estilo colgado. Con [ab] la
     * tapa gira sobre el cordón y deja ver la cera de la primera tablilla.
     */
    private fun DrawScope.tablillas(e: EstadoLibreta, medidor: TextMeasurer, r: Rect, grueso: Float, ab: Float, a: AireLibretaHeleno, k: Float, l: Float, caida: Offset) {
        val u = density
        val o = Offset(grueso * 0.62f, -grueso * 0.5f)
        val esquina = CornerRadius(2.6f * u)
        // Su sombra en el zócalo y en el banco.
        drawPath(Path().apply {
            moveTo(r.left + caida.x, r.top + caida.y); lineTo(r.right + o.x + caida.x, r.top + o.y + caida.y); lineTo(r.right + o.x + caida.x, r.bottom); lineTo(r.left, r.bottom); close()
        }, a.sombra(if (a.oscuro) 0.45f else 0.3f))
        drawOval(a.sombra(0.4f), Offset(r.left - r.width * 0.02f, r.bottom - 3f * u), Size(r.width * 1.1f + o.x, 6f * u))
        // Las tablillas de detrás, cada una un poco corrida: se ven sus cantos escalonados, más cuantas más hojas.
        val n = (3 + e.hojas / 5).coerceIn(3, 14)
        val cera = a.luz(P.CERA, k, l)
        for (i in n - 1 downTo 1) {
            val f = i / (n - 1f)
            val off = o * f + Offset((azar(e.semilla, 700 + i) - 0.5f) * 1.8f * u, (azar(e.semilla, 750 + i) - 0.5f) * 1.4f * u)
            val col = mezcla(a.luz(if (i % 2 == 0) P.BOJ_OSC else mezcla(P.BOJ, P.BOJ_OSC, 0.3f), maxOf(k, 0.35f), l), if (a.oscuro) P.NOCHE else sombraMueble(1f), 0.06f + 0.22f * f)
            drawRoundRect(col, r.topLeft + off, r.size, esquina)
            // El canto de la cera entre una tablilla y la otra, y el filo de arriba a la luz.
            drawRoundRect(cera.copy(alpha = 0.7f), r.topLeft + off, r.size, esquina, style = Stroke(1f * u))
            drawLine(if (a.oscuro) a.filo(0.4f * k) else a.claro(0.35f), r.topLeft + off + Offset(3f * u, 0.6f * u), Offset(r.right, r.top) + off + Offset(-3f * u, 0.6f * u), 0.7f * u)
        }
        // La primera tablilla por dentro (se ve al abrir): el marco de boj y la cera con renglones incisos.
        if (ab > 0.01f) {
            drawRoundRect(a.luz(P.BOJ, k, l), r.topLeft, r.size, esquina)
            val c = Rect(r.left + r.width * 0.06f, r.top + r.height * 0.1f, r.right - r.width * 0.05f, r.bottom - r.height * 0.1f)
            drawRect(cera, c.topLeft, c.size)
            drawRect(a.sombra(0.4f), c.topLeft, Size(c.width, 2f * u))
            val inciso = a.panel(P.INCISO, k).copy(alpha = 0.6f)
            var y = c.top + 7f * u; var j = 0
            while (y < c.bottom - 4f * u) {
                drawLine(inciso, Offset(c.left + 6f * u, y), Offset(c.left + 6f * u + c.width * (0.55f + 0.35f * azar(e.semilla, 900 + j)), y), 0.9f * u, StrokeCap.Round)
                y += 6.5f * u; j++
            }
        }
        // La tapa: gira sobre el cordón de la izquierda.
        withTransform({ scale(1f - 0.92f * ab, 1f, Offset(r.left, r.center.y)) }) {
            tapaTablillas(e, medidor, r, a, k)
            drawRoundRect(sombraMueble(0.5f * ab), r.topLeft, r.size, esquina)
        }
        // La vuelta de lana púrpura que las cierra, por la derecha, con su nudo y sus cabos (al abrir, se suelta).
        if (ab < 0.05f) {
            val x = r.right - r.width * 0.045f
            val lana = a.luz(P.PURPURA, k, l)
            drawLine(a.sombra(0.35f), Offset(x + 1.5f * u, r.top), Offset(x + 1.5f * u, r.bottom), 3.4f * u)
            drawLine(lana, Offset(x, r.top - 1f * u), Offset(x, r.bottom), 3.4f * u)
            drawLine(lana, Offset(x, r.top - 1f * u), Offset(x + o.x, r.top + o.y - 1f * u), 3.4f * u)
            var y = r.top; while (y < r.bottom) { drawLine(a.sombra(0.3f), Offset(x - 1.7f * u, y + 1.6f * u), Offset(x + 1.7f * u, y), 0.6f * u); y += 2.4f * u }
            drawLine(if (a.oscuro) a.filo(0.5f * k) else a.claro(0.35f), Offset(x - 1f * u, r.top), Offset(x - 1f * u, r.bottom), 0.7f * u)
            val nudo = Offset(x, r.top + r.height * 0.82f)
            drawCircle(lana, 3f * u, nudo)
            drawCircle(a.sombra(0.3f), 3f * u, nudo + Offset(0.8f * u, 0.8f * u), style = Stroke(0.8f * u))
            drawLine(lana, nudo, nudo + Offset(-4f * u, r.height * 0.2f), 1.6f * u, StrokeCap.Round)
            drawLine(lana, nudo, nudo + Offset(3f * u, r.height * 0.24f), 1.6f * u, StrokeCap.Round)
        }
        // El cordón que las ata por la izquierda, con su nudo, sus cabos y el estilo colgado.
        val cordon = a.luz(mezcla(P.LANA, P.TIERRA, 0.25f), k, l)
        val agujeros = listOf(r.top + r.height * 0.24f, r.top + r.height * 0.76f)
        agujeros.forEach { y ->
            val p = Offset(r.left + r.width * 0.035f, y)
            drawCircle(a.sombra(0.6f), 2.2f * u, p)
            val gaza = Path().apply { moveTo(p.x, p.y); cubicTo(p.x - 9f * u, p.y - 4f * u, p.x - 10f * u, p.y + 6f * u, p.x - 1f * u, p.y + 5f * u) }
            drawPath(gaza, a.sombra(0.4f), style = Stroke(2.6f * u))
            drawPath(gaza, cordon, style = Stroke(2f * u))
        }
        val nudo = Offset(r.left - 5f * u, agujeros[1] + 5f * u)
        drawCircle(cordon, 2.4f * u, nudo)
        val estiloArriba = Offset(nudo.x - 3f * u, nudo.y + r.height * 0.18f)
        drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x - 4f * u, nudo.y + 8f * u, estiloArriba.x + 2f * u, estiloArriba.y - 8f * u, estiloArriba.x, estiloArriba.y) }, cordon, style = Stroke(1.1f * u))
        drawLine(cordon, nudo, nudo + Offset(3f * u, 9f * u), 1.1f * u, StrokeCap.Round)
        estilo(estiloArriba, r.height * 0.42f, a, k, l)
    }

    /**
     * La tapa: el marco de boj con las esquinas gastadas y la cera oscura hundida, con lo de antes borrado a medias por la
     * paleta del estilo; el nombre y la cuenta, incisos (dejan ver la cera clara de abajo, como en las pestañas del índice).
     */
    private fun DrawScope.tapaTablillas(e: EstadoLibreta, medidor: TextMeasurer, r: Rect, a: AireLibretaHeleno, k: Float) {
        val u = density
        val esquina = CornerRadius(2.6f * u)
        val boj = a.luz(P.BOJ, maxOf(k, 0.45f))
        drawRoundRect(boj, r.topLeft, r.size, esquina)
        val m = r.height * 0.1f
        val cera = Rect(r.left + m * 1.15f, r.top + m, r.right - m, r.bottom - m)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // La veta fina del boj, en el marco.
            for (j in 0 until 6) {
                val y = r.top + r.height * (0.03f + 0.18f * j + 0.04f * azar(e.semilla, 810 + j))
                drawLine(a.luz(P.BOJ_OSC, maxOf(k, 0.45f)).copy(alpha = 0.45f), Offset(r.left, y), Offset(r.right, y + 1.2f * u * (azar(e.semilla, 820 + j) - 0.5f)), 0.6f * u)
            }
        }
        // Las esquinas, más oscuras de tanto tocarlas.
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (p in listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight)) drawCircle(a.sombra(0.12f), r.height * 0.09f, p)
        }
        // El canto del marco: a la luz arriba y a la izquierda, en sombra abajo y a la derecha; de noche, la brasa en el de la derecha.
        drawRoundRect(a.claro(0.45f), r.topLeft, Size(r.width, 1.4f * u), esquina)
        drawRect(a.claro(0.3f), r.topLeft + Offset(0f, 2f * u), Size(1.2f * u, r.height - 4f * u))
        drawRect(a.sombra(0.35f), Offset(r.left + 2f * u, r.bottom - 1.6f * u), Size(r.width - 4f * u, 1.6f * u))
        drawRect(a.sombra(0.3f), Offset(r.right - 1.4f * u, r.top + 2f * u), Size(1.4f * u, r.height - 4f * u))
        if (a.oscuro) drawRect(a.filo(0.6f * k), Offset(r.right - 1.4f * u, r.top + 2f * u), Size(1.4f * u, r.height - 4f * u))
        // La cera hundida: la sombra del marco arriba y a la izquierda, el filo de luz abajo y a la derecha.
        drawRect(a.luz(P.CERA, k), cera.topLeft, cera.size)
        // Lo escrito antes y alisado con la paleta: renglones fantasma y las pasadas de la paleta, más claras.
        val borrado = a.panel(P.INCISO, k)
        val fantasma = Path()
        for (j in 0 until 7) {
            val y = cera.top + cera.height * (0.1f + 0.125f * j)
            var x = cera.left + cera.width * 0.05f
            val fin = cera.left + cera.width * (0.45f + 0.5f * azar(e.semilla, 840 + j))
            var q = 0
            while (x < fin) {
                val largo = cera.width * (0.03f + 0.08f * azar(e.semilla + j, q))
                fantasma.moveTo(x, y); fantasma.lineTo(minOf(x + largo, fin), y + 0.5f * u * (azar(e.semilla + j, q + 30) - 0.5f))
                x += largo + cera.width * 0.02f; q++
            }
        }
        drawPath(fantasma, borrado.copy(alpha = 0.09f), style = Stroke(0.9f * u))
        for (j in 0 until 3) {
            drawOval(borrado.copy(alpha = 0.05f), Offset(cera.left + cera.width * (0.1f + 0.3f * j), cera.top + cera.height * (0.15f + 0.25f * azar(e.semilla, 860 + j))), Size(cera.width * 0.32f, cera.height * 0.12f))
        }
        if (!a.oscuro) drawOval(luzMueble(0.05f), Offset(cera.left + cera.width * 0.05f, cera.top + cera.height * 0.05f), Size(cera.width * 0.5f, cera.height * 0.4f))
        drawRect(a.sombra(0.55f), cera.topLeft, Size(cera.width, 2.2f * u))
        drawRect(a.sombra(0.35f), cera.topLeft, Size(1.8f * u, cera.height))
        drawRect(a.claro(0.25f), Offset(cera.left, cera.bottom - 1.2f * u), Size(cera.width, 1.2f * u))
        // El nombre y la cuenta, incisos, con una raya entre los dos.
        val inciso = a.panel(P.INCISO, k)
        val nombre = Rect(cera.left + 6f * u, cera.top + cera.height * 0.1f, cera.right - 6f * u, cera.top + cera.height * 0.56f)
        textoEn(medidor, e.nombre, nombre, LETRA, inciso, 17f, peso = FontWeight.Bold, minimo = 8f)
        drawLine(inciso.copy(alpha = 0.5f), Offset(cera.center.x - cera.width * 0.2f, cera.top + cera.height * 0.6f), Offset(cera.center.x + cera.width * 0.2f, cera.top + cera.height * 0.6f), 0.8f * u)
        textoEn(medidor, e.cuenta, Rect(cera.left + 4f * u, cera.top + cera.height * 0.64f, cera.right - 4f * u, cera.bottom - cera.height * 0.07f), LETRA, inciso.copy(alpha = 0.82f), 12f, minimo = 6.5f)
    }

    /** El estilo de bronce, colgado del cordón: la punta abajo y la paleta de borrar arriba. */
    private fun DrawScope.estilo(arriba: Offset, largo: Float, a: AireLibretaHeleno, k: Float, l: Float) {
        val u = density
        val abajo = arriba + Offset(-largo * 0.06f, largo)
        val bronce = a.luz(P.BRONCE, k, l)
        drawLine(a.sombra(0.35f), arriba + Offset(2f * u, 2f * u), abajo + Offset(2f * u, 2f * u), 2f * u)
        drawLine(bronce, arriba, abajo, 1.8f * u, StrokeCap.Round)
        drawPath(Path().apply {
            moveTo(arriba.x - 3.4f * u, arriba.y); lineTo(arriba.x + 3.4f * u, arriba.y); lineTo(arriba.x + 1f * u, arriba.y + 6f * u); lineTo(arriba.x - 1f * u, arriba.y + 6f * u); close()
        }, bronce)
        drawLine(if (a.oscuro) a.filo(0.6f * k) else a.claro(0.5f), arriba + Offset(-0.6f * u, 6f * u), abajo + Offset(-0.6f * u, -3f * u), 0.5f * u)
        drawCircle(a.luz(P.BRONCE_LUZ, k, l), 1.3f * u, arriba + Offset(0f, largo * 0.3f))
    }
}
