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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La libreta de momentos de Helénico (la usa `EstanteHeleno`, en `BibliotecaAntiguedad.kt`): el rincón del telar de la misma
 * casa ática del Ideario y del estante. El muro, encalado arriba y con un zócalo alto gris violeta, con el filete rojo y el
 * azul entre los dos, con sus remiendos y desconchones. A la izquierda, cortado por el borde, el telar de pesas: el enjulio con
 * lo tejido, la tela de lana cruda con su franja púrpura, la urdimbre en haces y las pesas de barro. En el banco de tablón, de
 * pie contra el muro, las tablillas de momentos: un políptico de boj. La tapa lleva la cera por fuera, pardo verdosa, con su
 * brillo rasante, las pasadas de la paleta y lo de antes a medio borrar, y el nombre y la cuenta incisos; el marco de boj, con
 * su veta, más alto que la cera; por detrás, los cantos escalonados de las demás tablillas (más cuantas más hojas). El cordón
 * las ata por la izquierda (el estilo cuelga del nudo) y la vuelta de lana púrpura las cierra cruzando la esquina. Al lado, la píxide y el ovillo; arriba, el
 * espejo de bronce colgado; abajo, la madeja colgada del canto, las pesas de repuesto y el brasero de barro.
 *
 * De día el sol entra por la izquierda. De noche alumbra el brasero desde el suelo a la derecha: su charco cálido en el suelo,
 * las patas, el canto del banco y la parte baja del políptico; el telar queda a la luz fría de la luna y casi a oscuras. Al
 * abrirse, la tapa sigue girando sobre el cordón (el nombre se desvanece, no se deforma) y deja ver la cera entera.
 */
internal object MuebleLibretaHeleno {
    private val P = PaletaLibretaHeleno
    /** La cera de las tablillas: pardo verdosa (cera de abeja con hollín), no negra. */
    private val CERA_VERDE = Color(0xFF3A3524)
    private val LETRA: FontFamily = Letra.garamond

    val ALTO: Dp = 252.dp

    // ---------------------------------------------------------------- lo que no cambia de un dibujo a otro

    /** Los caminos que solo dependen del tamaño: la luz que entra, los guijarros del suelo y los remiendos del muro. */
    private class Piezas(val w: Float, val h: Float, u: Float) {
        val sol = Path()
        val penumbra = Path()
        val guijarros = Path()
        val guijarrosLuz = Path()
        val remiendo = Path()
        val grieta = Path()

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
            // Un remiendo del revoque, arriba a la derecha, con la orilla de la llana.
            val c = Offset(w * 0.64f, h * 0.09f)
            for (k in 0 until 10) {
                val ang = 6.283f * k / 10
                val rr = h * (0.06f + 0.035f * azar(764, k))
                val q = Offset(c.x + rr * cos(ang) * 1.7f, c.y + rr * sin(ang) * 0.8f)
                if (k == 0) remiendo.moveTo(q.x, q.y) else remiendo.lineTo(q.x, q.y)
            }
            remiendo.close()
            // Una grieta que baja del friso por el zócalo.
            var g = Offset(w * 0.78f, h * 0.27f); grieta.moveTo(g.x, g.y)
            for (k in 0 until 7) { g += Offset((azar(765, k) - 0.5f) * h * 0.03f, h * 0.035f); grieta.lineTo(g.x, g.y) }
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
        // El brasero, abajo a la derecha: la luz de la noche. Su alcance es corto: lo lejano (el telar) queda casi a oscuras.
        val brasa = Offset(w - h * 0.2f, h * 0.83f)
        fun k(p: Offset): Float = (1f - (p - brasa).getDistance() / (h * 1.15f)).coerceIn(0f, 1f)
        fun l(p: Offset): Float = if (!oscuro) 0f else (1f - p.x / (w * 0.55f)).coerceIn(0f, 1f) * (1f - p.y / (h * 1.3f)).coerceIn(0f, 1f)
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo * 0.95f, largo * 0.55f)
            val v = p - brasa; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 1.5f)
        }

        // ---- dónde va cada cosa (lo que el espejo necesita saber de las tablillas)
        val xTelar = h * 0.34f
        val x0b = xTelar + h * 0.02f
        val lleno = (sqrt(e.hojas.coerceAtLeast(0).toFloat()) / 14f).coerceIn(0f, 1f)
        val grueso = h * (0.035f + 0.1f * lleno)
        // En un teléfono angosto el políptico se achica un poco para no salirse por la derecha.
        val ancho = (w - x0b - h * 0.16f - grueso).coerceIn(h * 0.5f, h * 0.64f); val alto = ancho * 0.66f
        val cx = (w * 0.5f + h * 0.08f).coerceIn(x0b + ancho * 0.5f + h * 0.05f, maxOf(x0b + ancho * 0.5f + h * 0.05f, w - ancho * 0.5f - grueso - h * 0.07f))
        val tabla = Rect(cx - ancho / 2, tope + h * 0.012f - alto, cx + ancho / 2, tope + h * 0.012f)
        val detras = Offset(grueso * 0.62f, -grueso * 0.5f)

        muro(pz, a, friso, suelo, ::k, ::l)

        // ---- la luz que entra por la izquierda (el sol; de noche, la luna) y el charco del brasero
        if (!oscuro) {
            drawPath(pz.sol, luzMueble(0.16f))
            drawPath(pz.penumbra, luzMueble(0.06f))
        } else {
            drawPath(pz.sol, P.LUNA.copy(alpha = 0.06f))
            drawPath(pz.penumbra, P.LUNA.copy(alpha = 0.03f))
        }

        // ---- el telar, a la izquierda
        with(RinconLibretaHeleno) { telar(xTelar, h, suelo, a, ::k, ::l, caida(Offset(xTelar * 0.5f, h * 0.5f), h * 0.03f)) }

        // ---- el espejo, colgado arriba a la derecha: si no cabe al lado del políptico, más arriba y más chico
        val angosto = w < h * 1.35f
        val rEsp = h * (if (angosto) 0.06f else 0.075f)
        val derechaTablas = tabla.right + detras.x + h * 0.01f
        val xEsp = maxOf(w - h * 0.21f, minOf(derechaTablas + rEsp + h * 0.03f, w - rEsp - h * 0.02f))
        var yDisco = h * 0.035f + rEsp * 2.25f
        if (xEsp - rEsp < derechaTablas) yDisco = minOf(yDisco, tabla.top + detras.y - rEsp - h * 0.012f)
        val clavo = Offset(xEsp, (yDisco - rEsp * 2.25f).coerceAtLeast(h * 0.012f))
        with(RinconLibretaHeleno) { espejo(clavo, rEsp, a, k(clavo + Offset(0f, h * 0.17f)), l(clavo), caida(clavo + Offset(0f, h * 0.17f), h * 0.025f)) }

        // ---- el banco, con las tablillas de pie contra el muro, la píxide y el ovillo
        with(RinconLibretaHeleno) { banco(x0b, w + h * 0.05f, tope, h, suelo, a, ::k, ::l) }
        tablillas(e, medidor, tabla, grueso, ab, a, ::k, l(tabla.center), caida(tabla.center, h * 0.045f))
        val xPix = tabla.right + detras.x + h * 0.12f
        if (xPix < w - h * 0.04f) with(RinconLibretaHeleno) { pixide(Offset(xPix, tope + h * 0.018f), h, a, k(Offset(xPix, tope)), l(Offset(xPix, tope)), caida(Offset(xPix, tope), h * 0.03f)) }
        val ovC = Offset(tabla.left - h * 0.025f, tope)
        with(RinconLibretaHeleno) { ovillo(ovC, h * 0.032f, Offset(xTelar - h * 0.01f, h * 0.53f), a, k(ovC), l(ovC)) }

        // ---- lo de debajo del banco: la madeja colgada del canto y las pesas de repuesto en el suelo
        val xMad = tabla.left + tabla.width * 0.32f
        with(RinconLibretaHeleno) { madeja(xMad, tope + h * 0.024f, h * 0.17f, a, k(Offset(xMad, tope + h * 0.1f)), l(Offset(xMad, tope + h * 0.1f)), caida(Offset(xMad, tope + h * 0.1f), h * 0.02f)) }
        val pPesas = Offset(x0b + h * 0.26f, h * 0.93f)
        with(RinconLibretaHeleno) { pesasSueltas(pPesas, h, a, k(pPesas), l(pPesas)) }

        // ---- de noche, el charco del brasero: en el suelo, las patas y el canto del banco y la parte baja del políptico
        if (oscuro) {
            mancha(brasa, h * 0.8f, P.BRASA.copy(alpha = 0.3f))
            mancha(brasa + Offset(0f, h * 0.05f), h * 0.34f, P.ASCUA.copy(alpha = 0.3f))
        }

        // ---- el brasero, en el suelo a la derecha
        with(RinconLibretaHeleno) { brasero(Offset(brasa.x, h * 0.975f), h, a, maxOf(k(brasa), if (oscuro) 0.9f else 0f), l(brasa), caida(brasa, h * 0.03f)) }

        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    /**
     * El muro: el revoque encalado de arriba, con sus pinceladas, un remiendo y una grieta; los filetes; el zócalo alto con sus
     * desconchones, la humedad que sube del suelo y el roce del banco; y el suelo de tierra apisonada con sus guijarros.
     */
    private fun DrawScope.muro(pz: Piezas, a: AireLibretaHeleno, friso: Float, suelo: Float, k: (Offset) -> Float, l: (Offset) -> Float) {
        val u = density
        val w = size.width; val h = size.height
        val lejos = Offset(w * 0.3f, h * 0.15f)
        drawRect(a.luz(P.CAL, k(lejos), l(lejos)), Offset.Zero, Size(w, friso))
        pinceladas(Rect(0f, 0f, w, friso), a.luz(mezcla(P.CAL, sombraMueble(1f), 0.25f), k(lejos)), 771, 30, u, 0.09f)
        pinceladas(Rect(0f, 0f, w, friso), a.luz(mezcla(P.CAL, P.ARCILLA, 0.3f), k(lejos)), 772, 16, u, 0.08f)
        pinceladas(Rect(0f, 0f, w, friso), luzMueble(1f), 770, 12, u, if (a.oscuro) 0.02f else 0.1f)
        drawPath(pz.remiendo, a.luz(mezcla(P.CAL, Color.White, 0.35f), k(lejos), l(lejos)).copy(alpha = 0.5f))
        drawPath(pz.remiendo, a.sombra(0.12f), style = Stroke(0.8f * u))
        val zoc = Offset(w * 0.5f, h * 0.55f)
        drawRect(a.luz(P.ZOCALO, k(zoc), l(zoc)), Offset(0f, friso), Size(w, suelo - friso))
        pinceladas(Rect(0f, friso, w, suelo), a.luz(mezcla(P.ZOCALO, luzMueble(1f), 0.25f), k(zoc)), 773, 30, u, 0.1f)
        pinceladas(Rect(0f, friso, w, suelo), a.luz(mezcla(P.ZOCALO, P.ROJO, 0.3f), k(zoc)), 778, 14, u, 0.1f)
        pinceladas(Rect(0f, friso, w, suelo), a.sombra(1f), 774, 16, u, 0.09f)
        // La humedad que sube del suelo: el pie del zócalo, más oscuro y manchado.
        pinceladas(Rect(0f, suelo - h * 0.08f, w, suelo), a.sombra(1f), 779, 18, u, 0.14f)
        drawRect(a.luz(P.EGEO, k(lejos), l(lejos)), Offset(0f, friso - h * 0.026f), Size(w, h * 0.008f))
        drawRect(a.luz(P.ROJO, k(lejos), l(lejos)), Offset(0f, friso - h * 0.012f), Size(w, h * 0.016f))
        // Las orillas de la pintura, a mano.
        for (j in 0 until 14) {
            val x = w * j / 13f
            drawOval(a.luz(P.ZOCALO, k(zoc), l(zoc)), Offset(x - w * 0.04f, friso + h * 0.002f - 1.2f * u * azar(775, j)), Size(w * 0.08f, 2.4f * u))
        }
        drawPath(pz.grieta, a.sombra(0.4f), style = Stroke(0.8f * u))
        drawPath(pz.grieta, luzMueble(if (a.oscuro) 0.04f else 0.2f), style = Stroke(0.5f * u))
        // El zócalo gastado: desconchones donde asoma el revoque, con su borde roto en sombra.
        for (j in 0 until 5) {
            val o = Offset(w * (0.08f + 0.86f * azar(776, j)), friso + (suelo - friso) * (0.2f + 0.7f * azar(777, j)))
            val r = h * (0.012f + 0.014f * azar(780, j))
            val forma = Path()
            for (q in 0 until 7) {
                val ang = 6.283f * q / 7 + azar(781 + j, q)
                val v = Offset(o.x + r * (1.1f + 0.6f * azar(782 + j, q)) * cos(ang), o.y + r * 0.6f * sin(ang))
                if (q == 0) forma.moveTo(v.x, v.y) else forma.lineTo(v.x, v.y)
            }
            forma.close()
            drawPath(forma, a.luz(mezcla(P.ZOCALO, P.CAL, 0.5f), k(o), l(o)))
            drawPath(forma, a.sombra(0.45f), style = Stroke(0.9f * u))
        }
        // ---- el suelo de tierra apisonada
        val cs = Offset(w * 0.5f, h * 0.93f)
        drawRect(a.luz(P.TIERRA, k(cs), l(cs)), Offset(0f, suelo), Size(w, h - suelo))
        pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(P.TIERRA, sombraMueble(1f), 0.4f), k(cs)), 779, 14, u, 0.12f)
        drawPath(pz.guijarros, a.sombra(0.3f))
        drawPath(pz.guijarrosLuz, a.luz(mezcla(P.TIERRA, P.CAL, 0.4f), k(cs), l(cs)))
        drawRect(a.sombra(0.42f), Offset(0f, suelo), Size(w, 1.5f * u))
        drawRect(a.sombra(0.14f), Offset(0f, suelo + 1.5f * u), Size(w, 4f * u))
    }

    // ---------------------------------------------------------------- las tablillas

    /**
     * El políptico de tablillas, de pie sobre el banco y apoyado en el muro: las tablillas de detrás con sus cantos
     * escalonados (una por cada tanto de hojas); la de delante, con su cara encerada y el nombre y la cuenta incisos; la vuelta
     * de lana púrpura que la cruza por la esquina y el cordón de la izquierda con el estilo colgado. Con [ab] la tapa gira sobre
     * el cordón (el nombre se desvanece, no se deforma) y deja ver la cera de la de dentro.
     */
    private fun DrawScope.tablillas(e: EstadoLibreta, medidor: TextMeasurer, r: Rect, grueso: Float, ab: Float, a: AireLibretaHeleno,
                                    k: (Offset) -> Float, l: Float, caida: Offset) {
        val u = density
        val o = Offset(grueso * 0.62f, -grueso * 0.5f)
        val esquina = CornerRadius(2.6f * u)
        val kc = k(r.center); val kb = k(Offset(r.center.x, r.bottom))
        // Su sombra en el zócalo y en el banco.
        drawPath(Path().apply {
            moveTo(r.left + caida.x, r.top + caida.y); lineTo(r.right + o.x + caida.x, r.top + o.y + caida.y); lineTo(r.right + o.x + caida.x, r.bottom); lineTo(r.left, r.bottom); close()
        }, a.sombra(if (a.oscuro) 0.45f else 0.3f))
        drawOval(a.sombra(0.4f), Offset(r.left - r.width * 0.02f, r.bottom - 3f * u), Size(r.width * 1.1f + o.x, 6f * u))
        // Las tablillas de detrás, cada una un poco corrida: se ven sus cantos escalonados, más cuantas más hojas.
        val n = (3 + e.hojas / 5).coerceIn(3, 14)
        val cera = a.luz(P.CERA, kc, l)
        for (i in n - 1 downTo 1) {
            val f = i / (n - 1f)
            val off = o * f + Offset((azar(e.semilla, 700 + i) - 0.5f) * 1.8f * u, (azar(e.semilla, 750 + i) - 0.5f) * 1.4f * u)
            val col = mezcla(a.luz(if (i % 2 == 0) P.BOJ_OSC else mezcla(P.BOJ, P.BOJ_OSC, 0.3f), maxOf(kc, 0.3f), l), if (a.oscuro) P.NOCHE else sombraMueble(1f), 0.06f + 0.22f * f)
            drawRoundRect(col, r.topLeft + off, r.size, esquina)
            drawRoundRect(cera.copy(alpha = 0.7f), r.topLeft + off, r.size, esquina, style = Stroke(1f * u))
            drawLine(if (a.oscuro) a.filo(0.45f * kc) else a.claro(0.35f), r.topLeft + off + Offset(3f * u, 0.6f * u), Offset(r.right, r.top) + off + Offset(-3f * u, 0.6f * u), 0.7f * u)
        }
        // La de dentro (se ve al abrir): su cera, con renglones.
        if (ab > 0.01f) caraEncerada(e, r, a, kc, kb, l, viejo = 0.5f)
        // La tapa: gira sobre el cordón de la izquierda. Su cera, con el nombre y la cuenta incisos.
        withTransform({ scale(1f - 0.92f * ab, 1f, Offset(r.left, r.center.y)) }) {
            val campo = caraEncerada(e, r, a, kc, kb, l, viejo = 0.16f)
            val alfa = (1f - ab * 3f).coerceIn(0f, 1f)
            if (alfa > 0.01f) {
                val inciso = a.panel(P.INCISO, kc).copy(alpha = alfa)
                textoEn(medidor, e.nombre, Rect(campo.left + 6f * u, campo.top + campo.height * 0.1f, campo.right - 6f * u, campo.top + campo.height * 0.56f), LETRA, inciso, 17f, peso = FontWeight.Bold, minimo = 8f)
                drawLine(inciso.copy(alpha = 0.5f * alfa), Offset(campo.center.x - campo.width * 0.2f, campo.top + campo.height * 0.6f), Offset(campo.center.x + campo.width * 0.2f, campo.top + campo.height * 0.6f), 0.8f * u)
                textoEn(medidor, e.cuenta, Rect(campo.left + 4f * u, campo.top + campo.height * 0.64f, campo.right - 4f * u, campo.bottom - campo.height * 0.07f), LETRA, inciso.copy(alpha = 0.84f * alfa), 12f, minimo = 6.5f)
            }
            drawRoundRect(sombraMueble(0.5f * ab), r.topLeft, r.size, esquina)
        }
        // La vuelta de lana púrpura que las ata: rodea las tablillas y cruza por delante la esquina de arriba a la derecha, con
        // su nudo en el canto y los cabos colgando (al abrir, se suelta).
        if (ab < 0.05f) {
            val lana = a.luz(P.PURPURA, kc, l)
            val p0 = Offset(r.right - r.width * 0.2f, r.top - 1.5f * u)
            val p1 = Offset(r.right + 1f * u, r.top + r.height * 0.3f)
            drawLine(a.sombra(0.4f), p0 + Offset(1.6f * u, 2f * u), p1 + Offset(1.6f * u, 2f * u), 3.6f * u, StrokeCap.Round)
            drawLine(lana, p0, p1, 3.4f * u, StrokeCap.Round)
            drawLine(lana, p0, p0 + o, 3f * u, StrokeCap.Round)
            val v = p1 - p0; val largo = v.getDistance().coerceAtLeast(1f); val dir = v / largo; val nrm = Offset(-dir.y, dir.x)
            var t = 0f
            val vueltas = Path()
            while (t < largo) { val p = p0 + dir * t; vueltas.moveTo((p + nrm * (1.6f * u)).x, (p + nrm * (1.6f * u)).y); vueltas.lineTo((p - nrm * (1.6f * u) + dir * (1.6f * u)).x, (p - nrm * (1.6f * u) + dir * (1.6f * u)).y); t += 2.4f * u }
            drawPath(vueltas, a.sombra(0.3f), style = Stroke(0.6f * u))
            drawLine(if (a.oscuro) a.filo(0.4f * kc) else a.claro(0.35f), p0 - nrm * (1f * u), p1 - nrm * (1f * u), 0.7f * u)
            drawCircle(lana, 3.2f * u, p1)
            drawCircle(a.sombra(0.3f), 3.2f * u, p1 + Offset(0.8f * u, 0.8f * u), style = Stroke(0.8f * u))
            drawLine(lana, p1, p1 + Offset(-2f * u, r.height * 0.32f), 1.6f * u, StrokeCap.Round)
            drawLine(lana, p1, p1 + Offset(3.5f * u, r.height * 0.4f), 1.6f * u, StrokeCap.Round)
        }
        // De noche, la brasa sube por la parte baja del políptico.
        if (a.oscuro) drawRect(P.BRASA.copy(alpha = 0.1f * kb), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width * (1f - 0.92f * ab), r.height * 0.3f))
        // El cordón que las ata por la izquierda, con su nudo, sus cabos y el estilo colgado.
        val cordon = a.luz(mezcla(P.LANA, P.TIERRA, 0.25f), kc, l)
        val agujeros = listOf(r.top + r.height * 0.24f, r.top + r.height * 0.76f)
        agujeros.forEach { y ->
            val p = Offset(r.left + r.width * 0.04f, y)
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
        estilo(estiloArriba, r.height * 0.42f, a, kb, l)
    }

    /**
     * Una cara encerada: el marco de boj ancho, con su veta y sus esquinas gastadas, más alto que la cera (su canto echa sombra
     * dentro); la cera de color pardo verdoso, no negro, con el brillo rasante de la luz, las pasadas de la paleta que la
     * alisan y lo escrito antes a medio borrar ([viejo]: cuánto se ve; trazos, no letras). Devuelve dónde quedó la cera.
     */
    private fun DrawScope.caraEncerada(e: EstadoLibreta, r: Rect, a: AireLibretaHeleno, k: Float, kb: Float, l: Float, viejo: Float): Rect {
        val u = density
        val esquina = CornerRadius(2.6f * u)
        val boj = a.luz(P.BOJ, maxOf(k, 0.4f), l)
        drawRoundRect(boj, r.topLeft, r.size, esquina)
        val m = r.height * 0.13f
        val c = Rect(r.left + m * 1.15f, r.top + m, r.right - m, r.bottom - m)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // La veta del boj, fina, y un nudo; las esquinas, oscuras de tanto tocarlas.
            for (j in 0 until 7) drawLine(a.luz(P.BOJ_OSC, maxOf(k, 0.4f), l).copy(alpha = 0.5f), Offset(r.left, r.top + r.height * (0.03f + 0.16f * j)), Offset(r.right, r.top + r.height * (0.05f + 0.16f * j + 0.02f * azar(e.semilla, 830 + j))), 0.6f * u)
            drawOval(a.luz(P.BOJ_OSC, maxOf(k, 0.4f), l).copy(alpha = 0.55f), Offset(r.left + r.width * 0.78f, r.bottom - m * 0.75f), Size(m * 0.7f, m * 0.4f))
            for (p in listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight)) drawCircle(a.sombra(0.14f), r.height * 0.1f, p)
            if (a.oscuro) drawRect(P.BRASA.copy(alpha = 0.16f * kb), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
        }
        // El canto del marco: a la luz arriba y a la izquierda, en sombra abajo y a la derecha; de noche, la brasa en el de la derecha.
        drawRoundRect(a.claro(0.5f), r.topLeft, Size(r.width, 1.4f * u), esquina)
        drawRect(a.claro(0.3f), r.topLeft + Offset(0f, 2f * u), Size(1.2f * u, r.height - 4f * u))
        drawRect(a.sombra(0.35f), Offset(r.left + 2f * u, r.bottom - 1.6f * u), Size(r.width - 4f * u, 1.6f * u))
        drawRect(a.sombra(0.3f), Offset(r.right - 1.4f * u, r.top + 2f * u), Size(1.4f * u, r.height - 4f * u))
        if (a.oscuro) drawRect(a.filo(0.6f * kb), Offset(r.right - 1.4f * u, r.top + r.height * 0.4f), Size(1.4f * u, r.height * 0.6f - 2f * u))
        // La cera.
        drawRect(a.luz(CERA_VERDE, k, l), c.topLeft, c.size)
        // El brillo rasante: la luz roza la cera de lado y la aclara en una franja (de día, el sol de la izquierda; de noche, la brasa abajo).
        if (a.oscuro) drawRect(P.BRASA.copy(alpha = 0.14f * kb), Offset(c.left, c.bottom - c.height * 0.3f), Size(c.width, c.height * 0.22f))
        else drawPath(Path().apply { moveTo(c.left, c.top + c.height * 0.15f); lineTo(c.left + c.width * 0.35f, c.top); lineTo(c.left + c.width * 0.6f, c.top); lineTo(c.left, c.top + c.height * 0.7f); close() }, luzMueble(0.07f))
        // Las pasadas de la paleta, más lisas y más claras, y lo escrito antes a medio borrar.
        val traza = a.panel(P.INCISO, k)
        for (j in 0 until 3) drawRoundRect(traza.copy(alpha = 0.05f), Offset(c.left + c.width * (0.04f + 0.12f * j), c.top + c.height * (0.08f + 0.3f * j)), Size(c.width * (0.55f + 0.1f * azar(e.semilla, 880 + j)), c.height * 0.16f), CornerRadius(c.height * 0.08f))
        val trazos = Path()
        // Lo de antes: trazos quebrados del estilo (subidas y bajadas, como de letra rayada a prisa, sin ser letras), en
        // renglones que no son del todo rectos y que la paleta cortó a trozos.
        for (j in 0 until 7) {
            val y = c.top + c.height * (0.1f + 0.125f * j + 0.03f * (azar(e.semilla, 890 + j) - 0.5f))
            var x = c.left + c.width * (0.05f + 0.06f * azar(e.semilla, 900 + j)); var q = 0
            val fin = c.right - c.width * (0.08f + 0.3f * azar(e.semilla, 910 + j))
            while (x < fin) {
                val largo = c.width * (0.04f + 0.08f * azar(e.semilla + j, q))
                val alto = c.height * 0.035f
                trazos.moveTo(x, y)
                var xx = x; var p = 0
                while (xx < x + largo) {
                    val paso = c.width * (0.008f + 0.012f * azar(e.semilla + j * 7 + q, p))
                    xx += paso
                    trazos.lineTo(xx, y + (if (p % 2 == 0) -alto else alto * 0.3f) * (0.4f + 0.6f * azar(e.semilla + q, p + 30)))
                    p++
                }
                x += largo + c.width * (0.03f + 0.06f * azar(e.semilla + j, q + 60)); q++
            }
        }
        drawPath(trazos, traza.copy(alpha = viejo), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        // El marco, más alto: su sombra dentro de la cera arriba y a la izquierda, y el canto de la cera a la luz abajo.
        drawRect(a.sombra(0.65f), c.topLeft, Size(c.width, 3f * u))
        drawRect(a.sombra(0.3f), c.topLeft + Offset(0f, 3f * u), Size(c.width, 2f * u))
        drawRect(a.sombra(0.45f), c.topLeft, Size(2.6f * u, c.height))
        drawRect(a.claro(0.35f), Offset(c.left, c.bottom - 1.2f * u), Size(c.width, 1.2f * u))
        drawRect(a.claro(0.2f), Offset(c.right - 1f * u, c.top), Size(1f * u, c.height))
        return c
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
