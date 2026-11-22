package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

/**
 * La pared pintada del cuarto de los oficiales, la ventana chica de espejuelo arriba a la izquierda y el suelo de ladrillo en
 * espiga del estante de Romano ([MuebleEstanteRomano]).
 */
internal object MuroEstanteRomano {
    private val P = PaletaRomano

    /** El hueco de la ventana (lo de fuera) y el derrame abocinado en el grueso del muro, en anchos del mueble. */
    private val HUECO = Rect(0.115f, 0.1f, 0.2f, 0.245f)
    private val DERRAME = Rect(0.075f, 0.068f, 0.24f, 0.278f)

    /**
     * La pared al temple: el paño de ocre amarillo (de día más claro hacia la ventana; de noche, encendido por la linterna y
     * hundido lejos de ella), la faja de cinabrio de arriba con su línea blanca, los paños con su filete rojo y su línea fina de
     * cal, las pinceladas, dos desconchones que dejan ver el ladrillo, las grietas y el tizne sobre la linterna.
     */
    fun DrawScope.muro(a: AireRomano, apoyo: Float, suelo: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val llama = Offset(w * MuebleEstanteRomano.LAMP_X, w * MuebleEstanteRomano.LAMP_Y)
        // El paño de ocre.
        if (!a.oscuro) {
            val base = a.luz(P.OCRE, 0.3f)
            drawRect(Brush.horizontalGradient(listOf(a.luz(mezcla(P.OCRE, P.OCRE_CLARO, 0.55f), 0.3f), base, a.luz(mezcla(P.OCRE, P.OCRE_OSC, 0.4f), 0.3f)), 0f, w),
                Offset.Zero, Size(w, suelo))
            if (!a.noche) mancha(Offset(w * 0.17f, w * 0.2f), w * 0.62f, P.OCRE_CLARO.copy(alpha = 0.55f))
            else {
                // Al anochecer: lo lejos de la ventana, en sombra violeta; junto a ella, el naranja bajo que entra.
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, P.ANOCHECER.copy(alpha = 0.3f)), w * 0.3f, w), Offset.Zero, Size(w, suelo))
                mancha(Offset(w * 0.16f, w * 0.34f), w * 0.48f, P.CREPUSCULO.copy(alpha = 0.6f))
            }
        } else {
            fun k(dd: Float) = a.luz(P.OCRE, MuebleEstanteRomano.alcance(dd * 1.9f, 0f))
            drawRect(Brush.radialGradient(0f to k(0.03f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.8f), 1f to k(1.3f),
                center = llama, radius = w * 1.3f), Offset.Zero, Size(w, suelo))
            // La luna que entra por el espejuelo apenas aclara el rincón de la ventana.
            mancha(Offset(w * 0.17f, w * 0.26f), w * 0.3f, P.LUNA_RAS.copy(alpha = if (a.noche) 0.14f else 0.08f))
        }
        val pano = Rect(0f, 0f, w, apoyo)
        pinceladas(pano, if (a.oscuro) a.hondo else P.OCRE_OSC, 5101, 40, u, if (a.oscuro) 0.1f else 0.1f)
        pinceladas(pano, if (a.oscuro) a.hondo else P.OCRE_CLARO, 5102, 22, u, if (a.oscuro) 0.05f else 0.12f)
        // La faja de cinabrio de arriba y su línea blanca.
        val faja = w * 0.034f
        val tf = cerca(Offset(w * 0.6f, faja / 2))
        drawRect(a.luz(P.CINABRIO, tf), Offset.Zero, Size(w, faja))
        pinceladas(Rect(0f, 0f, w, faja), a.luz(P.CINABRIO_OSC, tf), 5103, 12, u, 0.2f)
        drawRect(a.luz(P.CAL, tf).copy(alpha = 0.8f), Offset(0f, faja), Size(w, w * 0.004f))
        drawRect(a.sombra(0.25f), Offset(0f, faja + w * 0.004f), Size(w, 1f * u))
        // Bajo la faja, una cenefa pintada a pincel: un tallo que ondula con sus hojas, en verde apagado y gastado.
        val yc = faja + w * 0.034f
        val tallo = Path()
        val hojas = Path()
        val pc = w * 0.05f
        var cx = -pc * 0.5f
        var ic = 0
        tallo.moveTo(cx, yc)
        while (cx < w + pc && ic < 60) {
            tallo.quadraticTo(cx + pc * 0.25f, yc + (if (ic % 2 == 0) -1f else 1f) * w * 0.007f, cx + pc * 0.5f, yc)
            val hx = cx + pc * 0.25f
            val hy = yc + (if (ic % 2 == 0) -1f else 1f) * w * 0.007f
            hojas.addOval(Rect(hx - w * 0.008f, hy - w * 0.0035f + (if (ic % 2 == 0) -1f else 1f) * w * 0.005f, hx + w * 0.008f, hy + w * 0.0035f + (if (ic % 2 == 0) -1f else 1f) * w * 0.005f))
            cx += pc * 0.5f; ic++
        }
        val verde = a.luz(Color(0xFF66733A), cerca(Offset(w * 0.6f, yc)))
        drawPath(tallo, verde.copy(alpha = 0.6f), style = Stroke(0.9f * u))
        drawPath(hojas, verde.copy(alpha = 0.55f))
        // Los paños: bandas de cinabrio que los separan y su filete fino de cal por dentro.
        val alto = apoyo + w * 0.02f
        for ((i, fx) in listOf(0.285f, 0.715f).withIndex()) {
            val x = w * fx
            val k = cerca(Offset(x, w * 0.25f))
            drawRect(a.luz(P.CINABRIO, k).copy(alpha = 0.9f), Offset(x - w * 0.008f, faja + w * 0.004f), Size(w * 0.016f, alto - faja))
            drawRect(a.brillo(0.18f, k), Offset(x - w * 0.008f, faja), Size(1f * u, alto - faja))
            for (s in listOf(-1f, 1f)) drawRect(a.luz(P.CAL, k).copy(alpha = 0.55f), Offset(x + s * w * 0.024f - w * 0.0008f, faja + w * 0.02f), Size(w * 0.0016f, alto - faja - w * 0.02f))
            if (i == 0) drawRect(a.luz(P.CAL, k).copy(alpha = 0.55f), Offset(w * 0.024f, faja + w * 0.02f), Size(x - w * 0.048f, w * 0.0016f))
            else drawRect(a.luz(P.CAL, k).copy(alpha = 0.55f), Offset(x + w * 0.024f, faja + w * 0.02f), Size(w - x - w * 0.048f, w * 0.0016f))
        }
        drawRect(a.luz(P.CAL, cerca(Offset(w * 0.5f, w * 0.05f))).copy(alpha = 0.55f), Offset(w * 0.309f, faja + w * 0.02f), Size(w * 0.382f, w * 0.0016f))
        // Los desconchones y las grietas.
        desconchon(Offset(w * 0.045f, w * 0.36f), w * 0.045f, 5120, true, a, cerca)
        desconchon(Offset(w * 0.62f, w * 0.07f), w * 0.022f, 5121, false, a, cerca)
        desconchon(Offset(w * 0.985f, w * 0.42f), w * 0.035f, 5122, true, a, cerca)
        grieta(Offset(w * 0.355f, w * 0.06f), w * 0.1f, 61, a)
        grieta(Offset(w * 0.96f, w * 0.13f), w * 0.09f, 62, a)
        // El tizne de la linterna en el ocre, encima de ella.
        for (j in 0 until 4) {
            val an = w * (0.14f - 0.026f * j)
            drawOval(P.TINTA.copy(alpha = if (a.oscuro) 0.1f else 0.06f), Offset(llama.x - an / 2 + (j - 1.5f) * 1.5f * u, w * (0.02f + 0.045f * j)), Size(an, w * (0.25f - 0.03f * j)))
        }
    }

    /** Un desconchón de la pintura: el borde irregular, el revoque claro, la sombra del canto y, si es hondo, el ladrillo. */
    private fun DrawScope.desconchon(c: Offset, r: Float, s: Int, ladrillo: Boolean, a: AireRomano, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(c)
        val p = Path().apply {
            val n = 11
            for (i in 0 until n) {
                val t = 6.283f * i / n
                val rr = r * (0.55f + 0.5f * azar(s, i)) * (if (i % 3 == 0) 0.7f else 1f)
                val q = Offset(c.x + rr * cos(t) * 1.25f, c.y + rr * sin(t) * 0.8f)
                if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
            }
            close()
        }
        drawPath(p, a.luz(P.CAL, k))
        clipPath(p) {
            drawRect(a.sombra(0.25f), Offset(c.x - r * 2, c.y - r * 2), Size(r * 4, r * 1.55f))
            if (ladrillo) {
                val bp = poligonoRomano(c.x - r * 0.8f, c.y - r * 0.05f, c.x - r * 0.2f, c.y - r * 0.32f, c.x + r * 0.6f, c.y - r * 0.2f,
                    c.x + r * 0.85f, c.y + r * 0.22f, c.x + r * 0.2f, c.y + r * 0.45f, c.x - r * 0.55f, c.y + r * 0.36f)
                drawPath(bp, a.luz(P.LADRILLO, k))
                clipPath(bp) {
                    for (fy in listOf(-0.14f, 0.13f)) drawLine(a.luz(P.MORTERO, k), Offset(c.x - r, c.y + r * fy), Offset(c.x + r, c.y + r * fy), 1.4f * u)
                    for ((fx, fy) in listOf(-0.3f to -0.42f, 0.4f to -0.42f, 0.05f to -0.14f, -0.55f to 0.13f, 0.3f to 0.13f))
                        drawLine(a.luz(P.MORTERO, k), Offset(c.x + r * fx, c.y + r * fy), Offset(c.x + r * fx, c.y + r * (fy + 0.27f)), 1.2f * u)
                    drawRect(a.sombra(0.3f), Offset(c.x - r, c.y - r), Size(r * 2, r * 0.8f))
                }
            }
        }
        drawPath(p, a.sombra(0.35f), style = Stroke(0.8f * u))
        translate(0.9f * u, 0.9f * u) { drawPath(p, a.brillo(0.3f, k), style = Stroke(0.6f * u)) }
    }

    /** Una grieta: un trazo quebrado que se adelgaza, con su labio claro. */
    private fun DrawScope.grieta(desde: Offset, largo: Float, semilla: Int, a: AireRomano) {
        val u = density
        val p = Path().apply {
            moveTo(desde.x, desde.y)
            var x = desde.x; var y = desde.y
            for (i in 1..5) {
                x += (azar(semilla, i) - 0.5f) * largo * 0.35f
                y += largo / 5f
                lineTo(x, y)
            }
        }
        drawPath(corridoRomano(p, 0.6f * u, 0.5f * u), a.claro(0.3f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
        drawPath(p, a.sombra(0.35f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    /**
     * La ventana chica y alta: el derrame abocinado en el grueso del muro (encalado: arriba en sombra, el costado izquierdo a
     * media luz, el derecho y el alféizar con luz), el arco de descarga de ladrillo que asoma encima, y en el hueco dos placas
     * de espejuelo lechoso y desiguales, con vetas y una rajada, en un bastidor de madera con su reja de hierro en aspa. De día
     * deja pasar luz difusa; de noche, el azul de fuera y la luna desdibujada detrás; en los Sueños en claro, el anochecer.
     */
    fun DrawScope.ventana(a: AireRomano) {
        val u = density
        val w = size.width
        val o = Rect(w * DERRAME.left, w * DERRAME.top, w * DERRAME.right, w * DERRAME.bottom)
        val i = Rect(w * HUECO.left, w * HUECO.top, w * HUECO.right, w * HUECO.bottom)
        val kM = if (a.lumbre) 0.05f else 1f
        // El arco de descarga, con sus dovelas de ladrillo y su sombra.
        val ra = o.width * 0.56f
        val ca = Offset(o.center.x, o.top - w * 0.004f + ra)
        val r1 = ra + w * 0.026f
        val dovelas = Path()
        for (j in 0 until 11) {
            val a0 = Math.toRadians(205.0 + 130.0 * j / 11 + 1.4).toFloat()
            val a1 = Math.toRadians(205.0 + 130.0 * (j + 1) / 11 - 1.4).toFloat()
            dovelas.addPath(poligonoRomano(ca.x + ra * cos(a0), ca.y + ra * sin(a0), ca.x + r1 * cos(a0), ca.y + r1 * sin(a0),
                ca.x + r1 * cos(a1), ca.y + r1 * sin(a1), ca.x + ra * cos(a1), ca.y + ra * sin(a1)))
        }
        drawPath(corridoRomano(dovelas, 1f * u, 1.2f * u), a.sombra(0.25f))
        drawPath(dovelas, a.luz(P.LADRILLO, kM))
        drawPath(dovelas, a.luz(P.MORTERO, kM).copy(alpha = 0.5f), style = Stroke(0.8f * u))
        // El derrame abocinado, encalado: cuatro caras, cada una con su luz.
        drawPath(poligonoRomano(o.left, o.top, o.right, o.top, i.right, i.top, i.left, i.top), a.luz(mezcla(P.CAL, P.ESTUCO_SOMBRA, 0.75f), kM))
        drawPath(poligonoRomano(o.left, o.top, i.left, i.top, i.left, i.bottom, o.left, o.bottom), a.luz(mezcla(P.CAL, P.ESTUCO_SOMBRA, 0.45f), kM))
        drawPath(poligonoRomano(o.right, o.top, o.right, o.bottom, i.right, i.bottom, i.right, i.top), a.luz(P.CAL, kM))
        drawPath(poligonoRomano(o.left, o.bottom, i.left, i.bottom, i.right, i.bottom, o.right, o.bottom), a.luz(mezcla(P.CAL, P.OCRE_CLARO, 0.2f), kM))
        drawRect(a.luz(P.CINABRIO, kM).copy(alpha = 0.8f), Offset(o.left - w * 0.008f, o.top - w * 0.008f), Size(o.width + w * 0.016f, o.height + w * 0.016f), style = Stroke(w * 0.005f))
        drawLine(a.sombra(0.2f), Offset(o.left, o.top), Offset(i.left, i.top), 0.6f * u)
        // Las dos placas de espejuelo, desiguales, en el bastidor.
        drawRect(a.luz(P.ROBLE_OSC, kM), i.topLeft, i.size)
        val m = w * 0.006f
        val corte = i.top + i.height * 0.46f
        val placaA = poligonoRomano(i.left + m, i.top + m, i.right - m, i.top + m, i.right - m, corte - m * 0.5f + 1.5f * u, i.left + m, corte - m * 0.5f - 1f * u)
        val placaB = poligonoRomano(i.left + m, corte + m * 0.5f - 1f * u, i.right - m, corte + m * 0.5f + 1.5f * u, i.right - m, i.bottom - m, i.left + m, i.bottom - m)
        val (c0, c1) = when {
            a.oscuro && a.noche -> Color(0xFF2A3A72) to Color(0xFF1A2350)
            a.oscuro -> Color(0xFF2C344C) to Color(0xFF1C2032)
            a.noche -> mezcla(P.ANOCHECER, P.CREPUSCULO, 0.35f) to P.CREPUSCULO
            else -> Color(0xFFF2EFDF) to Color(0xFFE3E7DA)
        }
        drawPath(placaA, c0)
        drawPath(placaB, c1)
        val veta = if (a.oscuro) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.6f)
        for (j in 0 until 3) drawLine(veta, Offset(i.left + i.width * (0.1f + 0.25f * j), i.top + i.height * 0.1f), Offset(i.left + i.width * (0.3f + 0.25f * j), i.bottom - i.height * 0.15f), 1.4f * u)
        if (!a.oscuro && !a.noche) drawLine(Color(0xFFD7C9E2).copy(alpha = 0.6f), Offset(i.left + i.width * 0.2f, i.top + i.height * 0.6f), Offset(i.right - i.width * 0.2f, i.top + i.height * 0.75f), 1f * u)
        val raja = Path().apply {
            moveTo(i.left + i.width * 0.3f, corte + 1f * u)
            lineTo(i.left + i.width * 0.45f, corte + i.height * 0.15f)
            lineTo(i.left + i.width * 0.38f, corte + i.height * 0.28f)
            lineTo(i.left + i.width * 0.55f, i.bottom - m)
        }
        drawPath(raja, if (a.oscuro) Color.Black.copy(alpha = 0.4f) else P.TINTA.copy(alpha = 0.35f), style = Stroke(0.7f * u))
        if (a.oscuro) mancha(Offset(i.left + i.width * 0.62f, i.top + i.height * 0.32f), i.width * (if (a.noche) 1.1f else 0.8f), P.LUNA.copy(alpha = if (a.noche) 0.6f else 0.4f))
        drawRect(a.luz(P.ROBLE, kM), Offset(i.left, corte - m * 0.5f), Size(i.width, m))
        // La reja de hierro en aspa, con su clavo en el cruce.
        val hierro = a.luz(P.HIERRO, kM)
        drawLine(hierro, Offset(i.left + 1f * u, i.top + 1f * u), Offset(i.right - 1f * u, i.bottom - 1f * u), 1.8f * u)
        drawLine(hierro, Offset(i.right - 1f * u, i.top + 1f * u), Offset(i.left + 1f * u, i.bottom - 1f * u), 1.8f * u)
        remacheRomano(i.center, 2f * u, a, kM)
    }

    /**
     * El suelo de ladrillo en espiga, del pie del podio al borde: hiladas en zigzag de ladrillos de tonos parecidos que crecen
     * hacia quien mira, cada uno con su canto de luz, las juntas de tierra y el desgaste del paso; la sombra del podio donde
     * empieza y, de noche, la luz de la linterna que apenas llega.
     */
    fun DrawScope.espiga(a: AireRomano, suelo: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width; val h = size.height
        if (h - suelo < 1f) return
        clipRect(0f, suelo, w, h) {
            drawRect(Color(0xFF5E4636), Offset(0f, suelo), Size(w, h - suelo))
            // Dos tonos según hacia dónde mira cada ladrillo (el que mira a la ventana, más claro) y, de vez en cuando, uno gastado.
            val tonos = listOf(mezcla(P.LADRILLO, P.LADRILLO_CLARO, 0.22f), mezcla(P.LADRILLO, P.LADRILLO_OSC, 0.22f), mezcla(P.LADRILLO, P.MORTERO, 0.2f))
            val caminos = List(3) { Path() }
            val cantos = Path()
            var y = suelo
            var fila = 0
            while (y < h && fila < 30) {
                val alto = maxOf(3f * u, w * (0.026f + 0.007f * fila))
                val paso = maxOf(3f * u, alto * 1.1f)
                val dx = if (fila % 2 == 0) 0f else paso * 0.5f
                var x = -paso + dx
                var j = 0
                while (x < w + paso && j < 200) {
                    val izq = j % 2 == 0
                    val g = (0.05f + 0.05f * azar(fila * 17 + j, 3)) * alto
                    val p = if (izq) poligonoRomano(x + g, y + g, x + paso - g, y + alto * 0.5f, x + paso - g, y + alto - g, x + g, y + alto * 0.5f)
                    else poligonoRomano(x + g, y + alto * 0.5f, x + paso - g, y + g, x + paso - g, y + alto * 0.5f, x + g, y + alto - g)
                    caminos[if (azar(fila * 131 + j, 9) < 0.1f) 2 else if (izq) 0 else 1].addPath(p)
                    if (izq) { cantos.moveTo(x + g, y + g); cantos.lineTo(x + paso - g, y + alto * 0.5f) }
                    else { cantos.moveTo(x + g, y + alto * 0.5f); cantos.lineTo(x + paso - g, y + g) }
                    x += paso * 0.5f
                    j++
                }
                y += alto
                fila++
            }
            val kq = if (a.oscuro) 1f else 0.3f
            for (t in 0 until 3) drawPath(caminos[t], a.luz(tonos[t], kq))
            drawPath(cantos, a.brillo(0.16f, 0.3f), style = Stroke(0.7f * u))
            drawRect(a.sombra(0.12f), Offset(0f, suelo), Size(w, h - suelo))
            // El paso gastado en medio, más claro, y la sombra que se aleja de la ventana.
            drawOval(a.claro(0.12f), Offset(w * 0.15f, suelo + (h - suelo) * 0.3f), Size(w * 0.6f, (h - suelo) * 0.6f))
            if (!a.oscuro && !a.noche) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.frio(0.18f)), w * 0.4f, w), Offset(0f, suelo), Size(w, h - suelo))
            // De noche, el suelo se hunde salvo donde llega la linterna, a la derecha.
            if (a.oscuro) with(ObraEstanteRomano) { veloNoche(Rect(0f, suelo, w, h), a, false) { p -> cerca(Offset(p.x, suelo + (h - suelo) * 0.3f)) * 0.85f } }
            else if (a.noche) {
                val tramos = 6
                for (t in 0 until tramos) {
                    val x0 = w * t / tramos
                    val k = cerca(Offset(x0 + w / tramos / 2, suelo + (h - suelo) * 0.3f))
                    drawRect(a.sombra((if (a.oscuro) 0.55f else 0.3f) * (1f - k)), Offset(x0, suelo), Size(w / tramos + 1f, h - suelo))
                }
            }
            drawRect(a.sombra(0.45f), Offset(0f, suelo), Size(w, 3f * u))
            drawRect(a.sombra(0.2f), Offset(0f, suelo + 3f * u), Size(w, 6f * u))
        }
    }
}
