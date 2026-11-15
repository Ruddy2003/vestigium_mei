package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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
 * figuras que comparten las superficies: la línea de látigo, el aro de látigo del ícono, el tallo que crece, el iris, la
 * flor abierta, la libélula, el emblema (la libélula en su aro de látigo), la lámpara de vidrio de Nancy y el candelabro
 * de las entradas de metro. Todo con la curva tranquila del cartel de 1900: contorno de pluma, colores planos con una luz
 * suave que viene de arriba a la izquierda.
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
    /** El esmalte del medallón (como el del sello): violeta arriba, verde abajo, y su sombra honda. */
    val ESMALTE_VIOLETA = Color(0xFF4B3672)
    val ESMALTE_VERDE = Color(0xFF2B5446)
    val ESMALTE_HONDO = Color(0xFF1D1830)

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

    /** Una línea abierta por [pts], para trazarla con `Stroke`. */
    fun linea(pts: List<Offset>): Path = Path().apply {
        if (pts.isEmpty()) return@apply
        moveTo(pts[0].x, pts[0].y); for (k in 1 until pts.size) lineTo(pts[k].x, pts[k].y)
    }

    /**
     * Los puntos de un rizo que sigue a una curva que llega a [desde] con la dirección [tangente]: gira hacia el lado
     * [horario] (en la pantalla) con radio [radio] al principio y se va cerrando, [vueltas] vueltas, hasta un cuarto del radio.
     */
    fun puntosRizo(desde: Offset, tangente: Offset, radio: Float, horario: Boolean = true, vueltas: Float = 1.25f, n: Int = 36): List<Offset> {
        val tl = sqrt(tangente.x * tangente.x + tangente.y * tangente.y).coerceAtLeast(0.0001f)
        val ux = tangente.x / tl; val uy = tangente.y / tl
        val s = if (horario) 1f else -1f
        val cx = desde.x - uy * radio * s; val cy = desde.y + ux * radio * s
        val a0 = atan2(desde.y - cy, desde.x - cx)
        return List(n) { i ->
            val t = (i + 1) / n.toFloat()
            val r = radio * (1f - 0.75f * t)
            val a = a0 + s * vueltas * 2 * PI.toFloat() * t
            Offset(cx + r * cos(a), cy + r * sin(a))
        }
    }

    private fun Path.copiaMovida(d: Offset): Path = Path().also { it.addPath(this, d) }

    // ------------------------------------------------------------------------------------------------ la línea de látigo

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
        return curva + puntosRizo(hasta, Offset(hasta.x - c.x, hasta.y - c.y), rizo, horario, 1.25f, 36)
    }

    /**
     * La línea de látigo, dibujada como un trazo de pluma que nace fino, engorda en el medio de la S y se afina en el rizo.
     * Debajo, una sombra corta hacia abajo a la derecha (la luz viene de arriba a la izquierda).
     */
    fun DrawScope.latigo(desde: Offset, hasta: Offset, rizo: Float, grueso: Float, color: Color = HIERRO,
                         comba: Float = 0.28f, horario: Boolean = true, sombra: Boolean = true) {
        val pts = puntosLatigo(desde, hasta, rizo, comba, horario)
        val cuerpo = cinta(pts) { t ->
            // De la punta de salida (fina) al medio de la S (gruesa) y al rizo, que se va afinando.
            val f = if (t < 0.5f) 0.18f + 0.82f * sin(t / 0.5f * PI.toFloat() * 0.5f) else 1f - 0.82f * ((t - 0.5f) / 0.5f)
            grueso * f.coerceIn(0.14f, 1f)
        }
        if (sombra) drawPath(cuerpo.copiaMovida(Offset(grueso * 0.35f, grueso * 0.45f)), Color.Black.copy(alpha = 0.14f))
        drawPath(cuerpo, color)
        // El brillo del lado de la luz, un hilo fino sobre la primera mitad de la S.
        if (grueso >= 2.5f) {
            val brillo = pts.subList(6, 34).map { Offset(it.x - grueso * 0.16f, it.y - grueso * 0.2f) }
            drawPath(cinta(brillo) { t -> grueso * 0.22f * sin(t * PI.toFloat()) }, lerp(color, Color.White, 0.45f).copy(alpha = 0.55f))
        }
    }

    /**
     * Los puntos del aro de látigo del ícono y del sello: nace abajo a la izquierda, sube por la izquierda, da la vuelta
     * por arriba, baja por la derecha y, abajo a la derecha, se enrosca hacia dentro. [c] y [r]: el centro y el radio.
     */
    fun puntosAnillo(c: Offset, r: Float): List<Offset> {
        val desde = 128f; val hasta = 398f
        val aro = List(64) { i ->
            val g = Math.toRadians((desde + (hasta - desde) * i / 63f).toDouble()).toFloat()
            // Un poco más ancho que alto, y con la parte de abajo apenas caída, como un trazo a mano.
            val rr = r * (1f + 0.025f * sin(g * 2f + 0.6f))
            Offset(c.x + rr * cos(g), c.y + rr * sin(g) * 0.985f)
        }
        val fin = aro.last(); val antes = aro[aro.size - 2]
        return aro + puntosRizo(fin, Offset(fin.x - antes.x, fin.y - antes.y), r * 0.17f, true, 1.3f, 34)
    }

    /**
     * El aro de látigo, de oro: grueso arriba a la izquierda y cada vez más fino hasta el rizo. Nace de una hoja en punta
     * (la yema de la que sale la línea), con su sombra, su brillo del lado de la luz y un filete oscuro de cloisonné.
     */
    fun DrawScope.anilloLatigo(c: Offset, r: Float, grueso: Float = r * 0.075f, color: Color = ORO, brillo: Color = ORO_BRILLO,
                               sombra: Boolean = true, filete: Boolean = true) {
        val pts = puntosAnillo(c, r)
        val nAro = 64
        val ancho = { t: Float ->
            val s = t * (pts.size - 1) / (nAro - 1).toFloat()            // 0 a 1 en el aro; más de 1 en el rizo
            val f = if (s <= 1f) (0.55f + 0.45f * sin(min(s / 0.18f, 1f) * PI.toFloat() / 2)) * (1f - 0.45f * ((s - 0.3f) / 0.7f).coerceIn(0f, 1f))
            else 0.55f * (1f - 0.65f * ((s - 1f) / ((pts.size - 1) / (nAro - 1f) - 1f)).coerceIn(0f, 1f))
            grueso * f
        }
        val cuerpo = cinta(pts, ancho)
        // La yema de la que nace: una hoja en punta que sale hacia atrás desde el principio del aro.
        val p0 = pts[0]; val p1 = pts[2]
        val tl = sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y)).coerceAtLeast(0.001f)
        val atras = Offset((p0.x - p1.x) / tl, (p0.y - p1.y) / tl)
        val punta = Offset(p0.x + atras.x * r * 0.34f, p0.y + atras.y * r * 0.34f)
        val yema = hojaLanza(Offset(p0.x - atras.x * grueso * 0.4f, p0.y - atras.y * grueso * 0.4f), punta, grueso * 1.25f, -0.1f)
        val oscuro = lerp(color, Color.Black, 0.42f)
        if (sombra) {
            val d = Offset(grueso * 0.3f, grueso * 0.42f)
            drawPath(cuerpo.copiaMovida(d), Color.Black.copy(alpha = 0.18f))
            drawPath(yema.copiaMovida(d), Color.Black.copy(alpha = 0.18f))
        }
        val oro = Brush.linearGradient(listOf(brillo, color, lerp(color, Color.Black, 0.22f)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r))
        drawPath(cuerpo, oro)
        drawPath(yema, oro)
        if (filete && grueso > 1.6f) {
            drawPath(cuerpo, oscuro.copy(alpha = 0.55f), style = Stroke(max(grueso * 0.08f, 0.5f)))
            drawPath(yema, oscuro.copy(alpha = 0.55f), style = Stroke(max(grueso * 0.08f, 0.5f)))
        }
        // El nervio de la yema y el brillo del aro, del lado de la luz (arriba a la izquierda).
        drawLine(oscuro.copy(alpha = 0.45f), p0, Offset(p0.x + (punta.x - p0.x) * 0.8f, p0.y + (punta.y - p0.y) * 0.8f), max(grueso * 0.1f, 0.5f), StrokeCap.Round)
        if (grueso > 2f) {
            val luz = pts.subList(2, 40).map { q ->
                val vx = q.x - c.x; val vy = q.y - c.y; val l = sqrt(vx * vx + vy * vy).coerceAtLeast(0.001f)
                Offset(q.x + vx / l * grueso * 0.18f, q.y + vy / l * grueso * 0.18f)
            }
            drawPath(cinta(luz) { t -> grueso * 0.2f * sin(t * PI.toFloat()) }, Color.White.copy(alpha = 0.55f))
        }
    }

    // ------------------------------------------------------------------------------------------------ hojas, tallos y flores

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

    /** Una hoja de sauce con su nervio claro: la mitad del lado de la luz en [luz] y la otra en [sombra]. */
    fun DrawScope.hojaConNervio(base: Offset, punta: Offset, ancho: Float, comba: Float, luz: Color, sombra: Color) {
        drawPath(hojaLanza(base, punta, ancho, comba), sombra)
        drawPath(hojaLanza(base, punta, ancho * 0.55f, comba * 1.25f), luz)
        val m = Offset(base.x + (punta.x - base.x) * 0.82f, base.y + (punta.y - base.y) * 0.82f)
        drawLine(lerp(luz, Color.White, 0.35f).copy(alpha = luz.alpha * 0.7f), base, m, max(ancho * 0.08f, 0.4f), StrokeCap.Round)
    }

    /**
     * El tallo que crece: sube desde [base] [alto] con una curva suave hacia [lado] (−1 o 1), echa hojas alternas de sauce
     * y, a media altura, un zarcillo que se enrosca. [crecido] (de 0 a 1) dice cuánto ha crecido: las hojas salen conforme
     * el tallo pasa por ellas y, al final, el capullo de malva se abre en flor. Sirve para los bordes y para el fondo vivo.
     */
    fun DrawScope.tallo(base: Offset, alto: Float, crecido: Float = 1f, lado: Float = 1f, grueso: Float = alto * 0.018f,
                        color: Color = TALLO, sombra: Color = TALLO_SOMBRA, flor: Color = MALVA) {
        val k = crecido.coerceIn(0f, 1f)
        if (k <= 0.01f) return
        val todo = bezier(base, Offset(base.x + lado * alto * 0.16f, base.y - alto * 0.32f),
            Offset(base.x - lado * alto * 0.12f, base.y - alto * 0.7f), Offset(base.x + lado * alto * 0.1f, base.y - alto), 48)
        val n = max(2, (todo.size * k).toInt())
        val pts = todo.subList(0, min(n, todo.size))
        fun dir(i: Int): Offset {
            val a = todo[max(i - 1, 0)]; val b = todo[min(i + 1, todo.size - 1)]
            val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
            return Offset(dx / l, dy / l)
        }
        // El zarcillo, a media altura, del lado contrario a la primera hoja: sale recto y se enrosca.
        if (k > 0.5f) {
            val i = (0.46f * (todo.size - 1)).toInt(); val p = todo[i]; val d = dir(i)
            val s = -lado
            val sale = Offset(d.x * 0.55f - d.y * s * 0.85f, d.y * 0.55f + d.x * s * 0.85f)
            val largo = alto * 0.09f * ((k - 0.5f) / 0.2f).coerceIn(0f, 1f)
            if (largo > 0.5f) {
                val q = Offset(p.x + sale.x * largo, p.y + sale.y * largo)
                val rizo = puntosRizo(q, sale, largo * 0.42f, s * lado < 0f, 1.2f, 26)
                drawPath(linea(listOf(p, q) + rizo), sombra, style = Stroke(grueso * 0.32f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
        // Las hojas, alternas, de abajo arriba; cada una crece cuando el tallo ya pasó por ella.
        for (j in 0 until 5) {
            val f = 0.13f + j * 0.16f
            if (k < f) break
            val i = (f * (todo.size - 1)).toInt(); val p = todo[i]; val d = dir(i)
            val l = alto * (0.21f - j * 0.024f) * ((k - f) / 0.14f).coerceIn(0f, 1f)
            if (l < 0.5f) continue
            val s = if (j % 2 == 0) lado else -lado
            // La hoja sale hacia un lado y hacia arriba, arqueándose hacia fuera.
            val v = Offset(d.x * 0.62f - d.y * s * 0.78f, d.y * 0.62f + d.x * s * 0.78f)
            val punta = Offset(p.x + v.x * l, p.y + v.y * l)
            hojaConNervio(p, punta, l * 0.28f, -0.16f * s * (if (lado < 0) -1f else 1f), if (s * lado > 0) color else lerp(color, sombra, 0.45f), sombra)
        }
        drawPath(cinta(pts) { t -> grueso * (1f - 0.5f * t * k) }, sombra)
        drawPath(cinta(pts.map { Offset(it.x - grueso * 0.15f, it.y - grueso * 0.05f) }) { t -> grueso * 0.48f * (1f - 0.5f * t * k) }, color)
        // Arriba: el capullo que engorda y, al final, se abre en flor.
        val cima = pts.last()
        val d = dir(pts.size - 1)
        if (k > 0.62f) {
            val a = ((k - 0.62f) / 0.24f).coerceIn(0f, 1f)
            val abre = ((k - 0.86f) / 0.14f).coerceIn(0f, 1f)
            val rr = alto * 0.06f
            if (abre <= 0f) {
                val punta = Offset(cima.x + d.x * rr * 2.2f * a, cima.y + d.y * rr * 2.2f * a)
                drawPath(petalo(cima, punta, rr * 1.15f * a), lerp(flor, sombra, 0.25f))
                drawPath(hojaLanza(cima, Offset(cima.x + (punta.x - cima.x) * 0.7f - d.y * rr * 0.5f, cima.y + (punta.y - cima.y) * 0.7f + d.x * rr * 0.5f), rr * 0.5f * a, 0.1f), sombra)
            } else florAbierta(Offset(cima.x + d.x * rr * 0.9f, cima.y + d.y * rr * 0.9f), rr * (1.1f + 0.5f * abre), flor, abre, atan2(d.y, d.x))
        }
    }

    /**
     * Una flor abierta vista de lado, como las de los carteles: cinco pétalos redondos en abanico (los de atrás más oscuros),
     * el cáliz verde debajo y el corazón amarillo. [c] es el corazón, [r] el largo de un pétalo, [abre] (0 a 1) cuánto se
     * abre y [mira] hacia dónde, en radianes (−π/2 = hacia arriba).
     */
    fun DrawScope.florAbierta(c: Offset, r: Float, color: Color = MALVA, abre: Float = 1f, mira: Float = -PI.toFloat() / 2, caliz: Color = TALLO_SOMBRA) {
        val k = abre.coerceIn(0f, 1f)
        val oscuro = lerp(color, Color.Black, 0.22f); val claro = lerp(color, Color.White, 0.22f)
        // El cáliz: dos sépalos que abrazan la flor por debajo.
        for (s in listOf(-1f, 1f)) {
            val a = mira + PI.toFloat() + s * 0.55f
            drawPath(hojaLanza(c, Offset(c.x + cos(a) * r * 0.55f, c.y + sin(a) * r * 0.55f), r * 0.28f, 0.1f * s), caliz)
        }
        // Los pétalos, de atrás adelante.
        val orden = listOf(-1.2f to oscuro, 1.2f to oscuro, -0.6f to color, 0.6f to color, 0f to claro)
        for ((ang, col) in orden) {
            val a = mira + ang * (0.45f + 0.55f * k)
            val l = r * (if (ang == 0f) 1f else 0.92f) * (0.6f + 0.4f * k)
            drawPath(petalo(c, Offset(c.x + cos(a) * l, c.y + sin(a) * l), r * (0.5f + 0.25f * k)), col)
        }
        // Las venas del pétalo de delante y el corazón.
        val df = Offset(c.x + cos(mira) * r * 0.7f, c.y + sin(mira) * r * 0.7f)
        drawLine(oscuro.copy(alpha = 0.5f), c, df, max(r * 0.05f, 0.4f), StrokeCap.Round)
        drawCircle(IRIS_LENGUA, r * 0.17f, Offset(c.x + cos(mira) * r * 0.08f, c.y + sin(mira) * r * 0.08f))
    }

    /**
     * El iris: el tallo con sus hojas de espada y la flor arriba, con sus tres pétalos que suben (los estandartes) y sus
     * tres que caen (los «sépalos», con la lengua amarilla). [base] es el pie del tallo y [alto], hasta lo alto de la flor.
     */
    fun DrawScope.iris(base: Offset, alto: Float, flor: Color = IRIS, hoja: Color = TALLO, hojaSombra: Color = TALLO_SOMBRA,
                       contorno: Color? = null) {
        val u = alto / 100f
        // Las hojas de espada, de atrás adelante, con su nervio.
        val hojas = listOf(Triple(-22f, 74f, -0.08f), Triple(16f, 86f, 0.06f), Triple(-8f, 56f, 0.14f), Triple(26f, 48f, -0.12f))
        hojas.forEachIndexed { i, (dx, h, c) ->
            val b = Offset(base.x + dx * u * 0.25f, base.y); val t = Offset(base.x + dx * u, base.y - h * u)
            val p = hojaLanza(b, t, 8f * u, c, espada = true)
            drawPath(p, if (i % 2 == 0) hojaSombra else hoja)
            drawPath(hojaLanza(b, Offset(b.x + (t.x - b.x) * 0.9f, b.y + (t.y - b.y) * 0.9f), 1.1f * u, c, espada = true), lerp(hoja, Color.White, 0.3f).copy(alpha = 0.5f))
            contorno?.let { drawPath(p, it, style = Stroke(0.9f * u)) }
        }
        // El tallo.
        val cima = Offset(base.x + 2f * u, base.y - 72f * u)
        drawLine(hojaSombra, base, cima, 3.2f * u, StrokeCap.Round)
        drawLine(hoja, Offset(base.x - 0.6f * u, base.y), Offset(cima.x - 0.6f * u, cima.y), 1.6f * u, StrokeCap.Round)
        val fc = Offset(cima.x, cima.y - 6f * u)
        val oscuro = lerp(flor, Color.Black, 0.3f)
        val claro = lerp(flor, Color.White, 0.28f)
        // Los tres estandartes: el del medio sube recto, los otros dos se abren; cada uno con el borde ondulado arriba.
        listOf(-0.42f to 0.9f, 0.42f to 0.9f, 0f to 1f).forEach { (ang, k) ->
            val a = -PI.toFloat() / 2 + ang
            val punta = Offset(fc.x + cos(a) * 24f * u * k, fc.y + sin(a) * 24f * u * k)
            val p = petalo(fc, punta, 15f * u * k)
            drawPath(p, if (ang == 0f) claro else flor)
            drawLine(oscuro.copy(alpha = 0.35f), fc, Offset(fc.x + (punta.x - fc.x) * 0.8f, fc.y + (punta.y - fc.y) * 0.8f), 0.6f * u)
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

    /** Un ala de libélula de [base] a [punta]: larga y estrecha, más ancha cerca de la punta, redonda al final. */
    private fun alaLibelula(base: Offset, punta: Offset, ancho: Float): Path {
        val dx = punta.x - base.x; val dy = punta.y - base.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val nx = -dy / l * ancho / 2; val ny = dx / l * ancho / 2
        return Path().apply {
            moveTo(base.x, base.y)
            cubicTo(base.x + dx * 0.25f + nx * 0.9f, base.y + dy * 0.25f + ny * 0.9f, base.x + dx * 0.78f + nx * 1.25f, base.y + dy * 0.78f + ny * 1.25f, punta.x, punta.y)
            cubicTo(base.x + dx * 0.9f - nx * 0.95f, base.y + dy * 0.9f - ny * 0.95f, base.x + dx * 0.3f - nx * 0.75f, base.y + dy * 0.3f - ny * 0.75f, base.x, base.y)
            close()
        }
    }

    /**
     * La libélula vista desde arriba, como una joya: la cabeza con sus ojos grandes, el tórax, el abdomen largo y anillado,
     * y las cuatro alas de nácar en aspa (las de delante hacia adelante, las de atrás hacia atrás) con su nervadura de oro.
     * [c] es el tórax, [largo] la longitud del cuerpo, [angulo] hacia dónde mira (0 = hacia arriba, en grados) y [aleteo]
     * (de 0 a 1) cuánto se cierran las alas.
     */
    fun DrawScope.libelula(c: Offset, largo: Float, angulo: Float = 0f, aleteo: Float = 0f, cuerpo: Color = LIBELULA,
                           ala: Color = NACAR, vena: Color = ORO, contorno: Color? = null) {
        val u = largo / 100f
        rotate(angulo, c) {
            val abre = 1f - 0.45f * aleteo.coerceIn(0f, 1f)
            val detalle = largo > 36f
            // Las cuatro alas, en aspa: (lado, de dónde salen, ángulo desde la horizontal, largo).
            listOf(Triple(-1f, -4f, -22f), Triple(1f, -4f, -22f), Triple(-1f, 4f, 24f), Triple(1f, 4f, 24f)).forEach { (s, dy, g) ->
                val l = if (dy < 0) 56f else 52f
                val base = Offset(c.x + s * 2.5f * u, c.y + dy * u)
                val ang = Math.toRadians((g * abre + (1f - abre) * (if (dy < 0) 55f else 70f)).toDouble()).toFloat()
                val punta = Offset(base.x + s * l * u * cos(ang), base.y + l * u * sin(ang))
                val forma = alaLibelula(base, punta, (if (dy < 0) 13f else 15f) * u)
                drawPath(forma, Brush.linearGradient(listOf(ala.copy(alpha = 0.95f), lerp(ala, VIDRIO_VIOLETA, 0.22f).copy(alpha = 0.85f),
                    lerp(ala, VIDRIO_VERDE, 0.3f).copy(alpha = 0.8f)), base, punta))
                if (detalle) {
                    // La nervadura: la vena de delante, la del medio y unas venas cruzadas.
                    val dx = punta.x - base.x; val dyy = punta.y - base.y; val ll = sqrt(dx * dx + dyy * dyy)
                    val nx = -dyy / ll; val ny = dx / ll
                    val borde = if (dy < 0) -s else s
                    drawLine(vena.copy(alpha = 0.75f), base, Offset(punta.x - dx * 0.04f, punta.y - dyy * 0.04f), 0.6f * u)
                    for (k in 1..5) {
                        val f = k / 6.5f
                        val q = Offset(base.x + dx * f, base.y + dyy * f)
                        val w = (if (dy < 0) 6f else 7f) * u * (0.55f + 0.5f * f)
                        drawLine(vena.copy(alpha = 0.45f), Offset(q.x - nx * w * borde, q.y - ny * w * borde), Offset(q.x + nx * w * borde * 0.8f, q.y + ny * w * borde * 0.8f), 0.4f * u)
                    }
                    // El pterostigma, la mancha de color cerca de la punta del borde de delante.
                    drawCircle(lerp(cuerpo, Color.Black, 0.15f), 1.5f * u, Offset(base.x + dx * 0.86f + nx * 2.6f * u * borde, base.y + dyy * 0.86f + ny * 2.6f * u * borde))
                }
                drawPath(forma, vena.copy(alpha = 0.9f), style = Stroke(if (detalle) 0.9f * u else 1.2f * u))
                contorno?.let { drawPath(forma, it, style = Stroke(0.8f * u)) }
            }
            // El abdomen: largo, afinándose, con sus anillos de oro.
            val cola = List(20) { i -> Offset(c.x, c.y + 8f * u + i * 3.3f * u) }
            val abdomen = cinta(cola) { t -> 6.6f * u * (1f - 0.55f * t) }
            drawPath(abdomen, cuerpo)
            drawPath(cinta(cola.map { Offset(it.x - 1.1f * u, it.y) }) { t -> 2.2f * u * (1f - 0.6f * t) }, lerp(cuerpo, Color.White, 0.35f))
            for (i in 1 until 9) {
                val y = c.y + 8f * u + i * 7f * u
                drawLine(if (detalle) vena.copy(alpha = 0.8f) else lerp(cuerpo, Color.Black, 0.45f), Offset(c.x - 3.1f * u * (1f - i / 14f), y),
                    Offset(c.x + 3.1f * u * (1f - i / 14f), y), 0.9f * u)
            }
            contorno?.let { drawPath(abdomen, it, style = Stroke(0.8f * u)) }
            // El tórax y la cabeza con sus dos ojos.
            drawOval(cuerpo, Offset(c.x - 5.5f * u, c.y - 9f * u), Size(11f * u, 18f * u))
            drawOval(lerp(cuerpo, Color.White, 0.3f), Offset(c.x - 3.5f * u, c.y - 7f * u), Size(3.5f * u, 9f * u))
            contorno?.let { drawOval(it, Offset(c.x - 5.5f * u, c.y - 9f * u), Size(11f * u, 18f * u), style = Stroke(0.8f * u)) }
            drawCircle(lerp(cuerpo, Color.Black, 0.25f), 4.2f * u, Offset(c.x - 3.8f * u, c.y - 12.5f * u))
            drawCircle(lerp(cuerpo, Color.Black, 0.25f), 4.2f * u, Offset(c.x + 3.8f * u, c.y - 12.5f * u))
            drawCircle(Color.White.copy(alpha = 0.6f), 1.2f * u, Offset(c.x - 5f * u, c.y - 14f * u))
            drawCircle(Color.White.copy(alpha = 0.6f), 1.2f * u, Offset(c.x + 2.6f * u, c.y - 14f * u))
        }
    }

    // ------------------------------------------------------------------------------------------------ el emblema

    /**
     * El emblema del tema, como el ícono: la libélula dentro del aro de látigo de oro, que nace de una yema abajo a la
     * izquierda y se enrosca abajo a la derecha. [c] es el centro y [r] el radio del aro.
     */
    fun DrawScope.emblema(c: Offset, r: Float, marco: Color = ORO, cuerpo: Color = LIBELULA, ala: Color = NACAR) {
        anilloLatigo(c, r * 0.9f, r * 0.085f, marco, lerp(marco, Color.White, 0.55f))
        libelula(Offset(c.x + r * 0.04f, c.y - r * 0.1f), r * 0.98f, -14f, 0f, cuerpo, ala, marco)
    }

    /**
     * El medallón del emblema animado: el disco de esmalte (violeta arriba, verde abajo, con su guilloché y su brillo), el
     * iris que sube desde la yema, la libélula en medio y el aro de látigo encima. [t] (0 a 1) es la vuelta de la
     * animación: un destello recorre el aro, el nácar de las alas tornasola y la libélula aletea apenas. Pequeño (menos de
     * 48 dp), solo el aro sobre su esmalte, como el ícono del lanzador.
     */
    fun DrawScope.medallon(c: Offset, r: Float, t: Float, oscuro: Boolean) {
        val pequeno = r * 2 < 48f * density
        val oro = if (oscuro) ORO else lerp(ORO, Color(0xFF8A6A22), 0.28f)
        val oscuroOro = lerp(oro, Color.Black, 0.4f)
        val destello = sin(PI.toFloat() * t).coerceAtLeast(0f)
        if (pequeno) {
            // Pequeño: el aro de látigo solo, grueso, con un cabujón de esmalte en el centro.
            val ra = r * 0.84f
            val cc = Offset(c.x + r * 0.02f, c.y + r * 0.02f)
            drawCircle(Color.Black.copy(alpha = 0.15f), ra * 0.36f, Offset(cc.x + r * 0.04f, cc.y + r * 0.05f))
            drawCircle(Brush.linearGradient(listOf(lerp(ESMALTE_VIOLETA, Color.White, 0.15f), ESMALTE_VERDE), Offset(cc.x - ra * 0.3f, cc.y - ra * 0.3f), Offset(cc.x + ra * 0.3f, cc.y + ra * 0.3f)), ra * 0.34f, cc)
            drawCircle(oscuroOro, ra * 0.34f, cc, style = Stroke(max(ra * 0.05f, 0.6f)))
            drawCircle(Color.White.copy(alpha = 0.45f), ra * 0.08f, Offset(cc.x - ra * 0.12f, cc.y - ra * 0.12f))
            anilloLatigo(c, ra, ra * 0.16f, oro, ORO_BRILLO, filete = false)
            return
        }
        val rd = r * 0.94f
        val ra = r * 0.78f
        // El esmalte: degradado de violeta a verde, la sombra honda del borde y el guilloché de círculos finos.
        drawCircle(Color.Black.copy(alpha = 0.16f), rd, Offset(c.x + r * 0.03f, c.y + r * 0.045f))
        drawCircle(Brush.linearGradient(listOf(lerp(ESMALTE_VIOLETA, Color.White, if (oscuro) 0.04f else 0.1f), ESMALTE_VIOLETA, lerp(ESMALTE_VIOLETA, ESMALTE_VERDE, 0.55f), ESMALTE_VERDE),
            Offset(c.x - rd * 0.4f, c.y - rd), Offset(c.x + rd * 0.3f, c.y + rd)), rd, c)
        drawCircle(Brush.radialGradient(listOf(Color.Transparent, Color.Transparent, ESMALTE_HONDO.copy(alpha = 0.6f)), c, rd), rd, c)
        for (k in 1..10) drawCircle(Color.White.copy(alpha = 0.035f), rd * (0.1f + 0.088f * k), Offset(c.x + rd * 0.06f, c.y + rd * 0.04f), style = Stroke(max(rd * 0.006f, 0.5f)))
        // El reflejo del vidrio, una media luna clara arriba a la izquierda.
        drawArc(Color.White.copy(alpha = 0.09f), 195f, 80f, false, Offset(c.x - rd * 0.8f, c.y - rd * 0.8f), Size(rd * 1.6f, rd * 1.6f), style = Stroke(rd * 0.1f, cap = StrokeCap.Round))
        // El bisel de oro del borde: un filete con su luz arriba a la izquierda.
        drawCircle(Brush.linearGradient(listOf(ORO_BRILLO, oro, oscuroOro), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), rd, c, style = Stroke(r * 0.045f))
        drawCircle(oscuroOro.copy(alpha = 0.6f), rd - r * 0.024f, c, style = Stroke(max(r * 0.006f, 0.5f)))
        // El iris que sube desde la yema por dentro del aro, a la izquierda, con su contorno de oro.
        val pie = Offset(c.x - ra * 0.46f, c.y + ra * 0.86f)
        iris(pie, ra * 1.3f, lerp(IRIS, Color.White, 0.14f), lerp(TALLO, Color.White, 0.1f), TALLO_SOMBRA, oro.copy(alpha = 0.85f))
        // La libélula, arriba a la derecha: aletea apenas y su nácar cambia de color con la luz.
        val tornasol = lerp(NACAR, lerp(VIDRIO_VIOLETA, VIDRIO_VERDE, 0.5f + 0.5f * sin(2 * PI.toFloat() * t)), 0.14f)
        val aleteo = 0.1f * (0.5f + 0.5f * sin(4 * PI.toFloat() * t))
        libelula(Offset(c.x + ra * 0.26f, c.y - ra * 0.2f), ra * 0.98f, 38f, aleteo, LIBELULA, tornasol, oro, oscuroOro.copy(alpha = 0.6f))
        anilloLatigo(c, ra, ra * 0.1f, oro, ORO_BRILLO)
        // El destello que corre por el aro, de la yema al rizo, una vez por vuelta.
        val pts = puntosAnillo(c, ra)
        val q = pts[((t * 0.999f) * 63).toInt().coerceIn(0, 63)]
        val rb = ra * 0.16f
        drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.6f * destello), Color.White.copy(alpha = 0f)), q, rb), rb, q)
    }

    // ------------------------------------------------------------------------------------------------ la luz de noche

    /**
     * Una lámpara de vidrio de Nancy, de pantalla de hongo: el pie de vidrio que se ensancha abajo, la pantalla abombada que
     * pasa de [arriba] a [abajo] con un iris grabado, y la virola de bronce. [base] es el pie, [alto] la altura entera y
     * [encendida] (0 a 1) cuánto alumbra: el vidrio se enciende desde dentro y echa su halo.
     */
    fun DrawScope.lampara(base: Offset, alto: Float, encendida: Float = 0f, arriba: Color = VIDRIO_AMBAR, abajo: Color = VIDRIO_VIOLETA) {
        val u = alto / 100f
        val e = encendida.coerceIn(0f, 1f)
        val cs = Offset(base.x, base.y - alto * 0.62f)                       // el centro de la pantalla
        if (e > 0f) drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = 0.5f * e), LUZ.copy(alpha = 0.18f * e), Color.Transparent), cs, alto * 0.95f), alto * 0.95f, cs)
        // El pie: un balaústre de vidrio que se ensancha abajo, en los mismos colores.
        val pie = Path().apply {
            moveTo(base.x - 20f * u, base.y)
            cubicTo(base.x - 18f * u, base.y - 6f * u, base.x - 6f * u, base.y - 10f * u, base.x - 5f * u, base.y - 24f * u)
            cubicTo(base.x - 4f * u, base.y - 38f * u, base.x - 7f * u, base.y - 48f * u, base.x - 4f * u, base.y - 58f * u)
            lineTo(base.x + 4f * u, base.y - 58f * u)
            cubicTo(base.x + 7f * u, base.y - 48f * u, base.x + 4f * u, base.y - 38f * u, base.x + 5f * u, base.y - 24f * u)
            cubicTo(base.x + 6f * u, base.y - 10f * u, base.x + 18f * u, base.y - 6f * u, base.x + 20f * u, base.y)
            close()
        }
        val luzVidrio = { col: Color -> lerp(col, LUZ, 0.55f * e) }
        drawPath(pie, Brush.verticalGradient(listOf(luzVidrio(abajo), luzVidrio(lerp(abajo, arriba, 0.5f)), luzVidrio(lerp(abajo, Color.Black, 0.2f))), base.y - 58f * u, base.y))
        drawLine(Color.White.copy(alpha = 0.3f + 0.2f * e), Offset(base.x - 2.6f * u, base.y - 54f * u), Offset(base.x - 3.4f * u, base.y - 14f * u), 1.2f * u, StrokeCap.Round)
        // La pantalla: un hongo abombado, el borde de abajo apenas ondulado.
        val pantalla = Path().apply {
            moveTo(cs.x - 38f * u, cs.y + 6f * u)
            cubicTo(cs.x - 40f * u, cs.y - 18f * u, cs.x - 22f * u, cs.y - 34f * u, cs.x, cs.y - 35f * u)
            cubicTo(cs.x + 22f * u, cs.y - 34f * u, cs.x + 40f * u, cs.y - 18f * u, cs.x + 38f * u, cs.y + 6f * u)
            quadraticTo(cs.x + 28f * u, cs.y + 10f * u, cs.x + 19f * u, cs.y + 6f * u)
            quadraticTo(cs.x + 9.5f * u, cs.y + 11f * u, cs.x, cs.y + 7f * u)
            quadraticTo(cs.x - 9.5f * u, cs.y + 11f * u, cs.x - 19f * u, cs.y + 6f * u)
            quadraticTo(cs.x - 28f * u, cs.y + 10f * u, cs.x - 38f * u, cs.y + 6f * u)
            close()
        }
        drawPath(pantalla, Brush.verticalGradient(listOf(luzVidrio(lerp(arriba, Color.White, 0.15f)), luzVidrio(arriba), luzVidrio(abajo)), cs.y - 35f * u, cs.y + 10f * u))
        // El iris grabado en el vidrio, en un tono más hondo (de noche, a contraluz).
        val grabado = lerp(abajo, Color.Black, 0.35f).copy(alpha = 0.5f - 0.15f * e)
        drawPath(hojaLanza(Offset(cs.x - 14f * u, cs.y + 6f * u), Offset(cs.x - 22f * u, cs.y - 18f * u), 4f * u, 0.12f, espada = true), grabado)
        drawPath(hojaLanza(Offset(cs.x - 10f * u, cs.y + 6f * u), Offset(cs.x + 2f * u, cs.y - 14f * u), 3.5f * u, -0.1f, espada = true), grabado)
        val fl = Offset(cs.x - 12f * u, cs.y - 20f * u)
        listOf(-0.5f, 0.5f, 0f).forEach { a -> drawPath(petalo(fl, Offset(fl.x + sin(a) * 9f * u, fl.y - cos(a) * 9f * u), 6f * u), grabado) }
        listOf(-1.1f, 1.1f).forEach { a -> drawPath(petalo(fl, Offset(fl.x + sin(a) * 9f * u, fl.y + 4f * u), 6f * u), grabado) }
        // El brillo del vidrio y la virola de bronce que une pantalla y pie.
        drawArc(Color.White.copy(alpha = 0.35f), 200f, 55f, false, Offset(cs.x - 32f * u, cs.y - 30f * u), Size(64f * u, 60f * u), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawRoundRect(lerp(ORO, Color.Black, 0.3f), Offset(base.x - 5f * u, base.y - 61f * u), Size(10f * u, 4f * u), androidx.compose.ui.geometry.CornerRadius(1.5f * u))
        if (e > 0f) drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.55f * e), LUZ.copy(alpha = 0f)), Offset(cs.x, cs.y + 2f * u), 18f * u), 18f * u, Offset(cs.x, cs.y + 2f * u))
    }

    /**
     * Un candelabro de las entradas de metro de Guimard (los faroles son de 1901): el tallo de fundición que sube desde su
     * pie, se ensancha en una vaina y se dobla arriba como un tallo cargado, con el farol de vidrio en forma de pera colgado
     * de la punta. [lado] (−1 o 1) es hacia dónde se dobla; [luz] (0 a 1), cuánto alumbra el farol, rojo anaranjado.
     */
    fun DrawScope.candelabro(base: Offset, alto: Float, lado: Float = 1f, hierro: Color = HIERRO, luz: Float = 0f) {
        val u = alto / 100f
        val pts = bezier(base, Offset(base.x - lado * 3f * u, base.y - 45f * u), Offset(base.x - lado * 4f * u, base.y - 92f * u),
            Offset(base.x + lado * 16f * u, base.y - 96f * u), 40) +
            bezier(Offset(base.x + lado * 16f * u, base.y - 96f * u), Offset(base.x + lado * 24f * u, base.y - 97f * u),
                Offset(base.x + lado * 27f * u, base.y - 92f * u), Offset(base.x + lado * 26f * u, base.y - 86f * u), 12).drop(1)
        val sombra = lerp(hierro, Color.Black, 0.35f)
        // El pie en zócalo, el tallo que se afina y la vaina a media altura.
        drawPath(Path().apply {
            moveTo(base.x - 7f * u, base.y); quadraticTo(base.x - 6f * u, base.y - 6f * u, base.x - 2.6f * u, base.y - 9f * u)
            lineTo(base.x + 2.6f * u, base.y - 9f * u); quadraticTo(base.x + 6f * u, base.y - 6f * u, base.x + 7f * u, base.y); close()
        }, sombra)
        drawPath(cinta(pts) { t -> 4.4f * u * (1f - 0.55f * t) }, sombra)
        drawPath(cinta(pts.map { Offset(it.x - 0.8f * u, it.y) }) { t -> 2.1f * u * (1f - 0.55f * t) }, hierro)
        val v = pts[16]
        drawPath(hojaLanza(Offset(v.x, v.y + 12f * u), Offset(v.x - lado * 1f * u, v.y - 14f * u), 6.5f * u, 0.05f * lado), sombra)
        drawPath(hojaLanza(Offset(v.x, v.y + 10f * u), Offset(v.x - lado * 1f * u, v.y - 10f * u), 2.6f * u, 0.05f * lado), hierro)
        // El brote que remata el doblez, con su rizo.
        val cima = pts[40]
        drawPath(linea(puntosRizo(cima, Offset(lado, -0.4f), 3.2f * u, lado < 0f, 1.1f, 18)), sombra, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // El farol: una pera de vidrio que cuelga de la punta, con su casquillo.
        val punta = pts.last()
        val fc = Offset(punta.x, punta.y + 9f * u)
        val cristal = lerp(lerp(FAROL, TINTA_ROJA, 0.25f), Color(0xFF4A2A1E), 0.55f * (1f - luz))
        if (luz > 0f) drawCircle(Brush.radialGradient(listOf(FAROL.copy(alpha = 0.55f * luz), FAROL.copy(alpha = 0.12f * luz), Color.Transparent), fc, 34f * u), 34f * u, fc)
        drawRoundRect(sombra, Offset(punta.x - 2.6f * u, punta.y - 1f * u), Size(5.2f * u, 3.4f * u), androidx.compose.ui.geometry.CornerRadius(1f * u))
        val pera = Path().apply {
            moveTo(fc.x, fc.y - 7f * u)
            cubicTo(fc.x + 3.5f * u, fc.y - 7f * u, fc.x + 6.5f * u, fc.y + 2f * u, fc.x + 5.5f * u, fc.y + 6f * u)
            quadraticTo(fc.x + 4f * u, fc.y + 10f * u, fc.x, fc.y + 10f * u)
            quadraticTo(fc.x - 4f * u, fc.y + 10f * u, fc.x - 5.5f * u, fc.y + 6f * u)
            cubicTo(fc.x - 6.5f * u, fc.y + 2f * u, fc.x - 3.5f * u, fc.y - 7f * u, fc.x, fc.y - 7f * u)
            close()
        }
        drawPath(pera, Brush.verticalGradient(listOf(lerp(cristal, Color.White, 0.25f + 0.35f * luz), cristal), fc.y - 7f * u, fc.y + 10f * u))
        drawLine(Color.White.copy(alpha = 0.35f + 0.3f * luz), Offset(fc.x - 2.6f * u, fc.y - 2f * u), Offset(fc.x - 3.2f * u, fc.y + 4f * u), 1f * u, StrokeCap.Round)
    }
}
