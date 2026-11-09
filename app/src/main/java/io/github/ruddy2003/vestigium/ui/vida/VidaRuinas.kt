package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ciudad verde: Vida en una ciudad que el bosque recuperó, con los materiales de su librería caída y de su estantería
 * rescatada: hormigón con manchas, grietas y varillas oxidadas al aire, asfalto con la pintura vial gastada, tablas
 * rescatadas con clavos y pintura descascarada, ángulo ranurado con óxido, hojalata, cartón, hiedra, musgo, pasto y
 * hojarasca. De día entra la luz por los huecos; de noche, la luna, las luciérnagas, las velas y la guirnalda de focos.
 * - Social: la plaza donde se junta tu gente, vista desde arriba. El asfalto agrietado con musgo y pasto, el paso de cebra
 *   y la doble raya amarilla desteñidos, el charco con el cielo, la alcantarilla, el tambo con la fogata, la guirnalda de
 *   focos de lado a lado; alrededor, asientos rescatados (llantas, cajas de fruta, bloques y un bidón) y en el centro una
 *   puerta vieja tendida como mesa, con su pintura descascarada, sus bisagras y su pomo (el grafo va en el tablero).
 * - Mapas: la mesa de tablas sobre cajas de fruta, como la de su libreta, contra el muro roto con la ventana al cielo, la
 *   rama que entra y la hiedra que cuelga. El mapa es un pliego de borde rasgado sujeto con piedras; alrededor, la vela en
 *   su lata, la brújula, la navaja, el frasco con un brote y la linterna de manivela; abajo, tierra, pasto y hojarasca.
 * - Otras vidas: la estantería de ángulo ranurado bajo la losa rota. El hueco con el cielo y las varillas, la hiedra que
 *   cuelga, y arriba las latas con plantas, los ladrillos con musgo, el frasco de luciérnagas y el farol; cinco tramos
 *   iguales contra el hormigón visto con sus pasadores; el riel de abajo con su óxido, y el suelo con helechos y cascajo.
 * - Íconos: el rascacielos roto con un árbol en la azotea, la placa de lámina con la capa de óxido que se raspa con una llave
 *   vieja, y el mapa arrugado con banderitas de trapo.
 * - Notas: el tablero de corcho rescatado en su marco de tablas clavadas, con el farol de lata colgado de un clavo, la hiedra
 *   y el musgo, y la repisa con el lápiz, los clavos y una piña. Las notas son papeles rescatados: el cartón rasgado (idea),
 *   la hoja de cuaderno arrancada (pensamiento), la página de un libro viejo con su cerco de agua (propósito) y la etiqueta
 *   de cartón con su cordel y su marca de pintura (recordatorio). Las fijadas van con cinta plateada y una hoja de trébol;
 *   las demás, con un clavo oxidado.
 */
internal object VidaRuinas : DisenoVida {
    private val HORMIGON = Color(0xFFA8A69C)
    private val HORMIGON_LUZ = Color(0xFFC6C4BA)
    private val HORMIGON_OSC = Color(0xFF6E6C64)
    private val ASFALTO = Color(0xFF5E605A)
    private val OXIDO = Color(0xFFA34A1E)
    private val OXIDO_OSC = Color(0xFF6A2E12)
    private val OXIDO_LUZ = Color(0xFFC2703A)
    private val HIEDRA = Color(0xFF3E6B2A)
    private val HIEDRA_LUZ = Color(0xFF6A9A3E)
    private val MUSGO = Color(0xFF55722A)
    private val MUSGO_LUZ = Color(0xFF86A33C)
    private val MADERA = Color(0xFF7A5A3C)
    private val MADERA_LUZ = Color(0xFF9A7852)
    private val MADERA_OSC = Color(0xFF4A3524)
    private val VETA = Color(0xFF3A2A1C)
    private val HOJALATA = Color(0xFFB8BCB8)
    private val HOJALATA_OSC = Color(0xFF7E8480)
    private val ACERO = Color(0xFF6E7470)
    private val ACERO_OSC = Color(0xFF454A47)
    private val CINTA = Color(0xFFE8DCB0)
    private val CINTA_GRIS = Color(0xFFA4A7A3)
    private val PAPEL = Color(0xFFE6E3D6)
    private val PAPEL_VIEJO = Color(0xFFE3D6B0)
    private val CARTON = Color(0xFFC9B48E)
    private val CORCHO = Color(0xFFB89468)
    private val TINTA = Color(0xFF1F2420)
    private val TIERRA = Color(0xFF4A3B2A)
    private val PINTURA_VIAL = Color(0xFFE6E0CC)
    private val AMARILLO_VIAL = Color(0xFFD9B23A)
    private val PUERTA = Color(0xFF6E8A86)
    private val PANEL = Color(0xFFDCE2DA)
    private val NEUMATICO = Color(0xFF2A2B28)
    private val LADRILLO = Color(0xFF9A4E36)
    private val CIELO = Color(0xFFC4DCE6)
    private val CIELO_NOCHE = Color(0xFF0C1626)
    private val LUNA = Color(0xFFEDEBD8)
    private val LUCIERNAGA = Color(0xFFE6FF7A)
    private val LLAMA = Color(0xFFFFB347)
    private val HIERRO_TALLO = Color(0xFF4F6A2A)
    private val NOCHE = Color(0xFF0B110E)

    /** La hojarasca: ocre, pardo, naranja seco y verde. */
    private val HOJARASCA = listOf(Color(0xFF8A6A30), Color(0xFF6E5A2A), Color(0xFFA0702E), Color(0xFF3D6A2A))

    private fun DrawScope.u() = size.minDimension / 100f

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en oscuro: la ciudad de noche, bajo la luna. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.52f) else c

    /** El fondo del nicho: el material apenas mezclado con el fondo del esquema, para que se lea lo de encima. */
    private fun nicho(fondo: Color, material: Color, oscuro: Boolean): Color = mezcla(fondo, material, if (oscuro) 0.16f else 0.32f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.19f, 0.25f, 0.81f, 0.77f)
        MuebleVida.MAPAS -> Rect(0.22f, 0.31f, 0.78f, 0.79f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.058f, 0.36f, 0.942f, 0.84f)
        MuebleVida.NOTAS -> Rect(0.08f, 0.17f, 0.92f, 0.86f)
    }

    // ---------------------------------------------------------------- Ayudas de dibujo

    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float, radio: Float = 0f) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.3f), CornerRadius(radio + e))
        }
    }

    /** Una mancha radial suave (humedad, óxido, luz). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** La veta de la madera: líneas onduladas a lo largo de la tabla. */
    private fun DrawScope.vetas(r: Rect, color: Color, rnd: Random, paso: Float, vertical: Boolean = false, grosor: Float = 1f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / paso).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + rnd.nextFloat()) / n
                val amp = paso * (0.15f + 0.5f * rnd.nextFloat())
                val fase = rnd.nextFloat() * 6.28f
                val ondas = 0.8f + 1.8f * rnd.nextFloat()
                val p = Path()
                for (s in 0..16) {
                    val a = largo * s / 16f
                    val b = base + amp * sin(fase + ondas * 6.28f * s / 16f)
                    if (vertical) { if (s == 0) p.moveTo(r.left + b, r.top + a) else p.lineTo(r.left + b, r.top + a) } else { if (s == 0) p.moveTo(r.left + a, r.top + b) else p.lineTo(r.left + a, r.top + b) }
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.35f + 0.65f * rnd.nextFloat())), style = Stroke(grosor * (0.5f + rnd.nextFloat())))
            }
        }
    }

    /** Una tabla rescatada: el degradado, la veta gruesa, un nudo y, si se pide, restos de pintura descascarada. */
    private fun DrawScope.tabla(r: Rect, base: Color, oscuro: Boolean, rnd: Random, u: Float, vertical: Boolean = false, pintura: Color? = null) {
        val b = t(base, oscuro)
        if (vertical) drawRect(Brush.horizontalGradient(listOf(mezcla(b, Color.White, 0.1f), b, mezcla(b, Color.Black, 0.18f)), r.left, r.right), r.topLeft, r.size)
        else drawRect(Brush.verticalGradient(listOf(mezcla(b, Color.White, 0.1f), b, mezcla(b, Color.Black, 0.18f)), r.top, r.bottom), r.topLeft, r.size)
        vetas(r, t(VETA, oscuro).copy(alpha = 0.5f), rnd, 1.3f * u, vertical, 0.3f * u)
        val nc = Offset(r.left + r.width * (0.2f + 0.6f * rnd.nextFloat()), r.top + r.height * (0.25f + 0.5f * rnd.nextFloat()))
        val (rx, ry) = if (vertical) 0.8f * u to 2f * u else 2f * u to 0.8f * u
        for (k in 0 until 3) { val f = 1f - k * 0.3f; drawOval(t(VETA, oscuro).copy(alpha = 0.4f), Offset(nc.x - rx * f, nc.y - ry * f), Size(rx * 2 * f, ry * 2 * f), style = Stroke(0.2f * u)) }
        drawOval(t(VETA, oscuro).copy(alpha = 0.6f), Offset(nc.x - rx * 0.25f, nc.y - ry * 0.25f), Size(rx * 0.5f, ry * 0.5f))
        if (pintura != null) {
            // La pintura vieja cubre la tabla y se descascara en tiras a lo largo de la veta, dejando ver la madera.
            val huecos = Path()
            val n = ((if (vertical) r.height else r.width) / (11f * u)).toInt().coerceIn(3, 14)
            repeat(n) {
                val c = Offset(r.left + r.width * rnd.nextFloat(), r.top + r.height * rnd.nextFloat())
                val largo = (3f + 7f * rnd.nextFloat()) * u
                val ancho = (0.6f + 1.2f * rnd.nextFloat()) * u
                val (ex, ey) = if (vertical) ancho to largo else largo to ancho
                huecos.moveTo(c.x - ex, c.y)
                huecos.quadraticTo(c.x - ex * 0.5f, c.y - ey * (0.8f + 0.5f * rnd.nextFloat()), c.x + ex * 0.2f, c.y - ey * 0.7f)
                huecos.quadraticTo(c.x + ex * 0.8f, c.y - ey * 0.9f, c.x + ex, c.y)
                huecos.quadraticTo(c.x + ex * 0.4f, c.y + ey * (0.8f + 0.5f * rnd.nextFloat()), c.x - ex * 0.1f, c.y + ey * 0.6f)
                huecos.quadraticTo(c.x - ex * 0.7f, c.y + ey * 0.9f, c.x - ex, c.y)
                huecos.close()
            }
            val pin = t(pintura, oscuro)
            clipRect(r.left, r.top, r.right, r.bottom) {
                clipPath(huecos, ClipOp.Difference) {
                    if (vertical) drawRect(Brush.horizontalGradient(listOf(mezcla(pin, Color.White, 0.12f), pin, mezcla(pin, Color.Black, 0.15f)), r.left, r.right), r.topLeft, r.size)
                    else drawRect(Brush.verticalGradient(listOf(mezcla(pin, Color.White, 0.12f), pin, mezcla(pin, Color.Black, 0.15f)), r.top, r.bottom), r.topLeft, r.size)
                    vetas(r, Color.Black.copy(alpha = 0.12f), rnd, 2f * u, vertical, 0.25f * u)
                }
                drawPath(huecos, Color.Black.copy(alpha = 0.25f), style = Stroke(0.25f * u))
            }
        }
        drawRect(Color.Black.copy(alpha = 0.35f), r.topLeft, r.size, style = Stroke(0.3f * u))
    }

    /** Un clavo oxidado: la cabeza con su brillo y el chorrete de óxido que baja. */
    private fun DrawScope.clavo(c: Offset, r: Float, oscuro: Boolean, chorrete: Boolean = true) {
        if (chorrete) drawRect(Brush.verticalGradient(listOf(t(OXIDO, oscuro).copy(alpha = 0.5f), Color.Transparent), c.y, c.y + r * 6f), Offset(c.x - r * 0.45f, c.y), Size(r * 0.9f, r * 6f))
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + r * 0.3f, c.y + r * 0.4f))
        drawCircle(Brush.radialGradient(listOf(t(OXIDO_LUZ, oscuro), t(OXIDO, oscuro), t(OXIDO_OSC, oscuro)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.4f), r, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.12f else 0.3f), r * 0.35f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
    }

    /** Una hoja de hiedra: tres lóbulos con su punta, girada [giro] grados. */
    private fun DrawScope.hojaHiedra(c: Offset, s: Float, giro: Float, color: Color) {
        rotate(giro, c) {
            val p = Path().apply {
                moveTo(c.x, c.y + s * 0.45f)
                quadraticTo(c.x - s * 0.75f, c.y + s * 0.3f, c.x - s * 0.55f, c.y - s * 0.1f)
                quadraticTo(c.x - s * 0.35f, c.y - s * 0.15f, c.x - s * 0.2f, c.y - s * 0.2f)
                quadraticTo(c.x - s * 0.15f, c.y - s * 0.65f, c.x, c.y - s * 0.7f)
                quadraticTo(c.x + s * 0.15f, c.y - s * 0.65f, c.x + s * 0.2f, c.y - s * 0.2f)
                quadraticTo(c.x + s * 0.35f, c.y - s * 0.15f, c.x + s * 0.55f, c.y - s * 0.1f)
                quadraticTo(c.x + s * 0.75f, c.y + s * 0.3f, c.x, c.y + s * 0.45f)
                close()
            }
            drawPath(p, color)
            drawLine(mezcla(color, Color.Black, 0.3f), Offset(c.x, c.y + s * 0.4f), Offset(c.x, c.y - s * 0.5f), s * 0.06f)
        }
    }

    /** Una guía de hiedra por los puntos dados, con sus hojas alternadas. */
    private fun DrawScope.hiedra(puntos: List<Offset>, s: Float, oscuro: Boolean, rnd: Random) {
        val p = Path().apply {
            moveTo(puntos[0].x, puntos[0].y)
            for (k in 1 until puntos.size) {
                val a = puntos[k - 1]; val b = puntos[k]
                quadraticTo(a.x + (b.x - a.x) * 0.5f + (rnd.nextFloat() - 0.5f) * s * 2f, a.y + (b.y - a.y) * 0.5f + (rnd.nextFloat() - 0.5f) * s * 2f, b.x, b.y)
            }
        }
        drawPath(p, t(Color(0xFF4A3A26), oscuro), style = Stroke(s * 0.12f, cap = StrokeCap.Round))
        for (k in 1 until puntos.size) {
            val a = puntos[k - 1]; val b = puntos[k]
            val pasos = (((b - a).getDistance()) / (s * 0.9f)).toInt().coerceIn(1, 30)
            for (j in 0 until pasos) {
                val f = (j + 0.5f) / pasos
                val q = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
                val lado = if ((j + k) % 2 == 0) 1f else -1f
                hojaHiedra(Offset(q.x + lado * s * 0.45f, q.y + s * 0.1f), s * (0.75f + 0.4f * rnd.nextFloat()), lado * (20f + 30f * rnd.nextFloat()), t(if (rnd.nextFloat() < 0.4f) HIEDRA_LUZ else HIEDRA, oscuro))
            }
        }
    }

    /** Una mata de pasto vista de frente: hojas finas que salen de [b]. */
    private fun DrawScope.pasto(b: Offset, alto: Float, oscuro: Boolean, rnd: Random) {
        for (k in 0 until 6) {
            val a = Math.toRadians((-90.0 + (k - 2.5) * 16.0 + (rnd.nextFloat() - 0.5f) * 10.0))
            val l = alto * (0.6f + 0.4f * rnd.nextFloat())
            val fin = Offset(b.x + l * cos(a).toFloat(), b.y + l * sin(a).toFloat())
            val p = Path().apply { moveTo(b.x - alto * 0.04f, b.y); quadraticTo(b.x + (fin.x - b.x) * 0.3f, b.y + (fin.y - b.y) * 0.6f, fin.x, fin.y); quadraticTo(b.x + (fin.x - b.x) * 0.4f, b.y + (fin.y - b.y) * 0.5f, b.x + alto * 0.04f, b.y); close() }
            drawPath(p, t(if (k % 2 == 0) MUSGO_LUZ else HIEDRA_LUZ, oscuro))
        }
    }

    /** Una mata de pasto vista desde arriba: hojas que salen en estrella. */
    private fun DrawScope.pastoArriba(c: Offset, r: Float, oscuro: Boolean, rnd: Random) {
        for (k in 0 until 9) {
            val a = Math.toRadians(k * 40.0 + rnd.nextFloat() * 20.0)
            val l = r * (0.6f + 0.4f * rnd.nextFloat())
            drawLine(t(if (k % 2 == 0) MUSGO_LUZ else HIEDRA, oscuro), c, Offset(c.x + l * cos(a).toFloat(), c.y + l * sin(a).toFloat()), r * 0.12f, StrokeCap.Round)
        }
    }

    /** Musgo: puntitos de dos verdes alrededor de [c], a lo ancho de [ancho]. */
    private fun DrawScope.musgo(c: Offset, ancho: Float, alto: Float, oscuro: Boolean, rnd: Random, n: Int = 14) {
        for (k in 0 until n) {
            val p = Offset(c.x + (rnd.nextFloat() - 0.5f) * ancho, c.y + (rnd.nextFloat() - 0.5f) * alto)
            drawCircle(t(if (k % 3 == 0) MUSGO_LUZ else MUSGO, oscuro), minOf(ancho, alto) * (0.18f + 0.22f * rnd.nextFloat()), p)
        }
    }

    /** Una hoja seca caída. */
    private fun DrawScope.hojaSeca(c: Offset, s: Float, giro: Float, color: Color) {
        rotate(giro, c) {
            val p = Path().apply { moveTo(c.x - s, c.y); quadraticTo(c.x - s * 0.2f, c.y - s * 0.55f, c.x + s, c.y); quadraticTo(c.x - s * 0.2f, c.y + s * 0.55f, c.x - s, c.y); close() }
            drawPath(p, color)
            drawLine(mezcla(color, Color.Black, 0.3f), Offset(c.x - s * 1.2f, c.y), Offset(c.x + s * 0.8f, c.y), s * 0.08f)
        }
    }

    /** Una grieta: un trazo quebrado desde [a] en dirección [angulo], con una rama. */
    private fun DrawScope.grieta(a: Offset, largo: Float, angulo: Float, color: Color, grosor: Float, rnd: Random, conMusgo: Color? = null) {
        val p = Path(); p.moveTo(a.x, a.y)
        var q = a; var ang = Math.toRadians(angulo.toDouble())
        val pasos = 7
        var rama: Offset? = null
        for (k in 0 until pasos) {
            ang += (rnd.nextFloat() - 0.5f) * 0.9f
            val l = largo / pasos
            q = Offset(q.x + l * cos(ang).toFloat(), q.y + l * sin(ang).toFloat())
            p.lineTo(q.x, q.y)
            if (k == 3) rama = q
            if (conMusgo != null) { drawCircle(conMusgo, grosor * (1.6f + 1.4f * rnd.nextFloat()), q); if (k % 2 == 0) drawCircle(mezcla(conMusgo, Color(0xFF86A33C), 0.5f), grosor * 1.2f, Offset(q.x + grosor * 1.5f, q.y - grosor)) }
        }
        drawPath(p, Color.White.copy(alpha = 0.12f), style = Stroke(grosor * 2.6f))
        drawPath(p, color, style = Stroke(grosor))
        rama?.let { r ->
            val b = Path(); b.moveTo(r.x, r.y)
            var z = r; var an = ang + 0.9
            for (k in 0 until 3) { an += (rnd.nextFloat() - 0.5f) * 0.8f; z = Offset(z.x + largo / 10f * cos(an).toFloat(), z.y + largo / 10f * sin(an).toFloat()); b.lineTo(z.x, z.y) }
            drawPath(b, color, style = Stroke(grosor * 0.6f))
        }
    }

    /** Un foco de la guirnalda: el casquillo y el vidrio, con su halo si está encendido. */
    private fun DrawScope.foco(c: Offset, r: Float, oscuro: Boolean) {
        if (oscuro) drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = 0.55f), Color.Transparent), Offset(c.x, c.y + r), r * 5f), r * 5f, Offset(c.x, c.y + r))
        drawRect(t(ACERO_OSC, oscuro), Offset(c.x - r * 0.45f, c.y - r * 0.6f), Size(r * 0.9f, r * 0.7f))
        drawOval(if (oscuro) Color(0xFFFFE2A0) else Color(0xFFF2E6C4), Offset(c.x - r * 0.65f, c.y), Size(r * 1.3f, r * 1.7f))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.8f else 0.6f), r * 0.22f, Offset(c.x - r * 0.2f, c.y + r * 0.55f))
    }

    /** La guirnalda de focos colgada entre dos puntos, con su comba. */
    private fun DrawScope.guirnalda(a: Offset, b: Offset, comba: Float, r: Float, n: Int, oscuro: Boolean) {
        val cx = (a.x + b.x) / 2; val cy = (a.y + b.y) / 2 + comba * 2f
        val p = Path().apply { moveTo(a.x, a.y); quadraticTo(cx, cy, b.x, b.y) }
        drawPath(p, t(Color(0xFF222420), oscuro), style = Stroke(r * 0.22f))
        for (k in 1 until n) {
            val f = k / n.toFloat()
            val x = (1 - f) * (1 - f) * a.x + 2 * (1 - f) * f * cx + f * f * b.x
            val y = (1 - f) * (1 - f) * a.y + 2 * (1 - f) * f * cy + f * f * b.y
            foco(Offset(x, y + r * 0.4f), r, oscuro)
        }
    }

    /** Una luciérnaga: el punto de luz con su halo. */
    private fun DrawScope.luciernaga(o: Offset, r: Float) {
        drawCircle(Brush.radialGradient(listOf(LUCIERNAGA.copy(alpha = 0.45f), Color.Transparent), o, r * 5f), r * 5f, o)
        drawCircle(LUCIERNAGA, r, o)
    }

    /** Una llama con su halo. */
    private fun DrawScope.llama(b: Offset, alto: Float, oscuro: Boolean) {
        drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = if (oscuro) 0.55f else 0.3f), Color.Transparent), Offset(b.x, b.y - alto * 0.5f), alto * 2.6f), alto * 2.6f, Offset(b.x, b.y - alto * 0.5f))
        val p = Path().apply { moveTo(b.x, b.y); cubicTo(b.x - alto * 0.45f, b.y - alto * 0.2f, b.x - alto * 0.1f, b.y - alto * 0.7f, b.x, b.y - alto); cubicTo(b.x + alto * 0.1f, b.y - alto * 0.7f, b.x + alto * 0.45f, b.y - alto * 0.2f, b.x, b.y); close() }
        drawPath(p, LLAMA)
        drawOval(Color(0xFFFFF2C0), Offset(b.x - alto * 0.12f, b.y - alto * 0.45f), Size(alto * 0.24f, alto * 0.4f))
    }

    /** Un montante de ángulo ranurado vertical: la cara con su luz, las ranuras ovaladas y el óxido. */
    private fun DrawScope.montante(r: Rect, oscuro: Boolean, rnd: Random, u: Float) {
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.right, r.top), Size(0.6f * u, r.height))
        drawRect(Brush.horizontalGradient(listOf(t(mezcla(ACERO, Color.White, 0.2f), oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro)), r.left, r.right), r.topLeft, r.size)
        drawRect(t(ACERO_OSC, oscuro), Offset(r.right - r.width * 0.28f, r.top), Size(r.width * 0.28f, r.height))
        var y = r.top + 1.2f * u
        while (y < r.bottom - 1.6f * u) { drawRoundRect(t(NOCHE, oscuro).copy(alpha = 0.8f), Offset(r.left + r.width * 0.22f, y), Size(r.width * 0.32f, 1.6f * u), CornerRadius(r.width * 0.16f)); y += 3f * u }
        clipRect(r.left, r.top, r.right, r.bottom) { for (k in 0 until 2) mancha(Offset(r.center.x, r.top + r.height * rnd.nextFloat()), r.width * 0.9f, t(OXIDO, oscuro).copy(alpha = 0.6f)) }
    }

    /** Un riel horizontal de ángulo ranurado. */
    private fun DrawScope.riel(r: Rect, oscuro: Boolean, rnd: Random, u: Float) {
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left, r.bottom), Size(r.width, 0.8f * u))
        drawRect(Brush.verticalGradient(listOf(t(mezcla(ACERO, Color.White, 0.25f), oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)
        drawRect(t(mezcla(ACERO, Color.White, 0.35f), oscuro), r.topLeft, Size(r.width, r.height * 0.18f))
        var x = r.left + 2f * u
        while (x < r.right - 2f * u) { drawRoundRect(t(NOCHE, oscuro).copy(alpha = 0.8f), Offset(x, r.top + r.height * 0.42f), Size(1.6f * u, r.height * 0.3f), CornerRadius(r.height * 0.15f)); x += 3.2f * u }
        clipRect(r.left, r.top, r.right, r.bottom) { for (k in 0 until 5) mancha(Offset(r.left + r.width * rnd.nextFloat(), r.center.y), r.height * 1.1f, t(OXIDO, oscuro).copy(alpha = 0.55f)) }
    }

    /** Una lata de conserva de frente, con sus estrías, su borde y su óxido. */
    private fun DrawScope.lata(r: Rect, oscuro: Boolean, rnd: Random, u: Float, etiqueta: Color? = null) {
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(r.left - r.width * 0.1f, r.bottom - r.width * 0.12f), Size(r.width * 1.2f, r.width * 0.24f))
        drawRect(Brush.horizontalGradient(listOf(t(HOJALATA_OSC, oscuro), t(mezcla(HOJALATA, Color.White, 0.35f), oscuro), t(HOJALATA, oscuro), t(HOJALATA_OSC, oscuro)), r.left, r.right), r.topLeft, r.size)
        var y = r.top + r.height * 0.15f
        while (y < r.bottom - r.height * 0.1f) { drawLine(Color.Black.copy(alpha = 0.15f), Offset(r.left, y), Offset(r.right, y), 0.25f * u); y += r.height * 0.14f }
        if (etiqueta != null) drawRect(t(etiqueta, oscuro), Offset(r.left, r.top + r.height * 0.3f), Size(r.width, r.height * 0.38f))
        mancha(Offset(r.left + r.width * (0.2f + 0.6f * rnd.nextFloat()), r.bottom - r.height * 0.2f), r.width * 0.4f, t(OXIDO, oscuro).copy(alpha = 0.7f))
        drawOval(t(HOJALATA_OSC, oscuro), Offset(r.left, r.top - r.width * 0.1f), Size(r.width, r.width * 0.2f))
        drawOval(t(mezcla(HOJALATA, Color.White, 0.3f), oscuro), Offset(r.left, r.top - r.width * 0.1f), Size(r.width, r.width * 0.2f), style = Stroke(0.3f * u))
    }

    // ---------------------------------------------------------------- Social: la plaza

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.SOCIAL))
        val rnd = Random(e.semilla.toLong())

        // El asfalto: su grano, los parches de reparación y la luz de arriba.
        drawRect(c(ASFALTO))
        for (k in 0 until 260) {
            drawCircle((if (k % 2 == 0) Color.White else Color.Black).copy(alpha = if (oscuro) 0.06f else 0.09f), (0.15f + 0.3f * rnd.nextFloat()) * u, Offset(w * rnd.nextFloat(), h * rnd.nextFloat()))
        }
        // El paso de cebra desteñido a la izquierda y la doble raya amarilla a la derecha.
        val cebra = Rect(1.5f * u, 0f, minOf(13f * u, n.left - 12f * u), h)
        var zy = 1f * u
        while (zy < h) {
            val barra = Rect(cebra.left, zy, cebra.right, zy + 3.4f * u)
            drawRect(c(PINTURA_VIAL).copy(alpha = 0.7f), barra.topLeft, barra.size)
            for (k in 0 until 6) drawCircle(c(ASFALTO).copy(alpha = 0.8f), (0.4f + 1.2f * rnd.nextFloat()) * u, Offset(barra.left + barra.width * rnd.nextFloat(), barra.top + barra.height * rnd.nextFloat()))
            zy += 6.8f * u
        }
        listOf(w - 4.6f * u, w - 2.6f * u).forEach { x ->
            drawRect(c(AMARILLO_VIAL).copy(alpha = 0.7f), Offset(x, 0f), Size(0.9f * u, h))
        }
        for (k in 0 until 10) drawCircle(c(ASFALTO).copy(alpha = 0.85f), (0.4f + 0.9f * rnd.nextFloat()) * u, Offset(w - 4.6f * u + 2.9f * u * rnd.nextFloat(), h * rnd.nextFloat()))
        // Las grietas con musgo y el pasto que sale en ellas.
        val grietaCol = Color.Black.copy(alpha = if (oscuro) 0.6f else 0.45f)
        listOf(Triple(Offset(w * 0.02f, h * 0.18f), 30f * u, 10f), Triple(Offset(w * 0.98f, h * 0.62f), 26f * u, 170f), Triple(Offset(w * 0.45f, h), 22f * u, -80f), Triple(Offset(w * 0.55f, 0f), 18f * u, 95f)).forEach { (a, l, ang) ->
            grieta(a, l, ang, grietaCol, 0.4f * u, rnd, c(MUSGO))
        }
        listOf(Offset(w * 0.16f, h * 0.22f), Offset(w * 0.86f, h * 0.6f), Offset(w * 0.44f, h * 0.9f), Offset(w * 0.6f, h * 0.08f), Offset(w * 0.08f, h * 0.7f)).forEach { pastoArriba(it, 2.6f * u, oscuro, rnd) }

        // El charco con el cielo, abajo a la izquierda.
        val ch = Rect(w * 0.035f, h * 0.76f, w * 0.035f + 16f * u, h * 0.76f + 8f * u)
        drawOval(c(mezcla(ASFALTO, Color.Black, 0.25f)), ch.topLeft, ch.size)
        drawOval(Brush.linearGradient(listOf(if (oscuro) CIELO_NOCHE else CIELO, (if (oscuro) CIELO_NOCHE else CIELO).copy(alpha = 0.4f)), ch.topLeft, ch.bottomRight), Offset(ch.left + 0.8f * u, ch.top + 0.6f * u), Size(ch.width - 1.6f * u, ch.height - 1.2f * u))
        if (oscuro) drawCircle(LUNA.copy(alpha = 0.85f), 1.3f * u, Offset(ch.left + ch.width * 0.62f, ch.top + ch.height * 0.4f))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.6f), Offset(ch.left + ch.width * 0.2f, ch.top + ch.height * 0.35f), Offset(ch.left + ch.width * 0.45f, ch.top + ch.height * 0.3f), 0.4f * u, StrokeCap.Round)

        // La alcantarilla, abajo a la derecha.
        val al = Offset(w - 13f * u, h - 11f * u); val ar = 5f * u
        drawCircle(Color.Black.copy(alpha = 0.4f), ar * 1.08f, Offset(al.x + 0.4f * u, al.y + 0.6f * u))
        drawCircle(Brush.radialGradient(listOf(c(ACERO), c(ACERO_OSC)), Offset(al.x - ar * 0.3f, al.y - ar * 0.3f), ar * 1.4f), ar, al)
        clipPath(Path().apply { addOval(Rect(al.x - ar * 0.86f, al.y - ar * 0.86f, al.x + ar * 0.86f, al.y + ar * 0.86f)) }) {
            var k = -6
            while (k <= 6) {
                drawLine(c(ACERO_OSC), Offset(al.x + k * 0.8f * u, al.y - ar), Offset(al.x + k * 0.8f * u, al.y + ar), 0.3f * u)
                drawLine(c(ACERO_OSC), Offset(al.x - ar, al.y + k * 0.8f * u), Offset(al.x + ar, al.y + k * 0.8f * u), 0.3f * u)
                k++
            }
        }
        drawCircle(c(ACERO_OSC), ar * 0.88f, al, style = Stroke(0.4f * u))
        mancha(Offset(al.x + ar * 0.3f, al.y + ar * 0.2f), ar * 0.6f, c(OXIDO).copy(alpha = 0.6f))

        // El tambo con la fogata, arriba a la derecha.
        val tb = Offset(w - 11f * u, 11f * u); val tr = 5.6f * u
        drawCircle(Color.Black.copy(alpha = 0.4f), tr * 1.1f, Offset(tb.x + 0.8f * u, tb.y + 1.2f * u))
        drawCircle(Brush.radialGradient(listOf(c(OXIDO_LUZ), c(OXIDO), c(OXIDO_OSC)), Offset(tb.x - tr * 0.3f, tb.y - tr * 0.3f), tr * 1.4f), tr, tb)
        drawCircle(c(OXIDO_OSC), tr * 0.86f, tb)
        drawCircle(c(NEUMATICO), tr * 0.8f, tb)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE08A).copy(alpha = if (oscuro) 1f else 0.85f), LLAMA, Color(0xFFB0401A), Color(0xFF3A1A0A)), tb, tr * 0.8f), tr * 0.72f, tb)
        for (k in 0 until 4) { val a = k * 47.0; drawLine(c(Color(0xFF2A1C10)), Offset(tb.x - tr * 0.6f * cos(Math.toRadians(a)).toFloat(), tb.y - tr * 0.6f * sin(Math.toRadians(a)).toFloat()), Offset(tb.x + tr * 0.6f * cos(Math.toRadians(a)).toFloat(), tb.y + tr * 0.6f * sin(Math.toRadians(a)).toFloat()), 0.6f * u, StrokeCap.Round) }
        if (oscuro) drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = 0.4f), Color.Transparent), tb, tr * 4f), tr * 4f, tb)
        drawCircle(c(HOJALATA_OSC), tr * 0.98f, tb, style = Stroke(0.4f * u))

        // La hiedra que entra por la esquina de arriba a la izquierda.
        hiedra(listOf(Offset(0f, h * 0.05f), Offset(w * 0.06f, h * 0.1f), Offset(w * 0.1f, h * 0.04f), Offset(w * 0.14f, 0f)), 2.6f * u, oscuro, rnd)
        hiedra(listOf(Offset(0f, h * 0.3f), Offset(w * 0.035f, h * 0.36f)), 2.4f * u, oscuro, rnd)
        for (k in 0 until 10) {
            val p = Offset(w * rnd.nextFloat(), if (k % 2 == 0) h * (0.02f + 0.16f * rnd.nextFloat()) else h * (0.82f + 0.16f * rnd.nextFloat()))
            hojaSeca(p, 1.4f * u, rnd.nextFloat() * 360f, c(HOJARASCA[k % HOJARASCA.size]))
        }

        // Los asientos rescatados alrededor de la mesa: llantas, cajas de fruta, bloques y un bidón.
        val canto = Rect(n.left - 3.6f * u, n.top - 3.6f * u, n.right + 3.6f * u, n.bottom + 3.6f * u)
        val sr = 5.2f * u
        fun llanta(p: Offset) {
            drawCircle(Color.Black.copy(alpha = 0.4f), sr * 1.05f, Offset(p.x + 0.8f * u, p.y + 1.2f * u))
            drawCircle(c(NEUMATICO), sr, p)
            for (k in 0 until 24) { val a = Math.toRadians(k * 15.0); drawLine(Color.White.copy(alpha = 0.08f), Offset(p.x + sr * 0.72f * cos(a).toFloat(), p.y + sr * 0.72f * sin(a).toFloat()), Offset(p.x + sr * 0.97f * cos(a).toFloat(), p.y + sr * 0.97f * sin(a).toFloat()), 0.35f * u) }
            drawCircle(Color.White.copy(alpha = if (oscuro) 0.06f else 0.12f), sr * 0.7f, p, style = Stroke(0.5f * u))
            drawCircle(c(TIERRA), sr * 0.55f, p)
            musgo(Offset(p.x + sr * 0.15f, p.y + sr * 0.1f), sr * 0.6f, sr * 0.35f, oscuro, rnd, 6)
            drawArc(Color.White.copy(alpha = if (oscuro) 0.08f else 0.18f), 200f, 70f, false, Offset(p.x - sr * 0.85f, p.y - sr * 0.85f), Size(sr * 1.7f, sr * 1.7f), style = Stroke(0.4f * u))
        }
        fun caja(p: Offset, giro: Float) {
            rotate(giro, p) {
                val r = Rect(p.x - sr, p.y - sr * 0.8f, p.x + sr, p.y + sr * 0.8f)
                drawRect(Color.Black.copy(alpha = 0.4f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size)
                drawRect(c(MADERA_OSC), r.topLeft, r.size)
                val tablas = 4
                for (k in 0 until tablas) {
                    val tr0 = Rect(r.left + 0.6f * u, r.top + 0.6f * u + k * (r.height - 1.2f * u) / tablas + 0.2f * u, r.right - 0.6f * u, r.top + 0.6f * u + (k + 1) * (r.height - 1.2f * u) / tablas - 0.2f * u)
                    drawRect(c(if (k % 2 == 0) MADERA_LUZ else mezcla(MADERA_LUZ, MADERA, 0.4f)), tr0.topLeft, tr0.size)
                    drawLine(c(VETA).copy(alpha = 0.3f), Offset(tr0.left, tr0.center.y), Offset(tr0.right, tr0.center.y + 0.2f * u), 0.2f * u)
                    listOf(tr0.left + 0.8f * u, tr0.right - 0.8f * u).forEach { x -> drawCircle(c(OXIDO_OSC), 0.25f * u, Offset(x, tr0.center.y)) }
                }
            }
        }
        fun bloque(p: Offset) {
            val r = Rect(p.x - sr * 0.95f, p.y - sr * 0.55f, p.x + sr * 0.95f, p.y + sr * 0.55f)
            drawRect(Color.Black.copy(alpha = 0.4f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size)
            drawRect(Brush.linearGradient(listOf(c(HORMIGON_LUZ), c(HORMIGON)), r.topLeft, r.bottomRight), r.topLeft, r.size)
            listOf(0.27f, 0.73f).forEach { f -> drawRect(c(HORMIGON_OSC), Offset(r.left + r.width * f - r.width * 0.16f, r.top + r.height * 0.24f), Size(r.width * 0.32f, r.height * 0.52f)) }
            drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft, r.size, style = Stroke(0.3f * u))
            musgo(Offset(r.left + r.width * 0.15f, r.bottom - 0.6f * u), r.width * 0.3f, 1.2f * u, oscuro, rnd, 8)
        }
        fun bidon(p: Offset) {
            drawCircle(Color.Black.copy(alpha = 0.4f), sr * 1.05f, Offset(p.x + 0.8f * u, p.y + 1.2f * u))
            drawCircle(Brush.radialGradient(listOf(c(Color(0xFF4F7A86)), c(Color(0xFF2E4A52))), Offset(p.x - sr * 0.3f, p.y - sr * 0.3f), sr * 1.4f), sr, p)
            listOf(0.92f, 0.62f).forEach { f -> drawCircle(Color.Black.copy(alpha = 0.3f), sr * f, p, style = Stroke(0.4f * u)) }
            mancha(Offset(p.x + sr * 0.3f, p.y + sr * 0.2f), sr * 0.7f, c(OXIDO).copy(alpha = 0.8f))
            drawCircle(c(ACERO_OSC), sr * 0.14f, Offset(p.x + sr * 0.5f, p.y - sr * 0.3f))
        }
        val y0 = canto.top - sr * 1.05f; val y1 = canto.bottom + sr * 1.05f
        llanta(Offset(canto.left + canto.width * 0.22f, y0)); caja(Offset(canto.left + canto.width * 0.5f, y0), 4f); llanta(Offset(canto.left + canto.width * 0.78f, y0))
        bloque(Offset(canto.left + canto.width * 0.22f, y1)); llanta(Offset(canto.left + canto.width * 0.5f, y1)); caja(Offset(canto.left + canto.width * 0.78f, y1), -6f)
        bidon(Offset(canto.left - sr * 1.1f, canto.center.y)); caja(Offset(canto.right + sr * 1.1f, canto.center.y), 90f)

        // La mesa: una puerta vieja tendida, con su pintura descascarada, sus bisagras y su pomo.
        sombra(canto, 3f * u, if (oscuro) 0.6f else 0.4f)
        tabla(canto, MADERA, oscuro, rnd, u, vertical = true, pintura = PUERTA)
        drawRect(Color.Black.copy(alpha = 0.4f), canto.topLeft, canto.size, style = Stroke(0.35f * u))
        listOf(canto.top + canto.height * 0.2f, canto.bottom - canto.height * 0.2f).forEach { y ->
            val bz = Rect(canto.left - 0.4f * u, y - 2.2f * u, canto.left + 2.4f * u, y + 2.2f * u)
            drawRect(c(OXIDO_OSC), bz.topLeft, bz.size)
            drawRect(c(OXIDO), Offset(bz.left, bz.top), Size(bz.width, bz.height * 0.45f))
            listOf(0.25f, 0.75f).forEach { f -> drawCircle(c(ACERO_OSC), 0.3f * u, Offset(bz.center.x, bz.top + bz.height * f)) }
        }
        val pm = Offset(canto.right - 1.8f * u, canto.center.y)
        drawRoundRect(c(Color(0xFF8A7A4A)), Offset(pm.x - 0.9f * u, pm.y - 3f * u), Size(1.8f * u, 6f * u), CornerRadius(0.6f * u))
        drawCircle(Brush.radialGradient(listOf(c(Color(0xFFD9C27A)), c(Color(0xFF7A6A3A))), Offset(pm.x - 0.4f * u, pm.y - 1.4f * u), 1.6f * u), 1.1f * u, Offset(pm.x, pm.y - 1f * u))
        drawCircle(Color.Black, 0.3f * u, Offset(pm.x, pm.y + 1.6f * u))
        // El tablero de la puerta: el panel pintado, claro y liso, con su moldura alrededor.
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(n.left - 0.9f * u, n.top - 0.9f * u), Size(n.width + 1.8f * u, n.height + 1.8f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), Offset(n.left - 0.5f * u, n.top - 0.5f * u), Size(n.width + 1f * u, n.height + 1f * u))
        val panel = nicho(e.fondo, PANEL, oscuro)
        drawRect(panel, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 26) {
                val y = n.top + n.height * rnd.nextFloat(); val x = n.left + n.width * rnd.nextFloat()
                drawLine(Color.Black.copy(alpha = if (oscuro) 0.05f else 0.03f), Offset(x, y), Offset(x + (6f + 14f * rnd.nextFloat()) * u, y + 0.2f * u), 0.4f * u)
            }
            for (k in 0 until 8) drawCircle(c(MADERA).copy(alpha = if (oscuro) 0.25f else 0.18f), (0.3f + 0.5f * rnd.nextFloat()) * u, Offset(n.left + n.width * rnd.nextFloat(), n.top + n.height * rnd.nextFloat()))
        }
        // La hiedra que trepó por una esquina de la puerta.
        hiedra(listOf(Offset(canto.left - 2f * u, canto.bottom + 1f * u), Offset(canto.left + 1.6f * u, canto.bottom - 3f * u)), 2f * u, oscuro, rnd)

        // La copa de un árbol que creció entre el asfalto, en la esquina de arriba a la izquierda, con su sombra.
        val copa = listOf(Offset(2f * u, 4f * u) to 10f, Offset(12f * u, 1f * u) to 8.5f, Offset(4f * u, 14f * u) to 8f, Offset(13f * u, 9f * u) to 6.5f, Offset(-1f * u, 21f * u) to 6.5f, Offset(21f * u, -1f * u) to 6f)
        copa.forEach { (p, r) -> drawCircle(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.3f), r * u, Offset(p.x + 2.4f * u, p.y + 3.4f * u)) }
        copa.forEachIndexed { k, (p, r) ->
            drawCircle(Brush.radialGradient(listOf(c(HIEDRA_LUZ), c(HIEDRA), c(mezcla(HIEDRA, Color.Black, 0.35f))), Offset(p.x - r * 0.35f * u, p.y - r * 0.35f * u), r * 1.3f * u), r * u, p)
            for (j in 0 until 5) {
                val a = Math.toRadians((j * 72 + k * 20).toDouble())
                hojaHiedra(Offset(p.x + r * 0.55f * u * cos(a).toFloat(), p.y + r * 0.55f * u * sin(a).toFloat()), 2.4f * u, j * 72f, c(if (j % 2 == 0) MUSGO_LUZ else HIEDRA_LUZ).copy(alpha = 0.8f))
            }
        }

        // La guirnalda de focos de lado a lado, arriba y abajo.
        guirnalda(Offset(-1f * u, 2f * u), Offset(w + 1f * u, 2f * u), 2.6f * u, 1.1f * u, 11, oscuro)
        guirnalda(Offset(-1f * u, h - 7.6f * u), Offset(w + 1f * u, h - 7.6f * u), 2.2f * u, 1.1f * u, 11, oscuro)
        if (oscuro) listOf(0.1f to 0.5f, 0.92f to 0.32f, 0.3f to 0.13f, 0.7f to 0.88f).forEach { (fx, fy) -> luciernaga(Offset(w * fx, h * fy), 0.5f * u) }
    }

    // ---------------------------------------------------------------- Mapas: la mesa de tablas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.MAPAS))
        val rnd = Random(e.semilla.toLong())
        val yB = h * 0.24f; val yF = h * 0.83f

        // El muro de hormigón con manchas de humedad y grietas, hasta el piso.
        drawRect(Brush.verticalGradient(listOf(c(HORMIGON), c(mezcla(HORMIGON, HORMIGON_OSC, 0.5f))), 0f, yF), Offset.Zero, Size(w, yF))
        for (k in 0 until 10) mancha(Offset(w * rnd.nextFloat(), yF * rnd.nextFloat()), (8f + 14f * rnd.nextFloat()) * u, c(HORMIGON_OSC).copy(alpha = 0.3f))
        for (k in 0 until 3) mancha(Offset(w * (0.2f + 0.6f * rnd.nextFloat()), 2f * u), 7f * u, c(MUSGO).copy(alpha = 0.35f))
        grieta(Offset(w * 0.32f, 0f), 18f * u, 80f, Color.Black.copy(alpha = 0.4f), 0.35f * u, rnd)
        grieta(Offset(w * 0.04f, yB * 0.4f), 14f * u, 30f, Color.Black.copy(alpha = 0.35f), 0.3f * u, rnd)
        // La ventana rota con el cielo, las varillas al aire y la rama que entra.
        val vw = minOf(w * 0.3f, 40f * u)
        val vx = w * 0.62f
        val hueco = Path().apply {
            moveTo(vx, 1.6f * u); lineTo(vx + vw * 0.35f, 0.6f * u); lineTo(vx + vw * 0.7f, 2f * u); lineTo(vx + vw, 0.8f * u)
            lineTo(vx + vw * 0.96f, yB * 0.6f); lineTo(vx + vw * 1.02f, yB * 0.8f); lineTo(vx + vw * 0.6f, yB * 0.86f); lineTo(vx + vw * 0.3f, yB * 0.78f); lineTo(vx + vw * 0.05f, yB * 0.88f); lineTo(vx - vw * 0.04f, yB * 0.45f); close()
        }
        drawPath(hueco, Color.Black.copy(alpha = 0.35f), style = Stroke(1.6f * u))
        clipPath(hueco) {
            drawRect(Brush.verticalGradient(if (oscuro) listOf(CIELO_NOCHE, Color(0xFF1A2A3A)) else listOf(Color(0xFFA9CFE0), CIELO), 0f, yB), Offset(vx - vw * 0.1f, 0f), Size(vw * 1.2f, yB))
            if (oscuro) {
                drawCircle(Brush.radialGradient(listOf(LUNA.copy(alpha = 0.35f), Color.Transparent), Offset(vx + vw * 0.78f, yB * 0.3f), 6f * u), 6f * u, Offset(vx + vw * 0.78f, yB * 0.3f))
                drawCircle(LUNA, 2.2f * u, Offset(vx + vw * 0.78f, yB * 0.3f))
                listOf(0.2f to 0.2f, 0.45f to 0.55f, 0.6f to 0.15f).forEach { (fx, fy) -> drawCircle(Color.White.copy(alpha = 0.8f), 0.25f * u, Offset(vx + vw * fx, yB * fy)) }
            } else drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF6D0), Color.Transparent), Offset(vx + vw * 0.8f, yB * 0.2f), 8f * u), 8f * u, Offset(vx + vw * 0.8f, yB * 0.2f))
            // La copa del árbol de afuera.
            for (k in 0 until 7) drawCircle(c(if (k % 2 == 0) HIEDRA else HIEDRA_LUZ), (2.4f + 1.8f * rnd.nextFloat()) * u, Offset(vx + vw * (0.05f + 0.5f * rnd.nextFloat()), yB * (0.05f + 0.35f * rnd.nextFloat())))
        }
        drawPath(hueco, c(HORMIGON_OSC), style = Stroke(0.6f * u))
        listOf(Offset(vx + vw * 0.3f, yB * 0.78f) to Offset(vx + vw * 0.38f, yB * 0.55f), Offset(vx + vw * 0.96f, yB * 0.6f) to Offset(vx + vw * 0.84f, yB * 0.48f)).forEach { (a, b) -> drawLine(c(OXIDO), a, b, 0.6f * u, StrokeCap.Round) }
        val rama = Path().apply { moveTo(vx + vw * 0.2f, yB * 0.2f); quadraticTo(vx - vw * 0.05f, yB * 0.3f, vx - vw * 0.25f, yB * 0.62f) }
        drawPath(rama, c(Color(0xFF5A4636)), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        listOf(0.35f, 0.55f, 0.75f, 0.92f).forEachIndexed { k, f ->
            val q = Offset(vx + vw * 0.2f + (-vw * 0.45f) * f, yB * 0.2f + yB * 0.42f * f * f)
            hojaHiedra(Offset(q.x, q.y + (if (k % 2 == 0) 1.2f else -1.2f) * u), 2.6f * u, if (k % 2 == 0) 160f else 20f, c(if (k % 2 == 0) HIEDRA_LUZ else HIEDRA))
        }
        // La hiedra que cuelga del techo a la izquierda.
        hiedra(listOf(Offset(w * 0.07f, 0f), Offset(w * 0.08f, yB * 0.5f), Offset(w * 0.06f, yB * 0.95f)), 2.2f * u, oscuro, rnd)
        hiedra(listOf(Offset(w * 0.2f, 0f), Offset(w * 0.21f, yB * 0.45f)), 2f * u, oscuro, rnd)
        hiedra(listOf(Offset(w * 0.95f, 0f), Offset(w * 0.94f, yB * 0.7f)), 2f * u, oscuro, rnd)
        // De día, el rayo de sol que entra por el hueco (fuera del mapa).
        if (!oscuro) {
            val rayo = Path().apply { moveTo(vx + vw * 0.1f, yB * 0.8f); lineTo(vx + vw * 0.6f, yB * 0.86f); lineTo(w * 0.98f, yF); lineTo(w * 0.8f, yF); close() }
            clipRect(n.left - 4f * u, n.top - 4f * u, n.right + 4f * u, n.bottom + 4f * u, clipOp = ClipOp.Difference) { drawPath(rayo, Color(0xFFFFF6D0).copy(alpha = 0.14f)) }
        }

        // El tablero: cuatro tablas en perspectiva, con rendijas, veta y clavos en las puntas.
        val tablero = Path().apply { moveTo(w * 0.06f, yB); lineTo(w * 0.94f, yB); lineTo(w * 0.995f, yF); lineTo(w * 0.005f, yF); close() }
        drawPath(tablero, c(MADERA_OSC))
        val tablas = 5
        for (k in 0 until tablas) {
            val f0 = k / tablas.toFloat(); val f1 = (k + 1) / tablas.toFloat()
            val y0 = yB + (yF - yB) * f0 + 0.2f * u; val y1 = yB + (yF - yB) * f1 - 0.2f * u
            val xl0 = w * (0.06f - 0.055f * f0); val xr0 = w * (0.94f + 0.055f * f0)
            val xl1 = w * (0.06f - 0.055f * f1); val xr1 = w * (0.94f + 0.055f * f1)
            val pl = Path().apply { moveTo(xl0, y0); lineTo(xr0, y0); lineTo(xr1, y1); lineTo(xl1, y1); close() }
            clipPath(pl) { tabla(Rect(0f, y0, w, y1), if (k % 2 == 0) MADERA else mezcla(MADERA, MADERA_LUZ, 0.4f), oscuro, rnd, u, pintura = if (k == 3) Color(0xFF9A5A44) else null) }
            listOf(xl0 + (xl1 - xl0) * 0.5f + 2.2f * u, xr0 + (xr1 - xr0) * 0.5f - 2.2f * u).forEach { x -> clavo(Offset(x, (y0 + y1) / 2), 0.5f * u, oscuro, false) }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), yB, yB + 4f * u), Offset(0f, yB), Size(w, 4f * u))

        // El pliego del mapa con su borde rasgado, sujeto con piedras.
        val pg = Rect(n.left - 2.2f * u, n.top - 2.2f * u, n.right + 2.2f * u, n.bottom + 2.2f * u)
        val rasgado = Path().apply {
            val pasos = 16
            moveTo(pg.left, pg.top)
            for (k in 1..pasos) lineTo(pg.left + pg.width * k / pasos, pg.top + (if (k % 2 == 0) 0f else 0.8f * u) * rnd.nextFloat())
            for (k in 1..pasos / 2) lineTo(pg.right - (if (k % 2 == 0) 0f else 0.8f * u) * rnd.nextFloat(), pg.top + pg.height * k * 2 / pasos)
            for (k in 1..pasos) lineTo(pg.right - pg.width * k / pasos, pg.bottom - (if (k % 2 == 0) 0f else 0.8f * u) * rnd.nextFloat())
            for (k in 1 until pasos / 2) lineTo(pg.left + (if (k % 2 == 0) 0f else 0.8f * u) * rnd.nextFloat(), pg.bottom - pg.height * k * 2 / pasos)
            close()
        }
        sombra(pg, 1.6f * u, if (oscuro) 0.5f else 0.3f)
        val papel = nicho(e.fondo, PAPEL_VIEJO, oscuro)
        drawPath(rasgado, mezcla(papel, Color.Black, 0.06f))
        drawPath(rasgado, Color.Black.copy(alpha = 0.25f), style = Stroke(0.3f * u))
        drawRect(papel, n.topLeft, n.size)
        listOf(0.33f, 0.66f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.06f), Offset(n.left + n.width * f, pg.top), Offset(n.left + n.width * f, pg.bottom), 0.4f * u) }
        listOf(pg.topLeft, Offset(pg.right, pg.top), Offset(pg.left, pg.bottom)).forEachIndexed { k, q ->
            val pr = (2f + 0.6f * k) * u
            val pc = Offset(q.x + (if (q.x < w / 2) -0.8f else 0.8f) * u, q.y + (if (q.y < h / 2) -0.6f else 0.6f) * u)
            drawOval(Color.Black.copy(alpha = 0.35f), Offset(pc.x - pr, pc.y - pr * 0.4f), Size(pr * 2.2f, pr * 1.4f))
            drawOval(Brush.radialGradient(listOf(c(HORMIGON_LUZ), c(HORMIGON), c(HORMIGON_OSC)), Offset(pc.x - pr * 0.4f, pc.y - pr * 0.5f), pr * 1.6f), Offset(pc.x - pr, pc.y - pr * 0.8f), Size(pr * 2f, pr * 1.5f))
            if (k == 1) musgo(Offset(pc.x, pc.y - pr * 0.5f), pr, pr * 0.5f, oscuro, rnd, 8)
        }

        // A la izquierda, al fondo: la vela en su lata, con sus chorretes de cera y la llama.
        val colI = n.left - 4f * u
        val vc = Offset(colI * 0.55f, h * 0.5f)
        val lw = minOf(colI * 0.45f, 8f * u)
        lata(Rect(vc.x - lw / 2, vc.y - lw * 1.1f, vc.x + lw / 2, vc.y), oscuro, rnd, u, CINTA)
        val vela = Rect(vc.x - lw * 0.22f, vc.y - lw * 1.9f, vc.x + lw * 0.22f, vc.y - lw * 1.1f)
        drawRect(Brush.horizontalGradient(listOf(c(Color(0xFFE6E0CC)), Color(0xFFF6F2E4).let { if (oscuro) mezcla(it, Color(0xFFFFD48A), 0.3f) else it }, c(Color(0xFFCFC8B0))), vela.left, vela.right), vela.topLeft, vela.size)
        listOf(0.2f to 0.5f, 0.75f to 0.35f).forEach { (fx, fl) -> drawRoundRect(c(Color(0xFFEDE8D6)), Offset(vela.left + vela.width * fx - 0.4f * u, vela.top), Size(0.8f * u, vela.height * fl + lw * 0.3f), CornerRadius(0.4f * u)) }
        drawLine(c(NEUMATICO), Offset(vela.center.x, vela.top), Offset(vela.center.x, vela.top - 0.8f * u), 0.25f * u)
        llama(Offset(vela.center.x, vela.top - 0.6f * u), 3.6f * u, oscuro)

        // A la izquierda, al frente: la brújula de latón y la navaja oxidada.
        val br = Offset(colI * 0.4f, h * 0.68f); val brr = minOf(colI * 0.3f, 5f * u)
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(br.x - brr * 1.1f, br.y - brr * 0.2f), Size(brr * 2.4f, brr * 1.3f))
        drawOval(Brush.horizontalGradient(listOf(c(Color(0xFF7A6028)), c(Color(0xFFD9BE6E)), c(Color(0xFF9A7A3A))), br.x - brr, br.x + brr), Offset(br.x - brr, br.y - brr * 0.6f), Size(brr * 2, brr * 1.2f))
        drawOval(c(Color(0xFFEDE6D0)), Offset(br.x - brr * 0.8f, br.y - brr * 0.52f), Size(brr * 1.6f, brr * 0.9f))
        drawLine(c(Color(0xFFB3261E)), br.copy(y = br.y - brr * 0.07f), Offset(br.x + brr * 0.55f, br.y - brr * 0.25f), 0.45f * u, StrokeCap.Round)
        drawLine(c(NEUMATICO), br.copy(y = br.y - brr * 0.07f), Offset(br.x - brr * 0.55f, br.y + brr * 0.1f), 0.45f * u, StrokeCap.Round)
        drawOval(Color.White.copy(alpha = if (oscuro) 0.15f else 0.4f), Offset(br.x - brr * 0.6f, br.y - brr * 0.48f), Size(brr * 0.6f, brr * 0.25f))
        drawCircle(c(Color(0xFFD9BE6E)), 0.7f * u, Offset(br.x, br.y - brr * 0.7f))
        val nv = Offset(colI * 0.3f, h * 0.78f)
        rotate(-8f, nv) {
            drawRoundRect(c(Color(0xFF8A3A2A)), Offset(nv.x, nv.y - 0.9f * u), Size(8f * u, 1.8f * u), CornerRadius(0.9f * u))
            drawPath(Path().apply { moveTo(nv.x + 8f * u, nv.y - 0.7f * u); lineTo(nv.x + 15f * u, nv.y - 0.2f * u); lineTo(nv.x + 8f * u, nv.y + 0.6f * u); close() }, Brush.verticalGradient(listOf(c(HOJALATA), c(HOJALATA_OSC)), nv.y - 0.7f * u, nv.y + 0.6f * u))
            mancha(Offset(nv.x + 11f * u, nv.y), 1.6f * u, c(OXIDO).copy(alpha = 0.7f))
            drawCircle(c(HOJALATA), 0.35f * u, Offset(nv.x + 7.2f * u, nv.y))
        }

        // A la derecha, al fondo: el frasco de vidrio con tierra y un brote.
        val colD0 = n.right + 4f * u
        val dc = (colD0 + w * 0.97f) / 2
        val fw = minOf((w * 0.97f - colD0) * 0.42f, 8f * u)
        val fr = Rect(dc - fw / 2, h * 0.36f, dc + fw / 2, h * 0.5f)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(fr.left - 1f * u, fr.bottom - 0.8f * u), Size(fr.width + 2f * u, 1.8f * u))
        val brote = Path().apply { moveTo(fr.center.x, fr.bottom - fr.height * 0.35f); cubicTo(fr.center.x - 1f * u, fr.top + fr.height * 0.2f, fr.center.x + 2f * u, fr.top - 2f * u, fr.center.x + 1f * u, fr.top - 4f * u) }
        drawRoundRect(c(TIERRA), Offset(fr.left + 0.4f * u, fr.bottom - fr.height * 0.4f), Size(fr.width - 0.8f * u, fr.height * 0.38f), CornerRadius(0.8f * u))
        drawPath(brote, c(HIERRO_TALLO), style = Stroke(0.4f * u))
        hojaHiedra(Offset(fr.center.x + 2.2f * u, fr.top - 2.8f * u), 2.4f * u, 60f, c(HIEDRA_LUZ))
        hojaHiedra(Offset(fr.center.x - 1f * u, fr.top + 0.4f * u), 2f * u, -50f, c(HIEDRA))
        drawRoundRect(Color(0xFFDDEBEB).copy(alpha = if (oscuro) 0.12f else 0.25f), fr.topLeft, fr.size, CornerRadius(1f * u))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.25f else 0.6f), fr.topLeft, fr.size, CornerRadius(1f * u), style = Stroke(0.35f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.55f), Offset(fr.left + fr.width * 0.22f, fr.top + 1f * u), Offset(fr.left + fr.width * 0.22f, fr.bottom - 1.2f * u), 0.5f * u)
        drawRect(c(HOJALATA_OSC), Offset(fr.left + 0.4f * u, fr.top - 1f * u), Size(fr.width - 0.8f * u, 1f * u))
        listOf(-1f, 1.4f).forEach { d -> drawOval(c(if (d > 0) HORMIGON else LADRILLO), Offset(dc + d * fw * 0.9f, fr.bottom - 1f * u), Size(2f * u, 1.2f * u)) }

        // A la derecha, al frente: la linterna de manivela, amarilla y gastada.
        val li = Rect(dc - minOf(w * 0.08f, 12f * u), h * 0.66f, dc + minOf(w * 0.05f, 8f * u), h * 0.73f)
        sombra(li, 1.2f * u, 0.4f, li.height / 2)
        drawRoundRect(Brush.verticalGradient(listOf(c(mezcla(AMARILLO_VIAL, Color.White, 0.25f)), c(AMARILLO_VIAL), c(mezcla(AMARILLO_VIAL, Color.Black, 0.35f))), li.top, li.bottom), li.topLeft, li.size, CornerRadius(li.height * 0.4f))
        drawRect(c(NEUMATICO), Offset(li.left + li.width * 0.3f, li.top), Size(li.width * 0.12f, li.height))
        drawRect(Brush.verticalGradient(listOf(c(HOJALATA), c(HOJALATA_OSC)), li.top - 0.6f * u, li.bottom + 0.6f * u), Offset(li.right - 1f * u, li.top - 0.6f * u), Size(2f * u, li.height + 1.2f * u))
        drawOval(if (oscuro) Color(0xFFFFE8A0) else c(Color(0xFFEDEBD8)), Offset(li.right + 0.6f * u, li.top - 0.4f * u), Size(1f * u, li.height + 0.8f * u))
        if (oscuro) {
            val haz = Path().apply { moveTo(li.right + 1f * u, li.top); lineTo(w, li.top - 4f * u); lineTo(w, li.bottom + 4f * u); lineTo(li.right + 1f * u, li.bottom); close() }
            drawPath(haz, Brush.horizontalGradient(listOf(Color(0xFFFFE8A0).copy(alpha = 0.3f), Color.Transparent), li.right, w))
        }
        val mv = Offset(li.left + li.width * 0.62f, li.center.y)
        drawCircle(c(ACERO_OSC), 1f * u, mv)
        drawLine(c(ACERO_OSC), mv, Offset(mv.x - 1.6f * u, mv.y + 2.6f * u), 0.6f * u, StrokeCap.Round)
        drawCircle(c(NEUMATICO), 0.6f * u, Offset(mv.x - 1.6f * u, mv.y + 2.6f * u))
        mancha(Offset(li.left + li.width * 0.15f, li.bottom - 0.6f * u), 1.6f * u, c(OXIDO).copy(alpha = 0.6f))

        // El canto: la tabla del frente con sus clavos y su musgo, la hiedra que cae; las cajas de fruta y el suelo.
        val suelo = h * 0.975f
        drawRect(c(TIERRA), Offset(0f, yF), Size(w, h - yF))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), yF, h), Offset(0f, yF), Size(w, h - yF))
        val frente = Rect(w * 0.005f, yF, w * 0.995f, yF + h * 0.045f)
        tabla(frente, MADERA_OSC, oscuro, rnd, u)
        listOf(frente.left + 2f * u, w * 0.5f, frente.right - 2f * u).forEach { clavo(Offset(it, frente.center.y), 0.55f * u, oscuro) }
        musgo(Offset(w * 0.32f, frente.top + 0.4f * u), 14f * u, 1.6f * u, oscuro, rnd, 18)
        musgo(Offset(w * 0.78f, frente.top + 0.4f * u), 10f * u, 1.4f * u, oscuro, rnd, 12)
        listOf(w * 0.02f to w * 0.2f, w * 0.8f to w * 0.98f).forEach { (a, b) ->
            val cj = Rect(a, frente.bottom, b, suelo)
            drawRect(c(MADERA_OSC), cj.topLeft, cj.size)
            val lt = 3
            for (k in 0 until lt) {
                val ty = cj.top + 0.4f * u + k * (cj.height - 0.8f * u) / lt
                tabla(Rect(cj.left + 0.6f * u, ty, cj.right - 0.6f * u, ty + (cj.height - 0.8f * u) / lt - 0.6f * u), MADERA_LUZ, oscuro, rnd, u)
            }
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(cj.left, cj.top), Size(cj.width, 1f * u))
        }
        for (k in 0 until 16) hojaSeca(Offset(w * rnd.nextFloat(), suelo + (h - suelo) * rnd.nextFloat()), 1.2f * u, rnd.nextFloat() * 360f, c(HOJARASCA[k % HOJARASCA.size]))
        listOf(0.25f, 0.42f, 0.6f, 0.74f, 0.9f, 0.06f).forEach { f -> pasto(Offset(w * f, h), 5f * u, oscuro, rnd) }
        hiedra(listOf(Offset(w * 0.12f, yF + 0.6f * u), Offset(w * 0.13f, yF + 6f * u), Offset(w * 0.11f, suelo)), 2f * u, oscuro, rnd)
        if (oscuro) listOf(0.1f to 0.18f, 0.45f to 0.12f, 0.9f to 0.95f, 0.5f to 0.97f).forEach { (fx, fy) -> luciernaga(Offset(w * fx, h * fy), 0.5f * u) }
    }

    // ---------------------------------------------------------------- Otras vidas: la estantería bajo la losa rota

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.OTRAS_VIDAS))
        val rnd = Random(e.semilla.toLong())

        // El muro de detrás, con manchas de humedad.
        drawRect(Brush.verticalGradient(listOf(c(mezcla(HORMIGON, HORMIGON_OSC, 0.35f)), c(HORMIGON)), 0f, h), Offset.Zero, size)
        for (k in 0 until 10) mancha(Offset(w * rnd.nextFloat(), h * rnd.nextFloat()), (8f + 12f * rnd.nextFloat()) * u, c(HORMIGON_OSC).copy(alpha = 0.3f))

        // La losa rota del techo: el hueco con el cielo, las varillas dobladas, el musgo en el canto.
        val losa = h * 0.11f
        val hx0 = w * 0.56f; val hx1 = minOf(w * 0.82f, hx0 + 50f * u)
        drawRect(Brush.verticalGradient(listOf(c(HORMIGON_LUZ), c(HORMIGON)), 0f, losa), Offset.Zero, Size(w, losa))
        val hueco = Path().apply { moveTo(hx0, 0f); lineTo(hx1, 0f); lineTo(hx1 - 2f * u, losa * 0.5f); lineTo(hx1 + 1f * u, losa); lineTo(hx0 + (hx1 - hx0) * 0.6f, losa * 0.75f); lineTo(hx0 + (hx1 - hx0) * 0.3f, losa * 1.05f); lineTo(hx0 - 1f * u, losa * 0.6f); close() }
        clipPath(hueco) {
            drawRect(if (oscuro) CIELO_NOCHE else CIELO, Offset(hx0 - 2f * u, 0f), Size(hx1 - hx0 + 4f * u, losa * 1.2f))
            if (oscuro) { drawCircle(LUNA, 1.6f * u, Offset(hx0 + (hx1 - hx0) * 0.7f, losa * 0.4f)); drawCircle(Color.White.copy(alpha = 0.8f), 0.25f * u, Offset(hx0 + (hx1 - hx0) * 0.3f, losa * 0.3f)) }
            else drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF6D0), Color.Transparent), Offset(hx0 + (hx1 - hx0) * 0.7f, losa * 0.3f), 6f * u), 6f * u, Offset(hx0 + (hx1 - hx0) * 0.7f, losa * 0.3f))
            drawCircle(c(HIEDRA), 3f * u, Offset(hx0 + 2f * u, 0f)); drawCircle(c(HIEDRA_LUZ), 2.2f * u, Offset(hx0 + 5f * u, -0.5f * u))
        }
        drawPath(hueco, c(HORMIGON_OSC), style = Stroke(0.5f * u))
        listOf(0.2f, 0.45f, 0.8f).forEach { f ->
            val x = hx0 + (hx1 - hx0) * f
            drawPath(Path().apply { moveTo(x, losa * 0.2f); quadraticTo(x + 1f * u, losa * 0.8f, x - 1.5f * u, losa * 1.25f) }, c(OXIDO), style = Stroke(0.55f * u, cap = StrokeCap.Round))
        }
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, losa), Size(hx0 - 1f * u, 0.8f * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(hx1 + 1f * u, losa), Size(w - hx1, 0.8f * u))
        musgo(Offset(w * 0.2f, losa), 22f * u, 1.6f * u, oscuro, rnd, 20)
        musgo(Offset(w * 0.92f, losa), 12f * u, 1.4f * u, oscuro, rnd, 12)
        mancha(Offset(w * 0.3f, losa * 0.4f), 8f * u, c(MUSGO).copy(alpha = 0.3f))

        // Encima de la estantería: las latas con plantas, los ladrillos con musgo, el frasco de luciérnagas y el farol.
        val rTop = n.top - 4.4f * u
        val g = 1.4f // los objetos de arriba, a una escala mayor
        listOf(w * 0.1f, w * 0.9f).forEachIndexed { k, x -> scale(g, Offset(x, rTop)) {
            val lr = Rect(x - 3.2f * u, rTop - 7f * u, x + 3.2f * u, rTop)
            listOf(-125f, -100f, -75f, -55f).forEachIndexed { j, a ->
                val l = (7f + 2f * j % 3) * u
                val rad = Math.toRadians(a.toDouble())
                val b = Offset(x, lr.top)
                val f = Offset(b.x + l * cos(rad).toFloat(), b.y + l * sin(rad).toFloat())
                drawLine(c(HIERRO_TALLO), b, f, 0.35f * u)
                hojaHiedra(f, 2.4f * u, a + 90f, c(if ((j + k) % 2 == 0) HIEDRA else HIEDRA_LUZ))
            }
            lata(lr, oscuro, rnd, u, if (k == 0) Color(0xFF8A3A2A) else Color(0xFF3A5A7A))
        } }
        val lb = w * 0.27f
        scale(g, Offset(lb, rTop)) {
        listOf(0 to 0f, 1 to 0.8f).forEach { (k, dx) ->
            val br = Rect(lb - 5f * u + dx * u, rTop - (k + 1) * 3f * u, lb + 5f * u + dx * u, rTop - k * 3f * u)
            drawRect(Brush.verticalGradient(listOf(c(mezcla(LADRILLO, Color.White, 0.15f)), c(LADRILLO), c(mezcla(LADRILLO, Color.Black, 0.25f))), br.top, br.bottom), br.topLeft, br.size)
            drawRect(Color.Black.copy(alpha = 0.3f), br.topLeft, br.size, style = Stroke(0.3f * u))
            listOf(0.3f, 0.7f).forEach { f -> drawCircle(c(mezcla(LADRILLO, Color.Black, 0.4f)), 0.6f * u, Offset(br.left + br.width * f, br.center.y)) }
        }
        musgo(Offset(lb + 0.8f * u, rTop - 6f * u), 9f * u, 1.4f * u, oscuro, rnd, 12)
        }
        // El frasco de luciérnagas, con su tapa de tela y su cordel.
        scale(g, Offset(w * 0.5f, rTop)) {
        val fj = Rect(w * 0.5f - 3.4f * u, rTop - 9f * u, w * 0.5f + 3.4f * u, rTop)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(fj.left - 0.6f * u, fj.bottom - 0.8f * u), Size(fj.width + 1.2f * u, 1.6f * u))
        if (oscuro) drawCircle(Brush.radialGradient(listOf(LUCIERNAGA.copy(alpha = 0.4f), Color.Transparent), fj.center, 10f * u), 10f * u, fj.center)
        drawRoundRect(Color(0xFFDDEBEB).copy(alpha = if (oscuro) 0.15f else 0.3f), fj.topLeft, fj.size, CornerRadius(1.2f * u))
        listOf(0.3f to 0.4f, 0.65f to 0.6f, 0.45f to 0.78f, 0.7f to 0.3f).forEach { (fx, fy) ->
            val p = Offset(fj.left + fj.width * fx, fj.top + fj.height * fy)
            if (oscuro) luciernaga(p, 0.4f * u) else drawCircle(c(Color(0xFF3A3A28)), 0.35f * u, p)
        }
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.3f else 0.6f), fj.topLeft, fj.size, CornerRadius(1.2f * u), style = Stroke(0.35f * u))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(fj.left + 1.2f * u, fj.top + 1.5f * u), Offset(fj.left + 1.2f * u, fj.bottom - 1.5f * u), 0.4f * u)
        val tela = Path().apply { moveTo(fj.left - 0.6f * u, fj.top + 0.4f * u); lineTo(fj.right + 0.6f * u, fj.top + 0.4f * u); lineTo(fj.right - 0.2f * u, fj.top - 1.8f * u); lineTo(fj.left + 0.2f * u, fj.top - 1.8f * u); close() }
        drawPath(tela, c(Color(0xFFB8A27A)))
        drawLine(c(Color(0xFF8A6A40)), Offset(fj.left - 0.4f * u, fj.top - 0.2f * u), Offset(fj.right + 0.4f * u, fj.top - 0.2f * u), 0.4f * u)
        }
        // El farol de queroseno, con su asa, el vidrio y la llama.
        val fa = Offset(w * 0.72f, rTop)
        scale(g, fa) {
        val faw = 5.2f * u
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(fa.x - faw * 0.7f, fa.y - 0.8f * u), Size(faw * 1.4f, 1.6f * u))
        drawRect(Brush.horizontalGradient(listOf(c(OXIDO_OSC), c(OXIDO_LUZ), c(OXIDO)), fa.x - faw / 2, fa.x + faw / 2), Offset(fa.x - faw / 2, fa.y - 2.4f * u), Size(faw, 2.4f * u))
        val vid = Rect(fa.x - faw * 0.36f, fa.y - 8f * u, fa.x + faw * 0.36f, fa.y - 2.4f * u)
        drawOval(if (oscuro) Color(0xFFFFE2A0).copy(alpha = 0.55f) else Color(0xFFE9EDE6).copy(alpha = 0.6f), vid.topLeft, vid.size)
        llama(Offset(fa.x, fa.y - 3.4f * u), 2.6f * u, oscuro)
        drawOval(Color.White.copy(alpha = 0.5f), vid.topLeft, vid.size, style = Stroke(0.3f * u))
        listOf(-1f, 1f).forEach { d -> drawLine(c(OXIDO_OSC), Offset(fa.x + d * faw * 0.42f, fa.y - 2.4f * u), Offset(fa.x + d * faw * 0.42f, fa.y - 8.4f * u), 0.4f * u) }
        drawRect(c(OXIDO), Offset(fa.x - faw * 0.4f, fa.y - 9.2f * u), Size(faw * 0.8f, 1.2f * u))
        drawArc(c(ACERO_OSC), 180f, 180f, false, Offset(fa.x - faw * 0.4f, fa.y - 12f * u), Size(faw * 0.8f, 5f * u), style = Stroke(0.35f * u))
        }

        // La hiedra que cuelga de la losa por delante de la estantería.
        hiedra(listOf(Offset(w * 0.04f, losa), Offset(w * 0.05f, h * 0.3f), Offset(w * 0.03f, h * 0.55f)), 2.4f * u, oscuro, rnd)
        hiedra(listOf(Offset(w * 0.38f, losa), Offset(w * 0.39f, h * 0.22f)), 2.2f * u, oscuro, rnd)

        // El nicho: el hormigón visto, claro, con sus pasadores y las juntas del encofrado; cinco tramos iguales.
        drawRect(nicho(e.fondo, HORMIGON_LUZ, oscuro), n.topLeft, n.size)
        val cw = n.width / 5f
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 6) mancha(Offset(n.left + n.width * rnd.nextFloat(), n.top + n.height * rnd.nextFloat()), (5f + 8f * rnd.nextFloat()) * u, Color.Black.copy(alpha = if (oscuro) 0.06f else 0.04f))
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.14f else 0.07f), Offset(n.left, n.center.y), Offset(n.right, n.center.y), 0.3f * u)
            for (i in 0 until 5) {
                val r = Rect(n.left + cw * i, n.top, n.left + cw * (i + 1), n.bottom)
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Color.Transparent), r.top, r.top + 4.5f * u), r.topLeft, Size(r.width, 4.5f * u))
                listOf(0.2f, 0.8f).forEach { fx -> listOf(0.25f, 0.75f).forEach { fy -> drawCircle(Color.Black.copy(alpha = if (oscuro) 0.16f else 0.08f), 0.55f * u, Offset(r.left + r.width * fx, r.top + r.height * fy)) } }
            }
        }
        // Los montantes interiores, que separan los tramos.
        for (i in 1 until 5) {
            val x = n.left + cw * i
            montante(Rect(x - 1.3f * u, n.top, x + 1.3f * u, n.bottom), oscuro, rnd, u)
        }
        // Los montantes de los extremos y los rieles de arriba y de abajo.
        montante(Rect(n.left - 4.2f * u, n.top - 4.4f * u, n.left, h * 0.97f), oscuro, rnd, u)
        montante(Rect(n.right, n.top - 4.4f * u, n.right + 4.2f * u, h * 0.97f), oscuro, rnd, u)
        riel(Rect(n.left - 4.2f * u, n.top - 4.4f * u, n.right + 4.2f * u, n.top), oscuro, rnd, u)
        riel(Rect(n.left - 4.2f * u, n.bottom, n.right + 4.2f * u, n.bottom + 4.2f * u), oscuro, rnd, u)
        listOf(n.left - 2.1f * u, n.right + 2.1f * u).forEach { x -> listOf(n.top - 2.2f * u, n.bottom + 2.1f * u).forEach { y -> clavo(Offset(x, y), 0.6f * u, oscuro, false) } }

        // El suelo: tierra y cascajo, pasto, helechos y un ladrillo roto.
        val suelo = n.bottom + 4.2f * u
        drawRect(c(TIERRA), Offset(0f, suelo + 2f * u), Size(w, h - suelo - 2f * u))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), suelo + 2f * u, h), Offset(0f, suelo + 2f * u), Size(w, h - suelo - 2f * u))
        for (k in 0 until 18) {
            val p = Offset(w * rnd.nextFloat(), suelo + 3f * u + (h - suelo - 3f * u) * rnd.nextFloat())
            if (k % 3 == 0) drawOval(c(HORMIGON), Offset(p.x - 0.8f * u, p.y - 0.5f * u), Size(1.6f * u, 1f * u)) else hojaSeca(p, 1.1f * u, rnd.nextFloat() * 360f, c(HOJARASCA[k % HOJARASCA.size]))
        }
        val lad = Rect(w * 0.6f, h - 3.4f * u, w * 0.6f + 6f * u, h - 0.6f * u)
        drawPath(Path().apply { moveTo(lad.left, lad.top); lineTo(lad.right - 1.5f * u, lad.top); lineTo(lad.right, lad.center.y); lineTo(lad.right - 1f * u, lad.bottom); lineTo(lad.left, lad.bottom); close() }, c(LADRILLO))
        listOf(0.18f, 0.35f, 0.5f, 0.66f, 0.82f).forEach { f -> pasto(Offset(w * f, h), 4.2f * u, oscuro, rnd) }
        listOf(w * 0.02f to -60f, w * 0.98f to -120f).forEach { (x, a) ->
            for (j in 0 until 4) {
                val rad = Math.toRadians((a + (j - 1.5f) * 18f).toDouble())
                val b = Offset(x, h)
                val l = (9f + j % 2 * 2f) * u
                drawLine(c(HIERRO_TALLO), b, Offset(b.x + l * cos(rad).toFloat(), b.y + l * sin(rad).toFloat()), 0.35f * u)
                for (s in 1..7) {
                    val f = s / 8f
                    val q = Offset(b.x + l * f * cos(rad).toFloat(), b.y + l * f * sin(rad).toFloat())
                    val ll = l * 0.16f * (1f - f * 0.6f)
                    listOf(-1.1, 1.1).forEach { d -> drawLine(c(if (j % 2 == 0) MUSGO_LUZ else HIEDRA), q, Offset(q.x + ll * cos(rad + d).toFloat(), q.y + ll * sin(rad + d).toFloat()), 0.4f * u, StrokeCap.Round) }
                }
            }
        }
        hiedra(listOf(Offset(n.left - 2f * u, h), Offset(n.left - 1.4f * u, n.bottom - 4f * u), Offset(n.left - 2.4f * u, n.bottom - 14f * u)), 2.2f * u, oscuro, rnd)
        if (oscuro) listOf(0.18f to 0.2f, 0.62f to 0.24f, 0.88f to 0.15f, 0.4f to 0.95f).forEach { (fx, fy) -> luciernaga(Offset(w * fx, h * fy), 0.45f * u) }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f // un dp del ícono (el lienzo mide ≈ 70 dp de alto)
        val c = Offset(w / 2, h / 2)
        fun k(x: Color) = t(x, oscuro)
        val rnd = Random(23)
        when (id) {
            "unesco" -> {
                // El rascacielos roto con su retícula de ventanas, la hiedra que lo sube y el árbol en la azotea; al lado, un bloque bajo.
                val suelo = c.y + 28f * s
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(c.x - 46f * s, suelo - 2.5f * s), Size(92f * s, 5f * s))
                val bajo = Rect(c.x - 34f * s, suelo - 22f * s, c.x - 10f * s, suelo)
                drawRect(Brush.horizontalGradient(listOf(k(HORMIGON_LUZ), k(HORMIGON_OSC)), bajo.left, bajo.right), bajo.topLeft, bajo.size)
                for (i in 0 until 3) for (j in 0 until 3) drawRect(k(NOCHE).copy(alpha = 0.7f), Offset(bajo.left + 3f * s + i * 7f * s, bajo.top + 4f * s + j * 6f * s), Size(4f * s, 3.4f * s))
                val torre = Path().apply { moveTo(c.x - 12f * s, suelo); lineTo(c.x - 12f * s, c.y - 17f * s); lineTo(c.x - 6f * s, c.y - 21f * s); lineTo(c.x - 1f * s, c.y - 16f * s); lineTo(c.x + 4f * s, c.y - 23f * s); lineTo(c.x + 12f * s, c.y - 11f * s); lineTo(c.x + 12f * s, suelo); close() }
                drawPath(torre, Brush.horizontalGradient(listOf(k(HORMIGON_LUZ), k(HORMIGON), k(HORMIGON_OSC)), c.x - 12f * s, c.x + 12f * s))
                clipPath(torre) {
                    var y = c.y - 15f * s
                    while (y < suelo - 3f * s) {
                        for (i in 0 until 4) {
                            val roto = rnd.nextFloat() < 0.3f
                            drawRect(if (roto) k(NOCHE) else k(Color(0xFF5A6E78)).copy(alpha = 0.85f), Offset(c.x - 10f * s + i * 5.4f * s, y), Size(3.4f * s, 3f * s))
                        }
                        y += 5f * s
                    }
                }
                drawPath(torre, Color.Black.copy(alpha = 0.3f), style = Stroke(0.6f * s))
                listOf(-1f, 1f).forEach { d -> drawLine(k(OXIDO), Offset(c.x + d * 3f * s, c.y - 17f * s), Offset(c.x + d * 4.5f * s, c.y - 22f * s), 0.7f * s) }
                // La hiedra que sube por la torre.
                val sube = Path().apply { moveTo(c.x - 11f * s, suelo); quadraticTo(c.x - 4f * s, c.y + 6f * s, c.x - 9f * s, c.y - 10f * s) }
                drawPath(sube, k(Color(0xFF4A3A26)), style = Stroke(0.8f * s))
                listOf(0.15f, 0.35f, 0.55f, 0.75f, 0.92f).forEachIndexed { j, f ->
                    val q = Offset(c.x - 11f * s + 3f * s * sin(f * 4f), suelo - (suelo - c.y + 10f * s) * f)
                    hojaHiedra(Offset(q.x + (if (j % 2 == 0) 2f else -1f) * s, q.y), 4.4f * s, if (j % 2 == 0) 30f else -30f, if (j % 2 == 0) e.c1 else mezcla(e.c1, Color.Black, 0.25f))
                }
                // El árbol en la azotea.
                drawLine(k(Color(0xFF5A4636)), Offset(c.x + 4f * s, c.y - 17f * s), Offset(c.x + 6f * s, c.y - 23f * s), 1.4f * s)
                listOf(Offset(c.x + 2f * s, c.y - 24f * s) to 5.2f, Offset(c.x + 9.5f * s, c.y - 25f * s) to 4.8f, Offset(c.x + 6f * s, c.y - 28f * s) to 4.4f).forEach { (p, r) ->
                    drawCircle(e.c1, r * s, p); drawCircle(mezcla(e.c1, Color.White, 0.25f), r * 0.45f * s, Offset(p.x - r * 0.3f * s, p.y - r * 0.3f * s))
                }
                // A la derecha, el muro roto con óxido y el pasto.
                val muro = Path().apply { moveTo(c.x + 16f * s, suelo); lineTo(c.x + 16f * s, suelo - 14f * s); lineTo(c.x + 22f * s, suelo - 18f * s); lineTo(c.x + 26f * s, suelo - 11f * s); lineTo(c.x + 33f * s, suelo - 13f * s); lineTo(c.x + 36f * s, suelo); close() }
                drawPath(muro, Brush.verticalGradient(listOf(k(LADRILLO), k(mezcla(LADRILLO, Color.Black, 0.3f))), suelo - 18f * s, suelo))
                drawPath(muro, Color.Black.copy(alpha = 0.3f), style = Stroke(0.5f * s))
                drawRect(k(MUSGO), Offset(c.x - 46f * s, suelo - 1.5f * s), Size(92f * s, 2.5f * s))
                listOf(-40f, -22f, 14f, 38f).forEach { dx -> pasto(Offset(c.x + dx * s, suelo), 6f * s, oscuro, rnd) }
                hojaHiedra(Offset(c.x + 22f * s, suelo - 19f * s), 5f * s, -20f, e.c1)
                hojaHiedra(Offset(c.x + 27f * s, suelo - 13f * s), 4f * s, 30f, mezcla(e.c1, Color.Black, 0.25f))
                drawLine(e.c2, Offset(c.x + 30f * s, suelo - 12f * s), Offset(c.x + 30f * s, suelo - 20f * s), 0.8f * s)
                drawRect(e.c2, Offset(c.x + 30f * s, suelo - 20f * s), Size(6f * s, 3.6f * s))
            }
            "rascar" -> {
                // La placa de lámina atornillada, con el mapa y la capa de óxido que se raspa con una llave vieja.
                rotate(-4f, c) {
                    val pl = Rect(c.x - 34f * s, c.y - 26f * s, c.x + 34f * s, c.y + 26f * s)
                    drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(pl.left + 1.5f * s, pl.top + 2f * s), pl.size, CornerRadius(3f * s))
                    drawRoundRect(Brush.linearGradient(listOf(k(mezcla(HOJALATA, Color.White, 0.3f)), k(HOJALATA), k(HOJALATA_OSC)), pl.topLeft, pl.bottomRight), pl.topLeft, pl.size, CornerRadius(3f * s))
                    drawRoundRect(Color.Black.copy(alpha = 0.3f), pl.topLeft, pl.size, CornerRadius(3f * s), style = Stroke(0.7f * s))
                    val tierra = Path().apply {
                        moveTo(pl.left + 8f * s, pl.top + 14f * s); cubicTo(pl.left + 18f * s, pl.top + 4f * s, pl.left + 32f * s, pl.top + 14f * s, pl.left + 42f * s, pl.top + 7f * s)
                        cubicTo(pl.right - 6f * s, pl.top + 4f * s, pl.right - 4f * s, pl.top + 26f * s, pl.right - 14f * s, pl.top + 36f * s)
                        cubicTo(pl.left + 32f * s, pl.bottom - 4f * s, pl.left + 10f * s, pl.bottom - 12f * s, pl.left + 8f * s, pl.top + 14f * s); close()
                    }
                    drawPath(tierra, e.c1.copy(alpha = 0.4f)); drawPath(tierra, e.c1, style = Stroke(0.9f * s))
                    val capa = Rect(pl.center.x - 4f * s, pl.top + 3f * s, pl.right - 3f * s, pl.bottom - 3f * s)
                    val raspado = Path().apply {
                        moveTo(capa.left + 2f * s, capa.top + 8f * s); lineTo(capa.left + 14f * s, capa.top + 4f * s); lineTo(capa.left + 20f * s, capa.top + 14f * s)
                        lineTo(capa.left + 12f * s, capa.top + 22f * s); lineTo(capa.left + 18f * s, capa.bottom - 5f * s); lineTo(capa.left + 4f * s, capa.bottom - 2f * s); close()
                    }
                    clipPath(raspado, clipOp = ClipOp.Difference) {
                        drawRect(Brush.linearGradient(listOf(k(OXIDO_LUZ), k(OXIDO), k(OXIDO_OSC)), capa.topLeft, capa.bottomRight), capa.topLeft, capa.size)
                        for (j in 0 until 14) drawCircle(k(if (j % 2 == 0) OXIDO_OSC else OXIDO_LUZ).copy(alpha = 0.7f), (0.8f + 1.6f * rnd.nextFloat()) * s, Offset(capa.left + capa.width * rnd.nextFloat(), capa.top + capa.height * rnd.nextFloat()))
                    }
                    drawPath(raspado, k(OXIDO_OSC).copy(alpha = 0.7f), style = Stroke(0.5f * s))
                    listOf(pl.topLeft + Offset(3.4f * s, 3.4f * s), Offset(pl.right - 3.4f * s, pl.top + 3.4f * s), Offset(pl.left + 3.4f * s, pl.bottom - 3.4f * s), Offset(pl.right - 3.4f * s, pl.bottom - 3.4f * s)).forEach { b ->
                        drawCircle(k(ACERO_OSC), 1.8f * s, b); drawLine(k(NOCHE), Offset(b.x - 1.2f * s, b.y), Offset(b.x + 1.2f * s, b.y), 0.5f * s)
                    }
                }
                // La llave vieja, con su ojo redondo y sus dientes.
                val kc = Offset(c.x + 30f * s, c.y + 14f * s)
                rotate(-35f, kc) {
                    drawCircle(Color.Black.copy(alpha = 0.3f), 5.4f * s, Offset(kc.x + 1f * s, kc.y + 1.5f * s))
                    drawCircle(Brush.radialGradient(listOf(mezcla(e.c2, Color.White, 0.35f), e.c2, mezcla(e.c2, Color.Black, 0.35f)), Offset(kc.x - 2f * s, kc.y - 2f * s), 7f * s), 5f * s, kc)
                    drawCircle(Color.Black.copy(alpha = 0.6f), 2f * s, kc)
                    drawRect(e.c2, Offset(kc.x + 4f * s, kc.y - 1.3f * s), Size(18f * s, 2.6f * s))
                    drawRect(e.c2, Offset(kc.x + 17f * s, kc.y + 1f * s), Size(2.2f * s, 4f * s))
                    drawRect(e.c2, Offset(kc.x + 20f * s, kc.y + 1f * s), Size(2f * s, 3f * s))
                    drawLine(Color.White.copy(alpha = 0.4f), Offset(kc.x + 5f * s, kc.y - 0.8f * s), Offset(kc.x + 20f * s, kc.y - 0.8f * s), 0.5f * s)
                }
            }
            else -> {
                // El mapa arrugado con sus dobleces, la ruta y tres banderitas de trapo clavadas.
                val pl = Rect(c.x - 44f * s, c.y - 25f * s, c.x + 44f * s, c.y + 27f * s)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(pl.left + 1.5f * s, pl.top + 2f * s), pl.size)
                drawRect(k(PAPEL_VIEJO), pl.topLeft, pl.size)
                clipRect(pl.left, pl.top, pl.right, pl.bottom) {
                    val agua = Path().apply { moveTo(pl.left, pl.bottom - 12f * s); cubicTo(pl.left + 22f * s, pl.bottom - 22f * s, pl.left + 38f * s, pl.bottom - 2f * s, pl.left + 64f * s, pl.bottom); lineTo(pl.left, pl.bottom); close() }
                    drawPath(agua, k(Color(0xFF6E9AA0)).copy(alpha = 0.55f))
                    for (j in 0 until 10) drawCircle(k(MUSGO).copy(alpha = 0.3f), (1.4f + 1.8f * rnd.nextFloat()) * s, Offset(pl.left + pl.width * rnd.nextFloat(), pl.top + pl.height * 0.6f * rnd.nextFloat()))
                    listOf(0.25f, 0.5f, 0.75f).forEach { f ->
                        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.White.copy(alpha = 0.2f), Color.Transparent), pl.left + pl.width * f - 2f * s, pl.left + pl.width * f + 2f * s), Offset(pl.left + pl.width * f - 2f * s, pl.top), Size(4f * s, pl.height))
                    }
                    drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.White.copy(alpha = 0.2f), Color.Transparent), pl.center.y - 2f * s, pl.center.y + 2f * s), Offset(pl.left, pl.center.y - 2f * s), Size(pl.width, 4f * s))
                    mancha(Offset(pl.right - 12f * s, pl.top + 10f * s), 8f * s, k(Color(0xFF8A6A30)).copy(alpha = 0.3f))
                }
                drawRect(Color.Black.copy(alpha = 0.3f), pl.topLeft, pl.size, style = Stroke(0.6f * s))
                val ms = listOf(Offset(pl.left + 16f * s, pl.top + 30f * s) to e.c1, Offset(pl.left + 44f * s, pl.top + 18f * s) to e.c2, Offset(pl.right - 14f * s, pl.top + 34f * s) to k(AMARILLO_VIAL))
                val ruta = Path().apply { moveTo(ms[0].first.x, ms[0].first.y); ms.drop(1).forEach { lineTo(it.first.x, it.first.y) } }
                drawPath(ruta, k(TINTA).copy(alpha = 0.6f), style = Stroke(0.9f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * s, 2f * s))))
                ms.forEach { (p, col) ->
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(p.x - 1.5f * s, p.y - 0.8f * s), Size(5f * s, 1.8f * s))
                    drawLine(k(MADERA_OSC), p, Offset(p.x, p.y - 16f * s), 1f * s)
                    val trapo = Path().apply { moveTo(p.x, p.y - 16f * s); quadraticTo(p.x + 5f * s, p.y - 17.5f * s, p.x + 10f * s, p.y - 15f * s); lineTo(p.x + 8f * s, p.y - 12.5f * s); lineTo(p.x + 10f * s, p.y - 10f * s); quadraticTo(p.x + 5f * s, p.y - 12f * s, p.x, p.y - 10f * s); close() }
                    drawPath(trapo, Brush.linearGradient(listOf(mezcla(col, Color.White, 0.2f), col, mezcla(col, Color.Black, 0.3f)), Offset(p.x, p.y - 16f * s), Offset(p.x + 10f * s, p.y - 10f * s)))
                    drawPath(trapo, Color.Black.copy(alpha = 0.25f), style = Stroke(0.4f * s))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el corcho rescatado

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.NOTAS))
        val rnd = Random(e.semilla.toLong())

        // El muro de hormigón de fondo, con manchas.
        drawRect(c(HORMIGON))
        for (k in 0 until 8) mancha(Offset(w * rnd.nextFloat(), h * rnd.nextFloat()), (8f + 12f * rnd.nextFloat()) * u, c(HORMIGON_OSC).copy(alpha = 0.3f))

        // El marco de tablas rescatadas: los largueros de arriba y de abajo montan sobre los de los lados, clavados.
        val marco = Rect(w * 0.015f, h * 0.075f, w * 0.985f, n.bottom + 4.6f * u)
        sombra(marco, 3f * u, if (oscuro) 0.6f else 0.35f)
        val iz = Rect(marco.left, marco.top + 1f * u, n.left, marco.bottom - 1f * u)
        val de = Rect(n.right, marco.top + 2f * u, marco.right, marco.bottom)
        tabla(iz, MADERA, oscuro, rnd, u, vertical = true)
        tabla(de, mezcla(MADERA, MADERA_LUZ, 0.3f), oscuro, rnd, u, vertical = true)
        // El nicho: el corcho, claro, con su grano apenas visible.
        drawRect(nicho(e.fondo, CORCHO, oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 220) drawCircle((if (k % 3 == 0) Color.White else Color.Black).copy(alpha = if (oscuro) 0.05f else 0.035f), (0.2f + 0.35f * rnd.nextFloat()) * u, Offset(n.left + n.width * rnd.nextFloat(), n.top + n.height * rnd.nextFloat()))
            for (k in 0 until 3) mancha(Offset(n.left + n.width * rnd.nextFloat(), n.top + n.height * rnd.nextFloat()), 10f * u, Color.Black.copy(alpha = if (oscuro) 0.05f else 0.035f))
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Color.Transparent), n.top, n.top + 3f * u), n.topLeft, Size(n.width, 3f * u))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), Color.Transparent), n.left, n.left + 2.4f * u), n.topLeft, Size(2.4f * u, n.height))
        val ar = Rect(0f, marco.top, w, n.top)
        rotate(-0.6f, ar.center) { tabla(ar, MADERA_LUZ, oscuro, rnd, u, pintura = PUERTA) }
        // La repisa de abajo: la cara de arriba, donde descansan las cosas, y el frente.
        val repA = Rect(marco.left - 0.6f * u, n.bottom, marco.right + 0.6f * u, n.bottom + 3f * u)
        val repF = Rect(marco.left - 0.6f * u, repA.bottom, marco.right + 0.6f * u, marco.bottom)
        tabla(repA, mezcla(MADERA_LUZ, Color.White, 0.08f), oscuro, rnd, u)
        tabla(repF, MADERA_OSC, oscuro, rnd, u)
        listOf(iz.center.x, de.center.x).forEach { x ->
            clavo(Offset(x - 1.2f * u, ar.center.y - 1f * u), 0.6f * u, oscuro); clavo(Offset(x + 1.2f * u, ar.center.y + 1f * u), 0.6f * u, oscuro)
            clavo(Offset(x, repF.center.y), 0.6f * u, oscuro)
        }
        // Las chapas de lámina oxidada clavadas en las esquinas de arriba, como refuerzo.
        listOf(Offset(marco.left, marco.top) to 1f, Offset(marco.right, marco.top) to -1f).forEach { (q, d) ->
            val l = 7f * u
            val ch = Path().apply { moveTo(q.x, q.y); lineTo(q.x + d * l, q.y); lineTo(q.x + d * l * 0.8f, q.y + 2.6f * u); lineTo(q.x + d * 2.6f * u, q.y + 2.6f * u); lineTo(q.x + d * 2.6f * u, q.y + l * 0.9f); lineTo(q.x, q.y + l); close() }
            drawPath(ch, Color.Black.copy(alpha = 0.35f))
            drawPath(ch, Brush.linearGradient(listOf(c(OXIDO_LUZ), c(OXIDO), c(OXIDO_OSC)), q, Offset(q.x + d * l, q.y + l)))
            drawPath(ch, Color.Black.copy(alpha = 0.3f), style = Stroke(0.3f * u))
            clavo(Offset(q.x + d * 1.3f * u, q.y + 1.3f * u), 0.5f * u, oscuro, false)
            clavo(Offset(q.x + d * 5.4f * u, q.y + 1.3f * u), 0.5f * u, oscuro, false)
            clavo(Offset(q.x + d * 1.3f * u, q.y + 5.6f * u), 0.5f * u, oscuro, false)
        }
        musgo(Offset(w * 0.62f, ar.top + 0.4f * u), 24f * u, 1.6f * u, oscuro, rnd, 22)
        musgo(Offset(w * 0.2f, repF.bottom - 0.4f * u), 18f * u, 1.6f * u, oscuro, rnd, 20)
        musgo(Offset(marco.right - 1.6f * u, h * 0.5f), 2.4f * u, 12f * u, oscuro, rnd, 14)
        // En la repisa: el cabo de lápiz, unos clavos sueltos y una piña.
        val ry = repA.center.y
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(w * 0.3f, ry + 0.8f * u), Offset(w * 0.42f, ry + 0.8f * u), 1.2f * u, StrokeCap.Round)
        drawLine(c(AMARILLO_VIAL), Offset(w * 0.3f, ry), Offset(w * 0.4f, ry), 1.2f * u)
        drawPath(Path().apply { moveTo(w * 0.4f, ry - 0.6f * u); lineTo(w * 0.4f + 1.8f * u, ry); lineTo(w * 0.4f, ry + 0.6f * u); close() }, c(Color(0xFFE2C9A0)))
        drawCircle(c(NEUMATICO), 0.25f * u, Offset(w * 0.4f + 1.7f * u, ry))
        listOf(0.52f to 10f, 0.55f to -20f, 0.57f to 40f).forEach { (f, a) -> rotate(a, Offset(w * f, ry)) { drawLine(c(OXIDO), Offset(w * f - 1.4f * u, ry), Offset(w * f + 1.4f * u, ry), 0.3f * u); drawLine(c(OXIDO_OSC), Offset(w * f - 1.4f * u, ry - 0.4f * u), Offset(w * f - 1.4f * u, ry + 0.4f * u), 0.35f * u) } }
        val pi = Offset(w * 0.72f, ry - 0.2f * u)
        drawOval(c(Color(0xFF6A4A2A)), Offset(pi.x - 2.4f * u, pi.y - 1.2f * u), Size(4.8f * u, 2.4f * u))
        for (k in 0 until 5) drawArc(c(Color(0xFF3E2A18)), 90f, 180f, false, Offset(pi.x - 2.2f * u + k * 0.9f * u, pi.y - 1.1f * u), Size(0.9f * u, 2.2f * u), style = Stroke(0.2f * u))

        // El farol de lata colgado de un clavo arriba al centro, con sus agujeros de luz.
        val fc = Offset(w / 2, ar.top + 0.8f * u)
        clavo(fc, 0.6f * u, oscuro, false)
        val fl = Rect(w / 2 - 2.6f * u, ar.top + 2.6f * u, w / 2 + 2.6f * u, ar.bottom - 1.4f * u)
        drawArc(c(ACERO_OSC), 180f, 180f, false, Offset(fl.left + 0.6f * u, fc.y - 0.2f * u), Size(fl.width - 1.2f * u, (fl.top - fc.y) * 2f + 0.4f * u), style = Stroke(0.3f * u))
        if (oscuro) drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = 0.5f), Color.Transparent), fl.center, 14f * u), 14f * u, fl.center)
        lata(fl, oscuro, rnd, u)
        for (i in 0 until 3) for (j in 0 until 3) {
            val p = Offset(fl.left + fl.width * (0.25f + 0.25f * i), fl.top + fl.height * (0.25f + 0.25f * j))
            drawCircle(if (oscuro) Color(0xFFFFD27A) else c(NEUMATICO).copy(alpha = 0.7f), 0.35f * u, p)
        }
        // La hiedra que baja por la esquina de arriba a la izquierda y la del costado derecho.
        hiedra(listOf(Offset(0f, h * 0.02f), Offset(w * 0.08f, h * 0.12f), Offset(w * 0.05f, h * 0.35f), Offset(w * 0.035f, h * 0.6f)), 2.4f * u, oscuro, rnd)
        hiedra(listOf(Offset(w * 0.12f, 0f), Offset(w * 0.2f, h * 0.07f), Offset(w * 0.3f, h * 0.09f)), 2.2f * u, oscuro, rnd)
        hiedra(listOf(Offset(w, h * 0.3f), Offset(w * 0.965f, h * 0.45f), Offset(w * 0.975f, h * 0.7f)), 2.2f * u, oscuro, rnd)
        // La tierra y el pasto al pie del muro, con hojas secas.
        drawRect(c(TIERRA), Offset(0f, h - 2.4f * u), Size(w, 2.4f * u))
        for (k in 0 until 8) hojaSeca(Offset(w * rnd.nextFloat(), h - 1.2f * u), 1f * u, rnd.nextFloat() * 360f, c(HOJARASCA[k % HOJARASCA.size]))
        listOf(0.04f, 0.12f, 0.27f, 0.5f, 0.66f, 0.84f, 0.96f).forEach { f -> pasto(Offset(w * f, h), 5f * u, oscuro, rnd) }
        if (oscuro) listOf(0.2f to 0.03f, 0.8f to 0.04f, 0.97f to 0.85f).forEach { (fx, fy) -> luciernaga(Offset(w * fx, h * fy), 0.45f * u) }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        val u = minOf(r0.width, r0.height) / 100f
        val r = Rect(r0.left + r0.width * 0.035f, r0.top + r0.height * 0.045f, r0.right - r0.width * 0.035f, r0.bottom - r0.height * 0.035f)
        val gir = if (e.fijada) 0f else ((e.indice * 37) % 5 - 2) * 0.9f
        fun p(x: Color) = if (oscuro) mezcla(x, NOCHE, 0.3f) else x
        fun k(x: Color) = t(x, oscuro)
        val rnd = Random(e.indice * 31L + 7L)
        val papel = p(when (e.tipo) {
            "idea" -> mezcla(CARTON, PAPEL, 0.2f)
            "proposito" -> PAPEL_VIEJO
            "recordatorio" -> mezcla(CARTON, PAPEL, 0.45f)
            else -> PAPEL
        })
        rotate(gir, r.center) {
            // El contorno: rasgado arriba en la hoja de cuaderno, rasgado entero en el cartón, recto en lo demás.
            val forma = Path().apply {
                when (e.tipo) {
                    "idea" -> {
                        moveTo(r.left, r.top + 1f * u)
                        val n = 10
                        for (j in 1..n) lineTo(r.left + r.width * j / n, r.top + (if (j % 2 == 0) 0f else 1.4f * u) * rnd.nextFloat())
                        for (j in 1..n) lineTo(r.right - (if (j % 2 == 0) 0f else 1.2f * u) * rnd.nextFloat(), r.top + r.height * j / n)
                        for (j in 1..n) lineTo(r.right - r.width * j / n, r.bottom - (if (j % 2 == 0) 0f else 1.6f * u) * rnd.nextFloat())
                        for (j in 1 until n) lineTo(r.left + (if (j % 2 == 0) 0f else 1.2f * u) * rnd.nextFloat(), r.bottom - r.height * j / n)
                        close()
                    }
                    "pensamiento" -> {
                        moveTo(r.left, r.top + 1.6f * u)
                        val n = 14
                        for (j in 1..n) lineTo(r.left + r.width * j / n, r.top + (0.4f + 1.4f * rnd.nextFloat()) * u)
                        lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
                    }
                    "recordatorio" -> {
                        val cc = r.width * 0.12f
                        moveTo(r.left + cc, r.top); lineTo(r.right - cc, r.top); lineTo(r.right, r.top + cc); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.top + cc); close()
                    }
                    else -> addRect(r)
                }
            }
            translateSombra(forma, Offset(1.4f * u, 2.2f * u), Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f))
            drawPath(forma, papel)
            clipPath(forma) {
                drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.2f), Color.Transparent, Color.Black.copy(alpha = 0.08f)), r.topLeft, r.bottomRight), r.topLeft, r.size)
                when (e.tipo) {
                    "idea" -> {
                        // La onda del cartón corrugado que asoma en el borde rasgado de arriba.
                        var x = r.left
                        while (x < r.right) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(x, r.top), Offset(x, r.top + r.height * 0.05f), 0.5f * u); x += 1.6f * u }
                        drawRect(Color.Black.copy(alpha = 0.06f), r.topLeft, Size(r.width, r.height * 0.05f))
                    }
                    "pensamiento" -> {
                        // Los renglones azules y el margen rojo del cuaderno.
                        var y = r.top + r.height * 0.22f
                        while (y < r.bottom - 1f * u) { drawLine(k(Color(0xFF6E8AB0)).copy(alpha = 0.3f), Offset(r.left, y), Offset(r.right, y), 0.25f * u); y += r.height * 0.105f }
                        drawLine(k(Color(0xFFB3261E)).copy(alpha = 0.35f), Offset(r.left + r.width * 0.085f, r.top), Offset(r.left + r.width * 0.085f, r.bottom), 0.3f * u)
                    }
                    "proposito" -> {
                        // El cerco de agua y las pecas del papel viejo.
                        drawCircle(k(Color(0xFF8A6A30)).copy(alpha = 0.3f), r.width * 0.14f, Offset(r.right - r.width * 0.14f, r.bottom - r.height * 0.12f), style = Stroke(0.8f * u))
                        for (j in 0 until 7) drawCircle(k(Color(0xFF8A6A30)).copy(alpha = 0.15f), (0.4f + 0.8f * rnd.nextFloat()) * u, Offset(r.left + r.width * rnd.nextFloat(), r.top + r.height * rnd.nextFloat()))
                        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, k(Color(0xFF8A6A30)).copy(alpha = 0.12f)), r.right - r.width * 0.1f, r.right), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
                    }
                    "recordatorio" -> {
                        // La marca de pintura en aerosol, abajo a la derecha.
                        val m = Offset(r.right - r.width * 0.12f, r.bottom - r.height * 0.09f)
                        val col = k(mezcla(e.acento, OXIDO, 0.5f)).copy(alpha = 0.8f)
                        drawLine(col, Offset(m.x - r.width * 0.05f, m.y - r.width * 0.05f), Offset(m.x + r.width * 0.05f, m.y + r.width * 0.05f), 1.1f * u, StrokeCap.Round)
                        drawLine(col, Offset(m.x + r.width * 0.05f, m.y - r.width * 0.05f), Offset(m.x - r.width * 0.05f, m.y + r.width * 0.05f), 1.1f * u, StrokeCap.Round)
                        drawCircle(col.copy(alpha = 0.25f), r.width * 0.09f, m)
                    }
                    else -> {}
                }
            }
            drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(0.3f * u))
            if (e.tipo == "pensamiento") {
                // Los agujeros del espiral, arriba.
                var x = r.left + r.width * 0.08f
                while (x < r.right - r.width * 0.05f) { drawCircle(Color.Black.copy(alpha = 0.35f), 0.5f * u, Offset(x, r.top + r.height * 0.065f)); x += r.width * 0.09f }
            }
            if (e.tipo == "recordatorio") {
                val oj = Offset(r.center.x, r.top + r.height * 0.075f)
                drawCircle(k(CARTON), r.width * 0.05f, oj)
                drawCircle(Color.Black.copy(alpha = 0.25f), r.width * 0.05f, oj, style = Stroke(0.3f * u))
                drawCircle(Color.Black.copy(alpha = 0.6f), r.width * 0.02f, oj)
                listOf(-1f, 1f).forEach { d ->
                    drawPath(Path().apply { moveTo(oj.x, oj.y); cubicTo(oj.x + d * r.width * 0.12f, oj.y - r.height * 0.02f, oj.x + d * r.width * 0.06f, r0.top + 1f * u, oj.x + d * r.width * 0.015f, r0.top + 0.5f * u) }, k(Color(0xFFB39A70)), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                }
            }
            if (e.fijada) {
                // La cinta plateada en la esquina y una hoja de trébol metida en la otra.
                rotate(-38f, Offset(r.left + r.width * 0.06f, r.top + r.height * 0.06f)) {
                    val ci = Rect(r.left - r.width * 0.1f, r.top + r.height * 0.02f, r.left + r.width * 0.24f, r.top + r.height * 0.1f)
                    drawRect(k(CINTA_GRIS).copy(alpha = 0.95f), ci.topLeft, ci.size)
                    var x = ci.left
                    while (x < ci.right) { drawLine(Color.White.copy(alpha = 0.18f), Offset(x, ci.top), Offset(x, ci.bottom), 0.2f * u); x += 0.8f * u }
                    drawRect(Color.White.copy(alpha = 0.25f), ci.topLeft, Size(ci.width, ci.height * 0.2f))
                    drawRect(Color.Black.copy(alpha = 0.15f), ci.topLeft, ci.size, style = Stroke(0.2f * u))
                }
                val tc = Offset(r.right - r.width * 0.08f, r.top + r.height * 0.065f)
                listOf(-90f, 30f, 150f).forEach { a ->
                    val rad = Math.toRadians(a.toDouble())
                    drawCircle(k(if (a == -90f) HIEDRA_LUZ else HIEDRA), r.width * 0.032f, Offset(tc.x + r.width * 0.03f * cos(rad).toFloat(), tc.y + r.width * 0.03f * sin(rad).toFloat()))
                }
                drawLine(k(HIEDRA), tc, Offset(tc.x + r.width * 0.02f, tc.y + r.width * 0.08f), 0.4f * u)
            } else if (e.tipo != "recordatorio") {
                clavo(Offset(r.center.x, r.top + r.height * 0.05f), 2.8f * u, oscuro)
            }
        }
    }

    /** La sombra de una forma, corrida un poco hacia abajo. */
    private fun DrawScope.translateSombra(forma: Path, d: Offset, color: Color) {
        val sombraPath = Path().apply { addPath(forma, d) }
        drawPath(sombraPath, color)
    }

    /** El texto de las notas: lápiz de grafito sobre el papel rescatado; el papel se oscurece poco de noche y el texto sigue. */
    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFF101410) else TINTA
}
