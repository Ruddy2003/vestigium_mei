package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * Lo que equivale a [AireRomano.luz] puesto encima de algo ya pintado con sus colores de día: el anochecer o la noche, y el
 * calor de la linterna según [k]. Sirve para apagar por tramos lo que se pinta de una vez (los ladrillos, el suelo), así la
 * noche no queda pareja.
 */
internal fun DrawScope.veloRomano(r: Rect, a: AireRomano, k: Float) {
    if (r.width <= 0f || r.height <= 0f) return
    val kk = k.coerceIn(0f, 1f)
    val p = PaletaRomano
    if (!a.oscuro) {
        if (!a.noche) return
        drawRect(p.ANOCHECER.copy(alpha = (0.44f - 0.34f * kk).coerceIn(0f, 1f)), r.topLeft, r.size)
        if (kk > 0.02f) drawRect(p.LLAMA.copy(alpha = 0.16f * kk), r.topLeft, r.size)
        return
    }
    drawRect(a.hondo.copy(alpha = (0.8f - 0.82f * kk.pow(0.85f)).coerceIn(0f, 1f)), r.topLeft, r.size)
    if (kk > 0.02f) drawRect(p.LLAMA.copy(alpha = 0.3f * kk), r.topLeft, r.size)
}

/**
 * El muro encalado del cuarto del escribiente, la ventana chica de espejuelo arriba a la izquierda y el suelo de ladrillo en
 * espiga del estante de Romano ([MuebleEstanteRomano]).
 */
internal object MuroEstanteRomano {
    private val P = PaletaRomano

    /** El hueco de la ventana (lo de fuera) y el derrame abocinado en el grueso del muro, en anchos del mueble. */
    private val HUECO = Rect(0.125f, 0.075f, 0.205f, 0.215f)
    private val DERRAME = Rect(0.085f, 0.045f, 0.245f, 0.255f)

    /** El centro de la ventana, en anchos: de ahí sale el halo de día y el de la luna. */
    val VENTANA = Offset(0.165f, 0.15f)

    /**
     * El muro de cal entero: su cara con brochazos, el halo ancho y blando que deja la ventana (de día; en los Sueños en claro,
     * el anochecer anaranjado y bajo), las líneas de almagre gastadas, dos desconchones que dejan ver el ladrillo, dos
     * grietas, el tizne sobre la linterna y el zócalo de almagre gastado abajo.
     */
    fun DrawScope.muro(a: AireRomano, ch: Float, suelo: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = a.luz(P.CAL, if (a.lumbre) 0.05f else 1f)
        drawRect(fondo, Offset.Zero, Size(w, h))
        val vc = Offset(w * VENTANA.x, w * VENTANA.y)
        if (!a.oscuro && !a.noche) {
            // El espejuelo no deja rayo: difumina la luz en un halo ancho y blando, y lo lejano se enfría.
            drawRect(P.FRIO.copy(alpha = 0.07f), Offset(w * 0.5f, 0f), Size(w * 0.5f, ch))
            mancha(vc + Offset(w * 0.04f, w * 0.08f), w * 0.55f, P.CAL_CLARA.copy(alpha = 0.95f))
        } else if (!a.oscuro) {
            // Al anochecer: el resto en sombra violeta y la ventana que tiñe la cal de naranja, abajo.
            mancha(vc + Offset(w * 0.03f, w * 0.12f), w * 0.36f, P.CREPUSCULO.copy(alpha = 0.5f))
        } else {
            mancha(vc + Offset(w * 0.02f, w * 0.06f), w * 0.3f, P.LUNA_RAS.copy(alpha = if (a.noche) 0.16f else 0.1f))
        }
        // La cal no es lisa: brochazos cortos y cruzados, unos más claros y otros más grises.
        brochazos(Rect(0f, 0f, w, ch), a, 77)
        // Las líneas de almagre pintadas, gastadas: una faja doble arriba y los paños que bajan.
        val pinta = a.luz(P.ALMAGRE, cerca(Offset(w * 0.6f, w * 0.05f))).copy(alpha = 0.55f)
        tramado(0f, w, w * 0.022f, 2.4f * u, pinta, 31)
        tramado(0f, w, w * 0.036f, 0.9f * u, pinta, 32)
        for ((j, fx) in listOf(0.315f, 0.74f).withIndex()) {
            val x = w * fx
            var y = w * 0.036f
            var i = 0
            while (y < ch - w * 0.07f && i < 40) {
                val largo = w * (0.04f + 0.06f * azar(40 + j, i))
                drawLine(pinta, Offset(x, y), Offset(x, minOf(y + largo, ch - w * 0.07f)), 1.2f * u)
                y += largo + w * 0.014f * azar(50 + j, i)
                i++
            }
        }
        // Los desconchones: la cal saltada deja ver el ladrillo.
        desconchon(Rect(w * 0.02f, w * 0.3f, w * 0.17f, w * 0.4f), 81, a)
        desconchon(Rect(w * 0.6f, w * 0.03f, w * 0.7f, w * 0.1f), 82, a)
        // Dos grietas de la cal.
        grieta(Offset(w * 0.47f, w * 0.05f), w * 0.1f, 61, a)
        grieta(Offset(w * 0.95f, w * 0.12f), w * 0.09f, 62, a)
        // El tizne de la linterna en la cal, encima de ella.
        val lx = w * MuebleEstanteRomano.LAMP_X
        for (j in 0 until 4) {
            val an = w * (0.13f - 0.025f * j)
            drawOval(P.TINTA.copy(alpha = if (a.oscuro) 0.09f else 0.05f), Offset(lx - an / 2 + (j - 1.5f) * 1.5f * u, w * (0.0f + 0.045f * j)), Size(an, w * (0.25f - 0.03f * j)))
        }
        // El zócalo de almagre, abajo, gastado: la cal asoma donde se rozó.
        val z0 = suelo - w * 0.07f
        drawRect(a.luz(P.ALMAGRE, 0.04f), Offset(0f, z0), Size(w, suelo - z0))
        for (j in 0 until 6) {
            val x = w * (0.05f + 0.16f * j + 0.06f * azar(83, j))
            val an = w * (0.04f + 0.05f * azar(84, j))
            drawOval(a.luz(P.CAL, 0.04f).copy(alpha = 0.35f), Offset(x, z0 + (suelo - z0) * (0.2f + 0.5f * azar(85, j))), Size(an, (suelo - z0) * 0.35f))
        }
        drawRect(a.luz(P.CAL_CLARA, 0.04f).copy(alpha = 0.5f), Offset(0f, z0 - 1.6f * u), Size(w, 1f * u))
    }

    /** Brochazos de cal: trazos anchos, cortos y de punta redonda, cruzados, unos más claros y otros más grises. */
    private fun DrawScope.brochazos(r: Rect, a: AireRomano, semilla: Int) {
        val u = density
        val gris = a.luz(P.CAL_SOMBRA, 0.2f).copy(alpha = if (a.oscuro) 0.05f else 0.09f)
        val claro = a.claro(1f).copy(alpha = if (a.oscuro) 0.02f else 0.16f)
        for (j in 0 until 26) {
            val x = r.left + r.width * azar(semilla, j)
            val y = r.top + r.height * azar(semilla + 1, j)
            val largo = (14f + 22f * azar(semilla + 2, j)) * u
            val ang = (azar(semilla + 3, j) - 0.5f) * 0.9f
            drawLine(if (j % 2 == 0) gris else claro, Offset(x, y), Offset(x + largo * cos(ang), y + largo * sin(ang)), (4f + 5f * azar(semilla + 4, j)) * u, cap = StrokeCap.Round)
        }
    }

    /** Un desconchón: la cal que saltó, con su borde claro, y el ladrillo largo y apagado que asoma con su mortero. */
    private fun DrawScope.desconchon(r: Rect, semilla: Int, a: AireRomano) {
        val u = density
        val kM = if (a.lumbre) 0.05f else 1f
        val borde = Path().apply {
            val n = 9
            for (i in 0 until n) {
                val t = 2.0 * Math.PI * i / n
                val rx = r.width / 2 * (0.7f + 0.3f * azar(semilla, i)); val ry = r.height / 2 * (0.65f + 0.35f * azar(semilla + 1, i))
                val p = Offset(r.center.x + rx * cos(t).toFloat(), r.center.y + ry * sin(t).toFloat())
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(corridoRomano(borde, -1f * u, -1f * u), a.claro(0.5f))
        drawPath(borde, a.luz(P.MORTERO, kM))
        clipPath(borde) {
            val hilada = 6.4f * u
            var y = r.top
            var f = 0
            while (y < r.bottom && f < 20) {
                var x = r.left - (if (f % 2 == 0) 0f else r.width * 0.3f)
                var j = 0
                while (x < r.right && j < 6) {
                    val l = r.width * (0.5f + 0.2f * azar(semilla + f, j))
                    val t = azar(semilla * 3 + f, j)
                    drawRect(a.luz(if (t < 0.3f) P.LADRILLO_CLARO else if (t < 0.75f) P.LADRILLO else P.LADRILLO_OSC, kM), Offset(x + 1f * u, y + 1.2f * u), Size(l - 2f * u, hilada - 3f * u))
                    x += l; j++
                }
                y += hilada; f++
            }
        }
        drawPath(borde, a.frio(0.35f), style = Stroke(0.8f * u))
    }

    /** Una línea pintada a mano, que se corta donde la cal se gastó. */
    private fun DrawScope.tramado(x0: Float, x1: Float, y: Float, grosor: Float, color: Color, semilla: Int) {
        var x = x0
        var i = 0
        while (x < x1 && i < 60) {
            val largo = size.width * (0.06f + 0.14f * azar(semilla, i))
            val dy = (azar(semilla, 100 + i) - 0.5f) * grosor * 0.5f
            drawLine(color, Offset(x, y + dy), Offset(minOf(x + largo, x1), y - dy), grosor * (0.7f + 0.4f * azar(semilla, 200 + i)))
            x += largo + size.width * 0.02f * azar(semilla, 300 + i)
            i++
        }
    }

    /** Una grieta de la cal: un trazo quebrado que se adelgaza, con su labio claro. */
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
        drawPath(corridoRomano(p, 0.6f * u, 0.4f * u), a.claro(0.4f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        drawPath(p, a.frio(0.4f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    /**
     * La ventana chica y alta: el derrame abocinado en el grueso del muro (arriba en sombra, el costado izquierdo a media luz,
     * el derecho y el alféizar con luz), el arco de descarga de ladrillo que se transparenta encima, y en el hueco dos placas
     * de espejuelo lechoso y desiguales, con vetas y una rajada, en un bastidor de madera con su reja de hierro en aspa. De día
     * deja pasar luz difusa; de noche, el azul de fuera y la luna desdibujada detrás; en los Sueños en claro, el anochecer.
     */
    fun DrawScope.ventana(a: AireRomano) {
        val u = density
        val w = size.width
        val o = Rect(w * DERRAME.left, w * DERRAME.top, w * DERRAME.right, w * DERRAME.bottom)
        val i = Rect(w * HUECO.left, w * HUECO.top, w * HUECO.right, w * HUECO.bottom)
        val kM = if (a.lumbre) 0.05f else 1f
        // El arco de descarga de ladrillo encima, que se transparenta bajo la cal.
        val ra = o.width * 0.56f
        val ca = Offset(o.center.x, o.top - w * 0.006f + ra)
        val r1 = ra + w * 0.024f
        val dovelas = Path()
        for (j in 0 until 11) {
            val a0 = Math.toRadians(205.0 + 130.0 * j / 11 + 1.2).toFloat()
            val a1 = Math.toRadians(205.0 + 130.0 * (j + 1) / 11 - 1.2).toFloat()
            dovelas.addPath(poligonoRomano(ca.x + ra * cos(a0), ca.y + ra * sin(a0), ca.x + r1 * cos(a0), ca.y + r1 * sin(a0),
                ca.x + r1 * cos(a1), ca.y + r1 * sin(a1), ca.x + ra * cos(a1), ca.y + ra * sin(a1)))
        }
        drawPath(dovelas, a.luz(P.LADRILLO, kM).copy(alpha = 0.26f))
        // El derrame abocinado: cuatro caras, cada una con su luz.
        drawPath(poligonoRomano(o.left, o.top, o.right, o.top, i.right, i.top, i.left, i.top), a.luz(mezcla(P.CAL_SOMBRA, P.FRIO, 0.15f), kM))
        drawPath(poligonoRomano(o.left, o.top, i.left, i.top, i.left, i.bottom, o.left, o.bottom), a.luz(mezcla(P.CAL, P.CAL_SOMBRA, 0.45f), kM))
        drawPath(poligonoRomano(o.right, o.top, o.right, o.bottom, i.right, i.bottom, i.right, i.top), a.luz(P.CAL, kM))
        drawPath(poligonoRomano(o.left, o.bottom, i.left, i.bottom, i.right, i.bottom, o.right, o.bottom), a.luz(P.CAL_CLARA, kM))
        drawLine(a.frio(0.25f), Offset(o.left, o.top), Offset(i.left, i.top), 0.6f * u)
        drawLine(a.frio(0.15f), Offset(o.right, o.top), Offset(i.right, i.top), 0.6f * u)
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
            else -> Color(0xFFE9E5D2) to Color(0xFFDCE2D6)
        }
        drawPath(placaA, c0)
        drawPath(placaB, c1)
        // Las vetas lechosas, la raja de una placa y, de noche, la luna desdibujada detrás.
        val veta = if (a.oscuro) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.55f)
        for (j in 0 until 3) drawLine(veta, Offset(i.left + i.width * (0.1f + 0.25f * j), i.top + i.height * 0.1f), Offset(i.left + i.width * (0.3f + 0.25f * j), i.bottom - i.height * 0.15f), 1.4f * u)
        if (!a.oscuro && !a.noche) drawLine(Color(0xFFD7C9E2).copy(alpha = 0.6f), Offset(i.left + i.width * 0.2f, i.top + i.height * 0.6f), Offset(i.right - i.width * 0.2f, i.top + i.height * 0.75f), 1f * u)
        val raja = Path().apply {
            moveTo(i.left + i.width * 0.3f, corte + 1f * u)
            lineTo(i.left + i.width * 0.45f, corte + i.height * 0.15f)
            lineTo(i.left + i.width * 0.38f, corte + i.height * 0.28f)
            lineTo(i.left + i.width * 0.55f, i.bottom - m)
        }
        drawPath(raja, if (a.oscuro) Color.Black.copy(alpha = 0.4f) else P.FRIO.copy(alpha = 0.45f), style = Stroke(0.7f * u))
        if (a.oscuro) mancha(Offset(i.left + i.width * 0.62f, i.top + i.height * 0.32f), i.width * (if (a.noche) 1.1f else 0.8f), P.LUNA.copy(alpha = if (a.noche) 0.6f else 0.4f))
        drawRect(a.luz(P.ROBLE, kM), Offset(i.left, corte - m * 0.5f), Size(i.width, m))
        // La reja de hierro en aspa, con su clavo en el cruce.
        val hierro = a.luz(P.HIERRO, kM)
        drawLine(hierro, Offset(i.left + 1f * u, i.top + 1f * u), Offset(i.right - 1f * u, i.bottom - 1f * u), 1.6f * u)
        drawLine(hierro, Offset(i.right - 1f * u, i.top + 1f * u), Offset(i.left + 1f * u, i.bottom - 1f * u), 1.6f * u)
        drawCircle(hierro, 2f * u, i.center)
        drawCircle(a.claro(0.4f), 0.7f * u, i.center - Offset(0.6f * u, 0.6f * u))
    }

    /**
     * El suelo de ladrillo en espiga, del pie del estante al borde: hiladas en zigzag de ladrillos gastados de tonos parecidos,
     * cada uno algo corrido, con las juntas de tierra; más oscuro que el mueble, para que no compita. La sombra del estante
     * donde empieza y, de noche, el calor de la linterna que apenas llega.
     */
    fun DrawScope.espiga(a: AireRomano, suelo: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width; val h = size.height
        if (h - suelo < 1f) return
        clipRect(0f, suelo, w, h) {
            drawRect(Color(0xFF7A6450), Offset(0f, suelo), Size(w, h - suelo))
            val tonos = listOf(mezcla(P.LADRILLO, P.LADRILLO_CLARO, 0.4f), P.LADRILLO, mezcla(P.LADRILLO, P.LADRILLO_OSC, 0.4f))
            val caminos = List(3) { Path() }
            var y = suelo
            var fila = 0
            while (y < h && fila < 30) {
                val alto = maxOf(2f * u, w * (0.036f + 0.006f * fila))
                val paso = maxOf(2f * u, alto * 1.05f)
                val dx = if (fila % 2 == 0) 0f else paso * 0.5f
                var x = -paso + dx
                var j = 0
                while (x < w + paso && j < 200) {
                    val izq = j % 2 == 0
                    val g = (0.06f + 0.08f * azar(fila * 17 + j, 3)) * alto
                    val jy = (azar(fila * 13 + j, 4) - 0.5f) * alto * 0.12f
                    val p = if (izq) poligonoRomano(x + g, y + g + jy, x + paso - g, y + alto * 0.5f + jy, x + paso - g, y + alto - g + jy, x + g, y + alto * 0.5f + jy)
                    else poligonoRomano(x + g, y + alto * 0.5f + jy, x + paso - g, y + g + jy, x + paso - g, y + alto * 0.5f + jy, x + g, y + alto - g + jy)
                    caminos[Math.floorMod(fila * 7 + j * 3 + (azar(fila * 131 + j, 9) * 3).toInt(), 3)].addPath(p)
                    x += paso * 0.5f
                    j++
                }
                y += alto
                fila++
            }
            for (t in 0 until 3) drawPath(caminos[t], tonos[t])
            // El suelo queda más hondo que el mueble; de día, el sol difuso llega a la izquierda.
            drawRect(a.sombra(0.22f), Offset(0f, suelo), Size(w, h - suelo))
            if (!a.oscuro && !a.noche) drawRect(a.claro(0.12f), Offset(0f, suelo), Size(w * 0.45f, h - suelo))
            // La noche, por tramos: la linterna apenas llega al suelo de la derecha.
            val tramos = 6
            for (t in 0 until tramos) {
                val x0 = w * t / tramos
                veloRomano(Rect(x0, suelo, x0 + w / tramos + 1f, h), a, cerca(Offset(x0 + w / tramos / 2, suelo)) * 0.8f)
            }
            // La sombra del estante donde empieza el suelo.
            drawRect(a.sombra(0.4f), Offset(0f, suelo), Size(w, 3f * u))
            drawRect(a.sombra(0.18f), Offset(0f, suelo + 3f * u), Size(w, 5f * u))
        }
    }
}
