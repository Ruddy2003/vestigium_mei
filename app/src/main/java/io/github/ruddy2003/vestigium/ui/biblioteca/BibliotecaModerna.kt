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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.estrella
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 112.dp

    /** Olas seigaiha en [r]: filas de arcos concéntricos (tres por ola), cada fila corrida media ola. */
    private fun DrawScope.seigaiha(r: Rect, radio: Float, color: Color, grosor: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var fila = 0
            var y = r.top + radio
            while (y < r.bottom + radio) {
                var x = r.left + if (fila % 2 == 0) 0f else radio
                while (x < r.right + radio) {
                    for (k in 1..3) {
                        val rr = radio * k / 3f
                        drawArc(color, 180f, 180f, false, Offset(x - rr, y - rr), Size(rr * 2, rr * 2), style = Stroke(grosor))
                    }
                    x += radio * 2
                }
                y += radio; fila++
            }
        }
    }

    /** La cola de pez (gyobi) de los libros impresos en madera, de [a] de medio ancho, colgando de [y0] en [cx]. */
    private fun colaDePez(cx: Float, y0: Float, a: Float): Path = Path().apply {
        moveTo(cx - a, y0); lineTo(cx + a, y0); lineTo(cx + a, y0 + a * 0.9f); lineTo(cx, y0 + a * 0.45f); lineTo(cx - a, y0 + a * 0.9f); close()
    }

    /**
     * La cabecera de un libro impreso en madera: el marco doble (kyōkaku), grueso afuera y fino adentro, con la cola de pez
     * (gyobi) arriba al centro; a la izquierda, el sello bermellón con 足 («pie»: el viaje a pie; de noche, la ventana
     * redonda de shōji con la luna detrás del papel y su celosía); a su derecha, el rótulo del cuaderno de viaje en añil, la
     * fecha (si es larga, en dos renglones) y el detalle; abajo a la derecha, unas olas seigaiha.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val marco = Rect(2f * u, 4f * u, w - 2f * u, h - 3f * u)
        drawRect(p.tinta.copy(alpha = 0.75f), marco.topLeft, marco.size, style = Stroke(2.2f * u))
        val dentro = marco.deflate(3.5f * u)
        drawRect(p.tinta.copy(alpha = 0.5f), dentro.topLeft, dentro.size, style = Stroke(0.8f * u))
        drawPath(colaDePez(w / 2, dentro.top, 8f * u), p.tinta.copy(alpha = 0.55f))
        seigaiha(Rect(w * 0.62f, h - 18f * u, dentro.right - 1.5f * u, dentro.bottom - 1f * u), 4f * u, (if (e.noche) p.detalle else c(NAMI)).copy(alpha = 0.4f), 0.7f * u)
        val lado = 40f * u
        val s = Rect(14f * u, h / 2 - lado / 2, 14f * u + lado, h / 2 + lado / 2)
        if (!e.noche) {
            drawRoundRect(Color.Black.copy(alpha = 0.18f), s.topLeft + Offset(1f * u, 1.4f * u), s.size, CornerRadius(3f * u))
            drawRoundRect(c(BERMELLON), s.topLeft, s.size, CornerRadius(3f * u))
            val i = s.deflate(3f * u)
            drawRoundRect(c(ZOGE).copy(alpha = 0.85f), i.topLeft, i.size, CornerRadius(2f * u), style = Stroke(1f * u))
            textoEn(medidor, "足", s.deflate(5f * u), LetraExtra.shippori, c(ZOGE), 22f, peso = FontWeight.Bold, minimo = 8f)
        } else {
            val o = s.center; val r = lado * 0.55f
            drawCircle(c(URUSHI), r + 2.5f * u, o)
            drawCircle(mezcla(SHOJI, NOCHE, 0.55f), r, o)
            drawCircle(mezcla(LUNA, SHOJI, 0.3f).copy(alpha = 0.9f), r * 0.48f, Offset(o.x + r * 0.15f, o.y - r * 0.12f))
            clipPath(Path().apply { addOval(Rect(o, r)) }) {
                for (k in -2..2) {
                    drawLine(c(URUSHI).copy(alpha = 0.8f), Offset(o.x + k * r * 0.4f, o.y - r), Offset(o.x + k * r * 0.4f, o.y + r), 1f * u)
                    drawLine(c(URUSHI).copy(alpha = 0.8f), Offset(o.x - r, o.y + k * r * 0.4f), Offset(o.x + r, o.y + k * r * 0.4f), 1f * u)
                }
            }
        }
        val x0 = s.right + 10f * u; val x1 = dentro.right - 6f * u
        textoEn(medidor, e.rotulo, Rect(x0, 15f * u, x1, 32f * u), LetraExtra.shippori, if (e.noche) rotuloModerna(p, true) else p.detalle, 14f, minimo = 8f)
        fechaModerna(medidor, e.fecha, Rect(x0, 33f * u, x1, 73f * u), LetraExtra.zenMincho, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(x0, 74f * u, x1, 91f * u), LetraExtra.zenMincho, p.tenue, 11f, minimo = 7f)
    }

    /**
     * La hoja de un libro impreso en madera: el marco doble (kyōkaku) con la cola de pez arriba al centro, la columna del
     * hashira a la derecha con su propia cola de pez y su recuadro, bandas de bruma (kasumi) de oro arriba a la derecha y
     * olas seigaiha en añil en la esquina del pie. En modo oscuro, el andon (la lámpara de papel) abajo a la izquierda: la
     * hoja se hunde en la penumbra y la luz del papel la tiñe de ámbar.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = 10f * u
        drawRect(p.tinta.copy(alpha = 0.22f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(2.4f * u))
        drawRect(p.tinta.copy(alpha = 0.14f), Offset(m + 4f * u, m + 4f * u), Size(w - 2 * m - 8f * u, h - 2 * m - 8f * u), style = Stroke(1f * u))
        drawPath(colaDePez(w / 2, m + 4f * u, 9f * u), p.tinta.copy(alpha = 0.3f))
        val xh = w - m - 16f * u
        drawLine(p.tinta.copy(alpha = 0.14f), Offset(xh, m + 4f * u), Offset(xh, h - m - 4f * u), 0.8f * u)
        drawPath(colaDePez(w - m - 10f * u, m + 30f * u, 4.5f * u), p.tinta.copy(alpha = 0.22f))
        drawRect(p.tinta.copy(alpha = 0.14f), Offset(w - m - 14f * u, m + 60f * u), Size(8f * u, 50f * u), style = Stroke(0.8f * u))
        val oro = (if (noche) p.acento else KINPAKU).copy(alpha = 0.12f)
        drawRoundRect(oro, Offset(w * 0.52f, 26f * u), Size(w * 0.3f, 7f * u), CornerRadius(3.5f * u))
        drawRoundRect(oro, Offset(w * 0.64f, 37f * u), Size(w * 0.18f, 6f * u), CornerRadius(3f * u))
        seigaiha(Rect(m + 5f * u, h - m - 34f * u, m + 95f * u, h - m - 5f * u), 6f * u, (if (noche) p.detalle else NAMI).copy(alpha = 0.14f), 0.8f * u)
        if (oscuro && !p.oscura) {
            val ambar = Color(0xFFF2B65A)
            val luz = Offset(19f * u, h - 121f * u)
            penumbraModerna(luz, ambar)
            haloModerna(luz, 36f * u, ambar, 0.55f)
            val laca = t(URUSHI, true, false)
            listOf(12.5f * u, 25.5f * u).forEach { drawLine(laca, Offset(it, h - 104f * u), Offset(it, h - 96f * u), 1.4f * u) }
            drawRect(mezcla(SHOJI, Color(0xFFF6C46A), 0.35f), Offset(12f * u, h - 138f * u), Size(14f * u, 34f * u))
            drawRect(laca, Offset(12f * u, h - 138f * u), Size(14f * u, 34f * u), style = Stroke(1.2f * u))
            listOf(h - 126.7f * u, h - 115.3f * u).forEach { drawLine(laca.copy(alpha = 0.7f), Offset(12f * u, it), Offset(26f * u, it), 0.7f * u) }
            drawLine(laca.copy(alpha = 0.7f), Offset(19f * u, h - 138f * u), Offset(19f * u, h - 104f * u), 0.7f * u)
            drawRect(laca, Offset(10.5f * u, h - 141f * u), Size(17f * u, 3f * u))
        }
    }

    /**
     * La barra: el lomo del cuaderno cosido (fukuro-toji), una franja de papel algo más oscuro con los cuatro agujeros, el hilo
     * añil que los une y da la vuelta al canto, y la esquinera de tela.
     */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(p.color, KIRI, 0.3f).let { if (oscuro) mezcla(it, Color.Black, 0.24f) else it }
        drawRect(base)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, h))
        drawLine((if (p.oscura) p.tenue else p.tinta).copy(alpha = 0.25f), Offset(0f, h - 1f * u), Offset(w, h - 1f * u), 1f * u)
        val hilo = if (p.oscura) p.detalle.copy(alpha = 0.8f) else t(NAMI, oscuro, false)
        val y = h - 8f * u
        val xs = listOf(0.06f, 0.35f, 0.65f, 0.94f).map { w * it }
        drawLine(hilo, Offset(xs.first(), y), Offset(xs.last(), y), 1.4f * u)
        xs.forEach { x ->
            drawLine(hilo, Offset(x, y), Offset(x, h), 1.4f * u)
            drawCircle(Color.Black.copy(alpha = 0.35f), 1.3f * u, Offset(x, y))
        }
        drawRect(t(TELAS[0], oscuro && !p.oscura, noche), Offset(0f, h - 12f * u), Size(9f * u, 12f * u))
    }

    /** El botón de poner el sello: el hanko de bermellón, con su filete de marfil y las faltas donde la pasta no tomó. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        val rojo = t(BERMELLON, oscuro && !p.oscura, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1f * u, 1.6f * u), r.size, CornerRadius(4f * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(rojo, Color.White, 0.1f), mezcla(rojo, Color.Black, 0.16f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(4f * u))
        for (k in 0 until 6) {
            val f = azar(17, k)
            val o = if (k % 2 == 0) Offset(r.left + f * r.width, if (k % 4 == 0) r.top + 1f * u else r.bottom - 1f * u) else Offset(if (k % 4 == 1) r.left + 1f * u else r.right - 1f * u, r.top + f * r.height)
            drawCircle(mezcla(rojo, ZOGE, 0.4f).copy(alpha = 0.5f), (0.6f + azar(17, k + 7)) * u, o)
        }
        val i = r.deflate(3f * u)
        drawRoundRect(ZOGE.copy(alpha = 0.8f), i.topLeft, i.size, CornerRadius(2.5f * u), style = Stroke(1f * u))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = ZOGE

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 252.dp

    /** El tatami, su ribete de tela, el papel encendido del andon y el bronce del gotero. */
    private val TATAMI = Color(0xFFC6BC86)
    private val HERI = Color(0xFF2A2A26)
    private val ANDON = Color(0xFFF6D488)
    private val BRONCE = Color(0xFF7A6A44)

    /** El grosor del cuaderno cosido, en dp: de 3 con una hoja a 17 con 240 o más. */
    private fun grosorCuaderno(hojas: Int): Float = 3f + 14f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /** Un cuadrilátero visto en perspectiva: el borde de delante de [x0] a [x1] a la altura [y1], el de atrás metido [p] y a la altura [y0]. */
    private fun plano(x0: Float, x1: Float, y0: Float, y1: Float, p: Float): Path = Path().apply {
        moveTo(x0 + p, y0); lineTo(x1 - p, y0); lineTo(x1, y1); lineTo(x0, y1); close()
    }

    /**
     * El cuaderno cosido de momentos sobre el bundai (el escritorio bajo de laca negra), en una sala de tatami: detrás, el
     * shōji con su cuadrícula de listones bajo la viga; a la izquierda, el andon (el farol de papel en su bastidor) y, sobre
     * el tatami, la caja de escritura abierta con la piedra de tinta (suzuri), la barra, el pincel y la tapa apoyada. El
     * bundai tiene una hierba de otoño en oro (maki-e) en el canto y las patas de voluta. El cuaderno es índigo con hojas de
     * cáñamo, cosido a cuatro agujeros en el lomo de la derecha, con las esquinas de seda; la tira del título lleva el nombre
     * y una tira más chica la cuenta. Engorda con las hojas dobladas y le asoman más papelitos de marca. Al abrirse, la tapa
     * gira sobre el lomo de la derecha (como se leen) y deja ver las columnas y el sello bermellón. En oscuro, el andon
     * encendido alumbra la sala.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0B0A0C), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorCuaderno(e.hojas) * u
        val hoja = washi(oscuro, false)
        val oro = c(KINPAKU)
        // El shōji: papel tras la cuadrícula de listones, bajo la viga (nageshi).
        val suelo = h * 0.42f
        drawRect(if (oscuro) Color(0xFF2A2C38) else SHOJI, Offset(0f, 0f), Size(w, suelo))
        var kx = 0f
        while (kx < w) { drawLine(c(KIRI_SOMBRA), Offset(kx, 0f), Offset(kx, suelo), 1.4f * u); kx += 22 * u }
        var ky = h * 0.1f
        while (ky < suelo) { drawLine(c(KIRI_SOMBRA), Offset(0f, ky), Offset(w, ky), 1.2f * u); ky += 15 * u }
        var px = w * 0.25f
        while (px < w) { drawRect(c(KIRI_SOMBRA), Offset(px - 2.5f * u, 0f), Size(5 * u, suelo)); px += w * 0.25f }
        if (!oscuro) {
            // La sombra de un bambú tras el papel.
            drawLine(BAMBU.copy(alpha = 0.18f), Offset(w * 0.7f, 0f), Offset(w * 0.72f, suelo), 4 * u)
            listOf(0.15f, 0.3f, 0.42f).forEach { f -> drawPath(Path().apply { moveTo(w * 0.71f, h * f); quadraticTo(w * 0.78f, h * f - 6 * u, w * 0.84f, h * f + 2 * u); quadraticTo(w * 0.78f, h * f, w * 0.71f, h * f); close() }, BAMBU.copy(alpha = 0.16f)) }
        }
        drawRect(c(KIRI), Offset(0f, 0f), Size(w, h * 0.07f))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, h * 0.07f - 2 * u), Size(w, 2 * u))
        drawRect(c(URUSHI), Offset(0f, suelo - 4 * u), Size(w, 4 * u))
        // El tatami con su ribete, en perspectiva.
        drawRect(c(TATAMI), Offset(0f, suelo), Size(w, h - suelo))
        var tx = -w
        while (tx < w * 2) { drawLine(c(mezcla(TATAMI, Color.Black, 0.12f)), Offset(w / 2 + (tx - w / 2) * 0.55f, suelo), Offset(tx, h), 0.6f * u); tx += 5 * u }
        drawLine(c(HERI), Offset(w * 0.5f, suelo), Offset(w * 0.5f, h), 5 * u)
        drawRect(c(HERI), Offset(0f, h * 0.9f), Size(w, 4 * u))

        // ------------------------------------------------ el andon, a la izquierda
        val an = Rect(w * 0.025f, h * 0.2f, w * 0.14f, h * 0.62f)
        drawRect(if (oscuro) ANDON else c(SHOJI), an.topLeft, an.size)
        listOf(an.left, an.right).forEach { x -> drawRect(c(URUSHI), Offset(x - 1.5f * u, an.top - 6 * u), Size(3 * u, h * 0.92f - an.top + 6 * u)) }
        listOf(an.top, an.top + an.height * 0.5f, an.bottom).forEach { y -> drawRect(c(URUSHI), Offset(an.left - 1.5f * u, y - 1.5f * u), Size(an.width + 3 * u, 3 * u)) }
        drawRect(c(URUSHI), Offset(an.left - 3 * u, h * 0.85f), Size(an.width + 6 * u, 4 * u))
        val luz = an.center
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to ANDON.copy(alpha = 0.34f), 0.35f to ANDON.copy(alpha = 0.13f), 1f to Color.Transparent,
                center = luz, radius = w * 0.95f))
            haloModerna(luz, an.height * 0.7f, ANDON, 0.5f)
            drawRect(Color.White.copy(alpha = 0.35f), Offset(an.left + an.width * 0.3f, an.top + 3 * u), Size(an.width * 0.4f, an.height - 6 * u))
        }

        // ------------------------------------------------ el bundai
        val m0 = h * 0.37f; val m1 = h * 0.64f
        val tablero = plano(w * 0.1f, w * 0.97f, m0, m1, w * 0.06f)
        drawPath(tablero, c(URUSHI))
        drawPath(plano(w * 0.1f + 4 * u, w * 0.97f - 4 * u, m0 + 3 * u, m1 - 2 * u, w * 0.06f), Color.White.copy(alpha = 0.05f))
        drawPath(tablero, c(URUSHI_LUZ), style = Stroke(1 * u))
        val canto = Rect(w * 0.1f, m1, w * 0.97f, m1 + 9 * u)
        drawRect(c(URUSHI), canto.topLeft, canto.size)
        drawRect(c(URUSHI_LUZ), canto.topLeft, Size(canto.width, 1.2f * u))
        // La hierba de otoño en oro, a lo largo del canto.
        var hx = canto.left + 6 * u; var hk = 0
        while (hx < canto.right - 6 * u) {
            drawLine(oro, Offset(hx, canto.bottom - 1 * u), Offset(hx + (if (hk % 2 == 0) 4 else -3) * u, canto.top + 2 * u), 0.8f * u)
            if (hk % 3 == 0) drawCircle(oro, 1 * u, Offset(hx + 4 * u, canto.top + 2.5f * u))
            hx += 7 * u; hk++
        }
        // Las patas de voluta, en las puntas.
        listOf(canto.left + 6 * u, canto.right - 22 * u).forEach { x ->
            drawPath(Path().apply {
                moveTo(x, canto.bottom); lineTo(x + 16 * u, canto.bottom); lineTo(x + 14 * u, h * 0.86f)
                quadraticTo(x + 22 * u, h * 0.88f, x + 20 * u, h * 0.9f); lineTo(x - 4 * u, h * 0.9f)
                quadraticTo(x - 6 * u, h * 0.88f, x + 2 * u, h * 0.86f); close()
            }, c(URUSHI))
            drawCircle(oro, 2 * u, Offset(x + 8 * u, h * 0.78f), style = Stroke(0.8f * u))
        }
        drawOval(Color.Black.copy(alpha = 0.22f), Offset(w * 0.08f, h * 0.89f), Size(w * 0.9f, 5 * u))
        // El gotero de bronce, atrás a la derecha del tablero.
        val got = Offset(w * 0.89f, m0 + 16 * u)
        drawOval(c(BRONCE), Offset(got.x - 8 * u, got.y - 6 * u), Size(16 * u, 9 * u))
        drawRect(c(BRONCE), Offset(got.x - 2 * u, got.y - 9 * u), Size(4 * u, 4 * u))
        drawOval(Color.White.copy(alpha = 0.25f), Offset(got.x - 5 * u, got.y - 4 * u), Size(4 * u, 2 * u))

        // ------------------------------------------------ la caja de escritura, abierta sobre el tatami
        val caja = plano(w * 0.74f, w * 0.98f, h * 0.84f, h * 0.97f, 4 * u)
        drawPath(caja, c(URUSHI))
        drawPath(plano(w * 0.755f, w * 0.965f, h * 0.855f, h * 0.955f, 4 * u), c(mezcla(URUSHI, NAMI, 0.3f)))
        drawPath(plano(w * 0.77f, w * 0.87f, h * 0.865f, h * 0.945f, 3 * u), c(SUMI))
        drawOval(c(Color(0xFF0E0D10)), Offset(w * 0.79f, h * 0.875f), Size(w * 0.05f, 4 * u))
        drawRect(c(SUMI), Offset(w * 0.89f, h * 0.87f), Size(4 * u, 14 * u))
        drawRect(oro, Offset(w * 0.89f, h * 0.88f), Size(4 * u, 3 * u))
        drawLine(c(BAMBU), Offset(w * 0.91f, h * 0.87f), Offset(w * 0.96f, h * 0.95f), 2 * u)
        drawLine(c(SUMI), Offset(w * 0.96f, h * 0.95f), Offset(w * 0.97f, h * 0.97f), 2.6f * u, cap = StrokeCap.Round)

        // ------------------------------------------------ el cuaderno cosido
        val yb = h * 0.615f
        val L = w * 0.2f; val R = w * 0.8f
        val prof = h * 0.29f
        val p = (R - L) * 0.06f
        val yt = yb - g
        val tapa = plano(L, R, yt - prof, yt, p)
        drawPath(plano(L + 3 * u, R + 4 * u, yt - prof + 4 * u, yb + 4 * u, p), Color.Black.copy(alpha = 0.32f))
        // El canto de delante: las hojas dobladas (fukuro), con su doblez.
        drawRect(c(TELAS[0]), Offset(L, yb - 2 * u), Size(R - L, 2 * u))
        drawRect(hoja, Offset(L + 1 * u, yt), Size(R - L - 2 * u, g - 2 * u))
        var py = yt + 1.4f * u
        while (py < yb - 2 * u) { drawLine(Color.Black.copy(alpha = 0.16f), Offset(L + 1 * u, py), Offset(R - 1 * u, py), 0.5f * u); py += 1.5f * u }
        // Los papelitos de marca que asoman por delante: uno más cada 30 hojas.
        val marcas = (e.hojas / 30).coerceIn(0, 6)
        for (j in 0 until marcas) {
            val mx = L + (R - L) * (0.1f + 0.13f * j + 0.04f * azar(e.semilla, j))
            drawRect(c(if (j % 2 == 0) BERMELLON else ZOGE), Offset(mx, yt + g * 0.3f), Size(6 * u, g * 0.7f + (5 + 4 * azar(e.semilla, 10 + j)) * u))
        }
        // La hoja de adentro: columnas de derecha a izquierda, la cola de pez y el sello.
        if (a > 0.01f) {
            val pag = plano(L + 2 * u, R - 2 * u, yt - prof + 2 * u, yt - 1 * u, p)
            drawPath(pag, hoja)
            val marco = plano(L + 14 * u, R - 14 * u, yt - prof + 10 * u, yt - 10 * u, p)
            drawPath(marco, c(SUMI).copy(alpha = 0.7f), style = Stroke(1.2f * u))
            var cx = R - p - 22 * u; var k = 0
            while (cx > L + p + 20 * u) {
                var cy = yt - prof + 16 * u; var j = 0
                while (cy < yt - 16 * u) {
                    val l = (3 + 4 * azar(e.semilla + k, j)) * u
                    drawLine(c(SUMI).copy(alpha = 0.6f), Offset(cx, cy), Offset(cx, minOf(cy + l, yt - 16 * u)), 1.4f * u)
                    cy += l + 2.6f * u; j++
                }
                cx -= 9 * u; k++
            }
            drawRect(c(BERMELLON), Offset(L + p + 20 * u, yt - 30 * u), Size(11 * u, 11 * u))
        }
        // La tapa índigo con las hojas de cáñamo: gira sobre el lomo de la derecha.
        val sx = 1f - 0.94f * a
        val tela = c(TELAS[0])
        scale(sx, 1f, Offset(R, yt)) {
            drawPath(tapa, tela)
            clipPath(tapa) { asanoha(Rect(L, yt - prof, R, yt), 16 * u, Color.White.copy(alpha = if (oscuro) 0.08f else 0.12f)) }
            drawPath(tapa, Color.Black.copy(alpha = 0.3f), style = Stroke(1 * u))
            // Las esquinas de seda del lomo (kadogire).
            listOf(yt - prof to R - p, yt to R).forEach { (yy, xx) ->
                drawRect(c(Color(0xFF8A6A3A)), Offset(xx - 10 * u, yy - (if (yy < yt) 0f else 7 * u)), Size(10 * u, 7 * u))
            }
            // La costura de cuatro agujeros, junto al lomo de la derecha.
            val hilo = c(Color(0xFFEDE6D6))
            val pts = listOf(0.1f, 0.37f, 0.63f, 0.9f).map { f ->
                val yy = yt - prof + prof * f
                val xd = R - p * (1 - f)
                Offset(xd - 12 * u, yy) to Offset(xd, yy)
            }
            drawLine(hilo, pts.first().first, pts.last().first, 1.3f * u)
            pts.forEach { (dentro, borde) -> drawLine(hilo, dentro, borde, 1.3f * u); drawCircle(mezcla(tela, Color.Black, 0.5f), 1.5f * u, dentro) }
            // La tira del título (daisen) con el nombre, y la tira chica con la cuenta.
            val et = Rect(L + p + (R - L) * 0.05f, yt - prof * 0.9f, L + (R - L) * 0.7f, yt - prof * 0.5f)
            drawRect(Color.Black.copy(alpha = 0.3f), et.topLeft + Offset(1.4f * u, 1.8f * u), et.size)
            drawRect(hoja, et.topLeft, et.size)
            drawRect(c(BERMELLON), et.deflate(2.5f * u).topLeft, et.deflate(2.5f * u).size, style = Stroke(0.8f * u))
            textoEn(medidor, e.nombre, et.deflate(5 * u), letraTapa, c(SUMI), 14f, minimo = 7f)
            val tira = Rect(L + p + (R - L) * 0.05f, yt - prof * 0.42f, L + (R - L) * 0.78f, yt - prof * 0.18f)
            drawRect(hoja, tira.topLeft, tira.size)
            drawRect(oro, tira.topLeft, tira.size, style = Stroke(0.8f * u))
            textoEn(medidor, e.cuenta, tira.deflate(3 * u), letra, c(SUMI).copy(alpha = 0.85f), 10f, minimo = 6f)
            if (a > 0f) drawPath(tapa, Color.Black.copy(alpha = 0.45f * a))
        }
        drawLine(mezcla(tela, Color.Black, 0.3f), Offset(R - p, yt - prof), Offset(R, yb), 3 * u)
    }

    /**
     * La pestaña de un día: un tanzaku (la tira de papel de los poemas) de washi con su banda de tela arriba (con la letra
     * de la semana), el día en la letra del tema y, abajo, un sello redondo del color del ánimo (sin ánimo, solo su cerco).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val tela = c(TELAS[(e.semilla and 0x7fffffff) % TELAS.size])
        val tira = Rect(6 * u, 2 * u, w - 6 * u, h - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), tira.topLeft + Offset(1.2f * u, 1.6f * u), tira.size)
        drawRect(washi(oscuro, false), tira.topLeft, tira.size)
        // Las motas de oro del papel.
        for (j in 0 until 6) drawCircle(c(KINPAKU).copy(alpha = 0.6f), 0.7f * u, Offset(tira.left + tira.width * azar(e.semilla, 20 + j), tira.top + tira.height * azar(e.semilla, 30 + j)))
        val cab = Rect(tira.left, tira.top, tira.right, tira.top + 12 * u)
        drawRect(tela, cab.topLeft, cab.size)
        textoEn(medidor, e.semana, cab, letra, c(ZOGE), 9f, peso = FontWeight.Bold)
        textoEn(medidor, e.dia, Rect(tira.left, cab.bottom + 1 * u, tira.right, tira.bottom - 14 * u), letraTapa, c(SUMI), 18f, minimo = 10f)
        val o = Offset(w / 2, h - 10 * u)
        if (e.tono != null) {
            drawCircle(e.tono, 5.5f * u, o)
            drawCircle(washi(oscuro, false).copy(alpha = 0.7f), 3.8f * u, o, style = Stroke(0.8f * u))
        } else {
            drawCircle(c(BERMELLON).copy(alpha = 0.5f), 4.5f * u, o, style = Stroke(0.8f * u))
        }
        drawRect(c(KIRI_SOMBRA), tira.topLeft, tira.size, style = Stroke(0.6f * u))
    }

    /**
     * El rótulo de un mes: una tira de washi en un marco de laca negra, con olas seigaiha índigo en las dos puntas, un
     * filete de oro y el mes en la letra del tema.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val marco = Rect(2 * u, 2 * u, w - 2 * u, h - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), marco.topLeft + Offset(1.2f * u, 1.6f * u), marco.size)
        drawRect(c(URUSHI), marco.topLeft, marco.size)
        val papel = marco.deflate(3 * u)
        drawRect(washi(oscuro, false), papel.topLeft, papel.size)
        listOf(Rect(papel.left, papel.top, papel.left + 46 * u, papel.bottom), Rect(papel.right - 46 * u, papel.top, papel.right, papel.bottom)).forEach { r ->
            drawRect(c(NAMI), r.topLeft, r.size)
            seigaiha(r, 6 * u, c(FUSUMA).copy(alpha = 0.7f), 0.7f * u)
        }
        drawLine(c(KINPAKU), Offset(papel.left + 46 * u, papel.top + 2 * u), Offset(papel.right - 46 * u, papel.top + 2 * u), 0.8f * u)
        drawLine(c(KINPAKU), Offset(papel.left + 46 * u, papel.bottom - 2 * u), Offset(papel.right - 46 * u, papel.bottom - 2 * u), 0.8f * u)
        textoEn(medidor, texto, Rect(papel.left + 50 * u, papel.top + 3 * u, papel.right - 50 * u, papel.bottom - 3 * u), letraTapa, c(SUMI), 14f, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El rojo de la laca de dentro de la caja, el cordón de seda y el muro de arena del tokonoma. */
    private val LACA_ROJA = Color(0xFF9E2B22)
    private val CORDON_SEDA = Color(0xFFB8322A)
    private val JURAKU = Color(0xFFCDB894)

    /** El cierre 〆 a pincel, de alto [s], centrado en [o]. */
    private fun DrawScope.cierrePincel(o: Offset, s: Float, color: Color) {
        val g = Stroke(s * 0.13f, cap = StrokeCap.Round)
        drawPath(Path().apply { moveTo(o.x - s * 0.4f, o.y - s * 0.38f); quadraticTo(o.x, o.y - s * 0.05f, o.x + s * 0.36f, o.y + s * 0.42f) }, color, style = g)
        drawPath(Path().apply { moveTo(o.x + s * 0.34f, o.y - s * 0.42f); quadraticTo(o.x - s * 0.05f, o.y - s * 0.02f, o.x - s * 0.34f, o.y + s * 0.2f); lineTo(o.x - s * 0.18f, o.y + s * 0.42f) }, color, style = g)
    }

    /** El sellito rojo redondo del remitente, en [o] y de radio [r], con su marco y un trazo en reserva. */
    private fun DrawScope.sellito(o: Offset, r: Float, oscuro: Boolean) {
        val rojo = if (oscuro) mezcla(BERMELLON, Color.Black, 0.2f) else BERMELLON
        drawCircle(rojo, r, o)
        drawCircle(ZOGE, r * 0.74f, o, style = Stroke(r * 0.12f))
        drawLine(ZOGE, Offset(o.x, o.y - r * 0.42f), Offset(o.x, o.y + r * 0.42f), r * 0.14f)
        drawLine(ZOGE, Offset(o.x - r * 0.3f, o.y - r * 0.1f), Offset(o.x + r * 0.3f, o.y - r * 0.1f), r * 0.12f)
    }

    /** Un lazo de cordón de seda (dos orejas y dos colas con su borla) en [o], de tamaño [s]. */
    private fun DrawScope.lazoSeda(o: Offset, s: Float, color: Color, cae: Float) {
        val g = s * 0.14f
        listOf(-1f, 1f).forEach { l ->
            drawOval(color, Offset(o.x + l * s * 0.55f - s * 0.42f, o.y - s * 0.3f), Size(s * 0.84f, s * 0.6f), style = Stroke(g))
            val fin = o + Offset(l * s * 0.35f, s * 1.3f * cae)
            drawLine(color, o, fin, g, StrokeCap.Round)
            drawPath(Path().apply { moveTo(fin.x - g, fin.y); lineTo(fin.x + g, fin.y); lineTo(fin.x + g * 2f, fin.y + s * 0.5f * cae); lineTo(fin.x - g * 2f, fin.y + s * 0.5f * cae); close() }, color)
        }
        drawCircle(color, g * 1.6f, o)
    }

    /**
     * La carta va en un fubako, la caja de cartas de laca negra con olas seigaiha y una rama de ciruelo de oro, cerrada con
     * el fūjime: la tira de papel pegada a caballo entre la tapa y la caja con el 〆 a pincel y el sellito rojo (en Japón no
     * se lacraba: se cerraba así, y abrir la tira la rompe). Las selladas al futuro van además atadas con el cordón de seda
     * roja con su lazo y sus borlas. A quién va y las fechas, en una tira de washi pegada al frente, a tinta sumi. Al
     * abrirse la tira se rasga por la junta, la tapa se levanta y vuelca hacia atrás enseñando su laca roja, y sale la carta
     * plegada. En oscuro la laca se hunde casi a negro, el oro apenas brilla y el washi se lee.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF070606), 0.55f) else x
        val p = Rect(c.left + W * 0.05f, c.top + H * 0.08f, c.right - W * 0.05f, c.bottom - H * 0.06f)
        val cx = p.center.x
        val tapa = p.height * 0.32f
        val junta = p.top + tapa
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val partido = e.romper.coerceIn(0f, 1f)
        val tira = Rect(p.left + p.width * 0.72f, p.top + tapa * 0.18f, p.left + p.width * 0.72f + maxOf(9 * u, p.width * 0.075f), junta + p.height * 0.1f)
        val sello = Offset(tira.center.x, junta + p.height * 0.06f)
        val rSello = tira.width * 0.42f
        // La cara de la tapa: [g] es su giro (1 cerrada, de frente; negativo, volcada hacia atrás enseñando la laca roja).
        fun caraTapa(g: Float) = Rect(p.left - 2 * u, minOf(p.top, p.top + g * tapa), p.right + 2 * u, maxOf(p.top, p.top + g * tapa))

        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.55f else 0.25f), Offset(p.left + 2 * u, p.top + 3 * u), p.size, CornerRadius(3 * u))
        if (giro < 0f) {
            // La tapa volcada: su laca roja por dentro con el canto negro.
            val r = caraTapa(giro)
            drawRoundRect(k(URUSHI), r.topLeft, r.size, CornerRadius(3 * u))
            drawRoundRect(k(LACA_ROJA), r.topLeft + Offset(3 * u, 2.5f * u), Size(r.width - 6 * u, r.height - 3.5f * u), CornerRadius(2 * u))
        }
        // El borde interior de la caja, que asoma cuando la tapa no está.
        drawRect(k(URUSHI_LUZ), Offset(p.left + 3 * u, p.top + 2 * u), Size(p.width - 6 * u, tapa))
        val sale = salidaModerna(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaModerna(Rect(cx - p.width * 0.42f, p.top + 3 * u - sube, cx + p.width * 0.42f, p.bottom - 6 * u - sube), e, oscuro)
        }
        // La caja: laca negra con su canto de luz, las olas de oro abajo y la rama de ciruelo.
        val caja = Rect(p.left, p.top + 4 * u, p.right, p.bottom)
        drawRect(k(URUSHI), Offset(caja.left, junta), Size(caja.width, caja.bottom - junta))
        drawRect(k(URUSHI), Offset(caja.left + 3 * u, caja.top), Size(caja.width - 6 * u, junta - caja.top))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.08f), Offset(caja.left, junta), Size(caja.width, 2 * u))
        seigaiha(Rect(caja.left, caja.bottom - p.height * 0.2f, caja.right, caja.bottom), p.height * 0.07f, k(KINPAKU).copy(alpha = 0.65f), 0.8f * u)
        val rama = Path().apply {
            moveTo(caja.left + p.width * 0.03f, junta + p.height * 0.1f)
            quadraticTo(caja.left + p.width * 0.1f, junta + p.height * 0.05f, caja.left + p.width * 0.16f, junta + p.height * 0.14f)
        }
        drawPath(rama, k(KINPAKU), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        for (j in 0 until 3) {
            val o = Offset(caja.left + p.width * (0.05f + 0.05f * j), junta + p.height * (0.08f + 0.025f * (j % 2)))
            for (q in 0 until 5) drawCircle(k(KINPAKU), 1.3f * u, polarModerna(o, 1.6f * u, q * 72f - 90f))
            drawCircle(k(BERMELLON), 0.8f * u, o)
        }
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(caja.left, caja.bottom - 2 * u), Size(caja.width, 2 * u))
        // La tira de washi con lo escrito.
        val et = Rect(p.left + p.width * 0.08f, junta + p.height * 0.16f, p.right - p.width * 0.08f, caja.bottom - p.height * 0.08f)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            drawRect(Color.Black.copy(alpha = 0.3f), et.topLeft + Offset(1.2f * u, 1.6f * u), et.size)
            drawRect(if (oscuro) mezcla(SHOJI, Color(0xFF070606), 0.22f) else SHOJI, et.topLeft, et.size)
            drawRect(k(KINPAKU), et.topLeft + Offset(2 * u, 2 * u), Size(et.width - 4 * u, et.height - 4 * u), style = Stroke(0.6f * u))
            for (j in 0 until 8) drawCircle(KINPAKU.copy(alpha = 0.35f), (0.8f + azar(e.semilla, 600 + j)) * u, Offset(et.left + et.width * azar(e.semilla, 610 + j), et.top + et.height * azar(e.semilla, 620 + j)))
            val m = et.top + et.height * 0.56f
            textoEn(medidor, e.linea1, Rect(et.left + 5 * u, et.top + 2 * u, et.right - 5 * u, m), letra, SUMI, 12f, peso = FontWeight.SemiBold, minimo = 8f)
            textoEn(medidor, e.linea2, Rect(et.left + 5 * u, m - 1 * u, et.right - 5 * u, et.bottom - 2 * u), letra, mezcla(BERMELLON, Color.Black, 0.25f), 9f, minimo = 7f)
        }
        // La tapa cerrada (o levantándose), de frente.
        if (giro >= 0f) {
            val r = caraTapa(giro)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 2 * u))
            drawRoundRect(k(URUSHI), r.topLeft, r.size, CornerRadius(3 * u))
            if (r.height > 4 * u) {
                clipRect(r.left, r.top, r.right, r.bottom) {
                    scale(1f, giro, Offset(cx, p.top)) {
                        // Una nube de oro y una ola en la tapa.
                        seigaiha(Rect(p.right - p.width * 0.32f, p.top + tapa * 0.35f, p.right - 2 * u, p.top + tapa), tapa * 0.22f, k(KINPAKU).copy(alpha = 0.7f), 0.8f * u)
                        drawRoundRect(k(KINPAKU).copy(alpha = 0.85f), Offset(p.left + p.width * 0.05f, p.top + tapa * 0.3f), Size(p.width * 0.25f, tapa * 0.34f), CornerRadius(tapa * 0.17f))
                        drawRoundRect(k(KINPAKU).copy(alpha = 0.85f), Offset(p.left + p.width * 0.15f, p.top + tapa * 0.5f), Size(p.width * 0.22f, tapa * 0.28f), CornerRadius(tapa * 0.14f))
                    }
                }
                drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), Offset(r.left + 3 * u, r.top + 1.5f * u), Size(r.width - 6 * u, 1.5f * u))
            }
        }
        // El fūjime: la tira de papel con el 〆 a caballo de la junta; al abrir se rasga y su mitad de arriba se va con la tapa.
        val marca = Offset(tira.center.x, junta - tapa * 0.12f)
        val tamMarca = minOf(tira.width * 1.15f, tapa * 0.75f)
        conAlfaModerna(e.alfaSello.coerceIn(0f, 1f), Rect(sello, tira.height * 2f)) {
            val rasgon = Path().apply {
                moveTo(tira.left, junta)
                for (j in 0..4) lineTo(tira.left + tira.width * j / 4f, junta + (if (j % 2 == 0) -1f else 1.4f) * u * partido)
                lineTo(tira.right, junta)
            }
            val abajo = Path().apply { addPath(rasgon); lineTo(tira.right, tira.bottom); lineTo(tira.left, tira.bottom); close() }
            val arriba = Path().apply { addPath(rasgon); lineTo(tira.right, tira.top); lineTo(tira.left, tira.top); close() }
            clipPath(abajo) {
                drawRect(k(ZOGE), tira.topLeft, tira.size)
                cierrePincel(marca, tamMarca, k(SUMI))
            }
            if (giro >= 0f) scale(1f, giro, Offset(cx, p.top)) {
                translate(0f, -partido * 2.5f * u) {
                    clipPath(arriba) {
                        drawRect(k(ZOGE), tira.topLeft, tira.size)
                        cierrePincel(marca, tamMarca, k(SUMI))
                    }
                }
            }
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(tira.right - 1 * u, junta), Size(1 * u, tira.bottom - junta))
        }
        // El sellito rojo, que brilla en la que ya se puede abrir y cae grande al sellar.
        conAlfaModerna(e.alfaSello.coerceIn(0f, 1f), Rect(sello, rSello * 4f * e.escalaSello)) {
            scale(e.escalaSello, sello) {
                if (e.brillo > 0f) {
                    drawCircle(Brush.radialGradient(0f to KINPAKU.copy(alpha = 0.85f * e.brillo), 0.45f to BERMELLON.copy(alpha = 0.25f * e.brillo),
                        1f to Color.Transparent, center = sello, radius = rSello * 5f), rSello * 5f, sello)
                    rayosModerna(sello, rSello * 1.8f, rSello * 3.8f, KINPAKU.copy(alpha = 0.95f * e.brillo), 12, 1.1f)
                }
                sellito(sello, rSello, oscuro)
            }
        }
        // El cordón de seda roja, atado en lazo en el frente de la tapa.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f && giro > 0.9f) {
            val nudo = Offset(p.left + p.width * 0.4f, p.top + tapa * 0.5f)
            val f = minOf(1f, ata / 0.7f)
            val a = Offset(nudo.x - (nudo.x - p.left + 2 * u) * f, nudo.y); val b = Offset(nudo.x + (p.right + 2 * u - nudo.x) * f, nudo.y)
            drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(0f, 1.4f * u), b + Offset(0f, 1.4f * u), 0.8f * u)
            drawLine(k(CORDON_SEDA), a, b, 1.8f * u)
            if (ata > 0.7f) lazoSeda(nudo, tapa * 0.6f, k(CORDON_SEDA), (ata - 0.7f) / 0.3f)
        }
    }

    override val cajaPrevia = Rect(0.165f, 0.08f, 0.835f, 0.25f)

    override val altoBuzon: Dp = 212.dp

    /**
     * El buzón son los chigaidana del tokonoma: contra el muro de arena, dos baldas de laca escalonadas, unidas por su
     * pilarcito, con las cartas encima: las que ya se pueden abrir, en su fubako con el sellito rojo encendido; las selladas,
     * en su fubako atado con el cordón de seda roja; las abiertas, desplegadas, con sus columnas a pincel. Abajo, en un
     * listón, tres ema (las tablillas de madera con tejadillo) colgadas de su cordón, con la cuenta. A la derecha, el pilar
     * del tokonoma y una lámpara de papel, que en oscuro se enciende.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF070606), 0.58f) else x
        // El muro de arena del tokonoma, con su grano.
        drawRect(k(JURAKU))
        for (j in 0 until 120) drawCircle(k(Color(0xFFA8916A)).copy(alpha = 0.4f), (0.5f + azar(j, 71)) * u, Offset(w * azar(j, 72), h * azar(j, 73)))
        // El pilar del tokonoma, a la derecha, y el tatami abajo.
        val pilar = Rect(w * 0.9f, 0f, w * 0.93f, h)
        drawRect(k(TABLON), pilar.topLeft, pilar.size)
        vetas(pilar, k(KIRI_VETA).copy(alpha = 0.5f), 33, vertical = true, densidad = 0.7f, grosor = 0.6f)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(pilar.right, 0f), Size(2 * u, h))
        // La lámpara de papel (andon), al pie del pilar.
        val andon = Rect(w * 0.94f, h * 0.55f, w * 0.995f, h * 0.88f)
        if (oscuro) drawRect(Brush.radialGradient(0f to ANDON.copy(alpha = 0.3f), 0.45f to ANDON.copy(alpha = 0.1f), 1f to Color.Transparent,
            center = andon.center, radius = w * 0.8f))
        drawRect(if (oscuro) Color(0xFFF2D58E) else k(SHOJI), andon.topLeft, andon.size)
        drawRect(k(URUSHI), andon.topLeft, andon.size, style = Stroke(1.6f * u))
        drawLine(k(URUSHI), Offset(andon.left, andon.center.y), Offset(andon.right, andon.center.y), 1 * u)
        if (oscuro) haloModerna(andon.center, andon.width * 1.6f, ANDON, 0.5f)
        val tatami = h * 0.94f
        drawRect(k(TATAMI), Offset(0f, tatami), Size(w, h - tatami))
        drawRect(k(URUSHI), Offset(0f, tatami), Size(w, 2 * u))

        // ------------------------------------------------ las dos baldas escalonadas
        val alta = Rect(w * 0.03f, h * 0.31f, w * 0.56f, h * 0.31f + 6 * u)
        val baja = Rect(w * 0.36f, h * 0.58f, w * 0.88f, h * 0.58f + 6 * u)
        val ebizuka = Rect(w * 0.455f, alta.bottom, w * 0.465f, baja.top)
        // El kakejiku: el rollo colgado con un paisaje a tinta (montes y una barca), entre sus bandas de brocado.
        val rollo = Rect(w * 0.64f, h * 0.02f, w * 0.8f, h * 0.36f)
        drawLine(k(SUMI), Offset(rollo.center.x, 0f), Offset(rollo.center.x, rollo.top), 0.8f * u)
        drawRect(Color.Black.copy(alpha = 0.18f), rollo.topLeft + Offset(2 * u, 2 * u), rollo.size)
        drawRect(k(NAMI), rollo.topLeft, rollo.size)
        val pintura = Rect(rollo.left + 4 * u, rollo.top + rollo.height * 0.16f, rollo.right - 4 * u, rollo.bottom - rollo.height * 0.12f)
        drawRect(if (oscuro) mezcla(SHOJI, Color(0xFF070606), 0.4f) else SHOJI, pintura.topLeft, pintura.size)
        clipRect(pintura.left, pintura.top, pintura.right, pintura.bottom) {
            drawPath(Path().apply { moveTo(pintura.left, pintura.top + pintura.height * 0.6f); lineTo(pintura.left + pintura.width * 0.3f, pintura.top + pintura.height * 0.25f)
                lineTo(pintura.left + pintura.width * 0.55f, pintura.top + pintura.height * 0.55f); lineTo(pintura.left + pintura.width * 0.75f, pintura.top + pintura.height * 0.38f)
                lineTo(pintura.right, pintura.top + pintura.height * 0.62f); lineTo(pintura.right, pintura.bottom); lineTo(pintura.left, pintura.bottom); close() }, k(SUMI).copy(alpha = 0.25f))
            drawCircle(k(BERMELLON).copy(alpha = 0.7f), pintura.width * 0.1f, Offset(pintura.left + pintura.width * 0.72f, pintura.top + pintura.height * 0.18f))
            drawLine(k(SUMI).copy(alpha = 0.6f), Offset(pintura.left + pintura.width * 0.3f, pintura.top + pintura.height * 0.82f), Offset(pintura.left + pintura.width * 0.55f, pintura.top + pintura.height * 0.82f), 1.2f * u)
        }
        drawRoundRect(k(URUSHI), Offset(rollo.left - 2 * u, rollo.bottom - 1 * u), Size(rollo.width + 4 * u, 4 * u), CornerRadius(2 * u))
        drawRect(k(URUSHI), ebizuka.topLeft, ebizuka.size)
        val cartas = repartoModerna(e, 10)
        val enAlta = minOf(cartas.size, 5)
        listOf(alta to cartas.take(enAlta), baja to cartas.drop(enAlta)).forEachIndexed { b, (balda, lista) ->
            // La sombra de la balda en el muro.
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(balda.left + 3 * u, balda.bottom), Size(balda.width, 5 * u))
            val paso = (balda.width - 8 * u) / 5
            lista.forEachIndexed { i, clase ->
                val x = balda.left + 4 * u + paso * (i + 0.5f)
                val ancho = paso * 0.84f
                if (clase == 2) {
                    // Abierta: la carta desplegada, de pie contra el muro, con sus columnas a pincel.
                    val hoja = Rect(x - ancho * 0.45f, balda.top - h * 0.18f, x + ancho * 0.45f, balda.top)
                    rotate((azar(i + b * 5, 81) - 0.5f) * 8f, Offset(x, balda.top)) {
                        drawRect(Color.Black.copy(alpha = 0.2f), hoja.topLeft + Offset(1.5f * u, 0f), hoja.size)
                        drawRect(k(SHOJI), hoja.topLeft, hoja.size)
                        for (j in 0 until 4) drawLine(k(SUMI).copy(alpha = 0.55f), Offset(hoja.right - hoja.width * (0.2f + 0.2f * j), hoja.top + 4 * u), Offset(hoja.right - hoja.width * (0.2f + 0.2f * j), hoja.bottom - (4 + 6 * (j % 2)) * u), 1 * u)
                        drawCircle(k(BERMELLON), 1.6f * u, Offset(hoja.left + hoja.width * 0.18f, hoja.bottom - 5 * u))
                    }
                } else {
                    // En su fubako: la caja de laca negra con su tapa, el sellito y, si está sellada, el cordón.
                    val caja = Rect(x - ancho / 2, balda.top - ancho * 0.72f, x + ancho / 2, balda.top)
                    drawRect(k(URUSHI), caja.topLeft, caja.size)
                    drawRect(k(URUSHI_LUZ), Offset(caja.left, caja.top), Size(caja.width, caja.height * 0.36f))
                    drawLine(Color.Black, Offset(caja.left, caja.top + caja.height * 0.36f), Offset(caja.right, caja.top + caja.height * 0.36f), 0.7f * u)
                    drawLine(k(KINPAKU).copy(alpha = 0.7f), Offset(caja.left + 2 * u, caja.bottom - 3 * u), Offset(caja.right - 2 * u, caja.bottom - 3 * u), 0.7f * u)
                    val o = Offset(caja.left + caja.width * 0.72f, caja.top + caja.height * 0.36f)
                    if (clase == 1) {
                        drawLine(k(CORDON_SEDA), Offset(caja.left, caja.top + caja.height * 0.18f), Offset(caja.right, caja.top + caja.height * 0.18f), 1.4f * u)
                        lazoSeda(Offset(caja.left + caja.width * 0.36f, caja.top + caja.height * 0.18f), caja.height * 0.45f, k(CORDON_SEDA), 0.7f)
                    } else {
                        haloModerna(o, ancho * 0.8f, KINPAKU, if (oscuro) 1f else 0.8f)
                        rayosModerna(o, ancho * 0.22f, ancho * 0.52f, KINPAKU, 8, 0.9f)
                    }
                    drawRect(k(ZOGE), Offset(o.x - 1.5f * u, caja.top + 1 * u), Size(3 * u, caja.height * 0.55f))
                    sellito(o, 2.4f * u, oscuro)
                }
            }
            // La balda: laca negra con su canto de luz.
            drawRect(k(URUSHI), balda.topLeft, balda.size)
            drawRect(k(URUSHI_LUZ), balda.topLeft, Size(balda.width, 1.5f * u))
            // El rollito del borde (fudegaeshi) en la punta de afuera.
            val punta = if (b == 0) Offset(balda.left, balda.center.y) else Offset(balda.right, balda.center.y)
            drawRoundRect(k(URUSHI), Offset(punta.x - 3 * u, balda.top - 3 * u), Size(6 * u, balda.height + 3 * u), CornerRadius(3 * u))
        }
        // El listón con las tres ema.
        val liston = Rect(w * 0.03f, h * 0.66f, w * 0.88f, h * 0.66f + 4 * u)
        drawRect(k(TABLON), liston.topLeft, liston.size)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(liston.left, liston.bottom), Size(liston.width, 2 * u))
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "selladas", e.abiertas to "abiertas")
        rotulos.forEachIndexed { i, (n, nombre) ->
            val xc = liston.left + liston.width * (i * 2 + 1) / 6f
            val r = Rect(xc - liston.width * 0.14f, liston.bottom + 6 * u, xc + liston.width * 0.14f, tatami - 4 * u)
            drawLine(k(CORDON_SEDA), Offset(xc, liston.center.y), Offset(xc, r.top + r.height * 0.18f), 1.2f * u)
            val ema = Path().apply {
                moveTo(r.left, r.top + r.height * 0.28f); lineTo(xc, r.top); lineTo(r.right, r.top + r.height * 0.28f)
                lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
            }
            translate(1.5f * u, 2 * u) { drawPath(ema, Color.Black.copy(alpha = 0.25f)) }
            drawPath(ema, if (oscuro) mezcla(KIRI, Color(0xFF070606), 0.25f) else KIRI)
            clipPath(ema) { vetas(r, k(KIRI_VETA).copy(alpha = 0.35f), 90 + i, densidad = 0.5f, grosor = 0.5f) }
            drawPath(Path().apply { moveTo(r.left, r.top + r.height * 0.28f); lineTo(xc, r.top); lineTo(r.right, r.top + r.height * 0.28f) }, k(URUSHI), style = Stroke(2.4f * u))
            drawPath(ema, k(KIRI_SOMBRA), style = Stroke(0.8f * u))
            drawCircle(k(CORDON_SEDA), 1.6f * u, Offset(xc, r.top + r.height * 0.18f))
            val m = r.top + r.height * 0.7f
            textoEn(medidor, "$n", Rect(r.left, r.top + r.height * 0.26f, r.right, m), letra, if (i == 0 && n > 0) BERMELLON else SUMI, 17f, peso = FontWeight.Bold)
            textoEn(medidor, nombre, Rect(r.left + 3 * u, m - 2 * u, r.right - 3 * u, r.bottom - 2 * u), letra, SUMI, 9.5f, minimo = 7f)
        }
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /** Un estudio de geometría a pluma: el círculo, el cuadrado inscrito, sus diagonales, los ejes y los arcos del compás. */
    private fun DrawScope.estudio(o: Offset, r: Float, color: Color, grosor: Float) {
        drawCircle(color, r, o, style = Stroke(grosor))
        val l = r * 0.707f
        drawRect(color, Offset(o.x - l, o.y - l), Size(2 * l, 2 * l), style = Stroke(grosor))
        drawLine(color, Offset(o.x - l, o.y - l), Offset(o.x + l, o.y + l), grosor * 0.8f)
        drawLine(color, Offset(o.x - l, o.y + l), Offset(o.x + l, o.y - l), grosor * 0.8f)
        drawLine(color, Offset(o.x - r * 1.15f, o.y), Offset(o.x + r * 1.15f, o.y), grosor * 0.7f)
        drawLine(color, Offset(o.x, o.y - r * 1.15f), Offset(o.x, o.y + r * 1.15f), grosor * 0.7f)
        drawArc(color, -40f, 80f, false, Offset(o.x - 2 * r, o.y - r), Size(2 * r, 2 * r), style = Stroke(grosor * 0.7f))
        drawArc(color, 140f, 80f, false, Offset(o.x, o.y - r), Size(2 * r, 2 * r), style = Stroke(grosor * 0.7f))
        drawCircle(color, grosor * 1.4f, o)
    }

    /** Un renglón de escritura en espejo, de [x0] a [x1]: una línea que ondula como letra apretada. */
    private fun DrawScope.espejoPagina(x0: Float, x1: Float, y: Float, color: Color, semilla: Int) {
        val u = density
        val camino = Path().apply {
            moveTo(x1, y); var x = x1; var k = 0
            while (x > x0) {
                val paso = (2.5f + 2.5f * azar(semilla, k)) * u
                quadraticTo(x - paso / 2, y + (if (k % 2 == 0) -2.2f else 1.6f) * u, x - paso, y)
                x -= paso; k++
            }
        }
        drawPath(camino, color, style = Stroke(0.8f * u))
    }

    /**
     * La cabecera de un cuaderno de notas de taller: a la izquierda, un estudio de geometría a sanguina (el círculo, el
     * cuadrado, las diagonales y los arcos del compás; de noche, la luna dibujada a pluma con su sombra rayada y una
     * constelación unida por trazos) y, debajo, un renglón de escritura en espejo; a la derecha, el rótulo en letra cursiva
     * de canciller, la fecha (si es larga, en dos renglones) y el detalle; abajo, una escala graduada a pluma, como en los
     * dibujos medidos.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val sangre = p.acento; val tinta = p.tinta
        val o = Offset(34f * u, h / 2 - 6f * u); val r = 23f * u
        if (!e.noche) {
            estudio(o, r, sangre.copy(alpha = 0.75f), 0.9f * u)
        } else {
            val rl = r * 0.62f
            drawCircle(tinta.copy(alpha = 0.85f), rl, o, style = Stroke(0.9f * u))
            clipPath(Path().apply { addOval(Rect(o, rl)) }) {
                clipRect(o.x - rl, o.y - rl, o.x + rl * 0.15f, o.y + rl) {
                    var x = o.x - rl * 2
                    while (x < o.x + rl) { drawLine(tinta.copy(alpha = 0.55f), Offset(x, o.y + rl), Offset(x + rl, o.y - rl), 0.6f * u); x += 2.4f * u }
                }
            }
            drawArc(tinta.copy(alpha = 0.7f), -90f, 180f, false, Offset(o.x - rl * 0.55f, o.y - rl), Size(rl * 1.4f, rl * 2), style = Stroke(0.7f * u))
            val estrellas = listOf(Offset(0.8f, -0.7f), Offset(1.05f, -0.2f), Offset(0.85f, 0.4f), Offset(-0.75f, 0.75f), Offset(-0.95f, -0.55f))
                .map { Offset(o.x + it.x * r, o.y + it.y * r) }
            for (k in 0 until 2) drawLine(tinta.copy(alpha = 0.4f), estrellas[k], estrellas[k + 1], 0.6f * u)
            drawLine(tinta.copy(alpha = 0.4f), estrellas[3], estrellas[4], 0.6f * u)
            estrellas.forEach { drawPath(estrella(it, 2.2f * u, 4, 0.35f), sangre) }
        }
        listOf(h - 22f * u, h - 16f * u).forEachIndexed { k, y -> espejoPagina(8f * u, o.x + r - 2f * u * k, y, tinta.copy(alpha = 0.35f), e.numero + k) }
        val x0 = o.x + r + 12f * u; val x1 = w - 8f * u
        textoEn(medidor, e.rotulo, Rect(x0, 2f * u, x1, 36f * u), LetraExtra.italianno, rotuloModerna(p, e.noche), 28f, minimo = 12f)
        fechaModerna(medidor, e.fecha, Rect(x0, 37f * u, x1, 77f * u), letra, tinta, 22f)
        textoEn(medidor, e.detalle, Rect(x0, 78f * u, x1, 95f * u), letra, p.tenue, 12f, minimo = 7f)
        val ye = h - 10f * u
        drawLine(tinta.copy(alpha = 0.55f), Offset(x0, ye), Offset(x1, ye), 0.9f * u)
        var x = x0; var k = 0
        while (x <= x1 + 0.5f) {
            drawLine(tinta.copy(alpha = 0.5f), Offset(x, ye), Offset(x, ye - (if (k % 5 == 0) 5f else 2.5f) * u), 0.7f * u)
            if (k % 10 == 0 && x + 30f * u <= x1 + 0.5f) drawRect(tinta.copy(alpha = 0.4f), Offset(x, ye + 1f * u), Size(30f * u, 2f * u))
            x += 6f * u; k++
        }
    }

    /**
     * La hoja del cuaderno de notas: una escala graduada en el margen izquierdo; en la esquina del pie, a la derecha, los
     * trazos de construcción de un estudio (círculos, el cuadrado, diagonales y los arcos del compás) a punta de plomo; en
     * el margen derecho, unos trazos a sanguina; y abajo, unas notas en escritura de espejo, apenas marcadas. En modo oscuro,
     * la vela en su candelero de latón, en el margen derecho: el papel se hunde en la penumbra y la llama lo tiñe de miel.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val tinta = p.tinta; val sangre = p.acento
        drawLine(tinta.copy(alpha = 0.16f), Offset(12f * u, 30f * u), Offset(12f * u, h - 30f * u), 0.8f * u)
        var y = 30f * u; var k = 0
        while (y < h - 30f * u) { drawLine(tinta.copy(alpha = 0.16f), Offset(12f * u, y), Offset(12f * u + (if (k % 5 == 0) 6f else 3f) * u, y), 0.7f * u); y += 8f * u; k++ }
        val o = Offset(w * 0.8f, h * 0.85f)
        estudio(o, w * 0.2f, tinta.copy(alpha = 0.07f), 1f * u)
        drawCircle(tinta.copy(alpha = 0.06f), w * 0.1f, Offset(o.x - w * 0.2f, o.y), style = Stroke(1f * u))
        for (j in 0 until 5) {
            val a = Offset(w - 26f * u + j * 2.5f * u, h * 0.36f + j * 9f * u)
            drawPath(Path().apply { moveTo(a.x, a.y); quadraticTo(a.x - 14f * u, a.y + 16f * u, a.x - 4f * u, a.y + 34f * u) }, sangre.copy(alpha = 0.12f), style = Stroke(1.4f * u))
        }
        for (j in 0 until 4) espejoPagina(34f * u, w * 0.5f - j * 14f * u, h - 112f * u + j * 12f * u, tinta.copy(alpha = 0.08f), 40 + j)
        if (oscuro && !p.oscura) {
            val x = w - 15f * u
            val llama = Offset(x, 150f * u)
            penumbraModerna(llama - Offset(0f, 5f * u), LLAMA)
            haloModerna(llama - Offset(0f, 5f * u), 36f * u, LLAMA, 0.6f)
            val laton = t(LATON, true, false)
            drawOval(laton, Offset(x - 9f * u, llama.y + 50f * u), Size(18f * u, 5f * u))
            drawLine(laton, Offset(x, llama.y + 52f * u), Offset(x, llama.y + 34f * u), 2.4f * u)
            drawCircle(laton, 2.2f * u, Offset(x, llama.y + 42f * u))
            drawOval(laton, Offset(x - 7f * u, llama.y + 32f * u), Size(14f * u, 4f * u))
            drawRect(mezcla(CERA, Color.Black, 0.12f), Offset(x - 3f * u, llama.y + 2f * u), Size(6f * u, 31f * u))
            drawLine(TINTA, Offset(x, llama.y + 2f * u), Offset(x, llama.y - 1.5f * u), 0.8f * u)
            llamaModerna(llama + Offset(0f, 1f * u), 12f * u, LLAMA)
        }
    }

    /** La barra: una regla de latón pálido, con su canto oscuro y las rayas de la graduación al pie. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(LATON, p.color, 0.6f).let { if (oscuro) mezcla(it, Color.Black, 0.22f) else it }
        drawRect(Brush.verticalGradient(listOf(mezcla(base, Color.White, 0.12f), mezcla(base, Color.Black, 0.08f)), 0f, h))
        val raya = if (p.oscura) p.tenue.copy(alpha = 0.6f) else LATON_OSC.copy(alpha = 0.6f)
        drawLine(raya, Offset(0f, h - 0.8f * u), Offset(w, h - 0.8f * u), 1.4f * u)
        var x = 3f * u; var k = 0
        while (x < w) {
            val l = when { k % 10 == 0 -> 9f; k % 5 == 0 -> 6f; else -> 3f } * u
            drawLine(raya, Offset(x, h - 1f * u), Offset(x, h - 1f * u - l), 0.7f * u)
            x += 5f * u; k++
        }
    }

    /** El latón del botón: claro de día, apenas hundido en modo oscuro, en la penumbra de la vela de noche. */
    private fun latonBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color =
        if (noche) mezcla(LATON, NOCHE, 0.3f) else if (oscuro && !p.oscura) mezcla(LATON, Color.Black, 0.15f) else LATON

    /** El botón de anotar: una placa de latón grabada, con su filete y un tornillo en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        val laton = latonBoton(p, oscuro, noche)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1f * u, 1.6f * u), r.size, CornerRadius(3f * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(laton, Color.White, 0.22f), mezcla(laton, Color.Black, 0.14f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(3f * u))
        val i = r.deflate(3f * u)
        drawRoundRect(LATON_OSC.copy(alpha = 0.8f), i.topLeft, i.size, CornerRadius(2f * u), style = Stroke(1f * u))
        drawRoundRect(Color.White.copy(alpha = 0.25f), i.topLeft + Offset(0.8f * u, 0.8f * u), i.size, CornerRadius(2f * u), style = Stroke(0.6f * u))
        listOf(r.left + 7.5f * u, r.right - 7.5f * u).forEach { x ->
            val c0 = Offset(x, r.center.y)
            drawCircle(LATON_OSC, 2.4f * u, c0)
            drawLine(mezcla(laton, Color.White, 0.3f), Offset(x - 1.5f * u, c0.y + 1.5f * u), Offset(x + 1.5f * u, c0.y - 1.5f * u), 0.7f * u)
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = if (noche) CERA else TINTA

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 262.dp

    /** El cartón de las tapas, el papel del taller, el cordón y la sanguina. */
    private val CARTON = Color(0xFFB8956A)
    private val PAPEL_TALLER = Color(0xFFEAD9B0)
    private val CORDON = Color(0xFF4A2A14)
    private val SANGUINA = Color(0xFFA8503A)

    /** El grosor del cuaderno de taller, en dp: de 4 con una hoja a 20 con 240 o más. */
    private fun grosorTaller(hojas: Int): Float = 4f + 16f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /**
     * El cuaderno de taller sobre la mesa de caballetes de la bottega: el muro de yeso con hojas de estudios clavadas (un
     * ala, un estudio de geometría), la mesa de nogal con sus caballetes y, sobre ella, el atril de mesa donde se apoya el
     * cuaderno, la lucerna de latón, el compás de puntas abierto y la maqueta de una cúpula con sus nervios y su linterna.
     * El cuaderno es de cartón con la figura de construcción a sepia (el círculo, el cuadrado y sus diagonales), la etiqueta
     * de papel pegada con el nombre y la cuenta, y el cordón que lo ata en cruz con su nudo. Engorda con las hojas y le
     * asoman cada vez más hojas sueltas por la cabeza y el canto. Al abrirse, el cordón cae, la tapa gira y se ven los
     * estudios de la hoja. En oscuro, la lucerna encendida alumbra el taller.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0C0A08), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorTaller(e.hojas) * u
        val hoja = vitela(PAPEL_TALLER, oscuro, false)
        val sepia = c(SEPIA)
        // El muro de yeso con sus manchas.
        drawRect(c(MURO))
        for (j in 0 until 12) mancha(Offset(w * azar(e.semilla, 10 + j), h * 0.7f * azar(e.semilla, 30 + j)), 16 * u, Color.Black.copy(alpha = 0.035f))
        // Las hojas de estudios clavadas al muro.
        fun lamina(r: Rect, giro: Float, dibujo: DrawScope.(Rect) -> Unit) {
            rotate(giro, r.center) {
                drawRect(Color.Black.copy(alpha = 0.2f), r.topLeft + Offset(1.5f * u, 2 * u), r.size)
                drawRect(vitela(PAPEL_TALLER, oscuro, false).let { if (oscuro) mezcla(it, Color.Black, 0.3f) else it }, r.topLeft, r.size)
                dibujo(r)
                drawCircle(c(LATON), 2 * u, Offset(r.center.x, r.top + 4 * u))
            }
        }
        lamina(Rect(w * 0.03f, h * 0.08f, w * 0.19f, h * 0.36f), -4f) { r ->
            // Un ala de murciélago, a sanguina: el hueso y las varillas.
            val o = Offset(r.left + r.width * 0.15f, r.top + r.height * 0.35f)
            for (k in 0 until 5) {
                val p = Offset(r.left + r.width * (0.5f + 0.1f * k), r.top + r.height * (0.25f + 0.13f * k))
                drawLine(c(SANGUINA), o, p, 0.8f * u)
                drawArc(c(SANGUINA), 0f, 180f, false, Offset(p.x - 4 * u, p.y - 2 * u), Size(8 * u, 6 * u), style = Stroke(0.6f * u))
            }
            espejo(r.left + 4 * u, r.right - 4 * u, r.bottom - 8 * u, sepia.copy(alpha = 0.6f), 3)
        }
        lamina(Rect(w * 0.82f, h * 0.06f, w * 0.97f, h * 0.32f), 5f) { r ->
            estudio(Offset(r.center.x, r.top + r.height * 0.45f), r.width * 0.28f, sepia.copy(alpha = 0.7f), 0.7f * u)
            espejo(r.left + 4 * u, r.right - 4 * u, r.bottom - 6 * u, sepia.copy(alpha = 0.6f), 7)
        }
        // El piso de barro cocido.
        val piso = h * 0.95f
        drawRect(c(COTTO), Offset(0f, h * 0.86f), Size(w, h * 0.14f))
        var lx = 0f
        while (lx < w) { drawLine(Color.Black.copy(alpha = 0.2f), Offset(lx, h * 0.86f), Offset(lx, h), 1 * u); lx += 24 * u }
        drawLine(Color.Black.copy(alpha = 0.2f), Offset(0f, h * 0.93f), Offset(w, h * 0.93f), 1 * u)

        // ------------------------------------------------ la mesa de caballetes
        val mesa = h * 0.74f
        listOf(w * 0.16f, w * 0.84f).forEach { x ->
            drawLine(c(NOGAL_OSC), Offset(x - 2 * u, mesa), Offset(x - 18 * u, piso), 6 * u)
            drawLine(c(NOGAL_OSC), Offset(x + 2 * u, mesa), Offset(x + 18 * u, piso), 6 * u)
            drawLine(c(NOGAL), Offset(x - 12 * u, h * 0.88f), Offset(x + 12 * u, h * 0.88f), 4 * u)
        }
        val tabla = Rect(w * 0.02f, mesa, w * 0.98f, mesa + 11 * u)
        drawRect(c(NOGAL), tabla.topLeft, tabla.size)
        vetas(tabla, c(VETA).copy(alpha = 0.45f), 21, densidad = 1f, grosor = 0.6f)
        drawRect(c(NOGAL_LUZ), tabla.topLeft, Size(tabla.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tabla.left, tabla.bottom - 2 * u), Size(tabla.width, 2 * u))

        // ------------------------------------------------ la lucerna de latón, a la izquierda
        val lu = Offset(w * 0.075f, mesa)
        drawOval(c(LATON), Offset(lu.x - 9 * u, lu.y - 4 * u), Size(18 * u, 5 * u))
        drawRect(c(LATON), Offset(lu.x - 1.6f * u, lu.y - 38 * u), Size(3.2f * u, 36 * u))
        drawOval(c(LATON_OSC), Offset(lu.x - 3 * u, lu.y - 22 * u), Size(6 * u, 4 * u))
        drawArc(c(LATON), 0f, 180f, true, Offset(lu.x - 10 * u, lu.y - 46 * u), Size(20 * u, 12 * u))
        drawPath(Path().apply { moveTo(lu.x + 8 * u, lu.y - 41 * u); lineTo(lu.x + 15 * u, lu.y - 44 * u); lineTo(lu.x + 15 * u, lu.y - 42 * u); lineTo(lu.x + 9 * u, lu.y - 38 * u); close() }, c(LATON))
        drawCircle(c(LATON), 3 * u, Offset(lu.x, lu.y - 49 * u), style = Stroke(1.2f * u))
        val llama = Offset(lu.x + 15 * u, lu.y - 44 * u)
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to LLAMA.copy(alpha = 0.32f), 0.35f to LLAMA.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.9f))
            haloModerna(llama - Offset(0f, 4 * u), 26 * u, LLAMA, 0.7f)
            llamaModerna(llama, 10 * u, LLAMA)
        }
        // El compás de puntas, abierto y de pie.
        val bis = Offset(w * 0.19f, mesa - 44 * u)
        drawLine(c(LATON), bis, Offset(bis.x - 11 * u, mesa), 2.2f * u)
        drawLine(c(LATON), bis, Offset(bis.x + 11 * u, mesa), 2.2f * u)
        drawLine(c(Color(0xFF6E6D73)), Offset(bis.x - 9.5f * u, mesa - 5 * u), Offset(bis.x - 11 * u, mesa), 1.4f * u)
        drawLine(c(Color(0xFF6E6D73)), Offset(bis.x + 9.5f * u, mesa - 5 * u), Offset(bis.x + 11 * u, mesa), 1.4f * u)
        drawCircle(c(LATON), 3.4f * u, bis)
        drawCircle(c(LATON_OSC), 1.4f * u, bis)
        drawLine(c(LATON), bis, bis - Offset(0f, 7 * u), 1.6f * u)
        // La maqueta de la cúpula: el tambor con sus ventanas, los nervios y la linterna.
        val mq = Offset(w * 0.87f, mesa)
        drawRect(c(NOGAL_LUZ), Offset(mq.x - 22 * u, mq.y - 6 * u), Size(44 * u, 6 * u))
        val tambor = Rect(mq.x - 17 * u, mq.y - 22 * u, mq.x + 17 * u, mq.y - 6 * u)
        drawRect(c(PERGAMINO), tambor.topLeft, tambor.size)
        for (k in 0 until 4) drawPath(Path().apply { addOval(Rect(tambor.left + 4 * u + k * 8 * u, tambor.top + 4 * u, tambor.left + 8 * u + k * 8 * u, tambor.top + 11 * u)) }, c(NOGAL_OSC))
        val cupula = Path().apply {
            moveTo(tambor.left, tambor.top)
            cubicTo(tambor.left, tambor.top - 20 * u, mq.x - 5 * u, tambor.top - 28 * u, mq.x - 3 * u, tambor.top - 30 * u)
            lineTo(mq.x + 3 * u, tambor.top - 30 * u)
            cubicTo(mq.x + 5 * u, tambor.top - 28 * u, tambor.right, tambor.top - 20 * u, tambor.right, tambor.top)
            close()
        }
        drawPath(cupula, c(COTTO))
        listOf(-12f, -5f, 5f, 12f).forEach { dx -> drawLine(c(PERGAMINO), Offset(mq.x + dx * u, tambor.top), Offset(mq.x + dx * 0.25f * u, tambor.top - 29 * u), 1.2f * u) }
        drawRect(c(PERGAMINO), Offset(mq.x - 3 * u, tambor.top - 38 * u), Size(6 * u, 8 * u))
        drawCircle(c(ORO), 2 * u, Offset(mq.x, tambor.top - 40 * u))

        // ------------------------------------------------ el atril de mesa y el cuaderno
        val tapa = Rect(w * 0.27f, h * 0.14f + g * 0.5f, w * 0.75f - g, mesa)
        drawPath(Path().apply {
            moveTo(tapa.left - 6 * u, mesa); lineTo(tapa.left - 1 * u, h * 0.08f); lineTo(tapa.right + g + 2 * u, h * 0.08f); lineTo(tapa.right + g + 8 * u, mesa); close()
        }, c(NOGAL_OSC))
        drawLine(c(NOGAL_LUZ), Offset(tapa.left - 1 * u, h * 0.08f), Offset(tapa.right + g + 2 * u, h * 0.08f), 3 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + g + 4 * u, tapa.top - g * 0.5f + 4 * u), tapa.size)
        drawRect(c(mezcla(CARTON, Color.Black, 0.25f)), Offset(tapa.left + g, tapa.top - g * 0.5f), tapa.size)
        // Las hojas sueltas que asoman: más cuantas más hojas.
        val sueltas = (e.hojas / 25).coerceIn(0, 8)
        for (j in 0 until sueltas) {
            val arriba = j % 2 == 0
            if (arriba) {
                val sx0 = tapa.left + tapa.width * (0.1f + 0.75f * azar(e.semilla, 50 + j)) + g * 0.5f
                rotate(-8f + 16f * azar(e.semilla, 60 + j), Offset(sx0, tapa.top)) {
                    drawRect(hoja, Offset(sx0 - 9 * u, tapa.top - g * 0.5f - (6 + 6 * azar(e.semilla, 70 + j)) * u), Size(18 * u, 14 * u))
                    drawLine(sepia.copy(alpha = 0.4f), Offset(sx0 - 6 * u, tapa.top - g * 0.5f - 3 * u), Offset(sx0 + 5 * u, tapa.top - g * 0.5f - 3 * u), 0.6f * u)
                }
            } else {
                val sy = tapa.top + tapa.height * (0.15f + 0.7f * azar(e.semilla, 80 + j))
                rotate(-6f + 12f * azar(e.semilla, 90 + j), Offset(tapa.right + g, sy)) {
                    drawRect(hoja, Offset(tapa.right, sy - 6 * u), Size(g + (6 + 6 * azar(e.semilla, 100 + j)) * u, 12 * u))
                }
            }
        }
        val bloque = Path().apply {
            moveTo(tapa.left + 3 * u, tapa.top); lineTo(tapa.left + 3 * u + g, tapa.top - g * 0.5f + 1.5f * u)
            lineTo(tapa.right + g - 1.5f * u, tapa.top - g * 0.5f + 1.5f * u); lineTo(tapa.right + g - 1.5f * u, tapa.bottom - g * 0.5f - 1 * u)
            lineTo(tapa.right, tapa.bottom - 1 * u); lineTo(tapa.right, tapa.top); close()
        }
        drawPath(bloque, c(PAPEL_TALLER))
        val capas = (g / (1.5f * u)).toInt().coerceAtLeast(2)
        for (j in 1..capas) {
            val f = j / (capas + 1f)
            val tiembla = (azar(e.semilla, 200 + j) - 0.5f) * 1.2f * u
            drawLine(sepia.copy(alpha = 0.2f), Offset(tapa.right + g * f + tiembla, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.bottom - g * f * 0.5f), 0.5f * u)
        }
        // La hoja de adentro, con sus estudios.
        if (a > 0.01f) {
            val pag = tapa.deflate(2 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            drawRect(Color.Black.copy(alpha = 0.14f), pag.topLeft, Size(8 * u, pag.height))
            estudio(Offset(pag.left + pag.width * 0.32f, pag.top + pag.height * 0.32f), pag.width * 0.16f, c(SANGUINA).copy(alpha = 0.8f), 0.8f * u)
            var ry = pag.top + pag.height * 0.6f; var j = 0
            while (ry < pag.bottom - 8 * u) { espejo(pag.left + 12 * u, pag.right - 10 * u, ry, sepia.copy(alpha = 0.7f), e.semilla + j); ry += 7 * u; j++ }
            var ry2 = pag.top + 14 * u; var j2 = 0
            while (ry2 < pag.top + pag.height * 0.5f) { espejo(pag.left + pag.width * 0.6f, pag.right - 10 * u, ry2, sepia.copy(alpha = 0.7f), e.semilla + 40 + j2); ry2 += 7 * u; j2++ }
        }
        // La tapa de cartón: gira sobre el lomo.
        val sx = 1f - 0.94f * a
        val carton = c(CARTON)
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRect(carton, tapa.topLeft, tapa.size)
            for (j in 0 until 10) {
                val o = Offset(tapa.left + tapa.width * azar(e.semilla, 300 + j), tapa.top + tapa.height * azar(e.semilla, 320 + j))
                drawOval(Color.Black.copy(alpha = 0.06f), Offset(o.x - 8 * u, o.y - 4 * u), Size(16 * u, 8 * u))
            }
            bisel(tapa, Color.White.copy(alpha = 0.16f), Color.Black.copy(alpha = 0.3f), 1.6f * u)
            // La figura de construcción a sepia.
            estudio(Offset(tapa.left + tapa.width * 0.45f, tapa.top + tapa.height * 0.72f), minOf(tapa.width, tapa.height) * 0.2f, sepia.copy(alpha = 0.7f), 0.9f * u)
            // La etiqueta de papel pegada, con el nombre y la cuenta.
            val et = Rect(tapa.left + tapa.width * 0.08f, tapa.top + tapa.height * 0.1f, tapa.right - tapa.width * 0.18f, tapa.top + tapa.height * 0.42f)
            rotate(-1.5f, et.center) {
                drawRect(Color.Black.copy(alpha = 0.2f), et.topLeft + Offset(1.2f * u, 1.6f * u), et.size)
                drawRect(hoja, et.topLeft, et.size)
                drawLine(c(SANGUINA).copy(alpha = 0.6f), Offset(et.left + 6 * u, et.top + et.height * 0.6f), Offset(et.right - 6 * u, et.top + et.height * 0.6f), 0.7f * u)
                textoEn(medidor, e.nombre, Rect(et.left + 6 * u, et.top + 3 * u, et.right - 6 * u, et.top + et.height * 0.6f), letraTapa, c(TINTA), 16f, peso = FontWeight.Bold, minimo = 8f)
                textoEn(medidor, e.cuenta, Rect(et.left + 6 * u, et.top + et.height * 0.6f, et.right - 6 * u, et.bottom - 3 * u), letra, sepia, 11f, minimo = 6f)
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (j in 0 until 3) drawRect(Color.Black.copy(alpha = 0.1f * (3 - j) / 3f), Offset(borde, tapa.top), Size((3 + 4 * j) * u, tapa.height))
        }
        drawRect(mezcla(carton, Color.Black, 0.3f), Offset(tapa.left - 5 * u, tapa.top - 1 * u), Size(6 * u, tapa.height + 1 * u))
        // El cordón: en cruz mientras está cerrado; al abrir, suelto sobre la mesa.
        val cordon = c(CORDON)
        if (a < 0.12f) {
            val xc = tapa.left + tapa.width * 0.88f; val yc = tapa.top + tapa.height * 0.5f
            drawLine(cordon, Offset(xc, tapa.top - g * 0.5f), Offset(xc, mesa), 2.2f * u)
            drawLine(cordon, Offset(tapa.left - 5 * u, yc), Offset(tapa.right + g, yc - g * 0.5f), 2.2f * u)
            drawCircle(cordon, 4 * u, Offset(xc, yc))
            drawLine(cordon, Offset(xc, yc), Offset(xc + 7 * u, yc + 14 * u), 1.6f * u, cap = StrokeCap.Round)
            drawLine(cordon, Offset(xc, yc), Offset(xc - 5 * u, yc + 15 * u), 1.6f * u, cap = StrokeCap.Round)
        } else {
            drawPath(Path().apply {
                moveTo(tapa.right + g, mesa - 1 * u); cubicTo(tapa.right + g + 20 * u, mesa - 8 * u, tapa.right + g + 10 * u, mesa + 3 * u, tapa.right + g + 34 * u, mesa - 1 * u)
            }, cordon, style = Stroke(2 * u))
        }
    }

    /**
     * La pestaña de un día: un separador de cartón con su borde gastado, la letra de la semana a sepia en un circulito de
     * compás, el día en la letra del tema y, abajo, una aguada del color del ánimo (sin ánimo, una raya de sanguina).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val giro = -2f + 4f * azar(e.semilla, 1)
        rotate(giro, Offset(w / 2, h / 2)) {
            val r = Rect(4 * u, 3 * u, w - 4 * u, h - 3 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1.2f * u, 1.6f * u), r.size)
            drawRect(vitela(PAPEL_TALLER, oscuro, false), r.topLeft, r.size)
            drawRect(c(CARTON), r.topLeft, Size(r.width, 13 * u))
            drawCircle(c(SEPIA), 5 * u, Offset(w / 2, r.top + 6.5f * u), style = Stroke(0.7f * u))
            textoEn(medidor, e.semana, Rect(w / 2 - 5 * u, r.top + 1.5f * u, w / 2 + 5 * u, r.top + 11.5f * u), letra, c(TINTA), 8.5f, peso = FontWeight.Bold)
            textoEn(medidor, e.dia, Rect(r.left, r.top + 13 * u, r.right, r.bottom - 12 * u), letraTapa, c(TINTA), 19f, peso = FontWeight.Bold, minimo = 10f)
            if (e.tono != null) {
                drawRoundRect(e.tono, Offset(r.left + 4 * u, r.bottom - 11 * u), Size(r.width - 8 * u, 7 * u), CornerRadius(3.5f * u))
                drawRoundRect(Color.White.copy(alpha = 0.25f), Offset(r.left + 6 * u, r.bottom - 10 * u), Size(r.width * 0.4f, 2 * u), CornerRadius(1 * u))
            } else {
                drawLine(c(SANGUINA).copy(alpha = 0.6f), Offset(r.left + 7 * u, r.bottom - 7.5f * u), Offset(r.right - 7 * u, r.bottom - 7.5f * u), 0.9f * u)
            }
            drawRect(c(SEPIA).copy(alpha = 0.4f), r.topLeft, r.size, style = Stroke(0.6f * u))
        }
    }

    /**
     * El rótulo de un mes: una tira de papel de taller clavada con dos tachuelas de latón, pautada a sepia, con renglones en
     * espejo en las puntas y el mes en la letra del tema.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val tira = Rect(3 * u, 3 * u, w - 3 * u, h - 3 * u)
        drawPath(contornoIrregular(tira.translate(1.2f * u, 1.6f * u), 11, 40, 0.03f), Color.Black.copy(alpha = 0.22f))
        drawPath(contornoIrregular(tira, 11, 40, 0.03f), vitela(PAPEL_TALLER, oscuro, false))
        drawLine(c(SEPIA).copy(alpha = 0.35f), Offset(tira.left + 6 * u, tira.bottom - 6 * u), Offset(tira.right - 6 * u, tira.bottom - 6 * u), 0.6f * u)
        espejo(tira.left + 22 * u, tira.left + 60 * u, h / 2, c(SEPIA).copy(alpha = 0.55f), 5)
        espejo(tira.right - 60 * u, tira.right - 22 * u, h / 2, c(SEPIA).copy(alpha = 0.55f), 9)
        listOf(tira.left + 11 * u, tira.right - 11 * u).forEach { x ->
            drawCircle(Color.Black.copy(alpha = 0.3f), 3.4f * u, Offset(x + 0.8f * u, h / 2 + 1 * u))
            drawCircle(c(LATON), 3.4f * u, Offset(x, h / 2))
            drawCircle(Color.White.copy(alpha = 0.4f), 1.2f * u, Offset(x - 1 * u, h / 2 - 1 * u))
        }
        textoEn(medidor, texto, Rect(tira.left + 64 * u, tira.top + 2 * u, tira.right - 64 * u, tira.bottom - 4 * u), letraTapa, c(TINTA), 16f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El papel de la carta, la cera de sanguina del sello y el cordel sepia. */
    private val PAPEL_CARTA = Color(0xFFEEDFBC)
    private val CERA_SANGUINA = Color(0xFF8C3B1E)
    private val CORDEL = Color(0xFF5A3A1C)

    /** El sello de sanguina en [o], de radio [r]: la gota de cera con su filo y el compás del taller grabado. */
    private fun DrawScope.selloCompas(o: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        val cera = if (oscuro) mezcla(CERA_SANGUINA, Color.Black, 0.22f) else CERA_SANGUINA
        val hondo = mezcla(cera, Color.Black, 0.4f); val claro = mezcla(cera, Color.White, 0.32f)
        translate(r * 0.06f, r * 0.12f) { drawPath(gotaModerna(o, r, semilla + 7), Color.Black.copy(alpha = 0.3f)) }
        drawPath(gotaModerna(o, r, semilla + 7), cera)
        drawCircle(hondo, r * 0.78f, o, style = Stroke(r * 0.05f))
        val bisagra = Offset(o.x, o.y - r * 0.5f)
        drawLine(claro, bisagra, Offset(o.x - r * 0.36f, o.y + r * 0.46f), r * 0.08f, StrokeCap.Round)
        drawLine(claro, bisagra, Offset(o.x + r * 0.36f, o.y + r * 0.46f), r * 0.08f, StrokeCap.Round)
        drawCircle(claro, r * 0.09f, bisagra)
        drawArc(claro, 20f, 140f, false, Offset(o.x - r * 0.5f, o.y - r * 0.2f), Size(r, r), style = Stroke(r * 0.045f))
        drawArc(Color.White.copy(alpha = 0.22f), 205f, 70f, false, o - Offset(r * 0.86f, r * 0.86f), Size(r * 1.72f, r * 1.72f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /** Un cartellino en [r]: el papelito pegado de los cuadros, con su sombra y una esquina que se levanta. */
    private fun DrawScope.cartellino(r: Rect, papel: Color, u: Float) {
        val esquina = minOf(r.height * 0.35f, 7 * u)
        val forma = Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - esquina); lineTo(r.right - esquina, r.bottom); lineTo(r.left, r.bottom); close() }
        translate(1.5f * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.25f)) }
        drawPath(forma, papel)
        drawPath(Path().apply { moveTo(r.right, r.bottom - esquina); lineTo(r.right - esquina * 0.9f, r.bottom - esquina * 0.75f); lineTo(r.right - esquina, r.bottom); close() }, mezcla(papel, Color.Black, 0.18f))
        drawPath(forma, Color.Black.copy(alpha = 0.15f), style = Stroke(0.6f * u))
    }

    /**
     * La carta va plegada y trabada, como se cerraban las cartas antes de los sobres: un pliego de papel del taller con unos
     * trazos de construcción a sanguina (el círculo, su cuadrado y la diagonal), la solapa en bisel forrada de cartón y la
     * tira de papel que la traba pasando por un corte, sellada con cera de sanguina con el compás grabado. Las selladas al
     * futuro van además atadas con el doble cordel sepia y su nudo. A quién va y las fechas, en un cartellino pegado. Al
     * abrirse la cera se parte, la tira se suelta, la solapa se levanta y sale la carta. En oscuro el papel se hunde y el
     * cartellino se lee.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0E0A07), 0.6f) else x
        val p = Rect(c.left + W * 0.05f, c.top + H * 0.08f, c.right - W * 0.05f, c.bottom - H * 0.07f)
        val cx = p.center.x
        val largo = p.height * 0.38f
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val punta = Offset(cx, p.top + largo)
        val rs = minOf(W, H) * 0.1f
        val partido = e.romper.coerceIn(0f, 1f)
        // La solapa en bisel: baja recta por los lados, se achaflana y acaba en un borde plano.
        fun solapa(g: Float) = Path().apply {
            moveTo(p.left, p.top); lineTo(p.left + p.width * 0.04f, p.top + g * largo * 0.3f)
            lineTo(cx - p.width * 0.2f, p.top + g * largo); lineTo(cx + p.width * 0.2f, p.top + g * largo)
            lineTo(p.right - p.width * 0.04f, p.top + g * largo * 0.3f); lineTo(p.right, p.top); close()
        }
        fun DrawScope.selloEn(g: Float, mitad: Int) {
            val o = Offset(cx, p.top + g * largo)
            conAlfaModerna(e.alfaSello.coerceIn(0f, 1f), Rect(punta, rs * 4f * e.escalaSello)) {
                scale(e.escalaSello, punta) {
                    if (mitad == 0) {
                        // La tira que traba el pliego: baja por el corte y se sella bajo la cera (suelta, si ya se partió).
                        val suelta = partido * rs * 0.6f
                        drawRect(k(Color(0xFFF4E9CC)), Offset(o.x - rs * 0.32f + suelta * 0.3f, o.y - rs * 0.1f), Size(rs * 0.64f, rs * 1.9f - suelta))
                        drawRect(Color.Black.copy(alpha = 0.15f), Offset(o.x - rs * 0.32f + suelta * 0.3f, o.y - rs * 0.1f), Size(rs * 0.64f, rs * 1.9f - suelta), style = Stroke(0.6f * u))
                        if (e.brillo > 0f) {
                            drawCircle(Brush.radialGradient(0f to ORO.copy(alpha = 0.8f * e.brillo), 0.45f to LLAMA.copy(alpha = 0.28f * e.brillo),
                                1f to Color.Transparent, center = o, radius = rs * 3f), rs * 3f, o)
                            rayosModerna(o, rs * 1.3f, rs * 2.5f, ORO.copy(alpha = 0.9f * e.brillo), 12, 1.1f)
                        }
                    }
                    when {
                        partido <= 0f -> selloCompas(o, rs, oscuro, e.semilla)
                        mitad == 1 -> clipPath(mitadSelloModerna(o, rs, true, e.semilla)) {
                            translate(0f, -partido * rs * 0.18f) { rotate(-8f * partido, o) { selloCompas(o, rs, oscuro, e.semilla) } }
                        }
                        else -> clipPath(mitadSelloModerna(o, rs, false, e.semilla)) {
                            translate(0f, partido * rs * 0.1f) { rotate(6f * partido, o) { selloCompas(o, rs, oscuro, e.semilla) } }
                        }
                    }
                }
            }
        }

        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(p.left + 2 * u, p.top + 3 * u), p.size)
        if (giro < 0f) {
            drawPath(solapa(giro), k(CARTON))
            drawPath(solapa(giro), k(SEPIA).copy(alpha = 0.6f), style = Stroke(0.8f * u))
            if (partido > 0f) selloEn(giro, 1)
        }
        val sale = salidaModerna(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaModerna(Rect(cx - p.width * 0.42f, p.top + 3 * u - sube, cx + p.width * 0.42f, p.bottom - 6 * u - sube), e, oscuro, barbas = true)
        }
        // El pliego: papel del taller con sus dobleces y los trazos de construcción a sanguina.
        drawRect(k(PAPEL_CARTA), p.topLeft, p.size)
        clipRect(p.left, p.top, p.right, p.bottom) {
            val o = Offset(p.left + p.width * 0.8f, p.top + p.height * 0.58f); val r = p.height * 0.32f
            val trazo = k(CERA_SANGUINA).copy(alpha = 0.22f)
            drawCircle(trazo, r, o, style = Stroke(0.8f * u))
            drawRect(trazo, Offset(o.x - r * 0.707f, o.y - r * 0.707f), Size(r * 1.414f, r * 1.414f), style = Stroke(0.8f * u))
            drawLine(trazo, Offset(o.x - r * 1.4f, o.y + r * 1.2f), Offset(o.x + r * 1.3f, o.y - r * 1.3f), 0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u)))
            drawLine(Color.Black.copy(alpha = 0.08f), Offset(p.left, p.center.y + p.height * 0.1f), Offset(p.right, p.center.y + p.height * 0.1f), 1 * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.3f), Offset(p.left, p.center.y + p.height * 0.1f + 1 * u), Offset(p.right, p.center.y + p.height * 0.1f + 1 * u), 0.7f * u)
        }
        drawRect(k(SEPIA).copy(alpha = 0.45f), p.topLeft, p.size, style = Stroke(0.8f * u))
        // El cartellino con lo escrito.
        val car = Rect(p.left + p.width * 0.07f, punta.y + rs * 1.15f, p.right - p.width * 0.1f, p.bottom - 6 * u)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            cartellino(car, if (oscuro) mezcla(Color(0xFFF6EDD6), Color(0xFF0E0A07), 0.24f) else Color(0xFFF6EDD6), u)
            val m = car.top + car.height * 0.56f
            textoEn(medidor, e.linea1, Rect(car.left + 5 * u, car.top + 1.5f * u, car.right - 7 * u, m), letra, TINTA, 13f, peso = FontWeight.SemiBold, minimo = 8f)
            textoEn(medidor, e.linea2, Rect(car.left + 5 * u, m - 1 * u, car.right - 7 * u, car.bottom - 2 * u), letra, CERA_SANGUINA, 10f, minimo = 7.5f)
        }
        if (giro >= 0f) {
            translate(0f, 2 * u) { drawPath(solapa(giro), Color.Black.copy(alpha = 0.22f)) }
            drawPath(solapa(giro), k(Color(0xFFF2E6C8)))
            drawPath(solapa(giro), k(SEPIA).copy(alpha = 0.6f), style = Stroke(0.9f * u))
            // El corte por donde pasa la tira.
            if (giro > 0.5f) drawLine(k(TINTA).copy(alpha = 0.6f), Offset(cx - rs * 0.4f, p.top + giro * largo * 0.72f), Offset(cx + rs * 0.4f, p.top + giro * largo * 0.72f), 1 * u)
        }
        // El doble cordel sepia, con su nudo a la derecha.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val tramo = (p.width + 4 * u) * ata
            listOf(-0.42f, 0.42f).forEach { dy ->
                val y = punta.y + rs * dy
                drawLine(Color.Black.copy(alpha = 0.25f), Offset(p.left - 2 * u, y + 1 * u), Offset(p.left - 2 * u + tramo, y + 1 * u), 1.4f * u)
                drawLine(k(CORDEL), Offset(p.left - 2 * u, y), Offset(p.left - 2 * u + tramo, y), 1.4f * u)
            }
            if (ata > 0.85f) {
                val f = (ata - 0.85f) / 0.15f
                val o = Offset(p.left + p.width * 0.78f, punta.y)
                drawCircle(k(CORDEL), 2.4f * u, o)
                drawLine(k(CORDEL), o, o + Offset(rs * 0.5f * f, rs * 1.4f * f), 1.3f * u, StrokeCap.Round)
                drawLine(k(CORDEL), o, o + Offset(rs * 1.1f * f, rs * 1.1f * f), 1.3f * u, StrokeCap.Round)
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
     * El buzón es el portacartas del studiolo: una tabla de nogal con su marco de taracea (filetes de boj y de ébano) y
     * cintas de cuero clavadas con tachuelas de latón, en dos hileras, bajo las que se meten las cartas: las que ya se pueden
     * abrir con la cera de sanguina encendida, las selladas con el doble cordel, las leídas abiertas, con sus renglones. Una
     * pluma y un compás metidos en las cintas. Abajo, tres cartellini clavados con la cuenta, a tinta. A la derecha, un
     * candelero en la repisa, que en oscuro se enciende.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0E0A07), 0.58f) else x
        drawRect(k(MURO))
        drawRect(k(COTTO), Offset(0f, h * 0.93f), Size(w, h * 0.07f))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, h * 0.93f), Size(w, 2 * u))
        // El candelero, a la derecha.
        val vela = Rect(w * 0.935f - 3.5f * u, h * 0.42f, w * 0.935f + 3.5f * u, h * 0.84f)
        if (oscuro) drawRect(Brush.radialGradient(0f to LLAMA.copy(alpha = 0.32f), 0.45f to LLAMA.copy(alpha = 0.1f), 1f to Color.Transparent,
            center = Offset(vela.center.x, vela.top), radius = w * 0.85f))
        drawPath(Path().apply { moveTo(vela.center.x - 10 * u, h * 0.93f); lineTo(vela.center.x + 10 * u, h * 0.93f); lineTo(vela.center.x + 2.5f * u, vela.bottom); lineTo(vela.center.x - 2.5f * u, vela.bottom); close() }, k(LATON))
        drawOval(k(LATON), Offset(vela.center.x - 7 * u, vela.bottom - 2 * u), Size(14 * u, 4 * u))
        drawRect(if (oscuro) mezcla(CERA, Color.Black, 0.12f) else CERA, vela.topLeft, vela.size)
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(vela.right - 2 * u, vela.top), Size(2 * u, vela.height))
        drawLine(TINTA, Offset(vela.center.x, vela.top), Offset(vela.center.x, vela.top - 2 * u), 0.9f * u)
        if (oscuro) { haloModerna(Offset(vela.center.x, vela.top - 6 * u), 20 * u, LLAMA, 0.7f); llamaModerna(Offset(vela.center.x, vela.top - 1.5f * u), 10 * u, LLAMA) }

        // ------------------------------------------------ la tabla
        val tabla = Rect(w * 0.03f, h * 0.04f, w * 0.87f, h * 0.9f)
        drawRect(Color.Black.copy(alpha = 0.3f), tabla.topLeft + Offset(3 * u, 4 * u), tabla.size)
        drawRect(k(NOGAL), tabla.topLeft, tabla.size)
        vetas(tabla, k(VETA).copy(alpha = 0.4f), 51, densidad = 0.5f, grosor = 0.7f)
        // El marco de taracea: un filete de boj y otro de ébano, con un ajedrezado.
        val marco = 7 * u
        drawRect(k(NOGAL_OSC), tabla.topLeft, tabla.size, style = Stroke(marco))
        drawRect(k(Color(0xFFE2C990)), tabla.topLeft + Offset(marco * 0.5f, marco * 0.5f), Size(tabla.width - marco, tabla.height - marco), style = Stroke(1.2f * u))
        drawRect(k(Color(0xFF1E140C)), tabla.topLeft + Offset(marco + 1 * u, marco + 1 * u), Size(tabla.width - 2 * marco - 2 * u, tabla.height - 2 * marco - 2 * u), style = Stroke(1 * u))
        bisel(tabla, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.3f), 1.5f * u)
        val dentro = tabla.deflate(marco + 3 * u)
        // Las cartas, metidas bajo las cintas en dos hileras.
        val hileras = listOf(dentro.top + dentro.height * 0.3f, dentro.top + dentro.height * 0.64f)
        val cartas = repartoModerna(e, 10)
        cartas.forEachIndexed { i, clase ->
            val fila = i / 5; val col = i % 5
            val cxL = dentro.left + dentro.width * (col + 0.5f) / 5f + (if (fila == 1) dentro.width * 0.04f else 0f)
            val y = hileras[fila]
            val ancho = dentro.width / 5f * 0.78f; val alto = dentro.height * 0.3f
            val r = Rect(cxL - ancho / 2, y - alto * 0.6f, cxL + ancho / 2, y + alto * 0.4f)
            rotate((azar(i, 61) - 0.5f) * 14f, r.center) {
                drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(2 * u, 2.5f * u), r.size)
                if (clase == 2) {
                    drawRect(k(listOf(PERGAMINO, Color(0xFFF2E8D0))[i % 2]), r.topLeft, r.size)
                    for (j in 0 until 6) drawLine(k(SEPIA).copy(alpha = 0.5f), Offset(r.left + 3 * u, r.top + 5 * u + j * 3.6f * u), Offset(r.right - 3 * u - (j % 3) * 3 * u, r.top + 5 * u + j * 3.6f * u), 0.6f * u)
                    drawLine(Color.Black.copy(alpha = 0.12f), Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 0.8f * u)
                } else {
                    drawRect(k(PAPEL_CARTA), r.topLeft, r.size)
                    val o = Offset(r.center.x, r.top + r.height * 0.45f)
                    drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.left + r.width * 0.1f, r.top + r.height * 0.18f); lineTo(o.x - r.width * 0.2f, o.y); lineTo(o.x + r.width * 0.2f, o.y); lineTo(r.right - r.width * 0.1f, r.top + r.height * 0.18f); lineTo(r.right, r.top) }, k(SEPIA).copy(alpha = 0.6f), style = Stroke(0.7f * u))
                    if (clase == 1) listOf(-2.2f, 2.2f).forEach { dy -> drawLine(k(CORDEL), Offset(r.left, o.y + dy * u), Offset(r.right, o.y + dy * u), 1.1f * u) }
                    else {
                        haloModerna(o, ancho * 0.75f, ORO, if (oscuro) 1f else 0.8f)
                        rayosModerna(o, ancho * 0.24f, ancho * 0.5f, ORO, 8, 0.9f)
                    }
                    drawRect(k(Color(0xFFF4E9CC)), Offset(o.x - 2.5f * u, o.y - 1 * u), Size(5 * u, ancho * 0.3f))
                    drawPath(gotaModerna(o, ancho * 0.15f, i), k(CERA_SANGUINA))
                }
            }
        }
        // La pluma y el compás, metidos en las cintas.
        val pluma = Path().apply {
            val a = Offset(dentro.right - dentro.width * 0.04f, dentro.top + 2 * u); val b = Offset(dentro.right - dentro.width * 0.12f, dentro.top + dentro.height * 0.5f)
            moveTo(b.x, b.y); quadraticTo(a.x - 14 * u, a.y + 16 * u, a.x, a.y); quadraticTo(a.x - 4 * u, a.y + 20 * u, b.x + 2 * u, b.y - 6 * u); close()
        }
        drawPath(pluma, k(Color(0xFFF2EEE4)))
        drawPath(pluma, k(Color(0xFF9A9284)), style = Stroke(0.6f * u))
        val bisagra = Offset(dentro.left + dentro.width * 0.04f, dentro.top + dentro.height * 0.4f)
        drawLine(k(LATON), bisagra, bisagra + Offset(-4 * u, 34 * u), 1.6f * u, StrokeCap.Round)
        drawLine(k(LATON), bisagra, bisagra + Offset(8 * u, 32 * u), 1.6f * u, StrokeCap.Round)
        drawCircle(k(LATON_OSC), 2.2f * u, bisagra)
        // Las cintas de cuero clavadas con tachuelas.
        hileras.forEach { y ->
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(dentro.left - 2 * u, y + 1.5f * u), Size(dentro.width + 4 * u, 5 * u))
            drawRect(k(Color(0xFF6A3A22)), Offset(dentro.left - 2 * u, y - 2.5f * u), Size(dentro.width + 4 * u, 5 * u))
            drawLine(Color.White.copy(alpha = 0.15f), Offset(dentro.left - 2 * u, y - 2 * u), Offset(dentro.right + 2 * u, y - 2 * u), 0.6f * u)
            for (j in 0..5) { val x = dentro.left + dentro.width * j / 5f; drawCircle(k(LATON), 2 * u, Offset(x, y)); drawCircle(k(LATON_OSC), 2 * u, Offset(x, y), style = Stroke(0.5f * u)) }
        }
        // Los tres cartellini con la cuenta.
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "selladas", e.abiertas to "leídas")
        rotulos.forEachIndexed { i, (n, nombre) ->
            val xc = dentro.left + dentro.width * (i * 2 + 1) / 6f
            val r = Rect(xc - dentro.width * 0.14f, dentro.bottom - dentro.height * 0.17f, xc + dentro.width * 0.14f, dentro.bottom)
            cartellino(r, if (oscuro) mezcla(Color(0xFFF6EDD6), Color(0xFF0E0A07), 0.26f) else Color(0xFFF6EDD6), u)
            drawCircle(k(LATON), 1.6f * u, Offset(r.left + 4 * u, r.top + 4 * u))
            val m = r.left + r.width * 0.36f
            textoEn(medidor, "$n", Rect(r.left + 4 * u, r.top, m, r.bottom), letra, if (i == 0 && n > 0) CERA_SANGUINA else TINTA, 18f, peso = FontWeight.Bold)
            textoEn(medidor, nombre, Rect(m, r.top + 1 * u, r.right - 6 * u, r.bottom - 1 * u), letra, TINTA, 11f, minimo = 7f)
        }
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 118.dp

    /** Un cabo torcido, recto de [a] a [b], de [grosor]: el cordón claro y las vueltas sesgadas de sus cordones. */
    private fun DrawScope.cabo(a: Offset, b: Offset, grosor: Float, claro: Color, oscuro: Color) {
        val largo = (b - a).getDistance()
        if (largo < 1f) return
        drawLine(claro, a, b, grosor, cap = StrokeCap.Round)
        val d = (b - a) / largo
        val n = Offset(-d.y, d.x)
        val paso = grosor * 0.9f
        var s = paso / 2
        while (s < largo) {
            val c0 = a + d * s
            drawLine(oscuro, c0 + n * (grosor * 0.45f) - d * (grosor * 0.3f), c0 - n * (grosor * 0.45f) + d * (grosor * 0.3f), grosor * 0.22f)
            s += paso
        }
    }

    /** La rosa de los vientos: cuatro rumbos largos y cuatro cortos, cada punta en dos mitades (clara y oscura), y la flor del norte. */
    private fun DrawScope.rosa(o: Offset, r: Float, claro: Color, oscuro: Color, norte: Color, grosor: Float) {
        drawCircle(oscuro, r * 0.92f, o, style = Stroke(grosor))
        drawCircle(oscuro, r * 0.84f, o, style = Stroke(grosor * 0.6f))
        for (k in listOf(1, 3, 5, 7, 0, 2, 4, 6)) {
            val largo = if (k % 2 == 0) r else r * 0.58f
            val a = Math.toRadians(k * 45.0 - 90.0)
            val ancho = largo * 0.2f
            fun en(g: Double, l: Float) = Offset(o.x + l * kotlin.math.cos(g).toFloat(), o.y + l * kotlin.math.sin(g).toFloat())
            val punta = en(a, largo)
            drawPath(Path().apply { moveTo(o.x, o.y); en(a - Math.PI / 2, ancho).let { lineTo(it.x, it.y) }; lineTo(punta.x, punta.y); close() }, claro)
            drawPath(Path().apply { moveTo(o.x, o.y); en(a + Math.PI / 2, ancho).let { lineTo(it.x, it.y) }; lineTo(punta.x, punta.y); close() }, oscuro)
        }
        drawPath(Path().apply { moveTo(o.x, o.y - r * 1.18f); lineTo(o.x + r * 0.12f, o.y - r * 0.98f); lineTo(o.x - r * 0.12f, o.y - r * 0.98f); close() }, norte)
        drawCircle(claro, r * 0.08f, o)
    }

    /**
     * La cartela de una carta de marear: el marco de cabo torcido con un nudo en cada esquina; a la izquierda, la rosa de
     * los vientos con la flor del norte en rojo (de noche, la luna y su reflejo sobre el mar, con estrellas); a la derecha,
     * «Bitácora · singladura …» en letra de corsario, la fecha (si es larga, en dos renglones) y el detalle.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro && !p.oscura, e.noche)
        val claro = c(CABO); val osc = c(CABO_OSC)
        val esquinas = listOf(Offset(5f * u, 7f * u), Offset(w - 5f * u, 7f * u), Offset(w - 5f * u, h - 5f * u), Offset(5f * u, h - 5f * u))
        for (k in 0..3) cabo(esquinas[k], esquinas[(k + 1) % 4], 3f * u, claro, osc)
        esquinas.forEach { drawCircle(osc, 3.6f * u, it); drawCircle(claro, 2.4f * u, it) }
        val o = Offset(37f * u, h / 2 + 1f * u); val r = 25f * u
        if (!e.noche) {
            rosa(o, r, mezcla(p.color, p.tinta, 0.18f), p.acento, p.detalle, 0.8f * u)
        } else {
            val luna = mezcla(LUNA, p.acento, 0.15f)
            val mar = o.y + r * 0.45f
            drawPath(mediaLunaModerna(Offset(o.x - 3f * u, o.y - 7f * u), r * 0.4f), luna)
            drawPath(Path().apply {
                moveTo(o.x - r, mar); var x = o.x - r; var k = 0
                while (x < o.x + r) { quadraticTo(x + 3f * u, mar + (if (k % 2 == 0) -2f else 2f) * u, x + 6f * u, mar); x += 6f * u; k++ }
            }, p.acento.copy(alpha = 0.8f), style = Stroke(1f * u))
            for (k in 0..2) drawLine(luna.copy(alpha = 0.55f), Offset(o.x - (7f - k * 2f) * u, mar + (4f + k * 3f) * u), Offset(o.x + (3f - k * 2f) * u, mar + (4f + k * 3f) * u), 1f * u)
            listOf(Offset(0.55f, -0.75f), Offset(0.85f, -0.2f), Offset(-0.75f, -0.6f), Offset(0.3f, -1f)).forEach {
                drawPath(estrella(Offset(o.x + it.x * r, o.y + it.y * r), 1.8f * u, 4, 0.4f), luna)
            }
        }
        val x0 = o.x + r + 12f * u; val x1 = w - 12f * u
        textoEn(medidor, e.rotulo, Rect(x0, 13f * u, x1, 39f * u), LetraExtra.corsario, rotuloModerna(p, e.noche), 21f, minimo = 9f)
        fechaModerna(medidor, e.fecha, Rect(x0, 40f * u, x1, 81f * u), letra, p.tinta, 20f)
        textoEn(medidor, e.detalle, Rect(x0, 82f * u, x1, 99f * u), letra, p.tenue, 11f, minimo = 7f)
    }

    /**
     * La carta de marear: desde una rosa arriba a la derecha salen sus dieciséis líneas de rumbo (y otras, más tenues,
     * desde abajo a la izquierda); en el margen izquierdo, la escala de latitudes a tramos claros y oscuros; abajo, la «X»
     * con la derrota punteada que lleva a ella; y una aureola de sal o de café en la hoja. En modo oscuro, el farol de a
     * bordo cuelga arriba a la izquierda: el papel se hunde en la penumbra y la llama lo tiñe de ámbar.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val tinta = p.tinta
        val o = Offset(w * 0.86f, h * 0.1f); val largo = w * 0.7f
        for (k in 0 until 16) {
            val a = Math.PI / 2 + k * Math.PI / 16
            drawLine(tinta.copy(alpha = if (k % 4 == 0) 0.09f else 0.05f), o, Offset(o.x + (largo * kotlin.math.cos(a)).toFloat(), o.y + (largo * kotlin.math.sin(a)).toFloat()), 1f * u)
        }
        drawCircle(tinta.copy(alpha = 0.1f), w * 0.05f, o, style = Stroke(1f * u))
        drawPath(estrella(o, w * 0.045f, 8, 0.3f), tinta.copy(alpha = 0.1f), style = Stroke(0.8f * u))
        val o2 = Offset(w * 0.1f, h * 0.92f)
        for (k in 0..8) {
            val a = -Math.PI / 2 + k * Math.PI / 16
            drawLine(tinta.copy(alpha = 0.035f), o2, Offset(o2.x + (largo * kotlin.math.cos(a)).toFloat(), o2.y + (largo * kotlin.math.sin(a)).toFloat()), 1f * u)
        }
        var y = 20f * u; var k = 0
        while (y < h - 20f * u) {
            val alto = minOf(12f * u, h - 20f * u - y)
            if (k % 2 == 0) drawRect(tinta.copy(alpha = 0.18f), Offset(6f * u, y), Size(3f * u, alto))
            else drawRect(tinta.copy(alpha = 0.12f), Offset(6f * u, y), Size(3f * u, alto), style = Stroke(0.6f * u))
            y += 12f * u; k++
        }
        val x = Offset(w * 0.18f, h * 0.9f); val s = w * 0.025f
        drawLine(p.detalle.copy(alpha = 0.25f), x - Offset(s, s), x + Offset(s, s), 2f * u)
        drawLine(p.detalle.copy(alpha = 0.25f), x + Offset(-s, s), x + Offset(s, -s), 2f * u)
        drawPath(Path().apply { moveTo(x.x + s * 1.6f, x.y - s * 0.6f); quadraticTo(w * 0.32f, h * 0.95f, w * 0.44f, h * 0.8f) },
            p.detalle.copy(alpha = 0.2f), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f * u, 4f * u))))
        val mancha = Offset(w * 0.78f, h * 0.6f)
        drawCircle(p.tenue.copy(alpha = 0.07f), 26f * u, mancha, style = Stroke(3f * u))
        drawCircle(p.tenue.copy(alpha = 0.04f), 23f * u, mancha + Offset(1f * u, 1f * u), style = Stroke(1.5f * u))
        if (oscuro && !p.oscura) {
            val xl = 19f * u
            val llama = Offset(xl, 118f * u)
            penumbraModerna(llama, LLAMA)
            haloModerna(llama, 34f * u, LLAMA, 0.6f)
            val laton = t(LATON, true, false)
            var yc = 0f
            while (yc < llama.y - 20f * u) { drawOval(laton, Offset(xl - 1.2f * u, yc), Size(2.4f * u, 3.4f * u), style = Stroke(0.7f * u)); yc += 3f * u }
            drawCircle(laton, 2.4f * u, Offset(xl, llama.y - 20f * u), style = Stroke(1f * u))
            drawPath(Path().apply { moveTo(xl - 4f * u, llama.y - 17.5f * u); lineTo(xl + 4f * u, llama.y - 17.5f * u); lineTo(xl + 7.5f * u, llama.y - 12f * u); lineTo(xl - 7.5f * u, llama.y - 12f * u); close() }, laton)
            drawRect(mezcla(CRISTAL, LLAMA, 0.4f), Offset(xl - 6f * u, llama.y - 12f * u), Size(12f * u, 18f * u))
            listOf(xl - 6f * u, xl + 6f * u).forEach { drawLine(laton, Offset(it, llama.y - 12f * u), Offset(it, llama.y + 6f * u), 1.2f * u) }
            llamaModerna(Offset(xl, llama.y + 3f * u), 11f * u, LLAMA)
            drawPath(Path().apply { moveTo(xl - 7.5f * u, llama.y + 6f * u); lineTo(xl + 7.5f * u, llama.y + 6f * u); lineTo(xl + 4f * u, llama.y + 10f * u); lineTo(xl - 4f * u, llama.y + 10f * u); close() }, laton)
        }
    }

    /** La barra: una franja de papel de carta algo más tostado, con un cabo torcido al pie. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(p.color, p.tenue, 0.14f).let { if (oscuro) mezcla(it, Color.Black, 0.24f) else it }
        drawRect(base)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, h))
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        cabo(Offset(0f, h - 4f * u), Offset(w, h - 4f * u), 3.2f * u, c(CABO), c(CABO_OSC))
    }

    /** El botón de asentar: una tablilla de roble con su marco de latón y un tornillo en cada punta. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        val madera = c(ROBLE_LUZ); val laton = c(LATON)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1f * u, 1.6f * u), r.size, CornerRadius(3f * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(madera, Color.White, 0.08f), mezcla(madera, Color.Black, 0.25f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(3f * u))
        vetas(r.deflate(2f * u), c(VETA).copy(alpha = 0.35f), 5, densidad = 0.6f, grosor = 0.6f)
        drawRoundRect(laton, r.topLeft, r.size, CornerRadius(3f * u), style = Stroke(1.3f * u))
        listOf(r.left + 7f * u, r.right - 7f * u).forEach { x ->
            val c0 = Offset(x, r.center.y)
            drawCircle(laton, 2.3f * u, c0)
            drawLine(c(LATON_OSC), Offset(x - 1.4f * u, c0.y - 1.4f * u), Offset(x + 1.4f * u, c0.y + 1.4f * u), 0.7f * u)
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFF0DDA8)

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 256.dp

    /** La lona cruda de la bitácora, el cuero de sus cantoneras, el ébano de la regla paralela y el verde de la costa. */
    private val LONA = Color(0xFFBDB294)
    private val CUERO = Color(0xFF6A4228)
    private val EBANO = Color(0xFF1E1A16)
    private val COSTA = Color(0xFF8A9A6A)

    /** El grosor de la bitácora, en dp: de 4 con una singladura a 18 con 240 o más. */
    private fun grosorBitacora(hojas: Int): Float = 4f + 14f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /** Un cuadrilátero visto en perspectiva: el borde de delante de [x0] a [x1] a la altura [y1], el de atrás metido [p] y a la altura [y0]. */
    private fun plano(x0: Float, x1: Float, y0: Float, y1: Float, p: Float): Path = Path().apply {
        moveTo(x0 + p, y0); lineTo(x1 - p, y0); lineTo(x1, y1); lineTo(x0, y1); close()
    }

    /**
     * La bitácora de a bordo sobre la mesa de cartas del camarote: el forro de tablas con el ojo de buey (el mar de día, la
     * noche en oscuro) y la lámpara de cardán colgada; la mesa con su barandilla de mar alrededor, la carta de marear
     * extendida con su costa, sus rumbos y su rosa, la regla paralela de ébano, el compás de puntas y el sextante; abajo, el
     * cajón de las cartas y la cubierta. La bitácora es de lona cruda cosida a mano, con cantoneras de cuero, la rosa de
     * los vientos pintada, la etiqueta con el nombre y la cuenta y la correa que la cierra con su cazonete de madera.
     * Engorda con las singladuras (más canto, ondulado por la humedad) y le asoman más papeles. Al abrirse, la correa se
     * suelta, la tapa gira y se ve la tabla de horas, nudos y rumbo. En oscuro, la lámpara de cardán alumbra el camarote.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0A0B0E), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorBitacora(e.hojas) * u
        val hoja = papel(oscuro, false)
        // El forro de tablas del camarote.
        drawRect(c(ROBLE))
        var tx = 0f; var k = 0
        while (tx < w) {
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(tx, 0f), Offset(tx, h * 0.5f), 1.2f * u)
            if (k % 3 == 1) drawCircle(c(ROBLE_OSC), 1.2f * u, Offset(tx + 10 * u, h * 0.08f))
            tx += 26 * u; k++
        }
        vetas(Rect(0f, 0f, w, h * 0.5f), c(VETA).copy(alpha = 0.35f), 3, densidad = 0.5f, vertical = true, grosor = 0.6f)
        // El ojo de buey, a la derecha.
        val ojo = Offset(w * 0.86f, h * 0.15f); val ro = 17 * u
        drawCircle(if (oscuro) MAR_NOCHE else CIELO, ro, ojo)
        clipPath(Path().apply { addOval(Rect(ojo, ro)) }) {
            drawRect(if (oscuro) mezcla(MAR_NOCHE, Color.Black, 0.3f) else MAR, Offset(ojo.x - ro, ojo.y + ro * 0.15f), Size(2 * ro, ro))
            if (oscuro) drawPath(mediaLunaModerna(Offset(ojo.x - ro * 0.35f, ojo.y - ro * 0.4f), ro * 0.22f), LUNA.copy(alpha = 0.8f))
            else drawOval(NUBE, Offset(ojo.x - ro * 0.7f, ojo.y - ro * 0.55f), Size(ro * 0.9f, ro * 0.3f))
        }
        drawCircle(c(LATON), ro, ojo, style = Stroke(5 * u))
        for (j in 0 until 6) {
            val ang = Math.toRadians(j * 60.0)
            drawCircle(c(LATON_OSC), 1.2f * u, Offset(ojo.x + (ro + 0.5f * u) * kotlin.math.cos(ang).toFloat(), ojo.y + (ro + 0.5f * u) * kotlin.math.sin(ang).toFloat()))
        }
        drawArc(Color.White.copy(alpha = 0.3f), 200f, 60f, false, Offset(ojo.x - ro * 0.7f, ojo.y - ro * 0.7f), Size(ro * 1.4f, ro * 1.4f), style = Stroke(1.4f * u))
        // La cubierta.
        drawRect(c(ROBLE_OSC), Offset(0f, h * 0.84f), Size(w, h * 0.16f))
        var dy = h * 0.84f
        while (dy < h) { drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, dy), Offset(w, dy), 1 * u); dy += 9 * u }

        // ------------------------------------------------ la lámpara de cardán, colgada a la izquierda
        val eje = Offset(w * 0.12f, h * 0.18f)
        drawLine(c(LATON), Offset(eje.x - 16 * u, eje.y - 18 * u), Offset(eje.x - 16 * u, eje.y + 4 * u), 2.4f * u)
        drawLine(c(LATON), Offset(eje.x - 16 * u, eje.y - 4 * u), Offset(eje.x - 11 * u, eje.y - 4 * u), 2 * u)
        drawOval(c(LATON), Offset(eje.x - 11 * u, eje.y - 8 * u), Size(22 * u, 8 * u), style = Stroke(1.6f * u))
        val vidrio = Rect(eje.x - 6 * u, eje.y - 26 * u, eje.x + 6 * u, eje.y - 2 * u)
        drawRoundRect(if (oscuro) LLAMA.copy(alpha = 0.6f) else c(CRISTAL).copy(alpha = 0.7f), vidrio.topLeft, vidrio.size, CornerRadius(5 * u))
        drawRect(c(LATON), Offset(eje.x - 7 * u, eje.y - 2 * u), Size(14 * u, 9 * u))
        drawRect(c(LATON), Offset(eje.x - 4 * u, eje.y - 30 * u), Size(8 * u, 4 * u))
        val llama = Offset(eje.x, eje.y - 8 * u)
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to LLAMA.copy(alpha = 0.32f), 0.35f to LLAMA.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.95f))
            haloModerna(llama - Offset(0f, 4 * u), 26 * u, LLAMA, 0.7f)
            llamaModerna(llama, 10 * u, LLAMA)
        } else {
            drawLine(c(TINTA), Offset(eje.x, eje.y - 3 * u), Offset(eje.x, eje.y - 6 * u), 0.8f * u)
        }

        // ------------------------------------------------ la mesa de cartas
        val m0 = h * 0.3f; val m1 = h * 0.74f
        val tablero = plano(w * 0.02f, w * 0.98f, m0, m1, w * 0.07f)
        drawPath(tablero, c(ROBLE_LUZ))
        clipPath(tablero) { vetas(Rect(0f, m0, w, m1), c(VETA).copy(alpha = 0.35f), 7, densidad = 0.6f, grosor = 0.6f) }
        // La carta de marear: el papel, la costa, los rumbos y una rosa en la esquina.
        val carta = plano(w * 0.08f, w * 0.92f, m0 + 6 * u, m1 - 4 * u, w * 0.06f)
        drawPath(carta, c(PAPEL))
        clipPath(carta) {
            drawPath(Path().apply {
                moveTo(w * 0.1f, m0); cubicTo(w * 0.2f, m0 + 30 * u, w * 0.05f, m0 + 40 * u, w * 0.12f, m1); lineTo(0f, m1); lineTo(0f, m0); close()
            }, c(COSTA))
            val centro = Offset(w * 0.86f, m1 - 18 * u)
            for (j in 0 until 16) {
                val ang = Math.toRadians(j * 22.5)
                drawLine(c(TINTA).copy(alpha = 0.18f), centro, Offset(centro.x + w * kotlin.math.cos(ang).toFloat(), centro.y + w * 0.35f * kotlin.math.sin(ang).toFloat()), 0.5f * u)
            }
            rosa(centro, 12 * u, c(PAPEL), c(TINTA).copy(alpha = 0.7f), c(LACRE), 0.7f * u)
        }
        drawPath(carta, c(TINTA).copy(alpha = 0.3f), style = Stroke(0.6f * u))
        // La regla paralela de ébano, atrás a la izquierda.
        listOf(0f, 7f).forEach { dyy -> drawRect(c(EBANO), Offset(w * 0.1f, m0 + (10 + dyy) * u), Size(w * 0.14f, 4 * u)) }
        listOf(0.13f, 0.21f).forEach { f -> drawLine(c(LATON), Offset(w * f, m0 + 12 * u), Offset(w * f + 3 * u, m0 + 19 * u), 1.4f * u) }
        // La barandilla de mar alrededor del tablero y el canto con el cajón.
        drawPath(tablero, c(ROBLE_OSC), style = Stroke(3 * u))
        val canto = Rect(w * 0.02f, m1, w * 0.98f, m1 + 6 * u)
        drawRect(c(ROBLE_OSC), Offset(canto.left, m1 - 5 * u), Size(canto.width, 5 * u))
        drawRect(c(ROBLE), canto.topLeft, canto.size)
        val frente = Rect(w * 0.04f, canto.bottom, w * 0.96f, h * 0.86f)
        drawRect(c(ROBLE_OSC), frente.topLeft, frente.size)
        val cajon = Rect(w * 0.3f, frente.top + 4 * u, w * 0.7f, frente.bottom - 4 * u)
        drawRect(c(ROBLE), cajon.topLeft, cajon.size)
        bisel(cajon, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.3f), 1.2f * u)
        drawArc(c(LATON), 0f, 180f, false, Offset(cajon.center.x - 8 * u, cajon.center.y - 4 * u), Size(16 * u, 8 * u), style = Stroke(2 * u))

        // ------------------------------------------------ el sextante y el compás de puntas
        sextante(Offset(w * 0.89f, m0 + 40 * u), 30 * u, oscuro, false)
        val bis = Offset(w * 0.07f, m1 - 10 * u)
        drawLine(c(LATON), bis, Offset(w * 0.2f, m1 - 24 * u), 1.8f * u)
        drawLine(c(LATON), bis, Offset(w * 0.21f, m1 - 14 * u), 1.8f * u)
        drawCircle(c(LATON), 3 * u, bis)

        // ------------------------------------------------ la bitácora
        val yb = h * 0.69f
        val L = w * 0.21f; val R = w * 0.79f
        val prof = h * 0.33f
        val p = (R - L) * 0.06f
        val yt = yb - g
        val tapa = plano(L, R, yt - prof, yt, p)
        drawPath(plano(L + 3 * u, R + 4 * u, yt - prof + 4 * u, yb + 4 * u, p), Color.Black.copy(alpha = 0.3f))
        // El canto de las hojas, ondulado por la humedad, y los papeles que asoman.
        drawRect(c(LONA), Offset(L, yb - 2 * u), Size(R - L, 2 * u))
        drawRect(hoja, Offset(L + 1 * u, yt), Size(R - L - 2 * u, g - 2 * u))
        var py = yt + 1.4f * u; var pk = 0
        while (py < yb - 2 * u) {
            val onda = Path().apply {
                moveTo(L + 1 * u, py)
                var x = L + 1 * u
                while (x < R - 1 * u) { quadraticTo(x + 6 * u, py + (if (pk % 2 == 0) 0.6f else -0.6f) * u, x + 12 * u, py); x += 12 * u }
            }
            drawPath(onda, c(TINTA).copy(alpha = 0.18f), style = Stroke(0.5f * u))
            py += 1.5f * u; pk++
        }
        val papeles = (e.hojas / 30).coerceIn(0, 6)
        for (j in 0 until papeles) {
            val mx = L + (R - L) * (0.08f + 0.14f * j + 0.05f * azar(e.semilla, j))
            rotate(-8f + 16f * azar(e.semilla, 10 + j), Offset(mx, yb)) {
                drawRect(hoja, Offset(mx, yt + g * 0.3f), Size(14 * u, g * 0.7f + (4 + 4 * azar(e.semilla, 20 + j)) * u))
                drawRect(c(TINTA).copy(alpha = 0.2f), Offset(mx, yt + g * 0.3f), Size(14 * u, g * 0.7f + (4 + 4 * azar(e.semilla, 20 + j)) * u), style = Stroke(0.5f * u))
            }
        }
        // La tabla de la singladura: horas, nudos y rumbo.
        if (a > 0.01f) {
            val pag = plano(L + 2 * u, R - 2 * u, yt - prof + 2 * u, yt - 1 * u, p)
            drawPath(pag, hoja)
            val top = yt - prof + 10 * u; val bot = yt - 8 * u
            listOf(0.18f, 0.32f, 0.5f).forEach { f ->
                val x0 = L + p + 10 * u + (R - L - 2 * p - 20 * u) * f
                drawLine(c(TINTA).copy(alpha = 0.5f), Offset(x0, top), Offset(x0 - p * 0.3f, bot), 0.6f * u)
            }
            var ry = top + 8 * u
            while (ry < bot) { drawLine(c(TINTA).copy(alpha = 0.25f), Offset(L + p + 8 * u, ry), Offset(R - p - 8 * u, ry), 0.5f * u); ry += 6 * u }
            drawLine(c(LACRE).copy(alpha = 0.6f), Offset(L + p + 8 * u, top + 6 * u), Offset(R - p - 8 * u, top + 6 * u), 1 * u)
        }
        // La tapa de lona: cosida, con cantoneras, la rosa pintada y la etiqueta; gira sobre el lomo.
        val sx = 1f - 0.94f * a
        val lona = c(LONA)
        scale(sx, 1f, Offset(L, yt)) {
            drawPath(tapa, lona)
            clipPath(tapa) {
                var ty2 = yt - prof
                while (ty2 < yt) { drawLine(Color.Black.copy(alpha = 0.06f), Offset(L, ty2), Offset(R, ty2), 0.5f * u); ty2 += 2.2f * u }
                var tx2 = L
                while (tx2 < R) { drawLine(Color.White.copy(alpha = 0.08f), Offset(tx2, yt - prof), Offset(tx2, yt), 0.5f * u); tx2 += 2.2f * u }
                mancha(Offset(L + (R - L) * 0.3f, yt - prof * 0.25f), 26 * u, Color.Black.copy(alpha = 0.06f))
            }
            drawPath(plano(L + 6 * u, R - 6 * u, yt - prof + 5 * u, yt - 5 * u, p), c(TINTA).copy(alpha = 0.45f),
                style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
            // Las cantoneras de cuero.
            val cuero = c(CUERO)
            listOf(Triple(L + p, yt - prof, 1f), Triple(R - p, yt - prof, -1f)).forEach { (x, y, s) ->
                drawPath(Path().apply { moveTo(x, y); lineTo(x + s * 18 * u, y); lineTo(x + s * (p * 0.3f), y + 14 * u); close() }, cuero)
            }
            listOf(L to 1f, R to -1f).forEach { (x, s) ->
                drawPath(Path().apply { moveTo(x, yt); lineTo(x + s * 20 * u, yt); lineTo(x + s * (p * 0.25f), yt - 15 * u); close() }, cuero)
            }
            // La rosa de los vientos pintada a tinta.
            rosa(Offset(R - p - (R - L) * 0.15f, yt - prof * 0.3f), prof * 0.2f, lona, c(TINTA).copy(alpha = 0.7f), c(LACRE), 0.8f * u)
            // La etiqueta con el nombre y la cuenta.
            val et = Rect(L + p + (R - L) * 0.06f, yt - prof * 0.86f, L + (R - L) * 0.66f, yt - prof * 0.3f)
            drawRect(Color.Black.copy(alpha = 0.25f), et.topLeft + Offset(1.2f * u, 1.6f * u), et.size)
            drawRect(hoja, et.topLeft, et.size)
            drawRect(c(TINTA).copy(alpha = 0.6f), et.deflate(2.5f * u).topLeft, et.deflate(2.5f * u).size, style = Stroke(0.7f * u))
            val corte = et.top + et.height * 0.58f
            textoEn(medidor, e.nombre, Rect(et.left + 5 * u, et.top + 3 * u, et.right - 5 * u, corte), letraTapa, c(TINTA), 15f, peso = FontWeight.Bold, minimo = 7f)
            textoEn(medidor, e.cuenta, Rect(et.left + 5 * u, corte, et.right - 5 * u, et.bottom - 3 * u), letra, c(TINTA).copy(alpha = 0.85f), 10f, minimo = 6f)
            if (a > 0f) drawPath(tapa, Color.Black.copy(alpha = 0.45f * a))
        }
        drawLine(mezcla(lona, Color.Black, 0.35f), Offset(L + p, yt - prof), Offset(L, yb), 3 * u)
        // La correa de cuero con su cazonete: cierra mientras está cerrada; al abrir, suelta sobre la carta.
        val cuero = c(CUERO)
        if (a < 0.12f) {
            val yc = yt - prof * 0.68f
            drawRect(cuero, Offset(R - p * 0.5f - (R - L) * 0.24f, yc - 4 * u), Size((R - L) * 0.24f + 8 * u, 8 * u))
            val caz = Offset(R - p * 0.5f - (R - L) * 0.24f, yc)
            drawRoundRect(c(ROBLE_LUZ), Offset(caz.x - 3 * u, caz.y - 8 * u), Size(6 * u, 16 * u), CornerRadius(3 * u))
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(caz.x - 3 * u, caz.y - 8 * u), Size(6 * u, 16 * u), CornerRadius(3 * u), style = Stroke(0.7f * u))
        } else {
            drawRect(cuero, Offset(R, yb - 6 * u), Size(30 * u, 7 * u))
            drawRoundRect(c(ROBLE_LUZ), Offset(R + 28 * u, yb - 9 * u), Size(6 * u, 14 * u), CornerRadius(3 * u))
        }
    }

    /**
     * La pestaña de un día: una ficha de papel con las esquinas de arriba cortadas y un ollao de latón con su cabito, la
     * letra de la semana, el día en la letra del tema y, abajo, una bandera de señales del color del ánimo con su driza (sin
     * ánimo, la bandera en blanco, solo su contorno).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val forma = Path().apply {
            moveTo(10 * u, 2 * u); lineTo(w - 10 * u, 2 * u); lineTo(w - 3 * u, 9 * u); lineTo(w - 3 * u, h - 2 * u)
            lineTo(3 * u, h - 2 * u); lineTo(3 * u, 9 * u); close()
        }
        drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(2.4f * u))
        drawPath(forma, papel(oscuro, false))
        drawPath(forma, c(TINTA).copy(alpha = 0.4f), style = Stroke(0.6f * u))
        val ollao = Offset(w / 2, 8 * u)
        drawCircle(c(LATON), 3.4f * u, ollao)
        drawCircle(c(ROBLE_OSC), 1.6f * u, ollao)
        drawLine(c(CABO), ollao, Offset(w / 2 - 6 * u, 0f), 1.4f * u)
        textoEn(medidor, e.semana, Rect(4 * u, 12 * u, w - 4 * u, 20 * u), letra, c(TINTA).copy(alpha = 0.75f), 8.5f, peso = FontWeight.Bold)
        textoEn(medidor, e.dia, Rect(4 * u, 19 * u, w - 4 * u, 38 * u), letraTapa, c(TINTA), 18f, peso = FontWeight.Bold, minimo = 10f)
        val asta = w / 2 - 10 * u
        drawLine(c(ROBLE_OSC), Offset(asta, 39 * u), Offset(asta, h - 4 * u), 1 * u)
        val bandera = Rect(asta, 40 * u, asta + 18 * u, h - 6 * u)
        if (e.tono != null) {
            drawRect(e.tono, bandera.topLeft, bandera.size)
            drawRect(Color.White.copy(alpha = 0.6f), Offset(bandera.left, bandera.center.y - 1.2f * u), Size(bandera.width, 2.4f * u))
        } else {
            drawRect(c(TINTA).copy(alpha = 0.4f), bandera.topLeft, bandera.size, style = Stroke(0.7f * u))
        }
    }

    /**
     * El rótulo de un mes: una tabla de roble con un cabo torcido a lo largo de arriba y de abajo y, en medio, la placa de
     * latón atornillada con el mes grabado.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        drawRect(c(ROBLE), Offset(0f, 3 * u), Size(w, h - 6 * u))
        vetas(Rect(0f, 3 * u, w, h - 3 * u), c(VETA).copy(alpha = 0.4f), 5, densidad = 1f, grosor = 0.6f)
        cabo(Offset(2 * u, 4 * u), Offset(w - 2 * u, 4 * u), 4 * u, c(CABO), c(CABO_OSC))
        cabo(Offset(2 * u, h - 4 * u), Offset(w - 2 * u, h - 4 * u), 4 * u, c(CABO), c(CABO_OSC))
        val placa = Rect(w * 0.12f, 8 * u, w * 0.88f, h - 8 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), placa.topLeft + Offset(1.2f * u, 1.6f * u), placa.size)
        drawRect(if (oscuro) mezcla(c(LATON), LATON, 0.45f) else LATON, placa.topLeft, placa.size)
        drawRect(c(LATON_OSC), placa.deflate(2 * u).topLeft, placa.deflate(2 * u).size, style = Stroke(0.7f * u))
        listOf(placa.left + 5 * u, placa.right - 5 * u).forEach { x ->
            drawCircle(c(LATON_OSC), 2 * u, Offset(x, h / 2))
            drawLine(c(ROBLE_OSC), Offset(x - 1.4f * u, h / 2 - 1.4f * u), Offset(x + 1.4f * u, h / 2 + 1.4f * u), 0.7f * u)
        }
        textoEn(medidor, texto, Rect(placa.left + 12 * u, placa.top + 1 * u, placa.right - 12 * u, placa.bottom - 1 * u), letraTapa, c(TINTA), 14f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El verde de mar del vidrio de la botella, la brea del sello y el corcho. */
    private val VIDRIO_MAR = Color(0xFF2F5D62)
    private val BREA = Color(0xFF2B2622)
    private val CORCHO = Color(0xFFB08A5A)

    /**
     * Una botella tumbada con el cuello a la derecha: el cuerpo de [x0] a [x1], de alto [alto], el cuello hasta [xc] y la
     * boca con su gollete. Devuelve la silueta del vidrio.
     */
    private fun botella(x0: Float, x1: Float, xc: Float, y: Float, alto: Float): Path = Path().apply {
        val r = alto / 2; val cuello = alto * 0.19f
        moveTo(x0 + r * 0.35f, y - r)
        lineTo(x1 - r * 0.5f, y - r)
        cubicTo(x1, y - r, x1 + r * 0.2f, y - cuello, x1 + r * 0.6f, y - cuello)
        lineTo(xc, y - cuello); lineTo(xc, y + cuello); lineTo(x1 + r * 0.6f, y + cuello)
        cubicTo(x1 + r * 0.2f, y + cuello, x1, y + r, x1 - r * 0.5f, y + r)
        lineTo(x0 + r * 0.35f, y + r)
        quadraticTo(x0, y + r, x0, y + r * 0.6f); lineTo(x0, y - r * 0.6f); quadraticTo(x0, y - r, x0 + r * 0.35f, y - r)
        close()
    }

    /** El sello de brea en [o], de radio [r]: la gota negra derramada, con la calavera y las tibias en relieve. */
    private fun DrawScope.selloBrea(o: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        val brea = if (oscuro) mezcla(BREA, Color.Black, 0.2f) else BREA
        val claro = mezcla(brea, Color(0xFFE6DCC0), if (oscuro) 0.38f else 0.5f)
        translate(r * 0.06f, r * 0.12f) { drawPath(gotaModerna(o, r, semilla + 11, 18), Color.Black.copy(alpha = 0.35f)) }
        drawPath(gotaModerna(o, r, semilla + 11, 18), brea)
        // Dos goterones que se corrieron por el cuello.
        drawOval(brea, Offset(o.x - r * 0.75f, o.y + r * 0.5f), Size(r * 0.3f, r * 0.75f))
        drawOval(brea, Offset(o.x - r * 0.3f, o.y + r * 0.6f), Size(r * 0.26f, r * 0.55f))
        for (j in 0 until 16) drawCircle(claro.copy(alpha = 0.5f), r * 0.045f, polarModerna(o, r * 0.8f, j * 22.5f))
        listOf(-1f, 1f).forEach { s ->
            val a = Offset(o.x - s * r * 0.48f, o.y + r * 0.42f); val b = Offset(o.x + s * r * 0.48f, o.y - r * 0.06f)
            drawLine(claro, a, b, r * 0.09f, StrokeCap.Round)
            drawCircle(claro, r * 0.07f, a); drawCircle(claro, r * 0.07f, b)
        }
        drawCircle(claro, r * 0.28f, Offset(o.x, o.y - r * 0.12f))
        drawRoundRect(claro, Offset(o.x - r * 0.15f, o.y + r * 0.05f), Size(r * 0.3f, r * 0.18f), CornerRadius(r * 0.05f))
        drawCircle(brea, r * 0.075f, Offset(o.x - r * 0.1f, o.y - r * 0.13f)); drawCircle(brea, r * 0.075f, Offset(o.x + r * 0.1f, o.y - r * 0.13f))
        drawArc(Color.White.copy(alpha = 0.25f), 205f, 70f, false, o - Offset(r * 0.86f, r * 0.86f), Size(r * 1.72f, r * 1.72f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /** Un rollo de papel visto de lado, de [a] a [b] (su eje), de grosor [g], atado con un hilo rojo al medio. */
    private fun DrawScope.rolloPapel(a: Offset, b: Offset, g: Float, papel: Color) {
        drawLine(papel, a, b, g, StrokeCap.Round)
        drawLine(Color.Black.copy(alpha = 0.18f), a + Offset(0f, g * 0.25f), b + Offset(0f, g * 0.25f), g * 0.35f, StrokeCap.Round)
        drawCircle(mezcla(papel, Color.Black, 0.2f), g / 2, b)
        drawCircle(mezcla(papel, Color.Black, 0.4f), g * 0.2f, b, style = Stroke(g * 0.08f))
        val m = (a + b) / 2f
        drawLine(LACRE, m - Offset(0f, g / 2), m + Offset(0f, g / 2), g * 0.18f)
    }

    /**
     * La carta va en una botella de vidrio verde de mar, con el mensaje enrollado dentro y atado con un hilo rojo, el
     * corcho metido en el gollete y lacrado con brea (la gota negra con la calavera y las tibias). Las selladas al futuro
     * llevan además unas vueltas de cabo de cáñamo en el cuello, con su nudo y sus chicotes. A quién va y las fechas, en una
     * etiqueta de papel pegada a la botella. Al abrirse la brea se cuartea, el corcho sale y el mensaje sube desenrollado. En
     * oscuro el vidrio se hunde casi a negro, con sus brillos; la etiqueta se lee.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF080A0C), 0.55f) else x
        val y = c.top + H * 0.53f
        val alto = minOf(H * 0.6f, W * 0.42f)
        val x0 = c.left + W * 0.04f; val x1 = c.left + W * 0.66f; val xc = c.left + W * 0.8f
        val cuello = alto * 0.19f
        val vidrio = botella(x0, x1, xc, y, alto)
        val top = y - alto / 2
        val partido = e.romper.coerceIn(0f, 1f)
        val abre = e.abrir.coerceIn(0f, 1f)
        val rs = alto * 0.2f
        val sello = Offset(xc + cuello * 1.2f, y)
        // El corcho: sale hacia la derecha y arriba, girando, con la mitad de la brea que se lleva.
        val corchoSale = Offset(abre * alto * 0.12f, -abre * alto * 0.78f)
        val corchoGiro = -35f * abre

        // La sombra en la arena.
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.18f), Offset(x0 + 4 * u, y + alto / 2 - 2 * u), Size(xc - x0, 7 * u))
        // La carta, desenrollada, que sube por detrás de la botella.
        val sale = salidaModerna(e.subir)
        if (sale > 0f) {
            val sube = sale * (top + 3 * u - 4 * u)
            val hoja = Rect(c.left + W * 0.08f, top + 3 * u - sube, c.left + W * 0.64f, top + alto * 0.45f)
            hojaCartaModerna(hoja, e, oscuro, barbas = true)
            rolloPapel(Offset(hoja.left - 2 * u, hoja.bottom), Offset(hoja.right + 2 * u, hoja.bottom), alto * 0.16f, if (oscuro) mezcla(e.hoja, Color.Black, 0.2f) else e.hoja)
        }
        // El vidrio: verde de mar, un poco transparente, con el mensaje dentro (si aún no ha salido).
        drawPath(vidrio, k(VIDRIO_MAR).copy(alpha = 0.55f))
        if (sale < 0.02f) clipPath(vidrio) {
            rolloPapel(Offset(x0 + alto * 0.35f, y + alto * 0.12f), Offset(x1 - alto * 0.05f, y + alto * 0.02f), alto * 0.34f, k(PAPEL).copy(alpha = 0.9f))
        }
        drawPath(vidrio, k(VIDRIO_MAR).copy(alpha = 0.35f))
        clipPath(vidrio) {
            // El fondo de la botella (el picado) y un poco de arena dentro.
            drawOval(k(mezcla(VIDRIO_MAR, Color.Black, 0.3f)).copy(alpha = 0.6f), Offset(x0 - alto * 0.1f, y - alto * 0.4f), Size(alto * 0.3f, alto * 0.8f), style = Stroke(1.6f * u))
            drawOval(k(Color(0xFFD8C690)).copy(alpha = 0.7f), Offset(x0 + alto * 0.1f, y + alto * 0.36f), Size((x1 - x0) * 0.7f, alto * 0.2f))
        }
        drawPath(vidrio, k(mezcla(VIDRIO_MAR, Color.Black, 0.45f)), style = Stroke(1.2f * u))
        // Los brillos del vidrio.
        drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.55f), Offset(x0 + alto * 0.3f, top + alto * 0.14f), Offset(x1 - alto * 0.3f, top + alto * 0.14f), 2.2f * u, StrokeCap.Round)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.35f), Offset(x1 + alto * 0.2f, y - cuello * 0.55f), Offset(xc - 3 * u, y - cuello * 0.55f), 1.2f * u, StrokeCap.Round)
        // El gollete.
        drawRoundRect(k(mezcla(VIDRIO_MAR, Color.Black, 0.2f)), Offset(xc - 3 * u, y - cuello * 1.25f), Size(5 * u, cuello * 2.5f), CornerRadius(2 * u))
        // La etiqueta con lo escrito.
        val et = Rect(x0 + alto * 0.18f, y - alto * 0.34f, x1 + alto * 0.04f, y + alto * 0.34f)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            val forma = contornoIrregular(et, e.semilla, 26, 0.02f)
            drawPath(forma, if (oscuro) mezcla(PAPEL, Color(0xFF080A0C), 0.24f) else PAPEL)
            drawPath(forma, k(TINTA).copy(alpha = 0.35f), style = Stroke(0.7f * u))
            drawRect(k(LACRE).copy(alpha = 0.6f), et.topLeft + Offset(3 * u, 3 * u), Size(et.width - 6 * u, et.height - 6 * u), style = Stroke(0.6f * u))
            val m = et.top + et.height * 0.56f
            textoEn(medidor, e.linea1, Rect(et.left + 5 * u, et.top + 3 * u, et.right - 5 * u, m), letra, TINTA, 12f, peso = FontWeight.SemiBold, minimo = 7.5f)
            textoEn(medidor, e.linea2, Rect(et.left + 5 * u, m - 1 * u, et.right - 5 * u, et.bottom - 3 * u), letra, mezcla(LACRE, Color.Black, 0.1f), 9f, minimo = 6.5f)
        }
        // Las vueltas de cabo en el cuello, con su nudo y los chicotes.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val vueltas = (ata * 4.99f).toInt()
            for (j in 0 until vueltas) {
                val x = x1 + alto * 0.62f + j * 3.2f * u
                drawLine(k(CABO_OSC), Offset(x + 0.8f * u, y - cuello * 1.15f), Offset(x - 0.8f * u, y + cuello * 1.15f), 2.6f * u, StrokeCap.Round)
                drawLine(k(CABO), Offset(x + 0.8f * u, y - cuello * 1.1f), Offset(x - 0.8f * u, y + cuello * 1.1f), 1.8f * u, StrokeCap.Round)
            }
            if (ata > 0.85f) {
                val f = (ata - 0.85f) / 0.15f
                val o = Offset(x1 + alto * 0.68f, y + cuello * 1.2f)
                drawCircle(k(CABO), 2.6f * u, o)
                drawLine(k(CABO), o, o + Offset(-alto * 0.1f * f, alto * 0.32f * f), 1.8f * u, StrokeCap.Round)
                drawLine(k(CABO), o, o + Offset(alto * 0.12f * f, alto * 0.28f * f), 1.8f * u, StrokeCap.Round)
            }
        }
        // El corcho (y la brea): metido en el gollete o saliendo.
        val corcho = Rect(xc - cuello * 0.6f, y - cuello * 0.95f, xc + cuello * 1.2f, y + cuello * 0.95f)
        rotate(corchoGiro, sello) {
            translate(corchoSale.x, corchoSale.y) {
                drawRoundRect(k(CORCHO), corcho.topLeft, corcho.size, CornerRadius(1.5f * u))
                for (j in 0 until 6) drawCircle(k(mezcla(CORCHO, Color.Black, 0.35f)), 0.7f * u, Offset(corcho.left + corcho.width * azar(e.semilla, 700 + j), corcho.top + corcho.height * azar(e.semilla, 710 + j)))
            }
        }
        conAlfaModerna(e.alfaSello.coerceIn(0f, 1f), Rect(sello, rs * 4f * e.escalaSello)) {
            scale(e.escalaSello, sello) {
                if (e.brillo > 0f) {
                    drawCircle(Brush.radialGradient(0f to Color(0xFFF2D27A).copy(alpha = 0.85f * e.brillo), 0.45f to LLAMA.copy(alpha = 0.28f * e.brillo),
                        1f to Color.Transparent, center = sello, radius = rs * 3.2f), rs * 3.2f, sello)
                    rayosModerna(sello, rs * 1.3f, rs * 2.6f, Color(0xFFF2D27A).copy(alpha = 0.95f * e.brillo), 12, 1.1f)
                }
                if (partido <= 0f) rotate(corchoGiro, sello) { translate(corchoSale.x, corchoSale.y) { selloBrea(sello, rs, oscuro, e.semilla) } }
                else {
                    // La brea cuarteada: la mitad de abajo queda en el gollete; la de arriba se va con el corcho.
                    clipPath(mitadSelloModerna(sello, rs, false, e.semilla)) { translate(-partido * rs * 0.1f, partido * rs * 0.12f) { selloBrea(sello, rs, oscuro, e.semilla) } }
                    rotate(corchoGiro, sello) {
                        translate(corchoSale.x, corchoSale.y - partido * rs * 0.15f) { clipPath(mitadSelloModerna(sello, rs, true, e.semilla)) { selloBrea(sello, rs, oscuro, e.semilla) } }
                    }
                }
            }
        }
    }

    override val cajaPrevia = Rect(0.125f, 0.08f, 0.6f, 0.25f)

    override val altoBuzon: Dp = 214.dp

    /**
     * El buzón es la red de pesca colgada en el costado del camarote, de dos cabillas, con sus corchos de flotar, y dentro
     * las botellas que trajo el mar: las que ya se pueden descorchar, con la brea encendida; las selladas, con su corcho
     * lacrado y el cabo al cuello; las abiertas, sin corcho y con el mensaje asomando. Abajo, tres tablas de deriva colgadas
     * de un cabo, con la cuenta pintada en blanco. A la derecha, el farol, que en oscuro se enciende.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF080A0C), 0.58f) else x
        // El costado del camarote: tablones de roble con sus juntas calafateadas.
        drawRect(k(ROBLE))
        var ty = 0f; var fila = 0
        while (ty < h) {
            drawRect(k(listOf(ROBLE, ROBLE_LUZ, ROBLE_OSC)[(fila * 7) % 3]).copy(alpha = 0.35f), Offset(0f, ty), Size(w, 19 * u))
            drawLine(k(Color(0xFF14100C)), Offset(0f, ty), Offset(w, ty), 1.6f * u)
            var tx = (fila * 37 % 5) * 30f * u
            while (tx < w) { drawLine(k(Color(0xFF14100C)), Offset(tx, ty), Offset(tx, ty + 19 * u), 1 * u); drawCircle(k(LATON_OSC), 1.2f * u, Offset(tx + 4 * u, ty + 9.5f * u)); tx += 120 * u }
            ty += 19 * u; fila++
        }
        vetas(Rect(0f, 0f, w, h), k(VETA).copy(alpha = 0.3f), 77, densidad = 0.35f, grosor = 0.6f)
        // El farol, a la derecha.
        val farol = Offset(w * 0.93f, h * 0.36f)
        if (oscuro) drawRect(Brush.radialGradient(0f to LLAMA.copy(alpha = 0.34f), 0.45f to LLAMA.copy(alpha = 0.12f), 1f to Color.Transparent,
            center = farol, radius = w * 0.8f))
        drawLine(k(LATON_OSC), Offset(farol.x, 0f), Offset(farol.x, farol.y - 16 * u), 1.4f * u)
        val caja = Rect(farol.x - 9 * u, farol.y - 14 * u, farol.x + 9 * u, farol.y + 12 * u)
        drawRect(if (oscuro) Color(0xFFF2D27A) else k(CRISTAL), caja.topLeft, caja.size)
        drawRect(k(LATON), caja.topLeft, caja.size, style = Stroke(1.6f * u))
        drawLine(k(LATON), Offset(caja.center.x, caja.top), Offset(caja.center.x, caja.bottom), 1 * u)
        drawPath(Path().apply { moveTo(caja.left - 2 * u, caja.top); lineTo(caja.center.x, caja.top - 7 * u); lineTo(caja.right + 2 * u, caja.top); close() }, k(LATON))
        drawRect(k(LATON), Offset(caja.left - 2 * u, caja.bottom), Size(caja.width + 4 * u, 3 * u))
        if (oscuro) { haloModerna(farol, 24 * u, LLAMA, 0.6f); llamaModerna(Offset(farol.x, farol.y + 6 * u), 10 * u, LLAMA) }

        // ------------------------------------------------ la red con las botellas
        val izq = Offset(w * 0.04f, h * 0.07f); val der = Offset(w * 0.84f, h * 0.07f)
        val fondoRed = h * 0.66f
        listOf(izq, der).forEach { cab ->
            drawRoundRect(k(ROBLE_OSC), Offset(cab.x - 2.5f * u, cab.y - 8 * u), Size(5 * u, 16 * u), CornerRadius(2.5f * u))
            drawCircle(k(ROBLE_LUZ), 3.5f * u, Offset(cab.x, cab.y - 8 * u))
        }
        val bolsa = Path().apply {
            moveTo(izq.x, izq.y)
            quadraticTo((izq.x + der.x) / 2, izq.y + h * 0.08f, der.x, der.y)
            quadraticTo(der.x + 6 * u, fondoRed, (izq.x + der.x) / 2, fondoRed + h * 0.04f)
            quadraticTo(izq.x - 6 * u, fondoRed, izq.x, izq.y)
            close()
        }
        drawPath(bolsa, Color.Black.copy(alpha = 0.18f))
        // Las botellas, en dos hileras dentro de la red.
        val cartas = repartoModerna(e, 10)
        val filas = listOf(h * 0.3f, h * 0.5f)
        cartas.forEachIndexed { i, clase ->
            val f = if (cartas.size <= 5) 1 else i / 5
            val col = if (cartas.size <= 5) i else i % 5
            val n = if (cartas.size <= 5) cartas.size else if (f == 0) 5 else cartas.size - 5
            val ancho = (der.x - izq.x) * 0.84f
            val x = izq.x + (der.x - izq.x) * 0.08f + ancho * (col + 0.5f) / maxOf(n, 1)
            val yb = filas[f] + (if (f == 1 || cartas.size <= 5) 0f else 0f)
            val largo = minOf(ancho / maxOf(n, 1) * 1.3f, 72 * u)
            val altoB = largo * 0.36f
            rotate(-28f + (azar(i, 91) - 0.5f) * 18f, Offset(x, yb)) {
                val b0 = x - largo * 0.5f; val b1 = x + largo * 0.18f; val bc = x + largo * 0.45f
                val v = botella(b0, b1, bc, yb, altoB)
                drawPath(v, k(listOf(VIDRIO_MAR, Color(0xFF3E5A3A), Color(0xFF5A4630))[i % 3]).copy(alpha = 0.75f))
                clipPath(v) { rolloPapel(Offset(b0 + altoB * 0.3f, yb + altoB * 0.1f), Offset(b1, yb), altoB * 0.38f, k(PAPEL)) }
                drawPath(v, k(Color(0xFF14100C)).copy(alpha = 0.6f), style = Stroke(0.9f * u))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.45f), Offset(b0 + altoB * 0.3f, yb - altoB * 0.3f), Offset(b1 - altoB * 0.3f, yb - altoB * 0.3f), 1.2f * u, StrokeCap.Round)
                val boca = Offset(bc + 2 * u, yb)
                when (clase) {
                    2 -> {
                        // Abierta: sin corcho; el mensaje asoma desenrollado por el gollete.
                        drawPath(Path().apply { moveTo(boca.x - 1 * u, yb - altoB * 0.15f); lineTo(boca.x + largo * 0.3f, yb - altoB * 0.55f); lineTo(boca.x + largo * 0.36f, yb + altoB * 0.05f); lineTo(boca.x - 1 * u, yb + altoB * 0.15f); close() }, k(PAPEL))
                        for (j in 0 until 2) drawLine(k(TINTA).copy(alpha = 0.5f), Offset(boca.x + largo * 0.06f, yb - altoB * (0.05f + 0.18f * j)), Offset(boca.x + largo * 0.26f, yb - altoB * (0.3f + 0.18f * j)), 0.6f * u)
                    }
                    else -> {
                        drawRect(k(CORCHO), Offset(boca.x - 3 * u, yb - altoB * 0.17f), Size(6 * u, altoB * 0.34f))
                        if (clase == 0) {
                            haloModerna(boca, largo * 0.32f, Color(0xFFF2D27A), if (oscuro) 1f else 0.8f)
                            rayosModerna(boca, largo * 0.12f, largo * 0.26f, Color(0xFFF2D27A), 8, 0.9f)
                        } else for (j in 0 until 3) drawLine(k(CABO), Offset(b1 + altoB * 0.6f + j * 2.4f * u, yb - altoB * 0.22f), Offset(b1 + altoB * 0.6f + j * 2.4f * u, yb + altoB * 0.22f), 1.4f * u)
                        drawCircle(k(BREA), altoB * 0.24f, boca + Offset(2 * u, 0f))
                    }
                }
            }
        }
        // La malla de la red, por delante de las botellas, y la relinga con sus corchos.
        clipPath(bolsa) {
            val paso = 13 * u
            var x = -h
            while (x < w) {
                drawLine(k(CABO).copy(alpha = 0.55f), Offset(x, 0f), Offset(x + h, h), 0.8f * u)
                drawLine(k(CABO).copy(alpha = 0.55f), Offset(x + h, 0f), Offset(x, h), 0.8f * u)
                x += paso
            }
        }
        drawPath(bolsa, k(CABO_OSC), style = Stroke(1.4f * u))
        val relinga = Path().apply { moveTo(izq.x, izq.y); quadraticTo((izq.x + der.x) / 2, izq.y + h * 0.08f, der.x, der.y) }
        drawPath(relinga, k(CABO), style = Stroke(2.4f * u))
        for (j in 1..5) {
            val t = j / 6f
            val px = (1 - t) * (1 - t) * izq.x + 2 * (1 - t) * t * (izq.x + der.x) / 2 + t * t * der.x
            val py = (1 - t) * (1 - t) * izq.y + 2 * (1 - t) * t * (izq.y + h * 0.08f) + t * t * der.y
            drawOval(k(CORCHO), Offset(px - 5 * u, py - 3.5f * u), Size(10 * u, 7 * u))
            drawOval(Color.Black.copy(alpha = 0.25f), Offset(px - 5 * u, py - 3.5f * u), Size(10 * u, 7 * u), style = Stroke(0.7f * u))
        }
        // Las tres tablas de deriva con la cuenta.
        val cabo = h * 0.75f
        drawLine(k(CABO), Offset(0f, cabo), Offset(w * 0.86f, cabo), 2 * u)
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "selladas", e.abiertas to "abiertas")
        rotulos.forEachIndexed { i, (n, nombre) ->
            val xc = w * (0.15f + 0.28f * i)
            val r = Rect(xc - w * 0.125f, cabo + 7 * u, xc + w * 0.125f, h - 4 * u)
            drawLine(k(CABO), Offset(xc - w * 0.06f, cabo), Offset(r.left + 6 * u, r.top + 3 * u), 1.4f * u)
            drawLine(k(CABO), Offset(xc + w * 0.06f, cabo), Offset(r.right - 6 * u, r.top + 3 * u), 1.4f * u)
            val tabla = contornoIrregular(r, 120 + i, 18, 0.04f)
            translate(2 * u, 2.5f * u) { drawPath(tabla, Color.Black.copy(alpha = 0.35f)) }
            drawPath(tabla, if (oscuro) mezcla(Color(0xFF8E7A62), Color(0xFF080A0C), 0.35f) else Color(0xFF8E7A62))
            clipPath(tabla) { vetas(r, Color(0xFF4A3A2A).copy(alpha = 0.45f), 130 + i, densidad = 0.6f, grosor = 0.6f) }
            drawPath(tabla, Color.Black.copy(alpha = 0.35f), style = Stroke(0.8f * u))
            val m = r.left + r.width * 0.36f
            textoEn(medidor, "$n", Rect(r.left + 3 * u, r.top, m, r.bottom), letra, Color(0xFFF4EEDC), 18f, peso = FontWeight.Bold)
            textoEn(medidor, nombre, Rect(m, r.top + 2 * u, r.right - 4 * u, r.bottom - 2 * u), letra, Color(0xFFF4EEDC), 10f, minimo = 7f)
        }
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /**
     * La cabecera de un pliego impreso de la Revolución: arriba, la cinta tricolor (azul, blanco y rojo) y el filete grueso y
     * fino de imprenta; a la izquierda, la escarapela con sus dos cintas (de noche, la media luna y una estrella en su
     * lugar, dentro de un aro); a la derecha, el rótulo en dos renglones (el título en versales rojas y el resto en tinta
     * tenue), la fecha del calendario republicano (si es larga, en dos renglones) y el detalle; abajo, otra vez el filete
     * fino y grueso.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val hundir = oscuro && !p.oscura
        fun c(x: Color) = t(x, hundir, e.noche)
        val tercio = (w - 4f * u) / 3
        listOf(c(AZUL), c(BLANCO), c(ROJO)).forEachIndexed { i, col -> drawRect(col, Offset(2f * u + i * tercio, 3f * u), Size(tercio, 5f * u)) }
        drawRect(p.tinta.copy(alpha = 0.35f), Offset(2f * u, 3f * u), Size(w - 4f * u, 5f * u), style = Stroke(0.6f * u))
        drawLine(p.tinta.copy(alpha = 0.8f), Offset(2f * u, 12f * u), Offset(w - 2f * u, 12f * u), 2f * u)
        drawLine(p.tinta.copy(alpha = 0.7f), Offset(2f * u, 15.5f * u), Offset(w - 2f * u, 15.5f * u), 0.7f * u)
        drawLine(p.tinta.copy(alpha = 0.7f), Offset(2f * u, h - 8f * u), Offset(w - 2f * u, h - 8f * u), 0.7f * u)
        drawLine(p.tinta.copy(alpha = 0.8f), Offset(2f * u, h - 4.5f * u), Offset(w - 2f * u, h - 4.5f * u), 2f * u)
        val o = Offset(30f * u, h / 2 - 2f * u); val r = 19f * u
        if (!e.noche) {
            listOf(-1f to c(AZUL), 1f to c(ROJO)).forEach { (s, col) ->
                drawPath(Path().apply {
                    moveTo(o.x + s * 1f * u, o.y); lineTo(o.x + s * 12f * u, o.y + r + 13f * u); lineTo(o.x + s * 7f * u, o.y + r + 9f * u)
                    lineTo(o.x + s * 3f * u, o.y + r + 14f * u); lineTo(o.x - s * 3f * u, o.y); close()
                }, col)
            }
            escarapela(o, r, hundir, false)
        } else {
            val luna = mezcla(LUNA, p.acento, 0.2f)
            drawCircle(p.acento.copy(alpha = 0.5f), r, o, style = Stroke(1f * u))
            drawPath(mediaLunaModerna(Offset(o.x - 2f * u, o.y + 1f * u), r * 0.55f), luna)
            drawPath(estrella(Offset(o.x + r * 0.45f, o.y - r * 0.45f), 3f * u, 5, 0.45f), luna)
        }
        val x0 = o.x + r + 12f * u; val x1 = w - 8f * u
        val partes = e.rotulo.split(" · ", limit = 2)
        if (partes.size == 2) {
            textoEn(medidor, partes[0], Rect(x0, 19f * u, x1, 36f * u), letra, rotuloModerna(p, e.noche), 14f, espaciado = 2f, minimo = 8f)
            textoEn(medidor, partes[1], Rect(x0, 36f * u, x1, 49f * u), LetraExtra.ciudadana, p.tenue, 10.5f, espaciado = 0.8f, minimo = 7f)
        } else {
            textoEn(medidor, e.rotulo, Rect(x0, 19f * u, x1, 49f * u), letra, rotuloModerna(p, e.noche), 14f, espaciado = 2f, minimo = 8f)
        }
        fechaModerna(medidor, e.fecha, Rect(x0, 50f * u, x1, 88f * u), LetraExtra.ciudadana, p.tinta, 21f)
        textoEn(medidor, e.detalle, Rect(x0, 89f * u, x1, 106f * u), LetraExtra.ciudadana, p.tenue, 11f, minimo = 7f)
    }

    /**
     * El pliego impreso: el marco de filete grueso y fino alrededor, con un florón en cada esquina; el margen del cuaderno
     * con su filete azul y su filete rojo; al pie, la cinta tricolor; y abajo a la derecha, el sello redondo de la comuna en
     * tinta roja. En modo oscuro, el farol de la calle, colgado de su brazo de hierro en el margen derecho: el papel se
     * hunde en la penumbra y la luz de gas lo tiñe de amarillo.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(p.tinta.copy(alpha = 0.22f), Offset(8f * u, 8f * u), Size(w - 16f * u, h - 16f * u), style = Stroke(1.6f * u))
        drawRect(p.tinta.copy(alpha = 0.15f), Offset(11f * u, 11f * u), Size(w - 22f * u, h - 22f * u), style = Stroke(0.6f * u))
        listOf(Offset(8f * u, 8f * u), Offset(w - 8f * u, 8f * u), Offset(w - 8f * u, h - 8f * u), Offset(8f * u, h - 8f * u)).forEach { o ->
            rotate(45f, o) { drawRect(p.acento.copy(alpha = 0.35f), Offset(o.x - 3.5f * u, o.y - 3.5f * u), Size(7f * u, 7f * u)) }
            drawCircle(p.color, 1.4f * u, o)
        }
        listOf(18f * u to p.detalle, 22f * u to p.acento).forEach { (x, col) -> drawLine(col.copy(alpha = 0.45f), Offset(x, 11f * u), Offset(x, h - 11f * u), 1.5f * u) }
        val tercio = w * 0.4f / 3
        val tri = if (noche) listOf(p.detalle, p.tenue, p.acento) else listOf(AZUL, BLANCO, ROJO)
        tri.forEachIndexed { i, col -> drawRect(col.copy(alpha = 0.4f), Offset(w * 0.3f + i * tercio, h - 22f * u), Size(tercio, 3f * u)) }
        val s = Offset(w - 58f * u, h - 72f * u); val r = 22f * u
        val tintaRoja = p.acento.copy(alpha = 0.22f)
        drawCircle(tintaRoja, r, s, style = Stroke(1.4f * u))
        drawCircle(tintaRoja, r - 5f * u, s, style = Stroke(0.7f * u))
        for (k in 0 until 24) {
            val g = Math.toRadians(k * 15.0)
            val cs = kotlin.math.cos(g).toFloat(); val sn = kotlin.math.sin(g).toFloat()
            drawLine(tintaRoja, Offset(s.x + cs * (r - 4f * u), s.y + sn * (r - 4f * u)), Offset(s.x + cs * (r - 1.5f * u), s.y + sn * (r - 1.5f * u)), 1f * u)
        }
        drawPath(estrella(s, 7f * u, 5, 0.45f), tintaRoja)
        if (oscuro && !p.oscura) {
            val x = w - 17f * u
            val luz = Offset(x, 254f * u)
            penumbraModerna(luz, FAROL)
            haloModerna(luz, 34f * u, FAROL, 0.6f)
            val hierro = t(HIERRO, true, false)
            drawLine(hierro, Offset(w, 232f * u), Offset(x, 232f * u), 2f * u)
            drawArc(hierro, 90f, 180f, false, Offset(x + 2f * u, 232f * u), Size(6f * u, 6f * u), style = Stroke(1f * u))
            drawLine(hierro, Offset(x, 232f * u), Offset(x, 238f * u), 1.2f * u)
            drawPath(Path().apply { moveTo(x, 237f * u); lineTo(x + 8f * u, 244f * u); lineTo(x - 8f * u, 244f * u); close() }, hierro)
            drawRect(mezcla(VIDRIO, FAROL, 0.55f), Offset(x - 6f * u, 244f * u), Size(12f * u, 16f * u))
            drawRect(hierro, Offset(x - 6f * u, 244f * u), Size(12f * u, 16f * u), style = Stroke(1f * u))
            llamaModerna(Offset(x, 258f * u), 8f * u, FAROL)
            drawPath(Path().apply { moveTo(x - 6f * u, 260f * u); lineTo(x + 6f * u, 260f * u); lineTo(x, 265f * u); close() }, hierro)
        }
    }

    /** La barra: una franja de papel de pliego algo más oscuro, con un filete y la cinta tricolor al pie. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, Color.Black, 0.18f)
            else mezcla(p.color, p.tenue, 0.12f).let { if (oscuro) mezcla(it, Color.Black, 0.24f) else it }
        drawRect(base)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, h))
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        drawLine((if (p.oscura) p.tenue else p.tinta).copy(alpha = 0.5f), Offset(0f, h - 6f * u), Offset(w, h - 6f * u), 0.7f * u)
        val tercio = w / 3
        listOf(c(AZUL), c(BLANCO), c(ROJO)).forEachIndexed { i, col -> drawRect(col, Offset(i * tercio, h - 4f * u), Size(tercio, 3f * u)) }
    }

    /** El botón de firmar: los tres anillos de la escarapela en una placa, rojo afuera, el filete blanco y el campo azul. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(1f * u, 2f * u, w - 1f * u, h - 3f * u)
        fun c(x: Color) = t(x, oscuro && !p.oscura, noche)
        val azul = c(AZUL)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1f * u, 1.6f * u), r.size, CornerRadius(3f * u))
        drawRoundRect(c(ROJO), r.topLeft, r.size, CornerRadius(3f * u))
        val b = r.deflate(2f * u)
        drawRoundRect(c(BLANCO), b.topLeft, b.size, CornerRadius(2f * u))
        val a = r.deflate(3.6f * u)
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(azul, Color.White, 0.12f), mezcla(azul, Color.Black, 0.2f)), a.top, a.bottom), a.topLeft, a.size, CornerRadius(1.5f * u))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = BLANCO

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 262.dp

    /** El papel azulado de la tapa del cahier, el gris del muro, el tapete verde, el peltre y el parqué. */
    private val TAPA_CAHIER = Color(0xFFB9C7D6)
    private val MURO_SECCION = Color(0xFFC9C6B8)
    private val TAPETE = Color(0xFF2E5A3A)
    private val PELTRE = Color(0xFF8E9296)
    private val PARQUE = Color(0xFF7A5434)
    private val CERA_VELA = Color(0xFFEFE6CC)

    /** El grosor del cahier, en dp: de 3 con una página a 16 con 240 o más. */
    private fun grosorCahier(hojas: Int): Float = 3f + 13f * kotlin.math.sqrt(hojas.coerceIn(0, 240) / 240f)

    /**
     * El cahier de momentos en la mesa de la sección: el muro con su zócalo de paneles, el cartel pegado (la banda tricolor
     * y sus renglones, formas y no letras) y el gorro frigio colgado de su clavija; la mesa con el tapete verde de flecos que
     * cae por delante y, sobre ella, la pila de registros donde se apoya el cahier, el tintero de peltre con su pluma, la
     * campanilla del presidente y el candelero. El cahier tiene la tapa de papel azulado con su marco impreso, el lomo con
     * la cinta azul cosida, la etiqueta con el nombre y la cuenta, y la escarapela prendida. Engorda con las páginas y le
     * asoman más papeles por la cabeza. Al abrirse, la tapa gira y se ve la página rayada con su margen. En oscuro, la vela
     * del candelero alumbra la sala.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = if (oscuro) mezcla(x, Color(0xFF0A0B0C), 0.6f) else x
        val a = e.abierto.coerceIn(0f, 1f)
        val g = grosorCahier(e.hojas) * u
        val hoja = papel(BLANCO, oscuro, false)
        // El muro con su zócalo de paneles.
        drawRect(c(MURO_SECCION))
        val zocalo = h * 0.5f
        drawRect(c(mezcla(MURO_SECCION, VERDE, 0.25f)), Offset(0f, zocalo), Size(w, h * 0.3f))
        drawRect(c(BLANCO), Offset(0f, zocalo - 3 * u), Size(w, 3 * u))
        var px = 8 * u
        while (px < w - 30 * u) {
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(px, zocalo + 8 * u), Size(40 * u, h * 0.3f - 14 * u), style = Stroke(1 * u))
            px += 50 * u
        }
        // El cartel pegado: la banda tricolor y sus renglones.
        val cartel = Rect(w * 0.03f, h * 0.06f, w * 0.2f, h * 0.42f)
        rotate(-2f, cartel.center) {
            drawRect(Color.Black.copy(alpha = 0.2f), cartel.topLeft + Offset(1.4f * u, 1.8f * u), cartel.size)
            drawRect(papel(PAPEL_VIEJO, oscuro, false).let { if (oscuro) mezcla(it, Color.Black, 0.35f) else it }, cartel.topLeft, cartel.size)
            val tercio = (cartel.width - 8 * u) / 3
            listOf(AZUL, BLANCO, ROJO).forEachIndexed { j, col -> drawRect(c(col), Offset(cartel.left + 4 * u + j * tercio, cartel.top + 4 * u), Size(tercio, 8 * u)) }
            var ry = cartel.top + 18 * u; var j = 0
            while (ry < cartel.bottom - 8 * u) {
                val largo = if (j == 0) cartel.width * 0.7f else cartel.width * (0.5f + 0.35f * azar(e.semilla, j))
                drawLine(c(TINTA).copy(alpha = if (j == 0) 0.8f else 0.5f), Offset(cartel.center.x - largo / 2 + 4 * u, ry), Offset(cartel.center.x + largo / 2 - 4 * u, ry), (if (j == 0) 2.4f else 1.1f) * u)
                ry += if (j == 0) 9 * u else 5 * u; j++
            }
        }
        // El gorro frigio colgado de su clavija, a la derecha.
        val clavija = Offset(w * 0.9f, h * 0.1f)
        drawCircle(c(PARQUE), 2.4f * u, clavija)
        val gorro = Path().apply {
            moveTo(clavija.x - 14 * u, clavija.y + 34 * u); lineTo(clavija.x + 14 * u, clavija.y + 34 * u)
            cubicTo(clavija.x + 16 * u, clavija.y + 12 * u, clavija.x + 6 * u, clavija.y, clavija.x - 6 * u, clavija.y + 2 * u)
            cubicTo(clavija.x - 16 * u, clavija.y + 4 * u, clavija.x - 14 * u, clavija.y + 16 * u, clavija.x - 6 * u, clavija.y + 14 * u)
            cubicTo(clavija.x - 14 * u, clavija.y + 20 * u, clavija.x - 15 * u, clavija.y + 28 * u, clavija.x - 14 * u, clavija.y + 34 * u)
            close()
        }
        drawPath(gorro, c(ROJO))
        drawPath(gorro, Color.Black.copy(alpha = 0.25f), style = Stroke(0.8f * u))
        drawRect(c(mezcla(ROJO, Color.Black, 0.2f)), Offset(clavija.x - 14 * u, clavija.y + 31 * u), Size(28 * u, 4 * u))
        escarapela(Offset(clavija.x + 9 * u, clavija.y + 26 * u), 5 * u, oscuro, false)
        // El parqué.
        drawRect(c(PARQUE), Offset(0f, h * 0.8f), Size(w, h * 0.2f))
        var qx = -20 * u; var qk = 0
        while (qx < w) {
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(qx, h * 0.8f), Offset(qx + (if (qk % 2 == 0) 14 else -14) * u, h), 1 * u)
            qx += 18 * u; qk++
        }

        // ------------------------------------------------ la mesa con el tapete verde
        val mesa = h * 0.72f
        listOf(w * 0.08f, w * 0.88f).forEach { x -> drawRect(c(PARQUE), Offset(x, mesa), Size(10 * u, h * 0.97f - mesa)) }
        val tapete = Path().apply {
            moveTo(w * 0.02f, mesa - 2 * u); lineTo(w * 0.98f, mesa - 2 * u); lineTo(w * 0.97f, h * 0.86f)
            var x = w * 0.97f
            while (x > w * 0.03f) { quadraticTo(x - 9 * u, h * 0.875f, x - 18 * u, h * 0.86f); x -= 18 * u }
            lineTo(w * 0.03f, h * 0.86f); close()
        }
        drawPath(tapete, c(TAPETE))
        var fx = w * 0.03f
        while (fx < w * 0.97f) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(fx, mesa), Offset(fx, h * 0.86f), 0.6f * u); fx += 18 * u }
        drawRect(c(Color(0xFFC9A04A)), Offset(w * 0.02f, mesa - 2 * u), Size(w * 0.96f, 1.6f * u))
        var ff = w * 0.04f
        while (ff < w * 0.96f) { drawLine(c(Color(0xFFC9A04A)), Offset(ff, h * 0.865f), Offset(ff, h * 0.89f), 1 * u); ff += 4 * u }

        // ------------------------------------------------ el candelero, a la izquierda
        val pie = Offset(w * 0.09f, mesa - 2 * u)
        drawOval(c(PELTRE), Offset(pie.x - 10 * u, pie.y - 4 * u), Size(20 * u, 5 * u))
        drawRect(c(PELTRE), Offset(pie.x - 2 * u, pie.y - 34 * u), Size(4 * u, 31 * u))
        drawOval(c(PELTRE), Offset(pie.x - 7 * u, pie.y - 36 * u), Size(14 * u, 4 * u))
        val vela = Rect(pie.x - 3.5f * u, pie.y - 64 * u, pie.x + 3.5f * u, pie.y - 35 * u)
        drawRect(if (oscuro) mezcla(CERA_VELA, Color.Black, 0.15f) else CERA_VELA, vela.topLeft, vela.size)
        drawRect(Color.Black.copy(alpha = 0.14f), Offset(vela.right - 2 * u, vela.top), Size(2 * u, vela.height))
        drawLine(TINTA, Offset(pie.x, vela.top), Offset(pie.x, vela.top - 2.4f * u), 0.9f * u)
        val llama = Offset(pie.x, vela.top - 1.6f * u)
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to FAROL.copy(alpha = 0.32f), 0.35f to FAROL.copy(alpha = 0.12f), 1f to Color.Transparent,
                center = llama, radius = w * 0.95f))
            haloModerna(llama - Offset(0f, 5 * u), 26 * u, FAROL, 0.7f)
            llamaModerna(llama, 11 * u, FAROL)
        }

        // ------------------------------------------------ la pila de registros y el cahier apoyado en ella
        val tapa = Rect(w * 0.27f, h * 0.16f + g * 0.5f, w * 0.73f - g, mesa - 2 * u)
        val pila = listOf(CUBIERTAS[2], CUBIERTAS[3], CUBIERTAS[0], CUBIERTAS[4])
        var yp = mesa - 2 * u
        pila.forEachIndexed { j, col ->
            val alto = (14 + 4 * (j % 2)) * u
            val r = Rect(w * (0.2f + 0.02f * (j % 2)), yp - alto, w * (0.82f - 0.015f * j), yp)
            drawRect(c(col), r.topLeft, r.size)
            drawRect(hoja.let { if (oscuro) mezcla(it, Color.Black, 0.3f) else it }, Offset(r.right - 4 * u, r.top + 2 * u), Size(3 * u, r.height - 4 * u))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.bottom - 1.5f * u), Size(r.width, 1.5f * u))
            yp -= alto
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + g + 4 * u, tapa.top - g * 0.5f + 4 * u), tapa.size)
        drawRect(c(mezcla(TAPA_CAHIER, Color.Black, 0.2f)), Offset(tapa.left + g, tapa.top - g * 0.5f), tapa.size)
        // Los papeles que asoman por la cabeza: uno más cada 30 páginas.
        val papeles = (e.hojas / 30).coerceIn(0, 6)
        for (j in 0 until papeles) {
            val sx0 = tapa.left + tapa.width * (0.12f + 0.75f * azar(e.semilla, 50 + j)) + g * 0.5f
            rotate(-7f + 14f * azar(e.semilla, 60 + j), Offset(sx0, tapa.top)) {
                val col = if (j % 3 == 2) c(PAPEL_VIEJO) else hoja
                drawRect(col, Offset(sx0 - 9 * u, tapa.top - g * 0.5f - (6 + 6 * azar(e.semilla, 70 + j)) * u), Size(18 * u, 14 * u))
                drawRect(c(TINTA).copy(alpha = 0.2f), Offset(sx0 - 9 * u, tapa.top - g * 0.5f - (6 + 6 * azar(e.semilla, 70 + j)) * u), Size(18 * u, 14 * u), style = Stroke(0.5f * u))
            }
        }
        val bloque = Path().apply {
            moveTo(tapa.left + 3 * u, tapa.top); lineTo(tapa.left + 3 * u + g, tapa.top - g * 0.5f + 1.5f * u)
            lineTo(tapa.right + g - 1.5f * u, tapa.top - g * 0.5f + 1.5f * u); lineTo(tapa.right + g - 1.5f * u, tapa.bottom - g * 0.5f - 1 * u)
            lineTo(tapa.right, tapa.bottom - 1 * u); lineTo(tapa.right, tapa.top); close()
        }
        drawPath(bloque, hoja)
        val capas = (g / (1.5f * u)).toInt().coerceAtLeast(2)
        for (j in 1..capas) {
            val f = j / (capas + 1f)
            drawLine(c(TINTA).copy(alpha = 0.14f), Offset(tapa.right + g * f, tapa.top - g * f * 0.5f), Offset(tapa.right + g * f, tapa.bottom - g * f * 0.5f), 0.5f * u)
        }
        // La página rayada con su margen rojo.
        if (a > 0.01f) {
            val pag = tapa.deflate(2 * u)
            drawRect(hoja, pag.topLeft, pag.size)
            drawRect(Color.Black.copy(alpha = 0.12f), pag.topLeft, Size(8 * u, pag.height))
            var ry = pag.top + 14 * u
            while (ry < pag.bottom - 4 * u) { drawLine(c(AZUL).copy(alpha = 0.25f), Offset(pag.left + 8 * u, ry), Offset(pag.right - 4 * u, ry), 0.5f * u); ry += 7 * u }
            drawLine(c(ROJO).copy(alpha = 0.6f), Offset(pag.left + 26 * u, pag.top), Offset(pag.left + 26 * u, pag.bottom), 0.7f * u)
            var ly = pag.top + 21 * u; var j = 0
            while (ly < pag.bottom - 10 * u) {
                var x = pag.left + 30 * u; var q = 0
                while (x < pag.right - 8 * u) {
                    val l = minOf((4f + 10f * azar(e.semilla + j, q)) * u, pag.right - 8 * u - x)
                    drawLine(c(TINTA).copy(alpha = 0.55f), Offset(x, ly - 1.5f * u), Offset(x + l, ly - 1.5f * u), 1.1f * u)
                    x += l + 2.4f * u; q++
                }
                ly += 7 * u; j++
            }
        }
        // La tapa de papel azulado con su marco impreso: gira sobre el lomo.
        val sx = 1f - 0.94f * a
        val cubierta = c(TAPA_CAHIER)
        scale(sx, 1f, Offset(tapa.left, tapa.center.y)) {
            drawRect(cubierta, tapa.topLeft, tapa.size)
            bisel(tapa, Color.White.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.2f), 1.2f * u)
            val marco = Rect(tapa.left + tapa.width * 0.12f, tapa.top + 8 * u, tapa.right - 8 * u, tapa.bottom - 8 * u)
            drawRect(c(AZUL).copy(alpha = 0.7f), marco.topLeft, marco.size, style = Stroke(1.2f * u))
            drawRect(c(AZUL).copy(alpha = 0.45f), marco.deflate(3 * u).topLeft, marco.deflate(3 * u).size, style = Stroke(0.6f * u))
            listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach {
                drawCircle(cubierta, 4 * u, it); drawCircle(c(AZUL).copy(alpha = 0.7f), 3 * u, it, style = Stroke(0.9f * u))
            }
            // El lomo: la cinta azul cosida con hilo blanco.
            val lomo = Rect(tapa.left, tapa.top, tapa.left + tapa.width * 0.08f, tapa.bottom)
            drawRect(c(AZUL), lomo.topLeft, lomo.size)
            var sy = lomo.top + 8 * u
            while (sy < lomo.bottom - 8 * u) { drawLine(c(BLANCO), Offset(lomo.center.x, sy), Offset(lomo.center.x, sy + 5 * u), 1.3f * u); sy += 12 * u }
            // La etiqueta con el nombre y la cuenta.
            val et = Rect(marco.left + 10 * u, marco.top + marco.height * 0.14f, marco.right - 10 * u, marco.top + marco.height * 0.56f)
            drawRect(hoja, et.topLeft, et.size)
            drawRect(c(AZUL), et.deflate(3 * u).topLeft, et.deflate(3 * u).size, style = Stroke(1 * u))
            val corte = et.top + et.height * 0.58f
            textoEn(medidor, e.nombre, Rect(et.left + 6 * u, et.top + 4 * u, et.right - 6 * u, corte), letraTapa, c(TINTA), 15f, peso = FontWeight.Bold, minimo = 7f)
            drawLine(c(ROJO).copy(alpha = 0.6f), Offset(et.left + et.width * 0.3f, corte), Offset(et.right - et.width * 0.3f, corte), 0.7f * u)
            textoEn(medidor, e.cuenta, Rect(et.left + 6 * u, corte, et.right - 6 * u, et.bottom - 4 * u), letra, c(TINTA).copy(alpha = 0.85f), 10f, minimo = 6f)
            // La escarapela prendida, abajo a la derecha.
            escarapela(Offset(marco.right - 18 * u, marco.bottom - 18 * u), 11 * u, oscuro, false)
            if (oscuro) drawCircle(Color.Black.copy(alpha = 0.3f), 11 * u, Offset(marco.right - 18 * u, marco.bottom - 18 * u))
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
        if (a > 0.01f) {
            val borde = tapa.left + tapa.width * sx
            for (j in 0 until 3) drawRect(Color.Black.copy(alpha = 0.1f * (3 - j) / 3f), Offset(borde, tapa.top), Size((3 + 4 * j) * u, tapa.height))
        }

        // ------------------------------------------------ el tintero de peltre, la pluma y la campanilla
        val tin = Offset(w * 0.86f, mesa - 2 * u)
        drawRect(c(PELTRE), Offset(tin.x - 13 * u, tin.y - 6 * u), Size(26 * u, 6 * u))
        drawRect(c(PELTRE), Offset(tin.x - 7 * u, tin.y - 16 * u), Size(14 * u, 10 * u))
        drawOval(c(mezcla(PELTRE, Color.White, 0.2f)), Offset(tin.x - 8 * u, tin.y - 18 * u), Size(16 * u, 4 * u))
        drawOval(c(TINTA), Offset(tin.x - 3 * u, tin.y - 17.5f * u), Size(6 * u, 2.4f * u))
        val canon = Offset(tin.x + 1 * u, tin.y - 17 * u)
        val punta = Offset(tin.x + 16 * u, tin.y - 66 * u)
        drawPath(Path().apply {
            moveTo(canon.x, canon.y); quadraticTo(punta.x - 12 * u, punta.y + 26 * u, punta.x, punta.y)
            quadraticTo(punta.x + 3 * u, punta.y + 26 * u, canon.x + 2 * u, canon.y - 12 * u); close()
        }, c(BLANCO))
        drawLine(c(PELTRE), canon, punta + Offset(-1 * u, 6 * u), 0.7f * u)
        val camp = Offset(w * 0.79f, mesa - 2 * u)
        drawPath(Path().apply {
            moveTo(camp.x - 9 * u, camp.y); quadraticTo(camp.x - 8 * u, camp.y - 16 * u, camp.x, camp.y - 17 * u)
            quadraticTo(camp.x + 8 * u, camp.y - 16 * u, camp.x + 9 * u, camp.y); close()
        }, c(Color(0xFFC9A04A)))
        drawRect(c(PARQUE), Offset(camp.x - 1.6f * u, camp.y - 27 * u), Size(3.2f * u, 10 * u))
        drawCircle(c(PARQUE), 3 * u, Offset(camp.x, camp.y - 28 * u))
    }

    /**
     * La pestaña de un día: un boleto de la sección con la banda tricolor arriba (la letra de la semana en el blanco), el
     * día en la letra del tema y, abajo, una escarapela del color del ánimo con su centro blanco (sin ánimo, un círculo de
     * puntos).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val giro = -1.5f + 3f * azar(e.semilla, 1)
        rotate(giro, Offset(w / 2, h / 2)) {
            val r = Rect(4 * u, 2 * u, w - 4 * u, h - 2 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1.2f * u, 1.6f * u), r.size)
            drawRect(papel(PAPEL, oscuro, false), r.topLeft, r.size)
            val tercio = r.width / 3
            listOf(AZUL, BLANCO, ROJO).forEachIndexed { j, col -> drawRect(c(col), Offset(r.left + j * tercio, r.top), Size(tercio, 12 * u)) }
            textoEn(medidor, e.semana, Rect(r.left + tercio, r.top, r.left + 2 * tercio, r.top + 12 * u), letra, c(TINTA), 8.5f, peso = FontWeight.Bold)
            textoEn(medidor, e.dia, Rect(r.left, r.top + 13 * u, r.right, r.bottom - 14 * u), letraTapa, c(TINTA), 18f, peso = FontWeight.Bold, minimo = 10f)
            val o = Offset(w / 2, r.bottom - 9 * u)
            if (e.tono != null) {
                drawCircle(e.tono, 7 * u, o)
                drawCircle(c(BLANCO), 2.8f * u, o)
                for (j in 0 until 8) {
                    val ang = Math.toRadians(j * 45.0)
                    drawLine(Color.Black.copy(alpha = 0.18f), Offset(o.x + 3 * u * kotlin.math.cos(ang).toFloat(), o.y + 3 * u * kotlin.math.sin(ang).toFloat()), Offset(o.x + 7 * u * kotlin.math.cos(ang).toFloat(), o.y + 7 * u * kotlin.math.sin(ang).toFloat()), 0.5f * u)
                }
            } else {
                drawCircle(c(TINTA).copy(alpha = 0.4f), 4.5f * u, o, style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * u, 1.5f * u))))
            }
            drawRect(c(TINTA).copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(0.6f * u))
        }
    }

    /**
     * El rótulo de un mes: una tira de cartel pegada, con una banda tricolor en cada punta, un filete negro arriba y abajo
     * y el mes en la letra del tema.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val tira = Rect(2 * u, 3 * u, w - 2 * u, h - 3 * u)
        drawRect(Color.Black.copy(alpha = 0.22f), tira.topLeft + Offset(1.2f * u, 1.6f * u), tira.size)
        drawRect(papel(PAPEL, oscuro, false), tira.topLeft, tira.size)
        listOf(tira.left + 4 * u, tira.right - 4 * u - 24 * u).forEach { x ->
            listOf(AZUL, BLANCO, ROJO).forEachIndexed { j, col -> drawRect(c(col), Offset(x + j * 8 * u, tira.top + 4 * u), Size(8 * u, tira.height - 8 * u)) }
            drawRect(c(TINTA).copy(alpha = 0.3f), Offset(x, tira.top + 4 * u), Size(24 * u, tira.height - 8 * u), style = Stroke(0.6f * u))
        }
        drawLine(c(TINTA), Offset(tira.left + 32 * u, tira.top + 3 * u), Offset(tira.right - 32 * u, tira.top + 3 * u), 1.2f * u)
        drawLine(c(TINTA), Offset(tira.left + 32 * u, tira.bottom - 3 * u), Offset(tira.right - 32 * u, tira.bottom - 3 * u), 1.2f * u)
        textoEn(medidor, texto, Rect(tira.left + 34 * u, tira.top + 4 * u, tira.right - 34 * u, tira.bottom - 4 * u), letraTapa, c(TINTA), 15f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El papel de cartel de la carta, el lacre rojo bajo la escarapela, el cordón azul y el esmalte de las placas. */
    private val PAPEL_CARTEL = Color(0xFFF2ECDD)
    private val LACRE_ROJO = Color(0xFFB22234)
    private val CORDON_AZUL = Color(0xFF2F4A7A)
    private val ESMALTE = Color(0xFF1F3A6E)

    /** La escarapela tricolor en [o], de radio [r]: el anillo azul, el blanco y el rojo, plisados. */
    private fun DrawScope.escarapelaCarta(o: Offset, r: Float, oscuro: Boolean) {
        fun t(x: Color) = if (oscuro) mezcla(x, Color.Black, 0.22f) else x
        listOf(t(AZUL) to 1f, t(BLANCO) to 0.66f, t(ROJO) to 0.36f).forEach { (col, f) ->
            drawCircle(col, r * f, o)
            for (j in 0 until 18) drawLine(mezcla(col, Color.Black, 0.25f), polarModerna(o, r * f * 0.72f, j * 20f), polarModerna(o, r * f * 0.98f, j * 20f), r * 0.025f)
        }
        drawCircle(t(mezcla(ROJO, Color.Black, 0.3f)), r * 0.12f, o)
        drawCircle(Color.Black.copy(alpha = 0.25f), r, o, style = Stroke(r * 0.05f))
    }

    /** El sello de la escarapela en [o], de radio [r]: el disco de lacre rojo y la escarapela prendida encima. */
    private fun DrawScope.selloEscarapela(o: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        val lacre = if (oscuro) mezcla(LACRE_ROJO, Color.Black, 0.25f) else LACRE_ROJO
        translate(r * 0.06f, r * 0.12f) { drawPath(gotaModerna(o, r, semilla + 13), Color.Black.copy(alpha = 0.3f)) }
        drawPath(gotaModerna(o, r, semilla + 13), lacre)
        escarapelaCarta(o, r * 0.78f, oscuro)
    }

    /** El matasellos redondo de la estafeta, de radio [r] en [o]: dos círculos, una estrella y unas rayas (formas, no letras). */
    private fun DrawScope.matasellos(o: Offset, r: Float, color: Color) {
        val u = density
        drawCircle(color, r, o, style = Stroke(1 * u))
        drawCircle(color, r * 0.7f, o, style = Stroke(0.7f * u))
        drawPath(estrella(o, r * 0.32f, 5, 0.45f), color)
        for (j in 0 until 3) drawLine(color, Offset(o.x + r * 1.15f, o.y - r * 0.4f + j * r * 0.4f), Offset(o.x + r * 2.6f, o.y - r * 0.4f + j * r * 0.4f), 0.8f * u)
    }

    /**
     * La carta va en un pliego de papel de cartel con la franja tricolor impresa en el borde y el matasellos de la
     * estafeta, con la lengua (la solapa redonda) forrada de azul. La cierra la escarapela tricolor prendida sobre un disco
     * de lacre rojo, con sus dos cintas; las selladas al futuro van además atadas con el cordón azul, anudado a un lado.
     * A quién va y las fechas, a pluma entre dos filetes. Al abrirse el lacre se parte (media escarapela se va con la
     * lengua), la lengua se levanta y sale la carta. En oscuro el papel se hunde y la letra pasa a crema.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val c = e.cuerpo; val W = c.width; val H = c.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0B0D0C), 0.6f) else x
        val tinta = if (oscuro) Color(0xFFF0EADA) else TINTA
        val p = Rect(c.left + W * 0.05f, c.top + H * 0.08f, c.right - W * 0.05f, c.bottom - H * 0.07f)
        val cx = p.center.x
        val largo = p.height * 0.38f
        val giro = kotlin.math.cos(Math.PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val punta = Offset(cx, p.top + largo)
        val rs = minOf(W, H) * 0.11f
        val partido = e.romper.coerceIn(0f, 1f)
        fun lengua(g: Float) = Path().apply {
            moveTo(p.left, p.top)
            cubicTo(p.left + p.width * 0.12f, p.top + g * largo * 0.5f, cx - p.width * 0.22f, p.top + g * largo, cx, p.top + g * largo)
            cubicTo(cx + p.width * 0.22f, p.top + g * largo, p.right - p.width * 0.12f, p.top + g * largo * 0.5f, p.right, p.top)
            close()
        }
        fun DrawScope.selloEn(g: Float, mitad: Int) {
            val o = Offset(cx, p.top + g * largo)
            conAlfaModerna(e.alfaSello.coerceIn(0f, 1f), Rect(punta, rs * 4f * e.escalaSello)) {
                scale(e.escalaSello, punta) {
                    if (mitad == 0) {
                        // Las dos cintas, azul y roja, que caen de la escarapela.
                        listOf(-1f to k(AZUL), 1f to k(ROJO)).forEach { (s, cinta) ->
                            drawPath(Path().apply {
                                moveTo(o.x, o.y); lineTo(o.x + s * rs * 0.55f, o.y + rs * 1.45f); lineTo(o.x + s * rs * 0.3f, o.y + rs * 1.3f)
                                lineTo(o.x + s * rs * 0.08f, o.y + rs * 1.42f); close()
                            }, cinta)
                        }
                        if (e.brillo > 0f) {
                            drawCircle(Brush.radialGradient(0f to FAROL.copy(alpha = 0.85f * e.brillo), 0.45f to FAROL.copy(alpha = 0.25f * e.brillo),
                                1f to Color.Transparent, center = o, radius = rs * 2.8f), rs * 2.8f, o)
                            rayosModerna(o, rs * 1.25f, rs * 2.3f, Color(0xFFE0B54A).copy(alpha = 0.95f * e.brillo), 12, 1.1f)
                        }
                    }
                    when {
                        partido <= 0f -> selloEscarapela(o, rs, oscuro, e.semilla)
                        mitad == 1 -> clipPath(mitadSelloModerna(o, rs, true, e.semilla)) {
                            translate(0f, -partido * rs * 0.18f) { rotate(-8f * partido, o) { selloEscarapela(o, rs, oscuro, e.semilla) } }
                        }
                        else -> clipPath(mitadSelloModerna(o, rs, false, e.semilla)) {
                            translate(0f, partido * rs * 0.1f) { rotate(6f * partido, o) { selloEscarapela(o, rs, oscuro, e.semilla) } }
                        }
                    }
                }
            }
        }

        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(p.left + 2 * u, p.top + 3 * u), p.size)
        if (giro < 0f) {
            drawPath(lengua(giro), k(AZUL))
            drawPath(lengua(giro), k(mezcla(AZUL, Color.Black, 0.4f)), style = Stroke(0.8f * u))
            if (partido > 0f) selloEn(giro, 1)
        }
        val sale = salidaModerna(e.subir)
        if (sale > 0f) {
            val sube = sale * (p.top + 3 * u - 4 * u)
            hojaCartaModerna(Rect(cx - p.width * 0.42f, p.top + 3 * u - sube, cx + p.width * 0.42f, p.bottom - 6 * u - sube), e, oscuro)
        }
        // El pliego, con la franja tricolor impresa en el borde izquierdo y el matasellos.
        drawRect(k(PAPEL_CARTEL), p.topLeft, p.size)
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(k(col), Offset(p.left + i * W * 0.022f, p.top), Size(W * 0.022f, p.height)) }
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(p.left, p.top), Size(W * 0.066f, p.height), style = Stroke(0.5f * u))
        matasellos(Offset(p.right - p.width * 0.2f, p.top + largo * 0.62f), minOf(W, H) * 0.075f, k(ROJO).copy(alpha = 0.5f))
        drawRect(k(Color(0xFF8A8270)).copy(alpha = 0.6f), p.topLeft, p.size, style = Stroke(0.8f * u))
        // Lo escrito, a pluma entre dos filetes.
        val texto = Rect(p.left + W * 0.09f, punta.y + rs * 1.55f, p.right - 7 * u, p.bottom - 4 * u)
        if (e.linea1.isNotEmpty() || e.linea2.isNotEmpty()) {
            val m = texto.top + texto.height * 0.56f
            drawLine(k(AZUL).copy(alpha = 0.6f), Offset(texto.left, texto.top), Offset(texto.right, texto.top), 0.7f * u)
            drawLine(k(ROJO).copy(alpha = 0.5f), Offset(texto.left, texto.bottom), Offset(texto.right, texto.bottom), 0.7f * u)
            textoEn(medidor, e.linea1, Rect(texto.left, texto.top + 1 * u, texto.right, m), letra, tinta, 12f, peso = FontWeight.SemiBold, minimo = 7.5f)
            textoEn(medidor, e.linea2, Rect(texto.left, m - 1 * u, texto.right, texto.bottom - 1 * u), letra, if (oscuro) tinta.copy(alpha = 0.85f) else ROJO, 9f, minimo = 6.5f)
        }
        if (giro >= 0f) {
            translate(0f, 2 * u) { drawPath(lengua(giro), Color.Black.copy(alpha = 0.22f)) }
            drawPath(lengua(giro), k(Color(0xFFF7F2E6)))
            drawPath(lengua(giro), k(Color(0xFF8A8270)), style = Stroke(0.9f * u))
        }
        // El cordón azul, de lado a lado bajo la escarapela, con su nudo a la derecha.
        val ata = e.cordon.coerceIn(0f, 1f)
        if (ata > 0f) {
            val tramo = (p.width + 4 * u) * ata
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(p.left - 2 * u, punta.y + 1.2f * u), Offset(p.left - 2 * u + tramo, punta.y + 1.2f * u), 2 * u)
            drawLine(k(CORDON_AZUL), Offset(p.left - 2 * u, punta.y), Offset(p.left - 2 * u + tramo, punta.y), 2 * u)
            if (ata > 0.85f) {
                val f = (ata - 0.85f) / 0.15f
                val o = Offset(p.right - p.width * 0.14f, punta.y)
                drawOval(k(CORDON_AZUL), Offset(o.x - rs * 0.5f * f, o.y - rs * 0.55f * f), Size(rs * 0.5f * f, rs * 0.45f * f), style = Stroke(1.6f * u))
                drawOval(k(CORDON_AZUL), Offset(o.x, o.y - rs * 0.55f * f), Size(rs * 0.5f * f, rs * 0.45f * f), style = Stroke(1.6f * u))
                drawLine(k(CORDON_AZUL), o, o + Offset(-rs * 0.25f * f, rs * 0.9f * f), 1.6f * u, StrokeCap.Round)
                drawLine(k(CORDON_AZUL), o, o + Offset(rs * 0.35f * f, rs * 0.8f * f), 1.6f * u, StrokeCap.Round)
            }
        }
        if (partido <= 0f) selloEn(maxOf(giro, 0f), 0)
        else {
            selloEn(1f, 0)
            if (giro >= 0f) selloEn(giro, 1)
        }
    }

    override val cajaPrevia = Rect(0.165f, 0.08f, 0.835f, 0.25f)

    override val altoBuzon: Dp = 208.dp

    /**
     * El buzón es la caja de cartas de la sección, clavada en el muro de sillares: un cajón de madera pintado de azul con
     * la boca de latón arriba, la franja tricolor y una ventana de alambre por donde se ven las cartas de pie: las que ya se
     * pueden abrir con la escarapela encendida, las selladas con el cordón azul, las abiertas con la lengua levantada y la
     * hoja fuera. Abajo, el candado. A la derecha, tres placas de esmalte azul como las de las calles, con la cuenta en
     * blanco. En oscuro, el farol de la esquina alumbra desde arriba.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0B0D0C), 0.58f) else x
        // El muro de sillares y la acera.
        drawRect(k(PIEDRA))
        var y = 0f; var f = 0
        while (y < h * 0.93f) {
            drawLine(k(PIEDRA_JUNTA), Offset(0f, y), Offset(w, y), 1 * u)
            var x = if (f % 2 == 0) 0f else 30 * u
            while (x < w) { drawLine(k(PIEDRA_JUNTA), Offset(x, y), Offset(x, y + 24 * u), 1 * u); x += 60 * u }
            y += 24 * u; f++
        }
        drawRect(k(ADOQUIN), Offset(0f, h * 0.93f), Size(w, h * 0.07f))
        // Un cartel viejo, medio arrancado, a la izquierda de la caja (formas, no letras).
        val cartel = Rect(w * 0.005f, h * 0.18f, w * 0.06f, h * 0.62f)
        drawPath(contornoIrregular(cartel, 5, 16, 0.08f), k(PAPEL_VIEJO))
        for (j in 0 until 6) drawLine(k(TINTA).copy(alpha = 0.3f), Offset(cartel.left + 3 * u, cartel.top + 8 * u + j * 9 * u), Offset(cartel.right - 4 * u, cartel.top + 8 * u + j * 9 * u), 1.2f * u)
        // El farol de la esquina, arriba a la derecha.
        val farolO = Offset(w * 0.97f, h * 0.05f)
        if (oscuro) drawRect(Brush.radialGradient(0f to FAROL.copy(alpha = 0.34f), 0.45f to FAROL.copy(alpha = 0.12f), 1f to Color.Transparent,
            center = farolO, radius = w * 0.9f))
        drawLine(k(HIERRO), Offset(w, farolO.y - 6 * u), Offset(farolO.x - 2 * u, farolO.y - 6 * u), 2 * u)
        drawPath(Path().apply { moveTo(farolO.x - 7 * u, farolO.y - 5 * u); lineTo(farolO.x + 7 * u, farolO.y - 5 * u); lineTo(farolO.x + 4 * u, farolO.y + 9 * u); lineTo(farolO.x - 4 * u, farolO.y + 9 * u); close() },
            if (oscuro) FAROL else k(VIDRIO))
        drawPath(Path().apply { moveTo(farolO.x - 7 * u, farolO.y - 5 * u); lineTo(farolO.x + 7 * u, farolO.y - 5 * u); lineTo(farolO.x + 4 * u, farolO.y + 9 * u); lineTo(farolO.x - 4 * u, farolO.y + 9 * u); close() },
            k(HIERRO), style = Stroke(1.2f * u))
        if (oscuro) haloModerna(farolO + Offset(0f, 2 * u), 20 * u, FAROL, 0.6f)

        // ------------------------------------------------ la caja de cartas
        val caja = Rect(w * 0.07f, h * 0.06f, w * 0.64f, h * 0.9f)
        drawRect(Color.Black.copy(alpha = 0.3f), caja.topLeft + Offset(3 * u, 4 * u), caja.size)
        drawRect(k(ESMALTE), caja.topLeft, caja.size)
        vetas(caja, Color.Black.copy(alpha = 0.18f), 5, densidad = 0.4f, vertical = true, grosor = 0.6f)
        bisel(caja, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.35f), 2 * u)
        // La boca de latón, arriba.
        val boca = Rect(caja.left + caja.width * 0.18f, caja.top + 8 * u, caja.right - caja.width * 0.18f, caja.top + 22 * u)
        drawRoundRect(k(Color(0xFFC9A24E)), boca.topLeft, boca.size, CornerRadius(3 * u))
        drawRoundRect(k(Color(0xFF7E6230)), boca.topLeft, boca.size, CornerRadius(3 * u), style = Stroke(1 * u))
        drawRoundRect(Color.Black, Offset(boca.left + 6 * u, boca.center.y - 2 * u), Size(boca.width - 12 * u, 4 * u), CornerRadius(2 * u))
        listOf(boca.left + 3 * u, boca.right - 3 * u).forEach { drawCircle(k(Color(0xFF7E6230)), 1.2f * u, Offset(it, boca.center.y)) }
        // La ventana de alambre, con las cartas de pie dentro.
        val ventana = Rect(caja.left + 10 * u, boca.bottom + 8 * u, caja.right - 10 * u, caja.top + caja.height * 0.72f)
        drawRect(k(Color(0xFF1A1E24)), ventana.topLeft, ventana.size)
        val cartas = repartoModerna(e, 10)
        if (cartas.isNotEmpty()) clipRect(ventana.left, ventana.top, ventana.right, ventana.bottom) {
            val paso = ventana.width / cartas.size
            val ancho = minOf(paso * 1.45f, 40 * u)
            cartas.forEachIndexed { i, clase ->
                val x = ventana.left + paso * (i + 0.5f)
                val alto = ventana.height * 0.74f
                val r = Rect(x - ancho / 2, ventana.bottom - alto + 4 * u, x + ancho / 2, ventana.bottom + 4 * u)
                rotate((azar(i, 101) - 0.5f) * 12f, Offset(x, ventana.bottom)) {
                    drawRect(Color.Black.copy(alpha = 0.35f), r.topLeft + Offset(2 * u, 0f), r.size)
                    if (clase == 2) {
                        drawPath(Path().apply { moveTo(r.left, r.top); quadraticTo(x, r.top - ancho * 0.6f, r.right, r.top); close() }, k(AZUL))
                        drawRect(k(PAPEL), Offset(r.left + 3 * u, r.top - ancho * 0.45f), Size(r.width - 6 * u, r.height * 0.5f))
                        for (j in 0 until 3) drawLine(k(TINTA).copy(alpha = 0.5f), Offset(r.left + 5 * u, r.top - ancho * 0.3f + j * 3.4f * u), Offset(r.right - 5 * u, r.top - ancho * 0.3f + j * 3.4f * u), 0.6f * u)
                    }
                    drawRect(k(PAPEL_CARTEL), r.topLeft, r.size)
                    listOf(AZUL, BLANCO, ROJO).forEachIndexed { j, col -> drawRect(k(col), Offset(r.left + j * 2 * u, r.top), Size(2 * u, r.height)) }
                    if (clase != 2) {
                        val o = Offset(x, r.top + ancho * 0.55f)
                        drawPath(Path().apply { moveTo(r.left, r.top); quadraticTo(x - ancho * 0.3f, o.y, x, o.y); quadraticTo(x + ancho * 0.3f, o.y, r.right, r.top); close() }, k(Color(0xFFF7F2E6)))
                        drawPath(Path().apply { moveTo(r.left, r.top); quadraticTo(x - ancho * 0.3f, o.y, x, o.y); quadraticTo(x + ancho * 0.3f, o.y, r.right, r.top) }, k(Color(0xFF8A8270)), style = Stroke(0.7f * u))
                        if (clase == 1) drawLine(k(CORDON_AZUL), Offset(r.left, o.y), Offset(r.right, o.y), 1.6f * u)
                        else {
                            haloModerna(o, ancho * 0.85f, FAROL, if (oscuro) 1f else 0.85f)
                            rayosModerna(o, ancho * 0.26f, ancho * 0.55f, Color(0xFFE0B54A), 8, 0.9f)
                        }
                        escarapelaCarta(o, ancho * 0.2f, oscuro)
                    }
                }
            }
        }
        // La rejilla de alambre por delante.
        var gx = ventana.left
        while (gx <= ventana.right) { drawLine(k(HIERRO_LUZ).copy(alpha = 0.7f), Offset(gx, ventana.top), Offset(gx, ventana.bottom), 0.7f * u); gx += 7 * u }
        var gy = ventana.top
        while (gy <= ventana.bottom) { drawLine(k(HIERRO_LUZ).copy(alpha = 0.7f), Offset(ventana.left, gy), Offset(ventana.right, gy), 0.7f * u); gy += 7 * u }
        drawRect(k(HIERRO), ventana.topLeft, ventana.size, style = Stroke(2.4f * u))
        // La franja tricolor pintada y el candado.
        val franja = Rect(caja.left + 2 * u, ventana.bottom + 7 * u, caja.right - 2 * u, ventana.bottom + 17 * u)
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(k(col), Offset(franja.left + franja.width * i / 3f, franja.top), Size(franja.width / 3f, franja.height)) }
        val candado = Offset(caja.center.x, caja.bottom - 15 * u)
        drawArc(k(HIERRO_LUZ), 180f, 180f, false, Offset(candado.x - 5 * u, candado.y - 11 * u), Size(10 * u, 10 * u), style = Stroke(2 * u))
        drawRoundRect(k(Color(0xFFC9A24E)), Offset(candado.x - 7 * u, candado.y - 6 * u), Size(14 * u, 12 * u), CornerRadius(2 * u))
        drawCircle(Color.Black, 1.5f * u, Offset(candado.x, candado.y - 1 * u))
        // Las tres placas de esmalte, como las de las calles.
        val rotulos = listOf(e.listas to "por abrir", e.selladas to "selladas", e.abiertas to "abiertas")
        rotulos.forEachIndexed { i, (n, nombre) ->
            val r = Rect(w * 0.68f, h * (0.14f + 0.26f * i), w * 0.96f, h * (0.14f + 0.26f * i) + h * 0.21f)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(2 * u, 2.5f * u), r.size, CornerRadius(4 * u))
            drawRoundRect(if (oscuro) mezcla(ESMALTE, Color(0xFF0B0D0C), 0.2f) else ESMALTE, r.topLeft, r.size, CornerRadius(4 * u))
            drawRoundRect(Color(0xFF3E6A4E), r.topLeft, r.size, CornerRadius(4 * u), style = Stroke(2 * u))
            drawRoundRect(BLANCO, r.topLeft + Offset(3.5f * u, 3.5f * u), Size(r.width - 7 * u, r.height - 7 * u), CornerRadius(2.5f * u), style = Stroke(1 * u))
            val m = r.left + r.width * 0.36f
            textoEn(medidor, "$n", Rect(r.left + 5 * u, r.top + 2 * u, m, r.bottom - 2 * u), letra, BLANCO, 18f, peso = FontWeight.Bold)
            textoEn(medidor, nombre, Rect(m, r.top + 3 * u, r.right - 6 * u, r.bottom - 3 * u), letra, BLANCO, 10f, minimo = 7f)
        }
    }
}

// =====================================================================
//  Ayudantes de la página abierta (Edad Moderna)
// =====================================================================

/**
 * La fecha del encabezado dentro de [r]: en un renglón si cabe (o casi: [textoEn] la achica un poco); si es larga y tiene
 * « · », en dos renglones del mismo tamaño (el que haga falta para que quepa el más largo), partidos por el « · » que los deje más parejos.
 */
private fun DrawScope.fechaModerna(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
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
private fun rotuloModerna(p: Pagina, noche: Boolean): Color = if (noche) mezcla(p.acento, p.tinta, 0.35f) else p.acento

/**
 * El modo oscuro sobre un papel claro: la penumbra con la luz del tema. Todo el papel baja un 18 %; desde el centro del
 * texto hacia las orillas baja hasta un 62 % más; y la luz, desde [fuente], tiñe y aclara su rincón con su color [luz].
 * En el centro queda como papel algo atenuado (en promedio, como un velo de un 30 %) y la tinta oscura se sigue leyendo.
 * Son los dos degradados de la página.
 */
private fun DrawScope.penumbraModerna(fuente: Offset, luz: Color, fuerza: Float = 1f) {
    val w = size.width; val h = size.height
    drawRect(Color.Black.copy(alpha = 0.18f))
    drawRect(Brush.radialGradient(0f to Color.Transparent, 0.42f to Color.Black.copy(alpha = 0.05f), 1f to Color.Black.copy(alpha = 0.62f),
        center = Offset(w / 2, h * 0.4f), radius = kotlin.math.hypot(w, h) * 0.6f))
    drawRect(Brush.radialGradient(0f to luz.copy(alpha = 0.3f * fuerza), 0.3f to luz.copy(alpha = 0.12f * fuerza), 1f to Color.Transparent,
        center = fuente, radius = maxOf(w, h) * 0.6f))
}

/** Un resplandor sin degradado: discos concéntricos, cada uno más chico, que se suman hacia el centro. */
private fun DrawScope.haloModerna(o: Offset, r: Float, color: Color, fuerza: Float = 0.5f) {
    for (k in 5 downTo 1) drawCircle(color.copy(alpha = fuerza * 0.14f), r * k / 5f, o)
}

/** Una llama de [s] de alto con la base en [o], con su corazón claro. */
private fun DrawScope.llamaModerna(o: Offset, s: Float, color: Color) {
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
private fun mediaLunaModerna(o: Offset, r: Float): Path {
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
private fun salidaModerna(subir: Float): Float {
    val x = (subir * 2f).coerceIn(0f, 1f)
    return 1f - (1f - x) * (1f - x)
}

/** El punto a [r] de [o] en la dirección [grados] (0 a la derecha, 90 abajo). */
private fun polarModerna(o: Offset, r: Float, grados: Float): Offset {
    val a = Math.toRadians(grados.toDouble())
    return Offset(o.x + r * kotlin.math.cos(a).toFloat(), o.y + r * kotlin.math.sin(a).toFloat())
}

/** Una gota de cera de radio [r] en [o], con el borde un poco irregular (siempre el mismo para la misma [semilla]). */
private fun gotaModerna(o: Offset, r: Float, semilla: Int, puntos: Int = 22): Path = Path().apply {
    for (j in 0..puntos) {
        val p = polarModerna(o, r * (0.93f + 0.12f * azar(semilla, j % puntos)), j * 360f / puntos)
        if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/**
 * La raja de un sello que se parte, de lado a lado de [o] (radio [r]): devuelve la mitad de arriba ([arriba]) o la de abajo
 * como región de recorte, con el filo en zigzag.
 */
private fun mitadSelloModerna(o: Offset, r: Float, arriba: Boolean, semilla: Int): Path = Path().apply {
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
private fun DrawScope.hojaCartaModerna(r: Rect, e: EstadoCarta, oscuro: Boolean, barbas: Boolean = false, arriba: Float = 8f, margen: Float = 7f) {
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
private fun repartoModerna(e: EstadoBuzon, maximo: Int): List<Int> {
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
private fun DrawScope.rayosModerna(o: Offset, r0: Float, r1: Float, color: Color, n: Int = 8, grosor: Float = 1f) {
    for (j in 0 until n) {
        val g = j * 360f / n + 360f / n / 2
        drawLine(color, polarModerna(o, r0, g), polarModerna(o, if (j % 2 == 0) r1 else r0 + (r1 - r0) * 0.6f, g), grosor * density, StrokeCap.Round)
    }
}

/** Dibuja [bloque] con opacidad [alfa], en una capa aparte (para que no se transparente pieza por pieza). */
private inline fun DrawScope.conAlfaModerna(alfa: Float, limite: Rect, bloque: DrawScope.() -> Unit) {
    if (alfa >= 0.999f) { bloque(); return }
    drawContext.canvas.saveLayer(limite, Paint().apply { this.alpha = alfa })
    bloque()
    drawContext.canvas.restore()
}
