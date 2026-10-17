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
    }
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
        modifier.size(40.dp).clip(CircleShape).border(1.dp, borde, CircleShape).clickable {
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
    val sube = tema == TemaId.LABORATORIO || tema == TemaId.COLONOS
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
                    else -> drawCircle(col, s * 0.5f, pos)
                }
            }
        }
    }
}
