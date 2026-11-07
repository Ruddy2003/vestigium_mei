package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.arcoHerradura
import io.github.ruddy2003.vestigium.ui.ideario.arcoMedioPunto
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.cruzPatada
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Edad Media
// =====================================================================

/**
 * Medieval: la librería encadenada. Atriles de roble en pendiente bajo cada repisa y, a lo largo de cada una, la barra de
 * hierro donde se enganchan las cadenas, trabada en los costados con su cerradura. Arriba, un muro de sillares con un arco
 * románico de dovelas y columnillas sobre una viga de roble pintada en zigzag de bermellón y lapislázuli; en el vano, una
 * saetera por donde entra el día y una vela en su palmatoria, que de noche se enciende. Cada entrada es un libro de cuero
 * sobre tablas con sus nervios, un bollón de latón arriba (de noche, una luna de oro), el broche con su chapa y la etiqueta
 * de pergamino con el día, el mes dorado entre nervios y la cadena que baja del canto hasta la barra, que pasa por delante.
 * Distinto del Ideario, que es el armario cerrado del scriptorium con su arquería pintada.
 */
object EstanteMedieval : DisenoBiblioteca {
    private val ROBLE = Color(0xFF6E4A2A)
    private val ROBLE_LUZ = Color(0xFF8E6942)
    private val ROBLE_OSC = Color(0xFF3F2A18)
    private val VETA = Color(0xFF2B1B0E)
    private val HIERRO = Color(0xFF2B2A2D)
    private val HIERRO_LUZ = Color(0xFF6E6D73)
    private val PIEDRA = Color(0xFFA59C8A)
    private val PIEDRA_OSC = Color(0xFF8A806E)
    private val JUNTA = Color(0xFF6B6356)
    private val HUECO = Color(0xFF211913)
    private val DIA = Color(0xFFCAD6DA)
    private val PERGAMINO = Color(0xFFEADFBF)
    private val TINTA = Color(0xFF2A1B10)
    private val BERMELLON = Color(0xFFA5321F)
    private val LAPIZ = Color(0xFF2E4C88)
    private val ORO = Color(0xFFDDBE76)
    private val LATON = Color(0xFFB8924C)
    private val CERA = Color(0xFFEDE3C4)
    private val LLAMA = Color(0xFFF7C45A)
    private val BALDOSA = Color(0xFF8A4B2E)
    /** Los cueros de las tapas: castaño, tabaco, rojo de rubia, oliva y leonado. */
    private val CUEROS = listOf(Color(0xFF6B3A22), Color(0xFF4B2C1C), Color(0xFF7A3024), Color(0xFF574A30), Color(0xFF5E4029))
    private val NOCHE = Color(0xFF1B1626)
    /** A cuántos dp del piso de la repisa corre la barra de las cadenas (la misma en el mueble y en cada libro). */
    private val BARRA = 7f

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la penumbra de la vela. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100C08), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.42f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.1f
    override val lado = 0.05f
    override val altoRepisa: Dp = 114.dp
    override val tabla: Dp = 18.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.cardo

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.78f + 0.16f * azar(semilla, 1)

    /** La barra de hierro de las cadenas, de [x0] a [x1] a la altura [y]: la dibujan igual el mueble y cada libro. */
    private fun DrawScope.barra(x0: Float, x1: Float, y: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawLine(t(HIERRO, oscuro, noche), Offset(x0, y), Offset(x1, y), 2.4f * u)
        drawLine(t(HIERRO_LUZ, oscuro, noche), Offset(x0, y - 0.7f * u), Offset(x1, y - 0.7f * u), 0.6f * u)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo: tablas de roble a lo alto, ensambladas, en la penumbra de la sala.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(ROBLE_OSC), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(VETA).copy(alpha = 0.5f), 12, densidad = 0.6f, vertical = true, grosor = 0.8f)
        var x = cuerpo.left + 34 * u
        while (x < cuerpo.right) {
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 1.2f * u)
            drawLine(Color.White.copy(alpha = 0.05f), Offset(x + 1.2f * u, cuerpo.top), Offset(x + 1.2f * u, cuerpo.bottom), 0.6f * u)
            x += 34 * u
        }
        // Cada repisa: la sombra de la tabla de arriba y la barra de las cadenas con sus grapas en las puntas.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.28f), r.topLeft, Size(r.width, 11 * u))
            drawRect(Color.Black.copy(alpha = 0.18f), r.topLeft, Size(r.width, 4 * u))
            val yb = r.bottom - BARRA * u
            listOf(r.left + 4 * u, r.right - 4 * u).forEach { gx ->
                drawRect(c(HIERRO), Offset(gx - 2 * u, yb - 4 * u), Size(4 * u, 4 * u + BARRA * u))
            }
            barra(r.left, r.right, yb, oscuro, noche)
        }
        // Los costados: roble con la cerradura donde se traba la barra de cada repisa.
        listOf(d.izquierda, d.derecha).forEachIndexed { k, r ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.55f), 3 + k, densidad = 1.2f, vertical = true, grosor = 0.8f)
            bisel(r, Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.3f), 1.4f * u)
            d.repisas.forEach { rp ->
                val yb = rp.bottom - BARRA * u
                val chapa = Rect(r.left + r.width * 0.18f, yb - 7 * u, r.right - r.width * 0.18f, yb + 5 * u)
                drawRect(c(HIERRO), chapa.topLeft, chapa.size)
                drawRect(c(HIERRO_LUZ), chapa.topLeft, Size(chapa.width, 0.8f * u))
                drawCircle(Color.Black, 1.2f * u, Offset(chapa.center.x, chapa.center.y - 1 * u))
                drawLine(Color.Black, Offset(chapa.center.x, chapa.center.y - 0.5f * u), Offset(chapa.center.x, chapa.center.y + 2.4f * u), 1f * u)
                listOf(chapa.top + 1.8f * u, chapa.bottom - 1.8f * u).forEach { yy -> drawCircle(c(HIERRO_LUZ), 0.8f * u, Offset(chapa.left + 1.6f * u, yy)) }
            }
        }
        // Las tablas son atriles: el tablero en pendiente (más ancho por delante) y su canto con moldura.
        d.tablas.forEachIndexed { k, r ->
            val canto = r.top + r.height * 0.5f
            val atril = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top)
                lineTo(r.right + 2.5f * u, canto); lineTo(r.left - 2.5f * u, canto); close()
            }
            drawPath(atril, c(ROBLE_LUZ))
            clipPath(atril) { vetas(Rect(r.left - 3 * u, r.top, r.right + 3 * u, canto), c(VETA).copy(alpha = 0.4f), 20 + k, densidad = 0.7f, grosor = 0.6f) }
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.top), Offset(r.right, r.top), 1f * u)
            drawRect(c(ROBLE), Offset(r.left - 2.5f * u, canto), Size(r.width + 5 * u, r.bottom - canto))
            drawRect(c(ROBLE_LUZ).copy(alpha = 0.7f), Offset(r.left - 2.5f * u, canto), Size(r.width + 5 * u, 1.2f * u))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left - 2.5f * u, r.bottom - 2 * u), Size(r.width + 5 * u, 2 * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: un pergamino clavado al atril, pautado en rojo.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1.2f * u, r.top + 1.4f * u), r.size)
            drawRect(c(PERGAMINO), r.topLeft, r.size)
            val pauta = r.deflate(1.8f * u)
            drawRect(c(BERMELLON).copy(alpha = 0.7f), pauta.topLeft, pauta.size, style = Stroke(0.6f * u))
            listOf(r.left + 3.5f * u, r.right - 3.5f * u).forEach { cx -> drawCircle(c(HIERRO), 1.4f * u, Offset(cx, r.center.y)) }
            textoEn(medidor, p.texto, Rect(r.left + 7 * u, r.top, r.right - 7 * u, r.bottom), letra, c(TINTA), 11f, peso = FontWeight.Bold)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de roble con su banda de hierro claveteada, sobre baldosas de barro en damero.
        val b = d.base
        val piso = b.top + b.height * 0.6f
        drawRect(c(BALDOSA), Offset(0f, piso), Size(w, h - piso))
        val losa = 14 * u
        var fx = 0f; var col = 0
        while (fx < w) {
            var fy = piso; var fila = 0
            while (fy < h) {
                if ((col + fila) % 2 == 0) drawRect(Color.Black.copy(alpha = 0.2f), Offset(fx, fy), Size(losa, losa * 0.5f))
                fy += losa * 0.5f; fila++
            }
            fx += losa; col++
        }
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(ROBLE), zocalo.topLeft, zocalo.size)
        vetas(zocalo, c(VETA).copy(alpha = 0.5f), 41, densidad = 0.8f, grosor = 0.8f)
        drawRect(c(ROBLE_LUZ), zocalo.topLeft, Size(zocalo.width, 2.4f * u))
        val yb = zocalo.top + zocalo.height * 0.55f
        drawRect(c(HIERRO), Offset(zocalo.left, yb - 2.5f * u), Size(zocalo.width, 5 * u))
        var nx = zocalo.left + 10 * u
        while (nx < zocalo.right - 4 * u) { drawCircle(c(HIERRO_LUZ), 1.2f * u, Offset(nx, yb)); nx += 18 * u }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** El muro de sillares, el arco románico con su vela y la viga pintada. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro de sillares, cada hilada corrida media pieza.
        drawRect(c(PIEDRA), r.topLeft, r.size)
        val fila = r.height / 6f
        val pieza = 42 * u
        var y = r.top; var k = 0
        while (y < r.bottom) {
            var x = r.left - (if (k % 2 == 0) 0f else pieza / 2)
            var j = 0
            while (x < r.right) {
                val x0 = maxOf(x, r.left); val x1 = minOf(x + pieza, r.right)
                if (azar(k * 17 + j, 3) > 0.6f && x1 > x0) drawRect(c(PIEDRA_OSC).copy(alpha = 0.5f), Offset(x0, y), Size(x1 - x0, fila))
                drawLine(c(JUNTA), Offset(x, y), Offset(x, y + fila), 1f * u)
                x += pieza; j++
            }
            drawLine(c(JUNTA), Offset(r.left, y), Offset(r.right, y), 1f * u)
            y += fila; k++
        }
        val cornisa = Rect(r.left, r.bottom - r.height * 0.2f, r.right, r.bottom)
        // El arco románico: la arquivolta de dovelas, las columnillas y el vano en sombra.
        val altoVano = cornisa.top - (r.top + r.height * 0.1f)
        val anchoVano = minOf(r.width * 0.3f, altoVano * 1.15f)
        val vano = Rect(r.center.x - anchoVano / 2, cornisa.top - altoVano, r.center.x + anchoVano / 2, cornisa.top)
        val rad = anchoVano / 2
        val centro = Offset(vano.center.x, vano.top + rad)
        val grueso = 9 * u
        drawPath(arcoMedioPunto(Rect(vano.left - grueso, vano.top - grueso, vano.right + grueso, vano.bottom)), c(PIEDRA_OSC))
        for (j in 0..12) {
            val a = Math.toRadians(180.0 + j * 15.0)
            val cs = kotlin.math.cos(a).toFloat(); val sn = kotlin.math.sin(a).toFloat()
            drawLine(c(JUNTA), Offset(centro.x + cs * rad, centro.y + sn * rad), Offset(centro.x + cs * (rad + grueso), centro.y + sn * (rad + grueso)), 1f * u)
        }
        drawArc(c(JUNTA), 180f, 180f, false, Offset(centro.x - rad - grueso, centro.y - rad - grueso), Size((rad + grueso) * 2, (rad + grueso) * 2), style = Stroke(1f * u))
        val hueco = arcoMedioPunto(vano)
        drawPath(hueco, c(HUECO))
        clipPath(hueco) {
            if (noche) {
                // La luz de la vela llena el nicho.
                mancha(Offset(centro.x, vano.bottom - 22 * u), vano.height * 0.8f, LLAMA.copy(alpha = 0.42f))
            } else {
                // La saetera por donde entra el día.
                drawRect(DIA.copy(alpha = if (oscuro) 0.05f else 0.08f), vano.topLeft, vano.size)
                val saetera = Rect(centro.x - 2.6f * u, vano.top + vano.height * 0.22f, centro.x + 2.6f * u, vano.top + vano.height * 0.6f)
                drawPath(arcoMedioPunto(saetera), c(DIA))
            }
        }
        listOf(vano.left - grueso / 2, vano.right + grueso / 2).forEach { cx ->
            val fuste = cornisa.top - centro.y - 2 * u
            drawRect(c(PIEDRA), Offset(cx - grueso * 0.32f, centro.y + 2 * u), Size(grueso * 0.64f, fuste))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(cx + grueso * 0.12f, centro.y + 2 * u), Size(grueso * 0.2f, fuste))
            drawRect(c(PIEDRA), Offset(cx - grueso * 0.6f, centro.y - 1.5f * u), Size(grueso * 1.2f, 3.5f * u))
            drawRect(c(JUNTA), Offset(cx - grueso * 0.6f, centro.y - 1.5f * u), Size(grueso * 1.2f, 3.5f * u), style = Stroke(0.6f * u))
        }
        // La viga de roble: banda pintada en zigzag y canecillos debajo.
        drawRect(c(ROBLE), cornisa.topLeft, cornisa.size)
        vetas(cornisa, c(VETA).copy(alpha = 0.5f), 61, densidad = 0.7f, grosor = 0.8f)
        drawRect(c(ROBLE_LUZ), cornisa.topLeft, Size(cornisa.width, 2.2f * u))
        val banda = Rect(cornisa.left, cornisa.top + 3 * u, cornisa.right, cornisa.top + cornisa.height * 0.5f)
        val paso = banda.height * 1.3f
        var zx = banda.left; var zk = 0
        while (zx < banda.right) {
            val tri = Path().apply { moveTo(zx, banda.bottom); lineTo(zx + paso / 2, banda.top); lineTo(zx + paso, banda.bottom); close() }
            drawPath(tri, c(if (zk % 2 == 0) BERMELLON else LAPIZ).copy(alpha = 0.85f))
            zx += paso; zk++
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cornisa.left, cornisa.top + cornisa.height * 0.52f), Size(cornisa.width, 1.2f * u))
        val arranque = cornisa.top + cornisa.height * 0.56f
        var mx = cornisa.left + 10 * u
        while (mx < cornisa.right - 8 * u) {
            drawRect(c(ROBLE_OSC), Offset(mx, arranque), Size(7 * u, cornisa.bottom - arranque))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(mx + 5 * u, arranque), Size(2 * u, cornisa.bottom - arranque))
            mx += 22 * u
        }
        // La vela en su palmatoria de hierro, en el vano; de noche, encendida.
        val pie = cornisa.top
        drawRect(c(HIERRO), Offset(centro.x - 6 * u, pie - 2 * u), Size(12 * u, 2 * u))
        drawLine(c(HIERRO), Offset(centro.x, pie - 2 * u), Offset(centro.x, pie - 8 * u), 1.6f * u)
        drawRect(c(HIERRO), Offset(centro.x - 4.5f * u, pie - 9.6f * u), Size(9 * u, 1.6f * u))
        val vela = Rect(centro.x - 2.6f * u, pie - 25 * u, centro.x + 2.6f * u, pie - 9.6f * u)
        drawRect(if (noche) CERA else c(CERA), vela.topLeft, vela.size)
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(vela.right - 1.6f * u, vela.top), Size(1.6f * u, vela.height))
        drawLine(c(TINTA), Offset(vela.center.x, vela.top), Offset(vela.center.x, vela.top - 2.4f * u), 0.8f * u)
        if (noche) {
            val ly = vela.top - 2 * u
            val llama = Path().apply {
                moveTo(vela.center.x, ly - 9 * u)
                quadraticTo(vela.center.x + 3.4f * u, ly - 2.5f * u, vela.center.x, ly)
                quadraticTo(vela.center.x - 3.4f * u, ly - 2.5f * u, vela.center.x, ly - 9 * u); close()
            }
            drawPath(llama, LLAMA)
            drawOval(Color.White.copy(alpha = 0.8f), Offset(vela.center.x - 1 * u, ly - 4.5f * u), Size(2 * u, 3.6f * u))
            drawRect(CERA, Offset(vela.left - 0.6f * u, vela.top + 2 * u), Size(1.2f * u, 5 * u))
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val tinte = CUEROS[(v.semilla and 0x7fffffff) % CUEROS.size]
        val cuero = c(if (v.tono != null) mezcla(tinte, v.tono, 0.22f) else tinte)
        // El cuero sobre tablas: un filo de luz, uno de sombra, las cofias oscuras y alguna rozadura.
        drawRoundRect(cuero, Offset.Zero, size, CornerRadius(2f * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.08f, 0f), Size(w * 0.14f, h))
        drawRect(Color.Black.copy(alpha = 0.26f), Offset(w * 0.84f, 0f), Size(w * 0.16f, h))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset.Zero, Size(w, h * 0.03f))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, h * 0.97f), Size(w, h * 0.03f))
        for (j in 0 until 3) {
            val o = Offset(w * azar(v.semilla, 20 + j), h * azar(v.semilla, 30 + j))
            drawOval(Color.Black.copy(alpha = 0.08f), Offset(o.x - 2.5f * u, o.y - 1.5f * u), Size(5 * u, 3 * u))
        }
        // Los nervios: las cuerdas de la costura, en relieve bajo el cuero.
        listOf(0.1f, 0.26f, 0.56f, 0.8f).forEach { f ->
            val y = h * f
            drawRect(Color.White.copy(alpha = 0.18f), Offset(0f, y - 1.6f * u), Size(w, 1.4f * u))
            drawRect(Color.Black.copy(alpha = 0.32f), Offset(0f, y - 0.2f * u), Size(w, 1.4f * u))
        }
        // Arriba, el bollón de latón; de noche, una luna de oro.
        val cb = Offset(w * 0.5f, h * 0.064f)
        val rb = minOf(w * 0.16f, h * 0.026f)
        if (v.noche) {
            drawCircle(c(ORO), rb * 1.2f, cb)
            drawCircle(cuero, rb, Offset(cb.x + rb * 0.5f, cb.y - rb * 0.3f))
        } else {
            drawCircle(c(LATON), rb, cb)
            drawCircle(Color.White.copy(alpha = 0.35f), rb * 0.4f, Offset(cb.x - rb * 0.3f, cb.y - rb * 0.3f))
        }
        // Los filetes dorados del primer entrenervio: uno más por cada cuarto de página escrito.
        val filetes = 1 + (v.lleno * 3).toInt()
        for (j in 0 until filetes) {
            val y = h * (0.135f + 0.03f * j)
            drawLine(c(ORO).copy(alpha = 0.75f), Offset(w * 0.2f, y), Offset(w * 0.8f, y), 0.6f * u)
        }
        // El broche: la correa que cruza, la chapa de latón y su etiqueta de pergamino con el día.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, h * 0.37f), Size(w, h * 0.06f))
        val chapa = Rect(w * 0.1f, h * 0.3f, w * 0.9f, h * 0.5f)
        drawRect(c(LATON), chapa.topLeft, chapa.size)
        drawRect(Color.Black.copy(alpha = 0.3f), chapa.topLeft, chapa.size, style = Stroke(0.8f * u))
        val etiqueta = chapa.deflate(minOf(chapa.width, chapa.height) * 0.14f)
        drawRect(c(PERGAMINO), etiqueta.topLeft, etiqueta.size)
        textoEn(medidor, v.dia, etiqueta, letra, c(TINTA), 14f, peso = FontWeight.Bold)
        // El mes, dorado a lo largo del segundo entrenervio.
        textoEn(medidor, v.mes, Rect(w * 0.18f, h * 0.585f, w * 0.82f, h * 0.775f), letra, mezcla(c(ORO), ORO, 0.4f), 9f, vertical = true, minimo = 5f)
        // La cadena: de la grapa del canto hasta la barra, que pasa por delante del libro.
        val yb = h - BARRA * u
        val argolla = Offset(w * 0.5f, h * 0.85f)
        drawRect(c(HIERRO), Offset(argolla.x - 2.6f * u, argolla.y - 3.4f * u), Size(5.2f * u, 2.4f * u))
        drawCircle(c(HIERRO_LUZ), 2.2f * u, argolla, style = Stroke(1.1f * u))
        var ly = argolla.y + 1.8f * u
        var j = 0
        while (ly < yb - 1.2f * u) {
            if (j % 2 == 0) drawOval(c(HIERRO_LUZ), Offset(argolla.x - 1.3f * u, ly), Size(2.6f * u, 3.6f * u), style = Stroke(0.9f * u))
            else drawLine(c(HIERRO_LUZ), Offset(argolla.x, ly), Offset(argolla.x, ly + 3.6f * u), 1.1f * u)
            ly += 2.8f * u; j++
        }
        barra(0f, w, yb, oscuro, v.noche)
    }
}

/**
 * Templario: el armero de la encomienda. Postes de roble basto con flejes de hierro y clavijas, tablas con escuadras de
 * hierro, todo abierto por detrás contra el muro de sillares. Arriba, la percha de las armas: tres escudos colgados de sus
 * tiracoles (el blanco de la cruz roja, el blanco y negro, el rojo de la cruz blanca), el estandarte blanco y negro con sus
 * lenguas en medio, dos lanzas de pie en las puntas y una antorcha en su argolla, que de noche se enciende. Cada mes va en
 * una cartela de cuero pespunteada con la cruz. Cada entrada es el libro de la regla en piel blanca, con sus dos correas
 * (una con hebilla), la cruz patada roja, la cartela con el día y el mes (sin «juliano», que no cabe en el lomo), y un sello
 * de lacre en la correa de abajo; de noche, una luna en su lugar. Distinto del Ideario, que es la cámara del tesoro con su
 * gran arco apuntado y las arcas.
 */
object EstanteTemplario : DisenoBiblioteca {
    private val SILLAR = Color(0xFFC4B79C)
    private val SILLAR_JUNTA = Color(0xFF8C826F)
    private val ROBLE = Color(0xFF5C4129)
    private val ROBLE_LUZ = Color(0xFF7D5D3C)
    private val ROBLE_OSC = Color(0xFF3A2818)
    private val VETA = Color(0xFF2A1B0F)
    private val HIERRO = Color(0xFF2A2A2D)
    private val HIERRO_LUZ = Color(0xFF6C6D72)
    private val ROJO = Color(0xFFB0241E)
    private val BLANCO = Color(0xFFEFEADF)
    private val NEGRO = Color(0xFF17161A)
    private val CUERO = Color(0xFF5A3A22)
    private val CREMA = Color(0xFFEDE3CB)
    private val TINTA = Color(0xFF231A12)
    private val LATON = Color(0xFFB8954E)
    private val LACRE = Color(0xFF8E1C18)
    private val ESTOPA = Color(0xFF2A221C)
    private val LLAMA = Color(0xFFF6B44A)
    private val LOSA = Color(0xFF8E8676)
    private val HOJA = Color(0xFF8A7A5A)
    /** Las pieles blancas (curtidas al alumbre) de los libros de la regla. */
    private val BLANCOS = listOf(Color(0xFFE8DFC9), Color(0xFFDDD2B9), Color(0xFFECE5D3), Color(0xFFD7CBB0), Color(0xFFE3D9C0))
    private val NOCHE = Color(0xFF161A26)
    private val LUNA = Color(0xFFEDE6C8)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, a la luz de la antorcha. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0F0D0B), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    override val corona = 0.32f
    override val base = 0.1f
    override val lado = 0.06f
    override val altoRepisa: Dp = 116.dp
    override val tabla: Dp = 17.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.cruzada

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.15f * azar(semilla, 1)

    /** Un muro de sillares en [r]: hiladas de alto [fila] y piezas de largo [largo], alguna más oscura. */
    private fun DrawScope.sillares(r: Rect, junta: Color, fila: Float, largo: Float) {
        val u = density
        var y = r.top; var k = 0
        while (y < r.bottom) {
            val y2 = minOf(y + fila, r.bottom)
            var x = r.left - (if (k % 2 == 0) 0f else largo / 2)
            var j = 0
            while (x < r.right) {
                val a = azar(k * 31 + j, 5)
                val x0 = maxOf(x, r.left); val x1 = minOf(x + largo, r.right)
                if (a > 0.55f && x1 > x0) drawRect(Color.Black.copy(alpha = 0.05f + 0.16f * (a - 0.55f)), Offset(x0, y), Size(x1 - x0, y2 - y))
                drawLine(junta, Offset(x, y), Offset(x, y2), 1.2f * u)
                x += largo; j++
            }
            drawLine(junta, Offset(r.left, y), Offset(r.right, y), 1.2f * u)
            y += fila; k++
        }
    }

    /** Un escudo de caballero (de punta redonda) en [r]. */
    private fun escudo(r: Rect): Path = Path().apply {
        moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + r.height * 0.45f)
        quadraticTo(r.right, r.top + r.height * 0.85f, r.center.x, r.bottom)
        quadraticTo(r.left, r.top + r.height * 0.85f, r.left, r.top + r.height * 0.45f); close()
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro de sillares de la encomienda, detrás del armero, que no tiene fondo.
        val muro = Rect(0f, 0f, w, d.base.top + d.base.height * 0.55f)
        drawRect(c(SILLAR), muro.topLeft, muro.size)
        sillares(muro, c(SILLAR_JUNTA), 20 * u, 46 * u)
        // Dentro del armero el muro queda en sombra, más honda bajo cada tabla y junto a los postes.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(Color.Black.copy(alpha = if (noche) 0.34f else 0.24f), cuerpo.topLeft, cuerpo.size)
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft, Size(r.width, 10 * u))
            drawRect(Color.Black.copy(alpha = 0.16f), r.topLeft, Size(6 * u, r.height))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(r.right - 6 * u, r.top), Size(6 * u, r.height))
        }
        // Los postes: roble basto con las clavijas de las lanzas y un fleje de hierro en cada tabla.
        listOf(d.izquierda, d.derecha).forEachIndexed { k, r ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.6f), 5 + k, densidad = 1.3f, vertical = true, grosor = 0.9f, ondulacion = 1.6f)
            bisel(r, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 1.4f * u)
            var yy = r.top + 18 * u
            while (yy < r.bottom - 10 * u) {
                drawCircle(c(ROBLE_OSC), 2.6f * u, Offset(r.center.x, yy))
                drawCircle(c(ROBLE_LUZ), 1.6f * u, Offset(r.center.x - 0.4f * u, yy - 0.4f * u))
                yy += 26 * u
            }
            d.tablas.forEach { tb ->
                drawRect(c(HIERRO), Offset(r.left, tb.top - 2 * u), Size(r.width, tb.height + 4 * u))
                drawRect(c(HIERRO_LUZ), Offset(r.left, tb.top - 2 * u), Size(r.width, 0.8f * u))
                drawCircle(c(HIERRO_LUZ), 1.2f * u, Offset(r.center.x, tb.center.y))
            }
        }
        // Las tablas, con escuadras de hierro claveteadas en las puntas.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.55f), 30 + k, densidad = 0.7f, grosor = 0.8f, ondulacion = 1.4f)
            drawRect(c(ROBLE_LUZ), r.topLeft, Size(r.width, 2 * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom - 2 * u), Size(r.width, 2 * u))
            listOf(r.left, r.right - 14 * u).forEach { x0 ->
                drawRect(c(HIERRO), Offset(x0, r.top + r.height * 0.3f), Size(14 * u, r.height * 0.4f))
                drawCircle(c(HIERRO_LUZ), 1.1f * u, Offset(x0 + 4 * u, r.center.y))
                drawCircle(c(HIERRO_LUZ), 1.1f * u, Offset(x0 + 10 * u, r.center.y))
            }
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una cartela de cuero pespunteada, con la cruz.
        d.placas.forEach { p ->
            val r = p.r
            drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 1.2f * u, r.top + 1.4f * u), r.size, CornerRadius(2 * u))
            drawRoundRect(c(CUERO), r.topLeft, r.size, CornerRadius(2 * u))
            val pespunte = r.deflate(1.8f * u)
            drawRoundRect(c(CREMA).copy(alpha = 0.6f), pespunte.topLeft, pespunte.size, CornerRadius(1.4f * u), style = Stroke(0.6f * u))
            drawPath(cruzPatada(Offset(r.left + r.height * 0.7f, r.center.y), r.height * 0.3f), c(ROJO))
            textoEn(medidor, p.texto, Rect(r.left + r.height * 1.2f, r.top, r.right - 4 * u, r.bottom), letra, mezcla(c(CREMA), CREMA, 0.55f), 10.5f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de roble con su fleje sobre las losas del suelo.
        val b = d.base
        val piso = b.top + b.height * 0.55f
        drawRect(c(LOSA), Offset(0f, piso), Size(w, h - piso))
        var lx = 0f; var lk = 0
        while (lx < w) {
            drawLine(Color.Black.copy(alpha = 0.28f), Offset(lx, piso), Offset(lx - 8 * u, h), 1f * u)
            lx += 30 * u + 12 * u * azar(lk, 9); lk++
        }
        drawLine(Color.Black.copy(alpha = 0.28f), Offset(0f, piso + (h - piso) * 0.5f), Offset(w, piso + (h - piso) * 0.5f), 1f * u)
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(ROBLE_OSC), zocalo.topLeft, zocalo.size)
        vetas(zocalo, c(VETA).copy(alpha = 0.6f), 43, densidad = 0.8f, grosor = 0.8f)
        drawRect(c(ROBLE_LUZ), zocalo.topLeft, Size(zocalo.width, 2.2f * u))
        val yf = zocalo.top + zocalo.height * 0.5f
        drawRect(c(HIERRO), Offset(zocalo.left, yf - 2.5f * u), Size(zocalo.width, 5 * u))
        var nx = zocalo.left + 9 * u
        while (nx < zocalo.right - 4 * u) { drawCircle(c(HIERRO_LUZ), 1.2f * u, Offset(nx, yf)); nx += 16 * u }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** La percha de las armas: lanzas, escudos, el estandarte y la antorcha. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val cornisa = Rect(r.left, r.bottom - r.height * 0.16f, r.right, r.bottom)
        val percha = r.top + r.height * 0.14f
        // Las lanzas de pie en las puntas del armero.
        listOf(r.left + r.width * 0.035f, r.right - r.width * 0.035f).forEach { x ->
            val y0 = r.top + r.height * 0.1f
            drawLine(c(ROBLE_LUZ), Offset(x, cornisa.top), Offset(x, y0), 2.2f * u)
            val punta = Path().apply { moveTo(x, r.top + 2 * u); lineTo(x + 3 * u, y0 - 3 * u); lineTo(x, y0 + 1 * u); lineTo(x - 3 * u, y0 - 3 * u); close() }
            drawPath(punta, c(HIERRO_LUZ))
            drawLine(c(HIERRO), Offset(x - 2 * u, y0 + 1 * u), Offset(x + 2 * u, y0 + 1 * u), 1.2f * u)
        }
        // La percha de roble, sujeta al muro con grapas.
        val px0 = r.left + r.width * 0.08f; val px1 = r.right - r.width * 0.08f
        drawRect(c(ROBLE), Offset(px0, percha - 2.5f * u), Size(px1 - px0, 5 * u))
        drawRect(c(ROBLE_LUZ), Offset(px0, percha - 2.5f * u), Size(px1 - px0, 1.2f * u))
        listOf(px0 + 4 * u, px1 - 4 * u).forEach { gx -> drawRect(c(HIERRO), Offset(gx - 2 * u, percha - 4 * u), Size(4 * u, 8 * u)) }
        // Los escudos, colgados de sus tiracoles.
        val sw = minOf(r.width * 0.12f, (cornisa.top - percha - 12 * u) / 1.25f)
        val sh = sw * 1.25f
        listOf(0.17f, 0.32f, 0.83f).forEachIndexed { k, fx ->
            val cx = r.left + r.width * fx
            val er = Rect(cx - sw / 2, percha + 8 * u, cx + sw / 2, percha + 8 * u + sh)
            drawLine(c(CUERO), Offset(cx, percha), Offset(er.left + sw * 0.15f, er.top + 1 * u), 1.3f * u)
            drawLine(c(CUERO), Offset(cx, percha), Offset(er.right - sw * 0.15f, er.top + 1 * u), 1.3f * u)
            drawPath(escudo(er.translate(2 * u, 2.5f * u)), Color.Black.copy(alpha = 0.22f))
            val e = escudo(er)
            if (k == 0) {
                drawPath(e, c(BLANCO))
                drawPath(cruzPatada(Offset(cx, er.top + sh * 0.42f), sw * 0.3f), c(ROJO))
            } else if (k == 1) {
                drawPath(e, c(BLANCO))
                clipPath(e) { drawRect(c(NEGRO), er.topLeft, Size(sw, sh * 0.45f)) }
            } else {
                drawPath(e, c(ROJO))
                clipPath(e) {
                    drawRect(c(BLANCO), Offset(cx - sw * 0.08f, er.top), Size(sw * 0.16f, sh))
                    drawRect(c(BLANCO), Offset(er.left, er.top + sh * 0.3f), Size(sw, sw * 0.16f))
                }
            }
            drawPath(e, c(HIERRO), style = Stroke(1.6f * u))
            drawLine(Color.White.copy(alpha = 0.2f), Offset(er.left + 2.4f * u, er.top + 2.4f * u), Offset(er.left + 2.4f * u, er.top + sh * 0.45f), 1.2f * u)
            drawCircle(c(HIERRO_LUZ), 1.4f * u, Offset(cx, percha))
        }
        // El estandarte blanco y negro, con sus lenguas.
        val bw = r.width * 0.14f
        val bx0 = r.center.x - bw / 2; val bx1 = r.center.x + bw / 2
        val top = percha + 5 * u
        val bajo = cornisa.top - 6 * u
        val lengua = (bajo - top) * 0.16f
        val tela = Path().apply {
            moveTo(bx0, top); lineTo(bx1, top); lineTo(bx1, bajo)
            lineTo(bx0 + bw * 5 / 6, bajo - lengua); lineTo(bx0 + bw * 4 / 6, bajo); lineTo(bx0 + bw * 3 / 6, bajo - lengua)
            lineTo(bx0 + bw * 2 / 6, bajo); lineTo(bx0 + bw / 6, bajo - lengua); lineTo(bx0, bajo); close()
        }
        drawPath(tela, c(BLANCO))
        clipPath(tela) {
            drawRect(c(NEGRO), Offset(bx0, top), Size(bw, (bajo - top) * 0.45f))
            for (j in 1..3) drawRect(Color.Black.copy(alpha = 0.1f), Offset(bx0 + bw * j / 4f - bw * 0.03f, top), Size(bw * 0.05f, bajo - top))
        }
        drawPath(tela, c(NEGRO).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        drawLine(c(ROBLE_OSC), Offset(bx0 - 6 * u, top), Offset(bx1 + 6 * u, top), 3 * u)
        listOf(bx0 - 6 * u, bx1 + 6 * u).forEach { x -> drawCircle(c(LATON), 2.4f * u, Offset(x, top)) }
        drawLine(c(CUERO), Offset(bx0 - 2 * u, top), Offset(r.center.x, percha), 1f * u)
        drawLine(c(CUERO), Offset(bx1 + 2 * u, top), Offset(r.center.x, percha), 1f * u)
        // La antorcha en su argolla; de noche, encendida.
        val ta = Offset(r.left + r.width * 0.67f, r.top + r.height * 0.5f)
        drawRect(c(HIERRO), Offset(ta.x - 3 * u, ta.y - 2 * u), Size(6 * u, 14 * u))
        drawLine(c(HIERRO), Offset(ta.x, ta.y + 4 * u), Offset(ta.x + 6 * u, ta.y + 2 * u), 1.6f * u)
        drawOval(c(HIERRO_LUZ), Offset(ta.x + 3 * u, ta.y), Size(7 * u, 4 * u), style = Stroke(1.2f * u))
        val abajo = Offset(ta.x + 5 * u, ta.y + 16 * u)
        val arriba = Offset(ta.x + 8 * u, ta.y - 13 * u)
        drawLine(c(ROBLE_LUZ), abajo, arriba, 3.2f * u)
        if (noche) mancha(Offset(arriba.x, arriba.y - 8 * u), r.height * 0.55f, LLAMA.copy(alpha = 0.4f))
        drawOval(c(ESTOPA), Offset(arriba.x - 3.2f * u, arriba.y - 6 * u), Size(6.4f * u, 9 * u))
        for (j in 0 until 3) drawLine(Color.Black.copy(alpha = 0.4f), Offset(arriba.x - 3 * u, arriba.y - 4 * u + j * 2.4f * u), Offset(arriba.x + 3 * u, arriba.y - 3 * u + j * 2.4f * u), 0.6f * u)
        if (noche) {
            val base0 = arriba.y - 5 * u
            val llama = Path().apply {
                moveTo(arriba.x, base0 - 15 * u)
                quadraticTo(arriba.x + 6 * u, base0 - 5 * u, arriba.x + 3 * u, base0)
                lineTo(arriba.x - 3 * u, base0)
                quadraticTo(arriba.x - 6 * u, base0 - 6 * u, arriba.x - 1 * u, base0 - 11 * u)
                quadraticTo(arriba.x, base0 - 13 * u, arriba.x, base0 - 15 * u); close()
            }
            drawPath(llama, LLAMA)
            drawOval(Color(0xFFFFF0C0), Offset(arriba.x - 1.6f * u, base0 - 7 * u), Size(3.2f * u, 6 * u))
        }
        // La cornisa del armero: viga de roble con su fleje y clavos.
        drawRect(c(ROBLE), cornisa.topLeft, cornisa.size)
        vetas(cornisa, c(VETA).copy(alpha = 0.55f), 63, densidad = 0.8f, grosor = 0.8f, ondulacion = 1.4f)
        drawRect(c(ROBLE_LUZ), cornisa.topLeft, Size(cornisa.width, 2 * u))
        val yf = cornisa.top + cornisa.height * 0.55f
        drawRect(c(HIERRO), Offset(cornisa.left, yf - 2 * u), Size(cornisa.width, 4 * u))
        var nx = cornisa.left + 12 * u
        while (nx < cornisa.right - 6 * u) { drawCircle(c(HIERRO_LUZ), 1.1f * u, Offset(nx, yf)); nx += 20 * u }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(cornisa.left, cornisa.bottom - 2 * u), Size(cornisa.width, 2 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val tinte = BLANCOS[(v.semilla and 0x7fffffff) % BLANCOS.size]
        val piel = c(if (v.tono != null) mezcla(tinte, v.tono, 0.22f) else tinte)
        // La piel blanca, con un filo de luz, uno de sombra y el roce del uso en los cantos.
        drawRoundRect(piel, Offset.Zero, size, CornerRadius(2f * u))
        drawRect(Color.White.copy(alpha = 0.12f), Offset(w * 0.08f, 0f), Size(w * 0.12f, h))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(w * 0.86f, 0f), Size(w * 0.14f, h))
        drawRect(c(HOJA).copy(alpha = 0.22f), Offset.Zero, Size(w, h * 0.025f))
        drawRect(c(HOJA).copy(alpha = 0.22f), Offset(0f, h * 0.975f), Size(w, h * 0.025f))
        // Las hojas que asoman por arriba: más cuanto más escribiste.
        val hojas = 1 + (v.lleno * 3).toInt()
        for (j in 0 until hojas) {
            val y = (0.9f + j * 1.3f) * u
            drawLine(c(HOJA).copy(alpha = 0.55f), Offset(w * 0.1f, y), Offset(w * 0.9f, y), 0.5f * u)
        }
        // Las dos correas de cuero; la de arriba con su hebilla.
        listOf(0.13f, 0.87f).forEachIndexed { j, f ->
            val y = h * f
            drawRect(c(CUERO), Offset(0f, y - h * 0.022f), Size(w, h * 0.044f))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, y + h * 0.016f), Size(w, h * 0.006f))
            if (j == 0) {
                val hebilla = Rect(w * 0.58f, y - h * 0.034f, w * 0.8f, y + h * 0.034f)
                drawRect(c(LATON), hebilla.topLeft, hebilla.size, style = Stroke(1.1f * u))
                drawLine(c(LATON), Offset(hebilla.center.x, hebilla.top), Offset(hebilla.center.x, hebilla.bottom), 0.9f * u)
            }
        }
        // La cruz roja de la Orden.
        drawPath(cruzPatada(Offset(w * 0.5f, h * 0.3f), minOf(w * 0.3f, h * 0.1f)), c(ROJO))
        // La cartela con el día.
        val cartela = Rect(w * 0.12f, h * 0.43f, w * 0.88f, h * 0.57f)
        drawRect(c(CREMA), cartela.topLeft, cartela.size)
        drawRect(c(TINTA), cartela.topLeft, cartela.size, style = Stroke(0.8f * u))
        val filete = cartela.deflate(1.4f * u)
        drawRect(c(ROJO).copy(alpha = 0.6f), filete.topLeft, filete.size, style = Stroke(0.5f * u))
        textoEn(medidor, v.dia, cartela.deflate(1.6f * u), letra, c(TINTA), 14f, peso = FontWeight.SemiBold)
        // El mes, a lo largo del lomo.
        textoEn(medidor, v.mes.substringBefore(' '), Rect(w * 0.2f, h * 0.6f, w * 0.8f, h * 0.83f), letra, c(TINTA).copy(alpha = 0.85f), 8.5f, vertical = true, minimo = 4.5f)
        // El sello de lacre en la correa de abajo; de noche, una luna.
        val cs = Offset(w * 0.5f, h * 0.87f)
        val rs = minOf(w * 0.16f, 4.5f * u)
        if (v.noche) {
            drawCircle(LUNA, rs, cs)
            drawCircle(c(CUERO), rs * 0.82f, Offset(cs.x + rs * 0.42f, cs.y - rs * 0.22f))
        } else {
            drawCircle(c(LACRE), rs, cs)
            drawCircle(Color.Black.copy(alpha = 0.25f), rs * 0.7f, cs, style = Stroke(0.6f * u))
            drawPath(cruzPatada(cs, rs * 0.5f), Color.Black.copy(alpha = 0.3f))
        }
    }
}

/**
 * Ruta de la Seda: el estante de laca roja de cinabrio, con postes y tablas de laca, filetes de oro, esquineras en forma de
 * nube en lo alto de cada repisa y un fondo de laca negra con volutas de oro. Arriba, bajo un alero de laca negra con sus
 * tejas redondas, el panel rojo con la puerta de luna: un círculo de celosía de laca sobre papel, entre dos nubes de oro
 * talladas; delante cuelga una lámpara de papel con sus costillas de bambú y su borla, que de noche se enciende. Abajo, el
 * zócalo con su faldón de nube y patas de casco, sobre ladrillo gris. Cada mes va en una placa de marfil enmarcada en laca.
 * Cada entrada es un rollo de pie envuelto en seda labrada (índigo, verde, oro, ciruela, verde mar, ladrillo), con la perilla
 * de jade del rodillo arriba, la tira de papel con el mes en columna, el cordón que lo ata (una vuelta más cuanto más
 * escribiste) y la tarjeta de marfil colgando con el día; abajo, el sello rojo del dueño, y de noche una luna en su lugar.
 * Distinto del Ideario, que es el muro de ladrillo del caravasar con su gran arco y las cajas de laca.
 */
object EstanteSeda : DisenoBiblioteca {
    private val LACA = Color(0xFFA8261C)
    private val LACA_LUZ = Color(0xFFC9402E)
    private val LACA_OSC = Color(0xFF6E1610)
    private val NEGRO_LACA = Color(0xFF1E1412)
    private val TEJA = Color(0xFF3A3632)
    private val ORO = Color(0xFFD2AE5A)
    private val PAPEL = Color(0xFFF1E6CC)
    private val MURO = Color(0xFFDCD0B6)
    private val LADRILLO = Color(0xFF7A7670)
    private val JADE = Color(0xFF6E9C80)
    private val MARFIL = Color(0xFFEFE6CF)
    private val TINTA = Color(0xFF1E1A18)
    private val BERMELLON = Color(0xFFB8322A)
    private val CORDON = Color(0xFFB0302A)
    private val BAMBU = Color(0xFF8A6A3A)
    private val LAMPARA = Color(0xFFF4C66A)
    private val LUNA = Color(0xFFEDE6C8)
    /** Las sedas de los rollos: índigo, verde, oro viejo, ciruela, verde mar y ladrillo. */
    private val SEDAS = listOf(Color(0xFF2F4F7A), Color(0xFF4F6E3C), Color(0xFFB08C34), Color(0xFF6A3050), Color(0xFF2E6A6A), Color(0xFF8A3A28))
    private val NOCHE = Color(0xFF161828)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, a la luz de la lámpara. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF110B09), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.44f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.1f
    override val lado = 0.05f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.almendra

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 50).coerceAtMost(14)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.82f + 0.14f * azar(semilla, 1)

    /** Una nube de oro tallada (ruyi): tres lóbulos y su tallo. */
    private fun DrawScope.nube(c0: Offset, s: Float, color: Color, grosor: Float) {
        drawCircle(color, s * 0.5f, Offset(c0.x, c0.y - s * 0.3f), style = Stroke(grosor))
        drawCircle(color, s * 0.42f, Offset(c0.x - s * 0.55f, c0.y + s * 0.15f), style = Stroke(grosor))
        drawCircle(color, s * 0.42f, Offset(c0.x + s * 0.55f, c0.y + s * 0.15f), style = Stroke(grosor))
        drawLine(color, Offset(c0.x, c0.y + s * 0.2f), Offset(c0.x, c0.y + s * 1.1f), grosor)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de laca negra.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(NEGRO_LACA), cuerpo.topLeft, cuerpo.size)
        d.repisas.forEachIndexed { k, r ->
            // Volutas de oro tenues en el fondo.
            for (j in 0 until 3) {
                val cx = r.left + r.width * (0.18f + 0.3f * j + 0.08f * azar(k, j))
                val cy = r.top + r.height * (0.3f + 0.3f * azar(k, j + 5))
                drawArc(c(ORO).copy(alpha = 0.2f), 180f, 180f, false, Offset(cx - 8 * u, cy - 5 * u), Size(16 * u, 10 * u), style = Stroke(0.8f * u))
                drawArc(c(ORO).copy(alpha = 0.2f), 180f, 180f, false, Offset(cx + 4 * u, cy - 3 * u), Size(10 * u, 6 * u), style = Stroke(0.8f * u))
            }
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.3f), r.topLeft, Size(r.width, 9 * u))
            // Las esquineras en forma de nube, arriba en las dos puntas.
            val s = 13 * u
            val izq = Path().apply { moveTo(r.left, r.top); lineTo(r.left + s, r.top); quadraticTo(r.left + s * 0.2f, r.top + s * 0.2f, r.left, r.top + s); close() }
            val der = Path().apply { moveTo(r.right, r.top); lineTo(r.right - s, r.top); quadraticTo(r.right - s * 0.2f, r.top + s * 0.2f, r.right, r.top + s); close() }
            listOf(izq, der).forEach { esq -> drawPath(esq, c(LACA)); drawPath(esq, c(ORO), style = Stroke(0.7f * u)) }
        }
        // Los postes de laca roja, con su filete de oro y los remates de laca negra.
        listOf(d.izquierda, d.derecha).forEach { r ->
            drawRect(c(LACA), r.topLeft, r.size)
            drawRect(c(LACA_LUZ).copy(alpha = 0.6f), Offset(r.left + r.width * 0.15f, r.top), Size(r.width * 0.15f, r.height))
            drawRect(c(LACA_OSC).copy(alpha = 0.6f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
            drawLine(c(ORO), Offset(r.center.x, r.top + 6 * u), Offset(r.center.x, r.bottom - 6 * u), 0.8f * u)
            drawRect(c(NEGRO_LACA), r.topLeft, Size(r.width, 5 * u))
            drawRect(c(NEGRO_LACA), Offset(r.left, r.bottom - 5 * u), Size(r.width, 5 * u))
        }
        // Las tablas: laca roja con un filete de oro y el canto de laca negra.
        d.tablas.forEach { r ->
            drawRect(c(LACA), r.topLeft, r.size)
            drawRect(c(LACA_LUZ).copy(alpha = 0.7f), r.topLeft, Size(r.width, 1.8f * u))
            drawLine(c(ORO), Offset(r.left, r.top + r.height * 0.32f), Offset(r.right, r.top + r.height * 0.32f), 0.7f * u)
            drawRect(c(NEGRO_LACA), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una placa de marfil enmarcada en laca negra.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
            val marco = r.inflate(1.4f * u)
            drawRect(c(NEGRO_LACA), marco.topLeft, marco.size)
            drawRect(c(MARFIL), r.topLeft, r.size)
            val filete = r.deflate(1.2f * u)
            drawRect(c(ORO), filete.topLeft, filete.size, style = Stroke(0.6f * u))
            textoEn(medidor, p.texto, r.deflate(2 * u), letra, c(TINTA), 11f)
        }
        corona(d.corona, oscuro, noche)
        // La base: ladrillo gris y el zócalo de laca con su faldón de nube y patas de casco.
        val b = d.base
        val piso = b.top + b.height * 0.62f
        drawRect(c(LADRILLO), Offset(0f, piso), Size(w, h - piso))
        var y = piso; var fila = 0
        while (y < h) {
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, y), Offset(w, y), 0.8f * u)
            var x = if (fila % 2 == 0) 0f else 11 * u
            while (x < w) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(x, y), Offset(x, minOf(y + 6 * u, h)), 0.8f * u); x += 22 * u }
            y += 6 * u; fila++
        }
        // La sombra bajo el mueble, que se ve por el faldón recortado.
        drawRect(c(mezcla(LADRILLO, NEGRO_LACA, 0.65f)), Offset(b.left, b.top), Size(b.width, piso - b.top))
        val ya = b.top + b.height * 0.36f
        val zocalo = Path().apply {
            moveTo(b.left, b.top); lineTo(b.right, b.top); lineTo(b.right, piso)
            lineTo(b.right - b.width * 0.06f, piso)
            cubicTo(b.right - b.width * 0.08f, ya, b.left + b.width * 0.6f, ya, b.center.x + b.width * 0.06f, ya)
            quadraticTo(b.center.x, ya + b.height * 0.2f, b.center.x - b.width * 0.06f, ya)
            cubicTo(b.left + b.width * 0.4f, ya, b.left + b.width * 0.08f, ya, b.left + b.width * 0.06f, piso)
            lineTo(b.left, piso); close()
        }
        drawPath(zocalo, c(LACA))
        drawPath(zocalo, c(ORO), style = Stroke(0.8f * u))
        drawRect(c(NEGRO_LACA), Offset(b.left, b.top), Size(b.width, 3 * u))
        listOf(b.left + b.width * 0.03f, b.right - b.width * 0.03f).forEach { px -> drawCircle(c(NEGRO_LACA), 2.6f * u, Offset(px, piso - 2.6f * u)) }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(b.left + b.width * 0.06f, piso), Size(b.width * 0.88f, 3 * u))
    }

    /** El alero de tejas, el panel con la puerta de luna y la lámpara de papel. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(MURO), r.topLeft, r.size)
        // El alero de laca negra, con las puntas alzadas y la fila de tejas redondas encima.
        val alero = r.top + r.height * 0.16f
        val viga = Path().apply {
            moveTo(r.left, alero - r.height * 0.11f)
            quadraticTo(r.left + r.width * 0.07f, alero, r.left + r.width * 0.2f, alero)
            lineTo(r.right - r.width * 0.2f, alero)
            quadraticTo(r.right - r.width * 0.07f, alero, r.right, alero - r.height * 0.11f)
            lineTo(r.right, alero + r.height * 0.06f); lineTo(r.left, alero + r.height * 0.06f); close()
        }
        var tx = r.left + r.width * 0.2f
        while (tx < r.right - r.width * 0.2f) {
            drawRect(c(TEJA), Offset(tx - 3 * u, alero - 6 * u), Size(6 * u, 5 * u))
            drawCircle(c(TEJA), 3 * u, Offset(tx, alero - 1 * u))
            drawCircle(c(MURO).copy(alpha = 0.35f), 1.8f * u, Offset(tx, alero - 1 * u), style = Stroke(0.6f * u))
            tx += 8 * u
        }
        drawPath(viga, c(NEGRO_LACA))
        drawLine(c(ORO), Offset(r.left + r.width * 0.2f, alero + r.height * 0.025f), Offset(r.right - r.width * 0.2f, alero + r.height * 0.025f), 0.9f * u)
        // El panel de laca roja.
        val panel = Rect(r.left + r.width * 0.02f, alero + r.height * 0.06f, r.right - r.width * 0.02f, r.bottom - r.height * 0.03f)
        drawRect(c(LACA), panel.topLeft, panel.size)
        drawRect(c(LACA_LUZ).copy(alpha = 0.5f), panel.topLeft, Size(panel.width, 1.6f * u))
        val filete = panel.deflate(4 * u)
        drawRect(c(ORO), filete.topLeft, filete.size, style = Stroke(0.8f * u))
        // La puerta de luna: celosía de laca sobre papel.
        val rv = panel.height * 0.42f
        val cv = panel.center
        val luna = Path().apply { addOval(Rect(cv, rv)) }
        drawPath(luna, if (noche) mezcla(PAPEL, NOCHE, 0.5f) else c(PAPEL))
        clipPath(luna) {
            val paso = rv / 3f
            var gx = cv.x - rv; var i = 0
            while (gx < cv.x + rv) {
                var gy = cv.y - rv; var j = 0
                while (gy < cv.y + rv) {
                    if ((i + j) % 2 == 0) drawRect(c(LACA_OSC), Offset(gx + paso * 0.3f, gy + paso * 0.3f), Size(paso * 0.4f, paso * 0.4f), style = Stroke(1.2f * u))
                    gy += paso; j++
                }
                drawLine(c(LACA_OSC), Offset(gx, cv.y - rv), Offset(gx, cv.y + rv), 1.8f * u)
                drawLine(c(LACA_OSC), Offset(cv.x - rv, cv.y - rv + paso * i), Offset(cv.x + rv, cv.y - rv + paso * i), 1.8f * u)
                gx += paso; i++
            }
        }
        drawCircle(c(LACA_OSC), rv, cv, style = Stroke(4 * u))
        drawCircle(c(ORO), rv + 3 * u, cv, style = Stroke(0.8f * u))
        // Las nubes de oro a los lados.
        val sn = panel.height * 0.18f
        nube(Offset(panel.left + panel.width * 0.14f, cv.y + sn * 0.6f), sn, c(ORO), 1f * u)
        nube(Offset(panel.right - panel.width * 0.14f, cv.y), sn, c(ORO), 1f * u)
        // La lámpara de papel, colgada del alero; de noche, encendida.
        val lx = r.left + r.width * 0.14f
        val lt = alero + r.height * 0.16f
        val lw = r.width * 0.09f; val lh = lw * 1.25f
        drawLine(c(NEGRO_LACA), Offset(lx, alero + r.height * 0.06f), Offset(lx, lt), 1f * u)
        val globo = Rect(lx - lw / 2, lt + 3 * u, lx + lw / 2, lt + 3 * u + lh)
        if (noche) mancha(globo.center, lw * 2.2f, LAMPARA.copy(alpha = 0.5f))
        drawOval(if (noche) mezcla(PAPEL, LAMPARA, 0.5f) else c(PAPEL), globo.topLeft, globo.size)
        if (!noche) drawOval(Color.Black.copy(alpha = 0.1f), Offset(globo.center.x, globo.top), Size(globo.width / 2, globo.height))
        for (k in 1..3) {
            val dx = globo.width * 0.5f * k / 4f
            drawOval(c(BAMBU).copy(alpha = 0.6f), Offset(globo.left + dx, globo.top), Size(globo.width - 2 * dx, globo.height), style = Stroke(0.6f * u))
        }
        listOf(0.3f, 0.5f, 0.7f).forEach { f ->
            val yy = globo.top + globo.height * f
            val medio = globo.width / 2 * kotlin.math.sqrt(1f - (2 * f - 1) * (2 * f - 1))
            drawLine(c(BAMBU).copy(alpha = 0.6f), Offset(lx - medio, yy), Offset(lx + medio, yy), 0.6f * u)
        }
        drawRect(c(LACA), Offset(lx - lw * 0.3f, lt), Size(lw * 0.6f, 3.4f * u))
        drawRect(c(LACA), Offset(lx - lw * 0.3f, globo.bottom - 1 * u), Size(lw * 0.6f, 3.4f * u))
        drawLine(c(ORO), Offset(lx - lw * 0.3f, lt + 1.7f * u), Offset(lx + lw * 0.3f, lt + 1.7f * u), 0.6f * u)
        val borla = globo.bottom + 2.4f * u
        drawLine(c(CORDON), Offset(lx, borla), Offset(lx, borla + 3 * u), 1f * u)
        for (k in -2..2) drawLine(c(CORDON), Offset(lx + k * 0.6f * u, borla + 3 * u), Offset(lx + k * 1.1f * u, borla + 11 * u), 0.8f * u)
        drawCircle(c(CORDON), 1.8f * u, Offset(lx, borla + 3.2f * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val tinte = SEDAS[(v.semilla and 0x7fffffff) % SEDAS.size]
        val seda = c(if (v.tono != null) mezcla(tinte, v.tono, 0.22f) else tinte)
        // La perilla de jade del rodillo, asomando arriba.
        drawRoundRect(c(JADE), Offset(w * 0.3f, 0f), Size(w * 0.4f, h * 0.06f), CornerRadius(1.5f * u))
        drawRect(Color.White.copy(alpha = 0.25f), Offset(w * 0.36f, h * 0.008f), Size(w * 0.06f, h * 0.04f))
        // El rollo envuelto: un cilindro de seda de pie, con su luz y su sombra.
        val rad = w * 0.18f
        val cuerpo = Rect(0f, h * 0.045f, w, h)
        drawRoundRect(seda, cuerpo.topLeft, cuerpo.size, CornerRadius(rad))
        val recto = Rect(0f, cuerpo.top + rad, w, cuerpo.bottom - rad)
        drawRect(Color.White.copy(alpha = 0.14f), Offset(w * 0.16f, recto.top), Size(w * 0.14f, recto.height))
        drawRect(Color.Black.copy(alpha = 0.24f), Offset(w * 0.8f, recto.top), Size(w * 0.2f, recto.height))
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(0f, cuerpo.top), Size(w, rad * 1.2f))
        // El labrado de la seda: rombos tenues.
        clipRect(0f, recto.top, w, recto.bottom) {
            var k = -h
            while (k < w + h) {
                drawLine(Color.White.copy(alpha = 0.07f), Offset(k, recto.bottom), Offset(k + recto.height, recto.top), 0.6f * u)
                drawLine(Color.White.copy(alpha = 0.07f), Offset(k, recto.top), Offset(k + recto.height, recto.bottom), 0.6f * u)
                k += 5 * u
            }
        }
        // El borde de la envoltura, que da la vuelta en diagonal.
        drawLine(c(ORO).copy(alpha = 0.6f), Offset(0f, h * 0.2f), Offset(w, h * 0.13f), 1.2f * u)
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, h * 0.2f + 1.4f * u), Offset(w, h * 0.13f + 1.4f * u), 0.8f * u)
        // La tira de papel con el mes, en columna.
        val tira = Rect(w * 0.26f, h * 0.17f, w * 0.74f, h * 0.47f)
        drawRect(c(PAPEL), tira.topLeft, tira.size)
        drawRect(c(TINTA).copy(alpha = 0.5f), tira.topLeft, tira.size, style = Stroke(0.5f * u))
        val signos = v.mes.filter { !it.isWhitespace() }.map { it.toString() }
        if (signos.size in 1..4) {
            val paso = tira.height / signos.size
            signos.forEachIndexed { k, s -> textoEn(medidor, s, Rect(tira.left, tira.top + paso * k, tira.right, tira.top + paso * (k + 1)), letra, c(TINTA), 9f, minimo = 4f) }
        } else {
            textoEn(medidor, v.mes, tira.deflate(0.5f * u), letra, c(TINTA), 8f, vertical = true, minimo = 4.5f)
        }
        // El cordón que lo ata: una vuelta más cuanto más escribiste.
        val vueltas = 1 + (v.lleno * 2).toInt()
        for (j in 0 until vueltas) {
            val y = h * 0.52f + j * 1.8f * u
            drawLine(c(CORDON), Offset(0f, y), Offset(w, y), 1.1f * u)
        }
        val nudo = Offset(w * 0.5f, h * 0.52f + vueltas * 1.8f * u)
        drawCircle(c(CORDON), 1.6f * u, nudo)
        // La tarjeta de marfil colgando, con el día.
        val tarjeta = Rect(w * 0.18f, nudo.y + 3.2f * u, w * 0.82f, nudo.y + 3.2f * u + h * 0.18f)
        drawLine(c(CORDON), nudo, Offset(nudo.x, tarjeta.top + 1.6f * u), 0.9f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(tarjeta.left + 0.8f * u, tarjeta.top + 0.8f * u), tarjeta.size, CornerRadius(1.4f * u))
        drawRoundRect(c(MARFIL), tarjeta.topLeft, tarjeta.size, CornerRadius(1.4f * u))
        drawCircle(c(TINTA).copy(alpha = 0.5f), 0.9f * u, Offset(tarjeta.center.x, tarjeta.top + 1.6f * u))
        textoEn(medidor, v.dia, Rect(tarjeta.left, tarjeta.top + 2.8f * u, tarjeta.right, tarjeta.bottom - 0.5f * u), letra, c(TINTA), 12f, peso = FontWeight.Bold)
        // Abajo, el sello rojo del dueño; de noche, la luna.
        val cs = Offset(w * 0.5f, h * 0.89f)
        val s = minOf(w * 0.34f, 7 * u)
        if (v.noche) {
            drawCircle(LUNA, s * 0.5f, cs)
            drawCircle(seda, s * 0.42f, Offset(cs.x + s * 0.22f, cs.y - s * 0.12f))
        } else {
            drawRect(c(BERMELLON), Offset(cs.x - s / 2, cs.y - s / 2), Size(s, s))
            drawRect(c(PAPEL).copy(alpha = 0.8f), Offset(cs.x - s * 0.32f, cs.y - s * 0.32f), Size(s * 0.64f, s * 0.64f), style = Stroke(0.6f * u))
        }
    }
}

/**
 * Gótico: la librería tallada del coro. Roble oscuro con lancetas ciegas talladas en el fondo de cada repisa, costados como
 * contrafuertes con sus pequeños gabletes, tablas con un friso de cuadrifolios y cada mes en una filacteria de vitela (la
 * cinta de borde en cola de golondrina). Arriba, sobre un muro de piedra, una crestería de arquillos, cuatro pináculos con
 * sus crochets y remates dorados y, en medio, el gablete con el rosetón: de día con los vidrios pálidos; de noche,
 * iluminado por la luna, con sus colores encendidos y unos haces de color que caen sobre el mueble. Cada entrada es un libro
 * de horas de terciopelo (carmesí, azul, verde, morado, negro) con bullones dorados en las esquinas, uno en medio arriba (de
 * noche, una luna de plata), dos broches de latón, la cartela de latón con el día y los registros de seda que asoman abajo,
 * más cuanto más escribiste. Distinto del Ideario, que es la cajonera de la sacristía con su crestería y sus cajones.
 */
object EstanteGotico : DisenoBiblioteca {
    private val ROBLE = Color(0xFF4F3826)
    private val ROBLE_LUZ = Color(0xFF6E5038)
    private val ROBLE_OSC = Color(0xFF2A1D13)
    private val VETA = Color(0xFF1B120B)
    private val ORO = Color(0xFFC9A24E)
    private val LATON = Color(0xFFB89450)
    private val VITELA = Color(0xFFEDE3C9)
    private val TINTA = Color(0xFF22160E)
    private val PIEDRA = Color(0xFF8E8A80)
    private val MURO = Color(0xFF6F6B63)
    private val PLATA = Color(0xFFDCDDE4)
    private val LOSA_CLARA = Color(0xFFB7AF9F)
    private val LOSA_OSCURA = Color(0xFF4A4640)
    /** Los vidrios del rosetón: azul, rojo, oro, verde y violeta. */
    private val VIDRIOS = listOf(Color(0xFF2A4C9A), Color(0xFFA0222A), Color(0xFFD8A73A), Color(0xFF2E7A4C), Color(0xFF5A3A8A))
    /** Los terciopelos de los libros de horas: carmesí, azul, verde, morado y negro. */
    private val TERCIOPELOS = listOf(Color(0xFF7A1424), Color(0xFF1E2C5E), Color(0xFF1F4A34), Color(0xFF4A2152), Color(0xFF2A2226))
    /** Los registros de seda que asoman abajo. */
    private val REGISTROS = listOf(Color(0xFFC9A24E), Color(0xFFA0222A), Color(0xFF2E7A4C))
    private val LUNA = Color(0xFFE6E8F0)
    private val NOCHE = Color(0xFF141A30)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la luna azul. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0C0A08), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.46f)
        return r
    }

    override val corona = 0.36f
    override val base = 0.11f
    override val lado = 0.06f
    override val altoRepisa: Dp = 114.dp
    override val tabla: Dp = 17.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.goudy

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.76f + 0.16f * azar(semilla, 1)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(ROBLE_OSC), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(VETA).copy(alpha = 0.5f), 13, densidad = 0.5f, vertical = true, grosor = 0.7f)
        // El fondo de cada repisa: lancetas ciegas talladas, con su trébol arriba.
        d.repisas.forEach { r ->
            val aw = 24 * u
            var x = r.left + 5 * u
            while (x + aw < r.right - 3 * u) {
                val lanceta = Rect(x, r.top + 12 * u, x + aw, r.bottom - 2 * u)
                drawPath(arcoApuntado(lanceta, aw * 0.9f), c(ROBLE).copy(alpha = 0.6f))
                val dentro = lanceta.deflate(2.4f * u)
                drawPath(arcoApuntado(dentro, dentro.width * 0.9f), c(ROBLE_OSC))
                drawCircle(c(ROBLE).copy(alpha = 0.6f), aw * 0.16f, Offset(lanceta.center.x, lanceta.top + aw * 0.72f), style = Stroke(1f * u))
                x += aw + 6 * u
            }
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.3f), r.topLeft, Size(r.width, 10 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), r.topLeft, Size(r.width, 4 * u))
        }
        // Los costados: contrafuertes con su canal y pequeños gabletes escalonados.
        listOf(d.izquierda, d.derecha).forEachIndexed { k, r ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.5f), 7 + k, densidad = 1.1f, vertical = true, grosor = 0.7f)
            drawRect(c(ROBLE_OSC), Offset(r.left + r.width * 0.32f, r.top), Size(r.width * 0.36f, r.height))
            var yy = r.top + 30 * u
            while (yy < r.bottom - 10 * u) {
                val gab = Path().apply { moveTo(r.left, yy); lineTo(r.center.x, yy - r.width * 0.6f); lineTo(r.right, yy); close() }
                drawPath(gab, c(ROBLE_LUZ))
                drawRect(c(ROBLE_LUZ), Offset(r.left, yy), Size(r.width, 2 * u))
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, yy + 2 * u), Size(r.width, 1.2f * u))
                yy += 60 * u
            }
            bisel(r, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 1.4f * u)
        }
        // Las tablas, con un friso de cuadrifolios en el canto.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.45f), 23 + k, densidad = 0.6f, grosor = 0.6f)
            drawRect(c(ROBLE_LUZ), r.topLeft, Size(r.width, 2 * u))
            val rf = r.height * 0.13f
            var fx = r.left + 8 * u
            while (fx < r.right - 6 * u) {
                listOf(Offset(0f, -rf), Offset(rf, 0f), Offset(0f, rf), Offset(-rf, 0f)).forEach { o ->
                    drawCircle(c(VETA).copy(alpha = 0.7f), rf, Offset(fx + o.x, r.center.y + o.y), style = Stroke(0.7f * u))
                }
                fx += 14 * u
            }
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una filacteria de vitela, con las puntas en cola de golondrina.
        d.placas.forEach { p ->
            val r = p.r
            val m = r.height * 0.5f
            val cinta = Path().apply {
                moveTo(r.left - m, r.top); lineTo(r.right + m, r.top); lineTo(r.right + m * 0.4f, r.center.y); lineTo(r.right + m, r.bottom)
                lineTo(r.left - m, r.bottom); lineTo(r.left - m * 0.4f, r.center.y); close()
            }
            drawPath(cinta, Color.Black.copy(alpha = 0.25f))
            drawRect(c(VITELA), r.topLeft, r.size)
            clipPath(cinta) {
                drawRect(c(VITELA), Offset(r.left - m, r.top), Size(r.width + 2 * m, r.height))
                drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.left - m, r.top), Size(m, r.height))
                drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.right, r.top), Size(m, r.height))
            }
            drawPath(cinta, c(ORO), style = Stroke(0.7f * u))
            textoEn(medidor, p.texto, r.deflate(1.2f * u), letra, c(TINTA), 11f)
        }
        corona(d, oscuro, noche)
        // La base: el zócalo con su arquería ciega, sobre losas en damero.
        val b = d.base
        val piso = b.top + b.height * 0.62f
        drawRect(c(LOSA_CLARA), Offset(0f, piso), Size(w, h - piso))
        val losa = 12 * u
        var lx = 0f; var col = 0
        while (lx < w) {
            var ly = piso; var fila = 0
            while (ly < h) {
                if ((col + fila) % 2 == 0) drawRect(c(LOSA_OSCURA), Offset(lx, ly), Size(losa, minOf(losa * 0.5f, h - ly)))
                ly += losa * 0.5f; fila++
            }
            lx += losa; col++
        }
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(ROBLE), zocalo.topLeft, zocalo.size)
        vetas(zocalo, c(VETA).copy(alpha = 0.45f), 45, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(ROBLE_LUZ), zocalo.topLeft, Size(zocalo.width, 2.4f * u))
        val arquillo = 12 * u
        var ax = zocalo.left + 4 * u
        while (ax + arquillo < zocalo.right - 2 * u) {
            val a = Rect(ax, zocalo.top + zocalo.height * 0.25f, ax + arquillo, zocalo.bottom - 2 * u)
            drawPath(arcoApuntado(a, arquillo * 0.8f), c(ROBLE_OSC))
            drawPath(arcoApuntado(a, arquillo * 0.8f), c(ROBLE_LUZ), style = Stroke(0.8f * u))
            ax += arquillo + 3 * u
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** El muro, la crestería, los pináculos y el gablete con el rosetón. */
    private fun DrawScope.corona(d: DisposicionEstante, oscuro: Boolean, noche: Boolean) {
        val u = density
        val r = d.corona
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro de piedra detrás.
        drawRect(c(MURO), r.topLeft, r.size)
        var my = r.top + 16 * u; var mk = 0
        while (my < r.bottom) {
            drawLine(Color.Black.copy(alpha = 0.18f), Offset(r.left, my), Offset(r.right, my), 0.8f * u)
            var mx = r.left + (if (mk % 2 == 0) 0f else 20 * u)
            while (mx < r.right) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(mx, my - 16 * u), Offset(mx, my), 0.8f * u); mx += 40 * u }
            my += 16 * u; mk++
        }
        val cornisa = Rect(r.left, r.bottom - r.height * 0.13f, r.right, r.bottom)
        // La crestería de arquillos sobre la cornisa.
        val ar = 9 * u
        var ax = r.left
        while (ax < r.right) {
            val a = Rect(ax + 1 * u, cornisa.top - 9 * u, ax + ar - 1 * u, cornisa.top)
            drawPath(arcoApuntado(a, a.width * 0.9f), c(ROBLE_LUZ))
            drawPath(arcoApuntado(a.deflate(1.4f * u), a.width * 0.6f), c(ROBLE_OSC))
            ax += ar
        }
        // El gablete con sus crochets y su florón.
        val cx = r.center.x
        val pieIzq = Offset(r.left + r.width * 0.2f, cornisa.top)
        val pieDer = Offset(r.right - r.width * 0.2f, cornisa.top)
        val cima = Offset(cx, r.top + r.height * 0.08f)
        val gablete = Path().apply { moveTo(pieIzq.x, pieIzq.y); lineTo(cima.x, cima.y); lineTo(pieDer.x, pieDer.y); close() }
        drawPath(gablete, c(ROBLE))
        clipPath(gablete) { vetas(Rect(pieIzq.x, cima.y, pieDer.x, cornisa.top), c(VETA).copy(alpha = 0.4f), 71, densidad = 0.6f, grosor = 0.7f) }
        drawPath(gablete, c(ROBLE_LUZ), style = Stroke(2.4f * u))
        for (k in 1..5) {
            val f = k / 6f
            listOf(pieIzq, pieDer).forEach { p ->
                val o = Offset(p.x + (cima.x - p.x) * f, p.y + (cima.y - p.y) * f)
                val fuera = if (p.x < cx) -1f else 1f
                drawCircle(c(ROBLE_LUZ), 2.6f * u, Offset(o.x + fuera * 1.8f * u, o.y - 2 * u))
                drawCircle(c(VETA).copy(alpha = 0.5f), 1f * u, Offset(o.x + fuera * 2.4f * u, o.y - 2.4f * u))
            }
        }
        drawCircle(c(ORO), 3 * u, Offset(cx, cima.y - 3 * u))
        drawCircle(c(ORO), 2 * u, Offset(cx - 3.4f * u, cima.y - 1 * u))
        drawCircle(c(ORO), 2 * u, Offset(cx + 3.4f * u, cima.y - 1 * u))
        drawCircle(c(ORO), 2 * u, Offset(cx, cima.y - 7 * u))
        // El rosetón.
        val rv = minOf(r.height * 0.26f, r.width * 0.12f)
        val cv = Offset(cx, cornisa.top - (cornisa.top - cima.y) * 0.4f)
        roseton(cv, rv, oscuro, noche)
        // Los pináculos: dos grandes sobre los costados y dos chicos al pie del gablete.
        listOf(d.izquierda.center.x, d.derecha.center.x, pieIzq.x, pieDer.x).forEachIndexed { k, px ->
            val pw = if (k < 2) d.izquierda.width * 0.8f else 8 * u
            val top0 = if (k < 2) r.top + r.height * 0.36f else r.top + r.height * 0.52f
            drawRect(c(ROBLE), Offset(px - pw / 2, top0), Size(pw, cornisa.top - top0))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(px + pw * 0.2f, top0), Size(pw * 0.3f, cornisa.top - top0))
            drawRect(c(ROBLE_LUZ), Offset(px - pw / 2, top0), Size(pw, 1.6f * u))
            val alto0 = pw * 1.8f
            val aguja = Path().apply { moveTo(px - pw / 2, top0); lineTo(px, top0 - alto0); lineTo(px + pw / 2, top0); close() }
            drawPath(aguja, c(ROBLE_LUZ))
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(px, top0 - alto0), Offset(px + pw / 2, top0), 1f * u)
            for (j in 1..3) {
                val f = j / 4f
                val yy = top0 - alto0 * f
                val dx = pw / 2 * (1f - f)
                drawCircle(c(ROBLE_LUZ), pw * 0.13f, Offset(px - dx - pw * 0.08f, yy))
                drawCircle(c(ROBLE_LUZ), pw * 0.13f, Offset(px + dx + pw * 0.08f, yy))
            }
            drawCircle(c(ORO), pw * 0.2f, Offset(px, top0 - alto0 - pw * 0.12f))
        }
        // La cornisa, con su filete dorado.
        drawRect(c(ROBLE), cornisa.topLeft, cornisa.size)
        vetas(cornisa, c(VETA).copy(alpha = 0.45f), 73, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(ROBLE_LUZ), cornisa.topLeft, Size(cornisa.width, 2 * u))
        drawLine(c(ORO), Offset(cornisa.left, cornisa.top + cornisa.height * 0.45f), Offset(cornisa.right, cornisa.top + cornisa.height * 0.45f), 0.8f * u)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(cornisa.left, cornisa.bottom - 2 * u), Size(cornisa.width, 2 * u))
        // De noche, los haces de color del rosetón caen sobre el mueble.
        if (noche) {
            listOf(-1, 0, 1).forEachIndexed { j, s ->
                val haz = Path().apply {
                    moveTo(cv.x + s * rv * 0.5f - rv * 0.2f, cv.y); lineTo(cv.x + s * rv * 0.5f + rv * 0.2f, cv.y)
                    lineTo(cv.x + s * rv * 1.6f + rv * 0.5f, r.bottom + 70 * u); lineTo(cv.x + s * rv * 1.6f - rv * 0.5f, r.bottom + 70 * u); close()
                }
                drawPath(haz, VIDRIOS[j].copy(alpha = 0.08f))
            }
        }
    }

    /** El rosetón: dos coronas de vidrios de colores con su tracería de piedra. De día, pálido; de noche, encendido por la luna. */
    private fun DrawScope.roseton(cv: Offset, rv: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        val vid = VIDRIOS.map { col ->
            if (noche) col else if (oscuro) mezcla(col, Color.Black, 0.45f) else mezcla(col, Color.White, 0.32f)
        }
        val piedra = t(PIEDRA, oscuro, noche)
        if (noche) mancha(cv, rv * 1.6f, LUNA.copy(alpha = 0.32f))
        drawCircle(piedra, rv + 4 * u, cv)
        for (k in 0 until 16) {
            drawArc(vid[k % vid.size], -90f + k * 22.5f, 22.5f, true, Offset(cv.x - rv, cv.y - rv), Size(rv * 2, rv * 2))
        }
        val ri = rv * 0.62f
        for (k in 0 until 8) {
            drawArc(vid[(k + 2) % vid.size], -90f + k * 45f, 45f, true, Offset(cv.x - ri, cv.y - ri), Size(ri * 2, ri * 2))
        }
        drawCircle(vid[2], rv * 0.22f, cv)
        // La tracería: radios, anillos y lóbulos.
        for (k in 0 until 16) {
            val a = Math.toRadians(-90.0 + k * 22.5)
            val cs = kotlin.math.cos(a).toFloat(); val sn = kotlin.math.sin(a).toFloat()
            drawLine(piedra, Offset(cv.x + cs * ri, cv.y + sn * ri), Offset(cv.x + cs * rv, cv.y + sn * rv), 1.6f * u)
            val am = Math.toRadians(-90.0 + k * 22.5 + 11.25)
            drawCircle(piedra, rv * 0.11f, Offset(cv.x + kotlin.math.cos(am).toFloat() * rv * 0.82f, cv.y + kotlin.math.sin(am).toFloat() * rv * 0.82f), style = Stroke(1f * u))
        }
        for (k in 0 until 8) {
            val a = Math.toRadians(-90.0 + k * 45.0)
            val cs = kotlin.math.cos(a).toFloat(); val sn = kotlin.math.sin(a).toFloat()
            drawLine(piedra, Offset(cv.x + cs * rv * 0.22f, cv.y + sn * rv * 0.22f), Offset(cv.x + cs * ri, cv.y + sn * ri), 1.8f * u)
        }
        drawCircle(piedra, ri, cv, style = Stroke(2 * u))
        drawCircle(piedra, rv * 0.22f, cv, style = Stroke(1.8f * u))
        drawCircle(piedra, rv, cv, style = Stroke(3 * u))
        if (noche) drawCircle(LUNA.copy(alpha = 0.18f), rv * 0.5f, cv)
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val tinte = TERCIOPELOS[(v.semilla and 0x7fffffff) % TERCIOPELOS.size]
        val terciopelo = c(if (v.tono != null) mezcla(tinte, v.tono, 0.24f) else tinte)
        // Los registros de seda que asoman abajo, más cuanto más escribiste (se dibujan antes: salen de entre las hojas).
        val registros = 1 + (v.lleno * 2).toInt()
        for (j in 0 until registros) {
            val x = w * (0.34f + 0.12f * j)
            val cinta = Path().apply {
                moveTo(x, h * 0.9f); lineTo(x + 2.4f * u, h * 0.9f); lineTo(x + 2.4f * u, h)
                lineTo(x + 1.2f * u, h - 1.6f * u); lineTo(x, h); close()
            }
            drawPath(cinta, c(REGISTROS[j % REGISTROS.size]))
        }
        // El libro, con el pelo del terciopelo: luz a un lado, sombra al otro.
        val libro = Rect(0f, 0f, w, h * 0.95f)
        drawRoundRect(terciopelo, libro.topLeft, libro.size, CornerRadius(2f * u))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.06f, 0f), Size(w * 0.18f, libro.height))
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(w * 0.84f, 0f), Size(w * 0.16f, libro.height))
        drawRect(c(ORO).copy(alpha = 0.6f), Offset(0f, libro.bottom - 1.2f * u), Size(w, 1.2f * u))
        // Los bullones dorados de las esquinas.
        val rb = minOf(w * 0.12f, 3.4f * u)
        listOf(Offset(w * 0.18f, h * 0.05f), Offset(w * 0.82f, h * 0.05f), Offset(w * 0.18f, h * 0.88f), Offset(w * 0.82f, h * 0.88f)).forEach { o ->
            drawCircle(c(ORO), rb, o)
            drawCircle(Color.Black.copy(alpha = 0.3f), rb * 0.45f, o)
            drawCircle(Color.White.copy(alpha = 0.35f), rb * 0.3f, Offset(o.x - rb * 0.35f, o.y - rb * 0.35f))
        }
        // El bullón de en medio; de noche, una luna de plata.
        val cb = Offset(w * 0.5f, h * 0.17f)
        val rc = minOf(w * 0.2f, 5 * u)
        if (v.noche) {
            drawCircle(PLATA, rc, cb)
            drawCircle(terciopelo, rc * 0.84f, Offset(cb.x + rc * 0.42f, cb.y - rc * 0.24f))
        } else {
            drawPath(estrella(cb, rc, 8, 0.6f), c(ORO))
            drawCircle(c(LATON), rc * 0.45f, cb)
        }
        // Los dos broches de latón, a la derecha.
        listOf(0.27f, 0.8f).forEach { f ->
            val y = h * f
            drawRect(c(LATON), Offset(w * 0.76f, y - 2 * u), Size(w * 0.24f, 4 * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.76f, y + 1.2f * u), Size(w * 0.24f, 0.8f * u))
            drawCircle(c(ORO), 1.2f * u, Offset(w * 0.84f, y))
        }
        // La cartela de latón con el día, sobre vitela.
        val cartela = Rect(w * 0.12f, h * 0.33f, w * 0.88f, h * 0.5f)
        drawRect(c(LATON), cartela.topLeft, cartela.size)
        val vitela = cartela.deflate(minOf(cartela.width, cartela.height) * 0.14f)
        drawRect(c(VITELA), vitela.topLeft, vitela.size)
        drawRect(Color.Black.copy(alpha = 0.3f), cartela.topLeft, cartela.size, style = Stroke(0.7f * u))
        textoEn(medidor, v.dia, vitela, letra, c(TINTA), 14f)
        // El mes, en oro a lo largo de la tapa.
        textoEn(medidor, v.mes, Rect(w * 0.22f, h * 0.53f, w * 0.74f, h * 0.83f), letra, mezcla(c(ORO), ORO, 0.4f), 9f, vertical = true, minimo = 5f)
    }
}

/**
 * Al-Ándalus: la alcoba de la biblioteca. Un fondo de yesería con una arquería de herradura en cada repisa (nichos en
 * sombra, con su alfiz y su filo de almagra), costados de cedro con una columnilla de mármol, tablas de cedro con una
 * cinta de lazo dorado y cada mes en una placa de mármol con borde azul. Arriba, el alero de cedro con sus canecillos, una
 * banda azul con lazo de oro (formas, no letras) y tres hiladas de mocárabes en azul, almagra, oro y yeso; del alero cuelga
 * un farol de latón calado que de noche se enciende y proyecta estrellas sobre las paredes. Abajo, un zócalo de alicatado de
 * estrellas de ocho puntas sobre un piso de barro. Cada entrada es un libro de cuero labrado con su solapa (la lengüeta que
 * dobla sobre la tapa), un filete dorado más cuanto más escribiste, cantoneras, el medallón geométrico con el día dentro y
 * el mes a lo largo; en la punta de la solapa, una estrellita dorada y, de noche, una luna en su lugar. Distinto del
 * Ideario, que es la alacena del muro con su sebka y un solo arco.
 */
object EstanteAndalus : DisenoBiblioteca {
    private val YESO = Color(0xFFE8DCC2)
    private val YESO_SOMBRA = Color(0xFFC6B696)
    private val ALMAGRA = Color(0xFF9C3A26)
    private val AZUL = Color(0xFF2C4F86)
    private val VERDE = Color(0xFF2F6E52)
    private val ORO = Color(0xFFC9A04E)
    private val ORO_CLARO = Color(0xFFE3C47E)
    private val CEDRO = Color(0xFF7A4A2A)
    private val CEDRO_LUZ = Color(0xFF96643C)
    private val CEDRO_OSC = Color(0xFF4E2E18)
    private val VETA = Color(0xFF2E1A0C)
    private val MARMOL = Color(0xFFE6E1D6)
    private val BARRO = Color(0xFFA45C3A)
    private val NEGRO = Color(0xFF1C1A1E)
    private val LATON = Color(0xFFC69C4E)
    private val TINTA = Color(0xFF221812)
    private val LUZ_FAROL = Color(0xFFF5CF7A)
    private val LUNA = Color(0xFFEDE6C8)
    /** Los cueros labrados: rojo, castaño, verde oliva, sangre de buey y miel. */
    private val PIELES = listOf(Color(0xFF7A3A24), Color(0xFF55331F), Color(0xFF4B4A2A), Color(0xFF5E1F1E), Color(0xFF8C5A2C))
    private val NOCHE = Color(0xFF141C34)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, a la luz del farol. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100C0A), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.44f)
        return r
    }

    override val corona = 0.32f
    override val base = 0.14f
    override val lado = 0.055f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.amiri

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.78f + 0.16f * azar(semilla, 1)

    /** Las estrellas que proyecta el farol encendido sobre [r]: [n] estrellas de ocho puntas, siempre en el mismo sitio. */
    private fun DrawScope.estrellasFarol(r: Rect, n: Int, semilla: Int) {
        val u = density
        for (k in 0 until n) {
            val o = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla, k + 50))
            drawPath(estrella(o, (2.2f + 1.6f * azar(semilla, k + 90)) * u, 8, 0.55f), LUZ_FAROL.copy(alpha = 0.3f))
        }
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de yesería, con una arquería de herradura en cada repisa.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(YESO), cuerpo.topLeft, cuerpo.size)
        d.repisas.forEach { r ->
            val nw = 30 * u
            var x = r.left + 6 * u
            while (x + nw < r.right - 4 * u) {
                val nicho = Rect(x, r.top + 14 * u, x + nw, r.bottom)
                drawRect(c(ALMAGRA).copy(alpha = 0.55f), Offset(nicho.left - 3 * u, nicho.top - 4 * u), Size(nicho.width + 6 * u, nicho.height + 4 * u), style = Stroke(0.8f * u))
                drawPath(arcoHerradura(nicho.inflate(1.6f * u)), c(ALMAGRA).copy(alpha = 0.7f))
                drawPath(arcoHerradura(nicho), c(YESO_SOMBRA))
                drawRect(Color.Black.copy(alpha = 0.08f), Offset(nicho.left, nicho.top), Size(nicho.width, nicho.height * 0.3f))
                x += nw + 8 * u
            }
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.26f), r.topLeft, Size(r.width, 9 * u))
        }
        if (noche) d.repisas.firstOrNull()?.let { estrellasFarol(it, 16, 7) }
        // Los costados: cedro con una columnilla de mármol, su basa y su capitel.
        listOf(d.izquierda, d.derecha).forEachIndexed { k, r ->
            drawRect(c(CEDRO), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.5f), 9 + k, densidad = 1.1f, vertical = true, grosor = 0.7f)
            val fuste = Rect(r.center.x - r.width * 0.22f, r.top + 8 * u, r.center.x + r.width * 0.22f, r.bottom - 8 * u)
            drawRect(c(MARMOL), fuste.topLeft, fuste.size)
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(fuste.right - fuste.width * 0.3f, fuste.top), Size(fuste.width * 0.3f, fuste.height))
            drawRect(c(MARMOL), Offset(r.left + r.width * 0.12f, fuste.top - 6 * u), Size(r.width * 0.76f, 6 * u))
            drawRect(c(ORO).copy(alpha = 0.7f), Offset(r.left + r.width * 0.12f, fuste.top - 6 * u), Size(r.width * 0.76f, 1.2f * u))
            drawRect(c(MARMOL), Offset(r.left + r.width * 0.16f, fuste.bottom), Size(r.width * 0.68f, 5 * u))
            bisel(r, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 1.2f * u)
        }
        // Las tablas de cedro con una cinta de lazo dorado en el canto.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(CEDRO), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.45f), 25 + k, densidad = 0.6f, grosor = 0.6f)
            drawRect(c(CEDRO_LUZ), r.topLeft, Size(r.width, 1.8f * u))
            val banda = Rect(r.left, r.top + r.height * 0.3f, r.right, r.top + r.height * 0.75f)
            drawRect(c(ALMAGRA), banda.topLeft, banda.size)
            var lx = banda.left
            val paso = banda.height * 1.2f
            while (lx < banda.right) {
                drawLine(c(ORO), Offset(lx, banda.top), Offset(lx + paso, banda.bottom), 0.8f * u)
                drawLine(c(ORO), Offset(lx, banda.bottom), Offset(lx + paso, banda.top), 0.8f * u)
                lx += paso
            }
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una placa de mármol con borde azul.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
            drawRect(c(AZUL), r.topLeft, r.size)
            val dentro = r.deflate(1.6f * u)
            drawRect(c(MARMOL), dentro.topLeft, dentro.size)
            listOf(r.left + r.height * 0.5f, r.right - r.height * 0.5f).forEach { cx ->
                drawPath(estrella(Offset(cx, r.center.y), r.height * 0.28f, 8, 0.6f), c(AZUL))
            }
            textoEn(medidor, p.texto, Rect(r.left + r.height, r.top, r.right - r.height, r.bottom), letra, c(TINTA), 11f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de alicatado de estrellas y el piso de barro.
        val b = d.base
        val piso = b.top + b.height * 0.78f
        drawRect(c(BARRO), Offset(0f, piso), Size(w, h - piso))
        var px = 0f
        while (px < w) {
            drawLine(Color.Black.copy(alpha = 0.22f), Offset(px, piso), Offset(px, h), 0.8f * u)
            px += 16 * u
        }
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(MARMOL), zocalo.topLeft, zocalo.size)
        val fajas = Rect(zocalo.left, zocalo.top + 3 * u, zocalo.right, zocalo.bottom - 3 * u)
        val ts = fajas.height / 2
        val colores = listOf(AZUL, VERDE, ALMAGRA, NEGRO)
        var tx = fajas.left; var ti = 0
        while (tx < fajas.right) {
            for (tj in 0..1) {
                val cc = Offset(tx + ts / 2, fajas.top + ts * tj + ts / 2)
                drawPath(estrella(cc, ts * 0.44f, 8, 0.72f), c(colores[(ti + tj) % colores.size]))
                drawCircle(c(MARMOL), ts * 0.12f, cc)
                drawRect(c(ORO).copy(alpha = 0.7f), Offset(tx - 1.2f * u, fajas.top + ts * tj - 1.2f * u), Size(2.4f * u, 2.4f * u))
            }
            tx += ts; ti++
        }
        drawRect(c(AZUL), zocalo.topLeft, Size(zocalo.width, 3 * u))
        drawRect(c(AZUL), Offset(zocalo.left, zocalo.bottom - 3 * u), Size(zocalo.width, 3 * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(zocalo.left, piso), Size(zocalo.width, 2.4f * u))
    }

    /** El alero, la banda de lazo, los mocárabes y el farol. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(YESO), r.topLeft, r.size)
        // El alero de cedro con sus canecillos de lóbulos.
        val alero = Rect(r.left, r.top + r.height * 0.04f, r.right, r.top + r.height * 0.14f)
        drawRect(c(CEDRO), alero.topLeft, alero.size)
        vetas(alero, c(VETA).copy(alpha = 0.45f), 81, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(CEDRO_LUZ), alero.topLeft, Size(alero.width, 1.6f * u))
        var cx = alero.left + 8 * u
        while (cx < alero.right - 6 * u) {
            drawRect(c(CEDRO_OSC), Offset(cx - 3 * u, alero.bottom), Size(6 * u, 4 * u))
            drawCircle(c(CEDRO_OSC), 3 * u, Offset(cx, alero.bottom + 4 * u))
            cx += 16 * u
        }
        // La banda azul con su lazo de oro: formas, no letras.
        val banda = Rect(r.left, alero.bottom + 8 * u, r.right, r.top + r.height * 0.28f)
        drawRect(c(AZUL), banda.topLeft, banda.size)
        val onda = Path().apply {
            moveTo(banda.left, banda.center.y)
            var x = banda.left
            while (x < banda.right) {
                quadraticTo(x + 6 * u, banda.top + 2 * u, x + 12 * u, banda.center.y)
                quadraticTo(x + 18 * u, banda.bottom - 2 * u, x + 24 * u, banda.center.y)
                x += 24 * u
            }
        }
        drawPath(onda, c(ORO), style = Stroke(1f * u))
        var dx = banda.left + 12 * u
        while (dx < banda.right) { drawCircle(c(ORO), 1.2f * u, Offset(dx, banda.center.y)); dx += 24 * u }
        // Los mocárabes: tres hiladas de nichos que bajan escalonados.
        val zona = Rect(r.left, banda.bottom, r.right, r.bottom - r.height * 0.12f)
        val filas = 3
        val altoFila = zona.height / filas
        val tonos = listOf(AZUL, YESO, ALMAGRA, YESO_SOMBRA, ORO, YESO)
        for (f in 0 until filas) {
            val cw = 20 * u
            val y0 = zona.top + f * altoFila
            var x = zona.left - (if (f % 2 == 0) 0f else cw / 2)
            var k = 0
            while (x < zona.right) {
                val celda = Rect(x + 0.6f * u, y0, x + cw - 0.6f * u, y0 + altoFila)
                drawRect(c(YESO_SOMBRA), celda.topLeft, celda.size)
                val nicho = arcoApuntado(celda.deflate(1.2f * u), celda.width * 0.7f)
                drawPath(nicho, c(tonos[(k + f * 2) % tonos.size]))
                drawPath(nicho, Color.Black.copy(alpha = 0.25f), style = Stroke(0.6f * u))
                x += cw; k++
            }
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(zona.left, y0 + altoFila - 1.4f * u), Size(zona.width, 1.4f * u))
        }
        // La cornisa de cedro sobre la primera repisa.
        val cornisa = Rect(r.left, r.bottom - r.height * 0.12f, r.right, r.bottom)
        drawRect(c(CEDRO), cornisa.topLeft, cornisa.size)
        drawRect(c(CEDRO_LUZ), cornisa.topLeft, Size(cornisa.width, 1.6f * u))
        drawLine(c(ORO), Offset(cornisa.left, cornisa.center.y), Offset(cornisa.right, cornisa.center.y), 0.8f * u)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(cornisa.left, cornisa.bottom - 2 * u), Size(cornisa.width, 2 * u))
        // El farol de latón calado, colgado del alero; de noche, encendido y echando estrellas.
        val fx = r.center.x
        val cuerpoF = Rect(fx - r.width * 0.045f, zona.top + zona.height * 0.3f, fx + r.width * 0.045f, zona.top + zona.height * 0.82f)
        drawLine(c(LATON), Offset(fx, alero.bottom + 4 * u), Offset(fx, cuerpoF.top - 8 * u), 1f * u)
        if (noche) {
            mancha(cuerpoF.center, r.height * 0.6f, LUZ_FAROL.copy(alpha = 0.4f))
            estrellasFarol(Rect(r.left, banda.top, r.right, r.bottom), 22, 3)
        }
        val techo = Path().apply { moveTo(fx, cuerpoF.top - 9 * u); lineTo(cuerpoF.right + 2 * u, cuerpoF.top); lineTo(cuerpoF.left - 2 * u, cuerpoF.top); close() }
        drawPath(techo, c(LATON))
        drawRect(if (noche) LUZ_FAROL else c(LATON), cuerpoF.topLeft, cuerpoF.size)
        val calado = if (noche) Color(0xFFFFF4D0) else c(NEGRO).copy(alpha = 0.6f)
        for (j in 0 until 3) {
            drawPath(estrella(Offset(fx, cuerpoF.top + cuerpoF.height * (0.2f + 0.3f * j)), cuerpoF.width * 0.22f, 8, 0.55f), calado)
        }
        drawRect(c(LATON).copy(alpha = if (noche) 0.9f else 1f), cuerpoF.topLeft, cuerpoF.size, style = Stroke(1.4f * u))
        drawLine(c(LATON), Offset(fx, cuerpoF.top), Offset(fx, cuerpoF.bottom), 0.8f * u)
        val fondo = Path().apply { moveTo(cuerpoF.left - 2 * u, cuerpoF.bottom); lineTo(cuerpoF.right + 2 * u, cuerpoF.bottom); lineTo(fx, cuerpoF.bottom + 7 * u); close() }
        drawPath(fondo, c(LATON))
        drawCircle(c(LATON), 1.8f * u, Offset(fx, cuerpoF.top - 9.5f * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val tinte = PIELES[(v.semilla and 0x7fffffff) % PIELES.size]
        val piel = c(if (v.tono != null) mezcla(tinte, v.tono, 0.22f) else tinte)
        drawRoundRect(piel, Offset.Zero, size, CornerRadius(1.6f * u))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.06f, 0f), Size(w * 0.12f, h))
        // Los filetes dorados del recuadro: uno más cuanto más escribiste.
        val filetes = 1 + (v.lleno * 2).toInt()
        for (j in 0 until filetes) {
            val m = (2.4f + j * 1.8f) * u
            drawRect(c(ORO).copy(alpha = 0.75f), Offset(m, m), Size(w * 0.84f - 2 * m, h - 2 * m), style = Stroke(0.6f * u))
        }
        // Las cantoneras labradas de la izquierda.
        val rq = minOf(w * 0.24f, 6 * u)
        drawArc(c(ORO).copy(alpha = 0.8f), 0f, 90f, false, Offset(-rq, -rq), Size(rq * 2, rq * 2), style = Stroke(0.8f * u))
        drawArc(c(ORO).copy(alpha = 0.8f), 270f, 90f, false, Offset(-rq, h - rq), Size(rq * 2, rq * 2), style = Stroke(0.8f * u))
        // El medallón geométrico, con el día dentro.
        val cm = Offset(w * 0.42f, h * 0.3f)
        val rm = minOf(w * 0.4f, h * 0.14f)
        drawPath(estrella(cm, rm, 8, 0.8f), mezcla(piel, Color.Black, 0.25f))
        drawPath(estrella(cm, rm, 8, 0.8f), c(ORO), style = Stroke(0.9f * u))
        drawCircle(c(ORO), rm * 0.66f, cm, style = Stroke(0.7f * u))
        listOf(-1f, 1f).forEach { s -> drawCircle(c(ORO), 1.3f * u, Offset(cm.x, cm.y + s * (rm + 2.4f * u))) }
        textoEn(medidor, v.dia, Rect(cm, rm * 0.62f), letra, mezcla(c(ORO_CLARO), ORO_CLARO, 0.4f), 13f)
        // El mes, dorado a lo largo de la tapa.
        textoEn(medidor, v.mes, Rect(w * 0.16f, h * 0.5f, w * 0.68f, h * 0.8f), letra, mezcla(c(ORO_CLARO), ORO_CLARO, 0.4f), 9f, vertical = true, minimo = 4.5f)
        // La solapa: la franja del canto y su lengüeta en punta que dobla sobre la tapa.
        val solapa = mezcla(piel, Color.Black, 0.15f)
        drawRect(solapa, Offset(w * 0.84f, 0f), Size(w * 0.16f, h))
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(w * 0.84f, 0f), Offset(w * 0.84f, h), 0.8f * u)
        val lengueta = Path().apply { moveTo(w * 0.84f, h * 0.8f); lineTo(w * 0.56f, h * 0.885f); lineTo(w * 0.84f, h * 0.97f); close() }
        drawPath(lengueta, solapa)
        drawPath(lengueta, c(ORO).copy(alpha = 0.7f), style = Stroke(0.6f * u))
        // En la punta de la lengüeta: una estrellita dorada; de noche, la luna.
        val cp = Offset(w * 0.72f, h * 0.885f)
        val rp = minOf(w * 0.08f, 2.6f * u)
        if (v.noche) {
            drawCircle(LUNA, rp, cp)
            drawCircle(solapa, rp * 0.8f, Offset(cp.x + rp * 0.4f, cp.y - rp * 0.2f))
        } else {
            drawPath(estrella(cp, rp, 8, 0.55f), c(ORO))
        }
    }
}

/**
 * Mongol: el casillero pintado de la ger. Un mueble de casillas bajas, rojo y naranja, con los divisores pintados y
 * filetes de oro, costados naranjas con un recuadro azul y lunares de oro, tablas rojas con la greca del martillo en oro y
 * cada mes en un cartucho crema de borde azul. Arriba, contra el fieltro blanco: el toono (la corona de madera por donde
 * entra la luz) con sus varas que bajan hasta el khana (la celosía de listones cruzados de la pared) y, sobre el mueble, el
 * copete pintado con el nudo infinito en oro sobre azul y nubes de oro en paneles verdes. De noche, por el toono se ven las
 * estrellas. Abajo, una alfombra roja con su cenefa. Cada entrada es un pothi tumbado en su casilla: las hojas largas entre
 * dos tablas, envuelto en tela amarilla con sus pliegues, el mes escrito sobre la tela, la cinta que lo ata (una vuelta más
 * cuanto más escribiste) y la etiqueta de tela colgando con el día; en ella, un sello rojo y, de noche, una luna. Como van
 * tumbados, todos son igual de anchos (cada uno cabe en su casilla) y lo que crece con las palabras es el grosor. Distinto
 * del Ideario, que es el interior de la ger con sus arcones pintados.
 */
object EstanteMongol : DisenoBiblioteca {
    private val ROJO = Color(0xFFB0301F)
    private val ROJO_OSC = Color(0xFF6E1C12)
    private val NARANJA = Color(0xFFDA7A2C)
    private val AZUL = Color(0xFF2D5A8F)
    private val VERDE = Color(0xFF3D7A50)
    private val ORO = Color(0xFFE0B54A)
    private val FIELTRO = Color(0xFFE9E0CC)
    private val FIELTRO_OSC = Color(0xFFC9BEA6)
    private val KHANA = Color(0xFFB5652E)
    private val CIELO = Color(0xFF9CC3E0)
    private val CIELO_NOCHE = Color(0xFF101836)
    private val ESTRELLA = Color(0xFFF2EBD0)
    private val ALFOMBRA = Color(0xFF8E2A22)
    private val CREMA = Color(0xFFF0E6CC)
    private val HOJAS = Color(0xFFE8DDBF)
    private val TINTA = Color(0xFF231812)
    private val CINTA = Color(0xFF8E2A22)
    private val LUNA = Color(0xFFEDE6C8)
    /** Las telas amarillas y azafrán que envuelven los pothi. */
    private val TELAS = listOf(Color(0xFFE2B33A), Color(0xFFD9A22C), Color(0xFFE8C04E), Color(0xFFCF8E2A))
    /** Las tablas de madera pintada que encierran las hojas. */
    private val TAPAS = listOf(Color(0xFF7A3A22), Color(0xFF8A5A30), Color(0xFF5E2A1E))
    private val NOCHE = Color(0xFF141A30)
    /** El ancho fijo de cada pothi, en dp: así cada uno cae en su casilla. */
    private val ANCHO_POTHI = 80

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo las estrellas del toono. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0F0B09), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    override val corona = 0.36f
    override val base = 0.1f
    override val lado = 0.06f
    override val altoRepisa: Dp = 50.dp
    override val tabla: Dp = 14.dp
    override val separa: Dp = 8.dp
    override val letra: FontFamily = LetraExtra.filosofo

    override fun ancho(palabras: Int): Dp = ANCHO_POTHI.dp
    override fun alto(semilla: Int, palabras: Int): Float =
        (0.4f + 0.52f * (palabras / 800f).coerceIn(0f, 1f) + 0.06f * azar(semilla, 1)).coerceAtMost(1f)

    /** El nudo infinito: una trama en rombo con un lazo en cada punta. */
    private fun DrawScope.nudo(c0: Offset, s: Float, color: Color, grosor: Float) {
        rotate(45f, c0) {
            val a = s / 2
            for (j in 0..3) {
                val f = -a + j * s / 3f
                drawLine(color, Offset(c0.x - a, c0.y + f), Offset(c0.x + a, c0.y + f), grosor)
                drawLine(color, Offset(c0.x + f, c0.y - a), Offset(c0.x + f, c0.y + a), grosor)
            }
            val rl = s / 6f
            listOf(Offset(-1f, -1f), Offset(1f, -1f), Offset(1f, 1f), Offset(-1f, 1f)).forEach { e ->
                drawCircle(color, rl, Offset(c0.x + e.x * (a + rl * 0.3f), c0.y + e.y * (a + rl * 0.3f)), style = Stroke(grosor))
            }
        }
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de las casillas, pintado de rojo hondo.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(ROJO_OSC), cuerpo.topLeft, cuerpo.size)
        // Los divisores caen entre pothi y pothi: todos tienen el mismo ancho y la misma separación.
        val pw = ANCHO_POTHI.dp.toPx()
        val sp = separa.toPx()
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.3f), r.topLeft, Size(r.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.2f), r.topLeft, Size(5 * u, r.height))
            val cuantos = (((r.width - 2 * sp - pw) / (pw + sp)).toInt() + 1).coerceAtLeast(1)
            for (k in 1 until cuantos) {
                val gx = r.left + sp + k * (pw + sp) - sp / 2
                val div = Rect(gx - sp * 0.38f, r.top, gx + sp * 0.38f, r.bottom)
                drawRect(Color.Black.copy(alpha = 0.22f), Offset(div.right, div.top), Size(4 * u, div.height))
                drawRect(c(NARANJA), div.topLeft, div.size)
                drawLine(c(ORO), Offset(div.left + 0.6f * u, div.top), Offset(div.left + 0.6f * u, div.bottom), 0.6f * u)
                drawLine(c(ORO), Offset(div.right - 0.6f * u, div.top), Offset(div.right - 0.6f * u, div.bottom), 0.6f * u)
                drawCircle(c(AZUL), div.width * 0.28f, div.center)
            }
        }
        // Los costados: naranja con un recuadro azul y lunares de oro.
        listOf(d.izquierda, d.derecha).forEach { r ->
            drawRect(c(NARANJA), r.topLeft, r.size)
            val recuadro = Rect(r.left + r.width * 0.2f, r.top + 6 * u, r.right - r.width * 0.2f, r.bottom - 6 * u)
            drawRect(c(AZUL), recuadro.topLeft, recuadro.size)
            drawRect(c(ORO), recuadro.topLeft, recuadro.size, style = Stroke(0.8f * u))
            var yy = recuadro.top + 8 * u
            while (yy < recuadro.bottom - 4 * u) { drawCircle(c(ORO), 1.4f * u, Offset(recuadro.center.x, yy)); yy += 12 * u }
            drawRect(c(ROJO), r.topLeft, Size(r.width, 3 * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
        }
        // Las tablas rojas con la greca del martillo en oro.
        d.tablas.forEach { r ->
            drawRect(c(ROJO), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, 1.6f * u))
            val g = Rect(r.left, r.top + r.height * 0.25f, r.right, r.bottom - r.height * 0.25f)
            val paso = g.height * 1.6f
            val greca = Path().apply {
                moveTo(g.left, g.bottom)
                var x = g.left
                while (x < g.right) {
                    lineTo(x, g.top); lineTo(x + paso * 0.5f, g.top); lineTo(x + paso * 0.5f, g.bottom); lineTo(x + paso, g.bottom)
                    x += paso
                }
            }
            drawPath(greca, c(ORO), style = Stroke(0.9f * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: un cartucho crema de borde azul.
        d.placas.forEach { p ->
            val r = p.r
            drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size, CornerRadius(r.height / 2))
            drawRoundRect(c(AZUL), r.topLeft, r.size, CornerRadius(r.height / 2))
            val dentro = r.deflate(1.4f * u)
            drawRoundRect(c(CREMA), dentro.topLeft, dentro.size, CornerRadius(dentro.height / 2))
            textoEn(medidor, p.texto, Rect(dentro.left + dentro.height * 0.4f, dentro.top, dentro.right - dentro.height * 0.4f, dentro.bottom), letra, c(TINTA), 10.5f, minimo = 5f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo pintado y la alfombra roja con su cenefa.
        val b = d.base
        val piso = b.top + b.height * 0.55f
        drawRect(c(FIELTRO_OSC), Offset(0f, piso), Size(w, h - piso))
        val alfombra = Rect(w * 0.04f, piso + 2 * u, w * 0.96f, h)
        drawRect(c(ALFOMBRA), alfombra.topLeft, alfombra.size)
        drawRect(c(ORO), alfombra.deflate(2.4f * u).topLeft, alfombra.deflate(2.4f * u).size, style = Stroke(1f * u))
        var ax = alfombra.left + 8 * u
        while (ax < alfombra.right - 6 * u) {
            drawPath(estrellaRombo(Offset(ax, alfombra.top + (alfombra.height) * 0.5f), 2.6f * u), c(AZUL))
            ax += 14 * u
        }
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(NARANJA), zocalo.topLeft, zocalo.size)
        drawRect(c(ROJO), zocalo.topLeft, Size(zocalo.width, 3 * u))
        var nx = zocalo.left + 14 * u
        while (nx < zocalo.right - 10 * u) {
            drawArc(c(ROJO), 180f, 180f, false, Offset(nx - 6 * u, zocalo.top + 5 * u), Size(12 * u, 8 * u), style = Stroke(1.2f * u))
            drawCircle(c(ORO), 1.2f * u, Offset(nx, zocalo.top + 9 * u))
            nx += 22 * u
        }
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(zocalo.left, piso), Size(zocalo.width, 2.4f * u))
    }

    /** Un rombo pequeño, para la cenefa de la alfombra. */
    private fun estrellaRombo(c0: Offset, s: Float): Path = Path().apply {
        moveTo(c0.x, c0.y - s); lineTo(c0.x + s, c0.y); lineTo(c0.x, c0.y + s); lineTo(c0.x - s, c0.y); close()
    }

    /** El fieltro, el toono con sus varas, el khana y el copete pintado con el nudo infinito. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // El fieltro blanco, con sus pespuntes.
        drawRect(c(FIELTRO), r.topLeft, r.size)
        var py = r.top + 10 * u
        while (py < r.top + r.height * 0.5f) {
            var px = r.left + 4 * u
            while (px < r.right) { drawLine(c(FIELTRO_OSC), Offset(px, py), Offset(px + 3 * u, py), 0.6f * u); px += 7 * u }
            py += 12 * u
        }
        // El khana: la celosía de listones cruzados de la pared.
        val khana = Rect(r.left, r.top + r.height * 0.46f, r.right, r.top + r.height * 0.62f)
        drawRect(c(FIELTRO_OSC), khana.topLeft, khana.size)
        clipRect(khana.left, khana.top, khana.right, khana.bottom) {
            var kx = khana.left - khana.height
            while (kx < khana.right) {
                drawLine(c(KHANA), Offset(kx, khana.bottom), Offset(kx + khana.height, khana.top), 1.6f * u)
                drawLine(c(KHANA), Offset(kx, khana.top), Offset(kx + khana.height, khana.bottom), 1.6f * u)
                kx += 10 * u
            }
        }
        // Las varas del techo (uni), del toono hasta lo alto del khana.
        val cv = Offset(r.center.x, r.top + r.height * 0.2f)
        val rt = r.height * 0.17f
        for (k in 0..12) {
            val fin = Offset(r.left + r.width * k / 12f, khana.top)
            val dx = fin.x - cv.x; val dy = fin.y - cv.y
            val largo = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
            val ini = Offset(cv.x + dx / largo * rt, cv.y + dy / largo * rt)
            drawLine(c(ROJO_OSC), Offset(ini.x + 0.8f * u, ini.y + 0.8f * u), Offset(fin.x + 0.8f * u, fin.y), 2.6f * u)
            drawLine(c(NARANJA), ini, fin, 2.2f * u)
        }
        // El toono: la abertura del techo con su cruz, el cielo de día y las estrellas de noche.
        val ra = rt * 0.82f
        val abertura = Path().apply { addOval(Rect(cv, ra)) }
        drawPath(abertura, if (noche) CIELO_NOCHE else c(CIELO))
        clipPath(abertura) {
            if (noche) {
                mancha(cv, ra * 1.2f, Color(0xFF3A4A80).copy(alpha = 0.5f))
                for (k in 0 until 14) {
                    val o = Offset(cv.x - ra + 2 * ra * azar(k, 61), cv.y - ra + 2 * ra * azar(k, 62))
                    drawCircle(ESTRELLA.copy(alpha = 0.6f + 0.4f * azar(k, 63)), (0.5f + 0.8f * azar(k, 64)) * u, o)
                }
            } else {
                drawCircle(Color.White.copy(alpha = 0.25f), ra * 0.5f, Offset(cv.x - ra * 0.3f, cv.y - ra * 0.3f))
            }
            drawLine(c(NARANJA), Offset(cv.x - ra, cv.y), Offset(cv.x + ra, cv.y), 2 * u)
            drawLine(c(NARANJA), Offset(cv.x, cv.y - ra), Offset(cv.x, cv.y + ra), 2 * u)
            drawCircle(c(NARANJA), ra * 0.35f, cv, style = Stroke(1.6f * u))
        }
        drawCircle(c(NARANJA), rt, cv, style = Stroke(rt * 0.22f))
        drawCircle(c(ROJO), rt + rt * 0.11f, cv, style = Stroke(0.8f * u))
        drawCircle(c(ROJO), ra, cv, style = Stroke(0.8f * u))
        // El copete pintado del casillero.
        val copete = Rect(r.left, r.top + r.height * 0.6f, r.right, r.bottom)
        drawRect(c(ROJO), copete.topLeft, Size(copete.width, copete.height * 0.16f))
        drawLine(c(ORO), Offset(copete.left, copete.top + copete.height * 0.1f), Offset(copete.right, copete.top + copete.height * 0.1f), 0.8f * u)
        val panel = Rect(copete.left, copete.top + copete.height * 0.16f, copete.right, copete.bottom - copete.height * 0.12f)
        drawRect(c(NARANJA), panel.topLeft, panel.size)
        val centro = Rect(panel.center.x - panel.height * 0.9f, panel.top + 3 * u, panel.center.x + panel.height * 0.9f, panel.bottom - 3 * u)
        drawRect(c(AZUL), centro.topLeft, centro.size)
        drawRect(c(ORO), centro.topLeft, centro.size, style = Stroke(0.8f * u))
        nudo(centro.center, centro.height * 0.5f, c(ORO), 1.3f * u)
        listOf(panel.left + panel.width * 0.18f, panel.right - panel.width * 0.18f).forEach { x ->
            val lateral = Rect(x - panel.width * 0.12f, panel.top + 3 * u, x + panel.width * 0.12f, panel.bottom - 3 * u)
            drawRect(c(VERDE), lateral.topLeft, lateral.size)
            drawRect(c(ORO), lateral.topLeft, lateral.size, style = Stroke(0.8f * u))
            // Una nube de oro en espiral.
            val s = lateral.height * 0.22f
            drawArc(c(ORO), 90f, 270f, false, Offset(x - s * 1.4f, lateral.center.y - s), Size(s * 2, s * 2), style = Stroke(1.1f * u))
            drawArc(c(ORO), 180f, 270f, false, Offset(x - s * 0.6f, lateral.center.y - s), Size(s * 2, s * 2), style = Stroke(1.1f * u))
            drawCircle(c(ORO), s * 0.3f, Offset(x, lateral.center.y + s * 0.2f))
        }
        drawRect(c(ROJO), Offset(copete.left, panel.bottom), Size(copete.width, copete.bottom - panel.bottom))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(copete.left, copete.bottom - 2 * u), Size(copete.width, 2 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val tinte = TELAS[(v.semilla and 0x7fffffff) % TELAS.size]
        val tela = c(if (v.tono != null) mezcla(tinte, v.tono, 0.22f) else tinte)
        val tapa = c(TAPAS[(azar(v.semilla, 4) * TAPAS.size).toInt().coerceIn(0, TAPAS.size - 1)])
        // Las dos puntas descubiertas: las tablas arriba y abajo, las hojas largas en medio.
        val grueso = minOf(h * 0.18f, 4.5f * u)
        val extremo = w * 0.07f
        listOf(0f, w - extremo).forEach { x0 ->
            drawRect(c(HOJAS), Offset(x0, grueso), Size(extremo, h - 2 * grueso))
            var y = grueso + 1.6f * u
            while (y < h - grueso) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(x0, y), Offset(x0 + extremo, y), 0.5f * u); y += 1.8f * u }
            drawRect(tapa, Offset(x0, 0f), Size(extremo, grueso))
            drawRect(tapa, Offset(x0, h - grueso), Size(extremo, grueso))
        }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset.Zero, Size(1.2f * u, h))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(w - 1.2f * u, 0f), Size(1.2f * u, h))
        // El paño amarillo que envuelve el centro, con sus pliegues.
        val pano = Rect(extremo * 0.8f, 0f, w - extremo * 0.8f, h)
        drawRoundRect(tela, pano.topLeft, pano.size, CornerRadius(1.6f * u))
        for (j in 0 until 4) {
            val x = pano.left + pano.width * (0.12f + 0.24f * j + 0.06f * azar(v.semilla, 10 + j))
            drawLine(Color.Black.copy(alpha = 0.1f), Offset(x, 0f), Offset(x + 2 * u, h), 1f * u)
        }
        drawRect(Color.White.copy(alpha = 0.14f), pano.topLeft, Size(pano.width, h * 0.16f))
        drawRect(Color.Black.copy(alpha = 0.16f), Offset(pano.left, h * 0.84f), Size(pano.width, h * 0.16f))
        // La cinta que lo ata: una vuelta más cuanto más escribiste.
        val vueltas = 1 + (v.lleno * 2).toInt()
        for (j in 0 until vueltas) {
            val x = w * 0.64f + j * 2.6f * u
            drawRect(c(CINTA), Offset(x, 0f), Size(1.8f * u, h))
            drawRect(Color.White.copy(alpha = 0.15f), Offset(x, 0f), Size(0.5f * u, h))
        }
        // La etiqueta de tela colgando de arriba, con el día y su sello (de noche, una luna; si el pothi es tan delgado que
        // no cabe en la etiqueta, la luna va sobre la tela, al principio).
        val et = Rect(w * 0.76f, 0f, w * 0.9f, h * 0.92f)
        val punta = minOf(et.width * 0.5f, h * 0.14f)
        val dentro = Rect(et.left + 1.2f * u, et.top + 1.2f * u, et.right - 1.2f * u, et.bottom - punta)
        val rs = minOf(dentro.width * 0.24f, h * 0.08f)
        val conSello = dentro.height > 14 * u
        val lunaFuera = v.noche && !conSello
        if (lunaFuera) {
            val cl = Offset(pano.left + 4 * u, h * 0.5f)
            val rl = minOf(2.2f * u, h * 0.16f)
            drawCircle(LUNA, rl, cl)
            drawCircle(tela, rl * 0.8f, Offset(cl.x + rl * 0.4f, cl.y - rl * 0.2f))
        }
        // El mes, escrito sobre la tela.
        textoEn(medidor, v.mes, Rect(pano.left + (if (lunaFuera) 8f else 2f) * u, h * 0.16f, w * 0.62f, h * 0.84f), letra, c(TINTA), 8f, minimo = 4f)
        val etiqueta = Path().apply {
            moveTo(et.left, et.top); lineTo(et.right, et.top); lineTo(et.right, et.bottom - punta)
            lineTo(et.center.x, et.bottom); lineTo(et.left, et.bottom - punta); close()
        }
        drawPath(etiqueta, Color.Black.copy(alpha = 0.2f))
        drawPath(etiqueta, c(AZUL))
        drawRect(c(CREMA), dentro.topLeft, dentro.size)
        val zonaDia = if (conSello) Rect(dentro.left, dentro.top, dentro.right, dentro.bottom - rs * 2.6f) else dentro
        textoEn(medidor, v.dia, zonaDia, letra, c(TINTA), 11f, peso = FontWeight.Bold, minimo = 5f)
        if (conSello) {
            val cs = Offset(dentro.center.x, dentro.bottom - rs * 1.4f)
            if (v.noche) {
                drawCircle(LUNA, rs, cs)
                drawCircle(c(CREMA), rs * 0.8f, Offset(cs.x + rs * 0.4f, cs.y - rs * 0.2f))
                drawCircle(c(TINTA).copy(alpha = 0.3f), rs, cs, style = Stroke(0.4f * u))
            } else {
                drawRect(c(ROJO), Offset(cs.x - rs, cs.y - rs), Size(rs * 2, rs * 2))
            }
        }
    }
}
