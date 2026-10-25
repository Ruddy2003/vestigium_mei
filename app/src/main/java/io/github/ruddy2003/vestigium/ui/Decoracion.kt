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
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
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

/** Cuánto tarda en repetirse la animación de cada emblema, en ms. */
private fun periodo(t: TemaId) = when (t) {
    TemaId.ROMANO -> 6000; TemaId.CIBER -> 1400; TemaId.HELENO -> 60000; TemaId.MEDIEVAL -> 5000; TemaId.DISTOPIA -> 7000
    TemaId.REFUGIO -> 3200; TemaId.ORDEN -> 2600; TemaId.LABORATORIO -> 2400; TemaId.COLONOS -> 1900
    TemaId.NAPOLEON -> 5200; TemaId.BOTANICO -> 14000; TemaId.NAVIDAD -> 24000; TemaId.SEDA -> 4800; TemaId.URSS -> 12000
    TemaId.GOTICO -> 7000; TemaId.ESPACIO -> 9000; TemaId.ANDALUS -> 36000; TemaId.MONGOL -> 30000; TemaId.RENACIMIENTO -> 6000
    TemaId.REVOLUCION -> 9000
}

/**
 * El emblema de cada tema, dibujado (no son imágenes): corona de laurel, chip de terminal, sol de Vergina,
 * rosetón, ojo que todo lo ve, átomo, escudo con circuito, molécula y farol. Se mueven si las animaciones están activas.
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
        TemaId.CIBER -> chip(c, r, e.primary, e.secondary, t)
        TemaId.HELENO -> rotate(t * 360f, c) { sol(c, r, if (oscuro) e.primary else e.tertiary) }
        TemaId.MEDIEVAL -> roseton(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.DISTOPIA -> ojo(c, r, e.onBackground, e.primary, e.background, t)
        TemaId.REFUGIO -> atomo(c, r, e.primary, e.secondary, t)
        TemaId.ORDEN -> escudo(c, r, e.primary, e.secondary, t)
        TemaId.LABORATORIO -> molecula(c, r, e.primary, e.secondary, t)
        TemaId.COLONOS -> farol(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.NAPOLEON -> { laurel(c, r, if (oscuro) e.primary else e.tertiary, t); letraN(c, r * 0.5f, if (oscuro) e.onBackground else e.primary, t) }
        TemaId.BOTANICO -> escarabajoLamina(c, r, e.primary, e.secondary, e.tertiary, e.onBackground, t)
        TemaId.GOTICO -> vitral(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, if (oscuro) e.tertiary else e.primary, e.onBackground, t)
        TemaId.ESPACIO -> planeta(c, r, e.primary, e.secondary, e.tertiary, e.onBackground, t)
        TemaId.ANDALUS -> rotate(t * 360f, c) { estrellaLazo(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, e.primary, e.background) }
        TemaId.MONGOL -> toono(c, r, e.primary, e.secondary, e.tertiary, t)
        TemaId.RENACIMIENTO -> compas(c, r, e.primary, e.onBackground, e.secondary, t)
        TemaId.NAVIDAD -> rotate(t * 360f, c) { copo(c, r, if (oscuro) e.onBackground else e.secondary, e.tertiary) }
        TemaId.SEDA -> moneda(c, r, if (oscuro) e.primary else e.tertiary, e.secondary, e.background, t)
        TemaId.URSS -> estrellaEngrane(c, r, e.primary, if (oscuro) e.tertiary else e.onBackground, t)
        TemaId.REVOLUCION -> escarapelaEmblema(c, r, e.primary, if (oscuro) Color(0xFFF0EADB) else Color.White, e.secondary, e.onBackground, t)
    }
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

/** Estrella de ocho puntas de la lacería andalusí: dos cuadrados entrelazados con un rosetón dentro. */
private fun DrawScope.estrellaLazo(c: Offset, r: Float, oro: Color, verde: Color, azul: Color, fondo: Color) {
    fun cuadrado(giro: Double, rr: Float) = Path().apply {
        for (i in 0..3) { val p = c.mas(rr, rad(giro + 90.0 * i)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close()
    }
    val a = cuadrado(-45.0, r * 0.92f); val b = cuadrado(0.0, r * 0.92f)
    drawPath(a, verde.copy(alpha = 0.85f)); drawPath(b, verde.copy(alpha = 0.85f))
    drawPath(a, oro, style = Stroke(r * 0.07f)); drawPath(b, oro, style = Stroke(r * 0.07f))
    // Octógono interior y estrella pequeña.
    val oct = Path().apply { for (i in 0..7) { val p = c.mas(r * 0.5f, rad(22.5 + 45.0 * i)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
    drawPath(oct, fondo); drawPath(oct, oro, style = Stroke(r * 0.05f))
    val est = Path().apply { for (i in 0 until 16) { val rr = if (i % 2 == 0) r * 0.42f else r * 0.2f; val p = c.mas(rr, rad(-90.0 + 22.5 * i)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
    drawPath(est, azul)
    drawCircle(oro, r * 0.1f, c)
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

/** Copo de nieve de seis brazos con ramas, que gira muy despacio; destella en las puntas. */
private fun DrawScope.copo(c: Offset, r: Float, color: Color, oro: Color) {
    for (i in 0 until 6) {
        val a = rad(60.0 * i - 90)
        val punta = c.mas(r * 0.9f, a)
        drawLine(color, c, punta, r * 0.075f, StrokeCap.Round)
        listOf(0.38f to 0.26f, 0.62f to 0.2f).forEach { (d, l) ->
            val base = c.mas(r * 0.9f * d, a)
            drawLine(color, base, base.mas(r * l, a + rad(45.0)), r * 0.06f, StrokeCap.Round)
            drawLine(color, base, base.mas(r * l, a - rad(45.0)), r * 0.06f, StrokeCap.Round)
        }
        drawCircle(oro, r * 0.07f, punta)
    }
    val hex = Path().apply { for (i in 0..5) { val p = c.mas(r * 0.2f, rad(60.0 * i - 60)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
    drawPath(hex, oro)
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

/** Chip hexagonal con un indicador de terminal que parpadea y una falla de color de vez en cuando. */
private fun DrawScope.chip(c: Offset, r: Float, cian: Color, magenta: Color, t: Float) {
    fun hex(cc: Offset) = Path().apply {
        for (i in 0..5) { val p = cc.mas(r * 0.9f, rad(60.0 * i - 90)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
        close()
    }
    val falla = t in 0.86f..0.9f
    if (falla) drawPath(hex(c + Offset(r * 0.08f, 0f)), magenta.copy(alpha = 0.8f), style = Stroke(r * 0.07f))
    drawPath(hex(c), cian, style = Stroke(r * 0.09f))
    val s = r * 0.32f
    val p0 = c + Offset(-s * 1.1f, -s * 0.8f)
    drawLine(cian, p0, p0 + Offset(s * 0.8f, s * 0.8f), r * 0.1f, StrokeCap.Square)
    drawLine(cian, p0 + Offset(s * 0.8f, s * 0.8f), p0 + Offset(0f, s * 1.6f), r * 0.1f, StrokeCap.Square)
    val encendido = (t * 2).toInt() % 2 == 0
    drawRect(magenta.copy(alpha = if (encendido) 1f else 0.15f), c + Offset(s * 0.1f, s * 0.55f), Size(s * 1.1f, r * 0.12f))
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

/** Rosetón gótico: ocho lóbulos de lapislázuli, rayos de oro y una cuadrifolia al centro. La luz respira. */
private fun DrawScope.roseton(c: Offset, r: Float, rojo: Color, azul: Color, oro: Color, t: Float) {
    val luz = 0.55f + 0.45f * (0.5f + 0.5f * sin(2 * PI * t).toFloat())
    for (i in 0 until 8) {
        val p = c.mas(r * 0.56f, rad(45.0 * i - 90))
        drawCircle(azul.copy(alpha = 0.35f * luz + 0.15f), r * 0.28f, p)
        drawCircle(oro, r * 0.28f, p, style = Stroke(r * 0.045f))
    }
    for (i in 0 until 8) drawLine(oro, c.mas(r * 0.3f, rad(45.0 * i - 67.5)), c.mas(r * 0.93f, rad(45.0 * i - 67.5)), r * 0.035f)
    drawCircle(rojo.copy(alpha = 0.55f * luz + 0.25f), r * 0.3f, c)
    drawCircle(oro, r * 0.3f, c, style = Stroke(r * 0.05f))
    for (i in 0 until 4) drawCircle(oro, r * 0.09f, c.mas(r * 0.1f, rad(90.0 * i)))
    drawCircle(rojo, r * 0.93f, c, style = Stroke(r * 0.08f))
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

/** Átomo de tres órbitas con sus electrones girando. */
private fun DrawScope.atomo(c: Offset, r: Float, orbita: Color, electron: Color, t: Float) {
    val a = r * 0.92f; val b = r * 0.33f
    for (k in 0 until 3) {
        val giro = 60f * k
        rotate(giro, c) { drawOval(orbita, Offset(c.x - a, c.y - b), Size(a * 2, b * 2), style = Stroke(r * 0.07f)) }
        val th = 2 * PI * (t + k / 3.0)
        val px = a * cos(th).toFloat(); val py = b * sin(th).toFloat()
        val g = rad(giro.toDouble())
        drawCircle(electron, r * 0.11f, Offset(c.x + px * cos(g) - py * sin(g), c.y + px * sin(g) + py * cos(g)))
    }
    drawCircle(electron, r * 0.17f, c)
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

/** Molécula: anillo hexagonal con dos enlaces; un átomo se enciende por turno. */
private fun DrawScope.molecula(c: Offset, r: Float, cian: Color, violeta: Color, t: Float) {
    val rr = r * 0.5f
    val vertices = (0..5).map { c.mas(rr, rad(60.0 * it - 90)) }
    val hex = Path().apply { vertices.forEachIndexed { i, v -> if (i == 0) moveTo(v.x, v.y) else lineTo(v.x, v.y) }; close() }
    drawPath(hex, cian, style = Stroke(r * 0.07f))
    drawCircle(cian.copy(alpha = 0.55f), rr * 0.55f, c, style = Stroke(r * 0.045f))
    val ext1 = c.mas(r * 0.94f, rad(-90.0)); val ext2 = c.mas(r * 0.94f, rad(30.0))
    drawLine(cian, vertices[0], ext1, r * 0.06f); drawLine(cian, vertices[2], ext2, r * 0.06f)
    drawCircle(violeta, r * 0.11f, ext1); drawCircle(violeta, r * 0.11f, ext2)
    val activo = (t * 6).toInt() % 6
    vertices.forEachIndexed { i, v -> drawCircle(if (i == activo) violeta else cian, if (i == activo) r * 0.13f else r * 0.075f, v) }
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

/** Filete de periódico oficial: barra gruesa con un tramo rojo y una línea fina debajo. */
@Composable
internal fun Filete(modifier: Modifier) {
    val tinta = MaterialTheme.colorScheme.onBackground; val rojo = MaterialTheme.colorScheme.primary
    Canvas(modifier.fillMaxWidth().height(10.dp)) {
        val g = 4.5.dp.toPx()
        drawRect(tinta, Offset.Zero, Size(size.width, g))
        drawRect(rojo, Offset.Zero, Size(size.width * 0.22f, g))
        drawRect(tinta, Offset(0f, g + 2.5.dp.toPx()), Size(size.width, 1.dp.toPx()))
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

/** Regla graduada de laboratorio con una marca de lectura. */
@Composable
internal fun Regla(modifier: Modifier) {
    val tinta = MaterialTheme.colorScheme.onSurfaceVariant; val cian = MaterialTheme.colorScheme.primary
    Canvas(modifier.fillMaxWidth().height(10.dp)) {
        val b = size.height - 1f
        drawLine(tinta.copy(alpha = 0.6f), Offset(0f, b), Offset(size.width, b), 1f)
        val paso = 6.dp.toPx(); var i = 0; var x = 0f
        while (x <= size.width) {
            val alto = if (i % 10 == 0) size.height else if (i % 5 == 0) size.height * 0.6f else size.height * 0.3f
            drawLine(tinta.copy(alpha = 0.5f), Offset(x, b), Offset(x, b - alto), 1f)
            x += paso; i++
        }
        drawRect(cian, Offset(size.width * 0.3f - 1.5f, 0f), Size(3f, size.height))
    }
}

/** Viñeta de imprenta: doble filete con un rombo al centro. */
@Composable
internal fun Vineta(modifier: Modifier) {
    val tinta = MaterialTheme.colorScheme.primary; val rojo = MaterialTheme.colorScheme.secondary
    Canvas(modifier.fillMaxWidth().height(12.dp)) {
        val y = size.height / 2; val cx = size.width / 2; val s = size.height * 0.45f
        val hueco = s * 3.2f
        listOf(-1.6f, 1.6f).forEach { dy ->
            drawLine(tinta, Offset(0f, y + dy), Offset(cx - hueco, y + dy), 1f)
            drawLine(tinta, Offset(cx + hueco, y + dy), Offset(size.width, y + dy), 1f)
        }
        val rombo = Path().apply { moveTo(cx, y - s); lineTo(cx + s, y); lineTo(cx, y + s); lineTo(cx - s, y); close() }
        drawPath(rombo, rojo)
        drawCircle(tinta, 1.8.dp.toPx(), Offset(cx - s * 2.1f, y)); drawCircle(tinta, 1.8.dp.toPx(), Offset(cx + s * 2.1f, y))
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

/** Grecas mongolas (alkhan khee): un meandro de martillos en azul con remates de oro. */
@Composable
internal fun Alhan(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(12.dp)) {
        val h = size.height; val u = h / 3f; val paso = u * 4
        val p = Path(); var x = 0f
        p.moveTo(0f, h * 0.5f)
        while (x + paso <= size.width) {
            // Un martillo: sube, gira dos veces y baja.
            p.lineTo(x + u, h * 0.5f); p.lineTo(x + u, 1f); p.lineTo(x + u * 3, 1f); p.lineTo(x + u * 3, h * 0.5f)
            p.lineTo(x + u * 2, h * 0.5f); p.lineTo(x + u * 2, h - 1f); p.lineTo(x + u * 4, h - 1f); p.lineTo(x + u * 4, h * 0.5f)
            x += paso
        }
        drawPath(p, e.primary, style = Stroke(1.6f))
        var k = 0; x = paso * 2
        while (x < size.width) { if (k % 4 == 0) drawCircle(e.tertiary, u * 0.55f, Offset(x, h * 0.5f)); x += paso; k++ }
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

/** Sarta de monedas de agujero cuadrado en un cordón de seda. */
@Composable
internal fun Monedas(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val oro = if (e.background.luminance() < 0.5f) e.primary else e.tertiary
    Canvas(modifier.fillMaxWidth().height(14.dp)) {
        val y = size.height / 2
        drawLine(e.secondary, Offset(0f, y), Offset(size.width, y), 1.6f)
        val r = size.height * 0.42f; var x = 30.dp.toPx(); var k = 0
        while (x < size.width) {
            val n = if (k % 3 == 0) 3 else 1
            for (i in 0 until n) {
                val cx = x + i * r * 1.4f
                drawCircle(oro, r, Offset(cx, y))
                drawRect(e.background, Offset(cx - r * 0.3f, y - r * 0.3f), Size(r * 0.6f, r * 0.6f))
            }
            x += 54.dp.toPx(); k++
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
 * Una textura muy tenue detrás de todo: renglones de pergamino, retícula de neón, trama de imprenta,
 * líneas de pantalla antigua, papel milimétrico… Se dibuja una sola vez y se reutiliza.
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
        TemaId.URSS -> {
            // Rayos que salen de una esquina, como en los carteles.
            color = esquema.primary.copy(alpha = if (oscuro) 0.06f else 0.05f)
            val o = Offset(0f, h)
            for (i in 0 until 14) {
                val a = rad(-90.0 + i * 6.5)
                trazo.moveTo(o.x, o.y); trazo.lineTo(o.x + cos(a) * (w + h) * 2, o.y + sin(a) * (w + h) * 2)
            }
        }
        else -> {}
    }
    onDrawBehind {
        if (color.alpha > 0f) drawPath(trazo, color, style = Stroke(1f))
        if (color2.alpha > 0f) drawPath(puntos, color2)
    }
}

// =====================================================================
//  Botón de modo claro / oscuro
// =====================================================================

/** Sol o luna dibujados, para cambiar de modo dentro de la app. El cyberpunk no tiene modo claro. */
@Composable
fun BotonModo(modifier: Modifier = Modifier) {
    if (Estilo.tema.soloOscuro) return
    val oscuro = esOscuro()
    val sentir = rememberSentir()
    val color = MaterialTheme.colorScheme.primary
    val fondo = MaterialTheme.colorScheme.background
    val borde = MaterialTheme.colorScheme.outline
    Box(
        modifier.padding(start = 4.dp).size(36.dp).clip(CircleShape).border(1.dp, borde, CircleShape).clickable {
            sentir(Sentidos.Evento.CAMBIO)
            Repo.cambiarConfig { c -> c.copy(modo = if (oscuro) "claro" else "oscuro") }
        }.semantics { contentDescription = if (oscuro) "Cambiar a modo claro" else "Cambiar a modo oscuro" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(18.dp)) {
            val c = center; val r = size.minDimension / 2
            if (oscuro) {
                // En oscuro se ofrece el sol.
                drawCircle(color, r * 0.42f, c)
                for (i in 0 until 8) { val a = rad(45.0 * i); drawLine(color, c.mas(r * 0.62f, a), c.mas(r * 0.95f, a), r * 0.14f, StrokeCap.Round) }
            } else {
                drawCircle(color, r * 0.8f, c)
                drawCircle(fondo, r * 0.68f, c + Offset(r * 0.38f, -r * 0.3f))
            }
        }
    }
}

/** Lupa para buscar en todo, junto al título de las pestañas principales. */
@Composable
fun BotonBuscar(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme.primary
    val borde = MaterialTheme.colorScheme.outline
    val sentir = rememberSentir()
    Box(
        modifier.size(36.dp).clip(CircleShape).border(1.dp, borde, CircleShape).clickable { sentir(Sentidos.Evento.TOQUE); onClick() }
            .semantics { contentDescription = "Buscar en todo" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(17.dp)) {
            val r = size.minDimension
            drawCircle(color, r * 0.3f, Offset(r * 0.42f, r * 0.42f), style = Stroke(r * 0.12f))
            drawLine(color, Offset(r * 0.64f, r * 0.64f), Offset(r * 0.92f, r * 0.92f), r * 0.14f, StrokeCap.Round)
        }
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

/** Lluvia breve de figuras del tema: hojas de laurel, píxeles, rayos, gotas de tinta, chispas, burbujas o brasas. */
@Composable
fun CapaFiesta() {
    val n = Fiesta.n
    if (n == 0 || !Repo.config.animaciones) return
    val progreso = remember(n) { androidx.compose.animation.core.Animatable(0f) }
    var activa by remember(n) { mutableStateOf(true) }
    LaunchedEffect(n) { progreso.animateTo(1f, tween(1600, easing = LinearEasing)); activa = false }
    if (!activa) return
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val tonos = listOf(e.primary, e.secondary, e.tertiary)
    val parts = remember(n) {
        val azar = java.util.Random(n.toLong())
        List(44) { Particula((azar.nextFloat() * 2 * PI).toFloat(), 0.25f + azar.nextFloat() * 0.75f, azar.nextFloat() * 720f - 360f, 0.6f + azar.nextFloat() * 0.8f, azar.nextInt(3), azar.nextFloat() * 0.25f) }
    }
    // Burbujas y brasas suben; lo demás cae.
    val sube = tema == TemaId.LABORATORIO || tema == TemaId.COLONOS || tema == TemaId.ESPACIO
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
                    TemaId.ROMANO -> drawOval(col, Offset(pos.x - s, pos.y - s * 0.4f), Size(s * 2, s * 0.8f))
                    TemaId.CIBER, TemaId.DISTOPIA -> drawRect(col, Offset(pos.x - s / 2, pos.y - s / 2), Size(s, s))
                    TemaId.HELENO, TemaId.ORDEN -> drawLine(col, pos - Offset(s, 0f), pos + Offset(s, 0f), s * 0.3f, StrokeCap.Round)
                    TemaId.LABORATORIO -> drawCircle(col, s * 0.7f, pos, style = Stroke(s * 0.18f))
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
                    TemaId.URSS, TemaId.NAPOLEON -> {
                        val est = Path(); for (i in 0 until 10) { val rr = if (i % 2 == 0) s else s * 0.4f; val q = pos.mas(rr, rad(-90.0 + 36.0 * i)); if (i == 0) est.moveTo(q.x, q.y) else est.lineTo(q.x, q.y) }
                        est.close(); drawPath(est, col)
                    }
                    TemaId.SEDA -> { drawCircle(col, s * 0.7f, pos); drawRect(e.background.copy(alpha = a), Offset(pos.x - s * 0.22f, pos.y - s * 0.22f), Size(s * 0.44f, s * 0.44f)) }
                    TemaId.REVOLUCION -> { drawCircle(e.primary.copy(alpha = a), s * 0.8f, pos); drawCircle(Color.White.copy(alpha = a), s * 0.55f, pos); drawCircle(e.secondary.copy(alpha = a), s * 0.3f, pos) }
                    else -> drawCircle(col, s * 0.5f, pos)
                }
            }
        }
    }
}

/** El engrane de arriba: ajustes y todo lo demás («Más»). */
@Composable
fun BotonAjustes(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val sentir = rememberSentir()
    Box(
        modifier.size(36.dp).clip(CircleShape).border(1.dp, MaterialTheme.colorScheme.outline, CircleShape).clickable { sentir(Sentidos.Evento.TOQUE); onClick() }
            .semantics { contentDescription = "Ajustes y más" },
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.primary) {
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) { IconoPestana("ajustes", nombre = "Ajustes y más") }
        }
    }
}
