package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.TrazosPsicodelia
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Psicodelia: Vida en el departamento compartido de un dibujante de carteles de conciertos, en una casa victoriana del
 * Haight, en San Francisco, en 1967 (`docs/diseno/temas/psicodelia.md`; el mismo cuarto que su repisa de discos de la
 * biblioteca). La pared encalada con sus molduras blancas, la duela de abeto, el pino sin pintar, los ladrillos y las cajas
 * de fruta que hacen de muebles, la colcha de la India colgada como tapiz y los carteles propios, con letras de bloque que
 * llenan su forma (al modo de Roller) y colores complementarios que vibran. Ninguna persona dibujada, ninguna marca ni
 * portada real. De día la luz entra por la ventana de guillotina, a la izquierda, y la sombra es violeta (`ui/muebles/Luz.kt`,
 * versión sutil). De noche (modo oscuro) es el mismo cuarto con la luz negra: todo se hunde en el negro violeta y solo
 * brillan las tintas fluorescentes, la lámpara de cera encendida y, por la ventana, las farolas de la calle en la niebla.
 * - Social: el cuarto de estar. En medio, la colcha de algodón de la India colgada de su vara como tapiz, con la orla de
 *   granza estampada a mano con gotas de cachemir y la cenefa de azafrán y añil (el nicho: tu gente va en su campo liso);
 *   encima, el tubo de luz negra en su regleta. A la izquierda, la ventana de guillotina del mirador con la niebla, las
 *   casas de enfrente (casi todas grises, una pintada) y la mala madre en el alféizar, la cortina de teñido anudado
 *   recogida a un lado y, sobre una caja de fruta de pie, la lámpara de cera. A la derecha, un cartel de luz negra clavado
 *   y la guitarra apoyada en la pared. En el piso de duela, el tapete trenzado, los tres cojines grandes (el de espejitos,
 *   el teñido en espiral y el de cuadros de algodón) y el pandero.
 * - Mapas: la mesa de dibujo. El tablero de pino con el pliego sujeto con cinta de papel (el nicho); a la izquierda, el
 *   cartel de la semana a medio entintar (las letras de bloque de «BAILE» que se hinchan hacia el centro: arriba ya en
 *   tinta, abajo todavía a lápiz) y la tarjeta de los pares que vibran; a la derecha, los bocetos de la flor y la prueba de
 *   la tinta de arcoíris del tintero partido. En el canto, los lápices, la plumilla, la goma, la cinta, la paleta de
 *   porcelana con sus tintas, la lata de los pinceles y los frascos de tinta; debajo, las patas de caballete, los carteles
 *   enrollados y la papelera con bocetos arrugados.
 * - Otras vidas: cinco cajas de fruta de pino puestas de lado sobre una tabla y ladrillos: cada caja es un compartimento
 *   (el nicho). Encima, la caja de los discos con su etiqueta de papel (un huerto al sol, sin marca), el tocadiscos de
 *   maleta con un disco en el plato, una pila de sencillos, la radio de válvulas con el cuadrante de la FM y la hiedra
 *   en su maceta de barro, que cuelga por el costado.
 * - Íconos: el puente del Golden Gate con la niebla que entra; una hoja entintada de negro que una púa de guitarra raspa
 *   hasta descubrir la tinta de arcoíris; y el plano del barrio, con el Panhandle y el parque, y tres botones de solapa unidos
 *   por un camino que ondula.
 * - Notas: el corcho de la pared, sobre la mesa de dibujo, en un marco de pino pintado a mano con la onda de arcoíris y
 *   margaritas, con la flor de madera recortada en lo alto y una repisa con la lata de chinchetas, la cinta, un pincel y un
 *   frasco de tinta. Cada nota es un volante de papel de color con su franja de arcoíris impresa, clavado con una chincheta
 *   de colores; su tipo es una viñeta en la esquina (el sol, las burbujas, la margarita, el disco) y, si está fijada, la
 *   prende un botón de solapa y lleva el filete doble que vibra. De noche, el volante es de papel oscuro con las tintas
 *   que brillan.
 */
internal object VidaPsicodelia : DisenoVida {
    // ---------------------------------------------------------------- La paleta del departamento
    private val MAGENTA = TrazosPsicodelia.MAGENTA
    private val VERDE = TrazosPsicodelia.VERDE
    private val NARANJA = TrazosPsicodelia.NARANJA
    private val AZUL = TrazosPsicodelia.AZUL
    private val AMARILLO = TrazosPsicodelia.AMARILLO
    private val VIOLETA = TrazosPsicodelia.VIOLETA
    private val LIMON = TrazosPsicodelia.LIMON
    private val FMAGENTA = TrazosPsicodelia.FLUOR_MAGENTA
    private val FVIOLETA = TrazosPsicodelia.FLUOR_VIOLETA
    private val FNARANJA = TrazosPsicodelia.FLUOR_NARANJA
    private val SALA = TrazosPsicodelia.SALA
    private val NIEBLA = TrazosPsicodelia.NIEBLA
    private val VINILO = TrazosPsicodelia.VINILO
    /** El amarillo y el azul bajo la luz negra (los que no tienen fluorescente propio en la paleta). */
    private val FAMARILLO = Color(0xFFF6F45A)
    private val FAZUL = Color(0xFF6CF0C8)

    /** La pared encalada del departamento (la misma de su repisa de discos), su luz y su yeso en sombra. */
    private val PARED = Color(0xFFEDE3D2)
    private val PARED_LUZ = Color(0xFFF7F0E3)
    private val PARED_SOMBRA = Color(0xFFC9BBA4)
    /** Las molduras victorianas pintadas de blanco (la cornisa, el zócalo, el marco de la ventana) y su perfil en sombra. */
    private val MOLDURA = Color(0xFFF3EEE4)
    private val MOLDURA_OSC = Color(0xFFCFC5B3)
    /** El pino sin pintar de las tablas y las cajas de fruta: su color, en sombra y en luz, y sus nudos. */
    private val PINO = Color(0xFFCFA673)
    private val PINO_OSC = Color(0xFF8C6A42)
    private val PINO_LUZ = Color(0xFFE8CB98)
    private val NUDO = Color(0xFF7A532E)
    /** El pino viejo, ya gris, de alguna caja de fruta. */
    private val PINO_VIEJO = Color(0xFFB59B78)
    /** Los ladrillos y su mortero seco. */
    private val LADRILLO = Color(0xFFA9573C)
    private val LADRILLO_OSC = Color(0xFF6E3324)
    private val LADRILLO_LUZ = Color(0xFFC7765A)
    private val MORTERO = Color(0xFFD8CDBB)
    /** La duela de abeto del piso. */
    private val DUELA = Color(0xFF9A6B42)
    private val DUELA_LUZ = Color(0xFFB98A5C)
    private val DUELA_OSC = Color(0xFF6A4528)
    /** La colcha de la India: el algodón crudo del campo, la granza de la orla, el azafrán y el añil de la cenefa. */
    private val ALGODON = Color(0xFFF1E6D0)
    private val GRANZA = Color(0xFFA8322D)
    private val GRANZA_OSC = Color(0xFF6E1E1C)
    private val AZAFRAN = Color(0xFFE0A030)
    private val ANIL = Color(0xFF2E3F7A)
    /** El papel: el cartón de ilustración, el de periódico, el de los volantes y la tinta de leer. */
    private val PAPEL = TrazosPsicodelia.PAPEL
    private val PAPEL_CLARO = Color(0xFFFFF8EC)
    private val TINTA = TrazosPsicodelia.TINTA
    /** El grafito del lápiz y la cinta de papel. */
    private val LAPIZ = Color(0xFF8E8796)
    private val CINTA = Color(0xFFE9DCAE)
    /** El latón de las sonajas, los herrajes y la lámpara de cera. */
    private val LATON = Color(0xFFB8893E)
    private val LATON_LUZ = Color(0xFFE6C27A)
    private val LATON_OSC = Color(0xFF6E4E1E)
    /** La lámina de la regleta del tubo y el cromo. */
    private val LAMINA = Color(0xFFD9D6D0)
    private val LAMINA_OSC = Color(0xFF8E8A84)
    /** El vidrio del tubo de luz negra, apagado. */
    private val TUBO = Color(0xFF3D2C6B)
    /** El tocadiscos de maleta: el forro turquesa y el crema (los de la repisa de discos). */
    private val FORRO = Color(0xFF2B8C88)
    private val FORRO_OSC = Color(0xFF1F6663)
    private val CREMA = Color(0xFFEDE4CF)
    /** La madera de la radio. */
    private val RADIO = Color(0xFF8E5A32)
    private val RADIO_OSC = Color(0xFF5A341C)
    /** El abeto de la tapa de la guitarra, el palo de rosa del puente y del diapasón. */
    private val ABETO = Color(0xFFE2B978)
    private val ROSA_PALO = Color(0xFF4A2A1C)
    /** El corcho del tablero. */
    private val CORCHO = Color(0xFFB98D5A)
    /** Las hojas de las plantas. */
    private val HOJA = Color(0xFF4E8A45)
    private val HOJA_OSC = Color(0xFF2F5A2E)
    private val HOJA_LUZ = Color(0xFF8CC06A)
    /** El barro de las macetas. */
    private val BARRO = Color(0xFFC0673F)
    private val BARRO_OSC = Color(0xFF8A4228)
    /** La calle de día: el cielo de la mañana, sobre la niebla. */
    private val CIELO = Color(0xFFD7E1E8)
    /** La calle de noche: el cielo, la niebla alumbrada y la luz de las farolas. */
    private val CIELO_NOCHE = Color(0xFF1C1534)
    private val NIEBLA_NOCHE = Color(0xFF4A3E66)
    private val FAROL = Color(0xFFFFC46B)
    /** Lo que alumbra la lámpara de cera encendida: su líquido y su cera. */
    private val CERA_LUZ = Color(0xFFFFA24A)
    private val CERA = Color(0xFFC23A7E)

    /** El aire violeta del cuarto de noche: lo que multiplica cada color antes de hundirlo en el negro violeta. */
    private val AIRE = Color(0xFF8C82A8)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.235f, 0.17f, 0.765f, 0.695f)
        MuebleVida.MAPAS -> Rect(0.225f, 0.085f, 0.775f, 0.695f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.06f, 0.35f, 0.94f, 0.79f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.155f, 0.925f, 0.825f)
    }

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))
    private fun entre(a: Float, b: Float, f: Float): Float = a + (b - a) * f

    /** El color de noche: multiplicado por el aire violeta y hundido en el negro violeta de la luz negra; de día, tal cual. */
    private fun n(c: Color, o: Boolean): Color {
        if (!o) return c
        return mez(Color(c.red * AIRE.red, c.green * AIRE.green, c.blue * AIRE.blue, c.alpha), SALA.copy(alpha = c.alpha), 0.6f)
    }

    /** El color de noche junto a la lámpara de cera: [f] (0 a 1) es cuánto le llega su luz naranja. De día, tal cual. */
    private fun lam(c: Color, o: Boolean, f: Float): Color =
        if (!o || f <= 0f) n(c, o) else mez(n(c, o), mez(c, CERA_LUZ, 0.45f), f.coerceIn(0f, 1f) * 0.62f)

    /** Una tinta de cartel bajo la luz negra: de noche, su fluorescente; de día, tal cual. */
    private fun fl(c: Color, o: Boolean): Color = if (!o) c else when (c) {
        MAGENTA -> FMAGENTA
        VERDE -> LIMON
        NARANJA -> FNARANJA
        AMARILLO -> FAMARILLO
        AZUL -> FAZUL
        VIOLETA -> FVIOLETA
        else -> c
    }

    /** El papel blanco de noche: en penumbra, con el violeta tenue de la luz negra en su blanqueador. */
    private fun blanco(c: Color, o: Boolean): Color = if (o) mez(n(c, o), FVIOLETA, 0.14f) else c

    /** Cuánto le llega una luz (en [foco], con alcance [radio]) a un punto [p]: 1 al lado, 0 lejos. */
    private fun cerca(p: Offset, foco: Offset, radio: Float): Float {
        val dx = p.x - foco.x; val dy = p.y - foco.y
        val k = 1f - sqrt(dx * dx + dy * dy) / radio
        return if (k <= 0f) 0f else (k * k * (3f - 2f * k)).coerceIn(0f, 1f)
    }

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, o: Boolean, material: Color): Color =
        if (o) mez(e.fondo, n(material, o), 0.26f) else mez(e.fondo, material, 0.36f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)

    /** Un número estable de 0 a 1 (la misma irregularidad cada vez). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** El resplandor de una luz o de una tinta que brilla: discos que se suman hacia el centro, sin degradado. */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color, fuerza: Float, pasos: Int = 20) {
        if (r <= 0f || fuerza <= 0f) return
        val a = 0.3f / pasos
        for (k in 0 until pasos) {
            val s = k / pasos.toFloat()
            drawCircle(color.copy(alpha = (a * fuerza * (1f + 2f * s)).coerceIn(0f, 1f)), r * (1f - s) * (1f - 0.15f * s), c)
        }
    }

    /** El resplandor de una superficie que brilla bajo la luz negra (un cartel, una tira de tinta): marcos redondeados que se suman hacia ella. */
    private fun DrawScope.haloRect(r: Rect, alcance: Float, color: Color, fuerza: Float, pasos: Int = 9) {
        for (k in 0 until pasos) {
            val s = k / pasos.toFloat()
            val d = alcance * (1f - s)
            drawRoundRect(color.copy(alpha = (0.05f * fuerza * (0.4f + s)).coerceIn(0f, 1f)), Offset(r.left - d, r.top - d), Size(r.width + 2f * d, r.height + 2f * d), CornerRadius(d + 1f))
        }
    }

    /** Todo lo que se dibuja dentro de [bloque], menos lo que caería sobre [nicho]. */
    private fun DrawScope.fueraDe(nicho: Rect, bloque: DrawScope.() -> Unit) {
        val p = Path().apply { addRect(Rect(-size.width, -size.height, size.width * 2f, size.height * 2f)); addRect(nicho); fillType = PathFillType.EvenOdd }
        clipPath(p) { bloque() }
    }

    /** La sombra blanda que una pieza deja en la pared, corrida abajo a la derecha (la luz entra por la ventana de la izquierda). */
    private fun DrawScope.sombraEnPared(r: Rect, alcance: Float, fuerza: Float, radio: Float = 0f) {
        for (k in 3 downTo 1) {
            val d = alcance * k / 3f
            drawRoundRect(sombraMueble(fuerza / 3f), Offset(r.left + d * 0.55f, r.top + d * 0.5f), Size(r.width + d * 0.25f, r.height + d * 0.2f), CornerRadius(radio + d))
        }
    }

    // ---------------------------------------------------------------- Los materiales del departamento

    /** La pared encalada: el crema de la cal con sus pinceladas, las huellas claras de carteles viejos y las grietas finas del yeso. */
    private fun DrawScope.pared(r: Rect, o: Boolean, u: Float, semilla: Int) {
        rect(r, n(PARED, o))
        pinceladas(r, n(PARED_LUZ, o), semilla, (r.width * r.height / (55f * u * u)).toInt().coerceIn(6, 90), u, if (o) 0.05f else 0.16f)
        pinceladas(r, n(PARED_SOMBRA, o), semilla + 9, (r.width * r.height / (110f * u * u)).toInt().coerceIn(4, 60), u, if (o) 0.06f else 0.09f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las grietas del yeso viejo: dos líneas quebradas que bajan.
            for (k in 0 until 2) {
                val path = Path()
                var x = r.left + r.width * (0.2f + 0.6f * azar(semilla, k + 70)); var y = r.top + r.height * azar(semilla, k + 71) * 0.4f
                path.moveTo(x, y)
                for (s in 1..5) { x += (azar(semilla, k * 9 + s) - 0.5f) * 3f * u; y += (1.5f + 2f * azar(semilla, k * 11 + s)) * u; path.lineTo(x, y) }
                drawPath(path, n(PARED_SOMBRA, o).copy(alpha = 0.55f), style = Stroke(0.18f * u, join = StrokeJoin.Round))
            }
        }
    }

    /** La huella clara de un cartel viejo en la pared, con las cuatro marcas de sus chinchetas. */
    private fun DrawScope.huellaCartel(q: Rect, o: Boolean, u: Float) {
        rect(q, n(PARED_LUZ, o).copy(alpha = if (o) 0.06f else 0.3f))
        listOf(q.topLeft, Offset(q.right, q.top), Offset(q.left, q.bottom), q.bottomRight).forEach {
            val d = Offset(if (it.x < q.center.x) 1f else -1f, if (it.y < q.center.y) 1f else -1f) * (0.9f * u)
            drawCircle(n(PARED_SOMBRA, o).copy(alpha = 0.8f), 0.24f * u, it + d)
        }
    }

    /** La cornisa del techo: la moldura victoriana pintada de blanco, con su gola en luz y su filete en sombra. */
    private fun DrawScope.cornisa(r: Rect, o: Boolean, u: Float) {
        val hh = r.height
        rect(r, n(MOLDURA, o))
        rect(Rect(r.left, r.top, r.right, r.top + hh * 0.26f), n(MOLDURA_OSC, o))
        drawRect(luzMueble(if (o) 0.04f else 0.6f), Offset(r.left, r.top + hh * 0.32f), Size(r.width, hh * 0.18f))
        drawRect(sombraMueble(if (o) 0.25f else 0.14f), Offset(r.left, r.top + hh * 0.6f), Size(r.width, hh * 0.18f))
        drawLine(n(MOLDURA_OSC, o), Offset(r.left, r.bottom - 0.25f * u), Offset(r.right, r.bottom - 0.25f * u), 0.35f * u)
        drawRect(sombraMueble(if (o) 0.3f else 0.13f), Offset(r.left, r.bottom), Size(r.width, 0.9f * u))
    }

    /** El zócalo alto de la casa victoriana, pintado de blanco: la tabla, la moldura de remate en luz y el bocel junto al piso. */
    private fun DrawScope.zocalo(r: Rect, o: Boolean, u: Float) {
        rect(r, n(MOLDURA, o))
        pinceladas(r, n(MOLDURA_OSC, o), 5, (r.width / (12f * u)).toInt().coerceIn(3, 30), u * 0.5f, if (o) 0.06f else 0.12f)
        val m = Rect(r.left, r.top, r.right, r.top + 1.5f * u)
        rect(m, n(MOLDURA_OSC, o))
        drawRect(luzMueble(if (o) 0.05f else 0.65f), m.topLeft, Size(m.width, 0.55f * u))
        drawRect(sombraMueble(if (o) 0.3f else 0.12f), Offset(r.left, m.bottom), Size(r.width, 0.6f * u))
        val b = Rect(r.left, r.bottom - 1.2f * u, r.right, r.bottom)
        rect(b, n(MOLDURA_OSC, o))
        drawRect(luzMueble(if (o) 0.04f else 0.45f), b.topLeft, Size(b.width, 0.35f * u))
    }

    /** La duela de abeto: las tablas que se alejan, cada vez más anchas hacia delante, con sus juntas y alguna gota de tinta. */
    private fun DrawScope.duela(r: Rect, o: Boolean, u: Float, semilla: Int, tinta: Boolean = false) {
        rect(r, n(DUELA, o))
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top; var k = 0; var alto = 1.2f * u
            while (y < r.bottom && k < 40) {
                val tabla = Rect(r.left, y, r.right, y + alto)
                rect(tabla, n(mez(DUELA, if (k % 2 == 0) DUELA_LUZ else DUELA_OSC, 0.1f + 0.16f * azar(semilla, k)), o))
                pinceladas(tabla, n(DUELA_LUZ, o), semilla + k, 5, u * 0.5f, if (o) 0.04f else 0.12f)
                drawLine(n(DUELA_OSC, o), Offset(r.left, y), Offset(r.right, y), 0.26f * u)
                var xj = r.left + r.width * azar(semilla, k + 30) * 0.5f; var j = 0
                while (xj < r.right && j < 20) {
                    drawLine(n(DUELA_OSC, o).copy(alpha = 0.8f), Offset(xj, y), Offset(xj, y + alto), 0.2f * u)
                    xj += r.width * (0.26f + 0.25f * azar(semilla, k * 7 + j + 31)); j++
                }
                y += alto; alto *= 1.24f; k++
            }
            if (tinta) for (q in 0 until 4) {
                val c = Offset(r.left + r.width * azar(semilla, q + 90), r.top + r.height * (0.3f + 0.6f * azar(semilla, q + 91)))
                val rr = (0.5f + 1.1f * azar(semilla, q + 92)) * u
                val col = fl(listOf(MAGENTA, VERDE, NARANJA, AZUL)[q], o)
                drawOval(col.copy(alpha = if (o) 0.7f else 0.55f), Offset(c.x - rr * 1.5f, c.y - rr * 0.45f), Size(rr * 3f, rr * 0.9f))
                drawCircle(col.copy(alpha = if (o) 0.6f else 0.5f), rr * 0.3f, Offset(c.x + rr * 2.1f, c.y + rr * 0.1f))
            }
        }
        drawRect(sombraMueble(if (o) 0.4f else 0.22f), r.topLeft, Size(r.width, 0.9f * u))
    }

    /**
     * El pino sin pintar: la veta larga que ondula, los nudos con sus anillos y las pinceladas de luz. [vertical] para los
     * montantes; [f] es cuánto le llega de noche la lámpara de cera.
     */
    private fun DrawScope.pino(r: Rect, o: Boolean, u: Float, semilla: Int, base: Color = PINO, vertical: Boolean = false, f: Float = 0f, nudos: Boolean = true) {
        rect(r, lam(base, o, f))
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        clipRect(r.left, r.top, r.right, r.bottom) {
            val lineas = (ancho / (1.1f * u)).toInt().coerceIn(2, 40)
            for (k in 0 until lineas) {
                val b = ancho * (k + azar(semilla, k)) / lineas
                val amp = (0.15f + 0.6f * azar(semilla, k + 50)) * u
                val fase = azar(semilla, k + 90) * 6.28f
                val ondas = 0.4f + 1.2f * azar(semilla, k + 130)
                val path = Path()
                for (s in 0..14) {
                    val q = largo * s / 14f
                    val v = b + amp * sin(fase + ondas * 6.28f * s / 14f)
                    if (vertical) { if (s == 0) path.moveTo(r.left + v, r.top + q) else path.lineTo(r.left + v, r.top + q) }
                    else { if (s == 0) path.moveTo(r.left + q, r.top + v) else path.lineTo(r.left + q, r.top + v) }
                }
                val oscura = azar(semilla, k + 170) < 0.28f
                drawPath(path, lam(if (oscura) NUDO else PINO_OSC, o, f).copy(alpha = if (oscura) 0.32f else 0.22f), style = Stroke((0.12f + 0.18f * azar(semilla, k + 210)) * u))
            }
            if (nudos) {
                val cuantos = (largo / (34f * u)).toInt().coerceIn(1, 4)
                for (j in 0 until cuantos) if (azar(semilla, j + 400) < 0.6f) {
                    val q = largo * (j + 0.3f + 0.4f * azar(semilla, j + 410)) / cuantos
                    val s = ancho * (0.25f + 0.5f * azar(semilla, j + 420))
                    val c = if (vertical) Offset(r.left + s, r.top + q) else Offset(r.left + q, r.top + s)
                    val rr = minOf(ancho * 0.16f, 1.2f * u) * (0.6f + 0.5f * azar(semilla, j + 430))
                    val ow = if (vertical) rr else rr * 1.7f; val oh = if (vertical) rr * 1.7f else rr
                    drawOval(lam(NUDO, o, f).copy(alpha = 0.35f), Offset(c.x - ow * 1.9f, c.y - oh * 1.9f), Size(ow * 3.8f, oh * 3.8f), style = Stroke(0.18f * u))
                    drawOval(lam(NUDO, o, f), Offset(c.x - ow, c.y - oh), Size(ow * 2f, oh * 2f))
                    drawCircle(lam(PINO_OSC, o, f), rr * 0.35f, c)
                }
            }
            pinceladas(r, lam(PINO_LUZ, o, f), semilla + 5, (largo * ancho / (80f * u * u)).toInt().coerceIn(2, 24), u * 0.45f, if (o) 0.05f else 0.12f)
        }
    }

    /** Una tabla de pino con cuerpo: la cara con su veta, el canto de arriba en luz y el de abajo en sombra. */
    private fun DrawScope.tabla(r: Rect, o: Boolean, u: Float, semilla: Int, base: Color = PINO, vertical: Boolean = false, f: Float = 0f) {
        pino(r, o, u, semilla, base, vertical, f)
        val g = minOf(0.45f * u, (if (vertical) r.width else r.height) * 0.22f)
        if (vertical) {
            drawRect(luzMueble(if (o) 0.05f + 0.2f * f else 0.42f), r.topLeft, Size(g, r.height))
            drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(r.right - g, r.top), Size(g, r.height))
        } else {
            drawRect(luzMueble(if (o) 0.05f + 0.2f * f else 0.45f), r.topLeft, Size(r.width, g))
            drawRect(sombraMueble(if (o) 0.4f else 0.3f), Offset(r.left, r.bottom - g), Size(r.width, g))
        }
    }

    /** Una pila de ladrillos de cara, sin mezcla: cada uno de su tono, con el barro picado, el canto en luz y la junta seca. */
    private fun DrawScope.ladrillos(r: Rect, o: Boolean, u: Float, semilla: Int, alto: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.bottom; var k = 0
            while (y > r.top && k < 30) {
                val b = Rect(r.left + (azar(semilla, k + 60) - 0.5f) * 0.6f * u, y - alto, r.right + (azar(semilla, k + 61) - 0.5f) * 0.6f * u, y)
                val tono = mez(LADRILLO, if (azar(semilla, k) < 0.5f) LADRILLO_OSC else LADRILLO_LUZ, 0.12f + 0.3f * azar(semilla, k + 10))
                rect(b, n(tono, o))
                for (j in 0 until 7) drawCircle(n(LADRILLO_OSC, o).copy(alpha = 0.4f), (0.1f + 0.14f * azar(semilla, k * 13 + j)) * u,
                    Offset(b.left + b.width * azar(semilla, k * 17 + j + 3), b.top + b.height * azar(semilla, k * 19 + j + 5)))
                drawRect(luzMueble(if (o) 0.04f else 0.3f), b.topLeft, Size(b.width, 0.32f * u))
                drawRect(sombraMueble(if (o) 0.35f else 0.25f), Offset(b.right - 0.7f * u, b.top), Size(0.7f * u, b.height))
                drawLine(n(MORTERO, o).copy(alpha = 0.6f), Offset(b.left, b.bottom), Offset(b.right, b.bottom), 0.28f * u)
                y -= alto; k++
            }
        }
    }

    /** Una chincheta de cabeza de plástico de [color], con su brillo y su sombra corrida. */
    private fun DrawScope.chincheta(c: Offset, r: Float, color: Color, o: Boolean) {
        drawCircle(sombraMueble(if (o) 0.5f else 0.35f), r, Offset(c.x + r * 0.45f, c.y + r * 0.6f))
        drawCircle(mez(color, Color.Black, 0.25f), r, c)
        drawCircle(color, r * 0.82f, Offset(c.x - r * 0.08f, c.y - r * 0.1f))
        drawCircle(Color.White.copy(alpha = if (o) 0.35f else 0.6f), r * 0.26f, Offset(c.x - r * 0.32f, c.y - r * 0.36f))
    }

    /** Un trozo de cinta de papel pegado en diagonal, con su borde rasgado y su transparencia. */
    private fun DrawScope.cinta(c: Offset, largo: Float, ancho: Float, grados: Float, o: Boolean, u: Float) {
        rotate(grados, c) {
            val r = Rect(c.x - largo / 2, c.y - ancho / 2, c.x + largo / 2, c.y + ancho / 2)
            drawRect(sombraMueble(if (o) 0.25f else 0.12f), Offset(r.left + 0.3f * u, r.top + 0.4f * u), r.size)
            val p = Path().apply {
                moveTo(r.left, r.top)
                lineTo(r.right, r.top)
                for (s in 0..4) lineTo(r.right + (if (s % 2 == 0) 0f else 0.35f * u), r.top + ancho * s / 4f)
                lineTo(r.left, r.bottom)
                for (s in 4 downTo 0) lineTo(r.left - (if (s % 2 == 0) 0f else 0.35f * u), r.top + ancho * s / 4f)
                close()
            }
            drawPath(p, n(CINTA, o).copy(alpha = 0.88f))
            drawLine(luzMueble(if (o) 0.08f else 0.4f), Offset(r.left, r.top + ancho * 0.25f), Offset(r.right, r.top + ancho * 0.25f), ancho * 0.15f)
        }
    }

    // ---------------------------------------------------------------- Las letras de bloque

    /** Una letra de bloque: su ancho en la palabra y los bloques (llenos) y huecos que la forman, en fracciones de su celda. */
    private class Letra(val ancho: Float, val llenos: List<FloatArray>, val huecos: List<FloatArray>)

    /** Un bloque redondeado: x0, y0, x1, y1 y el radio de cada esquina (arriba a la izquierda, arriba a la derecha, abajo a la derecha, abajo a la izquierda). */
    private fun b(vararg v: Float) = v

    /**
     * «BAILE» en letras de bloque, al modo de las de Alfred Roller que tomaron los carteles de las salas de baile: gruesas,
     * con los huecos chicos, la A en arco, para que la palabra llene su forma.
     */
    private val BAILE = listOf(
        Letra(0.9f, listOf(b(0f, 0f, 0.9f, 1f, 0.05f, 0.3f, 0.32f, 0.05f)),
            listOf(b(0.31f, 0.21f, 0.6f, 0.4f, 0.03f, 0.09f, 0.09f, 0.03f), b(0.31f, 0.59f, 0.63f, 0.79f, 0.03f, 0.1f, 0.1f, 0.03f), b(0.8f, 0.45f, 1f, 0.55f, 0.05f, 0f, 0f, 0.05f))),
        Letra(0.95f, listOf(b(0f, 0f, 0.95f, 1f, 0.47f, 0.47f, 0.02f, 0.02f)),
            listOf(b(0.32f, 0.3f, 0.63f, 0.52f, 0.155f, 0.155f, 0.02f, 0.02f), b(0.32f, 0.7f, 0.63f, 1.2f, 0f, 0f, 0f, 0f))),
        Letra(0.34f, listOf(b(0f, 0f, 0.34f, 1f, 0.05f, 0.05f, 0.05f, 0.05f)), emptyList()),
        Letra(0.76f, listOf(b(0f, 0f, 0.76f, 1f, 0.05f, 0.05f, 0.06f, 0.06f)), listOf(b(0.34f, -0.2f, 0.9f, 0.7f, 0f, 0f, 0f, 0.07f))),
        Letra(0.78f, listOf(b(0f, 0f, 0.78f, 1f, 0.06f, 0.05f, 0.05f, 0.06f)),
            listOf(b(0.34f, 0.24f, 0.9f, 0.41f, 0.05f, 0f, 0f, 0.05f), b(0.34f, 0.59f, 0.9f, 0.76f, 0.05f, 0f, 0f, 0.05f))),
    )

    /** El contorno de un bloque redondeado como puntos (cada lado partido en tramos, para que se curve al deformarse). */
    private fun contornoBloque(v: FloatArray): List<Offset> {
        val x0 = v[0]; val y0 = v[1]; val x1 = v[2]; val y1 = v[3]
        val pts = ArrayList<Offset>(64)
        fun lado(a: Offset, c: Offset) { for (k in 0 until 6) pts.add(Offset(a.x + (c.x - a.x) * k / 6f, a.y + (c.y - a.y) * k / 6f)) }
        fun arco(c: Offset, r: Float, a0: Float) { for (k in 0 until 6) { val a = a0 + (PI.toFloat() / 2f) * k / 6f; pts.add(Offset(c.x + r * cos(a), c.y + r * sin(a))) } }
        val si = v[4]; val sd = v[5]; val id = v[6]; val ii = v[7]
        arco(Offset(x0 + si, y0 + si), si, PI.toFloat())
        lado(Offset(x0 + si, y0), Offset(x1 - sd, y0))
        arco(Offset(x1 - sd, y0 + sd), sd, PI.toFloat() * 1.5f)
        lado(Offset(x1, y0 + sd), Offset(x1, y1 - id))
        arco(Offset(x1 - id, y1 - id), id, 0f)
        lado(Offset(x1 - id, y1), Offset(x0 + ii, y1))
        arco(Offset(x0 + ii, y1 - ii), ii, PI.toFloat() / 2f)
        lado(Offset(x0, y1 - ii), Offset(x0, y0 + si))
        return pts
    }

    /**
     * «BAILE» en letras de bloque que llenan [r]: la palabra se hincha hacia el centro (las letras de en medio, más altas y
     * más anchas, como en los carteles de 1966 y 1967) y ondula apenas con [onda]. Devuelve el trazo de cada letra.
     */
    private fun palabraBaile(r: Rect, onda: Float = 0.05f): List<Path> {
        val hueco = 0.07f
        val total = BAILE.fold(0f) { s, l -> s + l.ancho } + hueco * (BAILE.size - 1)
        val dos = 2f * PI.toFloat()
        fun deforma(x: Float, y: Float): Offset {
            val xn = x - 0.045f * sin(dos * x)
            val alto = 0.6f + 0.4f * sin(PI.toFloat() * x)
            val cy = r.center.y + r.height * onda * sin(dos * x + 0.6f)
            return Offset(r.left + r.width * xn, cy + (y - 0.5f) * r.height * alto)
        }
        var desde = 0f
        return BAILE.map { l ->
            val x0 = desde
            fun camino(v: FloatArray): Path = Path().apply {
                contornoBloque(v).forEachIndexed { i, q -> val p = deforma((x0 + q.x) / total, q.y); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
                close()
            }
            var letra = l.llenos.map { camino(it) }.reduce { a, c -> Path.combine(PathOperation.Union, a, c) }
            l.huecos.forEach { letra = Path.combine(PathOperation.Difference, letra, camino(it)) }
            desde += l.ancho + hueco
            letra
        }
    }

    // ---------------------------------------------------------------- Los carteles

    /**
     * El cartel de la semana a medio entintar, en [r] (cartón de ilustración): el fondo verde, el sol de rayos naranja y
     * azul con la flor magenta que vibra, «BAILE» en magenta hinchándose en medio y la banda de ondas amarillas y violetas,
     * con su pie de letra chica. Todo está trazado a lápiz; la tinta ya cubre la parte de arriba y se detiene en una
     * diagonal húmeda, que [avance] (de 0 a 1) baja. De noche, lo entintado brilla con la luz negra.
     */
    private fun DrawScope.cartelSemana(r: Rect, o: Boolean, u: Float, avance: Float) {
        val m = r.width * 0.07f
        val a = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m * 1.6f)
        val aw = a.width; val ah = a.height
        // Las formas del dibujo (las mismas para el lápiz y para la tinta).
        val sol = Offset(a.center.x, a.top + ah * 0.25f)
        val rSol = ah * 0.13f
        val zonaSol = Rect(a.left, a.top, a.right, a.top + ah * 0.47f)
        val letras = palabraBaile(Rect(a.left + aw * 0.03f, a.top + ah * 0.47f, a.right - aw * 0.03f, a.top + ah * 0.76f), 0.05f)
        val banda = Rect(a.left, a.top + ah * 0.79f, a.right, a.top + ah * 0.93f)
        fun rayos(): List<Path> = (0 until 18).map { k ->
            val a0 = 2f * PI.toFloat() * k / 18f; val a1 = a0 + 2f * PI.toFloat() / 18f
            Path().apply {
                moveTo(sol.x, sol.y)
                for (s in 0..8) { val q = a0 + (a1 - a0) * s / 8f; val rr = ah * (0.6f + 0.03f * sin(q * 9f)); lineTo(sol.x + rr * cos(q), sol.y + rr * sin(q)) }
                close()
            }
        }
        // El cartón, con su sombra.
        sombraEnPared(r, 1.2f * u, if (o) 0.45f else 0.26f)
        rect(r, blanco(PAPEL_CLARO, o))
        // El dibujo a lápiz, entero.
        val lapiz = (if (o) mez(LAPIZ, FVIOLETA, 0.3f) else LAPIZ).copy(alpha = if (o) 0.5f else 0.75f)
        val fino = Stroke(0.16f * u)
        drawRect(lapiz, a.topLeft, a.size, style = fino)
        clipRect(zonaSol.left, zonaSol.top, zonaSol.right, zonaSol.bottom) { rayos().forEachIndexed { k, p -> if (k % 2 == 0) drawPath(p, lapiz, style = fino) } }
        drawLine(lapiz, Offset(a.left, zonaSol.bottom), Offset(a.right, zonaSol.bottom), 0.16f * u)
        drawCircle(lapiz, rSol, sol, style = fino)
        drawPath(TrazosPsicodelia.petalos(sol, rSol * 0.82f, 8, 0f, 0.27f, 0.1f), lapiz, style = fino)
        letras.forEach { drawPath(it, lapiz, style = Stroke(0.2f * u)) }
        for (k in 0..2) drawPath(TrazosPsicodelia.onda(banda.left, banda.right, banda.top + banda.height * (0.2f + 0.3f * k), banda.height * 0.12f, aw * 0.25f, k * 0.2f), lapiz, style = fino)
        for (k in 0..1) drawLine(lapiz, Offset(a.left + aw * 0.2f, a.bottom + m * (0.5f + 0.5f * k)), Offset(a.right - aw * 0.2f, a.bottom + m * (0.5f + 0.5f * k)), 0.16f * u)
        // La tinta: lo de arriba de la diagonal húmeda.
        val ya = a.top + ah * (0.42f + 0.35f * avance); val yb = a.top + ah * (0.78f + 0.2f * avance)
        val borde = Path().apply {
            moveTo(a.left - 1f, a.top - 1f); lineTo(a.right + 1f, a.top - 1f); lineTo(a.right + 1f, ya)
            for (s in 1..10) { val f = s / 10f; lineTo(a.right - aw * f, ya + (yb - ya) * f + sin(f * 19f) * 0.5f * u) }
            lineTo(a.left - 1f, a.bottom + 1f); lineTo(a.left - 1f, a.top - 1f); close()
        }
        clipPath(borde) {
            clipRect(a.left, a.top, a.right, a.bottom) {
                rect(a, fl(VERDE, o).let { if (o) mez(it, SALA, 0.2f) else it })
                clipRect(zonaSol.left, zonaSol.top, zonaSol.right, zonaSol.bottom) {
                    rayos().forEachIndexed { k, p -> drawPath(p, fl(if (k % 2 == 0) NARANJA else AZUL, o)) }
                }
                drawLine(fl(AMARILLO, o), Offset(a.left, zonaSol.bottom), Offset(a.right, zonaSol.bottom), 0.6f * u)
                drawCircle(fl(AZUL, o), rSol * 1.12f, sol)
                with(TrazosPsicodelia) { flor(sol, rSol, fl(MAGENTA, o), fl(AMARILLO, o), 8, 0.5f) }
                letras.forEach { drawPath(it, fl(MAGENTA, o)) }
                for (k in 0..4) drawPath(TrazosPsicodelia.onda(banda.left, banda.right, banda.top + banda.height * k / 4f, banda.height * 0.12f, aw * 0.25f, k * 0.2f),
                    fl(if (k % 2 == 0) AMARILLO else VIOLETA, o), style = Stroke(banda.height * 0.2f))
            }
            for (k in 0..1) drawLine(fl(VIOLETA, o), Offset(a.left + aw * 0.2f, a.bottom + m * (0.5f + 0.5f * k)), Offset(a.right - aw * (0.2f + 0.15f * k), a.bottom + m * (0.5f + 0.5f * k)), 0.5f * u, StrokeCap.Round)
            // El filo de la tinta húmeda.
            drawPath(Path().apply { moveTo(a.right, ya); for (s in 1..10) { val f = s / 10f; lineTo(a.right - aw * f, ya + (yb - ya) * f + sin(f * 19f) * 0.5f * u) } },
                fl(VERDE, o).let { mez(it, Color.Black, 0.3f) }.copy(alpha = 0.6f), style = Stroke(0.35f * u))
        }
        // El brillo de la tinta fresca y la luz de la ventana sobre el cartón.
        drawLine(luzMueble(if (o) 0.05f else 0.35f), Offset(r.left, r.top), Offset(r.right, r.top), 0.4f * u)
        drawRect(sombraMueble(if (o) 0.25f else 0.12f), Offset(r.right - 0.5f * u, r.top), Size(0.5f * u, r.height))
    }

    /**
     * El cartel de luz negra, en [r]: tintas fluorescentes sobre el negro de terciopelo, los anillos que ondulan en verde
     * limón, magenta, naranja y violeta alrededor de la flor que vibra con el símbolo de la paz, y abajo la onda y tres
     * margaritas. De día se ve vivo; de noche, bajo el tubo, es lo que más brilla del cuarto.
     */
    private fun DrawScope.cartelLuzNegra(r: Rect, o: Boolean, u: Float) {
        if (o) haloRect(r, 7f * u, FMAGENTA, 0.9f)
        sombraEnPared(r, 1.3f * u, if (o) 0.4f else 0.28f)
        val negro = if (o) Color(0xFF0C0712) else Color(0xFF1C1524)
        rect(r, negro)
        val c = Offset(r.center.x, r.top + r.height * 0.42f)
        val colores = listOf(LIMON, FMAGENTA, FNARANJA, FVIOLETA)
        val viva = { col: Color -> if (o) col else mez(col, Color(0xFF1C1524), 0.08f) }
        clipRect(r.left + 0.8f * u, r.top + 0.8f * u, r.right - 0.8f * u, r.bottom - 0.8f * u) {
            for (k in 16 downTo 1) {
                val rr = r.width * 0.064f * k
                drawPath(TrazosPsicodelia.gota(c, rr, k * 0.07f, 0.045f, 6), if (k % 2 == 0) viva(colores[(k / 2) % 4]) else negro)
            }
            if (o) halo(c, r.width * 0.5f, FMAGENTA, 0.6f)
            with(TrazosPsicodelia) {
                flor(c, r.width * 0.26f, viva(FMAGENTA), viva(LIMON), 8, 0.8f, 0.2f)
                drawCircle(viva(FAMARILLO), r.width * 0.085f, c)
                paz(c, r.width * 0.06f, negro, r.width * 0.012f)
            }
            // Abajo, la onda y tres margaritas.
            val yb = r.top + r.height * 0.86f
            drawRect(negro, Offset(r.left, r.top + r.height * 0.78f), Size(r.width, r.height * 0.22f))
            drawPath(TrazosPsicodelia.onda(r.left, r.right, yb - r.height * 0.05f, r.height * 0.015f, r.width * 0.3f), viva(LIMON), style = Stroke(0.55f * u))
            for (k in 0..2) with(TrazosPsicodelia) { margarita(Offset(r.left + r.width * (0.28f + 0.22f * k), yb + r.height * 0.03f), r.width * 0.07f, viva(colores[(k + 1) % 4]), negro, 6) }
        }
        // El papel curvado: la luz de la ventana arriba a la izquierda, la esquina de abajo levantada.
        drawRect(luzMueble(if (o) 0.03f else 0.14f), Offset(r.left, r.top), Size(r.width * 0.35f, r.height))
        drawPath(Path().apply { moveTo(r.right, r.bottom - 3f * u); lineTo(r.right, r.bottom); lineTo(r.right - 3f * u, r.bottom); close() }, n(PARED, o))
        drawPath(Path().apply { moveTo(r.right, r.bottom - 3f * u); lineTo(r.right - 2.6f * u, r.bottom - 2.6f * u); lineTo(r.right - 3f * u, r.bottom); close() }, mez(negro, Color.White, 0.18f))
        chincheta(Offset(r.left + 1.4f * u, r.top + 1.4f * u), 0.9f * u, n(VERDE, o), o)
        chincheta(Offset(r.right - 1.4f * u, r.top + 1.4f * u), 0.9f * u, n(NARANJA, o), o)
        chincheta(Offset(r.left + 1.4f * u, r.bottom - 1.4f * u), 0.9f * u, n(MAGENTA, o), o)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.SOCIAL))
        val suelo = h * 0.87f
        val caja = Rect(w * 0.032f, h * 0.735f, w * 0.152f, h * 0.975f)
        val lampAlto = h * 0.2f
        val lampC = Offset(caja.center.x, caja.top - lampAlto * 0.62f)
        fun fz(q: Offset): Float = if (o) cerca(q, lampC, w * 0.24f) else 0f

        // La pared encalada, con la huella de un cartel viejo, la cornisa y el zócalo.
        pared(Rect(0f, 0f, w, suelo), o, u, e.semilla)
        huellaCartel(Rect(w * 0.84f, h * 0.43f, w * 0.95f, h * 0.6f), o, u)
        // De noche, el violeta del tubo de luz negra que baña la pared, y el naranja de la lámpara de cera.
        if (o) {
            halo(Offset(w * 0.5f, h * 0.1f), w * 0.55f, FVIOLETA, 0.75f, 24)
            halo(lampC, w * 0.2f, CERA_LUZ, 0.55f)
        }
        cornisa(Rect(0f, 0f, w, h * 0.032f), o, u)
        zocalo(Rect(0f, suelo - h * 0.075f, w, suelo), o, u)

        // La ventana de guillotina del mirador, con la cortina teñida recogida a un lado y la mala madre en el alféizar.
        val ven = Rect(w * 0.024f, h * 0.105f, w * 0.17f, h * 0.7f)
        ventana(ven, o, u, e.semilla, f = fz(Offset(ven.center.x, ven.bottom)))
        malaMadre(Offset(ven.left + ven.width * 0.68f, ven.bottom + 0.2f * u), ven.width * 0.42f, o, u, fz(Offset(ven.right, ven.bottom)))
        cortina(ven, o, u, ::fz)

        // El cartel de luz negra clavado a la derecha.
        cartelLuzNegra(Rect(w * 0.838f, h * 0.075f, w * 0.982f, h * 0.405f), o, u)

        // La colcha de la India colgada como tapiz: el campo liso es el nicho.
        tapiz(Rect(w * 0.19f, h * 0.095f, w * 0.81f, h * 0.792f), ni, e, o, u)
        // El tubo de luz negra, encima.
        tuboLuzNegra(Rect(w * 0.37f, h * 0.04f, w * 0.63f, h * 0.068f), o, u)

        // El piso de duela y el tapete trenzado.
        duela(Rect(0f, suelo, w, h), o, u, e.semilla)
        tapete(Offset(w * 0.5f, h * 0.955f), w * 0.36f, h * 0.06f, o, u)
        // De día, el sol de la ventana que cae en el piso, hacia la derecha.
        if (!o) drawPath(Path().apply { moveTo(w * 0.15f, suelo); lineTo(w * 0.36f, suelo); lineTo(w * 0.6f, h); lineTo(w * 0.24f, h); close() }, luzMueble(0.16f))

        // La caja de fruta de pie con la lámpara de cera encima.
        cajaDePie(caja, o, u, e.semilla + 3, fz(caja.center))
        lamparaCera(Offset(lampC.x, caja.top), lampAlto, o, u)

        // La guitarra apoyada en la pared, a la derecha, y el pandero en el piso.
        guitarra(Offset(w * 0.925f, h * 0.975f), h * 0.47f, -11f, o, u)

        // Los tres cojines grandes del piso: el de cuadros, el de espejitos y, delante, el teñido en espiral.
        cojin(Rect(w * 0.58f, h * 0.775f, w * 0.83f, h * 0.975f), o, u, n(NARANJA, o).let { if (o) it else mez(NARANJA, PARED, 0.1f) }, 2, e.semilla + 1, 0f)
        cojin(Rect(w * 0.17f, h * 0.77f, w * 0.42f, h * 0.975f), o, u, MAGENTA, 0, e.semilla + 2, fz(Offset(w * 0.2f, h * 0.85f)))
        cojin(Rect(w * 0.38f, h * 0.8f, w * 0.63f, h * 0.995f), o, u, AMARILLO, 1, e.semilla + 4, 0f)
        pandero(Offset(w * 0.8f, h * 0.955f), h * 0.07f, o, u)

        granoMueble(Rect(0f, 0f, w, h), o, 0.7f)
    }

    /**
     * La ventana de guillotina del mirador: el marco de molduras blancas con sus tacos de esquina, las dos hojas (la de abajo
     * delante) con el travesaño de encuentro y su pestillo de latón, el vidrio con el reflejo, el alféizar y el faldón; por
     * el vidrio, la calle. [f] es cuánto le llega de noche la lámpara de cera.
     */
    private fun DrawScope.ventana(r: Rect, o: Boolean, u: Float, semilla: Int, f: Float) {
        val mol = 1.8f * u
        sombraEnPared(r, 1.8f * u, if (o) 0.45f else 0.24f)
        rect(r, lam(MOLDURA, o, f * 0.5f))
        // El perfil de la jamba: el filo en luz y el canto en sombra.
        drawRect(luzMueble(if (o) 0.04f else 0.55f), r.topLeft, Size(0.45f * u, r.height))
        drawRect(n(MOLDURA_OSC, o), Offset(r.left + mol - 0.45f * u, r.top + mol - 0.45f * u), Size(r.width - 2f * mol + 0.9f * u, r.height - mol))
        drawRect(sombraMueble(if (o) 0.3f else 0.18f), Offset(r.right - 0.5f * u, r.top), Size(0.5f * u, r.height))
        // Los tacos de esquina con su roseta torneada.
        listOf(r.left, r.right - mol * 1.25f).forEach { x ->
            val b = Rect(x, r.top, x + mol * 1.25f, r.top + mol * 1.25f)
            rect(b, n(MOLDURA, o))
            drawRect(n(MOLDURA_OSC, o), b.topLeft, b.size, style = Stroke(0.2f * u))
            drawCircle(n(MOLDURA_OSC, o), b.width * 0.3f, b.center, style = Stroke(0.22f * u))
            drawCircle(luzMueble(if (o) 0.05f else 0.6f), b.width * 0.12f, Offset(b.center.x - b.width * 0.08f, b.center.y - b.width * 0.08f))
        }
        val g = Rect(r.left + mol, r.top + mol, r.right - mol, r.bottom - 0.4f * u)
        clipRect(g.left, g.top, g.right, g.bottom) { calle(g, o, u, semilla) }
        // Las hojas: el marco de la de arriba, el travesaño de encuentro y el marco de la de abajo, delante.
        val hoja = 1.15f * u
        val madera = lam(MOLDURA, o, f * 0.5f)
        val medio = g.top + g.height * 0.5f
        rect(Rect(g.left, g.top, g.right, g.top + hoja), madera)
        rect(Rect(g.left, g.top, g.left + hoja * 0.9f, medio), madera)
        rect(Rect(g.right - hoja * 0.9f, g.top, g.right, medio), madera)
        drawRect(sombraMueble(if (o) 0.3f else 0.18f), Offset(g.left + hoja, g.top + hoja), Size(g.width - 2f * hoja, 0.5f * u))
        val inf = Rect(g.left - 0.2f * u, medio - hoja * 0.4f, g.right + 0.2f * u, g.bottom)
        rect(Rect(inf.left, inf.top, inf.left + hoja * 1.1f, inf.bottom), madera)
        rect(Rect(inf.right - hoja * 1.1f, inf.top, inf.right, inf.bottom), madera)
        rect(Rect(inf.left, inf.bottom - hoja * 1.6f, inf.right, inf.bottom), madera)
        val enc = Rect(inf.left, inf.top, inf.right, inf.top + hoja * 1.5f)
        drawRect(sombraMueble(if (o) 0.35f else 0.2f), Offset(enc.left, enc.bottom), Size(enc.width, 0.6f * u))
        rect(enc, madera)
        drawRect(luzMueble(if (o) 0.05f else 0.6f), enc.topLeft, Size(enc.width, 0.35f * u))
        drawRect(sombraMueble(if (o) 0.3f else 0.15f), Offset(enc.left, enc.bottom - 0.3f * u), Size(enc.width, 0.3f * u))
        // El pestillo de latón.
        val pc = Offset(enc.center.x, enc.top - 0.1f * u)
        drawOval(n(LATON_OSC, o), Offset(pc.x - 1.4f * u, pc.y - 0.5f * u), Size(2.8f * u, 1.1f * u))
        drawOval(lam(LATON, o, f), Offset(pc.x - 1.2f * u, pc.y - 0.6f * u), Size(2.4f * u, 0.9f * u))
        drawCircle(lam(LATON_LUZ, o, f), 0.3f * u, Offset(pc.x - 0.5f * u, pc.y - 0.3f * u))
        // El reflejo en los vidrios.
        listOf(Rect(g.left + hoja, g.top + hoja, g.right - hoja, medio - hoja * 0.4f), Rect(inf.left + hoja * 1.1f, enc.bottom, inf.right - hoja * 1.1f, inf.bottom - hoja * 1.6f)).forEach { v ->
            clipRect(v.left, v.top, v.right, v.bottom) {
                drawPath(Path().apply { moveTo(v.left, v.top + v.height * 0.55f); lineTo(v.left + v.width * 0.7f, v.top); lineTo(v.left + v.width, v.top); lineTo(v.left, v.top + v.height * 0.75f); close() },
                    Color.White.copy(alpha = if (o) 0.04f else 0.16f))
            }
        }
        // El alféizar que sobresale y el faldón de abajo.
        val sill = Rect(r.left - 1.4f * u, r.bottom - 0.2f * u, r.right + 1.4f * u, r.bottom + 1.7f * u)
        val faldon = Rect(r.left + 0.7f * u, sill.bottom, r.right - 0.7f * u, sill.bottom + 3.4f * u)
        sombraEnPared(faldon, 1.2f * u, if (o) 0.35f else 0.18f)
        rect(faldon, lam(MOLDURA, o, f))
        drawLine(n(MOLDURA_OSC, o), Offset(faldon.left, faldon.top + faldon.height * 0.45f), Offset(faldon.right, faldon.top + faldon.height * 0.45f), 0.25f * u)
        drawRect(sombraMueble(if (o) 0.25f else 0.12f), Offset(faldon.right - 0.4f * u, faldon.top), Size(0.4f * u, faldon.height))
        drawRect(sombraMueble(if (o) 0.35f else 0.22f), Offset(sill.left + 0.4f * u, sill.bottom), Size(sill.width, 0.8f * u))
        rect(sill, lam(MOLDURA, o, f))
        drawRect(luzMueble(if (o) 0.06f + 0.2f * f else 0.7f), sill.topLeft, Size(sill.width, 0.45f * u))
        drawRect(n(MOLDURA_OSC, o), Offset(sill.left, sill.bottom - 0.45f * u), Size(sill.width, 0.45f * u))
    }

    /**
     * Lo que se ve por la ventana: de día, el cielo blanco de la mañana con la niebla que baja; en la loma, la segunda fila
     * de casas casi borrada; enfrente, la casa victoriana gris, la vecina de blanco gastado y una rosa, con los cables del
     * poste. De noche, la misma calle: el cielo violeta, la niebla alumbrada por una farola, las ventanas encendidas y, en el
     * mirador de enfrente, la luz negra de otro cuarto.
     */
    private fun DrawScope.calle(g: Rect, o: Boolean, u: Float, semilla: Int) {
        val gw = g.width; val gh = g.height
        fun c(x: Color, velo: Float = 0f): Color = if (o) mez(n(x, true), NIEBLA_NOCHE, velo * 0.7f) else mez(x, NIEBLA, velo)
        // El cielo.
        if (o) {
            rect(g, CIELO_NOCHE)
            rect(Rect(g.left, g.top + gh * 0.4f, g.right, g.bottom), mez(CIELO_NOCHE, NIEBLA_NOCHE, 0.35f))
        } else {
            rect(g, CIELO)
            rect(Rect(g.left, g.top + gh * 0.22f, g.right, g.bottom), mez(CIELO, NIEBLA, 0.7f))
            halo(Offset(g.left + gw * 0.25f, g.top + gh * 0.12f), gw * 0.7f, Color.White, 0.6f, 12)
        }
        // La loma con la segunda fila de casas, casi borradas por la niebla.
        val loma = g.top + gh * 0.44f
        with(TrazosPsicodelia) {
            casaVictoriana(Offset(g.left + gw * 0.15f, loma), gw * 0.58f, gw * 0.7f, c(TrazosPsicodelia.CASA_GRIS, 0.45f), c(TrazosPsicodelia.CASA_BLANCA, 0.45f),
                if (o) mez(FVIOLETA, NIEBLA_NOCHE, 0.3f) else c(Color(0xFF5E6B78), 0.45f))
            casaVictoriana(Offset(g.left + gw * 0.8f, loma + gh * 0.02f), gw * 0.6f, gw * 0.74f, c(TrazosPsicodelia.CASA_ROSA, 0.4f), c(TrazosPsicodelia.CASA_BLANCA, 0.4f),
                if (o) mez(FAROL, NIEBLA_NOCHE, 0.45f) else c(Color(0xFF5E6B78), 0.4f))
        }
        rect(Rect(g.left, loma, g.right, g.top + gh * 0.52f), c(Color(0xFF7E8A78), 0.6f))
        // La niebla que baja por la loma.
        for (k in 0 until 4) {
            val y = g.top + gh * (0.3f + 0.07f * k)
            drawPath(TrazosPsicodelia.gota(Offset(g.left + gw * (0.2f + 0.5f * azar(semilla, k + 5)), y), gw * (0.45f + 0.2f * azar(semilla, k + 6)), k * 0.3f, 0.18f, 3),
                (if (o) NIEBLA_NOCHE else NIEBLA).copy(alpha = if (o) 0.32f else 0.42f))
        }
        // Enfrente: la casa gris, con la blanca gastada asomando a la izquierda.
        val base = g.bottom + gh * 0.05f
        val vent = if (o) mez(FAROL, n(TrazosPsicodelia.CASA_GRIS, true), 0.25f) else Color(0xFF5E6B78)
        with(TrazosPsicodelia) {
            casaVictoriana(Offset(g.left - gw * 0.15f, base + gh * 0.02f), gw * 0.95f, gw * 1.25f, c(TrazosPsicodelia.CASA_BLANCA, 0.12f), c(Color(0xFFF4F0E8), 0.12f), vent)
            casaVictoriana(Offset(g.left + gw * 0.72f, base), gw * 1.05f, gw * 1.4f, c(TrazosPsicodelia.CASA_GRIS, 0.06f), c(TrazosPsicodelia.CASA_BLANCA, 0.06f),
                if (o) mez(FVIOLETA, SALA, 0.15f) else vent)
        }
        // De noche, en el mirador de enfrente, las luces líquidas de otro cuarto con luz negra.
        if (o) {
            val mir = Rect(g.left + gw * 0.4f, base - gw * 1.4f * 0.38f, g.left + gw * 0.92f, base - gw * 1.4f * 0.22f)
            clipRect(mir.left, mir.top, mir.right, mir.bottom) { with(TrazosPsicodelia) { lucesLiquidas(mir.size, 0.3f, listOf(FMAGENTA, LIMON), 0.9f, mir.topLeft) } }
            halo(mir.center, gw * 0.45f, FVIOLETA, 0.35f, 12)
        }
        // Los cables del poste que cruzan.
        for (k in 0..2) drawLine((if (o) Color(0xFF0A0610) else Color(0xFF3A3540)).copy(alpha = 0.7f), Offset(g.left, g.top + gh * (0.12f + 0.035f * k)), Offset(g.right, g.top + gh * (0.2f + 0.03f * k)), 0.16f * u)
        // De noche, la farola de la calle con su halo en la niebla.
        if (o) {
            val fa = Offset(g.left + gw * 0.18f, g.top + gh * 0.62f)
            halo(fa, gw * 0.95f, FAROL, 0.8f, 16)
            drawLine(Color(0xFF0E0A16), Offset(fa.x - gw * 0.02f, fa.y), Offset(fa.x - gw * 0.02f, g.bottom), 0.7f * u)
            drawLine(Color(0xFF0E0A16), Offset(fa.x - gw * 0.02f, fa.y), Offset(fa.x + gw * 0.12f, fa.y - gh * 0.01f), 0.45f * u)
            drawOval(Color(0xFFFFE6B0), Offset(fa.x + gw * 0.04f, fa.y - 0.2f * u), Size(gw * 0.14f, 1.1f * u))
        }
        // Un velo de niebla sobre todo, más espeso abajo.
        rect(Rect(g.left, g.top + gh * 0.6f, g.right, g.bottom), (if (o) NIEBLA_NOCHE else NIEBLA).copy(alpha = if (o) 0.18f else 0.25f))
    }

    /**
     * La cortina de algodón teñido anudado, colgada de su barra y recogida a la izquierda con un lazo: sobre el amarillo, las
     * espirales de magenta y naranja con las líneas claras de los nudos, y los pliegues de la tela.
     */
    private fun DrawScope.cortina(v: Rect, o: Boolean, u: Float, f: (Offset) -> Float) {
        val barra = v.top - 1.6f * u
        val x0 = v.left - 2.2f * u; val x1 = v.left + v.width * 0.55f
        val lazo = Offset(v.left + v.width * 0.14f, v.top + v.height * 0.6f)
        val pie = v.bottom - 0.4f * u
        val fz = f(lazo)
        val tela = Path().apply {
            moveTo(x0, barra); lineTo(x1, barra)
            cubicTo(x1, barra + (lazo.y - barra) * 0.45f, lazo.x + (x1 - lazo.x) * 0.25f, lazo.y - (lazo.y - barra) * 0.22f, lazo.x, lazo.y)
            cubicTo(lazo.x + 0.8f * u, lazo.y + (pie - lazo.y) * 0.4f, lazo.x + 2.6f * u, pie - 3f * u, lazo.x + 3.2f * u, pie)
            lineTo(x0, pie); close()
        }
        translate(1.2f * u, 1f * u) { drawPath(tela, sombraMueble(if (o) 0.4f else 0.22f)) }
        drawPath(tela, lam(AMARILLO, o, fz))
        clipPath(tela) {
            listOf(Offset(x0 + (x1 - x0) * 0.42f, barra + (lazo.y - barra) * 0.42f) to (x1 - x0) * 0.75f, Offset(lazo.x - 0.5f * u, pie - (pie - lazo.y) * 0.3f) to (pie - lazo.y) * 0.62f).forEachIndexed { j, (c, rr) ->
                for (k in 7 downTo 1) {
                    val col = listOf(MAGENTA, NARANJA, AMARILLO)[k % 3]
                    drawPath(TrazosPsicodelia.gota(c, rr * k / 7f, j * 0.2f + k * 0.13f, 0.16f, 5), lam(col, o, fz).copy(alpha = 0.92f))
                }
                // Las líneas claras de los nudos, que salen del centro.
                for (k in 0 until 9) {
                    val a = 2f * PI.toFloat() * k / 9f + j
                    drawLine(lam(Color(0xFFFFF4D6), o, fz).copy(alpha = 0.55f), c, Offset(c.x + rr * cos(a), c.y + rr * sin(a)), 0.25f * u)
                }
            }
            // Los pliegues: bandas de sombra y de luz que bajan hasta el lazo.
            for (k in 0 until 4) {
                val xa = x0 + (x1 - x0) * (0.15f + 0.22f * k)
                val pl = Path().apply { moveTo(xa, barra); quadraticTo(xa - (xa - lazo.x) * 0.3f, (barra + lazo.y) / 2f, lazo.x + (k - 1.5f) * 0.3f * u, lazo.y) }
                drawPath(pl, sombraMueble(if (o) 0.3f else 0.2f), style = Stroke(0.9f * u))
                translate(-0.8f * u, 0f) { drawPath(pl, luzMueble(if (o) 0.04f else 0.25f), style = Stroke(0.4f * u)) }
            }
            for (k in 0 until 3) {
                val xb = x0 + (lazo.x + 3f * u - x0) * (0.25f + 0.3f * k)
                drawLine(sombraMueble(if (o) 0.3f else 0.16f), Offset(lazo.x - 0.5f * u + (k - 1) * 0.3f * u, lazo.y), Offset(xb, pie), 0.8f * u)
            }
        }
        // El lazo de cordón y la barra con sus remates.
        drawRoundRect(lam(MAGENTA, o, fz), Offset(lazo.x - 2.6f * u, lazo.y - 0.7f * u), Size(4.4f * u, 1.4f * u), CornerRadius(0.7f * u))
        drawLine(lam(VIOLETA, o, fz), Offset(lazo.x + 1.5f * u, lazo.y), Offset(lazo.x + 2.4f * u, lazo.y + 2.4f * u), 0.4f * u, StrokeCap.Round)
        val br = Rect(v.left - 2.6f * u, barra - 0.7f * u, v.right - 1f * u, barra + 0.7f * u)
        sombraEnPared(br, 0.8f * u, if (o) 0.35f else 0.22f, 0.7f * u)
        drawRoundRect(n(PINO_OSC, o), br.topLeft, br.size, CornerRadius(0.7f * u))
        drawLine(n(PINO_LUZ, o), Offset(br.left + u, br.top + 0.3f * u), Offset(br.right - u, br.top + 0.3f * u), 0.3f * u)
        listOf(br.left, br.right).forEach { x -> drawCircle(n(PINO_OSC, o), 1.1f * u, Offset(x, barra)); drawCircle(n(PINO_LUZ, o), 0.4f * u, Offset(x - 0.3f * u, barra - 0.3f * u)) }
    }

    /** La mala madre en su maceta de barro, en el alféizar: las hojas largas rayadas de crema que se arquean y un hijuelo colgando. */
    private fun DrawScope.malaMadre(base: Offset, ancho: Float, o: Boolean, u: Float, f: Float) {
        val mh = ancho * 0.62f
        sombraContacto(Offset(base.x + 0.5f * u, base.y), ancho * 0.9f, 1.2f * u, if (o) 0.4f else 0.3f)
        val c = base.copy(y = base.y - mh)
        // Las hojas: arcos largos que salen del centro.
        for (k in 0 until 11) {
            val lado = if (k % 2 == 0) -1f else 1f
            val largo = ancho * (0.7f + 0.6f * azar(7, k))
            val alto = mh * (0.6f + 1.2f * azar(8, k))
            val punta = Offset(c.x + lado * largo * (0.4f + 0.6f * azar(9, k)), c.y - alto * 0.2f + largo * 0.5f * azar(10, k))
            val ctrl = Offset(c.x + lado * largo * 0.2f, c.y - alto)
            val hoja = Path().apply { moveTo(c.x, c.y); quadraticTo(ctrl.x, ctrl.y, punta.x, punta.y) }
            drawPath(hoja, lam(if (k % 3 == 0) HOJA_OSC else HOJA, o, f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            drawPath(hoja, lam(Color(0xFFF2EDC8), o, f).copy(alpha = 0.7f), style = Stroke(0.25f * u, cap = StrokeCap.Round))
        }
        // El hijuelo que cuelga de su tallo.
        val t1 = Offset(c.x - ancho * 0.9f, c.y + mh * 1.6f)
        drawPath(Path().apply { moveTo(c.x, c.y); quadraticTo(c.x - ancho * 0.8f, c.y - mh * 0.4f, t1.x, t1.y) }, lam(HOJA_OSC, o, f), style = Stroke(0.25f * u))
        for (k in 0 until 5) drawLine(lam(HOJA, o, f), t1, Offset(t1.x + (k - 2) * 0.9f * u, t1.y - (1.4f - 0.2f * k * (k - 4)) * u), 0.5f * u, StrokeCap.Round)
        // La maceta.
        val m = Path().apply { moveTo(base.x - mh * 0.55f, c.y); lineTo(base.x + mh * 0.55f, c.y); lineTo(base.x + mh * 0.4f, base.y); lineTo(base.x - mh * 0.4f, base.y); close() }
        drawPath(m, lam(BARRO, o, f))
        drawRect(lam(BARRO_OSC, o, f), Offset(base.x - mh * 0.6f, c.y), Size(mh * 1.2f, mh * 0.22f))
        drawRect(luzMueble(if (o) 0.04f else 0.3f), Offset(base.x - mh * 0.55f, c.y), Size(mh * 0.3f, mh))
        drawPath(Path().apply { moveTo(base.x + mh * 0.25f, c.y); lineTo(base.x + mh * 0.55f, c.y); lineTo(base.x + mh * 0.4f, base.y); lineTo(base.x + mh * 0.18f, base.y); close() }, sombraMueble(if (o) 0.3f else 0.2f))
    }

    /** Una gota de cachemir estampada a mano en [c], de tamaño [s] y girada [grados]: el cuerpo, la punta que se curva, el borde de puntos y el corazón. */
    private fun DrawScope.buta(c: Offset, s: Float, grados: Float, cuerpo: Color, borde: Color, corazon: Color) {
        rotate(grados, c) {
            val p = Path().apply {
                moveTo(c.x - s, c.y + s * 0.2f)
                cubicTo(c.x - s, c.y + s * 1.45f, c.x + s, c.y + s * 1.45f, c.x + s, c.y + s * 0.2f)
                cubicTo(c.x + s, c.y - s * 0.8f, c.x + s * 0.15f, c.y - s * 1.3f, c.x + s * 0.75f, c.y - s * 1.9f)
                cubicTo(c.x - s * 0.55f, c.y - s * 1.7f, c.x - s, c.y - s * 0.75f, c.x - s, c.y + s * 0.2f)
                close()
            }
            drawPath(p, cuerpo)
            drawPath(p, borde, style = Stroke(s * 0.16f))
            drawCircle(corazon, s * 0.42f, Offset(c.x, c.y + s * 0.35f))
            drawCircle(cuerpo, s * 0.16f, Offset(c.x, c.y + s * 0.35f))
        }
    }

    /**
     * La colcha de algodón de la India colgada como tapiz, en [t]: la vara de madera con sus presillas, la orla de granza con
     * las gotas de cachemir estampadas a mano, la cenefa de azafrán con sus puntos de añil, el filete de añil, el campo
     * liso de algodón crudo (el nicho [ni]) y el fleco. De noche, el azafrán de los puntos brilla apenas con la luz negra
     * (la cúrcuma lo hace).
     */
    private fun DrawScope.tapiz(t: Rect, ni: Rect, e: EstadoMueble, o: Boolean, u: Float) {
        val nPres = 7
        // El borde de arriba cuelga entre las presillas.
        val tela = Path().apply {
            moveTo(t.left, t.top)
            for (k in 0 until nPres - 1) {
                val xa = t.left + t.width * k / (nPres - 1); val xb = t.left + t.width * (k + 1) / (nPres - 1)
                quadraticTo((xa + xb) / 2f, t.top + 1.3f * u, xb, t.top)
            }
            lineTo(t.right, t.bottom); lineTo(t.left, t.bottom); close()
        }
        translate(1.6f * u, 1.4f * u) { drawPath(tela, sombraMueble(if (o) 0.4f else 0.2f)) }
        translate(0.8f * u, 0.7f * u) { drawPath(tela, sombraMueble(if (o) 0.3f else 0.14f)) }
        drawPath(tela, n(GRANZA, o))
        val anillo1 = Rect(entre(t.left, ni.left, 0.62f), entre(t.top, ni.top, 0.62f), entre(t.right, ni.right, 0.62f), entre(t.bottom, ni.bottom, 0.62f))
        val anillo2 = Rect(entre(t.left, ni.left, 0.87f), entre(t.top, ni.top, 0.87f), entre(t.right, ni.right, 0.87f), entre(t.bottom, ni.bottom, 0.87f))
        clipPath(tela) {
            // Las gotas de cachemir en la orla, de dos en dos, una hacia cada lado.
            val cuerpo = n(AZAFRAN, o); val borde = n(ANIL, o); val corazon = n(GRANZA_OSC, o)
            val sTop = (anillo1.top - t.top) * 0.28f
            var x = t.left + sTop * 2.2f; var k = 0
            while (x < t.right - sTop && k < 60) {
                buta(Offset(x, (t.top + anillo1.top) / 2f + sTop * 0.15f), sTop, if (k % 2 == 0) 20f else 200f, cuerpo, borde, corazon)
                buta(Offset(x, (t.bottom + anillo1.bottom) / 2f - sTop * 0.1f), sTop * 1.1f, if (k % 2 == 0) 200f else 20f, cuerpo, borde, corazon)
                x += sTop * 3.1f; k++
            }
            val sLado = (anillo1.left - t.left) * 0.3f
            var y = anillo1.top + sLado * 2f; k = 0
            while (y < anillo1.bottom - sLado && k < 60) {
                buta(Offset((t.left + anillo1.left) / 2f, y), sLado, if (k % 2 == 0) 110f else 290f, cuerpo, borde, corazon)
                buta(Offset((t.right + anillo1.right) / 2f, y), sLado, if (k % 2 == 0) 290f else 110f, cuerpo, borde, corazon)
                y += sLado * 3.1f; k++
            }
            // Las rosetas de las esquinas.
            listOf(Offset(t.left, t.top), Offset(t.right, t.top), Offset(t.left, t.bottom), Offset(t.right, t.bottom)).forEach { q ->
                val cq = Offset((q.x + if (q.x < ni.center.x) anillo1.left else anillo1.right) / 2f, (q.y + if (q.y < ni.center.y) anillo1.top else anillo1.bottom) / 2f)
                rect(Rect(cq.x - sTop * 1.6f, cq.y - sTop * 1.6f, cq.x + sTop * 1.6f, cq.y + sTop * 1.6f), n(GRANZA, o))
                with(TrazosPsicodelia) { margarita(cq, sTop * 1.4f, n(AZAFRAN, o), n(ANIL, o), 8) }
            }
            // La cenefa de azafrán con sus puntos de añil y el filete de añil.
            rect(anillo1, n(AZAFRAN, o))
            rect(Rect(anillo1.left, anillo1.top, anillo1.right, anillo1.top + 0.3f * u), n(ANIL, o))
            val punto = if (o) mez(n(ANIL, o), FVIOLETA, 0.25f) else ANIL
            val paso = 1.9f * u
            var px = anillo1.left + paso / 2
            while (px < anillo1.right) {
                drawCircle(punto, 0.38f * u, Offset(px, (anillo1.top + anillo2.top) / 2f))
                drawCircle(punto, 0.38f * u, Offset(px, (anillo1.bottom + anillo2.bottom) / 2f))
                px += paso
            }
            var py = anillo2.top + paso / 2
            while (py < anillo2.bottom) {
                drawCircle(punto, 0.38f * u, Offset((anillo1.left + anillo2.left) / 2f, py))
                drawCircle(punto, 0.38f * u, Offset((anillo1.right + anillo2.right) / 2f, py))
                py += paso
            }
            rect(anillo2, n(ANIL, o))
            // El campo liso de algodón crudo: el nicho.
            rect(ni, nicho(e, o, ALGODON))
            rect(Rect(ni.left, ni.top, ni.right, ni.top + 0.6f * u), sombraMueble(if (o) 0.25f else 0.1f))
            rect(Rect(ni.left, ni.top, ni.left + 0.5f * u, ni.bottom), sombraMueble(if (o) 0.2f else 0.07f))
            // Los pliegues de la tela, solo en la orla: bandas tenues que bajan de las presillas.
            fueraDe(ni) {
                for (q in 0 until nPres - 1) {
                    val xa = t.left + t.width * (q + 0.5f) / (nPres - 1)
                    val baja = t.height * (0.3f + 0.25f * azar(e.semilla, q + 3))
                    drawPath(Path().apply { moveTo(xa - 2.4f * u, t.top); lineTo(xa + 2.4f * u, t.top); lineTo(xa + 0.3f * u, t.top + baja); close() }, sombraMueble(if (o) 0.18f else 0.12f))
                    drawPath(Path().apply { moveTo(xa - 4.6f * u, t.top); lineTo(xa - 2.6f * u, t.top); lineTo(xa - 1.2f * u, t.top + baja * 0.7f); close() }, luzMueble(if (o) 0.03f else 0.2f))
                }
                pinceladas(t, n(GRANZA_OSC, o), e.semilla + 21, 30, u * 0.6f, if (o) 0.06f else 0.12f)
            }
            // La luz de la ventana sobre la tela, de la izquierda, y la sombra del lado derecho.
            drawRect(sombraMueble(if (o) 0.2f else 0.1f), Offset(t.right - 2f * u, t.top), Size(2f * u, t.height))
        }
        // El fleco.
        val fleco = n(mez(ALGODON, GRANZA, 0.25f), o)
        var xf = t.left + 0.4f * u; var j = 0
        while (xf < t.right && j < 400) {
            drawLine(fleco, Offset(xf, t.bottom), Offset(xf + 0.25f * u * sin(j * 1.7f), t.bottom + (1.6f + 0.6f * azar(e.semilla, j)) * u), 0.3f * u)
            xf += 0.75f * u; j++
        }
        // La vara de madera con sus remates, los clavos y las presillas.
        val vara = Rect(t.left - 1.2f * u, t.top - 2.2f * u, t.right + 2.6f * u, t.top - 0.8f * u)
        sombraEnPared(vara, 0.9f * u, if (o) 0.4f else 0.25f, 0.7f * u)
        drawRoundRect(n(PINO_OSC, o), vara.topLeft, vara.size, CornerRadius(0.7f * u))
        drawRect(n(PINO, o), Offset(vara.left + u, vara.top + 0.2f * u), Size(vara.width - 2f * u, vara.height * 0.45f))
        drawRect(luzMueble(if (o) 0.05f else 0.4f), Offset(vara.left + u, vara.top + 0.2f * u), Size(vara.width - 2f * u, 0.25f * u))
        listOf(vara.left, vara.right).forEach { xr ->
            drawCircle(n(PINO_OSC, o), 1.3f * u, Offset(xr, vara.center.y))
            drawCircle(n(PINO_LUZ, o), 0.5f * u, Offset(xr - 0.35f * u, vara.center.y - 0.35f * u))
        }
        for (q in 0 until nPres) {
            val xp = t.left + t.width * q / (nPres - 1)
            val pr = Rect(xp - 1.3f * u, vara.top - 0.4f * u, xp + 1.3f * u, t.top + 1.1f * u)
            drawRoundRect(n(GRANZA_OSC, o), pr.topLeft, pr.size, CornerRadius(0.8f * u))
            drawRoundRect(n(GRANZA, o), Offset(pr.left + 0.2f * u, pr.top), Size(pr.width * 0.6f, pr.height), CornerRadius(0.8f * u))
        }
    }

    /** El tubo de luz negra en su regleta de lámina: de día, el vidrio violeta oscuro, apagado; de noche, encendido, con su resplandor. */
    private fun DrawScope.tuboLuzNegra(r: Rect, o: Boolean, u: Float) {
        val reg = Rect(r.left - 1.4f * u, r.top, r.right + 1.4f * u, r.top + r.height * 0.55f)
        if (o) halo(Offset(r.center.x, r.bottom), r.width * 0.75f, FVIOLETA, 0.8f, 18)
        sombraEnPared(reg, 1f * u, if (o) 0.3f else 0.22f, 0.5f * u)
        drawRoundRect(n(LAMINA, o), reg.topLeft, reg.size, CornerRadius(0.5f * u))
        drawRect(luzMueble(if (o) 0.05f else 0.55f), Offset(reg.left + 0.5f * u, reg.top + 0.2f * u), Size(reg.width - u, 0.3f * u))
        drawRect(sombraMueble(if (o) 0.3f else 0.2f), Offset(reg.left, reg.bottom - 0.4f * u), Size(reg.width, 0.4f * u))
        // Los portalámparas de las puntas.
        listOf(r.left - 0.6f * u, r.right - 1.2f * u).forEach { x ->
            drawRoundRect(n(LAMINA_OSC, o), Offset(x, reg.top + 0.3f * u), Size(1.8f * u, r.height - 0.3f * u), CornerRadius(0.4f * u))
            drawRect(luzMueble(if (o) 0.04f else 0.35f), Offset(x + 0.2f * u, reg.top + 0.5f * u), Size(0.4f * u, r.height - u))
        }
        // El tubo.
        val tb = Rect(r.left + 1.2f * u, r.bottom - 1.9f * u, r.right - 1.2f * u, r.bottom - 0.4f * u)
        if (o) {
            drawRoundRect(FVIOLETA, tb.topLeft, tb.size, CornerRadius(tb.height / 2))
            drawRoundRect(Color(0xFFE6DDFF), Offset(tb.left + u, tb.top + tb.height * 0.25f), Size(tb.width - 2f * u, tb.height * 0.35f), CornerRadius(tb.height / 4))
        } else {
            drawRoundRect(TUBO, tb.topLeft, tb.size, CornerRadius(tb.height / 2))
            drawRoundRect(Color.White.copy(alpha = 0.35f), Offset(tb.left + u, tb.top + tb.height * 0.2f), Size(tb.width - 2f * u, tb.height * 0.25f), CornerRadius(tb.height / 4))
        }
        // El cable que baja por la pared hasta el enchufe.
        drawPath(Path().apply { moveTo(reg.right, reg.center.y); cubicTo(reg.right + 3f * u, reg.center.y, reg.right + 2f * u, reg.bottom + 4f * u, reg.right + 3.5f * u, reg.bottom + 6f * u) },
            n(Color(0xFF3A3436), o), style = Stroke(0.3f * u))
    }

    /** El tapete trenzado de trapos, ovalado: las vueltas de colores, cada una con su trenza. */
    private fun DrawScope.tapete(c: Offset, rx: Float, ry: Float, o: Boolean, u: Float) {
        drawOval(sombraMueble(if (o) 0.4f else 0.22f), Offset(c.x - rx + 0.8f * u, c.y - ry + 0.8f * u), Size(rx * 2f, ry * 2f))
        val colores = listOf(Color(0xFF6E5A8E), MAGENTA, Color(0xFFD9B36A), Color(0xFF3F7A6E), NARANJA, Color(0xFF8E3B4A), Color(0xFFD9B36A))
        val vueltas = 7
        for (k in 0 until vueltas) {
            val f = 1f - k / vueltas.toFloat()
            val ax = rx * f; val ay = ry * f
            drawOval(n(mez(colores[k], PARED, 0.18f), o), Offset(c.x - ax, c.y - ay), Size(ax * 2f, ay * 2f))
            // La trenza: rayitas en diagonal a lo largo de la vuelta.
            val pasos = (ax / (0.9f * u)).toInt().coerceIn(8, 160)
            for (s in 0 until pasos) {
                val a = 2f * PI.toFloat() * s / pasos
                val q = Offset(c.x + ax * 0.93f * cos(a), c.y + ay * 0.9f * sin(a))
                drawLine(sombraMueble(if (o) 0.3f else 0.18f), q, Offset(q.x + 0.5f * u, q.y + 0.25f * u), 0.2f * u)
            }
        }
        drawOval(luzMueble(if (o) 0.03f else 0.12f), Offset(c.x - rx * 0.9f, c.y - ry * 0.95f), Size(rx * 1.2f, ry * 0.9f))
    }

    /**
     * La caja de fruta puesta de pie como mesita: las cabeceras gruesas arriba y abajo, las tablillas de los costados, el
     * fondo de tablillas con sus rendijas, el estante del medio (su divisor) y unos discos guardados abajo.
     */
    private fun DrawScope.cajaDePie(r: Rect, o: Boolean, u: Float, semilla: Int, f: Float) {
        sombraContacto(Offset(r.center.x + 1.2f * u, r.bottom), r.width * 1.05f, 2.2f * u, if (o) 0.5f else 0.32f)
        val cab = r.height * 0.07f; val lado = r.width * 0.07f
        val dentro = Rect(r.left + lado, r.top + cab, r.right - lado, r.bottom - cab)
        // El fondo: tablillas verticales con sus rendijas, en sombra.
        rect(dentro, lam(PINO_OSC, o, f * 0.6f))
        val nt = 3
        for (k in 0 until nt) {
            val tb = Rect(dentro.left + dentro.width * k / nt + 0.25f * u, dentro.top, dentro.left + dentro.width * (k + 1) / nt - 0.25f * u, dentro.bottom)
            pino(tb, o, u, semilla + k, mez(PINO, PINO_OSC, 0.45f), vertical = true, f = f * 0.6f, nudos = false)
        }
        val medio = dentro.center.y
        // La sombra de arriba y del costado izquierdo dentro de cada hueco.
        listOf(Rect(dentro.left, dentro.top, dentro.right, medio), Rect(dentro.left, medio, dentro.right, dentro.bottom)).forEach { hu ->
            drawRect(sombraMueble(if (o) 0.4f else 0.32f), hu.topLeft, Size(hu.width, 1.6f * u))
            drawRect(sombraMueble(if (o) 0.3f else 0.2f), hu.topLeft, Size(1.4f * u, hu.height))
        }
        // Discos guardados abajo, de canto.
        val cols = listOf(MAGENTA, Color(0xFF1C1524), AMARILLO, Color(0xFF3F7A6E), NARANJA)
        for (k in 0 until 5) {
            val x = dentro.left + 1.6f * u + k * 1.1f * u
            val d = Rect(x, medio + 2.4f * u + (k % 2) * 0.4f * u, x + 1f * u, dentro.bottom)
            rect(d, lam(cols[k], o, f))
            drawRect(sombraMueble(0.3f), Offset(d.right - 0.25f * u, d.top), Size(0.25f * u, d.height))
        }
        drawPath(Path().apply { val x = dentro.left + 7.4f * u; moveTo(x, dentro.bottom); lineTo(x + 3.6f * u, medio + 3.2f * u); lineTo(x + 4.6f * u, medio + 3.6f * u); lineTo(x + 1.2f * u, dentro.bottom); close() }, lam(CREMA, o, f))
        // El estante del medio, las cabeceras y los costados.
        tabla(Rect(dentro.left, medio - 0.6f * u, dentro.right, medio + 0.7f * u), o, u, semilla + 7, f = f)
        tabla(Rect(r.left, r.top, r.right, r.top + cab), o, u, semilla + 8, f = f)
        tabla(Rect(r.left, r.bottom - cab, r.right, r.bottom), o, u, semilla + 9, f = f)
        tabla(Rect(r.left, r.top + cab, r.left + lado, r.bottom - cab), o, u, semilla + 10, vertical = true, f = f)
        tabla(Rect(r.right - lado, r.top + cab, r.right, r.bottom - cab), o, u, semilla + 11, vertical = true, f = f)
        // Los clavos.
        listOf(r.top + cab * 0.5f, r.bottom - cab * 0.5f).forEach { y -> listOf(r.left + lado * 0.5f, r.right - lado * 0.5f).forEach { x -> drawCircle(n(Color(0xFF4A4448), o), 0.3f * u, Offset(x, y)) } }
    }

    /**
     * La lámpara de cera (otra silueta que la de cohete; la misma de la repisa de discos): el globo de vidrio sobre el pie de
     * cono de latón con sus agujeritos. De día, apagada: la cera quieta en el fondo del líquido ámbar. De noche, encendida:
     * el líquido brilla naranja, la cera sube en gotas blandas y alumbra alrededor.
     */
    private fun DrawScope.lamparaCera(pie: Offset, alto: Float, o: Boolean, u: Float) {
        val an = alto * 0.5f
        sombraContacto(Offset(pie.x + 0.6f * u, pie.y), an * 1.1f, 1.2f * u, if (o) 0.3f else 0.32f)
        val gc = Offset(pie.x, pie.y - alto * 0.3f - an * 0.42f)
        val gr = an * 0.44f
        if (o) halo(gc, alto * 1.2f, FNARANJA, 1f, 20)
        // El pie cónico.
        val cono = Path().apply {
            moveTo(pie.x - an * 0.42f, pie.y); lineTo(pie.x + an * 0.42f, pie.y)
            lineTo(pie.x + an * 0.24f, pie.y - alto * 0.3f); lineTo(pie.x - an * 0.24f, pie.y - alto * 0.3f); close()
        }
        val laton = if (o) mez(n(LATON, o), FNARANJA, 0.35f) else LATON
        drawPath(cono, laton)
        drawPath(Path().apply { moveTo(pie.x - an * 0.42f, pie.y); lineTo(pie.x - an * 0.24f, pie.y - alto * 0.3f); lineTo(pie.x - an * 0.1f, pie.y - alto * 0.3f); lineTo(pie.x - an * 0.18f, pie.y); close() },
            if (o) luzMueble(0.12f) else luzMueble(0.45f))
        drawPath(Path().apply { moveTo(pie.x + an * 0.42f, pie.y); lineTo(pie.x + an * 0.24f, pie.y - alto * 0.3f); lineTo(pie.x + an * 0.08f, pie.y - alto * 0.3f); lineTo(pie.x + an * 0.16f, pie.y); close() },
            sombraMueble(if (o) 0.3f else 0.35f))
        drawRect(if (o) n(LATON_OSC, o) else LATON_OSC, Offset(pie.x - an * 0.43f, pie.y - 0.5f * u), Size(an * 0.86f, 0.5f * u))
        for (i in -1..1) drawCircle(if (o) FNARANJA else Color(0xFF3A2A1A), 0.45f * u, Offset(pie.x + i * an * 0.12f, pie.y - alto * 0.12f))
        // El globo.
        if (o) {
            drawCircle(Color(0xFFB8501E), gr, gc)
            drawCircle(Color(0xFFFFA24A), gr * 0.88f, Offset(gc.x, gc.y + gr * 0.08f))
            drawCircle(Color(0xFFFFE0A0), gr * 0.5f, Offset(gc.x, gc.y + gr * 0.3f))
        } else {
            drawCircle(Color(0xFFD9944A), gr, gc)
            drawCircle(Color(0xFFF2C886), gr * 0.86f, Offset(gc.x - gr * 0.06f, gc.y - gr * 0.08f))
        }
        clipPath(Path().apply { addOval(Rect(gc, gr)) }) {
            if (o) {
                drawPath(TrazosPsicodelia.gota(Offset(gc.x - gr * 0.22f, gc.y - gr * 0.38f), gr * 0.28f, 0.2f, 0.18f), FMAGENTA)
                drawPath(TrazosPsicodelia.gota(Offset(gc.x + gr * 0.32f, gc.y + gr * 0.05f), gr * 0.2f, 0.6f, 0.2f), FMAGENTA)
                drawPath(TrazosPsicodelia.gota(Offset(gc.x, gc.y + gr * 1.05f), gr * 0.62f, 0.1f, 0.08f), FMAGENTA)
                drawCircle(Color(0xFFFFC2E6), gr * 0.08f, Offset(gc.x - gr * 0.28f, gc.y - gr * 0.46f))
            } else {
                drawPath(TrazosPsicodelia.gota(Offset(gc.x, gc.y + gr * 1.12f), gr * 0.7f, 0.1f, 0.06f), CERA)
                drawCircle(mez(CERA, Color.White, 0.3f), gr * 0.1f, Offset(gc.x - gr * 0.25f, gc.y + gr * 0.58f))
            }
            drawCircle(sombraMueble(if (o) 0.15f else 0.2f), gr, Offset(gc.x + gr * 0.55f, gc.y + gr * 0.3f), style = Stroke(gr * 0.3f))
        }
        drawArc(Color.White.copy(alpha = if (o) 0.45f else 0.6f), 200f, 55f, false, Offset(gc.x - gr * 0.72f, gc.y - gr * 0.72f), Size(gr * 1.44f, gr * 1.44f), style = Stroke(gr * 0.1f, cap = StrokeCap.Round))
        // El cuello y la tapita de latón.
        drawRect(laton, Offset(pie.x - an * 0.17f, gc.y + gr * 0.9f), Size(an * 0.34f, pie.y - alto * 0.3f - gc.y - gr * 0.9f + 0.2f * u))
        drawCircle(laton, an * 0.09f, Offset(gc.x, gc.y - gr - an * 0.04f))
    }

    /**
     * La guitarra acústica apoyada en la pared, de pie en [pie] y de [largo] total, inclinada [grados]: la caja de abeto con
     * su filete y su golpeador, la boca con la roseta, el puente, el mástil con sus trastes, el clavijero y las cuerdas.
     */
    private fun DrawScope.guitarra(pie: Offset, largo: Float, grados: Float, o: Boolean, u: Float) {
        val rb = largo * 0.18f
        val c1 = Offset(pie.x, pie.y - rb * 1.05f)
        val c2 = Offset(pie.x, pie.y - rb * 2.55f)
        val caja = Path.combine(PathOperation.Union,
            Path().apply { addOval(Rect(c1.x - rb, c1.y - rb * 1.02f, c1.x + rb, c1.y + rb * 1.02f)) },
            Path.combine(PathOperation.Union,
                Path().apply { addOval(Rect(c2.x - rb * 0.78f, c2.y - rb * 0.8f, c2.x + rb * 0.78f, c2.y + rb * 0.8f)) },
                Path().apply { addRect(Rect(pie.x - rb * 0.62f, c2.y, pie.x + rb * 0.62f, c1.y)) }))
        // La sombra en la pared y en el piso.
        rotate(grados + 4f, pie) { translate(1.6f * u, -0.4f * u) { drawPath(caja, sombraMueble(if (o) 0.4f else 0.22f)) } }
        sombraContacto(Offset(pie.x + 1f * u, pie.y), rb * 1.6f, 1.6f * u, if (o) 0.45f else 0.3f)
        rotate(grados, pie) {
            val cuello = Rect(pie.x - rb * 0.15f, pie.y - largo * 0.94f, pie.x + rb * 0.15f, c2.y - rb * 0.7f)
            // El mástil y el clavijero.
            drawRect(n(ROSA_PALO, o), cuello.topLeft, cuello.size)
            drawRect(n(Color(0xFF7A4E2E), o), Offset(cuello.left - rb * 0.03f, cuello.top), Size(rb * 0.06f, cuello.height))
            var yt = c2.y - rb * 0.75f; var k = 0
            while (yt > cuello.top + rb * 0.1f && k < 30) { drawLine(n(LAMINA, o), Offset(cuello.left, yt), Offset(cuello.right, yt), 0.18f * u); yt -= rb * (0.32f - 0.008f * k); k++ }
            val cl = Rect(pie.x - rb * 0.22f, pie.y - largo, pie.x + rb * 0.22f, cuello.top + rb * 0.02f)
            drawRoundRect(n(Color(0xFF3A2216), o), cl.topLeft, cl.size, CornerRadius(rb * 0.08f))
            for (q in 0 until 3) listOf(-1f, 1f).forEach { s ->
                val cy = cl.top + cl.height * (0.22f + 0.28f * q)
                drawRoundRect(n(LATON_LUZ, o), Offset(pie.x + s * rb * 0.22f - (if (s < 0) rb * 0.14f else 0f), cy - rb * 0.05f), Size(rb * 0.14f, rb * 0.1f), CornerRadius(rb * 0.04f))
            }
            // La caja de abeto, con su filete y la luz de la ventana a la izquierda.
            drawPath(caja, n(Color(0xFFF4D9A4), o))
            translate(rb * 0.1f, 0f) { drawPath(caja, n(ABETO, o)) }
            clipPath(caja) {
                // El sombreado del borde, como el de las tapas de abeto, y la sombra del lado derecho siguiendo la curva.
                drawPath(caja, n(Color(0xFF9A5A22), o).copy(alpha = 0.55f), style = Stroke(rb * 0.34f))
                translate(-rb * 0.2f, 0f) { clipPath(caja, ClipOp.Difference) { translate(rb * 0.2f, 0f) { drawPath(caja, sombraMueble(if (o) 0.35f else 0.3f)) } } }
                pinceladas(Rect(pie.x - rb, c2.y - rb, pie.x + rb, pie.y), n(Color(0xFFF4D9A4), o), 5, 8, rb * 0.08f, if (o) 0.05f else 0.15f)
            }
            drawPath(caja, n(Color(0xFF2A1810), o), style = Stroke(0.45f * u))
            drawPath(caja, n(CREMA, o), style = Stroke(0.15f * u))
            // El golpeador, la boca con su roseta, el puente y las cuerdas.
            val boca = Offset(pie.x, c2.y + rb * 0.75f)
            drawPath(Path().apply { moveTo(boca.x + rb * 0.25f, boca.y + rb * 0.15f); quadraticTo(boca.x + rb * 0.7f, boca.y + rb * 0.3f, boca.x + rb * 0.55f, boca.y + rb * 0.75f); quadraticTo(boca.x + rb * 0.25f, boca.y + rb * 0.6f, boca.x + rb * 0.25f, boca.y + rb * 0.15f); close() },
                n(Color(0xFF6A2E1C), o))
            drawCircle(n(Color(0xFF2A1810), o), rb * 0.36f, boca, style = Stroke(rb * 0.1f))
            drawCircle(n(MAGENTA, o), rb * 0.42f, boca, style = Stroke(rb * 0.03f))
            drawCircle(n(Color(0xFF120C10), o), rb * 0.3f, boca)
            val puente = Rect(pie.x - rb * 0.42f, c1.y + rb * 0.18f, pie.x + rb * 0.42f, c1.y + rb * 0.3f)
            drawRoundRect(n(ROSA_PALO, o), puente.topLeft, puente.size, CornerRadius(rb * 0.05f))
            for (q in 0 until 6) {
                val dx = (q - 2.5f) / 2.5f
                drawLine(n(LAMINA, o).copy(alpha = 0.85f), Offset(pie.x + dx * rb * 0.14f, puente.center.y), Offset(pie.x + dx * rb * 0.1f, cl.bottom), 0.1f * u)
            }
        }
    }

    /**
     * Un cojín grande del piso, en [r]: la funda abultada con su ribete y su botón, la luz de la ventana arriba a la izquierda,
     * la sombra abajo a la derecha y una borla en cada punta. [estampado]: 0, tela de la India con espejitos bordados; 1,
     * teñido anudado en espiral; 2, cuadros de algodón de la India. [f] es la luz de la lámpara de cera.
     */
    private fun DrawScope.cojin(r: Rect, o: Boolean, u: Float, base: Color, estampado: Int, semilla: Int, f: Float) {
        val b = r.height * 0.12f
        val funda = Path().apply {
            moveTo(r.left + b, r.top + b * 0.6f)
            quadraticTo(r.center.x, r.top - b * 0.5f, r.right - b, r.top + b * 0.6f)
            quadraticTo(r.right + b * 0.4f, r.center.y, r.right - b * 0.5f, r.bottom - b * 0.3f)
            quadraticTo(r.center.x, r.bottom + b * 0.35f, r.left + b * 0.5f, r.bottom - b * 0.3f)
            quadraticTo(r.left - b * 0.4f, r.center.y, r.left + b, r.top + b * 0.6f)
            close()
        }
        sombraContacto(Offset(r.center.x + 1.8f * u, r.bottom - 0.2f * u), r.width * 1.05f, b * 1.6f, if (o) 0.5f else 0.3f)
        drawPath(funda, lam(base, o, f))
        clipPath(funda) {
            when (estampado) {
                0 -> {
                    // Filas de espejitos con su bordado de azafrán y zigzags de hilo verde.
                    val paso = r.width / 6f
                    var fila = 0; var y = r.top + paso * 0.45f
                    while (y < r.bottom + paso && fila < 10) {
                        var x = r.left + (if (fila % 2 == 0) paso * 0.3f else paso * 0.8f)
                        while (x < r.right + paso) {
                            drawCircle(lam(AZAFRAN, o, f), paso * 0.2f, Offset(x, y))
                            drawCircle(if (o) mez(n(LAMINA, o), FVIOLETA, 0.2f) else Color(0xFFDCE2E8), paso * 0.12f, Offset(x, y))
                            drawCircle(Color.White.copy(alpha = if (o) 0.15f else 0.7f), paso * 0.04f, Offset(x - paso * 0.04f, y - paso * 0.04f))
                            x += paso
                        }
                        drawPath(TrazosPsicodelia.onda(r.left, r.right, y + paso * 0.48f, paso * 0.1f, paso * 0.4f), lam(VERDE, o, f), style = Stroke(0.3f * u))
                        y += paso * 0.95f; fila++
                    }
                }
                1 -> {
                    // La espiral del teñido: brazos de colores que giran desde el centro, con las líneas claras de los pliegues.
                    val c = Offset(r.center.x - r.width * 0.03f, r.center.y + r.height * 0.02f)
                    val rr = r.width * 0.62f
                    val cols = listOf(MAGENTA, VIOLETA, AZUL, VERDE, NARANJA)
                    for (k in cols.indices) {
                        val brazo = Path().apply {
                            moveTo(c.x, c.y)
                            for (s in 0..30) { val q = s / 30f; val a = 2f * PI.toFloat() * k / cols.size + q * 4.2f; lineTo(c.x + rr * q * cos(a), c.y + rr * q * sin(a) * 0.8f) }
                        }
                        drawPath(brazo, (if (o) mez(fl(cols[k], true), SALA, 0.22f) else cols[k]).copy(alpha = 0.85f), style = Stroke(r.height * 0.2f, cap = StrokeCap.Round))
                    }
                    for (k in 0 until 12) {
                        val a = 2f * PI.toFloat() * k / 12f
                        drawPath(Path().apply { moveTo(c.x, c.y); quadraticTo(c.x + rr * 0.4f * cos(a + 0.5f), c.y + rr * 0.4f * sin(a + 0.5f) * 0.8f, c.x + rr * cos(a + 0.9f), c.y + rr * sin(a + 0.9f) * 0.8f) },
                            Color.White.copy(alpha = if (o) 0.12f else 0.4f), style = Stroke(0.25f * u))
                    }
                    if (o) halo(c, r.width * 0.5f, FMAGENTA, 0.35f, 10)
                }
                else -> {
                    // Cuadros de algodón: franjas que se cruzan, de colores que se mezclan.
                    val paso = r.height / 5f
                    for (k in 0 until 12) {
                        val col = listOf(MAGENTA, AMARILLO, Color(0xFF3F7A6E))[k % 3]
                        drawRect(lam(col, o, f).copy(alpha = 0.32f), Offset(r.left + k * paso * 0.9f, r.top - b), Size(paso * 0.3f, r.height + 2f * b))
                        drawRect(lam(col, o, f).copy(alpha = 0.32f), Offset(r.left - b, r.top + k * paso * 0.9f), Size(r.width + 2f * b, paso * 0.3f))
                    }
                    for (k in 0 until 10) drawLine(lam(Color(0xFF3A1E2E), o, f).copy(alpha = 0.3f), Offset(r.left + k * paso * 1.8f, r.top - b), Offset(r.left + k * paso * 1.8f, r.bottom + b), 0.2f * u)
                }
            }
            // El volumen: la luz arriba a la izquierda, la sombra abajo a la derecha, en óvalos sobrepuestos.
            for (k in 0 until 3) {
                val s = 1f - k * 0.25f
                drawOval(luzMueble(if (o) 0.02f + 0.04f * f else 0.1f), Offset(r.left + r.width * 0.05f, r.top), Size(r.width * 0.6f * s, r.height * 0.55f * s))
                drawOval(sombraMueble(if (o) 0.12f else 0.1f), Offset(r.right - r.width * 0.65f * s, r.bottom - r.height * 0.62f * s), Size(r.width * 0.75f * s, r.height * 0.7f * s))
            }
        }
        // El ribete, el botón del centro con sus pliegues y las borlas.
        drawPath(funda, lam(mez(base, Color.Black, 0.35f), o, f), style = Stroke(0.45f * u))
        val bc = Offset(r.center.x, r.center.y + r.height * 0.03f)
        for (k in 0 until 4) { val a = PI.toFloat() / 4f + k * PI.toFloat() / 2f; drawLine(sombraMueble(if (o) 0.3f else 0.22f), bc, Offset(bc.x + r.width * 0.09f * cos(a), bc.y + r.height * 0.13f * sin(a)), 0.4f * u, StrokeCap.Round) }
        drawCircle(lam(mez(base, Color.Black, 0.3f), o, f), 0.9f * u, bc)
        drawCircle(luzMueble(if (o) 0.06f else 0.4f), 0.35f * u, Offset(bc.x - 0.25f * u, bc.y - 0.25f * u))
        listOf(Offset(r.left + b * 0.9f, r.top + b * 0.6f), Offset(r.right - b * 0.9f, r.top + b * 0.6f), Offset(r.left + b * 0.6f, r.bottom - b * 0.3f), Offset(r.right - b * 0.6f, r.bottom - b * 0.3f)).forEachIndexed { k, q ->
            val s = if (q.x < r.center.x) -1f else 1f
            val borla = lam(if (k % 2 == 0) AZAFRAN else mez(base, Color.White, 0.3f), o, f)
            drawCircle(borla, 0.7f * u, q)
            for (j in 0 until 4) drawLine(borla, q, Offset(q.x + s * (1.2f + 0.3f * j) * u, q.y + (0.6f + 0.5f * j) * u), 0.25f * u, StrokeCap.Round)
        }
    }

    /** El pandero en el piso, recostado: el aro de madera con sus pares de sonajas de latón, el parche de piel y dos cintas. */
    private fun DrawScope.pandero(c: Offset, r: Float, o: Boolean, u: Float) {
        sombraContacto(Offset(c.x + 0.8f * u, c.y + r * 0.42f), r * 2.3f, r * 0.5f, if (o) 0.45f else 0.3f)
        scale(1f, 0.5f, c) {
            drawCircle(n(Color(0xFF9A6B42), o), r, c)
            drawCircle(n(Color(0xFFF2E6CC), o), r * 0.86f, c)
            drawCircle(n(Color(0xFFD8C6A0), o), r * 0.4f, Offset(c.x + r * 0.15f, c.y + r * 0.1f))
            drawCircle(n(Color(0xFF6A4528), o), r, c, style = Stroke(0.4f * u))
            for (k in 0 until 5) {
                val a = 2f * PI.toFloat() * k / 5f + 0.4f
                val q = Offset(c.x + r * 0.93f * cos(a), c.y + r * 0.93f * sin(a))
                drawCircle(n(LATON_OSC, o), r * 0.15f, q)
                drawCircle(n(LATON_LUZ, o), r * 0.11f, q)
            }
        }
        listOf(MAGENTA to 0f, AMARILLO to 1f).forEach { (col, k) ->
            drawPath(Path().apply { moveTo(c.x + r * 0.9f, c.y); cubicTo(c.x + r * 1.3f, c.y + r * 0.2f, c.x + r * 1.1f, c.y + r * (0.5f + 0.2f * k), c.x + r * (1.6f + 0.3f * k), c.y + r * (0.55f + 0.15f * k)) },
                n(col, o), style = Stroke(0.55f * u, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.MAPAS))
        val suelo = h * 0.93f
        val tab = Rect(w * 0.015f, h * 0.03f, w * 0.985f, h * 0.725f)
        val canto = Rect(w * 0.008f, tab.bottom - 0.4f * u, w * 0.992f, h * 0.79f)

        // La pared, el zócalo y la duela con sus gotas de tinta.
        pared(Rect(0f, 0f, w, suelo), o, u, e.semilla + 4)
        if (o) halo(Offset(w * 0.3f, -h * 0.1f), w * 0.75f, FVIOLETA, 0.7f, 22)
        zocalo(Rect(0f, suelo - h * 0.08f, w, suelo), o, u)
        duela(Rect(0f, suelo, w, h), o, u, e.semilla, tinta = true)

        // Las patas de caballete, el travesaño y el faldón con las mariposas de latón que fijan la inclinación.
        val faldon = Rect(w * 0.05f, canto.bottom, w * 0.95f, canto.bottom + h * 0.04f)
        listOf(w * 0.1f to -1f, w * 0.9f to 1f).forEach { (x, s) ->
            val pata = Path().apply {
                moveTo(x - 1.6f * u, faldon.bottom); lineTo(x + 1.6f * u, faldon.bottom)
                lineTo(x + 1.6f * u + s * 2.6f * u, suelo + h * 0.04f); lineTo(x - 1.6f * u + s * 2.6f * u, suelo + h * 0.04f); close()
            }
            sombraContacto(Offset(x + s * 2.6f * u + 1f * u, suelo + h * 0.04f), 7f * u, 1.4f * u, if (o) 0.5f else 0.32f)
            drawPath(pata, n(PINO, o))
            drawPath(Path().apply { moveTo(x + 0.6f * u, faldon.bottom); lineTo(x + 1.6f * u, faldon.bottom); lineTo(x + 1.6f * u + s * 2.6f * u, suelo + h * 0.04f); lineTo(x + 0.6f * u + s * 2.6f * u, suelo + h * 0.04f); close() },
                sombraMueble(if (o) 0.4f else 0.28f))
            drawLine(luzMueble(if (o) 0.05f else 0.5f), Offset(x - 1.4f * u, faldon.bottom), Offset(x - 1.4f * u + s * 2.6f * u, suelo + h * 0.04f), 0.35f * u)
        }
        tabla(Rect(w * 0.1f, h * 0.86f, w * 0.9f, h * 0.86f + 2.2f * u), o, u, e.semilla + 30)
        // Debajo de la mesa: los carteles enrollados, el atado de pliegos y la papelera con los bocetos arrugados.
        rollos(Offset(w * 0.17f, suelo + 1.6f * u), h * 0.17f, o, u)
        atado(Rect(w * 0.4f, suelo - h * 0.055f, w * 0.56f, suelo + 1.8f * u), o, u, e.semilla)
        papelera(Offset(w * 0.72f, suelo + 2.2f * u), h * 0.12f, o, u, e.semilla)
        sombraEnPared(faldon, 1.2f * u, if (o) 0.4f else 0.25f)
        tabla(faldon, o, u, e.semilla + 31)
        listOf(faldon.left + 3f * u, faldon.right - 3f * u).forEach { x ->
            val c = Offset(x, faldon.center.y)
            drawCircle(sombraMueble(0.3f), 1.2f * u, Offset(c.x + 0.3f * u, c.y + 0.4f * u))
            drawOval(n(LATON, o), Offset(c.x - 2f * u, c.y - 0.7f * u), Size(4f * u, 1.4f * u))
            drawCircle(n(LATON_OSC, o), 0.7f * u, c)
            drawCircle(n(LATON_LUZ, o), 0.3f * u, Offset(c.x - 0.25f * u, c.y - 0.25f * u))
        }

        // El tablero de pino: el canto oscuro, la cara con su veta y el bisel de luz.
        sombraEnPared(tab, 2f * u, if (o) 0.45f else 0.28f, 0.6f * u)
        drawRoundRect(n(PINO_OSC, o), tab.topLeft, tab.size, CornerRadius(0.6f * u))
        val cara = tab.deflate(1.1f * u)
        pino(cara, o, u, e.semilla + 2, mez(PINO, PINO_LUZ, 0.45f))
        drawRect(luzMueble(if (o) 0.05f else 0.5f), cara.topLeft, Size(cara.width, 0.35f * u))
        drawRect(luzMueble(if (o) 0.04f else 0.35f), cara.topLeft, Size(0.35f * u, cara.height))
        // Marcas del trabajo: cortes de cúter y manchas de tinta en la madera.
        for (k in 0 until 7) {
            val q = Offset(cara.left + cara.width * azar(e.semilla, k + 500), cara.top + cara.height * azar(e.semilla, k + 510))
            if (ni.inflate(3f * u).contains(q)) continue
            if (k % 2 == 0) drawLine(n(PINO_OSC, o).copy(alpha = 0.5f), q, Offset(q.x + (2f + 3f * azar(e.semilla, k + 520)) * u, q.y + 0.4f * u), 0.15f * u)
            else drawCircle(fl(listOf(MAGENTA, VERDE, NARANJA, AZUL)[k % 4], o).copy(alpha = if (o) 0.55f else 0.35f), (0.4f + 0.5f * azar(e.semilla, k + 530)) * u, q)
        }

        // El pliego sujeto con cinta de papel: el nicho.
        val pliego = ni.inflate(1.4f * u)
        sombraEnPared(pliego, 1f * u, if (o) 0.4f else 0.22f)
        rect(pliego, blanco(PAPEL_CLARO, o).let { if (o) mez(it, e.fondo, 0.5f) else it })
        rect(ni, nicho(e, o, PAPEL_CLARO))
        fueraDe(ni) {
            listOf(Offset(pliego.left, pliego.top) to -45f, Offset(pliego.right, pliego.top) to 45f, Offset(pliego.left, pliego.bottom) to 45f, Offset(pliego.right, pliego.bottom) to -45f).forEach { (c, g) ->
                cinta(c, 6f * u, 2f * u, g, o, u)
            }
        }

        // A la izquierda: el cartel de la semana a medio entintar y la tarjeta de los pares que vibran.
        val cart = Rect(w * 0.035f, h * 0.075f, w * 0.2f, h * 0.075f + w * 0.165f / 0.66f)
        cartelSemana(cart, o, u, 0.35f)
        cinta(Offset(cart.left + 1.5f * u, cart.top + 0.6f * u), 4.4f * u, 1.6f * u, -40f, o, u)
        cinta(Offset(cart.right - 1.5f * u, cart.top + 0.6f * u), 4.4f * u, 1.6f * u, 40f, o, u)
        if (o) haloRect(cart, 4f * u, LIMON, 0.5f)
        tarjetaPares(Rect(w * 0.098f, h * 0.505f, w * 0.198f, h * 0.665f), o, u)

        // A la derecha: los bocetos de la flor y la prueba de la tinta de arcoíris.
        bocetos(Rect(w * 0.8f, h * 0.075f, w * 0.965f, h * 0.36f), o, u, e.semilla)
        tiraArcoiris(Rect(w * 0.795f, h * 0.4f, w * 0.97f, h * 0.49f), o, u, e.semilla)

        // El canto con su canal: lo que descansa en él, y el labio de adelante que lo tapa a medias.
        val labio = canto.top + canto.height * 0.45f
        rect(canto, n(PINO_OSC, o))
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), canto.topLeft, Size(canto.width, 0.8f * u))
        lataPinceles(Offset(w * 0.06f, labio + 0.6f * u), h * 0.13f, o, u)
        paleta(Rect(w * 0.255f, labio - 1.8f * u, w * 0.37f, labio + 0.4f * u), o, u)
        lapiz(Offset(w * 0.43f, labio - 0.7f * u), w * 0.13f, -3f, o, u, AMARILLO)
        plumilla(Offset(w * 0.575f, labio - 0.9f * u), w * 0.13f, 2f, o, u)
        goma(Offset(w * 0.7f, labio - 0.7f * u), o, u)
        rolloCinta(Offset(w * 0.765f, labio - 2.6f * u), 2.8f * u, o, u)
        listOf(MAGENTA, VERDE, NARANJA, AZUL).forEachIndexed { k, col -> frasco(Offset(w * (0.82f + 0.042f * k), labio + 0.5f * u), h * (0.115f + 0.012f * (k % 2)), col, o, u) }
        val lab = Rect(canto.left, labio, canto.right, canto.bottom)
        tabla(lab, o, u, e.semilla + 40)
        drawRect(sombraMueble(if (o) 0.5f else 0.35f), Offset(canto.left, canto.bottom), Size(canto.width, 1f * u))

        granoMueble(Rect(0f, 0f, w, h), o, 0.7f)
    }

    /** La tarjeta de los pares que vibran (la lección de Albers que tomaron los carteles): tres cuadros de un color con un círculo de su complementario. */
    private fun DrawScope.tarjetaPares(r: Rect, o: Boolean, u: Float) {
        sombraEnPared(r, 0.9f * u, if (o) 0.4f else 0.22f)
        rect(r, blanco(PAPEL_CLARO, o))
        val pares = listOf(MAGENTA to VERDE, NARANJA to AZUL, AMARILLO to VIOLETA)
        val lado = minOf((r.height - 2.4f * u) / 3f, r.width * 0.42f)
        pares.forEachIndexed { k, (a, c) ->
            val y = r.top + 1.2f * u + k * lado
            listOf(0, 1).forEach { j ->
                val q = Rect(r.left + 1.2f * u + j * (lado + 0.6f * u), y + 0.3f * u, r.left + 1.2f * u + j * (lado + 0.6f * u) + lado, y + lado - 0.3f * u)
                rect(q, fl(if (j == 0) a else c, o))
                drawCircle(fl(if (j == 0) c else a, o), q.height * 0.24f, q.center)
            }
        }
        cinta(Offset(r.center.x, r.top + 0.3f * u), 4.4f * u, 1.5f * u, 4f, o, u)
        if (o) haloRect(r, 2.5f * u, FMAGENTA, 0.4f)
    }

    /** La hoja de bocetos: cuatro flores chicas a lápiz para el cartel que sigue, una ya probada en color, y unas letras tanteadas. */
    private fun DrawScope.bocetos(r: Rect, o: Boolean, u: Float, semilla: Int) {
        rotate(-3f, r.center) {
            sombraEnPared(r, 1f * u, if (o) 0.4f else 0.2f)
            rect(r, blanco(PAPEL, o))
            val lapiz = (if (o) mez(LAPIZ, FVIOLETA, 0.3f) else LAPIZ).copy(alpha = if (o) 0.6f else 0.85f)
            val cw = r.width / 2f; val ch = r.height * 0.36f
            for (k in 0 until 4) {
                val c = Offset(r.left + cw * (k % 2 + 0.5f), r.top + ch * (k / 2 + 0.55f))
                val rr = minOf(cw, ch) * 0.34f
                drawRect(lapiz, Offset(c.x - cw * 0.42f, c.y - ch * 0.45f), Size(cw * 0.84f, ch * 0.9f), style = Stroke(0.12f * u))
                if (k == 1) with(TrazosPsicodelia) { flor(c, rr, fl(MAGENTA, o), fl(VERDE, o), 6, 0.6f) }
                else drawPath(TrazosPsicodelia.petalos(c, rr, 5 + k, k * 0.3f, 0.3f, 0.12f), lapiz, style = Stroke(0.15f * u))
                drawCircle(lapiz, rr * 0.25f, c, style = Stroke(0.15f * u))
            }
            // Las letras tanteadas: «BAILE» a lápiz, en una línea que se hincha.
            palabraBaile(Rect(r.left + r.width * 0.1f, r.top + r.height * 0.76f, r.right - r.width * 0.1f, r.top + r.height * 0.94f), 0.06f).forEach { drawPath(it, lapiz, style = Stroke(0.15f * u)) }
            cinta(Offset(r.center.x, r.top), 5f * u, 1.6f * u, -2f, o, u)
        }
    }

    /** La prueba del tintero partido: una tira de papel con el arcoíris que deja la prensa en una sola pasada, con los bordes donde la tinta se acaba. */
    private fun DrawScope.tiraArcoiris(r: Rect, o: Boolean, u: Float, semilla: Int) {
        rotate(2f, r.center) {
            sombraEnPared(r, 0.9f * u, if (o) 0.4f else 0.2f)
            rect(r, blanco(PAPEL, o))
            val t = r.deflate(1f * u)
            if (o) haloRect(t, 3f * u, FMAGENTA, 0.5f)
            drawRect(TrazosPsicodelia.arcoiris(Offset(t.left, t.top), Offset(t.right, t.top), if (o) 1f else 0.95f, o), t.topLeft, t.size)
            // Los bordes donde la tinta se acaba: la orilla del rodillo, desigual.
            for (k in 0 until 14) {
                val x = t.left + t.width * k / 13f
                drawCircle(blanco(PAPEL, o), (0.25f + 0.3f * azar(semilla, k + 600)) * u, Offset(x, t.top + 0.1f * u))
                drawCircle(blanco(PAPEL, o), (0.25f + 0.3f * azar(semilla, k + 620)) * u, Offset(x, t.bottom - 0.1f * u))
            }
            cinta(Offset(r.left + 0.8f * u, r.center.y), 3.2f * u, 1.4f * u, 80f, o, u)
        }
    }

    /** La lata de café que guarda los pinceles: el cilindro de lámina con su borde, y los pinceles de mango de colores que asoman. */
    private fun DrawScope.lataPinceles(base: Offset, alto: Float, o: Boolean, u: Float) {
        val an = alto * 0.38f
        val lata = Rect(base.x - an / 2, base.y - alto * 0.42f, base.x + an / 2, base.y)
        val mangos = listOf(Color(0xFFB0244A), Color(0xFF2E6B8E), Color(0xFFD9A12E), Color(0xFF3F7A4A), Color(0xFF8C5A32))
        mangos.forEachIndexed { k, col ->
            val g = -14f + k * 7f
            rotate(g, Offset(base.x, lata.top + 1f * u)) {
                val largo = alto * (0.82f + 0.15f * ((k * 7) % 3))
                val x = base.x + (k - 2) * 0.7f * u
                drawLine(n(col, o), Offset(x, lata.top + 1f * u), Offset(x, base.y - largo), 0.7f * u, StrokeCap.Round)
                drawLine(n(LAMINA, o), Offset(x, base.y - largo + alto * 0.06f), Offset(x, base.y - largo - alto * 0.04f), 0.75f * u)
                val punta = Path().apply { moveTo(x - 0.45f * u, base.y - largo - alto * 0.04f); quadraticTo(x - 0.4f * u, base.y - largo - alto * 0.14f, x, base.y - largo - alto * 0.17f); quadraticTo(x + 0.4f * u, base.y - largo - alto * 0.14f, x + 0.45f * u, base.y - largo - alto * 0.04f); close() }
                drawPath(punta, n(Color(0xFF3A2A22), o))
                if (k % 2 == 0) clipPath(punta) { drawRect(fl(listOf(MAGENTA, VERDE, NARANJA)[k / 2], o), Offset(x - u, base.y - largo - alto * 0.2f), Size(2f * u, alto * 0.06f)) }
            }
        }
        sombraContacto(Offset(base.x + 0.8f * u, base.y), an * 1.3f, 1f * u, if (o) 0.5f else 0.3f)
        rect(lata, n(LAMINA, o))
        drawRect(luzMueble(if (o) 0.05f else 0.55f), Offset(lata.left + an * 0.15f, lata.top), Size(an * 0.14f, lata.height))
        drawRect(sombraMueble(if (o) 0.35f else 0.3f), Offset(lata.right - an * 0.3f, lata.top), Size(an * 0.3f, lata.height))
        for (k in 1..3) drawLine(n(LAMINA_OSC, o), Offset(lata.left, lata.top + lata.height * k / 4f), Offset(lata.right, lata.top + lata.height * k / 4f), 0.2f * u)
        drawOval(n(LAMINA_OSC, o), Offset(lata.left, lata.top - 0.5f * u), Size(an, 1f * u))
        // El chorreado de tinta en la lata.
        drawPath(Path().apply { moveTo(lata.left + an * 0.55f, lata.top); lineTo(lata.left + an * 0.7f, lata.top); lineTo(lata.left + an * 0.68f, lata.top + lata.height * 0.5f); quadraticTo(lata.left + an * 0.62f, lata.top + lata.height * 0.58f, lata.left + an * 0.58f, lata.top + lata.height * 0.48f); close() },
            fl(MAGENTA, o))
    }

    /** La paleta de porcelana acostada en el canal: la losa blanca con sus pocitos de tinta, vista casi de canto. */
    private fun DrawScope.paleta(r: Rect, o: Boolean, u: Float) {
        drawRoundRect(sombraMueble(0.3f), Offset(r.left + 0.6f * u, r.top + 0.5f * u), r.size, CornerRadius(0.6f * u))
        drawRoundRect(blanco(Color(0xFFF6F3EC), o), r.topLeft, r.size, CornerRadius(0.6f * u))
        listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL, VIOLETA).forEachIndexed { k, col ->
            val c = Offset(r.left + r.width * (k + 0.5f) / 6f, r.top + r.height * 0.36f)
            drawOval(fl(col, o), Offset(c.x - r.width * 0.06f, c.y - r.height * 0.18f), Size(r.width * 0.12f, r.height * 0.36f))
            drawOval(Color.White.copy(alpha = if (o) 0.15f else 0.5f), Offset(c.x - r.width * 0.03f, c.y - r.height * 0.14f), Size(r.width * 0.04f, r.height * 0.1f))
        }
        drawRect(sombraMueble(if (o) 0.25f else 0.18f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
    }

    /** Un lápiz amarillo de seis caras, acostado: la madera tajada, la mina y el casquillo con su goma. */
    private fun DrawScope.lapiz(c: Offset, largo: Float, grados: Float, o: Boolean, u: Float, color: Color) {
        rotate(grados, c) {
            val a = 0.9f * u
            val cuerpo = Rect(c.x - largo / 2 + a * 2.2f, c.y - a / 2, c.x + largo / 2 - a * 1.6f, c.y + a / 2)
            drawRect(sombraMueble(0.3f), Offset(cuerpo.left + 0.4f * u, cuerpo.top + 0.5f * u), Size(cuerpo.width + a * 2f, cuerpo.height))
            rect(cuerpo, n(color, o))
            drawRect(luzMueble(if (o) 0.05f else 0.5f), cuerpo.topLeft, Size(cuerpo.width, a * 0.3f))
            drawRect(sombraMueble(if (o) 0.3f else 0.22f), Offset(cuerpo.left, cuerpo.bottom - a * 0.3f), Size(cuerpo.width, a * 0.3f))
            drawPath(Path().apply { moveTo(cuerpo.left, cuerpo.top); lineTo(cuerpo.left - a * 2f, c.y); lineTo(cuerpo.left, cuerpo.bottom); close() }, n(Color(0xFFE7C38E), o))
            drawPath(Path().apply { moveTo(cuerpo.left - a * 1.3f, c.y - a * 0.17f); lineTo(cuerpo.left - a * 2.2f, c.y); lineTo(cuerpo.left - a * 1.3f, c.y + a * 0.17f); close() }, n(Color(0xFF3A3640), o))
            rect(Rect(cuerpo.right, cuerpo.top, cuerpo.right + a * 0.8f, cuerpo.bottom), n(LAMINA, o))
            drawRoundRect(n(Color(0xFFE88A9A), o), Offset(cuerpo.right + a * 0.8f, cuerpo.top), Size(a * 0.8f, a), CornerRadius(a * 0.3f))
        }
    }

    /** La plumilla de entintar: el mango de madera, el casquillo y la plumilla de acero con su tinta en la punta. */
    private fun DrawScope.plumilla(c: Offset, largo: Float, grados: Float, o: Boolean, u: Float) {
        rotate(grados, c) {
            val a = 1f * u
            drawLine(sombraMueble(0.3f), Offset(c.x - largo / 2 + 0.4f * u, c.y + 0.6f * u), Offset(c.x + largo / 2 + 0.4f * u, c.y + 0.6f * u), a)
            drawLine(n(Color(0xFF2A1E1A), o), Offset(c.x - largo * 0.2f, c.y), Offset(c.x + largo / 2, c.y), a, StrokeCap.Round)
            drawLine(luzMueble(if (o) 0.05f else 0.3f), Offset(c.x - largo * 0.18f, c.y - a * 0.25f), Offset(c.x + largo * 0.48f, c.y - a * 0.25f), a * 0.25f)
            rect(Rect(c.x - largo * 0.3f, c.y - a * 0.45f, c.x - largo * 0.2f, c.y + a * 0.45f), n(LAMINA, o))
            drawPath(Path().apply { moveTo(c.x - largo * 0.3f, c.y - a * 0.4f); lineTo(c.x - largo / 2, c.y); lineTo(c.x - largo * 0.3f, c.y + a * 0.4f); close() }, n(LAMINA_OSC, o))
            drawCircle(fl(VERDE, o), a * 0.35f, Offset(c.x - largo * 0.46f, c.y))
        }
    }

    /** La goma de migajón gris, amasada, con las huellas de los dedos. */
    private fun DrawScope.goma(c: Offset, o: Boolean, u: Float) {
        drawPath(TrazosPsicodelia.gota(Offset(c.x + 0.3f * u, c.y + 0.4f * u), 1.6f * u, 0.3f, 0.14f), sombraMueble(0.3f))
        drawPath(TrazosPsicodelia.gota(c, 1.6f * u, 0.3f, 0.14f), n(Color(0xFF9A9AA4), o))
        drawCircle(n(Color(0xFFB8B8C2), o), 0.6f * u, Offset(c.x - 0.4f * u, c.y - 0.4f * u))
    }

    /** El rollo de cinta de papel, de canto: el anillo color crema y su hueco de cartón. */
    private fun DrawScope.rolloCinta(c: Offset, r: Float, o: Boolean, u: Float) {
        drawCircle(sombraMueble(0.3f), r, Offset(c.x + 0.5f * u, c.y + 0.6f * u))
        drawCircle(n(CINTA, o), r, c)
        for (k in 1..3) drawCircle(n(mez(CINTA, PINO_OSC, 0.3f), o), r * (0.62f + 0.12f * k), c, style = Stroke(0.12f * u))
        drawCircle(n(Color(0xFFB8956A), o), r * 0.6f, c)
        drawCircle(n(PINO_OSC, o), r * 0.48f, c)
        drawArc(luzMueble(if (o) 0.06f else 0.55f), 190f, 70f, false, Offset(c.x - r * 0.88f, c.y - r * 0.88f), Size(r * 1.76f, r * 1.76f), style = Stroke(0.35f * u))
    }

    /** Un frasco de tinta de cartel, chato y de boca ancha: el vidrio, la tinta hasta la mitad, la tapa negra y su etiqueta con la flor. */
    private fun DrawScope.frasco(base: Offset, alto: Float, tinta: Color, o: Boolean, u: Float) {
        val an = alto * 0.62f
        val cuerpo = Rect(base.x - an / 2, base.y - alto * 0.78f, base.x + an / 2, base.y)
        if (o) halo(Offset(base.x, base.y - alto * 0.3f), alto * 0.9f, fl(tinta, true), 0.45f, 10)
        sombraContacto(Offset(base.x + 0.6f * u, base.y), an * 1.3f, 1f * u, if (o) 0.5f else 0.3f)
        drawRoundRect(if (o) mez(n(Color(0xFFDDE6E8), o), FVIOLETA, 0.1f) else Color(0xFFE2ECEE), cuerpo.topLeft, cuerpo.size, CornerRadius(an * 0.18f))
        val nivel = Rect(cuerpo.left + 0.3f * u, cuerpo.top + cuerpo.height * 0.35f, cuerpo.right - 0.3f * u, cuerpo.bottom - 0.3f * u)
        drawRoundRect(fl(tinta, o).let { if (o) it else mez(it, Color.Black, 0.1f) }, nivel.topLeft, nivel.size, CornerRadius(an * 0.14f))
        // La etiqueta con la flor.
        val et = Rect(cuerpo.left + an * 0.12f, cuerpo.top + cuerpo.height * 0.42f, cuerpo.right - an * 0.12f, cuerpo.top + cuerpo.height * 0.82f)
        rect(et, blanco(PAPEL, o))
        with(TrazosPsicodelia) { margarita(et.center, et.height * 0.36f, fl(tinta, o), blanco(PAPEL, o), 6) }
        // El brillo del vidrio, la tapa y el cuello.
        drawRect(Color.White.copy(alpha = if (o) 0.12f else 0.55f), Offset(cuerpo.left + an * 0.1f, cuerpo.top + 0.4f * u), Size(an * 0.1f, cuerpo.height * 0.8f))
        drawRect(sombraMueble(if (o) 0.25f else 0.18f), Offset(cuerpo.right - an * 0.14f, cuerpo.top), Size(an * 0.14f, cuerpo.height))
        val tapa = Rect(base.x - an * 0.4f, base.y - alto, base.x + an * 0.4f, cuerpo.top + 0.2f * u)
        drawRoundRect(n(Color(0xFF221C24), o), tapa.topLeft, tapa.size, CornerRadius(0.4f * u))
        for (k in 1..4) drawLine(n(Color(0xFF4A4250), o), Offset(tapa.left + tapa.width * k / 5f, tapa.top), Offset(tapa.left + tapa.width * k / 5f, tapa.bottom), 0.15f * u)
        drawRect(luzMueble(if (o) 0.05f else 0.3f), Offset(tapa.left, tapa.top), Size(tapa.width, 0.3f * u))
    }

    /** Los carteles enrollados con su liga, apoyados en la pata: tres tubos de papel con el borde impreso que asoma. */
    private fun DrawScope.rollos(base: Offset, alto: Float, o: Boolean, u: Float) {
        listOf(-14f to PAPEL_CLARO, -3f to PAPEL, 9f to Color(0xFFF2E2C8)).forEachIndexed { k, (g, col) ->
            rotate(g, base) {
                val r = Rect(base.x - 1.8f * u + k * 3.4f * u, base.y - alto * (0.9f + 0.1f * k), base.x + 1.8f * u + k * 3.4f * u, base.y)
                drawRect(sombraMueble(0.25f), Offset(r.left + 0.6f * u, r.top + 0.6f * u), r.size)
                rect(r, blanco(col, o))
                drawRect(fl(listOf(MAGENTA, NARANJA, VERDE)[k], o), Offset(r.left, r.top), Size(r.width * 0.3f, r.height))
                drawLine(fl(listOf(AZUL, MAGENTA, NARANJA)[k], o), Offset(r.left + r.width * 0.3f, r.top), Offset(r.left + r.width * 0.3f, r.bottom), 0.3f * u)
                drawRect(luzMueble(if (o) 0.04f else 0.4f), Offset(r.left + r.width * 0.55f, r.top), Size(r.width * 0.12f, r.height))
                drawRect(sombraMueble(if (o) 0.3f else 0.2f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
                drawOval(n(mez(col, PINO_OSC, 0.3f), o), Offset(r.left, r.top - 0.6f * u), Size(r.width, 1.2f * u))
                drawOval(n(PINO_OSC, o), Offset(r.left + r.width * 0.3f, r.top - 0.3f * u), Size(r.width * 0.4f, 0.6f * u))
                drawLine(n(Color(0xFFB0244A), o), Offset(r.left, r.top + r.height * 0.4f), Offset(r.right, r.top + r.height * 0.42f), 0.35f * u)
            }
        }
    }

    /** El atado de pliegos en blanco, con su cordel. */
    private fun DrawScope.atado(r: Rect, o: Boolean, u: Float, semilla: Int) {
        sombraContacto(Offset(r.center.x + 1f * u, r.bottom), r.width * 1.1f, 1.4f * u, if (o) 0.5f else 0.3f)
        var y = r.bottom; var k = 0
        while (y > r.top && k < 20) {
            val hoja = Rect(r.left + (azar(semilla, k + 700) - 0.5f) * 1.2f * u, y - 0.8f * u, r.right + (azar(semilla, k + 710) - 0.5f) * 1.2f * u, y)
            rect(hoja, blanco(if (k % 3 == 0) PAPEL else PAPEL_CLARO, o))
            drawLine(n(PARED_SOMBRA, o), Offset(hoja.left, hoja.bottom), Offset(hoja.right, hoja.bottom), 0.12f * u)
            y -= 0.8f * u; k++
        }
        drawRect(sombraMueble(if (o) 0.3f else 0.16f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
        drawLine(n(Color(0xFFB08A5A), o), Offset(r.center.x, r.top - 0.2f * u), Offset(r.center.x, r.bottom), 0.4f * u)
        drawOval(n(Color(0xFFB08A5A), o), Offset(r.center.x - 1.2f * u, r.top - 1.2f * u), Size(2.4f * u, 1.2f * u), style = Stroke(0.35f * u))
    }

    /** La papelera de mimbre con los bocetos arrugados, uno caído al lado. */
    private fun DrawScope.papelera(base: Offset, alto: Float, o: Boolean, u: Float, semilla: Int) {
        val an = alto * 0.9f
        val cesta = Path().apply { moveTo(base.x - an / 2, base.y - alto); lineTo(base.x + an / 2, base.y - alto); lineTo(base.x + an * 0.38f, base.y); lineTo(base.x - an * 0.38f, base.y); close() }
        sombraContacto(Offset(base.x + 1f * u, base.y), an * 1.1f, 1.6f * u, if (o) 0.5f else 0.3f)
        // Los papeles que asoman.
        for (k in 0 until 4) {
            val c = Offset(base.x + (k - 1.5f) * an * 0.22f, base.y - alto - an * (0.06f + 0.06f * (k % 2)))
            papelArrugado(c, an * 0.17f, o, u, semilla + k)
        }
        drawPath(cesta, n(Color(0xFFC9A46A), o))
        clipPath(cesta) {
            var y = base.y - alto; var k = 0
            while (y < base.y && k < 40) { drawLine(n(Color(0xFF8C6A3A), o).copy(alpha = 0.6f), Offset(base.x - an, y), Offset(base.x + an, y), 0.25f * u); y += 1.1f * u; k++ }
            for (j in -6..6) drawLine(n(Color(0xFF8C6A3A), o).copy(alpha = 0.5f), Offset(base.x + j * an * 0.08f, base.y - alto), Offset(base.x + j * an * 0.06f, base.y), 0.3f * u)
            drawRect(sombraMueble(if (o) 0.35f else 0.25f), Offset(base.x + an * 0.15f, base.y - alto), Size(an, alto))
            drawRect(luzMueble(if (o) 0.04f else 0.3f), Offset(base.x - an / 2, base.y - alto), Size(an * 0.2f, alto))
        }
        drawRoundRect(n(Color(0xFFB08A5A), o), Offset(base.x - an / 2 - 0.3f * u, base.y - alto - 0.5f * u), Size(an + 0.6f * u, 1f * u), CornerRadius(0.5f * u))
        papelArrugado(Offset(base.x + an * 0.85f, base.y - an * 0.12f), an * 0.15f, o, u, semilla + 9)
    }

    /** Un boceto arrugado hecho bola, con sus pliegues y un rastro de tinta. */
    private fun DrawScope.papelArrugado(c: Offset, r: Float, o: Boolean, u: Float, semilla: Int) {
        val bola = TrazosPsicodelia.gota(c, r, azar(semilla, 1), 0.2f, 5, 20)
        drawPath(bola, blanco(PAPEL_CLARO, o))
        clipPath(bola) {
            drawCircle(sombraMueble(if (o) 0.3f else 0.2f), r, Offset(c.x + r * 0.5f, c.y + r * 0.4f))
            for (k in 0 until 4) {
                val a = 2f * PI.toFloat() * azar(semilla, k + 3)
                drawLine(n(PARED_SOMBRA, o), Offset(c.x - r * 0.6f * cos(a), c.y - r * 0.6f * sin(a)), Offset(c.x + r * 0.7f * cos(a + 0.4f), c.y + r * 0.7f * sin(a + 0.4f)), 0.15f * u)
            }
            drawArc(fl(MAGENTA, o), 30f, 120f, false, Offset(c.x - r * 0.4f, c.y - r * 0.2f), Size(r * 0.8f, r * 0.6f), style = Stroke(0.3f * u))
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.OTRAS_VIDAS))
        val suelo = h * 0.95f
        val borde = 2.6f * u
        val cabecera = 1.15f * u
        val arriba = ni.top - borde

        // La pared, el zócalo y la duela; de noche, el violeta del tubo desde arriba.
        pared(Rect(0f, 0f, w, suelo), o, u, e.semilla + 8)
        huellaCartel(Rect(w * 0.6f, h * 0.02f, w * 0.7f, h * 0.18f), o, u)
        if (o) halo(Offset(w * 0.45f, -h * 0.2f), w * 0.7f, FVIOLETA, 0.75f, 22)
        zocalo(Rect(0f, suelo - h * 0.09f, w, suelo), o, u)
        duela(Rect(0f, suelo, w, h), o, u, e.semilla + 2)

        // La tabla de abajo sobre sus pilas de ladrillos.
        val base = Rect(w * 0.025f, ni.bottom + borde, w * 0.975f, ni.bottom + borde + 2.6f * u)
        listOf(0.055f, 0.465f, 0.875f).forEachIndexed { k, x ->
            val pila = Rect(w * x, base.bottom, w * (x + 0.07f), suelo + 0.6f * u)
            sombraEnPared(pila, 1.2f * u, if (o) 0.4f else 0.25f)
            ladrillos(pila, o, u, e.semilla + k * 11, (pila.height / 4f))
            sombraContacto(Offset(pila.center.x + 1f * u, suelo + 0.6f * u), pila.width * 1.2f, 1.2f * u, if (o) 0.5f else 0.3f)
        }
        drawRect(sombraMueble(if (o) 0.45f else 0.3f), Offset(base.left, base.bottom), Size(base.width, 1.4f * u))
        tabla(base, o, u, e.semilla + 50)

        // Las cinco cajas de fruta de pie, con la boca hacia delante: cada una, un compartimento.
        val fila = Rect(ni.left, arriba, ni.right, ni.bottom + borde)
        sombraEnPared(fila, 2f * u, if (o) 0.45f else 0.28f)
        val cw = ni.width / 5f
        for (k in 0 until 5) {
            val x0 = ni.left + k * cw; val x1 = x0 + cw
            val vieja = if (k == 1 || k == 4) PINO_VIEJO else PINO
            val hueco = Rect(x0 + cabecera, ni.top, x1 - cabecera, ni.bottom)
            // El fondo de la caja: tres tablillas a lo largo, con las rendijas por donde se ve la pared en sombra (el nicho de la medalla).
            rect(hueco, nicho(e, o, if (k == 1 || k == 4) PINO_VIEJO else PINO_LUZ))
            val nt = 3
            for (j in 1 until nt) {
                val x = hueco.left + hueco.width * j / nt
                drawLine(n(PINO_OSC, o).copy(alpha = if (o) 0.7f else 0.55f), Offset(x, hueco.top), Offset(x, hueco.bottom), 0.55f * u)
                drawLine(luzMueble(if (o) 0.03f else 0.35f), Offset(x + 0.5f * u, hueco.top), Offset(x + 0.5f * u, hueco.bottom), 0.2f * u)
            }
            for (j in 0 until nt) {
                val x = hueco.left + hueco.width * (j + 0.5f) / nt
                listOf(hueco.top + 1.4f * u, hueco.bottom - 1.2f * u).forEach { yc -> drawCircle(n(Color(0xFF6A6268), o).copy(alpha = 0.7f), 0.28f * u, Offset(x, yc)) }
            }
            // La sombra que dejan dentro la cabecera de arriba y la tablilla de la izquierda.
            drawRect(sombraMueble(if (o) 0.4f else 0.3f), hueco.topLeft, Size(hueco.width, 1.5f * u))
            drawRect(sombraMueble(if (o) 0.25f else 0.15f), Offset(hueco.left, hueco.top + 1.5f * u), Size(hueco.width, 1.3f * u))
            drawRect(sombraMueble(if (o) 0.3f else 0.2f), hueco.topLeft, Size(1.2f * u, hueco.height))
            // Las tablillas delgadas de los costados, de canto, y las dos cabeceras gruesas, arriba y abajo.
            tabla(Rect(x0, ni.top, x0 + cabecera, ni.bottom), o, u, e.semilla + k * 7 + 2, vieja, vertical = true)
            tabla(Rect(x1 - cabecera, ni.top, x1, ni.bottom), o, u, e.semilla + k * 7 + 3, vieja, vertical = true)
            tabla(Rect(x0, arriba, x1, ni.top), o, u, e.semilla + k * 7, vieja)
            tabla(Rect(x0, ni.bottom, x1, ni.bottom + borde), o, u, e.semilla + k * 7 + 1, vieja)
            // Los clavos que atraviesan los costados hasta las cabeceras.
            listOf(arriba + borde * 0.35f, arriba + borde * 0.7f, ni.bottom + borde * 0.35f, ni.bottom + borde * 0.7f).forEach { y ->
                listOf(x0 + cabecera * 0.5f, x1 - cabecera * 0.5f).forEach { x -> drawCircle(n(Color(0xFF4A4448), o), 0.26f * u, Offset(x, y)) }
            }
            // El sello de la huerta, a fuego, en la cabecera de arriba de alguna caja.
            if (k == 0 || k == 3) {
                val c = Offset(x0 + cw * 0.5f, arriba + borde * 0.5f)
                drawOval(n(NUDO, o).copy(alpha = 0.45f), Offset(c.x - borde * 1.4f, c.y - borde * 0.32f), Size(borde * 2.8f, borde * 0.64f), style = Stroke(0.2f * u))
                drawCircle(n(NUDO, o).copy(alpha = 0.45f), borde * 0.16f, c)
            }
        }
        // La raya de entre caja y caja.
        for (k in 1 until 5) drawLine(sombraMueble(if (o) 0.5f else 0.4f), Offset(ni.left + k * cw, arriba), Offset(ni.left + k * cw, ni.bottom + borde), 0.3f * u)

        // Encima: la caja de los discos, el tocadiscos, los sencillos, la radio y la hiedra.
        cajaDiscos(Rect(w * 0.035f, arriba - h * 0.15f, w * 0.27f, arriba), o, u, e.semilla)
        tocadiscos(Rect(w * 0.3f, arriba - h * 0.3f, w * 0.555f, arriba), o, u)
        sencillos(Offset(w * 0.607f, arriba), w * 0.07f, o, u)
        radio(Rect(w * 0.665f, arriba - h * 0.2f, w * 0.845f, arriba), o, u)
        hiedra(Offset(w * 0.905f, arriba), h * 0.1f, Offset(w * 0.975f, ni.bottom - h * 0.05f), o, u, e.semilla)

        granoMueble(Rect(0f, 0f, w, h), o, 0.7f)
    }

    /**
     * La caja de fruta acostada que guarda los discos: la cabecera de frente con su etiqueta de papel (un huerto al sol con
     * la naranja, inventada y sin marca) y, asomando por arriba, las fundas de los discos de larga duración.
     */
    private fun DrawScope.cajaDiscos(r: Rect, o: Boolean, u: Float, semilla: Int) {
        // Las fundas que asoman, un poco abiertas en abanico.
        val fundas = listOf(0, 1, 2, 3, 4, 5)
        fundas.forEach { k ->
            val ancho = r.width * 0.84f
            val x = r.left + r.width * 0.06f + (k - 2.5f) * 0.5f * u
            val alto = r.height * (1.85f - 0.12f * (k % 3))
            val f = Rect(x, r.bottom - alto, x + ancho, r.bottom - r.height * 0.3f)
            rotate((k - 2.5f) * 1.6f, Offset(f.center.x, r.bottom)) {
                drawRect(sombraMueble(if (o) 0.4f else 0.25f), Offset(f.left + 0.6f * u, f.top + 0.4f * u), f.size)
                funda(f, k + semilla, o, u)
            }
        }
        sombraEnPared(r, 1.2f * u, if (o) 0.4f else 0.25f)
        tabla(r, o, u, semilla + 3, PINO_VIEJO)
        // La etiqueta: el cielo, el sol con sus rayos, las lomas con las hileras del huerto y la naranja con sus hojas.
        val et = Rect(r.left + r.width * 0.1f, r.top + r.height * 0.14f, r.right - r.width * 0.1f, r.bottom - r.height * 0.14f)
        clipRect(et.left, et.top, et.right, et.bottom) {
            rect(et, n(Color(0xFF6FA8D8), o))
            val sol = Offset(et.left + et.width * 0.3f, et.top + et.height * 0.62f)
            for (k in 0 until 12) {
                val a0 = PI.toFloat() + PI.toFloat() * k / 12f
                drawPath(Path().apply { moveTo(sol.x, sol.y); lineTo(sol.x + et.width * cos(a0), sol.y + et.width * sin(a0)); lineTo(sol.x + et.width * cos(a0 + 0.13f), sol.y + et.width * sin(a0 + 0.13f)); close() },
                    n(Color(0xFFF2D27A), o).copy(alpha = 0.8f))
            }
            drawCircle(n(Color(0xFFF2B33A), o), et.height * 0.28f, sol)
            drawPath(Path().apply { moveTo(et.left, et.bottom - et.height * 0.35f); quadraticTo(et.left + et.width * 0.35f, et.bottom - et.height * 0.6f, et.right, et.bottom - et.height * 0.3f); lineTo(et.right, et.bottom); lineTo(et.left, et.bottom); close() },
                n(Color(0xFF4F8A3A), o))
            for (k in 0 until 4) drawLine(n(Color(0xFF2F5A2E), o), Offset(et.left, et.bottom - et.height * (0.08f + 0.07f * k)), Offset(et.right, et.bottom - et.height * (0.04f + 0.07f * k)), 0.3f * u)
            val nar = Offset(et.right - et.width * 0.22f, et.top + et.height * 0.52f)
            drawCircle(n(Color(0xFFF07A1A), o), et.height * 0.26f, nar)
            drawCircle(n(Color(0xFFFFB25A), o), et.height * 0.08f, Offset(nar.x - et.height * 0.08f, nar.y - et.height * 0.08f))
            drawOval(n(HOJA, o), Offset(nar.x - et.height * 0.05f, nar.y - et.height * 0.42f), Size(et.height * 0.32f, et.height * 0.14f))
            // La cinta del rótulo, en blanco, que nadie llenó.
            drawRoundRect(n(CREMA, o), Offset(et.left + et.width * 0.06f, et.top + et.height * 0.08f), Size(et.width * 0.5f, et.height * 0.2f), CornerRadius(et.height * 0.05f))
            drawRoundRect(n(Color(0xFFB0244A), o), Offset(et.left + et.width * 0.06f, et.top + et.height * 0.08f), Size(et.width * 0.5f, et.height * 0.2f), CornerRadius(et.height * 0.05f), style = Stroke(0.25f * u))
        }
        drawRect(n(Color(0xFF7A6248), o), et.topLeft, et.size, style = Stroke(0.25f * u))
        // El papel gastado: una esquina despegada.
        drawPath(Path().apply { moveTo(et.right, et.bottom - 2.4f * u); lineTo(et.right, et.bottom); lineTo(et.right - 2.4f * u, et.bottom); close() }, n(PINO_VIEJO, o))
        drawPath(Path().apply { moveTo(et.right, et.bottom - 2.4f * u); lineTo(et.right - 1.9f * u, et.bottom - 1.9f * u); lineTo(et.right - 2.4f * u, et.bottom); close() }, n(CREMA, o))
        listOf(r.left + r.width * 0.04f, r.right - r.width * 0.04f).forEach { x -> listOf(r.top + r.height * 0.2f, r.bottom - r.height * 0.2f).forEach { y -> drawCircle(n(Color(0xFF4A4448), o), 0.3f * u, Offset(x, y)) } }
    }

    /**
     * La funda de un disco de larga duración, en [r], con una portada inventada según [k]: los anillos de dos colores que
     * vibran, las franjas que ondulan, la flor sobre su complementario o la funda negra con la espiral de tinta fluorescente.
     */
    private fun DrawScope.funda(r: Rect, k: Int, o: Boolean, u: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            when (k % 4) {
                0 -> {
                    rect(r, fl(NARANJA, o).let { if (o) mez(it, SALA, 0.35f) else it })
                    for (j in 6 downTo 1) drawCircle(fl(if (j % 2 == 0) AZUL else NARANJA, o).let { if (o) mez(it, SALA, 0.35f) else it }, r.width * 0.11f * j, Offset(r.center.x, r.top + r.width * 0.5f))
                }
                1 -> {
                    rect(r, blanco(CREMA, o))
                    for (j in 0 until 7) drawPath(TrazosPsicodelia.onda(r.left, r.right, r.top + r.height * 0.12f * j, r.height * 0.03f, r.width * 0.5f, j * 0.1f),
                        n(listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL, VIOLETA, MAGENTA)[j], o), style = Stroke(r.height * 0.06f))
                }
                2 -> {
                    rect(r, n(VERDE, o))
                    with(TrazosPsicodelia) { flor(Offset(r.center.x, r.top + r.width * 0.45f), r.width * 0.3f, n(MAGENTA, o), n(AMARILLO, o), 6, 0.6f) }
                }
                else -> {
                    rect(r, Color(0xFF15101C))
                    val c = Offset(r.center.x, r.top + r.width * 0.45f)
                    drawPath(Path().apply { for (s in 0..60) { val q = s / 60f; val a = q * 14f; val p = Offset(c.x + r.width * 0.42f * q * cos(a), c.y + r.width * 0.42f * q * sin(a)); if (s == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } },
                        if (o) LIMON else mez(LIMON, Color(0xFF15101C), 0.15f), style = Stroke(0.5f * u))
                    if (o) halo(c, r.width * 0.5f, LIMON, 0.4f, 10)
                }
            }
            drawRect(luzMueble(if (o) 0.03f else 0.2f), r.topLeft, Size(r.width * 0.12f, r.height))
            drawRect(sombraMueble(if (o) 0.2f else 0.12f), Offset(r.right - 0.6f * u, r.top), Size(0.6f * u, r.height))
        }
    }

    /**
     * El tocadiscos de maleta (el mismo de la repisa de discos), en [r] con la tapa abierta: la tapa forrada de turquesa con
     * la rejilla crema del altavoz, la caja de dos colores con sus broches de latón, el plato con el disco y su etiqueta, el
     * brazo de cromo y la perilla de las velocidades.
     */
    private fun DrawScope.tocadiscos(r: Rect, o: Boolean, u: Float) {
        val cajaAlto = r.height * 0.3f
        val caja = Rect(r.left, r.bottom - cajaAlto, r.right, r.bottom)
        val plataforma = r.height * 0.13f
        // La tapa abierta, detrás.
        val tapa = Rect(r.left + r.width * 0.04f, r.top, r.right - r.width * 0.04f, caja.top - plataforma + 1f * u)
        sombraEnPared(tapa, 1.4f * u, if (o) 0.4f else 0.25f, 1.6f * u)
        drawRoundRect(n(FORRO, o), tapa.topLeft, tapa.size, CornerRadius(1.6f * u))
        val dentro = tapa.deflate(1.6f * u)
        drawRoundRect(n(FORRO_OSC, o), dentro.topLeft, dentro.size, CornerRadius(1f * u))
        val rej = Rect(dentro.center.x - dentro.width * 0.3f, dentro.top + dentro.height * 0.16f, dentro.center.x + dentro.width * 0.3f, dentro.bottom - dentro.height * 0.22f)
        drawRoundRect(n(CREMA, o), rej.topLeft, rej.size, CornerRadius(rej.height / 2))
        var yy = rej.top + 0.9f * u
        while (yy < rej.bottom - 0.5f * u) { drawLine(n(Color(0xFF9E937A), o), Offset(rej.left + 1.6f * u, yy), Offset(rej.right - 1.6f * u, yy), 0.28f * u); yy += 0.9f * u }
        drawLine(luzMueble(if (o) 0.05f else 0.35f), Offset(tapa.left + 1.6f * u, tapa.top + 0.4f * u), Offset(tapa.right - 1.6f * u, tapa.top + 0.4f * u), 0.4f * u)
        // La cubierta, vista apenas desde arriba, con el plato y el disco.
        val cub = Rect(r.left + 0.4f * u, caja.top - plataforma, r.right - 0.4f * u, caja.top + 0.6f * u)
        drawRect(n(CREMA, o), cub.topLeft, cub.size)
        drawRect(sombraMueble(if (o) 0.25f else 0.12f), cub.topLeft, Size(cub.width, 0.6f * u))
        val pc = Offset(cub.left + cub.width * 0.42f, cub.center.y)
        val pr = cub.width * 0.3f
        scale(1f, 0.34f, pc) {
            drawCircle(sombraMueble(0.35f), pr * 1.04f, Offset(pc.x + 0.6f * u, pc.y + 1.6f * u))
            drawCircle(n(LAMINA_OSC, o), pr * 1.04f, pc)
            with(TrazosPsicodelia) { disco(pc, pr, if (o) n(NARANJA, true) else NARANJA, 0.15f) }
        }
        // El brazo de cromo con su cápsula, y la perilla.
        val pivote = Offset(cub.right - cub.width * 0.14f, cub.top + cub.height * 0.32f)
        drawCircle(n(LAMINA_OSC, o), 1.2f * u, pivote)
        drawCircle(n(LAMINA, o), 0.7f * u, pivote)
        val cap = Offset(pc.x + pr * 0.55f, pc.y + 0.2f * u)
        drawLine(n(LAMINA_OSC, o), pivote, Offset(cap.x + 1.2f * u, cap.y - 0.4f * u), 0.55f * u, StrokeCap.Round)
        drawLine(n(LAMINA, o), Offset(pivote.x, pivote.y - 0.15f * u), Offset(cap.x + 1.2f * u, cap.y - 0.55f * u), 0.25f * u, StrokeCap.Round)
        drawRoundRect(n(Color(0xFF2A2428), o), Offset(cap.x - 0.6f * u, cap.y - 0.9f * u), Size(2f * u, 1.2f * u), CornerRadius(0.3f * u))
        val pk = Offset(cub.right - cub.width * 0.07f, cub.bottom - cub.height * 0.3f)
        drawCircle(n(Color(0xFF3A3532), o), 1f * u, pk)
        // La caja de dos colores, con su ribete, los broches y la rejilla del frente.
        sombraContacto(Offset(caja.center.x + 1.4f * u, caja.bottom), caja.width * 1.04f, 1.4f * u, if (o) 0.5f else 0.32f)
        drawRoundRect(n(CREMA, o), caja.topLeft, caja.size, CornerRadius(1.2f * u))
        drawRect(n(FORRO, o), Offset(caja.left, caja.top + cajaAlto * 0.5f), Size(caja.width, cajaAlto * 0.5f - 1.2f * u))
        drawRoundRect(n(FORRO, o), Offset(caja.left, caja.bottom - 2.4f * u), Size(caja.width, 2.4f * u), CornerRadius(1.2f * u))
        drawLine(n(LAMINA, o), Offset(caja.left, caja.top + cajaAlto * 0.5f), Offset(caja.right, caja.top + cajaAlto * 0.5f), 0.3f * u)
        drawRoundRect(sombraMueble(if (o) 0.4f else 0.3f), caja.topLeft, caja.size, CornerRadius(1.2f * u), style = Stroke(0.3f * u))
        drawRect(luzMueble(if (o) 0.05f else 0.4f), Offset(caja.left + 0.6f * u, caja.top + 0.2f * u), Size(caja.width - 1.2f * u, 0.3f * u))
        drawRect(sombraMueble(if (o) 0.3f else 0.18f), Offset(caja.right - caja.width * 0.08f, caja.top), Size(caja.width * 0.08f, caja.height))
        for (i in 0 until 9) drawCircle(n(FORRO_OSC, o), 0.4f * u, Offset(caja.left + caja.width * 0.1f + i * 1.4f * u, caja.top + cajaAlto * 0.25f))
        listOf(caja.left + caja.width * 0.18f, caja.right - caja.width * 0.18f).forEach { x ->
            drawRoundRect(n(LATON, o), Offset(x - 1f * u, caja.top + cajaAlto * 0.42f), Size(2f * u, 1.6f * u), CornerRadius(0.3f * u))
            drawRect(n(LATON_LUZ, o), Offset(x - 0.8f * u, caja.top + cajaAlto * 0.44f), Size(0.6f * u, 1.2f * u))
        }
        val piloto = Offset(caja.right - caja.width * 0.12f, caja.top + cajaAlto * 0.25f)
        if (o) { halo(piloto, 3.4f * u, Color(0xFFFF6A3A), 0.7f, 8); drawCircle(Color(0xFFFF6A3A), 0.5f * u, piloto) } else drawCircle(Color(0xFF8A3A2A), 0.5f * u, piloto)
    }

    /** La pila de sencillos en sus fundas de papel con el agujero, y el adaptador amarillo encima. */
    private fun DrawScope.sencillos(base: Offset, ancho: Float, o: Boolean, u: Float) {
        val cols = listOf(PAPEL, Color(0xFFE88A3A), Color(0xFF8BC9A6), PAPEL, Color(0xFFD06A9A), PAPEL_CLARO)
        sombraContacto(Offset(base.x + 0.8f * u, base.y), ancho * 1.1f, 1.2f * u, if (o) 0.5f else 0.3f)
        cols.forEachIndexed { i, col ->
            val y = base.y - (i + 1) * 0.95f * u
            val dx = (azar(17, i) - 0.5f) * 1.4f * u
            val f = Rect(base.x - ancho / 2 + dx, y, base.x + ancho / 2 + dx, y + 0.95f * u)
            rect(f, n(col, o))
            drawRect(luzMueble(if (o) 0.04f else 0.35f), f.topLeft, Size(f.width, 0.2f * u))
            drawRect(sombraMueble(if (o) 0.3f else 0.2f), Offset(f.left, f.bottom - 0.2f * u), Size(f.width, 0.2f * u))
        }
        val tope = base.y - cols.size * 0.95f * u
        val ad = Offset(base.x + 0.4f * u, tope - 0.5f * u)
        drawOval(n(AMARILLO, o), Offset(ad.x - ancho * 0.18f, ad.y - 0.5f * u), Size(ancho * 0.36f, 1f * u))
        drawOval(n(Color(0xFFB8901A), o), Offset(ad.x - ancho * 0.06f, ad.y - 0.25f * u), Size(ancho * 0.12f, 0.5f * u))
    }

    /**
     * La radio de válvulas de mesa, en [r]: la caja de madera con las esquinas de arriba redondas y su veta, la tela del
     * altavoz con sus listones, el cuadrante de la FM con su aguja y las dos perillas de baquelita. De noche, el cuadrante
     * encendido apenas, con su luz de foco.
     */
    private fun DrawScope.radio(r: Rect, o: Boolean, u: Float) {
        sombraEnPared(r, 1.4f * u, if (o) 0.4f else 0.26f, 3f * u)
        val caja = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(3.4f * u), CornerRadius(3.4f * u), CornerRadius(0.6f * u), CornerRadius(0.6f * u))) }
        drawPath(caja, n(RADIO, o))
        clipPath(caja) {
            pino(r, o, u, 77, RADIO, nudos = false)
            drawRect(luzMueble(if (o) 0.04f else 0.3f), r.topLeft, Size(r.width * 0.08f, r.height))
            drawRect(sombraMueble(if (o) 0.35f else 0.28f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
            drawRect(luzMueble(if (o) 0.04f else 0.35f), r.topLeft, Size(r.width, 0.6f * u))
        }
        // La tela del altavoz con sus listones.
        val tela = Rect(r.left + 1.6f * u, r.top + 1.8f * u, r.left + r.width * 0.56f, r.bottom - 1.6f * u)
        drawRoundRect(n(Color(0xFFD6C6A0), o), tela.topLeft, tela.size, CornerRadius(1f * u))
        clipRect(tela.left, tela.top, tela.right, tela.bottom) {
            for (k in 0 until 40) drawLine(n(Color(0xFFB8A57A), o).copy(alpha = 0.5f), Offset(tela.left + k * 0.5f * u, tela.top), Offset(tela.left + k * 0.5f * u - 2f * u, tela.bottom), 0.12f * u)
        }
        var x = tela.left + 1.4f * u
        while (x < tela.right - 0.6f * u) {
            drawLine(n(RADIO_OSC, o), Offset(x, tela.top), Offset(x, tela.bottom), 0.7f * u)
            drawLine(n(Color(0xFFB07A4A), o), Offset(x - 0.25f * u, tela.top), Offset(x - 0.25f * u, tela.bottom), 0.15f * u)
            x += 2f * u
        }
        drawRoundRect(n(RADIO_OSC, o), tela.topLeft, tela.size, CornerRadius(1f * u), style = Stroke(0.35f * u))
        // El cuadrante de la FM, con sus rayitas y la aguja en el 106.9.
        val dial = Rect(r.left + r.width * 0.62f, r.top + 2f * u, r.right - 1.6f * u, r.top + r.height * 0.42f)
        if (o) halo(dial.center, dial.width * 0.9f, FAROL, 0.5f, 10)
        drawRoundRect(if (o) Color(0xFFE8B868) else Color(0xFFEFE2C0), dial.topLeft, dial.size, CornerRadius(0.6f * u))
        for (i in 0..8) {
            val xi = dial.left + 0.8f * u + i * (dial.width - 1.6f * u) / 8f
            drawLine(n(Color(0xFF5A4630), false).copy(alpha = if (o) 0.7f else 1f), Offset(xi, dial.bottom - (if (i % 2 == 0) 1.6f else 1f) * u), Offset(xi, dial.bottom - 0.4f * u), 0.15f * u)
        }
        drawLine(Color(0xFFC0392B), Offset(dial.left + dial.width * 0.82f, dial.top + 0.4f * u), Offset(dial.left + dial.width * 0.82f, dial.bottom - 0.4f * u), 0.3f * u)
        drawRoundRect(n(RADIO_OSC, o), dial.topLeft, dial.size, CornerRadius(0.6f * u), style = Stroke(0.3f * u))
        // Las dos perillas.
        listOf(dial.left + dial.width * 0.22f, dial.right - dial.width * 0.22f).forEach { kx ->
            val c = Offset(kx, r.bottom - r.height * 0.27f)
            drawCircle(sombraMueble(0.35f), 1.6f * u, Offset(c.x + 0.4f * u, c.y + 0.5f * u))
            drawCircle(n(Color(0xFF3A2414), o), 1.6f * u, c)
            drawCircle(n(Color(0xFF6A4630), o), 1.1f * u, Offset(c.x - 0.2f * u, c.y - 0.2f * u))
            drawCircle(luzMueble(if (o) 0.06f else 0.45f), 0.35f * u, Offset(c.x - 0.6f * u, c.y - 0.6f * u))
        }
        // Las patitas.
        listOf(r.left + r.width * 0.12f, r.right - r.width * 0.12f).forEach { px -> drawRect(n(RADIO_OSC, o), Offset(px - 1f * u, r.bottom - 0.2f * u), Size(2f * u, 0.6f * u)) }
    }

    /** La hiedra en su maceta de barro: las hojas de corazón en sus tallos y dos guías que cuelgan por el costado hasta [hasta]. */
    private fun DrawScope.hiedra(base: Offset, alto: Float, hasta: Offset, o: Boolean, u: Float, semilla: Int) {
        val an = alto * 0.9f
        val boca = base.y - alto * 0.62f
        fun hoja(c: Offset, s: Float, g: Float, k: Int) {
            rotate(g, c) {
                val p = Path().apply {
                    moveTo(c.x, c.y + s)
                    cubicTo(c.x - s * 1.2f, c.y + s * 0.2f, c.x - s * 0.9f, c.y - s * 0.9f, c.x, c.y - s * 0.45f)
                    cubicTo(c.x + s * 0.9f, c.y - s * 0.9f, c.x + s * 1.2f, c.y + s * 0.2f, c.x, c.y + s)
                    close()
                }
                drawPath(p, n(if (k % 3 == 0) HOJA_OSC else if (k % 3 == 1) HOJA else mez(HOJA, HOJA_LUZ, 0.4f), o))
                drawLine(n(HOJA_LUZ, o).copy(alpha = 0.6f), Offset(c.x, c.y - s * 0.35f), Offset(c.x, c.y + s * 0.8f), 0.15f * u)
            }
        }
        // Las guías que cuelgan, con sus hojas.
        listOf(0f, 1f).forEach { j ->
            val desde = Offset(base.x + an * (0.25f + 0.15f * j), boca)
            val fin = Offset(hasta.x - j * 1.6f * u, hasta.y - j * alto * 0.5f)
            val ctrl = Offset(fin.x + 2f * u, desde.y + (fin.y - desde.y) * 0.2f)
            drawPath(Path().apply { moveTo(desde.x, desde.y); quadraticTo(ctrl.x, ctrl.y, fin.x, fin.y) }, n(HOJA_OSC, o), style = Stroke(0.25f * u))
            for (k in 1..7) {
                val q = k / 7f
                val p = Offset((1 - q) * (1 - q) * desde.x + 2 * (1 - q) * q * ctrl.x + q * q * fin.x, (1 - q) * (1 - q) * desde.y + 2 * (1 - q) * q * ctrl.y + q * q * fin.y)
                hoja(Offset(p.x + (if (k % 2 == 0) 0.9f else -0.9f) * u, p.y), 1.3f * u * (1.1f - 0.3f * q), if (k % 2 == 0) 30f else -30f, k + semilla)
            }
        }
        // La maceta.
        sombraContacto(Offset(base.x + 0.8f * u, base.y), an * 1.1f, 1.2f * u, if (o) 0.5f else 0.3f)
        val m = Path().apply { moveTo(base.x - an * 0.5f, boca); lineTo(base.x + an * 0.5f, boca); lineTo(base.x + an * 0.36f, base.y); lineTo(base.x - an * 0.36f, base.y); close() }
        drawPath(m, n(BARRO, o))
        drawPath(Path().apply { moveTo(base.x + an * 0.18f, boca); lineTo(base.x + an * 0.5f, boca); lineTo(base.x + an * 0.36f, base.y); lineTo(base.x + an * 0.1f, base.y); close() }, sombraMueble(if (o) 0.3f else 0.22f))
        drawRect(n(BARRO_OSC, o), Offset(base.x - an * 0.56f, boca - alto * 0.12f), Size(an * 1.12f, alto * 0.14f))
        drawRect(luzMueble(if (o) 0.04f else 0.35f), Offset(base.x - an * 0.56f, boca - alto * 0.12f), Size(an * 1.12f, 0.3f * u))
        // Las hojas de arriba, en mata.
        for (k in 0 until 9) {
            val a = PI.toFloat() * (1.05f + 0.9f * k / 8f)
            val d = an * (0.35f + 0.25f * azar(semilla, k + 800))
            hoja(Offset(base.x + d * cos(a) * 1.1f, boca - alto * 0.18f + d * sin(a) * 0.9f), 1.4f * u, (a * 57.3f) + 90f, k)
        }
    }

    // ---------------------------------------------------------------- Íconos

    /** El naranja del puente, su sombra y el agua de la bahía. */
    private val PUENTE = Color(0xFFC8442A)
    private val PUENTE_OSC = Color(0xFF7E2616)
    private val BAHIA = Color(0xFF3F7F94)

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val o = oscuro
        val s = size.height / 100f
        val c = center
        when (id) {
            "unesco" -> goldenGate(c, s, o)
            "rascar" -> rascarPua(c, s, o)
            "lugares" -> planoBarrio(c, s, o, e)
        }
    }

    /**
     * El puente del Golden Gate con la niebla que entra: la torre de dos patas con sus portales escalonados, la otra torre a lo
     * lejos casi borrada, los cables con sus péndolas, el tablero, la loma de enfrente y el agua. De noche, la luz roja de lo
     * alto de las torres, las luces del tablero y la niebla oscura.
     */
    private fun DrawScope.goldenGate(c: Offset, s: Float, o: Boolean) {
        val bw = minOf(size.width * 0.94f, 200f * s)
        val b = Rect(c.x - bw / 2, c.y - 44f * s, c.x + bw / 2, c.y + 40f * s)
        val agua = b.bottom - 12f * s
        val tablero = agua - 16f * s
        val naranja = if (o) mez(PUENTE, SALA, 0.45f) else PUENTE
        val naranjaOsc = if (o) mez(PUENTE_OSC, SALA, 0.45f) else PUENTE_OSC
        val niebla = if (o) NIEBLA_NOCHE else Color(0xFFF4F4F2)
        // El sol de la tarde detrás, y la loma de enfrente.
        if (!o) drawCircle(TrazosPsicodelia.CIELO_TARDE.copy(alpha = 0.7f), 15f * s, Offset(b.left + bw * 0.74f, b.top + 22f * s))
        else drawCircle(Color(0xFFEDE6F5).copy(alpha = 0.8f), 5f * s, Offset(b.left + bw * 0.8f, b.top + 12f * s))
        drawPath(Path().apply { moveTo(b.left, agua); quadraticTo(b.left + bw * 0.2f, agua - 30f * s, b.left + bw * 0.45f, agua - 12f * s); lineTo(b.left + bw * 0.45f, agua); close() },
            if (o) Color(0xFF2A2238) else Color(0xFF8E9A6E))
        // El agua de la bahía con sus rayitas de luz.
        val ag = Rect(b.left, agua, b.right, b.bottom)
        rect(ag, if (o) Color(0xFF1E1A34) else BAHIA)
        for (k in 0 until 7) drawLine((if (o) FAROL else Color.White).copy(alpha = if (o) 0.35f else 0.45f), Offset(b.left + bw * (0.08f + 0.13f * k), agua + (3f + 2.5f * (k % 3)) * s), Offset(b.left + bw * (0.13f + 0.13f * k), agua + (3f + 2.5f * (k % 3)) * s), 0.8f * s)
        // La torre de lejos, menor y en la niebla.
        torre(Offset(b.left + bw * 0.16f, agua), 44f * s, s, mez(naranja, niebla, 0.55f), mez(naranjaOsc, niebla, 0.55f))
        // Los cables principales y las péndolas.
        val ta = Offset(b.left + bw * 0.16f, agua - 44f * s * 0.93f)
        val tb = Offset(b.left + bw * 0.64f, agua - 74f * s * 0.93f)
        val cable = Path().apply { moveTo(ta.x, ta.y); quadraticTo((ta.x + tb.x) / 2f, tablero + 18f * s, tb.x, tb.y); quadraticTo(tb.x + bw * 0.2f, tablero - 4f * s, b.right + 4f * s, tablero - 2f * s) }
        for (k in 0 until 22) {
            val x = ta.x + (b.right - ta.x) * k / 21f
            val y = if (x < tb.x) { val q = (x - ta.x) / (tb.x - ta.x); (1 - q) * (1 - q) * ta.y + 2 * (1 - q) * q * (tablero + 18f * s) + q * q * tb.y }
                else { val q = ((x - tb.x) / (b.right + 4f * s - tb.x)).coerceIn(0f, 1f); (1 - q) * (1 - q) * tb.y + 2 * (1 - q) * q * (tablero - 4f * s) + q * q * (tablero - 2f * s) }
            if (y < tablero) drawLine(naranja.copy(alpha = 0.8f), Offset(x, y), Offset(x, tablero), 0.5f * s)
        }
        drawPath(cable, naranja, style = Stroke(1.6f * s))
        // El tablero con su armadura.
        val tab = Rect(b.left, tablero, b.right, tablero + 3.4f * s)
        rect(tab, naranja)
        rect(Rect(tab.left, tab.bottom - 1.2f * s, tab.right, tab.bottom), naranjaOsc)
        var x = tab.left
        while (x < tab.right) { drawLine(naranjaOsc, Offset(x, tab.top + 0.6f * s), Offset(x + 2f * s, tab.bottom - 0.6f * s), 0.4f * s); x += 2f * s }
        if (o) for (k in 0 until 14) drawCircle(FAROL, 0.6f * s, Offset(tab.left + bw * (0.03f + 0.07f * k), tab.top))
        // La torre de cerca.
        torre(Offset(tb.x, agua), 74f * s, s, naranja, naranjaOsc)
        if (o) listOf(ta, tb).forEach { t -> halo(Offset(t.x, t.y - 2f * s), 7f * s, Color(0xFFFF4A3A), 0.9f, 8); drawCircle(Color(0xFFFF6A5A), 0.9f * s, Offset(t.x, t.y - 2f * s)) }
        // La niebla que entra por la boca de la bahía, tapando el pie de la torre de lejos.
        clipRect(b.left - 2f * s, b.top, b.right, b.bottom) {
            listOf(Offset(b.left + bw * 0.05f, agua - 8f * s) to 15f, Offset(b.left + bw * 0.2f, agua - 4f * s) to 13f, Offset(b.left + bw * 0.34f, agua - 9f * s) to 11f, Offset(b.left + bw * 0.45f, agua - 3f * s) to 9f).forEachIndexed { k, (q, r) ->
                drawPath(TrazosPsicodelia.gota(Offset(q.x + 1f * s, q.y + 1.4f * s), r * s, k * 0.3f, 0.14f, 4), sombraMueble(if (o) 0.25f else 0.08f))
                drawPath(TrazosPsicodelia.gota(q, r * s, k * 0.3f, 0.14f, 4), niebla.copy(alpha = if (o) 0.85f else 0.92f))
            }
        }
    }

    /** Una torre del puente, de pie en [pie] y de [alto]: las dos patas, los portales escalonados entre ellas y el remate. */
    private fun DrawScope.torre(pie: Offset, alto: Float, s: Float, color: Color, sombra: Color) {
        val sep = alto * 0.13f; val pata = alto * 0.06f
        listOf(-1f, 1f).forEach { lado ->
            val x = pie.x + lado * sep
            val p = Path().apply { moveTo(x - pata * 0.6f, pie.y - alto); lineTo(x + pata * 0.6f, pie.y - alto); lineTo(x + pata * 0.75f, pie.y); lineTo(x - pata * 0.75f, pie.y); close() }
            drawPath(p, color)
            drawRect(sombra, Offset(x + pata * 0.1f, pie.y - alto), Size(pata * 0.6f, alto))
            // Los escalones de la pata, cada tramo un poco más angosto.
            for (k in 1..3) drawLine(sombra, Offset(x - pata * 0.7f, pie.y - alto * k * 0.25f), Offset(x + pata * 0.7f, pie.y - alto * k * 0.25f), 0.6f * s)
        }
        // Los portales: travesaños con su arco escalonado debajo.
        listOf(0.97f, 0.72f, 0.48f).forEach { f ->
            val y = pie.y - alto * f
            val r = Rect(pie.x - sep, y, pie.x + sep, y + alto * 0.05f)
            rect(r, color)
            drawRect(sombra, Offset(r.left, r.bottom - alto * 0.015f), Size(r.width, alto * 0.015f))
            for (k in 1..3) drawRect(color, Offset(pie.x - sep * (1f - 0.18f * k), r.bottom), Size(sep * 0.18f * 0.8f, alto * 0.012f * k))
            for (k in 1..3) drawRect(color, Offset(pie.x + sep * (1f - 0.18f * k) - sep * 0.144f, r.bottom), Size(sep * 0.144f, alto * 0.012f * k))
        }
        drawRect(color, Offset(pie.x - sep - pata * 0.7f, pie.y - alto - alto * 0.03f), Size(2f * sep + pata * 1.4f, alto * 0.03f))
    }

    /**
     * La hoja entintada de negro que una púa de guitarra raspa: debajo aparece la tinta de arcoíris del tintero partido, en
     * la forma de la costa; el filo del raspado con sus virutas, las rayas de la púa y la púa de carey con su sombra.
     */
    private fun DrawScope.rascarPua(c: Offset, s: Float, o: Boolean) {
        val r = Rect(c.x - 40f * s, c.y - 34f * s, c.x + 34f * s, c.y + 34f * s)
        rotate(-5f, c) {
            drawRect(sombraMueble(if (o) 0.5f else 0.3f), Offset(r.left + 2f * s, r.top + 3f * s), r.size)
            drawRect(TrazosPsicodelia.arcoiris(Offset(r.left, r.top), Offset(r.right, r.bottom), 1f, o), r.topLeft, r.size)
            // La parte raspada: una costa con su bahía.
            val raspado = Path().apply {
                moveTo(r.left + 10f * s, r.top + 14f * s)
                cubicTo(r.left + 26f * s, r.top + 4f * s, r.left + 44f * s, r.top + 18f * s, r.left + 52f * s, r.top + 12f * s)
                cubicTo(r.left + 66f * s, r.top + 22f * s, r.left + 60f * s, r.top + 40f * s, r.left + 54f * s, r.top + 50f * s)
                cubicTo(r.left + 44f * s, r.top + 60f * s, r.left + 34f * s, r.top + 44f * s, r.left + 24f * s, r.top + 54f * s)
                cubicTo(r.left + 12f * s, r.top + 60f * s, r.left + 4f * s, r.top + 34f * s, r.left + 10f * s, r.top + 14f * s)
                close()
            }
            val tinta = Path().apply { addRect(r); addPath(raspado); fillType = PathFillType.EvenOdd }
            val negro = if (o) Color(0xFF0C0712) else Color(0xFF1C1524)
            drawPath(tinta, negro)
            // Las rayas de la púa dentro de lo raspado y las virutas del filo.
            clipPath(raspado) { for (k in 0 until 8) drawLine(negro.copy(alpha = 0.18f), Offset(r.left + (6f + 8f * k) * s, r.top + 6f * s), Offset(r.left + (0f + 8f * k) * s, r.bottom - 6f * s), 0.6f * s) }
            drawPath(raspado, negro.copy(alpha = 0.6f), style = Stroke(0.9f * s))
            // En lo negro, una flor de tinta fluorescente del cartel de luz negra.
            with(TrazosPsicodelia) { margarita(Offset(r.right - 8f * s, r.top + 8f * s), 5f * s, (if (o) LIMON else mez(LIMON, negro, 0.25f)), negro, 6) }
            if (o) halo(Offset(r.right - 8f * s, r.top + 8f * s), 9f * s, LIMON, 0.4f, 8)
            drawRect(luzMueble(if (o) 0.04f else 0.12f), r.topLeft, Size(r.width, 1.2f * s))
        }
        // La púa de carey, con su sombra.
        val pc = Offset(c.x + 28f * s, c.y + 16f * s)
        rotate(-35f, pc) { scale(1.35f, 1.35f, pc) {
            val pua = Path().apply {
                moveTo(pc.x, pc.y + 13f * s)
                cubicTo(pc.x - 4f * s, pc.y + 6f * s, pc.x - 11f * s, pc.y - 2f * s, pc.x - 10f * s, pc.y - 7f * s)
                cubicTo(pc.x - 8f * s, pc.y - 12f * s, pc.x + 8f * s, pc.y - 12f * s, pc.x + 10f * s, pc.y - 7f * s)
                cubicTo(pc.x + 11f * s, pc.y - 2f * s, pc.x + 4f * s, pc.y + 6f * s, pc.x, pc.y + 13f * s)
                close()
            }
            translate(2f * s, 3f * s) { drawPath(pua, sombraMueble(if (o) 0.5f else 0.35f)) }
            drawPath(pua, n(Color(0xFF8A4A1E), o))
            clipPath(pua) {
                for (k in 0 until 9) drawPath(TrazosPsicodelia.gota(Offset(pc.x + (azar(9, k) - 0.5f) * 18f * s, pc.y + (azar(10, k) - 0.5f) * 20f * s), (1.4f + 1.8f * azar(11, k)) * s, k * 0.2f, 0.3f, 3),
                    n(if (k % 3 == 0) Color(0xFFC9803A) else Color(0xFF5A2A10), o).copy(alpha = 0.55f))
                drawArc(Color.White.copy(alpha = if (o) 0.15f else 0.45f), 200f, 70f, false, Offset(pc.x - 8f * s, pc.y - 10f * s), Size(16f * s, 16f * s), style = Stroke(1f * s, cap = StrokeCap.Round))
            }
            drawPath(pua, n(Color(0xFF3A1A0A), o), style = Stroke(0.5f * s))
        } }
    }

    /**
     * El plano del barrio doblado en tres: las calles en cuadrícula, el parque Golden Gate y el Panhandle en verde, el mar a
     * la izquierda, y tres botones de solapa clavados (la flor, la paz y la margarita) unidos por un camino que ondula.
     */
    private fun DrawScope.planoBarrio(c: Offset, s: Float, o: Boolean, e: EstadoIcono) {
        val pw = minOf(size.width * 0.86f, 150f * s)
        val r = Rect(c.x - pw / 2, c.y - 36f * s, c.x + pw / 2, c.y + 36f * s)
        drawRect(sombraMueble(if (o) 0.5f else 0.3f), Offset(r.left + 2f * s, r.top + 3f * s), r.size)
        rect(r, if (o) mez(n(PAPEL, o), e.fondo, 0.3f) else PAPEL)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // El mar, a la izquierda.
            rect(Rect(r.left, r.top, r.left + pw * 0.07f, r.bottom), if (o) Color(0xFF2A2650) else Color(0xFF9CC4D2))
            // Las calles.
            val calle = (if (o) Color(0xFF6A5E80) else Color(0xFFC8BCA6))
            var x = r.left + pw * 0.07f
            while (x < r.right) { drawLine(calle, Offset(x, r.top), Offset(x, r.bottom), 0.5f * s); x += 7f * s }
            var y = r.top + 3f * s
            while (y < r.bottom) { drawLine(calle, Offset(r.left + pw * 0.07f, y), Offset(r.right, y), 0.5f * s); y += 8f * s }
            // La calle Haight, más ancha, en diagonal suave.
            drawLine(if (o) Color(0xFF8A7CA8) else Color(0xFFE2C9A0), Offset(r.left + pw * 0.4f, r.top + 46f * s), Offset(r.right, r.top + 44f * s), 1.6f * s)
            // El parque y el Panhandle, y Buena Vista.
            val verde = if (o) Color(0xFF2E4A3A) else Color(0xFF8DBF7A)
            rect(Rect(r.left + pw * 0.07f, r.top + 14f * s, r.left + pw * 0.42f, r.top + 34f * s), verde)
            rect(Rect(r.left + pw * 0.42f, r.top + 22f * s, r.left + pw * 0.72f, r.top + 28f * s), verde)
            drawPath(TrazosPsicodelia.gota(Offset(r.left + pw * 0.82f, r.top + 54f * s), 6f * s, 0.2f, 0.15f), verde)
            for (k in 0 until 6) drawCircle((if (o) Color(0xFF1E3428) else Color(0xFF5E9A52)), 1.2f * s, Offset(r.left + pw * (0.1f + 0.05f * k), r.top + (18f + 9f * (k % 2)) * s))
            // Los dobleces.
            listOf(1f / 3f, 2f / 3f).forEach { f ->
                val xf = r.left + pw * f
                drawRect(sombraMueble(if (o) 0.25f else 0.12f), Offset(xf, r.top), Size(2.4f * s, r.height))
                drawRect(luzMueble(if (o) 0.05f else 0.4f), Offset(xf - 1.6f * s, r.top), Size(1.6f * s, r.height))
            }
        }
        // El camino que ondula entre los tres botones.
        val p1 = Offset(r.left + pw * 0.22f, r.top + 24f * s)
        val p2 = Offset(r.left + pw * 0.6f, r.top + 25f * s)
        val p3 = Offset(r.left + pw * 0.56f, r.top + 54f * s)
        val camino = Path().apply { moveTo(p1.x, p1.y); cubicTo(p1.x + 10f * s, p1.y - 10f * s, p2.x - 14f * s, p2.y + 10f * s, p2.x, p2.y); cubicTo(p2.x + 14f * s, p2.y + 8f * s, p3.x + 14f * s, p3.y - 12f * s, p3.x, p3.y) }
        drawPath(camino, e.c1, style = Stroke(1.2f * s, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3f * s, 2.4f * s))))
        boton(p1, 7f * s, e.c2, 0, o, s)
        boton(p2, 7f * s, if (o) FMAGENTA else MAGENTA, 1, o, s)
        boton(p3, 8f * s, e.c1, 2, o, s)
    }

    /** Un botón de solapa clavado en el plano: el disco con su orla, su dibujo ([motivo]: la flor, la paz o la margarita) y el brillo de la cúpula. */
    private fun DrawScope.boton(c: Offset, r: Float, color: Color, motivo: Int, o: Boolean, s: Float) {
        drawCircle(sombraMueble(if (o) 0.5f else 0.35f), r, Offset(c.x + 1.4f * s, c.y + 2f * s))
        drawCircle(mez(color, Color.Black, 0.3f), r, c)
        drawCircle(color, r * 0.88f, c)
        val claro = if (o) Color(0xFF1A1126) else Color(0xFFFFFBF2)
        with(TrazosPsicodelia) {
            when (motivo) {
                0 -> flor(c, r * 0.62f, claro, TrazosPsicodelia.complementario(color).copy(alpha = 0.7f), 6, 0.4f)
                1 -> paz(c, r * 0.5f, claro, r * 0.12f)
                else -> margarita(c, r * 0.66f, claro, color, 8)
            }
        }
        drawArc(Color.White.copy(alpha = if (o) 0.3f else 0.55f), 200f, 60f, false, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(r * 0.12f, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.NOTAS))
        val marco = Rect(w * 0.028f, h * 0.075f, w * 0.972f, h * 0.87f)
        val repisa = Rect(w * 0.012f, marco.bottom + 0.4f * u, w * 0.988f, marco.bottom + 2.6f * u)

        // La pared, con el violeta del tubo de noche.
        pared(Rect(0f, 0f, w, h), o, u, e.semilla + 12)
        if (o) halo(Offset(w * 0.5f, -h * 0.15f), w * 0.7f, FVIOLETA, 0.7f, 22)

        // El marco de pino pintado a mano.
        sombraEnPared(marco, 2.2f * u, if (o) 0.45f else 0.28f, 0.6f * u)
        val travesano = Rect(marco.left, marco.top, marco.right, ni.top - 1.2f * u)
        tabla(travesano, o, u, e.semilla + 1)
        tabla(Rect(marco.left, ni.bottom + 1.2f * u, marco.right, marco.bottom), o, u, e.semilla + 2)
        tabla(Rect(marco.left, travesano.bottom, ni.left - 1.2f * u, ni.bottom + 1.2f * u), o, u, e.semilla + 3, vertical = true)
        tabla(Rect(ni.right + 1.2f * u, travesano.bottom, marco.right, ni.bottom + 1.2f * u), o, u, e.semilla + 4, vertical = true)
        // Las juntas a inglete de las esquinas.
        listOf(Offset(marco.left, marco.top) to Offset(ni.left - 1.2f * u, travesano.bottom), Offset(marco.right, marco.top) to Offset(ni.right + 1.2f * u, travesano.bottom),
            Offset(marco.left, marco.bottom) to Offset(ni.left - 1.2f * u, ni.bottom + 1.2f * u), Offset(marco.right, marco.bottom) to Offset(ni.right + 1.2f * u, ni.bottom + 1.2f * u)).forEach { (a, b) ->
            drawLine(sombraMueble(if (o) 0.4f else 0.3f), a, b, 0.25f * u)
        }
        // La onda de arcoíris pintada en el travesaño de arriba (de noche, en las pinturas que brillan) y las margaritas de las esquinas.
        val cols = listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL)
        val bandaAlto = travesano.height * 0.62f
        clipRect(travesano.left + 1f * u, travesano.top, travesano.right - 1f * u, travesano.bottom) {
            if (o) haloRect(Rect(travesano.left, travesano.center.y - bandaAlto / 2, travesano.right, travesano.center.y + bandaAlto / 2), 2f * u, FMAGENTA, 0.35f)
            cols.forEachIndexed { k, col ->
                val y = travesano.center.y - bandaAlto / 2 + bandaAlto * (k + 0.5f) / cols.size
                drawPath(TrazosPsicodelia.onda(travesano.left, travesano.right, y, travesano.height * 0.09f, w * 0.18f), fl(col, o).let { if (o) mez(it, SALA, 0.3f) else mez(it, PINO, 0.22f) },
                    style = Stroke(bandaAlto / cols.size * 1.05f))
            }
            // La pintura a pincel deja ver la veta: rayas claras y oscuras a lo largo.
            pinceladas(travesano, n(PINO_LUZ, o), e.semilla + 77, 18, u * 0.4f, if (o) 0.05f else 0.16f)
            pinceladas(travesano, n(PINO_OSC, o), e.semilla + 78, 10, u * 0.4f, if (o) 0.06f else 0.1f)
        }
        drawRect(luzMueble(if (o) 0.05f else 0.45f), travesano.topLeft, Size(travesano.width, 0.4f * u))
        listOf(Offset(marco.left + (ni.left - marco.left) / 2f, ni.top + 4f * u), Offset(marco.right - (marco.right - ni.right) / 2f, ni.top + 4f * u),
            Offset(marco.left + (ni.left - marco.left) / 2f, ni.bottom - 4f * u), Offset(marco.right - (marco.right - ni.right) / 2f, ni.bottom - 4f * u)).forEachIndexed { k, q ->
            with(TrazosPsicodelia) { margarita(q, (ni.left - marco.left) * 0.32f, fl(if (k % 2 == 0) AMARILLO else MAGENTA, o), fl(if (k % 2 == 0) NARANJA else AMARILLO, o), 6, k * 0.4f) }
        }
        for (k in 0 until 4) {
            val y = ni.top + ni.height * (0.3f + 0.13f * k)
            listOf(marco.left + (ni.left - marco.left) / 2f, marco.right - (marco.right - ni.right) / 2f).forEach { x -> drawCircle(fl(listOf(VERDE, AZUL, VIOLETA, NARANJA)[k], o), 0.45f * u, Offset(x, y)) }
        }

        // El corcho: el nicho, con su grano y los agujeros de chinchetas viejas.
        val bis = ni.inflate(1.2f * u)
        rect(bis, n(PINO_OSC, o))
        drawRect(luzMueble(if (o) 0.04f else 0.3f), Offset(bis.left, bis.bottom - 0.4f * u), Size(bis.width, 0.4f * u))
        rect(ni, nicho(e, o, CORCHO))
        val gr = (ni.width * ni.height / (2.6f * u * u)).toInt().coerceIn(40, 1400)
        for (k in 0 until gr) {
            val q = Offset(ni.left + ni.width * azar(e.semilla + 5, k), ni.top + ni.height * azar(e.semilla + 6, k))
            drawCircle((if (azar(e.semilla + 7, k) < 0.5f) n(Color(0xFF8A6238), o) else n(Color(0xFFE8C894), o)).copy(alpha = if (o) 0.12f else 0.16f), (0.12f + 0.2f * azar(e.semilla + 8, k)) * u, q)
        }
        for (k in 0 until 10) drawCircle(n(Color(0xFF5A3E22), o).copy(alpha = 0.4f), 0.22f * u, Offset(ni.left + ni.width * azar(e.semilla, k + 900), ni.top + ni.height * azar(e.semilla, k + 910)))
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 1.2f * u), sombraMueble(if (o) 0.3f else 0.16f))
        rect(Rect(ni.left, ni.top, ni.left + 1f * u, ni.bottom), sombraMueble(if (o) 0.25f else 0.12f))

        // La flor de madera recortada y pintada, clavada en lo alto del marco.
        val fc = Offset(w * 0.5f, marco.top + 0.6f * u)
        val fr = (ni.top - marco.top) * 0.62f
        drawPath(TrazosPsicodelia.petalos(Offset(fc.x + 1f * u, fc.y + 1.4f * u), fr, 8, 0.2f, 0.3f, 0.1f), sombraMueble(if (o) 0.45f else 0.3f))
        if (o) halo(fc, fr * 1.8f, LIMON, 0.45f, 10)
        drawPath(TrazosPsicodelia.petalos(Offset(fc.x - 0.4f * u, fc.y + 0.4f * u), fr, 8, 0.2f, 0.3f, 0.1f), n(PINO_OSC, o))
        with(TrazosPsicodelia) { flor(fc, fr, fl(MAGENTA, o), fl(VERDE, o), 8, 0.6f, 0.2f) }
        drawCircle(n(Color(0xFF4A4448), o), 0.4f * u, fc)

        // La repisa con sus ménsulas, y encima la lata de chinchetas, la cinta, un pincel y el frasco de tinta.
        drawRect(sombraMueble(if (o) 0.45f else 0.3f), Offset(repisa.left + 0.6f * u, repisa.bottom), Size(repisa.width, 1.6f * u))
        listOf(w * 0.12f, w * 0.88f).forEach { x ->
            val m = Path().apply { moveTo(x - 1.6f * u, repisa.bottom); lineTo(x + 1.6f * u, repisa.bottom); lineTo(x + 0.6f * u, repisa.bottom + 5f * u); lineTo(x - 0.6f * u, repisa.bottom + 5f * u); close() }
            drawPath(m, n(PINO, o))
            drawPath(Path().apply { moveTo(x + 0.4f * u, repisa.bottom); lineTo(x + 1.6f * u, repisa.bottom); lineTo(x + 0.6f * u, repisa.bottom + 5f * u); lineTo(x + 0.1f * u, repisa.bottom + 5f * u); close() }, sombraMueble(if (o) 0.4f else 0.3f))
        }
        val sobre = repisa.top
        lataChinchetas(Offset(w * 0.2f, sobre), 3.6f * u, o, u)
        rolloCinta(Offset(w * 0.32f, sobre - 2.2f * u), 2.2f * u, o, u)
        lapiz(Offset(w * 0.47f, sobre - 0.5f * u), w * 0.12f, 0f, o, u, NARANJA)
        plumilla(Offset(w * 0.64f, sobre - 0.6f * u), w * 0.1f, 0f, o, u)
        frasco(Offset(w * 0.95f, sobre), 7f * u, VERDE, o, u)
        tabla(repisa, o, u, e.semilla + 9)

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /** La lata redonda de las chinchetas, abierta, con las cabezas de colores asomando y la tapa apoyada. */
    private fun DrawScope.lataChinchetas(base: Offset, r: Float, o: Boolean, u: Float) {
        sombraContacto(Offset(base.x + 0.6f * u, base.y), r * 2.4f, 1f * u, if (o) 0.5f else 0.3f)
        val lata = Rect(base.x - r, base.y - r * 0.6f, base.x + r, base.y)
        rect(lata, n(LAMINA, o))
        drawRect(luzMueble(if (o) 0.05f else 0.5f), Offset(lata.left + r * 0.2f, lata.top), Size(r * 0.25f, lata.height))
        drawRect(sombraMueble(if (o) 0.3f else 0.25f), Offset(lata.right - r * 0.4f, lata.top), Size(r * 0.4f, lata.height))
        drawOval(n(LAMINA_OSC, o), Offset(lata.left, lata.top - r * 0.22f), Size(lata.width, r * 0.44f))
        listOf(MAGENTA, VERDE, AMARILLO, AZUL, NARANJA).forEachIndexed { k, col ->
            drawCircle(fl(col, o), r * 0.18f, Offset(lata.left + r * (0.4f + 0.3f * k), lata.top - r * 0.04f + (k % 2) * r * 0.08f))
        }
        drawOval(n(LAMINA, o), Offset(lata.right - r * 0.1f, lata.top - r * 0.6f), Size(r * 0.5f, r * 1.2f))
        drawOval(n(MAGENTA, o), Offset(lata.right + 0.02f * r, lata.top - r * 0.5f), Size(r * 0.26f, r))
    }

    /** Los papeles de los volantes, de día: rosa, amarillo, verde, lila, naranja y celeste pálidos. */
    private val VOLANTES = listOf(Color(0xFFF9D9E7), Color(0xFFFBF0BE), Color(0xFFD9F0DC), Color(0xFFE7DEF6), Color(0xFFFCDFC6), Color(0xFFD7E8F7))
    /** El papel oscuro de los volantes de noche. */
    private val VOLANTE_NOCHE = Color(0xFF2A1D3B)

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val i = e.indice
        val semilla = i * 131 + e.tipo.length * 7
        val tinta = listOf(MAGENTA, NARANJA, VERDE, VIOLETA, AZUL, AMARILLO)[i % 6]
        // El volante, cortado a mano: los bordes apenas desparejos.
        val hoja = Rect(r.left + 3f * un, r.top + 4.5f * un, r.right - 3f * un, r.bottom - 3f * un)
        val forma = Path().apply {
            val pasos = 6
            moveTo(hoja.left, hoja.top)
            for (k in 1..pasos) lineTo(hoja.left + hoja.width * k / pasos, hoja.top + (azar(semilla, k) - 0.5f) * 0.9f * un)
            for (k in 1..pasos) lineTo(hoja.right + (azar(semilla, k + 10) - 0.5f) * 0.9f * un, hoja.top + hoja.height * k / pasos)
            for (k in 1..pasos) lineTo(hoja.right - hoja.width * k / pasos, hoja.bottom + (azar(semilla, k + 20) - 0.5f) * 0.9f * un)
            for (k in 1 until pasos) lineTo(hoja.left + (azar(semilla, k + 30) - 0.5f) * 0.9f * un, hoja.bottom - hoja.height * k / pasos)
            close()
        }
        translate(1.4f * un, 2.4f * un) { drawPath(forma, sombraMueble(if (o) 0.45f else 0.3f)) }
        if (o) haloRect(hoja, 3f * un, fl(tinta, true), 0.35f, 6)
        drawPath(forma, if (o) VOLANTE_NOCHE else VOLANTES[i % 6])
        clipPath(forma) {
            // La franja impresa de arriba: la tinta de arcoíris del tintero partido.
            val franja = Rect(hoja.left, hoja.top, hoja.right, hoja.top + 7.5f * un)
            drawRect(TrazosPsicodelia.arcoiris(franja.topLeft, franja.topRight, if (o) 1f else 0.9f, o), franja.topLeft, franja.size)
            drawPath(TrazosPsicodelia.onda(hoja.left, hoja.right, franja.bottom, 0.8f * un, hoja.width * 0.25f, i * 0.15f), if (o) VOLANTE_NOCHE else VOLANTES[i % 6], style = Stroke(1.2f * un))
            // La fibra del papel y la luz de la ventana.
            pinceladas(hoja, if (o) Color.White else Color.White, semilla, 6, un, if (o) 0.02f else 0.18f)
            // Si está fijada: el filete doble que vibra, de un color y su complementario.
            if (e.fijada) {
                val f1 = hoja.deflate(2.2f * un)
                drawRect(fl(tinta, o), f1.topLeft, f1.size, style = Stroke(0.7f * un))
                val f2 = f1.deflate(1f * un)
                drawRect(fl(TrazosPsicodelia.complementario(tinta), o).copy(alpha = 0.8f), f2.topLeft, f2.size, style = Stroke(0.5f * un))
            }
            // La esquina de abajo, que se levanta un poco.
            drawPath(Path().apply { moveTo(hoja.left, hoja.bottom - 9f * un); lineTo(hoja.left + 9f * un, hoja.bottom); lineTo(hoja.left, hoja.bottom); close() }, sombraMueble(if (o) 0.2f else 0.08f))
        }
        // La viñeta del tipo, en la esquina de abajo a la derecha.
        val q = Offset(r.right - 13f * un, r.bottom - 12f * un)
        vineta(e.tipo, q, 5.5f * un, tinta, o, un)
        // Lo que la clava: el botón de solapa si está fijada; si no, una chincheta de color.
        val pin = Offset(r.center.x, r.top + 6.5f * un)
        if (e.fijada) boton(pin, 5f * un, fl(tinta, o), i % 3, o, un * 0.7f)
        else chincheta(pin, 2.4f * un, fl(listOf(VERDE, MAGENTA, AZUL, NARANJA, VIOLETA, AMARILLO)[(i + 2) % 6], o), o)
    }

    /**
     * La viñeta impresa de cada tipo de nota: la idea, un sol de rayos que ondulan; el pensamiento, tres burbujas del show de
     * luces; el propósito, la margarita que vibra; el recordatorio, el disco con su etiqueta.
     */
    private fun DrawScope.vineta(tipo: String, q: Offset, r: Float, tinta: Color, o: Boolean, un: Float) {
        when (tipo) {
            "idea" -> {
                val col = fl(NARANJA, o)
                for (k in 0 until 10) {
                    val a = 2f * PI.toFloat() * k / 10f
                    drawPath(Path().apply { moveTo(q.x + r * 0.55f * cos(a), q.y + r * 0.55f * sin(a)); quadraticTo(q.x + r * 0.8f * cos(a + 0.25f), q.y + r * 0.8f * sin(a + 0.25f), q.x + r * cos(a), q.y + r * sin(a)) },
                        col, style = Stroke(0.6f * un, cap = StrokeCap.Round))
                }
                drawCircle(fl(AMARILLO, o).let { if (o) it else mez(it, NARANJA, 0.2f) }, r * 0.45f, q)
                drawCircle(col, r * 0.45f, q, style = Stroke(0.4f * un))
            }
            "pensamiento" -> with(TrazosPsicodelia) {
                burbuja(Offset(q.x - r * 0.35f, q.y + r * 0.35f), r * 0.55f, fl(VIOLETA, o), 1f)
                burbuja(Offset(q.x + r * 0.4f, q.y - r * 0.05f), r * 0.38f, fl(AZUL, o), 1f)
                burbuja(Offset(q.x + r * 0.05f, q.y - r * 0.62f), r * 0.26f, fl(MAGENTA, o), 1f)
            }
            "proposito" -> with(TrazosPsicodelia) { flor(q, r, fl(MAGENTA, o), fl(VERDE, o), 6, 0.6f) }
            "recordatorio" -> with(TrazosPsicodelia) { disco(q, r, fl(tinta, o)) }
            else -> with(TrazosPsicodelia) { margarita(q, r * 0.8f, fl(tinta, o), if (o) VOLANTE_NOCHE else PAPEL_CLARO, 6) }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF2EAF7) else TINTA
}
