package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
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
 * La tapa es ese estuche tumbado de frente: la tira de papel con la fecha, la placa de washi con olas para lo escrito,
 * el sello y los kohaze en el canto; al abrirse la solapa se va a la izquierda y sale el cuadernillo cosido con su
 * asanoha (de noche, la luna con tres estrellas en lugar del sello).
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa = Rect(0.235f, 0.3f, 0.785f, 0.66f)
    override val letraTapa: FontFamily = LetraExtra.zenMincho
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(SUMI, NOCHE, 0.15f) else SUMI
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** El washi de la placa y de las hojas: se hunde menos que la tela, para que el extracto se lea siempre. */
    private fun washi(oscuro: Boolean, noche: Boolean): Color {
        var r = SHOJI
        if (oscuro) r = mezcla(r, Color(0xFF120E0B), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    /** Las hojas de cáñamo (asanoha) en [r]: estrellas de seis puntas de paso [paso], solo donde caben enteras. */
    private fun DrawScope.asanoha(r: Rect, paso: Float, color: Color) {
        val u = density
        var fy = r.top + paso * 0.5f; var fila = 0
        while (fy < r.bottom - paso * 0.45f) {
            var fx = r.left + paso * (if (fila % 2 == 0) 0.5f else 1f)
            while (fx < r.right - paso * 0.45f) {
                for (k in 0 until 6) {
                    val g = k * Math.PI / 3
                    drawLine(color, Offset(fx, fy), Offset(fx + (paso * 0.5f * kotlin.math.cos(g)).toFloat(), fy + (paso * 0.5f * kotlin.math.sin(g)).toFloat()), 0.6f * u)
                }
                fx += paso
            }
            fy += paso * 0.866f; fila++
        }
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val tela = c(TELAS[(e.semilla and 0x7fffffff) % TELAS.size])
        val papel = washi(oscuro, n)
        // El estuche (chitsu) tumbado: la cara de atrás, forrada de papel, donde descansa el cuaderno, y el lomo del
        // estuche a la derecha, con las presillas de hilo donde entran los cierres.
        val estuche = Rect(w * 0.025f, h * 0.05f, w * 0.965f, h * 0.92f)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(estuche.left + 3 * u, estuche.top + 5 * u), estuche.size)
        drawRect(mezcla(tela, Color.Black, 0.2f), estuche.topLeft, estuche.size)
        val forro = estuche.deflate(3 * u)
        drawRect(mezcla(papel, c(KIRI_SOMBRA), 0.3f), forro.topLeft, forro.size)
        val lomo = Rect(w * 0.905f, estuche.top, estuche.right, estuche.bottom)
        drawRect(mezcla(tela, Color.Black, 0.12f), lomo.topLeft, lomo.size)
        clipRect(lomo.left, lomo.top, lomo.right, lomo.bottom) {
            var k = -lomo.height
            while (k < lomo.width) { drawLine(Color.White.copy(alpha = 0.06f), Offset(lomo.left + k, lomo.bottom), Offset(lomo.left + k + lomo.height, lomo.top), 0.6f * u); k += 3 * u }
        }
        val ys = listOf(0.3f, 0.7f).map { estuche.top + estuche.height * it }
        // El cuaderno cosido (fukuro-toji) que asoma al abrirse: tapa de índigo claro con asanoha, esquinas de seda, la
        // costura de cuatro agujeros y su tira de título.
        if (a > 0.01f) {
            val sale = -w * 0.03f * a
            val libro = Rect(w * 0.06f + sale, estuche.top + 7 * u, w * 0.89f + sale, estuche.bottom - 7 * u)
            for (j in 1..4) drawLine(c(KIRI_SOMBRA), Offset(libro.left + 2 * u, libro.bottom + j * 0.9f * u), Offset(libro.right, libro.bottom + j * 0.9f * u), 0.6f * u)
            val cubierta = mezcla(tela, Color.White, 0.18f)
            drawRect(cubierta, libro.topLeft, libro.size)
            asanoha(libro, 22 * u, Color.White.copy(alpha = 0.16f))
            val ke = 12 * u
            listOf(libro.top to 1f, libro.bottom to -1f).forEach { (y, s) ->
                drawPath(Path().apply { moveTo(libro.right, y); lineTo(libro.right - ke, y); lineTo(libro.right, y + s * ke); close() }, c(BERMELLON))
            }
            val xs = libro.right - 9 * u
            drawLine(c(ZOGE), Offset(xs, libro.top), Offset(xs, libro.bottom), 1.4f * u)
            listOf(0.12f, 0.37f, 0.63f, 0.88f).forEach { f ->
                val y = libro.top + libro.height * f
                drawLine(c(ZOGE), Offset(xs, y), Offset(libro.right, y), 1.4f * u)
                drawCircle(mezcla(tela, Color.Black, 0.4f), 1.4f * u, Offset(xs, y))
            }
            val daisen = Rect(libro.left + 8 * u, libro.top + 7 * u, libro.left + 22 * u, libro.top + libro.height * 0.7f)
            drawRect(papel, daisen.topLeft, daisen.size)
            drawRect(c(BERMELLON).copy(alpha = 0.7f), daisen.deflate(2 * u).topLeft, daisen.deflate(2 * u).size, style = Stroke(0.6f * u))
            for (j in 0 until 4) drawLine(c(SUMI).copy(alpha = 0.7f), Offset(daisen.center.x, daisen.top + daisen.height * (0.12f + j * 0.2f)), Offset(daisen.center.x, daisen.top + daisen.height * (0.24f + j * 0.2f)), 1.6f * u)
        }
        // La solapa del estuche, de tela, que se abre hacia la izquierda: la trama, la tira de papel con la fecha, la placa
        // de washi donde va lo escrito y los cierres de hueso (kohaze) en su canto.
        val solapa = Rect(estuche.left, estuche.top, lomo.left + 2 * u, estuche.bottom)
        val sx = 1f - 0.88f * a
        scale(sx, 1f, Offset(solapa.left, solapa.center.y)) {
            drawRect(Brush.verticalGradient(listOf(mezcla(tela, Color.White, 0.1f), tela, mezcla(tela, Color.Black, 0.28f)), solapa.top, solapa.bottom), solapa.topLeft, solapa.size)
            clipRect(solapa.left, solapa.top, solapa.right, solapa.bottom) {
                var q = -solapa.height
                while (q < solapa.width) { drawLine(Color.White.copy(alpha = 0.05f), Offset(solapa.left + q, solapa.bottom), Offset(solapa.left + q + solapa.height, solapa.top), 0.6f * u); q += 3 * u }
            }
            drawRect(Color.Black.copy(alpha = 0.25f), solapa.topLeft, Size(solapa.width, 3 * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(solapa.left, solapa.bottom - 3 * u), Size(solapa.width, 3 * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(solapa.right - 2 * u, solapa.top), Size(2 * u, solapa.height))
            // La tira de papel (daisen) con la fecha, de arriba abajo.
            val daisen = Rect(w * 0.065f, h * 0.11f, w * 0.065f + minOf(w * 0.085f, 30 * u), h * 0.86f)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(daisen.left + 1 * u, daisen.top + 1.5f * u), daisen.size)
            drawRect(papel, daisen.topLeft, daisen.size)
            drawRect(c(KIRI_VETA), daisen.topLeft, daisen.size, style = Stroke(0.7f * u))
            drawRect(c(BERMELLON).copy(alpha = 0.6f), daisen.deflate(2.2f * u).topLeft, daisen.deflate(2.2f * u).size, style = Stroke(0.5f * u))
            textoEn(medidor, e.fecha, daisen.deflate(3.5f * u), letra, c(SUMI), 12.5f, vertical = true, peso = FontWeight.SemiBold, minimo = 7f)
            // La placa de washi, con su marco y las olas (seigaiha) en la cabecera.
            val placa = Rect(w * 0.205f, h * 0.15f, w * 0.815f, h * 0.83f)
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(placa.left + 1.5f * u, placa.top + 2 * u), placa.size)
            drawRect(papel, placa.topLeft, placa.size)
            drawRect(c(KIRI_VETA), placa.topLeft, placa.size, style = Stroke(0.8f * u))
            val filo = placa.deflate(3 * u)
            drawRect(c(NAMI).copy(alpha = 0.6f), filo.topLeft, filo.size, style = Stroke(0.6f * u))
            val olas = Rect(filo.left + 3 * u, filo.top + 2 * u, filo.right - 3 * u, h * 0.275f)
            clipRect(olas.left, olas.top, olas.right, olas.bottom) {
                val ro = (olas.height * 0.62f).coerceAtLeast(2 * u)
                var fila = 0; var oy = olas.bottom
                while (oy > olas.top - ro && fila < 3) {
                    var ox = olas.left + (if (fila % 2 == 0) 0f else ro)
                    while (ox < olas.right + ro) {
                        for (j in 0..2) drawArc(c(NAMI).copy(alpha = 0.55f), 180f, 180f, false, Offset(ox - ro * (1 - j * 0.3f), oy - ro * (1 - j * 0.3f)),
                            Size(ro * 2 * (1 - j * 0.3f), ro * 2 * (1 - j * 0.3f)), style = Stroke(0.6f * u))
                        ox += ro * 2
                    }
                    oy -= ro * 0.5f; fila++
                }
            }
            drawLine(c(NAMI).copy(alpha = 0.6f), Offset(filo.left, h * 0.28f), Offset(filo.right, h * 0.28f), 0.6f * u)
            // Abajo de la placa, la cuenta y el sello bermellón; de noche, la luna y tres estrellas.
            val s = 13 * u
            val cs = Offset(placa.right - s * 0.5f - 7 * u, placa.bottom - s * 0.5f - 6 * u)
            if (!e.vacio && e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(placa.left + 8 * u, h * 0.675f, cs.x - s, placa.bottom - 4 * u), letra, c(SUMI).copy(alpha = 0.8f), 10.5f, minimo = 6f)
            }
            if (n) {
                drawCircle(LUNA, s * 0.5f, cs)
                drawCircle(papel, s * 0.42f, Offset(cs.x + s * 0.22f, cs.y - s * 0.12f))
                drawCircle(c(SUMI).copy(alpha = 0.25f), s * 0.5f, cs, style = Stroke(0.5f * u))
                for (j in 0 until 3) drawPath(Path().apply {
                    val o = Offset(cs.x - s * (1.1f + 0.5f * j), cs.y - s * (0.6f - 0.4f * (j % 2)))
                    moveTo(o.x, o.y - 2 * u); lineTo(o.x + 0.6f * u, o.y); lineTo(o.x, o.y + 2 * u); lineTo(o.x - 0.6f * u, o.y); close()
                }, c(NAMI))
            } else {
                drawRect(c(BERMELLON), Offset(cs.x - s / 2, cs.y - s / 2), Size(s, s))
                drawRect(papel.copy(alpha = 0.85f), Offset(cs.x - s * 0.32f, cs.y - s * 0.32f), Size(s * 0.64f, s * 0.64f), style = Stroke(0.8f * u))
                drawLine(papel.copy(alpha = 0.85f), Offset(cs.x, cs.y - s * 0.32f), Offset(cs.x, cs.y + s * 0.32f), 0.8f * u)
                drawLine(papel.copy(alpha = 0.85f), Offset(cs.x - s * 0.32f, cs.y), Offset(cs.x, cs.y), 0.8f * u)
            }
            // Los cierres de hueso en el canto de la solapa, con su hilo hasta la presilla del lomo.
            ys.forEach { y ->
                val o = Offset(solapa.right - 3 * u, y)
                drawLine(c(ZOGE).copy(alpha = 0.8f), Offset(solapa.right - 12 * u, y), o, 1 * u)
                drawLine(c(ZOGE).copy(alpha = 0.8f), o, Offset(lomo.right - 4 * u, y), 1 * u)
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(o.x - 2.6f * u, o.y - 6.6f * u), Size(6 * u, 14 * u))
                drawOval(c(ZOGE), Offset(o.x - 3 * u, o.y - 7 * u), Size(6 * u, 14 * u))
                drawOval(c(KIRI_VETA), Offset(o.x - 3 * u, o.y - 7 * u), Size(6 * u, 14 * u), style = Stroke(0.6f * u))
                drawLine(Color.White.copy(alpha = 0.5f), Offset(o.x - 1 * u, o.y - 4 * u), Offset(o.x - 1 * u, o.y + 2 * u), 0.8f * u)
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.35f * a), solapa.topLeft, solapa.size)
        }
        // Las presillas de hilo en el lomo del estuche (de ahí se sueltan los cierres al abrir).
        ys.forEach { y -> drawOval(c(ZOGE).copy(alpha = 0.8f), Offset(lomo.right - 7 * u, y - 3 * u), Size(5 * u, 6 * u), style = Stroke(0.9f * u)) }
        // De noche, la luna detrás del shōji deja su luz fría.
        if (n) clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.85f, -h * 0.1f), h * 1.1f, LUNA.copy(alpha = 0.16f)) }
    }
}

/**
 * Renacimiento: la librería del palazzo, de nogal. Paneles de moldura en el fondo de cada repisa, costados como pilastras
 * acanaladas con su capitel dorado, tablas con una moldura de ovas y dardos, y cada mes en un cartucho de pergamino con
 * las puntas enrolladas. Arriba, sobre el muro, el entablamento clásico (arquitrabe, friso con rosetas y cornisa con
 * dentículos) y un frontón triangular con una venera dorada en el tímpano; sobre la cornisa, un globo terráqueo en su pie
 * con su meridiano de latón, un candelero y un astrolabio colgado de su soporte. De noche la vela se enciende y se cuelga
 * delante del friso una carta celeste con sus estrellas. Cada entrada es un libro en pergamino flexible, con los bordes
 * algo ondulados, el día escrito a mano en el lomo con su raya, el mes a lo largo, dos cintas de atar de color, el disco de
 * la signatura (de noche, una luna dibujada a tinta) y los pasos de la costura abajo, más cuanto más escribiste. Distinto
 * del Ideario, que es el studiolo de taracea con sus cajoncitos.
 * La tapa es ese cuaderno de vitela de frente: la fecha a mano, la figura de sanguina, notas en espejo y la solapa
 * con su botón de hueso; al abrirse se suelta la solapa, gira la tapa y aparecen los estudios de la hoja (de noche, una
 * luna y una carta de estrellas dibujadas a tinta).
 */
object EstanteRenacimiento : DisenoBiblioteca {
    private val NOGAL = Color(0xFF5A3B24)
    private val NOGAL_LUZ = Color(0xFF7C5636)
    private val NOGAL_OSC = Color(0xFF3A2416)
    private val VETA = Color(0xFF22140A)
    private val ORO = Color(0xFFC8A250)
    private val PERGAMINO = Color(0xFFEFE5CB)
    private val TINTA = Color(0xFF2A1E14)
    private val SEPIA = Color(0xFF5A3E26)
    private val LATON = Color(0xFFBF9A52)
    private val LATON_OSC = Color(0xFF8A6A30)
    private val MURO = Color(0xFFD9C8A6)
    private val GLOBO_MAR = Color(0xFFCFC49A)
    private val GLOBO_TIERRA = Color(0xFF9A7444)
    private val CARTA = Color(0xFF1E2A4A)
    private val ESTRELLA = Color(0xFFF2E6BE)
    private val CERA = Color(0xFFEFE6CC)
    private val LLAMA = Color(0xFFF7C45A)
    private val ROJO = Color(0xFFA23A30)
    private val COTTO = Color(0xFFA8704A)
    /** Los pergaminos de las tapas, del blanco al tostado. */
    private val VITELAS = listOf(Color(0xFFEDE3C8), Color(0xFFE4D6B4), Color(0xFFF0E8D4), Color(0xFFDCCCA8), Color(0xFFE8DCC0))
    /** Las cintas de atar: verde, roja, azul y ocre. */
    private val CINTAS = listOf(Color(0xFF4E7A4A), Color(0xFFA23A30), Color(0xFF3A5A8A), Color(0xFFB08A3A))
    private val NOCHE = Color(0xFF161A2C)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, a la luz de la vela. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100C08), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.44f)
        return r
    }

    override val corona = 0.36f
    override val base = 0.1f
    override val lado = 0.06f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 18.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.cormorant

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.15f * azar(semilla, 1)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(NOGAL_OSC), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(VETA).copy(alpha = 0.45f), 14, densidad = 0.5f, vertical = true, grosor = 0.7f)
        // El fondo de cada repisa: paneles de moldura.
        d.repisas.forEach { r ->
            val pw = 64 * u
            var x = r.left + 6 * u
            while (x + pw * 0.5f < r.right) {
                val panel = Rect(x, r.top + 8 * u, minOf(x + pw, r.right - 6 * u), r.bottom - 6 * u)
                drawRect(c(NOGAL).copy(alpha = 0.55f), panel.topLeft, panel.size)
                drawRect(Color.Black.copy(alpha = 0.3f), panel.topLeft, panel.size, style = Stroke(1f * u))
                val dentro = panel.deflate(5 * u)
                drawRect(Color.White.copy(alpha = 0.05f), dentro.topLeft, dentro.size)
                drawRect(Color.Black.copy(alpha = 0.2f), dentro.topLeft, dentro.size, style = Stroke(0.7f * u))
                x += pw + 6 * u
            }
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.3f), r.topLeft, Size(r.width, 10 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), r.topLeft, Size(r.width, 4 * u))
        }
        // Los costados: pilastras acanaladas, con su capitel de volutas doradas y su basa.
        listOf(d.izquierda, d.derecha).forEachIndexed { k, r ->
            drawRect(c(NOGAL), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.5f), 15 + k, densidad = 1.1f, vertical = true, grosor = 0.7f)
            val fuste = Rect(r.left + r.width * 0.16f, r.top + 12 * u, r.right - r.width * 0.16f, r.bottom - 12 * u)
            drawRect(c(NOGAL_LUZ).copy(alpha = 0.5f), fuste.topLeft, fuste.size)
            for (j in 1..3) {
                val fx = fuste.left + fuste.width * j / 4f
                drawLine(Color.Black.copy(alpha = 0.35f), Offset(fx, fuste.top + 3 * u), Offset(fx, fuste.bottom - 3 * u), 1.2f * u)
                drawLine(Color.White.copy(alpha = 0.12f), Offset(fx + 1.2f * u, fuste.top + 3 * u), Offset(fx + 1.2f * u, fuste.bottom - 3 * u), 0.6f * u)
            }
            drawRect(c(ORO).copy(alpha = 0.85f), Offset(r.left + 1 * u, fuste.top - 4 * u), Size(r.width - 2 * u, 4 * u))
            drawCircle(c(ORO), 2 * u, Offset(r.left + 3 * u, fuste.top - 5.5f * u), style = Stroke(1f * u))
            drawCircle(c(ORO), 2 * u, Offset(r.right - 3 * u, fuste.top - 5.5f * u), style = Stroke(1f * u))
            drawRect(c(NOGAL_LUZ), Offset(r.left, fuste.bottom), Size(r.width, 4 * u))
            drawRect(c(NOGAL_LUZ), Offset(r.left + 1.5f * u, fuste.bottom + 5 * u), Size(r.width - 3 * u, 2.5f * u))
            bisel(r, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 1.4f * u)
        }
        // Las tablas, con una moldura de ovas y dardos.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(NOGAL), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.45f), 27 + k, densidad = 0.6f, grosor = 0.6f)
            drawRect(c(NOGAL_LUZ), r.topLeft, Size(r.width, 2.2f * u))
            val ov = Rect(r.left, r.top + r.height * 0.3f, r.right, r.top + r.height * 0.78f)
            drawRect(c(NOGAL_OSC), ov.topLeft, ov.size)
            var ex = ov.left + 2 * u
            while (ex < ov.right - 6 * u) {
                drawOval(c(NOGAL_LUZ), Offset(ex, ov.top + 0.6f * u), Size(5 * u, ov.height - 1.2f * u))
                drawLine(c(NOGAL_LUZ), Offset(ex + 6.5f * u, ov.top + 1 * u), Offset(ex + 6.5f * u, ov.bottom - 1 * u), 0.8f * u)
                ex += 8 * u
            }
            drawRect(c(ORO).copy(alpha = 0.6f), Offset(r.left, r.bottom - 3 * u), Size(r.width, 0.8f * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: un cartucho de pergamino con las puntas enrolladas.
        d.placas.forEach { p ->
            val r = p.r
            val rr = r.height * 0.42f
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1.2f * u, r.top + 1.4f * u), r.size)
            drawRect(c(PERGAMINO), r.topLeft, r.size)
            drawLine(c(SEPIA), Offset(r.left, r.top), Offset(r.right, r.top), 0.7f * u)
            drawLine(c(SEPIA), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.7f * u)
            listOf(r.left, r.right).forEach { x ->
                drawCircle(c(PERGAMINO), rr, Offset(x, r.center.y))
                drawCircle(c(SEPIA), rr, Offset(x, r.center.y), style = Stroke(0.7f * u))
                drawCircle(c(SEPIA), rr * 0.4f, Offset(x, r.center.y), style = Stroke(0.6f * u))
            }
            textoEn(medidor, p.texto, Rect(r.left + rr * 1.3f, r.top, r.right - rr * 1.3f, r.bottom), letra, c(TINTA), 11.5f, peso = FontWeight.SemiBold)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de nogal con sus pies de voluta, sobre un piso de cotto.
        val b = d.base
        val piso = b.top + b.height * 0.62f
        drawRect(c(COTTO), Offset(0f, piso), Size(w, h - piso))
        var cx = -h
        while (cx < w) {
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(cx, h), Offset(cx + (h - piso), piso), 0.8f * u)
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(cx, piso), Offset(cx + (h - piso), h), 0.8f * u)
            cx += 16 * u
        }
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(NOGAL), zocalo.topLeft, zocalo.size)
        vetas(zocalo, c(VETA).copy(alpha = 0.45f), 47, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(NOGAL_LUZ), zocalo.topLeft, Size(zocalo.width, 2.4f * u))
        drawLine(c(ORO).copy(alpha = 0.7f), Offset(zocalo.left, zocalo.top + zocalo.height * 0.4f), Offset(zocalo.right, zocalo.top + zocalo.height * 0.4f), 0.8f * u)
        listOf(zocalo.left + 8 * u, zocalo.right - 8 * u).forEach { px ->
            drawCircle(c(NOGAL_OSC), 5 * u, Offset(px, piso - 4 * u))
            drawCircle(c(ORO).copy(alpha = 0.6f), 3 * u, Offset(px, piso - 4 * u), style = Stroke(0.8f * u))
        }
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** El entablamento, el frontón y lo que hay encima: el globo, el candelero, el astrolabio; de noche, la carta celeste. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(MURO), r.topLeft, r.size)
        val ycor = r.top + r.height * 0.58f
        val cornisa = Rect(r.left, ycor, r.right, r.top + r.height * 0.68f)
        val dentellon = Rect(r.left + r.width * 0.005f, cornisa.bottom, r.right - r.width * 0.005f, r.top + r.height * 0.73f)
        val friso = Rect(r.left + r.width * 0.01f, dentellon.bottom, r.right - r.width * 0.01f, r.top + r.height * 0.88f)
        val arquitrabe = Rect(r.left + r.width * 0.02f, friso.bottom, r.right - r.width * 0.02f, r.bottom)
        // El arquitrabe con sus dos fajas.
        drawRect(c(NOGAL), arquitrabe.topLeft, arquitrabe.size)
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(arquitrabe.left, arquitrabe.center.y), Offset(arquitrabe.right, arquitrabe.center.y), 0.8f * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(arquitrabe.left, arquitrabe.bottom - 2 * u), Size(arquitrabe.width, 2 * u))
        // El friso, con sus filetes de oro y rosetas.
        drawRect(c(NOGAL_OSC), friso.topLeft, friso.size)
        vetas(friso, c(VETA).copy(alpha = 0.4f), 91, densidad = 0.6f, grosor = 0.6f)
        drawLine(c(ORO), Offset(friso.left, friso.top + 1.5f * u), Offset(friso.right, friso.top + 1.5f * u), 0.7f * u)
        drawLine(c(ORO), Offset(friso.left, friso.bottom - 1.5f * u), Offset(friso.right, friso.bottom - 1.5f * u), 0.7f * u)
        var rx = friso.left + 20 * u
        while (rx < friso.right - 10 * u) {
            val rc = friso.height * 0.26f
            for (j in 0 until 6) {
                val a = Math.toRadians(j * 60.0)
                drawCircle(c(ORO).copy(alpha = 0.8f), rc * 0.42f, Offset(rx + kotlin.math.cos(a).toFloat() * rc * 0.6f, friso.center.y + kotlin.math.sin(a).toFloat() * rc * 0.6f))
            }
            drawCircle(c(NOGAL_LUZ), rc * 0.36f, Offset(rx, friso.center.y))
            rx += 40 * u
        }
        // Los dentículos y la cornisa volada.
        drawRect(c(NOGAL_OSC), dentellon.topLeft, dentellon.size)
        var dx = dentellon.left + 1 * u
        while (dx < dentellon.right) { drawRect(c(NOGAL_LUZ), Offset(dx, dentellon.top), Size(3 * u, dentellon.height - 0.6f * u)); dx += 5 * u }
        drawRect(c(NOGAL), cornisa.topLeft, cornisa.size)
        drawRect(c(NOGAL_LUZ), cornisa.topLeft, Size(cornisa.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cornisa.left, cornisa.bottom - 2 * u), Size(cornisa.width, 2 * u))
        // El frontón triangular con la venera dorada en el tímpano.
        val fx0 = r.left + r.width * 0.3f; val fx1 = r.right - r.width * 0.3f
        val apice = Offset(r.center.x, r.top + r.height * 0.12f)
        val fronton = Path().apply { moveTo(fx0 - 4 * u, ycor); lineTo(apice.x, apice.y - 3 * u); lineTo(fx1 + 4 * u, ycor); close() }
        drawPath(fronton, c(NOGAL_LUZ))
        val timpano = Path().apply { moveTo(fx0 + 8 * u, ycor - 2 * u); lineTo(apice.x, apice.y + 6 * u); lineTo(fx1 - 8 * u, ycor - 2 * u); close() }
        drawPath(timpano, c(NOGAL))
        clipPath(timpano) { vetas(Rect(fx0, apice.y, fx1, ycor), c(VETA).copy(alpha = 0.4f), 93, densidad = 0.6f, grosor = 0.6f) }
        drawPath(fronton, Color.Black.copy(alpha = 0.3f), style = Stroke(0.8f * u))
        val cc = Offset(r.center.x, ycor - 3 * u)
        val rc = (ycor - apice.y) * 0.4f
        drawArc(c(ORO).copy(alpha = 0.85f), 180f, 180f, true, Offset(cc.x - rc, cc.y - rc), Size(rc * 2, rc * 2))
        for (j in 0..8) {
            val a = Math.toRadians(180.0 + j * 22.5)
            drawLine(c(NOGAL_OSC), cc, Offset(cc.x + kotlin.math.cos(a).toFloat() * rc, cc.y + kotlin.math.sin(a).toFloat() * rc), 0.8f * u)
        }
        drawArc(c(NOGAL_OSC), 180f, 180f, false, Offset(cc.x - rc, cc.y - rc), Size(rc * 2, rc * 2), style = Stroke(0.8f * u))
        listOf(Offset(apice.x, apice.y - 5 * u), Offset(fx0 - 2 * u, ycor - 3 * u), Offset(fx1 + 2 * u, ycor - 3 * u)).forEach { o -> drawCircle(c(ORO), 2.6f * u, o) }
        // El globo terráqueo en su pie, con su meridiano de latón y su horizonte de madera.
        val gx = r.left + r.width * 0.15f
        val rg = r.height * 0.15f
        val gc = Offset(gx, ycor - 10 * u - rg)
        drawRect(c(NOGAL_OSC), Offset(gx - rg * 0.7f, ycor - 3 * u), Size(rg * 1.4f, 3 * u))
        drawLine(c(NOGAL_OSC), Offset(gx, ycor - 3 * u), Offset(gx, gc.y + rg), 2.4f * u)
        drawCircle(c(GLOBO_MAR), rg, gc)
        clipPath(Path().apply { addOval(Rect(gc, rg)) }) {
            drawOval(c(GLOBO_TIERRA), Offset(gc.x - rg * 0.7f, gc.y - rg * 0.6f), Size(rg * 0.8f, rg * 0.6f))
            drawOval(c(GLOBO_TIERRA), Offset(gc.x - rg * 0.3f, gc.y), Size(rg * 0.5f, rg * 0.8f))
            drawOval(c(GLOBO_TIERRA), Offset(gc.x + rg * 0.35f, gc.y - rg * 0.4f), Size(rg * 0.5f, rg * 0.4f))
            for (j in 1..2) drawOval(c(SEPIA).copy(alpha = 0.5f), Offset(gc.x - rg * j / 3f, gc.y - rg), Size(rg * 2 * j / 3f, rg * 2), style = Stroke(0.5f * u))
            drawLine(c(SEPIA).copy(alpha = 0.5f), Offset(gc.x - rg, gc.y - rg * 0.4f), Offset(gc.x + rg, gc.y - rg * 0.4f), 0.5f * u)
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(gc.x + rg * 0.3f, gc.y - rg), Size(rg * 0.7f, rg * 2))
        }
        drawCircle(c(LATON), rg + 2.4f * u, gc, style = Stroke(1.4f * u))
        drawRoundRect(c(NOGAL_OSC), Offset(gc.x - rg - 4 * u, gc.y - 2 * u), Size(rg * 2 + 8 * u, 4 * u), CornerRadius(2 * u))
        // El candelero de latón; de noche, la vela encendida.
        val vx = r.left + r.width * 0.255f
        drawOval(c(LATON), Offset(vx - 5 * u, ycor - 3 * u), Size(10 * u, 3 * u))
        drawLine(c(LATON), Offset(vx, ycor - 2 * u), Offset(vx, ycor - 10 * u), 1.8f * u)
        drawOval(c(LATON), Offset(vx - 4 * u, ycor - 12 * u), Size(8 * u, 2.6f * u))
        val vela = Rect(vx - 2.2f * u, ycor - 26 * u, vx + 2.2f * u, ycor - 11 * u)
        drawRect(if (noche) CERA else c(CERA), vela.topLeft, vela.size)
        drawLine(c(TINTA), Offset(vx, vela.top), Offset(vx, vela.top - 2 * u), 0.7f * u)
        if (noche) {
            mancha(Offset(vx, vela.top - 6 * u), r.height * 0.5f, LLAMA.copy(alpha = 0.38f))
            val llama = Path().apply {
                moveTo(vx, vela.top - 10 * u)
                quadraticTo(vx + 3 * u, vela.top - 4 * u, vx, vela.top - 1.5f * u)
                quadraticTo(vx - 3 * u, vela.top - 4 * u, vx, vela.top - 10 * u); close()
            }
            drawPath(llama, LLAMA)
            drawOval(Color.White.copy(alpha = 0.8f), Offset(vx - 0.9f * u, vela.top - 5.5f * u), Size(1.8f * u, 3.2f * u))
        }
        // El astrolabio, colgado de su soporte.
        val ax = r.right - r.width * 0.15f
        val ra = r.height * 0.14f
        val ac = Offset(ax, ycor - 8 * u - ra)
        val poste = ax + ra + 6 * u
        drawLine(c(NOGAL_OSC), Offset(poste, ycor), Offset(poste, ac.y - ra - 9 * u), 2.2f * u)
        drawLine(c(NOGAL_OSC), Offset(poste, ac.y - ra - 9 * u), Offset(ax, ac.y - ra - 9 * u), 2 * u)
        drawRect(c(NOGAL_OSC), Offset(poste - 5 * u, ycor - 3 * u), Size(10 * u, 3 * u))
        drawCircle(c(LATON), 2.4f * u, Offset(ax, ac.y - ra - 5 * u), style = Stroke(1f * u))
        drawRect(c(LATON), Offset(ax - 3 * u, ac.y - ra - 3 * u), Size(6 * u, 4 * u))
        drawCircle(c(LATON), ra, ac)
        drawCircle(c(LATON_OSC), ra * 0.86f, ac)
        drawCircle(c(LATON), ra * 0.86f, ac, style = Stroke(0.6f * u))
        drawCircle(c(LATON), ra * 0.5f, Offset(ac.x, ac.y - ra * 0.2f), style = Stroke(1f * u))
        for (j in 0 until 6) {
            val a = Math.toRadians(j * 60.0 + 15.0)
            val p = Offset(ac.x + kotlin.math.cos(a).toFloat() * ra * 0.78f, ac.y + kotlin.math.sin(a).toFloat() * ra * 0.78f)
            drawLine(c(LATON), ac, p, 0.7f * u)
            drawCircle(c(LATON), 1 * u, p)
        }
        drawLine(c(LATON), Offset(ac.x - ra * 0.9f, ac.y + ra * 0.4f), Offset(ac.x + ra * 0.9f, ac.y - ra * 0.4f), 1.4f * u)
        drawCircle(c(LATON), 1.2f * u, ac)
        // De noche, la carta celeste colgada delante del friso.
        if (noche) {
            val carta = Rect(r.right - r.width * 0.3f, friso.top - 4 * u, r.right - r.width * 0.07f, arquitrabe.bottom - 2 * u)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(carta.left + 1.5f * u, carta.top + 1.5f * u), carta.size)
            drawRect(CARTA, carta.topLeft, carta.size)
            clipRect(carta.left, carta.top, carta.right, carta.bottom) {
                drawArc(ESTRELLA.copy(alpha = 0.35f), 200f, 140f, false, Offset(carta.left - carta.width * 0.1f, carta.top + carta.height * 0.2f), Size(carta.width * 1.2f, carta.height * 1.6f), style = Stroke(0.6f * u))
                val puntos = (0 until 9).map { k -> Offset(carta.left + carta.width * (0.08f + 0.84f * azar(k, 71)), carta.top + carta.height * (0.12f + 0.76f * azar(k, 72))) }
                for (k in 0 until 3) drawLine(ESTRELLA.copy(alpha = 0.4f), puntos[k], puntos[k + 1], 0.5f * u)
                for (k in 5 until 7) drawLine(ESTRELLA.copy(alpha = 0.4f), puntos[k], puntos[k + 1], 0.5f * u)
                puntos.forEachIndexed { k, o -> drawCircle(ESTRELLA, (0.6f + 0.6f * azar(k, 73)) * u, o) }
            }
            drawRect(c(ORO).copy(alpha = 0.7f), carta.topLeft, carta.size, style = Stroke(0.8f * u))
            listOf(carta.left + 2 * u, carta.right - 2 * u).forEach { x -> drawCircle(c(LATON), 1.2f * u, Offset(x, carta.top + 2 * u)) }
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val tinte = VITELAS[(v.semilla and 0x7fffffff) % VITELAS.size]
        val vitela = c(if (v.tono != null) mezcla(tinte, v.tono, 0.22f) else tinte)
        // El pergamino flexible, con los bordes de arriba y abajo algo ondulados.
        val cuerpo = Path().apply {
            moveTo(0f, h * 0.025f); quadraticTo(w * 0.5f, 0f, w, h * 0.02f)
            lineTo(w, h * 0.98f); quadraticTo(w * 0.5f, h, 0f, h * 0.975f); close()
        }
        drawPath(cuerpo, vitela)
        clipPath(cuerpo) {
            drawRect(Color.White.copy(alpha = 0.12f), Offset(w * 0.1f, 0f), Size(w * 0.12f, h))
            drawRect(Color.Black.copy(alpha = 0.14f), Offset(w * 0.84f, 0f), Size(w * 0.16f, h))
            drawRect(c(SEPIA).copy(alpha = 0.16f), Offset.Zero, Size(w, h * 0.04f))
            drawRect(c(SEPIA).copy(alpha = 0.16f), Offset(0f, h * 0.96f), Size(w, h * 0.04f))
            for (j in 0 until 2) {
                val o = Offset(w * azar(v.semilla, 40 + j), h * azar(v.semilla, 50 + j))
                drawOval(c(SEPIA).copy(alpha = 0.08f), Offset(o.x - 3 * u, o.y - 2 * u), Size(6 * u, 4 * u))
            }
        }
        drawPath(cuerpo, c(SEPIA).copy(alpha = 0.4f), style = Stroke(0.6f * u))
        // El día, escrito a mano en el lomo, con su raya debajo.
        textoEn(medidor, v.dia, Rect(w * 0.1f, h * 0.07f, w * 0.9f, h * 0.25f), letra, c(TINTA), 16f, peso = FontWeight.SemiBold)
        drawLine(c(TINTA).copy(alpha = 0.6f), Offset(w * 0.25f, h * 0.266f), Offset(w * 0.75f, h * 0.262f), 0.6f * u)
        // El mes, a lo largo.
        textoEn(medidor, v.mes, Rect(w * 0.2f, h * 0.3f, w * 0.76f, h * 0.66f), letra, c(TINTA).copy(alpha = 0.85f), 10f, vertical = true, minimo = 5f)
        // Las dos cintas de atar, que salen del canto.
        val cinta = c(CINTAS[((v.semilla and 0x7fffffff) / 7) % CINTAS.size])
        listOf(0.36f, 0.62f).forEachIndexed { j, f ->
            val y = h * f
            drawRect(cinta, Offset(w * 0.8f, y - 1.2f * u), Size(w * 0.2f, 2.4f * u))
            drawLine(cinta, Offset(w * 0.9f, y), Offset(w * (0.84f + 0.04f * j), y + 6 * u), 1.4f * u)
        }
        // El disco de la signatura; de noche, una luna dibujada a tinta.
        val cs = Offset(w * 0.5f, h * 0.75f)
        val rs = minOf(w * 0.18f, 4.5f * u)
        if (v.noche) {
            drawCircle(c(SEPIA), rs, cs)
            drawCircle(vitela, rs * 0.82f, Offset(cs.x + rs * 0.42f, cs.y - rs * 0.22f))
            drawCircle(c(SEPIA), 0.7f * u, Offset(cs.x + rs * 1.2f, cs.y + rs * 0.6f))
        } else {
            drawCircle(c(ROJO), rs, cs)
            drawCircle(vitela.copy(alpha = 0.7f), rs * 0.6f, cs, style = Stroke(0.6f * u))
        }
        // Los pasos de la costura por el lomo: más cuanto más escribiste.
        val pasos = 2 + (v.lleno * 3).toInt()
        for (j in 0 until pasos) {
            val y = h * (0.84f + 0.025f * j)
            drawLine(c(SEPIA).copy(alpha = 0.6f), Offset(w * 0.3f, y), Offset(w * 0.42f, y), 0.9f * u)
            drawLine(c(SEPIA).copy(alpha = 0.6f), Offset(w * 0.58f, y), Offset(w * 0.7f, y), 0.9f * u)
        }
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 186.dp
    override val cajaTapa = Rect(0.08f, 0.31f, 0.6f, 0.7f)
    override val letraTapa: FontFamily = LetraExtra.cormorant
    override val tamTapa = 14.5f
    override val lineasTapa = 3
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La vitela de la tapa y el papel de las hojas: se hunden menos que el resto, para que el extracto se lea siempre. */
    private fun vitela(x: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = x
        if (oscuro) r = mezcla(r, Color(0xFF100C08), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    /** Un renglón de escritura en espejo, de [x1] hacia [x0] (de derecha a izquierda), como ondas pequeñas. */
    private fun DrawScope.espejo(x0: Float, x1: Float, y: Float, color: Color, semilla: Int) {
        val u = density
        var x = x1; var k = 0
        while (x > x0 + 4 * u) {
            val largo = minOf((6f + 12f * azar(semilla, k)) * u, x - x0)
            val p = Path(); p.moveTo(x, y)
            var q = x
            while (q > x - largo) { p.quadraticTo(q - 1.5f * u, y - 2.4f * u, q - 3 * u, y); q -= 3 * u }
            drawPath(p, color, style = Stroke(0.8f * u))
            x -= largo + 3 * u; k++
        }
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val piel = vitela(VITELAS[(e.semilla and 0x7fffffff) % VITELAS.size], oscuro, n)
        val papel = vitela(PERGAMINO, oscuro, n)
        val sanguina = c(ROJO).copy(alpha = 0.32f)
        val cinta = c(CINTAS[((e.semilla and 0x7fffffff) / 7) % CINTAS.size])
        // Las hojas sueltas que asoman, un poco torcidas, y la sombra del cuaderno.
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(w * 0.05f, h * 0.09f), Size(w * 0.9f, h * 0.87f))
        rotate(-1.4f, Offset(w * 0.5f, h * 0.5f)) { drawRect(mezcla(papel, c(SEPIA), 0.12f), Offset(w * 0.045f, h * 0.075f), Size(w * 0.88f, h * 0.87f)) }
        rotate(1f, Offset(w * 0.5f, h * 0.5f)) { drawRect(papel, Offset(w * 0.04f, h * 0.07f), Size(w * 0.88f, h * 0.86f)) }
        // La hoja de dentro, que se ve al abrir: estudios de sanguina (un ala, un engranaje, una espiral de agua) y notas
        // escritas en espejo.
        if (a > 0.01f) {
            val pag = Rect(w * 0.04f, h * 0.07f, w * 0.92f, h * 0.93f)
            drawRect(papel, pag.topLeft, pag.size)
            val ala = Offset(pag.left + pag.width * 0.2f, pag.top + pag.height * 0.55f)
            for (k in 0 until 6) {
                val p = Path().apply { moveTo(ala.x, ala.y); quadraticTo(ala.x + (20 + 8 * k) * u, ala.y - (34 - 3 * k) * u, ala.x + (44 + 6 * k) * u, ala.y - (10 - 4 * k) * u) }
                drawPath(p, sanguina.copy(alpha = 0.5f), style = Stroke(0.9f * u))
            }
            val rueda = Offset(pag.left + pag.width * 0.62f, pag.top + pag.height * 0.4f)
            val rr = pag.height * 0.18f
            drawCircle(sanguina.copy(alpha = 0.5f), rr, rueda, style = Stroke(1 * u))
            drawCircle(sanguina.copy(alpha = 0.5f), rr * 0.3f, rueda, style = Stroke(1 * u))
            for (k in 0 until 12) {
                val g = k * Math.PI / 6
                val cs = kotlin.math.cos(g).toFloat(); val sn = kotlin.math.sin(g).toFloat()
                drawLine(sanguina.copy(alpha = 0.5f), Offset(rueda.x + cs * rr, rueda.y + sn * rr), Offset(rueda.x + cs * rr * 1.18f, rueda.y + sn * rr * 1.18f), 2.2f * u)
            }
            val esp = Path(); var g = 0f; esp.moveTo(pag.left + pag.width * 0.84f, pag.top + pag.height * 0.7f)
            while (g < 12f) { val r = g * 1.6f * u; esp.lineTo(pag.left + pag.width * 0.84f + r * kotlin.math.cos(g), pag.top + pag.height * 0.7f + r * kotlin.math.sin(g)); g += 0.3f }
            drawPath(esp, c(SEPIA).copy(alpha = 0.4f), style = Stroke(0.8f * u))
            for (j in 0 until 4) espejo(pag.left + pag.width * 0.35f, pag.right - 14 * u, pag.top + pag.height * (0.72f + j * 0.06f), c(SEPIA).copy(alpha = 0.5f), e.semilla + j)
            for (j in 0 until 3) espejo(pag.left + 14 * u, pag.left + pag.width * 0.45f, pag.top + pag.height * (0.1f + j * 0.06f), c(SEPIA).copy(alpha = 0.5f), e.semilla + 10 + j)
        }
        // La tapa de vitela flexible, de bordes blandos, que se abre sobre su lomo cuando ya se soltó la solapa.
        val p1 = (a / 0.4f).coerceIn(0f, 1f)
        val p2 = ((a - 0.2f) / 0.8f).coerceIn(0f, 1f)
        val sx = 1f - 0.88f * p2
        scale(sx, 1f, Offset(w * 0.03f, h * 0.5f)) {
            val tapa = Path().apply {
                moveTo(w * 0.03f, h * 0.06f); quadraticTo(w * 0.4f, h * 0.03f, w * 0.8f, h * 0.05f)
                lineTo(w * 0.8f, h * 0.93f); quadraticTo(w * 0.4f, h * 0.96f, w * 0.03f, h * 0.93f)
                quadraticTo(w * 0.015f, h * 0.5f, w * 0.03f, h * 0.06f); close()
            }
            drawPath(tapa, Brush.verticalGradient(listOf(mezcla(piel, Color.White, 0.2f), piel, mezcla(piel, c(SEPIA), 0.12f)), h * 0.03f, h * 0.96f))
            clipPath(tapa) {
                for (j in 0 until 5) {
                    val o = Offset(w * (0.05f + 0.7f * azar(e.semilla, 40 + j)), h * (0.1f + 0.8f * azar(e.semilla, 50 + j)))
                    if (o.x !in w * 0.06f..w * 0.62f || o.y !in h * 0.28f..h * 0.73f) drawOval(c(SEPIA).copy(alpha = 0.07f), Offset(o.x - 9 * u, o.y - 5 * u), Size(18 * u, 10 * u))
                }
                drawRect(c(SEPIA).copy(alpha = 0.1f), Offset(w * 0.03f, h * 0.02f), Size(w * 0.78f, 4 * u))
                drawRect(c(SEPIA).copy(alpha = 0.1f), Offset(w * 0.03f, h * 0.93f - 4 * u), Size(w * 0.78f, 6 * u))
                // Las arrugas de la vitela junto al lomo.
                for (j in 0 until 4) drawLine(c(SEPIA).copy(alpha = 0.12f), Offset(w * (0.05f + 0.012f * j), h * 0.08f), Offset(w * (0.055f + 0.012f * j), h * 0.92f), 0.8f * u)
            }
            drawPath(tapa, c(SEPIA).copy(alpha = 0.55f), style = Stroke(1.2f * u))
            // La fecha escrita a mano, con su raya.
            textoEn(medidor, e.fecha, Rect(w * 0.07f, h * 0.1f, w * 0.5f, h * 0.24f), letra, c(TINTA), 15f, peso = FontWeight.SemiBold, minimo = 8f)
            drawPath(Path().apply { moveTo(w * 0.1f, h * 0.262f); quadraticTo(w * 0.28f, h * 0.25f, w * 0.47f, h * 0.258f) }, c(TINTA).copy(alpha = 0.6f), style = Stroke(0.8f * u))
            // Notas en espejo arriba a la derecha y el estudio de sanguina: el hombre en el círculo y el cuadrado, en figura.
            for (j in 0 until 3) espejo(w * 0.53f, w * 0.77f, h * (0.12f + j * 0.05f), c(SEPIA).copy(alpha = 0.32f), e.semilla + 20 + j)
            val o = Offset(w * 0.705f, h * 0.6f)
            val r = minOf(h * 0.14f, w * 0.075f)
            drawCircle(sanguina, r, o, style = Stroke(1.1f * u))
            drawRect(sanguina, Offset(o.x - r * 0.88f, o.y - r * 0.7f), Size(r * 1.76f, r * 1.76f), style = Stroke(1.1f * u))
            drawLine(sanguina, Offset(o.x - r, o.y - r * 0.15f), Offset(o.x + r, o.y - r * 0.15f), 1 * u)
            drawLine(sanguina, Offset(o.x, o.y - r * 0.55f), Offset(o.x, o.y + r * 0.5f), 1 * u)
            drawCircle(sanguina, r * 0.15f, Offset(o.x, o.y - r * 0.7f), style = Stroke(1 * u))
            listOf(-1f, 1f).forEach { s -> drawLine(sanguina, Offset(o.x, o.y + r * 0.5f), Offset(o.x + s * r * 0.5f, o.y + r * 1.02f), 1 * u) }
            // De noche, una luna dibujada a tinta y una carta de estrellas unidas por sus líneas.
            if (n) {
                val cl = Offset(w * 0.56f, h * 0.15f)
                val rl = 8 * u
                drawArc(c(SEPIA), 60f, 240f, false, Offset(cl.x - rl, cl.y - rl), Size(rl * 2, rl * 2), style = Stroke(1.2f * u))
                drawArc(c(SEPIA), 80f, 200f, false, Offset(cl.x - rl * 0.5f, cl.y - rl * 0.8f), Size(rl * 1.6f, rl * 1.6f), style = Stroke(0.8f * u))
                val puntos = (0 until 5).map { k -> Offset(w * (0.6f + 0.035f * k), h * (0.12f + 0.07f * azar(e.semilla, 60 + k))) }
                puntos.zipWithNext().forEach { (p, q) -> drawLine(c(SEPIA).copy(alpha = 0.5f), p, q, 0.6f * u) }
                puntos.forEach { p -> drawCircle(c(SEPIA), 1.3f * u, p) }
            }
            // La cuenta, abajo, a mano.
            if (!e.vacio && e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.07f, h * 0.75f, w * 0.5f, h * 0.87f), letra, c(SEPIA), 12f, minimo = 7f)
            }
            if (p2 > 0f) drawPath(tapa, Color.Black.copy(alpha = 0.35f * p2))
        }
        if (p2 > 0.01f) {
            val borde = w * 0.03f + (w * 0.8f - w * 0.03f) * sx
            for (k in 0 until 3) drawRect(Color.Black.copy(alpha = 0.1f * (3 - k) / 3f), Offset(borde, h * 0.07f), Size((4 + 4 * k) * u, h * 0.86f))
        }
        // La solapa que envuelve el canto, el botón de hueso y la presilla de cuero; se suelta primero.
        scale(1f - 0.9f * p1, 1f, Offset(w * 0.955f, h * 0.5f)) {
            val solapa = Path().apply {
                moveTo(w * 0.79f, h * 0.05f); lineTo(w * 0.95f, h * 0.12f); quadraticTo(w * 0.97f, h * 0.5f, w * 0.95f, h * 0.88f); lineTo(w * 0.79f, h * 0.93f); close()
            }
            drawPath(solapa, mezcla(piel, c(SEPIA), 0.28f))
            drawPath(solapa, c(SEPIA).copy(alpha = 0.55f), style = Stroke(1.2f * u))
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(w * 0.792f, h * 0.06f), Offset(w * 0.792f, h * 0.92f), 2 * u)
            for (j in 0 until 3) drawLine(c(SEPIA).copy(alpha = 0.15f), Offset(w * (0.83f + 0.03f * j), h * 0.1f), Offset(w * (0.835f + 0.03f * j), h * 0.9f), 0.8f * u)
            val b = Offset(w * 0.875f, h * 0.5f)
            drawLine(cinta, Offset(w * 0.76f, b.y), Offset(b.x + w * 0.025f, b.y), 2.6f * u)
            drawLine(Color.White.copy(alpha = 0.2f), Offset(w * 0.76f, b.y - 0.8f * u), Offset(b.x + w * 0.025f, b.y - 0.8f * u), 0.6f * u)
            val boton = Rect(b.x - 3 * u, b.y - h * 0.09f, b.x + 3 * u, b.y + h * 0.09f)
            drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(boton.left + 1 * u, boton.top + 1.5f * u), boton.size, CornerRadius(3 * u))
            drawRoundRect(c(CERA), boton.topLeft, boton.size, CornerRadius(3 * u))
            drawRoundRect(c(SEPIA), boton.topLeft, boton.size, CornerRadius(3 * u), style = Stroke(0.8f * u))
            // Las dos cintas de atar, que cuelgan del canto.
            listOf(0.2f, 0.8f).forEachIndexed { j, f ->
                val y = h * f
                drawLine(cinta, Offset(w * 0.93f, y), Offset(w * 0.985f, y + (if (j == 0) 4 else -4) * u), 2.2f * u)
                drawLine(cinta, Offset(w * 0.985f, y + (if (j == 0) 4 else -4) * u), Offset(w * 0.97f, y + (if (j == 0) 22 else 14) * u), 2f * u)
            }
        }
        // De noche, la vela alumbra desde un lado.
        if (n) clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.1f, -h * 0.05f), h * 1.1f, LLAMA.copy(alpha = 0.2f)) }
    }
}

/**
 * Pirata: la estantería del camarote del capitán. Roble oscuro de tablas machihembradas, y en cada repisa la barandilla de
 * mar (la barra de latón que impide que los libros caigan con el balanceo) entre dos balaustres torneados; las tablas con
 * su labio delantero y un fleje de latón, y cada mes en una placa de latón atornillada. Arriba, bajo el bao de cubierta
 * con sus curvas, las ventanas de popa con sus parteluces: de día, el cielo con nubes y el mar con espuma; de noche, el mar
 * oscuro con la luna, su reflejo y las estrellas. Del bao cuelga un farol, que de noche se enciende, y en el alféizar hay un
 * sextante de latón. Abajo, la cubierta de tablones con sus cabillas. Cada entrada es un cuaderno de bitácora en lona
 * alquitranada o en cuero, con su etiqueta de papel con el día, el mes pintado, el cabo que lo ata (una vuelta más cuanto
 * más escribiste) con su nudo y sus chicotes, la barandilla que pasa por delante y, bajo ella, un sello de lacre; de noche,
 * una luna. Distinto del Ideario, que es la bodega con sus cofres de marino.
 * La tapa es esa bitácora de frente: cantoneras de latón, el rótulo estampado, la mancha de sal, el rótulo de
 * pergamino con la fecha y lo escrito y el cabo con su nudo y su lacre; al abrirse el cabo cae, gira la tapa y aparece la
 * tabla de horas, nudos y rumbo (de noche, una luna en el nudo y la luz del farol).
 */
object EstantePirata : DisenoBiblioteca {
    private val ROBLE = Color(0xFF4E3524)
    private val ROBLE_LUZ = Color(0xFF6E4C33)
    private val ROBLE_OSC = Color(0xFF2E1E14)
    private val VETA = Color(0xFF1C120A)
    private val LATON = Color(0xFFC09A48)
    private val LATON_OSC = Color(0xFF7E6230)
    private val CIELO = Color(0xFFAFCBDD)
    private val NUBE = Color(0xFFE4ECF0)
    private val MAR = Color(0xFF3C6E8E)
    private val ESPUMA = Color(0xFFE6EEF0)
    private val CIELO_NOCHE = Color(0xFF141C34)
    private val MAR_NOCHE = Color(0xFF0C1828)
    private val LUNA = Color(0xFFEDE8CF)
    private val PAPEL = Color(0xFFE6DCC0)
    private val TINTA = Color(0xFF231A12)
    private val CABO = Color(0xFFB8A070)
    private val CABO_OSC = Color(0xFF7E6A44)
    private val LACRE = Color(0xFF8E2A20)
    private val LLAMA = Color(0xFFF7C45A)
    private val CRISTAL = Color(0xFFE8E0C0)
    private val CERA = Color(0xFFEFE6CC)
    /** Las tapas: lona alquitranada (negra, parda, verdosa, pizarra) y cuero (castaño, rojizo). */
    private val TAPAS = listOf(Color(0xFF2B2A26), Color(0xFF3A3328), Color(0xFF2E3530), Color(0xFF26302E), Color(0xFF6A4228), Color(0xFF553321))
    private val NOCHE = Color(0xFF101828)
    /** A cuántos dp del piso de la repisa corre la barandilla de mar (la misma en el mueble y en cada cuaderno). */
    private val RIEL = 22f

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, a la luz del farol. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0C0A08), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    override val corona = 0.34f
    override val base = 0.1f
    override val lado = 0.055f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 17.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.caslon

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.15f * azar(semilla, 1)

    /** La barandilla de mar, de [x0] a [x1] a la altura [y]: la dibujan igual el mueble y cada cuaderno. */
    private fun DrawScope.barandilla(x0: Float, x1: Float, y: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawRect(t(LATON_OSC, oscuro, noche), Offset(x0, y), Size(x1 - x0, 3.4f * u))
        drawRect(t(LATON, oscuro, noche), Offset(x0, y), Size(x1 - x0, 2f * u))
        drawRect(Color.White.copy(alpha = 0.3f), Offset(x0, y + 0.4f * u), Size(x1 - x0, 0.6f * u))
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo: tablas machihembradas de roble oscuro.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(ROBLE_OSC), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(VETA).copy(alpha = 0.4f), 15, densidad = 0.5f, vertical = true, grosor = 0.7f)
        var x = cuerpo.left + 18 * u
        while (x < cuerpo.right) {
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 1f * u)
            drawLine(Color.White.copy(alpha = 0.05f), Offset(x + 1 * u, cuerpo.top), Offset(x + 1 * u, cuerpo.bottom), 0.6f * u)
            x += 18 * u
        }
        // Cada repisa: la sombra de arriba y la barandilla de mar entre sus dos balaustres.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.3f), r.topLeft, Size(r.width, 10 * u))
            val yr = r.bottom - RIEL * u
            listOf(r.left + 3 * u, r.right - 3 * u).forEach { px ->
                drawRect(c(ROBLE_LUZ), Offset(px - 1.8f * u, yr), Size(3.6f * u, r.bottom - yr))
                drawCircle(c(ROBLE_LUZ), 2.6f * u, Offset(px, yr + (r.bottom - yr) * 0.45f))
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(px + 0.6f * u, yr), Size(1.2f * u, r.bottom - yr))
                drawCircle(c(LATON), 2 * u, Offset(px, yr + 1.7f * u))
            }
            barandilla(r.left, r.right, yr, oscuro, noche)
        }
        // Los costados: roble con tachuelas de latón.
        listOf(d.izquierda, d.derecha).forEachIndexed { k, r ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.5f), 17 + k, densidad = 1.1f, vertical = true, grosor = 0.7f)
            var yy = r.top + 10 * u
            while (yy < r.bottom - 6 * u) {
                drawCircle(c(LATON), 1.2f * u, Offset(r.center.x, yy))
                yy += 22 * u
            }
            bisel(r, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.32f), 1.4f * u)
        }
        // Las tablas: el labio delantero y un fleje de latón abajo.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.45f), 29 + k, densidad = 0.6f, grosor = 0.6f)
            drawRect(c(ROBLE_LUZ), Offset(r.left, r.top - 2 * u), Size(r.width, 3 * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.top + 1 * u), Size(r.width, 0.8f * u))
            drawRect(c(LATON_OSC), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
            drawRect(c(LATON).copy(alpha = 0.7f), Offset(r.left, r.bottom - 3 * u), Size(r.width, 1 * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una placa de latón atornillada, grabada.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
            drawRect(mezcla(c(LATON), LATON, 0.35f), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = 0.2f), r.topLeft, Size(r.width, r.height * 0.25f))
            drawRect(c(LATON_OSC), r.topLeft, r.size, style = Stroke(0.8f * u))
            listOf(r.left + 3 * u, r.right - 3 * u).forEach { sx ->
                drawCircle(c(LATON_OSC), 1.3f * u, Offset(sx, r.center.y))
                drawLine(c(TINTA).copy(alpha = 0.6f), Offset(sx - 1 * u, r.center.y - 1 * u), Offset(sx + 1 * u, r.center.y + 1 * u), 0.5f * u)
            }
            textoEn(medidor, p.texto, Rect(r.left + 6 * u, r.top, r.right - 6 * u, r.bottom), letra, c(TINTA), 11f, peso = FontWeight.SemiBold)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo con su chapa de latón, sobre la cubierta de tablones con sus cabillas.
        val b = d.base
        val piso = b.top + b.height * 0.6f
        drawRect(c(ROBLE_LUZ), Offset(0f, piso), Size(w, h - piso))
        var y = piso; var fila = 0
        while (y < h) {
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(0f, y), Offset(w, y), 1f * u)
            var cx = (if (fila % 2 == 0) 20f else 50f) * u
            while (cx < w) {
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(cx, y), Offset(cx, minOf(y + 7 * u, h)), 0.8f * u)
                drawCircle(c(ROBLE_OSC), 0.9f * u, Offset(cx + 4 * u, y + 3.5f * u))
                cx += 60 * u
            }
            y += 7 * u; fila++
        }
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(ROBLE), zocalo.topLeft, zocalo.size)
        vetas(zocalo, c(VETA).copy(alpha = 0.45f), 49, densidad = 0.7f, grosor = 0.7f)
        val chapa = Rect(zocalo.left, zocalo.top + zocalo.height * 0.55f, zocalo.right, zocalo.bottom)
        drawRect(c(LATON_OSC), chapa.topLeft, chapa.size)
        drawRect(c(LATON).copy(alpha = 0.7f), chapa.topLeft, Size(chapa.width, 1 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** El bao, las ventanas de popa con el mar, el farol y el sextante. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(ROBLE_OSC), r.topLeft, r.size)
        // El bao de cubierta y sus curvas en las puntas.
        val bao = Rect(r.left, r.top, r.right, r.top + r.height * 0.13f)
        drawRect(c(ROBLE), bao.topLeft, bao.size)
        vetas(bao, c(VETA).copy(alpha = 0.45f), 95, densidad = 0.7f, grosor = 0.7f)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(bao.left, bao.bottom - 2 * u), Size(bao.width, 2 * u))
        listOf(true, false).forEach { izq ->
            val x0 = if (izq) r.left else r.right
            val s = if (izq) 1f else -1f
            val curva = Path().apply { moveTo(x0, bao.bottom); lineTo(x0 + s * r.width * 0.07f, bao.bottom); quadraticTo(x0 + s * r.width * 0.02f, bao.bottom + 4 * u, x0, bao.bottom + r.height * 0.2f); close() }
            drawPath(curva, c(ROBLE))
        }
        // Las ventanas de popa.
        val zona = Rect(r.left + r.width * 0.04f, bao.bottom + r.height * 0.05f, r.right - r.width * 0.04f, r.top + r.height * 0.78f)
        drawRect(c(ROBLE), zona.topLeft, zona.size)
        val n = 4
        val gap = 5 * u
        val vw = (zona.width - gap * (n + 1)) / n
        for (k in 0 until n) {
            val x0 = zona.left + gap + k * (vw + gap)
            ventana(Rect(x0, zona.top + gap, x0 + vw, zona.bottom - gap), k, oscuro, noche)
        }
        // El alféizar y la moldura de abajo.
        val alfeizar = Rect(r.left, zona.bottom, r.right, zona.bottom + r.height * 0.06f)
        drawRect(c(ROBLE_LUZ), alfeizar.topLeft, alfeizar.size)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(alfeizar.left, alfeizar.bottom - 1.6f * u), Size(alfeizar.width, 1.6f * u))
        val moldura = Rect(r.left, alfeizar.bottom, r.right, r.bottom)
        drawRect(c(ROBLE), moldura.topLeft, moldura.size)
        drawLine(c(LATON).copy(alpha = 0.6f), Offset(moldura.left, moldura.center.y), Offset(moldura.right, moldura.center.y), 0.8f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(moldura.left, moldura.bottom - 2 * u), Size(moldura.width, 2 * u))
        // El sextante, de pie sobre su arco en el alféizar.
        sextante(Offset(r.right - r.width * 0.2f, alfeizar.top), r.height * 0.24f, oscuro, noche)
        // El farol colgado del bao; de noche, encendido.
        val fx = r.center.x
        val arriba = bao.bottom
        val cuerpoF = Rect(fx - 6 * u, arriba + 12 * u, fx + 6 * u, arriba + 30 * u)
        if (noche) mancha(cuerpoF.center, r.height * 0.55f, LLAMA.copy(alpha = 0.42f))
        drawLine(c(LATON_OSC), Offset(fx, arriba), Offset(fx, arriba + 6 * u), 1f * u)
        drawCircle(c(LATON), 2 * u, Offset(fx, arriba + 7.5f * u), style = Stroke(1f * u))
        val capuchon = Path().apply { moveTo(fx - 7 * u, cuerpoF.top); lineTo(fx, arriba + 9.5f * u); lineTo(fx + 7 * u, cuerpoF.top); close() }
        drawPath(capuchon, c(LATON))
        drawRect(if (noche) Color(0xFFF9DE90) else c(CRISTAL).copy(alpha = 0.7f), cuerpoF.topLeft, cuerpoF.size)
        if (noche) {
            val llama = Path().apply {
                moveTo(fx, cuerpoF.center.y - 5 * u)
                quadraticTo(fx + 2.6f * u, cuerpoF.center.y, fx, cuerpoF.center.y + 3 * u)
                quadraticTo(fx - 2.6f * u, cuerpoF.center.y, fx, cuerpoF.center.y - 5 * u); close()
            }
            drawPath(llama, LLAMA)
        } else {
            drawRect(c(CERA), Offset(fx - 1.6f * u, cuerpoF.center.y - 1 * u), Size(3.2f * u, cuerpoF.bottom - cuerpoF.center.y))
        }
        listOf(cuerpoF.left, fx, cuerpoF.right).forEach { bx -> drawLine(c(LATON_OSC), Offset(bx, cuerpoF.top), Offset(bx, cuerpoF.bottom), 1f * u) }
        drawRect(c(LATON), Offset(cuerpoF.left - 1.5f * u, cuerpoF.bottom), Size(cuerpoF.width + 3 * u, 3 * u))
        drawRect(c(LATON), Offset(cuerpoF.left - 1 * u, cuerpoF.top - 1 * u), Size(cuerpoF.width + 2 * u, 2 * u))
    }

    /** Una ventana de popa: cielo y mar (de noche, la luna en la segunda) con sus parteluces. */
    private fun DrawScope.ventana(vr: Rect, k: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        val horizonte = vr.top + vr.height * 0.55f
        drawRect(if (noche) CIELO_NOCHE else t(CIELO, oscuro, false), vr.topLeft, Size(vr.width, horizonte - vr.top))
        drawRect(if (noche) MAR_NOCHE else t(MAR, oscuro, false), Offset(vr.left, horizonte), Size(vr.width, vr.bottom - horizonte))
        clipRect(vr.left, vr.top, vr.right, vr.bottom) {
            if (noche) {
                for (j in 0 until 6) {
                    val s = k * 13 + j
                    drawCircle(LUNA.copy(alpha = 0.7f), (0.5f + 0.6f * azar(s, 2)) * u, Offset(vr.left + vr.width * azar(s, 3), vr.top + (horizonte - vr.top) * 0.9f * azar(s, 4)))
                }
                if (k == 1) {
                    val cl = Offset(vr.left + vr.width * 0.6f, vr.top + (horizonte - vr.top) * 0.42f)
                    drawCircle(LUNA, vr.width * 0.15f, cl)
                    var ry = horizonte + 2 * u; var j = 0
                    while (ry < vr.bottom) {
                        val medio = vr.width * (0.14f - 0.015f * j).coerceAtLeast(0.03f)
                        drawLine(LUNA.copy(alpha = 0.55f), Offset(cl.x - medio, ry), Offset(cl.x + medio, ry), 1f * u)
                        ry += 3.2f * u; j++
                    }
                }
            } else {
                drawOval(NUBE.copy(alpha = if (oscuro) 0.4f else 0.75f), Offset(vr.left + vr.width * (0.1f + 0.4f * azar(k, 5)), vr.top + (horizonte - vr.top) * 0.25f), Size(vr.width * 0.5f, (horizonte - vr.top) * 0.18f))
            }
            // Las olas.
            var oy = horizonte + 3 * u; var fila = 0
            while (oy < vr.bottom) {
                var ox = vr.left + (if (fila % 2 == 0) 2f else 6f) * u
                while (ox < vr.right) {
                    drawLine((if (noche) LUNA else ESPUMA).copy(alpha = if (noche) 0.15f else 0.45f), Offset(ox, oy), Offset(ox + 3.5f * u, oy), 0.7f * u)
                    ox += 9 * u
                }
                oy += 4 * u; fila++
            }
        }
        val marco = t(ROBLE_OSC, oscuro, noche)
        drawLine(marco, Offset(vr.center.x, vr.top), Offset(vr.center.x, vr.bottom), 1.6f * u)
        for (j in 1..2) drawLine(marco, Offset(vr.left, vr.top + vr.height * j / 3f), Offset(vr.right, vr.top + vr.height * j / 3f), 1.6f * u)
        drawRect(t(ROBLE, oscuro, noche), vr.topLeft, vr.size, style = Stroke(2.4f * u))
        drawLine(Color.White.copy(alpha = 0.16f), Offset(vr.left + 3 * u, vr.bottom - 3 * u), Offset(vr.left + vr.width * 0.4f, vr.top + 3 * u), 1.2f * u)
    }

    /** El sextante de latón, apoyado en su arco con la punta arriba. */
    private fun DrawScope.sextante(pie: Offset, s: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        val lat = t(LATON, oscuro, noche)
        val apice = Offset(pie.x, pie.y - s)
        drawArc(lat, 60f, 60f, false, Offset(apice.x - s, apice.y - s), Size(2 * s, 2 * s), style = Stroke(2.4f * u))
        val a1 = Math.toRadians(60.0); val a2 = Math.toRadians(120.0)
        val p1 = Offset(apice.x + kotlin.math.cos(a1).toFloat() * s, apice.y + kotlin.math.sin(a1).toFloat() * s)
        val p2 = Offset(apice.x + kotlin.math.cos(a2).toFloat() * s, apice.y + kotlin.math.sin(a2).toFloat() * s)
        drawLine(lat, apice, p1, 1.6f * u)
        drawLine(lat, apice, p2, 1.6f * u)
        drawLine(lat, Offset((apice.x + p1.x) / 2, (apice.y + p1.y) / 2), Offset((apice.x + p2.x) / 2, (apice.y + p2.y) / 2), 1f * u)
        val a3 = Math.toRadians(82.0)
        drawLine(t(LATON_OSC, oscuro, noche), apice, Offset(apice.x + kotlin.math.cos(a3).toFloat() * s * 1.04f, apice.y + kotlin.math.sin(a3).toFloat() * s * 1.04f), 1.4f * u)
        drawLine(t(ROBLE_OSC, oscuro, noche), Offset(apice.x - s * 0.35f, apice.y + s * 0.42f), Offset(apice.x + s * 0.2f, apice.y + s * 0.42f), 2.6f * u)
        drawRect(t(CRISTAL, oscuro, noche), Offset(apice.x - 2 * u, apice.y - 3 * u), Size(4 * u, 5 * u))
        drawRect(lat, Offset(apice.x - 2 * u, apice.y - 3 * u), Size(4 * u, 5 * u), style = Stroke(0.6f * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val k = (v.semilla and 0x7fffffff) % TAPAS.size
        val lona = k < 4
        val tinte = TAPAS[k]
        val tapa = c(if (v.tono != null) mezcla(tinte, v.tono, 0.22f) else tinte)
        drawRoundRect(tapa, Offset.Zero, size, CornerRadius(1.6f * u))
        if (lona) {
            // La trama de la lona y el brillo de la brea.
            var ty = 1.5f * u
            while (ty < h) { drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, ty), Offset(w, ty), 0.5f * u); ty += 2.2f * u }
            var tx = 1.5f * u
            while (tx < w) { drawLine(Color.Black.copy(alpha = 0.1f), Offset(tx, 0f), Offset(tx, h), 0.5f * u); tx += 2.2f * u }
            drawOval(Color.White.copy(alpha = 0.06f), Offset(w * 0.2f, h * 0.3f), Size(w * 0.4f, h * 0.2f))
        } else {
            for (j in 0 until 3) {
                val o = Offset(w * azar(v.semilla, 20 + j), h * azar(v.semilla, 30 + j))
                drawOval(Color.Black.copy(alpha = 0.1f), Offset(o.x - 2.5f * u, o.y - 1.5f * u), Size(5 * u, 3 * u))
            }
        }
        drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.06f, 0f), Size(w * 0.12f, h))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.86f, 0f), Size(w * 0.14f, h))
        // La etiqueta de papel con el día.
        val etiqueta = Rect(w * 0.14f, h * 0.1f, w * 0.86f, h * 0.32f)
        drawRect(c(PAPEL), etiqueta.topLeft, etiqueta.size)
        drawRect(c(TINTA), etiqueta.topLeft, etiqueta.size, style = Stroke(0.6f * u))
        val filete = etiqueta.deflate(1.4f * u)
        drawRect(c(TINTA).copy(alpha = 0.5f), filete.topLeft, filete.size, style = Stroke(0.4f * u))
        textoEn(medidor, v.dia, etiqueta.deflate(1.6f * u), letra, c(TINTA), 14f, peso = FontWeight.SemiBold)
        // El mes, pintado en la tapa.
        textoEn(medidor, v.mes, Rect(w * 0.22f, h * 0.36f, w * 0.78f, h * 0.58f), letra, mezcla(c(PAPEL), PAPEL, 0.4f), 9f, vertical = true, minimo = 5f)
        // El cabo que lo ata: una vuelta más cuanto más escribiste, con su nudo y sus chicotes.
        val yr = h - RIEL * u
        val vueltas = 1 + (v.lleno * 2).toInt()
        val y0 = h * 0.62f
        for (j in 0 until vueltas) {
            val y = y0 + j * 2.8f * u
            drawLine(c(CABO_OSC), Offset(0f, y + 0.6f * u), Offset(w, y + 0.6f * u), 2f * u)
            drawLine(c(CABO), Offset(0f, y), Offset(w, y), 1.6f * u)
            var cx = 1 * u
            while (cx < w) { drawLine(c(CABO_OSC), Offset(cx, y - 0.8f * u), Offset(cx + 1.2f * u, y + 0.8f * u), 0.5f * u); cx += 2.2f * u }
        }
        val nudo = Offset(w * 0.5f, y0 + (vueltas - 1) * 1.4f * u)
        drawLine(c(CABO), nudo, Offset(nudo.x - 3 * u, minOf(nudo.y + 9 * u, yr - 1 * u)), 1.4f * u)
        drawLine(c(CABO), nudo, Offset(nudo.x + 2.5f * u, minOf(nudo.y + 8 * u, yr - 1 * u)), 1.4f * u)
        drawCircle(c(CABO), 2.4f * u, nudo)
        drawCircle(c(CABO_OSC), 2.4f * u, nudo, style = Stroke(0.6f * u))
        // La barandilla de mar, que pasa por delante.
        barandilla(0f, w, yr, oscuro, v.noche)
        // Bajo la barandilla, el sello de lacre; de noche, una luna.
        val cs = Offset(w * 0.5f, (yr + 3.4f * u + h) / 2)
        val rs = minOf(w * 0.16f, 4 * u)
        if (v.noche) {
            drawCircle(LUNA, rs, cs)
            drawCircle(tapa, rs * 0.8f, Offset(cs.x + rs * 0.4f, cs.y - rs * 0.2f))
        } else {
            drawCircle(c(LACRE), rs, cs)
            drawCircle(Color.Black.copy(alpha = 0.25f), rs * 0.6f, cs, style = Stroke(0.6f * u))
        }
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.165f, 0.315f, 0.765f, 0.665f)
    override val letraTapa: FontFamily = LetraExtra.caslon
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** El papel del rótulo y de las hojas: se hunde menos que la tapa, para que el extracto se lea siempre. */
    private fun papel(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPEL
        if (oscuro) r = mezcla(r, Color(0xFF0C0A08), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val k = (e.semilla and 0x7fffffff) % TAPAS.size
        val lona = k < 4
        val cubierta = c(TAPAS[k])
        val hoja = papel(oscuro, n)
        val laton = mezcla(c(LATON), LATON, 0.25f)
        val libro = Rect(w * 0.025f, h * 0.04f, w * 0.935f, h * 0.91f)
        val lomo = Rect(libro.left, libro.top, libro.left + minOf(w * 0.065f, 26 * u), libro.bottom)
        val tapa = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        // La sombra y el canto de las hojas, onduladas por la humedad.
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(libro.left + 3 * u, libro.top + 5 * u), libro.size, CornerRadius(3 * u))
        val bloque = Rect(tapa.left, tapa.top + 3 * u, tapa.right + 3 * u, tapa.bottom + 3 * u)
        drawRect(c(PAPEL), bloque.topLeft, bloque.size)
        var cy = bloque.top + 2 * u; var j = 0
        while (cy < bloque.bottom - 1 * u) {
            drawLine(c(TINTA).copy(alpha = 0.2f), Offset(tapa.right, cy), Offset(bloque.right, cy + (if (j % 3 == 0) 1 else 0) * u), 0.5f * u)
            cy += 1.6f * u; j++
        }
        // La hoja del diario de navegación, que se ve al abrir: la tabla de horas, nudos, rumbo y viento, y sus asientos.
        if (a > 0.01f) {
            val pag = Rect(tapa.left + 1 * u, tapa.top + 3 * u, tapa.right - 1 * u, tapa.bottom - 1 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            listOf(10f to 0.14f, 5f to 0.12f).forEach { (an, al) -> drawRect(Color.Black.copy(alpha = al), pag.topLeft, Size(an * u, pag.height)) }
            val tabla = Rect(pag.left + 14 * u, pag.top + 10 * u, pag.right - 10 * u, pag.bottom - 10 * u)
            val cols = listOf(0f, 0.1f, 0.2f, 0.38f, 0.56f, 1f)
            val titulos = listOf("H", "N", "Rumbo", "Viento", "Novedades")
            val cab = tabla.top + 13 * u
            titulos.forEachIndexed { q, s ->
                textoEn(medidor, s, Rect(tabla.left + tabla.width * cols[q], tabla.top, tabla.left + tabla.width * cols[q + 1], cab), letra, c(TINTA).copy(alpha = 0.8f), 9f, minimo = 5f)
            }
            drawLine(c(TINTA).copy(alpha = 0.6f), Offset(tabla.left, cab), Offset(tabla.right, cab), 0.9f * u)
            cols.forEach { f -> drawLine(c(TINTA).copy(alpha = 0.35f), Offset(tabla.left + tabla.width * f, tabla.top), Offset(tabla.left + tabla.width * f, tabla.bottom), 0.6f * u) }
            var ry = cab + 9 * u; var fila = 0
            while (ry < tabla.bottom) {
                drawLine(c(TINTA).copy(alpha = 0.18f), Offset(tabla.left, ry + 2.5f * u), Offset(tabla.right, ry + 2.5f * u), 0.5f * u)
                for (q in 0 until 5) {
                    val x0 = tabla.left + tabla.width * cols[q] + 3 * u
                    val x1 = tabla.left + tabla.width * cols[q + 1] - 3 * u
                    val largo = (x1 - x0) * (if (q == 4) 0.4f + 0.6f * azar(e.semilla + fila, q) else 0.5f + 0.4f * azar(e.semilla + fila, q))
                    if (x1 > x0) drawLine(c(TINTA).copy(alpha = 0.55f), Offset(x0, ry), Offset(x0 + largo, ry), 1.3f * u)
                }
                ry += 8 * u; fila++
            }
        }
        // La tapa de lona embreada o de cuero, que gira sobre el lomo al abrirse.
        val sx = 1f - 0.9f * a
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(cubierta, Color.White, 0.12f), cubierta, mezcla(cubierta, Color.Black, 0.35f)), tapa.top, tapa.bottom),
                tapa.topLeft, tapa.size, CornerRadius(3 * u))
            clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) {
                if (lona) {
                    var ty = tapa.top + 1.5f * u
                    while (ty < tapa.bottom) { drawLine(Color.White.copy(alpha = 0.04f), Offset(tapa.left, ty), Offset(tapa.right, ty), 0.5f * u); ty += 2.4f * u }
                    var tx = tapa.left + 1.5f * u
                    while (tx < tapa.right) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(tx, tapa.top), Offset(tx, tapa.bottom), 0.5f * u); tx += 2.4f * u }
                    drawOval(Color.White.copy(alpha = 0.05f), Offset(tapa.left + tapa.width * 0.1f, tapa.top + tapa.height * 0.1f), Size(tapa.width * 0.5f, tapa.height * 0.3f))
                } else {
                    for (q in 0 until 8) {
                        val o = Offset(tapa.left + tapa.width * azar(e.semilla, 20 + q), tapa.top + tapa.height * azar(e.semilla, 30 + q))
                        drawOval(Color.Black.copy(alpha = 0.1f), Offset(o.x - 8 * u, o.y - 3 * u), Size(16 * u, 6 * u))
                    }
                }
                // La mancha de agua de mar: los cercos de sal que dejó al secarse.
                val cm = Offset(tapa.left + tapa.width * 0.93f, tapa.top + tapa.height * 0.28f)
                for (q in 0 until 4) drawCircle(c(PAPEL).copy(alpha = 0.07f + 0.03f * q), tapa.height * (0.24f - 0.045f * q), cm, style = Stroke((1.4f - 0.2f * q) * u))
                drawCircle(c(PAPEL).copy(alpha = 0.05f), tapa.height * 0.2f, cm)
            }
            // Las cantoneras de latón con sus clavos.
            val e0 = minOf(tapa.height * 0.17f, 30 * u)
            listOf(tapa.topLeft to Offset(1f, 1f), tapa.topRight to Offset(-1f, 1f), tapa.bottomLeft to Offset(1f, -1f), tapa.bottomRight to Offset(-1f, -1f)).forEach { (o, d) ->
                val tri = Path().apply { moveTo(o.x, o.y); lineTo(o.x + d.x * e0, o.y); lineTo(o.x, o.y + d.y * e0); close() }
                drawPath(tri, laton)
                drawPath(tri, mezcla(laton, Color.Black, 0.35f), style = Stroke(0.8f * u))
                drawLine(Color.White.copy(alpha = 0.3f), Offset(o.x + d.x * e0 * 0.8f, o.y + d.y * 1.5f * u), Offset(o.x + d.x * 1.5f * u, o.y + d.y * e0 * 0.8f), 0.7f * u)
                drawCircle(mezcla(laton, Color.Black, 0.45f), 1.4f * u, Offset(o.x + d.x * e0 * 0.28f, o.y + d.y * e0 * 0.28f))
            }
            // El rótulo estampado arriba.
            textoEn(medidor, "CUADERNO DE BITÁCORA", Rect(w * 0.15f, tapa.top + 4 * u, w * 0.78f, h * 0.17f), letra, laton, 10f, peso = FontWeight.SemiBold, espaciado = 1.6f, minimo = 6f)
            // El rótulo de pergamino: la fecha arriba, lo escrito en medio y la cuenta abajo.
            val rotulo = Rect(w * 0.13f, h * 0.19f, w * 0.8f, h * 0.775f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(rotulo.left + 1.5f * u, rotulo.top + 2 * u), rotulo.size)
            drawRect(hoja, rotulo.topLeft, rotulo.size)
            drawRect(c(TINTA).copy(alpha = 0.6f), rotulo.topLeft, rotulo.size, style = Stroke(1 * u))
            val filete = rotulo.deflate(2.5f * u)
            drawRect(c(TINTA).copy(alpha = 0.3f), filete.topLeft, filete.size, style = Stroke(0.6f * u))
            textoEn(medidor, e.fecha, Rect(filete.left + 6 * u, filete.top + 1 * u, filete.right - 6 * u, h * 0.295f), letra, c(TINTA), 13f, peso = FontWeight.SemiBold, minimo = 7f)
            drawLine(c(TINTA).copy(alpha = 0.45f), Offset(filete.left + 18 * u, h * 0.3f), Offset(filete.right - 18 * u, h * 0.3f), 0.6f * u)
            drawLine(c(TINTA).copy(alpha = 0.45f), Offset(filete.left + 18 * u, h * 0.68f), Offset(filete.right - 18 * u, h * 0.68f), 0.6f * u)
            if (!e.vacio && e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(filete.left + 6 * u, h * 0.685f, filete.right - 6 * u, filete.bottom - 1 * u), letra, c(TINTA).copy(alpha = 0.8f), 10.5f, minimo = 6f)
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.4f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (q in 0 until 3) drawRect(Color.Black.copy(alpha = 0.12f * (3 - q) / 3f), Offset(borde, tapa.top + 3 * u), Size((4 + 4 * q) * u, tapa.height - 4 * u))
        }
        // El lomo, más gastado.
        drawRoundRect(mezcla(cubierta, Color.Black, 0.2f), lomo.topLeft, lomo.size, CornerRadius(3 * u))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.2f, lomo.height))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(lomo.right - 2 * u, lomo.top), Size(2 * u, lomo.height))
        listOf(0.15f, 0.85f).forEach { f -> drawRect(Color.Black.copy(alpha = 0.3f), Offset(lomo.left, lomo.top + lomo.height * f - 1.5f * u), Size(lomo.width, 3 * u)) }
        // El cabo de cáñamo que rodea la bitácora y se anuda en el canto, con sus chicotes y el sello de lacre (de noche,
        // una luna). Al abrir, el cabo ya se soltó y cae.
        val suelto = (1f - a * 2.5f).coerceIn(0f, 1f)
        if (suelto > 0f) {
            val yc = h * 0.85f + (1f - suelto) * 16 * u
            drawLine(c(CABO_OSC).copy(alpha = suelto), Offset(0f, yc + 1.4f * u), Offset(w * 0.9f, yc + 1.4f * u), 4 * u)
            drawLine(c(CABO).copy(alpha = suelto), Offset(0f, yc), Offset(w * 0.9f, yc), 3.2f * u)
            var x = 0f
            while (x < w * 0.9f) { drawLine(c(CABO_OSC).copy(alpha = 0.7f * suelto), Offset(x, yc - 1.6f * u), Offset(x + 2.2f * u, yc + 1.6f * u), 0.7f * u); x += 3.2f * u }
            val nudo = Offset(w * 0.9f, yc)
            drawLine(c(CABO).copy(alpha = suelto), nudo, Offset(nudo.x + 6 * u, minOf(nudo.y + 14 * u, h - 1 * u)), 2.6f * u)
            drawLine(c(CABO).copy(alpha = suelto), nudo, Offset(nudo.x - 3 * u, minOf(nudo.y + 15 * u, h - 1 * u)), 2.6f * u)
            drawCircle(c(CABO).copy(alpha = suelto), 5.5f * u, nudo)
            drawCircle(c(CABO_OSC).copy(alpha = suelto), 5.5f * u, nudo, style = Stroke(0.9f * u))
            drawArc(c(CABO_OSC).copy(alpha = suelto), 200f, 140f, false, Offset(nudo.x - 3.5f * u, nudo.y - 3.5f * u), Size(7 * u, 7 * u), style = Stroke(0.8f * u))
            val cs = Offset(w * 0.86f, yc - 1 * u)
            val rs = 6 * u
            if (n) {
                drawCircle(LUNA.copy(alpha = suelto), rs, cs)
                drawCircle(cubierta.copy(alpha = suelto), rs * 0.8f, Offset(cs.x + rs * 0.4f, cs.y - rs * 0.2f))
            } else {
                drawCircle(Color.Black.copy(alpha = 0.25f * suelto), rs, Offset(cs.x + 1 * u, cs.y + 1.2f * u))
                drawCircle(c(LACRE).copy(alpha = suelto), rs, cs)
                drawCircle(Color.Black.copy(alpha = 0.25f * suelto), rs * 0.62f, cs, style = Stroke(0.7f * u))
                drawPath(Path().apply { moveTo(cs.x - rs * 0.3f, cs.y + rs * 0.3f); lineTo(cs.x, cs.y - rs * 0.35f); lineTo(cs.x + rs * 0.3f, cs.y + rs * 0.3f) }, Color.Black.copy(alpha = 0.3f * suelto), style = Stroke(0.7f * u))
            }
        }
        // De noche, el farol del camarote alumbra desde arriba.
        if (n) clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.5f, -h * 0.15f), h * 1.15f, LLAMA.copy(alpha = 0.2f)) }
    }
}

/**
 * Revolución: las cajas verdes de los libreros del muelle, sobre el pretil del río. El mueble es la caja por dentro:
 * listones verdes oscuros, costados con escuadras de hierro y el candado, tablas con el canto de hierro y la pintura
 * saltada, y cada mes en una etiqueta de papel pegada un poco torcida. Arriba, las dos tapas abiertas con los carteles
 * pegados por dentro (bandas tricolores, la escarapela, renglones: formas, no letras) y su tirante de hierro; detrás, la
 * otra orilla con sus casas de tejado de pizarra y el río; en medio, el farol de la calle en su poste de fundición. De
 * noche el farol se enciende, su reflejo tiembla en el río y se encienden algunas ventanas. Abajo, el pretil de sillares
 * con su albardilla y los adoquines del muelle. Cada entrada es un folleto cosido o un libro de lance (papel azul, gris,
 * becerro, jaspeado, ocre, verde) con la escarapela tricolor y sus cintas, la etiqueta de papel con el día, el mes en una
 * tira de papel y el sello del librero abajo; de noche, una luna en su lugar. Las hojas asoman más cuanto más escribiste.
 * Distinto del Ideario, que es el estante de la sección con sus colgaduras.
 * La tapa es ese folleto o libro de lance de frente: la cinta tricolor con la escarapela, la etiqueta con la fecha y
 * lo escrito, el sello del librero y la divisa; al abrirse gira y deja ver la portada impresa (de noche, una luna y la
 * luz del farol).
 */
object EstanteRevolucion : DisenoBiblioteca {
    private val VERDE = Color(0xFF2F4A34)
    private val VERDE_LUZ = Color(0xFF4A6A4E)
    private val VERDE_OSC = Color(0xFF1A2A1E)
    private val HIERRO = Color(0xFF26282A)
    private val HIERRO_LUZ = Color(0xFF5E6266)
    private val PIEDRA = Color(0xFFC8BFA8)
    private val PIEDRA_JUNTA = Color(0xFF948B76)
    private val ADOQUIN = Color(0xFF7E7A70)
    private val PAPEL = Color(0xFFEDE4CC)
    private val PAPEL_VIEJO = Color(0xFFD9CBA6)
    private val TINTA = Color(0xFF1E1C1A)
    private val AZUL = Color(0xFF2B3F8C)
    private val BLANCO = Color(0xFFF2EEE4)
    private val ROJO = Color(0xFFB0282A)
    private val CIELO = Color(0xFFC9D3D6)
    private val ORILLA = Color(0xFF9E9A8E)
    private val TEJADO = Color(0xFF6E7378)
    private val RIO = Color(0xFF6F8A8A)
    private val CIELO_NOCHE = Color(0xFF141A2A)
    private val ORILLA_NOCHE = Color(0xFF232838)
    private val RIO_NOCHE = Color(0xFF0F1A22)
    private val VENTANA_NOCHE = Color(0xFFE8C66A)
    private val VIDRIO = Color(0xFFDCE2DC)
    private val FAROL = Color(0xFFF3D27A)
    private val LUNA = Color(0xFFEDE6C8)
    /** Las cubiertas: papel azul, papel gris, becerro, jaspeado rojizo, ocre y verde. */
    private val CUBIERTAS = listOf(Color(0xFF5A6E8A), Color(0xFF7A8A9A), Color(0xFF7A4E2E), Color(0xFF8A4A3A), Color(0xFFB08A4A), Color(0xFF4E6A4A))
    private val NOCHE = Color(0xFF141A2C)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, a la luz del farol. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0B0D0C), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.46f)
        return r
    }

    override val corona = 0.4f
    override val base = 0.12f
    override val lado = 0.05f
    override val altoRepisa: Dp = 110.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.convencion

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.72f + 0.22f * azar(semilla, 1)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de la caja: listones verdes a lo largo.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(VERDE_OSC), cuerpo.topLeft, cuerpo.size)
        var y = cuerpo.top + 14 * u
        while (y < cuerpo.bottom) {
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 1f * u)
            drawLine(Color.White.copy(alpha = 0.05f), Offset(cuerpo.left, y + 1 * u), Offset(cuerpo.right, y + 1 * u), 0.6f * u)
            y += 14 * u
        }
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.3f), r.topLeft, Size(r.width, 9 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), r.topLeft, Size(5 * u, r.height))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.right - 5 * u, r.top), Size(5 * u, r.height))
        }
        // Los costados: la pared verde de la caja, con escuadras de hierro y, a la derecha, el candado.
        listOf(d.izquierda, d.derecha).forEachIndexed { k, r ->
            drawRect(c(VERDE), r.topLeft, r.size)
            drawRect(c(VERDE_LUZ).copy(alpha = 0.5f), r.topLeft, Size(r.width * 0.25f, r.height))
            d.tablas.forEach { tb ->
                drawRect(c(HIERRO), Offset(r.left, tb.top - 6 * u), Size(r.width, 3 * u))
                drawCircle(c(HIERRO_LUZ), 0.9f * u, Offset(r.left + r.width * 0.3f, tb.top - 4.5f * u))
                drawCircle(c(HIERRO_LUZ), 0.9f * u, Offset(r.right - r.width * 0.3f, tb.top - 4.5f * u))
            }
            if (k == 1) d.repisas.firstOrNull()?.let { rp ->
                val cx = r.center.x
                val cy = rp.top + 18 * u
                drawRect(c(HIERRO), Offset(cx - 2.5f * u, cy - 9 * u), Size(5 * u, 12 * u))
                drawArc(c(HIERRO_LUZ), 180f, 180f, false, Offset(cx - 3 * u, cy - 1 * u), Size(6 * u, 6 * u), style = Stroke(1.2f * u))
                drawRoundRect(c(HIERRO_LUZ), Offset(cx - 4 * u, cy + 2 * u), Size(8 * u, 7 * u), CornerRadius(1.5f * u))
                drawCircle(Color.Black, 0.9f * u, Offset(cx, cy + 5 * u))
            }
            bisel(r, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.3f), 1.2f * u)
        }
        // Las tablas: el canto de la caja, con su fleje de hierro y la pintura saltada.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(VERDE), r.topLeft, r.size)
            drawRect(c(VERDE_LUZ), r.topLeft, Size(r.width, 2 * u))
            drawRect(c(HIERRO), Offset(r.left, r.bottom - 2.4f * u), Size(r.width, 2.4f * u))
            for (j in 0 until 5) {
                val x = r.left + r.width * azar(k * 11 + j, 17)
                drawRect(c(PIEDRA_JUNTA).copy(alpha = 0.5f), Offset(x, r.top + r.height * (0.25f + 0.4f * azar(k * 11 + j, 18))), Size((2 + 4 * azar(k * 11 + j, 19)) * u, 1.6f * u))
            }
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una etiqueta de papel pegada, un poco torcida, con su doble filete.
        d.placas.forEachIndexed { k, p ->
            val r = p.r
            rotate(if (k % 2 == 0) -1.5f else 1.2f, r.center) {
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
                drawRect(c(PAPEL), r.topLeft, r.size)
                drawRect(c(PAPEL_VIEJO).copy(alpha = 0.6f), r.topLeft, Size(r.width * 0.18f, r.height))
                val f1 = r.deflate(1.2f * u)
                drawRect(c(TINTA).copy(alpha = 0.7f), f1.topLeft, f1.size, style = Stroke(0.6f * u))
                val f2 = r.deflate(2.2f * u)
                drawRect(c(TINTA).copy(alpha = 0.4f), f2.topLeft, f2.size, style = Stroke(0.4f * u))
                textoEn(medidor, p.texto, r.deflate(2.6f * u), letra, c(TINTA), 10.5f, peso = FontWeight.Bold)
            }
        }
        corona(d.corona, oscuro, noche)
        // La base: el pretil de sillares con su albardilla y los adoquines del muelle.
        val b = d.base
        val piso = b.top + b.height * 0.72f
        drawRect(c(ADOQUIN), Offset(0f, piso), Size(w, h - piso))
        var ay = piso + 1 * u; var fila = 0
        while (ay < h) {
            var ax = if (fila % 2 == 0) 0f else 5 * u
            while (ax < w) {
                drawRoundRect(Color.Black.copy(alpha = 0.18f), Offset(ax + 0.6f * u, ay), Size(9 * u, 4.4f * u), CornerRadius(1.6f * u), style = Stroke(0.6f * u))
                ax += 10 * u
            }
            ay += 5 * u; fila++
        }
        val pretil = Rect(b.left, b.top, b.right, piso)
        drawRect(c(PIEDRA), pretil.topLeft, pretil.size)
        val albardilla = pretil.top + pretil.height * 0.3f
        drawRect(Color.White.copy(alpha = 0.12f), pretil.topLeft, Size(pretil.width, albardilla - pretil.top))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(pretil.left, albardilla), Size(pretil.width, 2 * u))
        var sx = pretil.left + 18 * u; var sk = 0
        while (sx < pretil.right) {
            drawLine(c(PIEDRA_JUNTA), Offset(sx, albardilla + 2 * u), Offset(sx, piso), 1f * u)
            sx += 40 * u + 8 * u * azar(sk, 23); sk++
        }
        var jx = pretil.left + 30 * u
        while (jx < pretil.right) { drawLine(c(PIEDRA_JUNTA), Offset(jx, pretil.top), Offset(jx, albardilla), 0.8f * u); jx += 60 * u }
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(pretil.left, pretil.top), Size(pretil.width, 2.4f * u))
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(pretil.left, piso), Size(pretil.width, 2.4f * u))
    }

    /** El cielo, la otra orilla, el río, el farol y las dos tapas abiertas con sus carteles. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val horizonte = r.top + r.height * 0.3f
        val agua = r.top + r.height * 0.42f
        drawRect(if (noche) CIELO_NOCHE else c(CIELO), r.topLeft, Size(r.width, agua - r.top))
        // La otra orilla: casas de tejado de pizarra, con ventanas (de noche, alguna encendida).
        var bx = r.left; var bk = 0
        while (bx < r.right) {
            val bw = (26 + 18 * azar(bk, 41)) * u
            val bh = r.height * (0.1f + 0.08f * azar(bk, 42))
            val casa = Rect(bx, horizonte - bh, bx + bw, agua)
            drawRect(if (noche) ORILLA_NOCHE else c(ORILLA), casa.topLeft, casa.size)
            val techo = Path().apply {
                moveTo(casa.left, casa.top); lineTo(casa.left + 3 * u, casa.top - 5 * u); lineTo(casa.right - 3 * u, casa.top - 5 * u); lineTo(casa.right, casa.top); close()
            }
            drawPath(techo, if (noche) ORILLA_NOCHE else c(TEJADO))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(casa.right - 1 * u, casa.top), Size(1 * u, casa.height))
            var wy = casa.top + 3 * u; var fj = 0
            while (wy < horizonte - 2 * u) {
                var wx = casa.left + 3 * u; var cj = 0
                while (wx < casa.right - 4 * u) {
                    val encendida = noche && azar(bk * 37 + fj * 7 + cj, 44) > 0.7f
                    drawRect(if (encendida) VENTANA_NOCHE.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.18f), Offset(wx, wy), Size(2 * u, 3 * u))
                    wx += 5 * u; cj++
                }
                wy += 6 * u; fj++
            }
            bx += bw + 1 * u; bk++
        }
        drawRect(if (noche) ORILLA_NOCHE else c(PIEDRA_JUNTA), Offset(r.left, agua - 4 * u), Size(r.width, 4 * u))
        // El río, con sus rizos.
        drawRect(if (noche) RIO_NOCHE else c(RIO), Offset(r.left, agua), Size(r.width, r.bottom - agua))
        var ry = agua + 3 * u; var rf = 0
        while (ry < r.bottom) {
            var rx = r.left + (if (rf % 2 == 0) 4f else 12f) * u
            while (rx < r.right) {
                drawLine(Color.White.copy(alpha = if (noche) 0.06f else 0.18f), Offset(rx, ry), Offset(rx + 5 * u, ry), 0.7f * u)
                rx += 16 * u
            }
            ry += 4 * u; rf++
        }
        // El farol de la calle en su poste de fundición; de noche, encendido y reflejado en el río.
        val lx = r.center.x
        val cabeza = Rect(lx - 7 * u, r.top + r.height * 0.1f, lx + 7 * u, r.top + r.height * 0.1f + 16 * u)
        if (noche) {
            mancha(cabeza.center, r.height * 0.5f, FAROL.copy(alpha = 0.45f))
            for (j in 0 until 7) {
                val ancho0 = (4f - (j % 3)) * u
                drawLine(FAROL.copy(alpha = 0.5f - j * 0.05f), Offset(lx - ancho0, agua + 4 * u + j * 5 * u), Offset(lx + ancho0, agua + 4 * u + j * 5 * u), 1.2f * u)
            }
        }
        drawLine(c(HIERRO), Offset(lx, r.bottom), Offset(lx, cabeza.bottom + 2 * u), 3 * u)
        drawRect(c(HIERRO), Offset(lx - 3 * u, cabeza.bottom + 2 * u), Size(6 * u, 4 * u))
        drawRect(c(HIERRO), Offset(lx - 4 * u, agua + 10 * u), Size(8 * u, 3 * u))
        drawArc(c(HIERRO), 180f, 90f, false, Offset(lx - 8 * u, cabeza.bottom + 4 * u), Size(8 * u, 8 * u), style = Stroke(1f * u))
        drawArc(c(HIERRO), 270f, 90f, false, Offset(lx, cabeza.bottom + 4 * u), Size(8 * u, 8 * u), style = Stroke(1f * u))
        val vidrio = Path().apply {
            moveTo(cabeza.left, cabeza.top + 3 * u); lineTo(cabeza.right, cabeza.top + 3 * u)
            lineTo(cabeza.right - 3 * u, cabeza.bottom); lineTo(cabeza.left + 3 * u, cabeza.bottom); close()
        }
        drawPath(vidrio, if (noche) FAROL else c(VIDRIO).copy(alpha = 0.85f))
        drawPath(vidrio, c(HIERRO), style = Stroke(1.2f * u))
        drawLine(c(HIERRO), Offset(lx, cabeza.top + 3 * u), Offset(lx, cabeza.bottom), 0.8f * u)
        val capuchon = Path().apply { moveTo(cabeza.left - 2 * u, cabeza.top + 3 * u); lineTo(lx, cabeza.top - 5 * u); lineTo(cabeza.right + 2 * u, cabeza.top + 3 * u); close() }
        drawPath(capuchon, c(HIERRO))
        drawCircle(c(HIERRO), 1.6f * u, Offset(lx, cabeza.top - 6 * u))
        // Las dos tapas abiertas, con sus carteles pegados por dentro y su tirante.
        val bisagra = r.bottom - r.height * 0.05f
        val arriba = r.top + r.height * 0.5f
        listOf(0.06f to 0.44f, 0.56f to 0.94f).forEachIndexed { k, (fa, fb) ->
            val x0 = r.left + r.width * fa; val x1 = r.left + r.width * fb
            val tapa = Path().apply {
                moveTo(x0, bisagra); lineTo(x0 + r.width * 0.02f, arriba); lineTo(x1 - r.width * 0.02f, arriba); lineTo(x1, bisagra); close()
            }
            drawPath(tapa, c(VERDE))
            drawPath(tapa, c(VERDE_OSC), style = Stroke(3 * u))
            drawRect(c(VERDE_LUZ), Offset(x0 + r.width * 0.02f, arriba - 2 * u), Size(x1 - x0 - r.width * 0.04f, 2.5f * u))
            carteles(Rect(x0 + r.width * 0.04f, arriba + 5 * u, x1 - r.width * 0.04f, bisagra - 5 * u), k, oscuro, noche)
            val tx = if (k == 0) x1 - 4 * u else x0 + 4 * u
            val tz = if (k == 0) x1 - r.width * 0.035f else x0 + r.width * 0.035f
            drawLine(c(HIERRO), Offset(tx, bisagra), Offset(tz, arriba + (bisagra - arriba) * 0.3f), 1.2f * u)
        }
        // El canto de la caja con sus bisagras.
        drawRect(c(VERDE), Offset(r.left, bisagra), Size(r.width, r.bottom - bisagra))
        drawRect(c(HIERRO), Offset(r.left, bisagra), Size(r.width, 1.6f * u))
        listOf(0.12f, 0.38f, 0.62f, 0.88f).forEach { f -> drawRect(c(HIERRO_LUZ), Offset(r.left + r.width * f - 4 * u, bisagra - 1 * u), Size(8 * u, 3.6f * u)) }
    }

    /** Los carteles pegados en una tapa: bandas tricolores, la escarapela y renglones (formas, no letras). */
    private fun DrawScope.carteles(z: Rect, k: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val zonas = listOf(
            Rect(z.left, z.top, z.left + z.width * 0.3f, z.top + z.height * 0.82f),
            Rect(z.left + z.width * 0.35f, z.top + z.height * 0.08f, z.left + z.width * 0.66f, z.bottom),
            Rect(z.left + z.width * 0.71f, z.top, z.right, z.top + z.height * 0.7f),
        )
        zonas.forEachIndexed { j, cr ->
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(cr.left + 1 * u, cr.top + 1 * u), cr.size)
            drawRect(c(PAPEL), cr.topLeft, cr.size)
            val tipo = (j + k) % 3
            if (tipo == 0) {
                // Bandas tricolores.
                val tercio = cr.width / 3
                drawRect(c(AZUL), Offset(cr.left, cr.top), Size(tercio, cr.height))
                drawRect(c(BLANCO), Offset(cr.left + tercio, cr.top), Size(tercio, cr.height))
                drawRect(c(ROJO), Offset(cr.left + tercio * 2, cr.top), Size(tercio, cr.height))
            } else if (tipo == 1) {
                // La escarapela y unos renglones debajo.
                val ce = Offset(cr.center.x, cr.top + cr.height * 0.32f)
                val re = minOf(cr.width, cr.height) * 0.26f
                drawCircle(c(ROJO), re, ce); drawCircle(c(BLANCO), re * 0.66f, ce); drawCircle(c(AZUL), re * 0.33f, ce)
                for (l in 0 until 3) {
                    val ly = cr.top + cr.height * (0.66f + 0.1f * l)
                    drawLine(c(TINTA).copy(alpha = 0.5f), Offset(cr.left + cr.width * 0.15f, ly), Offset(cr.right - cr.width * (0.15f + 0.1f * l), ly), 0.8f * u)
                }
            } else {
                // Un pasquín: una barra gruesa arriba y renglones.
                drawRect(c(TINTA).copy(alpha = 0.8f), Offset(cr.left + cr.width * 0.12f, cr.top + cr.height * 0.1f), Size(cr.width * 0.76f, cr.height * 0.12f))
                drawLine(c(ROJO), Offset(cr.left + cr.width * 0.12f, cr.top + cr.height * 0.27f), Offset(cr.right - cr.width * 0.12f, cr.top + cr.height * 0.27f), 0.8f * u)
                var ly = cr.top + cr.height * 0.36f
                while (ly < cr.bottom - cr.height * 0.08f) {
                    drawLine(c(TINTA).copy(alpha = 0.45f), Offset(cr.left + cr.width * 0.12f, ly), Offset(cr.right - cr.width * 0.12f, ly), 0.7f * u)
                    ly += 3 * u
                }
            }
            drawRect(c(PAPEL_VIEJO).copy(alpha = 0.35f), Offset(cr.left, cr.bottom - cr.height * 0.12f), Size(cr.width, cr.height * 0.12f))
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val tinte = CUBIERTAS[(v.semilla and 0x7fffffff) % CUBIERTAS.size]
        val tapa = c(if (v.tono != null) mezcla(tinte, v.tono, 0.22f) else tinte)
        val folleto = azar(v.semilla, 3) > 0.5f
        drawRoundRect(tapa, Offset.Zero, size, CornerRadius((if (folleto) 0.6f else 1.6f) * u))
        if (folleto) {
            // El folleto cosido: las puntadas a la izquierda y el doblez claro a la derecha.
            var py = h * 0.06f
            while (py < h * 0.94f) { drawLine(c(PAPEL).copy(alpha = 0.55f), Offset(w * 0.1f, py), Offset(w * 0.1f, py + 2 * u), 0.6f * u); py += 4 * u }
            drawRect(Color.White.copy(alpha = 0.12f), Offset(w * 0.9f, 0f), Size(w * 0.1f, h))
        } else {
            // El libro de lance: luz, sombra y las bandas del lomo con su filete.
            drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.06f, 0f), Size(w * 0.12f, h))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.86f, 0f), Size(w * 0.14f, h))
            listOf(0.06f, 0.94f).forEach { f ->
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, h * f - 1.4f * u), Size(w, 2.8f * u))
                drawLine(Color(0xFFC9A24E).copy(alpha = 0.6f), Offset(0f, h * f - 2 * u), Offset(w, h * f - 2 * u), 0.5f * u)
            }
        }
        // Las hojas que asoman por arriba: más cuanto más escribiste.
        val hojas = 1 + (v.lleno * 3).toInt()
        for (j in 0 until hojas) {
            val y = (0.8f + j * 1.3f) * u
            drawLine(c(PAPEL).copy(alpha = 0.7f), Offset(w * 0.14f, y), Offset(w * 0.86f, y), 0.5f * u)
        }
        // La escarapela tricolor con sus cintas.
        val ce = Offset(w * 0.5f, h * 0.17f)
        val re = minOf(w * 0.3f, h * 0.07f)
        drawLine(c(ROJO), Offset(ce.x - re * 0.3f, ce.y), Offset(ce.x - re * 0.6f, ce.y + re * 1.8f), re * 0.4f)
        drawLine(c(AZUL), Offset(ce.x + re * 0.3f, ce.y), Offset(ce.x + re * 0.6f, ce.y + re * 1.8f), re * 0.4f)
        drawCircle(c(ROJO), re, ce)
        drawCircle(c(BLANCO), re * 0.66f, ce)
        drawCircle(c(AZUL), re * 0.33f, ce)
        for (j in 0 until 8) {
            val a = Math.toRadians(j * 45.0)
            drawLine(Color.Black.copy(alpha = 0.15f), Offset(ce.x + kotlin.math.cos(a).toFloat() * re * 0.4f, ce.y + kotlin.math.sin(a).toFloat() * re * 0.4f), Offset(ce.x + kotlin.math.cos(a).toFloat() * re, ce.y + kotlin.math.sin(a).toFloat() * re), 0.5f * u)
        }
        // La etiqueta de papel con el día.
        val etiqueta = Rect(w * 0.12f, h * 0.32f, w * 0.88f, h * 0.47f)
        drawRect(c(PAPEL), etiqueta.topLeft, etiqueta.size)
        drawRect(c(TINTA), etiqueta.topLeft, etiqueta.size, style = Stroke(0.6f * u))
        val filete = etiqueta.deflate(1.2f * u)
        drawRect(c(TINTA).copy(alpha = 0.5f), filete.topLeft, filete.size, style = Stroke(0.4f * u))
        textoEn(medidor, v.dia, etiqueta.deflate(1.6f * u), letra, c(TINTA), 14f, peso = FontWeight.Bold)
        // El mes en una tira de papel, a lo largo.
        val tira = Rect(w * 0.24f, h * 0.51f, w * 0.76f, h * 0.84f)
        drawRect(c(PAPEL_VIEJO), tira.topLeft, tira.size)
        drawRect(c(TINTA).copy(alpha = 0.4f), tira.topLeft, tira.size, style = Stroke(0.4f * u))
        textoEn(medidor, v.mes, tira.deflate(0.8f * u), letra, c(TINTA), 8.5f, vertical = true, minimo = 4f)
        // El sello del librero abajo; de noche, una luna.
        val cs = Offset(w * 0.5f, h * 0.91f)
        val rs = minOf(w * 0.2f, 4 * u)
        if (v.noche) {
            drawCircle(LUNA, rs, cs)
            drawCircle(tapa, rs * 0.8f, Offset(cs.x + rs * 0.4f, cs.y - rs * 0.2f))
        } else {
            drawCircle(c(TINTA).copy(alpha = 0.55f), rs, cs, style = Stroke(0.8f * u))
            drawCircle(c(TINTA).copy(alpha = 0.55f), rs * 0.4f, cs)
        }
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 186.dp
    override val cajaTapa = Rect(0.3f, 0.29f, 0.87f, 0.65f)
    override val letraTapa: FontFamily = LetraExtra.convencion
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = if (noche) mezcla(TINTA, NOCHE, 0.15f) else TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** El papel de la etiqueta y de las hojas: se hunde menos que la cubierta, para que el extracto se lea siempre. */
    private fun papel(x: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = x
        if (oscuro) r = mezcla(r, Color(0xFF0B0D0C), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.24f)
        return r
    }

    /** La escarapela tricolor de radio [r] en [o], con sus pliegues. */
    private fun DrawScope.escarapela(o: Offset, r: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.25f), r, Offset(o.x + 1.2f * u, o.y + 1.6f * u))
        drawCircle(t(ROJO, oscuro, noche), r, o)
        drawCircle(t(BLANCO, oscuro, noche), r * 0.66f, o)
        drawCircle(t(AZUL, oscuro, noche), r * 0.34f, o)
        for (j in 0 until 16) {
            val g = Math.toRadians(j * 22.5)
            val cs = kotlin.math.cos(g).toFloat(); val sn = kotlin.math.sin(g).toFloat()
            drawLine(Color.Black.copy(alpha = 0.15f), Offset(o.x + cs * r * 0.36f, o.y + sn * r * 0.36f), Offset(o.x + cs * r, o.y + sn * r), 0.6f * u)
        }
        drawCircle(t(HIERRO_LUZ, oscuro, noche), r * 0.1f, o)
    }

    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val n = e.noche
        fun c(x: Color) = t(x, oscuro, n)
        val a = e.abierto.coerceIn(0f, 1f)
        val k = (e.semilla and 0x7fffffff) % CUBIERTAS.size
        val cubierta = c(CUBIERTAS[k])
        val folleto = azar(e.semilla, 3) > 0.5f
        val hoja = papel(PAPEL, oscuro, n)
        val oro = Color(0xFFC9A24E)
        val libro = Rect(w * 0.025f, h * 0.045f, w * 0.965f, h * 0.9f)
        val lomo = Rect(libro.left, libro.top, libro.left + minOf(w * (if (folleto) 0.035f else 0.065f), 26 * u), libro.bottom)
        val tapa = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(libro.left + 3 * u, libro.top + 5 * u), libro.size, CornerRadius(2 * u))
        // El canto de las hojas, de papel de trapo, desparejo.
        val bloque = Rect(tapa.left, tapa.top + 3 * u, tapa.right + 2.5f * u, tapa.bottom + 3 * u)
        drawRect(c(PAPEL_VIEJO), bloque.topLeft, bloque.size)
        var cy = bloque.top + 2 * u; var j = 0
        while (cy < bloque.bottom - 1 * u) { drawLine(c(TINTA).copy(alpha = 0.15f), Offset(tapa.right + (j % 2) * u, cy), Offset(bloque.right, cy), 0.5f * u); cy += 1.6f * u; j++ }
        // La portada del folleto, que se ve al abrir: el título en renglones gruesos, el filete, la viñeta de la
        // escarapela y el pie de imprenta.
        if (a > 0.01f) {
            val pag = Rect(tapa.left + 1 * u, tapa.top + 3 * u, tapa.right - 1 * u, tapa.bottom - 1 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            listOf(10f to 0.14f, 5f to 0.12f).forEach { (an, al) -> drawRect(Color.Black.copy(alpha = al), pag.topLeft, Size(an * u, pag.height)) }
            val cx = pag.center.x + 4 * u
            listOf(0.12f to 0.5f, 0.2f to 0.8f, 0.28f to 0.65f).forEachIndexed { q, (f, an) ->
                drawLine(c(TINTA).copy(alpha = 0.75f), Offset(cx - pag.width * an * 0.35f, pag.top + pag.height * f), Offset(cx + pag.width * an * 0.35f, pag.top + pag.height * f), (if (q == 1) 3.4f else 2f) * u)
            }
            drawLine(c(TINTA).copy(alpha = 0.6f), Offset(cx - pag.width * 0.3f, pag.top + pag.height * 0.36f), Offset(cx + pag.width * 0.3f, pag.top + pag.height * 0.36f), 0.6f * u)
            drawLine(c(TINTA).copy(alpha = 0.6f), Offset(cx - pag.width * 0.3f, pag.top + pag.height * 0.375f), Offset(cx + pag.width * 0.3f, pag.top + pag.height * 0.375f), 1.2f * u)
            escarapela(Offset(cx, pag.top + pag.height * 0.53f), pag.height * 0.09f, oscuro, n)
            listOf(0.7f to 0.55f, 0.76f to 0.45f, 0.82f to 0.5f, 0.9f to 0.3f).forEach { (f, an) ->
                drawLine(c(TINTA).copy(alpha = 0.5f), Offset(cx - pag.width * an * 0.35f, pag.top + pag.height * f), Offset(cx + pag.width * an * 0.35f, pag.top + pag.height * f), 1.2f * u)
            }
        }
        // La cubierta de papel (o de becerro, en el libro de lance), que gira sobre el lomo al abrirse.
        val sx = 1f - 0.9f * a
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(cubierta, Color.White, 0.12f), cubierta, mezcla(cubierta, Color.Black, 0.3f)), tapa.top, tapa.bottom),
                tapa.topLeft, tapa.size, CornerRadius((if (folleto) 1f else 2.5f) * u))
            clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) {
                if (k == 3) {
                    // El jaspeado: vetas de color corridas por el peine.
                    for (q in 0 until 26) {
                        val o = Offset(tapa.left + tapa.width * azar(e.semilla, 100 + q), tapa.top + tapa.height * azar(e.semilla, 140 + q))
                        drawOval((if (q % 3 == 0) Color.Black else Color.White).copy(alpha = 0.1f), Offset(o.x - 12 * u, o.y - 3 * u), Size(24 * u, 6 * u), style = Stroke(1.2f * u))
                    }
                } else {
                    for (q in 0 until 90) {
                        val o = Offset(tapa.left + tapa.width * azar(e.semilla, 100 + q), tapa.top + tapa.height * azar(e.semilla, 300 + q))
                        drawCircle(Color.Black.copy(alpha = 0.08f), 0.7f * u, o)
                    }
                }
                // Las esquinas gastadas, con el cartón a la vista.
                listOf(tapa.topRight, tapa.bottomRight).forEach { o ->
                    drawCircle(c(PAPEL_VIEJO).copy(alpha = 0.5f), 6 * u, o)
                }
            }
            if (folleto) {
                var py = tapa.top + 6 * u
                while (py < tapa.bottom - 6 * u) { drawLine(c(PAPEL).copy(alpha = 0.5f), Offset(tapa.left + 3 * u, py), Offset(tapa.left + 3 * u, py + 3 * u), 0.8f * u); py += 7 * u }
            }
            // La cinta tricolor que baja por la tapa y la escarapela clavada encima.
            val xc = tapa.left + w * 0.05f
            val an = w * 0.018f
            listOf(AZUL, BLANCO, ROJO).forEachIndexed { q, col -> drawRect(c(col), Offset(xc + q * an, tapa.top), Size(an, tapa.height)) }
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(xc + an * 3, tapa.top), Size(1 * u, tapa.height))
            val ce = Offset(xc + an * 1.5f, h * 0.25f)
            escarapela(ce, minOf(h * 0.1f, w * 0.06f), oscuro, n)
            listOf(-1f to ROJO, 1f to AZUL).forEach { (s, col) ->
                drawPath(Path().apply {
                    moveTo(ce.x + s * 2 * u, ce.y + 6 * u); lineTo(ce.x + s * 8 * u, ce.y + h * 0.2f); lineTo(ce.x + s * 4 * u, ce.y + h * 0.18f)
                    lineTo(ce.x + s * 1 * u, ce.y + h * 0.21f); close()
                }, c(col))
            }
            // La etiqueta de papel con la fecha, lo escrito y la cuenta.
            val etiqueta = Rect(w * 0.27f, h * 0.14f, w * 0.9f, h * 0.785f)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(etiqueta.left + 1.5f * u, etiqueta.top + 2 * u), etiqueta.size)
            drawRect(hoja, etiqueta.topLeft, etiqueta.size)
            drawRect(c(TINTA), etiqueta.topLeft, etiqueta.size, style = Stroke(0.9f * u))
            val filete = etiqueta.deflate(2.4f * u)
            drawRect(c(TINTA).copy(alpha = 0.5f), filete.topLeft, filete.size, style = Stroke(0.5f * u))
            textoEn(medidor, e.fecha, Rect(filete.left + 6 * u, filete.top + 1 * u, filete.right - 6 * u, h * 0.255f), letra, c(TINTA), 13f, peso = FontWeight.Bold, minimo = 7f)
            listOf(AZUL, BLANCO, ROJO).forEachIndexed { q, col ->
                val x0 = filete.left + 14 * u + q * (filete.width - 28 * u) / 3
                drawRect(c(col), Offset(x0, h * 0.262f), Size((filete.width - 28 * u) / 3, 1.6f * u))
            }
            if (!e.vacio && e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(filete.left + 6 * u, h * 0.665f, filete.right - 22 * u, filete.bottom - 1 * u), letra, c(TINTA).copy(alpha = 0.8f), 10.5f, minimo = 6f)
            }
            // El sello del librero, entintado sobre la esquina; de noche, una luna.
            val cs = Offset(etiqueta.right - 12 * u, etiqueta.bottom - 6 * u)
            val rs = 9 * u
            if (n) {
                drawCircle(LUNA, rs * 0.8f, cs)
                drawCircle(hoja, rs * 0.66f, Offset(cs.x + rs * 0.34f, cs.y - rs * 0.18f))
            } else {
                drawCircle(c(TINTA).copy(alpha = 0.5f), rs, cs, style = Stroke(1 * u))
                drawCircle(c(TINTA).copy(alpha = 0.45f), rs * 0.72f, cs, style = Stroke(0.6f * u))
                for (q in 0 until 10) {
                    val g = Math.toRadians(q * 36.0)
                    drawCircle(c(TINTA).copy(alpha = 0.45f), 0.7f * u, Offset(cs.x + kotlin.math.cos(g).toFloat() * rs * 0.86f, cs.y + kotlin.math.sin(g).toFloat() * rs * 0.86f))
                }
                drawCircle(c(TINTA).copy(alpha = 0.5f), rs * 0.3f, cs)
            }
            // La divisa, impresa abajo.
            textoEn(medidor, "LIBERTÉ · ÉGALITÉ · FRATERNITÉ", Rect(w * 0.27f, etiqueta.bottom + 4 * u, w * 0.9f, tapa.bottom - 4 * u), letra,
                mezcla(c(PAPEL), PAPEL, 0.3f).copy(alpha = 0.85f), 9f, espaciado = 1f, minimo = 5f)
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.4f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (q in 0 until 3) drawRect(Color.Black.copy(alpha = 0.12f * (3 - q) / 3f), Offset(borde, tapa.top + 3 * u), Size((4 + 4 * q) * u, tapa.height - 4 * u))
        }
        // El lomo: el folleto, apenas un doblez; el libro de lance, con sus bandas y su filete.
        drawRect(mezcla(cubierta, Color.Black, 0.2f), lomo.topLeft, lomo.size)
        drawRect(Color.White.copy(alpha = 0.08f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.2f, lomo.height))
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(lomo.right - 1.5f * u, lomo.top), Size(1.5f * u, lomo.height))
        if (!folleto) {
            listOf(0.08f, 0.92f).forEach { f ->
                val y = lomo.top + lomo.height * f
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(lomo.left, y - 2 * u), Size(lomo.width - 1.5f * u, 4 * u))
                drawLine(mezcla(c(oro), oro, 0.3f).copy(alpha = 0.7f), Offset(lomo.left, y - 2.6f * u), Offset(lomo.right - 1.5f * u, y - 2.6f * u), 0.6f * u)
            }
            val tejuelo = Rect(lomo.left + 2 * u, lomo.top + lomo.height * 0.3f, lomo.right - 4 * u, lomo.top + lomo.height * 0.7f)
            drawRect(c(PAPEL_VIEJO), tejuelo.topLeft, tejuelo.size)
            drawRect(c(TINTA).copy(alpha = 0.4f), tejuelo.topLeft, tejuelo.size, style = Stroke(0.5f * u))
            for (q in 0 until 3) drawLine(c(TINTA).copy(alpha = 0.5f), Offset(tejuelo.center.x + (q - 1) * 3 * u, tejuelo.top + 6 * u), Offset(tejuelo.center.x + (q - 1) * 3 * u, tejuelo.bottom - 6 * u), 1 * u)
        }
        // De noche, el farol de la calle alumbra desde un lado.
        if (n) clipRect(0f, 0f, w, h) { mancha(Offset(w * 1.02f, h * 0.1f), h * 1.2f, FAROL.copy(alpha = 0.22f)) }
    }
}
