package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * La tapa del códice de momentos ([FacistolLibretaMedieval]): la tabla de roble de veta calmada, algo inclinada sobre el
 * tablero (más angosta arriba), la media piel del lomo con sus nervios en relieve, la etiqueta de pergamino con el nombre y
 * la cuenta, las dos manecillas de latón y el lomo redondo.
 */
internal object TapaLibretaMedieval {
    private val P = PaletaMedieval
    private val LATON = Color(0xFFB8924C)
    private val MANECILLAS = listOf(0.17f, 0.83f)
    private val NERVIOS = listOf(0.16f, 0.38f, 0.62f, 0.84f)

    fun DrawScope.tapa(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer, k: Float, hoja: Color) {
        val u = s.u
        val c = s.codice
        val entra = 4.5f * u
        val forma = poligonoMedieval(c.left + entra, c.top, c.right - entra, c.top, c.right, c.bottom, c.left, c.bottom)
        drawPath(forma, a.luz(P.ROBLE, k))
        clipPath(forma) {
            vetas(c, a.luz(P.NOGAL_OSC, k).copy(alpha = 0.26f), 3401, densidad = 0.3f, vertical = true, grosor = 0.6f, ondulacion = 0.2f)
            pinceladas(c, a.claro(1f), 3402, 8, u, 0.07f)
            pinceladas(c, a.sombra(1f), 3403, 6, u, 0.06f)
        }
        val luz = if (a.oscuro) Brush.linearGradient(listOf(Color.Transparent, a.filo(0.22f * k + 0.04f)), c.bottomLeft, c.topRight)
            else Brush.linearGradient(listOf(a.claro(0.26f), a.frio(0.24f)), c.topLeft, c.bottomRight)
        drawPath(forma, luz)
        // Los cantos de la tabla: arriba y a la izquierda la luz; abajo y a la derecha la sombra (de noche, el filo de la vela).
        drawLine(a.claro(0.45f), Offset(c.left + entra, c.top + 0.8f * u), Offset(c.right - entra, c.top + 0.8f * u), 1.6f * u)
        drawLine(a.frio(0.45f), Offset(c.left, c.bottom - 1f * u), Offset(c.right, c.bottom - 1f * u), 2f * u)
        drawLine(if (a.oscuro) a.filo(0.55f * k) else a.frio(0.35f), Offset(c.right - entra - 0.8f * u, c.top), Offset(c.right - 0.8f * u, c.bottom), 1.6f * u)
        // El roce de las manos en la orilla de abajo y una mancha de tinta vieja.
        drawOval(a.claro(0.12f), Offset(c.left + c.width * 0.35f, c.bottom - c.height * 0.12f), Size(c.width * 0.55f, c.height * 0.08f))
        drawOval(a.luz(P.TINTA, k).copy(alpha = 0.3f), Offset(c.left + c.width * (0.55f + 0.2f * azar(e.semilla, 5)), c.top + c.height * 0.6f), Size(5f * u, 3f * u))
        mediaPiel(s, a, k)
        etiqueta(s, a, e, medidor, k, hoja)
        manecillas(s, a, k)
    }

    /** La media piel: el cuero del lomo que monta sobre la tabla, con los nervios en relieve (luz arriba, sombra abajo) y sus clavos. */
    private fun DrawScope.mediaPiel(s: SitioLibretaMedieval, a: AireMedieval, k: Float) {
        val u = s.u
        val c = s.codice
        val ancho = c.width * 0.21f
        val piel = poligonoMedieval(c.left + 4.5f * u, c.top, c.left + ancho + 2f * u, c.top, c.left + ancho, c.bottom, c.left, c.bottom)
        val cuero = a.luz(P.CUERO, k)
        drawPath(piel, cuero)
        drawPath(piel, Brush.horizontalGradient(listOf(a.claro(0.2f), Color.Transparent, a.frio(0.3f)), c.left, c.left + ancho))
        pinceladas(Rect(c.left, c.top, c.left + ancho, c.bottom), mezcla(cuero, P.BADANA, 0.3f), 3410, 5, u, 0.12f)
        drawLine(a.sombra(0.45f), Offset(c.left + ancho + 1.4f * u, c.top), Offset(c.left + ancho + 0.6f * u, c.bottom), 1.8f * u)
        // Los nervios: los cordeles de la costura, que entran en la tabla junto al lomo y levantan el cuero en un tramo corto.
        val tramo = ancho * 0.42f
        for (f in NERVIOS) {
            val y = c.top + c.height * f
            drawRoundRect(a.frio(0.5f), Offset(c.left + 1f * u, y + 0.4f * u), Size(tramo, 2.4f * u), CornerRadius(1.2f * u))
            drawRoundRect(mezcla(cuero, P.BADANA, 0.14f), Offset(c.left, y - 2f * u), Size(tramo, 3.4f * u), CornerRadius(1.7f * u))
            drawRect(if (a.oscuro) a.filo(0.4f * k + 0.05f) else a.claro(0.45f), Offset(c.left + 1f * u, y - 1.8f * u), Size(tramo - 2f * u, 0.8f * u))
        }
        // El canto del cuero que monta sobre la tabla (la vuelta), más hondo.
        drawLine(mezcla(cuero, a.hondo, 0.3f), Offset(c.left + ancho + 1.4f * u, c.top + 1f * u), Offset(c.left + ancho - 0.2f * u, c.bottom - 1f * u), 1.2f * u)
        for (y in listOf(c.top + 7f * u, c.bottom - 7f * u)) clavoMedieval(Offset(c.left + ancho - 4f * u, y), 1.5f * u, a.luz(P.HIERRO, k), a, k)
    }

    /** La etiqueta de pergamino clavada en la tabla: el nombre en letra gótica y la cuenta en dos renglones. */
    private fun DrawScope.etiqueta(s: SitioLibretaMedieval, a: AireMedieval, e: EstadoLibreta, medidor: TextMeasurer, k: Float, hoja: Color) {
        val u = s.u
        val c = s.codice
        val et = Rect(c.left + c.width * 0.27f, c.top + c.height * 0.27f, c.right - c.width * 0.07f, c.top + c.height * 0.62f)
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

    /**
     * Las dos manecillas: la correa que viene de la tabla de atrás por el canto y se tiende sobre la tapa, la chapa de latón
     * remachada en su punta, con su lengüeta, y el pasador clavado en la tabla donde se engancha.
     */
    private fun DrawScope.manecillas(s: SitioLibretaMedieval, a: AireMedieval, k: Float) {
        val u = s.u
        val c = s.codice
        val correa = mezcla(a.luz(P.CUERO, k), a.hondo, 0.15f)
        val laton = a.luz(LATON, maxOf(k, 0.35f))
        for (f in MANECILLAS) {
            val y = c.top + c.height * f
            val x0 = c.right - 22f * u
            drawRect(a.sombra(0.38f), Offset(x0 + 1f * u, y - 3.5f * u + 1.8f * u), Size(c.right - x0, 7f * u))
            drawRect(correa, Offset(x0 + 6f * u, y - 3.5f * u), Size(c.right - x0 - 6f * u, 7f * u))
            drawRect(a.claro(0.2f), Offset(x0 + 6f * u, y - 3.5f * u), Size(c.right - x0 - 6f * u, 0.9f * u))
            val chapa = poligonoMedieval(x0 + 11f * u, y - 4.4f * u, x0 + 2.5f * u, y - 4.4f * u, x0 - 3f * u, y, x0 + 2.5f * u, y + 4.4f * u, x0 + 11f * u, y + 4.4f * u)
            drawPath(chapa, laton)
            drawPath(chapa, mezcla(laton, a.hondo, 0.45f), style = Stroke(0.6f * u))
            drawLine(if (a.oscuro) a.filo(0.7f * k + 0.1f) else a.claro(0.55f), Offset(x0 + 2.5f * u, y - 3.7f * u), Offset(x0 + 10.5f * u, y - 3.7f * u), 0.8f * u)
            for (dx in listOf(5f, 8.4f)) drawCircle(mezcla(laton, a.hondo, 0.5f), 0.8f * u, Offset(x0 + dx * u, y))
            drawCircle(a.sombra(0.4f), 1.7f * u, Offset(x0 - 3.6f * u, y + 0.9f * u))
            drawCircle(a.luz(P.HIERRO, maxOf(k, 0.3f)), 1.5f * u, Offset(x0 - 4f * u, y))
        }
    }

    /** El lomo redondo, a la izquierda de la tapa: el cuero, su cresta gastada y los nervios que asoman en relieve. */
    fun DrawScope.lomo(s: SitioLibretaMedieval, a: AireMedieval, k: Float) {
        val u = s.u
        val c = s.codice
        val cuero = mezcla(a.luz(P.CUERO, k), a.hondo, 0.22f)
        val l = Rect(c.left - 7f * u, c.top - 0.5f * u, c.left + 1.5f * u, c.bottom + 0.5f * u)
        drawRoundRect(cuero, l.topLeft, l.size, CornerRadius(3.5f * u))
        drawLine(mezcla(cuero, P.BADANA, 0.3f), Offset(l.left + 2.4f * u, l.top + 4f * u), Offset(l.left + 2.4f * u, l.bottom - 4f * u), 1.1f * u)
        for (f in NERVIOS) {
            val y = c.top + c.height * f
            drawRoundRect(mezcla(cuero, P.BADANA, 0.1f), Offset(l.left - 1.6f * u, y - 2.6f * u), Size(l.width + 1f * u, 4.2f * u), CornerRadius(2f * u))
            drawRect(a.claro(0.35f), Offset(l.left - 1f * u, y - 2.4f * u), Size(l.width, 0.8f * u))
            drawRect(a.frio(0.5f), Offset(l.left - 1f * u, y + 1.4f * u), Size(l.width, 1.2f * u))
        }
    }
}
