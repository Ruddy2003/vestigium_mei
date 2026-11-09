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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Egipto: la Casa de la Vida, con los materiales de su biblioteca, su Ideario y su sala de ofrendas: estuco y arenisca,
 * cedro con taracea de ébano y marfil, fayenza, lapislázuli, cornalina, oro y lino.
 * - Social: la sala del banquete. Arriba, la cornisa de gola con sus franjas de colores, el toro atado y el disco solar
 *   alado; a los lados, columnas papiriformes; al centro, el panel de estuco enmarcado por un toro atado, donde va tu
 *   gente. Abajo, frente al zócalo pintado, dos sillas de patas de león con su cojín, las jarras de vino en su soporte,
 *   las lámparas de pie encendidas y la mesa de ofrendas con panes, higos y lotos.
 * - Mapas: la mesa del agrimensor: tablero de cedro con orla de ébano y marfil, el papiro desenrollado con su rollo, la
 *   paleta de escriba con sus dos tintas y sus cálamos, el vaso de agua de fayenza, la cuerda de medir enrollada, la
 *   lámpara encendida y el codo real; al frente, el faldón con su orla y las patas de león sobre tambores.
 * - Otras vidas: el cofre de las joyas: friso de ureos, gola con franjas y disco alado, toro atado, pilares djed de oro a
 *   los lados, cinco compartimentos forrados de lino separados por listones de oro y ébano, y la base con su taracea y
 *   sus patas.
 * - Íconos: las pirámides de Guiza con su palmera, una tablilla de lapislázuli cubierta de pan de oro que un escarabeo
 *   raspa, y un trozo de papiro con el Nilo y tres obeliscos.
 * - Notas: una estela de arenisca con gola, disco alado y toro atado; el muro enlucido; abajo, el zócalo de colores y la
 *   repisa con la paleta. Las notas son óstraca de caliza (ideas y pensamientos) y hojas de papiro (propósitos y
 *   recordatorios), con su rúbrica roja y, si están fijadas, un sello de barro atado con lino.
 */
internal object VidaEgipto : DisenoVida {
    private val CEDRO = Color(0xFF8E4E2C)
    private val CEDRO_LUZ = Color(0xFFB4703F)
    private val CEDRO_OSC = Color(0xFF55301A)
    private val LAPIS = Color(0xFF1D2F7A)
    private val FAYENZA = Color(0xFF2C8C9C)
    private val ORO = Color(0xFFD4AF37)
    private val ORO_LUZ = Color(0xFFF3D77A)
    private val ORO_OSC = Color(0xFF8A6A1C)
    private val CORNALINA = Color(0xFFB8471E)
    private val VERDE = Color(0xFF3E7A4A)
    private val MURO = Color(0xFFE3CFA0)
    private val PIEDRA = Color(0xFFCDB98E)
    private val PIEDRA_LUZ = Color(0xFFE8DAB4)
    private val PIEDRA_OSC = Color(0xFF9A835A)
    private val EBANO = Color(0xFF1E1610)
    private val MARFIL = Color(0xFFF0E6CC)
    private val LINO = Color(0xFFE6D8B4)
    private val LINO_OSC = Color(0xFFBFAD84)
    private val BARRO = Color(0xFF6E4A2E)
    private val TERRACOTA = Color(0xFFB0643A)
    private val ESTUCO = Color(0xFFF6F0E0)
    private val PAPIRO = Color(0xFFE9D9A6)
    private val ALABASTRO = Color(0xFFEDE3CC)
    private val HIGO = Color(0xFF5A2E4A)
    private val PAN = Color(0xFFCF9550)
    private val LLAMA = Color(0xFFFFC870)
    private val NOCHE = Color(0xFF0E0A06)

    /** Las notas: caliza y papiro claros de día; de noche, caliza y papiro en sombra (con la tinta clara de [tintaNota]). */
    private val CALIZA_CLARA = Color(0xFFF1E9D6)
    private val CALIZA_OSCURA = Color(0xFF3B342B)
    private val PAPIRO_CLARO = Color(0xFFECDCAC)
    private val PAPIRO_OSCURO = Color(0xFF3D2F1C)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.13f, 0.20f, 0.87f, 0.775f)
        MuebleVida.MAPAS -> Rect(0.16f, 0.10f, 0.86f, 0.71f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.075f, 0.25f, 0.925f, 0.82f)
        MuebleVida.NOTAS -> Rect(0.065f, 0.135f, 0.935f, 0.885f)
    }

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material de noche: el mismo, en penumbra. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.58f) else c

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mez(e.fondo, mez(material, NOCHE, 0.8f), 0.45f) else mez(e.fondo, material, 0.35f)

    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)
    private fun DrawScope.rect(r: Rect, b: Brush) = drawRect(b, r.topLeft, r.size)
    private fun vert(r: Rect, c: List<Color>) = Brush.verticalGradient(c, r.top, r.bottom)
    private fun vert(r: Rect, a: Color, b: Color) = vert(r, listOf(a, b))
    private fun vert(r: Rect, a: Color, b: Color, c: Color) = vert(r, listOf(a, b, c))
    private fun horiz(r: Rect, c: List<Color>) = Brush.horizontalGradient(c, r.left, r.right)
    private fun horiz(r: Rect, a: Color, b: Color) = horiz(r, listOf(a, b))
    private fun horiz(r: Rect, a: Color, b: Color, c: Color) = horiz(r, listOf(a, b, c))
    private fun horiz(r: Rect, a: Color, b: Color, c: Color, d: Color) = horiz(r, listOf(a, b, c, d))

    /** Un número estable de 0 a 1 (la misma irregularidad cada vez). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Una mancha radial suave (luz de lámpara, humedad del estuco). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Vetas de madera en [r]: líneas onduladas a lo largo, de grosor [p] (≈ 1 dp). */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, p: Float, densidad: Float = 1f, vertical: Boolean = false) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        val n = (ancho / (3.2f * p) * densidad).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
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

    /** El friso jekeru de una gola: franjas de lapislázuli, cornalina y verde con filetes de oro y una fila de cuentas. */
    private fun DrawScope.franjas(r: Rect, ancho: Float, o: Boolean, p: Float) {
        val n = (r.width / ancho).toInt().coerceAtLeast(6)
        val colores = listOf(LAPIS, CORNALINA, VERDE, CORNALINA)
        val paso = r.width / n
        for (k in 0 until n) {
            val x = r.left + paso * k
            rect(Rect(x, r.top, x + paso, r.bottom), n(colores[k % 4], o))
            drawLine(n(ORO, o), Offset(x, r.top), Offset(x, r.bottom), p * 0.5f)
            drawCircle(n(ORO, o), paso * 0.22f, Offset(x + paso / 2, r.top + r.height * 0.24f))
        }
    }

    /** Una gola (la cornisa cóncava) en [r], con su filete arriba, las franjas y la sombra del vuelo. */
    private fun DrawScope.gola(r: Rect, o: Boolean, p: Float, u: Float) {
        val d = 1.8f * u
        val path = Path().apply {
            moveTo(r.left + d, r.bottom); lineTo(r.right - d, r.bottom)
            quadraticTo(r.right - d * 0.6f, r.top + r.height * 0.35f, r.right, r.top); lineTo(r.left, r.top)
            quadraticTo(r.left + d * 0.6f, r.top + r.height * 0.35f, r.left + d, r.bottom); close()
        }
        drawPath(path, n(PIEDRA, o))
        clipPath(path) {
            franjas(r, 2.6f * u, o, p)
            rect(r, Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent, Color.White.copy(alpha = 0.08f)), r.top, r.bottom))
        }
        val filete = Rect(r.left - 0.5f * u, r.top - r.height * 0.14f, r.right + 0.5f * u, r.top)
        rect(filete, vert(filete, n(PIEDRA_LUZ, o), n(PIEDRA_OSC, o)))
    }

    /** Un toro atado: la moldura redonda envuelta en cintas de colores; [vertical] si corre de arriba abajo. */
    private fun DrawScope.toro(r: Rect, vertical: Boolean, o: Boolean, p: Float) {
        val g = if (vertical) Brush.horizontalGradient(listOf(n(PIEDRA_OSC, o), n(PIEDRA_LUZ, o), n(PIEDRA, o), n(PIEDRA_OSC, o)), r.left, r.right)
        else Brush.verticalGradient(listOf(n(PIEDRA_LUZ, o), n(PIEDRA, o), n(PIEDRA_OSC, o)), r.top, r.bottom)
        rect(r, g)
        val grueso = if (vertical) r.width else r.height
        val largo = if (vertical) r.height else r.width
        clipRect(r.left, r.top, r.right, r.bottom) {
            var t = -grueso
            var k = 0
            while (t < largo + grueso) {
                val col = n(listOf(LAPIS, CORNALINA, LAPIS, VERDE)[k % 4], o)
                if (vertical) drawLine(col, Offset(r.left, r.top + t), Offset(r.right, r.top + t + grueso * 0.8f), grueso * 0.32f)
                else drawLine(col, Offset(r.left + t, r.bottom), Offset(r.left + t + grueso * 0.8f, r.top), grueso * 0.32f)
                t += grueso * 0.95f; k++
            }
            if (vertical) rect(r, Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.2f), Color.White.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.25f)), r.left, r.right))
            else rect(r, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), r.top, r.bottom))
        }
    }

    /** El disco solar alado con sus dos ureos: tres hileras de plumas a cada lado. */
    private fun DrawScope.solAlado(sol: Offset, rs: Float, o: Boolean, p: Float) {
        listOf(-1f, 1f).forEach { s ->
            for (k in 0 until 3) {
                val ala = Path().apply {
                    moveTo(sol.x + s * rs * 0.8f, sol.y - rs * 0.5f + k * rs * 0.35f)
                    quadraticTo(sol.x + s * rs * 3.5f, sol.y - rs * (1.1f - k * 0.3f), sol.x + s * rs * (5.2f - k * 0.6f), sol.y - rs * (0.4f - k * 0.3f))
                    lineTo(sol.x + s * rs * (5f - k * 0.6f), sol.y + rs * (0.05f + k * 0.25f))
                    quadraticTo(sol.x + s * rs * 3f, sol.y - rs * (0.1f - k * 0.3f), sol.x + s * rs * 0.8f, sol.y + rs * 0.2f + k * rs * 0.3f); close()
                }
                drawPath(ala, n(listOf(LAPIS, FAYENZA, VERDE)[k], o))
                drawPath(ala, n(ORO, o), style = Stroke(p * 0.6f))
            }
            val ureo = Path().apply { moveTo(sol.x + s * rs * 0.7f, sol.y + rs * 0.9f); quadraticTo(sol.x + s * rs * 1.3f, sol.y + rs * 0.2f, sol.x + s * rs * 1.0f, sol.y - rs * 0.4f) }
            drawPath(ureo, n(ORO, o), style = Stroke(rs * 0.22f, cap = StrokeCap.Round))
            drawCircle(n(CORNALINA, o), rs * 0.18f, Offset(sol.x + s * rs * 1.0f, sol.y - rs * 0.55f))
        }
        drawCircle(Brush.radialGradient(listOf(mez(n(CORNALINA, o), Color.White, 0.3f), n(CORNALINA, o)), Offset(sol.x - rs * 0.3f, sol.y - rs * 0.3f), rs * 1.2f), rs, sol)
        drawCircle(n(ORO, o), rs, sol, style = Stroke(p * 0.9f))
    }

    /** El zócalo pintado: piedra con una banda de rectángulos de colores entre filetes, y la parte baja más oscura. */
    private fun DrawScope.zocalo(r: Rect, o: Boolean, p: Float, u: Float) {
        rect(r, vert(r, n(PIEDRA, o), n(PIEDRA_OSC, o)))
        val b = Rect(r.left, r.top + r.height * 0.22f, r.right, r.top + r.height * 0.52f)
        rect(b, n(MARFIL, o))
        val nz = (r.width / (3.2f * u)).toInt().coerceAtLeast(8)
        for (k in 0 until nz) rect(Rect(r.left + r.width * k / nz + 0.35f * u, b.top + b.height * 0.18f, r.left + r.width * (k + 1) / nz - 0.35f * u, b.bottom - b.height * 0.18f), n(listOf(LAPIS, CORNALINA, VERDE, CORNALINA)[k % 4], o))
        drawLine(n(EBANO, o), Offset(r.left, b.top), Offset(r.right, b.top), p * 0.6f)
        drawLine(n(EBANO, o), Offset(r.left, b.bottom), Offset(r.right, b.bottom), p * 0.6f)
        drawLine(n(PIEDRA_LUZ, o), Offset(r.left, r.top + p * 0.4f), Offset(r.right, r.top + p * 0.4f), p * 0.6f)
    }

    /** Una llama de lámpara con su halo; de noche el halo es grande. */
    private fun DrawScope.llama(o: Offset, r: Float, oscuro: Boolean) {
        mancha(o, r * (if (oscuro) 7f else 3f), LLAMA.copy(alpha = if (oscuro) 0.38f else 0.22f))
        drawPath(Path().apply {
            moveTo(o.x, o.y - r * 1.6f); quadraticTo(o.x + r * 0.9f, o.y - r * 0.2f, o.x, o.y + r * 0.5f); quadraticTo(o.x - r * 0.9f, o.y - r * 0.2f, o.x, o.y - r * 1.6f); close()
        }, Color(0xFFFFA733))
        drawOval(Color(0xFFFFF1C2), Offset(o.x - r * 0.3f, o.y - r * 0.6f), Size(r * 0.6f, r * 0.95f))
    }

    /** Un loto abierto de perfil: tres pétalos y dos sépalos verdes. */
    private fun DrawScope.loto(c: Offset, r: Float, flor: Color, caliz: Color) {
        listOf(-0.5f, 0f, 0.5f).forEach { a ->
            val tip = Offset(c.x + a * r * 1.2f, c.y - r * (1.1f - kotlin.math.abs(a) * 0.4f))
            drawPath(Path().apply { moveTo(c.x - r * 0.22f, c.y); quadraticTo(tip.x - r * 0.35f, (c.y + tip.y) / 2, tip.x, tip.y); quadraticTo(tip.x + r * 0.35f, (c.y + tip.y) / 2, c.x + r * 0.22f, c.y); close() }, flor)
        }
        drawPath(Path().apply { moveTo(c.x - r * 0.55f, c.y - r * 0.5f); lineTo(c.x, c.y + r * 0.12f); lineTo(c.x + r * 0.55f, c.y - r * 0.5f); lineTo(c.x, c.y - r * 0.15f); close() }, caliz)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.SOCIAL)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val golaTop = h * 0.035f; val golaBajo = h * 0.135f; val toroBajo = h * 0.165f
        val suelo = h * 0.962f

        // El muro de arenisca enlucido, con sus hiladas, y el zócalo de colores detrás de los muebles.
        val muro = n(MURO, o)
        rect(Rect(0f, golaTop, w, suelo), vert(Rect(0f, golaTop, w, suelo), mez(muro, Color.Black, 0.18f), muro))
        val hil = 9f * u
        var yh = toroBajo + hil
        var fila = 0
        while (yh < suelo) {
            drawLine(mez(muro, Color.Black, 0.12f), Offset(0f, yh), Offset(w, yh), p * 0.5f)
            var xh = if (fila % 2 == 0) 0f else 9f * u
            while (xh < w) { drawLine(mez(muro, Color.Black, 0.1f), Offset(xh, yh - hil), Offset(xh, yh), p * 0.5f); xh += 18f * u }
            yh += hil; fila++
        }
        val zoc = ni.bottom + 4.5f * u
        zocalo(Rect(0f, zoc, w, suelo), o, p, u)
        // El piso de losas de piedra.
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(PIEDRA, o), n(PIEDRA_OSC, o)))
        drawLine(n(PIEDRA_LUZ, o), Offset(0f, suelo), Offset(w, suelo), p * 0.6f)
        var xj = (w / 2) % (20f * u)
        while (xj < w) { drawLine(n(PIEDRA_OSC, o), Offset(xj, suelo), Offset(xj + (xj - w / 2) * 0.12f, h), p * 0.6f); xj += 20f * u }

        // Las lámparas de pie (su halo cae sobre el muro; el panel lo tapa después).
        val lamX = listOf(w * 0.165f, w * 0.835f)
        val lamY = ni.bottom + 3f * u
        if (o) lamX.forEach { mancha(Offset(it, lamY), 32f * u, LLAMA.copy(alpha = 0.22f)) }

        // El panel de estuco enmarcado por un toro atado: el nicho donde va tu gente.
        val t = 2.4f * u
        val marco = ni.inflate(t)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        toro(Rect(marco.left, marco.top, marco.right, ni.top), false, o, p)
        toro(Rect(marco.left, ni.bottom, marco.right, marco.bottom), false, o, p)
        toro(Rect(marco.left, ni.top, ni.left, ni.bottom), true, o, p)
        toro(Rect(ni.right, ni.top, marco.right, ni.bottom), true, o, p)
        rect(ni, nicho(e, o, ESTUCO))
        val rnd = Random(e.semilla.toLong() * 13 + 5)
        repeat(14) { mancha(Offset(ni.left + ni.width * rnd.nextFloat(), ni.top + ni.height * rnd.nextFloat()), (4f + 8f * rnd.nextFloat()) * u, (if (o) Color.White else PIEDRA_OSC).copy(alpha = 0.04f)) }
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 3f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent), ni.top, ni.top + 3f * u))

        // La cornisa: gola con franjas, el toro atado debajo y el disco solar alado al centro.
        gola(Rect(0f, golaTop, w, golaBajo), o, p, u)
        toro(Rect(0f, golaBajo, w, toroBajo), false, o, p)
        rect(Rect(0f, toroBajo, w, toroBajo + 2f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), toroBajo, toroBajo + 2f * u))
        solAlado(Offset(w / 2, (golaTop + golaBajo) / 2 + 0.6f * u), (golaBajo - golaTop) * 0.24f, o, p)

        // Las columnas papiriformes.
        val cw = minOf(w * 0.048f, 8f * u)
        listOf(w * 0.064f, w * 0.936f).forEach { columnaPapiro(it, toroBajo, suelo, cw, o, p) }

        // Abajo: lámparas de pie, sillas, jarras y la mesa de ofrendas.
        val yT = ni.bottom + 0.8f * u
        val hh = suelo - yT
        lamX.forEach { lamparaPie(it, lamY, suelo, o, p, u) }
        silla(w * 0.255f, suelo, hh * 0.98f, 1f, e, o, p)
        silla(w * 0.745f, suelo, hh * 0.98f, -1f, e, o, p)
        jarra(w * 0.375f, suelo, hh * 0.86f, o, p)
        jarra(w * 0.625f, suelo, hh * 0.86f, o, p)
        mesaOfrendas(w * 0.5f, suelo, hh * 0.96f, minOf(w * 0.12f, hh * 1.4f), o, p)
    }

    /** Una columna papiriforme: basa de disco, fuste de tallos atados, las cinco ataduras y el capitel abierto de papiro. */
    private fun DrawScope.columnaPapiro(xc: Float, top: Float, bottom: Float, cw: Float, o: Boolean, p: Float) {
        val pie = n(PIEDRA, o); val luz = n(PIEDRA_LUZ, o); val osc = n(PIEDRA_OSC, o)
        val capH = cw * 1.25f
        val atad = top + capH
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), xc + cw / 2, xc + cw), Offset(xc + cw / 2, atad), Size(cw / 2, bottom - atad))
        // La basa.
        val basa = Rect(xc - cw * 0.78f, bottom - cw * 0.32f, xc + cw * 0.78f, bottom)
        drawRoundRect(vert(basa, luz, osc), basa.topLeft, basa.size, CornerRadius(cw * 0.12f))
        // El fuste, más angosto al pie, con los tallos.
        val f = Path().apply { moveTo(xc - cw * 0.5f, atad); lineTo(xc + cw * 0.5f, atad); lineTo(xc + cw * 0.5f, bottom - cw * 0.9f); quadraticTo(xc + cw * 0.48f, bottom - cw * 0.4f, xc + cw * 0.34f, basa.top); lineTo(xc - cw * 0.34f, basa.top); quadraticTo(xc - cw * 0.48f, bottom - cw * 0.4f, xc - cw * 0.5f, bottom - cw * 0.9f); close() }
        drawPath(f, Brush.horizontalGradient(listOf(osc, luz, pie, osc), xc - cw / 2, xc + cw / 2))
        for (k in 1 until 4) drawLine(osc.copy(alpha = 0.7f), Offset(xc - cw / 2 + cw * k / 4, atad), Offset(xc - cw * 0.34f + cw * 0.68f * k / 4, basa.top), p * 0.6f)
        // Las hojitas triangulares al pie del fuste.
        for (k in -1..1) drawPath(Path().apply { moveTo(xc + k * cw * 0.24f - cw * 0.12f, basa.top); lineTo(xc + k * cw * 0.24f, basa.top - cw * 0.45f); lineTo(xc + k * cw * 0.24f + cw * 0.12f, basa.top); close() }, n(VERDE, o).copy(alpha = 0.85f))
        // Las ataduras.
        for (k in 0 until 5) rect(Rect(xc - cw * 0.53f, atad + k * cw * 0.12f, xc + cw * 0.53f, atad + (k + 1) * cw * 0.12f), n(listOf(LAPIS, ORO, CORNALINA, ORO, LAPIS)[k], o))
        // El capitel acampanado, con sus sépalos pintados.
        val cap = Path().apply { moveTo(xc - cw * 0.5f, atad); cubicTo(xc - cw * 0.6f, top + capH * 0.5f, xc - cw * 1.0f, top + capH * 0.25f, xc - cw * 1.02f, top + cw * 0.18f); lineTo(xc + cw * 1.02f, top + cw * 0.18f); cubicTo(xc + cw * 1.0f, top + capH * 0.25f, xc + cw * 0.6f, top + capH * 0.5f, xc + cw * 0.5f, atad); close() }
        drawPath(cap, Brush.horizontalGradient(listOf(osc, luz, pie, osc), xc - cw, xc + cw))
        clipPath(cap) {
            for (k in -4..4) {
                val bx = xc + k * cw * 0.24f
                val col = n(if (k % 2 == 0) VERDE else LAPIS, o)
                drawPath(Path().apply { moveTo(xc + k * cw * 0.1f, atad); lineTo(bx - cw * 0.11f, top + cw * 0.2f); lineTo(bx + cw * 0.11f, top + cw * 0.2f); close() }, col.copy(alpha = 0.8f))
            }
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), xc - cw, xc + cw), Offset(xc - cw * 1.1f, top), Size(cw * 2.2f, capH))
        }
        drawLine(n(ORO, o), Offset(xc - cw * 1.02f, top + cw * 0.18f), Offset(xc + cw * 1.02f, top + cw * 0.18f), p * 0.8f)
        rect(Rect(xc - cw * 0.45f, top, xc + cw * 0.45f, top + cw * 0.18f), vert(Rect(0f, top, 0f, top + cw * 0.18f), luz, osc))
    }

    /** Una lámpara de pie: el fuste delgado de madera dorada, el plato y la llama. */
    private fun DrawScope.lamparaPie(x: Float, yLlama: Float, yF: Float, o: Boolean, p: Float, u: Float) {
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(x - 2.6f * u, yF - 0.7f * u), Size(5.2f * u, 1.4f * u))
        drawPath(Path().apply { moveTo(x - 2.2f * u, yF); lineTo(x + 2.2f * u, yF); lineTo(x + 0.5f * u, yF - 2f * u); lineTo(x - 0.5f * u, yF - 2f * u); close() }, n(ORO_OSC, o))
        drawLine(n(ORO, o), Offset(x, yF - 2f * u), Offset(x, yLlama + 1.6f * u), p * 1.3f)
        drawLine(n(ORO_LUZ, o), Offset(x - p * 0.4f, yF - 2f * u), Offset(x - p * 0.4f, yLlama + 1.6f * u), p * 0.4f)
        for (k in 0 until 3) drawOval(n(ORO, o), Offset(x - 0.9f * u, yF - (5f + k * 4f) * u), Size(1.8f * u, 0.9f * u))
        drawPath(Path().apply { moveTo(x - 2.6f * u, yLlama + 0.4f * u); quadraticTo(x, yLlama + 3f * u, x + 2.6f * u, yLlama + 0.4f * u); close() }, Brush.horizontalGradient(listOf(n(ALABASTRO, o), n(PIEDRA_OSC, o)), x - 2.6f * u, x + 2.6f * u))
        llama(Offset(x, yLlama), 0.9f * u, o)
    }

    /** Una silla egipcia de perfil, mirando hacia [sg]: patas de león sobre tambores, asiento con cojín y respaldo inclinado. */
    private fun DrawScope.silla(cx: Float, yF: Float, alto: Float, sg: Float, e: EstadoMueble, o: Boolean, p: Float) {
        val an = alto * 0.95f
        val ced = n(CEDRO, o); val luz = n(CEDRO_LUZ, o); val osc = n(CEDRO_OSC, o)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - an * 0.55f, yF - alto * 0.05f), Size(an * 1.1f, alto * 0.1f))
        val asiento = yF - alto * 0.45f
        val atras = cx - sg * an * 0.4f; val frente = cx + sg * an * 0.4f
        // El respaldo inclinado, con su panel de taracea y el puntal.
        val rTop = yF - alto
        val resp = Path().apply {
            moveTo(atras, asiento); lineTo(atras - sg * an * 0.14f, rTop + alto * 0.06f); quadraticTo(atras - sg * an * 0.12f, rTop, atras - sg * an * 0.04f, rTop)
            lineTo(atras + sg * an * 0.1f, asiento); close()
        }
        drawPath(resp, Brush.horizontalGradient(listOf(osc, ced, luz), atras - an * 0.15f, atras + an * 0.15f))
        clipPath(resp) {
            for (k in 0 until 4) {
                val yy = rTop + alto * (0.1f + 0.1f * k)
                drawLine(n(if (k % 2 == 0) LAPIS else ORO, o), Offset(atras - an * 0.25f, yy), Offset(atras + an * 0.25f, yy + alto * 0.02f), alto * 0.035f)
            }
        }
        drawPath(resp, n(EBANO, o), style = Stroke(p * 0.6f))
        drawLine(osc, Offset(atras - sg * an * 0.08f, asiento - alto * 0.32f), Offset(atras + sg * an * 0.3f, asiento), p * 1.2f)
        // Las patas de león, la de atrás más oscura.
        listOf(atras + sg * an * 0.06f to 0.35f, frente - sg * an * 0.06f to 0f).forEach { (x, sombra) ->
            val leg = Path().apply {
                moveTo(x - an * 0.05f, asiento); lineTo(x + an * 0.05f, asiento)
                cubicTo(x + an * 0.06f + sg * an * 0.04f, asiento + alto * 0.18f, x - an * 0.0f, asiento + alto * 0.26f, x + sg * an * 0.06f, yF - alto * 0.12f)
                lineTo(x - sg * an * 0.06f, yF - alto * 0.12f)
                cubicTo(x - an * 0.06f, asiento + alto * 0.25f, x - an * 0.07f, asiento + alto * 0.15f, x - an * 0.05f, asiento); close()
            }
            drawPath(leg, Brush.horizontalGradient(listOf(mez(osc, Color.Black, sombra), mez(luz, Color.Black, sombra), mez(osc, Color.Black, sombra)), x - an * 0.07f, x + an * 0.07f))
            drawOval(mez(n(ORO, o), Color.Black, sombra), Offset(x - an * 0.07f + sg * an * 0.02f, yF - alto * 0.14f), Size(an * 0.14f, alto * 0.06f))
            rect(Rect(x - an * 0.06f, yF - alto * 0.09f, x + an * 0.06f, yF), vert(Rect(0f, yF - alto * 0.09f, 0f, yF), mez(n(ORO, o), Color.Black, sombra), mez(n(ORO_OSC, o), Color.Black, sombra)))
        }
        // El travesaño y el asiento de cuerdas con su larguero.
        drawLine(osc, Offset(atras, asiento + alto * 0.2f), Offset(frente, asiento + alto * 0.2f), p * 1.2f)
        val as0 = Rect(minOf(atras, frente) - an * 0.04f, asiento - alto * 0.04f, maxOf(atras, frente) + an * 0.04f, asiento + alto * 0.05f)
        rect(as0, vert(as0, luz, ced, osc))
        drawLine(n(ORO, o), Offset(as0.left, as0.top + as0.height * 0.5f), Offset(as0.right, as0.top + as0.height * 0.5f), p * 0.5f)
        // El cojín, del color de acento.
        val tela = if (o) mez(e.acento, NOCHE, 0.3f) else e.acento
        val coj = Rect(as0.left + an * 0.04f, asiento - alto * 0.14f, as0.right - an * 0.02f, asiento - alto * 0.03f)
        drawRoundRect(vert(coj, mez(tela, Color.White, 0.3f), tela, mez(tela, Color.Black, 0.3f)), coj.topLeft, coj.size, CornerRadius(coj.height * 0.5f))
        drawLine(mez(tela, Color.White, 0.5f), Offset(coj.left + coj.height * 0.5f, coj.center.y), Offset(coj.right - coj.height * 0.5f, coj.center.y), p * 0.5f)
    }

    /** Una jarra de vino en su soporte cruzado, con su collar de pétalos y un loto. */
    private fun DrawScope.jarra(cx: Float, yF: Float, alto: Float, o: Boolean, p: Float) {
        val an = alto * 0.42f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - an * 0.6f, yF - alto * 0.04f), Size(an * 1.2f, alto * 0.08f))
        val sop = yF - alto * 0.4f
        listOf(-1f, 1f).forEach { s -> drawLine(n(CEDRO_OSC, o), Offset(cx - s * an * 0.45f, yF), Offset(cx + s * an * 0.4f, sop), p * 1.6f, StrokeCap.Round) }
        rect(Rect(cx - an * 0.45f, sop - alto * 0.03f, cx + an * 0.45f, sop + alto * 0.02f), n(CEDRO, o))
        val cuerpo = Path().apply {
            moveTo(cx - an * 0.12f, yF - alto * 0.98f); lineTo(cx + an * 0.12f, yF - alto * 0.98f)
            lineTo(cx + an * 0.14f, yF - alto * 0.86f)
            cubicTo(cx + an * 0.6f, yF - alto * 0.8f, cx + an * 0.52f, yF - alto * 0.5f, cx + an * 0.06f, yF - alto * 0.3f)
            lineTo(cx, yF - alto * 0.26f); lineTo(cx - an * 0.06f, yF - alto * 0.3f)
            cubicTo(cx - an * 0.52f, yF - alto * 0.5f, cx - an * 0.6f, yF - alto * 0.8f, cx - an * 0.14f, yF - alto * 0.86f); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(n(BARRO, o), n(TERRACOTA, o), mez(n(TERRACOTA, o), Color.White, 0.25f), n(BARRO, o)), cx - an * 0.5f, cx + an * 0.5f))
        clipPath(cuerpo) {
            val yc = yF - alto * 0.8f
            for (k in -3..3) drawPath(Path().apply { moveTo(cx + k * an * 0.14f - an * 0.07f, yc); quadraticTo(cx + k * an * 0.14f, yc + alto * 0.12f, cx + k * an * 0.14f + an * 0.07f, yc); close() }, n(if (k % 2 == 0) LAPIS else FAYENZA, o))
            drawLine(n(ORO, o), Offset(cx - an, yc), Offset(cx + an, yc), p * 0.6f)
        }
        drawOval(n(EBANO, o), Offset(cx - an * 0.12f, yF - alto * 1.0f), Size(an * 0.24f, alto * 0.04f))
        loto(Offset(cx + an * 0.3f, yF - alto * 0.86f), an * 0.3f, n(Color(0xFF6E8FD0), o), n(VERDE, o))
    }

    /** La mesa de ofrendas: un pie de alabastro con anillos de oro y, encima, panes, higos, uvas y lotos. */
    private fun DrawScope.mesaOfrendas(cx: Float, yF: Float, alto: Float, ancho: Float, o: Boolean, p: Float) {
        val ala = n(ALABASTRO, o); val alaO = n(PIEDRA_OSC, o)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - ancho * 0.35f, yF - alto * 0.05f), Size(ancho * 0.7f, alto * 0.1f))
        val tabla = yF - alto * 0.52f
        drawPath(Path().apply { moveTo(cx - ancho * 0.24f, yF); lineTo(cx + ancho * 0.24f, yF); lineTo(cx + ancho * 0.08f, yF - alto * 0.12f); lineTo(cx + ancho * 0.07f, tabla + alto * 0.06f); lineTo(cx + ancho * 0.2f, tabla); lineTo(cx - ancho * 0.2f, tabla); lineTo(cx - ancho * 0.07f, tabla + alto * 0.06f); lineTo(cx - ancho * 0.08f, yF - alto * 0.12f); close() },
            Brush.horizontalGradient(listOf(alaO, ala, mez(ala, Color.White, 0.3f), alaO), cx - ancho * 0.24f, cx + ancho * 0.24f))
        listOf(yF - alto * 0.12f, tabla + alto * 0.06f).forEach { y -> rect(Rect(cx - ancho * 0.1f, y - alto * 0.015f, cx + ancho * 0.1f, y + alto * 0.015f), n(ORO, o)) }
        val tab = Rect(cx - ancho * 0.5f, tabla - alto * 0.05f, cx + ancho * 0.5f, tabla)
        rect(tab, vert(tab, mez(ala, Color.White, 0.2f), alaO))
        // Las ofrendas.
        val pan = n(PAN, o)
        drawPath(Path().apply { moveTo(cx - ancho * 0.12f, tab.top); lineTo(cx - ancho * 0.03f, tab.top - alto * 0.42f); lineTo(cx + ancho * 0.06f, tab.top); close() }, Brush.horizontalGradient(listOf(mez(pan, Color.White, 0.2f), mez(pan, Color.Black, 0.25f)), cx - ancho * 0.12f, cx + ancho * 0.06f))
        listOf(-0.32f, 0.22f).forEach { f ->
            val r = Rect(cx + ancho * f - ancho * 0.12f, tab.top - alto * 0.16f, cx + ancho * f + ancho * 0.12f, tab.top + alto * 0.02f)
            drawOval(Brush.verticalGradient(listOf(mez(pan, Color.White, 0.25f), mez(pan, Color.Black, 0.3f)), r.top, r.bottom), r.topLeft, r.size)
            drawLine(mez(pan, Color.Black, 0.4f), Offset(r.center.x - r.width * 0.25f, r.top + r.height * 0.35f), Offset(r.center.x + r.width * 0.25f, r.top + r.height * 0.35f), p * 0.5f)
        }
        listOf(0.36f to 0f, 0.44f to 0.06f, 0.3f to 0.07f).forEach { (fx, fy) ->
            val c = Offset(cx + ancho * fx, tab.top - alto * (0.05f + fy))
            drawCircle(n(HIGO, o), alto * 0.05f, c); drawCircle(Color.White.copy(alpha = 0.25f), alto * 0.015f, Offset(c.x - alto * 0.015f, c.y - alto * 0.015f))
        }
        loto(Offset(cx - ancho * 0.38f, tab.top - alto * 0.05f), alto * 0.14f, n(Color(0xFF6E8FD0), o), n(VERDE, o))
        loto(Offset(cx + ancho * 0.08f, tab.top - alto * 0.32f), alto * 0.12f, n(MARFIL, o), n(VERDE, o))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.MAPAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val faldonA = h * 0.8f; val faldonB = h * 0.875f; val suelo = h * 0.955f

        // El muro de estuco, el zócalo de colores detrás de las patas y el piso de piedra.
        val muro = n(MURO, o)
        rect(Rect(0f, 0f, w, suelo), vert(Rect(0f, 0f, w, suelo), mez(muro, Color.Black, 0.2f), muro))
        zocalo(Rect(0f, faldonA, w, suelo), o, p, u)
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(PIEDRA, o), n(PIEDRA_OSC, o)))
        var xj = (w / 2) % (16f * u)
        while (xj < w) { drawLine(n(PIEDRA_OSC, o), Offset(xj, suelo), Offset(xj + (xj - w / 2) * 0.15f, h), p * 0.6f); xj += 16f * u }

        // El tablero de cedro: canto oscuro, la cara con su veta y la orla de ébano y marfil.
        val t = Rect(w * 0.02f, h * 0.025f, w * 0.98f, faldonA)
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(t.left, t.top + 1.2f * u), t.size, CornerRadius(1.2f * u))
        drawRoundRect(n(CEDRO_OSC, o), t.topLeft, t.size, CornerRadius(1.2f * u))
        val cara = t.deflate(1.2f * u)
        rect(cara, Brush.linearGradient(listOf(n(CEDRO_LUZ, o), n(CEDRO, o), mez(n(CEDRO, o), n(CEDRO_OSC, o), 0.5f)), cara.topLeft, cara.bottomRight))
        vetas(cara, n(CEDRO_OSC, o).copy(alpha = 0.45f), e.semilla, p, 0.9f)
        drawRect(n(ORO, o), cara.topLeft, cara.size, style = Stroke(p * 0.7f))
        val orla = cara.deflate(1.6f * u)
        val cuadro = 1.3f * u
        var xq = orla.left; var k = 0
        while (xq < orla.right - cuadro) {
            listOf(orla.top, orla.bottom - cuadro).forEach { y -> rect(Rect(xq, y, xq + cuadro, y + cuadro), if (k % 2 == 0) mez(n(MARFIL, o), n(CEDRO, o), 0.25f) else n(EBANO, o)) }
            xq += cuadro; k++
        }
        var yq = orla.top + cuadro; k = 1
        while (yq < orla.bottom - cuadro * 1.5f) {
            listOf(orla.left, orla.right - cuadro).forEach { x -> rect(Rect(x, yq, x + cuadro, yq + cuadro), if (k % 2 == 0) mez(n(MARFIL, o), n(CEDRO, o), 0.25f) else n(EBANO, o)) }
            yq += cuadro; k++
        }

        // El halo de la lámpara, antes que el papiro.
        val izq0 = orla.left + cuadro + 1.2f * u
        val der1 = orla.right - cuadro - 1.2f * u
        val rw = 3.6f * u
        val der0 = ni.right + rw + 1.2f * u
        val lampC = Offset((der0 + der1) / 2, ni.top + ni.height * 0.78f)
        mancha(lampC, (if (o) 34f else 14f) * u, LLAMA.copy(alpha = if (o) 0.32f else 0.14f))

        // El papiro desenrollado (el nicho), con el protocolo a la izquierda y el rollo a la derecha.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(ni.left + 0.8f * u, ni.top + 1.4f * u), Size(ni.width + rw, ni.height))
        rect(ni, nicho(e, o, PAPIRO))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            val fibra = (if (o) Color.White else Color(0xFF8A6A3A)).copy(alpha = 0.055f)
            var yf = ni.top + 1.4f * u
            while (yf < ni.bottom) { drawLine(fibra, Offset(ni.left, yf), Offset(ni.right, yf), p * 0.6f); yf += 1.9f * u }
            for (j in 1..4) { val xk = ni.left + ni.width * j / 5f; drawLine(fibra, Offset(xk, ni.top), Offset(xk, ni.bottom), p * 1.2f) }
            rect(Rect(ni.left, ni.top, ni.left + 1.2f * u, ni.bottom), (if (o) Color.Black else PIEDRA_OSC).copy(alpha = 0.12f))
        }
        val pap = if (o) mez(PAPIRO, NOCHE, 0.55f) else PAPIRO
        val rollo = Rect(ni.right, ni.top - 1.3f * u, ni.right + rw, ni.bottom + 1.3f * u)
        rect(rollo, horiz(rollo, mez(pap, Color.Black, 0.3f), mez(pap, Color.White, 0.3f), pap, mez(pap, Color.Black, 0.35f)))
        var yv = rollo.top + 2f * u
        while (yv < rollo.bottom - u) { drawLine(mez(pap, Color.Black, 0.25f), Offset(rollo.left, yv), Offset(rollo.right, yv + 0.8f * u), p * 0.4f); yv += 3.4f * u }
        listOf(rollo.top, rollo.bottom).forEach { y -> drawOval(mez(pap, Color.Black, 0.25f), Offset(rollo.left, y - 0.8f * u), Size(rw, 1.6f * u)); drawOval(mez(pap, Color.Black, 0.55f), Offset(rollo.center.x - rw * 0.15f, y - 0.3f * u), Size(rw * 0.3f, 0.6f * u)) }
        // La cinta de lino que ataba el rollo, suelta.
        drawPath(Path().apply { moveTo(rollo.center.x, ni.center.y); cubicTo(rollo.right + 2f * u, ni.center.y + 2f * u, rollo.right + 1f * u, ni.center.y + 6f * u, rollo.right + 3f * u, ni.center.y + 9f * u) }, n(LINO, o), style = Stroke(p * 1.1f, cap = StrokeCap.Round))

        // La paleta de escriba con sus dos tintas y sus cálamos, y el vaso de agua de fayenza.
        val izq1 = ni.left - 1.2f * u
        val ma = minOf(izq1 - izq0, 12f * u)
        val pal = Rect(izq0 + (izq1 - izq0 - ma * 0.55f) / 2, ni.top + ni.height * 0.02f, izq0 + (izq1 - izq0 + ma * 0.55f) / 2, ni.top + ni.height * 0.62f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(pal.left + 0.6f * u, pal.top + u), pal.size, CornerRadius(0.6f * u))
        drawRoundRect(horiz(pal, n(CEDRO_LUZ, o), mez(n(CEDRO_LUZ, o), n(PAPIRO, o), 0.4f), n(CEDRO, o)), pal.topLeft, pal.size, CornerRadius(0.6f * u))
        drawRoundRect(n(CEDRO_OSC, o), pal.topLeft, pal.size, CornerRadius(0.6f * u), style = Stroke(p * 0.5f))
        val rt = pal.width * 0.26f
        listOf(CORNALINA to 0.1f, EBANO to 0.22f).forEach { (col, f) ->
            val c = Offset(pal.center.x, pal.top + pal.height * f)
            drawCircle(n(CEDRO_OSC, o), rt * 1.1f, c)
            drawCircle(Brush.radialGradient(listOf(mez(n(col, o), Color.White, 0.25f), n(col, o)), Offset(c.x - rt * 0.3f, c.y - rt * 0.3f), rt), rt, c)
        }
        val ranura = Rect(pal.center.x - pal.width * 0.16f, pal.top + pal.height * 0.33f, pal.center.x + pal.width * 0.16f, pal.bottom - pal.height * 0.06f)
        rect(ranura, n(CEDRO_OSC, o))
        listOf(-0.5f, 0.5f).forEach { f -> drawLine(n(Color(0xFFC9B27A), o), Offset(ranura.center.x + f * ranura.width * 0.5f, ranura.top + ranura.height * 0.05f), Offset(ranura.center.x + f * ranura.width * 0.5f, ranura.bottom - ranura.height * 0.05f), p * 0.7f) }
        rect(Rect(pal.left, pal.bottom - pal.height * 0.04f, pal.right, pal.bottom), n(ORO, o))
        val vaso = Offset((izq0 + izq1) / 2, ni.top + ni.height * 0.82f)
        val rv = ma * 0.32f
        drawCircle(Color.Black.copy(alpha = 0.3f), rv, Offset(vaso.x + 0.6f * u, vaso.y + u))
        drawCircle(Brush.radialGradient(listOf(mez(n(FAYENZA, o), Color.White, 0.4f), n(FAYENZA, o), mez(n(FAYENZA, o), Color.Black, 0.3f)), Offset(vaso.x - rv * 0.3f, vaso.y - rv * 0.3f), rv * 1.3f), rv, vaso)
        drawCircle(mez(n(FAYENZA, o), Color(0xFF0A2A40), 0.5f), rv * 0.68f, vaso)
        drawCircle(Color.White.copy(alpha = 0.35f), rv * 0.18f, Offset(vaso.x - rv * 0.25f, vaso.y - rv * 0.25f))
        drawCircle(n(EBANO, o), rv, vaso, style = Stroke(p * 0.5f))

        // La cuerda de medir enrollada, con sus nudos.
        val cuerda = Offset((der0 + der1) / 2, ni.top + ni.height * 0.25f)
        val rc = minOf(der1 - der0, 12f * u) * 0.42f
        drawCircle(Color.Black.copy(alpha = 0.28f), rc, Offset(cuerda.x + 0.6f * u, cuerda.y + u))
        for (j in 0 until 5) {
            val rr = rc * (1f - j * 0.18f)
            drawCircle(n(LINO_OSC, o), rr, cuerda, style = Stroke(rc * 0.16f))
            drawCircle(n(LINO, o), rr, cuerda, style = Stroke(rc * 0.08f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(rc * 0.12f, rc * 0.08f))))
        }
        listOf(0f, 1.4f, 3f, 4.6f).forEach { ang -> drawCircle(n(BARRO, o), rc * 0.11f, Offset(cuerda.x + rc * cos(ang), cuerda.y + rc * sin(ang))) }
        drawPath(Path().apply { moveTo(cuerda.x + rc, cuerda.y); quadraticTo(cuerda.x + rc * 1.1f, cuerda.y + rc * 1.4f, cuerda.x + rc * 0.4f, cuerda.y + rc * 1.9f) }, n(LINO_OSC, o), style = Stroke(rc * 0.16f, cap = StrokeCap.Round))
        // La lámpara de barro, encendida.
        val rl = minOf(der1 - der0, 12f * u) * 0.32f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(lampC.x - rl + 0.6f * u, lampC.y - rl * 0.8f + u), Size(rl * 2, rl * 1.6f))
        drawOval(Brush.radialGradient(listOf(n(TERRACOTA, o), n(BARRO, o)), Offset(lampC.x - rl * 0.3f, lampC.y - rl * 0.3f), rl * 1.4f), Offset(lampC.x - rl, lampC.y - rl * 0.8f), Size(rl * 2, rl * 1.6f))
        drawOval(mez(n(BARRO, o), Color.Black, 0.4f), Offset(lampC.x - rl * 0.7f, lampC.y - rl * 0.52f), Size(rl * 1.4f, rl * 1.04f))
        drawOval(n(Color(0xFFB08A3A), o).copy(alpha = 0.8f), Offset(lampC.x - rl * 0.55f, lampC.y - rl * 0.38f), Size(rl * 1.1f, rl * 0.76f))
        llama(Offset(lampC.x, lampC.y - rl * 0.75f), rl * 0.3f, o)

        // El codo real: la regla de ébano con sus marcas y las puntas de oro, al pie del papiro.
        val yb = (ni.bottom + 1.3f * u + orla.bottom - cuadro) / 2
        val codo = Rect(ni.left + ni.width * 0.08f, yb - 1.1f * u, ni.left + ni.width * 0.6f, yb + 1.1f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(codo.left + 0.5f * u, codo.top + 0.8f * u), codo.size)
        rect(codo, vert(codo, mez(n(EBANO, o), Color.White, 0.25f), n(EBANO, o)))
        var xr = codo.left + codo.width / 28
        var kr = 1
        while (xr < codo.right - u) { drawLine(n(ORO, o), Offset(xr, codo.top), Offset(xr, codo.top + codo.height * (if (kr % 4 == 0) 0.75f else 0.4f)), p * 0.4f); xr += codo.width / 28; kr++ }
        listOf(codo.left, codo.right - 1.2f * u).forEach { x -> rect(Rect(x, codo.top, x + 1.2f * u, codo.bottom), n(ORO, o)) }
        // Un escarabeo de fayenza que sujeta la esquina.
        escarabeo(Offset(ni.left + ni.width * 0.8f, yb), 1.8f * u, o, p)

        // El faldón, con su orla de franjas y rosetas de oro, y las patas de león sobre tambores.
        val fa = Rect(t.left + 1.2f * u, faldonA, t.right - 1.2f * u, faldonB)
        rect(fa, vert(fa, n(CEDRO, o), n(CEDRO_OSC, o)))
        drawLine(n(ORO, o), Offset(fa.left, fa.top + p * 0.8f), Offset(fa.right, fa.top + p * 0.8f), p)
        val banda = Rect(fa.left + fa.width * 0.15f, fa.top + fa.height * 0.25f, fa.right - fa.width * 0.15f, fa.bottom - fa.height * 0.25f)
        val nb = (banda.width / (2.2f * u)).toInt()
        for (j in 0 until nb) rect(Rect(banda.left + banda.width * j / nb + 0.3f * u, banda.top, banda.left + banda.width * (j + 1) / nb - 0.3f * u, banda.bottom), n(listOf(LAPIS, ORO, CORNALINA, ORO, VERDE, ORO)[j % 6], o))
        listOf(fa.left + fa.width * 0.07f, fa.right - fa.width * 0.07f).forEach { x -> drawCircle(n(ORO, o), fa.height * 0.28f, Offset(x, fa.center.y)); drawCircle(n(CORNALINA, o), fa.height * 0.12f, Offset(x, fa.center.y)) }
        val an = minOf(w * 0.045f, 8f * u)
        listOf(fa.left + an, fa.right - an).forEachIndexed { j, x -> pataEgipcia(x, faldonB, suelo, an, if (j == 0) -1f else 1f, o, p) }
    }

    /** Un escarabeo de fayenza visto desde arriba. */
    private fun DrawScope.escarabeo(c: Offset, r: Float, o: Boolean, p: Float) {
        listOf(-1f, 1f).forEach { s -> for (j in 0 until 3) drawLine(n(EBANO, o), Offset(c.x + s * r * 0.3f, c.y - r * 0.3f + j * r * 0.5f), Offset(c.x + s * r * 1.15f, c.y - r * 0.6f + j * r * 0.7f), p * 0.6f) }
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - r * 0.7f + p, c.y - r * 0.8f + p * 1.5f), Size(r * 1.4f, r * 1.9f))
        drawOval(Brush.radialGradient(listOf(mez(n(FAYENZA, o), Color.White, 0.4f), n(FAYENZA, o), mez(n(FAYENZA, o), Color.Black, 0.3f)), Offset(c.x - r * 0.2f, c.y - r * 0.2f), r * 1.2f), Offset(c.x - r * 0.7f, c.y - r * 0.6f), Size(r * 1.4f, r * 1.6f))
        drawOval(n(FAYENZA, o), Offset(c.x - r * 0.4f, c.y - r * 1.05f), Size(r * 0.8f, r * 0.5f))
        drawLine(n(ORO, o), Offset(c.x, c.y - r * 0.55f), Offset(c.x, c.y + r * 0.95f), p * 0.5f)
    }

    /** Una pata de león egipcia sobre su tambor acanalado. */
    private fun DrawScope.pataEgipcia(cx: Float, y0: Float, y1: Float, an: Float, sg: Float, o: Boolean, p: Float) {
        val hh = y1 - y0
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - an * 0.8f, y1 - hh * 0.08f), Size(an * 1.6f, hh * 0.16f))
        val tam = Rect(cx - an * 0.42f, y1 - hh * 0.22f, cx + an * 0.42f, y1)
        rect(tam, horiz(tam, n(CEDRO_OSC, o), n(CEDRO_LUZ, o), n(CEDRO_OSC, o)))
        for (j in 1 until 4) drawLine(n(CEDRO_OSC, o), Offset(tam.left + tam.width * j / 4, tam.top), Offset(tam.left + tam.width * j / 4, tam.bottom), p * 0.5f)
        rect(Rect(tam.left - an * 0.04f, tam.top - hh * 0.03f, tam.right + an * 0.04f, tam.top), n(ORO, o))
        val pata = Path().apply {
            moveTo(cx - an * 0.4f, y0); lineTo(cx + an * 0.4f, y0)
            cubicTo(cx + an * 0.42f, y0 + hh * 0.3f, cx + an * 0.12f + sg * an * 0.05f, y0 + hh * 0.42f, cx + an * 0.3f, tam.top - hh * 0.12f)
            lineTo(cx - an * 0.3f, tam.top - hh * 0.12f)
            cubicTo(cx - an * 0.12f + sg * an * 0.05f, y0 + hh * 0.42f, cx - an * 0.42f, y0 + hh * 0.3f, cx - an * 0.4f, y0); close()
        }
        drawPath(pata, Brush.horizontalGradient(listOf(n(CEDRO_OSC, o), n(CEDRO_LUZ, o), n(CEDRO, o), n(CEDRO_OSC, o)), cx - an * 0.45f, cx + an * 0.45f))
        for (j in -1..1) drawOval(n(CEDRO, o), Offset(cx + j * an * 0.2f - an * 0.13f, tam.top - hh * 0.16f), Size(an * 0.26f, hh * 0.16f))
        rect(Rect(cx - an * 0.46f, y0, cx + an * 0.46f, y0 + hh * 0.1f), vert(Rect(0f, y0, 0f, y0 + hh * 0.1f), n(ORO_LUZ, o), n(ORO_OSC, o)))
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.OTRAS_VIDAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val ced = n(CEDRO, o); val luz = n(CEDRO_LUZ, o); val osc = n(CEDRO_OSC, o)
        val ureos = h * 0.065f; val golaBajo = h * 0.17f; val toroBajo = h * 0.2f
        val baseBajo = h * 0.94f

        // El cuerpo del cofre: cedro con su veta.
        val cuerpo = Rect(w * 0.01f, toroBajo, w * 0.99f, baseBajo)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left + 0.8f * u, cuerpo.top + 1.2f * u), cuerpo.size)
        rect(cuerpo, Brush.linearGradient(listOf(luz, ced, osc), cuerpo.topLeft, cuerpo.bottomRight))
        vetas(Rect(cuerpo.left, cuerpo.top, ni.left, cuerpo.bottom), osc.copy(alpha = 0.5f), e.semilla, p, vertical = true)
        vetas(Rect(ni.right, cuerpo.top, cuerpo.right, cuerpo.bottom), osc.copy(alpha = 0.5f), e.semilla + 1, p, vertical = true)
        vetas(Rect(cuerpo.left, ni.bottom, cuerpo.right, cuerpo.bottom), osc.copy(alpha = 0.5f), e.semilla + 2, p)
        vetas(Rect(cuerpo.left, cuerpo.top, cuerpo.right, ni.top), osc.copy(alpha = 0.5f), e.semilla + 3, p)

        // Los cinco compartimentos forrados de lino, con su sombra y su peldaño de cedro al pie.
        val forro = ni.inflate(0.8f * u)
        rect(forro, n(ORO, o))
        rect(ni, nicho(e, o, LINO))
        val cw = ni.width / 5
        val dv = (cw - 47f * density).coerceIn(3f * density, cw * 0.12f)
        for (i in 0 until 5) {
            val c = Rect(ni.left + cw * i + (if (i == 0) 0f else dv / 2), ni.top, ni.left + cw * (i + 1) - (if (i == 4) 0f else dv / 2), ni.bottom)
            clipRect(c.left, c.top, c.right, c.bottom) {
                var x = c.left - c.height
                while (x < c.right) { drawLine((if (o) Color.White else LINO_OSC).copy(alpha = if (o) 0.025f else 0.12f), Offset(x, c.bottom), Offset(x + c.height * 0.6f, c.top), 2.2f * u); x += 5f * u }
            }
            rect(Rect(c.left, c.top, c.right, c.top + 6f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.18f), Color.Transparent), c.top, c.top + 6f * u))
            rect(Rect(c.left, c.top, c.left + 2.5f * u, c.bottom), Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent), c.left, c.left + 2.5f * u))
            val pel = Rect(c.left + c.width * 0.16f, ni.bottom - 3.6f * u, c.right - c.width * 0.16f, ni.bottom)
            rect(pel, vert(pel, luz, osc))
            drawLine(n(ORO, o), Offset(pel.left, pel.top + p * 0.5f), Offset(pel.right, pel.top + p * 0.5f), p * 0.8f)
        }
        // Los listones entre compartimentos: ébano con una franja de oro y su capitel de loto.
        for (i in 1 until 5) {
            val x = ni.left + cw * i
            val r = Rect(x - dv / 2, ni.top, x + dv / 2, ni.bottom)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.right, r.top), Size(1f * u, r.height))
            rect(r, horiz(r, mez(n(EBANO, o), Color.White, 0.2f), n(EBANO, o)))
            drawLine(n(ORO, o), Offset(x, r.top + 2.5f * u), Offset(x, r.bottom - 2.5f * u), dv * 0.3f)
            rect(Rect(r.left - 0.6f * u, r.top, r.right + 0.6f * u, r.top + 2f * u), vert(Rect(0f, r.top, 0f, r.top + 2f * u), n(ORO_LUZ, o), n(ORO_OSC, o)))
            rect(Rect(r.left - 0.6f * u, r.bottom - 1.8f * u, r.right + 0.6f * u, r.bottom), vert(Rect(0f, r.bottom - 1.8f * u, 0f, r.bottom), n(ORO_LUZ, o), n(ORO_OSC, o)))
        }

        // Los pilares djed de oro a los lados.
        val dw = minOf(ni.left * 0.5f, 7f * u)
        listOf(ni.left * 0.5f, w - ni.left * 0.5f).forEach { djed(it, ni.top + 1f * u, ni.bottom - 0.5f * u, dw, o, p) }

        // La tapa: friso de ureos, gola con franjas y disco alado, y el toro atado.
        val nU = (w / (7.5f * u)).toInt().coerceAtLeast(10)
        for (i in 0 until nU) ureo(Offset(w * (i + 0.5f) / nU, ureos), ureos * 0.9f, o, p)
        gola(Rect(w * 0.005f, ureos + 0.8f * u, w * 0.995f, golaBajo), o, p, u)
        toro(Rect(0f, golaBajo, w, toroBajo), false, o, p)
        solAlado(Offset(w / 2, (ureos + golaBajo) / 2 + 1.2f * u), (golaBajo - ureos) * 0.25f, o, p)
        rect(Rect(cuerpo.left, toroBajo, cuerpo.right, toroBajo + 2f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), toroBajo, toroBajo + 2f * u))

        // La base: la orla de marfil y ébano, las cantoneras de oro y las patas.
        val orla = Rect(cuerpo.left + 2f * u, ni.bottom + (cuerpo.bottom - ni.bottom) * 0.35f, cuerpo.right - 2f * u, ni.bottom + (cuerpo.bottom - ni.bottom) * 0.65f)
        val cq = orla.height / 2
        var xq = orla.left; var k = 0
        while (xq < orla.right - cq) {
            rect(Rect(xq, orla.top, xq + cq, orla.top + cq), n(if (k % 2 == 0) MARFIL else EBANO, o))
            rect(Rect(xq, orla.top + cq, xq + cq, orla.bottom), n(if (k % 2 == 0) EBANO else MARFIL, o))
            xq += cq; k++
        }
        drawRect(n(ORO, o), orla.topLeft, orla.size, style = Stroke(p * 0.6f))
        listOf(cuerpo.left, cuerpo.right).forEach { x ->
            val s = if (x < w / 2) 1f else -1f
            val ca = 4.5f * u
            listOf(cuerpo.top, cuerpo.bottom).forEach { y ->
                val sy = if (y < h / 2) 1f else -1f
                drawPath(Path().apply { moveTo(x, y); lineTo(x + s * ca, y); lineTo(x, y + sy * ca); close() }, Brush.linearGradient(listOf(n(ORO_LUZ, o), n(ORO_OSC, o)), Offset(x, y), Offset(x + s * ca, y + sy * ca)))
            }
        }
        listOf(w * 0.06f, w * 0.94f).forEach { x ->
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(x - 4f * u, h - 1.6f * u), Size(8f * u, 1.6f * u))
            val pt = Rect(x - 2.6f * u, baseBajo, x + 2.6f * u, h - 0.6f * u)
            rect(pt, horiz(pt, osc, luz, osc))
            rect(Rect(pt.left - 0.4f * u, pt.bottom - 1.2f * u, pt.right + 0.4f * u, pt.bottom), n(ORO, o))
        }
    }

    /** Un pilar djed: el fuste con las cuatro barras del capitel, de oro con filetes de lapislázuli. */
    private fun DrawScope.djed(cx: Float, top: Float, bottom: Float, an: Float, o: Boolean, p: Float) {
        val oro = Brush.horizontalGradient(listOf(n(ORO_OSC, o), n(ORO_LUZ, o), n(ORO, o), n(ORO_OSC, o)), cx - an, cx + an)
        val hh = bottom - top
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(cx + an * 0.35f, top + hh * 0.3f), Size(an * 0.35f, hh * 0.7f))
        drawRect(oro, Offset(cx - an * 0.32f, top + hh * 0.3f), Size(an * 0.64f, hh * 0.64f))
        drawRect(oro, Offset(cx - an * 0.5f, bottom - hh * 0.06f), Size(an, hh * 0.06f))
        for (k in 0 until 4) {
            val y = top + hh * (0.04f + 0.065f * k)
            drawRect(oro, Offset(cx - an * 0.62f, y), Size(an * 1.24f, hh * 0.045f))
            drawLine(n(LAPIS, o), Offset(cx - an * 0.62f, y + hh * 0.045f), Offset(cx + an * 0.62f, y + hh * 0.045f), p * 0.6f)
        }
        drawPath(Path().apply { moveTo(cx - an * 0.32f, top + hh * 0.3f); lineTo(cx - an * 0.45f, top + hh * 0.28f); lineTo(cx + an * 0.45f, top + hh * 0.28f); lineTo(cx + an * 0.32f, top + hh * 0.3f); close() }, oro)
        for (k in 0 until 3) drawLine(n(LAPIS, o), Offset(cx - an * 0.32f, top + hh * (0.4f + 0.04f * k)), Offset(cx + an * 0.32f, top + hh * (0.4f + 0.04f * k)), p * 0.5f)
        rect(Rect(cx - an * 0.36f, top, cx + an * 0.36f, top + hh * 0.04f), n(ORO_OSC, o))
    }

    /** Un ureo: la cobra erguida con el disco solar, de oro con el pecho de lapislázuli. */
    private fun DrawScope.ureo(b: Offset, alto: Float, o: Boolean, p: Float) {
        val an = alto * 0.42f
        val cuerpo = Path().apply {
            moveTo(b.x - an * 0.45f, b.y); quadraticTo(b.x - an * 0.55f, b.y - alto * 0.45f, b.x - an * 0.18f, b.y - alto * 0.62f)
            quadraticTo(b.x - an * 0.4f, b.y - alto * 0.8f, b.x, b.y - alto * 0.78f)
            quadraticTo(b.x + an * 0.4f, b.y - alto * 0.8f, b.x + an * 0.18f, b.y - alto * 0.62f)
            quadraticTo(b.x + an * 0.55f, b.y - alto * 0.45f, b.x + an * 0.45f, b.y); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(n(ORO_OSC, o), n(ORO_LUZ, o), n(ORO, o)), b.x - an * 0.5f, b.x + an * 0.5f))
        drawPath(Path().apply { moveTo(b.x - an * 0.16f, b.y - alto * 0.05f); lineTo(b.x - an * 0.12f, b.y - alto * 0.58f); lineTo(b.x + an * 0.12f, b.y - alto * 0.58f); lineTo(b.x + an * 0.16f, b.y - alto * 0.05f); close() }, n(LAPIS, o))
        drawCircle(n(CORNALINA, o), an * 0.3f, Offset(b.x, b.y - alto * 0.9f))
        drawCircle(n(ORO, o), an * 0.3f, Offset(b.x, b.y - alto * 0.9f), style = Stroke(p * 0.4f))
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val c = Offset(w / 2, h / 2)
        val s = h * 0.43f
        val p = s * 0.035f
        val o = oscuro
        when (id) {
            "unesco" -> {
                // Las pirámides de Guiza al sol, sobre la arena, con una palmera.
                val sol = Offset(c.x + s * 1.35f, c.y - s * 0.55f)
                mancha(sol, s * 0.7f, n(ORO, o).copy(alpha = 0.5f))
                drawCircle(n(CORNALINA, o), s * 0.2f, sol)
                drawCircle(n(ORO, o), s * 0.2f, sol, style = Stroke(p))
                val arena = Path().apply { moveTo(c.x - s * 2.1f, c.y + s); lineTo(c.x - s * 2.1f, c.y + s * 0.55f); quadraticTo(c.x - s * 0.6f, c.y + s * 0.38f, c.x + s * 0.6f, c.y + s * 0.5f); quadraticTo(c.x + s * 1.5f, c.y + s * 0.58f, c.x + s * 2.1f, c.y + s * 0.48f); lineTo(c.x + s * 2.1f, c.y + s); close() }
                listOf(Triple(c.x + s * 0.75f, 0.62f, 0.5f), Triple(c.x - s * 0.95f, 0.78f, 0.47f), Triple(c.x - s * 0.05f, 1.0f, 0.5f)).forEach { (x, f, base) ->
                    val alt = s * 1.35f * f; val an = alt * 0.85f
                    val yb = c.y + s * base
                    val cima = Offset(x, yb - alt)
                    drawPath(Path().apply { moveTo(cima.x, cima.y); lineTo(x - an, yb); lineTo(x + an * 0.18f, yb); close() }, n(Color(0xFFE6C88A), o))
                    drawPath(Path().apply { moveTo(cima.x, cima.y); lineTo(x + an * 0.18f, yb); lineTo(x + an, yb); close() }, n(Color(0xFFB08A50), o))
                    for (j in 1 until 6) {
                        val y = cima.y + alt * j / 6f
                        val m = an * j / 6f
                        drawLine(n(Color(0xFFC9A96A), o), Offset(x - m, y), Offset(x + m * 0.18f, y), p * 0.4f)
                        drawLine(n(Color(0xFF8E6A36), o), Offset(x + m * 0.18f, y), Offset(x + m, y), p * 0.4f)
                    }
                    if (f == 1f) drawPath(Path().apply { moveTo(cima.x, cima.y); lineTo(x - an * 0.14f, cima.y + alt * 0.14f); lineTo(x + an * 0.14f, cima.y + alt * 0.14f); close() }, Brush.horizontalGradient(listOf(n(ORO_LUZ, o), n(ORO_OSC, o)), x - an * 0.14f, x + an * 0.14f))
                }
                drawPath(arena, Brush.verticalGradient(listOf(n(Color(0xFFE8CF95), o), n(Color(0xFFC9A465), o)), c.y + s * 0.4f, c.y + s))
                drawPath(Path().apply { moveTo(c.x - s * 1.6f, c.y + s * 0.8f); quadraticTo(c.x - s * 0.6f, c.y + s * 0.65f, c.x + s * 0.4f, c.y + s * 0.82f) }, n(Color(0xFFB08A50), o), style = Stroke(p))
                // La palmera.
                val pie = Offset(c.x + s * 1.75f, c.y + s * 0.62f)
                val copa = Offset(pie.x - s * 0.12f, pie.y - s * 0.95f)
                drawPath(Path().apply { moveTo(pie.x, pie.y); quadraticTo(pie.x + s * 0.05f, pie.y - s * 0.5f, copa.x, copa.y) }, n(CEDRO, o), style = Stroke(p * 3.2f, cap = StrokeCap.Round))
                for (j in 0 until 6) {
                    val ang = Math.toRadians(200.0 + j * 28.0)
                    val tip = Offset(copa.x + s * 0.45f * cos(ang).toFloat(), copa.y + s * 0.3f * sin(ang).toFloat() + s * 0.12f)
                    drawPath(Path().apply { moveTo(copa.x, copa.y); quadraticTo((copa.x + tip.x) / 2, copa.y - s * 0.18f, tip.x, tip.y) }, n(VERDE, o), style = Stroke(p * 2.2f, cap = StrokeCap.Round))
                }
            }
            "rascar" -> {
                // Una tablilla de lapislázuli cubierta de pan de oro: el escarabeo raspa el oro y aparece el papiro con el Nilo.
                val tab = Rect(c.x - s * 1.9f, c.y - s * 0.85f, c.x + s * 1.3f, c.y + s * 0.85f)
                drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(tab.left + p * 2, tab.top + p * 3), tab.size, CornerRadius(s * 0.08f))
                drawRoundRect(n(LAPIS, o), tab.topLeft, tab.size, CornerRadius(s * 0.08f))
                val oro = tab.deflate(s * 0.1f)
                rect(oro, Brush.linearGradient(listOf(n(ORO_LUZ, o), n(ORO, o), n(ORO_OSC, o), n(ORO, o)), oro.topLeft, oro.bottomRight))
                val raspado = Path().apply {
                    moveTo(oro.left + s * 0.15f, oro.top + s * 0.25f); cubicTo(oro.left + s * 0.9f, oro.top + s * 0.05f, oro.left + s * 2.0f, oro.top + s * 0.15f, oro.left + s * 2.6f, oro.top + s * 0.45f)
                    lineTo(oro.left + s * 2.75f, oro.top + s * 1.2f); cubicTo(oro.left + s * 1.8f, oro.bottom, oro.left + s * 0.8f, oro.bottom - s * 0.05f, oro.left + s * 0.2f, oro.top + s * 1.1f); close()
                }
                drawPath(raspado, n(PAPIRO, o))
                clipPath(raspado) {
                    val nilo = Path().apply { moveTo(oro.left + s * 1.5f, oro.bottom); cubicTo(oro.left + s * 1.3f, oro.top + s * 1.0f, oro.left + s * 1.75f, oro.top + s * 0.7f, oro.left + s * 1.45f, oro.top + s * 0.3f); quadraticTo(oro.left + s * 1.2f, oro.top + s * 0.05f, oro.left + s * 0.9f, oro.top) }
                    drawPath(nilo, n(VERDE, o).copy(alpha = 0.6f), style = Stroke(s * 0.3f, cap = StrokeCap.Round))
                    drawPath(nilo, n(LAPIS, o), style = Stroke(s * 0.1f, cap = StrokeCap.Round))
                    drawCircle(e.c2, s * 0.08f, Offset(oro.left + s * 1.9f, oro.top + s * 0.75f))
                    for (k in 0 until 8) drawLine(Color.Black.copy(alpha = 0.08f), Offset(oro.left, oro.top + s * 0.2f * k), Offset(oro.right, oro.top + s * 0.2f * k), p * 0.5f)
                }
                drawPath(raspado, n(ORO_OSC, o), style = Stroke(p))
                // El escarabeo que raspa.
                val ec = Offset(c.x + s * 1.35f, c.y + s * 0.25f)
                val r = s * 0.55f
                listOf(-1f, 1f).forEach { k -> for (j in 0 until 3) drawLine(n(EBANO, o), Offset(ec.x + k * r * 0.4f, ec.y - r * 0.3f + j * r * 0.45f), Offset(ec.x + k * r * 1.0f, ec.y - r * 0.55f + j * r * 0.6f), p * 1.2f, StrokeCap.Round) }
                drawOval(Color.Black.copy(alpha = 0.3f), Offset(ec.x - r * 0.62f + p * 2, ec.y - r * 0.75f + p * 3), Size(r * 1.24f, r * 1.7f))
                drawOval(Brush.radialGradient(listOf(mez(n(FAYENZA, o), Color.White, 0.5f), n(FAYENZA, o), mez(n(FAYENZA, o), Color.Black, 0.4f)), Offset(ec.x - r * 0.2f, ec.y - r * 0.2f), r * 1.1f), Offset(ec.x - r * 0.6f, ec.y - r * 0.5f), Size(r * 1.2f, r * 1.4f))
                drawOval(n(FAYENZA, o), Offset(ec.x - r * 0.38f, ec.y - r * 0.9f), Size(r * 0.76f, r * 0.45f))
                drawLine(n(ORO, o), Offset(ec.x, ec.y - r * 0.45f), Offset(ec.x, ec.y + r * 0.85f), p * 1.2f)
                drawLine(n(ORO, o), Offset(ec.x - r * 0.55f, ec.y - r * 0.4f), Offset(ec.x + r * 0.55f, ec.y - r * 0.4f), p * 1.2f)
            }
            else -> {
                // Tus lugares: un papiro con el Nilo y tres obeliscos con la punta de oro.
                val hoja = Rect(c.x - s * 2.0f, c.y - s * 0.85f, c.x + s * 2.0f, c.y + s * 0.85f)
                drawRect(Color.Black.copy(alpha = 0.28f), Offset(hoja.left + p * 2, hoja.top + p * 3), hoja.size)
                val pap = n(PAPIRO, o)
                rect(hoja, vert(hoja, mez(pap, Color.White, 0.2f), pap))
                clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                    for (k in 0 until 9) drawLine(mez(pap, Color.Black, 0.1f), Offset(hoja.left, hoja.top + s * 0.2f * k), Offset(hoja.right, hoja.top + s * 0.2f * k), p * 0.4f)
                    val nilo = Path().apply { moveTo(hoja.left + s * 0.6f, hoja.bottom); cubicTo(hoja.left + s * 0.9f, hoja.top + s * 1.2f, hoja.left + s * 0.4f, hoja.top + s * 0.8f, hoja.left + s * 0.8f, hoja.top + s * 0.4f); quadraticTo(hoja.left + s * 1.0f, hoja.top + s * 0.1f, hoja.left + s * 0.7f, hoja.top) }
                    drawPath(nilo, n(VERDE, o).copy(alpha = 0.55f), style = Stroke(s * 0.42f, cap = StrokeCap.Round))
                    drawPath(nilo, n(LAPIS, o), style = Stroke(s * 0.12f, cap = StrokeCap.Round))
                }
                drawRect(n(CORNALINA, o), Offset(hoja.left + s * 0.1f, hoja.top + s * 0.08f), Size(hoja.width - s * 0.2f, p * 1.2f))
                val pts = listOf(Offset(c.x - s * 0.3f, c.y + s * 0.6f), Offset(c.x + s * 0.55f, c.y + s * 0.15f), Offset(c.x + s * 1.4f, c.y + s * 0.65f))
                drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x, c.y - s * 0.1f, pts[1].x, pts[1].y); quadraticTo(c.x + s * 1.15f, c.y + s * 0.05f, pts[2].x, pts[2].y) },
                    n(CORNALINA, o).copy(alpha = 0.8f), style = Stroke(p * 1.1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(p * 2.5f, p * 2f))))
                listOf(e.c1, e.c2, n(ORO, o)).forEachIndexed { k, col ->
                    val b = pts[k]
                    val alt = s * (1.0f - 0.1f * k)
                    val an = alt * 0.16f
                    drawPath(Path().apply { moveTo(b.x + an, b.y); lineTo(b.x + an * 3.2f, b.y + an * 0.4f); lineTo(b.x - an * 0.2f, b.y + an * 0.6f); close() }, Color.Black.copy(alpha = 0.3f))
                    val ob = Path().apply { moveTo(b.x - an, b.y); lineTo(b.x + an, b.y); lineTo(b.x + an * 0.7f, b.y - alt * 0.82f); lineTo(b.x - an * 0.7f, b.y - alt * 0.82f); close() }
                    drawPath(ob, Brush.horizontalGradient(listOf(n(PIEDRA_LUZ, o), n(PIEDRA, o), n(PIEDRA_OSC, o)), b.x - an, b.x + an))
                    drawPath(Path().apply { moveTo(b.x - an * 0.7f, b.y - alt * 0.82f); lineTo(b.x, b.y - alt); lineTo(b.x + an * 0.7f, b.y - alt * 0.82f); close() }, Brush.horizontalGradient(listOf(n(ORO_LUZ, o), n(ORO_OSC, o)), b.x - an, b.x + an))
                    rect(Rect(b.x - an * 0.4f, b.y - alt * 0.7f, b.x + an * 0.4f, b.y - alt * 0.6f), col)
                    for (j in 0 until 3) drawLine(col.copy(alpha = 0.7f), Offset(b.x - an * 0.3f, b.y - alt * (0.5f - 0.12f * j)), Offset(b.x + an * 0.3f, b.y - alt * (0.5f - 0.12f * j)), p * 0.6f)
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
        val golaTop = h * 0.02f; val golaBajo = h * 0.095f
        val t = 2.4f * u
        val repisa = ni.bottom + t + 0.6f * u

        // La estela de arenisca: el cuerpo con sus sillares.
        val estela = Rect(w * 0.012f, golaBajo, w * 0.988f, repisa)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(estela.left + 0.8f * u, estela.top + 1.4f * u), estela.size)
        rect(estela, Brush.linearGradient(listOf(n(PIEDRA_LUZ, o), n(PIEDRA, o), n(PIEDRA_OSC, o)), estela.topLeft, estela.bottomRight))
        // Los toros atados alrededor del muro.
        toro(Rect(ni.left - t, ni.top - t, ni.right + t, ni.top), false, o, p)
        toro(Rect(ni.left - t, ni.bottom, ni.right + t, ni.bottom + t), false, o, p)
        toro(Rect(ni.left - t, ni.top, ni.left, ni.bottom), true, o, p)
        toro(Rect(ni.right, ni.top, ni.right + t, ni.bottom), true, o, p)
        // Las columnas de signos a los lados (en relieve hundido, con color).
        listOf((estela.left + ni.left - t) / 2, (estela.right + ni.right + t) / 2).forEach { x ->
            var y = ni.top + 4f * u; var k = (x / u).toInt()
            while (y < ni.bottom - 3f * u) { signo(k, Offset(x, y), minOf(3.2f * u, (ni.left - t - estela.left) * 0.7f), n(if (k % 3 == 0) CORNALINA else LAPIS, o).copy(alpha = 0.75f), p); y += 6.5f * u; k += 3 }
        }
        // El muro enlucido: el nicho donde van las notas.
        rect(ni, nicho(e, o, ESTUCO))
        val rnd = Random(e.semilla.toLong() * 19 + 11)
        repeat(16) { mancha(Offset(ni.left + ni.width * rnd.nextFloat(), ni.top + ni.height * rnd.nextFloat()), (5f + 9f * rnd.nextFloat()) * u, (if (o) Color.White else PIEDRA_OSC).copy(alpha = 0.04f)) }
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 3f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent), ni.top, ni.top + 3f * u))

        // La gola con sus franjas y el disco alado.
        gola(Rect(estela.left - 0.5f * u, golaTop + 1.2f * u, estela.right + 0.5f * u, golaBajo), o, p, u)
        solAlado(Offset(w / 2, (golaTop + golaBajo) / 2 + 1.2f * u), (golaBajo - golaTop) * 0.24f, o, p)

        // El zócalo de colores bajo la estela y la repisa con la paleta y dos vasos de tinta.
        zocalo(Rect(estela.left, repisa, estela.right, h * 0.955f), o, p, u)
        val rep = Rect(0f, repisa, w, repisa + 2.6f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 1.2f * u))
        rect(rep, vert(rep, n(PIEDRA_LUZ, o), n(PIEDRA, o), n(PIEDRA_OSC, o)))
        rect(Rect(0f, h * 0.955f, w, h), vert(Rect(0f, h * 0.955f, w, h), n(PIEDRA_OSC, o), mez(n(PIEDRA_OSC, o), Color.Black, 0.3f)))
        // La paleta de escriba acostada, con sus dos tintas.
        val pal = Rect(w * 0.18f, repisa - 2.2f * u, w * 0.38f, repisa)
        rect(pal, vert(pal, n(CEDRO_LUZ, o), n(CEDRO_OSC, o)))
        listOf(CORNALINA to 0.08f, EBANO to 0.16f).forEach { (col, f) -> drawCircle(n(col, o), pal.height * 0.3f, Offset(pal.left + pal.width * f, pal.center.y)) }
        drawLine(n(Color(0xFFC9B27A), o), Offset(pal.left + pal.width * 0.3f, pal.center.y), Offset(pal.right - pal.width * 0.05f, pal.center.y), p * 0.8f)
        // Una lámpara de barro encendida, a la derecha.
        val lx = w * 0.72f
        drawOval(Brush.verticalGradient(listOf(n(TERRACOTA, o), n(BARRO, o)), repisa - 2.6f * u, repisa), Offset(lx - 3f * u, repisa - 2.4f * u), Size(6f * u, 2.4f * u))
        llama(Offset(lx + 2.2f * u, repisa - 2.6f * u), 1f * u, o)
    }

    /** Un jeroglífico sencillo (agua, boca, pan, junco, ojo, sol, ankh) del tamaño [s]. */
    private fun DrawScope.signo(k: Int, o: Offset, s: Float, color: Color, p: Float) {
        when (((k % 7) + 7) % 7) {
            0 -> drawPath(Path().apply { moveTo(o.x - s / 2, o.y); for (i in 1..6) lineTo(o.x - s / 2 + s * i / 6, o.y + (if (i % 2 == 0) 0f else -s * 0.2f)) }, color, style = Stroke(p * 0.8f))
            1 -> drawOval(color, Offset(o.x - s * 0.4f, o.y - s * 0.18f), Size(s * 0.8f, s * 0.36f))
            2 -> drawArc(color, 180f, 180f, true, Offset(o.x - s * 0.35f, o.y - s * 0.3f), Size(s * 0.7f, s * 0.6f))
            3 -> { drawLine(color, Offset(o.x, o.y + s * 0.4f), Offset(o.x, o.y - s * 0.3f), p); drawOval(color, Offset(o.x - s * 0.1f, o.y - s * 0.5f), Size(s * 0.24f, s * 0.3f)) }
            4 -> { drawOval(color, Offset(o.x - s * 0.4f, o.y - s * 0.15f), Size(s * 0.8f, s * 0.3f), style = Stroke(p * 0.8f)); drawCircle(color, s * 0.1f, o) }
            5 -> { drawCircle(color, s * 0.25f, o, style = Stroke(p * 0.9f)); drawCircle(color, s * 0.06f, o) }
            else -> { drawOval(color, Offset(o.x - s * 0.15f, o.y - s * 0.5f), Size(s * 0.3f, s * 0.35f), style = Stroke(p * 0.9f)); drawLine(color, Offset(o.x, o.y - s * 0.15f), Offset(o.x, o.y + s * 0.45f), p); drawLine(color, Offset(o.x - s * 0.3f, o.y - s * 0.08f), Offset(o.x + s * 0.3f, o.y - s * 0.08f), p) }
        }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val semilla = e.indice * 131 + e.tipo.length * 7
        val caliza = e.tipo == "idea" || e.tipo == "pensamiento"
        val forma = if (caliza) lasca(r.deflate(1.5f * un), semilla) else hojaPapiro(r.deflate(1.5f * un), semilla)
        translate(1.2f * un, 2.2f * un) { drawPath(forma, Color.Black.copy(alpha = 0.32f)) }
        if (caliza) {
            // Un óstracon de caliza: una lasca clara y plana, con el canto biselado (claro arriba, gris abajo) y algunos poros.
            val cal = if (o) CALIZA_OSCURA else CALIZA_CLARA
            drawPath(forma, Brush.linearGradient(listOf(mez(cal, Color.White, 0.1f), cal, mez(cal, Color.Black, 0.06f)), r.topLeft, r.bottomRight))
            clipPath(forma) {
                for (k in 0 until 16) drawCircle(mez(cal, Color.Black, 0.2f).copy(alpha = 0.35f), (0.3f + 0.6f * azar(semilla, k)) * un, Offset(r.left + r.width * azar(semilla, k + 40), r.top + r.height * azar(semilla, k + 80)))
                translate(-1.2f * un, -1.6f * un) { drawPath(forma, mez(cal, Color.Black, if (o) 0.35f else 0.22f), style = Stroke(3.2f * un)) }
                translate(1f * un, 1.2f * un) { drawPath(forma, mez(cal, Color.White, if (o) 0.12f else 0.5f), style = Stroke(2.4f * un)) }
            }
            drawPath(forma, mez(cal, Color.Black, 0.35f), style = Stroke(0.7f * un, join = StrokeJoin.Round))
        } else {
            // Una hoja de papiro: las fibras horizontales y verticales, el canto derecho deshilachado.
            val pap = if (o) PAPIRO_OSCURO else PAPIRO_CLARO
            drawPath(forma, vert(r, mez(pap, Color.White, 0.12f), pap, mez(pap, Color.Black, 0.08f)))
            clipPath(forma) {
                var y = r.top + 2f * un
                while (y < r.bottom) { drawLine(mez(pap, Color.Black, 0.12f).copy(alpha = 0.5f), Offset(r.left, y), Offset(r.right, y), 0.5f * un); y += 3.2f * un }
                listOf(0.33f, 0.66f).forEach { f -> drawLine(mez(pap, Color.Black, 0.15f).copy(alpha = 0.4f), Offset(r.left + r.width * f, r.top), Offset(r.left + r.width * f, r.bottom), 1.2f * un) }
            }
            drawPath(forma, mez(pap, Color.Black, 0.3f), style = Stroke(0.7f * un, join = StrokeJoin.Round))
        }
        // La rúbrica roja, arriba (fuera del texto).
        val rojo = if (o) Color(0xFFE07A50) else CORNALINA
        drawLine(rojo, Offset(r.left + r.width * 0.14f, r.top + r.height * 0.1f), Offset(r.left + r.width * (if (e.fijada) 0.38f else 0.55f), r.top + r.height * 0.1f), 1.6f * un, StrokeCap.Round)
        // El signo del tipo, en la esquina de abajo a la derecha.
        val q = Offset(r.right - r.width * 0.11f, r.bottom - r.height * 0.09f)
        val t = r.height * 0.05f
        val tinta = if (o) Color(0xFF9DB2F0) else LAPIS
        when (e.tipo) {
            "idea" -> { drawCircle(rojo, t * 0.8f, q); drawCircle(if (o) ORO_LUZ else ORO, t * 0.8f, q, style = Stroke(0.6f * un)) }
            "pensamiento" -> {
                drawOval(tinta, Offset(q.x - t, q.y - t * 0.4f), Size(t * 2, t * 0.8f), style = Stroke(0.7f * un))
                drawCircle(tinta, t * 0.3f, q)
                drawLine(tinta, Offset(q.x - t * 0.2f, q.y + t * 0.4f), Offset(q.x - t * 0.5f, q.y + t * 1.1f), 0.7f * un)
            }
            "proposito" -> {
                drawOval(tinta, Offset(q.x - t * 0.4f, q.y - t * 1.2f), Size(t * 0.8f, t * 0.9f), style = Stroke(0.8f * un))
                drawLine(tinta, Offset(q.x, q.y - t * 0.3f), Offset(q.x, q.y + t * 1.1f), 0.9f * un)
                drawLine(tinta, Offset(q.x - t * 0.7f, q.y - t * 0.2f), Offset(q.x + t * 0.7f, q.y - t * 0.2f), 0.9f * un)
            }
            else -> {
                drawCircle(tinta, t * 0.65f, Offset(q.x, q.y - t * 0.3f), style = Stroke(0.9f * un))
                drawLine(tinta, Offset(q.x - t * 0.8f, q.y + t * 0.5f), Offset(q.x + t * 0.8f, q.y + t * 0.5f), 0.9f * un)
            }
        }
        // Fijada: un sello de barro con su cordel de lino sobre el borde de arriba.
        if (e.fijada) {
            val sc = Offset(r.left + r.width * 0.62f, r.top + r.height * 0.07f)
            drawLine(if (o) LINO_OSC else LINO, Offset(sc.x - 5f * un, r.top - 1f * un), Offset(sc.x + 1f * un, sc.y), 0.9f * un)
            drawLine(if (o) LINO_OSC else LINO, Offset(sc.x + 5f * un, r.top - 1f * un), Offset(sc.x - 1f * un, sc.y), 0.9f * un)
            drawOval(Color.Black.copy(alpha = 0.35f), Offset(sc.x - 4.2f * un + 0.8f * un, sc.y - 3.4f * un + 1.2f * un), Size(8.4f * un, 6.8f * un))
            drawOval(Brush.radialGradient(listOf(mez(BARRO, Color.White, 0.25f), BARRO, mez(BARRO, Color.Black, 0.3f)), Offset(sc.x - un, sc.y - un), 5f * un), Offset(sc.x - 4.2f * un, sc.y - 3.4f * un), Size(8.4f * un, 6.8f * un))
            drawOval(mez(BARRO, Color.Black, 0.4f), Offset(sc.x - 2.4f * un, sc.y - 1.8f * un), Size(4.8f * un, 3.6f * un), style = Stroke(0.5f * un))
            drawOval(mez(BARRO, Color.Black, 0.35f), Offset(sc.x - 1.2f * un, sc.y - 1.2f * un), Size(2.4f * un, 2.4f * un))
        }
    }

    /** Una lasca de caliza: un contorno casi rectangular de esquinas romas y canto desportillado, dentro de [r]. */
    private fun lasca(r: Rect, semilla: Int): Path {
        val rnd = Random(semilla.toLong() * 7919L + 31L)
        val path = Path()
        val n = 28
        val c = r.center
        for (k in 0 until n) {
            val ang = 2 * Math.PI * k / n
            val cx = cos(ang).toFloat(); val cy = sin(ang).toFloat()
            // Un «superelipse»: casi rectangular, con las esquinas redondas y alguna muesca.
            val f = 1f / Math.pow((Math.pow(kotlin.math.abs(cx).toDouble(), 5.0) + Math.pow(kotlin.math.abs(cy).toDouble(), 5.0)), 0.2).toFloat()
            val q = 1f - 0.035f * rnd.nextFloat() - (if (k % 7 == 3) 0.03f else 0f)
            val x = c.x + r.width / 2 * cx * f * q; val y = c.y + r.height / 2 * cy * f * q
            if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    /** Una hoja de papiro: casi rectangular, con el borde derecho deshilachado. */
    private fun hojaPapiro(r: Rect, semilla: Int): Path {
        val rnd = Random(semilla.toLong() * 104729L + 7L)
        val path = Path()
        path.moveTo(r.left, r.top + 0.5f)
        path.lineTo(r.right - r.width * 0.03f, r.top)
        val pasos = 10
        for (k in 1..pasos) path.lineTo(r.right - r.width * 0.035f * rnd.nextFloat(), r.top + r.height * k / pasos)
        path.lineTo(r.left + r.width * 0.01f, r.bottom - 0.5f)
        path.close()
        return path
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E4C0) else Color(0xFF231A0E)
}
