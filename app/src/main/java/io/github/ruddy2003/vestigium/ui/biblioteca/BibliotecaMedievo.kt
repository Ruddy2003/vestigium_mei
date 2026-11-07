package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
