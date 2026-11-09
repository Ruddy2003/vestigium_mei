package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Revolución francesa: Vida en la sección y en el club, con el papel de cartel, la tinta de imprenta, el pino, el nogal,
 * el yeso, las baldosas de barro, la piedra del Panteón y la escarapela azul, blanca y roja de su Ideario (el estante de la
 * sección con sus colgaduras), sus estantes (las cajas de los libreros del muelle) y su salón de logros (la imprenta).
 * - Social: el banquete fraterno en la vista de los juegos de mesa (la pared de frente, el piso desde arriba). La pared de
 *   yeso con su lambrín, las colgaduras tricolores con sus escarapelas, dos carteles pegados y, al centro, la escarapela
 *   grande entre dos banderas cruzadas; en el piso de baldosas hexagonales, la mesa con el mantel de lino (el nicho) y su
 *   orla tricolor, las sillas de anea, el árbol de la libertad en su cajón con el gorro frigio, el tambor con sus
 *   baquetas, el cesto del pan y el fajo de pasquines atado con su cinta.
 * - Mapas: el bufete del comité, de nogal. El tablero con su moldura de bronce dorado, el cuero verde con su filete de oro y
 *   el mapa (el nicho); la escribanía con sus dos frascos y la pluma, el compás, el candelero (encendido de noche), el
 *   reloj decimal de diez horas, el gorro frigio con su escarapela, el decreto enrollado con su cinta y la regla del
 *   metro; el friso acanalado, los cajones con sus anillas y las patas estriadas sobre las baldosas.
 * - Otras vidas: las hornacinas del Panteón. El frontón con la escarapela y el gorro frigio, la cornisa de dentículos, el
 *   friso con cinco coronas de laurel y los festones tricolores, cinco hornacinas de piedra (el nicho, partido en cinco)
 *   entre pilastras estriadas y el zócalo de sillares con sus placas de bronce.
 * - Íconos: el Panteón con su cúpula, el mapa de los departamentos que un sou de cobre rasca, las escarapelas clavadas en
 *   el plano de la ciudad.
 * - Notas: el tablón de la sección en el muro de la calle. El marco de madera verde con sus escuadras de hierro, el
 *   remate con la escarapela entre banderas y dos faroles de calle, el yeso (el nicho) y la cornisa de piedra con el cubo
 *   del engrudo y la brocha del pegador de carteles. Las notas son pasquín, hoja del cuaderno de quejas, decreto y hoja
 *   del calendario republicano, pegadas con engrudo y un clavo; las fijadas, con una escarapela y el sello de lacre.
 */
internal object VidaRevolucion : DisenoVida {
    private val PINO = Color(0xFFB08A5A)
    private val PINO_LUZ = Color(0xFFCBA676)
    private val PINO_OSC = Color(0xFF7A5A36)
    private val NOGAL = Color(0xFF6A4428)
    private val NOGAL_LUZ = Color(0xFF8C5E3A)
    private val NOGAL_OSC = Color(0xFF3A2414)
    private val VETA = Color(0xFF24160C)
    private val AZUL = Color(0xFF1F3A6E)
    private val AZUL_LUZ = Color(0xFF34548E)
    private val ROJO = Color(0xFFB22234)
    private val ROJO_OSC = Color(0xFF761522)
    private val BLANCO = Color(0xFFF4F0E6)
    private val PAPEL = Color(0xFFEFE8D6)
    private val PAPEL_VIEJO = Color(0xFFD9CBA6)
    private val NEGRO = Color(0xFF1C1A17)
    private val YESO = Color(0xFFE3D9C2)
    private val YESO_OSC = Color(0xFFC9BC9E)
    private val LAMBRIN = Color(0xFFB9BDB4)
    private val TOMETTE = Color(0xFFA8442A)
    private val TOMETTE_OSC = Color(0xFF6E2A18)
    private val JUNTA = Color(0xFF4A2418)
    private val HIERRO = Color(0xFF2A2826)
    private val HIERRO_LUZ = Color(0xFF5E6266)
    private val VERDE = Color(0xFF3F6B3A)
    private val VERDE_LUZ = Color(0xFF6E9A55)
    private val VERDE_OSC = Color(0xFF24401F)
    private val VERDE_TABLON = Color(0xFF2F4A34)
    private val VERDE_TABLON_LUZ = Color(0xFF4A6A4E)
    private val CUERO_VERDE = Color(0xFF2E5236)
    private val PIEDRA = Color(0xFFC8BFA8)
    private val PIEDRA_LUZ = Color(0xFFE2DACA)
    private val PIEDRA_JUNTA = Color(0xFF948B76)
    private val ORO = Color(0xFFC9A04E)
    private val ORO_LUZ = Color(0xFFEBD18A)
    private val ORO_OSC = Color(0xFF8A6A2C)
    private val COBRE = Color(0xFFB06A3B)
    private val COBRE_LUZ = Color(0xFFDC9A68)
    private val COBRE_OSC = Color(0xFF7A4224)
    private val ANEA = Color(0xFFC9A860)
    private val ANEA_OSC = Color(0xFF8E7034)
    private val PAN = Color(0xFFC88A44)
    private val LLAMA = Color(0xFFF7C45A)
    private val LLAMA_CENTRO = Color(0xFFFFF2C4)
    private val CIELO = Color(0xFFC9D3D6)
    private val NOCHE = Color(0xFF0B0D0C)

    /** Los papeles de las notas, de día y a la luz de las antorchas. */
    private val P_PASQUIN = Color(0xFFF3EDDD)
    private val P_QUEJAS = Color(0xFFEDE6D2)
    private val P_DECRETO = Color(0xFFF6F1E4)
    private val P_CALENDARIO = Color(0xFFE9E1CA)
    private val P_PASQUIN_N = Color(0xFF34322D)
    private val P_QUEJAS_N = Color(0xFF33302A)
    private val P_DECRETO_N = Color(0xFF2E2D2B)
    private val P_CALENDARIO_N = Color(0xFF36322A)

    // ---------------------------------------------------------------------
    //  Ayudas
    // ---------------------------------------------------------------------

    private fun DrawScope.u() = size.minDimension / 100f

    private fun mezcla(a: Color, b: Color, f: Float) = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en modo oscuro: la sala a la luz de las antorchas, como sus estantes. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, NOCHE, 0.36f) else c

    /** El bronce dorado se hunde menos: brilla a la luz de las velas. */
    private fun tl(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, NOCHE, 0.18f) else c

    /** La piedra y el yeso, de noche más hondos. */
    private fun n(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, NOCHE, 0.56f) else c

    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    private fun DrawScope.sombraSuave(r: Rect, radio: Float, alcance: Float, fuerza: Float = 0.22f) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.6f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(radio + e))
        }
    }

    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.5f)), r.bottom - ancho, r.bottom), Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.7f), Color.Transparent), r.left, r.left + ancho), r.topLeft, Size(ancho, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.7f)), r.right - ancho, r.right), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    private fun DrawScope.halo(c: Offset, r: Float, color: Color, fuerza: Float) =
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = fuerza), color.copy(alpha = fuerza * 0.35f), Color.Transparent), c, r.coerceAtLeast(1f)), r, c)

    private fun DrawScope.clavo(p: Offset, r: Float, oscuro: Boolean, cuerpo: Color = HIERRO_LUZ) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(p.x + r * 0.3f, p.y + r * 0.35f))
        drawCircle(tl(cuerpo, oscuro), r, p)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.2f else 0.4f), r * 0.42f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
    }

    /** Vetas de madera en [r]: líneas onduladas a lo largo. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, u: Float, vertical: Boolean = false, paso: Float = 1.3f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / (paso * u)).toInt().coerceIn(2, 60)
        for (k in 0 until n) {
            val base = ancho * (k + azar(semilla, k)) / n
            val amp = (0.2f + 0.6f * azar(semilla, k + 100)) * u
            val fase = azar(semilla, k + 200) * 6.28f
            val ondas = 1f + 2f * azar(semilla, k + 300)
            val p = Path()
            for (s in 0..14) {
                val q = largo * s / 14f
                val v = base + amp * sin(fase + ondas * 6.28f * s / 14f)
                val x = if (vertical) r.left + v else r.left + q
                val y = if (vertical) r.top + q else r.top + v
                if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(u * (0.15f + 0.25f * azar(semilla, k + 500))))
        }
    }

    /** Una tabla de madera en [r], con su veta y su bisel. */
    private fun DrawScope.tabla(r: Rect, oscuro: Boolean, u: Float, semilla: Int, vertical: Boolean = false, tono: Color = NOGAL) {
        if (r.width <= 0f || r.height <= 0f) return
        drawRect(t(tono, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, t(VETA, oscuro).copy(alpha = 0.4f), semilla, u, vertical) }
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), r.topLeft, if (vertical) Size(0.4f * u, r.height) else Size(r.width, 0.4f * u))
        if (vertical) drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.right - 0.4f * u, r.top), Size(0.4f * u, r.height))
        else drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left, r.bottom - 0.4f * u), Size(r.width, 0.4f * u))
    }

    /** Una moldura de bronce dorado horizontal en [r]. */
    private fun DrawScope.oro(r: Rect, oscuro: Boolean) =
        drawRect(Brush.verticalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO, oscuro), tl(ORO_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)

    /** Un hexágono de lado [l] con un lado arriba, achatado en vertical por [ay]. */
    private fun hexagono(c: Offset, l: Float, ay: Float = 1f): Path = Path().apply {
        for (k in 0 until 6) {
            val a = Math.toRadians(60.0 * k)
            val p = Offset(c.x + l * cos(a).toFloat(), c.y + l * sin(a).toFloat() * ay)
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /** El piso de baldosas hexagonales de barro (tomettes), con su junta y su tono que varía. */
    private fun DrawScope.baldosas(r: Rect, oscuro: Boolean, u: Float, semilla: Int, lado: Float, ay: Float = 1f) {
        drawRect(n(JUNTA, oscuro), r.topLeft, r.size)
        val dx = lado * 1.5f
        val dy = lado * sqrt(3f) * ay
        clipRect(r.left, r.top, r.right, r.bottom) {
            var i = 0
            var x = r.left - lado
            while (x < r.right + lado) {
                var y = r.top - dy + (if (i % 2 == 0) 0f else dy / 2)
                var j = 0
                while (y < r.bottom + dy) {
                    val c = Offset(x, y)
                    val f = azar(semilla, i * 97 + j)
                    drawPath(hexagono(c, lado * 0.9f, ay), n(mezcla(mezcla(TOMETTE, NOGAL_OSC, 0.22f), TOMETTE_OSC, 0.45f * f), oscuro))
                    drawPath(hexagono(Offset(c.x - lado * 0.08f, c.y - lado * 0.08f * ay), lado * 0.55f, ay), Color.White.copy(alpha = 0.05f + 0.04f * f))
                    y += dy; j++
                }
                x += dx; i++
            }
        }
    }

    /** Una pared de yeso con su lambrín de cuarterones y la moldura del zócalo. */
    private fun DrawScope.paredYeso(r: Rect, oscuro: Boolean, u: Float, lambrin: Float) {
        drawRect(n(YESO, oscuro), r.topLeft, r.size)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent), r.top, r.top + r.height * 0.5f), r.topLeft, Size(r.width, r.height * 0.5f))
        if (lambrin <= 0f) return
        val l = Rect(r.left, r.bottom - lambrin, r.right, r.bottom)
        drawRect(n(LAMBRIN, oscuro), l.topLeft, l.size)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), n(LAMBRIN, oscuro), Color.Black.copy(alpha = 0.2f)), l.top - 0.8f * u, l.top + 0.8f * u), Offset(l.left, l.top - 0.8f * u), Size(l.width, 1.6f * u))
        val paso = maxOf(l.height * 1.6f, 8f * u)
        var x = l.left + 1.4f * u
        while (x < l.right - 2f * u) {
            val p = Rect(x, l.top + l.height * 0.22f, minOf(x + paso - 2.4f * u, l.right - 1.4f * u), l.bottom - l.height * 0.2f)
            drawRect(Color.Black.copy(alpha = 0.18f), p.topLeft, p.size, style = Stroke(0.35f * u))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.35f), Offset(p.left + 0.35f * u, p.top + 0.35f * u), p.size, style = Stroke(0.25f * u))
            x += paso
        }
    }

    /** Una escarapela: el plisado azul afuera, el blanco y el botón rojo al centro; con [cintas], sus dos cintas colgando. */
    private fun DrawScope.escarapela(c: Offset, r: Float, oscuro: Boolean, cintas: Boolean = false) {
        if (cintas) {
            listOf(-1f, 1f).forEach { s ->
                val cinta = Path().apply {
                    moveTo(c.x + s * r * 0.15f, c.y); lineTo(c.x + s * r * 0.6f, c.y + r * 1.9f); lineTo(c.x + s * r * 0.4f, c.y + r * 1.7f)
                    lineTo(c.x + s * r * 0.22f, c.y + r * 1.95f); lineTo(c.x - s * r * 0.15f, c.y + r * 0.2f); close()
                }
                drawPath(cinta, t(if (s < 0) AZUL else ROJO, oscuro))
                drawPath(cinta, Color.Black.copy(alpha = 0.2f), style = Stroke(r * 0.04f))
            }
        }
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + r * 0.1f, c.y + r * 0.14f))
        drawCircle(t(AZUL, oscuro), r, c)
        drawCircle(t(BLANCO, oscuro), r * 0.66f, c)
        drawCircle(t(ROJO, oscuro), r * 0.36f, c)
        for (k in 0 until 20) {
            val a = k * Math.PI / 10
            drawLine(Color.Black.copy(alpha = 0.16f), Offset(c.x + r * 0.4f * cos(a).toFloat(), c.y + r * 0.4f * sin(a).toFloat()), Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), r * 0.05f)
        }
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.12f else 0.3f), r * 0.14f, Offset(c.x - r * 0.12f, c.y - r * 0.12f))
    }

    /** Una colgadura tricolor de [x0] a [x1] colgada en [y], de caída [caida]: azul arriba, blanco y rojo, con sus pliegues. */
    private fun DrawScope.colgadura(x0: Float, x1: Float, y: Float, caida: Float, oscuro: Boolean, u: Float) {
        fun banda(prof: Float, color: Color) {
            val p = Path().apply { moveTo(x0, y); lineTo(x1, y); quadraticTo((x0 + x1) / 2, y + prof * 2f, x0, y); close() }
            drawPath(p, color)
        }
        val sombra = Path().apply { moveTo(x0, y); lineTo(x1, y); quadraticTo((x0 + x1) / 2, y + caida * 2f + 1.6f * u, x0, y); close() }
        drawPath(sombra, Color.Black.copy(alpha = 0.18f))
        banda(caida, t(ROJO, oscuro))
        banda(caida * 0.74f, t(BLANCO, oscuro))
        banda(caida * 0.48f, t(AZUL, oscuro))
        // Los pliegues.
        for (k in 1 until 6) {
            val f = k / 6f
            val x = x0 + (x1 - x0) * f
            val prof = caida * 2f * (1 - (2 * f - 1) * (2 * f - 1)) * 0.5f
            drawLine(Color.Black.copy(alpha = 0.12f), Offset(x, y), Offset(x + (f - 0.5f) * 1.2f * u, y + prof * 0.96f), 0.3f * u)
        }
    }

    /** Una bandera tricolor en su asta, de [pie] a la punta, girada [giro] grados. */
    private fun DrawScope.bandera(pie: Offset, largo: Float, giro: Float, oscuro: Boolean, u: Float) {
        rotate(giro, pie) {
            val punta = Offset(pie.x, pie.y - largo)
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(pie.x + 0.4f * u, pie.y + 0.4f * u), Offset(punta.x + 0.4f * u, punta.y + 0.4f * u), 0.8f * u)
            drawLine(t(NOGAL_LUZ, oscuro), pie, punta, 0.7f * u)
            val fw = largo * 0.62f; val fh = largo * 0.42f
            val top = punta.y + 1.4f * u
            val tela = { x0: Float, x1: Float, col: Color ->
                val p = Path().apply {
                    moveTo(pie.x + x0 * fw, top + 0.6f * u * sin(x0 * 6f)); lineTo(pie.x + x1 * fw, top + 0.6f * u * sin(x1 * 6f))
                    lineTo(pie.x + x1 * fw, top + fh + 0.6f * u * sin(x1 * 6f)); lineTo(pie.x + x0 * fw, top + fh + 0.6f * u * sin(x0 * 6f)); close()
                }
                drawPath(p, t(col, oscuro))
            }
            tela(0f, 0.34f, AZUL); tela(0.33f, 0.67f, BLANCO); tela(0.66f, 1f, ROJO)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f), Color.Transparent, Color.Black.copy(alpha = 0.12f)), pie.x, pie.x + fw), Offset(pie.x, top - 0.8f * u), Size(fw, fh + 1.6f * u))
            drawPath(Path().apply { moveTo(punta.x - 1f * u, punta.y + 1.2f * u); lineTo(punta.x, punta.y - 1.4f * u); lineTo(punta.x + 1f * u, punta.y + 1.2f * u); close() }, tl(ORO, oscuro))
        }
    }

    /** El gorro frigio de lana roja, de perfil, con su escarapela; [r] es su alto. */
    private fun DrawScope.gorroFrigio(c: Offset, r: Float, oscuro: Boolean) {
        val gorro = Path().apply {
            moveTo(c.x - r * 0.5f, c.y + r * 0.42f)
            cubicTo(c.x - r * 0.62f, c.y - r * 0.1f, c.x - r * 0.3f, c.y - r * 0.6f, c.x + r * 0.12f, c.y - r * 0.52f)
            cubicTo(c.x + r * 0.5f, c.y - r * 0.46f, c.x + r * 0.62f, c.y - r * 0.2f, c.x + r * 0.42f, c.y - r * 0.02f)
            cubicTo(c.x + r * 0.36f, c.y - r * 0.2f, c.x + r * 0.2f, c.y - r * 0.24f, c.x + r * 0.14f, c.y - r * 0.08f)
            cubicTo(c.x + r * 0.34f, c.y + r * 0.1f, c.x + r * 0.46f, c.y + r * 0.24f, c.x + r * 0.5f, c.y + r * 0.42f)
            close()
        }
        drawPath(Path().apply { addPath(gorro, Offset(r * 0.05f, r * 0.07f)) }, Color.Black.copy(alpha = 0.3f))
        drawPath(gorro, Brush.linearGradient(listOf(t(mezcla(ROJO, Color.White, 0.15f), oscuro), t(ROJO, oscuro), t(ROJO_OSC, oscuro)), Offset(c.x - r * 0.5f, c.y - r * 0.5f), Offset(c.x + r * 0.5f, c.y + r * 0.4f)))
        drawPath(Path().apply { moveTo(c.x - r * 0.3f, c.y - r * 0.2f); quadraticTo(c.x, c.y - r * 0.44f, c.x + r * 0.3f, c.y - r * 0.32f) }, Color.Black.copy(alpha = 0.2f), style = Stroke(r * 0.04f))
        drawRoundRect(t(ROJO_OSC, oscuro), Offset(c.x - r * 0.54f, c.y + r * 0.3f), Size(r * 1.08f, r * 0.16f), CornerRadius(r * 0.06f))
        escarapela(Offset(c.x + r * 0.2f, c.y + r * 0.2f), r * 0.15f, oscuro)
    }

    /** Una corona de laurel: dos ramas de hojas que suben desde abajo y se cierran con un lazo. */
    private fun DrawScope.laurel(c: Offset, r: Float, oscuro: Boolean, hoja: Color, cinta: Color) {
        val n = 9
        listOf(-1f, 1f).forEach { s ->
            for (k in 0 until n) {
                val grados = if (s < 0) 105.0 + k * 140.0 / n else 75.0 - k * 140.0 / n
                val a = Math.toRadians(grados)
                val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
                val tam = 1f - 0.35f * k / n
                rotate(grados.toFloat() + s * 22f, p) {
                    drawOval(Color.Black.copy(alpha = 0.18f), Offset(p.x - r * 0.11f * tam + r * 0.03f, p.y - r * 0.24f * tam + r * 0.04f), Size(r * 0.22f * tam, r * 0.48f * tam))
                    drawOval(hoja, Offset(p.x - r * 0.11f * tam, p.y - r * 0.24f * tam), Size(r * 0.22f * tam, r * 0.48f * tam))
                    drawLine(Color.Black.copy(alpha = 0.22f), Offset(p.x, p.y - r * 0.2f * tam), Offset(p.x, p.y + r * 0.2f * tam), r * 0.03f)
                }
            }
        }
        drawCircle(cinta, r * 0.14f, Offset(c.x, c.y + r))
        listOf(-1f, 1f).forEach { s -> drawLine(cinta, Offset(c.x, c.y + r), Offset(c.x + s * r * 0.35f, c.y + r * 1.4f), r * 0.1f, StrokeCap.Round) }
    }

    /** Una pilastra de piedra estriada en [r], con basa y capitel. */
    private fun DrawScope.pilastra(r: Rect, oscuro: Boolean, u: Float) {
        val piedra = n(PIEDRA, oscuro); val luz = n(PIEDRA_LUZ, oscuro); val sombra = mezcla(piedra, Color.Black, 0.25f)
        drawRect(Brush.horizontalGradient(listOf(luz, piedra, sombra), r.left, r.right), r.topLeft, r.size)
        val cap = minOf(r.width * 0.5f, 3f * u)
        val estrias = ((r.width - 1f * u) / (1.1f * u)).toInt().coerceIn(1, 8)
        for (k in 1..estrias) {
            val x = r.left + r.width * k / (estrias + 1f)
            drawLine(Color.Black.copy(alpha = 0.18f), Offset(x, r.top + cap * 1.6f), Offset(x, r.bottom - cap * 1.4f), 0.35f * u, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.3f), Offset(x + 0.3f * u, r.top + cap * 1.6f), Offset(x + 0.3f * u, r.bottom - cap * 1.4f), 0.2f * u)
        }
        // El capitel con sus volutas y la basa con su toro.
        val capR = Rect(r.left - 0.8f * u, r.top, r.right + 0.8f * u, r.top + cap)
        drawRect(Brush.verticalGradient(listOf(luz, sombra), capR.top, capR.bottom), capR.topLeft, capR.size)
        listOf(capR.left + cap * 0.35f, capR.right - cap * 0.35f).forEach { x -> drawCircle(sombra, cap * 0.32f, Offset(x, capR.bottom), style = Stroke(0.3f * u)) }
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, capR.bottom), Size(r.width, 0.4f * u))
        val basR = Rect(r.left - 0.8f * u, r.bottom - cap * 1.1f, r.right + 0.8f * u, r.bottom)
        drawRect(Brush.verticalGradient(listOf(luz, piedra, sombra), basR.top, basR.bottom), basR.topLeft, basR.size)
        drawLine(Color.Black.copy(alpha = 0.2f), Offset(basR.left, basR.top + basR.height * 0.45f), Offset(basR.right, basR.top + basR.height * 0.45f), 0.25f * u)
    }

    /** Dentículos bajo una cornisa: dados de piedra en hilera. */
    private fun DrawScope.denticulos(x0: Float, x1: Float, y: Float, alto: Float, oscuro: Boolean, u: Float) {
        val d = alto * 0.9f
        var x = x0 + d * 0.3f
        while (x < x1 - d * 0.5f) {
            drawRect(n(PIEDRA_LUZ, oscuro), Offset(x, y), Size(d * 0.62f, alto))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(x + d * 0.5f, y), Size(d * 0.12f, alto))
            x += d
        }
    }

    // ---------------------------------------------------------------------
    //  Los nichos
    // ---------------------------------------------------------------------

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.17f, 0.335f, 0.83f, 0.835f)
        MuebleVida.MAPAS -> Rect(0.2f, 0.11f, 0.8f, 0.69f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.06f, 0.325f, 0.94f, 0.835f)
        MuebleVida.NOTAS -> Rect(0.085f, 0.19f, 0.915f, 0.87f)
    }

    // ---------------------------------------------------------------------
    //  Social: el banquete fraterno
    // ---------------------------------------------------------------------

    /** Una silla de anea vista desde arriba: el asiento tejido, el marco de pino y el respaldo de dos travesaños; mira hacia +y y gira [giro]. */
    private fun DrawScope.sillaAnea(c: Offset, ancho: Float, fondo: Float, giro: Float, oscuro: Boolean, u: Float) {
        rotate(giro, c) {
            val asiento = Path().apply {
                moveTo(c.x - ancho * 0.4f, c.y - fondo * 0.16f); lineTo(c.x + ancho * 0.4f, c.y - fondo * 0.16f)
                lineTo(c.x + ancho * 0.48f, c.y + fondo * 0.5f); lineTo(c.x - ancho * 0.48f, c.y + fondo * 0.5f); close()
            }
            drawPath(Path().apply { addPath(asiento, Offset(0.7f * u, 1f * u)) }, Color.Black.copy(alpha = 0.35f))
            drawPath(asiento, t(PINO_OSC, oscuro))
            val tejido = Path().apply {
                moveTo(c.x - ancho * 0.33f, c.y - fondo * 0.08f); lineTo(c.x + ancho * 0.33f, c.y - fondo * 0.08f)
                lineTo(c.x + ancho * 0.4f, c.y + fondo * 0.42f); lineTo(c.x - ancho * 0.4f, c.y + fondo * 0.42f); close()
            }
            drawPath(tejido, t(ANEA, oscuro))
            // El tejido de anea: vueltas concéntricas hacia el centro y las cuatro diagonales donde se cruzan.
            val cc = Offset(c.x, c.y + fondo * 0.17f)
            val esquinas = listOf(Offset(c.x - ancho * 0.33f, c.y - fondo * 0.08f), Offset(c.x + ancho * 0.33f, c.y - fondo * 0.08f), Offset(c.x + ancho * 0.4f, c.y + fondo * 0.42f), Offset(c.x - ancho * 0.4f, c.y + fondo * 0.42f))
            for (j in 1 until 5) {
                val f = 1f - j / 5f
                val q = esquinas.map { Offset(cc.x + (it.x - cc.x) * f, cc.y + (it.y - cc.y) * f) }
                drawPath(Path().apply { moveTo(q[0].x, q[0].y); q.drop(1).forEach { lineTo(it.x, it.y) }; close() }, t(ANEA_OSC, oscuro).copy(alpha = 0.55f), style = Stroke(0.22f * u))
            }
            esquinas.forEach { q -> drawLine(t(ANEA_OSC, oscuro).copy(alpha = 0.7f), q, cc, 0.22f * u) }
            drawPath(tejido, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.18f)), c.y - fondo * 0.1f, c.y + fondo * 0.45f))
            // El respaldo: los dos postes y los dos travesaños.
            listOf(-1f, 1f).forEach { s -> drawCircle(t(PINO, oscuro), 0.9f * u, Offset(c.x + s * ancho * 0.42f, c.y - fondo * 0.3f)) }
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(c.x - ancho * 0.42f + 0.5f * u, c.y - fondo * 0.3f + 0.8f * u), Offset(c.x + ancho * 0.42f + 0.5f * u, c.y - fondo * 0.3f + 0.8f * u), 1.3f * u)
            drawLine(t(PINO, oscuro), Offset(c.x - ancho * 0.42f, c.y - fondo * 0.3f), Offset(c.x + ancho * 0.42f, c.y - fondo * 0.3f), 1.3f * u, StrokeCap.Round)
            drawLine(t(PINO_LUZ, oscuro), Offset(c.x - ancho * 0.4f, c.y - fondo * 0.32f), Offset(c.x + ancho * 0.4f, c.y - fondo * 0.32f), 0.4f * u, StrokeCap.Round)
            drawLine(t(PINO_OSC, oscuro), Offset(c.x - ancho * 0.36f, c.y - fondo * 0.46f), Offset(c.x + ancho * 0.36f, c.y - fondo * 0.46f), 0.8f * u, StrokeCap.Round)
        }
    }

    /** El árbol de la libertad visto desde arriba, en su cajón de pino: la copa de hojas, las cintas y el gorro frigio. */
    private fun DrawScope.arbolLibertad(c: Offset, r: Float, oscuro: Boolean, u: Float, semilla: Int) {
        val caja = Rect(c.x - r * 0.62f, c.y - r * 0.62f, c.x + r * 0.62f, c.y + r * 0.62f)
        sombraSuave(caja, 0.4f * u, 1.6f * u, 0.3f)
        tabla(caja, oscuro, u, semilla, tono = PINO)
        drawRect(t(PINO_OSC, oscuro), caja.topLeft, caja.size, style = Stroke(0.8f * u))
        listOf(caja.topLeft, Offset(caja.right, caja.top), Offset(caja.left, caja.bottom), caja.bottomRight).forEach { q -> drawCircle(t(PINO_LUZ, oscuro), 0.8f * u, q) }
        drawCircle(Color.Black.copy(alpha = 0.35f), r * 0.95f, Offset(c.x + r * 0.1f, c.y + r * 0.14f))
        // Las cintas tricolores que asoman bajo la copa.
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col ->
            val a = Math.toRadians(40.0 + i * 28.0)
            drawLine(t(col, oscuro), Offset(c.x + r * 0.7f * cos(a).toFloat(), c.y + r * 0.7f * sin(a).toFloat()), Offset(c.x + r * 1.08f * cos(a + 0.12).toFloat(), c.y + r * 1.08f * sin(a + 0.12).toFloat()), 0.9f * u, StrokeCap.Round)
        }
        for (k in 0 until 9) {
            val a = k / 9.0 * 2 * Math.PI + azar(semilla, k)
            val rr = r * (0.52f + 0.12f * azar(semilla, k + 20))
            val p = Offset(c.x + r * 0.48f * cos(a).toFloat(), c.y + r * 0.48f * sin(a).toFloat())
            drawCircle(t(VERDE_OSC, oscuro), rr, p)
        }
        for (k in 0 until 9) {
            val a = k / 9.0 * 2 * Math.PI + azar(semilla, k)
            val rr = r * (0.42f + 0.1f * azar(semilla, k + 20))
            val p = Offset(c.x + r * 0.44f * cos(a).toFloat() - r * 0.06f, c.y + r * 0.44f * sin(a).toFloat() - r * 0.06f)
            drawCircle(t(VERDE, oscuro), rr, p)
            drawCircle(t(VERDE_LUZ, oscuro).copy(alpha = 0.7f), rr * 0.45f, Offset(p.x - rr * 0.3f, p.y - rr * 0.3f))
        }
        drawCircle(t(VERDE, oscuro), r * 0.5f, c)
        gorroFrigio(Offset(c.x, c.y), r * 0.58f, oscuro)
    }

    /** El tambor de la guardia visto desde arriba: el parche, el aro tricolor, las cuerdas y las baquetas cruzadas. */
    private fun DrawScope.tambor(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        drawCircle(Color.Black.copy(alpha = 0.38f), r, Offset(c.x + r * 0.1f, c.y + r * 0.14f))
        drawCircle(t(AZUL, oscuro), r, c)
        for (k in 0 until 14) {
            val a = k / 14.0 * 2 * Math.PI
            drawCircle(tl(ORO, oscuro), r * 0.05f, Offset(c.x + r * 0.92f * cos(a).toFloat(), c.y + r * 0.92f * sin(a).toFloat()))
        }
        drawCircle(t(ROJO, oscuro), r * 0.84f, c)
        drawCircle(t(ROJO_OSC, oscuro), r * 0.84f, c, style = Stroke(0.3f * u))
        drawCircle(Brush.radialGradient(listOf(t(Color(0xFFF2E8D0), oscuro), t(Color(0xFFDCCCA6), oscuro)), Offset(c.x - r * 0.2f, c.y - r * 0.2f), r * 0.9f), r * 0.72f, c)
        drawCircle(Color.Black.copy(alpha = 0.12f), r * 0.72f, c, style = Stroke(0.3f * u))
        // Las baquetas.
        listOf(-1f, 1f).forEach { s ->
            val p0 = Offset(c.x - r * 0.55f, c.y + s * r * 0.5f); val p1 = Offset(c.x + r * 0.7f, c.y - s * r * 0.35f)
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(p0.x + 0.5f * u, p0.y + 0.7f * u), Offset(p1.x + 0.5f * u, p1.y + 0.7f * u), 1f * u, StrokeCap.Round)
            drawLine(t(PINO_LUZ, oscuro), p0, p1, 0.9f * u, StrokeCap.Round)
            drawCircle(t(PINO, oscuro), 0.8f * u, p1)
        }
    }

    /** El cesto del pan visto desde arriba: el mimbre trenzado y tres hogazas con sus cortes. */
    private fun DrawScope.cestoPan(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(c.x - r + r * 0.1f, c.y - r * 0.7f + r * 0.14f), Size(r * 2, r * 1.4f))
        drawOval(t(ANEA_OSC, oscuro), Offset(c.x - r, c.y - r * 0.7f), Size(r * 2, r * 1.4f))
        drawOval(t(ANEA, oscuro), Offset(c.x - r, c.y - r * 0.7f), Size(r * 2, r * 1.4f), style = Stroke(r * 0.12f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(r * 0.12f, r * 0.08f))))
        listOf(Offset(-0.38f, -0.1f), Offset(0.3f, -0.2f), Offset(0.05f, 0.25f)).forEachIndexed { i, o ->
            val p = Offset(c.x + o.x * r, c.y + o.y * r)
            rotate(20f + i * 50f, p) {
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(p.x - r * 0.42f + 0.4f * u, p.y - r * 0.24f + 0.6f * u), Size(r * 0.84f, r * 0.48f))
                drawOval(Brush.radialGradient(listOf(t(mezcla(PAN, Color.White, 0.25f), oscuro), t(PAN, oscuro), t(mezcla(PAN, Color.Black, 0.3f), oscuro)), Offset(p.x - r * 0.1f, p.y - r * 0.08f), r * 0.5f),
                    Offset(p.x - r * 0.42f, p.y - r * 0.24f), Size(r * 0.84f, r * 0.48f))
                for (j in -1..1) drawLine(t(mezcla(PAN, Color.White, 0.45f), oscuro), Offset(p.x + j * r * 0.2f - r * 0.06f, p.y - r * 0.12f), Offset(p.x + j * r * 0.2f + r * 0.06f, p.y + r * 0.12f), 0.4f * u, StrokeCap.Round)
            }
        }
    }

    /** El fajo de pasquines atado con la cinta tricolor, visto desde arriba. */
    private fun DrawScope.fajo(r: Rect, oscuro: Boolean, u: Float) {
        for (k in 3 downTo 0) {
            val q = Rect(r.left + k * 0.6f * u, r.top + k * 0.5f * u, r.right + k * 0.3f * u, r.bottom + k * 0.4f * u)
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(q.left + 0.4f * u, q.top + 0.6f * u), q.size)
            drawRect(t(if (k % 2 == 0) PAPEL else PAPEL_VIEJO, oscuro), q.topLeft, q.size)
        }
        // La cabecera impresa del de arriba.
        drawRect(t(NEGRO, oscuro), Offset(r.left + r.width * 0.15f, r.top + r.height * 0.14f), Size(r.width * 0.7f, 0.7f * u))
        for (j in 0 until 4) drawRect(t(NEGRO, oscuro).copy(alpha = 0.35f), Offset(r.left + r.width * 0.15f, r.top + r.height * (0.32f + 0.13f * j)), Size(r.width * (if (j == 3) 0.4f else 0.7f), 0.35f * u))
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(t(col, oscuro), Offset(r.left + r.width * 0.55f + i * 0.7f * u, r.top - 0.5f * u), Size(0.7f * u, r.height + 1.5f * u)) }
        escarapela(Offset(r.left + r.width * 0.55f + 1f * u, r.center.y), minOf(r.width, r.height) * 0.14f, oscuro)
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.SOCIAL)
        val pared = h * 0.22f
        // El piso de baldosas hexagonales.
        baldosas(Rect(0f, pared, w, h), oscuro, u, e.semilla, 2.6f * u)
        // La pared de yeso con su lambrín, los carteles, la escarapela grande y las colgaduras.
        paredYeso(Rect(0f, 0f, w, pared), oscuro, u, pared * 0.24f)
        val lambTop = pared * 0.76f
        // Dos carteles pegados en el yeso, un poco torcidos.
        listOf(0.24f to -3f, 0.76f to 2.5f).forEachIndexed { i, (f, giro) ->
            val c = Offset(w * f, pared * 0.47f)
            val cw = pared * 0.44f; val ch = pared * 0.52f
            rotate(giro, c) {
                val r = Rect(c.x - cw / 2, c.y - ch / 2, c.x + cw / 2, c.y + ch / 2)
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 0.4f * u, r.top + 0.6f * u), r.size)
                drawRect(n(PAPEL, oscuro), r.topLeft, r.size)
                drawRect(t(AZUL, oscuro), r.topLeft, Size(r.width / 3, 0.8f * u))
                drawRect(t(BLANCO, oscuro), Offset(r.left + r.width / 3, r.top), Size(r.width / 3, 0.8f * u))
                drawRect(t(ROJO, oscuro), Offset(r.left + r.width * 2 / 3, r.top), Size(r.width / 3, 0.8f * u))
                drawRect(t(NEGRO, oscuro), Offset(r.left + r.width * 0.15f, r.top + r.height * 0.2f), Size(r.width * 0.7f, 0.9f * u))
                if (i == 0) escarapela(Offset(r.center.x, r.top + r.height * 0.48f), r.width * 0.16f, oscuro)
                for (j in 0 until (if (i == 0) 2 else 5)) drawRect(t(NEGRO, oscuro).copy(alpha = 0.35f), Offset(r.left + r.width * 0.15f, r.top + r.height * (if (i == 0) 0.72f else 0.38f) + j * 1.1f * u), Size(r.width * (if (j % 2 == 1) 0.45f else 0.7f), 0.35f * u))
            }
        }
        // Al centro, la escarapela grande entre dos banderas cruzadas.
        val ec = Offset(w / 2, pared * 0.5f)
        bandera(Offset(ec.x - pared * 0.1f, lambTop + pared * 0.12f), pared * 0.72f, -32f, oscuro, u)
        bandera(Offset(ec.x + pared * 0.1f, lambTop + pared * 0.12f), pared * 0.72f, 32f, oscuro, u)
        escarapela(ec, pared * 0.2f, oscuro, cintas = true)
        // Las colgaduras a lo largo de la cornisa, con su escarapela en cada nudo.
        val cornisa = Rect(0f, 0f, w, 1.6f * u)
        drawRect(Brush.verticalGradient(listOf(n(PIEDRA_LUZ, oscuro), n(PIEDRA, oscuro), mezcla(n(PIEDRA, oscuro), Color.Black, 0.25f)), 0f, cornisa.bottom), Offset.Zero, cornisa.size)
        val nc = 4
        for (k in 0 until nc) {
            val x0 = w * k / nc; val x1 = w * (k + 1) / nc
            colgadura(x0, x1, cornisa.bottom, pared * 0.12f, oscuro, u)
        }
        for (k in 0..nc) escarapela(Offset(w * k / nc, cornisa.bottom + 0.6f * u), pared * 0.09f, oscuro)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), pared, pared + 3f * u), Offset(0f, pared), Size(w, 3f * u))
        // Las esquinas: el fajo de pasquines y el cesto del pan arriba; el árbol de la libertad y el tambor abajo.
        val aro = 3.4f * u
        val esquina = a.left - aro
        val rTambor = minOf(esquina * 0.4f, (h - a.bottom) * 0.5f)
        fajo(Rect(esquina * 0.14f, pared + 2.6f * u, esquina * 0.14f + esquina * 0.56f, pared + 2.6f * u + esquina * 0.38f), oscuro, u)
        cestoPan(Offset(w - esquina * 0.48f, pared + 2.6f * u + esquina * 0.24f), esquina * 0.34f, oscuro, u)
        arbolLibertad(Offset(esquina * 0.48f, h - rTambor - 1.6f * u), rTambor * 1.05f, oscuro, u, e.semilla)
        tambor(Offset(w - esquina * 0.48f, h - rTambor - 1.6f * u), rTambor * 0.9f, oscuro, u)
        // Las sillas de anea: arriba, abajo y en las cabeceras.
        val fondoArr = minOf(a.top - aro - pared - 1.6f * u, 8.6f * u)
        val fondoAb = minOf(h - (a.bottom + aro) - 1.6f * u, 8.6f * u)
        val fondoS = minOf(fondoArr, fondoAb)
        val anchoS = fondoS * 1.1f
        val ns = ((a.width - 4f * u) / (anchoS * 1.8f)).toInt().coerceIn(3, 7)
        for (k in 0 until ns) {
            val x = a.left + a.width * (k + 0.5f) / ns
            sillaAnea(Offset(x, a.top - aro - 0.6f * u - fondoS * 0.5f), anchoS, fondoS, 0f, oscuro, u)
            sillaAnea(Offset(x, a.bottom + aro + 0.6f * u + fondoS * 0.5f), anchoS, fondoS, 180f, oscuro, u)
        }
        sillaAnea(Offset(a.left - aro - 0.6f * u - fondoS * 0.5f, a.center.y), anchoS, fondoS, -90f, oscuro, u)
        sillaAnea(Offset(a.right + aro + 0.6f * u + fondoS * 0.5f, a.center.y), anchoS, fondoS, 90f, oscuro, u)
        // La mesa: el mantel de lino que cae por los cantos, con su orla tricolor, y el lino (el nicho).
        val fuera = Rect(a.left - aro, a.top - aro, a.right + aro, a.bottom + aro)
        sombraSuave(fuera, 0.8f * u, 4f * u, if (oscuro) 0.5f else 0.3f)
        val lino = if (oscuro) mezcla(e.fondo, Color(0xFF4A463C), 0.4f) else mezcla(e.fondo, Color(0xFFF2ECDC), 0.5f)
        drawRect(mezcla(lino, Color.Black, if (oscuro) 0.2f else 0.08f), fuera.topLeft, fuera.size)
        val orla = aro * 0.55f
        val o = Rect(fuera.left + aro * 0.22f, fuera.top + aro * 0.22f, fuera.right - aro * 0.22f, fuera.bottom - aro * 0.22f)
        listOf(AZUL to 0f, BLANCO to orla / 3f, ROJO to orla * 2f / 3f).forEach { (col, m) ->
            drawRect(t(col, oscuro), Offset(o.left + m, o.top + m), Size(o.width - 2 * m, o.height - 2 * m), style = Stroke(orla / 3f + 0.1f * u))
        }
        // El borde ondulado del mantel que cuelga.
        var bx = fuera.left
        while (bx < fuera.right) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(bx, fuera.bottom - 0.6f * u), Offset(bx, fuera.bottom), 0.6f * u); bx += 2.2f * u }
        listOf(fuera.topLeft, Offset(fuera.right, fuera.top), Offset(fuera.left, fuera.bottom), fuera.bottomRight).forEach { escarapela(it, aro * 0.55f, oscuro) }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(a.left + 0.3f * u, a.top + 0.5f * u), a.size)
        drawRect(lino, a.topLeft, a.size)
        clipRect(a.left, a.top, a.right, a.bottom) {
            val hilo = (if (oscuro) Color.White else Color(0xFF6E5A36)).copy(alpha = if (oscuro) 0.018f else 0.03f)
            var lx = a.left + 1.2f * u; while (lx < a.right) { drawLine(hilo, Offset(lx, a.top), Offset(lx, a.bottom), 0.25f * u); lx += 1.4f * u }
            // Los pliegues del planchado, en cruz.
            val pl = Color.Black.copy(alpha = if (oscuro) 0.08f else 0.04f)
            val br = Color.White.copy(alpha = if (oscuro) 0.025f else 0.3f)
            listOf(0.33f, 0.67f).forEach { f -> drawLine(pl, Offset(a.left + a.width * f, a.top), Offset(a.left + a.width * f, a.bottom), 0.35f * u); drawLine(br, Offset(a.left + a.width * f + 0.4f * u, a.top), Offset(a.left + a.width * f + 0.4f * u, a.bottom), 0.35f * u) }
            drawLine(pl, Offset(a.left, a.center.y), Offset(a.right, a.center.y), 0.35f * u)
            drawLine(br, Offset(a.left, a.center.y + 0.4f * u), Offset(a.right, a.center.y + 0.4f * u), 0.35f * u)
        }
        sombraInterior(a, 1.6f * u, if (oscuro) 0.2f else 0.07f)
    }

    // ---------------------------------------------------------------------
    //  Mapas: el bufete del comité
    // ---------------------------------------------------------------------

    /** Una pata estriada al estilo de Luis XVI en [r]: el dado con su rosa, el fuste estriado que se afina y el trompo. */
    private fun DrawScope.pataEstriada(r: Rect, oscuro: Boolean, u: Float) {
        val cx = r.center.x; val wd = r.width
        val madera = Brush.horizontalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), r.left, r.right)
        val fuste = Path().apply { moveTo(r.left + wd * 0.1f, r.top + wd * 0.9f); lineTo(r.right - wd * 0.1f, r.top + wd * 0.9f); lineTo(cx + wd * 0.22f, r.bottom - wd * 0.6f); lineTo(cx - wd * 0.22f, r.bottom - wd * 0.6f); close() }
        drawPath(fuste, madera)
        for (k in -1..1) drawLine(Color.Black.copy(alpha = 0.3f), Offset(cx + k * wd * 0.22f, r.top + wd * 1.2f), Offset(cx + k * wd * 0.12f, r.bottom - wd * 0.9f), 0.3f * u)
        drawRect(madera, Offset(r.left, r.top), Size(wd, wd * 0.9f))
        drawCircle(tl(ORO, oscuro), wd * 0.22f, Offset(cx, r.top + wd * 0.45f))
        drawCircle(tl(ORO_OSC, oscuro), wd * 0.1f, Offset(cx, r.top + wd * 0.45f))
        drawRect(Brush.horizontalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO_OSC, oscuro)), r.left, r.right), Offset(cx - wd * 0.3f, r.bottom - wd * 0.7f), Size(wd * 0.6f, wd * 0.18f))
        drawPath(Path().apply { moveTo(cx - wd * 0.24f, r.bottom - wd * 0.52f); lineTo(cx + wd * 0.24f, r.bottom - wd * 0.52f); lineTo(cx + wd * 0.08f, r.bottom); lineTo(cx - wd * 0.08f, r.bottom); close() }, madera)
    }

    /** La escribanía: la bandeja de bronce con el tintero, el salvadera de arenilla y la pluma. */
    private fun DrawScope.escribania(c: Offset, ancho: Float, oscuro: Boolean, u: Float) {
        val k = ancho / 20f
        val bandeja = Rect(c.x - 10f * k, c.y + 2f * k, c.x + 10f * k, c.y + 5f * k)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(bandeja.left + 0.6f * u, bandeja.top + 0.9f * u), bandeja.size, CornerRadius(1.4f * k))
        drawRoundRect(Brush.verticalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO, oscuro), tl(ORO_OSC, oscuro)), bandeja.top, bandeja.bottom), bandeja.topLeft, bandeja.size, CornerRadius(1.4f * k))
        listOf(-4.8f, 4.8f).forEachIndexed { i, dx ->
            val f = Rect(c.x + dx * k - 3.4f * k, c.y - 5f * k, c.x + dx * k + 3.4f * k, c.y + 2.2f * k)
            drawRoundRect(Brush.horizontalGradient(listOf(t(Color(0xFF3E4A56), oscuro), t(Color(0xFF151A20), oscuro)), f.left, f.right), f.topLeft, f.size, CornerRadius(1.2f * k))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.35f), Offset(f.left + 1f * k, f.top + 1f * k), Offset(f.left + 1f * k, f.bottom - 1f * k), 0.5f * k)
            drawRect(Brush.horizontalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO_OSC, oscuro)), f.left, f.right), Offset(f.left + 0.6f * k, f.top - 1.4f * k), Size(f.width - 1.2f * k, 1.6f * k))
            if (i == 1) for (j in 0 until 3) drawCircle(t(NEGRO, oscuro), 0.35f * k, Offset(f.center.x - 1.2f * k + j * 1.2f * k, f.top - 0.6f * k))
        }
        val base = Offset(c.x - 4.6f * k, c.y - 5.6f * k)
        val punta = Offset(c.x - 13f * k, c.y - 18f * k)
        val pluma = Path().apply {
            moveTo(base.x, base.y); quadraticTo(base.x - 1.6f * k, base.y - 6f * k, punta.x, punta.y)
            quadraticTo(punta.x + 5f * k, punta.y + 3f * k, base.x - 1.4f * k, base.y - 3.4f * k); close()
        }
        drawPath(pluma, t(Color(0xFFF4EEE0), oscuro))
        drawPath(Path().apply { moveTo(base.x - 1.4f * k, base.y - 3.4f * k); quadraticTo(punta.x + 3.4f * k, punta.y + 4f * k, punta.x, punta.y); quadraticTo(punta.x + 2f * k, punta.y + 5.2f * k, base.x - 1.8f * k, base.y - 2.4f * k); close() }, Color.Black.copy(alpha = 0.16f))
        drawLine(t(Color(0xFFB8A880), oscuro), base, punta, 0.3f * k)
    }

    /** El compás de dibujo: dos brazos de latón con sus puntas, abiertos. */
    private fun DrawScope.compas(p: Offset, largo: Float, apertura: Float, giro: Float, oscuro: Boolean, u: Float) {
        rotate(giro, p) {
            listOf(-1f, 1f).forEach { s ->
                val a = Math.toRadians(90.0 + s * apertura / 2.0)
                val q = Offset(p.x + largo * cos(a).toFloat(), p.y + largo * sin(a).toFloat())
                val m = Offset(p.x + largo * 0.82f * cos(a).toFloat(), p.y + largo * 0.82f * sin(a).toFloat())
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(p.x + 0.6f * u, p.y + 0.9f * u), Offset(q.x + 0.6f * u, q.y + 0.9f * u), 1f * u, StrokeCap.Round)
                drawLine(tl(ORO, oscuro), p, m, 1f * u, StrokeCap.Round)
                drawLine(tl(ORO_LUZ, oscuro), p, m, 0.35f * u, StrokeCap.Round)
                drawLine(t(HIERRO_LUZ, oscuro), m, q, 0.5f * u, StrokeCap.Round)
            }
            drawCircle(tl(ORO, oscuro), 1.3f * u, p)
            drawCircle(tl(ORO_OSC, oscuro), 0.5f * u, p)
        }
    }

    /** El candelero de bronce con su vela; de noche, encendida y con su halo. */
    private fun DrawScope.candelero(c: Offset, alto: Float, oscuro: Boolean, u: Float) {
        val k = alto / 20f
        if (oscuro) halo(Offset(c.x, c.y - 17f * k), alto * 0.9f, LLAMA, 0.5f)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - 5f * k, c.y + 1f * k), Size(11f * k, 2.4f * k))
        drawOval(Brush.horizontalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO_OSC, oscuro)), c.x - 5f * k, c.x + 5f * k), Offset(c.x - 5f * k, c.y - 1.4f * k), Size(10f * k, 3f * k))
        drawRect(Brush.horizontalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO, oscuro), tl(ORO_OSC, oscuro)), c.x - 1f * k, c.x + 1f * k), Offset(c.x - 1f * k, c.y - 8f * k), Size(2f * k, 7f * k))
        drawOval(tl(ORO, oscuro), Offset(c.x - 2f * k, c.y - 5f * k), Size(4f * k, 2f * k))
        drawOval(Brush.horizontalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO_OSC, oscuro)), c.x - 3f * k, c.x + 3f * k), Offset(c.x - 3f * k, c.y - 9f * k), Size(6f * k, 1.8f * k))
        drawRect(Brush.horizontalGradient(listOf(t(Color(0xFFF6F0DE), oscuro), t(Color(0xFFD8CCAE), oscuro)), c.x - 1.5f * k, c.x + 1.5f * k), Offset(c.x - 1.5f * k, c.y - 16f * k), Size(3f * k, 7.4f * k))
        drawLine(t(NEGRO, oscuro), Offset(c.x, c.y - 16f * k), Offset(c.x, c.y - 17f * k), 0.3f * k)
        if (oscuro) {
            drawPath(Path().apply { moveTo(c.x, c.y - 16.4f * k); quadraticTo(c.x - 1.3f * k, c.y - 18f * k, c.x, c.y - 20f * k); quadraticTo(c.x + 1.3f * k, c.y - 18f * k, c.x, c.y - 16.4f * k) }, LLAMA)
            drawCircle(LLAMA_CENTRO, 0.5f * k, Offset(c.x, c.y - 17.4f * k))
        }
    }

    /** El reloj decimal de la República: la caja de nogal, el bisel dorado y la esfera de diez horas. */
    private fun DrawScope.relojDecimal(c: Offset, r: Float, oscuro: Boolean, u: Float, acento: Color) {
        val caja = Rect(c.x - r * 1.2f, c.y - r * 1.2f, c.x + r * 1.2f, c.y + r * 1.32f)
        sombraSuave(caja, 0.8f * u, 2.2f * u, if (oscuro) 0.5f else 0.3f)
        tabla(caja, oscuro, u, 71, tono = NOGAL_LUZ)
        drawRect(Color.Black.copy(alpha = 0.4f), caja.topLeft, caja.size, style = Stroke(0.35f * u))
        drawRect(tl(ORO, oscuro), Offset(caja.left + 0.8f * u, caja.top + 0.8f * u), Size(caja.width - 1.6f * u, caja.height - 1.6f * u), style = Stroke(0.3f * u))
        listOf(caja.topLeft, Offset(caja.right, caja.top)).forEach { q -> drawCircle(tl(ORO, oscuro), 0.7f * u, Offset(q.x + (if (q.x < c.x) 1.8f else -1.8f) * u, q.y + 1.8f * u)) }
        drawCircle(Color.Black.copy(alpha = 0.35f), r * 1.04f, Offset(c.x + 0.4f * u, c.y + 0.6f * u))
        drawCircle(Brush.linearGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO, oscuro), tl(ORO_OSC, oscuro)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), r * 1.04f, c)
        val cara = if (oscuro) mezcla(Color(0xFFF4EEDF), LLAMA, 0.15f).let { mezcla(it, Color.Black, 0.18f) } else Color(0xFFF7F2E4)
        drawCircle(cara, r * 0.86f, c)
        val tinta = Color(0xFF1C1A17)
        for (k in 0 until 10) {
            val a = Math.toRadians(k * 36.0 - 90)
            drawLine(tinta, Offset(c.x + r * 0.58f * cos(a).toFloat(), c.y + r * 0.58f * sin(a).toFloat()), Offset(c.x + r * 0.76f * cos(a).toFloat(), c.y + r * 0.76f * sin(a).toFloat()), r * 0.07f)
            for (j in 1 until 5) {
                val b = Math.toRadians(k * 36.0 + j * 7.2 - 90)
                drawLine(tinta.copy(alpha = 0.5f), Offset(c.x + r * 0.7f * cos(b).toFloat(), c.y + r * 0.7f * sin(b).toFloat()), Offset(c.x + r * 0.76f * cos(b).toFloat(), c.y + r * 0.76f * sin(b).toFloat()), r * 0.025f)
            }
        }
        drawCircle(tinta.copy(alpha = 0.6f), r * 0.78f, c, style = Stroke(r * 0.02f))
        val ah = Math.toRadians(3 * 36.0 - 90 + 10); val am = Math.toRadians(7 * 36.0 - 90)
        drawLine(tinta, c, Offset(c.x + r * 0.36f * cos(ah).toFloat(), c.y + r * 0.36f * sin(ah).toFloat()), r * 0.08f, StrokeCap.Round)
        drawLine(tinta, c, Offset(c.x + r * 0.6f * cos(am).toFloat(), c.y + r * 0.6f * sin(am).toFloat()), r * 0.05f, StrokeCap.Round)
        drawCircle(acento, r * 0.09f, c)
        drawArc(Color.White.copy(alpha = if (oscuro) 0.14f else 0.4f), 200f, 70f, false, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /** La regla del metro, de [x0] a [x1] a la altura [y]: el latón con sus decímetros y centímetros. */
    private fun DrawScope.metro(x0: Float, x1: Float, y: Float, g: Float, oscuro: Boolean, u: Float) {
        val r = Rect(x0, y - g / 2, x1, y + g / 2)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
        drawRect(Brush.verticalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO, oscuro), tl(ORO_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)
        val marca = tl(ORO_OSC, oscuro).let { mezcla(it, Color.Black, 0.4f) }
        for (k in 0..100) {
            val x = r.left + r.width * k / 100f
            val l = when { k % 10 == 0 -> g * 0.62f; k % 5 == 0 -> g * 0.42f; else -> g * 0.24f }
            drawLine(marca, Offset(x, r.top), Offset(x, r.top + l), if (k % 10 == 0) 0.25f * u else 0.12f * u)
        }
        drawRect(marca, r.topLeft, r.size, style = Stroke(0.2f * u))
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.MAPAS)
        // Detrás: la pared de yeso con su lambrín y el piso de baldosas.
        paredYeso(Rect(0f, 0f, w, h * 0.9f), oscuro, u, h * 0.2f)
        baldosas(Rect(0f, h * 0.9f, w, h), oscuro, u, e.semilla, 2.4f * u, ay = 0.45f)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), h * 0.9f, h * 0.94f), Offset(0f, h * 0.9f), Size(w, h * 0.04f))
        val tablero = Rect(w * 0.015f, h * 0.02f, w * 0.985f, h * 0.775f)
        // Las patas estriadas y el friso con sus dos cajones de anilla.
        listOf(w * 0.055f, w * 0.945f).forEach { x -> pataEstriada(Rect(x - 2.6f * u, h * 0.84f, x + 2.6f * u, h - 0.4f * u), oscuro, u) }
        val friso = Rect(w * 0.03f, h * 0.83f, w * 0.97f, h * 0.895f)
        tabla(friso, oscuro, u, e.semilla + 4)
        listOf(0.3f, 0.7f).forEach { f ->
            val cx = w * f
            val cajon = Rect(cx - w * 0.16f, friso.top + 0.6f * u, cx + w * 0.16f, friso.bottom - 0.6f * u)
            tabla(cajon, oscuro, u, e.semilla + (f * 10).toInt(), tono = NOGAL_LUZ)
            drawRect(tl(ORO, oscuro), Offset(cajon.left + 0.7f * u, cajon.top + 0.7f * u), Size(cajon.width - 1.4f * u, cajon.height - 1.4f * u), style = Stroke(0.3f * u))
            drawRect(Color.Black.copy(alpha = 0.45f), cajon.topLeft, cajon.size, style = Stroke(0.35f * u))
            drawCircle(tl(ORO, oscuro), 1.1f * u, Offset(cx, cajon.center.y - 0.6f * u))
            drawCircle(tl(ORO, oscuro), 1.6f * u, Offset(cx, cajon.center.y + 0.5f * u), style = Stroke(0.45f * u))
        }
        // Las rosetas doradas sobre las patas.
        listOf(w * 0.055f, w * 0.945f).forEach { x ->
            drawCircle(tl(ORO, oscuro), 1.6f * u, Offset(x, friso.center.y))
            for (k in 0 until 8) { val an = k * Math.PI / 4; drawCircle(tl(ORO_OSC, oscuro), 0.4f * u, Offset(x + 1.1f * u * cos(an).toFloat(), friso.center.y + 1.1f * u * sin(an).toFloat())) }
        }
        sombraSuave(tablero, 1f * u, 4f * u, if (oscuro) 0.5f else 0.32f)
        // El canto con su moldura acanalada de bronce.
        val canto = Rect(w * 0.005f, tablero.bottom, w * 0.995f, h * 0.835f)
        drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), canto.top, canto.bottom), canto.topLeft, canto.size)
        oro(Rect(canto.left, canto.top, canto.right, canto.top + 0.8f * u), oscuro)
        oro(Rect(canto.left, canto.bottom - 0.6f * u, canto.right, canto.bottom), oscuro)
        var gx = canto.left + 1.2f * u
        while (gx < canto.right - 1f * u) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(gx, canto.top + 1.4f * u), Offset(gx, canto.bottom - 1.2f * u), 0.35f * u, StrokeCap.Round); gx += 1.6f * u }
        // El tablero de nogal, la moldura de bronce y el cuero verde con su filete de oro.
        tabla(tablero, oscuro, u, e.semilla + 21)
        drawRect(Brush.linearGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO_OSC, oscuro)), tablero.topLeft, tablero.bottomRight), tablero.topLeft, tablero.size, style = Stroke(1f * u))
        val cuero = Rect(tablero.left + 3.4f * u, tablero.top + 2.4f * u, tablero.right - 3.4f * u, tablero.bottom - 2.4f * u)
        drawRect(t(CUERO_VERDE, oscuro), cuero.topLeft, cuero.size)
        drawRect(Brush.radialGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.03f else 0.08f), Color.Black.copy(alpha = 0.15f)), cuero.center, cuero.width * 0.6f), cuero.topLeft, cuero.size)
        drawRect(tl(ORO, oscuro), Offset(cuero.left + 1f * u, cuero.top + 1f * u), Size(cuero.width - 2f * u, cuero.height - 2f * u), style = Stroke(0.3f * u))
        drawRect(tl(ORO, oscuro).copy(alpha = 0.7f), Offset(cuero.left + 1.6f * u, cuero.top + 1.6f * u), Size(cuero.width - 3.2f * u, cuero.height - 3.2f * u), style = Stroke(0.18f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.5f * u, 0.5f * u))))
        drawRect(Color.Black.copy(alpha = 0.4f), cuero.topLeft, cuero.size, style = Stroke(0.3f * u))
        // El mapa (el nicho): papel de imprenta claro, con su cuadrícula de grados y su filete, muy suaves.
        val papel = if (oscuro) mezcla(e.fondo, Color(0xFF45413A), 0.4f) else mezcla(e.fondo, PAPEL, 0.4f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.25f), Offset(a.left + 0.5f * u, a.top + 0.8f * u), a.size)
        drawRect(papel, a.topLeft, a.size)
        clipRect(a.left, a.top, a.right, a.bottom) {
            val linea = (if (oscuro) Color.White else Color(0xFF3A3A44)).copy(alpha = if (oscuro) 0.03f else 0.05f)
            var x = a.left + a.width / 8; while (x < a.right - 1f) { drawLine(linea, Offset(x, a.top), Offset(x, a.bottom), 0.22f * u); x += a.width / 8 }
            var y = a.top + a.height / 6; while (y < a.bottom - 1f) { drawLine(linea, Offset(a.left, y), Offset(a.right, y), 0.22f * u); y += a.height / 6 }
        }
        drawRect(e.secundario.copy(alpha = if (oscuro) 0.35f else 0.4f), Offset(a.left + 1.2f * u, a.top + 1.2f * u), Size(a.width - 2.4f * u, a.height - 2.4f * u), style = Stroke(0.35f * u))
        drawRect(e.acento.copy(alpha = if (oscuro) 0.25f else 0.3f), Offset(a.left + 1.9f * u, a.top + 1.9f * u), Size(a.width - 3.8f * u, a.height - 3.8f * u), style = Stroke(0.2f * u))
        sombraInterior(a, 2f * u, if (oscuro) 0.22f else 0.08f)
        // A la izquierda: la escribanía, el compás y el candelero.
        val izq = Rect(cuero.left + 1.6f * u, a.top, a.left - 1.6f * u, a.bottom)
        escribania(Offset(izq.center.x + izq.width * 0.08f, izq.top + izq.height * 0.3f), minOf(izq.width * 0.9f, izq.height * 0.4f), oscuro, u)
        compas(Offset(izq.center.x - izq.width * 0.15f, izq.top + izq.height * 0.48f), izq.height * 0.26f, 26f, -24f, oscuro, u)
        candelero(Offset(izq.center.x + izq.width * 0.18f, izq.bottom - 1f * u), minOf(izq.height * 0.34f, izq.width * 0.9f), oscuro, u)
        // A la derecha: el reloj decimal y el gorro frigio.
        val der = Rect(a.right + 1.6f * u, a.top, cuero.right - 1.6f * u, a.bottom)
        val rr = minOf(der.width * 0.36f, der.height * 0.2f)
        relojDecimal(Offset(der.center.x, der.top + rr * 1.3f), rr, oscuro, u, e.acento)
        gorroFrigio(Offset(der.center.x, der.bottom - der.height * 0.2f), minOf(der.width * 0.8f, der.height * 0.32f), oscuro)
        // Arriba, el decreto enrollado con su cinta; abajo, la regla del metro.
        val gr = (a.top - cuero.top) * 0.62f
        val ry = (cuero.top + a.top) / 2f + 0.2f * u
        val rx0 = a.left + a.width * 0.12f; val rx1 = a.left + a.width * 0.5f
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(rx0 + 0.5f * u, ry - gr / 2 + 0.8f * u), Size(rx1 - rx0, gr), CornerRadius(gr / 2))
        drawRoundRect(Brush.verticalGradient(listOf(t(PAPEL, oscuro), t(mezcla(PAPEL, PAPEL_VIEJO, 0.5f), oscuro), t(PAPEL_VIEJO, oscuro)), ry - gr / 2, ry + gr / 2), Offset(rx0, ry - gr / 2), Size(rx1 - rx0, gr), CornerRadius(gr / 2))
        drawOval(t(PAPEL_VIEJO, oscuro), Offset(rx1 - gr * 0.3f, ry - gr / 2), Size(gr * 0.6f, gr))
        drawOval(t(mezcla(PAPEL_VIEJO, Color.Black, 0.25f), oscuro), Offset(rx1 - gr * 0.12f, ry - gr * 0.2f), Size(gr * 0.24f, gr * 0.4f))
        val cx = rx0 + (rx1 - rx0) * 0.45f
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(t(col, oscuro), Offset(cx + i * 0.6f * u, ry - gr / 2), Size(0.6f * u, gr)) }
        // La barra de lacre junto al decreto.
        drawRoundRect(t(ROJO_OSC, oscuro), Offset(a.left + a.width * 0.6f, ry - gr * 0.25f), Size(a.width * 0.12f, gr * 0.5f), CornerRadius(gr * 0.2f))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(a.left + a.width * 0.6f + 0.5f * u, ry - gr * 0.2f), Size(a.width * 0.11f, gr * 0.12f))
        metro(a.left + a.width * 0.1f, a.left + a.width * 0.9f, (a.bottom + cuero.bottom) / 2f, (cuero.bottom - a.bottom) * 0.55f, oscuro, u)
    }

    // ---------------------------------------------------------------------
    //  Otras vidas: las hornacinas del Panteón
    // ---------------------------------------------------------------------

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.OTRAS_VIDAS)
        val piedra = n(PIEDRA, oscuro); val luz = n(PIEDRA_LUZ, oscuro); val sombraP = mezcla(piedra, Color.Black, 0.25f); val junta = n(PIEDRA_JUNTA, oscuro)
        // El muro de sillares detrás de todo.
        drawRect(piedra, Offset.Zero, size)
        val fila = 4.4f * u
        var y = 0f; var k = 0
        while (y < h) {
            drawLine(junta, Offset(0f, y), Offset(w, y), 0.25f * u)
            var x = if (k % 2 == 0) 0f else 6f * u
            while (x < w) { drawLine(junta, Offset(x, y), Offset(x, y + fila), 0.25f * u); x += 12f * u }
            y += fila; k++
        }
        // El entablamento: frontón, cornisa con dentículos y friso con coronas y festones.
        val friso = Rect(0f, a.top - h * 0.135f, w, a.top - 1.6f * u)
        val corn = Rect(0f, friso.top - h * 0.06f, w, friso.top)
        val fr = Path().apply { moveTo(w * 0.32f, corn.top); lineTo(w / 2, corn.top - h * 0.085f); lineTo(w * 0.68f, corn.top); close() }
        drawPath(Path().apply { addPath(fr, Offset(0.5f * u, 0.8f * u)) }, Color.Black.copy(alpha = 0.25f))
        drawPath(fr, luz)
        drawPath(Path().apply { moveTo(w / 2, corn.top - h * 0.085f); lineTo(w * 0.68f, corn.top); lineTo(w / 2, corn.top); close() }, Color.Black.copy(alpha = 0.08f))
        drawPath(fr, sombraP, style = Stroke(0.6f * u))
        val tim = Path().apply { moveTo(w * 0.35f, corn.top - 0.6f * u); lineTo(w / 2, corn.top - h * 0.085f + 2f * u); lineTo(w * 0.65f, corn.top - 0.6f * u); close() }
        drawPath(tim, mezcla(piedra, e.secundario, if (oscuro) 0.15f else 0.12f))
        escarapela(Offset(w / 2, corn.top - h * 0.03f), h * 0.026f, oscuro)
        gorroFrigio(Offset(w / 2, corn.top - h * 0.085f - h * 0.022f), minOf(h * 0.06f, corn.top * 0.5f), oscuro)
        listOf(w * 0.32f, w * 0.68f).forEach { x -> drawPath(Path().apply { moveTo(x - 1.6f * u, corn.top); lineTo(x, corn.top - 3f * u); lineTo(x + 1.6f * u, corn.top); close() }, luz) }
        drawRect(Brush.verticalGradient(listOf(luz, piedra, sombraP), corn.top, corn.bottom), corn.topLeft, corn.size)
        drawRect(luz, corn.topLeft, Size(w, corn.height * 0.3f))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, corn.top + corn.height * 0.3f), Size(w, 0.35f * u))
        denticulos(0f, w, corn.top + corn.height * 0.48f, corn.height * 0.4f, oscuro, u)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), friso.top, friso.top + 1.6f * u), friso.topLeft, Size(w, 1.6f * u))
        drawRect(mezcla(piedra, Color.White, if (oscuro) 0f else 0.06f), Offset(0f, friso.top + 1.6f * u), Size(w, friso.height - 1.6f * u))
        val col = a.width / 5f
        val rl = minOf(friso.height * 0.34f, col * 0.24f)
        val hoja = t(mezcla(VERDE, ORO, 0.35f), oscuro)
        for (i in 0 until 5) laurel(Offset(a.left + col * (i + 0.5f), friso.center.y - rl * 0.1f), rl, oscuro, hoja, t(ROJO, oscuro))
        for (i in 0 until 4) {
            val x0 = a.left + col * (i + 0.5f) + rl * 1.2f; val x1 = a.left + col * (i + 1.5f) - rl * 1.2f
            colgadura(x0, x1, friso.top + 2.2f * u, friso.height * 0.24f, oscuro, u)
        }
        listOf(Offset(a.left - 0.5f * u, friso.top + 2.2f * u), Offset(a.right + 0.5f * u, friso.top + 2.2f * u)).forEach { escarapela(it, 1.4f * u, oscuro) }
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, friso.bottom - 0.5f * u), Size(w, 0.5f * u))
        // Las pilastras de los costados.
        pilastra(Rect(w * 0.01f, a.top - 1.6f * u, a.left - 0.8f * u, a.bottom + 1.2f * u), oscuro, u)
        pilastra(Rect(a.right + 0.8f * u, a.top - 1.6f * u, w * 0.99f, a.bottom + 1.2f * u), oscuro, u)
        // Las cinco hornacinas (el nicho): la piedra clara y la concha en sombra.
        val fondoH = if (oscuro) mezcla(e.fondo, Color(0xFF3E3C36), 0.42f) else mezcla(e.fondo, PIEDRA_LUZ, 0.3f)
        drawRect(sombraP, Offset(a.left - 0.6f * u, a.top - 1.6f * u), Size(a.width + 1.2f * u, a.height + 1.6f * u))
        val sep = 1.6f * u
        for (i in 0 until 5) {
            val c = Rect(a.left + col * i + sep, a.top, a.left + col * (i + 1) - sep, a.bottom)
            val arco = c.width / 2f
            val hueco = Path().apply {
                moveTo(c.left, c.bottom); lineTo(c.left, c.top + arco)
                arcTo(Rect(c.left, c.top, c.right, c.top + arco * 2f), 180f, 180f, false)
                lineTo(c.right, c.bottom); close()
            }
            drawPath(hueco, fondoH)
            clipPath(hueco) {
                // La concha de la bóveda: gallones que salen del centro del arco, y la sombra que cae.
                val cc = Offset(c.center.x, c.top + arco)
                for (j in 0 until 9) {
                    val an = Math.toRadians(180.0 + j * 22.5)
                    drawLine(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.08f), cc, Offset(cc.x + arco * cos(an).toFloat(), cc.y + arco * sin(an).toFloat()), 0.3f * u)
                }
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Color.Transparent), c.top, c.top + arco * 1.2f), c.topLeft, Size(c.width, arco * 1.2f))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.12f), Color.Transparent, Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.25f else 0.12f)), c.left, c.right), c.topLeft, c.size)
            }
            // La moldura del arco y la clave.
            drawArc(luz, 180f, 180f, false, Offset(c.left, c.top), Size(c.width, arco * 2f), style = Stroke(0.9f * u))
            drawArc(Color.Black.copy(alpha = 0.2f), 180f, 180f, false, Offset(c.left + 0.6f * u, c.top + 0.6f * u), Size(c.width - 1.2f * u, arco * 2f - 1.2f * u), style = Stroke(0.3f * u))
            drawPath(Path().apply { moveTo(c.center.x - 1.4f * u, c.top - 0.8f * u); lineTo(c.center.x + 1.4f * u, c.top - 0.8f * u); lineTo(c.center.x + 0.9f * u, c.top + 1.6f * u); lineTo(c.center.x - 0.9f * u, c.top + 1.6f * u); close() }, luz)
            // Las impostas a los lados.
            listOf(c.left, c.right).forEach { x -> drawRect(luz, Offset(x - 0.6f * u, c.top + arco - 0.5f * u), Size(1.2f * u, 1f * u)) }
        }
        // Las pilastras entre hornacinas, estriadas.
        for (i in 1 until 5) {
            val x = a.left + col * i
            pilastra(Rect(x - sep, a.top - 0.4f * u, x + sep, a.bottom), oscuro, u)
        }
        // El zócalo de sillares con las cinco placas de bronce y el plinto.
        val zoc = Rect(0f, a.bottom, w, h)
        drawRect(Brush.verticalGradient(listOf(luz, piedra), zoc.top, zoc.top + 1.6f * u), Offset(0f, zoc.top), Size(w, 1.6f * u))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(0f, zoc.top + 1.6f * u), Size(w, 0.4f * u))
        val pl = Rect(0f, zoc.top + 2f * u, w, h - (h - a.bottom) * 0.3f)
        drawRect(piedra, pl.topLeft, pl.size)
        for (i in 0 until 5) {
            val cx = a.left + col * (i + 0.5f)
            val placa = Rect(cx - col * 0.3f, pl.top + pl.height * 0.22f, cx + col * 0.3f, pl.bottom - pl.height * 0.22f)
            if (placa.height < 1f) continue
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(placa.left + 0.4f * u, placa.top + 0.6f * u), placa.size)
            drawRect(Brush.verticalGradient(listOf(tl(ORO_LUZ, oscuro), tl(ORO, oscuro), tl(ORO_OSC, oscuro)), placa.top, placa.bottom), placa.topLeft, placa.size)
            drawRect(tl(ORO_OSC, oscuro), Offset(placa.left + 0.6f * u, placa.top + 0.6f * u), Size(placa.width - 1.2f * u, placa.height - 1.2f * u), style = Stroke(0.25f * u))
            listOf(placa.left + 1.2f * u, placa.right - 1.2f * u).forEach { x -> drawCircle(tl(ORO_OSC, oscuro), 0.35f * u, Offset(x, placa.center.y)) }
            if (i < 4) drawLine(junta, Offset(a.left + col * (i + 1), pl.top), Offset(a.left + col * (i + 1), pl.bottom), 0.3f * u)
        }
        val plinto = Rect(0f, pl.bottom, w, h)
        drawRect(Brush.verticalGradient(listOf(luz, sombraP), plinto.top, plinto.bottom), plinto.topLeft, plinto.size)
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, plinto.top), Offset(w, plinto.top), 0.4f * u)
    }

    // ---------------------------------------------------------------------
    //  Íconos de «Tus mapas»
    // ---------------------------------------------------------------------

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 1f || h < 1f) return
        val k = h / 90f
        val u = k
        val c = Offset(w / 2, h / 2)
        val sombra = Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f)
        when (id) {
            "unesco" -> {
                // El Panteón: el pórtico de columnas con su frontón, el tambor columnado y la cúpula con su linterna.
                val piedra = n(PIEDRA, oscuro); val luz = n(PIEDRA_LUZ, oscuro); val sombraP = mezcla(piedra, Color.Black, 0.28f)
                drawOval(sombra, Offset(c.x - 50f * k, c.y + 34f * k), Size(100f * k, 8f * k))
                // La cúpula.
                val tamb = Rect(c.x - 20f * k, c.y - 16f * k, c.x + 20f * k, c.y - 2f * k)
                drawRect(Brush.horizontalGradient(listOf(luz, piedra, sombraP), tamb.left, tamb.right), tamb.topLeft, tamb.size)
                for (j in 0..8) drawLine(sombraP, Offset(tamb.left + 2f * k + j * 4.5f * k, tamb.top + 2f * k), Offset(tamb.left + 2f * k + j * 4.5f * k, tamb.bottom), 1f * k)
                drawRect(luz, Offset(tamb.left - 1.6f * k, tamb.top - 2f * k), Size(tamb.width + 3.2f * k, 2.4f * k))
                val cup = Path().apply { moveTo(c.x - 17f * k, tamb.top - 2f * k); cubicTo(c.x - 17f * k, c.y - 34f * k, c.x + 17f * k, c.y - 34f * k, c.x + 17f * k, tamb.top - 2f * k); close() }
                drawPath(cup, Brush.horizontalGradient(listOf(t(Color(0xFF9AA6AA), oscuro), t(Color(0xFF6E7A80), oscuro), t(Color(0xFF4A545A), oscuro)), c.x - 17f * k, c.x + 17f * k))
                clipPath(cup) { for (j in -3..3) drawLine(Color.Black.copy(alpha = 0.2f), Offset(c.x + j * 4.6f * k, tamb.top), Offset(c.x + j * 1.2f * k, c.y - 32f * k), 0.6f * k) }
                drawRect(luz, Offset(c.x - 2.6f * k, c.y - 37f * k), Size(5.2f * k, 6f * k))
                drawPath(Path().apply { moveTo(c.x - 3f * k, c.y - 37f * k); lineTo(c.x, c.y - 41f * k); lineTo(c.x + 3f * k, c.y - 37f * k); close() }, e.c2)
                drawLine(e.c1, Offset(c.x, c.y - 41f * k), Offset(c.x, c.y - 44f * k), 1f * k)
                // El cuerpo y el pórtico.
                drawRect(piedra, Offset(c.x - 40f * k, c.y - 2f * k), Size(80f * k, 34f * k))
                drawRect(sombraP, Offset(c.x + 22f * k, c.y - 2f * k), Size(18f * k, 34f * k))
                val fr = Path().apply { moveTo(c.x - 28f * k, c.y + 2f * k); lineTo(c.x, c.y - 10f * k); lineTo(c.x + 28f * k, c.y + 2f * k); close() }
                drawPath(fr, luz)
                drawPath(Path().apply { moveTo(c.x - 23f * k, c.y + 0.5f * k); lineTo(c.x, c.y - 7.4f * k); lineTo(c.x + 23f * k, c.y + 0.5f * k); close() }, mezcla(piedra, e.c1, 0.18f))
                drawRect(luz, Offset(c.x - 28f * k, c.y + 2f * k), Size(56f * k, 3f * k))
                for (j in 0 until 6) {
                    val x = c.x - 24f * k + j * 9.6f * k
                    drawRect(Brush.horizontalGradient(listOf(luz, piedra, sombraP), x - 2.2f * k, x + 2.2f * k), Offset(x - 2.2f * k, c.y + 5f * k), Size(4.4f * k, 24f * k))
                    drawRect(luz, Offset(x - 3f * k, c.y + 5f * k), Size(6f * k, 1.6f * k))
                }
                drawRect(luz, Offset(c.x - 32f * k, c.y + 29f * k), Size(64f * k, 2.4f * k))
                drawRect(sombraP, Offset(c.x - 36f * k, c.y + 31.4f * k), Size(72f * k, 2.4f * k))
                escarapela(Offset(c.x, c.y - 2.6f * k), 2.6f * k, oscuro)
            }
            "rascar" -> {
                // El mapa de los departamentos: la hoja impresa con sus casillas, la capa por rascar y un sou de cobre.
                val hoja = Rect(c.x - 44f * k, c.y - 31f * k, c.x + 44f * k, c.y + 31f * k)
                drawRect(sombra, Offset(hoja.left + 2f * k, hoja.top + 3f * k), hoja.size)
                drawRect(t(PAPEL, oscuro), hoja.topLeft, hoja.size)
                drawRect(e.c2.copy(alpha = 0.7f), Offset(hoja.left + 2.5f * k, hoja.top + 2.5f * k), Size(hoja.width - 5f * k, hoja.height - 5f * k), style = Stroke(1f * k))
                val francia = Path().apply {
                    moveTo(hoja.left + 22f * k, hoja.top + 8f * k); lineTo(hoja.left + 34f * k, hoja.top + 10f * k); lineTo(hoja.left + 42f * k, hoja.top + 20f * k)
                    lineTo(hoja.left + 38f * k, hoja.top + 38f * k); lineTo(hoja.left + 30f * k, hoja.top + 50f * k); lineTo(hoja.left + 14f * k, hoja.top + 48f * k)
                    lineTo(hoja.left + 12f * k, hoja.top + 32f * k); lineTo(hoja.left + 6f * k, hoja.top + 22f * k); lineTo(hoja.left + 14f * k, hoja.top + 20f * k); close()
                }
                drawPath(francia, mezcla(t(PAPEL, oscuro), e.c2, 0.15f))
                clipPath(francia) {
                    val tinta = t(NEGRO, oscuro).copy(alpha = 0.45f)
                    var x = hoja.left + 4f * k; var j = 0
                    while (x < hoja.left + 46f * k) { drawLine(tinta, Offset(x, hoja.top), Offset(x + (if (j % 2 == 0) 3f else -2f) * k, hoja.bottom), 0.5f * k); x += 7f * k; j++ }
                    var y = hoja.top + 6f * k
                    while (y < hoja.bottom) { drawLine(tinta, Offset(hoja.left, y), Offset(hoja.left + 50f * k, y + (if (j % 2 == 0) 2f else -1.5f) * k), 0.5f * k); y += 7f * k; j++ }
                    drawRect(e.c1.copy(alpha = 0.6f), Offset(hoja.left + 22f * k, hoja.top + 20f * k), Size(7f * k, 7f * k))
                }
                drawPath(francia, t(NEGRO, oscuro).copy(alpha = 0.8f), style = Stroke(1.1f * k))
                val capa = Path().apply {
                    moveTo(hoja.right, hoja.top); lineTo(hoja.left + 46f * k, hoja.top)
                    var yy = hoja.top; var j = 0
                    while (yy < hoja.bottom) { lineTo(hoja.left + 44f * k - (yy - hoja.top) * 0.3f + (if (j % 2 == 0) 3f else -2f) * k, yy); yy += 4f * k; j++ }
                    lineTo(hoja.left + 28f * k, hoja.bottom); lineTo(hoja.right, hoja.bottom); close()
                }
                drawPath(capa, t(Color(0xFF8E8A80), oscuro))
                clipPath(capa) { var xx = hoja.left; while (xx < hoja.right + hoja.height) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(xx, hoja.top), Offset(xx - hoja.height, hoja.bottom), 0.7f * k); xx += 3f * k } }
                // El sou de cobre con el gorro de la libertad.
                val mo = Offset(hoja.left + 54f * k, hoja.top + 40f * k)
                drawCircle(sombra, 13f * k, Offset(mo.x + 2f * k, mo.y + 3f * k))
                drawCircle(Brush.linearGradient(listOf(COBRE_LUZ, COBRE, COBRE_OSC), Offset(mo.x - 13f * k, mo.y - 13f * k), Offset(mo.x + 13f * k, mo.y + 13f * k)), 13f * k, mo)
                drawCircle(COBRE_OSC, 11f * k, mo, style = Stroke(0.8f * k))
                drawCircle(COBRE_OSC, 12.4f * k, mo, style = Stroke(0.6f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f * k, 1f * k))))
                drawPath(Path().apply {
                    moveTo(mo.x - 5f * k, mo.y + 4f * k); cubicTo(mo.x - 6f * k, mo.y - 2f * k, mo.x - 2f * k, mo.y - 7f * k, mo.x + 2f * k, mo.y - 6f * k)
                    cubicTo(mo.x + 6f * k, mo.y - 5f * k, mo.x + 6f * k, mo.y - 1f * k, mo.x + 4f * k, mo.y); cubicTo(mo.x + 3f * k, mo.y - 2f * k, mo.x + 1f * k, mo.y - 2f * k, mo.x + 1f * k, mo.y)
                    cubicTo(mo.x + 3f * k, mo.y + 1f * k, mo.x + 5f * k, mo.y + 2f * k, mo.x + 5f * k, mo.y + 4f * k); close()
                }, COBRE_OSC)
                drawArc(Color.White.copy(alpha = 0.45f), 200f, 60f, false, Offset(mo.x - 11f * k, mo.y - 11f * k), Size(22f * k, 22f * k), style = Stroke(1.2f * k))
                for (j in 0 until 5) drawRect(t(Color(0xFF8E8A80), oscuro), Offset(mo.x - 17f * k - j * 2.6f * k, mo.y - 2f * k + (j % 2) * 4f * k), Size(1.6f * k, 1.6f * k))
            }
            else -> {
                // Tus lugares: el plano de la ciudad con el río y tres escarapelas clavadas con sus cintas.
                val hoja = Rect(c.x - 50f * k, c.y - 26f * k, c.x + 50f * k, c.y + 34f * k)
                drawRect(sombra, Offset(hoja.left + 2f * k, hoja.top + 3f * k), hoja.size)
                drawRect(t(PAPEL, oscuro), hoja.topLeft, hoja.size)
                clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                    val calle = t(PAPEL_VIEJO, oscuro)
                    var x = hoja.left + 8f * k; while (x < hoja.right) { drawLine(calle, Offset(x, hoja.top), Offset(x + 6f * k, hoja.bottom), 1.6f * k); x += 16f * k }
                    var y = hoja.top + 10f * k; while (y < hoja.bottom) { drawLine(calle, Offset(hoja.left, y), Offset(hoja.right, y - 4f * k), 1.6f * k); y += 14f * k }
                    drawPath(Path().apply { moveTo(hoja.left, hoja.top + 34f * k); cubicTo(hoja.left + 30f * k, hoja.top + 20f * k, hoja.left + 60f * k, hoja.top + 48f * k, hoja.right, hoja.top + 30f * k) },
                        t(Color(0xFF7FA0B0), oscuro), style = Stroke(5f * k))
                }
                drawRect(e.c2.copy(alpha = 0.6f), Offset(hoja.left + 2.5f * k, hoja.top + 2.5f * k), Size(hoja.width - 5f * k, hoja.height - 5f * k), style = Stroke(0.8f * k))
                listOf(Offset(c.x - 30f * k, c.y + 4f * k), Offset(c.x + 2f * k, c.y - 8f * k), Offset(c.x + 32f * k, c.y + 8f * k)).forEach { p ->
                    drawLine(sombra, Offset(p.x + 1f * k, p.y + 2f * k), Offset(p.x + 6f * k, p.y + 12f * k), 1.6f * k, StrokeCap.Round)
                    escarapela(p, 8.6f * k, oscuro, cintas = true)
                    drawCircle(t(HIERRO_LUZ, oscuro), 1.6f * k, p)
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    //  Notas: el tablón de la sección
    // ---------------------------------------------------------------------

    /** Un farol de calle (reverbero) en su brazo de hierro; de noche, encendido. */
    private fun DrawScope.reverbero(c: Offset, alto: Float, oscuro: Boolean, u: Float, lado: Float) {
        val k = alto / 10f
        halo(c, alto * (if (oscuro) 3.2f else 1.6f), LLAMA, if (oscuro) 0.45f else 0.12f)
        // El brazo de hierro con su voluta.
        drawLine(t(HIERRO, oscuro), Offset(c.x, c.y - 5.4f * k), Offset(c.x - lado * 5f * k, c.y - 5.4f * k), 0.6f * k)
        drawArc(t(HIERRO, oscuro), if (lado > 0) 90f else -90f, 180f, false, Offset(c.x - lado * 5f * k - 1.6f * k, c.y - 5.4f * k), Size(3.2f * k, 3.2f * k), style = Stroke(0.5f * k))
        drawLine(t(HIERRO, oscuro), Offset(c.x, c.y - 5.4f * k), Offset(c.x, c.y - 4f * k), 0.5f * k)
        val cuerpo = Path().apply { moveTo(c.x - 3.4f * k, c.y - 3f * k); lineTo(c.x + 3.4f * k, c.y - 3f * k); lineTo(c.x + 2.2f * k, c.y + 3.4f * k); lineTo(c.x - 2.2f * k, c.y + 3.4f * k); close() }
        drawPath(cuerpo, if (oscuro) LLAMA.copy(alpha = 0.92f) else Color(0xFFEDE6CE))
        if (oscuro) drawPath(cuerpo, Brush.radialGradient(listOf(LLAMA_CENTRO, LLAMA), c, 3f * k))
        drawPath(cuerpo, t(HIERRO, oscuro), style = Stroke(0.45f * k))
        drawLine(t(HIERRO, oscuro), Offset(c.x, c.y - 3f * k), Offset(c.x, c.y + 3.4f * k), 0.35f * k)
        drawPath(Path().apply { moveTo(c.x - 4f * k, c.y - 3f * k); lineTo(c.x, c.y - 5f * k); lineTo(c.x + 4f * k, c.y - 3f * k); close() }, t(HIERRO, oscuro))
        drawRect(t(HIERRO, oscuro), Offset(c.x - 2.4f * k, c.y + 3.4f * k), Size(4.8f * k, 0.8f * k))
    }

    /** El cubo de engrudo de madera con sus aros y la brocha del pegador de carteles. */
    private fun DrawScope.cuboEngrudo(c: Offset, alto: Float, oscuro: Boolean, u: Float) {
        val k = alto / 10f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - 5f * k, c.y + 4.2f * k), Size(10.4f * k, 2f * k))
        val cubo = Path().apply { moveTo(c.x - 4.4f * k, c.y - 3f * k); lineTo(c.x + 4.4f * k, c.y - 3f * k); lineTo(c.x + 3.6f * k, c.y + 5f * k); lineTo(c.x - 3.6f * k, c.y + 5f * k); close() }
        drawPath(cubo, Brush.horizontalGradient(listOf(t(PINO_LUZ, oscuro), t(PINO, oscuro), t(PINO_OSC, oscuro)), c.x - 4.4f * k, c.x + 4.4f * k))
        clipPath(cubo) { for (j in -3..3) drawLine(Color.Black.copy(alpha = 0.2f), Offset(c.x + j * 1.3f * k, c.y - 3f * k), Offset(c.x + j * 1.1f * k, c.y + 5f * k), 0.2f * k) }
        listOf(-1.4f, 3f).forEach { dy -> drawLine(t(HIERRO, oscuro), Offset(c.x - 4.2f * k + (dy + 3f) * 0.1f * k, c.y + dy * k), Offset(c.x + 4.2f * k - (dy + 3f) * 0.1f * k, c.y + dy * k), 0.6f * k) }
        drawOval(t(Color(0xFFE8E0C8), oscuro), Offset(c.x - 4.2f * k, c.y - 3.8f * k), Size(8.4f * k, 1.8f * k))
        // La brocha metida en el cubo.
        drawLine(t(NOGAL_LUZ, oscuro), Offset(c.x + 1f * k, c.y - 3f * k), Offset(c.x + 5f * k, c.y - 9f * k), 0.9f * k, StrokeCap.Round)
        drawRoundRect(t(HIERRO_LUZ, oscuro), Offset(c.x - 0.4f * k, c.y - 3.6f * k), Size(2.6f * k, 1.4f * k), CornerRadius(0.4f * k))
        drawArc(t(HIERRO, oscuro), 180f, 180f, false, Offset(c.x - 4.4f * k, c.y - 7f * k), Size(8.8f * k, 8f * k), style = Stroke(0.35f * k))
    }

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.NOTAS)
        val piedra = n(PIEDRA, oscuro); val luz = n(PIEDRA_LUZ, oscuro); val junta = n(PIEDRA_JUNTA, oscuro)
        // El muro de sillares de la calle.
        drawRect(piedra, Offset.Zero, size)
        val fila = 5f * u
        var y = 0f; var k = 0
        while (y < h) {
            drawLine(junta, Offset(0f, y), Offset(w, y), 0.3f * u)
            var x = if (k % 2 == 0) 0f else 7f * u
            while (x < w) { drawLine(junta, Offset(x, y), Offset(x, y + fila), 0.3f * u); drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.15f), Offset(x + 0.4f * u, y + 0.4f * u), Offset(x + 13f * u, y + 0.4f * u), 0.25f * u); x += 14f * u }
            y += fila; k++
        }
        // El marco de madera verde con sus escuadras de hierro.
        val marco = Rect(w * 0.025f, a.top - h * 0.05f, w * 0.975f, a.bottom + h * 0.035f)
        sombraSuave(marco, 0f, 3f * u, if (oscuro) 0.5f else 0.3f)
        val verde = VERDE_TABLON
        tabla(Rect(marco.left, marco.top, a.left, marco.bottom), oscuro, u, e.semilla + 1, vertical = true, tono = verde)
        tabla(Rect(a.right, marco.top, marco.right, marco.bottom), oscuro, u, e.semilla + 2, vertical = true, tono = verde)
        tabla(Rect(a.left, marco.top, a.right, a.top), oscuro, u, e.semilla + 3, tono = verde)
        tabla(Rect(a.left, a.bottom, a.right, marco.bottom), oscuro, u, e.semilla + 4, tono = verde)
        // La pintura saltada en los cantos.
        val rnd = Random(e.semilla.toLong() + 9)
        repeat(14) {
            val lado = rnd.nextInt(4)
            val p = when (lado) {
                0 -> Offset(marco.left + (a.left - marco.left) * rnd.nextFloat(), marco.top + marco.height * rnd.nextFloat())
                1 -> Offset(a.right + (marco.right - a.right) * rnd.nextFloat(), marco.top + marco.height * rnd.nextFloat())
                2 -> Offset(marco.left + marco.width * rnd.nextFloat(), marco.top + (a.top - marco.top) * rnd.nextFloat())
                else -> Offset(marco.left + marco.width * rnd.nextFloat(), a.bottom + (marco.bottom - a.bottom) * rnd.nextFloat())
            }
            drawOval(t(PINO, oscuro).copy(alpha = 0.7f), p, Size((0.6f + rnd.nextFloat()) * u, (0.4f + 0.6f * rnd.nextFloat()) * u))
        }
        drawRect(Color.Black.copy(alpha = 0.45f), marco.topLeft, marco.size, style = Stroke(0.4f * u))
        drawRect(t(VERDE_TABLON_LUZ, oscuro), Offset(marco.left + 1.2f * u, marco.top + 1.2f * u), Size(marco.width - 2.4f * u, marco.height - 2.4f * u), style = Stroke(0.3f * u))
        val esc = 6f * u
        listOf(marco.topLeft to Offset(1f, 1f), Offset(marco.right, marco.top) to Offset(-1f, 1f), Offset(marco.left, marco.bottom) to Offset(1f, -1f), marco.bottomRight to Offset(-1f, -1f)).forEach { (p, s) ->
            val q = Offset(p.x + s.x * 0.6f * u, p.y + s.y * 0.6f * u)
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(q.x + 0.3f * u, q.y + 0.5f * u), Offset(q.x + s.x * esc + 0.3f * u, q.y + 0.5f * u), 1.2f * u)
            drawLine(t(HIERRO, oscuro), q, Offset(q.x + s.x * esc, q.y), 1.2f * u)
            drawLine(t(HIERRO, oscuro), q, Offset(q.x, q.y + s.y * esc), 1.2f * u)
            clavo(Offset(q.x + s.x * esc * 0.7f, q.y), 0.4f * u, oscuro); clavo(Offset(q.x, q.y + s.y * esc * 0.7f), 0.4f * u, oscuro)
        }
        // El yeso (el nicho): liso, con los restos muy pálidos de carteles viejos.
        val yeso = if (oscuro) mezcla(e.fondo, Color(0xFF45423A), 0.45f) else mezcla(e.fondo, YESO, 0.45f)
        drawRect(yeso, a.topLeft, a.size)
        clipRect(a.left, a.top, a.right, a.bottom) {
            val resto = (if (oscuro) Color.White else Color(0xFF8A7A5A)).copy(alpha = if (oscuro) 0.025f else 0.06f)
            val rr = Random(e.semilla.toLong() + 31)
            repeat(5) {
                val x = a.left + a.width * rr.nextFloat(); val yy = a.top + a.height * rr.nextFloat()
                drawRect(resto, Offset(x, yy), Size(a.width * (0.1f + 0.12f * rr.nextFloat()), a.height * (0.08f + 0.1f * rr.nextFloat())))
            }
            val grieta = Color.Black.copy(alpha = if (oscuro) 0.12f else 0.07f)
            drawPath(Path().apply { moveTo(a.right - a.width * 0.08f, a.top); lineTo(a.right - a.width * 0.1f, a.top + a.height * 0.06f); lineTo(a.right - a.width * 0.07f, a.top + a.height * 0.1f); lineTo(a.right - a.width * 0.09f, a.top + a.height * 0.16f) }, grieta, style = Stroke(0.25f * u))
        }
        sombraInterior(a, 2.4f * u, if (oscuro) 0.32f else 0.14f)
        // El remate: el frontón de madera verde con la escarapela entre banderas cruzadas.
        val rr = minOf(marco.top * 0.55f, w * 0.07f).coerceAtLeast(2f * u)
        val fr = Path().apply { moveTo(w * 0.28f, marco.top + 0.5f * u); lineTo(w / 2, marco.top - rr * 1.15f); lineTo(w * 0.72f, marco.top + 0.5f * u); close() }
        drawPath(Path().apply { addPath(fr, Offset(0.5f * u, 0.9f * u)) }, Color.Black.copy(alpha = 0.3f))
        drawPath(fr, t(verde, oscuro))
        clipPath(fr) { vetas(Rect(w * 0.28f, marco.top - rr * 1.2f, w * 0.72f, marco.top + 1f * u), t(VETA, oscuro).copy(alpha = 0.35f), e.semilla + 5, u) }
        drawPath(fr, t(VERDE_TABLON_LUZ, oscuro), style = Stroke(0.5f * u))
        bandera(Offset(w / 2 - rr * 0.5f, marco.top + rr * 0.1f), rr * 1.9f, -40f, oscuro, u)
        bandera(Offset(w / 2 + rr * 0.5f, marco.top + rr * 0.1f), rr * 1.9f, 40f, oscuro, u)
        escarapela(Offset(w / 2, marco.top - rr * 0.32f), rr * 0.5f, oscuro, cintas = true)
        // Los dos faroles de calle a los lados del remate.
        listOf(w * 0.12f to 1f, w * 0.88f to -1f).forEach { (x, s) -> reverbero(Offset(x, marco.top - rr * 0.55f), rr * 1.1f, oscuro, u, s) }
        // La cornisa de piedra de abajo con el cubo de engrudo y su brocha.
        val cor = Rect(0f, marco.bottom + 0.8f * u, w, h)
        drawRect(Brush.verticalGradient(listOf(luz, piedra, mezcla(piedra, Color.Black, 0.25f)), cor.top, cor.bottom), cor.topLeft, cor.size)
        drawRect(luz, Offset(0f, cor.top), Size(w, 1f * u))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(0f, cor.top + 1f * u), Size(w, 0.4f * u))
        val ch = cor.height
        if (ch > 4f * u) {
            cuboEngrudo(Offset(w * 0.86f, cor.top - ch * 0.15f), minOf(ch * 1.05f, 10f * u), oscuro, u)
            // Un fajo de carteles por pegar, enrollado.
            val gr = ch * 0.38f
            val ry = cor.top + ch * 0.45f
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.1f + 0.5f * u, ry - gr / 2 + 0.8f * u), Size(w * 0.24f, gr), CornerRadius(gr / 2))
            drawRoundRect(Brush.verticalGradient(listOf(t(PAPEL, oscuro), t(PAPEL_VIEJO, oscuro)), ry - gr / 2, ry + gr / 2), Offset(w * 0.1f, ry - gr / 2), Size(w * 0.24f, gr), CornerRadius(gr / 2))
            drawOval(t(PAPEL_VIEJO, oscuro), Offset(w * 0.34f - gr * 0.3f, ry - gr / 2), Size(gr * 0.6f, gr))
            listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(t(col, oscuro), Offset(w * 0.2f + i * 0.6f * u, ry - gr / 2), Size(0.6f * u, gr)) }
        }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        if (r0.width < 1f || r0.height < 1f) return
        val k = minOf(r0.width, r0.height) / 100f
        val r = Rect(r0.left + 2.5f * k, r0.top + 3f * k, r0.right - 2.5f * k, r0.bottom - 2f * k)
        val papel = when (e.tipo) {
            "idea" -> if (oscuro) P_PASQUIN_N else P_PASQUIN
            "proposito" -> if (oscuro) P_DECRETO_N else P_DECRETO
            "recordatorio" -> if (oscuro) P_CALENDARIO_N else P_CALENDARIO
            else -> if (oscuro) P_QUEJAS_N else P_QUEJAS
        }
        // La silueta: el pasquín lleva el borde de abajo rasgado; el resto, recto.
        val forma = if (e.tipo == "idea") Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 2f * k)
            var x = r.right; var j = 0
            while (x > r.left) { lineTo(x, r.bottom - 3f * k * azar(e.indice + 11, j)); x -= 3.6f * k; j++ }
            lineTo(r.left, r.bottom - 1f * k); close()
        } else Path().apply { addRect(r) }
        drawPath(Path().apply { addPath(forma, Offset(1.2f * k, 2f * k)) }, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f))
        drawPath(forma, papel)
        val tinta = if (oscuro) Color.White.copy(alpha = 0.18f) else Color(0xFF1C1A17).copy(alpha = 0.3f)
        val azul = if (oscuro) Color(0xFF8FAEE8) else AZUL
        val rojo = if (oscuro) Color(0xFFE5525E) else ROJO
        clipPath(forma) {
            // El papel de trapo: un poco más oscuro hacia los bordes.
            drawRect(Brush.radialGradient(listOf(Color.Transparent, (if (oscuro) Color.Black else PAPEL_VIEJO).copy(alpha = if (oscuro) 0.28f else 0.35f)), r.center, maxOf(r.width, r.height) * 0.8f), r.topLeft, r.size)
            when (e.tipo) {
                "idea" -> {
                    // El pasquín: la cinta tricolor arriba y el doble filete de imprenta.
                    drawRect(azul.copy(alpha = 0.85f), r.topLeft, Size(r.width / 3, 3f * k))
                    drawRect(Color.White.copy(alpha = if (oscuro) 0.5f else 0.9f), Offset(r.left + r.width / 3, r.top), Size(r.width / 3, 3f * k))
                    drawRect(rojo.copy(alpha = 0.85f), Offset(r.left + r.width * 2 / 3, r.top), Size(r.width / 3, 3f * k))
                    drawLine(tinta.copy(alpha = tinta.alpha * 2f), Offset(r.left + 6f * k, r.top + 8f * k), Offset(r.right - 6f * k, r.top + 8f * k), 1f * k)
                    drawLine(tinta.copy(alpha = tinta.alpha * 2f), Offset(r.left + 6f * k, r.top + 10.4f * k), Offset(r.right - 6f * k, r.top + 10.4f * k), 0.35f * k)
                }
                "pensamiento" -> {
                    // La hoja del cuaderno de quejas: el cosido a la izquierda, el margen rojo y los renglones, apenas.
                    var yy = r.top + 4f * k
                    while (yy < r.bottom - 2f * k) { drawLine(tinta.copy(alpha = tinta.alpha * 1.4f), Offset(r.left + 2f * k, yy), Offset(r.left + 2f * k, yy + 2.6f * k), 0.5f * k); yy += 5f * k }
                    drawLine(rojo.copy(alpha = 0.45f), Offset(r.left + 7f * k, r.top), Offset(r.left + 7f * k, r.bottom), 0.4f * k)
                    var ly = r.top + 14f * k
                    while (ly < r.bottom - 3f * k) { drawLine(azul.copy(alpha = if (oscuro) 0.14f else 0.12f), Offset(r.left + 7f * k, ly), Offset(r.right, ly), 0.3f * k); ly += 8.6f * k }
                }
                "proposito" -> {
                    // El decreto: el filete grueso y el fino arriba, y el sello redondo del comité abajo.
                    drawLine(tinta.copy(alpha = tinta.alpha * 2.2f), Offset(r.left + 5f * k, r.top + 6f * k), Offset(r.right - 5f * k, r.top + 6f * k), 1.4f * k)
                    drawLine(tinta.copy(alpha = tinta.alpha * 2.2f), Offset(r.left + 5f * k, r.top + 9f * k), Offset(r.right - 5f * k, r.top + 9f * k), 0.4f * k)
                    val s = Offset(r.right - 10f * k, r.bottom - 8f * k)
                    drawCircle(azul.copy(alpha = 0.6f), 5f * k, s, style = Stroke(0.6f * k))
                    drawCircle(azul.copy(alpha = 0.6f), 3.4f * k, s, style = Stroke(0.35f * k))
                    drawCircle(azul.copy(alpha = 0.5f), 1.2f * k, s)
                }
                "recordatorio" -> {
                    // La hoja del calendario republicano: la banda azul y roja de arriba con sus perforaciones.
                    drawRect(azul.copy(alpha = 0.85f), r.topLeft, Size(r.width * 0.3f, 7f * k))
                    drawRect(rojo.copy(alpha = 0.85f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, 7f * k))
                    drawRect(tinta, Offset(r.left + r.width * 0.3f, r.top), Size(r.width * 0.4f, 7f * k))
                    var px = r.left + 3f * k
                    while (px < r.right - 2f * k) { drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.25f), 0.6f * k, Offset(px, r.top + 9f * k)); px += 3f * k }
                    drawLine(tinta, Offset(r.left + 4f * k, r.bottom - 6f * k), Offset(r.left + 26f * k, r.bottom - 6f * k), 0.4f * k)
                }
                else -> {}
            }
        }
        // Las esquinas pegadas con engrudo, un poco más oscuras.
        val engrudo = (if (oscuro) Color.Black else Color(0xFFB8A472)).copy(alpha = if (oscuro) 0.22f else 0.22f)
        listOf(Offset(r.left + 3f * k, r.bottom - 3f * k), Offset(r.right - 3f * k, r.bottom - 3f * k)).forEach { drawCircle(engrudo, 3f * k, it) }
        // El clavo; en las fijadas, una escarapela clavada y el sello de lacre.
        val cl = Offset(r.center.x, r.top + (if (e.tipo == "idea" || e.tipo == "recordatorio") 4.4f else 4f) * k)
        if (e.fijada) {
            escarapela(Offset(r.left + 9f * k, r.top + 9f * k), 5.6f * k, oscuro, cintas = true)
            val s = Offset(r.left + 11f * k, r.bottom - 9f * k)
            val lacre = Path().apply {
                for (j in 0 until 12) {
                    val ang = j / 12.0 * 2 * Math.PI
                    val rr = 5f * k * (0.88f + 0.18f * azar(e.indice + 3, j))
                    val p = Offset(s.x + rr * cos(ang).toFloat(), s.y + rr * sin(ang).toFloat())
                    if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(Path().apply { addPath(lacre, Offset(0.6f * k, 0.9f * k)) }, Color.Black.copy(alpha = 0.3f))
            drawPath(lacre, mezcla(ROJO_OSC, e.acento, 0.2f))
            drawCircle(mezcla(ROJO_OSC, Color.Black, 0.35f), 3f * k, s, style = Stroke(0.5f * k))
            drawLine(mezcla(ROJO_OSC, Color.Black, 0.35f), Offset(s.x, s.y - 2f * k), Offset(s.x, s.y + 2f * k), 0.6f * k)
            drawCircle(Color.White.copy(alpha = 0.2f), 1f * k, Offset(s.x - 2f * k, s.y - 2f * k))
        } else {
            clavo(cl, 2.2f * k, oscuro, HIERRO)
        }
    }

    /** La tinta de las notas: tinta de imprenta sobre el papel de trapo; de noche, el papel a la luz de la antorcha y la tinta clara. */
    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0EADB) else Color(0xFF1C1A17)
}
