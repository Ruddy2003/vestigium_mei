package io.github.ruddy2003.vestigium.ui.escenas

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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Art déco: negro de laca con filete de oro, dos vueltas de perlas y una pajarita de coral
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/deco_*`, `herramientas/gato/gato20d.py`, rama «deco» y `pintar_laca`):
// negro liso, sin rayas (#1C1A17), de pecho apenas más claro (#34302A), ojos de jade (#3E9C7E; de noche #5FB59A) y
// nariz de oro (#A8801E). Es «laca negra con incrustación de oro»: el canto de afuera es un filete de oro (aquí,
// `Pelaje.filo`, por arriba y del lado de la luz) y sobre el lomo corren líneas de oro en escalón. Lleva dos vueltas de
// perlas marfil (#F6F0E2) y una pajarita de coral (#B5523B, el nudo #7A2E20) bajo la barbilla. Sus dos acciones
// propias, las mismas que el de pixel (`o_trompeta`, `o_abanico`): toca la trompeta (latón #D9B44A, sombra #8A6A1E,
// boquilla #2A2010; las notas que suben, de jade #1F6B57, de noche #5FB59A) y se abanica con plumas (seis plumas de
// avestruz alternas de jade #1F6B57 y #8CC7AE, de noche #5FB59A y #BFE6D4, sobre varillas de oro #D9B44A).
//
// El negro en el sótano de noche se perdería (ESCENAS.md, regla 8): el filete de oro es su filo de luz cálida.

private val D_MANTO = Color(0xFF1C1A17)
private val D_PECHO = Color(0xFF34302A)
private val D_SOMBRA = Color(0xFF100F0D)
private val D_OJOS = Color(0xFF3E9C7E)
private val D_ORO = Color(0xFFA8801E)
private val D_ORO_NOCHE = Color(0xFFD9B44A)
private val D_ORO_CLARO = Color(0xFFF2D27A)
private val D_PERLA = Color(0xFFF6F0E2)
private val D_PERLA_SOMBRA = Color(0xFFBDB29A)
private val D_CORAL = Color(0xFFB5523B)
private val D_CORAL_LUZ = Color(0xFFE48A6E)
private val D_CORAL_OSC = Color(0xFF7A2E20)
private val D_LATON = Color(0xFFD9B44A)
private val D_LATON_OSC = Color(0xFF8A6A1E)
private val D_BOQUILLA = Color(0xFF2A2010)
private val D_JADE = Color(0xFF1F6B57)
private val D_JADE_NOCHE = Color(0xFF5FB59A)
private val D_JADE_CLARO = Color(0xFF8CC7AE)
private val D_JADE_CLARO_NOCHE = Color(0xFFBFE6D4)

private fun unidadD(v: Offset, siNo: Offset = Offset(1f, 0f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** Un color de cosa que da o devuelve luz (oro, perlas, plumas): la luz del sitio, pero sin hundirse del todo. */
private fun LuzGato.vivo(c: Color, cuanto: Float = 0.3f): Color = lerp(tono(c), c, cuanto)

/** Una puntita de la mano (negra, con el filo de oro arriba) encima de lo que sostiene, para que se vea que lo agarra. */
private fun DrawScope.dedosDeco(luz: LuzGato, en: Offset, r: Float = 1.25f) {
    drawOval(luz.tono(D_MANTO), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f))
    drawArc(luz.vivo(D_ORO_NOCHE, 0.35f), 200f, 140f, false, Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f), style = Stroke(0.28f, cap = StrokeCap.Round))
    drawLine(luz.tono(D_SOMBRA), en + Offset(-0.1f, -r * 0.5f), en + Offset(0.1f, r * 0.5f), 0.2f)
}

/**
 * Lo que lleva puesto: las líneas de oro en escalón del lomo (la incrustación de la laca), las dos vueltas de perlas y
 * la pajarita de coral bajo la barbilla.
 */
private val puestoDeco: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    val oro = luz.vivo(lerp(D_ORO, D_ORO_NOCHE, luz.noche), 0.3f)
    // ---- La incrustación de la laca: trazos cortos de oro en escalón (zigzag), siempre a lo ancho, repartidos por el
    // tronco a tresbolillo, como las filas de líneas del de pixel. De espaldas se ven igual; de frente, menos.
    run {
        val dir = unidadD(p.hombro - p.cadera); val arriba = Offset(dir.y, -dir.x).let { if (it.y > 0f) it * -1f else it }
        val alfa = 0.8f - 0.35f * co.coerceAtLeast(0f)
        for ((t, h) in listOf(0.22f to 0.25f, 0.42f to -0.3f, 0.5f to 0.55f, 0.66f to 0.05f, 0.82f to 0.45f, 0.3f to -0.6f)) {
            val r = Gatuno.R_GRUPA + (Gatuno.R_PECHO - Gatuno.R_GRUPA) * t
            val c = p.cadera + (p.hombro - p.cadera) * t + arriba * (h * r)
            val zz = Path().apply {
                moveTo(c.x - 1.6f, c.y)
                for (i in 1..4) lineTo(c.x - 1.6f + 0.8f * i, c.y + if (i % 2 == 1) -0.55f else 0f)
            }
            drawPath(zz, oro.copy(alpha = alfa), style = Stroke(0.28f, cap = StrokeCap.Round))
        }
    }
    if (p.giro >= -0.25f) {
        val u = unidadD(p.cabeza - p.hombro, Offset(0f, -1f)); val n = Offset(-u.y, u.x)
        val a = p.cuello - u * 1.3f
        val perla = luz.vivo(D_PERLA, 0.25f); val perlaSombra = luz.tono(D_PERLA_SOMBRA); val brillo = Color.White.copy(alpha = 0.75f * (1f - 0.4f * luz.noche))
        fun cuenta(c: Offset, r: Float) {
            drawCircle(perlaSombra, r, c)
            drawCircle(perla, r * 0.78f, c + Offset(-0.12f * lado, -0.12f))
            drawCircle(brillo, r * 0.3f, c + Offset(-0.25f * lado * r, -0.3f * r))
        }
        // ---- Las perlas: la primera vuelta ciñe el cuello; la segunda cuelga más abajo, en curva, hacia el pecho.
        val ancho = 2.9f
        for (k in 0..6) { val t = -1f + k / 3f; cuenta(a + n * (ancho * t) - u * (0.35f * (1f - t * t)), 0.56f) }
        for (k in 0..5) { val t = -1f + (k + 0.5f) / 3f; cuenta(a + n * (ancho * 0.95f * t) - u * (1.5f + 1.3f * (1f - t * t)) + Offset(0.5f * s, 0f), 0.5f) }
        // ---- La pajarita de coral, bajo la barbilla: dos lazos y el nudo. De perfil se ve de canto, más estrecha.
        if (co > -0.1f) {
            val c = a + Offset(2.6f * s, 0.6f) + u * 0.6f
            val w = 1.1f + 1.4f * abs(co) + 0.2f
            val coral = luz.vivo(D_CORAL, 0.2f); val coralLuz = luz.vivo(D_CORAL_LUZ, 0.2f); val coralOsc = luz.tono(D_CORAL_OSC)
            for (l in listOf(-1f, 1f)) {
                val lazo = Path().apply {
                    moveTo(c.x, c.y)
                    cubicTo(c.x + l * w * 0.5f, c.y - 1.9f, c.x + l * w, c.y - 1.9f, c.x + l * w * 1.05f, c.y - 1.2f)
                    lineTo(c.x + l * w * 1.08f, c.y + 1.3f)
                    cubicTo(c.x + l * w, c.y + 1.9f, c.x + l * w * 0.5f, c.y + 1.8f, c.x, c.y)
                    close()
                }
                drawPath(lazo, if (l == lado) coralLuz else coral)
                drawLine(coralOsc.copy(alpha = 0.7f), Offset(c.x + l * w * 0.35f, c.y - 0.3f), Offset(c.x + l * w * 0.9f, c.y - 0.6f), 0.25f)
            }
            drawOval(coralOsc, Offset(c.x - 0.6f, c.y - 0.75f), Size(1.2f, 1.5f))
            drawOval(coral, Offset(c.x - 0.38f, c.y - 0.55f), Size(0.7f, 0.9f))
        }
    }
}

// ---- Toca la trompeta (sentado).

private val SOPLA = listOf(0f, 0.4f, 1f, 1f, 0.8f, 0.3f)

private fun poseTrompeta(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val sopla = SOPLA[q]
    // Se la lleva a la boca, alza el hocico al tocar la nota alta (con los ojos cerrados) y la va bajando.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f + 0.3f * sopla, -26.4f - 0.5f * sopla), morro = 0.05f - 0.22f * sopla, orejas = if (q == 3) 0.85f else 1f,
        ojos = if (sopla > 0.9f) 0.15f else 1f,
        manoC = Offset(13.6f + 0.4f * sopla, -21.2f - 1.4f * sopla), manoL = Offset(10.4f + 0.3f * sopla, -18.6f - 1f * sopla),
        cola2 = listOf(2.3f, 2.5f, 2.8f, 2.9f, 2.6f, 2.4f)[q],
    )
}

/** La trompeta de latón: boquilla, tubo con su vuelta, tres pistones, la campana y dos notas de jade que suben. */
private val trompeta: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val sopla = SOPLA[q]
    val laton = luz.vivo(D_LATON, 0.3f); val latonOsc = luz.tono(D_LATON_OSC); val claro = luz.vivo(D_ORO_CLARO, 0.35f)
    // La boquilla, en la boca; apunta al frente y un poco hacia arriba (más al soplar fuerte).
    val a = p.cabeza + Offset(5.2f, 3.1f - 0.4f * sopla)
    val d = unidadD(Offset(1f, -0.24f - 0.14f * sopla)); val n = Offset(d.y, -d.x)            // n: hacia arriba
    val largo = 15.5f
    fun en(t: Float, h: Float = 0f) = a + d * (largo * t) + n * h
    // La vuelta del tubo, por debajo (la parte de atrás del instrumento), un poco más oscura.
    val vuelta = Path().apply {
        val i = en(0.2f); val f = en(0.58f)
        moveTo(i.x, i.y)
        val c1 = en(0.16f, -2.6f); val c2 = en(0.62f, -2.6f)
        cubicTo(c1.x, c1.y, c2.x, c2.y, f.x, f.y)
    }
    drawPath(vuelta, latonOsc, style = Stroke(1.5f, cap = StrokeCap.Round))
    drawPath(vuelta, laton, style = Stroke(0.8f, cap = StrokeCap.Round))
    // Los tres pistones, que suben del bloque de válvulas; el que se pisa, más hundido.
    for ((j, t) in listOf(0.34f, 0.42f, 0.5f).withIndex()) {
        val hundido = if ((q + j) % 3 == 0) 0.5f else 0f
        drawLine(latonOsc, en(t, 0.4f), en(t, 2.7f - hundido), 0.9f, StrokeCap.Round)
        drawLine(laton, en(t, 0.4f), en(t, 2.6f - hundido), 0.55f, StrokeCap.Round)
        drawCircle(claro, 0.55f, en(t, 2.9f - hundido))
    }
    // El bloque de válvulas.
    drawLine(latonOsc, en(0.3f, -0.1f), en(0.54f, -0.1f), 2.1f, StrokeCap.Round)
    drawLine(laton, en(0.3f, 0.1f), en(0.54f, 0.1f), 1.5f, StrokeCap.Round)
    // El tubo, de la boquilla a la campana.
    drawLine(latonOsc, en(0.02f), en(0.74f), 1.25f, StrokeCap.Round)
    drawLine(laton, en(0.02f, 0.1f), en(0.74f, 0.1f), 0.8f, StrokeCap.Round)
    drawLine(claro.copy(alpha = 0.8f), en(0.08f, 0.35f), en(0.7f, 0.35f), 0.3f, StrokeCap.Round)
    // La campana: se abre del tubo a la boca.
    val campana = Path().apply {
        val p0 = en(0.7f, 0.6f); val p1 = en(1f, 3.3f); val p2 = en(1f, -3.3f); val p3 = en(0.7f, -0.6f)
        val c1 = en(0.86f, 0.8f); val c2 = en(0.95f, 2.4f); val c3 = en(0.95f, -2.4f); val c4 = en(0.86f, -0.8f)
        moveTo(p0.x, p0.y); cubicTo(c1.x, c1.y, c2.x, c2.y, p1.x, p1.y); lineTo(p2.x, p2.y); cubicTo(c3.x, c3.y, c4.x, c4.y, p3.x, p3.y); close()
    }
    drawPath(campana, Brush.linearGradient(listOf(claro, laton, latonOsc), en(0.85f, 2.5f), en(0.85f, -2.5f)))
    // La boca de la campana, vista de lado: un óvalo estrecho con su borde y lo oscuro de dentro.
    val boca = en(1f)
    val rx = 0.9f; val ry = 3.4f
    val ang = Math.toDegrees(atan2(d.y.toDouble(), d.x.toDouble())).toFloat()
    rotate(ang, boca) {
        drawOval(laton, Offset(boca.x - rx, boca.y - ry), Size(rx * 2f, ry * 2f))
        drawOval(luz.tono(D_BOQUILLA), Offset(boca.x - rx * 0.45f, boca.y - ry * 0.78f), Size(rx * 0.9f, ry * 1.56f))
        drawOval(claro, Offset(boca.x - rx, boca.y - ry), Size(rx * 2f, ry * 2f), style = Stroke(0.3f))
    }
    // La boquilla oscura, contra los labios.
    drawCircle(luz.tono(D_BOQUILLA), 0.75f, a + d * 0.3f)
    // Las manos: una en los pistones, la otra sujeta el tubo de abajo.
    dedosDeco(luz, en(0.42f, 2.2f), 1.15f)
    dedosDeco(luz, en(0.3f, -1.6f), 1.05f)
    // Las notas: dos corcheas de jade que salen de la campana, suben y se desvanecen.
    if (sopla > 0.2f) {
        val jade = luz.vivo(lerp(D_JADE, D_JADE_NOCHE, luz.noche), 0.55f)
        for (j in 0..1) {
            val f = ((q + j * 3) % 6) / 6f
            val nota = boca + Offset(2.2f + 3.2f * f + 1.6f * j, -2.6f - 7f * f - 1.2f * j)
            val alfa = (1f - f * 0.8f) * sopla
            drawOval(jade.copy(alpha = alfa), Offset(nota.x - 0.95f, nota.y - 0.65f), Size(1.9f, 1.3f))
            drawLine(jade.copy(alpha = alfa), nota + Offset(0.8f, -0.1f), nota + Offset(0.8f, -3f), 0.35f, StrokeCap.Round)
            drawLine(jade.copy(alpha = alfa), nota + Offset(0.8f, -3f), nota + Offset(1.9f, -2.1f), 0.4f, StrokeCap.Round)
        }
    }
}

// ---- Se abanica con plumas (sentado).

private val VAIVEN = listOf(0f, 9f, 16f, 9f, 0f, -7f)

private fun poseAbanico(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Ladea la cabeza y entorna los ojos, con el abanico delante del pecho, que va y viene.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.4f - 0.2f * (VAIVEN[q] / 16f), -25.8f), morro = 0.18f, orejas = 0.95f,
        ojos = if (q == 2) 0.2f else 0.55f, cara = 0.35f,
        manoC = Offset(9.6f + 0.6f * (VAIVEN[q] / 16f), -11.6f - 0.8f * (VAIVEN[q] / 16f)), manoL = Offset(6.5f, 0f),
        cola2 = listOf(2.3f, 2.4f, 2.5f, 2.4f, 2.3f, 2.2f)[q],
    )
}

/** El abanico: seis plumas de avestruz alternas sobre varillas de oro, que salen de la mano y se mecen. */
private val abanico: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val eje = p.manoC + Offset(0.6f, -0.6f)
    val ang = VAIVEN[q]
    val jade = luz.vivo(lerp(D_JADE, D_JADE_NOCHE, luz.noche), 0.3f)
    val jadeClaro = luz.vivo(lerp(D_JADE_CLARO, D_JADE_CLARO_NOCHE, luz.noche), 0.3f)
    val varilla = luz.vivo(D_LATON, 0.3f)
    for (j in 0 until 6) {
        val a = Math.toRadians((-112f + j * 17f + ang).toDouble()).toFloat()
        val u = Offset(cos(a), sin(a)); val w = Offset(-u.y, u.x)
        val col = if (j % 2 == 0) jade else jadeClaro
        val base = eje + u * 2.6f; val punta = eje + u * 15.5f
        // La pluma: ancha y suelta, que se curva un poco en la punta, con su borde deshilachado.
        val pluma = Path().apply {
            moveTo(base.x, base.y)
            val c1 = eje + u * 7f + w * 2.6f; val c2 = eje + u * 13.5f + w * 2.9f
            cubicTo(c1.x, c1.y, c2.x, c2.y, punta.x + w.x * 0.8f, punta.y + w.y * 0.8f)
            val c3 = eje + u * 13.5f - w * 2.4f; val c4 = eje + u * 7f - w * 2.2f
            cubicTo(c3.x, c3.y, c4.x, c4.y, base.x, base.y)
            close()
        }
        drawPath(pluma, col)
        // Las barbas sueltas por los dos bordes: trazos cortos que salen hacia afuera.
        for ((k, ancho) in listOf(1.75f, 2.05f, 2.15f, 2.05f, 1.75f).withIndex()) {
            val t = 7.6f + k * 1.6f
            for (l in listOf(-1f, 1f)) {
                val o = eje + u * t + w * (l * (ancho - 0.25f))
                drawLine(col, o, o + w * (l * 0.8f) + u * 0.8f, 0.35f, StrokeCap.Round)
            }
        }
        drawPath(pluma, luz.tono(D_JADE).copy(alpha = if (j % 2 == 0) 0f else 0.35f), style = Stroke(0.2f))
        // La varilla de oro, del eje hasta media pluma.
        drawLine(varilla, eje, eje + u * 11f, 0.35f, StrokeCap.Round)
    }
    // El clavillo del eje y la mano que lo sostiene.
    drawCircle(luz.vivo(D_ORO_CLARO, 0.35f), 0.7f, eje)
    dedosDeco(luz, eje + Offset(-0.2f, 0.4f), 1.2f)
}

/** El gato de Art déco. */
internal val PELAJE_DECO = Pelaje(
    manto = D_MANTO, claro = D_PECHO, marca = D_SOMBRA, dibujo = DibujoPelaje.LISO, ojos = D_OJOS,
    nariz = D_ORO, accesorio = puestoDeco, filo = D_ORO_NOCHE,
    especiales = listOf(EspecialGato("toca la trompeta", true, 6, ::poseTrompeta, trompeta), EspecialGato("se abanica con plumas", true, 6, ::poseAbanico, abanico)),
)
