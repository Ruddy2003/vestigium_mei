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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.sombraInterior
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Edad Moderna
// =====================================================================

/**
 * Japón Edo: el hondana de paulonia (kiri), claro y de veta recta, con postes de laca negra. Arriba, el armarito de puertas
 * correderas (fukurodana) con nubes de pan de oro y olas seigaiha, y una ventana redonda (marumado) de shōji: de día con la
 * sombra de un bambú, de noche con la luna detrás del papel. Encima, una pila de libros cosidos y un rollo atado. Cada
 * entrada es un estuche de tela (chitsu) de pie, con sus cierres de hueso (kohaze), la tira de papel (daisen) con el día y
 * el mes, y el sello bermellón; de noche, una luna en lugar del sello. Distinto del Ideario, que es el tansu de keyaki
 * sobre el tatami.
 */
object EstanteEdo : DisenoBiblioteca {
    private val KIRI = Color(0xFFDCC7A1)
    private val KIRI_LUZ = Color(0xFFEBDDBF)
    private val KIRI_SOMBRA = Color(0xFFB39770)
    private val KIRI_VETA = Color(0xFF9E825A)
    private val URUSHI = Color(0xFF231C18)
    private val URUSHI_LUZ = Color(0xFF4F4137)
    private val FUSUMA = Color(0xFFEFE6CF)
    private val KINPAKU = Color(0xFFCFAE58)
    private val NAMI = Color(0xFF3D5F86)
    private val SUMI = Color(0xFF1B1A1E)
    private val BERMELLON = Color(0xFFB8322A)
    private val ZOGE = Color(0xFFF1E8D2)
    private val SHOJI = Color(0xFFF6EFDC)
    private val BAMBU = Color(0xFF55663E)
    private val TABLON = Color(0xFF8E6B45)
    /** Las telas de los estuches: índigo, verde pino, castaño y violeta de Edo. */
    private val TELAS = listOf(Color(0xFF1E3A5F), Color(0xFF2E4A3A), Color(0xFF5A2E2A), Color(0xFF3F3556), Color(0xFF26404F))
    private val NOCHE = Color(0xFF141B33)
    private val LUNA = Color(0xFFEDE6C8)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la luna. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF120E0B), 0.32f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    override val corona = 0.24f
    override val base = 0.1f
    override val lado = 0.045f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.shippori

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.15f * azar(semilla, 1)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo del cuerpo: tablas de kiri a lo alto, en sombra.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(KIRI_SOMBRA), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(KIRI_VETA).copy(alpha = 0.45f), 11, densidad = 0.7f, vertical = true, grosor = 0.8f)
        var x = cuerpo.left + 46 * u
        while (x < cuerpo.right) { drawLine(c(KIRI_VETA).copy(alpha = 0.5f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 0.8f * u); x += 46 * u }
        // Cada repisa: la sombra de la tabla de arriba y de los costados.
        d.repisas.forEach { r -> sombraInterior(r, 10 * u, if (noche) 0.4f else 0.3f) }
        // Los costados: tabla de kiri con su poste de laca por fuera.
        listOf(d.izquierda to true, d.derecha to false).forEach { (r, izq) ->
            drawRect(c(KIRI), r.topLeft, r.size)
            vetas(r, c(KIRI_VETA).copy(alpha = 0.55f), if (izq) 3 else 4, densidad = 1.2f, vertical = true, grosor = 0.7f)
            val poste = r.width * 0.34f
            val px = if (izq) r.left else r.right - poste
            drawRect(c(URUSHI), Offset(px, r.top), Size(poste, r.height))
            drawLine(c(URUSHI_LUZ), Offset(px + poste * 0.3f, r.top), Offset(px + poste * 0.3f, r.bottom), 0.8f * u)
            bisel(r, Color.White.copy(alpha = 0.18f), Color.Black.copy(alpha = 0.18f), 1.2f * u)
        }
        // Las tablas, con su canto claro y un hilo de laca debajo.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(KIRI), r.topLeft, r.size)
            vetas(r, c(KIRI_VETA).copy(alpha = 0.5f), 20 + k, densidad = 0.6f, grosor = 0.7f)
            drawRect(c(KIRI_LUZ), r.topLeft, Size(r.width, 2.2f * u))
            drawRect(c(URUSHI), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una tira de washi pegada en el canto, con su sello.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.14f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
            drawRect(c(SHOJI), r.topLeft, r.size)
            drawRect(c(KIRI_VETA), r.topLeft, r.size, style = Stroke(0.7f * u))
            val s = r.height * 0.62f
            val sello = Rect(r.right - s - r.height * 0.22f, r.center.y - s / 2, r.right - r.height * 0.22f, r.center.y + s / 2)
            drawRect(c(BERMELLON), sello.topLeft, sello.size)
            drawRect(c(SHOJI).copy(alpha = 0.8f), sello.deflate(s * 0.2f).topLeft, sello.deflate(s * 0.2f).size, style = Stroke(0.7f * u))
            textoEn(medidor, p.texto, Rect(r.left + 4 * u, r.top, sello.left - 3 * u, r.bottom), letra, c(SUMI), 11f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo con su recorte en arco (kurikata), los pies y el piso de tablones del engawa.
        val b = d.base
        val piso = b.top + b.height * 0.62f
        drawRect(c(TABLON), Offset(0f, piso), Size(w, h - piso))
        var y = piso + 4 * u
        while (y < h) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(0f, y), Offset(w, y), 0.8f * u); y += 7 * u }
        vetas(Rect(0f, piso, w, h), Color.Black.copy(alpha = 0.18f), 31, densidad = 0.8f, grosor = 0.6f)
        val zocalo = Path().apply {
            moveTo(b.left, b.top); lineTo(b.right, b.top); lineTo(b.right, piso)
            lineTo(b.right - b.width * 0.08f, piso)
            cubicTo(b.right - b.width * 0.12f, b.top + b.height * 0.34f, b.left + b.width * 0.12f, b.top + b.height * 0.34f, b.left + b.width * 0.08f, piso)
            lineTo(b.left, piso); close()
        }
        drawPath(zocalo, c(KIRI))
        clipPath(zocalo) { vetas(b, c(KIRI_VETA).copy(alpha = 0.5f), 41, densidad = 0.8f, grosor = 0.7f) }
        drawPath(zocalo, c(URUSHI), style = Stroke(1.6f * u))
        drawRect(c(URUSHI), Offset(b.left, b.top), Size(b.width, 3 * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(b.left + b.width * 0.08f, piso), Size(b.width * 0.84f, 3 * u))
    }

    /** El armarito de arriba, la ventana redonda y lo que hay encima. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val arriba = r.top + r.height * 0.24f
        // El remate (kasagi): una viga de laca que se alza en las puntas.
        val viga = Path().apply {
            moveTo(r.left, arriba - r.height * 0.07f)
            quadraticTo(r.left + r.width * 0.06f, arriba, r.left + r.width * 0.16f, arriba)
            lineTo(r.right - r.width * 0.16f, arriba)
            quadraticTo(r.right - r.width * 0.06f, arriba, r.right, arriba - r.height * 0.07f)
            lineTo(r.right, arriba + r.height * 0.07f); lineTo(r.left, arriba + r.height * 0.07f); close()
        }
        drawPath(viga, c(URUSHI))
        drawLine(c(KINPAKU), Offset(r.left + r.width * 0.16f, arriba + r.height * 0.03f), Offset(r.right - r.width * 0.16f, arriba + r.height * 0.03f), 0.9f * u)
        // Encima: la pila de libros cosidos y el rollo atado con su cordón rojo.
        val y0 = arriba - r.height * 0.005f
        listOf(Triple(0.1f, 0.2f, TELAS[0]), Triple(0.105f, 0.19f, ZOGE), Triple(0.095f, 0.2f, TELAS[2])).forEachIndexed { k, (x0, an, col) ->
            val alto = r.height * 0.045f
            val top = y0 - alto * (k + 1)
            drawRect(c(col), Offset(r.left + r.width * x0, top), Size(r.width * an, alto))
            drawRect(c(ZOGE).copy(alpha = 0.85f), Offset(r.left + r.width * (x0 + an * 0.86f), top), Size(r.width * an * 0.04f, alto))
            drawLine(c(SUMI).copy(alpha = 0.5f), Offset(r.left + r.width * x0, top + alto), Offset(r.left + r.width * (x0 + an), top + alto), 0.6f * u)
        }
        val rollo = Rect(r.left + r.width * 0.7f, y0 - r.height * 0.075f, r.left + r.width * 0.86f, y0)
        drawRoundRect(c(FUSUMA), rollo.topLeft, rollo.size, CornerRadius(rollo.height / 2))
        drawOval(c(KIRI_VETA), Offset(rollo.right - rollo.height * 0.5f, rollo.top), Size(rollo.height * 0.5f, rollo.height))
        drawLine(c(BERMELLON), Offset(rollo.center.x, rollo.top), Offset(rollo.center.x, rollo.bottom), 1.6f * u)
        drawLine(c(BERMELLON), Offset(rollo.center.x, rollo.bottom), Offset(rollo.center.x - 3 * u, rollo.bottom + 4 * u), 1.1f * u)
        // El cuerpo del armarito, con su marco de laca.
        val caja = Rect(r.left + r.width * 0.025f, arriba + r.height * 0.07f, r.right - r.width * 0.025f, r.bottom - r.height * 0.04f)
        drawRect(c(KIRI), caja.topLeft, caja.size)
        vetas(caja, c(KIRI_VETA).copy(alpha = 0.45f), 51, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(URUSHI), caja.topLeft, caja.size, style = Stroke(3 * u))
        // Las dos puertas correderas (fusuma), con nubes de oro y olas.
        val puertas = Rect(caja.left + 6 * u, caja.top + 6 * u, caja.left + caja.width * 0.64f, caja.bottom - 6 * u)
        val mitad = puertas.width / 2
        for (k in 0..1) {
            val p = Rect(puertas.left + mitad * k + (if (k == 1) -2 * u else 0f), puertas.top, puertas.left + mitad * (k + 1) + (if (k == 0) 2 * u else 0f), puertas.bottom)
            drawRect(c(FUSUMA), p.topLeft, p.size)
            clipRect(p.left, p.top, p.right, p.bottom) {
                // Nubes de pan de oro (suyarigasumi): bandas redondeadas a través de la puerta.
                listOf(0.18f to 0.5f, 0.42f to 0.36f).forEachIndexed { j, (fy, an) ->
                    val cy = p.top + p.height * fy
                    val x0 = p.left + p.width * (if ((j + k) % 2 == 0) -0.1f else 0.3f)
                    drawRoundRect(c(KINPAKU).copy(alpha = 0.75f), Offset(x0, cy), Size(p.width * (an + 0.5f), p.height * 0.13f), CornerRadius(p.height * 0.065f))
                }
                // Las olas (seigaiha) abajo: arcos concéntricos en filas.
                val ra = p.height * 0.13f
                var fila = 0
                var yy = p.bottom - ra * 0.2f
                while (yy > p.top + p.height * 0.62f) {
                    var xx = p.left + (if (fila % 2 == 0) 0f else ra)
                    while (xx < p.right + ra) {
                        for (anillo in 3 downTo 1) {
                            val rr = ra * anillo / 3f
                            drawArc(if (anillo % 2 == 1) c(NAMI) else c(FUSUMA), 180f, 180f, true, Offset(xx - rr, yy - rr), Size(rr * 2, rr * 2))
                        }
                        xx += ra * 2
                    }
                    yy -= ra * 0.55f; fila++
                }
            }
            drawRect(c(URUSHI), p.topLeft, p.size, style = Stroke(1.6f * u))
            // El tirador hundido (hikite).
            val hx = if (k == 0) p.right - p.width * 0.14f else p.left + p.width * 0.14f
            drawCircle(c(URUSHI), p.height * 0.07f, Offset(hx, p.center.y))
            drawCircle(c(KINPAKU), p.height * 0.045f, Offset(hx, p.center.y), style = Stroke(0.8f * u))
        }
        // La ventana redonda de shōji.
        val rv = minOf(caja.height * 0.42f, caja.width * 0.15f)
        val cv = Offset(caja.left + caja.width * 0.82f, caja.center.y)
        val circulo = Path().apply { addOval(Rect(cv, rv)) }
        val papel = when {
            noche -> mezcla(SHOJI, Color(0xFF2A3354), 0.55f)
            oscuro -> mezcla(SHOJI, Color(0xFF15120F), 0.45f)
            else -> SHOJI
        }
        drawPath(circulo, papel)
        clipPath(circulo) {
            if (noche) {
                // La luna detrás del papel.
                mancha(Offset(cv.x + rv * 0.2f, cv.y - rv * 0.15f), rv * 0.9f, LUNA.copy(alpha = 0.55f))
                drawCircle(LUNA.copy(alpha = 0.85f), rv * 0.36f, Offset(cv.x + rv * 0.2f, cv.y - rv * 0.15f))
            } else {
                // La sombra de una rama de bambú, del otro lado del papel.
                val sombra = BAMBU.copy(alpha = if (oscuro) 0.25f else 0.32f)
                drawLine(sombra, Offset(cv.x - rv * 0.7f, cv.y + rv), Offset(cv.x - rv * 0.2f, cv.y - rv), 2.2f * u)
                listOf(-0.4f, 0f, 0.35f).forEachIndexed { j, f ->
                    val o = Offset(cv.x - rv * 0.45f + f * rv * 0.3f, cv.y + f * rv)
                    val hoja = Path().apply {
                        moveTo(o.x, o.y)
                        quadraticTo(o.x + rv * 0.3f, o.y - rv * (0.18f + 0.04f * j), o.x + rv * 0.62f, o.y - rv * 0.05f)
                        quadraticTo(o.x + rv * 0.3f, o.y + rv * 0.04f, o.x, o.y); close()
                    }
                    drawPath(hoja, sombra)
                }
            }
            // La celosía del shōji.
            val paso = rv / 2.2f
            var gx = cv.x - rv
            while (gx < cv.x + rv) { drawLine(c(KIRI_VETA), Offset(gx, cv.y - rv), Offset(gx, cv.y + rv), 1.2f * u); gx += paso }
            var gy = cv.y - rv
            while (gy < cv.y + rv) { drawLine(c(KIRI_VETA), Offset(cv.x - rv, gy), Offset(cv.x + rv, gy), 1.2f * u); gy += paso * 1.4f }
        }
        drawCircle(c(URUSHI), rv, cv, style = Stroke(3 * u))
        drawCircle(c(KINPAKU).copy(alpha = 0.6f), rv + 2.5f * u, cv, style = Stroke(0.7f * u))
        // La sombra que echa el armarito sobre la primera repisa.
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.bottom - r.height * 0.04f), Size(r.width, r.height * 0.04f))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = TELAS[(v.semilla and 0x7fffffff) % TELAS.size]
        val tela = c(if (v.tono != null) mezcla(base, v.tono, 0.25f) else base)
        // El estuche, con un filo de luz y uno de sombra (sin degradados: en una repisa caben muchos).
        drawRoundRect(tela, Offset.Zero, size, CornerRadius(1.5f * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.08f, 0f), Size(w * 0.12f, h))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(w * 0.86f, 0f), Size(w * 0.14f, h))
        // La trama de la tela.
        var k = -h
        while (k < w) { drawLine(Color.White.copy(alpha = 0.06f), Offset(k, h), Offset(k + h, 0f), 0.6f * u); k += 3 * u }
        // Los dobleces de arriba y abajo.
        drawRect(Color.Black.copy(alpha = 0.28f), Offset.Zero, Size(w, h * 0.035f))
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(0f, h * 0.965f), Size(w, h * 0.035f))
        // Los cierres de hueso (kohaze) con su presilla de hilo, a la derecha.
        listOf(0.3f, 0.72f).forEach { f ->
            val o = Offset(w * 0.93f, h * f)
            drawLine(c(ZOGE).copy(alpha = 0.7f), Offset(w * 0.7f, o.y), o, 0.8f * u)
            drawOval(c(ZOGE), Offset(o.x - 1.8f * u, o.y - 4f * u), Size(3.6f * u, 8f * u))
            drawOval(c(KIRI_VETA), Offset(o.x - 1.8f * u, o.y - 4f * u), Size(3.6f * u, 8f * u), style = Stroke(0.5f * u))
        }
        // La tira de papel con el día y el mes.
        val tira = Rect(w * 0.16f, h * 0.07f, w * 0.8f, h * 0.6f)
        drawRect(c(SHOJI), tira.topLeft, tira.size)
        drawRect(c(KIRI_VETA), tira.topLeft, tira.size, style = Stroke(0.6f * u))
        drawRect(c(SHOJI).copy(alpha = 0.55f), tira.deflate(1.6f * u).topLeft, tira.deflate(1.6f * u).size, style = Stroke(0.5f * u))
        textoEn(medidor, v.dia, Rect(tira.left, tira.top + 2 * u, tira.right, tira.top + tira.height * 0.3f), letra, c(SUMI), 14f, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes, Rect(tira.left + 1 * u, tira.top + tira.height * 0.32f, tira.right - 1 * u, tira.bottom - 2 * u), letra, c(SUMI).copy(alpha = 0.85f), 8.5f, vertical = true)
        // Abajo: el sello bermellón; de noche, la luna y tres estrellas.
        val s = w * 0.36f
        val cs = Offset(w * 0.47f, h * 0.74f)
        if (v.noche) {
            drawCircle(LUNA, s * 0.5f, cs)
            drawCircle(tela, s * 0.42f, Offset(cs.x + s * 0.22f, cs.y - s * 0.12f))
            for (j in 0 until 3) drawCircle(LUNA.copy(alpha = 0.8f), 0.9f * u, Offset(w * (0.25f + 0.25f * j), h * (0.86f + 0.03f * (j % 2))))
        } else {
            drawRect(c(BERMELLON), Offset(cs.x - s / 2, cs.y - s / 2), Size(s, s))
            drawRect(c(SHOJI).copy(alpha = 0.85f), Offset(cs.x - s * 0.32f, cs.y - s * 0.32f), Size(s * 0.64f, s * 0.64f), style = Stroke(0.7f * u))
            drawLine(c(SHOJI).copy(alpha = 0.85f), Offset(cs.x, cs.y - s * 0.32f), Offset(cs.x, cs.y + s * 0.32f), 0.7f * u)
        }
        // Cuanto más escribiste, más hilo de costura asoma por arriba.
        val hilos = 1 + (v.lleno * 3).toInt()
        for (j in 0 until hilos) drawLine(c(ZOGE).copy(alpha = 0.7f), Offset(w * (0.25f + 0.15f * j), 0f), Offset(w * (0.25f + 0.15f * j), h * 0.03f), 0.8f * u)
    }
}
