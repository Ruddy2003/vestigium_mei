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
        val v = s.v
        val ab = e.abierto.coerceIn(0f, 1f)
        val kc = k(l.center, s, a)
        // La sombra de la regla en el tablero del atril (solo en él): de día a la derecha; de noche, lejos de los mecheros (a la izquierda).
        clipRect(s.atrilIzq, s.atrilArriba, s.atrilDer, s.listonArriba) {
            val d = if (a.oscuro) Offset(-9f * U - s.lomo, 3f * U) else Offset(8f * U, 5f * U)
            drawRect(a.sombra(0.42f), l.topLeft + d, Size(l.width + v.x + s.lomo, l.height + v.y))
        }
        // La sombra de contacto en la cara de arriba del listón, donde apoya la tabla de atrás.
        val dc = if (a.oscuro) -4f * U else 4f * U
        drawRect(a.sombra(0.5f), Offset(l.left - s.lomo * 0.5f + v.x * 0.5f + dc, s.listonArriba), Size(l.width + s.lomo * 0.5f + v.x * 0.5f, 2.4f * U))
        bloque(e, s, a)
        cedulas(e, s, a)
        lomo(s, a)
        if (ab > 0.01f) hoja(e, s, a)
        val sx = 1f - 0.94f * ab
        scale(sx, 1f, Offset(l.left, l.center.y)) {
            canto(s, a)
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

    /**
     * Las cédulas que asoman: por la cabeza (por encima de la tapa) y por el canto de delante (de entre las hojas, a media
     * hondura del bloque). Más cuantos más artículos.
     */
    private fun DrawScope.cedulas(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val v = s.v
        val n = (e.hojas / 28).coerceIn(0, 6)
        for (j in 0 until n) {
            val arriba = j % 2 == 0
            val f = azar(e.semilla, 40 + j)
            val r = if (arriba) Rect(l.left + l.width * (0.18f + 0.62f * f), l.top - (6f + 6f * azar(e.semilla, 50 + j)) * U, l.left + l.width * (0.18f + 0.62f * f) + (12f + 6f * azar(e.semilla, 60 + j)) * U, l.top + 4f * U)
                else {
                    val t = 0.3f + 0.4f * azar(e.semilla, 90 + j)
                    val x = l.right - 2.2f * U + v.x * t
                    val y = l.top + l.height * (0.2f + 0.6f * f) + v.y * t
                    Rect(x - 1f * U, y, l.right + v.x + (5f + 6f * azar(e.semilla, 50 + j)) * U, y + 11f * U)
                }
            val kk = k(r.center, s, a)
            drawRect(a.sombra(0.3f), r.topLeft + Offset(1f * U, 1.2f * U), r.size)
            drawRect(a.etiqueta(mezcla(P.PERGAMINO, P.PERG_OSC, 0.25f * f), kk), r.topLeft, r.size)
            drawRect(a.sombra(0.2f), r.topLeft, r.size, style = Stroke(0.6f * U))
            if (!arriba) drawRect(a.sombra(0.45f), r.topLeft, Size(1.4f * U, r.height))
        }
    }

    /**
     * El grueso de la regla, en escorzo ([SitioLibretaTemplario.v]: hacia abajo y a la derecha): la tabla de atrás, que vuela
     * sobre las hojas (su canto de badana y la ceja en sombra), y el bloque de pergamino entre las dos tablas, por debajo (la
     * testa) y por la derecha (el canto de delante), con las hojas y las líneas de los cuadernillos, más marcadas cada pocas;
     * la cabezada de abajo, junto al lomo. Engorda con los artículos.
     */
    private fun DrawScope.bloque(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val v = s.v
        val t0 = (2.6f * U / s.g).coerceAtMost(0.3f)
        val t1 = 1f - t0
        val c = 2.2f * U
        val kk = k(Offset(l.right + v.x, l.bottom), s, a)
        // La tabla de atrás: su canto (abajo y a la derecha) y su cara de dentro, que asoma como ceja bajo las hojas.
        drawRect(a.dia(BADANA_HONDA, 0f, kk), l.topLeft + v, l.size)
        val dentro = Rect(l.left + v.x * t1, l.top + v.y * t1, l.right + v.x * t1, l.bottom + v.y * t1)
        drawRect(a.dia(BADANA, -0.3f, kk), dentro.topLeft, dentro.size)
        drawRect(a.dia(BADANA_LUZ, -0.15f, kk), Offset(dentro.left, dentro.bottom - 0.7f * U), Size(dentro.width, 0.7f * U))
        drawRect(a.dia(BADANA_LUZ, -0.15f, kk), Offset(dentro.right - 0.7f * U, dentro.top), Size(0.7f * U, dentro.height))
        // El bloque de las hojas: la testa y el canto de delante.
        val pa = Offset(l.left + 1f * U, l.bottom - c) + v * t0
        val pb = Offset(l.right - c, l.bottom - c) + v * t0
        val pe = Offset(l.right - c, l.top + c) + v * t0
        val d = v * (t1 - t0)
        val testa = poligonoTemplario(pa.x, pa.y, pb.x, pb.y, pb.x + d.x, pb.y + d.y, pa.x + d.x, pa.y + d.y)
        val canto = poligonoTemplario(pe.x, pe.y, pe.x + d.x, pe.y + d.y, pb.x + d.x, pb.y + d.y, pb.x, pb.y)
        // La sombra que echa el bloque en la ceja (la luz viene de arriba a la izquierda).
        drawPath(Path().apply { addPath(testa, Offset(1.4f * U, 1.8f * U)); addPath(canto, Offset(1.4f * U, 1.8f * U)) }, a.sombra(0.4f))
        val perg = mezcla(P.PERGAMINO, P.PERG_OSC, 0.22f)
        drawPath(testa, a.etiqueta(perg, k(Offset(l.center.x, l.bottom), s, a)))
        drawPath(canto, a.etiqueta(mezcla(perg, P.PERG_OSC, if (a.oscuro) 0.05f else 0.42f), kk))
        // Las hojas y los cuadernillos: una raya fina por hoja, más honda cada cuatro (el pliego), con su leve desorden.
        val hojas = Path(); val pliegos = Path()
        val largo = kotlin.math.sqrt(d.x * d.x + d.y * d.y)
        val n = (largo / (1.15f * U)).toInt().coerceAtLeast(2)
        for (i in 1 until n) {
            val f = (i + (azar(e.semilla + 7, i) - 0.5f) * 0.5f) / n
            val q = v * (f * (t1 - t0))
            val dx = (azar(e.semilla + 8, i) - 0.5f) * 0.6f * U
            val p = if (i % 4 == 0) pliegos else hojas
            p.moveTo(pa.x + q.x, pa.y + q.y); p.lineTo(pb.x + q.x + dx, pb.y + q.y + dx * 0.3f)
            p.moveTo(pe.x + q.x + dx * 0.3f, pe.y + q.y + 1f * U); p.lineTo(pb.x + q.x + dx, pb.y + q.y + dx * 0.3f)
        }
        drawPath(hojas, a.sombra(0.14f), style = Stroke(0.5f * U))
        drawPath(pliegos, a.sombra(0.34f), style = Stroke(0.75f * U))
        // La luz en la arista de las hojas que mira afuera, y la sombra que echa la tabla de delante, que vuela sobre ellas.
        clipPath(testa) { drawRect(a.sombra(0.4f), Offset(l.left, l.bottom), Size(l.width + v.x, 2.4f * U)) }
        clipPath(canto) { drawRect(a.sombra(if (a.oscuro) 0.2f else 0.4f), Offset(l.right, l.top), Size(2.4f * U, l.height + v.y)) }
        drawLine(a.claro(0.35f), Offset(pa.x + d.x, pa.y + d.y), Offset(pb.x + d.x, pb.y + d.y), 0.7f * U)
        if (a.oscuro) drawLine(a.filo(0.55f * kk), Offset(pe.x + d.x, pe.y + d.y), Offset(pb.x + d.x, pb.y + d.y), 1f * U)
        // La cabezada de abajo: el cordoncillo de grana y crudo, a lo hondo junto al lomo.
        val cab = poligonoTemplario(pa.x, pa.y, pa.x + 3.4f * U, pa.y, pa.x + 3.4f * U + d.x, pa.y + d.y, pa.x + d.x, pa.y + d.y)
        val kc = k(pa, s, a)
        drawPath(cab, a.dia(P.CRUZ, -0.1f, kc))
        clipPath(cab) {
            val hilos = Path()
            var f = 0f
            while (f < 1f) { val q = d * f; hilos.moveTo(pa.x + q.x, pa.y + q.y + 1f * U); hilos.lineTo(pa.x + q.x + 3.4f * U, pa.y + q.y - 0.4f * U); f += 1.6f * U / maxOf(largo, 1f) }
            drawPath(hilos, a.etiqueta(P.PERGAMINO, kc), style = Stroke(0.7f * U))
            drawRect(a.sombra(0.35f), Offset(pa.x + 2.4f * U, pa.y), Size(1.2f * U, d.y + 2f * U))
        }
    }

    /**
     * El lomo redondo a la izquierda: la badana que da la vuelta, clara donde mira a la ventana y honda en el cajo, con sus
     * cuatro nervios en relieve (su luz arriba, su sombra abajo, y el bulto que hacen en el perfil), la cofia de arriba con la
     * cabezada asomando y la de abajo, que se va hacia la tabla de atrás.
     */
    private fun DrawScope.lomo(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val v = s.v
        val xo = l.left - s.lomo
        val yb = l.bottom + v.y * 0.5f
        val kk = k(Offset(xo, l.center.y), s, a)
        // La cabezada de arriba, que asoma sobre la cofia.
        val cab = Rect(xo + 1.2f * U, l.top - 2.6f * U, l.left + 2.4f * U, l.top + 2f * U)
        drawRoundRect(a.dia(P.CRUZ, -0.1f, kk), cab.topLeft, cab.size, androidx.compose.ui.geometry.CornerRadius(2f * U))
        val hilos = Path()
        var x = cab.left + 1f * U
        while (x < cab.right - 0.5f * U) { hilos.moveTo(x, cab.top + 0.4f * U); hilos.lineTo(x + 0.8f * U, cab.top + 2.6f * U); x += 1.7f * U }
        drawPath(hilos, a.etiqueta(P.PERGAMINO, kk), style = Stroke(0.7f * U))
        val forma = Path().apply {
            moveTo(l.left + 1f * U, l.top)
            quadraticTo(xo + 0.5f * U, l.top - 0.4f * U, xo, l.top + 5f * U)
            lineTo(xo, yb - 4f * U)
            quadraticTo(xo + 0.6f * U, yb + v.y * 0.3f, l.left + v.x + 1f * U, l.bottom + v.y)
            lineTo(l.left + 1f * U, l.bottom)
            close()
        }
        val ancho = l.left - xo
        clipPath(forma) {
            // Por franjas: el perfil que se va, lo que mira a la ventana, el medio y el cajo.
            val franjas = if (a.oscuro) listOf(0f to -0.1f, 0.14f to 0f, 0.5f to -0.05f, 0.82f to -0.2f) else listOf(0f to -0.12f, 0.12f to 0.1f, 0.48f to 0f, 0.8f to -0.28f)
            franjas.forEachIndexed { i, (f, luz) ->
                val xa = xo + ancho * f; val xb = if (i + 1 < franjas.size) xo + ancho * franjas[i + 1].first + 0.6f else l.left + v.x + 2f * U
                drawRect(a.dia(if (i == 1) BADANA_LUZ else BADANA, luz, kk), Offset(xa, l.top - 2f * U), Size(xb - xa, l.height + v.y + 4f * U))
            }
            drawRect(a.claro(0.3f), Offset(xo + ancho * 0.2f, l.top), Size(0.9f * U, l.height + v.y))
            // La lanceta en el lomo: la misma luz, quebrada respecto de la tapa porque el lomo mira hacia la ventana.
            clipPath(s.lanceta(a.oscuro, -1f * U, -14f * U)) {
                drawRect(a.enLanceta(a.dia(BADANA, 0.08f, kk), 0.9f), Offset(xo, l.top), Size(ancho * 0.8f, l.height))
                drawRect(a.enLanceta(a.dia(BADANA, -0.12f, kk), 0.8f), Offset(xo + ancho * 0.8f, l.top), Size(ancho * 0.2f + 1f * U, l.height))
            }
            // Los nervios: un relieve redondo, con su luz arriba y su sombra abajo, y la que echan en el lomo.
            listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { f ->
                val y = l.top + 6f * U + (l.height - 12f * U) * f
                drawRect(a.sombra(0.3f), Offset(xo, y + 3.6f * U), Size(ancho + 2f * U, 1.6f * U))
            }
            // El cajo: la canal entre el lomo y la tapa.
            drawRect(a.sombra(0.5f), Offset(l.left - 1.4f * U, l.top), Size(1.6f * U, l.height + v.y))
        }
        drawPath(forma, a.sombra(0.35f), style = Stroke(0.8f * U))
        listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { f ->
            val y = l.top + 6f * U + (l.height - 12f * U) * f
            val r = Rect(xo - 1.3f * U, y, l.left - 1f * U, y + 3.6f * U)
            drawRoundRect(a.dia(BADANA, 0.02f, kk), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(1.8f * U))
            drawRect(a.claro(0.45f), Offset(r.left + 1f * U, r.top + 0.3f * U), Size(r.width * 0.6f, 0.9f * U))
            drawRect(a.sombra(0.45f), Offset(r.left + 1f * U, r.bottom - 1f * U), Size(r.width - 1f * U, 1f * U))
            if (a.oscuro) drawRect(a.brillo(0.3f, kk), Offset(r.left + 1f * U, r.top + 0.3f * U), Size(r.width * 0.6f, 0.7f * U))
        }
    }

    /** El canto de la tabla de delante: la badana que da la vuelta por abajo y por la derecha, en sombra, con su arista de luz. */
    private fun DrawScope.canto(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val t0 = (2.6f * U / s.g).coerceAtMost(0.3f)
        val q = s.v * t0
        val kk = k(Offset(l.right, l.bottom), s, a)
        val abajo = poligonoTemplario(l.left, l.bottom - 1f, l.right, l.bottom - 1f, l.right + q.x, l.bottom + q.y, l.left + q.x, l.bottom + q.y)
        val lado = poligonoTemplario(l.right - 1f, l.top, l.right + q.x, l.top + q.y, l.right + q.x, l.bottom + q.y, l.right - 1f, l.bottom)
        drawPath(abajo, a.dia(BADANA_HONDA, 0.05f, kk))
        drawPath(lado, a.dia(BADANA, if (a.oscuro) 0.05f else -0.4f, kk))
        drawLine(a.dia(BADANA_LUZ, 0f, kk).copy(alpha = 0.6f), Offset(l.left + q.x, l.bottom + q.y), Offset(l.right + q.x, l.bottom + q.y), 0.6f * U)
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
        // La lanceta de la ventana en la tapa: la badana encendida (de día, bermellón; de noche, la luna, fría), opaca y con su
        // borde, corrida respecto del fondo, del arco y del lomo porque es otro plano. Va antes del gofrado y los herrajes.
        val luz = s.lanceta(a.oscuro, 6f * U, -14f * U)
        clipRect(l.left, l.top, l.right, l.bottom) {
            clipPath(luz) {
                val base = a.dia(BADANA, 0f, k(Offset(l.left + 20f * U, l.top + 40f * U), s, a))
                drawRect(a.enLanceta(base), l.topLeft, l.size)
            }
            // El borde: de día, un filo más claro; de noche, blando (medio paso entre la luna y lo que la rodea).
            if (a.oscuro) drawPath(luz, a.enLanceta(a.dia(BADANA, 0f, k(Offset(l.left + 20f * U, l.top + 40f * U), s, a)), 0.5f), style = Stroke(3.2f * U))
            else drawPath(luz, a.enLanceta(a.dia(BADANA_LUZ, 0.1f, kc)).copy(alpha = 0.75f), style = Stroke(1.1f * U))
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
        // Las cantoneras y los cinco bullones (cuatro cerca de las esquinas y uno en medio del rombo); los que toca la
        // lanceta se vuelven a pintar encendidos dentro de ella, así la luz los prende y se corta en su borde.
        val bullones = listOf(Offset(l.left + 27f * U, l.top + 34f * U), Offset(l.right - 27f * U, l.top + 34f * U), Offset(l.left + 30f * U, l.bottom - 30f * U),
            Offset(l.right - 30f * U, l.bottom - 30f * U), Offset(in0.center.x, in0.center.y + 18f * U))
        herrajes(s, a, bullones, null)
        val caja = luz.getBounds()
        clipRect(l.left, l.top, l.right, l.bottom) { clipPath(luz) { herrajes(s, a, bullones, caja) } }
        tejuelo(e, s, a, medidor, luz)
    }

    /**
     * Las cantoneras de latón con sus clavos y los bullones. Con [caja] (el recuadro de la lanceta), solo los que caen en ella
     * y con el latón encendido (sin volver a echar su sombra, que ya está). De noche no se repintan: la luna solo les deja un
     * brillo frío chico en el filo que la mira (no blanco).
     */
    private fun DrawScope.herrajes(s: SitioLibretaTemplario, a: AireTemplario, bullones: List<Offset>, caja: Rect?) {
        val U = s.U
        val l = s.libro
        val lit = caja != null
        val luna = lit && a.oscuro
        val frio = Color(0xFF9AAAD2)
        fun c(col: Color, f: Float, kk: Float) = if (lit) a.enLanceta(a.dia(col, f, kk)) else a.dia(col, f, kk)
        val e0 = 22f * U
        listOf(Offset(l.left, l.top) to Offset(1f, 1f), Offset(l.right, l.top) to Offset(-1f, 1f), Offset(l.right, l.bottom) to Offset(-1f, -1f), Offset(l.left, l.bottom) to Offset(1f, -1f)).forEach { (o, d) ->
            if (caja != null && !caja.overlaps(Rect(minOf(o.x, o.x + d.x * e0), minOf(o.y, o.y + d.y * e0), maxOf(o.x, o.x + d.x * e0), maxOf(o.y, o.y + d.y * e0)))) return@forEach
            val kk = k(o, s, a)
            val p = Path().apply { moveTo(o.x, o.y); lineTo(o.x + d.x * e0, o.y); quadraticTo(o.x + d.x * e0 * 0.45f, o.y + d.y * e0 * 0.45f, o.x, o.y + d.y * e0); close() }
            if (luna) { drawPath(p, frio.copy(alpha = 0.4f), style = Stroke(0.6f * U)); return@forEach }
            if (!lit) drawPath(Path().apply { addPath(p, Offset(1.2f * U, 1.4f * U)) }, a.sombra(0.35f))
            drawPath(p, c(P.LATON, 0f, kk))
            drawPath(p, c(P.LATON_OSC, 0f, kk), style = Stroke(0.8f * U))
            if (lit) drawPath(p, a.enLanceta(a.dia(P.LATON_LUZ, 0f, kk)).copy(alpha = 0.7f), style = Stroke(0.6f * U))
            listOf(0.22f to 0.22f, 0.62f to 0.12f, 0.12f to 0.62f).forEach { (fx, fy) -> clavoTemplario(Offset(o.x + d.x * e0 * fx, o.y + d.y * e0 * fy), 1.2f * U, a, kk, if (lit) P.LATON else P.LATON_OSC) }
        }
        bullones.forEach { b ->
            if (caja != null && !caja.overlaps(Rect(b.x - 9f * U, b.y - 9f * U, b.x + 9f * U, b.y + 9f * U))) return@forEach
            val kk = k(b, s, a)
            if (luna) {
                drawArc(frio.copy(alpha = 0.45f), 165f, 100f, false, Offset(b.x - 7.2f * U, b.y - 7.2f * U), Size(14.4f * U, 14.4f * U), style = Stroke(0.9f * U))
                drawCircle(frio.copy(alpha = 0.7f), 1f * U, b - Offset(2.4f * U, 2.4f * U))
                return@forEach
            }
            if (!lit) drawCircle(a.sombra(0.4f), 8.5f * U, b + Offset(1.6f * U, 2f * U))
            drawCircle(c(P.LATON_OSC, 0f, kk), 8.5f * U, b)
            drawCircle(c(P.LATON, 0.05f, kk), 6f * U, b)
            drawCircle(c(P.LATON_LUZ, 0f, kk).copy(alpha = 0.8f), 3.4f * U, b - Offset(1.6f * U, 1.6f * U))
            if (lit) drawCircle(Color(0xFFFFF6DC), 1.6f * U, b - Offset(2.2f * U, 2.2f * U))
            else destelloTemplario(b - Offset(2.2f * U, 2.2f * U), 1.2f * U, a, kk)
            if (a.oscuro && !lit) drawArc(a.filo(0.9f * kk), -60f, 120f, false, Offset(b.x - 6f * U, b.y - 6f * U), Size(12f * U, 12f * U), style = Stroke(1f * U))
        }
    }

    /** El tejuelo: el pergamino con el nombre y la cuenta, bajo la lámina de cuerno, en su marco de latón clavado. */
    private fun DrawScope.tejuelo(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario, medidor: TextMeasurer, luz: Path) {
        val U = s.U
        val l = s.libro
        val r = Rect(l.center.x - l.width * 0.335f, l.top + 18f * U, l.center.x + l.width * 0.335f, l.top + 70f * U)
        val kk = k(r.center, s, a)
        drawRect(a.sombra(0.45f), r.topLeft + Offset(1.6f * U, 2f * U), r.size)
        drawRect(a.dia(P.LATON, 0f, kk), r.topLeft, r.size)
        val dentro = r.deflate(3f * U)
        drawRect(a.etiqueta(P.PERGAMINO, kk), dentro.topLeft, dentro.size)
        // Si la lanceta llega al tejuelo, el marco y el pergamino se encienden (el tejuelo vuela un poco: va algo más corrida).
        if (luz.getBounds().overlaps(r)) clipRect(r.left, r.top, r.right, r.bottom) {
            clipPath(Path().apply { addPath(luz, Offset(-1.5f * U, -1.5f * U)) }) {
                if (!a.oscuro) drawRect(a.enLanceta(a.dia(P.LATON, 0f, kk)), r.topLeft, r.size)
                drawRect(a.enLanceta(a.etiqueta(P.PERGAMINO, kk), 0.55f), dentro.topLeft, dentro.size)
            }
        }
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

    /**
     * Los registros de cinta: salen de entre las hojas de la testa (cada uno entre dos cuadernillos, a su hondura), pasan por
     * el canto de la tabla de atrás y el listón y cuelgan sobre el paño. Más con más artículos.
     */
    private fun DrawScope.registros(e: EstadoLibreta, s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val l = s.libro
        val v = s.v
        val t0 = (2.6f * U / s.g).coerceAtMost(0.3f)
        val n = 1 + (e.hojas / 60).coerceIn(0, 3)
        val colores = listOf(P.CRUZ, Color(0xFF3E6A44), P.LATON, P.AZUL)
        for (j in 0 until n) {
            val t = t0 + (1f - 2f * t0) * (0.3f + 0.4f * azar(e.semilla, 75 + j))
            val x = l.left + l.width * (0.3f + 0.13f * j) + v.x * t
            val y0 = l.bottom - 2.2f * U + v.y * t
            val largo = (24f + 14f * azar(e.semilla, 70 + j)) * U
            val y1 = s.listonAbajo + largo
            val kk = k(Offset(x, y1), s, a)
            val col = a.dia(colores[j % colores.size], 0f, kk)
            // La rendija entre los cuadernillos, de donde sale.
            drawLine(a.sombra(0.55f), Offset(x - 2.6f * U, y0), Offset(x + 2.6f * U, y0), 0.9f * U)
            val c = Path().apply { moveTo(x, y0); quadraticTo(x + 2.5f * U, s.listonArriba + 4f * U, x + (azar(e.semilla, 80 + j) - 0.5f) * 6f * U, y1) }
            drawPath(Path().apply { addPath(c, Offset(1.2f * U, 1.2f * U)) }, a.sombra(0.35f), style = Stroke(3.2f * U))
            drawPath(c, col, style = Stroke(3.2f * U))
            // La punta cortada en cola de golondrina.
            val pf = Offset(x + (azar(e.semilla, 80 + j) - 0.5f) * 6f * U, y1)
            drawPath(poligonoTemplario(pf.x - 1.6f * U, pf.y - 1f * U, pf.x + 1.6f * U, pf.y - 1f * U, pf.x + 1.6f * U, pf.y + 3f * U, pf.x, pf.y + 1f * U, pf.x - 1.6f * U, pf.y + 3f * U), col)
        }
    }
}
