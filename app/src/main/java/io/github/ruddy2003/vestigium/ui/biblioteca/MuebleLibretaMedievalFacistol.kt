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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/**
 * El facistol de roble y el códice de momentos de la libreta de Scriptorium ([MuebleLibretaMedieval]): el pie torneado, el
 * faldón, el tablero inclinado con su listón y, encima, el códice en tablas con media piel, que engorda con las hojas.
 */
internal object FacistolLibretaMedieval {
    private val P = PaletaMedieval
    private val SEDAS = listOf(P.RUBRICA, Color(0xFF3F5C8C), P.OCRE_PINTURA, P.VERDE, mezcla(P.ALMAGRE, P.NOGAL_OSC, 0.2f))
    private val LATON = Color(0xFFB08A48)

    fun DrawScope.facistol(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer) {
        val k = MuebleLibretaMedieval.cerca(s.codice.center, s, a)
        sombraMuro(s, a)
        pie(s, a)
        tablero(s, a)
        codice(s, a, e, medidor, k)
        liston(s, a, e, k)
    }

    /** La sombra del facistol y del códice en el muro: de día a la derecha; de noche, lejos de la vela (a la izquierda). */
    private fun DrawScope.sombraMuro(s: SitioLibretaMedieval, a: AireMedieval) {
        val t = s.tablero
        val d = if (a.oscuro) Offset(-s.w * 0.07f, s.h * 0.03f) else Offset(s.w * 0.045f, s.h * 0.035f)
        val sil = poligonoMedieval(t.left + 5f * s.u, t.top, t.right - 5f * s.u, t.top, t.right, t.bottom, t.left, t.bottom)
        drawPath(corridoMedieval(sil, d.x, d.y), a.sombra(if (a.oscuro) 0.3f else 0.2f))
        drawRect(a.sombra(if (a.oscuro) 0.25f else 0.16f), Offset(s.eje - 5f * s.u + d.x, t.bottom), Size(10f * s.u, s.suelo - t.bottom))
    }

    /** El pie: el fuste torneado de anillos y bolas y las tres patas que pisan el suelo con su sombra. */
    private fun DrawScope.pie(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val x = s.eje
        val y0 = s.liston + s.h * 0.08f; val y1 = s.h * 0.95f
        val k = MuebleLibretaMedieval.cerca(Offset(x, (y0 + y1) / 2), s, a)
        val madera = a.luz(P.NOGAL, k)
        contactoMedieval(Offset(x + 6f * u, y1 + 2f * u), s.w * 0.24f, 7f * u, a, 0.36f)
        // Las tres patas, la de delante más baja.
        for ((dx, dy) in listOf(-1f to 0f, 1f to 0f, 0.15f to 1f)) {
            val pata = poligonoMedieval(x - 4f * u, y1 - 9f * u, x + 4f * u, y1 - 9f * u, x + dx * s.w * 0.11f + 3f * u, y1 + dy * 3f * u, x + dx * s.w * 0.11f - 3f * u, y1 + dy * 3f * u - 3f * u)
            drawPath(pata, mezcla(madera, a.hondo, if (dy > 0) 0f else 0.15f))
            drawPath(pata, a.frio(0.3f), style = Stroke(0.7f * u))
        }
        // El fuste, con sus anillos torneados.
        val fuste = Rect(x - 5f * u, y0, x + 5f * u, y1 - 6f * u)
        drawRect(madera, fuste.topLeft, fuste.size)
        drawRect(Brush.horizontalGradient(listOf(a.claro(0.25f), Color.Transparent, a.frio(0.4f)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
        var y = y0 + 4f * u; var i = 0
        while (y < y1 - 10f * u) {
            val r = if (i % 2 == 0) 7.5f * u else 6.5f * u
            drawRoundRect(madera, Offset(x - r, y), Size(2 * r, 3.6f * u), CornerRadius(1.8f * u))
            drawRect(a.claro(0.3f), Offset(x - r + 1f * u, y + 0.4f * u), Size(2 * r - 2f * u, 0.8f * u))
            drawRect(a.frio(0.35f), Offset(x - r + 1f * u, y + 2.6f * u), Size(2 * r - 2f * u, 1f * u))
            y += if (i % 2 == 0) 7f * u else 11f * u; i++
        }
        if (a.oscuro) drawRect(a.filo(0.3f * k), Offset(fuste.right - 1.2f * u, fuste.top), Size(1.2f * u, fuste.height))
    }

    /** El faldón y el tablero inclinado de nogal (se ve más angosto arriba, porque se aleja), con su veta, sus cantos y su luz. */
    private fun DrawScope.tablero(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val t = s.tablero
        val k = MuebleLibretaMedieval.cerca(t.center, s, a)
        val nogal = a.luz(P.NOGAL, k)
        // El faldón bajo el listón, con tres rosetas taladradas.
        val falda = Rect(t.left + 12f * u, s.liston + 8f * u, t.right - 12f * u, s.liston + s.h * 0.085f)
        drawRect(nogal, falda.topLeft, falda.size)
        drawRect(a.frio(0.35f), Offset(falda.left, falda.bottom - 3f * u), Size(falda.width, 3f * u))
        for (f in listOf(0.2f, 0.5f, 0.8f)) {
            val c = Offset(falda.left + falda.width * f, falda.center.y - 0.5f * u)
            drawCircle(a.luz(P.NOGAL_OSC, k), 3.2f * u, c)
            drawArc(a.claro(0.3f), 20f, 140f, false, c - Offset(3.2f * u, 3.2f * u), Size(6.4f * u, 6.4f * u), style = Stroke(0.8f * u))
        }
        // El tablero.
        val forma = poligonoMedieval(t.left + 6f * u, t.top, t.right - 6f * u, t.top, t.right, t.bottom, t.left, t.bottom)
        drawPath(forma, nogal)
        vetas(t, a.luz(P.NOGAL_OSC, k).copy(alpha = 0.5f), 3201, densidad = 0.35f, vertical = true, grosor = 0.7f)
        drawPath(forma, Brush.linearGradient(listOf(a.claro(0.18f), Color.Transparent, a.frio(0.25f)), t.topLeft, t.bottomRight))
        // Los cantos: el de arriba toma la luz; el de la derecha, en sombra (de noche, el filo de la vela).
        drawLine(a.claro(0.45f), Offset(t.left + 6f * u, t.top + 0.6f * u), Offset(t.right - 6f * u, t.top + 0.6f * u), 1.2f * u)
        drawLine(if (a.oscuro) a.filo(0.5f * k) else a.frio(0.4f), Offset(t.right - 6f * u, t.top), Offset(t.right, t.bottom), 2f * u)
        drawLine(a.claro(0.25f), Offset(t.left + 6f * u, t.top), Offset(t.left, t.bottom), 1.2f * u)
    }

    // ---------------------------------------------------------------- el códice

    /**
     * El códice de momentos: la tabla de atrás y el bloque de las hojas (la cabeza y el canto, más gruesos con más hojas), las
     * cédulas que asoman, la primera hoja si se abre, la tapa de roble con su media piel, la etiqueta, la correa y el lomo.
     */
    private fun DrawScope.codice(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer, k: Float) {
        val u = s.u
        val c = s.codice
        val g = s.grueso
        val ab = e.abierto.coerceIn(0f, 1f)
        val hoja = a.etiqueta(maxOf(k, 0.55f))
        // La sombra del códice en el tablero, abajo a la derecha (de noche, a la izquierda).
        val d = if (a.oscuro) Offset(-5f * u, 4f * u) else Offset(5f * u, 5f * u)
        drawRect(a.sombra(0.4f), Offset(c.left + d.x + g * 0.5f, c.top + d.y - g * 0.5f), Size(c.width + g * 0.5f, c.height))
        // La tabla de atrás y el bloque de hojas visto por la cabeza y por el canto.
        drawRect(a.luz(P.ROBLE, k * 0.8f), Offset(c.left + g, c.top - g * 0.5f), c.size)
        val bloque = poligonoMedieval(c.left + 3f * u, c.top, c.left + 3f * u + g, c.top - g * 0.5f + 1.5f * u, c.right + g - 1.5f * u, c.top - g * 0.5f + 1.5f * u,
            c.right + g - 1.5f * u, c.bottom - g * 0.5f - 1f * u, c.right, c.bottom - 1f * u, c.right, c.top)
        drawPath(bloque, a.luz(P.PERGAMINO, k))
        val hojas = Path()
        val capas = (g / (1.6f * u)).toInt().coerceIn(2, 14)
        for (j in 1..capas) {
            val f = j / (capas + 1f)
            hojas.moveTo(c.right + g * f, c.top - g * f * 0.5f); hojas.lineTo(c.right + g * f, c.bottom - g * f * 0.5f)
            hojas.moveTo(c.left + g * f, c.top - g * f * 0.5f); hojas.lineTo(c.right + g * f, c.top - g * f * 0.5f)
        }
        drawPath(hojas, a.luz(P.TINTA, k).copy(alpha = 0.16f), style = Stroke(0.5f * u))
        // El canto, en la sombra fría; de noche lo alumbra la vela, que está a la derecha.
        drawPath(poligonoMedieval(c.right, c.top, c.right + g - 1.5f * u, c.top - g * 0.5f + 1.5f * u, c.right + g - 1.5f * u, c.bottom - g * 0.5f - 1f * u, c.right, c.bottom - 1f * u),
            if (a.oscuro) a.filo(0.18f * k) else a.frio(0.22f))
        cedulas(s, a, e, hoja)
        if (ab > 0.01f) primeraHoja(s, a, e, medidor, hoja, k)
        val sx = 1f - 0.94f * ab
        scale(sx, 1f, Offset(c.left, c.center.y)) {
            tapa(s, a, e, medidor, k, hoja)
            if (ab > 0f) drawRect(a.sombra(0.45f * ab), c.topLeft, c.size)
        }
        if (ab > 0.01f) {
            val borde = c.left + c.width * sx
            for (j in 0 until 3) drawRect(a.sombra(0.1f * (3 - j) / 3f), Offset(borde, c.top), Size((3 + 4 * j) * u, c.height))
        }
        // El lomo redondeado, a la izquierda.
        drawRoundRect(mezcla(a.luz(P.CUERO, k), a.hondo, 0.25f), Offset(c.left - 6f * u, c.top - 1f * u), Size(7f * u, c.height + 1f * u), CornerRadius(3f * u))
        drawLine(a.claro(0.3f), Offset(c.left - 4.5f * u, c.top + 3f * u), Offset(c.left - 4.5f * u, c.bottom - 3f * u), 0.9f * u)
    }

    /** Las cédulas de pergamino que asoman por la cabeza: una cada 30 hojas, hasta seis, ladeadas. */
    private fun DrawScope.cedulas(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, hoja: Color) {
        val u = s.u
        val c = s.codice
        val n = (e.hojas / 30).coerceIn(0, 6)
        for (j in 0 until n) {
            val cx = c.left + c.width * (0.15f + 0.7f * azar(e.semilla, 80 + j)) + s.grueso * 0.5f
            val alto = (6f + 7f * azar(e.semilla, 90 + j)) * u
            rotate(-6f + 12f * azar(e.semilla, 100 + j), Offset(cx, c.top)) {
                drawRect(hoja, Offset(cx - 5f * u, c.top - s.grueso * 0.3f - alto), Size(10f * u, alto + 2f * u))
                drawRect(a.frio(0.3f), Offset(cx - 5f * u, c.top - s.grueso * 0.3f - alto), Size(10f * u, alto + 2f * u), style = Stroke(0.5f * u))
            }
        }
    }

    /** La primera hoja, que se ve al abrirse: dos columnas pautadas, los renglones y la inicial roja. */
    private fun DrawScope.primeraHoja(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer, hoja: Color, k: Float) {
        val u = s.u
        val pag = s.codice.deflate(2f * u)
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

    /**
     * La tapa: tabla de roble desnuda (su veta, sus cantos, la luz que le da), la media piel del lomo clavada, la etiqueta de
     * pergamino con el nombre y la cuenta, y la correa que da la vuelta desde el canto hasta su broche de latón.
     */
    private fun DrawScope.tapa(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer, k: Float, hoja: Color) {
        val u = s.u
        val c = s.codice
        drawRect(a.luz(P.ROBLE, k), c.topLeft, c.size)
        vetas(c, a.luz(P.NOGAL_OSC, k).copy(alpha = 0.45f), e.semilla, densidad = 0.6f, vertical = true, grosor = 0.8f)
        val luz = if (a.oscuro) Brush.linearGradient(listOf(Color.Transparent, a.filo(0.16f * k)), c.bottomLeft, c.topRight)
            else Brush.linearGradient(listOf(a.claro(0.22f), Color.Transparent, a.frio(0.22f)), c.topLeft, c.bottomRight)
        drawRect(luz, c.topLeft, c.size)
        drawRect(a.claro(0.35f), c.topLeft, Size(c.width, 1.6f * u))
        drawRect(a.frio(0.4f), Offset(c.left, c.bottom - 2f * u), Size(c.width, 2f * u))
        drawRect(if (a.oscuro) a.filo(0.45f * k) else a.frio(0.3f), Offset(c.right - 1.6f * u, c.top), Size(1.6f * u, c.height))
        // El roce de las manos en la orilla de abajo y una mancha de tinta vieja.
        drawOval(a.claro(0.12f), Offset(c.left + c.width * 0.3f, c.bottom - c.height * 0.12f), Size(c.width * 0.6f, c.height * 0.08f))
        drawOval(a.luz(P.TINTA, k).copy(alpha = 0.35f), Offset(c.left + c.width * (0.55f + 0.2f * azar(e.semilla, 5)), c.top + c.height * 0.62f), Size(5f * u, 3f * u))
        // La media piel: el cuero del lomo que monta sobre la tabla, con sus nervios y dos clavos.
        val piel = Rect(c.left, c.top, c.left + c.width * 0.19f, c.bottom)
        val cuero = a.luz(P.CUERO, k)
        drawRect(cuero, piel.topLeft, piel.size)
        drawRect(Brush.horizontalGradient(listOf(a.claro(0.18f), Color.Transparent, a.frio(0.3f)), piel.left, piel.right), piel.topLeft, piel.size)
        drawRect(a.sombra(0.35f), Offset(piel.right, piel.top), Size(1.6f * u, piel.height))
        for (f in listOf(0.18f, 0.38f, 0.62f, 0.82f)) {
            val y = c.top + c.height * f
            drawRect(a.claro(0.2f), Offset(piel.left, y - 2f * u), Size(piel.width - 2f * u, 1.2f * u))
            drawRect(a.frio(0.45f), Offset(piel.left, y - 0.8f * u), Size(piel.width - 2f * u, 1.4f * u))
        }
        for (y in listOf(piel.top + 7f * u, piel.bottom - 7f * u)) clavoMedieval(Offset(piel.right - 5f * u, y), 1.6f * u, a.luz(P.HIERRO, k), a, k)
        etiqueta(s, a, e, medidor, k, hoja, piel)
        correa(s, a, k, cuero)
    }

    /** La etiqueta de pergamino clavada en la tabla: el nombre en letra gótica y la cuenta en dos renglones. */
    private fun DrawScope.etiqueta(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer, k: Float, hoja: Color, piel: Rect) {
        val u = s.u
        val c = s.codice
        val et = Rect(piel.right + c.width * 0.07f, c.top + c.height * 0.12f, c.right - c.width * 0.06f, c.top + c.height * 0.5f)
        drawRect(a.sombra(0.4f), et.topLeft + Offset(1.6f * u, 2f * u), et.size)
        drawRect(hoja, et.topLeft, et.size)
        drawRect(Brush.linearGradient(listOf(a.claro(0.2f), Color.Transparent, P.PERG_OSC.copy(alpha = 0.3f)), et.topLeft, et.bottomRight), et.topLeft, et.size)
        val pauta = et.deflate(3f * u)
        drawRect(a.luz(P.RUBRICA, k).copy(alpha = 0.55f), pauta.topLeft, pauta.size, style = Stroke(0.6f * u))
        drawRect(a.frio(0.3f), et.topLeft, et.size, style = Stroke(0.6f * u))
        for (p in listOf(et.topLeft, Offset(et.right, et.top), Offset(et.left, et.bottom), et.bottomRight))
            clavoMedieval(p + Offset(if (p.x > et.center.x) -2f * u else 2f * u, if (p.y > et.center.y) -2f * u else 2f * u), 1.1f * u, a.luz(P.HIERRO, k), a, k)
        val corte = et.top + et.height * 0.48f
        textoEn(medidor, e.nombre, Rect(et.left + 5f * u, et.top + 3f * u, et.right - 5f * u, corte), LetraExtra.gotica, P.TINTA, 15f, minimo = 8f)
        drawLine(a.luz(P.RUBRICA, k).copy(alpha = 0.7f), Offset(et.left + et.width * 0.25f, corte + 0.5f * u), Offset(et.right - et.width * 0.25f, corte + 0.5f * u), 0.7f * u)
        val partes = e.cuenta.split(" · ", limit = 2)
        val tinta = P.TINTA.copy(alpha = 0.88f)
        if (partes.size == 2) {
            val medio = (corte + et.bottom) / 2 + 0.5f * u
            textoEn(medidor, partes[0], Rect(et.left + 5f * u, corte + 1.5f * u, et.right - 5f * u, medio), P.LETRA, tinta, 10.5f, minimo = 6f)
            textoEn(medidor, partes[1], Rect(et.left + 5f * u, medio - 1f * u, et.right - 5f * u, et.bottom - 3f * u), P.LETRA, tinta, 9.5f, minimo = 6f)
        } else textoEn(medidor, e.cuenta, Rect(et.left + 5f * u, corte + 1.5f * u, et.right - 5f * u, et.bottom - 3f * u), P.LETRA, tinta, 10.5f, minimo = 6f)
    }

    /** La correa de cuero que viene del canto y se abrocha con su chapa de latón y el pasador. */
    private fun DrawScope.correa(s: SitioLibretaMedieval, a: AireMedieval, k: Float, cuero: Color) {
        val u = s.u
        val c = s.codice
        val y = c.top + c.height * 0.73f
        val chapa = Rect(c.right - c.width * 0.3f, y - 6f * u, c.right - c.width * 0.18f, y + 6f * u)
        drawRect(a.sombra(0.3f), Offset(chapa.left, y - 4f * u + 1.6f * u), Size(c.right - chapa.left, 8f * u))
        drawRect(mezcla(cuero, a.hondo, 0.12f), Offset(chapa.center.x, y - 4f * u), Size(c.right - chapa.center.x + s.grueso * 0.4f, 8f * u))
        drawRect(a.claro(0.2f), Offset(chapa.center.x, y - 4f * u), Size(c.right - chapa.center.x, 1f * u))
        val laton = a.luz(LATON, k)
        drawRect(laton, chapa.topLeft, chapa.size)
        drawRect(mezcla(laton, a.hondo, 0.4f), chapa.deflate(2f * u).topLeft, chapa.deflate(2f * u).size, style = Stroke(0.7f * u))
        drawRect(a.claro(0.5f), chapa.topLeft, Size(chapa.width, 1f * u))
        if (a.oscuro) drawRect(a.filo(0.6f * k), Offset(chapa.right - 1.2f * u, chapa.top), Size(1.2f * u, chapa.height))
        drawCircle(a.luz(P.HIERRO, k), 1.5f * u, Offset(chapa.left - 3f * u, y))
        drawLine(laton, Offset(chapa.left - 3f * u, y), Offset(chapa.left, y), 1.6f * u)
    }

    // ---------------------------------------------------------------- el listón

    /** El listón donde descansa el códice, con los registros de seda que cuelgan (uno más cada 50 hojas) y el plomo de pautar. */
    private fun DrawScope.liston(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, k: Float) {
        val u = s.u
        val t = s.tablero
        val l = Rect(t.left - 5f * u, s.liston, t.right + 5f * u, s.liston + s.h * 0.045f)
        val roble = a.luz(P.NOGAL_LUZ, k)
        drawRect(a.sombra(0.4f), Offset(l.left + 2f * u, l.bottom), Size(l.width, 3f * u))
        drawRect(roble, l.topLeft, l.size)
        vetas(l, a.luz(P.NOGAL_OSC, k).copy(alpha = 0.4f), 3230, densidad = 1f, grosor = 0.6f)
        drawRect(a.claro(0.4f), l.topLeft, Size(l.width, 2f * u))
        drawRect(a.frio(0.4f), Offset(l.left, l.bottom - 2f * u), Size(l.width, 2f * u))
        if (a.oscuro) drawRect(a.filo(0.5f * k), l.topLeft, Size(l.width, 1.2f * u))
        // Los registros que cuelgan por delante del listón.
        val c = s.codice
        val n = (1 + e.hojas / 50).coerceAtMost(5)
        for (j in 0 until n) {
            val x = c.left + c.width * (0.3f + 0.1f * j) + s.grueso * 0.3f
            val largo = (14f + 7f * azar(e.semilla, 120 + j)) * u
            val seda = a.luz(SEDAS[j], k)
            drawLine(a.sombra(0.3f), Offset(x + 1.5f * u, l.top + 1f * u), Offset(x + 2.5f * u, l.top + largo + 1f * u), 1.6f * u)
            drawLine(seda, Offset(x, c.bottom - 2f * u), Offset(x + 1f * u, l.top + largo), 1.6f * u)
            drawRoundRect(seda, Offset(x - 1.5f * u, l.top + largo - 1f * u), Size(5f * u, 5f * u), CornerRadius(1.5f * u))
        }
        // El plomo de pautar, tumbado en la punta del listón.
        val p = Offset(l.right - 14f * u, l.top - 1.6f * u)
        drawLine(a.sombra(0.35f), p + Offset(1f * u, 1.6f * u), p + Offset(13f * u, 0.6f * u), 2.6f * u, StrokeCap.Round)
        drawLine(a.luz(P.GRIS, k * 0.9f), p, p + Offset(12f * u, -1f * u), 2.6f * u, StrokeCap.Round)
        drawLine(a.claro(0.4f), p + Offset(1f * u, -0.8f * u), p + Offset(11f * u, -1.8f * u), 0.6f * u)
    }
}
