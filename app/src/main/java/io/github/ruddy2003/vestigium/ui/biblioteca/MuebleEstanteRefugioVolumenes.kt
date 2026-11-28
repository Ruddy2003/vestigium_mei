package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRefugio as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * Las entradas del estante de Refugio, tres objetos de la vida del refugio (la clase sale de la semilla, nunca del sitio):
 * - **la carpeta de vinilo**: alta y de tapa plana, del vinilo del color del ánimo (coral, mantequilla o verde azulado), con las
 *   hojas y las pestañas de colores asomando por arriba, el fuelle a un lado (más ancho cuanto más escribiste), la ventanilla con la
 *   tarjeta del día y la correa con su broche cromado;
 * - **el tarro de conserva del huerto hidropónico**: el vidrio de hombros redondos con las piezas del color del ánimo en su caldo
 *   (más lleno cuanto más escribiste), la goma naranja, la tapa de rosca esmaltada y la etiqueta pegada con cinta;
 * - **la bolsa de papel del economato**: blanda, del papel del color del ánimo, con su panza, sus arrugas, la boca doblada cerrada con
 *   una pinza de madera y la tarjeta grapada.
 * El día, en la letra de terminal de los rótulos del Ideario a unos 16 sp, y el mes corto, en una tarjeta crema tostada de valor
 * medio; en los Sueños, una media luna de pintura fosforescente.
 */

/** Los meses cortos en español, para la tarjeta. */
private val MESES_CORTOS_REFUGIO = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")
private val MESES_LARGOS_REFUGIO = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")

/** El mes corto de una entrada: por su nombre o por su número; si no se reconoce, tal cual. */
private fun mesCortoRefugio(mes: String): String {
    val n = mes.filter { it.isDigit() }.toIntOrNull()
    if (n != null && n in 1..12) return MESES_CORTOS_REFUGIO[n - 1]
    val i = MESES_LARGOS_REFUGIO.indexOfFirst { mes.trim().lowercase().startsWith(it) }
    return if (i >= 0) MESES_CORTOS_REFUGIO[i] else mes
}

internal fun DrawScope.volumenRefugio(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val m = MuebleEstanteRefugio
    val a = AireRefugio(oscuro, v.noche)
    val fila = m.FILA / m.ANCHO_NOMINAL
    val x = m.LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * m.LADO)
    val y = m.CORONA + v.repisa * fila + (m.ALTO_REPISA.value / m.ANCHO_NOMINAL) * 0.55f
    val total = m.CORONA + v.repisas * fila + m.BASE
    val l = m.luz(a, x, y, total, v.repisas, 0.06f)
    val hacia = m.hacia(a, x, y, total, v.repisas)
    val tono = tonoDeDiaRefugio(v.tono, oscuro)
    when (m.clase(v.semilla)) {
        0 -> carpetaRefugio(v, tono, medidor, l, hacia)
        1 -> tarroRefugio(v, tono, medidor, l, hacia)
        else -> bolsaRefugio(v, tono, medidor, l, hacia)
    }
}

/**
 * La sombra que la entrada echa detrás (en el fondo y en la vecina), lejos de la luz: un rectángulo redondeado del tamaño de [r]
 * con esquinas de [radio] (pinta más ligero que un camino con la silueta, y detrás no se distingue).
 */
private fun DrawScope.sombraDetrasRefugio(r: Rect, radio: Float, l: LuzRefugio, hacia: Float) {
    val u = density
    drawRoundRect(l.s(if (l.a.oscuro) 0.5f else 0.42f), Offset(r.left - hacia * 5f * u, r.top + (if (l.a.oscuro) -1.5f * u else 2.4f * u)), r.size, CornerRadius(radio))
}

/** La sombra de contacto en el labio de la tabla. */
private fun DrawScope.contactoRefugio(x0: Float, x1: Float, l: LuzRefugio, hacia: Float) {
    val u = density; val h = size.height
    drawOval(l.s(0.55f), Offset(x0 - hacia * 2f * u, h - 3f * u), Size(x1 - x0 + 3f * u, 5f * u))
}

/** La caja de la tarjeta: tan ancha como cabe el día (unos 16 sp) sin pasar del ancho del objeto [an], con su arriba en [y]. */
private fun DrawScope.rectTarjetaRefugio(x0: Float, an: Float, y: Float, alto: Float = 29f): Rect {
    val u = density
    val ancho = minOf(an * 0.9f, 28f * u).coerceAtLeast(minOf(an * 0.98f, 21f * u))
    val cx = x0 + an / 2
    return Rect(cx - ancho / 2, y, cx + ancho / 2, y + alto * u)
}

/**
 * La tarjeta del día (crema tostada de valor medio, teñida por la luz que le toca): el filete verde azulado de las etiquetas del
 * Ideario arriba, el día en la letra de terminal (unos 16 sp) y el mes corto debajo.
 */
private fun DrawScope.tarjetaRefugio(v: Volumen, medidor: TextMeasurer, l: LuzRefugio, r: Rect) {
    val u = density
    val carton = l.tarjeta()
    drawRect(carton, r.topLeft, r.size)
    drawRect(mezcla(carton, P.TEAL, 0.6f), Offset(r.left, r.top + 1.4f * u), Size(r.width, 1.1f * u))
    drawRect(mezcla(carton, P.CREMA_OSC, 0.5f).copy(alpha = 0.35f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
    val tinta = l.tinta(carton)
    val zonaDia = Rect(r.left - 0.6f * u, r.top + 2.4f * u, r.right + 0.6f * u, r.top + 19.6f * u)
    textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 17f, minimo = 10f)
    val zonaMes = Rect(r.left + 0.4f * u, zonaDia.bottom - 2.2f * u, r.right - 0.4f * u, r.bottom - 0.4f * u)
    textoEn(medidor, mesCortoRefugio(v.mes), zonaMes, P.LETRA, tinta.copy(alpha = 0.85f), 11f, minimo = 7f)
    if (l.k > 0.08f) drawRect(l.filo(0.45f), r.topLeft, r.size, style = Stroke(0.9f * u))
}

/**
 * La media luna de los Sueños: una pegatina de pintura fosforescente recortada en creciente (no un círculo entero, que se leería
 * como un ojo), verde pálida, que de noche brilla un poco.
 */
private fun DrawScope.mediaLunaRefugio(c: Offset, r: Float, l: LuzRefugio) {
    val u = density
    val luna = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val mordida = Path().apply { addOval(Rect(c.x - r * 0.3f, c.y - r * 1.25f, c.x + r * 1.7f, c.y + r * 0.75f)) }
    val creciente = Path().apply { op(luna, mordida, PathOperation.Difference) }
    if (l.a.oscuro) drawCircle(P.FOSFORESCENTE.copy(alpha = 0.16f), r * 1.7f, c)
    drawPath(corridoRefugio(creciente, 0.8f * u, 1f * u), l.s(0.4f))
    val color = if (l.a.oscuro) mezcla(P.FOSFORESCENTE, P.FOSFORO, 0.15f) else mezcla(l.c(P.FOSFORESCENTE), P.FOSFORESCENTE, 0.5f)
    drawPath(creciente, color)
    drawPath(creciente, mezcla(color, P.TEAL, 0.4f), style = Stroke(0.6f * u))
}

// ---------------------------------------------------------------- la carpeta de vinilo

/** El vinilo de cuando no hay ánimo: el transparente ahumado, sin teñir. */
private val SIN_ANIMO_VINILO_REFUGIO = Color(0xFFA6AFA8)

/**
 * La carpeta de vinilo, de pie: el cuerpo de tapa plana y esquinas vivas, del vinilo del color del ánimo, con el cordón soldado del
 * borde y el fuelle a un lado (más ancho cuanto más escribiste); por arriba asoman las hojas y dos o tres pestañas de colores; la
 * ventanilla de vinilo transparente con la tarjeta del día, y la correa que la cierra por abajo con su broche cromado.
 */
private fun DrawScope.carpetaRefugio(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzRefugio, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val giro = if (azar(s, 6) < 0.22f) (if (azar(s, 7) < 0.5f) -3f else 3f) * (0.6f + 0.4f * azar(s, 8)) else 0f
    val an = w * (if (giro != 0f) 0.86f else 0.89f + 0.08f * azar(s, 3))
    val x0 = (w - an) * (if (giro > 0f) 0.1f else if (giro < 0f) 0.9f else 0.5f)
    val asoma = h * (0.07f + 0.04f * v.lleno)
    val top = asoma + h * 0.01f
    val viniloBase = tono?.let { materialDelAnimoRefugio(it, P.VINILOS) } ?: SIN_ANIMO_VINILO_REFUGIO
    val vinilo = if (tono != null) l.animo(viniloBase) else l.c(viniloBase)
    val radio = 1.4f * u
    rotate(giro, Offset(if (giro > 0f) x0 + an else x0, h)) {
        val cuerpo = Path().apply { addRoundRect(RoundRect(Rect(x0, top, x0 + an, h - 0.5f * u), CornerRadius(radio))) }
        sombraDetrasRefugio(Rect(x0, top - asoma * 0.8f, x0 + an, h - 0.5f * u), radio, l, hacia)
        contactoRefugio(x0, x0 + an, l, hacia)
        // Las hojas que asoman por arriba (papel crema, una más corta) y las pestañas de colores.
        val hojas = listOf(mezcla(P.ETIQUETA, Color.White, 0.2f), P.ETIQUETA, mezcla(P.ETIQUETA, P.OCRE, 0.2f))
        for (j in 0 until 3) {
            val hx0 = x0 + an * (0.1f + 0.04f * j); val hx1 = x0 + an * (0.88f - 0.05f * j)
            val ht = top - asoma * (0.55f + 0.35f * azar(s, 70 + j))
            drawRect(l.c(hojas[j]), Offset(hx0, ht), Size(hx1 - hx0, top - ht + 2f))
            drawLine(l.b(0.6f), Offset(hx0, ht), Offset(hx1, ht), 0.8f * u)
            drawLine(l.s(0.3f), Offset(hx1, ht), Offset(hx1, top), 0.8f * u)
        }
        val pestanas = listOf(P.TEAL, P.OCRE, P.CORAL)
        val np = 2 + (azar(s, 75) * 2f).toInt().coerceAtMost(1)
        for (j in 0 until np) {
            val px = x0 + an * (0.14f + 0.3f * j + 0.08f * azar(s, 76 + j))
            val pw = an * 0.2f
            val pt = top - asoma * (0.95f + 0.05f * azar(s, 79 + j))
            val tab = Path().apply { addRoundRect(RoundRect(Rect(px, pt, px + pw, top), CornerRadius(1.2f * u, 1.2f * u))) }
            drawPath(tab, l.c(pestanas[(j + s.and(3)) % 3]))
            drawLine(l.b(0.5f), Offset(px + 0.8f * u, pt + 0.8f * u), Offset(px + pw - 0.8f * u, pt + 0.8f * u), 0.7f * u)
        }
        planosRefugio(cuerpo, vinilo, l, hacia, 0.16f, 0.26f)
        val fuelle = (2.2f + 3.8f * v.lleno) * u
        // Recorte de rectángulo (la esquina de la carpeta es de 1.4 dp: no se nota y pinta más ligero que un camino).
        clipRect(x0, top, x0 + an, h - 0.5f * u) {
            // El fuelle del lado de la sombra: el vinilo doblado, más hondo, con su pliegue.
            val fx = if (hacia < 0f) x0 + an - fuelle else x0
            drawRect(mezcla(vinilo, l.a.hondo, if (l.a.oscuro) 0.4f else 0.32f), Offset(fx, top), Size(fuelle, h))
            drawLine(l.b(0.25f), Offset(fx + fuelle * 0.5f, top), Offset(fx + fuelle * 0.5f, h), 0.6f * u)
            // El canto de arriba de la tapa, a la luz, y el cordón soldado del borde.
            drawRect(l.b(0.5f), Offset(x0, top), Size(an, 1.1f * u))
            val dentro = Rect(x0 + 2.2f * u, top + 2.2f * u, x0 + an - 2.2f * u, h - 2.6f * u)
            val cordon = Path().apply { addRoundRect(RoundRect(dentro, CornerRadius(1f * u))) }
            drawPath(cordon, l.s(0.32f), style = Stroke(0.9f * u))
            drawPath(corridoRefugio(cordon, -0.8f * u, -0.8f * u), l.b(0.35f), style = Stroke(0.6f * u))
            // El vinilo estirado: rozaduras claras y un pliegue de uso.
            for (j in 0 until 2) {
                val yy = top + (h - top) * (0.52f + 0.2f * azar(s, 30 + j))
                drawLine(l.b(0.22f), Offset(x0 + an * 0.2f, yy), Offset(x0 + an * (0.45f + 0.3f * azar(s, 33 + j)), yy - 1f * u), 0.7f * u)
            }
        }
        // La ventanilla de vinilo transparente, arriba, con la tarjeta del día detrás y su brillo en diagonal.
        val eti = rectTarjetaRefugio(x0, an, top + 4.4f * u + h * 0.04f * azar(s, 9))
        val marco = eti.inflate(1.8f * u)
        drawRoundRect(mezcla(vinilo, l.a.hondo, 0.3f), marco.topLeft, marco.size, CornerRadius(1.6f * u))
        tarjetaRefugio(v, medidor, l, eti)
        drawPath(poligonoRefugio(Offset(eti.left, eti.top + eti.height * 0.15f), Offset(eti.left + eti.width * 0.3f, eti.top), Offset(eti.left + eti.width * 0.5f, eti.top), Offset(eti.left, eti.top + eti.height * 0.5f)),
            Color.White.copy(alpha = if (l.a.oscuro) 0.06f else 0.2f))
        drawRoundRect(l.s(0.35f), marco.topLeft, marco.size, CornerRadius(1.6f * u), style = Stroke(1f * u))
        drawLine(l.b(0.4f), Offset(marco.left + 1f * u, marco.bottom + 0.6f * u), Offset(marco.right - 1f * u, marco.bottom + 0.6f * u), 0.7f * u)
        // La correa: una banda del mismo vinilo, más honda, que abraza la carpeta abajo, con su broche cromado.
        val yc = h - (h - top) * (0.25f + 0.06f * azar(s, 10))
        val ca = minOf(7f * u, (h - top) * 0.09f)
        val correa = Rect(x0 - 0.6f * u, yc, x0 + an + 0.6f * u, yc + ca)
        drawRect(l.s(0.35f), correa.topLeft + Offset(0f, 1.4f * u), correa.size)
        drawRect(mezcla(vinilo, l.a.hondo, if (l.a.oscuro) 0.25f else 0.22f), correa.topLeft, correa.size)
        drawLine(l.b(0.45f), Offset(correa.left, correa.top + 0.6f * u), Offset(correa.right, correa.top + 0.6f * u), 0.7f * u)
        val pes = Path()
        var xp = correa.left + 2f * u; var jp = 0
        while (xp < correa.right - 2f * u && jp < 30) { pes.addRect(Rect(xp, correa.top + ca * 0.2f, xp + 1.2f * u, correa.top + ca * 0.2f + 0.6f * u)); pes.addRect(Rect(xp, correa.bottom - ca * 0.2f - 0.6f * u, xp + 1.2f * u, correa.bottom - ca * 0.2f)); xp += 2.6f * u; jp++ }
        drawPath(pes, l.b(0.3f))
        botonCromoRefugio(Offset(x0 + an * (if (hacia < 0f) 0.3f else 0.7f), correa.center.y), minOf(an * 0.11f, ca * 0.62f), l)
        if (l.k > 0.06f) drawPath(bordeRefugio(cuerpo, -hacia * 1.4f * u, 0f), l.filo(0.85f))
        if (v.noche) mediaLunaRefugio(Offset(x0 + an * 0.5f, marco.bottom + (correa.top - marco.bottom) * 0.5f), minOf(an * 0.22f, 5.6f * u, (correa.top - marco.bottom) * 0.36f), l)
    }
}

// ---------------------------------------------------------------- el tarro de conserva

/** Las piezas de cuando no hay ánimo: el caldo claro de una conserva sin color, con sus piezas pálidas. */
private val SIN_ANIMO_CONSERVA_REFUGIO = Color(0xFFCFC6A2)

/**
 * El tarro de conserva del huerto hidropónico: el vidrio de hombros redondos con las piezas del color del ánimo apretadas en su
 * caldo (más lleno cuanto más escribiste) y lo vacío de arriba, claro; la goma naranja, la tapa de rosca esmaltada con su estriado
 * y la etiqueta de papel pegada a media altura.
 */
private fun DrawScope.tarroRefugio(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzRefugio, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val an = minOf(w * (0.82f + 0.1f * azar(s, 22)), 38f * u).coerceAtLeast(minOf(w * 0.92f, 24f * u))
    val x0 = (w - an) * (0.3f + 0.4f * azar(s, 23))
    val cx = x0 + an / 2
    val piezaBase = tono?.let { materialDelAnimoRefugio(it, P.CONSERVAS) } ?: SIN_ANIMO_CONSERVA_REFUGIO
    val pieza = if (tono != null) l.animo(piezaBase) else l.c(piezaBase)
    val tapaAl = h * 0.085f
    val cuelloAl = h * 0.03f
    val cuello = an * 0.84f
    val hombro = tapaAl + cuelloAl + h * 0.07f
    val vidrio = Path().apply {
        moveTo(cx - cuello / 2, tapaAl - 1f * u)
        lineTo(cx - cuello / 2, tapaAl + cuelloAl)
        cubicTo(cx - cuello / 2, tapaAl + cuelloAl + h * 0.04f, x0, tapaAl + cuelloAl + h * 0.03f, x0, hombro)
        lineTo(x0, h - an * 0.12f)
        quadraticTo(x0, h - 0.5f * u, x0 + an * 0.12f, h - 0.5f * u)
        lineTo(x0 + an * 0.88f, h - 0.5f * u)
        quadraticTo(x0 + an, h - 0.5f * u, x0 + an, h - an * 0.12f)
        lineTo(x0 + an, hombro)
        cubicTo(x0 + an, tapaAl + cuelloAl + h * 0.03f, cx + cuello / 2, tapaAl + cuelloAl + h * 0.04f, cx + cuello / 2, tapaAl + cuelloAl)
        lineTo(cx + cuello / 2, tapaAl - 1f * u)
        close()
    }
    val tapa = Rect(cx - cuello * 0.56f, h * 0.012f, cx + cuello * 0.56f, tapaAl + 0.5f * u)
    sombraDetrasRefugio(Rect(x0, h * 0.04f, x0 + an, h - 0.5f * u), an * 0.2f, l, hacia)
    contactoRefugio(x0, x0 + an, l, hacia)
    // Lo vacío de arriba: el vidrio claro, con el fondo oliva que se adivina detrás.
    drawPath(vidrio, l.c(mezcla(P.AGUA_VIDRIO, P.OLIVA, 0.55f)).copy(alpha = 0.75f))
    // Lo de dentro va bajo los hombros, donde el tarro ya es recto: basta un recorte de rectángulo (pinta más ligero que el camino).
    clipRect(x0 + 0.6f * u, hombro, x0 + an - 0.6f * u, h - an * 0.05f) {
        // El caldo, del color de las piezas pero más hondo, hasta su nivel.
        val nivel = hombro + (h - hombro) * (0.3f - 0.28f * v.lleno)
        drawRect(mezcla(pieza, l.a.hondo, if (l.a.oscuro) 0.35f else 0.3f), Offset(x0, nivel), Size(an, h - nivel))
        drawLine(mezcla(pieza, Color.White, 0.35f).copy(alpha = if (l.a.oscuro) 0.35f else 0.7f), Offset(x0, nivel), Offset(x0 + an, nivel), 1.1f * u)
        // Las piezas apretadas: en unos tarros, largas y de pie (las judías, los espárragos), cada una con su luz y su sombra; en
        // otros, redondas (los tomates, los albaricoques), en hileras trabadas.
        // Todas las piezas en tres caminos (el tono, la sombra y el brillo): un tarro no cuesta más que una carpeta.
        val cuerpos = Path(); val hondos = Path(); val brillos = Path()
        if (azar(s, 25) < 0.38f) {
            val pw = an * 0.15f
            var x = x0 + pw * 0.3f; var j = 0
            while (x < x0 + an && j < 12) {
                val tope = nivel + (h - nivel) * (0.06f + 0.12f * azar(s, 40 + j))
                cuerpos.addRoundRect(RoundRect(Rect(x, tope, x + pw, h - 2f * u), CornerRadius(pw * 0.5f)))
                hondos.addRect(Rect(if (hacia < 0f) x + pw * 0.62f else x, tope + pw * 0.4f, (if (hacia < 0f) x + pw * 0.62f else x) + pw * 0.38f, h - pw * 0.6f))
                brillos.addRect(Rect(if (hacia < 0f) x + pw * 0.16f else x + pw * 0.66f, tope + pw * 0.5f, (if (hacia < 0f) x + pw * 0.16f else x + pw * 0.66f) + pw * 0.16f, tope + pw * 0.5f + (h - tope) * 0.7f))
                x += pw * 1.08f; j++
            }
        } else {
            val r = an * (0.2f + 0.03f * azar(s, 24))
            var fila = 0
            var y = h - r * 0.9f
            while (y > nivel + r * 0.3f && fila < 12) {
                var x = x0 + r * (if (fila % 2 == 0) 0.75f else 1.6f)
                var j = 0
                while (x < x0 + an + r && j < 8) {
                    val c = Offset(x + r * 0.15f * (azar(s, 30 + fila * 7 + j) - 0.5f), y)
                    val rr = r * (0.88f + 0.18f * azar(s, 60 + fila * 7 + j))
                    cuerpos.addOval(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr))
                    val cs = c + Offset(-hacia * rr * 0.28f, rr * 0.25f)
                    hondos.addOval(Rect(cs.x - rr * 0.62f, cs.y - rr * 0.62f, cs.x + rr * 0.62f, cs.y + rr * 0.62f))
                    val cb = c + Offset(hacia * rr * 0.35f, -rr * 0.38f)
                    brillos.addOval(Rect(cb.x - rr * 0.24f, cb.y - rr * 0.24f, cb.x + rr * 0.24f, cb.y + rr * 0.24f))
                    x += r * 1.7f; j++
                }
                y -= r * 1.45f; fila++
            }
        }
        drawPath(cuerpos, pieza)
        drawPath(cuerpos, mezcla(pieza, l.a.hondo, 0.4f), style = Stroke(0.7f * u))
        drawPath(hondos, mezcla(pieza, l.a.hondo, 0.3f))
        drawPath(brillos, l.b(0.55f))
        // El vidrio: el hondo del lado de la sombra, el brillo largo del de la luz y el fondo grueso.
        val sx = if (hacia < 0f) x0 + an * 0.84f else x0
        drawRect(l.s(0.3f), Offset(sx, hombro - h * 0.05f), Size(an * 0.16f, h))
        val bx = if (hacia < 0f) x0 + an * 0.12f else x0 + an * 0.78f
        drawRect(l.b(0.85f), Offset(bx, hombro), Size(an * 0.08f, (h - hombro) * 0.78f))
        drawRect(l.b(0.35f), Offset(bx - hacia * an * 0.13f, hombro + h * 0.04f), Size(an * 0.03f, (h - hombro) * 0.55f))
        drawRect(mezcla(pieza, l.a.hondo, 0.45f), Offset(x0, h - 3f * u), Size(an, 3f * u))
    }
    drawPath(vidrio, l.c(mezcla(P.AGUA_HONDA, P.OLIVA_OSC, 0.4f)).copy(alpha = 0.75f), style = Stroke(1f * u))
    drawPath(bordeRefugio(vidrio, -hacia * 1.6f * u, 0f), l.b(0.5f))
    if (l.k > 0.06f) drawPath(bordeRefugio(vidrio, -hacia * 1.4f * u, 0f), l.filo(0.85f))
    // La goma naranja bajo la tapa y la tapa de rosca esmaltada, con su estriado y el filo de arriba.
    drawRect(l.c(P.NARANJA), Offset(cx - cuello * 0.52f, tapa.bottom - 1f * u), Size(cuello * 1.04f, 2f * u))
    val esmalte = when (Math.floorMod(s, 3)) { 0 -> P.AGUA; 1 -> P.VINILO; else -> P.CORAL }
    val tapaP = Path().apply { addRoundRect(RoundRect(tapa, CornerRadius(1.6f * u))) }
    drawPath(corridoRefugio(tapaP, -hacia * 1f * u, 1.4f * u), l.s(0.4f))
    cilindroRefugio(tapaP, l.c(esmalte), l, hacia, 0.7f)
    val estrias = Path()
    var xe = tapa.left + 1.4f * u; var je = 0
    while (xe < tapa.right - 1f * u && je < 30) { estrias.addRect(Rect(xe, tapa.top + tapa.height * 0.3f, xe + 0.6f * u, tapa.bottom - 1.4f * u)); xe += 1.9f * u; je++ }
    drawPath(estrias, l.s(0.3f))
    drawLine(l.b(0.8f), Offset(tapa.left + 1.4f * u, tapa.top + 0.8f * u), Offset(tapa.right - 1.4f * u, tapa.top + 0.8f * u), 0.9f * u)
    // La etiqueta de papel pegada a media altura, con su tira de cinta, un poco torcida.
    val eti = rectTarjetaRefugio(x0, an, hombro + (h - hombro) * (0.14f + 0.14f * azar(s, 28)))
    rotate((azar(s, 29) - 0.5f) * 5f, eti.center) {
        drawRect(l.s(0.3f), eti.topLeft + Offset(-hacia * 0.8f * u, 1f * u), eti.size)
        tarjetaRefugio(v, medidor, l, eti)
        drawRect(l.c(mezcla(P.ETIQUETA, P.OCRE, 0.3f)).copy(alpha = 0.55f), Offset(eti.center.x - eti.width * 0.3f, eti.top - 2.2f * u), Size(eti.width * 0.6f, 4f * u))
    }
    if (v.noche) mediaLunaRefugio(Offset(cx, eti.bottom + (h - eti.bottom) * 0.48f), minOf(an * 0.2f, 5.4f * u), l)
}

// ---------------------------------------------------------------- la bolsa de papel

/** El papel de cuando no hay ánimo: el de estraza, sin teñir. */
private val SIN_ANIMO_PAPEL_REFUGIO = Color(0xFFB48C5C)

/**
 * La bolsa de papel del economato, blanda: el papel del color del ánimo con la panza a los lados (más llena cuanto más escribiste),
 * arrugas que cogen la luz, el fuelle de abajo, la boca doblada dos veces y cerrada con una pinza de madera, y la tarjeta grapada.
 */
private fun DrawScope.bolsaRefugio(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzRefugio, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val giro = if (azar(s, 60) < 0.3f) (if (azar(s, 61) < 0.5f) -4.5f else 4.5f) else 0f
    val an = w * (if (giro != 0f) 0.82f else 0.86f + 0.08f * azar(s, 62))
    val x0 = (w - an) * (if (giro > 0f) 0.1f else if (giro < 0f) 0.9f else 0.5f)
    val cx = x0 + an / 2
    val papelBase = tono?.let { materialDelAnimoRefugio(it, P.PAPELES) } ?: SIN_ANIMO_PAPEL_REFUGIO
    val papel = if (tono != null) l.animo(papelBase) else l.c(papelBase)
    val panza = an * (0.03f + 0.06f * v.lleno)
    val pinzaAl = h * 0.12f
    val boca = pinzaAl * 0.75f
    val doblez = h * 0.11f
    val caido = h * 0.025f * (azar(s, 63) - 0.5f)
    rotate(giro, Offset(if (giro > 0f) x0 + an else x0, h)) {
        // La bolsa: el borde de arriba arrugado (la boca doblada), los costados con panza y el pie ancho.
        val puntos = ArrayList<Offset>()
        val n = 5
        for (j in 0..n) puntos.add(Offset(x0 + an * j / n, boca + caido * (j - n / 2f) / n + h * 0.012f * (azar(s, 100 + j) - 0.5f)))
        puntos.add(Offset(x0 + an + panza * 0.5f, boca + (h - boca) * 0.3f))
        puntos.add(Offset(x0 + an + panza, boca + (h - boca) * 0.72f))
        puntos.add(Offset(x0 + an * 0.96f, h - 0.5f * u))
        puntos.add(Offset(x0 + an * 0.04f, h - 0.5f * u))
        puntos.add(Offset(x0 - panza, boca + (h - boca) * 0.7f))
        puntos.add(Offset(x0 - panza * 0.5f, boca + (h - boca) * 0.3f))
        val forma = poligonoRefugio(puntos)
        sombraDetrasRefugio(Rect(x0 - panza * 0.6f, boca, x0 + an + panza * 0.6f, h - 0.5f * u), an * 0.1f, l, hacia)
        contactoRefugio(x0 - panza * 0.5f, x0 + an + panza * 0.5f, l, hacia)
        planosRefugio(forma, papel, l, hacia, 0.22f, 0.28f)
        clipPath(forma) {
            // Las arrugas: pliegues largos de arriba abajo, cada uno con su lado de luz y su lado de sombra.
            val sombras = Path(); val luces = Path()
            for (j in 0 until 4) {
                val xa = x0 + an * (0.15f + 0.22f * j + 0.08f * azar(s, 110 + j))
                val xb = xa + an * (azar(s, 115 + j) - 0.5f) * 0.25f
                val ya = boca + doblez + h * 0.02f
                val yb = boca + (h - boca) * (0.55f + 0.35f * azar(s, 120 + j))
                sombras.moveTo(xa, ya); sombras.lineTo(xb, yb); sombras.lineTo(xb + 1.4f * u, yb); sombras.lineTo(xa + 1.4f * u, ya); sombras.close()
                luces.moveTo(xa - 1f * u, ya); luces.lineTo(xb - 1f * u, yb); luces.lineTo(xb - 0.2f * u, yb); luces.lineTo(xa - 0.2f * u, ya); luces.close()
            }
            drawPath(sombras, l.s(0.25f))
            drawPath(luces, l.b(0.28f))
            // El fuelle del pie: la raya del doblez y las dos esquinas en diagonal.
            val yf = h - (h - boca) * 0.12f
            drawLine(l.s(0.3f), Offset(x0 - panza, yf), Offset(x0 + an + panza, yf), 0.9f * u)
            drawLine(l.s(0.22f), Offset(x0, yf), Offset(x0 + an * 0.12f, h), 0.8f * u)
            drawLine(l.s(0.22f), Offset(x0 + an, yf), Offset(x0 + an * 0.88f, h), 0.8f * u)
            // La boca doblada dos veces: dos bandas, la de arriba a la luz, con su sombra debajo.
            drawRect(mezcla(papel, l.a.hondo, 0.12f), Offset(x0 - panza, boca), Size(an + 2 * panza, doblez))
            drawRect(l.b(0.35f), Offset(x0 - panza, boca), Size(an + 2 * panza, doblez * 0.42f))
            drawRect(l.s(0.3f), Offset(x0 - panza, boca + doblez * 0.48f), Size(an + 2 * panza, 1f * u))
            drawRect(l.s(0.4f), Offset(x0 - panza, boca + doblez), Size(an + 2 * panza, 1.6f * u))
            pinceladas(Rect(x0, boca, x0 + an, h), if (l.a.oscuro) l.a.hondo else mezcla(papel, Color.Black, 0.3f), s, 5, u, 0.14f)
        }
        drawPath(bordeRefugio(forma, 0f, 1.2f * u), l.b(0.4f))
        if (l.k > 0.06f) drawPath(bordeRefugio(forma, -hacia * 1.4f * u, 0f), l.filo(0.85f))
        // La tarjeta grapada bajo el doblez, con sus dos grapas.
        val eti = rectTarjetaRefugio(x0, an, boca + doblez + 3.4f * u + h * 0.03f * azar(s, 64))
        rotate((azar(s, 65) - 0.5f) * 5f, eti.center) {
            drawRect(l.s(0.35f), eti.topLeft + Offset(-hacia * 1f * u, 1.4f * u), eti.size)
            tarjetaRefugio(v, medidor, l, eti)
            listOf(eti.left + eti.width * 0.2f, eti.right - eti.width * 0.2f).forEach { gx ->
                drawRect(l.c(P.CROMO), Offset(gx - 2f * u, eti.top + 0.8f * u), Size(4f * u, 0.9f * u))
            }
        }
        // La pinza de madera que cierra la boca, ladeada hacia un lado (de pie, con el muelle, haría cruz): las dos patas de madera
        // clara, que se abren un poco arriba, y la espira del muelle a un costado.
        val aLaIzq = azar(s, 66) < 0.5f
        val px = x0 + an * (if (aLaIzq) 0.26f else 0.74f)
        val pa = minOf(an * 0.12f, 3.4f * u)
        val largo = pinzaAl + doblez * 0.7f
        val base = Offset(px, boca + doblez * 0.85f)
        rotate(if (aLaIzq) -30f else 30f, base) {
            val madera = mezcla(P.OCRE, P.CREMA, 0.45f)
            val p1 = Path().apply { addRoundRect(RoundRect(Rect(base.x - pa, base.y - largo, base.x - 0.3f * u, base.y), CornerRadius(pa * 0.5f))) }
            val p2 = Path().apply { addRoundRect(RoundRect(Rect(base.x + 0.3f * u, base.y - largo * 0.96f, base.x + pa, base.y), CornerRadius(pa * 0.5f))) }
            drawPath(corridoRefugio(p1, -hacia * 1f * u, 1.4f * u), l.s(0.4f))
            drawPath(corridoRefugio(p2, -hacia * 1f * u, 1.4f * u), l.s(0.4f))
            drawPath(p1, l.c(madera))
            drawPath(p2, l.c(mezcla(madera, P.BAQUELITA, 0.2f)))
            drawLine(l.b(0.6f), Offset(base.x - pa + 0.6f * u, base.y - largo + 1f * u), Offset(base.x - pa + 0.6f * u, base.y - 1f * u), 0.6f * u)
            drawCircle(l.c(P.CROMO), pa * 0.55f, Offset(base.x + pa * 0.9f, base.y - largo * 0.55f), style = Stroke(0.9f * u))
        }
        if (v.noche) mediaLunaRefugio(Offset(cx, eti.bottom + (h - eti.bottom) * 0.45f), minOf(an * 0.2f, 5.6f * u), l)
    }
}
