package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trazos que se repiten en las figuras de Psicodelia (`docs/diseno/temas/psicodelia.md`): sus colores de dibujo y las
 * figuras que comparten las superficies: la flor que vibra en colores complementarios, las luces líquidas, el símbolo de
 * la paz, el disco de vinilo, la casa victoriana y el emblema. Formas blandas y orgánicas, colores planos de cartel.
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

    /** La tinta de arcoíris del tintero partido: del magenta al amarillo y al azul en una sola pasada. */
    val ARCOIRIS = listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL, VIOLETA)

    /** El complementario de cada color de la paleta de dibujo (para que la figura vibre). */
    fun complementario(c: Color): Color = when (c) {
        MAGENTA -> VERDE; VERDE -> MAGENTA; NARANJA -> AZUL; AZUL -> NARANJA; AMARILLO -> VIOLETA; VIOLETA -> AMARILLO
        LIMON -> FLUOR_MAGENTA; FLUOR_MAGENTA -> LIMON; FLUOR_VIOLETA -> FLUOR_NARANJA; FLUOR_NARANJA -> FLUOR_VIOLETA
        else -> lerp(c, Color.White, 0.6f)
    }

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

    /**
     * Las luces líquidas: [colores] gotas grandes y translúcidas que se mezclan y respiran dentro de [tam], movidas por [fase]
     * (de 0 a 1). [intensidad] baja el brillo (de día van más tenues).
     */
    fun DrawScope.lucesLiquidas(tam: Size, fase: Float, colores: List<Color>, intensidad: Float = 1f, origen: Offset = Offset.Zero) {
        colores.forEachIndexed { i, col ->
            val t = fase * 2f * PI.toFloat() + i * 2.1f
            val c = Offset(origen.x + tam.width * (0.5f + 0.32f * sin(t * 0.7f + i)), origen.y + tam.height * (0.5f + 0.3f * cos(t * 0.5f + i * 1.7f)))
            val r = minOf(tam.width, tam.height) * (0.34f + 0.06f * sin(t + i))
            drawPath(gota(c, r, fase + i * 0.3f, 0.14f, 3 + i % 2), Brush.radialGradient(listOf(col.copy(alpha = 0.55f * intensidad), col.copy(alpha = 0f)), c, r * 1.15f))
        }
    }

    /**
     * La flor que vibra: [petalos] pétalos redondos de [color] sobre un halo de su complementario, con el centro en el
     * complementario y un anillo del primero. [vibra] (de 0 a 1) desplaza un poco el halo, como cuando dos colores
     * opuestos de igual valor se tocan en un cartel.
     */
    fun DrawScope.flor(c: Offset, r: Float, color: Color = MAGENTA, contra: Color = complementario(color), petalos: Int = 6,
                       vibra: Float = 0f, giro: Float = 0f) {
        val rp = r * 0.36f
        val d = r * 0.58f
        // El halo complementario, corrido apenas: la vibración del borde.
        for (i in 0 until petalos) {
            val a = 2f * PI.toFloat() * i / petalos + giro
            val dx = r * 0.04f * (1f + vibra)
            drawCircle(contra, rp * 1.12f, Offset(c.x + d * cos(a) + dx, c.y + d * sin(a) - dx * 0.5f))
        }
        for (i in 0 until petalos) {
            val a = 2f * PI.toFloat() * i / petalos + giro
            val p = Offset(c.x + d * cos(a), c.y + d * sin(a))
            drawCircle(color, rp, p)
            drawCircle(lerp(color, Color.White, 0.35f), rp * 0.38f, Offset(p.x - rp * 0.28f, p.y - rp * 0.32f))
        }
        drawCircle(contra, r * 0.34f, c)
        drawCircle(color, r * 0.34f, c, style = Stroke(r * 0.07f))
        drawCircle(lerp(contra, Color.White, 0.3f), r * 0.12f, Offset(c.x - r * 0.08f, c.y - r * 0.09f))
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

    /** El disco de vinilo: negro con sus surcos y el brillo que gira con [giro], y la etiqueta de [etiqueta] con su flor. */
    fun DrawScope.disco(c: Offset, r: Float, etiqueta: Color = NARANJA, giro: Float = 0f) {
        drawCircle(VINILO, r, c)
        var k = 0.92f
        while (k > 0.42f) { drawCircle(Color.White.copy(alpha = 0.06f), r * k, c, style = Stroke(r * 0.006f)); k -= 0.045f }
        val a = giro * 2f * PI.toFloat()
        drawArc(Color.White.copy(alpha = 0.16f), Math.toDegrees(a.toDouble()).toFloat() - 20f, 40f, true, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f))
        drawArc(Color.White.copy(alpha = 0.16f), Math.toDegrees(a.toDouble()).toFloat() + 160f, 40f, true, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f))
        drawCircle(VINILO, r * 0.42f, c)
        drawCircle(etiqueta, r * 0.36f, c)
        flor(c, r * 0.22f, complementario(etiqueta), etiqueta, 6, 0f, a)
        drawCircle(VINILO, r * 0.035f, c)
    }

    /**
     * Una casa victoriana de San Francisco, vista de frente: el mirador saliente con sus tres ventanas, el frontón con su
     * remate y la escalera. [base] es el pie de la casa, [ancho] y [alto] su tamaño, [color] la madera (casi siempre gris) y
     * [adorno] el color de las molduras. [ventanas] colorea las ventanas (de noche, encendidas).
     */
    fun DrawScope.casaVictoriana(base: Offset, ancho: Float, alto: Float, color: Color = CASA_GRIS, adorno: Color = CASA_BLANCA,
                                 ventanas: Color = Color(0xFF5E6B78)) {
        val x0 = base.x - ancho / 2; val y0 = base.y - alto
        val sombra = lerp(color, Color.Black, 0.25f)
        // El cuerpo y el frontón.
        drawRect(color, Offset(x0, y0 + alto * 0.18f), Size(ancho, alto * 0.82f))
        drawPath(Path().apply {
            moveTo(x0 - ancho * 0.04f, y0 + alto * 0.2f); lineTo(base.x, y0); lineTo(x0 + ancho * 1.04f, y0 + alto * 0.2f); close()
        }, color)
        drawPath(Path().apply {
            moveTo(x0 - ancho * 0.04f, y0 + alto * 0.2f); lineTo(base.x, y0); lineTo(x0 + ancho * 1.04f, y0 + alto * 0.2f)
        }, adorno, style = Stroke(ancho * 0.03f))
        drawCircle(adorno, ancho * 0.05f, Offset(base.x, y0 + alto * 0.1f))
        // La cornisa con sus ménsulas.
        drawRect(adorno, Offset(x0 - ancho * 0.03f, y0 + alto * 0.2f), Size(ancho * 1.06f, alto * 0.025f))
        for (i in 0..5) drawRect(adorno, Offset(x0 + ancho * (0.04f + i * 0.18f), y0 + alto * 0.225f), Size(ancho * 0.03f, alto * 0.035f))
        // El mirador saliente: un trapecio con tres ventanas altas, en dos pisos.
        for (piso in 0..1) {
            val yt = y0 + alto * (0.3f + piso * 0.27f); val h = alto * 0.22f
            drawPath(Path().apply {
                moveTo(x0 + ancho * 0.05f, yt + h); lineTo(x0 + ancho * 0.12f, yt); lineTo(x0 + ancho * 0.58f, yt); lineTo(x0 + ancho * 0.65f, yt + h); close()
            }, lerp(color, Color.White, 0.08f))
            for (v in 0..2) {
                val vx = x0 + ancho * (0.15f + v * 0.15f)
                drawRect(ventanas, Offset(vx, yt + h * 0.12f), Size(ancho * 0.1f, h * 0.76f))
                drawRect(adorno, Offset(vx, yt + h * 0.12f), Size(ancho * 0.1f, h * 0.76f), style = Stroke(ancho * 0.012f))
            }
            drawRect(adorno, Offset(x0 + ancho * 0.05f, yt + h), Size(ancho * 0.6f, alto * 0.02f))
        }
        // La puerta alta con su escalera.
        drawRect(sombra, Offset(x0 + ancho * 0.72f, y0 + alto * 0.62f), Size(ancho * 0.18f, alto * 0.28f))
        drawRect(adorno, Offset(x0 + ancho * 0.72f, y0 + alto * 0.62f), Size(ancho * 0.18f, alto * 0.28f), style = Stroke(ancho * 0.015f))
        for (e in 0..3) drawRect(lerp(adorno, Color.Black, 0.1f), Offset(x0 + ancho * (0.68f - e * 0.02f), y0 + alto * (0.9f + e * 0.025f)), Size(ancho * (0.26f + e * 0.04f), alto * 0.025f))
        // La sombra del lado derecho.
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(x0 + ancho * 0.93f, y0 + alto * 0.18f), Size(ancho * 0.07f, alto * 0.82f))
    }

    /**
     * El emblema del tema: la flor que vibra (magenta sobre verde) dentro de un aro de [anillo], con el símbolo de la paz
     * pequeño en el centro si el tamaño lo permite.
     */
    fun DrawScope.emblema(c: Offset, r: Float, anillo: Color = NARANJA, fondo: Color = AZUL, vibra: Float = 0f, giro: Float = 0f) {
        drawCircle(fondo, r, c)
        drawCircle(anillo, r, c, style = Stroke(r * 0.1f))
        drawCircle(complementario(anillo), r * 0.88f, c, style = Stroke(r * 0.03f))
        flor(c, r * 0.7f, MAGENTA, VERDE, 6, vibra, giro)
        if (r > 22f) paz(c, r * 0.16f, Color.White, r * 0.03f)
    }
}
