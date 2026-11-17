package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.LejosEdo.CAMINO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoMojado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El plano medio del cuadro de Japón Edo: el camino real y, al otro lado, las primeras casas de la posta (una posada,
 * la casa grande de los señores con su portón y otra posada), la torre de vigía de los fuegos, y lo que el camino
 * lleva cada día: las sandalias rotas de los peregrinos, la mesa de los ricos, los conos de arena y los cubos de agua
 * de una procesión, los charcos.
 */
internal object PostaEdo {
    private val C = ColorEdo

    /** Donde acaba el camino y empieza el suelo de la casa de té, bajo el alero. */
    const val ORILLA = 248f

    /** Las casas de la posta: de dónde a dónde va cada una. La del medio es la de los señores (el honjin). */
    val CASAS = listOf(198f to 252f, 252f to 324f, 324f to 384f)

    /** Dónde cuelga el farol de la puerta de cada casa: se enciende de noche y se refleja en el camino mojado. */
    val FAROLES = listOf(Offset(224f, 192f), Offset(290f, 188f), Offset(346f, 192f))

    /** Cuántos faroles de la posta están encendidos, de 0 a 1: al anochecer todos; de madrugada, ninguno. */
    internal fun encendidos(m: Momento): Float { val h = m.hora; return when { h >= 18.5 || h < 0.5 -> 1f; h < 4.5 -> 0f; h < 5.5 -> 0.34f; else -> 0f } }

    // ---------------------------------------------------------------- Las casas de la posta

    fun DrawScope.casas(m: Momento, l: Luces, p: Pincel, a: Alumbrado?) {
        torreDeVigia(m, l, p)
        val procesion = EscenaEdo.procesion(m)
        for ((i, x) in CASAS.withIndex()) {
            val (x0, x1) = x
            if (i == 1) honjin(m, l, p, x0, x1, procesion) else posada(m, l, p, x0, x1, i, procesion)
        }
        if (a != null) for (f in a.focos) if (f.p.y < CAMINO && f.p.x > 190f) mancha(f.p + Offset(0f, 6f), f.alcance, f.alcance * 0.5f, f.color, (0.25f * f.fuerza).coerceIn(0f, 0.4f))
    }

    /** La torre de vigía de los fuegos, detrás de la casa de los señores: una escala con su garita y la campana. */
    private fun DrawScope.torreDeVigia(m: Momento, l: Luces, p: Pincel) {
        val lejos = 0.15f; val x = 334f; val pie = 156f; val alto = 98f
        val madera = p.medio(C.MADERA_OSC, lejos); val luz = p.luz(C.MADERA, lejos)
        drawLine(madera, Offset(x - 5f, pie), Offset(x - 3f, alto), 1.2f); drawLine(luz, Offset(x + 5f, pie), Offset(x + 3f, alto), 1.2f)
        var y = pie - 4f; while (y > alto + 4f) { drawLine(madera, Offset(x - 4.6f, y), Offset(x + 4.6f, y), 0.6f); y -= 4.4f }
        drawLine(madera, Offset(x - 5f, pie), Offset(x + 3f, alto + 20f), 0.5f); drawLine(madera, Offset(x + 5f, pie), Offset(x - 3f, alto + 20f), 0.5f)
        // La garita con su tejadillo y la campana de aviso.
        drawRect(p.medio(C.MADERA, lejos), Offset(x - 6f, alto - 1f), Size(12f, 2f))
        drawPath(poligono(Offset(x - 8f, alto - 9f), Offset(x + 8f, alto - 9f), Offset(x + 5f, alto - 14f), Offset(x - 5f, alto - 14f)), Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.TEJA, lejos), p.luz(C.TEJA, lejos)) else listOf(p.luz(C.TEJA, lejos), p.sombra(C.TEJA, lejos)), x - 8f, x + 8f))
        drawLine(madera, Offset(x - 5f, alto - 9f), Offset(x - 5f, alto), 0.6f); drawLine(madera, Offset(x + 5f, alto - 9f), Offset(x + 5f, alto), 0.6f)
        drawPath(Path().apply { moveTo(x - 1.6f, alto - 8f); lineTo(x - 2.2f, alto - 4f); lineTo(x + 2.2f, alto - 4f); lineTo(x + 1.6f, alto - 8f); close() }, p.medio(Color(0xFFA67C3E), lejos))
        if (EscenaEdo.nevado(m)) drawPath(poligono(Offset(x - 7f, alto - 10f), Offset(x + 7f, alto - 10f), Offset(x + 4.6f, alto - 14f), Offset(x - 4.6f, alto - 14f)), l.tono(C.NIEVE))
    }

    /** El tejado de teja gris de una casa, de [x0] a [x1], con el alero en [alero]: hiladas de teja, cumbrera y remates. */
    private fun DrawScope.tejado(m: Momento, l: Luces, p: Pincel, x0: Float, x1: Float, alero: Float, alto: Float, lejos: Float) {
        val forma = poligono(Offset(x0 - 4f, alero), Offset(x1 + 4f, alero), Offset(x1 - 2f, alero - alto), Offset(x0 + 2f, alero - alto))
        drawPath(forma, Brush.verticalGradient(listOf(p.arriba(lerp(C.TEJA, C.WASHI, 0.15f), lejos), p.medio(C.TEJA, lejos)), alero - alto, alero))
        clipPath(forma) {
            var x = x0 - 6f; while (x < x1 + 6f) { drawLine(p.sombra(C.TEJA, lejos).copy(alpha = 0.6f), Offset(x + 1f, alero - alto), Offset(x - 0.6f, alero), 0.6f); x += 2.4f }
            drawLine(p.luz(C.TEJA, lejos).copy(alpha = 0.5f), Offset(x0, alero - alto * 0.5f), Offset(x1, alero - alto * 0.5f), 0.4f)
            if (EscenaEdo.nevado(m)) drawPath(forma, l.tono(C.NIEVE, lejos).copy(alpha = 0.9f))
        }
        drawRect(p.medio(lerp(C.TEJA, C.SUMI, 0.3f), lejos), Offset(x0 + 1f, alero - alto - 2f), Size(x1 - x0 - 2f, 2.4f))           // la cumbrera
        for (x in listOf(x0 + 1f, x1 - 3f)) drawRect(p.medio(lerp(C.TEJA, C.SUMI, 0.4f), lejos), Offset(x, alero - alto - 4f), Size(2.4f, 3f))   // los remates
        drawRect(p.sombra(C.MADERA_OSC, lejos), Offset(x0 - 4f, alero), Size(x1 - x0 + 8f, 1.2f))
        if (p.r.fuerza > 0.05f) drawRect(p.r.color.copy(alpha = p.r.fuerza * 0.55f), Offset(x0 - 2f, alero + 1.2f), Size(x1 - x0 + 4f, 2.4f))
    }

    /** Una posada de dos pisos: abajo la celosía y la puerta con su cortina y su farol; arriba el yeso y la ventana de rejilla. */
    private fun DrawScope.posada(m: Momento, l: Luces, p: Pincel, x0: Float, x1: Float, i: Int, procesion: Boolean) {
        val lejos = 0.12f; val base = CAMINO; val piso = base - 28f; val arriba = piso - 22f
        val az = Azar(Semilla.de("edo", "posada", i))
        sombraCorta(p.r, Offset((x0 + x1) / 2f, base), x1 - x0, 40f)
        // El costado izquierdo de la primera, que se ve.
        if (i == 0) drawPath(poligono(Offset(x0 - 6f, base - 2f), Offset(x0, base), Offset(x0, arriba), Offset(x0 - 6f, arriba + 3f)), if (p.solALaDerecha) p.sombra(C.YESO, lejos) else p.luz(C.YESO, lejos))
        // Abajo: madera oscura, la celosía y la puerta abierta con su cortina de índigo.
        drawRect(p.frente(C.MADERA_OSC, lejos), Offset(x0, piso), Size(x1 - x0, base - piso))
        val puerta = Rect(x0 + (x1 - x0) * 0.4f, piso + 3f, x0 + (x1 - x0) * 0.72f, base)
        clipRect(x0, piso, x1, base) {
            var x = x0 + 2f; while (x < x1) { if (x < puerta.left || x > puerta.right) drawLine(p.luz(C.MADERA, lejos), Offset(x, piso + 3f), Offset(x, base - 3f), 0.7f); x += 2f }
        }
        drawRect(l.tono(Color(0xFF2A221C), lejos), puerta.topLeft, puerta.size)
        val noren = poligono(Offset(puerta.left - 1f, puerta.top), Offset(puerta.right + 1f, puerta.top), Offset(puerta.right + 1f, puerta.top + 11f), Offset(puerta.left - 1f, puerta.top + 11f))
        drawPath(noren, p.frente(C.INDIGO, lejos))
        for (k in 1..2) { val x = puerta.left + (puerta.width) * k / 3f; drawLine(l.tono(Color(0xFF2A221C), lejos), Offset(x, puerta.top + 2f), Offset(x, puerta.top + 11f), 0.8f) }
        drawCircle(p.medio(C.WASHI, lejos), 2.2f, Offset(puerta.center.x, puerta.top + 5.6f), style = Stroke(0.7f))
        drawRect(p.arriba(C.MADERA, lejos), Offset(x0, base - 2f), Size(x1 - x0, 2f))
        // El alero de entre pisos.
        tejado(m, l, p, x0, x1, piso, 4f, lejos)
        // Arriba: el yeso y la ventana de rejilla, o los postigos cerrados el día de la procesión.
        drawRect(p.frente(C.YESO, lejos), Offset(x0, arriba), Size(x1 - x0, piso - 4f - arriba))
        clipRect(x0, arriba, x1, piso - 4f) { repeat(5) { mancha(Offset(az.entre(x0, x1), az.entre(arriba, piso - 4f)), az.entre(4f, 10f), az.entre(1f, 2.4f), lerp(C.YESO, C.TIERRA, 0.4f), 0.3f) } }
        val ven = Rect(x0 + 6f, arriba + 4f, x1 - 6f, piso - 9f)
        if (procesion) { drawRect(p.frente(C.MADERA, lejos), ven.topLeft, ven.size); var x = ven.left; while (x < ven.right) { drawLine(p.sombra(C.MADERA_OSC, lejos), Offset(x, ven.top), Offset(x, ven.bottom), 0.5f); x += 3f } }
        else {
            drawRect(l.tono(Color(0xFF3A2E26), lejos), ven.topLeft, ven.size)
            var x = ven.left + 1f; while (x < ven.right) { drawLine(p.medio(C.MADERA, lejos), Offset(x, ven.top), Offset(x, ven.bottom), 0.8f); x += 2.4f }
            if (l.noche > 0.4f && az.real() < 0.7f * encendidos(m) + 0.2f) drawRect(Color(0xFFFFC874).copy(alpha = 0.45f), ven.topLeft, ven.size)
        }
        tejado(m, l, p, x0, x1, arriba, 14f, lejos)
        // Un letrero de tabla sin letras y, en la segunda, la baranda del balcón.
        drawRect(p.frente(C.MADERA, lejos), Offset(x0 + 4f, piso + 6f), Size(4f, 14f)); drawRect(p.luz(C.WASHI, lejos), Offset(x0 + 4.6f, piso + 7f), Size(2.8f, 12f))
        if (i == 2) { drawLine(p.medio(C.MADERA, lejos), Offset(x0, piso - 8f), Offset(x1, piso - 8f), 0.8f); var x = x0; while (x < x1) { drawLine(p.medio(C.MADERA, lejos), Offset(x, piso - 8f), Offset(x, piso - 4f), 0.5f); x += 3f } }
        farol(l, p, FAROLES[i], m)
    }

    /**
     * La casa grande de los señores (el honjin): un muro de tablas con su portón de tejadillo; el día de la procesión,
     * las cortinas moradas con el blasón blanco tendidas en el muro y el portón abierto.
     */
    private fun DrawScope.honjin(m: Momento, l: Luces, p: Pincel, x0: Float, x1: Float, procesion: Boolean) {
        val lejos = 0.14f; val base = CAMINO
        sombraCorta(p.r, Offset((x0 + x1) / 2f, base), x1 - x0, 46f)
        // La casa, detrás del muro: el yeso alto y el gran tejado.
        drawRect(p.frente(C.YESO, lejos), Offset(x0 + 2f, base - 58f), Size(x1 - x0 - 4f, 30f))
        for (k in 0 until 4) { val x = x0 + 8f + k * 16f; drawRect(l.tono(Color(0xFF3A2E26), lejos), Offset(x, base - 52f), Size(9f, 7f)); var xx = x + 1f; while (xx < x + 9f) { drawLine(p.medio(C.MADERA, lejos), Offset(xx, base - 52f), Offset(xx, base - 45f), 0.6f); xx += 2f } }
        tejado(m, l, p, x0, x1, base - 58f, 18f, lejos)
        // El muro de tablas con su albardilla de teja.
        drawRect(p.frente(C.MADERA, lejos), Offset(x0, base - 26f), Size(x1 - x0, 26f))
        var x = x0; while (x < x1) { drawLine(p.sombra(C.MADERA_OSC, lejos), Offset(x, base - 26f), Offset(x, base), 0.5f); x += 3.4f }
        drawRect(p.medio(C.TEJA, lejos), Offset(x0 - 1f, base - 29f), Size(x1 - x0 + 2f, 3f))
        // El portón, en medio, con su tejadillo.
        val pc = (x0 + x1) / 2f
        drawRect(l.tono(if (procesion) Color(0xFF3A2E26) else C.MADERA_OSC, lejos), Offset(pc - 10f, base - 30f), Size(20f, 30f))
        if (!procesion) { drawLine(p.medio(C.MADERA, lejos), Offset(pc, base - 30f), Offset(pc, base), 0.6f); for (yy in listOf(base - 22f, base - 10f)) drawLine(p.medio(Color(0xFF6A6A70), lejos), Offset(pc - 10f, yy), Offset(pc + 10f, yy), 0.8f) }
        else drawRect(p.arriba(C.TIERRA, lejos), Offset(pc - 9f, base - 6f), Size(18f, 6f))
        for (dx in listOf(-11f, 10f)) drawRect(p.medio(C.MADERA_OSC, lejos), Offset(pc + dx, base - 34f), Size(2f, 34f))
        tejado(m, l, p, pc - 13f, pc + 13f, base - 34f, 7f, lejos)
        if (procesion) {
            // Las cortinas de la procesión: moradas, con el blasón blanco, de punta a punta del muro.
            for ((a, b) in listOf(x0 + 1f to pc - 13f, pc + 13f to x1 - 1f)) {
                val forma = Path().apply { moveTo(a, base - 25f); lineTo(b, base - 25f); lineTo(b, base - 9f); cubicTo((a + b) / 2f + 4f, base - 7f, (a + b) / 2f - 4f, base - 10f, a, base - 8f); close() }
                drawPath(forma, p.frente(Color(0xFF5A3A6E), lejos))
                clipPath(forma) { for (k in 1 until 4) drawLine(p.sombra(Color(0xFF5A3A6E), lejos), Offset(a + (b - a) * k / 4f, base - 25f), Offset(a + (b - a) * k / 4f, base - 8f), 0.6f) }
                drawCircle(p.luz(C.WASHI, lejos), 3.2f, Offset((a + b) / 2f, base - 17f), style = Stroke(0.9f)); drawCircle(p.luz(C.WASHI, lejos), 1f, Offset((a + b) / 2f, base - 17f))
            }
        }
        farol(l, p, FAROLES[1], m)
    }

    /** Un farol de papel colgado a la puerta, con su aro rojo; encendido de noche. */
    private fun DrawScope.farol(l: Luces, p: Pincel, c: Offset, m: Momento) {
        val encendido = l.noche > 0.4f && encendidos(m) > 0f
        drawLine(p.medio(C.MADERA_OSC, 0.1f), c + Offset(0f, -6f), c + Offset(0f, -4f), 0.5f)
        drawOval(if (encendido) Color(0xFFFFD08A) else p.medio(C.WASHI, 0.1f), Offset(c.x - 2.6f, c.y - 4f), Size(5.2f, 7.4f))
        drawRect(p.medio(C.SUMI, 0.1f), Offset(c.x - 2f, c.y - 4.4f), Size(4f, 1f)); drawRect(p.medio(C.SUMI, 0.1f), Offset(c.x - 2f, c.y + 3f), Size(4f, 1f))
        drawLine(if (encendido) C.BERMELLON else p.medio(C.BERMELLON, 0.1f), Offset(c.x - 2.4f, c.y), Offset(c.x + 2.4f, c.y), 0.6f)
    }

    // ---------------------------------------------------------------- El camino

    /**
     * El camino real, de tierra apisonada: más claro y velado lejos, con las rodadas de los carros de mano, las pisadas,
     * las boñigas de los caballos y las piedras; de madrugada, regado y oscuro; con lluvia, charcos que devuelven el
     * cielo y los faroles; con nieve, blanco con sus rodadas.
     */
    fun DrawScope.camino(m: Momento, l: Luces, p: Pincel, a: Alumbrado?) {
        val az = Azar(Semilla.de("edo", "camino"))
        val regado = EscenaEdo.regado(m); val nevado = EscenaEdo.nevado(m); val llueve = EscenaEdo.llueve(m)
        val tierra = when { nevado -> C.NIEVE; regado || llueve -> lerp(C.TIERRA, C.BARRO, 0.55f); else -> C.TIERRA }
        // La cuneta del otro lado: hierba y la franja de paso hasta las casas.
        drawRect(p.arriba(lerp(C.ARROZ, C.TIERRA, 0.5f), 0.1f), Offset(0f, CAMINO - 2f), Size(ANCHO, 4f))
        drawRect(Brush.verticalGradient(listOf(p.arriba(lerp(tierra, C.WASHI, 0.15f), 0.12f), p.arriba(tierra), p.arriba(lerp(tierra, C.BARRO, 0.15f))), CAMINO + 1f, ORILLA), Offset(0f, CAMINO + 1f), Size(ANCHO, ORILLA - CAMINO))
        // La senda clara del medio, pisada por todos, más lisa y más pálida.
        drawPath(poligono(Offset(-4f, CAMINO + 12f), Offset(ANCHO + 4f, CAMINO + 18f), Offset(ANCHO + 4f, CAMINO + 30f), Offset(-4f, CAMINO + 20f)), p.arriba(lerp(tierra, C.WASHI, 0.18f)).copy(alpha = 0.6f))
        // Las rodadas y las pisadas, que se juntan hacia la izquierda, por donde el camino se va.
        for (k in listOf(0.35f, 0.5f, 0.72f)) {
            val y0 = CAMINO + 2f + (ORILLA - CAMINO) * k * 0.6f; val y1 = CAMINO + (ORILLA - CAMINO) * k
            drawLine(p.sombra(tierra).copy(alpha = 0.55f), Offset(0f, y0), Offset(ANCHO, y1), 1.4f)
            drawLine(p.luz(tierra).copy(alpha = 0.3f), Offset(0f, y0 - 1f), Offset(ANCHO, y1 - 1f), 0.6f)
        }
        repeat(220) { val q = Offset(az.entre(0f, ANCHO), az.entre(CAMINO + 2f, ORILLA)); drawOval((if (az.real() > 0.5f) p.sombra(tierra) else p.luz(tierra)).copy(alpha = 0.4f), Offset(q.x, q.y), Size(az.entre(0.8f, 2.4f), az.entre(0.5f, 1.2f))) }
        if (!nevado) repeat(5) { val q = Offset(az.entre(20f, 360f), az.entre(CAMINO + 8f, ORILLA - 4f)); drawOval(p.medio(Color(0xFF6A5236)), Offset(q.x, q.y), Size(3f, 1.4f)) }
        if (regado && !llueve) {
            // El agua recién echada con el cazo: arcos oscuros y brillos.
            repeat(18) { val q = Offset(az.entre(30f, 360f), az.entre(ORILLA - 26f, ORILLA - 2f)); drawArc(lerp(l.medio, l.bajo, 0.4f).copy(alpha = 0.35f), 200f, 140f, false, q, Size(az.entre(8f, 18f), 3f), style = Stroke(0.8f)) }
        }
        if (llueve) {
            for ((c, w) in listOf(Offset(70f, 226f) to 30f, Offset(190f, 238f) to 40f, Offset(310f, 222f) to 24f)) {
                drawOval(lerp(lerp(l.medio, l.alto, 0.5f), p.medio(C.BARRO), 0.45f).copy(alpha = 0.85f), Offset(c.x - w, c.y - w * 0.12f), Size(w * 2f, w * 0.24f))
                drawOval(lerp(l.bajo, Color.White, 0.2f).copy(alpha = 0.25f), Offset(c.x - w * 0.6f, c.y - w * 0.08f), Size(w * 1.2f, w * 0.06f))
                reflejoRoto(c.x - w * 0.6f, c.x + w * 0.6f, c.y - w * 0.06f, w * 0.1f, p.medio(C.MADERA_OSC, 0.1f), 0.4f, az, 0.6f)
            }
            if (a != null) for (f in a.focos) if (f.p.y < CAMINO) reflejoMojado(Offset(f.p.x, CAMINO + 6f), 30f, 6f, f.color, 0.6f * f.fuerza, az)
        }
        // La cuneta de piedra delante de las casas, con su tablón en cada puerta, y la de este lado, con hierba.
        drawRect(p.sombra(C.TEJA), Offset(196f, CAMINO + 0.6f), Size(ANCHO - 196f, 3.4f)); drawLine(p.luz(C.YESO), Offset(196f, CAMINO + 4f), Offset(ANCHO, CAMINO + 4f), 0.6f)
        for (x in listOf(230f, 290f, 350f)) drawRect(p.arriba(C.MADERA), Offset(x - 8f, CAMINO), Size(16f, 4f))
        repeat(50) { val q = Offset(az.entre(0f, ANCHO), ORILLA - az.entre(0f, 2.4f)); drawLine(p.medio(lerp(C.ARROZ, C.PAJA, 0.4f)), q, q + Offset(az.entre(-1.4f, 1.4f), -az.entre(1.4f, 3.6f)), 0.6f, StrokeCap.Round) }
        // El jizō del camino: una piedra con su babero rojo y su ofrenda, al otro lado, a la izquierda.
        jizo(l, p, Offset(64f, CAMINO + 1f))
        if (EscenaEdo.okage(m)) sandaliasRotas(m, l, p)
        if (EscenaEdo.procesion(m)) conosDeArena(l, p)
    }

    /** El jizō de piedra, protector de los caminantes, con su babero rojo, su gorro y una taza de ofrenda. */
    private fun DrawScope.jizo(l: Luces, p: Pincel, pie: Offset) {
        sombraCorta(p.r, pie, 8f, 14f)
        drawRect(p.lado(C.TEJA), Offset(pie.x - 6f, pie.y - 3f), Size(12f, 3f))
        val cuerpo = Path().apply { moveTo(pie.x - 4f, pie.y - 3f); lineTo(pie.x - 4.6f, pie.y - 12f); cubicTo(pie.x - 4.6f, pie.y - 18f, pie.x + 4.6f, pie.y - 18f, pie.x + 4.6f, pie.y - 12f); lineTo(pie.x + 4f, pie.y - 3f); close() }
        drawPath(cuerpo, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(lerp(C.TEJA, C.YESO, 0.4f)), p.luz(lerp(C.TEJA, C.YESO, 0.4f))) else listOf(p.luz(lerp(C.TEJA, C.YESO, 0.4f)), p.sombra(lerp(C.TEJA, C.YESO, 0.4f))), pie.x - 5f, pie.x + 5f))
        drawPath(poligono(Offset(pie.x - 4.4f, pie.y - 11f), Offset(pie.x + 4.4f, pie.y - 11f), Offset(pie.x, pie.y - 6f)), p.medio(C.BERMELLON))
        drawOval(p.medio(C.BERMELLON), Offset(pie.x - 4f, pie.y - 18.4f), Size(8f, 3f))
        drawCircle(p.luz(C.YESO), 1f, Offset(pie.x + 7f, pie.y - 1.6f))
    }

    /** Los montones de sandalias de paja rotas que dejan los peregrinos en la cuneta. */
    private fun DrawScope.sandaliasRotas(m: Momento, l: Luces, p: Pincel) {
        val az = m.azar("sandalias")
        for (c in listOf(Offset(40f, CAMINO + 4f), Offset(120f, CAMINO + 6f), Offset(176f, CAMINO + 3f), Offset(260f, ORILLA - 4f), Offset(84f, ORILLA - 6f))) repeat(9) {
            val q = c + Offset(az.entre(-9f, 9f), az.entre(-2f, 2f)); val ang = az.entre(-0.6f, 0.6f)
            drawOval(p.medio(lerp(C.PAJA, C.TIERRA, az.entre(0f, 0.5f))), Offset(q.x - 2.4f, q.y - 1f), Size(4.8f, 2f))
            drawLine(p.sombra(C.PAJA), q + Offset(-2f, ang), q + Offset(2f, -ang), 0.4f)
        }
    }

    /** Los conos de arena delante de cada puerta y los cubos con su cazo: el camino preparado para la procesión. */
    private fun DrawScope.conosDeArena(l: Luces, p: Pincel) {
        for (x0 in listOf(212f, 240f, 270f, 312f, 336f, 362f)) {
            val pie = Offset(x0, CAMINO + 6f)
            drawPath(poligono(pie + Offset(-3.4f, 0f), pie + Offset(3.4f, 0f), pie + Offset(0f, -5f)), Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.YESO), p.luz(C.YESO)) else listOf(p.luz(C.YESO), p.sombra(C.YESO)), pie.x - 3.4f, pie.x + 3.4f))
            sombraCorta(p.r, pie, 5f, 5f)
        }
        for (x0 in listOf(226f, 296f, 350f)) { val pie = Offset(x0, CAMINO + 4f); drawRect(p.lado(C.MADERA), Offset(pie.x - 2.4f, pie.y - 4f), Size(4.8f, 4f)); drawLine(p.medio(C.MADERA_OSC), pie + Offset(-2.4f, -2f), pie + Offset(2.4f, -2f), 0.5f); drawLine(p.medio(C.MADERA), pie + Offset(1f, -4f), pie + Offset(3f, -8f), 0.5f) }
        // El camino barrido: las rayas de la escoba.
        val az = Azar(Semilla.de("edo", "barrido"))
        repeat(40) { val q = Offset(az.entre(0f, ANCHO), az.entre(CAMINO + 4f, ORILLA - 2f)); drawArc(p.sombra(C.TIERRA).copy(alpha = 0.2f), 190f, 160f, false, q, Size(az.entre(6f, 12f), 2f), style = Stroke(0.5f)) }
    }

    /** La mesa que los ricos de la posta ponen para los peregrinos: un tablero con la tina del arroz, la tetera y sandalias nuevas. */
    fun DrawScope.mesaDeLosPeregrinos(m: Momento, l: Luces, p: Pincel) {
        if (!EscenaEdo.okage(m)) return
        val x0 = 140f; val x1 = 192f; val y = CAMINO - 2f
        sombraCorta(p.r, Offset((x0 + x1) / 2f, y + 1f), x1 - x0, 10f)
        drawRect(p.arriba(C.MADERA), Offset(x0, y - 9f), Size(x1 - x0, 2f)); drawRect(p.frente(C.MADERA_OSC), Offset(x0, y - 7f), Size(x1 - x0, 1.4f))
        for (x in listOf(x0 + 2f, x1 - 3f)) drawRect(p.medio(C.MADERA_OSC), Offset(x, y - 7f), Size(1.2f, 7f))
        drawPath(Path().apply { moveTo(x0 + 6f, y - 9f); lineTo(x0 + 7f, y - 15f); lineTo(x0 + 19f, y - 15f); lineTo(x0 + 20f, y - 9f); close() }, p.lado(C.MADERA))
        drawOval(p.arriba(C.WASHI), Offset(x0 + 7f, y - 16.4f), Size(12f, 2.6f))
        drawOval(p.medio(C.SUMI), Offset(x0 + 25f, y - 15f), Size(7f, 6f)); drawLine(p.medio(C.SUMI), Offset(x0 + 25f, y - 14f), Offset(x0 + 32f, y - 14f), 0.6f)
        for (k in 0..2) drawOval(p.medio(C.PAJA), Offset(x0 + 36f + k * 4f, y - 11f), Size(3.4f, 1.6f))
        // La bandera de tela del pueblo de los peregrinos, sin letras: un paño blanco con una franja.
        drawLine(p.medio(C.MADERA_OSC), Offset(x1 + 2f, y + 1f), Offset(x1 + 2f, y - 34f), 0.8f)
        drawRect(p.medio(C.WASHI), Offset(x1 + 2.4f, y - 33f), Size(6f, 18f)); drawRect(p.medio(C.INDIGO), Offset(x1 + 2.4f, y - 33f), Size(6f, 3f))
    }

    /** Las fiestas de la posta que se ven desde aquí: los faroles de Bon de casa en casa y las carpas de tela del día de los niños. */
    fun DrawScope.fiestasDeLaPosta(m: Momento, l: Luces, p: Pincel, reloj: Float = 0f) {
        if (EscenaEdo.bon(m)) banderines(l, Offset(198f, 172f), Offset(384f, 172f), 6f, listOf(C.WASHI, C.WASHI, Color(0xFFE8D0A0)), 4f, punta = false, lejos = 0.1f)
        if (EscenaEdo.tango(m)) {
            // Las carpas de tela en su vara, sobre el tejado de la primera posada.
            val x = 236f; drawLine(p.medio(C.MADERA_OSC, 0.1f), Offset(x, 150f), Offset(x, 96f), 1f)
            for ((i, c) in listOf(C.SUMI, C.BERMELLON, C.INDIGO).withIndex()) {
                val y = 100f + i * 11f; val largo = 26f - i * 4f
                drawPath(Path().apply { moveTo(x, y); cubicTo(x - largo * 0.4f, y - 2f, x - largo * 0.8f, y + 1f, x - largo, y + 2f); lineTo(x - largo + 3f, y + 4f); lineTo(x - largo, y + 6f); cubicTo(x - largo * 0.7f, y + 7f, x - largo * 0.3f, y + 8f, x, y + 7f); close() }, p.frente(c, 0.1f))
                drawCircle(p.luz(C.WASHI, 0.1f), 1.2f, Offset(x - 2.4f, y + 3.4f))
            }
        }
    }

}
