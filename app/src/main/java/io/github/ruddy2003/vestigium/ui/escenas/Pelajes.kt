package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Los pelajes del gato del cuadro (plan de historia y escenas, sección 3.4; tanda P5b, 12 nov 2026)
// =====================================================================
//
// El esqueleto, las 13 animaciones y la forma de portarse del gato son de todos los temas (`GatoEsqueleto.kt`,
// `GatoDibujo.kt`, `GatoCuadro.kt`). Aquí va lo que es de cada uno: sus colores y su dibujo, lo que lleva puesto y sus
// dos acciones propias. Todo sale del gato de pixel de ese tema (`assets/gato`), para que al cambiar de gato siga
// siendo el mismo: el de Helénico, crema con su clámide púrpura; el de Gótico, azul noche con su collar y su medallón;
// el de Ciudad neón, negro con su chaqueta de lluvia.
//
// Lo que lleva puesto y lo que tiene en las manos se pinta encima del gato ya terminado, en sus mismas medidas (las
// del gato de talla 1, que mira a la derecha y pisa en (0, 0)), con la luz de la escena. Lo que da luz propia (una
// llama, una estrella, las luces de un dron) se pinta tal cual.

/**
 * El pelaje del gato de cada tema, o null si el tema no tiene cuadro (Terminal, Navidad y Básico) o todavía no le
 * llega: va siempre junto con su escena ([escenaDe]).
 */
fun pelajeDe(tema: TemaId): Pelaje? = when (tema) {
    TemaId.ROMANO -> null           // S1
    TemaId.URBE -> null             // S5
    TemaId.CIBER -> null            // Terminal: sin cuadro
    TemaId.NEON -> PELAJE_NEON
    TemaId.HELENO -> PELAJE_HELENO
    TemaId.EGIPTO -> null           // S1
    TemaId.MEDIEVAL -> null         // S2
    TemaId.TEMPLARIO -> null        // S2
    TemaId.DISTOPIA -> null         // S6
    TemaId.REFUGIO -> null          // S6
    TemaId.ORDEN -> null            // S7
    TemaId.VAPOR -> null            // S4
    TemaId.LABORATORIO -> null      // S7
    TemaId.COLONOS -> null          // S7
    TemaId.NAPOLEON -> null         // S4
    TemaId.DECO -> null             // S5
    TemaId.BOTANICO -> null         // S4
    TemaId.RUINAS -> null           // S6
    TemaId.NAVIDAD -> null          // Navidad: sin cuadro
    TemaId.SEDA -> null             // S2
    TemaId.EDO -> null              // S3
    TemaId.GOTICO -> PELAJE_GOTICO
    TemaId.ARCANO -> null           // S7
    TemaId.ESPACIO -> null          // S6
    TemaId.ABISMO -> null           // S5
    TemaId.ANDALUS -> null          // S2
    TemaId.MESO -> null             // S1
    TemaId.MONGOL -> null           // S3
    TemaId.RENACIMIENTO -> null     // S3
    TemaId.PIRATA -> null           // S3
    TemaId.URSS -> null             // S5
    TemaId.REVOLUCION -> null       // S4
    TemaId.ANTIGUAS -> null         // S7
    TemaId.CUEVA -> null            // S1
    TemaId.BASICO -> null           // Básico: sin cuadro
}

// ---------------------------------------------------------------- Piezas para vestir al gato

private fun largo(v: Offset) = sqrt(v.x * v.x + v.y * v.y)
private fun unidad(v: Offset, siNo: Offset = Offset(1f, 0f)) = largo(v).let { if (it < 0.001f) siNo else v / it }

/** Una forma cerrada y sin esquinas que pasa cerca de los [puntos]. */
private fun redonda(puntos: List<Offset>): Path = Path().apply {
    val n = puntos.size
    fun medio(i: Int) = (puntos[Math.floorMod(i, n)] + puntos[Math.floorMod(i + 1, n)]) / 2f
    moveTo(medio(-1))
    for (i in 0 until n) { val a = medio(i - 1); val c = puntos[i]; val b = medio(i); cubicTo(a + (c - a) * 0.67f, b + (c - b) * 0.67f, b) }
    close()
}

/** Una línea suave por los [puntos], para un ribete o un pliegue. */
private fun trazo(puntos: List<Offset>): Path = Path().apply {
    moveTo(puntos.first())
    for (i in 1 until puntos.size - 1) { val a = (puntos[i - 1] + puntos[i]) / 2f; val b = (puntos[i] + puntos[i + 1]) / 2f; if (i == 1) lineTo(a); cubicTo(a + (puntos[i] - a) * 0.67f, b + (puntos[i] - b) * 0.67f, b) }
    lineTo(puntos.last())
}

/**
 * El tronco del gato en una postura, para ponerle algo encima. [en] da el punto que está a [t] del camino de la
 * cadera (0) al hombro (1) y a [h] del lomo: 1 es la línea del lomo, 0 el medio del cuerpo y −1 la del vientre.
 * También dice desde dónde se le ve ([s]: cuánto de perfil; [co]: cuánto de frente, o de espaldas si es negativo).
 */
private class Tronco(val p: PuntosGato) {
    private val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val deEspaldas = p.giro < -0.75f; val deFrente = p.giro > 0.75f
    private val medio = p.lomo + Offset(0f, 3.9f)
    val dir = unidad(p.hombro - p.cadera)
    /** Hacia el lomo. */
    val arriba = Offset(dir.y, -dir.x)
    /** El cuello: hacia dónde sube y cuál es su lado de la nuca. */
    val cuello = unidad(p.cabeza - p.hombro, Offset(0f, -1f)); val nuca = Offset(cuello.y, -cuello.x)
    fun centro(t: Float): Offset = if (t < 0.5f) p.cadera + (medio - p.cadera) * (t * 2f) else medio + (p.hombro - medio) * ((t - 0.5f) * 2f)
    fun radio(t: Float) = if (t < 0.5f) Gatuno.R_GRUPA + (3.9f - Gatuno.R_GRUPA) * t * 2f else 3.9f + (Gatuno.R_PECHO - 3.9f) * (t - 0.5f) * 2f
    fun en(t: Float, h: Float): Offset = centro(t) + arriba * (h * radio(t))
    /** De dónde nace la mano de este lado (la que se ve). */
    val raizMano = p.hombro + Offset(1.2f * s - 2.1f * co, 0.6f)
    /** De dónde nace la cola, para no pintarle nada encima cuando pasa por delante. */
    val raizCola = p.cadera + Offset(-4f * s, -1.4f - 2.2f * abs(co))
}

/**
 * Pinta [bloque] sin tapar lo que queda por delante de la ropa: la cabeza (salvo de espaldas, que queda detrás del
 * cuerpo) y, de espaldas, la cola en alto.
 */
private inline fun DrawScope.debajoDeLaCabeza(k: Tronco, crossinline bloque: DrawScope.() -> Unit) {
    val p = k.p
    if (p.giro >= -0.25f) {
        clipPath(Path().apply { addOval(Rect(p.cabeza, Gatuno.R_CABEZA + 0.2f)) }, ClipOp.Difference) { bloque() }
        return
    }
    val v = p.puntaCola - k.raizCola
    if (largo(v) < 2f) { bloque(); return }
    val n = unidad(v).let { Offset(-it.y, it.x) } * 2.6f
    clipPath(Path().apply { moveTo(k.raizCola + n); lineTo(p.puntaCola + n); lineTo(p.puntaCola - n); lineTo(k.raizCola - n); close() }, ClipOp.Difference) { bloque() }
}

/** Una estrella de cinco puntas con centro en [c]. */
private fun estrella(c: Offset, radio: Float, giro: Float = 0f): Path = Path().apply {
    for (i in 0 until 10) {
        val a = giro - PI.toFloat() / 2f + i * PI.toFloat() / 5f; val r = if (i % 2 == 0) radio else radio * 0.42f
        val q = Offset(c.x + r * cos(a), c.y + r * sin(a)); if (i == 0) moveTo(q) else lineTo(q)
    }
    close()
}

// =====================================================================
//  Helénico: crema, de pecho blanco y ojos azules, con una clámide púrpura
// =====================================================================

private val H_MANTO = Color(0xFFC89E68)
private val H_PURPURA = Color(0xFF5E2C86)
private val H_PURPURA_LUZ = Color(0xFF7C48A6)
private val H_PURPURA_SOMBRA = Color(0xFF3C1C5A)
private val H_ORO = Color(0xFFD2AC4E)
private val H_MADERA = Color(0xFF8A5A2E)
private val H_MADERA_LUZ = Color(0xFFC9A04A)

/**
 * La clámide: un manto corto de lana púrpura, con ribete de oro, echado sobre los hombros y el lomo y prendido con una
 * fíbula en el pecho. De espaldas se le ve caer por el lomo; de frente, asomar a los dos lados del pecho.
 */
private val clamide: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val k = Tronco(p)
    val tela = luz.tono(H_PURPURA); val clara = luz.tono(H_PURPURA_LUZ); val honda = luz.tono(H_PURPURA_SOMBRA); val oro = luz.tono(H_ORO)
    debajoDeLaCabeza(k) {
        when {
            k.deEspaldas -> {
                val x = (p.hombro.x + p.cadera.x) / 2f
                val y0 = p.hombro.y - Gatuno.R_PECHO - 0.5f; val y1 = p.hombro.y + maxOf(1.4f, (p.cadera.y - p.hombro.y) * 0.62f)
                val forma = redonda(listOf(Offset(x - 4.4f, y0), Offset(x + 4.4f, y0), Offset(x + 5.6f, y1), Offset(x + 2f, y1 + 0.9f), Offset(x - 2f, y1 + 0.9f), Offset(x - 5.6f, y1)))
                drawPath(forma, tela)
                clipPath(forma) { drawRect(clara, Offset(x - 6f, y0 - 1f), Size(12f, (y1 - y0) * 0.42f + 1f)) }
                drawPath(trazo(listOf(Offset(x - 5.2f, y1 - 0.4f), Offset(x - 2f, y1 + 0.5f), Offset(x + 2f, y1 + 0.5f), Offset(x + 5.2f, y1 - 0.4f))), oro, style = Stroke(0.7f, cap = StrokeCap.Round))
            }
            k.deFrente -> {
                for (lado in listOf(-1f, 1f)) {
                    val x = p.hombro.x + 3.9f * lado
                    val forma = redonda(listOf(Offset(x - 1.6f * lado, p.hombro.y - 3.6f), Offset(x + 1.9f * lado, p.hombro.y - 2.6f), Offset(x + 2.2f * lado, p.hombro.y + 4.4f), Offset(x - 0.8f * lado, p.hombro.y + 5f)))
                    drawPath(forma, if (lado < 0f) tela else honda)
                    drawLine(oro, Offset(x + 2.1f * lado, p.hombro.y + 4.2f), Offset(x - 0.7f * lado, p.hombro.y + 4.8f), 0.7f, StrokeCap.Round)
                }
                drawCircle(oro, 1.1f, Offset(p.hombro.x, p.hombro.y + 1.2f))
            }
            else -> {
                // De perfil o de tres cuartos: del medio del lomo a los hombros, y por delante hasta el pecho.
                val nuca = p.cuello + k.nuca * 3.3f; val gorja = p.cuello - k.nuca * 2.9f
                val broche = k.en(1f, 0.05f) + k.dir * 4f
                val caida = listOf(broche, k.en(0.9f, -0.78f), k.en(0.72f, -0.42f), k.en(0.56f, -0.76f), k.en(0.4f, -0.2f), k.en(0.3f, 0.55f))
                val forma = redonda(listOf(k.en(0.3f, 1.1f), k.en(0.66f, 1.16f), k.en(1f, 1.12f), nuca, gorja) + caida)
                drawPath(forma, tela)
                // El lomo recibe la luz; un pliegue baja del hombro.
                clipPath(forma) {
                    drawPath(redonda(listOf(k.en(0.24f, 1.3f), k.en(0.66f, 1.4f), k.en(1.02f, 1.3f), nuca + k.nuca * 1.2f, k.en(1f, 0.5f), k.en(0.62f, 0.42f), k.en(0.3f, 0.6f))), clara)
                    drawPath(trazo(listOf(k.en(0.96f, 0.9f), k.en(0.86f, 0.2f), k.en(0.8f, -0.4f))), honda, style = Stroke(0.6f, cap = StrokeCap.Round))
                }
                drawPath(trazo(caida), oro, style = Stroke(0.75f, cap = StrokeCap.Round))
                drawCircle(oro, 1.15f, broche); drawCircle(luz.tono(Color(0xFFF6E3A0)), 0.45f, broche + Offset(-0.25f, -0.25f))
            }
        }
    }
}

private fun poseLira(c: Int): Pose {
    val q = Math.floorMod(c, 4)
    val mano = listOf(Offset(11.6f, -13.6f), Offset(13f, -11.8f), Offset(14.4f, -13.2f), Offset(13f, -14.8f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5f, -25.6f + 0.3f * (q % 2)), morro = listOf(0.14f, 0.2f, 0.14f, 0.08f)[q], ojos = listOf(1f, 0.55f, 0.18f, 0.55f)[q], orejas = 0.9f,
        manoC = mano, manoL = Offset(16.6f, -15f), cola2 = listOf(2.3f, 2.65f, 2.3f, 1.95f)[q],
    )
}

/** La lira: la caja de concha, los dos brazos, el yugo y las cuerdas; y la mano que las pulsa, por delante. */
private val lira: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val madera = luz.tono(H_MADERA); val clara = luz.tono(H_MADERA_LUZ); val oro = luz.tono(H_ORO)
    val pie = Offset(13.6f, -0.4f)
    // Los brazos, curvos, y el yugo.
    for (lado in listOf(-1f, 1f)) drawPath(trazo(listOf(pie + Offset(2.4f * lado, -6f), pie + Offset(5f * lado, -12.4f), pie + Offset(3.3f * lado, -20f))), madera, style = Stroke(1.5f, cap = StrokeCap.Round))
    drawLine(clara, pie + Offset(-4.8f, -19.4f), pie + Offset(4.8f, -19.4f), 1.3f, StrokeCap.Round)
    // Las cuerdas: la que acaba de pulsar vibra.
    val cuerdas = luz.tono(Color(0xFFF2E6B0))
    for (i in 0..3) { val x = pie.x - 2.4f + i * 1.6f; val tiembla = if (i == Math.floorMod(c, 4)) 0.45f else 0f; drawLine(cuerdas.copy(alpha = 0.9f), Offset(x + tiembla, pie.y - 19.2f), Offset(x, pie.y - 5.6f), 0.36f) }
    // La caja.
    drawOval(madera, Offset(pie.x - 4.5f, pie.y - 7.4f), Size(9f, 7.4f))
    drawOval(clara, Offset(pie.x - 3.2f, pie.y - 6.8f), Size(5.8f, 3.4f), alpha = 0.55f)
    drawLine(oro, pie + Offset(-2.5f, -5.5f), pie + Offset(2.5f, -5.5f), 0.6f, StrokeCap.Round)
    // La mano, por delante de las cuerdas.
    drawCircle(luz.tono(Color(0xFFF8F4EA)), 1.5f, p.manoC + Offset(0.5f, -0.6f))
}

private fun poseAntorcha(c: Int): Pose {
    val q = Math.floorMod(c, 4)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.2f, -25.8f), morro = listOf(-0.2f, -0.28f, -0.2f, -0.14f)[q], orejas = 1f,
        manoC = Offset(13.6f, -26.6f + listOf(0f, -0.9f, 0f, 0.7f)[q]), cola2 = listOf(2.3f, 2.5f, 2.7f, 2.5f)[q],
    )
}

/** La antorcha: el mango, la copa de bronce y la llama, que alumbra. */
private val antorcha: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val m = p.manoC + Offset(0.4f, -1f)
    val abajo = m + Offset(-1.1f, 4.2f); val arriba = m + Offset(1.1f, -5.2f)
    drawLine(luz.tono(H_MADERA), abajo, arriba, 1.3f, StrokeCap.Round)
    drawPath(Path().apply { moveTo(arriba + Offset(-1.9f, -1.6f)); lineTo(arriba + Offset(1.9f, -1.6f)); lineTo(arriba + Offset(0.9f, 0.8f)); lineTo(arriba + Offset(-0.9f, 0.8f)); close() }, luz.tono(Color(0xFFB07A3A)))
    val reloj = 0.9f + c * 0.31f
    resplandor(arriba + Offset(0f, -4.5f), 13f, 0.3f + 0.45f * luz.noche)
    llama(arriba + Offset(0f, -1.4f), 7f, reloj, 3)
    drawCircle(luz.tono(Color(0xFFF8F4EA)), 1.5f, m)
}

private val PELAJE_HELENO = Pelaje(
    manto = H_MANTO, claro = Color(0xFFF8F4EA), marca = Color(0xFF94703E), dibujo = DibujoPelaje.BICOLOR, ojos = Color(0xFF2A78B4),
    nariz = Color(0xFFE0A0A0), accesorio = clamide,
    especiales = listOf(EspecialGato("toca la lira", true, 4, ::poseLira, lira), EspecialGato("alza la antorcha", true, 4, ::poseAntorcha, antorcha)),
)

// =====================================================================
//  Gótico: azul noche, de ojos dorados, con collar rojo y un medallón
// =====================================================================

private val G_ORO = Color(0xFFE2B34A)
private val G_ROJO = Color(0xFFB3122E)

/**
 * El collar rojo y el medallón que cuelga de él: un rosetón de oro con el centro rojo. El collar se pinta aquí, y no
 * con el del pelaje, para que la cabeza lo tape cuando el gato se echa o se asea. De espaldas el medallón no se ve.
 */
private val collarConMedallon: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val k = Tronco(p)
    debajoDeLaCabeza(k) {
        drawLine(luz.tono(Color(0xFF7A0C20)), p.cuello + k.nuca * 3.2f + Offset(0.2f, 0.4f), p.cuello - k.nuca * 3f + Offset(0.2f, 0.4f), 1.5f, StrokeCap.Round)
        drawLine(luz.tono(G_ROJO), p.cuello + k.nuca * 3.2f, p.cuello - k.nuca * 3f, 1.45f, StrokeCap.Round)
        if (p.giro >= -0.25f) {
            // Cuelga por delante del cuello: de perfil, hacia el pecho; de frente, en medio.
            val sitio = p.cuello - k.nuca * (3.5f * k.s) + Offset(0f, 1.3f + 2.4f * k.co.coerceAtLeast(0f))
            drawCircle(luz.tono(Color(0xFF8A6A22)), 1.75f, sitio + Offset(0.15f, 0.25f))
            drawCircle(luz.tono(G_ORO), 1.55f, sitio)
            drawCircle(luz.tono(G_ROJO), 0.6f, sitio)
            drawCircle(luz.tono(Color(0xFFFFF0C0)), 0.32f, sitio + Offset(-0.6f, -0.6f))
        }
    }
}

private fun poseVela(c: Int): Pose {
    val q = Math.floorMod(c, 4)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f, -25.2f + 0.3f * (q % 2)), morro = listOf(0.28f, 0.34f, 0.3f, 0.26f)[q], ojos = listOf(1f, 1f, 0.2f, 1f)[q], orejas = 0.95f,
        manoC = Offset(9.2f, -11.4f), manoL = Offset(10.6f, -11f), cola2 = listOf(2.3f, 2.4f, 2.5f, 2.4f)[q],
    )
}

/** La vela: la palmatoria de oro, el cirio y su llama, que tiembla. */
private val vela: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val base = p.manoC + Offset(1.2f, -1.6f)
    drawOval(luz.tono(Color(0xFF8A6A22)), Offset(base.x - 2.9f, base.y - 0.5f), Size(5.8f, 1.9f))
    drawOval(luz.tono(G_ORO), Offset(base.x - 2.7f, base.y - 0.9f), Size(5.4f, 1.7f))
    drawRoundRect(luz.tono(Color(0xFFF6F0E0)), Offset(base.x - 0.85f, base.y - 5.6f), Size(1.7f, 5.4f), CornerRadius(0.5f))
    drawLine(luz.tono(Color(0xFFD8CDB4)), Offset(base.x + 0.5f, base.y - 5.2f), Offset(base.x + 0.5f, base.y - 0.6f), 0.35f)
    val reloj = 0.4f + c * 0.37f
    resplandor(base + Offset(0f, -7.4f), 10f, 0.28f + 0.5f * luz.noche)
    llama(base + Offset(0f, -5.5f), 3.8f, reloj, 5)
}

private fun poseEstrella(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.2f, -26.2f), morro = listOf(-0.25f, -0.45f, -0.56f, -0.56f, -0.5f, -0.56f)[q], orejas = 1f,
        ojos = if (q == 4) 0.2f else 1f, manoC = if (q == 3 || q == 4) Offset(10.4f, -12.6f) else if (q == 2 || q == 5) Offset(8.4f, -5f) else Gatuno.SENTADO.manoC,
        cola2 = listOf(2.3f, 2.5f, 2.7f, 2.5f, 2.3f, 2.1f)[q],
    )
}

/** La estrella de la bóveda: de oro, en lo alto, con un destello que va y viene. */
private val estrellaDeLaBoveda: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, _, c ->
    val q = Math.floorMod(c, 6)
    val sitio = p.cabeza + Offset(13f, -14.5f)
    val radio = listOf(2.3f, 2.7f, 2.3f, 2.9f, 2.3f, 2.7f)[q]
    resplandor(sitio, radio * 3.4f, 0.5f, Color(0xFFFFD27A))
    drawPath(estrella(sitio, radio, 0.12f * (q % 2)), Color(0xFFE2B34A))
    drawPath(estrella(sitio, radio * 0.55f, 0.12f * (q % 2)), Color(0xFFFFF3C8))
    if (q % 2 == 1) drawCircle(Color(0xFFFFF3C8), 0.5f, sitio + Offset(radio * 1.3f, -radio * 1.1f))
}

private val PELAJE_GOTICO = Pelaje(
    manto = Color(0xFF2E3654), claro = Color(0xFF545C80), marca = Color(0xFF171C30), dibujo = DibujoPelaje.LISO, ojos = G_ORO,
    nariz = Color(0xFF9A5A6A), accesorio = collarConMedallon,
    especiales = listOf(EspecialGato("sostiene una vela", true, 4, ::poseVela, vela), EspecialGato("mira una estrella de la bóveda", true, 6, ::poseEstrella, estrellaDeLaBoveda)),
)

// =====================================================================
//  Ciudad neón: negro, de ojos verdes, con una chaqueta de lluvia con capucha
// =====================================================================

private val N_MANTO = Color(0xFF2A1F36)
private val N_CHAQUETA = Color(0xFF3A5878)
private val N_CHAQUETA_LUZ = Color(0xFF5A84AE)
private val N_CHAQUETA_SOMBRA = Color(0xFF233850)
private val N_MAGENTA = Color(0xFFFF3D9A)
private val N_AMBAR = Color(0xFFFFB238)

/**
 * La chaqueta impermeable: le cubre el lomo y el pecho, con la capucha caída en la nuca, una banda ámbar que refleja
 * la luz, el ribete magenta y la manga en la mano que se ve.
 */
private val chaqueta: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val k = Tronco(p)
    val tela = luz.tono(N_CHAQUETA); val clara = luz.tono(N_CHAQUETA_LUZ); val honda = luz.tono(N_CHAQUETA_SOMBRA)
    // La banda y el ribete devuelven la luz: se apagan menos que la tela.
    val ambar = lerp(luz.tono(N_AMBAR), N_AMBAR, 0.5f); val magenta = lerp(luz.tono(N_MAGENTA), N_MAGENTA, 0.5f)
    debajoDeLaCabeza(k) {
        when {
            k.deEspaldas -> {
                val x = (p.hombro.x + p.cadera.x) / 2f
                val y0 = p.hombro.y - Gatuno.R_PECHO - 0.4f; val y1 = p.hombro.y + maxOf(1.8f, (p.cadera.y - p.hombro.y) * 0.72f)
                val forma = redonda(listOf(Offset(x - 4.7f, y0), Offset(x + 4.7f, y0), Offset(x + 5.6f, y1), Offset(x, y1 + 0.7f), Offset(x - 5.6f, y1)))
                drawPath(forma, tela)
                clipPath(forma) {
                    drawRect(clara, Offset(x - 6f, y0 - 1f), Size(12f, (y1 - y0) * 0.36f + 1f))
                    drawRect(ambar, Offset(x - 6f, y0 + (y1 - y0) * 0.6f), Size(12f, 1.1f))
                }
                drawPath(trazo(listOf(Offset(x - 5.3f, y1 - 0.3f), Offset(x, y1 + 0.4f), Offset(x + 5.3f, y1 - 0.3f))), magenta, style = Stroke(0.6f, cap = StrokeCap.Round))
                // La capucha, caída sobre la nuca.
                drawOval(honda, Offset(x - 4.2f, y0 - 2.8f), Size(8.4f, 5.4f)); drawOval(tela, Offset(x - 3.5f, y0 - 3f), Size(7f, 4.2f))
            }
            k.deFrente -> {
                val forma = redonda(listOf(Offset(p.hombro.x - 4.8f, p.hombro.y - 3f), Offset(p.hombro.x + 4.8f, p.hombro.y - 3f), Offset(p.hombro.x + 5f, p.hombro.y + 4.6f), Offset(p.hombro.x - 5f, p.hombro.y + 4.6f)))
                drawPath(forma, tela)
                clipPath(forma) { drawRect(ambar, Offset(p.hombro.x - 6f, p.hombro.y + 1.6f), Size(12f, 1.1f)) }
                drawLine(magenta, Offset(p.hombro.x, p.hombro.y - 3f), Offset(p.hombro.x, p.hombro.y + 4.4f), 0.5f)
            }
            else -> {
                val nuca = p.cuello + k.nuca * 3.4f; val gorja = p.cuello - k.nuca * 3f
                // La capucha, caída detrás del cuello.
                val capucha = nuca + k.nuca * 1.9f - k.cuello * 1.2f
                drawCircle(honda, 4.1f, capucha); drawCircle(tela, 3.4f, capucha + k.nuca * 0.5f + k.cuello * 0.3f)
                // Le cubre el lomo y los costados, hasta el vientre.
                val bajo = listOf(k.en(1f, -0.3f) + k.dir * 4.3f, k.en(0.98f, -1.02f) + k.dir * 1.6f, k.en(0.68f, -0.94f), k.en(0.42f, -0.9f), k.en(0.22f, -0.4f))
                val forma = redonda(listOf(k.en(0.2f, 1.1f), k.en(0.6f, 1.16f), k.en(1f, 1.12f), nuca, gorja) + bajo)
                drawPath(forma, tela)
                clipPath(forma) {
                    drawPath(redonda(listOf(k.en(0.14f, 1.3f), k.en(0.6f, 1.4f), k.en(1.02f, 1.3f), nuca + k.nuca * 1.2f, k.en(1f, 0.45f), k.en(0.6f, 0.36f), k.en(0.18f, 0.5f))), clara)
                    // La banda que refleja la luz, de lado a lado.
                    drawLine(ambar, k.en(0.5f, 1.3f), k.en(0.46f, -1.2f), 1.25f)
                }
                drawPath(trazo(bajo), magenta, style = Stroke(0.6f, cap = StrokeCap.Round))
                // La manga: del hombro a medio brazo, con su puño.
                val fin = k.raizMano + (p.manoC - k.raizMano) * 0.52f
                drawLine(honda, k.raizMano, fin, 4.6f, StrokeCap.Round); drawLine(tela, k.raizMano + Offset(-0.3f, -0.2f), fin + Offset(-0.3f, -0.2f), 3.7f, StrokeCap.Round)
                val n = unidad(fin - k.raizMano).let { Offset(-it.y, it.x) } * 2.1f
                drawLine(magenta, fin + n, fin - n, 0.6f, StrokeCap.Round)
            }
        }
    }
}

private fun poseFideos(c: Int): Pose {
    val q = Math.floorMod(c, 4)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5f, -25.4f + listOf(0f, 0.2f, 0.6f, 0.2f)[q]), morro = listOf(0.24f, 0.3f, 0.42f, 0.3f)[q], ojos = listOf(1f, 1f, 0.15f, 0.7f)[q], boca = if (q == 2) 0.45f else 0f,
        manoC = listOf(Offset(13f, -16.4f), Offset(12.6f, -18.8f), Offset(12.2f, -20.8f), Offset(12.6f, -18.4f))[q], manoL = Offset(11.4f, -10.6f), cola2 = listOf(2.3f, 2.4f, 2.7f, 2.4f)[q],
    )
}

/** El tazón de fideos, los palillos y los fideos que suben; un poco de vapor. */
private val fideos: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 4)
    val tazon = Offset(12f, -12.4f)
    // Los palillos salen de la mano hacia abajo y hacia la boca; entre sus puntas, los fideos.
    val mano = p.manoC + Offset(0.3f, -0.8f)
    val punta = mano + Offset(-2.8f, listOf(3.4f, 3f, 2f, 3f)[q])
    val pasta = luz.tono(Color(0xFFFFE9A8))
    if (q != 0) for (i in -1..1) drawPath(trazo(listOf(punta + Offset(i * 0.6f, 0f), punta + Offset(i * 1f + 0.6f, (tazon.y - punta.y) * 0.35f), punta + Offset(i * 0.8f - 0.5f, (tazon.y - punta.y) * 0.7f), Offset(tazon.x + i * 1.1f - 0.6f, tazon.y - 1f))), pasta, style = Stroke(0.55f, cap = StrokeCap.Round))
    val vapor = Color.White.copy(alpha = 0.24f)
    for (i in 0..1) drawPath(trazo(listOf(tazon + Offset(-2.4f + i * 4.4f, -3f), tazon + Offset(-1.4f + i * 4.4f, -5.6f - q * 0.3f), tazon + Offset(-2.6f + i * 4.4f, -8.4f - q * 0.4f))), vapor, style = Stroke(0.8f, cap = StrokeCap.Round))
    // El tazón: rojo, con su borde claro y su pie.
    drawPath(Path().apply { moveTo(tazon.x - 4.7f, tazon.y - 2f); cubicTo(tazon.x - 4.2f, tazon.y + 3.6f, tazon.x + 4.2f, tazon.y + 3.6f, tazon.x + 4.7f, tazon.y - 2f); close() }, luz.tono(Color(0xFFC8284A)))
    drawOval(luz.tono(Color(0xFFFFE9A8)), Offset(tazon.x - 4.7f, tazon.y - 3.1f), Size(9.4f, 2.2f))
    drawOval(luz.tono(Color(0xFFE85A70)), Offset(tazon.x - 4.7f, tazon.y - 3.1f), Size(9.4f, 2.2f), style = Stroke(0.6f))
    drawRect(luz.tono(Color(0xFF8E1C36)), Offset(tazon.x - 1.7f, tazon.y + 1.9f), Size(3.4f, 1f))
    val palo = luz.tono(Color(0xFFD8B07A))
    for (d in listOf(0f, 0.9f)) drawLine(palo, mano + Offset(2f + d * 0.3f, -3f + d * 0.2f), punta + Offset(d * 0.5f, d * 0.3f), 0.5f, StrokeCap.Round)
    drawCircle(luz.tono(N_MANTO), 1.5f, mano)
}

private fun poseDron(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.2f, -26.2f), morro = listOf(-0.4f, -0.5f, -0.6f, -0.62f, -0.6f, -0.5f)[q], cara = listOf(0f, 0.16f, 0.4f, 0.62f, 0.4f, 0.16f)[q], orejas = 1f,
        cola2 = listOf(2.3f, 2.5f, 2.7f, 2.5f, 2.3f, 2.1f)[q],
    )
}

/** El dron de reparto: pasa por lo alto con su paquete, con las luces encendidas y las hélices girando. */
private val dron: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val sitio = p.cabeza + Offset(listOf(16f, 12f, 6f, 1.5f, 6f, 12f)[q], listOf(-13f, -15.6f, -17f, -16.2f, -17f, -14.8f)[q])
    resplandor(sitio + Offset(0f, 0.8f), 12f, 0.6f, N_MAGENTA)
    // El paquete, colgado.
    val cuerda = luz.tono(Color(0xFF8A7AA8))
    drawLine(cuerda, sitio + Offset(-1.4f, 1.4f), sitio + Offset(-1.4f, 3.9f), 0.4f); drawLine(cuerda, sitio + Offset(1.4f, 1.4f), sitio + Offset(1.4f, 3.9f), 0.4f)
    drawRect(luz.tono(Color(0xFFD8A060)), sitio + Offset(-2.6f, 3.7f), Size(5.2f, 3.9f)); drawLine(luz.tono(Color(0xFFA87840)), sitio + Offset(-2.6f, 5.6f), sitio + Offset(2.6f, 5.6f), 0.4f)
    // Los brazos y las hélices.
    drawLine(luz.tono(Color(0xFF5A4A7A)), sitio + Offset(-6.6f, -0.6f), sitio + Offset(6.6f, -0.6f), 1f, StrokeCap.Round)
    for (lado in listOf(-1f, 1f)) drawOval(Color(0xFFC8B8F0).copy(alpha = if (q % 2 == 0) 0.6f else 0.34f), Offset(sitio.x + 6.4f * lado - 3.9f, sitio.y - 2.3f), Size(7.8f, 1.5f))
    // El cuerpo y sus luces.
    drawRoundRect(luz.tono(Color(0xFF4A3C66)), sitio + Offset(-3.6f, -1.4f), Size(7.2f, 3.2f), CornerRadius(1.5f))
    drawLine(N_MAGENTA, sitio + Offset(-2.4f, 0.8f), sitio + Offset(2.4f, 0.8f), 0.8f, StrokeCap.Round)
    drawCircle(if (q % 2 == 0) N_AMBAR else Color(0xFFFFE59A), 0.7f, sitio + Offset(0f, -0.4f))
}

private val PELAJE_NEON = Pelaje(
    manto = N_MANTO, claro = Color(0xFF3E2E4C), marca = Color(0xFF0E0A14), dibujo = DibujoPelaje.LISO, ojos = Color(0xFF8CFF5A),
    nariz = Color(0xFFE86AA8), accesorio = chaqueta,
    especiales = listOf(EspecialGato("sorbe fideos con palillos", true, 4, ::poseFideos, fideos), EspecialGato("sigue al dron de reparto", true, 6, ::poseDron, dron)),
)
