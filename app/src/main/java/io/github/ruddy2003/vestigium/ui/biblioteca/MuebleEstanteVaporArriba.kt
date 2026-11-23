package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.ALMAGRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.ANTE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BARRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BERMELLON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BRASA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.COBRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.CUERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.GAS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LOZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.OCASO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL_VIEJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.TIZNE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * Lo de encima del mueble y lo del pie (ver [MuebleEstanteVapor]). Encima, de izquierda a derecha: los manuales de pie, uno
 * vencido, delante de la ventana; dos libros tumbados con la taza de té en su plato; el tintero de vidrio con su pluma; la
 * maqueta de locomotora en su tramo de vía ([MaquetaVapor]); y la campana de mano bajo el mechero. Cada cosa con su sombra en la
 * pared (de día a la derecha; de noche, lejos del gas). Encima de todo, la luz: de día el haz de la ventana en el aire con
 * humo; de noche el resplandor del gas; en los Sueños, la luna o el ocaso. Lo del pie está en [PieEstanteVapor].
 */
internal object ArribaEstanteVapor {

    /** Hacia dónde cae la sombra de algo en la pared, en píxeles: de día abajo a la derecha; de noche, lejos del gas. */
    private fun corrimiento(a: AireVapor, g: MarcoVapor, x: Float): Offset {
        val w = g.w
        // De día y al anochecer, la luz viene de la ventana; de noche, del gas (la sombra se va lejos de él, más cuanto más cerca).
        if (!a.oscuro) return Offset(w * (if (a.ocaso) 0.026f else 0.018f), w * 0.008f)
        val d = ((x / w - MuebleEstanteVapor.GAS_X) / 0.4f).coerceIn(-1f, 1f)
        return Offset(w * 0.055f * d, w * 0.008f)
    }

    /** La sombra de [p] en la pared, recortada donde empieza la tapa del mueble. */
    private fun DrawScope.sombraPared(p: Path, a: AireVapor, g: MarcoVapor, x: Float, fuerza: Float = 0.26f) {
        val k = if (a.oscuro) g.cerca(Offset(x, g.apoyo)) else 1f
        if (a.oscuro && k < 0.1f) return
        val o = corrimiento(a, g, x)
        clipRect(0f, 0f, g.w, g.fondoTapa + 1f) { drawPath(corridoVapor(p, o.x, o.y), a.sombra(if (a.oscuro) minOf(0.55f, fuerza * 2f * k) else fuerza)) }
    }

    // ---------------------------------------------------------------- encima del mueble

    fun DrawScope.bodegonVapor(a: AireVapor, g: MarcoVapor) {
        val w = g.w
        val pie = g.apoyo - w * 0.007f
        manualesDePie(a, g, pie)
        cintas(a, g, pie)
        tintero(a, g, pie)
        atril(a, g, pie)
        val cm = Offset(w * 0.56f, pie - w * 0.12f)
        val km = g.cerca(cm)
        val sil = Path().apply {
            addRect(Rect(w * 0.32f, pie - w * 0.05f, w * 0.78f, pie)); addRect(Rect(w * 0.37f, pie - w * 0.23f, w * 0.75f, pie - w * 0.04f))
            addRect(Rect(w * 0.39f, pie - w * 0.278f, w * 0.42f, pie - w * 0.2f)); addRect(Rect(w * 0.59f, pie - w * 0.27f, w * 0.68f, pie - w * 0.2f))
        }
        sombraPared(sil, a, g, cm.x, 0.24f)
        with(MaquetaVapor) { maqueta(w * 0.32f, w * 0.785f, pie, w * 0.44f, a, km, g.frio(cm), g.brasa(cm), if (a.oscuro) 1f else -1f) }
        librosTumbados(a, g, pie)
        campana(a, g, pie)
        luzEncima(a, g)
    }

    /** Tres manuales de tela de pie, con sus nervios dorados y su tejuelo; el tercero vencido contra los otros. */
    private fun DrawScope.manualesDePie(a: AireVapor, g: MarcoVapor, pie: Float) {
        val u = density; val w = g.w
        val libros = listOf(Triple(0.034f, 0.026f, 0.152f), Triple(0.061f, 0.022f, 0.136f), Triple(0.084f, 0.025f, 0.146f))
        val telas = listOf(VERDE, ALMAGRE, Color(0xFF2D3A5A))
        libros.forEachIndexed { i, (x0, ancho, alto) ->
            val r = Rect(w * x0, pie - w * alto, w * (x0 + ancho), pie)
            val p = Offset(r.center.x, r.center.y)
            val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
            val giro = if (i == 2) 9f else 0f
            rotate(giro, Offset(r.left, r.bottom)) {
                val forma = Path().apply { addRect(r) }
                if (i == 2) sombraPared(forma, a, g, r.center.x, 0.3f) else if (i == 0) sombraPared(forma, a, g, r.center.x, 0.22f)
                drawRect(a.luz(telas[i], k, f, br), r.topLeft, r.size)
                drawRect(a.brillo(0.3f, k, br), Offset(r.left + r.width * 0.15f, r.top), Size(r.width * 0.18f, r.height))
                drawRect(a.sombra(0.3f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
                listOf(0.1f, 0.16f, 0.78f, 0.88f).forEach { fy -> drawRect(a.luz(LATON, k, f, br), Offset(r.left, r.top + r.height * fy), Size(r.width, 0.9f * u)) }
                drawRect(a.luz(mezcla(PAPEL_VIEJO, NEGRO, 0.1f), k, f, br), Offset(r.left + r.width * 0.18f, r.top + r.height * 0.24f), Size(r.width * 0.64f, r.height * 0.12f))
                drawRect(a.sombra(0.35f), Offset(r.left, r.top), Size(r.width, 1f * u))
            }
        }
    }

    /** Las cintas de registro de los manuales (una roja y una verde), que salen por abajo y cuelgan por el canto de la cornisa. */
    private fun DrawScope.cintas(a: AireVapor, g: MarcoVapor, pie: Float) {
        val u = density; val w = g.w
        val p = Offset(w * 0.07f, g.apoyo)
        val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
        listOf(Triple(w * 0.046f, BERMELLON, 0.05f), Triple(w * 0.073f, VERDE, 0.034f)).forEach { (x, col, largo) ->
            val cinta = Path().apply {
                moveTo(x, pie - w * 0.004f); lineTo(x + w * 0.006f, pie - w * 0.004f)
                quadraticTo(x + w * 0.009f, g.apoyo + w * 0.004f, x + w * 0.01f, g.apoyo + w * largo)
                lineTo(x + w * 0.007f, g.apoyo + w * largo - w * 0.004f); lineTo(x + w * 0.004f, g.apoyo + w * largo)
                quadraticTo(x + w * 0.003f, g.apoyo + w * 0.004f, x, pie - w * 0.004f); close()
            }
            drawPath(corridoVapor(cinta, w * 0.003f, w * 0.003f), a.sombra(0.3f))
            drawPath(cinta, a.luz(col, k, f, br))
            drawLine(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(x + w * 0.0015f, pie), Offset(x + w * 0.005f, g.apoyo + w * largo * 0.8f), 0.6f * u)
        }
    }

    /** Dos libros tumbados (el canto de las hojas del de arriba a la vista) con la taza de té en su plato. */
    private fun DrawScope.librosTumbados(a: AireVapor, g: MarcoVapor, pie: Float) {
        val u = density; val w = g.w
        val b1 = Rect(w * 0.815f, pie - w * 0.024f, w * 0.958f, pie)
        val b2 = Rect(w * 0.828f, b1.top - w * 0.019f, w * 0.95f, b1.top)
        val p = b2.center
        val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
        val sil = Path().apply { addRect(Rect(b1.left, b2.top, b1.right, b1.bottom)); addRect(Rect(w * 0.85f, b2.top - w * 0.04f, w * 0.9f, b2.top)) }
        sombraPared(sil, a, g, p.x, 0.24f)
        // El de abajo, de lomo: cuero castaño con nervios.
        drawRect(a.luz(CUERO, k, f, br), b1.topLeft, b1.size)
        drawRect(a.brillo(0.3f, k, br), Offset(b1.left, b1.top + b1.height * 0.15f), Size(b1.width, b1.height * 0.18f))
        drawRect(a.sombra(0.3f), Offset(b1.left, b1.bottom - b1.height * 0.3f), Size(b1.width, b1.height * 0.3f))
        listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { fx -> drawRect(a.luz(LATON, k, f, br).copy(alpha = 0.8f), Offset(b1.left + b1.width * fx, b1.top), Size(0.9f * u, b1.height)) }
        // El de arriba, de canto: las tapas de tela azul y las hojas con su raya.
        drawRect(a.luz(Color(0xFF283C58), k, f, br), b2.topLeft, b2.size)
        val hojas = Rect(b2.left + w * 0.003f, b2.top + w * 0.003f, b2.right - w * 0.004f, b2.bottom - w * 0.003f)
        drawRect(a.luz(PAPEL_VIEJO, k, f, br), hojas.topLeft, hojas.size)
        for (j in 1 until 4) drawLine(a.sombra(0.18f), Offset(hojas.left, hojas.top + hojas.height * j / 4f), Offset(hojas.right, hojas.top + hojas.height * j / 4f), 0.5f * u)
        drawRect(a.sombra(0.4f), Offset(b2.left, b2.bottom - 1f * u), Size(b2.width, 1f * u))
        // La cinta de registro que asoma.
        drawPath(poligonoVapor(w * 0.92f, b2.bottom, w * 0.927f, b2.bottom, w * 0.929f, b1.bottom + w * 0.012f, w * 0.923f, b1.bottom + w * 0.008f, w * 0.918f, b1.bottom + w * 0.013f), a.luz(BERMELLON, k, f, br))
        // El plato y la taza de loza con su filete azul y el té.
        val cx = w * 0.875f; val base = b2.top
        drawOval(a.sombra(0.3f), Offset(cx - w * 0.026f, base - w * 0.004f), Size(w * 0.056f, w * 0.008f))
        drawOval(a.luz(LOZA, k, f, br), Offset(cx - w * 0.028f, base - w * 0.008f), Size(w * 0.056f, w * 0.009f))
        drawOval(a.luz(AZUL, k, f, br), Offset(cx - w * 0.028f, base - w * 0.008f), Size(w * 0.056f, w * 0.009f), style = Stroke(0.7f * u))
        val taza = Path().apply {
            moveTo(cx - w * 0.019f, base - w * 0.034f); lineTo(cx + w * 0.019f, base - w * 0.034f)
            quadraticTo(cx + w * 0.018f, base - w * 0.008f, cx + w * 0.009f, base - w * 0.006f); lineTo(cx - w * 0.009f, base - w * 0.006f)
            quadraticTo(cx - w * 0.018f, base - w * 0.008f, cx - w * 0.019f, base - w * 0.034f); close()
        }
        // El asa, a la derecha.
        drawArc(a.luz(LOZA, k, f, br), -80f, 160f, false, Offset(cx + w * 0.012f, base - w * 0.03f), Size(w * 0.016f, w * 0.016f), style = Stroke(w * 0.004f))
        drawPath(taza, a.luz(LOZA, k, f, br))
        clipPath(taza) {
            drawRect(a.sombra(0.25f), Offset(cx + w * 0.004f, base - w * 0.04f), Size(w * 0.02f, w * 0.04f))
            drawRect(a.brillo(0.6f, k, br), Offset(cx - w * 0.013f, base - w * 0.034f), Size(w * 0.004f, w * 0.026f))
            drawRect(a.luz(AZUL, k, f, br), Offset(cx - w * 0.02f, base - w * 0.03f), Size(w * 0.04f, w * 0.003f))
        }
        drawOval(a.luz(Color(0xFF8A5A2E), k, f, br), Offset(cx - w * 0.018f, base - w * 0.037f), Size(w * 0.036f, w * 0.006f))
        drawOval(a.luz(LOZA, k, f, br), Offset(cx - w * 0.019f, base - w * 0.038f), Size(w * 0.038f, w * 0.008f), style = Stroke(0.8f * u))
    }

    /** El tintero de vidrio, cuadrado y facetado, con su tapa de latón, la tinta dentro y la pluma tumbada delante. */
    private fun DrawScope.tintero(a: AireVapor, g: MarcoVapor, pie: Float) {
        val u = density; val w = g.w
        val r = Rect(w * 0.118f, pie - w * 0.034f, w * 0.152f, pie)
        val p = r.center
        val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
        sombraPared(Path().apply { addRect(r) }, a, g, p.x, 0.22f)
        contactoVapor(Offset(r.center.x, pie), r.width * 1.4f, w * 0.008f, a)
        drawRect(a.luz(Color(0xFF8FA3A0), k, f, br).copy(alpha = 0.85f), r.topLeft, r.size)
        drawRect(a.luz(Color(0xFF14141E), k, f, br), Offset(r.left + r.width * 0.1f, r.top + r.height * 0.45f), Size(r.width * 0.8f, r.height * 0.48f))
        drawRect(a.brillo(0.75f, maxOf(k, 0.3f), br), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.1f), Size(r.width * 0.12f, r.height * 0.8f))
        drawRect(a.sombra(0.3f), Offset(r.right - r.width * 0.22f, r.top), Size(r.width * 0.22f, r.height))
        val tapa = Rect(r.left + r.width * 0.2f, r.top - w * 0.008f, r.right - r.width * 0.2f, r.top + w * 0.002f)
        drawRect(a.luz(LATON, k, f, br), tapa.topLeft, tapa.size)
        drawRect(a.brillo(0.6f, k, br), tapa.topLeft, Size(tapa.width, 0.8f * u))
        // La pluma: el mango de madera y el plumín de acero, tumbada delante, con la punta saliendo del canto.
        val p0 = Offset(w * 0.126f, pie + w * 0.003f); val p1 = Offset(w * 0.2f, pie - w * 0.001f)
        drawLine(a.sombra(0.3f), p0 + Offset(1f * u, 1.5f * u), p1 + Offset(1f * u, 1.5f * u), w * 0.005f)
        drawLine(a.luz(NOGAL, k, f, br), p0, p1, w * 0.005f, StrokeCap.Round)
        drawLine(a.luz(HIERRO_LUZ, k, f, br), p1, p1 + Offset(w * 0.014f, -w * 0.001f), w * 0.0035f, StrokeCap.Round)
    }

    /**
     * El atril de mesa de latón y madera con el manual abierto por la lámina de una rueda y su manivela: las dos páginas con su
     * curva, las líneas del texto (sin letras), el dibujo, el lomo hundido y las tapas que asoman; el lápiz en el reborde.
     */
    private fun DrawScope.atril(a: AireVapor, g: MarcoVapor, pie: Float) {
        val u = density; val w = g.w
        val l = w * 0.165f; val r = w * 0.31f
        val mid = (l + r) / 2
        val alto = w * 0.1f
        val p = Offset(mid, pie - alto / 2)
        val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
        val inc = w * 0.012f
        // El respaldo de madera y el pie, y su sombra en la pared.
        val respaldo = poligonoVapor(l + inc, pie - alto - w * 0.004f, r - inc, pie - alto - w * 0.004f, r, pie - w * 0.012f, l, pie - w * 0.012f)
        sombraPared(respaldo, a, g, mid, 0.26f)
        contactoVapor(Offset(mid, pie), (r - l) * 1.15f, w * 0.01f, a)
        drawPath(respaldo, a.luz(NOGAL, k, f, br))
        // Las tapas de tela que asoman por los lados y las dos páginas.
        val tapas = poligonoVapor(l + inc - w * 0.006f, pie - alto + w * 0.004f, r - inc + w * 0.006f, pie - alto + w * 0.004f, r + w * 0.004f, pie - w * 0.016f, l - w * 0.004f, pie - w * 0.016f)
        drawPath(tapas, a.luz(Color(0xFF2D3A5A), k, f, br))
        val izq = Path().apply {
            moveTo(l + inc, pie - alto + w * 0.012f); quadraticTo(l + (mid - l) * 0.5f, pie - alto + w * 0.002f, mid, pie - alto + w * 0.014f)
            lineTo(mid, pie - w * 0.02f); quadraticTo(l + (mid - l) * 0.5f, pie - w * 0.026f, l + w * 0.002f, pie - w * 0.02f); close()
        }
        val der = Path().apply {
            moveTo(r - inc, pie - alto + w * 0.012f); quadraticTo(mid + (r - mid) * 0.5f, pie - alto + w * 0.002f, mid, pie - alto + w * 0.014f)
            lineTo(mid, pie - w * 0.02f); quadraticTo(mid + (r - mid) * 0.5f, pie - w * 0.026f, r - w * 0.002f, pie - w * 0.02f); close()
        }
        drawPath(izq, a.luz(PAPEL, k, f, br))
        drawPath(der, a.luz(mezcla(PAPEL, PAPEL_VIEJO, 0.3f), k, f, br))
        clipPath(izq) { drawRect(a.sombra(0.25f), Offset(mid - w * 0.014f, pie - alto), Size(w * 0.014f, alto)) }
        clipPath(der) { drawRect(a.sombra(0.18f), Offset(mid, pie - alto), Size(w * 0.012f, alto)) }
        // Las líneas del texto, a la izquierda, y la lámina de la rueda, a la derecha.
        val lineas = Path()
        for (j in 0 until 6) {
            val y = pie - alto + w * (0.024f + 0.011f * j)
            lineas.moveTo(l + inc + w * 0.008f, y); lineas.lineTo(mid - w * (0.01f + 0.012f * azar(3160, j)), y)
        }
        drawPath(lineas, a.grano(Color(0xFF6E6658), 0.7f), style = Stroke(0.8f * u))
        val cr = Offset(mid + (r - mid) * 0.45f, pie - alto * 0.52f)
        val rr = w * 0.022f
        val dib = a.grano(Color(0xFF3A3A48), 0.85f)
        drawCircle(dib, rr, cr, style = Stroke(0.8f * u))
        drawCircle(dib, rr * 0.25f, cr, style = Stroke(0.7f * u))
        val radios = Path()
        for (j in 0 until 8) { val t = j * Math.PI / 4; radios.moveTo(cr.x + rr * 0.25f * kotlin.math.cos(t).toFloat(), cr.y + rr * 0.25f * kotlin.math.sin(t).toFloat()); radios.lineTo(cr.x + rr * kotlin.math.cos(t).toFloat(), cr.y + rr * kotlin.math.sin(t).toFloat()) }
        drawPath(radios, dib, style = Stroke(0.5f * u))
        drawLine(a.luz(BERMELLON, k, f, br), cr, cr + Offset(rr * 1.5f, -rr * 0.7f), 0.8f * u)
        // El reborde de latón donde se apoya el libro, y el lápiz.
        drawRect(a.luz(LATON, k, f, br), Offset(l - w * 0.006f, pie - w * 0.02f), Size(r - l + w * 0.012f, w * 0.008f))
        drawRect(a.brillo(0.6f, maxOf(k, 0.3f), br), Offset(l - w * 0.006f, pie - w * 0.02f), Size(r - l + w * 0.012f, 0.8f * u))
        drawRect(a.sombra(0.4f), Offset(l - w * 0.006f, pie - w * 0.012f), Size(r - l + w * 0.012f, 1f * u))
        drawLine(a.luz(Color(0xFFB8862B), k, f, br), Offset(l + w * 0.02f, pie - w * 0.023f), Offset(r - w * 0.03f, pie - w * 0.025f), w * 0.005f, StrokeCap.Round)
        drawLine(a.luz(NEGRO, k, f, br), Offset(r - w * 0.03f, pie - w * 0.025f), Offset(r - w * 0.022f, pie - w * 0.0255f), w * 0.003f, StrokeCap.Round)
    }

    /** La campana de mano: el vaso de latón boca abajo, el mango torneado de madera oscura y su sombra. */
    private fun DrawScope.campana(a: AireVapor, g: MarcoVapor, pie: Float) {
        val u = density; val w = g.w
        val cx = w * 0.815f
        val p = Offset(cx, pie - w * 0.04f)
        val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
        val vaso = Path().apply {
            moveTo(cx - w * 0.034f, pie); quadraticTo(cx - w * 0.026f, pie - w * 0.006f, cx - w * 0.022f, pie - w * 0.03f)
            quadraticTo(cx - w * 0.02f, pie - w * 0.052f, cx, pie - w * 0.053f); quadraticTo(cx + w * 0.02f, pie - w * 0.052f, cx + w * 0.022f, pie - w * 0.03f)
            quadraticTo(cx + w * 0.026f, pie - w * 0.006f, cx + w * 0.034f, pie); close()
        }
        val mango = Path().apply {
            addRect(Rect(cx - w * 0.004f, pie - w * 0.07f, cx + w * 0.004f, pie - w * 0.05f))
            addOval(Rect(cx - w * 0.009f, pie - w * 0.098f, cx + w * 0.009f, pie - w * 0.066f))
        }
        sombraPared(Path().apply { addPath(vaso); addPath(mango) }, a, g, cx, 0.26f)
        contactoVapor(Offset(cx, pie), w * 0.08f, w * 0.008f, a, lado = if (a.oscuro) -1f else 1f)
        drawPath(vaso, a.luz(LATON, k, f, br))
        clipPath(vaso) {
            val izq = !a.oscuro
            drawRect(a.luz(LATON_OSC, k, f, br), Offset(if (izq) cx + w * 0.005f else cx - w * 0.035f, pie - w * 0.06f), Size(w * 0.03f, w * 0.06f))
            drawRect(a.luz(LATON_LUZ, k, f, br), Offset(if (izq) cx - w * 0.016f else cx + w * 0.01f, pie - w * 0.06f), Size(w * 0.006f, w * 0.06f))
            drawOval(Color.White.copy(alpha = if (a.oscuro) 0.6f * k else 0.7f), Offset(if (izq) cx - w * 0.015f else cx + w * 0.011f, pie - w * 0.04f), Size(w * 0.004f, w * 0.014f))
        }
        drawRect(a.luz(LATON_OSC, k, f, br), Offset(cx - w * 0.034f, pie - w * 0.004f), Size(w * 0.068f, w * 0.004f))
        drawPath(mango, a.luz(NOGAL, k, f, br))
        drawRect(a.brillo(0.45f, k, br), Offset(cx - w * (if (a.oscuro) -0.003f else 0.006f), pie - w * 0.094f), Size(w * 0.003f, w * 0.04f))
    }

    /** La luz de encima de todo: el haz de la ventana en el aire con humo, el resplandor del gas, la luna o el ocaso. */
    private fun DrawScope.luzEncima(a: AireVapor, g: MarcoVapor) {
        val w = g.w
        val gas = Offset(MuebleEstanteVapor.GAS_X * w, MuebleEstanteVapor.GAS_Y * w)
        val ven = Offset(MuebleEstanteVapor.VENTANA_X * w, MuebleEstanteVapor.VENTANA_Y * w)
        when {
            !a.lumbre -> {
                // El haz de la ventana baja en diagonal por el aire tiznado hasta la tapa y la maqueta.
                val haz = poligonoVapor(w * 0.07f, w * 0.16f, w * 0.24f, w * 0.12f, w * 0.66f, g.apoyo, w * 0.3f, g.apoyo)
                drawPath(haz, luzMueble(0.07f))
                drawPath(poligonoVapor(w * 0.11f, w * 0.18f, w * 0.2f, w * 0.16f, w * 0.52f, g.apoyo, w * 0.36f, g.apoyo), luzMueble(0.06f))
            }
            a.oscuro -> {
                val fuerte = if (a.noche) 0.55f else 1f
                // El halo ámbar ancho del gas, que tiñe la pared, la maqueta, la cornisa y lo de encima.
                mancha(gas + Offset(-w * 0.1f, w * 0.08f), w * 0.78f, GAS.copy(alpha = 0.17f * fuerte))
                mancha(gas, w * 0.32f, GAS.copy(alpha = 0.34f * fuerte))
                mancha(gas, w * 0.09f, Color(0xFFFFF0C8).copy(alpha = 0.55f * fuerte))
                // La luz que sale por la boca del globo y sube por la pared.
                clipRect(0f, 0f, w, gas.y) { drawPath(poligonoVapor(gas.x - w * 0.02f, gas.y - w * 0.03f, gas.x + w * 0.02f, gas.y - w * 0.03f, gas.x + w * 0.07f, 0f, gas.x - w * 0.07f, 0f), GAS.copy(alpha = 0.08f * fuerte)) }
                if (a.lunar) { mancha(ven, w * 0.3f, LUNA.copy(alpha = 0.1f)); with(MuroEstanteVapor) { planoDeLuz(a, g) } }
            }
            else -> {
                mancha(ven, w * 0.36f, OCASO.copy(alpha = 0.18f))
                mancha(gas, w * 0.18f, GAS.copy(alpha = 0.26f))
                with(MuroEstanteVapor) { planoDeLuz(a, g) }
            }
        }
    }
}
