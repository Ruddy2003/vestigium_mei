package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * La regla de la casa de la libreta de Templario ([MuebleLibretaTemplario]), tendida en el atril: tablas forradas de badana
 * teñida de rubia, con el gofrado en seco (tres filetes y el rombo de en medio, con sus anillos de punzón), cantoneras y cinco
 * bullones de latón, dos cierres de correa que vienen de la tabla de abajo por el canto, el lomo con sus nervios a la
 * izquierda y, arriba, el tejuelo de pergamino bajo su lámina de cuerno en un marco de latón, con el nombre y la cuenta. Por
 * debajo se ve el grueso (la testa, con el bloque de las hojas), que engorda con los artículos; asoman cédulas por la cabeza y
 * por el canto, y cuelgan más registros sobre el paño. Al abrirse, la tapa gira sobre el lomo y deja ver la primera hoja.
 */
internal object ReglaLibretaTemplario {
    private val P = PaletaTemplario
    private val BADANA = Color(0xFF7E2F22)
    private val BADANA_LUZ = Color(0xFFB0553A)
    private val BADANA_HONDA = Color(0xFF3E140D)
    private val CUERNO_LAMINA = Color(0xFFE8D7A4)

    private fun k(p: Offset, s: SitioLibretaTemplario, a: AireTemplario) = MuebleLibretaTemplario.cerca(p, s, a)

    fun DrawScope.regla(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario, medidor: TextMeasurer) {
        val U = s.U
        val l = s.libro
        val ab = e.abierto.coerceIn(0f, 1f)
        val kc = k(l.center, s, a)
        // La sombra de la regla en el atril: de día a la derecha; de noche, lejos de los mecheros (a la izquierda).
        clipRect(0f, 0f, s.w, s.listonArriba) {
            val d = if (a.oscuro) Offset(-9f * U, 3f * U) else Offset(8f * U, 5f * U)
            drawRect(a.sombra(0.42f), l.topLeft + d, Size(l.width + 6f * U, l.height + s.g))
        }
        cedulas(e, s, a)
        testa(s, a)
        lomo(s, a)
        if (ab > 0.01f) hoja(e, s, a)
        val sx = 1f - 0.94f * ab
        scale(sx, 1f, Offset(l.left, l.center.y)) {
            tapa(e, s, a, medidor)
            if (ab > 0f) drawRect(a.sombra(0.5f * ab), l.topLeft, l.size)
        }
        if (ab > 0.01f) {
            val borde = l.left + l.width * sx
            for (j in 0 until 3) drawRect(a.sombra(0.12f * (3 - j) / 3f), Offset(borde, l.top), Size((3f + 4f * j) * U, l.height))
        }
        registros(e, s, a)
        if (a.oscuro && ab < 0.5f) drawRect(a.filo(0.75f * kc), Offset(l.right - 1.6f * U, l.top + 2f * U), Size(1.6f * U, l.height - 4f * U))
    }

    /** Las cédulas que asoman por la cabeza y por el canto de delante: más cuantos más artículos. */
    private fun DrawScope.cedulas(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val n = (e.hojas / 28).coerceIn(0, 6)
        for (j in 0 until n) {
            val arriba = j % 2 == 0
            val f = azar(e.semilla, 40 + j)
            val r = if (arriba) Rect(l.left + l.width * (0.18f + 0.62f * f), l.top - (6f + 6f * azar(e.semilla, 50 + j)) * U, l.left + l.width * (0.18f + 0.62f * f) + (12f + 6f * azar(e.semilla, 60 + j)) * U, l.top + 4f * U)
                else Rect(l.right - 4f * U, l.top + l.height * (0.2f + 0.6f * f), l.right + (6f + 6f * azar(e.semilla, 50 + j)) * U, l.top + l.height * (0.2f + 0.6f * f) + 11f * U)
            val kk = k(r.center, s, a)
            drawRect(a.sombra(0.3f), r.topLeft + Offset(1f * U, 1.2f * U), r.size)
            drawRect(a.etiqueta(mezcla(P.PERGAMINO, P.PERG_OSC, 0.25f * f), kk), r.topLeft, r.size)
            drawRect(a.sombra(0.2f), r.topLeft, r.size, style = Stroke(0.6f * U))
        }
    }

    /**
     * El grueso de la regla, en escorzo: por debajo (la testa) y por la derecha (el canto de delante), el bloque de las hojas
     * entre el canto de la tabla de arriba y el de la de abajo. Engorda con los artículos.
     */
    private fun DrawScope.testa(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val vx = 0.32f * s.g; val vy = s.listonArriba + 0.5f * U - l.bottom
        val abajo = poligonoTemplario(l.left, l.bottom - 1f * U, l.right, l.bottom - 1f * U, l.right + vx, l.bottom + vy, l.left + vx, l.bottom + vy)
        val lado = poligonoTemplario(l.right - 1f * U, l.top + 1f * U, l.right + vx, l.top + vy * 0.9f, l.right + vx, l.bottom + vy, l.right - 1f * U, l.bottom)
        val kk = k(Offset(l.right, l.bottom), s, a)
        val perg = mezcla(P.PERGAMINO, P.PERG_OSC, 0.3f)
        drawPath(abajo, a.etiqueta(perg, k(Offset(l.center.x, l.bottom), s, a)))
        drawPath(lado, a.etiqueta(mezcla(perg, P.PERG_OSC, 0.3f), kk))
        // Las hojas: rayas finas a lo largo de cada cara.
        val rayas = Path()
        var f = 0.18f
        while (f < 0.9f) {
            rayas.moveTo(l.left + vx * f, l.bottom + vy * f); rayas.lineTo(l.right + vx * f, l.bottom + vy * f)
            rayas.moveTo(l.right + vx * f, l.top + vy * 0.9f * f + 2f * U); rayas.lineTo(l.right + vx * f, l.bottom + vy * f)
            f += 1.4f * U / maxOf(vy, 1f)
        }
        drawPath(rayas, a.sombra(0.22f), style = Stroke(0.5f * U))
        // El canto de la tabla de abajo, y la sombra que echa la de arriba en las hojas.
        val tabla = poligonoTemplario(l.left + vx, l.bottom + vy - 2.4f * U, l.right + vx, l.bottom + vy - 2.4f * U, l.right + vx, l.bottom + vy, l.left + vx, l.bottom + vy)
        drawPath(tabla, a.dia(BADANA_HONDA, 0f, kk))
        drawRect(a.dia(BADANA_HONDA, 0f, kk), Offset(l.right + vx - 2.2f * U, l.top + vy * 0.9f), Size(2.2f * U, l.bottom - l.top))
        drawRect(a.sombra(0.4f), Offset(l.left, l.bottom - 1f * U), Size(l.width, 1.6f * U))
    }

    /** El lomo a la izquierda: la badana redonda con sus nervios y la cofia. */
    private fun DrawScope.lomo(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val r = Rect(l.left - 7f * U, l.top + 2f * U, l.left + 1f * U, s.listonArriba - 0.5f * U)
        val kk = k(r.center, s, a)
        drawRect(a.dia(BADANA, -0.2f, kk), r.topLeft, r.size)
        drawRect(a.dia(BADANA_LUZ, -0.05f, kk), r.topLeft, Size(r.width * 0.4f, r.height))
        drawRect(a.sombra(0.35f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
        listOf(0.22f, 0.42f, 0.62f, 0.82f).forEach { f ->
            val y = r.top + r.height * f
            drawRect(a.dia(BADANA_HONDA, 0f, kk), Offset(r.left, y), Size(r.width, 3f * U))
            drawRect(a.claro(0.3f), Offset(r.left, y - 0.6f * U), Size(r.width, 0.8f * U))
        }
    }

    /** La primera hoja, al abrirse: el pergamino con su pauta a dos columnas, las rúbricas rojas y la capital en su cuadro. */
    private fun DrawScope.hoja(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val pag = s.libro.deflate(3f * U)
        drawRect(a.etiqueta(P.PERGAMINO, k(pag.center, s, a)), pag.topLeft, pag.size)
        drawRect(a.sombra(0.2f), pag.topLeft, Size(10f * U, pag.height))
        val m = Rect(pag.left + 16f * U, pag.top + 12f * U, pag.right - 12f * U, pag.bottom - 12f * U)
        val col = (m.width - 10f * U) / 2
        val tinta = Path(); val rojo = Path()
        for (q in 0..1) {
            val x0 = m.left + q * (col + 10f * U)
            var ry = m.top + 4f * U; var j = 0
            while (ry < m.bottom) {
                val rubrica = j % 7 == 0
                var x = x0 + (if (rubrica || (q == 0 && j < 3)) (if (q == 0 && j < 3) 14f * U else 0f) else 3f * U); var kk = 0
                while (x < x0 + col - 3f * U) {
                    val largo = minOf((4f + 9f * azar(e.semilla + q * 40 + j, kk)) * U, x0 + col - x)
                    (if (rubrica) rojo else tinta).apply { moveTo(x, ry); lineTo(x + largo, ry) }
                    x += largo + 2.2f * U; kk++
                }
                ry += 5.5f * U; j++
            }
        }
        drawPath(tinta, a.luz(P.TINTA, 1f).copy(alpha = 0.55f), style = Stroke(1.2f * U))
        drawPath(rojo, a.dia(P.CRUZ, 0f, 1f), style = Stroke(1.2f * U))
        // La capital: un cuadro rojo con su filigrana de pluma (sin letra).
        val cap = Rect(m.left, m.top, m.left + 12f * U, m.top + 13f * U)
        drawRect(a.dia(P.CRUZ, 0f, 1f), cap.topLeft, cap.size)
        drawRect(a.dia(P.AZUL, 0f, 1f), cap.deflate(3f * U).topLeft, cap.deflate(3f * U).size)
    }

    /** La tapa: la badana, su gofrado, las cantoneras y los bullones, los cierres y el tejuelo bajo su cuerno. */
    private fun DrawScope.tapa(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario, medidor: TextMeasurer) {
        val U = s.U
        val l = s.libro
        // La badana, por tramos a lo ancho de noche (la luz de los mecheros viene de la derecha).
        val n = if (a.oscuro) 12 else 1
        for (t in 0 until n) {
            val x0 = l.left + l.width * t / n; val x1 = l.left + l.width * (t + 1) / n + 1.2f
            val kk = k(Offset((x0 + x1) / 2, l.center.y), s, a)
            drawRect(a.dia(BADANA, 0f, kk), Offset(x0, l.top), Size(minOf(x1, l.right) - x0, l.height))
        }
        val kc = k(l.center, s, a)
        // La lanceta de la ventana en la tapa: la misma badana, alumbrada (el sol de día; la luna, fría, de noche), con su
        // borde, cortada por los bordes de la tabla. Va antes del gofrado y los herrajes, que quedan encima.
        clipRect(l.left, l.top, l.right, l.bottom) {
            clipPath(s.lanceta(a.oscuro, -5f * U)) {
                val base = a.dia(BADANA, 0f, k(Offset(l.left + 20f * U, l.top + 40f * U), s, a))
                drawRect(a.sol(BADANA, base, if (a.oscuro) 0.55f else 0.85f), l.topLeft, l.size)
            }
        }
        // El volumen de la tabla: más clara arriba a la izquierda (la luz), más honda abajo a la derecha.
        if (!a.oscuro) {
            drawRect(a.claro(0.12f), l.topLeft, Size(l.width * 0.35f, l.height))
            drawRect(a.sombra(0.14f), Offset(l.right - l.width * 0.3f, l.top), Size(l.width * 0.3f, l.height))
        }
        drawRect(a.sombra(0.18f), Offset(l.left, l.bottom - l.height * 0.18f), Size(l.width, l.height * 0.18f))
        // El roce del uso: las esquinas y el canto más claros, rozaduras, una mancha de agua.
        val roces = Path()
        for (j in 0 until 9) {
            val p = Offset(l.left + l.width * azar(8600, j), l.top + l.height * azar(8601, j))
            roces.moveTo(p.x, p.y); roces.lineTo(p.x + (8f + 14f * azar(8602, j)) * U, p.y + (azar(8603, j) - 0.5f) * 4f * U)
        }
        drawPath(roces, a.dia(BADANA_LUZ, 0.1f, kc).copy(alpha = 0.5f), style = Stroke(0.9f * U, cap = StrokeCap.Round))
        drawOval(a.sombra(0.12f), Offset(l.left + l.width * 0.55f, l.top + l.height * 0.6f), Size(l.width * 0.26f, l.height * 0.2f))
        drawOval(a.sombra(0.12f), Offset(l.left + l.width * 0.55f, l.top + l.height * 0.6f), Size(l.width * 0.26f, l.height * 0.2f), style = Stroke(1f * U))
        // El gofrado en seco: tres filetes alrededor, el rombo de en medio y los anillos de punzón (sin cruces ni letras).
        val hondo = a.dia(BADANA_HONDA, 0f, kc).copy(alpha = 0.8f)
        val claro = a.dia(BADANA_LUZ, 0.05f, kc).copy(alpha = 0.55f)
        val gof = Path()
        listOf(8f, 11f, 16f).forEach { d -> gof.addRect(l.deflate(d * U)) }
        val in0 = l.deflate(16f * U)
        val rombo = Path().apply {
            moveTo(in0.center.x, in0.top + 50f * U); lineTo(in0.right - 8f * U, in0.center.y + 18f * U)
            lineTo(in0.center.x, in0.bottom - 6f * U); lineTo(in0.left + 8f * U, in0.center.y + 18f * U); close()
        }
        gof.addPath(rombo)
        translate(0.7f * U, 0.8f * U) { drawPath(gof, claro, style = Stroke(0.8f * U)) }
        drawPath(gof, hondo, style = Stroke(1f * U))
        val anillos = Path()
        val r11 = l.deflate(13.5f * U)
        var x = r11.left + 6f * U
        while (x < r11.right - 4f * U) { anillos.addOval(Rect(x - 1.4f * U, r11.top - 1.4f * U, x + 1.4f * U, r11.top + 1.4f * U)); anillos.addOval(Rect(x - 1.4f * U, r11.bottom - 1.4f * U, x + 1.4f * U, r11.bottom + 1.4f * U)); x += 9f * U }
        var y = r11.top + 8f * U
        while (y < r11.bottom - 4f * U) { anillos.addOval(Rect(r11.left - 1.4f * U, y - 1.4f * U, r11.left + 1.4f * U, y + 1.4f * U)); anillos.addOval(Rect(r11.right - 1.4f * U, y - 1.4f * U, r11.right + 1.4f * U, y + 1.4f * U)); y += 9f * U }
        drawPath(anillos, hondo, style = Stroke(0.8f * U))
        // Los cantos de la tabla: el filete de luz arriba y a la izquierda, la sombra abajo y a la derecha.
        drawRect(a.brillo(0.45f, kc), l.topLeft, Size(l.width, 1.4f * U))
        if (!a.oscuro) drawRect(a.claro(0.25f), l.topLeft, Size(1.4f * U, l.height))
        drawRect(a.sombra(0.45f), Offset(l.left, l.bottom - 1.8f * U), Size(l.width, 1.8f * U))
        drawRect(a.sombra(0.35f), Offset(l.right - 1.6f * U, l.top), Size(1.6f * U, l.height))
        // Las cantoneras de latón, con sus clavos.
        val e0 = 22f * U
        listOf(Offset(l.left, l.top) to Offset(1f, 1f), Offset(l.right, l.top) to Offset(-1f, 1f), Offset(l.right, l.bottom) to Offset(-1f, -1f), Offset(l.left, l.bottom) to Offset(1f, -1f)).forEach { (o, d) ->
            val kk = k(o, s, a)
            val c = Path().apply { moveTo(o.x, o.y); lineTo(o.x + d.x * e0, o.y); quadraticTo(o.x + d.x * e0 * 0.45f, o.y + d.y * e0 * 0.45f, o.x, o.y + d.y * e0); close() }
            drawPath(Path().apply { addPath(c, Offset(1.2f * U, 1.4f * U)) }, a.sombra(0.35f))
            drawPath(c, a.dia(P.LATON, 0f, kk))
            drawPath(c, a.dia(P.LATON_OSC, 0f, kk), style = Stroke(0.8f * U))
            listOf(0.22f to 0.22f, 0.62f to 0.12f, 0.12f to 0.62f).forEach { (fx, fy) -> clavoTemplario(Offset(o.x + d.x * e0 * fx, o.y + d.y * e0 * fy), 1.2f * U, a, kk, P.LATON_OSC) }
        }
        // Los cierres: dos correas que vienen de la tabla de abajo por el canto y enganchan en su chapa.
        listOf(0.36f, 0.78f).forEach { f ->
            val yc = l.top + l.height * f
            val kk = k(Offset(l.right, yc), s, a)
            val correa = Rect(l.right - 30f * U, yc - 4.5f * U, l.right + 3f * U, yc + 4.5f * U)
            drawRect(a.sombra(0.4f), correa.topLeft + Offset(1f * U, 1.6f * U), correa.size)
            drawRect(a.dia(P.CUERO_OSC, 0f, kk), correa.topLeft, correa.size)
            drawRect(a.brillo(0.25f, kk), correa.topLeft, Size(correa.width, 0.8f * U))
            val chapa = Rect(correa.left - 9f * U, yc - 6f * U, correa.left + 4f * U, yc + 6f * U)
            drawRect(a.sombra(0.35f), chapa.topLeft + Offset(1f * U, 1.2f * U), chapa.size)
            drawRect(a.dia(P.LATON, 0f, kk), chapa.topLeft, chapa.size)
            drawRect(a.dia(P.LATON_OSC, 0f, kk), chapa.topLeft, chapa.size, style = Stroke(0.8f * U))
            drawRect(a.brillo(0.5f, kk), chapa.topLeft, Size(chapa.width, 0.8f * U))
            clavoTemplario(Offset(chapa.left + 3f * U, yc), 1.6f * U, a, kk, P.LATON_OSC)
        }
        // Los cinco bullones: cuatro cerca de las esquinas y uno en medio del rombo.
        val bullones = listOf(Offset(l.left + 27f * U, l.top + 34f * U), Offset(l.right - 27f * U, l.top + 34f * U), Offset(l.left + 30f * U, l.bottom - 30f * U),
            Offset(l.right - 30f * U, l.bottom - 30f * U), Offset(in0.center.x, in0.center.y + 18f * U))
        bullones.forEach { b ->
            val kk = k(b, s, a)
            drawCircle(a.sombra(0.4f), 8.5f * U, b + Offset(1.6f * U, 2f * U))
            drawCircle(a.dia(P.LATON_OSC, 0f, kk), 8.5f * U, b)
            drawCircle(a.dia(P.LATON, 0.05f, kk), 6f * U, b)
            drawCircle(a.dia(P.LATON_LUZ, 0f, kk).copy(alpha = 0.8f), 3.4f * U, b - Offset(1.6f * U, 1.6f * U))
            destelloTemplario(b - Offset(2.2f * U, 2.2f * U), 1.2f * U, a, kk)
            if (a.oscuro) drawArc(a.filo(0.9f * kk), -60f, 120f, false, Offset(b.x - 6f * U, b.y - 6f * U), Size(12f * U, 12f * U), style = Stroke(1f * U))
        }
        tejuelo(e, s, a, medidor)
    }

    /** El tejuelo: el pergamino con el nombre y la cuenta, bajo la lámina de cuerno, en su marco de latón clavado. */
    private fun DrawScope.tejuelo(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario, medidor: TextMeasurer) {
        val U = s.U
        val l = s.libro
        val r = Rect(l.center.x - l.width * 0.335f, l.top + 18f * U, l.center.x + l.width * 0.335f, l.top + 70f * U)
        val kk = k(r.center, s, a)
        drawRect(a.sombra(0.45f), r.topLeft + Offset(1.6f * U, 2f * U), r.size)
        drawRect(a.dia(P.LATON, 0f, kk), r.topLeft, r.size)
        val dentro = r.deflate(3f * U)
        drawRect(a.etiqueta(P.PERGAMINO, kk), dentro.topLeft, dentro.size)
        drawRect(a.etiqueta(P.PERG_OSC, kk).copy(alpha = 0.35f), Offset(dentro.left, dentro.bottom - dentro.height * 0.18f), Size(dentro.width, dentro.height * 0.18f))
        // La lámina de cuerno: un velo ambarino muy leve y su brillo en diagonal (es lo que la protege).
        drawRect(a.etiqueta(CUERNO_LAMINA, kk).copy(alpha = 0.14f), dentro.topLeft, dentro.size)
        clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
            val b = Path().apply {
                moveTo(dentro.left + dentro.width * 0.62f, dentro.top); lineTo(dentro.left + dentro.width * 0.72f, dentro.top)
                lineTo(dentro.left + dentro.width * 0.6f, dentro.bottom); lineTo(dentro.left + dentro.width * 0.5f, dentro.bottom); close()
            }
            drawPath(b, a.claro(0.25f))
        }
        val tinta = if (a.oscuro) Color(0xFF140C06) else P.TINTA
        val medio = dentro.top + dentro.height * 0.58f
        textoEn(medidor, e.nombre, Rect(dentro.left + 6f * U, dentro.top + 2f * U, dentro.right - 6f * U, medio), P.LETRA, tinta, 15f, peso = FontWeight.SemiBold, minimo = 8f)
        textoEn(medidor, e.cuenta, Rect(dentro.left + 6f * U, medio - 1f * U, dentro.right - 6f * U, dentro.bottom - 2f * U), P.LETRA, tinta.copy(alpha = 0.85f), 10f, minimo = 6f)
        // El marco de latón: su luz arriba, su sombra abajo y los cuatro clavos.
        drawRect(a.brillo(0.5f, kk), r.topLeft, Size(r.width, 0.9f * U))
        drawRect(a.dia(P.LATON_OSC, 0f, kk), r.topLeft, r.size, style = Stroke(0.8f * U))
        drawRect(a.sombra(0.3f), dentro.topLeft, dentro.size, style = Stroke(0.8f * U))
        listOf(r.topLeft + Offset(1.6f * U, 1.6f * U), Offset(r.right - 1.6f * U, r.top + 1.6f * U), Offset(r.left + 1.6f * U, r.bottom - 1.6f * U), r.bottomRight - Offset(1.6f * U, 1.6f * U)).forEach {
            clavoTemplario(it, 1.1f * U, a, kk, P.LATON_OSC)
        }
    }

    /** Los registros de cinta que cuelgan de la testa sobre el canto y el paño: más con más artículos. */
    private fun DrawScope.registros(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val n = 1 + (e.hojas / 60).coerceIn(0, 3)
        val colores = listOf(P.CRUZ, Color(0xFF3E6A44), P.LATON, P.AZUL)
        for (j in 0 until n) {
            val x = l.left + l.width * (0.3f + 0.13f * j)
            val largo = (24f + 14f * azar(e.semilla, 70 + j)) * U
            val y1 = s.listonAbajo + largo
            val kk = k(Offset(x, y1), s, a)
            val col = a.dia(colores[j % colores.size], 0f, kk)
            val c = Path().apply { moveTo(x, l.bottom); quadraticTo(x + 3f * U, s.listonArriba + 4f * U, x + (azar(e.semilla, 80 + j) - 0.5f) * 6f * U, y1) }
            drawPath(Path().apply { addPath(c, Offset(1.2f * U, 1.2f * U)) }, a.sombra(0.35f), style = Stroke(3.2f * U))
            drawPath(c, col, style = Stroke(3.2f * U))
            // La punta cortada en cola de golondrina.
            val pf = Offset(x + (azar(e.semilla, 80 + j) - 0.5f) * 6f * U, y1)
            drawPath(poligonoTemplario(pf.x - 1.6f * U, pf.y - 1f * U, pf.x + 1.6f * U, pf.y - 1f * U, pf.x + 1.6f * U, pf.y + 3f * U, pf.x, pf.y + 1f * U, pf.x - 1.6f * U, pf.y + 3f * U), col)
        }
    }
}
