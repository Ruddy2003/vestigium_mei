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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El cuaderno del superviviente (se pinta en cada dibujo: engorda con las hojas y se abre al tocarlo): de pie en la mesa y
 * apoyado en la pared, con su sombra; la contratapa y el canto de las hojas hinchado (a la derecha y arriba), de donde asoman las
 * plantas prensadas; la tapa de lona verde con su luz y su sombra, las cantoneras de badana, la hoja de hiedra prensada pegada
 * con una tira de papel y la etiqueta de cartón cosida con el nombre y la cuenta; el lomo cosido a mano; el bramante con su nudo
 * y el lápiz metido debajo. Al abrirse, la tapa gira sobre el lomo y deja ver la primera hoja, con un helecho prensado.
 */
internal object CuadernoLibretaRuinas {
    /** La lona verde de la tapa, la de la contratapa (más honda) y la badana de las cantoneras. */
    private val LONA = Color(0xFF5E7446)
    private val LONA_OSC = Color(0xFF3F5030)
    private val BADANA = Color(0xFF7A4A2A)

    fun DrawScope.cuaderno(e: EstadoLibreta, s: SitioLibretaRuinas, a: AireRuinas, medidor: TextMeasurer) {
        val U = s.U
        val t = s.tapa; val v = s.v
        val ab = e.abierto.coerceIn(0f, 1f)
        val li = s.luz(a, Offset(t.left, t.center.y), s.solEn(Offset(t.left, t.top + t.height * 0.3f), 12f))
        val ld = s.luz(a, Offset(t.right, t.center.y), s.solEn(Offset(t.right, t.top + t.height * 0.3f), 12f))
        val lc = s.luz(a, t.center, s.solEn(t.center, 12f))
        val hacia = s.hacia(a, t.center)
        // La sombra en la pared (de día, a la derecha y abajo; de noche, lejos del candil) y la de contacto en la mesa.
        val d = if (a.oscuro) Offset(16f * U, -3f * U) else Offset(10f * U, 6f * U)
        drawRect(lc.s(0.4f), Offset(t.left - s.lomo + d.x, t.top + v.y + d.y), Size(t.width + s.lomo + v.x, t.height - v.y - 4f * U))
        drawOval(lc.s(0.6f), Offset(t.left - s.lomo - 4f * U, s.mesa - 3f * U), Size(t.width + s.lomo + v.x + 12f * U, 7f * U))
        // La contratapa, detrás, y el canto de las hojas (a la derecha y arriba), hinchado y ondulado.
        val atras = Rect(t.left + v.x, t.top + v.y, t.right + v.x, t.bottom + v.y)
        drawRect(ld.c(LONA_OSC), atras.topLeft, atras.size)
        // El canto de la derecha se abomba con las plantas prensadas de dentro.
        val bulto = 0.16f * s.g
        val cantoDer = Path().apply {
            moveTo(t.right, t.top + 2f * U); lineTo(atras.right - 2f * U, atras.top + 3f * U)
            quadraticTo(atras.right - 2f * U + bulto, atras.center.y, atras.right - 2f * U, atras.bottom - 2f * U)
            lineTo(t.right, t.bottom - 2f * U); close()
        }
        val cantoArr = poligonoRuinas(Offset(t.left + 2f * U, t.top), Offset(atras.left + 3f * U, atras.top + 2f * U), Offset(atras.right - 3f * U, atras.top + 2f * U), Offset(t.right, t.top))
        val papel = mezcla(P.PAPEL, P.KRAFT, 0.28f)
        drawPath(cantoDer, ld.c(mezcla(papel, P.KRAFT_OSC, 0.18f)))
        drawPath(cantoArr, s.luz(a, Offset(t.center.x, t.top), s.solEn(Offset(t.center.x, t.top), 12f)).c(papel))
        // Las hojas en el canto: líneas a lo largo, más cuantas más hojas, y alguna hoja más oscura (una planta dentro).
        val lineas = Path()
        val n = (4 + e.hojas / 6).coerceAtMost(16)
        for (k in 1 until n) {
            val f = k / n.toFloat()
            lineas.moveTo(t.right + v.x * f, t.top + 3f * U + v.y * f); lineas.lineTo(t.right + v.x * f, t.bottom - 3f * U + v.y * f)
            lineas.moveTo(t.left + 3f * U + v.x * f, t.top + v.y * f); lineas.lineTo(t.right - 2f * U + v.x * f, t.top + v.y * f)
        }
        drawPath(lineas, ld.s(0.3f), style = Stroke(0.6f * U))
        // Las plantas prensadas que asoman del canto: helechos, hojas y una espiga, más cuantas más hojas.
        plantas(e, s, a)
        // La tapa, que gira sobre el lomo al abrirse: debajo, la primera hoja con su helecho.
        if (ab > 0.01f) primeraHoja(s, a)
        val sx = 1f - 0.94f * ab
        scale(sx, 1f, Offset(t.left, t.center.y)) { tapa(e, s, a, li, ld, lc, hacia, medidor) }
        if (ab > 0.01f) drawRect(lc.s(0.4f * ab), Offset(t.left + t.width * sx, t.top), Size(8f * U, t.height))
        lomo(s, a, li)
    }

    /** Las plantas prensadas que asoman del canto de las hojas, a la derecha y arriba. */
    private fun DrawScope.plantas(e: EstadoLibreta, s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U
        val t = s.tapa; val v = s.v
        val n = (3 + e.hojas / 10).coerceIn(3, 11)
        for (k in 0 until n) {
            val sem = 1600 + k * 7
            val derecha = k % 3 != 2
            val base = if (derecha) Offset(t.right + v.x * 0.6f, t.top + t.height * (0.12f + 0.8f * azar(sem, 1)) + v.y * 0.6f)
                else Offset(t.left + t.width * (0.2f + 0.6f * azar(sem, 2)) + v.x * 0.6f, t.top + v.y * 0.6f)
            val ang = if (derecha) 60f + 50f * azar(sem, 3) else -20f + 40f * azar(sem, 4)
            val l = s.luz(a, base)
            val hacia = s.hacia(a, base)
            val col = mezcla(if (k % 2 == 0) P.HELECHO else P.HIEDRA_LUZ, P.KRAFT, 0.12f + 0.18f * azar(sem, 5))
            when (k % 3) {
                0 -> frondaRuinas(base, (26f + 12f * azar(sem, 6)) * U, ang, 0.2f, col, l, hacia, sem, 8)
                1 -> hojaRuinas(base + Offset(2f * U, 0f), (14f + 6f * azar(sem, 7)) * U, ang, col, l, hacia, 1.6f * U, 0.95f)
                else -> {
                    val tope = giradoRuinas(base, 0f, -16f * U, ang)
                    drawLine(l.c(mezcla(P.KRAFT, P.HELECHO, 0.3f)), base, tope, 1f * U)
                    for (j in 0 until 4) {
                        val p = tope + (base - tope) * (j * 0.18f)
                        rotate(ang + (if (j % 2 == 0) 25f else -25f), p) { drawOval(l.c(mezcla(P.OCRE, P.KRAFT, 0.4f)), Offset(p.x - 1.3f * U, p.y - 3f * U), Size(2.6f * U, 4.4f * U)) }
                    }
                }
            }
        }
    }

    /** La tapa de lona: el color con su plano de luz y su plano de sombra, el desgaste, las cantoneras, la hoja pegada y la etiqueta. */
    private fun DrawScope.tapa(e: EstadoLibreta, s: SitioLibretaRuinas, a: AireRuinas, li: LuzRuinas, ld: LuzRuinas, lc: LuzRuinas, hacia: Float, medidor: TextMeasurer) {
        val U = s.U
        val t = s.tapa
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(t, CornerRadius(3f * U))) }
        val ci = li.c(LONA); val cd = ld.c(LONA)
        val claro = Color((ci.red * 1.16f + 0.04f).coerceAtMost(1f), (ci.green * 1.14f + 0.04f).coerceAtMost(1f), (ci.blue * 1.1f + 0.03f).coerceAtMost(1f), 1f)
        val hondo = mezcla(cd, a.sombra(1f).copy(alpha = 1f), 0.3f)
        val izq = if (hacia < 0f) claro else hondo
        val der = if (hacia < 0f) hondo else claro
        drawPath(forma, Brush.horizontalGradient(colorStops = arrayOf(0f to izq, 0.16f to izq, 0.16f to ci, 0.72f to cd, 0.72f to mezcla(cd, der, 0.5f), 1f to der), startX = t.left, endX = t.right))
        clipPath(forma) {
            // La trama de la lona, rala, y el desgaste de los cantos (más claro); un cerco de agua abajo.
            val trama = Path()
            var y = t.top + 5f * U
            while (y < t.bottom) { trama.moveTo(t.left, y); trama.lineTo(t.right, y + 1f * U); y += 7f * U }
            drawPath(trama, lc.s(0.08f), style = Stroke(0.6f * U))
            drawPath(bordeRuinas(forma, 5f * U, 5f * U), lc.c(mezcla(LONA, P.GASTADO, 0.55f)).copy(alpha = 0.6f))
            drawPath(bordeRuinas(forma, -4f * U, -4f * U), lc.c(mezcla(LONA, P.GASTADO, 0.4f)).copy(alpha = 0.45f))
            val nivel = t.bottom - t.height * 0.16f
            drawPath(Path().apply { moveTo(t.left, t.bottom); lineTo(t.left, nivel); quadraticTo(t.center.x, nivel - 8f * U, t.right, nivel + 4f * U); lineTo(t.right, t.bottom); close() }, lc.s(0.16f))
            // Las cantoneras de badana en las dos esquinas de la derecha.
            for ((y0, signo) in listOf(t.top to 1f, t.bottom to -1f)) {
                val cant = poligonoRuinas(Offset(t.right, y0), Offset(t.right - 22f * U, y0), Offset(t.right, y0 + signo * 22f * U))
                drawPath(cant, ld.c(BADANA))
                drawPath(bordeRuinas(cant, 1.6f * U, 1.6f * U * signo), ld.b(0.3f))
            }
        }
        // Los filos: la luz en el canto que la mira, la luna fría en el otro.
        if (lc.k > 0.06f || li.k > 0.06f) drawPath(bordeRuinas(forma, -hacia * 1.8f * U, 0f), li.filo(0.8f))
        if (ld.luna > 0.04f) drawPath(bordeRuinas(forma, -2f * U, 2f * U), ld.frio(0.7f))
        if (!a.oscuro) drawLine(li.b(0.6f), Offset(t.left + 3f * U, t.top + 1f * U), Offset(t.right - 3f * U, t.top + 1f * U), 1.2f * U)
        // La hoja de hiedra prensada, pegada abajo con una tira de papel engomado.
        val hc = Offset(t.left + t.width * 0.36f, t.top + t.height * 0.8f)
        val lh = s.luz(a, hc)
        hojaRuinas(hc, 30f * U, 18f, mezcla(P.HIEDRA, P.KRAFT, 0.28f), lh, hacia, 2f * U, 1f)
        rotate(-12f, hc) { drawRect(lh.c(mezcla(P.PAPEL, P.KRAFT, 0.4f)), Offset(hc.x - 12f * U, hc.y - 6f * U), Size(24f * U, 7f * U)) }
        // El bramante: dos vueltas a lo ancho, con su nudo, y el lápiz metido debajo que asoma arriba a la derecha.
        val yb = t.top + t.height * 0.62f
        val lb = s.luz(a, Offset(t.center.x, yb))
        for (dy in listOf(0f, 3.4f * U)) {
            val c = Path().apply { moveTo(t.left - 2f * U, yb + dy); quadraticTo(t.center.x, yb + dy + 3f * U, t.right + 2f * U, yb + dy - 1f * U) }
            drawPath(corridoRuinas(c, -hacia * 1.2f * U, 1.4f * U), lb.s(0.42f), style = Stroke(1.5f * U))
            drawPath(c, lb.c(P.BRAMANTE), style = Stroke(1.6f * U))
        }
        val nudo = Offset(t.left + t.width * 0.74f, yb + 3f * U)
        drawCircle(lb.c(mezcla(P.BRAMANTE, P.KRAFT_OSC, 0.35f)), 3f * U, nudo)
        drawLine(lb.c(P.BRAMANTE), nudo, nudo + Offset(5f * U, 14f * U), 1.3f * U, StrokeCap.Round)
        drawLine(lb.c(P.BRAMANTE), nudo, nudo + Offset(-3f * U, 12f * U), 1.3f * U, StrokeCap.Round)
        val lp0 = Offset(t.right - 18f * U, yb + 10f * U)
        rotate(-58f, lp0) {
            drawRect(lb.s(0.4f), Offset(lp0.x + 1.2f * U, lp0.y + 1.6f * U), Size(74f * U, 5f * U))
            drawRect(lb.c(Color(0xFF3F6A3A)), Offset(lp0.x, lp0.y), Size(66f * U, 5f * U))
            drawLine(lb.b(0.45f), Offset(lp0.x, lp0.y + 1f * U), Offset(lp0.x + 66f * U, lp0.y + 1f * U), 0.9f * U)
            drawPath(poligonoRuinas(Offset(lp0.x + 66f * U, lp0.y), Offset(lp0.x + 75f * U, lp0.y + 2.5f * U), Offset(lp0.x + 66f * U, lp0.y + 5f * U)), lb.c(Color(0xFFD9B884)))
            drawLine(lb.c(P.LAPIZ), Offset(lp0.x + 72f * U, lp0.y + 2.5f * U), Offset(lp0.x + 75f * U, lp0.y + 2.5f * U), 1.4f * U)
        }
        etiqueta(e, s, a, medidor)
    }

    /** La etiqueta de cartón cosida a la tapa con cuatro puntadas, con el nombre del cuaderno y la cuenta de las hojas. */
    private fun DrawScope.etiqueta(e: EstadoLibreta, s: SitioLibretaRuinas, a: AireRuinas, medidor: TextMeasurer) {
        val U = s.U
        val t = s.tapa
        val r = Rect(t.left + t.width * 0.1f, t.top + t.height * 0.17f, t.right - t.width * 0.1f, t.top + t.height * 0.43f)
        val l = s.luz(a, r.center, s.solEn(r.center, 12f))
        val hacia = s.hacia(a, r.center)
        drawRect(l.s(0.45f), r.topLeft + Offset(-hacia * 2f * U, 2.4f * U), r.size)
        val carton = if (a.oscuro) mezcla(mezcla(P.CARTON, a.hondo, 0.42f * (1f - l.k)), P.LLAMA, 0.3f * l.k) else l.c(P.CARTON)
        drawRect(carton, r.topLeft, r.size)
        drawRect(mezcla(carton, P.KRAFT_OSC, 0.4f).copy(alpha = 0.35f), Offset(r.left, r.bottom - r.height * 0.2f), Size(r.width, r.height * 0.2f))
        drawRect(mezcla(carton, P.KRAFT_OSC, 0.6f), r.topLeft, r.size, style = Stroke(0.8f * U))
        // Las puntadas de bramante en las cuatro esquinas, paralelas al canto.
        for ((x, y) in listOf(r.left + 4f * U to r.top + 4f * U, r.right - 4f * U to r.top + 4f * U, r.left + 4f * U to r.bottom - 4f * U, r.right - 4f * U to r.bottom - 4f * U)) {
            drawLine(l.c(P.BRAMANTE), Offset(x - 3f * U, y), Offset(x + 3f * U, y), 1.2f * U)
        }
        val tinta = if (a.oscuro) Color(0xFF14110D) else P.LAPIZ
        textoEn(medidor, e.nombre, Rect(r.left + 6f * U, r.top + 4f * U, r.right - 6f * U, r.top + r.height * 0.52f), P.LETRA, tinta, 14f, minimo = 8f)
        // La cuenta, en dos renglones si trae su «desde» (en uno no cabe sin cortarse).
        val corte = e.cuenta.indexOf(" ·")
        val renglones = if (corte > 0) listOf(e.cuenta.substring(0, corte), e.cuenta.substring(corte + 2)) else listOf(e.cuenta)
        val alto = (r.bottom - 3f * U - (r.top + r.height * 0.5f)) / renglones.size
        renglones.forEachIndexed { i, texto ->
            val y = r.top + r.height * 0.5f + alto * i
            textoEn(medidor, texto, Rect(r.left + 6f * U, y, r.right - 6f * U, y + alto), P.LETRA_MES, tinta.copy(alpha = 0.85f), 8.5f, peso = FontWeight.SemiBold, minimo = 6f)
        }
        if (l.k > 0.08f) drawRect(l.filo(0.6f), r.topLeft, r.size, style = Stroke(1f * U))
    }

    /** El lomo de lona, a la izquierda, cosido a mano con puntadas largas de bramante. */
    private fun DrawScope.lomo(s: SitioLibretaRuinas, a: AireRuinas, l: LuzRuinas) {
        val U = s.U
        val t = s.tapa
        val r = Rect(t.left - s.lomo, t.top + 1f * U, t.left + 1f * U, t.bottom - 1f * U)
        val c = l.c(mezcla(LONA, LONA_OSC, 0.5f))
        drawRoundRect(Brush.horizontalGradient(listOf(mezcla(c, Color.White, 0.12f), c, mezcla(c, a.hondo, 0.35f)), r.left, r.right), r.topLeft, r.size, CornerRadius(r.width * 0.4f))
        var y = r.top + 12f * U
        while (y < r.bottom - 8f * U) {
            drawLine(l.c(P.BRAMANTE), Offset(r.left + 1f * U, y), Offset(r.right - 1f * U, y + 2f * U), 1.3f * U, StrokeCap.Round)
            y += maxOf(4f, 24f * U)
        }
        if (l.k > 0.06f) drawLine(l.filo(0.8f), Offset(r.left + 1f * U, r.top + 4f * U), Offset(r.left + 1f * U, r.bottom - 4f * U), 1.4f * U)
    }

    /** La primera hoja (al abrirse): papel grueso con su veta, un helecho prensado pegado con dos tiras de papel. */
    private fun DrawScope.primeraHoja(s: SitioLibretaRuinas, a: AireRuinas) {
        val U = s.U
        val t = s.tapa
        val l = s.luz(a, t.center)
        drawRect(l.c(mezcla(P.PAPEL, P.KRAFT, 0.2f)), t.topLeft, t.size)
        drawRect(l.s(0.2f), t.topLeft, Size(10f * U, t.height))
        val pie = Offset(t.center.x, t.bottom - t.height * 0.12f)
        frondaRuinas(pie, t.height * 0.7f, -6f, 0.2f, mezcla(P.HELECHO, P.KRAFT, 0.3f), l, -1f, 1690, 10)
        for (dy in listOf(0.05f, 0.4f)) drawRect(l.c(mezcla(P.PAPEL, P.KRAFT, 0.45f)), Offset(pie.x - 10f * U, pie.y - t.height * dy - 3f * U), Size(20f * U, 6f * U))
    }
}
