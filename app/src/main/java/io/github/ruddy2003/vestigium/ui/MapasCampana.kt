package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los mapas de campaña de los 21 temas. El fondo, el camino y las paradas de cada uno se dibujan en [MapasMas] (el motor
 * de rutas, con sus siete puntos por tema); aquí está la escena de la victoria: la corona triunfal en Alesia, el sol de
 * Vergina en Babilonia, la cruz de Santiago, las campanas de la catedral, el cuadro enmarcado y firmado, el águila de 1804,
 * la bandera de la Federación, la casa del pueblo nuevo, la flor de la expedición, el árbol de la plaza, los farolillos de
 * Antioquía, el arco de herradura, el ovoo, la almenara, el sello de «APROBADO» y las escenas de los temas del lote 2.
 */
object MapasCampana {
    /** El fondo del mapa. [avance] va de 0 a 1 (días cumplidos de los siete). */
    fun DrawScope.fondo(tema: TemaId, cs: ColorScheme, avance: Float) {
        with(MapasMas) { fondoMas(tema, cs, avance) }
    }

    /** El camino entre etapas. */
    fun DrawScope.ruta(tema: TemaId, cs: ColorScheme, p: Path) {
        with(MapasMas) { rutaMas(tema, cs, p) }
    }

    /** La marca de cada etapa (la de la meta un poco más grande). */
    fun DrawScope.etapa(tema: TemaId, cs: ColorScheme, c: Offset, r: Float, col: Color, meta: Boolean) {
        with(MapasMas) { etapaMas(tema, cs, c, r, col, meta) }
    }

    /** Lo que se ve en la meta [m] cuando se gana la campaña: una escena propia de cada tema. */
    fun DrawScope.victoria(tema: TemaId, cs: ColorScheme, m: Offset) {
        val d = density
        val oro = Color(0xFFD4AF37)
        when (tema) {
            TemaId.ROMANO -> {
                // La corona triunfal: dos ramas de laurel de oro que casi se cierran arriba, con sus bayas, y las cintas
                // rojas atadas abajo.
                val c = Offset(m.x, m.y - 22 * d); val r = 11 * d
                val rojo = Color(0xFFA3172E)
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(c.x, c.y + r); quadraticTo(c.x + s * 3 * d, c.y + r + 6 * d, c.x + s * 7 * d, c.y + r + 9 * d) }, rojo, style = Stroke(1.6f * d))
                    for (k in 0 until 8) {
                        val a = (PI / 2 + s * (0.35 + k * 0.33)).toFloat()
                        val o = Offset(c.x + r * cos(a), c.y + r * sin(a))
                        hoja(o, a + s * (PI / 2).toFloat() + s * 0.35f, 6.4f * d, 1.7f * d, oro)
                        if (k % 2 == 1) drawCircle(lerp(oro, Color.Black, 0.25f), 0.9f * d, Offset(c.x + r * 0.78f * cos(a), c.y + r * 0.78f * sin(a)))
                    }
                }
                drawRect(rojo, Offset(c.x - 2 * d, c.y + r - 1.5f * d), Size(4 * d, 3 * d))
            }
            TemaId.HELENO -> with(TrofeosTema) {
                // El sol de Vergina, de dieciséis rayos, en oro sobre el escudo púrpura de Macedonia.
                val c = Offset(m.x, m.y - 24 * d)
                val purpura = Color(0xFF5B2A6E)
                drawCircle(purpura, 12 * d, c)
                drawCircle(oro, 12 * d, c, style = Stroke(1.4f * d))
                estrella(c, 10 * d, 3.4f * d, 16, Brush.linearGradient(listOf(oro, oro)))
                drawCircle(purpura, 2.6f * d, c); drawCircle(oro, 1.5f * d, c)
            }
            TemaId.EGIPTO -> {
                // Un obelisco recién alzado en Elefantina: el piramidión dorado brilla y el sol sale detrás, entre sus rayos.
                val base = Offset(m.x, m.y - 4 * d); val alto = 36 * d
                val sol = Offset(base.x, base.y - 22 * d)
                for (k in 0 until 9) {
                    val a = PI * (1.0 + k / 8.0)
                    drawLine(oro.copy(alpha = 0.55f), Offset(sol.x + (14 * d * cos(a)).toFloat(), sol.y + (14 * d * sin(a)).toFloat()),
                        Offset(sol.x + (22 * d * cos(a)).toFloat(), sol.y + (22 * d * sin(a)).toFloat()), 1.4f * d)
                }
                drawCircle(Color(0xFFB8471E), 9 * d, sol)
                drawPath(Path().apply { moveTo(base.x - 5 * d, base.y); lineTo(base.x - 3.6f * d, base.y - alto); lineTo(base.x + 3.6f * d, base.y - alto); lineTo(base.x + 5 * d, base.y); close() }, Color(0xFF9C7E64))
                drawPath(Path().apply { moveTo(base.x - 3.6f * d, base.y - alto); lineTo(base.x, base.y - alto - 6 * d); lineTo(base.x + 3.6f * d, base.y - alto); close() }, oro)
                for (k in 1..4) drawLine(Color(0xFF5E4A38), Offset(base.x - 1.2f * d, base.y - alto * k / 5f), Offset(base.x + 1.2f * d, base.y - alto * k / 5f), 0.8f * d)
                drawRect(Color(0xFF6E5A46), Offset(base.x - 8 * d, base.y), Size(16 * d, 3 * d))
            }
            TemaId.MEDIEVAL -> {
                // La cruz de Santiago en rojo, con la hoja de espada hacia abajo y flores de lis en las otras tres puntas.
                val rojo = Color(0xFFB0281E); val x = m.x; val y = m.y - 30 * d
                drawPath(Path().apply { moveTo(x - 1.6f * d, y); lineTo(x + 1.6f * d, y); lineTo(x + 1f * d, y + 14 * d); lineTo(x, y + 17 * d); lineTo(x - 1f * d, y + 14 * d); close() }, rojo)
                drawLine(rojo, Offset(x - 8 * d, y), Offset(x + 8 * d, y), 2.4f * d)
                drawLine(rojo, Offset(x, y), Offset(x, y - 8 * d), 2.4f * d)
                lis(Offset(x - 8.5f * d, y), PI.toFloat(), 3.4f * d, rojo)
                lis(Offset(x + 8.5f * d, y), 0f, 3.4f * d, rojo)
                lis(Offset(x, y - 8.5f * d), (-PI / 2).toFloat(), 3.4f * d, rojo)
            }
            TemaId.TEMPLARIO -> {
                // El castillo de Montalbar tomado: la torre almenada de piedra, el estandarte «baucent» (negro arriba, blanco
                // abajo) clavado en lo alto y, en la puerta, el escudo con la cruz patada roja.
                val piedra = Color(0xFFB9A57E); val sombra = lerp(piedra, Color.Black, 0.35f); val x = m.x; val base = m.y - 4 * d
                drawRect(piedra, Offset(x - 8 * d, base - 20 * d), Size(16 * d, 20 * d))
                for (k in 0 until 3) drawRect(piedra, Offset(x - 8 * d + k * 6 * d, base - 24 * d), Size(4 * d, 4 * d))
                drawRect(sombra, Offset(x - 8 * d, base - 20 * d), Size(16 * d, 20 * d), style = Stroke(0.8f * d))
                drawPath(Path().apply { moveTo(x - 3 * d, base); lineTo(x - 3 * d, base - 6 * d); quadraticTo(x, base - 10 * d, x + 3 * d, base - 6 * d); lineTo(x + 3 * d, base); close() }, sombra)
                drawLine(cs.onSurface, Offset(x, base - 24 * d), Offset(x, base - 42 * d), 1.2f * d)
                drawRect(Color(0xFF1A1816), Offset(x, base - 42 * d), Size(12 * d, 4.5f * d))
                drawRect(Color.White, Offset(x, base - 37.5f * d), Size(12 * d, 4.5f * d))
                drawRect(cs.onSurface.copy(alpha = 0.6f), Offset(x, base - 42 * d), Size(12 * d, 9 * d), style = Stroke(0.6f * d))
                drawPath(cruzPatada(Offset(x, base - 14 * d), 3.4f * d), Color(0xFFB0191F))
            }
            TemaId.GOTICO -> {
                // Las campanas echadas a vuelo en la torre: la campana de bronce en su yugo, el badajo y las ondas del repique.
                val bronce = Color(0xFFC08A3A); val c = Offset(m.x, m.y - 22 * d)
                drawLine(cs.onSurface, Offset(c.x - 9 * d, c.y - 10 * d), Offset(c.x + 9 * d, c.y - 10 * d), 1.6f * d)
                drawLine(cs.onSurface, Offset(c.x, c.y - 10 * d), Offset(c.x, c.y - 8 * d), 1.6f * d)
                drawPath(Path().apply { moveTo(c.x - 8 * d, c.y + 6 * d); cubicTo(c.x - 6 * d, c.y + 1 * d, c.x - 6 * d, c.y - 9 * d, c.x, c.y - 9 * d); cubicTo(c.x + 6 * d, c.y - 9 * d, c.x + 6 * d, c.y + 1 * d, c.x + 8 * d, c.y + 6 * d); close() }, bronce)
                drawOval(lerp(bronce, Color.Black, 0.25f), Offset(c.x - 8.5f * d, c.y + 4.6f * d), Size(17 * d, 3 * d))
                drawLine(lerp(bronce, Color.White, 0.4f), Offset(c.x - 3 * d, c.y - 6 * d), Offset(c.x - 4.6f * d, c.y + 3 * d), 0.8f * d)
                drawCircle(lerp(bronce, Color.Black, 0.45f), 1.8f * d, Offset(c.x + 2.2f * d, c.y + 7.6f * d))
                listOf(13f, 17f).forEach { rr ->
                    val R = rr * d
                    drawArc(oro, -35f, 70f, false, Offset(c.x - R, c.y - R), Size(2 * R, 2 * R), style = Stroke(1.2f * d))
                    drawArc(oro, 145f, 70f, false, Offset(c.x - R, c.y - R), Size(2 * R, 2 * R), style = Stroke(1.2f * d))
                }
            }
            TemaId.RENACIMIENTO -> {
                // El cuadro terminado: el marco dorado alrededor del lienzo, con sus rosetas, y la firma con su rúbrica.
                val w = size.width; val h = size.height
                val r = Rect(w * 0.3f, h * 0.08f, w * 0.7f, h * 0.92f)
                drawRect(oro, Offset(r.left - 3 * d, r.top - 3 * d), Size(r.width + 6 * d, r.height + 6 * d), style = Stroke(4 * d))
                drawRect(Color(0xFF8C6A1E), Offset(r.left - 0.5f * d, r.top - 0.5f * d), Size(r.width + 1 * d, r.height + 1 * d), style = Stroke(1 * d))
                listOf(r.topLeft, r.topRight, r.bottomLeft, r.bottomRight).forEach { e -> drawCircle(Color(0xFF8C6A1E), 2.6f * d, e); drawCircle(oro, 1.4f * d, e) }
                val tinta = Color(0xFF3A2614)
                drawPath(Path().apply {
                    moveTo(m.x - 18 * d, m.y - 10 * d); cubicTo(m.x - 15 * d, m.y - 19 * d, m.x - 11 * d, m.y - 4 * d, m.x - 8 * d, m.y - 12 * d)
                    cubicTo(m.x - 6 * d, m.y - 18 * d, m.x - 3 * d, m.y - 6 * d, m.x, m.y - 12 * d); cubicTo(m.x + 2 * d, m.y - 16 * d, m.x + 4 * d, m.y - 9 * d, m.x + 6 * d, m.y - 13 * d)
                }, tinta, style = Stroke(1 * d))
                drawPath(Path().apply { moveTo(m.x - 19 * d, m.y - 6 * d); quadraticTo(m.x - 4 * d, m.y - 3 * d, m.x + 8 * d, m.y - 8 * d); quadraticTo(m.x + 2 * d, m.y - 9 * d, m.x - 2 * d, m.y - 5 * d) }, tinta, style = Stroke(0.8f * d))
            }
            TemaId.PIRATA -> {
                // El cofre desenterrado sobre la «X»: la tapa abierta, el brillo del oro que sale y los doblones regados.
                val c = Offset(m.x, m.y - 16 * d)
                val madera = Color(0xFF6B4A2B); val laton = Color(0xFFC9A04A)
                drawCircle(Brush.radialGradient(listOf(oro.copy(alpha = 0.5f), Color.Transparent), c, 26 * d), 26 * d, c)
                drawLine(Color(0xFF9A2A1C), Offset(m.x - 12 * d, m.y - 2 * d), Offset(m.x + 12 * d, m.y + 4 * d), 2.4f * d, androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(Color(0xFF9A2A1C), Offset(m.x + 12 * d, m.y - 2 * d), Offset(m.x - 12 * d, m.y + 4 * d), 2.4f * d, androidx.compose.ui.graphics.StrokeCap.Round)
                drawPath(Path().apply { moveTo(c.x - 12 * d, c.y - 2 * d); lineTo(c.x - 10 * d, c.y - 16 * d); quadraticTo(c.x, c.y - 21 * d, c.x + 10 * d, c.y - 16 * d); lineTo(c.x + 12 * d, c.y - 2 * d); close() }, lerp(madera, Color.Black, 0.25f))
                for (k in 0 until 5) drawCircle(oro, 3 * d, Offset(c.x - 8 * d + k * 4 * d, c.y - 1 * d - (k % 2) * 2 * d))
                drawRect(madera, Offset(c.x - 12 * d, c.y), Size(24 * d, 12 * d))
                listOf(-9f, 7f).forEach { x -> drawRect(laton, Offset(c.x + x * d, c.y), Size(2 * d, 12 * d)) }
                drawRect(laton, Offset(c.x - 2 * d, c.y + 2 * d), Size(4 * d, 4 * d))
                listOf(Offset(-17f, 10f), Offset(15f, 12f), Offset(19f, 6f)).forEach { o -> drawOval(oro, Offset(c.x + o.x * d - 2.5f * d, c.y + o.y * d - 1.2f * d), Size(5 * d, 2.4f * d)) }
            }
            TemaId.NAPOLEON -> {
                // El sol de Austerlitz detrás y el águila imperial en lo alto del asta, sobre la bandera de 1804: el rombo
                // blanco al centro y los rincones azules y rojos, con su fleco de oro.
                drawCircle(Brush.radialGradient(listOf(oro.copy(alpha = 0.35f), Color.Transparent), Offset(m.x + 6 * d, m.y - 24 * d), 22 * d), 22 * d, Offset(m.x + 6 * d, m.y - 24 * d))
                val top = m.y - 38 * d
                drawLine(cs.onSurface, m, Offset(m.x, top), 1.2f * d)
                val f = Rect(m.x, top + 4 * d, m.x + 16 * d, top + 18 * d)
                val azul = Color(0xFF1E3A8A); val rojo = Color(0xFFB3122E)
                drawRect(Color.White, f.topLeft, f.size)
                fun rincon(a: Offset, b: Offset, c: Offset, col: Color) = drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); close() }, col)
                rincon(f.topLeft, Offset(f.center.x, f.top), Offset(f.left, f.center.y), azul)
                rincon(Offset(f.center.x, f.top), f.topRight, Offset(f.right, f.center.y), rojo)
                rincon(Offset(f.right, f.center.y), f.bottomRight, Offset(f.center.x, f.bottom), azul)
                rincon(Offset(f.center.x, f.bottom), f.bottomLeft, Offset(f.left, f.center.y), rojo)
                drawRect(oro, f.topLeft, f.size, style = Stroke(1 * d))
                // El águila: las alas abiertas sobre su peana.
                drawRect(oro, Offset(m.x - 2.5f * d, top - 0.5f * d), Size(5 * d, 1.5f * d))
                drawOval(oro, Offset(m.x - 1.6f * d, top - 6 * d), Size(3.2f * d, 6 * d))
                drawCircle(oro, 1.3f * d, Offset(m.x, top - 6.8f * d))
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(m.x, top - 3.5f * d); lineTo(m.x + s * 6.5f * d, top - 9 * d); lineTo(m.x + s * 5.5f * d, top - 5 * d); lineTo(m.x + s * 7 * d, top - 3.5f * d); lineTo(m.x + s * 1.5f * d, top - 1 * d); close() }, oro) }
            }
            TemaId.REVOLUCION -> {
                // La fiesta de la Federación: la bandera azul, blanca y roja que ondea en su asta, con el gorro frigio en la punta.
                val top = m.y - 36 * d
                drawLine(cs.onSurface, m, Offset(m.x, top), 1.2f * d)
                fun ola(x: Float) = sin((x - m.x) / (16 * d) * PI.toFloat()) * 2 * d
                listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234)).forEachIndexed { i, col ->
                    val x0 = m.x + i * 6 * d; val x1 = x0 + 6 * d; val xm = (x0 + x1) / 2
                    val cy = 2 * ola(xm) - (ola(x0) + ola(x1)) / 2
                    val franja = Path().apply {
                        moveTo(x0, top + 2 * d + ola(x0)); quadraticTo(xm, top + 2 * d + cy, x1, top + 2 * d + ola(x1))
                        lineTo(x1, top + 14 * d + ola(x1)); quadraticTo(xm, top + 14 * d + cy, x0, top + 14 * d + ola(x0)); close()
                    }
                    drawPath(franja, col)
                    drawPath(franja, cs.onSurface.copy(alpha = 0.3f), style = Stroke(0.6f * d))
                }
                drawPath(Path().apply {
                    moveTo(m.x - 3 * d, top + 1 * d); cubicTo(m.x - 3.5f * d, top - 3 * d, m.x, top - 5 * d, m.x + 2 * d, top - 4.5f * d)
                    cubicTo(m.x + 4.5f * d, top - 4 * d, m.x + 4 * d, top - 1 * d, m.x + 2.6f * d, top - 1.5f * d); lineTo(m.x + 3 * d, top + 1 * d); close()
                }, Color(0xFFB22234))
            }
            TemaId.COLONOS -> {
                // El pueblo nuevo: la casa del pueblo de tablas con techo de ladrillo, el campanario, el humo de la chimenea y
                // la bandera en lo alto.
                val ladrillo = Color(0xFF9C4A2E); val tabla = Color(0xFFE9DCC0); val b = m.y - 10 * d
                drawRect(ladrillo, Offset(m.x + 6 * d, b - 18 * d), Size(3 * d, 6 * d))
                val az = java.util.Random(41)
                for (k in 0 until 3) drawCircle(Color(0xFF8A8D91).copy(alpha = 0.5f - k * 0.12f), (1.6f + k * 0.9f) * d, Offset(m.x + (8 + k * 3) * d, b - (20 + k * 4) * d + az.nextFloat() * d))
                drawRect(tabla, Offset(m.x - 11 * d, b - 10 * d), Size(22 * d, 10 * d))
                for (k in 1 until 4) drawLine(ladrillo.copy(alpha = 0.3f), Offset(m.x - 11 * d, b - k * 2.5f * d), Offset(m.x + 11 * d, b - k * 2.5f * d), 0.5f * d)
                drawRect(ladrillo.copy(alpha = 0.7f), Offset(m.x - 11 * d, b - 10 * d), Size(22 * d, 10 * d), style = Stroke(0.8f * d))
                drawPath(Path().apply { moveTo(m.x - 13 * d, b - 10 * d); lineTo(m.x, b - 17 * d); lineTo(m.x + 13 * d, b - 10 * d); close() }, ladrillo)
                drawRect(Color(0xFF3A2A1C), Offset(m.x - 2 * d, b - 6 * d), Size(4 * d, 6 * d))
                listOf(-7f, 5f).forEach { x -> drawRect(Color(0xFF1F3A6E).copy(alpha = 0.7f), Offset(m.x + x * d, b - 7.5f * d), Size(2.4f * d, 2.4f * d)) }
                drawRect(tabla, Offset(m.x - 2.5f * d, b - 23 * d), Size(5 * d, 7 * d))
                drawRect(ladrillo.copy(alpha = 0.7f), Offset(m.x - 2.5f * d, b - 23 * d), Size(5 * d, 7 * d), style = Stroke(0.6f * d))
                drawCircle(Color(0xFFC9A04A), 1.2f * d, Offset(m.x, b - 19.5f * d))
                drawPath(Path().apply { moveTo(m.x - 3.5f * d, b - 23 * d); lineTo(m.x, b - 29 * d); lineTo(m.x + 3.5f * d, b - 23 * d); close() }, ladrillo)
                drawLine(cs.onSurface, Offset(m.x, b - 29 * d), Offset(m.x, b - 37 * d), 0.9f * d)
                drawRect(Color(0xFF1F3A6E), Offset(m.x, b - 37 * d), Size(8 * d, 5 * d))
                drawRect(Color(0xFFF1E6C8), Offset(m.x, b - 35 * d), Size(8 * d, 1 * d))
            }
            TemaId.BOTANICO -> {
                // La flor que se abre al final de la expedición: el tallo con sus hojas, los pétalos y una mariposa que llega.
                val verde = Color(0xFF3F7A3A); val f = Offset(m.x, m.y - 26 * d)
                drawPath(Path().apply { moveTo(m.x, m.y - 8 * d); quadraticTo(m.x - 3 * d, m.y - 16 * d, f.x, f.y) }, verde, style = Stroke(1.4f * d))
                hoja(Offset(m.x - 4 * d, m.y - 13 * d), -2.36f, 7 * d, 2 * d, verde)
                hoja(Offset(m.x + 2.6f * d, m.y - 18 * d), -0.79f, 6 * d, 1.8f * d, verde)
                for (k in 0 until 6) { val a = k * PI / 3; val o = Offset(f.x + (5.6f * d * cos(a)).toFloat(), f.y + (5.6f * d * sin(a)).toFloat()); drawCircle(Color(0xFFC86A7A), 3.6f * d, o); drawCircle(Color(0xFFE8A0AC), 1.8f * d, o) }
                drawCircle(Color(0xFFE2B34A), 3 * d, f)
                for (k in 0 until 5) { val a = k * 2 * PI / 5; drawCircle(Color(0xFF8A5A2B), 0.5f * d, Offset(f.x + (1.6f * d * cos(a)).toFloat(), f.y + (1.6f * d * sin(a)).toFloat())) }
                val mp = Offset(m.x + 15 * d, m.y - 34 * d)
                listOf(-1f, 1f).forEach { s ->
                    drawOval(Color(0xFF2E6DB4), Offset(mp.x + (if (s < 0) -4.4f else 0.2f) * d, mp.y - 3.6f * d), Size(4.2f * d, 3.6f * d))
                    drawOval(Color(0xFF6FA8DC), Offset(mp.x + (if (s < 0) -3.2f else 0.4f) * d, mp.y - 0.2f * d), Size(2.8f * d, 2.6f * d))
                }
                drawLine(Color(0xFF2A1E0E), Offset(mp.x, mp.y - 3 * d), Offset(mp.x, mp.y + 2.4f * d), 0.9f * d)
            }
            TemaId.NAVIDAD -> {
                // El árbol de la plaza: tres pisos de ramas, las bolas de colores, la estrella de oro y los regalos al pie.
                val verde = Color(0xFF1F6B3A); val b = m.y - 8 * d
                drawRect(Color(0xFF5A3A22), Offset(m.x - 1.6f * d, b - 3 * d), Size(3.2f * d, 4 * d))
                listOf(Triple(0f, 13f, 12f), Triple(8f, 10.5f, 11f), Triple(15.5f, 8f, 10f)).forEach { (sube, ancho, alto) ->
                    val y0 = b - 3 * d - sube * d
                    drawPath(Path().apply { moveTo(m.x - ancho * d, y0); lineTo(m.x, y0 - alto * d); lineTo(m.x + ancho * d, y0); close() }, verde)
                    drawPath(Path().apply { moveTo(m.x - ancho * d, y0); quadraticTo(m.x, y0 - 3 * d, m.x + ancho * d, y0) }, Color.White.copy(alpha = 0.5f), style = Stroke(0.8f * d))
                }
                listOf(Offset(-6f, -6f) to Color(0xFFB3122E), Offset(5f, -8f) to oro, Offset(-3f, -14f) to Color(0xFF1F3A9E), Offset(4f, -17f) to Color(0xFFB3122E), Offset(-1f, -23f) to oro)
                    .forEach { (o, col) -> drawCircle(col, 1.4f * d, Offset(m.x + o.x * d, b + o.y * d)) }
                with(TrofeosTema) { estrella(Offset(m.x, b - 30 * d), 4.4f * d, 1.8f * d, 5, Brush.linearGradient(listOf(oro, oro))) }
                listOf(Triple(-9f, Color(0xFFB3122E), oro), Triple(4.5f, Color(0xFF1F3A9E), Color.White)).forEach { (x, caja, cinta) ->
                    drawRect(caja, Offset(m.x + x * d, b - 3 * d), Size(5 * d, 4 * d))
                    drawLine(cinta, Offset(m.x + (x + 2.5f) * d, b - 3 * d), Offset(m.x + (x + 2.5f) * d, b + 1 * d), 0.8f * d)
                    drawLine(cinta, Offset(m.x + x * d, b - 1 * d), Offset(m.x + (x + 5) * d, b - 1 * d), 0.8f * d)
                }
            }
            TemaId.SEDA -> {
                // La llegada a Antioquía: dos farolillos rojos colgados de su vara, con sus costillas, tapas de oro y borlas.
                val top = m.y - 34 * d
                drawLine(cs.onSurface, m, Offset(m.x, top), 1.2f * d)
                drawLine(cs.onSurface, Offset(m.x, top), Offset(m.x + 19 * d, top), 1 * d)
                fun farol(x: Float, cuelga: Float, s: Float) {
                    val c = Offset(x, top + cuelga + s * 0.8f)
                    drawLine(cs.onSurface, Offset(x, top), Offset(x, c.y - s * 0.8f), 0.6f * d)
                    drawOval(Color(0xFFC0392B), Offset(c.x - s * 0.8f, c.y - s * 0.7f), Size(s * 1.6f, s * 1.4f))
                    drawOval(Color(0xFF8E1B1B), Offset(c.x - s * 0.4f, c.y - s * 0.7f), Size(s * 0.8f, s * 1.4f), style = Stroke(0.6f * d))
                    drawLine(Color(0xFF8E1B1B), Offset(c.x, c.y - s * 0.7f), Offset(c.x, c.y + s * 0.7f), 0.6f * d)
                    drawRect(oro, Offset(c.x - s * 0.45f, c.y - s * 0.85f), Size(s * 0.9f, s * 0.22f))
                    drawRect(oro, Offset(c.x - s * 0.45f, c.y + s * 0.63f), Size(s * 0.9f, s * 0.22f))
                    drawLine(oro, Offset(c.x, c.y + s * 0.85f), Offset(c.x, c.y + s * 1.5f), 1 * d)
                    drawCircle(Color(0xFFFFD27A).copy(alpha = 0.35f), s * 0.5f, c)
                }
                farol(m.x + 7 * d, 2 * d, 6 * d)
                farol(m.x + 16 * d, 6 * d, 4.6f * d)
            }
            TemaId.ANDALUS -> {
                // El arco de herradura: las dovelas rojas y blancas, el alfiz de oro que lo enmarca y sus dos columnas.
                val c = Offset(m.x, m.y - 26 * d); val R = 9 * d
                val dorado = Color(0xFFC9A04A)
                val pie = Offset(R * cos((PI / 4).toFloat()), R * sin((PI / 4).toFloat()))
                drawRect(dorado, Offset(c.x - R - 4 * d, c.y - R - 4 * d), Size(2 * R + 8 * d, R + pie.y + 4 * d), style = Stroke(1.2f * d))
                for (k in 0 until 9) drawArc(if (k % 2 == 0) Color(0xFFB0281E) else Color(0xFFF1E6C8), 135f + k * 30f, 30f, false, Offset(c.x - R, c.y - R), Size(2 * R, 2 * R), style = Stroke(4 * d))
                drawArc(dorado, 135f, 270f, false, Offset(c.x - R - 2 * d, c.y - R - 2 * d), Size(2 * R + 4 * d, 2 * R + 4 * d), style = Stroke(0.6f * d))
                listOf(-1f, 1f).forEach { s ->
                    val x = c.x + s * pie.x
                    drawRect(dorado, Offset(x - 2 * d, c.y + pie.y), Size(4 * d, 1.6f * d))
                    drawRect(Color(0xFFEDE3CF), Offset(x - 1 * d, c.y + pie.y + 1.6f * d), Size(2 * d, m.y - 9 * d - c.y - pie.y))
                }
                with(TrofeosMas) { estrellaLazo(Offset(c.x, c.y - R - 9 * d), 3 * d, Brush.linearGradient(listOf(dorado, dorado)), null, 0f) }
            }
            TemaId.MONGOL -> {
                // El ovoo de la llegada: el montón de piedras, la vara en medio con su punta de tridente y las bufandas azules
                // que ondean al viento.
                val piedra = Color(0xFF8A857A); val b = m.y - 7 * d
                listOf(Triple(-7f, 0f, 4f), Triple(0f, 0.5f, 4.4f), Triple(7f, 0f, 4f), Triple(-3.5f, -5f, 3.8f), Triple(3.5f, -5f, 3.8f), Triple(0f, -9.5f, 3.4f)).forEachIndexed { i, (x, y, r) ->
                    drawOval(lerp(piedra, Color.Black, (i % 3) * 0.1f), Offset(m.x + (x - r) * d, b + (y - r * 0.75f) * d), Size(2 * r * d, 1.5f * r * d))
                }
                val top = b - 32 * d
                drawLine(Color(0xFF5A3A22), Offset(m.x, b - 10 * d), Offset(m.x, top), 1.2f * d)
                listOf(-2.5f, 0f, 2.5f).forEach { x -> drawLine(oro, Offset(m.x + x * d, top), Offset(m.x + x * d, top - 3.5f * d), 0.9f * d) }
                drawLine(oro, Offset(m.x - 2.5f * d, top), Offset(m.x + 2.5f * d, top), 0.9f * d)
                listOf(Triple(4f, Color(0xFF2E6DB4), 18f), Triple(10f, Color(0xFF6FA8DC), 14f), Triple(16f, Color(0xFF2E6DB4), 10f)).forEach { (baja, col, largo) ->
                    val y = top + baja * d
                    drawPath(Path().apply {
                        moveTo(m.x, y); cubicTo(m.x + largo * 0.4f * d, y - 3 * d, m.x + largo * 0.6f * d, y + 3 * d, m.x + largo * d, y - 1 * d)
                        lineTo(m.x + largo * d, y + 2 * d); cubicTo(m.x + largo * 0.6f * d, y + 6 * d, m.x + largo * 0.4f * d, y, m.x, y + 3 * d); close()
                    }, col)
                }
            }
            TemaId.ANTIGUAS -> {
                // La almenara encendida en lo alto de la fortaleza: la torre de piedra con sus hiladas, el brasero y la hoguera
                // que avisa la llegada de la compañía.
                val piedra = Color(0xFF7A7468); val b = m.y - 8 * d
                drawPath(Path().apply { moveTo(m.x - 7 * d, b); lineTo(m.x + 7 * d, b); lineTo(m.x + 5 * d, b - 16 * d); lineTo(m.x - 5 * d, b - 16 * d); close() }, piedra)
                for (k in 1 until 4) { val y = b - k * 4 * d; val mx = 7 * d - k * 0.5f * d; drawLine(lerp(piedra, Color.Black, 0.35f), Offset(m.x - mx, y), Offset(m.x + mx, y), 0.6f * d) }
                for (k in 0 until 4) { val y = b - (k * 4 + 2) * d; val x = m.x + (if (k % 2 == 0) -1.5f else 2f) * d; drawLine(lerp(piedra, Color.Black, 0.35f), Offset(x, y - 2 * d), Offset(x, y + 2 * d), 0.6f * d) }
                drawRect(Color(0xFF2A2420), Offset(m.x - 6.5f * d, b - 18.5f * d), Size(13 * d, 2.5f * d))
                val fuego = Offset(m.x, b - 24 * d)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.5f), Color.Transparent), fuego, 14 * d), 14 * d, fuego)
                listOf(Triple(-3.5f, 9f, Color(0xFFE8601C)), Triple(3.5f, 8f, Color(0xFFE8601C)), Triple(0f, 13f, Color(0xFFFFB347)), Triple(0f, 7f, Color(0xFFFFF1B8))).forEach { (x, alto, col) ->
                    val bx = m.x + x * d; val by = b - 18.5f * d
                    drawPath(Path().apply { moveTo(bx, by - alto * d); quadraticTo(bx + 3.4f * d, by - alto * 0.4f * d, bx, by); quadraticTo(bx - 3.4f * d, by - alto * 0.4f * d, bx, by - alto * d); close() }, col)
                }
                listOf(Offset(-6f, -36f), Offset(5f, -38f), Offset(1f, -42f)).forEach { o -> drawCircle(Color(0xFFFFB347), 0.8f * d, Offset(m.x + o.x * d, b + o.y * d)) }
            }
            TemaId.DISTOPIA -> with(TrofeosMas) {
                // El sello de «APROBADO» estampado sobre el Ministerio, un poco torcido, con su doble filete.
                val rojo = Color(0xFFC8102E)
                val c = Offset(m.x.coerceIn(26 * d, size.width - 26 * d), m.y - 28 * d)
                rotate(-8f, c) {
                    drawRect(rojo, Offset(c.x - 22 * d, c.y - 8 * d), Size(44 * d, 16 * d), style = Stroke(1.8f * d))
                    drawRect(rojo, Offset(c.x - 19.5f * d, c.y - 5.5f * d), Size(39 * d, 11 * d), style = Stroke(0.7f * d))
                    mono("APROBADO", c.x, c.y + 2.6f * d, 7.5f * d, rojo, android.graphics.Paint.Align.CENTER, negrita = true)
                }
            }
            // «ACCESO CONCEDIDO» en un recuadro de consola sobre la raíz.
            TemaId.CIBER -> with(TrofeosMas) {
                val cian = Color(0xFF00E5FF); val t = "ACCESO CONCEDIDO"; val tam = 8 * d
                val tw = t.length * tam * 0.6f; val x = (m.x - tw / 2).coerceIn(3 * d, size.width - tw - 3 * d); val y = m.y - 30 * d
                drawRect(Color(0xFF05070B), Offset(x - 3 * d, y - tam), Size(tw + 6 * d, tam * 1.5f))
                drawRect(cian, Offset(x - 3 * d, y - tam), Size(tw + 6 * d, tam * 1.5f), style = Stroke(1f * d))
                mono(t, x, y, tam, cian, negrita = true)
            }
            // Al fondo de la bajada, el puesto se enciende: el letrero «ABIERTO» con su halo, la cortina y dos farolitos.
            TemaId.NEON -> with(TrofeosMas) {
                val magenta = Color(0xFFFF3D9A); val ambar = Color(0xFFFFB238); val t = "ABIERTO · 営業中"; val tam = 8 * d
                val tw = t.length * tam * 0.62f; val x = (m.x - tw / 2).coerceIn(3 * d, size.width - tw - 3 * d); val y = m.y - 32 * d
                drawRoundRect(magenta.copy(alpha = 0.22f), Offset(x - 6 * d, y - tam - 3 * d), Size(tw + 12 * d, tam * 1.5f + 6 * d), CornerRadius(8 * d), style = Stroke(5 * d))
                drawRoundRect(Color(0xFF0C0811), Offset(x - 4 * d, y - tam - 1 * d), Size(tw + 8 * d, tam * 1.5f + 2 * d), CornerRadius(6 * d))
                drawRoundRect(magenta, Offset(x - 4 * d, y - tam - 1 * d), Size(tw + 8 * d, tam * 1.5f + 2 * d), CornerRadius(6 * d), style = Stroke(1.4f * d))
                mono(t, x, y, tam, ambar, negrita = true)
                for (k in 0 until 4) drawRect(Color(0xFFFF6B5E).copy(alpha = 0.8f), Offset(m.x - 14 * d + k * 7 * d, m.y - 12 * d), Size(5 * d, 9 * d))
                listOf(-20f, 20f).forEach { dx ->
                    drawCircle(Brush.radialGradient(listOf(Color(0xFFFF6B5E).copy(alpha = 0.4f), Color.Transparent), Offset(m.x + dx * d, m.y - 16 * d), 9 * d), 9 * d, Offset(m.x + dx * d, m.y - 16 * d))
                    drawOval(Color(0xFFFF6B5E), Offset(m.x + dx * d - 3 * d, m.y - 20 * d), Size(6 * d, 8 * d))
                }
            }
            // La salida a la superficie: el sol que asoma sobre la compuerta abierta.
            TemaId.REFUGIO -> {
                val sol = Offset(m.x, m.y - 18 * d)
                for (k in 0 until 9) { val a = PI + k * PI / 8; drawLine(Color(0xFFE8B83A), Offset(sol.x + (10 * d * cos(a)).toFloat(), sol.y + (10 * d * sin(a)).toFloat()), Offset(sol.x + (16 * d * cos(a)).toFloat(), sol.y + (16 * d * sin(a)).toFloat()), 1.6f * d) }
                drawArc(Color(0xFFE8B83A), 180f, 180f, true, Offset(sol.x - 8 * d, sol.y - 8 * d), Size(16 * d, 16 * d))
                drawLine(Color(0xFF6B8A3A), Offset(sol.x - 18 * d, sol.y), Offset(sol.x + 18 * d, sol.y), 2 * d)
            }
            // La fortaleza tomada: la torre almenada con el estandarte carmesí y el engrane de latón.
            TemaId.ORDEN -> {
                drawRect(Color(0xFF6E757D), Offset(m.x - 9 * d, m.y - 22 * d), Size(18 * d, 12 * d))
                for (k in 0 until 3) drawRect(Color(0xFF6E757D), Offset(m.x - 9 * d + k * 7 * d, m.y - 26 * d), Size(4 * d, 4 * d))
                drawLine(cs.onSurface, Offset(m.x, m.y - 26 * d), Offset(m.x, m.y - 44 * d), 1.2f * d)
                drawPath(Path().apply { moveTo(m.x, m.y - 44 * d); lineTo(m.x + 16 * d, m.y - 44 * d); lineTo(m.x + 16 * d, m.y - 32 * d); lineTo(m.x + 8 * d, m.y - 35 * d); lineTo(m.x, m.y - 32 * d); close() }, Color(0xFF9E1B22))
                drawCircle(Color(0xFFD6B25A), 2.6f * d, Offset(m.x + 8 * d, m.y - 39.5f * d))
            }
            // El cultivo completo: las colonias que llenan la placa de Petri.
            TemaId.LABORATORIO -> {
                val az = java.util.Random(13)
                val c = m; val R = minOf(size.height * 0.32f, size.width * 0.08f)
                repeat(26) { val a = az.nextDouble() * 2 * PI; val rr = R * kotlin.math.sqrt(az.nextFloat())
                    drawCircle(listOf(Color(0xFF0B8FA1), Color(0xFF5E4FD1), Color(0xFF1E8E5A))[it % 3].copy(alpha = 0.8f), (1.5f + az.nextFloat() * 2.5f) * d, Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat())) }
            }
            // El aterrizaje en Marte: el módulo con sus patas y la bandera naranja.
            TemaId.ESPACIO -> {
                val b = Offset(m.x, m.y - 12 * d)
                drawPath(Path().apply { moveTo(b.x - 7 * d, b.y); lineTo(b.x + 7 * d, b.y); lineTo(b.x + 5 * d, b.y - 8 * d); lineTo(b.x - 5 * d, b.y - 8 * d); close() }, Color(0xFFC9A04A))
                listOf(-1f, 1f).forEach { s -> drawLine(cs.onSurface, Offset(b.x + s * 5 * d, b.y), Offset(b.x + s * 10 * d, b.y + 8 * d), 1.2f * d) }
                drawLine(cs.onSurface, Offset(b.x + 14 * d, b.y + 8 * d), Offset(b.x + 14 * d, b.y - 14 * d), 1f * d)
                drawRect(Color(0xFFE8601C), Offset(b.x + 14 * d, b.y - 14 * d), Size(10 * d, 6 * d))
            }
            // El fondo de la fosa: el batiscafo posado, el haz de su foco sobre el fango y una medusa que lo ilumina desde arriba.
            TemaId.ABISMO -> {
                val b = Offset(m.x, m.y - 10 * d)
                val laton = Color(0xFFB8913E)
                drawPath(Path().apply { moveTo(b.x + 4 * d, b.y + 2 * d); lineTo(b.x + 26 * d, b.y + 12 * d); lineTo(b.x + 10 * d, b.y + 12 * d); close() }, Color(0xFFFFF2B0).copy(alpha = 0.35f))
                drawRoundRect(laton, Offset(b.x - 13 * d, b.y - 12 * d), Size(26 * d, 8 * d), androidx.compose.ui.geometry.CornerRadius(4 * d))
                drawRect(laton, Offset(b.x - 3 * d, b.y - 16 * d), Size(6 * d, 4 * d))
                drawCircle(laton, 5 * d, Offset(b.x, b.y + 1 * d))
                drawCircle(Color(0xFF5CF2C6), 1.8f * d, Offset(b.x - 1 * d, b.y + 0.5f * d))
                val me = Offset(b.x - 16 * d, b.y - 26 * d)
                drawCircle(Color(0xFFE29BFF).copy(alpha = 0.2f), 9 * d, me)
                drawArc(Color(0xFFE29BFF), 180f, 180f, true, Offset(me.x - 5 * d, me.y - 4 * d), Size(10 * d, 8 * d))
                listOf(-3f, 0f, 3f).forEach { dx -> drawLine(Color(0xFFE29BFF).copy(alpha = 0.8f), Offset(me.x + dx * d, me.y), Offset(me.x + dx * 0.6f * d, me.y + 7 * d), 0.8f * d) }
            }
            // La gran estrella roja de la llegada a Vladivostok.
            TemaId.URSS -> with(TrofeosTema) {
                val c = Offset(m.x, m.y - 24 * d)
                estrella(c, 15 * d, 6 * d, 5, Brush.linearGradient(listOf(Color(0xFFD4A017), Color(0xFFD4A017))))
                estrella(c, 12 * d, 4.8f * d, 5, Brush.linearGradient(listOf(Color(0xFFE5252F), Color(0xFFC1121F)), Offset(c.x, c.y - 12 * d), Offset(c.x, c.y + 12 * d)))
            }
        }
    }

    /** Una hoja (de laurel, de tallo) centrada en [o], a lo largo del ángulo [g], de [largo] y medio ancho [ancho]. */
    private fun DrawScope.hoja(o: Offset, g: Float, largo: Float, ancho: Float, col: Color) {
        val ux = cos(g); val uy = sin(g)
        drawPath(Path().apply {
            moveTo(o.x - ux * largo / 2, o.y - uy * largo / 2)
            quadraticTo(o.x - uy * ancho, o.y + ux * ancho, o.x + ux * largo / 2, o.y + uy * largo / 2)
            quadraticTo(o.x + uy * ancho, o.y - ux * ancho, o.x - ux * largo / 2, o.y - uy * largo / 2); close()
        }, col)
    }

    /** Una flor de lis que nace en [o] y apunta hacia el ángulo [g], de tamaño [s]. */
    private fun DrawScope.lis(o: Offset, g: Float, s: Float, col: Color) {
        val ux = cos(g); val uy = sin(g); val nx = -uy; val ny = ux
        drawPath(Path().apply {
            moveTo(o.x - ux * s * 0.2f, o.y - uy * s * 0.2f)
            quadraticTo(o.x + ux * s * 0.6f + nx * s * 0.5f, o.y + uy * s * 0.6f + ny * s * 0.5f, o.x + ux * s * 1.2f, o.y + uy * s * 1.2f)
            quadraticTo(o.x + ux * s * 0.6f - nx * s * 0.5f, o.y + uy * s * 0.6f - ny * s * 0.5f, o.x - ux * s * 0.2f, o.y - uy * s * 0.2f); close()
        }, col)
        listOf(-1f, 1f).forEach { l ->
            drawPath(Path().apply { moveTo(o.x, o.y); quadraticTo(o.x + ux * s * 0.3f + nx * l * s * 1.1f, o.y + uy * s * 0.3f + ny * l * s * 1.1f, o.x - ux * s * 0.4f + nx * l * s * 0.9f, o.y - uy * s * 0.4f + ny * l * s * 0.9f) }, col, style = Stroke(s * 0.35f))
        }
    }
}
