package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * La obra de la capilla: los dos andamios (el de la pared del altar, de frente, y el de la pared derecha, que se viene
 * hacia quien mira y enmarca el cuadro) y las cosas del primer plano: el velero, el foco de trípode, el arcón, la mesa
 * de trabajo con el objeto del día, el cubo y el cable.
 *
 * **[Diseño propio]**: el cuento dice que el andamio tiene tres pisos de tablas unidos por escaleras de mano, con una
 * mesa de frascos en cada piso; lo demás (los tubos, las diagonales, dónde va cada cosa) es mío.
 */
internal object ObraUrbe {
    private val C = ColorUrbe
    private val K = CapillaUrbe

    /** Los tres pisos de tablas, en alto de la capilla. */
    private val PISOS = listOf(126f, 88f, 54f)

    val LUZ_TRIPODE = Offset(97f, 160f)
    val LUZ_ALTAR = Offset(225f, 106f)
    val LUZ_DERECHA = Offset(290f, 79f)
    val LUZ_VELERO = Offset(42f, 203f)
    val LUZ_FAROL = Offset(212f, 214f)

    /** Por dónde camina el gato: por delante de todo. */
    const val PISO_GATO = 271f
    private val MESA = Offset(238f, 226f)

    // ---------------------------------------------------------------- Los andamios

    fun DrawScope.andamios(m: Momento, a: Alumbrado) {
        if (EscenaUrbe.porMontar(m)) pila(a)
        if (EscenaUrbe.andamioDelAltar(m)) deFrente(m, a)
        if (EscenaUrbe.hayAndamio(m)) deLado(m, a)
    }

    private fun DrawScope.tubo(a: Alumbrado, desde: Offset, hasta: Offset, grueso: Float, mira: Float = 0f) {
        val medio = (desde + hasta) / 2f
        drawLine(a.en(medio, lerp(C.ACERO, C.HIERRO, 0.3f), mira), desde, hasta, grueso, StrokeCap.Round)
        drawLine(a.en(medio, lerp(C.ACERO, Color.White, 0.5f), mira).copy(alpha = 0.7f), desde + Offset(-grueso * 0.22f, 0f), hasta + Offset(-grueso * 0.22f, 0f), grueso * 0.3f, StrokeCap.Round)
    }

    /** Una lámpara de obra sujeta a un tubo, que mira hacia [hacia]: encendida, su cara es lo más claro del cuadro. */
    private fun DrawScope.lampara(a: Alumbrado, en: Offset, hacia: Float, encendida: Boolean, k: Float = 1f) {
        drawRect(a.en(en, C.HIERRO), en + Offset(-3.4f * k, -2.6f * k), Size(6.8f * k, 5.2f * k))
        drawRect(if (encendida) Color(0xFFFFFBEA) else a.en(en, C.ACERO), en + Offset((if (hacia < 0f) -3.4f else 2.2f) * k, -2.2f * k), Size(1.2f * k, 4.4f * k))
        if (encendida) resplandor(en + Offset(hacia * 3f * k, 0f), 12f * k, 0.6f, C.FOCO)
    }

    /** El andamio de la pared del altar, visto de frente: salva el altar y sube hasta la luneta. */
    private fun DrawScope.deFrente(m: Momento, a: Alumbrado) {
        val xs = listOf(222f, 258f); val t0 = 6f; val t1 = 24f; val x0 = 220f
        // Las tablas que protegen el suelo.
        drawPath(poligono(K.p(x0 - 2f, K.SUELO, 2f), K.p(262f, K.SUELO, 2f), K.p(262f, K.SUELO, 30f), K.p(x0 - 2f, K.SUELO, 30f)), a.en(K.p(182f, K.SUELO, 16f), lerp(C.MADERA_OSC, C.HOLLIN, 0.3f)).copy(alpha = 0.55f))
        for (x in xs) tubo(a, K.p(x, K.SUELO, t0), K.p(x, 40f, t0), 1.5f)
        for ((i, y) in PISOS.withIndex()) {
            // Por debajo de los ojos se ve la tabla por arriba; por encima, por abajo.
            drawPath(poligono(K.p(x0, y, t0), K.p(260f, y, t0), K.p(260f, y, t1), K.p(x0, y, t1)), a.en(K.p(182f, y, 15f), lerp(C.MADERA_OSC, C.HOLLIN, 0.35f)))
            val f0 = K.p(x0 - 1f, y - 1f, t1); val f1 = K.p(261f, y + 3.4f, t1)
            planoH(a, poligono(f0, Offset(f1.x, f0.y), f1, Offset(f0.x, f1.y)), f0.x, f1.x, f0.y, C.MADERA)
            for (k in 1..5) drawLine(a.en(f0, C.MADERA_OSC).copy(alpha = 0.6f), Offset(f0.x + (f1.x - f0.x) * k / 6f + (i * 7f) % 9f, f0.y), Offset(f0.x + (f1.x - f0.x) * k / 6f + (i * 7f) % 9f, f1.y), 0.5f)
            // La barandilla y el rodapié.
            drawLine(a.en(f0, lerp(C.MADERA, C.CAL, 0.4f)), f0, Offset(f1.x, f0.y), 0.7f); drawLine(a.en(f0, C.MADERA_OSC), Offset(f0.x, (f0.y + f1.y) / 2f), Offset(f1.x, (f0.y + f1.y) / 2f + 0.4f), 0.4f)
            tubo(a, K.p(x0, y - 11f, t1), K.p(260f, y - 11f, t1), 1.2f); tubo(a, K.p(x0, y - 5.5f, t1), K.p(260f, y - 5.5f, t1), 0.9f)
        }
        // Las diagonales, alternadas, y las escaleras de mano de un piso a otro.
        val alturas = listOf(K.SUELO) + PISOS
        for (i in 0..2) tubo(a, K.p(xs[i % 2], alturas[i], t1), K.p(xs[1 - i % 2], alturas[i + 1], t1), 1f)
        for ((i, x) in listOf(246f, 234f, 247f).withIndex()) escalera(a, K.p(x - 4f, alturas[i], t1 + 3f), K.p(x + 4f, alturas[i + 1] - 4f, t1 - 4f), 4.4f)
        for (x in xs) { tubo(a, K.p(x, K.SUELO, t1), K.p(x, 38f, t1), 2.4f); drawRect(a.en(K.p(x, K.SUELO, t1), C.HIERRO), K.p(x, K.SUELO, t1) + Offset(-2.6f, -1f), Size(5.2f, 1.6f)) }
        // En el primer piso, la mesa de los frascos; colgados de un tubo, los cascos; y la lámpara.
        val mesa = K.p(236f, 126f, t1 - 2f)
        drawRect(a.en(mesa, C.MADERA), mesa + Offset(-9f, -9.4f), Size(18f, 1.6f)); for (dx in listOf(-8f, 7f)) drawLine(a.en(mesa, C.MADERA_OSC), mesa + Offset(dx, -8f), mesa + Offset(dx, -0.6f), 0.8f)
        for ((k, c) in listOf(C.AZUL, C.CAL, C.OCRE, C.ROJO).withIndex()) drawRect(a.en(mesa, c), mesa + Offset(-7f + k * 4f, -13.4f + (k % 2)), Size(2.6f, 4f - (k % 2)))
        casco(a, K.p(222f, 150f, t1) + Offset(-3.4f, 0f), 3.6f)
        if (EscenaUrbe.conSamir(m)) casco(a, K.p(222f, 159f, t1) + Offset(-3.4f, 0f), 3.6f, C.CAL)
        // Un cubo en el segundo piso.
        val cubo = K.p(250f, 88f, t1 - 2f); drawPath(poligono(cubo + Offset(-3.4f, -7f), cubo + Offset(3.4f, -7f), cubo + Offset(2.6f, -0.8f), cubo + Offset(-2.6f, -0.8f)), a.en(cubo, C.ACERO))
        lampara(a, LUZ_ALTAR, -1f, EscenaUrbe.focosDeObra(m))
        // Una malla verde entre el primer piso y el segundo, que guarda del polvo lo ya limpio.
        val malla = poligono(K.p(223f, 90f, t1), K.p(257f, 90f, t1), K.p(257f, 124f, t1), K.p(223f, 124f, t1))
        drawPath(malla, a.en(K.p(240f, 108f, t1), C.VERDE).copy(alpha = 0.32f))
        for (k in 1..7) { drawLine(a.en(K.p(240f, 108f, t1), C.VERDE).copy(alpha = 0.45f), K.p(223f + k * 4.25f, 90f, t1), K.p(223f + k * 4.25f, 124f, t1), 0.4f); if (k < 7) drawLine(a.en(K.p(240f, 108f, t1), C.VERDE).copy(alpha = 0.45f), K.p(223f, 90f + k * 4.86f, t1), K.p(257f, 90f + k * 4.86f, t1), 0.4f) }
    }

    /** El andamio de la pared derecha, que se viene hacia quien mira: su último tramo cruza el cuadro de abajo arriba. */
    private fun DrawScope.deLado(m: Momento, a: Alumbrado) {
        val xp = 258f; val xf = 247f; val ts = listOf(34f, 78f, 122f, 160f); val tFin = 172f
        drawPath(poligono(K.p(xf - 2f, K.SUELO, 30f), K.p(xp + 3f, K.SUELO, 30f), K.p(xp + 3f, K.SUELO, tFin), K.p(xf - 2f, K.SUELO, tFin)), a.en(K.p(250f, K.SUELO, 110f), lerp(C.MADERA_OSC, C.HOLLIN, 0.25f)).copy(alpha = 0.6f))
        for (t in ts) tubo(a, K.p(xp, K.SUELO, t), K.p(xp, 40f, t), 1.2f * K.s(t), -1f)
        for (y in PISOS) {
            val bajo = poligono(K.p(xf, y, ts[0] - 2f), K.p(xp + 4f, y, ts[0] - 2f), K.p(xp + 4f, y, tFin), K.p(xf, y, tFin))
            drawPath(bajo, a.en(K.p(250f, y, 110f), lerp(C.MADERA_OSC, C.HOLLIN, 0.3f)))
            clipPath(bajo) { for (t in listOf(60f, 100f, 140f, 176f)) drawLine(a.en(K.p(250f, y, t), C.HOLLIN).copy(alpha = 0.5f), K.p(xf, y, t), K.p(xp + 4f, y, t), 0.7f); for (x in listOf(246f, 252f)) drawLine(a.en(K.p(x, y, 110f), C.HOLLIN).copy(alpha = 0.4f), K.p(x, y, ts[0]), K.p(x, y, tFin), 0.5f) }
            // El canto de la tabla de fuera, a la luz, y la barandilla.
            val canto = poligono(K.p(xf, y - 0.8f, ts[0] - 2f), K.p(xf, y + 2.4f, ts[0] - 2f), K.p(xf, y + 2.4f, tFin), K.p(xf, y - 0.8f, tFin))
            planoH(a, canto, K.p(xf, y, ts[0]).x, K.p(xf, y, tFin).x, K.p(xf, y, 110f).y, C.MADERA, mira = -1f)
            drawLine(a.en(K.p(xf, y, 110f), lerp(C.MADERA, C.CAL, 0.45f), -1f), K.p(xf, y - 0.8f, ts[0] - 2f), K.p(xf, y - 0.8f, tFin), 0.7f); drawLine(a.en(K.p(xf, y, 110f), C.MADERA_OSC, -1f), K.p(xf, y + 0.8f, ts[0] - 2f), K.p(xf, y + 0.8f, tFin), 0.4f)
            for (dy in listOf(11f, 5.5f)) for (i in 0 until ts.size) { val t1 = if (i == ts.size - 1) tFin else ts[i + 1]; tubo(a, K.p(xf, y - dy, ts[i]), K.p(xf, y - dy, t1), 0.6f * K.s(ts[i]), -1f) }
            for (t in ts) tubo(a, K.p(xf, y + 2.4f, t), K.p(xp, y + 2.4f, t), 0.6f * K.s(t), -1f)
        }
        val alturas = listOf(K.SUELO) + PISOS
        for (i in 0..2) tubo(a, K.p(xf, alturas[i], ts[i]), K.p(xf, alturas[i + 1], ts[i + 1]), 0.6f * K.s(ts[i]), -1f)
        // La mesa de frascos del primer piso asoma por encima de la barandilla; debajo, la escalera de mano.
        val mesa = K.p(xf + 3f, 126f, 100f)
        for ((k, c) in listOf(C.CAL, C.AZUL, C.OCRE).withIndex()) drawRect(a.en(mesa, c, -1f), mesa + Offset(k * 5f, -8f - k % 2 * 2f), Size(3.6f, 8f + k % 2 * 2f))
        escalera(a, K.p(xf + 4f, K.SUELO, 140f), K.p(xf + 4f, 122f, 152f), 6f * K.s(146f))
        for (t in ts) {
            tubo(a, K.p(xf, K.SUELO, t), K.p(xf, 38f, t), 1.7f * K.s(t), -1f)
            val pie = K.p(xf, K.SUELO, t); drawRect(a.en(pie, C.HIERRO), pie + Offset(-2.2f * K.s(t), -0.8f * K.s(t)), Size(4.4f * K.s(t), 1.4f * K.s(t)))
            // Las abrazaderas en cada cruce.
            for (y in PISOS) { val q = K.p(xf, y + 2.4f, t); drawRect(a.en(q, lerp(C.HIERRO, C.NARANJA, 0.35f), -1f), q + Offset(-1.3f * K.s(t), -1.3f * K.s(t)), Size(2.6f * K.s(t), 2.6f * K.s(t))) }
        }
        // La malla del tramo del fondo y, en el primer piso, un cubo y un taburete que asoman.
        val red = poligono(K.p(xf, 90f, ts[0]), K.p(xf, 90f, ts[1]), K.p(xf, 124f, ts[1]), K.p(xf, 124f, ts[0]))
        drawPath(red, a.en(K.p(xf, 108f, 56f), C.VERDE, -1f).copy(alpha = 0.32f))
        for (k in 1..5) drawLine(a.en(K.p(xf, 108f, 56f), C.VERDE, -1f).copy(alpha = 0.45f), K.p(xf, 90f, ts[0] + k * 7.3f), K.p(xf, 124f, ts[0] + k * 7.3f), 0.4f)
        val q = K.p(xf + 2f, 126f, 136f); val k = K.s(136f)
        drawPath(poligono(q + Offset(-2.4f * k, -6.4f * k), q + Offset(2.4f * k, -6.4f * k), q + Offset(1.8f * k, -1f * k), q + Offset(-1.8f * k, -1f * k)), a.en(q, C.ACERO, -1f))
        val b = K.p(xf + 2f, 126f, 150f); drawRect(a.en(b, C.MADERA, -1f), b + Offset(-3f * k, -6.6f * k), Size(6f * k, 1.2f * k)); for (dx in listOf(-2.4f, 2.4f)) drawLine(a.en(b, C.MADERA_OSC, -1f), b + Offset(dx * k, -5.6f * k), b + Offset(dx * k, -0.8f * k), 0.9f)
        casco(a, K.p(xf, 150f, 122f) + Offset(-4f, 0f), 5f, if (EscenaUrbe.trabajando(m)) C.CAL else C.NARANJA)
        if (EscenaUrbe.conSamir(m)) lampara(a, LUZ_DERECHA, 1f, EscenaUrbe.focosDeObra(m), 1.5f)
    }

    private fun DrawScope.escalera(a: Alumbrado, pie: Offset, cabeza: Offset, ancho: Float) {
        val c = a.en((pie + cabeza) / 2f, lerp(C.ACERO, C.CAL, 0.2f)); val lado = Offset(ancho, 0f)
        drawLine(c, pie - lado, cabeza - lado, 0.9f); drawLine(c, pie + lado, cabeza + lado, 0.9f)
        for (k in 1..7) { val q = pie + (cabeza - pie) * (k / 8f); drawLine(c, q - lado, q + lado, 0.7f) }
    }

    private fun DrawScope.casco(a: Alumbrado, en: Offset, r: Float, color: Color = C.NARANJA) {
        drawPath(Path().apply { moveTo(en.x - r, en.y); cubicTo(en.x - r, en.y - r * 1.5f, en.x + r, en.y - r * 1.5f, en.x + r, en.y); close() }, a.en(en, color))
        drawLine(a.en(en, lerp(color, C.HOLLIN, 0.3f)), en + Offset(-r * 1.25f, 0f), en + Offset(r * 1.1f, 0f), r * 0.3f, StrokeCap.Round)
    }

    /** Antes de montarlo: los tubos y las tablas, apilados delante del altar. */
    private fun DrawScope.pila(a: Alumbrado) {
        val az = Azar(Semilla.de("urbe", "pila"))
        for (k in 0..5) { val y = K.p(0f, K.SUELO, 52f + k * 2f).y - k * 1.6f; drawRect(a.en(Offset(200f, y), if (k % 2 == 0) C.MADERA else lerp(C.MADERA, C.MADERA_OSC, 0.4f)), Offset(118f + az.entre(-3f, 3f), y - 2f), Size(92f, 2.4f)) }
        for (k in 0..8) { val y = K.p(0f, K.SUELO, 70f).y - (k / 3) * 2.6f; val x = 196f + (k % 3) * 2f; tubo(a, Offset(x, y - 1f), Offset(x + 78f, y - 3f + az.entre(-1f, 1f)), 1.8f) }
        for (k in 0..4) drawCircle(a.en(Offset(180f, 226f), lerp(C.HIERRO, C.NARANJA, 0.4f)), 2.2f, Offset(170f + k * 5f + az.entre(-1f, 1f), 228f + az.entre(-2f, 2f)))
    }

    // ---------------------------------------------------------------- El primer plano

    /** Dónde está la llama de cada vela del velero. */
    private fun velasDelVelero(): List<Offset> { val az = Azar(Semilla.de("urbe", "velero")); return (0..8).map { Offset(27.6f + it * 3.6f, 212f - az.entre(3f, 9f) - (if (it % 2 == 0) 0f else 4f)) } }

    fun DrawScope.llamasDelVelero(m: Momento, reloj: Float) {
        val cuantas = if (EscenaUrbe.fiesta(m) || EscenaUrbe.luto(m)) 9 else 6
        resplandor(LUZ_VELERO, 20f, 0.5f, C.VELA)
        for ((i, v) in velasDelVelero().withIndex()) if (i < cuantas || i == 8) llama(v, 4.6f, reloj, 7 + i)
    }

    fun DrawScope.llamaDelFarol(m: Momento, reloj: Float) { resplandor(LUZ_FAROL, 12f, 0.5f, C.VELA); llama(LUZ_FAROL + Offset(0f, 3f), 4f, reloj, 3) }

    private fun cosas(m: Momento, a: Alumbrado): List<Cosa> = buildList {
        val obra = EscenaUrbe.hayAndamio(m) || EscenaUrbe.porMontar(m)
        // El velero: una bandeja de hierro sobre tres patas, con las velas que deja la gente.
        add(Cosa(poligono(Offset(26f, 206f), Offset(58f, 206f), Offset(54f, 252f), Offset(30f, 252f)), Offset(42f, 252f)) {
            val hierro = a.en(Offset(42f, 230f), C.HIERRO)
            for ((x0, x1) in listOf(30f to 36f, 54f to 48f, 42f to 42f)) drawLine(hierro, Offset(x0, 252f), Offset(x1, 216f), 1.5f, StrokeCap.Round)
            drawLine(hierro, Offset(33f, 236f), Offset(51f, 236f), 1f)
            drawRect(hierro, Offset(25f, 212f), Size(34f, 4.4f)); drawRect(a.en(Offset(42f, 213f), lerp(C.HIERRO, C.CERA, 0.4f)), Offset(25f, 212f), Size(34f, 1.1f))
            // Chorreras de cera en el borde.
            for (x in listOf(28f, 37f, 46f, 55f)) drawLine(a.en(Offset(x, 216f), C.CERA), Offset(x, 213f), Offset(x, 218f + (x % 3f)), 1f, StrokeCap.Round)
            for (v in velasDelVelero()) drawRect(a.en(v, C.CERA), Offset(v.x - 0.9f, v.y + 1f), Size(1.8f, 212f - v.y - 1f))
        })
        if (obra && !EscenaUrbe.porMontar(m)) {
            // El foco de trípode, que mira a la pared del altar.
            add(Cosa(poligono(listOf(Offset(88f, 150f), Offset(107f, 150f), Offset(98f, 200f), Offset(106f, 236f), Offset(80f, 236f), Offset(90f, 200f))), Offset(93f, 236f)) {
                val en = Offset(93f, 200f)
                for (x in listOf(80f, 106f, 95f)) drawLine(a.en(en, C.HIERRO), Offset(93f, 198f), Offset(x, if (x == 95f) 240f else 236f), 1.5f, StrokeCap.Round)
                drawLine(a.en(en, lerp(C.ACERO, C.HIERRO, 0.4f)), Offset(93f, 200f), Offset(93f, 166f), 2f); drawLine(a.en(en, C.ACERO), Offset(92.4f, 200f), Offset(92.4f, 166f), 0.6f)
                val encendido = EscenaUrbe.focosDeObra(m)
                val caja = poligono(Offset(89f, 154f), Offset(102f, 152f), Offset(103f, 163f), Offset(90f, 165f))
                drawPath(caja, a.en(Offset(92f, 158f), C.HIERRO, -1f)); drawPath(caja, a.en(Offset(92f, 158f), C.NARANJA, -1f), style = Stroke(1.1f))
                drawPath(poligono(Offset(102f, 152f), Offset(106f, 153.4f), Offset(107f, 162f), Offset(103f, 163f)), if (encendido) Color(0xFFFFFBEA) else a.en(Offset(106f, 158f), C.ACERO, 1f))
                drawLine(a.en(Offset(93f, 166f), C.HIERRO), Offset(90f, 165f), Offset(93f, 168f), 1.2f); drawLine(a.en(Offset(93f, 166f), C.HIERRO), Offset(101f, 163.4f), Offset(93f, 168f), 1.2f)
                if (encendido) resplandor(LUZ_TRIPODE + Offset(12f, -2f), 22f, 0.7f, C.FOCO)
            })
            // El cubo de zinc y la garrafa del agua desmineralizada.
            add(Cosa(poligono(Offset(160f, 246f), Offset(190f, 244f), Offset(190f, 264f), Offset(160f, 264f)), Offset(174f, 264f)) {
                val cubo = Path().apply { moveTo(160f, 250f); lineTo(175f, 250f); lineTo(173f, 264f); lineTo(162f, 264f); close() }
                cuerpo(a, cubo, 160f, 175f, 257f, C.ACERO); drawOval(a.en(Offset(167f, 250f), lerp(C.ACERO, C.HOLLIN, 0.5f)), Offset(160f, 248.4f), Size(15f, 3.2f))
                drawArc(a.en(Offset(167f, 246f), C.HIERRO), 180f, 180f, false, Offset(161f, 243f), Size(13f, 13f), style = Stroke(0.7f))
                val garrafa = Path().apply { moveTo(178f, 246f); lineTo(183f, 244f); lineTo(188f, 246f); lineTo(189f, 264f); lineTo(177f, 264f); close() }
                drawPath(garrafa, a.en(Offset(183f, 255f), lerp(C.CAL, C.AZUL, 0.25f)).copy(alpha = 0.85f)); drawRect(a.en(Offset(183f, 258f), lerp(C.AZUL, C.CAL, 0.5f)).copy(alpha = 0.6f), Offset(177.6f, 253f), Size(11f, 11f)); drawRect(a.en(Offset(183f, 243f), C.AZUL), Offset(181.4f, 241.6f), Size(3.2f, 2.6f))
            })
        }
        // El arcón de la sacristía, donde se guardan los frascos con llave.
        add(Cosa(poligono(Offset(112f, 228f), Offset(164f, 228f), Offset(164f, 264f), Offset(112f, 264f)), Offset(138f, 264f)) {
            val frente = poligono(Offset(112f, 237f), Offset(164f, 237f), Offset(164f, 264f), Offset(112f, 264f))
            planoH(a, frente, 112f, 164f, 250f, C.MADERA_OSC)
            drawPath(poligono(Offset(117f, 230f), Offset(160f, 230f), Offset(164f, 237f), Offset(112f, 237f)), a.en(Offset(138f, 233f), lerp(C.MADERA_OSC, C.MADERA, 0.5f)))
            for (y in listOf(244f, 251f, 258f)) drawLine(a.en(Offset(138f, y), C.HOLLIN).copy(alpha = 0.45f), Offset(112f, y), Offset(164f, y), 0.5f)
            for (x in listOf(120f, 156f)) { drawRect(a.en(Offset(x, 250f), C.HIERRO), Offset(x - 1.6f, 237f), Size(3.2f, 27f)); drawLine(a.en(Offset(x, 233f), C.HIERRO), Offset(x - 0.6f, 230f), Offset(x - 1.6f, 237f), 2.6f) }
            drawRect(a.en(Offset(138f, 243f), lerp(C.HIERRO, C.ORO, 0.4f)), Offset(135.4f, 239f), Size(5.2f, 6f)); drawCircle(a.en(Offset(138f, 243f), C.HOLLIN), 0.9f, Offset(138f, 242.4f))
            // Encima, un trapo doblado.
            drawPath(poligono(Offset(118f, 231f), Offset(131f, 230.4f), Offset(132f, 234f), Offset(117f, 234.6f)), a.en(Offset(124f, 232f), lerp(C.CAL, C.AZUL, 0.2f)))
        })
        // La mesa de trabajo, de caballetes; en la capilla abierta, con su paño.
        add(Cosa(poligono(Offset(186f, 221f), Offset(290f, 221f), Offset(298f, 266f), Offset(180f, 266f)), Offset(240f, 266f)) { mesa(m, a) })
        if (EscenaUrbe.fiesta(m)) add(Cosa(poligono(Offset(66f, 232f), Offset(82f, 232f), Offset(80f, 264f), Offset(68f, 264f)), Offset(74f, 264f)) {
            // Un jarrón de flores al pie del velero.
            val jarron = Path().apply { moveTo(70f, 264f); cubicTo(64f, 256f, 68f, 246f, 71.4f, 244f); lineTo(76.6f, 244f); cubicTo(80f, 246f, 84f, 256f, 78f, 264f); close() }
            cuerpo(a, jarron, 66f, 82f, 254f, C.AZUL)
            val az = Azar(Semilla.de("urbe", "jarron"))
            repeat(11) { val q = Offset(74f + az.entre(-9f, 9f), 236f + az.entre(-8f, 5f)); drawLine(a.en(q, C.VERDE), Offset(74f, 244f), q, 0.6f); drawCircle(a.en(q, listOf(C.CAL, C.ROJO, C.ORO)[it % 3]), 1.9f, q) }
        })
        if (EscenaUrbe.abierta(m)) add(Cosa(poligono(Offset(306f, 222f), Offset(376f, 222f), Offset(378f, 266f), Offset(304f, 266f)), Offset(340f, 266f)) {
            // Con la capilla abierta, un banco para quien entra a mirar.
            planoH(a, poligono(Offset(306f, 222f), Offset(376f, 222f), Offset(376f, 244f), Offset(306f, 244f)), 306f, 376f, 232f, C.MADERA_OSC)
            for (x in listOf(318f, 341f, 364f)) drawLine(a.en(Offset(x, 232f), C.HOLLIN).copy(alpha = 0.4f), Offset(x, 222f), Offset(x, 244f), 0.6f)
            drawPath(poligono(Offset(304f, 244f), Offset(378f, 244f), Offset(380f, 250f), Offset(302f, 250f)), a.en(Offset(340f, 247f), C.MADERA))
            for (x in listOf(306f, 372f)) drawRect(a.en(Offset(x, 258f), C.MADERA_OSC), Offset(x, 250f), Size(4f, 16f))
        })
    }

    private fun DrawScope.mesa(m: Momento, a: Alumbrado) {
        val abierta = EscenaUrbe.abierta(m)
        // Los caballetes.
        for (x in listOf(198f, 282f)) { val c = a.en(Offset(x, 250f), C.MADERA_OSC); drawLine(c, Offset(x, 234f), Offset(x - 7f, 266f), 2f, StrokeCap.Round); drawLine(c, Offset(x, 234f), Offset(x + 7f, 266f), 2f, StrokeCap.Round); drawLine(c, Offset(x - 4.6f, 254f), Offset(x + 4.6f, 254f), 1.3f) }
        val tablero = poligono(Offset(188f, 221f), Offset(288f, 221f), Offset(297f, 233f), Offset(181f, 233f))
        planoH(a, tablero, 181f, 297f, 227f, if (abierta) C.CAL else lerp(C.MADERA, C.CAL, 0.25f))
        drawRect(a.en(Offset(240f, 234.5f), if (abierta) lerp(C.CAL, C.SOMBRA, 0.25f) else C.MADERA_OSC), Offset(181f, 233f), Size(116f, if (abierta) 9f else 2.6f))
        if (!abierta) {
            // Manchas de color y cercos de frasco: la mesa de alguien que trabaja.
            val az = Azar(Semilla.de("urbe", "mesa"))
            repeat(9) { val q = Offset(az.entre(192f, 288f), az.entre(223f, 231.4f)); drawOval(a.en(q, listOf(C.AZUL, C.OCRE, C.ROJO, C.HOLLIN)[it % 4]).copy(alpha = 0.4f), q - Offset(2.4f, 0.9f), Size(4.8f, 1.8f)) }
            // Los frascos, el bote de los pinceles y el rollo de papel japonés.
            for ((k, c) in listOf(C.AZUL, C.OCRE, C.CAL).withIndex()) { val x = 193f + k * 6.6f; val y = 223.6f + k % 2 * 1.6f
                drawRect(a.en(Offset(x, y - 5f), lerp(c, C.CAL, 0.3f)).copy(alpha = 0.9f), Offset(x, y - 9f), Size(5f, 9f)); drawRect(a.en(Offset(x, y - 9f), C.HIERRO), Offset(x - 0.3f, y - 10.4f), Size(5.6f, 1.8f)); drawLine(Color.White.copy(alpha = 0.3f), Offset(x + 1f, y - 8f), Offset(x + 1f, y - 1.4f), 0.6f) }
            val bote = Offset(280f, 224.4f)
            for ((k, dx) in listOf(-2.4f, -0.6f, 1.2f, 2.8f).withIndex()) { drawLine(a.en(bote, C.MADERA), bote + Offset(dx * 0.5f, -8f), bote + Offset(dx * 1.7f, -19f - k % 2 * 3f), 0.8f); drawLine(a.en(bote, if (k % 2 == 0) C.HOLLIN else C.OCRE), bote + Offset(dx * 1.7f, -19f - k % 2 * 3f), bote + Offset(dx * 1.9f, -22f - k % 2 * 3f), 1.2f) }
            cuerpo(a, poligono(bote + Offset(-3.6f, -9f), bote + Offset(3.6f, -9f), bote + Offset(3f, 0f), bote + Offset(-3f, 0f)), bote.x - 3.6f, bote.x + 3.6f, bote.y - 4f, C.COTTO)
            drawRoundRect(a.en(Offset(262f, 228f), C.CAL), Offset(254f, 226.4f), Size(18f, 4.4f), androidx.compose.ui.geometry.CornerRadius(2.2f)); drawOval(a.en(Offset(254f, 228f), lerp(C.CAL, C.SOMBRA, 0.3f)), Offset(252.6f, 226.4f), Size(3f, 4.4f))
        }
        if (EscenaUrbe.sinLuz(m)) {
            // El día sin luz: un farol de vela sobre la mesa.
            val f = LUZ_FAROL; drawRect(a.en(f, C.HIERRO), f + Offset(-3.6f, 8.6f), Size(7.2f, 1.4f)); drawRect(Color(0xFFFFE6B0).copy(alpha = 0.5f), f + Offset(-3f, -2f), Size(6f, 10.6f))
            for (dx in listOf(-3f, 3f)) drawLine(a.en(f, C.HIERRO), f + Offset(dx, -2f), f + Offset(dx, 8.6f), 0.7f); drawPath(poligono(f + Offset(-4f, -2f), f + Offset(4f, -2f), f + Offset(0f, -6f)), a.en(f, C.HIERRO)); drawRect(a.en(f, C.CERA), f + Offset(-0.9f, 3.4f), Size(1.8f, 5.2f))
        }
        with(ObjetosUrbe) { objeto(m.ficha.objeto, MESA, 1.25f) { a.en(MESA, it) } }
    }

    fun DrawScope.primerPlano(m: Momento, a: Alumbrado, f: List<Foco>) {
        val cosas = cosas(m, a).sortedBy { it.pie.y }
        // Las sombras: a cada cosa la tumba la luz que más la alumbra (la ventana de día, el foco, las velas).
        capaSombra(rayoFijo(-0.6f, 40f, 0.62f, C.SOMBRA)) { c ->
            clipPath(K.formaDelSuelo()) { for (k in cosas) EscenaUrbe.rayoEn(f, k.pie, 1f)?.let { r -> sombraArrojada(r, k.forma, k.pie, c.copy(alpha = r.fuerza), K.SUELO, K.SUELO) } }
        }
        if (EscenaUrbe.hayAndamio(m)) {
            // El cable naranja, del foco al andamio, por el suelo.
            val cable = Path().apply { moveTo(95f, 240f); cubicTo(120f, 270f, 160f, 268f, 200f, 270f); cubicTo(250f, 273f, 290f, 262f, 316f, 256f) }
            drawPath(cable, a.en(Offset(200f, 268f), lerp(C.NARANJA, C.HOLLIN, 0.15f)), style = Stroke(1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        for (k in cosas) { val b = k.forma.getBounds(); drawOval(C.SOMBRA.copy(alpha = 0.34f), Offset(b.left - 1f, k.pie.y - 2.4f), Size(b.width + 2f, 5f)) }
        cosas.forEach { it.pintar(this) }
    }

    // ---------------------------------------------------------------- El gato

    fun estorbos(m: Momento): List<Float> = listOf(42f, 93f, 174f)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 16f, PISO_GATO, hasta = 364f))
        add(Ancla(TipoAncla.MIRADOR, 146f, 230.6f))                              // encima del arcón
        add(Ancla(TipoAncla.OBJETO, MESA.x - 10f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 268f, PISO_GATO))                            // al pie de la mesa
        if (EscenaUrbe.velas(m)) add(Ancla(TipoAncla.FUEGO, 78f, PISO_GATO, hasta = 42f))
        if (EscenaUrbe.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 74f, 262f))
    }
}
