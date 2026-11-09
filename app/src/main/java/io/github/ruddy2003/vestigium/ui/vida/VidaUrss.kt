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
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Soviético constructivista: los muebles de Vida y de Diario de la casa de cultura, de la misma casa que su kartoteka de
 * acero del Ideario, su estante de nogal con la banderola y su pizarra de honor del metro: nogal pulido con filetes de
 * latón, mármol de las estaciones con sus franjas de oro, granito rojo y negro moteados, paño verde de escritorio,
 * baquelita, el rojo del cartel con sus estrellas, espigas de oro y la luz de la lámpara de pantalla verde.
 * - Social: la sala del club con el periódico mural: la cornisa de granito, la banderola roja con su barra de latón, sus
 *   bandas y sus tres estrellas y el fleco de oro; el gran tablero de nogal con sus escuadras de latón y sus cuñas rojas,
 *   las pilastras de mármol acanaladas con capitel escalonado y basa de granito, los apliques de globo (de noche,
 *   encendidos), el zócalo de nogal, el parquet en espiga y la alfombra roja con su orla verde.
 * - Mapas: el escritorio del Gosplan: el tablero de nogal con el paño verde y su filete de oro, el plano clavado con
 *   chinchetas de latón y su recuadro de dibujo; a un lado, la lámpara de pantalla verde (de noche, encendida) y la
 *   escribanía de mármol con sus tinteros; al otro, el teléfono de baquelita con su disco y el vaso de té en su
 *   portavasos; el canto con su filete de latón y las dos cajoneras con tiradores y bocallaves.
 * - Otras vidas: la pizarra de honor: el frontón escalonado de granito con los rayos de oro, la gran estrella roja con su
 *   corona de espigas y su cinta, la faja roja con sus estrellas, cinco recuadros con su marco de latón separados por
 *   montantes de nogal, las pilastras de mármol y el plinto de granito rojo con su placa.
 * - Íconos: el rascacielos de la universidad con sus alas y la aguja con la estrella; el mapa raspado por una moneda de
 *   latón con su estrella y sus espigas; tres chinchetas de estrella unidas por la vía del tren.
 * - Notas: el tablón de avisos: el marco de nogal con escuadras de latón, el copete rojo con su cuña negra y sus estrellas,
 *   la bandera roja en su asta y el altavoz de bocina; el contrachapado claro y la repisa con el lápiz rojo y el sello;
 *   las notas son impresos con su raya roja, su casilla y su pauta, prendidos con una chincheta de latón o, si están
 *   fijados, con la insignia de la estrella y la franja roja del margen, y con un detalle según el tipo (el sello de la
 *   estrella, el sello violeta, las barras del plan o la esquina doblada).
 */
internal object VidaUrss : DisenoVida {
    // ---------------------------------------------------------------- materiales (los de su Ideario, su estante y su Hall)
    private val NOGAL = Color(0xFF5B3A26)
    private val NOGAL_LUZ = Color(0xFF7C5236)
    private val NOGAL_VETA = Color(0xFF3A2416)
    private val NOGAL_OSC = Color(0xFF3E2618)
    private val ROJO = Color(0xFFB0241E)
    private val ROJO_LUZ = Color(0xFFD23A2E)
    private val ROJO_OSC = Color(0xFF7E1914)
    private val ORO = Color(0xFFD9A93E)
    private val ORO_LUZ = Color(0xFFF0CF7A)
    private val ORO_OSC = Color(0xFF8E6A22)
    private val LATON = Color(0xFFC4A052)
    private val LATON_LUZ = Color(0xFFE6CB86)
    private val LATON_OSC = Color(0xFF7A6430)
    private val GRANITO_ROJO = Color(0xFF7A2A26)
    private val GRANITO_NEGRO = Color(0xFF2A2624)
    private val GRANITO_GRIS = Color(0xFF8A8580)
    private val MARMOL = Color(0xFFD8D0C0)
    private val MARMOL_SOMBRA = Color(0xFFB4AA96)
    private val MARMOL_VETA = Color(0xFF8A7A60)
    private val PARED = Color(0xFFD8CCAE)
    private val CREMA = Color(0xFFEFE4C9)
    private val CREMA_OSC = Color(0xFFD8CCB0)
    private val BAQUELITA = Color(0xFF2A2420)
    private val PANTALLA = Color(0xFF2E6B4A)
    private val PANTALLA_LUZ = Color(0xFF5FA07A)
    private val PANO = Color(0xFF2F5A40)
    private val PANO_OSC = Color(0xFF22442F)
    private val PARQUET = Color(0xFF8A6438)
    private val PARQUET_OSC = Color(0xFF5E4024)
    private val ALFOMBRA = Color(0xFF8E2A24)
    private val ALFOMBRA_ORLA = Color(0xFF2E4E36)
    private val TINTA = Color(0xFF1E1C1A)
    private val VIOLETA = Color(0xFF6A4FA0)
    private val NEGRO = Color(0xFF161414)
    private val TE = Color(0xFFB0611C)
    private val ACERO_GRIS = Color(0xFF7E878A)
    private val LUZ = Color(0xFFF6E2A0)
    private val NOCHE = Color(0xFF0D0C0B)

    /** El papel del periódico mural (el nicho de Social): crema de día; de noche, en la penumbra del club. */
    private fun carteNicho(oscuro: Boolean) = if (oscuro) Color(0xFF241E1C) else Color(0xFFF6EEDC)

    /** El papel de dibujo del plano. */
    private fun mapaNicho(oscuro: Boolean) = if (oscuro) Color(0xFF22201D) else Color(0xFFF4EEDC)

    /** El fondo de los recuadros de la pizarra de honor. */
    private fun honorNicho(oscuro: Boolean) = if (oscuro) Color(0xFF211C1B) else Color(0xFFF3EAD6)

    /** El contrachapado del tablón de avisos. */
    private fun tablonNicho(oscuro: Boolean) = if (oscuro) Color(0xFF201B1A) else Color(0xFFF0E7D2)

    /** El color en oscuro: más hundido, como en su biblioteca y su Hall. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.34f) else c

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** La pared de estuco: ocre de día; de noche, en penumbra (la alumbran las lámparas). */
    private fun pared(oscuro: Boolean) = if (oscuro) mezcla(PARED, NOCHE, 0.68f) else PARED

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.15f, 0.245f, 0.85f, 0.8f)
        MuebleVida.MAPAS -> Rect(0.19f, 0.1f, 0.81f, 0.7f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.075f, 0.235f, 0.925f, 0.8f)
        MuebleVida.NOTAS -> Rect(0.065f, 0.175f, 0.935f, 0.895f)
    }

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    // ---------------------------------------------------------------- ayudas de dibujo

    /** Un número estable de 0 a 1 para lo que varía (vetas, motas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Las vetas de la madera en [r], recortadas a la pieza: líneas onduladas a lo largo, más o menos marcadas. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, densidad: Float = 1f, vertical: Boolean = false, grosor: Float = 1f, ondulacion: Float = 1f) =
        clipRect(r.left, r.top, r.right, r.bottom) {
            val d = density
            val largo = if (vertical) r.height else r.width
            val ancho = if (vertical) r.width else r.height
            val n = (ancho / (3.2f * d) * densidad).toInt().coerceIn(2, 60)
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.6f + 1.8f * azar(semilla, k + 100)) * d * ondulacion
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.5f + 2.5f * azar(semilla, k + 300)
                val p = Path()
                for (s in 0..18) {
                    val a = largo * s / 18
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 18)
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.25f + 0.75f * azar(semilla, k + 400))), style = Stroke(grosor * d * (0.5f + azar(semilla, k + 500))))
            }
        }

    /** Una tabla de nogal en [r]: el color y su veta. */
    private fun DrawScope.nogal(r: Rect, oscuro: Boolean, semilla: Int, vertical: Boolean = false, color: Color = NOGAL) {
        drawRect(t(color, oscuro), r.topLeft, r.size)
        vetas(r, t(NOGAL_VETA, oscuro).copy(alpha = 0.55f), semilla, densidad = 0.8f, vertical = vertical, grosor = 0.7f, ondulacion = 0.8f)
    }

    /** El granito pulido en [r]: el color, el moteado claro y oscuro y el brillo de arriba. */
    private fun DrawScope.granito(r: Rect, base: Color, oscuro: Boolean, semilla: Int) {
        drawRect(t(base, oscuro), r.topLeft, r.size)
        val u = size.minDimension / 100f
        val n = (r.width * r.height / (2.2f * u * 2.2f * u)).toInt().coerceIn(10, 220)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val claro = azar(semilla, k + 500) > 0.5f
                drawCircle(t(mezcla(base, if (claro) Color.White else Color.Black, 0.3f), oscuro).copy(alpha = 0.7f), 0.3f * u * (0.6f + azar(semilla, k + 600)),
                    Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla, k + 300)))
            }
        }
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent), r.top, r.top + r.height * 0.4f), r.topLeft, Size(r.width, r.height * 0.4f))
    }

    /** El mármol claro en [r], con sus nubes y sus vetas en diagonal. */
    private fun DrawScope.marmol(r: Rect, oscuro: Boolean, semilla: Int) {
        drawRect(t(MARMOL, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until 5) {
                val y0 = r.top + r.height * azar(semilla, k)
                val p = Path().apply {
                    moveTo(r.left, y0)
                    cubicTo(r.left + r.width * 0.4f, y0 + r.height * (azar(semilla, k + 9) - 0.3f) * 0.3f, r.left + r.width * 0.6f, y0 + r.height * 0.12f, r.right, y0 + r.height * (azar(semilla, k + 19) * 0.25f))
                }
                drawPath(p, t(MARMOL_VETA, oscuro).copy(alpha = 0.4f), style = Stroke((0.4f + 0.6f * azar(semilla, k + 29)) * density))
            }
        }
    }

    /** Una pieza de latón en [r]: el metal, su luz arriba y su sombra abajo. */
    private fun DrawScope.laton(r: Rect, oscuro: Boolean, radio: Float = 0f) {
        drawRoundRect(t(LATON_OSC, oscuro), r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(t(LATON, oscuro), r.topLeft, Size(r.width, r.height * 0.78f), CornerRadius(radio))
        drawRoundRect(t(LATON_LUZ, oscuro), Offset(r.left + r.width * 0.05f, r.top + r.height * 0.12f), Size(r.width * 0.9f, r.height * 0.18f), CornerRadius(radio))
    }

    /** Un tornillo de latón de cabeza ranurada en [c]. */
    private fun DrawScope.tornillo(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(t(LATON_OSC, oscuro), r, c)
        drawCircle(t(LATON, oscuro), r * 0.78f, c - Offset(r * 0.1f, r * 0.1f))
        drawLine(t(LATON_OSC, oscuro), Offset(c.x - r * 0.6f, c.y + r * 0.3f), Offset(c.x + r * 0.6f, c.y - r * 0.3f), r * 0.28f)
    }

    /** Una sombra suave debajo de algo: varias capas cada vez más transparentes. */
    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(e))
        }
    }

    /** La sombra de dentro de un hueco: más honda en los bordes. */
    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.6f), Color.Transparent), r.left, r.left + ancho * 0.7f), r.topLeft, Size(ancho * 0.7f, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.6f)), r.right - ancho * 0.7f, r.right), Offset(r.right - ancho * 0.7f, r.top), Size(ancho * 0.7f, r.height))
    }

    /** Un halo de luz (las lámparas encendidas). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Una estrella de [puntas] puntas; [hondo] es el radio de los entrantes, en fracción de [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int = 5, hondo: Float = 0.42f): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** La estrella roja esmaltada con su ribete de oro y sus caras: la mitad de cada punta, más clara. */
    private fun DrawScope.estrellaRoja(c: Offset, r: Float, oscuro: Boolean) {
        drawPath(estrella(c + Offset(r * 0.06f, r * 0.1f), r * 1.02f), Color.Black.copy(alpha = 0.3f))
        drawPath(estrella(c, r * 1.1f), t(ORO_OSC, oscuro))
        drawPath(estrella(c, r * 1.02f), t(ORO, oscuro))
        drawPath(estrella(c, r * 0.86f), t(ROJO, oscuro))
        for (k in 0 until 5) {
            val a = Math.toRadians(-90.0 + k * 72.0)
            val b = Math.toRadians(-90.0 + k * 72.0 + 36.0)
            val p = Path().apply {
                moveTo(c.x, c.y)
                lineTo(c.x + r * 0.86f * cos(a).toFloat(), c.y + r * 0.86f * sin(a).toFloat())
                lineTo(c.x + r * 0.86f * 0.42f * cos(b).toFloat(), c.y + r * 0.86f * 0.42f * sin(b).toFloat()); close()
            }
            drawPath(p, t(ROJO_LUZ, oscuro))
        }
    }

    /** Una espiga de trigo de oro que sale de [a] y llega a [b]: el tallo y los granos a los dos lados. */
    private fun DrawScope.espiga(a: Offset, b: Offset, g: Float, oscuro: Boolean) {
        drawLine(t(ORO_OSC, oscuro), a, b, g * 0.25f)
        val ang = Math.toDegrees(kotlin.math.atan2((b.y - a.y).toDouble(), (b.x - a.x).toDouble())).toFloat()
        for (k in 0 until 6) {
            val f = 0.3f + k * 0.12f
            val p = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
            listOf(-1f, 1f).forEach { s ->
                rotate(ang + s * 32f, p) { drawOval(t(if (s < 0) ORO else ORO_LUZ, oscuro), Offset(p.x, p.y - g * 0.32f), Size(g * 1.1f, g * 0.64f)) }
            }
        }
        rotate(ang, b) { drawOval(t(ORO, oscuro), Offset(b.x - g * 0.3f, b.y - g * 0.3f), Size(g * 1.1f, g * 0.6f)) }
    }

    /** La corona de espigas alrededor de [c], de radio [r], atada abajo con la cinta roja. */
    private fun DrawScope.coronaEspigas(c: Offset, r: Float, oscuro: Boolean) {
        listOf(-1f, 1f).forEach { s ->
            for (k in 0 until 4) {
                val a0 = Math.toRadians(90.0 + s * (25.0 + k * 34.0))
                val a1 = Math.toRadians(90.0 + s * (25.0 + k * 34.0 + 30.0))
                espiga(Offset(c.x + r * cos(a0).toFloat(), c.y + r * sin(a0).toFloat()), Offset(c.x + r * cos(a1).toFloat(), c.y + r * sin(a1).toFloat()), r * 0.16f, oscuro)
            }
        }
        val y = c.y + r * 0.95f
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply { moveTo(c.x, y); lineTo(c.x + s * r * 0.5f, y + r * 0.35f); lineTo(c.x + s * r * 0.36f, y + r * 0.42f); lineTo(c.x + s * r * 0.44f, y + r * 0.5f); lineTo(c.x + s * r * 0.15f, y + r * 0.2f); close() }, t(ROJO, oscuro))
        }
        drawRect(t(ROJO_LUZ, oscuro), Offset(c.x - r * 0.25f, y - r * 0.1f), Size(r * 0.5f, r * 0.2f))
    }

    /** La banderola roja colgada de su barra de latón, de [x0] a [x1] y de [y0] a [y1], con bandas, tres estrellas y fleco. */
    private fun DrawScope.banderola(x0: Float, x1: Float, y0: Float, y1: Float, oscuro: Boolean) {
        val u = size.minDimension / 100f
        drawLine(t(LATON_OSC, oscuro), Offset(x0 - 4 * u, y0 - 0.2f * u), Offset(x1 + 4 * u, y0 - 0.2f * u), 1f * u, StrokeCap.Round)
        drawLine(t(LATON_LUZ, oscuro), Offset(x0 - 4 * u, y0 - 0.5f * u), Offset(x1 + 4 * u, y0 - 0.5f * u), 0.35f * u)
        listOf(x0 - 4.5f * u, x1 + 4.5f * u).forEach { drawCircle(t(LATON, oscuro), 1.2f * u, Offset(it, y0 - 0.2f * u)) }
        val tela = Rect(x0, y0, x1, y1)
        sombra(tela, 2 * u, if (oscuro) 0.4f else 0.22f)
        drawRect(t(ROJO, oscuro), tela.topLeft, tela.size)
        // Los pliegues de la tela: bandas verticales un poco más oscuras o más claras.
        val n = 9
        for (k in 0 until n) if (k % 2 == 1) drawRect(t(ROJO_OSC, oscuro).copy(alpha = 0.35f), Offset(x0 + tela.width * k / n, y0), Size(tela.width / n, tela.height))
        drawRect(t(ROJO_LUZ, oscuro).copy(alpha = 0.4f), Offset(x0 + tela.width * 4 / n, y0), Size(tela.width / n, tela.height))
        listOf(y0 + tela.height * 0.13f, y1 - tela.height * 0.17f).forEach { y -> drawRect(t(ORO, oscuro), Offset(x0 + 2 * u, y), Size(tela.width - 4 * u, 0.45f * u)) }
        val cy = tela.center.y
        listOf(0.22f to 0.8f, 0.5f to 1f, 0.78f to 0.8f).forEach { (f, k) ->
            val c = Offset(x0 + tela.width * f, cy)
            drawPath(estrella(c + Offset(0.3f * u, 0.4f * u), tela.height * 0.28f * k), t(ROJO_OSC, oscuro))
            drawPath(estrella(c, tela.height * 0.28f * k), t(ORO, oscuro))
        }
        listOf(0.36f, 0.64f).forEach { f ->
            val x = x0 + tela.width * f
            for (j in -1..1) drawRect(t(ORO, oscuro), Offset(x + j * 1.3f * u - 0.35f * u, cy - tela.height * 0.17f), Size(0.7f * u, tela.height * 0.34f))
        }
        // El fleco de oro.
        var x = x0
        while (x < x1) { drawLine(t(if (((x - x0) / u).toInt() % 2 == 0) ORO else ORO_OSC, oscuro), Offset(x, y1), Offset(x, y1 + 1.6f * u), 0.35f * u); x += 0.7f * u }
        drawRect(t(ORO, oscuro), Offset(x0, y1 - 0.3f * u), Size(tela.width, 0.6f * u))
    }

    /** Una pilastra de mármol en [r]: el fuste acanalado con sus franjas de oro, el capitel escalonado y la basa de granito negro. */
    private fun DrawScope.pilastra(r: Rect, oscuro: Boolean, semilla: Int) {
        val w = r.width; val u = size.minDimension / 100f
        val cap = w * 0.7f; val bas = w * 0.8f
        val fuste = Rect(r.left + w * 0.1f, r.top + cap, r.right - w * 0.1f, r.bottom - bas)
        marmol(fuste, oscuro, semilla)
        val n = if (fuste.width > 10 * density) 3 else 2
        for (k in 1..n) {
            val x = fuste.left + fuste.width * k / (n + 1)
            drawLine(t(MARMOL_SOMBRA, oscuro), Offset(x, fuste.top + 1.5f * u), Offset(x, fuste.bottom - 1.5f * u), max(0.5f * density, fuste.width * 0.08f), StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.3f), Offset(x + fuste.width * 0.08f, fuste.top + 1.5f * u), Offset(x + fuste.width * 0.08f, fuste.bottom - 1.5f * u), max(0.4f * density, fuste.width * 0.03f))
        }
        listOf(0.2f, 0.8f).forEach { f -> drawRect(t(ORO, oscuro), Offset(fuste.left, fuste.top + fuste.height * f), Size(fuste.width, 0.6f * u)) }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(fuste.right - fuste.width * 0.22f, fuste.top), Size(fuste.width * 0.22f, fuste.height))
        // El capitel en tres escalones, con su filete de oro.
        for (k in 0 until 3) {
            val inset = w * (0.08f * (2 - k))
            val y = r.top + cap * k / 3f
            granito(Rect(r.left + inset, y, r.right - inset, y + cap / 3f), GRANITO_GRIS, oscuro, semilla + k)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + inset, y + cap / 3f - 0.4f * u), Size(w - 2 * inset, 0.4f * u))
        }
        drawRect(t(ORO, oscuro), Offset(fuste.left, fuste.top - 0.6f * u), Size(fuste.width, 0.6f * u))
        // La basa de granito negro, más ancha abajo.
        granito(Rect(r.left + w * 0.04f, fuste.bottom, r.right - w * 0.04f, fuste.bottom + bas * 0.4f), GRANITO_NEGRO, oscuro, semilla + 5)
        granito(Rect(r.left, fuste.bottom + bas * 0.4f, r.right, r.bottom), GRANITO_NEGRO, oscuro, semilla + 6)
        drawRect(t(ORO, oscuro), Offset(r.left + w * 0.04f, fuste.bottom), Size(w * 0.92f, 0.5f * u))
    }

    /** El parquet de roble en espiga entre [y0] y [y1]. */
    private fun DrawScope.parquet(y0: Float, y1: Float, oscuro: Boolean) {
        val w = size.width; val u = size.minDimension / 100f
        val h = y1 - y0
        drawRect(t(PARQUET, oscuro), Offset(0f, y0), Size(w, h))
        clipRect(0f, y0, w, y1) {
            val c = 6 * u
            var j = 0
            var x = 0f
            while (x < w) {
                val s = if (j % 2 == 0) 1f else -1f
                var y = y0 - c
                var k = 0
                while (y < y1) {
                    val p = Path().apply { moveTo(x, y); lineTo(x + c * 0.6f, y + s * c * 0.3f); lineTo(x + c * 0.6f, y + s * c * 0.3f + c * 0.35f); lineTo(x, y + c * 0.35f); close() }
                    drawPath(p, t(if ((k + j) % 2 == 0) PARQUET else mezcla(PARQUET, PARQUET_OSC, 0.35f), oscuro))
                    drawPath(p, t(PARQUET_OSC, oscuro).copy(alpha = 0.7f), style = Stroke(0.3f * u))
                    y += c * 0.35f; k++
                }
                x += c * 0.6f; j++
            }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), y0, y0 + h * 0.6f), Offset(0f, y0), Size(w, h * 0.6f))
    }

    /** Una escuadra de latón en la esquina [o] con sus dos tornillos; [sx] y [sy] dicen hacia dónde abre. */
    private fun DrawScope.escuadra(o: Offset, sx: Float, sy: Float, l: Float, g: Float, oscuro: Boolean) {
        val p = Path().apply {
            moveTo(o.x, o.y); lineTo(o.x + sx * l, o.y); lineTo(o.x + sx * l, o.y + sy * g); lineTo(o.x + sx * g, o.y + sy * g)
            lineTo(o.x + sx * g, o.y + sy * l); lineTo(o.x, o.y + sy * l); close()
        }
        drawPath(p, t(LATON, oscuro))
        drawPath(p, t(LATON_OSC, oscuro), style = Stroke(0.25f * density))
        tornillo(Offset(o.x + sx * l * 0.7f, o.y + sy * g / 2), g * 0.3f, oscuro)
        tornillo(Offset(o.x + sx * g / 2, o.y + sy * l * 0.7f), g * 0.3f, oscuro)
    }

    // ---------------------------------------------------------------- Social: la sala del club con el periódico mural

    /** El aplique de pared: el brazo de latón y el globo opalino (de noche, encendido); colgado de [p]. */
    private fun DrawScope.aplique(p: Offset, s: Float, oscuro: Boolean) {
        val g = Offset(p.x, p.y - s * 0.55f)
        if (oscuro) { halo(g, s * 3.2f, LUZ.copy(alpha = 0.28f)); halo(g, s * 1.3f, LUZ.copy(alpha = 0.45f)) }
        laton(Rect(p.x - s * 0.18f, p.y - s * 0.1f, p.x + s * 0.18f, p.y + s * 0.4f), oscuro, s * 0.05f)
        drawPath(Path().apply { moveTo(p.x, p.y + s * 0.1f); quadraticTo(p.x + s * 0.02f, p.y - s * 0.1f, p.x, g.y + s * 0.25f) }, t(LATON, oscuro), style = Stroke(s * 0.08f))
        laton(Rect(p.x - s * 0.18f, g.y + s * 0.22f, p.x + s * 0.18f, g.y + s * 0.32f), oscuro)
        drawCircle(if (oscuro) mezcla(CREMA, LUZ, 0.6f) else CREMA, s * 0.3f, g)
        drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.7f), Color.Transparent), g - Offset(s * 0.1f, s * 0.1f), s * 0.2f), s * 0.3f, g)
        if (!oscuro) drawCircle(Color.Black.copy(alpha = 0.12f), s * 0.3f, g, style = Stroke(s * 0.04f))
        laton(Rect(p.x - s * 0.06f, g.y - s * 0.38f, p.x + s * 0.06f, g.y - s * 0.28f), oscuro)
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.SOCIAL)
        val piso = h * 0.875f
        // La pared de estuco ocre, con su luz cenital.
        drawRect(pared(oscuro))
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.02f else 0.12f), Color.Black.copy(alpha = 0.1f)), 0f, piso), Offset.Zero, Size(w, piso))
        // Los apliques encendidos dan su halo a la pared (se dibujan antes que las pilastras).
        val pil = w * 0.06f
        val xs = listOf(w * 0.015f, w * 0.985f - pil)
        val ax = listOf((xs[0] + pil + n.left - 3 * u) / 2, (xs[1] + n.right + 3 * u) / 2)
        // El zócalo de nogal con sus tableros y su filete.
        val zoc = Rect(0f, h * 0.7f, w, piso)
        nogal(zoc, oscuro, e.semilla + 1)
        drawRect(t(NOGAL_LUZ, oscuro), zoc.topLeft, Size(w, 0.9f * u))
        drawRect(t(LATON, oscuro), Offset(0f, zoc.top + 0.9f * u), Size(w, 0.4f * u))
        val nt = max(4, (w / (24 * u)).roundToInt())
        for (k in 0 until nt) {
            val r = Rect(w * k / nt + 1.6f * u, zoc.top + 2.6f * u, w * (k + 1) / nt - 1.6f * u, zoc.bottom - 1.6f * u)
            drawRect(t(NOGAL_OSC, oscuro).copy(alpha = 0.5f), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = 0.1f), Offset(r.left, r.bottom - 0.4f * u), Size(r.width, 0.4f * u))
        }
        parquet(piso, h, oscuro)
        // La alfombra roja con su orla verde.
        val alf = Rect(w * 0.12f, h * 0.915f, w * 0.88f, h * 0.985f)
        drawRect(t(ALFOMBRA, oscuro), alf.topLeft, alf.size)
        drawRect(t(ALFOMBRA_ORLA, oscuro), alf.deflate(0.8f * u).topLeft, alf.deflate(0.8f * u).size, style = Stroke(1.2f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.6f), alf.deflate(2.2f * u).topLeft, alf.deflate(2.2f * u).size, style = Stroke(0.3f * u))
        var xr = alf.left; while (xr < alf.right) { drawLine(t(CREMA, oscuro).copy(alpha = 0.8f), Offset(xr, alf.bottom), Offset(xr, alf.bottom + 1.2f * u), 0.3f * u); xr += 1 * u }
        // La cornisa de granito gris con su filete de oro.
        val corn = Rect(0f, 0f, w, h * 0.06f)
        granito(corn, GRANITO_GRIS, oscuro, e.semilla + 2)
        drawRect(t(ORO, oscuro), Offset(0f, corn.bottom - 1.6f * u), Size(w, 0.5f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, corn.bottom - 0.6f * u), Size(w, 0.6f * u))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), corn.bottom, corn.bottom + 3 * u), Offset(0f, corn.bottom), Size(w, 3 * u))
        // Las pilastras de mármol y los apliques.
        xs.forEachIndexed { i, x -> pilastra(Rect(x, corn.bottom, x + pil, piso), oscuro, e.semilla + 10 + i) }
        ax.forEach { x -> aplique(Offset(x, h * 0.42f), 9 * u, oscuro) }
        // El periódico mural: la sombra, el marco de nogal con su filete de latón, las cuñas rojas y las escuadras.
        val g = 3.2f * u
        val marco = Rect(n.left - g, n.top - g, n.right + g, n.bottom + g)
        sombra(marco, 3 * u, if (oscuro) 0.5f else 0.28f)
        nogal(marco, oscuro, e.semilla + 3)
        drawRect(Color.White.copy(alpha = 0.15f), marco.topLeft, Size(marco.width, 0.7f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left, marco.bottom - 0.7f * u), Size(marco.width, 0.7f * u))
        drawRect(t(LATON, oscuro), n.inflate(0.8f * u).topLeft, n.inflate(0.8f * u).size, style = Stroke(0.4f * u))
        drawRect(carteNicho(oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            // El papel: las juntas de los pliegos pegados, muy suaves.
            listOf(1f / 3, 2f / 3).forEach { f -> drawLine(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.05f), Offset(n.left + n.width * f, n.top), Offset(n.left + n.width * f, n.bottom), 0.5f * u) }
        }
        sombraInterior(n, 3 * u, if (oscuro) 0.3f else 0.1f)
        // Las cuñas rojas de las esquinas (arriba a la izquierda y abajo a la derecha), como en un cartel.
        drawPath(Path().apply { moveTo(marco.left, marco.top); lineTo(marco.left + 14 * u, marco.top); lineTo(marco.left, marco.top + 9 * u); close() }, t(ROJO, oscuro))
        drawPath(Path().apply { moveTo(marco.right, marco.bottom); lineTo(marco.right - 14 * u, marco.bottom); lineTo(marco.right, marco.bottom - 9 * u); close() }, t(ROJO, oscuro))
        drawLine(t(NEGRO, oscuro), Offset(marco.left + 15.5f * u, marco.top + 0.4f * u), Offset(marco.left + 0.4f * u, marco.top + 10f * u), 0.9f * u)
        escuadra(Offset(marco.right, marco.top), -1f, 1f, 7 * u, 2.2f * u, oscuro)
        escuadra(Offset(marco.left, marco.bottom), 1f, -1f, 7 * u, 2.2f * u, oscuro)
        // La placa de latón al pie del marco.
        val pl = Rect(w / 2 - 9 * u, marco.bottom - 2.6f * u, w / 2 + 9 * u, marco.bottom + 1.2f * u)
        laton(pl, oscuro, 0.4f * u)
        listOf(pl.left + 1.4f * u, pl.right - 1.4f * u).forEach { tornillo(Offset(it, pl.center.y), 0.6f * u, oscuro) }
        drawLine(t(LATON_OSC, oscuro), Offset(pl.left + 3 * u, pl.center.y), Offset(pl.right - 3 * u, pl.center.y), 0.3f * u)
        // La banderola roja sobre el mural.
        banderola(w * 0.24f, w * 0.76f, h * 0.085f, h * 0.195f, oscuro)
    }

    // ---------------------------------------------------------------- Mapas: el escritorio del Gosplan

    /** La lámpara de pantalla verde, de frente, con la base en [p] (de noche, encendida). */
    private fun DrawScope.lampara(p: Offset, s: Float, oscuro: Boolean) {
        val pant = Rect(p.x - s * 0.5f, p.y - s * 0.95f, p.x + s * 0.5f, p.y - s * 0.62f)
        if (oscuro) {
            halo(Offset(p.x, pant.bottom + s * 0.3f), s * 1.8f, LUZ.copy(alpha = 0.3f))
            drawPath(Path().apply { moveTo(pant.left + s * 0.05f, pant.bottom); lineTo(pant.right - s * 0.05f, pant.bottom); lineTo(p.x + s * 0.9f, p.y + s * 0.1f); lineTo(p.x - s * 0.9f, p.y + s * 0.1f); close() },
                Brush.verticalGradient(listOf(LUZ.copy(alpha = 0.3f), Color.Transparent), pant.bottom, p.y + s * 0.1f))
        }
        sombra(Rect(p.x - s * 0.36f, p.y - s * 0.08f, p.x + s * 0.36f, p.y), s * 0.08f, 0.35f)
        laton(Rect(p.x - s * 0.36f, p.y - s * 0.1f, p.x + s * 0.36f, p.y), oscuro, s * 0.04f)
        drawRect(t(LATON_OSC, oscuro), Offset(p.x - s * 0.04f, pant.bottom), Size(s * 0.08f, p.y - s * 0.1f - pant.bottom))
        drawRect(t(LATON_LUZ, oscuro), Offset(p.x - s * 0.03f, pant.bottom), Size(s * 0.025f, p.y - s * 0.1f - pant.bottom))
        val sp = Path().apply { moveTo(pant.left, pant.bottom); quadraticTo(pant.left, pant.top, p.x, pant.top); quadraticTo(pant.right, pant.top, pant.right, pant.bottom); close() }
        drawPath(sp, t(PANTALLA, oscuro))
        drawPath(sp, Brush.horizontalGradient(listOf(Color.Transparent, t(PANTALLA_LUZ, oscuro).copy(alpha = 0.7f), Color.Transparent), pant.left, pant.right))
        drawRect(if (oscuro) LUZ else t(CREMA, oscuro), Offset(pant.left + s * 0.04f, pant.bottom - s * 0.025f), Size(pant.width - s * 0.08f, s * 0.05f))
        laton(Rect(p.x - s * 0.05f, pant.top - s * 0.06f, p.x + s * 0.05f, pant.top + s * 0.01f), oscuro)
        drawLine(t(LATON, oscuro), Offset(p.x + s * 0.2f, pant.bottom), Offset(p.x + s * 0.2f, pant.bottom + s * 0.14f), s * 0.02f)
        drawCircle(t(LATON, oscuro), s * 0.03f, Offset(p.x + s * 0.2f, pant.bottom + s * 0.16f))
    }

    /** La escribanía de mármol con dos tinteros de vidrio y tapas de latón y la pluma, de frente, con la base en [p]. */
    private fun DrawScope.escribania(p: Offset, s: Float, oscuro: Boolean, semilla: Int) {
        val base = Rect(p.x - s * 0.5f, p.y - s * 0.16f, p.x + s * 0.5f, p.y)
        sombra(base, s * 0.06f, 0.35f)
        drawRect(t(Color(0xFF3F6A58), oscuro), base.topLeft, base.size)
        clipRect(base.left, base.top, base.right, base.bottom) { vetas(base, Color.White.copy(alpha = 0.3f), semilla, densidad = 0.6f, grosor = 0.4f, ondulacion = 1.5f) }
        drawRect(Color.White.copy(alpha = 0.2f), base.topLeft, Size(base.width, s * 0.03f))
        listOf(-0.25f, 0.25f).forEach { f ->
            val cx = p.x + s * f
            val v = Rect(cx - s * 0.14f, base.top - s * 0.24f, cx + s * 0.14f, base.top)
            drawRect(t(Color(0xFF9FB6B4), oscuro).copy(alpha = 0.9f), v.topLeft, v.size)
            drawRect(t(Color(0xFF1E2A3A), oscuro), Offset(v.left + s * 0.03f, v.top + v.height * 0.45f), Size(v.width - s * 0.06f, v.height * 0.5f))
            drawRect(Color.White.copy(alpha = 0.4f), Offset(v.left + s * 0.03f, v.top), Size(s * 0.03f, v.height))
            laton(Rect(cx - s * 0.12f, v.top - s * 0.08f, cx + s * 0.12f, v.top), oscuro, s * 0.02f)
            drawCircle(t(LATON, oscuro), s * 0.035f, Offset(cx, v.top - s * 0.1f))
        }
        // La pluma apoyada.
        drawLine(t(NEGRO, oscuro), Offset(p.x - s * 0.05f, base.top - s * 0.02f), Offset(p.x + s * 0.18f, base.top - s * 0.62f), s * 0.05f, StrokeCap.Round)
        drawLine(t(LATON, oscuro), Offset(p.x - s * 0.05f, base.top - s * 0.02f), Offset(p.x - s * 0.01f, base.top - s * 0.13f), s * 0.05f)
    }

    /** El teléfono de baquelita negra con su disco y el auricular en la horquilla, de frente, con la base en [p]. */
    private fun DrawScope.telefono(p: Offset, s: Float, oscuro: Boolean) {
        val baq = t(BAQUELITA, oscuro)
        sombra(Rect(p.x - s * 0.45f, p.y - s * 0.06f, p.x + s * 0.45f, p.y), s * 0.06f, 0.35f)
        val cuerpo = Path().apply { moveTo(p.x - s * 0.46f, p.y); lineTo(p.x - s * 0.34f, p.y - s * 0.48f); lineTo(p.x + s * 0.34f, p.y - s * 0.48f); lineTo(p.x + s * 0.46f, p.y); close() }
        drawPath(cuerpo, baq)
        drawPath(Path().apply { moveTo(p.x - s * 0.4f, p.y - s * 0.05f); lineTo(p.x - s * 0.31f, p.y - s * 0.44f); lineTo(p.x - s * 0.22f, p.y - s * 0.44f); lineTo(p.x - s * 0.3f, p.y - s * 0.05f); close() }, Color.White.copy(alpha = 0.12f))
        // El disco con sus diez agujeros y el tope.
        val dc = Offset(p.x, p.y - s * 0.22f)
        drawCircle(t(Color(0xFFE8E0CC), oscuro), s * 0.19f, dc)
        drawCircle(baq, s * 0.17f, dc)
        for (k in 0 until 10) {
            val a = Math.toRadians(-60.0 - k * 27.0)
            drawCircle(t(Color(0xFFE8E0CC), oscuro), s * 0.03f, Offset(dc.x + s * 0.12f * cos(a).toFloat(), dc.y + s * 0.12f * sin(a).toFloat()))
        }
        drawCircle(t(Color(0xFFE8E0CC), oscuro), s * 0.06f, dc)
        drawCircle(t(ROJO, oscuro), s * 0.03f, dc)
        // La horquilla y el auricular.
        listOf(-1f, 1f).forEach { k -> drawRect(baq, Offset(p.x + k * s * 0.3f - s * 0.04f, p.y - s * 0.6f), Size(s * 0.08f, s * 0.14f)) }
        val aur = Path().apply {
            moveTo(p.x - s * 0.5f, p.y - s * 0.62f); quadraticTo(p.x - s * 0.52f, p.y - s * 0.78f, p.x - s * 0.36f, p.y - s * 0.78f)
            lineTo(p.x + s * 0.36f, p.y - s * 0.78f); quadraticTo(p.x + s * 0.52f, p.y - s * 0.78f, p.x + s * 0.5f, p.y - s * 0.62f)
            lineTo(p.x + s * 0.28f, p.y - s * 0.62f); lineTo(p.x + s * 0.24f, p.y - s * 0.7f); lineTo(p.x - s * 0.24f, p.y - s * 0.7f); lineTo(p.x - s * 0.28f, p.y - s * 0.62f); close()
        }
        drawPath(aur, baq)
        drawLine(Color.White.copy(alpha = 0.2f), Offset(p.x - s * 0.34f, p.y - s * 0.76f), Offset(p.x + s * 0.3f, p.y - s * 0.76f), s * 0.02f)
        // El cordón en espiral.
        drawPath(Path().apply { moveTo(p.x + s * 0.44f, p.y - s * 0.1f); cubicTo(p.x + s * 0.7f, p.y - s * 0.1f, p.x + s * 0.6f, p.y + s * 0.08f, p.x + s * 0.4f, p.y + s * 0.06f) }, baq,
            style = Stroke(s * 0.035f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(s * 0.03f, s * 0.015f))))
    }

    /** El vaso de té en su portavasos calado de alpaca, con la cucharilla; la base en [p]. */
    private fun DrawScope.portavasos(p: Offset, s: Float, oscuro: Boolean) {
        val alpaca = t(Color(0xFFB9BEC2), oscuro)
        val alpacaOsc = t(Color(0xFF7E8488), oscuro)
        sombra(Rect(p.x - s * 0.3f, p.y - s * 0.05f, p.x + s * 0.3f, p.y), s * 0.06f, 0.35f)
        val vaso = Rect(p.x - s * 0.24f, p.y - s * 0.9f, p.x + s * 0.24f, p.y - s * 0.1f)
        drawRect(t(Color(0xFFDDE6E6), oscuro).copy(alpha = 0.7f), vaso.topLeft, Size(vaso.width, vaso.height * 0.2f))
        drawRect(t(TE, oscuro), Offset(vaso.left, vaso.top + vaso.height * 0.18f), Size(vaso.width, vaso.height * 0.82f))
        for (k in 0 until 5) drawLine(Color.White.copy(alpha = 0.18f), Offset(vaso.left + vaso.width * (k + 0.5f) / 5, vaso.top), Offset(vaso.left + vaso.width * (k + 0.5f) / 5, vaso.bottom), s * 0.025f)
        // La cucharilla asomando.
        drawLine(alpaca, Offset(p.x + s * 0.06f, vaso.top + s * 0.3f), Offset(p.x + s * 0.18f, vaso.top - s * 0.22f), s * 0.04f, StrokeCap.Round)
        // El portavasos: la pared calada, el aro, la base y el asa.
        val pv = Rect(vaso.left - s * 0.04f, p.y - s * 0.62f, vaso.right + s * 0.04f, p.y)
        drawRect(alpaca, Offset(pv.left, pv.top), Size(pv.width, s * 0.08f))
        drawRect(alpaca, Offset(pv.left, pv.bottom - s * 0.14f), Size(pv.width, s * 0.14f))
        for (k in 0 until 4) {
            val x = pv.left + pv.width * (k + 0.5f) / 4
            drawPath(Path().apply { moveTo(x - pv.width * 0.1f, pv.top + s * 0.08f); quadraticTo(x, pv.center.y, x - pv.width * 0.1f, pv.bottom - s * 0.14f) }, alpaca, style = Stroke(s * 0.03f))
            drawPath(Path().apply { moveTo(x + pv.width * 0.1f, pv.top + s * 0.08f); quadraticTo(x, pv.center.y, x + pv.width * 0.1f, pv.bottom - s * 0.14f) }, alpaca, style = Stroke(s * 0.03f))
        }
        drawPath(estrella(Offset(p.x, pv.bottom - s * 0.07f), s * 0.05f), t(ROJO, oscuro))
        drawRect(Color.White.copy(alpha = 0.3f), Offset(pv.left, pv.top), Size(pv.width, s * 0.02f))
        drawRect(alpacaOsc, Offset(pv.left, pv.bottom - s * 0.02f), Size(pv.width, s * 0.02f))
        drawPath(Path().apply { moveTo(pv.right, pv.top + s * 0.06f); cubicTo(pv.right + s * 0.26f, pv.top + s * 0.06f, pv.right + s * 0.26f, pv.bottom - s * 0.16f, pv.right, pv.bottom - s * 0.16f) }, alpaca, style = Stroke(s * 0.05f))
    }

    /** La carpeta de cartón del expediente en [r], un poco girada, con su estrella, sus renglones y sus cintas. */
    private fun DrawScope.carpeta(r: Rect, oscuro: Boolean) {
        val u = size.minDimension / 100f
        rotate(-6f, r.center) {
            drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(0.6f * u, 0.9f * u), r.size)
            drawRect(t(CREMA, oscuro), Offset(r.left + 0.8f * u, r.top - 0.6f * u), r.size)
            drawRect(t(Color(0xFFC9B48A), oscuro), r.topLeft, r.size)
            drawRect(t(Color(0xFF9E8A62), oscuro), r.topLeft, r.size, style = Stroke(0.3f * u))
            drawPath(estrella(Offset(r.center.x, r.top + r.height * 0.24f), r.width * 0.13f), t(ROJO, oscuro))
            listOf(0.48f, 0.6f, 0.72f).forEach { f -> drawLine(t(TINTA, oscuro).copy(alpha = 0.5f), Offset(r.left + r.width * 0.18f, r.top + r.height * f), Offset(r.right - r.width * 0.18f, r.top + r.height * f), 0.3f * u) }
            drawRect(t(TINTA, oscuro).copy(alpha = 0.55f), Rect(r.left + r.width * 0.15f, r.top + r.height * 0.4f, r.right - r.width * 0.15f, r.top + r.height * 0.8f).topLeft,
                Size(r.width * 0.7f, r.height * 0.4f), style = Stroke(0.3f * u))
            listOf(r.top + r.height * 0.5f).forEach { y ->
                drawLine(t(CREMA, oscuro), Offset(r.right, y), Offset(r.right + r.width * 0.16f, y + r.height * 0.1f), 0.5f * u, StrokeCap.Round)
                drawLine(t(CREMA, oscuro), Offset(r.right, y), Offset(r.right + r.width * 0.1f, y + r.height * 0.2f), 0.5f * u, StrokeCap.Round)
            }
        }
    }

    /** Los ábacos de oficina (schoty) en [r]: el marco de madera, las varillas en horizontal y las diez cuentas de cada una. */
    private fun DrawScope.abaco(r: Rect, oscuro: Boolean, semilla: Int) {
        val u = size.minDimension / 100f
        sombra(r, 1.4f * u, 0.35f)
        nogal(r, oscuro, semilla, color = NOGAL_LUZ)
        val dentro = r.deflate(r.width * 0.09f)
        drawRect(t(CREMA_OSC, oscuro), dentro.topLeft, dentro.size)
        val filas = 7
        for (k in 0 until filas) {
            val y = dentro.top + dentro.height * (k + 0.5f) / filas
            drawLine(t(LATON_OSC, oscuro), Offset(dentro.left, y), Offset(dentro.right, y), 0.3f * u)
            val bw = dentro.width / 13f
            val corridas = (azar(semilla, k) * 6).toInt()
            for (j in 0 until 10) {
                val x = if (j < corridas) dentro.left + bw * (j + 0.5f) else dentro.right - bw * (10 - j - 0.5f)
                val oscura = j == 4 || j == 5
                drawOval(t(if (oscura) NEGRO else Color(0xFFE8DCC0), oscuro), Offset(x - bw * 0.48f, y - dentro.height / filas * 0.38f), Size(bw * 0.96f, dentro.height / filas * 0.76f))
            }
        }
        drawRect(Color.White.copy(alpha = 0.15f), r.topLeft, Size(r.width, 0.5f * u))
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.MAPAS)
        val canto = h * 0.78f
        // El parquet de fondo y la sombra del escritorio.
        parquet(0f, h, oscuro)
        sombra(Rect(w * 0.03f, h * 0.96f, w * 0.97f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.3f)
        // Las dos cajoneras, el hueco de las rodillas en sombra y el cajón central.
        val fal = Rect(w * 0.025f, h * 0.825f, w * 0.975f, h * 0.975f)
        drawRect(t(NOGAL_OSC, oscuro), fal.topLeft, fal.size)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.6f), Color.Black.copy(alpha = 0.35f)), fal.top, fal.bottom), fal.topLeft, fal.size)
        val ancho = w * 0.24f
        listOf(fal.left, fal.right - ancho).forEachIndexed { i, x ->
            val cj = Rect(x, fal.top, x + ancho, fal.bottom)
            nogal(cj, oscuro, e.semilla + 30 + i)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(if (i == 0) cj.right - 0.8f * u else cj.left, cj.top), Size(0.8f * u, cj.height))
            val caj = Rect(cj.left + 1.6f * u, cj.top + 1.6f * u, cj.right - 1.6f * u, cj.bottom - 2.6f * u)
            drawRect(t(NOGAL_LUZ, oscuro), caj.topLeft, caj.size)
            vetas(caj, t(NOGAL_VETA, oscuro).copy(alpha = 0.4f), e.semilla + 40 + i, grosor = 0.6f)
            drawRect(Color.Black.copy(alpha = 0.35f), caj.topLeft, caj.size, style = Stroke(0.4f * u))
            laton(Rect(caj.center.x - 4.5f * u, caj.center.y - 0.7f * u, caj.center.x + 4.5f * u, caj.center.y + 0.7f * u), oscuro, 0.5f * u)
            drawCircle(t(LATON_OSC, oscuro), 0.9f * u, Offset(caj.center.x, caj.top + 1.8f * u))
            drawCircle(t(NEGRO, oscuro), 0.4f * u, Offset(caj.center.x, caj.top + 1.8f * u))
            laton(Rect(cj.left + 0.6f * u, cj.bottom - 1.2f * u, cj.right - 0.6f * u, cj.bottom), oscuro)
        }
        val central = Rect(fal.left + ancho + 1 * u, fal.top, fal.right - ancho - 1 * u, fal.top + 6 * u)
        nogal(central, oscuro, e.semilla + 35, color = NOGAL_LUZ)
        drawRect(Color.Black.copy(alpha = 0.35f), central.topLeft, central.size, style = Stroke(0.4f * u))
        laton(Rect(central.center.x - 6 * u, central.center.y - 0.6f * u, central.center.x + 6 * u, central.center.y + 0.6f * u), oscuro, 0.5f * u)
        // El canto con su filete de latón.
        val ct = Rect(w * 0.01f, canto, w * 0.99f, h * 0.825f)
        nogal(ct, oscuro, e.semilla + 2, color = NOGAL_LUZ)
        drawRect(Color.White.copy(alpha = 0.18f), ct.topLeft, Size(ct.width, 0.6f * u))
        drawRect(t(LATON, oscuro), Offset(ct.left, ct.center.y - 0.3f * u), Size(ct.width, 0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(ct.left, ct.bottom - 0.6f * u), Size(ct.width, 0.6f * u))
        // El tablero de nogal visto desde arriba, con el paño verde y su filete de oro.
        val tab = Rect(w * 0.01f, h * 0.015f, w * 0.99f, canto)
        nogal(tab, oscuro, e.semilla + 4)
        drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.1f), Color.Transparent), Offset(tab.left, tab.bottom), Offset(tab.right, tab.top)), tab.topLeft, tab.size)
        val pano = tab.deflate(3.4f * u)
        drawRect(t(PANO, oscuro), pano.topLeft, pano.size)
        for (k in 0 until 8) {
            val c = Offset(pano.left + pano.width * azar(e.semilla, k), pano.top + pano.height * azar(e.semilla, k + 20))
            drawCircle(Brush.radialGradient(listOf(t(PANO_OSC, oscuro).copy(alpha = 0.5f), Color.Transparent), c, 12 * u), 12 * u, c)
        }
        drawRect(t(ORO, oscuro), pano.deflate(1.2f * u).topLeft, pano.deflate(1.2f * u).size, style = Stroke(0.35f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), pano.topLeft, pano.size, style = Stroke(0.5f * u))
        // El plano: el papel un poco más grande que el nicho, su recuadro de dibujo y una chincheta en cada esquina.
        val pg = n.inflate(2.6f * u)
        sombra(pg, 2.2f * u, if (oscuro) 0.5f else 0.3f)
        drawRect(mapaNicho(oscuro), pg.topLeft, pg.size)
        drawRect(t(TINTA, oscuro).copy(alpha = if (oscuro) 0.6f else 0.55f), n.inflate(1.1f * u).topLeft, n.inflate(1.1f * u).size, style = Stroke(0.35f * u))
        drawRect(mapaNicho(oscuro), n.topLeft, n.size)
        drawRect(t(ROJO, oscuro), Offset(pg.left, pg.top), Size(pg.width, 0.5f * u))
        listOf(Offset(pg.left + 1.3f * u, pg.top + 1.3f * u), Offset(pg.right - 1.3f * u, pg.top + 1.3f * u), Offset(pg.left + 1.3f * u, pg.bottom - 1.3f * u), Offset(pg.right - 1.3f * u, pg.bottom - 1.3f * u)).forEach { c ->
            drawCircle(Color.Black.copy(alpha = 0.3f), 1.3f * u, c + Offset(0.4f * u, 0.5f * u))
            drawCircle(t(LATON, oscuro), 1.2f * u, c)
            drawCircle(t(LATON_LUZ, oscuro), 0.45f * u, c - Offset(0.35f * u, 0.35f * u))
        }
        // A un lado, la lámpara verde y la escribanía; al otro, el teléfono y el vaso de té.
        val izq = (pano.left + pg.left) / 2
        val der = (pano.right + pg.right) / 2
        val s = ((pg.left - pano.left) * 0.85f).coerceAtMost(20 * u)
        carpeta(Rect(izq - s * 0.42f, pano.top + 2.4f * u, izq + s * 0.42f, h * 0.27f), oscuro)
        lampara(Offset(izq, h * 0.53f), s * 1.05f, oscuro)
        escribania(Offset(izq, pano.bottom - 3 * u), s * 0.85f, oscuro, e.semilla + 60)
        abaco(Rect(der - s * 0.45f, pano.top + 2.6f * u, der + s * 0.45f, h * 0.26f), oscuro, e.semilla + 70)
        telefono(Offset(der, h * 0.49f), s * 0.95f, oscuro)
        portavasos(Offset(der, pano.bottom - 3 * u), s * 0.62f, oscuro)
    }

    // ---------------------------------------------------------------- Otras vidas: la pizarra de honor

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        // La sombra en el suelo.
        sombra(Rect(w * 0.02f, h * 0.955f, w * 0.98f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        // El plinto de granito rojo con su escalón de granito negro y su placa de latón.
        val plinto = Rect(w * 0.015f, h * 0.81f, w * 0.985f, h * 0.935f)
        granito(plinto, GRANITO_ROJO, oscuro, e.semilla + 1)
        drawRect(t(ORO, oscuro), Offset(plinto.left, plinto.top), Size(plinto.width, 0.5f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(plinto.left, plinto.bottom - 0.8f * u), Size(plinto.width, 0.8f * u))
        val esc = Rect(w * 0.005f, h * 0.935f, w * 0.995f, h * 0.975f)
        granito(esc, GRANITO_NEGRO, oscuro, e.semilla + 2)
        val pl = Rect(w / 2 - 14 * u, plinto.top + 2.4f * u, w / 2 + 14 * u, plinto.bottom - 2.2f * u)
        laton(pl, oscuro, 0.4f * u)
        drawRect(t(LATON_OSC, oscuro), pl.deflate(0.8f * u).topLeft, pl.deflate(0.8f * u).size, style = Stroke(0.3f * u))
        listOf(pl.left + 1.8f * u, pl.right - 1.8f * u).forEach { tornillo(Offset(it, pl.center.y), 0.7f * u, oscuro) }
        listOf(0.18f, 0.32f, 0.68f, 0.82f).forEach { f ->
            val c = Offset(w * f, plinto.center.y)
            drawPath(estrella(c + Offset(0.3f * u, 0.4f * u), 2.2f * u), Color.Black.copy(alpha = 0.3f))
            drawPath(estrella(c, 2.2f * u), t(ORO, oscuro))
        }
        // El cuerpo y las pilastras de mármol a los lados.
        val cuerpo = Rect(w * 0.02f, h * 0.18f, w * 0.98f, plinto.top)
        nogal(cuerpo, oscuro, e.semilla + 3)
        val pw = w * 0.045f
        listOf(w * 0.018f, w * 0.982f - pw).forEachIndexed { i, x -> pilastra(Rect(x, h * 0.2f, x + pw, plinto.top), oscuro, e.semilla + 10 + i) }
        // El nicho: cinco recuadros con su marco de latón, separados por montantes de nogal.
        drawRect(t(NOGAL_OSC, oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(honorNicho(oscuro), n.topLeft, n.size)
        sombraInterior(n, 4 * u, if (oscuro) 0.35f else 0.12f)
        val paso = n.width / 5
        for (k in 0 until 5) {
            val c = Rect(n.left + paso * k, n.top, n.left + paso * (k + 1), n.bottom)
            val m = Rect(c.left + 2.8f * u, c.top + 2.2f * u, c.right - 2.8f * u, c.bottom - 2.2f * u)
            drawRect(t(LATON_OSC, oscuro).copy(alpha = 0.8f), m.topLeft, m.size, style = Stroke(0.7f * u))
            drawRect(t(LATON, oscuro).copy(alpha = if (oscuro) 0.5f else 0.8f), m.deflate(0.25f * u).topLeft, m.deflate(0.25f * u).size, style = Stroke(0.3f * u))
            // La cinta roja de cada recuadro, arriba.
            val cx = c.center.x
            drawPath(Path().apply { moveTo(cx - 2.4f * u, m.top - 0.4f * u); lineTo(cx + 2.4f * u, m.top - 0.4f * u); lineTo(cx + 2.4f * u, m.top + 3.6f * u); lineTo(cx, m.top + 2.4f * u); lineTo(cx - 2.4f * u, m.top + 3.6f * u); close() }, t(ROJO, oscuro))
            drawPath(estrella(Offset(cx, m.top + 1.2f * u), 1f * u), t(ORO, oscuro))
        }
        for (k in 1 until 5) {
            val x = n.left + paso * k
            val p = Rect(x - 1.5f * u, n.top, x + 1.5f * u, n.bottom)
            nogal(p, oscuro, e.semilla + 20 + k, vertical = true, color = NOGAL_LUZ)
            drawRect(Color.White.copy(alpha = 0.12f), p.topLeft, Size(p.width * 0.3f, p.height))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(p.right - p.width * 0.3f, p.top), Size(p.width * 0.3f, p.height))
            drawRect(t(LATON, oscuro), Offset(p.center.x - 0.25f * u, p.top), Size(0.5f * u, p.height))
        }
        // La faja roja con sus filetes de oro y una estrella sobre cada recuadro.
        val faja = Rect(w * 0.01f, h * 0.165f, w * 0.99f, h * 0.225f)
        drawRect(t(ROJO, oscuro), faja.topLeft, faja.size)
        drawRect(t(ROJO_OSC, oscuro), Offset(faja.left, faja.bottom - 0.8f * u), Size(faja.width, 0.8f * u))
        listOf(faja.top + 0.6f * u, faja.bottom - 1.6f * u).forEach { y -> drawRect(t(ORO, oscuro), Offset(faja.left, y), Size(faja.width, 0.4f * u)) }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), faja.bottom, faja.bottom + 2.5f * u), Offset(faja.left, faja.bottom), Size(faja.width, 2.5f * u))
        for (k in 0 until 5) if (k != 2) drawPath(estrella(Offset(n.left + paso * (k + 0.5f), faja.center.y - 0.2f * u), 1.6f * u), t(ORO, oscuro))
        // El frontón escalonado de granito gris con los rayos de oro, la estrella roja y su corona de espigas.
        val cx = w / 2
        listOf(Triple(0.47f, 0.105f, 0.165f), Triple(0.385f, 0.055f, 0.105f), Triple(0.3f, 0.0f, 0.055f)).forEachIndexed { i, (mitad, y0, y1) ->
            val q = Rect(cx - w * mitad, h * y0, cx + w * mitad, h * y1)
            granito(q, GRANITO_GRIS, oscuro, e.semilla + 40 + i)
            drawRect(t(ORO, oscuro), Offset(q.left, q.top), Size(q.width, 0.4f * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(q.left, q.bottom - 0.5f * u), Size(q.width, 0.5f * u))
        }
        val sc = Offset(cx, h * 0.1f)
        clipRect(cx - w * 0.3f, 0f, cx + w * 0.3f, faja.top) {
            for (k in 0 until 18) {
                val a = Math.toRadians(180.0 + 5.0 + k * 10.0)
                drawLine(t(ORO, oscuro).copy(alpha = if (k % 2 == 0) 0.75f else 0.4f), sc, Offset(sc.x + 40 * u * cos(a).toFloat(), sc.y + 40 * u * sin(a).toFloat()), 0.8f * u)
            }
        }
        drawCircle(t(ROJO_OSC, oscuro), 8.6f * u, sc)
        drawCircle(t(ORO, oscuro), 8.6f * u, sc, style = Stroke(0.5f * u))
        coronaEspigas(sc, 7.6f * u, oscuro)
        estrellaRoja(Offset(sc.x, sc.y - 0.4f * u), 5.4f * u, oscuro)
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f * 60f
        val c = Offset(w / 2, h / 2)
        val u = s / 60f
        val piedra = t(Color(0xFFE2D3B0), oscuro)
        val piedraOsc = t(Color(0xFFB4A27C), oscuro)
        when (id) {
            "unesco" -> {
                // El rascacielos de la universidad: las alas con sus torrecillas, el cuerpo escalonado y la aguja con la estrella.
                val base = c.y + s * 0.48f
                drawOval(Color.Black.copy(alpha = 0.22f), Offset(c.x - 30 * u, base - 1.6f * u), Size(60 * u, 3.2f * u))
                fun bloque(r: Rect) {
                    drawRect(piedra, r.topLeft, r.size)
                    drawRect(piedraOsc, Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
                    drawRect(Color.White.copy(alpha = 0.3f), r.topLeft, Size(r.width, 0.5f * u))
                    clipRect(r.left, r.top, r.right, r.bottom) {
                        var y = r.top + 1.6f * u
                        while (y < r.bottom - 1 * u) {
                            var x = r.left + 1.2f * u
                            while (x < r.right - 0.8f * u) { drawRect(t(Color(0xFF5A4E3C), oscuro).let { if (oscuro && azar(7, (x + y * 3).toInt()) > 0.6f) LUZ else it }, Offset(x, y), Size(0.7f * u, 1f * u)); x += 1.8f * u }
                            y += 2.2f * u
                        }
                    }
                }
                listOf(-1f, 1f).forEach { sg ->
                    val ala = Rect(if (sg < 0) c.x - 27 * u else c.x + 9 * u, base - 13 * u, if (sg < 0) c.x - 9 * u else c.x + 27 * u, base)
                    bloque(ala)
                    val tx = if (sg < 0) ala.left + 3 * u else ala.right - 3 * u
                    bloque(Rect(tx - 2.4f * u, ala.top - 6 * u, tx + 2.4f * u, ala.top))
                    drawPath(Path().apply { moveTo(tx - 2.4f * u, ala.top - 6 * u); lineTo(tx, ala.top - 11 * u); lineTo(tx + 2.4f * u, ala.top - 6 * u); close() }, piedraOsc)
                    drawLine(t(ORO, oscuro), Offset(tx, ala.top - 11 * u), Offset(tx, ala.top - 14 * u), 0.5f * u)
                    drawPath(estrella(Offset(tx, ala.top - 14.6f * u), 1.1f * u), t(ROJO, oscuro))
                }
                val cuerpo = Rect(c.x - 9 * u, base - 22 * u, c.x + 9 * u, base)
                bloque(cuerpo)
                drawRect(t(Color(0xFF5A4E3C), oscuro), Offset(c.x - 2.4f * u, base - 5 * u), Size(4.8f * u, 5 * u))
                val t2 = Rect(c.x - 6.4f * u, cuerpo.top - 7 * u, c.x + 6.4f * u, cuerpo.top); bloque(t2)
                val t3 = Rect(c.x - 4.2f * u, t2.top - 6 * u, c.x + 4.2f * u, t2.top); bloque(t3)
                listOf(t2, t3).forEach { r -> listOf(r.left, r.right - 1.2f * u).forEach { x -> drawRect(piedraOsc, Offset(x, r.top - 1.4f * u), Size(1.2f * u, 1.4f * u)) } }
                val agujaBase = t3.top
                drawPath(Path().apply { moveTo(c.x - 3 * u, agujaBase); lineTo(c.x, agujaBase - 9 * u); lineTo(c.x + 3 * u, agujaBase); close() }, piedraOsc)
                drawLine(t(ORO, oscuro), Offset(c.x, agujaBase - 8 * u), Offset(c.x, agujaBase - 14 * u), 0.8f * u)
                estrellaRoja(Offset(c.x, agujaBase - 15.6f * u), 2.4f * u, oscuro)
                if (oscuro) halo(Offset(c.x, agujaBase - 15.6f * u), 6 * u, ROJO_LUZ.copy(alpha = 0.35f))
            }
            "rascar" -> {
                // El mapa del atlas con su borde rojo y su cuadrícula, la capa por rascar y la moneda de latón que la raspa.
                val m = Rect(c.x - s * 0.62f, c.y - s * 0.4f, c.x + s * 0.5f, c.y + s * 0.4f)
                drawRect(Color.Black.copy(alpha = 0.25f), m.topLeft + Offset(1.2f * u, 1.6f * u), m.size)
                drawRect(t(CREMA, oscuro), m.topLeft, m.size)
                drawRect(t(ROJO, oscuro), m.deflate(1.2f * u).topLeft, m.deflate(1.2f * u).size, style = Stroke(1.2f * u))
                val capa = m.deflate(3.6f * u)
                drawRect(t(Color(0xFFB8B2A2), oscuro), capa.topLeft, capa.size)
                clipRect(capa.left, capa.top, capa.right, capa.bottom) {
                    var k = capa.left; while (k < capa.right) { drawLine(Color.White.copy(alpha = 0.3f), Offset(k, capa.top), Offset(k, capa.bottom), 0.4f * u); k += 3 * u }
                    var j = capa.top; while (j < capa.bottom) { drawLine(Color.White.copy(alpha = 0.3f), Offset(capa.left, j), Offset(capa.right, j), 0.4f * u); j += 3 * u }
                    val raspa = Path().apply { moveTo(capa.left, capa.top + capa.height * 0.35f); cubicTo(capa.left + capa.width * 0.3f, capa.top - capa.height * 0.1f, capa.left + capa.width * 0.5f, capa.bottom, capa.right, capa.top + capa.height * 0.55f) }
                    drawPath(raspa, e.fondo, style = Stroke(capa.height * 0.42f, cap = StrokeCap.Round))
                    drawPath(raspa, e.c1.copy(alpha = 0.85f), style = Stroke(capa.height * 0.24f, cap = StrokeCap.Round))
                }
                // La moneda: el canto, la estrella y las dos espigas.
                val mc = Offset(m.right + s * 0.02f, m.bottom - s * 0.12f)
                val mr = s * 0.24f
                drawCircle(Color.Black.copy(alpha = 0.3f), mr, mc + Offset(1.2f * u, 1.6f * u))
                drawCircle(t(LATON_OSC, oscuro), mr, mc)
                drawCircle(t(LATON, oscuro), mr * 0.88f, mc)
                drawCircle(t(LATON_OSC, oscuro), mr * 0.8f, mc, style = Stroke(0.3f * u))
                listOf(-1f, 1f).forEach { sg -> espiga(Offset(mc.x + sg * mr * 0.25f, mc.y + mr * 0.6f), Offset(mc.x + sg * mr * 0.62f, mc.y - mr * 0.3f), mr * 0.18f, oscuro) }
                drawPath(estrella(mc - Offset(0f, mr * 0.12f), mr * 0.36f), t(LATON_OSC, oscuro))
                drawPath(estrella(mc - Offset(mr * 0.03f, mr * 0.15f), mr * 0.32f), t(LATON_LUZ, oscuro))
                drawArc(t(LATON_LUZ, oscuro), 200f, 90f, false, Offset(mc.x - mr * 0.78f, mc.y - mr * 0.78f), Size(mr * 1.56f, mr * 1.56f), style = Stroke(mr * 0.08f))
            }
            else -> {
                // Tus lugares: tres chinchetas de estrella en el mapa, unidas por la vía del tren.
                val m = Rect(c.x - s * 0.95f, c.y + s * 0.08f, c.x + s * 0.95f, c.y + s * 0.46f)
                val mp = Path().apply { moveTo(m.left + 4 * u, m.top); lineTo(m.right, m.top + 1 * u); lineTo(m.right - 4 * u, m.bottom); lineTo(m.left, m.bottom - 1 * u); close() }
                drawPath(mp, Color.Black.copy(alpha = 0.22f), style = Stroke(2 * u))
                drawPath(mp, t(CREMA, oscuro))
                clipPath(mp) {
                    drawPath(Path().apply { moveTo(m.left, m.top + m.height * 0.3f); quadraticTo(m.center.x, m.top + m.height * 0.9f, m.right, m.top + m.height * 0.5f) }, t(Color(0xFF7FA6C0), oscuro).copy(alpha = 0.6f), style = Stroke(0.9f * u))
                }
                drawPath(mp, t(CREMA_OSC, oscuro), style = Stroke(0.6f * u))
                val pts = listOf(Offset(c.x - s * 0.62f, m.top + m.height * 0.6f), Offset(c.x, m.top + m.height * 0.35f), Offset(c.x + s * 0.62f, m.top + m.height * 0.62f))
                val via = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.3f, m.bottom, pts[1].x, pts[1].y); quadraticTo(c.x + s * 0.3f, m.top, pts[2].x, pts[2].y) }
                drawPath(via, t(NEGRO, oscuro), style = Stroke(1.6f * u))
                drawPath(via, t(CREMA, oscuro), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 2.2f * u))))
                pts.forEachIndexed { i, p ->
                    val alto = s * (if (i == 1) 0.5f else 0.42f)
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(p.x - 1.6f * u, p.y - 0.7f * u), Size(3.2f * u, 1.4f * u))
                    drawLine(t(Color(0xFF9AA0A4), oscuro), p, Offset(p.x, p.y - alto), 0.8f * u)
                    estrellaRoja(Offset(p.x, p.y - alto - 3 * u), if (i == 1) 6 * u else 5 * u, oscuro)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el tablón de avisos

    /** El altavoz de bocina sobre su ménsula, colgado de [p], abriéndose hacia la izquierda. */
    private fun DrawScope.altavoz(p: Offset, s: Float, oscuro: Boolean) {
        drawLine(t(NEGRO, oscuro), p, Offset(p.x, p.y + s * 0.3f), s * 0.06f)
        drawLine(t(NEGRO, oscuro), Offset(p.x - s * 0.1f, p.y), Offset(p.x + s * 0.3f, p.y), s * 0.08f)
        val c = Offset(p.x, p.y + s * 0.38f)
        val bocina = Path().apply { moveTo(c.x + s * 0.3f, c.y - s * 0.08f); lineTo(c.x - s * 0.55f, c.y - s * 0.32f); lineTo(c.x - s * 0.55f, c.y + s * 0.32f); lineTo(c.x + s * 0.3f, c.y + s * 0.08f); close() }
        drawPath(bocina, t(ACERO_GRIS, oscuro))
        drawPath(Path().apply { moveTo(c.x + s * 0.3f, c.y - s * 0.08f); lineTo(c.x - s * 0.55f, c.y - s * 0.32f); lineTo(c.x - s * 0.55f, c.y - s * 0.12f); lineTo(c.x + s * 0.3f, c.y - s * 0.02f); close() }, Color.White.copy(alpha = 0.2f))
        drawOval(t(NEGRO, oscuro), Offset(c.x - s * 0.64f, c.y - s * 0.33f), Size(s * 0.18f, s * 0.66f))
        drawOval(t(ACERO_GRIS, oscuro), Offset(c.x - s * 0.64f, c.y - s * 0.33f), Size(s * 0.18f, s * 0.66f), style = Stroke(s * 0.03f))
        drawRoundRect(t(NEGRO, oscuro), Offset(c.x + s * 0.26f, c.y - s * 0.14f), Size(s * 0.24f, s * 0.28f), CornerRadius(s * 0.06f))
        drawCircle(t(ROJO, oscuro), s * 0.04f, Offset(c.x + s * 0.38f, c.y))
    }

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.NOTAS)
        // La pared con su raya roja y el zócalo; y la bandera roja en su asta, detrás del copete.
        drawRect(pared(oscuro))
        drawRect(t(ROJO, oscuro).copy(alpha = 0.85f), Offset(0f, h * 0.955f), Size(w, h * 0.012f))
        drawRect(t(NOGAL_OSC, oscuro), Offset(0f, h * 0.967f), Size(w, h * 0.033f))
        val asta0 = Offset(w * 0.045f, h * 0.2f)
        val asta1 = Offset(w * 0.17f, h * 0.025f)
        drawLine(t(LATON_OSC, oscuro), asta0, asta1, 1f * u, StrokeCap.Round)
        val tela = Path().apply {
            moveTo(asta1.x - 0.4f * u, asta1.y + 0.6f * u); quadraticTo(asta1.x + 5 * u, asta1.y + 3 * u, asta1.x + 11 * u, asta1.y + 2 * u)
            lineTo(asta1.x + 9 * u, asta1.y + 11 * u); quadraticTo(asta1.x + 3 * u, asta1.y + 11.5f * u, asta1.x - 4f * u, asta1.y + 5.6f * u); close()
        }
        drawPath(tela, t(ROJO, oscuro))
        drawPath(Path().apply { moveTo(asta1.x + 4 * u, asta1.y + 3 * u); quadraticTo(asta1.x + 6 * u, asta1.y + 8 * u, asta1.x + 3 * u, asta1.y + 11 * u) }, t(ROJO_OSC, oscuro).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        drawPath(estrella(Offset(asta1.x + 1.6f * u, asta1.y + 4 * u), 1.4f * u), t(ORO, oscuro))
        // El altavoz de bocina, arriba a la derecha.
        altavoz(Offset(w * 0.93f, h * 0.005f), 14 * u, oscuro)
        // El marco de nogal con su moldura, su filete de latón y las escuadras.
        val g = 3.2f * u
        val marco = Rect(n.left - g, n.top - g, n.right + g, n.bottom + g)
        sombra(marco, 3 * u, if (oscuro) 0.5f else 0.25f)
        nogal(marco, oscuro, e.semilla)
        drawRect(Color.White.copy(alpha = 0.14f), marco.topLeft, Size(marco.width, 0.8f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.right - 0.8f * u, marco.top), Size(0.8f * u, marco.height))
        drawRect(t(LATON, oscuro), n.inflate(0.8f * u).topLeft, n.inflate(0.8f * u).size, style = Stroke(0.4f * u))
        // El contrachapado claro con su veta suave y sus juntas.
        drawRect(tablonNicho(oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            vetas(n, if (oscuro) Color.White.copy(alpha = 0.03f) else NOGAL_LUZ.copy(alpha = 0.12f), e.semilla + 2, densidad = 0.3f, grosor = 0.5f, ondulacion = 2f)
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.07f), Offset(n.center.x, n.top), Offset(n.center.x, n.bottom), 0.5f * u)
            listOf(n.left + 2 * u, n.center.x - 2 * u, n.center.x + 2 * u, n.right - 2 * u).forEach { x ->
                listOf(n.top + 2 * u, n.center.y, n.bottom - 2 * u).forEach { y -> drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), 0.4f * u, Offset(x, y)) }
            }
        }
        sombraInterior(n, 3 * u, if (oscuro) 0.3f else 0.1f)
        escuadra(Offset(marco.left, marco.top), 1f, 1f, 7 * u, 2.2f * u, oscuro)
        escuadra(Offset(marco.right, marco.top), -1f, 1f, 7 * u, 2.2f * u, oscuro)
        escuadra(Offset(marco.left, marco.bottom), 1f, -1f, 7 * u, 2.2f * u, oscuro)
        escuadra(Offset(marco.right, marco.bottom), -1f, -1f, 7 * u, 2.2f * u, oscuro)
        // El copete rojo: la cuña negra, la estrella en su círculo blanco y las tres estrellas de oro.
        val cop = Rect(w * 0.27f, h * 0.035f, w * 0.73f, marco.top + 0.6f * u)
        sombra(cop, 2 * u, if (oscuro) 0.4f else 0.2f)
        drawRect(t(ROJO, oscuro), cop.topLeft, cop.size)
        drawRect(t(ORO, oscuro), cop.deflate(1f * u).topLeft, cop.deflate(1f * u).size, style = Stroke(0.4f * u))
        clipRect(cop.left, cop.top, cop.right, cop.bottom) {
            drawPath(Path().apply { moveTo(cop.left, cop.bottom); lineTo(cop.left + cop.width * 0.3f, cop.top); lineTo(cop.left + cop.width * 0.38f, cop.top); lineTo(cop.left + cop.width * 0.08f, cop.bottom); close() }, t(NEGRO, oscuro))
        }
        val ec = Offset(cop.left + cop.width * 0.5f, cop.center.y)
        drawCircle(t(CREMA, oscuro), cop.height * 0.36f, ec)
        drawPath(estrella(ec, cop.height * 0.3f), t(ROJO, oscuro))
        listOf(0.68f, 0.78f, 0.88f).forEach { f -> drawPath(estrella(Offset(cop.left + cop.width * f, cop.center.y), cop.height * 0.2f), t(ORO, oscuro)) }
        listOf(cop.left + 1.8f * u, cop.right - 1.8f * u).forEach { tornillo(Offset(it, cop.center.y), 0.7f * u, oscuro) }
        // La repisa de nogal con el lápiz rojo y el sello de goma.
        val rep = Rect(w * 0.005f, marco.bottom, w * 0.995f, marco.bottom + h * 0.035f)
        nogal(rep, oscuro, e.semilla + 4, color = NOGAL_LUZ)
        drawRect(Color.White.copy(alpha = 0.2f), rep.topLeft, Size(rep.width, 0.6f * u))
        drawRect(t(LATON, oscuro), Offset(rep.left, rep.bottom - 0.9f * u), Size(rep.width, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 0.6f * u))
        val py = rep.top - 0.1f * u
        drawRect(t(ROJO, oscuro), Offset(w * 0.6f, py - 1.4f * u), Size(w * 0.16f, 1.4f * u))
        drawPath(Path().apply { moveTo(w * 0.6f, py - 1.4f * u); lineTo(w * 0.6f - 3 * u, py - 0.7f * u); lineTo(w * 0.6f, py); close() }, t(Color(0xFFE6CFA4), oscuro))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(w * 0.6f, py - 1.4f * u), Size(w * 0.16f, 0.4f * u))
        drawRect(t(VIOLETA, oscuro), Offset(w * 0.22f, py - 1.2f * u), Size(6 * u, 1.2f * u))
        drawRect(t(NOGAL_OSC, oscuro), Offset(w * 0.22f + 2 * u, py - 4.2f * u), Size(2 * u, 3 * u))
        drawCircle(t(NOGAL, oscuro), 1.6f * u, Offset(w * 0.22f + 3 * u, py - 5 * u))
    }

    /** El papel del impreso: claro de día; de noche, un poco más pardo (la tinta se lee en los dos). */
    private fun papel(oscuro: Boolean) = if (oscuro) Color(0xFFD9CDB0) else Color(0xFFF4EBD5)

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val u = r.width / 100f; val v = r.height / 100f
        val p = Rect(r.left + 3 * u, r.top + 4 * v, r.right - 3 * u, r.bottom - 2 * v)
        // La sombra, el papel con su raya roja, su casilla y su pauta.
        sombra(p, 3 * u, if (oscuro) 0.5f else 0.28f)
        drawRect(papel(oscuro), p.topLeft, p.size)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, CREMA_OSC.copy(alpha = 0.5f)), p.top + p.height * 0.6f, p.bottom), Offset(p.left, p.top + p.height * 0.6f), Size(p.width, p.height * 0.4f))
        drawRect(ROJO, Offset(p.left + 5 * u, r.top + 11.5f * v), Size(p.width - 10 * u, 0.9f * v))
        drawRect(TINTA.copy(alpha = 0.6f), Offset(p.right - 13 * u, r.top + 6.5f * v), Size(7 * u, 3.6f * v), style = Stroke(0.5f * u))
        var y = r.top + 29 * v
        while (y < p.bottom - 6 * v) { drawLine(Color(0xFF6A7A9A).copy(alpha = 0.18f), Offset(p.left + 5 * u, y), Offset(p.right - 5 * u, y), 0.5f * u); y += 14.6f * v }
        // Si está fijado, la franja roja del margen.
        if (e.fijada) drawRect(ROJO, Offset(p.left + 1.6f * u, p.top + 16 * v), Size(2.6f * u, p.height - 26 * v))
        // El detalle del tipo, abajo a la derecha.
        val d = Offset(p.right - 10 * u, p.bottom - 10 * v)
        when (e.tipo) {
            "idea" -> rotate(-12f, d) {
                // El sello de la estrella, en rojo.
                drawCircle(ROJO.copy(alpha = 0.85f), 6.2f * u, d, style = Stroke(0.8f * u))
                drawCircle(ROJO.copy(alpha = 0.85f), 4.9f * u, d, style = Stroke(0.4f * u))
                drawPath(estrella(d, 3.6f * u), ROJO.copy(alpha = 0.85f))
            }
            "pensamiento" -> rotate(8f, d) {
                // El sello violeta de la biblioteca.
                drawCircle(VIOLETA.copy(alpha = 0.75f), 6.2f * u, d, style = Stroke(0.7f * u))
                drawCircle(VIOLETA.copy(alpha = 0.75f), 4.2f * u, d, style = Stroke(0.4f * u))
                for (k in 0 until 16) { val a = k * Math.PI / 8; drawCircle(VIOLETA.copy(alpha = 0.6f), 0.3f * u, Offset(d.x + 5.2f * u * cos(a).toFloat(), d.y + 5.2f * u * sin(a).toFloat())) }
                drawPath(estrella(d, 2f * u), VIOLETA.copy(alpha = 0.75f))
            }
            "proposito" -> {
                // Las barras del plan, cada una más alta, con su flecha.
                drawLine(TINTA.copy(alpha = 0.7f), Offset(d.x - 6 * u, d.y + 5 * u), Offset(d.x + 6 * u, d.y + 5 * u), 0.5f * u)
                listOf(3f, 5.5f, 8.5f).forEachIndexed { i, a -> drawRect(ROJO, Offset(d.x - 5 * u + i * 3.8f * u, d.y + 5 * u - a * u), Size(2.6f * u, a * u)) }
                drawLine(TINTA.copy(alpha = 0.8f), Offset(d.x - 5.5f * u, d.y + 0.5f * u), Offset(d.x + 4.5f * u, d.y - 6 * u), 0.6f * u)
                drawPath(Path().apply { moveTo(d.x + 5.6f * u, d.y - 6.8f * u); lineTo(d.x + 2.4f * u, d.y - 6.4f * u); lineTo(d.x + 4.4f * u, d.y - 4f * u); close() }, TINTA.copy(alpha = 0.8f))
            }
            else -> {
                // El recordatorio: la esquina doblada.
                val k = 10 * u
                drawPath(Path().apply { moveTo(p.right - k, p.bottom); lineTo(p.right, p.bottom - k); lineTo(p.right, p.bottom); close() }, tablonNicho(oscuro))
                drawPath(Path().apply { moveTo(p.right - k, p.bottom); lineTo(p.right, p.bottom - k); lineTo(p.right - k, p.bottom - k); close() }, CREMA_OSC)
                drawLine(Color.Black.copy(alpha = 0.2f), Offset(p.right - k, p.bottom), Offset(p.right, p.bottom - k), 0.5f * u)
            }
        }
        // Arriba al centro: la insignia de la estrella si está fijado; si no, la chincheta de latón.
        val s = Offset(p.center.x, p.top + 2.2f * v)
        if (e.fijada) {
            drawPath(estrella(s + Offset(0.6f * u, 1f * u), 6.4f * u), Color.Black.copy(alpha = 0.3f))
            drawPath(estrella(s, 6.8f * u), ORO_OSC)
            drawPath(estrella(s, 6.2f * u), ORO)
            drawPath(estrella(s, 5.2f * u), ROJO)
            for (k in 0 until 5) {
                val a = Math.toRadians(-90.0 + k * 72.0); val b = Math.toRadians(-90.0 + k * 72.0 + 36.0)
                drawPath(Path().apply { moveTo(s.x, s.y); lineTo(s.x + 5.2f * u * cos(a).toFloat(), s.y + 5.2f * u * sin(a).toFloat()); lineTo(s.x + 2.2f * u * cos(b).toFloat(), s.y + 2.2f * u * sin(b).toFloat()); close() }, ROJO_LUZ)
            }
        } else {
            drawLine(Color.Black.copy(alpha = 0.25f), s + Offset(0.5f * u, 0.5f * u), s + Offset(3f * u, 4.5f * u), 0.8f * u)
            drawCircle(Color.Black.copy(alpha = 0.3f), 3 * u, s + Offset(0.8f * u, 1.1f * u))
            drawCircle(LATON_OSC, 3 * u, s)
            drawCircle(LATON, 2.4f * u, s)
            drawCircle(LATON_LUZ, 0.9f * u, s - Offset(0.8f * u, 0.8f * u))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
