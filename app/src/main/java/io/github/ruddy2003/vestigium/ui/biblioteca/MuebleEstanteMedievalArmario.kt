package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.pow

/**
 * El armarium de nogal del estante de Scriptorium ([MuebleEstanteMedieval]): los tablones del fondo con sus clavos y la cenefa
 * de rombos pintada arriba de cada repisa, las tablas con su bocel sogueado, los pies derechos con sus flejes, las dos hojas
 * de la puerta abiertas a los lados (pintadas de almagre, verde y ocre, con sus bisagras de hierro en volutas), el tablero
 * con la cenefa de dientes de sierra, los canecillos con sus metopas pintadas y el zócalo.
 */
internal object ArmarioEstanteMedieval {
    private val P = PaletaMedieval

    /**
     * El velo de la luz sobre lo pintado: lo pintado se dibuja con sus colores de día y encima se le pone, por tramos, lo que
     * haría `AireMedieval.luz` (hundirlo hacia la noche y calentarlo con el candil). Así una cenefa entera son pocos trazos.
     */
    private fun DrawScope.velo(r: Rect, a: AireMedieval, vertical: Boolean, cerca: (Offset) -> Float) {
        if (!a.lumbre) return
        val tramos = 10
        for (t in 0 until tramos) {
            val q = if (vertical) Rect(r.left, r.top + r.height * t / tramos, r.right, r.top + r.height * (t + 1) / tramos + 0.6f)
            else Rect(r.left + r.width * t / tramos, r.top, r.left + r.width * (t + 1) / tramos + 0.6f, r.bottom)
            val k = cerca(q.center).coerceIn(0f, 1f)
            if (a.oscuro) {
                drawRect(a.hondo.copy(alpha = (0.9f - 0.92f * k.pow(0.85f)).coerceIn(0f, 1f)), q.topLeft, q.size)
                drawRect(P.LLAMA.copy(alpha = 0.3f * k), q.topLeft, q.size)
            } else {
                drawRect(P.ANOCHECER.copy(alpha = 0.4f - 0.2f * k), q.topLeft, q.size)
                drawRect(P.LLAMA.copy(alpha = 0.1f * k), q.topLeft, q.size)
            }
        }
    }

    /** Las manchas donde la pintura se ha ido y asoma la madera. */
    private fun DrawScope.gastado(r: Rect, semilla: Int, cuantas: Int, tam: Float = 1f) {
        val u = density
        for (i in 0 until cuantas) {
            val c = Offset(r.left + r.width * azar(semilla, i), r.top + r.height * azar(semilla, 40 + i))
            val s = (2f + 5f * azar(semilla, 80 + i)) * u * tam
            drawPath(poligonoMedieval(c.x - s, c.y, c.x - s * 0.2f, c.y - s * 0.6f, c.x + s * 0.9f, c.y - s * 0.2f, c.x + s * 0.4f, c.y + s * 0.5f), mezcla(P.NOGAL, P.NOGAL_LUZ, 0.3f).copy(alpha = 0.85f))
        }
    }

    // ---------------------------------------------------------------- las repisas

    /**
     * El fondo de una repisa: tablones de nogal de anchos desiguales, con sus juntas abiertas, los clavos de cabeza, las huellas
     * de la azuela y algún nudo; arriba, una cenefa de rombos pintada en almagre y ocre, gastada; la sombra de la tabla de
     * arriba, los costados en sombra y la del último volumen.
     */
    fun DrawScope.fondoRepisa(k: Int, r: Rect, oc: Float?, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density
        clipRect(r.left, r.top, r.right, r.bottom) {
            var x = r.left; var i = 0
            while (x < r.right) {
                val ancho = (18f + 12f * azar(1400 + k, i)) * u
                val t0 = azar(1401 + k, i)
                val tono = if (t0 > 0.75f) mezcla(P.NOGAL, P.NOGAL_LUZ, 0.18f) else mezcla(P.NOGAL, P.NOGAL_OSC, 0.25f + 0.4f * t0)
                val partes = if (a.lumbre) 3 else 1
                for (t in 0 until partes) {
                    val ya = r.top + r.height * t / partes
                    drawRect(a.luz(tono, cerca(Offset(x + ancho / 2, ya + r.height / (2 * partes)))), Offset(x, ya), Size(ancho + 0.6f, r.height / partes + 0.6f))
                }
                // La junta abierta entre dos tablones y el canto que toma la luz.
                drawRect(a.sombra(0.6f), Offset(x - 0.7f * u, r.top), Size(1.6f * u, r.height))
                drawLine(a.claro(0.16f), Offset(x + 1.4f * u, r.top), Offset(x + 1.4f * u, r.bottom), 0.8f * u)
                // Las huellas de la azuela.
                for (g in 0 until 2) {
                    val gy = r.top + r.height * (0.3f + 0.3f * g + 0.08f * azar(1402 + k, i * 7 + g))
                    val gx = x + ancho * (0.15f + 0.5f * azar(1403 + k, i * 7 + g))
                    drawArc(a.sombra(0.18f), 200f, 140f, false, Offset(gx, gy), Size(ancho * 0.4f, 4f * u), style = Stroke(0.8f * u))
                }
                if (azar(1405 + k, i) > 0.7f) {
                    val nc = Offset(x + ancho * (0.3f + 0.4f * azar(1406 + k, i)), r.top + r.height * (0.4f + 0.45f * azar(1407 + k, i)))
                    drawOval(a.sombra(0.32f), Offset(nc.x - 2.2f * u, nc.y - 3.4f * u), Size(4.4f * u, 6.8f * u))
                    drawOval(a.sombra(0.2f), Offset(nc.x - 4f * u, nc.y - 6.5f * u), Size(8f * u, 13f * u), style = Stroke(0.7f * u))
                }
                // Los clavos de cabeza que la sujetan al travesaño de atrás.
                val kc = cerca(Offset(x + ancho / 2, r.top + 16f * u))
                clavoMedieval(Offset(x + ancho * 0.5f, r.top + 17f * u), 1.5f * u, a.luz(P.HIERRO, kc), a, kc)
                x += ancho; i++
            }
            vetas(r, P.NOGAL_OSC.copy(alpha = if (a.oscuro) 0.4f else 0.5f), 1404 + k, densidad = 0.5f, vertical = true, grosor = 0.8f)
            cenefaRombos(Rect(r.left, r.top + 2f * u, r.right, r.top + 11f * u), 1430 + k, a, cerca)
            // La sombra de la tabla de arriba: honda junto a ella y deshecha hacia abajo.
            val f = if (a.oscuro) 1.2f else 1f
            drawRect(a.sombra(0.36f * f), r.topLeft, Size(r.width, 6f * u))
            drawRect(a.sombra(0.18f * f), r.topLeft, Size(r.width, 18f * u))
            drawRect(a.frio(0.1f * f), r.topLeft, Size(r.width, 34f * u))
            if (!a.oscuro) {
                drawRect(a.frio(0.3f), r.topLeft, Size(8f * u, r.height))
                pinceladas(Rect(r.left + 8f * u, r.top + r.height * 0.3f, r.left + r.width * 0.45f, r.bottom), P.NOGAL_LUZ, 1411 + k, 10, u, if (a.noche) 0.05f else 0.1f)
                drawRect(a.sombra(0.16f), Offset(r.right - 12f * u, r.top), Size(12f * u, r.height))
            } else {
                drawRect(a.sombra(0.2f), r.topLeft, Size(14f * u, r.height))
                drawRect(a.sombra(0.2f), Offset(r.right - 6f * u, r.top), Size(6f * u, r.height))
            }
            if (!a.oscuro && oc != null && oc > r.left + 6f * u) {
                val largo = 13f * u
                drawPath(poligonoMedieval(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.22f, oc + largo, r.top + r.height * 0.32f, oc + largo, r.bottom), a.frio(0.26f))
            }
        }
    }

    /** Una cenefa de rombos encadenados: fondo ocre, rombos de almagre con su filete negro y un punto de cal en cada uno. */
    private fun DrawScope.cenefaRombos(r: Rect, semilla: Int, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density
        val paso = 11f * u
        drawRect(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.2f), r.topLeft, r.size)
        drawRect(P.OCRE_PINTURA.copy(alpha = 0.5f), r.topLeft, r.size)
        val rombos = Path(); val linea = Path()
        var x = r.left - azar(semilla, 1) * paso
        while (x < r.right) {
            val c = Offset(x + paso / 2, r.center.y)
            rombos.addPath(poligonoMedieval(c.x - paso / 2, c.y, c.x, r.top, c.x + paso / 2, c.y, c.x, r.bottom))
            linea.moveTo(c.x - paso / 2, c.y); linea.lineTo(c.x, r.top); linea.lineTo(c.x + paso / 2, c.y); linea.lineTo(c.x, r.bottom); linea.close()
            x += paso
        }
        drawPath(rombos, P.ALMAGRE.copy(alpha = 0.55f))
        drawPath(linea, P.NEGRO.copy(alpha = 0.4f), style = Stroke(0.7f * u))
        x = r.left - azar(semilla, 1) * paso
        while (x < r.right) { drawCircle(P.CAL.copy(alpha = 0.45f), 0.8f * u, Offset(x + paso / 2, r.center.y)); x += paso }
        drawLine(P.NEGRO.copy(alpha = 0.4f), Offset(r.left, r.top), Offset(r.right, r.top), 0.7f * u)
        drawLine(P.NEGRO.copy(alpha = 0.4f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.7f * u)
        gastado(r, semilla, (r.width / (22f * u)).toInt().coerceIn(1, 40), 1.7f)
        velo(r, a, false, cerca)
    }

    /** Una tabla: su cara de arriba, el canto grueso con su bocel sogueado en relieve, los clavos y algún golpe. */
    fun DrawScope.tabla(r: Rect, semilla: Int, a: AireMedieval, cerca: (Float) -> Float) {
        val u = density
        val fondo = 3.5f * u
        val tramos = if (a.lumbre) 26 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            val k = cerca((xa + xb) / 2)
            drawRect(a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.6f), k), Offset(xa, r.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(P.NOGAL, k), Offset(xa, r.top), Size(xb - xa, r.height))
            if (a.oscuro) drawRect(a.filo(0.05f + 0.6f * k), Offset(xa, r.top - fondo), Size(xb - xa, 1.1f * u))
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, P.NOGAL_OSC.copy(alpha = 0.5f), semilla, densidad = 0.7f, grosor = 0.7f)
            // El listel de arriba y el de abajo, y la canal del bocel en sombra.
            val yt = r.top + 3.2f * u; val yb = r.bottom - 4f * u
            drawRect(a.claro(0.32f), Offset(r.left, r.top + 0.6f * u), Size(r.width, 1.2f * u))
            drawRect(a.sombra(0.5f), Offset(r.left, yt - 0.8f * u), Size(r.width, yb - yt + 1.6f * u))
            drawRect(a.claro(0.2f), Offset(r.left, yb + 1f * u), Size(r.width, 0.9f * u))
            // Los cabos de la soga: cada uno redondo, con la luz arriba a la izquierda y la sombra abajo a la derecha.
            // Las sombras y los brillos van en un solo trazo cada uno; el cuerpo de cada cabo, por tramos de luz.
            val paso = 5.2f * u
            val sombras = Path(); val brillos = Path()
            var x = r.left + 1f * u
            while (x < r.right) {
                sombras.moveTo(x + 1.1f * u, yb + 0.3f * u); sombras.lineTo(x + 5.5f * u, yt + 1.5f * u)
                brillos.moveTo(x - 0.9f * u, yb - 1.2f * u); brillos.lineTo(x + 3.2f * u, yt + 0.8f * u)
                x += paso
            }
            drawPath(sombras, a.sombra(0.45f), style = Stroke(3.6f * u, cap = StrokeCap.Round))
            val grupos = if (a.lumbre) 8 else 1
            for (g in 0 until grupos) {
                val cuerpos = Path()
                val xa = r.left + r.width * g / grupos; val xb = r.left + r.width * (g + 1) / grupos
                var xx = r.left + 1f * u
                while (xx < r.right) {
                    if (xx >= xa && xx < xb) { cuerpos.moveTo(xx, yb - 0.6f * u); cuerpos.lineTo(xx + 4.4f * u, yt + 0.6f * u) }
                    xx += paso
                }
                drawPath(cuerpos, a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.5f), cerca((xa + xb) / 2)), style = Stroke(3.6f * u, cap = StrokeCap.Round))
            }
            drawPath(brillos, if (a.oscuro) a.filo(0.4f * cerca(r.right - r.width * 0.2f)) else a.claro(0.42f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            for (i in 0 until 3) {
                val mx = r.left + r.width * (0.12f + 0.76f * azar(semilla, 9 + i))
                drawPath(poligonoMedieval(mx, r.top, mx + 4f * u, r.top, mx + 1.5f * u, r.top + 2.4f * u), a.sombra(0.4f))
            }
        }
        // Los clavos de cabeza de los extremos.
        for (x in listOf(r.left + 5f * u, r.right - 5f * u)) clavoMedieval(Offset(x, r.top + 1.8f * u), 1.3f * u, a.luz(P.HIERRO_LUZ, cerca(x)), a, cerca(x))
        drawLine(a.sombra(0.4f), Offset(r.left, r.top - fondo), Offset(r.right, r.top - fondo), 0.7f * u)
        drawRect(a.frio(0.45f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
    }

    /** El rótulo de un mes: una cédula de pergamino de borde desigual, pautada en almagre, clavada al canto con dos clavos. */
    fun DrawScope.cartela(p: PlacaEstante, medidor: TextMeasurer, a: AireMedieval, k: Float) {
        val u = density
        val r = p.r
        val s = p.texto.length * 31
        val ced = poligonoMedieval(
            r.left - 4f * u, r.top + 0.8f * u * azar(s, 1), r.center.x, r.top - 0.6f * u, r.right + 4f * u, r.top + 0.5f * u * azar(s, 2),
            r.right + 3f * u + 1.2f * u * azar(s, 3), r.center.y, r.right + 4f * u, r.bottom - 0.4f * u, r.center.x + r.width * 0.2f, r.bottom + 0.7f * u,
            r.left - 4f * u, r.bottom, r.left - 3f * u - 1f * u * azar(s, 4), r.center.y,
        )
        drawPath(corridoMedieval(ced, 1.2f * u, 1.6f * u), a.frio(0.45f))
        drawPath(ced, a.etiqueta(maxOf(k, 0.15f)))
        clipPath(ced) {
            pinceladas(Rect(r.left - 4f * u, r.top, r.right + 4f * u, r.bottom), a.sombra(1f), 1421 + s, 8, u, 0.06f)
            drawRect(a.claro(0.5f), Offset(r.left - 4f * u, r.top - 1f * u), Size(r.width + 8f * u, 1.4f * u))
            drawRect(a.sombra(0.12f), Offset(r.right - r.width * 0.12f, r.top - 1f * u), Size(r.width * 0.12f + 5f * u, r.height + 2f * u))
        }
        val roja = if (a.oscuro) mezcla(P.RUBRICA, a.hondo, 0.2f) else P.RUBRICA
        drawLine(roja.copy(alpha = 0.6f), Offset(r.left + 7f * u, r.top + 2.2f * u), Offset(r.right - 7f * u, r.top + 2.2f * u), 0.6f * u)
        drawLine(roja.copy(alpha = 0.6f), Offset(r.left + 7f * u, r.bottom - 2.2f * u), Offset(r.right - 7f * u, r.bottom - 2.2f * u), 0.6f * u)
        listOf(r.left + 2f * u, r.right - 2f * u).forEach { x -> clavoMedieval(Offset(x, r.center.y), 1.4f * u, a.luz(P.HIERRO_LUZ, maxOf(k, 0.3f)), a, k) }
        textoEn(medidor, p.texto, Rect(r.left + 7f * u, r.top + 2.5f * u, r.right - 7f * u, r.bottom - 2.5f * u), P.LETRA, a.tinta, 11f, minimo = 5f)
    }

    // ---------------------------------------------------------------- los costados: el pie derecho y la hoja de la puerta

    /**
     * Un costado: el pie derecho con sus chaflanes, que baja hasta el suelo, con un fleje en cada tabla; y, por fuera, la hoja
     * de la puerta abierta del todo, que se ve casi de frente y se corta en el borde: su cara pintada (almagre con cenefa verde
     * de puntos y rombos de ocre, gastada) y sus bisagras de hierro en volutas.
     */
    fun DrawScope.costado(r: Rect, derecho: Boolean, d: DisposicionEstante, a: AireMedieval, repisas: Int, ch: Float, fila: Float, suelo: Float) {
        val u = density; val w = size.width
        val anchoPie = w * 0.034f
        val pie = if (derecho) Rect(r.left, r.top, r.left + anchoPie, suelo) else Rect(r.right - anchoPie, r.top, r.right, suelo)
        fun k(y: Float, x: Float = pie.center.x) = cercaEn(x, y, ch, fila, repisas)
        // La hoja de la puerta.
        puerta(r, pie, derecho, d, a, suelo) { p -> k(p.y, p.x) }
        // El pie derecho.
        val tramos = if (a.lumbre) (pie.height / maxOf(1f, fila * 0.25f)).toInt().coerceIn(4, 80) else 1
        for (t in 0 until tramos) {
            val ya = pie.top + pie.height * t / tramos; val yb = pie.top + pie.height * (t + 1) / tramos + 0.6f
            drawRect(a.luz(P.NOGAL, k((ya + yb) / 2)), Offset(pie.left, ya), Size(pie.width, yb - ya))
        }
        clipRect(pie.left, pie.top, pie.right, pie.bottom) {
            vetas(pie, P.NOGAL_OSC.copy(alpha = 0.5f), if (derecho) 1431 else 1432, densidad = 0.8f, vertical = true, grosor = 0.7f)
        }
        val ch2 = 2.6f * u
        drawRect(if (!a.oscuro) a.claro(0.32f) else a.filo(0.15f), Offset(pie.left, pie.top), Size(ch2, pie.height))
        drawRect(a.frio(0.4f), Offset(pie.right - ch2, pie.top), Size(ch2, pie.height))
        d.tablas.forEach { t -> fleje(pie, t.center.y, derecho, a, k(t.center.y)) }
    }

    /** La luz del candil en un punto del cuerpo del mueble. */
    private fun DrawScope.cercaEn(x: Float, y: Float, ch: Float, fila: Float, repisas: Int): Float =
        MuebleEstanteMedieval.cercaCuerpo((y - ch) / maxOf(1f, fila), repisas, x / size.width)

    /** La hoja de la puerta, abierta y cortada por el borde, con su pintura y sus bisagras. */
    private fun DrawScope.puerta(r: Rect, pie: Rect, derecho: Boolean, d: DisposicionEstante, a: AireMedieval, suelo: Float, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val gozne = if (derecho) pie.right + 1.4f * u else pie.left - 1.4f * u
        val fuera = if (derecho) w + 4f * u else -4f * u
        val sobra = w * 0.012f
        val y0 = r.top + 3f * u; val y1 = suelo - w * 0.03f
        // Abierta casi del todo: el canto libre, más cerca de quien mira, se ve un poco más alto.
        val hoja = poligonoMedieval(gozne, y0, fuera, y0 - sobra, fuera, y1 + sobra, gozne, y1)
        val caja = Rect(minOf(gozne, fuera), y0 - sobra, maxOf(gozne, fuera), y1 + sobra)
        drawPath(hoja, P.NOGAL)
        // La pintura se recorta con el rectángulo del canto del gozne (más barato que recortar con la forma): en las cuñas del
        // canto libre, que el borde corta, solo queda la madera.
        clipRect(caja.left, y0, caja.right, y1) {
            vetas(caja, P.NOGAL_OSC.copy(alpha = 0.5f), if (derecho) 1441 else 1442, densidad = 0.7f, vertical = true, grosor = 0.7f)
            // Las juntas de los tablones de la hoja y sus clavos.
            val tablon = 13f * u
            var xt = caja.left + tablon * azar(if (derecho) 1445 else 1446, 1)
            while (xt < caja.right) { drawLine(a.sombra(0.4f), Offset(xt, caja.top), Offset(xt, caja.bottom), 1f * u); xt += tablon }
            // La pintura vieja: un lavado de almagre que se ha ido en buena parte, el filete del marco junto al gozne y una
            // hilera de rombos menudos; deja ver la madera.
            val hacia = if (derecho) 1f else -1f
            val marco = if (derecho) caja.left + 4f * u else caja.right - 4f * u
            val campo = Rect(caja.left, caja.top + 8f * u, caja.right, caja.bottom - 8f * u)
            drawRect(P.ALMAGRE.copy(alpha = 0.32f), campo.topLeft, campo.size)
            drawLine(P.VERDE.copy(alpha = 0.5f), Offset(marco, caja.top + 6f * u), Offset(marco, caja.bottom - 6f * u), 3f * u)
            drawLine(P.NEGRO.copy(alpha = 0.35f), Offset(marco + hacia * 2.4f * u, caja.top + 8f * u), Offset(marco + hacia * 2.4f * u, caja.bottom - 8f * u), 0.7f * u)
            drawLine(P.VERDE.copy(alpha = 0.45f), Offset(caja.left, caja.top + 7f * u), Offset(caja.right, caja.top + 7f * u), 2.4f * u)
            drawLine(P.VERDE.copy(alpha = 0.45f), Offset(caja.left, caja.bottom - 7f * u), Offset(caja.right, caja.bottom - 7f * u), 2.4f * u)
            val cx = marco + hacia * 9f * u
            val rombos = Path()
            var y = campo.top + 10f * u
            while (y < campo.bottom - 8f * u) {
                rombos.addPath(poligonoMedieval(cx - 3f * u, y, cx, y - 4.5f * u, cx + 3f * u, y, cx, y + 4.5f * u))
                y += 13f * u
            }
            drawPath(rombos, P.OCRE_PINTURA.copy(alpha = 0.5f))
            drawPath(rombos, P.NEGRO.copy(alpha = 0.3f), style = Stroke(0.6f * u))
            gastado(caja, if (derecho) 1443 else 1444, (caja.height / (16f * u)).toInt().coerceIn(4, 90), 2.8f)
            vetas(caja, P.NOGAL_OSC.copy(alpha = 0.25f), if (derecho) 1447 else 1448, densidad = 0.5f, vertical = true, grosor = 0.6f)
            // La luz: la hoja de la izquierda la recibe mejor de día; de noche, el velo del candil.
            if (!a.oscuro) drawRect(if (derecho) a.frio(0.18f) else a.claro(0.1f), caja.topLeft, caja.size)
            velo(caja, a, true, cerca)
        }
        // La rendija entre la hoja y el pie derecho.
        drawLine(a.sombra(0.7f), Offset(gozne, y0), Offset(gozne, y1), 1.6f * u)
        // Las bisagras: arriba, abajo y una más cada dos repisas.
        val ys = mutableListOf(y0 + 16f * u, y1 - 18f * u)
        var yb = y0 + 16f * u + d.tablas.firstOrNull().let { (it?.height ?: 18f * u) + 228f * u }
        while (yb < y1 - 80f * u) { ys.add(yb); yb += 264f * u }
        ys.forEach { yy -> bisagra(gozne, fuera, yy, derecho, a, cerca(Offset(gozne, yy))) }
    }

    /** Una bisagra de pala larga: el gozne en el pie derecho y la pala que se abre en volutas sobre la hoja. */
    private fun DrawScope.bisagra(gozne: Float, fuera: Float, y: Float, derecho: Boolean, a: AireMedieval, k: Float) {
        val u = density
        val hacia = if (derecho) 1f else -1f
        val hierro = a.luz(P.HIERRO, k)
        val largo = kotlin.math.abs(fuera - gozne)
        // La pala.
        drawLine(a.frio(0.45f), Offset(gozne, y + 1.4f * u), Offset(fuera, y + 1.4f * u), 4f * u)
        drawLine(hierro, Offset(gozne, y), Offset(fuera, y), 4f * u)
        drawLine(a.claro(0.3f), Offset(gozne, y - 1.4f * u), Offset(fuera, y - 1.4f * u), 0.7f * u)
        // Las volutas: dos rizos que salen de la pala, arriba y abajo, a media hoja.
        val x0 = gozne + hacia * minOf(largo * 0.45f, 16f * u)
        for (s in listOf(-1f, 1f)) {
            val p = Path().apply {
                moveTo(x0, y)
                cubicTo(x0 + hacia * 4f * u, y + s * 2f * u, x0 + hacia * 9f * u, y + s * 9f * u, x0 + hacia * 4f * u, y + s * 11f * u)
                cubicTo(x0 + hacia * 0.5f * u, y + s * 12f * u, x0 - hacia * 0.5f * u, y + s * 8f * u, x0 + hacia * 2.5f * u, y + s * 7f * u)
            }
            drawPath(corridoMedieval(p, 0.9f * u, 1.1f * u), a.frio(0.4f), style = Stroke(2f * u, cap = StrokeCap.Round))
            drawPath(p, hierro, style = Stroke(1.8f * u, cap = StrokeCap.Round))
        }
        // El gozne: el cilindro sobre la rendija, con su luz.
        drawRoundRect(hierro, Offset(gozne - 2.2f * u, y - 6f * u), Size(4.4f * u, 12f * u), CornerRadius(1.5f * u))
        drawRect(a.claro(0.35f), Offset(gozne - 1.4f * u, y - 5f * u), Size(0.9f * u, 10f * u))
        for (i in 1..2) clavoMedieval(Offset(gozne + hacia * (6f + 9f * i) * u, y), 1.2f * u, a.luz(P.HIERRO_LUZ, k), a, k)
        if (a.oscuro) drawLine(a.filo(0.5f * k), Offset(gozne, y - 2f * u), Offset(fuera, y - 2f * u), 0.7f * u)
    }

    /** El fleje de hierro que abraza el pie derecho y la tabla, con su rizo partido sobre el canto. */
    private fun DrawScope.fleje(r: Rect, y: Float, derecho: Boolean, a: AireMedieval, k: Float) {
        val u = density
        val hacia = if (derecho) -1f else 1f
        val x0 = if (derecho) r.right else r.left
        val x1 = if (derecho) r.left - 16f * u else r.right + 16f * u
        val g = 4.2f * u
        drawLine(a.frio(0.45f), Offset(x0, y + 1.4f * u), Offset(x1 + hacia * 1f * u, y + 1.4f * u), g)
        drawLine(a.luz(P.HIERRO, k), Offset(x0, y), Offset(x1, y), g)
        drawLine(a.claro(0.3f), Offset(x0, y - g * 0.4f), Offset(x1, y - g * 0.4f), 0.6f * u)
        val c = Offset(x1, y)
        for (s in listOf(-1f, 1f)) {
            val p = Path().apply {
                moveTo(c.x, c.y)
                cubicTo(c.x + hacia * 4f * u, c.y + s * 1f * u, c.x + hacia * 6f * u, c.y + s * 5f * u, c.x + hacia * 3f * u, c.y + s * 6.4f * u)
                cubicTo(c.x + hacia * 1f * u, c.y + s * 7f * u, c.x, c.y + s * 5f * u, c.x + hacia * 1.6f * u, c.y + s * 4f * u)
            }
            drawPath(p, a.frio(0.4f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(p, a.luz(P.HIERRO, k), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        clavoMedieval(Offset((x0 + x1) / 2 - hacia * 2f * u, y), 1.1f * u, a.luz(P.HIERRO_LUZ, k), a, k)
        if (a.oscuro) drawLine(a.filo(0.5f * k), Offset(x0, y - g * 0.45f), Offset(x1, y - g * 0.45f), 0.7f * u)
    }

    // ---------------------------------------------------------------- arriba y abajo

    /** El pomo torneado que remata cada pie derecho sobre el tablero, con su sombra en el muro. */
    fun DrawScope.cabeza(pie: Offset, an: Float, a: AireMedieval, cerca: (Offset) -> Float, caida: (Offset, Float) -> Offset) {
        val u = density; val w = size.width
        val k = cerca(pie - Offset(0f, w * 0.03f))
        val bloque = Rect(pie.x - an / 2, pie.y - an * 0.4f, pie.x + an / 2, pie.y + 2f * u)
        val cuello = Rect(pie.x - an * 0.2f, bloque.top - an * 0.24f, pie.x + an * 0.2f, bloque.top)
        val rb = an * 0.42f
        val bola = Offset(pie.x, cuello.top - rb * 0.9f)
        val c = caida(bola, w * 0.01f).let { Offset(it.x, minOf(it.y, w * 0.004f)) }
        drawCircle(a.frio(if (a.oscuro) 0.34f else 0.22f), rb, bola + c)
        drawRect(a.frio(if (a.oscuro) 0.34f else 0.22f), Offset(bloque.left + c.x, bloque.top + c.y), Size(an, bloque.height))
        drawRect(a.luz(P.NOGAL, k), bloque.topLeft, bloque.size)
        drawRect(a.claro(0.3f), bloque.topLeft, Size(2.4f * u, bloque.height))
        drawRect(a.frio(0.36f), Offset(bloque.right - 3f * u, bloque.top), Size(3f * u, bloque.height))
        drawRoundRect(a.luz(P.NOGAL_LUZ, k), Offset(pie.x - an * 0.4f, bloque.top - 3f * u), Size(an * 0.8f, 3f * u), CornerRadius(1.5f * u))
        drawRect(a.luz(P.NOGAL, k), cuello.topLeft, cuello.size)
        drawCircle(a.luz(P.NOGAL, k), rb, bola)
        clipPath(Path().apply { addOval(Rect(bola.x - rb, bola.y - rb, bola.x + rb, bola.y + rb)) }) {
            drawCircle(a.frio(0.32f), rb, bola + Offset(rb * 0.45f, rb * 0.35f))
            drawCircle(a.luz(P.NOGAL_LUZ, k), rb * 0.5f, bola - Offset(rb * 0.25f, rb * 0.3f))
            drawCircle(a.claro(0.4f), rb * 0.18f, bola - Offset(rb * 0.4f, rb * 0.45f))
        }
        if (a.oscuro && k > 0.05f) drawArc(a.filo(0.7f * k), 250f, 120f, false, Offset(bola.x - rb, bola.y - rb), Size(2 * rb, 2 * rb), style = Stroke(1.1f * u))
    }

    /** El tablero de arriba: su cara, que se ve apenas, y el canto con la cenefa de dientes de sierra en almagre y verde. */
    fun DrawScope.tablero(r: Rect, fondo: Float, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density
        val tramos = if (a.lumbre) 30 else 1
        for (t in 0 until tramos) {
            val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
            val k = cerca(Offset((xa + xb) / 2, r.top))
            drawRect(a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.55f), k), Offset(xa, r.top - fondo), Size(xb - xa, fondo))
            drawRect(a.luz(P.NOGAL, k), Offset(xa, r.top), Size(xb - xa, r.height))
            if (a.oscuro) drawRect(a.filo(0.06f + 0.6f * k), Offset(xa, r.top - fondo), Size(xb - xa, 1.2f * u))
        }
        clipRect(r.left, r.top - fondo, r.right, r.top) {
            vetas(Rect(r.left, r.top - fondo, r.right, r.top), P.NOGAL_OSC.copy(alpha = 0.3f), 1451, densidad = 0.9f, grosor = 0.5f)
        }
        // La cenefa: triángulos de almagre hacia abajo y de verde hacia arriba, separados por el zigzag negro y un punto de cal.
        val banda = Rect(r.left, r.top + 3f * u, r.right, r.bottom - 3.4f * u)
        drawRect(P.NOGAL, banda.topLeft, banda.size)
        drawRect(P.OCRE_PINTURA.copy(alpha = 0.55f), banda.topLeft, banda.size)
        val paso = 13f * u
        val rojos = Path(); val verdes = Path(); val zig = Path()
        var x = banda.left
        zig.moveTo(x, banda.bottom)
        while (x < banda.right) {
            rojos.addPath(poligonoMedieval(x, banda.top, x + paso, banda.top, x + paso / 2, banda.bottom))
            verdes.addPath(poligonoMedieval(x + paso / 2, banda.bottom, x + paso, banda.top, x + paso * 1.5f, banda.bottom))
            zig.lineTo(x + paso / 2, banda.bottom); zig.lineTo(x + paso, banda.top)
            x += paso
        }
        drawPath(rojos, P.ALMAGRE.copy(alpha = 0.6f))
        drawPath(verdes, P.VERDE.copy(alpha = 0.55f))
        drawPath(zig, P.NEGRO.copy(alpha = 0.45f), style = Stroke(0.9f * u))
        x = banda.left
        while (x < banda.right) { drawCircle(P.CAL.copy(alpha = 0.5f), 0.9f * u, Offset(x + paso / 2, banda.top + banda.height * 0.33f)); x += paso }
        drawLine(P.NEGRO.copy(alpha = 0.45f), Offset(banda.left, banda.top), Offset(banda.right, banda.top), 0.9f * u)
        drawLine(P.NEGRO.copy(alpha = 0.45f), Offset(banda.left, banda.bottom), Offset(banda.right, banda.bottom), 0.9f * u)
        gastado(banda, 1452, 24, 1.7f)
        if (!a.oscuro) drawRect(a.claro(0.08f), banda.topLeft, Size(banda.width * 0.5f, banda.height))
        velo(banda, a, false, cerca)
        drawRect(a.claro(0.32f), Offset(r.left, r.top), Size(r.width, 1.3f * u))
        drawRect(a.frio(0.4f), Offset(r.left, r.bottom - 3.4f * u), Size(r.width, 1f * u))
        drawLine(a.sombra(0.4f), Offset(r.left, r.top - fondo), Offset(r.right, r.top - fondo), 0.7f * u)
        drawRect(a.frio(0.5f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
    }

    /**
     * La fila de canecillos de rollos bajo el tablero, como los de los aleros de la iglesia, y entre ellos las metopas pintadas
     * de verde y de ocre con su rueda de puntos.
     */
    fun DrawScope.canecillos(r: Rect, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val an = w * 0.036f
        val paso = w * 0.074f
        // Las metopas pintadas.
        var x = r.left + w * 0.085f - paso
        var i = 0
        drawRect(P.NOGAL_OSC, r.topLeft, r.size)
        while (x < r.right) {
            val m = Rect(x + an + 1.5f * u, r.top + 2f * u, x + paso - 1.5f * u, r.bottom - 2f * u)
            if (m.width > 2f) {
                drawRect((if (i % 2 == 0) P.VERDE else P.OCRE_PINTURA).copy(alpha = 0.5f), m.topLeft, m.size)
                drawRect(P.NEGRO.copy(alpha = 0.35f), m.topLeft, m.size, style = Stroke(0.8f * u))
                val c = m.center; val rr = minOf(m.width, m.height) * 0.3f
                drawCircle((if (i % 2 == 0) P.OCRE_PINTURA else P.ALMAGRE).copy(alpha = 0.5f), rr, c)
                for (j in 0 until 6) {
                    val ang = Math.toRadians(60.0 * j)
                    drawCircle(P.CAL.copy(alpha = 0.45f), 0.8f * u, c + Offset((rr * 0.62f * kotlin.math.cos(ang)).toFloat(), (rr * 0.62f * kotlin.math.sin(ang)).toFloat()))
                }
            }
            x += paso; i++
        }
        gastado(r, 1461, 16, 1.5f)
        velo(r, a, false, cerca)
        // La sombra del tablero sobre las metopas.
        drawRect(a.frio(0.45f), r.topLeft, Size(r.width, r.height * 0.3f))
        x = r.left + w * 0.085f
        while (x + an < r.right - w * 0.04f) {
            val k = cerca(Offset(x + an / 2, r.center.y))
            val alto = r.height / 3f
            drawRect(a.frio(0.35f), Offset(x + an, r.top), Size(3f * u, r.height))
            for (j in 0 until 3) {
                val y0 = r.top + alto * j
                val rr = Rect(x + j * 0.6f * u, y0, x + an - j * 0.6f * u, y0 + alto)
                drawRoundRect(a.luz(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.3f), k), rr.topLeft, rr.size, CornerRadius(alto * 0.45f))
                drawRect(a.claro(0.32f), Offset(rr.left + 1f * u, rr.top + 0.6f * u), Size(rr.width - 2f * u, alto * 0.22f))
                drawRect(a.frio(0.38f), Offset(rr.left + 1f * u, rr.bottom - alto * 0.34f), Size(rr.width - 2f * u, alto * 0.34f))
                if (a.oscuro) drawRect(a.filo(0.45f * k), Offset(rr.left + 1f * u, rr.top + 0.4f * u), Size(rr.width - 2f * u, 0.8f * u))
            }
            x += paso
        }
        drawRect(a.sombra(0.5f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
    }

    /** El zócalo entre los pies: el travesaño con su arco rebajado, un filete de almagre y la sombra bajo el armario. */
    fun DrawScope.zocalo(r: Rect, a: AireMedieval, cerca: (Offset) -> Float) {
        val u = density
        if (r.height < 2f || r.width < 2f) return
        val k = cerca(r.center)
        drawRect(a.luz(mezcla(P.NOGAL_OSC, a.hondo, 0.4f), 0.2f * k), r.topLeft, r.size)
        val banda = r.height * 0.55f
        val pieza = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 1f * u)
            lineTo(r.right - r.width * 0.06f, r.bottom - 1f * u)
            cubicTo(r.right - r.width * 0.22f, r.top + banda * 0.9f, r.left + r.width * 0.22f, r.top + banda * 0.9f, r.left + r.width * 0.06f, r.bottom - 1f * u)
            lineTo(r.left, r.bottom - 1f * u); close()
        }
        drawPath(pieza, a.luz(P.NOGAL, k))
        clipPath(pieza) {
            vetas(r, P.NOGAL_OSC.copy(alpha = 0.45f), 1471, densidad = 0.7f, grosor = 0.7f)
            drawRect(a.claro(0.3f), r.topLeft, Size(r.width, 1.4f * u))
            drawRect(a.luz(P.ALMAGRE, k), Offset(r.left, r.top + 4f * u), Size(r.width, 2.2f * u))
            drawRect(a.luz(P.NEGRO, k), Offset(r.left, r.top + 6.2f * u), Size(r.width, 0.8f * u))
            pinceladas(r, a.sombra(1f), 1472, 10, u, 0.08f)
        }
        var x = r.left + 14f * u
        while (x < r.right - 10f * u) { clavoMedieval(Offset(x, r.top + 11f * u), 1.2f * u, a.luz(P.HIERRO, k), a, k); x += 46f * u }
    }
}
