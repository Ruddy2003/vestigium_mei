package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private fun rad(g: Double) = Math.toRadians(g).toFloat()
private fun Offset.mas(r: Float, ang: Float) = Offset(x + r * cos(ang), y + r * sin(ang))

/** Cuánto tarda en repetirse la animación de cada emblema, en ms. Cada tema tiene su propio pulso: no hay dos iguales. */
private fun periodo(t: TemaId) = when (t) {
    TemaId.ROMANO -> 6000
    TemaId.URBE -> 8200
    TemaId.CIBER -> 4200
    TemaId.NEON -> 3600
    TemaId.HELENO -> 60000
    TemaId.EGIPTO -> 18000
    TemaId.MEDIEVAL -> 5000
    TemaId.TEMPLARIO -> 4400
    TemaId.DISTOPIA -> 7000
    TemaId.REFUGIO -> 3200
    TemaId.ORDEN -> 2600
    TemaId.VAPOR -> 3200          // el engrane da una doceava de vuelta por ciclo; la aguja tiembla tres veces
    TemaId.LABORATORIO -> 2400
    TemaId.COLONOS -> 1900
    TemaId.NAPOLEON -> 5200
    TemaId.BOTANICO -> 14000
    TemaId.RUINAS -> 9000
    TemaId.NAVIDAD -> 24000
    TemaId.SEDA -> 4800
    TemaId.EDO -> 6400
    TemaId.GOTICO -> 7600
    TemaId.ARCANO -> 8800
    TemaId.ESPACIO -> 10000
    TemaId.ABISMO -> 4200                      // un latido de la medusa
    TemaId.ANDALUS -> 36000
    TemaId.MESO -> 13000                       // el humo del copal que respira detrás del glifo
    TemaId.MONGOL -> 30000
    TemaId.RENACIMIENTO -> 6600
    TemaId.PIRATA -> 18000
    TemaId.URSS -> 12000
    TemaId.REVOLUCION -> 9000
    TemaId.ANTIGUAS -> 16000
    TemaId.CUEVA -> 9000                       // el soplo de ocre respira despacio
}

/**
 * El emblema de cada tema, dibujado (no son imágenes): corona de laurel, ventana de consola, sol de Vergina,
 * rosetón, ojo que todo lo ve, átomo, escudo con circuito, molécula, farol, la N coronada con sus abejas… Se mueven si las animaciones están activas.
 */
@Composable
fun Emblema(tema: TemaId = Estilo.tema, tam: Dp = 30.dp, esquema: ColorScheme = MaterialTheme.colorScheme, animado: Boolean = Repo.config.animaciones,
            modifier: Modifier = Modifier) {
    val t: State<Float> = if (animado) {
        val inf = rememberInfiniteTransition(label = "emblema")
        inf.animateFloat(0f, 1f, infiniteRepeatable(tween(periodo(tema), easing = LinearEasing)), label = "t")
    } else remember { mutableFloatStateOf(0.3f) }
    val oscuro = esquema.background.luminance() < 0.5f
    Canvas(modifier.size(tam).semantics { contentDescription = "emblema ${tema.nombre}" }) {
        dibujarEmblema(tema, t.value, esquema, oscuro)
    }
}

internal fun DrawScope.dibujarEmblema(tema: TemaId, t: Float, e: ColorScheme, oscuro: Boolean) {
    val c = center
    val r = size.minDimension / 2
    when (tema) {
        TemaId.ROMANO -> laurel(c, r, if (oscuro) e.primary else e.tertiary, t)
        TemaId.URBE -> placaLoba(c, r, e.onBackground, e.primary, if (oscuro) e.surfaceVariant else e.surface, t)
        TemaId.CIBER -> consola(c, r, e.primary, e.secondary, t)
        TemaId.NEON -> letreroNeon(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.HELENO -> solVergina(c, r, if (oscuro) e.primary else e.tertiary, if (oscuro) e.tertiary else e.primary, t)
        TemaId.EGIPTO -> ojoUdyat(c, r, if (oscuro) e.primary else e.tertiary, if (oscuro) e.secondary else e.primary, t)
        TemaId.MEDIEVAL -> roseton(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.TEMPLARIO -> escudoTemplario(c, r, e.primary, e.secondary, e.tertiary, if (oscuro) Color(0xFFEEE7D8) else Color.White, t)
        TemaId.DISTOPIA -> ojo(c, r, e.onBackground, e.primary, e.background, t)
        TemaId.REFUGIO -> atomo(c, r, e.primary, e.secondary, t)
        TemaId.ORDEN -> escudo(c, r, e.primary, e.secondary, t)
        TemaId.VAPOR -> engraneManometro(c, r, e.primary, lerp(e.primary, e.onBackground, 0.35f), if (oscuro) Color(0xFFEADCC0) else e.surface, Color(0xFF231B14), e.tertiary, t)
        TemaId.LABORATORIO -> molecula(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.COLONOS -> farol(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.NAPOLEON -> nImperial(c, r, if (oscuro) e.primaryContainer else e.primary, if (oscuro) e.primary else e.tertiary, e.secondary, t)
        TemaId.BOTANICO -> escarabajoLamina(c, r, e.primary, e.secondary, e.tertiary, e.onBackground, t)
        TemaId.RUINAS -> semaforoHiedra(c, r, e.primary, e.secondary, e.tertiary, if (oscuro) Color(0xFF3A443B) else e.onBackground, t)
        TemaId.GOTICO -> vitral(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, if (oscuro) e.tertiary else e.primary, e.onBackground, t)
        TemaId.ARCANO -> signoArcano(c, r, if (oscuro) e.secondary else e.primary, if (oscuro) e.primary else e.tertiary, e.onBackground, e.background, t)
        TemaId.ESPACIO -> planeta(c, r, e.primary, e.secondary, e.tertiary, e.onBackground, t)
        TemaId.ABISMO -> medusa(c, r, e.primary, if (oscuro) e.primary else e.tertiary, e.secondary, t, oscuro)
        TemaId.ANDALUS -> rotate(t * 360f, c) { estrellaLazo(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, e.primary, e.background) }
        TemaId.MESO -> emblemaMaya(c, r, e.primary, if (oscuro) e.surfaceVariant else e.surface, e.onBackground, e.tertiary, t)
        TemaId.MONGOL -> toono(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.RENACIMIENTO -> compas(c, r, e.primary, e.onBackground, e.secondary, t)
        TemaId.PIRATA -> calaveraRosa(c, r, if (oscuro) e.onBackground else e.surface, e.onBackground, e.tertiary, if (oscuro) e.primary else e.secondary, t)
        TemaId.NAVIDAD -> copo(c, r, if (oscuro) e.onBackground else e.secondary, e.tertiary, t)
        TemaId.SEDA -> moneda(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, e.background, t)
        TemaId.EDO -> hanko(c, r, if (oscuro) e.primary else e.secondary, e.background, t)
        TemaId.URSS -> estrellaEngrane(c, r, e.primary, if (oscuro) e.tertiary else e.onBackground, t)
        TemaId.REVOLUCION -> escarapelaEmblema(c, r, e.primary, if (oscuro) Color(0xFFF0EADB) else Color.White, e.secondary, e.onBackground, t)
        TemaId.ANTIGUAS -> gemaRunas(c, r, e.primary, e.secondary, e.tertiary, e.onBackground, t)
        TemaId.CUEVA -> manoEmblema(c, r, e.primary, e.tertiary, e.background, e.onBackground, t)
    }
}

/**
 * Tierras Antiguas: una gema tallada dentro de un anillo de piedra con runas inventadas que gira despacio;
 * un destello recorre las facetas.
 */
private fun DrawScope.gemaRunas(c: Offset, r: Float, verde: Color, oro: Color, rubi: Color, tinta: Color, t: Float) {
    drawCircle(oro, r * 0.96f, c, style = Stroke(r * 0.12f))
    drawCircle(tinta.copy(alpha = 0.25f), r * 0.8f, c, style = Stroke(r * 0.03f))
    // Las runas del anillo: trazos rectos que no son de ningún alfabeto.
    for (i in 0 until 8) {
        val a = rad(t * 360.0 + i * 45.0)
        val o = c.mas(r * 0.96f, a)
        val tg = Offset(-sin(a), cos(a)); val radial = Offset(cos(a), sin(a))
        val l = r * 0.09f
        drawLine(tinta.copy(alpha = 0.55f), o - tg * l, o + tg * l, r * 0.03f)
        when (i % 3) {
            0 -> drawLine(tinta.copy(alpha = 0.55f), o + tg * l, o + radial * (l * 0.8f), r * 0.03f)
            1 -> drawLine(tinta.copy(alpha = 0.55f), o - tg * (l * 0.3f) - radial * (l * 0.6f), o + tg * (l * 0.3f) + radial * (l * 0.6f), r * 0.03f)
            else -> drawLine(tinta.copy(alpha = 0.55f), o, o - radial * (l * 0.8f), r * 0.03f)
        }
    }
    // La gema: un hexágono con su tabla y facetas.
    val g = r * 0.56f
    val pts = (0 until 6).map { c.mas(g, rad(-90.0 + it * 60.0)) }
    val gema = Path().apply { pts.forEachIndexed { i, q -> if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }
    drawPath(gema, Brush.linearGradient(listOf(lerp(verde, Color.White, 0.35f), verde, lerp(verde, Color.Black, 0.35f)), c - Offset(g, g), c + Offset(g, g)))
    val tabla = (0 until 6).map { c.mas(g * 0.5f, rad(-90.0 + it * 60.0)) }
    drawPath(Path().apply { tabla.forEachIndexed { i, q -> if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }, lerp(verde, Color.White, 0.18f))
    for (i in 0 until 6) drawLine(tinta.copy(alpha = 0.25f), pts[i], tabla[i], r * 0.02f)
    drawPath(gema, oro, style = Stroke(r * 0.05f))
    // El destello que recorre las facetas.
    val b = c.mas(g * 0.55f, rad(t * 720.0))
    drawCircle(Color.White.copy(alpha = 0.55f), r * 0.06f, b)
    drawCircle(rubi, r * 0.05f, c.mas(r * 0.96f, rad(t * 360.0 + 22.5)))
}

/**
 * Prehistoria: una mano en negativo, como las de las cuevas. Se apoyaba la mano en la roca y se soplaba ocre alrededor
 * (con la boca o con un hueso hueco): la mano queda del color de la pared, rodeada de la nube de pigmento. Aquí la nube
 * respira despacio y unas gotas titilan, como a la luz de una lámpara de grasa; tres puntos de ocre amarillo al lado.
 */
private fun DrawScope.manoEmblema(c: Offset, r: Float, ocre: Color, amarillo: Color, pared: Color, tinta: Color, t: Float) {
    val laja = Path().apply {
        for (i in 0 until 11) { val q = c.mas(r * (0.9f + 0.07f * sin(i * 2.3f)), rad(-90.0 + i * 360.0 / 11)); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
        close()
    }
    val roca = lerp(pared, tinta, 0.07f)
    drawPath(laja, roca)
    drawPath(laja, tinta.copy(alpha = 0.25f), style = Stroke(r * 0.03f))
    val vuelta = t * 2 * PI.toFloat()
    val respira = 0.82f + 0.18f * sin(vuelta)
    val m = Offset(c.x, c.y + r * 0.04f)
    drawCircle(Brush.radialGradient(listOf(ocre.copy(alpha = 0.9f * respira), ocre.copy(alpha = 0.55f * respira), ocre.copy(alpha = 0f)), m, r * 0.82f), r * 0.82f, m)
    val az = java.util.Random(23)
    repeat(46) { k ->
        val a = az.nextFloat() * 2 * PI.toFloat(); val d = r * (0.5f + az.nextFloat() * 0.38f); val g = r * (0.012f + az.nextFloat() * 0.022f)
        val vivo = 0.5f + 0.5f * sin(vuelta * 3 + k)
        drawCircle(ocre.copy(alpha = (0.35f + 0.4f * vivo) * (1f - (d / r - 0.5f) / 0.45f).coerceIn(0f, 1f)), g, m.mas(d, a))
    }
    piezasMano(Offset(m.x - r * 0.04f, m.y - r * 0.02f), r * 0.6f).forEach { drawPath(it, roca) }
    for (k in 0 until 3) drawCircle(amarillo, r * 0.045f, Offset(c.x + r * 0.64f, c.y - r * 0.4f + k * r * 0.14f))
}

/**
 * La mano de la Prehistoria, de alto 2·[r] con centro en [c] (los dedos arriba, el pulgar a la derecha): palma, muñeca,
 * cuatro dedos y el pulgar, cada pieza por separado (se pintan una por una, del mismo color, sin huecos entre ellas).
 * Sirve para la mano en negativo (pintada del color de la pared sobre el ocre) y para la mano en positivo (en ocre).
 */
internal fun piezasMano(c: Offset, r: Float): List<Path> {
    fun caja(l: Float, t: Float, d: Float, b: Float, radio: Float) = Path().apply {
        addRoundRect(androidx.compose.ui.geometry.RoundRect(c.x + l * r, c.y + t * r, c.x + d * r, c.y + b * r, CornerRadius(radio * r)))
    }
    fun dedo(x: Float, arriba: Float, abajo: Float, ancho: Float) = caja(x - ancho / 2, arriba, x + ancho / 2, abajo, ancho / 2)
    val pulgar = Path().apply {
        moveTo(c.x + 0.3f * r, c.y + 0.5f * r); lineTo(c.x + 0.36f * r, c.y + 0.08f * r)
        quadraticTo(c.x + 0.62f * r, c.y - 0.2f * r, c.x + 0.76f * r, c.y - 0.08f * r)
        quadraticTo(c.x + 0.84f * r, c.y + 0.02f * r, c.x + 0.66f * r, c.y + 0.22f * r)
        lineTo(c.x + 0.4f * r, c.y + 0.6f * r); close()
    }
    return listOf(
        caja(-0.44f, -0.12f, 0.4f, 0.62f, 0.2f),     // palma
        caja(-0.3f, 0.45f, 0.26f, 1f, 0.06f),        // muñeca
        dedo(-0.34f, -0.62f, 0.05f, 0.17f),          // meñique
        dedo(-0.13f, -0.86f, 0f, 0.19f),             // anular
        dedo(0.1f, -0.98f, 0f, 0.19f),               // medio
        dedo(0.3f, -0.8f, 0.05f, 0.18f),             // índice
        pulgar,
    )
}

/** La misma mano en un solo contorno (la unión de sus piezas), para pintarla con transparencia sin que se noten las juntas. */
internal fun manoUnida(c: Offset, r: Float): Path =
    piezasMano(c, r).reduce { a, b -> Path.combine(androidx.compose.ui.graphics.PathOperation.Union, a, b) }

/**
 * El bisonte de las cuevas, de perfil y mirando a la derecha, de ancho 2·[r] con centro en [c]: la giba alta sobre los
 * hombros, la cabeza baja con su cuerno y su barba, el lomo que cae hacia la grupa y la cola.
 */
internal fun bisonte(c: Offset, r: Float): Path = Path().apply {
    fun p(x: Float, y: Float) = Offset(c.x + x * r, c.y + y * r)
    val o = p(-0.85f, -0.15f); moveTo(o.x, o.y)
    val a1 = p(-0.5f, -0.3f); val a2 = p(-0.1f, -0.45f); val a3 = p(0.15f, -0.62f); cubicTo(a1.x, a1.y, a2.x, a2.y, a3.x, a3.y)
    val b1 = p(0.4f, -0.6f); val b2 = p(0.55f, -0.35f); val b3 = p(0.62f, -0.18f); cubicTo(b1.x, b1.y, b2.x, b2.y, b3.x, b3.y)
    listOf(0.7f to -0.34f, 0.72f to -0.16f, 0.9f to 0.05f, 0.92f to 0.22f, 0.8f to 0.28f, 0.68f to 0.44f, 0.58f to 0.3f,
        0.52f to 0.66f, 0.44f to 0.66f, 0.4f to 0.3f).forEach { (x, y) -> val q = p(x, y); lineTo(q.x, q.y) }
    val v = p(0f, 0.42f); val w = p(-0.38f, 0.3f); quadraticTo(v.x, v.y, w.x, w.y)
    listOf(-0.44f to 0.66f, -0.52f to 0.66f, -0.6f to 0.24f, -0.78f to 0.14f, -0.95f to 0.3f, -0.88f to 0.04f).forEach { (x, y) -> val q = p(x, y); lineTo(q.x, q.y) }
    close()
}

/** La escarapela tricolor (rojo afuera, blanco y azul al centro): tela plisada en tres anillos que gira despacio. */
private fun DrawScope.escarapelaEmblema(c: Offset, r: Float, afuera: Color, blanco: Color, centro: Color, tinta: Color, t: Float) {
    val pliegues = 24
    fun anillo(rr: Float, color: Color, giro: Float) {
        val p = Path()
        for (i in 0..pliegues * 2) {
            val a = rad(giro + 360.0 * i / (pliegues * 2))
            val q = c.mas(if (i % 2 == 0) rr else rr * 0.93f, a)
            if (i == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
        }
        p.close(); drawPath(p, color)
    }
    anillo(r * 0.98f, afuera, t * 360f)
    anillo(r * 0.68f, blanco, -t * 360f)
    anillo(r * 0.38f, centro, t * 720f)
    // Los pliegues, marcados con rayas finas.
    for (i in 0 until pliegues) {
        val a = rad(t * 360.0 + 360.0 * i / pliegues)
        drawLine(tinta.copy(alpha = 0.18f), c.mas(r * 0.7f, a), c.mas(r * 0.96f, a), r * 0.03f)
    }
    drawCircle(tinta.copy(alpha = 0.35f), r * 0.1f, c)
}

/** Una N imperial de trazo grueso y fino, con un brillo que la recorre. */
private fun DrawScope.letraN(c: Offset, r: Float, color: Color, t: Float) {
    val w = r * 0.62f; val h = r * 0.9f
    val l = c.x - w / 2; val d = c.x + w / 2; val arr = c.y - h / 2; val ab = c.y + h / 2
    drawLine(color, Offset(l, arr), Offset(l, ab), r * 0.09f)
    drawLine(color, Offset(d, arr), Offset(d, ab), r * 0.09f)
    drawLine(color, Offset(l, arr), Offset(d, ab), r * 0.24f)
    // Remates.
    listOf(Offset(l, ab), Offset(d, arr)).forEach { drawLine(color, it - Offset(r * 0.16f, 0f), it + Offset(r * 0.16f, 0f), r * 0.06f) }
    drawLine(color, Offset(l - r * 0.16f, arr), Offset(l + r * 0.1f, arr), r * 0.06f)
    val brillo = ((t * 1.6f) % 1.6f)
    if (brillo < 1f) {
        val p = Offset(l + (d - l) * brillo, arr + (ab - arr) * brillo)
        drawCircle(Color.White.copy(alpha = 0.55f * sin(PI * brillo).toFloat()), r * 0.16f, p)
    }
}

/**
 * El Imperio: una cartela oval azul con doble filete de oro, la N imperial al centro, la corona encima y dos abejas
 * (dibujo propio, no un escudo real) a los lados, que baten las alas. Un brillo recorre la N.
 */
private fun DrawScope.nImperial(c: Offset, r: Float, azul: Color, oro: Color, rojo: Color, t: Float) {
    val ov = Rect(c.x - r * 0.62f, c.y - r * 0.5f, c.x + r * 0.62f, c.y + r * 0.92f)
    drawOval(azul, ov.topLeft, ov.size)
    drawOval(oro, ov.topLeft, ov.size, style = Stroke(r * 0.08f))
    drawOval(oro.copy(alpha = 0.5f), ov.topLeft + Offset(r * 0.1f, r * 0.1f), Size(ov.width - r * 0.2f, ov.height - r * 0.2f), style = Stroke(r * 0.025f))
    letraN(Offset(c.x, ov.center.y), r * 0.6f, oro, t)
    coronaImperial(Offset(c.x, c.y - r * 0.52f), r * 0.28f, oro, rojo)
    abejaImperial(Offset(c.x - r * 0.8f, c.y + r * 0.6f), r * 0.2f, oro, t)
    abejaImperial(Offset(c.x + r * 0.8f, c.y + r * 0.6f), r * 0.2f, oro, t + 0.5f)
}

/** Corona imperial vista de frente: banda, bonete rojo bajo los arcos de oro, y el orbe con su cruz. [b]: centro de la base. */
private fun DrawScope.coronaImperial(b: Offset, s: Float, oro: Color, rojo: Color) {
    val cupula = Path().apply {
        moveTo(b.x - s * 0.95f, b.y - s * 0.25f); cubicTo(b.x - s, b.y - s * 1.2f, b.x + s, b.y - s * 1.2f, b.x + s * 0.95f, b.y - s * 0.25f); close()
    }
    drawPath(cupula, rojo.copy(alpha = 0.85f))
    drawPath(cupula, oro, style = Stroke(s * 0.14f))
    drawLine(oro, Offset(b.x, b.y - s * 0.25f), Offset(b.x, b.y - s * 0.95f), s * 0.14f)
    drawRect(oro, Offset(b.x - s, b.y - s * 0.25f), Size(s * 2, s * 0.32f))
    drawCircle(oro, s * 0.17f, Offset(b.x, b.y - s * 1.1f))
    drawLine(oro, Offset(b.x, b.y - s * 1.25f), Offset(b.x, b.y - s * 1.55f), s * 0.1f)
    drawLine(oro, Offset(b.x - s * 0.13f, b.y - s * 1.42f), Offset(b.x + s * 0.13f, b.y - s * 1.42f), s * 0.1f)
}

/** Abeja imperial vista desde arriba: alas abiertas que baten, cabeza, tórax y abdomen con franjas. */
private fun DrawScope.abejaImperial(p: Offset, s: Float, oro: Color, t: Float) {
    val bate = 0.75f + 0.25f * abs(sin(2 * PI * t * 6).toFloat())
    listOf(-1f, 1f).forEach { lado ->
        rotate(-lado * 35f, p) {
            val w = s * 0.95f * bate
            drawOval(oro.copy(alpha = 0.55f), Offset(if (lado < 0) p.x - w else p.x, p.y - s * 0.55f), Size(w, s * 0.42f))
        }
    }
    drawCircle(oro, s * 0.18f, Offset(p.x, p.y - s * 0.55f))
    drawOval(oro, Offset(p.x - s * 0.2f, p.y - s * 0.4f), Size(s * 0.4f, s * 0.38f))
    drawOval(oro, Offset(p.x - s * 0.25f, p.y - s * 0.05f), Size(s * 0.5f, s * 0.8f))
    listOf(0.2f, 0.42f).forEach { k -> drawLine(Color.Black.copy(alpha = 0.3f), Offset(p.x - s * 0.22f, p.y + s * k), Offset(p.x + s * 0.22f, p.y + s * k), s * 0.07f) }
    listOf(-1f, 1f).forEach { lado -> drawLine(oro, Offset(p.x, p.y - s * 0.68f), Offset(p.x + lado * s * 0.22f, p.y - s * 0.92f), s * 0.06f) }
}

/**
 * Lámina de naturalista: un escarabajo clavado con su alfiler sobre una fronda de helecho, con su etiqueta de colección.
 * Mueve las antenas, y una hormiga da la vuelta al ejemplar.
 */
private fun DrawScope.escarabajoLamina(c: Offset, r: Float, musgo: Color, oxido: Color, irisado: Color, tinta: Color, t: Float) {
    // Fronda de helecho detrás, en diagonal.
    val base = Offset(c.x - r * 0.78f, c.y + r * 0.8f); val punta = Offset(c.x + r * 0.72f, c.y - r * 0.82f)
    val raquis = Path().apply { moveTo(base.x, base.y); quadraticTo(c.x - r * 0.1f, c.y + r * 0.1f, punta.x, punta.y) }
    drawPath(raquis, musgo.copy(alpha = 0.55f), style = Stroke(r * 0.035f, cap = StrokeCap.Round))
    for (i in 2 until 16) {
        val k = i / 16f
        val x = (1 - k) * (1 - k) * base.x + 2 * (1 - k) * k * (c.x - r * 0.1f) + k * k * punta.x
        val y = (1 - k) * (1 - k) * base.y + 2 * (1 - k) * k * (c.y + r * 0.1f) + k * k * punta.y
        val l = r * (0.34f * (1 - k) + 0.06f)
        listOf(-1f, 1f).forEach { lado ->
            val a = atan2(punta.y - base.y, punta.x - base.x) + lado * 1.15f
            val q = Offset(x, y).mas(l, a)
            drawPath(Path().apply { moveTo(x, y); quadraticTo((x + q.x) / 2 + lado * l * 0.12f, (y + q.y) / 2 - l * 0.12f, q.x, q.y) }, musgo.copy(alpha = 0.42f), style = Stroke(r * 0.05f, cap = StrokeCap.Round))
        }
    }
    // Etiqueta de colección, abajo a la derecha.
    val et = Rect(c.x + r * 0.2f, c.y + r * 0.6f, c.x + r * 0.92f, c.y + r * 0.9f)
    drawRect(Color(0xFFF4ECD6), et.topLeft, et.size)
    drawRect(tinta.copy(alpha = 0.6f), et.topLeft, et.size, style = Stroke(r * 0.018f))
    drawLine(tinta.copy(alpha = 0.55f), Offset(et.left + r * 0.06f, et.top + r * 0.1f), Offset(et.right - r * 0.08f, et.top + r * 0.1f), r * 0.02f)
    drawLine(tinta.copy(alpha = 0.4f), Offset(et.left + r * 0.06f, et.top + r * 0.19f), Offset(et.right - r * 0.24f, et.top + r * 0.19f), r * 0.02f)
    // El escarabajo, de cabeza hacia arriba e inclinado.
    val L = r * 0.62f; val A = L * 0.68f; val b0 = Offset(c.x - r * 0.04f, c.y + r * 0.02f)
    rotate(-18f, b0) {
        val g = r * 0.035f
        // Patas: tres pares con codo, que se reacomodan apenas.
        listOf(Triple(-0.34f, -0.75f, -1.45f), Triple(-0.04f, 0.05f, 0.4f), Triple(0.2f, 0.65f, 1.2f)).forEachIndexed { i, (y0, a1, a2) ->
            val mov = 0.08f * sin(2 * PI * (t * 6 + i * 0.3)).toFloat()
            listOf(-1f, 1f).forEach { lado ->
                val p0 = Offset(b0.x + lado * A * 0.3f, b0.y + L * y0)
                val p1 = Offset(p0.x + lado * L * 0.3f * cos(a1 + mov), p0.y + L * 0.3f * sin(a1 + mov))
                val p2 = Offset(p1.x + lado * L * 0.26f * cos(a2 + mov), p1.y + L * 0.26f * sin(a2 + mov))
                drawPath(Path().apply { moveTo(p0.x, p0.y); lineTo(p1.x, p1.y); lineTo(p2.x, p2.y) }, tinta, style = Stroke(g, cap = StrokeCap.Round))
            }
        }
        // Antenas que tantean.
        listOf(-1f, 1f).forEach { lado ->
            val tanteo = 0.18f * sin(2 * PI * (t * 9 + if (lado < 0) 0.0 else 0.4)).toFloat()
            val ini = Offset(b0.x + lado * A * 0.1f, b0.y - L * 0.66f)
            val a = (-PI / 2 + lado * 0.55 + tanteo).toFloat()
            val m = ini.mas(L * 0.22f, a); val f = m.mas(L * 0.2f, a + lado * 0.35f)
            drawPath(Path().apply { moveTo(ini.x, ini.y); lineTo(m.x, m.y); lineTo(f.x, f.y) }, tinta, style = Stroke(g * 0.8f, cap = StrokeCap.Round))
            drawCircle(tinta, g * 1.1f, f)
        }
        drawOval(tinta, Offset(b0.x - A * 0.2f, b0.y - L * 0.74f), Size(A * 0.4f, L * 0.24f))
        drawOval(androidx.compose.ui.graphics.lerp(irisado, tinta, 0.35f), Offset(b0.x - A * 0.42f, b0.y - L * 0.58f), Size(A * 0.84f, L * 0.38f))
        // Élitros con brillo de metal: del irisado al musgo, con un destello de óxido dorado.
        val el = Rect(b0.x - A * 0.5f, b0.y - L * 0.24f, b0.x + A * 0.5f, b0.y + L * 0.56f)
        drawOval(androidx.compose.ui.graphics.Brush.radialGradient(listOf(oxido.copy(alpha = 0.95f), musgo, irisado, androidx.compose.ui.graphics.lerp(irisado, tinta, 0.5f)),
            center = Offset(el.center.x - A * 0.16f, el.top + el.height * 0.3f), radius = el.height * 0.8f), el.topLeft, el.size)
        drawOval(tinta, el.topLeft, el.size, style = Stroke(g * 0.8f))
        drawLine(tinta, Offset(b0.x, el.top + L * 0.02f), Offset(b0.x, el.bottom - L * 0.02f), g * 0.8f)
        listOf(-0.22f, -0.36f, 0.22f, 0.36f).forEach { k -> drawLine(tinta.copy(alpha = 0.35f), Offset(b0.x + A * k, el.top + L * 0.1f), Offset(b0.x + A * k * 1.04f, el.bottom - L * 0.14f), g * 0.4f) }
        drawOval(tinta, Offset(b0.x - A * 0.42f, b0.y - L * 0.58f), Size(A * 0.84f, L * 0.38f), style = Stroke(g * 0.8f))
        // El alfiler.
        drawCircle(oxido, r * 0.06f, Offset(b0.x + A * 0.14f, b0.y - L * 0.02f))
        drawCircle(Color.White.copy(alpha = 0.5f), r * 0.02f, Offset(b0.x + A * 0.12f, b0.y - L * 0.04f))
    }
    // Una hormiga que rodea el ejemplar.
    val ang = (2 * PI * t).toFloat()
    val pos = Offset(c.x + r * 0.86f * cos(ang), c.y + r * 0.86f * sin(ang))
    hormiga(pos, r * 0.16f, ang + PI.toFloat() / 2, tinta.copy(alpha = 0.85f), t)
}

/**
 * Ciudad verde: un semáforo viejo y ladeado, con la hiedra subiendo por el poste y abriéndose sobre la caja. El rojo y el
 * ámbar están muertos; solo la luz verde sigue viva y late despacio, y las hojas se mecen apenas.
 */
private fun DrawScope.semaforoHiedra(c: Offset, r: Float, hiedra: Color, oxido: Color, ambar: Color, tinta: Color, t: Float) {
    val late = 0.55f + 0.45f * (0.5f + 0.5f * sin(2 * PI * t).toFloat())
    rotate(-7f, Offset(c.x, c.y + r * 0.9f)) {
        // El poste, con su abrazadera oxidada.
        drawRect(tinta.copy(alpha = 0.75f), Offset(c.x - r * 0.07f, c.y + r * 0.1f), Size(r * 0.14f, r * 0.85f))
        drawRect(oxido, Offset(c.x - r * 0.1f, c.y + r * 0.22f), Size(r * 0.2f, r * 0.07f))
        // La caja, con su borde de óxido, y las tres luces con su visera.
        val caja = Rect(c.x - r * 0.26f, c.y - r * 0.86f, c.x + r * 0.26f, c.y + r * 0.18f)
        drawRoundRect(tinta.copy(alpha = 0.85f), caja.topLeft, caja.size, CornerRadius(r * 0.08f))
        drawRoundRect(oxido.copy(alpha = 0.5f), caja.topLeft, caja.size, CornerRadius(r * 0.08f), style = Stroke(r * 0.025f))
        listOf(-0.6f, -0.28f, 0.04f).forEachIndexed { i, fy ->
            val luz = Offset(c.x, c.y + r * fy)
            if (i == 2) drawCircle(hiedra.copy(alpha = 0.25f * late), r * 0.24f, luz)
            val col = when (i) { 0 -> oxido.copy(alpha = 0.35f); 1 -> ambar.copy(alpha = 0.3f); else -> hiedra.copy(alpha = late) }
            drawCircle(col, r * 0.11f, luz)
            drawArc(tinta, 200f, 140f, false, Offset(luz.x - r * 0.15f, luz.y - r * 0.15f), Size(r * 0.3f, r * 0.3f), style = Stroke(r * 0.04f))
        }
        // Una grieta en el costado de la caja.
        drawPath(Path().apply { moveTo(caja.right, c.y - r * 0.5f); lineTo(caja.right - r * 0.08f, c.y - r * 0.44f); lineTo(caja.right - r * 0.05f, c.y - r * 0.36f) },
            oxido, style = Stroke(r * 0.02f))
        // La hiedra: sube enroscada por el poste y trepa por un lado de la caja.
        val tallo = Path().apply {
            moveTo(c.x - r * 0.12f, c.y + r * 0.95f)
            cubicTo(c.x + r * 0.2f, c.y + r * 0.7f, c.x - r * 0.25f, c.y + r * 0.45f, c.x + r * 0.12f, c.y + r * 0.25f)
            cubicTo(c.x + r * 0.42f, c.y + r * 0.05f, c.x + r * 0.38f, c.y - r * 0.45f, c.x + r * 0.2f, c.y - r * 0.8f)
        }
        drawPath(tallo, hiedra.copy(alpha = 0.8f), style = Stroke(r * 0.03f, cap = StrokeCap.Round))
        val hojas = listOf(Offset(0.02f, 0.82f) to 30f, Offset(-0.16f, 0.55f) to -50f, Offset(0.1f, 0.33f) to 40f, Offset(0.36f, 0.05f) to 70f,
            Offset(0.4f, -0.3f) to 100f, Offset(0.3f, -0.62f) to 60f, Offset(-0.32f, 0.2f) to -80f)
        hojas.forEachIndexed { i, (o, g) ->
            val q = Offset(c.x + r * o.x, c.y + r * o.y)
            val mece = 6f * sin(2 * PI * (t * 2 + i * 0.17)).toFloat()
            rotate(g + mece, q) { hojaHiedraEmblema(q, r * (0.13f + 0.02f * (i % 3)), hiedra) }
        }
    }
}

/** Una hoja de hiedra de cinco puntas (la del centro más larga), con el pecíolo en [p] y la hoja hacia arriba. */
private fun DrawScope.hojaHiedraEmblema(p: Offset, s: Float, color: Color) {
    drawPath(Path().apply {
        moveTo(p.x, p.y); lineTo(p.x - s * 0.55f, p.y - s * 0.15f); lineTo(p.x - s * 0.8f, p.y - s * 0.55f); lineTo(p.x - s * 0.32f, p.y - s * 0.55f)
        lineTo(p.x, p.y - s); lineTo(p.x + s * 0.32f, p.y - s * 0.55f); lineTo(p.x + s * 0.8f, p.y - s * 0.55f); lineTo(p.x + s * 0.55f, p.y - s * 0.15f); close()
    }, color)
    drawLine(Color.Black.copy(alpha = 0.18f * color.alpha), p, Offset(p.x, p.y - s * 0.8f), s * 0.06f)
}

/** Una hormiga vista desde arriba: cabeza, tórax y gáster, con seis patas que se alternan al caminar. */
internal fun DrawScope.hormiga(p: Offset, l: Float, ang: Float, color: Color, t: Float = 0f) {
    rotate(Math.toDegrees(ang.toDouble()).toFloat(), p) {
        val g = l * 0.05f
        val paso = 0.25f * sin(2 * PI * t * 40).toFloat()
        // Patas cortas e inclinadas: las de adelante hacia adelante y las de atrás hacia atrás, para que se lea como hormiga y no como cruz.
        listOf(-0.04f, 0.03f, 0.1f).forEachIndexed { i, x ->
            listOf(-1f, 1f).forEach { lado ->
                val s = if ((i + if (lado > 0) 1 else 0) % 2 == 0) paso else -paso
                val p0 = Offset(p.x + l * x, p.y)
                val p1 = Offset(p0.x + l * (0.1f - i * 0.1f + s * 0.2f), p0.y + lado * l * 0.15f)
                val p2 = Offset(p1.x + l * (0.14f - i * 0.16f + s * 0.25f), p1.y + lado * l * 0.08f)
                drawPath(Path().apply { moveTo(p0.x, p0.y); lineTo(p1.x, p1.y); lineTo(p2.x, p2.y) }, color, style = Stroke(g, cap = StrokeCap.Round))
            }
        }
        drawOval(color, Offset(p.x - l * 0.58f, p.y - l * 0.17f), Size(l * 0.46f, l * 0.34f))
        drawOval(color, Offset(p.x - l * 0.12f, p.y - l * 0.08f), Size(l * 0.3f, l * 0.16f))
        drawOval(color, Offset(p.x + l * 0.16f, p.y - l * 0.11f), Size(l * 0.24f, l * 0.22f))
        listOf(-1f, 1f).forEach { lado ->
            drawPath(Path().apply { moveTo(p.x + l * 0.36f, p.y); lineTo(p.x + l * 0.48f, p.y + lado * l * 0.1f); lineTo(p.x + l * 0.6f, p.y + lado * l * 0.2f) }, color, style = Stroke(g * 0.8f, cap = StrokeCap.Round))
        }
    }
}

/** Ventana gótica de lanceta: arco apuntado, cristales de colores entre plomos y un rosetón; la luz pasa de un vidrio a otro. */
private fun DrawScope.vitral(c: Offset, r: Float, oro: Color, rubi: Color, zafiro: Color, plomo: Color, t: Float) {
    val w = r * 1.2f; val l = c.x - w / 2; val d = c.x + w / 2; val ab = c.y + r * 0.95f; val hombro = c.y - r * 0.15f
    val arco = Path().apply {
        moveTo(l, ab); lineTo(l, hombro)
        quadraticTo(l, c.y - r * 0.78f, c.x, c.y - r * 0.98f)
        quadraticTo(d, c.y - r * 0.78f, d, hombro); lineTo(d, ab); close()
    }
    val vidrios = listOf(zafiro, rubi, Color(0xFF2E8B57), oro, zafiro, rubi)
    clipPath(arco) {
        val filas = 4; val alto = (ab - (c.y - r)) / filas
        for (f in 0 until filas) for (k in 0 until 2) {
            val i = f * 2 + k
            val brillo = 0.55f + 0.45f * (0.5f + 0.5f * cos(2 * PI * (t - i / 8.0)).toFloat())
            drawRect(vidrios[(i + f) % vidrios.size].copy(alpha = brillo), Offset(l + k * w / 2, c.y - r + f * alto), Size(w / 2, alto))
        }
        // Rosetón arriba.
        val ro = Offset(c.x, c.y - r * 0.5f)
        drawCircle(oro.copy(alpha = 0.9f), r * 0.22f, ro)
        for (i in 0 until 6) drawCircle(rubi, r * 0.07f, ro.mas(r * 0.13f, rad(60.0 * i - 90)))
        drawCircle(zafiro, r * 0.06f, ro)
        // Plomos.
        drawLine(plomo, Offset(c.x, c.y - r * 0.28f), Offset(c.x, ab), r * 0.05f)
        for (f in 1 until 4) { val y = c.y - r + f * alto; if (y > c.y - r * 0.3f) drawLine(plomo, Offset(l, y), Offset(d, y), r * 0.035f) }
        drawCircle(plomo, r * 0.22f, ro, style = Stroke(r * 0.04f))
    }
    drawPath(arco, plomo, style = Stroke(r * 0.09f))
}

/**
 * El signo del horror cósmico (invención propia): un heptágono que no cierra, cruzado por tres ganchos, con un ojo de
 * pupila vertical en el centro que parpadea muy de vez en cuando; por abajo asoman, apenas, tres puntas que se enroscan.
 */
private fun DrawScope.signoArcano(c: Offset, r: Float, tinta: Color, ojo: Color, sombra: Color, fondo: Color, t: Float) {
    // Las puntas que asoman por debajo y se mecen despacio.
    for (k in -1..1) {
        val x = c.x + k * r * 0.5f; val m = sin(2 * PI * (t + k * 0.21)).toFloat() * r * 0.06f
        drawPath(Path().apply {
            moveTo(x - r * 0.07f, c.y + r * 1.02f)
            quadraticTo(x - r * 0.1f + m, c.y + r * 0.62f, x + k * r * 0.12f + m, c.y + r * 0.58f)
            quadraticTo(x + k * r * 0.2f + m, c.y + r * 0.66f, x + k * r * 0.1f + m, c.y + r * 0.7f)
            quadraticTo(x + r * 0.02f + m, c.y + r * 0.72f, x + r * 0.07f, c.y + r * 1.02f); close()
        }, sombra.copy(alpha = 0.35f))
    }
    // El heptágono abierto: le falta el lado de abajo a la izquierda.
    val p = Path()
    for (k in 0..6) {
        val q = c.mas(r * 0.82f, rad(-90.0 + k * 360.0 / 7))
        if (k == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
    }
    drawPath(p, tinta, style = Stroke(r * 0.08f, join = StrokeJoin.Round))
    listOf(0, 2, 5).forEach { k ->
        val a = rad(-90.0 + k * 360.0 / 7)
        drawLine(tinta, c.mas(r * 0.82f, a), c.mas(r * 1.0f, a + 0.32f), r * 0.07f, StrokeCap.Round)
        drawCircle(tinta, r * 0.05f, c.mas(r * 1.0f, a + 0.32f))
    }
    // El ojo: se cierra un instante al final de cada vuelta.
    val abre = if (t > 0.94f) abs(t - 0.97f) / 0.03f else 1f
    val ao = r * 0.5f; val alto = r * 0.3f * abre.coerceIn(0.08f, 1f)
    val parpado = Path().apply { moveTo(c.x - ao, c.y); quadraticTo(c.x, c.y - alto * 2, c.x + ao, c.y); quadraticTo(c.x, c.y + alto * 2, c.x - ao, c.y); close() }
    drawPath(parpado, fondo)
    clipPath(parpado) {
        drawCircle(ojo, r * 0.26f, c)
        drawOval(sombra, Offset(c.x - r * 0.055f, c.y - r * 0.22f), Size(r * 0.11f, r * 0.44f))
        drawCircle(fondo.copy(alpha = 0.7f), r * 0.045f, c + Offset(r * 0.09f, -r * 0.09f))
    }
    drawPath(parpado, tinta, style = Stroke(r * 0.07f))
}

/** Planeta con anillo y una luna que le da la vuelta; las estrellas titilan. */
private fun DrawScope.planeta(c: Offset, r: Float, azul: Color, naranja: Color, violeta: Color, tinta: Color, t: Float) {
    listOf(Offset(-0.8f, -0.7f), Offset(0.75f, -0.85f), Offset(0.85f, 0.6f), Offset(-0.7f, 0.8f)).forEachIndexed { i, o ->
        val tit = 0.3f + 0.7f * abs(sin(PI * (t * 3 + i * 0.37)).toFloat())
        val p = c + Offset(o.x * r, o.y * r); val s = r * 0.08f * tit
        drawLine(tinta.copy(alpha = tit), p - Offset(s, 0f), p + Offset(s, 0f), r * 0.025f); drawLine(tinta.copy(alpha = tit), p - Offset(0f, s), p + Offset(0f, s), r * 0.025f)
    }
    val ang = 2 * PI * t
    val luna = Offset(c.x + r * 0.82f * cos(ang).toFloat(), c.y + r * 0.28f * sin(ang).toFloat())
    val detras = sin(ang) < 0
    if (detras) drawCircle(tinta.copy(alpha = 0.8f), r * 0.09f, luna)
    rotate(-18f, c) {
        // Mitad trasera del anillo, el planeta y la mitad delantera.
        drawArc(naranja.copy(alpha = 0.6f), 180f, 180f, false, Offset(c.x - r * 0.88f, c.y - r * 0.22f), Size(r * 1.76f, r * 0.44f), style = Stroke(r * 0.07f))
        drawCircle(azul, r * 0.5f, c)
        clipPath(Path().apply { addOval(Rect(c.x - r * 0.5f, c.y - r * 0.5f, c.x + r * 0.5f, c.y + r * 0.5f)) }) {
            drawRect(violeta.copy(alpha = 0.45f), Offset(c.x - r * 0.5f, c.y - r * 0.18f), Size(r, r * 0.1f))
            drawRect(violeta.copy(alpha = 0.3f), Offset(c.x - r * 0.5f, c.y + r * 0.08f), Size(r, r * 0.07f))
            drawCircle(Color.Black.copy(alpha = 0.22f), r * 0.5f, c + Offset(r * 0.22f, r * 0.16f))
        }
        drawArc(naranja, 0f, 180f, false, Offset(c.x - r * 0.88f, c.y - r * 0.22f), Size(r * 1.76f, r * 0.44f), style = Stroke(r * 0.07f))
    }
    if (!detras) drawCircle(tinta, r * 0.09f, luna)
}

/**
 * La medusa luminosa del Abismo: la campana late (se cierra y se abre una vez por vuelta), con las cuatro gónadas en
 * herradura de la medusa luna (Aurelia), la orla de tentáculos finos y cuatro brazos orales largos que ondulan. De
 * noche la rodea su propia luz y le brillan puntos en el borde.
 */
private fun DrawScope.medusa(c: Offset, r: Float, cuerpo: Color, luz: Color, brazos: Color, t: Float, oscuro: Boolean) {
    val abre = 0.5f + 0.5f * sin(2 * PI * t).toFloat()
    val ancho = r * (0.56f + 0.1f * abre); val alto = r * (0.5f - 0.08f * abre)
    val yb = c.y - r * 0.08f
    if (oscuro) drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.32f), Color.Transparent), center = Offset(c.x, yb - alto * 0.3f), radius = r * 0.98f), r * 0.98f, Offset(c.x, yb - alto * 0.3f))
    // Los brazos orales, por detrás de la campana.
    for (k in 0 until 4) {
        val x0 = c.x + (k - 1.5f) * r * 0.1f
        val brazo = Path().apply {
            moveTo(x0, yb - alto * 0.2f)
            for (s in 1..12) lineTo(x0 + r * 0.08f * sin(2 * PI * (t + s * 0.08f + k * 0.25f)).toFloat() * s / 12f, yb + s * r * 0.05f)
        }
        drawPath(brazo, brazos.copy(alpha = 0.75f), style = Stroke(r * 0.045f, cap = StrokeCap.Round))
    }
    // La orla: tentáculos finos que cuelgan del borde.
    for (k in 0..10) {
        val x0 = c.x - ancho * 0.94f + 1.88f * ancho * k / 10f
        val onda = r * 0.06f * sin(2 * PI * (t + k * 0.11)).toFloat()
        drawPath(Path().apply { moveTo(x0, yb); quadraticTo(x0 + onda, yb + r * 0.22f, x0 - onda * 0.5f, yb + r * (0.36f + 0.06f * abre)) }, cuerpo.copy(alpha = 0.55f), style = Stroke(r * 0.014f))
    }
    // La campana, translúcida, con el borde ondulado por debajo.
    val campana = Path().apply {
        moveTo(c.x - ancho, yb)
        cubicTo(c.x - ancho, yb - alto * 1.36f, c.x + ancho, yb - alto * 1.36f, c.x + ancho, yb)
        for (k in 1..6) {
            val x = c.x + ancho - 2 * ancho * k / 6f
            quadraticTo(x + ancho / 6f, yb + r * 0.05f, x, yb)
        }
        close()
    }
    drawPath(campana, Brush.verticalGradient(listOf(cuerpo.copy(alpha = 0.5f), cuerpo.copy(alpha = 0.85f)), startY = yb - alto, endY = yb))
    drawPath(campana, cuerpo, style = Stroke(r * 0.03f))
    // Las cuatro gónadas en herradura, vistas a través de la campana.
    for (k in 0 until 4) {
        val x = c.x + (k - 1.5f) * ancho * 0.4f
        drawArc(luz.copy(alpha = if (oscuro) 0.9f else 0.7f), 160f, 220f, false, Offset(x - r * 0.07f, yb - alto * 0.72f), Size(r * 0.14f, r * 0.14f), style = Stroke(r * 0.03f))
    }
    drawArc(Color.White.copy(alpha = 0.35f), 200f, 50f, false, Offset(c.x - ancho * 0.8f, yb - alto * 0.95f), Size(ancho * 1.6f, alto * 1.5f), style = Stroke(r * 0.03f))
    if (oscuro) for (k in 0 until 8) {
        val brilla = 0.5f + 0.5f * sin(2 * PI * (t * 2 + k * 0.125)).toFloat()
        drawCircle(luz.copy(alpha = brilla), r * 0.022f, Offset(c.x - ancho + 2 * ancho * k / 7f, yb))
    }
}

/**
 * Estrella de ocho puntas de la lacería andalusí: dos cuadrados hechos cinta de oro (con su hilo al centro) que se cruzan
 * por encima y por debajo, alternando, como en el alicatado; dentro, un octógono con una estrella de dieciséis puntas, y
 * alrededor un anillo de dieciséis puntos.
 */
private fun DrawScope.estrellaLazo(c: Offset, r: Float, oro: Color, verde: Color, azul: Color, fondo: Color) {
    val lado = r * 0.86f
    fun cuadrado(giro: Double) = Path().apply {
        for (i in 0..3) { val p = c.mas(lado, rad(giro + 90.0 * i)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close()
    }
    val a = cuadrado(-45.0); val b = cuadrado(0.0)
    drawCircle(oro.copy(alpha = 0.55f), r * 0.97f, c, style = Stroke(r * 0.025f))
    for (i in 0 until 16) drawCircle(oro.copy(alpha = 0.7f), r * 0.03f, c.mas(r * 0.97f, rad(22.5 * i + 11.25)))
    drawPath(a, verde.copy(alpha = 0.8f)); drawPath(b, verde.copy(alpha = 0.8f))
    // La cinta: oro ancho con un hilo del color del fondo al centro.
    listOf(a, b).forEach { cinta ->
        drawPath(cinta, oro, style = Stroke(r * 0.12f, join = StrokeJoin.Miter))
        drawPath(cinta, fondo, style = Stroke(r * 0.035f, join = StrokeJoin.Miter))
    }
    // Los ocho cruces (a 0,7654 del vértice, cada 45° desde 22,5°): en cada uno pasa por encima una cinta distinta, alternando.
    for (k in 0 until 8) {
        val th = 22.5 + 45.0 * k
        val cruce = c.mas(lado * 0.7654f, rad(th))
        val normal = if (k % 2 == 0) 90.0 * Math.round(th / 90.0) else 45.0 + 90.0 * Math.floor(th / 90.0)
        val tg = rad(normal + 90.0)
        val l = r * 0.13f
        drawLine(oro, cruce.mas(-l, tg), cruce.mas(l, tg), r * 0.12f)
        drawLine(fondo, cruce.mas(-l, tg), cruce.mas(l, tg), r * 0.035f)
    }
    // Octógono interior y estrella pequeña.
    val oct = Path().apply { for (i in 0..7) { val p = c.mas(r * 0.46f, rad(22.5 + 45.0 * i)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
    drawPath(oct, fondo); drawPath(oct, oro, style = Stroke(r * 0.045f))
    val est = Path().apply { for (i in 0 until 16) { val rr = if (i % 2 == 0) r * 0.38f else r * 0.19f; val p = c.mas(rr, rad(-90.0 + 22.5 * i)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
    drawPath(est, azul)
    drawCircle(oro, r * 0.09f, c)
}

/** El toono, la corona del techo de la yurta: la rueda por donde se ve el cielo azul, con sus varas; el sol pasa por arriba. */
private fun DrawScope.toono(c: Offset, r: Float, cielo: Color, rojo: Color, oro: Color, t: Float) {
    clipPath(Path().apply { addOval(Rect(c.x - r * 0.62f, c.y - r * 0.62f, c.x + r * 0.62f, c.y + r * 0.62f)) }) {
        drawCircle(cielo.copy(alpha = 0.85f), r * 0.62f, c)
        val ang = 2 * PI * t
        drawCircle(oro, r * 0.16f, Offset(c.x + r * 0.42f * cos(ang).toFloat(), c.y + r * 0.42f * sin(ang).toFloat()))
    }
    // Varas del techo (uni) que salen de la rueda.
    for (i in 0 until 20) { val a = rad(18.0 * i); drawLine(rojo, c.mas(r * 0.68f, a), c.mas(r * 0.98f, a), r * 0.05f, StrokeCap.Round) }
    drawCircle(rojo, r * 0.66f, c, style = Stroke(r * 0.1f))
    drawCircle(oro, r * 0.66f, c, style = Stroke(r * 0.03f))
    // Las barras cruzadas del centro.
    val k = r * 0.6f
    listOf(-0.18f, 0.18f).forEach { d ->
        drawLine(rojo, Offset(c.x - k, c.y + d * r), Offset(c.x + k, c.y + d * r), r * 0.06f)
        drawLine(rojo, Offset(c.x + d * r, c.y - k), Offset(c.x + d * r, c.y + k), r * 0.06f)
    }
    drawCircle(oro, r * 0.07f, c)
}

/** Cuaderno de Leonardo: un círculo y un cuadrado de proporciones, y un compás que va trazando el arco. */
private fun DrawScope.compas(c: Offset, r: Float, sanguina: Color, tinta: Color, azul: Color, t: Float) {
    val s = r * 0.7f
    drawRect(tinta.copy(alpha = 0.75f), Offset(c.x - s, c.y - s * 0.8f), Size(s * 2, s * 1.8f), style = Stroke(r * 0.045f))
    drawCircle(tinta.copy(alpha = 0.5f), r * 0.86f, c + Offset(0f, r * 0.05f), style = Stroke(r * 0.035f))
    drawLine(tinta.copy(alpha = 0.3f), Offset(c.x - s, c.y - s * 0.86f), Offset(c.x + s, c.y + s), r * 0.015f)
    drawLine(tinta.copy(alpha = 0.3f), Offset(c.x + s, c.y - s * 0.86f), Offset(c.x - s, c.y + s), r * 0.015f)
    val barrido = 360f * t
    drawArc(sanguina, -90f, barrido, false, Offset(c.x - r * 0.86f, c.y - r * 0.81f), Size(r * 1.72f, r * 1.72f), style = Stroke(r * 0.09f, cap = StrokeCap.Round))
    // Compás: una pata clavada en el centro y la otra sobre el arco.
    val centro = c + Offset(0f, r * 0.05f)
    val punta = centro.mas(r * 0.86f, rad(-90.0 + barrido))
    val bisagra = centro + (punta - centro) * 0.5f + Offset(0f, -r * 0.45f).let { o -> val a = rad(barrido.toDouble()); Offset(o.x * cos(a) - o.y * sin(a), o.x * sin(a) + o.y * cos(a)) }
    drawLine(tinta, centro, bisagra, r * 0.08f, StrokeCap.Round)
    drawLine(tinta, bisagra, punta, r * 0.08f, StrokeCap.Round)
    drawCircle(azul, r * 0.1f, bisagra)
    drawCircle(sanguina, r * 0.04f, punta)
}

/**
 * La calavera sobre la rosa de los vientos: ocho puntas de tinta y latón (las cardinales largas, con la flor de lis al
 * norte) detrás de un cráneo de hueso. La aguja de la rosa busca el norte: cabecea un poco a cada lado.
 */
private fun DrawScope.calaveraRosa(c: Offset, r: Float, hueso: Color, tinta: Color, rojo: Color, laton: Color, t: Float) {
    val giro = 9f * sin(2 * PI * t).toFloat()
    rotate(giro, c) {
        for (i in 0 until 8) {
            val a = rad(-90.0 + 45.0 * i)
            val largo = r * (if (i % 2 == 0) 0.98f else 0.66f); val ancho = r * (if (i % 2 == 0) 0.16f else 0.12f)
            val punta = c.mas(largo, a); val izq = c.mas(ancho, a - PI.toFloat() / 2); val der = c.mas(ancho, a + PI.toFloat() / 2)
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(izq.x, izq.y); lineTo(punta.x, punta.y); close() }, laton)
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(der.x, der.y); lineTo(punta.x, punta.y); close() }, tinta.copy(alpha = 0.85f))
        }
        // La flor de lis del norte, en rojo.
        val n = c.mas(r * 0.98f, rad(-90.0))
        drawCircle(rojo, r * 0.06f, n)
        drawCircle(tinta.copy(alpha = 0.6f), r * 0.62f, c, style = Stroke(r * 0.03f))
    }
    // El cráneo, con las cuencas, la nariz y los dientes.
    val k = Offset(c.x, c.y - r * 0.06f)
    drawCircle(tinta, r * 0.37f, k)
    drawCircle(hueso, r * 0.33f, k)
    val quijada = Offset(c.x - r * 0.18f, c.y + r * 0.12f)
    drawRoundRect(tinta, quijada - Offset(r * 0.03f, 0f), Size(r * 0.42f, r * 0.26f), CornerRadius(r * 0.08f))
    drawRoundRect(hueso, quijada + Offset(0f, r * 0.0f), Size(r * 0.36f, r * 0.22f), CornerRadius(r * 0.06f))
    drawCircle(tinta, r * 0.095f, Offset(c.x - r * 0.13f, c.y - r * 0.08f))
    drawCircle(tinta, r * 0.095f, Offset(c.x + r * 0.13f, c.y - r * 0.08f))
    drawPath(Path().apply { moveTo(c.x, c.y + r * 0.02f); lineTo(c.x - r * 0.045f, c.y + r * 0.1f); lineTo(c.x + r * 0.045f, c.y + r * 0.1f); close() }, tinta)
    for (j in -1..1) drawLine(tinta, Offset(c.x + j * r * 0.09f, c.y + r * 0.16f), Offset(c.x + j * r * 0.09f, c.y + r * 0.31f), r * 0.025f)
}

/**
 * Copo de nieve dendrítico: seis brazos con tres pares de ramas que se acortan hacia la punta (las primeras con ramitas),
 * una placa hexagonal con su estrella de oro calada al centro y destellos que se encienden en las puntas, uno tras otro.
 * Gira muy despacio.
 */
private fun DrawScope.copo(c: Offset, r: Float, color: Color, oro: Color, t: Float) {
    rotate(t * 360f, c) {
        for (i in 0 until 6) {
            val a = rad(60.0 * i - 90)
            val punta = c.mas(r * 0.86f, a)
            drawLine(color, c, punta, r * 0.07f, StrokeCap.Round)
            listOf(Triple(0.32f, 0.26f, 0.06f), Triple(0.52f, 0.22f, 0.055f), Triple(0.7f, 0.14f, 0.05f)).forEach { (d, l, g) ->
                val base = c.mas(r * d, a)
                listOf(1f, -1f).forEach { s ->
                    val rama = base.mas(r * l, a + s * rad(55.0))
                    drawLine(color, base, rama, r * g, StrokeCap.Round)
                    if (d < 0.4f) { val m = base + (rama - base) * 0.55f; drawLine(color, m, m.mas(r * 0.08f, a), r * 0.035f, StrokeCap.Round) }
                }
            }
            val brillo = (0.5f + 0.5f * cos(2 * PI * (t * 12 - i / 6.0)).toFloat()).let { it * it * it }
            drawCircle(oro.copy(alpha = 0.3f + 0.7f * brillo), r * (0.05f + 0.04f * brillo), punta)
        }
        val hex = Path().apply { for (i in 0..5) { val p = c.mas(r * 0.2f, rad(60.0 * i - 60)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
        drawPath(hex, color)
        val est = Path().apply { for (i in 0 until 12) { val rr = if (i % 2 == 0) r * 0.14f else r * 0.06f; val p = c.mas(rr, rad(-90.0 + 30.0 * i)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
        drawPath(est, oro)
    }
}

/** Moneda de agujero cuadrado, con cuatro trazos alrededor; gira sobre su eje como si la lanzaran. */
private fun DrawScope.moneda(c: Offset, r: Float, oro: Color, cordon: Color, fondo: Color, t: Float) {
    val giro = cos(2 * PI * t).toFloat()
    val ancho = 0.2f + 0.8f * abs(giro)
    withTransform({ scale(ancho, 1f, c) }) {
        drawCircle(oro, r * 0.86f, c)
        drawCircle(fondo.copy(alpha = 0.35f), r * 0.7f, c, style = Stroke(r * 0.05f))
        val h = r * 0.2f
        drawRect(fondo, Offset(c.x - h, c.y - h), Size(h * 2, h * 2))
        drawRect(oro.copy(alpha = 1f), Offset(c.x - h, c.y - h), Size(h * 2, h * 2), style = Stroke(r * 0.05f))
        val tinta = fondo.copy(alpha = 0.55f)
        // Cuatro «caracteres» geométricos, uno en cada lado del agujero.
        drawLine(tinta, Offset(c.x - r * 0.12f, c.y - r * 0.5f), Offset(c.x + r * 0.12f, c.y - r * 0.5f), r * 0.05f)
        drawLine(tinta, Offset(c.x, c.y - r * 0.6f), Offset(c.x, c.y - r * 0.38f), r * 0.05f)
        drawLine(tinta, Offset(c.x - r * 0.1f, c.y + r * 0.42f), Offset(c.x + r * 0.1f, c.y + r * 0.42f), r * 0.05f)
        drawLine(tinta, Offset(c.x - r * 0.12f, c.y + r * 0.55f), Offset(c.x + r * 0.12f, c.y + r * 0.55f), r * 0.05f)
        drawRect(tinta, Offset(c.x + r * 0.38f, c.y - r * 0.1f), Size(r * 0.18f, r * 0.2f), style = Stroke(r * 0.045f))
        drawLine(tinta, Offset(c.x - r * 0.55f, c.y - r * 0.12f), Offset(c.x - r * 0.4f, c.y + r * 0.12f), r * 0.05f)
        drawLine(tinta, Offset(c.x - r * 0.4f, c.y - r * 0.12f), Offset(c.x - r * 0.55f, c.y + r * 0.12f), r * 0.05f)
    }
    // El cordón rojo que la ata.
    drawLine(cordon, Offset(c.x, c.y + r * 0.86f), Offset(c.x, c.y + r * 1.0f), r * 0.08f)
}

/**
 * El sello hanko de bermellón, un poco torcido como al estamparlo a mano: el bloque con sus huecos de tinta, el marco en
 * reserva y el carácter 足 («pie», la huella) en escritura de sello, simplificado. Cada tanto se hunde, como al sellar.
 */
private fun DrawScope.hanko(c: Offset, r: Float, rojo: Color, fondo: Color, t: Float) {
    val golpe = 1f - 0.05f * maxOf(0f, sin(2 * PI * t).toFloat())
    withTransform({ scale(golpe, golpe, c); rotate(-4f, c) }) {
        val s = r * 0.8f
        drawRoundRect(rojo, Offset(c.x - s, c.y - s), Size(2 * s, 2 * s), CornerRadius(s * 0.12f))
        // Huecos donde la tinta no agarró.
        listOf(Offset(-0.86f, -0.4f) to 0.05f, Offset(0.7f, 0.88f) to 0.06f, Offset(0.9f, -0.75f) to 0.035f, Offset(-0.5f, 0.92f) to 0.04f, Offset(0.15f, -0.9f) to 0.03f)
            .forEach { (o, k) -> drawCircle(fondo.copy(alpha = 0.5f), s * k, c + o * s) }
        drawRoundRect(fondo, Offset(c.x - s * 0.8f, c.y - s * 0.8f), Size(s * 1.6f, s * 1.6f), CornerRadius(s * 0.06f), style = Stroke(s * 0.08f))
        val g = Stroke(s * 0.1f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // 口 arriba; abajo el tallo, el trazo corto a la derecha y las dos patas.
        drawRect(fondo, Offset(c.x - s * 0.3f, c.y - s * 0.6f), Size(s * 0.6f, s * 0.36f), style = g)
        drawLine(fondo, Offset(c.x, c.y - s * 0.12f), Offset(c.x, c.y + s * 0.5f), g.width, StrokeCap.Round)
        drawLine(fondo, Offset(c.x, c.y + s * 0.12f), Offset(c.x + s * 0.34f, c.y + s * 0.12f), g.width, StrokeCap.Round)
        drawPath(Path().apply { moveTo(c.x - s * 0.08f, c.y + s * 0.22f); quadraticTo(c.x - s * 0.22f, c.y + s * 0.5f, c.x - s * 0.52f, c.y + s * 0.58f) }, fondo, style = g)
        drawPath(Path().apply { moveTo(c.x, c.y + s * 0.5f); quadraticTo(c.x + s * 0.3f, c.y + s * 0.6f, c.x + s * 0.56f, c.y + s * 0.56f) }, fondo, style = g)
    }
}

/** Estrella de cinco puntas dentro de un engrane que gira despacio. */
private fun DrawScope.estrellaEngrane(c: Offset, r: Float, rojo: Color, metal: Color, t: Float) {
    rotate(t * 360f, c) {
        val dientes = 12
        val eng = Path()
        for (i in 0 until dientes * 4) {
            val a = rad(360.0 * i / (dientes * 4))
            val rr = if ((i / 2) % 2 == 0) r * 0.96f else r * 0.8f
            val p = c.mas(rr, a); if (i == 0) eng.moveTo(p.x, p.y) else eng.lineTo(p.x, p.y)
        }
        eng.close()
        drawPath(eng, metal, style = Stroke(r * 0.07f))
        drawCircle(metal, r * 0.68f, c, style = Stroke(r * 0.04f))
    }
    val est = Path()
    for (i in 0 until 10) {
        val rr = if (i % 2 == 0) r * 0.6f else r * 0.24f
        val p = c.mas(rr, rad(-90.0 + 36.0 * i)); if (i == 0) est.moveTo(p.x, p.y) else est.lineTo(p.x, p.y)
    }
    est.close()
    drawPath(est, rojo)
    // Mitad en sombra, como en los carteles.
    clipPath(Path().apply { addRect(Rect(c.x, c.y - r, c.x + r, c.y + r)) }) { drawPath(est, Color.Black.copy(alpha = 0.18f)) }
}

/** Corona de laurel: dos ramas de siete hojas que se cierran arriba. */
private fun DrawScope.laurel(c: Offset, r0: Float, color: Color, t: Float) {
    val r = r0 * 0.78f
    val brillo = 0.82f + 0.18f * sin(2 * PI * t).toFloat()
    for (lado in listOf(1f, -1f)) {
        val tallo = Path()
        for (i in 0..24) {
            val a = rad(90.0 - lado * (18 + i * 5.6))
            val p = c.mas(r, a)
            if (i == 0) tallo.moveTo(p.x, p.y) else tallo.lineTo(p.x, p.y)
        }
        drawPath(tallo, color.copy(alpha = brillo), style = Stroke(r0 * 0.06f, cap = StrokeCap.Round))
        for (i in 0 until 7) {
            val ang = 90.0 - lado * (26 + i * 19.5)
            val a = rad(ang)
            val afuera = if (i % 2 == 0) 1f else -1f
            val p = c.mas(r + afuera * r0 * 0.13f, a)
            // La hoja sigue la rama hacia arriba y se abre un poco hacia afuera o hacia adentro.
            val dir = if (lado > 0) Offset(sin(a), -cos(a)) else Offset(-sin(a), cos(a))
            val grados = Math.toDegrees(atan2(dir.y, dir.x).toDouble()).toFloat() + afuera * lado * 28f
            val largo = r0 * 0.36f * (1f - i * 0.05f); val ancho = largo * 0.42f
            rotate(grados, p) { drawOval(color.copy(alpha = brillo), Offset(p.x - largo / 2, p.y - ancho / 2), Size(largo, ancho)) }
        }
    }
    // Cinta abajo.
    drawLine(color, c.mas(r, rad(80.0)), c.mas(r * 1.18f, rad(105.0)), r0 * 0.07f, StrokeCap.Round)
    drawLine(color, c.mas(r, rad(100.0)), c.mas(r * 1.18f, rad(75.0)), r0 * 0.07f, StrokeCap.Round)
}

/**
 * La silueta de la loba de la placa (de pie, mirando a la derecha, con la cola larga), centrada en [c] y de medio
 * ancho [k]. La usan el emblema de Roma, la libreta y los trofeos.
 */
internal fun lobaRuta(c: Offset, k: Float): Path = Path().apply {
    fun p(x: Float, y: Float) = Offset(c.x + x * k, c.y + y * k)
    val a = p(-1f, -0.12f); moveTo(a.x, a.y)
    p(-0.72f, -0.3f).let { lineTo(it.x, it.y) }
    val q1 = p(-0.2f, -0.44f); val e1 = p(0.36f, -0.3f); quadraticTo(q1.x, q1.y, e1.x, e1.y)
    listOf(p(0.55f, -0.44f), p(0.6f, -0.64f), p(0.7f, -0.48f), p(0.78f, -0.62f), p(0.82f, -0.42f), p(1f, -0.32f), p(0.98f, -0.22f),
        p(0.75f, -0.2f), p(0.63f, 0f), p(0.6f, 0.52f), p(0.49f, 0.52f), p(0.46f, 0.1f)).forEach { lineTo(it.x, it.y) }
    // El vientre, con las ubres de la loba que amamantó a los gemelos.
    val q2 = p(0.05f, 0.26f); val e2 = p(-0.36f, 0.1f); quadraticTo(q2.x, q2.y, e2.x, e2.y)
    listOf(p(-0.44f, 0.52f), p(-0.56f, 0.52f), p(-0.62f, 0.04f), p(-0.74f, -0.12f), p(-0.98f, -0.04f)).forEach { lineTo(it.x, it.y) }
    close()
}

/**
 * Roma de hoy: la placa de mármol de una esquina, con su marco, la loba arriba, dos renglones de nombre de calle y los
 * tornillos que la sujetan. Un reflejo de sol la cruza despacio, como cuando pasa la luz entre los edificios.
 */
private fun DrawScope.placaLoba(c: Offset, r: Float, tinta: Color, rojo: Color, marmol: Color, t: Float) {
    val placa = Rect(c.x - r * 0.96f, c.y - r * 0.68f, c.x + r * 0.96f, c.y + r * 0.68f)
    drawRect(marmol, placa.topLeft, placa.size)
    drawRect(tinta, placa.topLeft, placa.size, style = Stroke(r * 0.08f))
    drawRect(tinta.copy(alpha = 0.6f), placa.topLeft + Offset(r * 0.13f, r * 0.13f), Size(placa.width - r * 0.26f, placa.height - r * 0.26f), style = Stroke(r * 0.035f))
    drawPath(lobaRuta(Offset(c.x, c.y - r * 0.22f), r * 0.34f), rojo)
    drawLine(tinta, Offset(c.x - r * 0.56f, c.y + r * 0.16f), Offset(c.x + r * 0.56f, c.y + r * 0.16f), r * 0.1f)
    drawLine(tinta.copy(alpha = 0.7f), Offset(c.x - r * 0.38f, c.y + r * 0.36f), Offset(c.x + r * 0.38f, c.y + r * 0.36f), r * 0.07f)
    listOf(placa.left + r * 0.07f, placa.right - r * 0.07f).forEach { x -> drawCircle(tinta, r * 0.045f, Offset(x, c.y)) }
    // El reflejo: una franja clara que cruza en diagonal y vuelve a empezar.
    val x = placa.left - r + (placa.width + 2 * r) * t
    clipRect(placa.left, placa.top, placa.right, placa.bottom) {
        drawPath(Path().apply { moveTo(x, placa.top); lineTo(x + r * 0.3f, placa.top); lineTo(x - r * 0.2f, placa.bottom); lineTo(x - r * 0.5f, placa.bottom); close() }, Color.White.copy(alpha = 0.22f))
    }
}

/**
 * Terminal: una ventana de consola de fósforo con su barra de título, dos renglones ya escritos, líneas de barrido y,
 * abajo, el indicador «>» que teclea una orden de seis caracteres y deja parpadear el cursor de bloque. De vez en cuando
 * la imagen se corre un instante, como un monitor viejo.
 */
private fun DrawScope.consola(c: Offset, r: Float, fosforo: Color, falla: Color, t: Float) {
    val marco = Rect(c.x - r * 0.95f, c.y - r * 0.78f, c.x + r * 0.95f, c.y + r * 0.78f)
    val esquina = CornerRadius(r * 0.12f)
    drawRoundRect(fosforo.copy(alpha = 0.1f), marco.topLeft, marco.size, esquina)
    if (t in 0.9f..0.93f) drawRoundRect(falla.copy(alpha = 0.6f), marco.topLeft + Offset(r * 0.07f, 0f), marco.size, esquina, style = Stroke(r * 0.05f))
    drawRoundRect(fosforo, marco.topLeft, marco.size, esquina, style = Stroke(r * 0.07f))
    // Barra de título con tres botoncitos cuadrados a la derecha.
    val barra = marco.top + r * 0.3f
    drawLine(fosforo, Offset(marco.left, barra), Offset(marco.right, barra), r * 0.05f)
    for (i in 0 until 3) drawRect(fosforo.copy(alpha = 0.7f), Offset(marco.right - r * (0.24f + i * 0.17f), marco.top + r * 0.1f), Size(r * 0.1f, r * 0.1f))
    // Líneas de barrido del tubo.
    clipRect(left = marco.left, top = barra, right = marco.right, bottom = marco.bottom) {
        var y = barra + r * 0.04f
        while (y < marco.bottom) { drawLine(fosforo.copy(alpha = 0.08f), Offset(marco.left, y), Offset(marco.right, y), r * 0.02f); y += r * 0.08f }
    }
    val g = r * 0.075f   // alto de un carácter
    val ch = r * 0.11f   // ancho de un carácter
    val x0 = marco.left + r * 0.16f
    // Dos renglones ya escritos: tramos de «caracteres» separados por espacios.
    listOf(barra + r * 0.22f to listOf(3, 1, 4), barra + r * 0.46f to listOf(2, 5)).forEach { (y, tramos) ->
        var x = x0
        tramos.forEach { n -> drawLine(fosforo.copy(alpha = 0.55f), Offset(x, y), Offset(x + ch * n - ch * 0.3f, y), g); x += ch * (n + 1) }
    }
    // El indicador «>» y la orden que se va tecleando; luego el cursor espera parpadeando.
    val y = barra + r * 0.76f
    drawLine(fosforo, Offset(x0, y - r * 0.1f), Offset(x0 + r * 0.1f, y), g * 0.9f, StrokeCap.Square)
    drawLine(fosforo, Offset(x0 + r * 0.1f, y), Offset(x0, y + r * 0.1f), g * 0.9f, StrokeCap.Square)
    val escritos = (t * 9).toInt().coerceAtMost(6)
    val xi = x0 + r * 0.26f
    for (k in 0 until escritos) drawLine(fosforo, Offset(xi + k * ch, y), Offset(xi + k * ch + ch * 0.7f, y), g)
    if ((t * 8).toInt() % 2 == 0) drawRect(fosforo, Offset(xi + escritos * ch, y - r * 0.11f), Size(ch * 0.75f, r * 0.22f))
}

/**
 * El letrero de neón de un puesto de fideos, colgado de dos cadenas: el tubo ámbar del marco, el tazón magenta con sus
 * palillos y el vapor verde que sube ondulando. El marco parpadea de vez en cuando, como los letreros viejos, y el letrero
 * se mece apenas.
 */
private fun DrawScope.letreroNeon(c: Offset, r: Float, magenta: Color, ambar: Color, verde: Color, t: Float) {
    val vaiven = sin(t * 2 * PI).toFloat() * 2.5f
    rotate(vaiven, Offset(c.x, c.y - r)) {
        val marco = Rect(c.x - r * 0.82f, c.y - r * 0.55f, c.x + r * 0.82f, c.y + r * 0.78f)
        // Las cadenas.
        listOf(marco.left + r * 0.25f, marco.right - r * 0.25f).forEach { x ->
            var y = c.y - r; while (y < marco.top) { drawOval(ambar.copy(alpha = 0.5f), Offset(x - r * 0.03f, y), Size(r * 0.06f, r * 0.1f), style = Stroke(r * 0.02f)); y += maxOf(r * 0.09f, 1f) }
        }
        drawRoundRect(Color.Black.copy(alpha = 0.35f), marco.topLeft, marco.size, CornerRadius(r * 0.12f))
        // El marco: se apaga un instante.
        val apagado = t in 0.42f..0.46f || t in 0.5f..0.52f
        val am = if (apagado) ambar.copy(alpha = 0.25f) else ambar
        drawRoundRect(am.copy(alpha = am.alpha * 0.3f), marco.topLeft, marco.size, CornerRadius(r * 0.12f), style = Stroke(r * 0.14f))
        drawRoundRect(am, marco.topLeft, marco.size, CornerRadius(r * 0.12f), style = Stroke(r * 0.045f))
        // El tazón.
        val tazon = Path().apply {
            moveTo(c.x - r * 0.5f, c.y + r * 0.05f); lineTo(c.x + r * 0.5f, c.y + r * 0.05f)
            quadraticTo(c.x + r * 0.45f, c.y + r * 0.55f, c.x, c.y + r * 0.58f); quadraticTo(c.x - r * 0.45f, c.y + r * 0.55f, c.x - r * 0.5f, c.y + r * 0.05f)
        }
        drawPath(tazon, magenta.copy(alpha = 0.3f), style = Stroke(r * 0.13f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(tazon, magenta, style = Stroke(r * 0.045f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Los palillos.
        listOf(0f, r * 0.09f).forEach { d ->
            drawLine(magenta, Offset(c.x + r * 0.08f + d, c.y - r * 0.02f), Offset(c.x + r * 0.55f + d, c.y - r * 0.38f), r * 0.035f, StrokeCap.Round)
        }
        // El vapor, que ondula con el tiempo.
        for (k in 0 until 3) {
            val x0 = c.x - r * 0.25f + k * r * 0.2f
            val fase = (t * 2 * PI + k).toFloat()
            val vapor = Path().apply {
                moveTo(x0, c.y - r * 0.02f)
                cubicTo(x0 - r * 0.08f * sin(fase), c.y - r * 0.15f, x0 + r * 0.08f * sin(fase), c.y - r * 0.28f, x0, c.y - r * 0.42f)
            }
            drawPath(vapor, verde.copy(alpha = 0.25f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
            drawPath(vapor, verde, style = Stroke(r * 0.03f, cap = StrokeCap.Round))
        }
    }
}

/** El sol de Vergina: dieciséis rayos alternos, largos y cortos. */
internal fun DrawScope.sol(c: Offset, r: Float, color: Color) {
    for (i in 0 until 16) {
        val ang = rad(i * 22.5 - 90)
        val largo = if (i % 2 == 0) r else r * 0.72f
        val ancho = rad(if (i % 2 == 0) 5.5 else 4.5)
        val base = r * 0.22f
        val p = Path()
        p.moveTo(c.x + base * cos(ang - ancho * 2.2f), c.y + base * sin(ang - ancho * 2.2f))
        p.lineTo(c.x + largo * cos(ang), c.y + largo * sin(ang))
        p.lineTo(c.x + base * cos(ang + ancho * 2.2f), c.y + base * sin(ang + ancho * 2.2f))
        p.close()
        drawPath(p, color)
    }
    drawCircle(color, r * 0.2f, c)
}

/**
 * El sol de Vergina como en la urna de oro de Egas: dieciséis rayos (ocho largos y ocho cortos) con la arista en relieve,
 * una mitad a la luz y otra en sombra, sobre un disco tenue de púrpura; al centro, un disco con su roseta de ocho pétalos.
 * Gira muy despacio, los rayos respiran uno tras otro y un brillo pasa de rayo en rayo.
 */
private fun DrawScope.solVergina(c: Offset, r: Float, oro: Color, purpura: Color, t: Float) {
    drawCircle(purpura.copy(alpha = 0.16f), r * 0.98f, c)
    val claro = lerp(oro, Color.White, 0.25f); val sombra = lerp(oro, Color.Black, 0.2f)
    val brilla = (t * 32).toInt() % 16
    rotate(t * 360f, c) {
        for (i in 0 until 16) {
            val ang = rad(i * 22.5 - 90)
            val respira = 1f + 0.05f * sin(2 * PI * (t * 10 + i / 16.0)).toFloat()
            val largo = (if (i % 2 == 0) r * 0.93f else r * 0.68f) * respira
            val abre = rad(if (i % 2 == 0) 12.0 else 10.0)
            val base = r * 0.26f
            val izq = c.mas(base, ang - abre); val der = c.mas(base, ang + abre); val punta = c.mas(largo, ang); val eje = c.mas(base, ang)
            drawPath(Path().apply { moveTo(izq.x, izq.y); lineTo(punta.x, punta.y); lineTo(eje.x, eje.y); close() }, if (i == brilla) lerp(oro, Color.White, 0.6f) else claro)
            drawPath(Path().apply { moveTo(eje.x, eje.y); lineTo(punta.x, punta.y); lineTo(der.x, der.y); close() }, sombra)
        }
    }
    // El disco central con su roseta.
    drawCircle(oro, r * 0.27f, c)
    drawCircle(sombra, r * 0.27f, c, style = Stroke(r * 0.03f))
    for (i in 0 until 8) drawCircle(claro, r * 0.055f, c.mas(r * 0.15f, rad(45.0 * i + t * 360.0)))
    drawCircle(sombra, r * 0.07f, c)
}

/**
 * El ojo udyat, el ojo protector de Horus, en lapislázuli sobre un disco de oro: la ceja, el ojo almendrado con su pupila,
 * la línea que se alarga hacia la sien, la gota bajo el ojo y la espiral de la mejilla. Parpadea tres veces por vuelta y un
 * brillo recorre el canto del disco.
 */
private fun DrawScope.ojoUdyat(c: Offset, r: Float, oro: Color, lapis: Color, t: Float) {
    drawCircle(oro.copy(alpha = 0.18f), r * 0.98f, c)
    drawCircle(oro.copy(alpha = 0.75f), r * 0.98f, c, style = Stroke(r * 0.05f))
    val fase = (t * 3f) % 1f
    val abierto = if (fase > 0.94f) 1f - 0.85f * (1f - abs(fase - 0.97f) / 0.03f).coerceIn(0f, 1f) else 1f
    val g = r * 0.075f
    val tr = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val ojo = Path().apply {
        moveTo(c.x - r * 0.62f, c.y - r * 0.05f)
        quadraticTo(c.x - r * 0.1f, c.y - r * 0.42f * abierto, c.x + r * 0.48f, c.y - r * 0.08f)
        quadraticTo(c.x - r * 0.05f, c.y + r * 0.22f * abierto, c.x - r * 0.62f, c.y - r * 0.05f); close()
    }
    drawPath(ojo, Color.White.copy(alpha = 0.55f))
    if (abierto > 0.4f) drawCircle(lapis, r * 0.16f * abierto, Offset(c.x - r * 0.06f, c.y - r * 0.08f))
    drawPath(ojo, lapis, style = tr)
    drawLine(lapis, Offset(c.x + r * 0.45f, c.y - r * 0.08f), Offset(c.x + r * 0.78f, c.y - r * 0.1f), g, StrokeCap.Round)
    drawPath(Path().apply { moveTo(c.x - r * 0.64f, c.y - r * 0.36f); quadraticTo(c.x - r * 0.05f, c.y - r * 0.66f, c.x + r * 0.72f, c.y - r * 0.4f) }, lapis, style = Stroke(g * 1.4f, cap = StrokeCap.Round))
    drawLine(lapis, Offset(c.x - r * 0.18f, c.y + r * 0.12f), Offset(c.x - r * 0.22f, c.y + r * 0.62f), g, StrokeCap.Round)
    drawPath(Path().apply {
        moveTo(c.x + r * 0.02f, c.y + r * 0.14f); quadraticTo(c.x + r * 0.3f, c.y + r * 0.7f, c.x + r * 0.52f, c.y + r * 0.46f)
        quadraticTo(c.x + r * 0.6f, c.y + r * 0.3f, c.x + r * 0.44f, c.y + r * 0.32f)
    }, lapis, style = tr)
    val a = rad(t * 360.0 - 90.0)
    drawCircle(Color.White.copy(alpha = 0.55f), r * 0.06f, c.mas(r * 0.98f, a))
}

/**
 * Rosetón gótico de iluminador: ocho pétalos apuntados de vidrio azul y rojo entre tracerías de oro, tréboles en los huecos,
 * un anillo de piedra con dieciséis lóbulos y una cuadrifolia al centro. La luz da la vuelta al rosetón, pétalo por pétalo.
 */
private fun DrawScope.roseton(c: Offset, r: Float, rojo: Color, azul: Color, oro: Color, t: Float) {
    for (i in 0 until 8) {
        val a = rad(45.0 * i - 90)
        val luz = 0.5f + 0.5f * cos(2 * PI * (t - i / 8.0)).toFloat()
        val ini = c.mas(r * 0.3f, a); val punta = c.mas(r * 0.82f, a); val medio = c.mas(r * 0.56f, a)
        val n = Offset(-sin(a), cos(a)) * (r * 0.32f)
        val petalo = Path().apply {
            moveTo(ini.x, ini.y); quadraticTo(medio.x + n.x, medio.y + n.y, punta.x, punta.y); quadraticTo(medio.x - n.x, medio.y - n.y, ini.x, ini.y); close()
        }
        drawPath(petalo, (if (i % 2 == 0) azul else rojo).copy(alpha = 0.35f + 0.55f * luz))
        drawPath(petalo, oro, style = Stroke(r * 0.04f))
        // Trébol en el hueco entre dos pétalos.
        val h = c.mas(r * 0.7f, a + rad(22.5))
        for (k in 0 until 3) drawCircle(oro.copy(alpha = 0.85f), r * 0.045f, h.mas(r * 0.05f, a + rad(22.5 + 120.0 * k)))
    }
    // Anillo de piedra con sus lóbulos.
    drawCircle(rojo, r * 0.93f, c, style = Stroke(r * 0.07f))
    for (i in 0 until 16) drawCircle(oro, r * 0.055f, c.mas(r * 0.84f, rad(22.5 * i)), style = Stroke(r * 0.025f))
    // Cuadrifolia al centro.
    val luzC = 0.6f + 0.4f * sin(2 * PI * t).toFloat()
    for (k in 0 until 4) drawCircle(rojo.copy(alpha = 0.45f + 0.5f * luzC), r * 0.11f, c.mas(r * 0.1f, rad(90.0 * k)))
    for (k in 0 until 4) drawCircle(oro, r * 0.11f, c.mas(r * 0.1f, rad(90.0 * k)), style = Stroke(r * 0.03f))
    drawCircle(azul, r * 0.06f, c)
}

/**
 * La cruz patada de los templarios: cuatro brazos que se ensanchan hacia la punta, con el remate un poco cóncavo. Cabe en
 * un círculo de radio [r]; [centro] y [punta] son la media anchura del brazo junto al centro y en la punta (en fracción de [r]).
 */
internal fun cruzPatada(c: Offset, r: Float, centro: Float = 0.13f, punta: Float = 0.4f): Path = Path().apply {
    for (k in 0 until 4) {
        val a = rad(90.0 * k - 90)
        val d = Offset(cos(a), sin(a)); val n = Offset(-sin(a), cos(a))
        fun p(f: Float, s: Float) = c + d * (r * f) + n * (r * s)
        val p0 = p(centro, -centro); val p1 = p(1f, -punta); val q = p(0.86f, 0f); val p2 = p(1f, punta)
        if (k == 0) moveTo(p0.x, p0.y) else lineTo(p0.x, p0.y)
        lineTo(p1.x, p1.y); quadraticTo(q.x, q.y, p2.x, p2.y)
    }
    close()
}

/**
 * Templario: el escudo de lágrima de los caballeros del siglo XII, de lino blanco con la cruz patada roja y ribeteado de
 * hierro con sus clavos. La cruz late despacio, como a la luz de una antorcha, y un brillo recorre el borde de hierro.
 */
private fun DrawScope.escudoTemplario(c: Offset, r: Float, rojo: Color, hierro: Color, arena: Color, blanco: Color, t: Float) {
    val escudo = Path().apply {
        moveTo(c.x - r * 0.78f, c.y - r * 0.86f); lineTo(c.x + r * 0.78f, c.y - r * 0.86f)
        quadraticTo(c.x + r * 0.84f, c.y + r * 0.3f, c.x, c.y + r * 0.98f)
        quadraticTo(c.x - r * 0.84f, c.y + r * 0.3f, c.x - r * 0.78f, c.y - r * 0.86f); close()
    }
    drawPath(escudo, blanco)
    drawPath(escudo, Brush.verticalGradient(listOf(Color.Transparent, arena.copy(alpha = 0.16f)), startY = c.y - r, endY = c.y + r))
    val luz = 0.5f + 0.5f * sin(2 * PI * t).toFloat()
    drawPath(cruzPatada(c + Offset(0f, -r * 0.1f), r * 0.6f), rojo.copy(alpha = 0.82f + 0.18f * luz))
    drawPath(escudo, hierro, style = Stroke(r * 0.1f))
    // Los clavos del ribete.
    listOf(Offset(-0.6f, -0.86f), Offset(0f, -0.86f), Offset(0.6f, -0.86f), Offset(-0.74f, -0.3f), Offset(0.74f, -0.3f), Offset(-0.52f, 0.42f), Offset(0.52f, 0.42f), Offset(0f, 0.9f))
        .forEach { o -> drawCircle(arena, r * 0.045f, c + o * r) }
    // El brillo que corre por el borde de arriba, de un lado al otro.
    val bx = c.x + r * (-0.78f + 1.56f * t); val by = c.y - r * 0.86f
    drawCircle(Brush.radialGradient(listOf(blanco.copy(alpha = 0.7f), Color.Transparent), center = Offset(bx, by), radius = r * 0.16f), r * 0.16f, Offset(bx, by))
}

/** Un ojo dentro de un triángulo que mira de un lado a otro y parpadea. */
private fun DrawScope.ojo(c: Offset, r: Float, tinta: Color, rojo: Color, fondo: Color, t: Float) {
    val tri = Path().apply {
        moveTo(c.x, c.y - r * 0.95f); lineTo(c.x + r * 0.98f, c.y + r * 0.72f); lineTo(c.x - r * 0.98f, c.y + r * 0.72f); close()
    }
    drawPath(tri, tinta, style = Stroke(r * 0.08f))
    for (k in -2..2) {  // rayos
        val a = rad(-90.0 + k * 28)
        drawLine(tinta.copy(alpha = 0.6f), c.mas(r * 1.0f, a) + Offset(0f, -r * 0.05f), c.mas(r * 1.0f + r * 0.12f, a) + Offset(0f, -r * 0.05f), r * 0.05f)
    }
    val ec = c + Offset(0f, r * 0.18f)
    val ancho = r * 0.58f
    // Parpadeo: el párpado se cierra un instante cerca del final del ciclo.
    val abierto = (abs(t - 0.82f) / 0.03f).coerceIn(0f, 1f)
    val alto = r * 0.3f * abierto + r * 0.01f
    val almendra = Path().apply {
        moveTo(ec.x - ancho, ec.y); quadraticTo(ec.x, ec.y - alto * 2, ec.x + ancho, ec.y); quadraticTo(ec.x, ec.y + alto * 2, ec.x - ancho, ec.y); close()
    }
    drawPath(almendra, fondo)
    clipPath(almendra) {
        val mira = sin(2 * PI * t * 2).toFloat() * r * 0.2f
        drawCircle(rojo, r * 0.25f, ec + Offset(mira, 0f))
        drawCircle(tinta, r * 0.11f, ec + Offset(mira, 0f))
        drawCircle(Color.White.copy(alpha = 0.7f), r * 0.04f, ec + Offset(mira + r * 0.06f, -r * 0.06f))
    }
    drawPath(almendra, tinta, style = Stroke(r * 0.07f))
}

/**
 * Átomo de la era atómica: un destello de ocho puntas detrás, como en los anuncios de los años cincuenta, tres órbitas,
 * un núcleo de protones y neutrones que late, y tres electrones que dejan una estela al girar.
 */
private fun DrawScope.atomo(c: Offset, r: Float, orbita: Color, electron: Color, t: Float) {
    for (i in 0 until 8) {
        val an = rad(45.0 * i + 22.5)
        val largo = if (i % 2 == 0) r * 0.98f else r * 0.7f
        val p1 = c.mas(r * 0.12f, an - 0.35f); val p2 = c.mas(largo, an); val p3 = c.mas(r * 0.12f, an + 0.35f)
        drawPath(Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close() }, electron.copy(alpha = 0.2f))
    }
    val a = r * 0.9f; val b = r * 0.32f
    fun pos(k: Int, th: Double): Offset {
        val px = a * cos(th).toFloat(); val py = b * sin(th).toFloat(); val g = rad(60.0 * k)
        return Offset(c.x + px * cos(g) - py * sin(g), c.y + px * sin(g) + py * cos(g))
    }
    for (k in 0 until 3) {
        rotate(60f * k, c) { drawOval(orbita, Offset(c.x - a, c.y - b), Size(a * 2, b * 2), style = Stroke(r * 0.06f)) }
        val th = 2 * PI * (t + k / 3.0)
        for (j in 4 downTo 1) drawCircle(electron.copy(alpha = 0.5f * (1f - j / 5f)), r * (0.1f - j * 0.012f), pos(k, th - j * 0.14))
        drawCircle(electron, r * 0.1f, pos(k, th))
    }
    val late = 1f + 0.06f * sin(2 * PI * t * 2).toFloat()
    listOf(Offset(-1f, -0.8f), Offset(1f, -0.6f), Offset(-0.7f, 1f), Offset(0.9f, 0.9f)).forEachIndexed { i, o ->
        drawCircle(if (i % 2 == 0) electron else orbita, r * 0.1f * late, c + o * (r * 0.07f))
    }
}

/** Escudo de la Orden: una cruz de pistas de circuito con nodos de latón y una línea de escaneo. */
private fun DrawScope.escudo(c: Offset, r: Float, acero: Color, laton: Color, t: Float) {
    val w = r * 1.5f; val h = r * 1.9f
    val l = c.x - w / 2; val d = c.x + w / 2; val arriba = c.y - h / 2; val abajo = c.y + h / 2
    val p = Path().apply {
        moveTo(l, arriba); lineTo(d, arriba); lineTo(d, arriba + h * 0.45f)
        quadraticTo(d, arriba + h * 0.85f, c.x, abajo); quadraticTo(l, arriba + h * 0.85f, l, arriba + h * 0.45f); close()
    }
    drawPath(p, acero.copy(alpha = 0.14f), style = Fill)
    clipPath(p) { drawRect(laton.copy(alpha = 0.35f), Offset(l, arriba + h * t - r * 0.08f), Size(w, r * 0.12f)) }
    drawPath(p, acero, style = Stroke(r * 0.09f))
    val y = arriba + h * 0.38f
    val g = r * 0.07f
    drawLine(acero, Offset(c.x, arriba + h * 0.14f), Offset(c.x, abajo - h * 0.16f), g)
    drawLine(acero, Offset(l + w * 0.16f, y), Offset(d - w * 0.16f, y), g)
    drawLine(acero, Offset(c.x + w * 0.16f, y), Offset(c.x + w * 0.16f, y + h * 0.18f), g * 0.8f)
    listOf(Offset(c.x, arriba + h * 0.14f), Offset(c.x, abajo - h * 0.16f), Offset(l + w * 0.16f, y), Offset(d - w * 0.16f, y), Offset(c.x + w * 0.16f, y + h * 0.18f))
        .forEach { drawCircle(laton, r * 0.1f, it) }
    drawCircle(laton, r * 0.14f, Offset(c.x, y)); drawCircle(acero, r * 0.06f, Offset(c.x, y))
}

/**
 * Molécula de cuaderno de laboratorio: un anillo aromático con sus tres dobles enlaces y tres sustituyentes, dentro de una
 * escala graduada como la de un instrumento, con su aguja de lectura que da la vuelta. Los átomos vibran apenas y uno se enciende por turno.
 */
private fun DrawScope.molecula(c: Offset, r: Float, cian: Color, violeta: Color, verde: Color, t: Float) {
    for (i in 0 until 60) {
        val a = rad(6.0 * i)
        val largo = if (i % 5 == 0) r * 0.1f else r * 0.05f
        drawLine(cian.copy(alpha = if (i % 5 == 0) 0.6f else 0.3f), c.mas(r * 0.98f - largo, a), c.mas(r * 0.98f, a), r * 0.02f)
    }
    val lectura = rad(t * 360.0 - 90)
    drawLine(violeta, c.mas(r * 0.8f, lectura), c.mas(r * 0.98f, lectura), r * 0.04f, StrokeCap.Round)
    val rr = r * 0.42f
    val v = (0..5).map { c.mas(rr * (1f + 0.03f * sin(2 * PI * (t * 3 + it / 6.0)).toFloat()), rad(60.0 * it - 90)) }
    for (i in 0..5) drawLine(cian, v[i], v[(i + 1) % 6], r * 0.06f)
    // Dobles enlaces alternos, por dentro del anillo.
    for (i in listOf(0, 2, 4)) {
        val p = v[i] + (c - v[i]) * 0.2f; val q = v[(i + 1) % 6] + (c - v[(i + 1) % 6]) * 0.2f
        drawLine(cian.copy(alpha = 0.8f), p + (q - p) * 0.15f, q + (p - q) * 0.15f, r * 0.04f)
    }
    // Sustituyentes.
    val ext1 = c.mas(r * 0.74f, rad(-90.0)); val ext2 = c.mas(r * 0.74f, rad(30.0)); val ext3 = c.mas(r * 0.74f, rad(150.0))
    drawLine(cian, v[0], ext1, r * 0.05f); drawLine(cian, v[2], ext2, r * 0.05f); drawLine(cian, v[4], ext3, r * 0.05f)
    drawCircle(violeta, r * 0.1f, ext1); drawCircle(verde, r * 0.1f, ext2); drawCircle(violeta.copy(alpha = 0.7f), r * 0.08f, ext3)
    val activo = (t * 6).toInt() % 6
    v.forEachIndexed { i, p ->
        if (i == activo) drawCircle(violeta.copy(alpha = 0.3f), r * 0.16f, p)
        drawCircle(if (i == activo) violeta else cian, if (i == activo) r * 0.1f else r * 0.065f, p)
    }
}

/** Farol de colono con la llama que tiembla. */
private fun DrawScope.farol(c: Offset, r: Float, marco: Color, llama: Color, oro: Color, t: Float) {
    val tiembla = 0.86f + 0.09f * sin(2 * PI * t * 3).toFloat() + 0.05f * sin(2 * PI * t * 7).toFloat()
    drawCircle(llama.copy(alpha = 0.16f * tiembla), r * 0.62f, c + Offset(0f, r * 0.05f))
    drawCircle(marco, r * 0.12f, Offset(c.x, c.y - r * 0.86f), style = Stroke(r * 0.06f))
    val tapa = Path().apply {
        moveTo(c.x - r * 0.2f, c.y - r * 0.74f); lineTo(c.x + r * 0.2f, c.y - r * 0.74f); lineTo(c.x + r * 0.5f, c.y - r * 0.54f); lineTo(c.x - r * 0.5f, c.y - r * 0.54f); close()
    }
    drawPath(tapa, marco)
    val cuerpo = Rect(c.x - r * 0.4f, c.y - r * 0.54f, c.x + r * 0.4f, c.y + r * 0.6f)
    // Llama: una gota que sube y baja.
    val base = Offset(c.x, c.y + r * 0.32f)
    val fr = r * 0.17f; val alto = r * 0.62f * tiembla
    fun gota(escala: Float) = Path().apply {
        moveTo(base.x, base.y - alto * escala)
        cubicTo(base.x + fr * 1.6f * escala, base.y - alto * 0.45f * escala, base.x + fr * escala, base.y + fr * escala, base.x, base.y + fr * escala)
        cubicTo(base.x - fr * escala, base.y + fr * escala, base.x - fr * 1.6f * escala, base.y - alto * 0.45f * escala, base.x, base.y - alto * escala)
        close()
    }
    drawPath(gota(1f), llama); drawPath(gota(0.55f), oro)
    drawRect(marco, cuerpo.topLeft, cuerpo.size, style = Stroke(r * 0.07f))
    drawLine(marco, Offset(c.x - r * 0.14f, cuerpo.top), Offset(c.x - r * 0.14f, cuerpo.bottom), r * 0.03f)
    drawLine(marco, Offset(c.x + r * 0.14f, cuerpo.top), Offset(c.x + r * 0.14f, cuerpo.bottom), r * 0.03f)
    drawRect(marco, Offset(c.x - r * 0.52f, c.y + r * 0.6f), Size(r * 1.04f, r * 0.16f))
}

// =====================================================================
//  Bandas ornamentales de los temas nuevos (el romano, el helénico y Terminal están en Componentes)
// =====================================================================

/** Zarcillo de manuscrito: un tallo de oro que ondula, con hojas azules y puntos rojos. */
@Composable
internal fun Zarcillo(modifier: Modifier) {
    val oro = MaterialTheme.colorScheme.tertiary; val azul = MaterialTheme.colorScheme.secondary; val rojo = MaterialTheme.colorScheme.primary
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val mid = size.height / 2; val amp = size.height * 0.26f; val lam = 30.dp.toPx()
        val p = Path(); var x = 0f
        p.moveTo(0f, mid)
        while (x <= size.width) { p.lineTo(x, mid + amp * sin(2 * PI * x / lam).toFloat()); x += 2f }
        drawPath(p, oro, style = Stroke(1.4f))
        var k = 0; x = lam / 4
        while (x < size.width) {
            val arriba = k % 2 == 0
            val y = mid + if (arriba) -amp else amp
            if (k % 4 < 2) rotate(if (arriba) -35f else 35f, Offset(x, y)) {
                drawOval(azul, Offset(x - 5.dp.toPx() / 2, y - (if (arriba) 4.dp.toPx() else -1f)), Size(5.dp.toPx(), 3.dp.toPx()))
            } else drawCircle(rojo, 1.6.dp.toPx(), Offset(x, mid + if (arriba) amp * 1.9f else -amp * 1.9f))
            x += lam / 2; k++
        }
    }
}

/** Templario: el adarve de la muralla, con sus merlones de piedra y, cada tanto, un escudo con la cruz patada colgado del muro. */
@Composable
internal fun Adarve(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val piedra = e.onBackground.copy(alpha = 0.55f); val rojo = e.primary; val lino = e.surface
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val h = size.height; val m = 8.dp.toPx(); val base = h * 0.62f
        drawRect(piedra, Offset(0f, base), Size(size.width, h * 0.2f))
        var x = 0f; var k = 0
        while (x + m <= size.width) {
            if (k % 6 == 3) {
                val cx = x + m / 2
                val escudo = Path().apply {
                    moveTo(cx - m * 0.62f, 0.5f); lineTo(cx + m * 0.62f, 0.5f); quadraticTo(cx + m * 0.66f, h * 0.6f, cx, h - 0.5f); quadraticTo(cx - m * 0.66f, h * 0.6f, cx - m * 0.62f, 0.5f); close()
                }
                drawPath(escudo, lino); drawPath(escudo, piedra, style = Stroke(1.1f))
                drawPath(cruzPatada(Offset(cx, h * 0.36f), h * 0.26f), rojo)
            } else if (k % 2 == 0) drawRect(piedra, Offset(x, base - h * 0.42f), Size(m, h * 0.42f))
            x += maxOf(m, 1f); k++
        }
    }
}

/** Línea con destellos de cuatro puntas, como en los anuncios de la era atómica. */
@Composable
internal fun Destellos(modifier: Modifier) {
    val linea = MaterialTheme.colorScheme.primary; val estrella = MaterialTheme.colorScheme.secondary
    Canvas(modifier.fillMaxWidth().height(14.dp)) {
        val y = size.height / 2
        drawLine(linea.copy(alpha = 0.55f), Offset(0f, y), Offset(size.width, y), 1.5f)
        var x = 18.dp.toPx(); var k = 0
        while (x < size.width) {
            val s = if (k % 3 == 0) size.height * 0.5f else size.height * 0.3f
            val p = Path().apply {
                moveTo(x, y - s); quadraticTo(x, y, x + s, y); quadraticTo(x, y, x, y + s); quadraticTo(x, y, x - s, y); quadraticTo(x, y, x, y - s); close()
            }
            drawPath(p, if (k % 3 == 0) estrella else linea)
            x += 46.dp.toPx(); k++
        }
    }
}

/** Galones y remaches: chevrones carmesí y una línea de acero remachada en latón. */
@Composable
internal fun Galones(modifier: Modifier) {
    val rojo = MaterialTheme.colorScheme.primary; val laton = MaterialTheme.colorScheme.secondary; val acero = MaterialTheme.colorScheme.outline
    Canvas(modifier.fillMaxWidth().height(12.dp)) {
        val h = size.height; val y = h / 2
        val paso = h * 0.75f
        for (i in 0 until 5) {
            val x = i * paso
            val p = Path().apply { moveTo(x, 0f); lineTo(x + h * 0.4f, 0f); lineTo(x + h * 0.9f, y); lineTo(x + h * 0.4f, h); lineTo(x, h); lineTo(x + h * 0.5f, y); close() }
            drawPath(p, rojo)
        }
        val ini = 5 * paso + h
        drawRect(acero, Offset(ini, y - 1.5f), Size(size.width - ini, 3f))
        var x = ini + 8.dp.toPx()
        while (x < size.width) { drawCircle(laton, h * 0.2f, Offset(x, y)); x += 26.dp.toPx() }
    }
}

/** La vía del tren bajo el título: una locomotora chica con su penacho de vapor y, detrás, los carriles sobre sus traviesas. */
@Composable
internal fun BandaVia(modifier: Modifier) {
    val laton = MaterialTheme.colorScheme.primary; val hierro = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
    val traviesa = MaterialTheme.colorScheme.outline; val ladrillo = MaterialTheme.colorScheme.tertiary
    Canvas(modifier.fillMaxWidth().height(14.dp)) {
        val u = 1.dp.toPx(); val h = size.height; val base = h - 3 * u
        // La locomotora: caldera, chimenea, cabina, el faro de ladrillo y tres ruedas.
        drawRect(laton, Offset(4 * u, base - 7 * u), Size(16 * u, 5 * u))
        drawRect(laton, Offset(7 * u, base - 11 * u), Size(3 * u, 4 * u))
        drawRect(laton, Offset(20 * u, base - 10 * u), Size(7 * u, 8 * u))
        drawCircle(ladrillo, 1.4f * u, Offset(3.2f * u, base - 5 * u))
        listOf(8f, 14f, 22f).forEach { x -> drawCircle(hierro, 2.2f * u, Offset(x * u, base - 1 * u)) }
        listOf(Offset(9 * u, base - 13 * u) to 1.8f, Offset(13 * u, base - 13.5f * u) to 1.4f, Offset(16.5f * u, base - 12.5f * u) to 1f).forEach { (o, rr) ->
            drawCircle(hierro.copy(alpha = 0.3f), rr * u, o)
        }
        var x = 32 * u
        while (x < size.width) { drawRect(traviesa, Offset(x, base - 2.5f * u), Size(2 * u, 5 * u)); x += 7 * u }
        drawLine(hierro, Offset(30 * u, base - 1.5f * u), Offset(size.width, base - 1.5f * u), 1f * u)
        drawLine(hierro, Offset(30 * u, base + 1.5f * u), Offset(size.width, base + 1.5f * u), 1f * u)
    }
}

/** Rama de laurel dorada con una estrella de la Legión de Honor cada tanto. */
@Composable
internal fun Laureles(modifier: Modifier) {
    val oro = MaterialTheme.colorScheme.tertiary; val azul = MaterialTheme.colorScheme.primary
    Canvas(modifier.fillMaxWidth().height(14.dp)) {
        val y = size.height / 2
        drawLine(oro, Offset(0f, y), Offset(size.width, y), 1.4f)
        val paso = 11.dp.toPx(); var x = paso / 2; var k = 0
        val largo = 7.dp.toPx(); val ancho = 2.6.dp.toPx()
        while (x < size.width) {
            if (k % 12 == 6) {
                // Estrella de cinco brazos con el centro azul.
                val est = Path(); val cc = Offset(x, y)
                for (i in 0 until 10) { val rr = if (i % 2 == 0) ancho * 2.2f else ancho * 0.9f; val q = cc.mas(rr, rad(-90.0 + 36.0 * i)); if (i == 0) est.moveTo(q.x, q.y) else est.lineTo(q.x, q.y) }
                est.close(); drawPath(est, oro); drawCircle(azul, ancho * 0.6f, cc)
            } else {
                val arriba = k % 2 == 0
                rotate(if (arriba) -30f else 30f, Offset(x, y)) {
                    drawOval(oro, Offset(x, y - ancho / 2 + if (arriba) -ancho * 0.6f else ancho * 0.6f), Size(largo, ancho))
                }
            }
            x += paso; k++
        }
    }
}

/** Divisor del cuaderno de naturalista: una ramita a tinta por la que pasa una fila de hormigas, y un escarabajo de vez en cuando. */
@Composable
internal fun Enredadera(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val mid = size.height / 2; val amp = size.height * 0.12f; val lam = 80.dp.toPx()
        val p = Path(); var x = 0f
        p.moveTo(0f, mid)
        while (x <= size.width) { p.lineTo(x, mid + amp * sin(2 * PI * x / lam).toFloat()); x += 2f }
        drawPath(p, e.onBackground.copy(alpha = 0.55f), style = Stroke(1.2f))
        var k = 0; x = 10.dp.toPx()
        val l = 7.dp.toPx()
        while (x < size.width - l) {
            val y = mid + amp * sin(2 * PI * x / lam).toFloat()
            val a = atan2((amp * 2 * PI / lam * cos(2 * PI * x / lam)).toFloat(), 1f)
            if (k % 11 == 6) {
                // Escarabajo.
                rotate(Math.toDegrees(a.toDouble()).toFloat() + 90f, Offset(x, y - 1f)) {
                    drawOval(e.tertiary, Offset(x - 2.6.dp.toPx(), y - 3.4.dp.toPx()), Size(5.2.dp.toPx(), 6.dp.toPx()))
                    drawLine(e.onBackground.copy(alpha = 0.7f), Offset(x, y - 3.dp.toPx()), Offset(x, y + 2.4.dp.toPx()), 1f)
                    drawCircle(e.onBackground, 1.3.dp.toPx(), Offset(x, y - 4.dp.toPx()))
                }
                x += l * 2.4f
            } else {
                hormiga(Offset(x, y - 0.5.dp.toPx()), l, a, (if (k % 5 == 2) e.secondary else e.onBackground).copy(alpha = 0.8f))
                x += l * 1.9f
            }
            k++
        }
    }
}

/** Divisor de la Ciudad verde: la línea discontinua de una calle vieja, partida por grietas, con hierba y hiedra que brotan entre los tramos. */
@Composable
internal fun BandaCalle(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val mid = size.height * 0.62f; val tramo = 18.dp.toPx(); val hueco = 12.dp.toPx(); val g = 2.2.dp.toPx()
        val pintura = e.tertiary.copy(alpha = 0.7f)
        drawLine(e.onBackground.copy(alpha = 0.18f), Offset(0f, size.height - 1f), Offset(size.width, size.height - 1f), 1f)
        var x = 4.dp.toPx(); var k = 0
        while (x < size.width) {
            val fin = (x + tramo).coerceAtMost(size.width)
            // La pintura gastada: uno de cada cuatro tramos está partido por una grieta.
            if (k % 4 == 1) {
                val m = (x + fin) / 2
                drawLine(pintura, Offset(x, mid), Offset(m - 1.5.dp.toPx(), mid), g)
                drawLine(pintura, Offset(m + 1.5.dp.toPx(), mid), Offset(fin, mid), g)
                val gr = Path().apply { moveTo(m, mid - 5.dp.toPx()); lineTo(m - 1.dp.toPx(), mid - 1.dp.toPx()); lineTo(m + 1.dp.toPx(), mid + 1.dp.toPx()); lineTo(m - 0.5.dp.toPx(), mid + 5.dp.toPx()) }
                drawPath(gr, e.onBackground.copy(alpha = 0.5f), style = Stroke(0.8.dp.toPx()))
            } else drawLine(pintura, Offset(x, mid), Offset(fin, mid), g)
            // En el hueco, una mata de hierba o, cada tres, una hoja de hiedra.
            val cx = fin + hueco / 2
            if (cx < size.width) {
                if (k % 3 == 2) hojaHiedraEmblema(Offset(cx, mid + 1.dp.toPx()), 6.dp.toPx(), e.primary.copy(alpha = 0.85f))
                else for (j in -1..1) {
                    drawLine(e.primary.copy(alpha = 0.8f), Offset(cx + j * 1.2.dp.toPx(), mid + 1.dp.toPx()), Offset(cx + j * 2.6.dp.toPx(), mid - (if (j == 0) 4.5f else 3.5f).dp.toPx()), 1.1f)
                }
            }
            x = fin + hueco; k++
        }
    }
}

/** Arquería gótica: una fila de arcos apuntados con trébol, y en cada uno un vidrio que brilla. */
@Composable
internal fun Arqueria(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val oscuro = e.background.luminance() < 0.5f
    val piedra = if (oscuro) e.primary else e.onBackground.copy(alpha = 0.7f)
    val vidrios = listOf(e.secondary, if (oscuro) e.tertiary else e.primary, Color(0xFF2E8B57), Color(0xFFE2B34A))
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val h = size.height; val a = 16.dp.toPx(); var x = 0f; var k = 0
        drawLine(piedra, Offset(0f, h - 1f), Offset(size.width, h - 1f), 1.4f)
        while (x + a <= size.width) {
            val arco = Path().apply {
                moveTo(x + 2f, h - 1f); lineTo(x + 2f, h * 0.55f)
                quadraticTo(x + 2f, h * 0.12f, x + a / 2, 1f); quadraticTo(x + a - 2f, h * 0.12f, x + a - 2f, h * 0.55f); lineTo(x + a - 2f, h - 1f)
            }
            drawPath(arco, vidrios[k % vidrios.size].copy(alpha = 0.32f))
            drawPath(arco, piedra, style = Stroke(1.2f))
            drawCircle(vidrios[(k + 1) % vidrios.size], 1.6.dp.toPx(), Offset(x + a / 2, h * 0.42f))
            x += a; k++
        }
    }
}

/**
 * La banda del horror cósmico: la línea de la marea a tinta verde-negra, que de trecho en trecho se enrosca como una punta
 * que asoma del agua, y al final el faro con su haz punteado.
 */
@Composable
internal fun BandaMarea(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val oscuro = e.background.luminance() < 0.5f
    val tinta = if (oscuro) e.secondary.copy(alpha = 0.7f) else e.onBackground.copy(alpha = 0.6f)
    val luz = if (oscuro) e.primary else e.tertiary
    Canvas(modifier.fillMaxWidth().height(16.dp).clipToBounds()) {
        val h = size.height; val u = 1.dp.toPx(); val y = h * 0.62f
        val fin = size.width - 22 * u
        val ola = Path().apply {
            moveTo(0f, y); var x = 0f; var k = 0
            while (x + 12 * u <= fin) {
                if (k % 6 == 5) {
                    // Una punta que sale del agua y se enrosca.
                    quadraticTo(x + 3 * u, y - 9 * u, x + 8 * u, y - 10 * u); quadraticTo(x + 12 * u, y - 9 * u, x + 10 * u, y - 6 * u)
                    quadraticTo(x + 8 * u, y - 4 * u, x + 7 * u, y - 6.5f * u); moveTo(x + 12 * u, y)
                } else { quadraticTo(x + 3 * u, y - 2.5f * u, x + 6 * u, y); quadraticTo(x + 9 * u, y + 2.5f * u, x + 12 * u, y) }
                x += 12 * u; k++
            }
        }
        drawPath(ola, tinta, style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // El faro: torre con bandas, linterna y el haz hacia la izquierda.
        val fx = fin + 11 * u
        drawPath(Path().apply { moveTo(fx - 4 * u, h); lineTo(fx - 2.6f * u, 4 * u); lineTo(fx + 2.6f * u, 4 * u); lineTo(fx + 4 * u, h); close() }, tinta)
        drawRect(e.background, Offset(fx - 3.6f * u, h * 0.55f), Size(7.2f * u, 1.6f * u))
        drawCircle(luz, 1.8f * u, Offset(fx, 3 * u))
        drawLine(luz.copy(alpha = 0.6f), Offset(fx - 3 * u, 3 * u), Offset(fx - 18 * u, 1 * u), 1 * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
    }
}

/** Trayectoria orbital punteada con planetas y estrellas de cuatro puntas. */
@Composable
internal fun Orbita(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(14.dp).clipToBounds()) {
        val y = size.height / 2
        var x = 0f
        while (x < size.width) { drawCircle(e.primary.copy(alpha = 0.55f), 0.9f, Offset(x, y)); x += 5.dp.toPx() }
        x = 24.dp.toPx(); var k = 0
        while (x < size.width) {
            when (k % 3) {
                0 -> { drawCircle(e.primary, size.height * 0.24f, Offset(x, y)); drawOval(e.secondary, Offset(x - size.height * 0.46f, y - size.height * 0.1f), Size(size.height * 0.92f, size.height * 0.2f), style = Stroke(1.2f)) }
                1 -> { val s = size.height * 0.42f
                    val p = Path().apply { moveTo(x, y - s); quadraticTo(x, y, x + s, y); quadraticTo(x, y, x, y + s); quadraticTo(x, y, x - s, y); quadraticTo(x, y, x, y - s); close() }
                    drawPath(p, e.onBackground.copy(alpha = 0.7f)) }
                else -> drawCircle(e.tertiary, size.height * 0.14f, Offset(x, y))
            }
            x += 58.dp.toPx(); k++
        }
    }
}

/** La traza del sonar: el agua apenas ondulada, un pulso de eco cada tanto y burbujas sueltas encima. */
@Composable
internal fun TrazaSonar(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(14.dp).clipToBounds()) {
        val y = size.height / 2; val u = 1.dp.toPx(); val periodo = 64 * u
        val ola = Path(); var x = 0f
        ola.moveTo(0f, y)
        while (x <= size.width) {
            val fase = (x % periodo) / periodo
            val cerca = 1f - abs(fase - 0.5f) / 0.08f
            val pulso = if (cerca > 0f) sin((fase - 0.42f) / 0.16f * PI.toFloat() * 3f) * size.height * 0.4f * cerca else 0f
            ola.lineTo(x, y + 1.1f * u * sin(x / (5 * u)) + pulso)
            x += 1.5f * u
        }
        drawPath(ola, e.primary.copy(alpha = 0.65f), style = Stroke(1.1f * u))
        x = 18 * u; var k = 0
        while (x < size.width) {
            drawCircle(e.tertiary.copy(alpha = 0.6f), (1.1f + (k % 3) * 0.5f) * u, Offset(x, y - size.height * (0.18f + 0.08f * (k % 3))), style = Stroke(0.7f * u))
            x += maxOf(periodo, 1f); k++
        }
    }
}

/** Lazo de alicatado: una cinta en zigzag que se cruza con otra, y estrellitas de ocho puntas en los huecos. */
@Composable
internal fun Lazo(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val oscuro = e.background.luminance() < 0.5f
    val oro = if (oscuro) e.primary else e.tertiary
    Canvas(modifier.fillMaxWidth().height(14.dp).clipToBounds()) {
        val h = size.height; val p = h * 1.2f
        val a = Path(); val b = Path(); var x = 0f; var arriba = true
        a.moveTo(0f, 1.5f); b.moveTo(0f, h - 1.5f)
        while (x <= size.width + p) { x += p; arriba = !arriba; a.lineTo(x, if (arriba) 1.5f else h - 1.5f); b.lineTo(x, if (arriba) h - 1.5f else 1.5f) }
        drawPath(a, oro, style = Stroke(2.2f)); drawPath(b, e.secondary, style = Stroke(2.2f))
        x = p
        while (x < size.width) {
            val c = Offset(x, h / 2); val s = h * 0.24f
            val est = Path().apply { for (i in 0 until 16) { val rr = if (i % 2 == 0) s else s * 0.5f; val q = c.mas(rr, rad(-90.0 + 22.5 * i)); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }
            drawPath(est, e.primary)
            x += p * 2
        }
    }
}

/** Greca escalonada de un friso maya, entre dos filetes, con cuentas de jade bajo cada gancho. */
@Composable
internal fun BandaGreca(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(16.dp).clipToBounds()) {
        val h = size.height; val u = 1.dp.toPx()
        drawLine(e.secondary.copy(alpha = 0.6f), Offset(0f, 0.6f * u), Offset(size.width, 0.6f * u), 1f * u)
        drawLine(e.secondary.copy(alpha = 0.6f), Offset(0f, h - 0.6f * u), Offset(size.width, h - 0.6f * u), 1f * u)
        grecaEscalonada(2f * u, size.width, 3.2f * u, h - 3.2f * u, e.primary, 1.6f * u)
        val paso = (h - 6.4f * u) * 1.4f; var x = 2f * u + paso * 0.94f
        while (x < size.width) { drawCircle(e.tertiary, 1.4f * u, Offset(x, h - 4.6f * u)); x += maxOf(paso, 1f) }
    }
}

/** Trazo de cuaderno: una línea a pluma con marcas de medida, un arco de compás y sombreado a rayas. */
@Composable
internal fun Trazo(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val tinta = e.onBackground.copy(alpha = 0.6f)
    Canvas(modifier.fillMaxWidth().height(14.dp).clipToBounds()) {
        val y = size.height * 0.6f
        val azar = java.util.Random(5)
        val p = Path(); var x = 0f; p.moveTo(0f, y)
        while (x < size.width) { x += 6f; p.lineTo(x, y + (azar.nextFloat() - 0.5f) * 0.9f) }
        drawPath(p, tinta, style = Stroke(1.1f))
        var i = 0; x = 0f
        val paso = 12.dp.toPx()
        while (x < size.width) { val alto = if (i % 6 == 0) size.height * 0.5f else size.height * 0.22f; drawLine(tinta, Offset(x, y), Offset(x, y - alto), 0.9f); x += paso; i++ }
        val cx = size.width * 0.72f
        drawArc(e.primary, 200f, 140f, false, Offset(cx - size.height, y - size.height * 0.9f), Size(size.height * 2, size.height * 2), style = Stroke(1.3f))
        for (k in 0 until 6) { val xx = size.width * 0.18f + k * 3.dp.toPx(); drawLine(e.primary.copy(alpha = 0.5f), Offset(xx, y - 1f), Offset(xx + 4.dp.toPx(), y - size.height * 0.5f), 0.9f) }
    }
}

/** Un cabo de cáñamo trenzado que corre bajo el título, con un nudo llano al centro y su «X» de tesoro a un lado. */
@Composable
internal fun Cabo(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(14.dp).clipToBounds()) {
        val h = size.height; val w = size.width; val y = h * 0.5f; val f = 1.dp.toPx()
        val grueso = h * 0.34f; val paso = grueso * 0.9f
        val claro = e.secondary.copy(alpha = 0.75f); val sombra = e.onBackground.copy(alpha = 0.45f)
        val nudo = w * 0.5f; val hueco = h * 0.9f
        var x = 0f
        while (x < w) {
            if (abs(x - nudo) > hueco) {
                // Cada vuelta del cordón: una diagonal clara con su sombra debajo.
                drawLine(sombra, Offset(x + f * 0.6f, y + grueso * 0.6f), Offset(x + paso + f * 0.6f, y - grueso * 0.4f), grueso * 0.42f, StrokeCap.Round)
                drawLine(claro, Offset(x, y + grueso * 0.5f), Offset(x + paso, y - grueso * 0.5f), grueso * 0.36f, StrokeCap.Round)
            }
            x += maxOf(paso, 1f)
        }
        // El nudo llano: dos lazos que se abrazan.
        drawOval(claro, Offset(nudo - hueco, y - grueso * 0.9f), Size(hueco, grueso * 1.8f), style = Stroke(grueso * 0.34f))
        drawOval(claro, Offset(nudo, y - grueso * 0.9f), Size(hueco, grueso * 1.8f), style = Stroke(grueso * 0.34f))
        drawOval(sombra, Offset(nudo - hueco * 0.5f, y - grueso * 0.5f), Size(hueco, grueso), style = Stroke(f))
        // La «X» del tesoro, a un tercio.
        val c = Offset(w * 0.2f, y); val s = h * 0.32f
        drawCircle(e.background, s * 1.5f, c)
        drawLine(e.tertiary, c - Offset(s, s), c + Offset(s, s), 1.8f * f, StrokeCap.Round)
        drawLine(e.tertiary, c + Offset(-s, s), c + Offset(s, -s), 1.8f * f, StrokeCap.Round)
    }
}

/** Guirnalda de pino que cuelga en arcos, con focos de colores. */
@Composable
internal fun Guirnalda(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val focos = listOf(e.primary, e.tertiary, e.secondary, Color(0xFF64B5F6))
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val h = size.height; val tramo = 64.dp.toPx(); var x = 0f; var k = 0
        while (x < size.width) {
            val p = Path().apply { moveTo(x, 2f); quadraticTo(x + tramo / 2, h * 1.05f, x + tramo, 2f) }
            drawPath(p, e.secondary, style = Stroke(3.5f, cap = StrokeCap.Round))
            drawPath(p, e.secondary.copy(alpha = 0.4f), style = Stroke(7f, cap = StrokeCap.Round))
            listOf(0.25f, 0.5f, 0.75f).forEach { f ->
                val px = x + tramo * f; val py = 2f + (h * 1.05f - 2f) * 2 * f * (1 - f)
                drawCircle(focos[k % focos.size], 2.6.dp.toPx(), Offset(px, py + 2.6.dp.toPx()))
                k++
            }
            drawCircle(e.tertiary, 2.dp.toPx(), Offset(x, 3f))
            x += tramo
        }
    }
}

/** Barra de cartel constructivista: franja roja en diagonal, una estrella y líneas negras. */
@Composable
internal fun Barra(modifier: Modifier) {
    val rojo = MaterialTheme.colorScheme.primary; val negro = MaterialTheme.colorScheme.onBackground
    Canvas(modifier.fillMaxWidth().height(14.dp)) {
        val h = size.height
        val est = Path(); val c = Offset(h * 0.6f, h / 2)
        for (i in 0 until 10) { val rr = if (i % 2 == 0) h * 0.55f else h * 0.22f; val p = c.mas(rr, rad(-90.0 + 36.0 * i)); if (i == 0) est.moveTo(p.x, p.y) else est.lineTo(p.x, p.y) }
        est.close(); drawPath(est, rojo)
        val ini = h * 1.5f
        val franja = Path().apply { moveTo(ini + h * 0.5f, 0f); lineTo(size.width * 0.62f, 0f); lineTo(size.width * 0.62f - h * 0.5f, h * 0.62f); lineTo(ini, h * 0.62f); close() }
        drawPath(franja, rojo)
        drawRect(negro, Offset(ini, h * 0.78f), Size(size.width - ini, 2.2f))
        var x = size.width * 0.66f
        while (x < size.width) { drawLine(negro, Offset(x, h * 0.62f), Offset(x + h * 0.4f, 0f), 2f); x += 7.dp.toPx() }
    }
}

/** Tierras Antiguas: una línea tallada con runas inventadas a los lados de una gema. */
@Composable
internal fun Runica(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(12.dp)) {
        val h = size.height; val y = h / 2; val cx = size.width / 2; val r = h * 0.45f
        drawLine(e.secondary.copy(alpha = 0.7f), Offset(0f, y), Offset(cx - r * 1.8f, y), 1.2f.dp.toPx())
        drawLine(e.secondary.copy(alpha = 0.7f), Offset(cx + r * 1.8f, y), Offset(size.width, y), 1.2f.dp.toPx())
        with(TrofeosMas) {
            runas(Offset(cx - r * 9f, y), r * 6f, h * 0.7f, e.onBackground.copy(alpha = 0.45f), 1.dp.toPx(), 4, 8)
            runas(Offset(cx + r * 3f, y), r * 6f, h * 0.7f, e.onBackground.copy(alpha = 0.45f), 1.dp.toPx(), 4, 11)
        }
        drawPath(Path().apply { moveTo(cx, y - r); lineTo(cx + r * 0.8f, y); lineTo(cx, y + r); lineTo(cx - r * 0.8f, y); close() }, e.primary)
        drawPath(Path().apply { moveTo(cx, y - r); lineTo(cx + r * 0.8f, y); lineTo(cx, y + r); lineTo(cx - r * 0.8f, y); close() }, e.secondary, style = Stroke(1.dp.toPx()))
    }
}

/** La banda de la Prehistoria: hileras de puntos de ocre, como las de las paredes de las cuevas, y al centro un bisonte de carbón. */
@Composable
internal fun BandaCueva(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(14.dp)) {
        val h = size.height; val y = h / 2; val cx = size.width / 2; val r = h * 0.95f
        val az = java.util.Random(31)
        var x = 4.dp.toPx(); var k = 0
        while (x < size.width - 4.dp.toPx()) {
            if (abs(x - cx) > r * 1.5f) {
                val g = (1.3f + az.nextFloat() * 1.1f).dp.toPx()
                drawCircle(e.primary.copy(alpha = 0.55f + az.nextFloat() * 0.35f), g, Offset(x, y + (if (k % 2 == 0) -1f else 1f) * h * 0.08f))
            }
            x += maxOf((7f + az.nextFloat() * 3f).dp.toPx(), 1f); k++
        }
        drawPath(bisonte(Offset(cx, y), r), e.onBackground.copy(alpha = 0.75f))
    }
}

/** La banda de la Revolución: un filete azul, blanco y rojo cortado al centro por una escarapela pequeña. */
@Composable
internal fun Tricolor(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val oscuro = e.background.luminance() < 0.5f
    val blanco = if (oscuro) Color(0xFFF0EADB) else Color.White
    Canvas(modifier.fillMaxWidth().height(12.dp)) {
        val h = size.height; val y = h / 2; val cx = size.width / 2; val r = h * 0.48f
        val tercio = (cx - r * 1.6f) / 3
        listOf(e.secondary, blanco, e.primary).forEachIndexed { i, c ->
            drawRect(c, Offset(i * tercio, y - h * 0.12f), Size(tercio, h * 0.24f))
            drawRect(c, Offset(size.width - (i + 1) * tercio, y - h * 0.12f), Size(tercio, h * 0.24f))
        }
        drawLine(e.onBackground.copy(alpha = 0.25f), Offset(0f, y + h * 0.2f), Offset(cx - r * 1.6f, y + h * 0.2f), 1f)
        drawLine(e.onBackground.copy(alpha = 0.25f), Offset(cx + r * 1.6f, y + h * 0.2f), Offset(size.width, y + h * 0.2f), 1f)
        drawCircle(e.primary, r, Offset(cx, y)); drawCircle(blanco, r * 0.66f, Offset(cx, y)); drawCircle(e.secondary, r * 0.34f, Offset(cx, y))
    }
}

// =====================================================================
//  Fondo con textura según el tema
// =====================================================================

/**
 * Una textura muy tenue detrás de todo: vetas de mármol, retícula de terminal, guijarros de Pela, renglones de pergamino,
 * trama de imprenta, papel tapiz atómico o líneas de pantalla antigua, papel milimétrico… Cada tema tiene la suya.
 * Se dibuja una sola vez y se reutiliza.
 */
fun Modifier.fondoTema(tema: TemaId, esquema: ColorScheme): Modifier = drawWithCache {
    val oscuro = esquema.background.luminance() < 0.5f
    val trazo = Path()
    val puntos = Path()
    var color = Color.Transparent
    var color2 = Color.Transparent
    val w = size.width; val h = size.height
    when (tema) {
        TemaId.CIBER -> {
            color = esquema.primary.copy(alpha = 0.045f)
            val p = 28.dp.toPx(); var x = 0f; while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x, h); x += p }
            var y = 0f; while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += p }
        }
        TemaId.NEON -> {
            // La llovizna que cae en diagonal y, abajo, la silueta de los edificios con sus ventanas.
            color = esquema.secondary.copy(alpha = 0.07f); color2 = esquema.primary.copy(alpha = 0.05f)
            val az = java.util.Random(77); val largo = 14.dp.toPx()
            repeat(140) { val x = az.nextFloat() * w; val y = az.nextFloat() * h; trazo.moveTo(x, y); trazo.lineTo(x - largo * 0.25f, y + largo) }
            var x = 0f
            while (x < w) {
                val ancho = (22 + az.nextInt(30)).dp.toPx(); val alto = h * (0.06f + az.nextFloat() * 0.12f)
                puntos.addRect(Rect(x, h - alto, x + ancho - 3.dp.toPx(), h)); x += maxOf(ancho, 1f)
            }
        }
        TemaId.MEDIEVAL -> {
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.06f); color2 = esquema.primary.copy(alpha = 0.12f)
            var y = 30.dp.toPx(); while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += 30.dp.toPx() }
            listOf(8.dp.toPx(), 11.dp.toPx()).forEach { x -> puntos.addRect(Rect(x, 0f, x + 1.2f, h)) }
        }
        TemaId.TEMPLARIO -> {
            // Los sillares de la muralla, a soga (cada hilada corre media piedra), y de tanto en tanto una cruz grabada en la piedra.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.055f); color2 = esquema.primary.copy(alpha = if (oscuro) 0.07f else 0.06f)
            val alto = 22.dp.toPx(); val largo = 46.dp.toPx(); var y = alto; var fila = 0
            while (y < h + alto) {
                trazo.moveTo(0f, y); trazo.lineTo(w, y)
                var x = if (fila % 2 == 0) largo * 0.5f else 0f
                while (x < w) {
                    trazo.moveTo(x, y - alto); trazo.lineTo(x, y)
                    if ((fila * 7 + (x / largo).toInt() * 3) % 11 == 0) puntos.addPath(cruzPatada(Offset(x + largo / 2, y - alto / 2), alto * 0.28f))
                    x += maxOf(largo, 1f)
                }
                y += maxOf(alto, 1f); fila++
            }
        }
        TemaId.DISTOPIA -> {
            color2 = esquema.onBackground.copy(alpha = if (oscuro) 0.06f else 0.07f)
            val p = 13.dp.toPx(); var y = 0f; var fila = 0
            while (y < h * 0.45f) {
                var x = if (fila % 2 == 0) 0f else p / 2
                while (x < w) {
                    val k = ((x / w) * (1f - y / (h * 0.45f))).coerceIn(0f, 1f)
                    val rr = p * 0.32f * k
                    if (rr > 0.6f) puntos.addOval(Rect(x - rr, y - rr, x + rr, y + rr))
                    x += p
                }
                y += p * 0.87f; fila++
            }
        }
        TemaId.REFUGIO -> if (oscuro) {
            color = esquema.primary.copy(alpha = 0.035f)
            var y = 0f; while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += 3.dp.toPx() }
        } else {
            // De día, papel tapiz de la era atómica: destellos de cuatro puntas, órbitas sueltas con su núcleo y bumeranes.
            color = esquema.primary.copy(alpha = 0.06f); color2 = esquema.secondary.copy(alpha = 0.07f)
            val azar = java.util.Random(1955)
            repeat(((w * h) / 26000f).toInt().coerceIn(12, 160)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h; val tipo = azar.nextInt(3)
                if (tipo == 0) {
                    val s = (3f + azar.nextFloat() * 4f).dp.toPx()
                    puntos.moveTo(x, y - s); puntos.quadraticTo(x, y, x + s, y); puntos.quadraticTo(x, y, x, y + s)
                    puntos.quadraticTo(x, y, x - s, y); puntos.quadraticTo(x, y, x, y - s); puntos.close()
                } else if (tipo == 1) {
                    val rr = (5f + azar.nextFloat() * 5f).dp.toPx()
                    trazo.addOval(Rect(x - rr, y - rr * 0.35f, x + rr, y + rr * 0.35f)); trazo.addOval(Rect(x - rr * 0.35f, y - rr, x + rr * 0.35f, y + rr))
                    val n = 1.4f.dp.toPx(); puntos.addOval(Rect(x - n, y - n, x + n, y + n))
                } else {
                    val s = (6f + azar.nextFloat() * 6f).dp.toPx()
                    trazo.moveTo(x - s, y + s * 0.4f); trazo.quadraticTo(x, y - s * 0.7f, x + s, y + s * 0.4f)
                    trazo.moveTo(x - s * 0.8f, y + s * 0.45f); trazo.quadraticTo(x, y - s * 0.45f, x + s * 0.8f, y + s * 0.45f)
                }
            }
        }
        TemaId.ROMANO -> {
            // Mármol: vetas largas que serpentean en diagonal y se abren en ramitas (de noche, vetas de oro en el basalto),
            // y algún poro alargado de travertino.
            color = (if (oscuro) esquema.primary else esquema.onBackground).copy(alpha = if (oscuro) 0.06f else 0.05f)
            color2 = esquema.onBackground.copy(alpha = if (oscuro) 0.04f else 0.035f)
            val azar = java.util.Random(753)
            val paso = 9.dp.toPx()
            repeat(((w * h) / 160000f).toInt().coerceIn(4, 12)) {
                var x = (azar.nextFloat() * 1.3f - 0.3f) * w; var y = azar.nextFloat() * h
                var a = 0.6f + azar.nextFloat() * 0.5f
                trazo.moveTo(x, y)
                repeat(60) {
                    a = (a + (azar.nextFloat() - 0.5f) * 0.5f).coerceIn(0.2f, 1.4f)
                    x += cos(a) * paso; y += sin(a) * paso
                    trazo.lineTo(x, y)
                    if (azar.nextFloat() < 0.06f) {
                        val b = a + (if (azar.nextBoolean()) 0.7f else -0.7f); var bx = x; var by = y
                        repeat(6) { bx += cos(b) * paso * 0.65f; by += sin(b) * paso * 0.65f + (azar.nextFloat() - 0.5f) * 3f; trazo.lineTo(bx, by) }
                        trazo.moveTo(x, y)
                    }
                }
            }
            repeat(((w * h) / 30000f).toInt().coerceAtMost(120)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h
                val rx = (1.5f + azar.nextFloat() * 2.5f).dp.toPx(); val ry = (0.6f + azar.nextFloat() * 0.6f).dp.toPx()
                puntos.addOval(Rect(x - rx, y - ry, x + rx, y + ry))
            }
        }
        TemaId.URBE -> {
            // El revoque de los muros del centro: aquí y allá se cayó el enlucido y asoman las hiladas de ladrillo (con su
            // borde roto alrededor), y el resto lleva las manchitas del revoque a la cal.
            color = (if (oscuro) esquema.tertiary else esquema.primary).copy(alpha = if (oscuro) 0.07f else 0.07f)
            color2 = esquema.onBackground.copy(alpha = if (oscuro) 0.035f else 0.03f)
            val azar = java.util.Random(2779)
            val ladrillo = 5.dp.toPx(); val largo = 14.dp.toPx()
            repeat(((w * h) / 220000f).toInt().coerceIn(3, 9)) {
                val cx = azar.nextFloat() * w; val cy = azar.nextFloat() * h
                val rx = (34f + azar.nextFloat() * 40f).dp.toPx(); val ry = rx * (0.45f + azar.nextFloat() * 0.3f)
                // El borde roto del enlucido: un contorno irregular alrededor del hueco.
                val lados = 14
                for (k in 0..lados) {
                    val a = 2 * PI.toFloat() * k / lados
                    val f = 0.78f + azar.nextFloat() * 0.3f
                    val x = cx + cos(a) * rx * f; val y = cy + sin(a) * ry * f
                    if (k == 0) trazo.moveTo(x, y) else trazo.lineTo(x, y)
                }
                // Las hiladas, a tresbolillo, dentro del hueco (más cortas cerca de la orilla).
                var y = cy - ry * 0.62f; var fila = 0
                while (y < cy + ry * 0.62f) {
                    val medio = rx * 0.7f * kotlin.math.sqrt((1f - ((y - cy) / (ry * 0.7f)).let { it * it }).coerceAtLeast(0f))
                    trazo.moveTo(cx - medio, y); trazo.lineTo(cx + medio, y)
                    var x = cx - medio + (if (fila % 2 == 0) 0f else largo / 2)
                    while (x < cx + medio) { trazo.moveTo(x, y); trazo.lineTo(x, y + ladrillo); x += maxOf(largo, 1f) }
                    y += maxOf(ladrillo, 1f); fila++
                }
            }
            repeat(((w * h) / 9000f).toInt().coerceAtMost(260)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h
                val rr = (0.5f + azar.nextFloat() * 1.2f).dp.toPx()
                puntos.addOval(Rect(x - rr, y - rr * 0.8f, x + rr, y + rr * 0.8f))
            }
        }
        TemaId.HELENO -> {
            // Mosaico de guijarros, como los de Pela: piedrecitas en hileras desiguales, y aquí y allá un sol de dieciséis rayos a punta de cincel.
            color = (if (oscuro) esquema.primary else esquema.tertiary).copy(alpha = if (oscuro) 0.06f else 0.07f)
            color2 = esquema.onBackground.copy(alpha = if (oscuro) 0.045f else 0.04f)
            val azar = java.util.Random(336)
            val p = 11.dp.toPx(); var y = p / 2; var fila = 0
            while (y < h) {
                var x = if (fila % 2 == 0) p / 2 else p
                while (x < w) {
                    if (azar.nextFloat() < 0.6f) {
                        val rx = (1.2f + azar.nextFloat()).dp.toPx(); val ry = rx * (0.6f + azar.nextFloat() * 0.4f)
                        val jx = (azar.nextFloat() - 0.5f) * p * 0.35f; val jy = (azar.nextFloat() - 0.5f) * p * 0.35f
                        puntos.addOval(Rect(x + jx - rx, y + jy - ry, x + jx + rx, y + jy + ry))
                    }
                    x += p
                }
                y += p * 0.87f; fila++
            }
            repeat(((w * h) / 200000f).toInt().coerceIn(2, 8)) {
                val cx = azar.nextFloat() * w; val cy = azar.nextFloat() * h; val rr = (14f + azar.nextFloat() * 10f).dp.toPx()
                for (i in 0 until 16) {
                    val an = rad(22.5 * i - 90)
                    val l = if (i % 2 == 0) rr else rr * 0.7f
                    trazo.moveTo(cx + cos(an) * rr * 0.25f, cy + sin(an) * rr * 0.25f); trazo.lineTo(cx + cos(an) * l, cy + sin(an) * l)
                }
                trazo.addOval(Rect(cx - rr * 0.22f, cy - rr * 0.22f, cx + rr * 0.22f, cy + rr * 0.22f))
            }
        }
        TemaId.EGIPTO -> {
            // Papiro: las fibras en dos capas (las horizontales más juntas, las verticales más espaciadas), un poco torcidas,
            // y cada tanto la franja de la unión de dos hojas pegadas.
            color = (if (oscuro) esquema.primary else esquema.onBackground).copy(alpha = if (oscuro) 0.045f else 0.05f)
            color2 = (if (oscuro) esquema.primary else esquema.secondary).copy(alpha = if (oscuro) 0.05f else 0.045f)
            val azar = java.util.Random(4217)
            var y = 0f
            while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y + (azar.nextFloat() - 0.5f) * 2f); y += (3f + azar.nextFloat() * 4f).dp.toPx() }
            var x = 0f
            while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x + (azar.nextFloat() - 0.5f) * 3f, h); x += (9f + azar.nextFloat() * 10f).dp.toPx() }
            var j = 140.dp.toPx()
            while (j < w) { puntos.addRect(Rect(j, 0f, j + 4.dp.toPx(), h)); j += 140.dp.toPx() }
        }
        TemaId.LABORATORIO -> {
            color = esquema.primary.copy(alpha = 0.05f); color2 = esquema.primary.copy(alpha = 0.06f)
            val p = 12.dp.toPx(); var x = 0f; var i = 0
            while (x < w) { if (i % 5 == 0) puntos.addRect(Rect(x, 0f, x + 1f, h)) else { trazo.moveTo(x, 0f); trazo.lineTo(x, h) }; x += p; i++ }
            var y = 0f; i = 0
            while (y < h) { if (i % 5 == 0) puntos.addRect(Rect(0f, y, w, y + 1f)) else { trazo.moveTo(0f, y); trazo.lineTo(w, y) }; y += p; i++ }
        }
        TemaId.ORDEN -> {
            color = esquema.onBackground.copy(alpha = 0.03f)
            val p = 9.dp.toPx(); var x = -h
            while (x < w) { trazo.moveTo(x, h); trazo.lineTo(x + h, 0f); x += p }
        }
        TemaId.VAPOR -> {
            // El papel de plano: la cuadrícula del dibujante, un gran engrane trazado a compás en la esquina de abajo, con
            // sus círculos de construcción y su eje, y una cota con flechas arriba.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.04f)
            color2 = esquema.primary.copy(alpha = if (oscuro) 0.07f else 0.06f)
            val p = 24.dp.toPx(); var x = 0f
            while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x, h); x += maxOf(p, 1f) }
            var y = 0f
            while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += maxOf(p, 1f) }
            val ce = Offset(w * 0.92f, h * 0.9f); val rr = w * 0.42f
            trazo.addPath(Vapor.engrane(ce, rr, 18, 0.9f))
            trazo.addOval(Rect(ce, rr * 0.62f)); trazo.addOval(Rect(ce, rr * 0.2f))
            trazo.moveTo(ce.x - rr * 1.1f, ce.y); trazo.lineTo(ce.x + rr * 0.2f, ce.y)
            trazo.moveTo(ce.x, ce.y - rr * 1.1f); trazo.lineTo(ce.x, ce.y + rr * 0.2f)
            val yc = h * 0.06f
            trazo.moveTo(w * 0.1f, yc); trazo.lineTo(w * 0.6f, yc)
            trazo.moveTo(w * 0.1f, yc - 6.dp.toPx()); trazo.lineTo(w * 0.1f, yc + 6.dp.toPx()); trazo.moveTo(w * 0.6f, yc - 6.dp.toPx()); trazo.lineTo(w * 0.6f, yc + 6.dp.toPx())
            puntos.addOval(Rect(ce, rr * 0.06f))
            for (k in 0 until 6) { val q = Vapor.en(ce, rr * 0.41f, k * Math.PI / 3); puntos.addOval(Rect(q, rr * 0.045f)) }
        }
        TemaId.COLONOS -> {
            color2 = esquema.onBackground.copy(alpha = 0.05f)
            val azar = java.util.Random(17)
            repeat(((w * h) / 2500f).toInt().coerceAtMost(900)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h; val rr = 0.4f + azar.nextFloat() * 0.9f
                puntos.addOval(Rect(x - rr, y - rr, x + rr, y + rr))
            }
        }
        TemaId.NAPOLEON -> {
            // Tela imperial: rombos finos con un punto dorado en cada cruce.
            color = esquema.primary.copy(alpha = if (oscuro) 0.05f else 0.04f); color2 = esquema.tertiary.copy(alpha = 0.12f)
            val p = 26.dp.toPx(); var x = -h
            while (x < w + h) { trazo.moveTo(x, 0f); trazo.lineTo(x + h, h); trazo.moveTo(x, h); trazo.lineTo(x + h, 0f); x += p }
            var y = 0f; var fila = 0
            while (y < h) { var xx = if (fila % 2 == 0) 0f else p / 2; while (xx < w) { puntos.addOval(Rect(xx - 1.4f, y - 1.4f, xx + 1.4f, y + 1.4f)); xx += p }; y += p / 2; fila++ }
        }
        TemaId.BOTANICO -> {
            // Libreta de campo: cuadrícula muy tenue, y aquí y allá hileras de hormigas, semillas y frondas de helecho a lápiz.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.045f); color2 = esquema.onBackground.copy(alpha = if (oscuro) 0.07f else 0.06f)
            val paso = 28.dp.toPx(); var gx = 0f
            while (gx < w) { trazo.moveTo(gx, 0f); trazo.lineTo(gx, h); gx += paso }
            var gy = 0f
            while (gy < h) { trazo.moveTo(0f, gy); trazo.lineTo(w, gy); gy += paso }
            val azar = java.util.Random(23)
            fun ovalo(x: Float, y: Float, rx: Float, ry: Float) = puntos.addOval(Rect(x - rx, y - ry, x + rx, y + ry))
            // Hileras de hormigas: siguen una curva suave.
            repeat(((w * h) / 140000f).toInt().coerceIn(2, 9)) {
                val x0 = azar.nextFloat() * w; val y0 = azar.nextFloat() * h; val a0 = azar.nextFloat() * 2 * PI.toFloat(); val curva = (azar.nextFloat() - 0.5f) * 0.05f
                var x = x0; var y = y0; var a = a0; val l = 5.dp.toPx()
                repeat(5 + azar.nextInt(6)) {
                    val dx = cos(a); val dy = sin(a)
                    ovalo(x - dx * l * 0.4f, y - dy * l * 0.4f, l * 0.24f, l * 0.24f); ovalo(x, y, l * 0.13f, l * 0.13f); ovalo(x + dx * l * 0.3f, y + dy * l * 0.3f, l * 0.15f, l * 0.15f)
                    x += dx * l * 3.2f; y += dy * l * 3.2f; a += curva * 6
                }
            }
            // Semillas y helechitos.
            repeat(((w * h) / 60000f).toInt().coerceAtMost(40)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h
                if (azar.nextBoolean()) ovalo(x, y, 2.6f.dp.toPx(), 1.5f.dp.toPx())
                else {
                    val a = azar.nextFloat() * 2 * PI.toFloat(); val l = 12.dp.toPx(); val dx = cos(a) * l; val dy = sin(a) * l
                    trazo.moveTo(x, y); trazo.lineTo(x + dx, y + dy)
                    for (k in 1..5) { val f = k / 6f; val bx = x + dx * f; val by = y + dy * f; val pl = l * 0.28f * (1 - f)
                        trazo.moveTo(bx, by); trazo.lineTo(bx + cos(a + 1.1f) * pl, by + sin(a + 1.1f) * pl)
                        trazo.moveTo(bx, by); trazo.lineTo(bx + cos(a - 1.1f) * pl, by + sin(a - 1.1f) * pl) }
                }
            }
        }
        TemaId.RUINAS -> {
            // Losas de acera vistas desde arriba: juntas un poco torcidas, grietas que se ramifican y matas de musgo en las juntas.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.055f); color2 = esquema.primary.copy(alpha = if (oscuro) 0.07f else 0.08f)
            val azar = java.util.Random(41)
            val losa = 96.dp.toPx()
            var gy = 0f; var fila = 0
            while (gy < h) {
                trazo.moveTo(0f, gy); trazo.lineTo(w, gy + (azar.nextFloat() - 0.5f) * 6.dp.toPx())
                var gx = if (fila % 2 == 0) 0f else losa / 2
                while (gx < w) { trazo.moveTo(gx, gy); trazo.lineTo(gx + (azar.nextFloat() - 0.5f) * 4.dp.toPx(), gy + losa); gx += maxOf(losa, 1f) }
                gy += maxOf(losa, 1f); fila++
            }
            // Grietas: caminos quebrados que a la mitad se bifurcan una vez.
            fun grieta(x0: Float, y0: Float, a0: Float, pasos: Int, rama: Boolean) {
                var x = x0; var y = y0; var a = a0
                trazo.moveTo(x, y)
                repeat(pasos) { k ->
                    a += (azar.nextFloat() - 0.5f) * 1.1f
                    val l = (5f + azar.nextFloat() * 7f).dp.toPx()
                    x += cos(a) * l; y += sin(a) * l; trazo.lineTo(x, y)
                    if (rama && k == pasos / 2) { grieta(x, y, a + 0.9f, pasos / 2, false); trazo.moveTo(x, y) }
                }
            }
            repeat(((w * h) / 90000f).toInt().coerceIn(3, 14)) { grieta(azar.nextFloat() * w, azar.nextFloat() * h, azar.nextFloat() * 2 * PI.toFloat(), 6 + azar.nextInt(6), true) }
            // Musgo: grupos de puntitos donde se cruzan las juntas.
            repeat(((w * h) / 50000f).toInt().coerceIn(4, 40)) {
                val mx = azar.nextInt((w / losa).toInt() + 1) * losa + (azar.nextFloat() - 0.5f) * 10.dp.toPx()
                val my = azar.nextInt((h / losa).toInt() + 1) * losa + (azar.nextFloat() - 0.5f) * 10.dp.toPx()
                repeat(6 + azar.nextInt(8)) {
                    val rr = (0.8f + azar.nextFloat() * 1.8f).dp.toPx()
                    val px = mx + (azar.nextFloat() - 0.5f) * 16.dp.toPx(); val py = my + (azar.nextFloat() - 0.5f) * 10.dp.toPx()
                    puntos.addOval(Rect(px - rr, py - rr, px + rr, py + rr))
                }
            }
        }
        TemaId.GOTICO -> {
            // Vidriera de rombos emplomados; de noche, además, la bóveda azul sembrada de estrellas de oro.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.04f else 0.035f)
            val p = 22.dp.toPx(); var x = -h
            while (x < w + h) { trazo.moveTo(x, 0f); trazo.lineTo(x + h * 0.6f, h); trazo.moveTo(x + h * 0.6f, 0f); trazo.lineTo(x, h); x += p }
            if (oscuro) {
                color2 = esquema.primary.copy(alpha = 0.16f)
                val azar = java.util.Random(31)
                repeat(((w * h) / 9000f).toInt().coerceAtMost(260)) {
                    val cx = azar.nextFloat() * w; val cy = azar.nextFloat() * h; val s = 1.6f + azar.nextFloat() * 2.4f
                    for (i in 0 until 8) { val rr = if (i % 2 == 0) s else s * 0.35f; val q = Offset(cx, cy).mas(rr, rad(-90.0 + 45.0 * i)); if (i == 0) puntos.moveTo(q.x, q.y) else puntos.lineTo(q.x, q.y) }
                    puntos.close()
                }
            }
        }
        TemaId.ARCANO -> {
            // Papel de archivo manchado: cercos de taza y manchas de humedad. Desde la orilla de abajo asoman, apenas, unas
            // puntas que se enroscan; de noche, además, las líneas de la marea.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.045f)
            color2 = esquema.secondary.copy(alpha = if (oscuro) 0.05f else 0.06f)
            val u = 1.dp.toPx()
            val azar = java.util.Random(1926)
            repeat(((w * h) / 90000f).toInt().coerceIn(4, 24)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h; val rr = (14f + azar.nextFloat() * 26f) * u
                if (azar.nextInt(3) == 0) trazo.addOval(Rect(x - rr, y - rr * 0.92f, x + rr, y + rr * 0.92f))
                else {
                    puntos.addOval(Rect(x - rr, y - rr * 0.6f, x + rr, y + rr * 0.6f))
                    puntos.addOval(Rect(x + rr * 0.4f, y - rr * 0.9f, x + rr * 1.2f, y - rr * 0.1f))
                }
            }
            listOf(0.07f to 1f, 0.93f to -1f, 0.62f to 1f).forEachIndexed { i, (fx, s) ->
                val x0 = w * fx; val alto = (90f + 40f * i) * u; val g = (7f - i) * u
                listOf(-g, g).forEach { o ->
                    trazo.moveTo(x0 + o, h)
                    trazo.cubicTo(x0 + o + s * 30 * u, h - alto * 0.4f, x0 + o * 0.5f - s * 24 * u, h - alto * 0.75f, x0 + s * 8 * u + o * 0.2f, h - alto)
                }
                val punta = Offset(x0 + s * 8 * u, h - alto)
                trazo.moveTo(punta.x, punta.y); trazo.quadraticTo(punta.x + s * 18 * u, punta.y - 14 * u, punta.x + s * 16 * u, punta.y + 2 * u)
                trazo.quadraticTo(punta.x + s * 12 * u, punta.y + 10 * u, punta.x + s * 6 * u, punta.y + 2 * u)
            }
            if (oscuro) {
                var y = h * 0.72f; var k = 0
                while (y < h) {
                    val paso = 26 * u; var x = -paso * (k % 2) / 2
                    trazo.moveTo(x, y)
                    while (x < w) { trazo.quadraticTo(x + paso / 4, y - 3 * u, x + paso / 2, y); trazo.quadraticTo(x + paso * 3 / 4, y + 3 * u, x + paso, y); x += maxOf(paso, 1f) }
                    y += 22 * u; k++
                }
            }
        }
        TemaId.ESPACIO -> {
            // Campo de estrellas: muchas pequeñas, pocas grandes.
            color2 = (if (oscuro) Color.White else esquema.primary).copy(alpha = if (oscuro) 0.22f else 0.06f)
            val azar = java.util.Random(41)
            repeat(((w * h) / (if (oscuro) 1800f else 5000f)).toInt().coerceAtMost(900)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h
                val g = azar.nextFloat(); val rr = if (g > 0.97f) 1.9f else if (g > 0.85f) 1.2f else 0.6f
                puntos.addOval(Rect(x - rr, y - rr, x + rr, y + rr))
            }
            if (!oscuro) { color = esquema.primary.copy(alpha = 0.035f); var y = 0f; while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += 48.dp.toPx() } }
        }
        TemaId.ABISMO -> {
            if (!oscuro) {
                // Zona iluminada: la red de luz que el oleaje dibuja en el agua (cáusticas), más tenue cuanto más abajo.
                color = esquema.primary.copy(alpha = 0.05f)
                val p = 36.dp.toPx(); var y = 0f; var fila = 0
                while (y < h * 0.7f) {
                    var x = 0f
                    trazo.moveTo(0f, y)
                    while (x <= w) { trazo.lineTo(x, y + p * 0.18f * sin(x / p * 2.1f + fila * 1.7f) + p * 0.1f * sin(x / p * 5.3f + fila)); x += 4.dp.toPx() }
                    y += maxOf(p * (0.6f + fila * 0.08f), 1f); fila++
                }
            } else {
                // Zona abisal: puntos de plancton con luz propia, casi todos diminutos; algunos más grandes, de medusa.
                color2 = esquema.primary.copy(alpha = 0.2f)
                val azar = java.util.Random(53)
                repeat(((w * h) / 3200f).toInt().coerceAtMost(500)) {
                    val x = azar.nextFloat() * w; val y = azar.nextFloat() * h
                    val g = azar.nextFloat(); val rr = if (g > 0.98f) 2.4f else if (g > 0.9f) 1.3f else 0.7f
                    puntos.addOval(Rect(x - rr, y - rr, x + rr, y + rr))
                }
            }
        }
        TemaId.ANDALUS -> {
            // Lacería: retícula a 0°, 45° y 90° que forma estrellas de ocho puntas, como el alicatado.
            color = (if (oscuro) esquema.primary else esquema.primary).copy(alpha = if (oscuro) 0.05f else 0.045f)
            val p = 34.dp.toPx()
            var x = 0f; while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x, h); x += p }
            var y = 0f; while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += p }
            x = -h; while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x + h, h); trazo.moveTo(x, h); trazo.lineTo(x + h, 0f); x += p }
        }
        TemaId.MESO -> {
            // Papel amate: la corteza se golpea en una red de fibras que se cruzan sin orden, unas largas y torcidas, y deja
            // grumos oscuros donde quedó un nudo; en oscuro, las fibras se ven como vetas de obsidiana.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.04f else 0.055f)
            color2 = (if (oscuro) esquema.primary else esquema.onBackground).copy(alpha = if (oscuro) 0.05f else 0.07f)
            val azar = java.util.Random(7313)
            repeat(((w * h) / 2600f).toInt().coerceAtMost(900)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h
                val a = (azar.nextFloat() - 0.5f) * 0.9f + (if (azar.nextBoolean()) 0f else PI.toFloat() / 2)
                val l = (14f + azar.nextFloat() * 40f).dp.toPx(); val doblez = (azar.nextFloat() - 0.5f) * 6.dp.toPx()
                trazo.moveTo(x, y)
                trazo.quadraticTo(x + cos(a) * l / 2 - sin(a) * doblez, y + sin(a) * l / 2 + cos(a) * doblez, x + cos(a) * l, y + sin(a) * l)
            }
            repeat(((w * h) / 30000f).toInt().coerceAtMost(120)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h; val rr = (0.8f + azar.nextFloat() * 1.6f).dp.toPx()
                puntos.addOval(Rect(x - rr, y - rr * 0.6f, x + rr, y + rr * 0.6f))
            }
        }
        TemaId.MONGOL -> {
            // Fieltro: fibras cortas en todas direcciones.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.035f else 0.04f)
            val azar = java.util.Random(53)
            repeat(((w * h) / 1400f).toInt().coerceAtMost(1400)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h; val a = azar.nextFloat() * 2 * PI.toFloat(); val l = 2.dp.toPx() + azar.nextFloat() * 4.dp.toPx()
                trazo.moveTo(x, y); trazo.lineTo(x + cos(a) * l, y + sin(a) * l)
            }
        }
        TemaId.RENACIMIENTO -> {
            // Hoja de cuaderno: trazos de construcción (arcos de compás y diagonales) y manchas de tinta.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.06f); color2 = esquema.primary.copy(alpha = if (oscuro) 0.05f else 0.06f)
            val azar = java.util.Random(61)
            repeat(5) {
                val cx = azar.nextFloat() * w; val cy = azar.nextFloat() * h; val rr = (0.2f + azar.nextFloat() * 0.4f) * w
                trazo.addOval(Rect(cx - rr, cy - rr, cx + rr, cy + rr))
                trazo.moveTo(cx - rr, cy); trazo.lineTo(cx + rr, cy); trazo.moveTo(cx, cy - rr); trazo.lineTo(cx, cy + rr)
            }
            repeat(18) { val x = azar.nextFloat() * w; val y = azar.nextFloat() * h; val rr = 1f + azar.nextFloat() * 3.5f; puntos.addOval(Rect(x - rr, y - rr, x + rr, y + rr)) }
        }
        TemaId.PIRATA -> {
            // Carta náutica: las líneas de rumbo que salen de dos rosas (como en los portulanos), la cuadrícula de grados y
            // unas islas pequeñas.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.065f); color2 = esquema.primary.copy(alpha = if (oscuro) 0.05f else 0.07f)
            val largo = maxOf(w, h) * 1.2f
            listOf(Offset(w * 0.78f, h * 0.22f), Offset(w * 0.2f, h * 0.7f)).forEach { o ->
                for (i in 0 until 16) {
                    val a = 2 * PI * i / 16
                    trazo.moveTo(o.x, o.y); trazo.lineTo(o.x + (largo * cos(a)).toFloat(), o.y + (largo * sin(a)).toFloat())
                }
                trazo.addOval(Rect(o.x - 14.dp.toPx(), o.y - 14.dp.toPx(), o.x + 14.dp.toPx(), o.y + 14.dp.toPx()))
            }
            val g = 96.dp.toPx(); var y = g; while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += maxOf(g, 1f) }
            val azar = java.util.Random(1718)
            repeat(5) {
                val x = azar.nextFloat() * w; val yy = azar.nextFloat() * h; val rr = (6f + azar.nextFloat() * 14f) * 1.dp.toPx()
                puntos.addOval(Rect(x - rr, yy - rr * 0.55f, x + rr, yy + rr * 0.55f))
                puntos.addOval(Rect(x + rr * 0.4f, yy - rr * 0.8f, x + rr * 1.2f, yy - rr * 0.1f))
            }
        }
        TemaId.NAVIDAD -> {
            // Nieve.
            color2 = (if (oscuro) Color.White else esquema.secondary).copy(alpha = if (oscuro) 0.09f else 0.05f)
            val azar = java.util.Random(25)
            repeat(((w * h) / 4000f).toInt().coerceAtMost(500)) {
                val x = azar.nextFloat() * w; val y = azar.nextFloat() * h; val rr = 0.8f + azar.nextFloat() * 2.2f
                puntos.addOval(Rect(x - rr, y - rr, x + rr, y + rr))
            }
        }
        TemaId.SEDA -> {
            // Tejido de seda: diagonales cruzadas muy finas.
            color = esquema.primary.copy(alpha = 0.035f)
            val p = 7.dp.toPx(); var x = -h
            while (x < w) { trazo.moveTo(x, h); trazo.lineTo(x + h, 0f); x += p }
            x = 0f; while (x < w + h) { trazo.moveTo(x, h); trazo.lineTo(x - h * 0.5f, 0f); x += p * 3 }
        }
        TemaId.EDO -> {
            // Seigaiha: hileras alternas de olas de tres arcos, muy tenues, como el estampado de un kimono de verano.
            color = esquema.primary.copy(alpha = if (oscuro) 0.05f else 0.045f)
            val r = 16.dp.toPx(); var fila = 0; var y = r
            while (y < h + r) {
                var x = if (fila % 2 == 0) 0f else r
                while (x < w + r) {
                    for (k in 0..2) { val q = r * (1f - k * 0.3f); trazo.moveTo(x - q, y); trazo.arcTo(Rect(x - q, y - q, x + q, y + q), 180f, 180f, false) }
                    x += maxOf(2 * r, 1f)
                }
                y += maxOf(r, 1f); fila++
            }
        }
        TemaId.REVOLUCION -> {
            // Papel verjurado de la época: los corondeles verticales, y una escarapela diminuta de vez en cuando.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.04f else 0.035f); color2 = esquema.primary.copy(alpha = if (oscuro) 0.08f else 0.07f)
            val p = 24.dp.toPx(); var x = p / 2
            while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x, h); x += p }
            val q = 3.dp.toPx(); var y = 0f
            while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += q * 12 }
            val azar = java.util.Random(1789)
            repeat(((w * h) / 90000f).toInt().coerceIn(3, 14)) {
                val cx = azar.nextFloat() * w; val cy = azar.nextFloat() * h; val rr = 3.dp.toPx()
                puntos.addOval(Rect(cx - rr, cy - rr, cx + rr, cy + rr))
            }
        }
        TemaId.ANTIGUAS -> {
            // Muro de sillares tenue, y de vez en cuando una gema incrustada.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.045f); color2 = esquema.secondary.copy(alpha = if (oscuro) 0.08f else 0.07f)
            val alto = 22.dp.toPx(); val largo = 52.dp.toPx(); var y = 0f; var fila = 0
            while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); var x = if (fila % 2 == 0) 0f else largo / 2; while (x < w) { trazo.moveTo(x, y); trazo.lineTo(x, y + alto); x += largo }; y += alto; fila++ }
            val azar = java.util.Random(3019)
            repeat(((w * h) / 120000f).toInt().coerceIn(2, 10)) {
                val cx = azar.nextFloat() * w; val cy = azar.nextFloat() * h; val rr = 2.6f.dp.toPx()
                puntos.moveTo(cx, cy - rr); puntos.lineTo(cx + rr, cy); puntos.lineTo(cx, cy + rr); puntos.lineTo(cx - rr, cy); puntos.close()
            }
        }
        TemaId.CUEVA -> {
            // La caliza de la pared: estratos que ondulan y, de tanto en tanto, una hilera de puntos de ocre como las de las cuevas.
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.055f); color2 = esquema.primary.copy(alpha = if (oscuro) 0.1f else 0.08f)
            val az = java.util.Random(17000); val paso = 34.dp.toPx(); var y = paso * 0.6f
            while (y < h) {
                val a = (3 + az.nextInt(8)).dp.toPx(); trazo.moveTo(0f, y); var x = 0f
                while (x < w) { val nx = x + w / 3; trazo.quadraticTo(x + w / 6, y + (if (az.nextBoolean()) a else -a), nx, y + (az.nextFloat() - 0.5f) * a); x = nx }
                y += maxOf(paso * (0.7f + az.nextFloat() * 0.6f), 1f)
            }
            repeat(((w * h) / 140000f).toInt().coerceIn(2, 9)) {
                val x0 = az.nextFloat() * w; val y0 = az.nextFloat() * h; val n = 3 + az.nextInt(4); val rr = 2.4f.dp.toPx()
                for (k in 0 until n) { val cx = x0 + k * rr * 3.2f; val cy = y0 + (if (k % 2 == 0) 0f else rr * 0.8f); puntos.addOval(Rect(cx - rr, cy - rr, cx + rr, cy + rr)) }
            }
        }
        TemaId.URSS -> {
            // Rayos que salen de una esquina, como en los carteles.
            color = esquema.primary.copy(alpha = if (oscuro) 0.06f else 0.05f)
            val o = Offset(0f, h)
            for (i in 0 until 14) {
                val a = rad(-90.0 + i * 6.5)
                trazo.moveTo(o.x, o.y); trazo.lineTo(o.x + cos(a) * (w + h) * 2, o.y + sin(a) * (w + h) * 2)
            }
        }
    }
    onDrawBehind {
        if (color.alpha > 0f) drawPath(trazo, color, style = Stroke(1f))
        if (color2.alpha > 0f) drawPath(puntos, color2)
    }
}

// =====================================================================
//  Botón de modo claro / oscuro
// =====================================================================

/** Un polígono con los vértices dados en fracciones del ancho y del alto, para el marco de los botones. */
private fun poligonoBoton(vararg v: Pair<Float, Float>): Shape = GenericShape { s, _ ->
    v.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x * s.width, y * s.height) else lineTo(x * s.width, y * s.height) }
    close()
}

/** Un polígono regular o estrellado de [n] puntas, alternando los radios [r1] y [r2] (en fracciones del lado). */
private fun estrellaBoton(n: Int, r1: Float, r2: Float, desde: Double): Shape = GenericShape { s, _ ->
    for (i in 0 until n) {
        val a = rad(desde + 360.0 * i / n); val rr = if (i % 2 == 0) r1 else r2
        val x = s.width * (0.5f + rr * cos(a)); val y = s.height * (0.5f + rr * sin(a))
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/**
 * El marco de los botones de arriba (modo, buscar y «Más») en cada tema: arco de medio punto, pantalla de terminal,
 * frontón, cuadrifolia, carpeta de expediente, tubo de televisor, escudo, hexágono, boleto, cartela oval, hoja, bola de
 * Navidad, octógono, lanceta, ojo de buey, estrella de lazo, yurta, rombo, paralelogramo, escarapela plisada y piedra tallada.
 */
private fun formaBoton(t: TemaId): Shape = when (t) {
    TemaId.ROMANO -> GenericShape { s, _ ->
        moveTo(0.1f * s.width, s.height); lineTo(0.1f * s.width, 0.42f * s.height)
        arcTo(Rect(0.1f * s.width, 0.02f * s.height, 0.9f * s.width, 0.82f * s.height), 180f, 180f, false)
        lineTo(0.9f * s.width, s.height); close()
    }
    // La cúpula: tambor recto, casquete y la linterna arriba.
    TemaId.URBE -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(0.06f * w, h); lineTo(0.06f * w, 0.56f * h)
        arcTo(Rect(0.06f * w, 0.18f * h, 0.94f * w, 0.94f * h), 180f, 90f, false)
        lineTo(0.44f * w, 0.18f * h); lineTo(0.44f * w, 0.02f * h); lineTo(0.56f * w, 0.02f * h); lineTo(0.56f * w, 0.18f * h)
        arcTo(Rect(0.06f * w, 0.18f * h, 0.94f * w, 0.94f * h), 270f, 90f, false)
        lineTo(0.94f * w, h); close()
    }
    TemaId.CIBER -> RectangleShape
    TemaId.NEON -> androidx.compose.foundation.shape.RoundedCornerShape(50)   // la placa luminosa del puesto
    TemaId.HELENO -> poligonoBoton(0.08f to 1f, 0.08f to 0.3f, 0.5f to 0.02f, 0.92f to 0.3f, 0.92f to 1f)
    // Pilono: muros en talud y la gola que se abre arriba.
    TemaId.EGIPTO -> poligonoBoton(0.14f to 1f, 0.2f to 0.16f, 0.06f to 0.02f, 0.94f to 0.02f, 0.8f to 0.16f, 0.86f to 1f)
    TemaId.MEDIEVAL -> GenericShape { s, _ ->
        // Cuatro lóbulos que se cortan a 80,2° de su eje: un solo contorno, para que el borde no cruce por dentro.
        val d = 0.22f * s.width; val q = 0.27f * s.width; val cx = s.width / 2; val cy = s.height / 2
        listOf(0f, -90f, 180f, 90f).forEachIndexed { i, fi ->
            val ox = cx + d * cos(rad(fi.toDouble())); val oy = cy + d * sin(rad(fi.toDouble()))
            arcTo(Rect(ox - q, oy - q, ox + q, oy + q), fi + 80.2f, -160.4f, i == 0)
        }
        close()
    }
    // Torre del homenaje con tres merlones.
    TemaId.TEMPLARIO -> poligonoBoton(0.12f to 1f, 0.12f to 0.04f, 0.3f to 0.04f, 0.3f to 0.2f, 0.41f to 0.2f, 0.41f to 0.04f, 0.59f to 0.04f,
        0.59f to 0.2f, 0.7f to 0.2f, 0.7f to 0.04f, 0.88f to 0.04f, 0.88f to 1f)
    TemaId.DISTOPIA -> poligonoBoton(0.04f to 0.12f, 0.4f to 0.12f, 0.48f to 0.24f, 0.96f to 0.24f, 0.96f to 0.92f, 0.04f to 0.92f)
    TemaId.REFUGIO -> RoundedCornerShape(30)
    TemaId.ORDEN -> GenericShape { s, _ ->
        moveTo(0.1f * s.width, 0.06f * s.height); lineTo(0.9f * s.width, 0.06f * s.height); lineTo(0.9f * s.width, 0.5f * s.height)
        quadraticTo(0.9f * s.width, 0.85f * s.height, 0.5f * s.width, 0.98f * s.height)
        quadraticTo(0.1f * s.width, 0.85f * s.height, 0.1f * s.width, 0.5f * s.height); close()
    }
    // La placa de nombre de una locomotora: arco arriba, recta abajo, con las orejas de los pernos.
    TemaId.VAPOR -> GenericShape { s, _ ->
        moveTo(0.02f * s.width, 0.94f * s.height); lineTo(0.02f * s.width, 0.4f * s.height); lineTo(0.1f * s.width, 0.4f * s.height)
        quadraticTo(0.5f * s.width, -0.12f * s.height, 0.9f * s.width, 0.4f * s.height)
        lineTo(0.98f * s.width, 0.4f * s.height); lineTo(0.98f * s.width, 0.94f * s.height); close()
    }
    TemaId.LABORATORIO -> estrellaBoton(6, 0.48f, 0.48f, -90.0)
    TemaId.COLONOS -> GenericShape { s, _ ->
        // Boleto de feria: esquinas mordidas hacia dentro.
        val x0 = 0.04f * s.width; val x1 = 0.96f * s.width; val y0 = 0.1f * s.height; val y1 = 0.9f * s.height; val k = 0.14f * s.width
        moveTo(x0 + k, y0); lineTo(x1 - k, y0)
        arcTo(Rect(x1 - k, y0 - k, x1 + k, y0 + k), 180f, -90f, false); lineTo(x1, y1 - k)
        arcTo(Rect(x1 - k, y1 - k, x1 + k, y1 + k), 270f, -90f, false); lineTo(x0 + k, y1)
        arcTo(Rect(x0 - k, y1 - k, x0 + k, y1 + k), 0f, -90f, false); lineTo(x0, y0 + k)
        arcTo(Rect(x0 - k, y0 - k, x0 + k, y0 + k), 90f, -90f, false); close()
    }
    TemaId.NAPOLEON -> GenericShape { s, _ -> addOval(Rect(0.12f * s.width, 0.02f * s.height, 0.88f * s.width, 0.98f * s.height)) }
    TemaId.BOTANICO -> GenericShape { s, _ ->
        moveTo(0.06f * s.width, 0.94f * s.height)
        quadraticTo(-0.1f * s.width, -0.1f * s.height, 0.94f * s.width, 0.06f * s.height)
        quadraticTo(1.1f * s.width, 1.1f * s.height, 0.06f * s.width, 0.94f * s.height); close()
    }
    // Un trozo de hormigón partido: siete lados desiguales.
    TemaId.RUINAS -> GenericShape { s, _ ->
        moveTo(0.18f * s.width, 0.06f * s.height); lineTo(0.7f * s.width, 0.02f * s.height); lineTo(0.96f * s.width, 0.3f * s.height)
        lineTo(0.92f * s.width, 0.78f * s.height); lineTo(0.62f * s.width, 0.98f * s.height); lineTo(0.14f * s.width, 0.9f * s.height); lineTo(0.03f * s.width, 0.42f * s.height); close()
    }
    TemaId.NAVIDAD -> GenericShape { s, _ ->
        // Bola con su capuchón: el círculo (centro 0,57; radio 0,43) sale del capuchón a −76,55° y vuelve a −103,45°.
        moveTo(0.4f * s.width, 0.02f * s.height); lineTo(0.6f * s.width, 0.02f * s.height); lineTo(0.6f * s.width, 0.152f * s.height)
        arcTo(Rect(0.07f * s.width, 0.14f * s.height, 0.93f * s.width, 1f * s.height), -76.55f, 333.1f, false); close()
    }
    TemaId.SEDA -> CutCornerShape(30)
    // Kikkō: el hexágono del caparazón de tortuga, un motivo de buena suerte.
    TemaId.EDO -> GenericShape { s, _ ->
        moveTo(0.16f * s.width, 0f); lineTo(0.84f * s.width, 0f); lineTo(s.width, 0.5f * s.height); lineTo(0.84f * s.width, s.height)
        lineTo(0.16f * s.width, s.height); lineTo(0f, 0.5f * s.height); close()
    }
    TemaId.GOTICO -> GenericShape { s, _ ->
        moveTo(0.14f * s.width, s.height); lineTo(0.14f * s.width, 0.45f * s.height)
        quadraticTo(0.14f * s.width, 0.1f * s.height, 0.5f * s.width, 0f)
        quadraticTo(0.86f * s.width, 0.1f * s.height, 0.86f * s.width, 0.45f * s.height); lineTo(0.86f * s.width, s.height); close()
    }
    // El sello de lacre: un círculo con la orilla derramada en catorce gotas desiguales.
    TemaId.ARCANO -> GenericShape { s, _ ->
        val n = 14
        for (i in 0..n) {
            val a = rad(-90.0 + 360.0 * i / n); val rr = if (i % 2 == 0) 0.5f else (if (i % 3 == 0) 0.43f else 0.46f)
            val x = s.width * (0.5f + rr * cos(a)); val y = s.height * (0.5f + rr * sin(a))
            if (i == 0) moveTo(x, y) else {
                val am = rad(-90.0 + 360.0 * (i - 0.5) / n)
                quadraticTo(s.width * (0.5f + 0.53f * cos(am)), s.height * (0.5f + 0.53f * sin(am)), x, y)
            }
        }
        close()
    }
    TemaId.ESPACIO -> CircleShape
    // Una gota de agua.
    TemaId.ABISMO -> GenericShape { s, _ ->
        moveTo(0.5f * s.width, 0.02f * s.height)
        cubicTo(0.62f * s.width, 0.22f * s.height, 0.94f * s.width, 0.42f * s.height, 0.94f * s.width, 0.64f * s.height)
        cubicTo(0.94f * s.width, 0.88f * s.height, 0.72f * s.width, 0.98f * s.height, 0.5f * s.width, 0.98f * s.height)
        cubicTo(0.28f * s.width, 0.98f * s.height, 0.06f * s.width, 0.88f * s.height, 0.06f * s.width, 0.64f * s.height)
        cubicTo(0.06f * s.width, 0.42f * s.height, 0.38f * s.width, 0.22f * s.height, 0.5f * s.width, 0.02f * s.height); close()
    }
    TemaId.ANDALUS -> estrellaBoton(16, 0.5f, 0.383f, -90.0)
    // La cruz escalonada de los frisos (el «quincunce» de los cuatro rumbos y el centro).
    TemaId.MESO -> poligonoBoton(0.32f to 0.02f, 0.68f to 0.02f, 0.68f to 0.16f, 0.84f to 0.16f, 0.84f to 0.32f, 0.98f to 0.32f, 0.98f to 0.68f,
        0.84f to 0.68f, 0.84f to 0.84f, 0.68f to 0.84f, 0.68f to 0.98f, 0.32f to 0.98f, 0.32f to 0.84f, 0.16f to 0.84f, 0.16f to 0.68f,
        0.02f to 0.68f, 0.02f to 0.32f, 0.16f to 0.32f, 0.16f to 0.16f, 0.32f to 0.16f)
    TemaId.MONGOL -> poligonoBoton(0.04f to 0.96f, 0.04f to 0.46f, 0.36f to 0.06f, 0.64f to 0.06f, 0.96f to 0.46f, 0.96f to 0.96f)
    TemaId.RENACIMIENTO -> poligonoBoton(0.5f to 0f, 1f to 0.5f, 0.5f to 1f, 0f to 0.5f)
    // El doblón: una moneda acuñada a martillo, de canto irregular.
    TemaId.PIRATA -> poligonoBoton(0.5f to 0.02f, 0.8f to 0.1f, 0.97f to 0.36f, 0.95f to 0.68f, 0.76f to 0.93f, 0.46f to 0.98f, 0.18f to 0.86f, 0.03f to 0.6f, 0.06f to 0.28f, 0.24f to 0.08f)
    TemaId.URSS -> poligonoBoton(0.22f to 0.06f, 1f to 0.06f, 0.78f to 0.94f, 0f to 0.94f)
    TemaId.REVOLUCION -> estrellaBoton(48, 0.5f, 0.45f, -90.0)
    TemaId.ANTIGUAS -> poligonoBoton(0.12f to 0.1f, 0.55f to 0.02f, 0.9f to 0.14f, 0.98f to 0.55f, 0.84f to 0.94f, 0.4f to 0.98f, 0.06f to 0.8f, 0.02f to 0.4f)
    // Un canto rodado del río: casi redondo, un poco más ancho abajo.
    TemaId.CUEVA -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(0.5f * w, 0.05f * h); quadraticTo(0.95f * w, 0.02f * h, 0.97f * w, 0.52f * h); quadraticTo(0.96f * w, 0.97f * h, 0.52f * w, 0.97f * h)
        quadraticTo(0.03f * w, 0.96f * h, 0.04f * w, 0.5f * h); quadraticTo(0.07f * w, 0.07f * h, 0.5f * w, 0.05f * h); close()
    }
}

/** La palabra bajo el engrane de «Más», en la voz de cada tema (la descripción para lectores de pantalla sigue en español). */
private fun textoMas(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "Cetera"
    TemaId.URBE -> "Di più"
    TemaId.CIBER -> "más_"
    TemaId.NEON -> "más ▸"
    TemaId.HELENO -> "Πλέον"
    TemaId.EGIPTO -> "Más rollos"
    TemaId.MEDIEVAL -> "Ítem"
    TemaId.TEMPLARIO -> "Armería"
    TemaId.DISTOPIA -> "Anexo"
    TemaId.REFUGIO -> "Menú"
    TemaId.ORDEN -> "Taller"
    TemaId.VAPOR -> "Depósito"
    TemaId.LABORATORIO -> "Índice"
    TemaId.COLONOS -> "Tablón"
    TemaId.NAPOLEON -> "Plus"
    TemaId.BOTANICO -> "Notas"
    TemaId.RUINAS -> "Mochila"
    TemaId.NAVIDAD -> "Baúl"
    TemaId.SEDA -> "更多"
    TemaId.EDO -> "Posada"
    TemaId.GOTICO -> "Atrio"
    TemaId.ARCANO -> "Cajones"
    TemaId.ESPACIO -> "Panel"
    TemaId.ABISMO -> "Escotilla"
    TemaId.ANDALUS -> "مزيد"
    TemaId.MESO -> "Uláak'"                  // «otro», en maya yucateco
    TemaId.MONGOL -> "Илүү"
    TemaId.RENACIMIENTO -> "Altro"
    TemaId.PIRATA -> "Bodega"
    TemaId.URSS -> "Ещё"
    TemaId.REVOLUCION -> "Club"
    TemaId.ANTIGUAS -> "Morral"
    TemaId.CUEVA -> "Lo hondo"
}

/** El sol (se ofrece en oscuro, para pasar a claro) o la luna (en claro, para pasar a oscuro), a la manera de cada tema. */
private fun DrawScope.astroModo(tema: TemaId, esSol: Boolean, color: Color, fondo: Color) {
    val c = center; val r = size.minDimension / 2
    fun rayos(n: Int, ini: Float, fin: Float, g: Float, desde: Double = 0.0) {
        for (i in 0 until n) { val a = rad(desde + 360.0 * i / n); drawLine(color, c.mas(r * ini, a), c.mas(r * fin, a), r * g, StrokeCap.Round) }
    }
    fun espina(a: Float, ini: Float, fin: Float, abre: Float) {
        val p1 = c.mas(r * ini, a - abre); val p2 = c.mas(r * fin, a); val p3 = c.mas(r * ini, a + abre)
        drawPath(Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close() }, color)
    }
    /** Luna creciente: un disco al que otro, del color del fondo, le muerde un lado ([dx], [dy]: hacia dónde muerde). */
    fun luna(rr: Float = 0.78f, dx: Float = 0.38f, dy: Float = -0.3f, centro: Offset = c) {
        drawCircle(color, r * rr, centro); drawCircle(fondo, r * rr * 0.86f, centro + Offset(r * dx, r * dy))
    }
    fun estrella(p: Offset, s: Float, puntas: Int, interior: Float) {
        val est = Path()
        for (i in 0 until puntas * 2) { val rr = if (i % 2 == 0) s else s * interior; val q = p.mas(rr, rad(-90.0 + 180.0 * i / puntas)); if (i == 0) est.moveTo(q.x, q.y) else est.lineTo(q.x, q.y) }
        est.close(); drawPath(est, color)
    }
    fun destello(p: Offset, s: Float) = drawPath(Path().apply {
        moveTo(p.x, p.y - s); quadraticTo(p.x, p.y, p.x + s, p.y); quadraticTo(p.x, p.y, p.x, p.y + s); quadraticTo(p.x, p.y, p.x - s, p.y); quadraticTo(p.x, p.y, p.x, p.y - s); close()
    }, color)
    if (esSol) {
        when (tema) {
            // Sol invicto: disco y ocho rayos rectos, largos y cortos.
            TemaId.ROMANO -> { drawCircle(color, r * 0.38f, c); for (i in 0 until 8) { val a = rad(45.0 * i); drawLine(color, c.mas(r * 0.55f, a), c.mas(r * (if (i % 2 == 0) 0.96f else 0.76f), a), r * 0.13f, StrokeCap.Round) } }
            // El sol que se pone detrás de la cúpula: medio disco con sus rayos, sobre el perfil del tambor.
            TemaId.URBE -> { drawArc(color, 180f, 180f, true, Offset(c.x - r * 0.5f, c.y - r * 0.4f), Size(r, r))
                for (i in 0 until 7) { val a = rad(180.0 + 30.0 * i); drawLine(color, c.mas(r * 0.66f, a) + Offset(0f, r * 0.1f), c.mas(r * 0.96f, a) + Offset(0f, r * 0.1f), r * 0.11f, StrokeCap.Round) }
                drawRect(color, Offset(c.x - r * 0.9f, c.y + r * 0.2f), Size(r * 1.8f, r * 0.12f)) }
            // Sol de píxeles.
            TemaId.CIBER -> { val p = r * 0.22f; drawRect(color, Offset(c.x - p * 1.5f, c.y - p * 1.5f), Size(p * 3, p * 3))
                for (i in 0 until 8) { val q = c.mas(r * 0.82f, rad(45.0 * i)); drawRect(color, Offset(q.x - p / 2, q.y - p / 2), Size(p, p)) } }
            // El sol entre el smog: un disco de tubo con su halo, cortado por tres franjas de neblina.
            TemaId.NEON -> {
                drawCircle(color.copy(alpha = 0.3f), r * 0.62f, c, style = Stroke(r * 0.3f))
                drawCircle(color, r * 0.62f, c, style = Stroke(r * 0.1f))
                listOf(0.15f, 0.38f, 0.6f).forEach { dy -> drawLine(fondo, Offset(c.x - r, c.y + r * dy), Offset(c.x + r, c.y + r * dy), r * 0.08f) }
            }
            TemaId.HELENO -> sol(c, r * 0.95f, color)
            // El horizonte (ajet): el disco del sol entre dos montes.
            TemaId.EGIPTO -> {
                drawCircle(color, r * 0.4f, Offset(c.x, c.y - r * 0.12f))
                drawPath(Path().apply {
                    moveTo(c.x - r * 0.95f, c.y + r * 0.62f); quadraticTo(c.x - r * 0.62f, c.y - r * 0.05f, c.x - r * 0.3f, c.y + r * 0.62f)
                    moveTo(c.x + r * 0.3f, c.y + r * 0.62f); quadraticTo(c.x + r * 0.62f, c.y - r * 0.05f, c.x + r * 0.95f, c.y + r * 0.62f)
                }, color, style = Stroke(r * 0.12f, cap = StrokeCap.Round))
                drawLine(color, Offset(c.x - r, c.y + r * 0.66f), Offset(c.x + r, c.y + r * 0.66f), r * 0.1f)
            }
            // Sol de miniatura: rayos rectos alternados con rayos ondulados.
            TemaId.MEDIEVAL -> { drawCircle(color, r * 0.36f, c)
                for (i in 0 until 16) { val a = rad(22.5 * i)
                    if (i % 2 == 0) drawLine(color, c.mas(r * 0.5f, a), c.mas(r * 0.96f, a), r * 0.11f, StrokeCap.Round)
                    else { val z = Path(); val n = Offset(-sin(a), cos(a)) * (r * 0.07f); val p0 = c.mas(r * 0.5f, a); z.moveTo(p0.x, p0.y)
                        for (k in 1..3) { val q = c.mas(r * (0.5f + 0.12f * k), a) + n * (if (k % 2 == 0) -1f else 1f); z.lineTo(q.x, q.y) }
                        drawPath(z, color, style = Stroke(r * 0.08f, cap = StrokeCap.Round)) } } }
            // El sol del desierto sobre la arena: disco, doce rayos en punta y la línea de las dunas debajo.
            TemaId.TEMPLARIO -> { drawCircle(color, r * 0.34f, c.copy(y = c.y - r * 0.08f))
                for (i in 0 until 12) { val a = rad(30.0 * i - 90); val o = c.copy(y = c.y - r * 0.08f); val n = Offset(-sin(a), cos(a)) * (r * 0.07f)
                    val b = o.mas(r * 0.46f, a); val p = o.mas(r * (if (i % 2 == 0) 0.86f else 0.66f), a)
                    drawPath(Path().apply { moveTo(b.x + n.x, b.y + n.y); lineTo(p.x, p.y); lineTo(b.x - n.x, b.y - n.y); close() }, color) }
                drawPath(Path().apply { moveTo(c.x - r, c.y + r * 0.86f); quadraticTo(c.x - r * 0.5f, c.y + r * 0.62f, c.x, c.y + r * 0.84f); quadraticTo(c.x + r * 0.5f, c.y + r * 1.02f, c.x + r, c.y + r * 0.78f) },
                    color, style = Stroke(r * 0.09f, cap = StrokeCap.Round)) }
            // Sol tachado por el censor.
            TemaId.DISTOPIA -> { drawCircle(color, r * 0.62f, c); listOf(-0.3f, 0f, 0.3f).forEach { dy -> drawRect(fondo, Offset(c.x - r, c.y + r * dy - r * 0.05f), Size(r * 2, r * 0.1f)) }
                drawCircle(color, r * 0.9f, c, style = Stroke(r * 0.08f)) }
            // Destello atómico de ocho puntas.
            TemaId.REFUGIO -> { for (i in 0 until 8) espina(rad(45.0 * i + 22.5), 0.2f, if (i % 2 == 0) 0.98f else 0.66f, 0.4f); drawCircle(color, r * 0.26f, c) }
            // Engrane.
            TemaId.ORDEN -> { drawCircle(color, r * 0.62f, c); for (i in 0 until 10) { val a = rad(36.0 * i); drawLine(color, c.mas(r * 0.55f, a), c.mas(r * 0.92f, a), r * 0.22f) }
                drawCircle(fondo, r * 0.24f, c) }
            // Un sol engranado: el disco con doce dientes, su eje y el hueco del cubo.
            TemaId.VAPOR -> { drawPath(Vapor.engrane(c, r * 0.92f, 12, 0.76f), color); drawCircle(fondo, r * 0.4f, c); drawCircle(color, r * 0.2f, c) }
            // Disco dentro de una escala graduada.
            TemaId.LABORATORIO -> { drawCircle(color, r * 0.4f, c)
                for (i in 0 until 24) { val a = rad(15.0 * i); drawLine(color, c.mas(r * (if (i % 6 == 0) 0.56f else 0.74f), a), c.mas(r * 0.94f, a), r * (if (i % 6 == 0) 0.12f else 0.06f)) } }
            // Sol que sale sobre el horizonte, como en el parte del tiempo de la gaceta.
            TemaId.COLONOS -> { val o = Offset(c.x, c.y + r * 0.35f); drawArc(color, 180f, 180f, true, Offset(o.x - r * 0.45f, o.y - r * 0.45f), Size(r * 0.9f, r * 0.9f))
                for (i in 0 until 5) { val a = rad(-150.0 + 30.0 * i); drawLine(color, o.mas(r * 0.6f, a), o.mas(r * 0.95f, a), r * 0.12f, StrokeCap.Round) }
                drawLine(color, Offset(c.x - r, o.y + r * 0.08f), Offset(c.x + r, o.y + r * 0.08f), r * 0.1f) }
            // Sol de Austerlitz: doce rayos, uno de cada tres más largo.
            TemaId.NAPOLEON -> { drawCircle(color, r * 0.36f, c)
                for (i in 0 until 12) { val a = rad(30.0 * i); drawLine(color, c.mas(r * 0.5f, a), c.mas(r * (if (i % 3 == 0) 0.96f else 0.72f), a), r * (if (i % 3 == 0) 0.13f else 0.08f), StrokeCap.Round) } }
            // Girasol.
            TemaId.BOTANICO -> { for (i in 0 until 8) rotate(45f * i, c) { drawOval(color, Offset(c.x - r * 0.15f, c.y - r * 0.98f), Size(r * 0.3f, r * 0.5f)) }
                drawCircle(color, r * 0.36f, c); drawCircle(fondo, r * 0.18f, c) }
            // El sol que sale entre dos edificios rotos (recortados con el color del fondo).
            TemaId.RUINAS -> { drawCircle(color, r * 0.42f, c); rayos(8, 0.58f, 0.9f, 0.1f, 22.5)
                drawPath(Path().apply { moveTo(c.x - r * 0.9f, c.y + r); lineTo(c.x - r * 0.9f, c.y + r * 0.2f); lineTo(c.x - r * 0.62f, c.y + r * 0.05f); lineTo(c.x - r * 0.5f, c.y + r * 0.25f); lineTo(c.x - r * 0.3f, c.y + r * 0.18f); lineTo(c.x - r * 0.3f, c.y + r); close() }, fondo)
                drawPath(Path().apply { moveTo(c.x + r * 0.15f, c.y + r); lineTo(c.x + r * 0.15f, c.y + r * 0.35f); lineTo(c.x + r * 0.4f, c.y + r * 0.42f); lineTo(c.x + r * 0.55f, c.y + r * 0.3f); lineTo(c.x + r * 0.8f, c.y + r * 0.4f); lineTo(c.x + r * 0.8f, c.y + r); close() }, fondo) }
            // Estrella de Belén: la punta de abajo más larga.
            TemaId.NAVIDAD -> { listOf(-90.0 to 0.8f, 0.0 to 0.8f, 180.0 to 0.8f, 90.0 to 1f).forEach { (g, l) -> espina(rad(g), 0.16f, l, 0.6f) }; rayos(4, 0.2f, 0.5f, 0.08f, 45.0) }
            // Moneda solar con su agujero cuadrado.
            TemaId.SEDA -> { drawCircle(color, r * 0.48f, c); val h = r * 0.15f; drawRect(fondo, Offset(c.x - h, c.y - h), Size(h * 2, h * 2)); rayos(8, 0.66f, 0.94f, 0.12f, 22.5) }
            // Sol rojo cruzado por dos bandas de nube (kasumi), como en los grabados.
            TemaId.EDO -> { drawCircle(color, r * 0.66f, c)
                drawRoundRect(fondo, Offset(c.x - r, c.y + r * 0.12f), Size(r * 1.35f, r * 0.17f), CornerRadius(r * 0.085f))
                drawRoundRect(fondo, Offset(c.x - r * 0.25f, c.y - r * 0.34f), Size(r * 1.25f, r * 0.15f), CornerRadius(r * 0.075f)) }
            // Rosetón de lancetas.
            TemaId.GOTICO -> { for (i in 0 until 8) espina(rad(45.0 * i - 90), 0.5f, 0.98f, 0.22f)
                drawCircle(color, r * 0.44f, c, style = Stroke(r * 0.1f)); rayos(8, 0.1f, 0.4f, 0.06f); drawCircle(color, r * 0.12f, c) }
            // La lámpara del faro: el disco y sus dos haces largos, a un lado y al otro.
            TemaId.ARCANO -> { drawCircle(color, r * 0.3f, c); drawCircle(color, r * 0.44f, c, style = Stroke(r * 0.06f))
                listOf(0.0, 180.0).forEach { g -> val a = rad(g); val p1 = c.mas(r * 0.5f, a - 0.12f); val p2 = c.mas(r * 1.0f, a - 0.34f); val p3 = c.mas(r * 1.0f, a + 0.34f); val p4 = c.mas(r * 0.5f, a + 0.12f)
                    drawPath(Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close() }, color.copy(alpha = color.alpha * 0.6f)) } }
            // Estrella con un planeta en su órbita.
            TemaId.ESPACIO -> { drawCircle(color, r * 0.34f, c)
                rotate(-25f, c) { drawOval(color, Offset(c.x - r * 0.95f, c.y - r * 0.32f), Size(r * 1.9f, r * 0.64f), style = Stroke(r * 0.08f)) }
                drawCircle(color, r * 0.13f, c.mas(r * 0.95f, rad(-25.0))) }
            // El sol visto desde abajo del agua: el disco, sus haces que bajan y la superficie ondulada encima.
            TemaId.ABISMO -> { val s = c + Offset(0f, -r * 0.3f); drawCircle(color, r * 0.32f, s)
                for (i in 0 until 5) { val a = rad(55.0 + 17.5 * i); drawLine(color, s.mas(r * 0.46f, a), s.mas(r * (if (i % 2 == 0) 0.98f else 0.8f), a), r * 0.08f, StrokeCap.Round) }
                val ola = Path(); var x = c.x - r; ola.moveTo(x, c.y - r * 0.72f)
                while (x <= c.x + r) { ola.lineTo(x, c.y - r * 0.72f + r * 0.07f * sin((x - c.x) / r * 9f)); x += maxOf(r * 0.05f, 1f) }
                drawPath(ola, color, style = Stroke(r * 0.08f, cap = StrokeCap.Round)) }
            // Estrella de ocho puntas de lazo.
            TemaId.ANDALUS -> { estrella(c, r * 0.95f, 8, 0.77f); drawCircle(fondo, r * 0.2f, c) }
            // K'in, el sol: la flor de cuatro pétalos con un punto en el centro y cuatro rayos cortos entre los pétalos.
            TemaId.MESO -> { for (k in 0 until 4) rotate(90f * k, c) { drawOval(color, Offset(c.x - r * 0.2f, c.y - r * 0.9f), Size(r * 0.4f, r * 0.62f)) }
                drawCircle(color, r * 0.3f, c); drawCircle(fondo, r * 0.12f, c)
                for (k in 0 until 4) { val a = rad(45.0 + 90.0 * k); drawLine(color, c.mas(r * 0.44f, a), c.mas(r * 0.8f, a), r * 0.08f, StrokeCap.Round) } }
            // Sol con tres lenguas de fuego encima, de la estepa.
            TemaId.MONGOL -> { drawCircle(color, r * 0.4f, c + Offset(0f, r * 0.3f))
                for (k in -1..1) { val b = Offset(c.x + k * r * 0.26f, c.y - r * 0.2f); val alto = if (k == 0) 0.75f else 0.5f
                    drawPath(Path().apply { moveTo(b.x - r * 0.12f, b.y); quadraticTo(b.x - r * 0.1f, b.y - r * alto * 0.6f, b.x, b.y - r * alto); quadraticTo(b.x + r * 0.1f, b.y - r * alto * 0.6f, b.x + r * 0.12f, b.y); close() }, color) } }
            // Sol de cuaderno: círculo y cuadrado de proporciones.
            TemaId.RENACIMIENTO -> { drawCircle(color, r * 0.8f, c, style = Stroke(r * 0.08f)); val q = r * 0.56f
                drawRect(color, Offset(c.x - q, c.y - q), Size(q * 2, q * 2), style = Stroke(r * 0.06f)); drawCircle(color, r * 0.28f, c); rayos(8, 0.84f, 0.98f, 0.08f) }
            // El sol de las cartas antiguas: rayos rectos y rayos ondulados, uno y uno.
            TemaId.PIRATA -> { drawCircle(color, r * 0.4f, c); rayos(8, 0.52f, 0.98f, 0.09f)
                for (i in 0 until 8) { val a = rad(22.5 + 45.0 * i); val p = Path(); p.moveTo(c.mas(r * 0.5f, a).x, c.mas(r * 0.5f, a).y)
                    val m1 = c.mas(r * 0.62f, a + 0.16f); val m2 = c.mas(r * 0.74f, a - 0.16f); val f = c.mas(r * 0.84f, a)
                    p.cubicTo(m1.x, m1.y, m2.x, m2.y, f.x, f.y); drawPath(p, color, style = Stroke(r * 0.07f, cap = StrokeCap.Round)) } }
            // Sol que sale de una esquina, con rayos en abanico, como en los carteles.
            TemaId.URSS -> { val o = Offset(c.x - r * 0.6f, c.y + r * 0.6f); drawArc(color, -90f, 90f, true, Offset(o.x - r * 0.6f, o.y - r * 0.6f), Size(r * 1.2f, r * 1.2f))
                for (i in 0 until 5) { val a = rad(-90.0 + 22.5 * i); drawLine(color, o.mas(r * 0.78f, a), o.mas(r * 1.5f, a), r * 0.12f) } }
            // Sol escarapela: tres anillos y doce rayos.
            TemaId.REVOLUCION -> { drawCircle(color, r * 0.56f, c, style = Stroke(r * 0.12f)); drawCircle(color, r * 0.3f, c, style = Stroke(r * 0.1f)); drawCircle(color, r * 0.1f, c); rayos(12, 0.72f, 0.96f, 0.09f) }
            // Sol de runas: seis rayos que se abren en horquilla.
            TemaId.ANTIGUAS -> { drawCircle(color, r * 0.34f, c)
                for (i in 0 until 6) { val a = rad(60.0 * i - 90); val m = c.mas(r * 0.7f, a)
                    drawLine(color, c.mas(r * 0.46f, a), m, r * 0.11f); drawLine(color, m, m.mas(r * 0.26f, a - 0.6f), r * 0.09f); drawLine(color, m, m.mas(r * 0.26f, a + 0.6f), r * 0.09f) } }
            // El sol de los grabados en la roca: un anillo con su punto y diez rayos, uno largo y uno corto.
            TemaId.CUEVA -> { drawCircle(color, r * 0.42f, c, style = Stroke(r * 0.12f)); drawCircle(color, r * 0.13f, c)
                for (i in 0 until 10) { val a = rad(36.0 * i - 90); drawLine(color, c.mas(r * 0.62f, a), c.mas(r * (if (i % 2 == 0) 0.98f else 0.8f), a), r * 0.1f, StrokeCap.Round) } }
        }
    } else {
        when (tema) {
            // Lúnula: la luna con los cuernos arriba y su argolla, como el amuleto.
            TemaId.ROMANO -> { luna(0.72f, 0f, -0.36f, c + Offset(0f, r * 0.12f)); drawCircle(color, r * 0.14f, Offset(c.x, c.y - r * 0.72f), style = Stroke(r * 0.07f)) }
            // La luna creciente sobre una cúpula con su linterna.
            TemaId.URBE -> { luna(0.5f, 0.28f, -0.2f, c + Offset(r * 0.28f, -r * 0.42f))
                drawArc(color, 180f, 180f, true, Offset(c.x - r * 0.62f, c.y + r * 0.0f), Size(r * 1.24f, r * 1.0f))
                drawRect(color, Offset(c.x - r * 0.62f, c.y + r * 0.5f), Size(r * 1.24f, r * 0.34f))
                drawRect(color, Offset(c.x - r * 0.07f, c.y - r * 0.18f), Size(r * 0.14f, r * 0.2f)) }
            // Luna de píxeles.
            TemaId.CIBER -> { val p = r * 0.34f
                listOf(-1 to -2, 0 to -2, 1 to -2, -2 to -1, -1 to -1, -2 to 0, -1 to 0, -2 to 1, -1 to 1, -1 to 2, 0 to 2, 1 to 2).forEach { (i, j) ->
                    drawRect(color, Offset(c.x + (i - 0.5f) * p, c.y + (j - 0.5f) * p), Size(p * 0.9f, p * 0.9f)) } }
            // Una luna de tubo de neón con su halo.
            TemaId.NEON -> {
                val arco = Path().apply { addArc(Rect(c.x - r * 0.7f, c.y - r * 0.7f, c.x + r * 0.7f, c.y + r * 0.7f), 100f, 250f) }
                drawPath(arco, color.copy(alpha = 0.3f), style = Stroke(r * 0.28f, cap = StrokeCap.Round))
                drawPath(arco, color, style = Stroke(r * 0.1f, cap = StrokeCap.Round))
            }
            // Luna con tres estrellas.
            TemaId.HELENO -> { luna(); listOf(Offset(0.6f, -0.55f), Offset(0.85f, -0.1f), Offset(0.55f, 0.4f)).forEach { drawCircle(color, r * 0.08f, c + it * r) } }
            // La luna y Sopdet (Sirio), la estrella de cinco puntas que anunciaba la crecida.
            TemaId.EGIPTO -> {
                luna()
                val s = c + Offset(0.62f, -0.48f) * r
                drawPath(Path().apply { for (k in 0 until 10) { val q = s.mas(if (k % 2 == 0) r * 0.2f else r * 0.08f, rad(-90.0 + 36.0 * k)); if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }, color)
            }
            // Luna en su medallón de miniatura.
            TemaId.MEDIEVAL -> { luna(0.6f, 0.3f, -0.24f); drawCircle(color, r * 0.92f, c, style = Stroke(r * 0.09f)) }
            // Luna de vigilia sobre las almenas.
            TemaId.TEMPLARIO -> { luna(0.56f, 0.32f, -0.26f, c.copy(y = c.y - r * 0.2f))
                for (k in 0 until 3) drawRect(color, Offset(c.x - r * 0.9f + k * r * 0.68f, c.y + r * 0.56f), Size(r * 0.44f, r * 0.26f))
                drawRect(color, Offset(c.x - r * 0.9f, c.y + r * 0.8f), Size(r * 1.8f, r * 0.16f)) }
            // Luna tras los barrotes.
            TemaId.DISTOPIA -> { luna(); listOf(-0.45f, 0f, 0.45f).forEach { dx -> drawLine(color, Offset(c.x + r * dx, c.y - r * 0.95f), Offset(c.x + r * dx, c.y + r * 0.95f), r * 0.08f) } }
            // Luna con una órbita de átomo.
            TemaId.REFUGIO -> { luna(0.62f, 0.3f, -0.24f); rotate(-30f, c) { drawOval(color, Offset(c.x - r * 0.95f, c.y - r * 0.32f), Size(r * 1.9f, r * 0.64f), style = Stroke(r * 0.06f)) } }
            // Luna remachada.
            TemaId.ORDEN -> { luna(); listOf(-40.0, 0.0, 40.0).forEach { d -> drawCircle(fondo, r * 0.08f, c.mas(r * 0.6f, rad(180.0 + d))) } }
            // La luna creciente sobre la chimenea de una fábrica, con su humo.
            TemaId.VAPOR -> { luna(0.52f, 0.28f, -0.22f, c + Offset(r * 0.26f, -r * 0.34f))
                drawPath(Path().apply { moveTo(c.x - r * 0.78f, c.y + r * 0.9f); lineTo(c.x - r * 0.64f, c.y - r * 0.1f); lineTo(c.x - r * 0.4f, c.y - r * 0.1f); lineTo(c.x - r * 0.26f, c.y + r * 0.9f); close() }, color)
                drawCircle(color, r * 0.13f, c + Offset(-r * 0.46f, -r * 0.34f)); drawCircle(color, r * 0.1f, c + Offset(-r * 0.24f, -r * 0.52f)); drawCircle(color, r * 0.07f, c + Offset(-r * 0.04f, -r * 0.66f)) }
            // Media luna de diagrama de fases.
            TemaId.LABORATORIO -> { drawCircle(color, r * 0.8f, c, style = Stroke(r * 0.09f)); drawArc(color, 90f, 180f, true, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f)) }
            // Luna sobre el agua del puerto.
            TemaId.COLONOS -> { luna(0.66f, 0.34f, -0.26f, c + Offset(0f, -r * 0.12f))
                drawLine(color, Offset(c.x - r, c.y + r * 0.78f), Offset(c.x + r, c.y + r * 0.78f), r * 0.1f)
                drawLine(color, Offset(c.x - r * 0.6f, c.y + r * 0.98f), Offset(c.x + r * 0.4f, c.y + r * 0.98f), r * 0.07f) }
            // Luna en su cartela oval.
            TemaId.NAPOLEON -> { luna(0.6f, 0.3f, -0.24f); drawOval(color, Offset(c.x - r * 0.72f, c.y - r * 0.98f), Size(r * 1.44f, r * 1.96f), style = Stroke(r * 0.09f)) }
            // Luna con una hoja.
            TemaId.BOTANICO -> { luna(0.7f, 0.34f, -0.26f, c + Offset(r * 0.1f, -r * 0.1f))
                rotate(-40f, Offset(c.x - r * 0.55f, c.y + r * 0.6f)) { drawOval(color, Offset(c.x - r * 0.85f, c.y + r * 0.48f), Size(r * 0.6f, r * 0.24f)) } }
            // La luna sobre una torre rota, con dos ventanas vacías.
            TemaId.RUINAS -> { luna(0.62f, 0.32f, -0.24f, c + Offset(-r * 0.15f, -r * 0.2f))
                drawPath(Path().apply { moveTo(c.x + r * 0.3f, c.y + r); lineTo(c.x + r * 0.3f, c.y + r * 0.15f); lineTo(c.x + r * 0.45f, c.y + r * 0.05f); lineTo(c.x + r * 0.55f, c.y + r * 0.22f); lineTo(c.x + r * 0.7f, c.y + r * 0.1f); lineTo(c.x + r * 0.7f, c.y + r); close() }, color)
                for (k in 0..1) drawRect(fondo, Offset(c.x + r * 0.42f, c.y + r * (0.4f + 0.28f * k)), Size(r * 0.14f, r * 0.12f)) }
            // Luna con copos.
            TemaId.NAVIDAD -> { luna()
                listOf(Offset(0.6f, -0.55f), Offset(0.62f, 0.45f)).forEach { o -> val q = c + o * r
                    for (k in 0 until 3) { val a = rad(60.0 * k); drawLine(color, q.mas(r * 0.17f, a), q.mas(-r * 0.17f, a), r * 0.06f) } } }
            // Luna con un farolillo colgado.
            TemaId.SEDA -> { luna(0.7f, 0.34f, -0.26f, c + Offset(-r * 0.15f, 0f))
                drawLine(color, Offset(c.x + r * 0.62f, c.y - r), Offset(c.x + r * 0.62f, c.y - r * 0.2f), r * 0.05f)
                drawRoundRect(color, Offset(c.x + r * 0.44f, c.y - r * 0.2f), Size(r * 0.36f, r * 0.46f), CornerRadius(r * 0.12f))
                drawLine(color, Offset(c.x + r * 0.62f, c.y + r * 0.26f), Offset(c.x + r * 0.62f, c.y + r * 0.45f), r * 0.05f) }
            // La luna de la fiesta de otoño (tsukimi) con tres espigas de susuki delante.
            TemaId.EDO -> { luna(0.62f, 0.3f, -0.24f, c + Offset(-r * 0.2f, -r * 0.12f))
                listOf(0.3f, 0.55f, 0.8f).forEachIndexed { i, f ->
                    drawPath(Path().apply { moveTo(c.x + r * (f - 0.1f), c.y + r); quadraticTo(c.x + r * f, c.y + r * 0.3f, c.x + r * (f + 0.25f), c.y - r * (0.05f + i * 0.12f)) }, color, style = Stroke(r * 0.06f, cap = StrokeCap.Round)) } }
            // Luna con dos destellos de bóveda.
            TemaId.GOTICO -> { luna(); destello(c + Offset(r * 0.62f, -r * 0.5f), r * 0.26f); destello(c + Offset(r * 0.7f, r * 0.4f), r * 0.18f) }
            // La luna en medio de sus ocho fases: el almanaque del investigador.
            TemaId.ARCANO -> { luna(0.5f, 0.24f, -0.18f)
                for (i in 0 until 8) { val q = c.mas(r * 0.86f, rad(-90.0 + 45.0 * i)); if (i % 4 == 0) drawCircle(color, r * 0.1f, q, style = Stroke(r * 0.04f)) else drawCircle(color, r * (if (i % 2 == 0) 0.1f else 0.07f), q) } }
            // Luna con cráteres.
            TemaId.ESPACIO -> { drawCircle(color, r * 0.78f, c); drawCircle(fondo, r * 0.66f, c + Offset(r * 0.4f, -r * 0.28f))
                listOf(Offset(-0.45f, 0.1f) to 0.12f, Offset(-0.2f, 0.5f) to 0.09f, Offset(-0.5f, -0.3f) to 0.07f).forEach { (o, k) -> drawCircle(fondo.copy(alpha = 0.55f), r * k, c + o * r) } }
            // La linterna del rape: el anzuelo de luz al final de su varilla curva, en lo oscuro.
            TemaId.ABISMO -> { drawPath(Path().apply { moveTo(c.x - r * 0.8f, c.y + r * 0.85f); quadraticTo(c.x - r * 0.75f, c.y - r * 0.75f, c.x + r * 0.25f, c.y - r * 0.35f) }, color, style = Stroke(r * 0.1f, cap = StrokeCap.Round))
                drawCircle(color.copy(alpha = color.alpha * 0.3f), r * 0.5f, c + Offset(r * 0.3f, -r * 0.1f))
                drawCircle(color, r * 0.28f, c + Offset(r * 0.3f, -r * 0.1f)); drawCircle(fondo, r * 0.1f, c + Offset(r * 0.22f, -r * 0.18f)) }
            // Hilal fino con una estrella de ocho puntas.
            TemaId.ANDALUS -> { luna(0.76f, 0.26f, -0.2f); estrella(c + Offset(r * 0.4f, -r * 0.3f), r * 0.2f, 8, 0.5f) }
            // La luna con su conejo, como la lleva la diosa lunar en las vasijas mayas.
            TemaId.MESO -> { luna(0.8f, 0.34f, -0.18f)
                val q = c + Offset(-r * 0.32f, r * 0.18f)
                drawOval(fondo, Offset(q.x - r * 0.2f, q.y - r * 0.08f), Size(r * 0.4f, r * 0.28f)); drawCircle(fondo, r * 0.1f, q + Offset(r * 0.2f, -r * 0.08f))
                drawOval(fondo, Offset(q.x + r * 0.17f, q.y - r * 0.42f), Size(r * 0.07f, r * 0.3f)); drawOval(fondo, Offset(q.x + r * 0.25f, q.y - r * 0.4f), Size(r * 0.07f, r * 0.28f)) }
            // Luna sobre una yurta.
            TemaId.MONGOL -> { luna(0.62f, 0.3f, -0.24f, c + Offset(r * 0.2f, -r * 0.25f))
                drawArc(color, 180f, 180f, true, Offset(c.x - r * 0.9f, c.y + r * 0.3f), Size(r * 0.9f, r * 0.6f))
                drawRect(color, Offset(c.x - r * 0.9f, c.y + r * 0.6f), Size(r * 0.9f, r * 0.3f)) }
            // Luna con su círculo de construcción y una diagonal a pluma.
            TemaId.RENACIMIENTO -> { luna(); drawCircle(color, r * 0.78f, c, style = Stroke(r * 0.04f)); drawLine(color, Offset(c.x - r, c.y + r), Offset(c.x + r, c.y - r), r * 0.04f) }
            // Luna sobre la mar: la creciente y dos olas abajo.
            TemaId.PIRATA -> { luna(0.62f, 0.3f, -0.24f, c + Offset(0f, -r * 0.16f))
                listOf(0.66f, 0.88f).forEach { y -> val p = Path(); p.moveTo(c.x - r * 0.9f, c.y + r * y)
                    p.quadraticTo(c.x - r * 0.45f, c.y + r * (y - 0.14f), c.x, c.y + r * y); p.quadraticTo(c.x + r * 0.45f, c.y + r * (y + 0.14f), c.x + r * 0.9f, c.y + r * y)
                    drawPath(p, color, style = Stroke(r * 0.08f, cap = StrokeCap.Round)) } }
            // Luna con la estrella roja.
            TemaId.URSS -> { luna(); estrella(c + Offset(r * 0.5f, -r * 0.4f), r * 0.28f, 5, 0.4f) }
            // Luna con una escarapela pequeña.
            TemaId.REVOLUCION -> { luna(); drawCircle(color, r * 0.2f, c + Offset(r * 0.58f, r * 0.42f), style = Stroke(r * 0.07f)); drawCircle(color, r * 0.07f, c + Offset(r * 0.58f, r * 0.42f)) }
            // Luna con una gema.
            TemaId.ANTIGUAS -> { luna(); val q = c + Offset(r * 0.6f, -r * 0.45f); val k = r * 0.24f
                drawPath(Path().apply { moveTo(q.x, q.y - k); lineTo(q.x + k * 0.7f, q.y); lineTo(q.x, q.y + k); lineTo(q.x - k * 0.7f, q.y); close() }, color) }
            // La creciente sobre un hueso con muescas, como los que contaban las lunas.
            TemaId.CUEVA -> { luna(0.6f, 0.3f, -0.24f, c + Offset(0f, -r * 0.18f))
                drawLine(color, Offset(c.x - r * 0.8f, c.y + r * 0.72f), Offset(c.x + r * 0.8f, c.y + r * 0.72f), r * 0.16f, StrokeCap.Round)
                for (k in 0 until 7) { val x = c.x - r * 0.6f + k * r * 0.2f; drawLine(fondo, Offset(x, c.y + r * 0.62f), Offset(x, c.y + r * 0.76f), r * 0.05f) } }
        }
    }
}

/** La lupa de cada tema: la forma del cristal, el mango y un detalle dentro o al final. */
private fun DrawScope.lupaTema(tema: TemaId, color: Color) {
    val r = size.minDimension
    val c = Offset(r * 0.42f, r * 0.42f); val lr = r * 0.3f; val g = r * 0.12f
    fun lente() = drawCircle(color, lr, c, style = Stroke(g))
    fun mango(fin: Float = 0.92f, grosor: Float = 0.14f, desde: Float = 0.64f) = drawLine(color, Offset(r * desde, r * desde), Offset(r * fin, r * fin), r * grosor, StrokeCap.Round)
    fun poli(n: Int, rr: Float, desde: Double) = Path().apply {
        for (i in 0 until n) { val p = c.mas(rr, rad(desde + 360.0 * i / n)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close()
    }
    when (tema) {
        // Mango con remate de anillo.
        TemaId.ROMANO -> { lente(); mango(0.84f); drawCircle(color, r * 0.08f, Offset(r * 0.9f, r * 0.9f)) }
        // El cristal con la ventanita de un portón dentro y el mango con su virola.
        TemaId.URBE -> { lente(); mango(0.9f, 0.13f)
            drawPath(Path().apply { moveTo(c.x - lr * 0.38f, c.y + lr * 0.42f); lineTo(c.x - lr * 0.38f, c.y - lr * 0.02f); quadraticTo(c.x, c.y - lr * 0.5f, c.x + lr * 0.38f, c.y - lr * 0.02f); lineTo(c.x + lr * 0.38f, c.y + lr * 0.42f) }, color, style = Stroke(r * 0.06f))
            drawLine(color, Offset(r * 0.7f, r * 0.7f), Offset(r * 0.76f, r * 0.64f), r * 0.2f) }
        // Cristal cuadrado con un renglón dentro.
        TemaId.CIBER -> { drawRect(color, Offset(c.x - lr, c.y - lr), Size(lr * 2, lr * 2), style = Stroke(g)); mango(0.92f, 0.12f, 0.72f)
            drawLine(color, Offset(c.x - lr * 0.45f, c.y + lr * 0.3f), Offset(c.x + lr * 0.45f, c.y + lr * 0.3f), r * 0.08f) }
        // Lupa de tubo de neón: el aro con su halo.
        TemaId.NEON -> { drawCircle(color.copy(alpha = 0.3f), lr, c, style = Stroke(g * 2.6f)); lente(); mango() }
        // Cuentas alrededor, por dentro del cristal.
        TemaId.HELENO -> { lente(); mango(); for (i in 0 until 8) drawCircle(color, r * 0.035f, c.mas(lr * 0.55f, rad(45.0 * i))) }
        // El ankh dentro del cristal.
        TemaId.EGIPTO -> {
            lente(); mango()
            drawCircle(color, lr * 0.22f, Offset(c.x, c.y - lr * 0.28f), style = Stroke(r * 0.05f))
            drawLine(color, Offset(c.x - lr * 0.42f, c.y), Offset(c.x + lr * 0.42f, c.y), r * 0.06f)
            drawLine(color, Offset(c.x, c.y), Offset(c.x, c.y + lr * 0.55f), r * 0.06f)
        }
        // Mango rematado en flor de tres pétalos.
        TemaId.MEDIEVAL -> { lente(); mango(0.82f); listOf(Offset(0.93f, 0.84f), Offset(0.84f, 0.93f), Offset(0.92f, 0.92f)).forEach { drawCircle(color, r * 0.055f, it * r) } }
        // Mango de empuñadura de espada: la guarda en cruz y el pomo.
        TemaId.TEMPLARIO -> { lente(); mango(0.88f, 0.1f, 0.66f)
            drawLine(color, Offset(r * 0.6f, r * 0.84f), Offset(r * 0.84f, r * 0.6f), r * 0.08f, StrokeCap.Round); drawCircle(color, r * 0.065f, Offset(r * 0.92f, r * 0.92f)) }
        // Cruz de mira en el cristal.
        TemaId.DISTOPIA -> { lente(); mango()
            drawLine(color, Offset(c.x - lr * 0.6f, c.y), Offset(c.x + lr * 0.6f, c.y), r * 0.05f); drawLine(color, Offset(c.x, c.y - lr * 0.6f), Offset(c.x, c.y + lr * 0.6f), r * 0.05f) }
        // Mango con dos aros y un brillo en el cristal.
        TemaId.REFUGIO -> { lente(); mango(); listOf(0.74f, 0.83f).forEach { k -> val p = Offset(r * k, r * k); drawLine(color, p + Offset(-r * 0.08f, r * 0.08f), p + Offset(r * 0.08f, -r * 0.08f), r * 0.05f) }
            drawCircle(color, r * 0.05f, c + Offset(-lr * 0.35f, -lr * 0.35f)) }
        // Cristal hexagonal, como una tuerca, y mango grueso.
        TemaId.ORDEN -> { drawPath(poli(6, lr * 1.08f, 0.0), color, style = Stroke(g)); mango(0.92f, 0.18f, 0.7f) }
        // La lupa del relojero con el aro de latón remachado y el mango torneado.
        TemaId.VAPOR -> { lente(); for (i in 0 until 8) drawCircle(color, g * 0.5f, c.mas(lr + g * 0.6f, rad(45.0 * i))); mango(0.9f, 0.12f)
            drawCircle(color, r * 0.07f, Offset(r * 0.78f, r * 0.78f)) }
        // Escala dentro del cristal.
        TemaId.LABORATORIO -> { lente(); mango()
            for (k in -1..1) drawLine(color, Offset(c.x + k * lr * 0.4f, c.y + lr * 0.25f), Offset(c.x + k * lr * 0.4f, c.y + lr * (if (k == 0) -0.2f else 0.02f)), r * 0.04f) }
        // Mango de madera, más grueso al final, y un reflejo.
        TemaId.COLONOS -> { lente(); mango(0.74f, 0.1f); drawLine(color, Offset(r * 0.74f, r * 0.74f), Offset(r * 0.92f, r * 0.92f), r * 0.2f, StrokeCap.Round)
            drawArc(color, 200f, 60f, false, Offset(c.x - lr * 0.6f, c.y - lr * 0.6f), Size(lr * 1.2f, lr * 1.2f), style = Stroke(r * 0.04f)) }
        // Cristal oval y mango con guarda.
        TemaId.NAPOLEON -> { drawOval(color, Offset(c.x - lr * 0.8f, c.y - lr * 1.05f), Size(lr * 1.6f, lr * 2.1f), style = Stroke(g)); mango()
            drawLine(color, Offset(r * 0.62f, r * 0.8f), Offset(r * 0.8f, r * 0.62f), r * 0.07f) }
        // Una hoja vista con la lupa.
        TemaId.BOTANICO -> { lente(); mango(); rotate(-45f, c) { drawOval(color, Offset(c.x - lr * 0.5f, c.y - lr * 0.2f), Size(lr, lr * 0.4f)) } }
        // Un cristal estrellado: la grieta cruza la lente.
        TemaId.RUINAS -> { lente(); mango()
            drawPath(Path().apply { moveTo(c.x - lr * 0.7f, c.y - lr * 0.5f); lineTo(c.x - lr * 0.1f, c.y - lr * 0.05f); lineTo(c.x + lr * 0.15f, c.y - lr * 0.35f); moveTo(c.x - lr * 0.1f, c.y - lr * 0.05f); lineTo(c.x + lr * 0.3f, c.y + lr * 0.6f) }, color, style = Stroke(r * 0.04f)) }
        // Un copo dentro del cristal.
        TemaId.NAVIDAD -> { lente(); mango(); for (k in 0 until 3) { val a = rad(60.0 * k + 90.0); drawLine(color, c.mas(lr * 0.5f, a), c.mas(-lr * 0.5f, a), r * 0.04f) } }
        // Cristal octogonal y borla.
        TemaId.SEDA -> { drawPath(poli(8, lr * 1.04f, 22.5), color, style = Stroke(g)); mango(0.86f)
            drawLine(color, Offset(r * 0.86f, r * 0.86f), Offset(r * 0.8f, r * 0.99f), r * 0.05f); drawLine(color, Offset(r * 0.86f, r * 0.86f), Offset(r * 0.99f, r * 0.8f), r * 0.05f) }
        // El cristal con un sellito cuadrado dentro, como el que se estampa al revisar.
        TemaId.EDO -> { lente(); mango(0.86f)
            drawRect(color, Offset(c.x - lr * 0.3f, c.y - lr * 0.3f), Size(lr * 0.6f, lr * 0.6f), style = Stroke(r * 0.05f))
            drawLine(color, Offset(c.x, c.y - lr * 0.18f), Offset(c.x, c.y + lr * 0.18f), r * 0.05f) }
        // Cristal en arco apuntado.
        TemaId.GOTICO -> { drawPath(Path().apply {
                moveTo(c.x - lr, c.y + lr); lineTo(c.x - lr, c.y); quadraticTo(c.x - lr, c.y - lr * 0.8f, c.x, c.y - lr * 1.2f)
                quadraticTo(c.x + lr, c.y - lr * 0.8f, c.x + lr, c.y); lineTo(c.x + lr, c.y + lr); close() }, color, style = Stroke(g * 0.85f))
            mango(0.92f, 0.14f, 0.72f) }
        // Un ojo que devuelve la mirada desde el cristal.
        TemaId.ARCANO -> { lente(); mango(0.9f, 0.12f)
            drawPath(Path().apply { moveTo(c.x - lr * 0.62f, c.y); quadraticTo(c.x, c.y - lr * 0.62f, c.x + lr * 0.62f, c.y); quadraticTo(c.x, c.y + lr * 0.62f, c.x - lr * 0.62f, c.y); close() }, color, style = Stroke(r * 0.04f))
            drawOval(color, Offset(c.x - lr * 0.08f, c.y - lr * 0.26f), Size(lr * 0.16f, lr * 0.52f)) }
        // Ocular de telescopio: un anillo dentro de otro.
        TemaId.ESPACIO -> { lente(); drawCircle(color, lr * 0.45f, c, style = Stroke(r * 0.04f)); mango() }
        // Ojo de buey: el cristal con su brida de pernos.
        TemaId.ABISMO -> { lente(); mango(0.9f, 0.13f, 0.66f); for (i in 0 until 6) drawCircle(color, r * 0.035f, c.mas(lr * 0.68f, rad(30.0 + 60.0 * i))) }
        // Estrella de lazo dentro del cristal.
        TemaId.ANDALUS -> { lente(); mango()
            val est = Path(); for (i in 0 until 16) { val rr = if (i % 2 == 0) lr * 0.5f else lr * 0.25f; val q = c.mas(rr, rad(-90.0 + 22.5 * i)); if (i == 0) est.moveTo(q.x, q.y) else est.lineTo(q.x, q.y) }
            est.close(); drawPath(est, color) }
        // El espejo de obsidiana de los adivinos: el disco con su marco de cuentas, y un mango de madera.
        TemaId.MESO -> { lente(); mango(0.88f, 0.15f, 0.7f)
            for (i in 0 until 10) drawCircle(color, r * 0.04f, c.mas(lr * 1.18f, rad(36.0 * i)))
            drawCircle(color.copy(alpha = color.alpha * 0.35f), lr * 0.62f, c) }
        // Mango con cola de caballo.
        TemaId.MONGOL -> { lente(); mango(0.82f); listOf(Offset(0.86f, 0.99f), Offset(0.95f, 0.95f), Offset(0.99f, 0.86f)).forEach { drawLine(color, Offset(r * 0.82f, r * 0.82f), it * r, r * 0.045f) } }
        // Línea de construcción que atraviesa el cristal.
        TemaId.RENACIMIENTO -> { lente(); mango(); drawLine(color, Offset(r * 0.02f, r * 0.02f), Offset(c.x + lr * 0.3f, c.y + lr * 0.3f), r * 0.035f)
            drawLine(color, Offset(c.x - lr * 0.25f, c.y + lr * 0.25f), Offset(c.x + lr * 0.25f, c.y - lr * 0.25f), r * 0.035f) }
        // El catalejo: tres tramos de latón que se adelgazan, con la lente al frente.
        TemaId.PIRATA -> {
            val ini = Offset(r * 0.12f, r * 0.12f); val fin = Offset(r * 0.92f, r * 0.92f)
            listOf(Triple(0f, 0.36f, 0.24f), Triple(0.34f, 0.68f, 0.18f), Triple(0.66f, 1f, 0.12f)).forEach { (a, b, g2) ->
                drawLine(color, ini + (fin - ini) * a, ini + (fin - ini) * b, r * g2)
            }
            drawCircle(color, r * 0.15f, ini, style = Stroke(r * 0.05f))
            listOf(0.35f, 0.67f).forEach { k -> val p = ini + (fin - ini) * k; drawLine(color, p + Offset(-r * 0.12f, r * 0.12f), p + Offset(r * 0.12f, -r * 0.12f), r * 0.04f) }
        }
        // Estrella de cinco puntas en el cristal y mango grueso.
        TemaId.URSS -> { lente(); mango(0.92f, 0.17f)
            val est = Path(); for (i in 0 until 10) { val rr = if (i % 2 == 0) lr * 0.55f else lr * 0.22f; val q = c.mas(rr, rad(-90.0 + 36.0 * i)); if (i == 0) est.moveTo(q.x, q.y) else est.lineTo(q.x, q.y) }
            est.close(); drawPath(est, color) }
        // Mango de pica con su punta.
        TemaId.REVOLUCION -> { lente(); mango(0.86f)
            drawPath(Path().apply { moveTo(r * 0.99f, r * 0.99f); lineTo(r * 0.917f, r * 0.803f); lineTo(r * 0.803f, r * 0.917f); close() }, color) }
        // Cristal tallado en rombo, como una gema.
        TemaId.ANTIGUAS -> { drawPath(Path().apply { moveTo(c.x, c.y - lr * 1.15f); lineTo(c.x + lr * 1.0f, c.y); lineTo(c.x, c.y + lr * 1.15f); lineTo(c.x - lr * 1.0f, c.y); close() }, color, style = Stroke(g * 0.9f))
            mango(0.92f, 0.14f, 0.68f) }
        // Una lasca de sílex por cristal y un hueso por mango, con sus dos nudos al final.
        TemaId.CUEVA -> { drawPath(poli(7, lr * 1.08f, -80.0), color, style = Stroke(g * 0.9f, join = StrokeJoin.Round)); mango(0.84f, 0.12f, 0.68f)
            drawCircle(color, r * 0.065f, Offset(r * 0.91f, r * 0.84f)); drawCircle(color, r * 0.065f, Offset(r * 0.84f, r * 0.91f)) }
    }
}

/** Sol o luna dibujados a la manera del tema, para cambiar de modo dentro de la app. El de Terminal no tiene modo claro. */
@Composable
fun BotonModo(modifier: Modifier = Modifier) {
    val tema = Estilo.tema
    if (tema.soloOscuro) return
    val oscuro = esOscuro()
    val sentir = rememberSentir()
    val color = MaterialTheme.colorScheme.primary
    val fondo = MaterialTheme.colorScheme.background
    val borde = MaterialTheme.colorScheme.outline
    val forma = remember(tema) { formaBoton(tema) }
    Box(
        // 48 dp de espacio para el dedo alrededor del marco de 36 (N-27).
        modifier.padding(start = 4.dp).minimumInteractiveComponentSize().size(36.dp).clip(forma).border(1.dp, borde, forma).clickable {
            sentir(Sentidos.Evento.CAMBIO)
            Repo.cambiarConfig { c -> c.copy(modo = if (oscuro) "claro" else "oscuro") }
        }.semantics { contentDescription = if (oscuro) "Cambiar a modo claro" else "Cambiar a modo oscuro" },
        contentAlignment = Alignment.Center,
    ) {
        // En oscuro se ofrece el sol; en claro, la luna.
        Canvas(Modifier.size(18.dp)) { astroModo(tema, oscuro, color, fondo) }
    }
}

/** Lupa para buscar en todo, junto al título de las pestañas principales; su cristal, su mango y su marco son del tema. */
@Composable
fun BotonBuscar(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val tema = Estilo.tema
    val color = MaterialTheme.colorScheme.primary
    val borde = MaterialTheme.colorScheme.outline
    val sentir = rememberSentir()
    val forma = remember(tema) { formaBoton(tema) }
    Box(
        modifier.minimumInteractiveComponentSize().size(36.dp).clip(forma).border(1.dp, borde, forma).clickable { sentir(Sentidos.Evento.TOQUE); onClick() }
            .semantics { contentDescription = "Buscar en todo" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(17.dp)) { lupaTema(tema, color) }
    }
}

// =====================================================================
//  Celebración: cuando se completa el día o la racha llega a una semana
// =====================================================================

object Fiesta {
    var n by mutableIntStateOf(0)
    fun lanzar() { n++ }
}

private class Particula(val ang: Float, val vel: Float, val giro: Float, val tam: Float, val tono: Int, val retraso: Float)

/** Si la lluvia de celebración de [tema] sube (burbujas, brasas, polvo de estrellas) en lugar de caer. */
private fun fiestaSube(tema: TemaId): Boolean = when (tema) {
    TemaId.ROMANO -> false
    TemaId.URBE -> false
    TemaId.CIBER -> false
    TemaId.NEON -> false
    TemaId.HELENO -> false
    TemaId.EGIPTO -> false
    TemaId.MEDIEVAL -> false
    TemaId.TEMPLARIO -> false
    TemaId.DISTOPIA -> false
    TemaId.REFUGIO -> false
    TemaId.ORDEN -> false
    TemaId.VAPOR -> true                        // bocanadas de vapor y engranes que suben
    TemaId.LABORATORIO -> true
    TemaId.COLONOS -> true
    TemaId.NAPOLEON -> false
    TemaId.BOTANICO -> false
    TemaId.RUINAS -> true            // los vilanos suben con el aire tibio
    TemaId.NAVIDAD -> false
    TemaId.SEDA -> false
    TemaId.EDO -> false
    TemaId.GOTICO -> false
    TemaId.ARCANO -> false
    TemaId.ESPACIO -> true
    TemaId.ABISMO -> true                       // burbujas: suben
    TemaId.ANDALUS -> false
    TemaId.MESO -> false
    TemaId.MONGOL -> false
    TemaId.RENACIMIENTO -> false
    TemaId.PIRATA -> false          // los doblones caen
    TemaId.URSS -> false
    TemaId.REVOLUCION -> false
    TemaId.ANTIGUAS -> false
    TemaId.CUEVA -> false            // las manos de ocre y los puntos caen soplados sobre la pared
}

/**
 * Lluvia breve de figuras del tema, una distinta en cada uno: hojas de laurel (ROM), unos y ceros de terminal (CIB),
 * soles de Vergina (HEL), gotas de tinta y de rúbrica (MED), tiras de expediente (DIS), átomos (REF), galones (ORD),
 * burbujas (LAB), brasas que suben (COL), abejas (NAP), escarabajos (BOT), copos (NAV), monedas (SED), esquirlas de
 * vitral (GOT), destellos (ESP), estrellas de lazo (AND), flechas (MON), marcas de compás (REN), estrellas rojas (URS),
 * escarapelas (REV) y gemas (ANT).
 * [fijo]: un instante quieto de la lluvia (entre 0 y 1), para las capturas de diseño.
 */
@Composable
fun CapaFiesta(fijo: Float? = null) {
    val n = if (fijo != null) 1 else Fiesta.n
    if (n == 0 || (fijo == null && !Repo.config.animaciones)) return
    val progreso = remember(n) { androidx.compose.animation.core.Animatable(fijo ?: 0f) }
    var activa by remember(n) { mutableStateOf(true) }
    LaunchedEffect(n) { if (fijo == null) { progreso.animateTo(1f, tween(1600, easing = LinearEasing)); activa = false } }
    if (!activa) return
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val tonos = listOf(e.primary, e.secondary, e.tertiary)
    val parts = remember(n) {
        val azar = java.util.Random(n.toLong())
        List(44) { Particula((azar.nextFloat() * 2 * PI).toFloat(), 0.25f + azar.nextFloat() * 0.75f, azar.nextFloat() * 720f - 360f, 0.6f + azar.nextFloat() * 0.8f, azar.nextInt(3), azar.nextFloat() * 0.25f) }
    }
    // Burbujas, brasas y polvo de estrellas suben; lo demás cae.
    val sube = fiestaSube(tema)
    val oro = if (e.background.luminance() < 0.5f) e.primary else e.tertiary
    Canvas(Modifier.fillMaxSize()) {
        val origen = Offset(size.width / 2, size.height * if (sube) 0.62f else 0.28f)
        val alcance = size.minDimension * 0.55f
        val u = 7.dp.toPx()
        parts.forEach { p ->
            val t = ((progreso.value - p.retraso) / (1f - p.retraso)).coerceIn(0f, 1f)
            if (t <= 0f) return@forEach
            val dx = cos(p.ang) * p.vel * alcance * t
            val dy = sin(p.ang) * p.vel * alcance * t * (if (sube) 0.4f else 1f) + (if (sube) -1f else 1f) * size.height * 0.35f * t * t
            val pos = origen + Offset(dx, dy)
            val a = (1f - t).coerceIn(0f, 1f)
            val col = tonos[p.tono].copy(alpha = a)
            val s = u * p.tam
            rotate(p.giro * t, pos) {
                when (tema) {
                    // Hoja de laurel con su nervio.
                    TemaId.ROMANO -> { drawOval(col, Offset(pos.x - s, pos.y - s * 0.4f), Size(s * 2, s * 0.8f))
                        drawLine(e.background.copy(alpha = a * 0.5f), pos - Offset(s * 0.8f, 0f), pos + Offset(s * 0.8f, 0f), s * 0.08f) }
                    // Pétalos de rosa, como los que caen por el óculo del Panteón en Pentecostés.
                    TemaId.URBE -> { val rosa = lerp(Color(0xFFC8364A), col, 0.35f).copy(alpha = a)
                        drawPath(Path().apply { moveTo(pos.x, pos.y + s * 0.7f); cubicTo(pos.x - s * 1.1f, pos.y, pos.x - s * 0.5f, pos.y - s * 0.9f, pos.x, pos.y - s * 0.55f)
                            cubicTo(pos.x + s * 0.5f, pos.y - s * 0.9f, pos.x + s * 1.1f, pos.y, pos.x, pos.y + s * 0.7f); close() }, rosa)
                        drawLine(e.background.copy(alpha = a * 0.35f), pos + Offset(0f, s * 0.5f), pos - Offset(0f, s * 0.3f), s * 0.07f) }
                    // Unos y ceros de fósforo.
                    TemaId.CIBER -> {
                        val fosforo = e.primary.copy(alpha = a)
                        if (p.tono == 0) {
                            drawOval(fosforo, Offset(pos.x - s * 0.35f, pos.y - s * 0.6f), Size(s * 0.7f, s * 1.2f), style = Stroke(s * 0.2f))
                        } else {
                            drawLine(fosforo, pos - Offset(0f, s * 0.6f), pos + Offset(0f, s * 0.6f), s * 0.22f)
                            drawLine(fosforo, pos - Offset(0f, s * 0.6f), pos - Offset(s * 0.3f, s * 0.35f), s * 0.2f)
                        }
                    }
                    // Tramitos de tubo de neón que caen encendidos, con su halo.
                    TemaId.NEON -> {
                        drawLine(col.copy(alpha = a * 0.3f), pos - Offset(s * 0.7f, 0f), pos + Offset(s * 0.7f, 0f), s * 0.6f, StrokeCap.Round)
                        drawLine(col, pos - Offset(s * 0.7f, 0f), pos + Offset(s * 0.7f, 0f), s * 0.2f, StrokeCap.Round)
                    }
                    // Soles de Vergina pequeños: disco y ocho rayos alternos.
                    TemaId.HELENO -> { for (i in 0 until 8) { val an = rad(45.0 * i); drawLine(col, pos.mas(s * 0.35f, an), pos.mas(if (i % 2 == 0) s else s * 0.7f, an), s * 0.16f, StrokeCap.Round) }
                        drawCircle(col, s * 0.3f, pos) }
                    // Pétalos de loto y estrellas de oro de cinco puntas que caen.
                    TemaId.EGIPTO -> if (p.tono == 0) drawPath(Path().apply { moveTo(pos.x, pos.y - s); quadraticTo(pos.x + s * 0.6f, pos.y, pos.x, pos.y + s); quadraticTo(pos.x - s * 0.6f, pos.y, pos.x, pos.y - s); close() }, col)
                        else drawPath(Path().apply { for (k in 0 until 10) { val q = pos.mas(if (k % 2 == 0) s else s * 0.4f, rad(-90.0 + 36.0 * k)); if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }, col)
                    // Gotas de tinta ferrogálica, de rúbrica y de oro.
                    TemaId.MEDIEVAL -> drawPath(Path().apply {
                        moveTo(pos.x, pos.y - s)
                        cubicTo(pos.x + s * 0.9f, pos.y + s * 0.1f, pos.x + s * 0.5f, pos.y + s * 0.8f, pos.x, pos.y + s * 0.8f)
                        cubicTo(pos.x - s * 0.5f, pos.y + s * 0.8f, pos.x - s * 0.9f, pos.y + s * 0.1f, pos.x, pos.y - s); close()
                    }, listOf(e.onBackground, e.primary, e.tertiary)[p.tono].copy(alpha = a))
                    // Cruces patadas rojas, anillas de la cota de malla y granos de arena dorada.
                    TemaId.TEMPLARIO -> when (p.tono) {
                        0 -> drawPath(cruzPatada(pos, s * 0.9f), e.primary.copy(alpha = a))
                        1 -> { drawCircle(e.secondary.copy(alpha = a), s * 0.45f, pos, style = Stroke(s * 0.16f)); drawCircle(e.secondary.copy(alpha = a), s * 0.45f, pos + Offset(s * 0.5f, 0f), style = Stroke(s * 0.16f)) }
                        else -> drawCircle(e.tertiary.copy(alpha = a), s * 0.28f, pos)
                    }
                    // Tiras de expediente con sus renglones.
                    TemaId.DISTOPIA -> { drawRect(col, Offset(pos.x - s * 0.7f, pos.y - s * 0.45f), Size(s * 1.4f, s * 0.9f))
                        drawLine(e.background.copy(alpha = a * 0.8f), Offset(pos.x - s * 0.5f, pos.y - s * 0.15f), Offset(pos.x + s * 0.5f, pos.y - s * 0.15f), s * 0.1f)
                        drawLine(e.background.copy(alpha = a * 0.8f), Offset(pos.x - s * 0.5f, pos.y + s * 0.15f), Offset(pos.x + s * 0.2f, pos.y + s * 0.15f), s * 0.1f) }
                    // Galones de la Orden.
                    TemaId.ORDEN -> drawPath(Path().apply { moveTo(pos.x - s, pos.y - s * 0.3f); lineTo(pos.x, pos.y + s * 0.4f); lineTo(pos.x + s, pos.y - s * 0.3f) },
                        col, style = Stroke(s * 0.3f, join = StrokeJoin.Miter))
                    // Bocanadas de vapor y engranitos de latón que suben de la caldera.
                    TemaId.VAPOR -> if (p.tono == 0) {
                        val nube = lerp(col, Color.White, 0.55f).copy(alpha = a * 0.8f)
                        drawCircle(nube, s * 0.7f, pos); drawCircle(nube, s * 0.5f, pos + Offset(s * 0.6f, -s * 0.2f)); drawCircle(nube, s * 0.45f, pos + Offset(-s * 0.55f, s * 0.1f))
                    } else {
                        drawPath(Vapor.engrane(pos, s * 0.9f, 8, 0.72f), col); drawCircle(e.background.copy(alpha = a), s * 0.3f, pos)
                    }
                    // Brasas que suben del farol, con su halo y un parpadeo.
                    TemaId.COLONOS -> { val viva = 0.6f + 0.4f * sin(t * 20f + p.giro)
                        val brasa = listOf(Color(0xFFFF8A3D), Color(0xFFFFC14D), e.secondary)[p.tono].copy(alpha = a * viva)
                        drawCircle(brasa.copy(alpha = brasa.alpha * 0.25f), s * 0.9f, pos); drawCircle(brasa, s * 0.38f, pos)
                        drawCircle(Color.White.copy(alpha = a * viva * 0.5f), s * 0.14f, pos) }
                    // Abejas imperiales de oro.
                    TemaId.NAPOLEON -> { val ab = oro.copy(alpha = a)
                        drawOval(ab.copy(alpha = a * 0.55f), Offset(pos.x - s, pos.y - s * 0.5f), Size(s * 0.9f, s * 0.45f))
                        drawOval(ab.copy(alpha = a * 0.55f), Offset(pos.x + s * 0.1f, pos.y - s * 0.5f), Size(s * 0.9f, s * 0.45f))
                        drawOval(ab, Offset(pos.x - s * 0.25f, pos.y - s * 0.45f), Size(s * 0.5f, s * 1.2f)); drawCircle(ab, s * 0.2f, Offset(pos.x, pos.y - s * 0.6f))
                        drawLine(Color.Black.copy(alpha = a * 0.35f), Offset(pos.x - s * 0.22f, pos.y + s * 0.2f), Offset(pos.x + s * 0.22f, pos.y + s * 0.2f), s * 0.08f) }
                    TemaId.LABORATORIO -> { drawCircle(col, s * 0.7f, pos, style = Stroke(s * 0.18f)); drawCircle(Color.White.copy(alpha = a * 0.6f), s * 0.14f, pos + Offset(-s * 0.3f, -s * 0.3f)) }
                    TemaId.REFUGIO -> { drawCircle(col, s * 0.35f, pos); drawOval(col, Offset(pos.x - s, pos.y - s * 0.35f), Size(s * 2, s * 0.7f), style = Stroke(s * 0.12f)) }
                    TemaId.NAVIDAD -> for (i in 0 until 3) { val a = rad(60.0 * i); drawLine(col, pos.mas(s, a), pos.mas(-s, a), s * 0.22f, StrokeCap.Round) }
                    TemaId.BOTANICO -> { drawOval(col, Offset(pos.x - s * 0.55f, pos.y - s * 0.6f), Size(s * 1.1f, s * 1.5f)); drawCircle(col, s * 0.32f, pos - Offset(0f, s * 0.85f))
                        drawLine(col.copy(alpha = col.alpha * 0.6f), pos - Offset(s * 0.9f, s * 0.1f), pos + Offset(s * 0.9f, -s * 0.1f), s * 0.12f) }
                    // Vilanos de diente de león y hojas de hiedra que se elevan girando.
                    TemaId.RUINAS -> if (p.tono == 0) {
                        val cima = pos - Offset(0f, s)
                        drawLine(col, pos, cima, s * 0.1f)
                        for (j in 0 until 7) drawLine(col.copy(alpha = col.alpha * 0.7f), cima, cima.mas(s * 0.7f, rad(180.0 + 30.0 * j)), s * 0.07f)
                        drawCircle(col, s * 0.16f, pos)
                    } else rotate(p.giro * 57f + t * 120f, pos) { hojaHiedraEmblema(pos + Offset(0f, s * 0.5f), s * 1.1f, col) }
                    TemaId.GOTICO -> drawPath(Path().apply { moveTo(pos.x, pos.y - s); lineTo(pos.x + s * 0.7f, pos.y + s * 0.6f); lineTo(pos.x - s * 0.5f, pos.y + s * 0.8f); close() },
                        listOf(e.secondary, e.primary, Color(0xFF2E8B57), Color(0xFFE2B34A))[p.tono % 4].copy(alpha = a))
                    // Recortes de periódico que caen, con sus renglones; uno de cada tres es el ojo del signo.
                    TemaId.ARCANO -> if (p.tono == 0) {
                        drawPath(Path().apply { moveTo(pos.x - s, pos.y); quadraticTo(pos.x, pos.y - s * 0.8f, pos.x + s, pos.y); quadraticTo(pos.x, pos.y + s * 0.8f, pos.x - s, pos.y); close() }, e.onBackground.copy(alpha = a), style = Stroke(s * 0.14f))
                        drawOval(e.tertiary.copy(alpha = a), Offset(pos.x - s * 0.14f, pos.y - s * 0.36f), Size(s * 0.28f, s * 0.72f))
                    } else {
                        drawRect(Color(0xFFE8DDC0).copy(alpha = a), Offset(pos.x - s * 0.8f, pos.y - s * 0.55f), Size(s * 1.6f, s * 1.1f))
                        for (k in 0 until 3) drawLine((if (p.tono == 1) e.onBackground else e.secondary).copy(alpha = a * 0.6f), Offset(pos.x - s * 0.6f, pos.y - s * 0.3f + k * s * 0.3f), Offset(pos.x + s * (0.6f - k * 0.2f), pos.y - s * 0.3f + k * s * 0.3f), s * 0.1f)
                    }
                    TemaId.ESPACIO -> drawPath(Path().apply { moveTo(pos.x, pos.y - s); quadraticTo(pos.x, pos.y, pos.x + s, pos.y); quadraticTo(pos.x, pos.y, pos.x, pos.y + s); quadraticTo(pos.x, pos.y, pos.x - s, pos.y); quadraticTo(pos.x, pos.y, pos.x, pos.y - s); close() }, col)
                    TemaId.ABISMO -> { drawCircle(col, s * 0.8f, pos, style = Stroke(s * 0.14f)); drawCircle(col.copy(alpha = col.alpha * 0.8f), s * 0.22f, pos - Offset(s * 0.3f, s * 0.3f)) }
                    TemaId.ANDALUS -> drawPath(Path().apply { for (i in 0 until 16) { val rr = if (i % 2 == 0) s else s * 0.5f; val q = pos.mas(rr, rad(-90.0 + 22.5 * i)); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }, col)
                    // Plumas de quetzal que caen meciéndose, granos de cacao y cuentas de jade.
                    TemaId.MESO -> when (p.tono) {
                        0 -> plumaQuetzal(pos - Offset(s, 0f), s * 2.4f, p.giro * t * 0.02f + 60f, e.tertiary.copy(alpha = a), e.primary.copy(alpha = a * 0.8f))
                        1 -> granoCacao(pos, s * 0.75f, Color(0xFF6B3A22).copy(alpha = a), Color(0xFFD9B38C).copy(alpha = a * 0.7f))
                        else -> cuentaJade(pos, s * 0.62f, e.tertiary.copy(alpha = a), Color.White.copy(alpha = a))
                    }
                    TemaId.MONGOL -> { drawLine(col, pos - Offset(s, 0f), pos + Offset(s, 0f), s * 0.16f, StrokeCap.Round)
                        drawPath(Path().apply { moveTo(pos.x + s * 1.3f, pos.y); lineTo(pos.x + s * 0.7f, pos.y - s * 0.35f); lineTo(pos.x + s * 0.7f, pos.y + s * 0.35f); close() }, col) }
                    TemaId.RENACIMIENTO -> { drawLine(col, pos - Offset(s, 0f), pos + Offset(s, 0f), s * 0.14f); drawLine(col, pos - Offset(0f, s), pos + Offset(0f, s), s * 0.14f)
                        drawCircle(col, s * 0.55f, pos, style = Stroke(s * 0.1f)) }
                    // Doblones de oro que caen girando (se ven de canto a ratos) y alguna «X» roja.
                    TemaId.PIRATA -> if (p.tono == 2) { drawLine(e.tertiary.copy(alpha = a), pos - Offset(s * 0.6f, s * 0.6f), pos + Offset(s * 0.6f, s * 0.6f), s * 0.22f, StrokeCap.Round)
                        drawLine(e.tertiary.copy(alpha = a), pos + Offset(-s * 0.6f, s * 0.6f), pos + Offset(s * 0.6f, -s * 0.6f), s * 0.22f, StrokeCap.Round) } else {
                        val k = 0.25f + 0.75f * abs(cos(p.giro * t * 0.05f))
                        drawOval(oro.copy(alpha = a), Offset(pos.x - s * 0.75f * k, pos.y - s * 0.75f), Size(s * 1.5f * k, s * 1.5f))
                        drawOval(col, Offset(pos.x - s * 0.52f * k, pos.y - s * 0.52f), Size(s * 1.04f * k, s * 1.04f), style = Stroke(s * 0.1f))
                        drawLine(col, pos - Offset(0f, s * 0.3f), pos + Offset(0f, s * 0.3f), s * 0.1f); drawLine(col, pos - Offset(s * 0.3f * k, 0f), pos + Offset(s * 0.3f * k, 0f), s * 0.1f) }
                    TemaId.URSS -> {
                        val est = Path(); for (i in 0 until 10) { val rr = if (i % 2 == 0) s else s * 0.4f; val q = pos.mas(rr, rad(-90.0 + 36.0 * i)); if (i == 0) est.moveTo(q.x, q.y) else est.lineTo(q.x, q.y) }
                        est.close(); drawPath(est, col)
                    }
                    TemaId.SEDA -> { drawCircle(col, s * 0.7f, pos); drawRect(e.background.copy(alpha = a), Offset(pos.x - s * 0.22f, pos.y - s * 0.22f), Size(s * 0.44f, s * 0.44f)) }
                    // Flores de cerezo de cinco pétalos.
                    TemaId.EDO -> { for (i in 0 until 5) drawCircle(col, s * 0.42f, pos.mas(s * 0.48f, rad(72.0 * i - 90.0)))
                        drawCircle(Color(0xFFF2C94C).copy(alpha = a), s * 0.2f, pos) }
                    TemaId.REVOLUCION -> { drawCircle(e.primary.copy(alpha = a), s * 0.8f, pos); drawCircle(Color.White.copy(alpha = a), s * 0.55f, pos); drawCircle(e.secondary.copy(alpha = a), s * 0.3f, pos) }
                    // Gemas talladas que caen girando.
                    TemaId.ANTIGUAS -> { drawPath(Path().apply { moveTo(pos.x, pos.y - s); lineTo(pos.x + s * 0.7f, pos.y - s * 0.2f); lineTo(pos.x, pos.y + s); lineTo(pos.x - s * 0.7f, pos.y - s * 0.2f); close() },
                        listOf(e.primary, e.tertiary, e.secondary)[p.tono % 3].copy(alpha = a)); drawLine(Color.White.copy(alpha = a * 0.6f), Offset(pos.x - s * 0.7f, pos.y - s * 0.2f), Offset(pos.x + s * 0.7f, pos.y - s * 0.2f), s * 0.1f) }
                    // Manos de ocre en positivo que giran al caer, e hileras de tres puntos.
                    TemaId.CUEVA -> if (p.tono == 0) rotate(p.giro * 40f, pos) { drawPath(manoUnida(pos, s * 0.9f), col) }
                        else for (k in 0 until 3) drawCircle(col, s * 0.26f, pos + Offset((k - 1) * s * 0.7f, (k % 2) * s * 0.3f))
                }
            }
        }
    }
}

/** El engrane de arriba: ajustes y todo lo demás («Más»), con el marco del tema y la palabra en su voz. */
@Composable
fun BotonAjustes(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val sentir = rememberSentir()
    val tema = Estilo.tema
    val forma = remember(tema) { formaBoton(tema) }
    // 48 dp para el dedo y la palabra «Más» debajo, para que no sea solo un ícono (N-21).
    androidx.compose.foundation.layout.Column(
        modifier.size(48.dp).clip(MaterialTheme.shapes.small).clickable { sentir(Sentidos.Evento.TOQUE); onClick() }
            .semantics { contentDescription = "Más: ajustes y todas las pantallas" },
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Box(Modifier.size(30.dp).clip(forma).border(1.dp, MaterialTheme.colorScheme.outline, forma), contentAlignment = Alignment.Center) {
            androidx.compose.runtime.CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.primary) {
                Box(Modifier.size(18.dp), contentAlignment = Alignment.Center) { IconoPestana("ajustes", nombre = "Más") }
            }
        }
        androidx.compose.material3.Text(textoMas(tema), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1, softWrap = false)
    }
}
