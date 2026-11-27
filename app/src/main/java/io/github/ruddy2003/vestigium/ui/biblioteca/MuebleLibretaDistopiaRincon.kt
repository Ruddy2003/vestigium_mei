package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.ceil

/**
 * Lo fijo del rincón de la libreta de Distopía, pintado una vez por tamaño y modo: la pared del sótano a dos alturas (crema sucio
 * arriba, verde gris abajo, la raya oliva), con sus desconchones y la humedad que baja del tragaluz; el tubo de la luz que la
 * cruza arriba, la caja de empalmes de la que cuelga la lámpara y el que baja al interruptor; el tragaluz de la acera (el hueco
 * abocinado, el cristal armado, los barrotes de fuera); la lámpara colgante de tulipa esmaltada; la chapa del atril con su
 * puntal; la mesa de chapa con tablero de linóleo, su canto y sus cajones; el tampón abierto, el sello de madera, el pincho de
 * las papeletas y el lápiz de dos colores.
 */
internal object RinconLibretaDistopia {
    /** El linóleo del tablero de la mesa (algo más claro que el del suelo) y la tinta violeta del tampón. */
    private val LINO_MESA = mezcla(P.LINO, P.ACERO, 0.3f)
    private val TINTA_TAMPON = Color(0xFF3A2D52)
    private val LATA = Color(0xFF6E2A24)
    /** El esmalte de la tulipa: verde botella por fuera, blanco por dentro. */
    private val TULIPA = Color(0xFF3E5446)
    private val ESMALTE_BLANCO = Color(0xFFE9E6D8)
    /** Las papeletas del pincho: el papel hueso y las copias de color. */
    private val PAPELETAS = listOf(P.HUESO, Color(0xFFE8C4BC), Color(0xFFC9DCC4), Color(0xFFEBDDA0), P.HUESO, Color(0xFFE2D7BC))

    fun DrawScope.rincon(s: SitioLibretaDistopia, a: AireDistopia) {
        pared(s, a)
        tragaluz(s, a)
        conducto(s, a)
        interruptor(s, a)
        atril(s, a)
        mesa(s, a)
        lampara(s, a)
        tampon(s, a)
        sello(s, a)
        pincho(s, a)
        lapiz(s, a)
    }

    /**
     * Un rectángulo pintado del [color] con la luz de cada sitio: franjas a lo alto, cada una con su degradado a lo ancho (el tubo
     * de día; el charco de la lámpara y la luz de la calle de noche). Solo para lo que se pinta una vez.
     */
    private fun DrawScope.campo(s: SitioLibretaDistopia, a: AireDistopia, r: Rect, color: Color, pared: Boolean, muestras: Int = 13) {
        if (r.width < 1f || r.height < 1f) return
        val paso = maxOf(2f, 1.6f * s.U)
        val franjas = ceil(r.height / paso).toInt().coerceIn(1, 400)
        val alto = r.height / franjas
        drawRect(s.luz(a, r.center, pared).c(color), r.topLeft, r.size)
        for (i in 0 until franjas) {
            val y = r.top + alto * (i + 0.5f)
            val cols = List(muestras) { j -> s.luz(a, Offset(r.left + r.width * j / (muestras - 1f), y), pared).c(color) }
            drawRect(Brush.horizontalGradient(cols, r.left, r.right), Offset(r.left, r.top + alto * i), Size(r.width, minOf(alto + 1.2f, r.bottom - r.top - alto * i)))
        }
    }

    // ---------------------------------------------------------------- la pared

    private fun DrawScope.pared(s: SitioLibretaDistopia, a: AireDistopia) {
        val w = s.w; val U = s.U; val u = density
        campo(s, a, Rect(0f, 0f, w, s.raya), P.CREMA, true)
        campo(s, a, Rect(0f, s.raya, w, s.fondo + 1f), P.VERDE, true)
        pinceladas(Rect(0f, 0f, w, s.raya), if (a.oscuro) a.hondo else mezcla(P.CREMA, P.OXIDO, 0.35f), 7101, 26, u, 0.07f)
        pinceladas(Rect(0f, s.raya, w, s.fondo), if (a.oscuro) a.hondo else P.OLIVA, 7102, 22, u, 0.08f)
        // El techo, en penumbra; de día, el tubo (fuera del cuadro, arriba a la izquierda) aclara la pared cerca de él.
        drawRect(Brush.verticalGradient(listOf(a.sombra(if (a.oscuro) 0.5f else 0.3f), a.sombra(0f)), 0f, 30f * U), Offset.Zero, Size(w, 30f * U))
        if (!a.oscuro) mancha(Offset(s.cx - 200f * U, 20f * U), 230f * U, P.LUZ_TUBO.copy(alpha = 0.42f))
        raya(s, a)
        humedad(s, a)
        val viejo = mezcla(P.HUESO, P.CREMA, 0.4f)
        desconchon(s, a, Offset(s.cx - 172f * U, 44f * U), 8f * U, 7120, viejo)
        desconchon(s, a, Offset(s.cx - 132f * U, 152f * U), 6.5f * U, 7127, mezcla(P.VERDE, P.HUESO, 0.45f))
        desconchon(s, a, Offset(s.cx + 178f * U, 132f * U), 9f * U, 7134, mezcla(P.VERDE, P.HUESO, 0.45f))
        desconchon(s, a, Offset(s.cx - 205f * U, 96f * U), 6f * U, 7141, viejo)
        desconchon(s, a, Offset(s.cx + 120f * U, 92f * U), 5f * U, 7148, viejo)
    }

    /** La raya: un filete oliva dado a mano, un poco ondulado. */
    private fun DrawScope.raya(s: SitioLibretaDistopia, a: AireDistopia) {
        val w = s.w; val U = s.U; val u = density; val y = s.raya
        val p = Path().apply {
            moveTo(0f, y - 2.2f * U)
            for (j in 1..12) lineTo(w * j / 12f, y - 2.2f * U + (azar(7104, j) - 0.5f) * 1.2f * u)
            for (j in 12 downTo 0) lineTo(w * j / 12f, y + 2.4f * U + (azar(7105, j) - 0.5f) * 1.2f * u)
            close()
        }
        drawPath(p, Brush.horizontalGradient(List(9) { j -> s.luz(a, Offset(w * j / 8f, y), true).c(P.OLIVA) }, 0f, w))
        drawLine(s.luz(a, Offset(w * 0.2f, y), true).b(0.3f), Offset(0f, y + 2.4f * U), Offset(w, y + 2.4f * U), 0.8f * u)
    }

    /**
     * Lo que chorrea del tragaluz: una franja sucia y corta bajo el vierteaguas (donde gotea) y los chorreones de óxido del marco
     * de hierro, finos y de largo desigual, que se adelgazan hacia abajo.
     */
    private fun DrawScope.humedad(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U
        val o = s.tragaluz
        val y0 = o.bottom + 7f * U
        val l = s.luz(a, Offset(o.left + 30f * U, y0 + 20f * U), true)
        val sucio = l.c(mezcla(P.OXIDO, P.OLIVA, 0.4f))
        drawRect(Brush.verticalGradient(listOf(sucio.copy(alpha = if (a.oscuro) 0.16f else 0.12f), sucio.copy(alpha = 0f)), y0, y0 + 14f * U), Offset(o.left - 1f * U, y0), Size(o.width + 4f * U, 14f * U))
        // Los chorreones: trazos finos y algo torcidos que se desvanecen hacia abajo (no puntas: el óxido se diluye).
        val chorros = Path()
        var largoMax = 0f
        for (j in 0 until 5) {
            val x = o.left + (6f + 21f * j + 8f * azar(7110, j)) * U
            val largo = (14f + 40f * azar(7111, j)) * U
            largoMax = maxOf(largoMax, largo)
            val torcido = (azar(7112, j) - 0.5f) * 5f * U
            chorros.moveTo(x, y0)
            chorros.cubicTo(x + torcido, y0 + largo * 0.3f, x - torcido, y0 + largo * 0.6f, x + torcido * 0.5f, y0 + largo)
        }
        val oxido = l.c(P.OXIDO)
        drawPath(chorros, Brush.verticalGradient(listOf(oxido.copy(alpha = 0.4f), oxido.copy(alpha = 0.12f), oxido.copy(alpha = 0f)), y0, y0 + largoMax),
            style = Stroke(1.3f * U, cap = StrokeCap.Round))
    }

    /** Un desconchón: la capa vieja que asoma con la sombra del borde de la pintura arriba a la izquierda. */
    private fun DrawScope.desconchon(s: SitioLibretaDistopia, a: AireDistopia, c: Offset, r: Float, semilla: Int, capa: Color) {
        val u = density
        val l = s.luz(a, c, true)
        val p = escamaDistopia(c, r, r * 0.7f, semilla, 11)
        drawPath(p, l.s(0.32f))
        translate(1.1f * u, 1.3f * u) { drawPath(p, l.c(capa)) }
        for (j in 0 until 3) drawCircle(l.c(capa), (0.6f + azar(semilla, 70 + j)) * u,
            Offset(c.x + r * (1.3f + 0.6f * azar(semilla, 80 + j)) * (if (j == 1) -1f else 1f), c.y + r * (0.8f * azar(semilla, 90 + j) - 0.3f)))
    }

    // ---------------------------------------------------------------- el tragaluz

    /**
     * El tragaluz de la acera, arriba a la derecha: el hueco abocinado en el muro (las jambas y el derrame de abajo, que coge la luz
     * de fuera), el cristal armado en su marco de hierro, los barrotes de fuera y el vierteaguas de abajo. De día, la luz gris de la
     * calle; de noche, el cristal encendido frío (la farola de la acera): es la luz fría del rincón.
     */
    private fun DrawScope.tragaluz(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val o = s.tragaluz
        if (o.left > s.w) return
        val vidrio = Rect(o.left + 9f * U, 0f, o.right - 13f * U, o.bottom - 15f * U)
        val l = s.luz(a, o.center, true)
        // La sombra del vierteaguas en la pared, y el vierteaguas.
        drawRect(a.sombra(if (a.oscuro) 0.4f else 0.26f), Offset(o.left - 2f * U, o.bottom + 3f * U), Size(o.width + 7f * U, 5f * U))
        val lv = s.luz(a, Offset(o.center.x, o.bottom), true)
        drawRect(lv.c(mezcla(P.CREMA, P.ACERO, 0.25f)), Offset(o.left - 3f * U, o.bottom - 1f * U), Size(o.width + 6f * U, 4.5f * U))
        drawRect(lv.b(0.5f), Offset(o.left - 3f * U, o.bottom - 1f * U), Size(o.width + 6f * U, 1f * u))
        // Las jambas y el derrame: el muro hacia dentro, abocinado; el derrame de abajo coge la luz de fuera.
        val jambaIzq = poligonoDistopia(Offset(o.left, 0f), Offset(vidrio.left, 0f), Offset(vidrio.left, vidrio.bottom), Offset(o.left, o.bottom - 1f * U))
        val jambaDer = poligonoDistopia(Offset(vidrio.right, 0f), Offset(o.right, 0f), Offset(o.right, o.bottom - 1f * U), Offset(vidrio.right, vidrio.bottom))
        val derrame = poligonoDistopia(Offset(o.left, o.bottom - 1f * U), Offset(vidrio.left, vidrio.bottom), Offset(vidrio.right, vidrio.bottom), Offset(o.right, o.bottom - 1f * U))
        val muro = mezcla(P.CREMA, P.VERDE, 0.2f)
        drawPath(jambaIzq, l.c(mezcla(muro, P.ACERO_OSC, 0.12f)))
        drawPath(jambaDer, l.c(mezcla(muro, P.ACERO_OSC, 0.32f)))
        val lucero = if (a.oscuro) mezcla(l.c(muro), P.LUNA, 0.18f) else mezcla(muro, P.LUZ_TUBO, 0.45f)
        drawPath(derrame, lucero)
        drawPath(bordeDistopia(derrame, 0f, 2f * U), l.s(0.18f))
        drawLine(l.s(0.25f), Offset(vidrio.right, 0f), Offset(vidrio.right, vidrio.bottom), 1f * u)
        // El cristal armado: la luz de fuera (de día, gris clara; de noche, fría), más sucio abajo, con la malla de alambre.
        val arriba = if (a.oscuro) mezcla(P.LUNA, P.NOCHE, 0.42f) else mezcla(Color(0xFFE4E8E2), P.LUNA, 0.3f)
        val abajo = if (a.oscuro) mezcla(P.FRIO, P.NOCHE, 0.55f) else mezcla(Color(0xFFC9CDC4), P.CREMA, 0.3f)
        drawRect(Brush.verticalGradient(listOf(arriba, abajo), 0f, vidrio.bottom), vidrio.topLeft, vidrio.size)
        // Lo de fuera: el bordillo del patio inglés, más oscuro, abajo; los barrotes (por fuera, vistos a través del cristal).
        drawRect((if (a.oscuro) P.NOCHE else mezcla(P.ACERO_OSC, P.CREMA, 0.35f)).copy(alpha = 0.45f), Offset(vidrio.left, vidrio.bottom - 8f * U), Size(vidrio.width, 8f * U))
        val barra = if (a.oscuro) Color(0xFF0B1015) else mezcla(P.HIERRO, P.ACERO_OSC, 0.3f)
        var x = vidrio.left + 8f * U
        while (x < vidrio.right - 4f * U) { drawRect(barra.copy(alpha = 0.8f), Offset(x, 0f), Size(2.4f * U, vidrio.bottom)); x += 17f * U }
        drawRect(barra.copy(alpha = 0.8f), Offset(vidrio.left, vidrio.bottom * 0.5f), Size(vidrio.width, 2f * U))
        val malla = Path()
        var m = vidrio.left - vidrio.bottom
        while (m < vidrio.right) {
            malla.moveTo(maxOf(m, vidrio.left), vidrio.top + maxOf(0f, vidrio.left - m)); malla.lineTo(minOf(m + vidrio.bottom, vidrio.right), vidrio.top + minOf(vidrio.bottom, vidrio.right - m))
            m += 6f * U
        }
        clipPath(Path().apply { addRect(vidrio) }) {
            drawPath(malla, (if (a.oscuro) P.LUNA else P.HIERRO).copy(alpha = if (a.oscuro) 0.1f else 0.12f), style = Stroke(0.5f * u))
            // La mugre en las esquinas de abajo del cristal.
            mancha(Offset(vidrio.left + 4f * U, vidrio.bottom), 16f * U, mezcla(P.OXIDO, P.KRAFT, 0.4f).copy(alpha = if (a.oscuro) 0.25f else 0.35f))
            mancha(Offset(vidrio.right - 6f * U, vidrio.bottom), 12f * U, mezcla(P.OXIDO, P.KRAFT, 0.4f).copy(alpha = if (a.oscuro) 0.2f else 0.28f))
        }
        // El marco de hierro, con sus dos montantes (tres paños).
        val marco = l.c(mezcla(P.HIERRO, P.ACERO, 0.3f))
        drawRect(marco, vidrio.topLeft, vidrio.size, style = Stroke(2.2f * U))
        for (j in 1..2) {
            val xm = vidrio.left + vidrio.width * j / 3f
            drawRect(marco, Offset(xm - 1.2f * U, 0f), Size(2.4f * U, vidrio.bottom))
            drawLine(if (a.oscuro) P.LUNA.copy(alpha = 0.3f) else l.b(0.35f), Offset(xm - 1.2f * U, 0f), Offset(xm - 1.2f * U, vidrio.bottom), 0.7f * u)
        }
        if (a.oscuro) {
            // El filo frío en el canto del derrame y del vierteaguas, que miran a la luz de fuera.
            drawLine(P.LUNA.copy(alpha = 0.4f), Offset(vidrio.left, vidrio.bottom + 0.8f * U), Offset(vidrio.right, vidrio.bottom + 0.8f * U), 1.2f * u)
            drawLine(P.LUNA.copy(alpha = 0.3f), Offset(o.left - 3f * U, o.bottom - 0.5f * U), Offset(o.right + 3f * U, o.bottom - 0.5f * U), 1f * u)
        }
    }

    // ---------------------------------------------------------------- el tubo de la luz y el interruptor

    private fun DrawScope.conducto(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val y = s.conducto; val gr = 3.2f * U
        val metal = mezcla(P.CREMA, P.ACERO, 0.6f)
        fun tramo(p0: Offset, p1: Offset) {
            val l = s.luz(a, (p0 + p1) / 2f, true)
            val vertical = p0.x == p1.x
            drawLine(l.s(0.3f), p0 + Offset(1.6f * U, 2.2f * U), p1 + Offset(1.6f * U, 2.2f * U), gr)
            drawLine(l.c(metal), p0, p1, gr)
            val d = if (vertical) Offset(-gr * 0.25f, 0f) else Offset(0f, -gr * 0.25f)
            drawLine(l.b(0.45f), p0 + d, p1 + d, 0.8f * u)
        }
        // Cruza la pared y se mete en el techo antes del tragaluz; otro baja al interruptor.
        val fin = minOf(s.w, s.tragaluz.left - 10f * U)
        tramo(Offset(0f, y), Offset(fin, y))
        tramo(Offset(fin, y), Offset(fin, 0f))
        tramo(Offset(s.interruptor.x, y), Offset(s.interruptor.x, s.interruptor.y - 9f * U))
        // Los manguitos de empalme.
        listOf(s.cx - 150f * U, s.cx + 40f * U, s.cx - 230f * U).forEach { xm ->
            if (xm < 0f || xm > fin) return@forEach
            val l = s.luz(a, Offset(xm, y), true)
            drawRect(l.c(mezcla(metal, P.ACERO_OSC, 0.25f)), Offset(xm - 2.5f * U, y - gr * 0.7f), Size(5f * U, gr * 1.4f))
            drawRect(l.b(0.4f), Offset(xm - 2.5f * U, y - gr * 0.7f), Size(1f * u, gr * 1.4f))
        }
        // La caja de empalmes de la que cuelga la lámpara, con su tapa de dos tornillos.
        val caja = Rect(s.lamparaX - 8f * U, y - 7f * U, s.lamparaX + 8f * U, y + 8f * U)
        val l = s.luz(a, caja.center, true)
        drawRect(l.s(0.35f), caja.topLeft + Offset(2f * U, 2.5f * U), caja.size)
        drawRect(l.c(mezcla(P.ACERO, P.CREMA, 0.25f)), caja.topLeft, caja.size)
        drawRect(l.b(0.5f), caja.topLeft, Size(caja.width, 1.2f * u))
        drawRect(l.s(0.3f), Offset(caja.right - 1.4f * u, caja.top), Size(1.4f * u, caja.height))
        drawRect(l.s(0.35f), Offset(caja.left, caja.bottom - 1.4f * u), Size(caja.width, 1.4f * u))
        tornilloDistopia(Offset(caja.left + 3f * U, caja.top + 3f * U), 1.2f * u, l, 7150)
        tornilloDistopia(Offset(caja.right - 3f * U, caja.bottom - 3f * U), 1.2f * u, l, 7151)
    }

    /** El interruptor de baquelita, con su palanca. */
    private fun DrawScope.interruptor(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val c = s.interruptor
        if (c.x < -12f * U) return
        val r = Rect(c.x - 6f * U, c.y - 9f * U, c.x + 6f * U, c.y + 9f * U)
        val l = s.luz(a, c, true)
        drawRoundRect(l.s(0.4f), r.topLeft + Offset(2f * U, 2.5f * U), r.size, CornerRadius(2f * U))
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(2f * U))) }
        planosDistopia(forma, l.c(P.BAQUELITA), l, s.hacia(a, c), 0.22f, 0.2f)
        drawLine(l.b(0.5f), Offset(r.left + 1.5f * U, r.top + 1f * U), Offset(r.right - 1.5f * U, r.top + 1f * U), 0.8f * u)
        // La palanca, bajada: un vástago corto con su bola.
        val p0 = Offset(c.x, c.y + 1f * U); val p1 = Offset(c.x + 1.5f * U, c.y + 6.5f * U)
        drawLine(l.s(0.4f), p0 + Offset(1.2f * U, 1.2f * U), p1 + Offset(1.2f * U, 1.5f * U), 1.6f * U, StrokeCap.Round)
        drawLine(l.c(mezcla(P.BAQUELITA, P.ACERO, 0.3f)), p0, p1, 1.6f * U, StrokeCap.Round)
        drawCircle(l.c(mezcla(P.BAQUELITA, P.ACERO, 0.2f)), 1.6f * U, p1)
        drawCircle(l.b(0.6f), 0.6f * U, p1 - Offset(0.5f * U, 0.5f * U))
    }

    // ---------------------------------------------------------------- la lámpara colgante

    /**
     * La lámpara colgante de la caja de empalmes: el cordón trenzado, el portalámparas de baquelita, la tulipa de chapa esmaltada
     * (verde botella por fuera, blanca por dentro, con el borde enrollado) y la bombilla. De noche arde: la bombilla, el blanco de
     * dentro de la tulipa y el borde encendidos, y el halo corto en la pared; el charco lo pinta la luz de cada cosa.
     */
    private fun DrawScope.lampara(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val x = s.lamparaX; val boca = s.boca
        val arde = a.oscuro
        val l = s.luz(a, Offset(x, boca - 14f * U), true)
        // El cordón, de la caja al portalámparas.
        val y0 = s.conducto + 8f * U; val y1 = boca - 31f * U
        drawLine(l.s(0.3f), Offset(x + 2f * U, y0 + 2f * U), Offset(x + 2f * U, y1 + 2f * U), 1.4f * U)
        drawLine(l.c(Color(0xFF2E2A26)), Offset(x, y0), Offset(x, y1), 1.4f * U)
        var y = y0 + 2f * U
        while (y < y1 - 1f * U) { drawLine(l.b(0.35f), Offset(x - 0.6f * U, y), Offset(x + 0.6f * U, y + 1.2f * U), 0.5f * u); y += 2.6f * U }
        // El halo en la pared detrás de la tulipa: lo que se escapa por arriba (de noche).
        if (arde) mancha(Offset(x, boca - 18f * U), 34f * U, P.BOMBILLA.copy(alpha = 0.1f))
        // El portalámparas.
        val pl = Rect(x - 3.5f * U, y1, x + 3.5f * U, y1 + 9f * U)
        drawRect(l.c(P.BAQUELITA), pl.topLeft, pl.size)
        drawRect(l.b(0.4f), pl.topLeft, Size(1f * U, pl.height))
        // La tulipa: campana de chapa esmaltada, del cuello a la boca, con el borde enrollado.
        val cuello = boca - 24f * U
        val ancho = 25f * U
        val tulipa = Path().apply {
            moveTo(x - 6f * U, cuello)
            lineTo(x + 6f * U, cuello)
            cubicTo(x + 9f * U, cuello + 8f * U, x + ancho * 0.72f, boca - 9f * U, x + ancho, boca)
            lineTo(x - ancho, boca)
            cubicTo(x - ancho * 0.72f, boca - 9f * U, x - 9f * U, cuello + 8f * U, x - 6f * U, cuello)
            close()
        }
        // De día, la sombra blanda que la tulipa deja en la pared, abajo a la derecha (lejos de ella: borrosa).
        if (!arde) mancha(Offset(x + 14f * U, boca + 10f * U), 30f * U, a.sombra(0.16f))
        clipPath(tulipa) { cilindroDistopia(tulipa, l.c(TULIPA), l, -1f, 0.45f) }
        // El cuello con su anillo.
        drawRect(l.c(mezcla(TULIPA, P.HIERRO, 0.4f)), Offset(x - 6.5f * U, cuello - 2f * U), Size(13f * U, 3f * U))
        // La boca vista desde abajo: el blanco de dentro y la bombilla.
        val dentro = Rect(x - ancho, boca - 3.5f * U, x + ancho, boca + 4.5f * U)
        val blanco = if (arde) mezcla(ESMALTE_BLANCO, P.BOMBILLA, 0.35f) else l.c(mezcla(ESMALTE_BLANCO, P.ACERO, 0.35f))
        drawOval(blanco, dentro.topLeft, dentro.size)
        val bulbo = Rect(x - 5.5f * U, boca - 2f * U, x + 5.5f * U, boca + 9f * U)
        if (arde) {
            mancha(Offset(x, boca + 3f * U), 26f * U, P.BOMBILLA.copy(alpha = 0.55f))
            drawOval(mezcla(P.BOMBILLA, Color.White, 0.55f), bulbo.topLeft, bulbo.size)
            drawOval(Color.White.copy(alpha = 0.9f), Offset(x - 3f * U, boca), Size(6f * U, 6f * U))
        } else {
            drawOval(l.c(mezcla(ESMALTE_BLANCO, P.ACERO, 0.25f)), bulbo.topLeft, bulbo.size)
            drawOval(l.b(0.7f), Offset(x - 3.5f * U, boca), Size(2.5f * U, 4f * U))
            drawOval(l.s(0.25f), bulbo.topLeft, bulbo.size, style = Stroke(0.6f * u))
        }
        // El borde enrollado de la boca: verde por fuera, encendido de noche por dentro.
        drawLine(l.c(mezcla(TULIPA, P.HIERRO, 0.25f)), Offset(x - ancho - 0.5f * U, boca), Offset(x + ancho + 0.5f * U, boca), 2f * U, StrokeCap.Round)
        if (arde) drawLine(P.BOMBILLA.copy(alpha = 0.75f), Offset(x - ancho + 2f * U, boca + 1.2f * U), Offset(x + ancho - 2f * U, boca + 1.2f * U), 1f * u)
        else drawLine(l.b(0.6f), Offset(x - ancho, boca - 1f * U), Offset(x - 4f * U, boca - 1f * U), 0.8f * u)
        // Un saltado del esmalte, que deja ver la chapa.
        saltadoDistopia(Offset(x + 12f * U, boca - 8f * U), 1.8f * U, 7160, l.c(P.HIERRO), l.b(0.4f))
    }

    // ---------------------------------------------------------------- el atril

    /**
     * La chapa del atril portacopias, detrás del expediente: gris verdosa, de canto enrollado, con dos remaches; el puntal que la
     * sostiene por detrás asoma a la derecha, con su pie de goma en la mesa. El canal de delante va con el expediente.
     */
    private fun DrawScope.atril(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val r = s.atril
        val y = r.top + r.height * 0.4f
        // El puntal, por detrás, a la derecha: de la chapa a la mesa, hacia la pared.
        val p0 = Offset(r.right - 6f * U, r.top + r.height * 0.42f); val p1 = Offset(r.right + 14f * U, s.fondo + 5f * U)
        val lp = s.luz(a, (p0 + p1) / 2f)
        drawLine(lp.s(0.3f), p0 + Offset(4f * U, 3f * U), p1 + Offset(6f * U, 1f * U), 2.6f * U)
        drawLine(lp.c(P.ACERO_OSC), p0, p1, 2.6f * U)
        drawLine(lp.b(0.35f), p0 - Offset(0.8f * U, 0f), p1 - Offset(0.8f * U, 0f), 0.7f * u)
        drawRoundRect(lp.c(P.BAQUELITA), Offset(p1.x - 3f * U, p1.y - 1.5f * U), Size(6f * U, 3.5f * U), CornerRadius(1.2f * U))
        // La sombra de la chapa en la pared.
        val d = if (a.oscuro) Offset(9f * U, 1f * U) else Offset(7f * U, 5f * U)
        drawRect(lp.s(0.3f), r.topLeft + d, Size(r.width, s.fondo - r.top - d.y))
        // La chapa: el degradado de la luz a lo ancho, el canto enrollado y su grueso a la derecha.
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(3f * U))) }
        drawRect(s.luz(a, Offset(r.right, y)).c(P.ACERO_OSC), Offset(r.right - 1f * U, r.top + 2f * U), Size(2.5f * U, r.height - 3f * U))
        clipPath(forma) { campo(s, a, r, P.ACERO, false, 7) }
        val l = s.luz(a, Offset(r.left + 8f * U, y))
        drawPath(bordeDistopia(forma, 2f * U, 2f * U), s.luz(a, Offset(r.left + 8f * U, r.top)).b(0.45f))
        drawPath(bordeDistopia(forma, -2f * U, -2f * U), s.luz(a, Offset(r.right, y)).s(0.3f))
        if (a.oscuro) drawPath(bordeDistopia(forma, -1.6f * U, 0f), s.luz(a, Offset(r.right, r.top + 20f * U)).frio(0.8f))
        // Los remaches de arriba y un roce que deja ver el hierro.
        tornilloDistopia(Offset(r.left + 4.5f * U, r.top + 4.5f * U), 1.3f * u, l, 7170)
        tornilloDistopia(Offset(r.right - 4.5f * U, r.top + 4.5f * U), 1.3f * u, s.luz(a, Offset(r.right, r.top)), 7171)
        saltadoDistopia(Offset(r.left + 3.5f * U, r.top + r.height * 0.55f), 1.6f * U, 7172, l.c(P.HIERRO), l.b(0.35f), l.c(P.OXIDO))
    }

    // ---------------------------------------------------------------- la mesa

    /**
     * La mesa de chapa del registro: el tablero de linóleo (el charco de la lámpara lo enciende de noche), el canto de chapa con su
     * filo, la fila de cajones con su tirador de concha y su portaetiquetas, el cajón del centro con su cerradura y, debajo, los
     * cajones de los dos cuerpos y el hueco de las rodillas, hondo.
     */
    private fun DrawScope.mesa(s: SitioLibretaDistopia, a: AireDistopia) {
        val w = s.w; val U = s.U; val u = density; val h = s.h
        // El tablero, con la sombra de contacto contra la pared, el desgaste y algún rayón.
        campo(s, a, Rect(0f, s.fondo, w, s.frente), LINO_MESA, false)
        drawRect(Brush.verticalGradient(listOf(a.sombra(if (a.oscuro) 0.5f else 0.36f), a.sombra(0f)), s.fondo, s.fondo + 5f * U), Offset(0f, s.fondo), Size(w, 5f * U))
        pinceladas(Rect(0f, s.fondo + 2f * U, w, s.frente), if (a.oscuro) a.hondo else P.LINO_OSC, 7180, 14, u, 0.09f)
        val rayones = Path()
        for (j in 0 until 7) {
            val x0 = s.cx + (azar(7181, j) - 0.5f) * 320f * U; val y0 = s.fondo + (5f + 8f * azar(7182, j)) * U
            rayones.moveTo(x0, y0); rayones.lineTo(x0 + (10f + 14f * azar(7183, j)) * U, y0 + (azar(7184, j) - 0.5f) * 3f * U)
        }
        drawPath(rayones, s.luz(a, Offset(s.cx, s.mesa)).b(0.22f), style = Stroke(0.6f * u))
        // El canto de chapa del tablero: redondeado, con su filo de luz arriba y la sombra debajo.
        val canto = Rect(0f, s.frente, w, s.canto)
        val ym = (s.frente + s.canto) / 2f
        drawRect(Brush.horizontalGradient(List(9) { j -> s.luz(a, Offset(w * j / 8f, ym)).c(P.ACERO_LUZ) }, 0f, w), canto.topLeft, canto.size)
        drawRect(Brush.horizontalGradient(List(9) { j -> s.luz(a, Offset(w * j / 8f, s.frente)).b(0.65f) }, 0f, w), Offset(0f, s.frente), Size(w, 1.4f * U))
        drawRect(a.sombra(0.3f), Offset(0f, s.canto - 1.6f * U), Size(w, 1.6f * U))
        // La fila de cajones: chapa gris verdosa con la luz a lo ancho, las juntas, los tiradores y los portaetiquetas.
        val fila = Rect(0f, s.canto, w, s.cajones)
        val yf = fila.center.y
        drawRect(Brush.horizontalGradient(List(11) { j -> s.luz(a, Offset(w * j / 10f, yf)).c(P.ACERO) }, 0f, w), fila.topLeft, fila.size)
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.28f), fila.topLeft, Size(w, 2.2f * U))
        val izq = s.cx - 58f * U; val der = s.cx + 102f * U
        val juntas = listOf(s.cx - 160f * U, izq, der, s.cx + 206f * U)
        juntas.forEach { x -> junta(s, a, x, fila.top, h) }
        drawRect(a.sombra(0.4f), Offset(0f, fila.bottom - 1.2f * U), Size(w, 1.2f * U))
        listOf((s.cx - 160f * U + izq) / 2f, (izq + der) / 2f, (der + s.cx + 206f * U) / 2f, s.cx - 214f * U).forEachIndexed { i, x ->
            tirador(s, a, Offset(x, yf + 2f * U), i == 1)
        }
        // Debajo: los cajones de los dos cuerpos y el hueco de las rodillas, en el centro, hondo.
        val abajo = Rect(0f, s.cajones, w, h)
        drawRect(Brush.horizontalGradient(List(11) { j -> s.luz(a, Offset(w * j / 10f, abajo.center.y)).c(mezcla(P.ACERO, P.ACERO_OSC, 0.25f)) }, 0f, w), abajo.topLeft, abajo.size)
        val hueco = Rect(izq + 3f * U, s.cajones, der - 3f * U, h)
        val lh = s.luz(a, hueco.center)
        drawRect(Brush.verticalGradient(listOf(mezcla(lh.c(P.ACERO_OSC), a.hondo, 0.75f), mezcla(lh.c(P.ACERO_OSC), a.hondo, 0.45f)), hueco.top, hueco.bottom), hueco.topLeft, hueco.size)
        // Los costados de dentro de los dos cuerpos, vistos de canto, y el filo de luz en el de la izquierda.
        drawRect(lh.c(mezcla(P.ACERO, P.ACERO_OSC, 0.6f)), Offset(izq, s.cajones), Size(3f * U, h - s.cajones))
        drawRect(mezcla(lh.c(P.ACERO_OSC), a.hondo, 0.4f), Offset(der - 3f * U, s.cajones), Size(3f * U, h - s.cajones))
        drawLine(s.luz(a, Offset(izq, s.cajones + 8f * U)).b(0.4f), Offset(izq, s.cajones + 1f * U), Offset(izq, h), 0.8f * u)
        listOf(s.cx - 160f * U, s.cx + 206f * U).forEach { x -> junta(s, a, x, s.cajones, h) }
        listOf((s.cx - 160f * U + izq) / 2f, (der + s.cx + 206f * U) / 2f).forEach { x -> tirador(s, a, Offset(x, s.cajones + 9f * U), false) }
        drawRect(a.sombra(0.35f), Offset(0f, s.cajones), Size(w, 1.4f * U))
    }

    /** Una junta entre dos cajones: la ranura honda y el filo de luz del canto que la mira. */
    private fun DrawScope.junta(s: SitioLibretaDistopia, a: AireDistopia, x: Float, y0: Float, y1: Float) {
        if (x < -4f || x > s.w + 4f) return
        val U = s.U; val u = density
        val l = s.luz(a, Offset(x, (y0 + y1) / 2f))
        drawRect(mezcla(l.c(P.ACERO_OSC), a.hondo, 0.4f), Offset(x - 0.8f * U, y0), Size(1.6f * U, y1 - y0))
        drawLine(l.b(0.4f), Offset(x + 1.2f * U, y0 + 1f * U), Offset(x + 1.2f * U, y1), 0.7f * u)
    }

    /**
     * El tirador de concha de un cajón (la ranura honda con su labio de luz) y, encima, el portaetiquetas con su tarjeta en blanco;
     * en el del centro, la cerradura.
     */
    private fun DrawScope.tirador(s: SitioLibretaDistopia, a: AireDistopia, c: Offset, cerradura: Boolean) {
        if (c.x < -30f * s.U || c.x > s.w + 30f * s.U) return
        val U = s.U; val u = density
        val l = s.luz(a, c)
        val r = Rect(c.x - 12f * U, c.y - 2f * U, c.x + 12f * U, c.y + 3f * U)
        drawRoundRect(mezcla(l.c(P.ACERO_OSC), a.hondo, 0.35f), r.topLeft, r.size, CornerRadius(2.5f * U))
        drawLine(l.b(0.55f), Offset(r.left + 2f * U, r.bottom), Offset(r.right - 2f * U, r.bottom), 1f * u)
        drawLine(l.s(0.4f), Offset(r.left + 2f * U, r.top + 0.5f * U), Offset(r.right - 2f * U, r.top + 0.5f * U), 0.8f * u)
        if (cerradura) {
            val k = Offset(c.x + 24f * U, c.y)
            drawCircle(l.c(mezcla(P.ACERO_LUZ, P.HIERRO, 0.3f)), 2.6f * U, k)
            drawCircle(l.b(0.4f), 2.6f * U, k, style = Stroke(0.6f * u))
            drawRect(l.c(P.BAQUELITA), Offset(k.x - 0.5f * U, k.y - 1.5f * U), Size(1f * U, 3f * U))
        } else {
            val et = Rect(c.x - 7f * U, c.y - 10.5f * U, c.x + 7f * U, c.y - 4.5f * U)
            drawRect(l.c(mezcla(P.HUESO, P.MANILA, 0.3f)), et.topLeft, et.size)
            drawRect(l.c(mezcla(P.ACERO_LUZ, P.HIERRO, 0.2f)), et.topLeft, et.size, style = Stroke(0.9f * u))
            drawLine(l.s(0.25f), Offset(et.left + 1f * U, et.top + 1f * U), Offset(et.right - 1f * U, et.top + 1f * U), 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- lo de encima de la mesa

    /** El tampón, abierto: la lata con su almohadilla de tinta violeta y la tapa levantada hacia atrás, enseñando su plata. */
    private fun DrawScope.tampon(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val c = s.tampon
        if (c.x < -30f * U) return
        val l = s.luz(a, Offset(c.x, c.y - 6f * U))
        val hacia = s.hacia(a, c)
        val ancho = 32f * U
        val frente = Rect(c.x - ancho / 2, c.y - 4f * U, c.x + ancho / 2, c.y)
        val fondo = Offset(4f * U, -10f * U)
        // La sombra en la mesa, del lado contrario a la luz.
        drawOval(l.s(0.4f), Offset(frente.left - 2f * U - 6f * U * hacia, c.y - 6f * U), Size(ancho + 8f * U, 8f * U))
        // La tapa levantada, echada hacia atrás: su cara de dentro (la plata de la lata) y el canto enrollado.
        val tapa = poligonoDistopia(Offset(frente.left + fondo.x, frente.top + fondo.y), Offset(frente.right + fondo.x, frente.top + fondo.y),
            Offset(frente.right + fondo.x + 6f * U, frente.top + fondo.y - 9f * U), Offset(frente.left + fondo.x + 6f * U, frente.top + fondo.y - 9f * U))
        drawPath(tapa, l.c(mezcla(P.ACERO_LUZ, P.HUESO, 0.2f)))
        drawPath(bordeDistopia(tapa, 1.4f * U, 1.2f * U), l.c(LATA))
        drawLine(l.b(0.6f), Offset(frente.left + fondo.x + 7f * U, frente.top + fondo.y - 7.5f * U), Offset(frente.left + fondo.x + 3f * U, frente.top + fondo.y - 1.5f * U), 1.2f * u)
        // La cara de arriba: el borde de lata y la almohadilla de tinta, con su brillo.
        val arriba = poligonoDistopia(Offset(frente.left, frente.top), Offset(frente.right, frente.top), Offset(frente.right + fondo.x, frente.top + fondo.y), Offset(frente.left + fondo.x, frente.top + fondo.y))
        drawPath(arriba, l.c(mezcla(LATA, P.ACERO_LUZ, 0.2f)))
        val pad = poligonoDistopia(Offset(frente.left + 2.5f * U, frente.top - 1f * U), Offset(frente.right - 2f * U, frente.top - 1f * U),
            Offset(frente.right + fondo.x - 3f * U, frente.top + fondo.y + 1.2f * U), Offset(frente.left + fondo.x + 2f * U, frente.top + fondo.y + 1.2f * U))
        drawPath(pad, l.c(TINTA_TAMPON))
        drawLine(l.b(0.4f), Offset(frente.left + 8f * U, frente.top - 3f * U), Offset(frente.left + 20f * U, frente.top - 3.6f * U), 1.4f * u)
        // El frente de la lata.
        planosDistopia(Path().apply { addRect(frente) }, l.c(LATA), l, hacia, 0.18f, 0.2f)
        drawLine(l.b(0.5f), Offset(frente.left, frente.top + 0.6f * U), Offset(frente.right, frente.top + 0.6f * U), 0.8f * u)
        drawLine(l.s(0.3f), Offset(frente.left + 1f * U, frente.bottom - 1f * U), Offset(frente.right - 1f * U, frente.bottom - 1f * U), 0.8f * u)
        if (l.k > 0.05f) drawLine(l.filo(0.6f), Offset(frente.left, frente.top), Offset(frente.left, frente.bottom), 1f * u)
    }

    /** El sello de goma: el taco de madera con su goma, el cuello y el pomo, de pie en la mesa. */
    private fun DrawScope.sello(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val c = s.sello
        if (c.x < -20f * U) return
        val l = s.luz(a, Offset(c.x, c.y - 14f * U))
        val hacia = s.hacia(a, c)
        // La sombra en la mesa.
        drawOval(l.s(0.45f), Offset(c.x - 12f * U - 5f * U * hacia, c.y - 4f * U), Size(26f * U, 6f * U))
        // El taco: la cara de arriba, el frente y la goma negra abajo.
        val taco = Rect(c.x - 11f * U, c.y - 8f * U, c.x + 11f * U, c.y - 1.5f * U)
        drawPath(poligonoDistopia(Offset(taco.left, taco.top), Offset(taco.right, taco.top), Offset(taco.right + 2.5f * U, taco.top - 3.5f * U), Offset(taco.left + 2.5f * U, taco.top - 3.5f * U)),
            l.c(mezcla(P.MADERA, P.HUESO, 0.25f)))
        planosDistopia(Path().apply { addRect(taco) }, l.c(P.MADERA), l, hacia, 0.2f, 0.22f)
        drawRect(l.c(P.BAQUELITA), Offset(taco.left + 0.5f * U, taco.bottom), Size(taco.width - 1f * U, 1.5f * U))
        drawLine(l.s(0.3f), Offset(taco.left, taco.top + 2.5f * U), Offset(taco.right, taco.top + 2.5f * U), 0.6f * u)
        // El cuello y el pomo, torneados.
        val cuello = Path().apply { addRect(Rect(c.x - 2.6f * U, c.y - 19f * U, c.x + 2.6f * U, c.y - 9.5f * U)) }
        clipPath(cuello) { cilindroDistopia(cuello, l.c(P.MADERA), l, hacia, 0.5f) }
        val pomo = Path().apply { addOval(Rect(c.x - 6.5f * U, c.y - 30f * U, c.x + 6.5f * U, c.y - 17.5f * U)) }
        clipPath(pomo) { cilindroDistopia(pomo, l.c(mezcla(P.MADERA, P.BURDEOS, 0.25f)), l, hacia, 0.55f) }
        drawOval(l.b(0.6f), Offset(c.x - 4f * U * (-hacia) - 1.5f * U, c.y - 28f * U), Size(3f * U, 4f * U))
        if (l.k > 0.05f) drawPath(bordeDistopia(pomo, -hacia * 1.4f * U, 0f), l.filo(0.7f))
    }

    /**
     * El pincho de las papeletas: el pie de fundición, la aguja y el montón de papeletas ensartadas (hueso y copias de color),
     * giradas unas sobre otras y abarquilladas, con la punta que asoma arriba.
     */
    private fun DrawScope.pincho(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val c = s.pincho
        if (c.x > s.w + 20f * U) return
        val l = s.luz(a, Offset(c.x, c.y - 14f * U))
        val hacia = s.hacia(a, c)
        // La sombra en la mesa y el pie de fundición: el canto y la cara de arriba.
        drawOval(l.s(0.45f), Offset(c.x - 13f * U + 5f * U, c.y - 4f * U), Size(28f * U, 7f * U))
        val pie = Rect(c.x - 12f * U, c.y - 6f * U, c.x + 12f * U, c.y)
        drawRoundRect(l.c(P.HIERRO), Offset(pie.left, pie.top + 2.5f * U), Size(pie.width, 3.5f * U), CornerRadius(1.5f * U))
        drawOval(l.c(mezcla(P.HIERRO, P.ACERO, 0.35f)), Offset(pie.left, pie.top), Size(pie.width, 5f * U))
        drawOval(l.b(0.35f), Offset(pie.left + 3f * U, pie.top + 0.6f * U), Size(pie.width * 0.5f, 1.6f * U))
        if (a.oscuro) drawLine(l.frio(0.7f), Offset(pie.right - 2f * U, pie.top + 1.5f * U), Offset(pie.right - 0.5f * U, pie.top + 4.5f * U), 1f * u)
        // La aguja, detrás de las papeletas, y la punta.
        val tope = c.y - 50f * U
        drawLine(l.c(P.ACERO_LUZ), Offset(c.x, c.y - 3f * U), Offset(c.x, tope + 4f * U), 1.3f * U)
        drawPath(poligonoDistopia(Offset(c.x - 0.65f * U, tope + 4f * U), Offset(c.x + 0.65f * U, tope + 4f * U), Offset(c.x, tope)), l.c(P.ACERO_LUZ))
        drawLine(l.b(0.6f), Offset(c.x - 0.3f * U, c.y - 6f * U), Offset(c.x - 0.3f * U, tope + 5f * U), 0.5f * u)
        // Las papeletas, de abajo arriba, cada una girada y con su agujero.
        for (j in 0 until 7) {
            val y = c.y - (7f + 2.6f * j) * U
            val ang = (azar(7190, j) - 0.5f) * 44f
            val ancho = (24f - j * 0.8f + 4f * azar(7191, j)) * U
            val alto = (11f + 4f * azar(7192, j)) * U
            val dx = (azar(7193, j) - 0.5f) * 8f * U
            val papel = PAPELETAS[j % PAPELETAS.size]
            val lp = s.luz(a, Offset(c.x + dx, y))
            // Vista de canto (está tumbada en el montón): un rombo aplastado, con su canto de abajo en sombra.
            rotate(ang * 0.25f, Offset(c.x, y)) {
                val r = Rect(c.x - ancho / 2 + dx, y - alto * 0.18f, c.x + ancho / 2 + dx, y + alto * 0.18f)
                drawRect(lp.s(0.25f), Offset(r.left + 1f * U, r.bottom - 0.5f * U), Size(r.width, 1.2f * U))
                val hoja = poligonoDistopia(Offset(r.left, r.bottom), Offset(r.right - 3f * U, r.bottom), Offset(r.right, r.top), Offset(r.left + 3f * U, r.top))
                drawPath(hoja, lp.c(papel))
                drawLine(lp.c(mezcla(papel, P.KRAFT, 0.35f)), Offset(r.left, r.bottom), Offset(r.right - 3f * U, r.bottom), 0.7f * u)
                // La esquina abarquillada hacia arriba.
                if (j % 2 == 1) drawPath(poligonoDistopia(Offset(r.right - 6f * U, r.top), Offset(r.right, r.top), Offset(r.right + 1f * U, r.top - 3.5f * U)), lp.c(mezcla(papel, P.HUESO, 0.4f)))
            }
        }
        // La papeleta de arriba, la última ensartada, más levantada: se le ve la cara con sus renglones a máquina.
        val y = c.y - 27f * U
        val lp = s.luz(a, Offset(c.x, y))
        rotate(-14f, Offset(c.x, y)) {
            val r = Rect(c.x - 13f * U, y - 7f * U, c.x + 12f * U, y + 4f * U)
            drawRect(lp.s(0.3f), r.topLeft + Offset(1.2f * U, 2f * U), r.size)
            drawRect(lp.c(P.HUESO), r.topLeft, r.size)
            drawPath(renglonesDistopia(Rect(r.left + 2.5f * U, r.top + 2.5f * U, r.right - 2.5f * U, r.bottom - 1.5f * U), 0.9f * U, 2.4f * U, 7195), lp.c(P.BAQUELITA).copy(alpha = 0.45f))
            drawCircle(lp.s(0.6f), 1f * U, Offset(c.x, y - 1.5f * U))
            if (a.oscuro) drawLine(lp.frio(0.8f), Offset(r.right, r.top), Offset(r.right, r.bottom), 1f * u)
        }
        // La punta asoma por encima del montón.
        drawLine(l.c(P.ACERO_LUZ), Offset(c.x, y - 2f * U), Offset(c.x, tope + 4f * U), 1.3f * U)
        if (a.oscuro) drawLine(l.frio(0.9f), Offset(c.x + 0.5f * U, y - 4f * U), Offset(c.x + 0.5f * U, tope + 4f * U), 0.6f * u)
    }

    /** El lápiz de dos colores (rojo y azul, el de corregir), tumbado en la mesa delante del atril, con sus dos puntas. */
    private fun DrawScope.lapiz(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val c = Offset(s.tapa.left - 26f * U, s.mesa + 4.5f * U)
        val l = s.luz(a, c)
        rotate(-6f, c) {
            val largo = 54f * U; val g = 3.6f * U
            val r = Rect(c.x - largo / 2, c.y - g / 2, c.x + largo / 2, c.y + g / 2)
            drawRect(l.s(0.4f), Offset(r.left + 2f * U, r.bottom - 0.4f * U), Size(r.width, 1.6f * U))
            drawRect(l.c(P.CARTEL), r.topLeft, Size(r.width / 2, g))
            drawRect(l.c(Color(0xFF2E4E7A)), Offset(r.center.x, r.top), Size(r.width / 2, g))
            drawRect(l.b(0.45f), r.topLeft, Size(r.width, g * 0.35f))
            drawRect(l.s(0.25f), Offset(r.left, r.bottom - g * 0.3f), Size(r.width, g * 0.3f))
            // Las dos puntas afiladas: la madera y la mina.
            for (signo in listOf(-1f, 1f)) {
                val x0 = if (signo < 0f) r.left else r.right
                drawPath(poligonoDistopia(Offset(x0, r.top), Offset(x0 + signo * 7f * U, c.y), Offset(x0, r.bottom)), l.c(mezcla(P.MADERA, P.HUESO, 0.45f)))
                drawPath(poligonoDistopia(Offset(x0 + signo * 4.6f * U, c.y - 0.75f * U), Offset(x0 + signo * 7f * U, c.y), Offset(x0 + signo * 4.6f * U, c.y + 0.75f * U)),
                    l.c(if (signo < 0f) P.CARTEL else Color(0xFF2E4E7A)))
            }
        }
    }
}
