package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.arcoHerradura
import io.github.ruddy2003.vestigium.ui.ideario.arcoMedioPunto
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
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
 * La tapa es ese libro encadenado de frente: cuero gofrado con rombos, bollones y broches de latón, la fecha en oro,
 * la etiqueta de pergamino pautada en rojo para lo escrito y la cuenta dorada en el lomo; al abrirse gira y deja ver la
 * hoja a dos columnas con su capital (de noche, una luna de oro y la luz de la vela).
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
        // La vela en su palmatoria de hierro, a un lado del vano (no bajo la saetera); de noche, encendida.
        val pie = cornisa.top
        val vx = centro.x + 13 * u
        drawOval(c(HIERRO), Offset(vx - 8 * u, pie - 3 * u), Size(16 * u, 3 * u))
        drawCircle(c(HIERRO), 2.6f * u, Offset(vx + 9.5f * u, pie - 2 * u), style = Stroke(1.2f * u))
        drawRect(c(HIERRO), Offset(vx - 1 * u, pie - 7 * u), Size(2 * u, 5 * u))
        drawRect(c(HIERRO), Offset(vx - 5 * u, pie - 8.6f * u), Size(10 * u, 1.8f * u))
        val vela = Rect(vx - 4 * u, pie - 23 * u, vx + 4 * u, pie - 8.6f * u)
        drawRect(if (noche) CERA else c(CERA), vela.topLeft, vela.size)
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(vela.right - 2.2f * u, vela.top), Size(2.2f * u, vela.height))
        // Los chorretones de cera.
        drawRoundRect(if (noche) CERA else c(CERA), Offset(vela.left - 0.8f * u, vela.top + 1.5f * u), Size(1.8f * u, 6 * u), CornerRadius(0.9f * u))
        drawRoundRect(if (noche) CERA else c(CERA), Offset(vela.left + 2.4f * u, vela.top + 1 * u), Size(1.6f * u, 3.5f * u), CornerRadius(0.8f * u))
        drawLine(c(HIERRO), Offset(vela.center.x, vela.top), Offset(vela.center.x, vela.top - 2.6f * u), 1f * u)
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.29f, 0.35f, 0.81f, 0.7f)
    override val letraTapa: FontFamily = LetraExtra.gotica
    override val tamTapa = 14f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** El pergamino de la etiqueta y de las hojas: se hunde menos que el cuero, para que el extracto se lea siempre. */
    private fun pergamino(oscuro: Boolean, noche: Boolean): Color {
        var r = PERGAMINO
        if (oscuro) r = mezcla(r, Color(0xFF100C08), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    /** Un renglón de letra en miniatura, de [x0] a [x1]: las palabras como trazos de largo distinto. */
    private fun DrawScope.renglon(x0: Float, x1: Float, y: Float, semilla: Int, color: Color, grosor: Float = 1.4f) {
        val u = density
        var x = x0; var k = 0
        while (x < x1 - 2 * u) {
            val largo = minOf((4f + 10f * azar(semilla, k)) * u, x1 - x)
            drawLine(color, Offset(x, y), Offset(x + largo, y), grosor * u)
            x += largo + 2.4f * u; k++
        }
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val cuero = c(CUEROS[(e.semilla and 0x7fffffff) % CUEROS.size])
        val oro = mezcla(c(ORO), ORO, 0.35f)
        val hoja = pergamino(oscuro, n)
        val libro = Rect(w * 0.025f, h * 0.045f, w * 0.965f, h * 0.9f)
        val lomo = Rect(libro.left, libro.top, libro.left + minOf(w * 0.075f, 30 * u), libro.bottom)
        val tapa = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        // La sombra sobre el atril y el canto de las hojas, que asoma por abajo y por el corte.
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(libro.left + 3 * u, libro.top + 5 * u), libro.size, CornerRadius(4 * u))
        val bloque = Rect(tapa.left, tapa.top + 3 * u, tapa.right + 2.5f * u, tapa.bottom + 3.5f * u)
        drawRect(c(PERGAMINO), bloque.topLeft, bloque.size)
        var cy = bloque.top + 2 * u
        while (cy < bloque.bottom - 1 * u) { drawLine(c(TINTA).copy(alpha = 0.16f), Offset(tapa.right, cy), Offset(bloque.right, cy), 0.5f * u); cy += 1.5f * u }
        for (j in 1..3) drawLine(c(TINTA).copy(alpha = 0.18f), Offset(tapa.left, tapa.bottom + j * 0.9f * u), Offset(bloque.right, tapa.bottom + j * 0.9f * u), 0.5f * u)
        // La primera hoja, que se ve al abrir: dos columnas pautadas en rojo, renglones de letra gótica y una capital pintada.
        if (a > 0.01f) {
            val pag = Rect(tapa.left + 1 * u, tapa.top + 3 * u, tapa.right - 1 * u, tapa.bottom - 1 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            listOf(10f to 0.14f, 5f to 0.12f).forEach { (an, al) -> drawRect(Color.Black.copy(alpha = al), pag.topLeft, Size(an * u, pag.height)) }
            val m = Rect(pag.left + 18 * u, pag.top + 12 * u, pag.right - 14 * u, pag.bottom - 12 * u)
            val col = (m.width - 12 * u) / 2
            for (k in 0..1) {
                val x0 = m.left + k * (col + 12 * u)
                listOf(x0, x0 + col).forEach { x -> drawLine(c(BERMELLON).copy(alpha = 0.4f), Offset(x, m.top), Offset(x, m.bottom), 0.5f * u) }
                var ry = m.top + 4 * u; var j = 0
                while (ry < m.bottom) {
                    val sangria = if (k == 0 && j < 3) 17 * u else 2 * u
                    renglon(x0 + sangria, x0 + col * (if (j % 6 == 5) 0.6f else 1f) - 2 * u, ry, e.semilla + k * 50 + j, c(TINTA).copy(alpha = 0.6f))
                    if (j == 7) drawCircle(c(BERMELLON), 1.6f * u, Offset(x0 + 4 * u, ry))
                    ry += 6 * u; j++
                }
            }
            val capital = Rect(m.left + 1 * u, m.top, m.left + 15 * u, m.top + 15 * u)
            drawRect(c(LAPIZ), capital.topLeft, capital.size)
            drawRect(oro, capital.topLeft, capital.size, style = Stroke(0.8f * u))
            textoEn(medidor, "D", capital, letraTapa, hoja, 13f)
        }
        // La tapa de cuero sobre tablas, que gira sobre el lomo al abrirse.
        val sx = 1f - 0.9f * a
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(cuero, Color.White, 0.12f), cuero, mezcla(cuero, Color.Black, 0.32f)), tapa.top, tapa.bottom),
                tapa.topLeft, tapa.size, CornerRadius(3 * u))
            for (j in 0 until 8) {
                val o = Offset(tapa.left + tapa.width * azar(e.semilla, 20 + j), tapa.top + tapa.height * azar(e.semilla, 30 + j))
                drawOval(Color.Black.copy(alpha = 0.07f), Offset(o.x - 7 * u, o.y - 3 * u), Size(14 * u, 6 * u))
            }
            drawRect(Color.White.copy(alpha = 0.08f), tapa.topLeft, Size(tapa.width, 2 * u))
            val r1 = tapa.deflate(7 * u)
            val r2 = r1.deflate(11 * u)
            // Dentro del recuadro, la red de rombos gofrada en seco.
            clipRect(r2.left, r2.top, r2.right, r2.bottom) {
                var k = r2.left - r2.height
                while (k < r2.right) {
                    drawLine(Color.Black.copy(alpha = 0.22f), Offset(k, r2.bottom), Offset(k + r2.height, r2.top), 0.9f * u)
                    drawLine(Color.Black.copy(alpha = 0.22f), Offset(k, r2.top), Offset(k + r2.height, r2.bottom), 0.9f * u)
                    k += 16 * u
                }
            }
            // Los dos recuadros de filetes dorados y, en la banda de en medio, hierros en rombo.
            listOf(r1, r2).forEach { r -> drawRect(oro.copy(alpha = 0.75f), r.topLeft, r.size, style = Stroke(1.1f * u)) }
            val banda = r1.deflate(5.5f * u)
            fun rombo(o: Offset) {
                drawPath(Path().apply { moveTo(o.x, o.y - 2.4f * u); lineTo(o.x + 2.4f * u, o.y); lineTo(o.x, o.y + 2.4f * u); lineTo(o.x - 2.4f * u, o.y); close() }, Color.Black.copy(alpha = 0.32f))
                drawCircle(oro.copy(alpha = 0.6f), 0.7f * u, o)
            }
            var bx = banda.left + 12 * u
            while (bx < banda.right - 8 * u) { rombo(Offset(bx, banda.top)); rombo(Offset(bx, banda.bottom)); bx += 12 * u }
            var by = banda.top + 12 * u
            while (by < banda.bottom - 8 * u) { rombo(Offset(banda.left, by)); rombo(Offset(banda.right, by)); by += 12 * u }
            // El compartimento del título, liso, con la fecha en oro.
            val etiqueta = Rect(w * 0.27f, h * 0.31f, w * 0.83f, h * 0.735f)
            val titulo = Rect(r2.left + 5 * u, r2.top + 3 * u, r2.right - 5 * u, etiqueta.top - 4 * u)
            drawRect(mezcla(cuero, Color.Black, 0.22f), titulo.topLeft, titulo.size)
            drawRect(oro.copy(alpha = 0.7f), titulo.topLeft, titulo.size, style = Stroke(0.7f * u))
            textoEn(medidor, e.fecha, titulo.deflate(2 * u), letra, oro, 13f, peso = FontWeight.Bold, minimo = 7f)
            // La etiqueta de pergamino, pautada en rojo y clavada con cuatro clavillos: ahí va lo escrito.
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(etiqueta.left + 1.5f * u, etiqueta.top + 2 * u), etiqueta.size)
            drawRect(hoja, etiqueta.topLeft, etiqueta.size)
            val pauta = etiqueta.deflate(3 * u)
            drawRect(c(BERMELLON).copy(alpha = 0.55f), pauta.topLeft, pauta.size, style = Stroke(0.6f * u))
            drawRect(Color.Black.copy(alpha = 0.35f), etiqueta.topLeft, etiqueta.size, style = Stroke(0.8f * u))
            listOf(pauta.topLeft, pauta.topRight, pauta.bottomLeft, pauta.bottomRight).forEach { p -> drawCircle(c(HIERRO), 1.3f * u, p) }
            // Los bollones de latón de las esquinas.
            fun bollon(o: Offset, r: Float) {
                drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(o.x + 1.2f * u, o.y + 1.6f * u))
                drawCircle(c(LATON), r, o)
                drawCircle(mezcla(c(LATON), Color.Black, 0.3f), r * 0.62f, o, style = Stroke(0.8f * u))
                drawCircle(Color.White.copy(alpha = 0.4f), r * 0.3f, Offset(o.x - r * 0.3f, o.y - r * 0.3f))
            }
            listOf(r1.topLeft, r1.topRight, r1.bottomLeft, r1.bottomRight).forEach { bollon(it, 6.5f * u) }
            // Abajo, en medio, el bollón del centro; de noche, una luna de oro.
            val cb = Offset(etiqueta.center.x, (etiqueta.bottom + r2.bottom) / 2)
            val rb = minOf(5 * u, (r2.bottom - etiqueta.bottom) * 0.42f)
            if (n) {
                drawCircle(oro, rb * 1.15f, cb)
                drawCircle(mezcla(cuero, Color.Black, 0.25f), rb, Offset(cb.x + rb * 0.5f, cb.y - rb * 0.35f))
                listOf(-1f, 1f).forEach { s -> drawPath(estrella(Offset(cb.x + s * 18 * u, cb.y), 2.6f * u, 4, 0.4f), oro) }
            } else {
                bollon(cb, rb)
                listOf(-1f, 1f).forEach { s -> drawCircle(oro.copy(alpha = 0.6f), 1.2f * u, Offset(cb.x + s * 14 * u, cb.y)) }
            }
            // Los dos broches del canto, con su chapa de latón y el gancho.
            listOf(0.3f, 0.7f).forEach { f ->
                val y = tapa.top + tapa.height * f
                val chapa = Rect(tapa.right - 15 * u, y - 6 * u, tapa.right + 1.5f * u, y + 6 * u)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(chapa.left + 1.2f * u, chapa.top + 1.5f * u), chapa.size)
                drawRect(c(LATON), chapa.topLeft, chapa.size)
                val filo = chapa.deflate(2 * u)
                drawRect(mezcla(c(LATON), Color.Black, 0.35f), filo.topLeft, filo.size, style = Stroke(0.7f * u))
                drawCircle(c(HIERRO), 1.3f * u, Offset(chapa.left + 3.6f * u, y))
                drawLine(c(HIERRO), Offset(chapa.left + 6 * u, y), Offset(chapa.right, y), 1.6f * u)
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.4f * a), tapa.topLeft, tapa.size)
        }
        // La sombra que la tapa echa sobre la hoja mientras gira.
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (k in 0 until 3) drawRect(Color.Black.copy(alpha = 0.12f * (3 - k) / 3f), Offset(borde, tapa.top + 3 * u), Size((4 + 4 * k) * u, tapa.height - 4 * u))
        }
        // El lomo con sus nervios y la cuenta de palabras dorada entre ellos.
        drawRoundRect(mezcla(cuero, Color.Black, 0.18f), lomo.topLeft, lomo.size, CornerRadius(3 * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.18f, lomo.height))
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(lomo.right - 2 * u, lomo.top), Size(2 * u, lomo.height))
        listOf(0.1f, 0.27f, 0.73f, 0.9f).forEach { f ->
            val y = lomo.top + lomo.height * f
            drawRect(Color.White.copy(alpha = 0.16f), Offset(lomo.left, y - 2.2f * u), Size(lomo.width - 2 * u, 1.6f * u))
            drawRect(Color.Black.copy(alpha = 0.34f), Offset(lomo.left, y - 0.6f * u), Size(lomo.width - 2 * u, 1.8f * u))
            drawLine(oro.copy(alpha = 0.55f), Offset(lomo.left + 2 * u, y + 2.6f * u), Offset(lomo.right - 4 * u, y + 2.6f * u), 0.6f * u)
        }
        if (!e.vacio && e.palabras > 0) {
            textoEn(medidor, palabrasTapa(e.palabras), Rect(lomo.left + 1 * u, lomo.top + lomo.height * 0.31f, lomo.right - 3 * u, lomo.top + lomo.height * 0.69f),
                letra, oro, 10f, vertical = true, peso = FontWeight.Bold, minimo = 6f)
        }
        // La cadena: de la grapa al pie del lomo, hacia la barra del atril.
        val grapa = Offset(lomo.center.x, lomo.bottom - 3 * u)
        drawRect(c(HIERRO), Offset(grapa.x - 3.5f * u, grapa.y - 2 * u), Size(7 * u, 3.4f * u))
        drawRect(c(HIERRO_LUZ), Offset(grapa.x - 3.5f * u, grapa.y - 2 * u), Size(7 * u, 0.8f * u))
        var ly = grapa.y + 1.2f * u; var j = 0
        while (ly < h) {
            if (j % 2 == 0) drawOval(c(HIERRO_LUZ), Offset(grapa.x - 1.7f * u, ly), Size(3.4f * u, 4.8f * u), style = Stroke(1.1f * u))
            else drawLine(c(HIERRO_LUZ), Offset(grapa.x, ly), Offset(grapa.x, ly + 4.8f * u), 1.3f * u)
            ly += 3.8f * u; j++
        }
        // De noche, la luz de la vela llega desde un lado.
        if (n) clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.04f, h * 0.02f), h * 1.1f, LLAMA.copy(alpha = 0.2f)) }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 118.dp

    /** Una hojita de hiedra de la filigrana, de [s] de largo, que sale de [o] en el ángulo [giro] (grados). */
    private fun DrawScope.hiedra(o: Offset, s: Float, giro: Float, color: Color) {
        rotate(giro, o) {
            drawPath(Path().apply {
                moveTo(o.x, o.y)
                cubicTo(o.x + s * 0.3f, o.y - s * 0.45f, o.x + s * 0.8f, o.y - s * 0.32f, o.x + s, o.y)
                cubicTo(o.x + s * 0.8f, o.y + s * 0.32f, o.x + s * 0.3f, o.y + s * 0.45f, o.x, o.y)
                close()
            }, color)
        }
    }

    /**
     * El comienzo del folio, como en un códice: la cartela de doble filete, rojo afuera y azul adentro, con una hojita de
     * hiedra en cada esquina; a la izquierda, la inicial iluminada (un cuadro de lapislázuli con su filete de oro, la flor
     * roja de la rúbrica y sus puntos de plomo blanco; de noche, la media luna de oro y dos estrellas); a su derecha,
     * «Aquí comiença el folio …» en rojo, la fecha en letra gótica (si es larga, en dos renglones) y el detalle en tinta
     * tenue, sobre un renglón de filigrana roja y azul.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val rojo = p.acento; val azul = p.detalle
        val marco = Rect(1.5f * u, 4f * u, w - 1.5f * u, h - 3f * u)
        drawRect(rojo.copy(alpha = 0.75f), marco.topLeft, marco.size, style = Stroke(1.4f * u))
        drawRect(azul.copy(alpha = 0.55f), Offset(marco.left + 3f * u, marco.top + 3f * u), Size(marco.width - 6f * u, marco.height - 6f * u), style = Stroke(0.8f * u))
        listOf(marco.topLeft to 45f, Offset(marco.right, marco.top) to 135f, Offset(marco.right, marco.bottom) to 225f, Offset(marco.left, marco.bottom) to 315f)
            .forEachIndexed { i, (o, g) -> hiedra(o, 8f * u, g, (if (i % 2 == 0) azul else rojo).copy(alpha = 0.85f)) }
        // La inicial iluminada.
        val lado = 62f * u
        val ini = Rect(12f * u, (h - lado) / 2, 12f * u + lado, (h + lado) / 2)
        drawRect(Color.Black.copy(alpha = 0.18f), ini.topLeft + Offset(1.2f * u, 1.6f * u), ini.size)
        drawRect(c(LAPIZ), ini.topLeft, ini.size)
        drawRect(c(ORO), ini.topLeft, ini.size, style = Stroke(1.8f * u))
        drawRect(c(BERMELLON).copy(alpha = 0.8f), ini.topLeft + Offset(4f * u, 4f * u), Size(lado - 8f * u, lado - 8f * u), style = Stroke(0.7f * u))
        val o = ini.center
        if (!e.noche) {
            drawPath(estrella(o, lado * 0.3f, 6, 0.42f), c(BERMELLON))
            drawCircle(c(ORO), lado * 0.08f, o)
            for (k in 0 until 6) {
                val a = Math.toRadians(k * 60.0)
                drawCircle(c(CERA).copy(alpha = 0.7f), 0.9f * u, Offset(o.x + lado * 0.36f * kotlin.math.cos(a).toFloat(), o.y + lado * 0.36f * kotlin.math.sin(a).toFloat()))
            }
        } else {
            drawPath(mediaLunaMedievo(Offset(o.x - lado * 0.06f, o.y + lado * 0.02f), lado * 0.25f), mezcla(ORO, LLAMA, 0.3f))
            drawPath(estrella(Offset(o.x + lado * 0.24f, o.y - lado * 0.22f), lado * 0.08f, 5, 0.45f), mezcla(ORO, LLAMA, 0.3f))
            drawPath(estrella(Offset(o.x + lado * 0.2f, o.y + lado * 0.26f), lado * 0.05f, 5, 0.45f), mezcla(ORO, LLAMA, 0.3f))
        }
        listOf(ini.topLeft, Offset(ini.right, ini.top), Offset(ini.right, ini.bottom), Offset(ini.left, ini.bottom)).forEach { drawCircle(c(ORO), 1.6f * u, it) }
        // El rótulo, la fecha y el detalle.
        val x0 = ini.right + 8f * u; val x1 = marco.right - 8f * u
        textoEn(medidor, e.rotulo, Rect(x0, 10f * u, x1, 30f * u), LetraExtra.gotica, rotuloMedievo(p, e.noche), 15f, minimo = 8f)
        fechaMedievo(medidor, e.fecha, Rect(x0, 31f * u, x1, 74f * u), LetraExtra.gotica, p.tinta, 22f)
        textoEn(medidor, e.detalle, Rect(x0, 75f * u, x1, 92f * u), letra, p.tenue, 11.5f, minimo = 7f)
        // El renglón de filigrana que sale de la inicial.
        val yf = h - 14f * u
        drawLine(rojo.copy(alpha = 0.5f), Offset(ini.right + 3f * u, yf), Offset(x1 - 4f * u, yf), 0.8f * u)
        var x = ini.right + 10f * u; var k = 0
        while (x < x1 - 8f * u) { drawCircle((if (k % 2 == 0) azul else rojo).copy(alpha = 0.7f), 1.1f * u, Offset(x, yf)); x += 12f * u; k++ }
        hiedra(Offset(x1 - 4f * u, yf), 7f * u, 0f, azul.copy(alpha = 0.8f))
    }

    /**
     * La pauta del códice: las dos reglas del margen interior y la del exterior, las de cabeza y pie, los picados de la
     * orilla por donde se pasó la punta seca, la vara de filigrana roja y azul del margen con sus hojitas de hiedra y, al
     * pie, el recuadro del reclamo (la palabra con que empieza el cuaderno siguiente). En modo oscuro, la vela en su
     * palmatoria, abajo a la izquierda: el pergamino se hunde en la penumbra y la llama aclara su rincón.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val rojo = p.acento; val azul = p.detalle
        listOf(22f * u, 27f * u, w - 22f * u).forEach { x -> drawLine(rojo.copy(alpha = 0.2f), Offset(x, 0f), Offset(x, h), 1f * u) }
        listOf(66f * u, h - 46f * u).forEach { y -> drawLine(p.tenue.copy(alpha = 0.16f), Offset(0f, y), Offset(w, y), 0.8f * u) }
        var y = 20f * u
        while (y < h - 10f * u) { drawCircle(p.tenue.copy(alpha = 0.24f), 0.9f * u, Offset(w - 9f * u, y)); y += 14f * u }
        // La vara de filigrana: tramos rojos y azules, con una hojita de hiedra en cada junta.
        val y1 = h - 70f * u
        var yy = 90f * u; var k = 0
        while (yy < y1) {
            val fin = minOf(yy + 46f * u, y1)
            drawLine((if (k % 2 == 0) rojo else azul).copy(alpha = 0.32f), Offset(24.5f * u, yy), Offset(24.5f * u, fin), 2.2f * u)
            hiedra(Offset(23.5f * u, fin), 6.5f * u, if (k % 2 == 0) 200f else 160f, (if (k % 2 == 0) azul else rojo).copy(alpha = 0.38f))
            drawCircle(rojo.copy(alpha = 0.3f), 1.2f * u, Offset(24.5f * u, yy + 23f * u))
            yy = fin; k++
        }
        // El reclamo, al pie a la derecha.
        val rec = Rect(w - 84f * u, h - 36f * u, w - 32f * u, h - 20f * u)
        drawRect(p.tenue.copy(alpha = 0.3f), rec.topLeft, rec.size, style = Stroke(0.8f * u))
        for (j in 0..2) drawLine(p.tinta.copy(alpha = 0.22f), Offset(rec.left + (6f + j * 15f) * u, rec.center.y), Offset(rec.left + (17f + j * 15f) * u, rec.center.y), 1.2f * u)
        if (oscuro && !p.oscura) {
            val plato = Offset(15f * u, h - 58f * u)
            val llama = Offset(plato.x, plato.y - 40f * u)
            penumbraMedievo(llama - Offset(0f, 6f * u), LLAMA)
            haloMedievo(llama - Offset(0f, 6f * u), 36f * u, LLAMA, 0.6f)
            val laton = t(LATON, true, false)
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(plato.x - 12f * u, plato.y), Size(24f * u, 6f * u))
            drawOval(laton, Offset(plato.x - 11f * u, plato.y - 2f * u), Size(22f * u, 6f * u))
            drawArc(laton, -90f, 180f, false, Offset(plato.x + 8f * u, plato.y - 6f * u), Size(8f * u, 7f * u), style = Stroke(1.4f * u))
            drawRect(mezcla(CERA, Color.Black, 0.12f), Offset(plato.x - 3.5f * u, llama.y + 2f * u), Size(7f * u, plato.y - llama.y - 2f * u))
            drawCircle(mezcla(CERA, Color.Black, 0.05f), 1.6f * u, Offset(plato.x + 3f * u, llama.y + 6f * u))
            drawLine(TINTA, Offset(llama.x, llama.y + 2f * u), Offset(llama.x, llama.y - 2f * u), 0.8f * u)
            llamaMedievo(llama + Offset(0f, 1f * u), 13f * u, LLAMA)
        }
    }

    /** La barra: una franja de pergamino algo más tostado, con los picados de la pauta arriba y el doble filete rojo y azul al pie. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(p.color, p.tenue, 0.14f).let { if (oscuro) mezcla(it, Color.Black, 0.24f) else it }
        drawRect(base.copy(alpha = 0.95f))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, h))
        drawLine(p.acento.copy(alpha = 0.7f), Offset(0f, h - 3.5f * u), Offset(w, h - 3.5f * u), 1.4f * u)
        drawLine(p.detalle.copy(alpha = 0.55f), Offset(0f, h - 1f * u), Offset(w, h - 1f * u), 0.8f * u)
        var x = 6f * u
        while (x < w) { drawCircle(p.tenue.copy(alpha = 0.3f), 0.8f * u, Offset(x, 3f * u)); x += 14f * u }
    }

    /** El botón de rubricar: una filacteria de bermellón con las puntas en cola de golondrina, su filete de oro y dos puntos. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = 6f * u
        fun cinta(dx: Float, dy: Float) = Path().apply {
            moveTo(1f * u + dx, 3f * u + dy); lineTo(w - 1f * u + dx, 3f * u + dy); lineTo(w - 1f * u - m + dx, h / 2 + dy)
            lineTo(w - 1f * u + dx, h - 3f * u + dy); lineTo(1f * u + dx, h - 3f * u + dy); lineTo(1f * u + m + dx, h / 2 + dy); close()
        }
        val rojo = t(BERMELLON, oscuro && !p.oscura, noche)
        val oro = t(ORO, oscuro && !p.oscura, noche)
        drawPath(cinta(1f * u, 1.6f * u), Color.Black.copy(alpha = 0.25f))
        drawPath(cinta(0f, 0f), Brush.verticalGradient(listOf(mezcla(rojo, Color.White, 0.12f), mezcla(rojo, Color.Black, 0.18f)), 0f, h))
        drawPath(cinta(0f, 0f), oro.copy(alpha = 0.9f), style = Stroke(1f * u))
        listOf(1f * u + m + 4f * u, w - 1f * u - m - 4f * u).forEach { drawCircle(oro, 1.3f * u, Offset(it, h / 2)) }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = CERA

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 266.dp

    /** La cal del muro del scriptorium, el cuerno de la tinta, la piel alumbrada del lomo y la piedra pómez. */
    private val CAL = Color(0xFFE6DCC6)
    private val CUERNO = Color(0xFFD8C69A)
    private val PIEL_LOMO = Color(0xFF8A4A2C)
    private val POMEZ = Color(0xFFBDB6A6)

    /** El grosor del códice, en dp: de 4 con una hoja a 18 con 240 o más (crece rápido al principio y luego despacio). */
    private fun grosorCodice(hojas: Int): Float = 4f + 14f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /**
     * El códice de momentos sobre el atril del copista, contra el muro encalado del scriptorium pintado con un falso
     * despiece rojo y su ventanuco: el atril de roble con su tablero inclinado, el listón donde descansa el libro, el faldón
     * con arquillos y el pie en cruz. En el larguero derecho, los dos cuernos de tinta (negra y roja) con la pluma; en el
     * listón, el cortaplumas y la piedra pómez; en la pared, la vela en su gancho de hierro. El códice va en tablas de roble
     * desnudas con media piel en el lomo, la correa con su broche de latón y la etiqueta de vitela bajo una lámina de cuerno
     * con el nombre y la cuenta; engorda con las hojas (el canto y la cabeza, cada vez más gruesos), le asoman más cédulas
     * por arriba y más registros por abajo. Al abrirse, la tapa gira sobre el lomo y deja ver la primera hoja a dos columnas.
     * En oscuro, la vela encendida alumbra la escena desde la izquierda.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        // En oscuro la escena se hunde de verdad (más que el mueble): solo la vela la alumbra.
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0E0A07), 0.58f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorCodice(e.hojas) * u
        val hoja = pergamino(oscuro, false)
        // El muro encalado con su falso despiece de sillares pintado en almagre.
        drawRect(c(CAL))
        val hilada = 24 * u; val pieza = 52 * u
        var y = 6 * u; var k = 0
        while (y < h * 0.62f) {
            drawLine(c(BERMELLON).copy(alpha = 0.3f), Offset(0f, y), Offset(w, y), 0.8f * u)
            drawLine(c(BERMELLON).copy(alpha = 0.18f), Offset(0f, y + 2 * u), Offset(w, y + 2 * u), 0.6f * u)
            var x = if (k % 2 == 0) 0f else pieza / 2
            while (x < w) { drawLine(c(BERMELLON).copy(alpha = 0.3f), Offset(x, y), Offset(x, y + hilada), 0.8f * u); x += pieza }
            y += hilada; k++
        }
        // El zócalo pintado: una banda de ocre con su filete rojo.
        drawRect(c(Color(0xFFB98A4E)), Offset(0f, h * 0.62f), Size(w, h * 0.3f))
        drawRect(c(BERMELLON), Offset(0f, h * 0.62f), Size(w, 2.4f * u))
        for (j in 0 until 9) mancha(Offset(w * azar(e.semilla, 60 + j), h * (0.66f + 0.22f * azar(e.semilla, 70 + j))), 9 * u, Color.Black.copy(alpha = 0.05f))
        // El piso de baldosas de barro.
        val piso = h * 0.92f
        drawRect(c(BALDOSA), Offset(0f, piso), Size(w, h - piso))
        var bx = 0f; var bk = 0
        while (bx < w) { if (bk % 2 == 0) drawRect(Color.Black.copy(alpha = 0.18f), Offset(bx, piso), Size(18 * u, h - piso)); bx += 18 * u; bk++ }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, piso), Size(w, 2 * u))
        // El ventanuco de medio punto, a la derecha: el derrame del muro y el día (o el anochecer, en oscuro).
        val vano = Rect(w * 0.885f - 11 * u, h * 0.08f, w * 0.885f + 11 * u, h * 0.36f)
        drawPath(arcoMedioPunto(vano.inflate(6 * u)), c(Color(0xFFCFC3AA)))
        drawPath(arcoMedioPunto(vano), if (oscuro) Color(0xFF1C2238) else DIA)
        if (oscuro) drawCircle(Color(0xFFEDE6C8).copy(alpha = 0.8f), 1.2f * u, Offset(vano.center.x + 3 * u, vano.top + 14 * u))
        drawLine(c(HIERRO), Offset(vano.center.x, vano.top + 4 * u), Offset(vano.center.x, vano.bottom), 1.4f * u)
        drawLine(c(HIERRO), Offset(vano.left, vano.center.y + 6 * u), Offset(vano.right, vano.center.y + 6 * u), 1.4f * u)
        // La vela en su gancho de hierro, a la izquierda, sobre el muro.
        val gancho = Offset(w * 0.075f, h * 0.36f)
        drawLine(c(HIERRO), Offset(gancho.x - 9 * u, gancho.y - 20 * u), Offset(gancho.x - 9 * u, gancho.y + 8 * u), 2.2f * u)
        drawLine(c(HIERRO), Offset(gancho.x - 9 * u, gancho.y + 4 * u), Offset(gancho.x, gancho.y), 1.6f * u)
        drawOval(c(HIERRO), Offset(gancho.x - 7 * u, gancho.y - 2 * u), Size(14 * u, 4 * u))
        val vela = Rect(gancho.x - 3.2f * u, gancho.y - 26 * u, gancho.x + 3.2f * u, gancho.y - 1 * u)
        drawRect(if (oscuro) mezcla(CERA, Color.Black, 0.12f) else CERA, vela.topLeft, vela.size)
        drawRect(Color.Black.copy(alpha = 0.14f), Offset(vela.right - 2 * u, vela.top), Size(2 * u, vela.height))
        drawRoundRect(if (oscuro) mezcla(CERA, Color.Black, 0.12f) else CERA, Offset(vela.left - 0.8f * u, vela.top + 2 * u), Size(1.6f * u, 7 * u), CornerRadius(0.8f * u))
        drawLine(TINTA, Offset(vela.center.x, vela.top), Offset(vela.center.x, vela.top - 2.4f * u), 0.9f * u)
        val llama = Offset(vela.center.x, vela.top - 1.6f * u)
        if (oscuro) {
            // La luz propia de la escena: la vela encendida y su resplandor sobre la cal (el único degradado).
            drawRect(Brush.radialGradient(0f to LLAMA.copy(alpha = 0.3f), 0.35f to LLAMA.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.85f))
            haloMedievo(llama - Offset(0f, 5 * u), 26 * u, LLAMA, 0.7f)
            llamaMedievo(llama, 11 * u, LLAMA)
        }

        // ------------------------------------------------ el atril del copista
        // El tablero inclinado: se ve más angosto arriba, porque se aleja.
        val tablero = Rect(w * 0.14f, h * 0.07f, w * 0.86f, h * 0.76f)
        val inclinado = Path().apply {
            moveTo(tablero.left + w * 0.05f, tablero.top); lineTo(tablero.right - w * 0.05f, tablero.top)
            lineTo(tablero.right, tablero.bottom); lineTo(tablero.left, tablero.bottom); close()
        }
        drawPath(inclinado, Color.Black.copy(alpha = 0.22f), style = Stroke(6 * u))
        drawPath(inclinado, c(ROBLE_OSC))
        clipPath(inclinado) { vetas(tablero, c(VETA).copy(alpha = 0.45f), 7, densidad = 0.5f, vertical = true, grosor = 0.7f) }
        // Los largueros (que siguen la pendiente) y el travesaño de arriba, en roble más claro.
        val larguero = 9 * u
        listOf(-1f, 1f).forEach { s0 ->
            val x0 = if (s0 < 0) tablero.left else tablero.right
            val x1 = if (s0 < 0) tablero.left + w * 0.05f else tablero.right - w * 0.05f
            val p = Path().apply {
                moveTo(x1, tablero.top); lineTo(x1 - s0 * larguero, tablero.top)
                lineTo(x0 - s0 * larguero, tablero.bottom); lineTo(x0, tablero.bottom); close()
            }
            drawPath(p, c(ROBLE))
            drawPath(p, Color.Black.copy(alpha = 0.3f), style = Stroke(1 * u))
        }
        val travesano = Rect(tablero.left + w * 0.05f, tablero.top, tablero.right - w * 0.05f, tablero.top + 7 * u)
        drawRect(c(ROBLE), travesano.topLeft, travesano.size)
        bisel(travesano, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.28f), 1.2f * u)
        listOf(travesano.left + 4 * u, travesano.right - 4 * u).forEach { drawCircle(c(ROBLE_OSC), 1.4f * u, Offset(it, travesano.center.y)) }
        // El faldón con su arquería ciega y el pie en cruz.
        val falda = Rect(w * 0.19f, h * 0.8f, w * 0.81f, h * 0.87f)
        drawRect(c(ROBLE), falda.topLeft, falda.size)
        var ax = falda.left + 6 * u
        while (ax < falda.right - 14 * u) {
            drawPath(arcoMedioPunto(Rect(ax, falda.top + 3 * u, ax + 10 * u, falda.bottom - 2 * u)), c(ROBLE_OSC))
            ax += 15 * u
        }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(falda.left, falda.bottom - 1.5f * u), Size(falda.width, 1.5f * u))
        val poste = Rect(w * 0.5f - 9 * u, falda.bottom, w * 0.5f + 9 * u, piso + 2 * u)
        drawRect(c(ROBLE), poste.topLeft, poste.size)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(poste.right - 4 * u, poste.top), Size(4 * u, poste.height))
        drawRoundRect(c(ROBLE_OSC), Offset(w * 0.3f, piso - 3 * u), Size(w * 0.4f, 7 * u), CornerRadius(3.5f * u))
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(w * 0.28f, piso + 3 * u), Size(w * 0.44f, 5 * u))

        // ------------------------------------------------ los dos cuernos de tinta en el larguero derecho, con la pluma
        fun cuerno(boca: Offset, largo: Float, ancho: Float, filo: Color) {
            val p = Path().apply {
                moveTo(boca.x - ancho / 2, boca.y)
                cubicTo(boca.x - ancho * 0.5f, boca.y + largo * 0.5f, boca.x + ancho * 0.2f, boca.y + largo * 0.85f, boca.x + ancho * 0.9f, boca.y + largo)
                cubicTo(boca.x + ancho * 0.4f, boca.y + largo * 0.7f, boca.x + ancho * 0.5f, boca.y + largo * 0.4f, boca.x + ancho / 2, boca.y)
                close()
            }
            drawPath(p, c(CUERNO))
            clipPath(p) {
                for (j in 1..4) drawLine(c(Color(0xFF8E7A52)).copy(alpha = 0.5f), Offset(boca.x - ancho, boca.y + largo * j * 0.18f), Offset(boca.x + ancho, boca.y + largo * j * 0.18f + 2 * u), 0.8f * u)
                drawRect(Color.Black.copy(alpha = 0.18f), Offset(boca.x + ancho * 0.1f, boca.y), Size(ancho, largo))
            }
            drawOval(c(TINTA), Offset(boca.x - ancho / 2, boca.y - 2 * u), Size(ancho, 4 * u))
            drawOval(filo, Offset(boca.x - ancho / 2, boca.y - 2 * u), Size(ancho, 4 * u), style = Stroke(1.2f * u))
        }
        val xc = tablero.right - larguero / 2 - w * 0.012f
        drawRect(c(HIERRO), Offset(xc - 8 * u, h * 0.42f - 1 * u), Size(16 * u, 3 * u))
        cuerno(Offset(xc, h * 0.42f), 34 * u, 12 * u, c(LATON))
        drawRect(c(HIERRO), Offset(xc - 6 * u, h * 0.58f - 1 * u), Size(12 * u, 3 * u))
        cuerno(Offset(xc + 1 * u, h * 0.58f), 24 * u, 9 * u, c(BERMELLON))
        // La pluma de oca, metida en el cuerno de la tinta negra.
        val canon = Offset(xc - 1 * u, h * 0.42f)
        val punta = Offset(xc + 22 * u, h * 0.06f)
        val pluma = Path().apply {
            moveTo(canon.x, canon.y)
            quadraticTo(punta.x - 18 * u, punta.y + 30 * u, punta.x, punta.y)
            quadraticTo(punta.x + 4 * u, punta.y + 30 * u, canon.x + 2 * u, canon.y - 14 * u)
            close()
        }
        drawPath(pluma, c(Color(0xFFF2EEE4)))
        drawPath(pluma, c(Color(0xFF9A9284)), style = Stroke(0.6f * u))
        drawLine(c(Color(0xFF9A9284)), canon, Offset(punta.x - 2 * u, punta.y + 6 * u), 0.8f * u)

        // ------------------------------------------------ el listón donde descansa el códice
        val liston = Rect(w * 0.12f, h * 0.745f, w * 0.88f, h * 0.8f)

        // ------------------------------------------------ el códice
        val tapa = Rect(w * 0.235f, h * 0.16f + g * 0.5f, w * 0.77f - g, liston.top)
        // La tabla de atrás y el bloque de las hojas, vistos de lado (el canto) y desde arriba (la cabeza).
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + g + 4 * u, tapa.top - g * 0.5f + 4 * u), tapa.size)
        drawRect(c(ROBLE_OSC), Offset(tapa.left + g, tapa.top - g * 0.5f), tapa.size)
        val bloque = Path().apply {
            moveTo(tapa.left + 3 * u, tapa.top); lineTo(tapa.left + 3 * u + g, tapa.top - g * 0.5f + 1.5f * u)
            lineTo(tapa.right + g - 1.5f * u, tapa.top - g * 0.5f + 1.5f * u); lineTo(tapa.right + g - 1.5f * u, tapa.bottom - g * 0.5f - 1 * u)
            lineTo(tapa.right, tapa.bottom - 1 * u); lineTo(tapa.right, tapa.top); close()
        }
        drawPath(bloque, c(PERGAMINO))
        val capas = (g / (1.6f * u)).toInt().coerceAtLeast(2)
        for (j in 1..capas) {
            val f = j / (capas + 1f)
            drawLine(c(TINTA).copy(alpha = 0.16f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.bottom - g * f * 0.5f), 0.5f * u)
            drawLine(c(TINTA).copy(alpha = 0.12f), Offset(tapa.left + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), 0.5f * u)
        }
        // Las cédulas de vitela que asoman por la cabeza: más cuantas más hojas.
        val cedulas = (e.hojas / 30).coerceIn(0, 6)
        for (j in 0 until cedulas) {
            val cx = tapa.left + tapa.width * (0.15f + 0.7f * azar(e.semilla, 80 + j)) + g * 0.5f
            val alto = (5 + 7 * azar(e.semilla, 90 + j)) * u
            rotate(-6f + 12f * azar(e.semilla, 100 + j), Offset(cx, tapa.top)) {
                drawRect(hoja, Offset(cx - 5 * u, tapa.top - g * 0.3f - alto), Size(10 * u, alto + 2 * u))
                drawRect(c(TINTA).copy(alpha = 0.25f), Offset(cx - 5 * u, tapa.top - g * 0.3f - alto), Size(10 * u, alto + 2 * u), style = Stroke(0.5f * u))
            }
        }
        // La primera hoja, que se ve al abrir: dos columnas pautadas, renglones y la inicial roja.
        if (a > 0.01f) {
            val pag = tapa.deflate(2 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            drawRect(Color.Black.copy(alpha = 0.14f), pag.topLeft, Size(8 * u, pag.height))
            val m = Rect(pag.left + 14 * u, pag.top + 12 * u, pag.right - 10 * u, pag.bottom - 12 * u)
            val col = (m.width - 10 * u) / 2
            for (q in 0..1) {
                val x0 = m.left + q * (col + 10 * u)
                listOf(x0, x0 + col).forEach { x -> drawLine(c(BERMELLON).copy(alpha = 0.4f), Offset(x, m.top), Offset(x, m.bottom), 0.5f * u) }
                var ry = m.top + 4 * u; var j = 0
                while (ry < m.bottom) {
                    renglon(x0 + (if (q == 0 && j < 2) 14 * u else 2 * u), x0 + col * (if (j % 5 == 4) 0.6f else 1f) - 2 * u, ry, e.semilla + q * 40 + j, c(TINTA).copy(alpha = 0.55f), 1.2f)
                    ry += 5.5f * u; j++
                }
            }
            drawRect(c(BERMELLON), Offset(m.left, m.top), Size(12 * u, 12 * u))
            textoEn(medidor, "M", Rect(m.left, m.top, m.left + 12 * u, m.top + 12 * u), letraTapa, hoja, 10f)
        }
        // La tapa: tabla de roble desnuda con media piel en el lomo; gira sobre el lomo al abrirse.
        val sx = 1f - 0.94f * a
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRect(c(ROBLE_LUZ), tapa.topLeft, tapa.size)
            vetas(tapa, c(VETA).copy(alpha = 0.5f), e.semilla, densidad = 0.7f, grosor = 0.8f)
            bisel(tapa, Color.White.copy(alpha = 0.16f), Color.Black.copy(alpha = 0.3f), 2 * u)
            // La media piel: el cuero del lomo que monta sobre la tabla, clavado con cuatro clavos.
            val piel = Rect(tapa.left, tapa.top, tapa.left + tapa.width * 0.2f, tapa.bottom)
            drawRect(c(PIEL_LOMO), piel.topLeft, piel.size)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(piel.right - 1.6f * u, piel.top), Size(1.6f * u, piel.height))
            listOf(0.18f, 0.38f, 0.62f, 0.82f).forEach { f ->
                val yy = tapa.top + tapa.height * f
                drawRect(Color.White.copy(alpha = 0.14f), Offset(piel.left, yy - 2 * u), Size(piel.width - 2 * u, 1.2f * u))
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(piel.left, yy - 0.8f * u), Size(piel.width - 2 * u, 1.4f * u))
            }
            listOf(piel.top + 6 * u, piel.bottom - 6 * u).forEach { yy -> drawCircle(c(HIERRO), 1.6f * u, Offset(piel.right - 5 * u, yy)) }
            // La etiqueta de vitela bajo su lámina de cuerno, en un marco de latón: el nombre y la cuenta.
            val et = Rect(piel.right + tapa.width * 0.07f, tapa.top + tapa.height * 0.13f, tapa.right - tapa.width * 0.07f, tapa.top + tapa.height * 0.5f)
            drawRect(Color.Black.copy(alpha = 0.3f), et.topLeft + Offset(1.5f * u, 2 * u), et.size)
            drawRect(c(LATON), et.topLeft, et.size)
            val vit = et.deflate(3 * u)
            drawRect(hoja, vit.topLeft, vit.size)
            drawRect(c(BERMELLON).copy(alpha = 0.6f), vit.deflate(2 * u).topLeft, vit.deflate(2 * u).size, style = Stroke(0.6f * u))
            val corte = vit.top + vit.height * 0.58f
            textoEn(medidor, e.nombre, Rect(vit.left + 5 * u, vit.top + 3 * u, vit.right - 5 * u, corte), letraTapa, c(TINTA), 15f, minimo = 8f)
            drawLine(c(BERMELLON).copy(alpha = 0.7f), Offset(vit.left + vit.width * 0.25f, corte), Offset(vit.right - vit.width * 0.25f, corte), 0.7f * u)
            textoEn(medidor, e.cuenta, Rect(vit.left + 5 * u, corte + 1 * u, vit.right - 5 * u, vit.bottom - 3 * u), letra, c(TINTA).copy(alpha = 0.85f), 10f, minimo = 6f)
            // La lámina de cuerno: un brillo ambarino y su reflejo.
            drawRect(c(CUERNO).copy(alpha = 0.14f), vit.topLeft, vit.size)
            drawLine(Color.White.copy(alpha = 0.35f), Offset(vit.left + 4 * u, vit.top + 2 * u), Offset(vit.left + vit.width * 0.4f, vit.top + 2 * u), 1f * u)
            listOf(et.topLeft, Offset(et.right, et.top), Offset(et.left, et.bottom), et.bottomRight).forEach { drawCircle(c(HIERRO), 1.3f * u, it + Offset(if (it.x > et.center.x) -1.5f * u else 1.5f * u, if (it.y > et.center.y) -1.5f * u else 1.5f * u)) }
            // La correa que da la vuelta desde el canto y el broche de latón con su pasador.
            val yc = tapa.top + tapa.height * 0.72f
            val broche = Rect(tapa.right - tapa.width * 0.3f, yc - 6 * u, tapa.right - tapa.width * 0.18f, yc + 6 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(broche.left, yc - 4 * u + 1.5f * u), Size(tapa.right - broche.left, 8 * u))
            drawRect(c(PIEL_LOMO), Offset(broche.center.x, yc - 4 * u), Size(tapa.right - broche.center.x + g * 0.4f, 8 * u))
            drawRect(c(LATON), broche.topLeft, broche.size)
            drawRect(mezcla(c(LATON), Color.Black, 0.35f), broche.deflate(2 * u).topLeft, broche.deflate(2 * u).size, style = Stroke(0.7f * u))
            drawCircle(c(HIERRO), 1.5f * u, Offset(broche.left - 3 * u, yc))
            drawLine(c(LATON), Offset(broche.left - 3 * u, yc), Offset(broche.left, yc), 1.6f * u)
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (j in 0 until 3) drawRect(Color.Black.copy(alpha = 0.1f * (3 - j) / 3f), Offset(borde, tapa.top), Size((3 + 4 * j) * u, tapa.height))
        }
        // El lomo redondeado, a la izquierda de la tapa.
        drawRoundRect(mezcla(c(PIEL_LOMO), Color.Black, 0.2f), Offset(tapa.left - 6 * u, tapa.top - 1 * u), Size(7 * u, tapa.height + 1 * u), CornerRadius(3 * u))
        // El listón, por delante de todo: moldura de roble, con el cortaplumas y la piedra pómez.
        drawRect(c(ROBLE), liston.topLeft, liston.size)
        vetas(liston, c(VETA).copy(alpha = 0.45f), 31, densidad = 1f, grosor = 0.6f)
        drawRect(c(ROBLE_LUZ), liston.topLeft, Size(liston.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(liston.left, liston.bottom - 2 * u), Size(liston.width, 2 * u))
        val cuchillo = Offset(liston.left + 10 * u, liston.top - 1 * u)
        drawRoundRect(c(ROBLE_OSC), Offset(cuchillo.x, cuchillo.y - 3 * u), Size(16 * u, 4 * u), CornerRadius(2 * u))
        drawPath(Path().apply { moveTo(cuchillo.x + 16 * u, cuchillo.y - 3 * u); lineTo(cuchillo.x + 34 * u, cuchillo.y - 1.5f * u); lineTo(cuchillo.x + 16 * u, cuchillo.y + 1 * u); close() }, c(HIERRO_LUZ))
        drawPath(contornoPomez(Offset(liston.right - 22 * u, liston.top - 4 * u)), c(POMEZ))
        for (j in 0 until 6) drawCircle(Color.Black.copy(alpha = 0.2f), 0.7f * u, Offset(liston.right - 26 * u + 9 * u * azar(j, 3), liston.top - 6 * u + 4 * u * azar(j, 4)))
        // Los registros de seda que cuelgan por delante del listón: uno más cada 50 hojas.
        val registros = (1 + e.hojas / 50).coerceAtMost(5)
        val sedas = listOf(BERMELLON, LAPIZ, ORO, Color(0xFF3E6B3A), Color(0xFF6A3A6A))
        for (j in 0 until registros) {
            val rx = tapa.left + tapa.width * (0.3f + 0.1f * j) + g * 0.3f
            val largo = (16 + 6 * azar(e.semilla, 120 + j)) * u
            drawLine(c(sedas[j]), Offset(rx, tapa.bottom - 2 * u), Offset(rx + 1 * u, tapa.bottom + largo), 1.6f * u)
            drawRoundRect(c(sedas[j]), Offset(rx - 1.5f * u, tapa.bottom + largo - 1 * u), Size(5 * u, 5 * u), CornerRadius(1.5f * u))
        }
    }

    /** El contorno de la piedra pómez que descansa en el listón, con la base plana en [o]. */
    private fun DrawScope.contornoPomez(o: Offset): Path {
        val u = density
        return Path().apply {
            moveTo(o.x - 8 * u, o.y + 4 * u)
            cubicTo(o.x - 9 * u, o.y - 2 * u, o.x - 2 * u, o.y - 5 * u, o.x + 3 * u, o.y - 4 * u)
            cubicTo(o.x + 9 * u, o.y - 3 * u, o.x + 10 * u, o.y + 2 * u, o.x + 8 * u, o.y + 4 * u)
            close()
        }
    }

    /**
     * La pestaña de un día: una lengüeta de cuero cosida al canto del folio (con su costura y la letra de la semana en
     * oro) que sujeta una tira de vitela con el día en letra gótica; abajo, el color del ánimo como una banda pintada con
     * su filete de oro (o, sin ánimo, la pauta roja vacía).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val cuero = c(CUEROS[(e.semilla and 0x7fffffff) % CUEROS.size])
        val giro = -2f + 4f * azar(e.semilla, 1)
        rotate(giro, Offset(w / 2, h / 2)) {
            val vit = Rect(4 * u, 10 * u, w - 4 * u, h - 3 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), vit.topLeft + Offset(1.2f * u, 1.6f * u), vit.size)
            drawRect(pergamino(oscuro, false), vit.topLeft, vit.size)
            drawRect(c(BERMELLON).copy(alpha = 0.55f), vit.deflate(2.5f * u).topLeft, vit.deflate(2.5f * u).size, style = Stroke(0.6f * u))
            textoEn(medidor, e.dia, Rect(vit.left, vit.top + 7 * u, vit.right, vit.bottom - 11 * u), letraTapa, c(TINTA), 19f, minimo = 10f)
            val banda = Rect(vit.left + 2.5f * u, vit.bottom - 10 * u, vit.right - 2.5f * u, vit.bottom - 2.5f * u)
            if (e.tono != null) {
                drawRect(e.tono, banda.topLeft, banda.size)
                drawRect(c(ORO), banda.topLeft, banda.size, style = Stroke(0.8f * u))
            } else {
                drawLine(c(BERMELLON).copy(alpha = 0.4f), Offset(banda.left, banda.center.y), Offset(banda.right, banda.center.y), 0.6f * u)
            }
            // La lengüeta de cuero doblada sobre la vitela, con su costura y la letra de la semana dorada.
            val leng = Rect(2 * u, 2 * u, w - 2 * u, 17 * u)
            drawRoundRect(cuero, leng.topLeft, leng.size, CornerRadius(5 * u))
            drawRect(cuero, Offset(leng.left, leng.center.y), Size(leng.width, leng.height / 2))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(leng.left, leng.bottom - 1.6f * u), Size(leng.width, 1.6f * u))
            var x = leng.left + 4 * u
            while (x < leng.right - 3 * u) { drawLine(c(CERA).copy(alpha = 0.6f), Offset(x, leng.bottom - 3.5f * u), Offset(x + 2 * u, leng.bottom - 3.5f * u), 0.7f * u); x += 4 * u }
            textoEn(medidor, e.semana, Rect(leng.left, leng.top + 1 * u, leng.right, leng.bottom - 4 * u), letra, mezcla(c(ORO), ORO, 0.4f), 9f, peso = FontWeight.Bold)
        }
    }

    /**
     * El rótulo de un mes: una tira de vitela de orillas irregulares, pautada con doble regla roja, con una inicial
     * lombarda azul de filigrana roja a cada lado y el mes en letra gótica en rojo de rúbrica.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val tira = Rect(2 * u, 3 * u, w - 2 * u, h - 3 * u)
        drawPath(contornoIrregular(tira.translate(1.2f * u, 1.6f * u), 7, 40, 0.04f), Color.Black.copy(alpha = 0.22f))
        drawPath(contornoIrregular(tira, 7, 40, 0.04f), pergamino(oscuro, false))
        listOf(tira.top + 5 * u, tira.bottom - 5 * u).forEach { y ->
            drawLine(c(BERMELLON).copy(alpha = 0.55f), Offset(tira.left + 6 * u, y), Offset(tira.right - 6 * u, y), 0.6f * u)
        }
        listOf(tira.left + 16 * u, tira.right - 16 * u).forEach { x ->
            val q = Rect(x - 8 * u, h / 2 - 8 * u, x + 8 * u, h / 2 + 8 * u)
            drawRect(c(LAPIZ), q.topLeft, q.size)
            drawRect(c(BERMELLON), q.inflate(2 * u).topLeft, q.inflate(2 * u).size, style = Stroke(0.6f * u))
            drawPath(estrella(q.center, 5 * u, 4, 0.4f), pergamino(oscuro, false))
            hiedra(Offset(if (x < w / 2) q.right + 2 * u else q.left - 2 * u, h / 2), 6 * u, if (x < w / 2) 0f else 180f, c(BERMELLON).copy(alpha = 0.8f))
        }
        textoEn(medidor, texto, Rect(tira.left + 40 * u, tira.top + 5 * u, tira.right - 40 * u, tira.bottom - 5 * u), letraTapa, c(BERMELLON), 16f, minimo = 9f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El rojo de la cera de la cancillería y el azul de sus cintas. */
    private val CERA_SELLO = Color(0xFFB3261E)
    private val CINTA_SELLO = Color(0xFF1F3A93)

    /** Una flor de lis de alto [s] centrada en [o] (el pétalo del medio, los dos que se curvan, la banda y el pie). */
    private fun lis(o: Offset, s: Float): Path = Path().apply {
        moveTo(o.x, o.y - s * 0.62f)
        quadraticTo(o.x + s * 0.24f, o.y - s * 0.22f, o.x + s * 0.06f, o.y + s * 0.08f)
        lineTo(o.x - s * 0.06f, o.y + s * 0.08f)
        quadraticTo(o.x - s * 0.24f, o.y - s * 0.22f, o.x, o.y - s * 0.62f)
        close()
        for (sg in listOf(-1f, 1f)) {
            moveTo(o.x + sg * s * 0.06f, o.y + s * 0.06f)
            cubicTo(o.x + sg * s * 0.62f, o.y - s * 0.1f, o.x + sg * s * 0.6f, o.y - s * 0.5f, o.x + sg * s * 0.34f, o.y - s * 0.38f)
            quadraticTo(o.x + sg * s * 0.44f, o.y - s * 0.06f, o.x + sg * s * 0.08f, o.y + s * 0.2f)
            close()
        }
        addRect(Rect(o.x - s * 0.26f, o.y + s * 0.1f, o.x + s * 0.26f, o.y + s * 0.2f))
        moveTo(o.x - s * 0.07f, o.y + s * 0.2f); lineTo(o.x - s * 0.18f, o.y + s * 0.46f)
        lineTo(o.x + s * 0.18f, o.y + s * 0.46f); lineTo(o.x + s * 0.07f, o.y + s * 0.2f); close()
    }

    /** El sello de la cancillería en [o], de radio [r]: la gota de cera roja, la gráfila de perlas, el filo alzado y la lis. */
    private fun DrawScope.selloCancilleria(o: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        val cera = if (oscuro) mezcla(CERA_SELLO, Color.Black, 0.3f) else CERA_SELLO
        val hondo = mezcla(cera, Color.Black, 0.38f); val claro = mezcla(cera, Color.White, 0.28f)
        translate(r * 0.06f, r * 0.12f) { drawPath(gotaMedievo(o, r, semilla), Color.Black.copy(alpha = 0.3f)) }
        drawPath(gotaMedievo(o, r, semilla), cera)
        drawCircle(hondo, r * 0.74f, o, style = Stroke(r * 0.07f))
        drawCircle(claro.copy(alpha = 0.55f), r * 0.66f, o, style = Stroke(r * 0.035f))
        for (j in 0 until 22) drawCircle(hondo, r * 0.045f, polarMedievo(o, r * 0.84f, j * 360f / 22))
        drawPath(lis(o + Offset(r * 0.03f, r * 0.04f), r * 0.95f), hondo)
        drawPath(lis(o, r * 0.95f), claro)
        drawArc(Color.White.copy(alpha = 0.22f), 205f, 70f, false, o - Offset(r * 0.86f, r * 0.86f), Size(r * 1.72f, r * 1.72f),
            style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /**
     * La carta es un pliego de pergamino doblado en tres, con la lengua (la solapa redonda que baja de arriba) cerrada con el
     * sello pendiente de la cancillería: la cera roja con su gráfila de perlas y la flor de lis, que cuelga de dos cintas
     * azules. Las selladas al futuro llevan además la cinta azul de lado a lado, anudada bajo el sello. A quién va y las
     * fechas, a pluma sobre el pergamino entre dos renglones de pauta roja. Al abrirse el sello se parte (la mitad de arriba
     * se va con la lengua), la lengua gira hacia atrás y enseña su envés, y la carta sube. Al sellar, el sello cae grande y
     * se asienta. En oscuro el pergamino se hunde en la penumbra y la letra pasa a crema.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0E0A07), 0.6f) else x
        val tinta = if (oscuro) Color(0xFFF0E3C2) else TINTA
        val p = Rect(c.left + W * 0.05f, c.top + H * 0.08f, c.right - W * 0.05f, c.bottom - H * 0.07f)
        val cx = p.center.x
        val largo = p.height * 0.36f
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val punta = Offset(cx, p.top + largo)
        val rs = minOf(W, H) * 0.11f
        val partido = e.romper.coerceIn(0f, 1f)
        fun lengua(g: Float) = Path().apply {
            moveTo(p.left, p.top)
            cubicTo(p.left + p.width * 0.08f, p.top + g * largo * 0.3f, cx - p.width * 0.3f, p.top + g * largo * 0.78f, cx - p.width * 0.11f, p.top + g * largo * 0.96f)
            quadraticTo(cx, p.top + g * largo * 1.04f, cx + p.width * 0.11f, p.top + g * largo * 0.96f)
            cubicTo(cx + p.width * 0.3f, p.top + g * largo * 0.78f, p.right - p.width * 0.08f, p.top + g * largo * 0.3f, p.right, p.top)
            close()
        }
        // El sello (o su mitad de arriba, que se va con la lengua) en la punta de la lengua; [g] es el giro de la lengua.
        fun DrawScope.selloEn(g: Float, mitad: Int) {
            val o = Offset(cx, p.top + g * largo)
            val a = e.alfaSello.coerceIn(0f, 1f)
            if (a <= 0.01f) return
            conAlfaMedievo(a, Rect(punta, rs * 4f * e.escalaSello)) {
                scale(e.escalaSello, punta) {
                    if (mitad == 0) {
                        // Las dos cintas de donde cuelga, y el resplandor de la que ya se puede abrir.
                        listOf(-1f, 1f).forEach { s ->
                            drawPath(Path().apply {
                                moveTo(o.x + s * rs * 0.15f, o.y); lineTo(o.x + s * rs * 0.5f, o.y)
                                lineTo(o.x + s * rs * 0.78f, o.y + rs * 1.75f); lineTo(o.x + s * rs * 0.56f, o.y + rs * 1.55f)
                                lineTo(o.x + s * rs * 0.36f, o.y + rs * 1.78f); close()
                            }, k(CINTA_SELLO))
                        }
                        if (e.brillo > 0f) {
                            drawCircle(Brush.radialGradient(0f to ORO.copy(alpha = 0.75f * e.brillo), 0.45f to LLAMA.copy(alpha = 0.3f * e.brillo),
                                1f to Color.Transparent, center = o, radius = rs * 2.8f), rs * 2.8f, o)
                            rayosMedievo(o, rs * 1.25f, rs * 2.3f, ORO.copy(alpha = 0.9f * e.brillo), 12, 1.1f)
                        }
                    }
                    when {
                        partido <= 0f -> selloCancilleria(o, rs, oscuro, e.semilla)
                        mitad == 1 -> clipPath(mitadSelloMedievo(o, rs, true, e.semilla)) {
                            translate(0f, -partido * rs * 0.18f) { rotate(-8f * partido, o) { selloCancilleria(o, rs, oscuro, e.semilla) } }
                        }
                        else -> clipPath(mitadSelloMedievo(o, rs, false, e.semilla)) {
                            translate(0f, partido * rs * 0.1f) { rotate(5f * partido, o) { selloCancilleria(o, rs, oscuro, e.semilla) } }
                        }
                    }
                }
            }
        }

        // La sombra del pliego.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(p.left + 2 * u, p.top + 3 * u), p.size, CornerRadius(2 * u))
        // Abierta: la lengua vuelta hacia atrás, con su envés (y la mitad del sello que se llevó).
        if (giro < 0f) {
            drawPath(lengua(giro), k(Color(0xFFD2BE8E)))
            drawPath(lengua(giro), k(Color(0xFF8A7148)).copy(alpha = 0.6f), style = Stroke(0.8f * u))
            if (partido > 0f) selloEn(giro, 1)
        }
        // La carta, que sube desde dentro del pliego.
        val sale = salidaMedievo(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaMedievo(Rect(cx - p.width * 0.43f, p.top + 3 * u - sube, cx + p.width * 0.43f, p.bottom - 6 * u - sube), e, oscuro, barbas = true)
        }
        // El frente del pliego: el pergamino con sus dobleces, la pauta roja y lo escrito.
        val frente = contornoIrregular(p, e.semilla, 40, 0.008f)
        drawPath(frente, k(PERGAMINO))
        clipPath(frente) {
            for (j in 0 until 6) drawCircle(k(Color(0xFF9C7A44)).copy(alpha = 0.05f), p.height * (0.12f + 0.12f * azar(e.semilla, 30 + j)),
                Offset(p.left + p.width * azar(e.semilla, 40 + j), p.top + p.height * azar(e.semilla, 50 + j)))
            listOf(1f / 3, 2f / 3).forEach { f ->
                drawLine(Color.Black.copy(alpha = 0.1f), Offset(p.left + p.width * f, p.top), Offset(p.left + p.width * f, p.bottom), 1.2f * u)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), Offset(p.left + p.width * f + 1.2f * u, p.top), Offset(p.left + p.width * f + 1.2f * u, p.bottom), 0.8f * u)
            }
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(p.left, p.bottom - 3 * u), Size(p.width, 3 * u))
        }
        drawPath(frente, k(Color(0xFF8A7148)).copy(alpha = 0.5f), style = Stroke(0.8f * u))
        val texto = Rect(p.left + 8 * u, punta.y + rs * 1.9f, p.right - 8 * u, p.bottom - 4 * u)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            val m = texto.top + texto.height * 0.56f
            drawLine(k(BERMELLON).copy(alpha = 0.45f), Offset(texto.left, texto.top), Offset(texto.right, texto.top), 0.7f * u)
            drawLine(k(BERMELLON).copy(alpha = 0.45f), Offset(texto.left, m), Offset(texto.right, m), 0.6f * u)
            textoEn(medidor, e.linea1, Rect(texto.left, texto.top, texto.right, m), letra, tinta, 12f, peso = FontWeight.SemiBold, minimo = 8f)
            textoEn(medidor, e.linea2, Rect(texto.left, m, texto.right, texto.bottom), letra, if (oscuro) tinta.copy(alpha = 0.85f) else BERMELLON, 9f, minimo = 7f)
        }
        // Cerrada (o cerrándose): la lengua por delante, con su sombra.
        if (giro >= 0f) {
            translate(0f, 2f * u) { drawPath(lengua(giro), Color.Black.copy(alpha = 0.24f)) }
            drawPath(lengua(giro), k(Color(0xFFF4EBD3)))
            clipPath(lengua(giro)) { drawRect(Color.Black.copy(alpha = 0.06f), Offset(p.left, p.top), Size(p.width, 3 * u)) }
            drawPath(lengua(giro), k(Color(0xFF7A5E36)).copy(alpha = 0.8f), style = Stroke(0.9f * u))
        }
        // La cinta azul de lado a lado (las selladas al futuro), que se tiende desde las orillas hasta el sello y se anuda.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val ancho = maxOf(3 * u, H * 0.045f)
            val tramo = (cx - p.left) * ata
            listOf(p.left to p.left + tramo, p.right - tramo to p.right).forEach { (a, b) ->
                drawRect(k(CINTA_SELLO), Offset(a, punta.y - ancho / 2), Size(b - a, ancho))
                drawLine(Color.White.copy(alpha = 0.18f), Offset(a, punta.y - ancho * 0.3f), Offset(b, punta.y - ancho * 0.3f), 0.7f * u)
            }
            // El lazo, a los lados del sello, cuando ya da la vuelta entera.
            if (ata > 0.85f) listOf(-1f, 1f).forEach { s ->
                val f = (ata - 0.85f) / 0.15f
                val lazo = Rect(punta.x + s * rs * (0.7f + 0.9f * f) - rs * 0.55f * f, punta.y - rs * 0.42f * f, punta.x + s * rs * (0.7f + 0.9f * f) + rs * 0.55f * f, punta.y + rs * 0.42f * f)
                drawOval(k(CINTA_SELLO), lazo.topLeft, lazo.size, style = Stroke(ancho * 0.6f))
            }
        }
        // El sello: entero; o partido, con su mitad de abajo en el pliego.
        if (partido <= 0f) selloEn(maxOf(giro, 0f), 0)
        else {
            selloEn(1f, 0)
            if (giro >= 0f) selloEn(giro, 1)
        }
    }

    override val cajaPrevia = Rect(0.165f, 0.08f, 0.835f, 0.25f)

    override val altoBuzon: Dp = 200.dp

    /**
     * El buzón es el arca de la cancillería, contra un muro de sillares: un arcón de roble con flejes de hierro, la cerradura
     * y la tapa abierta hacia atrás, pintada por dentro de bermellón con su cenefa de lapislázuli en zigzag y sujeta con una
     * cadenilla. Dentro, de pie, los pliegos: los que ya se pueden abrir con su sello encendido, los sellados con la cinta azul
     * en cruz, los leídos abiertos con la lengua levantada; en el fondo, un rollo de cinta y una barra de lacre. En el frente,
     * tres cédulas de pergamino clavadas con la cuenta de cada clase. En oscuro, una vela en su gancho alumbra el muro.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0E0A07), 0.58f) else x
        // El muro de sillares y el piso de barro.
        drawRect(k(PIEDRA))
        val hilada = 22 * u; val pieza = 48 * u
        var y = 0f; var fila = 0
        while (y < h * 0.9f) {
            drawLine(k(JUNTA), Offset(0f, y), Offset(w, y), 1.2f * u)
            var x = if (fila % 2 == 0) -pieza / 3 else pieza / 3
            while (x < w) {
                drawLine(k(JUNTA), Offset(x, y), Offset(x, y + hilada), 1.2f * u)
                drawRect(k(PIEDRA_OSC).copy(alpha = 0.25f + 0.25f * azar(fila, (x / u).toInt())), Offset(x + 1.5f * u, y + 1.5f * u), Size(pieza - 3 * u, hilada - 3 * u))
                x += pieza
            }
            y += hilada; fila++
        }
        val piso = h * 0.9f
        drawRect(k(BALDOSA), Offset(0f, piso), Size(w, h - piso))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, piso), Size(w, 2 * u))
        // La vela en su gancho, a la izquierda; en oscuro, encendida.
        val vela = Rect(w * 0.05f - 3 * u, h * 0.2f, w * 0.05f + 3 * u, h * 0.36f)
        if (oscuro) drawRect(Brush.radialGradient(0f to LLAMA.copy(alpha = 0.32f), 0.4f to LLAMA.copy(alpha = 0.12f), 1f to Color.Transparent,
            center = Offset(vela.center.x, vela.top), radius = w * 0.75f))
        drawLine(k(HIERRO), Offset(vela.center.x - 8 * u, vela.bottom - 14 * u), Offset(vela.center.x - 8 * u, vela.bottom + 12 * u), 2 * u)
        drawLine(k(HIERRO), Offset(vela.center.x - 8 * u, vela.bottom + 8 * u), Offset(vela.center.x, vela.bottom + 2 * u), 1.6f * u)
        drawOval(k(HIERRO), Offset(vela.center.x - 6 * u, vela.bottom), Size(12 * u, 4 * u))
        drawRect(if (oscuro) mezcla(CERA, Color.Black, 0.12f) else CERA, vela.topLeft, vela.size)
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(vela.right - 2 * u, vela.top), Size(2 * u, vela.height))
        drawLine(TINTA, Offset(vela.center.x, vela.top), Offset(vela.center.x, vela.top - 2 * u), 0.9f * u)
        if (oscuro) {
            haloMedievo(Offset(vela.center.x, vela.top - 6 * u), 22 * u, LLAMA, 0.7f)
            llamaMedievo(Offset(vela.center.x, vela.top - 1.5f * u), 10 * u, LLAMA)
        }

        // ------------------------------------------------ el arca
        val frente = Rect(w * 0.13f, h * 0.5f, w * 0.89f, piso - 4 * u)
        val boca = Rect(frente.left + 5 * u, h * 0.4f, frente.right - 5 * u, frente.top)
        // La tapa abierta, vista por dentro (más angosta arriba, porque se aleja), con su cadenilla.
        val tapa = Path().apply {
            moveTo(boca.left + 6 * u, h * 0.06f); lineTo(boca.right - 6 * u, h * 0.06f); lineTo(frente.right, boca.top); lineTo(frente.left, boca.top); close()
        }
        drawPath(tapa, k(ROBLE_OSC))
        val pintura = Path().apply {
            moveTo(boca.left + 13 * u, h * 0.06f + 6 * u); lineTo(boca.right - 13 * u, h * 0.06f + 6 * u)
            lineTo(frente.right - 8 * u, boca.top - 5 * u); lineTo(frente.left + 8 * u, boca.top - 5 * u); close()
        }
        drawPath(pintura, k(BERMELLON))
        clipPath(pintura) {
            // La cenefa de lapislázuli en zigzag y unas estrellitas de oro.
            val y0 = h * 0.06f + 6 * u; val alto = 8 * u
            drawRect(k(LAPIZ), Offset(0f, y0), Size(w, alto))
            var x = boca.left; var arribaZ = true
            val zig = Path().apply { moveTo(x, y0 + alto) }
            while (x < boca.right) { x += 6 * u; zig.lineTo(x, if (arribaZ) y0 + 1.5f * u else y0 + alto - 1.5f * u); arribaZ = !arribaZ }
            drawPath(zig, k(ORO), style = Stroke(1.2f * u))
            for (j in 0 until 9) {
                val o = Offset(boca.left + 18 * u + (boca.width - 36 * u) * (j % 5) / 4f + (if (j >= 5) (boca.width - 36 * u) / 8f else 0f), y0 + alto + 12 * u + (j / 5) * 14 * u)
                drawPath(estrella(o, 3.2f * u, 6, 0.45f), k(ORO).copy(alpha = 0.8f))
            }
        }
        drawPath(tapa, Color.Black.copy(alpha = 0.35f), style = Stroke(1.2f * u))
        listOf(0.3f, 0.7f).forEach { f -> drawLine(k(HIERRO), Offset(boca.left + 6 * u + (boca.width - 12 * u) * f, h * 0.06f), Offset(frente.left + frente.width * f, boca.top), 4 * u) }
        // El hueco del arca, en sombra, con el canto de las tablas de atrás.
        drawRect(k(HUECO), boca.topLeft, boca.size)
        drawRect(k(ROBLE).copy(alpha = 0.6f), boca.topLeft, Size(boca.width, 3 * u))
        // Lo que hay siempre en el fondo: un rollo de cinta azul y una barra de lacre.
        drawCircle(k(CINTA_SELLO), 6 * u, Offset(boca.right - 16 * u, frente.top - 4 * u))
        drawCircle(k(mezcla(CINTA_SELLO, Color.White, 0.25f)), 6 * u, Offset(boca.right - 16 * u, frente.top - 4 * u), style = Stroke(1.2f * u))
        drawCircle(k(HUECO), 2 * u, Offset(boca.right - 16 * u, frente.top - 4 * u))
        rotate(-12f, Offset(boca.left + 26 * u, frente.top - 4 * u)) {
            drawRoundRect(k(CERA_SELLO), Offset(boca.left + 12 * u, frente.top - 7 * u), Size(28 * u, 5 * u), CornerRadius(2 * u))
        }
        // Los pliegos, de pie dentro del arca.
        val cartas = repartoMedievo(e, 10)
        if (cartas.isNotEmpty()) {
            val paso = minOf(30 * u, (boca.width - 16 * u) / cartas.size)
            val ancho = minOf(24 * u, paso - 3 * u)
            val x0 = boca.center.x - paso * cartas.size / 2 + paso / 2
            cartas.forEachIndexed { i, clase ->
                val xc = x0 + paso * i
                val alto = h * (0.25f + 0.05f * azar(i, 3))
                val r = Rect(xc - ancho / 2, frente.top - alto, xc + ancho / 2, frente.top + 4 * u)
                rotate((azar(i, 5) - 0.5f) * 8f, Offset(xc, frente.top)) {
                    if (clase == 2) {
                        // Leída: la lengua levantada y la hoja que asoma, escrita.
                        drawPath(Path().apply { moveTo(r.left, r.top); lineTo(xc, r.top - ancho * 0.55f); lineTo(r.right, r.top); close() }, k(Color(0xFFD2BE8E)))
                        drawRect(k(Color(0xFFF4EBD2)), Offset(r.left + 2 * u, r.top - ancho * 0.35f), Size(r.width - 4 * u, r.height * 0.6f))
                        for (j in 0 until 4) drawLine(k(TINTA).copy(alpha = 0.45f), Offset(r.left + 4 * u, r.top - ancho * 0.2f + j * 3.5f * u), Offset(r.right - 4 * u - (j % 2) * 3 * u, r.top - ancho * 0.2f + j * 3.5f * u), 0.7f * u)
                    }
                    drawRect(k(PERGAMINO), Offset(r.left, r.top + (if (clase == 2) r.height * 0.3f else 0f)), Size(r.width, r.height * (if (clase == 2) 0.7f else 1f)))
                    drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.right - 2.5f * u, r.top), Size(2.5f * u, r.height))
                    if (clase != 2) {
                        // La lengua cerrada y el sello (encendido en las que ya se pueden abrir).
                        drawPath(Path().apply { moveTo(r.left, r.top); lineTo(xc, r.top + r.height * 0.42f); lineTo(r.right, r.top); close() }, k(Color(0xFFEFE5C8)))
                        drawPath(Path().apply { moveTo(r.left, r.top); lineTo(xc, r.top + r.height * 0.42f); lineTo(r.right, r.top) }, k(Color(0xFF8A7148)), style = Stroke(0.7f * u))
                        val o = Offset(xc, r.top + r.height * 0.42f)
                        if (clase == 1) {
                            drawRect(k(CINTA_SELLO), Offset(r.left, o.y - 1.5f * u), Size(r.width, 3 * u))
                            drawRect(k(CINTA_SELLO), Offset(xc - 1.5f * u, r.top), Size(3 * u, r.height))
                        } else {
                            haloMedievo(o, ancho * 1.1f, ORO, if (oscuro) 0.9f else 0.7f)
                            rayosMedievo(o, ancho * 0.36f, ancho * 0.75f, ORO, 8, 0.9f)
                        }
                        drawCircle(k(CERA_SELLO), ancho * 0.24f, o)
                        drawCircle(k(mezcla(CERA_SELLO, Color.Black, 0.35f)), ancho * 0.16f, o, style = Stroke(0.8f * u))
                    } else drawCircle(k(CERA_SELLO), ancho * 0.12f, Offset(xc, r.top + r.height * 0.42f))
                }
            }
        }
        // El frente: tablas de roble, flejes de hierro con sus clavos y la cerradura.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(frente.left + 3 * u, frente.top + 4 * u), frente.size)
        drawRect(k(ROBLE), frente.topLeft, frente.size)
        vetas(frente, k(VETA).copy(alpha = 0.4f), 11, densidad = 0.5f, grosor = 0.7f)
        drawLine(k(ROBLE_OSC), Offset(frente.left, frente.center.y), Offset(frente.right, frente.center.y), 1 * u)
        bisel(frente, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.3f), 2 * u)
        drawRect(k(HIERRO), Offset(frente.left, frente.top), Size(frente.width, 5 * u))
        listOf(1f / 3, 2f / 3).forEach { f ->
            val x = frente.left + frente.width * f
            drawRect(k(HIERRO), Offset(x - 3 * u, frente.top), Size(6 * u, frente.height))
            var yy = frente.top + 9 * u
            while (yy < frente.bottom - 4 * u) { drawCircle(k(HIERRO_LUZ), 1.3f * u, Offset(x, yy)); yy += 11 * u }
        }
        val cerradura = Rect(frente.center.x - 9 * u, frente.top + 5 * u, frente.center.x + 9 * u, frente.top + 21 * u)
        drawRoundRect(k(HIERRO), cerradura.topLeft, cerradura.size, CornerRadius(2 * u))
        drawRoundRect(k(HIERRO_LUZ), cerradura.topLeft, cerradura.size, CornerRadius(2 * u), style = Stroke(0.8f * u))
        drawCircle(Color.Black, 2 * u, Offset(cerradura.center.x, cerradura.center.y - 2 * u))
        drawRect(Color.Black, Offset(cerradura.center.x - 0.8f * u, cerradura.center.y - 2 * u), Size(1.6f * u, 6 * u))
        // Las tres cédulas de pergamino clavadas, con la cuenta.
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "selladas", e.abiertas to "leídas")
        rotulos.forEachIndexed { i, (n, nombre) ->
            val xc = frente.left + frente.width * (i * 2 + 1) / 6f
            val r = Rect(xc - frente.width * 0.13f, frente.top + frente.height * 0.34f, xc + frente.width * 0.13f, frente.bottom - frente.height * 0.1f)
            val forma = contornoIrregular(r, 70 + i, 24, 0.03f)
            translate(1.5f * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
            val papel = if (oscuro) mezcla(PERGAMINO, Color(0xFF0E0A07), 0.3f) else PERGAMINO
            drawPath(forma, papel)
            listOf(r.left + 5 * u, r.right - 5 * u).forEach { drawCircle(k(HIERRO), 1.6f * u, Offset(it, r.top + 5 * u)) }
            val tono = if (i == 0 && n > 0) BERMELLON else TINTA
            textoEn(medidor, "$n", Rect(r.left, r.top + 2 * u, r.right, r.top + r.height * 0.64f), letra, tono, 19f, peso = FontWeight.Bold)
            textoEn(medidor, nombre, Rect(r.left + 3 * u, r.top + r.height * 0.6f, r.right - 3 * u, r.bottom - 3 * u), letra, TINTA, 9.5f, minimo = 7f)
        }
    }

    // ---------------------------------------------------------------- lo de alrededor

    /** El verde de cobre de una de las lengüetas y de la tela del tablero. */
    private val VERDIGRIS = Color(0xFF3E6A4C)
    /** Las lengüetas de cuero teñido del canto del códice, una por pestaña: rubia, lapislázuli, verde, leonado y castaño. */
    private val LENGUETAS = listOf(Color(0xFF8A2E22), Color(0xFF2E4C88), Color(0xFF3E6A4C), Color(0xFF8C6A2E), Color(0xFF5E3A24))
    /** Las maderas de las tablas desnudas de las obras: roble claro, roble y nogal. */
    private val TABLAS_OBRA = listOf(Color(0xFFA27B4E), Color(0xFF8E6942), Color(0xFF6F5034))

    /** El pergamino de lo de alrededor: en modo oscuro se hunde de verdad (hasta casi el cuero), para leerse con tinta clara. */
    private fun pergaminoHondo(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, Color(0xFF17110B), 0.74f) else PERGAMINO
    /** La tinta sobre [pergaminoHondo]: la de hierro y agalla de día, la cera clara de noche. */
    private fun tintaHonda(oscuro: Boolean): Color = if (oscuro) CERA else TINTA

    /**
     * El emblema de la pestaña [k] en [o], de radio [r]: el códice con su broche (diario), la tablilla de cera de dos hojas
     * con el punzón (momentos), la media luna con su estrella (sueños), la pluma de ave (ideario) y la carta doblada con su
     * sello colgante (cartas). [c] es el oro del dibujo y [f] el cuero de debajo (para los huecos).
     */
    private fun DrawScope.emblema(k: Int, o: Offset, r: Float, c: Color, f: Color) {
        val u = density
        when (k) {
            0 -> {
                drawRoundRect(c, Offset(o.x - r * 0.72f, o.y - r), Size(r * 1.44f, r * 2f), CornerRadius(r * 0.18f))
                drawRect(f, Offset(o.x - r * 0.5f, o.y - r * 0.8f), Size(r * 1f, r * 1.6f), style = Stroke(0.7f * u))
                drawRect(f, Offset(o.x + r * 0.42f, o.y - r * 0.16f), Size(r * 0.5f, r * 0.32f))
                drawCircle(c, r * 0.12f, Offset(o.x, o.y))
            }
            1 -> {
                listOf(-1f, 1f).forEach { s ->
                    val x0 = if (s < 0) o.x - r * 1.02f else o.x + r * 0.06f
                    drawRect(c, Offset(x0, o.y - r * 0.8f), Size(r * 0.96f, r * 1.6f))
                    drawRect(f, Offset(x0 + r * 0.14f, o.y - r * 0.62f), Size(r * 0.68f, r * 1.24f))
                    for (j in 0 until 3) drawLine(c.copy(alpha = 0.8f), Offset(x0 + r * 0.24f, o.y - r * 0.3f + j * r * 0.3f), Offset(x0 + r * 0.72f, o.y - r * 0.3f + j * r * 0.3f), 0.6f * u)
                }
                drawLine(c, Offset(o.x + r * 0.7f, o.y + r * 1.05f), Offset(o.x + r * 1.15f, o.y - r * 1.05f), 1.2f * u)
            }
            2 -> {
                drawPath(mediaLunaMedievo(Offset(o.x - r * 0.15f, o.y + r * 0.05f), r * 0.92f), c)
                drawPath(estrella(Offset(o.x + r * 0.78f, o.y - r * 0.6f), r * 0.36f, 5, 0.45f), c)
                drawCircle(c, r * 0.09f, Offset(o.x + r * 0.95f, o.y + r * 0.4f))
            }
            3 -> {
                val pluma = Path().apply {
                    moveTo(o.x - r * 0.85f, o.y + r * 1.0f)
                    cubicTo(o.x - r * 0.4f, o.y + r * 0.1f, o.x + r * 0.2f, o.y - r * 0.9f, o.x + r * 0.95f, o.y - r * 1.05f)
                    cubicTo(o.x + r * 0.6f, o.y - r * 0.3f, o.x + r * 0.1f, o.y + r * 0.35f, o.x - r * 0.6f, o.y + r * 0.72f)
                    close()
                }
                drawPath(pluma, c)
                drawLine(f, Offset(o.x - r * 0.62f, o.y + r * 0.72f), Offset(o.x + r * 0.78f, o.y - r * 0.92f), 0.7f * u)
                drawLine(c, Offset(o.x - r * 0.85f, o.y + r * 1.0f), Offset(o.x - r * 1.05f, o.y + r * 1.2f), 1f * u)
            }
            else -> {
                drawRect(c, Offset(o.x - r, o.y - r * 0.7f), Size(r * 2f, r * 1.2f))
                drawLine(f, Offset(o.x - r, o.y - r * 0.7f), Offset(o.x, o.y), 0.8f * u)
                drawLine(f, Offset(o.x + r, o.y - r * 0.7f), Offset(o.x, o.y), 0.8f * u)
                drawLine(c, Offset(o.x, o.y + r * 0.5f), Offset(o.x, o.y + r * 0.82f), 0.9f * u)
                drawCircle(c, r * 0.3f, Offset(o.x, o.y + r * 1.05f))
                drawCircle(f, r * 0.12f, Offset(o.x, o.y + r * 1.05f))
            }
        }
    }

    override val altoPestanas: Dp = 76.dp

    /**
     * Las pestañas: el canto delantero de un gran códice tumbado (el bloque de las hojas entre las dos tablas) y, cosidas a
     * él, cinco lengüetas de cuero teñido como las que marcaban las partes de un libro: cada una con su emblema en oro y su
     * nombre; la cuenta va escrita en rojo sobre el canto, debajo. La elegida se alza más, más clara, con filete de oro y
     * su nudo de cuero.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val canto = h - 20 * u
        // El canto del códice: la tabla de arriba, el bloque de las hojas (rayitas de los pliegos) y la tabla de abajo.
        drawRect(c(ROBLE_OSC), Offset(0f, canto - 3 * u), Size(w, 3 * u))
        val hojas = Rect(0f, canto, w, h - 4 * u)
        drawRect(c(PERGAMINO), hojas.topLeft, hojas.size)
        var y = hojas.top + 1.4f * u; var j = 0
        while (y < hojas.bottom && j < 40) { drawLine(c(PIEDRA_OSC).copy(alpha = 0.35f), Offset(0f, y), Offset(w, y), 0.4f * u); y += maxOf(1.6f * u, 1f); j++ }
        drawRect(Color.Black.copy(alpha = 0.12f), hojas.topLeft, Size(w, 2 * u))
        drawRect(c(ROBLE), Offset(0f, h - 4 * u), Size(w, 4 * u))
        drawRect(c(ROBLE_LUZ).copy(alpha = 0.6f), Offset(0f, h - 4 * u), Size(w, 0.8f * u))
        for (k in 0 until 5) {
            val q = quintoMedievo(k, w, h)
            val sel = k == e.actual
            val cuero0 = LENGUETAS[k]
            val cuero = c(if (sel) mezcla(cuero0, Color.White, 0.1f) else mezcla(cuero0, Color.Black, 0.18f))
            val arriba = if (sel) 2 * u else 10 * u
            val r = Rect(q.left + 3 * u, arriba, q.right - 3 * u, canto + 3 * u)
            // La sombra que la lengüeta echa sobre el canto, y la lengüeta: recta abajo, redonda arriba.
            drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.4f * u, r.top + 1.6f * u), r.size, CornerRadius(7 * u))
            drawRoundRect(cuero, r.topLeft, r.size, CornerRadius(7 * u))
            drawRect(Color.White.copy(alpha = 0.12f), Offset(r.left + 2 * u, r.top + 5 * u), Size(1.6f * u, r.height - 8 * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.right - 3 * u, r.top + 5 * u), Size(2.2f * u, r.height - 8 * u))
            val oro = if (sel) mezcla(c(ORO), ORO, 0.5f) else mezcla(c(ORO), c(PERGAMINO), 0.3f).copy(alpha = 0.9f)
            if (sel) {
                drawRoundRect(oro, Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), CornerRadius(5.4f * u), style = Stroke(0.8f * u))
                // El nudo de cuero con que se cose al canto.
                drawCircle(mezcla(cuero, Color.Black, 0.25f), 3.2f * u, Offset(r.center.x, canto + 2 * u))
                drawCircle(Color.White.copy(alpha = 0.18f), 1.2f * u, Offset(r.center.x - 0.8f * u, canto + 1.2f * u))
            } else {
                // Los dos puntos de la costura.
                listOf(-3f, 3f).forEach { dx -> drawCircle(Color.Black.copy(alpha = 0.4f), 0.9f * u, Offset(r.center.x + dx * u, canto + 0.5f * u)) }
            }
            emblema(k, Offset(r.center.x, r.top + 13 * u), 6.5f * u, oro, cuero)
            textoEn(medidor, e.nombres.getOrElse(k) { "" }, Rect(r.left + 3 * u, r.top + 23 * u, r.right - 3 * u, r.top + 38 * u), letra,
                if (sel) mezcla(c(CERA), CERA, 0.6f) else mezcla(c(CERA), CERA, 0.3f).copy(alpha = 0.9f), 11f, peso = FontWeight.Bold, minimo = 7f)
            // La cuenta, en rojo de rúbrica sobre el canto.
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", Rect(q.left, canto + 3.6f * u, q.right, h - 4.4f * u), letra,
                if (oscuro) mezcla(BERMELLON, Color.Black, 0.2f) else BERMELLON, 10f, peso = FontWeight.Bold, minimo = 6f)
        }
    }

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.19f, 0.2f, 0.95f, 0.8f)
    override val letraCampo: FontFamily = LetraExtra.cardo
    override fun tintaCampo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El buscador: el canto de roble del atril y, clavada en él, una tira de pergamino pautada en rojo, con los piquetes del
     * margen. A la izquierda, el cuerno de tinta encajado en su agujero con la pluma; con el foco, la pluma se inclina sobre
     * la tira para escribir y la pauta se enciende de oro.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRoundRect(c(ROBLE), Offset.Zero, size, CornerRadius(3 * u))
        vetas(Rect(0f, 0f, w, h), c(VETA).copy(alpha = 0.45f), 73, densidad = 0.7f, grosor = 0.7f)
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.3f), 1.4f * u)
        // La tira de pergamino.
        val tira = Rect(w * cajaCampo.left - 8 * u, 6 * u, w - 6 * u, h - 6 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tira.left + 1.2f * u, tira.top + 1.4f * u), tira.size)
        drawRect(pergaminoHondo(oscuro), tira.topLeft, tira.size)
        val pauta = if (e.enfocado) (if (oscuro) mezcla(ORO, Color.Black, 0.1f) else mezcla(ORO, BERMELLON, 0.35f)) else c(BERMELLON).copy(alpha = 0.55f)
        listOf(tira.top + 3 * u, tira.bottom - 3 * u).forEach { yy -> drawLine(pauta, Offset(tira.left + 4 * u, yy), Offset(tira.right - 3 * u, yy), (if (e.enfocado) 1f else 0.6f) * u) }
        drawLine(pauta, Offset(tira.left + 5 * u, tira.top + 1.5f * u), Offset(tira.left + 5 * u, tira.bottom - 1.5f * u), 0.6f * u)
        if (e.enfocado) drawRect(pauta.copy(alpha = 0.7f), tira.topLeft, tira.size, style = Stroke(1.2f * u))
        listOf(tira.left + 2 * u, tira.right - 2 * u).forEach { x -> listOf(tira.top + 2.2f * u, tira.bottom - 2.2f * u).forEach { yy -> drawCircle(c(HIERRO), 1.1f * u, Offset(x, yy)) } }
        // El cuerno de tinta en su agujero.
        val hoyo = Offset(w * cajaCampo.left * 0.42f, h * 0.66f)
        drawOval(Color.Black.copy(alpha = 0.55f), Offset(hoyo.x - 7 * u, hoyo.y - 2.5f * u), Size(14 * u, 5 * u))
        val cuerno = Path().apply {
            moveTo(hoyo.x - 5.5f * u, hoyo.y); quadraticTo(hoyo.x - 6.5f * u, hoyo.y - 12 * u, hoyo.x - 4 * u, hoyo.y - 18 * u)
            lineTo(hoyo.x + 4 * u, hoyo.y - 18 * u); quadraticTo(hoyo.x + 6.5f * u, hoyo.y - 12 * u, hoyo.x + 5.5f * u, hoyo.y); close()
        }
        drawPath(cuerno, c(CUERNO))
        drawLine(Color.Black.copy(alpha = 0.18f), Offset(hoyo.x + 3 * u, hoyo.y - 1 * u), Offset(hoyo.x + 2.6f * u, hoyo.y - 16 * u), 1.4f * u)
        drawOval(c(TINTA), Offset(hoyo.x - 4 * u, hoyo.y - 19.4f * u), Size(8 * u, 2.8f * u))
        // La pluma: de pie en el cuerno, o inclinada hacia la tira con el foco.
        val giro = if (e.enfocado) 38f else -8f
        rotate(giro, Offset(hoyo.x, hoyo.y - 18 * u)) {
            val base = Offset(hoyo.x, hoyo.y - 14 * u)
            val pluma = Path().apply {
                moveTo(base.x, base.y)
                cubicTo(base.x - 4 * u, base.y - 10 * u, base.x - 3 * u, base.y - 22 * u, base.x + 2 * u, base.y - 30 * u)
                cubicTo(base.x + 2.4f * u, base.y - 20 * u, base.x + 2 * u, base.y - 10 * u, base.x + 0.6f * u, base.y)
                close()
            }
            drawPath(pluma, c(CERA))
            drawLine(c(PIEDRA_OSC), base, Offset(base.x + 1.6f * u, base.y - 29 * u), 0.6f * u)
        }
        if (e.vacio) pistaMedievo(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), letraCampo,
            tintaHonda(oscuro).copy(alpha = 0.5f))
    }

    /**
     * Las obras: el muro encalado del scriptorium con su zócalo pintado de cortinaje (los cortinas colgados en ocre y rojo, a
     * la manera de los ábsides románicos) y una cenefa de perlas en lo alto; abajo, el tablero de roble sobre caballetes con
     * su cortina de lino listado en rojo, donde se apoyan las obras. Distinto del estante (los atriles encadenados).
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        // En modo oscuro el muro se hunde más que el resto, para que las portadas destaquen.
        drawRect(if (oscuro) mezcla(CAL, Color(0xFF15100B), 0.66f) else CAL)
        // La cenefa de arriba: un filete ocre y una banda de lapislázuli con perlas blancas.
        drawRect(c(BALDOSA).copy(alpha = 0.5f), Offset(0f, 6 * u), Size(w, 1.4f * u))
        drawRect(c(LAPIZ).copy(alpha = 0.32f), Offset(0f, 9 * u), Size(w, 5 * u))
        var x = 4 * u; var j = 0
        while (x < w && j < 80) { drawCircle(c(CAL).copy(alpha = 0.7f), 1 * u, Offset(x, 11.5f * u)); x += maxOf(7 * u, 1f); j++ }
        // El cortinaje pintado: la orla de arriba en ondas y los pliegues que bajan.
        val top = h * 0.4f; val pie = h - 30 * u
        val cortina = Path().apply {
            moveTo(0f, top)
            var px = 0f; var k = 0
            while (px < w && k < 60) { quadraticTo(px + 11 * u, top + 9 * u, px + 22 * u, top); px += maxOf(22 * u, 1f); k++ }
            lineTo(w, pie); lineTo(0f, pie); close()
        }
        drawPath(cortina, c(BALDOSA).copy(alpha = 0.16f))
        var fx = 11 * u; var k = 0
        while (fx < w && k < 60) {
            drawLine(c(BERMELLON).copy(alpha = 0.18f), Offset(fx, top + 8 * u), Offset(fx + 2 * u, pie), 1.6f * u)
            drawLine(Color.White.copy(alpha = 0.14f), Offset(fx + 5 * u, top + 6 * u), Offset(fx + 6 * u, pie), 1 * u)
            fx += maxOf(22 * u, 1f); k++
        }
        drawRect(c(BERMELLON).copy(alpha = 0.3f), Offset(0f, top - 2 * u), Size(w, 1.2f * u))
        // El tablero de roble con su canto y su sombra en el muro.
        val tablero = Rect(0f, h - 30 * u, w, h - 22 * u)
        drawRect(Color.Black.copy(alpha = 0.16f), Offset(0f, tablero.top - 4 * u), Size(w, 4 * u))
        drawRect(c(ROBLE_LUZ), tablero.topLeft, tablero.size)
        vetas(tablero, c(VETA).copy(alpha = 0.35f), 81, densidad = 0.8f, grosor = 0.6f)
        drawRect(c(ROBLE), Offset(0f, tablero.bottom), Size(w, 5 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tablero.bottom + 5 * u), Size(w, 1.2f * u))
        // Los caballetes y el suelo de baldosas.
        drawRect(c(BALDOSA), Offset(0f, h - 15 * u), Size(w, 15 * u))
        var bx = 0f; k = 0
        while (bx < w && k < 60) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(bx, h - 15 * u), Offset(bx - 6 * u, h), 0.8f * u); bx += maxOf(16 * u, 1f); k++ }
        listOf(w * 0.12f, w * 0.88f).forEach { cx ->
            val pata = Path().apply {
                moveTo(cx - 3 * u, tablero.bottom + 5 * u); lineTo(cx - 10 * u, h); lineTo(cx - 6 * u, h); lineTo(cx, tablero.bottom + 9 * u)
                lineTo(cx + 6 * u, h); lineTo(cx + 10 * u, h); lineTo(cx + 3 * u, tablero.bottom + 5 * u); close()
            }
            drawPath(pata, c(ROBLE_OSC))
        }
        // El cortina de lino que cuelga por delante, con sus dos listas rojas y los flecos.
        val lino = Rect(w * 0.22f, tablero.top - 0.5f * u, w * 0.78f, tablero.bottom + 9 * u)
        drawRect(c(PERGAMINO), lino.topLeft, lino.size)
        listOf(lino.left + 6 * u, lino.right - 9 * u).forEach { lx -> drawRect(c(BERMELLON).copy(alpha = 0.75f), Offset(lx, lino.top), Size(3 * u, lino.height)) }
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(lino.left, tablero.bottom), Size(lino.width, 1 * u))
        var lx = lino.left + 1.5f * u; k = 0
        while (lx < lino.right && k < 120) { drawLine(c(PERGAMINO), Offset(lx, lino.bottom), Offset(lx, lino.bottom + 3 * u), 0.7f * u); lx += maxOf(2.4f * u, 1f); k++ }
    }

    /**
     * La tapa de una obra: una encuadernación a media tabla. El lomo y un tercio de la tapa en cuero (de tres tintes) con su
     * fila de clavos de latón, el resto la tabla de roble desnuda con su veta; clavada en ella, la etiqueta de pergamino
     * pautada en rojo con el tipo en rúbrica, el título en negro, el autor y el año. Abajo, la correa con su pasador.
     * En miniatura, la etiqueta con la inicial en rojo.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = t(x, oscuro, false)
        val semilla = e.semilla and 0x7fffffff
        val madera = c(TABLAS_OBRA[semilla % TABLAS_OBRA.size])
        val cuero = c(CUEROS[(semilla / 3) % CUEROS.size])
        drawRect(madera)
        vetas(Rect(0f, 0f, w, h), c(VETA).copy(alpha = 0.4f), 90 + semilla % 7, densidad = 0.9f, vertical = true, grosor = 0.6f)
        // El cuero del lomo, con su filo vuelto y la fila de clavos.
        val lomo = w * 0.2f
        drawRect(cuero, Offset.Zero, Size(lomo, h))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(lomo, 0f), Size(maxOf(1f, w * 0.015f), h))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(lomo * 0.15f, 0f), Size(lomo * 0.2f, h))
        val pasos = if (e.mini) 3 else 6
        for (j in 0 until pasos) drawCircle(c(LATON), maxOf(1f, w * 0.016f), Offset(lomo - w * 0.04f, h * (0.1f + 0.8f * j / (pasos - 1))))
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.32f), maxOf(1f, w * 0.02f))
        val papel = if (oscuro) mezcla(PERGAMINO, Color(0xFF17110B), 0.22f) else PERGAMINO
        val rubrica = if (oscuro) mezcla(BERMELLON, Color.Black, 0.15f) else BERMELLON
        if (e.mini) {
            val r = Rect(w * 0.3f, h * 0.24f, w * 0.92f, h * 0.68f)
            drawRect(papel, r.topLeft, r.size)
            drawRect(rubrica.copy(alpha = 0.8f), r.deflate(w * 0.05f).topLeft, r.deflate(w * 0.05f).size, style = Stroke(maxOf(1f, w * 0.02f)))
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifBlank { "·" }, r.deflate(w * 0.06f), letraTapa, rubrica, s * 0.42f, peso = FontWeight.Bold)
            return
        }
        // La etiqueta de pergamino, con su sombra, la pauta roja y un clavo en cada esquina.
        val et = Rect(w * 0.27f, h * 0.12f, w * 0.94f, h * 0.72f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(et.left + w * 0.02f, et.top + w * 0.025f), et.size)
        drawRect(papel, et.topLeft, et.size)
        val pauta = et.deflate(w * 0.035f)
        drawRect(rubrica.copy(alpha = 0.6f), pauta.topLeft, pauta.size, style = Stroke(maxOf(0.6f * u, w * 0.008f)))
        listOf(et.topLeft, Offset(et.right, et.top), Offset(et.left, et.bottom), et.bottomRight).forEach { o ->
            drawCircle(c(HIERRO), maxOf(1f, w * 0.02f), o + Offset(if (o.x < et.center.x) w * 0.02f else -w * 0.02f, if (o.y < et.center.y) w * 0.02f else -w * 0.02f))
        }
        val dentro = pauta.deflate(w * 0.025f)
        textoEn(medidor, e.tipo.uppercase(), Rect(dentro.left, dentro.top, dentro.right, dentro.top + dentro.height * 0.14f), letra, rubrica, s * 0.07f,
            peso = FontWeight.Bold, espaciado = s * 0.006f, minimo = 4f)
        drawLine(rubrica.copy(alpha = 0.6f), Offset(dentro.left + dentro.width * 0.25f, dentro.top + dentro.height * 0.16f),
            Offset(dentro.right - dentro.width * 0.25f, dentro.top + dentro.height * 0.16f), maxOf(0.6f * u, w * 0.006f))
        tituloMedievo(medidor, e.titulo.ifBlank { "—" }, Rect(dentro.left, dentro.top + dentro.height * 0.19f, dentro.right, dentro.top + dentro.height * 0.74f),
            letraTapa, TINTA, s * 0.13f)
        textoEn(medidor, e.autor, Rect(dentro.left, dentro.top + dentro.height * 0.76f, dentro.right, dentro.top + dentro.height * 0.89f), letra, TINTA.copy(alpha = 0.85f), s * 0.075f, minimo = 4f)
        textoEn(medidor, e.anio, Rect(dentro.left, dentro.top + dentro.height * 0.89f, dentro.right, dentro.bottom), letra, rubrica, s * 0.07f, minimo = 4f)
        // La correa de abajo, con su chapa y su pasador de latón.
        val yc = h * 0.85f
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.55f, yc - h * 0.028f + w * 0.015f), Size(w * 0.45f, h * 0.056f))
        drawRect(cuero, Offset(w * 0.55f, yc - h * 0.028f), Size(w * 0.45f, h * 0.056f))
        drawRect(c(LATON), Offset(w * 0.5f, yc - h * 0.04f), Size(w * 0.09f, h * 0.08f))
        drawCircle(c(HIERRO), maxOf(1f, w * 0.022f), Offset(w * 0.545f, yc))
        drawCircle(c(LATON), maxOf(1f, w * 0.03f), Offset(w * 0.4f, yc))
    }

    override val margenRecuerdo: Dp = 14.dp
    override val altoRotuloRecuerdo: Dp = 46.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El recuerdo: una hoja de pergamino con el borde irregular, clavada con dos clavos de hierro, con los piquetes del
     * margen y su pauta roja bajo el rótulo. Arriba, «Hace N años» en rojo de rúbrica con una capital azul, la fecha (y el
     * clima) en tinta, y a la derecha el ánimo como un medallón pintado del color del día con su aro de oro.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenRecuerdo.toPx(); val alto = altoRotuloRecuerdo.toPx()
        val hoja = Rect(1.5f * u, 1.5f * u, w - 1.5f * u, h - 1.5f * u)
        val borde = contornoIrregular(hoja, e.semilla, 30, 0.012f)
        translate(1.2f * u, 1.6f * u) { drawPath(borde, Color.Black.copy(alpha = 0.22f)) }
        drawPath(borde, pergaminoHondo(oscuro))
        // Unas manchas leves del tiempo en las orillas.
        clipPath(borde) {
            drawRect(Color(0xFF8A6A3A).copy(alpha = if (oscuro) 0.06f else 0.08f), Offset.Zero, Size(w, 3 * u))
            drawCircle(Color(0xFF8A6A3A).copy(alpha = 0.06f), 22 * u, Offset(w - 10 * u, h - 8 * u))
        }
        val tinta = tintaHonda(oscuro)
        val rojo = if (oscuro) mezcla(BERMELLON, CERA, 0.25f) else BERMELLON
        val azul = if (oscuro) mezcla(LAPIZ, CERA, 0.3f) else LAPIZ
        // Los piquetes del margen.
        var y = alto + 4 * u; var j = 0
        while (y < h - 6 * u && j < 40) { drawCircle(tinta.copy(alpha = 0.25f), 0.7f * u, Offset(6 * u, y)); y += maxOf(9 * u, 1f); j++ }
        // Los clavos.
        listOf(7 * u, w - 7 * u).forEach { x -> drawCircle(t(HIERRO, oscuro, false), 2 * u, Offset(x, 7 * u)); drawCircle(Color.White.copy(alpha = 0.25f), 0.7f * u, Offset(x - 0.6f * u, 6.4f * u)) }
        // La capital azul y «Hace N años» en rúbrica.
        val hace = haceMedievo(e.anios)
        val cap = Rect(m, 6 * u, m + 18 * u, 26 * u)
        drawRect(azul, cap.topLeft, cap.size)
        drawRect(if (oscuro) mezcla(ORO, Color.Black, 0.1f) else ORO, cap.deflate(1.2f * u).topLeft, cap.deflate(1.2f * u).size, style = Stroke(0.6f * u))
        textoEn(medidor, hace.take(1), cap, letraTapa, pergaminoHondo(false), 15f, peso = FontWeight.Bold)
        val fin = izquierdaMedievo(medidor, hace.drop(1), cap.right + 0.5f * u, 6 * u, 26 * u, w - m - cap.right - 22 * u, letra, rojo, 16f, peso = FontWeight.Bold)
        drawLine(rojo.copy(alpha = 0.5f), Offset(fin + 4 * u, 18 * u), Offset(w - m - 18 * u, 18 * u), 0.6f * u)
        // El ánimo: un medallón del color del día con su aro de oro.
        e.animo?.let { a ->
            val o = Offset(w - m - 7 * u, 16 * u)
            drawCircle(if (oscuro) mezcla(ORO, Color.Black, 0.1f) else ORO, 7.5f * u, o)
            drawCircle(a, 5.8f * u, o)
            drawCircle(Color.White.copy(alpha = 0.3f), 1.8f * u, o + Offset(-1.8f * u, -1.8f * u))
        }
        val clima = climaMedievo(e.clima)
        val linea = if (clima.isEmpty()) e.fecha else "${e.fecha} · $clima"
        izquierdaMedievo(medidor, linea, m, 27 * u, alto - 4 * u, w - 2 * m, letra, tinta.copy(alpha = 0.85f), 12f, minimo = 8f)
        // La pauta roja doble bajo el rótulo.
        drawLine(rojo.copy(alpha = 0.55f), Offset(m, alto - 3 * u), Offset(w - m, alto - 3 * u), 0.7f * u)
        drawLine(rojo.copy(alpha = 0.3f), Offset(m, alto - 1.4f * u), Offset(w - m, alto - 1.4f * u), 0.5f * u)
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
 * La tapa es ese libro de la regla de frente: la gran cruz patada, las dos correas con hebilla y sello de lacre, clavos
 * de hierro y la cartela crema para lo escrito; al abrirse gira y deja ver la regla (de noche, una luna y la antorcha).
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 186.dp
    override val cajaTapa = Rect(0.45f, 0.33f, 0.86f, 0.68f)
    override val letraTapa: FontFamily = LetraExtra.cruzada
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La crema de la cartela y de las hojas: se hunde menos que la piel, para que el extracto se lea siempre. */
    private fun crema(oscuro: Boolean, noche: Boolean): Color {
        var r = CREMA
        if (oscuro) r = mezcla(r, Color(0xFF0F0D0B), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val piel = c(BLANCOS[(e.semilla and 0x7fffffff) % BLANCOS.size])
        val hoja = crema(oscuro, n)
        val tinta = mezcla(c(TINTA), Color.Black, 0.3f)
        val libro = Rect(w * 0.025f, h * 0.05f, w * 0.965f, h * 0.92f)
        val lomo = Rect(libro.left, libro.top, libro.left + minOf(w * 0.07f, 28 * u), libro.bottom)
        val tapa = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        // La sombra sobre la mesa y el canto de las hojas.
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(libro.left + 3 * u, libro.top + 5 * u), libro.size, CornerRadius(3 * u))
        val bloque = Rect(tapa.left, tapa.top + 3 * u, tapa.right + 2.5f * u, tapa.bottom + 3 * u)
        drawRect(c(CREMA), bloque.topLeft, bloque.size)
        var cy = bloque.top + 2 * u
        while (cy < bloque.bottom - 1 * u) { drawLine(c(HOJA).copy(alpha = 0.35f), Offset(tapa.right, cy), Offset(bloque.right, cy), 0.5f * u); cy += 1.5f * u }
        for (j in 1..3) drawLine(c(HOJA).copy(alpha = 0.4f), Offset(tapa.left, tapa.bottom + j * 0.8f * u), Offset(bloque.right, tapa.bottom + j * 0.8f * u), 0.5f * u)
        // La primera hoja de la regla: la cruz roja en lo alto, el capítulo y los renglones a una columna.
        if (a > 0.01f) {
            val pag = Rect(tapa.left + 1 * u, tapa.top + 3 * u, tapa.right - 1 * u, tapa.bottom - 1 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            listOf(10f to 0.14f, 5f to 0.12f).forEach { (an, al) -> drawRect(Color.Black.copy(alpha = al), pag.topLeft, Size(an * u, pag.height)) }
            val m = Rect(pag.left + 22 * u, pag.top + 12 * u, pag.right - 18 * u, pag.bottom - 12 * u)
            drawPath(cruzPatada(Offset(m.center.x, m.top + 7 * u), 7 * u), c(ROJO))
            var ry = m.top + 22 * u; var j = 0
            while (ry < m.bottom) {
                val color = if (j == 0) c(ROJO).copy(alpha = 0.8f) else c(TINTA).copy(alpha = 0.6f)
                var x = m.left + (if (j == 0) m.width * 0.25f else 0f); var k = 0
                val fin = if (j == 0) m.right - m.width * 0.25f else if (j % 5 == 4) m.left + m.width * 0.55f else m.right
                while (x < fin - 2 * u) {
                    val largo = minOf((4f + 9f * azar(e.semilla + j, k)) * u, fin - x)
                    drawLine(color, Offset(x, ry), Offset(x + largo, ry), 1.4f * u); x += largo + 2.6f * u; k++
                }
                ry += 6.5f * u; j++
            }
        }
        // La tapa de piel blanca curtida al alumbre, que gira sobre el lomo al abrirse.
        val sx = 1f - 0.9f * a
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(piel, Color.White, 0.18f), piel, mezcla(piel, c(HOJA), 0.3f)), tapa.top, tapa.bottom),
                tapa.topLeft, tapa.size, CornerRadius(2.5f * u))
            // El roce del uso: los cantos más oscuros y algunas arrugas de la piel.
            drawRect(c(HOJA).copy(alpha = 0.22f), tapa.topLeft, tapa.size, style = Stroke(3 * u))
            for (j in 0 until 10) {
                val o = Offset(tapa.left + tapa.width * azar(e.semilla, 40 + j), tapa.top + tapa.height * azar(e.semilla, 60 + j))
                val l = (6 + 10 * azar(e.semilla, 80 + j)) * u
                drawLine(c(HOJA).copy(alpha = 0.18f), o, Offset(o.x + l, o.y + l * 0.25f), 0.7f * u)
            }
            // La gran cruz patada de la Orden, a la izquierda, con su filo.
            val cx = Offset((tapa.left + w * 0.43f) / 2, tapa.center.y)
            val rc = minOf((w * 0.43f - tapa.left) * 0.4f, tapa.height * 0.3f)
            drawPath(cruzPatada(Offset(cx.x + 1.5f * u, cx.y + 2 * u), rc), Color.Black.copy(alpha = 0.15f))
            drawPath(cruzPatada(cx, rc), c(ROJO))
            drawPath(cruzPatada(cx, rc), mezcla(c(ROJO), Color.Black, 0.35f), style = Stroke(1.2f * u))
            drawPath(cruzPatada(cx, rc * 0.82f), Color.White.copy(alpha = 0.12f), style = Stroke(0.7f * u))
            // Las dos correas de cuero que cierran el libro, con su hebilla, y los clavos de hierro de las esquinas.
            listOf(0.2f, 0.86f).forEachIndexed { k, f ->
                val y = tapa.top + tapa.height * f
                val correa = Rect(w * 0.47f, y - 4.5f * u, tapa.right + 1.5f * u, y + 4.5f * u)
                drawRect(Color.Black.copy(alpha = 0.22f), Offset(correa.left, correa.top + 1.5f * u), correa.size)
                drawRect(c(CUERO), correa.topLeft, correa.size)
                drawRect(Color.White.copy(alpha = 0.12f), correa.topLeft, Size(correa.width, 1 * u))
                var px = correa.left + 3 * u
                while (px < correa.right - 2 * u) { drawLine(c(CREMA).copy(alpha = 0.35f), Offset(px, correa.top + 1.6f * u), Offset(px + 2 * u, correa.top + 1.6f * u), 0.5f * u); px += 3.5f * u }
                drawPath(Path().apply { moveTo(correa.left, correa.top); lineTo(correa.left - 6 * u, y); lineTo(correa.left, correa.bottom); close() }, c(CUERO))
                val hx = tapa.right - (if (k == 0) 26 else 34) * u
                val hebilla = Rect(hx, y - 6.5f * u, hx + 9 * u, y + 6.5f * u)
                drawRoundRect(c(LATON), hebilla.topLeft, hebilla.size, CornerRadius(1.5f * u), style = Stroke(1.6f * u))
                drawLine(c(LATON), Offset(hebilla.left + 2 * u, y), Offset(hebilla.right + 3 * u, y), 1.2f * u)
            }
            listOf(Offset(tapa.left + 8 * u, tapa.top + 8 * u), Offset(tapa.right - 8 * u, tapa.top + 8 * u), Offset(tapa.left + 8 * u, tapa.bottom - 8 * u),
                Offset(tapa.right - 8 * u, tapa.bottom - 8 * u)).forEach { o ->
                drawCircle(Color.Black.copy(alpha = 0.25f), 3.6f * u, Offset(o.x + 0.8f * u, o.y + 1 * u))
                drawCircle(c(HIERRO), 3.4f * u, o)
                drawCircle(c(HIERRO_LUZ), 1.2f * u, Offset(o.x - 1 * u, o.y - 1 * u))
            }
            // La fecha, encima de la correa de arriba.
            val arriba = tapa.top + tapa.height * 0.2f - 6 * u
            textoEn(medidor, e.fecha, Rect(w * 0.45f, tapa.top + 4 * u, w * 0.9f, arriba), letra, tinta, 12.5f, peso = FontWeight.SemiBold, minimo = 7f)
            // La cartela crema, con su doble filete rojo y negro: ahí va lo escrito.
            val cartela = Rect(w * 0.43f, h * 0.3f, w * 0.88f, h * 0.71f)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(cartela.left + 1.5f * u, cartela.top + 2 * u), cartela.size)
            drawRect(hoja, cartela.topLeft, cartela.size)
            drawRect(tinta, cartela.topLeft, cartela.size, style = Stroke(0.9f * u))
            val filete = cartela.deflate(2.2f * u)
            drawRect(c(ROJO).copy(alpha = 0.6f), filete.topLeft, filete.size, style = Stroke(0.6f * u))
            listOf(cartela.topLeft, cartela.topRight, cartela.bottomLeft, cartela.bottomRight).forEach { p -> drawPath(cruzPatada(p, 3.4f * u), c(ROJO)) }
            // El sello de lacre en la correa de abajo, con la cuenta al lado; de noche, una luna en su lugar.
            val ys = tapa.top + tapa.height * 0.86f
            val cs = Offset(w * 0.53f, ys)
            val rs = 7 * u
            if (n) {
                drawCircle(LUNA, rs, cs)
                drawCircle(c(CUERO), rs * 0.82f, Offset(cs.x + rs * 0.42f, cs.y - rs * 0.22f))
            } else {
                drawCircle(Color.Black.copy(alpha = 0.25f), rs, Offset(cs.x + 1 * u, cs.y + 1.2f * u))
                drawCircle(c(LACRE), rs, cs)
                drawCircle(Color.Black.copy(alpha = 0.25f), rs * 0.72f, cs, style = Stroke(0.6f * u))
                drawPath(cruzPatada(cs, rs * 0.5f), Color.Black.copy(alpha = 0.3f))
            }
            if (!e.vacio && e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.45f, ys + 5.5f * u, w * 0.9f, tapa.bottom - 2 * u), letra, tinta.copy(alpha = 0.85f), 10f, minimo = 6f)
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.4f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (k in 0 until 3) drawRect(Color.Black.copy(alpha = 0.12f * (3 - k) / 3f), Offset(borde, tapa.top + 3 * u), Size((4 + 4 * k) * u, tapa.height - 4 * u))
        }
        // El lomo de piel, con sus nervios y las correas que lo cruzan.
        drawRoundRect(mezcla(piel, c(HOJA), 0.35f), lomo.topLeft, lomo.size, CornerRadius(2.5f * u))
        drawRect(Color.White.copy(alpha = 0.16f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.2f, lomo.height))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(lomo.right - 2 * u, lomo.top), Size(2 * u, lomo.height))
        listOf(0.3f, 0.5f, 0.7f).forEach { f ->
            val y = lomo.top + lomo.height * f
            drawRect(Color.White.copy(alpha = 0.25f), Offset(lomo.left, y - 2 * u), Size(lomo.width - 2 * u, 1.4f * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(lomo.left, y - 0.6f * u), Size(lomo.width - 2 * u, 1.6f * u))
        }
        listOf(0.2f, 0.86f).forEach { f -> drawRect(c(CUERO), Offset(lomo.left, lomo.top + lomo.height * f - 4.5f * u), Size(lomo.width, 9 * u)) }
        drawPath(cruzPatada(Offset(lomo.center.x - 1 * u, lomo.top + lomo.height * 0.4f), minOf(lomo.width * 0.3f, 6 * u)), c(ROJO))
        // De noche, la antorcha alumbra desde arriba a la derecha.
        if (n) clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.96f, h * 0.02f), h * 1.2f, LLAMA.copy(alpha = 0.22f)) }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 116.dp

    /**
     * Una fila de almenas de [x0] a [x1] con la base en [y]: merlones de [m] de ancho y [alto] de alto, con huecos del mismo
     * ancho. [relleno]: el muro lleno (si no, solo su contorno).
     */
    private fun DrawScope.almenas(x0: Float, x1: Float, y: Float, m: Float, alto: Float, color: Color, relleno: Boolean) {
        val camino = Path().apply {
            moveTo(x0, y)
            var x = x0
            while (x < x1) {
                val a = minOf(x + m, x1)
                lineTo(x, y - alto); lineTo(a, y - alto); lineTo(a, y)
                x = minOf(a + m, x1)
                lineTo(x, y)
            }
            if (relleno) close()
        }
        if (relleno) drawPath(camino, color) else drawPath(camino, color, style = Stroke(1f * density))
    }

    /**
     * El parte de la jornada: arriba, una fila de almenas; el recuadro de la guardia con la banda roja de la sobreveste a la
     * izquierda; el escudo blanco con la cruz patada roja (de noche, el escudo negro con la media luna y una estrella); una
     * doble raya; y a la derecha, «Parte de la jornada …» en rojo, la fecha (si es larga, en dos renglones) y el detalle,
     * sobre una raya con la cruz en medio.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        almenas(1f * u, w - 1f * u, 12f * u, 9f * u, 6f * u, p.tinta.copy(alpha = 0.55f), relleno = false)
        val marco = Rect(1f * u, 12f * u, w - 1f * u, h - 3f * u)
        drawRect(p.tinta.copy(alpha = 0.6f), marco.topLeft, marco.size, style = Stroke(1.2f * u))
        drawRect(p.acento.copy(alpha = 0.75f), Offset(marco.left + 3f * u, marco.top + 3f * u), Size(4f * u, marco.height - 6f * u))
        // El escudo.
        val ew = 42f * u; val eh = 50f * u
        val cy = marco.center.y
        val er = Rect(16f * u, cy - eh / 2, 16f * u + ew, cy + eh / 2)
        drawPath(escudo(er.translate(1.2f * u, 1.6f * u)), Color.Black.copy(alpha = 0.2f))
        if (!e.noche) {
            drawPath(escudo(er), c(BLANCO))
            drawPath(cruzPatada(Offset(er.center.x, er.top + eh * 0.42f), ew * 0.36f), c(ROJO))
        } else {
            val luna = mezcla(LUNA, p.acento, 0.2f)
            drawPath(escudo(er), c(NEGRO))
            drawPath(mediaLunaMedievo(Offset(er.center.x - 2f * u, er.top + eh * 0.42f), ew * 0.24f), luna)
            drawPath(estrella(Offset(er.center.x + ew * 0.2f, er.top + eh * 0.18f), 3f * u, 5, 0.45f), luna)
        }
        drawPath(escudo(er), p.tinta.copy(alpha = 0.7f), style = Stroke(1.2f * u))
        val xs = er.right + 8f * u
        listOf(xs, xs + 2.5f * u).forEach { drawLine(p.tinta.copy(alpha = 0.35f), Offset(it, marco.top + 8f * u), Offset(it, marco.bottom - 8f * u), 0.8f * u) }
        // El rótulo, la fecha y el detalle.
        val x0 = xs + 8f * u; val x1 = marco.right - 8f * u
        textoEn(medidor, e.rotulo, Rect(x0, 16f * u, x1, 36f * u), LetraExtra.cruzada, rotuloMedievo(p, e.noche), 15f, espaciado = 0.5f, minimo = 8f)
        fechaMedievo(medidor, e.fecha, Rect(x0, 37f * u, x1, 78f * u), LetraExtra.regla, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(x0, 79f * u, x1, 95f * u), LetraExtra.regla, p.tenue, 11f, minimo = 7f)
        val yl = h - 12f * u
        drawLine(p.tinta.copy(alpha = 0.3f), Offset(x0 + 10f * u, yl), Offset(x1 - 10f * u, yl), 0.8f * u)
        drawPath(cruzPatada(Offset((x0 + x1) / 2, yl), 3.5f * u), p.acento.copy(alpha = 0.85f))
    }

    /**
     * La regla de la encomienda: la banda roja de la sobreveste en el margen con su filete, el muro almenado arriba y abajo,
     * apenas marcado, cruces patadas en las esquinas del pie y, en el centro, casi invisible, la gran cruz como marca de
     * agua. En modo oscuro, la antorcha en su argolla de hierro, arriba a la izquierda: el papel se hunde en la sombra y la
     * llama lo tiñe de naranja.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(p.acento.copy(alpha = 0.18f), Offset(16f * u, 0f), Size(4f * u, h))
        drawLine(p.detalle.copy(alpha = 0.25f), Offset(24f * u, 0f), Offset(24f * u, h), 1f * u)
        drawLine(p.detalle.copy(alpha = 0.18f), Offset(w - 18f * u, 0f), Offset(w - 18f * u, h), 0.8f * u)
        val muro = p.detalle.copy(alpha = 0.12f)
        drawRect(muro, Offset.Zero, Size(w, 4f * u))
        almenas(0f, w, 10f * u, 12f * u, 6f * u, muro, relleno = true)
        drawRect(muro, Offset(0f, h - 8f * u), Size(w, 8f * u))
        almenas(0f, w, h - 8f * u, 12f * u, 6f * u, muro, relleno = true)
        listOf(34f * u, w - 34f * u).forEach { drawPath(cruzPatada(Offset(it, h - 26f * u), 6f * u), p.acento.copy(alpha = 0.3f)) }
        drawPath(cruzPatada(Offset(w / 2, h * 0.45f), w * 0.28f), p.tinta.copy(alpha = 0.035f))
        if (oscuro && !p.oscura) {
            val x = 12f * u
            val llama = Offset(x, 128f * u)
            penumbraMedievo(llama - Offset(0f, 8f * u), LLAMA)
            haloMedievo(llama - Offset(0f, 8f * u), 42f * u, LLAMA, 0.65f)
            val hierro = t(HIERRO_LUZ, true, false)
            drawLine(t(HIERRO, true, false), Offset(0f, 150f * u), Offset(x, 150f * u), 2.2f * u)
            drawLine(t(ROBLE, true, false), Offset(x - 1f * u, 172f * u), Offset(x, 134f * u), 3.6f * u)
            drawOval(hierro, Offset(x - 5f * u, 147f * u), Size(10f * u, 6f * u), style = Stroke(1.6f * u))
            drawRoundRect(ESTOPA, Offset(x - 4f * u, 127f * u), Size(8f * u, 10f * u), CornerRadius(2f * u))
            llamaMedievo(Offset(x, 129f * u), 22f * u, LLAMA)
            listOf(Offset(6f, 98f), Offset(17f, 92f), Offset(10f, 86f)).forEach { drawCircle(LLAMA.copy(alpha = 0.7f), 0.9f * u, Offset(it.x * u, it.y * u)) }
        }
    }

    /** La barra: dos hiladas de sillares claros con sus juntas trabadas y, al pie, la banda roja con su raya negra. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(SILLAR, p.color, 0.45f).let { if (oscuro) mezcla(it, Color.Black, 0.24f) else it }
        drawRect(base)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.06f), Color.Black.copy(alpha = 0.1f)), 0f, h))
        val junta = if (p.oscura) p.tenue.copy(alpha = 0.3f) else SILLAR_JUNTA.copy(alpha = 0.5f)
        val medio = (h - 4f * u) / 2
        drawLine(junta, Offset(0f, medio), Offset(w, medio), 0.8f * u)
        for (fila in 0..1) {
            var x = if (fila == 0) 10f * u else 29f * u
            while (x < w) { drawLine(junta, Offset(x, fila * medio), Offset(x, (fila + 1) * medio), 0.8f * u); x += 38f * u }
        }
        drawRect(p.acento.copy(alpha = 0.8f), Offset(0f, h - 4f * u), Size(w, 2.6f * u))
        drawLine(Color.Black.copy(alpha = 0.5f), Offset(0f, h - 0.7f * u), Offset(w, h - 0.7f * u), 1f * u)
    }

    /** El botón de inscribir: una placa roja de la cruz, con su filete blanco y una cruz patada blanca en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        val rojo = t(ROJO, oscuro && !p.oscura, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1f * u, 1.6f * u), r.size, CornerRadius(3f * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(rojo, Color.White, 0.1f), mezcla(rojo, Color.Black, 0.2f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(3f * u))
        drawRoundRect(BLANCO.copy(alpha = 0.75f), r.topLeft + Offset(3f * u, 3f * u), Size(r.width - 6f * u, r.height - 6f * u), CornerRadius(1.5f * u), style = Stroke(0.8f * u))
        listOf(r.left + 9f * u, r.right - 9f * u).forEach { drawPath(cruzPatada(Offset(it, r.center.y), 3.6f * u), BLANCO.copy(alpha = 0.9f)) }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = BLANCO

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 262.dp

    /** La cal del muro de la sala capitular y la cera del cirio. */
    private val CAL_CAPITULO = Color(0xFFE2D9C6)
    private val CIRIO = Color(0xFFEFE6CF)

    /** El grosor de la regla, en dp: de 4 con una hoja a 19 con 240 o más. */
    private fun grosorRegla(hojas: Int): Float = 4f + 15f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /** Un arco apuntado en [r] cuya parte curva mide [k] veces su ancho. */
    private fun ojiva(r: Rect, k: Float): Path = arcoApuntado(r, r.width * k)

    /**
     * La regla de la casa sobre el banco de roble de la sala capitular: el muro encalado con la cruz de consagración pintada
     * en su círculo, una saetera y el banco con su respaldo de tablas y arquillos, su asiento y sus patas. Sobre el asiento,
     * la regla de pie contra el respaldo, encuadernada en tablas forradas de piel partida como el estandarte (negra arriba,
     * blanca abajo) con la cruz patada roja en el corte, cantoneras y bollón de hierro, dos correas con hebilla que cruzan
     * el canto y, colgando de la cabeza de abajo, el sello de cera de la orden en sus cordones. La cartela crema lleva el
     * nombre y la cuenta. Engorda con las hojas (más canto, más cédulas por el canto y más correas). A la izquierda, el cirio
     * en su candelero de hierro; a la derecha, la espada envainada apoyada en el banco. Al abrirse, la tapa gira sobre el
     * lomo y deja ver la regla a dos columnas con sus rúbricas. En oscuro, el cirio encendido alumbra la sala.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0D0B09), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorRegla(e.hojas) * u
        val hoja = crema(oscuro, false)
        // El muro encalado, con un zócalo de losas y la cruz de consagración pintada en rojo dentro de su círculo.
        drawRect(c(CAL_CAPITULO))
        for (j in 0 until 14) mancha(Offset(w * azar(e.semilla, 10 + j), h * 0.6f * azar(e.semilla, 30 + j)), 14 * u, Color.Black.copy(alpha = 0.035f))
        val cc = Offset(w * 0.065f, h * 0.36f)
        drawCircle(c(ROJO), 12 * u, cc, style = Stroke(1.6f * u))
        drawPath(cruzPatada(cc, 9 * u), c(ROJO))
        // La saetera, a la derecha, con su derrame.
        val vano = Rect(w * 0.935f - 4 * u, h * 0.1f, w * 0.935f + 4 * u, h * 0.4f)
        drawPath(ojiva(vano.inflate(7 * u), 1.1f), c(SILLAR))
        drawPath(ojiva(vano.inflate(7 * u), 1.1f), c(SILLAR_JUNTA), style = Stroke(1 * u))
        drawPath(ojiva(vano, 1.1f), if (oscuro) Color(0xFF1A2036) else Color(0xFFD3DEE2))
        // El banco corrido de piedra al pie del muro, detrás del banco de roble.
        val poyo = h * 0.6f
        drawRect(c(SILLAR), Offset(0f, poyo), Size(w, h - poyo))
        sillares(Rect(0f, poyo, w, h), c(SILLAR_JUNTA), 16 * u, 40 * u)
        drawRect(c(SILLAR_JUNTA), Offset(0f, poyo), Size(w, 3 * u))
        val piso = h * 0.93f
        drawRect(c(LOSA), Offset(0f, piso), Size(w, h - piso))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, piso), Size(w, 2 * u))

        // ------------------------------------------------ el banco de roble
        val asiento = h * 0.77f
        val respaldo = Rect(w * 0.15f, h * 0.12f, w * 0.85f, asiento)
        drawRect(Color.Black.copy(alpha = 0.2f), respaldo.topLeft + Offset(4 * u, 4 * u), respaldo.size)
        drawRect(c(ROBLE_OSC), respaldo.topLeft, respaldo.size)
        // Las tablas del respaldo y, arriba, una arquería ciega de medio punto.
        var tx = respaldo.left
        while (tx < respaldo.right) {
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(tx, respaldo.top), Offset(tx, respaldo.bottom), 1 * u)
            tx += 24 * u
        }
        vetas(respaldo, c(VETA).copy(alpha = 0.45f), 17, densidad = 0.5f, vertical = true, grosor = 0.7f)
        val friso = Rect(respaldo.left, respaldo.top, respaldo.right, respaldo.top + 22 * u)
        drawRect(c(ROBLE), friso.topLeft, friso.size)
        var ax = friso.left + 6 * u
        while (ax < friso.right - 14 * u) { drawPath(arcoMedioPunto(Rect(ax, friso.top + 5 * u, ax + 12 * u, friso.bottom - 3 * u)), c(ROBLE_OSC)); ax += 17 * u }
        drawRect(c(ROBLE_LUZ), friso.topLeft, Size(friso.width, 3 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(friso.left, friso.bottom - 2 * u), Size(friso.width, 2 * u))
        // Los postes de las puntas, con su bola.
        listOf(respaldo.left, respaldo.right).forEach { x ->
            val poste = Rect(x - 6 * u, respaldo.top - 6 * u, x + 6 * u, piso)
            drawRect(c(ROBLE), poste.topLeft, poste.size)
            bisel(poste, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 1.4f * u)
            drawCircle(c(ROBLE_LUZ), 7 * u, Offset(x, respaldo.top - 10 * u))
            drawCircle(Color.Black.copy(alpha = 0.25f), 7 * u, Offset(x, respaldo.top - 10 * u), style = Stroke(1 * u))
        }

        // ------------------------------------------------ el cirio en su candelero, a la izquierda
        val pie = Offset(w * 0.215f, asiento)
        drawOval(c(HIERRO), Offset(pie.x - 12 * u, pie.y - 4 * u), Size(24 * u, 6 * u))
        drawRect(c(HIERRO), Offset(pie.x - 1.6f * u, pie.y - 38 * u), Size(3.2f * u, 36 * u))
        listOf(0.3f, 0.65f).forEach { f -> drawOval(c(HIERRO_LUZ), Offset(pie.x - 4 * u, pie.y - 38 * u * f - 2 * u), Size(8 * u, 4 * u)) }
        drawOval(c(HIERRO), Offset(pie.x - 9 * u, pie.y - 41 * u), Size(18 * u, 5 * u))
        val cirio = Rect(pie.x - 4 * u, pie.y - 82 * u, pie.x + 4 * u, pie.y - 39 * u)
        drawRect(if (oscuro) mezcla(CIRIO, Color.Black, 0.15f) else CIRIO, cirio.topLeft, cirio.size)
        drawRect(Color.Black.copy(alpha = 0.14f), Offset(cirio.right - 2.4f * u, cirio.top), Size(2.4f * u, cirio.height))
        drawPath(cruzPatada(Offset(cirio.center.x, cirio.top + cirio.height * 0.55f), 3 * u), c(ROJO))
        drawLine(TINTA, Offset(cirio.center.x, cirio.top), Offset(cirio.center.x, cirio.top - 2.4f * u), 0.9f * u)
        val llama = Offset(cirio.center.x, cirio.top - 1.6f * u)
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to LLAMA.copy(alpha = 0.32f), 0.35f to LLAMA.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.9f))
            haloMedievo(llama - Offset(0f, 5 * u), 28 * u, LLAMA, 0.7f)
            llamaMedievo(llama, 12 * u, LLAMA)
        }

        // ------------------------------------------------ la regla, de pie contra el respaldo
        val tapa = Rect(w * 0.29f, h * 0.17f + g * 0.5f, w * 0.79f - g, asiento - 1 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + g + 4 * u, tapa.top - g * 0.5f + 4 * u), tapa.size)
        drawRect(c(NEGRO), Offset(tapa.left + g, tapa.top - g * 0.5f), tapa.size)
        val bloque = Path().apply {
            moveTo(tapa.left + 3 * u, tapa.top); lineTo(tapa.left + 3 * u + g, tapa.top - g * 0.5f + 1.5f * u)
            lineTo(tapa.right + g - 1.5f * u, tapa.top - g * 0.5f + 1.5f * u); lineTo(tapa.right + g - 1.5f * u, tapa.bottom - g * 0.5f - 1 * u)
            lineTo(tapa.right, tapa.bottom - 1 * u); lineTo(tapa.right, tapa.top); close()
        }
        drawPath(bloque, c(CREMA))
        val capas = (g / (1.6f * u)).toInt().coerceAtLeast(2)
        for (j in 1..capas) {
            val f = j / (capas + 1f)
            drawLine(c(TINTA).copy(alpha = 0.16f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.bottom - g * f * 0.5f), 0.5f * u)
            drawLine(c(TINTA).copy(alpha = 0.12f), Offset(tapa.left + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), 0.5f * u)
        }
        // Las cédulas que asoman por el canto, más cuantas más hojas.
        val cedulas = (e.hojas / 35).coerceIn(0, 5)
        for (j in 0 until cedulas) {
            val cy = tapa.top + tapa.height * (0.12f + 0.75f * azar(e.semilla, 40 + j))
            drawRect(hoja, Offset(tapa.right + g * 0.5f, cy), Size(g * 0.5f + (5 + 5 * azar(e.semilla, 50 + j)) * u, 7 * u))
            drawRect(c(TINTA).copy(alpha = 0.25f), Offset(tapa.right + g * 0.5f, cy), Size(g * 0.5f + (5 + 5 * azar(e.semilla, 50 + j)) * u, 7 * u), style = Stroke(0.5f * u))
        }
        // La primera hoja de la regla: dos columnas con sus rúbricas rojas.
        if (a > 0.01f) {
            val pag = tapa.deflate(2 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            drawRect(Color.Black.copy(alpha = 0.14f), pag.topLeft, Size(8 * u, pag.height))
            val m = Rect(pag.left + 14 * u, pag.top + 12 * u, pag.right - 10 * u, pag.bottom - 12 * u)
            val col = (m.width - 10 * u) / 2
            for (q in 0..1) {
                val x0 = m.left + q * (col + 10 * u)
                var ry = m.top + 4 * u; var j = 0
                while (ry < m.bottom) {
                    val rubrica = j % 7 == 0
                    var x = x0 + (if (rubrica) 0f else 3 * u); var k = 0
                    while (x < x0 + col - 3 * u) {
                        val largo = minOf((4f + 9f * azar(e.semilla + q * 40 + j, k)) * u, x0 + col - x)
                        drawLine(if (rubrica) c(ROJO) else c(TINTA).copy(alpha = 0.55f), Offset(x, ry), Offset(x + largo, ry), 1.2f * u)
                        x += largo + 2.2f * u; k++
                    }
                    ry += 5.5f * u; j++
                }
            }
            drawPath(cruzPatada(Offset(m.left + 5 * u, m.top - 5 * u), 4 * u), c(ROJO))
        }
        // La tapa: piel partida como el estandarte, negra arriba y blanca abajo, que gira sobre el lomo.
        val sx = 1f - 0.94f * a
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            val corte = tapa.top + tapa.height * 0.44f
            drawRect(c(NEGRO), tapa.topLeft, Size(tapa.width, corte - tapa.top))
            drawRect(c(BLANCO), Offset(tapa.left, corte), Size(tapa.width, tapa.bottom - corte))
            for (j in 0 until 8) {
                val o = Offset(tapa.left + tapa.width * azar(e.semilla, 60 + j), corte + (tapa.bottom - corte) * azar(e.semilla, 70 + j))
                drawOval(Color.Black.copy(alpha = 0.05f), Offset(o.x - 6 * u, o.y - 3 * u), Size(12 * u, 6 * u))
            }
            bisel(tapa, Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.35f), 2 * u)
            // Los nervios del lomo, sobre la piel.
            listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { f ->
                val yy = tapa.top + tapa.height * f
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left, yy - 1 * u), Size(tapa.width * 0.09f, 2 * u))
            }
            // La cruz patada en el corte de los dos campos.
            val cruz = Offset(tapa.left + tapa.width * 0.55f, corte - tapa.height * 0.06f)
            val rc = minOf(tapa.width * 0.2f, tapa.height * 0.2f)
            drawPath(cruzPatada(cruz + Offset(1.2f * u, 1.6f * u), rc), Color.Black.copy(alpha = 0.35f))
            drawPath(cruzPatada(cruz, rc), c(ROJO))
            drawPath(cruzPatada(cruz, rc), mezcla(c(ROJO), Color.Black, 0.35f), style = Stroke(1 * u))
            // Las cantoneras de hierro y el bollón del centro.
            val e0 = minOf(tapa.width, tapa.height) * 0.12f
            listOf(Offset(tapa.left, tapa.top) to 0f, Offset(tapa.right, tapa.top) to 90f, Offset(tapa.right, tapa.bottom) to 180f, Offset(tapa.left, tapa.bottom) to 270f).forEach { (o, giro) ->
                rotate(giro, o) {
                    drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + e0, o.y); lineTo(o.x, o.y + e0); close() }, c(HIERRO))
                    drawCircle(c(HIERRO_LUZ), 1.2f * u, Offset(o.x + e0 * 0.3f, o.y + e0 * 0.3f))
                }
            }
            // La cartela crema con el nombre y la cuenta, en marco de hierro, en el campo blanco.
            val et = Rect(tapa.left + tapa.width * 0.16f, corte + (tapa.bottom - corte) * 0.12f, tapa.right - tapa.width * 0.08f, corte + (tapa.bottom - corte) * 0.62f)
            drawRect(Color.Black.copy(alpha = 0.25f), et.topLeft + Offset(1.4f * u, 1.8f * u), et.size)
            drawRect(hoja, et.topLeft, et.size)
            drawRect(c(HIERRO), et.topLeft, et.size, style = Stroke(1.6f * u))
            drawRect(c(ROJO).copy(alpha = 0.6f), et.deflate(3 * u).topLeft, et.deflate(3 * u).size, style = Stroke(0.6f * u))
            val m2 = et.top + et.height * 0.56f
            textoEn(medidor, e.nombre, Rect(et.left + 6 * u, et.top + 3 * u, et.right - 6 * u, m2), letraTapa, c(TINTA), 15f, minimo = 8f)
            textoEn(medidor, e.cuenta, Rect(et.left + 6 * u, m2, et.right - 6 * u, et.bottom - 4 * u), letra, c(TINTA).copy(alpha = 0.8f), 10f, minimo = 6f)
            listOf(et.topLeft, Offset(et.right, et.top), Offset(et.left, et.bottom), et.bottomRight).forEach { drawCircle(c(HIERRO_LUZ), 1.3f * u, it) }
            // Las correas con su hebilla de hierro: una más cuando el libro engorda.
            val correas = if (e.hojas >= 120) listOf(0.18f, 0.83f, 0.5f) else listOf(0.18f, 0.83f)
            correas.forEach { f ->
                val yc = tapa.top + tapa.height * f
                val desde = tapa.right - tapa.width * 0.26f
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(desde, yc - 3 * u), Size(tapa.right - desde, 8 * u))
                drawRect(c(CUERO), Offset(desde, yc - 4 * u), Size(tapa.right - desde + g * 0.3f, 8 * u))
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(desde, yc + 2.6f * u), Offset(tapa.right, yc + 2.6f * u), 0.6f * u)
                val heb = Rect(desde - 2 * u, yc - 6.5f * u, desde + 8 * u, yc + 6.5f * u)
                drawRect(c(HIERRO), heb.topLeft, heb.size, style = Stroke(1.8f * u))
                drawLine(c(HIERRO_LUZ), Offset(heb.center.x, heb.top + 1 * u), Offset(heb.center.x, heb.bottom - 1 * u), 1.2f * u)
                drawRect(c(CUERO), Offset(desde - 12 * u, yc - 4 * u), Size(10 * u, 8 * u))
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (j in 0 until 3) drawRect(Color.Black.copy(alpha = 0.1f * (3 - j) / 3f), Offset(borde, tapa.top), Size((3 + 4 * j) * u, tapa.height))
        }
        drawRoundRect(c(NEGRO), Offset(tapa.left - 6 * u, tapa.top - 1 * u), Size(7 * u, tapa.height + 1 * u), CornerRadius(3 * u))
        // El asiento del banco, por delante: el canto con su moldura, el faldón y las patas.
        drawRect(c(ROBLE_LUZ), Offset(w * 0.11f, asiento), Size(w * 0.78f, 7 * u))
        drawRect(c(ROBLE), Offset(w * 0.11f, asiento + 7 * u), Size(w * 0.78f, 9 * u))
        vetas(Rect(w * 0.11f, asiento, w * 0.89f, asiento + 16 * u), c(VETA).copy(alpha = 0.4f), 23, densidad = 1f, grosor = 0.6f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.11f, asiento + 14 * u), Size(w * 0.78f, 2 * u))
        listOf(w * 0.16f, w * 0.84f).forEach { x ->
            drawRect(c(ROBLE), Offset(x - 7 * u, asiento + 16 * u), Size(14 * u, piso - asiento - 14 * u))
            drawRect(Color.Black.copy(alpha = 0.28f), Offset(x + 3 * u, asiento + 16 * u), Size(4 * u, piso - asiento - 14 * u))
        }
        // El sello de cera de la orden, colgando de la cabeza de abajo en sus cordones.
        val sello = Offset(tapa.left + tapa.width * 0.5f, asiento + 22 * u)
        listOf(-5f, 5f).forEach { dx -> drawLine(c(Color(0xFFC9B07A)), Offset(sello.x + dx * u, tapa.bottom - 4 * u), sello, 1.3f * u) }
        drawCircle(Color.Black.copy(alpha = 0.3f), 10 * u, sello + Offset(1.2f * u, 1.6f * u))
        drawCircle(c(LACRE), 10 * u, sello)
        drawCircle(mezcla(c(LACRE), Color.Black, 0.3f), 7.5f * u, sello, style = Stroke(1 * u))
        drawPath(cruzPatada(sello, 5 * u), mezcla(c(LACRE), Color.Black, 0.4f))
        // La espada envainada, apoyada en el banco a la derecha.
        rotate(8f, Offset(w * 0.875f, piso)) {
            val x = w * 0.875f
            drawRoundRect(c(CUERO), Offset(x - 3.5f * u, h * 0.3f), Size(7 * u, piso - h * 0.3f), CornerRadius(3 * u))
            drawRect(c(HIERRO_LUZ), Offset(x - 4 * u, piso - 10 * u), Size(8 * u, 8 * u))
            drawRect(c(HIERRO_LUZ), Offset(x - 14 * u, h * 0.3f - 4 * u), Size(28 * u, 4 * u))
            drawRect(c(CUERO), Offset(x - 2.5f * u, h * 0.3f - 22 * u), Size(5 * u, 18 * u))
            drawCircle(c(HIERRO_LUZ), 5 * u, Offset(x, h * 0.3f - 25 * u))
        }
    }

    /**
     * La pestaña de un día: una ficha de crema con la cabecera partida como el estandarte (negro y blanco, con la letra de
     * la semana), el día en la letra de la orden y, colgando del pie, el sello del color del ánimo con su cruz (sin ánimo,
     * un sello sin cera, solo el cordón y el anillo).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val ficha = Rect(3 * u, 2 * u, w - 3 * u, h - 11 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), ficha.topLeft + Offset(1.2f * u, 1.6f * u), ficha.size)
        drawRect(crema(oscuro, false), ficha.topLeft, ficha.size)
        val cab = Rect(ficha.left, ficha.top, ficha.right, ficha.top + 12 * u)
        val izquierda = (e.semilla and 1) == 0
        drawRect(c(NEGRO), cab.topLeft, Size(cab.width / 2, cab.height))
        drawRect(c(BLANCO), Offset(cab.center.x, cab.top), Size(cab.width / 2, cab.height))
        drawRect(c(NEGRO), cab.topLeft, cab.size, style = Stroke(0.8f * u))
        textoEn(medidor, e.semana, if (izquierda) Rect(cab.left, cab.top, cab.center.x, cab.bottom) else Rect(cab.center.x, cab.top, cab.right, cab.bottom),
            letra, if (izquierda) c(BLANCO) else c(NEGRO), 9f, peso = FontWeight.Bold)
        drawPath(cruzPatada(if (izquierda) Offset(cab.right - cab.width / 4, cab.center.y) else Offset(cab.left + cab.width / 4, cab.center.y), 4 * u), c(ROJO))
        textoEn(medidor, e.dia, Rect(ficha.left, cab.bottom + 1 * u, ficha.right, ficha.bottom - 3 * u), letraTapa, c(TINTA), 18f, minimo = 10f)
        drawRect(c(HIERRO), ficha.topLeft, ficha.size, style = Stroke(0.8f * u))
        // El sello del ánimo, colgado de sus cordones.
        val sello = Offset(w / 2, h - 7 * u)
        listOf(-3f, 3f).forEach { dx -> drawLine(c(Color(0xFFC9B07A)), Offset(sello.x + dx * u, ficha.bottom - 2 * u), sello, 1 * u) }
        if (e.tono != null) {
            drawCircle(Color.Black.copy(alpha = 0.3f), 6.5f * u, sello + Offset(0.8f * u, 1 * u))
            drawCircle(e.tono, 6.5f * u, sello)
            drawCircle(Color.Black.copy(alpha = 0.3f), 4.8f * u, sello, style = Stroke(0.7f * u))
            drawPath(cruzPatada(sello, 3 * u), Color.Black.copy(alpha = 0.3f))
        } else {
            drawCircle(c(HIERRO_LUZ), 3.5f * u, sello, style = Stroke(1 * u))
        }
    }

    /**
     * El rótulo de un mes: la banda del estandarte de la orden, blanca entre dos fajas negras, con el mes en la letra de
     * la orden y, en cada punta, una cruz patada roja en su cuadro blanco con las lenguas del estandarte.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val banda = Rect(14 * u, 3 * u, w - 14 * u, h - 3 * u)
        // Las lenguas de las puntas, en cola de golondrina.
        listOf(-1f, 1f).forEach { s ->
            val x0 = if (s < 0) banda.left + 2 * u else banda.right - 2 * u
            val x1 = if (s < 0) 2 * u else w - 2 * u
            drawPath(Path().apply {
                moveTo(x0, banda.top + 3 * u); lineTo(x1, banda.top + 3 * u); lineTo(x1 - s * 7 * u, h / 2)
                lineTo(x1, banda.bottom - 3 * u); lineTo(x0, banda.bottom - 3 * u); close()
            }, c(NEGRO))
        }
        drawRect(Color.Black.copy(alpha = 0.25f), banda.topLeft + Offset(1.2f * u, 1.6f * u), banda.size)
        drawRect(c(BLANCO), banda.topLeft, banda.size)
        drawRect(c(NEGRO), banda.topLeft, Size(banda.width, 4 * u))
        drawRect(c(NEGRO), Offset(banda.left, banda.bottom - 4 * u), Size(banda.width, 4 * u))
        listOf(banda.left + 14 * u, banda.right - 14 * u).forEach { x -> drawPath(cruzPatada(Offset(x, h / 2), 7 * u), c(ROJO)) }
        textoEn(medidor, texto, Rect(banda.left + 28 * u, banda.top + 4 * u, banda.right - 28 * u, banda.bottom - 4 * u), letraTapa, c(TINTA), 15f, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** La cera parda del sello de la casa y el cáñamo del cordón. */
    private val CERA_CASA = Color(0xFF6E3A1E)
    private val CANAMO = Color(0xFFA08A5E)

    /**
     * El sello de la casa en [o], de radio [r]: la cera parda con su gráfila de perlas y, al centro, dos freires sobre un
     * mismo caballo, con sus lanzas.
     */
    private fun DrawScope.selloCasa(o: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        val cera = if (oscuro) mezcla(CERA_CASA, Color.Black, 0.25f) else CERA_CASA
        val hondo = mezcla(cera, Color.Black, 0.45f); val claro = mezcla(cera, Color.White, 0.3f)
        translate(r * 0.06f, r * 0.12f) { drawPath(gotaMedievo(o, r, semilla + 3), Color.Black.copy(alpha = 0.32f)) }
        drawPath(gotaMedievo(o, r, semilla + 3), cera)
        drawCircle(hondo, r * 0.76f, o, style = Stroke(r * 0.06f))
        for (j in 0 until 26) drawCircle(claro.copy(alpha = 0.7f), r * 0.04f, polarMedievo(o, r * 0.86f, j * 360f / 26))
        // Los dos freires sobre un mismo caballo: primero el relieve claro, luego el hueco.
        listOf(claro.copy(alpha = 0.5f) to Offset(-r * 0.03f, -r * 0.04f), hondo to Offset.Zero).forEach { (col, d) ->
            val y0 = o.y + r * 0.14f + d.y; val x = o.x + d.x
            drawOval(col, Offset(x - r * 0.4f, y0 - r * 0.12f), Size(r * 0.72f, r * 0.26f))
            drawPath(Path().apply {
                moveTo(x + r * 0.22f, y0 - r * 0.06f); lineTo(x + r * 0.44f, y0 - r * 0.34f); lineTo(x + r * 0.56f, y0 - r * 0.27f); lineTo(x + r * 0.36f, y0 + r * 0.04f); close()
            }, col)
            listOf(-0.32f, -0.2f, 0.12f, 0.24f).forEachIndexed { i, fx ->
                drawLine(col, Offset(x + r * fx, y0 + r * 0.08f), Offset(x + r * (fx + if (i % 2 == 0) -0.05f else 0.05f), y0 + r * 0.42f), r * 0.06f, StrokeCap.Round)
            }
            drawLine(col, Offset(x - r * 0.4f, y0 - r * 0.02f), Offset(x - r * 0.54f, y0 + r * 0.24f), r * 0.05f, StrokeCap.Round)
            listOf(-0.16f, 0.06f).forEach { fx ->
                drawRoundRect(col, Offset(x + r * fx - r * 0.07f, y0 - r * 0.46f), Size(r * 0.14f, r * 0.36f), CornerRadius(r * 0.05f))
                drawCircle(col, r * 0.075f, Offset(x + r * fx, y0 - r * 0.54f))
                drawLine(col, Offset(x + r * fx - r * 0.02f, y0 - r * 0.3f), Offset(x + r * fx + r * 0.3f, y0 - r * 0.72f), r * 0.035f, StrokeCap.Round)
            }
        }
        drawArc(Color.White.copy(alpha = 0.2f), 205f, 70f, false, o - Offset(r * 0.88f, r * 0.88f), Size(r * 1.76f, r * 1.76f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /** Un tramo de cordón de cáñamo de [a] a [b], de grosor [g], con su torcido. */
    private fun DrawScope.canamo(a: Offset, b: Offset, g: Float, color: Color) {
        drawLine(mezcla(color, Color.Black, 0.35f), a + Offset(0f, g * 0.15f), b + Offset(0f, g * 0.15f), g, StrokeCap.Round)
        drawLine(color, a, b, g * 0.82f, StrokeCap.Round)
        val largo = (b - a).getDistance(); val n = (largo / (g * 0.9f)).toInt()
        if (n <= 0) return
        val dir = (b - a) / largo; val per = Offset(-dir.y, dir.x)
        for (j in 0 until n) {
            val m = a + dir * (largo * (j + 0.5f) / n)
            drawLine(mezcla(color, Color.Black, 0.3f), m + per * (g * 0.38f) - dir * (g * 0.22f), m - per * (g * 0.38f) + dir * (g * 0.22f), g * 0.16f)
        }
    }

    /**
     * La carta va en la cartera de despachos de la encomienda: una funda de cuero pardo con su pespunte y una solapa forrada
     * de lino blanco con el borde almenado y la cruz patada roja; por dentro, la solapa es roja. Las selladas al futuro van
     * ceñidas por un cordón de cáñamo con su lazo. El sello de la casa, de cera parda con los dos freires sobre un caballo,
     * une la almena del medio con la funda. A quién va y las fechas, en la cédula de pergamino remachada en latón. Al abrirse
     * el sello se parte, la solapa se levanta enseñando su forro rojo y sale la carta. En oscuro el cuero y el lino se hunden;
     * la cédula se atenúa poco, para que se lea.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0D0B09), 0.58f) else x
        val p = Rect(c.left + W * 0.06f, c.top + H * 0.07f, c.right - W * 0.06f, c.bottom - H * 0.07f)
        val cx = p.center.x
        val largo = p.height * 0.42f
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val punta = Offset(cx, p.top + largo * 0.94f)
        val rs = minOf(W, H) * 0.11f
        val partido = e.romper.coerceIn(0f, 1f)
        // La solapa almenada: cinco almenas, la del medio en el centro; [g] es el giro (1 cerrada, -1 vuelta hacia atrás).
        fun solapa(g: Float) = Path().apply {
            val n = 9; val paso = p.width / n
            moveTo(p.left, p.top); lineTo(p.left, p.top + g * largo * 0.78f)
            for (j in 0 until n) {
                val y = p.top + g * largo * (if (j % 2 == 0) 1f else 0.78f)
                lineTo(p.left + paso * j, y); lineTo(p.left + paso * (j + 1), y)
            }
            lineTo(p.right, p.top); close()
        }
        fun DrawScope.selloEn(g: Float, mitad: Int) {
            val o = Offset(cx, p.top + g * largo * 0.94f)
            conAlfaMedievo(e.alfaSello.coerceIn(0f, 1f), Rect(punta, rs * 4f * e.escalaSello)) {
                scale(e.escalaSello, punta) {
                    if (mitad == 0 && e.brillo > 0f) {
                        drawCircle(Brush.radialGradient(0f to Color(0xFFF6D27A).copy(alpha = 0.75f * e.brillo), 0.45f to LLAMA.copy(alpha = 0.28f * e.brillo),
                            1f to Color.Transparent, center = o, radius = rs * 2.8f), rs * 2.8f, o)
                        rayosMedievo(o, rs * 1.25f, rs * 2.3f, Color(0xFFF6D27A).copy(alpha = 0.9f * e.brillo), 12, 1.1f)
                    }
                    when {
                        partido <= 0f -> selloCasa(o, rs, oscuro, e.semilla)
                        mitad == 1 -> clipPath(mitadSelloMedievo(o, rs, true, e.semilla)) {
                            translate(0f, -partido * rs * 0.18f) { rotate(-8f * partido, o) { selloCasa(o, rs, oscuro, e.semilla) } }
                        }
                        else -> clipPath(mitadSelloMedievo(o, rs, false, e.semilla)) {
                            translate(0f, partido * rs * 0.1f) { rotate(6f * partido, o) { selloCasa(o, rs, oscuro, e.semilla) } }
                        }
                    }
                }
            }
        }

        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(p.left + 2 * u, p.top + 3 * u), p.size, CornerRadius(4 * u))
        // Abierta: la solapa vuelta hacia atrás, con su forro rojo y la mitad del sello que se llevó.
        if (giro < 0f) {
            drawPath(solapa(giro), k(ROJO))
            drawPath(solapa(giro), k(mezcla(ROJO, Color.Black, 0.4f)), style = Stroke(1 * u))
            if (partido > 0f) selloEn(giro, 1)
        }
        val sale = salidaMedievo(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaMedievo(Rect(cx - p.width * 0.42f, p.top + 3 * u - sube, cx + p.width * 0.42f, p.bottom - 6 * u - sube), e, oscuro, barbas = true)
        }
        // La funda de cuero, con su pespunte y el canto más oscuro.
        drawRoundRect(k(CUERO), p.topLeft, p.size, CornerRadius(4 * u))
        clipRect(p.left, p.top, p.right, p.bottom) {
            for (j in 0 until 8) drawCircle(Color.Black.copy(alpha = 0.04f), p.height * (0.1f + 0.12f * azar(e.semilla, 80 + j)),
                Offset(p.left + p.width * azar(e.semilla, 90 + j), p.top + p.height * azar(e.semilla, 100 + j)))
            drawRect(Color.White.copy(alpha = 0.06f), p.topLeft, Size(p.width, 3 * u))
        }
        drawRoundRect(k(CREMA).copy(alpha = 0.55f), p.topLeft + Offset(3.5f * u, 3.5f * u), Size(p.width - 7 * u, p.height - 7 * u), CornerRadius(2.5f * u),
            style = Stroke(0.9f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.2f * u))))
        drawRoundRect(Color.Black.copy(alpha = 0.4f), p.topLeft, p.size, CornerRadius(4 * u), style = Stroke(1 * u))
        // La cédula de pergamino remachada, con lo escrito.
        val ced = Rect(p.left + 10 * u, punta.y + rs * 1.25f, p.right - 10 * u, p.bottom - 6 * u)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            drawRect(Color.Black.copy(alpha = 0.3f), ced.topLeft + Offset(1.2f * u, 1.6f * u), ced.size)
            drawRect(if (oscuro) mezcla(CREMA, Color(0xFF0D0B09), 0.24f) else CREMA, ced.topLeft, ced.size)
            drawRect(k(ROJO).copy(alpha = 0.7f), ced.topLeft + Offset(2.5f * u, 2.5f * u), Size(ced.width - 5 * u, ced.height - 5 * u), style = Stroke(0.8f * u))
            listOf(ced.topLeft, Offset(ced.right, ced.top), Offset(ced.left, ced.bottom), ced.bottomRight).forEach {
                drawCircle(k(LATON), 1.8f * u, it); drawCircle(Color.Black.copy(alpha = 0.35f), 1.8f * u, it, style = Stroke(0.5f * u))
            }
            val m = ced.top + ced.height * 0.56f
            textoEn(medidor, e.linea1, Rect(ced.left + 6 * u, ced.top + 2 * u, ced.right - 6 * u, m), letra, TINTA, 12f, peso = FontWeight.SemiBold, minimo = 8f)
            textoEn(medidor, e.linea2, Rect(ced.left + 6 * u, m - 1 * u, ced.right - 6 * u, ced.bottom - 2 * u), letra, mezcla(ROJO, Color.Black, 0.25f), 9f, minimo = 7f)
        }
        // Cerrada: la solapa de lino blanco por delante, con su cruz patada.
        if (giro >= 0f) {
            translate(0f, 2 * u) { drawPath(solapa(giro), Color.Black.copy(alpha = 0.28f)) }
            drawPath(solapa(giro), k(BLANCO))
            clipPath(solapa(giro)) {
                var yy = p.top + 2 * u
                while (yy < p.top + largo) { drawLine(Color.Black.copy(alpha = 0.035f), Offset(p.left, yy), Offset(p.right, yy), 0.6f * u); yy += 2 * u }
                if (giro > 0.2f) scale(1f, giro, Offset(cx, p.top)) { drawPath(cruzPatada(Offset(cx, p.top + largo * 0.4f), largo * 0.27f), k(ROJO)) }
            }
            drawPath(solapa(giro), k(Color(0xFF8C826F)), style = Stroke(1 * u))
        }
        // El cordón de cáñamo que ciñe la funda, con su lazo a un lado del sello.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val g = maxOf(2.6f * u, H * 0.032f)
            val tramo = (cx - p.left + 2 * u) * ata
            canamo(Offset(p.left - 2 * u, punta.y), Offset(p.left - 2 * u + tramo, punta.y), g, k(CANAMO))
            canamo(Offset(p.right + 2 * u - tramo, punta.y), Offset(p.right + 2 * u, punta.y), g, k(CANAMO))
            if (ata > 0.85f) {
                val f = (ata - 0.85f) / 0.15f
                val o = punta + Offset(rs * 1.3f, 0f)
                canamo(o, o + Offset(rs * 0.6f * f, rs * 1.4f * f), g * 0.8f, k(CANAMO))
                canamo(o, o + Offset(rs * 1.3f * f, rs * 1.1f * f), g * 0.8f, k(CANAMO))
                drawCircle(k(mezcla(CANAMO, Color.Black, 0.2f)), g * 0.9f, o)
            }
        }
        if (partido <= 0f) selloEn(maxOf(giro, 0f), 0)
        else {
            selloEn(1f, 0)
            if (giro >= 0f) selloEn(giro, 1)
        }
    }

    override val cajaPrevia = Rect(0.17f, 0.075f, 0.83f, 0.245f)

    override val altoBuzon: Dp = 204.dp

    /**
     * El buzón es la viga del correo de la encomienda: contra el muro de sillares, una viga de roble con sus escuadras de
     * hierro y una hilera de ganchos de los que cuelgan las bolsas de cuero de los despachos, con su solapa y la cruz. Las que
     * ya se pueden abrir llevan el sello encendido; las selladas, el cordón de cáñamo y el sello pardo; las leídas, la solapa
     * abierta con su forro rojo y el pliego asomando. Debajo, tres escudos colgados de sus clavijas con la cuenta: el jefe
     * rojo para las que se pueden abrir, el negro para las selladas y el pardo para las leídas. En oscuro, una antorcha.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0D0B09), 0.58f) else x
        val piso = h * 0.95f
        drawRect(k(SILLAR))
        sillares(Rect(0f, 0f, w, piso), k(SILLAR_JUNTA), 23 * u, 46 * u)
        drawRect(k(LOSA), Offset(0f, piso), Size(w, h - piso))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, piso), Size(w, 1.5f * u))
        // La antorcha en su argolla, a la derecha; en oscuro, encendida.
        val ant = Offset(w * 0.955f, h * 0.62f)
        if (oscuro) drawRect(Brush.radialGradient(0f to LLAMA.copy(alpha = 0.34f), 0.4f to LLAMA.copy(alpha = 0.12f), 1f to Color.Transparent,
            center = ant - Offset(0f, 18 * u), radius = w * 0.8f))
        drawLine(k(HIERRO), ant + Offset(-6 * u, 6 * u), ant + Offset(6 * u, 6 * u), 2.4f * u)
        drawPath(Path().apply { moveTo(ant.x - 3 * u, ant.y + 22 * u); lineTo(ant.x + 3 * u, ant.y + 22 * u); lineTo(ant.x + 5 * u, ant.y - 8 * u); lineTo(ant.x - 5 * u, ant.y - 8 * u); close() }, k(ROBLE_OSC))
        drawOval(k(ESTOPA), Offset(ant.x - 6 * u, ant.y - 14 * u), Size(12 * u, 8 * u))
        if (oscuro) {
            haloMedievo(ant - Offset(0f, 20 * u), 22 * u, LLAMA, 0.7f)
            llamaMedievo(ant - Offset(0f, 11 * u), 16 * u, LLAMA)
        }
        // La viga, con sus escuadras.
        val viga = Rect(0f, h * 0.08f, w, h * 0.17f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, viga.bottom), Size(w, 3 * u))
        drawRect(k(ROBLE), viga.topLeft, viga.size)
        vetas(viga, k(VETA).copy(alpha = 0.45f), 21, densidad = 0.6f, grosor = 0.7f)
        bisel(viga, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 1.5f * u)
        listOf(w * 0.02f, w * 0.98f).forEach { x ->
            drawPath(Path().apply { moveTo(x - 3 * u, viga.bottom); lineTo(x + 3 * u, viga.bottom); lineTo(x + 3 * u, viga.bottom + 20 * u); lineTo(x - 3 * u, viga.bottom + 14 * u); close() }, k(HIERRO))
        }
        // Las bolsas de los despachos.
        val cartas = repartoMedievo(e, 9)
        val zona = Rect(w * 0.05f, viga.bottom, w * 0.95f, h * 0.52f)
        if (cartas.isEmpty()) {
            // Vacía: los ganchos desnudos y un rollo de cordón colgado.
            for (j in 0 until 7) {
                val x = zona.left + zona.width * (j + 0.5f) / 7
                drawLine(k(HIERRO), Offset(x, viga.bottom), Offset(x, viga.bottom + 8 * u), 1.6f * u)
                drawArc(k(HIERRO), 0f, 180f, false, Offset(x - 3 * u, viga.bottom + 5 * u), Size(6 * u, 6 * u), style = Stroke(1.6f * u))
            }
            val o = Offset(zona.left + zona.width * 3.5f / 7, viga.bottom + 22 * u)
            for (j in 0 until 4) drawOval(k(CANAMO), Offset(o.x - (9 - j) * u, o.y - 10 * u + j * u), Size((18 - 2 * j) * u, (22 - j) * u), style = Stroke(2.2f * u))
        }
        val paso = zona.width / maxOf(cartas.size, 1)
        val ancho = minOf(34 * u, paso - 5 * u)
        cartas.forEachIndexed { i, clase ->
            val x = zona.left + paso * (i + 0.5f)
            val gancho = Offset(x, viga.bottom + 4 * u)
            val bolsa = Rect(x - ancho / 2, gancho.y + 10 * u + azar(i, 2) * 4 * u, x + ancho / 2, zona.bottom - azar(i, 4) * 8 * u)
            // La correa al gancho.
            drawLine(k(HIERRO), Offset(x, viga.bottom), gancho, 1.6f * u)
            drawLine(k(mezcla(CUERO, Color.Black, 0.3f)), gancho, Offset(bolsa.left + 3 * u, bolsa.top + 2 * u), 1.6f * u)
            drawLine(k(mezcla(CUERO, Color.Black, 0.3f)), gancho, Offset(bolsa.right - 3 * u, bolsa.top + 2 * u), 1.6f * u)
            val cuerpo = Path().apply {
                moveTo(bolsa.left, bolsa.top); lineTo(bolsa.right, bolsa.top); lineTo(bolsa.right, bolsa.bottom - ancho * 0.35f)
                quadraticTo(bolsa.right, bolsa.bottom, x, bolsa.bottom); quadraticTo(bolsa.left, bolsa.bottom, bolsa.left, bolsa.bottom - ancho * 0.35f); close()
            }
            translate(2 * u, 3 * u) { drawPath(cuerpo, Color.Black.copy(alpha = 0.3f)) }
            val tapaAlta = ancho * 0.7f
            if (clase == 2) {
                // Leída: la solapa levantada, con su forro rojo, y el pliego que asoma por arriba.
                drawPath(Path().apply { moveTo(bolsa.left, bolsa.top); lineTo(x, bolsa.top - tapaAlta * 0.75f); lineTo(bolsa.right, bolsa.top); close() }, k(ROJO))
                drawRect(k(CREMA), Offset(x - ancho * 0.32f, bolsa.top - 10 * u), Size(ancho * 0.64f, 14 * u))
                for (j in 0 until 2) drawLine(k(TINTA).copy(alpha = 0.5f), Offset(x - ancho * 0.24f, bolsa.top - 7 * u + j * 3 * u), Offset(x + ancho * 0.2f, bolsa.top - 7 * u + j * 3 * u), 0.7f * u)
            }
            drawPath(cuerpo, k(listOf(CUERO, Color(0xFF6A4528), Color(0xFF4E321E))[i % 3]))
            drawPath(cuerpo, k(CREMA).copy(alpha = 0.4f), style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u))))
            if (clase != 2) {
                val solapa = Path().apply { moveTo(bolsa.left, bolsa.top); lineTo(bolsa.right, bolsa.top); lineTo(bolsa.right, bolsa.top + tapaAlta * 0.6f); lineTo(x, bolsa.top + tapaAlta); lineTo(bolsa.left, bolsa.top + tapaAlta * 0.6f); close() }
                drawPath(solapa, k(BLANCO))
                drawPath(solapa, Color.Black.copy(alpha = 0.3f), style = Stroke(0.8f * u))
                drawPath(cruzPatada(Offset(x, bolsa.top + tapaAlta * 0.38f), tapaAlta * 0.24f), k(ROJO))
                val o = Offset(x, bolsa.top + tapaAlta)
                if (clase == 1) {
                    canamo(Offset(bolsa.left - 1 * u, o.y + 2 * u), Offset(bolsa.right + 1 * u, o.y + 2 * u), 2.4f * u, k(CANAMO))
                    canamo(o + Offset(2 * u, 2 * u), o + Offset(5 * u, 12 * u), 2f * u, k(CANAMO))
                } else {
                    haloMedievo(o, ancho * 1.05f, Color(0xFFF6D27A), if (oscuro) 0.95f else 0.75f)
                    rayosMedievo(o, ancho * 0.34f, ancho * 0.72f, Color(0xFFF6D27A), 8, 0.9f)
                }
                drawCircle(k(CERA_CASA), ancho * 0.2f, o)
                drawCircle(k(mezcla(CERA_CASA, Color.Black, 0.4f)), ancho * 0.13f, o, style = Stroke(0.8f * u))
            }
        }
        // Los tres escudos de la cuenta, cada uno en su clavija.
        val rotulos = listOf(Triple(e.listas, "por abrir", ROJO), Triple(e.selladas, "selladas", NEGRO), Triple(e.abiertas, "leídas", CUERO))
        rotulos.forEachIndexed { i, (n, nombre, jefe) ->
            val xc = w * (0.2f + 0.29f * i)
            val r = Rect(xc - w * 0.11f, h * 0.6f, xc + w * 0.11f, h * 0.93f)
            val forma = escudo(r)
            drawCircle(k(HIERRO), 2.2f * u, Offset(xc, r.top - 4 * u))
            drawLine(k(mezcla(CUERO, Color.Black, 0.3f)), Offset(xc, r.top - 4 * u), Offset(r.left + 6 * u, r.top), 1.4f * u)
            drawLine(k(mezcla(CUERO, Color.Black, 0.3f)), Offset(xc, r.top - 4 * u), Offset(r.right - 6 * u, r.top), 1.4f * u)
            translate(2 * u, 3 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
            drawPath(forma, if (oscuro) mezcla(BLANCO, Color(0xFF0D0B09), 0.28f) else BLANCO)
            val franja = Rect(r.left, r.top, r.right, r.top + r.height * 0.3f)
            clipPath(forma) { drawRect(if (oscuro) mezcla(jefe, Color(0xFF0D0B09), 0.2f) else jefe, franja.topLeft, franja.size) }
            drawPath(forma, k(Color(0xFF3A3028)), style = Stroke(1.4f * u))
            textoEn(medidor, nombre, franja.deflate(2 * u), letra, if (oscuro) Color(0xFFE8E0CC) else BLANCO, 9.5f, minimo = 7f)
            textoEn(medidor, "$n", Rect(r.left, franja.bottom, r.right, r.bottom - r.height * 0.14f), letra, NEGRO, 20f, peso = FontWeight.Bold)
        }
    }

    // ---------------------------------------------------------------- lo de alrededor

    /** Lo claro de lo de alrededor (la piedra del buscador, la vitela del recuerdo): en modo oscuro se hunde de verdad. */
    private fun claroHondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0D0B), 0.74f) else c
    /** La tinta sobre [claroHondo]: la de agalla de día, la crema en modo oscuro. */
    private fun tintaHonda(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA

    /**
     * El emblema de la pestaña [k] en [o], de radio [r]: el libro con la cruz (diario), el rollo de pergamino (momentos), la
     * media luna con su estrella (sueños), el cirio encendido (ideario) y la carta cerrada con el sello de la cruz (cartas).
     * [c] es el color del trazo y [f] el del fondo (para los huecos).
     */
    private fun DrawScope.emblema(k: Int, o: Offset, r: Float, c: Color, f: Color) {
        val u = density
        when (k) {
            0 -> {
                drawRect(c, Offset(o.x - r * 0.75f, o.y - r), Size(r * 1.5f, r * 2f))
                drawRect(f, Offset(o.x + r * 0.5f, o.y - r * 0.85f), Size(r * 0.12f, r * 1.7f))
                drawPath(cruzPatada(Offset(o.x - r * 0.06f, o.y), r * 0.55f), f)
            }
            1 -> {
                drawRect(c, Offset(o.x - r * 0.8f, o.y - r * 0.62f), Size(r * 1.6f, r * 1.24f))
                listOf(-1f, 1f).forEach { s -> drawCircle(c, r * 0.32f, Offset(o.x + s * r * 0.85f, o.y)); drawCircle(f, r * 0.12f, Offset(o.x + s * r * 0.85f, o.y)) }
                for (j in 0 until 3) drawLine(f, Offset(o.x - r * 0.45f, o.y - r * 0.3f + j * r * 0.3f), Offset(o.x + r * 0.45f, o.y - r * 0.3f + j * r * 0.3f), 0.7f * u)
            }
            2 -> {
                drawPath(mediaLunaMedievo(Offset(o.x - r * 0.15f, o.y + r * 0.05f), r * 0.92f), c)
                drawPath(estrella(Offset(o.x + r * 0.78f, o.y - r * 0.62f), r * 0.36f, 5, 0.45f), c)
            }
            3 -> {
                drawRect(c, Offset(o.x - r * 0.28f, o.y - r * 0.3f), Size(r * 0.56f, r * 1.2f))
                drawRect(c, Offset(o.x - r * 0.7f, o.y + r * 0.9f), Size(r * 1.4f, r * 0.2f))
                llamaMedievo(Offset(o.x, o.y - r * 0.38f), r * 0.75f, c)
            }
            else -> {
                drawRect(c, Offset(o.x - r, o.y - r * 0.68f), Size(r * 2f, r * 1.36f))
                drawLine(f, Offset(o.x - r, o.y - r * 0.68f), Offset(o.x, o.y + r * 0.05f), 0.8f * u)
                drawLine(f, Offset(o.x + r, o.y - r * 0.68f), Offset(o.x, o.y + r * 0.05f), 0.8f * u)
                drawCircle(f, r * 0.42f, Offset(o.x, o.y + r * 0.1f))
                drawPath(cruzPatada(Offset(o.x, o.y + r * 0.1f), r * 0.3f), c)
            }
        }
    }

    override val altoPestanas: Dp = 78.dp

    /**
     * Las pestañas: el adarve de la encomienda, un lienzo de muralla de sillares con cinco almenas. En cada almena va
     * labrado su emblema y su nombre; la cuenta, en la cara del muro, debajo. La elegida se alza sobre las demás, con el
     * emblema y el nombre pintados de rojo, y de ella cuelga el pendón rojo con la cruz blanca y la cuenta.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val muro = h - 24 * u
        // En modo oscuro la muralla se hunde de verdad y lo labrado se lee claro.
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0F0D0B), 0.62f) else x
        val labrado = if (oscuro) mezcla(SILLAR, CREMA, 0.55f) else mezcla(SILLAR_JUNTA, TINTA, 0.55f)
        val rojo = if (oscuro) Color(0xFFE0605A) else ROJO
        // El lienzo del muro, con sus hiladas de sillares.
        drawRect(c(SILLAR), Offset(0f, muro), Size(w, h - muro))
        val fila = (h - muro) / 2f
        for (k in 0 until 2) {
            val y = muro + fila * k
            drawLine(c(SILLAR_JUNTA), Offset(0f, y), Offset(w, y), 0.8f * u)
            var x = if (k % 2 == 0) 0f else 17 * u; var j = 0
            while (x < w && j < 40) { drawLine(c(SILLAR_JUNTA), Offset(x, y), Offset(x, y + fila), 0.8f * u); x += maxOf(34 * u, 1f); j++ }
        }
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, h - 2 * u), Size(w, 2 * u))
        for (k in 0 until 5) {
            val q = quintoMedievo(k, w, h)
            val sel = k == e.actual
            val arriba = if (sel) 1 * u else 9 * u
            val r = Rect(q.left + 4 * u, arriba, q.right - 4 * u, muro + 1 * u)
            val piedra = if (sel) (if (oscuro) mezcla(SILLAR, Color(0xFF0F0D0B), 0.5f) else mezcla(SILLAR, Color.White, 0.12f)) else c(SILLAR)
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 2 * u, r.top + 2 * u), r.size)
            drawRect(piedra, r.topLeft, r.size)
            // La albardilla de la almena (el remate en bisel) y las juntas de sus dos sillares.
            drawRect(Color.White.copy(alpha = 0.22f), r.topLeft, Size(r.width, 2 * u))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(r.right - 3 * u, r.top), Size(3 * u, r.height))
            drawRect(Color.White.copy(alpha = 0.12f), Offset(r.left, r.top), Size(2 * u, r.height))
            val junta = r.top + r.height * 0.5f
            drawLine(c(SILLAR_JUNTA).copy(alpha = 0.7f), Offset(r.left, junta), Offset(r.right, junta), 0.7f * u)
            drawLine(c(SILLAR_JUNTA).copy(alpha = 0.7f), Offset(r.left + r.width * 0.3f, r.top), Offset(r.left + r.width * 0.3f, junta), 0.7f * u)
            drawLine(c(SILLAR_JUNTA).copy(alpha = 0.7f), Offset(r.left + r.width * 0.7f, junta), Offset(r.left + r.width * 0.7f, r.bottom), 0.7f * u)
            val tinta = if (sel) rojo else labrado
            emblema(k, Offset(r.center.x, r.top + 13 * u), 6.5f * u, tinta, piedra)
            textoEn(medidor, e.nombres.getOrElse(k) { "" }, Rect(r.left + 2 * u, r.top + 23 * u, r.right - 2 * u, r.top + 38 * u), letra, tinta, 11f,
                peso = FontWeight.Bold, minimo = 7f)
            val cuenta = "${e.cuentas.getOrElse(k) { 0 }}"
            if (sel) {
                // El pendón rojo, colgado del pie de la almena, con la cruz blanca y la cuenta.
                val p = Rect(r.center.x - 13 * u, muro - 1 * u, r.center.x + 13 * u, h - 1 * u)
                val pendon = Path().apply {
                    moveTo(p.left, p.top); lineTo(p.right, p.top); lineTo(p.right, p.bottom); lineTo(p.center.x, p.bottom - 4 * u); lineTo(p.left, p.bottom); close()
                }
                translate(1.2f * u, 1.2f * u) { drawPath(pendon, Color.Black.copy(alpha = 0.25f)) }
                drawPath(pendon, if (oscuro) mezcla(ROJO, Color.Black, 0.2f) else ROJO)
                drawRect(c(HIERRO), Offset(p.left - 2 * u, p.top - 1 * u), Size(p.width + 4 * u, 2 * u))
                drawPath(cruzPatada(Offset(p.left + 6 * u, p.top + 8 * u), 4 * u), if (oscuro) CREMA else BLANCO)
                textoEn(medidor, cuenta, Rect(p.left + 10 * u, p.top + 1 * u, p.right - 1 * u, p.bottom - 5 * u), letra, if (oscuro) CREMA else BLANCO, 11f, peso = FontWeight.Bold, minimo = 6f)
            } else {
                textoEn(medidor, cuenta, Rect(q.left, muro + 3 * u, q.right, h - 3 * u), letra, labrado, 11f, peso = FontWeight.Bold, minimo = 6f)
            }
        }
    }

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.17f, 0.22f, 0.95f, 0.78f)
    override val letraCampo: FontFamily = LetraExtra.cruzada
    override fun tintaCampo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El buscador: una lápida de sillería con su marco en bisel y la caja rehundida donde va la inscripción; a la izquierda,
     * un tondo con la cruz patada. Sin el foco, la cruz está solo labrada; con el foco, se pinta de rojo, igual que el filo
     * de la caja.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val piedra = claroHondo(SILLAR, oscuro)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(1.5f * u, 2 * u), Size(w - 1.5f * u, h - 2 * u))
        drawRect(piedra, Offset.Zero, Size(w - 1.5f * u, h - 2 * u))
        bisel(Rect(0f, 0f, w - 1.5f * u, h - 2 * u), Color.White.copy(alpha = if (oscuro) 0.08f else 0.3f), Color.Black.copy(alpha = 0.3f), 3 * u)
        // Unas picaduras de la piedra.
        for (j in 0 until 14) drawCircle(Color.Black.copy(alpha = 0.1f), (0.5f + azar(j, 3)) * u, Offset(w * azar(j, 1), h * (0.1f + 0.8f * azar(j, 2))))
        val caja = Rect(w * cajaCampo.left - 6 * u, 7 * u, w - 8 * u, h - 9 * u)
        drawRect(Color.Black.copy(alpha = 0.06f), caja.topLeft, caja.size)
        // El rehundido, sin degradados: sombra arriba y a la izquierda, luz abajo.
        drawRect(Color.Black.copy(alpha = 0.25f), caja.topLeft, Size(caja.width, 2.2f * u))
        drawRect(Color.Black.copy(alpha = 0.18f), caja.topLeft, Size(2 * u, caja.height))
        drawRect(Color.White.copy(alpha = 0.12f), Offset(caja.left, caja.bottom - 1.2f * u), Size(caja.width, 1.2f * u))
        val rojo = t(ROJO, oscuro, false)
        if (e.enfocado) drawRect(rojo, caja.topLeft, caja.size, style = Stroke(1.2f * u))
        // El tondo con la cruz.
        val o = Offset(w * cajaCampo.left * 0.48f, (h - 2 * u) / 2f)
        val rr = minOf(16 * u, h * 0.34f)
        drawCircle(Color.Black.copy(alpha = 0.12f), rr, o)
        drawCircle(Color.Black.copy(alpha = 0.3f), rr, o, style = Stroke(1.2f * u))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), rr - 2 * u, o + Offset(0.6f * u, 0.6f * u), style = Stroke(0.8f * u))
        val cruz = cruzPatada(o, rr * 0.7f)
        if (e.enfocado) {
            drawPath(cruz, rojo)
        } else {
            translate(0.6f * u, 0.6f * u) { drawPath(cruz, Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f)) }
            drawPath(cruz, Color.Black.copy(alpha = 0.3f))
        }
        if (e.vacio) pistaMedievo(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), letraCampo,
            tintaHonda(oscuro).copy(alpha = 0.5f))
    }

    /**
     * Las obras: el muro encalado de la capilla de la encomienda, con el falso despiece de sillares pintado en rojo (dos
     * líneas por junta) y una cenefa de crucecitas en lo alto; abajo, el poyo de piedra donde se apoyan, con el paño blanco
     * de orla roja y la cruz que cuelga por delante. Distinto del armero (el estante) y de la cámara del tesoro.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(if (oscuro) mezcla(CAL_CAPITULO, Color(0xFF0F0D0B), 0.68f) else CAL_CAPITULO)
        val pintura = c(ROJO).copy(alpha = if (oscuro) 0.3f else 0.22f)
        // El falso despiece: hiladas de 18 dp con juntas dobles.
        val pie = h - 30 * u
        var y = 20 * u; var k = 0
        while (y < pie && k < 20) {
            drawLine(pintura, Offset(0f, y), Offset(w, y), 0.7f * u); drawLine(pintura, Offset(0f, y + 2 * u), Offset(w, y + 2 * u), 0.7f * u)
            var x = if (k % 2 == 0) 20 * u else 50 * u; var j = 0
            while (x < w && j < 30) {
                val y1 = minOf(y + 2 * u + 18 * u, pie)
                drawLine(pintura, Offset(x, y + 2 * u), Offset(x, y1), 0.7f * u); drawLine(pintura, Offset(x + 2 * u, y + 2 * u), Offset(x + 2 * u, y1), 0.7f * u)
                x += maxOf(60 * u, 1f); j++
            }
            y += maxOf(20 * u, 1f); k++
        }
        // La cenefa de crucecitas.
        drawRect(c(ROJO).copy(alpha = 0.5f), Offset(0f, 5 * u), Size(w, 1 * u))
        drawRect(c(ROJO).copy(alpha = 0.5f), Offset(0f, 15 * u), Size(w, 1 * u))
        var cx = 9 * u; k = 0
        while (cx < w && k < 60) { drawPath(cruzPatada(Offset(cx, 10.5f * u), 3 * u), c(ROJO).copy(alpha = 0.55f)); cx += maxOf(18 * u, 1f); k++ }
        // El poyo de piedra: la losa con su canto y la sombra en el muro.
        drawRect(Color.Black.copy(alpha = 0.16f), Offset(0f, pie - 4 * u), Size(w, 4 * u))
        drawRect(c(LOSA), Offset(0f, pie), Size(w, 9 * u))
        drawRect(Color.White.copy(alpha = 0.15f), Offset(0f, pie), Size(w, 1.4f * u))
        drawRect(c(mezcla(LOSA, Color.Black, 0.25f)), Offset(0f, pie + 9 * u), Size(w, h - pie - 9 * u))
        var jx = 26 * u; k = 0
        while (jx < w && k < 30) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(jx, pie + 9 * u), Offset(jx, h), 0.8f * u); jx += maxOf(52 * u, 1f); k++ }
        // El paño blanco con orla roja y la cruz.
        val pano = Rect(w * 0.3f, pie - 0.5f * u, w * 0.7f, h - 3 * u)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(pano.left + 1.5f * u, pano.top + 1.5f * u), pano.size)
        drawRect(c(BLANCO), pano.topLeft, pano.size)
        drawRect(c(ROJO), Offset(pano.left, pano.bottom - 3 * u), Size(pano.width, 3 * u))
        drawRect(Color.Black.copy(alpha = 0.1f), Offset(pano.left, pie + 9 * u), Size(pano.width, 1 * u))
        drawPath(cruzPatada(Offset(pano.center.x, pie + 16.5f * u), 6 * u), c(ROJO))
    }

    /** Los campos pintados de las tablas de las obras: negro sobre blanco, blanco con la cruz, rojo y blanco, y cuartelado. */
    private fun DrawScope.campoHeraldico(variante: Int, oscuro: Boolean) {
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        when (variante) {
            0 -> { drawRect(c(BLANCO)); drawRect(c(NEGRO), Offset.Zero, Size(w, h * 0.42f)) }
            1 -> {
                drawRect(c(BLANCO))
                drawRect(c(ROJO), Offset(w * 0.42f, 0f), Size(w * 0.16f, h)); drawRect(c(ROJO), Offset(0f, h * 0.2f), Size(w, w * 0.16f))
            }
            2 -> { drawRect(c(BLANCO)); drawRect(c(ROJO), Offset.Zero, Size(w * 0.5f, h)) }
            else -> {
                drawRect(c(BLANCO))
                drawRect(c(NEGRO), Offset.Zero, Size(w * 0.5f, h * 0.5f)); drawRect(c(NEGRO), Offset(w * 0.5f, h * 0.5f), Size(w * 0.5f, h * 0.5f))
            }
        }
    }

    /**
     * La tapa de una obra: una tabla pintada con un campo de armas (cuatro particiones según la obra: el negro sobre blanco
     * del estandarte, la cruz roja, rojo y blanco, cuartelado), con esquineras y clavos de hierro; arriba la banda negra con
     * el tipo, y en medio la cartela de crema clavada con el título, el autor y el año. En miniatura, la cartela con la
     * inicial en rojo.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = t(x, oscuro, false)
        val semilla = e.semilla and 0x7fffffff
        campoHeraldico(semilla % 4, oscuro)
        // La pintura gastada: unas vetas de la tabla que asoman.
        vetas(Rect(0f, 0f, w, h), c(VETA).copy(alpha = 0.12f), 50 + semilla % 5, densidad = 0.6f, vertical = true, grosor = 0.6f)
        // Las esquineras de hierro.
        val e0 = w * 0.16f
        listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, d) ->
            drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + d.x * e0, o.y); lineTo(o.x, o.y + d.y * e0); close() }, c(HIERRO))
            drawCircle(c(HIERRO_LUZ), maxOf(1f, w * 0.018f), Offset(o.x + d.x * e0 * 0.3f, o.y + d.y * e0 * 0.3f))
        }
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.35f), maxOf(1f, w * 0.02f))
        val crema = if (oscuro) mezcla(CREMA, Color(0xFF0F0D0B), 0.2f) else CREMA
        val rojo = if (oscuro) mezcla(ROJO, Color.Black, 0.1f) else ROJO
        if (e.mini) {
            val r = Rect(w * 0.18f, h * 0.26f, w * 0.82f, h * 0.7f)
            drawRect(c(HIERRO), r.inflate(w * 0.03f).topLeft, r.inflate(w * 0.03f).size)
            drawRect(crema, r.topLeft, r.size)
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifBlank { "·" }, r.deflate(w * 0.04f), letraTapa, rojo, s * 0.42f, peso = FontWeight.Bold)
            return
        }
        // La banda negra del tipo.
        val banda = Rect(0f, h * 0.075f, w, h * 0.16f)
        drawRect(c(NEGRO), banda.topLeft, banda.size)
        drawRect(rojo, Offset(0f, banda.bottom), Size(w, maxOf(1f, h * 0.008f)))
        textoEn(medidor, e.tipo.uppercase(), Rect(w * 0.18f, banda.top, w * 0.82f, banda.bottom), letra, c(BLANCO), s * 0.075f, espaciado = s * 0.008f, minimo = 4f)
        // La cartela clavada.
        val cart = Rect(w * 0.1f, h * 0.22f, w * 0.9f, h * 0.8f)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(cart.left + w * 0.025f, cart.top + w * 0.03f), cart.size)
        drawRect(c(HIERRO), cart.topLeft, cart.size)
        val dentro = cart.deflate(w * 0.025f)
        drawRect(crema, dentro.topLeft, dentro.size)
        listOf(dentro.left + w * 0.035f, dentro.right - w * 0.035f).forEach { x -> listOf(dentro.top + w * 0.035f, dentro.bottom - w * 0.035f).forEach { y ->
            drawCircle(c(HIERRO), maxOf(1f, w * 0.018f), Offset(x, y))
        } }
        drawPath(cruzPatada(Offset(dentro.center.x, dentro.top + dentro.height * 0.1f), dentro.width * 0.07f), rojo)
        val texto = dentro.deflate(w * 0.06f)
        tituloMedievo(medidor, e.titulo.ifBlank { "—" }, Rect(texto.left, dentro.top + dentro.height * 0.18f, texto.right, dentro.top + dentro.height * 0.7f),
            letraTapa, TINTA, s * 0.13f, peso = FontWeight.Bold)
        drawLine(rojo, Offset(dentro.center.x - dentro.width * 0.2f, dentro.top + dentro.height * 0.73f), Offset(dentro.center.x + dentro.width * 0.2f, dentro.top + dentro.height * 0.73f), maxOf(0.6f * u, w * 0.008f))
        textoEn(medidor, e.autor, Rect(texto.left, dentro.top + dentro.height * 0.75f, texto.right, dentro.top + dentro.height * 0.86f), letra, TINTA.copy(alpha = 0.85f), s * 0.075f, minimo = 4f)
        textoEn(medidor, e.anio, Rect(texto.left, dentro.top + dentro.height * 0.86f, texto.right, dentro.top + dentro.height * 0.96f), letra, rojo, s * 0.07f, minimo = 4f)
    }

    override val margenRecuerdo: Dp = 14.dp
    override val altoRotuloRecuerdo: Dp = 48.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El recuerdo: una hoja de vitela con la cabecera partida como el estandarte (la banda negra arriba, con la cruz roja y
     * «Hace N años» en blanco; la blanca debajo, con la fecha y el clima) y un filete rojo. El ánimo es una gota de lacre
     * del color del día, a la derecha de la banda negra.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenRecuerdo.toPx(); val alto = altoRotuloRecuerdo.toPx()
        val hoja = Rect(0f, 0f, w - 1.5f * u, h - 1.5f * u)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(1.5f * u, 1.5f * u), hoja.size)
        drawRect(claroHondo(CREMA, oscuro), hoja.topLeft, hoja.size)
        val negra = Rect(0f, 0f, hoja.right, 25 * u)
        drawRect(if (oscuro) Color(0xFF0B0A0C) else NEGRO, negra.topLeft, negra.size)
        val banda = Rect(0f, negra.bottom, hoja.right, alto - 2 * u)
        drawRect(if (oscuro) mezcla(BLANCO, Color(0xFF0F0D0B), 0.62f) else BLANCO, banda.topLeft, banda.size)
        val rojo = if (oscuro) mezcla(ROJO, CREMA, 0.15f) else ROJO
        drawRect(rojo, Offset(0f, banda.bottom), Size(hoja.right, 1.6f * u))
        drawPath(cruzPatada(Offset(m + 5 * u, negra.center.y), 6 * u), rojo)
        izquierdaMedievo(medidor, haceMedievo(e.anios), m + 15 * u, 2 * u, negra.bottom - 1 * u, w - 2 * m - 40 * u, letra, BLANCO, 15f, peso = FontWeight.Bold)
        val clima = climaMedievo(e.clima)
        izquierdaMedievo(medidor, if (clima.isEmpty()) e.fecha else "${e.fecha} · $clima", m, banda.top + 1 * u, banda.bottom, w - 2 * m, letra,
            tintaHonda(oscuro).copy(alpha = 0.85f), 12f, minimo = 8f)
        e.animo?.let { a ->
            val o = Offset(w - m - 8 * u, negra.center.y)
            drawPath(gotaMedievo(o, 8 * u, e.semilla), a)
            drawCircle(Color.Black.copy(alpha = 0.25f), 5 * u, o, style = Stroke(0.8f * u))
            drawPath(cruzPatada(o, 3.2f * u), Color.Black.copy(alpha = 0.3f))
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
 * La tapa es ese rollo tumbado y grande, envuelto en seda con nubes de oro, el rótulo de papel con la fecha y lo
 * escrito, el cordón y la tarjeta de marfil; al abrirse corre a la derecha y deja tendida la pintura con sus columnas de
 * escritura (de noche, una luna en la tarjeta y la luz de la lámpara).
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa = Rect(0.21f, 0.345f, 0.71f, 0.665f)
    override val letraTapa: FontFamily = LetraExtra.kufi
    override val tamTapa = 13f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** El papel del rótulo y de la hoja: se hunde menos que la seda, para que el extracto se lea siempre. */
    private fun papel(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPEL
        if (oscuro) r = mezcla(r, Color(0xFF110B09), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val seda = c(SEDAS[(e.semilla and 0x7fffffff) % SEDAS.size])
        val hoja = papel(oscuro, n)
        val oro = mezcla(c(ORO), ORO, 0.3f)
        val rollo = Rect(w * 0.075f, h * 0.13f, w * 0.915f, h * 0.85f)
        val canto = rollo.height * 0.16f
        val eje = rollo.right + 12 * u
        // La sombra del rollo tumbado sobre la mesa.
        drawOval(Color.Black.copy(alpha = 0.22f), Offset(rollo.left, rollo.bottom - 6 * u), Size(rollo.width + 14 * u, 14 * u))
        // Al abrirse, el rollo corre hacia la derecha y deja tendida la pintura: el papel montado en seda, con sus columnas
        // de escritura de arriba abajo, el sello del dueño y la varilla del principio.
        val sx = 1f - 0.8f * a
        val izquierda = eje - (eje - (rollo.left - canto / 2)) * sx
        if (a > 0.01f) {
            val tendido = Rect(w * 0.035f, rollo.top + rollo.height * 0.07f, izquierda + 6 * u, rollo.bottom - rollo.height * 0.07f)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(tendido.left + 2 * u, tendido.top + 3 * u), tendido.size)
            drawRect(seda, tendido.topLeft, tendido.size)
            val pag = Rect(tendido.left + 6 * u, tendido.top + 10 * u, tendido.right, tendido.bottom - 10 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            listOf(pag.top - 3 * u, pag.bottom + 3 * u).forEach { y -> drawLine(oro.copy(alpha = 0.8f), Offset(tendido.left, y), Offset(tendido.right, y), 1 * u) }
            clipRect(pag.left, pag.top, pag.right, pag.bottom) {
                var x = pag.right - 10 * u; var k = 0
                while (x > pag.left + 6 * u) {
                    var y = pag.top + 6 * u; var j = 0
                    val fin = pag.bottom - (if (k % 4 == 3) pag.height * 0.4f else 6 * u)
                    while (y < fin) {
                        val largo = (2.2f + 2.6f * azar(e.semilla + k, j)) * u
                        drawLine(c(TINTA).copy(alpha = 0.6f), Offset(x, y), Offset(x, minOf(y + largo, fin)), 1.5f * u)
                        y += largo + 1.8f * u; j++
                    }
                    x -= 7 * u; k++
                }
                val s = 9 * u
                drawRect(c(BERMELLON), Offset(pag.left + 8 * u, pag.bottom - s - 8 * u), Size(s, s))
                drawRect(hoja.copy(alpha = 0.8f), Offset(pag.left + 10 * u, pag.bottom - s - 6 * u), Size(s - 4 * u, s - 4 * u), style = Stroke(0.6f * u))
            }
            val vara = Rect(tendido.left - 2.5f * u, tendido.top - 4 * u, tendido.left + 2.5f * u, tendido.bottom + 4 * u)
            drawRoundRect(c(BAMBU), vara.topLeft, vara.size, CornerRadius(2.5f * u))
            drawRect(Color.White.copy(alpha = 0.2f), Offset(vara.left + 1 * u, vara.top), Size(1 * u, vara.height))
        }
        // El rollo envuelto en seda labrada, con las perillas de jade en las puntas del rodillo.
        scale(sx, 1f, Offset(eje, rollo.center.y)) {
            listOf(rollo.left - canto / 2 - 11 * u, rollo.right + canto / 2 - 1 * u).forEach { x ->
                val perilla = Rect(x, rollo.center.y - rollo.height * 0.1f, x + 12 * u, rollo.center.y + rollo.height * 0.1f)
                drawRoundRect(c(JADE), perilla.topLeft, perilla.size, CornerRadius(4 * u))
                drawRect(Color.White.copy(alpha = 0.28f), Offset(perilla.left + 2 * u, perilla.top + 2 * u), Size(perilla.width - 4 * u, 2 * u))
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(perilla.left, perilla.bottom - 3 * u), Size(perilla.width, 3 * u))
            }
            drawRect(Brush.verticalGradient(listOf(mezcla(seda, Color.White, 0.22f), seda, mezcla(seda, Color.Black, 0.45f)), rollo.top, rollo.bottom),
                rollo.topLeft, rollo.size)
            // El labrado: rombos tenues y dos hileras de nubes de oro.
            clipRect(rollo.left, rollo.top, rollo.right, rollo.bottom) {
                var k = rollo.left - rollo.height
                while (k < rollo.right) {
                    drawLine(Color.White.copy(alpha = 0.07f), Offset(k, rollo.bottom), Offset(k + rollo.height, rollo.top), 0.7f * u)
                    drawLine(Color.White.copy(alpha = 0.07f), Offset(k, rollo.top), Offset(k + rollo.height, rollo.bottom), 0.7f * u)
                    k += 9 * u
                }
            }
            var nx = rollo.left + 14 * u; var j = 0
            while (nx < rollo.right - 8 * u) {
                nube(Offset(nx, rollo.top + rollo.height * 0.06f), 4.2f * u, oro.copy(alpha = 0.7f), 0.8f * u)
                nube(Offset(nx + 12 * u, rollo.bottom - rollo.height * 0.11f), 4.2f * u, oro.copy(alpha = 0.55f), 0.8f * u)
                nx += 24 * u; j++
            }
            // El dobladillo de la envoltura, que corre a lo largo.
            val dobla = rollo.top + rollo.height * 0.885f
            drawLine(oro.copy(alpha = 0.7f), Offset(rollo.left, dobla), Offset(rollo.right, dobla), 1.2f * u)
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(rollo.left, dobla + 1.4f * u), Offset(rollo.right, dobla + 1.4f * u), 0.8f * u)
            // Los cantos: la espiral del papel enrollado dentro de la seda.
            listOf(rollo.left, rollo.right).forEach { x ->
                val o = Rect(x - canto / 2, rollo.top, x + canto / 2, rollo.bottom)
                drawOval(mezcla(seda, Color.Black, 0.2f), o.topLeft, o.size)
                for (k in 1..4) {
                    val f = 1f - k * 0.18f
                    drawOval(c(PAPEL).copy(alpha = 0.55f), Offset(x - canto / 2 * f, rollo.center.y - rollo.height / 2 * f), Size(canto * f, rollo.height * f), style = Stroke(0.7f * u))
                }
                drawOval(c(BAMBU), Offset(x - canto * 0.08f, rollo.center.y - rollo.height * 0.08f), Size(canto * 0.16f, rollo.height * 0.16f))
            }
            // El rótulo de papel pegado a lo largo, con la fecha arriba; lo escrito va en medio.
            val rotulo = Rect(w * 0.18f, h * 0.22f, w * 0.74f, h * 0.775f)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(rotulo.left + 1.5f * u, rotulo.top + 2 * u), rotulo.size)
            drawRect(hoja, rotulo.topLeft, rotulo.size)
            drawRect(c(TINTA).copy(alpha = 0.55f), rotulo.topLeft, rotulo.size, style = Stroke(0.7f * u))
            val marco = rotulo.deflate(2.5f * u)
            drawRect(c(BERMELLON).copy(alpha = 0.5f), marco.topLeft, marco.size, style = Stroke(0.5f * u))
            textoEn(medidor, e.fecha, Rect(rotulo.left + 6 * u, rotulo.top + 3 * u, rotulo.right - 6 * u, h * 0.335f), letra,
                mezcla(c(BERMELLON), Color.Black, 0.25f), 12f, peso = FontWeight.Bold, minimo = 7f)
            drawLine(c(TINTA).copy(alpha = 0.35f), Offset(rotulo.left + 14 * u, h * 0.338f), Offset(rotulo.right - 14 * u, h * 0.338f), 0.5f * u)
            val pie = Rect(rotulo.left + 6 * u, h * 0.672f, rotulo.right - 20 * u, rotulo.bottom - 3 * u)
            if (!e.vacio && e.palabras > 0) textoEn(medidor, palabrasTapa(e.palabras), pie, letra, c(TINTA).copy(alpha = 0.8f), 10f, minimo = 6f)
            val s = minOf(11 * u, rotulo.bottom - h * 0.672f - 4 * u)
            drawRect(c(BERMELLON), Offset(rotulo.right - s - 6 * u, rotulo.bottom - s - 4 * u), Size(s, s))
            drawRect(hoja.copy(alpha = 0.8f), Offset(rotulo.right - s - 4 * u, rotulo.bottom - s - 2 * u), Size(s - 4 * u, s - 4 * u), style = Stroke(0.6f * u))
            // El cordón rojo que lo ata, con su nudo y la tarjeta de marfil colgando: el sello de día, la luna de noche.
            val xc = w * 0.8f
            for (k in 0 until 3) {
                val x = xc + k * 3.2f * u
                drawPath(Path().apply { moveTo(x, rollo.top); quadraticTo(x + 3 * u, rollo.center.y, x, rollo.bottom) }, c(CORDON), style = Stroke(1.6f * u))
            }
            val nudo = Offset(xc + 3.2f * u, rollo.top + rollo.height * 0.2f)
            drawCircle(c(CORDON), 3.4f * u, nudo)
            drawCircle(Color.Black.copy(alpha = 0.25f), 3.4f * u, nudo, style = Stroke(0.6f * u))
            val tarjeta = Rect(w * 0.845f, rollo.top + rollo.height * 0.36f, w * 0.905f, rollo.top + rollo.height * 0.8f)
            drawLine(c(CORDON), nudo, Offset(tarjeta.center.x, tarjeta.top + 2 * u), 1 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(tarjeta.left + 1 * u, tarjeta.top + 1.5f * u), tarjeta.size, CornerRadius(2 * u))
            drawRoundRect(c(MARFIL), tarjeta.topLeft, tarjeta.size, CornerRadius(2 * u))
            drawCircle(c(TINTA).copy(alpha = 0.5f), 1 * u, Offset(tarjeta.center.x, tarjeta.top + 2.4f * u))
            val cs = Offset(tarjeta.center.x, tarjeta.center.y + 3 * u)
            val r = minOf(tarjeta.width * 0.34f, 6 * u)
            if (n) {
                drawCircle(LUNA, r, cs)
                drawCircle(c(MARFIL), r * 0.82f, Offset(cs.x + r * 0.42f, cs.y - r * 0.22f))
                drawCircle(LUNA, 0.9f * u, Offset(cs.x - r * 0.6f, cs.y + r * 1.6f))
            } else {
                drawRect(c(BERMELLON), Offset(cs.x - r, cs.y - r), Size(r * 2, r * 2))
                drawLine(c(MARFIL), Offset(cs.x, cs.y - r * 0.6f), Offset(cs.x, cs.y + r * 0.6f), 0.7f * u)
                drawLine(c(MARFIL), Offset(cs.x - r * 0.6f, cs.y), Offset(cs.x + r * 0.6f, cs.y), 0.7f * u)
            }
            // La borla del cordón, que cuelga por delante.
            val borla = Offset(xc - 2 * u, rollo.bottom + 3 * u)
            drawLine(c(CORDON), Offset(xc + 1 * u, rollo.bottom - 4 * u), borla, 1.2f * u)
            drawRoundRect(c(CORDON), Offset(borla.x - 2.6f * u, borla.y), Size(5.2f * u, minOf(13 * u, h - borla.y - 1 * u)), CornerRadius(2 * u))
            drawRect(oro, Offset(borla.x - 2.8f * u, borla.y + 1 * u), Size(5.6f * u, 1.4f * u))
        }
        // De noche, la lámpara de papel alumbra desde arriba.
        if (n) clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.5f, -h * 0.1f), h * 1.1f, LAMPARA.copy(alpha = 0.22f)) }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 114.dp

    /** Un rombo de [s] de radio con centro en [c0]. */
    private fun rombo(c0: Offset, s: Float): Path = Path().apply {
        moveTo(c0.x, c0.y - s); lineTo(c0.x + s, c0.y); lineTo(c0.x, c0.y + s); lineTo(c0.x - s, c0.y); close()
    }

    /** Una cenefa de brocado en [r]: el fondo (si lo hay), dos hilos de oro y, entre ellos, rombos de oro y de bermellón. */
    private fun DrawScope.brocado(r: Rect, oro: Color, rojo: Color, fondo: Color?) {
        val u = density
        fondo?.let { drawRect(it, r.topLeft, r.size) }
        drawLine(oro, Offset(r.left, r.top + 0.6f * u), Offset(r.right, r.top + 0.6f * u), 0.9f * u)
        drawLine(oro, Offset(r.left, r.bottom - 0.6f * u), Offset(r.right, r.bottom - 0.6f * u), 0.9f * u)
        val paso = r.height * 1.4f
        val s = r.height * 0.26f
        var x = r.left + paso / 2; var k = 0
        while (x < r.right - paso / 4) { drawPath(rombo(Offset(x, r.center.y), s), if (k % 2 == 0) oro else rojo); x += paso; k++ }
    }

    /**
     * La cabecera del rollo montado: dos cenefas de brocado de oro sobre seda oscura, con los extremos de jade del rodillo;
     * a la izquierda, el sello cuadrado de cinabrio con la primera parte del rótulo grabada («日記»; de noche, la puerta de
     * luna con la luna llena velada por una nube); a la derecha, el resto del rótulo en oro, la fecha en grande (si es
     * larga, en dos renglones) y el detalle.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val oro = p.acento; val rojo = p.detalle
        val arriba = Rect(8f * u, 4f * u, w - 8f * u, 13f * u)
        val abajo = Rect(8f * u, h - 13f * u, w - 8f * u, h - 4f * u)
        listOf(arriba, abajo).forEach { r ->
            brocado(r, oro.copy(alpha = 0.85f), rojo.copy(alpha = 0.8f), mezcla(p.color, Color.Black, 0.25f))
            listOf(r.left - 4f * u, r.right + 4f * u).forEach { x ->
                drawRoundRect(c(JADE), Offset(x - 3.5f * u, r.top - 1.5f * u), Size(7f * u, r.height + 3f * u), CornerRadius(2.5f * u))
                drawLine(Color.White.copy(alpha = 0.3f), Offset(x - 1.5f * u, r.top), Offset(x - 1.5f * u, r.bottom), 1f * u)
            }
        }
        val partes = e.rotulo.split(" · ", limit = 2)
        val grabado = if (partes.size == 2) partes[0] else ""
        val titulo = if (partes.size == 2) partes[1] else e.rotulo
        val lado = 50f * u
        val s = Rect(12f * u, h / 2 - lado / 2, 12f * u + lado, h / 2 + lado / 2)
        if (!e.noche) {
            drawRect(Color.Black.copy(alpha = 0.25f), s.topLeft + Offset(1.2f * u, 1.6f * u), s.size)
            drawRect(c(BERMELLON), s.topLeft, s.size)
            // Las mellas del sello: donde la pasta no tomó.
            for (k in 0 until 8) {
                val f = azar(e.numero, k)
                val o = when (k % 4) { 0 -> Offset(s.left + f * lado, s.top); 1 -> Offset(s.right, s.top + f * lado); 2 -> Offset(s.left + f * lado, s.bottom); else -> Offset(s.left, s.top + f * lado) }
                drawCircle(p.color.copy(alpha = 0.8f), (0.8f + 1.2f * azar(e.numero, k + 9)) * u, o)
            }
            drawRect(p.color.copy(alpha = 0.9f), s.topLeft + Offset(3.5f * u, 3.5f * u), Size(lado - 7f * u, lado - 7f * u), style = Stroke(1.4f * u))
            if (grabado.isNotBlank()) textoEn(medidor, grabado, s.deflate(7f * u), FontFamily.Serif, p.color, 20f, peso = FontWeight.Bold, minimo = 8f)
        } else {
            val luna = mezcla(LUNA, p.acento, 0.25f)
            drawCircle(oro.copy(alpha = 0.8f), lado / 2, s.center, style = Stroke(1.4f * u))
            drawCircle(oro.copy(alpha = 0.4f), lado / 2 - 3f * u, s.center, style = Stroke(0.7f * u))
            drawCircle(luna, lado * 0.27f, s.center - Offset(0f, 2f * u))
            val nube = Rect(s.left + lado * 0.14f, s.center.y + lado * 0.1f, s.center.x + lado * 0.16f, s.center.y + lado * 0.19f)
            drawRoundRect(mezcla(p.color, Color.Black, 0.2f), nube.topLeft, nube.size, CornerRadius(nube.height / 2))
            drawRoundRect(oro.copy(alpha = 0.7f), nube.topLeft, nube.size, CornerRadius(nube.height / 2), style = Stroke(0.8f * u))
            listOf(Offset(0.2f, 0.24f), Offset(0.78f, 0.3f), Offset(0.7f, 0.78f)).forEach { f ->
                drawPath(estrella(Offset(s.left + f.x * lado, s.top + f.y * lado), 1.8f * u, 4, 0.4f), luna)
            }
        }
        val x0 = s.right + 10f * u; val x1 = w - 12f * u
        textoEn(medidor, titulo, Rect(x0, 16f * u, x1, 34f * u), LetraExtra.almendra, rotuloMedievo(p, e.noche), 15f, minimo = 8f)
        fechaMedievo(medidor, e.fecha, Rect(x0, 35f * u, x1, 77f * u), LetraExtra.alegreya, p.tinta, 22f)
        textoEn(medidor, e.detalle, Rect(x0, 78f * u, x1, 97f * u), LetraExtra.alegreya, p.tenue, 11f, minimo = 7f)
    }

    /**
     * La seda del rollo montado: los hilos de las columnas de escritura, las cenefas de brocado arriba y abajo, los bordes
     * de seda más oscura a los lados con su hilo de oro, una nube de oro en dos esquinas, el sello ovalado del coleccionista
     * en el margen y el sello cuadrado de cinabrio abajo a la derecha. La seda ya es oscura: en modo oscuro no cambia.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val oro = p.acento; val rojo = p.detalle
        var x = 34f * u
        while (x < w - 20f * u) { drawLine(rojo.copy(alpha = 0.09f), Offset(x, 24f * u), Offset(x, h - 24f * u), 0.8f * u); x += 38f * u }
        val borde = mezcla(p.color, Color.Black, 0.3f).copy(alpha = 0.7f)
        drawRect(borde, Offset.Zero, Size(7f * u, h)); drawRect(borde, Offset(w - 7f * u, 0f), Size(7f * u, h))
        listOf(7f * u, w - 7f * u).forEach { drawLine(oro.copy(alpha = 0.35f), Offset(it, 0f), Offset(it, h), 0.8f * u) }
        brocado(Rect(7f * u, 6f * u, w - 7f * u, 15f * u), oro.copy(alpha = 0.35f), rojo.copy(alpha = 0.3f), null)
        brocado(Rect(7f * u, h - 15f * u, w - 7f * u, h - 6f * u), oro.copy(alpha = 0.35f), rojo.copy(alpha = 0.3f), null)
        nube(Offset(26f * u, 40f * u), 10f * u, oro.copy(alpha = 0.16f), 1f * u)
        nube(Offset(w - 30f * u, h - 64f * u), 12f * u, oro.copy(alpha = 0.16f), 1f * u)
        // El sello del coleccionista, en el margen.
        drawOval(rojo.copy(alpha = 0.45f), Offset(12f * u, 120f * u), Size(14f * u, 20f * u))
        drawOval(p.color.copy(alpha = 0.6f), Offset(14.5f * u, 122.5f * u), Size(9f * u, 15f * u), style = Stroke(0.9f * u))
        // El sello cuadrado del dueño, abajo a la derecha.
        val l = 34f * u
        val o = Offset(w - l - 18f * u, h - l - 26f * u)
        drawRect(rojo.copy(alpha = 0.55f), o, Size(l, l))
        drawRect(p.color.copy(alpha = 0.7f), o + Offset(4f * u, 4f * u), Size(l - 8f * u, l - 8f * u), style = Stroke(1.6f * u))
        drawLine(p.color.copy(alpha = 0.7f), o + Offset(l / 2, 7f * u), o + Offset(l / 2, l - 7f * u), 1.6f * u)
        drawLine(p.color.copy(alpha = 0.7f), o + Offset(7f * u, l / 2), o + Offset(l - 7f * u, l / 2), 1.6f * u)
    }

    /** La barra: la orilla de seda oscura del montaje, con su cenefa de brocado al pie. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = mezcla(p.color, Color.Black, 0.35f)
        drawRect(Brush.verticalGradient(listOf(mezcla(base, p.acento, 0.06f), base), 0f, h))
        brocado(Rect(0f, h - 9f * u, w, h - 1f * u), p.acento.copy(alpha = 0.6f), p.detalle.copy(alpha = 0.55f), null)
    }

    /** El botón de sellar: el sello de cinabrio, con sus mellas y su filete claro grabado. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        val rojo = t(BERMELLON, false, noche)
        drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1f * u, 1.6f * u), r.size)
        drawRect(Brush.verticalGradient(listOf(mezcla(rojo, Color.White, 0.1f), mezcla(rojo, Color.Black, 0.15f)), r.top, r.bottom), r.topLeft, r.size)
        for (k in 0 until 6) {
            val f = azar(31, k)
            val o = if (k % 2 == 0) Offset(r.left + f * r.width, if (k % 4 == 0) r.top else r.bottom) else Offset(if (k % 4 == 1) r.left else r.right, r.top + f * r.height)
            drawCircle(mezcla(rojo, PAPEL, 0.45f).copy(alpha = 0.6f), (0.7f + azar(31, k + 7)) * u, o)
        }
        drawRect(PAPEL.copy(alpha = 0.75f), r.topLeft + Offset(3f * u, 3f * u), Size(r.width - 6f * u, r.height - 6f * u), style = Stroke(1f * u))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = PAPEL

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 252.dp

    /** El palisandro de la mesa baja, la piedra de tinta, el bronce de la lámpara y el celadón del gotero. */
    private val PALISANDRO = Color(0xFF5A2E1E)
    private val PALISANDRO_LUZ = Color(0xFF7A4430)
    private val PIEDRA_TINTA = Color(0xFF2C2A2E)
    private val BRONCE = Color(0xFF8C6A3A)
    private val CELADON = Color(0xFF9CB8A0)
    private val SUELO = Color(0xFF6E665A)

    /** El grosor del libro plegado, en dp: de 3 con un pliegue a 18 con 240 o más. */
    private fun grosorPlegado(hojas: Int): Float = 3f + 15f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /** Un cuadrilátero visto en perspectiva: el borde de delante de [x0] a [x1] a la altura [y1], el de atrás metido [p] y a la altura [y0]. */
    private fun plano(x0: Float, x1: Float, y0: Float, y1: Float, p: Float): Path = Path().apply {
        moveTo(x0 + p, y0); lineTo(x1 - p, y0); lineTo(x1, y1); lineTo(x0, y1); close()
    }

    /**
     * El libro plegado de momentos tumbado sobre la mesa baja de palisandro, en el estudio: el muro de cal con la ventana
     * de celosía, la mesa vista desde arriba con su faldón de nube y sus patas de casco, y sobre ella la piedra de tinta con
     * su barra, el gotero de celadón, el bote de pinceles, el apoyapinceles en forma de montes con un pincel y la lámpara de
     * aceite de bronce. El libro es un acordeón de papel entre dos tablas forradas de seda índigo con nubes de oro: el rótulo
     * de papel con el nombre y la tira con la cuenta; por delante se ve el canto de los pliegues, más alto cuanto más
     * escribiste, y asoman más cintas de seda. Al abrirse, la tapa gira sobre el lomo y los pliegues se despliegan hacia la
     * derecha con sus columnas de escritura y el sello rojo. En oscuro, la lámpara de aceite encendida alumbra la mesa.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0C0908), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorPlegado(e.hojas) * u
        val hoja = papel(oscuro, false)
        // El muro de cal y la ventana de celosía de madera, con el papel detrás.
        drawRect(c(MURO))
        drawRect(c(mezcla(MURO, LADRILLO, 0.35f)), Offset(0f, h * 0.44f), Size(w, h * 0.06f))
        val ven = Rect(w * 0.6f, h * 0.05f, w * 0.93f, h * 0.38f)
        drawRect(c(BAMBU), ven.inflate(4 * u).topLeft, ven.inflate(4 * u).size)
        drawRect(if (oscuro) Color(0xFF1B2034) else Color(0xFFF4ECD6), ven.topLeft, ven.size)
        clipRect(ven.left, ven.top, ven.right, ven.bottom) {
            // La celosía de hielo quebrado: líneas que se cortan en ángulos distintos.
            val celosia = c(PALISANDRO)
            for (j in 0 until 7) {
                val x0 = ven.left + ven.width * azar(13, j); val y0 = ven.top + ven.height * azar(17, j)
                val ang = Math.toRadians((30 + 120 * azar(19, j)).toDouble())
                val l = ven.width
                drawLine(celosia, Offset(x0 - l * kotlin.math.cos(ang).toFloat(), y0 - l * kotlin.math.sin(ang).toFloat()), Offset(x0 + l * kotlin.math.cos(ang).toFloat(), y0 + l * kotlin.math.sin(ang).toFloat()), 2 * u)
            }
            drawLine(celosia, Offset(ven.center.x, ven.top), Offset(ven.center.x, ven.bottom), 2.4f * u)
            drawLine(celosia, Offset(ven.left, ven.center.y), Offset(ven.right, ven.center.y), 2.4f * u)
        }
        drawRect(c(PALISANDRO), ven.topLeft, ven.size, style = Stroke(3 * u))
        // Un rollo colgado en el muro: la seda de los bordes y el papel con un monte a tinta.
        val colg = Rect(w * 0.3f, h * 0.04f, w * 0.42f, h * 0.36f)
        drawLine(c(TINTA), Offset(colg.center.x, 0f), Offset(colg.left + 4 * u, colg.top), 0.8f * u)
        drawLine(c(TINTA), Offset(colg.center.x, 0f), Offset(colg.right - 4 * u, colg.top), 0.8f * u)
        drawRect(c(SEDAS[4]), colg.topLeft, colg.size)
        val pint = Rect(colg.left + 3 * u, colg.top + 10 * u, colg.right - 3 * u, colg.bottom - 10 * u)
        drawRect(c(PAPEL), pint.topLeft, pint.size)
        drawPath(Path().apply {
            moveTo(pint.left, pint.bottom - 8 * u); lineTo(pint.left + pint.width * 0.35f, pint.top + pint.height * 0.35f)
            lineTo(pint.left + pint.width * 0.55f, pint.top + pint.height * 0.6f); lineTo(pint.left + pint.width * 0.8f, pint.top + pint.height * 0.25f)
            lineTo(pint.right, pint.bottom - 14 * u); lineTo(pint.right, pint.bottom); lineTo(pint.left, pint.bottom); close()
        }, c(TINTA).copy(alpha = 0.35f))
        drawRect(c(BERMELLON), Offset(pint.right - 6 * u, pint.bottom - 8 * u), Size(4 * u, 4 * u))
        drawRoundRect(c(PALISANDRO), Offset(colg.left - 3 * u, colg.bottom - 2 * u), Size(colg.width + 6 * u, 4 * u), CornerRadius(2 * u))
        // El suelo.
        val piso = h * 0.94f
        drawRect(c(SUELO), Offset(0f, h * 0.5f), Size(w, h * 0.5f))
        var fx = -w * 0.2f
        while (fx < w * 1.2f) { drawLine(Color.Black.copy(alpha = 0.15f), Offset(w / 2 + (fx - w / 2) * 0.6f, h * 0.5f), Offset(fx, h), 1 * u); fx += w * 0.12f }

        // ------------------------------------------------ la mesa baja
        val m0 = h * 0.47f; val m1 = h * 0.8f
        val tablero = plano(w * 0.04f, w * 0.96f, m0, m1, w * 0.07f)
        drawPath(tablero, c(PALISANDRO))
        clipPath(tablero) { vetas(Rect(0f, m0, w, m1), c(Color(0xFF2A140C)).copy(alpha = 0.4f), 5, densidad = 0.6f, grosor = 0.7f) }
        drawPath(tablero, c(PALISANDRO_LUZ), style = Stroke(1.4f * u))
        // El canto, el faldón con su nube recortada y las patas de casco.
        drawRect(c(PALISANDRO_LUZ), Offset(w * 0.04f, m1), Size(w * 0.92f, 6 * u))
        val falda = Rect(w * 0.07f, m1 + 6 * u, w * 0.93f, m1 + 22 * u)
        drawRect(c(PALISANDRO), falda.topLeft, falda.size)
        nube(Offset(w / 2, falda.top + 7 * u), 9 * u, c(ORO).copy(alpha = 0.7f), 1.2f * u)
        drawPath(Path().apply {
            moveTo(falda.left + 20 * u, falda.bottom); quadraticTo(w / 2, falda.top + 10 * u, falda.right - 20 * u, falda.bottom); close()
        }, c(SUELO))
        listOf(falda.left, falda.right - 12 * u).forEachIndexed { j, x ->
            drawRect(c(PALISANDRO), Offset(x, falda.top), Size(12 * u, piso - falda.top))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(x + 8 * u, falda.top), Size(4 * u, piso - falda.top))
            val pie = Path().apply {
                moveTo(x, piso - 8 * u); quadraticTo(x + (if (j == 0) -6 * u else 18 * u), piso - 2 * u, x + (if (j == 0) -4 * u else 16 * u), piso)
                lineTo(x + (if (j == 0) 12 * u else 0f), piso); close()
            }
            drawPath(pie, c(PALISANDRO_LUZ))
        }
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(w * 0.04f, piso - 2 * u), Size(w * 0.92f, 6 * u))

        // ------------------------------------------------ la lámpara de aceite, atrás a la izquierda
        val base = Offset(w * 0.14f, m0 + 14 * u)
        drawOval(c(BRONCE), Offset(base.x - 9 * u, base.y - 3 * u), Size(18 * u, 6 * u))
        drawRect(c(BRONCE), Offset(base.x - 1.6f * u, base.y - 40 * u), Size(3.2f * u, 38 * u))
        drawOval(c(BRONCE), Offset(base.x - 4 * u, base.y - 22 * u), Size(8 * u, 4 * u))
        val plato = Rect(base.x - 11 * u, base.y - 46 * u, base.x + 11 * u, base.y - 39 * u)
        drawArc(c(BRONCE), 0f, 180f, true, Offset(plato.left, plato.top - 3 * u), Size(plato.width, plato.height + 4 * u))
        drawLine(c(TINTA), Offset(plato.right - 4 * u, plato.top), Offset(plato.right - 1 * u, plato.top - 3 * u), 1 * u)
        val llama = Offset(plato.right - 1 * u, plato.top - 3 * u)
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to LAMPARA.copy(alpha = 0.32f), 0.35f to LAMPARA.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.9f))
            haloMedievo(llama - Offset(0f, 4 * u), 26 * u, LAMPARA, 0.7f)
            llamaMedievo(llama, 10 * u, LAMPARA)
        }
        // El bote de pinceles y el gotero de celadón, atrás a la derecha.
        val bote = Rect(w * 0.82f, m0 - 4 * u, w * 0.82f + 18 * u, m0 + 20 * u)
        listOf(-6f, -1f, 4f, 8f).forEachIndexed { j, dx ->
            val pie0 = Offset(bote.center.x + dx * 0.6f * u, bote.top + 4 * u)
            val punta = Offset(bote.center.x + dx * 1.6f * u, bote.top - (24 + 6 * j % 3) * u)
            drawLine(c(BAMBU), pie0, punta, 2 * u)
            drawLine(c(TINTA), punta, punta + Offset(dx * 0.15f * u, -6 * u), 2.6f * u, cap = StrokeCap.Round)
        }
        drawRect(c(mezcla(BAMBU, Color.Black, 0.1f)), bote.topLeft, bote.size)
        listOf(0.3f, 0.7f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.3f), Offset(bote.left, bote.top + bote.height * f), Offset(bote.right, bote.top + bote.height * f), 1 * u) }
        drawOval(c(mezcla(BAMBU, Color.White, 0.15f)), Offset(bote.left, bote.top - 2.5f * u), Size(bote.width, 5 * u))
        val got = Offset(w * 0.75f, m0 + 14 * u)
        drawOval(c(CELADON), Offset(got.x - 9 * u, got.y - 8 * u), Size(18 * u, 12 * u))
        drawRect(c(CELADON), Offset(got.x - 2.5f * u, got.y - 12 * u), Size(5 * u, 5 * u))
        drawOval(Color.White.copy(alpha = 0.3f), Offset(got.x - 6 * u, got.y - 6 * u), Size(5 * u, 3 * u))

        // ------------------------------------------------ el libro plegado
        val yb = h * 0.745f
        val L = w * 0.22f; val R = w * 0.78f
        val prof = h * 0.25f
        val p = (R - L) * 0.06f
        val yt = yb - g
        val tapa = plano(L, R, yt - prof, yt, p)
        drawPath(plano(L + 3 * u, R + 4 * u, yt - prof + 4 * u, yb + 4 * u, p), Color.Black.copy(alpha = 0.3f))
        // El canto de delante: los pliegues apilados, uno claro y uno en sombra.
        drawRect(c(SEDAS[0]), Offset(L, yb - 2.5f * u), Size(R - L, 2.5f * u))
        drawRect(hoja, Offset(L + 1 * u, yt), Size(R - L - 2 * u, g - 2.5f * u))
        var py = yt + 1.2f * u; var pk = 0
        while (py < yb - 2.5f * u) {
            drawLine(if (pk % 2 == 0) Color.Black.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.3f), Offset(L + 1 * u, py), Offset(R - 1 * u, py), 0.6f * u)
            py += 1.4f * u; pk++
        }
        // Las cintas de seda que asoman por delante: una más cada 50 pliegues.
        val cintas = (1 + e.hojas / 50).coerceAtMost(5)
        for (j in 0 until cintas) {
            val cx = L + (R - L) * (0.18f + 0.15f * j)
            drawRect(c(SEDAS[(j + 1) % SEDAS.size]), Offset(cx, yt), Size(4 * u, g + (6 + 5 * azar(e.semilla, j)) * u))
        }
        // La hoja de adentro y los pliegues que se despliegan a la derecha.
        if (a > 0.01f) {
            val n = 1 + (a * 4).toInt()
            val ancho = (R - L) * 0.42f * a
            for (k in n downTo 1) {
                val x0 = R - 2 * u + ancho * (k - 1) / n; val x1 = R - 2 * u + ancho * k / n
                val panel = Path().apply { moveTo(x0 - p, yt - prof); lineTo(x1 - p * 0.9f, yt - prof + (if (k % 2 == 0) 0f else 3 * u)); lineTo(x1, yt + (if (k % 2 == 0) 0f else 3 * u)); lineTo(x0, yt); close() }
                drawPath(panel, if (k % 2 == 0) hoja else mezcla(hoja, Color.Black, 0.12f))
                drawPath(panel, Color.Black.copy(alpha = 0.2f), style = Stroke(0.6f * u))
            }
            val pag = plano(L + 2 * u, R - 2 * u, yt - prof + 2 * u, yt - 1 * u, p)
            drawPath(pag, hoja)
            // Las columnas de escritura, de arriba abajo y de derecha a izquierda.
            var cx = R - p - 12 * u; var k = 0
            while (cx > L + p + 10 * u) {
                var cy = yt - prof + 10 * u; var j = 0
                while (cy < yt - 10 * u) {
                    val l = (3 + 4 * azar(e.semilla + k, j)) * u
                    drawLine(c(TINTA).copy(alpha = 0.6f), Offset(cx, cy), Offset(cx, minOf(cy + l, yt - 10 * u)), 1.4f * u)
                    cy += l + 2.6f * u; j++
                }
                cx -= 8 * u; k++
            }
            drawRect(c(BERMELLON), Offset(L + p + 8 * u, yt - 22 * u), Size(10 * u, 10 * u))
        }
        // La tapa de seda índigo con nubes de oro: gira sobre el lomo (a la izquierda).
        val sx = 1f - 0.94f * a
        scale(sx, 1f, Offset(L, yt)) {
            drawPath(tapa, c(SEDAS[0]))
            clipPath(tapa) {
                var ny = yt - prof + 10 * u; var fila = 0
                while (ny < yt) {
                    var nx = L + (if (fila % 2 == 0) 14 * u else 30 * u)
                    while (nx < R) { nube(Offset(nx, ny), 5 * u, c(ORO).copy(alpha = 0.35f), 0.8f * u); nx += 32 * u }
                    ny += 18 * u; fila++
                }
            }
            drawPath(tapa, c(ORO).copy(alpha = 0.7f), style = Stroke(1.2f * u))
            // El rótulo de papel con el nombre, enmarcado en rojo, y la tira con la cuenta.
            val et = Rect(L + p + (R - L) * 0.12f, yt - prof * 0.86f, R - p - (R - L) * 0.12f, yt - prof * 0.42f)
            drawRect(Color.Black.copy(alpha = 0.3f), et.topLeft + Offset(1.4f * u, 1.8f * u), et.size)
            drawRect(hoja, et.topLeft, et.size)
            drawRect(c(BERMELLON), et.deflate(2.5f * u).topLeft, et.deflate(2.5f * u).size, style = Stroke(0.9f * u))
            textoEn(medidor, e.nombre, et.deflate(6 * u), letra, c(TINTA), 15f, minimo = 8f)
            val tira = Rect(L + p + (R - L) * 0.2f, yt - prof * 0.34f, R - p - (R - L) * 0.2f, yt - prof * 0.1f)
            drawRect(hoja, tira.topLeft, tira.size)
            drawRect(c(ORO), tira.topLeft, tira.size, style = Stroke(0.8f * u))
            textoEn(medidor, e.cuenta, tira.deflate(3 * u), letra, c(TINTA).copy(alpha = 0.85f), 10f, minimo = 6f)
            if (a > 0f) drawPath(tapa, Color.Black.copy(alpha = 0.45f * a))
        }
        // El lomo, a la izquierda.
        drawLine(mezcla(c(SEDAS[0]), Color.Black, 0.3f), Offset(L + p, yt - prof), Offset(L, yb), 3 * u)

        // ------------------------------------------------ la piedra de tinta y el apoyapinceles, delante
        val piedra = plano(w * 0.04f, w * 0.18f, m1 - 40 * u, m1 - 10 * u, 3 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.045f, m1 - 10 * u), Size(w * 0.14f, 4 * u))
        drawPath(piedra, c(PIEDRA_TINTA))
        drawRect(c(mezcla(PIEDRA_TINTA, Color.Black, 0.3f)), Offset(w * 0.04f, m1 - 10 * u), Size(w * 0.14f, 4 * u))
        drawOval(c(Color(0xFF111014)), Offset(w * 0.07f, m1 - 36 * u), Size(w * 0.08f, 9 * u))
        drawOval(Color.White.copy(alpha = 0.12f), Offset(w * 0.08f, m1 - 34 * u), Size(w * 0.03f, 3 * u))
        rotate(-12f, Offset(w * 0.12f, m1 - 18 * u)) {
            drawRect(c(Color(0xFF1A1A1C)), Offset(w * 0.08f, m1 - 21 * u), Size(22 * u, 6 * u))
            drawRect(c(ORO).copy(alpha = 0.6f), Offset(w * 0.08f + 6 * u, m1 - 20 * u), Size(8 * u, 4 * u), style = Stroke(0.6f * u))
        }
        val apoyo = Offset(w * 0.88f, m1 - 12 * u)
        drawPath(Path().apply {
            moveTo(apoyo.x - 16 * u, apoyo.y); lineTo(apoyo.x - 10 * u, apoyo.y - 7 * u); lineTo(apoyo.x - 5 * u, apoyo.y - 3 * u)
            lineTo(apoyo.x, apoyo.y - 11 * u); lineTo(apoyo.x + 5 * u, apoyo.y - 3 * u); lineTo(apoyo.x + 10 * u, apoyo.y - 7 * u)
            lineTo(apoyo.x + 16 * u, apoyo.y); close()
        }, c(JADE))
        drawLine(c(BAMBU), Offset(apoyo.x - 26 * u, apoyo.y - 9 * u), Offset(apoyo.x + 18 * u, apoyo.y - 4 * u), 2.2f * u)
        drawLine(c(TINTA), Offset(apoyo.x - 26 * u, apoyo.y - 9 * u), Offset(apoyo.x - 34 * u, apoyo.y - 10 * u), 3 * u, cap = StrokeCap.Round)
    }

    /**
     * La pestaña de un día: una tira de papel colgada de una cinta de seda labrada (del color que toque), con la letra de la
     * semana en oro sobre la seda, el día en medio y, abajo, el sello cuadrado del color del ánimo (sin ánimo, solo su marco).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val seda = c(SEDAS[(e.semilla and 0x7fffffff) % SEDAS.size])
        val tira = Rect(5 * u, 3 * u, w - 5 * u, h - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), tira.topLeft + Offset(1.2f * u, 1.6f * u), tira.size)
        drawRect(papel(oscuro, false), tira.topLeft, tira.size)
        val cab = Rect(tira.left, tira.top, tira.right, tira.top + 13 * u)
        drawRect(seda, cab.topLeft, cab.size)
        drawLine(c(ORO), Offset(cab.left, cab.bottom), Offset(cab.right, cab.bottom), 1 * u)
        listOf(cab.left + 4 * u, cab.right - 4 * u).forEach { drawCircle(c(ORO).copy(alpha = 0.6f), 1 * u, Offset(it, cab.center.y)) }
        textoEn(medidor, e.semana, cab, letra, mezcla(c(ORO), ORO, 0.4f), 9f, peso = FontWeight.Bold)
        textoEn(medidor, e.dia, Rect(tira.left, cab.bottom + 1 * u, tira.right, tira.bottom - 13 * u), letra, c(TINTA), 17f, peso = FontWeight.Bold, minimo = 9f)
        val sello = Rect(w / 2 - 6 * u, tira.bottom - 14 * u, w / 2 + 6 * u, tira.bottom - 2 * u)
        if (e.tono != null) {
            drawRect(e.tono, sello.topLeft, sello.size)
            drawRect(papel(oscuro, false).copy(alpha = 0.7f), sello.deflate(2.5f * u).topLeft, sello.deflate(2.5f * u).size, style = Stroke(0.7f * u))
        } else {
            drawRect(c(BERMELLON).copy(alpha = 0.45f), sello.topLeft, sello.size, style = Stroke(0.8f * u))
        }
        drawRect(c(BERMELLON).copy(alpha = 0.5f), tira.topLeft, tira.size, style = Stroke(0.6f * u))
    }

    /**
     * El rótulo de un mes: una banda de seda índigo con su orla de brocado en oro y bermellón, una nube de oro en cada punta
     * y, en medio, la tira de papel con el mes.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val banda = Rect(2 * u, 2 * u, w - 2 * u, h - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), banda.topLeft + Offset(1.2f * u, 1.6f * u), banda.size)
        drawRect(c(SEDAS[0]), banda.topLeft, banda.size)
        brocado(Rect(banda.left, banda.top, banda.right, banda.top + 5 * u), c(ORO), c(BERMELLON), null)
        brocado(Rect(banda.left, banda.bottom - 5 * u, banda.right, banda.bottom), c(ORO), c(BERMELLON), null)
        listOf(banda.left + 18 * u, banda.right - 18 * u).forEach { x -> nube(Offset(x, h / 2 - 2 * u), 7 * u, c(ORO), 1.2f * u) }
        val tira = Rect(banda.left + 38 * u, banda.top + 7 * u, banda.right - 38 * u, banda.bottom - 7 * u)
        drawRect(papel(oscuro, false), tira.topLeft, tira.size)
        drawRect(c(BERMELLON), tira.topLeft, tira.size, style = Stroke(0.8f * u))
        textoEn(medidor, texto, tira.deflate(2 * u), letra, c(TINTA), 13f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El cinabrio del sello, la seda azul de la faja y el bambú seco del casillero. */
    private val CINABRIO = Color(0xFFC0392B)
    private val FAJA = Color(0xFF2B4A9E)
    private val BAMBU_SECO = Color(0xFFB89B58)

    /**
     * El sello de cinabrio en [o], de lado [s]: el cuadrado rojo estampado (algo gastado en los bordes), con su marco en
     * reserva y unos trazos inventados, que no son escritura.
     */
    private fun DrawScope.selloCinabrio(o: Offset, s: Float, oscuro: Boolean) {
        val rojo = if (oscuro) mezcla(CINABRIO, Color.Black, 0.22f) else CINABRIO
        val papel = if (oscuro) mezcla(PAPEL, Color(0xFF110B09), 0.55f) else PAPEL
        val r = Rect(o.x - s / 2, o.y - s / 2, o.x + s / 2, o.y + s / 2)
        drawRoundRect(rojo, r.topLeft, r.size, CornerRadius(s * 0.06f))
        val g = s * 0.065f
        drawRect(papel, r.topLeft + Offset(s * 0.1f, s * 0.1f), Size(s * 0.8f, s * 0.8f), style = Stroke(g))
        fun t(x0: Float, y0: Float, x1: Float, y1: Float) = drawLine(papel, Offset(r.left + s * x0, r.top + s * y0), Offset(r.left + s * x1, r.top + s * y1), g, StrokeCap.Square)
        t(0.27f, 0.24f, 0.27f, 0.76f); t(0.18f, 0.4f, 0.44f, 0.4f); t(0.2f, 0.6f, 0.42f, 0.6f)
        t(0.56f, 0.24f, 0.8f, 0.24f); t(0.68f, 0.24f, 0.68f, 0.78f); t(0.56f, 0.5f, 0.8f, 0.56f); t(0.56f, 0.76f, 0.8f, 0.76f)
        // Lo que no tomó la tinta: unas motas de papel dentro del rojo.
        for (j in 0 until 7) drawCircle(papel.copy(alpha = 0.7f), s * 0.025f, Offset(r.left + s * (0.12f + 0.76f * azar(j, 7)), r.top + s * (0.12f + 0.76f * azar(j, 9))))
    }

    /**
     * La carta va en un sobre largo de papel de arroz, con la solapa recta que baja de arriba y unas nubes de oro apenas
     * impresas. Se cierra con el sello de cinabrio estampado a caballo entre la solapa y el sobre, como se hacía para que
     * no se pudiera abrir sin partirlo; las selladas al futuro van además ceñidas por la faja de seda azul. A quién va y las
     * fechas, en la etiqueta roja con su filete de oro, en tinta clara. Al abrirse el sello se parte justo por el filo de la
     * solapa (media estampa se va con ella), la solapa se levanta y sale la carta. En oscuro el papel se hunde y la etiqueta
     * sigue legible.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF110B09), 0.58f) else x
        val p = Rect(c.left + W * 0.05f, c.top + H * 0.08f, c.right - W * 0.05f, c.bottom - H * 0.07f)
        val cx = p.center.x
        val largo = p.height * 0.32f
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val filo = p.top + largo
        val lado = minOf(W, H) * 0.2f
        val sello = Offset(cx + p.width * 0.18f, filo)
        val partido = e.romper.coerceIn(0f, 1f)
        fun solapa(g: Float) = Path().apply {
            val r = 3 * u
            moveTo(p.left, p.top); lineTo(p.left, p.top + g * (largo - r)); quadraticTo(p.left, p.top + g * largo, p.left + r, p.top + g * largo)
            lineTo(p.right - r, p.top + g * largo); quadraticTo(p.right, p.top + g * largo, p.right, p.top + g * (largo - r)); lineTo(p.right, p.top); close()
        }
        // El sello (o su media estampa de arriba, que va en la solapa y se mueve con ella).
        fun DrawScope.selloEn(g: Float, mitad: Int) {
            val o = Offset(sello.x, p.top + g * largo)
            conAlfaMedievo(e.alfaSello.coerceIn(0f, 1f), Rect(sello, lado * 2f * e.escalaSello)) {
                scale(e.escalaSello, sello) {
                    if (mitad == 0 && e.brillo > 0f) {
                        drawCircle(Brush.radialGradient(0f to ORO.copy(alpha = 0.8f * e.brillo), 0.45f to LAMPARA.copy(alpha = 0.3f * e.brillo),
                            1f to Color.Transparent, center = o, radius = lado * 1.7f), lado * 1.7f, o)
                        rayosMedievo(o, lado * 0.8f, lado * 1.45f, ORO.copy(alpha = 0.9f * e.brillo), 12, 1.1f)
                    }
                    when {
                        partido <= 0f -> selloCinabrio(o, lado, oscuro)
                        mitad == 1 -> clipRect(o.x - lado, o.y - lado, o.x + lado, o.y) {
                            translate(0f, -partido * lado * 0.06f) { scale(1f, maxOf(kotlin.math.abs(g), 0.05f), Offset(o.x, o.y)) { selloCinabrio(o, lado, oscuro) } }
                        }
                        else -> clipRect(o.x - lado, o.y, o.x + lado, o.y + lado) { selloCinabrio(o, lado, oscuro) }
                    }
                }
            }
        }

        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(p.left + 2 * u, p.top + 3 * u), p.size, CornerRadius(2 * u))
        if (giro < 0f) {
            drawPath(solapa(giro), k(Color(0xFFDCCFAE)))
            drawPath(solapa(giro), k(Color(0xFF9A8C6E)), style = Stroke(0.8f * u))
            if (partido > 0f) selloEn(giro, 1)
        }
        val sale = salidaMedievo(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaMedievo(Rect(cx - p.width * 0.42f, p.top + 3 * u - sube, cx + p.width * 0.42f, p.bottom - 6 * u - sube), e, oscuro)
        }
        // El sobre de papel de arroz, con sus fibras.
        drawRect(k(PAPEL), p.topLeft, p.size)
        clipRect(p.left, p.top, p.right, p.bottom) {
            for (j in 0 until 26) {
                val a = Offset(p.left + p.width * azar(e.semilla, 120 + j), p.top + p.height * azar(e.semilla, 150 + j))
                drawLine(k(Color(0xFFB9A880)).copy(alpha = 0.35f), a, a + Offset(p.width * 0.08f * (azar(e.semilla, 180 + j) - 0.3f), p.height * 0.05f * (azar(e.semilla, 200 + j) - 0.5f)), 0.6f * u)
            }
        }
        drawRect(k(Color(0xFF9A8C6E)).copy(alpha = 0.6f), p.topLeft, p.size, style = Stroke(0.8f * u))
        // La etiqueta roja con su filete de oro, con lo escrito.
        val faja = p.left + p.width * 0.1f
        val et = Rect(faja + W * 0.08f, filo + lado * 0.62f, p.right - W * 0.05f, p.bottom - 6 * u)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            val rojo = if (oscuro) mezcla(BERMELLON, Color(0xFF110B09), 0.3f) else BERMELLON
            drawRect(Color.Black.copy(alpha = 0.22f), et.topLeft + Offset(1 * u, 1.5f * u), et.size)
            drawRect(rojo, et.topLeft, et.size)
            drawRect(ORO, et.topLeft + Offset(2 * u, 2 * u), Size(et.width - 4 * u, et.height - 4 * u), style = Stroke(0.8f * u))
            drawRect(ORO.copy(alpha = 0.6f), et.topLeft + Offset(3.6f * u, 3.6f * u), Size(et.width - 7.2f * u, et.height - 7.2f * u), style = Stroke(0.5f * u))
            val m = et.top + et.height * 0.56f
            textoEn(medidor, e.linea1, Rect(et.left + 6 * u, et.top + 3 * u, et.right - 6 * u, m), letra, MARFIL, 12f, peso = FontWeight.SemiBold, minimo = 8f)
            textoEn(medidor, e.linea2, Rect(et.left + 6 * u, m - 1 * u, et.right - 6 * u, et.bottom - 3 * u), letra, Color(0xFFF3D98E), 9f, minimo = 7f)
        }
        // Cerrado: la solapa por delante, con sus nubes de oro.
        if (giro >= 0f) {
            translate(0f, 1.6f * u) { drawPath(solapa(giro), Color.Black.copy(alpha = 0.2f)) }
            drawPath(solapa(giro), k(Color(0xFFF7EEDA)))
            if (giro > 0.3f) clipPath(solapa(giro)) {
                scale(1f, giro, Offset(cx, p.top)) {
                    listOf(0.2f, 0.5f, 0.8f).forEachIndexed { j, f -> nube(Offset(p.left + p.width * f - (if (j == 1) p.width * 0.08f else 0f), p.top + largo * 0.42f), largo * 0.32f, k(ORO).copy(alpha = 0.45f), 0.9f * u) }
                }
            }
            drawPath(solapa(giro), k(Color(0xFF9A8C6E)), style = Stroke(0.8f * u))
        }
        // La faja de seda azul, que baja ciñendo el sobre (las selladas al futuro).
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val ancho = W * 0.075f
            val alto = (p.height + 4 * u) * ata
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(faja + 1.5f * u, p.top - 2 * u), Size(ancho, alto))
            drawRect(k(FAJA), Offset(faja, p.top - 2 * u), Size(ancho, alto))
            clipRect(faja, p.top - 2 * u, faja + ancho, p.top - 2 * u + alto) {
                var y = p.top
                while (y < p.bottom) { drawLine(Color.White.copy(alpha = 0.12f), Offset(faja, y), Offset(faja + ancho, y + ancho * 0.6f), 0.7f * u); y += 3 * u }
                drawRect(k(ORO).copy(alpha = 0.8f), Offset(faja + ancho * 0.18f, p.top - 2 * u), Size(0.8f * u, alto))
                drawRect(k(ORO).copy(alpha = 0.8f), Offset(faja + ancho * 0.82f - 0.8f * u, p.top - 2 * u), Size(0.8f * u, alto))
            }
        }
        if (partido <= 0f) selloEn(maxOf(giro, 0f), 0)
        else {
            selloEn(1f, 0)
            if (giro >= 0f) selloEn(giro, 1)
        }
    }

    override val cajaPrevia = Rect(0.165f, 0.08f, 0.835f, 0.25f)

    override val altoBuzon: Dp = 206.dp

    /**
     * El buzón es el casillero de bambú del caravasar: contra el muro de adobe encalado, un armazón de cañas atadas con
     * cordel, en dos pisos de cinco casillas, con un sobre de pie en cada una (el sobre vertical con su tira roja). Los que
     * ya se pueden abrir llevan el sello de cinabrio encendido; los sellados, la faja azul; los abiertos, la solapa abierta y
     * la carta asomando. A la derecha, colgadas de un clavo con sus cordones y borlas, tres tablillas de laca roja con la
     * cuenta en oro; arriba, un farol de papel que en oscuro se enciende.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF110B09), 0.58f) else x
        // El muro encalado y el zócalo de ladrillo gris.
        drawRect(k(MURO))
        for (j in 0 until 10) drawCircle(k(Color(0xFFB8A988)).copy(alpha = 0.12f), h * (0.08f + 0.1f * azar(j, 11)), Offset(w * azar(j, 12), h * 0.8f * azar(j, 13)))
        val zocalo = h * 0.9f
        drawRect(k(LADRILLO), Offset(0f, zocalo), Size(w, h - zocalo))
        var lx = 0f; var lj = 0
        while (lx < w) { drawLine(k(Color(0xFF5A5650)), Offset(lx, zocalo), Offset(lx, h), 1 * u); lx += 18 * u; lj++ }
        drawLine(k(Color(0xFF5A5650)), Offset(0f, zocalo + (h - zocalo) / 2), Offset(w, zocalo + (h - zocalo) / 2), 1 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, zocalo), Size(w, 2 * u))
        // El farol de papel, arriba a la derecha.
        val farol = Offset(w * 0.86f, h * 0.13f)
        if (oscuro) drawRect(Brush.radialGradient(0f to LAMPARA.copy(alpha = 0.34f), 0.4f to LAMPARA.copy(alpha = 0.12f), 1f to Color.Transparent,
            center = farol, radius = w * 0.75f))
        drawLine(k(TINTA), Offset(farol.x, 0f), Offset(farol.x, farol.y - 12 * u), 1 * u)
        val globo = Rect(farol.x - 11 * u, farol.y - 12 * u, farol.x + 11 * u, farol.y + 12 * u)
        drawOval(if (oscuro) LAMPARA else k(Color(0xFFE8C98E)), globo.topLeft, globo.size)
        for (j in 1..4) drawLine(k(BAMBU).copy(alpha = 0.6f), Offset(globo.left + 1 * u, globo.top + globo.height * j / 5), Offset(globo.right - 1 * u, globo.top + globo.height * j / 5), 0.7f * u)
        drawRect(k(LACA), Offset(globo.left + 5 * u, globo.top - 2 * u), Size(globo.width - 10 * u, 3 * u))
        drawRect(k(LACA), Offset(globo.left + 5 * u, globo.bottom - 1 * u), Size(globo.width - 10 * u, 3 * u))
        drawLine(k(CORDON), Offset(farol.x, globo.bottom + 2 * u), Offset(farol.x, globo.bottom + 12 * u), 2 * u)
        if (oscuro) haloMedievo(farol, 26 * u, LAMPARA, 0.6f)

        // ------------------------------------------------ el casillero de bambú
        val caja = Rect(w * 0.035f, h * 0.06f, w * 0.69f, zocalo - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), caja.topLeft + Offset(3 * u, 4 * u), caja.size)
        drawRect(k(Color(0xFF4A3A24)), caja.topLeft, caja.size)
        val cols = 5; val filas = 2
        val cw = caja.width / cols; val ch = caja.height / filas
        // El fondo de cada casilla: esterilla de caña.
        for (f in 0 until filas) for (cI in 0 until cols) {
            val r = Rect(caja.left + cw * cI, caja.top + ch * f, caja.left + cw * (cI + 1), caja.top + ch * (f + 1))
            var x = r.left + 2 * u
            while (x < r.right) { drawLine(k(Color(0xFF6A5434)), Offset(x, r.top), Offset(x, r.bottom), 1.2f * u); x += 3.2f * u }
            drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft, Size(r.width, 5 * u))
        }
        val cartas = repartoMedievo(e, cols * filas)
        cartas.forEachIndexed { i, clase ->
            val f = i / cols; val cI = i % cols
            val celda = Rect(caja.left + cw * cI, caja.top + ch * f, caja.left + cw * (cI + 1), caja.top + ch * (f + 1))
            val s = Rect(celda.center.x - cw * 0.3f, celda.top + ch * 0.2f, celda.center.x + cw * 0.3f, celda.bottom - 3 * u)
            rotate((azar(i, 21) - 0.5f) * 6f, Offset(s.center.x, s.bottom)) {
                if (clase == 2) {
                    // Abierto: la carta de papel índigo asoma por arriba, con sus columnas.
                    val hoja = Rect(s.left + 3 * u, s.top - ch * 0.14f, s.right - 3 * u, s.top + 10 * u)
                    drawRect(k(Color(0xFF1F3578)), hoja.topLeft, hoja.size)
                    for (j in 0 until 3) drawLine(k(MARFIL).copy(alpha = 0.6f), Offset(hoja.left + hoja.width * (0.3f + j * 0.2f), hoja.top + 3 * u), Offset(hoja.left + hoja.width * (0.3f + j * 0.2f), hoja.bottom), 0.8f * u)
                }
                drawRect(Color.Black.copy(alpha = 0.3f), s.topLeft + Offset(1.5f * u, 0f), s.size)
                drawRect(k(PAPEL), s.topLeft, s.size)
                // La tira roja a lo largo, con su filete de oro.
                val tira = Rect(s.center.x - s.width * 0.18f, s.top + s.height * 0.22f, s.center.x + s.width * 0.18f, s.bottom - s.height * 0.12f)
                drawRect(k(BERMELLON), tira.topLeft, tira.size)
                drawRect(k(ORO), tira.topLeft, tira.size, style = Stroke(0.7f * u))
                for (j in 0 until 3) drawLine(k(MARFIL).copy(alpha = 0.7f), Offset(tira.center.x, tira.top + tira.height * (0.18f + j * 0.26f)), Offset(tira.center.x, tira.top + tira.height * (0.3f + j * 0.26f)), 1 * u)
                if (clase == 2) drawRect(k(Color(0xFFDCCFAE)), Offset(s.left, s.top - 4 * u), Size(s.width, 4 * u))
                else {
                    drawRect(k(Color(0xFFF7EEDA)), s.topLeft, Size(s.width, s.height * 0.16f))
                    drawLine(k(Color(0xFF9A8C6E)), Offset(s.left, s.top + s.height * 0.16f), Offset(s.right, s.top + s.height * 0.16f), 0.7f * u)
                    val o = Offset(s.center.x, s.top + s.height * 0.16f)
                    if (clase == 1) drawRect(k(FAJA), Offset(s.left - 1 * u, s.top + s.height * 0.55f), Size(s.width + 2 * u, s.height * 0.12f))
                    else {
                        haloMedievo(o, s.width * 0.95f, ORO, if (oscuro) 0.95f else 0.75f)
                        rayosMedievo(o, s.width * 0.3f, s.width * 0.62f, ORO, 8, 0.9f)
                    }
                    selloCinabrio(o, s.width * 0.32f, oscuro)
                }
            }
        }
        // Las cañas del armazón, atadas con cordel en los cruces.
        fun cana(a: Offset, b: Offset, g: Float) {
            drawLine(k(mezcla(BAMBU_SECO, Color.Black, 0.35f)), a + Offset(1 * u, 1 * u), b + Offset(1 * u, 1 * u), g, StrokeCap.Round)
            drawLine(k(BAMBU_SECO), a, b, g, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.22f), a - Offset(g * 0.2f, g * 0.2f), b - Offset(g * 0.2f, g * 0.2f), g * 0.25f, StrokeCap.Round)
            val largo = (b - a).getDistance(); val dir = (b - a) / largo
            var d = 22 * u
            while (d < largo - 6 * u) { val m = a + dir * d; drawLine(k(mezcla(BAMBU_SECO, Color.Black, 0.3f)), m - Offset(dir.y, -dir.x) * (g / 2), m + Offset(dir.y, -dir.x) * (g / 2), 1.2f * u); d += 26 * u }
        }
        val g = 5 * u
        for (f in 0..filas) cana(Offset(caja.left - 4 * u, caja.top + ch * f), Offset(caja.right + 4 * u, caja.top + ch * f), g)
        for (cI in 0..cols) cana(Offset(caja.left + cw * cI, caja.top - 4 * u), Offset(caja.left + cw * cI, caja.bottom + 3 * u), g)
        for (f in 0..filas) for (cI in 0..cols) {
            val o = Offset(caja.left + cw * cI, caja.top + ch * f)
            drawLine(k(CORDON), o - Offset(g * 0.8f, g * 0.8f), o + Offset(g * 0.8f, g * 0.8f), 1.6f * u)
            drawLine(k(CORDON), o - Offset(g * 0.8f, -g * 0.8f), o + Offset(g * 0.8f, -g * 0.8f), 1.6f * u)
        }
        // Las tres tablillas de laca roja con la cuenta, colgadas de su clavo.
        val clavo = Offset(w * 0.845f, h * 0.29f)
        drawCircle(k(TINTA), 2 * u, clavo)
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "selladas", e.abiertas to "abiertas")
        rotulos.forEachIndexed { i, (n, nombre) ->
            val r = Rect(w * 0.725f, h * (0.33f + 0.19f * i), w * 0.965f, h * (0.33f + 0.19f * i) + h * 0.155f)
            if (i == 0) {
                drawLine(k(CORDON), clavo, Offset(r.left + 6 * u, r.top), 1.2f * u)
                drawLine(k(CORDON), clavo, Offset(r.right - 6 * u, r.top), 1.2f * u)
            } else drawLine(k(CORDON), Offset(r.center.x, r.top - h * 0.035f), Offset(r.center.x, r.top), 1.2f * u)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(2 * u, 2.5f * u), r.size, CornerRadius(2 * u))
            drawRoundRect(if (oscuro) mezcla(LACA, Color(0xFF110B09), 0.25f) else LACA, r.topLeft, r.size, CornerRadius(2 * u))
            drawRoundRect(ORO, r.topLeft + Offset(2 * u, 2 * u), Size(r.width - 4 * u, r.height - 4 * u), CornerRadius(1.5f * u), style = Stroke(0.8f * u))
            val m = r.left + r.width * 0.38f
            textoEn(medidor, "$n", Rect(r.left + 3 * u, r.top, m, r.bottom), letra, Color(0xFFF6DE96), 17f, peso = FontWeight.Bold)
            textoEn(medidor, nombre, Rect(m, r.top + 2 * u, r.right - 4 * u, r.bottom - 2 * u), letra, MARFIL, 9.5f, minimo = 7f)
            if (i == 2) {
                // La borla de abajo.
                drawLine(k(CORDON), Offset(r.center.x, r.bottom), Offset(r.center.x, r.bottom + 5 * u), 1.2f * u)
                drawPath(Path().apply { moveTo(r.center.x - 1.5f * u, r.bottom + 5 * u); lineTo(r.center.x + 1.5f * u, r.bottom + 5 * u); lineTo(r.center.x + 3.5f * u, r.bottom + 13 * u); lineTo(r.center.x - 3.5f * u, r.bottom + 13 * u); close() }, k(CORDON))
            }
        }
    }

    // ---------------------------------------------------------------- lo de alrededor

    /** El papel de lo de alrededor: en modo oscuro se hunde de verdad, para leerse con tinta de marfil. */
    private fun papelHondo(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, Color(0xFF110D0C), 0.74f) else PAPEL
    /** La tinta sobre [papelHondo]: la negra de humo de día, el marfil en modo oscuro. */
    private fun tintaHonda(oscuro: Boolean): Color = if (oscuro) MARFIL else TINTA

    /** Una nube de oro (tres lóbulos sobre una base) centrada en [o], de ancho [a]. */
    private fun DrawScope.nubecilla(o: Offset, a: Float, color: Color) {
        drawCircle(color, a * 0.22f, Offset(o.x - a * 0.22f, o.y))
        drawCircle(color, a * 0.28f, Offset(o.x + a * 0.05f, o.y - a * 0.08f))
        drawCircle(color, a * 0.2f, Offset(o.x + a * 0.3f, o.y + a * 0.02f))
        drawRoundRect(color, Offset(o.x - a * 0.45f, o.y), Size(a * 0.9f, a * 0.2f), CornerRadius(a * 0.1f))
    }

    /**
     * El emblema de la pestaña [k] en [o], de radio [r]: el rollo de pie con su perilla (diario), el libro plegado en
     * acordeón (momentos), la media luna sobre una nube (sueños), el pincel (ideario) y la carta atada con su cordón
     * (cartas). [c] es el oro del dibujo y [f] la seda de debajo.
     */
    private fun DrawScope.emblema(k: Int, o: Offset, r: Float, c: Color, f: Color) {
        val u = density
        when (k) {
            0 -> {
                drawRect(c, Offset(o.x - r * 0.45f, o.y - r * 0.8f), Size(r * 0.9f, r * 1.6f))
                drawRect(c, Offset(o.x - r * 0.62f, o.y - r * 0.98f), Size(r * 1.24f, r * 0.2f))
                drawRect(c, Offset(o.x - r * 0.62f, o.y + r * 0.78f), Size(r * 1.24f, r * 0.2f))
                drawRect(f, Offset(o.x - r * 0.12f, o.y - r * 0.6f), Size(r * 0.24f, r * 1.0f))
                drawCircle(c, r * 0.16f, Offset(o.x, o.y - r * 1.12f))
            }
            1 -> {
                val p = Path().apply {
                    moveTo(o.x - r, o.y - r * 0.5f); lineTo(o.x - r * 0.5f, o.y - r * 0.8f); lineTo(o.x, o.y - r * 0.5f)
                    lineTo(o.x + r * 0.5f, o.y - r * 0.8f); lineTo(o.x + r, o.y - r * 0.5f); lineTo(o.x + r, o.y + r * 0.8f)
                    lineTo(o.x + r * 0.5f, o.y + r * 0.5f); lineTo(o.x, o.y + r * 0.8f); lineTo(o.x - r * 0.5f, o.y + r * 0.5f)
                    lineTo(o.x - r, o.y + r * 0.8f); close()
                }
                drawPath(p, c)
                listOf(-0.5f, 0f, 0.5f).forEach { x -> drawLine(f, Offset(o.x + x * r, o.y - r * 0.6f), Offset(o.x + x * r, o.y + r * 0.6f), 0.7f * u) }
            }
            2 -> {
                drawPath(mediaLunaMedievo(Offset(o.x, o.y - r * 0.2f), r * 0.78f), c)
                nubecilla(Offset(o.x - r * 0.1f, o.y + r * 0.62f), r * 1.7f, c)
            }
            3 -> {
                drawLine(c, Offset(o.x - r * 0.8f, o.y + r * 0.8f), Offset(o.x + r * 0.5f, o.y - r * 0.5f), 2f * u)
                val pelo = Path().apply {
                    moveTo(o.x + r * 0.42f, o.y - r * 0.62f); quadraticTo(o.x + r * 1.0f, o.y - r * 1.1f, o.x + r * 1.15f, o.y - r * 1.15f)
                    quadraticTo(o.x + r * 1.1f, o.y - r * 1.0f, o.x + r * 0.62f, o.y - r * 0.42f); close()
                }
                drawPath(pelo, c)
                drawCircle(c, r * 0.14f, Offset(o.x - r * 0.88f, o.y + r * 0.88f), style = Stroke(0.7f * u))
            }
            else -> {
                drawRect(c, Offset(o.x - r, o.y - r * 0.62f), Size(r * 2f, r * 1.24f))
                drawLine(f, Offset(o.x - r, o.y - r * 0.62f), Offset(o.x, o.y + r * 0.1f), 0.8f * u)
                drawLine(f, Offset(o.x + r, o.y - r * 0.62f), Offset(o.x, o.y + r * 0.1f), 0.8f * u)
                drawLine(f, Offset(o.x - r * 0.25f, o.y - r * 0.62f), Offset(o.x - r * 0.25f, o.y + r * 0.62f), 1.2f * u)
                drawCircle(f, r * 0.2f, Offset(o.x - r * 0.25f, o.y + r * 0.1f))
            }
        }
    }

    override val altoPestanas: Dp = 80.dp

    /**
     * Las pestañas: el muestrario de sedas del mercader, cinco paños colgados de una vara de bambú por sus lazos de cordón.
     * Cada paño, de un color, con su labrado en rombos, el emblema en oro, la etiqueta de papel con el nombre y la cuenta
     * bordada en marfil sobre los flecos. El elegido cuelga más largo, más vivo, con nubes de oro labradas y el sello rojo
     * del mercader en la etiqueta.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val vara = 5 * u
        for (k in 0 until 5) {
            val q = quintoMedievo(k, w, h)
            val sel = k == e.actual
            val seda0 = listOf(SEDAS[0], SEDAS[1], SEDAS[2], SEDAS[3], SEDAS[4])[k]
            val seda = c(if (sel) mezcla(seda0, Color.White, 0.1f) else mezcla(seda0, Color.Black, 0.2f))
            val r = Rect(q.left + 4 * u, vara + 2 * u, q.right - 4 * u, if (sel) h - 5 * u else h - 13 * u)
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 2 * u, r.top + 2 * u), r.size)
            drawRect(seda, r.topLeft, r.size)
            // El labrado: una celosía de rombos apenas más clara.
            clipRect(r.left, r.top, r.right, r.bottom) {
                var x = r.left - r.height; var j = 0
                while (x < r.right && j < 40) {
                    drawLine(Color.White.copy(alpha = 0.07f), Offset(x, r.bottom), Offset(x + r.height, r.top), 0.8f * u)
                    drawLine(Color.White.copy(alpha = 0.07f), Offset(x, r.top), Offset(x + r.height, r.bottom), 0.8f * u)
                    x += maxOf(7 * u, 1f); j++
                }
                if (sel) {
                    nubecilla(Offset(r.left + 8 * u, r.bottom - 15 * u), 12 * u, c(ORO).copy(alpha = 0.35f))
                    nubecilla(Offset(r.right - 9 * u, r.top + 34 * u), 11 * u, c(ORO).copy(alpha = 0.35f))
                }
            }
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.right - 3 * u, r.top), Size(3 * u, r.height))
            if (sel) drawRect(c(ORO), r.topLeft, r.size, style = Stroke(1 * u))
            // Los flecos.
            var fx = r.left + 1 * u; var j = 0
            while (fx < r.right && j < 60) { drawLine(seda, Offset(fx, r.bottom), Offset(fx, r.bottom + 3.5f * u), 0.7f * u); fx += maxOf(1.8f * u, 1f); j++ }
            // Los dos lazos de cordón alrededor de la vara.
            listOf(r.left + 6 * u, r.right - 6 * u).forEach { x ->
                drawOval(c(CORDON), Offset(x - 2.2f * u, vara - 3.5f * u), Size(4.4f * u, 7 * u), style = Stroke(1.2f * u))
            }
            val oro = if (sel) mezcla(c(ORO), ORO, 0.5f) else c(ORO).copy(alpha = 0.85f)
            emblema(k, Offset(r.center.x, r.top + 12 * u), 6 * u, oro, seda)
            // La etiqueta de papel con el nombre.
            val et = Rect(r.left + 3 * u, r.top + 22 * u, r.right - 3 * u, r.top + 37 * u)
            drawRect(c(PAPEL), et.topLeft, et.size)
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(et.left, et.bottom - 1 * u), Size(et.width, 1 * u))
            textoEn(medidor, e.nombres.getOrElse(k) { "" }, Rect(et.left + 1 * u, et.top, et.right - 1 * u, et.bottom), letra, t(TINTA, oscuro, false), 11f,
                peso = FontWeight.Bold, minimo = 7f)
            if (sel) {
                drawRect(c(CINABRIO), Offset(et.right - 5 * u, et.top - 3 * u), Size(6 * u, 6 * u))
                drawRect(c(PAPEL).copy(alpha = 0.6f), Offset(et.right - 4 * u, et.top - 2 * u), Size(4 * u, 4 * u), style = Stroke(0.6f * u))
            }
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", Rect(r.left, et.bottom + 1 * u, r.right, r.bottom - 1 * u), letra,
                mezcla(c(MARFIL), MARFIL, 0.5f), 10f, peso = FontWeight.Bold, minimo = 6f)
        }
        // La vara de bambú, con sus nudos y la sombra.
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, vara + 1.5f * u), Size(w, 2 * u))
        drawRoundRect(c(BAMBU), Offset(0f, vara - 2.5f * u), Size(w, 5 * u), CornerRadius(2.5f * u))
        drawRect(Color.White.copy(alpha = 0.18f), Offset(0f, vara - 1.8f * u), Size(w, 1 * u))
        var nx = 30 * u; var j = 0
        while (nx < w && j < 30) { drawRect(c(mezcla(BAMBU, Color.Black, 0.3f)), Offset(nx, vara - 2.5f * u), Size(1.4f * u, 5 * u)); nx += maxOf(58 * u, 1f); j++ }
    }

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.19f, 0.22f, 0.95f, 0.78f)
    override val letraCampo: FontFamily = LetraExtra.almendra
    override fun tintaCampo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El buscador: una tira de papel en un marco de laca negra con filete de oro y, a la izquierda, la brújula del
     * caravanero: la cuchara de piedra imán sobre su placa cuadrada de bronce con el círculo de los rumbos. Sin el foco la
     * cuchara mira al sur; con el foco gira hacia el texto y el filete se enciende de bermellón.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(1.5f * u, 2 * u), Size(w - 1.5f * u, h - 2 * u), CornerRadius(4 * u))
        drawRoundRect(c(NEGRO_LACA), Offset.Zero, Size(w - 1.5f * u, h - 2 * u), CornerRadius(4 * u))
        drawRoundRect(Color.White.copy(alpha = 0.08f), Offset(2 * u, 1.5f * u), Size(w - 6 * u, 3 * u), CornerRadius(1.5f * u))
        val tira = Rect(w * cajaCampo.left - 8 * u, 7 * u, w - 8 * u, h - 9 * u)
        drawRect(papelHondo(oscuro), tira.topLeft, tira.size)
        val filete = if (e.enfocado) c(BERMELLON) else c(ORO).copy(alpha = 0.8f)
        drawRect(filete, tira.inflate(2 * u).topLeft, tira.inflate(2 * u).size, style = Stroke((if (e.enfocado) 1.4f else 0.8f) * u))
        // La placa de bronce con el círculo de los rumbos.
        val o = Offset(w * cajaCampo.left * 0.46f, (h - 2 * u) / 2f)
        val l = minOf(36 * u, h - 10 * u)
        drawRect(c(BRONCE), Offset(o.x - l / 2, o.y - l / 2), Size(l, l))
        bisel(Rect(o.x - l / 2, o.y - l / 2, o.x + l / 2, o.y + l / 2), Color.White.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.35f), 1.4f * u)
        drawCircle(Color.Black.copy(alpha = 0.3f), l * 0.36f, o, style = Stroke(0.7f * u))
        for (j in 0 until 24) {
            val a = Math.toRadians(j * 15.0)
            val p0 = Offset(o.x + l * 0.4f * kotlin.math.cos(a).toFloat(), o.y + l * 0.4f * kotlin.math.sin(a).toFloat())
            val p1 = Offset(o.x + l * 0.46f * kotlin.math.cos(a).toFloat(), o.y + l * 0.46f * kotlin.math.sin(a).toFloat())
            drawLine(Color.Black.copy(alpha = 0.35f), p0, p1, 0.6f * u)
        }
        drawCircle(c(mezcla(BRONCE, Color.White, 0.2f)), l * 0.24f, o)
        // La cuchara de piedra imán: la taza y el mango que apunta.
        val giro = if (e.enfocado) 0f else 90f
        rotate(giro, o) {
            val cuchara = Path().apply {
                moveTo(o.x + l * 0.42f, o.y)
                quadraticTo(o.x + l * 0.1f, o.y - l * 0.04f, o.x - l * 0.04f, o.y - l * 0.11f)
                cubicTo(o.x - l * 0.3f, o.y - l * 0.2f, o.x - l * 0.3f, o.y + l * 0.2f, o.x - l * 0.04f, o.y + l * 0.11f)
                quadraticTo(o.x + l * 0.1f, o.y + l * 0.04f, o.x + l * 0.42f, o.y); close()
            }
            translate(0.8f * u, 1 * u) { drawPath(cuchara, Color.Black.copy(alpha = 0.3f)) }
            drawPath(cuchara, if (e.enfocado) mezcla(c(PIEDRA_TINTA), c(JADE), 0.5f) else c(PIEDRA_TINTA))
            drawCircle(Color.White.copy(alpha = 0.25f), l * 0.05f, Offset(o.x - l * 0.12f, o.y - l * 0.05f))
        }
        if (e.vacio) pistaMedievo(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), letraCampo,
            tintaHonda(oscuro).copy(alpha = 0.5f))
    }

    /**
     * Las obras: el puesto del mercader bajo su toldo. La lona de la tienda con sus costuras, el faldón rojo de arriba con
     * sus picos y sus borlas, y abajo el banco cubierto con un kilim de franjas y rombos, donde se apoyan. Distinto del
     * estante de laca y del caravasar.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(if (oscuro) mezcla(MURO, Color(0xFF110D0C), 0.68f) else MURO)
        // Las costuras de la lona.
        var x = 26 * u; var k = 0
        while (x < w && k < 30) {
            drawLine(Color.Black.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, h), 1.4f * u)
            drawLine(Color.White.copy(alpha = 0.1f), Offset(x + 1.6f * u, 0f), Offset(x + 1.6f * u, h), 0.7f * u)
            x += maxOf(52 * u, 1f); k++
        }
        // El faldón rojo con sus picos y sus borlas.
        val f = 9 * u
        val faldon = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, f)
            var px = w; var j = 0
            while (px > 0 && j < 80) { lineTo(px - 7 * u, f + 6 * u); lineTo(px - 14 * u, f); px -= maxOf(14 * u, 1f); j++ }
            lineTo(0f, f); close()
        }
        drawPath(faldon, c(LACA))
        drawRect(c(ORO).copy(alpha = 0.7f), Offset(0f, f - 3 * u), Size(w, 1 * u))
        var bx = w - 7 * u; k = 0
        while (bx > 0 && k < 80) { drawLine(c(ORO).copy(alpha = 0.7f), Offset(bx, f + 6 * u), Offset(bx, f + 10 * u), 1.2f * u); bx -= maxOf(28 * u, 1f); k++ }
        // El banco con el kilim: franjas de laca, oro e índigo con rombos.
        val banco = Rect(0f, h - 30 * u, w, h)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, banco.top - 4 * u), Size(w, 4 * u))
        drawRect(c(LACA_OSC), banco.topLeft, banco.size)
        drawRect(c(mezcla(LACA, Color.White, 0.1f)), banco.topLeft, Size(w, 3 * u))
        drawRect(c(ORO).copy(alpha = 0.8f), Offset(0f, banco.top + 6 * u), Size(w, 2 * u))
        val franja = Rect(0f, banco.top + 10 * u, w, banco.top + 21 * u)
        drawRect(c(SEDAS[0]), franja.topLeft, franja.size)
        var rx = 8 * u; k = 0
        while (rx < w && k < 60) {
            val rombo = Path().apply { moveTo(rx, franja.top + 1.5f * u); lineTo(rx + 5 * u, franja.center.y); lineTo(rx, franja.bottom - 1.5f * u); lineTo(rx - 5 * u, franja.center.y); close() }
            drawPath(rombo, c(if (k % 2 == 0) ORO else MARFIL).copy(alpha = 0.8f))
            drawCircle(c(LACA), 1.3f * u, Offset(rx, franja.center.y))
            rx += maxOf(16 * u, 1f); k++
        }
        drawRect(c(ORO).copy(alpha = 0.8f), Offset(0f, banco.top + 23 * u), Size(w, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, h - 3 * u), Size(w, 3 * u))
    }

    /** Las sedas de las tapas de las obras: índigo, verde, ciruela y verde mar. */
    private val SEDAS_OBRA = listOf(SEDAS[0], SEDAS[1], SEDAS[3], SEDAS[4])

    /** El cuello de nube (cuatro cabezas de ruyi alrededor de un cuadrado) centrado en [o], de semiancho [a] y semialto [b]. */
    private fun DrawScope.cuelloNube(o: Offset, a: Float, b: Float, color: Color) {
        drawRect(color, Offset(o.x - a * 0.7f, o.y - b * 0.7f), Size(a * 1.4f, b * 1.4f))
        val r = minOf(a, b) * 0.36f
        drawCircle(color, r, Offset(o.x, o.y - b + r)); drawCircle(color, r, Offset(o.x, o.y + b - r))
        drawCircle(color, r, Offset(o.x - a + r, o.y)); drawCircle(color, r, Offset(o.x + a - r, o.y))
        listOf(-1f, 1f).forEach { s ->
            drawCircle(color, r * 0.7f, Offset(o.x + s * r * 1.1f, o.y - b + r * 1.25f)); drawCircle(color, r * 0.7f, Offset(o.x + s * r * 1.1f, o.y + b - r * 1.25f))
        }
    }

    /**
     * La tapa de una obra: un tablero forrado de brocado (índigo, verde, ciruela o verde mar según la obra), labrado con
     * nubecillas de oro en tresbolillo y un filete dorado; en medio, el cuello de nube de oro con el papel donde va el
     * título; arriba, el tipo en oro, y abajo el autor y el año. En miniatura, el cuello de nube con la inicial.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = t(x, oscuro, false)
        val semilla = e.semilla and 0x7fffffff
        val seda = c(SEDAS_OBRA[semilla % SEDAS_OBRA.size])
        drawRect(seda)
        val oro = c(ORO)
        // El labrado: nubecillas en tresbolillo.
        val paso = maxOf(w * 0.22f, 4f)
        var y = paso * 0.4f; var fila = 0
        while (y < h && fila < 30) {
            var x = if (fila % 2 == 0) paso * 0.3f else paso * 0.8f; var j = 0
            while (x < w && j < 30) { nubecilla(Offset(x, y), paso * 0.5f, oro.copy(alpha = 0.16f)); x += paso; j++ }
            y += paso * 0.7f; fila++
        }
        drawRect(Color.White.copy(alpha = 0.06f), Offset.Zero, Size(w * 0.3f, h))
        val borde = Rect(w * 0.05f, w * 0.05f, w * 0.95f, h - w * 0.05f)
        drawRect(oro.copy(alpha = 0.85f), borde.topLeft, borde.size, style = Stroke(maxOf(1f, w * 0.015f)))
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.35f), maxOf(1f, w * 0.02f))
        val papel = if (oscuro) mezcla(PAPEL, Color(0xFF110D0C), 0.2f) else PAPEL
        if (e.mini) {
            val o = Offset(w / 2, h / 2)
            cuelloNube(o, w * 0.4f, w * 0.4f, oro)
            cuelloNube(o, w * 0.34f, w * 0.34f, papel)
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifBlank { "·" }, Rect(o.x - w * 0.3f, o.y - w * 0.3f, o.x + w * 0.3f, o.y + w * 0.3f), letra, TINTA, s * 0.4f, peso = FontWeight.Bold)
            return
        }
        val o = Offset(w / 2, h * 0.5f)
        val a = w * 0.44f; val b = h * 0.3f
        translate(w * 0.015f, w * 0.02f) { cuelloNube(o, a, b, Color.Black.copy(alpha = 0.25f)) }
        cuelloNube(o, a, b, oro)
        cuelloNube(o, a - w * 0.03f, b - w * 0.03f, papel)
        textoEn(medidor, e.tipo.uppercase(), Rect(w * 0.1f, h * 0.06f, w * 0.9f, h * 0.15f), letra, mezcla(oro, ORO, 0.5f), s * 0.075f,
            peso = FontWeight.Bold, espaciado = s * 0.008f, minimo = 4f)
        tituloMedievo(medidor, e.titulo.ifBlank { "—" }, Rect(o.x - a * 0.64f, o.y - b * 0.56f, o.x + a * 0.64f, o.y + b * 0.56f), letraTapa, TINTA, s * 0.12f,
            peso = FontWeight.Bold)
        val marfil = mezcla(c(MARFIL), MARFIL, 0.5f)
        textoEn(medidor, e.autor, Rect(w * 0.1f, h * 0.83f, w * 0.9f, h * 0.89f), letra, marfil, s * 0.075f, minimo = 4f)
        textoEn(medidor, e.anio, Rect(w * 0.1f, h * 0.89f, w * 0.9f, h * 0.94f), letra, mezcla(oro, ORO, 0.5f), s * 0.07f, minimo = 4f)
    }

    override val margenRecuerdo: Dp = 16.dp
    override val altoRotuloRecuerdo: Dp = 48.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El recuerdo: el salvoconducto del caravanero, una hoja de papel con doble filete de cinabrio y una nubecilla en cada
     * esquina. Arriba, «Hace N años» y la fecha con el clima; a la derecha, el sello cuadrado estampado en el color del
     * ánimo del día.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenRecuerdo.toPx(); val alto = altoRotuloRecuerdo.toPx()
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(1.5f * u, 1.5f * u), Size(w - 1.5f * u, h - 1.5f * u))
        drawRect(papelHondo(oscuro), Offset.Zero, Size(w - 1.5f * u, h - 1.5f * u))
        val rojo = if (oscuro) mezcla(CINABRIO, MARFIL, 0.15f) else CINABRIO
        val marco = Rect(5 * u, 5 * u, w - 6.5f * u, h - 6.5f * u)
        drawRect(rojo.copy(alpha = 0.75f), marco.topLeft, marco.size, style = Stroke(1.2f * u))
        drawRect(rojo.copy(alpha = 0.45f), marco.deflate(2.2f * u).topLeft, marco.deflate(2.2f * u).size, style = Stroke(0.6f * u))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { p ->
            drawCircle(papelHondo(oscuro), 4.5f * u, p)
            nubecilla(Offset(p.x, p.y + 1 * u), 9 * u, rojo.copy(alpha = 0.8f))
        }
        val tinta = tintaHonda(oscuro)
        izquierdaMedievo(medidor, haceMedievo(e.anios), m, 7 * u, 27 * u, w - 2 * m - 30 * u, letra, tinta, 16f, peso = FontWeight.Bold)
        val clima = climaMedievo(e.clima)
        izquierdaMedievo(medidor, if (clima.isEmpty()) e.fecha else "${e.fecha} · $clima", m, 27 * u, alto - 5 * u, w - 2 * m - 30 * u, letra,
            tinta.copy(alpha = 0.8f), 12f, minimo = 8f)
        drawLine(rojo.copy(alpha = 0.4f), Offset(m, alto - 3 * u), Offset(w - m, alto - 3 * u), 0.6f * u)
        e.animo?.let { a ->
            val l = 22 * u
            val r = Rect(w - m - l, 9 * u, w - m, 9 * u + l)
            rotate(-4f, r.center) {
                drawRect(a.copy(alpha = 0.9f), r.topLeft, r.size)
                drawRect(papelHondo(oscuro).copy(alpha = 0.85f), r.deflate(2 * u).topLeft, r.deflate(2 * u).size, style = Stroke(1 * u))
                // Unos trazos inventados, en reserva (no son escritura).
                drawLine(papelHondo(oscuro), Offset(r.left + 6 * u, r.top + 6 * u), Offset(r.left + 6 * u, r.bottom - 6 * u), 1.4f * u)
                drawLine(papelHondo(oscuro), Offset(r.left + 6 * u, r.top + 6 * u), Offset(r.right - 6 * u, r.top + 6 * u), 1.4f * u)
                drawLine(papelHondo(oscuro), Offset(r.right - 6 * u, r.top + 6 * u), Offset(r.right - 7 * u, r.center.y + 2 * u), 1.4f * u)
                drawLine(papelHondo(oscuro), Offset(r.center.x - 1 * u, r.center.y - 1 * u), Offset(r.center.x + 2 * u, r.bottom - 5 * u), 1.4f * u)
                for (j in 0 until 6) drawCircle(papelHondo(oscuro).copy(alpha = 0.5f), 0.7f * u, Offset(r.left + l * azar(e.semilla, j), r.top + l * azar(e.semilla, j + 9)))
            }
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
 * La tapa es ese libro de horas de frente: terciopelo sembrado de lises, bullones, broches y la cartela de latón con
 * vitela (la fecha como rúbrica roja, lo escrito en medio); al abrirse gira y deja ver la hoja con su orla y su miniatura
 * (de noche, una luna de plata y los haces de color del rosetón).
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 190.dp
    override val cajaTapa = Rect(0.28f, 0.35f, 0.82f, 0.68f)
    override val letraTapa: FontFamily = LetraExtra.textura
    override val tamTapa = 14f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La vitela de la cartela y de las hojas: se hunde menos que el terciopelo, para que el extracto se lea siempre. */
    private fun vitela(oscuro: Boolean, noche: Boolean): Color {
        var r = VITELA
        if (oscuro) r = mezcla(r, Color(0xFF0C0A08), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    /** Una flor de lis de alto [s] con centro en [o]. */
    private fun DrawScope.lis(o: Offset, s: Float, color: Color) {
        val p = Path().apply {
            moveTo(o.x, o.y - s * 0.5f)
            quadraticTo(o.x + s * 0.2f, o.y - s * 0.15f, o.x, o.y + s * 0.15f)
            quadraticTo(o.x - s * 0.2f, o.y - s * 0.15f, o.x, o.y - s * 0.5f); close()
            moveTo(o.x - s * 0.06f, o.y + s * 0.05f)
            quadraticTo(o.x - s * 0.5f, o.y - s * 0.3f, o.x - s * 0.36f, o.y + s * 0.2f)
            quadraticTo(o.x - s * 0.2f, o.y + s * 0.05f, o.x - s * 0.06f, o.y + s * 0.18f); close()
            moveTo(o.x + s * 0.06f, o.y + s * 0.05f)
            quadraticTo(o.x + s * 0.5f, o.y - s * 0.3f, o.x + s * 0.36f, o.y + s * 0.2f)
            quadraticTo(o.x + s * 0.2f, o.y + s * 0.05f, o.x + s * 0.06f, o.y + s * 0.18f); close()
        }
        drawPath(p, color)
        drawRect(color, Offset(o.x - s * 0.22f, o.y + s * 0.17f), Size(s * 0.44f, s * 0.08f))
        drawLine(color, Offset(o.x, o.y + s * 0.25f), Offset(o.x, o.y + s * 0.45f), s * 0.08f)
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val terciopelo = c(TERCIOPELOS[(e.semilla and 0x7fffffff) % TERCIOPELOS.size])
        val oro = mezcla(c(ORO), ORO, 0.35f)
        val hoja = vitela(oscuro, n)
        val libro = Rect(w * 0.025f, h * 0.04f, w * 0.965f, h * 0.885f)
        val lomo = Rect(libro.left, libro.top, libro.left + minOf(w * 0.07f, 28 * u), libro.bottom)
        val tapa = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(libro.left + 3 * u, libro.top + 5 * u), libro.size, CornerRadius(4 * u))
        // Los registros de seda que asoman abajo, más cuanto más escribiste.
        val registros = 1 + ((e.palabras / 800f).coerceIn(0f, 1f) * 2).toInt()
        for (j in 0 until registros) {
            val x = tapa.left + tapa.width * (0.22f + 0.07f * j)
            val fin = h - (2 + 3 * j) * u
            drawPath(Path().apply { moveTo(x, libro.bottom - 4 * u); lineTo(x + 4 * u, libro.bottom - 4 * u); lineTo(x + 4 * u, fin); lineTo(x + 2 * u, fin - 2.6f * u); lineTo(x, fin); close() },
                c(REGISTROS[j % REGISTROS.size]))
        }
        // El canto dorado de las hojas.
        val bloque = Rect(tapa.left, tapa.top + 3 * u, tapa.right + 2.5f * u, tapa.bottom + 3 * u)
        drawRect(mezcla(c(ORO), c(VITELA), 0.4f), bloque.topLeft, bloque.size)
        var cy = bloque.top + 2 * u
        while (cy < bloque.bottom - 1 * u) { drawLine(Color.Black.copy(alpha = 0.14f), Offset(tapa.right, cy), Offset(bloque.right, cy), 0.5f * u); cy += 1.5f * u }
        // La primera hoja del libro de horas: la orla de follaje, la miniatura con su cielo, la capital y los renglones en
        // negro y en rojo.
        if (a > 0.01f) {
            val pag = Rect(tapa.left + 1 * u, tapa.top + 3 * u, tapa.right - 1 * u, tapa.bottom - 1 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            listOf(10f to 0.14f, 5f to 0.12f).forEach { (an, al) -> drawRect(Color.Black.copy(alpha = al), pag.topLeft, Size(an * u, pag.height)) }
            val orla = Rect(pag.left + 14 * u, pag.top + 8 * u, pag.right - 10 * u, pag.bottom - 8 * u)
            drawRect(c(ORO).copy(alpha = 0.6f), orla.topLeft, orla.size, style = Stroke(0.8f * u))
            var k = 0
            var ox = orla.left
            while (ox < orla.right) {
                listOf(orla.top, orla.bottom).forEach { y -> drawCircle(c(VIDRIOS[k % VIDRIOS.size]).copy(alpha = 0.7f), 1.5f * u, Offset(ox, y + (if (k % 2 == 0) 2 else -2) * u)) }
                ox += 7 * u; k++
            }
            val texto = orla.deflate(8 * u)
            val mini = Rect(texto.left, texto.top, texto.left + texto.width * 0.32f, texto.top + texto.height * 0.62f)
            drawRect(c(VIDRIOS[0]), mini.topLeft, mini.size)
            drawRect(c(VIDRIOS[3]), Offset(mini.left, mini.bottom - mini.height * 0.3f), Size(mini.width, mini.height * 0.3f))
            drawPath(arcoApuntado(Rect(mini.left + mini.width * 0.3f, mini.top + mini.height * 0.2f, mini.right - mini.width * 0.3f, mini.bottom - mini.height * 0.15f), mini.height * 0.2f), c(VITELA))
            drawRect(oro, mini.topLeft, mini.size, style = Stroke(1.2f * u))
            var ry = texto.top + 3 * u; var j = 0
            while (ry < texto.bottom) {
                val x0 = if (ry < mini.bottom + 2 * u) mini.right + 6 * u else texto.left
                var x = x0; var q = 0
                val color = if (j % 4 == 0) c(VIDRIOS[1]).copy(alpha = 0.8f) else c(TINTA).copy(alpha = 0.6f)
                while (x < texto.right - 2 * u) {
                    val largo = minOf((4f + 9f * azar(e.semilla + j, q)) * u, texto.right - x)
                    drawLine(color, Offset(x, ry), Offset(x + largo, ry), 1.5f * u); x += largo + 2.4f * u; q++
                }
                ry += 6.2f * u; j++
            }
        }
        // La tapa de terciopelo sembrada de lises, que gira sobre el lomo al abrirse.
        val sx = 1f - 0.9f * a
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(terciopelo, Color.White, 0.14f), terciopelo, mezcla(terciopelo, Color.Black, 0.4f)), tapa.top, tapa.bottom),
                tapa.topLeft, tapa.size, CornerRadius(4 * u))
            drawOval(Color.White.copy(alpha = 0.05f), Offset(tapa.left + tapa.width * 0.1f, tapa.top + tapa.height * 0.08f), Size(tapa.width * 0.5f, tapa.height * 0.4f))
            val r1 = tapa.deflate(8 * u)
            var fy = r1.top + 10 * u; var fila = 0
            while (fy < r1.bottom - 6 * u) {
                var fx = r1.left + (if (fila % 2 == 0) 12f else 24f) * u
                while (fx < r1.right - 6 * u) { lis(Offset(fx, fy), 8 * u, oro.copy(alpha = 0.45f)); fx += 24 * u }
                fy += 16 * u; fila++
            }
            drawRoundRect(oro.copy(alpha = 0.8f), r1.topLeft, r1.size, CornerRadius(3 * u), style = Stroke(1.2f * u))
            val r1b = r1.deflate(3 * u)
            drawRoundRect(oro.copy(alpha = 0.45f), r1b.topLeft, r1b.size, CornerRadius(2 * u), style = Stroke(0.6f * u))
            fun bullon(o: Offset, r: Float) {
                drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(o.x + 1 * u, o.y + 1.5f * u))
                drawPath(estrella(o, r, 8, 0.72f), oro)
                drawCircle(mezcla(c(LATON), Color.Black, 0.2f), r * 0.55f, o)
                drawCircle(Color.White.copy(alpha = 0.4f), r * 0.24f, Offset(o.x - r * 0.2f, o.y - r * 0.2f))
            }
            listOf(r1.topLeft, r1.topRight, r1.bottomLeft, r1.bottomRight).forEach { bullon(it, 7 * u) }
            // La cartela de latón con la vitela: arriba la fecha en rojo, como una rúbrica; en medio, lo escrito.
            val cartela = Rect(w * 0.25f, h * 0.225f, w * 0.85f, h * 0.765f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(cartela.left + 1.5f * u, cartela.top + 2 * u), cartela.size)
            drawRect(c(LATON), cartela.topLeft, cartela.size)
            drawRect(Color.White.copy(alpha = 0.2f), cartela.topLeft, Size(cartela.width, 1.2f * u))
            listOf(cartela.topLeft, cartela.topRight, cartela.bottomLeft, cartela.bottomRight).forEach { p -> drawCircle(mezcla(c(LATON), Color.Black, 0.4f), 1.2f * u, Offset(p.x + (if (p.x < cartela.center.x) 2.4f else -2.4f) * u, p.y + (if (p.y < cartela.center.y) 2.4f else -2.4f) * u)) }
            val piel = cartela.deflate(5 * u)
            drawRect(hoja, piel.topLeft, piel.size)
            drawRect(Color.Black.copy(alpha = 0.35f), piel.topLeft, piel.size, style = Stroke(0.7f * u))
            textoEn(medidor, e.fecha, Rect(piel.left + 4 * u, piel.top + 1.5f * u, piel.right - 4 * u, h * 0.335f), letra, mezcla(c(VIDRIOS[1]), Color.Black, 0.1f), 13f, minimo = 7f)
            drawLine(c(VIDRIOS[1]).copy(alpha = 0.5f), Offset(piel.left + 12 * u, h * 0.342f), Offset(piel.right - 12 * u, h * 0.342f), 0.6f * u)
            if (!e.vacio && e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(piel.left + 4 * u, h * 0.685f, piel.right - 4 * u, piel.bottom - 1 * u), letra, c(TINTA).copy(alpha = 0.75f), 10f, minimo = 6f)
            }
            // El bullón de en medio, arriba; de noche, una luna de plata.
            val cb = Offset(cartela.center.x, (r1.top + cartela.top) / 2)
            val rc = minOf(7.5f * u, (cartela.top - r1.top) * 0.36f)
            if (n) {
                drawCircle(PLATA, rc, cb)
                drawCircle(mezcla(terciopelo, Color.Black, 0.1f), rc * 0.84f, Offset(cb.x + rc * 0.42f, cb.y - rc * 0.24f))
                listOf(-1f, 1f).forEach { s -> drawPath(estrella(Offset(cb.x + s * 20 * u, cb.y), 2.6f * u, 4, 0.4f), PLATA) }
            } else {
                bullon(cb, rc)
            }
            lis(Offset(cartela.center.x, (cartela.bottom + r1.bottom) / 2), minOf(12 * u, (r1.bottom - cartela.bottom) * 0.8f), oro)
            // Los dos broches de latón del canto.
            listOf(0.3f, 0.7f).forEach { f ->
                val y = tapa.top + tapa.height * f
                val broche = Rect(tapa.right - 16 * u, y - 5 * u, tapa.right + 1.5f * u, y + 5 * u)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(broche.left + 1.2f * u, broche.top + 1.5f * u), broche.size)
                drawRect(c(LATON), broche.topLeft, broche.size)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(broche.left, broche.bottom - 1.2f * u), Size(broche.width, 1.2f * u))
                drawPath(Path().apply { moveTo(broche.left, broche.top); lineTo(broche.left - 5 * u, y); lineTo(broche.left, broche.bottom); close() }, c(LATON))
                drawCircle(oro, 1.4f * u, Offset(broche.left + 4 * u, y))
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.4f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (k in 0 until 3) drawRect(Color.Black.copy(alpha = 0.12f * (3 - k) / 3f), Offset(borde, tapa.top + 3 * u), Size((4 + 4 * k) * u, tapa.height - 4 * u))
        }
        // El lomo de terciopelo, con sus nervios dorados.
        drawRoundRect(mezcla(terciopelo, Color.Black, 0.2f), lomo.topLeft, lomo.size, CornerRadius(3 * u))
        drawRect(Color.White.copy(alpha = 0.07f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.2f, lomo.height))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(lomo.right - 2 * u, lomo.top), Size(2 * u, lomo.height))
        listOf(0.12f, 0.31f, 0.5f, 0.69f, 0.88f).forEach { f ->
            val y = lomo.top + lomo.height * f
            drawRect(oro.copy(alpha = 0.7f), Offset(lomo.left + 1 * u, y - 1 * u), Size(lomo.width - 3 * u, 2 * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(lomo.left + 1 * u, y + 1 * u), Size(lomo.width - 3 * u, 0.8f * u))
        }
        listOf(0.215f, 0.405f, 0.595f, 0.785f).forEach { f -> lis(Offset(lomo.center.x - 1 * u, lomo.top + lomo.height * f), minOf(lomo.width * 0.5f, 10 * u), oro.copy(alpha = 0.7f)) }
        // De noche, la luna entra por el rosetón: haces de color sobre el libro y su luz fría.
        if (n) clipRect(libro.left, libro.top, libro.right + 3 * u, libro.bottom + 3 * u) {
            listOf(0.3f, 0.48f, 0.66f).forEachIndexed { k, f ->
                val x = w * f
                drawPath(Path().apply { moveTo(x, 0f); lineTo(x + w * 0.06f, 0f); lineTo(x - w * 0.12f, h); lineTo(x - w * 0.2f, h); close() }, VIDRIOS[(k * 2) % VIDRIOS.size].copy(alpha = 0.1f))
            }
        }
        if (n) {
            clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.8f, -h * 0.1f), h * 1.1f, LUNA.copy(alpha = 0.16f)) }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 122.dp

    /** La barra de la orla de un libro de horas, de [x0] a [x1] a la altura [y]: el filete de oro con tramos azules y rojos. */
    private fun DrawScope.orla(x0: Float, x1: Float, y: Float, grosor: Float, azul: Color, rojo: Color, oro: Color) {
        val u = density
        drawLine(oro, Offset(x0, y), Offset(x1, y), grosor + 1.4f * u)
        val tramo = 22f * u
        var x = x0 + 3f * u; var k = 0
        while (x < x1 - 3f * u) {
            drawLine(if (k % 2 == 0) azul else rojo, Offset(x, y), Offset(minOf(x + tramo - 3f * u, x1 - 3f * u), y), grosor)
            x += tramo; k++
        }
    }

    /** Un rosetón chico: el disco de vidrio, ocho lóbulos de color alrededor y el ojo de oro, con su tracería. */
    private fun DrawScope.rosetaPagina(o: Offset, r: Float, oro: Color, fondo: Color, a: Color, b: Color) {
        val u = density
        drawCircle(fondo, r, o)
        for (k in 0 until 8) {
            val g = Math.toRadians(k * 45.0)
            val pc = Offset(o.x + r * 0.6f * kotlin.math.cos(g).toFloat(), o.y + r * 0.6f * kotlin.math.sin(g).toFloat())
            drawCircle(if (k % 2 == 0) a else b, r * 0.25f, pc)
            drawCircle(oro, r * 0.25f, pc, style = Stroke(0.7f * u))
        }
        drawCircle(oro, r * 0.22f, o)
        drawCircle(oro, r, o, style = Stroke(1.2f * u))
    }

    /**
     * La cabecera del libro de horas: arriba y abajo, la barra de la orla en oro con sus tramos azules y rojos, rematada en
     * lises; a la izquierda, un nicho de arco apuntado en azul con el rosetón y dos lancetas (de noche, la luna de plata y
     * tres estrellas); a su derecha, «Liber horarum · Horae diei» en rúbrica roja, la fecha en letra textura (si es larga, en
     * dos renglones), el detalle y tres bezantes de oro.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val oro = c(ORO); val azul = p.detalle; val rojo = p.acento
        listOf(5f * u, h - 5f * u).forEach { y ->
            orla(14f * u, w - 14f * u, y, 2.2f * u, azul, rojo, oro)
            lis(Offset(7f * u, y), 9f * u, oro); lis(Offset(w - 7f * u, y), 9f * u, oro)
        }
        val nr = Rect(10f * u, 14f * u, 58f * u, h - 14f * u)
        drawPath(arcoApuntado(nr.translate(1.2f * u, 1.6f * u), 26f * u), Color.Black.copy(alpha = 0.18f))
        drawPath(arcoApuntado(nr, 26f * u), c(VIDRIOS[0]))
        val cx = nr.center.x
        if (!e.noche) {
            rosetaPagina(Offset(cx, nr.top + 40f * u), 15f * u, oro, c(TERCIOPELOS[1]), c(VIDRIOS[1]), c(VIDRIOS[2]))
            listOf(Rect(nr.left + 8f * u, nr.top + 62f * u, cx - 2f * u, nr.bottom - 6f * u), Rect(cx + 2f * u, nr.top + 62f * u, nr.right - 8f * u, nr.bottom - 6f * u))
                .forEach { lr -> drawPath(arcoApuntado(lr, 8f * u), oro, style = Stroke(1f * u)) }
        } else {
            val plata = mezcla(LUNA, p.acento, 0.15f)
            drawPath(mediaLunaMedievo(Offset(cx - 2f * u, nr.top + 42f * u), 11f * u), plata)
            listOf(Offset(cx + 10f * u, nr.top + 28f * u), Offset(cx - 9f * u, nr.top + 66f * u), Offset(cx + 7f * u, nr.top + 78f * u)).forEach {
                drawPath(estrella(it, 3f * u, 8, 0.45f), plata)
            }
        }
        drawPath(arcoApuntado(nr, 26f * u), oro, style = Stroke(1.4f * u))
        val x0 = nr.right + 10f * u; val x1 = w - 10f * u
        textoEn(medidor, e.rotulo, Rect(x0, 14f * u, x1, 36f * u), LetraExtra.textura, rotuloMedievo(p, e.noche), 16f, minimo = 8f)
        fechaMedievo(medidor, e.fecha, Rect(x0, 37f * u, x1, 82f * u), LetraExtra.textura, p.tinta, 22f)
        textoEn(medidor, e.detalle, Rect(x0, 83f * u, x1, 100f * u), letra, p.tenue, 11f, minimo = 7f)
        for (k in -1..1) drawCircle(oro, 1.7f * u, Offset((x0 + x1) / 2 + k * 9f * u, h - 14f * u))
    }

    /**
     * La orla del libro de horas en el margen: el filete azul y el rojo, los bezantes de oro cada tanto y, entre ellos,
     * hojitas de hiedra de oro que salen a uno y otro lado; arriba y abajo, la barra corre un trecho a lo ancho y remata en
     * una lis. En modo oscuro, la luna entra por un rosetón en el margen derecho: la vitela se hunde en la sombra, la luz es
     * fría y caen, muy tenues, unas manchas de color de sus vidrios.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val azul = p.detalle; val rojo = p.acento
        val oro = (if (noche) mezcla(ORO, NOCHE, 0.3f) else ORO)
        drawLine(azul.copy(alpha = 0.45f), Offset(16f * u, 0f), Offset(16f * u, h), 2f * u)
        drawLine(rojo.copy(alpha = 0.45f), Offset(20f * u, 0f), Offset(20f * u, h), 2f * u)
        var y = 30f * u; var k = 0
        while (y < h) {
            drawCircle(oro.copy(alpha = 0.7f), 2.2f * u, Offset(18f * u, y))
            val yh = y + 23f * u
            if (yh < h) {
                val lado = if (k % 2 == 0) -1f else 1f
                val o = Offset(18f * u + lado * 7f * u, yh)
                drawLine(oro.copy(alpha = 0.45f), Offset(18f * u, yh), o, 0.8f * u)
                listOf(Offset(0f, -2.2f), Offset(lado * 2.2f, 0f), Offset(0f, 2.2f)).forEach { d -> drawCircle(oro.copy(alpha = 0.45f), 1.5f * u, o + Offset(d.x * u, d.y * u)) }
            }
            y += 46f * u; k++
        }
        listOf(12f * u, h - 24f * u).forEach { yb ->
            orla(16f * u, w * 0.5f, yb, 1.4f * u, azul.copy(alpha = 0.45f), rojo.copy(alpha = 0.45f), oro.copy(alpha = 0.5f))
            lis(Offset(w * 0.5f + 6f * u, yb), 9f * u, oro.copy(alpha = 0.5f))
        }
        drawLine(rojo.copy(alpha = 0.15f), Offset(w - 16f * u, 0f), Offset(w - 16f * u, h), 0.8f * u)
        if (oscuro && !p.oscura) {
            val ventana = Offset(w - 15f * u, 260f * u)
            penumbraMedievo(ventana, Color(0xFFBFD0FF), 0.9f)
            for (j in 0..3) haloMedievo(Offset(w * 0.66f - j * 34f * u, 300f * u + j * 52f * u), 26f * u, VIDRIOS[j], 0.2f)
            drawCircle(t(PIEDRA, true, false), 15.5f * u, ventana)
            rosetaPagina(ventana, 13f * u, t(ORO, true, false), mezcla(VIDRIOS[0], Color.White, 0.15f), mezcla(VIDRIOS[1], Color.White, 0.2f), mezcla(VIDRIOS[2], Color.White, 0.2f))
            drawCircle(Color.White.copy(alpha = 0.18f), 13f * u, ventana)
        }
    }

    /** La barra: una franja de vitela algo más oscura, con la barra de la orla al pie, rematada en bezantes. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(p.color, p.tenue, 0.1f).let { if (oscuro) mezcla(it, Color.Black, 0.24f) else it }
        drawRect(base)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, h))
        val oro = t(ORO, oscuro && !p.oscura, noche)
        orla(10f * u, w - 10f * u, h - 4f * u, 2f * u, p.detalle.copy(alpha = 0.8f), p.acento.copy(alpha = 0.8f), oro)
        listOf(5f * u, w - 5f * u).forEach { drawCircle(oro, 2.4f * u, Offset(it, h - 4f * u)) }
    }

    /** El botón de sellar: una placa de azul con su marco de oro, puntos de oro en las esquinas y una lis en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        val azul = t(VIDRIOS[0], oscuro && !p.oscura, noche)
        val oro = t(ORO, oscuro && !p.oscura, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1f * u, 1.6f * u), r.size, CornerRadius(4f * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(azul, Color.White, 0.12f), mezcla(azul, Color.Black, 0.22f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(4f * u))
        drawRoundRect(oro, r.topLeft, r.size, CornerRadius(4f * u), style = Stroke(1.2f * u))
        val i = r.deflate(3f * u)
        drawRoundRect(oro.copy(alpha = 0.55f), i.topLeft, i.size, CornerRadius(2f * u), style = Stroke(0.6f * u))
        listOf(i.topLeft, Offset(i.right, i.top), Offset(i.right, i.bottom), Offset(i.left, i.bottom)).forEach { drawCircle(oro, 1f * u, it) }
        lis(Offset(r.left + 8f * u, r.center.y), 8f * u, oro); lis(Offset(r.right - 8f * u, r.center.y), 8f * u, oro)
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFF3E2B0)

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 268.dp

    /** La piel de ante del libro de cinturón, la cera del cirio, la llama y el coral del rosario. */
    private val ANTE = Color(0xFF8E6440)
    private val CIRIO = Color(0xFFEDE4CC)
    private val LLAMA_CIRIO = Color(0xFFF7C45A)
    private val CORAL = Color(0xFFA8382E)

    /** El grosor del libro de horas, en dp: de 4 con un folio a 17 con 240 o más. */
    private fun grosorHoras(hojas: Int): Float = 4f + 13f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /** Un arco apuntado en [r] cuya parte curva mide [k] veces su ancho. */
    private fun ojiva(r: Rect, k: Float): Path = arcoApuntado(r, r.width * k)

    /**
     * El libro de horas de momentos sobre el reclinatorio de roble, en la capilla: el muro de sillería con una ventana de dos
     * lancetas con su vidriera y su óculo, el reclinatorio con el atril en pendiente, el frente tallado con dos lancetas
     * ciegas y un cuadrifolio, los costados con su pináculo y, abajo, el escalón con el cojín de terciopelo carmesí y sus
     * borlas. Al lado, el cirio en su candelero de pie. El libro es un libro de cinturón: piel de ante gofrada en rombos con
     * cinco bullones, el broche y el canto dorado; la piel sigue más allá del pie en una cola que cae por delante del
     * reclinatorio y remata en un nudo; la cartela de latón con vitela lleva el nombre y la cuenta. Engorda con los folios
     * (más canto dorado y más registros de seda) y del borde del atril cuelga un rosario de coral. Al abrirse, la tapa gira y
     * deja ver la hoja con su orla y su miniatura. En oscuro, el cirio encendido alumbra la capilla.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0A0A0C), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorHoras(e.hojas) * u
        val hoja = vitela(oscuro, false)
        val oro = c(ORO)
        // El muro de sillería de la capilla.
        drawRect(c(PIEDRA))
        var y = 0f; var k = 0
        while (y < h) {
            drawLine(c(MURO), Offset(0f, y), Offset(w, y), 1 * u)
            var x = if (k % 2 == 0) 0f else 23 * u
            while (x < w) { drawLine(c(MURO), Offset(x, y), Offset(x, y + 20 * u), 1 * u); x += 46 * u }
            y += 20 * u; k++
        }
        // La ventana de dos lancetas con su óculo, a la izquierda: vidrios de color (de noche, hundidos y azules).
        val ven = Rect(w * 0.03f, h * 0.06f, w * 0.2f, h * 0.56f)
        drawPath(ojiva(ven.inflate(5 * u), 0.9f), c(MURO))
        drawPath(ojiva(ven, 0.9f), c(Color(0xFF3A3630)))
        val mitad = ven.width / 2
        listOf(ven.left + 2 * u, ven.left + mitad + 1 * u).forEachIndexed { j, x0 ->
            val lanc = Rect(x0, ven.top + ven.height * 0.36f, x0 + mitad - 3 * u, ven.bottom - 2 * u)
            val forma = ojiva(lanc, 0.9f)
            drawPath(forma, if (oscuro) Color(0xFF1A2440) else c(VIDRIOS[0]))
            clipPath(forma) {
                var vy = lanc.top; var q = j
                while (vy < lanc.bottom) {
                    drawRect((if (oscuro) mezcla(VIDRIOS[q % VIDRIOS.size], Color(0xFF0A0A14), 0.7f) else c(VIDRIOS[q % VIDRIOS.size])).copy(alpha = 0.85f),
                        Offset(lanc.left + (q % 2) * lanc.width / 2, vy), Size(lanc.width / 2, 9 * u))
                    vy += 9 * u; q += 2
                }
                var gy = lanc.top
                while (gy < lanc.bottom) { drawLine(Color.Black.copy(alpha = 0.6f), Offset(lanc.left, gy), Offset(lanc.right, gy), 0.8f * u); gy += 9 * u }
                drawLine(Color.Black.copy(alpha = 0.6f), Offset(lanc.center.x, lanc.top), Offset(lanc.center.x, lanc.bottom), 0.8f * u)
            }
            drawPath(forma, c(MURO), style = Stroke(1.6f * u))
        }
        val oculo = Offset(ven.center.x, ven.top + ven.height * 0.24f)
        rosetaPagina(oculo, ven.width * 0.22f, c(MURO), if (oscuro) Color(0xFF1A2440) else c(VIDRIOS[0]),
            if (oscuro) Color(0xFF3A2A40) else c(VIDRIOS[1]), if (oscuro) Color(0xFF2A3A30) else c(VIDRIOS[2]))
        // El piso de losas en damero.
        val piso = h * 0.95f
        drawRect(c(LOSA_CLARA), Offset(0f, h * 0.9f), Size(w, h * 0.1f))
        var lx = 0f; var lk = 0
        while (lx < w) { if (lk % 2 == 0) drawRect(c(LOSA_OSCURA), Offset(lx, h * 0.9f), Size(16 * u, h * 0.1f)); lx += 16 * u; lk++ }

        // ------------------------------------------------ el cirio en su candelero de pie, a la derecha
        val cx = w * 0.91f
        drawLine(c(LATON), Offset(cx, piso), Offset(cx, h * 0.34f), 3 * u)
        listOf(0.5f, 0.72f).forEach { f -> drawOval(c(LATON), Offset(cx - 5 * u, h * f - 2.5f * u), Size(10 * u, 5 * u)) }
        drawPath(Path().apply { moveTo(cx - 14 * u, piso); lineTo(cx, piso - 14 * u); lineTo(cx + 14 * u, piso); close() }, c(LATON))
        drawOval(c(LATON), Offset(cx - 10 * u, h * 0.34f - 3 * u), Size(20 * u, 5 * u))
        val cirio = Rect(cx - 4.5f * u, h * 0.1f, cx + 4.5f * u, h * 0.34f - 2 * u)
        drawRect(if (oscuro) mezcla(CIRIO, Color.Black, 0.15f) else CIRIO, cirio.topLeft, cirio.size)
        drawRect(Color.Black.copy(alpha = 0.14f), Offset(cirio.right - 2.5f * u, cirio.top), Size(2.5f * u, cirio.height))
        drawLine(TINTA, Offset(cx, cirio.top), Offset(cx, cirio.top - 2.5f * u), 0.9f * u)
        val llama = Offset(cx, cirio.top - 1.6f * u)
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to LLAMA_CIRIO.copy(alpha = 0.3f), 0.35f to LLAMA_CIRIO.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.95f))
            haloMedievo(llama - Offset(0f, 5 * u), 26 * u, LLAMA_CIRIO, 0.7f)
            llamaMedievo(llama, 12 * u, LLAMA_CIRIO)
        }

        // ------------------------------------------------ el reclinatorio
        val labio = h * 0.62f
        val frente = Rect(w * 0.22f, labio + 6 * u, w * 0.78f, h * 0.84f)
        // Los costados, con su pináculo de crochets.
        listOf(frente.left - 8 * u, frente.right).forEach { x ->
            drawRect(c(ROBLE), Offset(x, h * 0.42f), Size(8 * u, h * 0.86f - h * 0.42f))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(x + 5 * u, h * 0.42f), Size(3 * u, h * 0.86f - h * 0.42f))
            val pin = Path().apply { moveTo(x - 1 * u, h * 0.42f); lineTo(x + 4 * u, h * 0.3f); lineTo(x + 9 * u, h * 0.42f); close() }
            drawPath(pin, c(ROBLE_LUZ))
            listOf(0.33f, 0.37f, 0.41f).forEach { f -> drawCircle(c(ROBLE_LUZ), 1.6f * u, Offset(x + (if (f < 0.35f) 2.2f else 0f) * u, h * f)) }
            drawCircle(oro, 2 * u, Offset(x + 4 * u, h * 0.3f))
        }
        drawRect(c(ROBLE_OSC), frente.topLeft, frente.size)
        vetas(frente, c(VETA).copy(alpha = 0.4f), 13, densidad = 0.6f, vertical = true, grosor = 0.6f)
        // El frente tallado: dos lancetas ciegas y un cuadrifolio entre ellas.
        val anchoL = frente.width * 0.22f
        listOf(frente.left + frente.width * 0.12f, frente.right - frente.width * 0.12f - anchoL).forEach { x0 ->
            val lanc = Rect(x0, frente.top + 8 * u, x0 + anchoL, frente.bottom - 6 * u)
            drawPath(ojiva(lanc, 0.9f), c(ROBLE))
            drawPath(ojiva(lanc.deflate(3 * u), 0.9f), c(mezcla(ROBLE_OSC, Color.Black, 0.25f)))
        }
        val cq = frente.center
        for (q in 0 until 4) {
            val ang = Math.toRadians(q * 90.0)
            drawCircle(c(ROBLE), 6 * u, Offset(cq.x + 6 * u * kotlin.math.cos(ang).toFloat(), cq.y + 6 * u * kotlin.math.sin(ang).toFloat()))
        }
        drawCircle(oro, 2.6f * u, cq)
        // El escalón con el cojín de terciopelo y sus borlas.
        val escalon = Rect(w * 0.13f, h * 0.86f, w * 0.87f, piso)
        drawRect(c(ROBLE), escalon.topLeft, escalon.size)
        drawRect(c(ROBLE_LUZ), escalon.topLeft, Size(escalon.width, 2 * u))
        val cojin = Rect(w * 0.2f, h * 0.81f, w * 0.8f, h * 0.875f)
        val terciopelo = c(TERCIOPELOS[0])
        drawRoundRect(terciopelo, cojin.topLeft, cojin.size, CornerRadius(6 * u))
        drawRoundRect(Color.White.copy(alpha = 0.1f), Offset(cojin.left + 6 * u, cojin.top + 2 * u), Size(cojin.width - 12 * u, 3 * u), CornerRadius(1.5f * u))
        drawRoundRect(oro, cojin.topLeft, cojin.size, CornerRadius(6 * u), style = Stroke(1 * u))
        listOf(cojin.left + 2 * u, cojin.right - 2 * u).forEach { bx ->
            drawCircle(oro, 2.4f * u, Offset(bx, cojin.bottom - 2 * u))
            drawPath(Path().apply { moveTo(bx - 2.5f * u, cojin.bottom); lineTo(bx + 2.5f * u, cojin.bottom); lineTo(bx + 3.5f * u, cojin.bottom + 8 * u); lineTo(bx - 3.5f * u, cojin.bottom + 8 * u); close() }, oro)
        }

        // ------------------------------------------------ el libro de cinturón, de pie sobre el atril
        val tapa = Rect(w * 0.25f, h * 0.09f + g * 0.5f, w * 0.75f - g, labio)
        // La tabla del atril, inclinada, asoma por detrás.
        drawPath(Path().apply {
            moveTo(w * 0.2f, labio); lineTo(w * 0.24f, h * 0.18f); lineTo(w * 0.76f, h * 0.18f); lineTo(w * 0.8f, labio); close()
        }, c(ROBLE))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + g + 4 * u, tapa.top - g * 0.5f + 4 * u), tapa.size)
        drawRect(c(mezcla(ANTE, Color.Black, 0.3f)), Offset(tapa.left + g, tapa.top - g * 0.5f), tapa.size)
        // El canto dorado: el bloque de los folios visto de lado y desde arriba.
        val bloque = Path().apply {
            moveTo(tapa.left + 3 * u, tapa.top); lineTo(tapa.left + 3 * u + g, tapa.top - g * 0.5f + 1.5f * u)
            lineTo(tapa.right + g - 1.5f * u, tapa.top - g * 0.5f + 1.5f * u); lineTo(tapa.right + g - 1.5f * u, tapa.bottom - g * 0.5f - 1 * u)
            lineTo(tapa.right, tapa.bottom - 1 * u); lineTo(tapa.right, tapa.top); close()
        }
        drawPath(bloque, oro)
        val capas = (g / (1.8f * u)).toInt().coerceAtLeast(2)
        for (j in 1..capas) {
            val f = j / (capas + 1f)
            drawLine(Color.Black.copy(alpha = 0.14f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.bottom - g * f * 0.5f), 0.5f * u)
            drawLine(Color.Black.copy(alpha = 0.1f), Offset(tapa.left + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), 0.5f * u)
        }
        // La hoja con su orla y su miniatura, al abrir.
        if (a > 0.01f) {
            val pag = tapa.deflate(2 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            drawRect(Color.Black.copy(alpha = 0.14f), pag.topLeft, Size(8 * u, pag.height))
            val m = Rect(pag.left + 14 * u, pag.top + 12 * u, pag.right - 12 * u, pag.bottom - 12 * u)
            orla(m.left, m.right, m.top, 1.6f * u, c(VIDRIOS[0]), c(VIDRIOS[1]), oro)
            orla(m.left, m.right, m.bottom, 1.6f * u, c(VIDRIOS[0]), c(VIDRIOS[1]), oro)
            val mini = Rect(m.left + 4 * u, m.top + 6 * u, m.left + m.width * 0.45f, m.top + m.height * 0.45f)
            drawRect(c(VIDRIOS[0]), mini.topLeft, mini.size)
            drawPath(ojiva(mini.deflate(4 * u), 0.9f), c(ORO).copy(alpha = 0.8f))
            drawRect(oro, mini.topLeft, mini.size, style = Stroke(1.2f * u))
            var ry = m.top + 8 * u; var j = 0
            while (ry < m.bottom - 6 * u) {
                val x0 = if (ry < mini.bottom + 2 * u) mini.right + 5 * u else m.left + 4 * u
                var x = x0; var q = 0
                while (x < m.right - 4 * u) {
                    val l = minOf((3f + 9f * azar(e.semilla + j, q)) * u, m.right - 4 * u - x)
                    drawLine(c(TINTA).copy(alpha = 0.6f), Offset(x, ry), Offset(x + l, ry), 1.2f * u)
                    x += l + 2 * u; q++
                }
                ry += 5.5f * u; j++
            }
        }
        // La tapa de ante gofrado: gira sobre el lomo.
        val sx = 1f - 0.94f * a
        val ante = c(ANTE)
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRect(ante, tapa.topLeft, tapa.size)
            clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) {
                var d = tapa.left - tapa.height
                while (d < tapa.right) {
                    drawLine(Color.Black.copy(alpha = 0.16f), Offset(d, tapa.bottom), Offset(d + tapa.height, tapa.top), 0.9f * u)
                    drawLine(Color.Black.copy(alpha = 0.16f), Offset(d, tapa.top), Offset(d + tapa.height, tapa.bottom), 0.9f * u)
                    d += 18 * u
                }
            }
            bisel(tapa, Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.3f), 2 * u)
            drawRect(Color.Black.copy(alpha = 0.2f), tapa.deflate(6 * u).topLeft, tapa.deflate(6 * u).size, style = Stroke(1.2f * u))
            // Los cinco bullones: uno en cada esquina y uno abajo en medio.
            fun bullon(o: Offset, r: Float) {
                drawCircle(Color.Black.copy(alpha = 0.3f), r, o + Offset(1 * u, 1.4f * u))
                drawCircle(c(LATON), r, o)
                drawCircle(Color.White.copy(alpha = 0.35f), r * 0.35f, o - Offset(r * 0.3f, r * 0.3f))
            }
            val m = 13 * u
            listOf(Offset(tapa.left + m, tapa.top + m), Offset(tapa.right - m, tapa.top + m), Offset(tapa.left + m, tapa.bottom - m), Offset(tapa.right - m, tapa.bottom - m),
                Offset(tapa.center.x, tapa.bottom - tapa.height * 0.2f)).forEach { bullon(it, 4.5f * u) }
            // La cartela de latón con la vitela: el nombre en rúbrica y la cuenta.
            val et = Rect(tapa.left + tapa.width * 0.12f, tapa.top + tapa.height * 0.18f, tapa.right - tapa.width * 0.12f, tapa.top + tapa.height * 0.58f)
            drawRect(Color.Black.copy(alpha = 0.3f), et.topLeft + Offset(1.4f * u, 1.8f * u), et.size)
            drawRect(c(LATON), et.topLeft, et.size)
            val vit = et.deflate(3 * u)
            drawRect(hoja, vit.topLeft, vit.size)
            orla(vit.left + 3 * u, vit.right - 3 * u, vit.top + 3 * u, 1 * u, c(VIDRIOS[0]), c(VIDRIOS[1]), oro)
            val corte = vit.top + vit.height * 0.6f
            textoEn(medidor, e.nombre, Rect(vit.left + 5 * u, vit.top + 6 * u, vit.right - 5 * u, corte), letraTapa, c(VIDRIOS[1]), 16f, minimo = 8f)
            textoEn(medidor, e.cuenta, Rect(vit.left + 5 * u, corte, vit.right - 5 * u, vit.bottom - 3 * u), letra, c(TINTA).copy(alpha = 0.85f), 10f, minimo = 6f)
            // El broche del canto.
            val yb = tapa.center.y + tapa.height * 0.12f
            val chapa = Rect(tapa.right - 16 * u, yb - 6 * u, tapa.right + 1 * u, yb + 6 * u)
            drawRect(c(LATON), chapa.topLeft, chapa.size)
            drawRect(mezcla(c(LATON), Color.Black, 0.35f), chapa.deflate(2 * u).topLeft, chapa.deflate(2 * u).size, style = Stroke(0.7f * u))
            drawCircle(c(LATON), 2 * u, Offset(chapa.left - 2 * u, yb))
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (j in 0 until 3) drawRect(Color.Black.copy(alpha = 0.1f * (3 - j) / 3f), Offset(borde, tapa.top), Size((3 + 4 * j) * u, tapa.height))
        }
        drawRoundRect(mezcla(ante, Color.Black, 0.25f), Offset(tapa.left - 6 * u, tapa.top - 1 * u), Size(7 * u, tapa.height + 1 * u), CornerRadius(3 * u))
        // El labio del atril, por delante del pie del libro.
        drawRect(c(ROBLE_LUZ), Offset(w * 0.19f, labio), Size(w * 0.62f, 7 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.19f, labio + 5 * u), Size(w * 0.62f, 2 * u))
        // Los registros de seda: uno más cada 50 folios.
        val registros = (1 + e.hojas / 50).coerceAtMost(5)
        for (j in 0 until registros) {
            val rx = tapa.left + tapa.width * (0.12f + 0.08f * j)
            val largo = (18 + 8 * azar(e.semilla, 30 + j)) * u
            val col = c(listOf(REGISTROS[0], REGISTROS[1], REGISTROS[2], VIDRIOS[0], VIDRIOS[4])[j])
            drawPath(Path().apply {
                moveTo(rx, labio); lineTo(rx + 4 * u, labio); lineTo(rx + 4 * u, labio + largo); lineTo(rx + 2 * u, labio + largo - 3 * u); lineTo(rx, labio + largo); close()
            }, col)
        }
        // La cola de la piel que cae por delante del reclinatorio y remata en el nudo.
        val raiz = tapa.left + tapa.width * 0.6f
        val nudo = Offset(raiz + 6 * u, h * 0.79f)
        drawPath(Path().apply {
            moveTo(raiz - 14 * u, labio - 2 * u); lineTo(raiz + 14 * u, labio - 2 * u)
            cubicTo(raiz + 12 * u, labio + 20 * u, nudo.x + 6 * u, nudo.y - 18 * u, nudo.x + 3 * u, nudo.y)
            lineTo(nudo.x - 3 * u, nudo.y)
            cubicTo(nudo.x - 6 * u, nudo.y - 20 * u, raiz - 12 * u, labio + 20 * u, raiz - 14 * u, labio - 2 * u); close()
        }, mezcla(ante, Color.Black, 0.1f))
        drawCircle(mezcla(ante, Color.Black, 0.2f), 6.5f * u, nudo)
        listOf(-1f, 1f).forEach { s -> drawArc(Color.Black.copy(alpha = 0.3f), if (s < 0) 100f else -80f, 160f, false, Offset(nudo.x - 6.5f * u, nudo.y - 6.5f * u), Size(13 * u, 13 * u), style = Stroke(1 * u)) }
        drawLine(mezcla(ante, Color.Black, 0.2f), nudo, nudo + Offset(-4 * u, 12 * u), 2.4f * u, cap = StrokeCap.Round)
        drawLine(mezcla(ante, Color.Black, 0.2f), nudo, nudo + Offset(5 * u, 11 * u), 2.4f * u, cap = StrokeCap.Round)
        // El rosario de coral colgado del labio, con su cruz.
        val r0 = Offset(w * 0.74f, labio + 4 * u); val r1 = Offset(w * 0.8f, labio + 2 * u)
        for (j in 0..14) {
            val f = j / 14f
            val o = Offset(r0.x + (r1.x - r0.x) * f, r0.y + (r1.y - r0.y) * f + 26 * u * kotlin.math.sin(f * Math.PI).toFloat())
            drawCircle(c(if (j % 5 == 0) ORO else CORAL), (if (j % 5 == 0) 2f else 1.6f) * u, o)
        }
        val cruz = Offset((r0.x + r1.x) / 2, labio + 34 * u)
        drawLine(c(CORAL), Offset(cruz.x, labio + 30 * u), cruz + Offset(0f, 10 * u), 1 * u)
        drawLine(oro, cruz, cruz + Offset(0f, 12 * u), 1.8f * u)
        drawLine(oro, cruz + Offset(-4 * u, 4 * u), cruz + Offset(4 * u, 4 * u), 1.8f * u)
    }

    /**
     * La pestaña de un día: una lanceta de vitela con su arco apuntado y su filete de oro, la letra de la semana en rojo en
     * el vértice, el día en letra de textura y, al pie, un medallón del color del ánimo con su cerco de oro (sin ánimo, una
     * flor de lis pequeña).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val forma = ojiva(Rect(4 * u, 2 * u, w - 4 * u, h - 2 * u), 0.75f)
        drawPath(ojiva(Rect(5.2f * u, 3.6f * u, w - 2.8f * u, h - 0.4f * u), 0.75f), Color.Black.copy(alpha = 0.25f))
        drawPath(forma, vitela(oscuro, false))
        drawPath(forma, c(ORO), style = Stroke(1.4f * u))
        drawPath(ojiva(Rect(7 * u, 5 * u, w - 7 * u, h - 5 * u), 0.75f), c(VIDRIOS[0]).copy(alpha = 0.5f), style = Stroke(0.6f * u))
        textoEn(medidor, e.semana, Rect(w / 2 - 6 * u, 9 * u, w / 2 + 6 * u, 19 * u), letra, c(VIDRIOS[1]), 9f, peso = FontWeight.Bold)
        textoEn(medidor, e.dia, Rect(5 * u, 18 * u, w - 5 * u, h - 17 * u), letraTapa, c(TINTA), 18f, minimo = 10f)
        val o = Offset(w / 2, h - 11 * u)
        if (e.tono != null) {
            drawCircle(e.tono, 5.5f * u, o)
            drawCircle(c(ORO), 5.5f * u, o, style = Stroke(1.2f * u))
        } else {
            lis(o, 8 * u, c(ORO))
        }
    }

    /**
     * El rótulo de un mes: una viga de roble con su crestería de arquillos dorados arriba y abajo y, en medio, la cartela de
     * vitela de puntas apuntadas con el mes en letra de textura y un cuadrifolio rojo y azul a cada lado.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(ROBLE), Offset(0f, 2 * u), Size(w, h - 4 * u))
        drawRect(c(ROBLE_LUZ), Offset(0f, 2 * u), Size(w, 1.6f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, h - 4 * u), Size(w, 2 * u))
        var x = 0f
        while (x < w) {
            drawArc(c(ORO).copy(alpha = 0.8f), 0f, 180f, false, Offset(x, 1 * u), Size(10 * u, 6 * u), style = Stroke(0.8f * u))
            drawArc(c(ORO).copy(alpha = 0.8f), 180f, 180f, false, Offset(x, h - 7 * u), Size(10 * u, 6 * u), style = Stroke(0.8f * u))
            x += 10 * u
        }
        val cart = Rect(w * 0.1f, 7 * u, w * 0.9f, h - 7 * u)
        val forma = Path().apply {
            moveTo(cart.left, cart.center.y); lineTo(cart.left + 10 * u, cart.top); lineTo(cart.right - 10 * u, cart.top)
            lineTo(cart.right, cart.center.y); lineTo(cart.right - 10 * u, cart.bottom); lineTo(cart.left + 10 * u, cart.bottom); close()
        }
        drawPath(forma, vitela(oscuro, false))
        drawPath(forma, c(ORO), style = Stroke(1.2f * u))
        listOf(cart.left + 18 * u, cart.right - 18 * u).forEachIndexed { j, cx ->
            for (q in 0 until 4) {
                val ang = Math.toRadians(q * 90.0)
                drawCircle(c(if (j == 0) VIDRIOS[1] else VIDRIOS[0]), 2.6f * u, Offset(cx + 2.8f * u * kotlin.math.cos(ang).toFloat(), h / 2 + 2.8f * u * kotlin.math.sin(ang).toFloat()))
            }
            drawCircle(c(ORO), 1.4f * u, Offset(cx, h / 2))
        }
        textoEn(medidor, texto, Rect(cart.left + 28 * u, cart.top, cart.right - 28 * u, cart.bottom), letraTapa, c(TINTA), 15f, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El azul de bóveda del sobre, el rubí de su forro y del lacre, y el oro de la cinta. */
    private val BOVEDA = Color(0xFF1F2F7A)
    private val RUBI = Color(0xFFB3122E)
    private val ORO_CINTA = Color(0xFFE2B34A)

    /** La mandorla (el óvalo con punta arriba y abajo) de alto [alto] y ancho [ancho], centrada en [o]. */
    private fun mandorla(o: Offset, ancho: Float, alto: Float): Path = Path().apply {
        moveTo(o.x, o.y - alto / 2)
        quadraticTo(o.x + ancho * 1.0f, o.y, o.x, o.y + alto / 2)
        quadraticTo(o.x - ancho * 1.0f, o.y, o.x, o.y - alto / 2)
        close()
    }

    /** El sello de capítulo en forma de mandorla, de cera roja, en [o] (de radio [r]): su filo, la crucecita arriba y la lis. */
    private fun DrawScope.selloCapitulo(o: Offset, r: Float, oscuro: Boolean) {
        val cera = if (oscuro) mezcla(RUBI, Color.Black, 0.25f) else RUBI
        val hondo = mezcla(cera, Color.Black, 0.4f); val claro = mezcla(cera, Color.White, 0.3f)
        translate(r * 0.08f, r * 0.12f) { drawPath(mandorla(o, r * 1.25f, r * 2.6f), Color.Black.copy(alpha = 0.32f)) }
        drawPath(mandorla(o, r * 1.25f, r * 2.6f), cera)
        drawPath(mandorla(o, r * 1.0f, r * 2.18f), hondo, style = Stroke(r * 0.08f))
        drawPath(mandorla(o, r * 0.9f, r * 2.0f), claro.copy(alpha = 0.5f), style = Stroke(r * 0.04f))
        drawLine(claro, Offset(o.x, o.y - r * 0.82f), Offset(o.x, o.y - r * 0.5f), r * 0.08f)
        drawLine(claro, Offset(o.x - r * 0.13f, o.y - r * 0.7f), Offset(o.x + r * 0.13f, o.y - r * 0.7f), r * 0.08f)
        lis(Offset(o.x + r * 0.03f, o.y + r * 0.2f), r * 0.95f, hondo)
        lis(Offset(o.x, o.y + r * 0.16f), r * 0.95f, claro)
    }

    /** Una estrellita de oro de la bóveda: cuatro rayos y su punto. */
    private fun DrawScope.estrellita(o: Offset, s: Float, color: Color) {
        drawLine(color, Offset(o.x - s, o.y), Offset(o.x + s, o.y), s * 0.32f)
        drawLine(color, Offset(o.x, o.y - s), Offset(o.x, o.y + s), s * 0.32f)
        drawCircle(color, s * 0.38f, o)
    }

    /** Una filacteria de vitela en [r], con las puntas en cola de golondrina vueltas hacia atrás. */
    private fun DrawScope.filacteria(r: Rect, papel: Color, reves: Color) {
        val u = density; val cola = minOf(r.height * 0.55f, 9 * u)
        listOf(-1f, 1f).forEach { s ->
            val x = if (s < 0) r.left else r.right
            drawPath(Path().apply {
                moveTo(x, r.top + r.height * 0.22f); lineTo(x + s * cola * 1.4f, r.top + r.height * 0.22f)
                lineTo(x + s * cola * 0.9f, r.top + r.height * 0.61f); lineTo(x + s * cola * 1.4f, r.bottom + r.height * 0.0f); lineTo(x, r.bottom); close()
            }, reves)
        }
        drawRect(papel, r.topLeft, r.size)
        drawRect(Color.Black.copy(alpha = 0.18f), r.topLeft, r.size, style = Stroke(0.8f * u))
    }

    /**
     * La carta va en un sobre de azul de bóveda sembrado de estrellas de oro, con un filete de oro y otro de rubí, y la
     * solapa en ojiva (el arco apuntado, vuelto hacia abajo) forrada de rubí. La cierra el sello de capítulo: una mandorla
     * de cera roja con la crucecita y la lis; las selladas al futuro llevan además la cinta de oro que baja hasta el sello y
     * cae en dos colas. A quién va y las fechas, en una filacteria de vitela, con tinta. Al abrirse la mandorla se parte, la
     * ojiva se levanta enseñando su forro y sale la carta. En oscuro el azul se hunde casi a negro y las estrellas apenas
     * brillan; la filacteria se lee.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF07091A), 0.55f) else x
        val p = Rect(c.left + W * 0.05f, c.top + H * 0.08f, c.right - W * 0.05f, c.bottom - H * 0.07f)
        val cx = p.center.x
        val largo = p.height * 0.42f
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val punta = Offset(cx, p.top + largo)
        val rs = minOf(W, H) * 0.1f
        val partido = e.romper.coerceIn(0f, 1f)
        fun ojivaSolapa(g: Float) = Path().apply {
            moveTo(p.left, p.top)
            cubicTo(p.left + p.width * 0.04f, p.top + g * largo * 0.5f, cx - p.width * 0.2f, p.top + g * largo * 0.86f, cx, p.top + g * largo)
            cubicTo(cx + p.width * 0.2f, p.top + g * largo * 0.86f, p.right - p.width * 0.04f, p.top + g * largo * 0.5f, p.right, p.top)
            close()
        }
        fun DrawScope.selloEn(g: Float, mitad: Int) {
            val o = Offset(cx, p.top + g * largo)
            conAlfaMedievo(e.alfaSello.coerceIn(0f, 1f), Rect(punta, rs * 4f * e.escalaSello)) {
                scale(e.escalaSello, punta) {
                    if (mitad == 0 && e.brillo > 0f) {
                        drawCircle(Brush.radialGradient(0f to ORO_CINTA.copy(alpha = 0.8f * e.brillo), 0.45f to ORO_CINTA.copy(alpha = 0.25f * e.brillo),
                            1f to Color.Transparent, center = o, radius = rs * 3f), rs * 3f, o)
                        rayosMedievo(o, rs * 1.5f, rs * 2.6f, ORO_CINTA.copy(alpha = 0.9f * e.brillo), 12, 1.1f)
                    }
                    when {
                        partido <= 0f -> selloCapitulo(o, rs, oscuro)
                        mitad == 1 -> clipPath(mitadSelloMedievo(o, rs * 1.3f, true, e.semilla)) {
                            translate(0f, -partido * rs * 0.2f) { rotate(-7f * partido, o) { selloCapitulo(o, rs, oscuro) } }
                        }
                        else -> clipPath(mitadSelloMedievo(o, rs * 1.3f, false, e.semilla)) {
                            translate(0f, partido * rs * 0.12f) { rotate(5f * partido, o) { selloCapitulo(o, rs, oscuro) } }
                        }
                    }
                }
            }
        }

        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.55f else 0.25f), Offset(p.left + 2 * u, p.top + 3 * u), p.size, CornerRadius(2 * u))
        if (giro < 0f) {
            drawPath(ojivaSolapa(giro), k(RUBI))
            drawPath(ojivaSolapa(giro), k(ORO_CINTA).copy(alpha = 0.7f), style = Stroke(1 * u))
            if (partido > 0f) selloEn(giro, 1)
        }
        val sale = salidaMedievo(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaMedievo(Rect(cx - p.width * 0.42f, p.top + 3 * u - sube, cx + p.width * 0.42f, p.bottom - 6 * u - sube), e, oscuro)
        }
        // El sobre: azul de bóveda con sus estrellas, el filete de oro y el de rubí.
        drawRect(k(BOVEDA), p.topLeft, p.size)
        clipRect(p.left, p.top, p.right, p.bottom) {
            for (j in 0 until 30) estrellita(Offset(p.left + p.width * azar(e.semilla, 300 + j), p.top + p.height * azar(e.semilla, 340 + j)),
                (1.4f + 1.6f * azar(e.semilla, 380 + j)) * u, k(ORO_CINTA).copy(alpha = if (oscuro) 0.45f else 0.75f))
        }
        drawRect(k(ORO_CINTA), p.topLeft + Offset(3 * u, 3 * u), Size(p.width - 6 * u, p.height - 6 * u), style = Stroke(1 * u))
        drawRect(k(RUBI), p.topLeft + Offset(5 * u, 5 * u), Size(p.width - 10 * u, p.height - 10 * u), style = Stroke(0.8f * u))
        // La filacteria con lo escrito.
        val fil = Rect(p.left + p.width * 0.1f, punta.y + rs * 1.5f, p.right - p.width * 0.1f, p.bottom - 7 * u)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            filacteria(fil, if (oscuro) mezcla(VITELA, Color(0xFF07091A), 0.22f) else VITELA, if (oscuro) mezcla(Color(0xFFC9B98E), Color(0xFF07091A), 0.4f) else Color(0xFFC9B98E))
            val m = fil.top + fil.height * 0.56f
            textoEn(medidor, e.linea1, Rect(fil.left + 2 * u, fil.top + 1.5f * u, fil.right - 2 * u, m), letra, TINTA, 12f, peso = FontWeight.SemiBold, minimo = 7.5f)
            textoEn(medidor, e.linea2, Rect(fil.left + 4 * u, m - 1 * u, fil.right - 4 * u, fil.bottom - 1.5f * u), letra, mezcla(RUBI, Color.Black, 0.2f), 9f, minimo = 7f)
        }
        if (giro >= 0f) {
            translate(0f, 2 * u) { drawPath(ojivaSolapa(giro), Color.Black.copy(alpha = 0.35f)) }
            drawPath(ojivaSolapa(giro), k(mezcla(BOVEDA, Color.White, 0.08f)))
            clipPath(ojivaSolapa(giro)) {
                for (j in 0 until 10) estrellita(Offset(p.left + p.width * azar(e.semilla, 400 + j), p.top + largo * giro * azar(e.semilla, 420 + j)),
                    (1.4f + 1.4f * azar(e.semilla, 440 + j)) * u, k(ORO_CINTA).copy(alpha = if (oscuro) 0.45f else 0.8f))
            }
            drawPath(ojivaSolapa(giro), k(ORO_CINTA), style = Stroke(1.2f * u))
        }
        // La cinta de oro: baja por el medio de la solapa hasta el sello y cae en dos colas.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val ancho = maxOf(3 * u, W * 0.03f)
            val baja = (punta.y - p.top + 2 * u) * minOf(1f, ata / 0.6f)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(cx - ancho / 2 + 1 * u, p.top - 2 * u), Size(ancho, baja))
            drawRect(k(ORO_CINTA), Offset(cx - ancho / 2, p.top - 2 * u), Size(ancho, baja))
            drawLine(Color.White.copy(alpha = 0.35f), Offset(cx - ancho * 0.2f, p.top - 2 * u), Offset(cx - ancho * 0.2f, p.top - 2 * u + baja), 0.7f * u)
            if (ata > 0.6f) {
                val f = (ata - 0.6f) / 0.4f
                listOf(-1f, 1f).forEach { s ->
                    val fin = punta + Offset(s * rs * 2.2f * f, rs * 1.15f * f)
                    drawLine(k(ORO_CINTA), punta, fin, ancho, StrokeCap.Butt)
                    drawPath(Path().apply { moveTo(fin.x - ancho * 0.5f, fin.y); lineTo(fin.x + s * ancho * 0.4f, fin.y + ancho * 0.9f); lineTo(fin.x + ancho * 0.5f, fin.y); close() }, k(ORO_CINTA))
                }
            }
        }
        if (partido <= 0f) selloEn(maxOf(giro, 0f), 0)
        else {
            selloEn(1f, 0)
            if (giro >= 0f) selloEn(giro, 1)
        }
    }

    override val cajaPrevia = Rect(0.165f, 0.08f, 0.835f, 0.25f)

    override val altoBuzon: Dp = 212.dp

    /**
     * El buzón es el relicario de las cartas, sobre el altar de piedra: una arqueta de cobre dorado como una iglesia
     * pequeña, con el tejado de esmalte azul con sus crochets y pináculos, el gablete del medio con su rosetón y, en el
     * frente, una arquería de ocho nichos con columnillas donde se guardan las cartas de pie: las que ya se pueden abrir
     * con su mandorla encendida, las selladas con la cinta de oro, las abiertas con la ojiva levantada y la hoja fuera. En el
     * zócalo, tres placas de esmalte azul con la cuenta en oro. A los lados, dos cirios, que en oscuro se encienden.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF07091A), 0.58f) else x
        val esmalte = k(Color(0xFF203C8E)); val dorado = k(ORO); val doradoLuz = k(Color(0xFFE6C877))
        // El muro de la capilla y el altar.
        drawRect(k(MURO))
        var y = 0f; var f = 0
        while (y < h) {
            drawLine(k(Color(0xFF55524B)), Offset(0f, y), Offset(w, y), 1 * u)
            var x = if (f % 2 == 0) 0f else 26 * u
            while (x < w) { drawLine(k(Color(0xFF55524B)), Offset(x, y), Offset(x, y + 20 * u), 1 * u); x += 52 * u }
            y += 20 * u; f++
        }
        val altar = h * 0.88f
        drawRect(k(PIEDRA), Offset(0f, altar), Size(w, h - altar))
        drawRect(k(LOSA_CLARA), Offset(0f, altar), Size(w, 4 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, altar + 4 * u), Size(w, 2 * u))
        // Los cirios.
        val cirios = listOf(Offset(w * 0.045f, altar), Offset(w * 0.955f, altar))
        if (oscuro) drawRect(Brush.radialGradient(0f to Color(0xFFF7C45A).copy(alpha = 0.3f), 0.5f to Color(0xFFF7C45A).copy(alpha = 0.1f), 1f to Color.Transparent,
            center = Offset(w / 2, altar - h * 0.5f), radius = w * 0.7f))
        cirios.forEach { b ->
            drawRect(k(LATON), Offset(b.x - 6 * u, b.y - 6 * u), Size(12 * u, 6 * u))
            drawRect(if (oscuro) mezcla(Color(0xFFEDE4CC), Color.Black, 0.15f) else Color(0xFFEDE4CC), Offset(b.x - 3.5f * u, b.y - h * 0.5f), Size(7 * u, h * 0.5f - 6 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(b.x + 1.5f * u, b.y - h * 0.5f), Size(2 * u, h * 0.5f - 6 * u))
            val llama = Offset(b.x, b.y - h * 0.5f - 1 * u)
            drawLine(TINTA, llama, llama - Offset(0f, 2 * u), 0.9f * u)
            if (oscuro) { haloMedievo(llama - Offset(0f, 5 * u), 18 * u, Color(0xFFF7C45A), 0.7f); llamaMedievo(llama, 10 * u, Color(0xFFF7C45A)) }
        }

        // ------------------------------------------------ el relicario
        val R = Rect(w * 0.1f, h * 0.05f, w * 0.9f, altar)
        val alero = R.top + R.height * 0.26f
        val zocalo = R.bottom - R.height * 0.25f
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(R.left + 4 * u, alero + 4 * u), Size(R.width, R.bottom - alero - 2 * u))
        // El tejado de esmalte azul, con sus escamas de oro, los crochets en el caballete y los pináculos.
        val tejado = Path().apply { moveTo(R.left - 3 * u, alero); lineTo(R.left + R.width * 0.06f, R.top + R.height * 0.08f); lineTo(R.right - R.width * 0.06f, R.top + R.height * 0.08f); lineTo(R.right + 3 * u, alero); close() }
        drawPath(tejado, esmalte)
        clipPath(tejado) {
            var ty = R.top + R.height * 0.08f; var fila = 0
            while (ty < alero) {
                var tx = R.left + (if (fila % 2 == 0) 0f else 5 * u)
                while (tx < R.right) { drawArc(dorado.copy(alpha = 0.6f), 0f, 180f, false, Offset(tx, ty - 5 * u), Size(10 * u, 10 * u), style = Stroke(0.8f * u)); tx += 10 * u }
                ty += 5 * u; fila++
            }
        }
        drawPath(tejado, dorado, style = Stroke(1.6f * u))
        val cumbre = R.top + R.height * 0.08f
        drawRect(dorado, Offset(R.left + R.width * 0.06f, cumbre - 3 * u), Size(R.width * 0.88f, 3 * u))
        var cx0 = R.left + R.width * 0.1f
        while (cx0 < R.right - R.width * 0.1f) { drawPath(Path().apply { moveTo(cx0, cumbre - 3 * u); quadraticTo(cx0 + 3 * u, cumbre - 9 * u, cx0 + 6 * u, cumbre - 5 * u); lineTo(cx0 + 4 * u, cumbre - 3 * u); close() }, dorado); cx0 += 12 * u }
        listOf(R.left + 2 * u, R.right - 2 * u).forEach { x ->
            drawPath(Path().apply { moveTo(x - 4 * u, alero + 2 * u); lineTo(x, R.top + R.height * 0.04f); lineTo(x + 4 * u, alero + 2 * u); close() }, dorado)
            drawCircle(doradoLuz, 2.2f * u, Offset(x, R.top + R.height * 0.04f))
        }
        // El gablete del medio, con su rosetón.
        val gab = Rect(R.center.x - R.width * 0.12f, R.top, R.center.x + R.width * 0.12f, alero + 2 * u)
        drawPath(arcoApuntado(gab, gab.width * 0.9f), dorado)
        drawPath(arcoApuntado(gab.deflate(2.5f * u), gab.width * 0.85f), esmalte)
        roseton(Offset(gab.center.x, gab.top + gab.height * 0.6f), gab.width * 0.28f, oscuro, false)
        drawCircle(doradoLuz, 2.6f * u, Offset(gab.center.x, gab.top - 1 * u))
        // El frente: la arquería de ocho nichos.
        val frente = Rect(R.left, alero, R.right, zocalo)
        drawRect(dorado, frente.topLeft, frente.size)
        val n = 8; val paso = frente.width / n
        val nichos = (0 until n).map { j -> Rect(frente.left + paso * j + 3 * u, frente.top + 5 * u, frente.left + paso * (j + 1) - 3 * u, frente.bottom - 3 * u) }
        nichos.forEach { r -> drawPath(arcoApuntado(r, r.width * 0.75f), k(Color(0xFF12183A))) }
        val cartas = repartoMedievo(e, n)
        val inicio = (n - cartas.size) / 2
        cartas.forEachIndexed { i, clase ->
            val r = nichos[inicio + i]
            clipPath(arcoApuntado(r, r.width * 0.75f)) {
                val s = Rect(r.left + r.width * 0.14f, r.top + r.height * 0.36f, r.right - r.width * 0.14f, r.bottom + 2 * u)
                if (clase == 2) {
                    drawPath(Path().apply { moveTo(s.left, s.top); lineTo(s.center.x, s.top - s.width * 0.55f); lineTo(s.right, s.top); close() }, k(RUBI))
                    drawRect(k(VITELA), Offset(s.left + 2 * u, s.top - s.width * 0.45f), Size(s.width - 4 * u, s.height * 0.5f))
                    for (j in 0 until 3) drawLine(k(TINTA).copy(alpha = 0.5f), Offset(s.left + 4 * u, s.top - s.width * 0.3f + j * 3.2f * u), Offset(s.right - 4 * u, s.top - s.width * 0.3f + j * 3.2f * u), 0.6f * u)
                }
                drawRect(k(BOVEDA), s.topLeft, s.size)
                for (j in 0 until 4) estrellita(Offset(s.left + s.width * azar(i, 50 + j), s.top + s.height * (0.45f + 0.5f * azar(i, 60 + j))), 1.3f * u, dorado)
                if (clase != 2) {
                    val o = Offset(s.center.x, s.top + s.height * 0.32f)
                    drawPath(Path().apply { moveTo(s.left, s.top); quadraticTo(s.center.x - s.width * 0.1f, s.top + s.height * 0.25f, o.x, o.y); quadraticTo(s.center.x + s.width * 0.1f, s.top + s.height * 0.25f, s.right, s.top); close() },
                        k(mezcla(BOVEDA, Color.White, 0.1f)))
                    if (clase == 1) drawRect(dorado, Offset(s.center.x - 1.4f * u, s.top), Size(2.8f * u, s.height))
                    else {
                        haloMedievo(o, s.width * 1.0f, ORO_CINTA, if (oscuro) 1f else 0.8f)
                        rayosMedievo(o, s.width * 0.32f, s.width * 0.7f, ORO_CINTA, 8, 0.9f)
                    }
                    drawPath(mandorla(o, s.width * 0.22f, s.width * 0.5f), k(RUBI))
                }
            }
        }
        // Las columnillas entre los nichos, con su capitel.
        for (j in 0..n) {
            val x = frente.left + paso * j
            drawRect(doradoLuz, Offset(x - 1.6f * u, frente.top + 4 * u), Size(3.2f * u, frente.height - 6 * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(x + 0.6f * u, frente.top + 4 * u), Size(1 * u, frente.height - 6 * u))
            drawRect(dorado, Offset(x - 3 * u, frente.top + 2 * u), Size(6 * u, 3 * u))
        }
        drawRect(doradoLuz, frente.topLeft, Size(frente.width, 2.4f * u))
        // El zócalo, con las tres placas de esmalte.
        val base = Rect(R.left - 3 * u, zocalo, R.right + 3 * u, R.bottom)
        drawRect(dorado, base.topLeft, base.size)
        drawRect(doradoLuz, base.topLeft, Size(base.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(base.left, base.bottom - 2.5f * u), Size(base.width, 2.5f * u))
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "selladas", e.abiertas to "abiertas")
        rotulos.forEachIndexed { i, (cuenta, nombre) ->
            val r = Rect(base.left + base.width * (0.03f + 0.325f * i), base.top + 4 * u, base.left + base.width * (0.3f + 0.325f * i), base.bottom - 5 * u)
            drawRoundRect(if (oscuro) mezcla(Color(0xFF203C8E), Color(0xFF07091A), 0.3f) else Color(0xFF203C8E), r.topLeft, r.size, CornerRadius(2 * u))
            drawRoundRect(Color(0xFFE6C877), r.topLeft, r.size, CornerRadius(2 * u), style = Stroke(1.2f * u))
            drawRoundRect(Color(0xFFE6C877).copy(alpha = 0.5f), r.topLeft + Offset(2.5f * u, 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), CornerRadius(1.5f * u), style = Stroke(0.6f * u))
            val m = r.left + r.width * 0.36f
            textoEn(medidor, "$cuenta", Rect(r.left + 3 * u, r.top, m, r.bottom), letra, Color(0xFFF2D58A), 16f, peso = FontWeight.Bold)
            textoEn(medidor, nombre, Rect(m, r.top + 1 * u, r.right - 4 * u, r.bottom - 1 * u), letra, Color(0xFFF3EBD6), 9.5f, minimo = 7f)
        }
    }

    // ---------------------------------------------------------------- lo de alrededor

    /** La vitela de lo de alrededor: en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun vitelaHonda(oscuro: Boolean): Color = if (oscuro) mezcla(VITELA, Color(0xFF0E0C10), 0.74f) else VITELA
    /** La tinta sobre [vitelaHonda]. */
    private fun tintaHonda(oscuro: Boolean): Color = if (oscuro) VITELA else TINTA
    /** El rojo de las rúbricas. */
    private val RUBRICA = Color(0xFFA0222A)
    /** Los marfiles de las tapas de las obras. */
    private val MARFILES = listOf(Color(0xFFEDE3CC), Color(0xFFE4D6B8), Color(0xFFF0E9DA))

    /** Un cuadrifolio (cuatro lóbulos en cruz) de radio [r] centrado en [o]. */
    private fun DrawScope.cuadrifolio(o: Offset, r: Float, color: Color, trazo: Float = 0f) {
        val l = r * 0.5f
        listOf(Offset(0f, -l), Offset(l, 0f), Offset(0f, l), Offset(-l, 0f)).forEach { d ->
            if (trazo > 0f) drawCircle(color, l, o + d, style = Stroke(trazo)) else drawCircle(color, l, o + d)
        }
        if (trazo <= 0f) drawRect(color, Offset(o.x - l, o.y - l), Size(2 * l, 2 * l))
    }

    /**
     * El emblema de la pestaña [k] en [o], de radio [r], pintado a la grisalla: el libro de horas con sus broches (diario),
     * el reloj de arena (momentos), la media luna con su estrella (sueños), el cuadrifolio (ideario) y la carta cerrada con
     * su sello (cartas). [c] es la pintura y [f] el vidrio de debajo.
     */
    private fun DrawScope.emblema(k: Int, o: Offset, r: Float, c: Color, f: Color) {
        val u = density
        when (k) {
            0 -> {
                drawRoundRect(c, Offset(o.x - r * 0.72f, o.y - r), Size(r * 1.44f, r * 2f), CornerRadius(r * 0.15f))
                drawRect(f, Offset(o.x - r * 0.48f, o.y - r * 0.76f), Size(r * 0.96f, r * 1.52f), style = Stroke(0.6f * u))
                listOf(-0.4f, 0.4f).forEach { y -> drawRect(f, Offset(o.x + r * 0.5f, o.y + y * r - r * 0.08f), Size(r * 0.3f, r * 0.16f)) }
                drawCircle(f, r * 0.14f, o)
            }
            1 -> {
                drawRect(c, Offset(o.x - r * 0.75f, o.y - r), Size(r * 1.5f, r * 0.18f))
                drawRect(c, Offset(o.x - r * 0.75f, o.y + r * 0.82f), Size(r * 1.5f, r * 0.18f))
                val p = Path().apply {
                    moveTo(o.x - r * 0.55f, o.y - r * 0.82f); lineTo(o.x + r * 0.55f, o.y - r * 0.82f); lineTo(o.x + r * 0.08f, o.y)
                    lineTo(o.x + r * 0.55f, o.y + r * 0.82f); lineTo(o.x - r * 0.55f, o.y + r * 0.82f); lineTo(o.x - r * 0.08f, o.y); close()
                }
                drawPath(p, c)
                drawPath(Path().apply { moveTo(o.x - r * 0.3f, o.y + r * 0.78f); lineTo(o.x, o.y + r * 0.35f); lineTo(o.x + r * 0.3f, o.y + r * 0.78f); close() }, f)
            }
            2 -> {
                drawPath(mediaLunaMedievo(Offset(o.x - r * 0.15f, o.y + r * 0.05f), r * 0.92f), c)
                drawPath(estrella(Offset(o.x + r * 0.78f, o.y - r * 0.6f), r * 0.36f, 6, 0.45f), c)
            }
            3 -> {
                cuadrifolio(o, r * 1.05f, c)
                cuadrifolio(o, r * 0.6f, f, 0.7f * u)
                drawCircle(c, r * 0.16f, o)
            }
            else -> {
                drawRect(c, Offset(o.x - r, o.y - r * 0.62f), Size(r * 2f, r * 1.24f))
                drawLine(f, Offset(o.x - r, o.y - r * 0.62f), Offset(o.x, o.y + r * 0.1f), 0.8f * u)
                drawLine(f, Offset(o.x + r, o.y - r * 0.62f), Offset(o.x, o.y + r * 0.1f), 0.8f * u)
                drawCircle(f, r * 0.34f, Offset(o.x, o.y + r * 0.2f))
            }
        }
    }

    override val altoPestanas: Dp = 82.dp

    /**
     * Las pestañas: una vidriera de cinco lancetas en su tracería de piedra. Las cuatro que no están elegidas son de
     * grisalla (vidrio blanco en losanjes emplomados, con el emblema y el nombre pintados en pardo); la elegida está encendida
     * de color, azul con su orla roja, el emblema en amarillo de plata y un halo de luz. Cada cuenta va labrada en el
     * alféizar, debajo de su lanceta.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0E0C10), 0.6f) else x
        val alfeizar = h - 17 * u
        drawRect(c(PIEDRA))
        drawRect(Color.White.copy(alpha = 0.12f), Offset(0f, alfeizar), Size(w, 1.5f * u))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, alfeizar + 1.5f * u), Size(w, 2 * u))
        var j = 0; var x = 0f
        while (x < w && j < 20) { drawLine(Color.Black.copy(alpha = 0.2f), Offset(x, alfeizar + 3.5f * u), Offset(x, h), 0.7f * u); x += maxOf(36 * u, 1f); j++ }
        val labrado = if (oscuro) mezcla(PIEDRA, VITELA, 0.55f) else mezcla(MURO, TINTA, 0.5f)
        for (k in 0 until 5) {
            val q = quintoMedievo(k, w, h)
            val sel = k == e.actual
            val vano = Rect(q.left + 5 * u, 3 * u, q.right - 5 * u, alfeizar)
            val arco = arcoApuntado(vano, vano.width * 0.75f)
            // El derrame: la piedra del hueco, en sombra.
            drawPath(arcoApuntado(vano.inflate(2.5f * u), vano.width * 0.8f), Color.Black.copy(alpha = 0.25f))
            val vidrio: Color
            val pintura: Color
            val letraC: Color
            if (sel) {
                vidrio = if (oscuro) mezcla(VIDRIOS[0], Color.White, 0.08f) else VIDRIOS[0]
                pintura = Color(0xFFF2D27A)
                letraC = Color(0xFFF6EED8)
            } else {
                vidrio = if (oscuro) Color(0xFF39404A) else mezcla(VITELA, PLATA, 0.5f)
                pintura = if (oscuro) Color(0xFFB9B4A6) else Color(0xFF5A4630)
                letraC = if (oscuro) Color(0xFFD8D2C0) else TINTA
            }
            drawPath(arco, vidrio)
            clipPath(arco) {
                if (sel) {
                    // La orla roja y el halo de la luz que entra.
                    drawPath(arcoApuntado(vano, vano.width * 0.75f), VIDRIOS[1], style = Stroke(5 * u))
                    haloMedievo(Offset(vano.center.x, vano.top + 18 * u), vano.width * 0.7f, Color.White, 0.6f)
                    for (i in 0 until 6) drawRect(VIDRIOS[2 + i % 3].copy(alpha = 0.9f), Offset(vano.left + (i % 2) * (vano.width - 2.5f * u), vano.top + 16 * u + i / 2 * 12 * u), Size(2.5f * u, 6 * u))
                } else {
                    // Los losanjes emplomados de la grisalla.
                    var d = vano.left - vano.height; var i = 0
                    while (d < vano.right && i < 40) {
                        drawLine(Color.Black.copy(alpha = 0.16f), Offset(d, vano.bottom), Offset(d + vano.height * 0.7f, vano.top), 0.6f * u)
                        drawLine(Color.Black.copy(alpha = 0.16f), Offset(d + vano.height * 0.7f, vano.bottom), Offset(d, vano.top), 0.6f * u)
                        d += maxOf(9 * u, 1f); i++
                    }
                }
            }
            // Los plomos del contorno y el baquetón de piedra.
            drawPath(arco, Color(0xFF2A2626), style = Stroke(1.2f * u))
            drawPath(arcoApuntado(vano.inflate(1.6f * u), vano.width * 0.78f), c(mezcla(PIEDRA, Color.White, 0.18f)), style = Stroke(1.4f * u))
            // El emblema en su medallón y el nombre en la cartela.
            val o = Offset(vano.center.x, vano.top + 22 * u)
            drawCircle(if (sel) VIDRIOS[1] else vidrio, 9 * u, o)
            drawCircle(Color(0xFF2A2626), 9 * u, o, style = Stroke(1 * u))
            emblema(k, o, 5.5f * u, pintura, if (sel) VIDRIOS[1] else vidrio)
            val nombre = Rect(vano.left + 2 * u, vano.top + 34 * u, vano.right - 2 * u, vano.top + 49 * u)
            drawLine(Color(0xFF2A2626), Offset(vano.left, nombre.top - 1 * u), Offset(vano.right, nombre.top - 1 * u), 0.8f * u)
            textoEn(medidor, e.nombres.getOrElse(k) { "" }, nombre, letra, letraC, 11f, peso = FontWeight.Bold, minimo = 7f)
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", Rect(q.left, alfeizar + 3 * u, q.right, h - 1 * u), letra,
                if (sel) (if (oscuro) Color(0xFFF2D27A) else RUBRICA) else labrado, 10.5f, peso = FontWeight.Bold, minimo = 6f)
        }
    }

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.18f, 0.24f, 0.95f, 0.76f)
    override val letraCampo: FontFamily = LetraExtra.goudy
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) VITELA else TINTA

    /**
     * El buscador: una lauda de latón embutida en la losa oscura del coro, con su filete grabado y, a la izquierda, un
     * cuadrifolio de latón con una rosa. Con el foco, el latón se pule (más claro, con su brillo) y el cuadrifolio se esmalta
     * de rojo.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) mezcla(LOSA_OSCURA, Color.Black, 0.35f) else LOSA_OSCURA)
        for (j in 0 until 18) drawCircle(Color.White.copy(alpha = 0.05f), (0.5f + azar(j, 4)) * u, Offset(w * azar(j, 5), h * azar(j, 6)))
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.4f), 1.5f * u)
        val laton0 = if (e.enfocado) mezcla(LATON, Color.White, 0.18f) else LATON
        val laton = if (oscuro) mezcla(laton0, Color.Black, 0.6f) else laton0
        val grabado = if (oscuro) mezcla(LATON, Color.Black, 0.2f) else mezcla(LATON, TINTA, 0.6f)
        val banda = Rect(w * cajaCampo.left - 8 * u, 7 * u, w - 7 * u, h - 7 * u)
        drawRect(Color.Black.copy(alpha = 0.4f), banda.inflate(1.2f * u).topLeft, banda.inflate(1.2f * u).size)
        drawRect(laton, banda.topLeft, banda.size)
        drawRect(grabado.copy(alpha = 0.7f), banda.deflate(2 * u).topLeft, banda.deflate(2 * u).size, style = Stroke(0.7f * u))
        if (e.enfocado) drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.5f), Offset(banda.left + 4 * u, banda.top + 1.2f * u), Offset(banda.right - 4 * u, banda.top + 1.2f * u), 0.8f * u)
        // El cuadrifolio de latón con la rosa.
        val o = Offset(w * cajaCampo.left * 0.46f, h / 2)
        val r = minOf(17 * u, h * 0.36f)
        cuadrifolio(o, r * 1.08f, Color.Black.copy(alpha = 0.4f))
        cuadrifolio(o, r, laton)
        cuadrifolio(o, r * 0.72f, if (e.enfocado) (if (oscuro) mezcla(RUBI, Color.Black, 0.3f) else RUBI) else grabado.copy(alpha = 0.35f))
        for (j in 0 until 5) drawCircle(laton, r * 0.13f, polarMedievo(o, r * 0.18f, j * 72f - 90f))
        drawCircle(grabado, r * 0.1f, o)
        if (e.vacio) pistaMedievo(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), letraCampo,
            tintaCampo(oscuro).copy(alpha = 0.55f))
    }

    /**
     * Las obras: el poyo de una ventana del claustro. El muro de piedra con la tracería ciega apenas marcada (dos lancetas y
     * un óculo) y, abajo, la losa del alféizar con un cojín de terciopelo carmesí y su fleco de oro, donde se apoyan.
     * Distinto de la librería del coro y de la cajonera de la sacristía.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0E0C10), 0.66f) else x
        drawRect(c(mezcla(PIEDRA, VITELA, 0.3f)))
        // Las hiladas del muro.
        var y = 0f; var k = 0
        while (y < h && k < 20) {
            drawLine(Color.Black.copy(alpha = 0.08f), Offset(0f, y), Offset(w, y), 0.8f * u)
            var x = if (k % 2 == 0) 0f else 24 * u; var j = 0
            while (x < w && j < 20) { drawLine(Color.Black.copy(alpha = 0.06f), Offset(x, y), Offset(x, y + 16 * u), 0.8f * u); x += maxOf(48 * u, 1f); j++ }
            y += maxOf(16 * u, 1f); k++
        }
        // La tracería ciega: dos lancetas y el óculo, en relieve suave.
        val ventana = Rect(w * 0.18f, 8 * u, w * 0.82f, h - 32 * u)
        val tr = c(mezcla(PIEDRA, Color.White, 0.25f))
        drawPath(arcoApuntado(ventana, ventana.width * 0.5f), Color.Black.copy(alpha = 0.06f))
        drawPath(arcoApuntado(ventana, ventana.width * 0.5f), tr, style = Stroke(2.5f * u))
        val m = ventana.center.x
        listOf(Rect(ventana.left + 10 * u, ventana.top + ventana.height * 0.45f, m - 5 * u, ventana.bottom),
            Rect(m + 5 * u, ventana.top + ventana.height * 0.45f, ventana.right - 10 * u, ventana.bottom)).forEach { l ->
            drawPath(arcoApuntado(l, l.width * 0.8f), tr, style = Stroke(1.6f * u))
        }
        val oculo = Offset(m, ventana.top + ventana.height * 0.3f)
        drawCircle(tr, ventana.height * 0.14f, oculo, style = Stroke(1.6f * u))
        cuadrifolio(oculo, ventana.height * 0.11f, tr, 1.2f * u)
        // La losa del alféizar.
        val losa = h - 30 * u
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, losa - 4 * u), Size(w, 4 * u))
        drawRect(c(PIEDRA), Offset(0f, losa), Size(w, h - losa))
        drawRect(Color.White.copy(alpha = 0.15f), Offset(0f, losa + 18 * u), Size(w, 1.4f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, losa + 19.4f * u), Size(w, 2 * u))
        // El cojín de terciopelo, con su fleco de oro.
        val cojin = Rect(w * 0.06f, losa - 3 * u, w * 0.94f, losa + 14 * u)
        drawRoundRect(c(TERCIOPELOS[0]), cojin.topLeft, cojin.size, CornerRadius(6 * u))
        drawRoundRect(Color.White.copy(alpha = 0.1f), Offset(cojin.left + 6 * u, cojin.top + 2 * u), Size(cojin.width - 12 * u, 3 * u), CornerRadius(1.5f * u))
        drawLine(c(ORO), Offset(cojin.left + 4 * u, cojin.bottom - 2 * u), Offset(cojin.right - 4 * u, cojin.bottom - 2 * u), 1.2f * u)
        var fx = cojin.left + 5 * u; k = 0
        while (fx < cojin.right - 4 * u && k < 160) { drawLine(c(ORO).copy(alpha = 0.8f), Offset(fx, cojin.bottom - 2 * u), Offset(fx, cojin.bottom + 2.5f * u), 0.7f * u); fx += maxOf(2.2f * u, 1f); k++ }
        listOf(cojin.left + 2 * u, cojin.right - 2 * u).forEach { bx -> drawCircle(c(ORO), 2.5f * u, Offset(bx, cojin.center.y)); drawLine(c(ORO), Offset(bx, cojin.center.y), Offset(bx, cojin.bottom + 6 * u), 1.2f * u) }
    }

    /**
     * La tapa de una obra: una hoja de díptico de marfil tallado (tres tonos de marfil según la obra). Arriba, una arquería
     * de tres arcos apuntados con sus columnillas; debajo, la banda esmaltada (azul, roja o verde) con el tipo; en medio, el
     * título en negro; abajo, el autor, el año y un friso de cuadrifolios. En miniatura, un arco con la inicial.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        val semilla = e.semilla and 0x7fffffff
        val marfil0 = MARFILES[semilla % MARFILES.size]
        val marfil = if (oscuro) mezcla(marfil0, Color(0xFF0E0C10), 0.28f) else marfil0
        val talla = mezcla(marfil, Color(0xFF5A4630), 0.45f)
        val esmalte = listOf(VIDRIOS[0], VIDRIOS[1], VIDRIOS[3])[(semilla / 3) % 3].let { if (oscuro) mezcla(it, Color.Black, 0.2f) else it }
        drawRect(marfil)
        // La pátina del marfil: unas vetas finas a lo largo.
        vetas(Rect(0f, 0f, w, h), Color(0xFF8A7A5A).copy(alpha = 0.18f), 30 + semilla % 9, densidad = 0.5f, vertical = true, grosor = 0.5f)
        val t1 = maxOf(1f, w * 0.012f)
        val marco = Rect(w * 0.05f, w * 0.05f, w * 0.95f, h - w * 0.05f)
        drawRect(talla, marco.topLeft, marco.size, style = Stroke(t1 * 1.4f))
        drawRect(Color.White.copy(alpha = 0.35f), marco.deflate(t1 * 1.6f).topLeft, marco.deflate(t1 * 1.6f).size, style = Stroke(t1 * 0.8f))
        if (e.mini) {
            val v = Rect(w * 0.2f, h * 0.18f, w * 0.8f, h * 0.84f)
            drawPath(arcoApuntado(v, v.width * 0.7f), talla.copy(alpha = 0.25f))
            drawPath(arcoApuntado(v, v.width * 0.7f), talla, style = Stroke(t1 * 1.6f))
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifBlank { "·" }, Rect(v.left, v.top + v.height * 0.3f, v.right, v.bottom), letraTapa, TINTA, s * 0.4f)
            return
        }
        // La arquería de arriba: tres arcos con sus columnillas, en hueco.
        val fila = Rect(marco.left + w * 0.05f, marco.top + w * 0.05f, marco.right - w * 0.05f, h * 0.27f)
        val a = fila.width / 3f
        for (j in 0 until 3) {
            val v = Rect(fila.left + a * j + a * 0.12f, fila.top, fila.left + a * (j + 1) - a * 0.12f, fila.bottom)
            drawPath(arcoApuntado(v, v.width * 0.9f), talla.copy(alpha = 0.3f))
            drawPath(arcoApuntado(v, v.width * 0.9f), talla, style = Stroke(t1))
            cuadrifolio(Offset(v.center.x, v.top + v.width * 0.55f), v.width * 0.24f, talla, t1 * 0.7f)
        }
        listOf(0, 1, 2, 3).forEach { j -> drawRect(talla, Offset(fila.left + a * j - t1, fila.top + fila.height * 0.3f), Size(t1 * 2, fila.height * 0.7f)) }
        // La banda esmaltada con el tipo.
        val banda = Rect(marco.left + t1 * 2, h * 0.29f, marco.right - t1 * 2, h * 0.36f)
        drawRect(esmalte, banda.topLeft, banda.size)
        drawRect(Color(0xFFD9B45A), banda.topLeft, banda.size, style = Stroke(t1))
        textoEn(medidor, e.tipo.uppercase(), banda.deflate(t1 * 2), letra, Color(0xFFF6EED8), s * 0.07f, espaciado = s * 0.008f, minimo = 4f)
        tituloMedievo(medidor, e.titulo.ifBlank { "—" }, Rect(marco.left + w * 0.06f, h * 0.39f, marco.right - w * 0.06f, h * 0.7f), letraTapa, TINTA, s * 0.14f)
        textoEn(medidor, e.autor, Rect(marco.left + w * 0.06f, h * 0.72f, marco.right - w * 0.06f, h * 0.79f), letra, TINTA.copy(alpha = 0.85f), s * 0.075f, minimo = 4f)
        textoEn(medidor, e.anio, Rect(marco.left + w * 0.06f, h * 0.79f, marco.right - w * 0.06f, h * 0.85f), letra, RUBRICA, s * 0.07f, minimo = 4f)
        // El friso de cuadrifolios.
        val yf = h * 0.9f
        var x = marco.left + w * 0.12f; var j = 0
        while (x < marco.right - w * 0.06f && j < 12) { cuadrifolio(Offset(x, yf), w * 0.05f, talla, t1 * 0.8f); x += maxOf(w * 0.15f, 1f); j++ }
    }

    override val margenRecuerdo: Dp = 18.dp
    override val altoRotuloRecuerdo: Dp = 48.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El recuerdo: una hoja del calendario de un libro de horas. La orla de barra en el margen izquierdo (oro y azul con
     * hojitas de hiedra), «Hace N años» en rúbrica roja con la letra de libro, la fecha y el clima en negro, el doble filete
     * azul y rojo, y a la derecha el medallón del mes, del color del ánimo, con su aro de oro y una estrella.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenRecuerdo.toPx(); val alto = altoRotuloRecuerdo.toPx()
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(1.5f * u, 1.5f * u), Size(w - 1.5f * u, h - 1.5f * u))
        drawRect(vitelaHonda(oscuro), Offset.Zero, Size(w - 1.5f * u, h - 1.5f * u))
        val oro = if (oscuro) mezcla(ORO, Color.Black, 0.15f) else ORO
        val azul = if (oscuro) mezcla(BOVEDA, VITELA, 0.35f) else BOVEDA
        val rojo = if (oscuro) mezcla(RUBRICA, VITELA, 0.3f) else RUBRICA
        // La orla de barra con la hiedra.
        drawRect(oro, Offset(5 * u, 5 * u), Size(2.4f * u, h - 11.5f * u))
        drawRect(azul, Offset(7.4f * u, 5 * u), Size(1.4f * u, h - 11.5f * u))
        var y = 9 * u; var j = 0
        while (y < h - 8 * u && j < 40) {
            val hoja = Offset(11 * u, y)
            drawLine(oro.copy(alpha = 0.7f), Offset(8.8f * u, y - 1 * u), hoja, 0.5f * u)
            drawPath(Path().apply { moveTo(hoja.x, hoja.y - 1.6f * u); lineTo(hoja.x + 1.6f * u, hoja.y); lineTo(hoja.x, hoja.y + 1.6f * u); lineTo(hoja.x - 0.6f * u, hoja.y); close() },
                if (j % 2 == 0) oro else azul)
            y += maxOf(11 * u, 1f); j++
        }
        izquierdaMedievo(medidor, haceMedievo(e.anios), m, 5 * u, 27 * u, w - 2 * m - 26 * u, letraTapa, rojo, 18f)
        val clima = climaMedievo(e.clima)
        izquierdaMedievo(medidor, if (clima.isEmpty()) e.fecha else "${e.fecha} · $clima", m, 26 * u, alto - 5 * u, w - 2 * m - 22 * u, letra,
            tintaHonda(oscuro).copy(alpha = 0.85f), 12f, minimo = 8f)
        drawLine(azul.copy(alpha = 0.7f), Offset(m, alto - 3.4f * u), Offset(w - 8 * u, alto - 3.4f * u), 0.8f * u)
        drawLine(rojo.copy(alpha = 0.6f), Offset(m, alto - 1.8f * u), Offset(w - 8 * u, alto - 1.8f * u), 0.6f * u)
        e.animo?.let { a ->
            val o = Offset(w - 19 * u, 18 * u)
            drawCircle(oro, 11 * u, o)
            drawCircle(azul, 9.4f * u, o)
            drawCircle(a, 7.6f * u, o)
            drawPath(estrella(o, 4 * u, 6, 0.45f), oro)
        }
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
 * La tapa es ese libro labrado de frente, con lazo de oro, estrellas de lacería, la fecha arriba y la cartela de
 * pergamino para lo escrito; primero se levanta la solapa y luego gira la tapa, y se ve la hoja con su cabecera
 * iluminada (de noche, una luna en la punta de la solapa y las estrellas del farol).
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 186.dp
    override val cajaTapa = Rect(0.235f, 0.34f, 0.665f, 0.68f)
    override val letraTapa: FontFamily = LetraExtra.amiri
    override val tamTapa = 14f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** El pergamino de la cartela y de las hojas: se hunde menos que el cuero, para que el extracto se lea siempre. */
    private fun pergamino(oscuro: Boolean, noche: Boolean): Color {
        var r = YESO
        if (oscuro) r = mezcla(r, Color(0xFF100C0A), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val piel = c(PIELES[(e.semilla and 0x7fffffff) % PIELES.size])
        val oro = mezcla(c(ORO), ORO, 0.35f)
        val oroClaro = mezcla(c(ORO_CLARO), ORO_CLARO, 0.4f)
        val hoja = pergamino(oscuro, n)
        val libro = Rect(w * 0.025f, h * 0.05f, w * 0.93f, h * 0.9f)
        val lomo = Rect(libro.left, libro.top, libro.left + minOf(w * 0.065f, 26 * u), libro.bottom)
        val tapa = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(libro.left + 3 * u, libro.top + 5 * u), Size(w * 0.95f - libro.left, libro.height), CornerRadius(3 * u))
        val bloque = Rect(tapa.left, tapa.top + 3 * u, tapa.right + 2 * u, tapa.bottom + 3 * u)
        drawRect(c(YESO), bloque.topLeft, bloque.size)
        for (j in 1..3) drawLine(c(YESO_SOMBRA), Offset(tapa.left, tapa.bottom + j * 0.8f * u), Offset(bloque.right, tapa.bottom + j * 0.8f * u), 0.5f * u)
        // La primera hoja: el marco dorado, la cabecera iluminada en oro y azul y los renglones con sus rosetas.
        if (a > 0.01f) {
            val pag = Rect(tapa.left + 1 * u, tapa.top + 3 * u, tapa.right - 1 * u, tapa.bottom - 1 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            listOf(10f to 0.14f, 5f to 0.12f).forEach { (an, al) -> drawRect(Color.Black.copy(alpha = al), pag.topLeft, Size(an * u, pag.height)) }
            val marco = Rect(pag.left + 16 * u, pag.top + 10 * u, pag.right - 12 * u, pag.bottom - 10 * u)
            drawRect(c(ORO), marco.topLeft, marco.size, style = Stroke(1.2f * u))
            drawRect(c(AZUL).copy(alpha = 0.6f), marco.deflate(2 * u).topLeft, marco.deflate(2 * u).size, style = Stroke(0.6f * u))
            val cabecera = Rect(marco.left + 6 * u, marco.top + 6 * u, marco.right - 6 * u, marco.top + 20 * u)
            drawRect(c(ORO), cabecera.topLeft, cabecera.size)
            drawRect(c(AZUL), cabecera.deflate(2.5f * u).topLeft, cabecera.deflate(2.5f * u).size)
            var sx0 = cabecera.left + 8 * u
            while (sx0 < cabecera.right - 6 * u) { drawPath(estrella(Offset(sx0, cabecera.center.y), 3.4f * u, 8, 0.55f), c(ORO)); sx0 += 12 * u }
            var ry = cabecera.bottom + 8 * u; var j = 0
            while (ry < marco.bottom - 4 * u) {
                var x = marco.right - 8 * u; var k = 0
                while (x > marco.left + 10 * u) {
                    val largo = minOf((4f + 10f * azar(e.semilla + j, k)) * u, x - marco.left - 8 * u)
                    drawLine(c(TINTA).copy(alpha = 0.6f), Offset(x, ry), Offset(x - largo, ry), 1.4f * u)
                    x -= largo + 2.6f * u; k++
                    if (k % 5 == 4 && x > marco.left + 16 * u) { drawCircle(c(ORO), 1.6f * u, Offset(x - 1 * u, ry)); x -= 5 * u }
                }
                ry += 6.5f * u; j++
            }
        }
        // La tapa de cuero labrado, que gira sobre el lomo cuando ya se levantó la solapa.
        val p1 = (a / 0.45f).coerceIn(0f, 1f)
        val p2 = ((a - 0.2f) / 0.8f).coerceIn(0f, 1f)
        val sx = 1f - 0.9f * p2
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(piel, Color.White, 0.12f), piel, mezcla(piel, Color.Black, 0.32f)), tapa.top, tapa.bottom),
                tapa.topLeft, tapa.size, CornerRadius(2.5f * u))
            val r1 = tapa.deflate(7 * u)
            val r2 = r1.deflate(10 * u)
            listOf(r1, r2).forEach { r -> drawRect(oro.copy(alpha = 0.8f), r.topLeft, r.size, style = Stroke(1 * u)) }
            // El lazo dorado en la banda: dos cintas que se cruzan una y otra vez.
            val banda = r1.deflate(5 * u)
            fun lazo(x0: Float, y0: Float, x1: Float, y1: Float) {
                val largo = kotlin.math.hypot(x1 - x0, y1 - y0)
                val pasos = (largo / (3 * u)).toInt().coerceAtLeast(2)
                listOf(0f, 3.1416f).forEach { fase ->
                    val p = Path()
                    for (s in 0..pasos) {
                        val f = s / pasos.toFloat()
                        val d = 3 * u * kotlin.math.sin(f * largo / (6 * u) + fase)
                        val x = x0 + (x1 - x0) * f + (if (x0 == x1) d else 0f)
                        val y = y0 + (y1 - y0) * f + (if (y0 == y1) d else 0f)
                        if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                    }
                    drawPath(p, oro.copy(alpha = 0.7f), style = Stroke(0.8f * u))
                }
            }
            lazo(banda.left, banda.top, banda.right, banda.top); lazo(banda.left, banda.bottom, banda.right, banda.bottom)
            lazo(banda.left, banda.top, banda.left, banda.bottom); lazo(banda.right, banda.top, banda.right, banda.bottom)
            // Estrellas de lacería en las esquinas del recuadro y un punteado de oro en el campo.
            listOf(r2.topLeft, r2.topRight, r2.bottomLeft, r2.bottomRight).forEach { o ->
                drawPath(estrella(o, 8 * u, 8, 0.6f), mezcla(piel, Color.Black, 0.2f))
                drawPath(estrella(o, 8 * u, 8, 0.6f), oro, style = Stroke(0.9f * u))
                drawCircle(oro, 1.6f * u, o)
            }
            var dy = r2.top + 6 * u; var fila = 0
            while (dy < r2.bottom - 4 * u) {
                var dx = r2.left + (if (fila % 2 == 0) 8f else 13f) * u
                while (dx < r2.right - 4 * u) { drawCircle(oro.copy(alpha = 0.35f), 0.7f * u, Offset(dx, dy)); dx += 10 * u }
                dy += 7 * u; fila++
            }
            // La fecha en oro, entre dos estrellitas, arriba de la cartela.
            val cartela = Rect(w * 0.22f, h * 0.29f, w * 0.68f, h * 0.73f)
            val alto = Rect(r2.left + 14 * u, r2.top + 2 * u, w * 0.74f, cartela.top - 3 * u)
            drawRect(mezcla(piel, Color.Black, 0.25f), alto.topLeft, alto.size)
            drawRect(oro.copy(alpha = 0.6f), alto.topLeft, alto.size, style = Stroke(0.6f * u))
            listOf(alto.left + 7 * u, alto.right - 7 * u).forEach { x -> drawPath(estrella(Offset(x, alto.center.y), 3.6f * u, 8, 0.5f), oro) }
            textoEn(medidor, e.fecha, Rect(alto.left + 14 * u, alto.top + 1 * u, alto.right - 14 * u, alto.bottom - 1 * u), letra, oroClaro, 13f, minimo = 7f)
            // La cartela de pergamino embutida, con sus puntas: ahí va lo escrito.
            val forma = Path().apply {
                moveTo(cartela.left, cartela.top); lineTo(cartela.right, cartela.top); lineTo(cartela.right + w * 0.035f, cartela.center.y)
                lineTo(cartela.right, cartela.bottom); lineTo(cartela.left, cartela.bottom); lineTo(cartela.left - w * 0.035f, cartela.center.y); close()
            }
            drawPath(forma, mezcla(piel, Color.Black, 0.35f), style = Stroke(5 * u))
            drawPath(forma, hoja)
            drawPath(forma, oro, style = Stroke(1.2f * u))
            listOf(cartela.left - w * 0.035f + 6 * u, cartela.right + w * 0.035f - 6 * u).forEach { x -> drawPath(estrella(Offset(x, cartela.center.y), 3.2f * u, 8, 0.5f), c(VERDE)) }
            if (!e.vacio && e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(cartela.left, cartela.bottom + 3 * u, cartela.right, r2.bottom - 2 * u), letra, oroClaro, 10.5f, minimo = 6f)
            }
            if (p2 > 0f) drawRect(Color.Black.copy(alpha = 0.4f * p2), tapa.topLeft, tapa.size)
        }
        if (p2 > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (k in 0 until 3) drawRect(Color.Black.copy(alpha = 0.12f * (3 - k) / 3f), Offset(borde, tapa.top + 3 * u), Size((4 + 4 * k) * u, tapa.height - 4 * u))
        }
        // El lomo, liso, con sus filetes.
        drawRoundRect(mezcla(piel, Color.Black, 0.2f), lomo.topLeft, lomo.size, CornerRadius(2.5f * u))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.2f, lomo.height))
        listOf(0.06f, 0.94f).forEach { f -> drawLine(oro.copy(alpha = 0.7f), Offset(lomo.left + 2 * u, lomo.top + lomo.height * f), Offset(lomo.right - 2 * u, lomo.top + lomo.height * f), 0.8f * u) }
        listOf(0.3f, 0.5f, 0.7f).forEach { f -> drawPath(estrella(Offset(lomo.center.x, lomo.top + lomo.height * f), minOf(lomo.width * 0.3f, 6 * u), 8, 0.55f), oro.copy(alpha = 0.7f)) }
        // La solapa (lisán): la franja que cubre el corte y su lengüeta en punta sobre la tapa. Se levanta primero.
        val solapa = mezcla(piel, Color.Black, 0.15f)
        val canto = Rect(libro.right, libro.top + 1 * u, w * 0.975f, libro.bottom - 1 * u)
        scale(1f - 0.92f * p1, 1f, Offset(canto.right, canto.center.y)) {
            val punta = Offset(w * 0.76f, tapa.center.y)
            val lengua = Path().apply { moveTo(canto.left, tapa.top + tapa.height * 0.2f); lineTo(punta.x, punta.y); lineTo(canto.left, tapa.top + tapa.height * 0.8f); close() }
            drawPath(lengua, Color.Black.copy(alpha = 0.25f), style = Stroke(4 * u))
            drawPath(lengua, solapa)
            drawPath(lengua, oro.copy(alpha = 0.8f), style = Stroke(0.9f * u))
            val dentro = Path().apply { moveTo(canto.left - 3 * u, tapa.top + tapa.height * 0.27f); lineTo(punta.x + 10 * u, punta.y); lineTo(canto.left - 3 * u, tapa.top + tapa.height * 0.73f) }
            drawPath(dentro, oro.copy(alpha = 0.45f), style = Stroke(0.6f * u))
            drawRect(solapa, canto.topLeft, canto.size)
            drawRect(Color.Black.copy(alpha = 0.3f), canto.topLeft, Size(1 * u, canto.height))
            drawRect(Color.White.copy(alpha = 0.08f), Offset(canto.left + canto.width * 0.4f, canto.top), Size(canto.width * 0.2f, canto.height))
            // En la punta: una estrella dorada; de noche, la luna.
            val cp = Offset(punta.x + 9 * u, punta.y)
            val rp = 5 * u
            if (n) {
                drawCircle(LUNA, rp, cp)
                drawCircle(solapa, rp * 0.8f, Offset(cp.x + rp * 0.4f, cp.y - rp * 0.2f))
            } else {
                drawPath(estrella(cp, rp, 8, 0.55f), oro)
                drawCircle(c(VERDE), rp * 0.3f, cp)
            }
        }
        // De noche, el farol calado alumbra y echa sus estrellas alrededor.
        if (n) {
            clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.5f, -h * 0.15f), h * 1.1f, LUZ_FAROL.copy(alpha = 0.2f)) }
            estrellasFarol(Rect(0f, 0f, w, h * 0.2f), 8, e.semilla)
            estrellasFarol(Rect(0f, h * 0.82f, w, h), 6, e.semilla + 7)
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /** Una cinta de lacería de [x0] a [x1] entre [y0] y [y1]: dos zigzags que se cruzan, de [paso] de onda. */
    private fun DrawScope.laceria(x0: Float, x1: Float, y0: Float, y1: Float, paso: Float, a: Color, b: Color, grosor: Float) {
        for (fase in 0..1) {
            val camino = Path()
            var x = x0; var k = 0
            camino.moveTo(x0, if (fase == 0) y0 else y1)
            while (x < x1) {
                x = minOf(x + paso / 2, x1); k++
                camino.lineTo(x, if ((k + fase) % 2 == 0) y0 else y1)
            }
            drawPath(camino, if (fase == 0) a else b, style = Stroke(grosor))
        }
    }

    /**
     * La cabecera iluminada, como el título de una sura: un recuadro de oro con su filete verde y una estrella de ocho puntas
     * en cada esquina y, en los márgenes, a cada lado, la palmeta que sale del recuadro; a la izquierda, un arco de herradura
     * verde dentro de su alfiz de almagra, con la estrella de ocho puntas en oro (de noche, la media luna y una estrella); a
     * su derecha, el rótulo en cúfico verde, la fecha (si es larga, en dos renglones) y el detalle.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val oro = c(ORO); val verde = p.acento; val rojo = p.detalle
        val panel = Rect(14f * u, 8f * u, w - 14f * u, h - 6f * u)
        listOf(panel.left to -1f, panel.right to 1f).forEach { (x, s) ->
            val cy = panel.center.y
            drawLine(oro, Offset(x, cy), Offset(x + s * 4f * u, cy), 1f * u)
            val palmeta = Path().apply {
                moveTo(x + s * 13f * u, cy)
                quadraticTo(x + s * 8f * u, cy - 7f * u, x + s * 3.5f * u, cy)
                quadraticTo(x + s * 8f * u, cy + 7f * u, x + s * 13f * u, cy)
                close()
            }
            drawPath(palmeta, c(VERDE))
            drawPath(palmeta, oro, style = Stroke(0.9f * u))
            drawCircle(oro, 1.3f * u, Offset(x + s * 7.5f * u, cy))
        }
        drawRect(oro, panel.topLeft, panel.size, style = Stroke(2.4f * u))
        val dentro = panel.deflate(3.5f * u)
        drawRect(verde.copy(alpha = 0.7f), dentro.topLeft, dentro.size, style = Stroke(0.8f * u))
        listOf(panel.topLeft, Offset(panel.right, panel.top), Offset(panel.right, panel.bottom), Offset(panel.left, panel.bottom))
            .forEach { drawPath(estrella(it, 4.5f * u, 8, 0.55f), oro) }
        // El arco de herradura en su alfiz.
        val ar = Rect(panel.left + 10f * u, panel.top + 13f * u, panel.left + 50f * u, panel.bottom - 8f * u)
        drawRect(rojo.copy(alpha = 0.7f), Offset(ar.left - 4f * u, ar.top - 5f * u), Size(ar.width + 8f * u, ar.height + 5f * u), style = Stroke(1f * u))
        drawPath(arcoHerradura(ar.translate(1f * u, 1.4f * u)), Color.Black.copy(alpha = 0.15f))
        drawPath(arcoHerradura(ar), c(VERDE))
        val o = Offset(ar.center.x, ar.top + 23f * u)
        if (!e.noche) {
            drawPath(estrella(o, 11f * u, 8, 0.62f), oro)
            drawCircle(c(VERDE), 3.5f * u, o)
            drawCircle(oro, 1.4f * u, o)
            listOf(ar.left + 10f * u, ar.right - 10f * u).forEach { x -> drawLine(oro.copy(alpha = 0.7f), Offset(x, o.y + 18f * u), Offset(x, ar.bottom - 4f * u), 1.2f * u) }
        } else {
            val luna = mezcla(LUNA, p.acento, 0.2f)
            drawPath(mediaLunaMedievo(Offset(o.x - 1f * u, o.y + 2f * u), 9f * u), luna)
            drawPath(estrella(Offset(o.x + 8f * u, o.y - 9f * u), 2.8f * u, 8, 0.5f), luna)
        }
        drawPath(arcoHerradura(ar), oro, style = Stroke(1.2f * u))
        val x0 = ar.right + 12f * u; val x1 = panel.right - 8f * u
        textoEn(medidor, e.rotulo, Rect(x0, panel.top + 8f * u, x1, panel.top + 29f * u), LetraExtra.kufi, rotuloMedievo(p, e.noche), 15f, minimo = 8f)
        fechaMedievo(medidor, e.fecha, Rect(x0, panel.top + 30f * u, x1, panel.top + 71f * u), letra, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(x0, panel.top + 72f * u, x1, panel.top + 89f * u), letra, p.tenue, 11f, minimo = 7f)
    }

    /**
     * El marco de la página iluminada: el filete verde y el de almagra, con una hilera de puntos de oro entre ellos y una
     * estrella de ocho puntas en cada esquina; arriba y abajo, una cinta de lacería; en el margen derecho, la shamsa (el
     * medallón de oro). En modo oscuro, el farol de latón calado cuelga arriba a la izquierda: el papel se hunde en la
     * penumbra y el farol echa su luz dorada y sus estrellas por la pared.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val verde = p.acento; val rojo = p.detalle
        val oro = if (noche) mezcla(ORO, NOCHE, 0.3f) else ORO
        drawRect(verde.copy(alpha = 0.45f), Offset(10f * u, 10f * u), Size(w - 20f * u, h - 20f * u), style = Stroke(1.5f * u))
        drawRect(rojo.copy(alpha = 0.35f), Offset(14f * u, 14f * u), Size(w - 28f * u, h - 28f * u), style = Stroke(1f * u))
        var x = 16f * u
        while (x < w - 12f * u) { drawCircle(oro.copy(alpha = 0.3f), 0.7f * u, Offset(x, 12f * u)); drawCircle(oro.copy(alpha = 0.3f), 0.7f * u, Offset(x, h - 12f * u)); x += 8f * u }
        var y = 16f * u
        while (y < h - 12f * u) { drawCircle(oro.copy(alpha = 0.3f), 0.7f * u, Offset(12f * u, y)); drawCircle(oro.copy(alpha = 0.3f), 0.7f * u, Offset(w - 12f * u, y)); y += 8f * u }
        listOf(Offset(12f * u, 12f * u), Offset(w - 12f * u, 12f * u), Offset(w - 12f * u, h - 12f * u), Offset(12f * u, h - 12f * u))
            .forEach { drawPath(estrella(it, 5.5f * u, 8, 0.55f), oro.copy(alpha = 0.55f)) }
        laceria(22f * u, w - 22f * u, 19f * u, 26f * u, 10f * u, verde.copy(alpha = 0.25f), rojo.copy(alpha = 0.2f), 0.9f * u)
        laceria(22f * u, w - 22f * u, h - 26f * u, h - 19f * u, 10f * u, verde.copy(alpha = 0.25f), rojo.copy(alpha = 0.2f), 0.9f * u)
        val sh = Offset(w - 12f * u, h * 0.46f)
        drawCircle(p.color, 7f * u, sh)
        drawCircle(oro.copy(alpha = 0.45f), 7f * u, sh, style = Stroke(0.8f * u))
        drawPath(estrella(sh, 5f * u, 8, 0.5f), oro.copy(alpha = 0.4f))
        if (oscuro && !p.oscura) {
            val farol = Offset(23f * u, 84f * u)
            penumbraMedievo(farol + Offset(0f, 4f * u), LUZ_FAROL)
            for (k in 0 until 16) {
                val a = azar(7, k) * 6.283f
                val d = (40f + azar(7, k + 20) * 150f) * u
                val o = Offset(farol.x + kotlin.math.cos(a) * d, farol.y + kotlin.math.sin(a) * d * 1.3f)
                if (o.x > 0f && o.y > 0f) drawPath(estrella(o, (2f + azar(7, k + 40) * 2.5f) * u, 8, 0.5f), LUZ_FAROL.copy(alpha = 0.08f + 0.08f * azar(7, k + 60)))
            }
            haloMedievo(farol + Offset(0f, 4f * u), 30f * u, LUZ_FAROL, 0.55f)
            val laton = t(LATON, true, false)
            drawLine(laton, Offset(farol.x, 0f), Offset(farol.x, farol.y - 13f * u), 1f * u)
            drawPath(Path().apply {
                moveTo(farol.x - 7f * u, farol.y - 4f * u); quadraticTo(farol.x, farol.y - 16f * u, farol.x + 7f * u, farol.y - 4f * u); close()
            }, laton)
            drawRect(mezcla(LUZ_FAROL, Color.White, 0.25f), Offset(farol.x - 5.5f * u, farol.y - 4f * u), Size(11f * u, 15f * u))
            listOf(farol.x - 6f * u, farol.x, farol.x + 6f * u).forEach { drawLine(laton, Offset(it, farol.y - 4f * u), Offset(it, farol.y + 11f * u), 1.2f * u) }
            drawLine(laton, Offset(farol.x - 6f * u, farol.y + 3.5f * u), Offset(farol.x + 6f * u, farol.y + 3.5f * u), 0.8f * u)
            drawPath(Path().apply { moveTo(farol.x - 7f * u, farol.y + 11f * u); lineTo(farol.x + 7f * u, farol.y + 11f * u); lineTo(farol.x, farol.y + 18f * u); close() }, laton)
        }
    }

    /** La barra: una franja de yesería clara, con el filete de almagra y el verde al pie y una hilera de estrellitas de oro. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(YESO, p.color, 0.4f).let { if (oscuro) mezcla(it, Color.Black, 0.24f) else it }
        drawRect(base)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, h))
        drawLine(p.detalle.copy(alpha = 0.7f), Offset(0f, h - 4.5f * u), Offset(w, h - 4.5f * u), 1.6f * u)
        drawLine(p.acento.copy(alpha = 0.6f), Offset(0f, h - 1.5f * u), Offset(w, h - 1.5f * u), 0.8f * u)
        val oro = t(ORO, oscuro && !p.oscura, noche)
        var x = 9f * u
        while (x < w) { drawPath(estrella(Offset(x, h - 9.5f * u), 2.2f * u, 8, 0.5f), oro.copy(alpha = 0.55f)); x += 18f * u }
    }

    /** El botón de guardar: un azulejo verde vidriado con su marco de oro y una estrella de ocho puntas en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        val verde = t(VERDE, oscuro && !p.oscura, noche)
        val oro = t(ORO, oscuro && !p.oscura, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1f * u, 1.6f * u), r.size, CornerRadius(3f * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(verde, Color.White, 0.16f), mezcla(verde, Color.Black, 0.2f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(3f * u))
        drawRoundRect(oro, r.topLeft, r.size, CornerRadius(3f * u), style = Stroke(1.2f * u))
        val i = r.deflate(3f * u)
        drawRoundRect(oro.copy(alpha = 0.5f), i.topLeft, i.size, CornerRadius(1.5f * u), style = Stroke(0.6f * u))
        listOf(r.left + 8f * u, r.right - 8f * u).forEach { drawPath(estrella(Offset(it, r.center.y), 3.6f * u, 8, 0.5f), oro) }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFF6E8C2)

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 266.dp

    /** La piel verde del manuscrito, el hueso de la taracea, el blanco del alicatado y la luz del candil. */
    private val PIEL_VERDE = Color(0xFF1E4A3A)
    private val HUESO = Color(0xFFE9DFC6)
    private val AZULEJO = Color(0xFFEDE8DA)
    private val ALFOMBRA = Color(0xFF7E2622)
    private val LUZ_CANDIL = Color(0xFFF6C86A)

    /** El grosor del manuscrito, en dp: de 4 con un folio a 17 con 240 o más. */
    private fun grosorManuscrito(hojas: Int): Float = 4f + 13f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /** Una almendra (mandorla) con centro en [o], [a] de ancho y [b] de alto. */
    private fun almendra(o: Offset, a: Float, b: Float): Path = Path().apply {
        moveTo(o.x, o.y - b / 2)
        quadraticTo(o.x + a * 0.75f, o.y, o.x, o.y + b / 2)
        quadraticTo(o.x - a * 0.75f, o.y, o.x, o.y - b / 2)
        close()
    }

    /**
     * El manuscrito de momentos en su atril plegable (rahle) de taracea, en una alcoba: el muro de yeso con su ajimez (dos
     * arcos de herradura con su parteluz y la celosía), el zócalo de alicatado verde y blanco y la alfombra. El rahle es de
     * cedro con taracea de hueso: la hoja de atrás asoma por encima del libro con su copete lobulado y la de delante baja
     * hasta el suelo con un arco lobulado entre las patas y una estrella de ocho puntas. En la alfombra, el candil de latón,
     * el tintero y el estuche de cálamos. El libro es de piel verde con marco de oro, la almendra central con sus colgantes y
     * la solapa que cierra el canto con su lengüeta; el nombre y la cuenta van en dos cartelas. Engorda con los folios y le
     * asoman más cintas. Al abrirse, primero se aparta la solapa y luego gira la tapa: se ve la hoja con su cabecera
     * iluminada. En oscuro, el candil encendido alumbra la alcoba.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0C0A0A), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorManuscrito(e.hojas) * u
        val hoja = pergamino(oscuro, false)
        val oro = c(ORO)
        // El muro de yeso.
        drawRect(c(YESO))
        // El ajimez: dos arcos de herradura con su parteluz de mármol y la celosía de cedro.
        val aj = Rect(w * 0.04f, h * 0.07f, w * 0.2f, h * 0.46f)
        drawRect(c(YESO_SOMBRA), aj.inflate(5 * u).topLeft, aj.inflate(5 * u).size)
        drawRect(c(ALMAGRA), aj.inflate(5 * u).topLeft, aj.inflate(5 * u).size, style = Stroke(1.2f * u))
        val mitad = aj.width / 2
        listOf(aj.left, aj.left + mitad).forEach { x0 ->
            val arco = arcoHerradura(Rect(x0 + 2 * u, aj.top + 4 * u, x0 + mitad - 2 * u, aj.bottom))
            drawPath(arco, if (oscuro) Color(0xFF16203A) else Color(0xFFDCE6E8))
            clipPath(arco) {
                var d = x0 - aj.height
                while (d < x0 + mitad) {
                    drawLine(c(CEDRO), Offset(d, aj.bottom), Offset(d + aj.height, aj.top), 1.4f * u)
                    drawLine(c(CEDRO), Offset(d, aj.top), Offset(d + aj.height, aj.bottom), 1.4f * u)
                    d += 8 * u
                }
            }
            drawPath(arco, c(CEDRO_OSC), style = Stroke(1.6f * u))
        }
        drawRect(c(MARMOL), Offset(aj.center.x - 2 * u, aj.top + aj.height * 0.3f), Size(4 * u, aj.height * 0.7f))
        // Un friso de yesería: estrellitas entre dos filetes, a lo ancho.
        val friso = h * 0.52f
        drawLine(c(YESO_SOMBRA), Offset(0f, friso - 6 * u), Offset(w, friso - 6 * u), 1 * u)
        drawLine(c(YESO_SOMBRA), Offset(0f, friso + 6 * u), Offset(w, friso + 6 * u), 1 * u)
        var fx = 6 * u
        while (fx < w) { drawPath(estrella(Offset(fx, friso), 4 * u, 8, 0.5f), c(YESO_SOMBRA)); fx += 14 * u }
        // El zócalo de alicatado verde y blanco, en damero girado.
        val z0 = h * 0.58f; val z1 = h * 0.8f
        drawRect(c(AZULEJO), Offset(0f, z0), Size(w, z1 - z0))
        clipRect(0f, z0, w, z1) {
            val l = 10 * u
            var ty = z0; var fila = 0
            while (ty < z1 + l) {
                var tx = if (fila % 2 == 0) 0f else l
                while (tx < w + l) {
                    drawPath(Path().apply { moveTo(tx, ty - l); lineTo(tx + l, ty); lineTo(tx, ty + l); lineTo(tx - l, ty); close() }, c(VERDE))
                    tx += l * 2
                }
                ty += l; fila++
            }
        }
        drawRect(c(AZUL), Offset(0f, z0), Size(w, 2.4f * u))
        drawRect(c(AZUL), Offset(0f, z1 - 2.4f * u), Size(w, 2.4f * u))
        // La alfombra.
        drawRect(c(ALFOMBRA), Offset(0f, z1), Size(w, h - z1))
        drawRect(c(ORO).copy(alpha = 0.6f), Offset(0f, z1 + 5 * u), Size(w, 1.4f * u))
        var ax = 4 * u
        while (ax < w) { drawPath(estrella(Offset(ax, z1 + 12 * u), 3 * u, 8, 0.5f), c(AZUL)); ax += 16 * u }

        // ------------------------------------------------ el candil de latón, a la izquierda, sobre la alfombra
        val candil = Offset(w * 0.12f, h * 0.9f)
        drawOval(c(LATON), Offset(candil.x - 12 * u, candil.y - 6 * u), Size(24 * u, 10 * u))
        drawPath(Path().apply { moveTo(candil.x + 8 * u, candil.y - 4 * u); lineTo(candil.x + 22 * u, candil.y - 9 * u); lineTo(candil.x + 22 * u, candil.y - 6 * u); lineTo(candil.x + 10 * u, candil.y); close() }, c(LATON))
        drawArc(c(LATON), 90f, 180f, false, Offset(candil.x - 18 * u, candil.y - 8 * u), Size(10 * u, 9 * u), style = Stroke(1.6f * u))
        drawOval(c(mezcla(LATON, Color.Black, 0.4f)), Offset(candil.x - 5 * u, candil.y - 6 * u), Size(10 * u, 3 * u))
        val llama = Offset(candil.x + 22 * u, candil.y - 9 * u)
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to LUZ_CANDIL.copy(alpha = 0.32f), 0.35f to LUZ_CANDIL.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.9f))
            haloMedievo(llama - Offset(0f, 4 * u), 24 * u, LUZ_CANDIL, 0.7f)
            llamaMedievo(llama, 10 * u, LUZ_CANDIL)
        }
        // El estuche de cálamos y el tintero de latón, a la derecha.
        val est = Rect(w * 0.8f, h * 0.88f, w * 0.97f, h * 0.88f + 9 * u)
        drawRoundRect(c(NEGRO), est.topLeft, est.size, CornerRadius(4.5f * u))
        drawRoundRect(oro, est.topLeft, est.size, CornerRadius(4.5f * u), style = Stroke(0.8f * u))
        listOf(0.3f, 0.5f, 0.7f).forEach { f -> drawCircle(c(ALMAGRA), 1.4f * u, Offset(est.left + est.width * f, est.center.y)) }
        val tin = Offset(w * 0.86f, h * 0.86f)
        drawRect(c(LATON), Offset(tin.x - 6 * u, tin.y - 12 * u), Size(12 * u, 12 * u))
        drawOval(c(LATON), Offset(tin.x - 7 * u, tin.y - 14 * u), Size(14 * u, 4 * u))
        drawOval(c(NEGRO), Offset(tin.x - 3 * u, tin.y - 13.5f * u), Size(6 * u, 2 * u))
        listOf(-14f, -6f).forEachIndexed { j, ang ->
            rotate(ang, Offset(tin.x, tin.y - 13 * u)) {
                drawLine(c(Color(0xFFC9A86A)), Offset(tin.x, tin.y - 13 * u), Offset(tin.x, tin.y - (40 + 6 * j) * u), 2 * u)
            }
        }

        // ------------------------------------------------ el rahle: la hoja de atrás, con su copete lobulado
        val atras = Rect(w * 0.21f, h * 0.06f, w * 0.79f, h * 0.62f)
        val copete = Path().apply {
            moveTo(atras.left, atras.bottom); lineTo(atras.left, atras.top + 14 * u)
            quadraticTo(atras.left + atras.width * 0.25f, atras.top + 14 * u, atras.left + atras.width * 0.35f, atras.top + 6 * u)
            quadraticTo(atras.center.x, atras.top - 6 * u, atras.right - atras.width * 0.35f, atras.top + 6 * u)
            quadraticTo(atras.right - atras.width * 0.25f, atras.top + 14 * u, atras.right, atras.top + 14 * u)
            lineTo(atras.right, atras.bottom); close()
        }
        drawPath(copete, c(CEDRO_OSC))
        drawPath(copete, c(HUESO).copy(alpha = 0.6f), style = Stroke(1.2f * u))
        drawPath(estrella(Offset(atras.center.x, atras.top + 8 * u), 4 * u, 8, 0.5f), c(HUESO))

        // ------------------------------------------------ el manuscrito, apoyado en el rahle
        val tapa = Rect(w * 0.27f, h * 0.12f + g * 0.5f, w * 0.73f - g, h * 0.63f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + g + 4 * u, tapa.top - g * 0.5f + 4 * u), tapa.size)
        drawRect(c(mezcla(PIEL_VERDE, Color.Black, 0.3f)), Offset(tapa.left + g, tapa.top - g * 0.5f), tapa.size)
        val bloque = Path().apply {
            moveTo(tapa.left + 3 * u, tapa.top); lineTo(tapa.left + 3 * u + g, tapa.top - g * 0.5f + 1.5f * u)
            lineTo(tapa.right + g - 1.5f * u, tapa.top - g * 0.5f + 1.5f * u); lineTo(tapa.right + g - 1.5f * u, tapa.bottom - g * 0.5f - 1 * u)
            lineTo(tapa.right, tapa.bottom - 1 * u); lineTo(tapa.right, tapa.top); close()
        }
        drawPath(bloque, c(YESO))
        val capas = (g / (1.6f * u)).toInt().coerceAtLeast(2)
        for (j in 1..capas) {
            val f = j / (capas + 1f)
            drawLine(c(TINTA).copy(alpha = 0.15f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.bottom - g * f * 0.5f), 0.5f * u)
            drawLine(c(TINTA).copy(alpha = 0.12f), Offset(tapa.left + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), 0.5f * u)
        }
        // Las cintas que asoman por la cabeza: una más cada 50 folios.
        val cintas = (1 + e.hojas / 50).coerceAtMost(5)
        for (j in 0 until cintas) {
            val cx = tapa.left + tapa.width * (0.2f + 0.14f * j) + g * 0.5f
            drawRect(c(listOf(ALMAGRA, ORO, AZUL, VERDE, ORO_CLARO)[j]), Offset(cx, tapa.top - g * 0.5f - (8 + 6 * azar(e.semilla, j)) * u), Size(3.5f * u, (10 + 6 * azar(e.semilla, j)) * u))
        }
        // La hoja de adentro, con su cabecera iluminada.
        if (a > 0.01f) {
            val pag = tapa.deflate(2 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            drawRect(Color.Black.copy(alpha = 0.14f), pag.topLeft, Size(8 * u, pag.height))
            val m = Rect(pag.left + 14 * u, pag.top + 12 * u, pag.right - 12 * u, pag.bottom - 12 * u)
            val cab = Rect(m.left, m.top, m.right, m.top + 20 * u)
            drawRect(c(AZUL), cab.topLeft, cab.size)
            drawRect(oro, cab.deflate(3 * u).topLeft, cab.deflate(3 * u).size)
            drawPath(almendra(cab.center, 28 * u, 12 * u), c(AZUL))
            drawRect(oro, m.topLeft, m.size, style = Stroke(1 * u))
            var ry = cab.bottom + 8 * u; var j = 0
            while (ry < m.bottom - 4 * u) {
                var x = m.right - 4 * u; var q = 0
                while (x > m.left + 4 * u) {
                    val l = minOf((4f + 10f * azar(e.semilla + j, q)) * u, x - m.left - 4 * u)
                    drawLine(c(TINTA).copy(alpha = 0.6f), Offset(x, ry), Offset(x - l, ry), 1.3f * u)
                    if (q % 3 == 1) drawCircle(c(ALMAGRA), 0.9f * u, Offset(x - l / 2, ry - 2.5f * u))
                    x -= l + 2.4f * u; q++
                }
                ry += 6.5f * u; j++
            }
        }
        // La tapa de piel verde (gira cuando ya se apartó la solapa).
        val giro = ((a - 0.3f) / 0.7f).coerceIn(0f, 1f)
        val sx = 1f - 0.94f * giro
        val piel = c(PIEL_VERDE)
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRect(piel, tapa.topLeft, tapa.size)
            bisel(tapa, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.32f), 2 * u)
            val m1 = tapa.deflate(7 * u); val m2 = tapa.deflate(11 * u)
            drawRect(oro.copy(alpha = 0.8f), m1.topLeft, m1.size, style = Stroke(1.4f * u))
            drawRect(oro.copy(alpha = 0.5f), m2.topLeft, m2.size, style = Stroke(0.8f * u))
            // La almendra central con sus colgantes, dorada y con lacería azul dentro.
            val o = Offset(tapa.center.x - tapa.width * 0.04f, tapa.center.y)
            val alto = tapa.height * 0.34f
            drawPath(almendra(o, tapa.width * 0.34f, alto), oro)
            drawPath(almendra(o, tapa.width * 0.26f, alto * 0.78f), c(AZUL))
            drawPath(estrella(o, alto * 0.22f, 8, 0.55f), oro)
            listOf(-1f, 1f).forEach { s ->
                val p0 = Offset(o.x, o.y + s * alto / 2)
                drawLine(oro, p0, p0 + Offset(0f, s * 6 * u), 1.2f * u)
                drawPath(estrella(p0 + Offset(0f, s * 9 * u), 3.5f * u, 8, 0.5f), oro)
            }
            // Las cartelas lobuladas con el nombre (arriba) y la cuenta (abajo).
            fun cartela(r: Rect) {
                val forma = Path().apply {
                    moveTo(r.left, r.center.y)
                    quadraticTo(r.left, r.top, r.left + r.height * 0.6f, r.top); lineTo(r.right - r.height * 0.6f, r.top)
                    quadraticTo(r.right, r.top, r.right, r.center.y); quadraticTo(r.right, r.bottom, r.right - r.height * 0.6f, r.bottom)
                    lineTo(r.left + r.height * 0.6f, r.bottom); quadraticTo(r.left, r.bottom, r.left, r.center.y); close()
                }
                drawPath(forma, hoja)
                drawPath(forma, oro, style = Stroke(1.2f * u))
            }
            val c1 = Rect(m2.left + 2 * u, m2.top + 5 * u, m2.right - 2 * u, m2.top + 5 * u + tapa.height * 0.2f)
            cartela(c1)
            textoEn(medidor, e.nombre, Rect(c1.left + 7 * u, c1.top + 3 * u, c1.right - 7 * u, c1.bottom - 3 * u), letra, c(TINTA), 13.5f, peso = FontWeight.Bold, minimo = 7f)
            val c2 = Rect(m2.left + 10 * u, m2.bottom - 5 * u - tapa.height * 0.13f, m2.right - 10 * u, m2.bottom - 5 * u)
            cartela(c2)
            textoEn(medidor, e.cuenta, c2.deflate(4 * u), letra, c(TINTA).copy(alpha = 0.85f), 10.5f, minimo = 6f)
            if (giro > 0f) drawRect(Color.Black.copy(alpha = 0.45f * giro), tapa.topLeft, tapa.size)
        }
        if (giro > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (j in 0 until 3) drawRect(Color.Black.copy(alpha = 0.1f * (3 - j) / 3f), Offset(borde, tapa.top), Size((3 + 4 * j) * u, tapa.height))
        }
        // La solapa: cubre el canto y dobla su lengüeta sobre la tapa; al abrir se aparta hacia la derecha.
        val fuera = (a / 0.3f).coerceIn(0f, 1f)
        if (fuera < 1f) {
            val dx = fuera * (g + 26 * u)
            val canto = Rect(tapa.right + dx, tapa.top - g * 0.5f, tapa.right + g + dx, tapa.bottom - g * 0.5f)
            drawRect(mezcla(piel, Color.Black, 0.15f), canto.topLeft, canto.size)
            val leng = Path().apply {
                moveTo(tapa.right + dx, tapa.top + tapa.height * 0.3f); lineTo(tapa.right - tapa.width * 0.16f + dx * 1.6f, tapa.center.y)
                lineTo(tapa.right + dx, tapa.top + tapa.height * 0.7f); close()
            }
            drawPath(leng, Color.Black.copy(alpha = 0.3f), style = Stroke(3 * u))
            drawPath(leng, mezcla(piel, Color.White, 0.05f))
            drawPath(leng, oro.copy(alpha = 0.7f), style = Stroke(0.9f * u))
            drawPath(estrella(Offset(tapa.right - tapa.width * 0.1f + dx * 1.4f, tapa.center.y), 3 * u, 8, 0.5f), oro)
        }
        drawRoundRect(mezcla(piel, Color.Black, 0.25f), Offset(tapa.left - 6 * u, tapa.top - 1 * u), Size(7 * u, tapa.height + 1 * u), CornerRadius(3 * u))

        // ------------------------------------------------ el rahle: la hoja de delante, con la taracea
        val frente = Path().apply {
            moveTo(w * 0.3f, h * 0.6f); lineTo(w * 0.7f, h * 0.6f); lineTo(w * 0.8f, h * 0.97f); lineTo(w * 0.62f, h * 0.97f)
            cubicTo(w * 0.62f, h * 0.84f, w * 0.38f, h * 0.84f, w * 0.38f, h * 0.97f)
            lineTo(w * 0.2f, h * 0.97f); close()
        }
        drawPath(frente, c(CEDRO))
        clipPath(frente) { vetas(Rect(w * 0.2f, h * 0.6f, w * 0.8f, h), c(VETA).copy(alpha = 0.4f), 9, densidad = 0.6f, vertical = true, grosor = 0.6f) }
        drawPath(frente, c(HUESO), style = Stroke(1.4f * u))
        drawLine(c(CEDRO_LUZ), Offset(w * 0.3f, h * 0.6f), Offset(w * 0.7f, h * 0.6f), 3 * u)
        // La taracea: un zigzag de hueso y la estrella de ocho puntas en cada pata.
        laceria(w * 0.31f, w * 0.69f, h * 0.64f, h * 0.69f, 12 * u, c(HUESO), c(VERDE), 1 * u)
        listOf(w * 0.3f, w * 0.7f).forEach { x ->
            drawPath(estrella(Offset(x, h * 0.82f), 9 * u, 8, 0.55f), c(HUESO))
            drawPath(estrella(Offset(x, h * 0.82f), 5 * u, 8, 0.55f), c(VERDE))
        }
        drawPath(estrella(Offset(w * 0.5f, h * 0.76f), 5 * u, 8, 0.5f), oro)
    }

    /**
     * La pestaña de un día: la lengüeta de una solapa de piel verde (punta abajo), con filete de oro, la letra de la semana
     * arriba, el día en dorado y, en la punta, una estrella de ocho puntas del color del ánimo (sin ánimo, solo su contorno).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val piel = c(if (azar(e.semilla, 3) > 0.5f) PIEL_VERDE else PIELES[(e.semilla and 0x7fffffff) % PIELES.size])
        val forma = Path().apply {
            moveTo(3 * u, 2 * u); lineTo(w - 3 * u, 2 * u); lineTo(w - 3 * u, h - 20 * u); lineTo(w / 2, h - 2 * u); lineTo(3 * u, h - 20 * u); close()
        }
        drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(2.4f * u))
        drawPath(forma, piel)
        val filo = Path().apply {
            moveTo(6 * u, 5 * u); lineTo(w - 6 * u, 5 * u); lineTo(w - 6 * u, h - 21.5f * u); lineTo(w / 2, h - 6 * u); lineTo(6 * u, h - 21.5f * u); close()
        }
        drawPath(filo, c(ORO).copy(alpha = 0.8f), style = Stroke(0.8f * u))
        val claro = mezcla(c(ORO_CLARO), ORO_CLARO, 0.5f)
        textoEn(medidor, e.semana, Rect(6 * u, 6 * u, w - 6 * u, 15 * u), letra, claro, 9f, peso = FontWeight.Bold)
        textoEn(medidor, e.dia, Rect(6 * u, 14 * u, w - 6 * u, 34 * u), letra, claro, 18f, peso = FontWeight.Bold, minimo = 10f)
        val o = Offset(w / 2, h - 15 * u)
        if (e.tono != null) {
            drawPath(estrella(o, 7 * u, 8, 0.55f), e.tono)
            drawPath(estrella(o, 7 * u, 8, 0.55f), claro, style = Stroke(0.8f * u))
        } else {
            drawPath(estrella(o, 5 * u, 8, 0.55f), claro.copy(alpha = 0.7f), style = Stroke(0.8f * u))
        }
    }

    /**
     * El rótulo de un mes: una banda azul con una cinta de lacería de oro arriba y abajo y, en medio, la cartela de yeso de
     * puntas lobuladas con el mes; a los lados, dos estrellas de ocho puntas.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(AZUL), Offset(0f, 2 * u), Size(w, h - 4 * u))
        laceria(0f, w, 3 * u, 8 * u, 10 * u, c(ORO), c(ORO_CLARO), 0.8f * u)
        laceria(0f, w, h - 8 * u, h - 3 * u, 10 * u, c(ORO), c(ORO_CLARO), 0.8f * u)
        val r = Rect(w * 0.08f, 9 * u, w * 0.92f, h - 9 * u)
        val forma = Path().apply {
            moveTo(r.left, r.center.y)
            quadraticTo(r.left, r.top, r.left + 10 * u, r.top); lineTo(r.right - 10 * u, r.top)
            quadraticTo(r.right, r.top, r.right, r.center.y); quadraticTo(r.right, r.bottom, r.right - 10 * u, r.bottom)
            lineTo(r.left + 10 * u, r.bottom); quadraticTo(r.left, r.bottom, r.left, r.center.y); close()
        }
        drawPath(forma, pergamino(oscuro, false))
        drawPath(forma, c(ORO), style = Stroke(1 * u))
        listOf(w * 0.04f, w * 0.96f).forEach { x -> drawPath(estrella(Offset(x, h / 2), 6 * u, 8, 0.5f), c(ORO_CLARO)) }
        textoEn(medidor, texto, Rect(r.left + 12 * u, r.top, r.right - 12 * u, r.bottom), letra, c(TINTA), 14f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El papel de Xàtiva, el cuero del forro y el verde del lacre de la estrella. */
    private val PAPEL_XATIVA = Color(0xFFF3E7CC)
    private val FORRO = Color(0xFF7A3B1E)
    private val LACRE_VERDE = Color(0xFF1E6B5C)

    /** La estrella de ocho puntas de dos cuadrados cruzados (el jatam), de radio [r] en [o]. */
    private fun jatam(o: Offset, r: Float): Path = Path().apply {
        for (g in 0 until 2) {
            for (j in 0..4) {
                val p = polarMedievo(o, r, g * 45f + j * 90f + 45f)
                if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
    }

    /** El sello de la estrella en [o], de radio [r]: el jatam de cera verde con su filo de oro, el círculo y la roseta. */
    private fun DrawScope.selloEstrella(o: Offset, r: Float, oscuro: Boolean) {
        val cera = if (oscuro) mezcla(LACRE_VERDE, Color.Black, 0.2f) else LACRE_VERDE
        val oro = if (oscuro) mezcla(ORO_CLARO, Color.Black, 0.2f) else ORO_CLARO
        translate(r * 0.07f, r * 0.12f) { drawPath(jatam(o, r), Color.Black.copy(alpha = 0.32f)) }
        drawPath(jatam(o, r), cera)
        drawPath(jatam(o, r), oro, style = Stroke(r * 0.08f))
        drawCircle(mezcla(cera, Color.Black, 0.3f), r * 0.5f, o)
        drawCircle(oro, r * 0.44f, o, style = Stroke(r * 0.06f))
        for (j in 0 until 8) drawCircle(oro, r * 0.075f, polarMedievo(o, r * 0.25f, j * 45f))
        drawCircle(oro, r * 0.08f, o)
        drawLine(Color.White.copy(alpha = 0.3f), polarMedievo(o, r * 0.8f, 200f), polarMedievo(o, r * 0.8f, 250f), r * 0.06f, StrokeCap.Round)
    }

    /** Una cartela lobulada en [r]: el rectángulo con un medio círculo en cada punta. */
    private fun cartelaLobulada(r: Rect): Path = Path().apply {
        val rad = r.height / 2
        moveTo(r.left + rad, r.top); lineTo(r.right - rad, r.top)
        arcTo(Rect(r.right - rad * 2, r.top, r.right, r.bottom), -90f, 180f, false)
        lineTo(r.left + rad, r.bottom)
        arcTo(Rect(r.left, r.top, r.left + rad * 2, r.bottom), 90f, 180f, false)
        close()
    }

    /**
     * La carta va en un sobre de papel de Xàtiva con dos filetes, de oro y de almagra, y la solapa escalonada (como las
     * almenas de un adarve) con su lacería de oro, forrada de cuero por dentro. La cierra el sello de la estrella: el jatam
     * de cera verde con su filo de oro y la roseta; las selladas al futuro van además ceñidas por el doble cordón de oro, con
     * su borla. A quién va y las fechas, en una cartela lobulada con su filete azul. Al abrirse la estrella se parte, la
     * solapa se levanta enseñando el cuero y sale la carta. En oscuro el papel se hunde; la cartela se lee.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF100C0A), 0.6f) else x
        val p = Rect(c.left + W * 0.05f, c.top + H * 0.08f, c.right - W * 0.05f, c.bottom - H * 0.07f)
        val cx = p.center.x
        val largo = p.height * 0.4f
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val punta = Offset(cx, p.top + largo)
        val rs = minOf(W, H) * 0.11f
        val partido = e.romper.coerceIn(0f, 1f)
        // La solapa escalonada: cuatro escalones de cada lado hasta la meseta del medio, donde va la estrella.
        fun solapa(g: Float) = Path().apply {
            moveTo(p.left, p.top)
            val n = 4
            for (i in 0 until n) {
                val y = p.top + g * largo * (0.4f + 0.6f * i / (n - 1))
                lineTo(p.left + p.width * 0.44f * i / n, y); lineTo(p.left + p.width * 0.44f * (i + 1) / n, y)
            }
            for (i in n - 1 downTo 0) {
                val y = p.top + g * largo * (0.4f + 0.6f * i / (n - 1))
                lineTo(p.right - p.width * 0.44f * (i + 1) / n, y); lineTo(p.right - p.width * 0.44f * i / n, y)
            }
            lineTo(p.right, p.top); close()
        }
        fun DrawScope.selloEn(g: Float, mitad: Int) {
            val o = Offset(cx, p.top + g * largo)
            conAlfaMedievo(e.alfaSello.coerceIn(0f, 1f), Rect(punta, rs * 4f * e.escalaSello)) {
                scale(e.escalaSello, punta) {
                    if (mitad == 0 && e.brillo > 0f) {
                        drawCircle(Brush.radialGradient(0f to ORO_CLARO.copy(alpha = 0.8f * e.brillo), 0.45f to LUZ_FAROL.copy(alpha = 0.28f * e.brillo),
                            1f to Color.Transparent, center = o, radius = rs * 2.8f), rs * 2.8f, o)
                        rayosMedievo(o, rs * 1.25f, rs * 2.3f, ORO.copy(alpha = 0.9f * e.brillo), 16, 1.1f)
                    }
                    when {
                        partido <= 0f -> selloEstrella(o, rs, oscuro)
                        mitad == 1 -> clipPath(mitadSelloMedievo(o, rs, true, e.semilla)) {
                            translate(0f, -partido * rs * 0.18f) { rotate(-8f * partido, o) { selloEstrella(o, rs, oscuro) } }
                        }
                        else -> clipPath(mitadSelloMedievo(o, rs, false, e.semilla)) {
                            translate(0f, partido * rs * 0.1f) { rotate(6f * partido, o) { selloEstrella(o, rs, oscuro) } }
                        }
                    }
                }
            }
        }

        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(p.left + 2 * u, p.top + 3 * u), p.size, CornerRadius(3 * u))
        if (giro < 0f) {
            drawPath(solapa(giro), k(FORRO))
            drawPath(solapa(giro), k(ORO).copy(alpha = 0.7f), style = Stroke(0.9f * u))
            if (partido > 0f) selloEn(giro, 1)
        }
        val sale = salidaMedievo(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaMedievo(Rect(cx - p.width * 0.42f, p.top + 3 * u - sube, cx + p.width * 0.42f, p.bottom - 6 * u - sube), e, oscuro)
        }
        // El sobre de papel de Xàtiva, con su verjurado y los dos filetes.
        drawRoundRect(k(PAPEL_XATIVA), p.topLeft, p.size, CornerRadius(3 * u))
        clipRect(p.left, p.top, p.right, p.bottom) {
            var x = p.left + 6 * u
            while (x < p.right) { drawLine(k(Color(0xFFC9B48A)).copy(alpha = 0.3f), Offset(x, p.top), Offset(x, p.bottom), 0.6f * u); x += 14 * u }
        }
        drawRoundRect(k(ORO), p.topLeft + Offset(3 * u, 3 * u), Size(p.width - 6 * u, p.height - 6 * u), CornerRadius(2 * u), style = Stroke(1 * u))
        drawRoundRect(k(ALMAGRA).copy(alpha = 0.8f), p.topLeft + Offset(5 * u, 5 * u), Size(p.width - 10 * u, p.height - 10 * u), CornerRadius(1.5f * u), style = Stroke(0.7f * u))
        // La cartela lobulada con lo escrito.
        val car = Rect(p.left + p.width * 0.07f, punta.y + rs * 1.3f, p.right - p.width * 0.07f, p.bottom - 8 * u)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            val forma = cartelaLobulada(car)
            drawPath(forma, if (oscuro) mezcla(Color(0xFFFBF3DE), Color(0xFF100C0A), 0.25f) else Color(0xFFFBF3DE))
            drawPath(forma, k(AZUL), style = Stroke(1.2f * u))
            drawPath(cartelaLobulada(car.inflate(2.2f * u)), k(ORO), style = Stroke(0.8f * u))
            val m = car.top + car.height * 0.55f
            textoEn(medidor, e.linea1, Rect(car.left + car.height * 0.28f, car.top + 1.5f * u, car.right - car.height * 0.28f, m), letra, TINTA, 12f, peso = FontWeight.SemiBold, minimo = 7.5f)
            textoEn(medidor, e.linea2, Rect(car.left + car.height * 0.28f, m - 1 * u, car.right - car.height * 0.28f, car.bottom - 1.5f * u), letra, mezcla(VERDE, Color.Black, 0.2f), 9f, minimo = 7f)
        }
        if (giro >= 0f) {
            translate(0f, 2 * u) { drawPath(solapa(giro), Color.Black.copy(alpha = 0.22f)) }
            drawPath(solapa(giro), k(Color(0xFFEFDFB8)))
            if (giro > 0.25f) clipPath(solapa(giro)) {
                // La lacería de oro que sigue los escalones, y una estrellita en cada escalón.
                laceria(p.left, p.right, p.top + 4 * u, p.top + 4 * u + largo * giro * 0.2f, 12 * u, k(ORO), k(VERDE).copy(alpha = 0.7f), 0.9f * u)
                for (i in 0 until 3) listOf(-1f, 1f).forEach { s ->
                    val x = cx + s * p.width * (0.44f - 0.11f * i - 0.055f)
                    drawPath(estrella(Offset(x, p.top + largo * giro * (0.4f + 0.2f * (3 - i)) - 5 * u), 3.4f * u, 8, 0.55f), k(ORO).copy(alpha = 0.9f))
                }
            }
            drawPath(solapa(giro), k(ALMAGRA).copy(alpha = 0.6f), style = Stroke(2.2f * u))
            drawPath(solapa(giro), k(ORO), style = Stroke(1.1f * u))
        }
        // El doble cordón de oro, con su borla.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val tramo = (cx - p.left + 3 * u) * ata
            listOf(-0.32f, 0.32f).forEach { dy ->
                val y = punta.y + rs * dy
                listOf(p.left - 3 * u to p.left - 3 * u + tramo, p.right + 3 * u - tramo to p.right + 3 * u).forEach { (a, b) ->
                    drawLine(Color.Black.copy(alpha = 0.3f), Offset(a, y + 1 * u), Offset(b, y + 1 * u), 1.8f * u)
                    drawLine(k(Color(0xFFD9B25B)), Offset(a, y), Offset(b, y), 1.8f * u)
                }
            }
            if (ata > 0.85f) {
                val f = (ata - 0.85f) / 0.15f
                val o = Offset(p.right + 1 * u, punta.y)
                drawLine(k(Color(0xFFD9B25B)), o, o + Offset(0f, rs * 0.9f * f), 1.4f * u)
                drawPath(Path().apply { moveTo(o.x - 1.5f * u, o.y + rs * 0.9f * f); lineTo(o.x + 1.5f * u, o.y + rs * 0.9f * f); lineTo(o.x + 3 * u, o.y + rs * 1.7f * f); lineTo(o.x - 3 * u, o.y + rs * 1.7f * f); close() }, k(Color(0xFFD9B25B)))
            }
        }
        if (partido <= 0f) selloEn(maxOf(giro, 0f), 0)
        else {
            selloEn(1f, 0)
            if (giro >= 0f) selloEn(giro, 1)
        }
    }

    override val cajaPrevia = Rect(0.165f, 0.08f, 0.835f, 0.25f)

    override val altoBuzon: Dp = 206.dp

    /**
     * El buzón son las tacas del zaguán: en el muro de yesería con su red de rombos, bajo una banda azul con lazo de oro
     * (formas, no letras), una hilera de ocho nichos de herradura con su alfiz, y en cada uno una carta apoyada: las que ya
     * se pueden abrir con la estrella verde encendida, las selladas con el doble cordón de oro, las abiertas desplegadas con
     * sus renglones. Abajo, el zócalo de alicatado de estrellas de ocho puntas con tres placas de mármol de borde azul y la
     * cuenta. En oscuro, la luz de un farol que no se ve tiñe el muro y siembra estrellas de luz.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF100C0A), 0.58f) else x
        // La yesería, con su red de rombos en relieve.
        drawRect(k(YESO))
        val paso = 14 * u
        var x = -h
        while (x < w + h) {
            drawLine(k(YESO_SOMBRA).copy(alpha = 0.5f), Offset(x, 0f), Offset(x + h, h), 0.8f * u)
            drawLine(k(YESO_SOMBRA).copy(alpha = 0.5f), Offset(x + h, 0f), Offset(x, h), 0.8f * u)
            x += paso
        }
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to LUZ_FAROL.copy(alpha = 0.3f), 0.45f to LUZ_FAROL.copy(alpha = 0.1f), 1f to Color.Transparent,
                center = Offset(w * 0.5f, -h * 0.1f), radius = w * 0.8f))
            estrellasFarol(Rect(0f, 0f, w, h * 0.7f), 26, 7)
        }
        // La banda azul con su lazo de oro, entre filetes de almagra.
        val banda = Rect(0f, h * 0.04f, w, h * 0.15f)
        drawRect(k(AZUL), banda.topLeft, banda.size)
        laceria(0f, w, banda.top + 4 * u, banda.bottom - 4 * u, 18 * u, k(ORO), k(ORO_CLARO), 1.4f * u)
        drawRect(k(ALMAGRA), Offset(0f, banda.top - 2 * u), Size(w, 2 * u))
        drawRect(k(ALMAGRA), Offset(0f, banda.bottom), Size(w, 2 * u))
        // Las ocho tacas.
        val n = 8; val fila = Rect(w * 0.03f, h * 0.2f, w * 0.97f, h * 0.63f)
        val ancho = fila.width / n
        val cartas = repartoMedievo(e, n)
        val inicio = (n - cartas.size) / 2
        for (j in 0 until n) {
            val alfiz = Rect(fila.left + ancho * j + 2.5f * u, fila.top, fila.left + ancho * (j + 1) - 2.5f * u, fila.bottom)
            drawRect(k(YESO_SOMBRA), alfiz.topLeft, alfiz.size)
            drawRect(k(ALMAGRA).copy(alpha = 0.7f), alfiz.topLeft, alfiz.size, style = Stroke(0.8f * u))
            val vano = Rect(alfiz.left + 4 * u, alfiz.top + 5 * u, alfiz.right - 4 * u, alfiz.bottom)
            val arco = arcoHerradura(vano)
            drawPath(arco, k(Color(0xFF3A2A20)))
            // Las enjutas con una estrellita.
            listOf(alfiz.left + 3.5f * u, alfiz.right - 3.5f * u).forEach { drawPath(estrella(Offset(it, alfiz.top + 4 * u), 2.2f * u, 8, 0.55f), k(ORO)) }
            val i = j - inicio
            if (i in cartas.indices) clipPath(arco) {
                val clase = cartas[i]
                val s = Rect(vano.left + vano.width * 0.12f, vano.top + vano.height * 0.42f, vano.right - vano.width * 0.12f, vano.bottom - 1 * u)
                rotate((azar(j, 31) - 0.5f) * 8f, Offset(s.center.x, s.bottom)) {
                    drawRect(Color.Black.copy(alpha = 0.3f), s.topLeft + Offset(2 * u, 0f), s.size)
                    if (clase == 2) {
                        // Abierta: desplegada, con sus renglones y la solapa de cuero arriba.
                        val hoja = Rect(s.left, s.top - vano.height * 0.14f, s.right, s.bottom)
                        drawPath(Path().apply { moveTo(hoja.left, hoja.top); lineTo(hoja.center.x, hoja.top - 6 * u); lineTo(hoja.right, hoja.top); close() }, k(FORRO))
                        drawRect(k(Color(0xFFFBF3DE)), hoja.topLeft, hoja.size)
                        for (r in 0 until 6) drawLine(k(TINTA).copy(alpha = 0.45f), Offset(hoja.left + 3 * u, hoja.top + 5 * u + r * 3.4f * u), Offset(hoja.right - 3 * u - (r % 3) * 2 * u, hoja.top + 5 * u + r * 3.4f * u), 0.6f * u)
                    } else {
                        drawRect(k(PAPEL_XATIVA), s.topLeft, s.size)
                        drawRect(k(ORO), s.topLeft + Offset(1.5f * u, 1.5f * u), Size(s.width - 3 * u, s.height - 3 * u), style = Stroke(0.6f * u))
                        val o = Offset(s.center.x, s.top + s.height * 0.4f)
                        drawPath(Path().apply { moveTo(s.left, s.top); lineTo(s.left, s.top + s.height * 0.22f); lineTo(o.x, o.y); lineTo(s.right, s.top + s.height * 0.22f); lineTo(s.right, s.top); close() }, k(Color(0xFFF8EFD8)))
                        drawPath(Path().apply { moveTo(s.left, s.top + s.height * 0.22f); lineTo(o.x, o.y); lineTo(s.right, s.top + s.height * 0.22f) }, k(ORO), style = Stroke(0.6f * u))
                        if (clase == 1) listOf(-2f, 2f).forEach { dy -> drawLine(k(Color(0xFFD9B25B)), Offset(s.left, o.y + dy * u), Offset(s.right, o.y + dy * u), 1.2f * u) }
                        else {
                            haloMedievo(o, s.width * 0.95f, ORO_CLARO, if (oscuro) 1f else 0.8f)
                            rayosMedievo(o, s.width * 0.3f, s.width * 0.62f, ORO, 8, 0.9f)
                        }
                        drawPath(jatam(o, s.width * 0.2f), k(LACRE_VERDE))
                        drawPath(jatam(o, s.width * 0.2f), k(ORO_CLARO), style = Stroke(0.6f * u))
                    }
                }
            }
            sombraArco(arco, u)
        }
        // El zócalo de alicatado.
        val zocalo = Rect(0f, h * 0.67f, w, h * 0.94f)
        drawRect(k(MARMOL), zocalo.topLeft, zocalo.size)
        clipRect(zocalo.left, zocalo.top, zocalo.right, zocalo.bottom) {
            val t = 17 * u
            var ty = zocalo.top; var fi = 0
            while (ty < zocalo.bottom) {
                var tx = if (fi % 2 == 0) 0f else t / 2
                var ci = 0
                while (tx < w + t) {
                    val col = listOf(VERDE, AZUL, NEGRO, ALMAGRA)[(ci + fi) % 4]
                    drawPath(estrella(Offset(tx, ty + t / 2), t * 0.42f, 8, 0.62f), k(col))
                    drawCircle(k(MARMOL), t * 0.1f, Offset(tx, ty + t / 2))
                    tx += t; ci++
                }
                ty += t; fi++
            }
        }
        drawRect(k(ALMAGRA), Offset(0f, zocalo.top - 2.5f * u), Size(w, 2.5f * u))
        drawRect(k(BARRO), Offset(0f, zocalo.bottom), Size(w, h - zocalo.bottom))
        // Las tres placas de mármol con la cuenta.
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "selladas", e.abiertas to "abiertas")
        rotulos.forEachIndexed { i, (cuenta, nombre) ->
            val xc = w * (i * 2 + 1) / 6f
            val r = Rect(xc - w * 0.135f, zocalo.top + 5 * u, xc + w * 0.135f, zocalo.bottom - 5 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1.5f * u, 2 * u), r.size, CornerRadius(3 * u))
            drawRoundRect(if (oscuro) mezcla(MARMOL, Color(0xFF100C0A), 0.25f) else MARMOL, r.topLeft, r.size, CornerRadius(3 * u))
            drawRoundRect(AZUL, r.topLeft, r.size, CornerRadius(3 * u), style = Stroke(2.2f * u))
            drawRoundRect(ORO, r.topLeft + Offset(3 * u, 3 * u), Size(r.width - 6 * u, r.height - 6 * u), CornerRadius(1.5f * u), style = Stroke(0.6f * u))
            val m = r.left + r.width * 0.36f
            textoEn(medidor, "$cuenta", Rect(r.left + 4 * u, r.top, m, r.bottom), letra, if (i == 0 && cuenta > 0) VERDE else TINTA, 18f, peso = FontWeight.Bold)
            textoEn(medidor, nombre, Rect(m, r.top + 2 * u, r.right - 5 * u, r.bottom - 2 * u), letra, TINTA, 10f, minimo = 7f)
        }
    }

    /** La sombra que echa el arco de una taca sobre su fondo: más honda arriba. */
    private fun DrawScope.sombraArco(arco: Path, u: Float) {
        clipPath(arco) { translate(-2 * u, -3 * u) { drawPath(arco, Color.Black.copy(alpha = 0.25f), style = Stroke(5 * u)) } }
    }

    // ---------------------------------------------------------------- lo de alrededor

    /** El morado de manganeso de los perfiles de la loza verde y morada. */
    private val MANGANESO = Color(0xFF3A2430)
    /** Las maderas de las tapas de taracea: cedro, ébano y nogal. */
    private val MADERAS_TARACEA = listOf(Color(0xFF7A4A2A), Color(0xFF2E221C), Color(0xFF5A3A22))

    /** Lo claro de lo de alrededor (el hueso, el mármol): en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun claroHondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C0A), 0.74f) else c
    /** La tinta sobre [claroHondo]. */
    private fun tintaHonda(oscuro: Boolean): Color = if (oscuro) HUESO else TINTA

    /** Un medallón de dieciséis lóbulos de radio [r] en [o]. */
    private fun DrawScope.medallon(o: Offset, r: Float, color: Color) {
        drawCircle(color, r * 0.9f, o)
        for (j in 0 until 16) drawCircle(color, r * 0.17f, polarMedievo(o, r * 0.86f, j * 22.5f))
    }

    /**
     * El emblema de la pestaña [k] en [o], de radio [r], pintado con el verde y perfilado de manganeso: el libro con su
     * solapa (diario), la tablilla de escribir con su asa (momentos), la media luna con su estrella (sueños), el candil con
     * su llama (ideario) y la carta doblada con su estrella de sello (cartas).
     */
    private fun DrawScope.emblema(k: Int, o: Offset, r: Float, verde: Color, perfil: Color) {
        val u = density
        val t1 = 0.9f * u
        when (k) {
            0 -> {
                val p = Path().apply {
                    moveTo(o.x - r * 0.7f, o.y - r); lineTo(o.x + r * 0.5f, o.y - r); lineTo(o.x + r * 0.5f, o.y + r); lineTo(o.x - r * 0.7f, o.y + r); close()
                }
                drawPath(p, verde); drawPath(p, perfil, style = Stroke(t1))
                val solapa = Path().apply { moveTo(o.x + r * 0.5f, o.y - r * 0.6f); lineTo(o.x + r * 0.95f, o.y); lineTo(o.x + r * 0.5f, o.y + r * 0.6f) }
                drawPath(solapa, perfil, style = Stroke(t1))
                drawPath(estrella(Offset(o.x - r * 0.1f, o.y), r * 0.4f, 8, 0.6f), perfil)
            }
            1 -> {
                drawRoundRect(verde, Offset(o.x - r * 0.65f, o.y - r * 0.7f), Size(r * 1.3f, r * 1.7f), CornerRadius(r * 0.2f))
                drawRoundRect(perfil, Offset(o.x - r * 0.65f, o.y - r * 0.7f), Size(r * 1.3f, r * 1.7f), CornerRadius(r * 0.2f), style = Stroke(t1))
                drawArc(perfil, 180f, 180f, false, Offset(o.x - r * 0.3f, o.y - r * 1.05f), Size(r * 0.6f, r * 0.7f), style = Stroke(t1))
                for (j in 0 until 3) drawLine(perfil, Offset(o.x - r * 0.4f, o.y - r * 0.25f + j * r * 0.35f), Offset(o.x + r * 0.4f, o.y - r * 0.25f + j * r * 0.35f), t1 * 0.8f)
            }
            2 -> {
                val luna = mediaLunaMedievo(Offset(o.x - r * 0.15f, o.y + r * 0.05f), r * 0.92f)
                drawPath(luna, verde); drawPath(luna, perfil, style = Stroke(t1))
                drawPath(estrella(Offset(o.x + r * 0.75f, o.y - r * 0.6f), r * 0.4f, 8, 0.55f), perfil)
            }
            3 -> {
                val p = Path().apply {
                    moveTo(o.x - r, o.y + r * 0.2f); quadraticTo(o.x - r * 0.2f, o.y + r * 0.9f, o.x + r * 0.5f, o.y + r * 0.35f)
                    lineTo(o.x + r, o.y); lineTo(o.x + r * 0.4f, o.y + r * 0.05f); quadraticTo(o.x - r * 0.3f, o.y - r * 0.2f, o.x - r, o.y + r * 0.2f); close()
                }
                drawPath(p, verde); drawPath(p, perfil, style = Stroke(t1))
                llamaMedievo(Offset(o.x + r * 0.95f, o.y - r * 0.05f), r * 0.9f, perfil)
                drawLine(perfil, Offset(o.x - r * 0.2f, o.y + r * 0.62f), Offset(o.x - r * 0.2f, o.y + r * 0.95f), t1)
            }
            else -> {
                drawRect(verde, Offset(o.x - r, o.y - r * 0.62f), Size(r * 2f, r * 1.24f))
                drawRect(perfil, Offset(o.x - r, o.y - r * 0.62f), Size(r * 2f, r * 1.24f), style = Stroke(t1))
                drawLine(perfil, Offset(o.x - r, o.y - r * 0.62f), Offset(o.x, o.y + r * 0.1f), t1)
                drawLine(perfil, Offset(o.x + r, o.y - r * 0.62f), Offset(o.x, o.y + r * 0.1f), t1)
                drawPath(jatam(Offset(o.x, o.y + r * 0.15f), r * 0.38f), perfil)
            }
        }
    }

    override val altoPestanas: Dp = 78.dp

    /**
     * Las pestañas: una cenefa de cinco azulejos de loza verde y morada sobre el yeso del muro, cada uno con su orla verde
     * perfilada de manganeso, el emblema y el nombre en letra de friso; la cuenta en un ojo verde en lo alto. El elegido se
     * alza con su sombra y lleva el reflejo dorado de la loza de lustre en la orla y el nombre en almagra; los demás quedan
     * asentados en el muro.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF100C0A), 0.62f) else x
        // La llaga de yeso donde asientan los azulejos.
        drawRect(c(YESO_SOMBRA), Offset(0f, h - 4 * u), Size(w, 4 * u))
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(0f, h - 4 * u), Size(w, 1 * u))
        val perfil = if (oscuro) mezcla(MANGANESO, HUESO, 0.55f) else MANGANESO
        for (k in 0 until 5) {
            val q = quintoMedievo(k, w, h)
            val sel = k == e.actual
            val alza = if (sel) 0f else 6 * u
            val r = Rect(q.left + 2.5f * u, 2 * u + alza, q.right - 2.5f * u, h - 5 * u + (if (sel) -1 * u else 0f))
            drawRect(Color.Black.copy(alpha = if (sel) 0.32f else 0.16f), Offset(r.left + (if (sel) 2.5f else 1f) * u, r.top + (if (sel) 3f else 1f) * u), r.size)
            // El elegido, en modo oscuro, se queda claro y con su perfil de manganeso, para que destaque.
            val pf = if (sel) MANGANESO else perfil
            val vidriado = if (sel) (if (oscuro) mezcla(AZULEJO, Color(0xFF100C0A), 0.25f) else mezcla(AZULEJO, Color.White, 0.3f)) else c(AZULEJO)
            drawRect(vidriado, r.topLeft, r.size)
            // La orla verde perfilada de manganeso (en el elegido, con el reflejo dorado de la loza de lustre).
            val orla = r.deflate(2.5f * u)
            val verde = c(VERDE)
            drawRect(if (sel) c(ORO) else verde, orla.topLeft, orla.size, style = Stroke(2.4f * u))
            drawRect(pf, orla.inflate(1.2f * u).topLeft, orla.inflate(1.2f * u).size, style = Stroke(0.7f * u))
            drawRect(pf, orla.deflate(1.2f * u).topLeft, orla.deflate(1.2f * u).size, style = Stroke(0.7f * u))
            // El brillo del vidriado.
            drawRect(Color.White.copy(alpha = if (sel) 0.45f else 0.25f), Offset(r.left + 4 * u, r.top + 4 * u), Size(r.width * 0.3f, 1.2f * u))
            emblema(k, Offset(r.center.x, r.top + 15 * u), 6.5f * u, if (sel) mezcla(verde, c(ORO), 0.35f) else verde, pf)
            textoEn(medidor, e.nombres.getOrElse(k) { "" }.uppercase(), Rect(orla.left + 2 * u, r.top + 26 * u, orla.right - 2 * u, r.top + 40 * u), letra,
                if (sel) ALMAGRA else perfil, 10f, peso = FontWeight.Bold, minimo = 6f)
            // La cuenta, en un ojo verde abajo.
            val ojo = Rect(r.center.x - 11 * u, r.top + 41 * u, r.center.x + 11 * u, minOf(r.top + 52 * u, orla.bottom - 1 * u))
            drawRoundRect(verde, ojo.topLeft, ojo.size, CornerRadius(ojo.height / 2))
            drawRoundRect(pf, ojo.topLeft, ojo.size, CornerRadius(ojo.height / 2), style = Stroke(0.7f * u))
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", ojo, letra, if (oscuro) HUESO else AZULEJO, 9.5f, peso = FontWeight.Bold, minimo = 6f)
        }
    }

    override val altoCampo: Dp = 54.dp
    override val cajaCampo = Rect(0.2f, 0.22f, 0.95f, 0.78f)
    override val letraCampo: FontFamily = LetraExtra.amiri
    override fun tintaCampo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El buscador: una regla de cedro con su filete de taracea (triangulitos de hueso y ébano) y la tira de hueso donde se
     * escribe; a la izquierda, un astrolabio de latón con su red de estrellas y su alidada. Con el foco la red gira, el latón
     * se enciende y el filete de la tira se vuelve de oro.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(1.5f * u, 2 * u), Size(w - 1.5f * u, h - 2 * u), CornerRadius(3 * u))
        drawRoundRect(c(CEDRO), Offset.Zero, Size(w - 1.5f * u, h - 2 * u), CornerRadius(3 * u))
        vetas(Rect(0f, 0f, w, h - 2 * u), c(VETA).copy(alpha = 0.4f), 57, densidad = 0.7f, grosor = 0.7f)
        // El filete de taracea, arriba y abajo.
        listOf(2.5f * u, h - 6.5f * u).forEach { y ->
            var x = 4 * u; var j = 0
            while (x < w - 6 * u && j < 200) {
                drawPath(Path().apply { moveTo(x, y + 2.5f * u); lineTo(x + 2 * u, y); lineTo(x + 4 * u, y + 2.5f * u); close() }, c(if (j % 2 == 0) HUESO else NEGRO))
                x += maxOf(4 * u, 1f); j++
            }
        }
        val tira = Rect(w * cajaCampo.left - 8 * u, 7.5f * u, w - 8 * u, h - 10 * u)
        drawRect(claroHondo(HUESO, oscuro), tira.topLeft, tira.size)
        drawRect(if (e.enfocado) c(ORO_CLARO) else c(VETA).copy(alpha = 0.6f), tira.topLeft, tira.size, style = Stroke((if (e.enfocado) 1.4f else 0.8f) * u))
        // El astrolabio: la madre con su limbo graduado, la red de estrellas y la alidada.
        val o = Offset(w * cajaCampo.left * 0.47f, (h - 2 * u) / 2f + 1 * u)
        val r = minOf(19 * u, h * 0.4f)
        val laton = c(if (e.enfocado) mezcla(LATON, Color.White, 0.2f) else LATON)
        drawCircle(laton, 3 * u, Offset(o.x, o.y - r - 1.5f * u), style = Stroke(1.2f * u))
        drawCircle(Color.Black.copy(alpha = 0.3f), r, o + Offset(0.8f * u, 1 * u))
        drawCircle(laton, r, o)
        drawCircle(mezcla(laton, Color.Black, 0.35f), r * 0.78f, o)
        for (j in 0 until 36) drawLine(Color.Black.copy(alpha = 0.4f), polarMedievo(o, r * 0.8f, j * 10f), polarMedievo(o, r * 0.94f, j * 10f), 0.5f * u)
        rotate(if (e.enfocado) 40f else 0f, o) {
            drawCircle(laton, r * 0.5f, o + Offset(0f, -r * 0.12f), style = Stroke(1.2f * u))
            for (j in 0 until 5) {
                val p = polarMedievo(o, r * 0.6f, j * 72f - 60f)
                drawLine(laton, o, p, 0.9f * u)
                drawCircle(laton, 1.3f * u, p)
            }
        }
        rotate(if (e.enfocado) -25f else 15f, o) { drawLine(c(mezcla(LATON, Color.Black, 0.2f)), o - Offset(r * 0.95f, 0f), o + Offset(r * 0.95f, 0f), 1.6f * u) }
        drawCircle(laton, 1.8f * u, o)
        if (e.enfocado) haloMedievo(o, r * 1.2f, LUZ_FAROL, 0.25f)
        if (e.vacio) pistaMedievo(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), letraCampo,
            tintaHonda(oscuro).copy(alpha = 0.5f))
    }

    /**
     * Las obras: el estrado de la sala. El muro encalado con su zócalo pintado de almagra (bandas que se cruzan en lazo) y,
     * abajo, la tarima con su alfombra de cenefa verde y oro, y un cojín azul con borlas en cada punta. Distinto de la
     * alcoba de yesería y de la alacena del muro.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF100C0A), 0.64f) else t(x, false, false)
        drawRect(c(YESO))
        // El zócalo de almagra: dos filetes y un lazo de cintas que se cruzan.
        val z0 = h * 0.5f; val z1 = h - 30 * u
        val almagra = c(ALMAGRA).copy(alpha = 0.4f)
        drawLine(almagra, Offset(0f, z0), Offset(w, z0), 1.2f * u)
        drawLine(almagra, Offset(0f, z1 - 1 * u), Offset(w, z1 - 1 * u), 1.2f * u)
        val paso = maxOf((z1 - z0) * 1.2f, 4f)
        var x = -paso; var k = 0
        while (x < w && k < 60) {
            drawLine(almagra, Offset(x, z1 - 2 * u), Offset(x + paso, z0 + 2 * u), 2 * u)
            drawLine(almagra, Offset(x, z0 + 2 * u), Offset(x + paso, z1 - 2 * u), 2 * u)
            drawLine(c(YESO), Offset(x, z1 - 2 * u), Offset(x + paso, z0 + 2 * u), 0.6f * u)
            x += paso; k++
        }
        // Arriba, una línea fina de estrellitas pintadas.
        var sx = 10 * u; k = 0
        while (sx < w && k < 40) { drawPath(estrella(Offset(sx, 10 * u), 2.4f * u, 8, 0.55f), c(ALMAGRA).copy(alpha = 0.3f)); sx += maxOf(22 * u, 1f); k++ }
        // La tarima y la alfombra.
        val t0 = h - 30 * u
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(0f, t0 - 4 * u), Size(w, 4 * u))
        drawRect(c(ALFOMBRA), Offset(0f, t0), Size(w, 21 * u))
        drawRect(c(VERDE), Offset(0f, t0 + 3 * u), Size(w, 5 * u))
        drawRect(c(ORO), Offset(0f, t0 + 3 * u), Size(w, 0.8f * u)); drawRect(c(ORO), Offset(0f, t0 + 7.2f * u), Size(w, 0.8f * u))
        var rx = 6 * u; k = 0
        while (rx < w && k < 80) { drawPath(estrella(Offset(rx, t0 + 5.5f * u), 1.8f * u, 4, 0.4f), c(HUESO)); rx += maxOf(10 * u, 1f); k++ }
        var mx = 12 * u; k = 0
        while (mx < w && k < 40) {
            drawPath(Path().apply { moveTo(mx, t0 + 11 * u); lineTo(mx + 5 * u, t0 + 15 * u); lineTo(mx, t0 + 19 * u); lineTo(mx - 5 * u, t0 + 15 * u); close() }, c(ORO).copy(alpha = 0.55f))
            mx += maxOf(24 * u, 1f); k++
        }
        drawRect(c(CEDRO_OSC), Offset(0f, t0 + 21 * u), Size(w, h - t0 - 21 * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, t0 + 21 * u), Size(w, 1.5f * u))
        // Los cojines de las puntas, con sus borlas.
        listOf(Rect(-6 * u, t0 - 14 * u, 30 * u, t0 + 4 * u), Rect(w - 30 * u, t0 - 14 * u, w + 6 * u, t0 + 4 * u)).forEach { r ->
            drawRoundRect(c(AZUL), r.topLeft, r.size, CornerRadius(7 * u))
            drawLine(c(ORO), Offset(r.left + 4 * u, r.center.y), Offset(r.right - 4 * u, r.center.y), 1 * u)
            listOf(r.left + 3 * u, r.right - 3 * u).forEach { bx -> drawCircle(c(ORO), 2 * u, Offset(bx, r.top + 3 * u)) }
        }
    }

    /**
     * La tapa de una obra: una tabla (cedro, ébano o nogal según la obra) con taracea de hueso: la orla de triangulitos de
     * hueso y de color (verde o azul), estrellas de ocho puntas en las esquinas y, en medio, un medallón lobulado de hueso con
     * el título. Arriba, la tira de hueso con el tipo; abajo, el autor y el año. En miniatura, el medallón con la inicial.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = t(x, oscuro, false)
        val semilla = e.semilla and 0x7fffffff
        val madera = c(MADERAS_TARACEA[semilla % MADERAS_TARACEA.size])
        val color = c(if ((semilla / 3) % 2 == 0) VERDE else AZUL)
        val hueso = if (oscuro) mezcla(HUESO, Color(0xFF100C0A), 0.22f) else HUESO
        drawRect(madera)
        vetas(Rect(0f, 0f, w, h), c(VETA).copy(alpha = 0.35f), 70 + semilla % 7, densidad = 0.8f, vertical = true, grosor = 0.6f)
        // La orla de triangulitos de hueso y color.
        val b = w * 0.06f
        val orla = Rect(w * 0.06f, w * 0.06f, w * 0.94f, h - w * 0.06f)
        drawRect(hueso, orla.topLeft, orla.size, style = Stroke(maxOf(1f, w * 0.008f)))
        val dentro = orla.deflate(b)
        drawRect(hueso, dentro.topLeft, dentro.size, style = Stroke(maxOf(1f, w * 0.008f)))
        if (!e.mini) {
            val n = maxOf(4, (orla.width / b).toInt())
            val pasoX = orla.width / n
            for (j in 0 until n) listOf(orla.top, orla.bottom - b).forEach { y ->
                val x0 = orla.left + pasoX * j
                drawPath(Path().apply { moveTo(x0, y + b); lineTo(x0 + pasoX / 2, y); lineTo(x0 + pasoX, y + b); close() }, if (j % 2 == 0) hueso else color)
            }
            val m2 = maxOf(4, ((orla.height - 2 * b) / b).toInt())
            val pasoY = (orla.height - 2 * b) / m2
            for (j in 0 until m2) listOf(orla.left, orla.right - b).forEach { x ->
                val y0 = orla.top + b + pasoY * j
                drawPath(Path().apply { moveTo(x, y0); lineTo(x + b, y0 + pasoY / 2); lineTo(x, y0 + pasoY); close() }, if (j % 2 == 0) color else hueso)
            }
            listOf(orla.topLeft, Offset(orla.right, orla.top), Offset(orla.left, orla.bottom), orla.bottomRight).forEach { p ->
                val q = Offset(p.x + (if (p.x < w / 2) b / 2 else -b / 2), p.y + (if (p.y < h / 2) b / 2 else -b / 2))
                drawRect(madera, Offset(q.x - b / 2, q.y - b / 2), Size(b, b))
                drawPath(jatam(q, b * 0.5f), hueso)
            }
        }
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.35f), maxOf(1f, w * 0.02f))
        if (e.mini) {
            val o = Offset(w / 2, h / 2)
            medallon(o, w * 0.36f, hueso)
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifBlank { "·" }, Rect(o.x - w * 0.3f, o.y - w * 0.3f, o.x + w * 0.3f, o.y + w * 0.3f), letraTapa, TINTA, s * 0.4f, peso = FontWeight.Bold)
            return
        }
        // La tira del tipo.
        val tira = Rect(dentro.left + w * 0.08f, dentro.top + w * 0.05f, dentro.right - w * 0.08f, dentro.top + h * 0.1f)
        drawRect(hueso, tira.topLeft, tira.size)
        textoEn(medidor, e.tipo.uppercase(), tira.deflate(maxOf(1f, w * 0.01f)), letra, color, s * 0.07f, peso = FontWeight.Bold, espaciado = s * 0.006f, minimo = 4f)
        // El medallón lobulado del título.
        val o = Offset(w / 2, h * 0.5f)
        val rr = dentro.width * 0.48f
        translate(w * 0.012f, w * 0.018f) { medallon(o, rr, Color.Black.copy(alpha = 0.3f)) }
        medallon(o, rr, hueso)
        drawCircle(color.copy(alpha = 0.7f), rr * 0.82f, o, style = Stroke(maxOf(1f, w * 0.01f)))
        tituloMedievo(medidor, e.titulo.ifBlank { "—" }, Rect(o.x - rr * 0.78f, o.y - rr * 0.6f, o.x + rr * 0.78f, o.y + rr * 0.6f), letraTapa, TINTA, s * 0.12f,
            peso = FontWeight.Bold)
        textoEn(medidor, e.autor, Rect(dentro.left + w * 0.04f, h * 0.75f, dentro.right - w * 0.04f, h * 0.82f), letra, hueso, s * 0.075f, minimo = 4f)
        textoEn(medidor, e.anio, Rect(dentro.left + w * 0.04f, h * 0.82f, dentro.right - w * 0.04f, h * 0.87f), letra, mezcla(hueso, c(ORO_CLARO), 0.5f), s * 0.07f, minimo = 4f)
    }

    override val margenRecuerdo: Dp = 16.dp
    override val altoRotuloRecuerdo: Dp = 48.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El recuerdo: una lápida de mármol blanco con sus vetas grises, su moldura tallada y, en lo alto, el rótulo labrado en
     * letra de friso: «HACE N AÑOS», la fecha y el clima. El ánimo es un taraceado redondo de piedra del color del día con
     * su aro de oro, a la derecha.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenRecuerdo.toPx(); val alto = altoRotuloRecuerdo.toPx()
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(1.5f * u, 2 * u), Size(w - 1.5f * u, h - 2 * u))
        val marmol = claroHondo(MARMOL, oscuro)
        drawRect(marmol, Offset.Zero, Size(w - 1.5f * u, h - 2 * u))
        // Unas vetas grises, solo en las orillas.
        for (j in 0 until 3) {
            val y0 = h * azar(e.semilla, j)
            drawPath(Path().apply {
                moveTo(0f, y0); cubicTo(w * 0.05f, y0 + 10 * u, w * 0.08f, y0 - 6 * u, w * 0.14f, y0 + 14 * u)
            }, Color(0xFF8C8A84).copy(alpha = 0.25f), style = Stroke(0.7f * u))
            drawPath(Path().apply {
                moveTo(w, h - y0); cubicTo(w * 0.94f, h - y0 - 8 * u, w * 0.9f, h - y0 + 6 * u, w * 0.84f, h - y0 - 12 * u)
            }, Color(0xFF8C8A84).copy(alpha = 0.2f), style = Stroke(0.7f * u))
        }
        bisel(Rect(0f, 0f, w - 1.5f * u, h - 2 * u), Color.White.copy(alpha = if (oscuro) 0.06f else 0.5f), Color.Black.copy(alpha = 0.25f), 2 * u)
        val moldura = Rect(4.5f * u, 4.5f * u, w - 6 * u, h - 6.5f * u)
        drawRect(Color.Black.copy(alpha = 0.18f), moldura.topLeft, moldura.size, style = Stroke(0.8f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.5f), moldura.deflate(1 * u).topLeft, moldura.deflate(1 * u).size, style = Stroke(0.6f * u))
        val tinta = tintaHonda(oscuro)
        val labra = if (oscuro) mezcla(HUESO, ORO_CLARO, 0.2f) else mezcla(TINTA, ALMAGRA, 0.4f)
        izquierdaMedievo(medidor, haceMedievo(e.anios).uppercase(), m, 7 * u, 27 * u, w - 2 * m - 26 * u, letra, labra, 15f, peso = FontWeight.Bold, espaciado = 1.2f)
        val clima = climaMedievo(e.clima)
        izquierdaMedievo(medidor, if (clima.isEmpty()) e.fecha else "${e.fecha} · $clima", m, 27 * u, alto - 5 * u, w - 2 * m - 22 * u, letra,
            tinta.copy(alpha = 0.85f), 12f, minimo = 8f)
        drawLine(Color.Black.copy(alpha = 0.22f), Offset(m, alto - 3 * u), Offset(w - m, alto - 3 * u), 0.8f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.6f), Offset(m, alto - 2f * u), Offset(w - m, alto - 2f * u), 0.6f * u)
        e.animo?.let { a ->
            val o = Offset(w - m - 8 * u, 18 * u)
            drawCircle(if (oscuro) mezcla(ORO, Color.Black, 0.15f) else ORO, 9 * u, o)
            drawCircle(a, 7.4f * u, o)
            drawPath(jatam(o, 4.2f * u), Color.White.copy(alpha = 0.3f))
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
 * La tapa es ese pothi tumbado y grande: las puntas de la tabla con el nudo infinito, el paño amarillo con el paño
 * crema cosido (la fecha y lo escrito), la cinta y la etiqueta azul con la cuenta; al abrirse la tela se aparta hacia
 * las puntas y deja ver la hoja larga (de noche, una luna en la etiqueta y las estrellas del toono).
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
    override val altoRepisa: Dp = 64.dp
    override val tabla: Dp = 14.dp
    override val separa: Dp = 8.dp
    override val letra: FontFamily = LetraExtra.filosofo

    override fun ancho(palabras: Int): Dp = ANCHO_POTHI.dp
    override fun alto(semilla: Int, palabras: Int): Float =
        (0.58f + 0.36f * (palabras / 800f).coerceIn(0f, 1f) + 0.05f * azar(semilla, 1)).coerceAtMost(1f)

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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa = Rect(0.2f, 0.325f, 0.7f, 0.665f)
    override val letraTapa: FontFamily = LetraExtra.filosofo
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La crema del paño cosido y de la hoja: se hunde menos que la tela amarilla, para que el extracto se lea siempre. */
    private fun crema(x: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = x
        if (oscuro) r = mezcla(r, Color(0xFF0F0B09), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val tela = c(TELAS[(e.semilla and 0x7fffffff) % TELAS.size])
        val madera = c(TAPAS[(azar(e.semilla, 4) * TAPAS.size).toInt().coerceIn(0, TAPAS.size - 1)])
        val oro = mezcla(c(ORO), ORO, 0.3f)
        // El pothi tumbado: la cara de arriba (la tabla) y, delante, el canto con las hojas largas entre las dos tablas.
        val cara = Rect(w * 0.035f, h * 0.08f, w * 0.965f, h * 0.79f)
        val frente = Rect(cara.left, cara.bottom, cara.right, h * 0.92f)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(cara.left + 3 * u, cara.top + 5 * u), Size(cara.width, frente.bottom - cara.top))
        drawRect(madera, cara.topLeft, cara.size)
        vetas(cara, Color.Black.copy(alpha = 0.25f), e.semilla, densidad = 0.5f, grosor = 0.7f)
        val g = 4.5f * u
        drawRect(mezcla(madera, Color.Black, 0.25f), frente.topLeft, Size(frente.width, g))
        drawRect(c(HOJAS), Offset(frente.left, frente.top + g), Size(frente.width, frente.height - 2 * g))
        var hy = frente.top + g + 1.4f * u
        while (hy < frente.bottom - g) { drawLine(Color.Black.copy(alpha = 0.16f), Offset(frente.left, hy), Offset(frente.right, hy), 0.5f * u); hy += 1.7f * u }
        drawRect(mezcla(madera, Color.Black, 0.35f), Offset(frente.left, frente.bottom - g), Size(frente.width, g))
        // En las puntas descubiertas de la tabla: el filete de oro y el nudo infinito sobre azul.
        listOf(cara.left, cara.right - w * 0.09f).forEach { x0 ->
            val punta = Rect(x0 + 3 * u, cara.top + 3 * u, x0 + w * 0.09f - 3 * u, cara.bottom - 3 * u)
            drawRect(oro.copy(alpha = 0.8f), punta.topLeft, punta.size, style = Stroke(1 * u))
            val cn = Offset(punta.center.x, punta.center.y)
            val s = minOf(punta.width * 0.5f, 16 * u)
            drawCircle(c(AZUL), s * 0.95f, cn)
            nudo(cn, s, oro, 1 * u)
            listOf(punta.top + 8 * u, punta.bottom - 8 * u).forEach { y -> drawCircle(oro, 1.6f * u, Offset(cn.x, y)) }
        }
        // Lo que se ve al abrirse la tela: la hoja de arriba, larga, con sus renglones y los dos círculos del cordel.
        val pano = Rect(w * 0.125f, cara.top - 2 * u, w * 0.875f, frente.bottom)
        if (a > 0.01f) {
            val hoja = Rect(pano.left + 2 * u, cara.top + 6 * u, pano.right - 2 * u, cara.bottom - 6 * u)
            drawRect(crema(HOJAS, oscuro, n), hoja.topLeft, hoja.size)
            drawRect(c(ROJO).copy(alpha = 0.5f), hoja.deflate(5 * u).topLeft, hoja.deflate(5 * u).size, style = Stroke(0.7f * u))
            val huecos = listOf(hoja.left + hoja.width * 0.3f, hoja.left + hoja.width * 0.7f)
            var ry = hoja.top + 12 * u; var j = 0
            while (ry < hoja.bottom - 9 * u) {
                var x = hoja.left + 12 * u; var k = 0
                while (x < hoja.right - 14 * u) {
                    val largo = minOf((5f + 9f * azar(e.semilla + j, k)) * u, hoja.right - 12 * u - x)
                    val tapado = huecos.any { hx -> x + largo > hx - 9 * u && x < hx + 9 * u }
                    if (!tapado) drawLine(c(TINTA).copy(alpha = 0.6f), Offset(x, ry), Offset(x + largo, ry), 1.4f * u)
                    x += largo + 2.6f * u; k++
                }
                ry += 7 * u; j++
            }
            huecos.forEach { hx ->
                drawCircle(c(ROJO).copy(alpha = 0.6f), 6 * u, Offset(hx, hoja.center.y), style = Stroke(0.8f * u))
                drawCircle(Color.Black.copy(alpha = 0.5f), 1.8f * u, Offset(hx, hoja.center.y))
            }
        }
        // El paño amarillo que lo envuelve, con sus pliegues y el paño crema cosido encima (ahí va lo escrito). Al abrirse,
        // sus dos mitades se apartan hacia las puntas.
        val panel = Rect(w * 0.17f, h * 0.15f, w * 0.73f, h * 0.72f)
        fun envoltura() {
            drawRect(tela, pano.topLeft, Size(pano.width, cara.bottom - pano.top))
            drawRect(Color.White.copy(alpha = 0.14f), pano.topLeft, Size(pano.width, (cara.bottom - pano.top) * 0.18f))
            drawRect(Color.White.copy(alpha = 0.07f), Offset(pano.left, pano.top + (cara.bottom - pano.top) * 0.18f), Size(pano.width, (cara.bottom - pano.top) * 0.16f))
            drawRect(mezcla(tela, Color.Black, 0.22f), Offset(pano.left, cara.bottom), Size(pano.width, pano.bottom - cara.bottom))
            for (j in 0 until 7) {
                val x = pano.left + pano.width * (0.06f + 0.14f * j + 0.04f * azar(e.semilla, 10 + j))
                drawLine(Color.Black.copy(alpha = 0.09f), Offset(x, pano.top), Offset(x + 6 * u, cara.bottom), 1.4f * u)
                drawLine(Color.White.copy(alpha = 0.12f), Offset(x + 2 * u, pano.top), Offset(x + 8 * u, cara.bottom), 0.7f * u)
                drawLine(Color.Black.copy(alpha = 0.14f), Offset(x + 6 * u, cara.bottom), Offset(x + 3 * u, pano.bottom), 1.2f * u)
            }
            listOf(pano.left, pano.right - 3 * u).forEach { x -> drawRect(Color.Black.copy(alpha = 0.18f), Offset(x, pano.top), Size(3 * u, pano.height)) }
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(panel.left + 1.5f * u, panel.top + 2 * u), panel.size)
            drawRect(c(AZUL), panel.topLeft, panel.size)
            val dentro = panel.deflate(3.5f * u)
            drawRect(crema(CREMA, oscuro, n), dentro.topLeft, dentro.size)
            var px = dentro.left + 2 * u
            while (px < dentro.right - 2 * u) { drawLine(c(CREMA).copy(alpha = 0.7f), Offset(px, panel.top + 1.6f * u), Offset(px + 2 * u, panel.top + 1.6f * u), 0.6f * u); px += 4 * u }
            textoEn(medidor, e.fecha, Rect(dentro.left + 4 * u, dentro.top + 2 * u, dentro.right - 4 * u, h * 0.315f), letra, c(AZUL), 12.5f, peso = FontWeight.Bold, minimo = 7f)
            drawLine(c(ROJO).copy(alpha = 0.6f), Offset(dentro.left + 10 * u, h * 0.318f), Offset(dentro.right - 10 * u, h * 0.318f), 0.7f * u)
            val ry = (h * 0.67f + dentro.bottom) / 2
            listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(dentro.center.x + s * 4 * u, ry); lineTo(dentro.center.x + s * 40 * u, ry) }, c(ROJO).copy(alpha = 0.6f), style = Stroke(0.7f * u)) }
            drawPath(Path().apply { moveTo(dentro.center.x, ry - 2.5f * u); lineTo(dentro.center.x + 2.5f * u, ry); lineTo(dentro.center.x, ry + 2.5f * u); lineTo(dentro.center.x - 2.5f * u, ry); close() }, c(ROJO))
        }
        val sx = 1f - 0.86f * a
        val medio = pano.center.x
        scale(sx, 1f, Offset(pano.left, pano.center.y)) { clipRect(pano.left - 1 * u, 0f, medio + 0.5f * u, h) { envoltura() } }
        scale(sx, 1f, Offset(pano.right, pano.center.y)) { clipRect(medio - 0.5f * u, 0f, pano.right + 1 * u, h) { envoltura() } }
        // La cinta roja que lo ata (una vuelta más cuanto más escribiste) y la etiqueta azul colgando, con la cuenta y el
        // sello; de noche, una luna. Al abrirse, la cinta ya está suelta.
        val suelta = (1f - a * 2.5f).coerceIn(0f, 1f)
        if (suelta > 0f) {
            val vueltas = 1 + ((e.palabras / 800f).coerceIn(0f, 1f) * 2).toInt()
            for (j in 0 until vueltas) {
                val x = w * 0.75f + j * 3.4f * u
                drawRect(c(CINTA).copy(alpha = suelta), Offset(x, pano.top), Size(2.4f * u, pano.height))
                drawRect(Color.White.copy(alpha = 0.18f * suelta), Offset(x, pano.top), Size(0.7f * u, pano.height))
            }
            val nudo = Offset(w * 0.75f + vueltas * 1.7f * u, cara.top + 8 * u)
            drawCircle(c(CINTA).copy(alpha = suelta), 3.6f * u, nudo)
            val et = Rect(w * 0.785f, cara.top + 12 * u, w * 0.85f, h * 0.76f)
            val punta = et.width * 0.5f
            drawLine(c(CINTA).copy(alpha = suelta), nudo, Offset(et.center.x, et.top + 2 * u), 1.2f * u)
            val etiqueta = Path().apply {
                moveTo(et.left, et.top); lineTo(et.right, et.top); lineTo(et.right, et.bottom - punta)
                lineTo(et.center.x, et.bottom); lineTo(et.left, et.bottom - punta); close()
            }
            drawPath(etiqueta, Color.Black.copy(alpha = 0.22f * suelta))
            drawPath(etiqueta, c(AZUL).copy(alpha = suelta))
            val dentro = Rect(et.left + 2 * u, et.top + 2 * u, et.right - 2 * u, et.bottom - punta - 1 * u)
            drawRect(crema(CREMA, oscuro, n).copy(alpha = suelta), dentro.topLeft, dentro.size)
            val rs = minOf(dentro.width * 0.3f, 5 * u)
            val cs = Offset(dentro.center.x, dentro.bottom - rs - 3 * u)
            if (!e.vacio && e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(dentro.left, dentro.top + 2 * u, dentro.right, cs.y - rs - 3 * u), letra,
                    c(TINTA).copy(alpha = suelta), 10f, vertical = true, peso = FontWeight.Bold, minimo = 6f)
            }
            if (n) {
                drawCircle(LUNA.copy(alpha = suelta), rs, cs)
                drawCircle(crema(CREMA, oscuro, n).copy(alpha = suelta), rs * 0.8f, Offset(cs.x + rs * 0.4f, cs.y - rs * 0.2f))
                drawCircle(c(TINTA).copy(alpha = 0.3f * suelta), rs, cs, style = Stroke(0.5f * u))
            } else {
                drawRect(c(ROJO).copy(alpha = suelta), Offset(cs.x - rs, cs.y - rs), Size(rs * 2, rs * 2))
                drawRect(c(CREMA).copy(alpha = 0.7f * suelta), Offset(cs.x - rs * 0.6f, cs.y - rs * 0.6f), Size(rs * 1.2f, rs * 1.2f), style = Stroke(0.5f * u))
            }
        }
        // De noche, las estrellas que se ven por el toono y su luz fría sobre el fieltro.
        if (n) {
            for (k in 0 until 9) drawCircle(ESTRELLA.copy(alpha = 0.5f + 0.4f * azar(e.semilla, 70 + k)), (0.6f + 0.8f * azar(e.semilla, 80 + k)) * u,
                Offset(w * azar(e.semilla, 90 + k), h * 0.06f * azar(e.semilla, 100 + k) + 1 * u))
            clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.5f, -h * 0.15f), h * 1.1f, CIELO.copy(alpha = 0.16f)) }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 116.dp

    /** La greca del martillo de [x0] a [x1] entre [y0] y [y1]: un meandro de ganchos cuadrados de [paso] de ancho. */
    private fun DrawScope.grecaMartillo(x0: Float, x1: Float, y0: Float, y1: Float, paso: Float, color: Color, grosor: Float) {
        val camino = Path()
        val a = y1 - y0
        camino.moveTo(x0, y1)
        var x = x0
        while (x + paso <= x1) {
            camino.lineTo(x, y0); camino.lineTo(x + paso * 0.75f, y0); camino.lineTo(x + paso * 0.75f, y0 + a * 0.6f)
            camino.lineTo(x + paso * 0.4f, y0 + a * 0.6f); camino.lineTo(x + paso * 0.4f, y1); camino.lineTo(x + paso, y1)
            x += paso
        }
        drawPath(camino, color, style = Stroke(grosor))
    }

    /**
     * La cabecera pintada como el frente de un arcón de la ger: el marco rojo con su filete naranja, una banda roja con la
     * greca del martillo en oro arriba y abajo y lunares de oro en las esquinas; a la izquierda, el cuadro azul con el nudo
     * infinito en oro (de noche, el toono, la corona de la ger, por donde se ven la luna y las estrellas); a la derecha, el
     * rótulo en azul, la fecha (si es larga, en dos renglones) y el detalle.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val rojo = c(ROJO); val oro = c(ORO); val naranja = c(NARANJA)
        val marco = Rect(2f * u, 4f * u, w - 2f * u, h - 3f * u)
        val banda = 8f * u
        listOf(marco.top, marco.bottom - banda).forEach { y ->
            drawRect(rojo, Offset(marco.left, y), Size(marco.width, banda))
            grecaMartillo(marco.left + 7f * u, marco.right - 7f * u, y + 1.6f * u, y + banda - 1.6f * u, 8f * u, oro, 0.9f * u)
            listOf(marco.left + 3.5f * u, marco.right - 3.5f * u).forEach { drawCircle(oro, 1.6f * u, Offset(it, y + banda / 2)) }
        }
        drawRect(rojo, marco.topLeft, marco.size, style = Stroke(1.6f * u))
        drawRect(naranja, Offset(marco.left + 3f * u, marco.top + banda + 2f * u), Size(marco.width - 6f * u, marco.height - 2 * banda - 4f * u), style = Stroke(0.8f * u))
        // El cuadro del nudo, o el toono de noche.
        val lado = 46f * u
        val q = Rect(13f * u, h / 2 - lado / 2, 13f * u + lado, h / 2 + lado / 2)
        if (!e.noche) {
            drawRect(Color.Black.copy(alpha = 0.18f), q.topLeft + Offset(1.2f * u, 1.6f * u), q.size)
            drawRect(c(AZUL), q.topLeft, q.size)
            drawRect(naranja, q.topLeft, q.size, style = Stroke(1.4f * u))
            val i = q.deflate(3f * u)
            drawRect(oro.copy(alpha = 0.6f), i.topLeft, i.size, style = Stroke(0.6f * u))
            nudo(q.center, lado * 0.4f, oro, 1.2f * u)
        } else {
            val o = q.center; val r = lado / 2
            drawCircle(c(CIELO_NOCHE), r, o)
            listOf(Offset(-0.45f, -0.2f), Offset(0.15f, -0.55f), Offset(0.5f, 0.1f), Offset(-0.2f, 0.45f), Offset(0.3f, 0.5f), Offset(-0.55f, 0.25f)).forEachIndexed { k, f ->
                drawPath(estrella(Offset(o.x + f.x * r * 0.75f, o.y + f.y * r * 0.75f), (if (k % 2 == 0) 1.8f else 1.2f) * u, 4, 0.4f), ESTRELLA)
            }
            drawPath(mediaLunaMedievo(Offset(o.x + r * 0.08f, o.y - r * 0.05f), r * 0.26f), LUNA)
            drawCircle(c(KHANA), r * 0.72f, o, style = Stroke(1f * u))
            for (k in 0 until 16) {
                val g = Math.toRadians(k * 22.5)
                val cs = kotlin.math.cos(g).toFloat(); val sn = kotlin.math.sin(g).toFloat()
                drawLine(c(KHANA), Offset(o.x + cs * r * 0.72f, o.y + sn * r * 0.72f), Offset(o.x + cs * r, o.y + sn * r), 1.2f * u)
            }
            drawCircle(c(KHANA), r, o, style = Stroke(2.2f * u))
        }
        val x0 = q.right + 10f * u; val x1 = marco.right - 8f * u
        textoEn(medidor, e.rotulo, Rect(x0, 16f * u, x1, 34f * u), letra, rotuloMedievo(p, e.noche), 13f, espaciado = 1.5f, minimo = 8f)
        fechaMedievo(medidor, e.fecha, Rect(x0, 35f * u, x1, 77f * u), letra, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(x0, 78f * u, x1, 95f * u), letra, p.tenue, 11f, minimo = 7f)
    }

    /**
     * Las cenefas pintadas de la ger: arriba y abajo, una banda roja con la greca del martillo en oro; a los lados, un filete
     * azul; en el margen izquierdo, nubecillas azules en espiral cada tanto; y en las esquinas del pie, el khana (la celosía
     * de listones cruzados) apenas marcado. En modo oscuro, la lámpara de manteca de latón, abajo a la derecha: el fieltro
     * se hunde en la penumbra y la llamita lo tiñe de oro.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val rojo = if (noche) mezcla(ROJO, NOCHE, 0.4f) else ROJO
        val oro = if (noche) mezcla(ORO, NOCHE, 0.3f) else ORO
        listOf(0f, h - 10f * u).forEach { y ->
            drawRect(rojo.copy(alpha = 0.7f), Offset(0f, y), Size(w, 10f * u))
            grecaMartillo(6f * u, w - 6f * u, y + 2f * u, y + 8f * u, 9f * u, oro.copy(alpha = 0.75f), 0.9f * u)
        }
        listOf(9f * u, w - 9f * u).forEach { drawLine(p.acento.copy(alpha = 0.3f), Offset(it, 10f * u), Offset(it, h - 10f * u), 1f * u) }
        var y = 80f * u
        while (y < h - 80f * u) {
            val o = Offset(19f * u, y)
            drawArc(p.acento.copy(alpha = 0.22f), 0f, 300f, false, Offset(o.x - 4f * u, o.y - 4f * u), Size(8f * u, 8f * u), style = Stroke(1f * u))
            drawArc(p.acento.copy(alpha = 0.22f), 180f, 240f, false, Offset(o.x - 2f * u, o.y - 2f * u), Size(4f * u, 4f * u), style = Stroke(1f * u))
            y += 70f * u
        }
        val listones = (if (noche) p.tenue else KHANA).copy(alpha = 0.08f)
        listOf(0f, w - 70f * u).forEach { x0 ->
            clipRect(x0, h - 80f * u, x0 + 70f * u, h - 10f * u) {
                var d = -80f * u
                while (d < 80f * u) {
                    drawLine(listones, Offset(x0 + d, h - 10f * u), Offset(x0 + d + 70f * u, h - 80f * u), 1.2f * u)
                    drawLine(listones, Offset(x0 + d, h - 80f * u), Offset(x0 + d + 70f * u, h - 10f * u), 1.2f * u)
                    d += 12f * u
                }
            }
        }
        if (oscuro && !p.oscura) {
            val x = w - 15f * u; val yl = h - 92f * u
            val miel = Color(0xFFF2C46B)
            penumbraMedievo(Offset(x, yl - 4f * u), miel)
            haloMedievo(Offset(x, yl - 4f * u), 30f * u, miel, 0.6f)
            val laton = t(ORO, true, false)
            drawPath(Path().apply {
                moveTo(x - 7f * u, yl + 4f * u); lineTo(x + 7f * u, yl + 4f * u)
                quadraticTo(x + 5f * u, yl + 11f * u, x, yl + 11f * u); quadraticTo(x - 5f * u, yl + 11f * u, x - 7f * u, yl + 4f * u); close()
            }, laton)
            drawRect(laton, Offset(x - 1.2f * u, yl + 11f * u), Size(2.4f * u, 10f * u))
            drawOval(laton, Offset(x - 6f * u, yl + 19f * u), Size(12f * u, 4f * u))
            drawLine(CREMA, Offset(x - 6f * u, yl + 4.6f * u), Offset(x + 6f * u, yl + 4.6f * u), 1.4f * u)
            llamaMedievo(Offset(x, yl + 3.5f * u), 10f * u, Color(0xFFF7C45A))
        }
    }

    /** La barra: una franja de fieltro claro con la triple raya pintada al pie (roja, naranja y azul) y lunares de oro. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(FIELTRO, p.color, 0.3f).let { if (oscuro) mezcla(it, Color.Black, 0.24f) else it }
        drawRect(Brush.verticalGradient(listOf(mezcla(base, Color.White, 0.06f), mezcla(base, Color.Black, 0.06f)), 0f, h))
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        drawRect(c(ROJO), Offset(0f, h - 7f * u), Size(w, 2.4f * u))
        drawRect(c(NARANJA), Offset(0f, h - 4.2f * u), Size(w, 1.2f * u))
        drawRect(c(AZUL), Offset(0f, h - 2.2f * u), Size(w, 1.4f * u))
        var x = 8f * u
        while (x < w) { drawCircle(c(ORO).copy(alpha = 0.7f), 0.9f * u, Offset(x, h - 10.5f * u)); x += 16f * u }
    }

    /** El botón de sellar: la tamga, un sello rojo con su filete naranja, el hilo de oro y un cuadrito de oro en cada esquina. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        val rojo = c(ROJO); val oro = c(ORO)
        drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1f * u, 1.6f * u), r.size)
        drawRect(Brush.verticalGradient(listOf(mezcla(rojo, Color.White, 0.1f), mezcla(rojo, Color.Black, 0.2f)), r.top, r.bottom), r.topLeft, r.size)
        drawRect(c(NARANJA), r.topLeft, r.size, style = Stroke(1.4f * u))
        val i = r.deflate(3.5f * u)
        drawRect(oro.copy(alpha = 0.7f), i.topLeft, i.size, style = Stroke(0.6f * u))
        listOf(i.topLeft, Offset(i.right, i.top), Offset(i.right, i.bottom), Offset(i.left, i.bottom)).forEach { o ->
            drawRect(oro, Offset(o.x - 1.4f * u, o.y - 1.4f * u), Size(2.8f * u, 2.8f * u))
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = CREMA

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 254.dp

    /** El azul de la bufanda (khadag), la plata del cuenco, el latón de la tetera y la luz de la lámpara de manteca. */
    private val KHADAG = Color(0xFF2E6DB4)
    private val PLATA = Color(0xFFC9CCD2)
    private val MADERA_CUENCO = Color(0xFF7A4A2A)
    private val LATON = Color(0xFFC9A04A)
    private val MANTECA = Color(0xFFF6CC6A)

    /** El radio del rollo de órdenes, en dp: de 13 con un mandato a 26 con 240 o más. */
    private fun radioRollo(hojas: Int): Float = 13f + 13f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /**
     * El rollo de órdenes sobre la mesa baja pintada, dentro de la ger: el fieltro blanco con la celosía del khana y las
     * varas del techo que suben, la faja que ciñe la pared y la alfombra. La mesa es naranja con el tablero rojo, el frente
     * en paneles azules con el nudo infinito de oro, la greca del martillo y las patas curvas. Sobre ella, el cuenco de té con
     * su borde de plata en su platito, la tetera de latón y la lámpara de manteca. El rollo, tumbado, engorda con los
     * mandatos: el papel enrollado con sus vueltas, los rodillos de madera con sus perillas, la faja de papel con el nombre y
     * la bufanda azul que lo ata, con la tablilla de la cuenta colgada de su nudo. Al abrirse, la bufanda se suelta y el
     * papel cae por delante de la mesa con sus columnas de escritura vertical y el sello rojo. En oscuro, la lámpara de
     * manteca encendida alumbra la ger.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0C0A0A), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val r = radioRollo(e.hojas) * u
        val papel = crema(HOJAS, oscuro, false)
        val oro = c(ORO)
        // El fieltro de la pared, la celosía del khana y las varas del techo.
        drawRect(c(FIELTRO))
        val faja = h * 0.2f
        for (j in 0..14) {
            val x = w * (j / 14f)
            drawLine(c(KHANA), Offset(w / 2 + (x - w / 2) * 0.25f, -h * 0.4f), Offset(x, faja), 2.4f * u)
        }
        drawRect(c(FIELTRO_OSC), Offset(0f, faja), Size(w, h * 0.5f))
        clipRect(0f, faja, w, h * 0.7f) {
            var d = -h
            while (d < w + h) {
                drawLine(c(KHANA).copy(alpha = 0.55f), Offset(d, faja), Offset(d + h * 0.5f, h * 0.7f), 1.8f * u)
                drawLine(c(KHANA).copy(alpha = 0.55f), Offset(d + h * 0.5f, faja), Offset(d, h * 0.7f), 1.8f * u)
                d += 22 * u
            }
        }
        drawRect(c(ROJO), Offset(0f, faja - 3 * u), Size(w, 7 * u))
        grecaMartillo(0f, w, faja - 2 * u, faja + 3 * u, 8 * u, oro, 0.8f * u)
        // La alfombra.
        val piso = h * 0.88f
        drawRect(c(ALFOMBRA), Offset(0f, h * 0.7f), Size(w, h * 0.3f))
        drawRect(oro.copy(alpha = 0.6f), Offset(0f, h * 0.73f), Size(w, 1.4f * u))
        var ax = 8 * u
        while (ax < w) { drawPath(estrellaRombo(Offset(ax, h * 0.77f), 3 * u), c(AZUL)); ax += 16 * u }

        // ------------------------------------------------ la mesa baja pintada
        val m0 = h * 0.5f; val m1 = h * 0.64f
        val tablero = Path().apply { moveTo(w * 0.1f, m0); lineTo(w * 0.9f, m0); lineTo(w * 0.96f, m1); lineTo(w * 0.04f, m1); close() }
        drawPath(tablero, c(ROJO))
        drawPath(Path().apply { moveTo(w * 0.13f, m0 + 3 * u); lineTo(w * 0.87f, m0 + 3 * u); lineTo(w * 0.92f, m1 - 3 * u); lineTo(w * 0.08f, m1 - 3 * u); close() }, oro.copy(alpha = 0.7f), style = Stroke(1 * u))
        drawRect(c(NARANJA), Offset(w * 0.04f, m1), Size(w * 0.92f, 6 * u))
        val frente = Rect(w * 0.07f, m1 + 6 * u, w * 0.93f, h * 0.82f)
        drawRect(c(NARANJA), frente.topLeft, frente.size)
        grecaMartillo(frente.left, frente.right, frente.top + 1 * u, frente.top + 6 * u, 8 * u, c(ROJO), 1 * u)
        val n = 3
        val ancho = (frente.width - 8 * u * (n + 1)) / n
        for (j in 0 until n) {
            val p = Rect(frente.left + 8 * u + j * (ancho + 8 * u), frente.top + 9 * u, frente.left + 8 * u + j * (ancho + 8 * u) + ancho, frente.bottom - 4 * u)
            drawRect(c(if (j == 1) VERDE else AZUL), p.topLeft, p.size)
            drawRect(oro, p.topLeft, p.size, style = Stroke(1.2f * u))
            nudo(p.center, minOf(p.height, p.width) * 0.6f, oro, 1.2f * u)
        }
        listOf(frente.left, frente.right - 12 * u).forEachIndexed { j, x ->
            drawRect(c(ROJO_OSC), Offset(x, frente.bottom), Size(12 * u, piso - frente.bottom))
            drawPath(Path().apply {
                moveTo(x, piso); quadraticTo(x + (if (j == 0) -6 * u else 18 * u), piso - 4 * u, x + (if (j == 0) -2 * u else 14 * u), piso - 10 * u); lineTo(x + 6 * u, piso - 10 * u); close()
            }, c(ROJO_OSC))
        }
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(w * 0.04f, piso - 3 * u), Size(w * 0.92f, 6 * u))

        // ------------------------------------------------ la lámpara de manteca, el cuenco de té y la tetera
        val lam = Offset(w * 0.12f, m0 + 8 * u)
        drawOval(c(LATON), Offset(lam.x - 7 * u, lam.y - 2 * u), Size(14 * u, 4 * u))
        drawRect(c(LATON), Offset(lam.x - 1.6f * u, lam.y - 14 * u), Size(3.2f * u, 13 * u))
        drawArc(c(LATON), 0f, 180f, true, Offset(lam.x - 9 * u, lam.y - 22 * u), Size(18 * u, 14 * u))
        drawOval(c(mezcla(MANTECA, Color.White, 0.3f)), Offset(lam.x - 9 * u, lam.y - 16 * u), Size(18 * u, 4 * u))
        drawLine(c(TINTA), Offset(lam.x, lam.y - 15 * u), Offset(lam.x, lam.y - 18 * u), 0.9f * u)
        val llama = Offset(lam.x, lam.y - 17 * u)
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to MANTECA.copy(alpha = 0.32f), 0.35f to MANTECA.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.9f))
            haloMedievo(llama - Offset(0f, 4 * u), 26 * u, MANTECA, 0.7f)
            llamaMedievo(llama, 11 * u, MANTECA)
        }
        val tet = Offset(w * 0.88f, m0 + 12 * u)
        drawOval(c(LATON), Offset(tet.x - 10 * u, tet.y - 26 * u), Size(20 * u, 26 * u))
        drawRect(c(mezcla(LATON, Color.Black, 0.2f)), Offset(tet.x - 10 * u, tet.y - 14 * u), Size(20 * u, 3 * u))
        drawPath(Path().apply { moveTo(tet.x - 8 * u, tet.y - 12 * u); lineTo(tet.x - 20 * u, tet.y - 24 * u); lineTo(tet.x - 18 * u, tet.y - 26 * u); lineTo(tet.x - 6 * u, tet.y - 18 * u); close() }, c(LATON))
        drawArc(c(LATON), 180f, 180f, false, Offset(tet.x - 7 * u, tet.y - 34 * u), Size(14 * u, 14 * u), style = Stroke(1.8f * u))
        drawCircle(c(ROJO), 2.4f * u, Offset(tet.x, tet.y - 27 * u))
        drawOval(Color.White.copy(alpha = 0.25f), Offset(tet.x - 6 * u, tet.y - 22 * u), Size(4 * u, 7 * u))
        val cuenco = Offset(w * 0.15f, m1 - 2 * u)
        drawOval(c(PLATA), Offset(cuenco.x - 13 * u, cuenco.y - 3 * u), Size(26 * u, 6 * u))
        drawArc(c(MADERA_CUENCO), 0f, 180f, true, Offset(cuenco.x - 10 * u, cuenco.y - 16 * u), Size(20 * u, 16 * u))
        drawOval(c(PLATA), Offset(cuenco.x - 10 * u, cuenco.y - 10 * u), Size(20 * u, 4 * u))
        drawOval(c(Color(0xFFD9B98A)), Offset(cuenco.x - 8 * u, cuenco.y - 9.4f * u), Size(16 * u, 2.6f * u))

        // ------------------------------------------------ el rollo de órdenes
        val L = w * 0.25f; val R = w * 0.75f
        val yc = m1 - 8 * u - r
        // El papel que cae por delante de la mesa al abrirse, con sus columnas de escritura vertical y el sello.
        if (a > 0.01f) {
            val caida = Rect(L + 8 * u, yc, R - 8 * u, yc + r + (h * 0.95f - yc - r) * a)
            drawRect(Color.Black.copy(alpha = 0.25f), caida.topLeft + Offset(3 * u, 3 * u), caida.size)
            drawRect(papel, caida.topLeft, caida.size)
            var cx = caida.left + 14 * u; var k = 0
            while (cx < caida.right - 10 * u) {
                var cy = yc + r + 8 * u
                drawLine(c(TINTA).copy(alpha = 0.6f), Offset(cx, cy), Offset(cx, caida.bottom - 8 * u), 1.1f * u)
                while (cy < caida.bottom - 10 * u) {
                    val l = (2 + 4 * azar(e.semilla + k, cy.toInt())) * u
                    drawLine(c(TINTA).copy(alpha = 0.6f), Offset(cx, cy), Offset(cx + l * (if (k % 2 == 0) 1 else -1), cy + 2 * u), 1.1f * u)
                    cy += 7 * u
                }
                cx += 13 * u; k++
            }
            if (caida.height > 40 * u) {
                drawRect(c(ROJO), Offset(caida.right - 22 * u, caida.bottom - 22 * u), Size(14 * u, 14 * u))
                drawRect(papel.copy(alpha = 0.6f), Offset(caida.right - 19 * u, caida.bottom - 19 * u), Size(8 * u, 8 * u), style = Stroke(0.8f * u))
            }
        }
        // La sombra en la mesa, el cilindro con sus vueltas y sus luces.
        drawOval(Color.Black.copy(alpha = 0.28f), Offset(L, yc + r * 0.6f), Size(R - L, r * 0.8f + 4 * u))
        drawRect(papel, Offset(L, yc - r), Size(R - L, 2 * r))
        drawRect(Color.White.copy(alpha = 0.3f), Offset(L, yc - r * 0.7f), Size(R - L, r * 0.35f))
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(L, yc + r * 0.2f), Size(R - L, r * 0.4f))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(L, yc + r * 0.6f), Size(R - L, r * 0.4f))
        val vueltas = (2 + e.hojas / 30).coerceAtMost(9)
        for (j in 0 until vueltas) {
            val x = L + (R - L) * azar(e.semilla, 40 + j)
            drawLine(c(TINTA).copy(alpha = 0.1f), Offset(x, yc - r), Offset(x + 2 * u, yc + r), 0.6f * u)
        }
        // Los rodillos de madera con sus perillas y el canto del rollo, en espiral.
        listOf(L, R).forEachIndexed { j, x ->
            drawOval(c(mezcla(HOJAS, Color.Black, 0.15f)), Offset(x - r * 0.35f, yc - r), Size(r * 0.7f, 2 * r))
            for (q in 1..3) drawOval(c(TINTA).copy(alpha = 0.18f), Offset(x - r * 0.35f * q / 4, yc - r * q / 4), Size(r * 0.7f * q / 4 * 2 / 2, 2 * r * q / 4), style = Stroke(0.6f * u))
            val s = if (j == 0) -1f else 1f
            drawRect(c(TAPAS[0]), Offset(minOf(x, x + s * 12 * u), yc - 3 * u), Size(12 * u, 6 * u))
            drawCircle(c(TAPAS[0]), 6 * u, Offset(x + s * 15 * u, yc))
            drawCircle(oro, 6 * u, Offset(x + s * 15 * u, yc), style = Stroke(1.2f * u))
            drawCircle(Color.White.copy(alpha = 0.25f), 2 * u, Offset(x + s * 15 * u - 2 * u, yc - 2 * u))
        }
        // La faja de papel con el nombre.
        val faja2 = Rect(L + (R - L) * 0.08f, yc - r, L + (R - L) * 0.6f, yc + r)
        drawRect(crema(CREMA, oscuro, false), faja2.topLeft, faja2.size)
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(faja2.left, yc + r * 0.55f), Size(faja2.width, r * 0.45f))
        listOf(faja2.left + 2 * u, faja2.right - 4 * u).forEach { x -> drawRect(c(AZUL), Offset(x, faja2.top), Size(2 * u, faja2.height)) }
        textoEn(medidor, e.nombre, Rect(faja2.left + 8 * u, yc - r + 2 * u, faja2.right - 8 * u, yc + r - 2 * u), letra, c(TINTA), 15f, peso = FontWeight.Bold, minimo = 7f)
        // La bufanda azul: atada mientras el rollo está cerrado; al abrir, suelta sobre la mesa.
        val xb = L + (R - L) * 0.76f
        val khadag = c(KHADAG)
        if (a < 0.12f) {
            drawRect(khadag, Offset(xb - 8 * u, yc - r - 1 * u), Size(16 * u, 2 * r + 2 * u))
            drawRect(Color.White.copy(alpha = 0.15f), Offset(xb - 6 * u, yc - r - 1 * u), Size(3 * u, 2 * r + 2 * u))
            val nudoB = Offset(xb, yc + r * 0.2f)
            drawPath(Path().apply {
                moveTo(nudoB.x - 4 * u, nudoB.y); cubicTo(nudoB.x - 14 * u, nudoB.y + 26 * u, nudoB.x + 2 * u, nudoB.y + 40 * u, nudoB.x - 8 * u, h * 0.84f)
                lineTo(nudoB.x + 6 * u, h * 0.84f); cubicTo(nudoB.x + 12 * u, nudoB.y + 40 * u, nudoB.x + 2 * u, nudoB.y + 22 * u, nudoB.x + 6 * u, nudoB.y); close()
            }, khadag)
            for (j in 0 until 5) drawLine(khadag, Offset(nudoB.x - 8 * u + j * 3.4f * u, h * 0.84f), Offset(nudoB.x - 8 * u + j * 3.4f * u, h * 0.84f + 5 * u), 0.8f * u)
            drawCircle(mezcla(khadag, Color.Black, 0.2f), 6 * u, nudoB)
            // La tablilla de la cuenta, colgada del nudo por un cordón.
            val tab = Rect(w * 0.34f, h * 0.7f, w * 0.66f, h * 0.81f)
            drawLine(c(ROJO), nudoB, Offset(tab.right - 10 * u, tab.top), 1 * u)
            drawRect(Color.Black.copy(alpha = 0.3f), tab.topLeft + Offset(1.4f * u, 1.8f * u), tab.size)
            drawRect(crema(CREMA, oscuro, false), tab.topLeft, tab.size)
            drawRect(c(AZUL), tab.topLeft, tab.size, style = Stroke(1.6f * u))
            textoEn(medidor, e.cuenta, tab.deflate(4 * u), letra, c(TINTA), 10.5f, minimo = 6f)
        } else {
            drawPath(Path().apply {
                moveTo(R - 6 * u, m1 - 2 * u); cubicTo(R + 6 * u, m1 - 10 * u, R + 20 * u, m1 + 4 * u, R + 30 * u, m1 - 4 * u)
                lineTo(R + 34 * u, m1 + 30 * u); lineTo(R + 22 * u, m1 + 30 * u); cubicTo(R + 18 * u, m1 + 10 * u, R + 4 * u, m1 + 6 * u, R - 6 * u, m1 + 4 * u); close()
            }, khadag)
        }
    }

    /**
     * La pestaña de un día: una ficha de fieltro crema con su pespunte rojo, la letra de la semana en un cartucho azul y,
     * abajo, el disco del color del ánimo con su cerco de oro (sin ánimo, un rombo pequeño de oro).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val ficha = Rect(3 * u, 2 * u, w - 3 * u, h - 2 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), ficha.topLeft + Offset(1.2f * u, 1.6f * u), ficha.size, CornerRadius(5 * u))
        drawRoundRect(crema(CREMA, oscuro, false), ficha.topLeft, ficha.size, CornerRadius(5 * u))
        drawRoundRect(c(ROJO), ficha.deflate(3 * u).topLeft, ficha.deflate(3 * u).size, CornerRadius(3 * u),
            style = Stroke(0.9f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.6f * u))))
        val cart = Rect(ficha.left + 8 * u, ficha.top + 5 * u, ficha.right - 8 * u, ficha.top + 15 * u)
        drawRect(c(AZUL), cart.topLeft, cart.size)
        textoEn(medidor, e.semana, cart, letra, c(ORO), 8.5f, peso = FontWeight.Bold)
        textoEn(medidor, e.dia, Rect(ficha.left, cart.bottom, ficha.right, ficha.bottom - 15 * u), letra, c(TINTA), 18f, peso = FontWeight.Bold, minimo = 10f)
        val o = Offset(w / 2, h - 12 * u)
        if (e.tono != null) {
            drawCircle(e.tono, 5.5f * u, o)
            drawCircle(c(ORO), 5.5f * u, o, style = Stroke(1.2f * u))
        } else {
            drawPath(estrellaRombo(o, 3 * u), c(ORO))
        }
    }

    /**
     * El rótulo de un mes: una tabla pintada de rojo con la greca del martillo en oro arriba y abajo, un nudo infinito de
     * oro sobre azul en cada punta y el cartucho crema de borde azul con el mes.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(ROJO), Offset(0f, 2 * u), Size(w, h - 4 * u))
        grecaMartillo(0f, w, 3 * u, 8 * u, 8 * u, c(ORO), 0.8f * u)
        grecaMartillo(0f, w, h - 8 * u, h - 3 * u, 8 * u, c(ORO), 0.8f * u)
        listOf(14 * u, w - 14 * u).forEach { x ->
            drawRect(c(AZUL), Offset(x - 9 * u, 9 * u), Size(18 * u, h - 18 * u))
            nudo(Offset(x, h / 2), 10 * u, c(ORO), 0.9f * u)
        }
        val cart = Rect(30 * u, 9 * u, w - 30 * u, h - 9 * u)
        drawRect(crema(CREMA, oscuro, false), cart.topLeft, cart.size)
        drawRect(c(AZUL), cart.topLeft, cart.size, style = Stroke(1.4f * u))
        textoEn(medidor, texto, cart.deflate(2 * u), letra, c(TINTA), 13.5f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El ocre dorado de la cera del sello, el cuero del carcaj y el latón de sus bollones. */
    private val CERA_OCRE = Color(0xFFC48A1E)
    private val CUERO_CARCAJ = Color(0xFF7A4226)
    private val LATON_MONGOL = Color(0xFFD8B25A)

    /** El sello del jinete en [o], de radio [r]: la gota de cera ocre con su anillo y el sello cuadrado estampado. */
    private fun DrawScope.selloJinete(o: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        val cera = if (oscuro) mezcla(CERA_OCRE, Color.Black, 0.25f) else CERA_OCRE
        val hondo = mezcla(cera, Color.Black, 0.42f); val claro = mezcla(cera, Color.White, 0.3f)
        translate(r * 0.06f, r * 0.12f) { drawPath(gotaMedievo(o, r, semilla + 5), Color.Black.copy(alpha = 0.3f)) }
        drawPath(gotaMedievo(o, r, semilla + 5), cera)
        drawCircle(hondo, r * 0.8f, o, style = Stroke(r * 0.06f))
        val s = r * 0.95f
        val q = Rect(o.x - s / 2, o.y - s / 2, o.x + s / 2, o.y + s / 2)
        drawRect(hondo, q.topLeft, q.size, style = Stroke(r * 0.08f))
        // Trazos en columnas, como una escritura cuadrada (formas, no letras).
        for (j in 0 until 3) {
            val x = q.left + q.width * (0.25f + 0.25f * j)
            drawLine(hondo, Offset(x, q.top + q.height * 0.18f), Offset(x, q.bottom - q.height * 0.18f), r * 0.07f)
            drawLine(hondo, Offset(x - q.width * 0.08f, q.top + q.height * (0.35f + 0.15f * j)), Offset(x + q.width * 0.08f, q.top + q.height * (0.35f + 0.15f * j)), r * 0.06f)
        }
        drawArc(claro.copy(alpha = 0.6f), 200f, 70f, false, o - Offset(r * 0.86f, r * 0.86f), Size(r * 1.72f, r * 1.72f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /** La paiza: la tablilla de oro del salvoconducto, de alto [alto], colgada en [o] (arriba al medio), con su escritura en columnas. */
    private fun DrawScope.paiza(o: Offset, alto: Float, oscuro: Boolean) {
        val w = alto * 0.42f
        val oro = if (oscuro) mezcla(ORO, Color.Black, 0.25f) else ORO
        val hondo = mezcla(oro, Color.Black, 0.45f)
        val placa = Path().apply {
            moveTo(o.x - w / 2, o.y + alto); lineTo(o.x - w / 2, o.y + w / 2)
            arcTo(Rect(o.x - w / 2, o.y, o.x + w / 2, o.y + w), 180f, 180f, false)
            lineTo(o.x + w / 2, o.y + alto); close()
        }
        translate(w * 0.08f, w * 0.12f) { drawPath(placa, Color.Black.copy(alpha = 0.3f)) }
        drawPath(placa, oro)
        drawPath(placa, hondo, style = Stroke(w * 0.07f))
        drawCircle(hondo, w * 0.13f, Offset(o.x, o.y + w * 0.36f), style = Stroke(w * 0.06f))
        for (j in 0 until 3) {
            val x = o.x + (j - 1) * w * 0.24f
            drawLine(hondo, Offset(x, o.y + w * 0.75f), Offset(x, o.y + alto - w * 0.2f), w * 0.06f)
            for (r in 0 until 3) drawLine(hondo, Offset(x, o.y + w * (0.9f + 0.5f * r + 0.15f * j)), Offset(x + w * 0.07f, o.y + w * (1.0f + 0.5f * r + 0.15f * j)), w * 0.05f)
        }
        drawLine(Color.White.copy(alpha = 0.35f), Offset(o.x - w * 0.32f, o.y + w * 0.5f), Offset(o.x - w * 0.32f, o.y + alto - w * 0.15f), w * 0.05f)
    }

    /**
     * La carta va en el estuche de fieltro del correo de postas: fieltro crema con su ribete azul pespunteado y la greca
     * del martillo, y la solapa redonda con una nube de fieltro rojo aplicada, forrada de azul cielo. La cierra el sello del
     * jinete, de cera ocre con el sello cuadrado estampado, del que cuelga la paiza de oro, el salvoconducto. Las selladas al
     * futuro van además ceñidas por la khadag, la bufanda de seda azul, anudada a un lado con sus flecos. A quién va y las
     * fechas, a tinta sobre el fieltro. Al abrirse el sello se parte, la solapa se levanta enseñando el forro azul y sale la
     * carta. En oscuro el fieltro se hunde y la letra pasa a crema.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0F0B09), 0.6f) else x
        val tinta = if (oscuro) Color(0xFFF0E6CC) else TINTA
        val p = Rect(c.left + W * 0.05f, c.top + H * 0.08f, c.right - W * 0.05f, c.bottom - H * 0.07f)
        val cx = p.center.x
        val largo = p.height * 0.38f
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val sello = Offset(cx, p.top + largo * 0.76f)
        val rs = minOf(W, H) * 0.105f
        val partido = e.romper.coerceIn(0f, 1f)
        fun solapa(g: Float) = Path().apply {
            moveTo(p.left, p.top); lineTo(p.left, p.top + g * largo * 0.2f)
            cubicTo(p.left + p.width * 0.08f, p.top + g * largo * 0.95f, p.right - p.width * 0.08f, p.top + g * largo * 0.95f, p.right, p.top + g * largo * 0.2f)
            lineTo(p.right, p.top); close()
        }
        fun DrawScope.selloEn(g: Float, mitad: Int) {
            val o = Offset(cx, p.top + g * largo * 0.76f)
            conAlfaMedievo(e.alfaSello.coerceIn(0f, 1f), Rect(sello, rs * 4f * e.escalaSello)) {
                scale(e.escalaSello, sello) {
                    if (mitad == 0) {
                        // La paiza cuelga de su cordón, tendida a la derecha del sello.
                        val ojo = o + Offset(rs * 1.15f, 0f)
                        drawLine(k(KHADAG), o, ojo, 1.4f * u)
                        rotate(-90f, ojo) { paiza(ojo - Offset(0f, rs * 0.25f), rs * 2.3f, oscuro) }
                        if (e.brillo > 0f) {
                            drawCircle(Brush.radialGradient(0f to ORO.copy(alpha = 0.8f * e.brillo), 0.45f to ORO.copy(alpha = 0.25f * e.brillo),
                                1f to Color.Transparent, center = o, radius = rs * 2.8f), rs * 2.8f, o)
                            rayosMedievo(o, rs * 1.25f, rs * 2.3f, ORO.copy(alpha = 0.95f * e.brillo), 12, 1.1f)
                        }
                    }
                    when {
                        partido <= 0f -> selloJinete(o, rs, oscuro, e.semilla)
                        mitad == 1 -> clipPath(mitadSelloMedievo(o, rs, true, e.semilla)) {
                            translate(0f, -partido * rs * 0.18f) { rotate(-8f * partido, o) { selloJinete(o, rs, oscuro, e.semilla) } }
                        }
                        else -> clipPath(mitadSelloMedievo(o, rs, false, e.semilla)) {
                            translate(0f, partido * rs * 0.1f) { rotate(6f * partido, o) { selloJinete(o, rs, oscuro, e.semilla) } }
                        }
                    }
                }
            }
        }
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(p.left + 2 * u, p.top + 3 * u), p.size, CornerRadius(6 * u))
        if (giro < 0f) {
            drawPath(solapa(giro), k(Color(0xFF6FA2D4)))
            drawPath(solapa(giro), k(AZUL), style = Stroke(1 * u))
            if (partido > 0f) selloEn(giro, 1)
        }
        val sale = salidaMedievo(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaMedievo(Rect(cx - p.width * 0.42f, p.top + 3 * u - sube, cx + p.width * 0.42f, p.bottom - 6 * u - sube), e, oscuro)
        }
        // El estuche de fieltro: su ribete azul pespunteado y la greca abajo.
        drawRoundRect(k(FIELTRO), p.topLeft, p.size, CornerRadius(6 * u))
        clipRect(p.left, p.top, p.right, p.bottom) {
            for (j in 0 until 40) drawCircle(k(FIELTRO_OSC).copy(alpha = 0.4f), (0.6f + azar(e.semilla, 500 + j)) * u,
                Offset(p.left + p.width * azar(e.semilla, 540 + j), p.top + p.height * azar(e.semilla, 580 + j)))
        }
        drawRoundRect(k(KHADAG), p.topLeft, p.size, CornerRadius(6 * u), style = Stroke(3 * u))
        drawRoundRect(k(FIELTRO), p.topLeft + Offset(1.5f * u, 1.5f * u), Size(p.width - 3 * u, p.height - 3 * u), CornerRadius(5 * u),
            style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 2 * u))))
        val greca = Rect(p.left + 8 * u, p.bottom - 9 * u, p.right - 8 * u, p.bottom - 4 * u)
        grecaMartillo(greca.left, greca.right, greca.top, greca.bottom, 9 * u, k(ROJO), 1.1f * u)
        // Lo escrito, a tinta sobre el fieltro.
        val texto = Rect(p.left + 8 * u, sello.y + rs * 1.25f, p.right - 8 * u, greca.top - 2 * u)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            val m = texto.top + texto.height * 0.56f
            textoEn(medidor, e.linea1, Rect(texto.left, texto.top, texto.right, m), letra, tinta, 12f, peso = FontWeight.SemiBold, minimo = 8f)
            textoEn(medidor, e.linea2, Rect(texto.left, m - 1 * u, texto.right, texto.bottom), letra, if (oscuro) tinta.copy(alpha = 0.85f) else AZUL, 9f, minimo = 7f)
        }
        if (giro >= 0f) {
            translate(0f, 2 * u) { drawPath(solapa(giro), Color.Black.copy(alpha = 0.22f)) }
            drawPath(solapa(giro), k(Color(0xFFF1EAD8)))
            if (giro > 0.3f) clipPath(solapa(giro)) {
                scale(1f, giro, Offset(cx, p.top)) {
                    // La nube de fieltro rojo aplicada, con su pespunte, a los dos lados del sello.
                    listOf(-1f, 1f).forEach { s -> nudo(Offset(cx + s * p.width * 0.27f, p.top + largo * 0.42f), largo * 0.36f, k(ROJO), 1.6f * u) }
                }
            }
            drawPath(solapa(giro), k(KHADAG), style = Stroke(2.2f * u))
        }
        // La khadag: la bufanda azul que ciñe el estuche, anudada a la izquierda, con sus flecos.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val ancho = maxOf(4 * u, H * 0.06f)
            val y = sello.y
            val tramo = (p.width + 6 * u) * ata
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(p.left - 3 * u, y - ancho / 2 + 1.5f * u), Size(tramo, ancho))
            drawRect(k(KHADAG), Offset(p.left - 3 * u, y - ancho / 2), Size(tramo, ancho))
            var x = p.left
            while (x < p.left - 3 * u + tramo - 3 * u) { drawLine(Color.White.copy(alpha = 0.18f), Offset(x, y - ancho / 2), Offset(x + ancho * 0.5f, y + ancho / 2), 0.7f * u); x += 4 * u }
            if (ata > 0.85f) {
                val f = (ata - 0.85f) / 0.15f
                val nudoO = Offset(p.left + p.width * 0.16f, y)
                drawOval(k(mezcla(KHADAG, Color.Black, 0.15f)), Offset(nudoO.x - ancho * 0.7f, y - ancho * 0.7f), Size(ancho * 1.4f, ancho * 1.4f))
                listOf(-1f, 1f).forEach { s ->
                    val fin = nudoO + Offset(s * ancho * 0.9f * f, ancho * 2.6f * f)
                    drawPath(Path().apply { moveTo(nudoO.x - ancho * 0.3f, nudoO.y); lineTo(nudoO.x + ancho * 0.3f, nudoO.y); lineTo(fin.x + ancho * 0.45f, fin.y); lineTo(fin.x - ancho * 0.45f, fin.y); close() }, k(KHADAG))
                    for (j in 0 until 4) drawLine(k(KHADAG), Offset(fin.x - ancho * 0.4f + j * ancho * 0.27f, fin.y), Offset(fin.x - ancho * 0.4f + j * ancho * 0.27f, fin.y + 3 * u * f), 0.7f * u)
                }
            }
        }
        if (partido <= 0f) selloEn(maxOf(giro, 0f), 0)
        else {
            selloEn(1f, 0)
            if (giro >= 0f) selloEn(giro, 1)
        }
    }

    override val cajaPrevia = Rect(0.165f, 0.08f, 0.835f, 0.25f)

    override val altoBuzon: Dp = 210.dp

    /**
     * El buzón es el carcaj de los mensajes del puesto de postas, colgado de su correa en la khana (la celosía de la pared
     * de la ger), bajo el fieltro con su ribete: un carcaj ancho de cuero con clavos de latón y la greca del martillo, donde
     * los mensajes enrollados se guardan de pie, como flechas. Los que ya se pueden abrir llevan la paiza de oro encendida; los
     * sellados, la cinta azul y el lacre ocre; los abiertos, la hoja desenrollada que cuelga. En el frente, tres bollones de
     * latón con la cuenta y su nombre bordado debajo. Abajo, la alfombra roja. En oscuro, la lámpara de manteca alumbra.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0F0B09), 0.58f) else x
        // El fieltro de arriba, con su ribete azul.
        drawRect(k(FIELTRO))
        val khana = Rect(0f, h * 0.17f, w, h * 0.9f)
        drawRect(k(Color(0xFFDCD0B4)), khana.topLeft, khana.size)
        drawRect(k(AZUL), Offset(0f, khana.top - 5 * u), Size(w, 5 * u))
        grecaMartillo(0f, w, h * 0.04f, h * 0.11f, 12 * u, k(ROJO), 1.4f * u)
        // La khana: listones cruzados con sus clavos de cuero.
        clipRect(khana.left, khana.top, khana.right, khana.bottom) {
            val paso = 22 * u
            var x = -khana.height
            while (x < w + khana.height) {
                drawLine(k(mezcla(KHANA, Color.Black, 0.3f)).copy(alpha = 0.5f), Offset(x + 1.5f * u, khana.top), Offset(x + khana.height + 1.5f * u, khana.bottom), 3 * u)
                drawLine(k(mezcla(KHANA, FIELTRO, 0.3f)), Offset(x, khana.top), Offset(x + khana.height, khana.bottom), 2.6f * u)
                drawLine(k(mezcla(KHANA, FIELTRO, 0.3f)), Offset(x + khana.height, khana.top), Offset(x, khana.bottom), 2.6f * u)
                x += paso
            }
            var yy = khana.top + paso / 2
            while (yy < khana.bottom) {
                var xx = ((yy - khana.top) % paso)
                while (xx < w) { drawCircle(k(ROJO_OSC), 1.6f * u, Offset(xx, yy)); xx += paso }
                yy += paso / 2
            }
        }
        val alfombra = h * 0.9f
        drawRect(k(ALFOMBRA), Offset(0f, alfombra), Size(w, h - alfombra))
        grecaMartillo(0f, w, alfombra + 4 * u, h - 3 * u, 10 * u, k(ORO), 1.2f * u)
        // La lámpara de manteca en el suelo, a la derecha.
        val lampara = Offset(w * 0.93f, alfombra - 2 * u)
        if (oscuro) drawRect(Brush.radialGradient(0f to Color(0xFFF7C45A).copy(alpha = 0.32f), 0.45f to Color(0xFFF7C45A).copy(alpha = 0.1f), 1f to Color.Transparent,
            center = lampara - Offset(0f, 20 * u), radius = w * 0.8f))
        drawPath(Path().apply { moveTo(lampara.x - 9 * u, lampara.y - 14 * u); lineTo(lampara.x + 9 * u, lampara.y - 14 * u); lineTo(lampara.x + 4 * u, lampara.y - 6 * u); lineTo(lampara.x + 6 * u, lampara.y); lineTo(lampara.x - 6 * u, lampara.y); lineTo(lampara.x - 4 * u, lampara.y - 6 * u); close() }, k(ORO))
        drawLine(k(mezcla(ORO, Color.Black, 0.4f)), Offset(lampara.x - 9 * u, lampara.y - 14 * u), Offset(lampara.x + 9 * u, lampara.y - 14 * u), 1.2f * u)
        if (oscuro) { haloMedievo(lampara - Offset(0f, 20 * u), 20 * u, Color(0xFFF7C45A), 0.7f); llamaMedievo(lampara - Offset(0f, 15 * u), 10 * u, Color(0xFFF7C45A)) }

        // ------------------------------------------------ el carcaj
        val boca = h * 0.4f
        val carcaj = Path().apply { moveTo(w * 0.1f, boca); lineTo(w * 0.86f, boca); lineTo(w * 0.8f, h * 0.88f); quadraticTo(w * 0.48f, h * 0.93f, w * 0.16f, h * 0.88f); close() }
        // La correa que lo cuelga de la khana.
        val clavo = Offset(w * 0.48f, h * 0.2f)
        drawLine(k(mezcla(CUERO_CARCAJ, Color.Black, 0.3f)), clavo, Offset(w * 0.13f, boca + 4 * u), 3 * u)
        drawLine(k(mezcla(CUERO_CARCAJ, Color.Black, 0.3f)), clavo, Offset(w * 0.83f, boca + 4 * u), 3 * u)
        drawCircle(k(LATON_MONGOL), 3 * u, clavo)
        // Los mensajes enrollados, de pie dentro del carcaj.
        val cartas = repartoMedievo(e, 10)
        val zona = Rect(w * 0.14f, boca, w * 0.82f, boca)
        val paso = zona.width / maxOf(cartas.size, 1)
        val grueso = minOf(16 * u, paso - 4 * u)
        cartas.forEachIndexed { i, clase ->
            val x = zona.left + paso * (i + 0.5f)
            val alto = h * (0.2f + 0.06f * azar(i, 41))
            val top = boca - alto
            rotate((azar(i, 43) - 0.5f) * 10f, Offset(x, boca + 20 * u)) {
                if (clase == 2) {
                    // Abierto: la hoja desenrollada que cae por fuera de la boca.
                    drawRect(k(HOJAS), Offset(x - grueso / 2, top), Size(grueso * 1.7f, alto * 0.55f))
                    drawRect(k(Color(0xFF8A7A58)), Offset(x - grueso / 2, top), Size(grueso * 1.7f, alto * 0.55f), style = Stroke(0.8f * u))
                    for (j in 0 until 4) drawLine(k(TINTA).copy(alpha = 0.45f), Offset(x - grueso * 0.3f + j * grueso * 0.35f, top + 3 * u), Offset(x - grueso * 0.3f + j * grueso * 0.35f, top + alto * 0.48f), 0.7f * u)
                    drawRoundRect(k(mezcla(HOJAS, Color.Black, 0.1f)), Offset(x - grueso / 2, top - 2 * u), Size(grueso * 0.6f, alto + 22 * u), CornerRadius(grueso * 0.3f))
                } else {
                    drawRoundRect(k(HOJAS), Offset(x - grueso / 2, top), Size(grueso, alto + 22 * u), CornerRadius(grueso / 2))
                    drawRoundRect(k(Color(0xFF8A7A58)), Offset(x - grueso / 2, top), Size(grueso, alto + 22 * u), CornerRadius(grueso / 2), style = Stroke(0.8f * u))
                    drawRect(Color.Black.copy(alpha = 0.15f), Offset(x + grueso * 0.15f, top + grueso / 2), Size(grueso * 0.35f, alto))
                    drawOval(k(mezcla(HOJAS, Color.Black, 0.22f)), Offset(x - grueso / 2, top - grueso * 0.2f), Size(grueso, grueso * 0.4f))
                    drawOval(k(mezcla(HOJAS, Color.Black, 0.4f)), Offset(x - grueso * 0.2f, top - grueso * 0.08f), Size(grueso * 0.4f, grueso * 0.16f))
                    val yc = top + alto * 0.4f
                    drawRect(k(if (clase == 1) KHADAG else CINTA), Offset(x - grueso / 2, yc - 2 * u), Size(grueso, 4 * u))
                    if (clase == 1) drawCircle(k(CERA_OCRE), grueso * 0.28f, Offset(x, yc))
                    else {
                        haloMedievo(Offset(x, yc + grueso * 0.6f), grueso * 1.5f, ORO, if (oscuro) 1f else 0.8f)
                        rayosMedievo(Offset(x, yc + grueso * 0.6f), grueso * 0.6f, grueso * 1.2f, ORO, 8, 0.9f)
                        paiza(Offset(x, yc), grueso * 1.4f, oscuro)
                    }
                }
            }
        }
        // El carcaj: cuero con su borde de clavos y la greca.
        translate(3 * u, 4 * u) { drawPath(carcaj, Color.Black.copy(alpha = 0.3f)) }
        drawPath(carcaj, k(CUERO_CARCAJ))
        clipPath(carcaj) {
            for (j in 0 until 12) drawCircle(Color.Black.copy(alpha = 0.05f), h * (0.05f + 0.06f * azar(j, 47)), Offset(w * (0.12f + 0.7f * azar(j, 48)), h * (0.42f + 0.45f * azar(j, 49))))
            drawRect(k(ROJO), Offset(0f, boca), Size(w, 9 * u))
            grecaMartillo(w * 0.1f, w * 0.86f, boca + 2 * u, boca + 7 * u, 8 * u, k(ORO), 1 * u)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, boca + 9 * u), Size(w, 1.5f * u))
        }
        drawPath(carcaj, k(mezcla(CUERO_CARCAJ, Color.Black, 0.45f)), style = Stroke(1.4f * u))
        // Los tres bollones de latón con la cuenta, y su nombre bordado.
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "sellados", e.abiertas to "abiertos")
        rotulos.forEachIndexed { i, (n, nombre) ->
            val o = Offset(w * (0.24f + 0.24f * i), h * 0.62f)
            val r = h * 0.1f
            drawCircle(Color.Black.copy(alpha = 0.3f), r, o + Offset(1.5f * u, 2 * u))
            drawCircle(if (oscuro) mezcla(LATON_MONGOL, Color(0xFF0F0B09), 0.3f) else LATON_MONGOL, r, o)
            drawCircle(mezcla(LATON_MONGOL, Color.Black, 0.35f), r * 0.82f, o, style = Stroke(1 * u))
            for (j in 0 until 12) drawCircle(mezcla(LATON_MONGOL, Color.Black, 0.35f), 0.8f * u, polarMedievo(o, r * 0.91f, j * 30f))
            textoEn(medidor, "$n", Rect(o.x - r * 0.7f, o.y - r * 0.7f, o.x + r * 0.7f, o.y + r * 0.7f), letra, Color(0xFF2A1A0C), 17f, peso = FontWeight.Bold)
            val tira = Rect(o.x - w * 0.1f, o.y + r + 3 * u, o.x + w * 0.1f, o.y + r + 17 * u)
            drawRoundRect(if (oscuro) mezcla(CREMA, Color(0xFF0F0B09), 0.25f) else CREMA, tira.topLeft, tira.size, CornerRadius(2 * u))
            drawRoundRect(AZUL, tira.topLeft, tira.size, CornerRadius(2 * u), style = Stroke(1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.5f * u))))
            textoEn(medidor, nombre, tira.deflate(1.5f * u), letra, TINTA, 9.5f, minimo = 7f)
        }
    }

    // ---------------------------------------------------------------- lo de alrededor

    /** La corteza de abedul del recuerdo. */
    private val ABEDUL = Color(0xFFEFE9DA)
    /** Los cueros de las tapas de las obras y el color de su aplicación: rojo con oro, pardo con crema, azul con oro, verde con crema. */
    private val CUEROS_OBRA = listOf(Color(0xFF6E1C12) to Color(0xFFE0B54A), Color(0xFF6A4A2A) to Color(0xFFF0E6CC),
        Color(0xFF233E66) to Color(0xFFE0B54A), Color(0xFF2E5A3A) to Color(0xFFF0E6CC))

    /** Lo claro de lo de alrededor (el fieltro, la corteza): en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun claroHondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B09), 0.74f) else c
    /** La tinta sobre [claroHondo]. */
    private fun tintaHonda(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA

    /** Los cuernos de carnero (el ugalz): dos volutas que salen de [o] hacia los lados, de ancho [s]; [abajo] las da vuelta. */
    private fun DrawScope.ugalz(o: Offset, s: Float, color: Color, grosor: Float, abajo: Boolean = false) {
        val v = if (abajo) -1f else 1f
        listOf(-1f, 1f).forEach { g ->
            val p = Path().apply {
                moveTo(o.x, o.y + v * s * 0.25f)
                cubicTo(o.x + g * s * 0.08f, o.y - v * s * 0.25f, o.x + g * s * 0.62f, o.y - v * s * 0.42f, o.x + g * s * 0.7f, o.y - v * s * 0.08f)
                cubicTo(o.x + g * s * 0.74f, o.y + v * s * 0.2f, o.x + g * s * 0.42f, o.y + v * s * 0.22f, o.x + g * s * 0.42f, o.y - v * s * 0.02f)
                quadraticTo(o.x + g * s * 0.44f, o.y - v * s * 0.16f, o.x + g * s * 0.56f, o.y - v * s * 0.08f)
            }
            drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round))
        }
    }

    /**
     * El emblema de la pestaña [k] en [o], de radio [r]: el pothi atado (diario), el cuenco de té (momentos), la media luna
     * con su estrella (sueños), el nudo sin fin (ideario) y la carta atada con su cinta (cartas).
     */
    private fun DrawScope.emblema(k: Int, o: Offset, r: Float, c: Color, f: Color) {
        val u = density
        when (k) {
            0 -> {
                drawRoundRect(c, Offset(o.x - r, o.y - r * 0.45f), Size(r * 2f, r * 0.9f), CornerRadius(r * 0.15f))
                drawLine(f, Offset(o.x - r, o.y), Offset(o.x + r, o.y), 0.7f * u)
                drawRect(f, Offset(o.x - r * 0.15f, o.y - r * 0.45f), Size(r * 0.3f, r * 0.9f))
                drawLine(c, Offset(o.x, o.y + r * 0.45f), Offset(o.x + r * 0.2f, o.y + r * 0.95f), 0.9f * u)
            }
            1 -> {
                drawArc(c, 0f, 180f, true, Offset(o.x - r, o.y - r * 0.55f), Size(r * 2f, r * 1.5f))
                drawRect(c, Offset(o.x - r * 0.35f, o.y + r * 0.6f), Size(r * 0.7f, r * 0.28f))
                listOf(-0.3f, 0.1f).forEach { dx -> drawArc(c, 200f, 140f, false, Offset(o.x + dx * r, o.y - r * 1.05f), Size(r * 0.3f, r * 0.7f), style = Stroke(0.7f * u)) }
            }
            2 -> {
                drawPath(mediaLunaMedievo(Offset(o.x - r * 0.15f, o.y + r * 0.05f), r * 0.92f), c)
                drawPath(estrella(Offset(o.x + r * 0.78f, o.y - r * 0.6f), r * 0.36f, 5, 0.45f), c)
            }
            3 -> nudo(o, r * 1.9f, c, 1.1f * u)
            else -> {
                drawRect(c, Offset(o.x - r, o.y - r * 0.62f), Size(r * 2f, r * 1.24f))
                drawLine(f, Offset(o.x - r, o.y - r * 0.62f), Offset(o.x, o.y + r * 0.1f), 0.8f * u)
                drawLine(f, Offset(o.x + r, o.y - r * 0.62f), Offset(o.x, o.y + r * 0.1f), 0.8f * u)
                drawLine(f, Offset(o.x + r * 0.4f, o.y - r * 0.62f), Offset(o.x + r * 0.4f, o.y + r * 0.62f), 1.2f * u)
            }
        }
    }

    override val altoPestanas: Dp = 80.dp

    /**
     * Las pestañas: cinco banderas de los cinco colores (azul, blanco, rojo, verde y amarillo) colgadas de una cuerda tendida,
     * cada una cosida a la cuerda por su dobladillo, con su emblema, su nombre en letra de rótulo y su cuenta. La elegida
     * cuelga más larga, más viva, con orla de oro y el borde de abajo ondeando al viento.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0F0B09), 0.5f) else x
        val telas = listOf(AZUL, FIELTRO, ROJO, VERDE, ORO)
        val claras = listOf(false, true, false, false, true)
        val cuerda = 5 * u
        for (k in 0 until 5) {
            val q = quintoMedievo(k, w, h)
            val sel = k == e.actual
            val tela = c(if (sel) mezcla(telas[k], Color.White, 0.08f) else mezcla(telas[k], Color.Black, 0.12f))
            val abajo = if (sel) h - 4 * u else h - 13 * u
            val x0 = q.left + 3 * u; val x1 = q.right - 3 * u
            val bandera = Path().apply {
                moveTo(x0, cuerda); lineTo(x1, cuerda); lineTo(x1, abajo)
                if (sel) {
                    val n = 4; val paso = (x1 - x0) / n
                    for (j in 0 until n) quadraticTo(x1 - paso * (j + 0.5f), abajo + (if (j % 2 == 0) 3f else -3f) * u, x1 - paso * (j + 1), abajo)
                } else lineTo(x0, abajo)
                close()
            }
            translate(1.6f * u, 1.8f * u) { drawPath(bandera, Color.Black.copy(alpha = 0.2f)) }
            drawPath(bandera, tela)
            // El dobladillo donde pasa la cuerda, y una arruga de la tela.
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(x0, cuerda), Size(x1 - x0, 3.5f * u))
            drawLine(Color.White.copy(alpha = 0.12f), Offset(x0 + (x1 - x0) * 0.3f, cuerda + 4 * u), Offset(x0 + (x1 - x0) * 0.36f, abajo - 2 * u), 1.4f * u)
            drawLine(Color.Black.copy(alpha = 0.08f), Offset(x0 + (x1 - x0) * 0.7f, cuerda + 4 * u), Offset(x0 + (x1 - x0) * 0.66f, abajo - 2 * u), 1.6f * u)
            if (sel) drawPath(bandera, c(if (k == 4) ROJO_OSC else ORO), style = Stroke(1.1f * u))
            val tinta = if (claras[k]) (if (oscuro) mezcla(TINTA, Color.Black, 0.2f) else TINTA) else (if (oscuro) mezcla(CREMA, Color.White, 0.1f) else CREMA)
            emblema(k, Offset((x0 + x1) / 2, cuerda + 15 * u), 6 * u, tinta, tela)
            textoEn(medidor, e.nombres.getOrElse(k) { "" }.uppercase(), Rect(x0 + 2 * u, cuerda + 25 * u, x1 - 2 * u, cuerda + 39 * u), letra, tinta, 10f,
                peso = FontWeight.Bold, espaciado = 0.4f, minimo = 6f)
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", Rect(x0, cuerda + 39 * u, x1, minOf(cuerda + 52 * u, abajo - 1 * u)), letra, tinta.copy(alpha = 0.85f), 10f,
                peso = FontWeight.Bold, minimo = 6f)
        }
        // La cuerda, con su torcido.
        drawLine(c(KHANA), Offset(0f, cuerda), Offset(w, cuerda), 2 * u)
        var x = 0f; var j = 0
        while (x < w && j < 200) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, cuerda - 1 * u), Offset(x + 2 * u, cuerda + 1 * u), 0.5f * u); x += maxOf(3 * u, 1f); j++ }
    }

    override val altoCampo: Dp = 54.dp
    override val cajaCampo = Rect(0.19f, 0.22f, 0.95f, 0.78f)
    override val letraCampo: FontFamily = LetraExtra.filosofo
    override fun tintaCampo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El buscador: una tira de fieltro blanco acolchado, con el cordón rojo cosido en doble filete y sus ganchos en las
     * esquinas; a la izquierda, el eslabón del jinete (la bolsita de cuero con su placa de plata y el acero curvo debajo) y
     * su pedernal. Con el foco saltan las chispas y el cordón se vuelve de oro.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(1.5f * u, 2 * u), Size(w - 1.5f * u, h - 2 * u), CornerRadius(8 * u))
        val fieltro = claroHondo(FIELTRO, oscuro)
        drawRoundRect(fieltro, Offset.Zero, Size(w - 1.5f * u, h - 2 * u), CornerRadius(8 * u))
        // La pelusa del fieltro.
        for (j in 0 until 40) drawCircle(Color.Black.copy(alpha = 0.05f), (0.6f + azar(j, 7)) * u, Offset(w * azar(j, 8), (h - 2 * u) * azar(j, 9)))
        val cordon = if (e.enfocado) c(ORO) else c(ROJO)
        val marco = Rect(4 * u, 4 * u, w - 5.5f * u, h - 6 * u)
        drawRoundRect(cordon, marco.topLeft, marco.size, CornerRadius(6 * u), style = Stroke(1.3f * u))
        drawRoundRect(cordon.copy(alpha = 0.6f), marco.deflate(2.4f * u).topLeft, marco.deflate(2.4f * u).size, CornerRadius(4 * u), style = Stroke(0.8f * u))
        // Los ganchos del cordón en las cuatro esquinas.
        listOf(Offset(marco.left + 9 * u, marco.top + 9 * u) to 0f, Offset(marco.right - 9 * u, marco.top + 9 * u) to 90f,
            Offset(marco.right - 9 * u, marco.bottom - 9 * u) to 180f, Offset(marco.left + 9 * u, marco.bottom - 9 * u) to 270f).forEach { (o, g) ->
            rotate(g, o) { drawArc(cordon, 180f, 270f, false, Offset(o.x - 3 * u, o.y - 3 * u), Size(6 * u, 6 * u), style = Stroke(1 * u)) }
        }
        // El eslabón: la bolsita, la placa y el acero.
        val o = Offset(w * cajaCampo.left * 0.46f, (h - 2 * u) / 2f)
        val bolsa = Path().apply {
            moveTo(o.x - 9 * u, o.y - 12 * u); lineTo(o.x + 9 * u, o.y - 12 * u); lineTo(o.x + 11 * u, o.y + 6 * u); lineTo(o.x - 11 * u, o.y + 6 * u); close()
        }
        drawPath(bolsa, c(CUERO_CARCAJ))
        drawPath(bolsa, Color.Black.copy(alpha = 0.3f), style = Stroke(0.8f * u))
        drawCircle(c(PLATA), 4.5f * u, Offset(o.x, o.y - 4 * u))
        drawCircle(c(ROJO), 1.8f * u, Offset(o.x, o.y - 4 * u))
        drawLine(c(KHANA), Offset(o.x, o.y - 12 * u), Offset(o.x, o.y - 17 * u), 1.2f * u)
        val acero = Path().apply {
            moveTo(o.x - 13 * u, o.y + 5 * u); quadraticTo(o.x, o.y + 13 * u, o.x + 13 * u, o.y + 5 * u)
            lineTo(o.x + 13 * u, o.y + 8 * u); quadraticTo(o.x, o.y + 16 * u, o.x - 13 * u, o.y + 8 * u); close()
        }
        drawPath(acero, c(Color(0xFF55585E)))
        drawLine(Color.White.copy(alpha = 0.3f), Offset(o.x - 9 * u, o.y + 8 * u), Offset(o.x + 9 * u, o.y + 8 * u), 0.6f * u)
        if (e.enfocado) {
            for (j in 0 until 7) {
                val a = 200f + 140f * azar(j, 11)
                val p0 = polarMedievo(Offset(o.x + 12 * u, o.y + 6 * u), 3 * u, a)
                val p1 = polarMedievo(Offset(o.x + 12 * u, o.y + 6 * u), (6 + 6 * azar(j, 12)) * u, a)
                drawLine(c(MANTECA), p0, p1, 0.8f * u)
                drawCircle(c(NARANJA), 0.9f * u, p1)
            }
        }
        if (e.vacio) pistaMedievo(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), letraCampo,
            tintaHonda(oscuro).copy(alpha = 0.5f))
    }

    /**
     * Las obras: la estepa abierta. El cielo alto, unas montañas azules a lo lejos, la hierba y, abajo, la plataforma de
     * tablas del carro, con su viga pintada de rojo y los cubos de las ruedas en las puntas, donde se apoyan. De noche
     * (modo oscuro), el cielo de la estepa con unas estrellas. Distinto del casillero de la ger y de sus arcones.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0C1020), 0.66f) else x
        drawRect(if (oscuro) mezcla(CIELO_NOCHE, Color.Black, 0.2f) else mezcla(CIELO, Color.White, 0.4f))
        if (oscuro) for (j in 0 until 24) drawCircle(ESTRELLA.copy(alpha = 0.3f + 0.4f * azar(j, 21)), (0.4f + 0.6f * azar(j, 22)) * u, Offset(w * azar(j, 23), h * 0.55f * azar(j, 24)))
        val horizonte = h - 50 * u
        // Las montañas lejanas.
        val montes = Path().apply {
            moveTo(0f, horizonte)
            var x = 0f; var j = 0
            while (x < w && j < 30) { val a = (10 + 14 * azar(j, 31)) * u; lineTo(x + 20 * u, horizonte - a); lineTo(x + 46 * u, horizonte - a * 0.4f); x += maxOf(46 * u, 1f); j++ }
            lineTo(w, horizonte); close()
        }
        drawPath(montes, c(mezcla(AZUL, CIELO, 0.55f)).copy(alpha = 0.6f))
        // La hierba.
        drawRect(c(mezcla(VERDE, ORO, 0.35f)), Offset(0f, horizonte), Size(w, h - horizonte))
        var gx = 3 * u; var k = 0
        while (gx < w && k < 120) { drawLine(c(VERDE).copy(alpha = 0.5f), Offset(gx, horizonte + 6 * u), Offset(gx + 1.5f * u, horizonte + 2 * u), 0.7f * u); gx += maxOf(9 * u, 1f); k++ }
        // La plataforma del carro.
        val p0 = h - 30 * u
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, p0 - 3 * u), Size(w, 3 * u))
        drawRect(c(KHANA), Offset(0f, p0), Size(w, 9 * u))
        var tx = 0f; k = 0
        while (tx < w && k < 40) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(tx, p0), Offset(tx, p0 + 9 * u), 0.8f * u); tx += maxOf(34 * u, 1f); k++ }
        drawRect(Color.White.copy(alpha = 0.15f), Offset(0f, p0), Size(w, 1.2f * u))
        val viga = Rect(0f, p0 + 9 * u, w, p0 + 19 * u)
        drawRect(c(ROJO), viga.topLeft, viga.size)
        drawRect(c(ORO), Offset(0f, viga.top + 1.5f * u), Size(w, 1 * u)); drawRect(c(ORO), Offset(0f, viga.bottom - 2.5f * u), Size(w, 1 * u))
        grecaMartillo(viga.left + 40 * u, viga.right - 40 * u, viga.top + 3.5f * u, viga.bottom - 3.5f * u, 6 * u, c(ORO), 0.8f * u)
        drawRect(c(mezcla(KHANA, Color.Black, 0.4f)), Offset(0f, viga.bottom), Size(w, h - viga.bottom))
        // Los cubos de las ruedas, en las puntas.
        listOf(18 * u, w - 18 * u).forEach { cx ->
            val o = Offset(cx, h - 3 * u)
            drawCircle(c(mezcla(KHANA, Color.Black, 0.2f)), 22 * u, o, style = Stroke(4 * u))
            for (j in 0 until 6) drawLine(c(KHANA), o, polarMedievo(o, 21 * u, j * 30f + 180f), 2 * u)
            drawCircle(c(ROJO_OSC), 5 * u, o)
            drawCircle(c(ORO), 2 * u, o)
        }
    }

    /**
     * La tapa de una obra: una cubierta de cuero con aplicación de cuero de otro color (rojo con oro, pardo con crema, azul con
     * oro o verde con crema según la obra): el pespunte que la recorre, los cuernos de carnero arriba y abajo, el tipo en lo
     * alto y, en medio, el paño de fieltro cosido con el título, el autor y el año. En miniatura, el paño con la inicial.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = t(x, oscuro, false)
        val semilla = e.semilla and 0x7fffffff
        val (cuero0, aplic0) = CUEROS_OBRA[semilla % CUEROS_OBRA.size]
        val cuero = c(cuero0); val aplic = c(aplic0)
        drawRect(cuero)
        for (j in 0 until 6) drawOval(Color.Black.copy(alpha = 0.07f), Offset(w * azar(semilla, j), h * azar(semilla, j + 6)), Size(w * 0.2f, w * 0.12f))
        drawRect(Color.White.copy(alpha = 0.06f), Offset(w * 0.08f, 0f), Size(w * 0.18f, h))
        val t1 = maxOf(1f, w * 0.012f)
        // El pespunte.
        val pes = Rect(w * 0.06f, w * 0.06f, w * 0.94f, h - w * 0.06f)
        drawRoundRect(aplic, pes.topLeft, pes.size, CornerRadius(w * 0.05f), style = Stroke(t1, pathEffect = PathEffect.dashPathEffect(floatArrayOf(t1 * 3, t1 * 2))))
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.35f), maxOf(1f, w * 0.02f))
        val pano = if (oscuro) mezcla(CREMA, Color(0xFF0F0B09), 0.2f) else CREMA
        if (e.mini) {
            val r = Rect(w * 0.18f, h * 0.28f, w * 0.82f, h * 0.72f)
            drawRoundRect(aplic, r.inflate(w * 0.04f).topLeft, r.inflate(w * 0.04f).size, CornerRadius(w * 0.08f))
            drawRoundRect(pano, r.topLeft, r.size, CornerRadius(w * 0.06f))
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifBlank { "·" }, r, letraTapa, cuero, s * 0.4f, peso = FontWeight.Bold)
            return
        }
        textoEn(medidor, e.tipo.uppercase(), Rect(w * 0.12f, h * 0.045f, w * 0.88f, h * 0.105f), letra, aplic, s * 0.075f, peso = FontWeight.Bold, espaciado = s * 0.01f, minimo = 4f)
        ugalz(Offset(w / 2, h * 0.215f), w * 0.42f, aplic, maxOf(1.2f, w * 0.035f))
        ugalz(Offset(w / 2, h * 0.865f), w * 0.42f, aplic, maxOf(1.2f, w * 0.035f), abajo = true)
        // El paño de fieltro, con su ribete de la aplicación y sus puntas en cuerno.
        val r = Rect(w * 0.12f, h * 0.27f, w * 0.88f, h * 0.77f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + w * 0.015f, r.top + w * 0.02f), r.size, CornerRadius(w * 0.06f))
        drawRoundRect(aplic, r.topLeft, r.size, CornerRadius(w * 0.06f))
        val dentro = r.deflate(w * 0.03f)
        drawRoundRect(pano, dentro.topLeft, dentro.size, CornerRadius(w * 0.04f))
        drawRoundRect(cuero.copy(alpha = 0.5f), dentro.deflate(w * 0.025f).topLeft, dentro.deflate(w * 0.025f).size, CornerRadius(w * 0.03f),
            style = Stroke(t1 * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(t1 * 2, t1 * 1.5f))))
        val txt = dentro.deflate(w * 0.05f)
        tituloMedievo(medidor, e.titulo.ifBlank { "—" }, Rect(txt.left, txt.top, txt.right, txt.top + txt.height * 0.68f), letraTapa, TINTA, s * 0.11f,
            peso = FontWeight.Bold)
        drawLine(cuero.copy(alpha = 0.6f), Offset(txt.center.x - txt.width * 0.2f, txt.top + txt.height * 0.71f), Offset(txt.center.x + txt.width * 0.2f, txt.top + txt.height * 0.71f), t1)
        textoEn(medidor, e.autor, Rect(txt.left, txt.top + txt.height * 0.74f, txt.right, txt.top + txt.height * 0.87f), letra, TINTA.copy(alpha = 0.85f), s * 0.07f, minimo = 4f)
        textoEn(medidor, e.anio, Rect(txt.left, txt.top + txt.height * 0.87f, txt.right, txt.bottom), letra, mezcla(TINTA, ROJO, 0.6f), s * 0.07f, minimo = 4f)
    }

    override val margenRecuerdo: Dp = 16.dp
    override val altoRotuloRecuerdo: Dp = 48.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tintaHonda(oscuro)

    /**
     * El recuerdo: una tira de corteza de abedul, como las cartas que se escribían en ella, con las orillas rasgadas y las
     * lenticelas (las rayitas oscuras de la corteza) solo arriba y abajo. En lo alto, «HACE N AÑOS» en letra de rótulo, la
     * fecha y el clima; a la derecha, la marca del ánimo: un tamga (la marca del clan: un arco con su trazo) del color del día.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenRecuerdo.toPx(); val alto = altoRotuloRecuerdo.toPx()
        val tira = Rect(2 * u, 1 * u, w - 2 * u, h - 1.5f * u)
        val borde = contornoIrregular(tira, e.semilla, 36, 0.015f)
        translate(1.2f * u, 1.6f * u) { drawPath(borde, Color.Black.copy(alpha = 0.2f)) }
        val corteza = claroHondo(ABEDUL, oscuro)
        drawPath(borde, corteza)
        val raya = if (oscuro) Color(0xFF4A4038) else Color(0xFF3A2E24)
        clipPath(borde) {
            // Las lenticelas: rayitas horizontales, solo en las orillas de arriba y abajo.
            for (j in 0 until 26) {
                val arriba = j % 2 == 0
                val x = w * azar(e.semilla, j + 40)
                val y = if (arriba) (2 + 4 * azar(e.semilla, j + 70)) * u else h - (3 + 4 * azar(e.semilla, j + 70)) * u
                drawLine(raya.copy(alpha = 0.55f), Offset(x, y), Offset(x + (5 + 10 * azar(e.semilla, j + 90)) * u, y), (0.8f + 0.6f * azar(e.semilla, j)) * u)
            }
            drawRect(Color(0xFFB08A5A).copy(alpha = 0.07f), Offset(0f, h - 10 * u), Size(w, 10 * u))
        }
        val tinta = tintaHonda(oscuro)
        izquierdaMedievo(medidor, haceMedievo(e.anios).uppercase(), m, 8 * u, 27 * u, w - 2 * m - 26 * u, letra, if (oscuro) CREMA else mezcla(TINTA, ROJO_OSC, 0.45f), 15f,
            peso = FontWeight.Bold, espaciado = 1f)
        val clima = climaMedievo(e.clima)
        izquierdaMedievo(medidor, if (clima.isEmpty()) e.fecha else "${e.fecha} · $clima", m, 27 * u, alto - 5 * u, w - 2 * m - 22 * u, letra,
            tinta.copy(alpha = 0.85f), 12f, minimo = 8f)
        drawLine(tinta.copy(alpha = 0.25f), Offset(m, alto - 3 * u), Offset(w - m, alto - 3 * u), 0.7f * u)
        e.animo?.let { a ->
            val o = Offset(w - m - 7 * u, 15 * u)
            // Un arco abierto abajo, atravesado por un trazo que baja, con su travesaño: una marca, no una letra.
            drawArc(a, 180f, 180f, false, Offset(o.x - 7 * u, o.y - 5 * u), Size(14 * u, 12 * u), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            drawLine(a, Offset(o.x, o.y - 5 * u), Offset(o.x, o.y + 12 * u), 2.4f * u, cap = StrokeCap.Round)
            drawLine(a, Offset(o.x - 3.5f * u, o.y + 7 * u), Offset(o.x + 3.5f * u, o.y + 9 * u), 2.4f * u, cap = StrokeCap.Round)
        }
    }
}

// =====================================================================
//  Ayudantes de la página abierta (Edad Media)
// =====================================================================

/**
 * La fecha del encabezado dentro de [r]: en un renglón si cabe (o casi: [textoEn] la achica un poco); si es larga y tiene
 * « · », en dos renglones del mismo tamaño (el que haga falta para que quepa el más largo), partidos por el « · » que los deje más parejos.
 */
private fun DrawScope.fechaMedievo(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                   peso: FontWeight = FontWeight.Normal) {
    val largo = medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso), maxLines = 1, softWrap = false).size.width
    val partes = texto.split(" · ")
    if (largo <= r.width * 1.15f || partes.size < 2) {
        textoEn(medidor, texto, r, letra, color, tam, peso = peso, minimo = 9f)
        return
    }
    var corte = 1
    var mejor = Int.MAX_VALUE
    for (k in 1 until partes.size) {
        val d = kotlin.math.abs(partes.take(k).joinToString(" · ").length - partes.drop(k).joinToString(" · ").length)
        if (d < mejor) { mejor = d; corte = k }
    }
    val uno = partes.take(corte).joinToString(" · ")
    val dos = partes.drop(corte).joinToString(" · ")
    // Los dos renglones, del mismo tamaño: el que haga falta para que quepa el más largo.
    val estilo = TextStyle(fontFamily = letra, fontSize = (tam * 0.8f).sp, fontWeight = peso)
    val ancho = maxOf(medidor.measure(uno, estilo, maxLines = 1, softWrap = false).size.width, medidor.measure(dos, estilo, maxLines = 1, softWrap = false).size.width)
    val chico = maxOf(9f, tam * 0.8f * minOf(1f, r.width / ancho.coerceAtLeast(1)))
    val m = r.top + r.height / 2
    textoEn(medidor, uno, Rect(r.left, r.top, r.right, m), letra, color, chico, peso = peso, minimo = 9f)
    textoEn(medidor, dos, Rect(r.left, m, r.right, r.bottom), letra, color, chico, peso = peso, minimo = 9f)
}

/** El color del rótulo del encabezado: el acento del papel; de noche, más cerca de la tinta, para que se lea sobre el cielo. */
private fun rotuloMedievo(p: Pagina, noche: Boolean): Color = if (noche) mezcla(p.acento, p.tinta, 0.35f) else p.acento

/**
 * El modo oscuro sobre un papel claro: la penumbra con la luz del tema. Todo el papel baja un 18 %; desde el centro del
 * texto hacia las orillas baja hasta un 62 % más; y la luz, desde [fuente], tiñe y aclara su rincón con su color [luz].
 * En el centro queda como papel algo atenuado (en promedio, como un velo de un 30 %) y la tinta oscura se sigue leyendo.
 * Son los dos degradados de la página.
 */
private fun DrawScope.penumbraMedievo(fuente: Offset, luz: Color, fuerza: Float = 1f) {
    val w = size.width; val h = size.height
    drawRect(Color.Black.copy(alpha = 0.18f))
    drawRect(Brush.radialGradient(0f to Color.Transparent, 0.42f to Color.Black.copy(alpha = 0.05f), 1f to Color.Black.copy(alpha = 0.62f),
        center = Offset(w / 2, h * 0.4f), radius = kotlin.math.hypot(w, h) * 0.6f))
    drawRect(Brush.radialGradient(0f to luz.copy(alpha = 0.3f * fuerza), 0.3f to luz.copy(alpha = 0.12f * fuerza), 1f to Color.Transparent,
        center = fuente, radius = maxOf(w, h) * 0.6f))
}

/** Un resplandor sin degradado: discos concéntricos, cada uno más chico, que se suman hacia el centro. */
private fun DrawScope.haloMedievo(o: Offset, r: Float, color: Color, fuerza: Float = 0.5f) {
    for (k in 5 downTo 1) drawCircle(color.copy(alpha = fuerza * 0.14f), r * k / 5f, o)
}

/** Una llama de [s] de alto con la base en [o], con su corazón claro. */
private fun DrawScope.llamaMedievo(o: Offset, s: Float, color: Color) {
    fun gota(k: Float) = Path().apply {
        moveTo(o.x, o.y - s * k)
        quadraticTo(o.x + s * 0.42f * k, o.y - s * 0.32f * k, o.x, o.y)
        quadraticTo(o.x - s * 0.42f * k, o.y - s * 0.32f * k, o.x, o.y - s * k)
        close()
    }
    drawPath(gota(1f), color)
    drawPath(gota(0.55f), Color.White.copy(alpha = 0.75f))
}

/** Una media luna de radio [r] con centro en [o] (el hueco, corrido arriba a la derecha). */
private fun mediaLunaMedievo(o: Offset, r: Float): Path {
    val a = Path().apply { addOval(Rect(o, r)) }
    val b = Path().apply { addOval(Rect(Offset(o.x + r * 0.45f, o.y - r * 0.2f), r * 0.85f)) }
    return Path().apply { op(a, b, PathOperation.Difference) }
}

// =====================================================================
//  Ayudantes de las cartas
// =====================================================================

/**
 * Cuánto ha salido la carta (0 a 1) según [EstadoCarta.subir]: sale entera con la mitad (la rejilla la deja en 0.5 y el
 * diálogo llega a 1), con el final suave.
 */
private fun salidaMedievo(subir: Float): Float {
    val x = (subir * 2f).coerceIn(0f, 1f)
    return 1f - (1f - x) * (1f - x)
}

/** El punto a [r] de [o] en la dirección [grados] (0 a la derecha, 90 abajo). */
private fun polarMedievo(o: Offset, r: Float, grados: Float): Offset {
    val a = Math.toRadians(grados.toDouble())
    return Offset(o.x + r * kotlin.math.cos(a).toFloat(), o.y + r * kotlin.math.sin(a).toFloat())
}

/** Una gota de cera de radio [r] en [o], con el borde un poco irregular (siempre el mismo para la misma [semilla]). */
private fun gotaMedievo(o: Offset, r: Float, semilla: Int, puntos: Int = 22): Path = Path().apply {
    for (j in 0..puntos) {
        val p = polarMedievo(o, r * (0.93f + 0.12f * azar(semilla, j % puntos)), j * 360f / puntos)
        if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/**
 * La raja de un sello que se parte, de lado a lado de [o] (radio [r]): devuelve la mitad de arriba ([arriba]) o la de abajo
 * como región de recorte, con el filo en zigzag.
 */
private fun mitadSelloMedievo(o: Offset, r: Float, arriba: Boolean, semilla: Int): Path = Path().apply {
    val n = 7
    moveTo(o.x - r * 1.6f, o.y)
    for (j in 0..n) {
        val x = o.x - r * 1.1f + 2.2f * r * j / n
        lineTo(x, o.y + r * (if (j % 2 == 0) -0.12f else 0.14f) * (0.6f + 0.8f * azar(semilla, j)))
    }
    lineTo(o.x + r * 1.6f, o.y)
    val y = if (arriba) o.y - r * 3f else o.y + r * 3f
    lineTo(o.x + r * 1.6f, y); lineTo(o.x - r * 1.6f, y); close()
}

/**
 * La hoja de la carta en [r]: el papel [EstadoCarta.hoja] con sus renglones [EstadoCarta.renglones] cada 11 dp (el primero a
 * 10 dp de [arriba], bajo la primera línea que escribe la pantalla), una sombra corta y, si [oscuro], un velo leve que no
 * apaga la letra. [barbas]: el borde irregular del pergamino.
 */
private fun DrawScope.hojaCartaMedievo(r: Rect, e: EstadoCarta, oscuro: Boolean, barbas: Boolean = false, arriba: Float = 8f, margen: Float = 7f) {
    if (r.height < 1f || r.width < 1f) return
    val u = density
    val forma = if (barbas) contornoIrregular(r, e.semilla, 30, 0.012f) else Path().apply { addRect(r) }
    translate(1.2f * u, 1.6f * u) { drawPath(forma, Color.Black.copy(alpha = 0.25f)) }
    drawPath(forma, if (oscuro) mezcla(e.hoja, Color.Black, 0.12f) else e.hoja)
    clipPath(forma) {
        var y = r.top + (arriba + 10f) * u
        while (y < r.bottom - 3 * u) {
            drawLine(e.renglones, Offset(r.left + margen * u, y), Offset(r.right - margen * u, y), 0.8f * u)
            y += 11 * u
        }
        drawRect(Color.Black.copy(alpha = 0.07f), Offset(r.right - 3 * u, r.top), Size(3 * u, r.height))
    }
}

/**
 * Qué cartas dibuja el buzón, como mucho [maximo]: 0 = lista, 1 = sellada, 2 = abierta, en ese orden. Cada clase que tenga
 * alguna sale al menos una vez; el resto del cupo se reparte en proporción.
 */
private fun repartoMedievo(e: EstadoBuzon, maximo: Int): List<Int> {
    val n = intArrayOf(e.listas.coerceAtLeast(0), e.selladas.coerceAtLeast(0), e.abiertas.coerceAtLeast(0))
    if (n.sum() <= maximo) return (0..2).flatMap { k -> List(n[k]) { k } }
    val cupo = IntArray(3) { if (n[it] > 0) 1 else 0 }
    repeat(maximo - cupo.sum()) {
        val k = (0..2).maxByOrNull { if (n[it] > cupo[it]) n[it].toFloat() / (cupo[it] + 1) else -1f }!!
        cupo[k]++
    }
    return (0..2).flatMap { k -> List(cupo[k]) { k } }
}

/** Unos rayos finos alrededor de [o] (el brillo de lo que ya se puede abrir), sin degradado. */
private fun DrawScope.rayosMedievo(o: Offset, r0: Float, r1: Float, color: Color, n: Int = 8, grosor: Float = 1f) {
    for (j in 0 until n) {
        val g = j * 360f / n + 360f / n / 2
        drawLine(color, polarMedievo(o, r0, g), polarMedievo(o, if (j % 2 == 0) r1 else r0 + (r1 - r0) * 0.6f, g), grosor * density, StrokeCap.Round)
    }
}

/** Dibuja [bloque] con opacidad [alfa], en una capa aparte (para que no se transparente pieza por pieza). */
private inline fun DrawScope.conAlfaMedievo(alfa: Float, limite: Rect, bloque: DrawScope.() -> Unit) {
    if (alfa >= 0.999f) { bloque(); return }
    drawContext.canvas.saveLayer(limite, Paint().apply { this.alpha = alfa })
    bloque()
    drawContext.canvas.restore()
}

// =====================================================================
//  Ayudantes de lo de alrededor (Edad Media): pestañas, buscador, obras y recuerdos
// =====================================================================

/**
 * El título de una obra partido en renglones por palabras: uno si es corto (hasta 12 letras), dos hasta 26 y si no tres
 * (nunca más que [maximo] ni que palabras haya), con los cortes donde los renglones quedan más parejos. Lo que no quepa lo
 * achica [textoEn] en cada renglón.
 */
private fun renglonesMedievo(titulo: String, maximo: Int = 3): List<String> {
    val palabras = titulo.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (palabras.isEmpty()) return listOf("—")
    val total = palabras.sumOf { it.length } + palabras.size - 1
    val n = (if (total <= 12) 1 else if (total <= 26) 2 else 3).coerceAtMost(maximo).coerceAtMost(palabras.size)
    val meta = total / n.toFloat()
    val r = mutableListOf<String>()
    var actual = ""
    for (p in palabras) {
        val junto = if (actual.isEmpty()) p else "$actual $p"
        if (actual.isNotEmpty() && r.size < n - 1 && junto.length > meta && junto.length - meta > meta - actual.length) { r += actual; actual = p }
        else actual = junto
    }
    r += actual
    return r
}

/**
 * Los renglones del título de una obra dentro de [r], uno debajo de otro y centrados, con [textoEn] (que achica el que no
 * cabe). El tamaño baja un poco si son tres.
 */
private fun DrawScope.tituloMedievo(medidor: TextMeasurer, titulo: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                    peso: FontWeight = FontWeight.Normal, maximo: Int = 3) {
    val l = renglonesMedievo(titulo, maximo)
    val alto = r.height / l.size
    val k = if (l.size == 3) 0.86f else if (l.size == 2) 0.94f else 1f
    l.forEachIndexed { i, s -> textoEn(medidor, s, Rect(r.left, r.top + alto * i, r.right, r.top + alto * (i + 1)), letra, color, tam * k, peso = peso, minimo = 5f) }
}

/**
 * Un texto de un renglón alineado a la izquierda, desde [x] y hasta [x] + [maximo] como mucho, centrado en alto entre
 * [arriba] y [abajo]: se mide a [tam] sp y se le da a [textoEn] un recuadro de su ancho (si no cabe, lo achica).
 * Devuelve dónde acabó (para poner algo detrás).
 */
private fun DrawScope.izquierdaMedievo(medidor: TextMeasurer, texto: String, x: Float, arriba: Float, abajo: Float, maximo: Float, letra: FontFamily,
                                   color: Color, tam: Float, peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f, minimo: Float = 8f): Float {
    if (texto.isBlank() || maximo <= 1f) return x
    val capa = medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false)
    val ancho = minOf(capa.size.width + 2f, maximo)
    textoEn(medidor, texto, Rect(x, arriba, x + ancho, abajo), letra, color, tam, peso = peso, espaciado = espaciado, minimo = minimo)
    return x + ancho
}

/** La pista del buscador, alineada a la izquierda de [caja] como lo que se escribe (a 16 sp si cabe, si no [textoEn] la achica). */
private fun DrawScope.pistaMedievo(medidor: TextMeasurer, pista: String, caja: Rect, letra: FontFamily, color: Color,
                                   peso: FontWeight = FontWeight.Normal) {
    izquierdaMedievo(medidor, pista, caja.left, caja.top, caja.bottom, caja.width, letra, color, 16f, peso = peso, minimo = 9f)
}

/** «Hace un año» o «Hace N años». */
private fun haceMedievo(anios: Int): String = if (anios == 1) "Hace un año" else "Hace $anios años"

/** El clima sin el signo del principio (☀, ☂, ❄…), que las letras del tema no tienen: «lluvia 18°». */
private fun climaMedievo(clima: String): String = clima.dropWhile { !it.isLetterOrDigit() }.trim()

/** El rectángulo de la pestaña [k] (de cinco) en un lienzo de ancho [w] y alto [h]: exactamente una quinta parte. */
private fun quintoMedievo(k: Int, w: Float, h: Float): Rect = Rect(w * k / 5f, 0f, w * (k + 1) / 5f, h)
