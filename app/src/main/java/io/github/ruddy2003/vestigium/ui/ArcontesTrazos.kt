package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los trazos que se repiten en las figuras de Arcontes (`docs/diseno/temas/arcontes.md`): sus colores de dibujo y las
 * figuras del tema que comparten todas las superficies: el ouroboros, las siete esferas, la grieta de luz, la lámpara de
 * aceite, la chispa, el amuleto de Abraxas, la gema de jaspe y la sombra de un arconte. Lo demás (la jarra, el códice, las
 * barcas) lo dibuja cada superficie con estos colores.
 */
object TrazosArcontes {
    /** La caliza de Galilea, al sol y en sombra. */
    val CALIZA = Color(0xFFD9CBA6)
    val CALIZA_SOMBRA = Color(0xFF9C8A68)
    /** El lino. */
    val LINO = Color(0xFFEFE6D0)
    /** El barro de las lámparas y las jarras, y el cocido claro. */
    val BARRO = Color(0xFF8A4E2B)
    val BARRO_CLARO = Color(0xFFB9774A)
    /** El oro de más del mundo visible: solo en lo que es del mundo de abajo (el sol, el sello de los arcontes). */
    val ORO_DEMIURGO = Color(0xFFD4A62A)
    /** El lago, y su hondura. */
    val LAGO = Color(0xFF3F7A8A)
    val LAGO_HONDO = Color(0xFF1F4652)
    /** El cielo antiguo de la noche y los círculos finos de las esferas. */
    val CIELO_ANTIGUO = Color(0xFF0B0D1C)
    val ESFERA = Color(0xFF6E6A9E)
    /** La grieta: la única luz que no viene de una llama ni del sol. Blanca, con su halo violeta. */
    val GRIETA = Color(0xFFF4F1FF)
    val GRIETA_HALO = Color(0xFF9C7BFF)
    /** La llama de la lámpara y su halo. */
    val LLAMA = Color(0xFFF0B24A)
    val LLAMA_HALO = Color(0xFFFFD98A)
    /** La sombra de un arconte: se intuye, nunca entera. */
    val SOMBRA = Color(0xFF1A1424)
    /** El jaspe de las gemas, verde y rojo, y la hematita. */
    val JASPE_VERDE = Color(0xFF3E6B4E)
    val JASPE_ROJO = Color(0xFF8E3A2E)
    val HEMATITA = Color(0xFF3A3A40)
    /** El cuero de la tapa del códice y su correa. */
    val CUERO = Color(0xFF6B4A2E)
    val CORREA = Color(0xFF4A321E)
    /** El corte del grabado en la gema: claro y mate, con su pared en sombra; y el fondo del corte (el hueco de los detalles). */
    val GRABADO = Color(0xFFDDD5BC)
    val GRABADO_SOMBRA = Color(0xFF10201A)
    val GRABADO_HUECO = Color(0xFF2C4A39)

    private fun poligono(p: List<Offset>): Path = Path().apply {
        moveTo(p[0].x, p[0].y)
        for (k in 1 until p.size) lineTo(p[k].x, p[k].y)
        close()
    }

    /** Los puntos de una curva de Bézier cúbica (de [a] a [d], con los controles [b] y [c]), en [n] tramos. */
    private fun bezier(a: Offset, b: Offset, c: Offset, d: Offset, n: Int): List<Offset> = List(n + 1) { i ->
        val t = i / n.toFloat(); val s = 1f - t
        Offset(s * s * s * a.x + 3 * s * s * t * b.x + 3 * s * t * t * c.x + t * t * t * d.x,
            s * s * s * a.y + 3 * s * s * t * b.y + 3 * s * t * t * c.y + t * t * t * d.y)
    }

    /** Una cinta de ancho variable (de [w0] a [w1]) a lo largo de [pts]. */
    private fun cinta(pts: List<Offset>, w0: Float, w1: Float): Path {
        val n = pts.size
        val izq = ArrayList<Offset>(n); val der = ArrayList<Offset>(n)
        for (i in 0 until n) {
            val a = pts[max(i - 1, 0)]; val b = pts[minOf(i + 1, n - 1)]
            val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.0001f)
            val w = (w0 + (w1 - w0) * i / (n - 1).coerceAtLeast(1)) / 2
            izq.add(Offset(pts[i].x - dy / l * w, pts[i].y + dx / l * w)); der.add(Offset(pts[i].x + dy / l * w, pts[i].y - dx / l * w))
        }
        return Path().apply {
            moveTo(izq[0].x, izq[0].y); for (k in 1 until n) lineTo(izq[k].x, izq[k].y)
            for (k in n - 1 downTo 0) lineTo(der[k].x, der[k].y)
            close()
        }
    }

    /**
     * El ouroboros: la serpiente que se muerde la cola, en un anillo de radio [r] y cuerpo de grosor [grueso] en el cuello.
     * La cabeza va a [cabeza] grados (medidos como en `drawArc`: 0 a la derecha, en el sentido del reloj), de perfil, con la
     * mandíbula de arriba por fuera y la de abajo por dentro, cerradas sobre la punta de la cola. El cuerpo sigue el anillo en
     * el sentido del reloj y adelgaza hacia la cola. Con [escamas], las escamas en hileras alternas (medias lunas con el
     * borde libre hacia la cola), las comisuras, el ojo y la nariz se recortan en ese color; sin él, solo la silueta.
     * Es el mismo dibujo que el del ícono y el sello (`herramientas/iconos.py`, `ouroboros`).
     */
    fun DrawScope.ouroboros(c: Offset, r: Float, grueso: Float, color: Color, cabeza: Float = -60f, escamas: Color? = null) {
        val cab = Math.toRadians(cabeza.toDouble())
        val wCola = grueso * 0.31f
        val largoCab = grueso * 2f; val anchoCab = grueso * 1.12f
        val cola0 = cab + largoCab * 0.3 / r
        val cuello = cab - largoCab * 0.5 / r + 2 * PI
        val n = 144
        val angs = DoubleArray(n + 1) { cola0 + (cuello - cola0) * it / n }
        // El cuerpo engruesa rápido al salir de la boca y luego despacio hasta el cuello.
        val anchos = FloatArray(n + 1) { wCola + (grueso - wCola) * (it / n.toFloat()).pow(0.55f) }
        val cuerpo = Path()
        for (i in 0..n) {
            val rr = r + anchos[i] / 2
            val x = c.x + rr * cos(angs[i]).toFloat(); val y = c.y + rr * sin(angs[i]).toFloat()
            if (i == 0) cuerpo.moveTo(x, y) else cuerpo.lineTo(x, y)
        }
        for (i in n downTo 0) {
            val rr = r - anchos[i] / 2
            cuerpo.lineTo(c.x + rr * cos(angs[i]).toFloat(), c.y + rr * sin(angs[i]).toFloat())
        }
        cuerpo.close()
        drawPath(cuerpo, color)
        // Las escamas: una media luna cada tanto, dos y una por hilera, corridas como tejas.
        if (escamas != null && grueso > 2.5f) {
            val largo = ((cuello - cola0) * r).toFloat()
            val paso = grueso * 0.34f
            var tt = largoCab * 0.75f; var k = 0
            while (tt < largo - paso) {
                val i = (tt / largo * n).toInt().coerceIn(0, n)
                val a = angs[i]; val w = anchos[i]
                val nx = cos(a).toFloat(); val ny = sin(a).toFloat(); val tx = -ny; val ty = nx
                val x = c.x + r * nx; val y = c.y + r * ny
                val rr = w * 0.2f
                val lados = if (k % 2 == 0) floatArrayOf(-0.22f, 0.22f) else floatArrayOf(0f)
                for (g in lados) {
                    val bx = x + nx * g * w; val by = y + ny * g * w
                    val arco = Path()
                    for (j in 0..8) {
                        val uu = -1.2f + 2.4f * j / 8
                        val px = bx - (cos(uu) * tx - sin(uu) * nx) * rr; val py = by - (cos(uu) * ty - sin(uu) * ny) * rr
                        if (j == 0) arco.moveTo(px, py) else arco.lineTo(px, py)
                    }
                    drawPath(arco, escamas, style = Stroke(max(w * 0.07f, 0.5f), cap = StrokeCap.Round))
                }
                tt += paso; k++
            }
        }
        // La cabeza, en el marco del cuello: f hacia donde avanza (hacia la cola), g hacia fuera del anillo.
        val hx = c.x + r * cos(cab).toFloat(); val hy = c.y + r * sin(cab).toFloat()
        val tx = -sin(cab).toFloat(); val ty = cos(cab).toFloat(); val nx = cos(cab).toFloat(); val ny = sin(cab).toFloat()
        fun q(f: Float, g: Float) = Offset(hx + tx * f * largoCab + nx * g * anchoCab, hy + ty * f * largoCab + ny * g * anchoCab)
        val lab = wCola / anchoCab / 2 + 0.07f
        // El cráneo y la mandíbula de arriba (por fuera), y la de abajo (por dentro), cerradas bajo la cola.
        drawPath(poligono(listOf(q(-0.62f, 0.46f), q(-0.4f, 0.6f), q(-0.1f, 0.68f), q(0.22f, 0.64f), q(0.5f, 0.55f), q(0.7f, 0.46f), q(0.82f, 0.38f),
            q(0.89f, 0.29f), q(0.9f, 0.2f), q(0.87f, lab + 0.03f), q(0.8f, lab), q(0.3f, lab), q(0.3f, -lab), q(-0.62f, -0.46f))), color)
        drawPath(poligono(listOf(q(-0.62f, -0.46f), q(-0.3f, -0.58f), q(0.1f, -0.56f), q(0.44f, -0.46f), q(0.66f, -0.34f), q(0.78f, -0.22f),
            q(0.76f, -lab), q(0.3f, -lab), q(0.3f, lab), q(-0.62f, 0.46f))), color)
        if (escamas != null) {
            val fino = Stroke(max(anchoCab * 0.055f, 0.5f), cap = StrokeCap.Round, join = StrokeJoin.Round)
            // Las comisuras, por encima y por debajo de la cola, que se abren hacia atrás.
            for (s in floatArrayOf(1f, -1f)) {
                val a = q(if (s > 0f) 0.84f else 0.72f, s * (lab - 0.02f)); val b = q(0.12f, s * (lab - 0.02f)); val d = q(-0.16f, s * (lab + 0.14f))
                drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(d.x, d.y) }, escamas, style = fino)
            }
            // El ojo con su pupila, dos escamas grandes de la cabeza y la fosa de la nariz.
            val ojo = q(0.12f, 0.38f); val ro = anchoCab * 0.15f
            drawCircle(escamas, ro, ojo)
            drawCircle(color, ro * 0.5f, ojo + Offset(ro * 0.25f, 0f))
            drawLine(escamas, q(-0.52f, 0.3f), q(-0.24f, 0.5f), max(anchoCab * 0.06f, 0.5f), StrokeCap.Round)
            drawLine(escamas, q(-0.52f, -0.3f), q(-0.24f, -0.45f), max(anchoCab * 0.06f, 0.5f), StrokeCap.Round)
            drawLine(escamas, q(0.62f, 0.36f), q(0.74f, 0.3f), max(anchoCab * 0.05f, 0.5f), StrokeCap.Round)
        }
    }

    /**
     * Las siete esferas: siete círculos finos uno dentro de otro, como el diagrama de los ofitas (recurso: así se
     * dibujan). [giro] (de 0 a 1) mueve una marca pequeña en cada círculo, cada uno a su paso, para el fondo vivo.
     */
    fun DrawScope.esferas(c: Offset, r: Float, color: Color, grueso: Float, giro: Float = 0f, n: Int = 7) {
        for (i in 0 until n) {
            val ri = r * (0.32f + 0.68f * i / (n - 1).coerceAtLeast(1))
            drawCircle(color.copy(alpha = color.alpha * (0.55f + 0.45f * (1f - i / n.toFloat()))), ri, c, style = Stroke(grueso))
            // La marca de cada esfera: el de dentro va más deprisa (vueltas enteras por vuelta, para que el ciclo cierre).
            val a = (giro * 360f * (n - i) + i * 51f) * (PI / 180.0)
            drawCircle(color, grueso * 1.6f, Offset(c.x + ri * cos(a).toFloat(), c.y + ri * sin(a).toFloat()))
        }
    }

    /**
     * La grieta de luz: una rendija en forma de huso entre [desde] y [hasta], afilada en las dos puntas y más abierta en medio
     * (allí mide [ancho] y algo más), que se curva apenas, con una o dos inflexiones suaves y ningún quiebre: luz que sale por
     * una raja en la piedra o en el velo, nunca un rayo. Blanca, con su halo violeta alrededor. [luz] (0 a 1) la abre y la
     * cierra; [semilla] elige su curva.
     */
    fun DrawScope.grieta(desde: Offset, hasta: Offset, ancho: Float, luz: Float = 1f, semilla: Int = 7) {
        if (luz <= 0f) return
        val rnd = java.util.Random(semilla.toLong())
        val dx = hasta.x - desde.x
        val dy = hasta.y - desde.y
        val largo = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val nx = -dy / largo
        val ny = dx / largo
        // La curva: una comba suave y, a veces, una segunda inflexión más leve.
        val a1 = (rnd.nextFloat() - 0.5f) * 0.09f
        val a2 = (rnd.nextFloat() - 0.5f) * 0.04f
        val n = 40
        fun centro(t: Float): Offset {
            val lado = largo * (a1 * sin(PI * t).toFloat() + a2 * sin(2 * PI * t).toFloat())
            return Offset(desde.x + dx * t + nx * lado, desde.y + dy * t + ny * lado)
        }
        fun huso(abre: Float): Path {
            val izq = ArrayList<Offset>(n + 1); val der = ArrayList<Offset>(n + 1)
            for (k in 0..n) {
                val t = k / n.toFloat()
                val p = centro(t)
                val m = abre * sin(PI * t).toFloat().pow(0.85f) / 2
                izq.add(Offset(p.x + nx * m, p.y + ny * m)); der.add(Offset(p.x - nx * m, p.y - ny * m))
            }
            return Path().apply {
                moveTo(izq[0].x, izq[0].y); for (k in 1..n) lineTo(izq[k].x, izq[k].y)
                for (k in n downTo 0) lineTo(der[k].x, der[k].y)
                close()
            }
        }
        val eje = Path().apply { val p0 = centro(0f); moveTo(p0.x, p0.y); for (k in 1..n) { val p = centro(k / n.toFloat()); lineTo(p.x, p.y) } }
        // El halo violeta, suave: un trazo ancho y tenue a lo largo y dos husos cada vez más abiertos.
        drawPath(eje, GRIETA_HALO.copy(alpha = 0.1f * luz), style = Stroke(ancho * 10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(huso(ancho * 7f), GRIETA_HALO.copy(alpha = 0.16f * luz))
        drawPath(huso(ancho * 3.6f), GRIETA_HALO.copy(alpha = 0.3f * luz))
        // La rendija: blanca, y más clara aún por dentro.
        drawPath(huso(ancho * 1.5f), GRIETA.copy(alpha = luz))
        drawPath(huso(ancho * 0.6f), Color.White.copy(alpha = luz))
    }

    /**
     * La lámpara de aceite de barro de las casas de Galilea (la «herodiana», hecha al torno), vista de lado: el cuerpo redondo
     * con la luz arriba, el disco hundido con su agujero de llenar, el pico corto y ancho, recortado a cuchillo, con la mecha y
     * la llama, y el pie. [c] es el centro del cuerpo; [w], el ancho total; [llama] (0 a 1), su tamaño, para que tiemble.
     */
    fun DrawScope.lampara(c: Offset, w: Float, barro: Color = BARRO_CLARO, llama: Float = 1f) {
        val h = w * 0.36f
        val oscuro = lerp(barro, Color.Black, 0.38f)
        val claro = lerp(barro, Color.White, 0.22f)
        val cuerpo = Path().apply {
            moveTo(c.x - w * 0.4f, c.y)
            cubicTo(c.x - w * 0.4f, c.y - h * 0.9f, c.x + w * 0.06f, c.y - h * 0.95f, c.x + w * 0.14f, c.y - h * 0.5f)
            lineTo(c.x + w * 0.36f, c.y - h * 0.44f)
            quadraticTo(c.x + w * 0.43f, c.y - h * 0.4f, c.x + w * 0.42f, c.y - h * 0.12f)
            lineTo(c.x + w * 0.14f, c.y + h * 0.14f)
            cubicTo(c.x + w * 0.04f, c.y + h * 0.5f, c.x - w * 0.36f, c.y + h * 0.48f, c.x - w * 0.4f, c.y)
            close()
        }
        drawPath(cuerpo, Brush.verticalGradient(listOf(claro, barro, oscuro), startY = c.y - h * 0.8f, endY = c.y + h * 0.45f))
        // La luz de arriba sobre el hombro.
        drawArc(Color.White.copy(alpha = 0.22f), 200f, 70f, false, Offset(c.x - w * 0.34f, c.y - h * 0.78f), Size(w * 0.48f, h * 1.3f), style = Stroke(w * 0.022f, cap = StrokeCap.Round))
        // El disco hundido y el agujero de llenar.
        drawOval(oscuro.copy(alpha = 0.45f), Offset(c.x - w * 0.27f, c.y - h * 0.76f), Size(w * 0.3f, h * 0.24f))
        drawCircle(lerp(barro, Color.Black, 0.62f), w * 0.04f, Offset(c.x - w * 0.12f, c.y - h * 0.64f))
        // El pie: un aro bajo el vientre.
        drawLine(oscuro, Offset(c.x - w * 0.16f, c.y + h * 0.44f), Offset(c.x + w * 0.04f, c.y + h * 0.42f), w * 0.03f, StrokeCap.Round)
        // La boca del pico, oscura, con la mecha asomando.
        val base = Offset(c.x + w * 0.39f, c.y - h * 0.4f)
        drawOval(lerp(barro, Color.Black, 0.6f), Offset(base.x - w * 0.04f, base.y - h * 0.06f), Size(w * 0.07f, h * 0.12f))
        if (llama > 0f) {
            val alto = w * 0.27f * llama
            val cl = Offset(base.x + w * 0.005f, base.y - alto * 0.45f)
            drawCircle(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = 0.42f), LLAMA_HALO.copy(alpha = 0f)), cl, alto * 1.4f), alto * 1.4f, cl)
            val fuego = Path().apply {
                moveTo(base.x - w * 0.04f, base.y)
                quadraticTo(base.x - w * 0.06f, base.y - alto * 0.55f, base.x + w * 0.012f, base.y - alto)
                quadraticTo(base.x + w * 0.06f, base.y - alto * 0.5f, base.x + w * 0.038f, base.y)
                close()
            }
            drawPath(fuego, LLAMA)
            // El corazón claro de la llama.
            val dentro = Path().apply {
                moveTo(base.x - w * 0.018f, base.y - alto * 0.04f)
                quadraticTo(base.x - w * 0.026f, base.y - alto * 0.4f, base.x + w * 0.006f, base.y - alto * 0.62f)
                quadraticTo(base.x + w * 0.03f, base.y - alto * 0.34f, base.x + w * 0.018f, base.y - alto * 0.04f)
                close()
            }
            drawPath(dentro, LLAMA_HALO)
            drawCircle(Color.White.copy(alpha = 0.55f), w * 0.011f, Offset(base.x, base.y - alto * 0.18f))
        }
    }

    /**
     * La chispa: la luz de lo alto que cada uno lleva dentro, como una estrella pequeña de cuatro puntas, blanca, con su halo
     * violeta (sobre un fondo claro, con [color] y [halo] del tema). [r] es el largo de las puntas; [luz] (0 a 1), cuánto brilla.
     */
    fun DrawScope.chispa(c: Offset, r: Float, luz: Float = 1f, color: Color = GRIETA, halo: Color = GRIETA_HALO) {
        if (luz <= 0f) return
        drawCircle(Brush.radialGradient(listOf(halo.copy(alpha = 0.5f * luz), halo.copy(alpha = 0f)), c, r * 1.6f), r * 1.6f, c)
        val estrella = Path()
        for (i in 0 until 8) {
            val rr = if (i % 2 == 0) r else r * 0.2f
            val a = -PI / 2 + PI / 4 * i
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (i == 0) estrella.moveTo(x, y) else estrella.lineTo(x, y)
        }
        estrella.close()
        drawPath(estrella, color.copy(alpha = luz))
        drawCircle(lerp(color, Color.White, 0.6f).copy(alpha = luz), r * 0.16f, c)
    }

    /**
     * La gema: un cabujón de jaspe verde (el heliotropo de las gemas de Abraxas), con sus nubes, una veta fina y las motas
     * rojas, abombado con la luz arriba a la izquierda. [brillo] (0 a 1) corre un destello de lado a lado sobre la piedra.
     */
    fun DrawScope.gemaJaspe(c: Offset, r: Float, brillo: Float = -1f, semilla: Int = 51) {
        drawCircle(Brush.radialGradient(listOf(Color(0xFF557F63), JASPE_VERDE, Color(0xFF223C2D)), Offset(c.x - r * 0.32f, c.y - r * 0.36f), r * 1.5f), r, c)
        val rnd = java.util.Random(semilla.toLong())
        clipPath(Path().apply { addOval(Rect(c, r)) }) {
            // Las nubes de la piedra.
            repeat(5) {
                val p = Offset(c.x + (rnd.nextFloat() - 0.5f) * r * 1.6f, c.y + (rnd.nextFloat() - 0.5f) * r * 1.6f)
                val rr = r * (0.25f + rnd.nextFloat() * 0.35f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFF6F9478).copy(alpha = 0.22f), Color.Transparent), p, rr), rr, p)
            }
            // Una veta fina que cruza la piedra.
            val veta = Path().apply {
                val y0 = c.y + (rnd.nextFloat() - 0.3f) * r
                moveTo(c.x - r, y0)
                cubicTo(c.x - r * 0.4f, y0 - r * 0.3f, c.x + r * 0.2f, y0 + r * 0.25f, c.x + r, y0 - r * 0.1f)
            }
            drawPath(veta, Color(0xFF8DAE95).copy(alpha = 0.25f), style = Stroke(r * 0.012f))
            // Las motas rojas, en grumos.
            repeat(4) {
                val g = Offset(c.x + (rnd.nextFloat() - 0.5f) * r * 1.5f, c.y + (rnd.nextFloat() - 0.5f) * r * 1.5f)
                repeat(7) {
                    val p = Offset(g.x + (rnd.nextFloat() - 0.5f) * r * 0.3f, g.y + (rnd.nextFloat() - 0.5f) * r * 0.3f)
                    drawCircle(JASPE_ROJO.copy(alpha = 0.35f + rnd.nextFloat() * 0.35f), r * (0.008f + rnd.nextFloat() * 0.018f), p)
                }
            }
            // El destello que corre sobre el cabujón.
            if (brillo in 0f..1f) {
                val x = c.x - r * 1.3f + r * 2.6f * brillo
                val fuerza = sin(PI * brillo).toFloat()
                drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.22f * fuerza), Color.Transparent), Offset(x, c.y - r * 0.45f), r * 0.55f), r * 0.55f, Offset(x, c.y - r * 0.45f))
            }
        }
        // La luz fija del abombado y la sombra del borde.
        drawArc(Color.White.copy(alpha = 0.16f), 195f, 75f, false, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f), style = Stroke(r * 0.06f, cap = StrokeCap.Round))
        drawCircle(Color.Black.copy(alpha = 0.22f), r * 0.97f, c, style = Stroke(r * 0.06f))
    }

    /**
     * Abraxas como en las gemas grabadas de los siglos II a IV (el «amuleto de Abraxas»): cabeza de gallo de perfil, torso con
     * coraza y faldellín de tiras, las dos piernas de serpiente que se abren y levantan la cabeza a los lados, el látigo en
     * alto en la mano de fuera y el escudo redondo en la otra. Mira a la derecha. [c] es el centro de la figura y [alto], su
     * altura; [color] es el del grabado y [hueco], el de los cortes de detalle. Es el mismo dibujo del sello
     * (`herramientas/sellos.py`, `abraxas`).
     */
    fun DrawScope.abraxas(c: Offset, alto: Float, color: Color, hueco: Color) {
        val u = alto / 88f
        val ox = c.x - 1f * u; val oy = c.y + 13f * u
        fun p(x: Float, y: Float) = Offset(ox + x * u, oy + y * u)
        fun circ(x: Float, y: Float, r: Float, col: Color = color) = drawCircle(col, r * u, p(x, y))
        fun linea(pts: List<Offset>, w: Float, col: Color = color) {
            val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (k in 1 until pts.size) lineTo(pts[k].x, pts[k].y) }
            drawPath(camino, col, style = Stroke(w * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        fun arco(x0: Float, y0: Float, x1: Float, y1: Float, desde: Float, barrido: Float, w: Float) =
            drawArc(hueco, desde, barrido, false, p(x0, y0), Size((x1 - x0) * u, (y1 - y0) * u), style = Stroke(w * u))
        // Las piernas de serpiente: salen del faldellín, bajan, se abren y suben con la cabeza hacia fuera.
        for (lado in floatArrayOf(-1f, 1f)) {
            val pts = bezier(p(lado * 3.5f, 4f), p(lado * 5f, 22f), p(lado * 22f, 31f), p(lado * 22.5f, 13f), 40)
            drawPath(cinta(pts, 6.8f * u, 4.4f * u), color)
            // Las escamas: cortes cortos de través.
            var i = 5
            while (i < 38) {
                val a = pts[i - 1]; val b = pts[i + 1]; val m = pts[i]
                val l = (b - a).getDistance().coerceAtLeast(0.0001f)
                val dx = (b.x - a.x) / l; val dy = (b.y - a.y) / l
                val ww = (6.8f - 2.4f * i / 40f) * u
                linea(listOf(Offset(m.x - dy * ww * 0.28f - dx * 0.6f * u, m.y + dx * ww * 0.28f - dy * 0.6f * u), Offset(m.x + dx * 0.5f * u, m.y + dy * 0.5f * u),
                    Offset(m.x + dy * ww * 0.28f - dx * 0.6f * u, m.y - dx * ww * 0.28f - dy * 0.6f * u)), 0.55f, hueco)
                i += 4
            }
            // La cabeza de la serpiente, levantada y mirando hacia fuera, con la boca abierta y la lengua.
            val h = Offset((pts.last().x - ox) / u, (pts.last().y - oy) / u)
            drawPath(poligono(listOf(p(h.x - lado * 2.6f, h.y + 1f), p(h.x - lado * 2.8f, h.y - 2.6f), p(h.x - lado * 0.6f, h.y - 5.2f), p(h.x + lado * 2.6f, h.y - 5.4f),
                p(h.x + lado * 6.4f, h.y - 4.2f), p(h.x + lado * 4f, h.y - 2.8f), p(h.x + lado * 6f, h.y - 1.4f), p(h.x + lado * 2f, h.y + 0.2f))), color)
            circ(h.x + lado * 1.4f, h.y - 3.4f, 0.75f, hueco)
            linea(listOf(p(h.x + lado * 6.8f, h.y - 2.8f), p(h.x + lado * 8.2f, h.y - 3.4f), p(h.x + lado * 8.9f, h.y - 4.4f)), 0.45f)
            linea(listOf(p(h.x + lado * 8.2f, h.y - 3.4f), p(h.x + lado * 9.1f, h.y - 2.7f)), 0.4f)
        }
        // El faldellín de tiras y el torso con la coraza.
        drawPath(poligono(listOf(p(-8.5f, -5f), p(8.5f, -5f), p(10.5f, 6f), p(-10.5f, 6f))), color)
        for (k in -3..3) linea(listOf(p(k * 2.7f, -3.6f), p(k * 3.1f, 5.2f)), 0.55f, hueco)
        drawPath(poligono(listOf(p(-12.5f, -27f), p(12.5f, -27f), p(10.5f, -16f), p(7.5f, -5f), p(-7.5f, -5f), p(-10.5f, -16f))), color)
        linea(listOf(p(-7.5f, -6.4f), p(7.5f, -6.4f)), 0.7f, hueco)                       // el cinturón
        arco(-8.2f, -26f, 0.6f, -16f, 20f, 140f, 0.6f)                                      // los pectorales
        arco(-0.6f, -26f, 8.2f, -16f, 20f, 140f, 0.6f)
        for (lado in floatArrayOf(-1f, 1f)) arco(lado * 3f - 3.6f, -14.5f, lado * 3f + 3.6f, -9.5f, 30f, 120f, 0.45f)
        circ(0f, -8.8f, 0.6f, hueco)
        // El brazo de fuera, levantado, con el látigo: el mango y tres correas que caen hacia fuera.
        linea(listOf(p(-11f, -25f), p(-19f, -30f), p(-18.5f, -40f)), 4.4f)
        circ(-18.5f, -41.5f, 2.8f)
        linea(listOf(p(-18.2f, -43f), p(-16.4f, -52f)), 1.5f)
        listOf(Triple(Offset(-19.5f, -57f), Offset(-25.5f, -55f), Offset(-28f, -47f)), Triple(Offset(-20.5f, -55f), Offset(-27f, -50f), Offset(-29f, -39.5f)),
            Triple(Offset(-20.5f, -53f), Offset(-25.5f, -45.5f), Offset(-26.5f, -32f))).forEachIndexed { k, (c1, c2, fin) ->
            linea(bezier(p(-16.4f, -52f), p(c1.x, c1.y), p(c2.x, c2.y), p(fin.x, fin.y), 16), 1.05f - k * 0.12f)
        }
        // El brazo del escudo y el escudo redondo, con su borde, su anillo y el umbo.
        linea(listOf(p(11f, -25f), p(16f, -21f), p(18f, -17f)), 4.4f)
        circ(19.5f, -17f, 10.6f)
        drawCircle(hueco, 8.7f * u, p(19.5f, -17f), style = Stroke(0.8f * u))
        drawCircle(hueco, 5.1f * u, p(19.5f, -17f), style = Stroke(0.6f * u))
        circ(19.5f, -17f, 1.9f, hueco); circ(19.5f, -17f, 1.05f)
        // El cuello y la cabeza de gallo, de perfil: la cresta, el pico, la barba, el ojo y las plumas del cuello.
        drawPath(poligono(listOf(p(-4.2f, -26f), p(4.4f, -26f), p(4f, -33f), p(-3f, -34f))), color)
        circ(0.8f, -37.2f, 5.6f)
        listOf(Triple(4f, -42.6f, 1.9f), Triple(1.6f, -44.2f, 2.2f), Triple(-1.2f, -44f, 2.1f), Triple(-3.6f, -42.2f, 1.8f)).forEach { (x, y, r) -> circ(x, y, r) }
        drawPath(poligono(listOf(p(4.6f, -41.6f), p(-4.6f, -41.4f), p(-2.6f, -38.4f), p(3.8f, -38.4f))), color)
        drawPath(poligono(listOf(p(5.2f, -39.6f), p(11.4f, -37f), p(5.4f, -35.2f))), color)
        drawPath(poligono(listOf(p(4.4f, -35.4f), p(7f, -35f), p(6.6f, -31f), p(5f, -30.4f), p(4f, -32.6f))), color)
        circ(2.6f, -38.4f, 1.15f, hueco)
        for (k in 0 until 4) for (j in 0 until 2) {
            val cx = -2.6f + k * 2.1f + j * 1f; val cy = -31.6f + j * 2.6f
            arco(cx - 1.1f, cy - 1.4f, cx + 1.1f, cy + 1f, 20f, 140f, 0.42f)
        }
    }

    /**
     * El emblema del tema: el amuleto de Abraxas, grabado en un cabujón de jaspe dentro del ouroboros y montado en un bisel
     * de [oro] granulado. Pequeño (menos de 56 dp de lado), Abraxas no se lee: queda el ouroboros con la chispa en el centro,
     * como en el ícono. [t] (0 a 1) corre un destello por la piedra, enciende una tras otra las siete estrellas y hace
     * latir la chispa.
     */
    fun DrawScope.emblema(c: Offset, r: Float, t: Float, oro: Color) {
        val pequeno = r * 2 < 56f * density
        // El bisel de oro y su granulado.
        drawCircle(lerp(oro, Color.Black, 0.25f), r, c)
        drawCircle(oro, r * 0.96f, c)
        drawCircle(Color.White.copy(alpha = 0.25f), r * 0.93f, c, style = Stroke(r * 0.02f))
        if (!pequeno) for (i in 0 until 48) {
            val a = 2 * PI * i / 48
            drawCircle(lerp(oro, Color.White, 0.3f), r * 0.017f, Offset(c.x + r * 0.925f * cos(a).toFloat(), c.y + r * 0.925f * sin(a).toFloat()))
        }
        val rg = r * (if (pequeno) 0.86f else 0.88f)
        // El destello pasa una vez por vuelta, en su primera quinta parte.
        gemaJaspe(c, rg, if (t < 0.2f) t / 0.2f else -1f)
        // Los grabados: primero la pared del corte, en sombra, y encima el corte claro.
        val d = max(r * 0.012f, 0.6f)
        val sombra = Offset(-d, -d)
        val ro = rg * (if (pequeno) 0.68f else 0.82f); val g = rg * (if (pequeno) 0.2f else 0.115f)
        ouroboros(c + sombra, ro, g, GRABADO_SOMBRA.copy(alpha = 0.7f), -90f)
        ouroboros(c, ro, g, GRABADO.copy(alpha = 0.92f), -90f, if (pequeno && r < 16f * density) null else GRABADO_HUECO)
        if (pequeno) {
            chispa(c, rg * 0.36f, 0.75f + 0.25f * sin(2 * PI * t).toFloat())
        } else {
            val alto = rg * 1.06f
            val ca = Offset(c.x - rg * 0.02f, c.y + rg * 0.02f)
            abraxas(ca + sombra, alto, GRABADO_SOMBRA.copy(alpha = 0.7f), GRABADO_SOMBRA.copy(alpha = 0f))
            abraxas(ca, alto, GRABADO.copy(alpha = 0.92f), GRABADO_HUECO)
            // Como en las gemas: la luna creciente arriba y las siete estrellas en arco a la derecha; una tras otra se encienden.
            val luna = Offset(c.x + rg * 0.4f, c.y - rg * 0.46f); val rl = rg * 0.075f
            val creciente = Path().apply {
                op(Path().apply { addOval(Rect(luna, rl)) }, Path().apply { addOval(Rect(Offset(luna.x + rl * 0.45f, luna.y - rl * 0.3f), rl * 0.86f)) }, PathOperation.Difference)
            }
            translate(sombra.x, sombra.y) { drawPath(creciente, GRABADO_SOMBRA.copy(alpha = 0.7f)) }
            drawPath(creciente, GRABADO.copy(alpha = 0.92f))
            for (k in 0 until 7) {
                val a = Math.toRadians(-28.0 + k * 12.0)
                val p = Offset(c.x + rg * 0.64f * cos(a).toFloat(), c.y + rg * 0.64f * sin(a).toFloat())
                val luz = (1f - abs(((t * 7f) % 7f) - k - 0.5f) * 1.6f).coerceIn(0f, 1f)
                val est = Path()
                for (i in 0 until 16) {
                    val rr = if (i % 2 == 0) rg * 0.04f else rg * 0.014f
                    val b = -PI / 2 + PI / 8 * i + k * 0.21
                    val x = p.x + rr * cos(b).toFloat(); val y = p.y + rr * sin(b).toFloat()
                    if (i == 0) est.moveTo(x, y) else est.lineTo(x, y)
                }
                est.close()
                translate(sombra.x, sombra.y) { drawPath(est, GRABADO_SOMBRA.copy(alpha = 0.7f)) }
                drawPath(est, lerp(GRABADO, Color.White, luz).copy(alpha = 0.92f))
                if (luz > 0f) drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.35f * luz), Color.Transparent), p, rg * 0.09f), rg * 0.09f, p)
            }
        }
    }

    /**
     * La sombra de un arconte: una silueta a contraluz, de hombros y cabeza de león de melena revuelta y hocico largo, que
     * mira a la derecha (a la izquierda con [mira] = -1). Nunca se dibuja entera: quien la llama la corta con el borde.
     * [c] es el centro de la cabeza y [alto], el alto de la cabeza. Con [desvanece], los hombros se pierden en la oscuridad
     * hacia abajo en vez de acabar en un borde.
     */
    fun DrawScope.sombraArconte(c: Offset, alto: Float, color: Color, mira: Float = 1f, desvanece: Boolean = false) {
        val a = alto
        fun p(x: Float, y: Float) = Offset(c.x + mira * x * a, c.y + y * a)
        // Los hombros, anchos, que se pierden hacia abajo.
        val hombros = Path().apply {
            val i = p(-1.7f, 3f); moveTo(i.x, i.y)
            val c1 = p(-1.5f, 0.9f); val q1 = p(-0.55f, 0.55f); quadraticTo(c1.x, c1.y, q1.x, q1.y)
            val q2 = p(0.4f, 0.5f); lineTo(q2.x, q2.y)
            val c3 = p(1.4f, 0.7f); val q3 = p(1.6f, 3f); quadraticTo(c3.x, c3.y, q3.x, q3.y)
            close()
        }
        if (desvanece) drawPath(hombros, Brush.verticalGradient(listOf(color, color.copy(alpha = 0f)), startY = c.y + 0.4f * a, endY = c.y + 2.8f * a))
        else drawPath(hombros, color)
        // La melena: un contorno de mechones irregulares alrededor de la cabeza.
        val melena = Path()
        val n = 22
        for (k in 0..n) {
            val ang = -PI * 0.95 + 1.9 * PI * k / n
            val rr = if (k % 2 == 0) 0.62f else 0.5f + 0.06f * ((k * 7) % 3)
            val x = -0.12f + rr * cos(ang).toFloat() * 0.95f; val y = -0.05f + rr * sin(ang).toFloat()
            val q = p(x, y)
            if (k == 0) melena.moveTo(q.x, q.y) else melena.lineTo(q.x, q.y)
        }
        melena.close()
        drawPath(melena, color)
        // El hocico, largo y romo, y la oreja que asoma de la melena.
        val hocico = Path().apply {
            val i = p(0.2f, -0.28f); moveTo(i.x, i.y)
            val c1 = p(0.62f, -0.3f); val f1 = p(0.82f, -0.08f); quadraticTo(c1.x, c1.y, f1.x, f1.y)
            val f2 = p(0.78f, 0.1f); lineTo(f2.x, f2.y)
            val c3 = p(0.55f, 0.28f); val f3 = p(0.2f, 0.3f); quadraticTo(c3.x, c3.y, f3.x, f3.y)
            close()
        }
        drawPath(hocico, color)
        drawPath(poligono(listOf(p(-0.05f, -0.48f), p(0.14f, -0.86f), p(0.22f, -0.46f))), color)
    }
}
