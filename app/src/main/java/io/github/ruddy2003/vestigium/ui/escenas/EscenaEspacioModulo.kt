package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.FONDO_D
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.FONDO_I
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.SUELO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.TECHO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.VENTANA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.VENTANA_RX
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.VENTANA_RY
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.fondo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.p
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tuberia
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El módulo de la nave donde trabaja la astronauta (`EscenaEspacio`): el techo con sus lámparas, el mamparo del fondo
 * con la compuerta, las consolas y el armario del traje, los dos costados, el suelo, la barra y los cables. Todo pide
 * su color al [Alumbrado] del momento, así que cada placa es más clara cuanto más cerca está de la lámpara que manda.
 */
internal object ModuloEspacio {
    private val C = ColorEspacio
    private val E = EscenaEspacio

    private fun centro(q: List<Offset>) = Offset(q.sumOf { it.x.toDouble() }.toFloat() / q.size, q.sumOf { it.y.toDouble() }.toFloat() / q.size)
    private fun redondo(x0: Float, y0: Float, x1: Float, y1: Float, r: Float) = Path().apply { addRoundRect(RoundRect(x0, y0, x1, y1, CornerRadius(r))) }
    private fun entreDos(a: Offset, b: Offset, k: Float) = Offset(a.x + (b.x - a.x) * k, a.y + (b.y - a.y) * k)

    /**
     * Una placa del casco: el relleno, con la luz que le toca; la junta alrededor; el filo de luz en el canto de arriba;
     * los remaches y, a veces, un roce o una mancha. [q]: sus cuatro esquinas (arriba izquierda, arriba derecha, abajo
     * derecha, abajo izquierda).
     */
    private fun DrawScope.placa(a: Alumbrado, q: List<Offset>, propio: Color, mira: Float, az: Azar, lejos: Float = 0f, remaches: Boolean = true, desgaste: Float = 1f) {
        val c = centro(q); val forma = poligono(q)
        val arriba = minOf(q[0].y, q[1].y); val abajo = maxOf(q[2].y, q[3].y)
        val tono = lerp(propio, if (az.real() > 0.5f) Color.White else C.CASCO_OSC, az.real() * 0.1f)
        val alto = a.en(Offset(c.x, arriba), tono, mira, lejos); val bajo = a.en(Offset(c.x, abajo), lerp(tono, C.SOMBRA, 0.12f), mira, lejos)
        drawPath(forma, Brush.verticalGradient(listOf(alto, bajo), arriba, abajo))
        clipPath(forma) {
            val w = (q[1] - q[0]).getDistance(); val h = (q[3] - q[0]).getDistance()
            if (desgaste > 0f && az.real() < 0.55f * desgaste) repeat(1 + az.entero(3)) { val o = entreDos(entreDos(q[0], q[1], az.real()), entreDos(q[3], q[2], az.real()), az.real()); drawLine(a.en(o, lerp(tono, Color.White, 0.5f), mira, lejos).copy(alpha = 0.3f), o, o + Offset(az.entre(-0.3f, 0.3f) * w, az.entre(-0.1f, 0.1f) * h), 0.5f, StrokeCap.Round) }
            if (desgaste > 0f && az.real() < 0.4f * desgaste) { val o = entreDos(q[3], q[2], az.real()); mancha(o, w * az.entre(0.14f, 0.34f), h * az.entre(0.1f, 0.26f), C.SOMBRA, 0.16f) }
        }
        drawPath(forma, C.GOMA.copy(alpha = 0.5f), style = Stroke(0.6f, join = StrokeJoin.Round))
        drawLine(Color.White.copy(alpha = 0.2f), q[0] + Offset(0.6f, 0.7f), q[1] + Offset(-0.6f, 0.7f), 0.5f)
        if (remaches) for ((i, e) in q.withIndex()) { val r = entreDos(e, c, 0.12f); drawCircle(C.GOMA.copy(alpha = 0.45f), 0.6f, r); if (i < 2) drawCircle(Color.White.copy(alpha = 0.25f), 0.3f, r + Offset(-0.2f, -0.2f)) }
    }

    /** Una lámpara: el vidrio apagado o encendido con su [color], según [cuanto]; y el halo que deja alrededor. */
    private fun DrawScope.lampara(a: Alumbrado, q: List<Offset>, color: Color, cuanto: Float) {
        val forma = poligono(q); val k = cuanto.coerceIn(0f, 1f)
        drawPath(forma, lerp(a.l.tono(Color(0xFFB4BACA)), lerp(color, Color.White, 0.5f), kotlin.math.sqrt(k)))
        drawPath(forma, a.l.tono(lerp(C.CASCO_OSC, C.CASCO, 0.3f)), style = Stroke(1.4f, join = StrokeJoin.Round)); drawPath(forma, C.GOMA.copy(alpha = 0.35f), style = Stroke(0.5f, join = StrokeJoin.Round))
        val c = centro(q)
        if (k > 0.03f) { val w = (q[1] - q[0]).getDistance(); mancha(c, w * 1.1f, w * 0.6f, color, 0.3f * k); mancha(c + Offset(0f, 8f), w * 0.8f, w * 0.9f, color, 0.12f * k) }
        drawLine(C.GOMA.copy(alpha = 0.25f), entreDos(q[0], q[1], 0.5f), entreDos(q[3], q[2], 0.5f), 0.5f)
    }

    // ---------------------------------------------------------------- El techo

    fun DrawScope.techo(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("espacio", "techo")); val g = E.programa(m)
        val columnas = listOf(-40f, 20f, 60f, 84f, 100f, 140f, 200f, 216f, 232f, 250f, 266f, 288f, 330f, 400f)
        val filas = listOf(1f, 1.13f, 1.3f, 1.52f, 1.86f)
        for (j in 0 until filas.size - 1) for (i in 0 until columnas.size - 1) {
            val x0 = columnas[i]; val x1 = columnas[i + 1]; val s0 = filas[j]; val s1 = filas[j + 1]
            val q = listOf(p(x0, TECHO, s1), p(x1, TECHO, s1), p(x1, TECHO, s0), p(x0, TECHO, s0))
            val lamp = (j == 1 || j == 2) && (x0 == 84f || x0 == 140f || x0 == 250f)
            if (!lamp) placa(a, q, if ((i + j) % 3 == 0) lerp(C.CASCO, C.CASCO_OSC, 0.35f) else lerp(C.CASCO, C.CASCO_OSC, 0.2f), 0f, az, remaches = x1 - x0 > 20f)
        }
        // Las tres lámparas: la de la izquierda, la del centro y la de la derecha.
        val colorIzq = a.focos[0].color; val colorDer = a.focos[2].color
        for ((x0, x1, color, cuanto) in listOf(Cuatro(84f, 100f, colorIzq, g.izq), Cuatro(140f, 200f, C.LUZ_BLANCA, g.centro), Cuatro(250f, 266f, colorDer, g.der)))
            lampara(a, listOf(p(x0 + 2f, TECHO, 1.5f), p(x1 - 2f, TECHO, 1.5f), p(x1 - 2f, TECHO, 1.15f), p(x0 + 2f, TECHO, 1.15f)), color, cuanto)
        // El conducto del aire, colgado del techo: su costado, su panza con las rejillas y los aros que lo sujetan.
        val bajo = TECHO + 9f
        val lado = poligono(p(216f, TECHO, 1.9f), p(216f, TECHO, 1f), p(216f, bajo, 1f), p(216f, bajo, 1.9f))
        drawPath(lado, Brush.verticalGradient(listOf(a.en(Offset(250f, 8f), C.ACERO, -1f), a.en(Offset(214f, 50f), lerp(C.ACERO, C.SOMBRA, 0.3f), -1f)), 0f, 58f))
        val panza = poligono(p(216f, bajo, 1.9f), p(234f, bajo, 1.9f), p(234f, bajo, 1f), p(216f, bajo, 1f))
        drawPath(panza, Brush.verticalGradient(listOf(a.en(Offset(250f, 14f), lerp(C.ACERO, C.SOMBRA, 0.28f)), a.en(Offset(226f, 58f), lerp(C.ACERO, C.SOMBRA, 0.4f))), 0f, 60f))
        for (i in 0..5) { val s = fondo(i, 5, 1.02f, 1.86f); drawLine(C.GOMA.copy(alpha = 0.6f), p(216f, TECHO, s), p(216f, bajo, s), 0.5f + 0.4f * s); drawLine(C.GOMA.copy(alpha = 0.6f), p(216f, bajo, s), p(234f, bajo, s), 0.5f + 0.4f * s) }
        for (i in 0..3) { val s0 = fondo(i * 2 + 1, 9, 1.05f, 1.8f); val s1 = fondo(i * 2 + 2, 9, 1.05f, 1.8f); for (k in listOf(0.3f, 0.5f, 0.7f)) drawLine(C.GOMA.copy(alpha = 0.7f), p(216f + 18f * k, bajo, s0), p(216f + 18f * k, bajo, s1), 0.7f) }
        // La bandeja de cables: cuatro, de colores, hasta el mamparo.
        for ((i, color) in listOf(C.GOMA, C.NARANJA, C.TELA, C.GOMA, C.TELA_AZUL).withIndex()) { val wx = 64f + i * 3.2f; drawLine(a.en(p(wx, TECHO, 1.3f), color), p(wx, TECHO + 1.6f, 1f), p(wx, TECHO + 1.6f, 1.9f), 0.9f) }
        for (i in 0..4) { val s = fondo(i, 4, 1.04f, 1.8f); drawLine(a.en(p(70f, TECHO, s), C.ACERO), p(62f, TECHO + 2.4f, s), p(80f, TECHO + 2.4f, s), 0.6f * s) }
        // Unos rociadores y un detector, para que el techo no sea solo placas.
        for ((wx, s) in listOf(120f to 1.2f, 180f to 1.72f, 276f to 1.34f, 40f to 1.4f)) { val o = p(wx, TECHO, s); drawCircle(a.en(o, C.ACERO), 1.6f * s, o + Offset(0f, 1.2f * s)); drawCircle(C.GOMA, 0.7f * s, o + Offset(0f, 1.6f * s)) }
    }

    private class Cuatro(val a: Float, val b: Float, val c: Color, val d: Float) { operator fun component1() = a; operator fun component2() = b; operator fun component3() = c; operator fun component4() = d }

    // ---------------------------------------------------------------- El mamparo del fondo

    private val VANO = redondo(88f, 96f, 152f, SUELO, 15f)

    fun DrawScope.mamparo(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("espacio", "mamparo"))
        // Las placas: tres hileras, cortadas por la compuerta, las consolas y el armario.
        val xs = listOf(FONDO_I, 84f, 120f, 156f, 168f, 208f, 248f, FONDO_D); val ys = listOf(TECHO, 66f, 94f, 150f, SUELO)
        for (j in 0 until ys.size - 1) for (i in 0 until xs.size - 1) placa(a, listOf(Offset(xs[i], ys[j]), Offset(xs[i + 1], ys[j]), Offset(xs[i + 1], ys[j + 1]), Offset(xs[i], ys[j + 1])), if (j == 0) lerp(C.CASCO, C.CASCO_OSC, 0.55f) else C.CASCO, 0f, az, lejos = 0.1f)
        // Las tuberías de arriba y la cenefa de rejillas.
        tuberia(a, Offset(FONDO_I, 55.4f), Offset(166f, 55.4f), 2.2f, C.NARANJA, 0.1f); tuberia(a, Offset(FONDO_I, 60.6f), Offset(FONDO_D, 60.6f), 3f, C.ACERO, 0.1f)
        for (x in listOf(182f, 196f, 262f, 274f)) for (k in 0..2) drawLine(C.GOMA.copy(alpha = 0.6f), Offset(x, 52.6f + k * 1.6f), Offset(x + 9f, 52.6f + k * 1.6f), 0.6f)
        // Lo que quince años le han dejado al mamparo: un parche atornillado con su chorrera, una flecha de salida y dos tiras de cinta.
        val parche = Offset(104f, 74f)
        drawRect(a.en(parche, lerp(C.CASCO, Color.White, 0.2f), lejos = 0.1f), parche + Offset(-8f, -5f), Size(16f, 10f)); drawRect(C.GOMA.copy(alpha = 0.4f), parche + Offset(-8f, -5f), Size(16f, 10f), style = Stroke(0.5f))
        for (dx in listOf(-6.4f, 6.4f)) for (dy in listOf(-3.4f, 3.4f)) drawCircle(C.GOMA.copy(alpha = 0.6f), 0.6f, parche + Offset(dx, dy))
        drawRect(Brush.verticalGradient(listOf(C.SOMBRA.copy(alpha = 0.22f), Color.Transparent), 79f, 90f), Offset(100f, 79f), Size(2.2f, 11f))
        val flecha = Offset(72f, 84f)
        drawRoundRect(a.en(flecha, C.TELA, lejos = 0.1f), flecha + Offset(-6f, -3.6f), Size(12f, 7.2f), CornerRadius(1f)); drawPath(poligono(flecha + Offset(4.4f, 0f), flecha + Offset(0f, -2.6f), flecha + Offset(0f, 2.6f)), a.en(flecha, C.NARANJA)); drawRect(a.en(flecha, C.NARANJA), flecha + Offset(-4f, -0.9f), Size(4.4f, 1.8f))
        for ((o, giro) in listOf(Offset(196f, 142f) to 0.4f, Offset(160f, 100f) to -0.6f)) drawLine(a.en(o, C.NARANJA, lejos = 0.1f).copy(alpha = 0.85f), o, o + Offset(6f, giro * 3f), 1.6f)
        compuerta(m, a)
        consolas(m, a, az)
        armarioDelTraje(m, a)
        // La luz de emergencia de este lado y, en las fiestas, los banderines de pared a pared.
        aplique(a, Offset(70f, 72f), E.emergencia(m))
        if (E.fiesta(m) && !E.lamparitas(m)) banderines(E.luzDeAdorno(m, a, Offset(170f, 80f)), Offset(62f, 68f), Offset(286f, 66f), 9f, listOf(C.NARANJA, C.CIAN, C.TELA, C.LUZ_CULTIVO, C.TRIGO), 6.4f)
        // La luz de guardia, a ras del suelo.
        luzDeGuardia(Offset(FONDO_I + 2f, SUELO - 2.2f), Offset(86f, SUELO - 2.2f), E.programa(m).guardia); luzDeGuardia(Offset(154f, SUELO - 2.2f), Offset(FONDO_D - 2f, SUELO - 2.2f), E.programa(m).guardia)
    }

    /** Un aplique de emergencia: una caja con su lente ámbar. */
    fun DrawScope.aplique(a: Alumbrado, o: Offset, cuanto: Float, k: Float = 1f) {
        drawRoundRect(a.en(o, C.CASCO_OSC), o + Offset(-5f * k, -3.4f * k), Size(10f * k, 6.8f * k), CornerRadius(1.4f * k))
        drawRoundRect(lerp(Color(0xFF7A5A3A), C.AMBAR, (cuanto * 2f).coerceIn(0f, 1f)), o + Offset(-3.6f * k, -2.2f * k), Size(7.2f * k, 4.4f * k), CornerRadius(1f * k))
        if (cuanto > 0.02f) { resplandor(o, 20f * k, cuanto, C.AMBAR); drawRoundRect(Color(0xFFFFF0D0).copy(alpha = cuanto.coerceIn(0f, 1f)), o + Offset(-2f * k, -1f * k), Size(4f * k, 2f * k), CornerRadius(0.8f * k)) }
    }

    /** La tira de luz azul que corre por el pie de las paredes cuando baja la luz. */
    fun DrawScope.luzDeGuardia(desde: Offset, hasta: Offset, cuanto: Float) {
        drawLine(lerp(Color(0xFF4A5068), C.GUARDIA, cuanto.coerceIn(0f, 1f)), desde, hasta, 1f, StrokeCap.Round)
        if (cuanto > 0.05f) { drawLine(C.GUARDIA.copy(alpha = 0.22f * cuanto), desde, hasta, 5f, StrokeCap.Round, blendMode = BlendMode.Plus); drawLine(Color.White.copy(alpha = 0.5f * cuanto), desde, hasta, 0.35f) }
    }

    /**
     * La compuerta: el marco con sus pernos, el umbral rayado, la hoja corrida a un lado y, por el vano, la galería de la
     * granja, que sube siguiendo la curva del anillo.
     */
    private fun DrawScope.compuerta(m: Momento, a: Alumbrado) {
        // Los carriles y la hoja, corrida a la izquierda.
        for (y in listOf(90.6f, SUELO - 1f)) drawRect(a.en(Offset(110f, y), lerp(C.ACERO, C.SOMBRA, 0.3f), lejos = 0.1f), Offset(FONDO_I, y - 1.2f), Size(98f, 2.4f))
        val hoja = redondo(FONDO_I + 0.6f, 93f, 87f, SUELO - 2f, 3f)
        planoH(a, hoja, FONDO_I, 87f, 150f, lerp(C.TELA_AZUL, C.CASCO_OSC, 0.45f), lejos = 0.1f, pasos = 2)
        clipPath(hoja) {
            for (y in listOf(108f, 124f, 172f, 188f)) { drawLine(C.GOMA.copy(alpha = 0.5f), Offset(FONDO_I, y), Offset(87f, y), 0.8f); drawLine(Color.White.copy(alpha = 0.14f), Offset(FONDO_I, y + 0.9f), Offset(87f, y + 0.9f), 0.5f) }
            drawCircle(C.GOMA, 5.6f, Offset(73f, 148f)); drawCircle(lerp(E.FUERA, C.LUZ_CULTIVO, 0.2f * E.luzDeCultivo(m)), 4.2f, Offset(73f, 148f)); drawArc(Color.White.copy(alpha = 0.3f), 200f, 70f, false, Offset(69.6f, 144.6f), Size(6.8f, 6.8f), style = Stroke(0.6f))
            val az = Azar(Semilla.de("espacio", "hoja")); repeat(5) { mancha(Offset(az.entre(62f, 86f), az.entre(100f, 200f)), az.entre(3f, 7f), az.entre(4f, 10f), C.SOMBRA, 0.2f) }
        }
        drawRoundRect(a.en(Offset(80f, 160f), C.NARANJA, lejos = 0.1f), Offset(79f, 156f), Size(3f, 14f), CornerRadius(1.4f))
        // Lo que se ve por el vano.
        clipPath(VANO) { galeria(m, a) }
        // El marco, grueso, con su lado de luz, sus pernos y el burlete.
        val marco = Path().apply { addPath(redondo(82f, 90f, 158f, SUELO, 19f)); addPath(VANO); fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd }
        planoH(a, marco, 82f, 158f, 110f, lerp(C.CASCO_OSC, C.ACERO, 0.4f), lejos = 0.05f, pasos = 4)
        drawPath(VANO, C.GOMA, style = Stroke(1.6f)); drawPath(redondo(82f, 90f, 158f, SUELO, 19f), C.GOMA.copy(alpha = 0.6f), style = Stroke(0.7f))
        drawPath(redondo(83.4f, 91.4f, 156.6f, SUELO + 4f, 18f), Color.White.copy(alpha = 0.22f), style = Stroke(0.6f))
        val pernos = (0..7).map { val ang = PI.toFloat() * (1f + it / 7f); Offset(120f + 34.6f * cos(ang), 113f + 20.2f * sin(ang)) } + listOf(Offset(85f, 136f), Offset(85f, 166f), Offset(85f, 194f), Offset(155f, 136f), Offset(155f, 166f), Offset(155f, 194f))
        for (o in pernos) { drawCircle(C.GOMA.copy(alpha = 0.7f), 0.9f, o); drawCircle(Color.White.copy(alpha = 0.3f), 0.35f, o + Offset(-0.25f, -0.25f)) }
        // El umbral, con sus rayas de aviso gastadas.
        val umbral = Path().apply { addRect(Rect(86f, SUELO - 4.6f, 154f, SUELO)) }
        drawPath(umbral, a.en(Offset(120f, SUELO), C.GOMA))
        clipPath(umbral) { for (i in 0..12) drawLine(a.en(Offset(120f, SUELO), C.NARANJA).copy(alpha = if (i % 5 == 3) 0.45f else 0.9f), Offset(84f + i * 6f, SUELO), Offset(88f + i * 6f, SUELO - 4.6f), 2.4f) }
        drawLine(Color.White.copy(alpha = 0.25f), Offset(86f, SUELO - 4.4f), Offset(154f, SUELO - 4.4f), 0.4f)
        // La baliza, encima: apagada es una cúpula parda; con la alarma, roja.
        val b = E.BALIZA
        drawRect(a.en(b, C.CASCO_OSC), b + Offset(-5f, 1.6f), Size(10f, 2.4f))
        drawArc(if (E.alarma(m)) lerp(C.ROJO, Color.White, 0.25f) else a.en(b, lerp(C.ROJO, C.GOMA, 0.5f)), 180f, 180f, true, b + Offset(-4f, -3f), Size(8f, 9.2f))
        drawArc(Color.White.copy(alpha = 0.4f), 200f, 50f, false, b + Offset(-3f, -2f), Size(6f, 7f), style = Stroke(0.5f))
        if (E.alarma(m)) { resplandor(b, 46f, 0.5f, C.ROJO); mancha(Offset(120f, 140f), 70f, 60f, C.ROJO, 0.1f) }
    }

    /** La galería de la granja, vista por la compuerta: cuatro tramos, cada uno más alto y más chico, con sus bancales a los lados. */
    private fun DrawScope.galeria(m: Momento, a: Alumbrado) {
        val luz = E.luzDeCultivo(m); val crecido = E.cultivo(m); val maduro = E.trigoMaduro(m)
        val rosa = lerp(C.LUZ_CULTIVO, C.LUZ_BLANCA, 0.45f)
        fun g(propio: Color, lejos: Float): Color { val c = Color(propio.red * (0.2f + 0.8f * luz) * rosa.red, propio.green * (0.2f + 0.8f * luz), propio.blue * (0.24f + 0.76f * luz) * rosa.blue); return lerp(lerp(c, E.FUERA, 0.3f * (1f - luz)), lerp(Color(0xFF2A2440), rosa, 0.3f + 0.5f * luz), lejos) }
        val base = listOf(SUELO, 196f, 180f, 157f, 126f); val k = listOf(1f, 0.8f, 0.62f, 0.47f, 0.35f); val cx = listOf(120f, 120.5f, 121.2f, 122f, 123f)
        fun izq(i: Int) = cx[i] - 34f * k[i]
        fun der(i: Int) = cx[i] + 34f * k[i]
        fun tope(i: Int) = base[i] - 110f * k[i]
        // El fondo: más galería, ya solo una claridad rosada.
        drawRect(Brush.verticalGradient(listOf(g(Color(0xFFB8A8C8), 0.7f), g(Color(0xFF6A6488), 0.5f)), 90f, 140f), Offset(86f, 90f), Size(70f, 120f))
        for (i in 3 downTo 0) {
            val lejos = 0.09f * i
            // El suelo, con su pasillo de rejilla, y el techo, con su tira de luz.
            drawPath(poligono(Offset(izq(i), base[i]), Offset(der(i), base[i]), Offset(der(i + 1), base[i + 1]), Offset(izq(i + 1), base[i + 1])), Brush.verticalGradient(listOf(g(Color(0xFF8E94A6), lejos + 0.1f), g(Color(0xFF6E7488), lejos)), base[i + 1], base[i]))
            drawPath(poligono(Offset(cx[i] - 9f * k[i], base[i]), Offset(cx[i] + 9f * k[i], base[i]), Offset(cx[i + 1] + 9f * k[i + 1], base[i + 1]), Offset(cx[i + 1] - 9f * k[i + 1], base[i + 1])), g(Color(0xFF50566A), lejos))
            drawPath(poligono(Offset(izq(i), tope(i)), Offset(der(i), tope(i)), Offset(der(i + 1), tope(i + 1)), Offset(izq(i + 1), tope(i + 1))), g(Color(0xFF9A9CAE), lejos))
            drawPath(poligono(Offset(cx[i] - 12f * k[i], tope(i)), Offset(cx[i] + 12f * k[i], tope(i)), Offset(cx[i + 1] + 12f * k[i + 1], tope(i + 1)), Offset(cx[i + 1] - 12f * k[i + 1], tope(i + 1))), lerp(Color(0xFF5A5470), rosa, 0.25f + 0.75f * luz))
            // Los bancales de los dos lados: tres pisos, cada uno con su lámpara rosada encima y lo sembrado debajo.
            for (lado in listOf(-1, 1)) {
                val x0 = if (lado < 0) izq(i) else der(i); val x1 = if (lado < 0) izq(i + 1) else der(i + 1)
                drawPath(poligono(Offset(x0, tope(i)), Offset(x1, tope(i + 1)), Offset(x1, base[i + 1]), Offset(x0, base[i])), Brush.horizontalGradient(listOf(g(Color(0xFF3E4A52), lejos), g(Color(0xFF56645E), lejos + 0.1f)), minOf(x0, x1), maxOf(x0, x1)))
                for (piso in 0..2) {
                    val v = 0.3f + 0.24f * piso
                    val a0 = Offset(x0, tope(i) + (base[i] - tope(i)) * v); val a1 = Offset(x1, tope(i + 1) + (base[i + 1] - tope(i + 1)) * v)
                    val alto = (base[i] - tope(i)) * 0.19f; val alto1 = (base[i + 1] - tope(i + 1)) * 0.19f
                    // La lámpara y su claridad.
                    drawLine(lerp(Color(0xFF6A5A78), rosa, luz), a0 + Offset(0f, -alto * 1.3f), a1 + Offset(0f, -alto1 * 1.3f), maxOf(0.5f, 1.3f * k[i]))
                    // Lo sembrado: el trigo a la derecha, la soya y las papas a la izquierda.
                    val trigo = lado > 0 && piso != 1
                    val verde = if (trigo && maduro) C.TRIGO else if (trigo) lerp(C.VERDE, C.TRIGO, 0.25f) else if (piso == 1) C.VERDE_OSC else C.VERDE
                    val h = (0.25f + 0.75f * crecido)
                    drawPath(poligono(a0, a1, a1 + Offset(0f, -alto1 * h), a0 + Offset(0f, -alto * h)), g(verde, lejos))
                    drawPath(poligono(a0 + Offset(0f, -alto * h), a1 + Offset(0f, -alto1 * h), a1 + Offset(0f, -alto1 * h * 0.7f), a0 + Offset(0f, -alto * h * 0.7f)), g(lerp(verde, Color(0xFFF0F4C0), 0.4f), lejos))
                    val n = 5 - i
                    for (j in 0..n) {
                        val t = (j + 0.5f) / (n + 1f); val o = entreDos(a0, a1, t); val hh = (alto + (alto1 - alto) * t) * h
                        // Cada mata: su sombra por dentro y su copa, que se sale un poco de la bandeja hacia el pasillo.
                        drawOval(g(lerp(verde, C.VERDE_OSC, 0.6f), lejos), o + Offset(-hh * 0.5f - lado * hh * 0.1f, -hh * 0.9f), Size(hh, hh * 0.9f))
                        drawOval(g(lerp(verde, Color(0xFFF0F4C0), if (j % 2 == 0) 0.45f else 0.2f), lejos), o + Offset(-hh * 0.42f - lado * hh * 0.36f, -hh * 1.12f), Size(hh * 0.84f, hh * 0.6f))
                    }
                    // La bandeja.
                    drawLine(g(C.TELA, lejos), a0 + Offset(0f, 0.6f * k[i]), a1 + Offset(0f, 0.6f * k[i + 1]), maxOf(0.6f, 2f * k[i]))
                }
            }
            // La cuaderna del tramo siguiente.
            drawPath(redondo(izq(i + 1), tope(i + 1), der(i + 1), base[i + 1], 15f * k[i + 1]), g(Color(0xFF9AA0B2), lejos + 0.06f), style = Stroke(2.6f * k[i + 1]))
            drawPath(redondo(izq(i + 1) - 1.3f * k[i + 1], tope(i + 1) - 1.3f * k[i + 1], der(i + 1) + 1.3f * k[i + 1], base[i + 1] + 1.3f * k[i + 1], 16f * k[i + 1]), g(Color(0xFF3A3E54), lejos).copy(alpha = 0.7f), style = Stroke(0.5f))
        }
        // La cosecha: sacos en el pasillo. La fiesta de las luces: lamparitas a lo largo de la galería.
        if (E.cosecha(m)) for ((x, y, r) in listOf(Triple(110f, 199f, 4f), Triple(131f, 194f, 3.4f), Triple(117f, 186f, 2.6f))) { drawOval(g(C.TELA, 0.1f), Offset(x - r, y - r * 1.5f), Size(r * 2f, r * 1.7f)); drawOval(g(C.TRIGO, 0.1f), Offset(x - r * 0.5f, y - r * 1.9f), Size(r, r * 0.7f)) }
        if (E.lamparitas(m)) for (i in 0..9) { val t = i / 9f; val o = Offset(104f + (i % 2) * 32f + t * 2f, 200f - 60f * t * t); drawCircle(Color(0xFFFFE2A8), 0.9f - 0.4f * t, o); resplandor(o, 5f - 2f * t, 0.6f, Color(0xFFFFC070)) }
        // El aire entre un módulo y otro: una veladura que crece hacia el fondo.
        drawRect(Brush.verticalGradient(listOf(lerp(Color(0xFF2A2440), rosa, luz).copy(alpha = 0.2f + 0.14f * luz), Color.Transparent), 96f, 190f), Offset(86f, 90f), Size(70f, 110f))
    }

    // ---------------------------------------------------------------- Las consolas

    private fun DrawScope.consolas(m: Momento, a: Alumbrado, az: Azar) {
        val pan = E.pantallas(m); val hoy = m.azar("consolas")
        // El mueble, que sobresale un poco del mamparo: su canto da sombra a la izquierda.
        val mueble = redondo(168f, 56f, 247f, SUELO, 2f)
        val lado = E.deLado(m)   // el canto del mueble da sombra del lado contrario a la lámpara que manda
        drawRect(C.SOMBRA.copy(alpha = 0.14f + 0.26f * lado.coerceAtLeast(0f)), Offset(164f - 3f * lado.coerceAtLeast(0f), 60f), Size(4f + 3f * lado.coerceAtLeast(0f), SUELO - 60f))
        if (lado < -0.1f) drawRect(C.SOMBRA.copy(alpha = 0.4f * -lado), Offset(247f, 60f), Size(4f, SUELO - 60f))
        planoH(a, mueble, 168f, 247f, 120f, lerp(C.CASCO_OSC, C.GOMA, 0.25f), lejos = 0.06f, pasos = 4)
        drawPath(mueble, C.GOMA.copy(alpha = 0.7f), style = Stroke(0.7f)); drawLine(Color.White.copy(alpha = 0.22f), Offset(169f, 56.8f), Offset(246f, 56.8f), 0.5f)
        // La regleta de testigos y llaves.
        drawRoundRect(C.GOMA, Offset(170.6f, 60.6f), Size(74f, 9.6f), CornerRadius(1.2f))
        for (i in 0..5) drawCircle(listOf(C.CIAN, C.AMBAR, C.ALGA)[i % 3].copy(alpha = 0.3f), 0.8f, Offset(173f + i * 5.2f, 65f))
        for (i in 0..5) { val x = 208f + i * 6f; val arriba = hoy.real() > 0.4f; drawRect(a.en(Offset(x, 65f), C.CASCO_OSC), Offset(x - 1.5f, 62.4f), Size(3f, 5.6f)); drawLine(a.en(Offset(x, 65f), C.ACERO), Offset(x, 65.2f), Offset(x, if (arriba) 62.6f else 67.8f), 1.1f, StrokeCap.Round) }
        // Las tres pantallas, cada una con lo suyo.
        val tipoGrande = when { E.fuera(m) -> Pantalla.CAMARA; E.alarma(m) -> Pantalla.ALERTA; E.luto(m) -> Pantalla.RECUERDO; hoy.real() > 0.5f -> Pantalla.ONDAS; else -> Pantalla.BARRAS }
        pantalla(m, Rect(171f, 75f, 215f, 113f), tipoGrande, pan, hoy)
        pantalla(m, Rect(219f, 75f, 245f, 95f), Pantalla.RUTA, pan * (if (E.durmiendo(m)) 2f else 1f), hoy)
        pantalla(m, Rect(219f, 98f, 245f, 113f), Pantalla.DIAL, if (E.durmiendo(m)) 0f else pan, hoy)
        // Las etiquetas de colores pegadas al marco: cada día, en otro sitio.
        for (i in 0..2) if (hoy.real() > 0.3f) { val o = Offset(172f + hoy.entre(0f, 38f), 113.4f); drawRect(a.en(o, listOf(C.TRIGO, C.LUZ_CULTIVO, C.CIAN)[i]), o, Size(4.4f, 3.6f)) }
        if (E.luto(m)) drawPath(poligono(Offset(206f, 75f), Offset(215f, 75f), Offset(215f, 84f)), a.en(Offset(210f, 80f), Color(0xFF1C1A24)))
        // El tablero: la cara de arriba, con sus teclas, y el canto.
        val tapa = poligono(Offset(169f, 118f), Offset(246f, 118f), Offset(251f, 127f), Offset(164f, 127f))
        planoH(a, tapa, 164f, 251f, 122f, lerp(C.CASCO, C.CASCO_OSC, 0.3f), pasos = 4)
        drawRect(a.en(Offset(208f, 130f), lerp(C.CASCO_OSC, C.SOMBRA, 0.3f)), Offset(164f, 127f), Size(87f, 4.2f)); drawLine(Color.White.copy(alpha = 0.3f), Offset(164f, 127.2f), Offset(251f, 127.2f), 0.5f)
        drawRect(C.SOMBRA.copy(alpha = 0.32f), Offset(166f, 131.2f), Size(83f, 3.4f))
        for (fila in 0..1) for (i in 0..13) { val x = 173f + i * 3.5f - fila * 0.8f; val y = 119.6f + fila * 3f; val tecla = if ((i * 7 + fila * 3) % 11 == 0) C.NARANJA else if ((i + fila) % 6 == 0) C.CIAN else C.TELA; drawRoundRect(a.en(Offset(x, y), tecla), Offset(x, y), Size(2.6f + fila * 0.3f, 2.1f), CornerRadius(0.5f)) }
        for (i in 0..2) { val x = 226f + i * 6.4f; drawLine(C.GOMA, Offset(x, 119.4f), Offset(x + 0.8f, 125.4f), 1f); val kk = hoy.real(); drawRect(a.en(Offset(x, 122f), C.ACERO), Offset(x - 1.3f + 0.8f * kk, 119.6f + 4.4f * kk), Size(3.2f, 1.6f)) }
        // Los dos registros de abajo: con sus rejillas y sus tiradores. A sus horas de trabajo, el de la izquierda está destapado.
        for ((i, x) in listOf(170f, 209f).withIndex()) {
            val puerta = Rect(x, 137f, x + 36f, SUELO - 5f)
            if (i == 0 && E.trabajando(m)) {
                drawRect(Color(0xFF14172A), puerta.topLeft, puerta.size)
                // Dentro, a la vista: las tarjetas, los mazos de cables y una lucecita.
                for (k in 0..4) drawRect(a.en(puerta.center, lerp(C.VERDE_OSC, C.GOMA, 0.3f)).copy(alpha = 0.9f), Offset(x + 3f + k * 6.6f, 141f), Size(4.4f, 26f))
                for ((k, color) in listOf(C.NARANJA, C.CIAN, C.TELA, C.ROJO).withIndex()) drawPath(Path().apply { moveTo(x + 6f + k * 7f, 168f); cubicTo(x + 4f + k * 7f, 184f, x + 20f - k * 3f, 186f, x + 12f + k * 5f, 200f) }, a.en(puerta.center, color), style = Stroke(1f, cap = StrokeCap.Round))
                drawCircle(C.ALGA, 0.9f, Offset(x + 31f, 144f)); resplandor(Offset(x + 31f, 144f), 5f, 0.5f, C.ALGA)
                drawRect(C.GOMA.copy(alpha = 0.8f), puerta.topLeft, puerta.size, style = Stroke(0.8f))
                // La tapa, apoyada en el mueble.
                val tapaSuelta = poligono(Offset(x + 38f, SUELO + 2f), Offset(x + 66f, SUELO + 3f), Offset(x + 62f, 152f), Offset(x + 34f, 151f))
                drawPath(tapaSuelta, a.en(Offset(x + 50f, 180f), lerp(C.CASCO, C.CASCO_OSC, 0.4f))); drawPath(tapaSuelta, C.GOMA.copy(alpha = 0.6f), style = Stroke(0.6f))
                for (k in 0..2) drawLine(C.GOMA.copy(alpha = 0.6f), Offset(x + 41f, 162f + k * 3f), Offset(x + 57f, 162.6f + k * 3f), 1f)
            } else {
                placa(a, listOf(puerta.topLeft, puerta.topRight, puerta.bottomRight, puerta.bottomLeft), lerp(C.CASCO, C.CASCO_OSC, 0.25f), 0f, az, lejos = 0.05f)
                for (k in 0..2) drawLine(C.GOMA.copy(alpha = 0.6f), Offset(x + 5f, 146f + k * 3f), Offset(x + 23f, 146f + k * 3f), 1f)
                drawRoundRect(a.en(puerta.center, C.NARANJA), Offset(x + 27f, 164f), Size(2.6f, 12f), CornerRadius(1.2f))
            }
        }
    }

    private enum class Pantalla { ONDAS, BARRAS, RUTA, DIAL, CAMARA, ALERTA, RECUERDO }

    /** Una pantalla: su marco, el vidrio y lo que enseña (gráficas, nunca letras). [pan]: cuánto luce. */
    private fun DrawScope.pantalla(m: Momento, r: Rect, tipo: Pantalla, pan: Float, hoy: Azar) {
        drawRoundRect(C.GOMA, r.topLeft + Offset(-1.6f, -1.6f), Size(r.width + 3.2f, r.height + 3.2f), CornerRadius(2f))
        val k = pan.coerceIn(0f, 1f)
        val tinta = if (tipo == Pantalla.ALERTA) C.ROJO else if (E.parada(m)) C.AMBAR else C.CIAN
        val vidrio = Path().apply { addRoundRect(RoundRect(r, CornerRadius(1.2f))) }
        drawPath(vidrio, Brush.verticalGradient(listOf(lerp(Color(0xFF10142A), lerp(tinta, Color(0xFF10203A), 0.74f), k), lerp(Color(0xFF0A0D1E), lerp(tinta, Color(0xFF0A1830), 0.84f), k)), r.top, r.bottom))
        clipPath(vidrio) {
            val c = tinta.copy(alpha = k); val c2 = lerp(tinta, Color.White, 0.5f).copy(alpha = k)
            if (k > 0.05f) when (tipo) {
                Pantalla.ONDAS -> {
                    for (i in 1..3) drawLine(c.copy(alpha = 0.2f * k), Offset(r.left, r.top + r.height * i / 4f), Offset(r.right, r.top + r.height * i / 4f), 0.3f)
                    for ((j, color) in listOf(c, C.AMBAR.copy(alpha = k)).withIndex()) { val linea = Path(); var y = r.center.y + (j - 0.5f) * 9f; linea.moveTo(r.left, y); var x = r.left; while (x < r.right) { x += 2.6f; y = (y + hoy.entre(-3.4f, 3.4f)).coerceIn(r.top + 3f, r.bottom - 3f); linea.lineTo(x, y) }; drawPath(linea, color, style = Stroke(0.8f, join = StrokeJoin.Round)) }
                }
                Pantalla.BARRAS -> {
                    val n = 9; for (i in 0 until n) { val h = r.height * hoy.entre(0.2f, 0.8f); drawRect(if (i % 4 == 2) C.AMBAR.copy(alpha = k) else c, Offset(r.left + 3f + i * (r.width - 6f) / n, r.bottom - 3f - h), Size((r.width - 6f) / n - 1.2f, h)) }
                    drawLine(c2, Offset(r.left + 2f, r.bottom - 3f), Offset(r.right - 2f, r.bottom - 3f), 0.4f)
                }
                Pantalla.RUTA -> {
                    // La ruta: el Sol atrás, la estrella delante y la nave en la parte del camino que lleva hecha.
                    val x0 = r.left + 3.4f; val x1 = r.right - 3.4f; val y = r.center.y + 1f
                    for (i in 0..10) drawCircle(c.copy(alpha = 0.5f * k), 0.35f, Offset(x0 + (x1 - x0) * i / 10f, y))
                    drawCircle(C.AMBAR.copy(alpha = k), 1.5f, Offset(x0, y)); drawCircle(c2, 1.2f, Offset(x1, y)); drawCircle(c2.copy(alpha = 0.4f * k), 2.4f, Offset(x1, y), style = Stroke(0.3f))
                    val nave = Offset(x0 + (x1 - x0) * E.viaje(m), y); drawLine(C.AMBAR.copy(alpha = k), Offset(x0, y), nave, 0.7f)
                    drawPath(poligono(nave + Offset(2f, 0f), nave + Offset(-1.4f, -1.6f), nave + Offset(-1.4f, 1.6f)), Color.White.copy(alpha = k))
                    drawLine(c.copy(alpha = 0.5f * k), Offset(nave.x, r.top + 2.4f), Offset(nave.x, y - 2.6f), 0.3f)
                }
                Pantalla.DIAL -> {
                    val o = Offset(r.center.x, r.bottom - 2.4f); val rad = r.height * 0.74f
                    drawArc(c.copy(alpha = 0.5f * k), 180f, 180f, false, o - Offset(rad, rad), Size(rad * 2f, rad * 2f), style = Stroke(1.4f))
                    drawArc(C.AMBAR.copy(alpha = k), 320f, 40f, false, o - Offset(rad, rad), Size(rad * 2f, rad * 2f), style = Stroke(1.4f))
                    val ang = PI.toFloat() * (1.12f + (if (E.parada(m)) 0f else 0.5f + 0.16f * hoy.real()))
                    drawLine(Color.White.copy(alpha = k), o, o + Offset(cos(ang), sin(ang)) * (rad - 1f), 0.6f, StrokeCap.Round); drawCircle(c2, 1f, o)
                }
                Pantalla.CAMARA -> {
                    // La cámara del casco: la curva de la nave, una placa, la luz de quien trabaja fuera y las estrellas.
                    drawRect(Color(0xFF05060E).copy(alpha = k), r.topLeft, r.size)
                    repeat(14) { drawCircle(Color.White.copy(alpha = 0.7f * k), 0.3f, Offset(hoy.entre(r.left, r.right), hoy.entre(r.top, r.top + r.height * 0.5f))) }
                    drawPath(Path().apply { moveTo(r.left, r.bottom - 10f); cubicTo(r.left + 14f, r.bottom - 19f, r.right - 12f, r.bottom - 17f, r.right, r.bottom - 6f); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close() }, lerp(Color(0xFF3A4054), C.ACERO, 0.5f).copy(alpha = k))
                    for (i in 1..4) drawLine(Color(0xFF1E2234).copy(alpha = 0.7f * k), Offset(r.left + i * 9f, r.bottom - 14f - (2 - kotlin.math.abs(i - 2)) * 1f), Offset(r.left + i * 9f - 2f, r.bottom), 0.4f)
                    drawCircle(Color(0xFFFFF0D0).copy(alpha = k), 1f, Offset(r.left + 27f, r.bottom - 17f)); mancha(Offset(r.left + 27f, r.bottom - 16f), 7f, 4f, Color(0xFFFFE2B0), 0.5f * k)
                    drawLine(C.NARANJA.copy(alpha = k), Offset(r.left + 27f, r.bottom - 16f), Offset(r.left + 6f, r.bottom - 12.6f), 0.4f)
                }
                Pantalla.ALERTA -> {
                    val o = r.center + Offset(-6f, 1f)
                    drawPath(poligono(o + Offset(0f, -11f), o + Offset(11f, 8f), o + Offset(-11f, 8f)), c, style = Stroke(1.6f, join = StrokeJoin.Round)); drawLine(c, o + Offset(0f, -4f), o + Offset(0f, 2.4f), 1.6f, StrokeCap.Round); drawCircle(c, 0.9f, o + Offset(0f, 5.2f))
                    for (i in 0..3) drawRect(c.copy(alpha = (0.4f + 0.2f * i) * k), Offset(r.right - 13f, r.top + 4f + i * 7.6f), Size(9f * hoy.entre(0.5f, 1f), 4.4f))
                }
                Pantalla.RECUERDO -> {
                    // El mensaje de su madre, detenido: la ventana de una cocina junto al mar. No se ve a nadie.
                    drawRect(Color(0xFFD8C8A8).copy(alpha = k), r.topLeft, r.size)
                    val v = Rect(r.left + 12f, r.top + 6f, r.right - 6f, r.bottom - 12f)
                    drawRect(Brush.verticalGradient(listOf(Color(0xFFBFD8E8), Color(0xFFE8E2C8), Color(0xFF4E8AA0), Color(0xFF3A7088)), v.top, v.bottom), v.topLeft, v.size, alpha = k)
                    drawRect(Color(0xFFF4EEE0).copy(alpha = k), v.topLeft, v.size, style = Stroke(1.4f)); drawLine(Color(0xFFF4EEE0).copy(alpha = k), Offset(v.center.x, v.top), Offset(v.center.x, v.bottom), 1f)
                    drawRect(Color(0xFF8A6A4A).copy(alpha = k), Offset(r.left, r.bottom - 8f), Size(r.width, 8f)); drawOval(Color(0xFFE8E0D0).copy(alpha = k), Offset(r.left + 5f, r.bottom - 12f), Size(6f, 4.6f))
                }
            }
            // El reflejo del vidrio.
            drawPath(poligono(Offset(r.left + r.width * 0.56f, r.top), Offset(r.left + r.width * 0.74f, r.top), Offset(r.left + r.width * 0.5f, r.bottom), Offset(r.left + r.width * 0.32f, r.bottom)), Color.White.copy(alpha = 0.045f + 0.03f * (1f - k)))
        }
        if (k > 0.1f) mancha(r.center, r.width * 0.8f, r.height * 0.8f, tinta, 0.07f * k * (0.4f + 0.6f * (1f - E.programa(m).nivel)))
    }

    // ---------------------------------------------------------------- El armario del traje

    private fun DrawScope.armarioDelTraje(m: Momento, a: Alumbrado) {
        val hueco = redondo(251f, 62f, 286f, SUELO - 3f, 3f)
        // El hueco, oscuro, con su luz de arriba: dentro se ve el traje, o los ganchos vacíos.
        drawPath(hueco, Brush.verticalGradient(listOf(a.en(Offset(268f, 70f), Color(0xFF4A5068)), a.en(Offset(268f, 190f), Color(0xFF262A40))), 62f, SUELO))
        val o = Offset(268.5f, 0f); val enArmario = E.trajeEnSuSitio(m); val casco = enArmario && m.ficha.objeto != "traje"
        clipPath(hueco) {
            drawRect(Color.White.copy(alpha = 0.16f), Offset(256f, 63.4f), Size(25f, 1.6f))
            drawLine(a.en(Offset(268f, 88f), C.ACERO), Offset(251f, 88f), Offset(286f, 88f), 1.4f)                   // la balda del casco
            drawLine(a.en(Offset(268f, 93f), C.ACERO), Offset(254f, 93.4f), Offset(283f, 93.4f), 0.9f)                 // la barra
            if (casco) {
                // El casco: la bola blanca, el visor dorado y el aro del cuello.
                val c = Offset(o.x, 77.6f)
                drawCircle(a.en(c, C.TELA), 9.6f, c); drawCircle(a.en(c, lerp(C.TELA, C.SOMBRA, 0.3f)), 9.6f, c, style = Stroke(0.6f))
                drawOval(Brush.horizontalGradient(listOf(a.en(c, Color(0xFF8A6A2E), -1f), a.en(c, C.AMBAR), a.en(c, Color(0xFF5A4424), 1f)), c.x - 7f, c.x + 7f), c + Offset(-6.6f, -5.6f), Size(13.2f, 10.4f))
                drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, c + Offset(-5f, -4.4f), Size(9f, 7f), style = Stroke(0.6f))
                drawRect(a.en(c, C.ACERO), Offset(c.x - 6f, 85.6f), Size(12f, 2.2f))
            } else if (!enArmario) { drawOval(a.en(Offset(262f, 86f), C.TELA), Offset(256f, 84f), Size(7f, 3.6f)); drawOval(a.en(Offset(262f, 86f), C.NARANJA), Offset(256f, 85.4f), Size(2.4f, 2f)) }   // un guante olvidado
            if (enArmario) {
                // El traje, colgado: el torso con su mochila, las mangas, las perneras y las botas.
                val blanco = a.en(Offset(o.x, 130f), C.TELA); val gris = a.en(Offset(o.x, 130f), lerp(C.TELA, C.SOMBRA, 0.32f)); val naranja = a.en(Offset(o.x, 130f), C.NARANJA)
                drawRoundRect(gris, Offset(o.x - 13.6f, 97f), Size(27.2f, 40f), CornerRadius(5f))                                                   // la mochila, por detrás
                for (lado in listOf(-1f, 1f)) {
                    drawPath(Path().apply { moveTo(o.x + lado * 9f, 99f); cubicTo(o.x + lado * 15.6f, 104f, o.x + lado * 15f, 130f, o.x + lado * 13.4f, 146f); lineTo(o.x + lado * 8.6f, 146f); cubicTo(o.x + lado * 9.6f, 130f, o.x + lado * 8f, 112f, o.x + lado * 6f, 106f); close() }, if (lado < 0f) gris else blanco)
                    drawOval(a.en(Offset(o.x, 148f), C.CASCO_OSC), Offset(o.x + lado * 11f - 3.2f, 144.6f), Size(6.4f, 7.6f))
                    drawRoundRect(if (lado < 0f) gris else blanco, Offset(o.x + lado * 5.6f - 4.8f, 140f), Size(9.6f, 44f), CornerRadius(3.4f))
                    drawRect(naranja, Offset(o.x + lado * 5.6f - 4.8f, 160f), Size(9.6f, 2.4f))
                    drawRoundRect(a.en(Offset(o.x, 190f), C.CASCO_OSC), Offset(o.x + lado * 5.6f - 5.6f, 182f), Size(11.2f, 10f), CornerRadius(2.4f))
                }
                drawRoundRect(Brush.horizontalGradient(listOf(gris, blanco, blanco), o.x - 10f, o.x + 10f), Offset(o.x - 10f, 96f), Size(20f, 46f), CornerRadius(5f))
                drawRoundRect(a.en(Offset(o.x, 112f), C.CASCO_OSC), Offset(o.x - 5.6f, 106f), Size(11.2f, 9f), CornerRadius(1.4f)); for (k in 0..2) drawCircle(listOf(C.ROJO, C.CIAN, C.TRIGO)[k], 0.8f, Offset(o.x - 3f + k * 3f, 110.4f))
                drawRect(naranja, Offset(o.x - 10f, 122f), Size(20f, 2.2f)); drawOval(a.en(Offset(o.x, 97f), C.ACERO), Offset(o.x - 6f, 94.6f), Size(12f, 4f))
            } else {
                // Los ganchos vacíos y el carrete de la cuerda, que no está.
                for (x in listOf(260f, 268.5f, 277f)) drawPath(Path().apply { moveTo(x, 93.4f); cubicTo(x, 100f, x + 3f, 100f, x + 3f, 97f) }, a.en(Offset(x, 96f), C.ACERO), style = Stroke(0.9f, cap = StrokeCap.Round))
                drawOval(Color.Black.copy(alpha = 0.3f), Offset(256f, 188f), Size(24f, 5f))
            }
            // La sombra del dintel, dentro.
            drawRect(Brush.verticalGradient(listOf(C.SOMBRA.copy(alpha = 0.5f), Color.Transparent), 62f, 74f), Offset(251f, 62f), Size(35f, 12f))
        }
        drawPath(hueco, C.GOMA.copy(alpha = 0.8f), style = Stroke(1f)); drawPath(redondo(250f, 61f, 287f, SUELO - 2f, 3.6f), Color.White.copy(alpha = 0.2f), style = Stroke(0.5f))
        // La hoja del armario, corrida.
        drawRect(a.en(Offset(288f, 130f), lerp(C.CASCO, C.CASCO_OSC, 0.5f)), Offset(286f, 62f), Size(2f, SUELO - 65f))
    }

    // ---------------------------------------------------------------- El costado izquierdo: las bandejas del cultivo

    fun DrawScope.costadoIzquierdo(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("espacio", "izquierda"))
        val luz = E.luzDeCultivo(m); val crecido = E.cultivo(m)
        for (j in 0..2) for (i in 0..2) {
            val s0 = fondo(i, 3, 1f, 1.56f); val s1 = fondo(i + 1, 3, 1f, 1.56f); val y0 = TECHO + j * 52f; val y1 = y0 + 52f
            placa(a, listOf(p(FONDO_I, y0, s1), p(FONDO_I, y0, s0), p(FONDO_I, y1, s0), p(FONDO_I, y1, s1)), lerp(C.CASCO_OSC, C.GOMA, 0.2f), 1f, az, lejos = 0.05f, desgaste = 0.5f)
        }
        // El bastidor: tres montantes y, en cada piso, la bandeja con lo sembrado y su lámpara encima.
        val s0 = 1.03f; val s1 = 1.56f
        for (i in 0..2) { val s = fondo(i, 2, s0, s1); drawLine(a.en(p(FONDO_I, 130f, s), C.ACERO, 1f), p(FONDO_I, 62f, s) + Offset(1f, 0f), p(FONDO_I, SUELO, s) + Offset(1f, 0f), 1.2f * s) }
        for ((piso, wy) in listOf(100f, 143f, 186f).withIndex()) {
            val a0 = p(FONDO_I, wy, s0); val a1 = p(FONDO_I, wy, s1)
            // La lámpara de este piso: una barra rosada, y la claridad que deja en la pared.
            val l0 = p(FONDO_I, wy - 33f, s0) + Offset(1.6f, 0f); val l1 = p(FONDO_I, wy - 33f, s1) + Offset(1.6f, 0f)
            drawLine(a.en(l0, C.CASCO_OSC, 1f), l0 + Offset(0f, -1.2f), l1 + Offset(0f, -1.8f), 2.2f)
            drawLine(lerp(Color(0xFF6A5A78), lerp(C.LUZ_CULTIVO, Color.White, 0.4f), luz), l0, l1, 1.5f, StrokeCap.Round)
            if (luz > 0.05f) { val c = entreDos(l0, l1, 0.5f); mancha(c + Offset(0f, 12f), 36f, 17f, C.LUZ_CULTIVO, 0.26f * luz * (1.2f - E.programa(m).nivel * 0.7f)) }
            // Lo sembrado: arriba la soya, en medio el trigo enano y abajo las papas.
            val n = 9
            for (k in 0 until n) {
                val t = (k + 0.5f) / n; val s = 1f / (1f / s0 + (1f / s1 - 1f / s0) * t); val pie = p(FONDO_I, wy, s) + Offset(1f + 2f * s, 0f)
                val planta = Azar(Semilla.de("espacio", "mata", piso, k))
                when (piso) {
                    0 -> mataDeHoja(a, pie, (4f + 13f * crecido) * s, C.VERDE, planta, redonda = true)
                    1 -> trigo(a, pie, (3f + 17f * crecido) * s, crecido, E.trigoMaduro(m), planta, E.sinPeso(m))
                    else -> mataDeHoja(a, pie, (4f + 11f * crecido) * s, C.VERDE_OSC, planta, redonda = false)
                }
            }
            // La bandeja: su canto blanco, con el filo de luz y la gotera del riego.
            val canto = poligono(a0 + Offset(1f, 0f), a1 + Offset(2f, 0f), a1 + Offset(2f, 7.4f * s1), a0 + Offset(1f, 7.4f * s0))
            drawPath(canto, Brush.horizontalGradient(listOf(a.en(a1, C.TELA, 1f), a.en(a0, lerp(C.TELA, C.CASCO_OSC, 0.3f), 1f)), a1.x, a0.x))
            drawLine(Color.White.copy(alpha = 0.4f), a0 + Offset(1f, 0.4f), a1 + Offset(2f, 0.6f), 0.5f); drawLine(C.GOMA.copy(alpha = 0.5f), a0 + Offset(1f, 7.4f * s0), a1 + Offset(2f, 7.4f * s1), 0.6f)
            for (k in 0..2) { val q = entreDos(a0, a1, 0.2f + k * 0.3f) + Offset(0f, 4f); drawRect(a.en(q, C.TELA_AZUL, 1f), q, Size(3.4f, 2f)) }
        }
        luzDeGuardia(p(FONDO_I, SUELO - 2f, 1.02f), p(FONDO_I, SUELO - 2f, 1.56f), E.programa(m).guardia)
    }

    /** Una mata de hojas: la sombra de dentro, las hojas de fuera y las que les da la lámpara. [redonda]: de hoja ancha (soya) o apiñada (papa). */
    fun DrawScope.mataDeHoja(a: Alumbrado, pie: Offset, alto: Float, propio: Color, az: Azar, redonda: Boolean) {
        val oscuro = a.en(pie + Offset(0f, -alto * 0.5f), lerp(propio, C.SOMBRA, 0.45f)); val medio = a.en(pie + Offset(0f, -alto * 0.6f), propio); val claro = a.en(pie + Offset(0f, -alto), lerp(propio, Color(0xFFE8F0A8), 0.45f))
        drawOval(oscuro, pie + Offset(-alto * 0.5f, -alto * 0.8f), Size(alto, alto * 0.8f))
        val n = 7 + az.entero(4)
        for (i in 0 until n) {
            val ang = PI.toFloat() * (1.05f + 0.9f * i / (n - 1f)) + az.entre(-0.2f, 0.2f); val d = alto * az.entre(0.5f, 1f)
            val c = pie + Offset(cos(ang) * d * 0.62f, sin(ang) * d); val w = alto * (if (redonda) 0.3f else 0.22f) * az.entre(0.8f, 1.2f)
            drawLine(oscuro, pie, c, maxOf(0.4f, alto * 0.04f))
            drawOval(if (i % 3 == 0) claro else medio, c + Offset(-w, -w * (if (redonda) 0.7f else 0.5f)), Size(w * 2f, w * (if (redonda) 1.4f else 1f)))
            if (i % 2 == 0) drawOval(claro.copy(alpha = 0.7f), c + Offset(-w * 0.5f, -w * 0.6f), Size(w, w * 0.5f))
        }
    }

    /** Una mata de trigo enano: las cañas, las hojas y, cuando ha crecido, la espiga. Con la cosecha, el rastrojo. */
    fun DrawScope.trigo(a: Alumbrado, pie: Offset, alto: Float, crecido: Float, maduro: Boolean, az: Azar, sinPeso: Boolean = false) {
        val verde = if (maduro) C.TRIGO else lerp(C.VERDE, C.TRIGO, 0.2f + 0.3f * crecido)
        val c = a.en(pie + Offset(0f, -alto * 0.6f), verde); val luz = a.en(pie + Offset(0f, -alto), lerp(verde, Color(0xFFFFF4C0), 0.4f)); val sombra = a.en(pie, lerp(verde, C.SOMBRA, 0.4f))
        val n = 6
        for (i in 0 until n) {
            val ladeo = (i - (n - 1) / 2f) * alto * 0.12f + az.entre(-1f, 1f) * alto * 0.05f; val h = alto * az.entre(0.75f, 1f)
            val punta = pie + Offset(ladeo * (if (sinPeso) 1.7f else 1f), -h)
            drawPath(Path().apply { moveTo(pie.x + ladeo * 0.2f, pie.y); cubicTo(pie.x + ladeo * 0.3f, pie.y - h * 0.5f, punta.x - ladeo * 0.2f, punta.y + h * 0.3f, punta.x, punta.y) }, if (i % 2 == 0) c else sombra, style = Stroke(maxOf(0.5f, alto * 0.05f), cap = StrokeCap.Round))
            if (crecido > 0.55f) drawOval(luz, punta + Offset(-alto * 0.05f, -alto * 0.16f), Size(alto * 0.1f, alto * 0.24f))
        }
    }

    // ---------------------------------------------------------------- El costado derecho: la ventana

    fun DrawScope.costadoDerecho(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("espacio", "derecha")); val hoy = m.azar("red")
        val filas = listOf(1f, 1.16f, 1.38f, 1.68f, 2.1f)
        // Arriba, el acolchado, con sus costuras y sus botones; abajo, las placas.
        for (i in 0 until filas.size - 1) {
            val s0 = filas[i]; val s1 = filas[i + 1]
            for ((y0, y1, tela) in listOf(Triple(TECHO, 100f, true), Triple(100f, 150f, true), Triple(150f, SUELO, false))) {
                val q = listOf(p(FONDO_D, y0, s0), p(FONDO_D, y0, s1), p(FONDO_D, y1, s1), p(FONDO_D, y1, s0))
                placa(a, q, if (tela) lerp(C.TELA, C.CASCO, 0.3f) else lerp(C.CASCO, C.CASCO_OSC, 0.3f), -1f, az, remaches = !tela, desgaste = if (tela) 0.3f else 1f)
                if (tela) {
                    val c = centro(q)
                    drawLine(C.SOMBRA.copy(alpha = 0.16f), entreDos(q[0], q[1], 0.5f), entreDos(q[3], q[2], 0.5f), 0.6f); drawLine(C.SOMBRA.copy(alpha = 0.16f), entreDos(q[0], q[3], 0.5f), entreDos(q[1], q[2], 0.5f), 0.6f)
                    drawCircle(a.en(c, C.NARANJA, -1f), 0.9f * s0, c); mancha(c + Offset(-1f, 1.4f), 5f * s0, 4f * s0, C.SOMBRA, 0.14f)
                }
            }
        }
        ventana(m, a)
        // Los dibujos de los niños de a bordo, sujetos al acolchado: manchas de color, sin letras. Cada día hay uno distinto arriba.
        val dibujo = m.azar("dibujos")
        for ((i, wy) in listOf(106f, 126f, 112f).withIndex()) {
            val s = 1.05f + i * 0.1f; val o = p(FONDO_D, wy, s); val w = 7.6f * s; val h = 11f * s
            val hoja = poligono(o, o + Offset(w, -w * 0.5f), o + Offset(w, -w * 0.5f + h), o + Offset(0f, h))
            drawPath(hoja, a.en(o, lerp(C.TELA, Color.White, 0.4f), -1f))
            clipPath(hoja) {
                val fondoDib = listOf(C.CIAN, C.TRIGO, C.LUZ_CULTIVO, C.VERDE)[(i + dibujo.entero(4)) % 4]
                drawRect(a.en(o, fondoDib, -1f).copy(alpha = 0.5f), o + Offset(0f, h * 0.5f), Size(w, h))
                drawCircle(a.en(o, listOf(C.NARANJA, C.ROJO, C.TRIGO)[(i + dibujo.entero(3)) % 3], -1f), w * 0.22f, o + Offset(w * dibujo.entre(0.3f, 0.7f), h * 0.2f))
                drawLine(a.en(o, C.TELA_AZUL, -1f), o + Offset(w * 0.2f, h * 0.7f), o + Offset(w * 0.8f, h * dibujo.entre(0.3f, 0.6f)), 0.7f * s)
            }
            drawCircle(a.en(o, C.ROJO, -1f), 0.6f * s, o + Offset(w * 0.5f, -w * 0.1f))
        }
        // El pasamanos, naranja, con sus soportes.
        val h0 = p(FONDO_D, 152f, 1.04f) + Offset(-2.4f, 0f); val h1 = p(FONDO_D, 152f, 1.9f) + Offset(-4.6f, 0f)
        for (k in listOf(0.06f, 0.4f, 0.8f)) { val q = entreDos(h0, h1, k); drawLine(a.en(q, C.ACERO, -1f), q, q + Offset(3f + 2f * k, 1f), 1f + k) }
        drawLine(C.SOMBRA.copy(alpha = 0.3f), h0 + Offset(1f, 2.4f), h1 + Offset(2f, 4f), 2f); drawLine(a.en(entreDos(h0, h1, 0.5f), C.NARANJA, -1f), h0, h1, 2.4f, StrokeCap.Round); drawLine(Color.White.copy(alpha = 0.35f), h0 + Offset(0f, -0.7f), h1 + Offset(0f, -0.9f), 0.5f)
        // El banco de debajo de la ventana: la tapa, el canto y sus dos escuadras. En su rato de la ventana, el cojín y la manta.
        val b0 = 1.2f; val b1 = 1.62f; val wy = 178f
        val tapa = poligono(p(FONDO_D, wy, b0), p(FONDO_D, wy, b1), p(FONDO_D - 20f, wy, b1), p(FONDO_D - 20f, wy, b0))
        for (s in listOf(b0 + 0.04f, b1 - 0.05f)) drawPath(poligono(p(FONDO_D, wy + 3f, s), p(FONDO_D - 16f, wy + 3f, s), p(FONDO_D, wy + 16f, s)), a.en(p(FONDO_D - 8f, wy + 8f, s), lerp(C.ACERO, C.SOMBRA, 0.3f), -1f))
        drawPath(tapa, a.en(p(FONDO_D - 10f, wy, 1.4f), lerp(C.CASCO, C.TELA, 0.5f)))
        val canto = poligono(p(FONDO_D - 20f, wy, b0), p(FONDO_D - 20f, wy, b1), p(FONDO_D - 20f, wy + 3.4f, b1), p(FONDO_D - 20f, wy + 3.4f, b0))
        drawPath(canto, a.en(p(FONDO_D - 20f, wy + 2f, 1.4f), lerp(C.CASCO_OSC, C.SOMBRA, 0.2f), -1f)); drawPath(tapa, C.GOMA.copy(alpha = 0.5f), style = Stroke(0.5f))
        // La red de carga, con los bultos que guarda hoy.
        val r0 = 1.05f; val r1 = 1.2f
        val red = poligono(p(FONDO_D, 158f, r0), p(FONDO_D, 156f, r1), p(FONDO_D, 204f, r1), p(FONDO_D, 203f, r0))
        clipPath(red) {
            val n = 2 + hoy.entero(3)
            for (i in 0 until n) { val c = entreDos(p(FONDO_D, 170f + i * 9f, r0), p(FONDO_D, 168f + i * 10f, r1), hoy.entre(0.3f, 0.7f)); drawOval(a.en(c, listOf(C.TELA_AZUL, C.TELA, C.NARANJA, C.VERDE_OSC)[(i + hoy.entero(4)) % 4], -1f), c + Offset(-7f, -6f), Size(14f, 12f)) }
            for (i in -4..12) { drawLine(C.TELA.copy(alpha = 0.5f), p(FONDO_D, 150f + i * 6f, r0), p(FONDO_D, 164f + i * 6f, r1), 0.35f); drawLine(C.TELA.copy(alpha = 0.5f), p(FONDO_D, 164f + i * 6f, r0), p(FONDO_D, 150f + i * 6f, r1), 0.35f) }
        }
        drawPath(red, a.en(p(FONDO_D, 180f, 1.12f), C.NARANJA, -1f), style = Stroke(0.8f))
        // Lo que hay en el banco: una planta en una lata, al fondo, y la manta doblada; en su rato de la ventana, el cojín y la manta echada; el día del luto, un paño negro.
        val lata = p(FONDO_D - 7f, wy, 1.27f)
        mataDeHoja(a, lata + Offset(0f, -6.4f), 11f, C.VERDE, Azar(Semilla.de("espacio", "lata")), redonda = true)
        cuerpo(a, redondo(lata.x - 3.2f, lata.y - 7f, lata.x + 3.2f, lata.y, 0.8f), lata.x - 3.2f, lata.x + 3.2f, lata.y - 3f, C.ACERO); drawRect(a.en(lata, C.TRIGO, -1f), Offset(lata.x - 3.2f, lata.y - 5f), Size(6.4f, 2.6f)); drawOval(a.en(lata, lerp(C.VERDE_OSC, C.GOMA, 0.5f)), Offset(lata.x - 3.2f, lata.y - 7.8f), Size(6.4f, 1.8f))
        val c = p(FONDO_D - 9f, wy, 1.42f)
        if (E.luto(m)) {
            val negro = Color(0xFF1C1A24)
            val pano = poligono(listOf(p(FONDO_D - 4f, wy, 1.34f), p(FONDO_D - 4f, wy, 1.52f), p(FONDO_D - 20f, wy, 1.52f), p(FONDO_D - 20f, wy + 19f, 1.5f), p(FONDO_D - 20f, wy + 12f, 1.43f), p(FONDO_D - 20f, wy + 21f, 1.37f), p(FONDO_D - 20f, wy, 1.34f)))
            drawPath(pano, a.en(c, negro, -1f))
            drawPath(poligono(p(FONDO_D - 4f, wy, 1.34f), p(FONDO_D - 4f, wy, 1.52f), p(FONDO_D - 20f, wy, 1.52f), p(FONDO_D - 20f, wy, 1.34f)), a.en(c, lerp(negro, C.CASCO_OSC, 0.3f), -1f))
            drawLine(a.en(c, lerp(negro, C.CASCO_OSC, 0.5f), -1f), p(FONDO_D - 20f, wy + 1f, 1.44f), p(FONDO_D - 20f, wy + 13f, 1.43f), 0.6f)
        } else if (E.enLaVentana(m) || E.aOscuras(m)) {
            drawRoundRect(a.en(c, C.TELA_AZUL), c + Offset(-11f, -6.6f), Size(22f, 7f), CornerRadius(3f)); drawLine(Color.White.copy(alpha = 0.25f), c + Offset(-8f, -5.6f), c + Offset(8f, -5.6f), 0.6f)
            drawPath(Path().apply { moveTo(c.x + 8f, c.y - 5f); cubicTo(c.x + 16f, c.y - 6f, c.x + 20f, c.y + 2f, c.x + 17f, c.y + 12f); lineTo(c.x + 9f, c.y + 10f); cubicTo(c.x + 12f, c.y + 2f, c.x + 9f, c.y - 1f, c.x + 6f, c.y - 0.4f); close() }, a.en(c + Offset(12f, 2f), C.NARANJA, -1f))
        } else {
            // La manta, doblada en tres: naranja, con su lista clara, un pico que cuelga por el canto y un doblez mal hecho.
            val manta = a.en(c, C.NARANJA, -1f); val vuelta = a.en(c, lerp(C.NARANJA, C.SOMBRA, 0.3f), -1f)
            drawPath(poligono(c + Offset(-12f, -1.4f), c + Offset(-7f, -1f), c + Offset(-8f, 6.4f), c + Offset(-12.6f, 3.4f)), vuelta)
            for ((k, y) in listOf(-2.4f, -5.2f, -7.8f).withIndex()) { drawRoundRect(if (k == 1) vuelta else manta, c + Offset(-11f + k * 0.8f, y - 0.4f), Size(21f - k * 1.4f, 3f), CornerRadius(1.4f)); drawLine(Color.White.copy(alpha = 0.22f), c + Offset(-9f + k, y), c + Offset(8f - k, y), 0.4f) }
            drawRect(a.en(c, C.TELA, -1f), c + Offset(2.4f, -8.2f), Size(2.4f, 8.6f))
        }
        // El aplique de emergencia, la segunda baliza y la luz de guardia.
        aplique(a, p(FONDO_D, 64f, 1.1f) + Offset(-1f, 0f), E.emergencia(m), 0.8f)
        val baliza = Offset(352f, 40f)
        drawRoundRect(a.en(baliza, C.CASCO_OSC, -1f), baliza + Offset(-3f, -5f), Size(6f, 10f), CornerRadius(2f)); drawOval(if (E.alarma(m)) lerp(C.ROJO, Color.White, 0.25f) else a.en(baliza, lerp(C.ROJO, C.GOMA, 0.5f)), baliza + Offset(-4.4f, -3.4f), Size(5f, 6.8f))
        if (E.alarma(m)) { resplandor(baliza, 40f, 0.5f, C.ROJO); mancha(Offset(340f, 120f), 50f, 90f, C.ROJO, 0.1f) }
        luzDeGuardia(p(FONDO_D, SUELO - 2f, 1.02f), p(FONDO_D, SUELO - 2f, 2f), E.programa(m).guardia)
    }

    /** La ventana: pequeña, de vidrio grueso, con su aro atornillado. Lo de fuera se pinta en la parte viva, porque gira. */
    private fun DrawScope.ventana(m: Momento, a: Alumbrado) {
        val o = VENTANA
        fun aro(rx: Float, ry: Float) = Path().apply { addOval(Rect(o.x - rx, o.y - ry, o.x + rx, o.y + ry)) }
        // La claridad que entra, de noche.
        val noche = 1f - E.programa(m).nivel
        if (noche > 0.3f) mancha(o, 34f, 40f, Color(0xFFB0C4FF), 0.1f * noche)
        drawPath(aro(VENTANA_RX + 4.6f, VENTANA_RY + 5.6f), C.SOMBRA.copy(alpha = 0.3f))
        drawPath(aro(VENTANA_RX + 3.6f, VENTANA_RY + 4.4f), Brush.horizontalGradient(listOf(a.en(o + Offset(-14f, 0f), C.ACERO, -1f), a.en(o + Offset(14f, 0f), lerp(C.ACERO, C.SOMBRA, 0.35f), 1f)), o.x - 16f, o.x + 16f))
        drawPath(aro(VENTANA_RX + 3.6f, VENTANA_RY + 4.4f), C.GOMA.copy(alpha = 0.7f), style = Stroke(0.6f))
        for (i in 0 until 10) { val ang = (2f * PI.toFloat() * i / 10f) + 0.3f; val q = o + Offset(cos(ang) * (VENTANA_RX + 1.9f), sin(ang) * (VENTANA_RY + 2.4f)); drawCircle(C.GOMA.copy(alpha = 0.8f), 0.8f, q); drawCircle(Color.White.copy(alpha = 0.35f), 0.3f, q + Offset(-0.2f, -0.2f)) }
        drawPath(aro(VENTANA_RX, VENTANA_RY), C.GOMA)
        drawPath(aro(VENTANA_RX - 2.2f, VENTANA_RY - 2.6f), E.FUERA)
        // El canto del hueco: el lado que mira a la luz de dentro.
        drawArc(a.en(o, lerp(C.CASCO_OSC, Color.White, 0.3f), -1f).copy(alpha = 0.8f), 250f, 150f, false, Offset(o.x - VENTANA_RX + 1f, o.y - VENTANA_RY + 1.2f), Size(VENTANA_RX * 2f - 2f, VENTANA_RY * 2f - 2.4f), style = Stroke(1f))
    }

    // ---------------------------------------------------------------- El suelo

    fun formaDelSuelo(): Path = poligono(listOf(p(FONDO_I, SUELO, 1.56f), Offset(FONDO_I, SUELO), Offset(FONDO_D, SUELO), p(FONDO_D, SUELO, 2.1f), Offset(ANCHO + 2f, ALTO + 2f), Offset(-2f, ALTO + 2f), Offset(-2f, p(FONDO_I, SUELO, 1.56f).y)))

    fun DrawScope.suelo(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("espacio", "suelo"))
        clipPath(formaDelSuelo()) {
            // Las planchas: seis hileras que se agrandan hacia quien mira. La del medio es la rejilla del pasillo.
            val filas = (0..6).map { fondo(it, 6, 1f, 2.02f) }
            val xs = listOf(-130f, -92f, -54f, -16f, 22f, 60f, 98f, 136f, 204f, 242f, 280f, 318f, 356f, 394f, 432f)
            for (j in 0 until filas.size - 1) for (i in 0 until xs.size - 1) {
                val s0 = filas[j]; val s1 = filas[j + 1]; val corre = if (j % 2 == 0 || xs[i] == 136f || xs[i + 1] == 136f) 0f else 12f
                val q = listOf(p(xs[i] + corre, SUELO, s0), p(xs[i + 1] + corre, SUELO, s0), p(xs[i + 1] + corre, SUELO, s1), p(xs[i] + corre, SUELO, s1))
                val rejilla = xs[i] == 136f
                placa(a, q, if (rejilla) lerp(C.CASCO_OSC, C.GOMA, 0.4f) else lerp(C.CASCO_OSC, C.CASCO, 0.36f), 0f, az, remaches = !rejilla)
                if (rejilla) clipPath(poligono(q)) { for (k in 0..11) { val t = k / 11f; drawLine(C.GOMA.copy(alpha = 0.5f), entreDos(q[0], q[1], t), entreDos(q[3], q[2], t), 0.5f * s0) }; for (k in 1..3) { val t = k / 4f; drawLine(C.GOMA.copy(alpha = 0.5f), entreDos(q[0], q[3], t), entreDos(q[1], q[2], t), 0.5f * s0) } }
            }
            // El brillo de cada lámpara encendida en las planchas, y los roces del ir y venir.
            for (f in a.focos.take(3)) if (f.fuerza > 0.1f) { val x = f.p.x + (f.p.x - 170f) * 0.3f; mancha(Offset(x, 236f), 74f, 13f, f.color, 0.2f * f.fuerza); mancha(Offset(x, 250f), 22f, 34f, Color.White, 0.07f * f.fuerza) }
            repeat(34) { val o = Offset(az.entre(0f, ANCHO), az.entre(SUELO + 4f, ALTO)); val k = (o.y - 126f) / 160f; drawLine((if (az.real() > 0.5f) Color.White else C.SOMBRA).copy(alpha = 0.12f), o, o + Offset(az.entre(4f, 16f) * k, az.entre(-1f, 1f)), 0.5f * k, StrokeCap.Round) }
            // Las esquinas de cinta naranja que marcan dónde va cada cosa, y las manchas de riego al pie de la torre y de las algas.
            for ((wx, s) in listOf(74f to 1.5f, 128f to 1.5f, 196f to 1.36f, 296f to 1.36f)) { val o = p(wx, SUELO, s); val k = s * 0.8f; drawLine(a.en(o, C.NARANJA).copy(alpha = 0.8f), o, o + Offset(7f * k, 0f), 1.2f * k); drawLine(a.en(o, C.NARANJA).copy(alpha = 0.8f), o, o + Offset(-1.6f * k, 4f * k), 1.2f * k) }
            mancha(Offset(46f, 276f), 30f, 5f, C.VERDE_OSC, 0.2f); mancha(Offset(350f, 280f), 26f, 4f, C.VERDE_OSC, 0.22f); mancha(Offset(120f, 218f), 22f, 4f, C.SOMBRA, 0.14f)
            // La sombra al pie de las paredes.
            drawLine(C.SOMBRA.copy(alpha = 0.3f), Offset(FONDO_I, SUELO + 1.4f), Offset(FONDO_D, SUELO + 1.4f), 2.8f)
            drawLine(C.SOMBRA.copy(alpha = 0.26f), Offset(FONDO_I, SUELO) + Offset(1f, 1.4f), p(FONDO_I, SUELO, 1.56f) + Offset(1.4f, 2f), 3f)
            drawLine(C.SOMBRA.copy(alpha = 0.26f), Offset(FONDO_D, SUELO) + Offset(-1f, 1.4f), p(FONDO_D, SUELO, 2.1f) + Offset(-1.4f, 2.4f), 3f)
            // La alfombra de agarre, por donde anda el gato: azul, raída por el centro, con su ribete.
            val alfombra = poligono(Offset(22f, 270.6f), Offset(338f, 270.6f), Offset(352f, ALTO + 2f), Offset(8f, ALTO + 2f))
            planoH(a, alfombra, 8f, 352f, 278f, lerp(C.TELA_AZUL, C.CASCO, 0.3f), pasos = 8)
            clipPath(alfombra) {
                repeat(90) { val o = Offset(az.entre(10f, 350f), az.entre(271f, ALTO)); drawLine((if (az.real() > 0.4f) Color.White else C.SOMBRA).copy(alpha = 0.1f), o, o + Offset(az.entre(2f, 6f), 0f), 0.6f) }
                repeat(5) { mancha(Offset(az.entre(60f, 310f), az.entre(275f, 284f)), az.entre(12f, 26f), az.entre(2f, 4f), C.TELA, 0.16f) }
                drawLine(a.en(Offset(180f, 272f), C.TELA).copy(alpha = 0.7f), Offset(22f, 272.2f), Offset(338f, 272.2f), 0.7f)
                var x = 24f; while (x < 338f) { drawLine(C.NARANJA.copy(alpha = 0.5f), Offset(x, 271.2f), Offset(x + 2.4f, 271.2f), 0.6f); x += 5.2f }
            }
            drawLine(C.SOMBRA.copy(alpha = 0.3f), Offset(22f, 270.2f), Offset(338f, 270.2f), 0.8f)
        }
    }

    // ---------------------------------------------------------------- La barra

    /** La barra que va del suelo al techo, para agarrarse: con el extintor, una bolsa colgada y una cinta. */
    fun barraCosa(m: Momento, a: Alumbrado): io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa =
        io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa(Path().apply { addRect(Rect(161f, -70f, 167f, 242f)); addRect(Rect(167f, 104f, 175f, 130f)) }, Offset(164f, 242f)) { barra(m, a) }   // la silueta sigue por encima del cuadro: la barra llega al techo, y su sombra también

    private fun DrawScope.barra(m: Momento, a: Alumbrado) {
        val x = 164f; val arriba = 16f; val abajo = 242f
        drawOval(a.en(Offset(x, abajo), C.ACERO), Offset(x - 6f, abajo - 2.8f), Size(12f, 4f))
        val fuste = Path().apply { addRect(Rect(x - 3f, arriba, x + 3f, abajo - 1f)) }
        drawPath(fuste, Brush.horizontalGradient(listOf(a.en(Offset(x - 3f, 130f), lerp(C.ACERO, Color.White, 0.3f), -1f), a.en(Offset(x, 130f), lerp(C.ACERO, C.SOMBRA, 0.4f)), a.en(Offset(x + 3f, 130f), lerp(C.ACERO, Color.White, 0.3f), 1f)), x - 3f, x + 3f))
        drawOval(a.en(Offset(x, arriba), C.ACERO), Offset(x - 4.6f, arriba - 1.4f), Size(9.2f, 3f))
        for (y in listOf(70f, 150f, 214f)) drawRect(a.en(Offset(x, y), C.GOMA), Offset(x - 3.6f, y), Size(7.2f, 2f))
        // La empuñadura de goma, a la altura de la mano.
        drawRoundRect(a.en(Offset(x, 170f), C.NARANJA), Offset(x - 3.7f, 158f), Size(7.4f, 22f), CornerRadius(2f)); for (k in 0..4) drawLine(C.SOMBRA.copy(alpha = 0.3f), Offset(x - 3.7f, 161f + k * 4f), Offset(x + 3.7f, 161f + k * 4f), 0.5f)
        // El extintor, en su abrazadera.
        val e = Offset(x + 8.6f, 120f)
        val botella = redondo(e.x - 4f, e.y - 14f, e.x + 4f, e.y + 8f, 3f)
        cuerpo(a, botella, e.x - 4f, e.x + 4f, e.y, C.ROJO)
        drawRect(a.en(e, C.GOMA), Offset(e.x - 1.6f, e.y - 17.6f), Size(3.2f, 4f)); drawLine(a.en(e, C.GOMA), e + Offset(1f, -16f), e + Offset(5f, -9f), 0.9f, StrokeCap.Round); drawRect(a.en(e, C.TELA), Offset(e.x - 3.6f, e.y - 5f), Size(7.2f, 4f))
        for (y in listOf(-9f, 4f)) drawLine(a.en(e, C.ACERO), Offset(x, e.y + y), Offset(e.x + 3.8f, e.y + y), 1.1f)
        // Una bolsa de malla colgada, con lo que haya hoy dentro.
        val hoy = m.azar("bolsa"); val b = Offset(x - 8f, 96f + (if (E.sinPeso(m)) -6f else 0f))
        drawLine(a.en(b, C.TELA), Offset(x - 1f, 84f), b + Offset(0f, -8f), 0.5f)
        val bolsa = Path().apply { moveTo(b.x - 3f, b.y - 8f); cubicTo(b.x - 9f, b.y - 2f, b.x - 8f, b.y + 9f, b.x, b.y + 10f); cubicTo(b.x + 7f, b.y + 9f, b.x + 6f, b.y - 2f, b.x + 3f, b.y - 8f); close() }
        drawPath(bolsa, a.en(b, listOf(C.TELA, C.TELA_AZUL, C.TRIGO)[hoy.entero(3)]))
        clipPath(bolsa) { for (k in -3..6) { drawLine(C.SOMBRA.copy(alpha = 0.25f), b + Offset(-9f, -8f + k * 3.4f), b + Offset(9f, -2f + k * 3.4f), 0.3f); drawLine(C.SOMBRA.copy(alpha = 0.25f), b + Offset(9f, -8f + k * 3.4f), b + Offset(-9f, -2f + k * 3.4f), 0.3f) }; drawOval(a.en(b, C.NARANJA), b + Offset(-4f, 1f), Size(6f, 6f)) }
    }

    // ---------------------------------------------------------------- Lo que enmarca: los cables y las mangueras

    private class Tendido(val desde: Offset, val hasta: Offset, val caida: Float, val color: Color, val grueso: Float)

    private val TENDIDOS = listOf(
        Tendido(Offset(-4f, 62f), Offset(164f, 24f), 22f, C.GOMA, 1.8f), Tendido(Offset(50f, -3f), Offset(164f, 30f), 9f, C.NARANJA, 1.1f),
        Tendido(Offset(164f, 22f), Offset(ANCHO + 4f, 30f), 26f, C.GOMA, 1.6f), Tendido(Offset(164f, 28f), Offset(300f, -3f), 12f, C.TELA, 1f),
    )

    /** Por dónde va cada cable: con peso cuelga; sin peso hace eses y hasta sube. Lo usan el dibujo y la sombra que dejan en las paredes. */
    fun cables(m: Momento): List<List<Offset>> = TENDIDOS.mapIndexed { n, t ->
        if (!E.sinPeso(m)) cuerda(t.desde, t.hasta, t.caida, 18) else cuerda(t.desde, t.hasta, -t.caida * 0.36f, 18).mapIndexed { i, q -> q + Offset(0f, sin(i * 0.9f + n + 1) * t.caida * 0.22f) }
    }

    /** El mamparo del fondo y los dos costados: donde cae la sombra de los cables. */
    fun formaDeLasParedes(): Path = Path().apply {
        addRect(Rect(FONDO_I, TECHO, FONDO_D, SUELO))
        addPath(poligono(Offset(FONDO_D, TECHO), p(FONDO_D, TECHO, 2.1f), p(FONDO_D, SUELO, 2.1f), Offset(FONDO_D, SUELO)))
        addPath(poligono(Offset(FONDO_I, TECHO), p(FONDO_I, TECHO, 1.56f), p(FONDO_I, SUELO, 1.56f), Offset(FONDO_I, SUELO)))
    }

    fun DrawScope.marco(m: Momento, a: Alumbrado) {
        val flota = E.sinPeso(m); val hoy = m.azar("cables")
        cables(m).forEachIndexed { n, pts ->
            val color = TENDIDOS[n].color; val grueso = TENDIDOS[n].grueso
            val trazo = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y) }
            drawPath(trazo, C.SOMBRA.copy(alpha = 0.3f), style = Stroke(grueso + 0.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(trazo, a.en(pts[9], color), style = Stroke(grueso, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(trazo, Color.White.copy(alpha = 0.18f), style = Stroke(grueso * 0.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        // Lo que cuelga de los cables: una pinza con un trapo, que cada día está en otro sitio, y las abrazaderas.
        val pts = cuerda(Offset(164f, 22f), Offset(ANCHO + 4f, 30f), if (flota) -9f else 26f, 18)
        for (i in listOf(4, 13)) drawRect(a.en(pts[i], C.ACERO), pts[i] + Offset(-1.6f, -1.6f), Size(3.2f, 3.2f))
        val pinza = pts[6 + hoy.entero(5)]
        val trapo = listOf(C.TELA, C.NARANJA, C.TELA_AZUL, C.VERDE)[hoy.entero(4)]
        if (!flota) drawPath(poligono(pinza + Offset(-5f, 0f), pinza + Offset(5f, 0.6f), pinza + Offset(4f, 15f), pinza + Offset(-1f, 13f), pinza + Offset(-5.4f, 15.6f)), a.en(pinza + Offset(0f, 8f), trapo))
        else drawPath(poligono(pinza + Offset(-2f, 0f), pinza + Offset(2f, 0f), pinza + Offset(11f, -9f), pinza + Offset(6f, -14f), pinza + Offset(-3f, -11f)), a.en(pinza + Offset(3f, -7f), trapo))
        drawRect(a.en(pinza, C.NARANJA), pinza + Offset(-1f, -1.4f), Size(2f, 3.4f))
        // La manguera gruesa que baja por la esquina de la derecha, con su boquilla.
        val manga = Path().apply { moveTo(372f, -3f); cubicTo(378f, 30f, 362f, 52f, 372f, 84f); cubicTo(378f, 104f, 368f, 120f, 374f, 138f) }
        drawPath(manga, C.SOMBRA.copy(alpha = 0.3f), style = Stroke(5f, cap = StrokeCap.Round)); drawPath(manga, a.en(Offset(372f, 70f), lerp(C.GOMA, C.CASCO_OSC, 0.4f), -1f), style = Stroke(3.6f, cap = StrokeCap.Round))
        var k = 0f; while (k < 1f) { val q = Offset(372f + 4f * sin(k * 9f), -3f + 141f * k); drawLine(Color.White.copy(alpha = 0.14f), q + Offset(-1.8f, 0f), q + Offset(1.8f, 0.6f), 0.5f); k += 0.045f }
        drawRoundRect(a.en(Offset(374f, 142f), C.NARANJA, -1f), Offset(371f, 136f), Size(6f, 9f), CornerRadius(1.6f))
        // En las fiestas, una ristra de banderines por delante; el luto, una cinta negra en la barra.
        if (E.fiesta(m) && !E.lamparitas(m)) { banderines(E.luzDeAdorno(m, a, Offset(100f, 50f)), Offset(44f, 40f), Offset(164f, 36f), 12f, listOf(C.NARANJA, C.TELA, C.CIAN, C.LUZ_CULTIVO), 9f); banderines(E.luzDeAdorno(m, a, Offset(260f, 50f)), Offset(164f, 40f), Offset(352f, 50f), 14f, listOf(C.TELA, C.TRIGO, C.NARANJA, C.CIAN), 9f) }
        if (E.luto(m)) { drawRect(a.en(Offset(164f, 100f), Color(0xFF1C1A24)), Offset(161f, 138f), Size(6f, 3.4f)); drawPath(poligono(Offset(161.4f, 141f), Offset(165f, 141f), Offset(163.4f, 156f), Offset(159.6f, 154f)), a.en(Offset(162f, 148f), Color(0xFF1C1A24))) }
    }
}
