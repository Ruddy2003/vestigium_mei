package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * Los tres humos de Vapor victoriano, que no se parecen entre sí (**[Diseño propio]** en la forma de pintarlos):
 * - [bocanadas]: el **humo de carbón** de una máquina, gris, que sube a borbotones con volumen (luz arriba y del lado
 *   del sol, sombra debajo);
 * - [vapor]: el **vapor** blanco y denso de una válvula o de un purgador, que sale con fuerza y se deshace enseguida;
 * - [penacho]: el **humo pardo** de una chimenea de fábrica, una cinta larga que el viento del día tiende.
 *
 * Ninguno usa manchas difuminadas: cada borbotón es la unión de unos pocos círculos de borde limpio, como una pincelada
 * de gouache. El viento va con signo: negativo, hacia la izquierda.
 */
internal object HumoVapor {
    private fun frac(x: Float): Float = ((x % 1f) + 1f) % 1f

    /** Añade a [forma] un borbotón de [lobulos] círculos alrededor de [c]. */
    private fun borbota(forma: Path, c: Offset, radio: Float, lobulos: Int, giro: Float, abre: Float, chato: Float, grosor: Float) {
        for (k in 0 until lobulos) {
            val an = giro + k * 6.2832f / lobulos
            val q = c + Offset(cos(an) * radio * abre, sin(an) * radio * abre * chato)
            val rr = radio * grosor * (0.86f + 0.14f * sin(an * 2.3f + giro))
            forma.addOval(Rect(q.x - rr, q.y - rr * chato.coerceAtLeast(0.7f), q.x + rr, q.y + rr * chato.coerceAtLeast(0.7f)))
        }
    }

    /**
     * El humo de carbón que sale de [boca] y sube hasta [alto], abriéndose hasta [ancho]. [viento] (con signo) lo tumba;
     * con mucho viento sube menos. [oscuro], de 0 (gris claro) a 1 (negro de encendido). [tinte] y [cuanto]: la luz de
     * un fuego cercano que lo tiñe por debajo.
     */
    fun DrawScope.bocanadas(l: Luces, r: Rayo, boca: Offset, alto: Float, ancho: Float, reloj: Float, viento: Float, oscuro: Float, semilla: Int, n: Int, fuerza: Float = 1f, ritmo: Float = 0.07f, tinte: Color? = null, cuanto: Float = 0f) {
        val propio = lerp(Color(0xFFDAD6CE), Color(0xFF34302E), oscuro)
        val medio = lerp(l.tono(propio), l.medio, 0.1f)
        val claro = lerp(medio, l.tono(Color(0xFFFFF6E4)), (0.5f - 0.25f * oscuro) * (0.35f + 0.65f * r.dia))
        val hondo = lerp(lerp(medio, l.tono(Color(0xFF26222C)), 0.3f), tinte ?: medio, cuanto)
        val lado = if (r.lat >= 0f) 1f else -1f
        val tumbado = abs(viento)
        val sube = alto * (1f - 0.5f * ((tumbado - 0.22f) / 0.78f).coerceIn(0f, 1f))
        val forma = Path()
        // Los borbotones viejos, arriba y casi deshechos, van primero; los recién salidos, encima.
        val orden = (0 until n).map { i -> frac(reloj * ritmo + i / n.toFloat() + semilla * 0.137f) to i }.sortedByDescending { it.first }
        for ((p, i) in orden) {
            val deshace = ((p - 0.5f) / 0.5f).coerceIn(0f, 1f)
            val a = (1f - deshace * deshace * (3f - 2f * deshace)) * minOf(1f, p * 12f) * fuerza
            if (a < 0.02f) continue
            val y = boca.y - sube * (1f - (1f - p).pow(1.6f))
            val x = boca.x + viento * alto * 0.95f * p.pow(1.3f) + sin(p * 6f + i * 2.4f + semilla) * ancho * 0.08f * p
            val rad = ancho * (0.1f + 0.36f * p.pow(0.8f))
            val c = Offset(x, y); val giro = i * 0.9f + p * 1.6f; val chato = 1f / (1f + 0.5f * tumbado * p)
            forma.rewind(); borbota(forma, c + Offset(-lado * rad * 0.14f, rad * 0.24f), rad, 5, giro, 0.56f, chato, 0.62f)
            drawPath(forma, hondo.copy(alpha = a.coerceIn(0f, 1f)))
            forma.rewind(); borbota(forma, c, rad, 6, giro + 0.5f, 0.52f, chato, 0.6f)
            drawPath(forma, medio.copy(alpha = a.coerceIn(0f, 1f)))
            forma.rewind(); borbota(forma, c + Offset(lado * rad * 0.2f, -rad * 0.3f), rad * 0.66f, 4, giro + 1.1f, 0.5f, chato, 0.54f)
            drawPath(forma, claro.copy(alpha = (a * 0.9f).coerceIn(0f, 1f)))
        }
    }

    /**
     * Un chorro de vapor que sale de [boca] hacia [hacia] (un vector de largo 1) y llega a [largo]: blanco y denso junto a
     * la boca, se abre hasta [ancho] y se deshace pronto. El [viento] se lo lleva al final.
     */
    fun DrawScope.vapor(l: Luces, r: Rayo, boca: Offset, hacia: Offset, largo: Float, ancho: Float, reloj: Float, viento: Float, semilla: Int, n: Int = 6, fuerza: Float = 1f, tinte: Color? = null, cuanto: Float = 0f) {
        // El vapor es lo más claro del cuadro: de noche se queda en un gris azulado, no en negro.
        val blanco = lerp(lerp(l.tono(Color.White), Color.White, 0.45f * (1f - 0.75f * l.noche)), tinte ?: Color.White, cuanto)
        val hondo = lerp(blanco, l.tono(Color(0xFF7C86A0)), 0.5f)
        val normal = Offset(-hacia.y, hacia.x)
        // El dardo: el chorro apretado junto a la boca.
        drawPath(poligono(boca + normal * (ancho * 0.03f), boca + hacia * (largo * 0.34f) + normal * (ancho * 0.12f), boca + hacia * (largo * 0.34f) - normal * (ancho * 0.12f), boca - normal * (ancho * 0.03f)), blanco.copy(alpha = (0.9f * fuerza).coerceIn(0f, 1f)))
        val forma = Path()
        val orden = (0 until n).map { i -> frac(reloj * 0.42f + i / n.toFloat() + semilla * 0.31f) to i }.sortedByDescending { it.first }
        for ((p, i) in orden) {
            val a = (1f - p).pow(1.5f) * minOf(1f, p * 14f) * fuerza * 1.25f
            if (a < 0.02f) continue
            val c = boca + hacia * (largo * (1f - (1f - p).pow(2f))) + Offset(viento * largo * 0.6f * p * p, -largo * 0.22f * p * p) + normal * (sin(i * 2.1f + semilla) * ancho * 0.08f * p)
            val rad = ancho * (0.1f + 0.44f * p.pow(0.7f)); val giro = i * 1.3f + p
            forma.rewind(); borbota(forma, c + Offset(0f, rad * 0.22f), rad, 5, giro, 0.55f, 1f, 0.6f)
            drawPath(forma, hondo.copy(alpha = (a * 0.7f).coerceIn(0f, 1f)))
            forma.rewind(); borbota(forma, c + Offset(0f, -rad * 0.1f), rad * 0.92f, 6, giro + 0.6f, 0.5f, 1f, 0.6f)
            drawPath(forma, blanco.copy(alpha = a.coerceIn(0f, 1f)))
        }
    }

    /**
     * El humo de una chimenea de fábrica: sale de [boca], sube un poco y el [viento] (con signo) lo tiende en una cinta de
     * [largo], cada vez más ancha y más tenue. [color], ya con la luz del momento. [brasa]: de noche, el resplandor de
     * los hornos que lo tiñe junto a la boca.
     */
    fun DrawScope.penacho(boca: Offset, largo: Float, grueso: Float, viento: Float, reloj: Float, color: Color, semilla: Int, alfa: Float = 0.6f, brasa: Color? = null, n: Int = 12) {
        val v = abs(viento).coerceIn(0f, 1f); val lado = if (viento < 0f) -1f else 1f
        val total = largo * (0.7f + 0.7f * v)
        val hondo = lerp(color, Color(0xFF1E1A1C), 0.3f)
        for (i in 0 until n) {
            val p = frac(reloj * 0.035f + i / n.toFloat() + semilla * 0.21f)
            val a = alfa * (1f - p).pow(1.15f) * minOf(1f, p * 12f)
            if (a < 0.02f) continue
            val sube = largo * (0.5f - 0.42f * v) * (1f - (1f - p).pow(2.2f))
            val corre = total * (0.4f + 0.6f * v) * p.pow(1.2f)
            val c = boca + Offset(lado * corre, -sube + sin(p * 9f + i * 1.7f + semilla) * grueso * 0.35f * p)
            val rx = grueso * (0.5f + 2.4f * p) * (1f + 0.9f * v); val ry = grueso * (0.5f + 1.5f * p) / (1f + 0.6f * v)
            val propio = if (brasa != null) lerp(color, brasa, (0.7f * (1f - p * 3f)).coerceIn(0f, 1f)) else color
            drawOval(hondo.copy(alpha = (a * 0.7f).coerceIn(0f, 1f)), Offset(c.x - rx, c.y - ry * 0.6f), Size(rx * 2f, ry * 2f))
            drawOval(propio.copy(alpha = a.coerceIn(0f, 1f)), Offset(c.x - rx * 0.94f, c.y - ry), Size(rx * 1.88f, ry * 1.7f))
        }
    }

    /** El hilo de humo de una chimenea de casa: tres volutas pequeñas. */
    fun DrawScope.hilo(boca: Offset, alto: Float, viento: Float, reloj: Float, color: Color, semilla: Int) {
        for (i in 0 until 4) {
            val p = frac(reloj * 0.05f + i / 4f + semilla * 0.37f)
            val a = 0.55f * (1f - p).pow(1.2f) * minOf(1f, p * 8f)
            val c = boca + Offset(viento * alto * 1.6f * p.pow(1.2f), -alto * (1f - abs(viento) * 0.5f) * p)
            val rr = alto * (0.08f + 0.22f * p)
            drawOval(color.copy(alpha = a), Offset(c.x - rr * (1f + abs(viento)), c.y - rr * 0.7f), Size(rr * 2f * (1f + abs(viento)), rr * 1.4f))
        }
    }

    /**
     * Lo que el vendaval arranca y se lleva de derecha a izquierda: hojas (o briznas, en invierno) y unos papeles que
     * dan vueltas. [hoja]: el color de las hojas, ya con la luz del momento.
     */
    fun DrawScope.volanderas(reloj: Float, hoja: Color, papel: Color, semilla: Int, suelo: Float) {
        for (i in 0 until 26) {
            val s = i * 12.9898f + semilla * 78.233f
            val rapidez = 150f + 110f * frac(sin(s) * 43758.5f)
            val y0 = 60f + (suelo - 70f) * frac(sin(s * 1.7f) * 24634.6f)
            val x = ANCHO + 12f - ((frac(sin(s * 2.3f) * 9631.2f) * (ANCHO + 24f) + reloj * rapidez) % (ANCHO + 24f))
            val y = y0 + 9f * sin(reloj * 3.1f + i * 1.9f) + 4f * sin(reloj * 7.3f + i)
            val giro = (reloj * 300f + i * 47f) % 360f
            rotate(giro, Offset(x, y)) { drawOval(hoja.copy(alpha = 0.9f), Offset(x - 1.9f, y - 0.8f), Size(3.8f, 1.6f)) }
        }
        for (i in 0 until 3) {
            val rapidez = 120f + 40f * i
            val x = ANCHO + 20f - ((i * 150f + reloj * rapidez) % (ANCHO + 40f))
            val y = suelo - 26f - 22f * i + 12f * sin(reloj * 2.2f + i * 2.4f)
            val giro = (reloj * 190f + i * 80f) % 360f; val canto = 0.35f + 0.65f * abs(sin(reloj * 3.3f + i))
            rotate(giro, Offset(x, y)) { drawRect(papel.copy(alpha = 0.95f), Offset(x - 3.4f, y - 2.4f * canto), Size(6.8f, 4.8f * canto)) }
        }
    }
}
