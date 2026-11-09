package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Frases
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * El «corazón» de la frase del día, con la forma de cada tema (laurel, corazón de píxeles, sol de Vergina, cuadrifolio,
 * ojo, estrella atómica, escudo, anillo de benceno, casa, abeja, hoja, copo, moneda, flor de lis, destello, estrella de
 * ocho puntas, yurta, corazón con trazos de construcción, estrella roja, escarapela, gema): late al tocarlo y suelta una
 * chispa de puntos cuando se guarda. Guardada, va rellena; sin guardar, solo el contorno.
 */
@Composable
fun CorazonFrase(f: Frases.Frase, color: Color, modifier: Modifier = Modifier, fecha: String = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()) {
    val guardada = Frases.guardada(f)
    val sentir = rememberSentir()
    val scope = rememberCoroutineScope()
    val escala = remember { Animatable(1f) }
    val brote = remember { Animatable(1f) }
    val animar = Repo.config.animaciones
    val tema = Estilo.tema
    Box(
        modifier.size(40.dp).clip(CircleShape).clickable {
            val ahora = Frases.alternar(f, fecha = fecha)
            sentir(if (ahora) Sentidos.Evento.ELEGIR else Sentidos.Evento.TOQUE)
            if (animar) scope.launch { escala.snapTo(0.55f); escala.animateTo(1f, spring(dampingRatio = 0.32f, stiffness = 420f)) }
            if (animar && ahora) scope.launch { brote.snapTo(0f); brote.animateTo(1f, tween(520)) }
        }.semantics { contentDescription = if (guardada) "Quitar de frases guardadas" else "Guardar esta frase" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(24.dp).graphicsLayer { scaleX = escala.value; scaleY = escala.value }) {
            val u = size.minDimension / 24f
            formaCorazon(tema, u, color, guardada)
            val b = brote.value
            if (b < 1f) for (i in 0 until 8) {
                val a = Math.toRadians(45.0 * i - 90).toFloat()
                val r = (8f + 10f * b) * u
                drawCircle(color.copy(alpha = 1f - b), (1.6f - b) * u * 1.2f, Offset(center.x + r * cos(a), center.y + r * sin(a)))
            }
        }
    }
}

/** Dibuja la forma del tema en una cuadrícula de 24 × 24 (cada unidad mide [u]). */
private fun DrawScope.formaCorazon(tema: TemaId, u: Float, color: Color, lleno: Boolean) {
    fun p(x: Float, y: Float) = Offset(x * u, y * u)
    // Rellena si está guardada; si no, el contorno.
    fun pinta(path: Path, trazo: Float = 1.6f) { if (lleno) drawPath(path, color) else drawPath(path, color.copy(alpha = 0.7f), style = Stroke(trazo * u)) }
    fun poligono(vararg xy: Float, hueco: Boolean = false) = Path().apply {
        if (hueco) fillType = PathFillType.EvenOdd
        moveTo(xy[0] * u, xy[1] * u)
        var i = 2
        while (i + 1 < xy.size) { lineTo(xy[i] * u, xy[i + 1] * u); i += 2 }
        close()
    }
    fun estrella(cx: Float, cy: Float, puntas: Int, rExt: Float, rInt: Float, path: Path = Path()) = path.apply {
        for (i in 0 until puntas * 2) {
            val a = -Math.PI / 2 + i * Math.PI / puntas
            val r = if (i % 2 == 0) rExt else rInt
            val x = (cx + r * cos(a).toFloat()) * u; val y = (cy + r * sin(a).toFloat()) * u
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    fun circulo(path: Path, cx: Float, cy: Float, r: Float) = path.addOval(Rect((cx - r) * u, (cy - r) * u, (cx + r) * u, (cy + r) * u))
    // El corazón clásico, el de siempre.
    fun corazon() = Path().apply {
        moveTo(12f * u, 21f * u)
        cubicTo(5f * u, 15.5f * u, 2f * u, 12f * u, 2f * u, 8.5f * u)
        cubicTo(2f * u, 5.5f * u, 4.5f * u, 3f * u, 7.5f * u, 3f * u)
        cubicTo(9.5f * u, 3f * u, 11f * u, 4f * u, 12f * u, 5.8f * u)
        cubicTo(13f * u, 4f * u, 14.5f * u, 3f * u, 16.5f * u, 3f * u)
        cubicTo(19.5f * u, 3f * u, 22f * u, 5.5f * u, 22f * u, 8.5f * u)
        cubicTo(22f * u, 12f * u, 19f * u, 15.5f * u, 12f * u, 21f * u)
        close()
    }
    // Una hoja de a (base) a b (punta) que se abre [ancho] a cada lado; todas giran en el mismo sentido, así se suman al rellenar.
    fun hoja(path: Path, a: Offset, b: Offset, ancho: Float) {
        val d = b - a; val l = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y / l, d.x / l) * (2 * ancho * u)
        val m = (a + b) / 2f
        path.moveTo(a.x, a.y); path.quadraticTo(m.x + n.x, m.y + n.y, b.x, b.y); path.quadraticTo(m.x - n.x, m.y - n.y, a.x, a.y); path.close()
    }
    when (tema) {
        // Corona de laurel: dos ramas de cinco hojas atadas abajo.
        TemaId.ROMANO -> {
            val c = Path()
            listOf(115f, 145f, 175f, 205f, 235f).forEach { g ->
                val a = Math.toRadians(g.toDouble()); val b = Math.toRadians(g + 24.0)
                hoja(c, p(12f + 8f * cos(a).toFloat(), 12.5f + 8f * sin(a).toFloat()), p(12f + 9.5f * cos(b).toFloat(), 12.5f + 9.5f * sin(b).toFloat()), 1.3f)
                val a2 = Math.toRadians(180.0 - g); val b2 = Math.toRadians(180.0 - g - 24.0)
                hoja(c, p(12f + 8f * cos(a2).toFloat(), 12.5f + 8f * sin(a2).toFloat()), p(12f + 9.5f * cos(b2).toFloat(), 12.5f + 9.5f * sin(b2).toFloat()), 1.3f)
            }
            pinta(c, 1.2f)
            drawCircle(if (lleno) color else color.copy(alpha = 0.7f), 1.3f * u, p(12f, 21.2f))
        }
        // El candado de los enamorados de los puentes: el arco del candado y el cuerpo, con un corazón calado.
        TemaId.URBE -> {
            drawArc(if (lleno) color else color.copy(alpha = 0.7f), 180f, 180f, false, p(7f, 3f), androidx.compose.ui.geometry.Size(10f * u, 10f * u), style = Stroke(1.8f * u))
            listOf(7f, 17f).forEach { x -> drawLine(if (lleno) color else color.copy(alpha = 0.7f), p(x, 8f), p(x, 10f), 1.8f * u) }
            val cuerpo = Path().apply {
                fillType = PathFillType.EvenOdd
                addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(4f * u, 10f * u, 20f * u, 22f * u), androidx.compose.ui.geometry.CornerRadius(2.4f * u)))
                moveTo(12f * u, 19.5f * u); cubicTo(8.2f * u, 17f * u, 8.4f * u, 13.4f * u, 10.4f * u, 13.4f * u); cubicTo(11.3f * u, 13.4f * u, 11.8f * u, 14f * u, 12f * u, 14.6f * u)
                cubicTo(12.2f * u, 14f * u, 12.7f * u, 13.4f * u, 13.6f * u, 13.4f * u); cubicTo(15.6f * u, 13.4f * u, 15.8f * u, 17f * u, 12f * u, 19.5f * u); close()
            }
            pinta(cuerpo, 1.4f)
        }
        // Un corazón de píxeles, como en la consola; sin guardar, solo los píxeles del borde.
        TemaId.CIBER -> {
            val filas = listOf(".XX.XX.", "XXXXXXX", "XXXXXXX", ".XXXXX.", "..XXX..", "...X...")
            fun hay(f: Int, c: Int) = f in filas.indices && c in 0 until 7 && filas[f][c] == 'X'
            val px = 3f
            filas.forEachIndexed { f, fila ->
                fila.forEachIndexed { c, ch ->
                    val borde = !hay(f - 1, c) || !hay(f + 1, c) || !hay(f, c - 1) || !hay(f, c + 1)
                    if (ch == 'X' && (lleno || borde)) drawRect(if (lleno) color else color.copy(alpha = 0.7f), p(1.5f + c * px + 0.15f, 3f + f * px + 0.15f), Size((px - 0.3f) * u, (px - 0.3f) * u))
                }
            }
        }
        // El corazón de neón: un tubo de un solo trazo con su halo; lleno, encendido.
        TemaId.NEON -> {
            val tubo = Path().apply {
                val a = p(12f, 20f); moveTo(a.x, a.y)
                val b = p(2f, 9f); val c1 = p(4f, 2f); val m = p(12f, 7f); val c2 = p(20f, 2f); val d = p(22f, 9f)
                quadraticTo(b.x, b.y, c1.x, c1.y); quadraticTo(p(8f, 0f).x, p(8f, 0f).y, m.x, m.y)
                quadraticTo(p(16f, 0f).x, p(16f, 0f).y, c2.x, c2.y); quadraticTo(d.x, d.y, a.x, a.y)
            }
            if (lleno) drawPath(tubo, color.copy(alpha = 0.3f), style = Stroke(4f * u, cap = StrokeCap.Round))
            drawPath(tubo, if (lleno) color else color.copy(alpha = 0.6f), style = Stroke(1.5f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        // Sol de Vergina: dieciséis rayos y el disco al centro.
        TemaId.HELENO -> pinta(estrella(12f, 12f, 16, 11f, 5.6f).also { it.fillType = PathFillType.EvenOdd; circulo(it, 12f, 12f, 2.6f) }, 1.2f)
        // El ankh: el lazo, el travesaño y el fuste.
        TemaId.EGIPTO -> pinta(Path().apply {
            fillType = PathFillType.EvenOdd
            addOval(Rect(p(8.5f, 2f), p(15.5f, 11f))); addOval(Rect(p(10.3f, 4f), p(13.7f, 9.2f)))
            addRect(Rect(p(5f, 11f), p(19f, 13.2f))); addRect(Rect(p(10.9f, 13.2f), p(13.1f, 22f)))
        }, 1.2f)
        // Cuadrifolio de las tracerías.
        TemaId.MEDIEVAL -> pinta(Path().also { c -> circulo(c, 12f, 7.4f, 5f); circulo(c, 16.6f, 12f, 5f); circulo(c, 12f, 16.6f, 5f); circulo(c, 7.4f, 12f, 5f) }, 1.3f)
        // El escudo de lágrima de la casa.
        TemaId.TEMPLARIO -> pinta(Path().apply { moveTo(4.5f * u, 3.5f * u); lineTo(19.5f * u, 3.5f * u); quadraticTo(20.2f * u, 14.5f * u, 12f * u, 21f * u); quadraticTo(3.8f * u, 14.5f * u, 4.5f * u, 3.5f * u); close() }, 1.4f)
        // El ojo que todo lo ve.
        TemaId.DISTOPIA -> pinta(Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(1f * u, 12f * u); quadraticTo(12f * u, 1.5f * u, 23f * u, 12f * u); quadraticTo(12f * u, 22.5f * u, 1f * u, 12f * u); close()
            circulo(this, 12f, 12f, 4.4f); circulo(this, 12f, 12f, 1.9f)
        }, 1.4f)
        // Estrella atómica de los cincuenta: rayos largos y cortos alternados.
        TemaId.REFUGIO -> pinta(Path().apply {
            for (i in 0 until 16) {
                val a = -Math.PI / 2 + i * Math.PI / 8
                val r = if (i % 2 == 1) 3.6f else if (i % 4 == 0) 11.5f else 7.5f
                val x = (12f + r * cos(a).toFloat()) * u; val y = (12f + r * sin(a).toFloat()) * u
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }, 1.3f)
        // Escudo con la cruz de la orden.
        TemaId.ORDEN -> pinta(Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(3f * u, 3f * u); lineTo(21f * u, 3f * u); lineTo(21f * u, 11f * u); quadraticTo(21f * u, 18f * u, 12f * u, 22f * u); quadraticTo(3f * u, 18f * u, 3f * u, 11f * u); close()
            addPath(poligono(11f, 6f, 13f, 6f, 13f, 9.5f, 17f, 9.5f, 17f, 11.5f, 13f, 11.5f, 13f, 17f, 11f, 17f, 11f, 11.5f, 7f, 11.5f, 7f, 9.5f, 11f, 9.5f))
        })
        // Un corazón de latón con un engrane calado.
        TemaId.VAPOR -> pinta(Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(12f * u, 21f * u); cubicTo(2f * u, 14f * u, 2f * u, 5f * u, 8f * u, 5f * u); cubicTo(10.5f * u, 5f * u, 12f * u, 7f * u, 12f * u, 8f * u)
            cubicTo(12f * u, 7f * u, 13.5f * u, 5f * u, 16f * u, 5f * u); cubicTo(22f * u, 5f * u, 22f * u, 14f * u, 12f * u, 21f * u); close()
            addPath(io.github.ruddy2003.vestigium.ui.Vapor.engrane(p(12f, 12f), 3.6f * u, 8, 0.7f))
        })
        // Anillo de benceno: hexágono con su círculo.
        TemaId.LABORATORIO -> pinta(estrella(12f, 12f, 3, 10.5f, 10.5f).also { it.fillType = PathFillType.EvenOdd; circulo(it, 12f, 12f, 5f) })
        // Una casa con chimenea: el pueblo que se reconstruye.
        TemaId.COLONOS -> pinta(poligono(12f, 2.5f, 15.5f, 5.6f, 15.5f, 3.5f, 18f, 3.5f, 18f, 7.8f, 21.5f, 11f, 19f, 11f, 19f, 21f, 5f, 21f, 5f, 11f, 2.5f, 11f, hueco = true)
            .also { it.addPath(poligono(10.3f, 14.5f, 13.7f, 14.5f, 13.7f, 19.5f, 10.3f, 19.5f)) })
        // La abeja imperial.
        TemaId.NAPOLEON -> {
            pinta(Path().also { c ->
                c.addOval(Rect(9f * u, 9f * u, 15f * u, 21f * u)); c.addOval(Rect(10.2f * u, 4.8f * u, 13.8f * u, 8.6f * u))
                c.addOval(Rect(3f * u, 6.5f * u, 10.5f * u, 12f * u)); c.addOval(Rect(13.5f * u, 6.5f * u, 21f * u, 12f * u))
            }, 1.3f)
            listOf(13.5f, 16.5f).forEach { y -> drawLine(if (lleno) Color.Black.copy(alpha = 0.35f) else color.copy(alpha = 0.7f), p(9.6f, y), p(14.4f, y), 1f * u) }
        }
        // El abanico solar con su base: lleno, rayos de oro; vacío, el contorno.
        TemaId.DECO -> {
            pinta(Path().also { c ->
                c.moveTo(3f * u, 16f * u); c.arcTo(Rect(3f * u, 7f * u, 21f * u, 25f * u), 180f, 180f, false); c.close()
                c.addRect(Rect(3f * u, 16f * u, 21f * u, 18.5f * u))
            }, 1.3f)
            for (i in 1..4) {
                val a = Math.PI + i * Math.PI / 5
                drawLine(if (lleno) Color.Black.copy(alpha = 0.35f) else color.copy(alpha = 0.7f), p(12f, 16f), p(12f + 9f * cos(a).toFloat(), 16f + 9f * sin(a).toFloat()), 1f * u)
            }
        }
        // Una hoja de cuaderno de campo, con su nervio.
        TemaId.BOTANICO -> {
            pinta(Path().also { c -> hoja(c, p(5f, 20f), p(19.5f, 3.5f), 3.6f) })
            drawLine(if (lleno) Color.White.copy(alpha = 0.55f) else color.copy(alpha = 0.7f), p(4f, 21f), p(17.5f, 5.8f), 1f * u)
        }
        // Una hoja de hiedra de cinco puntas, con su nervio.
        TemaId.RUINAS -> {
            pinta(poligono(12f, 21f, 6f, 18f, 2.5f, 10.5f, 8f, 11f, 12f, 2.5f, 16f, 11f, 21.5f, 10.5f, 18f, 18f))
            drawLine(if (lleno) Color.White.copy(alpha = 0.55f) else color.copy(alpha = 0.7f), p(12f, 20f), p(12f, 6f), 1f * u)
        }
        // Copo de nieve: seis brazos con sus ramitas.
        TemaId.NAVIDAD -> {
            val k = if (lleno) color else color.copy(alpha = 0.7f); val t = (if (lleno) 2.2f else 1.4f) * u
            for (i in 0 until 6) {
                val a = i * Math.PI / 3
                val dx = cos(a).toFloat(); val dy = sin(a).toFloat()
                drawLine(k, p(12f, 12f), p(12f + 10.5f * dx, 12f + 10.5f * dy), t, cap = StrokeCap.Round)
                listOf(Math.PI / 4, -Math.PI / 4).forEach { g ->
                    val ex = cos(a + g).toFloat(); val ey = sin(a + g).toFloat()
                    drawLine(k, p(12f + 6.5f * dx, 12f + 6.5f * dy), p(12f + 6.5f * dx + 3.6f * ex, 12f + 6.5f * dy + 3.6f * ey), t * 0.8f, cap = StrokeCap.Round)
                }
            }
        }
        // Moneda con agujero cuadrado.
        TemaId.SEDA -> pinta(Path().apply {
            fillType = PathFillType.EvenOdd
            circulo(this, 12f, 12f, 10.5f)
            addPath(poligono(9f, 9f, 15f, 9f, 15f, 15f, 9f, 15f))
        }, 1.4f)
        // Un abanico plegable (sensu) abierto, con su remache.
        TemaId.EDO -> {
            pinta(Path().apply {
                fillType = PathFillType.EvenOdd
                moveTo(12f * u, 20.5f * u); lineTo(2.5f * u, 10.5f * u); quadraticTo(12f * u, 0.5f * u, 21.5f * u, 10.5f * u); close()
                moveTo(12f * u, 20.5f * u); lineTo(8.2f * u, 16.4f * u); quadraticTo(12f * u, 13f * u, 15.8f * u, 16.4f * u); close()
            }, 1.4f)
            for (k in 0..4) { val f = k / 4f; drawLine(if (lleno) Color.White.copy(alpha = 0.45f) else color.copy(alpha = 0.6f), p(12f, 20.5f), p(4.5f + 15f * f, 7.5f - 3.5f * (1 - (2 * f - 1) * (2 * f - 1))), 0.6f * u) }
        }
        // Flor de lis.
        TemaId.GOTICO -> pinta(Path().apply {
            moveTo(12f * u, 2f * u); quadraticTo(16.5f * u, 8f * u, 12f * u, 14.5f * u); quadraticTo(7.5f * u, 8f * u, 12f * u, 2f * u); close()
            moveTo(11f * u, 14.5f * u); cubicTo(4f * u, 14.5f * u, 0.5f * u, 9f * u, 4.5f * u, 7f * u); cubicTo(6.5f * u, 6f * u, 7.5f * u, 9f * u, 5.8f * u, 10f * u); cubicTo(7f * u, 12.5f * u, 9.5f * u, 12.5f * u, 11f * u, 12f * u); close()
            moveTo(13f * u, 12f * u); cubicTo(14.5f * u, 12.5f * u, 17f * u, 12.5f * u, 18.2f * u, 10f * u); cubicTo(16.5f * u, 9f * u, 17.5f * u, 6f * u, 19.5f * u, 7f * u); cubicTo(23.5f * u, 9f * u, 20f * u, 14.5f * u, 13f * u, 14.5f * u); close()
            addPath(poligono(7.5f, 14.5f, 16.5f, 14.5f, 16.5f, 16.5f, 7.5f, 16.5f))
            addPath(poligono(12f, 16.5f, 14.5f, 22f, 9.5f, 22f))
        }, 1.3f)
        // El ojo del signo, con la pupila vertical calada: lo que se guarda, se vigila.
        TemaId.ARCANO -> pinta(Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(1.5f * u, 12f * u); quadraticTo(12f * u, 0.5f * u, 22.5f * u, 12f * u); quadraticTo(12f * u, 23.5f * u, 1.5f * u, 12f * u); close()
            addOval(androidx.compose.ui.geometry.Rect(10.4f * u, 7.4f * u, 13.6f * u, 16.6f * u))
        }, 1.3f)
        // Destello de estrella de cuatro puntas.
        TemaId.ESPACIO -> pinta(Path().apply {
            moveTo(12f * u, 1f * u); quadraticTo(13f * u, 11f * u, 23f * u, 12f * u); quadraticTo(13f * u, 13f * u, 12f * u, 23f * u)
            quadraticTo(11f * u, 13f * u, 1f * u, 12f * u); quadraticTo(11f * u, 11f * u, 12f * u, 1f * u); close()
        }, 1.4f)
        // Una medusa: la campana y tres tentáculos en forma de gota.
        TemaId.ABISMO -> pinta(Path().apply {
            moveTo(2.5f * u, 12f * u); cubicTo(2.5f * u, 1.5f * u, 21.5f * u, 1.5f * u, 21.5f * u, 12f * u); quadraticTo(12f * u, 10f * u, 2.5f * u, 12f * u); close()
            listOf(7f, 12f, 17f).forEach { x -> moveTo((x - 1.4f) * u, 13f * u); quadraticTo((x - 2.2f) * u, 18f * u, x * u, 22.5f * u); quadraticTo((x + 1.6f) * u, 18f * u, (x + 1.4f) * u, 13f * u); close() }
        }, 1.3f)
        // Estrella de ocho puntas de los alicatados, con el centro abierto.
        TemaId.ANDALUS -> pinta(estrella(12f, 12f, 8, 11f, 8.4f).also { it.fillType = PathFillType.EvenOdd; circulo(it, 12f, 12f, 3.2f) }, 1.3f)
        // El cartucho de un glifo con su pie de tres lóbulos y un punto hueco en el centro.
        TemaId.MESO -> pinta(Path().apply {
            fillType = PathFillType.EvenOdd
            addRoundRect(androidx.compose.ui.geometry.RoundRect(3f * u, 2.5f * u, 21f * u, 17f * u, androidx.compose.ui.geometry.CornerRadius(4.5f * u, 4.5f * u)))
            for (k in -1..1) circulo(this, 12f + k * 4.6f, 19.6f, 2.2f)
            circulo(this, 12f, 9.8f, 2.6f)
        }, 1.3f)
        // La yurta: cúpula, muro y puerta.
        TemaId.MONGOL -> pinta(Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(2f * u, 13f * u); quadraticTo(12f * u, 1f * u, 22f * u, 13f * u); lineTo(20.5f * u, 13f * u); lineTo(20.5f * u, 21f * u); lineTo(3.5f * u, 21f * u); lineTo(3.5f * u, 13f * u); close()
            addPath(poligono(10.2f, 15f, 13.8f, 15f, 13.8f, 20f, 10.2f, 20f))
        }, 1.4f)
        // El corazón de siempre, con sus trazos de construcción: el círculo que lo contiene y los centros de los lóbulos.
        TemaId.RENACIMIENTO -> {
            val k = color.copy(alpha = 0.35f)
            drawCircle(k, 11f * u, p(12f, 12f), style = Stroke(0.6f * u))
            drawCircle(k, 4.6f * u, p(7.5f, 8.2f), style = Stroke(0.6f * u)); drawCircle(k, 4.6f * u, p(16.5f, 8.2f), style = Stroke(0.6f * u))
            drawLine(k, p(12f, 1f), p(12f, 23f), 0.6f * u)
            pinta(corazon(), 1.5f)
        }
        // El corazón tatuado del marinero, con su cinta y el ancla detrás.
        TemaId.PIRATA -> {
            val k = color.copy(alpha = 0.4f)
            drawLine(k, p(12f, 2f), p(12f, 22f), 0.9f * u); drawLine(k, p(7f, 5f), p(17f, 5f), 0.9f * u)
            drawPath(Path().apply { moveTo(3f * u, 15f * u); quadraticTo(4f * u, 22f * u, 12f * u, 22f * u); quadraticTo(20f * u, 22f * u, 21f * u, 15f * u) }, k, style = Stroke(0.9f * u))
            pinta(corazon(), 1.5f)
        }
        // Estrella roja de cinco puntas.
        TemaId.URSS -> pinta(estrella(12f, 12.8f, 5, 11f, 4.4f), 1.4f)
        // Escarapela: anillos concéntricos.
        TemaId.REVOLUCION -> pinta(Path().apply { fillType = PathFillType.EvenOdd; circulo(this, 12f, 12f, 10.5f); circulo(this, 12f, 12f, 7f); circulo(this, 12f, 12f, 3.5f) }, 1.3f)
        // Una gema tallada, con sus facetas.
        TemaId.ANTIGUAS -> {
            pinta(poligono(6.5f, 4f, 17.5f, 4f, 22f, 9.5f, 12f, 21.5f, 2f, 9.5f), 1.4f)
            val k = if (lleno) Color.White.copy(alpha = 0.45f) else color.copy(alpha = 0.5f)
            drawLine(k, p(2f, 9.5f), p(22f, 9.5f), 0.7f * u)
            drawPath(Path().apply { moveTo(6.5f * u, 4f * u); lineTo(9f * u, 9.5f * u); lineTo(12f * u, 21.5f * u); lineTo(15f * u, 9.5f * u); lineTo(17.5f * u, 4f * u)
                moveTo(9f * u, 9.5f * u); lineTo(12f * u, 4f * u); lineTo(15f * u, 9.5f * u) }, k, style = Stroke(0.7f * u))
        }
        // Guardar una frase es dejar la mano en la pared.
        TemaId.CUEVA -> pinta(io.github.ruddy2003.vestigium.ui.manoUnida(p(12f, 12.2f), 9.4f * u), 1.3f)
        TemaId.BASICO -> pinta(corazon())
    }
}

/** Una frase en la lista: del día, de Kindle o copiada de una obra. [quitar] la saca de la lista. */
private class ItemFrase(val clave: String, val original: String?, val texto: String, val autor: String, val origen: String, val fecha: String, val obra: String?,
                        val restaurar: () -> Unit = {}, val quitar: () -> Unit)

/** Las frases que guardaste con el corazón: las del día (de todos los temas), los subrayados de Kindle y las citas de tus obras. */
@Composable
fun PantallaFrasesGuardadas(nav: Nav) {
    val delDia = Repo.config.frases_guardadas.map { g ->
        ItemFrase("f" + g.texto + g.autor, g.original, g.texto, g.autor,
            if (g.tema == "kindle") "Subrayado de Kindle" else "Frase del día · " + (TemaId.entries.firstOrNull { it.id == g.tema }?.nombre ?: g.tema), g.fecha, null,
            restaurar = { Repo.cambiarConfig { c -> if (c.frases_guardadas.any { it.texto == g.texto && it.autor == g.autor }) c else c.copy(frases_guardadas = c.frases_guardadas + g) } }) {
            Repo.cambiarConfig { c -> c.copy(frases_guardadas = c.frases_guardadas.filterNot { it.texto == g.texto && it.autor == g.autor }) }
        }
    }
    val deObras = Repo.obras.flatMap { o -> o.citas.filter { it.favorita }.map { c ->
        ItemFrase("c" + c.id, null, c.texto, listOfNotNull(o.autor.ifBlank { null }, o.titulo).joinToString(", ") + (c.pagina.ifBlank { null }?.let { if (it.all { ch -> ch.isDigit() }) ", p. $it" else ", $it" } ?: ""),
            "Cita de " + (TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre?.lowercase() ?: "obra"), c.fecha.ifBlank { o.agregado }, o.id,
            restaurar = { Repo.obras.firstOrNull { it.id == o.id }?.let { x -> Repo.guardarObra(x.copy(citas = x.citas.map { if (it.id == c.id) it.copy(favorita = true) else it })) } }) {
            Repo.obras.firstOrNull { it.id == o.id }?.let { x -> Repo.guardarObra(x.copy(citas = x.citas.map { if (it.id == c.id) it.copy(favorita = false) else it })) }
        }
    } }
    val todas = (delDia + deObras).sortedByDescending { it.fecha }
    var filtro by remember { mutableStateOf<String?>(null) }
    val lista = todas.filter { filtro == null || it.origen.startsWith(filtro!!) }
    var armado by remember { mutableStateOf<String?>(null) }
    val sentir = rememberSentir()
    Hoja("Frases guardadas", nav.atras, "Las frases a las que les diste ♥: las del día, tus subrayados de Kindle y las citas que copias de tus libros, películas y artículos.") {
        if (todas.isNotEmpty()) Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todas", filtro == null) { filtro = null }
            if (delDia.any { it.origen.startsWith("Frase") }) Ficha("del día", filtro == "Frase") { filtro = "Frase" }
            if (deObras.isNotEmpty()) Ficha("de mis obras", filtro == "Cita") { filtro = "Cita" }
            if (delDia.any { it.origen.startsWith("Subrayado") }) Ficha("Kindle", filtro == "Subrayado") { filtro = "Subrayado" }
        }
        if (todas.isEmpty()) Box(Modifier.padding(top = 16.dp)) { Vacio("Todavía no guardas ninguna. Toca el corazón junto a la frase del día, o guarda citas dentro de un libro en Obras.") }
        lista.forEach { g ->
            Box(Modifier.padding(top = 12.dp)) {
                Tarjeta {
                    g.original?.let { Text(it, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.primary) }
                    Text("«${g.texto}»", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
                    Text("— ${g.autor}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp).then(if (g.obra != null) Modifier.clickable { EdicionObra.id = g.obra; nav.ir("obra") } else Modifier))
                    Row(Modifier.fillMaxWidth().padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${g.origen} · ${Fechas.relativo(g.fecha)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        BotonTexto(onClick = {
                            if (armado == g.clave) { sentir(Sentidos.Evento.TOQUE); g.quitar(); armado = null; io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Frase quitada") { g.restaurar() } } else armado = g.clave
                        }) {
                            Text(if (armado == g.clave) "¿Quitar?" else "Quitar", color = if (armado == g.clave) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
