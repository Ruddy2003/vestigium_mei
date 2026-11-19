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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las tres clases de entrada del estante de Espacio ([MuebleEstanteEspacio]), según la semilla del día, cada una con su
 * silueta: la bitácora de tela, alta y delgada, con su goma; el cartucho de datos, bajo y ancho, con su asa y sus topes de
 * goma; y la bolsa sellada de lámina, blanda, con la esquina doblada y apretada por el cabo. El color del ánimo es el cuerpo
 * entero (la tela, el aluminio anodizado, la lámina); las tres dicen el día y el mes en una etiqueta chica, engordan con lo
 * escrito y cambian la estrella por la luna en el estante de los Sueños. Todas quedan sujetas por el cabo elástico de su
 * repisa, que cada volumen dibuja por delante de sí.
 */
internal object VolumenesEstanteEspacio {
    private val P = PaletaEstanteEspacio
    private val M = MuebleEstanteEspacio

    /** Los trazos que se rehacen en cada volumen (se dibuja de uno en uno, en el mismo hilo): así no se crea uno por volumen. */
    private val trazo = Path()
    private val trazo2 = Path()

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val a = AireEstanteEspacio(oscuro, v.noche)
        val fx = M.LADO + v.centro * (1f - 2 * M.LADO)
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

    /** La luz en un cuerpo: de día, el claro a la izquierda y la sombra a la derecha; de noche, el filo del lado de la luz. */
    private fun DrawScope.luzDeCuerpo(r: Rect, a: AireEstanteEspacio, l: LuzEstanteEspacio, hacia: Float, fuerza: Float = 1f) {
        val u = density
        val an = r.width
        if (!a.lumbre) {
            drawRect(a.claro(0.2f * fuerza), Offset(r.left + an * 0.06f, r.top), Size(an * 0.09f, r.height))
            drawRect(a.sombra(0.18f * fuerza), Offset(r.right - an * 0.2f, r.top), Size(an * 0.2f, r.height))
            drawRect(a.sombra(0.3f * fuerza), Offset(r.right - 1f * u, r.top), Size(1f * u, r.height))
            return
        }
        if (hacia > 0f) {
            drawRect(a.sombra(0.2f * fuerza), Offset(r.left, r.top), Size(an * 0.22f, r.height))
            if (l.k > 0.06f) drawRect(a.filo(0.55f * l.k), Offset(r.right - 1.2f * u, r.top + 1f * u), Size(1.2f * u, r.height - 1f * u))
        } else {
            drawRect(a.sombra(0.2f * fuerza), Offset(r.right - an * 0.22f, r.top), Size(an * 0.22f, r.height))
            if (l.k > 0.06f) drawRect(a.filo(0.6f * l.k), Offset(r.left, r.top + 1f * u), Size(1.2f * u, r.height - 1f * u))
        }
        // El filo frío del ojo de buey o del piloto de guardia: del lado izquierdo, también en las repisas de abajo.
        if (l.f > 0.04f) drawRect(a.filoFrio(0.75f * l.f + 0.1f), Offset(r.left, r.top + 1f * u), Size(1f * u, r.height - 2f * u))
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
            poligonoEnEspacio(trazo2, c.x, c.y - k, c.x + e, c.y - e, c.x + k * 0.8f, c.y, c.x + e, c.y + e, c.x, c.y + k, c.x - e, c.y + e, c.x - k * 0.8f, c.y, c.x - e, c.y - e)
            translate(0.5f * u, 0.6f * u) { drawPath(trazo2, a.sombra(0.35f)) }
            drawPath(trazo2, tinta)
        }
    }

    /** El día grande y el mes debajo, más chico, en tinta sobre [r]. */
    private fun DrawScope.fecha(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        textoEn(medidor, v.dia, Rect(r.left + 0.3f * u, r.top + r.height * 0.02f, r.right - 0.3f * u, r.top + r.height * 0.68f), M.LETRA, tinta, 14.5f, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes, Rect(r.left + 0.5f * u, r.top + r.height * 0.64f, r.right - 0.5f * u, r.bottom - 0.4f * u), M.LETRA, tinta.copy(alpha = 0.85f), 7f, minimo = 4.5f)
    }

    // ---------------------------------------------------------------- la bitácora: alta y delgada

    /**
     * La bitácora de tela (la libreta de papel de a bordo, que es un lujo): alta y delgada, más angosta que su sitio. Las tapas
     * de tela del color del ánimo con su trama, la costura de los cantos, la cabeza con el canto de las hojas, una etiqueta
     * chica de cinta de tela con el día, la goma que la mantiene cerrada (sin peso se abriría sola), la estrella o la luna y,
     * según la semilla, el lápiz metido en la goma o la cinta de marcar que flota por arriba.
     */
    private fun DrawScope.bitacora(v: Volumen, medidor: TextMeasurer, a: AireEstanteEspacio, l: LuzEstanteEspacio, hacia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val variante = (s / 7) % 3
        val top = 7f * u
        val m = minOf(3.2f * u, w * 0.12f)
        val cuerpo = Rect(m, top, w - m, h)
        val tela = v.tono?.let { a.animo(it, l) } ?: a.luz(mezcla(P.TELA_AZUL, P.GOMA, 0.3f), l)
        // La cinta de marcar, flotando fuera de la cabeza en una ese.
        if (variante != 0) {
            val x0 = cuerpo.left + cuerpo.width * (0.3f + 0.3f * azar(s, 3))
            trazo.reset()
            trazo.moveTo(x0, top + 2f * u); trazo.cubicTo(x0 - 5f * u, top - 2f * u, x0 + 6f * u, top - 4f * u, x0 + 3f * u, 0.6f * u)
            drawPath(trazo, a.luz(if (variante == 1) P.ROJO else P.TRIGO, l), style = Stroke(2.2f * u, cap = StrokeCap.Butt))
        }
        // La cabeza: el canto de las hojas entre las dos tapas.
        drawRoundRect(a.luz(P.PAPEL, l), Offset(cuerpo.left + 1f * u, top - 1.6f * u), Size(cuerpo.width - 2f * u, 3f * u), CornerRadius(1f * u))
        // Las tapas de tela, con la sombra que dejan en el fondo.
        drawRoundRect(a.sombra(0.35f), cuerpo.topLeft + Offset(1.6f * u, 1f * u), cuerpo.size, CornerRadius(2f * u))
        drawRoundRect(tela, cuerpo.topLeft, cuerpo.size, CornerRadius(2f * u))
        var tx = cuerpo.left + 1.5f * u
        var j = 0
        while (tx < cuerpo.right - 1f * u) {
            drawLine(if (j % 2 == 0) a.claro(0.07f) else a.sombra(0.06f), Offset(tx, top + 1f * u), Offset(tx, h - 1f * u), 0.5f * u)
            tx += maxOf(1f, 2.6f * u); j++
        }
        var ty = top + 3f * u
        while (ty < h - 2f * u) { drawLine(a.sombra(0.05f), Offset(cuerpo.left + 1f * u, ty), Offset(cuerpo.right - 1f * u, ty), 0.5f * u); ty += maxOf(1f, 5f * u) }
        luzDeCuerpo(cuerpo, a, l, hacia)
        // La costura de los dos cantos.
        listOf(cuerpo.left + 1.8f * u, cuerpo.right - 1.8f * u).forEach { x ->
            var y = top + 3f * u
            while (y < h - 3f * u) { drawLine(a.luz(mezcla(P.TELA, P.TRIGO, 0.2f), l).copy(alpha = 0.55f), Offset(x, y), Offset(x, y + 2f * u), 0.6f * u); y += maxOf(1f, 4.2f * u) }
        }
        // Las esquinas de abajo, gastadas.
        drawRect(mezcla(tela, a.hondo, 0.25f), Offset(cuerpo.left, h - 4f * u), Size(cuerpo.width, 4f * u))
        drawLine(a.claro(0.2f), Offset(cuerpo.left + 1f * u, h - 4f * u), Offset(cuerpo.right - 1f * u, h - 4f * u), 0.6f * u)
        // La etiqueta de cinta de tela con el día: chica, con los extremos cortados en pico.
        val e = Rect(cuerpo.left + cuerpo.width * 0.06f, top + 4f * u, cuerpo.right - cuerpo.width * 0.06f, top + 4f * u + minOf(22f * u, (h - top) * 0.26f))
        val pico = minOf(2.2f * u, e.width * 0.1f)
        poligonoEnEspacio(trazo, e.left, e.top, e.right, e.top, e.right - pico, e.center.y, e.right, e.bottom, e.left, e.bottom, e.left + pico, e.center.y)
        // Con un ánimo muy pálido, la tela casi es del color de la etiqueta: la etiqueta lleva su filete y la goma va naranja.
        val palida = tela.luminance() > 0.55f
        translate(0.8f * u, 1f * u) { drawPath(trazo, a.sombra(if (palida) 0.5f else 0.35f)) }
        drawPath(trazo, a.etiqueta(l))
        drawPath(trazo, mezcla(tela, P.TINTA, 0.55f).copy(alpha = if (palida) 0.8f else 0.45f), style = Stroke(0.8f * u))
        drawRect(a.claro(0.4f), Offset(e.left + pico, e.top), Size(e.width - 2 * pico, 0.7f * u))
        fecha(v, medidor, Rect(e.left + pico * 0.5f, e.top, e.right - pico * 0.5f, e.bottom), P.TINTA)
        // La goma que la cierra, un poco más ancha que el lomo donde aprieta.
        val gy = top + (h - top) * 0.47f
        drawRect(a.sombra(0.35f), Offset(cuerpo.left, gy + 2.8f * u), Size(cuerpo.width, 1.4f * u))
        drawRoundRect(a.luz(if (variante == 2 || palida) P.NARANJA else P.GOMA, l), Offset(cuerpo.left - 0.5f * u, gy), Size(cuerpo.width + 1f * u, 3f * u), CornerRadius(1.2f * u))
        drawRect(a.claro(0.3f), Offset(cuerpo.left + 0.5f * u, gy + 0.5f * u), Size(cuerpo.width - 1f * u, 0.6f * u))
        // El lápiz metido en la goma, junto al canto derecho.
        if (variante == 0) {
            val lx = cuerpo.right - cuerpo.width * 0.32f
            val lal = (gy - e.bottom - 3f * u).coerceAtLeast(4f * u)
            drawRect(a.sombra(0.3f), Offset(lx + 1.2f * u, gy - lal + 1f * u), Size(2.8f * u, lal + 6f * u))
            drawRect(a.luz(P.TRIGO, l), Offset(lx, gy - lal), Size(2.8f * u, lal + 6f * u))
            drawRect(a.claro(0.4f), Offset(lx, gy - lal), Size(0.8f * u, lal + 6f * u))
            poligonoEnEspacio(trazo, lx, gy - lal, lx + 2.8f * u, gy - lal, lx + 1.4f * u, gy - lal - 3.2f * u)
            drawPath(trazo, a.luz(mezcla(P.TRIGO, P.PAPEL, 0.4f), l))
            drawRect(a.luz(P.ROJO, l), Offset(lx, gy + 6f * u), Size(2.8f * u, 2.4f * u))
        }
        // La estrella (la luna, en los Sueños), entre la goma y el cabo.
        val my = (gy + 3f * u + (h - M.CABO * u - 2f * u)) / 2
        marca(Offset(w * 0.5f, my), minOf(cuerpo.width * 0.3f, 5.5f * u), a, l, v.noche, tela)
    }

    // ---------------------------------------------------------------- el cartucho: bajo y ancho, con su asa

    /** Un tope de goma achaflanado dentro de [r], con chaflanes de [ch], en [trazo]. */
    private fun tope(r: Rect, ch: Float) = poligonoEnEspacio(trazo, r.left + ch, r.top, r.right - ch, r.top, r.right, r.top + ch, r.right, r.bottom - ch,
        r.right - ch, r.bottom, r.left + ch, r.bottom, r.left, r.bottom - ch, r.left, r.top + ch)

    /**
     * El cartucho de datos: bajo y ancho, con el asa de goma arriba para sacarlo. El cuerpo de aluminio anodizado del color
     * del ánimo, entero (aluminio pelado, si no se marcó), cepillado, con el metal asomando en los cantos; los topes de goma
     * grafito, achaflanados y más anchos que el cuerpo; la ventanilla hundida con la tarjeta del día y la ranura de la carga
     * (la barra más larga cuanto más escribiste). La estrella (o la luna) va estampada en el tope de abajo.
     */
    private fun DrawScope.cartucho(v: Volumen, medidor: TextMeasurer, a: AireEstanteEspacio, l: LuzEstanteEspacio, hacia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 9f * u
        val anod = v.tono?.let { a.animo(it, l) } ?: a.luz(P.ACERO, l)
        val metal = a.luz(mezcla(P.ACERO, P.PAPEL, 0.35f), l)
        val goma = a.luz(mezcla(P.GOMA, P.ACERO, 0.1f), l)
        // El asa de goma, arriba.
        val asa = Rect(w * 0.2f, 1.2f * u, w * 0.8f, top * 2f + 1f * u)
        drawArc(a.sombra(0.3f), 180f, 180f, false, asa.topLeft + Offset(1.2f * u, 1f * u), asa.size, style = Stroke(3.4f * u))
        drawArc(goma, 180f, 180f, false, asa.topLeft, asa.size, style = Stroke(3.4f * u))
        drawArc(a.claro(0.3f), 200f, 70f, false, asa.topLeft, asa.size, style = Stroke(0.8f * u))
        // El cuerpo anodizado, entero del color del ánimo, cepillado.
        val cuerpo = Rect(1.8f * u, top + 6f * u, w - 1.8f * u, h - 7f * u)
        drawRect(a.sombra(0.35f), cuerpo.topLeft + Offset(1.6f * u, 1f * u), cuerpo.size)
        drawRect(anod, cuerpo.topLeft, cuerpo.size)
        var bx = cuerpo.left + 1.2f * u
        var j = 0
        while (bx < cuerpo.right - 1f * u) {
            drawLine(if (azar(s, 40 + j) > 0.5f) a.claro(0.08f) else a.sombra(0.07f), Offset(bx, cuerpo.top), Offset(bx, cuerpo.bottom), 0.5f * u)
            bx += maxOf(1f, (1.8f + 1.6f * azar(s, 60 + j)) * u); j++
        }
        drawRect(a.claro(0.12f), Offset(cuerpo.left + cuerpo.width * 0.16f, cuerpo.top), Size(cuerpo.width * 0.1f, cuerpo.height))
        drawRect(metal.copy(alpha = 0.7f), Offset(cuerpo.left, cuerpo.top), Size(0.8f * u, cuerpo.height))
        drawRect(metal.copy(alpha = 0.4f), Offset(cuerpo.right - 0.8f * u, cuerpo.top), Size(0.8f * u, cuerpo.height))
        val ry = cuerpo.top + cuerpo.height * (0.62f + 0.2f * azar(s, 70))
        drawLine(metal, Offset(cuerpo.left + cuerpo.width * 0.12f, ry), Offset(cuerpo.left + cuerpo.width * 0.6f, ry + 2.5f * u), 0.5f * u)
        luzDeCuerpo(cuerpo, a, l, hacia)
        // Los topes de goma, achaflanados, con sus estrías.
        listOf(Rect(0f, top, w, top + 8f * u), Rect(0f, h - 9f * u, w, h)).forEachIndexed { i, r ->
            translate(1.2f * u, 1f * u) { drawPath(tope(r, 2.4f * u), a.sombra(0.3f)) }
            drawPath(tope(r, 2.4f * u), goma)
            for (k in 1 until 3) {
                val y = r.top + r.height * k / 3f
                drawLine(a.sombra(0.4f), Offset(r.left + 2.4f * u, y), Offset(r.right - 2.4f * u, y), 0.7f * u)
                drawLine(a.claro(0.12f), Offset(r.left + 2.4f * u, y + 0.8f * u), Offset(r.right - 2.4f * u, y + 0.8f * u), 0.5f * u)
            }
            drawLine(a.claro(0.3f), Offset(r.left + 2.6f * u, r.top + 0.6f * u), Offset(r.right - 2.6f * u, r.top + 0.6f * u), 0.8f * u)
            if (a.lumbre && i == 0 && l.k > 0.06f) drawLine(a.filo(0.5f * l.k), Offset(r.left + 2.4f * u, r.top + 0.4f * u), Offset(r.right - 2.4f * u, r.top + 0.4f * u), 0.8f * u)
        }
        // La ventanilla hundida con su tarjeta: chica, para que mande el color.
        val vn = Rect(cuerpo.left + cuerpo.width * 0.1f, cuerpo.top + 3.5f * u, cuerpo.right - cuerpo.width * 0.1f, cuerpo.top + 3.5f * u + minOf(20f * u, cuerpo.height * 0.48f))
        drawRect(mezcla(anod, a.hondo, 0.35f), vn.topLeft - Offset(1.2f * u, 1.2f * u), Size(vn.width + 2.4f * u, vn.height + 2.4f * u))
        drawRect(a.etiqueta(l), vn.topLeft, vn.size)
        drawRect(a.sombra(0.3f), vn.topLeft, Size(vn.width, 1.4f * u))
        drawRect(a.sombra(0.2f), vn.topLeft, Size(1f * u, vn.height))
        fecha(v, medidor, Rect(vn.left, vn.top + 1f * u, vn.right, vn.bottom), P.TINTA)
        // La ranura de la carga: la barra crema, más larga con lo escrito.
        val rn = Rect(vn.left + 1f * u, vn.bottom + 3f * u, vn.right - 1f * u, vn.bottom + 6.2f * u)
        if (rn.bottom < h - M.CABO * u - 3f * u) {
            drawRoundRect(a.luz(P.TINTA, l), rn.topLeft, rn.size, CornerRadius(1.6f * u))
            drawRoundRect(a.etiqueta(l), Offset(rn.left + 0.8f * u, rn.top + 0.8f * u), Size((rn.width - 1.6f * u) * (0.15f + 0.85f * v.lleno), rn.height - 1.6f * u), CornerRadius(1f * u))
        }
        marca(Offset(w * 0.5f, h - 4.5f * u), minOf(w * 0.18f, 3.8f * u), a, l, v.noche, goma)
    }

    // ---------------------------------------------------------------- la bolsa sellada: blanda

    /**
     * La bolsa sellada de lámina (lo que se guarda al vacío para que no lo toque la humedad): blanda, de contorno irregular.
     * La lámina metalizada del color del ánimo, entera (plateada, si no se marcó), con sus pliegues; el sellado estriado de
     * arriba con el agujero de colgar y la esquina doblada, que enseña el plateado de dentro; el cierre a presión; la
     * pegatina con el día, un poco torcida; y el cabo, que la aprieta y le hace cintura (se abomba arriba y abajo, más cuanto
     * más escribiste).
     */
    private fun DrawScope.bolsa(v: Volumen, medidor: TextMeasurer, a: AireEstanteEspacio, l: LuzEstanteEspacio, hacia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 3f * u
        val yc = h - M.CABO * u
        val lamina = v.tono?.let { a.animo(it, l) } ?: a.luz(mezcla(P.ACERO, P.PAPEL, 0.2f), l)
        val brillo = if (a.oscuro) a.filo(0.15f + 0.45f * l.k) else a.claro(0.45f)
        val b1 = (0.4f + 1.1f * v.lleno) * u
        val dobl = minOf(w * 0.32f, 9f * u) * (0.7f + 0.5f * azar(s, 6))
        // La silueta: arriba recta hasta la esquina doblada; los lados abombados, con cintura donde aprieta el cabo.
        val forma = trazo.apply {
            reset()
            moveTo(1f * u, top)
            lineTo(w - 1f * u - dobl, top)
            lineTo(w - 1f * u, top + dobl)
            cubicTo(w - 1f * u + b1, top + dobl + (yc - top) * 0.3f, w - 0.5f * u + b1, yc - 9f * u, w - 3f * u, yc)
            cubicTo(w - 0.5f * u + b1 * 0.6f, yc + 6f * u, w - 0.8f * u, h - 3f * u, w - 4f * u, h - 0.6f * u)
            quadraticTo(w * 0.5f, h + 0.8f * u, 4f * u, h - 0.6f * u)
            cubicTo(0.8f * u, h - 3f * u, 0.5f * u - b1 * 0.6f, yc + 6f * u, 3f * u, yc)
            cubicTo(0.5f * u - b1, yc - 9f * u, 1f * u - b1, top + 10f * u, 1f * u, top + 7f * u)
            close()
        }
        translate(1.6f * u, 1.2f * u) { drawPath(forma, a.sombra(0.35f)) }
        drawPath(forma, lamina)
        clipPath(forma) {
            // Los pliegues de la lámina: hondos y suaves, con un brillo fino de metal en el borde de cada uno.
            for (k in 0 until 3) {
                val y0 = top + 8f * u + (yc - top - 8f * u) * (0.2f + 0.3f * k + 0.1f * azar(s, 10 + k))
                val d = (h - top) * (0.05f + 0.08f * azar(s, 20 + k)) * (if (k % 2 == 0) 1f else -1f)
                val g = (3f + 3f * azar(s, 30 + k)) * u
                poligonoEnEspacio(trazo2, 0f, y0, w, y0 - d, w, y0 - d + g, 0f, y0 + g * 0.8f)
                drawPath(trazo2, a.sombra(0.14f))
                drawLine(brillo, Offset(0f, y0 - 0.6f * u), Offset(w, y0 - d - 0.6f * u), 0.8f * u)
            }
            // El abombado de abajo del cabo, con su luz.
            drawOval(a.claro(if (a.oscuro) 0.05f else 0.14f), Offset(w * 0.12f, yc + 3f * u), Size(w * 0.3f, (h - yc) * 0.5f))
            // La cintura: la sombra que deja el cabo al apretar.
            drawRect(a.sombra(0.22f), Offset(0f, yc - 4.5f * u), Size(w, 2.5f * u))
            luzDeCuerpo(Rect(0f, top, w, h), a, l, hacia, 0.85f)
        }
        // El sellado: la franja estriada y el agujero de colgar.
        val sr = Rect(1f * u, top, w - 1f * u - dobl * 0.6f, top + 6.5f * u)
        drawRect(mezcla(lamina, a.hondo, 0.16f), sr.topLeft, sr.size)
        var sx = sr.left + 1f * u
        while (sx < sr.right) { drawLine(a.sombra(0.22f), Offset(sx, sr.top + 1f * u), Offset(sx, sr.bottom - 1f * u), 0.5f * u); sx += maxOf(1f, 2f * u) }
        drawRect(brillo, Offset(sr.left, sr.top), Size(sr.width, 0.7f * u))
        drawRoundRect(a.luz(mezcla(P.CASCO_OSC, P.TINTA, 0.55f), l), Offset(w * 0.42f - 3f * u, sr.top + 1.8f * u), Size(6f * u, 2.8f * u), CornerRadius(1.4f * u))
        // La esquina doblada hacia delante: enseña el plateado de dentro, con su sombra en la lámina.
        poligonoEnEspacio(trazo2, w - 1f * u - dobl, top, w - 1f * u, top + dobl, w - 1f * u - dobl * 1.05f, top + dobl * 1.1f)
        translate(-0.8f * u, 1.4f * u) { drawPath(trazo2, a.sombra(0.4f)) }
        drawPath(trazo2, a.luz(mezcla(P.ACERO, P.PAPEL, 0.45f), l))
        drawLine(brillo, Offset(w - 1f * u - dobl, top), Offset(w - 1f * u, top + dobl), 0.8f * u)
        // El cierre a presión, justo debajo.
        val cy = top + 9f * u
        drawLine(a.sombra(0.4f), Offset(2f * u, cy), Offset(w - 2f * u, cy), 1f * u)
        drawLine(brillo, Offset(2f * u, cy + 1f * u), Offset(w - 2f * u, cy + 1f * u), 0.6f * u)
        // La pegatina con el día, chica y pegada a mano, un poco torcida.
        val pg = Rect(w * 0.14f, cy + 3.4f * u, w * 0.86f, cy + 3.4f * u + minOf(21f * u, (yc - cy) * 0.5f))
        rotate(if (s % 2 == 0) -3.5f else 3f, pg.center) {
            drawRoundRect(a.sombra(0.32f), pg.topLeft + Offset(0.6f * u, 0.8f * u), pg.size, CornerRadius(1.6f * u))
            drawRoundRect(a.etiqueta(l), pg.topLeft, pg.size, CornerRadius(1.6f * u))
            drawRoundRect(mezcla(lamina, P.TINTA, 0.55f).copy(alpha = if (lamina.luminance() > 0.55f) 0.8f else 0.4f), pg.topLeft, pg.size, CornerRadius(1.6f * u), style = Stroke(0.8f * u))
            fecha(v, medidor, Rect(pg.left, pg.top + 0.6f * u, pg.right, pg.bottom), P.TINTA)
        }
        // La estrella (o la luna), entre la pegatina y el cabo, o abajo, en el abombado.
        val libre = yc - 4f * u - pg.bottom
        if (libre > 11f * u) marca(Offset(w * 0.5f, pg.bottom + libre / 2), minOf(w * 0.22f, 4.6f * u), a, l, v.noche, lamina)
        else marca(Offset(w * 0.5f, (yc + h) / 2 + 1f * u), minOf(w * 0.2f, 4.2f * u), a, l, v.noche, lamina)
        drawPath(forma, a.sombra(0.32f), style = Stroke(0.6f * u))
    }
}
