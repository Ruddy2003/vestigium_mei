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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Al-Ándalus: la casa nazarí, con los materiales de su biblioteca, su Ideario y su sala de logros: yesería con la red de
 * rombos (sebka), azulejo de lazo (alicatado verde, azul, negro y melado con sus estrellas blancas), cedro tallado con su
 * alero y sus canecillos, mocárabes, mármol de Macael, dovelas rojas y blancas, taracea de marfil y latón con su brillo.
 * - Social: el salón del palacio. Arriba el alero de cedro con sus canecillos y una fila de mocárabes; la banda de la
 *   inscripción cúfica; dos columnas nazaríes de mármol a los lados y, al centro, el paño liso enmarcado por una cenefa de
 *   estrellas, donde va tu gente. Abajo, sobre el zócalo de alicatado y el piso de mármol, dos almadrabas con colchón y
 *   cojines de borlas, la fuente de taza al medio, un cuenco de granadas, un laúd y dos candiles encendidos.
 * - Mapas: la mesa del geógrafo de Córdoba: tablero de cedro con la orla de taracea, la vitela sujeta con cuatro pesas de
 *   latón, el astrolabio con su red, el tintero octogonal con sus cálamos, el candil encendido, la regla y el compás;
 *   al frente, el faldón taraceado y las patas torneadas, sobre el piso de olambrillas.
 * - Otras vidas: la arquería de cinco arcos de herradura con sus dovelas, sobre columnillas nazaríes, bajo el alero, los
 *   mocárabes y la inscripción, con el alicatado y el antepecho de mármol abajo.
 * - Íconos: la Alhambra sobre su colina con Sierra Nevada detrás; una vitela en su marco con la capa de pan de oro rascada
 *   por un dinar; una vitela con tres alfileres de azulejo de ocho puntas unidos por el camino.
 * - Notas: el tablero de yeso liso en su marco de cedro taraceado, con cantoneras de latón, bajo un alero con mocárabes,
 *   entre dos columnillas con candiles en ménsula; cada nota es un pliego de papel de Xàtiva con su cabecera iluminada.
 */
internal object VidaAndalus : DisenoVida {
    private val YESO = Color(0xFFEFE5D0)
    private val YESO_LUZ = Color(0xFFFAF4E6)
    private val YESO_OSC = Color(0xFFD6C6A4)
    private val YESO_SOMBRA = Color(0xFFAE9C7A)
    private val CEDRO = Color(0xFF7A4A2A)
    private val CEDRO_LUZ = Color(0xFF9E6A40)
    private val CEDRO_OSC = Color(0xFF4A2B16)
    private val VETA = Color(0xFF2E1A0C)
    private val VERDE = Color(0xFF1E6B5C)
    private val AZUL = Color(0xFF2A4F9E)
    private val ALMAGRA = Color(0xFFA83B26)
    private val MELADO = Color(0xFFC8963A)
    private val NEGRO = Color(0xFF221C18)
    private val MARFIL = Color(0xFFF1E8D2)
    private val MARMOL = Color(0xFFE9E4D8)
    private val MARMOL_LUZ = Color(0xFFF8F5EE)
    private val MARMOL_OSC = Color(0xFFBDB4A2)
    private val LATON = Color(0xFFC9A04A)
    private val LATON_LUZ = Color(0xFFF0D58A)
    private val LATON_OSC = Color(0xFF6E5020)
    private val AGUA = Color(0xFF5FA8BE)
    private val GRANADA = Color(0xFFB0302A)
    private val BARRO = Color(0xFFB2603C)
    private val LLAMA = Color(0xFFFFC870)
    private val NOCHE = Color(0xFF08110F)

    /** El papel de Xàtiva: crema de día; de noche, papel teñido oscuro (con la tinta clara de [tintaNota]). */
    private val PAPEL_CLARO = Color(0xFFF4EAD3)
    private val PAPEL_OSCURO = Color(0xFF2F2A21)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.135f, 0.205f, 0.865f, 0.715f)
        MuebleVida.MAPAS -> Rect(0.175f, 0.095f, 0.825f, 0.70f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.045f, 0.215f, 0.955f, 0.83f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.135f, 0.925f, 0.855f)
    }

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material de noche: el mismo, en penumbra ([f] cuánto se hunde). */
    private fun n(c: Color, oscuro: Boolean, f: Float = 0.58f): Color = if (oscuro) mez(c, NOCHE, f) else c

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mez(e.fondo, mez(material, NOCHE, 0.82f), 0.4f) else mez(e.fondo, material, 0.3f)

    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)
    private fun DrawScope.rect(r: Rect, b: Brush) = drawRect(b, r.topLeft, r.size)
    private fun vert(r: Rect, c: List<Color>) = Brush.verticalGradient(c, r.top, r.bottom)
    private fun vert(r: Rect, a: Color, b: Color) = vert(r, listOf(a, b))
    private fun vert(r: Rect, a: Color, b: Color, c: Color) = vert(r, listOf(a, b, c))
    private fun horiz(r: Rect, c: List<Color>) = Brush.horizontalGradient(c, r.left, r.right)
    private fun horiz(r: Rect, a: Color, b: Color, c: Color) = horiz(r, listOf(a, b, c))
    private fun horiz(r: Rect, a: Color, b: Color, c: Color, d: Color) = horiz(r, listOf(a, b, c, d))

    /** Un número estable de 0 a 1 (la misma irregularidad cada vez). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Una mancha radial suave (luz de candil, sombra). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Una estrella de [puntas] puntas; [hondo] es el radio de los entrantes, en fracción de [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int = 8, hondo: Float = 0.62f, giro: Float = -90f): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(giro + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** El arco de herradura de centro ([cx], [cy]) y radio [rr]: la herradura (de 155° a 25° por arriba) y las jambas hasta [bajo]. */
    private fun arco(cx: Float, cy: Float, rr: Float, bajo: Float): Path = Path().apply {
        moveTo(cx - rr * 0.906f, bajo); lineTo(cx - rr * 0.906f, cy + rr * 0.423f)
        arcTo(Rect(cx - rr, cy - rr, cx + rr, cy + rr), 155f, 230f, false)
        lineTo(cx + rr * 0.906f, bajo); close()
    }

    /** Un sector de anillo (una dovela o el anillo entero), entre los radios [r1] < [r2]. */
    private fun sector(c: Offset, r1: Float, r2: Float, a0: Float, barrido: Float): Path = Path().apply {
        arcTo(Rect(c.x - r2, c.y - r2, c.x + r2, c.y + r2), a0, barrido, true)
        val a1 = Math.toRadians((a0 + barrido).toDouble())
        lineTo(c.x + r1 * cos(a1).toFloat(), c.y + r1 * sin(a1).toFloat())
        arcTo(Rect(c.x - r1, c.y - r1, c.x + r1, c.y + r1), a0 + barrido, -barrido, false)
        close()
    }

    /** Las dovelas de un arco de herradura: rojas de almagra y blancas, alternas, con sus juntas y una moldura fina. */
    private fun DrawScope.dovelas(c: Offset, rInt: Float, rExt: Float, cuantas: Int, o: Boolean, p: Float) {
        val paso = 230f / cuantas
        for (j in 0 until cuantas) {
            val s = sector(c, rInt, rExt, 155f + j * paso, paso)
            val col = if (j % 2 == 0) n(ALMAGRA, o) else n(MARFIL, o)
            drawPath(s, Brush.radialGradient(listOf(mez(col, Color.White, 0.12f), col, mez(col, Color.Black, 0.12f)), c, rExt))
        }
        val anillo = sector(c, rInt, rExt, 155f, 230f)
        drawPath(anillo, n(YESO_SOMBRA, o), style = Stroke(p * 0.6f))
        drawArc(n(YESO_LUZ, o), 155f, 230f, false, Offset(c.x - rExt - p, c.y - rExt - p), Size((rExt + p) * 2, (rExt + p) * 2), style = Stroke(p * 0.8f))
    }

    /** Vetas de madera en [r]: líneas onduladas a lo largo, de grosor [p] (≈ 1 dp). */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, p: Float, densidad: Float = 1f, vertical: Boolean = false) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        val cuantas = (ancho / (3.2f * p) * densidad).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until cuantas) {
                val base = ancho * (k + azar(semilla, k)) / cuantas
                val amp = (0.6f + 1.6f * azar(semilla, k + 100)) * p
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.5f + 2.5f * azar(semilla, k + 300)
                val path = Path()
                for (s in 0..16) {
                    val t = largo * s / 16
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16)
                    val x = if (vertical) r.left + v else r.left + t
                    val y = if (vertical) r.top + t else r.top + v
                    if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(p * (0.5f + 0.6f * azar(semilla, k + 500))))
            }
        }
    }

    /** El paño de yesería: el yeso con la red de rombos (sebka) en relieve y una florecilla en cada rombo. */
    private fun DrawScope.yeseria(r: Rect, o: Boolean, p: Float, paso: Float) {
        rect(r, vert(r, n(YESO_LUZ, o, 0.68f), n(YESO, o, 0.68f), n(mez(YESO, YESO_OSC, 0.6f), o, 0.68f)))
        clipRect(r.left, r.top, r.right, r.bottom) {
            val alto = r.height
            val ini = -((alto / paso).toInt() + 2)
            val fin = (r.width / paso).toInt() + 2
            val sombra = n(YESO_SOMBRA, o, 0.68f).copy(alpha = 0.6f)
            val luz = n(YESO_LUZ, o, 0.6f).copy(alpha = if (o) 0.35f else 0.9f)
            for (k in ini..fin + (alto / paso).toInt() + 2) {
                val x = r.left + k * paso
                drawLine(sombra, Offset(x + p * 0.7f, r.top), Offset(x + alto + p * 0.7f, r.bottom), p * 0.9f)
                drawLine(luz, Offset(x - p * 0.3f, r.top), Offset(x + alto - p * 0.3f, r.bottom), p * 0.5f)
                drawLine(sombra, Offset(x + p * 0.7f, r.top), Offset(x - alto + p * 0.7f, r.bottom), p * 0.9f)
                drawLine(luz, Offset(x - p * 0.3f, r.top), Offset(x - alto - p * 0.3f, r.bottom), p * 0.5f)
            }
            // Una florecilla de cuatro hojas en el centro de cada rombo.
            val flor = n(YESO_OSC, o, 0.66f)
            var j = 1
            while (r.top + j * paso / 2 < r.bottom + paso) {
                var i = if (j % 2 == 0) 1 else 0
                while (r.left + i * paso / 2 < r.right + paso) {
                    val c = Offset(r.left + i * paso / 2, r.top + j * paso / 2)
                    val s = paso * 0.1f
                    drawCircle(flor, s, Offset(c.x - s, c.y)); drawCircle(flor, s, Offset(c.x + s, c.y))
                    drawCircle(flor, s, Offset(c.x, c.y - s)); drawCircle(flor, s, Offset(c.x, c.y + s))
                    i += 2
                }
                j++
            }
        }
    }

    /** El alicatado: tres filas de piezas verdes, azules, negras y meladas con estrellas blancas de ocho puntas en los cruces. */
    private fun DrawScope.alicatado(r: Rect, o: Boolean, p: Float, filas: Int = 3) {
        if (r.height <= 0f) return
        val c = r.height / filas
        rect(r, n(MARFIL, o, 0.5f))
        val colores = listOf(VERDE, AZUL, NEGRO, MELADO)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val cols = (r.width / c).toInt() + 2
            for (j in 0 until filas) for (i in 0..cols) {
                val col = n(colores[(i + 2 * j) % 4], o, 0.45f)
                val q = Rect(r.left + i * c + c * 0.06f, r.top + j * c + c * 0.06f, r.left + (i + 1) * c - c * 0.06f, r.top + (j + 1) * c - c * 0.06f)
                rect(q, vert(q, mez(col, Color.White, 0.18f), col, mez(col, Color.Black, 0.15f)))
                drawLine(Color.White.copy(alpha = if (o) 0.12f else 0.3f), Offset(q.left + c * 0.1f, q.top + c * 0.12f), Offset(q.left + c * 0.4f, q.top + c * 0.12f), p * 0.5f)
            }
            for (j in 0..filas) for (i in 0..cols) {
                if ((i + j) % 2 != 0) continue
                val s = Offset(r.left + i * c, r.top + j * c)
                drawPath(estrella(s, c * 0.36f, 8, 0.55f), n(MARFIL, o, 0.45f))
                drawPath(estrella(s, c * 0.36f, 8, 0.55f), n(NEGRO, o).copy(alpha = 0.45f), style = Stroke(p * 0.4f))
            }
        }
        drawLine(n(NEGRO, o), Offset(r.left, r.top + p * 0.5f), Offset(r.right, r.top + p * 0.5f), p)
        drawLine(n(NEGRO, o), Offset(r.left, r.bottom - p * 0.5f), Offset(r.right, r.bottom - p * 0.5f), p)
    }

    /** La banda de la inscripción cúfica: astas verticales de alturas distintas, algún lazo y la línea de base, entre dos filetes. */
    private fun DrawScope.inscripcion(r: Rect, o: Boolean, p: Float, semilla: Int) {
        rect(r, vert(r, n(mez(YESO, YESO_OSC, 0.45f), o), n(mez(YESO, YESO_OSC, 0.7f), o)))
        val tinta = n(VERDE, o, 0.4f)
        val base = r.bottom - r.height * 0.22f
        val paso = maxOf(r.height * 0.34f, p * 2.6f)
        var x = r.left + paso
        var k = 0
        while (x < r.right - paso) {
            val alto = r.height * (0.22f + 0.48f * azar(semilla, k))
            drawLine(tinta, Offset(x, base), Offset(x, base - alto), p * 1.1f, StrokeCap.Round)
            if (k % 4 == 2) drawArc(tinta, 180f, 180f, false, Offset(x, base - r.height * 0.2f), Size(paso * 1.4f, r.height * 0.3f), style = Stroke(p * 0.9f))
            if (k % 7 == 5) drawCircle(tinta, p * 0.8f, Offset(x + paso * 0.5f, base - r.height * 0.55f))
            x += paso; k++
        }
        drawLine(tinta, Offset(r.left + paso, base), Offset(r.right - paso, base), p * 0.9f)
        drawLine(n(MELADO, o), Offset(r.left, r.top + p * 0.4f), Offset(r.right, r.top + p * 0.4f), p * 0.8f)
        drawLine(n(YESO_SOMBRA, o), Offset(r.left, r.bottom - p * 0.4f), Offset(r.right, r.bottom - p * 0.4f), p * 0.8f)
    }

    /** El alero de cedro en [tabla] con su veta, y debajo, en [can], la fila de canecillos de lóbulos con su sombra en el muro. */
    private fun DrawScope.alero(tabla: Rect, can: Rect, o: Boolean, p: Float, u: Float, semilla: Int) {
        rect(Rect(can.left, can.top, can.right, can.bottom + 2.4f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), can.top, can.bottom + 2.4f * u))
        val paso = 6.4f * u; val an = 3.2f * u
        var x = can.left + (can.width % paso) / 2 + (paso - an) / 2
        while (x < can.right - an * 0.5f) {
            val hh = can.height
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(x + p * 1.4f, can.top), Size(an, hh * 0.92f))
            val b = horiz(Rect(x, 0f, x + an, 0f), n(CEDRO_LUZ, o), n(CEDRO, o), n(CEDRO_OSC, o))
            drawRect(b, Offset(x, can.top), Size(an, hh * 0.4f))
            drawRoundRect(b, Offset(x + an * 0.06f, can.top + hh * 0.3f), Size(an * 0.88f, hh * 0.36f), CornerRadius(hh * 0.18f))
            drawRoundRect(b, Offset(x + an * 0.14f, can.top + hh * 0.56f), Size(an * 0.72f, hh * 0.4f), CornerRadius(hh * 0.2f))
            drawLine(n(VETA, o).copy(alpha = 0.6f), Offset(x + an * 0.14f, can.top + hh * 0.66f), Offset(x + an * 0.86f, can.top + hh * 0.66f), p * 0.5f)
            drawLine(n(VETA, o).copy(alpha = 0.6f), Offset(x + an * 0.06f, can.top + hh * 0.36f), Offset(x + an * 0.94f, can.top + hh * 0.36f), p * 0.5f)
            x += paso
        }
        rect(tabla, vert(tabla, n(CEDRO_LUZ, o), n(CEDRO, o), n(CEDRO_OSC, o)))
        vetas(tabla, n(VETA, o).copy(alpha = 0.45f), semilla, p, 0.8f)
        drawLine(n(CEDRO_LUZ, o), Offset(tabla.left, tabla.top + p * 0.5f), Offset(tabla.right, tabla.top + p * 0.5f), p * 0.7f)
        // El cordón tallado del canto.
        val yc = tabla.bottom - tabla.height * 0.28f
        var xc = tabla.left
        while (xc < tabla.right) { drawLine(n(VETA, o).copy(alpha = 0.55f), Offset(xc, yc - tabla.height * 0.12f), Offset(xc + tabla.height * 0.2f, yc + tabla.height * 0.12f), p * 0.6f); xc += tabla.height * 0.28f }
        drawLine(n(VETA, o), Offset(tabla.left, tabla.bottom), Offset(tabla.right, tabla.bottom), p)
    }

    /** Mocárabes: dos hileras de alvéolos colgantes, la de abajo desplazada media pieza, con su sombra dentro. */
    private fun DrawScope.mocarabes(r: Rect, o: Boolean, p: Float) {
        rect(r, n(YESO_SOMBRA, o))
        val filas = 2
        val hh = r.height / filas
        val c = hh * 1.15f
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (j in 0 until filas) {
                val y0 = r.top + j * hh
                var x = r.left - c + (if (j % 2 == 1) c / 2 else 0f)
                while (x < r.right + c) {
                    // La cara del alvéolo: el yeso con el hueco del arco cortado abajo.
                    val cara = Path().apply {
                        moveTo(x, y0); lineTo(x + c, y0); lineTo(x + c, y0 + hh)
                        cubicTo(x + c, y0 + hh * 0.3f, x, y0 + hh * 0.3f, x, y0 + hh); close()
                    }
                    drawPath(cara, Brush.verticalGradient(listOf(n(YESO_LUZ, o), n(YESO, o), n(YESO_OSC, o)), y0, y0 + hh))
                    drawPath(Path().apply { moveTo(x + c, y0 + hh); cubicTo(x + c, y0 + hh * 0.3f, x, y0 + hh * 0.3f, x, y0 + hh) }, n(YESO_SOMBRA, o), style = Stroke(p * 0.6f))
                    // La gota que cuelga entre dos alvéolos.
                    drawPath(Path().apply {
                        moveTo(x - c * 0.1f, y0 + hh * 0.72f); lineTo(x + c * 0.1f, y0 + hh * 0.72f); lineTo(x, y0 + hh * 1.02f); close()
                    }, n(YESO_OSC, o))
                    x += c
                }
            }
        }
        drawLine(n(YESO_SOMBRA, o), Offset(r.left, r.bottom), Offset(r.right, r.bottom), p * 0.7f)
    }

    /** Una columna nazarí de fuste [cw]: basa, fuste de mármol, anillos, capitel cúbico con ataurique pintado y cimacio. */
    private fun DrawScope.columna(xc: Float, top: Float, bottom: Float, cw: Float, o: Boolean, p: Float) {
        val mar = n(MARMOL, o); val luz = n(MARMOL_LUZ, o); val osc = n(MARMOL_OSC, o)
        val capH = cw * 2.0f; val baseH = cw * 0.7f
        val fTop = top + capH; val fBot = bottom - baseH
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), xc + cw / 2, xc + cw * 1.1f), Offset(xc + cw / 2, fTop), Size(cw * 0.6f, fBot - fTop))
        val f = Rect(xc - cw / 2, fTop, xc + cw / 2, fBot)
        rect(f, horiz(f, osc, luz, mar, osc))
        drawLine(Color.White.copy(alpha = if (o) 0.1f else 0.5f), Offset(xc - cw * 0.18f, fTop), Offset(xc - cw * 0.18f, fBot), p * 0.6f)
        // Los anillos bajo el capitel.
        for (k in 0 until 3) {
            val y = fTop - cw * 0.05f + k * cw * 0.2f
            val a = Rect(xc - cw * 0.6f, y, xc + cw * 0.6f, y + cw * 0.13f)
            drawRoundRect(horiz(a, osc, luz, osc), a.topLeft, a.size, CornerRadius(a.height / 2))
        }
        // El cuerpo bajo del capitel, cilíndrico, con hojas lisas.
        val cil = Rect(xc - cw * 0.62f, top + capH * 0.5f, xc + cw * 0.62f, fTop - cw * 0.05f)
        rect(cil, horiz(cil, osc, luz, mar, osc))
        for (k in 0 until 3) {
            val hx = cil.left + cil.width * (k + 0.5f) / 3
            drawPath(Path().apply { moveTo(hx - cil.width * 0.13f, cil.bottom); quadraticTo(hx, cil.top - cil.height * 0.1f, hx + cil.width * 0.13f, cil.bottom); close() }, osc.copy(alpha = 0.7f))
        }
        // El cubo pintado: banda azul con ataurique dorado y filete rojo.
        val cubo = Rect(xc - cw * 0.85f, top + capH * 0.16f, xc + cw * 0.85f, top + capH * 0.52f)
        rect(cubo, horiz(cubo, osc, luz, mar, osc))
        val banda = Rect(cubo.left + cw * 0.12f, cubo.top + cubo.height * 0.2f, cubo.right - cw * 0.12f, cubo.bottom - cubo.height * 0.2f)
        rect(banda, n(AZUL, o, 0.45f))
        drawArc(n(MELADO, o, 0.4f), 180f, 270f, false, Offset(banda.left + banda.width * 0.12f, banda.top + banda.height * 0.2f), Size(banda.width * 0.36f, banda.height * 0.6f), style = Stroke(p * 0.6f))
        drawArc(n(MELADO, o, 0.4f), 0f, 270f, false, Offset(banda.left + banda.width * 0.52f, banda.top + banda.height * 0.2f), Size(banda.width * 0.36f, banda.height * 0.6f), style = Stroke(p * 0.6f))
        drawLine(n(ALMAGRA, o, 0.45f), Offset(cubo.left, cubo.bottom - p * 0.4f), Offset(cubo.right, cubo.bottom - p * 0.4f), p * 0.8f)
        // El cimacio.
        val cim = Rect(xc - cw * 1.0f, top, xc + cw * 1.0f, top + capH * 0.16f)
        rect(cim, vert(cim, luz, osc))
        // La basa: toro y plinto.
        val toro = Rect(xc - cw * 0.66f, fBot, xc + cw * 0.66f, fBot + baseH * 0.4f)
        drawRoundRect(vert(toro, luz, osc), toro.topLeft, toro.size, CornerRadius(toro.height / 2))
        val pl = Rect(xc - cw * 0.78f, toro.bottom, xc + cw * 0.78f, bottom)
        rect(pl, vert(pl, mar, osc))
    }

    /** Una llama de candil con su halo; de noche el halo es grande. */
    private fun DrawScope.llama(c: Offset, r: Float, oscuro: Boolean) {
        mancha(c, r * (if (oscuro) 8f else 3f), LLAMA.copy(alpha = if (oscuro) 0.36f else 0.22f))
        drawPath(Path().apply {
            moveTo(c.x, c.y - r * 1.6f); quadraticTo(c.x + r * 0.9f, c.y - r * 0.2f, c.x, c.y + r * 0.5f); quadraticTo(c.x - r * 0.9f, c.y - r * 0.2f, c.x, c.y - r * 1.6f); close()
        }, Color(0xFFFFA733))
        drawOval(Color(0xFFFFF1C2), Offset(c.x - r * 0.3f, c.y - r * 0.6f), Size(r * 0.6f, r * 0.95f))
    }

    /** Un candil de latón de piquera, de largo [l], apoyado en [base]: el depósito, el asa, la piquera y la llama. */
    private fun DrawScope.candil(base: Offset, l: Float, sg: Float, o: Boolean, p: Float) {
        val hh = l * 0.42f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(base.x - l * 0.45f, base.y - hh * 0.12f), Size(l * 0.9f, hh * 0.24f))
        // El pie.
        drawPath(Path().apply { moveTo(base.x - l * 0.14f, base.y); lineTo(base.x + l * 0.14f, base.y); lineTo(base.x + l * 0.07f, base.y - hh * 0.3f); lineTo(base.x - l * 0.07f, base.y - hh * 0.3f); close() },
            horiz(Rect(base.x - l * 0.14f, 0f, base.x + l * 0.14f, 0f), n(LATON_OSC, o), n(LATON_LUZ, o), n(LATON, o), n(LATON_OSC, o)))
        // El depósito y la piquera.
        val dep = Rect(base.x - l * 0.28f, base.y - hh * 0.78f, base.x + l * 0.28f, base.y - hh * 0.26f)
        drawPath(Path().apply {
            moveTo(dep.left, dep.center.y); quadraticTo(dep.left, dep.top, base.x, dep.top); quadraticTo(dep.right, dep.top, dep.right, dep.center.y)
            lineTo(base.x + sg * l * 0.5f, dep.top + dep.height * 0.1f); lineTo(base.x + sg * l * 0.52f, dep.top + dep.height * 0.35f)
            quadraticTo(dep.right, dep.bottom + dep.height * 0.2f, base.x, dep.bottom); quadraticTo(dep.left, dep.bottom, dep.left, dep.center.y); close()
        }, Brush.radialGradient(listOf(n(LATON_LUZ, o), n(LATON, o), n(LATON_OSC, o)), Offset(base.x - l * 0.08f, dep.top + dep.height * 0.3f), l * 0.5f))
        drawOval(n(LATON_OSC, o), Offset(base.x - l * 0.1f, dep.top - hh * 0.04f), Size(l * 0.2f, hh * 0.1f))
        // El asa.
        drawArc(n(LATON_OSC, o), if (sg > 0) 90f else -90f, 180f, false, Offset(base.x - sg * l * 0.28f - l * 0.1f, dep.top + dep.height * 0.05f), Size(l * 0.2f, dep.height * 0.7f), style = Stroke(p * 1.1f))
        llama(Offset(base.x + sg * l * 0.5f, dep.top - hh * 0.05f), hh * 0.18f, o)
    }

    /** Una banda de taracea: cedro oscuro con estrellas de marfil de ocho puntas y rombos de ébano, entre filetes. */
    private fun DrawScope.taracea(r: Rect, o: Boolean, p: Float) {
        rect(r, vert(r, n(CEDRO, o), n(CEDRO_OSC, o)))
        val s = r.height
        drawLine(n(MARFIL, o), Offset(r.left, r.top + s * 0.12f), Offset(r.right, r.top + s * 0.12f), p * 0.5f)
        drawLine(n(MARFIL, o), Offset(r.left, r.bottom - s * 0.12f), Offset(r.right, r.bottom - s * 0.12f), p * 0.5f)
        var x = r.left + s * 0.7f; var k = 0
        while (x < r.right - s * 0.4f) {
            val c = Offset(x, r.center.y)
            if (k % 2 == 0) {
                drawPath(estrella(c, s * 0.3f, 8, 0.6f), n(MARFIL, o))
                drawCircle(n(VERDE, o, 0.4f), s * 0.09f, c)
            } else {
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.22f); lineTo(c.x + s * 0.16f, c.y); lineTo(c.x, c.y + s * 0.22f); lineTo(c.x - s * 0.16f, c.y); close() }, n(NEGRO, o))
                drawCircle(n(LATON, o), s * 0.05f, c)
            }
            x += s * 0.62f; k++
        }
    }

    /** Un cojín con su bordado y una borla en cada esquina de abajo. */
    private fun DrawScope.cojin(r: Rect, tela: Color, bordado: Color, o: Boolean, p: Float) {
        drawRoundRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + p, r.top + p * 1.5f), r.size, CornerRadius(r.height * 0.3f))
        drawRoundRect(Brush.radialGradient(listOf(mez(tela, Color.White, 0.28f), tela, mez(tela, Color.Black, 0.35f)), Offset(r.center.x - r.width * 0.15f, r.center.y - r.height * 0.2f), r.width * 0.75f), r.topLeft, r.size, CornerRadius(r.height * 0.3f))
        val b = Rect(r.left + r.width * 0.16f, r.top + r.height * 0.4f, r.right - r.width * 0.16f, r.top + r.height * 0.6f)
        rect(b, bordado)
        drawPath(estrella(r.center, r.height * 0.18f, 8, 0.55f), mez(tela, Color.White, 0.55f))
        listOf(r.left + r.width * 0.06f, r.right - r.width * 0.06f).forEach { x ->
            drawLine(n(LATON, o), Offset(x, r.bottom - r.height * 0.1f), Offset(x, r.bottom + r.height * 0.18f), p * 1.1f)
            drawCircle(n(LATON_LUZ, o), p * 0.9f, Offset(x, r.bottom - r.height * 0.08f))
        }
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.SOCIAL)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val suelo = h * 0.955f
        val t = 2.6f * u
        val marco = ni.inflate(t)
        val zTop = marco.bottom + 1.2f * u

        // El muro de yesería, el zócalo de alicatado y el piso de mármol.
        yeseria(Rect(0f, 0f, w, zTop), o, p, 7f * u)
        alicatado(Rect(0f, zTop, w, suelo), o, p, 3)
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(MARMOL_OSC, o), mez(n(MARMOL_OSC, o), Color.Black, 0.2f)))
        drawLine(n(MARMOL_LUZ, o), Offset(0f, suelo), Offset(w, suelo), p * 0.7f)
        var xj = (w / 2) % (18f * u)
        while (xj < w) { drawLine(n(YESO_SOMBRA, o), Offset(xj, suelo), Offset(xj + (xj - w / 2) * 0.12f, h), p * 0.6f); xj += 18f * u }

        // El alero de cedro con sus canecillos, los mocárabes y la inscripción.
        val aleroB = h * 0.05f; val canB = h * 0.088f; val mocB = h * 0.135f
        mocarabes(Rect(0f, canB, w, mocB), o, p)
        alero(Rect(0f, 0f, w, aleroB), Rect(0f, aleroB, w, canB), o, p, u, e.semilla)
        inscripcion(Rect(0f, mocB, w, marco.top - 0.8f * u), o, p, e.semilla + 4)

        // Las columnas nazaríes a los lados.
        val cw = minOf(w * 0.03f, 3.8f * u)
        val xs = listOf(maxOf(w * 0.048f, cw * 1.2f), w - maxOf(w * 0.048f, cw * 1.2f))
        xs.forEach { columna(it, mocB, suelo, cw, o, p) }

        // El alfiz: la cenefa de estrellas alrededor del paño, con estrellas de latón en las esquinas.
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(marco.left + 0.7f * u, marco.top + 1.1f * u), marco.size)
        rect(marco, n(MARFIL, o, 0.5f))
        val colores = listOf(VERDE, ALMAGRA, AZUL, MELADO)
        listOf(true, false).forEach { horizontal ->
            val largo = if (horizontal) marco.width else marco.height
            val cuantas = (largo / (t * 1.2f)).toInt()
            for (k in 0 until cuantas) {
                val f = (k + 0.5f) / cuantas
                val col = n(colores[k % 4], o, 0.45f)
                if (horizontal) listOf(marco.top + t / 2, marco.bottom - t / 2).forEach { y -> drawPath(estrella(Offset(marco.left + marco.width * f, y), t * 0.36f, 8, 0.6f), col) }
                else listOf(marco.left + t / 2, marco.right - t / 2).forEach { x -> drawPath(estrella(Offset(x, marco.top + marco.height * f), t * 0.36f, 8, 0.6f), col) }
            }
        }
        drawRect(n(MELADO, o), marco.topLeft, marco.size, style = Stroke(p * 0.8f))
        rect(ni.inflate(0.7f * u), n(VERDE, o, 0.45f))
        rect(ni, nicho(e, o, YESO))
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 3f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent), ni.top, ni.top + 3f * u))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { c ->
            drawPath(estrella(c, t * 0.75f, 8, 0.62f), n(LATON_OSC, o))
            drawPath(estrella(c, t * 0.62f, 8, 0.62f), Brush.radialGradient(listOf(n(LATON_LUZ, o), n(LATON, o)), Offset(c.x - t * 0.2f, c.y - t * 0.2f), t))
            drawCircle(n(VERDE, o, 0.4f), t * 0.16f, c)
        }

        // Abajo: dos almadrabas con sus cojines, la fuente de taza al medio, granadas, un laúd y dos candiles.
        val hz = suelo - zTop
        almadraba(w * 0.115f, w * 0.415f, suelo, hz, true, e, o, p)
        almadraba(w * 0.585f, w * 0.885f, suelo, hz, false, e, o, p)
        fuente(w / 2, suelo, hz * 1.0f, minOf(w * 0.15f, hz * 1.6f), o, p)
    }

    /** Una almadraba: la tarima taraceada, el colchón, dos cojines contra el muro y, encima, granadas o un laúd y un candil. */
    private fun DrawScope.almadraba(x0: Float, x1: Float, yF: Float, hz: Float, izq: Boolean, e: EstadoMueble, o: Boolean, p: Float) {
        val l = x1 - x0
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(x0 - l * 0.01f, yF - hz * 0.06f), Size(l * 1.02f, hz * 0.14f))
        // La tarima de cedro con su frente taraceado.
        val tar = Rect(x0, yF - hz * 0.3f, x1, yF)
        rect(tar, vert(tar, n(CEDRO_LUZ, o), n(CEDRO_OSC, o)))
        taracea(Rect(tar.left + l * 0.03f, tar.top + tar.height * 0.22f, tar.right - l * 0.03f, tar.bottom - tar.height * 0.18f), o, p)
        drawLine(n(CEDRO_LUZ, o), Offset(tar.left, tar.top), Offset(tar.right, tar.top), p * 0.8f)
        // El colchón, del color secundario, con su fleco.
        val tela = if (o) mez(e.secundario, NOCHE, 0.45f) else e.secundario
        val col = Rect(x0 + l * 0.01f, yF - hz * 0.48f, x1 - l * 0.01f, tar.top + hz * 0.02f)
        drawRoundRect(vert(col, mez(tela, Color.White, 0.25f), tela, mez(tela, Color.Black, 0.3f)), col.topLeft, col.size, CornerRadius(col.height * 0.4f))
        drawLine(mez(tela, Color.White, 0.5f).copy(alpha = 0.7f), Offset(col.left + col.height, col.top + col.height * 0.35f), Offset(col.right - col.height, col.top + col.height * 0.35f), p * 0.6f)
        var xf = col.left + col.height * 0.5f
        while (xf < col.right - col.height * 0.5f) { drawLine(n(LATON, o).copy(alpha = 0.8f), Offset(xf, col.bottom - p), Offset(xf, col.bottom + hz * 0.04f), p * 0.5f); xf += p * 2.4f }
        // Dos cojines contra el muro, del color de acento.
        val cTela = if (o) mez(e.acento, NOCHE, 0.45f) else e.acento
        val cw = l * 0.22f; val ch = hz * 0.42f
        val xs = if (izq) listOf(x0 + l * 0.2f, x0 + l * 0.43f) else listOf(x1 - l * 0.2f - cw, x1 - l * 0.43f - cw)
        xs.forEachIndexed { k, x ->
            cojin(Rect(x, col.top - ch * 0.8f, x + cw, col.top + ch * 0.2f), if (k == 0) cTela else mez(cTela, n(MARFIL, o), 0.45f), n(MELADO, o, 0.45f), o, p)
        }
        // Encima, del lado de la fuente: un cuenco de granadas o un laúd.
        val lejos = if (izq) x1 - l * 0.24f else x0 + l * 0.24f
        if (izq) granadas(Offset(lejos, col.top + col.height * 0.3f), hz * 0.24f, o, p)
        else laud(Offset(lejos, col.top - hz * 0.02f), hz * 0.72f, o, p)
        // El candil, en la esquina de afuera de la tarima.
        candil(Offset(if (izq) x0 + l * 0.1f else x1 - l * 0.1f, tar.top + p), hz * 0.5f, if (izq) 1f else -1f, o, p)
    }

    /** Un cuenco de latón con tres granadas. */
    private fun DrawScope.granadas(c: Offset, r: Float, o: Boolean, p: Float) {
        listOf(Offset(-0.7f, -0.5f), Offset(0.7f, -0.55f), Offset(0f, -0.95f)).forEach { d ->
            val g = Offset(c.x + d.x * r, c.y + d.y * r)
            drawCircle(Brush.radialGradient(listOf(mez(n(GRANADA, o), Color.White, 0.3f), n(GRANADA, o), mez(n(GRANADA, o), Color.Black, 0.35f)), Offset(g.x - r * 0.25f, g.y - r * 0.25f), r * 0.9f), r * 0.62f, g)
            drawPath(estrella(Offset(g.x, g.y - r * 0.62f), r * 0.2f, 5, 0.45f), mez(n(GRANADA, o), Color.Black, 0.4f))
        }
        val cu = Path().apply { moveTo(c.x - r * 1.5f, c.y - r * 0.4f); lineTo(c.x + r * 1.5f, c.y - r * 0.4f); quadraticTo(c.x + r * 1.3f, c.y + r * 0.6f, c.x, c.y + r * 0.62f); quadraticTo(c.x - r * 1.3f, c.y + r * 0.6f, c.x - r * 1.5f, c.y - r * 0.4f); close() }
        drawPath(cu, Brush.horizontalGradient(listOf(n(LATON_OSC, o), n(LATON_LUZ, o), n(LATON, o), n(LATON_OSC, o)), c.x - r * 1.5f, c.x + r * 1.5f))
        drawLine(n(LATON_LUZ, o), Offset(c.x - r * 1.5f, c.y - r * 0.4f), Offset(c.x + r * 1.5f, c.y - r * 0.4f), p * 0.8f)
        drawLine(n(LATON_OSC, o), Offset(c.x - r * 1.1f, c.y + r * 0.1f), Offset(c.x + r * 1.1f, c.y + r * 0.1f), p * 0.5f)
    }

    /** Un laúd recostado: la caja de duelas, el rosetón, el puente, el mástil y el clavijero doblado. */
    private fun DrawScope.laud(c: Offset, l: Float, o: Boolean, p: Float) {
        rotate(-14f, c) {
            val caja = Rect(c.x - l * 0.5f, c.y - l * 0.3f, c.x + l * 0.1f, c.y + l * 0.12f)
            drawOval(Color.Black.copy(alpha = 0.28f), Offset(caja.left + p, caja.top + p * 2), caja.size)
            drawOval(Brush.radialGradient(listOf(n(CEDRO_LUZ, o), n(CEDRO, o), n(CEDRO_OSC, o)), Offset(caja.center.x - l * 0.08f, caja.center.y - l * 0.08f), l * 0.4f), caja.topLeft, caja.size)
            clipPath(Path().apply { addOval(caja) }) {
                for (k in 1..4) drawLine(n(VETA, o).copy(alpha = 0.5f), Offset(caja.left, caja.top + caja.height * k / 5), Offset(caja.right, caja.top + caja.height * k / 5 + l * 0.03f), p * 0.5f)
            }
            drawCircle(n(VETA, o), l * 0.06f, Offset(caja.center.x + l * 0.03f, caja.center.y - l * 0.02f))
            drawPath(estrella(Offset(caja.center.x + l * 0.03f, caja.center.y - l * 0.02f), l * 0.055f, 8, 0.6f), n(MARFIL, o).copy(alpha = 0.7f), style = Stroke(p * 0.4f))
            drawLine(n(VETA, o), Offset(caja.left + l * 0.1f, caja.center.y - l * 0.05f), Offset(caja.left + l * 0.1f, caja.center.y + l * 0.05f), p * 1.4f)
            val m0 = Offset(caja.right - l * 0.02f, caja.center.y - l * 0.02f)
            val m1 = Offset(m0.x + l * 0.36f, m0.y - l * 0.03f)
            drawLine(n(VETA, o), m0, m1, l * 0.07f, StrokeCap.Round)
            drawLine(n(CEDRO_OSC, o), m1, Offset(m1.x + l * 0.08f, m1.y + l * 0.1f), l * 0.06f, StrokeCap.Round)
            for (k in 0 until 3) drawCircle(n(MARFIL, o), p * 0.7f, Offset(m1.x + l * 0.02f + k * l * 0.025f, m1.y + l * 0.025f + k * l * 0.03f))
            for (k in -1..1) drawLine(n(MARFIL, o).copy(alpha = 0.7f), Offset(caja.left + l * 0.1f, caja.center.y + k * l * 0.012f), Offset(m1.x, m1.y + k * l * 0.01f), p * 0.3f)
        }
    }

    /** La fuente de taza: el pilón bajo de mármol, el fuste, la taza y el surtidor con sus gotas. */
    private fun DrawScope.fuente(cx: Float, yF: Float, alto: Float, ancho: Float, o: Boolean, p: Float) {
        val mar = n(MARMOL, o); val luz = n(MARMOL_LUZ, o); val osc = n(MARMOL_OSC, o)
        val agua = if (o) mez(AGUA, NOCHE, 0.25f) else AGUA
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - ancho * 0.55f, yF - alto * 0.06f), Size(ancho * 1.1f, alto * 0.14f))
        // El pilón: un brocal bajo.
        val pil = Rect(cx - ancho * 0.5f, yF - alto * 0.22f, cx + ancho * 0.5f, yF)
        rect(pil, horiz(pil, osc, luz, mar, osc))
        drawLine(luz, Offset(pil.left, pil.top), Offset(pil.right, pil.top), p)
        for (k in 1..3) drawLine(osc, Offset(pil.left + pil.width * k / 4, pil.top), Offset(pil.left + pil.width * k / 4, pil.bottom), p * 0.5f)
        // El fuste y la taza.
        val fu = Rect(cx - ancho * 0.07f, yF - alto * 0.62f, cx + ancho * 0.07f, pil.top)
        rect(fu, horiz(fu, osc, luz, mar, osc))
        for (k in 0 until 2) drawRoundRect(horiz(fu, osc, luz, osc), Offset(fu.left - ancho * 0.02f, fu.top + k * alto * 0.06f), Size(fu.width + ancho * 0.04f, alto * 0.035f), CornerRadius(alto * 0.02f))
        val ty = yF - alto * 0.68f
        val taza = Path().apply { moveTo(cx - ancho * 0.38f, ty); lineTo(cx + ancho * 0.38f, ty); quadraticTo(cx + ancho * 0.3f, ty + alto * 0.16f, cx, ty + alto * 0.17f); quadraticTo(cx - ancho * 0.3f, ty + alto * 0.16f, cx - ancho * 0.38f, ty); close() }
        drawPath(taza, Brush.horizontalGradient(listOf(osc, luz, mar, osc), cx - ancho * 0.38f, cx + ancho * 0.38f))
        drawOval(agua, Offset(cx - ancho * 0.36f, ty - alto * 0.03f), Size(ancho * 0.72f, alto * 0.06f))
        drawLine(luz, Offset(cx - ancho * 0.38f, ty), Offset(cx + ancho * 0.38f, ty), p * 0.8f)
        // El surtidor y el agua que cae en arco a ambos lados.
        val pico = Offset(cx, ty - alto * 0.3f)
        drawLine(agua, Offset(cx, ty - alto * 0.02f), pico, p * 1.2f, StrokeCap.Round)
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply { moveTo(pico.x, pico.y); quadraticTo(cx + s * ancho * 0.3f, pico.y - alto * 0.08f, cx + s * ancho * 0.36f, ty) }, agua.copy(alpha = 0.85f), style = Stroke(p * 0.8f, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(cx + s * ancho * 0.38f, ty + alto * 0.02f); quadraticTo(cx + s * ancho * 0.46f, ty + alto * 0.1f, cx + s * ancho * 0.44f, pil.top) }, agua.copy(alpha = 0.6f), style = Stroke(p * 0.6f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(p * 1.5f, p * 1.2f))))
        }
        drawCircle(Color.White.copy(alpha = 0.8f), p * 0.7f, pico)
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.MAPAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val faldonA = h * 0.8f; val faldonB = h * 0.875f; val suelo = h * 0.955f

        // El muro de yesería, el zócalo de alicatado detrás de las patas y el piso de olambrillas.
        yeseria(Rect(0f, 0f, w, faldonA), o, p, 7f * u)
        alicatado(Rect(0f, faldonA, w, suelo), o, p, 2)
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(BARRO, o), mez(n(BARRO, o), Color.Black, 0.25f)))
        val lo = 9f * u
        var xo = (w / 2) % lo - lo
        while (xo < w) {
            drawLine(mez(n(BARRO, o), Color.Black, 0.35f), Offset(xo, suelo), Offset(xo + (xo - w / 2) * 0.1f, h), p * 0.6f)
            rect(Rect(xo - 0.9f * u, suelo + (h - suelo) * 0.3f, xo + 0.9f * u, suelo + (h - suelo) * 0.3f + 1.8f * u), n(VERDE, o, 0.45f))
            xo += lo
        }

        // El tablero de cedro: el canto, la cara con su veta y la orla de taracea.
        val t = Rect(w * 0.02f, h * 0.022f, w * 0.98f, faldonA)
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(t.left, t.top + 1.2f * u), t.size, CornerRadius(1.2f * u))
        drawRoundRect(n(CEDRO_OSC, o), t.topLeft, t.size, CornerRadius(1.2f * u))
        val cara = t.deflate(1.1f * u)
        rect(cara, Brush.linearGradient(listOf(n(CEDRO_LUZ, o), n(CEDRO, o), mez(n(CEDRO, o), n(CEDRO_OSC, o), 0.5f)), cara.topLeft, cara.bottomRight))
        vetas(cara, n(VETA, o).copy(alpha = 0.45f), e.semilla, p, 0.9f)
        val tb = 2.2f * u
        val orla = cara.deflate(1.2f * u)
        taracea(Rect(orla.left, orla.top, orla.right, orla.top + tb), o, p)
        taracea(Rect(orla.left, orla.bottom - tb, orla.right, orla.bottom), o, p)
        drawRect(n(LATON, o), orla.topLeft, orla.size, style = Stroke(p * 0.6f))
        val dentro = Rect(orla.left, orla.top + tb, orla.right, orla.bottom - tb)

        // El halo del candil, antes que la vitela.
        val izq0 = dentro.left + 1.2f * u; val izq1 = ni.left - 1.6f * u
        val der0 = ni.right + 1.6f * u; val der1 = dentro.right - 1.2f * u
        val zona = minOf(izq1 - izq0, der1 - der0)
        val lampB = Offset((der0 + der1) / 2, ni.bottom - ni.height * 0.04f)
        mancha(Offset(lampB.x, lampB.y - zona * 0.3f), (if (o) 34f else 14f) * u, LLAMA.copy(alpha = if (o) 0.3f else 0.14f))

        // La vitela (el nicho), con su sombra y cuatro pesas de latón en las esquinas.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(ni.left + 0.8f * u, ni.top + 1.3f * u), ni.size)
        rect(ni, nicho(e, o, Color(0xFFE8D9B4)))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            val rnd = Random(e.semilla.toLong() * 13 + 5)
            repeat(10) { mancha(Offset(ni.left + ni.width * rnd.nextFloat(), ni.top + ni.height * rnd.nextFloat()), (6f + 10f * rnd.nextFloat()) * u, (if (o) Color.White else Color(0xFF9A7A4A)).copy(alpha = 0.035f)) }
            rect(Rect(ni.left, ni.top, ni.right, ni.top + 2f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent), ni.top, ni.top + 2f * u))
        }
        drawRect(n(YESO_SOMBRA, o), ni.topLeft, ni.size, style = Stroke(p * 0.5f))
        listOf(ni.topLeft, Offset(ni.right, ni.top), Offset(ni.left, ni.bottom), ni.bottomRight).forEach { c -> pesa(c, 1.9f * u, o, p) }

        // El astrolabio, arriba a la izquierda, con su argolla.
        val ra = minOf(zona * 0.42f, 9f * u)
        astrolabio(Offset((izq0 + izq1) / 2, ni.top + ra + 2.6f * u), ra, o, p)
        // El tintero octogonal de latón con sus dos cálamos, abajo a la izquierda.
        tintero(Offset((izq0 + izq1) / 2, ni.bottom - ni.height * 0.12f), minOf(zona * 0.34f, 6f * u), o, p)
        // Un rollo de vitela atado, arriba a la derecha.
        val rx = (der0 + der1) / 2
        val rl = minOf(zona * 0.42f, 8f * u)
        val rollo = Rect(rx - rl * 0.38f, ni.top + 1.5f * u, rx + rl * 0.38f, ni.top + ni.height * 0.5f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(rollo.left + 0.6f * u, rollo.top + u), rollo.size, CornerRadius(rollo.width / 2))
        val vit = n(Color(0xFFE6D4AC), o)
        drawRoundRect(horiz(rollo, mez(vit, Color.Black, 0.3f), mez(vit, Color.White, 0.3f), vit, mez(vit, Color.Black, 0.35f)), rollo.topLeft, rollo.size, CornerRadius(rollo.width / 2))
        drawOval(mez(vit, Color.Black, 0.3f), Offset(rollo.left, rollo.top), Size(rollo.width, rollo.width * 0.5f))
        drawOval(mez(vit, Color.Black, 0.5f), Offset(rollo.center.x - rollo.width * 0.12f, rollo.top + rollo.width * 0.17f), Size(rollo.width * 0.24f, rollo.width * 0.14f))
        val cinta = n(ALMAGRA, o, 0.45f)
        rect(Rect(rollo.left, rollo.center.y - 0.6f * u, rollo.right, rollo.center.y + 0.6f * u), cinta)
        drawLine(cinta, Offset(rollo.right, rollo.center.y), Offset(rollo.right + 1.6f * u, rollo.center.y + 3f * u), p * 1.2f, StrokeCap.Round)
        drawLine(cinta, Offset(rollo.right, rollo.center.y), Offset(rollo.right + 0.6f * u, rollo.center.y + 3.6f * u), p * 1.2f, StrokeCap.Round)
        // El candil encendido, abajo a la derecha.
        clipRect(ni.right + p, dentro.top, dentro.right, dentro.bottom) { candil(lampB, minOf(zona * 1.1f, 17f * u), -1f, o, p) }

        // La regla de boj con sus marcas y el compás abierto, al pie de la vitela.
        val yb = (ni.bottom + dentro.bottom) / 2
        val regla = Rect(ni.left + ni.width * 0.06f, yb - 1f * u, ni.left + ni.width * 0.52f, yb + 1f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(regla.left + 0.5f * u, regla.top + 0.7f * u), regla.size)
        rect(regla, vert(regla, n(Color(0xFFE2C68A), o), n(Color(0xFFB08A4A), o)))
        var xr = regla.left + regla.width / 30; var kr = 1
        while (xr < regla.right - u) { drawLine(n(VETA, o), Offset(xr, regla.top), Offset(xr, regla.top + regla.height * (if (kr % 5 == 0) 0.7f else 0.35f)), p * 0.4f); xr += regla.width / 30; kr++ }
        val cc = Offset(ni.left + ni.width * 0.74f, yb - 0.8f * u)
        listOf(-1f, 1f).forEach { s -> drawLine(n(LATON_OSC, o), cc, Offset(cc.x + s * 7f * u, cc.y + 1.6f * u), p * 1.3f, StrokeCap.Round); drawLine(n(LATON_LUZ, o), cc, Offset(cc.x + s * 6.6f * u, cc.y + 1.4f * u), p * 0.5f) }
        drawCircle(n(LATON, o), 0.9f * u, cc); drawCircle(n(LATON_OSC, o), 0.9f * u, cc, style = Stroke(p * 0.4f))

        // El faldón taraceado y las patas torneadas.
        val fa = Rect(t.left + 1.2f * u, faldonA, t.right - 1.2f * u, faldonB)
        rect(fa, vert(fa, n(CEDRO, o), n(CEDRO_OSC, o)))
        taracea(Rect(fa.left + fa.width * 0.12f, fa.top + fa.height * 0.18f, fa.right - fa.width * 0.12f, fa.bottom - fa.height * 0.18f), o, p)
        drawLine(n(LATON, o), Offset(fa.left, fa.top + p * 0.6f), Offset(fa.right, fa.top + p * 0.6f), p * 0.8f)
        // Los arquillos lobulados bajo el faldón.
        val an = minOf(w * 0.045f, 7f * u)
        listOf(fa.left + an, fa.right - an).forEach { x -> pata(x, faldonB, suelo, an, o) }
        listOf(fa.left + fa.width * 0.06f, fa.right - fa.width * 0.06f).forEach { x ->
            drawPath(estrella(Offset(x, fa.center.y), fa.height * 0.32f, 8, 0.62f), n(LATON, o))
            drawCircle(n(LATON_OSC, o), fa.height * 0.08f, Offset(x, fa.center.y))
        }
    }

    /** Una pesa de latón octogonal con su estrella, para sujetar la esquina de la vitela. */
    private fun DrawScope.pesa(c: Offset, r: Float, o: Boolean, p: Float) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + p, c.y + p * 1.6f))
        val oct = estrella(c, r, 8, 0.93f, -67.5f)
        drawPath(oct, Brush.radialGradient(listOf(n(LATON_LUZ, o), n(LATON, o), n(LATON_OSC, o)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.4f))
        drawPath(estrella(c, r * 0.6f, 8, 0.6f), n(LATON_OSC, o).copy(alpha = 0.7f), style = Stroke(p * 0.5f))
        drawCircle(n(LATON_LUZ, o), r * 0.15f, c)
    }

    /** El astrolabio: la madre con su limbo graduado, la lámina, la red calada con sus punteros y la alidada, y la argolla. */
    private fun DrawScope.astrolabio(c: Offset, r: Float, o: Boolean, p: Float) {
        val lat = n(LATON, o); val luz = n(LATON_LUZ, o); val osc = n(LATON_OSC, o)
        drawCircle(Color.Black.copy(alpha = 0.3f), r * 1.04f, Offset(c.x + r * 0.08f, c.y + r * 0.12f))
        // El trono y la argolla.
        drawPath(Path().apply { moveTo(c.x - r * 0.3f, c.y - r * 0.9f); quadraticTo(c.x, c.y - r * 1.4f, c.x + r * 0.3f, c.y - r * 0.9f); close() }, Brush.radialGradient(listOf(luz, lat, osc), Offset(c.x, c.y - r * 1.1f), r * 0.5f))
        drawCircle(osc, r * 0.17f, Offset(c.x, c.y - r * 1.32f), style = Stroke(r * 0.07f))
        // La madre.
        drawCircle(Brush.radialGradient(listOf(luz, lat, osc), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.5f), r, c)
        drawCircle(osc, r * 0.8f, c, style = Stroke(p * 0.5f))
        for (k in 0 until 36) {
            val ang = k * 10.0 * Math.PI / 180
            val r0 = if (k % 3 == 0) r * 0.8f else r * 0.88f
            drawLine(osc, Offset(c.x + r0 * cos(ang).toFloat(), c.y + r0 * sin(ang).toFloat()), Offset(c.x + r * 0.95f * cos(ang).toFloat(), c.y + r * 0.95f * sin(ang).toFloat()), p * 0.4f)
        }
        // La lámina: los almucantarates y el horizonte.
        drawCircle(mez(lat, osc, 0.3f), r * 0.78f, c)
        for (k in 1..3) drawCircle(osc.copy(alpha = 0.5f), r * 0.2f * k, Offset(c.x, c.y - r * 0.12f * k), style = Stroke(p * 0.35f))
        drawLine(osc.copy(alpha = 0.6f), Offset(c.x - r * 0.78f, c.y), Offset(c.x + r * 0.78f, c.y), p * 0.4f)
        // La red: el círculo de la eclíptica, los punteros flamígeros y el anillo del trópico.
        drawCircle(luz, r * 0.7f, c, style = Stroke(r * 0.07f))
        drawCircle(luz, r * 0.4f, Offset(c.x, c.y - r * 0.24f), style = Stroke(r * 0.06f))
        listOf(30.0, 110.0, 200.0, 280.0, 340.0).forEach { g ->
            val ang = Math.toRadians(g)
            val pu = Offset(c.x + r * 0.62f * cos(ang).toFloat(), c.y + r * 0.62f * sin(ang).toFloat())
            drawPath(Path().apply {
                moveTo(c.x + r * 0.32f * cos(ang - 0.25).toFloat(), c.y + r * 0.32f * sin(ang - 0.25).toFloat())
                quadraticTo(c.x + r * 0.55f * cos(ang + 0.15).toFloat(), c.y + r * 0.55f * sin(ang + 0.15).toFloat(), pu.x, pu.y)
            }, luz, style = Stroke(r * 0.05f, cap = StrokeCap.Round))
        }
        // La alidada y el eje.
        drawLine(osc, Offset(c.x - r * 0.9f, c.y + r * 0.3f), Offset(c.x + r * 0.9f, c.y - r * 0.3f), r * 0.1f, StrokeCap.Round)
        drawLine(luz, Offset(c.x - r * 0.9f, c.y + r * 0.27f), Offset(c.x + r * 0.9f, c.y - r * 0.33f), r * 0.03f)
        drawCircle(osc, r * 0.1f, c); drawCircle(luz, r * 0.04f, c)
    }

    /** El tintero octogonal de latón, visto de arriba, con su tinta y dos cálamos. */
    private fun DrawScope.tintero(c: Offset, r: Float, o: Boolean, p: Float) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + p, c.y + p * 2f))
        drawPath(estrella(c, r, 8, 0.93f, -67.5f), Brush.radialGradient(listOf(n(LATON_LUZ, o), n(LATON, o), n(LATON_OSC, o)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.4f))
        drawPath(estrella(c, r * 0.72f, 8, 0.93f, -67.5f), n(LATON_OSC, o), style = Stroke(p * 0.6f))
        drawCircle(n(NEGRO, o), r * 0.48f, c)
        drawCircle(Color.White.copy(alpha = 0.25f), r * 0.14f, Offset(c.x - r * 0.16f, c.y - r * 0.16f))
        listOf(-0.5f to -1.4f, 0.1f to -1.6f).forEach { (dx, dy) ->
            val fin = Offset(c.x + dx * r * 1.4f, c.y + dy * r)
            drawLine(n(Color(0xFFCDB27A), o), Offset(c.x + dx * r * 0.2f, c.y - r * 0.1f), fin, p * 1.4f, StrokeCap.Round)
            drawLine(n(VETA, o), Offset(c.x + dx * r * 0.2f, c.y - r * 0.1f), Offset(c.x + dx * r * 0.3f, c.y - r * 0.35f), p * 1.2f, StrokeCap.Round)
        }
    }

    /** Una pata torneada de cedro: dado, anillos, bulbo y pie. */
    private fun DrawScope.pata(cx: Float, y0: Float, y1: Float, an: Float, o: Boolean) {
        val hh = y1 - y0
        val mad = n(CEDRO, o); val luz = n(CEDRO_LUZ, o); val osc = n(CEDRO_OSC, o)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - an * 0.8f, y1 - hh * 0.08f), Size(an * 1.6f, hh * 0.16f))
        fun pieza(r: Rect, oval: Boolean) {
            val b = Brush.horizontalGradient(listOf(osc, luz, mad, osc), r.left, r.right)
            if (oval) drawOval(b, r.topLeft, r.size) else rect(r, b)
        }
        pieza(Rect(cx - an * 0.5f, y0, cx + an * 0.5f, y0 + hh * 0.18f), false)
        pieza(Rect(cx - an * 0.56f, y0 + hh * 0.16f, cx + an * 0.56f, y0 + hh * 0.26f), true)
        pieza(Rect(cx - an * 0.24f, y0 + hh * 0.24f, cx + an * 0.24f, y0 + hh * 0.34f), false)
        pieza(Rect(cx - an * 0.52f, y0 + hh * 0.3f, cx + an * 0.52f, y0 + hh * 0.66f), true)
        pieza(Rect(cx - an * 0.22f, y0 + hh * 0.64f, cx + an * 0.22f, y0 + hh * 0.78f), false)
        pieza(Rect(cx - an * 0.42f, y0 + hh * 0.76f, cx + an * 0.42f, y1), true)
        drawLine(n(LATON, o), Offset(cx - an * 0.5f, y0 + hh * 0.48f), Offset(cx + an * 0.5f, y0 + hh * 0.48f), an * 0.06f)
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.OTRAS_VIDAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val suelo = h * 0.965f

        // El muro, el alero con sus canecillos, los mocárabes y la inscripción.
        yeseria(Rect(0f, 0f, w, suelo), o, p, 7f * u)
        val aleroB = h * 0.055f; val canB = h * 0.095f; val mocB = h * 0.15f
        mocarabes(Rect(0f, canB, w, mocB), o, p)
        alero(Rect(0f, 0f, w, aleroB), Rect(0f, aleroB, w, canB), o, p, u, e.semilla)

        // El alfiz que enmarca la arquería: filete de latón y banda de inscripción arriba.
        val marco = Rect(ni.left - 2.2f * u, mocB + 0.4f * u, ni.right + 2.2f * u, ni.bottom + 1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(marco.left + 0.6f * u, marco.top + u), marco.size)
        rect(marco, n(MARFIL, o, 0.5f))
        inscripcion(Rect(marco.left + 0.6f * u, marco.top + 0.5f * u, marco.right - 0.6f * u, ni.top - 0.4f * u), o, p, e.semilla + 9)
        drawRect(n(MELADO, o), marco.topLeft, marco.size, style = Stroke(p * 0.8f))
        // Una estrella de latón al centro de la inscripción.
        val sc = Offset(w / 2, (marco.top + ni.top) / 2)
        drawPath(estrella(sc, (ni.top - marco.top) * 0.62f, 8, 0.62f), n(LATON_OSC, o))
        drawPath(estrella(sc, (ni.top - marco.top) * 0.52f, 8, 0.62f), Brush.radialGradient(listOf(n(LATON_LUZ, o), n(LATON, o)), sc, (ni.top - marco.top) * 0.6f))
        drawCircle(n(VERDE, o, 0.4f), (ni.top - marco.top) * 0.12f, sc)

        // Las enjutas (el paño de yeso con su red) dentro del nicho, entre los arcos.
        yeseria(ni, o, p, 4.6f * u)

        // Los cinco arcos de herradura, con sus dovelas, sobre columnillas.
        val cwid = ni.width / 5
        val cw = minOf(2.8f * u, cwid * 0.075f)
        val anillo = 2.4f * u
        val ai = (cwid - cw) / 2 - 0.3f * u
        val rr = ai / 0.906f
        val cy = ni.top + anillo + 0.6f * u + rr
        for (i in 0 until 5) {
            val cx = ni.left + cwid * (i + 0.5f)
            val hueco = arco(cx, cy, rr, ni.bottom)
            drawPath(hueco, nicho(e, o, YESO))
            clipPath(hueco) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (o) 0.2f else 0.1f), Color.Transparent), cy - rr, cy), Offset(cx - rr, cy - rr), Size(rr * 2, rr))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent), cx - ai, cx - ai + 2f * u), Offset(cx - ai, cy), Size(2f * u, ni.bottom - cy))
            }
            dovelas(Offset(cx, cy), rr, rr + anillo, 11, o, p)
        }
        // Las columnillas entre los arcos y las jambas de los extremos.
        val impost = cy + rr * 0.423f
        for (i in 0..5) {
            val x = ni.left + cwid * i
            columna(x.coerceIn(ni.left + cw * 0.9f, ni.right - cw * 0.9f), impost - cw * 0.4f, ni.bottom, cw, o, p)
        }

        // El antepecho de mármol y el zócalo de alicatado.
        val ante = Rect(marco.left - 1f * u, marco.bottom, marco.right + 1f * u, marco.bottom + 2.4f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(ante.left, ante.bottom), Size(ante.width, 1f * u))
        rect(ante, vert(ante, n(MARMOL_LUZ, o), n(MARMOL, o), n(MARMOL_OSC, o)))
        alicatado(Rect(0f, ante.bottom + 0.4f * u, w, suelo), o, p, 2)
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(MARMOL_OSC, o), mez(n(MARMOL_OSC, o), Color.Black, 0.25f)))
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val o = oscuro
        val c = Offset(w / 2, h / 2)
        val s = minOf(h / 76f, w / 150f)
        val p = density * 0.8f
        when (id) {
            "unesco" -> {
                // La Alhambra: la colina, las murallas de almagre con sus torres, la de Comares, cipreses y Sierra Nevada detrás.
                val base = c.y + 30f * s
                val sierra = Path().apply {
                    moveTo(c.x - 62f * s, c.y + 4f * s); lineTo(c.x - 38f * s, c.y - 22f * s); lineTo(c.x - 22f * s, c.y - 10f * s); lineTo(c.x - 4f * s, c.y - 30f * s)
                    lineTo(c.x + 18f * s, c.y - 12f * s); lineTo(c.x + 36f * s, c.y - 24f * s); lineTo(c.x + 62f * s, c.y + 4f * s); close()
                }
                val roca = if (o) Color(0xFF5E7088) else Color(0xFF8FA3BC)
                drawPath(sierra, Brush.verticalGradient(listOf(mez(roca, Color.White, 0.15f), mez(roca, e.fondo, 0.5f)), c.y - 30f * s, c.y + 4f * s))
                listOf(Offset(-38f, -22f), Offset(-4f, -30f), Offset(36f, -24f)).forEach { k ->
                    drawPath(Path().apply { moveTo(c.x + (k.x - 7f) * s, c.y + (k.y + 7f) * s); lineTo(c.x + k.x * s, c.y + k.y * s); lineTo(c.x + (k.x + 7f) * s, c.y + (k.y + 7f) * s); lineTo(c.x + (k.x + 2f) * s, c.y + (k.y + 5f) * s); close() }, Color.White.copy(alpha = if (o) 0.7f else 0.95f))
                }
                val colina = Path().apply { moveTo(c.x - 70f * s, base); quadraticTo(c.x - 40f * s, c.y + 8f * s, c.x, c.y + 10f * s); quadraticTo(c.x + 40f * s, c.y + 8f * s, c.x + 70f * s, base); close() }
                drawPath(colina, Brush.verticalGradient(listOf(Color(0xFF6E8A4A), Color(0xFF3E5A2A)), c.y + 8f * s, base))
                val roja = if (o) Color(0xFFB0604A) else Color(0xFFC0623E)
                val roja2 = mez(roja, Color.Black, 0.25f)
                // La muralla y las torres.
                val mu = Rect(c.x - 46f * s, c.y - 2f * s, c.x + 46f * s, c.y + 14f * s)
                rect(mu, vert(mu, roja, roja2))
                listOf(-44f to 14f, -24f to 12f, 22f to 12f, 40f to 16f).forEach { (x, an) ->
                    val to = Rect(c.x + x * s - an * s / 2, c.y - 10f * s, c.x + x * s + an * s / 2, c.y + 14f * s)
                    rect(to, horiz(to, mez(roja, Color.White, 0.15f), roja, roja2))
                    var xm = to.left; while (xm < to.right - 1f) { rect(Rect(xm, to.top - 3f * s, xm + 2.6f * s, to.top), roja); xm += 4.4f * s }
                    rect(Rect(to.center.x - 1.2f * s, to.top + 5f * s, to.center.x + 1.2f * s, to.top + 10f * s), mez(roja, Color.Black, 0.5f))
                }
                // La torre de Comares, la más alta, con sus ventanas geminadas.
                val co = Rect(c.x - 12f * s, c.y - 30f * s, c.x + 12f * s, c.y + 14f * s)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(co.right, co.top + 3f * s), Size(3f * s, co.height - 3f * s))
                rect(co, horiz(co, mez(roja, Color.White, 0.2f), roja, roja2))
                var xm = co.left; while (xm < co.right - 1f) { rect(Rect(xm, co.top - 4f * s, xm + 3f * s, co.top), roja); xm += 5f * s }
                listOf(-4.5f, 4.5f).forEach { dx -> drawPath(arco(c.x + dx * s, c.y - 18f * s, 2.6f * s, c.y - 10f * s), mez(roja, Color.Black, 0.55f)) }
                drawLine(e.c1, Offset(co.left, co.top + 4f * s), Offset(co.right, co.top + 4f * s), p)
                // Los cipreses.
                listOf(-56f, -51f, 54f).forEach { x ->
                    drawPath(Path().apply { moveTo(c.x + x * s, c.y - 6f * s); quadraticTo(c.x + (x + 4f) * s, c.y + 8f * s, c.x + x * s, c.y + 22f * s); quadraticTo(c.x + (x - 4f) * s, c.y + 8f * s, c.x + x * s, c.y - 6f * s); close() }, Color(0xFF2E4A26))
                }
                drawLine(e.c1, Offset(c.x - 70f * s, base), Offset(c.x + 70f * s, base), p * 1.4f)
            }
            "rascar" -> {
                // Una vitela en su marco de cedro, cubierta de pan de oro que un dinar va rascando desde una esquina.
                val m = Rect(c.x - 46f * s, c.y - 30f * s, c.x + 30f * s, c.y + 30f * s)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(m.left + 2f * s, m.top + 3f * s), m.size)
                rect(m, Brush.linearGradient(listOf(CEDRO_LUZ, CEDRO, CEDRO_OSC), m.topLeft, m.bottomRight))
                val v = m.deflate(5f * s)
                rect(v, Color(0xFFEBDDB8))
                // El mapa que asoma: costa, río y una estrella.
                drawPath(Path().apply { moveTo(v.left, v.top + v.height * 0.6f); quadraticTo(v.left + v.width * 0.3f, v.top + v.height * 0.3f, v.left + v.width * 0.55f, v.top + v.height * 0.55f); quadraticTo(v.left + v.width * 0.8f, v.top + v.height * 0.8f, v.right, v.top + v.height * 0.5f) }, e.c2, style = Stroke(p * 1.4f))
                drawPath(estrella(Offset(v.left + v.width * 0.68f, v.top + v.height * 0.3f), 5f * s, 8, 0.55f), e.c1)
                // El oro que falta rascar: casi todo, menos la esquina de arriba a la derecha.
                val oro = Path().apply { moveTo(v.left, v.top); lineTo(v.left + v.width * 0.5f, v.top); quadraticTo(v.left + v.width * 0.65f, v.top + v.height * 0.4f, v.right, v.top + v.height * 0.62f); lineTo(v.right, v.bottom); lineTo(v.left, v.bottom); close() }
                drawPath(oro, Brush.linearGradient(listOf(Color(0xFFE8C46A), Color(0xFFC9A04A), Color(0xFFF2D88E), Color(0xFFB08A3A)), v.topLeft, v.bottomRight))
                for (k in 0 until 5) drawLine(Color.White.copy(alpha = 0.35f), Offset(v.left + v.width * (0.1f + 0.12f * k), v.bottom - 2f * s), Offset(v.left + v.width * (0.28f + 0.12f * k), v.top + v.height * 0.4f), p * 0.6f)
                drawPath(Path().apply { moveTo(v.left + v.width * 0.5f, v.top); quadraticTo(v.left + v.width * 0.65f, v.top + v.height * 0.4f, v.right, v.top + v.height * 0.62f) }, Color(0xFF8A6A2A), style = Stroke(p * 0.8f))
                listOf(m.topLeft, Offset(m.right, m.top), Offset(m.left, m.bottom), m.bottomRight).forEach { k -> drawPath(estrella(k, 4.5f * s, 8, 0.6f), LATON); drawCircle(LATON_OSC, 1.2f * s, k) }
                // El dinar de oro, con su orla y las líneas cúficas.
                val d = Offset(c.x + 38f * s, c.y + 4f * s)
                val rd = 20f * s
                drawCircle(Color.Black.copy(alpha = 0.3f), rd, Offset(d.x + 2f * s, d.y + 3f * s))
                drawCircle(Brush.radialGradient(listOf(Color(0xFFF6DC8E), Color(0xFFD4A63E), Color(0xFF8A6420)), Offset(d.x - rd * 0.35f, d.y - rd * 0.35f), rd * 1.5f), rd, d)
                drawCircle(Color(0xFF8A6420), rd * 0.82f, d, style = Stroke(p * 0.8f))
                drawCircle(Color(0xFF8A6420), rd * 0.92f, d, style = Stroke(p * 0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(p * 1.2f, p * 1.2f))))
                for (k in -1..1) {
                    val y = d.y + k * rd * 0.3f
                    drawLine(Color(0xFF7A5418), Offset(d.x - rd * 0.5f, y), Offset(d.x + rd * 0.5f, y), p * 0.8f)
                    for (j in 0 until 4) drawLine(Color(0xFF7A5418), Offset(d.x - rd * 0.42f + j * rd * 0.28f, y), Offset(d.x - rd * 0.42f + j * rd * 0.28f, y - rd * (0.12f + 0.06f * ((j + k + 3) % 2))), p * 0.7f)
                }
                drawArc(Color.White.copy(alpha = 0.5f), 200f, 70f, false, Offset(d.x - rd * 0.75f, d.y - rd * 0.75f), Size(rd * 1.5f, rd * 1.5f), style = Stroke(p))
            }
            else -> {
                // Una vitela con tres alfileres de azulejo de ocho puntas y el camino punteado que los une.
                val v = Rect(c.x - 58f * s, c.y - 26f * s, c.x + 58f * s, c.y + 30f * s)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(v.left + 2f * s, v.top + 3f * s), v.size)
                val vit = if (o) Color(0xFFCDBB92) else Color(0xFFEBDDB8)
                rect(v, vert(v, mez(vit, Color.White, 0.2f), vit, mez(vit, Color.Black, 0.1f)))
                drawPath(Path().apply { moveTo(v.left, v.top + v.height * 0.35f); quadraticTo(v.left + v.width * 0.2f, v.top + v.height * 0.5f, v.left + v.width * 0.18f, v.bottom); lineTo(v.left, v.bottom); close() }, mez(e.c2, vit, 0.55f))
                taracea(Rect(v.left, v.top - 4f * s, v.right, v.top), o, p * 0.6f)
                taracea(Rect(v.left, v.bottom, v.right, v.bottom + 4f * s), o, p * 0.6f)
                val pts = listOf(Offset(c.x - 34f * s, c.y + 10f * s), Offset(c.x + 4f * s, c.y - 8f * s), Offset(c.x + 38f * s, c.y + 12f * s))
                drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - 18f * s, c.y - 16f * s, pts[1].x, pts[1].y); quadraticTo(c.x + 26f * s, c.y - 6f * s, pts[2].x, pts[2].y) },
                    Color(0xFF5A3A1E), style = Stroke(p, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f * s, 2.4f * s))))
                val cols = listOf(e.c1, e.c2, MELADO)
                pts.forEachIndexed { k, q ->
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(q.x - 4f * s, q.y + 8f * s), Size(8f * s, 3f * s))
                    drawLine(Color(0xFF6E6A62), q, Offset(q.x, q.y + 9f * s), p * 1.2f)
                    val st = Offset(q.x, q.y - 3f * s)
                    drawPath(estrella(st, 10f * s, 8, 0.62f), mez(cols[k], Color.Black, 0.35f))
                    drawPath(estrella(st, 8.6f * s, 8, 0.62f), Brush.radialGradient(listOf(mez(cols[k], Color.White, 0.35f), cols[k]), Offset(st.x - 3f * s, st.y - 3f * s), 10f * s))
                    drawCircle(MARFIL, 2.6f * s, st)
                    drawCircle(Color.White.copy(alpha = 0.6f), 1.2f * s, Offset(st.x - 3f * s, st.y - 3.4f * s))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.NOTAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val t = 2.4f * u
        val marco = ni.inflate(t)

        // El muro de yesería, el alero y los mocárabes.
        yeseria(Rect(0f, 0f, w, h), o, p, 7f * u)
        val aleroB = h * 0.042f; val canB = h * 0.072f
        mocarabes(Rect(0f, canB, w, marco.top - 0.6f * u), o, p)
        alero(Rect(0f, 0f, w, aleroB), Rect(0f, aleroB, w, canB), o, p, u, e.semilla)

        // Las columnillas a los lados, con un candil en su ménsula.
        val zonaLado = marco.left
        val cw = minOf(zonaLado * 0.28f, 2.6f * u)
        val zocTop = marco.bottom + 1.6f * u
        listOf(zonaLado / 2, w - zonaLado / 2).forEachIndexed { k, x ->
            columna(x, marco.top + 2f * u, zocTop, cw, o, p)
            val my = ni.top + ni.height * 0.62f
            val men = Rect(x - zonaLado * 0.38f, my, x + zonaLado * 0.38f, my + 1.4f * u)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(men.left, men.bottom), Size(men.width, 0.8f * u))
            rect(men, vert(men, n(CEDRO_LUZ, o), n(CEDRO_OSC, o)))
            drawPath(Path().apply { moveTo(men.left + men.width * 0.2f, men.bottom); lineTo(men.right - men.width * 0.2f, men.bottom); lineTo(x, men.bottom + 2.6f * u); close() }, n(CEDRO_OSC, o))
            candil(Offset(x, men.top), minOf(zonaLado * 0.85f, 7f * u), if (k == 0) 1f else -1f, o, p)
        }

        // El marco de cedro taraceado, con cantoneras de latón y clavos.
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(marco.left + 0.8f * u, marco.top + 1.3f * u), marco.size)
        rect(marco, Brush.linearGradient(listOf(n(CEDRO_LUZ, o), n(CEDRO, o), n(CEDRO_OSC, o)), marco.topLeft, marco.bottomRight))
        vetas(Rect(marco.left, marco.top, marco.right, ni.top), n(VETA, o).copy(alpha = 0.5f), e.semilla, p * 0.7f)
        vetas(Rect(marco.left, ni.bottom, marco.right, marco.bottom), n(VETA, o).copy(alpha = 0.5f), e.semilla + 1, p * 0.7f)
        taracea(Rect(marco.left + t * 1.2f, marco.top + t * 0.18f, marco.right - t * 1.2f, ni.top - t * 0.18f), o, p)
        taracea(Rect(marco.left + t * 1.2f, ni.bottom + t * 0.18f, marco.right - t * 1.2f, marco.bottom - t * 0.18f), o, p)
        var yc = ni.top + 4f * u
        while (yc < ni.bottom - 2f * u) { listOf(marco.left + t / 2, marco.right - t / 2).forEach { x -> punto(n(LATON_OSC, o), p * 1.2f, x, yc); punto(n(LATON_LUZ, o), p * 0.6f, x - p * 0.3f, yc - p * 0.3f) }; yc += 6f * u }
        // El yeso liso del tablero: el nicho.
        rect(ni.inflate(0.5f * u), n(CEDRO_OSC, o))
        rect(ni, nicho(e, o, YESO))
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 3f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent), ni.top, ni.top + 3f * u))
        rect(Rect(ni.left, ni.top, ni.left + 2f * u, ni.bottom), Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent), ni.left, ni.left + 2f * u))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { c ->
            val sx = if (c.x < w / 2) 1f else -1f; val sy = if (c.y < h / 2) 1f else -1f
            val q = Path().apply { moveTo(c.x, c.y); lineTo(c.x + sx * t * 2.2f, c.y); lineTo(c.x + sx * t * 1.4f, c.y + sy * t * 0.9f); lineTo(c.x + sx * t * 0.9f, c.y + sy * t * 1.4f); lineTo(c.x, c.y + sy * t * 2.2f); close() }
            drawPath(q, Brush.radialGradient(listOf(n(LATON_LUZ, o), n(LATON, o), n(LATON_OSC, o)), c, t * 2.4f))
            drawCircle(n(LATON_OSC, o), p * 0.9f, Offset(c.x + sx * t * 0.6f, c.y + sy * t * 0.6f))
        }

        // El zócalo de alicatado bajo el marco.
        alicatado(Rect(0f, zocTop, w, h), o, p, 2)
    }

    private fun DrawScope.punto(c: Color, r: Float, x: Float, y: Float) = drawCircle(c, r, Offset(x, y))

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val semilla = e.indice * 131 + e.tipo.length * 7
        val pliego = papel(r.deflate(2f * un), semilla)
        translate(1.2f * un, 2.2f * un) { drawPath(pliego, Color.Black.copy(alpha = 0.3f)) }
        val pap = if (o) PAPEL_OSCURO else PAPEL_CLARO
        drawPath(pliego, Brush.linearGradient(listOf(mez(pap, Color.White, if (o) 0.04f else 0.25f), pap, mez(pap, Color.Black, 0.07f)), r.topLeft, r.bottomRight))
        clipPath(pliego) {
            // Las líneas de la forma (verjura) del papel hecho a mano.
            var y = r.top + 3f * un
            while (y < r.bottom) { drawLine(mez(pap, if (o) Color.White else Color(0xFF8A6A3A), 0.08f), Offset(r.left, y), Offset(r.right, y), 0.4f * un); y += 2.2f * un }
            listOf(0.3f, 0.7f).forEach { f -> drawLine(mez(pap, if (o) Color.White else Color(0xFF8A6A3A), 0.1f), Offset(r.left + r.width * f, r.top), Offset(r.left + r.width * f, r.bottom), 0.6f * un) }
        }
        drawPath(pliego, mez(pap, Color.Black, 0.3f), style = Stroke(0.6f * un, join = StrokeJoin.Round))

        // La cabecera iluminada, del color del tipo: una franja con estrellas sobre el texto.
        val tipo = when (e.tipo) {
            "idea" -> MELADO
            "pensamiento" -> AZUL
            "proposito" -> VERDE
            else -> ALMAGRA
        }
        val tc = if (o) mez(tipo, Color.White, 0.25f) else tipo
        val cab = Rect(r.left + r.width * 0.12f, r.top + r.height * 0.075f, r.right - r.width * 0.12f, r.top + r.height * 0.125f)
        rect(cab, tc)
        val ns = (cab.width / (cab.height * 1.5f)).toInt().coerceAtLeast(3)
        for (k in 0 until ns) drawPath(estrella(Offset(cab.left + cab.width * (k + 0.5f) / ns, cab.center.y), cab.height * 0.36f, 8, 0.55f), if (o) Color(0xFF2A2318) else PAPEL_CLARO)
        drawLine(if (o) Color(0xFFE0B65A) else Color(0xFFB08A3A), Offset(cab.left, cab.bottom + 0.6f * un), Offset(cab.right, cab.bottom + 0.6f * un), 0.5f * un)

        // El sello del tipo abajo a la derecha: una estrella de ocho puntas en su círculo.
        val q = Offset(r.right - r.width * 0.11f, r.bottom - r.height * 0.085f)
        val rs = r.height * 0.045f
        drawCircle(tc, rs * 1.25f, q, style = Stroke(0.5f * un))
        drawPath(estrella(q, rs, 8, 0.6f), tc)
        // El recordatorio lleva la esquina de abajo a la izquierda doblada.
        if (e.tipo == "recordatorio") {
            val d = 9f * un
            val bl = Offset(r.left + 2f * un, r.bottom - 2f * un)
            drawPath(Path().apply { moveTo(bl.x, bl.y - d); lineTo(bl.x + d, bl.y); lineTo(bl.x, bl.y); close() }, if (o) Color(0xFF1C1A15) else Color(0xFFD9CBA8))
            drawPath(Path().apply { moveTo(bl.x, bl.y - d); lineTo(bl.x + d, bl.y); lineTo(bl.x + d * 0.85f, bl.y - d * 0.85f); close() }, mez(pap, Color.Black, 0.14f))
        }

        // El clavo arriba al centro; la fijada lleva clavo de estrella de latón y una borla de seda.
        val cl = Offset(r.center.x, r.top + r.height * 0.035f)
        if (e.fijada) {
            val bor = if (o) mez(e.acento, Color.White, 0.1f) else e.acento
            drawLine(Color(0xFF8A6A2A), cl, Offset(r.right - r.width * 0.06f, r.top + r.height * 0.2f), 0.6f * un)
            val bt = Offset(r.right - r.width * 0.06f, r.top + r.height * 0.2f)
            drawCircle(Color(0xFFC9A04A), 1.4f * un, bt)
            drawPath(Path().apply { moveTo(bt.x - 1.6f * un, bt.y + 1f * un); lineTo(bt.x + 1.6f * un, bt.y + 1f * un); lineTo(bt.x + 2.4f * un, bt.y + 9f * un); lineTo(bt.x - 2.4f * un, bt.y + 9f * un); close() }, bor)
            for (k in -1..1) drawLine(mez(bor, Color.Black, 0.3f), Offset(bt.x + k * 1.1f * un, bt.y + 2f * un), Offset(bt.x + k * 1.6f * un, bt.y + 9f * un), 0.4f * un)
            drawCircle(Color.Black.copy(alpha = 0.35f), 4.4f * un, Offset(cl.x + 0.8f * un, cl.y + 1.2f * un))
            drawPath(estrella(cl, 4.6f * un, 8, 0.62f), Brush.radialGradient(listOf(LATON_LUZ, LATON, LATON_OSC), Offset(cl.x - 1.4f * un, cl.y - 1.4f * un), 5f * un))
            drawCircle(VERDE, 1.2f * un, cl)
        } else {
            drawCircle(Color.Black.copy(alpha = 0.35f), 1.8f * un, Offset(cl.x + 0.6f * un, cl.y + 0.9f * un))
            drawCircle(Brush.radialGradient(listOf(LATON_LUZ, LATON, LATON_OSC), Offset(cl.x - 0.6f * un, cl.y - 0.6f * un), 2.2f * un), 1.8f * un, cl)
        }
    }

    /** Un pliego de papel de Xàtiva: casi rectangular, con los bordes de barbas (algo irregulares). */
    private fun papel(r: Rect, semilla: Int): Path {
        val rnd = Random(semilla.toLong() * 7919L + 17L)
        val path = Path()
        val pasos = 9
        path.moveTo(r.left, r.top)
        for (k in 1..pasos) path.lineTo(r.left + r.width * k / pasos, r.top + r.height * 0.008f * rnd.nextFloat())
        for (k in 1..pasos) path.lineTo(r.right - r.width * 0.01f * rnd.nextFloat(), r.top + r.height * k / pasos)
        for (k in 1..pasos) path.lineTo(r.right - r.width * k / pasos, r.bottom - r.height * 0.01f * rnd.nextFloat())
        for (k in 1 until pasos) path.lineTo(r.left + r.width * 0.008f * rnd.nextFloat(), r.bottom - r.height * k / pasos)
        path.close()
        return path
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E6CC) else Color(0xFF2A1A12)
}
