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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo de la derecha y la izquierda del bodegón de Renacimiento, con su volumen: el farol de mano de hojalata con sus hojas de
 * asta (la luz de la tienda: de noche arde detrás del asta y tiñe el muro), la pila de libros sobre la que está, y la resma
 * envuelta en papel azul. También la marca de mercader inventada que llevan la resma y la bala del pie.
 */
internal object FarolEstanteRenacimiento {
    private val P = PaletaRenacimiento
    private val M = MuebleEstanteRenacimiento

    /** La hojalata del farol: su cara, su luz y lo hondo. */
    private val HOJALATA = Color(0xFF8D8F92)
    private val HOJALATA_LUZ = Color(0xFFD9DADB)
    private val HOJALATA_OSC = Color(0xFF45474D)
    /** El asta de las hojas del farol, apagada (de día) y encendida (de noche). */
    private val ASTA = Color(0xFFC9A46A)
    private val ASTA_ARDE = Color(0xFFFFD08A)

    /**
     * La marca de mercader inventada (no es letra, cifra ni símbolo conocido): un rombo abajo, un asta que sube de su punta,
     * un gallardete triangular lleno arriba a la derecha y dos puntos a los lados del asta.
     */
    fun DrawScope.marcaMercader(c: Offset, r: Float, color: Color) {
        val u = density
        val rombo = poligonoRenacimiento(c.x, c.y + r * 0.2f, c.x + r * 0.55f, c.y + r * 0.75f, c.x, c.y + r * 1.3f, c.x - r * 0.55f, c.y + r * 0.75f)
        drawPath(rombo, color, style = Stroke(1.2f * u))
        drawLine(color, Offset(c.x, c.y + r * 0.2f), Offset(c.x, c.y - r * 1.3f), 1.2f * u)
        drawPath(poligonoRenacimiento(c.x, c.y - r * 1.3f, c.x + r * 0.75f, c.y - r * 1.05f, c.x, c.y - r * 0.8f), color)
        drawCircle(color, r * 0.13f, Offset(c.x - r * 0.38f, c.y - r * 0.25f))
        drawCircle(color, r * 0.13f, Offset(c.x + r * 0.38f, c.y - r * 0.25f))
    }

    // ---------------------------------------------------------------- el farol

    /**
     * El farol de mano: la base redonda de hojalata, el cuerpo con sus costillas y sus dos hojas de asta (que de día dejan ver
     * la vela apagada y de noche arden), los zunchos de arriba y abajo, el sombrerete cónico con sus respiraderos y la anilla.
     * [pie] es donde pisa (encima de la pila de libros).
     */
    fun DrawScope.farol(pie: Offset, a: AireRenacimiento, t: TonoRenacimiento, hasta: Float) {
        val u = density
        val w = size.width
        val an = w * 0.08f
        val cuerpo = Rect(pie.x - an / 2, pie.y - w * 0.122f, pie.x + an / 2, pie.y - w * 0.017f)
        val sombrero = poligonoRenacimiento(cuerpo.left - w * 0.006f, cuerpo.top, cuerpo.right + w * 0.006f, cuerpo.top, pie.x + w * 0.008f, cuerpo.top - w * 0.035f,
            pie.x - w * 0.008f, cuerpo.top - w * 0.035f)
        val anilla = Offset(pie.x, cuerpo.top - w * 0.047f)
        val silueta = Path().apply { addRect(Rect(cuerpo.left, cuerpo.top, cuerpo.right, pie.y)); addPath(sombrero) }
        sombraEnMuroRenacimiento(silueta, cuerpo.center, hasta, a, 0.3f, 0.6f)
        contactoRenacimiento(Offset(pie.x + w * 0.008f, pie.y), an * 1.4f, 4f * u, a, 0.42f)
        val lata = t.c(HOJALATA)
        val lataOsc = t.c(HOJALATA_OSC)
        val lataLuz = t.c(HOJALATA_LUZ)
        // La base, con su reborde.
        drawOval(lataOsc, Offset(pie.x - an * 0.62f, pie.y - w * 0.008f), Size(an * 1.24f, w * 0.014f))
        drawRect(lata, Offset(pie.x - an * 0.56f, cuerpo.bottom), Size(an * 1.12f, pie.y - cuerpo.bottom - w * 0.002f))
        drawRect(lataLuz, Offset(pie.x - an * 0.56f, cuerpo.bottom), Size(an * 1.12f, 0.9f * u))
        drawRect(lataOsc, Offset(pie.x + an * 0.25f, cuerpo.bottom), Size(an * 0.31f, pie.y - cuerpo.bottom - w * 0.002f))
        // Las hojas de asta: de día, translúcidas, con la vela apagada detrás; de noche, encendidas desde dentro.
        val hojas = Rect(cuerpo.left + an * 0.08f, cuerpo.top + w * 0.008f, cuerpo.right - an * 0.08f, cuerpo.bottom - w * 0.006f)
        if (!a.lumbre) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(ASTA, Color.White, 0.25f), ASTA, mezcla(ASTA, P.NOGAL_OSC, 0.4f)), hojas.left, hojas.right), hojas.topLeft, hojas.size)
            val vela = Rect(pie.x - an * 0.09f, hojas.top + hojas.height * 0.42f, pie.x + an * 0.09f, hojas.bottom)
            drawRect(mezcla(ASTA, P.NOGAL_OSC, 0.25f), vela.topLeft, vela.size)
            drawLine(mezcla(ASTA, P.IMPRENTA, 0.6f), Offset(pie.x, vela.top), Offset(pie.x, vela.top - hojas.height * 0.08f), 1f * u)
        } else {
            val arde = if (a.oscuro) 1f else 0.85f
            drawRect(Brush.radialGradient(listOf(P.FUEGO, mezcla(ASTA_ARDE, P.LLAMA, 0.3f), mezcla(P.LLAMA, ASTA, 0.35f)), Offset(pie.x, hojas.top + hojas.height * 0.55f),
                hojas.height * 0.75f), hojas.topLeft, hojas.size, alpha = arde)
            val vela = Rect(pie.x - an * 0.09f, hojas.top + hojas.height * 0.5f, pie.x + an * 0.09f, hojas.bottom)
            drawRect(mezcla(ASTA_ARDE, P.LLAMA, 0.5f), vela.topLeft, vela.size)
            val llama = Path().apply {
                val b = Offset(pie.x, vela.top)
                moveTo(b.x - an * 0.08f, b.y)
                quadraticTo(b.x - an * 0.1f, b.y - hojas.height * 0.18f, b.x, b.y - hojas.height * 0.34f)
                quadraticTo(b.x + an * 0.1f, b.y - hojas.height * 0.18f, b.x + an * 0.08f, b.y)
                close()
            }
            drawPath(llama, Color.White.copy(alpha = 0.85f))
        }
        // Las vetas del asta y la marca de las costillas.
        val vetas = Path()
        for (j in 0 until 3) { val y = hojas.top + hojas.height * (0.25f + 0.25f * j) + (azar(881, j) - 0.5f) * 2f * u; vetas.addRect(Rect(hojas.left, y, hojas.right, y + 0.5f * u)) }
        drawPath(vetas, mezcla(ASTA, P.NOGAL_OSC, 0.5f).copy(alpha = if (a.lumbre) 0.25f else 0.35f))
        // Las costillas de hojalata (los dos cantos y la del medio) y los zunchos.
        val costillas = Path().apply {
            addRect(Rect(cuerpo.left, cuerpo.top, cuerpo.left + an * 0.09f, cuerpo.bottom))
            addRect(Rect(cuerpo.right - an * 0.09f, cuerpo.top, cuerpo.right, cuerpo.bottom))
            addRect(Rect(pie.x - an * 0.04f, cuerpo.top, pie.x + an * 0.04f, cuerpo.bottom))
            addRect(Rect(cuerpo.left - w * 0.002f, cuerpo.top, cuerpo.right + w * 0.002f, cuerpo.top + w * 0.009f))
            addRect(Rect(cuerpo.left - w * 0.002f, cuerpo.bottom - w * 0.007f, cuerpo.right + w * 0.002f, cuerpo.bottom))
        }
        drawPath(costillas, lata)
        drawRect(lataLuz, Offset(cuerpo.left, cuerpo.top), Size(an * 0.03f, cuerpo.height))
        drawRect(lataOsc, Offset(cuerpo.right - an * 0.05f, cuerpo.top), Size(an * 0.05f, cuerpo.height))
        drawRect(lataLuz, Offset(cuerpo.left, cuerpo.top), Size(an, 0.8f * u))
        // El sombrerete con sus respiraderos y la anilla.
        drawPath(sombrero, lata)
        drawPath(poligonoRenacimiento(pie.x, cuerpo.top, cuerpo.right + w * 0.006f, cuerpo.top, pie.x + w * 0.008f, cuerpo.top - w * 0.035f, pie.x, cuerpo.top - w * 0.035f), lataOsc)
        drawLine(lataLuz, Offset(cuerpo.left - w * 0.004f, cuerpo.top - 0.5f * u), Offset(pie.x - w * 0.007f, cuerpo.top - w * 0.034f), 0.8f * u)
        val respiraderos = Path()
        for (j in 0 until 4) {
            val x = pie.x - an * 0.3f + an * 0.2f * j
            respiraderos.addOval(Rect(x - 0.9f * u, cuerpo.top - w * 0.013f, x + 0.9f * u, cuerpo.top - w * 0.013f + 1.6f * u))
        }
        drawPath(respiraderos, if (a.lumbre) P.FUEGO.copy(alpha = if (a.oscuro) 0.95f else 0.75f) else t.s(0.6f))
        drawCircle(lata, w * 0.012f, anilla, style = Stroke(1.6f * u))
        drawArc(lataLuz, 170f, 90f, false, anilla - Offset(w * 0.012f, w * 0.012f), Size(w * 0.024f, w * 0.024f), style = Stroke(0.8f * u))
        destelloRenacimiento(Offset(cuerpo.left + an * 0.05f, cuerpo.top + cuerpo.height * 0.3f), 0.9f * u, t)
        if (a.lumbre) drawRect(t.f(0.8f), Offset(cuerpo.right - 1.2f * u, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    /** El halo del farol encendido, encima de todo: el muro se tiñe de su luz. */
    fun DrawScope.haloFarol(a: AireRenacimiento) {
        val w = size.width
        val c = Offset(w * M.LAMP_X, w * M.LAMP_Y)
        mancha(c, w * 0.22f, P.LLAMA.copy(alpha = if (a.oscuro) 0.4f else 0.28f))
        mancha(c, w * 0.07f, P.FUEGO.copy(alpha = if (a.oscuro) 0.5f else 0.35f))
    }

    // ---------------------------------------------------------------- la pila de libros

    /**
     * La pila de libros tumbados bajo el farol, de cueros distintos: de cada uno se ven las tapas con su grueso (la luz en el
     * canto de arriba), el corte de las hojas con su comba y sus renglones, y del de arriba la tapa con su filete dorado y su
     * florón. Su sombra va en el muro. Devuelve dónde pisa lo de encima.
     */
    fun DrawScope.pilaLibros(pie: Offset, an: Float, a: AireRenacimiento, t: TonoRenacimiento, hasta: Float): Float {
        val u = density
        val cueros = listOf(mezcla(P.RUBIA, P.CUERO, 0.35f), mezcla(P.VERDE_FONDO, P.CUERO, 0.15f), mezcla(P.AZUL, P.CUERO, 0.3f))
        val gruesos = listOf(0.17f, 0.14f, 0.12f)
        val total = gruesos.sum() * an
        val cara = an * 0.1f
        sombraEnMuroRenacimiento(poligonoRenacimiento(pie.x - an / 2, pie.y, pie.x + an / 2, pie.y, pie.x + an / 2, pie.y - total - cara, pie.x - an / 2, pie.y - total - cara),
            Offset(pie.x, pie.y - total), hasta, a, 0.28f, 0.6f)
        contactoRenacimiento(Offset(pie.x + an * 0.04f, pie.y), an * 1.15f, 5f * u, a, 0.45f)
        var y = pie.y
        var xUlt = pie.x - an / 2; var lwUlt = an
        cueros.forEachIndexed { j, col ->
            val g = an * gruesos[j]
            val lw = an * (1f - 0.07f * j)
            val x = pie.x - lw / 2 + (azar(77, j) - 0.5f) * an * 0.08f
            val tapa = g * 0.2f
            // Las dos tapas (abajo y arriba) y su canto de luz.
            drawRect(t.c(col), Offset(x, y - g), Size(lw, g))
            drawRect(t.b(0.35f), Offset(x, y - g), Size(lw, 0.9f * u))
            drawRect(t.s(0.3f), Offset(x, y - tapa), Size(lw, tapa))
            // El corte de las hojas, metido entre las tapas, con su comba (más oscuro en el medio) y sus renglones.
            val corte = Rect(x + lw * 0.025f, y - g + tapa, x + lw * 0.975f, y - tapa)
            drawRect(t.c(P.PAPEL_OSC), corte.topLeft, corte.size)
            drawRect(t.s(0.12f), Offset(corte.left, corte.center.y - corte.height * 0.2f), Size(corte.width, corte.height * 0.4f))
            val reng = Path()
            for (l in 1..3) reng.addRect(Rect(corte.left, corte.top + corte.height * l / 4f, corte.right, corte.top + corte.height * l / 4f + 0.4f * u))
            drawPath(reng, t.s(0.2f))
            if (j == 1) drawRect(t.c(P.ORO).copy(alpha = 0.7f), corte.topLeft, Size(corte.width, 0.7f * u))
            // El lado de la sombra y el filo del farol.
            drawRect(t.s(0.3f), Offset(x + lw * 0.8f, y - g), Size(lw * 0.2f, g))
            drawRect(t.f(0.7f), Offset(x, y - g), Size(lw, 0.9f * u))
            y -= g; xUlt = x; lwUlt = lw
        }
        // La tapa del de arriba, vista un poco desde arriba, con su filete dorado y el florón.
        val tapaArriba = poligonoRenacimiento(xUlt, y, xUlt + lwUlt, y, xUlt + lwUlt - an * 0.03f, y - cara, xUlt + an * 0.03f, y - cara)
        drawPath(tapaArriba, t.c(mezcla(cueros.last(), Color.White, 0.08f)))
        drawPath(tapaArriba, t.b(0.2f))
        val m = an * 0.06f
        drawPath(poligonoRenacimiento(xUlt + m, y - cara * 0.2f, xUlt + lwUlt - m, y - cara * 0.2f, xUlt + lwUlt - m - an * 0.02f, y - cara * 0.8f, xUlt + m + an * 0.02f, y - cara * 0.8f),
            t.c(P.ORO).copy(alpha = 0.8f), style = Stroke(0.6f * u))
        drawOval(t.c(P.ORO), Offset(xUlt + lwUlt / 2 - an * 0.03f, y - cara * 0.65f), Size(an * 0.06f, cara * 0.3f))
        return y - cara * 0.5f
    }

    // ---------------------------------------------------------------- la resma

    /**
     * La resma de papel envuelta en papel azul: el frente con su luz y su sombra, la cara de arriba con las solapas del
     * envoltorio dobladas, el testero de la derecha donde se ven los cantos de las hojas, el bramante en cruz con su nudo, la
     * marca del papelero (inventada) y las tijeras encima; su sombra en el muro.
     */
    fun DrawScope.resma(pie: Offset, an: Float, a: AireRenacimiento, t: TonoRenacimiento, hasta: Float) {
        val u = density
        val al = an * 0.4f
        val cara = an * 0.16f
        val fondo = an * 0.1f
        val r = Rect(pie.x, pie.y - al, pie.x + an - fondo, pie.y)
        val silueta = poligonoRenacimiento(r.left, r.bottom, r.right + fondo, r.bottom - cara * 0.4f, r.right + fondo, r.top - cara, r.left + fondo, r.top - cara, r.left, r.top)
        sombraEnMuroRenacimiento(silueta, r.center, hasta, a, 0.3f, 0.6f)
        contactoRenacimiento(Offset(r.center.x + an * 0.06f, pie.y), an * 1.15f, 5f * u, a, 0.42f)
        val azul = t.c(mezcla(P.AZUL, P.PAPEL_OSC, 0.25f))
        // La cara de arriba, con las solapas del envoltorio que se doblan hacia el centro.
        val arriba = poligonoRenacimiento(r.left, r.top, r.right, r.top, r.right + fondo, r.top - cara, r.left + fondo, r.top - cara)
        drawPath(arriba, mezcla(azul, Color.White, 0.22f))
        drawPath(arriba, t.b(0.2f))
        drawLine(t.s(0.25f), Offset(r.left + fondo * 0.5f, r.top - cara * 0.5f), Offset(r.left + an * 0.3f, r.top - cara * 0.5f), 0.6f * u)
        drawLine(t.s(0.25f), Offset(r.right + fondo * 0.5f, r.top - cara * 0.5f), Offset(r.right - an * 0.25f, r.top - cara * 0.5f), 0.6f * u)
        // El testero: los cantos de las hojas entre los dos pliegues del papel azul.
        val testero = poligonoRenacimiento(r.right, r.top, r.right + fondo, r.top - cara, r.right + fondo, r.bottom - cara, r.right, r.bottom)
        drawPath(testero, t.c(P.PAPEL))
        clipPath(testero) {
            val hojas = Path()
            for (l in 1..9) { val y = r.top - cara + (al + cara) * l / 10f; hojas.addRect(Rect(r.right, y, r.right + fondo, y + 0.45f * u)) }
            drawPath(hojas, t.s(0.25f))
            drawRect(t.s(0.22f), Offset(r.right, r.top - cara), Size(fondo, al + cara))
            drawPath(poligonoRenacimiento(r.right, r.top, r.right + fondo, r.top - cara, r.right + fondo, r.top - cara + al * 0.25f, r.right, r.top + al * 0.3f), azul)
            drawPath(poligonoRenacimiento(r.right, r.bottom, r.right + fondo, r.bottom - cara, r.right + fondo, r.bottom - cara - al * 0.2f, r.right, r.bottom - al * 0.25f), mezcla(azul, a.hondo, 0.2f))
        }
        // El frente: la luz a la izquierda, la sombra a la derecha y la arruga del papel.
        drawRect(azul, r.topLeft, r.size)
        drawRect(t.b(0.22f), r.topLeft, Size(r.width * 0.22f, r.height))
        drawRect(t.s(0.22f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
        drawLine(t.s(0.25f), Offset(r.left + an * 0.18f, r.top + al * 0.1f), Offset(r.left + an * 0.24f, r.bottom), 0.6f * u)
        drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.9f * u))
        // El bramante en cruz: por el frente y por la cara de arriba, con el nudo.
        val cana = t.c(P.CANAMO)
        val xb = r.left + r.width * 0.46f
        drawLine(cana, Offset(r.left, r.center.y), Offset(r.right, r.center.y), 1.3f * u)
        drawLine(cana, Offset(r.right, r.center.y), Offset(r.right + fondo, r.center.y - cara), 1.2f * u)
        drawLine(cana, Offset(xb, r.top), Offset(xb, r.bottom), 1.3f * u)
        drawLine(cana, Offset(xb, r.top), Offset(xb + fondo, r.top - cara), 1.2f * u)
        drawCircle(mezcla(cana, a.hondo, 0.3f), 2f * u, Offset(xb, r.center.y))
        // La marca del papelero, estampada.
        marcaMercader(Offset(r.left + an * 0.2f, r.center.y + al * 0.02f), al * 0.22f, t.c(P.SEPIA).copy(alpha = 0.7f))
        // Las tijeras de hierro sobre la cara de arriba, con su sombra.
        val c = Offset(r.left + an * 0.6f, r.top - cara * 0.45f)
        val hierro = t.c(P.HIERRO)
        drawLine(a.sombra(0.4f), Offset(c.x - an * 0.26f, c.y + 2.4f * u), Offset(c.x + an * 0.24f, c.y + 1.8f * u), 1.8f * u)
        drawLine(hierro, c, Offset(c.x + an * 0.26f, c.y - 2.8f * u), 1.8f * u, StrokeCap.Round)
        drawLine(hierro, c, Offset(c.x + an * 0.26f, c.y + 0.6f * u), 1.5f * u, StrokeCap.Round)
        drawOval(hierro, Offset(c.x - an * 0.24f, c.y - 3.6f * u), Size(an * 0.11f, 4.4f * u), style = Stroke(1.4f * u))
        drawOval(hierro, Offset(c.x - an * 0.14f, c.y - 2.4f * u), Size(an * 0.11f, 3.8f * u), style = Stroke(1.4f * u))
        destelloRenacimiento(Offset(c.x + an * 0.1f, c.y - 1.6f * u), 0.9f * u, t)
    }
}
