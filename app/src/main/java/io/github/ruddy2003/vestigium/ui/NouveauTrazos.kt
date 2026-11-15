package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los trazos que se repiten en las figuras de Art nouveau (`docs/diseno/temas/nouveau.md`): sus colores de dibujo y las
 * figuras que comparten las superficies: la línea de látigo, el tallo que crece, el iris, la libélula y el emblema (la
 * libélula en su marco de látigo). Todo con la curva tranquila del cartel de 1900: contorno de pluma, colores planos con
 * una luz suave que viene de arriba a la izquierda.
 */
object TrazosNouveau {
    /** El hierro de las entradas de metro de Guimard (fundición pintada de verde), a la luz y en sombra. */
    val HIERRO = Color(0xFF3D5A48)
    val HIERRO_SOMBRA = Color(0xFF24372B)
    /** El tallo y la hoja, verde salvia, a la luz y en sombra. */
    val TALLO = Color(0xFF8FA67E)
    val TALLO_SOMBRA = Color(0xFF5F7A55)
    /** El iris violeta, su lengua amarilla y la malva. */
    val IRIS = Color(0xFF7E5FA6)
    val IRIS_LENGUA = Color(0xFFE2B84A)
    val MALVA = Color(0xFFB48AB0)
    /** El oro pálido de los marcos y las letras doradas, con su brillo. */
    val ORO = Color(0xFFD9BE7A)
    val ORO_BRILLO = Color(0xFFF2E2B0)
    /** El vidrio de Nancy, que pasa de un color al otro: ámbar, violeta y verde. */
    val VIDRIO_AMBAR = Color(0xFFD9A14A)
    val VIDRIO_VIOLETA = Color(0xFF6B4E8C)
    val VIDRIO_VERDE = Color(0xFF7FA88A)
    /** La libélula: alas de nácar con nervadura de oro y cuerpo verde azul. */
    val NACAR = Color(0xFFE6E3EC)
    val LIBELULA = Color(0xFF2F6E7A)
    /** El pavo real, con el ojo de la pluma en oro. */
    val PAVO = Color(0xFF2F6E7A)
    val PAVO_VERDE = Color(0xFF5E9C6E)
    /** El carey del peine y sus manchas. */
    val CAREY = Color(0xFF7A4A22)
    val CAREY_MANCHA = Color(0xFFC08A4A)
    /** La piedra litográfica y las dos tintas de los carteles (solo dentro de un cartel). */
    val PIEDRA = Color(0xFFC9C2B0)
    val TINTA_ROJA = Color(0xFFB5533A)
    val TINTA_AZUL = Color(0xFF2C4A6B)
    /** De noche: la luz eléctrica con su halo, el farol del metro y el verde de los cafés. */
    val LUZ = Color(0xFFF5D488)
    val LUZ_HALO = Color(0xFFFFE9B8)
    val FAROL = Color(0xFFF2B55A)
    val CAFE = Color(0xFF6FB58C)
    /** El contorno de pluma de los carteles. */
    val PLUMA = Color(0xFF2A2530)

    // ------------------------------------------------------------------------------------------------ ayudantes

    /** Los puntos de una curva de Bézier cúbica (de [a] a [d], con los controles [b] y [c]), en [n] tramos. */
    fun bezier(a: Offset, b: Offset, c: Offset, d: Offset, n: Int): List<Offset> = List(n + 1) { i ->
        val t = i / n.toFloat(); val s = 1f - t
        Offset(s * s * s * a.x + 3 * s * s * t * b.x + 3 * s * t * t * c.x + t * t * t * d.x,
            s * s * s * a.y + 3 * s * s * t * b.y + 3 * s * t * t * c.y + t * t * t * d.y)
    }

    /** Una cinta de ancho variable a lo largo de [pts]: el ancho en cada punto lo da [ancho] (de 0 a 1 a lo largo). */
    fun cinta(pts: List<Offset>, ancho: (Float) -> Float): Path {
        val n = pts.size
        val izq = ArrayList<Offset>(n); val der = ArrayList<Offset>(n)
        for (i in 0 until n) {
            val a = pts[max(i - 1, 0)]; val b = pts[min(i + 1, n - 1)]
            val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.0001f)
            val w = ancho(i / (n - 1).coerceAtLeast(1).toFloat()) / 2
            izq.add(Offset(pts[i].x - dy / l * w, pts[i].y + dx / l * w)); der.add(Offset(pts[i].x + dy / l * w, pts[i].y - dx / l * w))
        }
        return Path().apply {
            moveTo(izq[0].x, izq[0].y); for (k in 1 until n) lineTo(izq[k].x, izq[k].y)
            for (k in n - 1 downTo 0) lineTo(der[k].x, der[k].y)
            close()
        }
    }

    /**
     * Los puntos de una línea de látigo: una curva larga en S de [desde] a [hasta] que, al llegar, se dobla sobre sí
     * misma y se enrosca en un rizo de radio [rizo] (vuelta y cuarto, cerrándose). [comba] es cuánto se curva la S (en
     * fracción del largo; con signo, hacia un lado o el otro) y [horario], hacia dónde se enrosca el rizo.
     */
    fun puntosLatigo(desde: Offset, hasta: Offset, rizo: Float, comba: Float = 0.28f, horario: Boolean = true): List<Offset> {
        val dx = hasta.x - desde.x; val dy = hasta.y - desde.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val nx = -dy / l; val ny = dx / l                        // la normal a la recta
        val b = Offset(desde.x + dx * 0.3f + nx * l * comba, desde.y + dy * 0.3f + ny * l * comba)
        val c = Offset(desde.x + dx * 0.72f - nx * l * comba * 0.8f, desde.y + dy * 0.72f - ny * l * comba * 0.8f)
        val curva = bezier(desde, b, c, hasta, 40)
        // La tangente al llegar, para que el rizo siga la curva sin quiebre.
        val tx = hasta.x - c.x; val ty = hasta.y - c.y; val tl = sqrt(tx * tx + ty * ty).coerceAtLeast(0.001f)
        val ux = tx / tl; val uy = ty / tl
        val s = if (horario) 1f else -1f
        // El centro del rizo queda a un lado de la tangente; el rizo arranca en [hasta].
        val cx = hasta.x - uy * rizo * s; val cy = hasta.y + ux * rizo * s
        val a0 = atan2(hasta.y - cy, hasta.x - cx)
        val vueltas = 2.5f * PI.toFloat()
        val rizoPts = List(36) { i ->
            val t = (i + 1) / 36f
            val r = rizo * (1f - 0.72f * t)
            val a = a0 + s * vueltas * t
            Offset(cx + r * cos(a), cy + r * sin(a))
        }
        return curva + rizoPts
    }

    /**
     * La línea de látigo, dibujada como un trazo de pluma que engorda en el medio de la S y se afina en el rizo. Debajo,
     * una sombra corta hacia abajo a la derecha (la luz viene de arriba a la izquierda).
     */
    fun DrawScope.latigo(desde: Offset, hasta: Offset, rizo: Float, grueso: Float, color: Color = HIERRO,
                         comba: Float = 0.28f, horario: Boolean = true, sombra: Boolean = true) {
        val pts = puntosLatigo(desde, hasta, rizo, comba, horario)
        val cuerpo = cinta(pts) { t ->
            // De la punta de salida (fina) al medio de la S (gruesa) y al rizo, que se va afinando.
            val f = if (t < 0.53f) 0.35f + 0.65f * sin(t / 0.53f * PI.toFloat() * 0.5f + 0.2f).coerceIn(0f, 1f) else 1f - 0.8f * ((t - 0.53f) / 0.47f)
            grueso * f.coerceIn(0.15f, 1f)
        }
        if (sombra) drawPath(cuerpo.copiaMovida(Offset(grueso * 0.35f, grueso * 0.45f)), Color.Black.copy(alpha = 0.14f))
        drawPath(cuerpo, color)
        // El brillo del lado de la luz, un hilo fino sobre la primera mitad de la S.
        if (grueso >= 2.5f) {
            val brillo = pts.subList(4, 30).map { Offset(it.x - grueso * 0.16f, it.y - grueso * 0.2f) }
            drawPath(cinta(brillo) { t -> grueso * 0.22f * sin(t * PI.toFloat()) }, lerp(color, Color.White, 0.45f).copy(alpha = 0.55f))
        }
    }

    private fun Path.copiaMovida(d: Offset): Path = Path().also { it.addPath(this, d) }

    /**
     * Una hoja larga de [base] a [punta], con [ancho] y una comba hacia un lado: de sauce (fina en los dos extremos y ancha
     * en el primer tercio) o, con [espada], la del iris (ancha en la base y afinándose hasta la punta).
     */
    fun hojaLanza(base: Offset, punta: Offset, ancho: Float, comba: Float = 0.12f, espada: Boolean = false): Path {
        val dx = punta.x - base.x; val dy = punta.y - base.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val nx = -dy / l; val ny = dx / l
        val medio = bezier(base, Offset(base.x + dx * 0.35f + nx * l * comba, base.y + dy * 0.35f + ny * l * comba),
            Offset(base.x + dx * 0.75f + nx * l * comba * 0.6f, base.y + dy * 0.75f + ny * l * comba * 0.6f), punta, 24)
        return cinta(medio) { t ->
            if (espada) ancho * (1f - Math.pow(t.toDouble(), 1.8).toFloat()).coerceIn(0f, 1f)
            else ancho * sin(PI.toFloat() * Math.pow(t.toDouble(), 0.7).toFloat()).coerceIn(0f, 1f)
        }
    }

    /**
     * El tallo que crece: sube desde [base] [alto] con una curva suave hacia [lado] (−1 o 1) y echa hojas alternas. [crecido]
     * (de 0 a 1) dice cuánto ha crecido: las hojas salen conforme el tallo pasa por ellas y, al final, se abre un capullo
     * de malva. Sirve para los bordes y para el fondo vivo.
     */
    fun DrawScope.tallo(base: Offset, alto: Float, crecido: Float = 1f, lado: Float = 1f, grueso: Float = alto * 0.018f,
                        color: Color = TALLO, sombra: Color = TALLO_SOMBRA, flor: Color = MALVA) {
        val k = crecido.coerceIn(0f, 1f)
        if (k <= 0.01f) return
        val todo = bezier(base, Offset(base.x + lado * alto * 0.16f, base.y - alto * 0.32f),
            Offset(base.x - lado * alto * 0.1f, base.y - alto * 0.7f), Offset(base.x + lado * alto * 0.12f, base.y - alto), 48)
        val n = max(2, (todo.size * k).toInt())
        val pts = todo.subList(0, n)
        // Las hojas, alternas, de abajo arriba; cada una crece cuando el tallo ya pasó por ella.
        for (i in 0 until 5) {
            val f = 0.16f + i * 0.15f
            if (k < f) break
            val p = todo[(f * (todo.size - 1)).toInt()]
            val l = alto * (0.2f - i * 0.022f) * ((k - f) / 0.12f).coerceIn(0f, 1f)
            val s = if (i % 2 == 0) 1f else -1f
            val punta = Offset(p.x + s * l * 0.9f, p.y - l * 0.55f)
            val hoja = hojaLanza(p, punta, l * 0.32f, 0.18f * s)
            drawPath(hoja, if (s * lado > 0) color else sombra)
            drawLine(sombra.copy(alpha = 0.6f), p, Offset(p.x + (punta.x - p.x) * 0.8f, p.y + (punta.y - p.y) * 0.8f), grueso * 0.35f, StrokeCap.Round)
        }
        drawPath(cinta(pts) { t -> grueso * (1f - 0.55f * t) }, sombra)
        drawPath(cinta(pts.map { Offset(it.x - grueso * 0.18f, it.y) }) { t -> grueso * 0.5f * (1f - 0.55f * t) }, color)
        // El capullo que se abre arriba.
        if (k > 0.85f) {
            val a = (k - 0.85f) / 0.15f
            val p = pts.last()
            val r = alto * 0.05f * a
            for (j in 0 until 3) {
                val ang = -PI.toFloat() / 2 + (j - 1) * 0.55f * a
                val q = Offset(p.x + cos(ang) * r * 1.4f, p.y + sin(ang) * r * 1.4f)
                drawPath(hojaLanza(p, q, r * 1.3f, 0.1f * (j - 1)), lerp(flor, Color.White, 0.12f * j))
            }
        }
    }

    /**
     * El iris: el tallo con sus hojas de espada y la flor arriba, con sus tres pétalos que suben (los estandartes) y sus
     * tres que caen (los «sépalos», con la lengua amarilla). [base] es el pie del tallo y [alto], hasta lo alto de la flor.
     */
    fun DrawScope.iris(base: Offset, alto: Float, flor: Color = IRIS, hoja: Color = TALLO, hojaSombra: Color = TALLO_SOMBRA,
                       contorno: Color? = null) {
        val u = alto / 100f
        // Las hojas de espada, de atrás adelante.
        val hojas = listOf(Triple(-22f, 74f, -0.08f), Triple(16f, 86f, 0.06f), Triple(-8f, 56f, 0.14f), Triple(26f, 48f, -0.12f))
        hojas.forEachIndexed { i, (dx, h, c) ->
            val p = hojaLanza(Offset(base.x + dx * u * 0.25f, base.y), Offset(base.x + dx * u, base.y - h * u), 8f * u, c, espada = true)
            drawPath(p, if (i % 2 == 0) hojaSombra else hoja)
            contorno?.let { drawPath(p, it, style = Stroke(0.9f * u)) }
        }
        // El tallo.
        val cima = Offset(base.x + 2f * u, base.y - 72f * u)
        drawLine(hojaSombra, base, cima, 3.2f * u, StrokeCap.Round)
        drawLine(hoja, Offset(base.x - 0.6f * u, base.y), Offset(cima.x - 0.6f * u, cima.y), 1.6f * u, StrokeCap.Round)
        val fc = Offset(cima.x, cima.y - 6f * u)
        val oscuro = lerp(flor, Color.Black, 0.3f)
        val claro = lerp(flor, Color.White, 0.28f)
        // Los tres estandartes: el del medio sube recto, los otros dos se abren.
        listOf(-0.42f to 0.9f, 0.42f to 0.9f, 0f to 1f).forEach { (ang, k) ->
            val a = -PI.toFloat() / 2 + ang
            val punta = Offset(fc.x + cos(a) * 24f * u * k, fc.y + sin(a) * 24f * u * k)
            val p = petalo(fc, punta, 15f * u * k)
            drawPath(p, if (ang == 0f) claro else flor)
            contorno?.let { drawPath(p, it, style = Stroke(0.9f * u)) }
        }
        // Las tres que caen, colgando, con la lengua amarilla cerca de la base.
        listOf(-0.72f, 0.72f, 0f).forEach { ang ->
            val a = PI.toFloat() / 2 + ang
            val largo = if (ang == 0f) 24f else 28f
            val desde = Offset(fc.x, fc.y + 2f * u)
            val punta = Offset(fc.x + cos(a) * largo * u, desde.y + sin(a) * largo * u)
            val p = petalo(desde, punta, 18f * u)
            drawPath(p, Brush.linearGradient(listOf(flor, oscuro), desde, punta))
            contorno?.let { drawPath(p, it, style = Stroke(0.9f * u)) }
            // Las venas oscuras y la lengua.
            for (v in -1..1) {
                val q = Offset(desde.x + (punta.x - desde.x) * 0.7f + v * 2.2f * u * sin(a), desde.y + (punta.y - desde.y) * 0.7f - v * 2.2f * u * cos(a))
                drawLine(lerp(flor, Color.Black, 0.5f).copy(alpha = 0.45f), desde, q, 0.6f * u)
            }
            drawLine(IRIS_LENGUA, Offset(desde.x + (punta.x - desde.x) * 0.08f, desde.y + (punta.y - desde.y) * 0.08f),
                Offset(desde.x + (punta.x - desde.x) * 0.36f, desde.y + (punta.y - desde.y) * 0.36f), 2.6f * u, StrokeCap.Round)
        }
        drawCircle(lerp(flor, Color.Black, 0.45f), 2.6f * u, fc)
    }

    /** Un pétalo ancho y redondeado, de [base] a [punta], de ancho [ancho]. */
    fun petalo(base: Offset, punta: Offset, ancho: Float): Path {
        val dx = punta.x - base.x; val dy = punta.y - base.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val nx = -dy / l * ancho / 2; val ny = dx / l * ancho / 2
        return Path().apply {
            moveTo(base.x, base.y)
            cubicTo(base.x + dx * 0.3f + nx * 1.3f, base.y + dy * 0.3f + ny * 1.3f, punta.x + nx * 0.9f - dx * 0.05f, punta.y + ny * 0.9f - dy * 0.05f, punta.x, punta.y)
            cubicTo(punta.x - nx * 0.9f - dx * 0.05f, punta.y - ny * 0.9f - dy * 0.05f, base.x + dx * 0.3f - nx * 1.3f, base.y + dy * 0.3f - ny * 1.3f, base.x, base.y)
            close()
        }
    }

    /**
     * La libélula vista desde arriba: la cabeza con sus ojos grandes, el tórax, el abdomen largo y anillado, y las cuatro
     * alas de nácar con su nervadura de oro. [c] es el tórax, [largo] la longitud del cuerpo, [angulo] hacia dónde mira
     * (0 = hacia arriba, en grados) y [aleteo] (de 0 a 1) cuánto se cierran las alas.
     */
    fun DrawScope.libelula(c: Offset, largo: Float, angulo: Float = 0f, aleteo: Float = 0f, cuerpo: Color = LIBELULA,
                           ala: Color = NACAR, vena: Color = ORO, contorno: Color? = null) {
        val u = largo / 100f
        rotate(angulo, c) {
            val abre = 1f - 0.45f * aleteo.coerceIn(0f, 1f)
            // Las cuatro alas: las de delante un poco más largas, todas echadas hacia atrás.
            listOf(Triple(-1f, -5f, 54f), Triple(1f, -5f, 54f), Triple(-1f, 3f, 50f), Triple(1f, 3f, 50f)).forEach { (s, dy, l) ->
                val base = Offset(c.x + s * 3f * u, c.y + dy * u)
                val ang = if (dy < 0) -0.1f else 0.22f
                val punta = Offset(base.x + s * l * u * cos(ang) * abre, base.y + l * u * sin(ang))
                val forma = petalo(base, punta, 19f * u * (0.55f + 0.45f * abre))
                drawPath(forma, Brush.linearGradient(listOf(ala.copy(alpha = 0.92f), lerp(ala, VIDRIO_VERDE, 0.35f).copy(alpha = 0.7f)), base, punta))
                drawPath(forma, vena.copy(alpha = 0.85f), style = Stroke(1.1f * u))
                // La vena del medio y el punto de color cerca de la punta (el pterostigma).
                drawLine(vena.copy(alpha = 0.7f), base, Offset(base.x + (punta.x - base.x) * 0.9f, base.y + (punta.y - base.y) * 0.9f), 0.7f * u)
                drawCircle(lerp(cuerpo, Color.Black, 0.2f), 1.6f * u, Offset(base.x + (punta.x - base.x) * 0.82f, base.y + (punta.y - base.y) * 0.82f))
                contorno?.let { drawPath(forma, it, style = Stroke(0.8f * u)) }
            }
            // El abdomen: largo, afinándose, con sus anillos.
            val cola = List(20) { i -> Offset(c.x, c.y + 8f * u + i * 3.3f * u) }
            val abdomen = cinta(cola) { t -> 7f * u * (1f - 0.55f * t) }
            drawPath(abdomen, cuerpo)
            drawPath(cinta(cola.map { Offset(it.x - 1.2f * u, it.y) }) { t -> 2.4f * u * (1f - 0.6f * t) }, lerp(cuerpo, Color.White, 0.35f))
            for (i in 1 until 9) {
                val y = c.y + 8f * u + i * 7f * u
                drawLine(lerp(cuerpo, Color.Black, 0.45f), Offset(c.x - 3.2f * u * (1f - i / 14f), y), Offset(c.x + 3.2f * u * (1f - i / 14f), y), 0.9f * u)
            }
            contorno?.let { drawPath(abdomen, it, style = Stroke(0.8f * u)) }
            // El tórax y la cabeza con sus dos ojos.
            drawOval(cuerpo, Offset(c.x - 5.5f * u, c.y - 9f * u), Size(11f * u, 18f * u))
            drawOval(lerp(cuerpo, Color.White, 0.3f), Offset(c.x - 3.5f * u, c.y - 7f * u), Size(3.5f * u, 9f * u))
            drawCircle(lerp(cuerpo, Color.Black, 0.25f), 4.2f * u, Offset(c.x - 3.8f * u, c.y - 12.5f * u))
            drawCircle(lerp(cuerpo, Color.Black, 0.25f), 4.2f * u, Offset(c.x + 3.8f * u, c.y - 12.5f * u))
            drawCircle(Color.White.copy(alpha = 0.6f), 1.2f * u, Offset(c.x - 5f * u, c.y - 14f * u))
            drawCircle(Color.White.copy(alpha = 0.6f), 1.2f * u, Offset(c.x + 2.6f * u, c.y - 14f * u))
        }
    }

    /**
     * El emblema del tema: la libélula dentro de un marco de dos líneas de látigo que nacen abajo, suben por los lados
     * abrazándola y se enroscan arriba, una hacia cada lado. [c] es el centro y [r] el radio del marco.
     */
    fun DrawScope.emblema(c: Offset, r: Float, marco: Color = ORO, cuerpo: Color = LIBELULA, ala: Color = NACAR) {
        val g = r * 0.1f
        val abajo = Offset(c.x, c.y + r)
        latigo(abajo, Offset(c.x - r * 0.7f, c.y - r * 0.5f), r * 0.27f, g, marco, comba = 0.24f, horario = true)
        latigo(abajo, Offset(c.x + r * 0.7f, c.y - r * 0.5f), r * 0.27f, g, marco, comba = -0.24f, horario = false)
        // El nudo de abajo, donde nacen las dos.
        drawCircle(marco, g * 0.95f, abajo)
        drawCircle(lerp(marco, Color.White, 0.4f), g * 0.4f, Offset(abajo.x - g * 0.3f, abajo.y - g * 0.3f))
        libelula(Offset(c.x, c.y - r * 0.12f), r * 0.92f, 0f, 0f, cuerpo, ala, marco)
    }
}
