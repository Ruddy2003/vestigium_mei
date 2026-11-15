package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.k
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Lo poco que queda de la casa: el tablado de la galería (tablas gastadas, con sus juntas, sus clavos, el barro de las
 * botas y las hojas que trae el viento) con la luz que entra rota por la bóveda; y la esquina del alero, vista por
 * debajo (los cabios, el cañizo y las tejas del canto), de la que cuelgan los cestos de orquídeas, una bromelia en su
 * tabla y un manojo de plantas secándose.
 */
internal object GaleriaSelva {
    private val C = ColorBotanico
    private val S = SelvaBotanico

    /** La madera del tablado, gastada por el agua y el sol: tira a gris. */
    private val TABLA = lerp(lerp(ColorBotanico.MADERA, ColorBotanico.ROCA, 0.2f), ColorBotanico.MADERA_OSC, 0.18f)

    /** El tablado en el lienzo. */
    fun forma(): Path = poligono(listOf(pc(S.TABLADO_X0, 0f, S.TABLADO_Z0), pc(S.TABLADO_X1, 0f, S.TABLADO_Z0), pc(S.TABLADO_X1, 0f, S.TABLADO_Z1), pc(S.TABLADO_X0, 0f, S.TABLADO_Z1)))

    fun DrawScope.tablado(m: Momento, luz: LuzSelva.Luz, cosas: List<CercaSelva.Cosa3>) {
        val az = Azar(Semilla.de("botanico", "tablado"))
        val p = luz.sombra; val tablado = forma()
        drawPath(tablado, p.arriba(TABLA))
        clipPath(tablado) {
            // Cada tabla con su tono, de la casa al jardín: unas más grises, otras con el rojizo de la madera nueva.
            var x = S.TABLADO_X0; var i = 0
            while (x < S.TABLADO_X1) {
                val tono = lerp(TABLA, if (i % 3 == 0) C.MADERA else if (i % 3 == 1) C.ROCA else C.ARENA, az.entre(0.05f, 0.22f))
                drawPath(poligono(pc(x, 0f, S.TABLADO_Z0), pc(x + 0.16f, 0f, S.TABLADO_Z0), pc(x + 0.16f, 0f, S.TABLADO_Z1), pc(x, 0f, S.TABLADO_Z1)), p.arriba(tono))
                // La veta, en trazos largos; los nudos.
                repeat(3) { val u = x + az.entre(0.02f, 0.14f); val z0 = az.entre(1.5f, 4.5f); drawLine(p.sombra(tono).copy(alpha = 0.22f), pc(u, 0f, z0), pc(u + az.entre(-0.01f, 0.01f), 0f, z0 + az.entre(0.4f, 1.2f)), 0.4f) }
                if (az.real() < 0.3f) { val q = pc(x + 0.08f, 0f, az.entre(2.8f, 5f)); drawOval(p.sombra(tono).copy(alpha = 0.45f), Offset(q.x - 1.4f, q.y - 0.5f), Size(2.8f, 1f)) }
                // La junta, oscura, y el canto de luz de la tabla de al lado.
                drawLine(p.sombra(lerp(C.MADERA_OSC, C.BREA, 0.3f)).copy(alpha = 0.75f), pc(x, 0f, S.TABLADO_Z0), pc(x, 0f, S.TABLADO_Z1), 0.7f)
                drawLine(p.luz(tono).copy(alpha = 0.25f), pc(x + 0.012f, 0f, S.TABLADO_Z0), pc(x + 0.012f, 0f, S.TABLADO_Z1), 0.4f)
                // Los clavos, de dos en dos, en cada durmiente.
                var z = 1.6f; while (z < S.TABLADO_Z1) { for (dx in listOf(0.04f, 0.12f)) drawCircle(p.sombra(C.HIERRO).copy(alpha = 0.7f), (0.012f * k(z)).coerceIn(0.35f, 1.3f), pc(x + dx, 0f, z)); z += 0.62f }
                x += 0.16f; i++
            }
            // El roce del paso: de la escalera del jardín (a la derecha) hacia la mesa; y el barro de las botas.
            for (j in 0..6) { val t = j / 6f; val q = pc(1.2f - 2.2f * t, 0f, 4.9f - 1.2f * t); mancha(q, 14f + 8f * t, 3.5f + 2f * t, p.luz(lerp(TABLA, C.ARENA, 0.4f)), 0.16f) }
            for (j in 0..7) { val t = j / 7f; val lado = if (j % 2 == 0) -0.09f else 0.09f; val q = V3(1.3f - 2.0f * t + lado, 0f, 5.0f - 1.4f * t)
                val c = pc(q); val e = k(q.z); drawOval(p.medio(lerp(C.MADERA_OSC, C.CUERO, 0.3f)).copy(alpha = 0.4f * (1f - t * 0.7f)), Offset(c.x - e * 0.05f, c.y - e * 0.03f), Size(e * 0.1f, e * 0.06f)) }
            // Las hojas que trae el viento y la tierra que cae de las macetas.
            repeat(26) { val q = V3(az.entre(-1.6f, 1.4f), 0f, az.entre(2.9f, 5.2f)); val c = pc(q); val e = k(q.z); val a = az.entre(0f, 6.28f); val l = e * az.entre(0.04f, 0.09f)
                val col = if (it % 3 == 0) lerp(C.ARENA, C.CUERO, 0.5f) else lerp(C.HOJA, C.CUERO, az.real())
                drawPath(Path().apply { moveTo(c); cubicTo(c + Offset(cos(a + 0.6f) * l * 0.6f, sin(a + 0.6f) * l * 0.25f), c + Offset(cos(a) * l, sin(a) * l * 0.35f), c + Offset(cos(a) * l * 1.1f, sin(a) * l * 0.38f)); cubicTo(c + Offset(cos(a) * l * 0.9f, sin(a) * l * 0.3f), c + Offset(cos(a - 0.6f) * l * 0.6f, sin(a - 0.6f) * l * 0.25f), c); close() }, p.arriba(col)) }
            // Con lluvia, el tablado mojado: más oscuro junto al jardín y con charcos que reflejan.
            if (S.mojado(m) && m.ficha.clima != Clima.NIEBLA) {
                drawPath(tablado, Brush.horizontalGradient(listOf(Color.Transparent, p.sombra(C.MADERA_OSC).copy(alpha = 0.4f)), pc(-1f, 0f, 4f).x, pc(1.4f, 0f, 4f).x))
                for ((cx, cz, r) in listOf(Triple(0.9f, 4.3f, 0.35f), Triple(0.2f, 3.3f, 0.25f), Triple(1.2f, 3.4f, 0.2f))) {
                    val c = pc(cx, 0f, cz); val e = k(cz)
                    val charco = Path().apply { addOval(androidx.compose.ui.geometry.Rect(c.x - r * e, c.y - r * e * 0.2f, c.x + r * e, c.y + r * e * 0.2f)) }
                    drawPath(charco, lerp(luz.l.medio, luz.l.alto, 0.4f).copy(alpha = 0.35f))
                    clipPath(charco) { reflejoRoto(c.x - r * e * 0.3f, c.x + r * e * 0.2f, c.y - r * e * 0.2f, r * e * 0.4f, p.sombra(C.SELVA), 0.5f, az, 0.7f) }
                }
            }
        }
        // El sol (o la luna) que entra roto: la mancha, con la sombra de lo que hay en el tablado.
        with(LuzSelva) { luzEnPlano(luz, tablado, 0f, cosas.mapNotNull { it.sombra }) }
        // El reflejo de la mancha: lo claro de alrededor devuelve algo de luz.
        if (luz.directa > 0.2f && !luz.deLuna) mancha(pc(0.4f, 0f, 4f), 90f, 18f, luz.color, 0.06f * luz.directa)
        // De noche, la lámpara deja su charco en las tablas, junto a la mesa.
        if (luz.lampara > 0.05f) clipPath(tablado) { charcoDeLuz(Foco(CercaSelva.LAMPARA, C.LUMBRE, luz.lampara, 90f), pc(-0.8f, 0f, 3.75f), 95f, 24f, TABLA, 0.85f) }
        // El borde del tablado sobre el jardín: el canto de la última tabla, con su luz, y el musgo de debajo.
        drawLine(p.luz(lerp(TABLA, C.ARENA, 0.3f)), pc(S.TABLADO_X0, 0f, S.TABLADO_Z1), pc(S.TABLADO_X1, 0f, S.TABLADO_Z1), 1.3f)
        drawLine(p.luz(lerp(TABLA, C.ARENA, 0.3f)), pc(S.TABLADO_X1, 0f, S.TABLADO_Z0), pc(S.TABLADO_X1, 0f, S.TABLADO_Z1), 1.3f)
        // Lo que crece en la orilla y se echa sobre las tablas: frondas y hierbas que rompen el borde.
        val orilla = (0..16).map { val t = it / 16f; if (t < 0.6f) V3(-2.4f + 4f * t / 0.6f, 0f, S.TABLADO_Z1) else V3(S.TABLADO_X1, 0f, S.TABLADO_Z1 - 2.2f * (t - 0.6f) / 0.4f) }
        val hierba = ArrayList<Offset>(); val clara = ArrayList<Offset>()
        for (q in orilla) { val c = pc(q); val e = k(q.z); repeat(6) { val b = c + Offset(az.entre(-6f, 6f), az.entre(-1f, 1.5f)); val a = -PI.toFloat() / 2f + az.entre(-1.3f, 1.3f); val l = e * az.entre(0.05f, 0.14f)
            (if (it % 2 == 0) hierba else clara).apply { add(b); add(b + Offset(cos(a) * l, sin(a) * l)) } } }
        drawPoints(hierba, PointMode.Lines, luz.p.sombra(lerp(C.SELVA, C.HOJA, 0.3f)), 1.1f, StrokeCap.Round)
        drawPoints(clara, PointMode.Lines, luz.p.luz(lerp(C.HOJA, C.HOJA_LUZ, 0.3f)), 0.9f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- La esquina del alero

    /** La altura del tejado sobre las tablas en [x] (sube hacia la casa). */
    private fun tejado(x: Float): Float = S.ALTO_ALERO + (S.ALTO_CUMBRE - S.ALTO_ALERO) * ((S.ALERO_X - x) / (S.ALERO_X - S.PARED_X)).coerceIn(0f, 1.3f)

    fun DrawScope.alero(m: Momento, luz: LuzSelva.Luz) {
        val az = Azar(Semilla.de("botanico", "alero"))
        val p = luz.sombra
        val cerca = 1.4f
        val bajo = poligono(listOf(pc(S.ALERO_X, S.ALTO_ALERO, S.ALERO_Z), pc(S.PARED_X, S.ALTO_CUMBRE, S.ALERO_Z), pc(S.PARED_X, S.ALTO_CUMBRE, cerca), pc(S.ALERO_X, S.ALTO_ALERO, cerca)))
        val canizo = lerp(C.CANAMO, C.MADERA, 0.3f)
        // El envés del tejado: el cañizo pardo, más oscuro hacia la casa y con el rebote de la luz del jardín junto al canto.
        drawPath(bajo, Brush.linearGradient(listOf(p.por(canizo, -0.8f), p.por(canizo, -0.5f), p.por(lerp(canizo, C.ARENA, 0.25f), -0.1f)), pc(-2.5f, 3f, 3f), pc(S.ALERO_X, S.ALTO_ALERO, 3.8f)))
        clipPath(bajo) {
            // El cañizo, en cañas finas a lo largo; y los cabios, rollizos de la pared al canto, con su veta y su filo claro.
            var x = S.PARED_X; var i = 0
            while (x < S.ALERO_X) { drawLine((if (i % 3 == 0) p.por(lerp(canizo, C.ARENA, 0.3f), -0.3f) else p.sombra(canizo)).copy(alpha = 0.5f), pc(x, tejado(x), cerca), pc(x, tejado(x), S.ALERO_Z), 0.6f); x += 0.05f; i++ }
            var z = cerca + 0.25f
            while (z < S.ALERO_Z) {
                val a = pc(S.ALERO_X + 0.12f, S.ALTO_ALERO - 0.02f, z); val b = pc(S.PARED_X, S.ALTO_CUMBRE, z)
                val a2 = pc(S.ALERO_X + 0.12f, S.ALTO_ALERO - 0.13f, z + 0.05f); val b2 = pc(S.PARED_X, S.ALTO_CUMBRE - 0.13f, z + 0.05f)
                drawPath(poligono(a, b, b2, a2), p.por(C.MADERA_OSC, -0.5f))
                drawLine(p.por(lerp(C.MADERA, C.ARENA, 0.3f), 0f).copy(alpha = 0.7f), a2, b2, (k(z) * 0.02f).coerceAtLeast(0.5f))
                z += 0.48f
            }
            // Una telaraña en el rincón de un cabio, con la araña («una araña enorme lo mira desde el techo»).
            val t = pc(-0.9f, tejado(-0.9f) - 0.05f, 3.3f)
            for (j in 0..6) { val a = PI.toFloat() * 0.15f + j * 0.18f; drawLine(Color.White.copy(alpha = 0.18f), t, t + Offset(cos(a) * 16f, sin(a) * 16f), 0.3f) }
            for (r in listOf(5f, 9f, 13f)) drawArc(Color.White.copy(alpha = 0.14f), 25f, 70f, false, t - Offset(r, r), Size(r * 2f, r * 2f), style = Stroke(0.3f))
            drawCircle(p.sombra(C.BREA), 1.3f, t + Offset(6f, 5f)); for (j in -2..2) drawLine(p.sombra(C.BREA), t + Offset(6f, 5f), t + Offset(6f + j * 1.2f, 5f + (if (j == 0) 3f else 2f)), 0.3f)
            // De noche, la lámpara alumbra el alero desde abajo.
            if (luz.lampara > 0.05f) charcoDeLuz(Foco(CercaSelva.LAMPARA, C.LUMBRE, luz.lampara, 70f), CercaSelva.LAMPARA + Offset(0f, -90f), 70f, 40f, canizo, 0.7f)
        }
        // La tabla del canto (la del piñón) y las tejas rojas del alero, con sus bocas.
        val t0 = pc(S.ALERO_X, S.ALTO_ALERO, S.ALERO_Z); val t1 = pc(S.PARED_X, S.ALTO_CUMBRE, S.ALERO_Z)
        drawLine(p.por(C.MADERA, 0.2f), t0, t1, 3f)
        drawLine(p.luz(lerp(C.MADERA, C.ARENA, 0.3f)), t0 + Offset(0f, 1.2f), t1 + Offset(0f, 1.2f), 0.7f)
        // Las tejas del piñón, de lado: una hilera de lomos rojos sobre la tabla.
        for (j in 0..9) { val q = t0 + (t1 - t0) * (j / 9f); val r = 3.2f; drawArc(luz.p.por(C.TEJA, 0.2f), 180f, 180f, true, Offset(q.x - r, q.y - r * 1.4f), Size(r * 2f, r * 1.6f)); drawArc(luz.p.luz(lerp(C.TEJA, C.ARENA, 0.3f)).copy(alpha = 0.6f), 200f, 60f, false, Offset(q.x - r, q.y - r * 1.4f), Size(r * 2f, r * 1.6f), style = Stroke(0.6f)) }
        // Las bocas de las tejas del alero, a lo largo del canto: medios caños con el hueco oscuro.
        var z = cerca + 0.1f
        while (z < S.ALERO_Z + 0.01f) {
            val q = pc(S.ALERO_X, S.ALTO_ALERO, z); val r = k(z) * 0.07f
            drawArc(luz.p.por(C.TEJA, 0.1f), 180f, 180f, true, Offset(q.x - r * 0.55f, q.y - r * 0.9f), Size(r * 1.1f, r * 1.8f))
            drawArc(p.sombra(lerp(C.TEJA, C.BREA, 0.55f)), 180f, 180f, true, Offset(q.x - r * 0.3f, q.y - r * 0.5f), Size(r * 0.6f, r * 1.0f))
            z += 0.22f
        }
        // Lo que cuelga del alero: los cestos de orquídeas, la bromelia en su tabla y el manojo de plantas secándose.
        with(PiezasBotanico) {
            for ((i, c) in listOf(V3(-1.55f, 0f, 3.85f), V3(-0.95f, 0f, 3.45f)).withIndex()) {
                val g = pc(c.x, tejado(c.x) - 0.08f, c.z); val e = k(c.z); val baja = (tejado(c.x) - 2.2f + 0.2f * i) * e
                val q = Offset(g.x, g.y + baja)
                val pin = LuzSelva.pincelEn(luz, q, LuzSelva.alumbrado(luz, c.x, 2.1f, c.z))
                orquideaEnCesto(pin, g, baja, e * 0.26f, Azar(Semilla.de("botanico", "orquidea", i)), 0f, flores = 3 - i, contraluz = BovedaSelva.contraluz(luz, q) * 0.6f)
            }
            // La bromelia atada a una tabla de corteza, colgada de un alambre.
            val g = pc(-0.72f, tejado(-0.72f) - 0.05f, 3.0f); val e = k(3f); val q = g + Offset(0f, e * 0.5f)
            val pin = LuzSelva.pincelEn(luz, q, LuzSelva.alumbrado(luz, -0.72f, 2.2f, 3.0f))
            drawLine(pin.sombra(C.HIERRO), g, q + Offset(0f, -e * 0.12f), 0.5f)
            drawPath(poligono(q + Offset(-e * 0.07f, -e * 0.13f), q + Offset(e * 0.07f, -e * 0.12f), q + Offset(e * 0.06f, e * 0.16f), q + Offset(-e * 0.08f, e * 0.15f)), pin.por(lerp(C.MADERA_OSC, C.ROCA, 0.3f), -0.1f))
            bromelia(pin, q, e * 0.3f, Azar(Semilla.de("botanico", "bromelia tabla")), 0f, flor = true)
            // El manojo de plantas secándose, boca abajo.
            val h = pc(-1.95f, tejado(-1.95f) - 0.05f, 3.95f); val eh = k(3.95f)
            drawLine(pin.sombra(C.CANAMO), h, h + Offset(0f, eh * 0.12f), 0.6f)
            repeat(10) { val a = PI.toFloat() / 2f + az.entre(-0.35f, 0.35f); val l = eh * az.entre(0.22f, 0.36f); val b = h + Offset(0f, eh * 0.12f)
                drawLine(pin.por(lerp(C.HOJA, C.CUERO, 0.45f), az.entre(-0.4f, 0.3f)), b, b + Offset(cos(a) * l, sin(a) * l), 1.1f, StrokeCap.Round)
                drawOval(pin.por(lerp(C.HOJA, C.CUERO, 0.35f), 0.1f), Offset(b.x + cos(a) * l - 2f, b.y + sin(a) * l - 1.5f), Size(4f, 3f)) }
        }
    }
}
