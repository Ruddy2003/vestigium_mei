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
    TemaId.CIBER -> 4200
    TemaId.NEON -> 4200
    TemaId.HELENO -> 60000
    TemaId.EGIPTO -> 18000
    TemaId.MEDIEVAL -> 5000
    TemaId.DISTOPIA -> 7000
    TemaId.REFUGIO -> 3200
    TemaId.ORDEN -> 2600
    TemaId.LABORATORIO -> 2400
    TemaId.COLONOS -> 1900
    TemaId.NAPOLEON -> 5200
    TemaId.BOTANICO -> 14000
    TemaId.NAVIDAD -> 24000
    TemaId.SEDA -> 4800
    TemaId.GOTICO -> 7600
    TemaId.ESPACIO -> 10000
    TemaId.ANDALUS -> 36000
    TemaId.MONGOL -> 30000
    TemaId.RENACIMIENTO -> 6600
    TemaId.URSS -> 12000
    TemaId.REVOLUCION -> 9000
    TemaId.ANTIGUAS -> 16000
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
        TemaId.CIBER -> consola(c, r, e.primary, e.secondary, t)
        TemaId.NEON -> consola(c, r, e.primary, e.secondary, t)
        TemaId.HELENO -> solVergina(c, r, if (oscuro) e.primary else e.tertiary, if (oscuro) e.tertiary else e.primary, t)
        TemaId.EGIPTO -> ojoUdyat(c, r, if (oscuro) e.primary else e.tertiary, if (oscuro) e.secondary else e.primary, t)
        TemaId.MEDIEVAL -> roseton(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.DISTOPIA -> ojo(c, r, e.onBackground, e.primary, e.background, t)
        TemaId.REFUGIO -> atomo(c, r, e.primary, e.secondary, t)
        TemaId.ORDEN -> escudo(c, r, e.primary, e.secondary, t)
        TemaId.LABORATORIO -> molecula(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.COLONOS -> farol(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.NAPOLEON -> nImperial(c, r, if (oscuro) e.primaryContainer else e.primary, if (oscuro) e.primary else e.tertiary, e.secondary, t)
        TemaId.BOTANICO -> escarabajoLamina(c, r, e.primary, e.secondary, e.tertiary, e.onBackground, t)
        TemaId.GOTICO -> vitral(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, if (oscuro) e.tertiary else e.primary, e.onBackground, t)
        TemaId.ESPACIO -> planeta(c, r, e.primary, e.secondary, e.tertiary, e.onBackground, t)
        TemaId.ANDALUS -> rotate(t * 360f, c) { estrellaLazo(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, e.primary, e.background) }
        TemaId.MONGOL -> toono(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.RENACIMIENTO -> compas(c, r, e.primary, e.onBackground, e.secondary, t)
        TemaId.NAVIDAD -> copo(c, r, if (oscuro) e.onBackground else e.secondary, e.tertiary, t)
        TemaId.SEDA -> moneda(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, e.background, t)
        TemaId.URSS -> estrellaEngrane(c, r, e.primary, if (oscuro) e.tertiary else e.onBackground, t)
        TemaId.REVOLUCION -> escarapelaEmblema(c, r, e.primary, if (oscuro) Color(0xFFF0EADB) else Color.White, e.secondary, e.onBackground, t)
        TemaId.ANTIGUAS -> gemaRunas(c, r, e.primary, e.secondary, e.tertiary, e.onBackground, t)
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
//  Bandas ornamentales de los temas nuevos (el romano, el helénico y el cyberpunk están en Componentes)
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
            color = esquema.primary.copy(alpha = 0.045f)
            val p = 28.dp.toPx(); var x = 0f; while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x, h); x += p }
            var y = 0f; while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += p }
        }
        TemaId.MEDIEVAL -> {
            color = esquema.onBackground.copy(alpha = if (oscuro) 0.05f else 0.06f); color2 = esquema.primary.copy(alpha = 0.12f)
            var y = 30.dp.toPx(); while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += 30.dp.toPx() }
            listOf(8.dp.toPx(), 11.dp.toPx()).forEach { x -> puntos.addRect(Rect(x, 0f, x + 1.2f, h)) }
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
        TemaId.ANDALUS -> {
            // Lacería: retícula a 0°, 45° y 90° que forma estrellas de ocho puntas, como el alicatado.
            color = (if (oscuro) esquema.primary else esquema.primary).copy(alpha = if (oscuro) 0.05f else 0.045f)
            val p = 34.dp.toPx()
            var x = 0f; while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x, h); x += p }
            var y = 0f; while (y < h) { trazo.moveTo(0f, y); trazo.lineTo(w, y); y += p }
            x = -h; while (x < w) { trazo.moveTo(x, 0f); trazo.lineTo(x + h, h); trazo.moveTo(x, h); trazo.lineTo(x + h, 0f); x += p }
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
    TemaId.CIBER -> RectangleShape
    TemaId.NEON -> RectangleShape
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
    TemaId.DISTOPIA -> poligonoBoton(0.04f to 0.12f, 0.4f to 0.12f, 0.48f to 0.24f, 0.96f to 0.24f, 0.96f to 0.92f, 0.04f to 0.92f)
    TemaId.REFUGIO -> RoundedCornerShape(30)
    TemaId.ORDEN -> GenericShape { s, _ ->
        moveTo(0.1f * s.width, 0.06f * s.height); lineTo(0.9f * s.width, 0.06f * s.height); lineTo(0.9f * s.width, 0.5f * s.height)
        quadraticTo(0.9f * s.width, 0.85f * s.height, 0.5f * s.width, 0.98f * s.height)
        quadraticTo(0.1f * s.width, 0.85f * s.height, 0.1f * s.width, 0.5f * s.height); close()
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
    TemaId.NAVIDAD -> GenericShape { s, _ ->
        // Bola con su capuchón: el círculo (centro 0,57; radio 0,43) sale del capuchón a −76,55° y vuelve a −103,45°.
        moveTo(0.4f * s.width, 0.02f * s.height); lineTo(0.6f * s.width, 0.02f * s.height); lineTo(0.6f * s.width, 0.152f * s.height)
        arcTo(Rect(0.07f * s.width, 0.14f * s.height, 0.93f * s.width, 1f * s.height), -76.55f, 333.1f, false); close()
    }
    TemaId.SEDA -> CutCornerShape(30)
    TemaId.GOTICO -> GenericShape { s, _ ->
        moveTo(0.14f * s.width, s.height); lineTo(0.14f * s.width, 0.45f * s.height)
        quadraticTo(0.14f * s.width, 0.1f * s.height, 0.5f * s.width, 0f)
        quadraticTo(0.86f * s.width, 0.1f * s.height, 0.86f * s.width, 0.45f * s.height); lineTo(0.86f * s.width, s.height); close()
    }
    TemaId.ESPACIO -> CircleShape
    TemaId.ANDALUS -> estrellaBoton(16, 0.5f, 0.383f, -90.0)
    TemaId.MONGOL -> poligonoBoton(0.04f to 0.96f, 0.04f to 0.46f, 0.36f to 0.06f, 0.64f to 0.06f, 0.96f to 0.46f, 0.96f to 0.96f)
    TemaId.RENACIMIENTO -> poligonoBoton(0.5f to 0f, 1f to 0.5f, 0.5f to 1f, 0f to 0.5f)
    TemaId.URSS -> poligonoBoton(0.22f to 0.06f, 1f to 0.06f, 0.78f to 0.94f, 0f to 0.94f)
    TemaId.REVOLUCION -> estrellaBoton(48, 0.5f, 0.45f, -90.0)
    TemaId.ANTIGUAS -> poligonoBoton(0.12f to 0.1f, 0.55f to 0.02f, 0.9f to 0.14f, 0.98f to 0.55f, 0.84f to 0.94f, 0.4f to 0.98f, 0.06f to 0.8f, 0.02f to 0.4f)
}

/** La palabra bajo el engrane de «Más», en la voz de cada tema (la descripción para lectores de pantalla sigue en español). */
private fun textoMas(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "Cetera"
    TemaId.CIBER -> "más_"
    TemaId.NEON -> "más_"
    TemaId.HELENO -> "Πλέον"
    TemaId.EGIPTO -> "Más rollos"
    TemaId.MEDIEVAL -> "Ítem"
    TemaId.DISTOPIA -> "Anexo"
    TemaId.REFUGIO -> "Menú"
    TemaId.ORDEN -> "Taller"
    TemaId.LABORATORIO -> "Índice"
    TemaId.COLONOS -> "Tablón"
    TemaId.NAPOLEON -> "Plus"
    TemaId.BOTANICO -> "Notas"
    TemaId.NAVIDAD -> "Baúl"
    TemaId.SEDA -> "更多"
    TemaId.GOTICO -> "Atrio"
    TemaId.ESPACIO -> "Panel"
    TemaId.ANDALUS -> "مزيد"
    TemaId.MONGOL -> "Илүү"
    TemaId.RENACIMIENTO -> "Altro"
    TemaId.URSS -> "Ещё"
    TemaId.REVOLUCION -> "Club"
    TemaId.ANTIGUAS -> "Morral"
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
            // Sol de píxeles.
            TemaId.CIBER -> { val p = r * 0.22f; drawRect(color, Offset(c.x - p * 1.5f, c.y - p * 1.5f), Size(p * 3, p * 3))
                for (i in 0 until 8) { val q = c.mas(r * 0.82f, rad(45.0 * i)); drawRect(color, Offset(q.x - p / 2, q.y - p / 2), Size(p, p)) } }
            TemaId.NEON -> { val p = r * 0.22f; drawRect(color, Offset(c.x - p * 1.5f, c.y - p * 1.5f), Size(p * 3, p * 3))
                for (i in 0 until 8) { val q = c.mas(r * 0.82f, rad(45.0 * i)); drawRect(color, Offset(q.x - p / 2, q.y - p / 2), Size(p, p)) } }
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
            // Sol tachado por el censor.
            TemaId.DISTOPIA -> { drawCircle(color, r * 0.62f, c); listOf(-0.3f, 0f, 0.3f).forEach { dy -> drawRect(fondo, Offset(c.x - r, c.y + r * dy - r * 0.05f), Size(r * 2, r * 0.1f)) }
                drawCircle(color, r * 0.9f, c, style = Stroke(r * 0.08f)) }
            // Destello atómico de ocho puntas.
            TemaId.REFUGIO -> { for (i in 0 until 8) espina(rad(45.0 * i + 22.5), 0.2f, if (i % 2 == 0) 0.98f else 0.66f, 0.4f); drawCircle(color, r * 0.26f, c) }
            // Engrane.
            TemaId.ORDEN -> { drawCircle(color, r * 0.62f, c); for (i in 0 until 10) { val a = rad(36.0 * i); drawLine(color, c.mas(r * 0.55f, a), c.mas(r * 0.92f, a), r * 0.22f) }
                drawCircle(fondo, r * 0.24f, c) }
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
            // Estrella de Belén: la punta de abajo más larga.
            TemaId.NAVIDAD -> { listOf(-90.0 to 0.8f, 0.0 to 0.8f, 180.0 to 0.8f, 90.0 to 1f).forEach { (g, l) -> espina(rad(g), 0.16f, l, 0.6f) }; rayos(4, 0.2f, 0.5f, 0.08f, 45.0) }
            // Moneda solar con su agujero cuadrado.
            TemaId.SEDA -> { drawCircle(color, r * 0.48f, c); val h = r * 0.15f; drawRect(fondo, Offset(c.x - h, c.y - h), Size(h * 2, h * 2)); rayos(8, 0.66f, 0.94f, 0.12f, 22.5) }
            // Rosetón de lancetas.
            TemaId.GOTICO -> { for (i in 0 until 8) espina(rad(45.0 * i - 90), 0.5f, 0.98f, 0.22f)
                drawCircle(color, r * 0.44f, c, style = Stroke(r * 0.1f)); rayos(8, 0.1f, 0.4f, 0.06f); drawCircle(color, r * 0.12f, c) }
            // Estrella con un planeta en su órbita.
            TemaId.ESPACIO -> { drawCircle(color, r * 0.34f, c)
                rotate(-25f, c) { drawOval(color, Offset(c.x - r * 0.95f, c.y - r * 0.32f), Size(r * 1.9f, r * 0.64f), style = Stroke(r * 0.08f)) }
                drawCircle(color, r * 0.13f, c.mas(r * 0.95f, rad(-25.0))) }
            // Estrella de ocho puntas de lazo.
            TemaId.ANDALUS -> { estrella(c, r * 0.95f, 8, 0.77f); drawCircle(fondo, r * 0.2f, c) }
            // Sol con tres lenguas de fuego encima, de la estepa.
            TemaId.MONGOL -> { drawCircle(color, r * 0.4f, c + Offset(0f, r * 0.3f))
                for (k in -1..1) { val b = Offset(c.x + k * r * 0.26f, c.y - r * 0.2f); val alto = if (k == 0) 0.75f else 0.5f
                    drawPath(Path().apply { moveTo(b.x - r * 0.12f, b.y); quadraticTo(b.x - r * 0.1f, b.y - r * alto * 0.6f, b.x, b.y - r * alto); quadraticTo(b.x + r * 0.1f, b.y - r * alto * 0.6f, b.x + r * 0.12f, b.y); close() }, color) } }
            // Sol de cuaderno: círculo y cuadrado de proporciones.
            TemaId.RENACIMIENTO -> { drawCircle(color, r * 0.8f, c, style = Stroke(r * 0.08f)); val q = r * 0.56f
                drawRect(color, Offset(c.x - q, c.y - q), Size(q * 2, q * 2), style = Stroke(r * 0.06f)); drawCircle(color, r * 0.28f, c); rayos(8, 0.84f, 0.98f, 0.08f) }
            // Sol que sale de una esquina, con rayos en abanico, como en los carteles.
            TemaId.URSS -> { val o = Offset(c.x - r * 0.6f, c.y + r * 0.6f); drawArc(color, -90f, 90f, true, Offset(o.x - r * 0.6f, o.y - r * 0.6f), Size(r * 1.2f, r * 1.2f))
                for (i in 0 until 5) { val a = rad(-90.0 + 22.5 * i); drawLine(color, o.mas(r * 0.78f, a), o.mas(r * 1.5f, a), r * 0.12f) } }
            // Sol escarapela: tres anillos y doce rayos.
            TemaId.REVOLUCION -> { drawCircle(color, r * 0.56f, c, style = Stroke(r * 0.12f)); drawCircle(color, r * 0.3f, c, style = Stroke(r * 0.1f)); drawCircle(color, r * 0.1f, c); rayos(12, 0.72f, 0.96f, 0.09f) }
            // Sol de runas: seis rayos que se abren en horquilla.
            TemaId.ANTIGUAS -> { drawCircle(color, r * 0.34f, c)
                for (i in 0 until 6) { val a = rad(60.0 * i - 90); val m = c.mas(r * 0.7f, a)
                    drawLine(color, c.mas(r * 0.46f, a), m, r * 0.11f); drawLine(color, m, m.mas(r * 0.26f, a - 0.6f), r * 0.09f); drawLine(color, m, m.mas(r * 0.26f, a + 0.6f), r * 0.09f) } }
        }
    } else {
        when (tema) {
            // Lúnula: la luna con los cuernos arriba y su argolla, como el amuleto.
            TemaId.ROMANO -> { luna(0.72f, 0f, -0.36f, c + Offset(0f, r * 0.12f)); drawCircle(color, r * 0.14f, Offset(c.x, c.y - r * 0.72f), style = Stroke(r * 0.07f)) }
            // Luna de píxeles.
            TemaId.CIBER -> { val p = r * 0.34f
                listOf(-1 to -2, 0 to -2, 1 to -2, -2 to -1, -1 to -1, -2 to 0, -1 to 0, -2 to 1, -1 to 1, -1 to 2, 0 to 2, 1 to 2).forEach { (i, j) ->
                    drawRect(color, Offset(c.x + (i - 0.5f) * p, c.y + (j - 0.5f) * p), Size(p * 0.9f, p * 0.9f)) } }
            TemaId.NEON -> { val p = r * 0.34f
                listOf(-1 to -2, 0 to -2, 1 to -2, -2 to -1, -1 to -1, -2 to 0, -1 to 0, -2 to 1, -1 to 1, -1 to 2, 0 to 2, 1 to 2).forEach { (i, j) ->
                    drawRect(color, Offset(c.x + (i - 0.5f) * p, c.y + (j - 0.5f) * p), Size(p * 0.9f, p * 0.9f)) } }
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
            // Luna tras los barrotes.
            TemaId.DISTOPIA -> { luna(); listOf(-0.45f, 0f, 0.45f).forEach { dx -> drawLine(color, Offset(c.x + r * dx, c.y - r * 0.95f), Offset(c.x + r * dx, c.y + r * 0.95f), r * 0.08f) } }
            // Luna con una órbita de átomo.
            TemaId.REFUGIO -> { luna(0.62f, 0.3f, -0.24f); rotate(-30f, c) { drawOval(color, Offset(c.x - r * 0.95f, c.y - r * 0.32f), Size(r * 1.9f, r * 0.64f), style = Stroke(r * 0.06f)) } }
            // Luna remachada.
            TemaId.ORDEN -> { luna(); listOf(-40.0, 0.0, 40.0).forEach { d -> drawCircle(fondo, r * 0.08f, c.mas(r * 0.6f, rad(180.0 + d))) } }
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
            // Luna con copos.
            TemaId.NAVIDAD -> { luna()
                listOf(Offset(0.6f, -0.55f), Offset(0.62f, 0.45f)).forEach { o -> val q = c + o * r
                    for (k in 0 until 3) { val a = rad(60.0 * k); drawLine(color, q.mas(r * 0.17f, a), q.mas(-r * 0.17f, a), r * 0.06f) } } }
            // Luna con un farolillo colgado.
            TemaId.SEDA -> { luna(0.7f, 0.34f, -0.26f, c + Offset(-r * 0.15f, 0f))
                drawLine(color, Offset(c.x + r * 0.62f, c.y - r), Offset(c.x + r * 0.62f, c.y - r * 0.2f), r * 0.05f)
                drawRoundRect(color, Offset(c.x + r * 0.44f, c.y - r * 0.2f), Size(r * 0.36f, r * 0.46f), CornerRadius(r * 0.12f))
                drawLine(color, Offset(c.x + r * 0.62f, c.y + r * 0.26f), Offset(c.x + r * 0.62f, c.y + r * 0.45f), r * 0.05f) }
            // Luna con dos destellos de bóveda.
            TemaId.GOTICO -> { luna(); destello(c + Offset(r * 0.62f, -r * 0.5f), r * 0.26f); destello(c + Offset(r * 0.7f, r * 0.4f), r * 0.18f) }
            // Luna con cráteres.
            TemaId.ESPACIO -> { drawCircle(color, r * 0.78f, c); drawCircle(fondo, r * 0.66f, c + Offset(r * 0.4f, -r * 0.28f))
                listOf(Offset(-0.45f, 0.1f) to 0.12f, Offset(-0.2f, 0.5f) to 0.09f, Offset(-0.5f, -0.3f) to 0.07f).forEach { (o, k) -> drawCircle(fondo.copy(alpha = 0.55f), r * k, c + o * r) } }
            // Hilal fino con una estrella de ocho puntas.
            TemaId.ANDALUS -> { luna(0.76f, 0.26f, -0.2f); estrella(c + Offset(r * 0.4f, -r * 0.3f), r * 0.2f, 8, 0.5f) }
            // Luna sobre una yurta.
            TemaId.MONGOL -> { luna(0.62f, 0.3f, -0.24f, c + Offset(r * 0.2f, -r * 0.25f))
                drawArc(color, 180f, 180f, true, Offset(c.x - r * 0.9f, c.y + r * 0.3f), Size(r * 0.9f, r * 0.6f))
                drawRect(color, Offset(c.x - r * 0.9f, c.y + r * 0.6f), Size(r * 0.9f, r * 0.3f)) }
            // Luna con su círculo de construcción y una diagonal a pluma.
            TemaId.RENACIMIENTO -> { luna(); drawCircle(color, r * 0.78f, c, style = Stroke(r * 0.04f)); drawLine(color, Offset(c.x - r, c.y + r), Offset(c.x + r, c.y - r), r * 0.04f) }
            // Luna con la estrella roja.
            TemaId.URSS -> { luna(); estrella(c + Offset(r * 0.5f, -r * 0.4f), r * 0.28f, 5, 0.4f) }
            // Luna con una escarapela pequeña.
            TemaId.REVOLUCION -> { luna(); drawCircle(color, r * 0.2f, c + Offset(r * 0.58f, r * 0.42f), style = Stroke(r * 0.07f)); drawCircle(color, r * 0.07f, c + Offset(r * 0.58f, r * 0.42f)) }
            // Luna con una gema.
            TemaId.ANTIGUAS -> { luna(); val q = c + Offset(r * 0.6f, -r * 0.45f); val k = r * 0.24f
                drawPath(Path().apply { moveTo(q.x, q.y - k); lineTo(q.x + k * 0.7f, q.y); lineTo(q.x, q.y + k); lineTo(q.x - k * 0.7f, q.y); close() }, color) }
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
        // Cristal cuadrado con un renglón dentro.
        TemaId.CIBER -> { drawRect(color, Offset(c.x - lr, c.y - lr), Size(lr * 2, lr * 2), style = Stroke(g)); mango(0.92f, 0.12f, 0.72f)
            drawLine(color, Offset(c.x - lr * 0.45f, c.y + lr * 0.3f), Offset(c.x + lr * 0.45f, c.y + lr * 0.3f), r * 0.08f) }
        TemaId.NEON -> { drawRect(color, Offset(c.x - lr, c.y - lr), Size(lr * 2, lr * 2), style = Stroke(g)); mango(0.92f, 0.12f, 0.72f)
            drawLine(color, Offset(c.x - lr * 0.45f, c.y + lr * 0.3f), Offset(c.x + lr * 0.45f, c.y + lr * 0.3f), r * 0.08f) }
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
        // Cruz de mira en el cristal.
        TemaId.DISTOPIA -> { lente(); mango()
            drawLine(color, Offset(c.x - lr * 0.6f, c.y), Offset(c.x + lr * 0.6f, c.y), r * 0.05f); drawLine(color, Offset(c.x, c.y - lr * 0.6f), Offset(c.x, c.y + lr * 0.6f), r * 0.05f) }
        // Mango con dos aros y un brillo en el cristal.
        TemaId.REFUGIO -> { lente(); mango(); listOf(0.74f, 0.83f).forEach { k -> val p = Offset(r * k, r * k); drawLine(color, p + Offset(-r * 0.08f, r * 0.08f), p + Offset(r * 0.08f, -r * 0.08f), r * 0.05f) }
            drawCircle(color, r * 0.05f, c + Offset(-lr * 0.35f, -lr * 0.35f)) }
        // Cristal hexagonal, como una tuerca, y mango grueso.
        TemaId.ORDEN -> { drawPath(poli(6, lr * 1.08f, 0.0), color, style = Stroke(g)); mango(0.92f, 0.18f, 0.7f) }
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
        // Un copo dentro del cristal.
        TemaId.NAVIDAD -> { lente(); mango(); for (k in 0 until 3) { val a = rad(60.0 * k + 90.0); drawLine(color, c.mas(lr * 0.5f, a), c.mas(-lr * 0.5f, a), r * 0.04f) } }
        // Cristal octogonal y borla.
        TemaId.SEDA -> { drawPath(poli(8, lr * 1.04f, 22.5), color, style = Stroke(g)); mango(0.86f)
            drawLine(color, Offset(r * 0.86f, r * 0.86f), Offset(r * 0.8f, r * 0.99f), r * 0.05f); drawLine(color, Offset(r * 0.86f, r * 0.86f), Offset(r * 0.99f, r * 0.8f), r * 0.05f) }
        // Cristal en arco apuntado.
        TemaId.GOTICO -> { drawPath(Path().apply {
                moveTo(c.x - lr, c.y + lr); lineTo(c.x - lr, c.y); quadraticTo(c.x - lr, c.y - lr * 0.8f, c.x, c.y - lr * 1.2f)
                quadraticTo(c.x + lr, c.y - lr * 0.8f, c.x + lr, c.y); lineTo(c.x + lr, c.y + lr); close() }, color, style = Stroke(g * 0.85f))
            mango(0.92f, 0.14f, 0.72f) }
        // Ocular de telescopio: un anillo dentro de otro.
        TemaId.ESPACIO -> { lente(); drawCircle(color, lr * 0.45f, c, style = Stroke(r * 0.04f)); mango() }
        // Estrella de lazo dentro del cristal.
        TemaId.ANDALUS -> { lente(); mango()
            val est = Path(); for (i in 0 until 16) { val rr = if (i % 2 == 0) lr * 0.5f else lr * 0.25f; val q = c.mas(rr, rad(-90.0 + 22.5 * i)); if (i == 0) est.moveTo(q.x, q.y) else est.lineTo(q.x, q.y) }
            est.close(); drawPath(est, color) }
        // Mango con cola de caballo.
        TemaId.MONGOL -> { lente(); mango(0.82f); listOf(Offset(0.86f, 0.99f), Offset(0.95f, 0.95f), Offset(0.99f, 0.86f)).forEach { drawLine(color, Offset(r * 0.82f, r * 0.82f), it * r, r * 0.045f) } }
        // Línea de construcción que atraviesa el cristal.
        TemaId.RENACIMIENTO -> { lente(); mango(); drawLine(color, Offset(r * 0.02f, r * 0.02f), Offset(c.x + lr * 0.3f, c.y + lr * 0.3f), r * 0.035f)
            drawLine(color, Offset(c.x - lr * 0.25f, c.y + lr * 0.25f), Offset(c.x + lr * 0.25f, c.y - lr * 0.25f), r * 0.035f) }
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
    TemaId.CIBER -> false
    TemaId.NEON -> false
    TemaId.HELENO -> false
    TemaId.EGIPTO -> false
    TemaId.MEDIEVAL -> false
    TemaId.DISTOPIA -> false
    TemaId.REFUGIO -> false
    TemaId.ORDEN -> false
    TemaId.LABORATORIO -> true
    TemaId.COLONOS -> true
    TemaId.NAPOLEON -> false
    TemaId.BOTANICO -> false
    TemaId.NAVIDAD -> false
    TemaId.SEDA -> false
    TemaId.GOTICO -> false
    TemaId.ESPACIO -> true
    TemaId.ANDALUS -> false
    TemaId.MONGOL -> false
    TemaId.RENACIMIENTO -> false
    TemaId.URSS -> false
    TemaId.REVOLUCION -> false
    TemaId.ANTIGUAS -> false
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
                    TemaId.NEON -> {
                        val fosforo = e.primary.copy(alpha = a)
                        if (p.tono == 0) {
                            drawOval(fosforo, Offset(pos.x - s * 0.35f, pos.y - s * 0.6f), Size(s * 0.7f, s * 1.2f), style = Stroke(s * 0.2f))
                        } else {
                            drawLine(fosforo, pos - Offset(0f, s * 0.6f), pos + Offset(0f, s * 0.6f), s * 0.22f)
                            drawLine(fosforo, pos - Offset(0f, s * 0.6f), pos - Offset(s * 0.3f, s * 0.35f), s * 0.2f)
                        }
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
                    // Tiras de expediente con sus renglones.
                    TemaId.DISTOPIA -> { drawRect(col, Offset(pos.x - s * 0.7f, pos.y - s * 0.45f), Size(s * 1.4f, s * 0.9f))
                        drawLine(e.background.copy(alpha = a * 0.8f), Offset(pos.x - s * 0.5f, pos.y - s * 0.15f), Offset(pos.x + s * 0.5f, pos.y - s * 0.15f), s * 0.1f)
                        drawLine(e.background.copy(alpha = a * 0.8f), Offset(pos.x - s * 0.5f, pos.y + s * 0.15f), Offset(pos.x + s * 0.2f, pos.y + s * 0.15f), s * 0.1f) }
                    // Galones de la Orden.
                    TemaId.ORDEN -> drawPath(Path().apply { moveTo(pos.x - s, pos.y - s * 0.3f); lineTo(pos.x, pos.y + s * 0.4f); lineTo(pos.x + s, pos.y - s * 0.3f) },
                        col, style = Stroke(s * 0.3f, join = StrokeJoin.Miter))
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
                    TemaId.GOTICO -> drawPath(Path().apply { moveTo(pos.x, pos.y - s); lineTo(pos.x + s * 0.7f, pos.y + s * 0.6f); lineTo(pos.x - s * 0.5f, pos.y + s * 0.8f); close() },
                        listOf(e.secondary, e.primary, Color(0xFF2E8B57), Color(0xFFE2B34A))[p.tono % 4].copy(alpha = a))
                    TemaId.ESPACIO -> drawPath(Path().apply { moveTo(pos.x, pos.y - s); quadraticTo(pos.x, pos.y, pos.x + s, pos.y); quadraticTo(pos.x, pos.y, pos.x, pos.y + s); quadraticTo(pos.x, pos.y, pos.x - s, pos.y); quadraticTo(pos.x, pos.y, pos.x, pos.y - s); close() }, col)
                    TemaId.ANDALUS -> drawPath(Path().apply { for (i in 0 until 16) { val rr = if (i % 2 == 0) s else s * 0.5f; val q = pos.mas(rr, rad(-90.0 + 22.5 * i)); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }, col)
                    TemaId.MONGOL -> { drawLine(col, pos - Offset(s, 0f), pos + Offset(s, 0f), s * 0.16f, StrokeCap.Round)
                        drawPath(Path().apply { moveTo(pos.x + s * 1.3f, pos.y); lineTo(pos.x + s * 0.7f, pos.y - s * 0.35f); lineTo(pos.x + s * 0.7f, pos.y + s * 0.35f); close() }, col) }
                    TemaId.RENACIMIENTO -> { drawLine(col, pos - Offset(s, 0f), pos + Offset(s, 0f), s * 0.14f); drawLine(col, pos - Offset(0f, s), pos + Offset(0f, s), s * 0.14f)
                        drawCircle(col, s * 0.55f, pos, style = Stroke(s * 0.1f)) }
                    TemaId.URSS -> {
                        val est = Path(); for (i in 0 until 10) { val rr = if (i % 2 == 0) s else s * 0.4f; val q = pos.mas(rr, rad(-90.0 + 36.0 * i)); if (i == 0) est.moveTo(q.x, q.y) else est.lineTo(q.x, q.y) }
                        est.close(); drawPath(est, col)
                    }
                    TemaId.SEDA -> { drawCircle(col, s * 0.7f, pos); drawRect(e.background.copy(alpha = a), Offset(pos.x - s * 0.22f, pos.y - s * 0.22f), Size(s * 0.44f, s * 0.44f)) }
                    TemaId.REVOLUCION -> { drawCircle(e.primary.copy(alpha = a), s * 0.8f, pos); drawCircle(Color.White.copy(alpha = a), s * 0.55f, pos); drawCircle(e.secondary.copy(alpha = a), s * 0.3f, pos) }
                    // Gemas talladas que caen girando.
                    TemaId.ANTIGUAS -> { drawPath(Path().apply { moveTo(pos.x, pos.y - s); lineTo(pos.x + s * 0.7f, pos.y - s * 0.2f); lineTo(pos.x, pos.y + s); lineTo(pos.x - s * 0.7f, pos.y - s * 0.2f); close() },
                        listOf(e.primary, e.tertiary, e.secondary)[p.tono % 3].copy(alpha = a)); drawLine(Color.White.copy(alpha = a * 0.6f), Offset(pos.x - s * 0.7f, pos.y - s * 0.2f), Offset(pos.x + s * 0.7f, pos.y - s * 0.2f), s * 0.1f) }
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
