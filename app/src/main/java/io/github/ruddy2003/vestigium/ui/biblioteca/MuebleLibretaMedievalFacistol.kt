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
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/**
 * El facistol de nogal y el códice de momentos de la libreta de Scriptorium ([MuebleLibretaMedieval]): el pie torneado, el
 * faldón, el tablero inclinado con su canto y su reborde y, encima, el códice en tablas con media piel, que engorda con las
 * hojas por el canto de delante y el de abajo.
 */
internal object FacistolLibretaMedieval {
    private val P = PaletaMedieval
    private val SEDAS = listOf(P.RUBRICA, Color(0xFF3F5C8C), P.OCRE_PINTURA, P.VERDE, mezcla(P.ALMAGRE, P.NOGAL_OSC, 0.2f))
    /** Dónde van las dos manecillas, en fracción del alto del códice. */
    private val MANECILLAS = listOf(0.17f, 0.83f)

    private fun cerca(p: Offset, s: SitioLibretaMedieval, a: AireMedieval) = MuebleLibretaMedieval.cerca(p, s, a)

    fun DrawScope.facistol(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer) {
        val k = cerca(s.codice.center, s, a)
        sombraMuro(s, a)
        pie(s, a)
        tablero(s, a)
        codice(s, a, e, medidor, k)
        reborde(s, a, e, k)
    }

    /** La sombra del facistol en el muro: de día a la derecha; de noche, lejos de la vela (a la izquierda). */
    private fun DrawScope.sombraMuro(s: SitioLibretaMedieval, a: AireMedieval) {
        val t = s.tablero
        val d = if (a.oscuro) Offset(-s.w * 0.08f, s.h * 0.03f) else Offset(s.w * 0.05f, s.h * 0.04f)
        val sil = poligonoMedieval(t.left + 8f * s.u, t.top, t.right - 8f * s.u, t.top, t.right, t.bottom, t.left, t.bottom)
        drawPath(corridoMedieval(sil, d.x, d.y), a.sombra(if (a.oscuro) 0.32f else 0.22f))
        drawRect(a.sombra(if (a.oscuro) 0.26f else 0.18f), Offset(s.eje - 7f * s.u + d.x, t.bottom), Size(14f * s.u, s.suelo - t.bottom))
    }

    /** El pie: el fuste torneado de anillos y bolas, las tres patas que pisan el suelo, su sombra, y el faldón de arriba. */
    private fun DrawScope.pie(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val x = s.eje
        val y0 = s.liston + s.h * 0.055f; val y1 = s.h * 0.955f
        val k = cerca(Offset(x, (y0 + y1) / 2), s, a)
        val madera = a.luz(P.NOGAL, k)
        contactoMedieval(Offset(x + (if (a.oscuro) -6f else 8f) * u, y1 + 2f * u), s.w * 0.3f, 8f * u, a, 0.4f)
        for ((dx, dy) in listOf(-1f to 0f, 1f to 0f, 0.15f to 1f)) {
            val pie = Offset(x + dx * s.w * 0.12f, y1 + dy * 3f * u)
            val pata = poligonoMedieval(x - 5f * u, y1 - 11f * u, x + 5f * u, y1 - 11f * u, pie.x + 4f * u, pie.y, pie.x - 4f * u, pie.y - 3f * u)
            drawPath(pata, mezcla(madera, a.hondo, if (dy > 0) 0f else 0.18f))
            drawPath(pata, a.frio(0.3f), style = Stroke(0.7f * u))
            drawOval(mezcla(madera, a.hondo, 0.3f), Offset(pie.x - 5f * u, pie.y - 3f * u), Size(10f * u, 5f * u))
        }
        val fuste = Rect(x - 7f * u, y0, x + 7f * u, y1 - 8f * u)
        drawRect(madera, fuste.topLeft, fuste.size)
        drawRect(Brush.horizontalGradient(listOf(a.claro(0.3f), Color.Transparent, a.frio(0.45f)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
        if (a.oscuro) drawRect(a.filo(0.45f * k), Offset(fuste.right - 1.6f * u, fuste.top), Size(1.6f * u, fuste.height))
        var y = y0 + 3f * u; var i = 0
        while (y < y1 - 12f * u) {
            val r = if (i % 3 == 1) 11f * u else 9f * u
            val alto = if (i % 3 == 1) 6f * u else 4f * u
            drawRoundRect(madera, Offset(x - r, y), Size(2 * r, alto), CornerRadius(alto / 2))
            drawRect(a.claro(0.35f), Offset(x - r + 1.5f * u, y + 0.5f * u), Size(2 * r - 3f * u, 1f * u))
            drawRect(a.frio(0.4f), Offset(x - r + 1.5f * u, y + alto - 1.4f * u), Size(2 * r - 3f * u, 1.2f * u))
            if (a.oscuro) drawRect(a.filo(0.5f * k), Offset(x + r - 2.5f * u, y + 0.5f * u), Size(1.4f * u, alto - 1f * u))
            y += if (i % 3 == 1) 13f * u else 8f * u; i++
        }
        // El faldón bajo el reborde, con tres rosetas taladradas.
        val falda = Rect(s.tablero.left + 16f * u, s.liston + 6f * u, s.tablero.right - 16f * u, y0 + 2f * u)
        val kf = cerca(falda.center, s, a)
        drawRect(a.luz(P.NOGAL, kf), falda.topLeft, falda.size)
        drawRect(a.frio(0.4f), Offset(falda.left, falda.bottom - 3f * u), Size(falda.width, 3f * u))
        for (f in listOf(0.2f, 0.5f, 0.8f)) {
            val c = Offset(falda.left + falda.width * f, falda.center.y - 0.5f * u)
            drawCircle(a.luz(P.NOGAL_OSC, kf), 3f * u, c)
            drawArc(a.claro(0.3f), 20f, 140f, false, c - Offset(3f * u, 3f * u), Size(6f * u, 6f * u), style = Stroke(0.8f * u))
        }
    }

    /**
     * El tablero inclinado de nogal (más angosto arriba, porque se aleja), con su veta, la luz que le da, su canto de arriba que
     * mira al cielo, y el grueso de la tabla y la cartela que lo sostiene, a la derecha, que dicen la pendiente.
     */
    private fun DrawScope.tablero(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val t = s.tablero
        val k = cerca(t.center, s, a)
        val nogal = a.luz(P.NOGAL, k)
        val entra = 9f * u
        // La cartela de la derecha, que baja en rampa desde arriba hasta el reborde, y el grueso del tablero.
        drawPath(poligonoMedieval(t.right - entra, t.top, t.right - entra + 6f * u, t.top + 4f * u, t.right + 7f * u, t.bottom + 3f * u, t.right, t.bottom),
            mezcla(nogal, a.hondo, if (a.oscuro) 0.1f else 0.35f))
        if (a.oscuro) drawLine(a.filo(0.55f * k), Offset(t.right - entra + 6f * u, t.top + 4f * u), Offset(t.right + 7f * u, t.bottom + 3f * u), 1.4f * u)
        val forma = poligonoMedieval(t.left + entra, t.top, t.right - entra, t.top, t.right, t.bottom, t.left, t.bottom)
        drawPath(forma, nogal)
        clipPath(forma) {
            vetas(t, a.luz(P.NOGAL_OSC, k).copy(alpha = 0.5f), 3201, densidad = 0.3f, vertical = true, grosor = 0.7f, ondulacion = 0.4f)
        }
        drawPath(forma, Brush.linearGradient(listOf(if (a.oscuro) Color.Transparent else a.claro(0.2f), if (a.oscuro) a.filo(0.18f * k) else a.frio(0.28f)), t.topLeft, t.bottomRight))
        // El canto de arriba (la cabeza de la tabla), que toma la luz, y el filo de la izquierda.
        drawPath(poligonoMedieval(t.left + entra, t.top - 3f * u, t.right - entra, t.top - 3f * u, t.right - entra + 1f * u, t.top, t.left + entra - 1f * u, t.top),
            a.luz(P.NOGAL_LUZ, k))
        drawLine(a.claro(0.5f), Offset(t.left + entra, t.top - 2.6f * u), Offset(t.right - entra, t.top - 2.6f * u), 0.8f * u)
        drawLine(a.claro(0.3f), Offset(t.left + entra, t.top), Offset(t.left, t.bottom), 1.2f * u)
        drawLine(a.frio(0.4f), Offset(t.right - entra, t.top), Offset(t.right, t.bottom), 1.4f * u)
    }

    // ---------------------------------------------------------------- el códice

    /**
     * El códice de momentos: su sombra en el tablero, lo grueso (la tabla de atrás, el bloque de las hojas por el canto de
     * delante y el de abajo, con sus hojas, y las correas que dan la vuelta), las cédulas que asoman, la primera hoja si se
     * abre, la tapa con su media piel y sus manecillas, y el lomo redondo con los nervios.
     */
    private fun DrawScope.codice(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer, k: Float) {
        val u = s.u
        val c = s.codice
        val ab = e.abierto.coerceIn(0f, 1f)
        val hoja = a.etiqueta(maxOf(k, 0.55f))
        val d = if (a.oscuro) Offset(-6f * u, 5f * u) else Offset(6f * u, 6f * u)
        drawRect(a.sombra(0.42f), Offset(c.left + d.x, c.top + d.y), Size(c.width + s.canto.x, c.height + s.canto.y))
        grueso(s, a, e, k)
        cedulas(s, a, e, hoja)
        if (ab > 0.01f) primeraHoja(s, a, e, medidor, hoja, k)
        val sx = 1f - 0.94f * ab
        scale(sx, 1f, Offset(c.left, c.center.y)) {
            with(TapaLibretaMedieval) { tapa(s, a, e, medidor, k, hoja) }
            if (ab > 0f) drawRect(a.sombra(0.45f * ab), c.topLeft, c.size)
        }
        if (ab > 0.01f) {
            val borde = c.left + c.width * sx
            for (j in 0 until 3) drawRect(a.sombra(0.1f * (3 - j) / 3f), Offset(borde, c.top), Size((3 + 4 * j) * u, c.height))
        }
        with(TapaLibretaMedieval) { lomo(s, a, k) }
    }

    /** Lo grueso del códice: las tablas, el bloque de las hojas por los dos cantos que se ven, y las correas por el canto. */
    private fun DrawScope.grueso(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, k: Float) {
        val u = s.u
        val c = s.codice; val f = s.canto
        val tabla = 2.4f * u
        val total = s.grueso
        fun en(t: Float) = Offset(f.x * t / total, f.y * t / total)
        val roble = a.luz(P.ROBLE, k)
        val izq = c.left + 2f * u
        // Las tablas (su canto de madera) y, entre ellas, el bloque de las hojas.
        drawPath(poligonoMedieval(c.right, c.top + 1f * u, c.right + f.x, c.top + f.y, c.right + f.x, c.bottom + f.y, izq + f.x, c.bottom + f.y, izq, c.bottom, c.right, c.bottom),
            mezcla(roble, a.hondo, 0.3f))
        val t0 = en(tabla); val t1 = en(maxOf(tabla + 0.5f * u, total - tabla))
        val hojas = a.luz(P.PERGAMINO, maxOf(k, 0.4f))
        // El canto de delante (a la derecha), en la sombra de día; de noche lo alumbra la vela, que está de ese lado.
        drawPath(poligonoMedieval(c.right + t0.x, c.top + 2f * u + t0.y, c.right + t1.x, c.top + 2f * u + t1.y, c.right + t1.x, c.bottom + t1.y, c.right + t0.x, c.bottom + t0.y),
            if (a.oscuro) mezcla(hojas, P.LLAMA, 0.18f * k) else mezcla(hojas, P.FRIO, 0.08f))
        drawPath(poligonoMedieval(izq + 1f * u + t0.x, c.bottom + t0.y, c.right + t0.x, c.bottom + t0.y, c.right + t1.x, c.bottom + t1.y, izq + 1f * u + t1.x, c.bottom + t1.y),
            mezcla(hojas, P.PERG_OSC, 0.25f))
        val lineas = Path()
        val n = (total / (1.5f * u)).toInt().coerceIn(1, 14)
        for (j in 1..n) {
            val p = en(tabla + (total - 2 * tabla) * j / (n + 1f))
            lineas.moveTo(c.right + p.x, c.top + 3f * u + p.y); lineas.lineTo(c.right + p.x, c.bottom + p.y)
            lineas.moveTo(izq + 2f * u + p.x, c.bottom + p.y); lineas.lineTo(c.right + p.x, c.bottom + p.y)
        }
        drawPath(lineas, a.luz(P.TINTA, k).copy(alpha = 0.26f), style = Stroke(0.5f * u))
        drawLine(a.sombra(0.3f), Offset(c.right + t1.x, c.top + 2f * u + t1.y), Offset(c.right + t1.x, c.bottom + t1.y), 0.8f * u)
        // Las correas de las manecillas, que vienen de la tabla de atrás por el canto.
        val correa = mezcla(a.luz(P.CUERO, k), a.hondo, 0.15f)
        for (fr in MANECILLAS) {
            val y = c.top + c.height * fr
            drawPath(poligonoMedieval(c.right, y - 3.5f * u, c.right + f.x, y - 3.5f * u + f.y, c.right + f.x, y + 3.5f * u + f.y, c.right, y + 3.5f * u), correa)
        }
    }

    /** Las cédulas de pergamino que asoman por la cabeza: una cada 30 hojas, hasta seis, ladeadas. */
    private fun DrawScope.cedulas(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, hoja: Color) {
        val u = s.u
        val c = s.codice
        val n = (e.hojas / 30).coerceIn(0, 6)
        for (j in 0 until n) {
            val cx = c.left + c.width * (0.25f + 0.65f * azar(e.semilla, 80 + j))
            val alto = (6f + 7f * azar(e.semilla, 90 + j)) * u
            rotate(-6f + 12f * azar(e.semilla, 100 + j), Offset(cx, c.top)) {
                drawRect(a.sombra(0.25f), Offset(cx - 4f * u, c.top - alto + 1f * u), Size(10f * u, alto))
                drawRect(hoja, Offset(cx - 5f * u, c.top - alto), Size(10f * u, alto + 2f * u))
                drawRect(a.frio(0.3f), Offset(cx - 5f * u, c.top - alto), Size(10f * u, alto + 2f * u), style = Stroke(0.5f * u))
            }
        }
    }

    /** La primera hoja, que se ve al abrirse: dos columnas pautadas, los renglones y la inicial roja. */
    private fun DrawScope.primeraHoja(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer, hoja: Color, k: Float) {
        val u = s.u
        val pag = s.codice.deflate(1f * u)
        drawRect(hoja, pag.topLeft, pag.size)
        drawRect(a.sombra(0.14f), pag.topLeft, Size(8f * u, pag.height))
        val m = Rect(pag.left + 14f * u, pag.top + 12f * u, pag.right - 10f * u, pag.bottom - 12f * u)
        val col = (m.width - 10f * u) / 2
        val renglones = Path(); val reglas = Path()
        for (q in 0..1) {
            val x0 = m.left + q * (col + 10f * u)
            reglas.moveTo(x0, m.top); reglas.lineTo(x0, m.bottom); reglas.moveTo(x0 + col, m.top); reglas.lineTo(x0 + col, m.bottom)
            var y = m.top + 4f * u; var j = 0
            while (y < m.bottom) {
                var x = x0 + if (q == 0 && j < 2) 14f * u else 2f * u
                val fin = x0 + col * (if (j % 5 == 4) 0.6f else 1f) - 2f * u
                var p = 0
                while (x < fin - 2f * u) {
                    val largo = minOf((4f + 9f * azar(e.semilla + q * 40 + j, p)) * u, fin - x)
                    renglones.moveTo(x, y); renglones.lineTo(x + largo, y)
                    x += largo + 2.2f * u; p++
                }
                y += 5.5f * u; j++
            }
        }
        drawPath(reglas, a.luz(P.RUBRICA, k).copy(alpha = 0.4f), style = Stroke(0.5f * u))
        drawPath(renglones, a.luz(P.TINTA, k).copy(alpha = 0.55f), style = Stroke(1.2f * u))
        val ini = Rect(m.left, m.top, m.left + 12f * u, m.top + 12f * u)
        drawRect(a.luz(P.RUBRICA, k), ini.topLeft, ini.size)
        textoEn(medidor, "M", ini, LetraExtra.gotica, hoja, 10f)
    }

    // ---------------------------------------------------------------- el reborde

    /**
     * El reborde donde apoya el libro, por delante de todo: su cara de arriba con la luz, la de delante con la veta y la sombra
     * que echa debajo; los registros de seda que cuelgan (uno más cada 50 hojas) y el plomo de pautar en la punta.
     */
    private fun DrawScope.reborde(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, k: Float) {
        val u = s.u
        val t = s.tablero
        val arriba = Rect(t.left - 6f * u, s.liston - 1f * u, t.right + 8f * u, s.liston + 3.5f * u)
        val frente = Rect(arriba.left, arriba.bottom, arriba.right, arriba.bottom + s.h * 0.035f)
        val kl = cerca(frente.center, s, a)
        drawRect(a.sombra(0.42f), Offset(frente.left + 3f * u, frente.bottom), Size(frente.width, 4f * u))
        drawRect(a.luz(P.NOGAL_LUZ, kl), arriba.topLeft, arriba.size)
        drawRect(a.claro(0.4f), arriba.topLeft, Size(arriba.width, 1.2f * u))
        drawRect(a.luz(P.NOGAL, kl), frente.topLeft, frente.size)
        vetas(frente, a.luz(P.NOGAL_OSC, kl).copy(alpha = 0.4f), 3230, densidad = 1f, grosor = 0.6f, ondulacion = 0.5f)
        drawRect(a.frio(0.4f), Offset(frente.left, frente.bottom - 2f * u), Size(frente.width, 2f * u))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, if (a.oscuro) a.filo(0.35f * kl) else a.frio(0.2f)), frente.left, frente.right), frente.topLeft, frente.size)
        // Los registros que cuelgan por delante del reborde.
        val c = s.codice
        val n = (1 + e.hojas / 50).coerceAtMost(5)
        for (j in 0 until n) {
            val x = c.left + c.width * (0.3f + 0.1f * j) + s.canto.x * 0.5f
            val largo = (14f + 7f * azar(e.semilla, 120 + j)) * u
            val seda = a.luz(SEDAS[j], k)
            drawLine(a.sombra(0.3f), Offset(x + 1.5f * u, frente.top + 1f * u), Offset(x + 2.5f * u, frente.top + largo + 1f * u), 1.6f * u)
            drawLine(seda, Offset(x, c.bottom + s.canto.y - 2f * u), Offset(x + 1f * u, frente.top + largo), 1.6f * u)
            drawRoundRect(seda, Offset(x - 1.5f * u, frente.top + largo - 1f * u), Size(5f * u, 5f * u), CornerRadius(1.5f * u))
        }
        // El plomo de pautar, tumbado en la punta del reborde.
        val p = Offset(arriba.right - 16f * u, arriba.top - 1f * u)
        drawLine(a.sombra(0.35f), p + Offset(1f * u, 1.6f * u), p + Offset(13f * u, 0.6f * u), 2.6f * u, StrokeCap.Round)
        drawLine(a.luz(P.GRIS, maxOf(kl, 0.4f)), p, p + Offset(12f * u, -1f * u), 2.6f * u, StrokeCap.Round)
        drawLine(a.claro(0.4f), p + Offset(1f * u, -0.8f * u), p + Offset(11f * u, -1.8f * u), 0.6f * u)
    }
}
