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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las tres clases de entrada del estante de Espacio ([MuebleEstanteEspacio]), según la semilla del día: la bitácora de tela
 * con su goma, el cartucho de datos de aluminio con sus topes de goma y la bolsa sellada de lámina. Las tres dicen el día y
 * el mes en su etiqueta, llevan el color del ánimo bien a la vista (la tela, los topes de goma, la lámina), engordan con lo
 * escrito y cambian la estrella por la luna en el estante de los Sueños. Todas quedan sujetas por el cabo elástico de su
 * repisa, que cada volumen dibuja por delante de sí.
 */
internal object VolumenesEstanteEspacio {
    private val P = PaletaEstanteEspacio
    private val M = MuebleEstanteEspacio

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val a = AireEstanteEspacio(oscuro, v.noche)
        val fx = MuebleEstanteEspacio.LADO + v.centro * (1f - 2 * MuebleEstanteEspacio.LADO)
        val l = if (a.lumbre) M.cercaCuerpo(v.repisa + 0.55f, v.repisas, fx, v.noche) else LuzEstanteEspacio(1f, 0f)
        // De dónde viene la luz que manda: de día, de la izquierda (el ojo de buey); de noche, de la lámpara (a la derecha);
        // en los Sueños, del piloto de guardia del costado izquierdo.
        val hacia = if (!a.lumbre || v.noche) -1f else if (M.LAMP_X > fx) 1f else -1f
        when (M.clase(v.semilla)) {
            0 -> bitacora(v, medidor, a, l, hacia)
            1 -> cartucho(v, medidor, a, l, hacia)
            else -> bolsa(v, medidor, a, l, hacia)
        }
        // El tramo del cabo elástico que pasa por delante, con su sombra en el volumen.
        val u = density
        val y = size.height - M.CABO * u
        drawRect(a.sombra(0.3f), Offset(0f, y + 1.4f * u), Size(size.width, 2.4f * u))
        caboTramoEspacio(0f, size.width, y, a, l)
    }

    // ---------------------------------------------------------------- piezas comunes

    /** La luz en un cuerpo de lados rectos: de día, el claro a la izquierda y la sombra a la derecha; de noche, el filo del lado de la luz. */
    private fun DrawScope.luzDeCuerpo(r: Rect, a: AireEstanteEspacio, l: LuzEstanteEspacio, hacia: Float, fuerza: Float = 1f) {
        val u = density
        val an = r.width
        if (!a.lumbre) {
            drawRect(a.claro(0.24f * fuerza), Offset(r.left + an * 0.05f, r.top), Size(an * 0.1f, r.height))
            drawRect(a.sombra(0.2f * fuerza), Offset(r.right - an * 0.2f, r.top), Size(an * 0.2f, r.height))
            drawRect(a.sombra(0.3f * fuerza), Offset(r.right - 1f * u, r.top), Size(1f * u, r.height))
            return
        }
        if (hacia > 0f) {
            drawRect(a.sombra(0.22f * fuerza), Offset(r.left, r.top), Size(an * 0.22f, r.height))
            if (l.k > 0.06f) drawRect(a.filo(0.55f * l.k), Offset(r.right - 1.2f * u, r.top + 1f * u), Size(1.2f * u, r.height - 1f * u))
        } else {
            drawRect(a.sombra(0.22f * fuerza), Offset(r.right - an * 0.22f, r.top), Size(an * 0.22f, r.height))
            if (l.k > 0.06f) drawRect(a.filo(0.55f * l.k), Offset(r.left, r.top + 1f * u), Size(1.2f * u, r.height - 1f * u))
        }
        if (l.f > 0.05f) drawRect(a.filoFrio(0.6f * l.f), Offset(r.left, r.top + 1f * u), Size(0.9f * u, r.height * 0.6f))
        if (l.k > 0.06f) drawRect(a.filo(0.45f * l.k), Offset(r.left + 1f * u, r.top), Size(an - 2f * u, 1f * u))
    }

    /**
     * La marca de la entrada, estampada en crudo sobre su cuerpo ([fondo]): una estrella de cuatro puntas largas y finas, la
     * de delante; en los Sueños, una luna creciente.
     */
    private fun DrawScope.marca(c: Offset, rs: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, luna: Boolean, fondo: Color) {
        val u = density
        val tinta = a.etiqueta(l)
        if (luna) {
            drawCircle(a.sombra(0.3f), rs * 0.86f, c + Offset(0.5f * u, 0.6f * u))
            drawCircle(tinta, rs * 0.86f, c)
            drawCircle(fondo, rs * 0.74f, Offset(c.x + rs * 0.36f, c.y - rs * 0.24f))
        } else {
            val k = rs; val e = rs * 0.15f
            val estrella = poligonoEspacio(c.x, c.y - k, c.x + e, c.y - e, c.x + k * 0.8f, c.y, c.x + e, c.y + e, c.x, c.y + k, c.x - e, c.y + e, c.x - k * 0.8f, c.y, c.x - e, c.y - e)
            drawPath(corridoEspacio(estrella, 0.5f * u, 0.6f * u), a.sombra(0.35f))
            drawPath(estrella, tinta)
        }
    }

    /** El día grande y el mes debajo, más chico, en tinta sobre [r]. */
    private fun DrawScope.fecha(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        textoEn(medidor, v.dia, Rect(r.left + 0.4f * u, r.top + r.height * 0.04f, r.right - 0.4f * u, r.top + r.height * 0.68f), M.LETRA, tinta, 15f, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes, Rect(r.left + 0.6f * u, r.top + r.height * 0.64f, r.right - 0.6f * u, r.bottom - 0.6f * u), M.LETRA, tinta.copy(alpha = 0.85f), 7.5f, minimo = 4.5f)
    }

    // ---------------------------------------------------------------- la bitácora

    /**
     * La bitácora de tela (la libreta de papel de a bordo, que es un lujo): las tapas de tela del color del ánimo con su
     * trama, la costura de los cantos, la etiqueta de cinta de tela con el día, la goma que la mantiene cerrada (sin peso
     * se abriría sola), la estrella o la luna y, según la semilla, el lápiz metido en la goma o la cinta de marcar que flota
     * por arriba. La cabeza deja ver las hojas: más gruesa cuanto más escribiste.
     */
    private fun DrawScope.bitacora(v: Volumen, medidor: TextMeasurer, a: AireEstanteEspacio, l: LuzEstanteEspacio, hacia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val variante = (s / 7) % 3
        val top = 7f * u
        val tela = v.tono?.let { a.animo(it, l) } ?: a.luz(mezcla(P.TELA_AZUL, P.GOMA, 0.35f), l)
        // La cinta de marcar, flotando fuera de la cabeza en una ese.
        if (variante != 0) {
            val x0 = w * (0.28f + 0.2f * azar(s, 3))
            val cinta = Path().apply { moveTo(x0, top + 2f * u); cubicTo(x0 - 4f * u, top - 2f * u, x0 + 5f * u, top - 3f * u, x0 + 2f * u, 0.5f * u) }
            drawPath(cinta, a.luz(if (variante == 1) P.ROJO else P.TRIGO, l), style = Stroke(2.2f * u, cap = StrokeCap.Butt))
        }
        // La cabeza: el canto de las hojas entre las dos tapas.
        drawRoundRect(a.luz(P.PAPEL, l), Offset(1f * u, top - 1.6f * u), Size(w - 2f * u, 3f * u), CornerRadius(1f * u))
        var hx = 2.4f * u
        while (hx < w - 2f * u) { drawLine(a.sombra(0.18f), Offset(hx, top - 1.2f * u), Offset(hx, top + 1f * u), 0.4f * u); hx += maxOf(1f, 2.2f * u) }
        // Las tapas de tela.
        val cuerpo = Rect(0f, top, w, h)
        drawRoundRect(tela, cuerpo.topLeft, cuerpo.size, CornerRadius(2f * u))
        // La trama de la tela: hilos finos, uno claro y uno oscuro.
        var tx = 1.5f * u
        var j = 0
        while (tx < w - 1f * u) {
            drawLine(if (j % 2 == 0) a.claro(0.08f) else a.sombra(0.07f), Offset(tx, top + 1f * u), Offset(tx, h - 1f * u), 0.5f * u)
            tx += maxOf(1f, 2.6f * u); j++
        }
        var ty = top + 3f * u
        while (ty < h - 2f * u) { drawLine(a.sombra(0.05f), Offset(1f * u, ty), Offset(w - 1f * u, ty), 0.5f * u); ty += maxOf(1f, 5f * u) }
        luzDeCuerpo(cuerpo, a, l, hacia)
        // La costura de los dos cantos.
        listOf(1.8f * u, w - 1.8f * u).forEach { x ->
            var y = top + 3f * u
            while (y < h - 3f * u) { drawLine(a.luz(mezcla(P.TELA, P.TRIGO, 0.2f), l).copy(alpha = 0.6f), Offset(x, y), Offset(x, y + 2f * u), 0.6f * u); y += maxOf(1f, 4.2f * u) }
        }
        // El refuerzo del pie, gastado.
        drawRect(mezcla(tela, a.hondo, 0.3f), Offset(0f, h - 5f * u), Size(w, 5f * u))
        drawLine(a.claro(0.2f), Offset(1f * u, h - 5f * u), Offset(w - 1f * u, h - 5f * u), 0.6f * u)
        // La etiqueta de cinta de tela con el día: los extremos cortados en pico.
        val e = Rect(w * 0.1f, top + (h - top) * 0.06f, w * 0.9f, top + (h - top) * 0.31f)
        val pico = minOf(2.4f * u, e.width * 0.1f)
        val eti = poligonoEspacio(e.left, e.top, e.right, e.top, e.right - pico, e.center.y, e.right, e.bottom, e.left, e.bottom, e.left + pico, e.center.y)
        drawPath(corridoEspacio(eti, 0.8f * u, 1f * u), a.sombra(0.35f))
        drawPath(eti, a.etiqueta(l))
        drawRect(a.claro(0.4f), Offset(e.left + pico, e.top), Size(e.width - 2 * pico, 0.7f * u))
        fecha(v, medidor, Rect(e.left + pico * 0.6f, e.top, e.right - pico * 0.6f, e.bottom), P.TINTA)
        // La goma que la cierra, un poco más ancha que el lomo donde aprieta.
        val gy = top + (h - top) * 0.42f
        drawRect(a.sombra(0.35f), Offset(0f, gy + 2.8f * u), Size(w, 1.4f * u))
        drawRoundRect(a.luz(if (variante == 2) P.NARANJA else P.GOMA, l), Offset(-0.4f * u, gy), Size(w + 0.8f * u, 3f * u), CornerRadius(1.2f * u))
        drawRect(a.claro(0.3f), Offset(0.5f * u, gy + 0.5f * u), Size(w - 1f * u, 0.6f * u))
        // El lápiz metido en la goma, junto al canto derecho.
        if (variante == 0) {
            val lx = w * 0.72f
            drawRect(a.sombra(0.3f), Offset(lx + 1.2f * u, gy - (h - top) * 0.3f + 1f * u), Size(2.8f * u, (h - top) * 0.36f))
            drawRect(a.luz(P.TRIGO, l), Offset(lx, gy - (h - top) * 0.3f), Size(2.8f * u, (h - top) * 0.36f))
            drawRect(a.claro(0.4f), Offset(lx, gy - (h - top) * 0.3f), Size(0.8f * u, (h - top) * 0.36f))
            drawPath(poligonoEspacio(lx, gy - (h - top) * 0.3f, lx + 2.8f * u, gy - (h - top) * 0.3f, lx + 1.4f * u, gy - (h - top) * 0.3f - 3.2f * u), a.luz(mezcla(P.TRIGO, P.PAPEL, 0.4f), l))
            drawRect(a.luz(P.ROJO, l), Offset(lx, gy + 3f * u), Size(2.8f * u, 2.4f * u))
        }
        // La estrella (la luna, en los Sueños), entre la goma y el cabo.
        val my = (gy + 3f * u + (h - M.CABO * u - 2f * u)) / 2
        marca(Offset(w * 0.5f, my), minOf(w * 0.24f, 5.5f * u), a, l, v.noche, tela)
    }

    // ---------------------------------------------------------------- el cartucho

    /** Un tope de goma achaflanado dentro de [r], con chaflanes de [ch]. */
    private fun tope(r: Rect, ch: Float) = poligonoEspacio(r.left + ch, r.top, r.right - ch, r.top, r.right, r.top + ch, r.right, r.bottom - ch,
        r.right - ch, r.bottom, r.left + ch, r.bottom, r.left, r.bottom - ch, r.left, r.top + ch)

    /**
     * El cartucho de datos: el cuerpo de aluminio anodizado del color del ánimo (aluminio pelado, si no se marcó), cepillado,
     * con el metal asomando en los cantos y en las rayas de uso; los topes de goma grafito, achaflanados y estriados, arriba y
     * abajo; la boca de agarre; la ventanilla hundida con la tarjeta del día; la ranura de la carga (la barra más larga cuanto
     * más escribiste) y su lazo naranja asomando por arriba. La estrella (o la luna) va estampada en el tope de abajo.
     */
    private fun DrawScope.cartucho(v: Volumen, medidor: TextMeasurer, a: AireEstanteEspacio, l: LuzEstanteEspacio, hacia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 5f * u
        val anod = v.tono?.let { mezcla(a.animo(it, l), a.luz(P.ACERO, l), 0.1f) } ?: a.luz(P.ACERO, l)
        val metal = a.luz(mezcla(P.ACERO, P.PAPEL, 0.3f), l)
        val goma = a.luz(mezcla(P.GOMA, P.ACERO, 0.1f), l)
        // El lazo de cinta naranja, por arriba.
        drawArc(a.luz(P.NARANJA, l), 180f, 180f, false, Offset(w * 0.26f, 0.6f * u), Size(w * 0.48f, top * 2.3f), style = Stroke(1.8f * u))
        // El cuerpo anodizado, cepillado.
        val capA = 10f * u; val capB = 9.5f * u
        val cuerpo = Rect(0.9f * u, top + capA - 2f * u, w - 0.9f * u, h - capB + 2f * u)
        drawRect(anod, cuerpo.topLeft, cuerpo.size)
        var bx = cuerpo.left + 1.2f * u
        var j = 0
        while (bx < cuerpo.right - 1f * u) {
            drawLine(if (azar(s, 40 + j) > 0.5f) a.claro(0.12f) else a.sombra(0.1f), Offset(bx, cuerpo.top), Offset(bx, cuerpo.bottom), 0.5f * u)
            bx += maxOf(1f, (1.8f + 1.6f * azar(s, 60 + j)) * u); j++
        }
        // El brillo del metal, una franja ancha y suave; el anodizado gastado en los cantos; las rayas.
        drawRect(a.claro(0.16f), Offset(w * 0.2f, cuerpo.top), Size(w * 0.16f, cuerpo.height))
        drawRect(a.claro(0.1f), Offset(w * 0.4f, cuerpo.top), Size(w * 0.05f, cuerpo.height))
        drawRect(metal.copy(alpha = 0.75f), Offset(cuerpo.left, cuerpo.top), Size(0.9f * u, cuerpo.height))
        drawRect(metal.copy(alpha = 0.45f), Offset(cuerpo.right - 0.9f * u, cuerpo.top), Size(0.9f * u, cuerpo.height))
        for (k in 0 until 3) {
            val y0 = cuerpo.top + cuerpo.height * (0.45f + 0.2f * azar(s, 70 + k))
            drawLine(metal, Offset(w * (0.1f + 0.2f * azar(s, 72 + k)), y0), Offset(w * (0.45f + 0.35f * azar(s, 75 + k)), y0 + 2.5f * u), 0.5f * u)
        }
        // Los topes de goma: achaflanados, con sus estrías; el de arriba con la boca de agarre.
        val tope1 = Rect(0f, top, w, top + capA)
        val tope2 = Rect(0f, h - capB, w, h)
        listOf(tope1, tope2).forEachIndexed { i, r ->
            val p = tope(r, 2.4f * u)
            drawPath(p, goma)
            for (k in 1 until 3) {
                val y = r.top + r.height * k / 3f
                drawLine(a.sombra(0.4f), Offset(r.left + 2f * u, y), Offset(r.right - 2f * u, y), 0.7f * u)
                drawLine(a.claro(0.14f), Offset(r.left + 2f * u, y + 0.8f * u), Offset(r.right - 2f * u, y + 0.8f * u), 0.5f * u)
            }
            drawLine(a.claro(0.3f), Offset(r.left + 2.6f * u, r.top + 0.6f * u), Offset(r.right - 2.6f * u, r.top + 0.6f * u), 0.8f * u)
            if (i == 0) {
                drawRoundRect(a.luz(P.TINTA, l), Offset(w * 0.27f, r.top + r.height * 0.28f), Size(w * 0.46f, r.height * 0.44f), CornerRadius(r.height * 0.22f))
                drawLine(a.claro(0.25f), Offset(w * 0.3f, r.top + r.height * 0.74f), Offset(w * 0.7f, r.top + r.height * 0.74f), 0.6f * u)
            }
        }
        luzDeCuerpo(Rect(0f, top, w, h), a, l, hacia)
        // La ventanilla hundida con su tarjeta: el marco del anodizado más hondo y la sombra del borde.
        val vn = Rect(w * 0.13f, cuerpo.top + 4f * u, w * 0.87f, cuerpo.top + 4f * u + (h - top) * 0.34f)
        drawRect(mezcla(anod, a.hondo, 0.35f), vn.topLeft - Offset(1.2f * u, 1.2f * u), Size(vn.width + 2.4f * u, vn.height + 2.4f * u))
        drawRect(a.etiqueta(l), vn.topLeft, vn.size)
        drawRect(a.sombra(0.3f), vn.topLeft, Size(vn.width, 1.4f * u))
        drawRect(a.sombra(0.2f), vn.topLeft, Size(1f * u, vn.height))
        drawLine(metal, Offset(vn.left - 1.2f * u, vn.bottom + 1.2f * u), Offset(vn.right + 1.2f * u, vn.bottom + 1.2f * u), 0.6f * u)
        fecha(v, medidor, Rect(vn.left, vn.top + 1f * u, vn.right, vn.bottom), P.TINTA)
        // La ranura de la carga: la barra crema, más larga con lo escrito.
        val rn = Rect(w * 0.18f, vn.bottom + 3.4f * u, w * 0.82f, vn.bottom + 6.6f * u)
        if (rn.bottom < h - M.CABO * u - 3f * u) {
            drawRoundRect(a.luz(P.TINTA, l), rn.topLeft, rn.size, CornerRadius(1.6f * u))
            drawRoundRect(a.etiqueta(l), Offset(rn.left + 0.8f * u, rn.top + 0.8f * u), Size((rn.width - 1.6f * u) * (0.15f + 0.85f * v.lleno), rn.height - 1.6f * u), CornerRadius(1f * u))
        }
        marca(Offset(w * 0.5f, tope2.center.y + 0.4f * u), minOf(w * 0.2f, 3.8f * u), a, l, v.noche, goma)
    }

    // ---------------------------------------------------------------- la bolsa sellada

    /**
     * La bolsa sellada de lámina (lo que se guarda al vacío para que no lo toque la humedad): la lámina metalizada del color
     * del ánimo (plateada, si no se marcó), arrugada en facetas con brillos finos de metal y pliegues hondos, el sellado de
     * arriba con su estría, el agujero de colgar y las muescas de rasgar, el cierre a presión, la pegatina con el día pegada un
     * poco torcida y una ventanilla por la que se ven las hojas dobladas (más cuanto más escribiste). Se abomba con lo que
     * lleva dentro, cada una a su manera.
     */
    private fun DrawScope.bolsa(v: Volumen, medidor: TextMeasurer, a: AireEstanteEspacio, l: LuzEstanteEspacio, hacia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 2.5f * u
        val sello = 7f * u
        val lamina = v.tono?.let { mezcla(a.animo(it, l), a.hondo, if (a.oscuro) 0.05f else 0.12f) } ?: a.luz(mezcla(P.ACERO, P.GOMA, 0.12f), l)
        val brillo = if (a.oscuro) a.filo(0.2f + 0.5f * l.k) else a.claro(0.55f)
        val bi = (0.2f + (0.6f + 1.2f * v.lleno) * azar(s, 5)) * u
        val bd = (0.2f + (0.6f + 1.2f * v.lleno) * (1f - azar(s, 5))) * u
        val forma = Path().apply {
            moveTo(1f * u, top)
            lineTo(w - 1f * u, top)
            lineTo(w - 1f * u, top + sello)
            cubicTo(w - 1f * u + bd, top + sello + (h - top) * 0.3f, w - 1f * u + bd, h - (h - top) * 0.25f, w - 2f * u, h - 1.5f * u)
            quadraticTo(w * 0.5f, h + 0.6f * u, 2f * u, h - 1.5f * u)
            cubicTo(1f * u - bi, h - (h - top) * 0.25f, 1f * u - bi, top + sello + (h - top) * 0.3f, 1f * u, top + sello)
            close()
        }
        drawPath(forma, lamina)
        clipPath(forma) {
            // Los pliegues hondos de la lámina y, al lado de cada uno, el brillo fino del metal que lo dobla.
            for (k in 0 until 3) {
                val y0 = top + sello + (h - top - sello) * (0.18f + 0.27f * k + 0.08f * azar(s, 10 + k))
                val d = (h - top) * (0.06f + 0.1f * azar(s, 20 + k)) * (if (k % 2 == 0) 1f else -1f)
                val g = (3f + 4f * azar(s, 30 + k)) * u
                drawPath(poligonoEspacio(0f, y0, w, y0 - d, w, y0 - d + g, 0f, y0 + g * 0.8f), a.sombra(0.2f))
                drawPath(poligonoEspacio(0f, y0 - 1.6f * u, w, y0 - d - 1.6f * u, w, y0 - d - 0.4f * u, 0f, y0 - 0.5f * u), brillo)
            }
            // El brillo largo del lado de la luz, quebrado.
            drawPath(poligonoEspacio(w * 0.14f, top + sello, w * 0.24f, top + sello, w * 0.2f, h * 0.55f, w * 0.26f, h, w * 0.16f, h, w * 0.1f, h * 0.55f), a.claro(if (a.oscuro) 0.08f else 0.2f))
            luzDeCuerpo(Rect(0f, top, w, h), a, l, hacia, 0.9f)
        }
        // El sellado: la franja estriada, el agujero de colgar y las dos muescas.
        val sr = Rect(1f * u, top, w - 1f * u, top + sello)
        drawRect(mezcla(lamina, a.hondo, 0.15f), sr.topLeft, sr.size)
        var sx = sr.left + 1f * u
        while (sx < sr.right) { drawLine(a.sombra(0.22f), Offset(sx, sr.top + 1f * u), Offset(sx, sr.bottom - 1f * u), 0.5f * u); sx += maxOf(1f, 2f * u) }
        drawRect(a.claro(0.4f), Offset(sr.left, sr.top), Size(sr.width, 0.7f * u))
        drawRoundRect(a.luz(mezcla(P.CASCO_OSC, P.TINTA, 0.55f), l), Offset(w * 0.5f - 3.5f * u, sr.top + 1.8f * u), Size(7f * u, 3f * u), CornerRadius(1.5f * u))
        listOf(1f * u to 1f, w - 1f * u to -1f).forEach { (x, sg) ->
            drawPath(poligonoEspacio(x, sr.bottom + 0.5f * u, x + sg * 2.2f * u, sr.bottom + 1.8f * u, x, sr.bottom + 3.2f * u), a.sombra(0.55f))
        }
        // El cierre a presión, justo debajo.
        val cy = sr.bottom + 4.4f * u
        drawLine(a.sombra(0.4f), Offset(2f * u, cy), Offset(w - 2f * u, cy), 1f * u)
        drawLine(brillo, Offset(2f * u, cy + 1f * u), Offset(w - 2f * u, cy + 1f * u), 0.6f * u)
        // La pegatina con el día, pegada a mano, un poco torcida.
        val pg = Rect(w * 0.12f, cy + 3.4f * u, w * 0.88f, cy + 3.4f * u + (h - top) * 0.23f)
        rotate(if (s % 2 == 0) -3f else 2.5f, pg.center) {
            drawRoundRect(a.sombra(0.32f), pg.topLeft + Offset(0.6f * u, 0.8f * u), pg.size, CornerRadius(1.6f * u))
            drawRoundRect(a.etiqueta(l), pg.topLeft, pg.size, CornerRadius(1.6f * u))
            drawRect(a.luz(mezcla(P.ROJO, P.NARANJA, 0.4f), l), Offset(pg.left, pg.top + 1.2f * u), Size(pg.width, 1.3f * u))
            fecha(v, medidor, Rect(pg.left, pg.top + 2.4f * u, pg.right, pg.bottom), P.TINTA)
        }
        // La ventanilla transparente con las hojas dobladas.
        val bajo = h - M.CABO * u - 3f * u
        val vt = Rect(w * 0.18f, pg.bottom + 3.4f * u, w * 0.82f, minOf(pg.bottom + 3.4f * u + (h - top) * 0.2f, bajo))
        if (vt.height > 6f * u) {
            // Por la ventanilla, las hojas dobladas: el papel de la de encima y, debajo, los cantos de las otras (más con más palabras).
            drawRect(mezcla(a.etiqueta(l), lamina, 0.2f), vt.topLeft, vt.size)
            val hojas = 1 + (v.lleno * 5f).toInt()
            for (k in 0 until hojas) {
                val yy = vt.bottom - 1.6f * u - k * 1.8f * u
                if (yy < vt.top + vt.height * 0.35f) break
                drawLine(a.sombra(0.32f), Offset(vt.left + 1f * u, yy), Offset(vt.right - 1f * u, yy - 0.5f * u), 0.6f * u)
            }
            // El doblez de la hoja de encima, en diagonal, y lo escrito en ella: renglones que no se leen.
            drawLine(a.sombra(0.22f), Offset(vt.left, vt.top + vt.height * 0.25f), Offset(vt.right, vt.top + vt.height * 0.05f), 0.8f * u)
            for (k in 0 until 2) drawLine(a.luz(P.TINTA, l).copy(alpha = 0.3f), Offset(vt.left + 2f * u, vt.top + vt.height * (0.36f + 0.16f * k)), Offset(vt.right - (3f + 4f * azar(s, 90 + k)) * u, vt.top + vt.height * (0.34f + 0.16f * k)), 0.6f * u)
            // El plástico de la ventanilla: su brillo y su marco soldado.
            drawPath(poligonoEspacio(vt.left, vt.bottom, vt.left + vt.width * 0.3f, vt.top, vt.left + vt.width * 0.45f, vt.top, vt.left + vt.width * 0.15f, vt.bottom), a.claro(if (a.oscuro) 0.06f else 0.22f))
            drawRect(mezcla(lamina, a.hondo, 0.2f), vt.topLeft, vt.size, style = Stroke(1.2f * u))
        }
        val libre = bajo - maxOf(vt.bottom, pg.bottom)
        if (libre > 9f * u) marca(Offset(w * 0.5f, maxOf(vt.bottom, pg.bottom) + libre / 2 + 1f * u), minOf(w * 0.22f, 4.6f * u), a, l, v.noche, lamina)
        else marca(Offset(w * 0.5f, h - (M.CABO - 2f) * u * 0.5f - 2f * u), minOf(w * 0.2f, 4f * u), a, l, v.noche, lamina)
        drawPath(forma, a.sombra(0.3f), style = Stroke(0.6f * u))
    }
}
