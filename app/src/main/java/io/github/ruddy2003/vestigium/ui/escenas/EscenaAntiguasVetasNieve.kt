package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.VETAS
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.alturaEn
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * La ladera de las vetas tapada de nieve, con el paso cerrado: lisa, pero con su modelado y sin rayas iguales. Las lomas
 * suaves, cada una con su lado de luz y el de sombra; la sombra de la sierra de enfrente, con su borde; la huella de un alud
 * viejo, más lisa y más clara, con la lengua de bolos al pie; las rocas negras que asoman con su gorro de nieve; los alerces
 * pelados en grupos y algún pino con su nieve; y las bocas de las minas: el hueco negro con su visera de nieve, el labio de
 * nieve que las mete en la ladera y la escombrera tapada, que solo se adivina por su bulto.
 */
internal object VetasEnLaNieve {
    private val C = ColorAntiguas
    private const val LEJOS = 0.14f

    /** Una boca de mina vista en la nieve: dónde, su ancho, cuánto se ladea (grados), el largo de su escombrera, si está hundida. */
    class Boca(val q: Offset, val r: Float, val giro: Float, val escombrera: Float, val hundida: Boolean = false)

    /** Las lomas: su centro y su ancho. */
    private val LOMAS = listOf(Offset(36f, 170f) to 34f, Offset(98f, 146f) to 26f, Offset(146f, 196f) to 28f, Offset(72f, 204f) to 30f)

    /** Los alerces, en grupos: el centro de cada grupo, cuántos y lo ancho que es. */
    private val ALERCES = listOf(Triple(Offset(24f, 192f), 7, 22f), Triple(Offset(100f, 184f), 5, 14f), Triple(Offset(164f, 160f), 6, 16f), Triple(Offset(62f, 152f), 4, 12f))

    /** Los pinos que no pierden la aguja, en dos manchas: el centro y cuántos. */
    private val PINOS = listOf(Offset(40f, 214f) to 5, Offset(138f, 172f) to 4)

    /** La huella del alud viejo: el eje de la lengua, de arriba abajo. */
    private val ALUD = pts(110f, 124f, 115f, 146f, 111f, 168f, 119f, 190f, 125f, 208f)

    fun DrawScope.ladera(v: VistaAntiguas, bocas: List<Boca>) {
        val p = v.p; val az = fijo("vetas", "nieve")
        val lado = if (p.solALaDerecha) 1f else -1f
        val sombra = p.sombra(C.NIEVE_SOMBRA, LEJOS)
        drawRect(Brush.verticalGradient(listOf(p.por(C.NIEVE, 0.4f, LEJOS), p.por(C.NIEVE, 0.1f, LEJOS)), 100f, 230f), Offset(-10f, 90f), Size(200f, 150f))
        for ((c, w) in LOMAS) {
            mancha(c + Offset(-lado * w * 0.42f, w * 0.1f), w * 0.7f, w * 0.36f, sombra, 0.24f)
            mancha(c + Offset(lado * w * 0.28f, -w * 0.08f), w * 0.7f, w * 0.24f, Color.White, 0.45f * p.r.dia)
        }
        sombraDeLaSierra(p, sombra, az)
        alud(p, sombra, az)
        rocas(p, lado, sombra, az, bocas)
        alerces(p, lado, sombra, az)
        for (b in bocas) boca(p, b)
    }

    /** La sombra que echa la sierra de enfrente sobre lo alto de la ladera: un plano azul de borde quebrado, más fuerte con sol. */
    private fun DrawScope.sombraDeLaSierra(p: Pincel, sombra: Color, az: Azar) {
        val borde = rota(pts(-12f, 204f, 14f, 178f, 30f, 168f, 44f, 146f, 62f, 134f, 74f, 112f), az, 3f, 3)
        val forma = poligono(borde + listOf(Offset(74f, 90f), Offset(-12f, 90f)))
        drawPath(forma, sombra.copy(alpha = 0.18f + 0.3f * p.r.fuerza))
        drawPath(curva(borde), sombra.copy(alpha = 0.12f + 0.2f * p.r.fuerza), style = Stroke(1.2f))
    }

    /** La huella del alud viejo: una lengua más lisa y más clara que baja recta, con un borde en sombra y los bolos al pie. */
    private fun DrawScope.alud(p: Pincel, sombra: Color, az: Azar) {
        val n = ALUD.size
        val izq = rota(ALUD.mapIndexed { i, q -> q + Offset(-4f - 4f * i / (n - 1), 0f) }, az, 1.2f, 2)
        val der = rota(ALUD.mapIndexed { i, q -> q + Offset(4f + 6f * i / (n - 1), 0f) }, az, 1.2f, 2)
        // La lengua barrida: más lisa y más clara que la ladera, con la sombra del borde de un lado y el labio de nieve del otro.
        drawPath(poligono(izq + der.reversed()), sombra.copy(alpha = 0.2f))
        drawPath(poligono(izq.map { it + Offset(1.4f, 0f) } + der.reversed().map { it + Offset(-1f, 0f) }), p.por(C.NIEVE, 0.6f, LEJOS).copy(alpha = 0.85f))
        drawPath(curva(izq), sombra.copy(alpha = 0.6f), style = Stroke(1.1f))
        drawPath(curva(der), Color.White.copy(alpha = 0.5f * p.r.dia), style = Stroke(0.8f))
        val pie = ALUD.last()
        repeat(16) {
            val q = pie + Offset(az.entre(-13f, 13f), az.entre(-4f, 5f)); val w = az.entre(1.2f, 2.8f)
            drawPath(TrazosValle.blando(q, w, w * 0.55f, az, 7, 0.25f), p.por(C.NIEVE, 0.45f, LEJOS))
            drawLine(sombra.copy(alpha = 0.6f), q + Offset(-w * 0.8f, w * 0.5f), q + Offset(w * 0.8f, w * 0.5f), 0.6f)
        }
    }

    /** Las rocas negras que asoman: su peña oscura, el gorro de nieve encima y la sombra del lado contrario al sol. */
    private fun DrawScope.rocas(p: Pincel, lado: Float, sombra: Color, az: Azar, bocas: List<Boca>) {
        repeat(9) {
            val x = az.entre(8f, 178f); val y = az.entre(alturaEn(VETAS, x) + 10f, 214f); val q = Offset(x, y)
            if (bocas.any { (it.q - q).getDistance() < 12f } || kotlin.math.abs(x - TrazosValle.alturaEn(ALUD.map { Offset(it.y, it.x) }, y)) < 8f) return@repeat
            val w = az.entre(2f, 3.8f) * (0.7f + (y - 110f) / 200f)
            drawLine(sombra.copy(alpha = 0.6f), q + Offset(-lado * w * 0.2f, w * 0.5f), q + Offset(-lado * w * 1.6f, w * 0.6f), w * 0.5f, StrokeCap.Round)
            drawPath(TrazosValle.borron(q, w, w * 0.7f, az, 7, 0.35f), p.por(lerp(C.PIEDRA_OSC, C.ESCORIA, 0.3f), 0.1f, LEJOS))
            drawPath(TrazosValle.blando(q + Offset(0f, -w * 0.45f), w * 0.9f, w * 0.35f, az, 7, 0.25f), p.por(C.NIEVE, 0.45f, LEJOS))
        }
    }

    /** Los alerces pelados en grupos (un palo y su ramaje gris, sin aguja) y los pinos, oscuros, con la nieve en las ramas. */
    private fun DrawScope.alerces(p: Pincel, lado: Float, sombra: Color, az: Azar) {
        val tronco = p.medio(lerp(C.MADERA_OSC, C.ROCA, 0.3f), LEJOS); val ramaje = p.medio(lerp(C.MADERA, C.ROCA, 0.45f), LEJOS).copy(alpha = 0.5f)
        for ((c, n, w) in ALERCES) repeat(n) {
            val q = c + Offset(az.entre(-w / 2f, w / 2f), az.entre(-3f, 3f)); val h = 4.5f + (q.y - 110f) * 0.045f + az.entre(0f, 2f)
            drawLine(sombra.copy(alpha = 0.5f), q, q + Offset(-lado * h * 0.6f, 0.6f), 0.5f)
            drawPath(poligono(q + Offset(0f, -h), q + Offset(h * 0.22f, -h * 0.15f), q + Offset(-h * 0.22f, -h * 0.15f)), ramaje)
            drawLine(tronco, q, q + Offset(0f, -h), 0.5f)
        }
        val pino = p.medio(lerp(C.PINO, C.ESCORIA, 0.35f), LEJOS); val nieve = p.por(C.NIEVE, 0.45f, LEJOS)
        for ((c, n) in PINOS) repeat(n) {
            val q = c + Offset(az.entre(-8f, 8f), az.entre(-3f, 3f)); val h = 5f + (q.y - 110f) * 0.05f
            drawPath(poligono(q + Offset(0f, -h), q + Offset(h * 0.34f, 0f), q + Offset(-h * 0.34f, 0f)), pino)
            for (k in 1..2) { val y = q.y - h * k / 3f; val a = h * 0.34f * (1f - k / 3f); drawLine(nieve, Offset(q.x - a, y + 0.4f), Offset(q.x + a * 0.8f, y - 0.3f), 0.7f) }
        }
    }

    /**
     * Una boca en la nieve: la escombrera tapada, que solo se adivina por su bulto (su lado de sombra y nada de escoria); el
     * corte en la ladera, el hueco negro, la visera de nieve que sobresale con su espesor y su sombra debajo, y el labio de
     * nieve de abajo, que la mete en la ladera para que no flote. La hundida: solo su hondonada y dos troncos.
     */
    fun DrawScope.boca(p: Pincel, b: Boca) {
        val sombra = p.sombra(C.NIEVE_SOMBRA, LEJOS); val nieve = p.por(C.NIEVE, 0.45f, LEJOS); val lado = if (p.solALaDerecha) 1f else -1f
        val q = b.q; val r = b.r; val l = b.escombrera
        // El bulto de la escombrera tapada: una sombra corta y blanda pegada al labio, del lado contrario al sol.
        mancha(q + Offset(-lado * r * 0.5f, r * 0.9f + l * 0.08f), r * 1.5f + l * 0.06f, r * 0.55f, sombra, 0.3f)
        if (b.hundida) {
            mancha(q + Offset(0f, -r * 0.5f), r * 1.6f, r * 0.6f, sombra, 0.7f)
            val madera = p.medio(C.MADERA_OSC, LEJOS)
            drawLine(madera, q + Offset(-r * 0.9f, 0f), q + Offset(-r * 0.3f, -r * 0.9f), 0.7f); drawLine(madera, q + Offset(r * 0.4f, 0f), q + Offset(r * 1.1f, -r * 0.5f), 0.7f)
            return
        }
        rotate(b.giro, q) {
            val alto = r * 1.4f
            drawPath(poligono(q + Offset(-r * 1.5f, 0.8f), q + Offset(-r * 1.15f, -alto * 0.95f), q + Offset(r * 1.15f, -alto * 0.95f), q + Offset(r * 1.5f, 0.8f)), sombra.copy(alpha = 0.5f))
            drawPath(poligono(q + Offset(-r * 0.85f, 0.6f), q + Offset(r * 0.85f, 0.6f), q + Offset(r * 0.75f, -alto), q + Offset(-r * 0.75f, -alto)), p.medio(C.ESCORIA, LEJOS))
            drawPath(poligono(listOf(q + Offset(-r * 1.6f, -alto + 0.5f), q + Offset(-r * 1.1f, -alto - r * 0.5f), q + Offset(r * 0.2f, -alto - r * 0.7f), q + Offset(r * 1.4f, -alto - r * 0.45f), q + Offset(r * 1.7f, -alto + 0.4f), q + Offset(r * 0.8f, -alto - 0.1f), q + Offset(-r * 0.8f, -alto - 0.1f))), nieve)
            drawLine(sombra.copy(alpha = 0.75f), q + Offset(-r * 0.75f, -alto + 0.5f), q + Offset(r * 0.75f, -alto + 0.5f), 0.7f)
            drawPath(poligono(q + Offset(-r * 1.4f, 1.3f), q + Offset(-r * 0.6f, 0.2f), q + Offset(r * 0.5f, 0.3f), q + Offset(r * 1.4f, 1.3f)), nieve)
        }
    }
}
