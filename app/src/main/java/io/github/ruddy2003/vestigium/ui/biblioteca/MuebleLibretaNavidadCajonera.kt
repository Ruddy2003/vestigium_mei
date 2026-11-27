package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que cambia en la cajonera de adviento de la libreta de Navidad ([MuebleLibretaNavidad]), en cada dibujo: los 24 cajoncitos
 * numerados en orden revuelto (cada casilla escrita abre uno, el de su número, con el papelito doblado asomando; con más casillas,
 * más papelitos y más altos), el cajón largo de abajo (pasadas las 24, guarda los papelitos atados en fajos y ya no cierra del
 * todo; al tocar la libreta, sale), y el nombre y la cuenta en la cartela del copete. Los números de los cajoncitos se miden una
 * sola vez por modo.
 */
internal object CajoneraLibretaNavidad {
    private val P = PaletaNavidad

    /** El orden revuelto de las 24 casillas, como en los calendarios de verdad (de izquierda a derecha y de arriba abajo). */
    private val ORDEN = intArrayOf(13, 2, 21, 7, 18, 10, 4, 23, 15, 1, 9, 20, 6, 17, 12, 24, 3, 14, 8, 22, 11, 5, 19, 16)

    /** Los números ya medidos (el medidor de la pantalla guarda solo 8 medidas): uno por tamaño de letra; se vacían si cambia la escala. */
    private val numeros = HashMap<String, TextLayoutResult>()
    private val claveNumeros = FloatArray(2)

    private fun DrawScope.numero(medidor: TextMeasurer, n: Int, caja: Rect, tinta: Color) {
        if (claveNumeros[0] != density || claveNumeros[1] != fontScale) { numeros.clear(); claveNumeros[0] = density; claveNumeros[1] = fontScale }
        val clave = "$n"
        val capa = numeros[clave] ?: (capaEn(medidor, clave, caja, P.LETRA, P.TINTA, 10f, peso = FontWeight.Bold, minimo = 6f) ?: return).also { numeros[clave] = it }
        drawText(capa, color = tinta, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    fun DrawScope.cajonera(e: EstadoLibreta, s: SitioLibretaNavidad, a: AireNavidad, medidor: TextMeasurer) {
        val U = s.U
        val abiertas = e.hojas.coerceIn(0, 24)
        val cw = s.rejilla.width / 6f; val ch = s.rejilla.height / 4f
        for (fila in 0 until 4) for (col in 0 until 6) {
            val i = fila * 6 + col
            val r = Rect(s.rejilla.left + col * cw + 1.4f * U, s.rejilla.top + fila * ch + 1.4f * U, s.rejilla.left + (col + 1) * cw - 1.4f * U, s.rejilla.top + (fila + 1) * ch - 1.4f * U)
            cajoncito(r, ORDEN[i], ORDEN[i] <= abiertas, s, a, medidor, e.semilla + i)
        }
        cajonLargo(e, s, a)
        // El nombre y la cuenta en la cartela del copete.
        val ct = s.cartela
        val k = MuebleLibretaNavidad.cerca(ct.center, s, a)
        val tinta = if (a.oscuro) Color(0xFF140C06) else P.TINTA
        textoEn(medidor, e.nombre, Rect(ct.left + 6f * U, ct.top + 4f * U, ct.right - 6f * U, ct.top + 22f * U), P.LETRA, tinta, 14f, peso = FontWeight.Bold, minimo = 8f)
        textoEn(medidor, e.cuenta, Rect(ct.left + 6f * U, ct.top + 21f * U, ct.right - 6f * U, ct.bottom - 4f * U), P.LETRA,
            if (a.oscuro) mezcla(Color(0xFF3A0E0A), P.CARMIN, 0.3f * k) else P.CARMIN, 9.5f, minimo = 6f)
    }

    /**
     * Un cajoncito en [r] con su [num]ero: el frente crema con su filete verde, el número pintado y el tirador de porcelana. Si está
     * [abierto], sale un poco: se ve lo hondo de su caja por encima del frente y el papelito doblado que asoma (más alto con más
     * casillas), y su sombra en el marco de abajo.
     */
    private fun DrawScope.cajoncito(r: Rect, num: Int, abierto: Boolean, s: SitioLibretaNavidad, a: AireNavidad, medidor: TextMeasurer, semilla: Int) {
        val U = s.U
        val k = MuebleLibretaNavidad.cerca(r.center, s, a)
        val baja = if (abierto) 2.8f * U else 0f
        val f = Rect(r.left, r.top + baja, r.right, r.bottom + baja)
        if (abierto) {
            // Lo hondo de la caja que sale, sus costados y el papelito (o dos) que asoma.
            val caja = Rect(f.left + 1f * U, f.top - 5.5f * U, f.right - 1f * U, f.top + 1f)
            drawRect(a.dia(mezcla(P.PINO_OSC, P.TINTA, 0.3f), 0f, k), caja.topLeft, caja.size)
            drawRect(a.dia(P.CREMA, -0.1f, k), caja.topLeft, Size(1.6f * U, caja.height))
            drawRect(a.dia(P.CREMA, -0.25f, k), Offset(caja.right - 1.6f * U, caja.top), Size(1.6f * U, caja.height))
            val papelitos = if (s.lleno > 0.6f && azar(semilla, 39) > 0.5f) 2 else 1
            for (j in 0 until papelitos) {
                val alto = (8f + 3f * s.lleno + 1.5f * azar(semilla, 40 + j)) * U
                val ancho = r.width * (0.5f - 0.1f * j)
                val x = f.left + r.width * (0.14f + 0.36f * j + 0.06f * azar(semilla, 42 + j))
                val giro = (azar(semilla, 44 + j) - 0.5f) * 18f
                rotate(giro, Offset(x + ancho / 2, caja.bottom)) {
                    val pap = Rect(x, caja.bottom - alto, x + ancho, caja.bottom)
                    drawRect(a.sombra(0.3f), pap.topLeft + Offset(1f * U, 1f * U), pap.size)
                    // El papelito doblado por la mitad: la hoja de delante, la de detrás asomando y el doblez arriba.
                    drawRect(a.dia(mezcla(P.PAPEL, P.CARTULINA, 0.35f), 0f, k), Offset(pap.left + 1.2f * U, pap.top - 1.2f * U), pap.size)
                    drawRect(a.dia(if (j == 0) P.PAPEL else mezcla(P.PAPEL, P.CARTULINA, 0.2f), 0f, k), pap.topLeft, pap.size)
                    drawRect(a.sombra(0.16f), Offset(pap.right - pap.width * 0.3f, pap.top), Size(pap.width * 0.3f, pap.height))
                    drawRect(a.brillo(0.5f, k), pap.topLeft, Size(pap.width, 0.8f * U))
                    if (j == 0 && azar(semilla, 46) > 0.6f) drawRect(a.dia(P.CARMIN, 0f, k), Offset(pap.left, pap.top + pap.height * 0.55f), Size(pap.width, 1.2f * U))
                }
            }
            drawRect(a.sombra(0.45f), Offset(f.left, f.bottom), Size(f.width, 2f * U))
        }
        // El frente: la cara, el filete verde, la luz arriba y la sombra abajo.
        val tono = mezcla(P.CREMA, if (num % 3 == 0) P.PAPEL else P.CARTULINA, 0.15f * azar(semilla, 1))
        val pf = Offset(f.left, f.center.y)
        drawRect(MuebleLibretaNavidad.conFrio(a.dia(tono, 0f, k), pf, s, a, 1.1f), f.topLeft, f.size)
        drawRect(MuebleLibretaNavidad.conFrio(a.dia(P.FILETE, 0f, k), pf, s, a), f.deflate(2f * U).topLeft, f.deflate(2f * U).size, style = Stroke(0.8f * U))
        drawRect(a.brillo(0.45f, k), f.topLeft, Size(f.width, 0.9f * U))
        drawRect(a.sombra(0.25f), Offset(f.left, f.bottom - 1.2f * U), Size(f.width, 1.2f * U))
        if (a.oscuro) {
            val fr = MuebleLibretaNavidad.frio(f.topLeft, s)
            if (fr > 0.05f) drawRect(a.filoFrio(0.45f * fr), f.topLeft, Size(1.2f * U, f.height))
            if (k > 0.25f) drawRect(a.filo(0.5f * k), Offset(f.right - 1.2f * U, f.top), Size(1.2f * U, f.height))
        }
        // El número pintado y el tirador de porcelana con su vástago de latón.
        // El número: de noche, oscuro y del color de su pintura, para que se lea también en el lado frío.
        val tintaNum = if (a.oscuro) mezcla(if (num % 2 == 0) P.CARMIN else P.FILETE, P.TINTA, 0.45f).let { mezcla(it, Color(0xFF3A1A0A), 0.25f * k) } else a.dia(if (num % 2 == 0) P.CARMIN else P.FILETE, 0f, k)
        numero(medidor, num, Rect(f.left + 2f * U, f.top + 2f * U, f.right - 2f * U, f.top + f.height * 0.62f), tintaNum)
        val t = Offset(f.center.x, f.bottom - f.height * 0.24f)
        drawCircle(a.sombra(0.35f), 2.2f * U, t + Offset(0.8f * U, 0.9f * U))
        drawCircle(a.dia(P.LOZA, 0f, maxOf(k, 0.1f)), 2f * U, t)
        drawCircle(a.brillo(0.75f, k), 0.7f * U, t - Offset(0.6f * U, 0.6f * U))
    }

    /**
     * El cajón largo de abajo, con sus dos tiradores: pasadas las 24 casillas guarda los papelitos atados en fajos (uno cada 24, hasta
     * cuatro) y ya no cierra del todo; al tocar la libreta sale entero.
     */
    private fun DrawScope.cajonLargo(e: EstadoLibreta, s: SitioLibretaNavidad, a: AireNavidad) {
        val U = s.U
        val fajos = ((e.hojas - 1) / 24).coerceIn(0, 4)
        val sale = maxOf(if (fajos > 0) 0.35f + 0.1f * fajos else 0f, e.abierto.coerceIn(0f, 1f))
        val r = Rect(s.rejilla.left + 1.4f * U, s.largo0 + 1.4f * U, s.rejilla.right - 1.4f * U, s.largo1 - 1.4f * U)
        val k = MuebleLibretaNavidad.cerca(r.center, s, a)
        val baja = 5f * U * sale
        val f = Rect(r.left - 1.5f * U * sale, r.top + baja, r.right + 1.5f * U * sale, r.bottom + baja + 1.5f * U * sale)
        if (sale > 0.01f) {
            // Lo hondo del cajón, por encima del frente, con los fajos atados de papelitos.
            val caja = Rect(f.left + 2f * U, f.top - 12f * U * sale, f.right - 2f * U, f.top + 1f)
            drawRect(a.dia(mezcla(P.PINO_OSC, P.TINTA, 0.3f), 0f, k), caja.topLeft, caja.size)
            drawRect(a.dia(P.CREMA, -0.1f, k), caja.topLeft, Size(2f * U, caja.height))
            drawRect(a.dia(P.CREMA, -0.25f, k), Offset(caja.right - 2f * U, caja.top), Size(2f * U, caja.height))
            val n = maxOf(fajos, if (e.abierto > 0.05f) 1 else 0)
            for (j in 0 until n) {
                val fx = caja.left + caja.width * (0.12f + 0.2f * j)
                val fw = caja.width * 0.17f
                val fh = (8f + 6f * sale) * U
                val fr = Rect(fx, caja.bottom - fh, fx + fw, caja.bottom)
                rotate((azar(e.semilla, 60 + j) - 0.5f) * 10f, Offset(fr.center.x, fr.bottom)) {
                    drawRect(a.sombra(0.3f), fr.topLeft + Offset(1.5f * U, 1f * U), fr.size)
                    for (q in 0 until 4) drawRect(a.dia(if (q % 2 == 0) P.PAPEL else P.CARTULINA, -0.04f * q, k), Offset(fr.left + (q - 1.5f) * 0.6f * U, fr.top + q * 1.6f * U), Size(fw, fh - q * 1.6f * U))
                    drawRect(a.dia(P.CARMIN, 0f, k), Offset(fr.left + fw * 0.62f, fr.top), Size(2f * U, fh))
                    drawCircle(a.dia(P.CARMIN, -0.1f, k), 2.2f * U, Offset(fr.left + fw * 0.62f + 1f * U, fr.top + fh * 0.3f))
                }
            }
            drawRect(a.sombra(0.4f), Offset(f.left, f.bottom), Size(f.width, 2.5f * U * (0.5f + sale)))
        }
        if (a.oscuro) {
            val n = 8
            drawRect(androidx.compose.ui.graphics.Brush.horizontalGradient(List(n + 1) { i ->
                val p = Offset(f.left + f.width * i / n, f.center.y)
                MuebleLibretaNavidad.conFrio(a.dia(P.CREMA, 0.02f, MuebleLibretaNavidad.cerca(p, s, a)), p, s, a, 1.1f)
            }, f.left, f.right), f.topLeft, f.size)
        } else drawRect(a.dia(P.CREMA, 0.02f, k), f.topLeft, f.size)
        drawRect(MuebleLibretaNavidad.conFrio(a.dia(P.FILETE, 0f, k), Offset(f.left, f.center.y), s, a), f.deflate(2.5f * U).topLeft, f.deflate(2.5f * U).size, style = Stroke(1f * U))
        drawRect(a.brillo(0.45f, k), f.topLeft, Size(f.width, 1f * U))
        drawRect(a.sombra(0.25f), Offset(f.left, f.bottom - 1.4f * U), Size(f.width, 1.4f * U))
        listOf(0.22f, 0.78f).forEach { fx ->
            val t = Offset(f.left + f.width * fx, f.center.y)
            val kt = MuebleLibretaNavidad.cerca(t, s, a)
            drawCircle(a.sombra(0.35f), 2.8f * U, t + Offset(1f * U, 1f * U))
            drawCircle(a.dia(P.LOZA, 0f, maxOf(kt, 0.1f)), 2.6f * U, t)
            drawCircle(a.dia(P.FILETE, 0f, kt), 1.3f * U, t, style = Stroke(0.5f * U))
            drawCircle(a.brillo(0.75f, kt), 0.8f * U, t - Offset(0.8f * U, 0.8f * U))
        }
        if (a.oscuro && k > 0.25f) drawRect(a.filo(0.5f * k), Offset(f.right - 1.2f * U, f.top), Size(1.2f * U, f.height))
    }
}
