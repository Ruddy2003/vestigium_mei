package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.TrazosNouveau
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Art nouveau: Vida en el rincón del taller de litografía, en París hacia 1900 (`docs/diseno/temas/nouveau.md`; el mismo
 * mundo que su biblioteca de nogal y su libreta sobre la mesa de dibujo). Muros de yeso crema con el zócalo de tablas
 * pintadas de verde salvia, piso de roble con sus manchas de tinta, el tragaluz de hierro y vidrio por donde entra la luz
 * de París, los pliegos que se secan colgados con pinzas, la prensa litográfica de mano con su rodillo, las piedras de caliza
 * apiladas, los frascos de tinta, la lámpara de vidrio de Nancy y la planta en su maceta; por la ventana, los tejados de
 * mansarda. Lo que se imprime es propio (ningún cartel real) y no hay personas dibujadas. La luz viene de arriba a la
 * izquierda y la sombra es violeta (`ui/muebles/Luz.kt`, versión sutil). De noche (modo oscuro) es el mismo taller en el
 * azul negro de la calle: la luz cálida solo llega cerca de la lámpara de vidrio; por el tragaluz se ve la luna y, por la
 * ventana, las luces eléctricas de la ciudad.
 * - Social: el fondo del taller bajo el tragaluz. El pliego grande de papel vélin colgado a secar de la cuerda con sus
 *   pinzas (el nicho: tu gente va en el papel); a la derecha, un cartel a medio imprimir (ya pasaron las piedras del iris
 *   y del tallo; el oro y las letras esperan); a la izquierda, la ventana con los tejados de París y la planta en su
 *   maceta de loza, y debajo el velador de mármol con la lámpara de vidrio, las tazas y la silla de madera curvada. Al
 *   frente, la prensa de mano con su rodillo sobre la piedra, el tímpano levantado y la estrella de cinco brazos; al pie
 *   del pliego, las piedras apiladas en su tarima, la resma atada y el balde con la esponja.
 * - Mapas: la mesa de dibujo inclinada vista de frente: el tablero de tilo en su marco de nogal, el pliego sujeto con
 *   chinchetas (el nicho); a la izquierda, el estudio de iris y la tira de las cuatro tintas; a la derecha, la lámpara
 *   de tulipa en su brazo de latón que se curva como un tallo, el calco con la línea de látigo y el compás colgado.
 *   Abajo, el canto con las barras de lápiz litográfico, el pincel, el tiralíneas, la regla en T, los frascos de tinta y el
 *   vaso de los pinceles; el faldón tallado, la cremallera de la inclinación y las patas sobre el piso.
 * - Otras vidas: el casillero de las piedras: cinco casillas de roble con una piedra de caliza graneada en el fondo de
 *   cada una y la boca en arco (el nicho); en el friso, dos líneas de látigo talladas que nacen de una flor; encima, las
 *   piedras apiladas, el rodillo sobre su losa de entintar, los frascos de tinta y la lámpara de vidrio de Nancy; arriba
 *   de todo, la viga del tragaluz con sus ménsulas; abajo, los portaetiquetas, los cajones con tiradores de hoja y el piso.
 * - Íconos: el Grand Palais de la Exposición de 1900 con su nave y su cúpula de hierro y vidrio sobre el Sena; una piedra
 *   entintada que el rascador de litógrafo raspa hasta descubrir el Sena; y una prueba con el plano de París y tres
 *   chinchetas de flor unidas por el camino.
 * - Notas: el tablero del taller, de sarga verde en su marco de nogal tallado como la biblioteca, con el iris en la
 *   cresta, la repisa del tintero y del florero de Nancy, y la tulipa que cuelga arriba a la derecha. Cada nota es una
 *   tarjeta de papel vélin prendida con una chincheta de latón en flor; su tipo es una viñeta impresa en la esquina, en el lenguaje de las flores (la tulipa encendida, el pensamiento, el
 *   brote, el nomeolvides) y, si está fijada, la prende un alfiler de vidrio de Nancy y lleva un filete de oro.
 */
internal object VidaNouveau : DisenoVida {
    // ---------------------------------------------------------------- La paleta del taller
    private val HIERRO = TrazosNouveau.HIERRO
    private val HIERRO_SOMBRA = TrazosNouveau.HIERRO_SOMBRA
    private val TALLO = TrazosNouveau.TALLO
    private val TALLO_SOMBRA = TrazosNouveau.TALLO_SOMBRA
    private val IRIS = TrazosNouveau.IRIS
    private val IRIS_LENGUA = TrazosNouveau.IRIS_LENGUA
    private val MALVA = TrazosNouveau.MALVA
    private val ORO = TrazosNouveau.ORO
    private val VIDRIO_AMBAR = TrazosNouveau.VIDRIO_AMBAR
    private val VIDRIO_VIOLETA = TrazosNouveau.VIDRIO_VIOLETA
    private val VIDRIO_VERDE = TrazosNouveau.VIDRIO_VERDE
    private val PIEDRA = TrazosNouveau.PIEDRA
    private val TINTA_AZUL = TrazosNouveau.TINTA_AZUL
    private val LUZ = TrazosNouveau.LUZ
    private val LUZ_HALO = TrazosNouveau.LUZ_HALO
    private val FAROL = TrazosNouveau.FAROL
    private val PLUMA = TrazosNouveau.PLUMA

    /** El yeso crema de los muros, su luz y su sombra. */
    private val YESO = Color(0xFFE4D9BF)
    private val YESO_LUZ = Color(0xFFF1E9D6)
    private val YESO_SOMBRA = Color(0xFFBDAE8E)
    /** Las tablas del zócalo, pintadas de verde salvia. */
    private val ZOCALO = Color(0xFF93A383)
    private val ZOCALO_LUZ = Color(0xFFB4C2A4)
    private val ZOCALO_OSC = Color(0xFF63765A)
    /** El nogal de los muebles (el mismo de la biblioteca), su luz, su sombra y su veta. */
    private val NOGAL = Color(0xFF6E4C33)
    private val NOGAL_LUZ = Color(0xFF9A7350)
    private val NOGAL_OSC = Color(0xFF3F2A1C)
    private val VETA = Color(0xFF2C1D13)
    /** El roble del piso, del casillero y de la tarima. */
    private val ROBLE = Color(0xFFA27D52)
    private val ROBLE_LUZ = Color(0xFFC6A273)
    private val ROBLE_OSC = Color(0xFF6E5034)
    /** El tilo claro del tablero de dibujo. */
    private val TILO = Color(0xFFD8C29A)
    /** El papel vélin de los pliegos y su sombra. */
    private val VELIN = Color(0xFFF2EAD5)
    private val VELIN_OSC = Color(0xFFD6C8A8)
    /** La piedra de caliza: su cara pulida, su canto en capas y su sombra. */
    private val PIEDRA_LUZ = Color(0xFFE0DACA)
    private val PIEDRA_CANTO = Color(0xFFB5AA90)
    private val PIEDRA_OSC = Color(0xFF8C826C)
    /** El latón de las chinchetas, los tiradores y la virola. */
    private val LATON = Color(0xFFC09A52)
    private val LATON_LUZ = Color(0xFFEAD08E)
    private val LATON_OSC = Color(0xFF7A5A26)
    /** La haya de las pinzas y los mangos. */
    private val HAYA = Color(0xFFD7B98A)
    private val HAYA_OSC = Color(0xFF9C7C50)
    /** El cuero del rodillo, entintado de negro. */
    private val CUERO = Color(0xFF6A4630)
    private val TINTA_NEGRA = Color(0xFF221E26)
    /** El mármol del velador. */
    private val MARMOL = Color(0xFFE9E3D6)
    private val MARMOL_OSC = Color(0xFFBDB4A2)
    /** La loza vidriada de la maceta, verde de pavo real. */
    private val LOZA = Color(0xFF3F7468)
    private val LOZA_LUZ = Color(0xFF6FA394)
    /** La sarga verde del tablero de notas. */
    private val SARGA = Color(0xFF6E8466)
    /** París: el cielo de día, sus nubes, la piedra de las fachadas, el zinc de las mansardas y las chimeneas. */
    private val CIELO = Color(0xFFC6D6DA)
    private val CIELO_ALTO = Color(0xFFA9C2CC)
    private val NUBE = Color(0xFFF5F1E6)
    private val FACHADA = Color(0xFFDCCFB2)
    private val ZINC = Color(0xFF8A98A2)
    private val ZINC_OSC = Color(0xFF5D6A74)
    private val TEJA = Color(0xFFB8704A)
    /** La noche de París por el vidrio, la luna y su luz fría. */
    private val CIELO_NOCHE = Color(0xFF141B2A)
    private val CIELO_NOCHE_BAJO = Color(0xFF223047)
    private val LUNA = Color(0xFFF2EDD9)

    /** El aire de la noche: lo que multiplica cada color, y el azul negro de la calle hacia el que se hunde. */
    private val AIRE = Color(0xFF6A7388)
    private val NOCHE = Color(0xFF121519)
    /** Lo que alumbra la lámpara de vidrio. */
    private val CALIDO = Color(0xFFE3A653)

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color de noche: multiplicado por el aire y hundido en el azul negro de la calle; de día, tal cual. */
    private fun n(c: Color, o: Boolean): Color {
        if (!o) return c
        return mez(Color(c.red * AIRE.red, c.green * AIRE.green, c.blue * AIRE.blue, c.alpha), NOCHE.copy(alpha = c.alpha), 0.58f)
    }

    /** El color de noche a la luz de la lámpara: [f] (0 a 1) es cuánto le llega. De día, tal cual. */
    private fun lam(c: Color, o: Boolean, f: Float): Color = if (!o || f <= 0f) n(c, o) else mez(n(c, o), mez(c, CALIDO, 0.3f), f.coerceIn(0f, 1f) * 0.82f)

    /** Cuánto le llega la lámpara (en [foco], con alcance [radio]) a un punto [p]: 1 al lado, 0 lejos. */
    private fun cerca(p: Offset, foco: Offset, radio: Float): Float {
        val dx = p.x - foco.x; val dy = p.y - foco.y
        val t = 1f - sqrt(dx * dx + dy * dy) / radio
        return (t * t * (3f - 2f * t)).coerceIn(0f, 1f).let { if (t <= 0f) 0f else it }
    }

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, o: Boolean, material: Color): Color =
        if (o) mez(e.fondo, n(material, o), 0.24f) else mez(e.fondo, material, 0.34f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)

    /** Un número estable de 0 a 1. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** El halo de una luz: discos que se suman hacia el centro, sin degradado. */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color, fuerza: Float, pasos: Int = 24) {
        val a = 0.3f / pasos
        for (k in 0 until pasos) {
            val t = k / pasos.toFloat()
            drawCircle(color.copy(alpha = (a * fuerza * (1f + 2f * t)).coerceIn(0f, 1f)), r * (1f - t) * (1f - 0.15f * t), c)
        }
    }

    /** Todo lo que se dibuja dentro de [bloque], menos lo que caería sobre [nicho]. */
    private fun DrawScope.fueraDe(nicho: Rect, bloque: DrawScope.() -> Unit) {
        val p = Path().apply { addRect(Rect(-size.width, -size.height, size.width * 2f, size.height * 2f)); addRect(nicho); fillType = PathFillType.EvenOdd }
        clipPath(p) { bloque() }
    }

    /** La sombra blanda de una pieza sobre lo que tiene detrás, corrida abajo a la derecha. */
    private fun DrawScope.sombraPieza(r: Rect, alcance: Float, fuerza: Float, radio: Float = 0f) {
        for (k in 3 downTo 1) {
            val d = alcance * k / 3f
            drawRoundRect(sombraMueble(fuerza / 3f), Offset(r.left + d * 0.5f, r.top + d * 0.6f), Size(r.width + d * 0.2f, r.height + d * 0.15f), CornerRadius(radio + d))
        }
    }

    /** Una curva de Bézier cúbica como lista de puntos. */
    private fun bezier(a: Offset, b: Offset, c: Offset, d: Offset, n: Int): List<Offset> = TrazosNouveau.bezier(a, b, c, d, n)

    /** Una cinta de ancho variable a lo largo de [pts]. */
    private fun cinta(pts: List<Offset>, ancho: (Float) -> Float): Path = TrazosNouveau.cinta(pts, ancho)

    // ---------------------------------------------------------------- Los materiales del taller

    /**
     * Madera con su veta larga y ondulada: [base] con sus [luz], [osc] y la veta oscura; [vertical] para los montantes,
     * [f] es cuánto le llega la lámpara de noche.
     */
    private fun DrawScope.madera(r: Rect, o: Boolean, u: Float, semilla: Int, base: Color = NOGAL, osc: Color = NOGAL_OSC, luz: Color = NOGAL_LUZ,
                                 vertical: Boolean = false, f: Float = 0f, veta: Color = VETA) {
        rect(r, lam(base, o, f))
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        clipRect(r.left, r.top, r.right, r.bottom) {
            val lineas = (ancho / (1.1f * u)).toInt().coerceIn(2, 50)
            for (k in 0 until lineas) {
                val b = ancho * (k + azar(semilla, k)) / lineas
                val amp = (0.2f + 0.7f * azar(semilla, k + 50)) * u
                val fase = azar(semilla, k + 90) * 6.28f
                val ondas = 0.5f + 1.4f * azar(semilla, k + 130)
                val path = Path()
                for (s in 0..16) {
                    val t = largo * s / 16
                    val v = b + amp * sin(fase + ondas * 6.28f * s / 16)
                    val x = if (vertical) r.left + v else r.left + t
                    val y = if (vertical) r.top + t else r.top + v
                    if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                val oscura = azar(semilla, k + 170) < 0.3f
                drawPath(path, lam(if (oscura) veta else osc, o, f).copy(alpha = if (oscura) 0.36f else 0.26f), style = Stroke((0.12f + 0.2f * azar(semilla, k + 210)) * u))
            }
            pinceladas(r, lam(luz, o, f), semilla + 5, (largo * ancho / (90f * u * u)).toInt().coerceIn(2, 30), u * 0.45f, if (o) 0.05f else 0.09f)
        }
    }

    /** Un listón de madera con cuerpo: la cara con su veta, el canto de arriba en luz y el de abajo en sombra. */
    private fun DrawScope.liston(r: Rect, o: Boolean, u: Float, semilla: Int, base: Color = NOGAL, osc: Color = NOGAL_OSC, luz: Color = NOGAL_LUZ,
                                 vertical: Boolean = false, f: Float = 0f) {
        madera(r, o, u, semilla, base, osc, luz, vertical, f)
        val g = minOf(0.5f * u, (if (vertical) r.width else r.height) * 0.22f)
        if (vertical) {
            drawRect(luzMueble(if (o) 0.06f + 0.25f * f else 0.38f), r.topLeft, Size(g, r.height))
            drawRect(sombraMueble(if (o) 0.35f else 0.3f), Offset(r.right - g, r.top), Size(g, r.height))
        } else {
            drawRect(luzMueble(if (o) 0.06f + 0.25f * f else 0.38f), r.topLeft, Size(r.width, g))
            drawRect(sombraMueble(if (o) 0.35f else 0.3f), Offset(r.left, r.bottom - g), Size(r.width, g))
        }
    }

    /** El muro de yeso crema: pinceladas de luz y de sombra, alguna mancha de humedad y la huella de un dedo entintado. */
    private fun DrawScope.yeso(r: Rect, o: Boolean, u: Float, semilla: Int) {
        rect(r, n(YESO, o))
        pinceladas(r, n(YESO_LUZ, o), semilla, (r.width * r.height / (70f * u * u)).toInt().coerceIn(6, 70), u, if (o) 0.06f else 0.12f)
        pinceladas(r, n(YESO_SOMBRA, o), semilla + 11, (r.width * r.height / (110f * u * u)).toInt().coerceIn(4, 50), u, if (o) 0.06f else 0.08f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until 3) {
                val c = Offset(r.left + r.width * azar(semilla, k + 300), r.top + r.height * (0.2f + 0.7f * azar(semilla, k + 310)))
                val rr = (3f + 5f * azar(semilla, k + 320)) * u
                drawOval(n(YESO_SOMBRA, o).copy(alpha = 0.12f), Offset(c.x - rr, c.y - rr * 0.7f), Size(rr * 2f, rr * 1.4f))
                drawOval(n(YESO_SOMBRA, o).copy(alpha = 0.1f), Offset(c.x - rr * 0.6f, c.y - rr * 0.3f), Size(rr * 1.3f, rr * 0.9f), style = Stroke(0.25f * u))
            }
        }
    }

    /** El zócalo de tablas machihembradas pintadas de verde salvia, con su moldura de remate. */
    private fun DrawScope.zocalo(r: Rect, o: Boolean, u: Float, semilla: Int) {
        rect(r, n(ZOCALO, o))
        var x = r.left + 2.2f * u * azar(semilla, 1)
        var k = 0
        while (x < r.right) {
            drawLine(n(ZOCALO_OSC, o).copy(alpha = 0.7f), Offset(x, r.top + 1.6f * u), Offset(x, r.bottom), 0.3f * u)
            drawLine(n(ZOCALO_LUZ, o).copy(alpha = 0.6f), Offset(x + 0.35f * u, r.top + 1.6f * u), Offset(x + 0.35f * u, r.bottom), 0.2f * u)
            if (azar(semilla, k + 20) < 0.5f) pinceladas(Rect(x, r.top, x + 4.4f * u, r.bottom), n(ZOCALO_LUZ, o), semilla + k, 2, u * 0.5f, 0.12f)
            x += 4.4f * u; k++
        }
        // La moldura de arriba: un cordón en luz sobre un filete en sombra.
        val m = Rect(r.left, r.top, r.right, r.top + 1.6f * u)
        rect(m, n(ZOCALO_OSC, o))
        drawRect(n(ZOCALO_LUZ, o), m.topLeft, Size(m.width, 0.8f * u))
        drawRect(luzMueble(if (o) 0.04f else 0.3f), m.topLeft, Size(m.width, 0.3f * u))
        drawRect(sombraMueble(if (o) 0.3f else 0.18f), Offset(r.left, m.bottom), Size(r.width, 0.7f * u))
        // Lo despintado: unos raspones claros abajo, donde pegan los pies.
        for (j in 0 until 6) {
            val px = r.left + r.width * azar(semilla, j + 60)
            drawLine(n(YESO_LUZ, o).copy(alpha = 0.25f), Offset(px, r.bottom - (1f + 2f * azar(semilla, j + 70)) * u), Offset(px + (1.5f + 2f * azar(semilla, j + 80)) * u, r.bottom - 0.8f * u), 0.3f * u, StrokeCap.Round)
        }
    }

    /** El piso de tablas de roble que se alejan, con sus juntas, su veta y alguna mancha de tinta negra. */
    private fun DrawScope.piso(r: Rect, o: Boolean, u: Float, semilla: Int) {
        rect(r, n(ROBLE, o))
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las tablas, cada vez más anchas hacia delante.
            var y = r.top
            var k = 0
            var alto = 1.6f * u
            while (y < r.bottom) {
                val tabla = Rect(r.left, y, r.right, y + alto)
                val tono = mez(ROBLE, if (k % 2 == 0) ROBLE_LUZ else ROBLE_OSC, 0.12f + 0.12f * azar(semilla, k))
                rect(tabla, n(tono, o))
                pinceladas(tabla, n(ROBLE_LUZ, o), semilla + k, 6, u * 0.5f, if (o) 0.05f else 0.1f)
                drawLine(n(ROBLE_OSC, o), Offset(r.left, y), Offset(r.right, y), 0.28f * u)
                // Las juntas de testa, al tresbolillo.
                var xj = r.left + r.width * azar(semilla, k + 40) * 0.4f
                while (xj < r.right) { drawLine(n(ROBLE_OSC, o).copy(alpha = 0.8f), Offset(xj, y), Offset(xj, y + alto), 0.22f * u); xj += r.width * (0.3f + 0.2f * azar(semilla, k + 60)) }
                y += alto; alto *= 1.22f; k++
            }
            // Manchas de tinta negra: el taller las deja en el piso.
            for (j in 0 until 4) {
                val c = Offset(r.left + r.width * azar(semilla, j + 90), r.top + r.height * (0.3f + 0.65f * azar(semilla, j + 100)))
                val rr = (0.6f + 1.4f * azar(semilla, j + 110)) * u
                drawOval(n(TINTA_NEGRA, o).copy(alpha = 0.32f), Offset(c.x - rr * 1.6f, c.y - rr * 0.5f), Size(rr * 3.2f, rr))
                drawCircle(n(TINTA_NEGRA, o).copy(alpha = 0.3f), rr * 0.3f, Offset(c.x + rr * 2.2f, c.y + rr * 0.1f))
            }
        }
        drawRect(sombraMueble(if (o) 0.4f else 0.24f), r.topLeft, Size(r.width, 1f * u))
    }

    /**
     * El tragaluz de hierro y vidrio visto desde abajo: el cielo de París por los vidrios (de día, nubes claras; de noche, la
     * luna y las estrellas), los cabios de hierro verde de Guimard que suben y se juntan, la correa del medio y la viga de
     * abajo con sus remaches; al pie de cada cabio, una ménsula en línea de látigo que baja al muro. [luna] es dónde va la
     * luna (fracción del ancho).
     */
    private fun DrawScope.tragaluz(r: Rect, o: Boolean, u: Float, semilla: Int, paso: Float, luna: Float = 0.7f) {
        val cielo = Path().apply { addRect(r) }
        clipPath(cielo) {
            if (o) {
                rect(r, CIELO_NOCHE)
                rect(Rect(r.left, r.top + r.height * 0.55f, r.right, r.bottom), mez(CIELO_NOCHE, CIELO_NOCHE_BAJO, 0.5f))
                for (k in 0 until 40) drawCircle(LUNA.copy(alpha = 0.25f + 0.55f * azar(semilla, k)), u * (0.1f + 0.16f * azar(semilla, k + 20)),
                    Offset(r.left + r.width * azar(semilla, k + 40), r.top + r.height * azar(semilla, k + 60)))
                val lc = Offset(r.left + r.width * luna, r.top + r.height * 0.42f)
                halo(lc, r.height * 0.9f, LUNA, 0.5f)
                drawCircle(LUNA, r.height * 0.2f, lc)
                drawCircle(mez(LUNA, CIELO_NOCHE, 0.18f), r.height * 0.06f, Offset(lc.x + r.height * 0.05f, lc.y - r.height * 0.04f))
                drawCircle(mez(LUNA, CIELO_NOCHE, 0.12f), r.height * 0.04f, Offset(lc.x - r.height * 0.07f, lc.y + r.height * 0.06f))
                // Nubes de noche, con el filo que les pone la luna.
                for (k in 0 until 4) {
                    val c = Offset(r.left + r.width * azar(semilla, k + 80), r.top + r.height * (0.25f + 0.6f * azar(semilla, k + 90)))
                    val an = r.width * (0.08f + 0.08f * azar(semilla, k + 100))
                    drawOval(mez(CIELO_NOCHE, CIELO_NOCHE_BAJO, 0.9f).copy(alpha = 0.8f), Offset(c.x - an, c.y - r.height * 0.08f), Size(an * 2f, r.height * 0.16f))
                    drawLine(LUNA.copy(alpha = 0.12f), Offset(c.x - an * 0.6f, c.y - r.height * 0.075f), Offset(c.x + an * 0.4f, c.y - r.height * 0.075f), 0.3f * u, StrokeCap.Round)
                }
            } else {
                rect(r, CIELO_ALTO)
                rect(Rect(r.left, r.top + r.height * 0.35f, r.right, r.bottom), mez(CIELO_ALTO, CIELO, 0.6f))
                rect(Rect(r.left, r.top + r.height * 0.7f, r.right, r.bottom), CIELO)
                // Nubes de gouache: óvalos claros con la panza en sombra.
                for (k in 0 until 7) {
                    val c = Offset(r.left + r.width * azar(semilla, k + 80), r.top + r.height * (0.2f + 0.65f * azar(semilla, k + 90)))
                    val an = r.width * (0.05f + 0.07f * azar(semilla, k + 100))
                    drawOval(mez(NUBE, CIELO, 0.45f), Offset(c.x - an, c.y - r.height * 0.02f), Size(an * 2f, r.height * 0.2f))
                    drawOval(NUBE, Offset(c.x - an * 0.8f, c.y - r.height * 0.1f), Size(an * 1.5f, r.height * 0.2f))
                    drawOval(NUBE, Offset(c.x - an * 0.2f, c.y - r.height * 0.16f), Size(an * 0.9f, r.height * 0.2f))
                }
            }
            // El vidrio: un velo verde apenas y los reflejos en diagonal.
            rect(r, VIDRIO_VERDE.copy(alpha = if (o) 0.05f else 0.1f))
        }
        // Los cabios: suben y se juntan hacia lo alto (el tejado se va alejando).
        val fuga = Offset(r.center.x, r.top - r.height * 3.2f)
        val hierro = n(HIERRO, o); val hierroS = n(HIERRO_SOMBRA, o); val hierroL = n(mez(HIERRO, Color.White, 0.25f), o)
        var x = r.left + paso * 0.5f
        var k = 0
        while (x < r.right + paso) {
            val arriba = Offset(x + (fuga.x - x) * 0.24f, r.top)
            // Los reflejos del vidrio en el paño de la izquierda.
            drawLine((if (o) LUNA else Color.White).copy(alpha = if (o) 0.06f else 0.22f), Offset(x - paso * 0.32f, r.bottom - r.height * 0.2f), Offset(x - paso * 0.12f + (fuga.x - x) * 0.1f, r.top + r.height * 0.2f), 0.5f * u, StrokeCap.Round)
            drawLine(hierroS, Offset(x + 0.3f * u, r.bottom), Offset(arriba.x + 0.3f * u, arriba.y), 1.3f * u)
            drawLine(hierro, Offset(x, r.bottom), arriba, 1.0f * u)
            drawLine(hierroL, Offset(x - 0.35f * u, r.bottom), Offset(arriba.x - 0.35f * u, arriba.y), 0.25f * u)
            x += paso; k++
        }
        // La correa del medio, que cruza los cabios.
        val yc = r.top + r.height * 0.42f
        drawLine(hierroS, Offset(r.left, yc + 0.35f * u), Offset(r.right, yc + 0.35f * u), 0.9f * u)
        drawLine(hierro, Offset(r.left, yc), Offset(r.right, yc), 0.6f * u)
        // La viga de abajo, con sus remaches.
        val v = Rect(r.left, r.bottom - 2.2f * u, r.right, r.bottom)
        rect(v, hierro)
        drawRect(hierroL, v.topLeft, Size(v.width, 0.45f * u))
        drawRect(hierroS, Offset(v.left, v.bottom - 0.6f * u), Size(v.width, 0.6f * u))
        var xr = r.left + 1.6f * u
        while (xr < r.right) { drawCircle(hierroS, 0.32f * u, Offset(xr, v.center.y)); drawCircle(hierroL, 0.14f * u, Offset(xr - 0.1f * u, v.center.y - 0.1f * u)); xr += 3.2f * u }
        drawRect(sombraMueble(if (o) 0.4f else 0.25f), Offset(r.left, r.bottom), Size(r.width, 1.4f * u))
        // Las ménsulas en látigo: bajan de la viga al muro bajo cada cabio.
        with(TrazosNouveau) {
            var xm = r.left + paso * 0.5f
            while (xm < r.right + paso) {
                latigo(Offset(xm, r.bottom), Offset(xm - 2.2f * u, r.bottom + 5.4f * u), 1.1f * u, 1.1f * u, hierro, comba = 0.22f, horario = false)
                drawCircle(hierroS, 0.4f * u, Offset(xm, r.bottom + 0.3f * u))
                xm += paso
            }
        }
    }

    /**
     * Los rayos de luz que bajan del tragaluz y cruzan el muro hacia abajo a la derecha: de día, crema; de noche, la luz fría
     * y muy tenue de la luna, solo desde su paño.
     */
    private fun DrawScope.rayos(r: Rect, o: Boolean, u: Float, paso: Float, luna: Float = 0.7f) {
        if (o) {
            val x = r.left + r.width * luna
            drawPath(Path().apply {
                moveTo(x - paso * 0.4f, r.top); lineTo(x + paso * 0.4f, r.top)
                lineTo(x + paso * 0.4f + r.height * 0.5f, r.bottom); lineTo(x - paso * 0.4f + r.height * 0.5f, r.bottom); close()
            }, LUNA.copy(alpha = 0.035f))
            return
        }
        var x = r.left + paso * 0.1f
        var k = 0
        while (x < r.right) {
            val a = if (k % 3 == 1) 0.12f else 0.07f
            drawPath(Path().apply {
                moveTo(x, r.top); lineTo(x + paso * 0.7f, r.top)
                lineTo(x + paso * 0.7f + r.height * 0.55f, r.bottom); lineTo(x + r.height * 0.55f, r.bottom); close()
            }, luzMueble(a))
            x += paso * 1.6f; k++
        }
    }

    /**
     * Lo que se ve por la ventana: los tejados de mansarda de París. De día, el cielo con nubes, las fachadas de piedra con
     * sus ventanas, las mansardas de zinc con sus buhardillas y las chimeneas con sus cañones de barro; de noche, la misma
     * ciudad con las ventanas y las buhardillas encendidas y el resplandor de la luz eléctrica de la calle.
     */
    private fun DrawScope.paris(r: Rect, o: Boolean, u: Float, semilla: Int) {
        if (o) {
            rect(r, CIELO_NOCHE)
            rect(Rect(r.left, r.top + r.height * 0.4f, r.right, r.bottom), mez(CIELO_NOCHE, CIELO_NOCHE_BAJO, 0.6f))
            for (k in 0 until 10) drawCircle(LUNA.copy(alpha = 0.3f + 0.5f * azar(semilla, k)), u * (0.1f + 0.14f * azar(semilla, k + 20)),
                Offset(r.left + r.width * azar(semilla, k + 40), r.top + r.height * 0.35f * azar(semilla, k + 60)))
            // El resplandor de la calle, que sube detrás de los tejados.
            halo(Offset(r.center.x, r.bottom + r.height * 0.1f), r.width * 1.1f, FAROL, 0.35f, 16)
        } else {
            rect(r, CIELO_ALTO)
            rect(Rect(r.left, r.top + r.height * 0.3f, r.right, r.bottom), CIELO)
            for (k in 0 until 3) {
                val c = Offset(r.left + r.width * azar(semilla, k + 80), r.top + r.height * (0.08f + 0.2f * azar(semilla, k + 90)))
                val an = r.width * (0.18f + 0.15f * azar(semilla, k + 100))
                drawOval(mez(NUBE, CIELO, 0.35f), Offset(c.x - an, c.y), Size(an * 2f, r.height * 0.05f))
                drawOval(NUBE, Offset(c.x - an * 0.6f, c.y - r.height * 0.03f), Size(an * 1.3f, r.height * 0.055f))
            }
        }
        // Tres planos de tejados: lejos (velados), en medio y cerca.
        // Lejos, los tejados se funden en el aire; en medio, las fachadas con su mansarda; cerca, una mansarda grande.
        val planos = listOf(Triple(0.3f, 0.55f, 0.3f), Triple(0.44f, 0.22f, 0.36f), Triple(0.64f, 0f, 1f))
        planos.forEachIndexed { k, (alto, velo, mans) ->
            val cornisa = r.top + r.height * alto
            var x = r.left - r.width * (0.1f + 0.3f * azar(semilla, k + 200))
            var j = 0
            while (x < r.right) {
                val an = r.width * (0.45f + 0.3f * azar(semilla, k * 20 + j)) * (1f + 0.5f * k)
                val dy = r.height * 0.05f * (azar(semilla, k * 20 + j + 7) - 0.5f)
                val alto0 = r.bottom + u - (cornisa + dy)
                edificio(Rect(x, cornisa + dy, x + an, r.bottom + u), o, u * (0.5f + 0.28f * k), semilla + k * 31 + j, velo, alto0 * mans)
                x += an; j++
            }
        }
    }

    /**
     * Un edificio de París visto por encima de los tejados de enfrente: la fachada de piedra con su fila de ventanas y su
     * cornisa, la mansarda de zinc con sus buhardillas y, encima, una chimenea con sus cañones de barro. [velo] lo aleja.
     */
    private fun DrawScope.edificio(r: Rect, o: Boolean, u: Float, semilla: Int, velo: Float, mans: Float) {
        val aire = if (o) CIELO_NOCHE_BAJO else CIELO
        fun c(x: Color): Color = mez(n(x, o), aire, velo * (if (o) 0.7f else 0.85f))
        val techo = r.top
        // La mansarda: la parte baja empinada y el tejadillo de arriba.
        val m = Path().apply {
            moveTo(r.left, techo + mans); lineTo(r.left + mans * 0.18f, techo + mans * 0.25f); lineTo(r.left + mans * 0.3f, techo)
            lineTo(r.right - mans * 0.3f, techo); lineTo(r.right - mans * 0.18f, techo + mans * 0.25f); lineTo(r.right, techo + mans); close()
        }
        // La fachada con su cornisa.
        rect(Rect(r.left, techo + mans, r.right, r.bottom), c(FACHADA))
        drawRect(c(mez(FACHADA, Color.White, 0.3f)), Offset(r.left, techo + mans), Size(r.width, 0.6f * u))
        drawRect(c(mez(FACHADA, PLUMA, 0.25f)), Offset(r.left, techo + mans + 0.6f * u), Size(r.width, 0.35f * u))
        drawRect(c(mez(FACHADA, PLUMA, 0.15f)), Offset(r.right - r.width * 0.06f, techo + mans), Size(r.width * 0.06f, r.height))
        drawPath(m, c(ZINC))
        drawLine(c(mez(ZINC, Color.White, 0.35f)), Offset(r.left + mans * 0.3f, techo), Offset(r.right - mans * 0.3f, techo), 0.35f * u)
        // Las juntas del zinc.
        var xz = r.left + 1.4f * u
        while (xz < r.right - u) { drawLine(c(ZINC_OSC).copy(alpha = 0.45f), Offset(xz, techo + mans * 0.25f), Offset(xz, techo + mans), 0.15f * u); xz += 1.4f * u }
        // Las buhardillas, con su tejadillo redondo, y las ventanas de la fachada.
        val nb = (r.width / (7f * u)).toInt().coerceIn(1, 6)
        val hb = minOf(mans * 0.6f, 4.4f * u)
        for (k in 0 until nb) {
            val bx = r.left + r.width * (k + 0.5f) / nb
            val b = Rect(bx - hb * 0.36f, techo + mans * 0.3f + 0.3f * u, bx + hb * 0.36f, techo + mans * 0.3f + hb)
            rect(b, c(FACHADA))
            drawArc(c(ZINC_OSC), 180f, 180f, true, Offset(b.left - 0.4f * u, b.top - 1.1f * u), Size(b.width + 0.8f * u, 2.2f * u))
            val vid = b.deflate(0.4f * u)
            val encendida = o && azar(semilla, k + 400) < 0.45f
            rect(vid, if (encendida) mez(LUZ, FAROL, azar(semilla, k + 410)).copy(alpha = 1f - velo * 0.4f) else c(if (o) Color(0xFF263042) else Color(0xFF6F7C86)))
            val fx = r.left + r.width * (k + 0.5f) / nb
            val fv = Rect(fx - 1f * u, techo + mans + 2.2f * u, fx + 1f * u, techo + mans + 5.4f * u)
            if (fv.bottom < r.bottom) {
                val luzF = o && azar(semilla, k + 430) < 0.35f
                rect(fv, if (luzF) mez(LUZ, FAROL, 0.4f).copy(alpha = 1f - velo * 0.4f) else c(if (o) Color(0xFF263042) else Color(0xFF7A848A)))
                drawRect(c(mez(FACHADA, Color.White, 0.25f)), Offset(fv.left - 0.3f * u, fv.bottom), Size(fv.width + 0.6f * u, 0.35f * u))
            }
        }
        // La chimenea con sus cañones de barro.
        if (azar(semilla, 7) < 0.8f) {
            val cx = r.left + r.width * (0.2f + 0.6f * azar(semilla, 8))
            val ch = Rect(cx - 1.6f * u, techo - 3.4f * u, cx + 1.6f * u, techo + 0.2f * u)
            rect(ch, c(mez(FACHADA, TEJA, 0.35f)))
            drawRect(c(mez(FACHADA, PLUMA, 0.2f)), Offset(ch.right - 0.6f * u, ch.top), Size(0.6f * u, ch.height))
            for (j in 0 until 3) {
                val px = ch.left + 0.5f * u + j * 1.05f * u
                rect(Rect(px, ch.top - 1.2f * u, px + 0.7f * u, ch.top), c(TEJA))
            }
        }
    }

    /** Una pinza de madera con su resorte, colgada de la cuerda en [c]; [alto] es su largo. */
    private fun DrawScope.pinza(c: Offset, alto: Float, o: Boolean, f: Float = 0f) {
        val an = alto * 0.34f
        val r = Rect(c.x - an / 2, c.y - alto * 0.3f, c.x + an / 2, c.y + alto * 0.7f)
        drawRoundRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(r.left + alto * 0.08f, r.top + alto * 0.1f), r.size, CornerRadius(an * 0.4f))
        // Las dos patas de haya, con la luz en la izquierda.
        drawRoundRect(lam(HAYA_OSC, o, f), r.topLeft, r.size, CornerRadius(an * 0.4f))
        drawRoundRect(lam(HAYA, o, f), r.topLeft, Size(an * 0.48f, r.height), CornerRadius(an * 0.3f))
        drawRoundRect(lam(mez(HAYA, HAYA_OSC, 0.3f), o, f), Offset(r.left + an * 0.52f, r.top), Size(an * 0.48f, r.height), CornerRadius(an * 0.3f))
        drawLine(luzMueble(if (o) 0.06f + 0.3f * f else 0.6f), Offset(r.left + an * 0.14f, r.top + alto * 0.08f), Offset(r.left + an * 0.14f, r.bottom - alto * 0.1f), alto * 0.05f, StrokeCap.Round)
        // El resorte de alambre.
        val ry = r.top + alto * 0.42f
        drawCircle(n(Color(0xFF9AA0A4), o), an * 0.3f, Offset(c.x, ry), style = Stroke(alto * 0.05f))
        drawLine(n(Color(0xFF6E7478), o), Offset(r.left - an * 0.05f, ry), Offset(r.right + an * 0.05f, ry), alto * 0.05f)
    }

    /** La cuerda de tender que pasa por los [puntos] y cuelga un poco entre ellos. */
    private fun DrawScope.cuerda(puntos: List<Offset>, caida: Float, o: Boolean, u: Float) {
        val p = Path().apply {
            moveTo(puntos[0].x, puntos[0].y)
            for (k in 1 until puntos.size) {
                val a = puntos[k - 1]; val b = puntos[k]
                quadraticTo((a.x + b.x) / 2, (a.y + b.y) / 2 + caida * ((b.x - a.x) / (40f * u)).coerceIn(0.3f, 1.4f), b.x, b.y)
            }
        }
        translate(0.3f * u, 0.6f * u) { drawPath(p, sombraMueble(if (o) 0.3f else 0.2f), style = Stroke(0.35f * u)) }
        drawPath(p, n(Color(0xFFB9A982), o), style = Stroke(0.32f * u, cap = StrokeCap.Round))
    }

    /**
     * Un cartel a medio imprimir (propio, ningún cartel real), colgado a secar: ya pasaron la piedra del iris violeta, la del
     * tallo salvia y la del marco en látigo; el arco de oro y las letras todavía son solo el trazo de lápiz de la piedra
     * clave, y abajo queda el cuadro de las letras sin entintar. Las cruces de registro, en los márgenes.
     */
    private fun DrawScope.cartelMedio(r: Rect, o: Boolean, u: Float, f: Float = 0f) {
        sombraPieza(r, 1.6f * u, if (o) 0.4f else 0.26f)
        rect(r, lam(VELIN, o, f))
        pinceladas(r, lam(VELIN_OSC, o, f), 17, 8, u * 0.6f, 0.12f)
        val m = minOf(r.width, r.height) * 0.08f
        val cuadro = r.deflate(m)
        val lapiz = lam(Color(0xFF8C8A90), o, f).copy(alpha = 0.55f)
        // El fondo de malva claro (la primera piedra), con la línea de látigo del marco en salvia.
        rect(cuadro, lam(mez(VELIN, MALVA, 0.28f), o, f))
        val arco = Rect(cuadro.left + cuadro.width * 0.12f, cuadro.top + cuadro.height * 0.1f, cuadro.right - cuadro.width * 0.12f, cuadro.top + cuadro.height * 0.62f)
        // El arco de oro: aún sin entintar, solo el lápiz de la piedra clave.
        drawArc(lapiz, 180f, 180f, false, Offset(arco.left, arco.top), Size(arco.width, arco.width), style = Stroke(0.22f * u))
        drawArc(lapiz, 180f, 180f, false, Offset(arco.left + arco.width * 0.08f, arco.top + arco.width * 0.08f), Size(arco.width * 0.84f, arco.width * 0.84f), style = Stroke(0.18f * u))
        with(TrazosNouveau) {
            // El iris (piedra violeta y piedra del tallo).
            iris(Offset(cuadro.center.x - cuadro.width * 0.04f, cuadro.top + cuadro.height * 0.74f), cuadro.height * 0.6f,
                lam(IRIS, o, f), lam(TALLO, o, f), lam(TALLO_SOMBRA, o, f), lam(PLUMA, o, f).copy(alpha = 0.6f))
            // Las líneas de látigo de los lados.
            latigo(Offset(cuadro.left + cuadro.width * 0.06f, cuadro.bottom - cuadro.height * 0.28f), Offset(cuadro.left + cuadro.width * 0.12f, cuadro.top + cuadro.height * 0.16f),
                cuadro.width * 0.06f, 0.55f * u, lam(TALLO_SOMBRA, o, f), 0.12f, true, sombra = false)
            latigo(Offset(cuadro.right - cuadro.width * 0.06f, cuadro.bottom - cuadro.height * 0.28f), Offset(cuadro.right - cuadro.width * 0.12f, cuadro.top + cuadro.height * 0.16f),
                cuadro.width * 0.06f, 0.55f * u, lam(TALLO_SOMBRA, o, f), -0.12f, false, sombra = false)
        }
        drawRect(lam(TALLO_SOMBRA, o, f), cuadro.topLeft, cuadro.size, style = Stroke(0.4f * u))
        // El cuadro de las letras, abajo: el papel limpio con el trazo de lápiz de las letras por venir.
        val letras = Rect(cuadro.left, cuadro.bottom - cuadro.height * 0.2f, cuadro.right, cuadro.bottom)
        rect(letras, lam(VELIN, o, f))
        drawRect(lam(TALLO_SOMBRA, o, f), letras.topLeft, letras.size, style = Stroke(0.4f * u))
        drawLine(lapiz, Offset(letras.left + letras.width * 0.08f, letras.center.y - letras.height * 0.18f), Offset(letras.right - letras.width * 0.08f, letras.center.y - letras.height * 0.18f), 0.12f * u)
        drawLine(lapiz, Offset(letras.left + letras.width * 0.08f, letras.center.y + letras.height * 0.22f), Offset(letras.right - letras.width * 0.08f, letras.center.y + letras.height * 0.22f), 0.12f * u)
        var xl = letras.left + letras.width * 0.12f
        var k = 0
        val alto = letras.height * 0.38f
        val y0 = letras.center.y + letras.height * 0.2f
        while (xl < letras.right - letras.width * 0.14f) {
            // El esqueleto de cada letra: un asta con su rizo, una panza o una ese, como las de pluma del cartel.
            val trazo = when ((k * 7 + 3) % 4) {
                0 -> Path().apply { moveTo(xl, y0); lineTo(xl + alto * 0.05f, y0 - alto); quadraticTo(xl + alto * 0.4f, y0 - alto * 1.1f, xl + alto * 0.3f, y0 - alto * 0.75f) }
                1 -> Path().apply { addOval(Rect(xl, y0 - alto * 0.62f, xl + alto * 0.42f, y0)) }
                2 -> Path().apply { moveTo(xl + alto * 0.4f, y0 - alto * 0.85f); cubicTo(xl - alto * 0.1f, y0 - alto * 0.95f, xl + alto * 0.5f, y0 - alto * 0.1f, xl, y0) }
                else -> Path().apply { moveTo(xl, y0); lineTo(xl, y0 - alto * 0.62f); quadraticTo(xl + alto * 0.35f, y0 - alto * 0.8f, xl + alto * 0.38f, y0) }
            }
            drawPath(trazo, lapiz, style = Stroke(0.16f * u, cap = StrokeCap.Round))
            xl += alto * (0.6f + 0.12f * azar(k, 5)); k++
            if (k == 4) xl += alto * 0.4f
        }
        // Las cruces de registro, en el margen de arriba y en el de abajo.
        listOf(Offset(r.center.x, r.top + m * 0.5f), Offset(r.center.x, r.bottom - m * 0.5f)).forEach { q ->
            val rr = m * 0.32f
            drawCircle(lam(PLUMA, o, f).copy(alpha = 0.6f), rr, q, style = Stroke(0.12f * u))
            drawLine(lam(PLUMA, o, f).copy(alpha = 0.6f), Offset(q.x - rr * 1.5f, q.y), Offset(q.x + rr * 1.5f, q.y), 0.12f * u)
            drawLine(lam(PLUMA, o, f).copy(alpha = 0.6f), Offset(q.x, q.y - rr * 1.5f), Offset(q.x, q.y + rr * 1.5f), 0.12f * u)
        }
        // El papel cuelga: una ondulación de sombra cerca de las pinzas y la esquina de abajo que se levanta.
        drawRect(sombraMueble(if (o) 0.12f else 0.06f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
        drawPath(Path().apply { moveTo(r.right, r.bottom - r.height * 0.06f); lineTo(r.right, r.bottom); lineTo(r.right - r.width * 0.12f, r.bottom); close() }, sombraMueble(if (o) 0.25f else 0.14f))
    }

    /** Una piedra litográfica de canto: la cara de arriba pulida, el canto en capas de caliza y su sombra. */
    private fun DrawScope.piedraCanto(r: Rect, o: Boolean, u: Float, semilla: Int, f: Float = 0f, entintada: Boolean = false) {
        drawRect(sombraMueble(if (o) 0.4f else 0.28f), Offset(r.left + 0.6f * u, r.top + 0.8f * u), r.size)
        rect(r, lam(PIEDRA_CANTO, o, f))
        // Las capas de la caliza y los golpes del canto.
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Una veta de la caliza y las motas; los golpes del canto, más claros, y la esquina mellada.
            val yv = r.top + r.height * (0.5f + 0.25f * azar(semilla, 1))
            drawLine(lam(PIEDRA_OSC, o, f).copy(alpha = 0.4f), Offset(r.left, yv), Offset(r.right, yv + (azar(semilla, 9) - 0.5f) * 0.6f * u), 0.15f * u)
            for (j in 0 until 5) drawCircle(lam(PIEDRA_OSC, o, f).copy(alpha = 0.3f), (0.12f + 0.2f * azar(semilla, j + 30)) * u,
                Offset(r.left + r.width * azar(semilla, j + 40), r.top + r.height * (0.35f + 0.6f * azar(semilla, j + 50))))
            for (j in 0 until 2) {
                val x = r.left + r.width * (0.15f + 0.7f * azar(semilla, j + 60))
                drawOval(lam(PIEDRA_LUZ, o, f).copy(alpha = 0.3f), Offset(x, r.bottom - r.height * 0.4f), Size((0.6f + 0.6f * azar(semilla, j + 70)) * u, r.height * 0.22f))
            }
            drawPath(Path().apply { moveTo(r.right - 1.2f * u, r.bottom); lineTo(r.right, r.bottom - r.height * 0.5f); lineTo(r.right, r.bottom); close() }, lam(PIEDRA_OSC, o, f).copy(alpha = 0.5f))
        }
        val cara = Rect(r.left, r.top, r.right, r.top + minOf(r.height * 0.28f, 0.9f * u))
        rect(cara, lam(if (entintada) TINTA_NEGRA else PIEDRA_LUZ, o, f))
        drawRect(luzMueble(if (o) 0.05f + 0.3f * f else 0.4f), Offset(r.left, r.top), Size(r.width, cara.height * 0.4f))
        drawRect(sombraMueble(if (o) 0.3f else 0.22f), Offset(r.right - 0.5f * u, r.top), Size(0.5f * u, r.height))
    }

    /** Un frasco de tinta de taller: el vidrio con la tinta adentro, el cuello, el tapón de corcho y la etiqueta en blanco. */
    private fun DrawScope.frasco(base: Offset, alto: Float, tinta: Color, o: Boolean, f: Float = 0f) {
        val an = alto * 0.62f
        val cuerpo = Rect(base.x - an / 2, base.y - alto * 0.72f, base.x + an / 2, base.y)
        sombraContacto(Offset(base.x + an * 0.3f, base.y), an * 1.4f, alto * 0.16f, if (o) 0.4f else 0.3f)
        drawRoundRect(lam(mez(tinta, PLUMA, 0.25f), o, f), cuerpo.topLeft, cuerpo.size, CornerRadius(an * 0.18f))
        drawRoundRect(lam(tinta, o, f), cuerpo.topLeft, Size(an * 0.55f, cuerpo.height), CornerRadius(an * 0.18f))
        // La etiqueta crema con su filete.
        val et = Rect(cuerpo.left + an * 0.12f, cuerpo.top + cuerpo.height * 0.3f, cuerpo.right - an * 0.12f, cuerpo.top + cuerpo.height * 0.72f)
        rect(et, lam(VELIN, o, f))
        drawRect(lam(tinta, o, f), et.deflate(et.height * 0.12f).topLeft, et.deflate(et.height * 0.12f).size, style = Stroke(alto * 0.02f))
        drawLine(lam(PLUMA, o, f).copy(alpha = 0.45f), Offset(et.left + et.width * 0.25f, et.center.y), Offset(et.right - et.width * 0.25f, et.center.y), alto * 0.025f)
        // El cuello y el corcho.
        val cuello = Rect(base.x - an * 0.22f, cuerpo.top - alto * 0.1f, base.x + an * 0.22f, cuerpo.top)
        rect(cuello, lam(mez(tinta, PLUMA, 0.4f), o, f))
        drawRoundRect(lam(Color(0xFFB8875A), o, f), Offset(base.x - an * 0.26f, cuerpo.top - alto * 0.24f), Size(an * 0.52f, alto * 0.15f), CornerRadius(alto * 0.03f))
        // El brillo del vidrio.
        drawLine(luzMueble(if (o) 0.1f + 0.4f * f else 0.7f), Offset(cuerpo.left + an * 0.14f, cuerpo.top + alto * 0.08f), Offset(cuerpo.left + an * 0.14f, cuerpo.top + cuerpo.height * 0.26f), alto * 0.05f, StrokeCap.Round)
    }

    /**
     * El rodillo de entintar: el cilindro de cuero (entintado de negro) y sus dos mangos de haya. [a] y [b] son las puntas
     * de los mangos y [grueso], el diámetro del cilindro.
     */
    private fun DrawScope.rodillo(a: Offset, b: Offset, grueso: Float, o: Boolean, f: Float = 0f) {
        val dx = b.x - a.x; val dy = b.y - a.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val ux = dx / l; val uy = dy / l
        val c0 = Offset(a.x + dx * 0.2f, a.y + dy * 0.2f); val c1 = Offset(a.x + dx * 0.8f, a.y + dy * 0.8f)
        // La sombra.
        drawLine(sombraMueble(if (o) 0.4f else 0.3f), Offset(c0.x + grueso * 0.2f, c0.y + grueso * 0.55f), Offset(c1.x + grueso * 0.2f, c1.y + grueso * 0.55f), grueso * 0.9f, StrokeCap.Round)
        // Los mangos, torneados.
        listOf(a to c0, b to c1).forEach { (p, q) ->
            drawLine(lam(HAYA_OSC, o, f), p, q, grueso * 0.36f, StrokeCap.Round)
            drawLine(lam(HAYA, o, f), Offset(p.x - uy * grueso * 0.06f, p.y - grueso * 0.06f), Offset(q.x - uy * grueso * 0.06f, q.y - grueso * 0.06f), grueso * 0.16f, StrokeCap.Round)
            val m = Offset((p.x + q.x) / 2, (p.y + q.y) / 2)
            drawCircle(lam(HAYA_OSC, o, f), grueso * 0.24f, m)
        }
        // El cilindro: cuero con la tinta negra, el canto de cuero en las bocas y el brillo de la tinta fresca.
        drawLine(lam(CUERO, o, f), c0, c1, grueso, StrokeCap.Butt)
        drawLine(lam(TINTA_NEGRA, o, f), Offset(c0.x + ux * grueso * 0.15f, c0.y + uy * grueso * 0.15f), Offset(c1.x - ux * grueso * 0.15f, c1.y - uy * grueso * 0.15f), grueso * 0.94f, StrokeCap.Butt)
        drawLine((if (o) LUZ_HALO else Color.White).copy(alpha = if (o) 0.08f + 0.35f * f else 0.45f), Offset(c0.x + ux * grueso * 0.3f, c0.y - grueso * 0.28f), Offset(c1.x - ux * grueso * 0.3f, c1.y - grueso * 0.28f), grueso * 0.12f, StrokeCap.Round)
        drawLine(lam(mez(TINTA_NEGRA, CUERO, 0.4f), o, f), Offset(c0.x + ux * grueso * 0.3f, c0.y + grueso * 0.3f), Offset(c1.x - ux * grueso * 0.3f, c1.y + grueso * 0.3f), grueso * 0.1f)
    }

    /**
     * La prensa litográfica de mano, de lado (de las de rascador): las patas de fundición verde que se curvan en tallo y
     * se enroscan al pie, con su filete de oro; el carro de roble con la piedra y el rodillo encima; el tímpano levantado
     * con su cuero; la jamba con el porta-rascador, el rascador que baja a la piedra y la palanca; y la estrella de cinco
     * brazos con sus mangos, que mueve el carro. [x0] es el extremo izquierdo del carro, [suelo] el piso y [a] la escala
     * (la prensa mide unos 100 [a] de alto).
     */
    private fun DrawScope.prensa(x0: Float, suelo: Float, a: Float, o: Boolean, u: Float, f: Float = 0f, semilla: Int = 0) {
        val hierro = lam(HIERRO, o, f); val hierroS = lam(HIERRO_SOMBRA, o, f); val hierroL = lam(mez(HIERRO, Color.White, 0.28f), o, f)
        val oro = lam(ORO, o, f).copy(alpha = 0.75f)
        val carroArriba = suelo - 56f * a; val carroAbajo = suelo - 49f * a
        sombraContacto(Offset(x0 + 70f * a, suelo), 150f * a, 6f * a, if (o) 0.45f else 0.32f)
        // La jamba: la pieza de fundición que va del piso hasta arriba y sostiene el porta-rascador; abajo se abre en dos
        // tallos que se enroscan, arriba remata en una hoja.
        val jx = x0 + 66f * a
        val jTop = carroArriba - 36f * a
        val perfil = Path().apply {
            moveTo(jx - 4f * a, jTop); lineTo(jx + 4f * a, jTop)
            cubicTo(jx + 4.5f * a, jTop + 30f * a, jx + 5f * a, suelo - 30f * a, jx + 12f * a, suelo - 1f * a)
            lineTo(jx - 12f * a, suelo - 1f * a)
            cubicTo(jx - 5f * a, suelo - 30f * a, jx - 4.5f * a, jTop + 30f * a, jx - 4f * a, jTop); close()
        }
        translate(2.4f * a, 1.2f * a) { drawPath(perfil, sombraMueble(if (o) 0.4f else 0.25f)) }
        drawPath(perfil, hierroS)
        drawPath(Path().apply {
            moveTo(jx - 4f * a, jTop); lineTo(jx + 1.2f * a, jTop)
            cubicTo(jx + 1.5f * a, jTop + 30f * a, jx + 1.2f * a, suelo - 30f * a, jx + 4f * a, suelo - 1f * a)
            lineTo(jx - 12f * a, suelo - 1f * a)
            cubicTo(jx - 5f * a, suelo - 30f * a, jx - 4.5f * a, jTop + 30f * a, jx - 4f * a, jTop); close()
        }, hierro)
        drawPath(TrazosNouveau.linea(bezier(Offset(jx - 2.4f * a, jTop + 4f * a), Offset(jx - 2.6f * a, jTop + 30f * a), Offset(jx - 3.2f * a, suelo - 26f * a), Offset(jx - 8.5f * a, suelo - 3f * a), 20)),
            oro, style = Stroke(0.4f * a, cap = StrokeCap.Round))
        drawLine(hierroL, Offset(jx - 3.2f * a, jTop + 2f * a), Offset(jx - 3.4f * a, carroArriba), 0.5f * a, StrokeCap.Round)
        // Los pies de la jamba que se enroscan.
        listOf(-1f, 1f).forEach { lado ->
            val p0 = Offset(jx + lado * 12f * a, suelo - 1f * a)
            drawPath(TrazosNouveau.linea(TrazosNouveau.puntosRizo(p0, Offset(lado, -0.6f), 2.8f * a, lado < 0f, 1.0f, 14)), hierroS, style = Stroke(1.6f * a, cap = StrokeCap.Round))
        }
        // La hoja de fundición que remata la jamba.
        with(TrazosNouveau) {
            drawPath(hojaLanza(Offset(jx, jTop + 2f * a), Offset(jx - 1f * a, jTop - 9f * a), 6f * a, 0.05f), hierroS)
            drawPath(hojaLanza(Offset(jx - 0.4f * a, jTop + 1f * a), Offset(jx - 1.2f * a, jTop - 7.5f * a), 2.6f * a, 0.05f), hierro)
        }
        // La pata del otro extremo del carro: un tallo de fundición que baja, se abre y se enrosca.
        listOf(-1f, 1f).forEach { lado ->
            val arriba = Offset(x0 + 18f * a, carroAbajo)
            val abajo = Offset(x0 + (18f + lado * 12f) * a, suelo - 3f * a)
            val pts = bezier(arriba, Offset(arriba.x + lado * 1f * a, arriba.y + 22f * a), Offset(abajo.x - lado * 8f * a, abajo.y - 12f * a), abajo, 24) +
                TrazosNouveau.puntosRizo(abajo, Offset(lado * 1f, 0.35f), 2.6f * a, lado > 0f, 1.05f, 16)
            drawPath(cinta(pts) { t -> 4.2f * a * (1f - 0.65f * t) }, hierroS)
            drawPath(cinta(pts.map { Offset(it.x - 0.6f * a, it.y - 0.2f * a) }) { t -> 2.2f * a * (1f - 0.65f * t) }, hierro)
            drawPath(TrazosNouveau.linea(pts.subList(2, 22).map { Offset(it.x - 0.9f * a, it.y) }), oro, style = Stroke(0.35f * a))
        }
        val yt = suelo - 22f * a
        drawLine(hierroS, Offset(x0 + 12f * a, yt + 0.6f * a), Offset(x0 + 24f * a, yt + 0.6f * a), 2.4f * a)
        drawLine(hierro, Offset(x0 + 12f * a, yt), Offset(x0 + 24f * a, yt), 1.6f * a)
        drawCircle(oro, 1.4f * a, Offset(x0 + 18f * a, yt)); drawCircle(hierroS, 0.6f * a, Offset(x0 + 18f * a, yt))
        // El carro de roble, que pasa por la jamba, con su riel de hierro y sus rodillos.
        val carro = Rect(x0, carroArriba, x0 + 130f * a, carroAbajo)
        madera(carro, o, u, semilla + 3, ROBLE, ROBLE_OSC, ROBLE_LUZ, f = f)
        drawRect(luzMueble(if (o) 0.05f + 0.3f * f else 0.4f), carro.topLeft, Size(carro.width, 0.8f * a))
        drawRect(hierroS, Offset(carro.left, carro.bottom - 1.6f * a), Size(carro.width, 1.6f * a))
        var xr = carro.left + 6f * a
        while (xr < carro.right) { drawCircle(hierroS, 1.4f * a, Offset(xr, carro.bottom + 0.6f * a)); drawCircle(hierroL, 0.5f * a, Offset(xr - 0.3f * a, carro.bottom + 0.3f * a)); xr += 12f * a }
        // La piedra sobre el carro, ya entintada, y el rodillo encima.
        val piedra = Rect(x0 + 7f * a, carroArriba - 8f * a, x0 + 54f * a, carroArriba)
        piedraCanto(piedra, o, u, semilla + 9, f, entintada = true)
        rodillo(Offset(x0 + 4f * a, piedra.top - 3.4f * a), Offset(x0 + 50f * a, piedra.top - 3.8f * a), 6f * a, o, f)
        // El tímpano levantado: un bastidor de hierro con su cuero, abierto sobre la bisagra del carro.
        val bis = Offset(x0 + 2.5f * a, carroArriba - 0.5f * a)
        val tope = Offset(x0 - 3f * a, carroArriba - 48f * a)
        val ancho = Offset(6.5f * a, 1.5f * a)
        val tim = Path().apply { moveTo(bis.x, bis.y); lineTo(tope.x, tope.y); lineTo(tope.x + ancho.x, tope.y + ancho.y); lineTo(bis.x + ancho.x, bis.y); close() }
        drawPath(tim, lam(Color(0xFF7A5434), o, f))
        drawPath(tim, hierroS, style = Stroke(1.1f * a))
        drawLine(luzMueble(if (o) 0.05f + 0.25f * f else 0.35f), Offset(bis.x + 0.8f * a, bis.y - 2f * a), Offset(tope.x + 0.8f * a, tope.y + 2f * a), 0.5f * a)
        drawCircle(hierroL, 1.1f * a, bis); drawCircle(hierroS, 0.5f * a, bis)
        // El porta-rascador: la viga que cruza sobre la jamba, con el rascador de haya que baja hasta la piedra.
        val viga = Rect(jx - 16f * a, jTop - 1f * a, jx + 10f * a, jTop + 4.5f * a)
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(viga.left + 1f * a, viga.bottom), Size(viga.width, 1.4f * a))
        drawRoundRect(hierro, viga.topLeft, viga.size, CornerRadius(1.4f * a))
        drawRect(hierroL, Offset(viga.left + a, viga.top + 0.5f * a), Size(viga.width - 2f * a, 0.6f * a))
        drawRect(hierroS, Offset(viga.left + a, viga.bottom - 1f * a), Size(viga.width - 2f * a, 1f * a))
        val ras = Rect(jx - 13f * a, viga.bottom, jx - 9f * a, carroArriba - 9f * a)
        madera(ras, o, u, semilla + 5, HAYA_OSC, ROBLE_OSC, HAYA, vertical = true, f = f)
        drawRect(lam(CUERO, o, f), Offset(ras.left - 0.4f * a, ras.bottom - 1.6f * a), Size(ras.width + 0.8f * a, 1.6f * a))
        drawRect(hierroS, Offset(ras.left - 1f * a, viga.bottom), Size(ras.width + 2f * a, 2f * a))
        // La palanca de la presión, que sale de la viga hacia la derecha y se curva en tallo hasta su bola.
        val pal = bezier(Offset(jx + 8f * a, viga.center.y), Offset(jx + 16f * a, viga.top - 6f * a), Offset(jx + 28f * a, viga.top - 10f * a), Offset(jx + 38f * a, viga.top - 5f * a), 24)
        drawPath(cinta(pal) { t -> 2.6f * a * (1f - 0.4f * t) }, hierroS)
        drawPath(cinta(pal.map { Offset(it.x, it.y - 0.4f * a) }) { t -> 1.2f * a * (1f - 0.4f * t) }, hierroL)
        drawCircle(hierroS, 2.6f * a, pal.last()); drawCircle(hierro, 2f * a, Offset(pal.last().x - 0.4f * a, pal.last().y - 0.4f * a))
        drawCircle(luzMueble(if (o) 0.1f + 0.3f * f else 0.5f), 0.7f * a, Offset(pal.last().x - 1f * a, pal.last().y - 1f * a))
        // La estrella de cinco brazos, de cara: el cubo, los brazos de fundición y los mangos de haya.
        val cubo = Offset(jx, suelo - 30f * a)
        for (k in 0 until 5) {
            val ang = -PI.toFloat() / 2 + k * 2f * PI.toFloat() / 5f + 0.25f
            val fin = Offset(cubo.x + cos(ang) * 16f * a, cubo.y + sin(ang) * 16f * a)
            drawLine(sombraMueble(if (o) 0.35f else 0.25f), Offset(cubo.x + 1.2f * a, cubo.y + 1.6f * a), Offset(fin.x + 1.2f * a, fin.y + 1.6f * a), 2.2f * a, StrokeCap.Round)
            drawLine(hierroS, cubo, fin, 2.2f * a, StrokeCap.Round)
            drawLine(hierro, Offset(cubo.x - 0.3f * a, cubo.y - 0.3f * a), Offset(fin.x - 0.3f * a, fin.y - 0.3f * a), 1.2f * a, StrokeCap.Round)
            val mango = Offset(cubo.x + cos(ang) * 22f * a, cubo.y + sin(ang) * 22f * a)
            drawLine(lam(HAYA_OSC, o, f), fin, mango, 3.2f * a, StrokeCap.Round)
            drawLine(lam(HAYA, o, f), Offset(fin.x - 0.5f * a, fin.y - 0.5f * a), Offset(mango.x - 0.5f * a, mango.y - 0.5f * a), 1.3f * a, StrokeCap.Round)
            drawCircle(lam(HAYA_OSC, o, f), 1.9f * a, mango)
            drawCircle(lam(HAYA, o, f), 1f * a, Offset(mango.x - 0.4f * a, mango.y - 0.4f * a))
        }
        drawCircle(hierroS, 5f * a, cubo)
        drawCircle(hierro, 4f * a, Offset(cubo.x - 0.4f * a, cubo.y - 0.4f * a))
        drawCircle(oro, 2.4f * a, cubo, style = Stroke(0.45f * a))
        drawCircle(hierroL, 1f * a, Offset(cubo.x - 1.3f * a, cubo.y - 1.3f * a))
    }

    /** La lámpara de vidrio de Nancy de la casa, encendida de noche (con el halo que alumbra lo de alrededor). */
    private fun DrawScope.lamparaNancy(base: Offset, alto: Float, o: Boolean) {
        with(TrazosNouveau) { lampara(base, alto, if (o) 1f else 0f) }
    }

    // ---------------------------------------------------------------- Los nichos

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.22f, 0.215f, 0.76f, 0.725f)
        MuebleVida.MAPAS -> Rect(0.215f, 0.09f, 0.785f, 0.715f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.065f, 0.3f, 0.935f, 0.8f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.155f, 0.925f, 0.825f)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.SOCIAL))
        val techo = h * 0.135f
        val zocaloA = h * 0.77f
        val suelo = h * 0.885f
        val paso = w * 0.1f
        val lamp = Offset(w * 0.105f, h * 0.665f)            // el centro de la pantalla de la lámpara
        fun fl(p: Offset): Float = if (o) cerca(p, lamp, w * 0.24f) else 0f

        // El muro, el zócalo, el piso y el tragaluz, con los rayos que bajan.
        yeso(Rect(0f, techo, w, zocaloA), o, u, e.semilla + 1)
        zocalo(Rect(0f, zocaloA, w, suelo), o, u, e.semilla + 2)
        piso(Rect(0f, suelo, w, h), o, u, e.semilla + 3)
        rayos(Rect(0f, techo, w, suelo), o, u, paso, 0.66f)
        tragaluz(Rect(0f, 0f, w, techo), o, u, e.semilla + 4, paso, 0.66f)

        // La ventana que da a los tejados de París.
        val ven = Rect(w * 0.028f, h * 0.215f, w * 0.162f, h * 0.585f)
        ventana(ven, o, u, e.semilla + 5, fl(Offset(ven.center.x, ven.bottom)))

        // De noche, la lámpara alumbra el muro y el piso a su alrededor (fuera del pliego).
        if (o) fueraDe(ni.inflate(2f * u)) {
            halo(lamp, w * 0.26f, LUZ_HALO, 0.75f)
            halo(lamp, w * 0.1f, LUZ, 0.7f)
        }

        // La cuerda de tender, del clavo junto a la ventana hasta salir por la derecha, con sus pinzas.
        val yc = h * 0.172f
        val clavo = Offset(w * 0.19f, yc - 0.6f * u)
        val pinzasPliego = List(5) { k -> Offset(ni.left - 1.4f * u + (ni.width + 2.8f * u) * k / 4f, yc + 0.6f * u) }
        val cartel = Rect(w * 0.798f, h * 0.195f, w * 0.968f, h * 0.55f)
        val pinzasCartel = listOf(Offset(cartel.left + cartel.width * 0.12f, yc + 1.1f * u), Offset(cartel.right - cartel.width * 0.12f, yc + 1.3f * u))
        cuerda(listOf(clavo) + pinzasPliego + pinzasCartel + listOf(Offset(w + u, yc + 0.6f * u)), 0.8f * u, o, u)
        drawCircle(n(HIERRO_SOMBRA, o), 0.6f * u, clavo); drawCircle(n(mez(HIERRO, Color.White, 0.3f), o), 0.25f * u, Offset(clavo.x - 0.15f * u, clavo.y - 0.15f * u))

        // El cartel a medio imprimir, a secar a la derecha.
        cartelMedio(cartel, o, u)
        pinzasCartel.forEach { pinza(it, 3.6f * u, o) }

        // El pliego grande de papel vélin: el nicho donde va tu gente.
        val pliego = Rect(ni.left - 1.4f * u, yc + 0.6f * u, ni.right + 1.4f * u, ni.bottom + 1.8f * u)
        sombraPieza(pliego, 2.4f * u, if (o) 0.42f else 0.28f)
        // El papel cuelga de las pinzas: el borde de arriba se comba un poco entre una y otra.
        val hoja = Path().apply {
            moveTo(pliego.left, pliego.top)
            for (k in 1 until pinzasPliego.size) {
                val a0 = pinzasPliego[k - 1]; val b0 = pinzasPliego[k]
                quadraticTo((a0.x + b0.x) / 2, pliego.top + 1.4f * u, b0.x, pliego.top)
            }
            lineTo(pliego.right, pliego.top); lineTo(pliego.right, pliego.bottom); lineTo(pliego.left, pliego.bottom); close()
        }
        drawPath(hoja, lam(VELIN, o, 0f))
        drawPath(hoja, n(VELIN_OSC, o).copy(alpha = 0.6f), style = Stroke(0.2f * u))
        // El barbado del papel hecho a mano, en el borde de abajo.
        for (k in 0 until 40) {
            val x = pliego.left + pliego.width * k / 40f
            drawLine(n(VELIN_OSC, o), Offset(x, pliego.bottom - 0.25f * u), Offset(x + pliego.width / 40f, pliego.bottom - 0.25f * u + (azar(e.semilla, k) - 0.5f) * 0.4f * u), 0.35f * u)
        }
        rect(ni, nicho(e, o, VELIN))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            // Las líneas de la verjura del papel, muy tenues, y la luz que entra por arriba a la izquierda.
            var xv = ni.left + 1.2f * u
            while (xv < ni.right) { drawLine((if (o) VELIN else VELIN_OSC).copy(alpha = if (o) 0.025f else 0.12f), Offset(xv, ni.top), Offset(xv, ni.bottom), 0.12f * u); xv += 2.6f * u }
            // Las ondas del papel: dos franjas de sombra muy suaves a la derecha y la luz que entra por la izquierda.
            for (k in 0 until 3) drawRect(sombraMueble(if (o) 0.035f else 0.018f), Offset(ni.right - ni.width * (0.08f + 0.06f * k), ni.top), Size(ni.width * (0.08f + 0.06f * k), ni.height))
            if (!o) for (k in 0 until 3) drawRect(luzMueble(0.07f), ni.topLeft, Size(ni.width * (0.1f + 0.08f * k), ni.height))
        }
        // Las ondas del papel que cuelga: una sombra suave bajo cada pinza.
        pinzasPliego.forEach { q -> drawOval(sombraMueble(if (o) 0.08f else 0.05f), Offset(q.x - 1.2f * u, pliego.top), Size(2.4f * u, 7f * u)) }
        pinzasPliego.forEach { pinza(it, 3.8f * u, o) }

        // Al pie del pliego: la resma atada, las piedras apiladas en su tarima y el balde con la esponja.
        resma(Rect(w * 0.36f, h * 0.845f, w * 0.46f, h * 0.915f), o, u, e.semilla + 7)
        val tarima = Rect(w * 0.545f, h * 0.915f, w * 0.705f, h * 0.945f)
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(tarima.left + u, tarima.bottom), Size(tarima.width, 1.2f * u))
        liston(tarima, o, u, e.semilla + 8, ROBLE, ROBLE_OSC, ROBLE_LUZ)
        listOf(Triple(0.01f, 0.97f, 3.4f), Triple(0.06f, 0.88f, 4.2f), Triple(0.02f, 0.93f, 3f), Triple(0.12f, 0.78f, 3.6f)).fold(tarima.top) { y, (iz, de, gr) ->
            val r = Rect(tarima.left + tarima.width * iz, y - gr * u, tarima.left + tarima.width * de, y)
            piedraCanto(r, o, u, e.semilla + (gr * 10).toInt())
            r.top
        }
        balde(Offset(w * 0.74f, h * 0.945f), 6.4f * u, 6f * u, o, u)

        // El velador de mármol bajo la ventana, con la lámpara de vidrio y las tazas; y la silla curvada.
        silla(Offset(w * 0.215f, h * 0.975f), h * 0.22f, o, u, fl(Offset(w * 0.215f, h * 0.85f)))
        velador(Offset(w * 0.105f, h * 0.79f), w * 0.082f, h * 0.17f, o, u)
        lamparaNancy(Offset(w * 0.087f, h * 0.785f), h * 0.135f, o)
        taza(Offset(w * 0.14f, h * 0.785f), 2.4f * u, o, 1f)
        taza(Offset(w * 0.163f, h * 0.792f), 2.2f * u, o, 0.9f)

        // Al frente, a la derecha, la prensa de mano.
        prensa(w * 0.772f, h * 0.985f, h * 0.0036f, o, u, 0f, e.semilla)

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /**
     * La ventana de dos hojas, pintada de crema, con su falleba de latón: por los vidrios, los tejados de París; en el
     * alféizar, la aspidistra en su maceta de loza.
     */
    private fun DrawScope.ventana(r: Rect, o: Boolean, u: Float, semilla: Int, f: Float) {
        // El derrame del muro alrededor (el grueso de la pared), en sombra a la derecha.
        val marco = r.inflate(1.4f * u)
        sombraPieza(marco, 1.6f * u, if (o) 0.35f else 0.22f)
        rect(marco, n(YESO_LUZ, o))
        drawRect(n(YESO_SOMBRA, o), Offset(marco.right - 1.4f * u, marco.top), Size(1.4f * u, marco.height))
        clipRect(r.left, r.top, r.right, r.bottom) { paris(r, o, u * 0.9f, semilla) }
        // Los reflejos del vidrio.
        drawLine((if (o) LUNA else Color.White).copy(alpha = if (o) 0.05f else 0.25f), Offset(r.left + r.width * 0.1f, r.top + r.height * 0.4f), Offset(r.left + r.width * 0.35f, r.top + r.height * 0.1f), 0.8f * u)
        // Las dos hojas: el bastidor, el montante del medio y los travesaños.
        val madera = n(mez(YESO_LUZ, VELIN, 0.5f), o); val maderaS = n(YESO_SOMBRA, o)
        val g = 1.1f * u
        drawRect(madera, r.topLeft, r.size, style = Stroke(g))
        drawRect(maderaS, Offset(r.left + g / 2, r.top + g / 2), Size(r.width - g, 0.35f * u))
        drawLine(maderaS, Offset(r.center.x + 0.4f * u, r.top), Offset(r.center.x + 0.4f * u, r.bottom), g * 1.2f)
        drawLine(madera, Offset(r.center.x - 0.2f * u, r.top), Offset(r.center.x - 0.2f * u, r.bottom), g * 0.9f)
        for (k in 1..2) {
            val y = r.top + r.height * k / 3f
            drawLine(maderaS, Offset(r.left, y + 0.3f * u), Offset(r.right, y + 0.3f * u), g * 0.6f)
            drawLine(madera, Offset(r.left, y), Offset(r.right, y), g * 0.5f)
        }
        // La falleba de latón: la varilla del medio y su manija.
        val lat = lam(LATON, o, f * 0.4f)
        drawLine(lat, Offset(r.center.x + 0.15f * u, r.top + u), Offset(r.center.x + 0.15f * u, r.bottom - u), 0.22f * u)
        drawRoundRect(lat, Offset(r.center.x - 0.2f * u, r.center.y - 1.6f * u), Size(0.7f * u, 3.2f * u), CornerRadius(0.3f * u))
        drawCircle(lam(LATON_LUZ, o, f), 0.25f * u, Offset(r.center.x, r.center.y - 1.2f * u))
        // El alféizar de piedra, con su sombra.
        val al = Rect(marco.left - 1f * u, r.bottom + 0.2f * u, marco.right + 1f * u, r.bottom + 2.2f * u)
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(al.left + 0.6f * u, al.bottom), Size(al.width, 1.2f * u))
        rect(al, lam(mez(YESO_LUZ, PIEDRA, 0.5f), o, f))
        drawRect(luzMueble(if (o) 0.05f + 0.3f * f else 0.5f), al.topLeft, Size(al.width, 0.4f * u))
        drawRect(n(YESO_SOMBRA, o), Offset(al.left, al.bottom - 0.5f * u), Size(al.width, 0.5f * u))
        // La aspidistra en su maceta de loza, sobre el alféizar.
        aspidistra(Offset(r.left + r.width * 0.3f, al.top), r.height * 0.5f, o, u, f)
    }

    /** Una aspidistra: hojas largas de lanza con su nervio, que salen de la maceta de loza vidriada verde de pavo real. */
    private fun DrawScope.aspidistra(base: Offset, alto: Float, o: Boolean, u: Float, f: Float) {
        val an = alto * 0.36f
        val boca = base.y - alto * 0.28f
        // Las hojas, de atrás adelante.
        val hojas = listOf(Triple(-0.55f, 0.82f, -0.12f), Triple(0.45f, 0.9f, 0.1f), Triple(-0.15f, 1f, -0.05f), Triple(0.8f, 0.62f, 0.16f), Triple(-0.85f, 0.55f, -0.18f), Triple(0.15f, 0.78f, 0.12f))
        with(TrazosNouveau) {
            hojas.forEachIndexed { k, (dx, h, c) ->
                val pie = Offset(base.x + dx * an * 0.15f, boca)
                val punta = Offset(base.x + dx * an * 1.2f, boca - alto * h * 0.95f)
                hojaConNervio(pie, punta, alto * 0.13f, c, lam(if (k % 2 == 0) TALLO else mez(TALLO, TALLO_SOMBRA, 0.3f), o, f), lam(TALLO_SOMBRA, o, f))
            }
        }
        // La maceta: la panza vidriada con el iris en relieve y el borde.
        val m = Rect(base.x - an * 0.5f, boca, base.x + an * 0.5f, base.y)
        sombraContacto(Offset(base.x + an * 0.2f, base.y), an * 1.3f, alto * 0.06f, if (o) 0.4f else 0.3f)
        val maceta = Path().apply {
            moveTo(m.left, m.top); lineTo(m.right, m.top)
            cubicTo(m.right + an * 0.06f, m.top + m.height * 0.5f, m.right - an * 0.1f, m.bottom, m.right - an * 0.18f, m.bottom)
            lineTo(m.left + an * 0.18f, m.bottom)
            cubicTo(m.left + an * 0.1f, m.bottom, m.left - an * 0.06f, m.top + m.height * 0.5f, m.left, m.top); close()
        }
        drawPath(maceta, lam(mez(LOZA, PLUMA, 0.2f), o, f))
        clipPath(maceta) {
            drawRect(lam(LOZA, o, f), Offset(m.left, m.top), Size(m.width * 0.6f, m.height))
            drawOval(lam(LOZA_LUZ, o, f).copy(alpha = 0.7f), Offset(m.left + an * 0.1f, m.top + m.height * 0.15f), Size(an * 0.14f, m.height * 0.55f))
            // El iris en relieve, en un tono más claro del vidriado.
            val rel = lam(mez(LOZA, VIDRIO_VIOLETA, 0.45f), o, f)
            for (s in listOf(-1f, 1f)) drawPath(TrazosNouveau.petalo(Offset(m.center.x + an * 0.08f, m.center.y), Offset(m.center.x + an * 0.08f + s * an * 0.14f, m.center.y - m.height * 0.3f), an * 0.12f), rel)
            drawPath(TrazosNouveau.petalo(Offset(m.center.x + an * 0.08f, m.center.y), Offset(m.center.x + an * 0.08f, m.center.y - m.height * 0.38f), an * 0.13f), rel)
        }
        val borde = Rect(m.left - an * 0.06f, m.top - alto * 0.04f, m.right + an * 0.06f, m.top + alto * 0.02f)
        drawRoundRect(lam(LOZA, o, f), borde.topLeft, borde.size, CornerRadius(borde.height / 2))
        drawLine(lam(LOZA_LUZ, o, f), Offset(borde.left + an * 0.08f, borde.top + borde.height * 0.3f), Offset(borde.center.x, borde.top + borde.height * 0.3f), alto * 0.012f, StrokeCap.Round)
    }

    /** La resma de papel vélin envuelta en su papel de estraza y atada con bramante. */
    private fun DrawScope.resma(r: Rect, o: Boolean, u: Float, semilla: Int) {
        sombraContacto(Offset(r.center.x + r.width * 0.15f, r.bottom), r.width * 1.1f, 1.6f * u, if (o) 0.4f else 0.3f)
        val tapa = Rect(r.left, r.top, r.right, r.top + r.height * 0.3f)
        rect(r, n(Color(0xFFB59468), o))
        pinceladas(r, n(Color(0xFFD2B48A), o), semilla, 5, u * 0.4f, 0.18f)
        rect(tapa, n(Color(0xFFCBAC80), o))
        drawRect(luzMueble(if (o) 0.05f else 0.35f), tapa.topLeft, Size(tapa.width, 0.4f * u))
        // Los cantos del papel que asoman, en hojas finas.
        val cantos = Rect(r.left + r.width * 0.7f, tapa.bottom, r.right, r.bottom)
        rect(cantos, n(VELIN, o))
        var y = cantos.top + 0.4f * u
        while (y < cantos.bottom) { drawLine(n(VELIN_OSC, o), Offset(cantos.left, y), Offset(cantos.right, y), 0.1f * u); y += 0.45f * u }
        drawRect(sombraMueble(if (o) 0.3f else 0.18f), Offset(r.right - 0.6f * u, r.top), Size(0.6f * u, r.height))
        // El bramante en cruz.
        val br = n(Color(0xFFE6D8B4), o)
        drawLine(br, Offset(r.left + r.width * 0.42f, r.top), Offset(r.left + r.width * 0.42f, r.bottom), 0.3f * u)
        drawLine(br, Offset(r.left, r.top + r.height * 0.15f), Offset(r.right, r.top + r.height * 0.15f), 0.3f * u)
        drawCircle(br, 0.6f * u, Offset(r.left + r.width * 0.42f, r.top + r.height * 0.15f), style = Stroke(0.25f * u))
    }

    /** El balde de cinc con el agua para mojar la piedra y la esponja asomando. */
    private fun DrawScope.balde(base: Offset, ancho: Float, alto: Float, o: Boolean, u: Float) {
        sombraContacto(Offset(base.x + ancho * 0.2f, base.y), ancho * 1.4f, 1.6f * u, if (o) 0.4f else 0.3f)
        val c = Path().apply {
            moveTo(base.x - ancho / 2, base.y - alto); lineTo(base.x + ancho / 2, base.y - alto)
            lineTo(base.x + ancho * 0.4f, base.y); lineTo(base.x - ancho * 0.4f, base.y); close()
        }
        drawPath(c, n(ZINC_OSC, o))
        clipPath(c) {
            drawRect(n(ZINC, o), Offset(base.x - ancho / 2, base.y - alto), Size(ancho * 0.45f, alto))
            drawRect(luzMueble(if (o) 0.06f else 0.35f), Offset(base.x - ancho * 0.36f, base.y - alto), Size(ancho * 0.08f, alto))
            for (k in 1..2) drawLine(n(mez(ZINC_OSC, PLUMA, 0.3f), o), Offset(base.x - ancho, base.y - alto * k / 3f), Offset(base.x + ancho, base.y - alto * k / 3f), 0.3f * u)
        }
        drawOval(n(ZINC_OSC, o), Offset(base.x - ancho / 2, base.y - alto - 0.8f * u), Size(ancho, 1.6f * u))
        drawOval(n(Color(0xFF4E6470), o), Offset(base.x - ancho * 0.44f, base.y - alto - 0.5f * u), Size(ancho * 0.88f, 1.1f * u))
        // La esponja.
        drawOval(n(Color(0xFFD9B66A), o), Offset(base.x - ancho * 0.05f, base.y - alto - 1.6f * u), Size(ancho * 0.36f, 1.8f * u))
        for (k in 0 until 4) drawCircle(n(Color(0xFFA07E3A), o), 0.18f * u, Offset(base.x + ancho * (0.02f + 0.07f * k), base.y - alto - (0.6f + 0.3f * (k % 2)) * u))
        // El asa de alambre.
        drawArc(n(Color(0xFF6E7478), o), 200f, 140f, false, Offset(base.x - ancho * 0.5f, base.y - alto - ancho * 0.45f), Size(ancho, ancho * 0.7f), style = Stroke(0.25f * u))
    }

    /**
     * El velador de café: el tablero redondo de mármol visto un poco desde arriba y el pie de fundición que baja en tres
     * tallos y se enrosca al pie. [c] es el centro del tablero, [r] su radio y [alto] hasta el piso.
     */
    private fun DrawScope.velador(c: Offset, r: Float, alto: Float, o: Boolean, u: Float) {
        val pie = c.y + alto
        val hierro = lam(HIERRO, o, 0.9f); val hierroS = lam(HIERRO_SOMBRA, o, 0.9f)
        sombraContacto(Offset(c.x + r * 0.2f, pie), r * 1.9f, 2f * u, if (o) 0.3f else 0.32f)
        // El pie: la columna y los tres tallos que se abren y se enroscan.
        drawLine(hierroS, Offset(c.x + 0.3f * u, c.y), Offset(c.x + 0.3f * u, pie - alto * 0.3f), 1.4f * u)
        drawLine(hierro, Offset(c.x - 0.2f * u, c.y), Offset(c.x - 0.2f * u, pie - alto * 0.3f), 0.7f * u)
        listOf(-1f, 0f, 1f).forEach { s ->
            val desde = Offset(c.x, pie - alto * 0.32f)
            val hasta = Offset(c.x + s * r * 0.85f, pie - 1f * u)
            val pts = bezier(desde, Offset(desde.x + s * r * 0.1f, desde.y + alto * 0.18f), Offset(hasta.x - s * r * 0.35f, hasta.y - alto * 0.08f), hasta, 18) +
                if (s != 0f) TrazosNouveau.puntosRizo(hasta, Offset(s, 0.2f), 1.1f * u, s > 0f, 1.0f, 12) else emptyList()
            drawPath(TrazosNouveau.linea(pts), hierroS, style = Stroke(1.1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(TrazosNouveau.linea(pts.map { Offset(it.x - 0.2f * u, it.y - 0.2f * u) }), hierro, style = Stroke(0.5f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        // El tablero de mármol: el canto y la cara.
        val gr = 1.4f * u
        drawOval(lam(MARMOL_OSC, o, 1f), Offset(c.x - r, c.y - r * 0.22f + gr * 0.5f), Size(r * 2f, r * 0.44f + gr))
        drawOval(lam(MARMOL, o, 1f), Offset(c.x - r, c.y - r * 0.22f), Size(r * 2f, r * 0.44f))
        drawArc(lam(Color(0xFFA9A08E), o, 1f), 200f, 60f, false, Offset(c.x - r * 0.7f, c.y - r * 0.14f), Size(r * 1.2f, r * 0.26f), style = Stroke(0.15f * u))
        drawOval(luzMueble(if (o) 0.25f else 0.4f), Offset(c.x - r * 0.8f, c.y - r * 0.18f), Size(r * 0.7f, r * 0.12f))
    }

    /** Una taza de café en su plato. */
    private fun DrawScope.taza(base: Offset, r: Float, o: Boolean, f: Float) {
        drawOval(lam(MARMOL_OSC, o, f), Offset(base.x - r * 1.1f, base.y - r * 0.28f), Size(r * 2.2f, r * 0.5f))
        drawOval(lam(Color(0xFFF4F0E6), o, f), Offset(base.x - r * 1.05f, base.y - r * 0.32f), Size(r * 2.1f, r * 0.46f))
        val t = Path().apply {
            moveTo(base.x - r * 0.6f, base.y - r * 1.1f); lineTo(base.x + r * 0.6f, base.y - r * 1.1f)
            quadraticTo(base.x + r * 0.6f, base.y - r * 0.15f, base.x, base.y - r * 0.12f)
            quadraticTo(base.x - r * 0.6f, base.y - r * 0.15f, base.x - r * 0.6f, base.y - r * 1.1f); close()
        }
        drawPath(t, lam(Color(0xFFF4F0E6), o, f))
        drawPath(t, lam(MARMOL_OSC, o, f), style = Stroke(r * 0.06f))
        drawLine(lam(TALLO_SOMBRA, o, f), Offset(base.x - r * 0.55f, base.y - r * 0.9f), Offset(base.x + r * 0.55f, base.y - r * 0.9f), r * 0.08f)
        drawArc(lam(Color(0xFFE6E0D2), o, f), -70f, 140f, false, Offset(base.x + r * 0.4f, base.y - r * 0.95f), Size(r * 0.55f, r * 0.55f), style = Stroke(r * 0.12f))
        drawOval(lam(Color(0xFF4A2E1E), o, f), Offset(base.x - r * 0.52f, base.y - r * 1.18f), Size(r * 1.04f, r * 0.2f))
    }

    /**
     * La silla de madera curvada al vapor, de café: el respaldo en dos aros, el asiento de rejilla, las patas que se abren y
     * el aro que las une. [pie] es el centro del piso bajo el asiento, [alto] hasta lo alto del respaldo.
     */
    private fun DrawScope.silla(pie: Offset, alto: Float, o: Boolean, u: Float, f: Float) {
        val mad = lam(NOGAL, o, f); val madS = lam(NOGAL_OSC, o, f); val madL = lam(NOGAL_LUZ, o, f)
        val g = alto * 0.035f
        val asiento = pie.y - alto * 0.5f
        val an = alto * 0.36f
        sombraContacto(Offset(pie.x + an * 0.3f, pie.y), an * 1.6f, 1.6f * u, if (o) 0.4f else 0.3f)
        // El respaldo: el aro grande que sube desde las patas de atrás y el aro chico de adentro.
        val resp = Path().apply {
            moveTo(pie.x - an * 0.36f, pie.y)
            cubicTo(pie.x - an * 0.42f, asiento, pie.x - an * 0.6f, pie.y - alto * 0.98f, pie.x, pie.y - alto)
            cubicTo(pie.x + an * 0.6f, pie.y - alto * 0.98f, pie.x + an * 0.42f, asiento, pie.x + an * 0.36f, pie.y)
        }
        drawPath(resp, madS, style = Stroke(g * 1.3f, cap = StrokeCap.Round))
        drawPath(resp, mad, style = Stroke(g * 0.8f, cap = StrokeCap.Round))
        val chico = Path().apply {
            moveTo(pie.x - an * 0.3f, asiento - alto * 0.04f)
            cubicTo(pie.x - an * 0.36f, pie.y - alto * 0.74f, pie.x - an * 0.34f, pie.y - alto * 0.86f, pie.x, pie.y - alto * 0.87f)
            cubicTo(pie.x + an * 0.34f, pie.y - alto * 0.86f, pie.x + an * 0.36f, pie.y - alto * 0.74f, pie.x + an * 0.3f, asiento - alto * 0.04f)
        }
        drawPath(chico, madS, style = Stroke(g, cap = StrokeCap.Round))
        drawPath(chico, madL, style = Stroke(g * 0.4f, cap = StrokeCap.Round))
        // Las patas de delante, que se abren, y el aro que las une.
        listOf(-1f, 1f).forEach { s ->
            drawLine(madS, Offset(pie.x + s * an * 0.42f, asiento), Offset(pie.x + s * an * 0.5f, pie.y), g * 1.1f, StrokeCap.Round)
            drawLine(madL, Offset(pie.x + s * an * 0.42f - g * 0.2f, asiento), Offset(pie.x + s * an * 0.5f - g * 0.2f, pie.y), g * 0.35f, StrokeCap.Round)
        }
        drawOval(madS, Offset(pie.x - an * 0.47f, pie.y - alto * 0.2f), Size(an * 0.94f, alto * 0.06f), style = Stroke(g * 0.7f))
        // El asiento redondo, con su rejilla.
        val as0 = Rect(pie.x - an * 0.55f, asiento - alto * 0.05f, pie.x + an * 0.55f, asiento + alto * 0.05f)
        drawOval(madS, as0.topLeft, as0.size)
        drawOval(lam(Color(0xFFD6BE8A), o, f), as0.deflate(g * 0.6f).topLeft, as0.deflate(g * 0.6f).size)
        clipPath(Path().apply { addOval(as0.deflate(g * 0.6f)) }) {
            var x = as0.left
            while (x < as0.right + as0.height) {
                drawLine(lam(Color(0xFFA88A54), o, f).copy(alpha = 0.6f), Offset(x, as0.top), Offset(x - as0.height, as0.bottom), g * 0.15f)
                drawLine(lam(Color(0xFFA88A54), o, f).copy(alpha = 0.6f), Offset(x - as0.height, as0.top), Offset(x, as0.bottom), g * 0.15f)
                x += g * 0.6f
            }
        }
        drawOval(madL.copy(alpha = 0.7f), Offset(as0.left + g * 0.3f, as0.top + g * 0.1f), Size(as0.width * 0.5f, g * 0.4f))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.MAPAS))
        val tablero = Rect(w * 0.035f, h * 0.035f, w * 0.965f, h * 0.79f)
        val canto = Rect(tablero.left - 0.6f * u, tablero.bottom, tablero.right + 0.6f * u, tablero.bottom + 3.4f * u)
        val faldon = Rect(tablero.left + 2f * u, canto.bottom, tablero.right - 2f * u, h * 0.905f)
        val suelo = h * 0.93f
        val punta = Offset(w * 0.876f, h * 0.17f)                // donde cuelga la tulipa
        val foco = Offset(punta.x, punta.y + 3.6f * u)          // la tulipa encendida
        fun fl(p: Offset): Float = if (o) cerca(p, foco, w * 0.32f) else 0f

        // El muro de detrás y el piso.
        yeso(Rect(0f, 0f, w, suelo), o, u, e.semilla + 1)
        piso(Rect(0f, suelo, w, h), o, u, e.semilla + 2)
        // Las patas de nogal (detrás del faldón) y la cremallera de hierro de la inclinación.
        listOf(faldon.left + 3f * u, faldon.right - 3f * u).forEachIndexed { k, x ->
            val s = if (k == 0) -1f else 1f
            val pata = Path().apply {
                moveTo(x - 1.6f * u, faldon.bottom - u); lineTo(x + 1.6f * u, faldon.bottom - u)
                cubicTo(x + 1.4f * u, suelo - 3f * u, x + s * 1.6f * u + 1f * u, suelo - 1f * u, x + s * 2.4f * u + 1f * u, suelo)
                lineTo(x + s * 2.4f * u - 1.4f * u, suelo)
                cubicTo(x + s * 1f * u - 1.2f * u, suelo - 1f * u, x - 1.4f * u, suelo - 3f * u, x - 1.6f * u, faldon.bottom - u); close()
            }
            sombraContacto(Offset(x + s * 2.4f * u, suelo), 7f * u, 1.6f * u, if (o) 0.4f else 0.3f)
            drawPath(pata, lam(NOGAL, o, fl(Offset(x, suelo))))
            drawLine(lam(NOGAL_LUZ, o, fl(Offset(x, suelo))), Offset(x - 0.9f * u, faldon.bottom), Offset(x - 0.6f * u + s * 0.8f * u, suelo - 1.6f * u), 0.4f * u, StrokeCap.Round)
        }
        val cre = Rect(w * 0.9f, faldon.bottom - 4f * u, w * 0.93f, suelo - 3f * u)
        drawRoundRect(lam(HIERRO_SOMBRA, o, fl(cre.center)), cre.topLeft, cre.size, CornerRadius(0.6f * u))
        var yd = cre.top + 0.6f * u
        while (yd < cre.bottom - 0.4f * u) { drawLine(lam(HIERRO, o, fl(cre.center)), Offset(cre.left, yd), Offset(cre.left - 0.7f * u, yd + 0.35f * u), 0.35f * u); yd += 1f * u }
        // La sombra de la mesa en el muro.
        sombraPieza(Rect(tablero.left, tablero.top, tablero.right, canto.bottom), 2.6f * u, if (o) 0.4f else 0.24f, 0.8f * u)

        // De noche, la tulipa alumbra el muro alrededor (antes que el tablero, que la tapa).
        if (o) halo(foco, w * 0.36f, LUZ_HALO, 0.5f)

        // El tablero: el marco de nogal y la tabla de tilo con su veta, sus agujeros de chincheta y sus manchas.
        madera(tablero, o, u, e.semilla + 4, f = 0f)
        val tabla = tablero.deflate(2.4f * u)
        madera(tabla, o, u, e.semilla + 5, TILO, mez(TILO, ROBLE_OSC, 0.3f), mez(TILO, Color.White, 0.4f), veta = mez(TILO, ROBLE_OSC, 0.6f))
        if (o) fueraDe(ni.inflate(2f * u)) { halo(foco, w * 0.2f, LUZ, 0.55f) }
        drawRect(sombraMueble(if (o) 0.35f else 0.22f), tabla.topLeft, Size(tabla.width, 0.7f * u))
        drawRect(sombraMueble(if (o) 0.25f else 0.16f), tabla.topLeft, Size(0.6f * u, tabla.height))
        drawRect(luzMueble(if (o) 0.04f else 0.35f), Offset(tablero.left, tablero.top), Size(tablero.width, 0.45f * u))
        drawRect(luzMueble(if (o) 0.04f else 0.3f), Offset(tablero.left, tablero.top), Size(0.45f * u, tablero.height))
        drawRect(sombraMueble(if (o) 0.35f else 0.25f), Offset(tablero.right - 0.5f * u, tablero.top), Size(0.5f * u, tablero.height))
        clipRect(tabla.left, tabla.top, tabla.right, tabla.bottom) {
            for (k in 0 until 40) drawCircle(n(ROBLE_OSC, o).copy(alpha = 0.5f), 0.14f * u, Offset(tabla.left + tabla.width * azar(e.semilla, k + 500), tabla.top + tabla.height * azar(e.semilla, k + 540)))
            for (k in 0 until 6) {
                val c = Offset(tabla.left + tabla.width * azar(e.semilla, k + 600), tabla.top + tabla.height * azar(e.semilla, k + 610))
                drawLine(n(PLUMA, o).copy(alpha = 0.12f), c, Offset(c.x + (2f + 4f * azar(e.semilla, k + 620)) * u, c.y + (azar(e.semilla, k + 630) - 0.5f) * 2f * u), 0.12f * u)
            }
            // El cerco de una taza y unas gotas de tinta.
            val cc = Offset(tabla.right - tabla.width * 0.1f, tabla.bottom - tabla.height * 0.08f)
            drawCircle(n(Color(0xFF8A5A30), o).copy(alpha = 0.2f), 2.4f * u, cc, style = Stroke(0.3f * u))
            drawCircle(n(TINTA_NEGRA, o).copy(alpha = 0.45f), 0.5f * u, Offset(tabla.left + tabla.width * 0.12f, tabla.bottom - tabla.height * 0.12f))
            drawCircle(n(IRIS, o).copy(alpha = 0.4f), 0.35f * u, Offset(tabla.left + tabla.width * 0.15f, tabla.bottom - tabla.height * 0.1f))
        }

        // A la izquierda: el estudio de iris, la tira de las tintas y el calco de la libélula.
        estudioIris(Rect(w * 0.06f, h * 0.075f, w * 0.188f, h * 0.43f), o, u, -2.5f)
        tiraTintas(Rect(w * 0.062f, h * 0.475f, w * 0.19f, h * 0.535f), o, u)
        calco(Rect(w * 0.065f, h * 0.575f, w * 0.185f, h * 0.745f), o, u, false, 2f)

        // El pliego sujeto con sus cuatro chinchetas: el nicho donde va el mapa.
        val pliego = ni.inflate(2f * u)
        drawRect(sombraMueble(if (o) 0.4f else 0.26f), Offset(pliego.left + 0.8f * u, pliego.top + 1.1f * u), pliego.size)
        rect(pliego, lam(VELIN, o, 0f))
        drawRect(n(VELIN_OSC, o), pliego.topLeft, pliego.size, style = Stroke(0.2f * u))
        rect(ni, nicho(e, o, VELIN))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            if (!o) for (k in 0 until 3) drawRect(luzMueble(0.06f), ni.topLeft, Size(ni.width * (0.1f + 0.08f * k), ni.height))
            drawRect(sombraMueble(if (o) 0.08f else 0.04f), Offset(ni.left, ni.bottom - 2f * u), Size(ni.width, 2f * u))
        }
        listOf(pliego.topLeft, Offset(pliego.right, pliego.top), Offset(pliego.left, pliego.bottom), pliego.bottomRight).forEach { q ->
            chincheta(Offset(q.x + (if (q.x < ni.center.x) 1f else -1f) * 0.9f * u, q.y + (if (q.y < ni.center.y) 1f else -1f) * 0.9f * u), 0.85f * u, o, fl(q))
        }

        // A la derecha: el calco de la línea de látigo y el compás colgado de su clavo.
        calco(Rect(w * 0.808f, h * 0.43f, w * 0.94f, h * 0.6f), o, u, true, -2f)
        compas(Offset(w * 0.9f, h * 0.63f), h * 0.13f, o, u, fl(Offset(w * 0.9f, h * 0.68f)))

        // El canto del tablero: la regleta de nogal donde descansan las barras de lápiz, el pincel y el tiralíneas.
        val fc = fl(canto.center)
        madera(canto, o, u, e.semilla + 7, f = fc)
        drawRect(lam(NOGAL_LUZ, o, fc), canto.topLeft, Size(canto.width, 0.8f * u))
        drawRect(luzMueble(if (o) 0.05f + 0.2f * fc else 0.35f), canto.topLeft, Size(canto.width, 0.3f * u))
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(canto.left, canto.bottom - 0.6f * u), Size(canto.width, 0.6f * u))
        val yUtil = canto.top + 0.2f * u
        lapizLito(Offset(w * 0.3f, yUtil), w * 0.11f, o, u, fl(Offset(w * 0.35f, yUtil)), LATON)
        lapizLito(Offset(w * 0.44f, yUtil + 0.1f * u), w * 0.09f, o, u, fl(Offset(w * 0.48f, yUtil)), HAYA)
        pincel(Offset(w * 0.56f, yUtil), w * 0.13f, o, u, fl(Offset(w * 0.62f, yUtil)))
        tiraLineas(Offset(w * 0.71f, yUtil), w * 0.07f, o, u, fl(Offset(w * 0.74f, yUtil)))
        // La regla en T: la hoja apoyada en el canto y la cabeza colgando por la izquierda.
        val hojaT = Rect(tablero.left + 0.4f * u, canto.top - 1.3f * u, w * 0.27f, canto.top)
        drawRect(sombraMueble(if (o) 0.35f else 0.25f), Offset(hojaT.left + 0.4f * u, hojaT.bottom), Size(hojaT.width, 0.5f * u))
        madera(hojaT, o, u, e.semilla + 9, Color(0xFFC9A46E), Color(0xFF8A6A40), Color(0xFFE2C496), f = fl(hojaT.center))
        drawLine(lam(Color(0xFF3E3A40), o, 0f), Offset(hojaT.left + u, hojaT.top + 0.2f * u), Offset(hojaT.right - 0.4f * u, hojaT.top + 0.2f * u), 0.25f * u)
        val cabeza = Rect(tablero.left - 1.6f * u, canto.top - 3f * u, tablero.left + 1.2f * u, canto.bottom + 4f * u)
        drawRect(sombraMueble(if (o) 0.35f else 0.25f), Offset(cabeza.left + 0.6f * u, cabeza.top + 0.6f * u), cabeza.size)
        madera(cabeza, o, u, e.semilla + 10, Color(0xFFC9A46E), Color(0xFF8A6A40), Color(0xFFE2C496), vertical = true)
        drawCircle(lam(LATON, o, 0f), 0.4f * u, Offset(cabeza.center.x, hojaT.center.y))
        // Los frascos de tinta y el vaso con los pinceles, a la derecha del canto.
        frasco(Offset(w * 0.8f, canto.top + 0.3f * u), 4.6f * u, IRIS, o, fl(Offset(w * 0.8f, canto.top)))
        frasco(Offset(w * 0.835f, canto.top + 0.3f * u), 3.8f * u, TALLO_SOMBRA, o, fl(Offset(w * 0.835f, canto.top)))
        vasoPinceles(Offset(w * 0.875f, canto.top + 0.3f * u), 5.4f * u, o, u, fl(Offset(w * 0.875f, canto.top)))

        // El faldón tallado: la tabla de nogal con su moldura, la línea de látigo y el cajón con su tirador de hoja.
        val ff = fl(faldon.center)
        madera(faldon, o, u, e.semilla + 11, f = ff * 0.6f)
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), faldon.topLeft, Size(faldon.width, 0.8f * u))
        drawRect(sombraMueble(if (o) 0.3f else 0.2f), Offset(faldon.left, faldon.bottom), Size(faldon.width, 1f * u))
        val cajon = Rect(faldon.center.x - faldon.width * 0.14f, faldon.top + 1.4f * u, faldon.center.x + faldon.width * 0.14f, faldon.bottom - 1.2f * u)
        drawRect(lam(NOGAL_OSC, o, ff * 0.6f), cajon.topLeft, cajon.size, style = Stroke(0.35f * u))
        drawLine(luzMueble(if (o) 0.04f else 0.25f), Offset(cajon.left, cajon.bottom + 0.2f * u), Offset(cajon.right, cajon.bottom + 0.2f * u), 0.2f * u)
        tirador(Offset(cajon.center.x, cajon.center.y), 1.4f * u, o, ff)
        with(TrazosNouveau) {
            val talla = lam(NOGAL_OSC, o, ff * 0.6f)
            latigo(Offset(cajon.left - 2f * u, faldon.center.y + 0.6f * u), Offset(faldon.left + faldon.width * 0.08f, faldon.center.y - 0.4f * u), 1.4f * u, 0.7f * u, talla, 0.18f, false, sombra = false)
            latigo(Offset(cajon.right + 2f * u, faldon.center.y + 0.6f * u), Offset(faldon.right - faldon.width * 0.08f, faldon.center.y - 0.4f * u), 1.4f * u, 0.7f * u, talla, -0.18f, true, sombra = false)
        }

        // La lámpara: el brazo de latón que sube del marco como un tallo y se dobla, con la tulipa colgada de la punta.
        val arranque = Offset(tablero.right - 0.6f * u, h * 0.4f)
        val curva = bezier(arranque, Offset(w * 0.995f, h * 0.22f), Offset(w * 0.96f, h * 0.05f), Offset(w * 0.9f, h * 0.06f), 30) +
            bezier(Offset(w * 0.9f, h * 0.06f), Offset(w * 0.878f, h * 0.065f), Offset(punta.x, h * 0.1f), punta, 10).drop(1)
        val brazo = lam(LATON, o, 0.6f); val brazoS = lam(LATON_OSC, o, 0.6f)
        translate(0.6f * u, 0.9f * u) { drawPath(TrazosNouveau.linea(curva), sombraMueble(if (o) 0.3f else 0.22f), style = Stroke(1f * u, cap = StrokeCap.Round)) }
        drawPath(TrazosNouveau.linea(curva), brazoS, style = Stroke(0.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(TrazosNouveau.linea(curva.map { Offset(it.x - 0.18f * u, it.y - 0.18f * u) }), brazo, style = Stroke(0.4f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // La hoja que abraza el arranque y la abrazadera en el marco.
        with(TrazosNouveau) {
            drawPath(hojaLanza(Offset(arranque.x + 0.6f * u, arranque.y + 2f * u), Offset(w * 0.985f, h * 0.27f), 1.6f * u, 0.1f), lam(TALLO_SOMBRA, o, 0.4f))
        }
        with(TrazosNouveau) {
            // Una hoja de latón que sale del brazo en lo alto, como de un tallo.
            val q = curva[18]
            drawPath(hojaLanza(q, Offset(q.x + 3.4f * u, q.y + 3f * u), 1.3f * u, -0.15f), brazoS)
            drawPath(hojaLanza(q, Offset(q.x + 3f * u, q.y + 2.6f * u), 0.5f * u, -0.15f), brazo)
        }
        drawRoundRect(brazoS, Offset(arranque.x - 0.8f * u, arranque.y - 1f * u), Size(2.2f * u, 2.6f * u), CornerRadius(0.5f * u))
        // La tulipa cuelga boca abajo de la punta del brazo.
        rotate(180f, punta) { with(TrazosNouveau) { tulipa(punta, 6.4f * u, if (o) 1f else 0f) } }

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /** Una chincheta de latón con su cabeza abombada y su brillo. */
    private fun DrawScope.chincheta(c: Offset, r: Float, o: Boolean, f: Float) {
        drawCircle(sombraMueble(if (o) 0.4f else 0.3f), r, Offset(c.x + r * 0.4f, c.y + r * 0.55f))
        drawCircle(lam(LATON_OSC, o, f), r, c)
        drawCircle(lam(LATON, o, f), r * 0.78f, Offset(c.x - r * 0.1f, c.y - r * 0.1f))
        drawCircle(lam(LATON_LUZ, o, f), r * 0.28f, Offset(c.x - r * 0.32f, c.y - r * 0.32f))
    }

    /**
     * El estudio de un iris en una hoja suelta: el dibujo a lápiz con sus aguadas de color, las líneas de construcción y
     * una chincheta arriba. [giro] la inclina un poco.
     */
    private fun DrawScope.estudioIris(r: Rect, o: Boolean, u: Float, giro: Float) {
        rotate(giro, r.center) {
            drawRect(sombraMueble(if (o) 0.35f else 0.24f), Offset(r.left + 0.6f * u, r.top + 0.9f * u), r.size)
            rect(r, n(VELIN, o))
            pinceladas(r, n(VELIN_OSC, o), 3, 5, u * 0.5f, 0.15f)
            val lapiz = n(Color(0xFF8C8A90), o).copy(alpha = 0.5f)
            drawLine(lapiz, Offset(r.center.x, r.top + r.height * 0.06f), Offset(r.center.x, r.bottom - r.height * 0.06f), 0.1f * u)
            drawCircle(lapiz, r.width * 0.28f, Offset(r.center.x, r.top + r.height * 0.32f), style = Stroke(0.1f * u))
            with(TrazosNouveau) {
                iris(Offset(r.center.x, r.bottom - r.height * 0.08f), r.height * 0.78f, n(mez(IRIS, VELIN, 0.3f), o), n(mez(TALLO, VELIN, 0.25f), o), n(mez(TALLO_SOMBRA, VELIN, 0.2f), o),
                    n(PLUMA, o).copy(alpha = 0.55f))
            }
            // Un pétalo suelto, dibujado aparte, a lápiz.
            drawPath(TrazosNouveau.petalo(Offset(r.left + r.width * 0.16f, r.bottom - r.height * 0.1f), Offset(r.left + r.width * 0.26f, r.bottom - r.height * 0.28f), r.width * 0.12f), lapiz, style = Stroke(0.12f * u))
            chincheta(Offset(r.center.x, r.top + 1.2f * u), 0.7f * u, o, 0f)
        }
    }

    /** La tira de prueba de las cuatro tintas del cartel (oro, violeta, salvia y azul de Prusia), con sus cruces de registro. */
    private fun DrawScope.tiraTintas(r: Rect, o: Boolean, u: Float) {
        drawRect(sombraMueble(if (o) 0.35f else 0.24f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
        rect(r, n(VELIN, o))
        val tintas = listOf(ORO, IRIS, TALLO_SOMBRA, TINTA_AZUL)
        val m = r.height * 0.18f
        val an = (r.width - m * 2f) / tintas.size
        tintas.forEachIndexed { k, c ->
            val q = Rect(r.left + m + an * k + 0.2f * u, r.top + m, r.left + m + an * (k + 1) - 0.2f * u, r.bottom - m)
            rect(q, n(c, o))
            drawRect(n(mez(c, Color.White, 0.3f), o), q.topLeft, Size(q.width * 0.4f, q.height))
        }
        drawCircle(n(PLUMA, o).copy(alpha = 0.6f), m * 0.3f, Offset(r.left + m * 0.5f, r.center.y), style = Stroke(0.1f * u))
        chincheta(Offset(r.right - 0.9f * u, r.top + 0.9f * u), 0.6f * u, o, 0f)
    }

    /**
     * Un calco en papel de seda, que deja ver la madera: la libélula a lápiz o, con [latigo], la línea de látigo trazada
     * en tinta azul para pasarla a la piedra; con su chincheta y una esquina levantada.
     */
    private fun DrawScope.calco(r: Rect, o: Boolean, u: Float, latigo: Boolean, giro: Float) {
        rotate(giro, r.center) {
            drawRect(sombraMueble(if (o) 0.2f else 0.12f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
            rect(r, n(Color(0xFFF4EEDF), o).copy(alpha = 0.72f))
            with(TrazosNouveau) {
                if (latigo) {
                    drawPath(linea(puntosLatigo(Offset(r.left + r.width * 0.15f, r.bottom - r.height * 0.15f), Offset(r.left + r.width * 0.62f, r.top + r.height * 0.35f), r.width * 0.12f, 0.3f)),
                        n(TINTA_AZUL, o).copy(alpha = 0.8f), style = Stroke(0.3f * u, cap = StrokeCap.Round))
                    drawPath(linea(puntosLatigo(Offset(r.left + r.width * 0.3f, r.bottom - r.height * 0.1f), Offset(r.left + r.width * 0.8f, r.top + r.height * 0.55f), r.width * 0.08f, -0.25f, false)),
                        n(TINTA_AZUL, o).copy(alpha = 0.5f), style = Stroke(0.2f * u, cap = StrokeCap.Round))
                } else {
                    libelula(Offset(r.center.x, r.center.y - r.height * 0.08f), r.height * 0.62f, 18f, 0f, n(Color(0xFF8C8A90), o).copy(alpha = 0.7f),
                        n(Color(0xFFF4EEDF), o).copy(alpha = 0.3f), n(Color(0xFF8C8A90), o).copy(alpha = 0.6f))
                }
            }
            // La esquina de abajo que se levanta.
            drawPath(Path().apply { moveTo(r.right, r.bottom - r.height * 0.18f); lineTo(r.right - r.width * 0.18f, r.bottom); lineTo(r.right, r.bottom); close() }, n(YESO_SOMBRA, o).copy(alpha = 0.5f))
            drawPath(Path().apply { moveTo(r.right, r.bottom - r.height * 0.18f); lineTo(r.right - r.width * 0.18f, r.bottom); lineTo(r.right - r.width * 0.2f, r.bottom - r.height * 0.2f); close() }, n(VELIN, o).copy(alpha = 0.85f))
            chincheta(Offset(r.left + r.width * 0.5f, r.top + 0.9f * u), 0.6f * u, o, 0f)
        }
    }

    /** El compás de latón, abierto, colgado de un clavo por su cabeza. */
    private fun DrawScope.compas(clavo: Offset, largo: Float, o: Boolean, u: Float, f: Float) {
        drawCircle(lam(HIERRO_SOMBRA, o, f), 0.45f * u, clavo)
        val cab = Offset(clavo.x, clavo.y + 1.6f * u)
        listOf(-1f, 1f).forEach { s ->
            val punta = Offset(cab.x + s * largo * 0.22f, cab.y + largo)
            drawLine(sombraMueble(if (o) 0.3f else 0.22f), Offset(cab.x + 0.6f * u, cab.y + 0.8f * u), Offset(punta.x + 0.6f * u, punta.y + 0.4f * u), 0.7f * u, StrokeCap.Round)
            drawLine(lam(LATON_OSC, o, f), cab, punta, 0.7f * u, StrokeCap.Round)
            drawLine(lam(LATON_LUZ, o, f), Offset(cab.x - 0.15f * u, cab.y), Offset(punta.x - 0.15f * u - s * 0.2f * u, punta.y - largo * 0.12f), 0.22f * u, StrokeCap.Round)
            drawLine(lam(Color(0xFF9AA0A4), o, f), Offset(punta.x - s * largo * 0.02f, punta.y - largo * 0.1f), punta, 0.3f * u, StrokeCap.Round)
        }
        drawCircle(lam(LATON, o, f), 1.1f * u, cab); drawCircle(lam(LATON_LUZ, o, f), 0.4f * u, Offset(cab.x - 0.3f * u, cab.y - 0.3f * u))
        drawLine(lam(LATON_OSC, o, f), clavo, Offset(cab.x, cab.y - 0.8f * u), 0.3f * u)
    }

    /** Una barra de lápiz litográfico (la tiza grasa) en su portalápiz de [vaina], acostada en el canto. */
    private fun DrawScope.lapizLito(desde: Offset, largo: Float, o: Boolean, u: Float, f: Float, vaina: Color) {
        val g = 1.1f * u
        drawLine(sombraMueble(if (o) 0.35f else 0.25f), Offset(desde.x + 0.4f * u, desde.y + 0.4f * u), Offset(desde.x + largo + 0.4f * u, desde.y + 0.4f * u), g, StrokeCap.Round)
        drawLine(lam(mez(vaina, Color.Black, 0.3f), o, f), Offset(desde.x + largo * 0.25f, desde.y - g * 0.5f), Offset(desde.x + largo, desde.y - g * 0.5f), g, StrokeCap.Round)
        drawLine(lam(vaina, o, f), Offset(desde.x + largo * 0.25f, desde.y - g * 0.7f), Offset(desde.x + largo, desde.y - g * 0.7f), g * 0.4f, StrokeCap.Round)
        drawLine(lam(TINTA_NEGRA, o, f), Offset(desde.x, desde.y - g * 0.5f), Offset(desde.x + largo * 0.27f, desde.y - g * 0.5f), g * 0.7f, StrokeCap.Round)
        drawRect(lam(LATON_OSC, o, f), Offset(desde.x + largo * 0.23f, desde.y - g), Size(g * 0.8f, g))
    }

    /** Un pincel de marta: el mango de madera, la virola de latón y la punta entintada. */
    private fun DrawScope.pincel(desde: Offset, largo: Float, o: Boolean, u: Float, f: Float) {
        val g = 0.8f * u
        drawLine(sombraMueble(if (o) 0.35f else 0.25f), Offset(desde.x + 0.4f * u, desde.y + 0.3f * u), Offset(desde.x + largo + 0.4f * u, desde.y + 0.3f * u), g, StrokeCap.Round)
        drawLine(lam(IRIS, o, f), Offset(desde.x + largo * 0.25f, desde.y - g * 0.5f), Offset(desde.x + largo, desde.y - g * 0.4f), g, StrokeCap.Round)
        drawLine(lam(mez(IRIS, Color.White, 0.35f), o, f), Offset(desde.x + largo * 0.25f, desde.y - g * 0.75f), Offset(desde.x + largo, desde.y - g * 0.65f), g * 0.3f, StrokeCap.Round)
        drawRect(lam(LATON, o, f), Offset(desde.x + largo * 0.16f, desde.y - g), Size(largo * 0.1f, g))
        drawPath(Path().apply { moveTo(desde.x + largo * 0.16f, desde.y - g); quadraticTo(desde.x + largo * 0.04f, desde.y - g * 0.9f, desde.x, desde.y - g * 0.45f); quadraticTo(desde.x + largo * 0.04f, desde.y, desde.x + largo * 0.16f, desde.y); close() },
            lam(TINTA_NEGRA, o, f))
    }

    /** El tiralíneas de acero con su mango de ébano y la ruedecilla que lo abre. */
    private fun DrawScope.tiraLineas(desde: Offset, largo: Float, o: Boolean, u: Float, f: Float) {
        val g = 0.7f * u
        drawLine(sombraMueble(if (o) 0.35f else 0.25f), Offset(desde.x + 0.4f * u, desde.y + 0.3f * u), Offset(desde.x + largo + 0.4f * u, desde.y + 0.3f * u), g, StrokeCap.Round)
        drawLine(lam(Color(0xFF2A2420), o, f), Offset(desde.x + largo * 0.45f, desde.y - g * 0.5f), Offset(desde.x + largo, desde.y - g * 0.5f), g, StrokeCap.Round)
        drawLine(lam(Color(0xFFA7ADB2), o, f), Offset(desde.x, desde.y - g * 0.5f), Offset(desde.x + largo * 0.46f, desde.y - g * 0.5f), g * 0.6f, StrokeCap.Round)
        drawCircle(lam(Color(0xFF7E848A), o, f), g * 0.6f, Offset(desde.x + largo * 0.3f, desde.y - g * 0.9f))
    }

    /** El vaso de vidrio con el agua turbia y tres pinceles. */
    private fun DrawScope.vasoPinceles(base: Offset, alto: Float, o: Boolean, u: Float, f: Float) {
        val an = alto * 0.5f
        sombraContacto(Offset(base.x + an * 0.3f, base.y), an * 1.4f, alto * 0.14f, if (o) 0.35f else 0.25f)
        listOf(-0.3f to IRIS, 0.05f to HAYA, 0.35f to TALLO_SOMBRA).forEachIndexed { k, (dx, c) ->
            val pie = Offset(base.x + dx * an * 0.5f, base.y - alto * 0.2f)
            val punta = Offset(base.x + dx * an * 1.4f, base.y - alto * (1.5f + 0.15f * k))
            drawLine(lam(c, o, f), pie, punta, 0.45f * u, StrokeCap.Round)
            drawLine(lam(LATON, o, f), Offset(pie.x + (punta.x - pie.x) * 0.55f, pie.y + (punta.y - pie.y) * 0.55f), Offset(pie.x + (punta.x - pie.x) * 0.65f, pie.y + (punta.y - pie.y) * 0.65f), 0.55f * u)
        }
        val vaso = Rect(base.x - an / 2, base.y - alto, base.x + an / 2, base.y)
        drawRect(lam(Color(0xFFB7C6BE), o, f).copy(alpha = 0.55f), vaso.topLeft, vaso.size)
        drawRect(lam(Color(0xFF8E9A9C), o, f).copy(alpha = 0.6f), Offset(vaso.left, vaso.top + alto * 0.4f), Size(an, alto * 0.6f))
        drawRect(lam(PLUMA, o, f).copy(alpha = 0.35f), vaso.topLeft, vaso.size, style = Stroke(0.18f * u))
        drawLine(luzMueble(if (o) 0.1f + 0.4f * f else 0.6f), Offset(vaso.left + an * 0.18f, vaso.top + alto * 0.1f), Offset(vaso.left + an * 0.18f, vaso.bottom - alto * 0.1f), 0.3f * u)
    }

    /** Un tirador de latón en forma de hoja, con su roseta. */
    private fun DrawScope.tirador(c: Offset, r: Float, o: Boolean, f: Float) {
        drawCircle(sombraMueble(if (o) 0.35f else 0.25f), r * 0.6f, Offset(c.x + r * 0.2f, c.y + r * 0.3f))
        drawPath(TrazosNouveau.hojaLanza(Offset(c.x - r * 1.6f, c.y + r * 0.1f), Offset(c.x + r * 1.6f, c.y + r * 0.1f), r * 0.9f, 0.08f), lam(LATON_OSC, o, f))
        drawPath(TrazosNouveau.hojaLanza(Offset(c.x - r * 1.5f, c.y), Offset(c.x + r * 1.5f, c.y), r * 0.6f, 0.08f), lam(LATON, o, f))
        drawCircle(lam(LATON_LUZ, o, f), r * 0.32f, Offset(c.x - r * 0.2f, c.y - r * 0.1f))
    }
    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.OTRAS_VIDAS))
        val viga = h * 0.06f
        val cima = h * 0.215f                                    // la tapa del casillero, donde descansan las cosas
        val tapaB = ni.top
        val suelo = h * 0.965f
        val foco = Offset(w * 0.905f, h * 0.075f)
        fun fl(p: Offset): Float = if (o) cerca(p, foco, w * 0.3f) else 0f

        // El muro, el piso y, arriba, la viga del tragaluz con sus ménsulas y la luz que baja.
        yeso(Rect(0f, viga, w, suelo), o, u, e.semilla + 1)
        piso(Rect(0f, suelo, w, h), o, u, e.semilla + 2)
        rayos(Rect(0f, viga, w, cima), o, u, w * 0.1f, 0.4f)
        val vg = Rect(0f, 0f, w, viga)
        rect(vg, n(HIERRO, o))
        drawRect(n(mez(HIERRO, Color.White, 0.25f), o), Offset(0f, vg.bottom - 1.6f * u), Size(w, 0.45f * u))
        drawRect(n(HIERRO_SOMBRA, o), Offset(0f, vg.bottom - 0.7f * u), Size(w, 0.7f * u))
        var xr = 1.6f * u
        while (xr < w) { drawCircle(n(HIERRO_SOMBRA, o), 0.35f * u, Offset(xr, vg.bottom - 2.8f * u)); xr += 3.4f * u }
        drawRect(sombraMueble(if (o) 0.4f else 0.25f), Offset(0f, vg.bottom), Size(w, 1.4f * u))
        with(TrazosNouveau) {
            var xm = w * 0.05f
            while (xm < w) { latigo(Offset(xm, vg.bottom), Offset(xm - 2.4f * u, vg.bottom + 5.6f * u), 1.2f * u, 1.1f * u, n(HIERRO, o), 0.22f, false); xm += w * 0.1f }
        }
        // De noche, la lámpara de encima del casillero alumbra el muro (fuera de las casillas).
        if (o) fueraDe(ni) { halo(foco, w * 0.34f, LUZ_HALO, 0.75f); halo(foco, w * 0.12f, LUZ, 0.6f) }

        // El casillero: la sombra en el muro, los montantes de los lados con su ménsula tallada y la tapa moldurada.
        val mueble = Rect(w * 0.018f, cima, w * 0.982f, suelo)
        sombraPieza(mueble, 2.6f * u, if (o) 0.42f else 0.26f)
        listOf(Rect(mueble.left, cima, ni.left, suelo), Rect(ni.right, cima, mueble.right, suelo)).forEachIndexed { k, r ->
            liston(r, o, u, e.semilla + 10 + k, ROBLE, ROBLE_OSC, ROBLE_LUZ, vertical = true, f = fl(r.center))
        }
        // El cuerpo de las casillas: el nicho, con una piedra graneada en el fondo de cada una.
        rect(ni, nicho(e, o, PIEDRA_LUZ))
        val cw = ni.width / 5f
        val dv = (cw - 47f * density).coerceIn(3f * density, cw * 0.12f)
        for (i in 0 until 5) {
            val c = Rect(ni.left + cw * i + (if (i == 0) 0f else dv / 2), ni.top, ni.left + cw * (i + 1) - (if (i == 4) 0f else dv / 2), ni.bottom)
            clipRect(c.left, c.top, c.right, c.bottom) {
                // El grano de la piedra, muy fino, y los cantos de la piedra contra la madera.
                for (k in 0 until 26) drawCircle((if (o) PIEDRA_LUZ else PIEDRA_OSC).copy(alpha = if (o) 0.04f else 0.08f), 0.18f * u,
                    Offset(c.left + c.width * azar(e.semilla + i, k), c.top + c.height * azar(e.semilla + i, k + 50)))
                drawRect(sombraMueble(if (o) 0.22f else 0.14f), c.topLeft, Size(c.width, 1.6f * u))
                drawRect(sombraMueble(if (o) 0.12f else 0.07f), c.topLeft, Size(c.width, 3.6f * u))
                drawRect(sombraMueble(if (o) 0.16f else 0.1f), c.topLeft, Size(1.2f * u, c.height))
                drawRect(luzMueble(if (o) 0.02f else 0.12f), Offset(c.right - 0.8f * u, c.top), Size(0.8f * u, c.height))
            }
        }
        // Los montantes entre casillas, de roble, con su ménsula en hoja bajo la tapa.
        for (i in 1 until 5) {
            val x = ni.left + cw * i
            val r = Rect(x - dv / 2, ni.top, x + dv / 2, ni.bottom)
            drawRect(sombraMueble(if (o) 0.25f else 0.16f), Offset(r.right, r.top), Size(1f * u, r.height))
            liston(r, o, u, e.semilla + 20 + i, ROBLE, ROBLE_OSC, ROBLE_LUZ, vertical = true, f = fl(r.center))
        }
        // La boca de cada casilla, en arco: las esquinas de arriba de roble, con un rizo tallado (como un tallo que se enrosca).
        for (i in 0 until 5) {
            val l = ni.left + cw * i + (if (i == 0) 0f else dv / 2); val rr = ni.left + cw * (i + 1) - (if (i == 4) 0f else dv / 2)
            val ra = 5.2f * u
            listOf(-1f, 1f).forEach { s ->
                val x = if (s < 0) l else rr
                val esq = Path().apply {
                    moveTo(x, ni.top); lineTo(x - s * ra, ni.top)
                    quadraticTo(x - s * ra * 0.15f, ni.top + ra * 0.15f, x, ni.top + ra * 1.4f); close()
                }
                val fe = fl(Offset(x, ni.top))
                translate(0.5f * u, 0.7f * u) { drawPath(esq, sombraMueble(if (o) 0.25f else 0.16f)) }
                drawPath(esq, lam(ROBLE, o, fe))
                drawPath(esq, lam(ROBLE_OSC, o, fe), style = Stroke(0.25f * u))
                drawPath(TrazosNouveau.linea(TrazosNouveau.puntosRizo(Offset(x - s * ra * 0.82f, ni.top + 0.5f * u), Offset(s, 0.6f), ra * 0.2f, s < 0f, 1.1f, 14)),
                    lam(ROBLE_OSC, o, fe), style = Stroke(0.3f * u, cap = StrokeCap.Round))
            }
        }
        // La tapa moldurada: el canto que vuela, el cordón y el friso.
        val tapa = Rect(mueble.left - 1.2f * u, cima, mueble.right + 1.2f * u, cima + 2.2f * u)
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(tapa.left, tapa.bottom), Size(tapa.width, 1.2f * u))
        liston(tapa, o, u, e.semilla + 30, ROBLE, ROBLE_OSC, ROBLE_LUZ, f = fl(Offset(w * 0.85f, cima)))
        val friso = Rect(mueble.left, tapa.bottom, mueble.right, tapaB)
        madera(friso, o, u, e.semilla + 31, ROBLE, ROBLE_OSC, ROBLE_LUZ, f = fl(Offset(w * 0.85f, friso.center.y)) * 0.6f)
        drawRect(sombraMueble(if (o) 0.3f else 0.2f), Offset(friso.left, friso.bottom - 0.8f * u), Size(friso.width, 0.8f * u))
        drawRect(sombraMueble(if (o) 0.25f else 0.18f), friso.topLeft, Size(friso.width, 0.6f * u))
        // La talla del friso: dos líneas de látigo que nacen de una flor en el medio y se enroscan hacia los lados.
        with(TrazosNouveau) {
            val ff = fl(Offset(w * 0.85f, friso.center.y)) * 0.6f
            val talla = lam(ROBLE_OSC, o, 0f); val tallaD = lam(ROBLE_OSC, o, ff)
            val cy = friso.center.y + 0.2f * u
            latigo(Offset(w * 0.47f, cy), Offset(w * 0.12f, cy - 0.2f * u), 1.2f * u, 0.7f * u, talla, 0.035f, false, sombra = false)
            latigo(Offset(w * 0.53f, cy), Offset(w * 0.88f, cy - 0.2f * u), 1.2f * u, 0.7f * u, tallaD, -0.035f, true, sombra = false)
            florAbierta(Offset(w * 0.5f, cy + 0.6f * u), 2.4f * u, lam(mez(ROBLE, ROBLE_OSC, 0.4f), o, 0f), 1f, -PI.toFloat() / 2, lam(ROBLE_OSC, o, 0f), lam(ROBLE_LUZ, o, 0f))
        }

        // El riel de abajo con los portaetiquetas de latón (sin escribir) y los cajones con su tirador de hoja.
        val riel = Rect(mueble.left, ni.bottom, mueble.right, ni.bottom + 3f * u)
        liston(riel, o, u, e.semilla + 40, ROBLE, ROBLE_OSC, ROBLE_LUZ, f = fl(riel.center) * 0.5f)
        for (i in 0 until 5) {
            val cx = ni.left + cw * (i + 0.5f)
            val et = Rect(cx - 3f * u, riel.top + 0.6f * u, cx + 3f * u, riel.bottom - 0.6f * u)
            drawRect(lam(LATON_OSC, o, fl(et.center)), et.topLeft, et.size)
            drawRect(lam(VELIN, o, fl(et.center)), et.deflate(0.35f * u).topLeft, et.deflate(0.35f * u).size)
            drawLine(lam(LATON_LUZ, o, fl(et.center)), et.topLeft, Offset(et.right, et.top), 0.25f * u)
        }
        val cajones = Rect(mueble.left, riel.bottom, mueble.right, suelo - 2.4f * u)
        madera(cajones, o, u, e.semilla + 41, ROBLE, ROBLE_OSC, ROBLE_LUZ, f = fl(cajones.center) * 0.4f)
        for (k in 0 until 3) {
            val c = Rect(cajones.left + cajones.width * k / 3f + 1.4f * u, cajones.top + 1f * u, cajones.left + cajones.width * (k + 1) / 3f - 1.4f * u, cajones.bottom - 1f * u)
            drawRect(sombraMueble(if (o) 0.4f else 0.3f), c.topLeft, c.size, style = Stroke(0.4f * u))
            drawLine(luzMueble(if (o) 0.04f else 0.3f), Offset(c.left, c.bottom + 0.25f * u), Offset(c.right, c.bottom + 0.25f * u), 0.25f * u)
            tirador(c.center, 1.2f * u, o, fl(c.center))
        }
        val zoc = Rect(mueble.left + 0.8f * u, suelo - 2.4f * u, mueble.right - 0.8f * u, suelo)
        rect(zoc, n(ROBLE_OSC, o))
        drawRect(luzMueble(if (o) 0.04f else 0.2f), zoc.topLeft, Size(zoc.width, 0.3f * u))

        // Encima del casillero: dos pilas de piedras, la losa de entintar con su rodillo y su espátula, los frascos y la lámpara.
        fun pila(x0: Float, ancho: Float, capas: List<Pair<Float, Float>>, semilla: Int) {
            capas.fold(cima) { y, (desp, gr) ->
                val r = Rect(x0 + ancho * desp, y - gr * u, x0 + ancho * (desp + 0.86f), y)
                piedraCanto(r, o, u, semilla + (gr * 13).toInt(), fl(r.center))
                r.top
            }
        }
        pila(w * 0.05f, w * 0.13f, listOf(0f to 3.6f, 0.07f to 4.2f, 0.02f to 3.4f, 0.12f to 3.8f), e.semilla + 50)
        pila(w * 0.21f, w * 0.1f, listOf(0.04f to 4.4f, 0f to 3.6f), e.semilla + 60)
        // La losa de entintar: una piedra gruesa con su capa de tinta negra extendida, el rodillo y la espátula.
        val losa = Rect(w * 0.42f, cima - 3f * u, w * 0.66f, cima)
        piedraCanto(losa, o, u, e.semilla + 70, fl(losa.center))
        val capa = Rect(losa.left + losa.width * 0.1f, losa.top - 0.25f * u, losa.right - losa.width * 0.2f, losa.top + 0.5f * u)
        drawRoundRect(lam(TINTA_NEGRA, o, fl(losa.center)), capa.topLeft, capa.size, CornerRadius(0.4f * u))
        rodillo(Offset(losa.left + losa.width * 0.04f, losa.top - 2.4f * u), Offset(losa.left + losa.width * 0.7f, losa.top - 2.6f * u), 4.4f * u, o, fl(losa.center))
        val esp0 = Offset(losa.left + losa.width * 0.72f, losa.top - 0.4f * u); val esp1 = Offset(losa.right + 2f * u, losa.top - 1.6f * u)
        drawLine(lam(Color(0xFFA7ADB2), o, fl(esp0)), esp0, Offset(esp0.x + (esp1.x - esp0.x) * 0.55f, esp0.y + (esp1.y - esp0.y) * 0.55f), 0.9f * u, StrokeCap.Round)
        drawLine(lam(HAYA_OSC, o, fl(esp1)), Offset(esp0.x + (esp1.x - esp0.x) * 0.55f, esp0.y + (esp1.y - esp0.y) * 0.55f), esp1, 1.1f * u, StrokeCap.Round)
        // Los frascos de tinta: violeta, azul de Prusia, oro y salvia.
        listOf(Triple(0.705f, 6.4f, IRIS), Triple(0.745f, 5.4f, TINTA_AZUL), Triple(0.78f, 6f, mez(ORO, IRIS_LENGUA, 0.4f)), Triple(0.815f, 4.6f, TALLO_SOMBRA)).forEach { (x, al, c) ->
            frasco(Offset(w * x, cima + 0.2f * u), al * u, c, o, fl(Offset(w * x, cima)))
        }
        // La lámpara de vidrio de Nancy, a la derecha.
        lamparaNancy(Offset(w * 0.905f, cima + 0.2f * u), h * 0.2f, o)

        granoMueble(Rect(0f, 0f, w, h), o)
    }
    // ---------------------------------------------------------------- Íconos

    /** El color de un ícono de noche: apenas hundido, para que se lea sobre la tarjeta. */
    private fun ic(c: Color, o: Boolean): Color = if (o) mez(c, NOCHE, 0.3f) else c

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val c = Offset(w / 2, h / 2)
        val s = h * 0.43f
        val p = s * 0.035f
        val o = oscuro
        when (id) {
            "unesco" -> grandPalais(c, s, p, o, e)
            "rascar" -> {
                // Una piedra litográfica entintada de negro: el rascador del litógrafo la raspa y aparece el Sena en la caliza.
                val cara = Path().apply {
                    moveTo(c.x - s * 1.75f, c.y - s * 0.38f); lineTo(c.x + s * 0.55f, c.y - s * 0.62f)
                    lineTo(c.x + s * 1.15f, c.y + s * 0.22f); lineTo(c.x - s * 1.2f, c.y + s * 0.55f); close()
                }
                val canto = Path().apply {
                    moveTo(c.x - s * 1.75f, c.y - s * 0.38f); lineTo(c.x - s * 1.2f, c.y + s * 0.55f); lineTo(c.x + s * 1.15f, c.y + s * 0.22f)
                    lineTo(c.x + s * 1.15f, c.y + s * 0.5f); lineTo(c.x - s * 1.2f, c.y + s * 0.85f); lineTo(c.x - s * 1.75f, c.y - s * 0.08f); close()
                }
                translate(p * 2.5f, p * 3.5f) { drawPath(canto, sombraMueble(0.35f)); drawPath(cara, sombraMueble(0.3f)) }
                drawPath(canto, ic(PIEDRA_CANTO, o))
                drawPath(Path().apply { moveTo(c.x - s * 1.2f, c.y + s * 0.55f); lineTo(c.x + s * 1.15f, c.y + s * 0.22f); lineTo(c.x + s * 1.15f, c.y + s * 0.5f); lineTo(c.x - s * 1.2f, c.y + s * 0.85f); close() },
                    ic(PIEDRA_OSC, o))
                listOf(0.35f, 0.62f).forEach { f -> drawLine(ic(mez(PIEDRA_OSC, PLUMA, 0.3f), o).copy(alpha = 0.6f), Offset(c.x - s * 1.2f, c.y + s * (0.55f + 0.3f * f)), Offset(c.x + s * 1.15f, c.y + s * (0.22f + 0.28f * f)), p * 0.6f) }
                drawPath(cara, ic(PIEDRA_LUZ, o))
                // La tinta negra cubre la cara; el raspado la levanta en una franja ancha.
                val raspado = Path().apply {
                    moveTo(c.x - s * 1.35f, c.y - s * 0.12f)
                    cubicTo(c.x - s * 0.8f, c.y - s * 0.5f, c.x - s * 0.1f, c.y - s * 0.5f, c.x + s * 0.45f, c.y - s * 0.35f)
                    lineTo(c.x + s * 0.72f, c.y + s * 0.1f)
                    cubicTo(c.x + s * 0.1f, c.y + s * 0.35f, c.x - s * 0.6f, c.y + s * 0.42f, c.x - s * 1.1f, c.y + s * 0.3f); close()
                }
                clipPath(cara) {
                    drawPath(cara, ic(TINTA_NEGRA, o))
                    for (k in 0 until 4) drawLine(Color.White.copy(alpha = 0.07f), Offset(c.x - s * (1.6f - 0.6f * k), c.y + s * 0.5f), Offset(c.x - s * (1.1f - 0.6f * k), c.y - s * 0.6f), p * 1.2f)
                    clipPath(raspado) {
                        drawPath(cara, ic(PIEDRA_LUZ, o))
                        for (k in 0 until 30) drawCircle(ic(PIEDRA_OSC, o).copy(alpha = 0.4f), p * 0.35f, Offset(c.x - s * 1.4f + s * 2.2f * azar(k, 3), c.y - s * 0.5f + s * 0.9f * azar(k, 5)))
                        // El Sena que da la vuelta, con la isla, y el punto del lugar.
                        val rio = Path().apply {
                            moveTo(c.x - s * 1.3f, c.y + s * 0.25f)
                            cubicTo(c.x - s * 0.7f, c.y - s * 0.45f, c.x - s * 0.2f, c.y + s * 0.25f, c.x + s * 0.2f, c.y - s * 0.05f)
                            cubicTo(c.x + s * 0.4f, c.y - s * 0.2f, c.x + s * 0.55f, c.y - s * 0.3f, c.x + s * 0.75f, c.y - s * 0.45f)
                        }
                        drawPath(rio, ic(TINTA_AZUL, o).copy(alpha = 0.85f), style = Stroke(p * 2.6f, cap = StrokeCap.Round))
                        drawOval(ic(PIEDRA_LUZ, o), Offset(c.x - s * 0.42f, c.y - s * 0.05f), Size(s * 0.24f, s * 0.08f))
                        drawCircle(e.c1, p * 2.4f, Offset(c.x - s * 0.05f, c.y - s * 0.2f))
                        drawCircle(ic(PLUMA, o), p * 2.4f, Offset(c.x - s * 0.05f, c.y - s * 0.2f), style = Stroke(p * 0.5f))
                    }
                }
                drawPath(raspado, ic(TINTA_NEGRA, o).copy(alpha = 0.4f), style = Stroke(p * 0.8f))
                // Las virutas de tinta que levanta el rascador.
                listOf(0.86f to 0.16f, 0.95f to 0.06f, 0.8f to 0.28f).forEachIndexed { k, (fx, fy) ->
                    val v = Offset(c.x + s * fx, c.y + s * fy)
                    drawPath(Path().apply { moveTo(v.x - p * 2f, v.y); quadraticTo(v.x, v.y - p * (2.4f + k), v.x + p * 2f, v.y + p * 0.4f); quadraticTo(v.x, v.y - p * 0.8f, v.x - p * 2f, v.y); close() }, ic(TINTA_NEGRA, o))
                }
                // El rascador: la hoja de acero triangular y el mango de haya con su virola.
                val filo = Offset(c.x + s * 0.7f, c.y + s * 0.02f)
                val mango0 = Offset(c.x + s * 1.25f, c.y - s * 0.55f); val mango1 = Offset(c.x + s * 1.75f, c.y - s * 1.0f)
                drawLine(sombraMueble(0.35f), Offset(filo.x + p * 3, filo.y + p * 3), Offset(mango1.x + p * 3, mango1.y + p * 4), p * 4f, StrokeCap.Round)
                drawPath(Path().apply { moveTo(filo.x - p * 2.5f, filo.y + p); lineTo(filo.x + p * 1.5f, filo.y + p * 1.5f); lineTo(mango0.x + p * 1.6f, mango0.y + p * 1.2f); lineTo(mango0.x - p * 1.6f, mango0.y - p * 1.2f); close() },
                    ic(Color(0xFFB3B9BE), o))
                drawLine(ic(Color.White, o).copy(alpha = 0.7f), Offset(filo.x - p * 1.5f, filo.y), Offset(mango0.x - p * 1f, mango0.y - p * 0.6f), p * 0.6f)
                drawLine(ic(LATON, o), Offset(mango0.x - p * 0.6f, mango0.y + p * 0.4f), Offset(mango0.x + p * 1.4f, mango0.y - p * 1.4f), p * 3.6f)
                drawLine(ic(HAYA_OSC, o), Offset(mango0.x + p * 1.2f, mango0.y - p * 1.2f), mango1, p * 4.4f, StrokeCap.Round)
                drawLine(ic(HAYA, o), Offset(mango0.x + p * 1f, mango0.y - p * 2f), Offset(mango1.x - p * 0.4f, mango1.y - p * 0.8f), p * 1.6f, StrokeCap.Round)
            }
            else -> {
                // Tus lugares: una prueba de imprenta con el plano de París (el Sena y sus islas) y tres chinchetas en flor.
                val hoja = Rect(c.x - s * 1.85f, c.y - s * 0.82f, c.x + s * 1.85f, c.y + s * 0.82f)
                drawRect(sombraMueble(0.3f), Offset(hoja.left + p * 2, hoja.top + p * 3), hoja.size)
                rect(hoja, ic(VELIN, o))
                val dentro = hoja.deflate(s * 0.14f)
                clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
                    rect(dentro, ic(mez(VELIN, MALVA, 0.18f), o))
                    // Las manzanas de casas, como un tejido de líneas finas, y los bulevares.
                    for (k in 0 until 9) drawLine(ic(mez(VELIN, TALLO_SOMBRA, 0.35f), o), Offset(dentro.left + dentro.width * k / 8f, dentro.top), Offset(dentro.left + dentro.width * (k - 1.5f) / 8f, dentro.bottom), p * 0.4f)
                    for (k in 0 until 5) drawLine(ic(mez(VELIN, TALLO_SOMBRA, 0.35f), o), Offset(dentro.left, dentro.top + dentro.height * k / 4f), Offset(dentro.right, dentro.top + dentro.height * (k + 0.4f) / 4f), p * 0.4f)
                    val sena = Path().apply {
                        moveTo(dentro.left, c.y + s * 0.05f)
                        cubicTo(c.x - s * 1.1f, c.y + s * 0.5f, c.x - s * 0.5f, c.y - s * 0.25f, c.x + s * 0.1f, c.y + s * 0.05f)
                        cubicTo(c.x + s * 0.7f, c.y + s * 0.35f, c.x + s * 1.1f, c.y - s * 0.45f, dentro.right, c.y - s * 0.3f)
                    }
                    drawPath(sena, ic(TINTA_AZUL, o).copy(alpha = 0.75f), style = Stroke(p * 3.2f, cap = StrokeCap.Round))
                    drawOval(ic(mez(VELIN, MALVA, 0.18f), o), Offset(c.x - s * 0.35f, c.y - s * 0.12f), Size(s * 0.36f, s * 0.1f))
                    // Un jardín: unos puntos verdes.
                    listOf(0.24f to 0.3f, 0.3f to 0.36f, 0.36f to 0.28f).forEach { (fx, fy) -> drawCircle(ic(TALLO, o), p * 1.6f, Offset(dentro.left + dentro.width * fx, dentro.top + dentro.height * fy)) }
                }
                drawRect(ic(TALLO_SOMBRA, o), dentro.topLeft, dentro.size, style = Stroke(p * 0.7f))
                // Las cruces de registro de la prueba.
                listOf(Offset(c.x, hoja.top + s * 0.07f), Offset(c.x, hoja.bottom - s * 0.07f)).forEach { q ->
                    drawLine(ic(PLUMA, o).copy(alpha = 0.6f), Offset(q.x - s * 0.08f, q.y), Offset(q.x + s * 0.08f, q.y), p * 0.4f)
                    drawLine(ic(PLUMA, o).copy(alpha = 0.6f), Offset(q.x, q.y - s * 0.05f), Offset(q.x, q.y + s * 0.05f), p * 0.4f)
                }
                val pts = listOf(Offset(c.x - s * 1.15f, c.y - s * 0.22f), Offset(c.x + s * 0.0f, c.y + s * 0.38f), Offset(c.x + s * 1.15f, c.y - s * 0.1f))
                drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.7f, c.y + s * 0.45f, pts[1].x, pts[1].y); quadraticTo(c.x + s * 0.7f, c.y + s * 0.35f, pts[2].x, pts[2].y) },
                    ic(PLUMA, o).copy(alpha = 0.65f), style = Stroke(p * 1.1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(p * 2.5f, p * 2f))))
                listOf(e.c1, e.c2, ic(ORO, o)).forEachIndexed { k, col -> chinchetaFlor(pts[k], s * 0.2f, col, o) }
            }
        }
    }

    /** Una chincheta de latón con cabeza de flor de cinco pétalos, del color [col], y su sombra. */
    private fun DrawScope.chinchetaFlor(c: Offset, r: Float, col: Color, o: Boolean) {
        drawCircle(sombraMueble(0.35f), r * 0.95f, Offset(c.x + r * 0.35f, c.y + r * 0.5f))
        for (k in 0 until 5) {
            val a = -PI.toFloat() / 2 + k * 2f * PI.toFloat() / 5f
            drawCircle(mez(col, Color.Black, 0.2f), r * 0.46f, Offset(c.x + cos(a) * r * 0.52f, c.y + sin(a) * r * 0.52f))
            drawCircle(col, r * 0.38f, Offset(c.x + cos(a) * r * 0.5f - r * 0.04f, c.y + sin(a) * r * 0.5f - r * 0.04f))
        }
        drawCircle(ic(LATON, o), r * 0.3f, c)
        drawCircle(ic(LATON_LUZ, o), r * 0.12f, Offset(c.x - r * 0.08f, c.y - r * 0.08f))
    }

    /**
     * El Grand Palais de la Exposición de 1900 sobre el Sena: el muelle de piedra, la fachada con su columnata y su pórtico,
     * la nave de hierro y vidrio y la cúpula con su linterna y su bandera; a un lado, un castaño.
     */
    private fun DrawScope.grandPalais(c: Offset, s: Float, p: Float, o: Boolean, e: EstadoIcono) {
        val agua = c.y + s * 0.62f
        val base = c.y + s * 0.42f
        val piedra = ic(FACHADA, o); val piedraL = ic(mez(FACHADA, Color.White, 0.4f), o); val piedraS = ic(mez(FACHADA, PLUMA, 0.3f), o)
        val vidrio = ic(mez(CIELO, VIDRIO_VERDE, 0.4f), o); val vidrioL = ic(mez(CIELO, Color.White, 0.5f), o)
        val hierro = ic(HIERRO, o)
        // El Sena con sus reflejos y el muelle.
        val sena = Rect(c.x - s * 2.1f, agua, c.x + s * 2.1f, c.y + s * 1.0f)
        rect(sena, ic(mez(TINTA_AZUL, CIELO, 0.45f), o))
        for (k in 0 until 6) drawLine(ic(CIELO, o).copy(alpha = 0.6f), Offset(c.x - s * (1.8f - 0.65f * k), agua + s * (0.12f + 0.1f * (k % 2))), Offset(c.x - s * (1.55f - 0.65f * k), agua + s * (0.12f + 0.1f * (k % 2))), p * 0.8f)
        val muelle = Rect(c.x - s * 2.1f, base, c.x + s * 2.1f, agua)
        rect(muelle, piedraS)
        drawLine(piedraL, Offset(muelle.left, muelle.top), Offset(muelle.right, muelle.top), p)
        for (k in 0 until 9) drawLine(ic(PLUMA, o).copy(alpha = 0.2f), Offset(muelle.left + muelle.width * k / 8f, muelle.top), Offset(muelle.left + muelle.width * k / 8f, muelle.bottom), p * 0.5f)
        // La nave: la bóveda de vidrio larga detrás de la fachada, con sus arcos de hierro.
        val naveTop = c.y - s * 0.62f
        val nave = Path().apply {
            moveTo(c.x - s * 1.7f, c.y - s * 0.1f); quadraticTo(c.x - s * 1.7f, naveTop, c.x - s * 1.2f, naveTop)
            lineTo(c.x + s * 1.2f, naveTop); quadraticTo(c.x + s * 1.7f, naveTop, c.x + s * 1.7f, c.y - s * 0.1f); close()
        }
        drawPath(nave, vidrio)
        clipPath(nave) {
            for (k in 0 until 14) { val x = c.x - s * 1.7f + s * 3.4f * k / 13f; drawLine(hierro, Offset(x, naveTop), Offset(x, c.y), p * 0.7f) }
            drawLine(hierro, Offset(c.x - s * 1.7f, c.y - s * 0.38f), Offset(c.x + s * 1.7f, c.y - s * 0.38f), p * 0.6f)
            drawLine(vidrioL, Offset(c.x - s * 1.4f, naveTop + p * 2), Offset(c.x - s * 0.2f, naveTop + p * 2), p * 1.2f)
        }
        drawPath(nave, hierro, style = Stroke(p * 0.8f))
        // La cúpula de vidrio en el medio, con su linterna y la bandera.
        val cup = Rect(c.x - s * 0.55f, c.y - s * 1.05f, c.x + s * 0.55f, c.y - s * 0.15f)
        drawArc(vidrio, 180f, 180f, true, Offset(cup.left, cup.top), Size(cup.width, cup.height * 1.6f))
        for (k in -3..3) {
            val x = c.x + k * cup.width * 0.14f
            val yTop = cup.top + cup.height * 0.8f * (1f - sqrt(1f - (k / 3.6f) * (k / 3.6f)))
            drawLine(hierro, Offset(x, yTop), Offset(x, cup.top + cup.height * 0.8f), p * 0.7f)
        }
        drawArc(hierro, 180f, 180f, false, Offset(cup.left, cup.top), Size(cup.width, cup.height * 1.6f), style = Stroke(p))
        drawArc(vidrioL, 200f, 50f, false, Offset(cup.left + p * 3, cup.top + p * 3), Size(cup.width - p * 6, cup.height * 1.6f - p * 6), style = Stroke(p * 1.2f))
        val lint = Rect(c.x - s * 0.1f, cup.top - s * 0.14f, c.x + s * 0.1f, cup.top + p)
        rect(lint, hierro)
        drawLine(ic(PLUMA, o), Offset(c.x, lint.top), Offset(c.x, lint.top - s * 0.32f), p * 0.7f)
        drawPath(Path().apply { moveTo(c.x, lint.top - s * 0.32f); quadraticTo(c.x + s * 0.18f, lint.top - s * 0.34f, c.x + s * 0.32f, lint.top - s * 0.24f); lineTo(c.x, lint.top - s * 0.17f); close() }, e.c1)
        // La fachada de piedra: el basamento, la columnata y la cornisa, con el pórtico más alto en el medio.
        val fach = Rect(c.x - s * 1.85f, c.y - s * 0.12f, c.x + s * 1.85f, base)
        rect(fach, piedra)
        drawRect(piedraL, fach.topLeft, Size(fach.width, p * 1.6f))
        drawRect(piedraS, Offset(fach.left, fach.top + p * 1.6f), Size(fach.width, p * 0.8f))
        val nc = 16
        for (k in 0 until nc) {
            val x = fach.left + fach.width * (k + 0.5f) / nc
            if (abs(x - c.x) < s * 0.42f) continue
            drawRect(piedraS, Offset(x - p * 1.4f, fach.top + p * 3f), Size(p * 2.8f, fach.height - p * 6f))
            drawRect(piedraL, Offset(x - p * 1.4f, fach.top + p * 3f), Size(p * 0.9f, fach.height - p * 6f))
        }
        drawRect(piedraS, Offset(fach.left, fach.bottom - p * 3f), Size(fach.width, p * 3f))
        // El pórtico: el frontón bajo con su arco de entrada.
        val por = Rect(c.x - s * 0.42f, c.y - s * 0.32f, c.x + s * 0.42f, base)
        rect(por, piedraL)
        drawPath(Path().apply { moveTo(por.left - p * 2, por.top); lineTo(c.x, por.top - s * 0.18f); lineTo(por.right + p * 2, por.top); close() }, piedra)
        drawPath(Path().apply { moveTo(por.left - p * 2, por.top); lineTo(c.x, por.top - s * 0.18f); lineTo(por.right + p * 2, por.top); close() }, piedraS, style = Stroke(p * 0.7f))
        drawArc(ic(Color(0xFF4E5A62), o), 180f, 180f, true, Offset(c.x - s * 0.2f, por.top + s * 0.12f), Size(s * 0.4f, s * 0.4f))
        drawRect(ic(Color(0xFF4E5A62), o), Offset(c.x - s * 0.2f, por.top + s * 0.32f), Size(s * 0.4f, por.bottom - por.top - s * 0.32f))
        for (k in -1..1 step 2) drawRect(piedraS, Offset(c.x + k * s * 0.3f - p, por.top + p * 2), Size(p * 2f, por.height - p * 2))
        // Un castaño a la derecha, junto al muelle.
        val tronco = Offset(c.x + s * 1.95f, base)
        drawLine(ic(NOGAL, o), tronco, Offset(tronco.x, tronco.y - s * 0.5f), p * 2.4f, StrokeCap.Round)
        listOf(Offset(0f, -0.7f) to 0.3f, Offset(-0.2f, -0.55f) to 0.24f, Offset(0.18f, -0.52f) to 0.22f, Offset(0.02f, -0.92f) to 0.2f).forEach { (d, r) ->
            drawCircle(ic(TALLO_SOMBRA, o), s * r, Offset(tronco.x + d.x * s, tronco.y + d.y * s))
        }
        drawCircle(ic(TALLO, o), s * 0.12f, Offset(tronco.x - s * 0.08f, tronco.y - s * 0.86f))
    }
    // ---------------------------------------------------------------- Notas

    /** El tono de noche de los muebles del Diario (receta de la luz, versión sutil): el mismo azul negro, un poco menos hondo. */
    private fun nm(c: Color, o: Boolean): Color {
        if (!o) return c
        return mez(Color(c.red * AIRE.red, c.green * AIRE.green, c.blue * AIRE.blue, c.alpha), NOCHE.copy(alpha = c.alpha), 0.52f)
    }

    /** El tono de noche de los muebles del Diario, a la luz de la tulipa: [f] es cuánto le llega. */
    private fun lamm(c: Color, o: Boolean, f: Float): Color = if (!o || f <= 0f) nm(c, o) else mez(nm(c, o), mez(c, CALIDO, 0.3f), f.coerceIn(0f, 1f) * 0.8f)

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.NOTAS))
        val marco = Rect(w * 0.022f, h * 0.085f, w * 0.978f, h * 0.875f)
        val punta = Offset(w * 0.915f, h * 0.0f)
        val foco = Offset(w * 0.915f, h * 0.075f)
        fun fl(q: Offset): Float = if (o) cerca(q, foco, w * 0.36f) else 0f

        // La pared con su papel pintado de tallos, como la de la biblioteca.
        rect(Rect(0f, 0f, w, h), nm(YESO_LUZ, o))
        for (k in 0 until 9) {
            val x = w * (k + 0.5f) / 9f + (azar(e.semilla, k) - 0.5f) * 2f * u
            with(TrazosNouveau) {
                tallo(Offset(x, h * (if (k % 2 == 0) 1.02f else 0.62f)), h * 0.34f, 1f, if (k % 2 == 0) 1f else -1f, 0.5f * u,
                    nm(mez(YESO_LUZ, TALLO, 0.28f), o), nm(mez(YESO_LUZ, TALLO_SOMBRA, 0.3f), o), nm(mez(YESO_LUZ, MALVA, 0.4f), o), true, nm(mez(YESO_LUZ, IRIS_LENGUA, 0.4f), o))
            }
        }
        pinceladas(Rect(0f, 0f, w, h), nm(YESO_SOMBRA, o), e.semilla + 3, 30, u, 0.06f)
        if (o) halo(foco, w * 0.42f, LUZ_HALO, 0.6f)

        // El tablero entero: el copete que sube en curva hasta la cresta del iris, los largueros y el travesaño de abajo.
        val cx = w / 2
        val cresta = h * 0.008f
        val hombro = marco.top
        val tablero = Path().apply {
            moveTo(marco.left, marco.bottom); lineTo(marco.left, hombro + 1.4f * u)
            quadraticTo(marco.left, hombro, marco.left + 2f * u, hombro)
            cubicTo(w * 0.24f, hombro, w * 0.3f, hombro - h * 0.01f, cx - w * 0.12f, hombro - h * 0.035f)
            cubicTo(cx - w * 0.07f, cresta, cx + w * 0.07f, cresta, cx + w * 0.12f, hombro - h * 0.035f)
            cubicTo(w * 0.7f, hombro - h * 0.01f, w * 0.76f, hombro, marco.right - 2f * u, hombro)
            quadraticTo(marco.right, hombro, marco.right, hombro + 1.4f * u)
            lineTo(marco.right, marco.bottom); close()
        }
        translate(1.2f * u, 1.8f * u) { drawPath(tablero, sombraMueble(if (o) 0.4f else 0.3f)) }
        clipPath(tablero) {
            madera(Rect(0f, 0f, w, h), o, u, e.semilla + 5, NOGAL, NOGAL_OSC, NOGAL_LUZ, vertical = true)
            if (o) halo(foco, w * 0.3f, mez(NOGAL_LUZ, CALIDO, 0.4f), 0.9f)
            // La ranura tallada que corre por el marco, a un dedo del borde, con su luz abajo.
            val ran = Rect(marco.left + 1.6f * u, hombro + 1.6f * u, marco.right - 1.6f * u, marco.bottom - 1.4f * u)
            drawRoundRect(lamm(NOGAL_OSC, o, 0f), ran.topLeft, ran.size, CornerRadius(1.6f * u), style = Stroke(0.35f * u))
            drawRoundRect(luzMueble(if (o) 0.03f else 0.18f), Offset(ran.left + 0.3f * u, ran.top + 0.3f * u), ran.size, CornerRadius(1.6f * u), style = Stroke(0.2f * u))
        }
        // La moldura que sigue el copete, en luz, y el filo de abajo en sombra.
        drawPath(tablero, luzMueble(if (o) 0.05f else 0.35f), style = Stroke(0.5f * u))
        // La cresta tallada: el iris en relieve, con dos tallos que se enroscan a los lados y un toque de oro en la lengua.
        with(TrazosNouveau) {
            val talla = lamm(NOGAL_OSC, o, 0f); val relieve = lamm(NOGAL_LUZ, o, 0f)
            latigo(Offset(cx - w * 0.04f, hombro + 0.6f * u), Offset(cx - w * 0.25f, hombro + 1.6f * u), 1.5f * u, 0.8f * u, talla, -0.1f, true, sombra = false)
            latigo(Offset(cx + w * 0.04f, hombro + 0.6f * u), Offset(cx + w * 0.25f, hombro + 1.6f * u), 1.5f * u, 0.8f * u, lamm(NOGAL_OSC, o, fl(Offset(w * 0.7f, hombro))), 0.1f, false, sombra = false)
            iris(Offset(cx, hombro + 2.2f * u), h * 0.105f, lamm(mez(NOGAL_LUZ, IRIS, 0.2f), o, 0f), relieve, talla, talla.copy(alpha = 0.8f))
            drawCircle(lamm(ORO, o, 0f), 0.45f * u, Offset(cx - 0.2f * u, hombro + 2.2f * u - h * 0.105f * 0.78f))
        }

        // Los largueros y los travesaños tienen su canto: la luz a la izquierda y arriba, la sombra a la derecha y abajo.
        drawRect(luzMueble(if (o) 0.04f else 0.25f), Offset(marco.left, hombro), Size(0.6f * u, marco.height))
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(marco.right - 0.7f * u, hombro), Size(0.7f * u, marco.height))
        drawRect(sombraMueble(if (o) 0.35f else 0.25f), Offset(marco.left, marco.bottom - 0.8f * u), Size(marco.width, 0.8f * u))
        // El rebajo alrededor del panel y el junquillo de oro.
        val reb = ni.inflate(1.8f * u)
        drawRect(lamm(NOGAL_OSC, o, 0f), reb.topLeft, reb.size)
        drawRect(luzMueble(if (o) 0.04f else 0.25f), Offset(reb.left, reb.bottom - 0.5f * u), Size(reb.width, 0.5f * u))
        drawRect(luzMueble(if (o) 0.04f + 0.2f * fl(Offset(reb.right, reb.top)) else 0.2f), Offset(reb.right - 0.5f * u, reb.top), Size(0.5f * u, reb.height))
        val junq = ni.inflate(0.7f * u)
        drawRect(lamm(ORO, o, fl(Offset(junq.right, junq.top)) * 0.6f), junq.topLeft, junq.size, style = Stroke(0.45f * u))
        // El panel de sarga verde: el nicho donde van las notas.
        rect(ni, nicho(e, o, SARGA))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            // La sarga: sus diagonales finas.
            val hilo = (if (o) SARGA else mez(SARGA, Color.White, 0.2f)).copy(alpha = if (o) 0.07f else 0.12f)
            var x = ni.left - ni.height
            while (x < ni.right) { drawLine(hilo, Offset(x, ni.bottom), Offset(x + ni.height, ni.top), 0.14f * u); x += 0.9f * u }
            drawRect(sombraMueble(if (o) 0.2f else 0.12f), ni.topLeft, Size(ni.width, 1.6f * u))
            drawRect(sombraMueble(if (o) 0.14f else 0.08f), ni.topLeft, Size(1.2f * u, ni.height))
            // Los agujeritos de las chinchetas de antes.
            for (k in 0 until 24) drawCircle(sombraMueble(if (o) 0.3f else 0.22f), 0.13f * u, Offset(ni.left + ni.width * azar(e.semilla, k + 700), ni.top + ni.height * azar(e.semilla, k + 740)))
            if (o) halo(foco, w * 0.3f, LUZ, 0.35f, 14)
        }
        // Las esquinas del marco: un rizo tallado en cada una, como el arranque de un tallo.
        with(TrazosNouveau) {
            listOf(Offset(marco.left + 2.4f * u, marco.bottom - 2.6f * u) to false, Offset(marco.right - 2.4f * u, marco.bottom - 2.6f * u) to true).forEach { (q, der) ->
                val ff = fl(q)
                drawPath(linea(puntosRizo(q, Offset(if (der) -1f else 1f, -0.4f), 1.2f * u, !der, 1.2f, 18)), lamm(NOGAL_OSC, o, ff), style = Stroke(0.45f * u, cap = StrokeCap.Round))
            }
        }

        // La repisa de abajo, con sus dos ménsulas en látigo; encima, el tintero con la pluma, el florero de Nancy y la caja de chinchetas.
        val rep = Rect(marco.left - 1.6f * u, marco.bottom, marco.right + 1.6f * u, marco.bottom + 2.6f * u)
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(rep.left + 0.6f * u, rep.bottom), Size(rep.width, 1.6f * u))
        liston(rep, o, u, e.semilla + 9, f = fl(Offset(w * 0.85f, rep.top)) * 0.5f)
        with(TrazosNouveau) {
            listOf(w * 0.12f to true, w * 0.88f to false).forEach { (x, izq) ->
                val fm = fl(Offset(x, rep.bottom))
                latigo(Offset(x, rep.bottom), Offset(x + (if (izq) 1f else -1f) * 2.4f * u, h * 0.975f), 1.4f * u, 1.6f * u, lamm(NOGAL, o, fm), if (izq) 0.2f else -0.2f, izq)
            }
        }
        val yRep = rep.top + 0.2f * u
        tintero(Offset(w * 0.2f, yRep), 4.6f * u, o, u, fl(Offset(w * 0.2f, yRep)))
        floreroNancy(Offset(w * 0.78f, yRep), 7.4f * u, o, u, fl(Offset(w * 0.78f, yRep)))
        cajita(Rect(w * 0.84f, yRep - 1.8f * u, w * 0.9f, yRep), o, u, fl(Offset(w * 0.87f, yRep)))

        // La tulipa que cuelga de su cadena arriba a la derecha: la luz del tablero de noche.
        drawLine(lamm(LATON_OSC, o, 0.5f), punta, Offset(punta.x, foco.y - 3.4f * u), 0.35f * u)
        var yk = punta.y + 0.6f * u
        while (yk < foco.y - 3.6f * u) { drawOval(lamm(LATON, o, 0.6f), Offset(punta.x - 0.35f * u, yk), Size(0.7f * u, 1f * u), style = Stroke(0.2f * u)); yk += 1f * u }
        val boca = Offset(punta.x, foco.y - 3.2f * u)
        rotate(180f, boca) { with(TrazosNouveau) { tulipa(boca, 5.2f * u, if (o) 1f else 0f) } }

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /** El tintero de cristal tallado con su tapa de latón abierta y la pluma de acero en su mango, apoyada. */
    private fun DrawScope.tintero(base: Offset, alto: Float, o: Boolean, u: Float, f: Float) {
        val an = alto * 1.1f
        sombraContacto(Offset(base.x + an * 0.3f, base.y), an * 1.4f, alto * 0.18f, if (o) 0.4f else 0.3f)
        val vaso = Path().apply {
            moveTo(base.x - an * 0.5f, base.y); lineTo(base.x - an * 0.42f, base.y - alto * 0.62f); lineTo(base.x + an * 0.42f, base.y - alto * 0.62f); lineTo(base.x + an * 0.5f, base.y); close()
        }
        drawPath(vaso, lamm(Color(0xFFB9C7C0), o, f).copy(alpha = 0.85f))
        drawPath(Path().apply { moveTo(base.x - an * 0.46f, base.y - alto * 0.05f); lineTo(base.x - an * 0.4f, base.y - alto * 0.4f); lineTo(base.x + an * 0.4f, base.y - alto * 0.4f); lineTo(base.x + an * 0.46f, base.y - alto * 0.05f); close() },
            lamm(TINTA_NEGRA, o, f).copy(alpha = 0.8f))
        for (k in -2..2) drawLine(lamm(Color.White, o, f).copy(alpha = 0.3f), Offset(base.x + k * an * 0.18f, base.y - alto * 0.04f), Offset(base.x + k * an * 0.16f, base.y - alto * 0.58f), alto * 0.03f)
        drawRect(lamm(LATON, o, f), Offset(base.x - an * 0.24f, base.y - alto * 0.74f), Size(an * 0.48f, alto * 0.12f))
        drawPath(Path().apply { moveTo(base.x + an * 0.22f, base.y - alto * 0.74f); lineTo(base.x + an * 0.42f, base.y - alto * 1.08f); lineTo(base.x + an * 0.5f, base.y - alto * 1.02f); lineTo(base.x + an * 0.3f, base.y - alto * 0.7f); close() }, lamm(LATON_OSC, o, f))
        // La pluma: el mango negro, la virola y el plumín.
        val m0 = Offset(base.x - an * 0.15f, base.y - alto * 0.7f); val m1 = Offset(base.x - an * 1.7f, base.y - alto * 1.9f)
        drawLine(lamm(TINTA_NEGRA, o, f), m0, m1, alto * 0.13f, StrokeCap.Round)
        drawLine(lamm(LATON, o, f), m0, Offset(m0.x - (m0.x - m1.x) * 0.12f, m0.y - (m0.y - m1.y) * 0.12f), alto * 0.15f)
        drawLine(lamm(Color(0xFFA7ADB2), o, f), m0, Offset(m0.x + an * 0.08f, m0.y + alto * 0.08f), alto * 0.08f, StrokeCap.Round)
    }

    /** El florero de vidrio de Nancy, del ámbar al violeta, con una vara de lirio del valle. */
    private fun DrawScope.floreroNancy(base: Offset, alto: Float, o: Boolean, u: Float, f: Float) {
        val an = alto * 0.42f
        sombraContacto(Offset(base.x + an * 0.3f, base.y), an * 1.4f, alto * 0.08f, if (o) 0.4f else 0.3f)
        // La vara con sus campanillas, que se arquea a la derecha, y su hoja.
        with(TrazosNouveau) {
            val pie = Offset(base.x, base.y - alto * 0.7f)
            drawPath(hojaLanza(pie, Offset(base.x - an * 0.9f, base.y - alto * 1.5f), an * 0.42f, 0.12f), lamm(TALLO, o, f))
            val tallo = bezier(pie, Offset(base.x + an * 0.1f, base.y - alto * 1.4f), Offset(base.x + an * 0.6f, base.y - alto * 1.6f), Offset(base.x + an * 1.1f, base.y - alto * 1.3f), 20)
            drawPath(linea(tallo), lamm(TALLO_SOMBRA, o, f), style = Stroke(0.25f * u, cap = StrokeCap.Round))
            for (k in 0 until 5) {
                val q = tallo[6 + k * 3]
                drawLine(lamm(TALLO_SOMBRA, o, f), q, Offset(q.x + 0.2f * u, q.y + 0.9f * u), 0.15f * u)
                drawCircle(lamm(Color(0xFFF4F0E4), o, f), 0.55f * u, Offset(q.x + 0.2f * u, q.y + 1.3f * u))
            }
        }
        // El vidrio: la panza que se ensancha abajo y el cuello largo, del ámbar al violeta, con su brillo.
        val v = Path().apply {
            moveTo(base.x - an * 0.18f, base.y - alto * 0.78f); lineTo(base.x + an * 0.18f, base.y - alto * 0.78f)
            cubicTo(base.x + an * 0.16f, base.y - alto * 0.45f, base.x + an * 0.62f, base.y - alto * 0.32f, base.x + an * 0.44f, base.y)
            lineTo(base.x - an * 0.44f, base.y)
            cubicTo(base.x - an * 0.62f, base.y - alto * 0.32f, base.x - an * 0.16f, base.y - alto * 0.45f, base.x - an * 0.18f, base.y - alto * 0.78f); close()
        }
        drawPath(v, lamm(VIDRIO_VIOLETA, o, f))
        clipPath(v) {
            drawRect(lamm(mez(VIDRIO_VIOLETA, VIDRIO_AMBAR, 0.5f), o, f), Offset(base.x - an, base.y - alto * 0.42f), Size(an * 2f, alto * 0.2f))
            drawRect(lamm(VIDRIO_AMBAR, o, f), Offset(base.x - an, base.y - alto * 0.24f), Size(an * 2f, alto * 0.24f))
            drawPath(TrazosNouveau.hojaLanza(Offset(base.x - an * 0.1f, base.y), Offset(base.x + an * 0.2f, base.y - alto * 0.5f), an * 0.18f, 0.2f, espada = true), lamm(VIDRIO_VERDE, o, f).copy(alpha = 0.7f))
        }
        drawLine(luzMueble(if (o) 0.1f + 0.5f * f else 0.6f), Offset(base.x - an * 0.3f, base.y - alto * 0.06f), Offset(base.x - an * 0.12f, base.y - alto * 0.5f), 0.3f * u, StrokeCap.Round)
    }

    /** La cajita de latón de las chinchetas, con la tapa entreabierta. */
    private fun DrawScope.cajita(r: Rect, o: Boolean, u: Float, f: Float) {
        sombraContacto(Offset(r.center.x + r.width * 0.2f, r.bottom), r.width * 1.2f, 0.8f * u, if (o) 0.4f else 0.3f)
        rect(r, lamm(LATON_OSC, o, f))
        drawRect(lamm(LATON, o, f), r.topLeft, Size(r.width * 0.6f, r.height))
        drawRect(lamm(LATON_LUZ, o, f), Offset(r.left - 0.2f * u, r.top - 0.5f * u), Size(r.width + 0.4f * u, 0.6f * u))
        for (k in 0 until 3) drawCircle(lamm(LATON_LUZ, o, f), 0.3f * u, Offset(r.left + r.width * (0.25f + 0.25f * k), r.top - 0.6f * u))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val semilla = e.indice * 131 + e.tipo.length * 7
        val giro = (azar(semilla, 1) - 0.5f) * 2.4f
        val t = r.deflate(2.2f * un)
        rotate(giro, t.center) {
            // La tarjeta de papel vélin, con su sombra en la sarga y el canto barbado abajo.
            drawRect(sombraMueble(if (o) 0.4f else 0.28f), Offset(t.left + 1.2f * un, t.top + 2f * un), t.size)
            val papel = nm(VELIN, o)
            rect(t, papel)
            pinceladas(t, nm(VELIN_OSC, o), semilla, 8, un, if (o) 0.05f else 0.07f)
            if (!o) drawRect(luzMueble(0.14f), t.topLeft, Size(t.width * 0.3f, t.height))
            for (k in 0 until 16) {
                val x = t.left + t.width * k / 16f
                drawLine(nm(VELIN_OSC, o), Offset(x, t.bottom - 0.3f * un), Offset(x + t.width / 16f, t.bottom - 0.3f * un + (azar(semilla, k + 30) - 0.5f) * 0.8f * un), 0.6f * un)
            }
            // El filete impreso de la tarjeta, con sus rizos en las esquinas; si está fijada, de oro.
            val fil = t.deflate(3.6f * un)
            val col = if (e.fijada) nm(mez(ORO, LATON_OSC, 0.35f), o) else nm(TALLO_SOMBRA, o).copy(alpha = 0.7f)
            drawRect(col, fil.topLeft, fil.size, style = Stroke(if (e.fijada) 0.9f * un else 0.5f * un))
            if (e.fijada) drawRect(col.copy(alpha = 0.6f), fil.deflate(1.2f * un).topLeft, fil.deflate(1.2f * un).size, style = Stroke(0.3f * un))
            listOf(fil.topLeft to Offset(1f, 1f), Offset(fil.right, fil.top) to Offset(-1f, 1f), Offset(fil.left, fil.bottom) to Offset(1f, -1f), fil.bottomRight to Offset(-1f, -1f)).forEach { (q, d) ->
                drawPath(TrazosNouveau.linea(TrazosNouveau.puntosRizo(Offset(q.x + d.x * 0.2f * un, q.y), Offset(d.x, 0f), 1.6f * un, d.x * d.y > 0f, 1.1f, 14)), col, style = Stroke(0.4f * un, cap = StrokeCap.Round))
            }
            // La viñeta del tipo, en la esquina de abajo a la derecha (fuera del texto), en el lenguaje de las flores.
            val q = Offset(r.right - r.width * 0.12f, r.bottom - r.height * 0.11f)
            val tt = r.height * 0.05f
            vineta(e.tipo, q, tt, o, un)
        }
        // La chincheta: de latón en flor o, si está fijada, el alfiler de vidrio de Nancy.
        val cl = Offset(r.center.x + (azar(semilla, 3) - 0.5f) * 6f * un, r.top + 4.6f * un)
        if (e.fijada) alfilerNancy(cl, 5.6f * un, o, un)
        else {
            drawCircle(sombraMueble(if (o) 0.4f else 0.3f), 2.8f * un, Offset(cl.x + 0.9f * un, cl.y + 1.3f * un))
            for (k in 0 until 5) {
                val a = -PI.toFloat() / 2 + k * 2f * PI.toFloat() / 5f
                drawCircle(nm(LATON_OSC, o), 1.3f * un, Offset(cl.x + cos(a) * 1.5f * un, cl.y + sin(a) * 1.5f * un))
                drawCircle(nm(LATON, o), 1.05f * un, Offset(cl.x + cos(a) * 1.45f * un - 0.15f * un, cl.y + sin(a) * 1.45f * un - 0.15f * un))
            }
            drawCircle(nm(LATON_LUZ, o), 0.8f * un, cl)
            drawCircle(nm(LATON_OSC, o), 0.35f * un, cl)
        }
    }

    /** El alfiler de sombrero de vidrio de Nancy que prende la nota fijada: la cabeza que pasa del ámbar al violeta, clavada en diagonal. */
    private fun DrawScope.alfilerNancy(c: Offset, r: Float, o: Boolean, un: Float) {
        val cola = Offset(c.x + r * 2.2f, c.y + r * 0.9f)
        drawLine(sombraMueble(if (o) 0.4f else 0.3f), Offset(c.x + un, c.y + 1.4f * un), Offset(cola.x + un, cola.y + 1.4f * un), 0.6f * un, StrokeCap.Round)
        drawLine(nm(Color(0xFFA7ADB2), o), c, cola, 0.55f * un, StrokeCap.Round)
        drawCircle(sombraMueble(if (o) 0.4f else 0.3f), r * 0.6f, Offset(c.x + r * 0.2f, c.y + r * 0.28f))
        drawCircle(nm(VIDRIO_VIOLETA, o), r * 0.55f, c)
        drawCircle(nm(mez(VIDRIO_VIOLETA, VIDRIO_AMBAR, 0.5f), o), r * 0.42f, Offset(c.x - r * 0.08f, c.y - r * 0.06f))
        drawCircle(nm(VIDRIO_AMBAR, o), r * 0.26f, Offset(c.x - r * 0.14f, c.y - r * 0.14f))
        drawCircle(luzMueble(if (o) 0.4f else 0.8f), r * 0.1f, Offset(c.x - r * 0.24f, c.y - r * 0.26f))
        drawCircle(nm(LATON, o), r * 0.18f, Offset(c.x + r * 0.5f, c.y + r * 0.2f))
    }

    /**
     * La viñeta impresa del tipo de nota, en el lenguaje de las flores: la idea es la tulipa encendida; el pensamiento, la
     * flor del pensamiento; el propósito, un brote con su capullo; el recordatorio, el nomeolvides.
     */
    private fun DrawScope.vineta(tipo: String, q: Offset, t: Float, o: Boolean, un: Float) {
        when (tipo) {
            "idea" -> {
                // La tulipa encendida: el casquillo, los tres pétalos de vidrio boca abajo y los rayos de su luz.
                val c = Offset(q.x, q.y - t * 0.7f)
                for (k in 0 until 5) {
                    val a = PI.toFloat() * (0.1f + 0.2f * k)
                    drawLine(nm(IRIS_LENGUA, o), Offset(q.x + cos(a) * t * 1.2f, c.y + t * 0.5f + sin(a) * t * 1.2f), Offset(q.x + cos(a) * t * 1.7f, c.y + t * 0.5f + sin(a) * t * 1.7f), 0.35f * un, StrokeCap.Round)
                }
                listOf(-1f, 1f).forEach { sd -> drawPath(TrazosNouveau.petalo(c, Offset(c.x + sd * t * 0.75f, c.y + t * 1.1f), t * 0.75f), nm(VIDRIO_VIOLETA, o)) }
                drawPath(TrazosNouveau.petalo(c, Offset(c.x, c.y + t * 1.3f), t * 0.85f), nm(VIDRIO_AMBAR, o))
                drawRect(nm(LATON_OSC, o), Offset(c.x - t * 0.22f, c.y - t * 0.2f), Size(t * 0.44f, t * 0.24f))
                drawLine(nm(LATON_OSC, o), Offset(c.x, c.y - t * 0.2f), Offset(c.x, c.y - t * 0.9f), 0.3f * un)
            }
            "pensamiento" -> {
                // Dos pétalos de arriba violeta hondo, dos de los lados malva y el de abajo con su mancha y su ojo amarillo.
                val hondo = nm(VIDRIO_VIOLETA, o); val malva = nm(MALVA, o)
                listOf(-0.5f, 0.5f).forEach { s -> drawCircle(hondo, t * 0.62f, Offset(q.x + s * t * 0.55f, q.y - t * 0.55f)) }
                listOf(-1f, 1f).forEach { s -> drawCircle(malva, t * 0.52f, Offset(q.x + s * t * 0.72f, q.y + t * 0.05f)) }
                drawCircle(nm(mez(MALVA, Color.White, 0.3f), o), t * 0.62f, Offset(q.x, q.y + t * 0.42f))
                drawCircle(nm(IRIS, o), t * 0.34f, Offset(q.x, q.y + t * 0.3f))
                for (k in -1..1) drawLine(nm(PLUMA, o).copy(alpha = 0.6f), Offset(q.x, q.y + t * 0.1f), Offset(q.x + k * t * 0.35f, q.y + t * 0.75f), 0.25f * un)
                drawCircle(nm(IRIS_LENGUA, o), t * 0.16f, Offset(q.x, q.y + t * 0.06f))
            }
            "proposito" -> {
                with(TrazosNouveau) {
                    tallo(Offset(q.x - t * 0.2f, q.y + t * 1f), t * 2.6f, 0.82f, 1f, t * 0.12f, nm(TALLO, o), nm(TALLO_SOMBRA, o), nm(MALVA, o), true, nm(IRIS_LENGUA, o))
                }
            }
            else -> {
                // El nomeolvides: tres florecillas azules de cinco pétalos con su ojo amarillo.
                listOf(Offset(-0.55f, 0.2f) to 0.55f, Offset(0.45f, -0.1f) to 0.6f, Offset(-0.05f, -0.75f) to 0.5f).forEach { (d, k) ->
                    val c = Offset(q.x + d.x * t, q.y + d.y * t)
                    val rr = t * k
                    for (j in 0 until 5) {
                        val a = -PI.toFloat() / 2 + j * 2f * PI.toFloat() / 5f
                        drawCircle(nm(Color(0xFF6E8FC4), o), rr * 0.42f, Offset(c.x + cos(a) * rr * 0.48f, c.y + sin(a) * rr * 0.48f))
                    }
                    drawCircle(nm(IRIS_LENGUA, o), rr * 0.22f, c)
                }
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFECE5D6) else PLUMA
}
