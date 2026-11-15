package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Los trazos que se repiten en las figuras de Psicodelia (`docs/diseno/temas/psicodelia.md`): sus colores de dibujo y las
 * figuras que comparten las superficies: la flor que vibra en colores complementarios, las luces líquidas, el símbolo de
 * la paz, el disco de vinilo, la casa victoriana, el botón de solapa del emblema, las burbujas del show de luces y la
 * tinta de arcoíris del periódico. Formas blandas y orgánicas, colores planos de cartel.
 */
object TrazosPsicodelia {
    /** Los pares complementarios que vibran (un color junto a su opuesto de igual valor, como en los carteles de 1967). */
    val MAGENTA = Color(0xFFE0218A)
    val VERDE = Color(0xFF2FBF71)
    val NARANJA = Color(0xFFFF7A1A)
    val AZUL = Color(0xFF2F6BFF)
    val AMARILLO = Color(0xFFFFD23F)
    val VIOLETA = Color(0xFF7B3FD1)
    /** Las casas: la madera desteñida, el blanco gastado y los pocos colores de las que ya se pintaban. */
    val CASA_GRIS = Color(0xFFA9A49B)
    val CASA_BLANCA = Color(0xFFE4DED3)
    val CASA_TURQUESA = Color(0xFF2BB3A8)
    val CASA_ROSA = Color(0xFFF28DB2)
    val CASA_AMARILLA = Color(0xFFF2C14E)
    /** La niebla y el cielo de la tarde. */
    val NIEBLA = Color(0xFFE9EDF0)
    val CIELO_TARDE = Color(0xFFF7C99B)
    /** La luz negra: lo que brilla sobre el negro violeta de la sala. */
    val LIMON = Color(0xFFC9F04A)
    val FLUOR_MAGENTA = Color(0xFFFF5CBE)
    val FLUOR_VIOLETA = Color(0xFFAE91FF)
    val FLUOR_NARANJA = Color(0xFFFF9F43)
    val SALA = Color(0xFF120A1C)
    /** El papel del periódico del barrio. */
    val PAPEL = Color(0xFFEFE7D6)
    /** El vinilo. */
    val VINILO = Color(0xFF17131A)
    /** La tinta oscura con que se contornean las figuras de los carteles (un violeta casi negro, nunca negro puro). */
    val TINTA = Color(0xFF2A1C32)

    /** La tinta de arcoíris del tintero partido: del magenta al amarillo y al azul en una sola pasada. */
    val ARCOIRIS = listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL, VIOLETA)
    /** El mismo arcoíris bajo la luz negra: del violeta al magenta, al naranja y al verde limón (sin pasar por el gris). */
    val ARCOIRIS_NOCHE = listOf(FLUOR_VIOLETA, FLUOR_MAGENTA, FLUOR_NARANJA, LIMON)

    /** El complementario de cada color de la paleta de dibujo (para que la figura vibre). */
    fun complementario(c: Color): Color = when (c) {
        MAGENTA -> VERDE; VERDE -> MAGENTA; NARANJA -> AZUL; AZUL -> NARANJA; AMARILLO -> VIOLETA; VIOLETA -> AMARILLO
        LIMON -> FLUOR_MAGENTA; FLUOR_MAGENTA -> LIMON; FLUOR_VIOLETA -> FLUOR_NARANJA; FLUOR_NARANJA -> FLUOR_VIOLETA
        else -> opuesto(c)
    }

    /**
     * El color opuesto de cualquier color en la rueda (el tono girado media vuelta, con la misma saturación y la misma luz):
     * el que vibra junto a él. Sirve para los colores del esquema, que no están en la paleta de dibujo.
     */
    fun opuesto(c: Color): Color {
        val mx = max(c.red, max(c.green, c.blue)); val mn = min(c.red, min(c.green, c.blue))
        val l = (mx + mn) / 2f; val d = mx - mn
        if (d < 0.0001f) return c
        val s = d / (1f - abs(2f * l - 1f))
        val h = when (mx) {
            c.red -> 60f * (((c.green - c.blue) / d).mod(6f))
            c.green -> 60f * ((c.blue - c.red) / d + 2f)
            else -> 60f * ((c.red - c.green) / d + 4f)
        }
        val h2 = (h + 180f).mod(360f)
        val k = (1f - abs(2f * l - 1f)) * s
        val x = k * (1f - abs((h2 / 60f).mod(2f) - 1f))
        val m = l - k / 2f
        val (r, g, b) = when ((h2 / 60f).toInt()) {
            0 -> Triple(k, x, 0f); 1 -> Triple(x, k, 0f); 2 -> Triple(0f, k, x)
            3 -> Triple(0f, x, k); 4 -> Triple(x, 0f, k); else -> Triple(k, 0f, x)
        }
        return Color((r + m).coerceIn(0f, 1f), (g + m).coerceIn(0f, 1f), (b + m).coerceIn(0f, 1f), c.alpha)
    }

    /** La tinta de arcoíris del tintero partido, de [desde] a [hasta], con la opacidad [alfa]; con [noche], la de la luz negra. */
    fun arcoiris(desde: Offset, hasta: Offset, alfa: Float = 1f, noche: Boolean = false): Brush =
        Brush.linearGradient((if (noche) ARCOIRIS_NOCHE else ARCOIRIS).map { it.copy(alpha = alfa) }, desde, hasta)

    // ------------------------------------------------------------------------------------------------ figuras

    /**
     * Una gota blanda: un círculo que se deforma con [n] lóbulos según [fase] (de 0 a 1, da la vuelta). Es la forma de las
     * luces líquidas y de las burbujas; con [blando] = 0 es un círculo.
     */
    fun gota(c: Offset, r: Float, fase: Float = 0f, blando: Float = 0.12f, n: Int = 3, puntos: Int = 48): Path = Path().apply {
        for (i in 0..puntos) {
            val a = 2f * PI.toFloat() * i / puntos
            val rr = r * (1f + blando * sin(n * a + fase * 2f * PI.toFloat()) + blando * 0.5f * cos((n + 2) * a - fase * 4f * PI.toFloat()))
            val p = Offset(c.x + rr * cos(a), c.y + rr * sin(a))
            if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /** Una onda de [x0] a [x1] a la altura [y], de [amp] de alto y [periodo] de largo, corrida [fase] (de 0 a 1). */
    fun onda(x0: Float, x1: Float, y: Float, amp: Float, periodo: Float, fase: Float = 0f): Path = Path().apply {
        val paso = max(periodo / 16f, 0.5f)
        var x = x0; var k = 0
        while (x <= x1 + 0.01f && k < 2000) {
            val yy = y + amp * sin(2f * PI.toFloat() * ((x - x0) / periodo + fase))
            if (k == 0) moveTo(x, yy) else lineTo(x, yy)
            x += paso; k++
        }
    }

    /**
     * Los pétalos redondos de una margarita, juntos en un solo trazo: [n] pétalos de punta redonda que nacen cerca del
     * centro ([desde], en fracción de [r]) y se ensanchan hasta su punta, a [r]. [ancho] es el radio de la punta, en
     * fracción de [r]; [giro], en radianes. Con [unidos], un solo contorno (para trazarlo sin que se crucen por dentro).
     */
    fun petalos(c: Offset, r: Float, n: Int = 6, giro: Float = 0f, ancho: Float = 0.34f, desde: Float = 0.12f, unidos: Boolean = false): Path {
        if (unidos) return (0 until n).fold(Path()) { p, i ->
            Path.combine(androidx.compose.ui.graphics.PathOperation.Union, p, petalos(c, r, 1, giro + 2f * PI.toFloat() * i / n, ancho, desde))
        }
        return petalosSueltos(c, r, n, giro, ancho, desde)
    }

    private fun petalosSueltos(c: Offset, r: Float, n: Int, giro: Float, ancho: Float, desde: Float): Path = Path().apply {
        val rp = r * ancho
        for (i in 0 until n) {
            val a = 2f * PI.toFloat() * i / n + giro
            val d = Offset(cos(a), sin(a)); val nn = Offset(-d.y, d.x)
            val t = c + d * (r - rp)
            val b = c + d * (r * desde)
            val izq = t + nn * rp
            val k1 = c + d * (r * 0.4f) + nn * (rp * 1.02f)
            val k2 = c + d * (r * 0.4f) - nn * (rp * 1.02f)
            moveTo(b.x, b.y)
            quadraticTo(k1.x, k1.y, izq.x, izq.y)
            arcTo(Rect(t, rp), Math.toDegrees(a.toDouble()).toFloat() + 90f, -180f, false)
            quadraticTo(k2.x, k2.y, b.x, b.y)
            close()
        }
    }

    /** Una margarita plana de un solo color, para lo pequeño (marcas, íconos, puntos): los pétalos y el botón calado en [centro]. */
    fun DrawScope.margarita(c: Offset, r: Float, color: Color, centro: Color? = null, n: Int = 8, giro: Float = 0f) {
        drawPath(petalos(c, r, n, giro, if (n >= 8) 0.25f else 0.32f, 0.1f), color)
        if (centro != null) { drawCircle(centro, r * 0.3f, c); drawCircle(color, r * 0.16f, c) }
    }

    /**
     * Una burbuja de aceite del show de luces: el cuerpo apenas teñido, el borde más vivo (el menisco) y el brillo de la
     * lámpara arriba a la izquierda. [alfa] la apaga entera.
     */
    fun DrawScope.burbuja(c: Offset, r: Float, color: Color, alfa: Float = 1f) {
        if (r <= 0f || alfa <= 0f) return
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = 0.08f * alfa), color.copy(alpha = 0.18f * alfa), color.copy(alpha = 0.42f * alfa)), c, r), r, c)
        drawCircle(color.copy(alpha = 0.55f * alfa), r, c, style = Stroke(max(r * 0.07f, 0.6f)))
        drawArc(Color.White.copy(alpha = 0.55f * alfa), 200f, 60f, false, Offset(c.x - r * 0.66f, c.y - r * 0.66f), Size(r * 1.32f, r * 1.32f),
            style = Stroke(max(r * 0.1f, 0.6f), cap = StrokeCap.Round))
    }

    /**
     * Las luces líquidas: [colores] gotas grandes y translúcidas que se mezclan y respiran dentro de [tam], movidas por [fase]
     * (de 0 a 1; la vuelta cierra sin salto). Cada gota tiene su resplandor, el cuerpo de tinta más vivo hacia el borde,
     * como el aceite entre dos vidrios, y alguna burbuja dentro. [intensidad] baja el brillo (de día van más tenues).
     */
    fun DrawScope.lucesLiquidas(tam: Size, fase: Float, colores: List<Color>, intensidad: Float = 1f, origen: Offset = Offset.Zero) {
        val dos = 2f * PI.toFloat()
        colores.forEachIndexed { i, col ->
            val t = fase * dos
            val c = Offset(origen.x + tam.width * (0.5f + 0.3f * sin(t + i * 2.1f)), origen.y + tam.height * (0.5f + 0.28f * cos(2f * t + i * 1.7f)))
            val r = min(tam.width, tam.height) * (0.3f + 0.05f * sin(t + i))
            gotaDeLuz(c, r, fase + i * 0.3f, 3 + i % 2, col, intensidad)
        }
    }

    /** Una gota de luz líquida: el resplandor, el cuerpo de tinta (más vivo en el borde), el menisco y una burbuja dentro. */
    private fun DrawScope.gotaDeLuz(c: Offset, r: Float, fase: Float, lobulos: Int, col: Color, k: Float, modo: BlendMode = BlendMode.SrcOver) {
        if (r <= 0f || k <= 0f) return
        drawCircle(Brush.radialGradient(listOf(col.copy(alpha = 0.16f * k), col.copy(alpha = 0f)), c, r * 1.55f), r * 1.55f, c, blendMode = modo)
        val cuerpo = gota(c, r, fase, 0.13f, lobulos, if (r > 60f) 96 else 48)
        drawPath(cuerpo, Brush.radialGradient(listOf(col.copy(alpha = 0.14f * k), col.copy(alpha = 0.22f * k), col.copy(alpha = 0.4f * k)), c, r * 1.12f), blendMode = modo)
        drawPath(cuerpo, col.copy(alpha = 0.22f * k), style = Stroke(max(r * 0.018f, 0.8f)), blendMode = modo)
        val b = Offset(c.x + r * 0.32f * cos(fase * 2f * PI.toFloat() + lobulos), c.y + r * 0.28f * sin(fase * 2f * PI.toFloat() + lobulos))
        burbuja(b, r * 0.13f, lerp(col, Color.White, 0.4f), k * 0.8f)
    }

    /**
     * El fondo vivo de la sala de baile (de noche) o de la calle (de día): las luces líquidas de un retroproyector, cinco
     * manchas de aceite y tinta que se cruzan despacio y respiran, y unas burbujas que suben con el calor de la lámpara. De
     * noche, en los colores de la luz negra, que se suman donde se cruzan; de día, en tintas de cartel muy tenues, que se
     * mezclan como en el papel. [t] da la vuelta de 0 a 1 sin salto; [fuerza] multiplica la opacidad.
     */
    fun DrawScope.lucesSala(t: Float, oscuro: Boolean, fuerza: Float = 1f, burbujas: Int = 9) {
        val w = size.width; val h = size.height; val m = min(w, h)
        val dos = 2f * PI.toFloat()
        val colores = if (oscuro) listOf(LIMON, FLUOR_MAGENTA, FLUOR_VIOLETA, FLUOR_NARANJA, FLUOR_MAGENTA)
            else listOf(MAGENTA, NARANJA, VERDE, AZUL, AMARILLO)
        val k = (if (oscuro) 0.5f else 0.3f) * fuerza
        val modo = if (oscuro) BlendMode.Screen else BlendMode.Multiply
        // Cada mancha: dónde anda (en fracción del ancho y del alto), cuánto se aleja, su tamaño y su fase. Las vueltas de
        // cada una son enteras, así el ciclo cierra.
        val manchas = listOf(
            floatArrayOf(0.14f, 0.16f, 0.12f, 0.1f, 0.48f, 0f), floatArrayOf(0.88f, 0.38f, 0.1f, 0.14f, 0.4f, 0.23f),
            floatArrayOf(0.22f, 0.74f, 0.14f, 0.08f, 0.44f, 0.47f), floatArrayOf(0.8f, 0.9f, 0.1f, 0.08f, 0.38f, 0.71f),
            floatArrayOf(0.55f, 0.52f, 0.2f, 0.18f, 0.26f, 0.9f),
        )
        manchas.forEachIndexed { i, f ->
            val a = (t + f[5]) * dos
            val c = Offset(w * (f[0] + f[2] * sin(a + i)), h * (f[1] + f[3] * cos(2f * a - i * 0.7f)))
            val r = m * f[4] * (0.92f + 0.08f * sin(3f * a + i))
            gotaDeLuz(c, r, t * (1 + i % 2) + f[5], 3 + i % 2, colores[i], k, modo)
        }
        // Las burbujas que suben, despacio, con su menisco.
        for (b in 0 until burbujas) {
            val sem = (b * 0.618034f) % 1f
            val y = h * (1.08f - 1.16f * (((t * (1 + b % 2)) + sem) % 1f))
            val x = w * ((sem * 1.7f + 0.13f * b) % 1f) + m * 0.03f * sin((t * 3f + sem) * dos)
            val r = m * (0.012f + 0.02f * ((b * 0.37f) % 1f))
            burbuja(Offset(x, y), r, colores[b % colores.size], (if (oscuro) 0.5f else 0.35f) * fuerza)
        }
    }

    /**
     * La flor que vibra: [petalos] pétalos redondos de [color] sobre un halo de su complementario ([contra]), con una línea
     * del complementario por dentro de cada pétalo, como en los carteles, y el botón en el complementario con su anillo del
     * primero. [vibra] (de 0 a 1) corre un poco el halo, como cuando dos colores opuestos de igual valor se tocan.
     */
    fun DrawScope.flor(c: Offset, r: Float, color: Color = MAGENTA, contra: Color = complementario(color), petalos: Int = 6,
                       vibra: Float = 0f, giro: Float = 0f) {
        val ancho = if (petalos >= 8) 0.27f else if (petalos >= 7) 0.3f else 0.34f
        // El halo complementario, corrido apenas: la vibración del borde.
        val dx = r * (0.035f + 0.035f * vibra)
        drawPath(petalos(Offset(c.x + dx, c.y - dx * 0.6f), r * 1.07f, petalos, giro, ancho * 1.05f, 0.1f), contra)
        val cuerpo = petalos(c, r, petalos, giro, ancho, 0.1f)
        drawPath(cuerpo, color)
        // La sombra de cada pétalo junto al botón y la línea de dentro.
        clipPath(cuerpo) { drawCircle(Brush.radialGradient(listOf(lerp(color, Color.Black, 0.22f), color.copy(alpha = 0f)), c, r * 0.62f), r * 0.62f, c) }
        if (r > 6f) drawPath(petalos(c, r * 0.8f, petalos, giro, ancho * 0.72f, 0.36f), contra.copy(alpha = 0.85f), style = Stroke(max(r * 0.035f, 0.6f), join = StrokeJoin.Round))
        // El botón: el disco del complementario, el anillo del primero y un brillo.
        drawCircle(contra, r * 0.3f, c)
        drawCircle(color, r * 0.3f, c, style = Stroke(r * 0.06f))
        if (r > 10f) for (k in 0 until 8) drawCircle(lerp(contra, Color.White, 0.45f), r * 0.03f, Offset(c.x + r * 0.17f * cos(k * PI.toFloat() / 4 + giro), c.y + r * 0.17f * sin(k * PI.toFloat() / 4 + giro)))
        drawCircle(lerp(contra, Color.White, 0.35f), r * 0.08f, Offset(c.x - r * 0.08f, c.y - r * 0.09f))
    }

    /** El símbolo de la paz (Holtom, 1958; de dominio público), en la versión de una sola línea: un aro con su tridente invertido. */
    fun DrawScope.paz(c: Offset, r: Float, color: Color, grueso: Float = r * 0.16f) {
        val st = Stroke(grueso, cap = StrokeCap.Round)
        drawCircle(color, r, c, style = st)
        drawLine(color, Offset(c.x, c.y - r), Offset(c.x, c.y + r), grueso, StrokeCap.Round)
        val a = PI.toFloat() / 4
        drawLine(color, c, Offset(c.x - r * sin(a), c.y + r * cos(a)), grueso, StrokeCap.Round)
        drawLine(color, c, Offset(c.x + r * sin(a), c.y + r * cos(a)), grueso, StrokeCap.Round)
    }

    /** El disco de vinilo: negro con sus surcos, el canto, el brillo que gira con [giro] y la etiqueta de [etiqueta] con su flor. */
    fun DrawScope.disco(c: Offset, r: Float, etiqueta: Color = NARANJA, giro: Float = 0f) {
        drawCircle(VINILO, r, c)
        var k = 0.94f; var n = 0
        while (k > 0.42f) {
            drawCircle(Color.White.copy(alpha = if (n % 5 == 4) 0.09f else 0.045f), r * k, c, style = Stroke(max(r * 0.006f, 0.5f)))
            k -= 0.032f; n++
        }
        // El brillo de la lámpara en los surcos: dos abanicos opuestos que se esfuman a los lados.
        val claro = Color.White.copy(alpha = 0.17f); val nada = Color.White.copy(alpha = 0f)
        rotate(giro * 360f, c) {
            drawCircle(Brush.sweepGradient(listOf(nada, claro, nada, nada, nada, claro, nada, nada, nada), c), r * 0.96f, c)
        }
        drawCircle(Color.White.copy(alpha = 0.12f), r * 0.985f, c, style = Stroke(max(r * 0.015f, 0.6f)))
        drawCircle(VINILO, r * 0.4f, c)
        drawCircle(etiqueta, r * 0.35f, c)
        drawCircle(lerp(etiqueta, Color.Black, 0.25f), r * 0.35f, c, style = Stroke(max(r * 0.012f, 0.5f)))
        drawCircle(lerp(etiqueta, Color.White, 0.3f), r * 0.3f, c, style = Stroke(max(r * 0.006f, 0.4f)))
        flor(c, r * 0.21f, complementario(etiqueta), etiqueta, 6, 0f, giro * 2f * PI.toFloat())
        drawCircle(lerp(etiqueta, Color.Black, 0.55f), r * 0.04f, c)
    }

    /**
     * Una casa victoriana de San Francisco, vista de frente: el frontón con sus escamas de madera y su remate, la cornisa
     * con sus ménsulas, el mirador saliente de dos pisos con sus tres ventanas de guillotina (el costado de la luz más
     * claro, el otro en sombra), la tablazón de la fachada y la puerta alta con su montante, sus columnitas y la escalera.
     * [base] es el pie de la casa, [ancho] y [alto] su tamaño, [color] la madera (casi siempre gris) y [adorno] el color
     * de las molduras. [ventanas] colorea los vidrios (de noche, encendidos).
     */
    fun DrawScope.casaVictoriana(base: Offset, ancho: Float, alto: Float, color: Color = CASA_GRIS, adorno: Color = CASA_BLANCA,
                                 ventanas: Color = Color(0xFF5E6B78)) {
        val x0 = base.x - ancho / 2; val y0 = base.y - alto
        val sombra = lerp(color, Color.Black, 0.28f)
        val luz = lerp(color, Color.White, 0.12f)
        val fino = max(ancho * 0.008f, 0.5f)
        // El cuerpo, con la tablazón horizontal.
        val cuerpoArriba = y0 + alto * 0.2f
        drawRect(color, Offset(x0, cuerpoArriba), Size(ancho, alto * 0.8f))
        var yt = cuerpoArriba + alto * 0.05f
        while (yt < base.y - alto * 0.02f) { drawLine(sombra.copy(alpha = 0.25f), Offset(x0, yt), Offset(x0 + ancho, yt), fino); yt += alto * 0.028f }
        // El frontón: el triángulo con sus escamas de madera en hileras y el remate redondo.
        val fronton = Path().apply { moveTo(x0 - ancho * 0.04f, y0 + alto * 0.2f); lineTo(base.x, y0); lineTo(x0 + ancho * 1.04f, y0 + alto * 0.2f); close() }
        drawPath(fronton, luz)
        clipPath(fronton) {
            val e = ancho * 0.07f; var fila = 0; var yy = y0 + alto * 0.05f
            while (yy < y0 + alto * 0.21f) {
                var xx = x0 - ancho * 0.04f + (if (fila % 2 == 0) 0f else e / 2)
                while (xx < x0 + ancho * 1.05f) { drawArc(sombra.copy(alpha = 0.35f), 0f, 180f, false, Offset(xx, yy - e / 2), Size(e, e), style = Stroke(fino)); xx += e }
                yy += e * 0.55f; fila++
            }
        }
        drawPath(Path().apply { moveTo(x0 - ancho * 0.05f, y0 + alto * 0.205f); lineTo(base.x, y0 - alto * 0.005f); lineTo(x0 + ancho * 1.05f, y0 + alto * 0.205f) },
            adorno, style = Stroke(ancho * 0.03f, join = StrokeJoin.Round))
        drawCircle(adorno, ancho * 0.055f, Offset(base.x, y0 + alto * 0.1f))
        drawCircle(ventanas, ancho * 0.032f, Offset(base.x, y0 + alto * 0.1f))
        drawLine(adorno, Offset(base.x, y0 - alto * 0.005f), Offset(base.x, y0 - alto * 0.035f), ancho * 0.02f, StrokeCap.Round)
        // La cornisa con sus ménsulas.
        drawRect(adorno, Offset(x0 - ancho * 0.04f, y0 + alto * 0.2f), Size(ancho * 1.08f, alto * 0.026f))
        for (i in 0..6) drawRect(adorno, Offset(x0 + ancho * (0.02f + i * 0.155f), y0 + alto * 0.226f), Size(ancho * 0.035f, alto * 0.04f))
        drawRect(sombra.copy(alpha = 0.4f), Offset(x0, y0 + alto * 0.266f), Size(ancho, alto * 0.008f))
        // El mirador saliente: un trapecio con sus tres ventanas altas, en dos pisos; el costado izquierdo a la luz.
        for (piso in 0..1) {
            val yv = y0 + alto * (0.31f + piso * 0.27f); val h = alto * 0.22f
            val a = x0 + ancho * 0.04f; val b = x0 + ancho * 0.13f; val c2 = x0 + ancho * 0.57f; val d = x0 + ancho * 0.66f
            drawPath(Path().apply { moveTo(a, yv + h); lineTo(b, yv); lineTo(b, yv + h); close() }, luz)
            drawPath(Path().apply { moveTo(c2, yv); lineTo(d, yv + h); lineTo(c2, yv + h); close() }, sombra)
            drawRect(color, Offset(b, yv), Size(c2 - b, h))
            drawRect(adorno, Offset(a - ancho * 0.01f, yv - alto * 0.018f), Size(d - a + ancho * 0.02f, alto * 0.018f))
            for (v in 0..2) {
                val vx = b + (c2 - b) * (0.08f + v * 0.32f); val vw = (c2 - b) * 0.22f
                val vy = yv + h * 0.12f; val vh = h * 0.74f
                drawRect(ventanas, Offset(vx, vy), Size(vw, vh))
                drawRect(Color.White.copy(alpha = 0.12f), Offset(vx, vy), Size(vw * 0.4f, vh * 0.48f))
                drawRect(adorno, Offset(vx, vy), Size(vw, vh), style = Stroke(ancho * 0.014f))
                drawLine(adorno, Offset(vx, vy + vh * 0.5f), Offset(vx + vw, vy + vh * 0.5f), ancho * 0.012f)
                drawRect(adorno, Offset(vx - vw * 0.12f, vy - h * 0.06f), Size(vw * 1.24f, h * 0.045f))
            }
            drawRect(adorno, Offset(a, yv + h), Size(d - a, alto * 0.02f))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(d, yv - alto * 0.018f), Size(ancho * 0.025f, h + alto * 0.038f))
        }
        // La puerta alta con su montante, sus dos columnitas, el sombrerete y la escalera.
        val px = x0 + ancho * 0.73f; val pw = ancho * 0.17f; val py = y0 + alto * 0.63f; val ph = alto * 0.27f
        drawRect(sombra, Offset(px, py), Size(pw, ph))
        drawRect(ventanas, Offset(px + pw * 0.12f, py + ph * 0.06f), Size(pw * 0.76f, ph * 0.16f))
        drawRect(lerp(sombra, Color.Black, 0.2f), Offset(px + pw * 0.14f, py + ph * 0.3f), Size(pw * 0.72f, ph * 0.66f))
        drawRect(adorno, Offset(px, py), Size(pw, ph), style = Stroke(ancho * 0.014f))
        listOf(px - ancho * 0.03f, px + pw + ancho * 0.008f).forEach { cx -> drawRect(adorno, Offset(cx, py), Size(ancho * 0.022f, ph)) }
        drawPath(Path().apply { moveTo(px - ancho * 0.045f, py); lineTo(px + pw / 2, py - alto * 0.04f); lineTo(px + pw + ancho * 0.045f, py); close() }, adorno)
        for (e in 0..3) drawRect(lerp(adorno, Color.Black, 0.08f + e * 0.03f), Offset(px - ancho * (0.02f + e * 0.02f), base.y - alto * (0.1f - e * 0.025f)),
            Size(pw + ancho * (0.04f + e * 0.04f), alto * 0.025f))
        // La sombra del lado derecho y el zócalo.
        drawRect(Color.Black.copy(alpha = 0.13f), Offset(x0 + ancho * 0.94f, y0 + alto * 0.2f), Size(ancho * 0.06f, alto * 0.8f))
        drawRect(sombra.copy(alpha = 0.6f), Offset(x0, base.y - alto * 0.025f), Size(ancho * 0.66f, alto * 0.025f))
    }

    /**
     * El botón de solapa del emblema: la orla de franjas torcidas en [anillo] y su complementario, el disco de [fondo] con
     * su trama de imprenta y su luz de cúpula, la margarita magenta que vibra y, en su botón amarillo, el símbolo de la paz.
     * [vibra] corre el halo de la flor; [giro] (de 0 a 1) gira la orla una franja; [destello] (de 0 a 1) pasa un brillo por
     * la cúpula; [noche] le pone el resplandor de la luz negra alrededor.
     */
    private fun DrawScope.botonSolapa(c: Offset, r: Float, anillo: Color, fondo: Color, vibra: Float, giro: Float, destello: Float, noche: Boolean) {
        val ro = r * 0.95f; val ri = r * 0.78f
        if (noche) drawCircle(Brush.radialGradient(listOf(LIMON.copy(alpha = 0.2f), LIMON.copy(alpha = 0f)), c, r * 1.05f), r * 1.05f, c)
        drawCircle(Color.Black.copy(alpha = 0.22f), ro, Offset(c.x + r * 0.03f, c.y + r * 0.05f))
        // La orla: franjas torcidas de los dos colores, que giran despacio.
        val otro = complementario(anillo)
        drawCircle(anillo, ro, c)
        val n = 18
        val orla = Path().apply { addOval(Rect(c, ro)); addOval(Rect(c, ri)); fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd }
        clipPath(orla) {
            for (k in 0 until n) {
                val a0 = 2f * PI.toFloat() * (k + giro) / n; val paso = 2f * PI.toFloat() / n
                val tuerce = paso * 0.9f
                drawPath(Path().apply {
                    moveTo(c.x + ri * cos(a0), c.y + ri * sin(a0)); lineTo(c.x + ro * 1.02f * cos(a0 + tuerce), c.y + ro * 1.02f * sin(a0 + tuerce))
                    lineTo(c.x + ro * 1.02f * cos(a0 + tuerce + paso * 0.5f), c.y + ro * 1.02f * sin(a0 + tuerce + paso * 0.5f))
                    lineTo(c.x + ri * cos(a0 + paso * 0.5f), c.y + ri * sin(a0 + paso * 0.5f)); close()
                }, otro)
            }
            // El canto de la orla, abombado: claro arriba a la izquierda, oscuro abajo a la derecha.
            drawCircle(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.32f), Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                Offset(c.x - ro, c.y - ro), Offset(c.x + ro, c.y + ro)), ro, c)
        }
        drawCircle(TINTA.copy(alpha = 0.5f), ro, c, style = Stroke(max(r * 0.012f, 0.6f)))
        // El disco, con su trama de puntos de imprenta y la luz de la cúpula.
        drawCircle(fondo, ri, c)
        clipPath(Path().apply { addOval(Rect(c, ri)) }) {
            val p = max(ri * 0.075f, 2f); var y = c.y - ri; var fila = 0
            val punto = lerp(fondo, Color.White, 0.22f).copy(alpha = 0.55f)
            while (y < c.y + ri) {
                var x = c.x - ri + (if (fila % 2 == 0) 0f else p / 2)
                while (x < c.x + ri) {
                    val f = ((x - (c.x - ri)) + (y - (c.y - ri))) / (4f * ri)
                    val rr = p * 0.28f * (1f - f)
                    if (rr > 0.35f) drawCircle(punto, rr, Offset(x, y))
                    x += p
                }
                y += p * 0.87f; fila++
            }
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent, Color.Black.copy(alpha = 0.25f)),
                Offset(c.x - ri * 0.35f, c.y - ri * 0.4f), ri * 1.6f), ri, c)
        }
        drawCircle(TINTA.copy(alpha = 0.45f), ri, c, style = Stroke(max(r * 0.012f, 0.6f)))
        // La margarita que vibra y, en su botón amarillo, la paz.
        val rf = ri * 0.86f
        flor(c, rf, MAGENTA, lerp(fondo, Color.White, 0.42f), 8, vibra, -PI.toFloat() / 8)
        drawCircle(AMARILLO, rf * 0.32f, c)
        drawCircle(lerp(AMARILLO, NARANJA, 0.5f), rf * 0.32f, c, style = Stroke(max(rf * 0.03f, 0.6f)))
        if (r * 2 >= 64f * density) paz(c, rf * 0.2f, lerp(MAGENTA, TINTA, 0.35f), rf * 0.04f)
        else drawCircle(lerp(AMARILLO, Color.White, 0.5f), rf * 0.1f, Offset(c.x - rf * 0.08f, c.y - rf * 0.08f))
        // El brillo de la cúpula de plástico: la media luna clara de arriba y el destello que la cruza.
        drawArc(Color.White.copy(alpha = 0.38f), 195f, 70f, false, Offset(c.x - ri * 0.78f, c.y - ri * 0.78f), Size(ri * 1.56f, ri * 1.56f),
            style = Stroke(ri * 0.09f, cap = StrokeCap.Round))
        drawCircle(Color.White.copy(alpha = 0.55f), ri * 0.045f, Offset(c.x - ri * 0.5f, c.y - ri * 0.56f))
        if (destello > 0f) {
            val a = Math.toRadians(200.0 + 70.0 * destello).toFloat()
            val q = Offset(c.x + ri * 0.74f * cos(a), c.y + ri * 0.74f * sin(a))
            val rb = ri * 0.32f
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.45f * sin(PI.toFloat() * destello)), Color.Transparent), q, rb), rb, q)
        }
    }

    /**
     * El emblema del tema: el botón de solapa con la margarita que vibra (magenta sobre su complementario), su orla de
     * franjas de [anillo] y el disco de [fondo], con el símbolo de la paz en el botón de la flor si el tamaño lo permite.
     */
    fun DrawScope.emblema(c: Offset, r: Float, anillo: Color = NARANJA, fondo: Color = AZUL, vibra: Float = 0f, giro: Float = 0f) {
        botonSolapa(c, r, anillo, fondo, vibra, giro / (2f * PI.toFloat()), 0f, false)
    }

    /**
     * El emblema animado de la app, como el ícono: el botón de solapa verde con la margarita magenta y la orla naranja y
     * azul. [t] (de 0 a 1) es la vuelta: la flor vibra (su halo se corre y vuelve), la orla gira una franja y un brillo
     * cruza la cúpula. Movimiento contenido: nada parpadea. Pequeño (menos de 48 dp), la flor sola, que vibra. [oscuro]: la
     * sala con luz negra, con el resplandor verde limón alrededor.
     */
    fun DrawScope.emblemaVivo(c: Offset, r: Float, t: Float, oscuro: Boolean) {
        val vibra = 0.5f + 0.5f * sin(2f * PI.toFloat() * t)
        if (r * 2 < 48f * density) {
            if (oscuro) drawCircle(Brush.radialGradient(listOf(LIMON.copy(alpha = 0.22f), LIMON.copy(alpha = 0f)), c, r), r, c)
            val rf = r * 0.9f
            flor(Offset(c.x - rf * 0.03f, c.y + rf * 0.02f), rf, MAGENTA, if (oscuro) LIMON else VERDE, 8, vibra, -PI.toFloat() / 8)
            drawCircle(AMARILLO, rf * 0.3f, Offset(c.x - rf * 0.03f, c.y + rf * 0.02f))
            drawCircle(lerp(AMARILLO, Color.White, 0.55f), rf * 0.1f, Offset(c.x - rf * 0.1f, c.y - rf * 0.05f))
            return
        }
        botonSolapa(c, r, NARANJA, VERDE, vibra, t, t, oscuro)
    }
}
