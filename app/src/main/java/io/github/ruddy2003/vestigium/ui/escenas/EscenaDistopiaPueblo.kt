package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.briznas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El pueblo de la hermana (estado `provincia`, dos días de la temporada 4). No es un cuadro nuevo (decisión 21): es la
 * misma plaza con su poste y su cartel, pero detrás no hay bloques, ni cámaras, ni farolas, y el cielo es el de todos.
 *
 * De lejos a cerca: las lomas azuladas, con la bruma al pie y el camino que sube por una de ellas; el río, que se
 * adivina por sus chopos; la vía del tren con sus postes del telégrafo, la estación con su depósito y un mercancías
 * parado; una aldea a la izquierda y una granja a la derecha; el llano, partido en parcelas de distinto cultivo y
 * distinto surco, con sus lindes y sus setos; la calle de tierra, que tuerce y se pierde detrás de un cambio de rasante;
 * las tres casas de teja, a la izquierda; y a la derecha, apartada del camino, la cooperativa con su explanada y sus silos.
 */
internal object PuebloDistopia {
    private val C = ColorDistopia
    private const val H = EscenaDistopia.H
    private const val BORDE = CiudadDistopia.BORDE

    /** Hacia dónde huyen los surcos del llano, y cuánto mide de fondo (en el cuadro) del horizonte a la plaza. */
    private const val EJE = 156.5f
    private const val FONDO = BORDE - 1f - H

    /** La profundidad del cambio de rasante: detrás de él el camino deja de verse. */
    private const val CRESTA = 0.07f

    /** La altura (en el cuadro) de la vía del tren, que cruza el llano de lado a lado, al otro lado del cambio de rasante. */
    private const val VIA = 132.8f

    private val TEJA = Color(0xFFA85A3A)
    private val ADOBE = Color(0xFFB88A62)
    private val TIERRA = Color(0xFFC6B48C)
    private val TIERRA_OSC = Color(0xFF8A7658)
    private val ARADO = Color(0xFF9A8460)
    private val PALO = Color(0xFF5E4E3C)
    private val LUZ_CASA = Color(0xFFFFD48A)

    private val CHIMENEA = Offset(25.7f, 142f)
    private val TREN = Offset(177.4f, 128.8f)

    private fun yDe(u: Float) = H + 2f + FONDO * u

    /** Un punto del llano: a [X] del eje (medido donde el llano llega a la plaza), a la profundidad [u]. */
    private fun fc(X: Float, u: Float) = Offset(EJE + X * u, yDe(u))

    /** Cuánto se ha torcido el camino, hacia la izquierda, a la profundidad [u]: nada hasta lejos; luego, cada vez más. */
    private fun curvaDelCamino(u: Float) = if (u >= 0.3f) 0f else -22f * (1f - u / 0.3f) * (1f - u / 0.3f)

    /** Un punto de la calle de tierra: a [X] de su eje (de −71 a 71 al llegar a la plaza), a la profundidad [u]. */
    private fun rc(X: Float, u: Float) = Offset(EJE + curvaDelCamino(u) + (22.5f + X) * u, yDe(u))

    private fun lejosDe(u: Float) = (0.6f - 0.62f * u).coerceIn(0.04f, 0.6f)
    private fun cara(l: Luces, r: Rayo, propio: Color, hacia: Float, lejos: Float) = CiudadDistopia.cara(l, r, propio, hacia, lejos)
    private fun frente(r: Rayo) = CiudadDistopia.frente(r)
    private fun lado(r: Rayo, mira: Float) = CiudadDistopia.lado(r, mira)

    fun DrawScope.pueblo(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("distopia", "pueblo"))
        val est = m.ficha.estacion
        val verde = lerp(hierbaDe(est), Color(0xFF5E7A4A), 0.5f)
        val luces = l.noche > 0.3f
        val despiertos = luces && m.hacer != Hacer.DORMIR
        lomas(l, est, fijo)
        // El suelo de debajo de todo: el color de las cunetas y las lindes.
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(verde, ARADO, 0.35f), 0.6f), l.tono(lerp(verde, ARADO, 0.3f), 0.06f)), H, BORDE), Offset(0f, H), Size(ANCHO, BORDE - H + 1f))
        campos(m, l, r, est, verde)
        lejania(m, l, r, est, verde, luces, despiertos)
        // El cambio de rasante: el lomo del llano coge la luz, y lo de detrás queda un punto más bajo.
        drawLine(l.tono(C.LUZ, 0.45f).copy(alpha = 0.3f), Offset(0f, yDe(CRESTA) - 0.2f), Offset(ANCHO, yDe(CRESTA) - 0.2f), 0.8f)
        mancha(Offset(150f, yDe(CRESTA) - 1.8f), 150f, 1.4f, l.tono(Color(0xFF4E5E4A), 0.5f), 0.22f)
        // Lo que queda a media distancia: la alameda de la linde, unos árboles sueltos y la granja de la derecha.
        val chopo = lerp(hierbaDe(est), Color(0xFF7E9A58), 0.5f)
        val az = Azar(Semilla.de("distopia", "alameda"))
        var x = -4f
        while (x < 40f) { chopo(l, r, Offset(x, yDe(0.2f) + az.entre(-0.6f, 0.6f)), az.entre(14f, 21f), 0.4f, lerp(chopo, Color(0xFF5E7A4A), az.real() * 0.5f), est); x += az.entre(5.4f, 9.6f) }
        for ((p, alto) in listOf(Offset(200f, yDe(0.16f)) to 9f, Offset(214f, yDe(0.28f)) to 13f, Offset(96f, yDe(0.12f)) to 7f, Offset(292f, yDe(0.3f)) to 14f)) arbolito(l, r, p, alto, lejosDe((p.y - H - 2f) / FONDO), lerp(verde, Color(0xFF4E6A40), 0.5f), est)
        granja(l, r, Offset(278f, yDe(0.1f)), despiertos, chopo, est)
        calle(l, r, verde, fijo)
        // Los postes del telégrafo del camino, por su orilla izquierda, con su hilo.
        val cimas = ArrayList<Offset>()
        var u = 0.96f
        while (u > 0.09f) { val pie = rc(-80f, u); val alto = 70f * u; val lejos = lejosDe(u)
            drawOval(r.color, Offset(pie.x - 3f * u + r.ax * 8f * u, pie.y - 0.8f * u), Size(6f * u + kotlin.math.abs(r.ax) * 16f * u, 1.8f * u), alpha = r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
            drawLine(l.tono(PALO, lejos), pie, pie + Offset(0.6f * u, -alto), 2f * u + 0.35f); drawLine(l.tono(lerp(PALO, C.LUZ, 0.3f), lejos), pie + Offset(if (r.lat >= 0f) 0.7f * u else -0.7f * u, 0f), pie + Offset(0.6f * u + (if (r.lat >= 0f) 0.7f * u else -0.7f * u), -alto), 0.5f * u + 0.1f)
            drawLine(l.tono(PALO, lejos), pie + Offset(-6f * u, -alto * 0.92f), pie + Offset(7f * u, -alto * 0.92f), 1.2f * u + 0.25f)
            for (s in listOf(-5f, 6f)) drawCircle(l.tono(C.HUESO, lejos), 1f * u + 0.15f, pie + Offset(s * u, -alto * 0.95f))
            cimas += pie + Offset(-5f * u, -alto * 0.95f); u *= 0.62f }
        for (i in 0 until cimas.size - 1) { val a = cimas[i]; val b = cimas[i + 1]; val medio = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f + kotlin.math.abs(a.y - b.y) * 0.14f + 1f)
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(medio.x, medio.y, medio.x, medio.y, b.x, b.y) }, l.tono(C.HOLLIN, 0.25f).copy(alpha = 0.8f), style = Stroke(0.5f)) }
        cimas.firstOrNull()?.let { a -> drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x - 20f, a.y + 8f, a.x - 60f, a.y + 2f, a.x - 130f, a.y - 26f) }, l.tono(C.HOLLIN, 0.1f).copy(alpha = 0.8f), style = Stroke(0.6f)) }
        // Las casas, de lejos a cerca; y enfrente, apartada del camino, la cooperativa.
        casa(m, l, r, 110f, 130f, 152f, 13f, Color(0xFFDCD6C6), 0.36f, 3, despiertos, 1)
        casa(m, l, r, 68f, 102f, 174f, 21f, Color(0xFFD4C8A8), 0.22f, 2, despiertos, 1)
        casa(m, l, r, 2f, 58f, 199f, 33f, Color(0xFFE2D8BE), 0.08f, 1, despiertos, 2)
        cooperativa(m, l, r, despiertos)
        // El huerto de la hermana, junto a la casa, con sus cañas y su valla; y las matas del borde de la plaza.
        for (i in 0..6) { val p = Offset(78f + i * 4.6f, 200f - i * 0.5f); drawLine(l.tono(Color(0xFF8A7A52), 0.08f), p, p + Offset(0.4f, -8f - (i % 3) * 1.6f), 0.5f); drawCircle(l.tono(Color(0xFF4E6A40), 0.08f), 1.8f + 0.5f * (i % 2), p + Offset(0f, -3f - (i % 3))) }
        for (i in 0..5) { val p = Offset(74f + i * 6.4f, 203.4f - i * 0.4f); drawLine(l.tono(PALO, 0.06f), p, p + Offset(0f, -5.4f + (i % 2) * 0.8f), 0.9f) }
        drawLine(l.tono(PALO, 0.06f), Offset(74f, 200.4f), Offset(106f, 198.6f), 0.6f)
        repeat(16) { val xx = fijo.entre(0f, ANCHO); mancha(Offset(xx, BORDE - fijo.entre(0f, 3f)), fijo.entre(5f, 12f), 2f, l.tono(lerp(verde, mult(verde, r.color, 0.6f), 0.4f), 0.06f), 0.7f) }
    }

    // ---------------------------------------------------------------- La lejanía

    /** Las lomas del fondo: tres filas, cada una más baja, más verde y más nítida; la bruma al pie y unos rodales de monte. */
    private fun DrawScope.lomas(l: Luces, est: Estacion, az: Azar) {
        monte(l, perfil(az, -60f, 460f, H + 2f, 27f, 0.3f, 30), H + 3f, Color(0xFF7484A4), 0.8f)
        mancha(Offset(190f, H - 2f), 240f, 6f, l.bruma, 0.4f)
        monte(l, perfil(az, -80f, 330f, H + 2f, 14f, 0.25f, 26), H + 3f, lerp(hierbaDe(est), Color(0xFF6C7C8E), 0.6f), 0.76f)
        monte(l, perfil(az, 170f, 470f, H + 2f, 9f, 0.22f, 20), H + 3f, lerp(hierbaDe(est), Color(0xFF5E6E62), 0.45f), 0.62f)
        // Los rodales de pinar de la loma de en medio y las parcelas de la ladera.
        val pinar = l.tono(Color(0xFF4E6252), 0.72f)
        repeat(9) { val x = az.entre(0f, 300f); mancha(Offset(x, H - az.entre(1f, 8f)), az.entre(7f, 20f), az.entre(1f, 2.4f), pinar, 0.4f) }
        repeat(5) { val x = az.entre(20f, 360f); mancha(Offset(x, H - az.entre(0f, 5f)), az.entre(8f, 18f), az.entre(0.8f, 1.6f), l.tono(Color(0xFFCFC58E), 0.72f), 0.3f) }
        mancha(Offset(180f, H + 1f), 230f, 3.4f, l.bruma, 0.5f)
        // El camino, que después del paso a nivel sube por la loma y la dobla.
        val senda = Path().apply { moveTo(142.6f, VIA - 0.4f); cubicTo(138f, 128f, 124f, 127f, 120f, 123.4f); cubicTo(117f, 120.6f, 126f, 119.6f, 129f, 117f) }
        drawPath(senda, l.tono(TIERRA, 0.66f).copy(alpha = 0.85f), style = Stroke(0.9f, cap = StrokeCap.Round))
    }

    /**
     * El llano: parcelas en seis franjas de profundidad, cada una con su cultivo (remolacha en surcos que huyen, cereal,
     * tierra arada de través, alfalfa segada a tiras, barbecho), sus linderos claros y, en algunas lindes, un seto.
     */
    private fun DrawScope.campos(m: Momento, l: Luces, r: Rayo, est: Estacion, verde: Color) {
        val az = Azar(Semilla.de("distopia", "parcelas"))
        val nieve = EscenaDistopia.nevado(m)
        val cereal = when (est) { Estacion.VERANO -> Color(0xFFD9C070); Estacion.OTONO -> Color(0xFFC2A868); Estacion.INVIERNO -> Color(0xFF8E9A62); else -> Color(0xFFA8B45E) }
        val bandas = listOf(0f, CRESTA, 0.14f, 0.24f, 0.38f, 0.58f, 1.03f)
        for (b in 0 until bandas.size - 1) {
            val ua = bandas[b]; val ub = bandas[b + 1]; val um = (ua + ub) / 2f
            val lejosA = lejosDe(ua) + (if (b == 0) 0.06f else 0f); val lejosB = lejosDe(ub) + (if (b == 0) 0.06f else 0f)
            for (lado in listOf(-1f, 1f)) {
                var X = if (lado < 0f) -48.5f else 93.5f
                var n = 0
                while (EJE + X * ub > -30f && EJE + X * ub < ANCHO + 30f && n < 12) {
                    val X1 = X + lado * az.entre(56f, 124f) / um.coerceAtLeast(0.05f)
                    val celda = poligono(fc(X, ua), fc(X1, ua), fc(X1, ub), fc(X, ub))
                    val tipo = (n * 2 + b * 3 + (if (lado < 0f) 0 else 2) + az.entero(2)) % 5
                    val propio = when (tipo) { 0 -> lerp(verde, ARADO, 0.4f); 1 -> cereal; 2 -> ARADO; 3 -> lerp(hierbaDe(est), Color(0xFF6E9A52), 0.5f); else -> Color(0xFFB4B286) }.let { if (nieve) lerp(it, C.NIEVE, 0.7f) else it }
                    drawPath(celda, Brush.verticalGradient(listOf(l.tono(propio, lejosA), l.tono(propio, lejosB)), yDe(ua), yDe(ub)))
                    clipPath(celda) {
                        val grueso = 0.4f + 1.5f * um
                        when (tipo) {
                            // La remolacha: surcos que huyen, verde oscuro sobre la tierra.
                            0 -> { val paso = 6.5f * maxOf(1f, 0.3f / um); var xs = minOf(X, X1); val hoja = l.tono(lerp(verde, Color(0xFF34503A), 0.55f), lejosB)
                                while (xs < maxOf(X, X1)) { drawLine(hoja.copy(alpha = 0.62f), fc(xs, ua), fc(xs, ub), grueso); drawLine(l.tono(lerp(verde, C.LUZ, 0.3f), lejosB).copy(alpha = 0.3f), fc(xs + paso * (if (r.lat >= 0f) 0.3f else -0.3f), ua), fc(xs + paso * (if (r.lat >= 0f) 0.3f else -0.3f), ub), grueso * 0.4f); xs += paso } }
                            // El cereal: rachas de viento, más claras y más oscuras, y la sombra del borde de acá.
                            1 -> { repeat(7) { val c = fc(az.entre(minOf(X, X1), maxOf(X, X1)), az.entre(ua, ub)); mancha(c, az.entre(14f, 40f) * (0.3f + um), az.entre(0.6f, 2f) * (0.3f + um), l.tono(if (it % 2 == 0) lerp(propio, Color.White, 0.3f) else lerp(propio, Color(0xFF7A6A3A), 0.35f), lejosB), 0.3f) }
                                drawLine(l.tono(lerp(propio, Color(0xFF6A5A30), 0.5f), lejosB).copy(alpha = 0.5f), fc(X, ub) + Offset(0f, -0.6f), fc(X1, ub) + Offset(0f, -0.6f), grueso) }
                            // La tierra arada: surcos de través, con el lomo a la luz.
                            2 -> { var us = ub; while (us > ua + 0.004f) { drawLine(l.tono(lerp(ARADO, Color(0xFF5E4A36), 0.6f), lejosB).copy(alpha = 0.5f), fc(X, us), fc(X1, us), 0.3f + 1.1f * us); drawLine(l.tono(lerp(ARADO, C.LUZ, 0.4f), lejosB).copy(alpha = 0.3f), fc(X, us) + Offset(0f, -0.8f * us - 0.3f), fc(X1, us) + Offset(0f, -0.8f * us - 0.3f), 0.3f + 0.5f * us); us -= (0.012f + 0.06f * us) } }
                            // La alfalfa: segada a tiras, unas ya claras y otras todavía oscuras.
                            3 -> { val paso = 30f * maxOf(1f, 0.3f / um); var xs = minOf(X, X1); var i = 0
                                while (xs < maxOf(X, X1)) { if (i % 2 == 0) drawPath(poligono(fc(xs, ua), fc(xs + paso * 0.9f, ua), fc(xs + paso * 0.9f, ub), fc(xs, ub)), l.tono(lerp(propio, Color(0xFFC8D08A), 0.5f), lejosB).copy(alpha = 0.5f)); xs += paso * az.entre(0.8f, 1.3f); i++ } }
                            // El barbecho: matas sueltas y, de cerca, unas flores.
                            else -> repeat(12) { val uu = az.entre(ua, ub); val c = fc(az.entre(minOf(X, X1), maxOf(X, X1)), uu); mancha(c, az.entre(2f, 6f) * (0.3f + uu), az.entre(0.6f, 1.4f) * (0.3f + uu), l.tono(if (it % 3 == 0) Color(0xFF6E7A4A) else Color(0xFF9A8E5E), lejosB), 0.5f)
                                if (uu > 0.4f && est != Estacion.INVIERNO && it % 2 == 0) drawCircle(l.tono(if (it % 4 == 0) C.ROJO else C.HUESO, lejosB), 0.5f + 0.5f * uu, c + Offset(az.entre(-4f, 4f), -1f)) }
                        }
                    }
                    // El lindero: una senda clara entre parcela y parcela; y en la linde de acá, a veces, un seto.
                    drawLine(l.tono(lerp(TIERRA, verde, 0.3f), lejosB).copy(alpha = 0.7f), fc(X1, ua), fc(X1, ub), 0.4f + 1.4f * um)
                    drawLine(l.tono(lerp(verde, Color(0xFF3E5236), 0.4f), lejosB).copy(alpha = 0.75f), fc(X, ub), fc(X1, ub), 0.5f + 1.6f * ub)
                    if (b in 1..4 && az.real() < 0.5f) { val t0 = az.entre(0f, 0.4f); val t1 = az.entre(0.6f, 1f); seto(l, r, fc(X + (X1 - X) * t0, ub), fc(X + (X1 - X) * t1, ub), 1.2f + 6.4f * ub, lejosB, lerp(verde, Color(0xFF3E5A36), 0.5f), az) }
                    X = X1; n++
                }
            }
        }
    }

    /**
     * Lo del otro lado del cambio de rasante: los chopos del río, la aldea, la vía del tren con sus postes y su hilo, el
     * paso a nivel, la estación con su depósito y su señal, y un mercancías parado. De noche, las pocas luces que hay.
     */
    private fun DrawScope.lejania(m: Momento, l: Luces, r: Rayo, est: Estacion, verde: Color, luces: Boolean, despiertos: Boolean) {
        val az = Azar(Semilla.de("distopia", "lejania"))
        val chopo = lerp(hierbaDe(est), Color(0xFF7E9A58), 0.5f)
        // El río no se ve: se ve la fila de chopos que lo sigue, que hace sus revueltas.
        var x = 0f
        while (x < 300f) {
            if (x !in 44f..100f && x !in 136f..150f && x !in 174f..236f) chopo(l, r, Offset(x, 131.2f + 1.3f * sin(x * 0.045f)), az.entre(5.6f, 11f), 0.56f, lerp(chopo, Color(0xFF5E7A4A), az.real() * 0.6f), est)
            x += az.entre(3.4f, 8f) * (if (az.real() < 0.14f) 2.6f else 1f)
        }
        // El terraplén de la vía, de lado a lado, con su balasto y su carril.
        drawRect(l.tono(Color(0xFF8E8676), 0.5f), Offset(0f, VIA - 0.5f), Size(ANCHO, 1.5f)); drawRect(l.tono(C.LUZ, 0.5f).copy(alpha = 0.4f), Offset(0f, VIA - 0.6f), Size(ANCHO, 0.35f))
        drawLine(l.tono(C.HOLLIN, 0.45f), Offset(0f, VIA - 0.1f), Offset(ANCHO, VIA - 0.1f), 0.35f)
        // Los postes del telégrafo de la vía, y su hilo, que cuelga de uno a otro.
        val palo = l.tono(PALO, 0.4f); var antes: Offset? = null
        x = 5f
        while (x < ANCHO + 10f) {
            val cima = Offset(x, VIA - az.entre(6.2f, 7f))
            if (x !in 198f..226f) { drawLine(palo, Offset(x, VIA), cima, 0.7f); drawLine(palo, cima + Offset(-1.8f, 0.9f), cima + Offset(1.8f, 0.9f), 0.5f) }
            antes?.let { a -> drawPath(Path().apply { moveTo(a.x, a.y + 0.9f); cubicTo(a.x + (cima.x - a.x) * 0.33f, a.y + 2.5f, a.x + (cima.x - a.x) * 0.67f, cima.y + 2.5f, cima.x, cima.y + 0.9f) }, palo.copy(alpha = 0.7f), style = Stroke(0.3f)) }
            antes = cima; x += az.entre(14f, 18f)
        }
        // La aldea del otro lado, a la izquierda: unas casas apiñadas, un granero más alto y sus árboles.
        for ((i, c) in listOf(Triple(50f, 5.4f, 3.2f), Triple(57f, 6.8f, 4.2f), Triple(65.6f, 5f, 3.2f), Triple(71.6f, 3.8f, 7.6f), Triple(77f, 6.6f, 3.8f), Triple(85.6f, 5f, 3f), Triple(92f, 4.4f, 2.6f)).withIndex())
            casita(l, r, c.first, 134.2f + (i % 2) * 0.5f, c.second, c.third, 0.5f, if (i == 3) Color(0xFF8A7A66) else TEJA, despiertos && i % 2 == 0)
        for (xx in listOf(46.6f, 63.4f, 82.6f, 98.4f)) arbolito(l, r, Offset(xx, 134.8f), az.entre(3.6f, 5.2f), 0.5f, lerp(verde, Color(0xFF4E6A40), 0.5f), est)
        // El paso a nivel: lo que se ve del camino al otro lado del cambio de rasante, y sus dos postes.
        drawPath(poligono(Offset(140.6f, yDe(CRESTA) - 0.6f), Offset(149.6f, yDe(CRESTA) - 0.6f), Offset(145f, VIA + 0.6f), Offset(141.6f, VIA + 0.6f)), l.tono(TIERRA, 0.52f))
        for (xx in listOf(139.6f, 147.4f)) { drawLine(l.tono(C.HUESO, 0.45f), Offset(xx, VIA + 0.6f), Offset(xx, VIA - 3.2f), 0.7f); drawLine(l.tono(C.ROJO, 0.45f), Offset(xx, VIA - 1.4f), Offset(xx, VIA - 2.4f), 0.7f) }
        // El mercancías parado: tres vagones y la máquina, sin nadie.
        for ((i, v) in listOf(153f to C.OXIDO, 160.4f to C.VERDE_GRIS, 167.8f to Color(0xFF6E5A46)).withIndex()) {
            drawRect(cara(l, r, v.second, frente(r), 0.46f), Offset(v.first, VIA - 4f + (i % 2) * 0.3f), Size(6.6f, 3.5f - (i % 2) * 0.3f)); drawRect(l.tono(C.LUZ, 0.46f).copy(alpha = 0.3f), Offset(v.first, VIA - 4f + (i % 2) * 0.3f), Size(6.6f, 0.4f)) }
        drawRect(l.tono(Color(0xFF2E2C30), 0.42f), Offset(175.2f, VIA - 3.6f), Size(7.6f, 3.2f)); drawRect(l.tono(Color(0xFF2E2C30), 0.42f), Offset(180.2f, VIA - 5.4f), Size(2.8f, 2.2f)); drawRect(l.tono(Color(0xFF2E2C30), 0.42f), Offset(176.6f, VIA - 5.6f), Size(1.4f, 2.2f))
        drawLine(l.tono(C.ROJO, 0.42f), Offset(175.2f, VIA - 0.9f), Offset(182.8f, VIA - 0.9f), 0.5f)
        // La estación: el edificio con su marquesina, el depósito del agua sobre sus patas y la señal.
        val est0 = Rect(202f, VIA - 6.6f, 222f, VIA)
        drawRect(cara(l, r, Color(0xFFDCD2B8), frente(r), 0.44f), est0.topLeft, est0.size); drawRect(cara(l, r, Color(0xFFDCD2B8), lado(r, 1f), 0.44f), Offset(est0.right - 3f, est0.top), Size(3f, est0.height))
        drawPath(poligono(Offset(est0.left - 1.2f, est0.top + 0.4f), Offset(est0.right + 1.2f, est0.top + 0.4f), Offset(est0.right - 1.6f, est0.top - 3f), Offset(est0.left + 1.6f, est0.top - 3f)), cara(l, r, TEJA, 0.5f * r.dia, 0.44f))
        drawRect(l.tono(C.HOLLIN, 0.44f).copy(alpha = 0.5f), Offset(est0.left - 3f, est0.top + 2.6f), Size(est0.width + 6f, 0.7f))
        for (i in 0..2) drawRect(if (luces) LUZ_CASA.copy(alpha = 0.9f) else l.tono(lerp(C.CRISTAL, C.HOLLIN, 0.3f), 0.44f), Offset(est0.left + 2.4f + i * 5.6f, est0.top + 3.6f), Size(1.8f, 2.2f))
        val tanque = Offset(229f, VIA)
        for (s in listOf(-2.2f, 2.2f)) drawLine(l.tono(PALO, 0.42f), tanque + Offset(s * 1.2f, 0f), tanque + Offset(s, -6f), 0.6f)
        drawRoundRect(Brush.horizontalGradient(if (r.lat >= 0f) listOf(cara(l, r, C.OXIDO, -0.7f, 0.42f), cara(l, r, C.OXIDO, 0.8f, 0.42f)) else listOf(cara(l, r, C.OXIDO, 0.8f, 0.42f), cara(l, r, C.OXIDO, -0.7f, 0.42f)), tanque.x - 3.6f, tanque.x + 3.6f), tanque + Offset(-3.6f, -11.4f), Size(7.2f, 5.6f), CornerRadius(1.2f))
        drawLine(l.tono(C.HOLLIN, 0.42f).copy(alpha = 0.6f), tanque + Offset(-3.6f, -8.6f), tanque + Offset(3.6f, -8.6f), 0.3f)
        val senal = Offset(190f, VIA)
        drawLine(l.tono(C.HOLLIN, 0.4f), senal, senal + Offset(0f, -9f), 0.6f); drawLine(l.tono(C.ROJO, 0.4f), senal + Offset(0f, -8.4f), senal + Offset(4f, -7.2f), 1f); drawLine(l.tono(C.HUESO, 0.4f), senal + Offset(2.6f, -7.6f), senal + Offset(3.2f, -7.4f), 1f)
        // De noche: la luz de la señal y, mientras hay gente despierta, alguna ventana.
        if (luces) { drawCircle(C.PILOTO, 0.7f, senal + Offset(0.2f, -9.4f)); resplandor(senal + Offset(0.2f, -9.4f), 4f, 0.6f, C.PILOTO); resplandor(Offset(est0.center.x, est0.top + 4.6f), 9f, 0.4f) }
    }

    /** Una granja sola, a media distancia: la casa, el pajar y dos árboles. */
    private fun DrawScope.granja(l: Luces, r: Rayo, pie: Offset, luz: Boolean, hoja: Color, est: Estacion) {
        arbolito(l, r, pie + Offset(-3.6f, 0.2f), 7.4f, 0.46f, lerp(hoja, Color(0xFF4E6A40), 0.5f), est)
        casita(l, r, pie.x, pie.y, 7.6f, 4.2f, 0.44f, TEJA, luz)
        casita(l, r, pie.x + 9.4f, pie.y + 0.4f, 9f, 5.4f, 0.44f, Color(0xFF8A7A66), false)
        chopo(l, r, pie + Offset(21f, 0.4f), 11f, 0.44f, hoja, est)
        mancha(pie + Offset(8f, 1.4f), 15f, 1.2f, l.tono(TIERRA, 0.44f), 0.6f)
    }

    /** Una casa lejana: la pared, con su costado a la luz o en sombra; el tejado; y, de noche, una ventana. */
    private fun DrawScope.casita(l: Luces, r: Rayo, x: Float, base: Float, w: Float, h: Float, lejos: Float, tejado: Color, luz: Boolean) {
        if (r.fuerza > 0.05f) drawOval(r.color, Offset(x - 0.6f + r.ax * h * 0.5f, base - 0.5f), Size(w + 1.2f, 1.2f), alpha = r.fuerza * 0.7f, blendMode = BlendMode.Multiply)
        val pared = Color(0xFFE2D8BE)
        drawRect(cara(l, r, pared, frente(r), lejos), Offset(x, base - h), Size(w, h))
        drawRect(cara(l, r, pared, lado(r, 1f), lejos), Offset(x + w * 0.68f, base - h), Size(w * 0.32f, h))
        drawPath(poligono(Offset(x - 0.6f, base - h + 0.2f), Offset(x + w + 0.6f, base - h + 0.2f), Offset(x + w - 0.8f, base - h - h * 0.5f), Offset(x + 0.8f, base - h - h * 0.5f)), Brush.verticalGradient(listOf(cara(l, r, tejado, 0.6f * r.dia, lejos), cara(l, r, tejado, 0.1f, lejos)), base - h * 1.5f, base - h))
        if (luz) { drawRect(LUZ_CASA, Offset(x + w * 0.22f, base - h * 0.62f), Size(1f, 1f)); resplandor(Offset(x + w * 0.3f, base - h * 0.5f), 3.4f, 0.5f) }
        else drawRect(l.tono(C.HOLLIN, lejos).copy(alpha = 0.6f), Offset(x + w * 0.22f, base - h * 0.62f), Size(0.9f, 1f))
    }

    /** Un chopo: la copa alta y estrecha, con su lado de luz y su lado de sombra, y la sombra corta que tumba. En invierno, pelado. */
    private fun DrawScope.chopo(l: Luces, r: Rayo, pie: Offset, alto: Float, lejos: Float, propio: Color, est: Estacion) {
        val w = alto * 0.15f + 0.6f
        if (r.fuerza > 0.05f) drawOval(r.color, Offset(pie.x - w + r.ax * alto * 0.2f, pie.y - w * 0.3f), Size(w * 2.4f + kotlin.math.abs(r.ax) * alto * 0.2f, w * 0.7f), alpha = r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
        val tronco = l.tono(Color(0xFF6A5A46), lejos)
        if (est == Estacion.INVIERNO) {
            drawLine(tronco, pie, pie + Offset(0f, -alto), maxOf(0.4f, alto * 0.04f))
            for (i in 1..5) { val y = pie.y - alto * (0.25f + 0.13f * i); val largo = w * (1.1f - 0.15f * i); for (s in listOf(-1f, 1f)) drawLine(tronco.copy(alpha = 0.8f), Offset(pie.x, y), Offset(pie.x + s * largo, y - alto * 0.12f), maxOf(0.3f, alto * 0.02f)) }
            return
        }
        drawLine(tronco, pie, pie + Offset(0f, -alto * 0.3f), maxOf(0.4f, alto * 0.04f))
        val color = if (est == Estacion.OTONO) lerp(propio, Color(0xFFC8A040), 0.6f) else propio
        drawOval(l.tono(mult(color, r.color, 0.75f * r.contraste), lejos), Offset(pie.x - w, pie.y - alto), Size(w * 2f, alto * 0.86f))
        drawOval(l.tono(lerp(color, C.LUZ, 0.32f * r.contraste), lejos), Offset(if (r.lat >= 0f) pie.x - w * 0.3f else pie.x - w, pie.y - alto + alto * 0.03f), Size(w * 1.3f, alto * 0.78f))
    }

    /**
     * Un árbol de copa ancha (una encina, un nogal): el tronco, corto y abierto en dos brazos, y la copa en tres masas,
     * más ancha que alta, con la luz por un lado, la sombra por debajo y su sombra en el suelo.
     */
    private fun DrawScope.arbolito(l: Luces, r: Rayo, pie: Offset, alto: Float, lejos: Float, propio: Color, est: Estacion) {
        val w = alto * 0.62f
        if (r.fuerza > 0.05f) drawOval(r.color, Offset(pie.x - w + r.ax * alto * 0.3f, pie.y - alto * 0.1f), Size(w * 2f + kotlin.math.abs(r.ax) * alto * 0.2f, alto * 0.24f), alpha = r.fuerza * 0.65f, blendMode = BlendMode.Multiply)
        val tronco = l.tono(Color(0xFF5A4A3A), lejos); val g = maxOf(0.5f, alto * 0.09f)
        drawLine(tronco, pie, pie + Offset(alto * 0.03f, -alto * 0.34f), g); drawLine(tronco, pie + Offset(0f, -alto * 0.26f), pie + Offset(-alto * 0.22f, -alto * 0.5f), g * 0.7f); drawLine(tronco, pie + Offset(0f, -alto * 0.26f), pie + Offset(alto * 0.26f, -alto * 0.46f), g * 0.7f)
        val color = if (est == Estacion.OTONO) lerp(propio, Color(0xFFB8863A), 0.5f) else propio
        val sombra = l.tono(mult(color, r.color, 0.8f * r.contraste), lejos); val luz = l.tono(lerp(color, C.LUZ, 0.3f * r.contraste), lejos); val s = if (r.lat >= 0f) 1f else -1f
        val masas = listOf(Triple(-0.42f, -0.6f, 0.36f), Triple(0.3f, -0.56f, 0.4f), Triple(-0.04f, -0.8f, 0.4f))
        if (est == Estacion.INVIERNO) { for ((dx, dy, _) in masas) drawLine(tronco.copy(alpha = 0.8f), pie + Offset(0f, -alto * 0.3f), pie + Offset(dx * alto * 1.2f, dy * alto * 1.1f), g * 0.4f); return }
        for ((dx, dy, rad) in masas) drawOval(sombra, Offset(pie.x + (dx - rad) * alto, pie.y + (dy - rad * 0.74f) * alto), Size(rad * 2f * alto, rad * 1.48f * alto))
        for ((dx, dy, rad) in masas) drawOval(luz, Offset(pie.x + (dx - rad * 0.62f + s * rad * 0.26f) * alto, pie.y + (dy - rad * 0.7f) * alto), Size(rad * 1.24f * alto, rad * 0.92f * alto))
    }

    /** Un seto en una linde, de [a] a [b]: matas apretadas, de distinto alto, con la luz por un lado y su sombra al pie. */
    private fun DrawScope.seto(l: Luces, r: Rayo, a: Offset, b: Offset, alto: Float, lejos: Float, propio: Color, az: Azar) {
        val largo = kotlin.math.abs(b.x - a.x); val n = (largo / (alto * 0.8f)).toInt().coerceIn(2, 60); val x0 = minOf(a.x, b.x)
        if (r.fuerza > 0.05f) drawRect(r.color, Offset(x0 + r.ax * alto * 0.3f, a.y - alto * 0.12f), Size(largo, alto * 0.3f), alpha = r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
        for (i in 0..n) { val x = x0 + largo * i / n + az.entre(-0.3f, 0.3f) * alto; val h = alto * az.entre(0.6f, 1.15f)
            drawOval(l.tono(mult(propio, r.color, 0.8f * r.contraste), lejos), Offset(x - h * 0.6f, a.y - h), Size(h * 1.2f, h * 1.05f))
            drawOval(l.tono(lerp(propio, C.LUZ, 0.28f * r.contraste), lejos), Offset(x - h * 0.36f + (if (r.lat >= 0f) h * 0.2f else -h * 0.2f), a.y - h * 0.98f), Size(h * 0.72f, h * 0.62f)) }
    }

    // ---------------------------------------------------------------- La calle

    /**
     * La calle de tierra que sale de la plaza: las dos rodadas de los carros, la hierba que crece en el centro y se mete
     * por los bordes, los charcos ya secos, con su barro cuarteado, y las piedras sueltas. Lejos tuerce a la izquierda y
     * se pierde detrás del cambio de rasante.
     */
    private fun DrawScope.calle(l: Luces, r: Rayo, verde: Color, az: Azar) {
        val us = listOf(1.03f, 0.86f, 0.7f, 0.56f, 0.44f, 0.34f, 0.27f, 0.21f, 0.16f, 0.12f, 0.09f, CRESTA)
        val calle = poligono(us.map { rc(-71f, it) } + us.reversed().map { rc(71f, it) })
        drawPath(calle, Brush.verticalGradient(listOf(l.tono(TIERRA, 0.56f), l.tono(TIERRA, 0.3f), l.tono(lerp(TIERRA, TIERRA_OSC, 0.15f), 0.05f)), H, BORDE))
        fun lejos(u: Float) = (0.55f - 0.55f * u).coerceAtLeast(0.04f)
        fun uAlAzar() = sqrt(az.real()).coerceIn(CRESTA + 0.01f, 1f)
        clipPath(calle) {
            // Lo pisado y lo sin pisar.
            repeat(26) { val u = uAlAzar(); val c = rc(az.entre(-66f, 66f), u); mancha(c, az.entre(8f, 24f) * u, az.entre(1f, 2.6f) * u, l.tono(if (it % 2 == 0) TIERRA_OSC else Color(0xFFE2D4B0), lejos(u)), 0.12f + 0.16f * u) }
            // Las rodadas: dos surcos hondos y, al lado, las de los días en que el carro se arrimó.
            for ((X, w, fuerza) in listOf(Triple(-25f, 3.4f, 1f), Triple(25f, 3.4f, 1f), Triple(-33f, 2f, 0.5f), Triple(19f, 2f, 0.5f))) {
                val pts = us.mapIndexed { i, u -> rc(X + 3f * sin(i * 0.9f + X) * minOf(1f, u * 3f), u) to u }
                for (i in 0 until pts.size - 1) { val (a, ua) = pts[i]; val (b, ub) = pts[i + 1]
                    drawPath(poligono(a + Offset(-w * ua, 0f), a + Offset(w * ua, 0f), b + Offset(w * ub, 0f), b + Offset(-w * ub, 0f)), l.tono(TIERRA_OSC, lejos(ua)).copy(alpha = fuerza * (0.2f + 0.4f * ua)))
                    drawLine(l.tono(Color(0xFFE6DAB8), lejos(ua)).copy(alpha = 0.4f * fuerza * ua), a + Offset(if (r.lat >= 0f) -w * ua else w * ua, 0f), b + Offset(if (r.lat >= 0f) -w * ub else w * ub, 0f), 0.5f * ua + 0.1f) }
            }
            // Los charcos secos: el barro claro, cuarteado.
            for ((X, u) in listOf(-44f to 0.9f, 40f to 0.64f, -10f to 0.44f)) { val c = rc(X, u); val w = 15f * u
                val charco = curva((0..8).map { val a = it / 8f * 6.283f; Offset(c.x + cos(a) * w * (0.8f + 0.3f * sin(it * 2.3f + X)), c.y + sin(a) * w * 0.2f) }).apply { close() }
                drawPath(charco, l.tono(TIERRA_OSC, lejos(u)).copy(alpha = 0.5f), style = Stroke(0.9f * u)); drawPath(charco, l.tono(Color(0xFFDDD0AC), lejos(u)))
                clipPath(charco) { repeat(7) { var q = c + Offset(az.entre(-w, w), az.entre(-w * 0.2f, w * 0.2f)); val g = Path().apply { moveTo(q.x, q.y); repeat(3) { q += Offset(az.entre(-4f, 4f) * u, az.entre(-1.4f, 1.4f) * u); lineTo(q.x, q.y) } }; drawPath(g, l.tono(TIERRA_OSC, lejos(u)).copy(alpha = 0.7f), style = Stroke(0.35f + 0.2f * u)) } } }
            // La hierba del centro, entre las rodadas, y la que se mete por los bordes.
            repeat(46) { val u = uAlAzar(); val c = rc(az.entre(-9f, 9f), u)
                mancha(c, az.entre(3f, 7f) * u, 1.1f * u, l.tono(lerp(verde, TIERRA, 0.3f), lejos(u)), 0.6f)
                repeat(3) { drawLine(l.tono(if (it == 0) lerp(verde, C.LUZ, 0.25f) else verde, lejos(u)), c + Offset((it - 1) * 1.6f * u, 0.4f * u), c + Offset((it - 1) * 1.6f * u + az.entre(-1.2f, 1.2f) * u, -az.entre(2f, 4.6f) * u), 0.5f * u + 0.15f) } }
            repeat(40) { val u = uAlAzar(); val s = if (it % 2 == 0) -1f else 1f; val c = rc(s * az.entre(60f, 73f), u)
                mancha(c, az.entre(5f, 11f) * u, 1.6f * u, l.tono(lerp(verde, mult(verde, r.color, 0.5f), 0.3f), lejos(u)), 0.75f)
                repeat(2) { drawLine(l.tono(verde, lejos(u)), c + Offset(it * 2f * u, 0f), c + Offset(it * 2f * u + az.entre(-1f, 1f) * u, -az.entre(2f, 5f) * u), 0.5f * u + 0.15f) } }
            // Las piedras sueltas, con su sombra corta.
            repeat(30) { val u = uAlAzar().coerceAtLeast(0.12f); val c = rc(az.entre(-64f, 64f), u); val rad = az.entre(0.6f, 1.5f) * u
                drawOval(l.tono(TIERRA_OSC, lejos(u)).copy(alpha = 0.6f), Offset(c.x - rad + r.ax * 0.6f * rad, c.y - rad * 0.2f), Size(rad * 2.2f, rad * 0.8f)); drawCircle(l.tono(if (it % 3 == 0) C.HORMIGON else Color(0xFF9E8E72), lejos(u)), rad, c + Offset(0f, -rad * 0.4f)) }
        }
        // Las cunetas: un filo de sombra a cada orilla, que deja los dos bordes del camino limpios hasta el cambio de rasante.
        for (s in listOf(-1f, 1f)) for (i in 0 until us.size - 1) { val a = rc(s * 72.5f, us[i]); val b = rc(s * 72.5f, us[i + 1])
            drawLine(l.tono(lerp(verde, Color(0xFF3E5236), 0.5f), lejos(us[i])).copy(alpha = 0.7f), a, b, 0.5f + 1.8f * us[i], StrokeCap.Round) }
    }

    /** Un desconchón: donde se cayó el enlucido asoma el adobe, con sus hiladas. */
    private fun DrawScope.desconchon(l: Luces, r: Rayo, c: Offset, rad: Float, lejos: Float, az: Azar) {
        val pts = (0 until 9).map { val a = it / 9f * 6.283f; val d = rad * az.entre(0.5f, 1.15f); Offset(c.x + cos(a) * d * 1.5f, c.y + sin(a) * d * 0.8f) }
        val forma = curva(pts + pts.first()).apply { close() }
        val junta = l.tono(Color(0xFF8A6244), lejos)
        drawPath(forma, l.tono(ADOBE, lejos))
        clipPath(forma) {
            var y = c.y - rad; var fila = 0
            while (y < c.y + rad) {
                drawLine(junta.copy(alpha = 0.7f), Offset(c.x - rad * 2f, y), Offset(c.x + rad * 2f, y), 0.35f)
                var x = c.x - rad * 2f + (fila % 2) * 2.4f
                while (x < c.x + rad * 2f) { drawLine(junta.copy(alpha = 0.6f), Offset(x, y), Offset(x, y + 2.2f), 0.3f); x += 4.8f }
                y += 2.2f; fila++
            }
            // El canto del enlucido da sombra dentro, arriba y del lado de la luz.
            translate(-r.lat * 0.8f, 0.9f) { drawPath(forma, mult(Color.White, r.color).copy(alpha = 0.5f * (0.4f + 0.6f * r.dia)), style = Stroke(1.4f), blendMode = BlendMode.Multiply) }
        }
        drawPath(forma, l.tono(Color.White, lejos).copy(alpha = 0.5f), style = Stroke(0.35f))
    }

    /**
     * Una casa del pueblo, de [x0] a [x1] y con el pie en [base]: la fachada enlucida, con sus brochazos, sus chorreras y
     * sus desconchones; el costado que da a la calle, con su hastial, a la luz o en sombra según la hora; el tejado de
     * teja, con sus hiladas, sus tejas nuevas y viejas y su cumbrera; el zócalo, la puerta y las ventanas con postigos.
     */
    private fun DrawScope.casa(m: Momento, l: Luces, r: Rayo, x0: Float, x1: Float, base: Float, alto: Float, pared: Color, lejos: Float, n: Int, encendida: Boolean, ventanas: Int) {
        val az = Azar(Semilla.de("distopia", "casa del pueblo", n))
        val k = alto / 30f
        val top = base - alto; val d = alto * 0.42f; val cumbre = top - alto * 0.46f
        val cFrente = CiudadDistopia.cara(l, r, pared, CiudadDistopia.frente(r), lejos)
        val cCostado = CiudadDistopia.cara(l, r, pared, CiudadDistopia.lado(r, 1f), lejos)
        val sucio = l.tono(Color(0xFF8A7A62), lejos)
        // La sombra que tumba en el suelo.
        if (r.fuerza > 0.05f) drawPath(poligono(Offset(x0, base), Offset(x1 + d, base - d * 0.14f), Offset(x1 + d + r.ax * alto * 0.5f, base - d * 0.14f + r.ay * alto * 0.3f), Offset(x0 + r.ax * alto * 0.5f, base + r.ay * alto * 0.3f)), r.color, alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
        // El costado y su hastial.
        val cumbreDer = Offset(x1 + d * 0.5f, cumbre - d * 0.1f)
        val costado = poligono(listOf(Offset(x1, base), Offset(x1, top), cumbreDer, Offset(x1 + d, top - d * 0.2f), Offset(x1 + d, base - d * 0.14f)))
        drawPath(costado, Brush.verticalGradient(listOf(cCostado, lerp(cCostado, sucio, 0.35f)), cumbre, base))
        clipPath(costado) {
            repeat(3) { val x = az.entre(x1, x1 + d); drawRect(Brush.verticalGradient(listOf(sucio.copy(alpha = 0.34f), Color.Transparent), top - d * 0.2f, top + az.entre(4f, 12f) * k), Offset(x, top - d * 0.2f), Size(az.entre(0.6f, 1.6f) * k, 16f * k)) }
            desconchon(l, r, Offset(x1 + d * 0.55f, base - alto * 0.3f), 3f * k, lejos, az)
            drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(x1, base - 4.4f * k), Size(d, 4.4f * k))
            // El ventanuco del desván.
            drawRect(l.tono(C.HOLLIN, lejos), Offset(x1 + d * 0.42f, top - alto * 0.2f), Size(2.4f * k, 3f * k))
        }
        // La fachada.
        drawRect(Brush.verticalGradient(listOf(lerp(cFrente, l.tono(C.LUZ, lejos), 0.1f), cFrente, lerp(cFrente, sucio, 0.3f)), top, base), Offset(x0, top), Size(x1 - x0, alto))
        clipRect(x0, top, x1, base) {
            repeat((14 * k).toInt() + 6) { val p = Offset(az.entre(x0, x1), az.entre(top, base)); drawLine((if (az.real() > 0.5f) l.tono(Color.White, lejos) else sucio).copy(alpha = 0.13f), p, p + Offset(az.entre(3f, 10f) * k, az.entre(-1f, 1f)), az.entre(0.8f, 2f) * k, StrokeCap.Round) }
            repeat(4) { val x = az.entre(x0, x1); val h = az.entre(4f, 12f) * k; drawRect(Brush.verticalGradient(listOf(sucio.copy(alpha = 0.34f), Color.Transparent), top, top + h), Offset(x, top), Size(az.entre(0.6f, 1.8f) * k, h)) }
            repeat(2 + n % 2) { desconchon(l, r, Offset(az.entre(x0 + 5f * k, x1 - 5f * k), az.entre(top + alto * 0.34f, base - 6f * k)), az.entre(2.4f, 4.4f) * k, lejos, az) }
            // La humedad que sube del suelo y el zócalo, con la pintura saltada.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sucio.copy(alpha = 0.3f)), base - 10f * k, base), Offset(x0, base - 10f * k), Size(x1 - x0, 10f * k))
            drawRect(l.tono(C.HORMIGON_OSC, lejos), Offset(x0, base - 4.4f * k), Size(x1 - x0, 4.4f * k))
            repeat((6 * k).toInt() + 3) { drawOval(cFrente.copy(alpha = az.entre(0.3f, 0.6f)), Offset(az.entre(x0, x1), base - az.entre(1f, 4.6f) * k), Size(az.entre(1.5f, 5f) * k, az.entre(0.6f, 1.4f) * k)) }
        }
        // La puerta, con su escalón, y las ventanas con sus postigos.
        val madera = l.tono(Color(0xFF5A4630), lejos); val postigo = CiudadDistopia.cara(l, r, lerp(C.VERDE_GRIS, Color(0xFF4A6A52), 0.5f), CiudadDistopia.frente(r), lejos)
        val px = x0 + (x1 - x0) * 0.6f
        drawRect(l.tono(C.HOLLIN, lejos), Offset(px - 0.6f * k, base - 14.6f * k), Size(7.2f * k, 14.6f * k)); drawRect(madera, Offset(px, base - 14f * k), Size(6f * k, 14f * k))
        for (i in 1..2) drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.5f), Offset(px + 2f * k * i, base - 14f * k), Offset(px + 2f * k * i, base), 0.3f * k + 0.1f)
        drawRect(l.tono(C.HORMIGON, lejos), Offset(px - 1.6f * k, base - 1.2f * k), Size(9.2f * k, 1.6f * k))
        val luzCasa = Color(0xFFFFD48A)
        for (i in 0 until ventanas) { val vx = x0 + (x1 - x0) * (if (i == 0) 0.2f else 0.84f) - 2.6f * k; val vy = top + alto * 0.3f
            drawRect(if (encendida) luzCasa else CiudadDistopia.cara(l, r, lerp(C.CRISTAL, C.HOLLIN, 0.3f), CiudadDistopia.frente(r), lejos), Offset(vx, vy), Size(5.2f * k, 6.4f * k))
            drawLine(l.tono(C.HUESO, lejos), Offset(vx + 2.6f * k, vy), Offset(vx + 2.6f * k, vy + 6.4f * k), 0.4f * k + 0.1f)
            drawRect(postigo, Offset(vx - 2.6f * k, vy - 0.3f * k), Size(2.4f * k, 7f * k)); drawRect(postigo, Offset(vx + 5.4f * k, vy - 0.3f * k), Size(2.4f * k, 7f * k))
            drawRect(l.tono(C.HORMIGON, lejos), Offset(vx - 0.8f * k, vy + 6.4f * k), Size(6.8f * k, 1f * k)) }
        // El alero da sombra en lo alto de la pared.
        drawRect(r.color, Offset(x0, top), Size(x1 - x0, 2f * k), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
        // El tejado: el faldón de delante, que recibe la luz de lleno.
        val teja = if (EscenaDistopia.nevado(m)) lerp(TEJA, C.NIEVE, 0.75f) else TEJA
        val a = Offset(x0 - 2f * k, top + 1.2f * k); val b = Offset(x1 + 1f * k, top + 1.2f * k); val cumbreIzq = Offset(x0 + d * 0.5f, cumbre)
        val faldon = poligono(a, b, cumbreDer, cumbreIzq)
        val tejaLuz = CiudadDistopia.cara(l, r, teja, 0.7f * r.dia, lejos); val tejaOsc = l.tono(mult(teja, r.color, 0.7f), lejos)
        drawPath(faldon, Brush.verticalGradient(listOf(tejaLuz, CiudadDistopia.cara(l, r, teja, 0.2f * r.dia, lejos)), cumbre, top))
        clipPath(faldon) {
            // Las hiladas, las canales y las tejas sueltas: unas nuevas, otras negras de verdín.
            val hiladas = (alto / 5.5f).toInt().coerceIn(2, 6)
            for (j in 1 until hiladas) { val t = j / hiladas.toFloat(); drawLine(tejaOsc.copy(alpha = 0.55f), a + (cumbreIzq - a) * t, b + (cumbreDer - b) * t + Offset(0f, az.entre(-0.4f, 0.4f)), 0.35f * k + 0.15f) }
            var x = 0.02f
            while (x < 1f) { val pa = a + (b - a) * x; val pb = cumbreIzq + (cumbreDer - cumbreIzq) * x; drawLine(tejaOsc.copy(alpha = 0.22f), pa, pb, 0.3f * k + 0.1f); x += az.entre(0.035f, 0.07f) / k.coerceAtLeast(0.5f) }
            repeat((22 * k).toInt() + 6) { val tx = az.real(); val ty = az.real(); val q = (a + (b - a) * tx) * (1f - ty) + (cumbreIzq + (cumbreDer - cumbreIzq) * tx) * ty
                drawOval(l.tono(listOf(Color(0xFFC87A52), Color(0xFF7A4630), Color(0xFF6A6A4A))[it % 3], lejos).copy(alpha = 0.55f), q, Size(az.entre(1.4f, 3f) * k, az.entre(0.8f, 1.4f) * k)) }
        }
        drawLine(tejaOsc, a + Offset(0f, 0.5f * k), b + Offset(0f, 0.5f * k), 0.8f * k + 0.2f)                                                     // el alero
        drawLine(l.tono(lerp(teja, Color(0xFFFFE0B0), 0.4f), lejos), cumbreIzq, cumbreDer, 1.1f * k + 0.2f, StrokeCap.Round)                     // la cumbrera
        drawLine(if (r.lat >= 0f) tejaLuz else tejaOsc, b, cumbreDer, 1.2f * k + 0.2f, StrokeCap.Round); drawLine(tejaOsc, cumbreDer, Offset(x1 + d + 1f * k, top - d * 0.2f + 1f * k), 1f * k + 0.2f, StrokeCap.Round)
        // La chimenea.
        val ch = cumbreIzq + (cumbreDer - cumbreIzq) * 0.3f
        drawRect(CiudadDistopia.cara(l, r, Color(0xFF8A5A40), CiudadDistopia.frente(r), lejos), Offset(ch.x - 2f * k, ch.y - 6f * k), Size(4f * k, 7f * k)); drawRect(CiudadDistopia.cara(l, r, Color(0xFF8A5A40), CiudadDistopia.lado(r, 1f), lejos), Offset(ch.x + 0.8f * k, ch.y - 6f * k), Size(1.2f * k, 7f * k))
        drawRect(l.tono(C.HOLLIN, lejos), Offset(ch.x - 2.6f * k, ch.y - 7f * k), Size(5.2f * k, 1.2f * k))
        // La hierba del pie del muro.
        briznas(l, x0, x1, base + 0.6f, lerp(hierbaDe(m.ficha.estacion), Color(0xFF7C8A56), 0.4f), az, 3.6f * k, 2.2f)
    }

    /**
     * La cooperativa, a media distancia y apartada del camino: la explanada de tierra apisonada, a la que se entra desde
     * la calle; los dos silos de grano, anchos, con sus aros, su escalera y su pasarela; el elevador que los llena; la
     * tolva; la nave de bloques con su tejado de chapa; y un remolque de remolacha en medio de la explanada.
     */
    private fun DrawScope.cooperativa(m: Momento, l: Luces, r: Rayo, encendida: Boolean) {
        val az = Azar(Semilla.de("distopia", "cooperativa")); val lejos = 0.14f
        val solDer = r.lat >= 0f
        val hierro = l.tono(C.HIERRO, lejos)
        // La explanada: va del borde del camino a la nave, y baja hasta cerca de la plaza.
        val explanada = poligono(listOf(rc(74f, 0.58f), Offset(ANCHO + 4f, yDe(0.58f)), Offset(ANCHO + 4f, yDe(0.84f)), rc(74f, 0.84f), rc(73f, 0.7f)))
        val firme = lerp(TIERRA, C.HORMIGON, 0.3f)
        drawPath(explanada, Brush.verticalGradient(listOf(l.tono(lerp(firme, TIERRA_OSC, 0.2f), 0.18f), l.tono(firme, 0.08f)), yDe(0.58f), yDe(0.84f)))
        clipPath(explanada) {
            repeat(16) { mancha(Offset(az.entre(216f, ANCHO), az.entre(yDe(0.58f), yDe(0.84f))), az.entre(8f, 24f), az.entre(0.8f, 2.2f), l.tono(if (it % 3 == 0) Color(0xFFE2D8B8) else TIERRA_OSC, lejos), 0.22f) }
            // Las rodadas del remolque, que entran desde el camino y giran hacia el portón.
            for (k in listOf(0f, 5f)) drawPath(Path().apply { moveTo(222f, 196f - k); cubicTo(250f, 194f - k, 290f, 192f - k, 330f, 183f - k * 0.4f) }, l.tono(TIERRA_OSC, lejos).copy(alpha = 0.5f), style = Stroke(1.5f, cap = StrokeCap.Round))
            mancha(Offset(300f, 189f), 9f, 2f, l.tono(C.HOLLIN, lejos), 0.3f)
        }
        // Las matas del borde de la explanada, que la separan del campo y de la plaza.
        repeat(14) { val x = az.entre(222f, ANCHO); mancha(Offset(x, yDe(0.84f) + az.entre(-0.6f, 1f)), az.entre(4f, 9f), 1.4f, l.tono(Color(0xFF6E8250), lejos), 0.75f) }
        fun silo(z: Rect, propio: Color, n: Int) {
            val a = Azar(Semilla.de("distopia", "silo", n))
            if (r.fuerza > 0.05f) drawPath(poligono(Offset(z.left, z.bottom), Offset(z.right, z.bottom), Offset(z.right + r.ax * 22f, z.bottom + r.ay * 6f + 2.4f), Offset(z.left + r.ax * 22f, z.bottom + r.ay * 6f + 2.4f)), r.color, alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(z.left - 1.5f, z.bottom - 2f), Size(z.width + 3f, 4f))
            val luz = cara(l, r, propio, 0.95f, lejos); val medio = cara(l, r, propio, 0.1f, lejos); val sombra = cara(l, r, lerp(propio, C.HORMIGON_OSC, 0.45f), -0.9f, lejos); val rebote = lerp(sombra, medio, 0.4f)
            val tonos = if (solDer) arrayOf(0f to rebote, 0.14f to sombra, 0.5f to medio, 0.8f to luz, 1f to medio) else arrayOf(0f to medio, 0.2f to luz, 0.5f to medio, 0.86f to sombra, 1f to rebote)
            drawRect(Brush.horizontalGradient(*tonos, startX = z.left, endX = z.right), z.topLeft, z.size)
            clipRect(z.left, z.top, z.right, z.bottom) {
                // Los aros, cada uno con su filo de luz, y las costuras de las chapas.
                var y = z.top + 5f; while (y < z.bottom - 3f) { drawLine(hierro.copy(alpha = 0.55f), Offset(z.left, y), Offset(z.right, y), 0.6f); drawLine(l.tono(C.LUZ, lejos).copy(alpha = 0.3f), Offset(z.left, y - 0.7f), Offset(z.right, y - 0.7f), 0.3f); y += a.entre(7f, 10f) }
                for (k in 1..3) drawLine(l.tono(C.HORMIGON_OSC, lejos).copy(alpha = 0.22f), Offset(z.left + z.width * k / 4f + a.entre(-1f, 1f), z.top), Offset(z.left + z.width * k / 4f, z.bottom), 0.3f)
                repeat(6) { val x = a.entre(z.left, z.right); val h = a.entre(10f, 36f); drawRect(Brush.verticalGradient(listOf(l.tono(C.OXIDO, lejos).copy(alpha = 0.4f), Color.Transparent), z.top, z.top + h), Offset(x, z.top), Size(a.entre(0.8f, 2.4f), h)) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.HOLLIN, lejos).copy(alpha = 0.3f)), z.bottom - 12f, z.bottom), Offset(z.left, z.bottom - 12f), Size(z.width, 12f))
            }
            // El tejado cónico, con su lado de luz, su respiradero y la sombra del alero.
            val cono = poligono(Offset(z.left - 1.6f, z.top + 0.6f), Offset(z.right + 1.6f, z.top + 0.6f), Offset(z.center.x + 2f, z.top - z.width * 0.3f), Offset(z.center.x - 2f, z.top - z.width * 0.3f))
            val techo = lerp(propio, C.HORMIGON_OSC, 0.5f)
            drawPath(cono, Brush.horizontalGradient(if (solDer) listOf(cara(l, r, techo, -0.6f, lejos), cara(l, r, techo, 0.9f, lejos)) else listOf(cara(l, r, techo, 0.9f, lejos), cara(l, r, techo, -0.6f, lejos)), z.left, z.right))
            drawRect(hierro, Offset(z.center.x - 1.6f, z.top - z.width * 0.3f - 2.4f), Size(3.2f, 2.6f))
            drawRect(r.color, Offset(z.left, z.top + 0.6f), Size(z.width, 1.6f), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
        }
        val chico = Rect(262f, 114f, 286f, 179f); val grande = Rect(230f, 99f, 264f, 181f)
        // El elevador: la torre de celosía que sube el grano, con su caseta arriba y los tubos que bajan a cada silo.
        val pie = Offset(291f, 179.4f); val cabeza = Offset(291f, 86f)
        drawLine(hierro, pie + Offset(-1.8f, 0f), cabeza + Offset(-1.8f, 0f), 0.7f); drawLine(l.tono(lerp(C.HIERRO, C.HUESO, 0.3f), lejos), pie + Offset(1.8f, 0f), cabeza + Offset(1.8f, 0f), 0.7f)
        var ey = pie.y; var ei = 0
        while (ey > cabeza.y + 4f) { val sig = ey - az.entre(5f, 7f); drawLine(hierro.copy(alpha = 0.7f), Offset(pie.x - 1.8f, ey), Offset(pie.x + 1.8f, sig), 0.35f); ey = sig; ei++ }
        drawRect(cara(l, r, C.VERDE_GRIS, frente(r), lejos), cabeza + Offset(-4f, -5f), Size(8f, 6f)); drawRect(cara(l, r, C.VERDE_GRIS, lado(r, 1f), lejos), cabeza + Offset(2f, -5f), Size(2f, 6f)); drawRect(l.tono(C.HOLLIN, lejos), cabeza + Offset(-4.6f, -6f), Size(9.2f, 1.2f))
        silo(chico, Color(0xFFCFCAB8), 2)
        drawLine(hierro, cabeza + Offset(-3f, 0f), Offset(chico.center.x + 1f, chico.top - chico.width * 0.3f - 1f), 1.5f, StrokeCap.Round)
        silo(grande, C.CAL, 1)
        drawLine(hierro, cabeza + Offset(-3f, -2f), Offset(grande.center.x + 3f, grande.top - grande.width * 0.3f - 1f), 1.6f, StrokeCap.Round)
        drawLine(l.tono(lerp(C.HIERRO, C.HUESO, 0.4f), lejos).copy(alpha = 0.7f), cabeza + Offset(-3f, -2.6f), Offset(grande.center.x + 3f, grande.top - grande.width * 0.3f - 1.6f), 0.4f)
        // La pasarela de un silo a otro y la escalera del grande, con su jaula arriba.
        drawLine(hierro, Offset(grande.right - 2f, grande.top + 12f), Offset(chico.left + 4f, chico.top + 0.6f), 0.9f); drawLine(hierro.copy(alpha = 0.7f), Offset(grande.right - 2f, grande.top + 9f), Offset(chico.left + 4f, chico.top - 2.4f), 0.4f)
        val ex = grande.left + grande.width * (if (solDer) 0.3f else 0.66f)
        for (s in listOf(-1.1f, 1.1f)) drawLine(hierro.copy(alpha = 0.8f), Offset(ex + s, grande.top + 3f), Offset(ex + s, grande.bottom - 1f), 0.35f)
        var py = grande.top + 5f; while (py < grande.bottom - 2f) { drawLine(hierro.copy(alpha = 0.6f), Offset(ex - 1.1f, py), Offset(ex + 1.1f, py), 0.3f); py += 3.4f }
        // La tolva de carga, sobre sus patas, al pie del silo grande.
        val tv = Offset(221f, 181.6f)
        for (s in listOf(-4f, 4f)) drawLine(hierro, tv + Offset(s, 0f), tv + Offset(s * 0.9f, -9f), 0.8f)
        drawPath(poligono(tv + Offset(-6f, -15f), tv + Offset(6f, -15f), tv + Offset(1.6f, -6f), tv + Offset(-1.6f, -6f)), Brush.horizontalGradient(if (solDer) listOf(cara(l, r, C.OXIDO, -0.7f, lejos), cara(l, r, C.OXIDO, 0.8f, lejos)) else listOf(cara(l, r, C.OXIDO, 0.8f, lejos), cara(l, r, C.OXIDO, -0.7f, lejos)), tv.x - 6f, tv.x + 6f))
        drawLine(hierro, tv + Offset(-6.4f, -15f), tv + Offset(6.4f, -15f), 0.9f); drawLine(hierro, tv + Offset(5f, -13f), Offset(grande.left + 1f, grande.bottom - 22f), 1f)
        drawOval(Color.Black.copy(alpha = 0.2f), tv + Offset(-6f, -1.2f), Size(12f, 2.6f))
        // La nave.
        val nave = Rect(294f, 153f, 366f, 180f)
        if (r.fuerza > 0.05f) drawPath(poligono(Offset(nave.left, nave.bottom), Offset(nave.right, nave.bottom), Offset(nave.right + r.ax * 12f, nave.bottom + 3f), Offset(nave.left + r.ax * 12f, nave.bottom + 3f)), r.color, alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
        val pared = cara(l, r, C.HORMIGON, frente(r), lejos)
        drawRect(Brush.verticalGradient(listOf(pared, lerp(pared, l.tono(C.HORMIGON_OSC, lejos), 0.45f)), nave.top, nave.bottom), nave.topLeft, nave.size)
        clipRect(nave.left, nave.top, nave.right, nave.bottom) {
            // Los bloques vistos, las chorreras y un paño enlucido que se cae.
            var y = nave.top + 3f; var fila = 0
            while (y < nave.bottom) { drawLine(l.tono(C.HORMIGON_OSC, lejos).copy(alpha = 0.3f), Offset(nave.left, y), Offset(nave.right, y), 0.3f); var x = nave.left + (fila % 2) * 4f; while (x < nave.right) { if (az.real() > 0.4f) drawLine(l.tono(C.HORMIGON_OSC, lejos).copy(alpha = 0.26f), Offset(x, y), Offset(x, y + 3.6f), 0.3f); x += 8f }; y += 3.6f; fila++ }
            repeat(6) { val x = az.entre(nave.left, nave.right); drawRect(Brush.verticalGradient(listOf(l.tono(C.HOLLIN, lejos).copy(alpha = 0.3f), Color.Transparent), nave.top, nave.top + az.entre(6f, 18f)), Offset(x, nave.top), Size(az.entre(0.8f, 2f), 18f)) }
            drawRect(l.tono(Color(0xFFDCD6C6), lejos).copy(alpha = 0.8f), Offset(nave.left + 2f, nave.top + 8f), Size(17f, 14f)); desconchon(l, r, Offset(nave.left + 15f, nave.top + 19f), 3f, lejos, az)
            // El emblema, pintado hace años.
            with(CiudadDistopia) { ojo(Offset(nave.left + 10.4f, nave.top + 14f), 12f, l.tono(C.ROJO, lejos), l.tono(C.ROJO_OSC, lejos), 0.6f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.HOLLIN, lejos).copy(alpha = 0.28f)), nave.bottom - 7f, nave.bottom), Offset(nave.left, nave.bottom - 7f), Size(nave.width, 7f))
        }
        // El portón corredero, a medio abrir, con su carril; y las ventanas.
        val p0 = nave.left + 26f
        drawRect(l.tono(C.HOLLIN, lejos), Offset(p0, nave.top + 9f), Size(24f, 18f)); drawRect(cara(l, r, C.VERDE_GRIS, frente(r), lejos), Offset(p0 - 4f, nave.top + 9f), Size(13f, 18f))
        for (i in 1..3) drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.4f), Offset(p0 - 4f + i * 3.2f, nave.top + 9f), Offset(p0 - 4f + i * 3.2f, nave.bottom), 0.3f)
        drawLine(hierro, Offset(p0 - 6f, nave.top + 8.4f), Offset(p0 + 28f, nave.top + 8.4f), 0.9f)
        if (encendida) drawRect(LUZ_CASA.copy(alpha = 0.5f), Offset(p0 + 9f, nave.top + 9f), Size(15f, 18f))
        for (i in 0..1) drawRect(if (encendida) LUZ_CASA else cara(l, r, lerp(C.CRISTAL, C.HOLLIN, 0.2f), frente(r), lejos), Offset(nave.left + 56f + i * 8f, nave.top + 12f), Size(5.4f, 4.4f))
        // El tejado de chapa, con sus ondas y su óxido.
        val chapa = poligono(Offset(nave.left - 3f, nave.top + 1f), Offset(nave.right + 3f, nave.top + 1f), Offset(nave.right - 3f, nave.top - 9f), Offset(nave.left + 5f, nave.top - 9f))
        drawPath(chapa, Brush.verticalGradient(listOf(cara(l, r, Color(0xFF9A9A8E), 0.7f * r.dia, lejos), cara(l, r, Color(0xFF8A8676), 0.2f * r.dia, lejos)), nave.top - 9f, nave.top))
        clipPath(chapa) { var x = nave.left - 3f; while (x < nave.right + 3f) { drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.2f), Offset(x, nave.top + 1f), Offset(x + (nave.center.x - x) * 0.1f + 4f, nave.top - 9f), 0.35f); x += az.entre(2.2f, 3.6f) }
            repeat(6) { mancha(Offset(az.entre(nave.left, nave.right), az.entre(nave.top - 8f, nave.top)), az.entre(3f, 8f), az.entre(1.2f, 2.6f), l.tono(C.OXIDO, lejos), 0.5f) } }
        drawRect(r.color, Offset(nave.left, nave.top + 1f), Size(nave.width, 2f), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
        // El remolque de remolacha, en medio de la explanada: la caja, su carga, las dos ruedas y la lanza apoyada en el suelo.
        val rem = Offset(252f, 192.6f)
        if (r.fuerza > 0.05f) drawOval(r.color, Offset(rem.x - 16f + r.ax * 5f, rem.y - 1.6f), Size(32f, 4f), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
        drawOval(Color.Black.copy(alpha = 0.2f), rem + Offset(-15f, -1.4f), Size(30f, 3.4f))
        drawPath(curva(listOf(rem + Offset(-13f, -10.6f), rem + Offset(-7f, -14f), rem + Offset(1f, -15f), rem + Offset(8f, -13.4f), rem + Offset(13f, -10.6f))).apply { lineTo(rem.x + 13f, rem.y - 10f); lineTo(rem.x - 13f, rem.y - 10f); close() }, cara(l, r, Color(0xFFB8A68E), 0.5f * r.dia, 0.1f))
        repeat(9) { drawCircle(l.tono(if (it % 2 == 0) Color(0xFF9A8A76) else Color(0xFFD0C2A8), 0.1f), 1.1f, rem + Offset(-10f + it * 2.5f, -11.4f - 2.4f * sin(it * 0.5f + 0.6f))) }
        val caja = Rect(rem.x - 14f, rem.y - 10.6f, rem.x + 14f, rem.y - 3.6f)
        drawRect(Brush.verticalGradient(listOf(cara(l, r, C.MADERA, frente(r), 0.1f), cara(l, r, lerp(C.MADERA, C.HOLLIN, 0.3f), frente(r), 0.1f)), caja.top, caja.bottom), caja.topLeft, caja.size)
        for (i in 1..3) drawLine(l.tono(C.HOLLIN, 0.1f).copy(alpha = 0.45f), Offset(caja.left, caja.top + i * 1.75f), Offset(caja.right, caja.top + i * 1.75f + az.entre(-0.2f, 0.2f)), 0.3f)
        for (x in listOf(caja.left + 0.6f, caja.center.x, caja.right - 0.6f)) drawLine(l.tono(C.HIERRO, 0.1f), Offset(x, caja.top - 0.6f), Offset(x, caja.bottom), 0.7f)
        for (x in listOf(rem.x - 6f, rem.x + 7f)) { drawCircle(l.tono(Color(0xFF2A2826), 0.1f), 3.4f, Offset(x, rem.y - 3f)); drawCircle(l.tono(C.HORMIGON_OSC, 0.1f), 1.3f, Offset(x, rem.y - 3f)) }
        drawLine(l.tono(C.HIERRO, 0.1f), Offset(caja.left, caja.bottom - 1f), rem + Offset(-23f, 0.2f), 1f, StrokeCap.Round)
        // Unas cajas apiladas junto a la nave y la hierba del pie.
        for ((i, x) in listOf(nave.left + 2f, nave.left + 10f, nave.left + 6f).withIndex()) { val y = nave.bottom + 0.6f - (if (i == 2) 5f else 0f); drawRect(cara(l, r, C.MADERA, frente(r), lejos), Offset(x, y - 5f), Size(7.4f, 5f)); drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.5f), Offset(x, y - 2.6f), Offset(x + 7.4f, y - 2.6f), 0.3f) }
        briznas(l, grande.left - 4f, chico.right + 2f, grande.bottom + 0.6f, lerp(hierbaDe(m.ficha.estacion), Color(0xFF7C8A56), 0.4f), az, 3.4f, 2.6f)
    }

    fun DrawScope.puebloVivo(m: Momento, l: Luces, reloj: Float) {
        val viento = -(0.25f + 0.75f * m.viento)
        if (m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE) humo(l, CHIMENEA, 34f, 8f, reloj, viento, 0.2f, 0.1f, 2, 8)
        humo(l, TREN, 20f, 6f, reloj, viento, 0.3f, 0.6f, 4, 6, 0.6f)
    }
}
